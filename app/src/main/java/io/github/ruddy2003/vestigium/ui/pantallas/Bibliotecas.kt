@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.data.privado
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Carta
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.alFuturo
import io.github.ruddy2003.vestigium.data.legible
import io.github.ruddy2003.vestigium.data.lista
import io.github.ruddy2003.vestigium.data.sellada
import io.github.ruddy2003.vestigium.ui.AperturaCarta
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BorradorCarta
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.CieloNoche
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.DialogoCarta
import io.github.ruddy2003.vestigium.ui.Escrito
import io.github.ruddy2003.vestigium.ui.EscribirSueno
import io.github.ruddy2003.vestigium.ui.EstadoSobre
import io.github.ruddy2003.vestigium.ui.EstanteDiario
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Etiquetas
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.HojaCarta
import io.github.ruddy2003.vestigium.ui.LecturaCarta
import io.github.ruddy2003.vestigium.ui.LecturaTema
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.ObjetoDiario
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.SelladoCarta
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.SobreCarta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.estadoSobre
import io.github.ruddy2003.vestigium.ui.mediaLuna
import io.github.ruddy2003.vestigium.ui.rememberSentir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import io.github.ruddy2003.vestigium.ui.Dialogo
import io.github.ruddy2003.vestigium.ui.elegirFecha

/** Minúsculas y sin acentos, para buscar sin importar cómo se escribió. */
private fun sinAcentos(s: String) = java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

private val BIBLIOTECAS = listOf(Triple("diario", "Diario", "biblioteca_diario"), Triple("momentos", "Momentos", "biblioteca_momentos"),
    Triple("suenos", "Sueños", "biblioteca_suenos"), Triple("ideas", "Ideario", "biblioteca_ideas"), Triple("cartas", "Cartas", "cartas"))

/**
 * Las cinco bibliotecas en una: el diario, la libreta de momentos, los sueños, el Ideario y las cartas (el mismo orden que en la pestaña Diario). Se cambia entre ellas con las pestañas de arriba
 * o deslizando a los lados.
 */
@Composable
fun PantallaBibliotecas(nav: Nav, inicial: String) {
    var local by remember { mutableStateOf(inicial) }
    // Cambiar de pestaña reemplaza la pantalla: así, al volver de una entrada, sigues en la misma biblioteca.
    val cual = local
    val sentir = rememberSentir()
    fun ir(id: String) {
        if (id == cual) return
        sentir(Sentidos.Evento.PASAR)
        local = id
        BIBLIOTECAS.firstOrNull { it.first == id }?.let { nav.reemplazar(it.third) }
    }
    val umbral = with(LocalDensity.current) { 110.dp.toPx() }
    val i = BIBLIOTECAS.indexOfFirst { it.first == cual }.coerceAtLeast(0)
    val deslizar = Modifier.pointerInput(cual) {
        var total = 0f
        detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
            if (total < -umbral && i < BIBLIOTECAS.lastIndex) ir(BIBLIOTECAS[i + 1].first)
            else if (total > umbral && i > 0) ir(BIBLIOTECAS[i - 1].first)
        }) { _, dx -> total += dx }
    }
    val titulo = when (cual) { "suenos" -> "Biblioteca de sueños"; "cartas" -> "Cartas"; "momentos" -> "Libreta de momentos"; "ideas" -> "Ideario"; else -> "Biblioteca del diario" }
    val sub = when (cual) {
        "suenos" -> "Lo que soñaste, noche por noche, en el mismo estante del tema pero de noche."
        "momentos" -> "Una hoja por día con todo lo que registraste al momento: cómo te sentías, en qué pensabas, con quién y lo que tomaste."
        "cartas" -> "Las que te escribes al futuro, selladas hasta su fecha, y las que escribes a otras personas."
        "ideas" -> "Lo que aprendiste de un libro, un video o una plática, y lo que se te ocurrió, con tus palabras, su fuente y quién lo dice."
        else -> LibroDiario.entradas().let { l -> "${l.size} entradas · %,d palabras. Toca una para abrirla.".format(l.sumOf { Texto.palabras(it.nota) }) }
    }
    Hoja(titulo, nav.atras, sub, modifier = deslizar) {
        SelectorBibliotecas(cual) { ir(it) }
        when (cual) {
            "suenos" -> BibliotecaSuenos(nav)
            "cartas" -> BibliotecaCartas(nav)
            "momentos" -> BibliotecaMomentosLibreta(nav)
            "ideas" -> BibliotecaIdeas(nav, ::rotuloMes)
            else -> BibliotecaDiario(nav)
        }
    }
}

/** Las pestañas, con su dibujo y cuántas cosas hay en cada una. */
@Composable
private fun SelectorBibliotecas(actual: String, elegir: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val n = mapOf("diario" to Repo.dias.values.count { it.nota.isNotBlank() }, "suenos" to Repo.dias.values.count { it.sueno_texto.isNotBlank() },
        "momentos" to diasConMomentos().size, "cartas" to Repo.cartas.size, "ideas" to Repo.ideas.size)
    val tema = Estilo.tema
    Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        BIBLIOTECAS.forEach { (id, nombre, _) ->
            val sel = id == actual
            Surface(onClick = { elegir(id) }, shape = MaterialTheme.shapes.small, color = if (sel) cs.primary else cs.surface,
                border = BorderStroke(1.dp, if (sel) cs.primary else cs.outline), modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val tinta = if (sel) cs.onPrimary else cs.primary
                    Canvas(Modifier.size(width = 30.dp, height = 20.dp)) { icono(id, tinta, tema, if (sel) cs.primary else cs.surface) }
                    // Con cinco pestañas «Momentos» casi no cabe (y en mayúsculas con letra ancha, griego o Revolución, menos): se achica
                    // un poco en lugar de partirse.
                    val estilo = MaterialTheme.typography.labelLarge.let { if (nombre.length > 7) it.copy(fontSize = it.fontSize * (if (Estilo.mayusculas) 0.76f else 0.9f), letterSpacing = 0.sp) else it }
                    Text(if (Estilo.mayusculas) nombre.uppercase() else nombre, style = estilo, color = if (sel) cs.onPrimary else cs.onSurface,
                        maxLines = 1, softWrap = false, modifier = Modifier.padding(top = 4.dp))
                    Text("${n[id] ?: 0}", style = MaterialTheme.typography.labelSmall, color = (if (sel) cs.onPrimary else cs.onSurfaceVariant).copy(alpha = 0.85f))
                }
            }
        }
    }
}

/**
 * Un libro abierto, una luna con estrellas, una libreta, un sobre o una ficha de fichero, y en cada uno el símbolo del tema (el mismo con que se
 * califica: hoja de laurel, cruz de malta, abeja, engrane, rosetón…): en la página derecha del libro, como la estrella mayor
 * junto a la luna, en la tapa de la libreta y como el sello del sobre.
 */
private fun DrawScope.icono(id: String, c: Color, t: TemaId, fondo: Color) {
    val w = size.width; val h = size.height
    val trazo = Stroke(1.6.dp.toPx())
    fun sello(o: Offset, r: Float, aro: Boolean = false) {
        if (aro) drawCircle(fondo, r * 1.25f, o)
        drawPath(simboloCalificacion(t, o, r), c)
    }
    when (id) {
        "suenos" -> {
            mediaLuna(Offset(w * 0.42f, h * 0.52f), h * 0.42f, c)
            sello(Offset(w * 0.82f, h * 0.24f), h * 0.18f)
            listOf(Offset(w * 0.72f, h * 0.76f) to 1.1f, Offset(w * 0.94f, h * 0.58f) to 0.9f).forEach { (o, r) -> drawCircle(c, r.dp.toPx(), o) }
        }
        "momentos" -> {
            // Una libreta de espiral con el símbolo del tema en la tapa.
            drawRect(c, Offset(w * 0.22f, h * 0.08f), Size(w * 0.6f, h * 0.84f), style = trazo)
            for (k in 0 until 4) drawCircle(c, 1.4.dp.toPx(), Offset(w * 0.22f, h * (0.22f + k * 0.19f)))
            sello(Offset(w * 0.54f, h * 0.5f), h * 0.27f)
        }
        "ideas" -> {
            // Una ficha de fichero con su pestaña y el símbolo del tema.
            drawRect(c, Offset(w * 0.14f, h * 0.24f), Size(w * 0.72f, h * 0.68f), style = trazo)
            drawPath(Path().apply { moveTo(w * 0.14f, h * 0.24f); lineTo(w * 0.2f, h * 0.06f); lineTo(w * 0.46f, h * 0.06f); lineTo(w * 0.52f, h * 0.24f) }, c, style = trazo)
            sello(Offset(w * 0.5f, h * 0.58f), h * 0.2f)
        }
        "cartas" -> {
            drawRect(c, Offset(w * 0.12f, h * 0.15f), Size(w * 0.76f, h * 0.7f), style = trazo)
            drawPath(Path().apply { moveTo(w * 0.12f, h * 0.15f); lineTo(w * 0.5f, h * 0.55f); lineTo(w * 0.88f, h * 0.15f) }, c, style = trazo)
            sello(Offset(w * 0.5f, h * 0.56f), h * 0.2f, aro = true)
        }
        else -> {
            drawPath(Path().apply { moveTo(w * 0.5f, h * 0.25f); quadraticTo(w * 0.3f, h * 0.1f, w * 0.08f, h * 0.18f); lineTo(w * 0.08f, h * 0.88f); quadraticTo(w * 0.3f, h * 0.8f, w * 0.5f, h * 0.92f); close() }, c, style = trazo)
            drawPath(Path().apply { moveTo(w * 0.5f, h * 0.25f); quadraticTo(w * 0.7f, h * 0.1f, w * 0.92f, h * 0.18f); lineTo(w * 0.92f, h * 0.88f); quadraticTo(w * 0.7f, h * 0.8f, w * 0.5f, h * 0.92f); close() }, c, style = trazo)
            sello(Offset(w * 0.71f, h * 0.53f), h * 0.18f)
        }
    }
}

private fun rotuloMes(mes: String) = Estilo.mes(mes.drop(5).toInt(), mes.take(4).toInt()).let { if (it.contains(mes.take(4))) it else it + " · " + mes.take(4) }

// ---------------------------------------------------------------- Diario

@Composable
private fun BibliotecaDiario(nav: Nav) {
    val todas = remember(Repo.dias) { LibroDiario.entradas() }
    var busca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf<String?>(null) }
    var meses by remember { mutableIntStateOf(6) }
    val q = sinAcentos(busca.trim())
    val visibles = todas.filter { d -> (anio == null || d.fecha.startsWith(anio!!)) && (q.isBlank() || sinAcentos(d.nota + " " + d.mejor_momento + " " + d.peor_momento).contains(q)) }
    val porMes = visibles.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    OutlinedTextField(busca, { busca = it }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true,
        placeholder = { Text("buscar en las entradas", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        todas.map { it.fecha.take(4) }.distinct().forEach { a -> Ficha(a, anio == a) { anio = if (anio == a) null else a } }
    }
    if (todas.isEmpty()) Vacio("Todavía no hay entradas. Escribe la de hoy desde la pestaña Diario o al llenar el día.")
    else if (visibles.isEmpty()) Vacio("Nada coincide con la búsqueda.")
    porMes.entries.take(meses).forEach { (mes, lista) ->
        Text(rotuloMes(mes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 20.dp, bottom = 2.dp))
        EstanteDiario(lista) { f -> Seleccion.entrada = f; nav.ir("entrada") }
    }
    if (porMes.size > meses) BotonSecundario("ver más meses", Modifier.padding(top = 14.dp)) { meses += 6 }
}

// ---------------------------------------------------------------- Sueños

@Composable
private fun BibliotecaSuenos(nav: Nav) {
    val todas = remember(Repo.dias) { LibroDiario.suenos() }
    var escribiendo by remember { mutableStateOf<String?>(null) }
    var busca by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf<String?>(null) }
    var tema by remember { mutableStateOf<String?>(null) }
    var meses by remember { mutableIntStateOf(6) }
    val hoy = Fechas.diaLogico()
    Seccion("Anoche", "Toca para contar lo que soñaste. Se guarda mientras escribes; lo demás del sueño (qué tan agradable, lúcido o vívido) se marca en la mañana de Día.") {
        ObjetoDiario(hoy, Repo.dias[hoy]?.sueno_texto.orEmpty(), noche = true) { escribiendo = hoy }
    }
    if (todas.isNotEmpty()) {
        val lucidos = todas.count { it.sueno_lucidez == "parcial" || it.sueno_lucidez == "total" }
        val pesadillas = todas.count { io.github.ruddy2003.vestigium.data.Catalogo.pesadillaDe(it) == true }
        val agradable = todas.mapNotNull { it.sueno_valencia }.takeIf { it.isNotEmpty() }?.average()
        Row(Modifier.fillMaxWidth().padding(top = 16.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("${todas.size}", "sueños escritos", Modifier.width(100.dp))
            Cifra("$lucidos", "lúcidos", Modifier.width(100.dp))
            Cifra("$pesadillas", "pesadillas", Modifier.width(100.dp))
            agradable?.let { Cifra("%.1f".format(it), "qué tan agradables (1 a 7)", Modifier.width(120.dp)) }
        }
        // Lo que más aparece: personas y temas marcados en el sueño o escritos con @ y #.
        val frecuentes = remember(todas) {
            todas.flatMap { d -> (d.sueno_temas + Texto.personas(d.sueno_texto).map { "@$it" } + Texto.temas(d.sueno_texto).map { "#$it" }).distinct() }
                .groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(10).map { it.key }
        }
        if (frecuentes.isNotEmpty()) {
            Text("lo que más aparece", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                frecuentes.forEach { t -> Ficha(t, tema == t) { tema = if (tema == t) null else t } }
            }
        }
    }
    val q = sinAcentos(busca.trim())
    val visibles = todas.filter { d ->
        (anio == null || d.fecha.startsWith(anio!!)) && (q.isBlank() || sinAcentos(d.sueno_texto + " " + d.sueno_temas.joinToString(" ")).contains(q)) &&
            (tema == null || tema!!.removePrefix("@").removePrefix("#").let { t -> t in d.sueno_temas || sinAcentos(d.sueno_texto).contains(sinAcentos(tema!!)) })
    }
    OutlinedTextField(busca, { busca = it }, Modifier.fillMaxWidth().padding(top = 14.dp), singleLine = true,
        placeholder = { Text("buscar en tus sueños", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        todas.map { it.fecha.take(4) }.distinct().forEach { a -> Ficha(a, anio == a) { anio = if (anio == a) null else a } }
    }
    if (todas.isEmpty()) Vacio("Todavía no hay sueños escritos. Cuéntalo arriba, o en la mañana desde Día → Sueño.")
    else if (visibles.isEmpty()) Vacio("Ningún sueño coincide.")
    val porMes = visibles.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    porMes.entries.take(meses).forEach { (mes, lista) ->
        Text(rotuloMes(mes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 20.dp, bottom = 6.dp))
        CieloNoche { EstanteDiario(lista, noche = true) { f -> Seleccion.sueno = f; nav.ir("sueno") } }
    }
    if (porMes.size > meses) BotonSecundario("ver más meses", Modifier.padding(top = 14.dp)) { meses += 6 }
    escribiendo?.let { f -> EscribirSueno(f) { escribiendo = null } }
}

/** Un sueño abierto a pantalla completa, sobre la página nocturna del tema. */
@Composable
fun PantallaSueno(nav: Nav) {
    val ctx = LocalContext.current
    val todas = remember(Repo.dias) { LibroDiario.suenos() }
    var fecha by remember { mutableStateOf(Seleccion.sueno ?: todas.firstOrNull()?.fecha ?: Fechas.diaLogico()) }
    var escribiendo by remember { mutableStateOf(false) }
    val d = Repo.dias[fecha]
    val i = todas.indexOfFirst { it.fecha == fecha }
    LecturaTema(fecha, d, volver = nav.atras,
        anterior = if (i in 0 until todas.size - 1) ({ fecha = todas[i + 1].fecha; Seleccion.sueno = fecha }) else null,
        siguiente = if (i > 0) ({ fecha = todas[i - 1].fecha; Seleccion.sueno = fecha }) else null,
        acciones = listOf(
            "Editar" to { escribiendo = true },
            "Compartir texto" to {
                if (d != null) ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, "Sueño · ${Fechas.titulo(fecha)}\n\n" + d.sueno_texto.trim()), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            },
            "Abrir el día" to { Seleccion.abrir(nav, fecha) },
        ), modo = Escrito.SUENO)
    if (escribiendo) EscribirSueno(fecha) { escribiendo = false }
}

// ---------------------------------------------------------------- Momentos

/** Los momentos que se ven fuera de la pantalla Privado. */
internal fun momentosVisibles() = Repo.momentos.filter { !it.privado }

/** Los días con algo registrado al momento (ánimo, momentos o sustancias visibles), del más reciente al más antiguo. */
fun diasConMomentos(): List<String> =
    (momentosVisibles().map { it.fecha } + Repo.consumos.filter { io.github.ruddy2003.vestigium.data.Sustancias.visible(it) }.map { it.fecha }).distinct().sortedDescending()

private fun lineaMomento(m: io.github.ruddy2003.vestigium.data.Momento, discreto: Boolean): String = buildString {
    val cat = io.github.ruddy2003.vestigium.data.Catalogo
    append(Fechas.horaDe(m.ts)); append(" — "); append(cat.titulo(m, discreto))
    val cifras = listOfNotNull(m.valencia?.let { "agrado $it/7" }, m.activacion?.let { "energía $it/7" }, m.intensidad?.let { "intensidad $it/5" })
    if (cifras.isNotEmpty()) append(" (" + cifras.joinToString(", ") + ")")
    if (!discreto) {
        if (cat.emocionesDe(m).isNotEmpty()) cat.pensamientosDe(m).takeIf { it.isNotEmpty() }?.let { l -> append("\nen qué pensabas: " + l.joinToString(", ") { cat.nombrePensamiento(it).lowercase() }) }
        if (m.con_quien.isNotEmpty()) append("\ncon: " + m.con_quien.joinToString(", ") { q -> cat.CON_QUIEN.firstOrNull { it.id == q }?.nombre?.lowercase() ?: q })
        m.lugar?.takeIf { it.isNotBlank() }?.let { append("\nen: $it") }
    }
    if (m.texto.isNotBlank()) append("\n" + m.texto.trim())
}

private fun lineaConsumo(c: io.github.ruddy2003.vestigium.data.Consumo): String = buildString {
    val cat = io.github.ruddy2003.vestigium.data.Catalogo
    val sus = io.github.ruddy2003.vestigium.data.Sustancias
    append(Fechas.horaDe(c.inicio)); append(" — "); append(cat.nombreSustancia(c))
    val det = listOfNotNull(sus.maximo(c)?.let { "nivel $it/10" }, c.cantidad?.let { q -> "%s %s".format(if (q % 1.0 == 0.0) "${q.toInt()}" else "%.1f".format(q), cat.sustancia(c.sustancia)?.unidad ?: "") },
        sus.horas(c)?.let { "%.1f h".format(it) }, c.cruda?.let { "cruda $it/5" })
    if (det.isNotEmpty()) append(" (" + det.joinToString(", ") + ")")
    if (c.con_quien.isNotEmpty()) append("\ncon: " + c.con_quien.joinToString(", "))
    if (c.nota.isNotBlank()) append("\n" + c.nota.trim())
}

/** La hoja de momentos de un día, como si fuera una entrada del diario (para el estante y la lectura del tema). */
fun hojaMomentos(fecha: String, discreto: Boolean = Repo.config.discreto): io.github.ruddy2003.vestigium.data.Dia? {
    val ms = momentosVisibles().filter { it.fecha == fecha }
    val cs = Repo.consumos.filter { it.fecha == fecha && io.github.ruddy2003.vestigium.data.Sustancias.visible(it) }
    if (ms.isEmpty() && cs.isEmpty()) return null
    val l = (ms.map { Fechas.millis(it.ts) to lineaMomento(it, discreto) } + cs.map { Fechas.millis(it.inicio) to lineaConsumo(it) }).sortedBy { it.first }
    val v = ms.filter { io.github.ruddy2003.vestigium.data.Catalogo.esRegistro(it) }.mapNotNull { it.valencia }
    return io.github.ruddy2003.vestigium.data.Dia(fecha, nota = l.joinToString("\n\n") { it.second },
        valencia = v.takeIf { it.isNotEmpty() }?.average()?.let { kotlin.math.round(it).toInt() })
}

// ---------------------------------------------------------------- Cartas

@Composable
private fun RejillaCartas(lista: List<Carta>, abrir: (Carta) -> Unit) {
    Column(Modifier.padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        lista.chunked(2).forEach { fila ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                fila.forEach { c ->
                    val abierta = estadoSobre(c) == EstadoSobre.ABIERTA
                    SobreCarta(c, Modifier.weight(1f).height(if (abierta) 158.dp else 112.dp)) { abrir(c) }
                }
                if (fila.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun BibliotecaCartas(nav: Nav) {
    val hoy = Fechas.diaLogico()
    var busca by remember { mutableStateOf("") }
    var persona by remember { mutableStateOf<String?>(null) }
    var abriendo by remember { mutableStateOf<Carta?>(null) }
    val todas = Repo.cartas
    BotonPrincipal("✉  escribir una carta", Modifier.fillMaxWidth().padding(top = 14.dp)) { nav.ir("carta_nueva") }
    val listas = todas.filter { it.lista(hoy) }.sortedBy { it.abrir_en }
    if (listas.isNotEmpty()) Seccion("Listas para abrir", "Ya llegó su fecha. Toca el sobre para romper el lacre.") { RejillaCartas(listas) { abriendo = it } }
    val selladas = todas.filter { it.sellada(hoy) }.sortedBy { it.abrir_en }
    Seccion("Selladas", if (selladas.isEmpty()) null else "${selladas.size} para tu yo del futuro. No se pueden abrir antes de su fecha y no salen en el buscador.") {
        if (selladas.isEmpty()) Vacio("Ninguna por ahora. Escribe una a tu yo de dentro de un año.")
        else RejillaCartas(selladas) {}
    }
    val legibles = todas.filter { it.legible }
    if (legibles.isEmpty()) return
    OutlinedTextField(busca, { busca = it }, Modifier.fillMaxWidth().padding(top = 18.dp), singleLine = true,
        placeholder = { Text("buscar en tus cartas", style = MaterialTheme.typography.bodyMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
    val q = sinAcentos(busca.trim())
    fun coincide(c: Carta) = q.isBlank() || sinAcentos(c.texto + " " + c.para).contains(q)
    val aOtros = legibles.filter { !it.alFuturo && coincide(it) }
    if (legibles.any { !it.alFuturo }) Seccion("A otras personas", "Las que escribiste a alguien con @. Se guardan abiertas.") {
        val personas = legibles.filter { !it.alFuturo }.groupingBy { it.para }.eachCount().entries.sortedByDescending { it.value }.map { it.key }
        if (personas.size > 1) FlowRow(Modifier.padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            personas.forEach { p -> Ficha("@$p", persona == p) { persona = if (persona == p) null else p } }
        }
        val l = aOtros.filter { persona == null || it.para == persona }.sortedByDescending { it.escrita + it.hora }
        if (l.isEmpty()) Vacio("Ninguna coincide.") else RejillaCartas(l) { Seleccion.carta = it.id; nav.ir("carta") }
    }
    val propias = legibles.filter { it.alFuturo && coincide(it) }.sortedByDescending { it.abierta_el ?: it.abrir_en }
    if (legibles.any { it.alFuturo }) Seccion("Del pasado", "Las que te escribiste y ya abriste.") {
        if (propias.isEmpty()) Vacio("Ninguna coincide.") else RejillaCartas(propias) { Seleccion.carta = it.id; nav.ir("carta") }
    }
    abriendo?.let { c ->
        AperturaCarta(c) {
            val abierta = c.copy(abierta = true, abierta_el = hoy)
            Repo.guardarCarta(abierta); Seleccion.carta = c.id; abriendo = null; nav.ir("carta")
        }
    }
}

/** Dónde estás (la ciudad de tu última ubicación de las últimas horas), para el encabezado de la carta. */
private fun lugarActual(ctx: Context): String? = runCatching {
    val ms = Repo.db().ultimaUbicacion() ?: return null
    if (System.currentTimeMillis() - ms > 12 * 3_600_000L) return null
    val p = Repo.db().ubicaciones(ms, ms + 1).firstOrNull() ?: return null
    val i = Mundo.ciudadEn(ctx, p.lat, p.lon)
    if (i < 0) null else Mundo.ciudades(ctx).nombre(i)
}.getOrNull()

/** Escribir una carta: a tu yo del futuro (se sella hasta una fecha) o a alguien (@persona). */
@Composable
fun PantallaCartaNueva(nav: Nav) {
    val ctx = LocalContext.current
    val hoy = Fechas.diaLogico()
    val hora = remember { Fechas.horaAhora() }
    var escribiendo by remember { mutableStateOf(false) }
    var sellando by remember { mutableStateOf<Carta?>(null) }
    val lugar by produceState(Repo.config.perfil.ciudad) { value = withContext(Dispatchers.IO) { lugarActual(ctx) } ?: Repo.config.perfil.ciudad }
    val conocidas = remember {
        (Repo.config.perfil.cercanas.map { it.nombre } + Repo.config.perfil.convivientes + Repo.cartas.map { it.para } + Etiquetas.frecuentes().first)
            .filter { it.isNotBlank() }.distinct()
    }
    val b = BorradorCarta
    Hoja("Escribir una carta", nav.atras, "Con el papel del tema, la fecha, la hora y el lugar donde la escribes.") {
        Seccion("¿Para quién?") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OpcionCarta("Mi yo del futuro", "Se sella y no se abre hasta la fecha que elijas.", b.alFuturo, Modifier.weight(1f)) { b.alFuturo = true }
                OpcionCarta("Alguien", "Una carta para una persona, con su @. Se guarda abierta.", !b.alFuturo, Modifier.weight(1f)) { b.alFuturo = false }
            }
            if (!b.alFuturo) {
                OutlinedTextField(b.para, { b.para = Texto.etiqueta(it) }, Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true,
                    prefix = { Text("@") }, placeholder = { Text("nombre, como lo escribes en tu diario") })
                val sug = conocidas.filter { it != b.para && (b.para.isBlank() || it.startsWith(b.para)) }.take(10)
                if (sug.isNotEmpty()) FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sug.forEach { p -> Ficha("@$p", false) { b.para = p } }
                }
            } else {
                Text("¿Cuándo se abre?", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(30L to "1 mes", 182L to "6 meses", 365L to "1 año", 730L to "2 años", 1826L to "5 años", 3652L to "10 años").forEach { (d, n) ->
                        Ficha(n, b.fecha == null && b.plazo == d) { b.plazo = d; b.fecha = null }
                    }
                    Ficha(b.fecha?.let { "el ${Fechas.corto(it)} ${it.take(4)}" } ?: "otra fecha…", b.fecha != null) {
                        elegirFecha(ctx, b.abrirEn(hoy), minimo = Fechas.parse(Fechas.sumar(hoy, 1))) { f -> b.fecha = if (f <= hoy) Fechas.sumar(hoy, 1) else f }
                    }
                }
                Ayuda("Se abrirá el ${Fechas.titulo(b.abrirEn(hoy)).lowercase()} de ${b.abrirEn(hoy).take(4)}. Ese día te llegará un aviso.", Modifier.padding(top = 8.dp))
            }
        }
        Seccion("La carta", "Toca la hoja para escribir a pantalla completa.") {
            HojaCarta(b.texto, if (b.alFuturo) "" else b.para, if (b.alFuturo) b.abrirEn(hoy) else null) { escribiendo = true }
        }
        BotonPrincipal(if (b.alFuturo) "Sellar carta" else "Guardar carta", Modifier.fillMaxWidth().padding(top = 18.dp),
            habilitado = b.texto.isNotBlank() && (b.alFuturo || b.para.isNotBlank())) {
            val c = Carta(Repo.nuevoId(), hoy, if (b.alFuturo) b.abrirEn(hoy) else hoy, b.texto.trim(), abierta = !b.alFuturo, para = if (b.alFuturo) "" else b.para,
                hora = hora, tema = Estilo.tema.id, lugar = lugar, abierta_el = if (b.alFuturo) null else hoy)
            Repo.guardarCarta(c); sellando = c
        }
        if (b.texto.isNotBlank()) TextButton(onClick = { b.limpiar() }, modifier = Modifier.padding(top = 4.dp)) { Text("descartar el borrador", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
    if (escribiendo) DialogoCarta(lugar, hora) { escribiendo = false }
    sellando?.let { c -> SelladoCarta(c) { b.limpiar(); sellando = null; nav.atras() } }
}

@Composable
private fun OpcionCarta(titulo: String, detalle: String, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(onClick = onClick, shape = MaterialTheme.shapes.small, color = if (activa) cs.primaryContainer else cs.surface,
        border = BorderStroke(if (activa) 2.dp else 1.dp, if (activa) cs.primary else cs.outline), modifier = modifier.height(108.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = if (activa) cs.primary else cs.onSurface)
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

/** Una carta abierta a pantalla completa: PDF con el diseño del tema, compartir y borrar. */
@Composable
fun PantallaCarta(nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val c = Repo.cartas.firstOrNull { it.id == Seleccion.carta }
    var borrar by remember { mutableStateOf(false) }
    var editando by remember { mutableStateOf(false) }
    if (c == null || c.sellada()) {
        Hoja("Carta", nav.atras) { Vacio(if (c == null) "Esta carta ya no existe." else "Sigue sellada hasta el ${Fechas.larga(c.abrir_en)}.") }
        return
    }
    LecturaCarta(c, nav.atras, listOfNotNull(
        "PDF de la carta" to {
            scope.launch {
                val f = runCatching { withContext(Dispatchers.IO) { LibroDiario.cartaPdf(ctx, c) } }.getOrNull()
                if (f != null) LibroDiario.compartir(ctx, f, "application/pdf")
                else android.widget.Toast.makeText(ctx, "No se pudo generar el PDF.", android.widget.Toast.LENGTH_LONG).show()
            }
            Unit
        },
        "Compartir texto" to {
            val t = (if (c.alFuturo) "" else "Para @${c.para}\n") + Fechas.larga(c.escrita) + (if (c.hora.isNotBlank()) " · ${c.hora}" else "") + "\n\n" + c.texto.trim()
            ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, t), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        },
        if (!c.alFuturo) "Editar" to { editando = true } else null,
        "Borrar" to { borrar = true },
    ))
    if (editando) {
        var texto by remember { mutableStateOf(c.texto) }
        androidx.compose.ui.window.Dialog(onDismissRequest = { Repo.guardarCarta(c.copy(texto = texto)); editando = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)) {
            io.github.ruddy2003.vestigium.ui.EscritorTema(c.escrita, texto, { texto = it }, { Repo.guardarCarta(c.copy(texto = texto)); editando = false },
                modo = Escrito.CARTA, encabezado = { p -> io.github.ruddy2003.vestigium.ui.EncabezadoCarta(Estilo.tema, p, c.escrita, c.hora, c.lugar, c.para, null) }, textoBoton = "listo")
        }
    }
    if (borrar) Dialogo(onDismissRequest = { borrar = false },
        title = { Text("¿Borrar esta carta?") }, text = { Text("Tendrás unos segundos para deshacerlo.") },
        confirmButton = { TextButton(onClick = { Repo.borrarCarta(c.id); borrar = false; nav.atras(); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Carta borrada") { Repo.guardarCarta(c) } }) { Text("Borrar", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = { borrar = false }) { Text("Cancelar") } })
}
