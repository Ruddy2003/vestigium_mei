package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.privado
import android.content.Context
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Perfiles
import io.github.ruddy2003.vestigium.data.Politica
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * «Tu año»: todo lo de un año, ya contado y listo para las historias. Usa lo mismo que los reportes ([Periodo]) y le suma
 * lo divertido: equivalencias, lo más común, lo único, el día más raro, la palabra del año, tu arquetipo y tus predicciones.
 * Nunca toca el seguimiento privado ni el contenido de las cartas selladas.
 */
object TuAnio {
    class Datos(
        val anio: Int, val faltan: Int, val p: Periodo.Datos, val previo: Periodo.Datos?,
        val paginas: Int, val horasLeerlo: Double,
        val distancia: List<String>, val suenoDias: Double, val pantallaDias: Double, val elefantes: Double, val cancionesDias: Double,
        val comun: List<Pair<String, String>>, val unaVez: List<String>, val raro: Pair<String, List<String>>?,
        val records: List<Pair<String, String>>, val relacion: String?,
        val vida: OtrasVidas.Vida?, val brujula: List<String>,
        val palabra: String?, val palabraVeces: Int, val palabrasCasi: List<String>,
        val arquetipo: String, val arquetipoPor: String,
        val medallas: List<Logros.Logro>, val predicciones: List<Triple<String, String, String>>,
        val paisesAnio: Set<String>, val mesMejor: Int?, val mesesOrden: List<Int>,
        val dirigidas: Int,
        /** El primer día con diario (nota o valencia) dentro del periodo; null si no hay ninguno. */
        val primerDiario: String? = null,
        /** Días del periodo desde [primerDiario] (incluido): el «de N» honesto cuando el diario empezó a mitad del año. */
        val diasDesdePrimero: Int = 0,
        /** El día de la semana (1 = lunes) con mejor ánimo medio, solo entre los que tienen [MIN_DIAS_BARRA] días o más; null si no hay dos. */
        val mejorDiaSemana: Int? = null,
        /** Cuántos días comparten la valencia más alta del periodo (1 = sin empate). */
        val empateFeliz: Int = 0,
    ) {
        val completo: Boolean get() = faltan <= 0
    }

    /** Las preguntas de enero: clave, pregunta y cómo se mide al final. */
    val PREGUNTAS = listOf(
        "obras" to "¿Cuántos libros, películas, series o juegos terminarás?",
        "paises" to "¿Cuántos países nuevos pisarás?",
        "ciudades" to "¿Cuántas ciudades nuevas?",
        "pasos" to "¿Cuántos pasos caminarás al día?",
        "sueno" to "¿Cuántas horas dormirás por noche?",
        "animo" to "¿Cuál será tu ánimo medio (1 a 7)?",
        "persona" to "¿A quién mencionarás más en el diario?",
        "palabras" to "¿Cuántas palabras escribirás en el diario?",
    )

    private fun n0(x: Double) = "%,.0f".format(x)
    private fun n1(x: Double) = "%.1f".format(x)
    private fun hora(h: Double) = "%02d:%02d".format(((h % 24) + 24).toInt() % 24, ((h - h.toInt()) * 60).roundToInt().coerceIn(0, 59))

    /** Lo que mediste ese año, como en «Otras vidas», para ver a quién te pareciste. */
    private fun tuyo(p: Periodo.Datos): Map<String, Double> {
        val m = HashMap<String, Double>()
        p.pasos?.let { m["pasos"] = it }; p.kcal?.let { m["kcal"] = it }; p.suenoH?.let { m["sueno"] = it }; p.dormirH?.let { m["dormir"] = it }
        p.pantallaMin?.let { m["pantalla"] = it / 60 }
        val dn = p.fechas.count { Repo.notifs[it] != null }
        if (dn > 0) m["notificaciones"] = p.notifs.toDouble() / dn
        // Por día con dato de la fuente (la regla P-15), no por día de calendario: con Spotify importado desde julio, las
        // horas de música de medio año se dividían entre el año entero y salía la mitad.
        if (p.musicaH > 0 && p.fechas.isNotEmpty()) {
            val desde = runCatching { Repo.escuchas(p.fechas.first(), p.fechas.last()).minOfOrNull { it.fecha } }.getOrNull()
            val dias = p.fechas.count { desde != null && it >= desde }.coerceAtLeast(1)
            m["musica"] = p.musicaH / dias
        }
        if (p.km > 0) {
            val dias = p.fechas.count { Repo.salud[it]?.let { s -> s.distancia_km != null || s.pasos != null } == true }.coerceAtLeast(1)
            m["km_anio"] = p.km * 365.0 / dias
        }
        val nut = p.fechas.mapNotNull { Repo.nutricion[it] }.filter { (it.valores["kcal"] ?: 0.0) > 300 }
        fun prom(k: String) = nut.mapNotNull { it.valores[k] }.takeIf { it.isNotEmpty() }?.average()
        prom("fibra_g")?.let { m["fibra"] = it }; prom("alcohol_g")?.let { m["alcohol"] = it }; prom("azucares_g")?.let { m["azucar"] = it }
        return m
    }

    /** Noches (o días) mínimos para decir «dormías…» y «caminabas…»: con menos, la frase se calla. */
    const val MIN_NOCHES = 14
    /** Días mínimos con valencia en un día de la semana para que compita por «tu mejor día». */
    const val MIN_DIAS_BARRA = 5

    /** La mediana: el valor del medio (con un número par, la media de los dos del medio). Null sin datos. */
    private fun mediana(l: List<Double>): Double? {
        if (l.isEmpty()) return null
        val o = l.sorted(); val m = o.size / 2
        return if (o.size % 2 == 1) o[m] else (o[m - 1] + o[m]) / 2
    }

    private fun moda(l: List<Double>, paso: Double): Double? =
        l.groupingBy { Math.round(it / paso) * paso }.eachCount().maxByOrNull { it.value }?.key

    /** Hora decimal; la madrugada suma 24 para que dormir a la 1 sea «tarde» y no «temprano». */
    private fun horaDec(iso: String?, noche: Boolean): Double? = runCatching {
        val x = iso!!.substring(11, 13).toInt() + iso.substring(14, 16).toInt() / 60.0
        if (noche && x < 15) x + 24 else x
    }.getOrNull()

    fun calcular(ctx: Context, anio: Int): Datos {
        val hoy = Fechas.diaLogico()
        val ini = "$anio-01-01"; val finAnio = "$anio-12-31"
        val fin = minOf(finAnio, hoy)
        val faltan = if (hoy >= finAnio) 0 else Fechas.diasEntre(hoy, finAnio).toInt()
        val p = Periodo.calcular(ctx, ini, fin)
        val pIni = "${anio - 1}-01-01"
        val previo = if (Repo.dias.keys.any { it in pIni..Fechas.sumar(ini, -1) } || Repo.salud.keys.any { it in pIni..Fechas.sumar(ini, -1) })
            // Un año antes con el calendario (el 29 de febrero pasa al 28): armar «AAAA-02-29» de un año no bisiesto fallaba.
            runCatching { Periodo.calcular(ctx, pIni, Fechas.iso(Fechas.parse(fin).minusYears(1))) }.getOrNull() else null
        val en = p.fechas.toHashSet()
        val perfil = Repo.config.perfil
        val f = Perfiles.sexo() == "mujer"

        // ---- equivalencias absurdas pero exactas
        val paginas = (p.palabras / 300.0).roundToInt()
        val dist = buildList {
            if (p.km >= 42.195) add("${n1(p.km / 42.195)} maratones")
            if (p.km >= 100) add("el ${n1(p.km / 40_075 * 100)}% de la vuelta a la Tierra por el ecuador")
            if (p.km >= 8.849) add("${porNumero(p.km / 8.849, "1 vez", "${n0(p.km / 8.849)} veces")} la altura del Everest, pero acostado")
        }

        // ---- lo más común: un día típico armado con lo que más se repitió
        val sal = p.fechas.mapNotNull { Repo.salud[it] }.filter { (it.sueno_min ?: 0) > 120 }
        val comun = buildList {
            // La mediana de las horas (no la caja de 15 minutos más repetida), y solo con MIN_NOCHES noches o más.
            val conHoras = sal.mapNotNull { s -> val a = horaDec(s.sueno_inicio, true); val b = horaDec(s.sueno_fin, false); if (a != null && b != null) a to b else null }
            if (conHoras.size >= MIN_NOCHES) {
                val dormir = mediana(conHoras.map { it.first }); val despertar = mediana(conHoras.map { it.second })
                if (dormir != null && despertar != null) add("dormías" to "hacia las ${hora(dormir)} y despertabas hacia las ${hora(despertar)} (mediana de ${conHoras.size} noches)")
            }
            moda(p.valencia.filterNotNull(), 1.0)?.let { add("tu ánimo" to "${it.toInt()} de 7") }
            val pasosDias = p.fechas.mapNotNull { Repo.salud[it]?.pasos?.takeIf { x -> x > 0 }?.toDouble() }
            if (pasosDias.size >= MIN_NOCHES) mediana(pasosDias)?.let { add("caminabas" to "unos ${n0(Math.round(it / 100.0) * 100.0)} pasos (mediana de ${pasosDias.size} días)") }
            p.ciudades.firstOrNull()?.let { add("estabas en" to "${Mundo.bandera(it.second)} ${it.first}") }
            p.apps.firstOrNull()?.let { add("abrías" to it.first) }
            p.comidaTop.firstOrNull()?.let { add("comías" to it.first.lowercase()) }
            p.artistas.firstOrNull()?.let { add("escuchabas" to it.first) }
            p.momentos.firstOrNull()?.let { add("anotabas" to it.first.lowercase()) }
            // Lo que dicen los registros de ánimo: la emoción que más nombraste y en lo que más pensaste (no en modo discreto).
            if (!Repo.config.discreto) {
                p.emociones.firstOrNull()?.let { add("sentías" to it.nombre.lowercase()) }
                p.pensamientos.firstOrNull()?.let { add("pensabas" to it.first.lowercase()) }
            }
            p.personas.firstOrNull()?.let { add("veías a" to "@${it.first}") }
            val diasDiario = p.fechas.filter { Repo.dias[it]?.nota?.isNotBlank() == true }
            if (diasDiario.size >= 10) add("escribías" to "unas ${n0(diasDiario.map { Texto.palabras(Repo.dias[it]!!.nota).toDouble() }.sorted()[diasDiario.size / 2])} palabras")
        }

        // ---- lo que pasó una sola vez
        val eq = Repo.equivalencias()
        val unaVez = buildList {
            // Las fechas señaladas (con los hitos de antes) que pasaron una sola vez, como en todas las vistas: «✈ Viaje a la costa».
            val senaladas = p.fechas.flatMap { io.github.ruddy2003.vestigium.data.Senaladas.etiquetas(Repo.dias[it]) }.groupingBy { it }.eachCount().filter { it.value == 1 }.keys
            senaladas.take(3).forEach { add(it) }
            val mundo = runCatching { Mundo.diario(ctx) }.getOrNull()
            val ciudadesDias = p.fechas.flatMap { fe -> mundo?.get(Fechas.parse(fe).toEpochDay().toInt())?.ciudades?.map { it.first.nombre to fe }.orEmpty() }
                .groupBy { it.first }.filter { it.value.map { x -> x.second }.distinct().size == 1 }.keys
            ciudadesDias.take(3).forEach { add("estuviste en $it") }
            val mom = Repo.momentos.filter { it.fecha in en && it.tipo != "muestreo" && !it.privado }.groupingBy { it.tipo }.eachCount().filter { it.value == 1 }.keys
            mom.take(2).forEach { add("registraste: " + io.github.ruddy2003.vestigium.data.Catalogo.nombreTipo(it).lowercase()) }
            val gente = p.fechas.mapNotNull { Repo.dias[it] }.flatMap { d -> Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct() }.groupingBy { it }.eachCount().filter { it.value == 1 }.keys
            gente.take(2).forEach { add("mencionaste a @$it") }
        }.take(8)

        // ---- el día más raro y los récords
        // «El día más raro» solo se busca desde el primer día con diario del periodo: lo importado de antes de usar la app
        // (pasos, ubicación, música) no compite. La referencia de «lo normal» sigue siendo el panel entero del periodo.
        val primerDiario = p.fechas.firstOrNull { Periodo.conDiario(it) }
        val panel = runCatching { Estadistica.panel(dinamicas = false) }.getOrDefault(emptyMap())
        val vars = runCatching { Estadistica.variables(dinamicas = false) }.getOrDefault(emptyList())
        val panelAnio = panel.filterKeys { it in en }
        val raro = runCatching { Analisis.atipicos(panelAnio, vars, 2.8, desde = null) }.getOrDefault(emptyList()).filter { it.fecha in en && primerDiario != null && it.fecha >= primerDiario }.maxByOrNull { it.z * it.razones.size }
            ?.let { it.fecha to it.razones.take(3) }
        val records = buildList {
            p.fechas.mapNotNull { fe -> Repo.salud[fe]?.pasos?.let { fe to it } }.maxByOrNull { it.second }?.let { add("más pasos" to "${n0(it.second.toDouble())} el ${Fechas.titulo(it.first).lowercase()}") }
            p.fechas.mapNotNull { fe -> Repo.dias[fe]?.let { fe to Texto.palabras(it.nota) } }.maxByOrNull { it.second }?.takeIf { it.second > 0 }?.let { add("más palabras" to "${n0(it.second.toDouble())} el ${Fechas.titulo(it.first).lowercase()}") }
            Repo.gastos.filter { it.dia in en }.groupBy { it.dia }.mapValues { (_, l) -> l.sumOf { g -> g.monto } }.maxByOrNull { it.value }?.let { add("más gasto" to "$${n0(it.value)} el ${Fechas.titulo(it.key).lowercase()}") }
            if (p.rachaDiario >= 3) add("racha de diario" to "${porNumero(p.rachaDiario, "1 día seguido", "${p.rachaDiario} días seguidos")}")
            p.fechas.mapNotNull { fe -> Repo.pantalla[fe]?.let { fe to it.total_min } }.filter { it.second > 0 }.minByOrNull { it.second }?.let { add("menos pantalla" to "${it.second / 60} h ${it.second % 60} min el ${Fechas.titulo(it.first).lowercase()}") }
        }
        // La relación más fuerte que se encontró (no obvia y con poca probabilidad de azar).
        val relacion = runCatching {
            if (panelAnio.size < 40) null else Estadistica.relaciones(panelAnio, vars, true).lista.filter { !it.obvia && it.q < 0.05 && it.n >= 30 }
                .maxByOrNull { abs(it.r) }?.let { c -> "${c.a.nombre} y ${c.b.nombre.lowercase()}: " + if (c.r > 0) "cuando una sube, la otra también" else "cuando una sube, la otra baja" }
        }.getOrNull()

        // ---- comparaciones
        val t = tuyo(p)
        val vida = runCatching { OtrasVidas.visibles(Repo.config.tema).mapNotNull { v -> OtrasVidas.distancia(v, t)?.let { v to it.first } }.minByOrNull { it.second }?.first }.getOrNull()
        val tests = Repo.config.politica.sortedBy { it.fecha }
        val esteTest = tests.lastOrNull { it.fecha.startsWith("$anio") }
        val antes = esteTest?.let { e -> tests.lastOrNull { it.fecha < e.fecha } }
        val brujula = if (esteTest != null && antes != null) Politica.EJES.mapNotNull { e ->
            val a = antes.ejes[e.id] ?: return@mapNotNull null; val b = esteTest.ejes[e.id] ?: return@mapNotNull null
            val d = b - a
            if (abs(d) < 8) null else "${e.nombre}: ${if (d > 0) "hacia ${e.mas}" else "hacia ${e.menos}"} (${if (d > 0) "+" else "−"}${abs(d).roundToInt()})"
        } else emptyList()

        // ---- la palabra del año: la que más usaste este año en comparación con los demás
        fun palabras(d: Collection<String>): Map<String, Int> {
            val m = HashMap<String, Int>()
            d.forEach { fe -> Repo.dias[fe]?.nota?.let { nota -> Regex("[\\p{L}]{4,}").findAll(nota.lowercase()).map { it.value }.filter { Analisis.normal(it) !in Analisis.VACIAS_N }.forEach { w -> m.merge(w, 1, Int::plus) } } }
            return m
        }
        val esta = palabras(p.fechas)
        val otras = palabras(Repo.dias.keys.filter { it !in en })
        val tE = esta.values.sum().coerceAtLeast(1); val tO = otras.values.sum().coerceAtLeast(1)
        val ranking = esta.filter { it.value >= 4 && !it.key.startsWith("@") }.map { (w, k) ->
            w to (k.toDouble() / tE) / ((otras[w] ?: 0) + 1.0).div(tO) * kotlin.math.ln(1.0 + k)
        }.sortedByDescending { it.second }
        val palabra = ranking.firstOrNull()?.first

        // ---- arquetipo: lo que más te define, con el horario como adjetivo
        val dias = p.n.toDouble()
        val rasgos = listOf(
            Triple("cronista", p.palabras / dias / 250.0, if (f) "Cronista" to "escribiste mucho" else "Cronista" to "escribiste mucho"),
            Triple("caminante", (p.pasos ?: 0.0) / 11_000.0, "Caminante" to "caminaste como pocos"),
            Triple("viajero", (p.ciudades.size / 12.0) + p.paisesNuevos.size / 3.0, (if (f) "Viajera" else "Viajero") to "estuviste en muchos lugares"),
            Triple("lector", p.obras.count { it.tipo == "libro" } / 12.0, (if (f) "Lectora" else "Lector") to "leíste de verdad"),
            Triple("cinefilo", p.obras.count { it.tipo == "pelicula" || it.tipo == "serie" } / 25.0, (if (f) "Cinéfila" else "Cinéfilo") to "viste muchas historias"),
            Triple("melomano", p.musicaH / dias / 1.5, (if (f) "Melómana" else "Melómano") to "la música no paró"),
            Triple("atleta", p.entrenos / 120.0, "Atleta" to "entrenaste casi sin faltar"),
            Triple("sonador", p.suenosRecordados / 60.0, (if (f) "Soñadora" else "Soñador") to "recordaste muchos sueños"),
            Triple("naturalista", p.fotos / 2500.0 + p.sitios.size / 4.0, "Naturalista" to "observaste y fotografiaste el mundo"),
        ).maxByOrNull { it.second }!!
        val adjetivo = when {
            (p.dormirH ?: 0.0) >= 24.5 -> if (f) "nocturna" else "nocturno"
            (p.despertarH ?: 9.0) <= 6.5 -> if (f) "madrugadora" else "madrugador"
            (p.habitosPct ?: 0.0) >= 0.7 -> "constante"
            (p.valMedia ?: 0.0) >= 5.3 -> "luminoso".let { if (f) "luminosa" else it }
            p.diasLluvia > 90 -> if (f) "de lluvia" else "de lluvia"
            else -> if (f) "serena" else "sereno"
        }
        val arquetipo = "${rasgos.third.first} $adjetivo"

        // ---- medallas y predicciones
        val medallas = runCatching { Logros.todos(ctx).filter { it.conseguido?.startsWith("$anio") == true }.sortedByDescending { it.nivel } }.getOrDefault(emptyList())
        val pr = Repo.config.predicciones_anio["$anio"]
        val predicciones = if (pr == null) emptyList() else PREGUNTAS.mapNotNull { (k, q) ->
            val dijo = pr.respuestas[k]?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val fue = when (k) {
                "obras" -> "${p.obras.size}"; "paises" -> "${p.paisesNuevos.size}"; "ciudades" -> "${p.ciudadesNuevas.size}"
                "pasos" -> p.pasos?.let { n0(it) } ?: "—"; "sueno" -> p.suenoH?.let { n1(it) } ?: "—"; "animo" -> p.valMedia?.let { n1(it) } ?: "—"
                "persona" -> p.personas.firstOrNull()?.let { "@${it.first}" } ?: "—"; "palabras" -> n0(p.palabras.toDouble())
                else -> "—"
            }
            Triple(q, dijo, fue)
        }
        val paisesAnio = p.paises.toSet()
        // «Tu mejor día de la semana»: compiten solo los días de la semana con MIN_DIAS_BARRA días con valencia o más, y hacen
        // falta al menos dos; si no, se calla. El día más feliz: cuántos días comparten la valencia más alta.
        val porDiaSemana = p.fechas.zip(p.valencia).mapNotNull { (fe, v) -> v?.let { Fechas.parse(fe).dayOfWeek.value to it } }.groupBy({ it.first }, { it.second })
        val barras = porDiaSemana.filter { it.value.size >= MIN_DIAS_BARRA }
        val mejorDiaSemana = if (barras.size < 2) null else barras.maxByOrNull { it.value.average() }?.key
        val maxValencia = p.valencia.filterNotNull().maxOrNull()
        val empateFeliz = if (maxValencia == null) 0 else p.valencia.count { it == maxValencia }
        val mesesOrden = p.valPorMes.entries.sortedByDescending { it.value }.map { it.key }

        return Datos(anio, faltan, p, previo, paginas, p.palabras / 150.0 / 60.0, dist,
            p.suenoTotalH / 24.0, p.pantallaTotalMin / 60.0 / 24.0, p.volumenKg / 6000.0, p.canciones * 3.5 / 60 / 24,
            comun, unaVez, raro, records, relacion, vida, brujula, palabra, palabra?.let { esta[it] } ?: 0, ranking.drop(1).take(4).map { it.first },
            arquetipo, rasgos.third.second, medallas, predicciones, paisesAnio, mesesOrden.firstOrNull(), mesesOrden,
            Repo.cartas.count { it.escrita.startsWith("$anio") && it.para.isNotBlank() },
            primerDiario = primerDiario, diasDesdePrimero = p.fechas.count { primerDiario != null && it >= primerDiario },
            mejorDiaSemana = mejorDiaSemana, empateFeliz = empateFeliz)
    }

    /** ¿Ya escribiste la carta que se abre el próximo 31 de diciembre? */
    fun cartaLista(anio: Int): Boolean = Repo.cartas.any { it.abrir_en == "${anio + 1}-12-31" }

    fun fechaCarta(anio: Int): String = "${anio + 1}-12-31"

    @Suppress("unused") private fun primeroDe(anio: Int) = LocalDate.of(anio, 1, 1)
}
