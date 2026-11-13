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
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Gótico: un gonfalón de cofradía, como los que salían en las procesiones: un paño azul de waide que cuelga de un
 * travesaño dorado, con orla roja, tres farpas abajo y, bordado en oro, un rosetón de seis pétalos. Al mantener el
 * dedo se va tiñendo de oro desde abajo.
 */
internal object BanderitaGotico : Banderita {
    private val AZUL = Color(0xFF2F4F86)
    private val AZUL_LUZ = Color(0xFF4A6FAE)
    private val AZUL_OSC = Color(0xFF1C3058)
    private val ROJO = Color(0xFF9E3528)
    private val ORO = Color(0xFFD9B455)
    private val ORO_LUZ = Color(0xFFF6E3A0)
    private val ORO_OSC = Color(0xFF8A662A)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con sus tres farpas.
            val cuerpo = Path().apply {
                moveTo(3f, 2.4f); lineTo(23f, 2.4f); lineTo(23f, 29f); lineTo(20f, 37f); lineTo(16.6f, 30.6f)
                lineTo(13f, 38.6f); lineTo(9.4f, 30.6f); lineTo(6f, 37f); lineTo(3f, 29f); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.8f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(AZUL_OSC, AZUL, AZUL_LUZ, AZUL), 3f, 23f))
            // Lo que se ha llenado: oro que sube desde las farpas.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 39f - 37f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 39f), Offset(0f, y), Size(26f, 39f - y))
            }
            val tinta = if (lleno > 0.55f) AZUL_OSC else ORO
            // La orla roja, con su filete.
            clipPath(cuerpo) { drawPath(cuerpo, if (lleno > 0.55f) ORO_OSC else ROJO, style = Stroke(2.6f)) }
            drawLine(tinta, Offset(5.2f, 5.6f), Offset(20.8f, 5.6f), 0.5f); drawLine(tinta, Offset(5.2f, 26.6f), Offset(20.8f, 26.6f), 0.5f)
            // El rosetón: el aro, seis pétalos y el botón.
            val c = Offset(13f, 16f)
            drawCircle(tinta, 7.4f, c, style = Stroke(0.9f)); drawCircle(tinta, 5.9f, c, style = Stroke(0.4f))
            for (i in 0 until 6) {
                val a = (2 * PI * i / 6 - PI / 2).toFloat(); val d = Offset(cos(a), sin(a))
                drawCircle(tinta, 1.75f, c + d * 3.7f, style = Stroke(0.7f))
                drawLine(tinta, c + d * 1.4f, c + d * 2f, 0.6f, StrokeCap.Round)
            }
            drawCircle(tinta, 1.3f, c)
            // Las tres borlas de las farpas.
            for (px in listOf(6f, 13f, 20f)) drawCircle(if (lleno > 0.05f) ORO_LUZ else ORO, 0.9f, Offset(px, if (px == 13f) 39.2f else 37.6f))
            // El travesaño dorado, con sus dos pomos y los cordones.
            drawLine(ORO_OSC, Offset(5f, 0f), Offset(3.4f, 2.4f), 0.5f); drawLine(ORO_OSC, Offset(21f, 0f), Offset(22.6f, 2.4f), 0.5f)
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO, ORO_OSC), 0.6f, 3.6f), Offset(1.4f, 0.6f), Size(23.2f, 3f), CornerRadius(1.5f))
            drawCircle(ORO, 1.9f, Offset(1.9f, 2.1f)); drawCircle(ORO, 1.9f, Offset(24.1f, 2.1f))
            drawCircle(ORO_LUZ, 0.7f, Offset(1.5f, 1.6f)); drawCircle(ORO_LUZ, 0.7f, Offset(23.7f, 1.6f))
        }
    }
}
