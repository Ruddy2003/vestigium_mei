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
 * Renacimiento: un pendón de taller, blanco de yeso con una orla de sanguina, que cuelga de una vara de madera y acaba
 * en pico. Lleva bordados un compás abierto y, debajo, un lirio, sin letras. Al mantener el dedo se llena de azul de
 * ultramar desde abajo. **[Diseño propio]**: no copia el estandarte de ningún gremio ni el lirio del escudo de
 * Florencia (este es de tres pétalos sueltos).
 */
internal object BanderitaRenacimiento : Banderita {
    private val YESO = Color(0xFFF1EADA)
    private val YESO_OSC = Color(0xFFD9CDB0)
    private val SANGUINA = Color(0xFFB0492E)
    private val SEPIA = Color(0xFF5E4630)
    private val ULTRAMAR = Color(0xFF2F4E9A)
    private val ULTRAMAR_LUZ = Color(0xFF5E7CC4)
    private val MADERA = Color(0xFF8E6A40)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 31f); lineTo(13f, 38f); lineTo(4f, 31f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(YESO_OSC, YESO, YESO, YESO_OSC), 4f, 22f))
            // Lo que se ha llenado: el azul, que sube desde el pico.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38f - 35f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ULTRAMAR_LUZ, ULTRAMAR), y, 38f), Offset(0f, y), Size(26f, 38f - y))
            }
            clipPath(cuerpo) {
                for (x in listOf(9f, 17.4f)) drawLine(Color.White.copy(alpha = 0.16f), Offset(x, 3f), Offset(x + 0.6f, 38f), 1.4f)
                // La orla de sanguina, a los dos lados y en el pico.
                drawPath(Path().apply { moveTo(5.4f, 3f); lineTo(5.4f, 30.4f); lineTo(13f, 36.2f); lineTo(20.6f, 30.4f); lineTo(20.6f, 3f) }, SANGUINA.copy(alpha = 0.9f), style = Stroke(0.9f))
            }
            val tinta = if (lleno > 0.6f) YESO else SEPIA
            // El compás: la cabeza, las dos patas abiertas y el arco que traza.
            drawCircle(tinta, 1.3f, Offset(13f, 9f))
            drawLine(tinta, Offset(13f, 9.6f), Offset(9f, 20.6f), 1f); drawLine(tinta, Offset(13f, 9.6f), Offset(17f, 20.6f), 1f)
            drawLine(tinta, Offset(10.6f, 16f), Offset(15.4f, 16f), 0.6f)
            drawArc(if (lleno > 0.6f) YESO else SANGUINA, 20f, 140f, false, Offset(8f, 17f), Size(10f, 6f), style = Stroke(0.6f))
            // El lirio: tres pétalos y su ligadura.
            val flor = if (lleno > 0.3f) YESO else SANGUINA
            drawPath(Path().apply { moveTo(13f, 24.6f); cubicTo(11.6f, 26.4f, 11.8f, 29f, 13f, 31f); cubicTo(14.2f, 29f, 14.4f, 26.4f, 13f, 24.6f); close() }, flor)
            drawPath(Path().apply { moveTo(12.2f, 30f); cubicTo(10f, 29.6f, 9f, 27.6f, 9.6f, 26f); cubicTo(10.6f, 27f, 11.8f, 28.4f, 12.2f, 30f); close() }, flor)
            drawPath(Path().apply { moveTo(13.8f, 30f); cubicTo(16f, 29.6f, 17f, 27.6f, 16.4f, 26f); cubicTo(15.4f, 27f, 14.2f, 28.4f, 13.8f, 30f); close() }, flor)
            drawLine(flor, Offset(10.8f, 30.6f), Offset(15.2f, 30.6f), 0.7f)
            drawPath(cuerpo, SEPIA.copy(alpha = 0.5f), style = Stroke(0.5f))
            // La vara de madera, con sus dos pomos.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFC49A62), MADERA, Color(0xFF4E3820)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) drawCircle(MADERA, 1.7f, Offset(x, 2.1f))
        }
    }
}
