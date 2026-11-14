package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Revolución francesa: un banderín de lienzo crudo, como una sábana recién lavada, colgado de su palo con dos pinzas;
 * lleva una escarapela de tres colores y, debajo, un ramo de espigas (el pan), sin ninguna letra. Al mantener el dedo
 * se va tiñendo desde abajo con las tres franjas de la bandera.
 */
internal object BanderitaRevolucion : Banderita {
    private val LIENZO = Color(0xFFF2ECDD)
    private val LIENZO_OSC = Color(0xFFD6CCB6)
    private val ANIL = Color(0xFF41608E)
    private val GRANZA = Color(0xFFB4463C)
    private val ESPIGA = Color(0xFFC79A3E)
    private val MADERA = Color(0xFFB8905A)
    private val MADERA_OSC = Color(0xFF6A4E30)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con la punta en pico.
            val cuerpo = Path().apply { moveTo(3.6f, 3f); lineTo(22.4f, 3f); lineTo(22.4f, 32f); lineTo(13f, 38f); lineTo(3.6f, 32f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(LIENZO_OSC, LIENZO, LIENZO, LIENZO_OSC), 3.6f, 22.4f))
            // Lo que se ha llenado: las tres franjas, que suben desde el pico.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38f - 35f * lleno.coerceIn(0f, 1f)
                drawRect(ANIL, Offset(3.6f, y), Size(6.3f, 38f - y)); drawRect(LIENZO, Offset(9.9f, y), Size(6.2f, 38f - y)); drawRect(GRANZA, Offset(16.1f, y), Size(6.3f, 38f - y))
            }
            clipPath(cuerpo) { for (x in listOf(8.6f, 17.6f)) drawLine(Color.Black.copy(alpha = 0.06f), Offset(x, 3f), Offset(x + 0.8f, 38f), 1.2f) }
            // La escarapela.
            val c = Offset(13f, 12.6f)
            drawCircle(Color.Black.copy(alpha = 0.18f), 5.6f, c + Offset(0.5f, 0.7f))
            drawCircle(ANIL, 5.4f, c); drawCircle(LIENZO, 3.7f, c); drawCircle(GRANZA, 1.9f, c)
            // Las espigas: tres tallos atados.
            for (dx in listOf(-3.6f, 0f, 3.6f)) {
                drawLine(ESPIGA, Offset(13f, 31f), Offset(13f + dx, 22.6f), 0.7f)
                for (i in 0..2) { drawOval(ESPIGA, Offset(13f + dx - 1.5f + dx * 0.04f * i, 20.4f + i * 1.7f - kotlin.math.abs(dx) * 0.1f), Size(1.4f, 2.2f)); drawOval(ESPIGA, Offset(13f + dx + 0.1f + dx * 0.04f * i, 20.4f + i * 1.7f - kotlin.math.abs(dx) * 0.1f), Size(1.4f, 2.2f)) }
            }
            drawRect(GRANZA, Offset(11.6f, 27.6f), Size(2.8f, 1.2f))
            // El palo del que cuelga, con sus dos pinzas.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFE2C088), MADERA, MADERA_OSC), 0.6f, 3.6f), Offset(1.4f, 0.6f), Size(23.2f, 3f), CornerRadius(1.5f))
            for (x in listOf(6f, 19f)) drawRect(MADERA_OSC, Offset(x, 0.2f), Size(1.4f, 5.2f))
        }
    }
}
