package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar

/*
 * Piezas de ciudad moderna, vista de noche o entre brumas: torres con ventanas, anuncios de luz (sin texto),
 * pasarelas, tuberías y cables. Las usa Ciudad neón; sirven también para Art déco, Distopía, Soviético y Roma.
 */

/**
 * Una torre lejana que ocupa [zona]: el cuerpo, que se aclara hacia abajo con la bruma de la calle, sus nervios, un
 * remate ([remate]: 0 liso, 1 escalonado, 2 con antena, 3 con depósito) y la retícula de ventanas. [encendidas], de 0
 * a 1: cuántas tienen luz. [luces]: los colores de esas luces. [lejos] la desvanece.
 */
fun DrawScope.torre(l: Luces, zona: Rect, propio: Color, lejos: Float, azar: Azar, encendidas: Float, luces: List<Color>, remate: Int = 0, resplandor: Color? = null) {
    val arriba = l.tono(propio, lejos); val abajo = lerp(arriba, resplandor ?: l.bruma, 0.25f + 0.45f * lejos)
    drawRect(Brush.verticalGradient(listOf(arriba, abajo), zona.top, zona.bottom), zona.topLeft, zona.size)
    val w = zona.width
    when (remate) {
        1 -> { drawRect(arriba, Offset(zona.left + w * 0.18f, zona.top - w * 0.16f), Size(w * 0.64f, w * 0.17f)); drawRect(arriba, Offset(zona.left + w * 0.34f, zona.top - w * 0.3f), Size(w * 0.32f, w * 0.15f)) }
        2 -> { drawLine(arriba, Offset(zona.left + w * 0.3f, zona.top), Offset(zona.left + w * 0.3f, zona.top - w * 0.9f), maxOf(0.5f, w * 0.03f)); drawRect(arriba, Offset(zona.left + w * 0.55f, zona.top - w * 0.12f), Size(w * 0.3f, w * 0.13f)); drawCircle(Color(0xFFFF5A4A).copy(alpha = 0.9f * (1f - 0.5f * lejos)), maxOf(0.5f, w * 0.03f), Offset(zona.left + w * 0.3f, zona.top - w * 0.9f)) }
        3 -> { drawRoundRect(arriba, Offset(zona.left + w * 0.12f, zona.top - w * 0.3f), Size(w * 0.4f, w * 0.3f), CornerRadius(w * 0.06f)); drawLine(arriba, Offset(zona.left + w * 0.7f, zona.top), Offset(zona.left + w * 0.7f, zona.top - w * 0.42f), maxOf(0.5f, w * 0.025f)) }
        else -> Unit
    }
    // El canto del lado de la calle iluminada y los nervios.
    val nervio = lerp(arriba, Color.Black, 0.25f)
    val cada = maxOf(3.2f, w / (3 + azar.entero(4)))
    var x = zona.left + cada
    while (x < zona.right - 1f) { drawLine(nervio.copy(alpha = 0.5f), Offset(x, zona.top), Offset(x, zona.bottom), 0.4f); x += cada }
    // Las ventanas: una retícula; algunas columnas enteras a oscuras, como pasa en una torre de verdad.
    val vw = maxOf(1.1f, 2.6f * (1f - 0.55f * lejos)); val vh = vw * 0.8f; val pasoX = vw * 2.1f; val pasoY = vh * 2.5f
    clipRect(zona.left, zona.top, zona.right, zona.bottom) {
        var y = zona.top + pasoY * 0.8f
        while (y < zona.bottom - 2f) {
            val pisoApagado = azar.real() < 0.16f
            var xx = zona.left + pasoX * 0.6f
            while (xx < zona.right - vw) {
                val u = azar.real(); val c = luces[azar.entero(luces.size)]
                val baja = ((zona.bottom - y) / zona.height.coerceAtLeast(1f)).coerceIn(0f, 1f)
                if (!pisoApagado && u < encendidas) drawRect(c.copy(alpha = (0.5f + 0.5f * azar.real()) * (1f - 0.55f * lejos) * (0.45f + 0.55f * baja)), Offset(xx, y), Size(vw, vh))
                else if (u < encendidas + 0.3f) drawRect(Color.Black.copy(alpha = 0.16f), Offset(xx, y), Size(vw, vh))
                xx += pasoX
            }
            y += pasoY
        }
    }
}

/**
 * Un anuncio de luz en [zona], sin texto: una pantalla con franjas, un disco o unas barras de su [color]. [encendido],
 * de 0 a 1. Apagado queda el bastidor oscuro. [lejos] lo desvanece.
 */
fun DrawScope.anuncio(l: Luces, zona: Rect, color: Color, encendido: Float, azar: Azar, lejos: Float = 0.5f) {
    val marco = l.tono(Color(0xFF2A2840), lejos)
    drawRect(marco, Offset(zona.left - 0.8f, zona.top - 0.8f), Size(zona.width + 1.6f, zona.height + 1.6f))
    val k = encendido.coerceIn(0f, 1f) * (1f - 0.45f * lejos)
    val fondo = lerp(l.tono(Color(0xFF1C1A30), lejos), color, 0.5f * k)
    drawRect(Brush.verticalGradient(listOf(lerp(fondo, Color.White, 0.14f * k), fondo), zona.top, zona.bottom), zona.topLeft, zona.size)
    if (k > 0.02f) {
        val claro = lerp(color, Color.White, 0.55f).copy(alpha = k)
        clipRect(zona.left, zona.top, zona.right, zona.bottom) {
            when (azar.entero(4)) {
                0 -> for (i in 0..2) drawRect(claro.copy(alpha = k * (0.9f - 0.25f * i)), Offset(zona.left + zona.width * 0.14f, zona.top + zona.height * (0.18f + 0.26f * i)), Size(zona.width * (0.72f - 0.2f * i), zona.height * 0.12f))
                1 -> { drawCircle(claro, minOf(zona.width, zona.height) * 0.28f, zona.center); drawCircle(fondo, minOf(zona.width, zona.height) * 0.15f, zona.center) }
                2 -> for (i in 0..3) drawRect(claro.copy(alpha = k * 0.85f), Offset(zona.left + zona.width * (0.12f + 0.21f * i), zona.top + zona.height * (0.62f - 0.13f * ((i * 7) % 4))), Size(zona.width * 0.12f, zona.height * (0.26f + 0.13f * ((i * 7) % 4))))
                else -> { drawLine(claro, Offset(zona.left + zona.width * 0.15f, zona.top + zona.height * 0.7f), Offset(zona.left + zona.width * 0.5f, zona.top + zona.height * 0.25f), zona.height * 0.1f, StrokeCap.Round); drawLine(claro, Offset(zona.left + zona.width * 0.5f, zona.top + zona.height * 0.25f), Offset(zona.left + zona.width * 0.85f, zona.top + zona.height * 0.7f), zona.height * 0.1f, StrokeCap.Round) }
            }
        }
        drawRect(color.copy(alpha = 0.22f * k), Offset(zona.left - zona.width * 0.25f, zona.top - zona.height * 0.3f), Size(zona.width * 1.5f, zona.height * 1.6f), blendMode = BlendMode.Plus)
    }
}

/**
 * Una pasarela o un puente entre torres, a la altura [y], de [x0] a [x1]: el tablero, la baranda, los tirantes y,
 * por debajo, una fila de luces. [luces], de 0 a 1: cuánto se ven esas luces.
 */
fun DrawScope.pasarela(l: Luces, y: Float, x0: Float, x1: Float, alto: Float, propio: Color, lejos: Float, luces: Float, colorLuz: Color = Color(0xFFFFD9A0)) {
    val c = l.tono(propio, lejos)
    drawRect(c, Offset(x0, y), Size(x1 - x0, alto))
    drawLine(lerp(c, Color.White, 0.12f), Offset(x0, y), Offset(x1, y), maxOf(0.4f, alto * 0.14f))
    drawLine(c, Offset(x0, y - alto * 0.9f), Offset(x1, y - alto * 0.9f), maxOf(0.35f, alto * 0.1f))
    var x = x0 + alto
    while (x < x1) {
        drawLine(c, Offset(x, y - alto * 0.9f), Offset(x, y), maxOf(0.3f, alto * 0.07f))
        if (luces > 0.02f) drawCircle(colorLuz.copy(alpha = (0.85f * luces * (1f - 0.5f * lejos)).coerceIn(0f, 1f)), maxOf(0.45f, alto * 0.16f), Offset(x + alto * 0.5f, y + alto * 1.05f))
        x += alto * 2.2f
    }
}

/** Una tubería de [desde] a [hasta] (recta), con su brillo por arriba o por un lado, sus abrazaderas y sus juntas. */
fun DrawScope.tuberia(a: Alumbrado, desde: Offset, hasta: Offset, grueso: Float, propio: Color, lejos: Float = 0f) {
    val medio = Offset((desde.x + hasta.x) / 2f, (desde.y + hasta.y) / 2f)
    val vertical = kotlin.math.abs(hasta.y - desde.y) > kotlin.math.abs(hasta.x - desde.x)
    val c = a.en(medio, propio, 0f, lejos)
    drawLine(lerp(c, Color.Black, 0.45f), desde + Offset(if (vertical) grueso * 0.3f else 0f, if (vertical) 0f else grueso * 0.3f), hasta + Offset(if (vertical) grueso * 0.3f else 0f, if (vertical) 0f else grueso * 0.3f), grueso)
    drawLine(c, desde, hasta, grueso)
    val luz = a.en(medio, lerp(propio, Color.White, 0.5f), if (vertical) -1f else 0f, lejos)
    drawLine(luz.copy(alpha = 0.7f), desde + Offset(if (vertical) -grueso * 0.25f else 0f, if (vertical) 0f else -grueso * 0.25f), hasta + Offset(if (vertical) -grueso * 0.25f else 0f, if (vertical) 0f else -grueso * 0.25f), grueso * 0.22f)
    val largo = (hasta - desde).getDistance(); val n = (largo / 34f).toInt().coerceAtLeast(1)
    for (i in 1..n) {
        val p = desde + (hasta - desde) * (i / (n + 1f))
        if (vertical) drawRect(lerp(c, Color.Black, 0.3f), Offset(p.x - grueso * 0.75f, p.y - grueso * 0.3f), Size(grueso * 1.5f, grueso * 0.6f))
        else drawRect(lerp(c, Color.Black, 0.3f), Offset(p.x - grueso * 0.3f, p.y - grueso * 0.75f), Size(grueso * 0.6f, grueso * 1.5f))
    }
}

/** Un cable que cuelga de [a] a [b], con [caida] en el centro. */
fun DrawScope.cable(desde: Offset, hasta: Offset, caida: Float, color: Color, grueso: Float = 0.7f) {
    val p = cuerda(desde, hasta, caida, 16)
    val trazo = Path().apply { moveTo(p[0].x, p[0].y); for (i in 1 until p.size) lineTo(p[i].x, p[i].y) }
    drawPath(trazo, color, style = Stroke(grueso, cap = StrokeCap.Round))
}
