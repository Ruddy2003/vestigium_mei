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
 * Mesoamérica: un estandarte de plumas, de los que se llevaban en un asta. Un paño de algodón rojo con su cenefa de
 * grecas escalonadas, un disco de jade en medio y un fleco de plumas verdes que cuelga; arriba, la vara con sus
 * borlas. Sin glifos. Al mantener el dedo se llena de amarillo de maíz desde abajo. **[Diseño propio]**: no copia
 * ningún estandarte pintado ni tallado.
 */
internal object BanderitaMeso : Banderita {
    private val ROJO = Color(0xFFB0503C)
    private val ROJO_LUZ = Color(0xFFCB6E52)
    private val ROJO_OSC = Color(0xFF7A3226)
    private val JADE = Color(0xFF4F9A82)
    private val JADE_LUZ = Color(0xFF8CCBB0)
    private val PLUMA = Color(0xFF2F7E5C)
    private val MAIZ = Color(0xFFE2C452)
    private val MAIZ_LUZ = Color(0xFFF4E29A)
    private val CREMA = Color(0xFFF2E8D0)
    private val MADERA = Color(0xFF7A5A38)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 30f); lineTo(4f, 30f); close() }
            // El fleco de plumas, que cuelga del borde de abajo.
            for (i in 0..5) {
                val x = 5.4f + i * 3.04f; val largo = if (i % 2 == 0) 7.4f else 6f
                drawPath(Path().apply { moveTo(x - 1.5f, 29.6f); cubicTo(x - 1.8f, 33f, x - 0.8f, 35.6f, x, 29.6f + largo); cubicTo(x + 0.8f, 35.6f, x + 1.8f, 33f, x + 1.5f, 29.6f); close() }, if (lleno > 0.02f && i < 6) lerpPluma(lleno) else PLUMA)
                drawLine(JADE_LUZ.copy(alpha = 0.7f), Offset(x, 30f), Offset(x, 29.6f + largo - 1f), 0.35f)
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(ROJO_OSC, ROJO, ROJO_LUZ, ROJO), 4f, 22f))
            // Lo que se ha llenado: el amarillo del maíz, que sube desde abajo.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 30f - 27f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(MAIZ_LUZ, MAIZ), y, 30f), Offset(0f, y), Size(26f, 30f - y))
            }
            val tinta = if (lleno > 0.55f) ROJO_OSC else CREMA
            clipPath(cuerpo) {
                // La cenefa de grecas escalonadas, arriba y abajo.
                for (y in listOf(5.2f, 26.2f)) {
                    drawLine(tinta, Offset(4f, y - 1f), Offset(22f, y - 1f), 0.5f); drawLine(tinta, Offset(4f, y + 2.4f), Offset(22f, y + 2.4f), 0.5f)
                    for (i in 0..4) { val x = 5f + i * 3.6f; drawPath(Path().apply { moveTo(x, y + 1.9f); lineTo(x, y + 0.9f); lineTo(x + 1f, y + 0.9f); lineTo(x + 1f, y); lineTo(x + 2f, y); lineTo(x + 2f, y + 1.9f) }, tinta, style = Stroke(0.5f)) }
                }
            }
            // El disco de jade, con su aro y sus cuatro rumbos.
            val centro = Offset(13f, 16.6f)
            drawCircle(tinta, 5.6f, centro); drawCircle(JADE, 4.6f, centro); drawCircle(JADE_LUZ, 4.6f, centro, style = Stroke(0.5f)); drawCircle(if (lleno > 0.55f) ROJO_OSC else ROJO, 1.5f, centro)
            for (d in listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f))) drawCircle(CREMA, 0.6f, centro + d * 3.1f)
            drawPath(cuerpo, ROJO_OSC.copy(alpha = 0.5f), style = Stroke(0.5f))
            // La vara, con sus dos borlas de plumas.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFA8845A), MADERA, Color(0xFF4A3420)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(2f, 24f)) { drawCircle(JADE, 1.6f, Offset(x, 2.1f)); drawLine(PLUMA, Offset(x, 3.4f), Offset(x + (if (x < 13f) -0.6f else 0.6f), 7.6f), 1.1f) }
        }
    }

    /** El color de las plumas del fleco conforme se llena: del verde al amarillo del maíz. */
    private fun lerpPluma(lleno: Float): Color = androidx.compose.ui.graphics.lerp(PLUMA, MAIZ, (lleno * 1.4f).coerceIn(0f, 1f))
}
