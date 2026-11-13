package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Ciudad neón: un letrero de luz de los que cuelgan de canto en los callejones. Una caja oscura con un tubo magenta
 * que la bordea y, dentro, un reloj de arena de tubo verde agua (la historia). Al mantener el dedo se llena de luz
 * ámbar desde abajo, como si se fuera encendiendo.
 */
internal object BanderitaNeon : Banderita {
    private val MAGENTA = Color(0xFFFF3E9A)
    private val CIAN = Color(0xFF3CE8CF)
    private val AMBAR = Color(0xFFFFB25A)
    private val CAJA = Color(0xFF1E1A36)
    private val CAJA_LUZ = Color(0xFF3A3460)

    private fun DrawScope.tubo(trazo: Path, color: Color, grueso: Float) {
        val s = { w: Float -> Stroke(w, cap = StrokeCap.Round, join = StrokeJoin.Round) }
        drawPath(trazo, color.copy(alpha = 0.22f), style = s(grueso * 3.4f), blendMode = BlendMode.Plus)
        drawPath(trazo, color, style = s(grueso))
        drawPath(trazo, Color.White.copy(alpha = 0.8f), style = s(grueso * 0.36f))
    }

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El brazo del que cuelga y sus dos tirantes.
            drawRect(Color(0xFF6A6688), Offset(0f, 0f), Size(26f, 2f))
            for (x in listOf(7f, 19f)) drawLine(Color(0xFF8A86A8), Offset(x, 2f), Offset(x, 5f), 1f)
            val caja = Path().apply { addRoundRect(RoundRect(3f, 5f, 23f, 38f, CornerRadius(2.4f))) }
            drawPath(Path().apply { addPath(caja, Offset(0.8f, 1.2f)) }, Color.Black.copy(alpha = 0.34f))
            drawPath(caja, Brush.horizontalGradient(listOf(CAJA, CAJA_LUZ, CAJA), 3f, 23f))
            // Lo que se ha llenado: luz ámbar que sube.
            if (lleno > 0f) clipPath(caja) {
                val y = 38f - 33f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(Color(0xFFFFE2A8), AMBAR), y, 38f), Offset(0f, y), Size(26f, 38f - y))
            }
            // El tubo que la bordea.
            tubo(Path().apply { addRoundRect(RoundRect(5.2f, 7.2f, 20.8f, 35.8f, CornerRadius(1.6f))) }, if (lleno > 0.55f) Color(0xFFB8205E) else MAGENTA, 1.1f)
            // El reloj de arena: dos triángulos que se tocan, con sus dos tapas y el hilo de arena.
            val arena = Path().apply { moveTo(8.6f, 12.6f); lineTo(17.4f, 12.6f); lineTo(13f, 21.5f); lineTo(17.4f, 30.4f); lineTo(8.6f, 30.4f); lineTo(13f, 21.5f); close() }
            tubo(arena, if (lleno > 0.55f) Color(0xFF14786C) else CIAN, 0.9f)
            val tinta = if (lleno > 0.55f) Color(0xFF14786C) else CIAN
            drawLine(tinta, Offset(7.6f, 11f), Offset(18.4f, 11f), 1.1f, StrokeCap.Round); drawLine(tinta, Offset(7.6f, 32f), Offset(18.4f, 32f), 1.1f, StrokeCap.Round)
            drawLine(Color.White.copy(alpha = 0.85f), Offset(13f, 22.6f), Offset(13f, 27.6f), 0.5f); drawLine(Color.White.copy(alpha = 0.85f), Offset(11.4f, 28.8f), Offset(14.6f, 28.8f), 0.6f, StrokeCap.Round)
            // Dos tornillos y el reflejo del canto.
            for (y in listOf(6.4f, 36.6f)) drawCircle(Color(0xFF8A86A8), 0.5f, Offset(4.2f, y))
            drawLine(Color.White.copy(alpha = 0.14f), Offset(3.6f, 7f), Offset(3.6f, 36f), 0.5f)
        }
    }
}
