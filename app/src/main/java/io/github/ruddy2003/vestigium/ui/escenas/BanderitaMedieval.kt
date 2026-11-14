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
 * Scriptorium: una tira de pergamino colgada de una varilla de roble, con su orla roja de rúbrica y una gran inicial
 * pintada (un recuadro azul con un tallo enroscado de oro, que no es ninguna letra). Debajo, tres renglones de trazos
 * que no se leen. Acaba en un pico, con un sello de cera. Al mantener el dedo se llena de oro desde abajo, como una
 * hoja a la que le van poniendo el pan de oro. **[Diseño propio]**: no copia ningún manuscrito.
 */
internal object BanderitaMedieval : Banderita {
    private val PERGAMINO = Color(0xFFEFE3C2)
    private val PERGAMINO_OSC = Color(0xFFD2C29A)
    private val ROJO = Color(0xFFB8432E)
    private val AZUL = Color(0xFF3A628C)
    private val ORO = Color(0xFFD6A840)
    private val ORO_LUZ = Color(0xFFF2D684)
    private val TINTA = Color(0xFF2C2622)
    private val ROBLE = Color(0xFF634A32)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            val cuerpo = Path().apply { moveTo(4.4f, 3f); lineTo(21.6f, 3f); lineTo(21.6f, 31f); lineTo(13f, 37.4f); lineTo(4.4f, 31f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(PERGAMINO_OSC, PERGAMINO, PERGAMINO, PERGAMINO_OSC), 4.4f, 21.6f))
            // Lo que se ha llenado: el pan de oro, que sube desde el pico.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 37.4f - 34.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ORO_LUZ, ORO), y, 37.4f), Offset(0f, y), Size(26f, 37.4f - y))
            }
            clipPath(cuerpo) {
                // La orla de rúbrica, a los dos lados, y las manchas del pergamino.
                for (x in listOf(5.8f, 20.2f)) drawLine(ROJO.copy(alpha = 0.9f), Offset(x, 3f), Offset(x, 36f), 0.8f)
                drawCircle(PERGAMINO_OSC.copy(alpha = 0.5f), 2.4f, Offset(17f, 27f))
            }
            // La gran inicial: el recuadro azul, su filete de oro y el tallo enroscado.
            val tinta = if (lleno > 0.6f) ROJO else TINTA
            drawRect(AZUL, Offset(8f, 7f), Size(10f, 10f)); drawRect(if (lleno > 0.6f) ROJO else ORO, Offset(8f, 7f), Size(10f, 10f), style = Stroke(0.9f))
            drawPath(Path().apply { moveTo(10.4f, 15f); cubicTo(9.6f, 8.6f, 16.6f, 8.6f, 15.8f, 12.6f); cubicTo(15f, 15.4f, 11.8f, 14.6f, 12.6f, 12f) }, ORO_LUZ, style = Stroke(1.2f, cap = StrokeCap.Round))
            drawCircle(ROJO, 0.9f, Offset(15.6f, 14.8f)); drawCircle(Color(0xFF5F8A56), 0.8f, Offset(10.6f, 9.4f))
            // Tres renglones que no se leen.
            for ((i, y) in listOf(20.6f, 23.6f, 26.6f).withIndex()) { var x = 8f; var n = 0
                while (x < 18f - i) { val w = listOf(2.6f, 1.6f, 3.2f, 2f)[(n + i) % 4].coerceAtMost(18f - i - x); drawLine(tinta.copy(alpha = 0.8f), Offset(x, y), Offset(x + w, y), 0.7f); x += w + 0.8f; n++ } }
            // El sello de cera, en el pico.
            drawCircle(ROJO, 2.2f, Offset(13f, 32.4f)); drawCircle(Color(0xFFD86A50), 1.1f, Offset(12.6f, 32f))
            drawPath(cuerpo, ROBLE.copy(alpha = 0.5f), style = Stroke(0.5f))
            // La varilla de roble, con sus dos pomos.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFAE8250), ROBLE, Color(0xFF3A2A1C)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(1.8f, 24.2f)) drawCircle(ROBLE, 1.7f, Offset(x, 2.1f))
        }
    }
}
