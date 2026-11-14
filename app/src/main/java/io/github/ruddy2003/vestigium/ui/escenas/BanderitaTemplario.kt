package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Templario: un gonfalón de lino que cuelga de una vara de madera con remates de hierro, partido como el estandarte de
 * la orden (negro arriba y blanco abajo) y con la cruz roja patada en el campo blanco; acaba en dos colas. Sin letras.
 * Al mantener el dedo se llena de rojo desde abajo. **[Diseño propio]**.
 */
internal object BanderitaTemplario : Banderita {
    private val LINO = Color(0xFFF2ECDC)
    private val LINO_OSC = Color(0xFFD2C8B2)
    private val NEGRO = Color(0xFF2B2725)
    private val CRUZ = Color(0xFFB0281E)
    private val CRUZ_LUZ = Color(0xFFD8503E)
    private val MADERA = Color(0xFF8E6A44)
    private val HIERRO = Color(0xFF5A5C64)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 37.6f); lineTo(17.4f, 32f); lineTo(13f, 37.6f); lineTo(8.6f, 32f); lineTo(4f, 37.6f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(LINO_OSC, LINO, LINO, LINO_OSC), 4f, 22f))
            clipPath(cuerpo) {
                drawRect(NEGRO, Offset(0f, 0f), Size(26f, 12f))
                if (lleno > 0f) { val y = 37.6f - 34.6f * lleno.coerceIn(0f, 1f); drawRect(Brush.verticalGradient(listOf(CRUZ_LUZ, CRUZ), y, 37.6f), Offset(0f, y), Size(26f, 37.6f - y)) }
                for (x in listOf(9f, 17f)) drawLine(Color.White.copy(alpha = 0.12f), Offset(x, 3f), Offset(x + 0.4f, 37f), 1.2f)
            }
            // La cruz patada: los brazos se abren hacia la punta.
            val c = Offset(13f, 22f); val tinta = if (lleno > 0.6f) LINO else CRUZ
            fun brazo(dx: Float, dy: Float) { val n = Offset(-dy, dx); drawPath(Path().apply { moveTo(c.x + n.x * 0.7f, c.y + n.y * 0.7f); lineTo(c.x + dx * 5.4f + n.x * 2.6f, c.y + dy * 5.4f + n.y * 2.6f); lineTo(c.x + dx * 5.4f - n.x * 2.6f, c.y + dy * 5.4f - n.y * 2.6f); lineTo(c.x - n.x * 0.7f, c.y - n.y * 0.7f); close() }, tinta) }
            brazo(1f, 0f); brazo(-1f, 0f); brazo(0f, 1f); brazo(0f, -1f)
            drawPath(cuerpo, NEGRO.copy(alpha = 0.45f), style = Stroke(0.5f))
            // La vara de madera con sus remates de hierro.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFB08A5A), MADERA, Color(0xFF4C3828)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) drawCircle(HIERRO, 1.7f, Offset(x, 2.1f))
        }
    }
}
