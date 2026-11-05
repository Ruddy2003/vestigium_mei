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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ciencia.Totales
import io.github.ruddy2003.vestigium.ui.ArchivoIdeas
import io.github.ruddy2003.vestigium.ui.ETIQUETA_ARCHIVO
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.FormaFicha
import io.github.ruddy2003.vestigium.ui.InteriorArchivo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.PapelFicha
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.adornoFicha
import io.github.ruddy2003.vestigium.ui.altoBarraVentana
import io.github.ruddy2003.vestigium.ui.archivoIdeas
import io.github.ruddy2003.vestigium.ui.colorEtiqueta
import io.github.ruddy2003.vestigium.ui.dibujarContenedor
import io.github.ruddy2003.vestigium.ui.esquemaPagina
import io.github.ruddy2003.vestigium.ui.formaDeFicha
import io.github.ruddy2003.vestigium.ui.margenLado
import io.github.ruddy2003.vestigium.ui.margenSuperior
import io.github.ruddy2003.vestigium.ui.materia
import io.github.ruddy2003.vestigium.ui.paginaFicha
import io.github.ruddy2003.vestigium.ui.rayadoFicha
import io.github.ruddy2003.vestigium.ui.rememberSentir
import io.github.ruddy2003.vestigium.ui.tintaInterior

/** Lo que se ve de un cajón: su tema ("" = sin tema), cuántas fichas tiene y la fecha de la más reciente. */
private class Cajon(val tema: String, val n: Int, val ultima: String?)

/** Los cajones del fichero: uno por tema, del más lleno al más vacío, y al final el de las ideas sin tema. */
private fun cajones(ideas: List<Idea>): List<Cajon> {
    val porTema = Ideario.temas(ideas).map { (t, n) -> Cajon(t, n, Ideario.deTema(ideas, t).firstOrNull()?.fecha) }
    val sin = Ideario.deTema(ideas, null)
    return porTema + (if (sin.isNotEmpty()) listOf(Cajon("", sin.size, sin.firstOrNull()?.fecha)) else emptyList())
}

/** Cómo se lee el nombre de un cajón. */
internal fun rotuloCajon(tema: String) = if (tema.isEmpty()) "sin tema" else "#" + tema.replace('_', ' ')

/**
 * El Ideario como el archivo del tema ([archivoIdeas]): cada tema del Ideario es un contenedor (un cajón, una capsa, un arcón,
 * un directorio…) con su etiqueta, y asoman más fichas cuanto más lleno está. Con [max], solo los más llenos (la vista corta de
 * la pestaña Diario). Tocar uno lo abre ([PantallaCajonIdeas]).
 */
@Composable
fun FicheroIdeas(nav: Nav, ideas: List<Idea> = Repo.ideas, max: Int? = null, modifier: Modifier = Modifier) {
    val sentir = rememberSentir()
    val todos = remember(ideas) { cajones(ideas) }
    val lista = if (max != null) todos.take(max) else todos
    val tema = Estilo.tema
    val m = materia(tema)
    val a = archivoIdeas(tema)
    if (lista.isEmpty()) return
    val (_, tintaEtiqueta) = colorEtiqueta(a, m)
    // En una columna propia: así el «y N más» va debajo también donde el fichero no está dentro de otra columna.
    Column(modifier.fillMaxWidth()) {
    Surface(shape = MaterialTheme.shapes.small, shadowElevation = 3.dp, color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
        Box {
            InteriorArchivo(a, m, Modifier.matchParentSize())
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                lista.chunked(2).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        fila.forEach { c ->
                            BoxWithConstraints(Modifier.weight(1f).aspectRatio(1.55f).clickable {
                                sentir(Sentidos.Evento.ABRIR); Seleccion.cajon = c.tema; Seleccion.fichaIdea = null; nav.ir("cajon_ideas")
                            }) {
                                Canvas(Modifier.fillMaxSize()) { dibujarContenedor(a, m, tema, c.n) }
                                // El texto, sobre la etiqueta (la misma zona en todas las formas).
                                Column(Modifier.align(Alignment.TopCenter).fillMaxWidth(ETIQUETA_ARCHIVO.width * 0.96f)
                                    .padding(top = maxHeight * ETIQUETA_ARCHIVO.top).height(maxHeight * ETIQUETA_ARCHIVO.height),
                                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Text(rotuloCajon(c.tema), style = MaterialTheme.typography.labelLarge, color = tintaEtiqueta, maxLines = 1, overflow = TextOverflow.Ellipsis,
                                        fontStyle = if (c.tema.isEmpty()) FontStyle.Italic else FontStyle.Normal, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                                    Text(a.cuenta(c.n), style = MaterialTheme.typography.labelSmall, color = tintaEtiqueta.copy(alpha = 0.75f), maxLines = 1)
                                }
                            }
                        }
                        if (fila.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
    if (max != null && todos.size > max)
        Text("y ${todos.size - max} ${if (todos.size - max == 1) a.contenedor else a.contenedores} más", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
    }
}

/** Lo que dice la ficha de dónde salió: quién, la obra o la fuente, y el minuto o la página. */
private fun fuenteDe(i: Idea): String = listOfNotNull(i.quien.joinToString(", ").ifBlank { null },
    i.obra_id?.let { o -> Repo.obras.firstOrNull { it.id == o }?.titulo } ?: i.fuente.ifBlank { null }, i.donde.ifBlank { null }).joinToString(" · ")

@Composable
private fun colorPosturaFicha(id: String, p: Pagina): Color? = when (id) {
    "convence" -> p.acento; "piensa" -> p.detalle; "duda" -> p.tenue; "desacuerdo" -> MaterialTheme.colorScheme.error; else -> null
}

/** El encabezado de una ficha: el tema y la fecha; en la ficha-ventana van dentro de la barra de título, como el nombre del archivo. */
@Composable
private fun EncabezadoFicha(i: Idea, rotulo: String, a: ArchivoIdeas, p: Pagina, grande: Boolean, modifier: Modifier = Modifier) {
    val ventana = a.formaFicha == FormaFicha.VENTANA
    val color = if (ventana) p.color else p.acento
    val estilo = if (grande) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall
    Row(modifier.fillMaxWidth().let { if (ventana) it.height(altoBarraVentana(grande)) else it }, verticalAlignment = Alignment.CenterVertically) {
        val nombre = if (ventana) rotulo.removePrefix("#").replace(' ', '_') + "/" + (i.titulo.ifBlank { i.texto }.take(14).trim().replace(' ', '_').lowercase().ifBlank { "idea" }) + ".md" else rotulo
        Text((if (i.fijada) "◆ " else "") + nombre, style = estilo, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Text(Fechas.corto(i.fecha), style = estilo, color = color.copy(alpha = 0.75f), maxLines = 1,
            modifier = Modifier.padding(end = if (ventana) (if (grande) 64.dp else 34.dp) else 0.dp))
    }
}

/**
 * Una ficha chica, para ver varias a la vez: el papel del tema con la forma de sus fichas (tablilla, tanzaku, boleto, expediente,
 * ventana, laja o recorte), el tema y la fecha arriba, la línea de color, la idea en una línea y unos renglones del texto. Abajo,
 * el color de cómo la tienes. [rotulo] es lo que va en la pestaña (el tema del cajón, o el primero de la idea).
 */
@Composable
fun FichaMini(i: Idea, modifier: Modifier = Modifier, rotulo: String? = null, lineas: Int = 4, onClick: () -> Unit) {
    val t = Estilo.tema
    val a = archivoIdeas(t); val p = paginaFicha(t); val m = materia(t)
    val forma = remember(i.id, a.formaFicha) { formaDeFicha(a.formaFicha, i.id.hashCode()) }
    val tam = (p.tam * 0.74f).sp; val inter = (p.interlinea * 0.66f).sp
    val paso = with(LocalDensity.current) { inter.toDp() }
    val sup = margenSuperior(a.formaFicha); val lado = margenLado(a.formaFicha)
    val encabezado = if (a.formaFicha == FormaFicha.VENTANA) altoBarraVentana() else 16.dp
    val texto = rotulo ?: Ideario.temasDe(i).firstOrNull()?.let { rotuloCajon(it) } ?: "idea"
    // Los colores de la página (también las marcas de @ y #): sobre el papel claro, los del modo oscuro de la app no se leen.
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
    Surface(shape = forma, color = Color.Transparent, shadowElevation = 3.dp, modifier = modifier.clickable(onClick = onClick)) {
        Box {
            PapelFicha(p)
            Canvas(Modifier.matchParentSize()) { adornoFicha(a.formaFicha, p, m) }
            // La línea de color va justo debajo del encabezado tal como quedó medido: con letras altas (Art déco) el
            // encabezado pasa de los 16 dp y la línea caía encima del título.
            val densidad = LocalDensity.current
            var altoEncabezado by remember { mutableStateOf(encabezado) }
            Column(Modifier.fillMaxSize().rayadoFicha(p, sup + maxOf(encabezado, altoEncabezado) + 2.dp, paso).padding(start = lado, end = lado, top = sup, bottom = 6.dp)) {
                EncabezadoFicha(i, texto, a, p, grande = false, modifier = Modifier.onSizeChanged { altoEncabezado = with(densidad) { it.height.toDp() } })
                Spacer(Modifier.height(4.dp))
                val estilo = TextStyle(fontFamily = p.texto, fontSize = tam, lineHeight = inter, color = p.tinta)
                if (i.titulo.isNotBlank()) Text(i.titulo, style = estilo.copy(fontWeight = FontWeight.SemiBold), maxLines = 2, overflow = TextOverflow.Ellipsis)
                val resto = (lineas - if (i.titulo.isNotBlank()) 1 else 0).coerceAtLeast(1)
                if (i.texto.isNotBlank()) TextoMarcado(i.texto.trim(), style = estilo, maxLines = resto, overflow = TextOverflow.Ellipsis)
            }
            Row(Modifier.align(Alignment.BottomEnd).padding(end = lado, bottom = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                colorPosturaFicha(i.postura, p)?.let { c -> Text("●", style = MaterialTheme.typography.labelSmall, color = c) }
            }
        }
    }
    }
}

/** La ficha entera: el papel del tema con su forma, el texto completo, de dónde salió, los otros temas y en qué lugar del cajón va. */
@Composable
private fun FichaGrande(i: Idea, numero: Int, total: Int, tema: String, a: ArchivoIdeas) {
    val t = Estilo.tema
    val p = paginaFicha(t); val m = materia(t)
    val forma = remember(i.id, a.formaFicha) { formaDeFicha(a.formaFicha, i.id.hashCode()) }
    val paso = with(LocalDensity.current) { p.interlinea.sp.toDp() }
    val sup = margenSuperior(a.formaFicha, true); val lado = margenLado(a.formaFicha, true)
    val encabezado = if (a.formaFicha == FormaFicha.VENTANA) altoBarraVentana(true) else 22.dp
    Box(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 8.dp)) {
        MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
        Surface(shape = forma, color = Color.Transparent, shadowElevation = 6.dp, modifier = Modifier.fillMaxSize()) {
            Box {
                PapelFicha(p)
                Canvas(Modifier.matchParentSize()) { adornoFicha(a.formaFicha, p, m, grande = true) }
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                    val densidad = LocalDensity.current
                    var altoEncabezado by remember { mutableStateOf(encabezado) }
                    Column(Modifier.fillMaxWidth().rayadoFicha(p, sup + maxOf(encabezado, altoEncabezado) + 4.dp, paso).padding(start = lado, end = lado, top = sup, bottom = 22.dp)) {
                        EncabezadoFicha(i, rotuloCajon(tema), a, p, grande = true, modifier = Modifier.onSizeChanged { altoEncabezado = with(densidad) { it.height.toDp() } })
                        Spacer(Modifier.height(6.dp))
                        val estilo = TextStyle(fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, color = p.tinta)
                        if (i.titulo.isNotBlank()) Text(i.titulo, style = estilo.copy(fontWeight = FontWeight.SemiBold))
                        TextoMarcado(i.texto.trim(), style = estilo)
                        val fuente = fuenteDe(i)
                        if (fuente.isNotBlank()) Text("— $fuente", style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic, color = p.tinta.copy(alpha = 0.8f), modifier = Modifier.padding(top = 12.dp))
                        val otros = Ideario.temasDe(i).filter { it != tema }
                        val pie = listOfNotNull(Catalogo.ORIGENES_IDEA.firstOrNull { it.id == i.origen }?.nombre?.lowercase(),
                            Catalogo.POSTURAS_IDEA.firstOrNull { it.id == i.postura }?.nombre?.lowercase(),
                            otros.takeIf { it.isNotEmpty() }?.joinToString(" ") { "#" + it.replace('_', ' ') }).joinToString(" · ")
                        if (pie.isNotBlank()) Text(pie, style = MaterialTheme.typography.labelSmall, color = p.tenue, modifier = Modifier.padding(top = 8.dp))
                        Spacer(Modifier.height(14.dp))
                        Text("${a.ficha} $numero de $total", style = MaterialTheme.typography.labelSmall, color = p.tenue, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        }
    }
}

/** La barra de arriba sobre el interior del contenedor: atrás, el título en el centro y lo de la derecha. */
@Composable
private fun BarraArchivo(tinta: Color, volver: () -> Unit, titulo: String, derecha: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        TextButton(onClick = volver) { Text(Estilo.atras, style = MaterialTheme.typography.labelLarge, color = tinta) }
        Text(titulo, style = MaterialTheme.typography.labelLarge, color = tinta.copy(alpha = 0.85f), modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        derecha()
    }
}

/** Una idea nueva ya con el tema del cajón abierto. */
private fun nuevaEnCajon(nav: Nav, tema: String) {
    val n = EdicionIdea.nueva().let { if (tema.isNotEmpty()) it.copy(temas = listOf(tema)) else it }
    EdicionIdea.abrir(nav, n)
}

/**
 * Un contenedor abierto: sus fichas en cuadrícula (dos columnas; tres si son tiras), de la más nueva a la más vieja, sobre el
 * interior del contenedor del tema. Se busca dentro, y tocar una la abre entera ([PantallaFichaIdea]).
 */
@Composable
fun PantallaCajonIdeas(nav: Nav) {
    val t = Estilo.tema
    val a = archivoIdeas(t); val m = materia(t)
    val tinta = tintaInterior(a, m)
    val sentir = rememberSentir()
    val tema = Seleccion.cajon ?: ""
    val fichas = remember(Repo.ideas, tema) { Ideario.deTema(Repo.ideas, tema.ifEmpty { null }) }
    var buscando by remember { mutableStateOf(false) }
    var q by remember { mutableStateOf("") }
    val nq = Totales.normal(q.trim())
    val visibles = if (nq.isBlank()) fichas else fichas.filter { Totales.normal(Ideario.textoBuscable(it, Repo.obras)).contains(nq) }
    val tira = a.formaFicha == FormaFicha.TIRA
    Box(Modifier.fillMaxSize()) {
        InteriorArchivo(a, m)
        Column(Modifier.fillMaxSize()) {
            BarraArchivo(tinta, nav.atras, "${rotuloCajon(tema)} · ${a.cuenta(fichas.size)}") {
                TextButton(onClick = { buscando = !buscando; if (!buscando) q = "" }) { Text(if (buscando) "cerrar" else "buscar", style = MaterialTheme.typography.labelLarge, color = tinta) }
            }
            if (buscando) BasicTextField(q, { q = it }, singleLine = true, cursorBrush = SolidColor(tinta),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = tinta),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                decorationBox = { campo ->
                    Column {
                        Box { if (q.isEmpty()) Text("buscar en ${rotuloCajon(tema)}…", style = MaterialTheme.typography.bodyLarge, color = tinta.copy(alpha = 0.5f)); campo() }
                        Box(Modifier.fillMaxWidth().padding(top = 4.dp).height(1.dp).background(tinta.copy(alpha = 0.4f)))
                    }
                })
            if (fichas.isEmpty()) Box(Modifier.weight(1f).fillMaxWidth()) {
                Text(a.vacio, style = MaterialTheme.typography.bodyLarge, color = tinta.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Center))
            } else LazyVerticalGrid(GridCells.Fixed(if (tira) 3 else 2), Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (visibles.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Nada coincide.", style = MaterialTheme.typography.bodyMedium, color = tinta.copy(alpha = 0.8f), textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
                items(visibles, key = { it.id }) { f ->
                    FichaMini(f, Modifier.fillMaxWidth().aspectRatio(if (tira) 0.5f else 1.2f), rotulo = rotuloCajon(tema), lineas = if (tira) 9 else 4) {
                        sentir(Sentidos.Evento.ABRIR); Seleccion.fichaIdea = f.id; nav.ir("ficha_idea")
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { nuevaEnCajon(nav, tema) }) { Text("＋ ${a.ficha}", style = MaterialTheme.typography.labelLarge, color = tinta) }
            }
        }
    }
}

/**
 * Una ficha abierta, entera, sobre el interior de su contenedor. Se pasa a la siguiente deslizando (o con las flechas); «editar»
 * la abre para escribir, y «＋» anota otra con el mismo tema.
 */
@Composable
fun PantallaFichaIdea(nav: Nav) {
    val ctx = LocalContext.current
    val t = Estilo.tema
    val a = archivoIdeas(t); val m = materia(t)
    val tinta = tintaInterior(a, m)
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
    Box(Modifier.fillMaxSize()) {
        InteriorArchivo(a, m)
        Column(Modifier.fillMaxSize()) {
            val actual = fichas.getOrNull(i)
            BarraArchivo(tinta, nav.atras, rotuloCajon(tema)) {
                if (actual != null) TextButton(onClick = {
                    ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, listOf(actual.titulo, actual.texto).filter { it.isNotBlank() }.joinToString("\n\n")), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }) { Text("compartir", style = MaterialTheme.typography.labelLarge, color = tinta) }
            }
            Box(Modifier.weight(1f).fillMaxWidth().pointerInput(fichas) {
                var total = 0f
                detectHorizontalDragGestures(onDragStart = { total = 0f }, onDragEnd = {
                    val ahora = fichas.indexOfFirst { it.id == id }
                    if (total < -umbral) ir(ahora + 1) else if (total > umbral) ir(ahora - 1)
                }) { _, dx -> total += dx }
            }) {
                if (fichas.isEmpty()) Text(a.vacio, style = MaterialTheme.typography.bodyLarge, color = tinta.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Center))
                else AnimatedContent(targetState = fichas[i], transitionSpec = {
                    // La ficha sale hacia arriba, como cuando se saca del contenedor, y entra la siguiente.
                    val dir = if (adelante) 1 else -1
                    (slideInVertically(tween(320)) { h -> dir * h / 4 } + fadeIn(tween(240))) togetherWith (slideOutVertically(tween(300)) { h -> -dir * h / 3 } + fadeOut(tween(220)))
                }, label = "ficha") { f ->
                    FichaGrande(f, fichas.indexOfFirst { it.id == f.id } + 1, fichas.size, tema, a)
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { ir(i - 1) }, enabled = i > 0) { Text("‹", style = MaterialTheme.typography.titleLarge, color = tinta.copy(alpha = if (i > 0) 1f else 0.3f)) }
                Spacer(Modifier.weight(1f))
                actual?.let { f -> TextButton(onClick = { Seleccion.fichaIdea = f.id; EdicionIdea.abrir(nav, f) }) { Text("editar", style = MaterialTheme.typography.labelLarge, color = tinta) } }
                TextButton(onClick = { nuevaEnCajon(nav, tema) }) { Text("＋ ${a.ficha}", style = MaterialTheme.typography.labelLarge, color = tinta) }
                Spacer(Modifier.weight(1f))
                val hay = i in 0 until fichas.size - 1
                TextButton(onClick = { ir(i + 1) }, enabled = hay) { Text("›", style = MaterialTheme.typography.titleLarge, color = tinta.copy(alpha = if (hay) 1f else 0.3f)) }
            }
        }
    }
}
