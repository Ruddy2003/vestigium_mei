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
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Totales
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.porNumero
import io.github.ruddy2003.vestigium.ui.ArchivoIdeas
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.PapelFicha
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TextoMarcado
import io.github.ruddy2003.vestigium.ui.archivoIdeas
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.esquemaPagina
import io.github.ruddy2003.vestigium.ui.ideario.BotonArchivo
import io.github.ruddy2003.vestigium.ui.ideario.CajonIdeas
import io.github.ruddy2003.vestigium.ui.ideario.CompartimentoIdeas
import io.github.ruddy2003.vestigium.ui.ideario.DisenoIdeas
import io.github.ruddy2003.vestigium.ui.ideario.InteriorIdeas
import io.github.ruddy2003.vestigium.ui.ideario.MarcaFijada
import io.github.ruddy2003.vestigium.ui.ideario.MarcaPostura
import io.github.ruddy2003.vestigium.ui.ideario.MuebleIdeas
import io.github.ruddy2003.vestigium.ui.ideario.disenoIdeas
import io.github.ruddy2003.vestigium.ui.paginaFicha
import io.github.ruddy2003.vestigium.ui.rayadoFicha
import io.github.ruddy2003.vestigium.ui.rememberSentir

/** Los cajones del fichero: uno por tema, del más lleno al más vacío, y al final el de las ideas sin tema. */
private fun cajones(ideas: List<Idea>): List<CajonIdeas> {
    val porTema = Ideario.temas(ideas).map { (t, n) -> CajonIdeas(t, n, Ideario.deTema(ideas, t).firstOrNull()?.fecha) }
    val sin = Ideario.deTema(ideas, null)
    return porTema + (if (sin.isNotEmpty()) listOf(CajonIdeas("", sin.size, sin.firstOrNull()?.fecha)) else emptyList())
}

/** Cómo se lee el nombre de un cajón. */
internal fun rotuloCajon(tema: String) = if (tema.isEmpty()) "sin tema" else "#" + tema.replace('_', ' ')

/** «el tansu» → «El tansu». */
internal fun conMayuscula(s: String) = s.replaceFirstChar { it.uppercase() }

/**
 * El Ideario como el mueble del tema ([MuebleIdeas]): cada tema del Ideario es un compartimento (un cajón del tansu, un nicho
 * en la pared de la cueva…) con su rótulo, y se ve más lleno cuanto más fichas tiene. Con [max], solo los más llenos (la vista
 * corta de la pestaña Diario) y la cuenta de los que faltan. Sin ideas, el mueble se ve vacío. Tocar uno lo abre ([PantallaCajonIdeas]).
 */
@Composable
fun FicheroIdeas(nav: Nav, ideas: List<Idea> = Repo.ideas, max: Int? = null, modifier: Modifier = Modifier) {
    val todos = remember(ideas) { cajones(ideas) }
    val lista = if (max != null) todos.take(max) else todos
    val a = archivoIdeas(Estilo.tema)
    val d = disenoIdeas(Estilo.tema)
    val mas = todos.size - lista.size
    MuebleIdeas(lista, ::rotuloCajon, { c -> a.cuenta(c.n) + (if (d.cols <= 2) c.ultima?.let { " · " + Fechas.corto(it) } ?: "" else "") }, modifier,
        mas = mas, textoMas = "y $mas ${porNumero(mas, a.contenedor, a.contenedores)} más") { c ->
        Seleccion.cajon = c.tema; Seleccion.fichaIdea = null; nav.ir("cajon_ideas")
    }
}

/** Lo que dice la ficha de dónde salió: quién, la obra o la fuente, y el minuto o la página. */
private fun fuenteDe(i: Idea): String = listOfNotNull(i.quien.joinToString(", ").ifBlank { null },
    i.obra_id?.let { o -> Repo.obras.firstOrNull { it.id == o }?.titulo } ?: i.fuente.ifBlank { null }, i.donde.ifBlank { null }).joinToString(" · ")

/**
 * El encabezado de una ficha: la marca de fijada, el tema y la fecha. En la ficha-ventana van dentro de la barra de título,
 * como el nombre del archivo; en las fichas angostas ([DisenoIdeas.apilado]) la fecha va debajo del tema.
 */
@Composable
private fun EncabezadoFicha(i: Idea, rotulo: String, d: DisenoIdeas, p: Pagina, grande: Boolean, modifier: Modifier = Modifier) {
    val ventana = d.barra
    val color = if (ventana) p.color else p.acento
    val estilo = if (grande) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall
    val nombre = if (ventana) rotulo.removePrefix("#").replace(' ', '_') + "/" + (i.titulo.ifBlank { i.texto }.take(14).trim().replace(' ', '_').lowercase().ifBlank { "idea" }) + ".md" else rotulo
    val marca = if (grande) 13.dp else 9.dp
    if (d.apilado && !ventana) Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (i.fijada) MarcaFijada(p, Modifier.padding(end = 4.dp), marca)
            Text(nombre, style = estilo, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(Fechas.corto(i.fecha), style = estilo, color = color.copy(alpha = 0.7f), maxLines = 1)
    } else Row(modifier.fillMaxWidth().let { if (ventana) it.height(d.altoBarra(grande)) else it }, verticalAlignment = Alignment.CenterVertically) {
        if (i.fijada) MarcaFijada(p, Modifier.padding(end = 4.dp), marca)
        Text(nombre, style = estilo, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Text(Fechas.corto(i.fecha), style = estilo, color = color.copy(alpha = 0.75f), maxLines = 1,
            modifier = Modifier.padding(end = if (ventana) (if (grande) 64.dp else 34.dp) else 0.dp))
    }
}

/**
 * Una ficha chica, para ver varias a la vez: el papel del tema con la forma de sus fichas ([DisenoIdeas.forma]: el tanzaku, la
 * laja…), el tema y la fecha arriba, la línea de color, la idea en una línea y unos renglones del texto. Abajo, la marca de
 * cómo la tienes. [rotulo] es lo que va en la pestaña (el tema del cajón, o el primero de la idea).
 */
@Composable
fun FichaMini(i: Idea, modifier: Modifier = Modifier, rotulo: String? = null, lineas: Int = 4, onClick: () -> Unit) {
    val t = Estilo.tema
    val d = disenoIdeas(t); val p = paginaFicha(t)
    val semilla = i.id.hashCode()
    val forma = remember(i.id, t) { d.forma(semilla) }
    val tam = (p.tam * 0.74f).sp; val inter = (p.interlinea * 0.66f).sp
    val paso = with(LocalDensity.current) { inter.toDp() }
    val sup = d.margenes.arriba; val lado = d.margenes.lado
    val encabezado = if (d.barra) d.altoBarra(false) else 16.dp
    val texto = rotulo ?: Ideario.temasDe(i).firstOrNull()?.let { rotuloCajon(it) } ?: "idea"
    // Los colores de la página (también las marcas de @ y #): sobre el papel claro, los del modo oscuro de la app no se leen.
    MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
    Surface(shape = forma, color = Color.Transparent, shadowElevation = 3.dp, modifier = modifier.clickable(onClick = onClick)) {
        Box {
            PapelFicha(p)
            Canvas(Modifier.matchParentSize()) { with(d) { adornoFicha(p, false, semilla) } }
            // La línea de color va justo debajo del encabezado tal como quedó medido, a medio camino del título: con la
            // altura supuesta (16 dp) caía sobre las astas del título cuando la letra del tema es más baja (Art déco).
            val densidad = LocalDensity.current
            var altoEncabezado by remember { mutableStateOf(encabezado) }
            Column(Modifier.fillMaxSize().rayadoFicha(p, sup + altoEncabezado + 1.dp, paso).padding(start = lado, end = lado, top = sup, bottom = d.margenes.abajo)) {
                EncabezadoFicha(i, texto, d, p, grande = false, modifier = Modifier.onSizeChanged { altoEncabezado = with(densidad) { it.height.toDp() } })
                // Aire bajo la línea: con un interlineado apretado, las astas del título suben por encima de su renglón.
                Spacer(Modifier.height(7.dp))
                val estilo = TextStyle(fontFamily = p.texto, fontSize = tam, lineHeight = inter, color = p.tinta)
                if (i.titulo.isNotBlank()) Text(i.titulo, style = estilo.copy(fontWeight = FontWeight.SemiBold), maxLines = 2, overflow = TextOverflow.Ellipsis)
                val resto = (lineas - if (i.titulo.isNotBlank()) 1 else 0).coerceAtLeast(1)
                if (i.texto.isNotBlank()) TextoMarcado(i.texto.trim(), style = estilo, maxLines = resto, overflow = TextOverflow.Ellipsis)
            }
            MarcaPostura(i.postura, p, Modifier.align(Alignment.BottomEnd).padding(end = lado, bottom = (d.margenes.abajo - 6.dp).coerceAtLeast(4.dp)))
        }
    }
    }
}

/** Un sello de goma con [texto], un poco torcido, en el color de [color]. */
@Composable
private fun Sello(texto: String, color: Color, modifier: Modifier = Modifier) {
    Text(texto.uppercase(), style = MaterialTheme.typography.labelSmall, color = color, letterSpacing = 1.2.sp, maxLines = 1,
        modifier = modifier.rotate(-4f).border(1.4.dp, color.copy(alpha = 0.8f), RoundedCornerShape(3.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
}

/**
 * La ficha entera, con la forma y la proporción del objeto del tema (un tanzaku es alto y angosto): mide lo que su texto, con
 * un alto mínimo, y se desplaza si es largo. Abajo, de dónde salió, los otros temas, cómo la tienes como un sello y en qué
 * lugar del cajón va.
 */
@Composable
private fun FichaGrande(i: Idea, numero: Int, total: Int, tema: String, a: ArchivoIdeas) {
    val t = Estilo.tema
    val d = disenoIdeas(t); val p = paginaFicha(t)
    val semilla = i.id.hashCode()
    val forma = remember(i.id, t) { d.forma(semilla) }
    val paso = with(LocalDensity.current) { p.interlinea.sp.toDp() }
    val sup = d.margenes.arribaGrande; val lado = d.margenes.ladoGrande
    val encabezado = if (d.barra) d.altoBarra(true) else 22.dp
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val ancho = (maxWidth - 28.dp) * d.anchoGrande
        val minimo = minOf(ancho / d.proporcionGrande, maxHeight - 16.dp)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            MaterialTheme(colorScheme = esquemaPagina(p), typography = MaterialTheme.typography, shapes = MaterialTheme.shapes) {
            Surface(shape = forma, color = Color.Transparent, shadowElevation = 6.dp, modifier = Modifier.width(ancho).heightIn(min = minimo)) {
                Box {
                    PapelFicha(p, Modifier.matchParentSize())
                    Canvas(Modifier.matchParentSize()) { with(d) { adornoFicha(p, true, semilla) } }
                    val densidad = LocalDensity.current
                    var altoEncabezado by remember { mutableStateOf(encabezado) }
                    Column(Modifier.fillMaxWidth().rayadoFicha(p, sup + altoEncabezado + 2.dp, paso).padding(start = lado, end = lado, top = sup, bottom = d.margenes.abajoGrande)) {
                        EncabezadoFicha(i, rotuloCajon(tema), d, p, grande = true, modifier = Modifier.onSizeChanged { altoEncabezado = with(densidad) { it.height.toDp() } })
                        Spacer(Modifier.height(10.dp))
                        val estilo = TextStyle(fontFamily = p.texto, fontSize = p.tam.sp, lineHeight = p.interlinea.sp, color = p.tinta)
                        if (i.titulo.isNotBlank()) Text(i.titulo, style = estilo.copy(fontWeight = FontWeight.SemiBold))
                        TextoMarcado(i.texto.trim(), style = estilo)
                        val fuente = fuenteDe(i)
                        if (fuente.isNotBlank()) Text("— $fuente", style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic, color = p.tinta.copy(alpha = 0.8f), modifier = Modifier.padding(top = 12.dp))
                        val otros = Ideario.temasDe(i).filter { it != tema }
                        val pie = listOfNotNull(Catalogo.ORIGENES_IDEA.firstOrNull { it.id == i.origen }?.nombre?.lowercase(),
                            otros.takeIf { it.isNotEmpty() }?.joinToString(" ") { "#" + it.replace('_', ' ') }).joinToString(" · ")
                        if (pie.isNotBlank()) Text(pie, style = MaterialTheme.typography.labelSmall, color = p.tenue, modifier = Modifier.padding(top = 8.dp))
                        Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Catalogo.POSTURAS_IDEA.firstOrNull { it.id == i.postura }?.let { po ->
                                MarcaPostura(i.postura, p, Modifier.padding(end = 6.dp), 14.dp)
                                Sello(po.nombre, p.acento)
                            }
                            Spacer(Modifier.weight(1f))
                            Text("${a.ficha} $numero de $total", style = MaterialTheme.typography.labelSmall, color = p.tenue, textAlign = TextAlign.End)
                        }
                    }
                }
            }
            }
        }
    }
}

/** La barra de arriba sobre el interior del contenedor: atrás y lo de la derecha, como placas del tema. */
@Composable
private fun BarraArchivo(tinta: Color, volver: () -> Unit, derecha: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp, start = 4.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        BotonArchivo(Estilo.atras, tinta) { volver() }
        Spacer(Modifier.weight(1f))
        derecha()
    }
}

/** El compartimento cerrado, chico, arriba del interior: dice qué cajón se abrió y cuántas fichas tiene. */
@Composable
private fun CabeceraCajon(tema: String, n: Int, a: ArchivoIdeas, d: DisenoIdeas, oscuro: Boolean) {
    Box(Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 4.dp), contentAlignment = Alignment.Center) {
        CompartimentoIdeas(d, n, tema.isEmpty(), tema.hashCode(), rotuloCajon(tema), a.cuenta(n), oscuro,
            Modifier.height(if (d.proporcion < 1.2f) 108.dp else 84.dp).aspectRatio(d.proporcion, matchHeightConstraintsFirst = true))
    }
}

/** Una idea nueva ya con el tema del cajón abierto. */
private fun nuevaEnCajon(nav: Nav, tema: String) {
    val n = EdicionIdea.nueva().let { if (tema.isNotEmpty()) it.copy(temas = listOf(tema)) else it }
    EdicionIdea.abrir(nav, n)
}

/**
 * Un contenedor abierto: sus fichas en cuadrícula ([DisenoIdeas.columnas], con la proporción del objeto y, en las piedras y los
 * recortes, un poco torcidas), de la más nueva a la más vieja, sobre el interior del contenedor del tema. Arriba, el
 * compartimento cerrado con su rótulo. Se busca dentro, y tocar una la abre entera ([PantallaFichaIdea]).
 */
@Composable
fun PantallaCajonIdeas(nav: Nav) {
    val t = Estilo.tema
    val a = archivoIdeas(t); val d = disenoIdeas(t)
    val oscuro = esOscuro()
    val tinta = d.tintaInterior(oscuro)
    val sentir = rememberSentir()
    val tema = Seleccion.cajon ?: ""
    val fichas = remember(Repo.ideas, tema) { Ideario.deTema(Repo.ideas, tema.ifEmpty { null }) }
    var buscando by remember { mutableStateOf(false) }
    var q by remember { mutableStateOf("") }
    val nq = Totales.normal(q.trim())
    val visibles = if (nq.isBlank()) fichas else fichas.filter { Totales.normal(Ideario.textoBuscable(it, Repo.obras)).contains(nq) }
    Box(Modifier.fillMaxSize()) {
        InteriorIdeas(oscuro)
        Column(Modifier.fillMaxSize().padding(horizontal = d.pared)) {
            BarraArchivo(tinta, nav.atras) {
                BotonArchivo(if (buscando) "cerrar" else "buscar", tinta) { buscando = !buscando; if (!buscando) q = "" }
            }
            CabeceraCajon(tema, fichas.size, a, d, oscuro)
            if (buscando) BasicTextField(q, { q = it }, singleLine = true, cursorBrush = SolidColor(tinta),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = tinta),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                decorationBox = { campo ->
                    Column {
                        Box { if (q.isEmpty()) Text("buscar en ${rotuloCajon(tema)}…", style = MaterialTheme.typography.bodyLarge, color = tinta.copy(alpha = 0.5f)); campo() }
                        Box(Modifier.fillMaxWidth().padding(top = 4.dp).height(1.dp).background(tinta.copy(alpha = 0.4f)))
                    }
                })
            if (fichas.isEmpty()) Box(Modifier.weight(1f).fillMaxWidth()) {
                Text(a.vacio, style = MaterialTheme.typography.bodyLarge, color = tinta.copy(alpha = 0.8f), modifier = Modifier.align(Alignment.Center))
            } else LazyVerticalGrid(GridCells.Fixed(d.columnas), Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (visibles.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("Nada coincide.", style = MaterialTheme.typography.bodyMedium, color = tinta.copy(alpha = 0.8f), textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
                items(visibles, key = { it.id }) { f ->
                    val giro = if (d.desorden == 0f) 0f else (((f.id.hashCode() and 0xff) / 255f) - 0.5f) * 2f * d.desorden
                    FichaMini(f, Modifier.fillMaxWidth().aspectRatio(d.proporcionFicha).graphicsLayer { rotationZ = giro }, rotulo = rotuloCajon(tema), lineas = d.lineas) {
                        sentir(Sentidos.Evento.ABRIR); Seleccion.fichaIdea = f.id; nav.ir("ficha_idea")
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
                BotonArchivo("＋ ${a.ficha}", tinta) { nuevaEnCajon(nav, tema) }
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
    val a = archivoIdeas(t); val d = disenoIdeas(t)
    val oscuro = esOscuro()
    val tinta = d.tintaInterior(oscuro)
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
        InteriorIdeas(oscuro)
        Column(Modifier.fillMaxSize().padding(horizontal = d.pared)) {
            val actual = fichas.getOrNull(i)
            BarraArchivo(tinta, nav.atras) {
                Text(rotuloCajon(tema), style = MaterialTheme.typography.labelLarge, color = tinta.copy(alpha = 0.85f), maxLines = 1, overflow = TextOverflow.Ellipsis,
                    fontFamily = d.letraRotulo, modifier = Modifier.padding(end = 10.dp))
                if (actual != null) BotonArchivo("compartir", tinta) {
                    ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, listOf(actual.titulo, actual.texto).filter { it.isNotBlank() }.joinToString("\n\n")), "Compartir").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
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
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonArchivo("‹", tinta, habilitado = i > 0) { ir(i - 1) }
                Spacer(Modifier.weight(1f))
                actual?.let { f -> BotonArchivo("editar", tinta) { Seleccion.fichaIdea = f.id; EdicionIdea.abrir(nav, f) } }
                BotonArchivo("＋ ${a.ficha}", tinta) { nuevaEnCajon(nav, tema) }
                Spacer(Modifier.weight(1f))
                BotonArchivo("›", tinta, habilitado = i in 0 until fichas.size - 1) { ir(i + 1) }
            }
        }
    }
}
