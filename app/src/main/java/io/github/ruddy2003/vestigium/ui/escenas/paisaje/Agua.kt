package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.sin

/** El color propio del mar en calma, de día. */
val MAR = Color(0xFF2F6F9E)

/**
 * Agua quieta dentro de [zona] (el mar, un río, un estanque), recortada a [contorno] si se da: refleja el cielo en el
 * horizonte (el borde de arriba de [zona]) y se hace más honda hacia quien mira, con rizos más largos cuanto más
 * cerca. [viento], de 0 a 1: con viento, más rizos y crestas blancas.
 * Debajo del sol bajo, o de la luna, lleva su estela de luz.
 */
fun DrawScope.agua(l: Luces, zona: Rect, azar: Azar, propio: Color = MAR, viento: Float = 0f, contorno: Path? = null) {
    val lejos = lerp(l.tono(propio, 0.2f), l.bajo, 0.55f)
    val medio = lerp(l.tono(propio), l.medio, 0.25f)
    val cerca = lerp(l.tono(lerp(propio, Color(0xFF123A5C), 0.45f)), l.alto, 0.18f)
    val dibujo: DrawScope.() -> Unit = {
        drawRect(Brush.verticalGradient(0f to lejos, 0.35f to medio, 1f to cerca, startY = zona.top, endY = zona.bottom), zona.topLeft, zona.size)
        // Rizos: trazos claros (el cielo) y oscuros (el seno de la ola).
        val claro = lerp(l.bajo, Color.White, 0.25f); val oscuro = lerp(cerca, Color(0xFF0A1A2C), 0.35f)
        val cuantos = ((zona.width * zona.height) / 190f * (1f + viento)).toInt().coerceAtMost(520)
        repeat(cuantos) {
            val k = azar.real(); val prof = k * k                              // más rizos lejos (arriba)
            val y = zona.top + 1.5f + (zona.height - 2f) * prof
            val largo = (2.5f + 16f * prof) * azar.entre(0.5f, 1.3f)
            val x = azar.entre(zona.left - 6f, zona.right)
            val a = (0.10f + 0.16f * azar.real()) * (1f + 0.5f * viento)
            drawLine((if (azar.real() > 0.45f) claro else oscuro).copy(alpha = a.coerceAtMost(0.5f)), Offset(x, y), Offset(x + largo, y), 0.5f + 0.7f * prof, StrokeCap.Round)
        }
        if (viento > 0.4f) repeat((zona.width / 16f * viento).toInt()) {
            val prof = azar.real(); val y = zona.top + 3f + (zona.height - 5f) * prof; val x = azar.entre(zona.left, zona.right)
            val w = 2f + 4.5f * prof
            drawPath(Path().apply { moveTo(x, y); cubicTo(x + w * 0.3f, y - 1.6f, x + w * 0.7f, y - 1.6f, x + w, y) }, l.tono(Color.White).copy(alpha = 0.6f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(0.7f, cap = StrokeCap.Round))
        }
        // La estela del sol bajo o de la luna.
        val conSol = l.solAltura > -0.06f && l.solAltura < 0.6f && l.solFuerza > 0.3f
        val conLuna = !conSol && l.estrellas > 0.6f
        if (conSol || conLuna) {
            val x0 = (if (conSol) l.solX else l.lunaX) * ANCHO
            val color = if (conSol) lerp(l.solColor, Color.White, 0.35f) else Color(0xFFDDE6FF)
            val fuerza = if (conSol) (1f - l.solAltura / 0.6f) * l.solFuerza * (1f - l.eclipse) else 0.45f * l.estrellas
            repeat(70) {
                val prof = azar.real(); val y = zona.top + 1f + (zona.height - 2f) * prof
                val ancho = 3f + 26f * prof
                val x = x0 + (azar.real() - 0.5f) * ancho * 1.6f
                val largo = (2f + 9f * prof) * azar.entre(0.5f, 1.2f)
                drawLine(color.copy(alpha = (0.55f * fuerza * azar.entre(0.4f, 1f)).coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.6f + 0.8f * prof, StrokeCap.Round, blendMode = BlendMode.Screen)
            }
        }
    }
    if (contorno != null) clipPath(contorno) { dibujo() } else dibujo()
}

/**
 * Los destellos que se mueven sobre el agua: unos pocos rizos que se encienden y se apagan con el [reloj]. Es lo único
 * del agua que se traza en cada cuadro.
 */
fun DrawScope.destellos(l: Luces, zona: Rect, reloj: Float, semilla: Long, cuantos: Int = 26) {
    val azar = Azar(semilla)
    val claro = lerp(l.bajo, Color.White, 0.5f)
    val x0 = (if (l.estrellas > 0.6f) l.lunaX else l.solX) * ANCHO
    repeat(cuantos) { i ->
        val prof = azar.real(); val y = zona.top + 2f + (zona.height - 3f) * prof
        // La mitad, repartidos; la otra mitad, en la estela.
        val x = if (i % 2 == 0) azar.entre(zona.left, zona.right) else x0 + (azar.real() - 0.5f) * (10f + 40f * prof)
        val largo = 2f + 8f * prof
        val fase = azar.entre(0f, 6.28f); val ritmo = azar.entre(0.9f, 2.2f)
        val a = (0.5f + 0.5f * sin(reloj * ritmo + fase)).let { it * it } * (0.35f - 0.2f * l.nublado)
        if (x > zona.left && x < zona.right) drawLine(claro.copy(alpha = a.coerceIn(0f, 1f)), Offset(x - largo / 2f, y), Offset(x + largo / 2f, y), 0.6f + 0.6f * prof, StrokeCap.Round)
    }
}

/** El reflejo de una luz (una antorcha, una ventana) en el agua: una columna de trazos que tiembla con el [reloj]. */
fun DrawScope.reflejo(pie: Offset, alto: Float, color: Color, fuerza: Float, reloj: Float, semilla: Int = 0) {
    for (i in 0 until 6) {
        val k = i / 6f
        val y = pie.y + 1f + alto * k
        val w = 1.5f + 2.5f * k + 0.8f * sin(reloj * 3.1f + i * 1.7f + semilla)
        drawLine(color.copy(alpha = (fuerza * (0.55f - 0.45f * k)).coerceIn(0f, 1f)), Offset(pie.x - w, y), Offset(pie.x + w, y), 0.8f, StrokeCap.Round, blendMode = BlendMode.Screen)
    }
}
