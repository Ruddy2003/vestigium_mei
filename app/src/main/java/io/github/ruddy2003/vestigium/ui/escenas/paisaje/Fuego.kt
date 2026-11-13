package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import kotlin.math.pow
import kotlin.math.sin

/** El color del resplandor de una llama. */
val LLAMA = Color(0xFFFFB03C)

private fun lengua(pie: Offset, ancho: Float, alto: Float, vaiven: Float) = Path().apply {
    moveTo(pie.x - ancho, pie.y)
    cubicTo(pie.x - ancho * 1.1f, pie.y - alto * 0.45f, pie.x - ancho * 0.1f + vaiven * 0.4f, pie.y - alto * 0.6f, pie.x + vaiven, pie.y - alto)
    cubicTo(pie.x + ancho * 0.3f + vaiven * 0.4f, pie.y - alto * 0.55f, pie.x + ancho * 1.1f, pie.y - alto * 0.4f, pie.x + ancho, pie.y)
    close()
}

/**
 * Una llama con el [pie] en la leña: tres lenguas (naranja, ámbar y amarillo) que se mecen con el [reloj]. Da luz
 * propia: no la apaga la noche. [semilla] desfasa una llama de otra para que no bailen a la vez.
 */
fun DrawScope.llama(pie: Offset, alto: Float, reloj: Float, semilla: Int = 0) {
    val f = semilla * 1.7f
    val late = 0.86f + 0.14f * sin(reloj * 9f + f)
    val vaiven = alto * 0.16f * sin(reloj * 6.3f + f) + alto * 0.06f * sin(reloj * 14.1f + f * 2f)
    val ancho = alto * 0.34f
    drawPath(lengua(pie, ancho, alto * late, vaiven), Color(0xFFFF6A1A).copy(alpha = 0.92f))
    drawPath(lengua(pie + Offset(ancho * 0.1f * sin(reloj * 5f + f), 0f), ancho * 0.68f, alto * 0.74f * late, vaiven * 0.7f), Color(0xFFFFA42C))
    drawPath(lengua(pie, ancho * 0.36f, alto * 0.46f * late, vaiven * 0.4f), Color(0xFFFFE694))
}

/**
 * El resplandor de un fuego sobre lo que tiene alrededor: se suma a lo que haya debajo. [fuerza], de 0 a 1; de día casi
 * no se nota, así que conviene multiplicarla por [Luces.noche].
 */
fun DrawScope.resplandor(centro: Offset, radio: Float, fuerza: Float, color: Color = LLAMA) {
    if (fuerza <= 0.01f) return
    drawCircle(Brush.radialGradient(listOf(color.copy(alpha = (0.55f * fuerza).coerceIn(0f, 1f)), color.copy(alpha = (0.16f * fuerza).coerceIn(0f, 1f)), Color.Transparent), centro, radio),
        radio, centro, blendMode = BlendMode.Plus)
}

/** Brasas: un montón oscuro con puntos rojos que laten despacio. */
fun DrawScope.brasas(l: Luces, centro: Offset, ancho: Float, reloj: Float) {
    drawOval(l.tono(Color(0xFF2A2220)), Offset(centro.x - ancho / 2f, centro.y - ancho * 0.16f), Size(ancho, ancho * 0.32f))
    for (i in 0 until 5) {
        val a = 0.45f + 0.4f * sin(reloj * 1.3f + i * 2.1f)
        drawCircle(Color(0xFFFF5A1E).copy(alpha = a.coerceIn(0f, 1f)), ancho * 0.05f, Offset(centro.x - ancho * 0.3f + ancho * 0.15f * i, centro.y - ancho * 0.03f * (i % 2)))
    }
}

/**
 * Una columna de humo que sube desde [pie] hasta [alto] de altura y se ensancha hasta [ancho]. Las bocanadas suben
 * con el [reloj] y el [viento] (de −1, hacia la izquierda, a 1) las tumba. [oscuro], de 0 (humo blanco de leña) a 1
 * (humo negro de brea o de incendio). [lejos] lo desvanece con la distancia.
 */
fun DrawScope.humo(l: Luces, pie: Offset, alto: Float, ancho: Float, reloj: Float, viento: Float = 0f, oscuro: Float = 0.2f, lejos: Float = 0f, semilla: Int = 0, bocanadas: Int = 10, fuerza: Float = 1f) {
    val propio = lerp(Color(0xFFEDEAE4), Color(0xFF2E2A28), oscuro)
    val color = lerp(l.tono(propio, lejos), l.medio, 0.15f)
    val ritmo = 0.07f + 0.015f * (semilla % 3)
    val cuantas = bocanadas * 2
    for (i in 0 until cuantas) {
        val p = ((reloj * ritmo + i / cuantas.toFloat() + semilla * 0.137f) % 1f + 1f) % 1f
        val y = pie.y - alto * p
        val x = pie.x + viento * alto * 0.75f * p.pow(1.5f) + sin(p * 7f + i * 1.3f + semilla) * ancho * 0.14f * p
        val r = ancho * (0.16f + 0.55f * p)
        val a = (1f - p).pow(1.2f) * minOf(1f, p * 8f) * (0.34f + 0.3f * oscuro) * fuerza
        mancha(Offset(x, y), r * (1f + 0.5f * kotlin.math.abs(viento) * p), r, color, a)
    }
}

/** Una antorcha: el palo clavado en [pie], con su llama y su resplandor. */
fun DrawScope.antorcha(l: Luces, pie: Offset, alto: Float, reloj: Float, lejos: Float = 0f, semilla: Int = 0) {
    drawLine(l.tono(Color(0xFF4A3524), lejos), pie, pie + Offset(0f, -alto), maxOf(0.6f, alto * 0.08f))
    val boca = pie + Offset(0f, -alto)
    drawRect(l.tono(Color(0xFF2E2420), lejos), Offset(boca.x - alto * 0.08f, boca.y - alto * 0.04f), Size(alto * 0.16f, alto * 0.14f))
    resplandor(boca + Offset(0f, -alto * 0.2f), alto * 1.6f, 0.35f + 0.65f * l.noche)
    llama(boca, alto * 0.5f, reloj, semilla)
}
