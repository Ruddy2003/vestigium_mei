package io.github.ruddy2003.vestigium.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los íconos de las pistas de la cinta, dibujados todos en el mismo cuadro y con el mismo trazo,
 * para que se vean del mismo tamaño (los caracteres de texto de antes no medían lo mismo en cada letra).
 */
@Composable
fun IconoPista(id: String, color: Color, modifier: Modifier = Modifier, tam: Dp = 18.dp) {
    Canvas(modifier.size(tam)) { iconoPista(id, color) }
}

fun DrawScope.iconoPista(id: String, c: Color) {
    val w = size.width; val h = size.height
    val t = Stroke(w * 0.09f, cap = StrokeCap.Round)
    val o = Offset(w / 2, h / 2)
    when (id) {
        "sueno" -> mediaLuna(Offset(w * 0.46f, h * 0.5f), h * 0.4f, c)
        "enfoque" -> {
            drawCircle(c, w * 0.4f, o, style = t)
            drawLine(c, o, Offset(o.x, h * 0.24f), t.width, StrokeCap.Round)
            drawLine(c, o, Offset(w * 0.7f, o.y), t.width, StrokeCap.Round)
        }
        "lugares" -> {
            drawPath(Path().apply { moveTo(w * 0.1f, h * 0.48f); lineTo(w * 0.5f, h * 0.12f); lineTo(w * 0.9f, h * 0.48f) }, c, style = t)
            drawRect(c, Offset(w * 0.22f, h * 0.46f), Size(w * 0.56f, h * 0.42f), style = t)
        }
        "ejercicio" -> drawPath(Path().apply { moveTo(w * 0.06f, h * 0.86f); lineTo(w * 0.38f, h * 0.24f); lineTo(w * 0.56f, h * 0.56f); lineTo(w * 0.7f, h * 0.38f); lineTo(w * 0.94f, h * 0.86f); close() }, c, style = t)
        "pantalla" -> {
            drawRoundRect(c, Offset(w * 0.28f, h * 0.06f), Size(w * 0.44f, h * 0.88f), CornerRadius(w * 0.08f), style = t)
            drawLine(c, Offset(w * 0.44f, h * 0.8f), Offset(w * 0.56f, h * 0.8f), t.width, StrokeCap.Round)
        }
        "avisos" -> {
            drawPath(Path().apply {
                moveTo(w * 0.2f, h * 0.74f); quadraticTo(w * 0.28f, h * 0.62f, w * 0.28f, h * 0.44f); quadraticTo(w * 0.3f, h * 0.14f, w * 0.5f, h * 0.14f)
                quadraticTo(w * 0.7f, h * 0.14f, w * 0.72f, h * 0.44f); quadraticTo(w * 0.72f, h * 0.62f, w * 0.8f, h * 0.74f); close()
            }, c, style = t)
            drawCircle(c, w * 0.07f, Offset(w * 0.5f, h * 0.86f))
        }
        "luz" -> {
            drawCircle(c, w * 0.2f, o, style = t)
            for (k in 0 until 8) {
                val a = k * PI / 4
                drawLine(c, Offset(o.x + (w * 0.32f) * cos(a).toFloat(), o.y + (h * 0.32f) * sin(a).toFloat()),
                    Offset(o.x + (w * 0.45f) * cos(a).toFloat(), o.y + (h * 0.45f) * sin(a).toFloat()), t.width, StrokeCap.Round)
            }
        }
        "musica" -> {
            drawCircle(c, w * 0.13f, Offset(w * 0.32f, h * 0.76f))
            drawCircle(c, w * 0.13f, Offset(w * 0.74f, h * 0.66f))
            drawLine(c, Offset(w * 0.44f, h * 0.76f), Offset(w * 0.44f, h * 0.18f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.86f, h * 0.66f), Offset(w * 0.86f, h * 0.1f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.44f, h * 0.18f), Offset(w * 0.86f, h * 0.1f), t.width, StrokeCap.Round)
        }
        "momentos" -> {
            val r = w * 0.44f
            drawPath(Path().apply {
                moveTo(o.x, o.y - r); quadraticTo(o.x, o.y, o.x + r, o.y); quadraticTo(o.x, o.y, o.x, o.y + r)
                quadraticTo(o.x, o.y, o.x - r, o.y); quadraticTo(o.x, o.y, o.x, o.y - r); close()
            }, c)
        }
        "comidas" -> {
            drawPath(Path().apply { moveTo(w * 0.08f, h * 0.46f); lineTo(w * 0.92f, h * 0.46f); quadraticTo(w * 0.88f, h * 0.88f, w * 0.5f, h * 0.88f); quadraticTo(w * 0.12f, h * 0.88f, w * 0.08f, h * 0.46f); close() }, c, style = t)
            drawLine(c, Offset(w * 0.4f, h * 0.36f), Offset(w * 0.62f, h * 0.1f), t.width, StrokeCap.Round)
        }
        "gastos" -> {
            drawCircle(c, w * 0.4f, o, style = t)
            drawLine(c, Offset(o.x, h * 0.22f), Offset(o.x, h * 0.78f), t.width * 0.8f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(w * 0.64f, h * 0.36f); quadraticTo(w * 0.5f, h * 0.28f, w * 0.38f, h * 0.38f); quadraticTo(w * 0.36f, h * 0.5f, w * 0.5f, h * 0.5f)
                quadraticTo(w * 0.64f, h * 0.52f, w * 0.62f, h * 0.62f); quadraticTo(w * 0.5f, h * 0.72f, w * 0.36f, h * 0.64f) }, c, style = Stroke(t.width * 0.8f, cap = StrokeCap.Round))
        }
        "fotos" -> {
            drawRoundRect(c, Offset(w * 0.08f, h * 0.28f), Size(w * 0.84f, h * 0.6f), CornerRadius(w * 0.08f), style = t)
            drawRect(c, Offset(w * 0.34f, h * 0.16f), Size(w * 0.32f, h * 0.12f))
            drawCircle(c, w * 0.16f, Offset(o.x, h * 0.58f), style = t)
        }
        "sustancias" -> {
            // Una copa.
            drawPath(Path().apply { moveTo(w * 0.22f, h * 0.1f); lineTo(w * 0.78f, h * 0.1f); quadraticTo(w * 0.78f, h * 0.5f, w * 0.5f, h * 0.52f); quadraticTo(w * 0.22f, h * 0.5f, w * 0.22f, h * 0.1f); close() }, c, style = t)
            drawLine(c, Offset(w * 0.5f, h * 0.52f), Offset(w * 0.5f, h * 0.86f), t.width, StrokeCap.Round)
            drawLine(c, Offset(w * 0.3f, h * 0.88f), Offset(w * 0.7f, h * 0.88f), t.width, StrokeCap.Round)
        }
        "cafeina" -> {
            drawPath(Path().apply { moveTo(w * 0.14f, h * 0.34f); lineTo(w * 0.7f, h * 0.34f); lineTo(w * 0.64f, h * 0.88f); lineTo(w * 0.2f, h * 0.88f); close() }, c, style = t)
            drawArc(c, -90f, 180f, false, Offset(w * 0.6f, h * 0.44f), Size(w * 0.26f, h * 0.24f), style = t)
            drawLine(c, Offset(w * 0.34f, h * 0.22f), Offset(w * 0.38f, h * 0.06f), t.width * 0.7f, StrokeCap.Round)
            drawLine(c, Offset(w * 0.5f, h * 0.22f), Offset(w * 0.54f, h * 0.06f), t.width * 0.7f, StrokeCap.Round)
        }
        else -> drawCircle(c, w * 0.3f, o, style = t)
    }
}
