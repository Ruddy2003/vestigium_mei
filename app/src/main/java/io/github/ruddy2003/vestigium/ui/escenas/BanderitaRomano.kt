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
 * Romano: un vexilo, el estandarte de paño de las legiones: un cuadro de lana del color del pórfido que cuelga de un
 * travesaño dorado, con orla y una corona de laurel bordadas en oro, fleco abajo y, a los lados, las dos correas con
 * su hoja de hiedra de bronce. Al mantener el dedo se va tiñendo de oro desde abajo.
 */
internal object BanderitaRomano : Banderita {
    private val PORFIDO = Color(0xFF7A2438)
    private val PORFIDO_LUZ = Color(0xFFA2405A)
    private val PORFIDO_OSC = Color(0xFF4C1424)
    private val ORO = Color(0xFFD9B455)
    private val ORO_LUZ = Color(0xFFF6E3A0)
    private val ORO_OSC = Color(0xFF8A662A)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // Las dos correas que cuelgan de las puntas del travesaño, con su hoja de hiedra.
            for (x in listOf(2.6f, 23.4f)) {
                drawLine(PORFIDO_OSC, Offset(x, 3f), Offset(x, 33f), 1.5f)
                drawPath(Path().apply { moveTo(x, 32.4f); cubicTo(x - 2.4f, 33.4f, x - 2f, 37f, x, 38.6f); cubicTo(x + 2f, 37f, x + 2.4f, 33.4f, x, 32.4f); close() }, if (lleno > 0.05f) ORO_LUZ else ORO)
            }
            // El paño.
            val cuerpo = Path().apply { moveTo(5f, 2.6f); lineTo(21f, 2.6f); lineTo(21f, 30f); lineTo(5f, 30f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.8f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(PORFIDO_OSC, PORFIDO, PORFIDO_LUZ, PORFIDO), 5f, 21f))
            // Lo que se ha llenado: oro que sube desde el fleco.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 30f - 27.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 30f), Offset(0f, y), Size(26f, 30f - y))
            }
            val tinta = if (lleno > 0.55f) PORFIDO_OSC else ORO
            // La orla.
            drawRect(tinta, Offset(6.4f, 4.6f), Size(13.2f, 24f), style = Stroke(0.7f))
            // La corona de laurel: dos ramas de hojas que se abren arriba, atadas abajo.
            val c = Offset(13f, 16.4f)
            for (lado in listOf(-1f, 1f)) for (i in 0..5) {
                val a = (PI / 2 + lado * (0.35 + i * 0.44)).toFloat(); val p = c + Offset(cos(a) * 5.2f, sin(a) * 5.2f)
                val t = a - lado * 1.25f
                drawLine(tinta, p, p + Offset(cos(t) * 2.6f, sin(t) * 2.6f), 1.1f, StrokeCap.Round)
            }
            drawLine(tinta, c + Offset(-1.6f, 6.4f), c + Offset(1.6f, 6.4f), 0.8f, StrokeCap.Round)
            drawCircle(tinta, 1.3f, c)
            // El fleco.
            for (i in 0..6) { val x = 6f + i * 2.33f; drawLine(if (lleno > 0.05f) ORO_LUZ else ORO, Offset(x, 30.2f), Offset(x, 34.2f), 0.8f, StrokeCap.Round) }
            // El travesaño dorado, con sus dos pomos.
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO, ORO_OSC), 0.4f, 3.6f), Offset(1.4f, 0.4f), Size(23.2f, 3.2f), CornerRadius(1.6f))
            drawCircle(ORO, 1.9f, Offset(1.9f, 2f)); drawCircle(ORO, 1.9f, Offset(24.1f, 2f))
            drawCircle(ORO_LUZ, 0.7f, Offset(1.5f, 1.5f)); drawCircle(ORO_LUZ, 0.7f, Offset(23.7f, 1.5f))
        }
    }
}
