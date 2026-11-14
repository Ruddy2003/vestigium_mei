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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ruta de la Seda: un paño de seda carmesí que cuelga de una vara de bronce y acaba en pico, con su orla añil. Lleva
 * tejido un medallón de perlas (un aro de puntos, como en las sedas que llevaban las caravanas) y, dentro, un camello
 * de dos jorobas; sin letras. Al mantener el dedo se llena de oro desde abajo. **[Diseño propio]**: no copia ninguna
 * tela conservada.
 */
internal object BanderitaSeda : Banderita {
    private val SEDA = Color(0xFF9A3430)
    private val SEDA_LUZ = Color(0xFFBE5648)
    private val SEDA_OSC = Color(0xFF6C2226)
    private val ANIL = Color(0xFF3E6682)
    private val ORO = Color(0xFFD2A244)
    private val ORO_LUZ = Color(0xFFF0D080)
    private val PERLA = Color(0xFFF3ECD8)
    private val BRONCE = Color(0xFF8E6E32)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 31f); lineTo(13f, 37.6f); lineTo(4f, 31f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(SEDA_OSC, SEDA, SEDA_LUZ, SEDA), 4f, 22f))
            // Lo que se ha llenado: el oro, que sube desde el pico.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 37.6f - 34.6f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 37.6f), Offset(0f, y), Size(26f, 37.6f - y))
            }
            clipPath(cuerpo) {
                // El brillo de la seda y la orla añil, a los dos lados.
                for (x in listOf(9f, 17.4f)) drawLine(Color.White.copy(alpha = 0.12f), Offset(x, 3f), Offset(x + 0.6f, 37f), 1.4f)
                for (x in listOf(5.1f, 20.9f)) drawLine(ANIL, Offset(x, 3f), Offset(x, 37f), 1.5f)
                drawLine(ORO.copy(alpha = 0.9f), Offset(4f, 6.6f), Offset(22f, 6.6f), 0.6f)
            }
            // El medallón de perlas y, dentro, el camello.
            val tinta = if (lleno > 0.55f) SEDA_OSC else ORO_LUZ; val centro = Offset(13f, 18.6f)
            drawCircle(tinta, 6.4f, centro, style = Stroke(0.6f))
            for (j in 0 until 14) { val a = j * 0.4488f; drawCircle(if (lleno > 0.55f) SEDA_OSC else PERLA, 0.62f, centro + Offset(cos(a) * 6.4f, sin(a) * 6.4f)) }
            drawPath(Path().apply {
                moveTo(9.4f, 22.6f); lineTo(9.6f, 18.8f); cubicTo(10f, 15.6f, 11.6f, 15.6f, 12.2f, 18f); cubicTo(12.8f, 15.4f, 14.6f, 15.4f, 15.2f, 18.2f)
                lineTo(16.2f, 15.6f); lineTo(17.8f, 15f); lineTo(17.4f, 16.6f); lineTo(16.4f, 17.4f); lineTo(16.2f, 22.6f); lineTo(15.2f, 22.6f); lineTo(15f, 20.4f); lineTo(10.8f, 20.4f); lineTo(10.6f, 22.6f); close()
            }, tinta)
            drawPath(cuerpo, SEDA_OSC.copy(alpha = 0.5f), style = Stroke(0.5f))
            // La borla del pico y la vara de bronce, con sus dos pomos.
            drawLine(ORO, Offset(13f, 37.4f), Offset(13f, 39.6f), 0.9f)
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, BRONCE, Color(0xFF5A4520)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) drawCircle(BRONCE, 1.7f, Offset(x, 2.1f))
        }
    }
}
