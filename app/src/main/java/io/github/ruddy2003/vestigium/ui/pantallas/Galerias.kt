@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Obra
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Cifra
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.rememberSentir
import io.github.ruddy2003.vestigium.ui.Sentidos
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Calificación con el símbolo del tema (en lugar de estrellas)
// =====================================================================

private fun dir(a: Double) = Offset(cos(a).toFloat(), sin(a).toFloat())

private fun estrella(c: Offset, r: Float, puntas: Int, interior: Float, giro: Double = -PI / 2): Path = Path().apply {
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * interior
        val p = c + dir(giro + PI * i / puntas) * rr
        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

private fun poligono(c: Offset, r: Float, lados: Int, giro: Double = -PI / 2): Path = Path().apply {
    for (i in 0 until lados) { val p = c + dir(giro + 2 * PI * i / lados) * r; if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
    close()
}

private fun circulo(c: Offset, r: Float) = Path().apply { addOval(Rect(c, r)) }
private fun ovalo(c: Offset, rx: Float, ry: Float) = Path().apply { addOval(Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)) }
private fun menos(a: Path, b: Path) = Path.combine(PathOperation.Difference, a, b)
private fun mas(a: Path, b: Path) = Path.combine(PathOperation.Union, a, b)

/**
 * El símbolo del tema para calificar, uno distinto por tema: hoja de laurel, sol de Vergina, cuadrifolio, ojo, cruz de malta,
 * matraz, abeja imperial, escarabajo, copo, moneda de la ruta, rosetón, engrane con estrella, escarapela, gema…
 */
fun simboloCalificacion(t: TemaId, c: Offset, r: Float): Path = when (t) {
    TemaId.ROMANO -> Path().apply { moveTo(c.x - r, c.y + r * 0.35f); quadraticTo(c.x - r * 0.1f, c.y - r * 1.15f, c.x + r, c.y - r * 0.35f); quadraticTo(c.x + r * 0.1f, c.y + r * 1.15f, c.x - r, c.y + r * 0.35f); close() }
    TemaId.HELENO -> mas(estrella(c, r, 16, 0.55f), circulo(c, r * 0.3f))
    TemaId.MEDIEVAL -> listOf(0.0, PI / 2, PI, 3 * PI / 2).map { circulo(c + dir(it) * (r * 0.45f), r * 0.48f) }.reduce { a, b -> mas(a, b) }
    TemaId.GOTICO -> (0 until 6).map { circulo(c + dir(PI / 3 * it - PI / 2) * (r * 0.55f), r * 0.4f) }.fold(circulo(c, r * 0.42f)) { a, b -> mas(a, b) }
    TemaId.CIBER -> menos(poligono(c, r, 6, 0.0), poligono(c, r * 0.45f, 6, 0.0))
    TemaId.NEON -> menos(poligono(c, r, 6, 0.0), poligono(c, r * 0.45f, 6, 0.0))
    TemaId.DISTOPIA -> {
        val ojo = Path().apply { moveTo(c.x - r, c.y); quadraticTo(c.x, c.y - r * 1.15f, c.x + r, c.y); quadraticTo(c.x, c.y + r * 1.15f, c.x - r, c.y); close() }
        mas(menos(ojo, circulo(c, r * 0.36f)), circulo(c, r * 0.18f))
    }
    TemaId.REFUGIO -> menos(estrella(c, r, 20, 0.84f), circulo(c, r * 0.5f)).let { mas(it, circulo(c, r * 0.32f)) }
    // La cruz de malta de los caballeros: cuatro brazos que se abren y terminan en dos puntas.
    TemaId.ORDEN -> (0 until 4).map { k ->
        val a = -PI / 2 + PI / 2 * k
        val izq = c + dir(a - 0.42) * r; val muesca = c + dir(a) * (r * 0.62f); val der = c + dir(a + 0.42) * r
        Path().apply { moveTo(c.x, c.y); lineTo(izq.x, izq.y); lineTo(muesca.x, muesca.y); lineTo(der.x, der.y); close() }
    }.reduce { a, b -> mas(a, b) }
    TemaId.LABORATORIO -> Path().apply {
        moveTo(c.x - r * 0.24f, c.y - r); lineTo(c.x + r * 0.24f, c.y - r); lineTo(c.x + r * 0.24f, c.y - r * 0.3f); lineTo(c.x + r * 0.92f, c.y + r * 0.85f)
        lineTo(c.x - r * 0.92f, c.y + r * 0.85f); lineTo(c.x - r * 0.24f, c.y - r * 0.3f); close()
    }
    TemaId.COLONOS -> Path().apply {
        moveTo(c.x, c.y - r); lineTo(c.x + r * 0.95f, c.y - r * 0.05f); lineTo(c.x + r * 0.7f, c.y - r * 0.05f); lineTo(c.x + r * 0.7f, c.y + r * 0.9f)
        lineTo(c.x - r * 0.7f, c.y + r * 0.9f); lineTo(c.x - r * 0.7f, c.y - r * 0.05f); lineTo(c.x - r * 0.95f, c.y - r * 0.05f); close()
    }
    // La abeja imperial: alas, tórax, cabeza y el abdomen con sus franjas.
    TemaId.NAPOLEON -> {
        val alaIzq = Path().apply { moveTo(c.x - r * 0.12f, c.y - r * 0.25f); quadraticTo(c.x - r * 1.0f, c.y - r * 0.95f, c.x - r * 0.9f, c.y - r * 0.12f)
            quadraticTo(c.x - r * 0.55f, c.y + r * 0.12f, c.x - r * 0.12f, c.y - r * 0.05f); close() }
        val alaDer = Path().apply { moveTo(c.x + r * 0.12f, c.y - r * 0.25f); quadraticTo(c.x + r * 1.0f, c.y - r * 0.95f, c.x + r * 0.9f, c.y - r * 0.12f)
            quadraticTo(c.x + r * 0.55f, c.y + r * 0.12f, c.x + r * 0.12f, c.y - r * 0.05f); close() }
        val franjas = Path().apply {
            addRect(Rect(c.x - r, c.y + r * 0.28f, c.x + r, c.y + r * 0.36f)); addRect(Rect(c.x - r, c.y + r * 0.52f, c.x + r, c.y + r * 0.6f))
        }
        val abdomen = menos(ovalo(c + Offset(0f, r * 0.42f), r * 0.32f, r * 0.55f), franjas)
        listOf(alaIzq, alaDer, circulo(c + Offset(0f, -r * 0.18f), r * 0.24f), circulo(c + Offset(0f, -r * 0.52f), r * 0.16f), abdomen).reduce { a, b -> mas(a, b) }
    }
    // Un escarabajo: élitros, pronoto y cabeza.
    TemaId.BOTANICO -> listOf(ovalo(c + Offset(0f, r * 0.2f), r * 0.62f, r * 0.78f), ovalo(c + Offset(0f, -r * 0.58f), r * 0.44f, r * 0.24f),
        circulo(c + Offset(0f, -r * 0.86f), r * 0.17f)).reduce { a, b -> mas(a, b) }
    TemaId.NAVIDAD -> estrella(c, r, 6, 0.32f)
    TemaId.SEDA -> menos(circulo(c, r), poligono(c, r * 0.36f, 4, PI / 4))
    // Un engrane de ocho dientes con la estrella calada en medio.
    TemaId.URSS -> {
        val paso = 2 * PI / 8
        val rueda = Path().apply {
            for (i in 0 until 8) {
                val a = -PI / 2 + i * paso
                listOf(a - paso * 0.5 to r * 0.74f, a - paso * 0.22 to r * 0.74f, a - paso * 0.16 to r, a + paso * 0.16 to r, a + paso * 0.22 to r * 0.74f)
                    .forEachIndexed { j, (g, rr) -> val q = c + dir(g) * rr; if (i == 0 && j == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
            }
            close()
        }
        mas(menos(rueda, circulo(c, r * 0.46f)), estrella(c, r * 0.38f, 5, 0.4f))
    }
    TemaId.ESPACIO -> mas(circulo(c, r * 0.5f), menos(ovalo(c, r, r * 0.36f), ovalo(c, r * 0.78f, r * 0.2f)))
    TemaId.ANDALUS -> mas(poligono(c, r, 4, 0.0), poligono(c, r, 4, PI / 4))
    TemaId.MONGOL -> mas(estrella(c, r, 8, 0.5f), circulo(c, r * 0.45f))
    TemaId.RENACIMIENTO -> mas(menos(circulo(c, r), circulo(c, r * 0.8f)), poligono(c, r * 0.8f, 4, -PI / 2))
    // La escarapela: borde plisado y el anillo blanco hueco.
    TemaId.REVOLUCION -> menos(estrella(c, r, 18, 0.88f), menos(circulo(c, r * 0.62f), circulo(c, r * 0.4f)))
    // Una gema tallada: seis puntas cortas.
    TemaId.ANTIGUAS -> estrella(c, r, 6, 0.72f)
}

/**
 * La calificación (1 a 10) como cinco símbolos del tema: cada símbolo vale 2 y la mitad se dibuja partida.
 * Sin calificación no dibuja nada.
 */
@Composable
fun Calificacion(valor: Int?, modifier: Modifier = Modifier, tam: Dp = 14.dp, color: Color = MaterialTheme.colorScheme.primary, tema: TemaId = Estilo.tema) {
    if (valor == null) return
    val vacio = MaterialTheme.colorScheme.outline
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(tam * 0.18f)) {
        for (i in 0 until 5) {
            val lleno = when { valor >= 2 * (i + 1) -> 1f; valor == 2 * i + 1 -> 0.5f; else -> 0f }
            Canvas(Modifier.size(tam)) {
                val p = simboloCalificacion(tema, center, size.minDimension / 2 * 0.92f)
                if (lleno < 1f) drawPath(p, vacio.copy(alpha = 0.55f), style = Stroke(1.1.dp.toPx()))
                if (lleno >= 1f) drawPath(p, color)
                else if (lleno > 0f) clipRect(right = size.width / 2) { drawPath(p, color) }
            }
        }
    }
}

// =====================================================================
//  Los recuadros grandes de «Más»
// =====================================================================

/**
 * Un recuadro grande con su dibujo arriba y el título abajo. El dibujo lleva encima el marco del tema (el mismo que el mapa:
 * dentículos, greca, remaches, galones, nieve, runas…), más fino, y solo en los bordes.
 */
@Composable
internal fun Recuadro(titulo: String, detalle: String, modifier: Modifier = Modifier, alto: Dp = 150.dp, onClick: () -> Unit, dibujo: @Composable BoxScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    val sentir = rememberSentir()
    val tema = Estilo.tema
    Surface(onClick = { sentir(Sentidos.Evento.ABRIR); onClick() }, shape = MaterialTheme.shapes.medium, color = cs.surface,
        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.55f)), modifier = modifier.fillMaxWidth()) {
        Column {
            Box(Modifier.fillMaxWidth().height(alto).clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))) {
                dibujo()
                Canvas(Modifier.matchParentSize()) { marcoDelMapa(tema, 0.75.dp.toPx(), cs.onSurface.copy(alpha = 0.8f), cs.primary, cs.surface) }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(Estilo.seccion(titulo, Romano.latin(titulo)).first, style = MaterialTheme.typography.titleLarge)
                    Text(detalle, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Text("→", style = MaterialTheme.typography.titleLarge, color = cs.primary)
            }
        }
    }
}

/** Mapas: el mapa para rascar a la vista, con tus países descubiertos. */
@Composable
fun RecuadroMapas(nav: Nav) {
    val r = rememberResumen("todo")
    val detalle = r?.let { "${it.paises.size} países · ${it.ciudades.size} ciudades · ${it.sitios.size} sitios del Patrimonio Mundial" } ?: "Mundo, mapa para rascar, Patrimonio Mundial y tus lugares"
    Recuadro("Mapas", detalle, alto = 168.dp, onClick = { nav.ir("mapas") }) {
        MapaRascar(r?.codigosPaises ?: emptySet(), Modifier.fillMaxSize().padding(6.dp))
    }
}

/** Obras: las portadas de lo último que viste o leíste, en abanico. */
@Composable
fun RecuadroObras(nav: Nav) {
    val vistas = Repo.obras.filter { it.titulo.isNotBlank() && (it.estado == "terminado" || it.estado == "en_curso") }
        .sortedByDescending { it.fin ?: it.dias.maxOrNull() ?: it.agregado }
    val terminadas = Repo.obras.count { it.estado == "terminado" }
    Recuadro("Obras", if (Repo.obras.isEmpty()) "Libros, películas, series y juegos, con su portada y tu calificación" else
        "$terminadas vistas o leídas · ${Repo.obras.count { it.estado == "en_curso" }} en curso", alto = 168.dp, onClick = { nav.ir("obras") }) {
        val m = io.github.ruddy2003.vestigium.ui.materia(Estilo.tema)
        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp))) {
            // El estante del tema detrás de las portadas.
            Canvas(Modifier.fillMaxSize()) {
                drawRect(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(m.brillo.copy(alpha = 0.25f), m.cuerpo.copy(alpha = 0.35f))))
                drawRect(m.sombra.copy(alpha = 0.7f), Offset(0f, size.height * 0.86f), Size(size.width, size.height * 0.14f))
            }
            if (vistas.isEmpty()) Text("Añade lo que estés leyendo o viendo", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center))
            else BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
                val l = vistas.take(6)
                val ancho = 78.dp
                val paso = if (l.size > 1) ((maxWidth - ancho) / (l.size - 1)).coerceAtMost(ancho * 0.9f) else 0.dp
                val inicio = (maxWidth - (ancho + paso * (l.size - 1))) / 2
                l.forEachIndexed { i, o ->
                    val giro = (i - (l.size - 1) / 2f) * 4f
                    PortadaObra(o, Modifier.offset(x = inicio + paso * i, y = 12.dp + (kotlin.math.abs(i - (l.size - 1) / 2f) * 4).dp).width(ancho).aspectRatio(2f / 3f).rotate(giro), lado = 240)
                }
            }
        }
    }
}

// =====================================================================
//  Mapas: Mundo, el mapa para rascar, el Patrimonio Mundial, las calles y tus lugares
// =====================================================================

/**
 * Un dibujo pequeño para cada mapa, con las piezas del tema: los lugares se marcan con la forma del tema (la misma que en el
 * mapa: tabla con asas, cursor, abeto, bicornio…), la ruta termina en ella y los demás llevan la rosa de los vientos del tema.
 */
private fun DrawScope.dibujoMapa(id: String, c1: Color, c2: Color, t: TemaId, fondo: Color) {
    val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
    val trazo = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round)
    val relleno = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { color = c1.toArgb() }
    val borde = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        style = android.graphics.Paint.Style.STROKE; strokeWidth = 1.2.dp.toPx(); color = fondo.toArgb(); strokeJoin = android.graphics.Paint.Join.ROUND
    }
    fun marca(o: Offset, rr: Float) = drawIntoCanvas { lienzo -> Formas.dibujar(lienzo.nativeCanvas, t, o.x, o.y, rr, relleno, borde) }
    fun rosa() = rosaDelMapa(t, Offset(w - h * 0.2f, h * 0.22f), h * 0.17f, c2, c1, c2, fondo)
    when (id) {
        "mundo" -> {
            drawCircle(c2.copy(alpha = 0.18f), r, c); drawCircle(c1, r, c, style = trazo)
            listOf(0.35f, 0.75f).forEach { k -> drawOval(c1, Offset(c.x - r * k, c.y - r), Size(2 * r * k, 2 * r), style = Stroke(1.2.dp.toPx())) }
            listOf(-0.5f, 0f, 0.5f).forEach { k -> val y = c.y + r * k; val dx = r * kotlin.math.sqrt(1 - k * k); drawLine(c1, Offset(c.x - dx, y), Offset(c.x + dx, y), 1.2.dp.toPx()) }
            rosa()
        }
        "unesco" -> {
            val base = c.y + r * 0.8f
            drawPath(Path().apply { moveTo(c.x - r * 1.1f, c.y - r * 0.45f); lineTo(c.x, c.y - r * 1.05f); lineTo(c.x + r * 1.1f, c.y - r * 0.45f); close() }, c1)
            for (i in 0 until 5) { val x = c.x - r * 0.9f + i * r * 0.45f; drawRect(c1, Offset(x - r * 0.08f, c.y - r * 0.38f), Size(r * 0.16f, base - (c.y - r * 0.38f))) }
            drawRect(c1, Offset(c.x - r * 1.2f, base), Size(r * 2.4f, r * 0.18f))
            rosa()
        }
        "calles" -> {
            val az = java.util.Random(3)
            for (i in 0..5) { val x = w * (0.1f + i * 0.16f) + az.nextFloat() * 8f; drawLine(c2.copy(alpha = 0.6f), Offset(x, 0f), Offset(x + (az.nextFloat() - 0.5f) * 30f, h), 1.5.dp.toPx()) }
            for (i in 0..3) { val y = h * (0.15f + i * 0.23f); drawLine(c2.copy(alpha = 0.6f), Offset(0f, y), Offset(w, y + (az.nextFloat() - 0.5f) * 20f), 1.5.dp.toPx()) }
            val ruta = Path().apply { moveTo(w * 0.12f, h * 0.8f); cubicTo(w * 0.35f, h * 0.7f, w * 0.4f, h * 0.3f, w * 0.62f, h * 0.38f); cubicTo(w * 0.8f, h * 0.45f, w * 0.78f, h * 0.15f, w * 0.9f, h * 0.2f) }
            drawPath(ruta, c1, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
            marca(Offset(w * 0.9f, h * 0.2f), h * 0.11f)
        }
        "rascar" -> {
            drawRect(c2.copy(alpha = 0.35f), Offset(w * 0.12f, h * 0.2f), Size(w * 0.76f, h * 0.6f))
            val p = Path().apply { moveTo(w * 0.2f, h * 0.3f); var x = w * 0.2f; var arriba = true; while (x < w * 0.75f) { x += w * 0.07f; lineTo(x, if (arriba) h * 0.62f else h * 0.32f); arriba = !arriba } }
            drawPath(p, c1, style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
            rosa()
        }
        else -> {
            // Lugares: tres marcas del tema sobre un mapa.
            listOf(Offset(w * 0.3f, h * 0.45f) to 1f, Offset(w * 0.62f, h * 0.35f) to 0.8f, Offset(w * 0.5f, h * 0.72f) to 0.7f).forEach { (o, k) -> marca(o, r * 0.42f * k) }
        }
    }
}

@Composable
private fun TarjetaMapa(id: String, titulo: String, detalle: String, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val sentir = rememberSentir()
    val tema = Estilo.tema
    Surface(onClick = { sentir(Sentidos.Evento.ABRIR); onClick() }, shape = MaterialTheme.shapes.medium, color = cs.surface, border = BorderStroke(1.dp, cs.outline), modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            // El dibujo del mapa dentro del marco del tema, en pequeño.
            Canvas(Modifier.fillMaxWidth().height(70.dp)) {
                dibujoMapa(id, cs.primary, cs.secondary, tema, cs.surface)
                marcoDelMapa(tema, 0.55.dp.toPx(), cs.onSurface.copy(alpha = 0.7f), cs.primary, cs.surface)
            }
            Text(Estilo.seccion(titulo, Romano.latin(titulo)).first, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(detalle, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** Todos los mapas en un solo lugar. */
@Composable
fun PantallaMapas(nav: Nav) {
    val ctx = LocalContext.current
    val r = rememberResumen("todo")
    val sitios = remember { runCatching { Mundo.sitios(ctx).size }.getOrDefault(0) }
    Hoja("Mapas", nav.atras, "Dónde has estado: países y ciudades, el mapa para rascar, el Patrimonio Mundial, tus calles y tus lugares de siempre.") {
        Surface(onClick = { nav.ir("rascar") }, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)), modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
            MapaRascar(r?.codigosPaises ?: emptySet(), Modifier.fillMaxWidth().aspectRatio(1.9f).padding(6.dp))
        }
        if (r != null) Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Cifra("${r.paises.size}", "países", Modifier.weight(1f))
            Cifra("${r.ciudades.size}", "ciudades", Modifier.weight(1f))
            Cifra("${r.sitios.size}", "patrimonio", Modifier.weight(1f))
        }
        Seccion("Tus mapas") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TarjetaMapa("mundo", "Mundo", "Ciudades y países, dónde pasaste más tiempo", Modifier.weight(1f)) { nav.ir("mundo") }
                    TarjetaMapa("rascar", "Mapa para rascar", "Descubre a mano los países que visitaste", Modifier.weight(1f)) { nav.ir("rascar") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TarjetaMapa("unesco", "Patrimonio Mundial", "${r?.sitios?.size ?: 0} de ${"%,d".format(sitios)} sitios de la UNESCO", Modifier.weight(1f)) { nav.ir("unesco") }
                    TarjetaMapa("calles", "Tus calles", "Ánimo, tiempo y la ruta de un día, sobre el mapa", Modifier.weight(1f)) { nav.ir("mapa") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TarjetaMapa("lugares", "Lugares", "${Repo.lugares.size} lugares marcados y tu ánimo en cada uno", Modifier.weight(1f)) { nav.ir("lugares") }
                    TarjetaMapa("mundo", "Ciudades visitadas", "Añade a mano las de antes de la app", Modifier.weight(1f)) { nav.ir("ciudad_nueva") }
                }
            }
        }
        r?.ciudades?.take(5)?.takeIf { it.isNotEmpty() }?.let { l ->
            Seccion("Donde más has estado") {
                io.github.ruddy2003.vestigium.ui.Tarjeta {
                    l.forEachIndexed { i, e ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(Estilo.numeral(i + 1), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.width(36.dp))
                            Text("${Mundo.bandera(e.pais)}  ${e.nombre}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            Text("${e.totalDias} días", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
        Ayuda(Mundo.CREDITOS, Modifier.padding(top = 16.dp))
    }
}

// =====================================================================
//  Obras: la galería de lo que viste, leíste y jugaste
// =====================================================================

private val ORDENES = listOf("recientes" to "recientes", "calificacion" to "mejor calificadas", "titulo" to "por título")
private val ESTADOS_GALERIA = listOf("terminado" to "vistas y leídas", "en_curso" to "en curso", "todas" to "todas")

/** Las obras en grande: portadas en dos columnas (o cuatro por fila), con la calificación en los símbolos del tema. */
@Composable
fun PantallaObras(nav: Nav) {
    var tipo by remember { mutableStateOf<String?>(null) }
    var estado by remember { mutableStateOf("terminado") }
    var columnas by remember { mutableStateOf(2) }
    var orden by remember { mutableStateOf("recientes") }
    var separar by remember { mutableStateOf(false) }
    val todas = Repo.obras.filter { it.titulo.isNotBlank() }
    val lista = todas.filter { (tipo == null || it.tipo == tipo) && (estado == "todas" || it.estado == estado) }.let { l ->
        when (orden) {
            "calificacion" -> l.sortedWith(compareByDescending<Obra> { it.calificacion ?: -1 }.thenByDescending { it.fin ?: it.agregado })
            "titulo" -> l.sortedBy { java.text.Normalizer.normalize(it.titulo.lowercase(), java.text.Normalizer.Form.NFD) }
            else -> l.sortedByDescending { it.fin ?: it.dias.maxOrNull() ?: it.inicio ?: it.agregado }
        }
    }
    Hoja("Obras", nav.atras, "Lo que has visto, leído y jugado, con su portada y tu calificación. Toca una para ver su ficha, tus notas y tus citas.") {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonPrincipal("+ añadir", Modifier.weight(1f)) {
                BusquedaObra.tipo = tipo ?: "libro"; BusquedaObra.texto = ""; BusquedaObra.portadaDe = null; nav.ir("buscar_obra")
            }
            BotonSecundario("lista y pendientes", Modifier.weight(1f)) { nav.ir("biblioteca") }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("todo", tipo == null) { tipo = null }
            TIPOS_OBRA.forEach { t -> Ficha(t.nombre.lowercase(), tipo == t.id) { tipo = if (tipo == t.id) null else t.id } }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ESTADOS_GALERIA.forEach { (id, n) -> Ficha(n, estado == id) { estado = id } }
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Ficha("2 por fila", columnas == 2) { columnas = 2 }
            Ficha("4 por fila", columnas == 4) { columnas = 4 }
            Ficha("separar por tipo", separar) { separar = !separar }
            ORDENES.forEach { (id, n) -> Ficha(n, orden == id) { orden = id } }
        }
        val calificadas = lista.mapNotNull { it.calificacion }
        if (lista.isNotEmpty()) Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${lista.size} obra${if (lista.size == 1) "" else "s"}", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            if (calificadas.isNotEmpty()) {
                Text("promedio  ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Calificacion(Math.round(calificadas.average()).toInt(), tam = 15.dp)
            }
        }
        if (todas.isEmpty()) { Vacio("Todavía no hay obras. Busca un libro, una película o una serie para empezar."); return@Hoja }
        if (lista.isEmpty()) { Vacio("Nada con estos filtros."); return@Hoja }
        val grupos = if (separar && tipo == null) TIPOS_OBRA.mapNotNull { t -> lista.filter { it.tipo == t.id }.takeIf { it.isNotEmpty() }?.let { t.nombre to it } } else listOf(null to lista)
        grupos.forEach { (titulo, l) ->
            if (titulo != null) Text(titulo + "s", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 18.dp))
            Column(Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(if (columnas == 2) 18.dp else 12.dp)) {
                l.chunked(columnas).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(if (columnas == 2) 14.dp else 8.dp)) {
                        fila.forEach { o -> CeldaObra(o, columnas, Modifier.weight(1f)) { EdicionObra.id = o.id; nav.ir("obra") } }
                        repeat(columnas - fila.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CeldaObra(o: Obra, columnas: Int, modifier: Modifier, abrir: () -> Unit) {
    val grande = columnas == 2
    Column(modifier.clickable(onClick = abrir), horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            PortadaObra(o, Modifier.fillMaxWidth().aspectRatio(2f / 3f), lado = if (grande) 520 else 280)
            if (o.estado == "en_curso" && o.progreso != null && o.total != null && o.total > 0) Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(5.dp).height(4.dp)
                .clip(RoundedCornerShape(2.dp)).background(Color.Black.copy(alpha = 0.45f))) {
                Box(Modifier.fillMaxWidth((o.progreso.toFloat() / o.total).coerceIn(0f, 1f)).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
            }
        }
        Text(o.titulo, style = if (grande) MaterialTheme.typography.titleSmall else MaterialTheme.typography.labelSmall, maxLines = if (grande) 2 else 1,
            overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp))
        if (grande) Text(listOfNotNull(o.autor.substringBefore(",").substringBefore(";").ifBlank { null }, (o.fin ?: o.anio?.toString())?.take(4)).joinToString(" · "),
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Calificacion(o.calificacion, Modifier.padding(top = 4.dp), tam = if (grande) 16.dp else 10.dp)
    }
}
