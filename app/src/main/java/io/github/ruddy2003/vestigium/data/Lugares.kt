package io.github.ruddy2003.vestigium.data

import android.content.Context
import java.time.LocalTime

/**
 * Lugares especiales: cuando llevas un rato en un sitio que no tienes guardado ni ignorado, la app pregunta si es un
 * lugar especial. Al guardarlo se reconstruyen las visitas de los últimos 30 días con las ubicaciones que ya había.
 * También reparte el tiempo de cada día entre los tipos de lugar (casa, trabajo, bar…) y mide el ánimo en cada tipo.
 */
object Lugares {
    /** Para no insistir: como mucho dos preguntas al día, nunca de noche, y «ahora no» vuelve a preguntar a los tres días (dos veces). */
    const val MAX_AL_DIA = 2
    const val MAX_PREGUNTAS = 3
    private const val TRES_DIAS = 3L * 86_400_000L

    // ---------- Visitas: cuándo cuentan y cómo se cierran (esquema 19) ----------

    /** Lo mínimo que dura una visita para contar: menos es pasar cerca o un brinco del GPS (decisión del autor, 5 nov 2026). */
    const val VISITA_MINIMA_MS = 8 * 60_000L
    /** Cómo se supo la salida ([Visita.cierre]). */
    const val CIERRE_GEOCERCA = "geocerca"
    const val CIERRE_OTRO_LUGAR = "otro_lugar"
    const val CIERRE_UBICACIONES = "ubicaciones"
    const val CIERRE_RECONSTRUIDA = "reconstruida"
    const val CIERRE_MANUAL = "manual"
    /** Una salida deducida de tus puntos solo reemplaza a la que avisó Android si es al menos así de anterior. */
    private const val ADELANTO_MINIMO_MS = 15 * 60_000L
    /** Puntos más imprecisos que esto no deciden si estás dentro o fuera. */
    private const val PRECISION_MAXIMA_M = 150.0

    /** El radio con que se vigila un lugar: el de su geocerca (al menos 80 m). */
    private fun radioVigilado(l: Lugar): Double = l.radio_m.coerceAtLeast(80).toDouble()

    /**
     * ¿Están lejos dos lugares? Sí, si sus círculos no se tocan. Si uno está dentro del otro (un gimnasio dentro del campus),
     * llegar a uno no te saca del otro.
     */
    fun lejos(a: Lugar, b: Lugar): Boolean = Geo.distanciaM(a.lat, a.lon, b.lat, b.lon) > radioVigilado(a) + radioVigilado(b)

    /** Cuánto dura una visita cerrada, en ms (null si sigue abierta o sus horas no se leen). */
    fun duracion(v: Visita): Long? {
        val a = Fechas.millis(v.entrada); val z = v.salida?.let { Fechas.millis(it) } ?: return null
        return if (a <= 0 || z <= 0) null else z - a
    }

    /**
     * El primer momento entre [desde] y [hasta] en que tus ubicaciones ya te ponen fuera de [l]: dos puntos seguidos fuera
     * del círculo vigilado aun contando su imprecisión (un punto suelto puede ser un brinco del GPS). Devuelve la hora del
     * primero de los dos, o null si nada dice que saliste. Solo cuentan puntos con precisión de 150 m o mejor; a los que
     * no la traen (los de Google Maps) se les supone 100 m.
     */
    fun salidaPorUbicaciones(l: Lugar, desde: Long, hasta: Long): Long? {
        if (hasta <= desde) return null
        val r = radioVigilado(l)
        var primeroFuera: Long? = null
        for (p in Repo.db().ubicacionesSinFotos(desde, hasta)) {
            val prec = p.precision ?: Db.PRECISION_SIN_DATO
            if (prec > PRECISION_MAXIMA_M) continue
            val fuera = Geo.distanciaM(p.lat, p.lon, l.lat, l.lon) - prec > r
            if (!fuera) { primeroFuera = null; continue }
            if (primeroFuera != null) return primeroFuera
            primeroFuera = p.ms
        }
        return null
    }

    /**
     * Si [v] debió terminar antes de [hasta]: al entrar a un lugar lejano (otra visita que empieza mientras esta sigue) o
     * cuando tus ubicaciones ya te ponen fuera, lo que pase primero. Null si nada dice que saliste antes.
     */
    fun cierreDeducido(v: Visita, hasta: Long, lugares: Map<String, Lugar>, visitas: List<Visita>): Pair<Long, String>? {
        val l = lugares[v.lugar] ?: return null
        val a = Fechas.millis(v.entrada); if (a <= 0) return null
        val otra = visitas.asSequence().filter { it.id != v.id }.mapNotNull { w ->
            val t = Fechas.millis(w.entrada); val lw = lugares[w.lugar]
            if (lw != null && t > a && t < hasta && lejos(l, lw)) t else null
        }.minOrNull()
        val porPuntos = salidaPorUbicaciones(l, a, otra ?: hasta)
        return when {
            porPuntos != null -> porPuntos to CIERRE_UBICACIONES
            otra != null -> otra to CIERRE_OTRO_LUGAR
            else -> null
        }
    }

    /**
     * En cada pulso: cierra las visitas abiertas de las que tus ubicaciones ya dicen que saliste (cuando Android no avisó la
     * salida, por ejemplo con el teléfono en reposo). Las que así duran menos de [VISITA_MINIMA_MS] se quitan. Si ya
     * volviste, la vuelta sale de tus ubicaciones ([reconstruirEn]). Devuelve si cerró alguna: entonces hay que volver a
     * registrar las geocercas, porque Android sigue creyendo que estás dentro y no avisaría la próxima entrada.
     */
    fun cerrarAbiertas(ahora: Long = System.currentTimeMillis()): Boolean {
        val porId = Repo.lugares.associateBy { it.id }
        val cambios = ArrayList<Visita>(); val quitar = HashSet<String>()
        Repo.visitas.filter { it.salida == null }.forEach { v ->
            val l = porId[v.lugar] ?: return@forEach
            val a = Fechas.millis(v.entrada); if (a <= 0) return@forEach
            val fin = salidaPorUbicaciones(l, a, ahora) ?: return@forEach
            val cerrada = v.copy(salida = Fechas.isoDeMillis(fin), cierre = CIERRE_UBICACIONES)
            if (fin - a < VISITA_MINIMA_MS) quitar += v.id else cambios += cerrada
            val despues = Repo.visitas.filter { it.id != v.id } + cambios
            cambios += reconstruirEn(l, fin, ahora + 60_000L, despues, ahora)
        }
        Repo.cambiarVisitas(cambios, quitar)
        return cambios.isNotEmpty() || quitar.isNotEmpty()
    }

    /**
     * Una sola vez (esquema 19): corrige las visitas guardadas antes de estas reglas. Primero quita las de menos de
     * [VISITA_MINIMA_MS] (así una entrada falsa no corta la visita del lugar donde seguías); luego cada visita termina, a
     * más tardar, al entrar a un lugar lejano o cuando tus ubicaciones te ponen fuera (si eso es al menos 15 min antes de
     * la salida que tenía). Las que quedan de menos de [VISITA_MINIMA_MS] también se quitan. Devuelve (corregidas, quitadas).
     */
    fun limpiarVisitasPasadas(ahora: Long = System.currentTimeMillis()): Pair<Int, Int> {
        val porId = Repo.lugares.associateBy { it.id }
        val todas = Repo.visitas
        val cortas = todas.filter { (duracion(it) ?: Long.MAX_VALUE) < VISITA_MINIMA_MS }.map { it.id }.toSet()
        val vivas = todas.filter { it.id !in cortas }
        val corregidas = vivas.mapNotNull { v ->
            val z = v.salida?.let { Fechas.millis(it) }?.takeIf { it > 0 } ?: ahora
            val (fin, como) = cierreDeducido(v, z, porId, vivas) ?: return@mapNotNull null
            val adelanto = z - fin
            if (adelanto <= 0 || (como == CIERRE_UBICACIONES && v.salida != null && adelanto < ADELANTO_MINIMO_MS)) null
            else v.copy(salida = Fechas.isoDeMillis(fin), cierre = como)
        }
        val quitar = cortas + corregidas.filter { (duracion(it) ?: Long.MAX_VALUE) < VISITA_MINIMA_MS }.map { it.id }
        val guardar = ArrayList(corregidas.filter { it.id !in quitar })
        // Lo que quedó entre la salida corregida y la que tenía: si volviste al mismo lugar (Android no avisó la entrada
        // porque creía que nunca habías salido), la vuelta sale de tus ubicaciones.
        corregidas.forEach { v ->
            val l = porId[v.lugar] ?: return@forEach
            val antes = todas.firstOrNull { it.id == v.id } ?: return@forEach
            val desde = v.salida?.let { Fechas.millis(it) } ?: return@forEach
            val hasta = antes.salida?.let { Fechas.millis(it) } ?: ahora
            val actuales = vivas.filter { it.id !in quitar && guardar.none { g -> g.id == it.id } } + guardar
            guardar += reconstruirEn(l, desde, hasta, actuales, ahora)
        }
        Repo.cambiarVisitas(guardar, quitar)
        return corregidas.size to quitar.size
    }

    /**
     * La salida de una visita abierta cuando Android avisa que saliste a la hora [t]: si tus ubicaciones dicen que saliste
     * al menos 15 min antes (un aviso tardío, o el que llega al volver a registrar las geocercas), vale esa hora.
     */
    fun salidaAvisada(v: Visita, t: Long): Pair<Long, String> {
        val l = Repo.lugares.firstOrNull { it.id == v.lugar }
        val a = Fechas.millis(v.entrada)
        val antes = l?.let { salidaPorUbicaciones(it, a, t) }?.takeIf { t - it >= ADELANTO_MINIMO_MS }
        return if (antes != null) antes to CIERRE_UBICACIONES else t to CIERRE_GEOCERCA
    }

    /** El sitio donde llevas un rato, si cumple todo para preguntar (o null). Solo lee; no guarda nada. */
    fun estancia(ahora: Long = System.currentTimeMillis()): CandidatoLugar? {
        val c = Repo.config
        val ventana = c.lugar_minutos.coerceIn(10, 120) * 60_000L
        val radio = c.lugar_radio_m.coerceIn(30, 300).toDouble()
        val pts = runCatching { Repo.db().ubicacionesSinFotos(ahora - ventana - 45 * 60_000L, ahora + 60_000L) }.getOrDefault(emptyList())
            .filter { (it.precision ?: Db.PRECISION_SIN_DATO) <= 120.0 }
        if (pts.size < 2) return null
        val ultimo = pts.last()
        if (ahora - ultimo.ms > 25 * 60_000L) return null
        // Hacia atrás, mientras sigas dentro del radio del último punto.
        var i = pts.size - 1
        while (i > 0 && Geo.distanciaM(pts[i - 1].lat, pts[i - 1].lon, ultimo.lat, ultimo.lon) <= radio) i--
        val tramo = pts.subList(i, pts.size)
        if (tramo.size < 2 || ultimo.ms - tramo.first().ms < ventana) return null
        val lat = tramo.map { it.lat }.average(); val lon = tramo.map { it.lon }.average()
        if (Repo.lugares.any { Geo.distanciaM(lat, lon, it.lat, it.lon) <= it.radio_m + radio / 2 }) return null
        if (c.lugares_ignorados.any { Geo.distanciaM(lat, lon, it.lat, it.lon) <= it.radio_m }) return null
        return CandidatoLugar(lat, lon, tramo.first().ms, ultimo.ms)
    }

    /** El candidato guardado cerca de un punto (150 m). */
    fun candidatoCerca(lat: Double, lon: Double): CandidatoLugar? =
        Repo.config.lugares_candidatos.firstOrNull { Geo.distanciaM(lat, lon, it.lat, it.lon) <= 150.0 }

    /**
     * Desde el pulso de 15 minutos: si hay un sitio nuevo donde llevas el tiempo pedido, lo guarda como candidato y
     * devuelve el candidato para avisar. Respeta la noche, el máximo diario y el «ahora no».
     */
    fun revisar(ctx: Context, ahora: Long = System.currentTimeMillis()): CandidatoLugar? {
        val c = Repo.config
        if (!c.lugares_detectar) return null
        val hora = LocalTime.now().hour
        if (hora < 8 || hora >= 22) return null
        val hoy = Fechas.hoyCalendario()
        if (c.disparos.keys.count { it.startsWith("lugar:$hoy:") } >= MAX_AL_DIA) return null
        val e = estancia(ahora) ?: return null
        val previo = candidatoCerca(e.lat, e.lon)
        if (previo != null && (previo.preguntas >= MAX_PREGUNTAS || ahora < previo.proximo_ms)) return null
        val sug = runCatching { sugerencia(ctx, e.lat, e.lon) }.getOrDefault("")
        val n = e.copy(preguntas = (previo?.preguntas ?: 0) + 1, proximo_ms = ahora + TRES_DIAS, sugerencia = sug.ifBlank { previo?.sugerencia.orEmpty() })
        val limite = ahora - 40L * 86_400_000
        Repo.cambiarConfig { cc ->
            val resto = cc.lugares_candidatos.filter { Geo.distanciaM(it.lat, it.lon, e.lat, e.lon) > 150.0 }.takeLast(9)
            cc.copy(lugares_candidatos = resto + n,
                disparos = cc.disparos.filterValues { it > limite } + ("lugar:$hoy:${cc.disparos.keys.count { it.startsWith("lugar:$hoy:") }}" to ahora))
        }
        return n
    }

    /** Un nombre para proponer: la calle o el sitio según el teléfono (si responde). */
    @Suppress("DEPRECATION")
    fun sugerencia(ctx: Context, lat: Double, lon: Double): String {
        val g = android.location.Geocoder(ctx, java.util.Locale("es", "MX"))
        val a = g.getFromLocation(lat, lon, 1)?.firstOrNull() ?: return ""
        val sitio = a.featureName?.takeIf { f -> f.any { it.isLetter() } && f != a.thoroughfare }
        return sitio ?: listOfNotNull(a.thoroughfare, a.subLocality).joinToString(", ")
    }

    /** Sugerencia de tipo según la hora en que estuviste ahí. */
    fun tipoProbable(cand: CandidatoLugar): String {
        val h = Fechas.local(cand.desde_ms).hour
        return when (h) { in 8..15 -> "trabajo"; in 19..23 -> "social"; else -> "otro" }
    }

    /** Guarda el lugar, quita el candidato, reconstruye las visitas de los últimos 30 días y actualiza las geocercas. */
    fun guardar(ctx: Context, cand: CandidatoLugar, nombre: String, tipo: String, radio: Int): Int {
        val l = Lugar(Repo.nuevoId(), nombre.trim().ifBlank { "Lugar" }, cand.lat, cand.lon, radio, tipo)
        Repo.guardarLugar(l)
        Repo.cambiarConfig { cc -> cc.copy(lugares_candidatos = cc.lugares_candidatos.filter { Geo.distanciaM(it.lat, it.lon, cand.lat, cand.lon) > 150.0 }) }
        val n = runCatching { reconstruir(l) }.getOrDefault(0)
        runCatching { io.github.ruddy2003.vestigium.sensores.Geocercas.registrar(ctx) }
        return n
    }

    /** «No guardar nunca aquí»: ignora 150 m alrededor. */
    fun ignorar(cand: CandidatoLugar) {
        Repo.cambiarConfig { cc ->
            cc.copy(lugares_ignorados = cc.lugares_ignorados + LugarIgnorado(cand.lat, cand.lon, 150, Fechas.diaLogico()),
                lugares_candidatos = cc.lugares_candidatos.filter { Geo.distanciaM(it.lat, it.lon, cand.lat, cand.lon) > 150.0 })
        }
    }

    /** «Ahora no»: se queda esperando y vuelve a preguntar a los tres días (si no se ha preguntado ya tres veces). */
    fun despues(cand: CandidatoLugar) {
        val ahora = System.currentTimeMillis()
        Repo.cambiarConfig { cc -> cc.copy(lugares_candidatos = cc.lugares_candidatos.map { if (Geo.distanciaM(it.lat, it.lon, cand.lat, cand.lon) <= 150.0) it.copy(proximo_ms = ahora + TRES_DIAS) else it }) }
    }

    /** Las visitas con las de [de] pasadas a [a]; con [a] = null, sin ellas (al borrar un lugar, N-05). */
    fun reasignar(visitas: List<Visita>, de: String, a: String?): List<Visita> =
        if (a == null) visitas.filter { it.lugar != de } else visitas.map { if (it.lugar == de) it.copy(lugar = a) else it }

    /** «Ignorar este sitio para siempre» para un lugar ya guardado: deja de preguntar en su radio. */
    fun ignorarLugar(l: Lugar) {
        Repo.cambiarConfig { cc -> cc.copy(lugares_ignorados = cc.lugares_ignorados + LugarIgnorado(l.lat, l.lon, l.radio_m.coerceAtLeast(150), Fechas.diaLogico())) }
    }

    /** Los que esperan respuesta (ya avisados y todavía sin decidir), del más reciente al más viejo. */
    fun pendientes(): List<CandidatoLugar> = Repo.config.lugares_candidatos.filter { it.preguntas in 1 until MAX_PREGUNTAS + 1 }.sortedByDescending { it.hasta_ms }

    /**
     * Reconstruye las visitas a [l] de los últimos [dias] días con las ubicaciones guardadas: cada racha de puntos dentro
     * del radio (con huecos de hasta dos horas) es una visita. No toca las que ya existen.
     */
    fun reconstruir(l: Lugar, dias: Int = 30, ahora: Long = System.currentTimeMillis()): Int {
        val nuevas = reconstruirEn(l, ahora - dias * 86_400_000L, ahora + 60_000L, Repo.visitas, ahora)
        if (nuevas.isNotEmpty()) Repo.agregarVisitas(nuevas)
        return nuevas.size
    }

    /**
     * Las visitas a [l] que salen de tus ubicaciones entre [desde] y [hasta] y no se enciman con ninguna de [visitas] (las
     * de [l]). No guarda nada. Una racha que termina hace menos de 30 min queda abierta, si no hay ya una abierta ahí.
     */
    fun reconstruirEn(l: Lugar, desde: Long, hasta: Long, visitas: List<Visita>, ahora: Long = System.currentTimeMillis()): List<Visita> {
        val pts = Repo.db().ubicacionesSinFotos(desde, hasta)
        val radio = l.radio_m.coerceAtLeast(60).toDouble()
        val rachas = ArrayList<Pair<Long, Long>>()
        var ini: Long? = null; var fin = 0L
        for (p in pts) {
            val dentro = Geo.distanciaM(p.lat, p.lon, l.lat, l.lon) <= radio
            if (dentro) {
                if (ini != null && p.ms - fin > 2 * 3_600_000L) { rachas += ini to fin; ini = null }
                if (ini == null) ini = p.ms
                fin = p.ms
            } else if (ini != null) { rachas += ini to fin; ini = null }
        }
        ini?.let { rachas += it to fin }
        val existentes = visitas.filter { it.lugar == l.id }.map { Fechas.millis(it.entrada) to (it.salida?.let { s -> Fechas.millis(s) } ?: ahora) }
        return rachas.filter { (a, b) -> b - a >= VISITA_MINIMA_MS && existentes.none { (x, y) -> a < y && b > x } }.map { (a, b) ->
            val abierta = ahora - b < 30 * 60_000L && visitas.none { it.lugar == l.id && it.salida == null }
            Visita(Repo.nuevoId(), l.id, Fechas.isoDeMillis(a), if (abierta) null else Fechas.isoDeMillis(b), if (abierta) null else CIERRE_RECONSTRUIDA)
        }
    }

    /** Minutos de cada día en cada tipo de lugar (la visita se recorta a cada día lógico). */
    fun minutosPorTipo(ahora: Long = System.currentTimeMillis()): Map<String, Map<String, Double>> {
        val tipos = Repo.lugares.associate { it.id to it.tipo }
        val out = HashMap<String, HashMap<String, Double>>()
        Repo.visitas.forEach { v ->
            val tipo = tipos[v.lugar] ?: return@forEach
            var a = Fechas.millis(v.entrada); val z = v.salida?.let { Fechas.millis(it) } ?: ahora
            if (a <= 0 || z <= a || z - a > 40L * 86_400_000L) return@forEach
            while (a < z) {
                val f = Fechas.diaLogicoDeMillis(a)
                val y = minOf(z, Fechas.finDiaLogico(f))
                if (y <= a) break
                val m = out.getOrPut(f) { HashMap() }
                m[tipo] = (m[tipo] ?: 0.0) + (y - a) / 60_000.0
                a = y
            }
        }
        return out.mapValues { (_, m) -> m.mapValues { minOf(it.value, 1440.0) } }
    }

    /** Horas por tipo de lugar entre dos fechas (incluidas). */
    fun horasPorTipo(desde: String, hasta: String, porDia: Map<String, Map<String, Double>> = minutosPorTipo()): Map<String, Double> {
        val out = HashMap<String, Double>()
        porDia.filterKeys { it in desde..hasta }.values.forEach { m -> m.forEach { (t, min) -> out[t] = (out[t] ?: 0.0) + min / 60.0 } }
        return out.toList().sortedByDescending { it.second }.toMap()
    }

    /** Tipo del lugar donde se registró un momento: el que guardó al registrarlo o, si no, la visita que lo cubre. */
    fun tipoDe(m: Momento): String? {
        m.lugar?.let { id -> Repo.lugares.firstOrNull { it.id == id }?.let { return it.tipo } }
        val t = Fechas.millis(m.ts); if (t <= 0) return null
        val v = Repo.visitas.lastOrNull { Fechas.millis(it.entrada) <= t && (it.salida?.let { s -> Fechas.millis(s) } ?: Long.MAX_VALUE) >= t } ?: return null
        return Repo.lugares.firstOrNull { it.id == v.lugar }?.tipo
    }

    /** Valencia media de tus registros de ánimo en cada tipo de lugar (media y cuántos), sin lo privado. */
    fun valenciaPorTipo(desde: String = "0000", hasta: String = "9999"): Map<String, Pair<Double, Int>> =
        Repo.momentos.filter { it.esAnimo && !it.privado && it.valencia != null && it.fecha in desde..hasta }
            .mapNotNull { m -> tipoDe(m)?.let { it to m.valencia!!.toDouble() } }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, l) -> l.average() to l.size }
}
