package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

// =====================================================================
//  Edad Media
// =====================================================================

/** La cruz patada: cuatro brazos que se abren hacia la punta. */
internal fun cruzPatada(c: Offset, r: Float): Path = Path().apply {
    val a = r * 0.13f; val b = r * 0.42f
    moveTo(c.x - a, c.y - a)
    lineTo(c.x - b, c.y - r); lineTo(c.x + b, c.y - r); lineTo(c.x + a, c.y - a)
    lineTo(c.x + r, c.y - b); lineTo(c.x + r, c.y + b); lineTo(c.x + a, c.y + a)
    lineTo(c.x + b, c.y + r); lineTo(c.x - b, c.y + r); lineTo(c.x - a, c.y + a)
    lineTo(c.x - r, c.y + b); lineTo(c.x - r, c.y - b); close()
}

/** Un círculo abierto, la marca de la duda en casi todos los temas (cada uno con su color y su trazo). */
internal fun DrawScope.circuloDuda(color: Color) {
    val s = size.minDimension
    drawCircle(color, s * 0.32f, center, style = Stroke(s * 0.11f))
}

/**
 * «Hecho comprobado»: un sello lleno de [color] con la palomita recortada en [hueco] (el papel). Más fuerte que «me
 * convence»: no es una opinión, está comprobado. [cuadrado]: sello cuadrado en lugar de redondo.
 */
internal fun DrawScope.selloHecho(color: Color, hueco: Color, cuadrado: Boolean = false) {
    val s = size.minDimension; val c = center
    if (cuadrado) drawRect(color, Offset(c.x - s * 0.38f, c.y - s * 0.38f), Size(s * 0.76f, s * 0.76f)) else drawCircle(color, s * 0.42f, c)
    drawPath(Path().apply { moveTo(c.x - s * 0.2f, c.y + s * 0.01f); lineTo(c.x - s * 0.05f, c.y + s * 0.16f); lineTo(c.x + s * 0.22f, c.y - s * 0.15f) },
        hueco, style = Stroke(s * 0.1f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

/** Escuadras de metal en las cuatro esquinas de un interior, sin salirse de sus paredes de grosor [pl]. */
internal fun DrawScope.escuadrasPared(w: Float, h: Float, pl: Float, largo: Float, color: Color) {
    listOf(0f to 0f, w to 0f, 0f to h, w to h).forEach { (x, y) ->
        val sx = if (x == 0f) 0f else x - largo; val sy = if (y == 0f) 0f else y - pl
        drawRect(color, Offset(sx, sy), Size(largo, pl))
        drawRect(color, Offset(if (x == 0f) 0f else x - pl, if (y == 0f) 0f else y - largo), Size(pl, largo))
    }
}

/** Dos trazos cruzados de [color]: la marca del desacuerdo. */
internal fun DrawScope.aspa(color: Color, grosor: Float = 0.12f) {
    val s = size.minDimension; val c = center
    drawLine(color, Offset(c.x - s * 0.32f, c.y - s * 0.32f), Offset(c.x + s * 0.32f, c.y + s * 0.32f), s * grosor, StrokeCap.Round)
    drawLine(color, Offset(c.x + s * 0.32f, c.y - s * 0.32f), Offset(c.x - s * 0.32f, c.y + s * 0.32f), s * grosor, StrokeCap.Round)
}
