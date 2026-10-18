package io.github.ruddy2003.vestigium.data

import android.content.Context
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.serialization.builtins.ListSerializer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.Normalizer
import java.time.Instant
import java.time.LocalDate
import kotlin.math.floor

/**
 * El mundo sin conexión: fronteras de Natural Earth, ~135 mil localidades de GeoNames y los sitios del Patrimonio Mundial.
 * Todo viene en la app (assets/mundo) y nada sale del teléfono: tus ubicaciones se convierten aquí en ciudades y países.
 */
object Mundo {
    const val CREDITOS = "Fronteras: Natural Earth (dominio público). Ciudades: GeoNames, CC BY 4.0. Patrimonio Mundial: Centro del Patrimonio Mundial de la UNESCO."
    val CONTINENTES = listOf("África", "Asia", "Europa", "Norteamérica", "Sudamérica", "Oceanía", "Antártida", "Océanos")

    // =====================================================================
    //  Países
    // =====================================================================

    /** Contornos en centésimas de grado: lon, lat, lon, lat… Cada parte es un anillo (los huecos también son partes). */
    class Pais(val codigo: String, val nombre: String, val continente: Int, val color: Int, val areaKm2: Int,
               val etiquetaLon: Float, val etiquetaLat: Float, val caja: IntArray, val partes: Array<ShortArray>) {
        val bandera: String get() = bandera(codigo)
        val diminuto: Boolean get() = areaKm2 < 2500
    }

    @Volatile private var paisesCache: List<Pais>? = null
    @Volatile private var porCodigoCache: Map<String, Pais>? = null

    fun paises(ctx: Context): List<Pais> = paisesCache ?: synchronized(this) {
        paisesCache ?: leerPaises(ctx).also { l -> paisesCache = l; porCodigoCache = l.associateBy { it.codigo } }
    }

    fun pais(ctx: Context, codigo: String): Pais? { paises(ctx); return porCodigoCache?.get(codigo) }

    private fun leerPaises(ctx: Context): List<Pais> {
        val b = ByteBuffer.wrap(ctx.assets.open("mundo/paises.bin").use { it.readBytes() }).order(ByteOrder.LITTLE_ENDIAN)
        val magia = ByteArray(4).also { b.get(it) }
        require(String(magia) == "VMP1")
        val n = b.short.toInt() and 0xffff
        return List(n) {
            val cod = ByteArray(2).also { b.get(it) }.let { String(it, Charsets.US_ASCII) }
            val nom = ByteArray(b.get().toInt() and 0xff).also { b.get(it) }.let { String(it, Charsets.UTF_8) }
            val cont = b.get().toInt() and 0xff; val color = b.get().toInt() and 0xff; val area = b.int
            val lx = b.short / 100f; val ly = b.short / 100f
            val caja = IntArray(4) { b.short.toInt() }
            val np = b.short.toInt() and 0xffff
            val partes = Array(np) { val k = b.short.toInt() and 0xffff; ShortArray(k * 2) { b.short } }
            Pais(cod, nom, cont, color, area, lx, ly, caja, partes)
        }
    }

    /** El país que contiene el punto (regla par-impar sobre todas sus partes, así los huecos quedan fuera). */
    fun paisEn(ctx: Context, lat: Double, lon: Double): Pais? {
        val x = lon * 100; val y = lat * 100
        val candidatos = paises(ctx).filter { p -> val c = p.caja; x >= c[0] - 60 && x <= c[2] + 60 && y >= c[1] - 60 && y <= c[3] + 60 }
        candidatos.firstOrNull { p -> val c = p.caja; x >= c[0] && x <= c[2] && y >= c[1] && y <= c[3] && dentro(p.partes, x, y) }?.let { return it }
        // Fuera de todas (una isla que la simplificación borró, la orilla del mar): el más cercano a menos de ~0.6°.
        var cerca: Pais? = null; var dCerca = 60.0
        for (p in candidatos) { val d = distanciaBorde(p.partes, x, y); if (d < dCerca) { dCerca = d; cerca = p } }
        return cerca
    }

    fun dentro(partes: Array<ShortArray>, x: Double, y: Double): Boolean {
        var adentro = false
        for (a in partes) {
            val n = a.size / 2; var j = n - 1
            for (i in 0 until n) {
                val xi = a[2 * i].toDouble(); val yi = a[2 * i + 1].toDouble(); val xj = a[2 * j].toDouble(); val yj = a[2 * j + 1].toDouble()
                if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) adentro = !adentro
                j = i
            }
        }
        return adentro
    }

    private fun distanciaBorde(partes: Array<ShortArray>, x: Double, y: Double): Double {
        var m = Double.MAX_VALUE
        for (a in partes) {
            val n = a.size / 2
            for (i in 0 until n) {
                val j = (i + 1) % n
                val ax = a[2 * i].toDouble(); val ay = a[2 * i + 1].toDouble(); val bx = a[2 * j].toDouble(); val by = a[2 * j + 1].toDouble()
                val dx = bx - ax; val dy = by - ay; val l2 = dx * dx + dy * dy
                val t = if (l2 == 0.0) 0.0 else (((x - ax) * dx + (y - ay) * dy) / l2).coerceIn(0.0, 1.0)
                val px = ax + t * dx - x; val py = ay + t * dy - y
                m = minOf(m, Math.sqrt(px * px + py * py))
            }
        }
        return m
    }

    fun bandera(codigo: String): String =
        if (codigo.length == 2 && codigo[0] in 'A'..'Z' && codigo[1] in 'A'..'Z' && !codigo.startsWith("X")) String(Character.toChars(0x1F1E6 + (codigo[0] - 'A'))) + String(Character.toChars(0x1F1E6 + (codigo[1] - 'A'))) else "🏳"

    // =====================================================================
    //  Ciudades
    // =====================================================================

    /** Todas las localidades en arreglos (una sola vez en memoria). [padre] apunta a la ciudad grande a la que pertenece, o -1. */
    class Ciudades(val lat: IntArray, val lon: IntArray, val pob: IntArray, val pais: CharArray, val padre: IntArray, val principal: BooleanArray,
                   private val inicio: IntArray, private val bytes: ByteArray) {
        val n: Int get() = lat.size
        fun nombre(i: Int): String = String(bytes, inicio[i], inicio[i + 1] - inicio[i], Charsets.UTF_8)
        fun pais(i: Int): String = String(charArrayOf(pais[2 * i], pais[2 * i + 1]))
        fun raiz(i: Int): Int { var k = i; var g = 0; while (padre[k] >= 0 && g++ < 8) k = padre[k]; return k }
        internal val rejilla = HashMap<Int, IntArray>()
        internal val normal: Array<String?> = arrayOfNulls(lat.size)
    }

    @Volatile private var ciudadesCache: Ciudades? = null

    fun ciudades(ctx: Context): Ciudades = ciudadesCache ?: synchronized(this) { ciudadesCache ?: leerCiudades(ctx).also { ciudadesCache = it } }

    private fun leerCiudades(ctx: Context): Ciudades {
        val todo = ctx.assets.open("mundo/ciudades.bin").use { it.readBytes() }
        val b = ByteBuffer.wrap(todo).order(ByteOrder.LITTLE_ENDIAN)
        val magia = ByteArray(4).also { b.get(it) }
        require(String(magia) == "VMC1")
        val n = b.int
        val lat = IntArray(n); val lon = IntArray(n); val pob = IntArray(n); val pais = CharArray(2 * n); val padre = IntArray(n); val principal = BooleanArray(n)
        val inicio = IntArray(n + 1)
        val nombres = java.io.ByteArrayOutputStream(n * 12)
        for (i in 0 until n) {
            lat[i] = b.int; lon[i] = b.int; pob[i] = b.int
            pais[2 * i] = b.get().toInt().toChar(); pais[2 * i + 1] = b.get().toInt().toChar()
            padre[i] = b.int; principal[i] = (b.get().toInt() and 1) == 1
            val l = b.get().toInt() and 0xff
            inicio[i] = nombres.size(); nombres.write(todo, b.position(), l); b.position(b.position() + l)
        }
        inicio[n] = nombres.size()
        val c = Ciudades(lat, lon, pob, pais, padre, principal, inicio, nombres.toByteArray())
        // Rejilla de un grado para buscar cerca sin recorrer todo.
        val tmp = HashMap<Int, MutableList<Int>>()
        for (i in 0 until n) tmp.getOrPut(celda(lat[i] / 1e5, lon[i] / 1e5)) { ArrayList() }.add(i)
        tmp.forEach { (k, v) -> c.rejilla[k] = v.toIntArray() }
        return c
    }

    private fun celda(lat: Double, lon: Double): Int = (floor(lat).toInt() + 90) * 400 + (floor(lon).toInt() + 180)

    /**
     * La ciudad donde cae un punto: la localidad más cercana a menos de 3 km (aunque sea un pueblo pequeño); si no hay,
     * la ciudad de 5 mil habitantes o más más cercana a menos de 30 km. Devuelve la ciudad grande a la que pertenece, o -1.
     */
    fun ciudadEn(ctx: Context, lat: Double, lon: Double, pais: String? = null): Int {
        val c = ciudades(ctx)
        var mejorCerca = -1; var dCerca = 3.0
        var mejorLejos = -1; var dLejos = 30.0
        val cy = floor(lat).toInt(); val cx = floor(lon).toInt()
        for (dy in -1..1) for (dx in -1..1) {
            val arr = c.rejilla[(cy + dy + 90) * 400 + (cx + dx + 180)] ?: continue
            for (i in arr) {
                if (pais != null && c.pais(i) != pais) continue
                val d = Geo.distanciaM(lat, lon, c.lat[i] / 1e5, c.lon[i] / 1e5) / 1000
                if (d < dCerca) { dCerca = d; mejorCerca = i }
                if (c.principal[i] && d < dLejos) { dLejos = d; mejorLejos = i }
            }
        }
        val i = if (mejorCerca >= 0) mejorCerca else mejorLejos
        return if (i < 0) -1 else c.raiz(i)
    }

    fun normalizar(s: String): String = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "").trim()

    /** Busca por nombre (sin acentos): primero las que empiezan así, luego las que lo contienen; las más pobladas primero. */
    fun buscarCiudad(ctx: Context, texto: String, max: Int = 30): List<Int> {
        val q = normalizar(texto)
        if (q.length < 2) return emptyList()
        val c = ciudades(ctx)
        val empieza = ArrayList<Int>(); val contiene = ArrayList<Int>()
        for (i in 0 until c.n) {
            val nm = c.normal[i] ?: normalizar(c.nombre(i)).also { c.normal[i] = it }
            if (nm.startsWith(q)) empieza += i else if (contiene.size < 400 && nm.contains(q)) contiene += i
            if (empieza.size >= 400) break
        }
        // El archivo ya viene ordenado de mayor a menor población.
        return (empieza + contiene).take(max)
    }

    // =====================================================================
    //  Patrimonio Mundial
    // =====================================================================

    @Serializable
    data class Sitio(val i: Int, val n: String, val la: Double, val lo: Double, val p: List<String>, val c: String, val a: Int, val d: Int = 0) {
        val categoria: String get() = when (c) { "n" -> "Natural"; "m" -> "Mixto"; else -> "Cultural" }
        val enPeligro: Boolean get() = d == 1
        /** Qué tan cerca hay que pasar para contarlo: los sitios naturales son enormes y su punto es solo el centro. */
        val radioM: Double get() = when (c) { "n" -> 8000.0; "m" -> 5000.0; else -> 1500.0 }
        val url: String get() = "https://whc.unesco.org/es/list/$i"
    }

    @Volatile private var sitiosCache: List<Sitio>? = null
    fun sitios(ctx: Context): List<Sitio> = sitiosCache ?: synchronized(this) {
        sitiosCache ?: ctx.assets.open("mundo/unesco.json").use { s ->
            JSON.decodeFromString(ListSerializer(Sitio.serializer()), s.readBytes().toString(Charsets.UTF_8))
        }.also { sitiosCache = it }
    }

    // =====================================================================
    //  Dónde has estado: de ubicaciones a ciudades y países
    // =====================================================================

    /** Una ciudad o un país con el tiempo que pasaste ahí. [dias] son días del calendario (epochDay) con al menos una ubicación. */
    class Estancia(val clave: String, val nombre: String, val pais: String, val lat: Double, val lon: Double) {
        var horas = 0.0
        val dias = HashSet<Int>()
        /** Horas registradas cada día (epochDay → horas). */
        val horasDia = HashMap<Int, Double>()
        var diasManuales = 0
        var manual = false
        val totalDias: Int get() = dias.size + diasManuales
        val primero: Int? get() = dias.minOrNull()
        val ultimo: Int? get() = dias.maxOrNull()
    }

    class Resumen(val ciudades: List<Estancia>, val paises: List<Estancia>, val sitios: Map<Int, Int>, val muestras: Int, val desde: Int?) {
        val codigosPaises: Set<String> = paises.map { it.clave }.toSet()
    }

    /** Horas por cuadro de ~1 km y por día; se calcula una vez y se reutiliza para cualquier periodo. */
    private class Bruto(val celdas: HashMap<Long, HashMap<Int, Float>>, val centro: HashMap<Long, DoubleArray>, val sitios: HashMap<Int, Int>, val muestras: Int, val hasta: Long,
                        val diasSitio: HashMap<Int, HashSet<Int>> = HashMap())

    @Volatile private var bruto: Bruto? = null
    private val ubicacionCelda = HashMap<Long, Pair<Int, String?>>()

    private fun dia(ms: Long): Int = Instant.ofEpochMilli(ms).atZone(Fechas.zona()).toLocalDate().toEpochDay().toInt()

    /** Día del calendario de muchas marcas de tiempo seguidas, sin crear objetos para cada una. */
    private class Calendario {
        private var ini = Long.MAX_VALUE; private var fin = Long.MIN_VALUE; private var d = 0
        fun dia(ms: Long): Int {
            if (ms in ini until fin) return d
            val f = Instant.ofEpochMilli(ms).atZone(Fechas.zona()).toLocalDate()
            d = f.toEpochDay().toInt()
            ini = f.atStartOfDay(Fechas.zona()).toInstant().toEpochMilli(); fin = f.plusDays(1).atStartOfDay(Fechas.zona()).toInstant().toEpochMilli()
            return d
        }
    }

    private fun recorrer(ctx: Context): Bruto {
        bruto?.let { if (System.currentTimeMillis() - it.hasta < 10 * 60_000L) return it }
        val celdas = HashMap<Long, HashMap<Int, Float>>()
        val centro = HashMap<Long, DoubleArray>()
        val vistos = HashMap<Int, Int>()
        val diasSitio = HashMap<Int, HashSet<Int>>()
        // Rejilla de 0.1° con los sitios cercanos a cada cuadro, para revisar cada ubicación en un instante.
        val sitios = sitios(ctx)
        val porCuadro = HashMap<Long, MutableList<Sitio>>()
        for (s in sitios) {
            val gr = s.radioM / 111_000.0
            var y = floor((s.la - gr) * 10).toLong()
            while (y <= floor((s.la + gr) * 10).toLong()) {
                var x = floor((s.lo - gr * 2) * 10).toLong()
                while (x <= floor((s.lo + gr * 2) * 10).toLong()) { porCuadro.getOrPut(y * 10_000 + x) { ArrayList() }.add(s); x++ }
                y++
            }
        }
        var n = 0
        var pMs = -1L; var pLat = 0.0; var pLon = 0.0
        val cal = Calendario()
        fun sumar(ms: Long, lat: Double, lon: Double, h: Double) {
            if (h <= 0) return
            val k = (floor(lat * 100).toLong() + 10_000L) * 100_000L + (floor(lon * 100).toLong() + 20_000L)
            val porDia = celdas.getOrPut(k) { HashMap() }
            val d = cal.dia(ms)
            porDia[d] = (porDia[d] ?: 0f) + h.toFloat()
            val c = centro.getOrPut(k) { DoubleArray(3) }
            c[0] += lat * h; c[1] += lon * h; c[2] += h
        }
        Repo.db().recorrerUbicaciones(0) { ms, lat, lon, prec ->
            if (prec > 1000) return@recorrerUbicaciones
            n++
            if (pMs >= 0) {
                val dt = (ms - pMs) / 3_600_000.0
                val cerca = Geo.distanciaM(pLat, pLon, lat, lon) < 1000
                sumar(pMs, pLat, pLon, dt.coerceIn(0.0, if (cerca) 8.0 else 0.5))
            }
            pMs = ms; pLat = lat; pLon = lon
            if (prec <= 500) porCuadro[floor(lat * 10).toLong() * 10_000 + floor(lon * 10).toLong()]?.forEach { s ->
                if (Geo.distanciaM(lat, lon, s.la, s.lo) <= s.radioM) {
                    val d = cal.dia(ms)
                    if (s.i !in vistos) vistos[s.i] = d
                    diasSitio.getOrPut(s.i) { HashSet() }.add(d)
                }
            }
        }
        if (pMs >= 0) sumar(pMs, pLat, pLon, 0.25)
        return Bruto(celdas, centro, vistos, n, System.currentTimeMillis(), diasSitio).also { bruto = it }
    }

    /** Solo para las capturas de diseño: un resumen ya hecho, sin leer ubicaciones. */
    @Volatile var resumenFijo: Resumen? = null

    /** Sube cada vez que cambian las ubicaciones, para que las pantallas vuelvan a calcular. */
    var version by androidx.compose.runtime.mutableIntStateOf(0)

    /** Olvida lo calculado (tras importar ubicaciones, por ejemplo). */
    fun invalidar() { bruto = null; android.os.Handler(android.os.Looper.getMainLooper()).post { version++ } }

    /**
     * Resume dónde has estado. [desdeDia] limita a un periodo (epochDay). Un país o ciudad entra con al menos media hora
     * registrada, para que no cuenten los que solo cruzaste en avión; las ciudades que añadiste a mano siempre entran.
     */
    fun resumen(ctx: Context, desdeDia: Int? = null): Resumen {
        val b = recorrer(ctx)
        val cs = ciudades(ctx)
        val porCiudad = LinkedHashMap<String, Estancia>()
        val porPais = LinkedHashMap<String, Estancia>()
        for ((k, porDia) in b.celdas) {
            var h = 0.0; val dias = ArrayList<Int>(); val enDia = ArrayList<Pair<Int, Float>>()
            for ((d, hh) in porDia) if (desdeDia == null || d >= desdeDia) { h += hh; dias += d; enDia += d to hh }
            if (h <= 0) continue
            val c = b.centro[k]!!
            val lat = c[0] / c[2]; val lon = c[1] / c[2]
            val (ci, pa) = synchronized(ubicacionCelda) { ubicacionCelda[k] } ?: run {
                val p = paisEn(ctx, lat, lon)?.codigo
                val i = ciudadEn(ctx, lat, lon, p).let { if (it < 0 && p == null) ciudadEn(ctx, lat, lon) else it }
                val r = i to (p ?: if (i >= 0) cs.pais(i) else null)
                synchronized(ubicacionCelda) { ubicacionCelda[k] = r }
                r
            }
            if (pa != null) porPais.getOrPut(pa) { Estancia(pa, pais(ctx, pa)?.nombre ?: pa, pa, lat, lon) }.let { it.horas += h; it.dias.addAll(dias); enDia.forEach { (d, hh) -> it.horasDia.merge(d, hh.toDouble(), Double::plus) } }
            if (ci >= 0) porCiudad.getOrPut("c$ci") { Estancia("c$ci", cs.nombre(ci), pa ?: cs.pais(ci), cs.lat[ci] / 1e5, cs.lon[ci] / 1e5) }.let { it.horas += h; it.dias.addAll(dias); enDia.forEach { (d, hh) -> it.horasDia.merge(d, hh.toDouble(), Double::plus) } }
        }
        // Las que añadiste a mano.
        for (m in Repo.config.ciudades_visitadas) {
            val ci = ciudadEn(ctx, m.lat, m.lon, m.pais)
            val clave = if (ci >= 0) "c$ci" else "m${m.id}"
            val e = porCiudad.getOrPut(clave) { Estancia(clave, if (ci >= 0) cs.nombre(ci) else m.nombre, m.pais, m.lat, m.lon) }
            e.manual = true
            val diasRango = rango(m.desde, m.hasta)
            val enPeriodo = diasRango.filter { desdeDia == null || it >= desdeDia }
            if (diasRango.isNotEmpty()) { e.dias.addAll(enPeriodo); enPeriodo.forEach { e.horasDia.putIfAbsent(it, 0.0) } }
            else if (desdeDia == null) e.diasManuales += (m.dias ?: 1)
            val pe = porPais.getOrPut(m.pais) { Estancia(m.pais, pais(ctx, m.pais)?.nombre ?: m.pais, m.pais, m.lat, m.lon) }
            pe.manual = true
            if (diasRango.isNotEmpty()) { pe.dias.addAll(enPeriodo); enPeriodo.forEach { pe.horasDia.putIfAbsent(it, 0.0) } } else if (desdeDia == null) pe.diasManuales += (m.dias ?: 1)
        }
        for (cod in Repo.config.paises_rascados) porPais.getOrPut(cod) { Estancia(cod, pais(ctx, cod)?.nombre ?: cod, cod, 0.0, 0.0) }.manual = true
        val tapados = Repo.config.paises_tapados.toSet()
        val ciudades = porCiudad.values.filter { it.manual || it.horas >= 0.5 }.sortedWith(compareByDescending<Estancia> { it.totalDias }.thenByDescending { it.horas })
        val paises = porPais.values.filter { it.clave !in tapados && (it.manual || it.horas >= 0.5) }.sortedWith(compareByDescending<Estancia> { it.totalDias }.thenByDescending { it.horas })
        val descartados = Repo.config.unesco_descartados.toSet()
        val sitios = HashMap<Int, Int>()
        b.sitios.forEach { (id, d) -> if (id !in descartados && (desdeDia == null || d >= desdeDia)) sitios[id] = d }
        Repo.config.unesco_marcados.forEach { m -> val d = runCatching { Fechas.parse(m.fecha).toEpochDay().toInt() }.getOrNull() ?: 0; if (desdeDia == null || d >= desdeDia) sitios.putIfAbsent(m.id, d) }
        return Resumen(ciudades, paises, sitios, b.muestras, desdeDia)
    }

    // =====================================================================
    //  Un día en el mundo: para los análisis del día
    // =====================================================================

    /** Contexto de la app para los cálculos que no reciben uno (el panel de análisis). Se pone al arrancar. */
    @Volatile var app: Context? = null

    /** Dónde estuviste un día: ciudades y países con sus horas, lo que fue primera vez y los sitios del Patrimonio Mundial. */
    class DiaMundo(val ciudades: List<Pair<Estancia, Double>>, val paises: List<Pair<Estancia, Double>>, val ciudadesNuevas: List<Estancia>,
                   val paisesNuevos: List<Estancia>, val sitios: List<Sitio>, val sitiosNuevos: Set<Int>)

    /** Todos los días con ubicación, ya resumidos (epochDay → día). Se recalcula cuando cambian las ubicaciones. */
    @Volatile private var diarioCache: Pair<Long, Map<Int, DiaMundo>>? = null

    fun diario(ctx: Context): Map<Int, DiaMundo> {
        val b = recorrer(ctx)
        diarioCache?.let { if (it.first == b.hasta) return it.second }
        val r = resumen(ctx)
        val sitiosPorId = sitios(ctx).associateBy { it.i }
        val ciu = HashMap<Int, MutableList<Pair<Estancia, Double>>>(); val pai = HashMap<Int, MutableList<Pair<Estancia, Double>>>()
        r.ciudades.forEach { e -> e.horasDia.forEach { (d, h) -> ciu.getOrPut(d) { ArrayList() }.add(e to h) } }
        r.paises.forEach { e -> e.horasDia.forEach { (d, h) -> pai.getOrPut(d) { ArrayList() }.add(e to h) } }
        val sit = HashMap<Int, MutableList<Sitio>>()
        val descartados = Repo.config.unesco_descartados.toSet()
        b.diasSitio.forEach { (id, dias) -> if (id !in descartados) sitiosPorId[id]?.let { s -> dias.forEach { d -> sit.getOrPut(d) { ArrayList() }.add(s) } } }
        Repo.config.unesco_marcados.forEach { m -> runCatching { Fechas.parse(m.fecha).toEpochDay().toInt() }.getOrNull()?.let { d -> sitiosPorId[m.id]?.let { s -> sit.getOrPut(d) { ArrayList() }.let { l -> if (l.none { it.i == s.i }) l.add(s) } } } }
        val dias = ciu.keys + pai.keys + sit.keys
        val res = HashMap<Int, DiaMundo>()
        for (d in dias) {
            val c = ciu[d].orEmpty().sortedByDescending { it.second }; val p = pai[d].orEmpty().sortedByDescending { it.second }
            val s = sit[d].orEmpty()
            res[d] = DiaMundo(c, p, c.map { it.first }.filter { it.primero == d }, p.map { it.first }.filter { it.primero == d }, s,
                s.filter { r.sitios[it.i] == d }.map { it.i }.toSet())
        }
        diarioCache = b.hasta to res
        return res
    }

    /** Lo ya calculado, sin leer ubicaciones (para el hilo principal). */
    fun diarioListo(): Map<Int, DiaMundo>? = diarioCache?.second

    /** Un día (AAAA-MM-DD). */
    fun delDia(ctx: Context, fecha: String): DiaMundo? = runCatching { diario(ctx)[Fechas.parse(fecha).toEpochDay().toInt()] }.getOrNull()

    private fun rango(desde: String?, hasta: String?): List<Int> {
        val a = desde?.let { runCatching { Fechas.parse(it).toEpochDay().toInt() }.getOrNull() } ?: return emptyList()
        val z = hasta?.let { runCatching { Fechas.parse(it).toEpochDay().toInt() }.getOrNull() } ?: a
        return if (z < a || z - a > 3650) listOf(a) else (a..z).toList()
    }

    fun fechaDeDia(d: Int): String = Fechas.iso(LocalDate.ofEpochDay(d.toLong()))
}

/** Una ciudad que añadiste a mano: de dónde (para ubicarla) y, si quieres, cuándo o cuántos días. */
@Serializable
data class CiudadVisitada(val id: String, val nombre: String, val pais: String, val lat: Double, val lon: Double,
                          val desde: String? = null, val hasta: String? = null, val dias: Int? = null, val nota: String = "")

/** Un sitio del Patrimonio Mundial marcado a mano como visitado. */
@Serializable
data class SitioMarcado(val id: Int, val fecha: String)
