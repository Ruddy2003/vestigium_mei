package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.cos
import kotlin.math.sin

/**
 * Japón Edo: una cortina de puerta (noren) de índigo, partida en dos paños, colgada de su vara de bambú, con una flor de
 * ciruelo blanca en medio y el borde de abajo deshilachado. Sin letras. Al mantener el dedo se llena de bermellón
 * desde abajo. **[Diseño propio]**.
 */
internal object BanderitaEdo : Banderita {
    private val INDIGO = Color(0xFF2E4A6E)
    private val INDIGO_LUZ = Color(0xFF41608A)
    private val INDIGO_OSC = Color(0xFF1C2E48)
    private val WASHI = Color(0xFFF2ECDC)
    private val BERMELLON = Color(0xFFC4452E)
    private val BAMBU = Color(0xFFB8A868)
    private val BAMBU_OSC = Color(0xFF7A6E3A)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val izq = Path().apply { moveTo(3.6f, 3f); lineTo(12.6f, 3f); lineTo(12.6f, 37f); lineTo(3.6f, 37.6f); close() }
            val der = Path().apply { moveTo(13.4f, 3f); lineTo(22.4f, 3f); lineTo(22.4f, 37.6f); lineTo(13.4f, 37f); close() }
            for (paño in listOf(izq, der)) {
                drawPath(Path().apply { addPath(paño, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.26f))
                drawPath(paño, Brush.horizontalGradient(listOf(INDIGO_OSC, INDIGO, INDIGO_LUZ, INDIGO), 3.6f, 22.4f))
                if (lleno > 0f) clipPath(paño) { val y = 37.6f - 34.6f * lleno.coerceIn(0f, 1f); drawRect(BERMELLON, Offset(0f, y), Size(26f, 37.6f - y)) }
                clipPath(paño) { for (x in listOf(6f, 10f, 16f, 20f)) drawLine(Color.White.copy(alpha = 0.08f), Offset(x, 3f), Offset(x, 38f), 1f) }
            }
            // La flor de ciruelo, partida entre los dos paños.
            val c = Offset(13f, 18f); val tinta = if (lleno > 0.55f) INDIGO_OSC else WASHI
            for (i in 0 until 5) { val a = -1.5708f + i * 1.2566f; drawCircle(tinta, 2.2f, c + Offset(cos(a), sin(a)) * 3f) }
            drawCircle(if (lleno > 0.55f) WASHI else BERMELLON, 1.2f, c)
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(13f, 3f), Offset(13f, 37.6f), 0.8f)
            // El borde de abajo, deshilachado, y la vara de bambú con sus nudos.
            for (x in listOf(4.4f, 6.6f, 8.8f, 11f, 14.2f, 16.4f, 18.6f, 20.8f)) drawLine(INDIGO_OSC, Offset(x, 37f), Offset(x + 0.3f, 38.6f), 0.5f)
            drawRect(Brush.verticalGradient(listOf(Color(0xFFD8CC8C), BAMBU, BAMBU_OSC), 0.8f, 3.6f), Offset(1f, 0.8f), Size(24f, 2.8f))
            for (x in listOf(7f, 19f)) drawLine(BAMBU_OSC, Offset(x, 0.8f), Offset(x, 3.6f), 0.6f)
            drawPath(izq, INDIGO_OSC.copy(alpha = 0.5f), style = Stroke(0.4f)); drawPath(der, INDIGO_OSC.copy(alpha = 0.5f), style = Stroke(0.4f))
        }
    }
}
