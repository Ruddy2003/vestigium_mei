package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Espacio: el banderín de la misión, colgado de su varilla de aluminio. Es azul noche, de punta redonda, con la ruta
 * del viaje bordada: el Sol abajo, la estrella de delante arriba y, entre los dos, la órbita ladeada y la nave. No
 * lleva ninguna letra. Al mantener el dedo se va llenando de ámbar desde abajo, como si amaneciera a bordo.
 */
internal object BanderitaEspacio : Banderita {
    private val NOCHE = Color(0xFF18244A)
    private val NOCHE_LUZ = Color(0xFF2E4078)
    private val AMBAR = Color(0xFFE8A04A)
    private val AMBAR_LUZ = Color(0xFFFFD08A)
    private val HILO = Color(0xFFEFF2FA)
    private val NARANJA = Color(0xFFE8843C)
    private val ALUMINIO = Color(0xFFB4BCCC)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño: recto arriba y redondo abajo, con su ribete naranja.
            val cuerpo = Path().apply { moveTo(3.6f, 3f); lineTo(22.4f, 3f); lineTo(22.4f, 28f); cubicTo(22.4f, 35f, 17f, 38.4f, 13f, 38.4f); cubicTo(9f, 38.4f, 3.6f, 35f, 3.6f, 28f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(NOCHE, NOCHE_LUZ, NOCHE, NOCHE), 3.6f, 22.4f))
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38.4f - 35.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(AMBAR_LUZ, AMBAR), y, 38.4f), Offset(0f, y), Size(26f, 38.4f - y))
            }
            clipPath(cuerpo) {
                for (x in listOf(8.6f, 17.6f)) drawLine(Color.White.copy(alpha = 0.07f), Offset(x, 3f), Offset(x + 0.8f, 38f), 1.4f)
                // Unas estrellas de fondo.
                for ((x, y) in listOf(6.6f to 8f, 19.6f to 12.4f, 7.4f to 19f, 18.6f to 31f, 9.6f to 33f, 15.4f to 7.4f)) drawCircle(HILO.copy(alpha = 0.7f), 0.45f, Offset(x, y))
            }
            drawPath(cuerpo, NARANJA, style = Stroke(0.9f))
            val tinta = if (lleno > 0.6f) NOCHE else HILO
            // La órbita ladeada y la ruta: del Sol, abajo, a la estrella de delante, arriba.
            val c = Offset(13f, 21f)
            rotate(-24f, c) { drawOval(tinta.copy(alpha = 0.8f), Offset(c.x - 7.6f, c.y - 3f), Size(15.2f, 6f), style = Stroke(0.6f)) }
            val sol = Offset(8.4f, 30.4f); val estrella = Offset(17.4f, 10.4f)
            for (i in 1..6) { val t = i / 7f; drawCircle(tinta.copy(alpha = 0.75f), 0.4f, Offset(sol.x + (estrella.x - sol.x) * t, sol.y + (estrella.y - sol.y) * t)) }
            drawCircle(if (lleno > 0.6f) NOCHE else AMBAR_LUZ, 1.9f, sol)
            // La estrella de delante, de cuatro puntas.
            drawPath(Path().apply { moveTo(estrella.x, estrella.y - 4f); lineTo(estrella.x + 0.9f, estrella.y - 0.9f); lineTo(estrella.x + 4f, estrella.y); lineTo(estrella.x + 0.9f, estrella.y + 0.9f); lineTo(estrella.x, estrella.y + 4f); lineTo(estrella.x - 0.9f, estrella.y + 0.9f); lineTo(estrella.x - 4f, estrella.y); lineTo(estrella.x - 0.9f, estrella.y - 0.9f); close() }, tinta)
            // La nave, a medio camino: una punta de flecha.
            val nave = Offset(13.4f, 19.4f)
            rotate(-66f, nave) { drawPath(Path().apply { moveTo(nave.x + 3f, nave.y); lineTo(nave.x - 2f, nave.y - 1.9f); lineTo(nave.x - 0.9f, nave.y); lineTo(nave.x - 2f, nave.y + 1.9f); close() }, NARANJA) }
            // La varilla de la que cuelga, con sus dos presillas.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFE8ECF4), ALUMINIO, Color(0xFF6A7488)), 0.6f, 3.6f), Offset(1.4f, 0.6f), Size(23.2f, 3f), CornerRadius(1.5f))
            for (x in listOf(5f, 21f)) drawLine(NARANJA, Offset(x, 0.4f), Offset(x, 4f), 1f, StrokeCap.Round)
        }
    }
}
