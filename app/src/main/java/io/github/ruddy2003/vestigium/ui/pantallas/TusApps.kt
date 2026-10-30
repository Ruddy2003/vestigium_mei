package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.barraGrafica
import io.github.ruddy2003.vestigium.ui.ejeGrafica
import io.github.ruddy2003.vestigium.ui.estiloCifras
import io.github.ruddy2003.vestigium.ui.estiloGrafica
import io.github.ruddy2003.vestigium.ui.lineaGrafica
import io.github.ruddy2003.vestigium.ui.puntoGrafica
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Tus apps en números: lo que importaste de YouTube, Spotify, MacroFactor, el gimnasio, el Kindle, WhatsApp y la búsqueda de Google,
 * visto como un resumen anual de esos que hacen las apps, pero de todo junto y de los años que quieras.
 */
private val PESTANAS = listOf("youtube" to "YouTube", "musica" to "Música", "comida" to "Comida", "gimnasio" to "Gimnasio", "kindle" to "Kindle", "whatsapp" to "WhatsApp", "google" to "Google")
private val PERIODOS = listOf("todo" to "todo", "anio" to "último año", "mes" to "30 días")

/** Lo que se dibuja: cifras grandes, gráficas y listas. Se calcula fuera de la pantalla y aquí solo se pinta. */
private sealed class Bloque {
    class Cifras(val l: List<Pair<String, String>>) : Bloque()
    class Meses(val titulo: String, val l: List<Pair<String, Double>>, val unidad: String) : Bloque()
    class Horas(val titulo: String, val h: DoubleArray, val nota: String = "") : Bloque()
    class Semana(val titulo: String, val m: Array<DoubleArray>) : Bloque()
    class Lista(val titulo: String, val l: List<Triple<String, Double, String>>, val nota: String = "") : Bloque()
    class Partes(val titulo: String, val l: List<Pair<String, Double>>) : Bloque()
    class Linea(val titulo: String, val l: List<Pair<String, Double>>, val unidad: String) : Bloque()
    class Texto(val t: String) : Bloque()
}

@Composable
fun PantallaTusApps(nav: Nav, inicial: String = "youtube") {
    var pestana by rememberSaveable { mutableStateOf(inicial) }
    var periodo by rememberSaveable { mutableStateOf("todo") }
    fun seguro(p: String, q: String) = runCatching { calcular(p, q) }.getOrElse { listOf(Bloque.Texto("No se pudo calcular: ${it.message}")) }
    val bloques by produceState(if (io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono) seguro(pestana, periodo) else null, pestana, periodo) {
        if (io.github.ruddy2003.vestigium.ciencia.TusNumeros.sincrono) { value = seguro(pestana, periodo); return@produceState }
        value = null
        value = withContext(Dispatchers.Default) { seguro(pestana, periodo) }
    }
    Hoja("Tus apps", nav.atras, "YouTube, música, comida, gimnasio, lectura y mensajes, en números.") {
        Row(Modifier.padding(top = 10.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PESTANAS.forEach { (id, n) -> Ficha(n, pestana == id) { pestana = id } }
        }
        Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            PERIODOS.forEach { (id, n) -> Ficha(n, periodo == id) { periodo = id } }
        }
        val l = bloques
        when {
            l == null -> Vacio("Contando…")
            l.isEmpty() -> Vacio(when (pestana) {
                "youtube" -> "Importa tu historial de YouTube (Google Takeout, «Mi actividad» en JSON) en Datos."
                "musica" -> "Importa tu historial de Spotify o conecta la cuenta en Datos; también cuenta lo que suena en el teléfono."
                "comida", "gimnasio" -> "Importa la exportación de MacroFactor en Datos."
                "kindle" -> "Importa tus subrayados del Kindle (My Clippings.txt) en Datos."
                "whatsapp" -> "Importa chats exportados de WhatsApp en Datos (solo se guardan conteos, nunca el texto)."
                else -> "Importa tu actividad de Google (Takeout, «Mi actividad» en JSON) en Datos."
            })
            else -> l.forEach { DibujarBloque(it) }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// =====================================================================
//  Cálculos
// =====================================================================

private fun desde(periodo: String): String = when (periodo) { "anio" -> Fechas.sumar(Fechas.diaLogico(), -365); "mes" -> Fechas.sumar(Fechas.diaLogico(), -30); else -> "0000-01-01" }
private const val HASTA = "9999-12-31"
private val MESES = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
private fun mes(fecha: String) = fecha.take(7)
private fun etiquetaMes(m: String) = runCatching { MESES[m.substring(5, 7).toInt() - 1] + " " + m.substring(2, 4) }.getOrDefault(m)
private fun hora(ts: String): Int? = ts.getOrNull(11)?.let { ts.substring(11, 13).toIntOrNull() }
private fun diaSemana(f: String): Int = runCatching { Fechas.parse(f).dayOfWeek.value - 1 }.getOrDefault(0)
private fun n(x: Number): String = "%,d".format(x.toLong())
private fun d1(x: Double): String = "%.1f".format(x)

/** Meses seguidos (también los vacíos), para que la gráfica no salte. */
private fun porMes(fechas: Map<String, Double>): List<Pair<String, Double>> {
    if (fechas.isEmpty()) return emptyList()
    val ini = Fechas.parse(fechas.keys.min() + "-01")
    val fin = Fechas.parse(fechas.keys.max() + "-01")
    val out = ArrayList<Pair<String, Double>>()
    var m = ini.withDayOfMonth(1)
    while (!m.isAfter(fin)) { val k = m.toString().take(7); out += k to (fechas[k] ?: 0.0); m = m.plusMonths(1) }
    return out.takeLast(36)
}

private fun racha(fechas: Collection<String>): Int {
    val s = fechas.toSortedSet(); var mejor = 0; var actual = 0; var prev: String? = null
    for (f in s) { actual = if (prev != null && Fechas.sumar(prev, 1) == f) actual + 1 else 1; mejor = maxOf(mejor, actual); prev = f }
    return mejor
}

private fun calcular(pestana: String, periodo: String): List<Bloque> = when (pestana) {
    "youtube" -> youtube(periodo); "musica" -> musica(periodo); "comida" -> comida(periodo); "gimnasio" -> gimnasio(periodo)
    "kindle" -> kindle(periodo); "whatsapp" -> whatsapp(periodo); else -> google(periodo)
}

private fun youtube(periodo: String): List<Bloque> {
    val todo = Repo.huellasDe(desde(periodo), HASTA).filter { it.producto == "youtube" }
    val vistos = todo.filter { it.accion == "visto" }
    // Lo de los CSV de Takeout: «me gusta», comentarios, listas y suscripciones (estas no traen fecha).
    val d0 = desde(periodo)
    val yt = Repo.youtube
    val megusta = yt.filter { it.tipo == "megusta" && (it.fecha.isBlank() || it.fecha >= d0) }
    val coment = yt.filter { (it.tipo == "comentario" || it.tipo == "chat") && it.fecha >= d0 }
    val listas = yt.filter { it.tipo == "lista" && (it.fecha.isBlank() || it.fecha >= d0) }
    val subs = yt.filter { it.tipo == "suscripcion" }
    if (vistos.isEmpty() && yt.isEmpty()) return emptyList()
    val extra = listOfNotNull(
        megusta.takeIf { it.isNotEmpty() }?.let { n(it.size) to "«me gusta»" }, coment.takeIf { it.isNotEmpty() }?.let { n(it.size) to "comentarios" },
        subs.takeIf { it.isNotEmpty() }?.let { n(it.size) to "suscripciones" }, listas.takeIf { it.isNotEmpty() }?.let { n(it.size) to "videos en tus listas" })
    val deCsv = listOfNotNull(
        megusta.filter { it.fecha.isNotBlank() }.takeIf { it.size > 3 }?.let { l -> Bloque.Meses("«Me gusta» por mes", porMes(l.groupingBy { mes(it.fecha) }.eachCount().mapValues { it.value.toDouble() }), "me gusta") },
        coment.takeIf { it.size > 3 }?.let { l ->
            val h = DoubleArray(24); l.forEach { c -> hora(c.ts)?.let { h[it]++ } }
            Bloque.Horas("A qué hora comentas", h, "Solo se guarda cuándo comentaste, nunca lo que escribiste.")
        },
        listas.takeIf { it.isNotEmpty() }?.let { l -> Bloque.Lista("Tus listas", l.groupingBy { it.lista }.eachCount().entries.sortedByDescending { it.value }.take(12).map { Triple(it.key, it.value.toDouble(), "${it.value} videos") }) },
    )
    if (vistos.isEmpty()) return listOf(Bloque.Cifras(extra)) + deCsv +
        listOfNotNull(subs.takeIf { it.isNotEmpty() }?.let { Bloque.Texto("Tus suscripciones: " + it.map { s -> s.canal }.sorted().take(40).joinToString(", ") + if (it.size > 40) "…" else "") })
    val busq = todo.filter { it.accion == "buscado" }
    val dias = vistos.map { it.fecha }.toSet()
    val horas = DoubleArray(24); val semana = Array(7) { DoubleArray(24) }
    vistos.forEach { v -> hora(v.ts)?.let { h -> horas[h]++; semana[diaSemana(v.fecha)][h]++ } }
    val noche = vistos.count { v -> hora(v.ts)?.let { it < 5 } == true }
    val canales = vistos.filter { it.canal.isNotBlank() }.groupingBy { it.canal }.eachCount().entries.sortedByDescending { it.value }
    val repetidos = vistos.groupingBy { it.titulo }.eachCount().filter { it.value >= 3 && it.key.isNotBlank() }.entries.sortedByDescending { it.value }
    val palabras = busq.flatMap { it.titulo.lowercase().split(Regex("[^\\p{L}\\d]+")) }.filter { it.length > 3 && it !in VACIAS }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
    val primero = vistos.minOf { it.fecha }
    val total = Fechas.diasEntre(primero, vistos.maxOf { it.fecha }) + 1
    // Suscripciones contra lo que de verdad ves.
    val vistosPorCanal = canales.associate { it.key.lowercase() to it.value }
    val subsVistas = subs.map { it.canal to (vistosPorCanal[it.canal.lowercase()] ?: 0) }
    val olvidadas = subsVistas.filter { it.second == 0 }.map { it.first }.sorted()
    val sinSuscribir = canales.filter { c -> subs.none { it.canal.equals(c.key, true) } }.take(10)
    return listOfNotNull(
        Bloque.Cifras(listOf(n(vistos.size) to "videos vistos", n(busq.size) to "búsquedas", d1(vistos.size.toDouble() / total) to "videos al día", n(canales.size) to "canales distintos",
            n(racha(dias)) to "días seguidos (récord)", "${(noche * 100.0 / vistos.size).roundToInt()}%" to "de madrugada") + extra),
        Bloque.Meses("Videos por mes", porMes(vistos.groupingBy { mes(it.fecha) }.eachCount().mapValues { it.value.toDouble() }), "videos"),
        Bloque.Semana("Cuándo ves YouTube", semana),
        Bloque.Lista("Tus canales", canales.take(15).map { Triple(it.key, it.value.toDouble(), "${n(it.value)} videos" + if (subs.any { s -> s.canal.equals(it.key, true) }) " · suscrito" else "") }),
        Bloque.Lista("Lo que más repetiste", repetidos.take(10).map { Triple(it.key, it.value.toDouble(), "${it.value} veces") }, "Videos que viste 3 veces o más."),
        Bloque.Lista("Lo que más buscas", palabras.take(15).map { Triple(it.key, it.value.toDouble(), "${it.value}") }),
    ) + deCsv + listOfNotNull(
        if (subs.isNotEmpty() && sinSuscribir.isNotEmpty()) Bloque.Lista("Los ves mucho y no estás suscrito", sinSuscribir.map { Triple(it.key, it.value.toDouble(), "${n(it.value)} videos") }) else null,
        if (subs.isNotEmpty()) Bloque.Texto("De tus ${subs.size} suscripciones, ${subs.size - olvidadas.size} aparecen en tu historial" +
            (if (olvidadas.isNotEmpty()) ". No has visto nada de: " + olvidadas.take(25).joinToString(", ") + (if (olvidadas.size > 25) "…" else "") + "." else ".")) else null,
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private val VACIAS = setOf("para", "como", "with", "that", "this", "from", "what", "the", "and", "porque", "cuando", "donde", "sobre", "entre", "esta", "este", "tiene", "hacer", "have", "your", "about", "best", "mejor", "video", "videos", "como")

private fun musica(periodo: String): List<Bloque> {
    val e = Repo.escuchas(desde(periodo), HASTA)
    if (e.isEmpty()) return emptyList()
    // Minutos de cada escucha: lo escuchado de verdad (Spotify) o la duración de la canción, o 3.5 minutos si no se sabe.
    fun min(x: io.github.ruddy2003.vestigium.data.Escucha): Double = (x.ms?.div(60000.0)) ?: x.duracion_s?.div(60.0) ?: 3.5
    val total = e.sumOf { min(it) }
    val horas = DoubleArray(24); val semana = Array(7) { DoubleArray(24) }
    e.forEach { x -> hora(x.ts)?.let { h -> val m = min(x); horas[h] += m; semana[diaSemana(x.fecha)][h] += m } }
    val artistas = e.filter { it.artista.isNotBlank() }.groupBy { it.artista }.mapValues { (_, l) -> l.sumOf { min(it) } }.entries.sortedByDescending { it.value }
    val canciones = e.groupBy { it.titulo + " · " + it.artista }.mapValues { (_, l) -> l.size.toDouble() }.entries.sortedByDescending { it.value }
    val albums = e.filter { it.album.isNotBlank() }.groupBy { it.album + " · " + it.artista }.mapValues { (_, l) -> l.sumOf { min(it) } }.entries.sortedByDescending { it.value }
    val saltadas = e.filter { it.saltada != null }
    val primeraVez = e.filter { it.artista.isNotBlank() }.groupBy { it.artista }.mapValues { (_, l) -> l.minOf { it.fecha } }
    val nuevos = primeraVez.values.groupingBy { mes(it) }.eachCount().mapValues { it.value.toDouble() }
    val contextos = e.filter { it.contexto.isNotBlank() }.groupingBy { when (it.contexto) { "playlist" -> "listas"; "album" -> "álbumes"; "artist" -> "artistas"; "collection" -> "tus me gusta"; else -> it.contexto } }.eachCount()
    val dias = e.map { it.fecha }.toSet().size
    return listOfNotNull(
        Bloque.Cifras(listOfNotNull(n(total / 60) to "horas de música", n(e.size) to "reproducciones", n(artistas.size) to "artistas", d1(total / maxOf(1, dias)) to "minutos por día que escuchas",
            if (saltadas.isNotEmpty()) "${(saltadas.count { it.saltada == true } * 100.0 / saltadas.size).roundToInt()}%" to "canciones saltadas" else null)),
        Bloque.Meses("Horas por mes", porMes(e.groupBy { mes(it.fecha) }.mapValues { (_, l) -> l.sumOf { min(it) } / 60 }), "h"),
        Bloque.Semana("Cuándo escuchas", semana),
        Bloque.Lista("Tus artistas", artistas.take(15).map { Triple(it.key, it.value, "${n(it.value / 60)} h") }),
        Bloque.Lista("Tus canciones", canciones.take(15).map { Triple(it.key, it.value, "${n(it.value)} veces") }),
        Bloque.Lista("Tus álbumes", albums.take(10).map { Triple(it.key, it.value, "${n(it.value / 60)} h") }),
        if (nuevos.isNotEmpty()) Bloque.Meses("Artistas que descubriste cada mes", porMes(nuevos), "artistas") else null,
        if (contextos.isNotEmpty()) Bloque.Partes("Desde dónde suena", contextos.map { it.key to it.value.toDouble() }) else null,
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private fun comida(periodo: String): List<Bloque> {
    val d0 = desde(periodo)
    val dias = Repo.nutricion.values.filter { it.fecha >= d0 && (it.valores["kcal"] ?: 0.0) > 300 }.sortedBy { it.fecha }
    if (dias.isEmpty()) return emptyList()
    fun prom(k: String) = dias.mapNotNull { it.valores[k] }.takeIf { it.isNotEmpty() }?.average()
    val kcal = prom("kcal") ?: 0.0; val p = prom("proteina_g") ?: 0.0; val c = prom("carbohidratos_g") ?: 0.0; val g = prom("grasa_g") ?: 0.0
    val comidas = runCatching { Repo.comidas(d0, HASTA) }.getOrDefault(emptyList())
    val veces = comidas.groupingBy { it.alimento.trim() }.eachCount().entries.sortedByDescending { it.value }
    val porKcal = comidas.groupBy { it.alimento.trim() }.mapValues { (_, l) -> l.sumOf { it.valores["kcal"] ?: 0.0 } }.entries.sortedByDescending { it.value }
    val semanas = dias.groupBy { Fechas.parse(it.fecha).let { f -> f.minusDays((f.dayOfWeek.value - 1).toLong()).toString() } }
        .mapValues { (_, l) -> l.mapNotNull { it.valores["kcal"] }.average() }.toSortedMap().entries.map { it.key to it.value }.takeLast(52)
    val peso = Repo.nutricion.values.filter { it.fecha >= d0 }.mapNotNull { n -> n.valores["peso_tendencia_kg"]?.let { n.fecha to it } }.sortedBy { it.first }
    val gasto = prom("gasto_kcal")
    val primera = dias.mapNotNull { it.primera?.let { h -> io.github.ruddy2003.vestigium.data.Exportar.horaTexto(h) } }.takeIf { it.isNotEmpty() }?.average()
    val ultima = dias.mapNotNull { it.ultima?.let { h -> io.github.ruddy2003.vestigium.data.Exportar.horaTexto(h) } }.takeIf { it.isNotEmpty() }?.average()
    fun hhmm(h: Double) = "%d:%02d".format(h.toInt(), ((h - h.toInt()) * 60).roundToInt())
    return listOfNotNull(
        Bloque.Cifras(listOfNotNull(n(dias.size) to "días registrados", n(kcal) to "kcal al día", gasto?.let { n(it) to "kcal de gasto estimado" }, n(p) to "g de proteína",
            primera?.let { hhmm(it) to "primer bocado (promedio)" }, ultima?.let { hhmm(it) to "último bocado (promedio)" })),
        Bloque.Partes("De dónde vienen tus calorías", listOf("proteína" to p * 4, "carbohidratos" to c * 4, "grasa" to g * 9) + listOfNotNull(prom("alcohol_g")?.takeIf { it > 0.5 }?.let { "alcohol" to it * 7 })),
        Bloque.Linea("Calorías por semana", semanas, "kcal"),
        if (peso.size > 3) Bloque.Linea("Tu peso de tendencia", peso, "kg") else null,
        Bloque.Lista("Lo que más comes", veces.take(15).map { Triple(it.key, it.value.toDouble(), "${it.value} veces") }),
        Bloque.Lista("Lo que más calorías te da", porKcal.take(10).map { Triple(it.key, it.value, "${n(it.value)} kcal") }),
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private fun gimnasio(periodo: String): List<Bloque> {
    val d0 = desde(periodo)
    val e = Repo.entrenos.values.filter { it.fecha >= d0 && it.series > 0 }.sortedBy { it.fecha }
    if (e.isEmpty()) return emptyList()
    val series = runCatching { Repo.series(d0, HASTA) }.getOrDefault(emptyList())
    val ejercicios = series.groupBy { it.ejercicio }.mapValues { (_, l) -> l.size.toDouble() }.entries.sortedByDescending { it.value }
    // La misma 1RM estimada que la exportación, Fuerza y R (P-22).
    val mejores = series.mapNotNull { s -> io.github.ruddy2003.vestigium.data.Macrofactor.e1rm(s)?.let { s.ejercicio to it } }.groupBy { it.first }
        .mapValues { (_, l) -> l.maxOf { it.second } }.entries.sortedByDescending { it.value }
    val musculos = HashMap<String, Double>()
    e.forEach { x -> x.series_musculo.forEach { (k, v) -> musculos.merge(k.replace('_', ' '), v, Double::plus) } }
    val semanas = Fechas.diasEntre(e.first().fecha, e.last().fecha) / 7.0 + 1
    return listOf(
        Bloque.Cifras(listOf(n(e.size) to "entrenamientos", d1(e.size / semanas) to "por semana", n(e.sumOf { it.series }) to "series", "${n(e.sumOf { it.volumen_kg } / 1000)} t" to "levantadas en total",
            n(e.mapNotNull { it.minutos }.sum() / 60) to "horas entrenando", n(racha(e.map { it.fecha })) to "días seguidos (récord)")),
        Bloque.Meses("Entrenamientos por mes", porMes(e.groupingBy { mes(it.fecha) }.eachCount().mapValues { it.value.toDouble() }), "entrenos"),
        Bloque.Partes("Series por músculo", musculos.entries.sortedByDescending { it.value }.take(8).map { it.key to it.value }),
        Bloque.Lista("Tus ejercicios", ejercicios.take(12).map { Triple(it.key, it.value, "${n(it.value)} series") }),
        Bloque.Lista("Tus mejores marcas (1RM estimado)", mejores.take(10).map { Triple(it.key, it.value, "${d1(it.value)} kg") }, "Estimado con la fórmula de Epley a partir de tu mejor serie."),
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private fun kindle(periodo: String): List<Bloque> {
    val d0 = desde(periodo)
    val s = Repo.subrayados.filter { (it.fecha ?: "0000") >= d0 }
    if (s.isEmpty()) return emptyList()
    val libros = s.groupBy { it.libro }.entries.sortedByDescending { it.value.size }
    val autores = s.filter { it.autor.isNotBlank() }.groupingBy { it.autor }.eachCount().entries.sortedByDescending { it.value }
    val palabras = s.sumOf { io.github.ruddy2003.vestigium.data.Texto.palabras(it.texto) }
    val horas = DoubleArray(24); s.forEach { x -> x.ts?.let { hora(it) }?.let { horas[it]++ } }
    return listOfNotNull(
        Bloque.Cifras(listOf(n(s.size) to "subrayados", n(libros.size) to "libros", n(autores.size) to "autores", n(palabras) to "palabras subrayadas")),
        Bloque.Meses("Subrayados por mes", porMes(s.mapNotNull { it.fecha }.groupingBy { mes(it) }.eachCount().mapValues { it.value.toDouble() }), "subrayados"),
        if (horas.sum() > 0) Bloque.Horas("A qué hora subrayas", horas) else null,
        Bloque.Lista("Los libros que más subrayaste", libros.take(12).map { Triple(it.key, it.value.size.toDouble(), "${it.value.size}") }),
        Bloque.Lista("Tus autores", autores.take(10).map { Triple(it.key, it.value.toDouble(), "${it.value}") }),
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private fun whatsapp(periodo: String): List<Bloque> {
    val d0 = desde(periodo)
    val c = Repo.chats.filter { it.fecha >= d0 }
    if (c.isEmpty()) return emptyList()
    val mios = c.sumOf { it.mios }; val otros = c.sumOf { it.otros }
    val porChat = c.groupBy { it.chat }.mapValues { (_, l) -> l.sumOf { it.mios + it.otros }.toDouble() }.entries.sortedByDescending { it.value }
    val inicias = c.groupBy { it.chat }.mapValues { (_, l) -> l.sumOf { it.inicie }.toDouble() / maxOf(1, l.sumOf { it.conversaciones }) }
    val resp = c.mapNotNull { it.resp_mia_min }.takeIf { it.isNotEmpty() }?.sorted()?.let { it[it.size / 2] }
    val respOtros = c.mapNotNull { it.resp_otros_min }.takeIf { it.isNotEmpty() }?.sorted()?.let { it[it.size / 2] }
    return listOfNotNull(
        Bloque.Cifras(listOfNotNull(n(mios) to "mensajes tuyos", n(otros) to "mensajes recibidos", n(porChat.size) to "chats", n(c.sumOf { it.palabras_mias }) to "palabras que escribiste",
            resp?.let { "${d1(it)} min" to "tardas en responder (mediana)" }, respOtros?.let { "${d1(it)} min" to "tardan en responderte" })),
        Bloque.Meses("Mensajes por mes", porMes(c.groupBy { mes(it.fecha) }.mapValues { (_, l) -> l.sumOf { it.mios + it.otros }.toDouble() }), "mensajes"),
        Bloque.Lista("Con quién más hablas", porChat.take(12).map { Triple(it.key, it.value, "${n(it.value)} · inicias ${((inicias[it.key] ?: 0.0) * 100).roundToInt()}%") }),
        Bloque.Texto("Solo se guardan conteos y tiempos: el texto de tus chats nunca entra a la app."),
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

private fun google(periodo: String): List<Bloque> {
    val todo = Repo.huellasDe(desde(periodo), HASTA).filter { it.producto != "youtube" }
    val b = todo.filter { it.accion == "buscado" }
    if (todo.isEmpty()) return emptyList()
    val horas = DoubleArray(24); val semana = Array(7) { DoubleArray(24) }
    b.forEach { x -> hora(x.ts)?.let { h -> horas[h]++; semana[diaSemana(x.fecha)][h]++ } }
    val palabras = b.flatMap { it.titulo.lowercase().split(Regex("[^\\p{L}\\d]+")) }.filter { it.length > 3 && it !in VACIAS }.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
    val visitas = todo.filter { it.accion == "visitado" }.mapNotNull { runCatching { java.net.URI(it.titulo.trim()).host?.removePrefix("www.") }.getOrNull() ?: it.titulo.take(40) }
        .groupingBy { it }.eachCount().entries.sortedByDescending { it.value }
    val preguntas = b.count { x -> val t = x.titulo.lowercase(); listOf("qué ", "que ", "cómo ", "como ", "por qué", "cuánto", "cuando", "dónde", "how ", "what ", "why ").any { t.startsWith(it) } }
    return listOf(
        Bloque.Cifras(listOf(n(b.size) to "búsquedas", n(todo.count { it.accion == "visitado" }) to "páginas visitadas", "${if (b.isEmpty()) 0 else (preguntas * 100 / b.size)}%" to "son preguntas")),
        Bloque.Meses("Búsquedas por mes", porMes(b.groupingBy { mes(it.fecha) }.eachCount().mapValues { it.value.toDouble() }), "búsquedas"),
        Bloque.Semana("Cuándo buscas", semana),
        Bloque.Lista("Las palabras que más buscas", palabras.take(15).map { Triple(it.key, it.value.toDouble(), "${it.value}") }),
        Bloque.Lista("Los sitios que más visitas", visitas.take(10).map { Triple(it.key, it.value.toDouble(), "${it.value}") }),
    ).filter { it !is Bloque.Lista || it.l.isNotEmpty() }
}

// =====================================================================
//  Dibujo
// =====================================================================

@Composable
private fun DibujarBloque(b: Bloque) {
    val cs = MaterialTheme.colorScheme
    when (b) {
        is Bloque.Cifras -> Row(Modifier.padding(top = 14.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            b.l.forEach { (v, e) -> Cifra(v, e, Modifier.width(112.dp)) }
        }
        is Bloque.Meses -> Seccion(b.titulo) { Tarjeta { BarrasMes(b.l, b.unidad) } }
        is Bloque.Horas -> Seccion(b.titulo) { Tarjeta { BarrasHoras(b.h); if (b.nota.isNotEmpty()) Ayuda(b.nota) } }
        is Bloque.Semana -> Seccion(b.titulo) { Tarjeta { MapaSemana(b.m) } }
        is Bloque.Lista -> Seccion(b.titulo) {
            Tarjeta {
                val max = b.l.maxOfOrNull { it.second } ?: 1.0
                val eg = estiloGrafica()
                val cifras = estiloCifras(MaterialTheme.typography.labelMedium)
                b.l.forEachIndexed { i, (nombre, v, txt) ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(Estilo.numeral(i + 1), style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.width(30.dp))
                            Text(nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text(txt, style = cifras, color = cs.onSurfaceVariant)
                        }
                        Box(Modifier.padding(start = 30.dp, top = 3.dp).fillMaxWidth().height(4.dp)) {
                            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                                barraGrafica(eg, Offset.Zero, size, cs.outline.copy(alpha = 0.2f), horizontal = true, pista = true)
                                barraGrafica(eg, Offset.Zero, Size(size.width * (v / max).toFloat().coerceIn(0.02f, 1f), size.height), lerp(cs.primary, cs.tertiary, i / 15f), horizontal = true)
                            }
                        }
                    }
                }
                if (b.nota.isNotEmpty()) Ayuda(b.nota, Modifier.padding(top = 6.dp))
            }
        }
        is Bloque.Partes -> Seccion(b.titulo) { Tarjeta { Dona(b.l) } }
        is Bloque.Linea -> Seccion(b.titulo) { Tarjeta { LineaTiempo(b.l, b.unidad) } }
        is Bloque.Texto -> Ayuda(b.t, Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun BarrasMes(l: List<Pair<String, Double>>, unidad: String) {
    if (l.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    val max = l.maxOf { it.second }.coerceAtLeast(1e-9)
    val mejor = l.maxByOrNull { it.second }
    val eg = estiloGrafica()
    Canvas(Modifier.fillMaxWidth().height(150.dp)) {
        val abajo = size.height - 16.dp.toPx(); val ancho = size.width / l.size
        l.forEachIndexed { i, (m, v) ->
            val h = (v / max).toFloat() * (abajo - 4.dp.toPx())
            val col = if (m == mejor?.first) cs.secondary else cs.primary
            barraGrafica(eg, Offset(i * ancho + ancho * 0.15f, abajo - h), Size(ancho * 0.7f, h), col.copy(alpha = 0.85f))
            val cada = maxOf(1, l.size / 6)
            if (i % cada == 0) drawText(medidor, etiquetaMes(m), Offset(i * ancho, abajo + 3.dp.toPx()), estilo)
        }
        ejeGrafica(eg, Offset(0f, abajo), Offset(size.width, abajo), cs.outline)
    }
    mejor?.let { Ayuda("Tu mes con más: ${etiquetaMes(it.first)}, con ${if (it.second >= 10) n(it.second) else d1(it.second)} $unidad.") }
}

@Composable
private fun BarrasHoras(h: DoubleArray) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    val max = (h.maxOrNull() ?: 1.0).coerceAtLeast(1e-9)
    val eg = estiloGrafica()
    val cifras = estiloCifras(estilo)
    Canvas(Modifier.fillMaxWidth().height(110.dp)) {
        val abajo = size.height - 14.dp.toPx(); val ancho = size.width / 24
        for (i in 0 until 24) {
            val alto = (h[i] / max).toFloat() * (abajo - 2.dp.toPx())
            barraGrafica(eg, Offset(i * ancho + 1f, abajo - alto), Size(ancho - 2f, alto), lerp(cs.tertiary, cs.primary, if (i in 7..19) 1f else 0f))
            if (i % 6 == 0) drawText(medidor, "$i h", Offset(i * ancho, abajo + 2.dp.toPx()), cifras)
        }
        ejeGrafica(eg, Offset(0f, abajo), Offset(size.width, abajo), cs.outline)
    }
}

/** Siete filas (lunes a domingo) por 24 horas: más oscuro, más actividad. */
@Composable
private fun MapaSemana(m: Array<DoubleArray>) {
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    val max = m.maxOf { it.maxOrNull() ?: 0.0 }.coerceAtLeast(1e-9)
    val dias = listOf("L", "M", "M", "J", "V", "S", "D")
    val eg = estiloGrafica()
    val cifras = estiloCifras(estilo)
    Canvas(Modifier.fillMaxWidth().height(150.dp)) {
        val izq = 14.dp.toPx(); val abajo = 14.dp.toPx()
        val cw = (size.width - izq) / 24; val ch = (size.height - abajo) / 7
        for (d in 0 until 7) {
            drawText(medidor, dias[d], Offset(0f, d * ch + ch / 2 - 6.dp.toPx()), estilo)
            for (h in 0 until 24) {
                val f = (m[d][h] / max).toFloat()
                // Cada hora, con la silueta de barra del tema (sin adornos internos: las celdas son muy chicas).
                barraGrafica(eg, Offset(izq + h * cw + 1f, d * ch + 1f), Size(cw - 2f, ch - 2f), if (f == 0f) cs.outline.copy(alpha = 0.12f) else cs.primary.copy(alpha = 0.15f + 0.85f * f), pista = true)
            }
        }
        for (h in listOf(0, 6, 12, 18)) drawText(medidor, "$h", Offset(izq + h * cw, size.height - abajo + 2.dp.toPx()), cifras)
    }
}

@Composable
private fun Dona(l: List<Pair<String, Double>>) {
    val cs = MaterialTheme.colorScheme
    val total = l.sumOf { it.second }.coerceAtLeast(1e-9)
    val colores = listOf(cs.primary, cs.secondary, cs.tertiary, lerp(cs.primary, cs.secondary, 0.5f), lerp(cs.tertiary, cs.onBackground, 0.4f), lerp(cs.secondary, cs.tertiary, 0.5f), cs.outline, lerp(cs.primary, cs.onBackground, 0.5f))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(120.dp)) {
            var ini = -90f
            l.forEachIndexed { i, (_, v) ->
                val ang = (v / total * 360).toFloat()
                drawArc(colores[i % colores.size], ini, ang - 1.5f, false, Offset(10f, 10f), Size(size.width - 20f, size.height - 20f), style = Stroke(18.dp.toPx(), cap = StrokeCap.Butt))
                ini += ang
            }
        }
        Column(Modifier.padding(start = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            l.forEachIndexed { i, (nombre, v) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(10.dp)) { drawCircle(colores[i % colores.size]) }
                    Text(" $nombre · ${(v * 100 / total).roundToInt()}%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun LineaTiempo(l: List<Pair<String, Double>>, unidad: String) {
    if (l.size < 2) return
    val cs = MaterialTheme.colorScheme
    val medidor = rememberTextMeasurer()
    val estilo = TextStyle(fontSize = 9.sp, color = cs.onSurfaceVariant)
    val min = l.minOf { it.second }; val max = l.maxOf { it.second }; val rango = (max - min).coerceAtLeast(1e-9)
    val eg = estiloGrafica()
    val cifras = estiloCifras(estilo)
    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
        val abajo = size.height - 14.dp.toPx(); val arriba = 6.dp.toPx()
        fun p(i: Int) = Offset(i * size.width / (l.size - 1), abajo - ((l[i].second - min) / rango).toFloat() * (abajo - arriba))
        val camino = Path().apply { moveTo(p(0).x, p(0).y); for (i in 1 until l.size) lineTo(p(i).x, p(i).y) }
        ejeGrafica(eg, Offset(0f, abajo), Offset(size.width, abajo), cs.outline)
        lineaGrafica(eg, camino, cs.primary)
        puntoGrafica(eg, p(l.size - 1), 4.dp.toPx(), cs.secondary)
        drawText(medidor, l.first().first.take(7), Offset(0f, abajo + 2.dp.toPx()), cifras)
        val fin = medidor.measure(l.last().first.take(7), cifras)
        drawText(fin, topLeft = Offset(size.width - fin.size.width, abajo + 2.dp.toPx()))
    }
    fun f(x: Double) = if (max >= 100) n(x) else d1(x)
    Ayuda("De ${f(l.first().second)} a ${f(l.last().second)} $unidad · mínimo ${f(min)}, máximo ${f(max)}.")
}

