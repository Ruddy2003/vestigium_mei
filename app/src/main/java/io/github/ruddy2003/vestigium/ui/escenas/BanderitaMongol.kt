package io.github.ruddy2003.vestigium.ui.escenas

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

/**
 * Mongol: una cinta de seda azul, como las que se atan a los palos del ovoo, anudada a una vara de sauce y con los
 * hilos sueltos abajo. Lleva tejida una tienda de fieltro blanco, de puerta roja y aro de oro, sobre una franja de
 * estepa, y encima el sol y la luna; arriba y abajo, una greca de llaves en oro. Sin letras. Al mantener el dedo se
 * llena de oro desde abajo. **[Diseño propio]**: el tema de pixel no tiene banderita; los colores son los del tema (el
 * azul del cielo, el rojo, el oro y el fieltro) y el motivo es el campamento de Bat.
 */
internal object BanderitaMongol : Banderita {
    private val SEDA = Color(0xFF2F64A6)
    private val SEDA_LUZ = Color(0xFF5888C4)
    private val SEDA_OSC = Color(0xFF1C3F72)
    private val FIELTRO = Color(0xFFF4EEDF)
    private val FIELTRO_OSC = Color(0xFFC9BCA2)
    private val ROJO = Color(0xFFC2412D)
    private val ORO = Color(0xFFC8963A)
    private val ORO_LUZ = Color(0xFFF0CE78)
    private val ESTEPA = Color(0xFF8E9A5A)
    private val SAUCE = Color(0xFF9C7A52)
    private val SAUCE_OSC = Color(0xFF5E4630)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // La cinta: cae del nudo, un poco más ancha abajo, con los lados que ondulan.
            val cuerpo = Path().apply {
                moveTo(5f, 4.4f); lineTo(21f, 4.4f)
                cubicTo(21.6f, 13f, 20.6f, 22f, 21.8f, 35.4f); lineTo(4.2f, 35.4f)
                cubicTo(5.2f, 24f, 4.4f, 13f, 5f, 4.4f); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(SEDA_OSC, SEDA, SEDA_LUZ, SEDA), 4f, 22f))
            // Lo que se ha llenado: el oro, que sube desde los hilos.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 35.4f - 31f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 35.4f), Offset(0f, y), Size(26f, 35.4f - y))
            }
            /** Si el oro ya llegó a la altura [y]: lo que queda debajo cambia a azul para que se siga viendo. */
            fun cubre(y: Float) = lleno > 0f && y > 35.4f - 31f * lleno.coerceIn(0f, 1f)
            clipPath(cuerpo) {
                // El brillo de la seda, que corre a lo largo, y las grecas de llaves arriba y abajo.
                for (x in listOf(8.4f, 16.6f)) drawLine(Color.White.copy(alpha = 0.13f), Offset(x, 4f), Offset(x + 0.5f, 35f), 1.3f)
                for (y0 in listOf(7f, 31.4f)) {
                    val greca = if (cubre(y0)) SEDA_OSC else ORO
                    drawLine(greca, Offset(4f, y0 - 1.6f), Offset(22f, y0 - 1.6f), 0.45f); drawLine(greca, Offset(4f, y0 + 1.6f), Offset(22f, y0 + 1.6f), 0.45f)
                    var x = 5.2f
                    while (x < 21f) {
                        drawPath(Path().apply { moveTo(x, y0 + 1f); lineTo(x, y0 - 0.9f); lineTo(x + 2f, y0 - 0.9f); lineTo(x + 2f, y0 + 0.2f); lineTo(x + 1f, y0 + 0.2f) }, greca, style = Stroke(0.42f, cap = StrokeCap.Square))
                        x += 3f
                    }
                }
            }
            // El sol y la luna, sobre la tienda.
            val astro = if (cubre(13f)) SEDA_OSC else ORO_LUZ
            drawCircle(astro, 1.5f, Offset(13f, 11.6f))
            drawPath(Path().apply { moveTo(10.6f, 13.8f); cubicTo(11.4f, 15.4f, 14.6f, 15.4f, 15.4f, 13.8f); cubicTo(14.4f, 14.6f, 11.6f, 14.6f, 10.6f, 13.8f); close() }, astro)
            // La franja de estepa y la tienda: la pared, el techo, la puerta roja y el aro de oro.
            drawPath(Path().apply { moveTo(5f, 27.2f); cubicTo(9f, 25.8f, 17f, 25.8f, 21.2f, 27.2f); lineTo(21.4f, 28.4f); lineTo(4.8f, 28.4f); close() }, if (cubre(27f)) SEDA_OSC else ESTEPA)
            val pared = Path().apply { moveTo(7.4f, 22.4f); lineTo(18.6f, 22.4f); lineTo(18.8f, 27f); cubicTo(15f, 27.6f, 11f, 27.6f, 7.2f, 27f); close() }
            drawPath(pared, Brush.horizontalGradient(listOf(FIELTRO, FIELTRO, FIELTRO_OSC), 7f, 19f))
            val techo = Path().apply { moveTo(6.6f, 22.6f); cubicTo(8.6f, 20.4f, 11f, 18.4f, 12f, 18f); lineTo(14f, 18f); cubicTo(15f, 18.4f, 17.4f, 20.4f, 19.4f, 22.6f); cubicTo(15f, 23.2f, 11f, 23.2f, 6.6f, 22.6f); close() }
            drawPath(techo, Brush.horizontalGradient(listOf(FIELTRO, FIELTRO_OSC), 6.6f, 19.4f))
            drawLine(ORO, Offset(11.6f, 17.9f), Offset(14.4f, 17.9f), 0.9f, StrokeCap.Round)
            for (y in listOf(23.6f, 25.4f)) drawLine(SAUCE_OSC.copy(alpha = 0.6f), Offset(7.5f, y), Offset(18.6f, y), 0.3f)
            drawPath(Path().apply { moveTo(11.8f, 27.4f); lineTo(11.8f, 23.4f); lineTo(14.2f, 23.4f); lineTo(14.2f, 27.4f); close() }, ROJO)
            drawLine(ORO, Offset(11.8f, 23.4f), Offset(14.2f, 23.4f), 0.4f)
            drawPath(cuerpo, SEDA_OSC.copy(alpha = 0.5f), style = Stroke(0.5f))
            // Los hilos sueltos del borde de abajo.
            for (j in 0..12) { val x = 4.6f + j * 1.37f; drawLine(if (j % 2 == 0) SEDA else SEDA_OSC, Offset(x, 35.2f), Offset(x + (j % 3 - 1) * 0.3f, 38.4f + (j % 2) * 0.9f), 0.55f, StrokeCap.Round) }
            // La vara de sauce, con sus nudos, y el nudo de la cinta, que le da la vuelta.
            drawLine(SAUCE_OSC, Offset(1.2f, 3.1f), Offset(24.8f, 2.5f), 2.4f, StrokeCap.Round); drawLine(SAUCE, Offset(1.2f, 2.6f), Offset(24.8f, 2f), 1.3f, StrokeCap.Round)
            for (x in listOf(3.4f, 22.2f)) drawCircle(SAUCE_OSC, 0.7f, Offset(x, 2.6f))
            drawLine(SAUCE, Offset(24f, 2.2f), Offset(25.4f, 0.6f), 0.8f, StrokeCap.Round)
            drawPath(Path().apply { moveTo(9.6f, 1.2f); cubicTo(11.6f, 0.2f, 14.4f, 0.2f, 16.4f, 1.2f); lineTo(16.8f, 5.4f); cubicTo(14.6f, 4.4f, 11.4f, 4.4f, 9.2f, 5.4f); close() }, SEDA_OSC)
            drawPath(Path().apply { moveTo(10.4f, 1.6f); cubicTo(12f, 1f, 14f, 1f, 15.6f, 1.6f) }, SEDA_LUZ, style = Stroke(0.5f, cap = StrokeCap.Round))
        }
    }
}
