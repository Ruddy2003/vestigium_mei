package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Edad Contemporánea
// =====================================================================

/** Un engrane: dientes cuadrados alrededor de [c]. */
internal fun engrane(c: Offset, r: Float, dientes: Int): Path = Path().apply {
    val n = dientes * 4
    for (k in 0 until n) {
        val a = 2 * Math.PI * k / n
        val rr = if ((k / 2) % 2 == 0) r else r * 0.8f
        val p = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Una carátula de manómetro: aro, marcas y aguja ([valor] de 0 a 1). */
internal fun DrawScope.manometro(c: Offset, r: Float, aro: Color, cara: Color, aguja: Color, valor: Float = 0.62f) {
    val u = density
    drawCircle(aro, r, c)
    drawCircle(cara, r * 0.8f, c)
    for (k in 0..10) {
        val a = Math.toRadians(135.0 + k * 27.0)
        val p0 = Offset(c.x + r * 0.62f * cos(a).toFloat(), c.y + r * 0.62f * sin(a).toFloat())
        val p1 = Offset(c.x + r * 0.76f * cos(a).toFloat(), c.y + r * 0.76f * sin(a).toFloat())
        drawLine(Color.Black.copy(alpha = 0.6f), p0, p1, (if (k % 5 == 0) 1.4f else 0.7f) * u)
    }
    val a = Math.toRadians(135.0 + 270.0 * valor)
    drawLine(aguja, c, Offset(c.x + r * 0.66f * cos(a).toFloat(), c.y + r * 0.66f * sin(a).toFloat()), 1.4f * u, StrokeCap.Round)
    drawCircle(aro, r * 0.1f, c)
}
