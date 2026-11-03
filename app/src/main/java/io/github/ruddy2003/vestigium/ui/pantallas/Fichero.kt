package io.github.ruddy2003.vestigium.ui.pantallas

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.materia
import io.github.ruddy2003.vestigium.ui.rememberSentir

/** Lo que se ve de un cajón: su tema ("" = sin tema), cuántas fichas tiene y la fecha de la más reciente. */
private class Cajon(val tema: String, val n: Int, val ultima: String?)

/** Los cajones del fichero: uno por tema, del más lleno al más vacío, y al final el de las ideas sin tema. */
private fun cajones(ideas: List<Idea>): List<Cajon> {
    val porTema = Ideario.temas(ideas).map { (t, n) -> Cajon(t, n, Ideario.deTema(ideas, t).firstOrNull()?.fecha) }
    val sin = Ideario.deTema(ideas, null)
    return porTema + (if (sin.isNotEmpty()) listOf(Cajon("", sin.size, sin.firstOrNull()?.fecha)) else emptyList())
}

/** Cómo se lee el nombre de un cajón. */
private fun rotuloCajon(tema: String) = if (tema.isEmpty()) "sin tema" else "#" + tema.replace('_', ' ')

/**
 * El Ideario como mueble de fichero: cada tema es un cajón con su portaetiquetas (el tema, el símbolo del tema y cuántas fichas
 * tiene), y asoman más fichas cuanto más lleno está. Los colores son los del objeto del tema ([materia]): madera, latón, papel.
 * Con [max], solo los cajones más llenos (la vista corta de la pestaña Diario). Tocar un cajón lo abre ([PantallaCajonIdeas]).
 */
@Composable
fun FicheroIdeas(nav: Nav, ideas: List<Idea> = Repo.ideas, max: Int? = null, modifier: Modifier = Modifier) {
    val sentir = rememberSentir()
    val todos = remember(ideas) { cajones(ideas) }
    val lista = if (max != null) todos.take(max) else todos
    val m = materia(Estilo.tema)
    val tema = Estilo.tema
    if (lista.isEmpty()) return
    // El mueble: un marco de madera con los cajones en dos columnas.
    Surface(color = m.sombra, shape = MaterialTheme.shapes.small, shadowElevation = 3.dp, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.background(m.cuerpo).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            lista.chunked(2).forEach { fila ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fila.forEach { c ->
                        BoxWithConstraints(Modifier.weight(1f).aspectRatio(1.55f).clickable {
                            sentir(Sentidos.Evento.ABRIR); Seleccion.cajon = c.tema; Seleccion.fichaIdea = null; nav.ir("cajon_ideas")
                        }) {
                            Canvas(Modifier.fillMaxSize()) {
                                val w = size.width; val h = size.height
                                // Las fichas que asoman por arriba: más cuanto más lleno (hasta 5).
                                val asoman = c.n.coerceIn(1, 5)
                                for (k in 0 until asoman) {
                                    val x0 = w * (0.12f + k * 0.035f); val alto = h * (0.1f + 0.02f * (asoman - k))
                                    drawRect(lerp(m.hoja, Color.Black, 0.06f * k), Offset(x0, 0f), Size(w * 0.76f - k * w * 0.02f, alto))
                                }
                                // El frente del cajón, con su bisel.
                                val top = h * 0.16f
                                drawRoundRect(m.sombra, Offset(0f, top), Size(w, h - top), CornerRadius(4f))
                                drawRoundRect(lerp(m.cuerpo, m.brillo, 0.25f), Offset(3f, top + 3f), Size(w - 6f, h - top - 6f), CornerRadius(3f))
                                drawRoundRect(m.sombra.copy(alpha = 0.6f), Offset(w * 0.06f, top + h * 0.06f), Size(w * 0.88f, h - top - h * 0.12f), CornerRadius(3f), style = Stroke(1.2f))
                                // El portaetiquetas de latón con su cartulina.
                                val lx = w * 0.18f; val ly = top + h * 0.12f; val lw = w * 0.64f; val lh = h * 0.36f
                                drawRoundRect(m.detalle, Offset(lx - 3f, ly - 3f), Size(lw + 6f, lh + 6f), CornerRadius(3f))
                                drawRect(m.hoja, Offset(lx, ly), Size(lw, lh))
                                // El tirador, debajo de la etiqueta, y el símbolo del tema en una esquina.
                                drawRoundRect(m.detalle, Offset(w * 0.4f, ly + lh + h * 0.08f), Size(w * 0.2f, h * 0.07f), CornerRadius(h * 0.035f))
                                drawPath(simboloCalificacion(tema, Offset(w * 0.9f, top + h * 0.13f), h * 0.06f), m.acento)
                            }
                            // El texto, sobre la cartulina (que empieza al 28 % del alto y ocupa el 36 %).
                            Column(Modifier.align(Alignment.TopCenter).fillMaxWidth(0.62f).padding(top = maxHeight * 0.28f).height(maxHeight * 0.36f),
                                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(rotuloCajon(c.tema), style = MaterialTheme.typography.labelLarge, color = m.tinta, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    fontStyle = if (c.tema.isEmpty()) FontStyle.Italic else FontStyle.Normal, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                Text("${c.n} ${if (c.n == 1) "ficha" else "fichas"}", style = MaterialTheme.typography.labelSmall, color = m.tinta.copy(alpha = 0.75f))
                            }
                        }
                    }
                    if (fila.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
    if (max != null && todos.size > max)
        Text("y ${todos.size - max} cajones más", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
}

/** Una ficha de cartulina: la línea roja de arriba, renglones azules, la idea y de dónde salió. */
@Composable
private fun FichaIdea(i: Idea, numero: Int, total: Int, tema: String) {
    val m = materia(Estilo.tema)
    val oscuro = esOscuro()
    val papel = if (oscuro) lerp(m.hoja, Color.Black, 0.55f) else m.hoja
    val tinta = if (oscuro) lerp(m.hoja, Color.White, 0.6f) else m.tinta
    val renglon = 28.dp
    Box(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
        Surface(color = papel, shape = MaterialTheme.shapes.small, shadowElevation = 4.dp, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().drawFicha(renglon, m.acento, tinta).verticalScroll(rememberScrollState()).padding(start = 18.dp, end = 16.dp, top = 12.dp, bottom = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(rotuloCajon(tema), style = MaterialTheme.typography.labelLarge, color = m.acento, modifier = Modifier.weight(1f))
                    Text("${Fechas.corto(i.fecha)}", style = MaterialTheme.typography.labelMedium, color = tinta.copy(alpha = 0.7f))
                }
                Spacer(Modifier.height(16.dp))
                if (i.titulo.isNotBlank()) Text(i.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = tinta)
                TextoMarcado(i.texto, Modifier.padding(top = 6.dp), style = MaterialTheme.typography.bodyLarge.copy(color = tinta))
                val fuente = listOfNotNull(i.quien.joinToString(", ").ifBlank { null },
                    i.obra_id?.let { o -> Repo.obras.firstOrNull { it.id == o }?.titulo } ?: i.fuente.ifBlank { null }, i.donde.ifBlank { null }).joinToString(" · ")
                if (fuente.isNotBlank()) Text("— $fuente", style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic, color = tinta.copy(alpha = 0.8f), modifier = Modifier.padding(top = 12.dp))
                val otros = Ideario.temasDe(i).filter { it != tema }
                val pie = listOfNotNull(Catalogo.ORIGENES_IDEA.firstOrNull { it.id == i.origen }?.nombre?.lowercase(),
                    Catalogo.POSTURAS_IDEA.firstOrNull { it.id == i.postura }?.nombre?.lowercase(),
                    otros.takeIf { it.isNotEmpty() }?.joinToString(" ") { "#" + it.replace('_', ' ') }).joinToString(" · ")
                if (pie.isNotBlank()) Text(pie, style = MaterialTheme.typography.labelSmall, color = tinta.copy(alpha = 0.7f), modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(14.dp))
                Text("ficha $numero de $total", style = MaterialTheme.typography.labelSmall, color = tinta.copy(alpha = 0.6f), textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/** El rayado de la ficha: una línea roja arriba y renglones finos. */
private fun Modifier.drawFicha(renglon: androidx.compose.ui.unit.Dp, roja: Color, tinta: Color): Modifier = this.then(
    Modifier.drawBehind {
        val paso = renglon.toPx()
        val arriba = 44.dp.toPx()
        drawLine(roja.copy(alpha = 0.7f), Offset(0f, arriba), Offset(size.width, arriba), 1.6f)
        var y = arriba + paso
        while (y < size.height) { drawLine(tinta.copy(alpha = 0.12f), Offset(0f, y), Offset(size.width, y), 1f); y += paso }
    })

/**
 * Un cajón abierto: sus fichas, de la más nueva a la más vieja. Se pasa de ficha deslizando (o con las flechas); «editar» abre la
 * idea, y «＋ ficha» anota una idea nueva ya con este tema.
 */
@Composable
fun PantallaCajonIdeas(nav: Nav) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val sentir = rememberSentir()
    val tema = Seleccion.cajon ?: ""
    val fichas = remember(Repo.ideas, tema) { Ideario.deTema(Repo.ideas, tema.ifEmpty { null }) }
    var id by remember { mutableStateOf(Seleccion.fichaIdea ?: fichas.firstOrNull()?.id) }
    val i = fichas.indexOfFirst { it.id == id }.coerceAtLeast(0)
    var adelante by remember { mutableStateOf(true) }
    fun ir(k: Int) {
        val f = fichas.getOrNull(k) ?: return
        adelante = k > i; sentir(Sentidos.Evento.PASAR); id = f.id; Seleccion.fichaIdea = f.id
    }
    val umbral = with(LocalDensity.current) { 90.dp.toPx() }
    Column(Modifier.fillMaxSize().background(lerp(cs.background, Color.Black, if (esOscuro()) 0.25f else 0.06f))) {
        Row(Modifier.fillMaxWidth().padding(top = 6.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = nav.atras) { Text(Estilo.atras, style = MaterialTheme.typography.labelLarge, color = cs.primary) }
            Text("${rotuloCajon(tema)} · ${fichas.size} ${if (fichas.size == 1) "ficha" else "fichas"}", style = MaterialTheme.typography.labelLarge,
                color = cs.onSurfaceVariant, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
            val actual = fichas.getOrNull(i)
            if (actual != null) TextButton(onClick = {
                ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, listOf(actual.titulo, actual.texto).filter { it.isNotBlank() }.joinToString("\n\n")), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }) { Text("compartir", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
        }
        Box(Modifier.weight(1f).fillMaxWidth().pointerInput(fichas) {
            var total = 0f
            detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
                val ahora = fichas.indexOfFirst { it.id == id }
                if (total < -umbral) ir(ahora + 1) else if (total > umbral) ir(ahora - 1)
            }) { _, dx -> total += dx }
        }) {
            if (fichas.isEmpty()) Text("Este cajón está vacío.", style = MaterialTheme.typography.bodyLarge, color = cs.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
            else AnimatedContent(targetState = fichas[i], transitionSpec = {
                // La ficha sale hacia arriba, como cuando se saca del cajón, y entra la siguiente.
                val dir = if (adelante) 1 else -1
                (slideInVertically(tween(320)) { h -> dir * h / 4 } + fadeIn(tween(240))) togetherWith (slideOutVertically(tween(300)) { h -> -dir * h / 3 } + fadeOut(tween(220)))
            }, label = "ficha") { f ->
                FichaIdea(f, fichas.indexOfFirst { it.id == f.id } + 1, fichas.size, tema)
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { ir(i - 1) }, enabled = i > 0) { Text("‹", style = MaterialTheme.typography.titleLarge) }
            Spacer(Modifier.weight(1f))
            fichas.getOrNull(i)?.let { f -> TextButton(onClick = { Seleccion.fichaIdea = f.id; EdicionIdea.abrir(nav, f) }) { Text("editar", style = MaterialTheme.typography.labelLarge) } }
            TextButton(onClick = {
                val n = EdicionIdea.nueva().let { if (tema.isNotEmpty()) it.copy(temas = listOf(tema)) else it }
                EdicionIdea.abrir(nav, n)
            }) { Text("＋ ficha", style = MaterialTheme.typography.labelLarge) }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { ir(i + 1) }, enabled = i in 0 until fichas.size - 1) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
    }
}
