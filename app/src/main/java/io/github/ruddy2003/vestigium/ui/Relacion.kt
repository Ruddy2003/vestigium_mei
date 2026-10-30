package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Variable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Una relación entre dos variables dibujada como dos recuadros unidos por un lazo del tema: un cable de circuito,
 * una enredadera, una cuerda trenzada, una cinta de lacería… El grosor dice qué tan fuerte es; el color, si suben juntas
 * o una sube cuando la otra baja; la línea punteada, que podría ser azar.
 */
@Composable
fun TarjetaRelacion(a: Variable, b: Variable, r: Double, detalle: String, azar: Boolean, tambien: String?, modifier: Modifier = Modifier) {
    val tema = Estilo.tema
    val oscuro = esOscuro()
    val color = if (r >= 0) Tonos.ingreso(oscuro) else Tonos.gasto(oscuro)
    Surface(modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
                Recuadro(a, tema, Modifier.weight(1f).fillMaxHeight())
                Box(Modifier.width(80.dp).fillMaxHeight().heightIn(min = 64.dp), contentAlignment = Alignment.Center) {
                    val fondo = MaterialTheme.colorScheme.surface
                    Canvas(Modifier.fillMaxSize()) { lazo(tema, color, abs(r).toFloat(), azar, fondo) }
                    // El valor de r en una placa encima del lazo, para que el lazo se vea completo.
                    Text("%+.2f".format(r), style = MaterialTheme.typography.labelMedium, color = color,
                        modifier = Modifier.offset(y = (-17).dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(9.dp))
                            .border(1.dp, color.copy(alpha = if (azar) 0.5f else 0.9f), RoundedCornerShape(9.dp)).padding(horizontal = 6.dp, vertical = 1.dp))
                }
                Recuadro(b, tema, Modifier.weight(1f).fillMaxHeight())
            }
            Text(detalle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            tambien?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp)) }
        }
    }
}

/** La forma del recuadro de cada tema y su aire vertical (los arcos, las bandas y los filetes piden más). */
private class MarcoRecuadro(val forma: Shape, val vertical: Dp)

private fun marcoRecuadro(tema: TemaId): MarcoRecuadro = when (tema) {
    TemaId.ROMANO -> MarcoRecuadro(RoundedCornerShape(2.dp), 12.dp)
    TemaId.CIBER -> MarcoRecuadro(RectangleShape, 9.dp)
    TemaId.NEON -> MarcoRecuadro(RectangleShape, 9.dp)
    TemaId.HELENO -> MarcoRecuadro(RoundedCornerShape(4.dp), 13.dp)
    TemaId.MEDIEVAL -> MarcoRecuadro(RoundedCornerShape(3.dp), 11.dp)
    TemaId.DISTOPIA -> MarcoRecuadro(RectangleShape, 12.dp)
    TemaId.REFUGIO -> MarcoRecuadro(RoundedCornerShape(12.dp), 10.dp)
    TemaId.ORDEN -> MarcoRecuadro(CutCornerShape(7.dp), 10.dp)
    TemaId.LABORATORIO -> MarcoRecuadro(RoundedCornerShape(14.dp), 9.dp)
    TemaId.COLONOS -> MarcoRecuadro(RoundedCornerShape(1.dp), 12.dp)
    TemaId.NAPOLEON -> MarcoRecuadro(RoundedCornerShape(5.dp), 11.dp)
    TemaId.BOTANICO -> MarcoRecuadro(RoundedCornerShape(8.dp), 12.dp)
    TemaId.NAVIDAD -> MarcoRecuadro(RoundedCornerShape(10.dp), 12.dp)
    TemaId.SEDA -> MarcoRecuadro(CutCornerShape(3.dp), 11.dp)
    TemaId.GOTICO -> MarcoRecuadro(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 3.dp, bottomEnd = 3.dp), 14.dp)
    TemaId.ESPACIO -> MarcoRecuadro(RoundedCornerShape(18.dp), 10.dp)
    TemaId.ANDALUS -> MarcoRecuadro(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 4.dp, bottomEnd = 4.dp), 14.dp)
    TemaId.MONGOL -> MarcoRecuadro(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 6.dp, bottomEnd = 6.dp), 12.dp)
    TemaId.RENACIMIENTO -> MarcoRecuadro(RectangleShape, 10.dp)
    TemaId.PIRATA -> MarcoRecuadro(CutCornerShape(7.dp), 12.dp)
    TemaId.URSS -> MarcoRecuadro(CutCornerShape(topEnd = 12.dp), 10.dp)
    TemaId.REVOLUCION -> MarcoRecuadro(RoundedCornerShape(2.dp), 12.dp)
    TemaId.ANTIGUAS -> MarcoRecuadro(CutCornerShape(4.dp), 12.dp)
}

/** Un recuadro con el nombre de la variable y su grupo, con el marco y el adorno de cada tema. */
@Composable
private fun Recuadro(v: Variable, tema: TemaId, modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val marco = marcoRecuadro(tema)
    Box(modifier.background(e.primary.copy(alpha = 0.07f), marco.forma).border(1.5.dp, e.primary.copy(alpha = 0.7f), marco.forma).drawBehind { adorno(tema, e) }
        .padding(horizontal = 10.dp, vertical = marco.vertical), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(v.nombre, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, maxLines = 4)
            Text(v.grupo.lowercase(), style = MaterialTheme.typography.labelSmall, color = e.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** El detalle propio de cada recuadro, detrás del texto: molduras, corchetes, grecas, remaches, sellos, gemas… */
private fun DrawScope.adorno(tema: TemaId, e: ColorScheme) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    val c = e.primary
    when (tema) {
        // Tabula ansata: doble moldura y una cola de milano a cada lado.
        TemaId.ROMANO -> {
            drawRect(c.copy(alpha = 0.45f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), style = Stroke(0.8f * u))
            drawRect(c.copy(alpha = 0.25f), Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), style = Stroke(0.6f * u))
            listOf(0f to 1f, w to -1f).forEach { (x0, d) ->
                drawPath(Path().apply { moveTo(x0 + d * 5 * u, h / 2); lineTo(x0 + d * 1.5f * u, h / 2 - 5 * u); lineTo(x0 + d * 1.5f * u, h / 2 + 5 * u); close() }, c.copy(alpha = 0.4f))
            }
        }
        // Corchetes de consola a los lados y el cursor abajo.
        TemaId.CIBER -> {
            val k = c.copy(alpha = 0.8f)
            listOf(4 * u to 1f, w - 4 * u to -1f).forEach { (x, d) ->
                drawLine(k, Offset(x, 4 * u), Offset(x, h - 4 * u), 1.5f * u)
                drawLine(k, Offset(x, 4 * u), Offset(x + d * 4 * u, 4 * u), 1.5f * u)
                drawLine(k, Offset(x, h - 4 * u), Offset(x + d * 4 * u, h - 4 * u), 1.5f * u)
            }
            drawRect(k, Offset(w - 14 * u, h - 7 * u), Size(5 * u, 1.6f * u))
        }
        TemaId.NEON -> {
            val k = c.copy(alpha = 0.8f)
            listOf(4 * u to 1f, w - 4 * u to -1f).forEach { (x, d) ->
                drawLine(k, Offset(x, 4 * u), Offset(x, h - 4 * u), 1.5f * u)
                drawLine(k, Offset(x, 4 * u), Offset(x + d * 4 * u, 4 * u), 1.5f * u)
                drawLine(k, Offset(x, h - 4 * u), Offset(x + d * 4 * u, h - 4 * u), 1.5f * u)
            }
            drawRect(k, Offset(w - 14 * u, h - 7 * u), Size(5 * u, 1.6f * u))
        }
        // Meandros arriba y abajo.
        TemaId.HELENO -> { grecaRel(3 * u, c.copy(alpha = 0.5f), u); grecaRel(h - 9 * u, c.copy(alpha = 0.5f), u) }
        // Marco de rúbrica y cuatro puntos de oro en cada esquina.
        TemaId.MEDIEVAL -> {
            drawRect(c.copy(alpha = 0.5f), Offset(3 * u, 3 * u), Size(w - 6 * u, h - 6 * u), style = Stroke(0.8f * u))
            listOf(Offset(3 * u, 3 * u), Offset(w - 3 * u, 3 * u), Offset(3 * u, h - 3 * u), Offset(w - 3 * u, h - 3 * u)).forEach { o ->
                listOf(Offset(0f, -1.6f), Offset(1.6f, 0f), Offset(0f, 1.6f), Offset(-1.6f, 0f)).forEach { d -> drawCircle(e.tertiary, 1.1f * u, o + d * u) }
            }
        }
        // Expediente: franja de encabezado y dos perforaciones de archivador.
        TemaId.DISTOPIA -> {
            drawRect(c.copy(alpha = 0.16f), Offset.Zero, Size(w, 6 * u))
            drawLine(c.copy(alpha = 0.5f), Offset(0f, 6 * u), Offset(w, 6 * u), 0.8f * u)
            listOf(0.4f, 0.7f).forEach { f -> drawCircle(c.copy(alpha = 0.45f), 1.8f * u, Offset(5 * u, h * f), style = Stroke(0.9f * u)) }
        }
        // Un átomo de los cincuenta en la esquina.
        TemaId.REFUGIO -> {
            val o = Offset(w - 9 * u, 8 * u); val k = c.copy(alpha = 0.5f)
            listOf(0f, 60f, 120f).forEach { g -> rotate(g, o) { drawOval(k, Offset(o.x - 5 * u, o.y - 1.8f * u), Size(10 * u, 3.6f * u), style = Stroke(0.8f * u)) } }
            drawCircle(k, 1.2f * u, o)
        }
        // Placa blindada: remaches de latón en las esquinas.
        TemaId.ORDEN -> listOf(Offset(6 * u, 6 * u), Offset(w - 6 * u, 6 * u), Offset(6 * u, h - 6 * u), Offset(w - 6 * u, h - 6 * u)).forEach {
            drawCircle(e.secondary, 2 * u, it); drawCircle(c.copy(alpha = 0.5f), 2 * u, it, style = Stroke(0.6f * u))
        }
        // Probeta: marcas de graduación en el borde izquierdo.
        TemaId.LABORATORIO -> {
            var y = 7 * u; var i = 0
            while (y < h - 6 * u) { drawLine(c.copy(alpha = 0.55f), Offset(2 * u, y), Offset((if (i % 4 == 0) 7f else 4f) * u, y), 0.8f * u); y += 3.5f * u; i++ }
        }
        // Recorte de gaceta: filete grueso y fino arriba, fino abajo.
        TemaId.COLONOS -> {
            val k = e.onSurface.copy(alpha = 0.55f)
            drawLine(k, Offset(4 * u, 4 * u), Offset(w - 4 * u, 4 * u), 1.6f * u)
            drawLine(k, Offset(4 * u, 6.8f * u), Offset(w - 4 * u, 6.8f * u), 0.6f * u)
            drawLine(k, Offset(4 * u, h - 5 * u), Offset(w - 4 * u, h - 5 * u), 0.6f * u)
        }
        // Galón de oficial: un trenzado en zigzag abajo, entre dos hilos de oro.
        TemaId.NAPOLEON -> {
            val k = e.tertiary.copy(alpha = 0.8f); val y0 = h - 8 * u
            drawLine(k, Offset(5 * u, y0), Offset(w - 5 * u, y0), 0.6f * u)
            drawLine(k, Offset(5 * u, y0 + 4 * u), Offset(w - 5 * u, y0 + 4 * u), 0.6f * u)
            val z = Path().apply {
                moveTo(5 * u, y0 + 4 * u); var x = 5 * u; var arriba = true
                while (x + 2 * u <= w - 5 * u) { x += 2 * u; lineTo(x, if (arriba) y0 else y0 + 4 * u); arriba = !arriba }
            }
            drawPath(z, k, style = Stroke(0.8f * u))
        }
        // Etiqueta de herbario: el alfiler arriba y la línea punteada para la fecha abajo.
        TemaId.BOTANICO -> {
            drawLine(e.onSurfaceVariant.copy(alpha = 0.6f), Offset(w / 2, 2 * u), Offset(w / 2 + 2 * u, 8 * u), 0.7f * u)
            drawCircle(e.secondary, 2 * u, Offset(w / 2, 3 * u))
            drawLine(c.copy(alpha = 0.35f), Offset(8 * u, h - 5 * u), Offset(w - 8 * u, h - 5 * u), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 2 * u)))
        }
        // Ramita de acebo en la esquina.
        TemaId.NAVIDAD -> {
            val o = Offset(6 * u, 6 * u)
            drawPath(hojaRel(o, o + Offset(9 * u, 1 * u), 2.4f * u), e.secondary.copy(alpha = 0.85f))
            drawPath(hojaRel(o, o + Offset(1 * u, 9 * u), 2.4f * u), e.secondary.copy(alpha = 0.85f))
            listOf(Offset(0f, 0f), Offset(2.2f, -0.6f), Offset(-0.6f, 2.2f)).forEach { drawCircle(c, 1.5f * u, o + it * u) }
        }
        // Grecas chinas (回) en las cuatro esquinas.
        TemaId.SEDA -> {
            val k = c.copy(alpha = 0.6f); val l = 5 * u
            listOf(Offset(4 * u, 4 * u), Offset(w - 4 * u - l, 4 * u), Offset(4 * u, h - 4 * u - l), Offset(w - 4 * u - l, h - 4 * u - l)).forEach { o ->
                drawRect(k, o, Size(l, l), style = Stroke(0.8f * u))
                drawRect(k, o + Offset(1.6f * u, 1.6f * u), Size(l - 3.2f * u, l - 3.2f * u))
            }
        }
        // Un rosetón pequeño bajo la clave del arco.
        TemaId.GOTICO -> {
            val o = Offset(w / 2, 8 * u); val k = c.copy(alpha = 0.5f)
            drawCircle(k, 4 * u, o, style = Stroke(0.8f * u))
            for (i in 0 until 6) { val a = i * PI.toFloat() / 3; drawCircle(k, 1.2f * u, o + Offset(cos(a), sin(a)) * (2.3f * u), style = Stroke(0.6f * u)) }
        }
        // Miras de la sala de control en las esquinas y dos estrellas.
        TemaId.ESPACIO -> {
            val k = c.copy(alpha = 0.8f); val m = 6 * u
            listOf(Offset(m, m) to Offset(1f, 1f), Offset(w - m, m) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f))
                .forEach { (o, d) -> drawLine(k, o, o + Offset(d.x * 6 * u, 0f), 1.4f * u); drawLine(k, o, o + Offset(0f, d.y * 6 * u), 1.4f * u) }
            drawCircle(k, 0.9f * u, Offset(w * 0.72f, 5 * u)); drawCircle(k, 0.7f * u, Offset(w * 0.24f, h - 5 * u))
        }
        // Estrella de ocho puntas de los alicatados, bajo el arco.
        TemaId.ANDALUS -> drawPath(estrellaRel(Offset(w / 2, 9 * u), 8, 4.5f * u, 3.45f * u), c.copy(alpha = 0.55f), style = Stroke(0.9f * u))
        // Cuernos de carnero en espiral en las esquinas de arriba, como en el fieltro bordado.
        TemaId.MONGOL -> listOf(7 * u to 1f, w - 7 * u to -1f).forEach { (x, d) ->
            drawPath(espiralRel(Offset(x, 7 * u), 4 * u, d), c.copy(alpha = 0.6f), style = Stroke(u, cap = StrokeCap.Round))
        }
        // Trazos de construcción: las diagonales, una cruz al centro y un arco de compás.
        TemaId.RENACIMIENTO -> {
            val k = c.copy(alpha = 0.18f)
            drawLine(k, Offset.Zero, Offset(w, h), 0.6f * u); drawLine(k, Offset(w, 0f), Offset(0f, h), 0.6f * u)
            val r = minOf(w, h) * 0.6f
            drawArc(c.copy(alpha = 0.28f), -90f, 90f, false, topLeft = Offset(-r, h - r), size = Size(2 * r, 2 * r), style = Stroke(0.6f * u))
            drawLine(c.copy(alpha = 0.3f), Offset(w / 2 - 3 * u, h / 2), Offset(w / 2 + 3 * u, h / 2), 0.6f * u)
            drawLine(c.copy(alpha = 0.3f), Offset(w / 2, h / 2 - 3 * u), Offset(w / 2, h / 2 + 3 * u), 0.6f * u)
        }
        // Las líneas de rumbo que salen de la esquina y la «X» en la otra punta.
        TemaId.PIRATA -> {
            val k = c.copy(alpha = 0.16f); val o = Offset(w, 0f); val l = maxOf(w, h) * 1.2f
            for (i in 0 until 7) { val a = Math.PI / 2 + i * Math.PI / 12; drawLine(k, o, Offset(o.x + (l * kotlin.math.cos(a)).toFloat(), o.y + (l * kotlin.math.sin(a)).toFloat()), 0.6f * u) }
            val x = Offset(10 * u, h - 10 * u); val s = 3 * u
            drawLine(e.tertiary.copy(alpha = 0.45f), x - Offset(s, s), x + Offset(s, s), 1.2f * u); drawLine(e.tertiary.copy(alpha = 0.45f), x + Offset(-s, s), x + Offset(s, -s), 1.2f * u)
        }
        // Cuña roja constructivista y una barra negra.
        TemaId.URSS -> {
            drawPath(Path().apply { moveTo(2 * u, h - 2 * u); lineTo(2 * u, h - 13 * u); lineTo(16 * u, h - 2 * u); close() }, c.copy(alpha = 0.3f))
            drawRect(e.onSurface.copy(alpha = 0.5f), Offset(4 * u, 4 * u), Size(11 * u, 2.4f * u))
        }
        // Escarapela azul, blanca y roja en la esquina.
        TemaId.REVOLUCION -> {
            val o = Offset(8 * u, 8 * u)
            drawCircle(e.secondary, 4.5f * u, o); drawCircle(Color(0xFFF4F1E8), 3 * u, o); drawCircle(c, 1.5f * u, o)
        }
        // Gemas arriba y abajo y muescas de runas en el borde.
        TemaId.ANTIGUAS -> {
            listOf(Offset(w / 2, 4.5f * u), Offset(w / 2, h - 4.5f * u)).forEach { o ->
                drawPath(Path().apply { moveTo(o.x, o.y - 2.6f * u); lineTo(o.x + 2 * u, o.y); lineTo(o.x, o.y + 2.6f * u); lineTo(o.x - 2 * u, o.y); close() }, e.tertiary.copy(alpha = 0.8f))
            }
            listOf(0.3f, 0.45f, 0.6f, 0.75f).forEachIndexed { i, f ->
                val y = h * f
                drawLine(c.copy(alpha = 0.45f), Offset(3 * u, y), Offset(6 * u, y + (if (i % 2 == 0) -2f else 2f) * u), 0.8f * u)
            }
        }
    }
}

/** Una tira de meandros (ganchos sobre una línea) de 6 dp de alto que empieza en [y0]. */
private fun DrawScope.grecaRel(y0: Float, c: Color, u: Float) {
    val w = size.width
    val p = Path()
    p.moveTo(6 * u, y0 + 6 * u); p.lineTo(w - 6 * u, y0 + 6 * u)
    var x = 6 * u
    while (x + 7 * u <= w - 6 * u) {
        p.moveTo(x, y0 + 6 * u); p.lineTo(x, y0); p.lineTo(x + 6 * u, y0); p.lineTo(x + 6 * u, y0 + 4 * u); p.lineTo(x + 2.5f * u, y0 + 4 * u); p.lineTo(x + 2.5f * u, y0 + 2 * u)
        x += 8 * u
    }
    drawPath(p, c, style = Stroke(0.8f * u))
}

/** Una hoja de [a] (la base) a [b] (la punta); [ancho] es lo que se abre cada lado. */
private fun hojaRel(a: Offset, b: Offset, ancho: Float): Path {
    val d = b - a; val l = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y / l, d.x / l)
    val m = (a + b) / 2f
    return Path().apply {
        moveTo(a.x, a.y)
        quadraticTo(m.x + n.x * ancho * 2, m.y + n.y * ancho * 2, b.x, b.y)
        quadraticTo(m.x - n.x * ancho * 2, m.y - n.y * ancho * 2, a.x, a.y)
        close()
    }
}

/** Una estrella de [puntas] puntas centrada en [c]. */
private fun estrellaRel(c: Offset, puntas: Int, rExt: Float, rInt: Float): Path = Path().apply {
    for (i in 0 until puntas * 2) {
        val a = -PI / 2 + i * PI / puntas
        val r = if (i % 2 == 0) rExt else rInt
        val x = c.x + r * cos(a).toFloat(); val y = c.y + r * sin(a).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Una espiral de radio [r] que se cierra hacia [o]; [sentido] (1 o −1) la refleja. */
private fun espiralRel(o: Offset, r: Float, sentido: Float): Path = Path().apply {
    for (k in 0..40) {
        val t = k / 40f
        val a = -PI / 2 - t * 2.2 * PI
        val rr = r * (1 - 0.8f * t)
        val x = o.x + sentido * rr * cos(a).toFloat(); val y = o.y + rr * sin(a).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
}

/** El lazo entre los dos recuadros. [fuerza] es |r| (0–1): engruesa el trazo; [azar] lo vuelve punteado y más tenue. */
private fun DrawScope.lazo(tema: TemaId, color: Color, fuerza: Float, azar: Boolean, fondo: Color) {
    val u = 1.dp.toPx(); val y = size.height / 2; val w = size.width
    val grosor = (1.2f + 4.5f * fuerza) * u
    val punteado = if (azar) PathEffect.dashPathEffect(floatArrayOf(5 * u, 4 * u)) else null
    val col = color.copy(alpha = if (azar) 0.65f else 1f)
    fun onda(amp: Float, ciclos: Float, fase: Float = 0f) = Path().apply {
        moveTo(0f, y); var x = 0f
        while (x <= w) { lineTo(x, y + amp * sin(2 * PI * (x / w * ciclos) + fase).toFloat()); x += 2f }
    }
    when (tema) {
        // Haz de varas (fasces) atado con correas en diagonal y una argolla en cada punta.
        TemaId.ROMANO -> {
            drawLine(col, Offset(3 * u, y), Offset(w - 3 * u, y), grosor + u, pathEffect = punteado)
            var x = 10 * u
            while (x < w - 8 * u) { drawLine(col, Offset(x - 2.5f * u, y - grosor / 2 - 2 * u), Offset(x + 2.5f * u, y + grosor / 2 + 2 * u), 1.2f * u); x += 11 * u }
            listOf(3 * u, w - 3 * u).forEach { drawCircle(col, grosor / 2 + 2 * u, Offset(it, y), style = Stroke(1.2f * u)) }
        }
        // Barra de progreso de la terminal: ocho celdas entre corchetes, llenas según la fuerza.
        TemaId.CIBER -> {
            val n = 8; val llenas = (fuerza * n).roundToInt().coerceIn(0, n)
            val m = 7 * u; val cw = (w - 2 * m) / n; val ch = grosor + 4 * u
            listOf(m - 3 * u to 1f, w - m + 3 * u to -1f).forEach { (x, d) ->
                drawLine(col, Offset(x, y - ch / 2 - u), Offset(x, y + ch / 2 + u), u)
                drawLine(col, Offset(x, y - ch / 2 - u), Offset(x + d * 2 * u, y - ch / 2 - u), u)
                drawLine(col, Offset(x, y + ch / 2 + u), Offset(x + d * 2 * u, y + ch / 2 + u), u)
            }
            for (i in 0 until n) {
                val o = Offset(m + i * cw + 0.8f * u, y - ch / 2); val s = Size(cw - 1.6f * u, ch)
                if (i < llenas) drawRect(col, o, s) else drawRect(col.copy(alpha = col.alpha * 0.5f), o, s, style = Stroke(0.8f * u, pathEffect = punteado))
            }
        }
        TemaId.NEON -> {
            val n = 8; val llenas = (fuerza * n).roundToInt().coerceIn(0, n)
            val m = 7 * u; val cw = (w - 2 * m) / n; val ch = grosor + 4 * u
            listOf(m - 3 * u to 1f, w - m + 3 * u to -1f).forEach { (x, d) ->
                drawLine(col, Offset(x, y - ch / 2 - u), Offset(x, y + ch / 2 + u), u)
                drawLine(col, Offset(x, y - ch / 2 - u), Offset(x + d * 2 * u, y - ch / 2 - u), u)
                drawLine(col, Offset(x, y + ch / 2 + u), Offset(x + d * 2 * u, y + ch / 2 + u), u)
            }
            for (i in 0 until n) {
                val o = Offset(m + i * cw + 0.8f * u, y - ch / 2); val s = Size(cw - 1.6f * u, ch)
                if (i < llenas) drawRect(col, o, s) else drawRect(col.copy(alpha = col.alpha * 0.5f), o, s, style = Stroke(0.8f * u, pathEffect = punteado))
            }
        }
        // Rama de olivo: un tallo con hojas alternas.
        TemaId.HELENO -> {
            drawLine(col, Offset(0f, y), Offset(w, y), grosor * 0.55f + 0.6f * u, cap = StrokeCap.Round, pathEffect = punteado)
            val l = 5 * u + grosor * 0.5f
            var x = 7 * u; var i = 0
            while (x < w - 7 * u) {
                val d = if (i % 2 == 0) -1f else 1f
                drawPath(hojaRel(Offset(x, y), Offset(x + l, y + d * l * 0.75f), 1.6f * u + grosor * 0.15f), col)
                x += 9 * u; i++
            }
        }
        // Filacteria: una cinta ondulada con cola de golondrina en cada punta.
        TemaId.MEDIEVAL -> {
            val ancho = grosor + 3 * u
            drawPath(onda(3 * u, 1f), col, style = Stroke(ancho, pathEffect = punteado))
            drawPath(onda(3 * u, 1f), fondo.copy(alpha = 0.5f), style = Stroke(0.6f * u))
            listOf(0f to 1f, w to -1f).forEach { (x, d) ->
                drawPath(Path().apply { moveTo(x, y - ancho / 2 - u); lineTo(x + d * 3.5f * u, y); lineTo(x, y + ancho / 2 + u); close() }, fondo)
            }
        }
        // Alambre de púas.
        TemaId.DISTOPIA -> {
            drawLine(col, Offset(0f, y), Offset(w, y), grosor * 0.7f + 0.6f * u, pathEffect = punteado)
            val b = 2.5f * u + grosor * 0.35f
            var x = 9 * u
            while (x < w - 6 * u) {
                drawLine(col, Offset(x - b, y - b), Offset(x + b, y + b), 1.1f * u); drawLine(col, Offset(x - b, y + b), Offset(x + b, y - b), 1.1f * u)
                x += 12 * u
            }
        }
        // Tubería del refugio con dos bridas.
        TemaId.REFUGIO -> {
            val t = grosor + 3 * u
            drawLine(col.copy(alpha = col.alpha * 0.35f), Offset(0f, y), Offset(w, y), t)
            listOf(-1f, 1f).forEach { s -> drawLine(col, Offset(0f, y + s * t / 2), Offset(w, y + s * t / 2), 1.2f * u, pathEffect = punteado) }
            listOf(0.3f, 0.7f).forEach { f -> drawRect(col, Offset(w * f - 1.5f * u, y - t / 2 - 2.5f * u), Size(3 * u, t + 5 * u)) }
        }
        // Cadena de eslabones: uno de frente, otro de canto.
        TemaId.ORDEN -> {
            val l = 9 * u; val alto = 4 * u + grosor * 0.7f; val trazo = 1.1f * u + grosor * 0.25f
            var x = 2 * u; var i = 0
            while (x + l <= w - 2 * u) {
                if (i % 2 == 0) drawOval(col, Offset(x, y - alto / 2), Size(l, alto), style = Stroke(trazo, pathEffect = punteado))
                else drawLine(col, Offset(x + u, y), Offset(x + l - u, y), trazo + u)
                x += l * 0.72f; i++
            }
        }
        // Tubo de vidrio con el líquido adentro y tres burbujas.
        TemaId.LABORATORIO -> {
            val r = grosor / 2 + 2.5f * u
            val vidrio = col.copy(alpha = col.alpha * 0.6f)
            listOf(-1f, 1f).forEach { s -> drawLine(vidrio, Offset(4 * u, y + s * r), Offset(w - 4 * u, y + s * r), 0.9f * u) }
            listOf(4 * u, w - 4 * u).forEach { x -> drawLine(vidrio, Offset(x, y - r), Offset(x, y + r), 0.9f * u) }
            drawLine(col, Offset(5 * u, y), Offset(w - 5 * u, y), grosor, pathEffect = punteado)
            listOf(0.3f, 0.52f, 0.74f).forEach { f -> drawCircle(fondo.copy(alpha = 0.8f), grosor * 0.22f + 0.4f * u, Offset(w * f, y)) }
        }
        // Una tabla clavada, con su veta.
        TemaId.COLONOS -> {
            val t = grosor + 3 * u
            if (azar) drawRect(col, Offset(2 * u, y - t / 2), Size(w - 4 * u, t), style = Stroke(u, pathEffect = punteado))
            else drawRect(col, Offset(2 * u, y - t / 2), Size(w - 4 * u, t))
            drawLine(fondo.copy(alpha = 0.35f), Offset(9 * u, y - t * 0.15f), Offset(w - 14 * u, y - t * 0.15f), 0.6f * u)
            listOf(6 * u, w - 6 * u).forEach { x -> drawCircle(fondo, 1.3f * u, Offset(x, y)); drawCircle(col, 0.5f * u, Offset(x, y)) }
        }
        // Cordón de charretera con un nudo al centro y una borla en cada punta.
        TemaId.NAPOLEON -> {
            drawLine(col, Offset(6 * u, y), Offset(w - 6 * u, y), grosor * 0.8f + 0.5f * u, pathEffect = punteado)
            listOf(6 * u, w - 6 * u).forEach { x ->
                drawCircle(col, grosor * 0.4f + 1.8f * u, Offset(x, y))
                for (k in -2..2) drawLine(col, Offset(x, y), Offset(x + k * 1.2f * u, y + 9 * u), 0.8f * u)
            }
            drawCircle(col, grosor * 0.3f + 1.4f * u, Offset(w / 2, y))
        }
        // Una ramita por la que caminan dos hormigas.
        TemaId.BOTANICO -> {
            drawPath(onda(4 * u, 1.5f), col, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = punteado))
            listOf(0.32f, 0.62f).forEach { f ->
                val x = w * f; val y0 = y + 4 * u * sin(2 * PI * f * 1.5).toFloat()
                val a = kotlin.math.atan2((4 * u * 2 * PI * 1.5 / w * kotlin.math.cos(2 * PI * f * 1.5)).toFloat(), 1f)
                hormiga(Offset(x, y0 - grosor), 8 * u + grosor, a, col)
            }
        }
        // Guirnalda con dos esferas colgando.
        TemaId.NAVIDAD -> {
            val p = Path().apply { moveTo(0f, y - 3 * u); quadraticTo(w / 2, y + 9 * u, w, y - 3 * u) }
            drawPath(p, col, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = punteado))
            listOf(0.33f, 0.67f).forEach { f -> val x = w * f; val y0 = y + 3.4f * u
                drawLine(col, Offset(x, y0), Offset(x, y0 + 5 * u), u); drawCircle(col, 2.5f * u + grosor * 0.4f, Offset(x, y0 + 7 * u + grosor * 0.4f)) }
        }
        // Camino de caravana: puntos y una moneda con agujero cuadrado en cada extremo.
        TemaId.SEDA -> {
            var x = 12 * u; while (x < w - 10 * u) { drawCircle(col, grosor * 0.45f + u, Offset(x, y)); x += 7 * u }
            listOf(5 * u, w - 5 * u).forEach { cx -> drawCircle(col, 5 * u, Offset(cx, y)); drawRect(fondo, Offset(cx - 1.6f * u, y - 1.6f * u), Size(3.2f * u, 3.2f * u)) }
        }
        // Arquería de ojivas sobre una línea, como un friso de capilla.
        TemaId.GOTICO -> {
            val base = y + 3 * u; val paso = 9 * u; val alto = 7 * u
            drawLine(col, Offset(0f, base), Offset(w, base), grosor * 0.8f, pathEffect = punteado)
            val p = Path(); var x = 2 * u
            while (x + paso <= w - 2 * u + 0.1f) {
                p.moveTo(x, base); p.quadraticTo(x, base - alto * 0.8f, x + paso / 2, base - alto); p.quadraticTo(x + paso, base - alto * 0.8f, x + paso, base)
                x += paso
            }
            drawPath(p, col, style = Stroke(grosor * 0.35f + 0.7f * u, pathEffect = punteado))
        }
        // Una órbita colgante entre dos estrellas, con un planeta anillado al fondo.
        TemaId.ESPACIO -> {
            drawPath(Path().apply { moveTo(5 * u, y - 2 * u); quadraticTo(w / 2, y + 14 * u, w - 5 * u, y - 2 * u) }, col,
                style = Stroke(grosor * 0.7f + 0.5f * u, cap = StrokeCap.Round, pathEffect = punteado))
            listOf(5 * u, w - 5 * u).forEach { x -> drawPath(estrellaRel(Offset(x, y - 2 * u), 4, 4 * u, 1.2f * u), col) }
            val o = Offset(w / 2, y + 6 * u)
            drawCircle(col, 2.5f * u + grosor * 0.3f, o)
            drawOval(col, Offset(o.x - 6 * u, o.y - 1.6f * u), Size(12 * u, 3.2f * u), style = Stroke(0.9f * u))
        }
        // Dos cintas que se cruzan, como en la lacería.
        TemaId.ANDALUS -> {
            drawPath(onda(6 * u, 1f), col, style = Stroke(grosor * 0.75f, pathEffect = punteado))
            drawPath(onda(6 * u, 1f, PI.toFloat()), col, style = Stroke(grosor * 0.75f, pathEffect = punteado))
        }
        // Cuerda trenzada de crin.
        TemaId.MONGOL -> {
            drawPath(onda(3.5f * u, 3f), col, style = Stroke(grosor * 0.7f, pathEffect = punteado))
            drawPath(onda(3.5f * u, 3f, PI.toFloat()), col.copy(alpha = col.alpha * 0.7f), style = Stroke(grosor * 0.7f, pathEffect = punteado))
        }
        // Línea de cota, como en los cuadernos: topes, flechas y un círculo de construcción.
        TemaId.RENACIMIENTO -> {
            val a = 4 * u; val b = w - 4 * u
            drawLine(col, Offset(a, y), Offset(b, y), grosor * 0.6f + 0.5f * u, pathEffect = punteado)
            listOf(a, b).forEach { x -> drawLine(col, Offset(x, y - 6 * u), Offset(x, y + 6 * u), 0.9f * u) }
            val f = 4 * u + grosor * 0.4f
            drawPath(Path().apply {
                moveTo(a, y); lineTo(a + f, y - f * 0.45f); lineTo(a + f, y + f * 0.45f); close()
                moveTo(b, y); lineTo(b - f, y - f * 0.45f); lineTo(b - f, y + f * 0.45f); close()
            }, col)
            drawCircle(col.copy(alpha = col.alpha * 0.45f), 5 * u, Offset(w / 2, y), style = Stroke(0.6f * u))
        }
        // Un cabo tendido entre los dos: dos cordones que se enroscan y un nudo al medio.
        TemaId.PIRATA -> {
            val a = 4 * u; val b = w - 4 * u
            drawPath(onda(2.2f * u + grosor * 0.2f, (b - a) / (10 * u)), col, style = Stroke(grosor * 0.5f + 0.6f * u))
            drawPath(onda(2.2f * u + grosor * 0.2f, (b - a) / (10 * u), PI.toFloat()), col.copy(alpha = col.alpha * 0.55f), style = Stroke(grosor * 0.5f + 0.6f * u))
            drawCircle(col, 3 * u + grosor * 0.3f, Offset(w / 2, y))
        }
        // Viga de celosía constructivista: dos cordones y diagonales en zigzag.
        TemaId.URSS -> {
            val t = 6 * u + grosor; val trazo = grosor * 0.45f + 0.8f * u
            listOf(-1f, 1f).forEach { s -> drawLine(col, Offset(0f, y + s * t / 2), Offset(w, y + s * t / 2), trazo, pathEffect = punteado) }
            val p = Path().apply {
                moveTo(0f, y + t / 2); var x = 0f; var arriba = true
                while (x + 7 * u <= w + 0.1f) { x += 7 * u; lineTo(x, if (arriba) y - t / 2 else y + t / 2); arriba = !arriba }
            }
            drawPath(p, col, style = Stroke(0.9f * u + grosor * 0.2f))
        }
        // Cordel de fiesta con banderines, como en la Fiesta de la Federación.
        TemaId.REVOLUCION -> {
            val y0 = y - 2 * u; val yc = y + 6 * u
            drawPath(Path().apply { moveTo(0f, y0); quadraticTo(w / 2, yc, w, y0) }, col, style = Stroke(0.9f * u + grosor * 0.3f, pathEffect = punteado))
            val n = 6; val largo = 5 * u + grosor * 0.8f
            for (i in 0 until n) {
                val t = (i + 0.5f) / n; val x = t * w
                val yt = (1 - t) * (1 - t) * y0 + 2 * (1 - t) * t * yc + t * t * y0
                drawPath(Path().apply { moveTo(x - 2.6f * u, yt); lineTo(x + 2.6f * u, yt); lineTo(x, yt + largo); close() }, col.copy(alpha = col.alpha * (if (i % 2 == 0) 1f else 0.55f)))
            }
        }
        // Báculo con muescas: grupos de rayas arriba, cruzadas, abajo y en diagonal, como un alfabeto de palos.
        TemaId.ANTIGUAS -> {
            drawLine(col, Offset(0f, y), Offset(w, y), grosor * 0.7f + 0.6f * u, pathEffect = punteado)
            val l = 5 * u + grosor * 0.3f
            listOf(Triple(0.18f, 1, 0), Triple(0.38f, 3, 1), Triple(0.62f, 2, 2), Triple(0.82f, 4, 3)).forEach { (f, n, tipo) ->
                for (k in 0 until n) {
                    val x = w * f + (k - (n - 1) / 2f) * 2.6f * u
                    when (tipo) {
                        0 -> drawLine(col, Offset(x, y), Offset(x, y - l), u)
                        1 -> drawLine(col, Offset(x, y - l * 0.6f), Offset(x, y + l * 0.6f), u)
                        2 -> drawLine(col, Offset(x, y), Offset(x, y + l), u)
                        else -> drawLine(col, Offset(x - l * 0.3f, y + l * 0.5f), Offset(x + l * 0.3f, y - l * 0.5f), u)
                    }
                }
            }
        }
    }
}
