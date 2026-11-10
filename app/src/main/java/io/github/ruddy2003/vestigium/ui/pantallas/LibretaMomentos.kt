@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.IconoPista
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.rememberSentir

/** Un renglón de la hoja: un registro de ánimo o un consumo, con su hora. */
private class Renglon(val ms: Long, val hora: String, val titulo: String, val detalle: String, val texto: String, val valencia: Int?, val icono: String)

private fun cifra(q: Double) = if (q % 1.0 == 0.0) "${q.toInt()}" else "%.1f".format(q)

private fun renglones(fecha: String, discreto: Boolean): List<Renglon> {
    val ms = momentosVisibles().filter { it.fecha == fecha }.map { m ->
        val det = listOfNotNull(m.valencia?.let { "agrado $it/7" }, m.activacion?.let { "energía $it/7" }, m.intensidad?.let { "intensidad $it/5" },
            Catalogo.pensamientosDe(m).takeIf { it.isNotEmpty() && !discreto && Catalogo.emocionesDe(m).isNotEmpty() }?.let { l -> "pensando: " + l.joinToString(", ") { Catalogo.nombrePensamiento(it).lowercase() } },
            m.con_quien.takeIf { it.isNotEmpty() && !discreto }?.let { l -> "con " + l.joinToString(", ") { q -> Catalogo.CON_QUIEN.firstOrNull { it.id == q }?.nombre?.lowercase() ?: q } },
            Repo.lugares.firstOrNull { it.id == m.lugar }?.nombre?.takeIf { !discreto }?.let { "en $it" })
        Renglon(Fechas.millis(m.ts), Fechas.horaDe(m.ts), Catalogo.tituloDetallado(m, discreto), det.joinToString(" · "), m.texto.trim(), m.valencia, "momentos")
    }
    val cs = Repo.consumos.filter { it.fecha == fecha && Sustancias.visible(it) }.map { c ->
        val det = listOfNotNull(Sustancias.maximo(c)?.let { "nivel $it/10" }, c.cantidad?.let { "${cifra(it)} ${Catalogo.sustancia(c.sustancia)?.unidad ?: ""}".trim() },
            Sustancias.horas(c)?.let { "%.1f h".format(it) }, c.cruda?.let { "cruda $it/5" }, c.con_quien.takeIf { it.isNotEmpty() }?.let { "con " + it.joinToString(", ") })
        Renglon(Fechas.millis(c.inicio), Fechas.horaDe(c.inicio), Catalogo.nombreSustancia(c), det.joinToString(" · "), c.nota.trim(), null, if (c.sustancia == "cafeina") "cafeina" else "sustancias")
    }
    return (ms + cs).sortedBy { it.ms }
}

/**
 * El plural del nombre de una hoja del contenedor: «columna» → «columnas», «piel» → «pieles», «inmersión» → «inmersiones»,
 * «página pintada» → «páginas pintadas», «página del carnet» → «páginas del carnet».
 */
internal fun pluralHoja(h: String): String {
    fun plural(p: String) = when {
        p.endsWith("ón") -> p.dropLast(2) + "ones"
        p.last() in "aeiouáéíóú" -> p + "s"
        else -> p + "es"
    }
    val palabras = h.split(" ")
    val corte = palabras.indexOfFirst { it == "de" || it == "del" }.let { if (it < 0) palabras.size else it }
    return palabras.mapIndexed { i, w -> if (i < corte) plural(w) else w }.joinToString(" ")
}

/**
 * La libreta cerrada sobre su soporte, como la dibuja el tema ([io.github.ruddy2003.vestigium.ui.biblioteca.DisenoBiblioteca.libreta]):
 * el objeto de su contenedor ([contenedorDe]), que engorda con cada hoja, con el nombre y la cuenta dentro. Al tocarla se
 * abre y después pasa a la última hoja.
 */
@Composable
internal fun LibretaCerrada(hojas: Int, desde: String?, modifier: Modifier = Modifier, abrir: () -> Unit) {
    val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(Estilo.tema)
    val oscuro = esOscuro()
    val contenedor = io.github.ruddy2003.vestigium.ui.contenedorDe(Estilo.tema)
    val medidor = rememberTextMeasurer()
    val sentir = rememberSentir()
    val alcance = rememberCoroutineScope()
    val abre = remember { Animatable(0f) }
    val animar = Repo.config.animaciones
    val cuenta = "$hojas ${if (hojas == 1) contenedor.hoja else pluralHoja(contenedor.hoja)}" + (desde?.let { " · desde el ${Fechas.corto(it)}" } ?: "")
    Canvas(modifier.fillMaxWidth().height(d.altoLibreta).semantics { contentDescription = contenedor.nombre + ", " + cuenta }.clickable {
        sentir(Sentidos.Evento.ABRIR)
        if (animar) alcance.launch { abre.animateTo(1f, tween(300)); abrir(); abre.snapTo(0f) } else abrir()
    }) {
        clipRect { with(d) { libreta(io.github.ruddy2003.vestigium.ui.biblioteca.EstadoLibreta(contenedor.nombre, cuenta, hojas, abre.value, hojas * 31 + 7), medidor, oscuro) } }
    }
}

/** La pestaña «Momentos» de la biblioteca: la libreta, su índice por mes y el buscador. */
@Composable
fun BibliotecaMomentosLibreta(nav: Nav) {
    val discreto = Repo.config.discreto
    val todas = remember(Repo.momentos, Repo.consumos) { diasConMomentos() }
    var busca by remember { mutableStateOf("") }
    var meses by remember { mutableIntStateOf(3) }
    val sentir = rememberSentir()
    fun abrir(f: String) { sentir(Sentidos.Evento.ABRIR); Seleccion.momentos = f; nav.ir("hoja_momentos") }
    if (todas.isEmpty()) { Vacio("La libreta está en blanco. Toca «¿Cómo estás ahora?» en la pestaña Diario y aquí se irá llenando, una hoja por día."); return }
    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
        LibretaCerrada(todas.size, todas.lastOrNull(), Modifier.fillMaxWidth()) { Seleccion.momentos = todas.first(); nav.ir("hoja_momentos") }
    }
    Text("Toca la libreta para abrirla en la última hoja; adentro se pasa de día deslizando.", style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
    io.github.ruddy2003.vestigium.ui.biblioteca.CampoTema(busca, { busca = it }, "buscar en tus momentos", Modifier.padding(top = 14.dp))
    val q = normalLibreta(busca.trim())
    val visibles = if (q.isBlank()) todas else todas.filter { f -> normalLibreta(hojaMomentos(f, discreto)?.nota.orEmpty()).contains(q) }
    if (q.isNotBlank() && visibles.isEmpty()) Vacio("Ninguna hoja tiene eso.")
    // El índice: cada mes con su rótulo y sus días como pestañas del tema (con el color del ánimo medio del día).
    val oscuro = esOscuro()
    val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(Estilo.tema)
    val medidor = rememberTextMeasurer()
    val porMes = visibles.groupBy { it.take(7) }.toSortedMap(compareByDescending { it })
    porMes.entries.take(if (q.isBlank()) meses else 24).forEach { (mes, dias) ->
        val rotulo = Estilo.mesConComun(mes.drop(5).toInt(), mes.take(4).toInt())
        Canvas(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp).height(34.dp).semantics { contentDescription = rotulo }) {
            clipRect { with(d) { rotuloMes(rotulo, medidor, oscuro) } }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            dias.sorted().forEach { f ->
                val v = momentosVisibles().filter { it.fecha == f && Catalogo.esRegistro(it) }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
                val dia = f.takeLast(2).trimStart('0')
                val semana = Fechas.parse(f).dayOfWeek.getDisplayName(java.time.format.TextStyle.NARROW, java.util.Locale("es"))
                val e = io.github.ruddy2003.vestigium.ui.biblioteca.EstadoPestana(dia, semana, Tonos.valencia(v, oscuro), f.hashCode())
                Canvas(Modifier.size(44.dp, 56.dp).semantics { contentDescription = Fechas.corto(f) }.clickable { abrir(f) }) {
                    with(d) { pestana(e, medidor, oscuro) }
                }
            }
        }
    }
    if (q.isBlank() && porMes.size > meses) BotonTexto(onClick = { meses += 6 }) { Text("ver más meses", style = MaterialTheme.typography.labelLarge) }
}

private fun normalLibreta(s: String) = java.text.Normalizer.normalize(s.lowercase(), java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

/** La hoja de un día: papel rayado con margen, el resorte a la izquierda y la línea del día arriba. */
@Composable
private fun HojaLibreta(fecha: String, numero: Int, total: Int, discreto: Boolean) {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val contenedor = io.github.ruddy2003.vestigium.ui.contenedorDe(Estilo.tema)
    val papel = io.github.ruddy2003.vestigium.ui.Contenedores.papel(contenedor, cs, oscuro)
    val renglon = 30.dp
    val l = remember(fecha, Repo.momentos, Repo.consumos, discreto) { renglones(fecha, discreto) }
    val vals = l.mapNotNull { it.valencia }
    Box(Modifier.fillMaxSize().padding(start = 6.dp, end = 10.dp, top = 4.dp, bottom = 4.dp)) {
        Surface(color = papel, shape = MaterialTheme.shapes.medium, shadowElevation = 3.dp, modifier = Modifier.fillMaxSize().padding(start = 12.dp)) {
            Column(Modifier.fillMaxSize().drawBehind {
                // Renglones y margen (o fibras, cera, reglado, cuadrícula Seyès… según el contenedor del tema).
                with(io.github.ruddy2003.vestigium.ui.Contenedores) { fondoHoja(contenedor, cs, oscuro, renglon.toPx(), 64.dp.toPx()) }
            }.verticalScroll(rememberScrollState()).padding(start = 26.dp, end = 18.dp, top = 18.dp, bottom = 24.dp)) {
                Text(Estilo.fecha(fecha), style = MaterialTheme.typography.titleLarge, color = cs.primary, modifier = Modifier.padding(start = 46.dp))
                Text(Fechas.titulo(fecha).lowercase() + " · " + (if (l.size == 1) "1 registro" else "${l.size} registros") +
                    (vals.takeIf { it.isNotEmpty() }?.let { " · ánimo medio %.1f".format(it.average()) } ?: ""),
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(start = 46.dp))
                // El día de 5:00 a 5:00, con un punto por registro del color de su ánimo.
                Canvas(Modifier.fillMaxWidth().padding(start = 46.dp, top = 8.dp).height(16.dp)) {
                    val ini = Fechas.inicioDiaLogico(fecha)
                    drawLine(cs.outline.copy(alpha = 0.6f), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1f * density)
                    for (h in 0..24 step 6) { val x = h / 24f * size.width; drawLine(cs.outline, Offset(x, size.height / 2 - 3 * density), Offset(x, size.height / 2 + 3 * density), 1f * density) }
                    l.forEach { r ->
                        val x = ((r.ms - ini).toFloat() / (24 * 3_600_000f)).coerceIn(0f, 1f) * size.width
                        drawCircle(Tonos.valencia(r.valencia?.toDouble(), oscuro) ?: cs.secondary, 4.5f * density, Offset(x, size.height / 2))
                    }
                }
                Spacer(Modifier.height(14.dp))
                if (l.isEmpty()) Text("Hoja en blanco.", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(start = 46.dp))
                l.forEach { r ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Text(r.hora, style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.width(46.dp))
                        Column(Modifier.weight(1f).padding(start = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val tono = Tonos.valencia(r.valencia?.toDouble(), oscuro)
                                if (tono != null) Box(Modifier.size(10.dp).background(tono, MaterialTheme.shapes.extraLarge))
                                else IconoPista(r.icono, cs.secondary, tam = 14.dp)
                                Text(r.titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
                            }
                            if (r.detalle.isNotBlank()) Text(r.detalle, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant)
                            if (r.texto.isNotBlank()) TextoMarcado(r.texto, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("${contenedor.hoja} $numero de $total", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        }
        // La encuadernación encima del borde izquierdo: el resorte, la varilla del rollo, los cordones, la costura…
        Canvas(Modifier.fillMaxSize()) { with(io.github.ruddy2003.vestigium.ui.Contenedores) { encuadernacion(contenedor, cs, oscuro) } }
    }
}

/**
 * La libreta abierta: una hoja por día. Se pasa de hoja deslizando (o con las flechas), con un giro de página;
 * la más nueva es la última.
 */
@Composable
fun PantallaHojaMomentos(nav: Nav) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val discreto = Repo.config.discreto
    val sentir = rememberSentir()
    // De la más vieja a la más nueva, como las hojas de una libreta.
    val todas = remember(Repo.momentos, Repo.consumos) { diasConMomentos().sorted() }
    var fecha by remember { mutableStateOf(Seleccion.momentos ?: todas.lastOrNull() ?: Fechas.diaLogico()) }
    var adelante by remember { mutableStateOf(true) }
    val i = todas.indexOf(fecha)
    fun ir(k: Int) {
        val f = todas.getOrNull(k) ?: return
        // La hoja de ahora se lee del estado (no de cuando se armó el gesto): así, ir hacia lo más nuevo siempre pasa la hoja a la izquierda.
        adelante = k > todas.indexOf(fecha); sentir(Sentidos.Evento.PASAR); fecha = f; Seleccion.momentos = f
    }
    val umbral = with(LocalDensity.current) { 90.dp.toPx() }
    Column(Modifier.fillMaxSize().background(lerp(cs.background, Color.Black, if (esOscuro()) 0.25f else 0.06f))) {
        // Las barras de arriba y de abajo llevan la franja del tema (la misma de la página del diario).
        val p = io.github.ruddy2003.vestigium.ui.pagina(Estilo.tema)
        val d = io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca(Estilo.tema)
        val oscuro = esOscuro()
        val franja = Modifier.drawBehind { with(d) { barra(p, oscuro, false) } }
        @Composable fun Boton(texto: String, activo: Boolean = true, al: () -> Unit) = Text(texto, style = MaterialTheme.typography.labelLarge,
            color = if (activo) p.acento else p.tenue.copy(alpha = 0.45f), maxLines = 1,
            modifier = Modifier.clickable(enabled = activo, onClick = al).padding(horizontal = 10.dp, vertical = 10.dp))
        Row(Modifier.fillMaxWidth().then(franja).padding(top = 6.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Boton(Estilo.atras) { nav.atras() }
            Text(io.github.ruddy2003.vestigium.ui.contenedorDe(Estilo.tema).nombre,
                style = MaterialTheme.typography.labelLarge, color = p.tenue, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1)
            Boton("compartir") {
                val t = hojaMomentos(fecha, discreto)?.nota
                if (t != null) ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "Momentos · ${Fechas.titulo(fecha)}\n\n$t"), "Compartir")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth().pointerInput(todas) {
            var total = 0f
            detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
                // A la izquierda, la hoja siguiente (más nueva); a la derecha, la anterior.
                val ahora = todas.indexOf(fecha)
                if (total < -umbral) ir(ahora + 1) else if (total > umbral) ir(ahora - 1)
            }) { _, dx -> total += dx }
        }) {
            AnimatedContent(targetState = fecha, transitionSpec = {
                val dir = if (adelante) 1 else -1
                (slideInHorizontally(tween(340)) { w -> dir * w } + fadeIn(tween(240))) togetherWith
                    (slideOutHorizontally(tween(340)) { w -> -dir * w / 3 } + fadeOut(tween(260)) + scaleOut(tween(340), targetScale = 0.94f))
            }, label = "hoja") { f ->
                HojaLibreta(f, todas.indexOf(f) + 1, todas.size, discreto)
            }
        }
        Row(Modifier.fillMaxWidth().then(franja).padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Boton("‹ " + (todas.getOrNull(i - 1)?.let { Fechas.corto(it) } ?: ""), i > 0) { ir(i - 1) }
            Spacer(Modifier.weight(1f))
            Boton("abrir el día") { Seleccion.abrir(nav, fecha) }
            Spacer(Modifier.weight(1f))
            Boton((todas.getOrNull(i + 1)?.let { Fechas.corto(it) } ?: "") + " ›", i in 0 until todas.size - 1) { ir(i + 1) }
        }
    }
}
