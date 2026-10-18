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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.Variable
import kotlin.math.PI
import kotlin.math.abs
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

/** Un recuadro con el nombre de la variable y su grupo, con el marco de cada tema. */
@Composable
private fun Recuadro(v: Variable, tema: TemaId, modifier: Modifier) {
    val e = MaterialTheme.colorScheme
    val forma = when (tema) {
        TemaId.CIBER, TemaId.DISTOPIA, TemaId.URSS, TemaId.ORDEN, TemaId.REFUGIO -> RoundedCornerShape(2.dp)
        TemaId.GOTICO -> RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 3.dp, bottomEnd = 3.dp)
        TemaId.ANDALUS -> RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
        TemaId.BOTANICO, TemaId.NAVIDAD, TemaId.LABORATORIO, TemaId.ESPACIO -> RoundedCornerShape(14.dp)
        else -> RoundedCornerShape(5.dp)
    }
    Box(modifier.background(e.primary.copy(alpha = 0.07f), forma).border(1.5.dp, e.primary.copy(alpha = 0.7f), forma).drawBehind { adorno(tema, e.primary) }
        .padding(horizontal = 10.dp, vertical = if (tema == TemaId.GOTICO || tema == TemaId.ANDALUS) 14.dp else 9.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(v.nombre, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, maxLines = 4)
            Text(v.grupo.lowercase(), style = MaterialTheme.typography.labelSmall, color = e.onSurfaceVariant, textAlign = TextAlign.Center, maxLines = 1,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** Detalles de las esquinas del recuadro: remaches, miras, puntos de oro. */
private fun DrawScope.adorno(tema: TemaId, c: Color) {
    val u = 1.dp.toPx(); val w = size.width; val h = size.height
    when (tema) {
        TemaId.CIBER, TemaId.ESPACIO, TemaId.LABORATORIO -> listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f))
            .forEach { (o, d) -> drawLine(c, o + Offset(d.x * 3 * u, d.y * 3 * u), o + Offset(d.x * 11 * u, d.y * 3 * u), 2 * u); drawLine(c, o + Offset(d.x * 3 * u, d.y * 3 * u), o + Offset(d.x * 3 * u, d.y * 11 * u), 2 * u) }
        TemaId.ORDEN, TemaId.URSS, TemaId.REFUGIO -> listOf(Offset(5 * u, 5 * u), Offset(w - 5 * u, 5 * u), Offset(5 * u, h - 5 * u), Offset(w - 5 * u, h - 5 * u)).forEach { drawCircle(c, 1.8f * u, it) }
        TemaId.MEDIEVAL, TemaId.NAPOLEON, TemaId.COLONOS, TemaId.RENACIMIENTO -> drawRect(c.copy(alpha = 0.35f), Offset(3 * u, 3 * u), androidx.compose.ui.geometry.Size(w - 6 * u, h - 6 * u), style = Stroke(0.8f * u))
        else -> {}
    }
}

/** El lazo entre los dos recuadros. [fuerza] es |r| (0–1). */
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
        // Enredadera con una hoja; guirnalda en Navidad.
        TemaId.BOTANICO -> {
            drawPath(onda(4 * u, 1.5f), col, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = punteado))
            // Hojitas que salen del tallo, una arriba y una abajo.
            listOf(0.3f to -1f, 0.7f to 1f).forEach { (f, lado) ->
                val x = w * f; val y0 = y + 4 * u * sin(2 * PI * f * 1.5).toFloat(); val l = 7 * u + grosor
                drawPath(Path().apply { moveTo(x, y0); quadraticTo(x + l * 0.2f, y0 + lado * l * 0.9f, x + l * 0.9f, y0 + lado * l); quadraticTo(x + l * 0.8f, y0 + lado * l * 0.2f, x, y0); close() }, col)
            }
        }
        // Guirnalda con dos esferas colgando.
        TemaId.NAVIDAD -> {
            val p = Path().apply { moveTo(0f, y - 3 * u); quadraticTo(w / 2, y + 9 * u, w, y - 3 * u) }
            drawPath(p, col, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = punteado))
            listOf(0.33f, 0.67f).forEach { f -> val x = w * f; val y0 = y + 3.4f * u
                drawLine(col, Offset(x, y0), Offset(x, y0 + 5 * u), u); drawCircle(col, 2.5f * u + grosor * 0.4f, Offset(x, y0 + 7 * u + grosor * 0.4f)) }
        }
        // Cuerda trenzada de crin.
        TemaId.MONGOL -> {
            drawPath(onda(3.5f * u, 3f), col, style = Stroke(grosor * 0.7f, pathEffect = punteado))
            drawPath(onda(3.5f * u, 3f, PI.toFloat()), col.copy(alpha = col.alpha * 0.7f), style = Stroke(grosor * 0.7f, pathEffect = punteado))
        }
        // Dos cintas que se cruzan, como en la lacería.
        TemaId.ANDALUS -> {
            drawPath(onda(6 * u, 1f), col, style = Stroke(grosor * 0.75f, pathEffect = punteado))
            drawPath(onda(6 * u, 1f, PI.toFloat()), col, style = Stroke(grosor * 0.75f, pathEffect = punteado))
        }
        // Cable de circuito con codos y nodos.
        TemaId.CIBER, TemaId.LABORATORIO, TemaId.ESPACIO -> {
            val p = Path().apply { moveTo(0f, y); lineTo(w * 0.3f, y); lineTo(w * 0.4f, y - 6 * u); lineTo(w * 0.6f, y - 6 * u); lineTo(w * 0.7f, y); lineTo(w, y) }
            drawPath(p, col, style = Stroke(grosor, pathEffect = punteado))
            listOf(Offset(0f, y), Offset(w, y)).forEach { drawCircle(col, grosor + 1.5f * u, it) }
        }
        // Barra gruesa con remaches.
        TemaId.URSS, TemaId.DISTOPIA, TemaId.ORDEN, TemaId.REFUGIO -> {
            drawLine(col, Offset(0f, y), Offset(w, y), grosor + 1.5f * u, pathEffect = punteado)
            listOf(0.15f, 0.85f).forEach { drawCircle(col, grosor * 0.6f + 2 * u, Offset(w * it, y)) }
        }
        // Camino de caravana: puntos y una moneda en cada extremo.
        TemaId.SEDA -> {
            var x = 12 * u; while (x < w - 10 * u) { drawCircle(col, grosor * 0.45f + u, Offset(x, y)); x += 7 * u }
            // Monedas con agujero cuadrado en los extremos.
            listOf(5 * u, w - 5 * u).forEach { cx -> drawCircle(col, 5 * u, Offset(cx, y)); drawRect(fondo, Offset(cx - 1.6f * u, y - 1.6f * u), androidx.compose.ui.geometry.Size(3.2f * u, 3.2f * u)) }
        }
        // Tinta: un trazo con un rombo en cada punta (scriptorium, gótico, napoleónico, Renacimiento, colonos…).
        else -> {
            drawLine(col, Offset(0f, y), Offset(w, y), grosor, pathEffect = punteado)
            listOf(4 * u, w - 4 * u).forEach { x ->
                val k = grosor * 0.8f + 3 * u
                drawPath(Path().apply { moveTo(x - k, y); lineTo(x, y - k); lineTo(x + k, y); lineTo(x, y + k); close() }, col)
            }
        }
    }
}
