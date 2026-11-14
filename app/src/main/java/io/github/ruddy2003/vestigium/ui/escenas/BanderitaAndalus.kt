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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Al-Ándalus: un estandarte de seda, como los que tejía el tiraz de Córdoba: un paño blanco (el color de los omeyas)
 * que cuelga de un travesaño dorado, con orla verde, el pie en arco de herradura y, bordada en oro, una estrella de
 * ocho puntas hecha con dos cuadrados cruzados. No lleva letras. Al mantener el dedo se va tiñendo de oro desde abajo.
 */
internal object BanderitaAndalus : Banderita {
    private val BLANCO = Color(0xFFF6F0E2)
    private val BLANCO_LUZ = Color(0xFFFFFBF2)
    private val BLANCO_OSC = Color(0xFFD6CCB6)
    private val VERDE = Color(0xFF2F7A5C)
    private val VERDE_OSC = Color(0xFF1C4E3C)
    private val CARMESI = Color(0xFFA9283A)
    private val ORO = Color(0xFFD9B455)
    private val ORO_LUZ = Color(0xFFF6E3A0)
    private val ORO_OSC = Color(0xFF8A662A)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño: recto por los lados y, abajo, dos puntas con un arco de herradura entre ellas.
            val cuerpo = Path().apply {
                moveTo(3f, 2.4f); lineTo(23f, 2.4f); lineTo(23f, 37.4f); lineTo(19.2f, 33f)
                cubicTo(20.8f, 27.4f, 5.2f, 27.4f, 6.8f, 33f); lineTo(3f, 37.4f); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.8f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(BLANCO_OSC, BLANCO, BLANCO_LUZ, BLANCO), 3f, 23f))
            // Lo que se ha llenado: oro que sube desde las puntas.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38f - 36f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 38f), Offset(0f, y), Size(26f, 38f - y))
            }
            val lleno2 = lleno > 0.55f
            val tinta = if (lleno2) VERDE_OSC else ORO
            // La orla verde, con su filete carmesí por dentro.
            clipPath(cuerpo) { drawPath(cuerpo, if (lleno2) ORO_OSC else VERDE, style = Stroke(2.8f)) }
            drawLine(if (lleno2) VERDE_OSC else CARMESI, Offset(5.2f, 5.8f), Offset(20.8f, 5.8f), 0.6f)
            drawLine(if (lleno2) VERDE_OSC else CARMESI, Offset(5.2f, 25.2f), Offset(20.8f, 25.2f), 0.6f)
            // La estrella de ocho puntas: dos cuadrados cruzados, un aro y el botón.
            val c = Offset(13f, 15.4f)
            for (giro in listOf(0f, (PI / 4).toFloat())) {
                val cuadro = Path().apply { for (i in 0 until 4) { val a = giro + (PI / 4 + i * PI / 2).toFloat(); val q = Offset(c.x + 7.6f * cos(a), c.y + 7.6f * sin(a)); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }
                drawPath(cuadro, if (lleno2) VERDE_OSC else VERDE, style = Stroke(1.5f)); drawPath(cuadro, tinta, style = Stroke(0.7f))
            }
            drawCircle(tinta, 3f, c, style = Stroke(0.7f))
            drawCircle(if (lleno2) VERDE_OSC else CARMESI, 1.5f, c); drawCircle(if (lleno2) ORO_LUZ else ORO_LUZ, 0.55f, c + Offset(-0.4f, -0.4f))
            // Las dos borlas de las puntas.
            for (px in listOf(3.6f, 22.4f)) { drawCircle(if (lleno > 0.05f) ORO_LUZ else ORO, 1.1f, Offset(px, 38.2f)); drawLine(ORO_OSC, Offset(px, 36.6f), Offset(px, 37.4f), 0.5f) }
            // El travesaño dorado, con sus dos pomos y los cordones.
            drawLine(ORO_OSC, Offset(5f, 0f), Offset(3.4f, 2.4f), 0.5f); drawLine(ORO_OSC, Offset(21f, 0f), Offset(22.6f, 2.4f), 0.5f)
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO, ORO_OSC), 0.6f, 3.6f), Offset(1.4f, 0.6f), Size(23.2f, 3f), CornerRadius(1.5f))
            drawCircle(ORO, 1.9f, Offset(1.9f, 2.1f)); drawCircle(ORO, 1.9f, Offset(24.1f, 2.1f))
            drawCircle(ORO_LUZ, 0.7f, Offset(1.5f, 1.6f)); drawCircle(ORO_LUZ, 0.7f, Offset(23.7f, 1.6f))
        }
    }
}
