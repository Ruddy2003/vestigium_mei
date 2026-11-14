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
 * Roma: un estandarte con los dos colores de la ciudad, el púrpura y el oro, que cuelga de una vara de latón y acaba
 * en dos picos con su fleco. Lleva bordada una cúpula con su linterna, sin letras. Al mantener el dedo se llena de oro
 * desde abajo. **[Diseño propio]**: no copia el escudo ni el estandarte de la ciudad.
 */
internal object BanderitaUrbe : Banderita {
    private val PURPURA = Color(0xFF8C2A32)
    private val PURPURA_LUZ = Color(0xFFB04A48)
    private val PURPURA_OSC = Color(0xFF64202A)
    private val ORO = Color(0xFFD8A640)
    private val ORO_LUZ = Color(0xFFF2D27A)
    private val LATON = Color(0xFF9A7A34)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 37f); lineTo(17.5f, 32.6f); lineTo(13f, 37f); lineTo(8.5f, 32.6f); lineTo(4f, 37f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(PURPURA_OSC, PURPURA, PURPURA_LUZ, PURPURA), 4f, 22f))
            // Lo que se ha llenado: el oro, que sube desde los picos.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 37f - 34f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 37f), Offset(0f, y), Size(26f, 37f - y))
            }
            clipPath(cuerpo) {
                for (x in listOf(9f, 17.4f)) drawLine(Color.White.copy(alpha = 0.1f), Offset(x, 3f), Offset(x + 0.6f, 37f), 1.4f)
                // La orla de oro, a los dos lados.
                for (x in listOf(5.2f, 20.8f)) drawLine(ORO.copy(alpha = 0.9f), Offset(x, 3f), Offset(x, 37f), 0.9f)
            }
            // La cúpula bordada: el tambor, el casquete con sus nervios y la linterna.
            val tinta = if (lleno > 0.6f) PURPURA_OSC else ORO_LUZ
            drawRect(tinta, Offset(8.2f, 21.4f), Size(9.6f, 3.4f)); drawRect(tinta, Offset(7.4f, 24.8f), Size(11.2f, 1.1f)); drawRect(tinta, Offset(7.6f, 20.4f), Size(10.8f, 1f))
            val casquete = Path().apply { moveTo(8.4f, 20.4f); cubicTo(8.4f, 14.6f, 10.6f, 11.4f, 13f, 11.4f); cubicTo(15.4f, 11.4f, 17.6f, 14.6f, 17.6f, 20.4f); close() }
            drawPath(casquete, tinta)
            val hueco = if (lleno > 0.6f) ORO else PURPURA
            for (x in listOf(10.4f, 13f, 15.6f)) { drawLine(hueco, Offset(x, 20.2f), Offset(13f + (x - 13f) * 0.3f, 12f), 0.5f); drawRect(hueco, Offset(x - 0.5f, 22f), Size(1f, 2f)) }
            drawRect(tinta, Offset(12.1f, 8.6f), Size(1.8f, 3f)); drawCircle(tinta, 0.9f, Offset(13f, 7.8f))
            // El fleco de los picos.
            for (x in listOf(5f, 7f, 10f, 12f, 14f, 16f, 19f, 21f)) drawLine(ORO, Offset(x, if (x in 7.5f..9.5f || x in 16.5f..18.5f) 33.4f else 35.6f), Offset(x, 38.6f), 0.6f)
            drawPath(cuerpo, PURPURA_OSC.copy(alpha = 0.5f), style = Stroke(0.5f))
            // La vara de latón, con sus dos pomos.
            drawRoundRect(Brush.verticalGradient(listOf(ORO_LUZ, LATON, Color(0xFF5E4A20)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) drawCircle(LATON, 1.7f, Offset(x, 2.1f))
        }
    }
}
