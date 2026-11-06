package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.privado
import android.content.Context
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Geo
import io.github.ruddy2003.vestigium.data.LogroPropio
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.alFuturo
import io.github.ruddy2003.vestigium.data.esAnimo
import java.time.LocalDate

/**
 * Logros: lo que has conseguido en la app y en tu vida medida. Cada uno sabe cuánto llevas, cuánto falta y el día en que lo lograste.
 * Hay de cantidad (100 días escritos), de récord (un día de 20,000 pasos), de constancia (25 de 30 días; no hace falta racha)
 * y los que inventas tú. Se calculan de tus datos; nada se guarda salvo cuáles ya viste.
 */
object Logros {
    class Logro(
        val id: String, val nombre: String, val descripcion: String, val icono: String, val categoria: String,
        /** 1 bronce, 2 plata, 3 oro, 4 legendario. */
        val nivel: Int, val meta: Double, val progreso: Double,
        /** Día en que se logró (null = todavía no). */
        val conseguido: String?, val propio: Boolean = false, val secreto: Boolean = false, val unidad: String = "",
    ) {
        val hecho: Boolean get() = conseguido != null
        val fraccion: Float get() = if (meta <= 0) 0f else (progreso / meta).coerceIn(0.0, 1.0).toFloat()
    }

    val CATEGORIAS = listOf("Diario", "Mundo", "Cuerpo", "Mente", "Cultura", "Gente", "Constancia", "Curiosos", "Tuyos")

    @Volatile private var cache: Pair<List<Any?>, List<Logro>>? = null
    private fun clave(): List<Any?> = listOf(Repo.dias, Repo.momentos, Repo.salud, Repo.entrenos, Repo.obras, Repo.cartas, Repo.reacciones, Repo.respuestas,
        Repo.gastos, Repo.pantalla, Repo.config, Repo.ascensos, Repo.sesiones, Mundo.version, Fechas.diaLogico(), Repo.tramos)

    /** Fecha en que una lista ordenada de días llega a [n] (el día del n-ésimo). */
    private fun alLlegar(fechas: List<String>, n: Int): String? = if (n <= 0) null else fechas.getOrNull(n - 1)

    /** Fecha en que una suma acumulada (por día, ordenada) llega a [meta]. */
    private fun acumulado(porDia: List<Pair<String, Double>>, meta: Double): String? {
        var s = 0.0
        for ((f, v) in porDia) { s += v; if (s >= meta) return f }
        return null
    }

    /** Primer día en que hubo [k] días seguidos (en una lista ordenada de días). */
    private fun racha(fechas: List<String>, k: Int): Pair<Int, String?> {
        var mejor = 0; var actual = 0; var prev: String? = null; var cuando: String? = null
        for (f in fechas) {
            actual = if (prev != null && Fechas.sumar(prev, 1) == f) actual + 1 else 1
            if (actual > mejor) mejor = actual
            if (cuando == null && actual >= k) cuando = f
            prev = f
        }
        return mejor to cuando
    }

    /**
     * Constancia: el primer día en que, mirando los últimos [ventana] días, hubo al menos [k] con algo. No hace falta racha.
     * Devuelve el mejor conteo en cualquier ventana y cuándo se logró.
     */
    private fun constancia(dias: Set<String>, ventana: Int, k: Int): Pair<Int, String?> {
        if (dias.isEmpty()) return 0 to null
        val ord = dias.sorted()
        val ini = Fechas.parse(ord.first()); val fin = Fechas.parse(maxOf(ord.last(), Fechas.diaLogico()))
        val n = (fin.toEpochDay() - ini.toEpochDay()).toInt() + 1
        val tiene = BooleanArray(n) { Fechas.iso(ini.plusDays(it.toLong())) in dias }
        var suma = 0; var mejor = 0; var cuando: String? = null
        for (i in 0 until n) {
            if (tiene[i]) suma++
            if (i >= ventana && tiene[i - ventana]) suma--
            if (suma > mejor) mejor = suma
            if (cuando == null && suma >= k) cuando = Fechas.iso(ini.plusDays(i.toLong()))
        }
        return mejor to cuando
    }

    fun todos(ctx: Context?): List<Logro> {
        val k = clave()
        cache?.let { if (it.first.size == k.size && it.first.indices.all { i -> it.first[i] === k[i] || it.first[i] == k[i] }) return it.second }
        val l = calcular(ctx)
        cache = k to l
        return l
    }

    private fun calcular(ctx: Context?): List<Logro> {
        val out = ArrayList<Logro>()
        fun escalones(base: String, cat: String, icono: String, unidad: String, metas: List<Pair<Double, String>>, progreso: Double,
                      cuando: (Double) -> String?, descripcion: (Double) -> String, secreto: Boolean = false) {
            metas.forEachIndexed { i, (m, nombre) ->
                out += Logro("${base}_${m.toLong()}", nombre, descripcion(m), icono, cat, (i + 1).coerceAtMost(4), m, progreso, if (progreso >= m) cuando(m) else null, unidad = unidad, secreto = secreto)
            }
        }
        val hoy = Fechas.diaLogico()

        // ------------------------------------------------ Diario
        val conDiario = Repo.dias.values.filter { it.nota.isNotBlank() || it.valencia != null }.map { it.fecha }.sorted()
        escalones("diario", "Diario", "✎", "días", listOf(1.0 to "Primera página", 7.0 to "Una semana escrita", 30.0 to "Un mes de páginas", 100.0 to "Cien días",
            365.0 to "Un año en papel", 1000.0 to "Mil días"), conDiario.size.toDouble(), { alLlegar(conDiario, it.toInt()) }, { "Llena el diario ${porNumero(it.toInt(), "1 día", "${it.toInt()} días")}" })
        val palabras = Repo.dias.values.filter { it.nota.isNotBlank() }.sortedBy { it.fecha }.map { it.fecha to Texto.palabras(it.nota).toDouble() }
        escalones("palabras", "Diario", "❡", "palabras", listOf(10_000.0 to "Un cuento", 50_000.0 to "Una novela corta", 100_000.0 to "Una novela",
            500_000.0 to "Una saga", 1_000_000.0 to "Un millón de palabras"), palabras.sumOf { it.second }, { acumulado(palabras, it) }, { "Escribe %,d palabras en el diario".format(it.toLong()) })
        val (rachaMax, _) = racha(conDiario, 1)
        listOf(7 to "Siete seguidos", 30 to "Un mes sin faltar", 100 to "Cien seguidos").forEachIndexed { i, (n, nom) ->
            out += Logro("racha_diario_$n", nom, "Escribe ${porNumero(n, "1 día seguido", "$n días seguidos")}", "⚑", "Diario", i + 1, n.toDouble(), rachaMax.toDouble(), racha(conDiario, n).second, unidad = "días")
        }
        val suenos = Repo.dias.values.filter { it.sueno_recordado }.map { it.fecha }.sorted()
        escalones("suenos", "Diario", "☾", "sueños", listOf(1.0 to "Primer sueño", 10.0 to "Cazador de sueños", 50.0 to "Onironauta", 100.0 to "Archivo onírico"),
            suenos.size.toDouble(), { alLlegar(suenos, it.toInt()) }, { "Anota ${porNumero(it.toInt(), "1 sueño", "${it.toInt()} sueños")}" })
        Repo.dias.values.filter { it.sueno_lucidez == "total" || it.sueno_lucidez == "parcial" }.minByOrNull { it.fecha }.let { d ->
            out += Logro("lucido", "Sueño lúcido", "Anota un sueño en el que supiste que soñabas", "◐", "Diario", 3, 1.0, if (d != null) 1.0 else 0.0, d?.fecha, secreto = true)
        }
        val cartas = Repo.cartas.map { it.escrita }.sorted()
        out += Logro("carta_1", "Carta al futuro", "Escribe una carta sellada", "✉", "Diario", 1, 1.0, cartas.size.toDouble().coerceAtMost(1.0), cartas.firstOrNull())
        escalones("cartas", "Diario", "✉", "cartas", listOf(10.0 to "Corresponsal", 25.0 to "Epistolario"), cartas.size.toDouble(), { alLlegar(cartas, it.toInt()) }, { "Escribe ${porNumero(it.toInt(), "1 carta", "${it.toInt()} cartas")}" })
        Repo.cartas.filter { it.abierta }.mapNotNull { it.abierta_el?.take(10) ?: it.abrir_en }.minOrNull().let { f ->
            out += Logro("carta_abierta", "Del pasado", "Abre una carta que te escribiste", "✉", "Diario", 2, 1.0, if (f != null) 1.0 else 0.0, f)
        }
        val momentos = Repo.momentos.filter { it.tipo != "muestreo" && !it.privado }.map { it.fecha }.sorted()
        escalones("momentos", "Diario", "◷", "momentos", listOf(10.0 to "Coleccionista", 100.0 to "Cien momentos", 1000.0 to "Mil momentos"), momentos.size.toDouble(),
            { alLlegar(momentos, it.toInt()) }, { "Registra ${porNumero(it.toInt(), "1 momento", "${it.toInt()} momentos")}" })
        val avisos = Repo.momentos.filter { it.tipo == "muestreo" }.map { it.fecha }.sorted()
        escalones("avisos", "Diario", "◔", "avisos", listOf(50.0 to "Atento", 250.0 to "Muy atento", 1000.0 to "Siempre presente"), avisos.size.toDouble(),
            { alLlegar(avisos, it.toInt()) }, { "Contesta ${porNumero(it.toInt(), "1 aviso", "${it.toInt()} avisos")} de ánimo" })

        // ------------------------------------------------ Mundo
        val res = ctx?.let { c -> runCatching { Mundo.resumenFijo ?: Mundo.resumen(c) }.getOrNull() }
        if (res != null) {
            val ciu = res.ciudades.mapNotNull { e -> (e.primero ?: if (e.manual) Fechas.parse(hoy).toEpochDay().toInt() else null)?.let { Mundo.fechaDeDia(it) } }.sorted()
            escalones("ciudades", "Mundo", "⌂", "ciudades", listOf(2.0 to "Nueva ciudad", 10.0 to "Viajero", 25.0 to "Trotamundos", 50.0 to "Cartógrafo", 100.0 to "Cien ciudades"),
                ciu.size.toDouble(), { alLlegar(ciu, it.toInt()) }, { "Pasa tiempo en ${porNumero(it.toInt(), "1 ciudad distinta", "${it.toInt()} ciudades distintas")}" })
            val pai = res.paises.mapNotNull { e -> (e.primero ?: Fechas.parse(hoy).toEpochDay().toInt()).let { Mundo.fechaDeDia(it) } }.sorted()
            escalones("paises", "Mundo", "⚐", "países", listOf(2.0 to "Nuevo país", 5.0 to "Pasaporte con sellos", 10.0 to "Diez banderas", 20.0 to "Ciudadano del mundo", 40.0 to "Embajador"),
                pai.size.toDouble(), { alLlegar(pai, it.toInt()) }, { "Visita ${porNumero(it.toInt(), "1 país", "${it.toInt()} países")}" })
            val conts = res.paises.mapNotNull { e -> Mundo.pais(ctx!!, e.clave)?.continente?.let { it to Mundo.fechaDeDia(e.primero ?: Fechas.parse(hoy).toEpochDay().toInt()) } }
                .groupBy { it.first }.mapValues { (_, l) -> l.minOf { it.second } }.values.sorted()
            escalones("continentes", "Mundo", "◍", "continentes", listOf(2.0 to "Dos continentes", 3.0 to "Tres continentes", 5.0 to "Cinco continentes"),
                conts.size.toDouble(), { alLlegar(conts, it.toInt()) }, { "Pisa ${it.toInt()} continentes" })
            val sit = res.sitios.values.sorted().map { Mundo.fechaDeDia(it) }
            escalones("unesco", "Mundo", "♜", "sitios", listOf(1.0 to "Patrimonio de la humanidad", 10.0 to "Diez maravillas", 25.0 to "Guardián del patrimonio", 50.0 to "Medio centenar"),
                sit.size.toDouble(), { alLlegar(sit, it.toInt()) }, { "Visita ${porNumero(it.toInt(), "1 sitio", "${it.toInt()} sitios")} del Patrimonio Mundial" })
            Perfiles.casa()?.let { (la, lo) ->
                val lejos = res.ciudades.mapNotNull { e -> e.primero?.let { Geo.distanciaM(la, lo, e.lat, e.lon) / 1000 to Mundo.fechaDeDia(it) } }
                val max = lejos.maxOfOrNull { it.first } ?: 0.0
                listOf(500.0 to "Lejos de casa", 2000.0 to "Muy lejos", 5000.0 to "Al otro lado", 10000.0 to "Las antípodas").forEachIndexed { i, (km, nom) ->
                    out += Logro("lejos_${km.toInt()}", nom, "Llega a %,d km de tu ciudad".format(km.toInt()), "➶", "Mundo", i + 1, km, max,
                        lejos.filter { it.first >= km }.minOfOrNull { it.second }, unidad = "km")
                }
            }
        }

        // ------------------------------------------------ Cuerpo
        val pasosDia = Repo.salud.values.mapNotNull { s -> s.pasos?.takeIf { it > 0 }?.let { s.fecha to it.toDouble() } }.sortedBy { it.first }
        listOf(10_000.0 to "Diez mil", 20_000.0 to "Veinte mil", 30_000.0 to "Treinta mil", 40_000.0 to "Ultramaratón a pie").forEachIndexed { i, (m, nom) ->
            out += Logro("pasos_dia_${m.toLong()}", nom, "Camina %,d pasos en un día".format(m.toLong()), "❖", "Cuerpo", i + 1, m, pasosDia.maxOfOrNull { it.second } ?: 0.0,
                pasosDia.firstOrNull { it.second >= m }?.first, unidad = "pasos")
        }
        escalones("pasos_total", "Cuerpo", "❖", "pasos", listOf(1e6 to "Un millón de pasos", 5e6 to "Cinco millones", 1e7 to "Diez millones", 2.5e7 to "La vuelta a México"),
            pasosDia.sumOf { it.second }, { acumulado(pasosDia, it) }, { "Acumula %,d pasos".format(it.toLong()) })
        val km = Repo.salud.values.mapNotNull { s -> s.distancia_km?.takeIf { it > 0 }?.let { s.fecha to it } }.sortedBy { it.first }
        if (km.isNotEmpty()) out += Logro("maraton", "Maratonista", "Recorre 42 km en un solo día", "⚑", "Cuerpo", 3, 42.195, km.maxOf { it.second }, km.firstOrNull { it.second >= 42.195 }?.first, unidad = "km")
        val ent = Repo.entrenos.values.filter { it.series > 0 }.map { it.fecha }.sorted()
        escalones("entrenos", "Cuerpo", "▣", "entrenos", listOf(1.0 to "Primer entreno", 10.0 to "Constante", 50.0 to "Medio centenar", 100.0 to "Cien entrenos", 250.0 to "Hierro"),
            ent.size.toDouble(), { alLlegar(ent, it.toInt()) }, { "Completa ${it.toInt()} entrenamientos" })
        val vol = Repo.entrenos.values.filter { it.volumen_kg > 0 }.sortedBy { it.fecha }.map { it.fecha to it.volumen_kg }
        escalones("volumen", "Cuerpo", "▣", "kg", listOf(1e5 to "Cien toneladas", 5e5 to "Quinientas toneladas", 1e6 to "Mil toneladas"), vol.sumOf { it.second },
            { acumulado(vol, it) }, { "Levanta %,d toneladas en total".format((it / 1000).toLong()) })
        val esc = Repo.ascensos.map { it.fecha }.sorted()
        if (esc.isNotEmpty()) escalones("escalada", "Cuerpo", "▲", "sesiones", listOf(1.0 to "Primera pared", 25.0 to "Escalador", 100.0 to "Cien paredes"), esc.size.toDouble(),
            { alLlegar(esc, it.toInt()) }, { "Escala ${porNumero(it.toInt(), "1 sesión", "${it.toInt()} sesiones")}" })
        val madrugadas = Repo.salud.values.filter { s -> s.sueno_fin?.substring(11, 13)?.toIntOrNull()?.let { it < 6 } == true && (s.sueno_min ?: 0) > 180 }.map { it.fecha }.sorted()
        out += Logro("madrugador", "Madrugador", "Despierta antes de las 6:00 treinta veces", "☀", "Cuerpo", 2, 30.0, madrugadas.size.toDouble(), alLlegar(madrugadas, 30), unidad = "días")

        // ------------------------------------------------ Mente
        val reac = Repo.reacciones.sortedBy { it.fecha }
        out += Logro("reaccion_1", "Reflejos", "Haz tu primera prueba de reacción", "⚡", "Mente", 1, 1.0, reac.size.toDouble().coerceAtMost(1.0), reac.firstOrNull()?.fecha)
        escalones("reacciones", "Mente", "⚡", "pruebas", listOf(30.0 to "Un mes de reflejos", 100.0 to "Cien pruebas"), reac.size.toDouble(), { alLlegar(reac.map { it.fecha }, it.toInt()) }, { "Haz ${it.toInt()} pruebas de reacción" })
        val rapida = reac.filter { (it.mediana_ms ?: 9999.0) < 250 }
        // El avance es qué tan cerca estás (250 / tu mejor mediana), sin unidad: antes se mostraba «208 / 250 ms» con una mediana de 300 ms.
        out += Logro("reaccion_rapida", "Rayo", "Termina una prueba con mediana menor a 250 ms", "⚡", "Mente", 3, 1.0, reac.mapNotNull { it.mediana_ms }.minOrNull()?.let { (250.0 / it.coerceAtLeast(1.0)).coerceAtMost(1.0) } ?: 0.0, rapida.firstOrNull()?.fecha)
        val cues = Repo.respuestas.map { it.fecha }.sorted()
        escalones("cuestionarios", "Mente", "?", "cuestionarios", listOf(1.0 to "Autoconocimiento", 12.0 to "Seguimiento serio", 50.0 to "Científico de ti"), cues.size.toDouble(),
            { alLlegar(cues, it.toInt()) }, { "Responde ${it.toInt()} cuestionarios" })
        Repo.config.politica.minByOrNull { it.fecha }.let { t -> out += Logro("brujula", "Brújula", "Haz el test de la brújula política", "✧", "Mente", 1, 1.0, if (t != null) 1.0 else 0.0, t?.fecha) }
        val conocer = !Perfiles.vacio(Repo.config.perfil)
        out += Logro("conocerte", "Conócete", "Llena «Conocerte»", "☉", "Mente", 1, 1.0, if (conocer) 1.0 else 0.0,
            if (conocer) (Repo.config.perfil_historial.minOfOrNull { it.fecha } ?: conDiario.firstOrNull() ?: hoy) else null)

        // ------------------------------------------------ Cultura
        val obras = Repo.obras.filter { it.estado == "terminado" }.mapNotNull { it.fin }.sorted()
        escalones("obras", "Cultura", "❦", "obras", listOf(1.0 to "Primera obra", 10.0 to "Diez obras", 50.0 to "Biblioteca personal", 100.0 to "Cien obras"), obras.size.toDouble(),
            { alLlegar(obras, it.toInt()) }, { "Termina ${porNumero(it.toInt(), "1 libro, película, serie o juego", "${it.toInt()} libros, películas, series o juegos")}" })
        val libros = Repo.obras.filter { it.estado == "terminado" && it.tipo == "libro" }.mapNotNull { it.fin }
        val porAnio = libros.groupBy { it.take(4) }
        val mejorAnio = porAnio.maxByOrNull { it.value.size }
        out += Logro("libros_12", "Un libro al mes", "Termina 12 libros en un mismo año", "❦", "Cultura", 3, 12.0, (mejorAnio?.value?.size ?: 0).toDouble(),
            porAnio.values.filter { it.size >= 12 }.map { it.sorted()[11] }.minOrNull(), unidad = "libros")
        val frases = Repo.config.frases_guardadas.map { it.fecha }.sorted()
        escalones("frases", "Cultura", "♥", "frases", listOf(10.0 to "Coleccionista de frases", 50.0 to "Florilegio"), frases.size.toDouble(), { alLlegar(frases, it.toInt()) }, { "Guarda ${it.toInt()} frases con ♥" })
        val subr = Repo.subrayados.mapNotNull { it.fecha }.sorted()
        if (subr.isNotEmpty()) escalones("subrayados", "Cultura", "▬", "subrayados", listOf(100.0 to "Lápiz en mano", 1000.0 to "Mil subrayados"), subr.size.toDouble(),
            { alLlegar(subr, it.toInt()) }, { "Haz ${porNumero(it.toInt(), "1 subrayado", "${it.toInt()} subrayados")} en Kindle" })

        // ------------------------------------------------ Gente
        val eq = Repo.equivalencias()
        val primeraVez = HashMap<String, String>()
        Repo.dias.values.sortedBy { it.fecha }.forEach { d -> Texto.personas(d.nota).forEach { p -> primeraVez.putIfAbsent(Texto.canon(p, eq), d.fecha) } }
        val gente = primeraVez.values.sorted()
        escalones("personas", "Gente", "☺", "personas", listOf(10.0 to "Tu gente", 50.0 to "Muchas caras", 150.0 to "El número de Dunbar"), gente.size.toDouble(),
            { alLlegar(gente, it.toInt()) }, { "Menciona a ${porNumero(it.toInt(), "1 persona distinta", "${it.toInt()} personas distintas")} en el diario" })

        // ------------------------------------------------ Constancia (la mayoría de los días, sin necesidad de racha)
        fun constante(id: String, nombre: String, desc: String, icono: String, dias: Set<String>, ventana: Int, metas: List<Int>) {
            val (mejor, _) = constancia(dias, ventana, 1)
            metas.forEachIndexed { i, k ->
                out += Logro("${id}_${k}_$ventana", nombre + " " + listOf("I", "II", "III", "IV")[i.coerceAtMost(3)], desc.format(k, ventana), icono, "Constancia", i + 1,
                    k.toDouble(), mejor.toDouble(), constancia(dias, ventana, k).second, unidad = "de ${porNumero(ventana, "1 día", "$ventana días")}")
            }
        }
        constante("c_diario", "Escritura constante", "Escribe %d de %d días", "✎", conDiario.toSet(), 30, listOf(20, 25, 28))
        constante("c_pasos", "Caminante", "8,000 pasos o más %d de %d días", "❖", pasosDia.filter { it.second >= 8000 }.map { it.first }.toSet(), 30, listOf(15, 20, 25))
        constante("c_sueno", "Buen dormir", "7 horas o más %d de %d noches", "☾", Repo.salud.values.filter { (it.sueno_min ?: 0) >= 420 }.map { it.fecha }.toSet(), 30, listOf(15, 20, 25))
        constante("c_gym", "Disciplina", "Entrena %d de %d días", "▣", ent.toSet(), 56, listOf(16, 20, 24))
        constante("c_reac", "Mente afilada", "Haz la prueba de reacción %d de %d días", "⚡", reac.map { it.fecha }.toSet(), 30, listOf(15, 25))
        io.github.ruddy2003.vestigium.data.Privado.habitosVisibles().filter { it.activo }.forEach { h ->
            constante("c_hab_${h.id}", h.nombre, "«${h.nombre}» %d de %d días", "✓", Repo.dias.values.filter { it.habitos[h.id] == true }.map { it.fecha }.toSet(), 30, listOf(15, 20, 25))
        }

        // ------------------------------------------------ Curiosos (algunos, secretos hasta lograrlos)
        Repo.dias.values.filter { it.valencia == 7 }.minByOrNull { it.fecha }.let { d -> out += Logro("dia_perfecto", "Día perfecto", "Califica un día con 7 de 7", "✺", "Curiosos", 2, 1.0, if (d != null) 1.0 else 0.0, d?.fecha, secreto = true) }
        val vals = Repo.dias.values.filter { it.valencia != null }.sortedBy { it.fecha }
        // «En menos de 3 días» de calendario: antes miraba los 3 registros anteriores, aunque fueran de hace un mes.
        val remonta = vals.firstOrNull { d -> d.valencia!! >= 6 && vals.any { it.fecha >= Fechas.sumar(d.fecha, -3) && it.fecha < d.fecha && (it.valencia ?: 9) <= 2 } }?.fecha
        out += Logro("remontada", "Remontada", "De un día muy malo a uno muy bueno en menos de 3 días", "↗", "Curiosos", 3, 1.0, if (remonta != null) 1.0 else 0.0, remonta, secreto = true)
        val noctambulo = Repo.dias.values.filter { d -> d.ts_editado?.let { t -> t.length > 13 && t.substring(11, 13).toIntOrNull()?.let { it in 2..4 } == true } == true }.minByOrNull { it.fecha }
        out += Logro("noctambulo", "Noctámbulo", "Escribe el diario entre las 2 y las 5 de la mañana", "✦", "Curiosos", 1, 1.0, if (noctambulo != null) 1.0 else 0.0, noctambulo?.fecha, secreto = true)
        val pocaPantalla = Repo.pantalla.values.filter { it.total_min in 1..59 }.minByOrNull { it.fecha }
        out += Logro("desconectado", "Desconectado", "Un día con menos de una hora de pantalla", "○", "Curiosos", 2, 1.0, if (pocaPantalla != null) 1.0 else 0.0, pocaPantalla?.fecha)
        val aniv = conDiario.firstOrNull()?.let { p -> Fechas.iso(LocalDate.parse(p).plusYears(1)).takeIf { it <= hoy } }
        out += Logro("aniversario", "Aniversario", "Cumple un año usando el diario", "✶", "Curiosos", 3, 365.0,
            conDiario.firstOrNull()?.let { Fechas.diasEntre(it, hoy).toDouble() } ?: 0.0, aniv, unidad = "días")
        val gastos = Repo.gastos.map { it.dia }.sorted()
        escalones("gastos", "Curiosos", "$", "gastos", listOf(100.0 to "Contador", 1000.0 to "Libro mayor"), gastos.size.toDouble(), { alLlegar(gastos, it.toInt()) }, { "Registra ${porNumero(it.toInt(), "1 gasto", "${it.toInt()} gastos")}" })
        val temas = Repo.config.tema_rotado.isNotBlank()
        out += Logro("camaleon", "Camaleón", "Deja que la app cambie de tema sola", "◑", "Curiosos", 1, 1.0, if (temas) 1.0 else 0.0, Repo.config.tema_rotado.ifBlank { null })

        // ------------------------------------------------ Secretos: aparecen cuando los logras
        fun secreto(id: String, nombre: String, desc: String, icono: String, nivel: Int, meta: Double, progreso: Double, cuando: String?, unidad: String = "") {
            out += Logro(id, nombre, desc, icono, "Curiosos", nivel, meta, progreso.coerceAtMost(meta), cuando, secreto = true, unidad = unidad)
        }
        Repo.dias.values.filter { it.sueno_lucidez == "total" }.minByOrNull { it.fecha }.let { d ->
            secreto("lucido_total", "Lúcido de verdad", "Anota un sueño en el que supiste por completo que soñabas", "◑", 4, 1.0, if (d != null) 1.0 else 0.0, d?.fecha)
        }
        val animo = Repo.momentos.filter { it.esAnimo }.sortedBy { it.ts }
        secreto("cien_animo", "Cien estados de ánimo", "Registra tu ánimo 100 veces", "◔", 2, 100.0, animo.size.toDouble(), animo.getOrNull(99)?.fecha, "registros")
        // Un hito en cada mes de un mismo año.
        val hitosPorAnio = Repo.dias.values.filter { it.hitos.isNotEmpty() }.groupBy { it.fecha.take(4) }
        val anioMemorable = hitosPorAnio.entries.mapNotNull { (_, l) ->
            val primeroPorMes = l.groupBy { it.fecha.substring(5, 7) }.mapValues { (_, x) -> x.minOf { it.fecha } }
            if (primeroPorMes.size == 12) primeroPorMes.values.max() else null
        }.minOrNull()
        secreto("anio_memorable", "Un año memorable", "Anota al menos un hito en cada mes de un mismo año", "✶", 3, 12.0,
            hitosPorAnio.values.maxOfOrNull { l -> l.map { it.fecha.substring(5, 7) }.toSet().size }?.toDouble() ?: 0.0, anioMemorable, "meses")
        // Una carta abierta al menos un año después de escribirla.
        val cartaVieja = Repo.cartas.filter { it.alFuturo && it.abierta }.mapNotNull { c ->
            val ab = c.abierta_el?.take(10) ?: c.abrir_en
            if (runCatching { Fechas.diasEntre(c.escrita, ab) }.getOrDefault(0) >= 365) ab else null
        }.minOrNull()
        secreto("carta_un_anio", "Correo del pasado", "Abre una carta que te escribiste hace un año o más", "✉", 3, 1.0, if (cartaVieja != null) 1.0 else 0.0, cartaVieja)
        // El mismo día del calendario escrito en tres años distintos.
        val porDiaDelAnio = conDiario.groupBy { it.substring(5) }
        val tresAnios = porDiaDelAnio.values.filter { it.size >= 3 }.mapNotNull { it.sorted().getOrNull(2) }.minOrNull()
        secreto("tres_anios", "Tres veces el mismo día", "Escribe el diario el mismo día del calendario en tres años distintos", "❂", 4, 3.0,
            porDiaDelAnio.values.maxOfOrNull { it.size }?.toDouble() ?: 0.0, tresAnios, "años")
        // Diez sentimientos distintos en una misma semana.
        val emo = Repo.momentos.filter { it.esAnimo }.flatMap { m -> io.github.ruddy2003.vestigium.data.Catalogo.emocionesDe(m).filter { it != "no_se" && it != "otro" }.map { m.fecha to it } }.sortedBy { it.first }
        var paleta: String? = null; var mejorPaleta = 0
        for (f in emo.map { it.first }.distinct()) {
            val desde = Fechas.sumar(f, -6)
            val n = emo.filter { it.first in desde..f }.map { it.second }.toSet().size
            if (n > mejorPaleta) mejorPaleta = n
            if (paleta == null && n >= 10) paleta = f
        }
        secreto("paleta", "Paleta de sentimientos", "Nombra diez sentimientos distintos en una misma semana", "❋", 2, 10.0, mejorPaleta.toDouble(), paleta, "sentimientos")
        // Una campaña ganada los siete días.
        val perfecta = Repo.config.campanas.mapNotNull { c -> runCatching { Campanas.estado(c, hoy) }.getOrNull()?.takeIf { e -> e.dias.all { it.second == true } }?.let { Fechas.sumar(c.lunes, 6) } }.minOrNull()
        secreto("siete_de_siete", "Siete de siete", "Cumple el reto de una campaña los siete días de la semana", "⚑", 3, 1.0, if (perfecta != null) 1.0 else 0.0, perfecta)
        // Muchos frentes: diez #temas distintos en 30 días.
        val temasFecha = Repo.dias.values.flatMap { d -> Texto.temas(d.nota).map { Texto.canon(it, eq) to d.fecha } }.sortedBy { it.second }
        var frentes: String? = null; var mejorFrentes = 0
        for (f in temasFecha.map { it.second }.distinct()) {
            val n = temasFecha.filter { it.second in Fechas.sumar(f, -29)..f }.map { it.first }.toSet().size
            if (n > mejorFrentes) mejorFrentes = n
            if (frentes == null && n >= 10) frentes = f
        }
        secreto("muchos_frentes", "Muchos frentes", "Escribe sobre diez #temas distintos en treinta días", "◇", 2, 10.0, mejorFrentes.toDouble(), frentes, "temas")
        // Semana completa: siete días seguidos con diario, ánimo, sueño y al menos un registro al momento.
        val completos = Repo.dias.values.filter { d -> d.nota.isNotBlank() && d.valencia != null && d.sueno_calidad != null && animo.any { it.fecha == d.fecha } }.map { it.fecha }.sorted()
        val (rachaCompleta, cuandoCompleta) = racha(completos, 7)
        secreto("semana_completa", "Semana completa", "Siete días seguidos con diario, ánimo, sueño y un registro al momento", "▣", 3, 7.0, rachaCompleta.toDouble(), cuandoCompleta, "días")
        val gratitud = animo.filter { m -> "gratitud" in io.github.ruddy2003.vestigium.data.Catalogo.emocionesDe(m) }
        secreto("agradecido", "Agradecido", "Registra gratitud 30 veces", "♡", 2, 30.0, gratitud.size.toDouble(), gratitud.getOrNull(29)?.fecha, "veces")
        // Alba: un registro de ánimo entre las 5 y las 6 de la mañana.
        val alba = animo.firstOrNull { m -> m.ts.length > 13 && m.ts.substring(11, 13) == "05" }
        secreto("alba", "Alba", "Registra tu ánimo entre las 5 y las 6 de la mañana", "☀", 1, 1.0, if (alba != null) 1.0 else 0.0, alba?.fecha)
        // Desahogo: una entrada de mil palabras o más.
        val larga = Repo.dias.values.filter { Texto.palabras(it.nota) >= 1000 }.minByOrNull { it.fecha }
        secreto("desahogo", "Desahogo", "Escribe una entrada de mil palabras o más", "❡", 2, 1000.0,
            Repo.dias.values.maxOfOrNull { Texto.palabras(it.nota) }?.toDouble() ?: 0.0, larga?.fecha, "palabras")

        // ------------------------------------------------ Enfoque y campañas
        val enfoque = Repo.tramos.filter { it.tipo != "siesta" }.groupBy { it.fecha }.mapValues { (_, l) -> l.sumOf { it.min } / 60.0 }.entries.sortedBy { it.key }.map { it.key to it.value }
        if (enfoque.isNotEmpty()) escalones("enfoque", "Mente", "◷", "horas", listOf(10.0 to "Diez horas de enfoque", 50.0 to "Cincuenta horas", 100.0 to "Cien horas", 500.0 to "Quinientas horas", 1000.0 to "Mil horas"),
            enfoque.sumOf { it.second }, { acumulado(enfoque, it) }, { "Acumula ${it.toInt()} horas de enfoque con el cronómetro" })
        val victorias = runCatching { Campanas.victorias(hoy) }.getOrDefault(emptyList())
        if (Repo.config.campanas.isNotEmpty()) escalones("campanas", "Constancia", "⚑", "campañas", listOf(1.0 to "Primera campaña", 5.0 to "Veterano de campaña", 20.0 to "Conquistador", 52.0 to "Un año de campañas"),
            victorias.size.toDouble(), { alLlegar(victorias, it.toInt()) }, { "Gana ${it.toInt()} campañas semanales" })

        // ------------------------------------------------ Los tuyos
        // Un logro sobre algo privado (el hábito ligado a las recaídas o una variable privada) no se muestra.
        val ligado = io.github.ruddy2003.vestigium.data.Privado.habito()?.id
        Repo.config.logros_propios.filter { p ->
            if (p.fuente == "habito") p.clave != ligado else p.fuente == "etiqueta" || !io.github.ruddy2003.vestigium.data.Privado.esVariable(p.clave, ligado)
        }.forEach { p -> out += propio(p) }
        return out
    }

    /** Días con un valor (y su valor) según la fuente de un logro propio. */
    fun serie(fuente: String, clave: String): List<Pair<String, Double>> = when (fuente) {
        "habito" -> Repo.dias.values.filter { it.habitos[clave] == true }.map { it.fecha to 1.0 }
        "etiqueta" -> {
            val eq = Repo.equivalencias(); val k = clave.removePrefix("@").removePrefix("#").lowercase()
            val esPersona = clave.startsWith("@")
            Repo.dias.values.filter { d -> (if (esPersona) Texto.personas(d.nota) else Texto.temas(d.nota)).any { Texto.canon(it, eq) == Texto.canon(k, eq) } }.map { it.fecha to 1.0 }
        }
        else -> runCatching { Estadistica.panel(dinamicas = clave.contains('_') && Dinamicas.prefijo(clave) != null) }.getOrDefault(emptyMap())
            .mapNotNull { (f, m) -> m[clave]?.let { f to it } }
    }.sortedBy { it.first }

    fun propio(p: LogroPropio): Logro {
        val s = serie(p.fuente, p.clave)
        // «Como mucho» (esquema 17): cuentan los días que quedan en el umbral o por debajo; si no, los que lo pasan.
        val utiles = if (p.umbral_max && p.fuente == "variable") s.filter { it.second <= p.umbral } else s.filter { it.second > p.umbral || (p.umbral == 0.0 && it.second > 0) }
        return when (p.modo) {
            "total" -> Logro("p_${p.id}", p.nombre, "Suma %s".format(fmt(p.meta)), p.icono, "Tuyos", 2, p.meta, s.sumOf { it.second }, acumulado(s, p.meta), propio = true)
            "racha" -> { val (m, c) = racha(utiles.map { it.first }, p.meta.toInt()); Logro("p_${p.id}", p.nombre, "${porNumero(p.meta.toInt(), "1 día seguido", "${p.meta.toInt()} días seguidos")}", p.icono, "Tuyos", 2, p.meta, m.toDouble(), c, propio = true, unidad = "días") }
            else -> if (p.ventana > 0) {
                val (m, c) = constancia(utiles.map { it.first }.toSet(), p.ventana, p.meta.toInt())
                Logro("p_${p.id}", p.nombre, "${p.meta.toInt()} de ${porNumero(p.ventana, "1 día", "${p.ventana} días")}", p.icono, "Tuyos", 2, p.meta, m.toDouble(), c, propio = true, unidad = "de ${porNumero(p.ventana, "1 día", "${p.ventana} días")}")
            } else Logro("p_${p.id}", p.nombre, "${porNumero(p.meta.toInt(), "1 día", "${p.meta.toInt()} días")}", p.icono, "Tuyos", 2, p.meta, utiles.size.toDouble(), alLlegar(utiles.map { it.first }, p.meta.toInt()), propio = true, unidad = "días")
        }
    }

    private fun fmt(x: Double) = if (x == Math.floor(x)) "%,d".format(x.toLong()) else "%.1f".format(x)

    /**
     * Cómo se consiguió: cuánto tardó desde el primer registro, si fue un día de diario y qué escribiste esa noche,
     * y cómo estuvo tu ánimo ese día. Para el detalle del trofeo y el hall.
     */
    fun comoSeConsiguio(l: Logro): String? {
        val f = l.conseguido ?: return null
        val partes = mutableListOf<String>()
        val primero = Repo.dias.keys.minOrNull()
        if (primero != null && primero <= f) runCatching {
            val n = java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(primero), Fechas.parse(f)) + 1
            partes += if (n <= 1) "Llegó en tu primer día de registro" else "Llegó en tu día $n de registro"
        }
        val d = Repo.dias[f]
        d?.valencia?.let { v -> partes += "ese día tu ánimo estuvo en $v de 7" }
        val nota = d?.nota?.trim()?.takeIf { it.isNotEmpty() && !l.secreto }
        val txt = partes.joinToString(", ").replaceFirstChar { it.uppercase() }
        val cita = nota?.let { n -> val pal = n.split(Regex("\\s+")); pal.take(22).joinToString(" ") + if (pal.size > 22) "…" else "" }
        return when {
            txt.isEmpty() && cita == null -> null
            cita == null -> "$txt."
            txt.isEmpty() -> "Esa noche escribiste: «$cita»"
            else -> "$txt. Esa noche escribiste: «$cita»"
        }
    }

    /** Los conseguidos que todavía no habías visto (para festejarlos), y marcarlos como vistos. */
    fun nuevos(ctx: Context?): List<Logro> {
        val vistos = Repo.config.logros_vistos
        return todos(ctx).filter { it.hecho && it.id !in vistos }
    }
    fun marcarVistos(l: List<Logro>) {
        if (l.isEmpty()) return
        val hoy = Fechas.diaLogico()
        Repo.cambiarConfig { c -> c.copy(logros_vistos = c.logros_vistos + l.associate { it.id to hoy }) }
    }
}
