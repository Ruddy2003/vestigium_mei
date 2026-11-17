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

/**
 * Egipto: un paño de lino teñido de azul que cuelga de una vara dorada y acaba redondo, como medio collar ancho, con
 * sus bandas de turquesa, rojo y oro y una fila de cuentas en gota. En medio lleva una flor de loto abierta bajo el
 * disco del sol; sin signos ni letras. Al mantener el dedo se llena de oro desde abajo. **[Diseño propio]**: no copia
 * ninguna pieza conservada.
 */
internal object BanderitaEgipto : Banderita {
    private val AZUL = Color(0xFF28507E)
    private val AZUL_LUZ = Color(0xFF3E6E9E)
    private val AZUL_OSC = Color(0xFF1A3658)
    private val TURQUESA = Color(0xFF52B8AE)
    private val ROJO = Color(0xFFB4482E)
    private val ORO = Color(0xFFD6A63E)
    private val ORO_LUZ = Color(0xFFF2D684)
    private val LINO = Color(0xFFF4ECD8)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 27f); cubicTo(22f, 33.4f, 18f, 37.4f, 13f, 37.4f); cubicTo(8f, 37.4f, 4f, 33.4f, 4f, 27f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(AZUL_OSC, AZUL, AZUL_LUZ, AZUL), 4f, 22f))
            // Lo que se ha llenado: el oro, que sube desde abajo.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 37.4f - 34.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 37.4f), Offset(0f, y), Size(26f, 37.4f - y))
            }
            clipPath(cuerpo) {
                // Las bandas del collar, que siguen la curva de abajo: turquesa, rojo y oro.
                for ((j, c) in listOf(ORO, ROJO, TURQUESA).withIndex()) { val d = 1.2f + j * 1.7f
                    drawPath(Path().apply { moveTo(4f + d, 3f); lineTo(4f + d, 27f); cubicTo(4f + d, 32.4f - d * 0.5f, 8.6f + d * 0.4f, 37.4f - d, 13f, 37.4f - d); cubicTo(17.4f - d * 0.4f, 37.4f - d, 22f - d, 32.4f - d * 0.5f, 22f - d, 27f); lineTo(22f - d, 3f) }, c, style = Stroke(1.1f)) }
                drawLine(ORO, Offset(4f, 6.4f), Offset(22f, 6.4f), 0.7f)
                for (x in listOf(10.4f, 15.6f)) drawLine(Color.White.copy(alpha = 0.1f), Offset(x, 3f), Offset(x + 0.4f, 37f), 1.2f)
            }
            // El disco del sol y, debajo, la flor de loto abierta sobre su tallo.
            val tinta = if (lleno > 0.55f) AZUL_OSC else ORO_LUZ; val petalo = if (lleno > 0.55f) AZUL_OSC else LINO
            drawCircle(if (lleno > 0.55f) ROJO else ORO, 2.5f, Offset(13f, 11.4f)); drawCircle(tinta, 2.5f, Offset(13f, 11.4f), style = Stroke(0.5f))
            drawLine(tinta, Offset(13f, 29.6f), Offset(13f, 24.4f), 0.8f, StrokeCap.Round)
            for (dx in listOf(-4.6f, -2.4f, 0f, 2.4f, 4.6f)) drawPath(Path().apply { moveTo(13f, 24.6f); cubicTo(13f + dx * 0.3f - 1.2f, 21f, 13f + dx - 0.9f, 18f, 13f + dx, 15.6f + kotlin.math.abs(dx) * 0.5f); cubicTo(13f + dx + 0.9f, 18f, 13f + dx * 0.3f + 1.2f, 21f, 13f, 24.6f); close() }, if (dx == 0f || kotlin.math.abs(dx) > 4f) petalo else TURQUESA)
            drawPath(Path().apply { moveTo(10.6f, 24.8f); cubicTo(11.6f, 26.4f, 14.4f, 26.4f, 15.4f, 24.8f); close() }, tinta)
            drawPath(cuerpo, AZUL_OSC.copy(alpha = 0.5f), style = Stroke(0.5f))
            // Las cuentas en gota del borde, y la vara dorada con sus dos remates de loto cerrado.
            for ((x, y) in listOf(7.4f to 35.4f, 10.2f to 37.2f, 13f to 37.8f, 15.8f to 37.2f, 18.6f to 35.4f)) drawOval(if (x == 13f) ROJO else TURQUESA, Offset(x - 0.8f, y), Size(1.6f, 2.2f))
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO, Color(0xFF7A5A1E)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) { drawOval(TURQUESA, Offset(x - 1.5f, 0.3f), Size(3f, 3.6f)); drawLine(ORO_LUZ, Offset(x, 0.6f), Offset(x, 3.6f), 0.5f) }
        }
    }
}
