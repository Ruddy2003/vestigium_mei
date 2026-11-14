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
 * Ciudad verde: un paño verde de hiedra, remendado, que cuelga de una rama con dos lazadas de cuerda y acaba en dos
 * picos. Lleva bordada una semilla que brota (la semilla, el tallo y sus dos hojas): sin letras. Al mantener el dedo se
 * va llenando de color de paja desde abajo, como un bancal que madura, y las dos hojas se abren en flor.
 * **[Diseño propio]**: el emblema es inventado.
 */
internal object BanderitaRuinas : Banderita {
    private val VERDE = Color(0xFF3E683C)
    private val VERDE_LUZ = Color(0xFF5E8A50)
    private val VERDE_OSC = Color(0xFF2A4A2E)
    private val PAJA = Color(0xFFDCC27C)
    private val PAJA_LUZ = Color(0xFFF0DC9C)
    private val HUESO = Color(0xFFF2ECD8)
    private val TELA = Color(0xFFB8543A)
    private val RAMA = Color(0xFF6A5240)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con sus dos picos.
            val cuerpo = Path().apply { moveTo(4.4f, 4f); lineTo(21.6f, 4f); lineTo(21.6f, 38.6f); lineTo(17.4f, 33.6f); lineTo(13f, 38.6f); lineTo(8.6f, 33.6f); lineTo(4.4f, 38.6f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(VERDE_OSC, VERDE, VERDE_LUZ, VERDE), 4.4f, 21.6f))
            // Lo que se ha llenado: el color de la paja, que sube desde los picos.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38.6f - 34.6f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(PAJA_LUZ, PAJA), y, 38.6f), Offset(0f, y), Size(26f, 38.6f - y))
            }
            clipPath(cuerpo) {
                // El pliegue, la cenefa de puntadas y el remiendo.
                drawLine(Color.White.copy(alpha = 0.1f), Offset(10f, 4f), Offset(10.6f, 38f), 1.4f)
                for (i in 0..7) { drawLine(HUESO.copy(alpha = 0.8f), Offset(5.6f + i * 2.1f, 7.4f), Offset(6.8f + i * 2.1f, 7.4f), 0.6f); drawLine(HUESO.copy(alpha = 0.8f), Offset(5.6f + i * 2.1f, 29.4f), Offset(6.8f + i * 2.1f, 29.4f), 0.6f) }
                drawRect(TELA.copy(alpha = 0.9f), Offset(16.4f, 24f), Size(3.6f, 3.4f)); for (i in 0..2) drawLine(HUESO.copy(alpha = 0.7f), Offset(16.2f, 24.6f + i * 1.2f), Offset(16.9f, 24.6f + i * 1.2f), 0.4f)
            }
            // La semilla que brota: la semilla partida, la raíz, el tallo y las dos hojas (o la flor, cuando se llena).
            val tinta = if (lleno > 0.6f) VERDE_OSC else HUESO
            val linea = Stroke(1.1f, cap = StrokeCap.Round)
            drawOval(tinta, Offset(10.2f, 20.4f), Size(5.6f, 3.8f)); drawLine(if (lleno > 0.6f) PAJA_LUZ else VERDE, Offset(11f, 22.3f), Offset(15f, 22.3f), 0.5f)
            drawPath(Path().apply { moveTo(13f, 24f); cubicTo(12.4f, 25.4f, 13.6f, 26.2f, 12.8f, 27.6f) }, tinta, style = Stroke(0.7f, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(13f, 20.6f); cubicTo(12.6f, 17.6f, 13.4f, 15.6f, 13f, 13f) }, tinta, style = linea)
            if (lleno > 0.6f) { for (i in 0..5) { val a = i * 1.047f; drawCircle(TELA, 1.5f, Offset(13f + kotlin.math.cos(a) * 2.4f, 12f + kotlin.math.sin(a) * 2.4f)) }; drawCircle(PAJA_LUZ, 1.3f, Offset(13f, 12f)) }
            else {
                drawPath(Path().apply { moveTo(13f, 14.6f); cubicTo(10.4f, 14.4f, 8.4f, 12.4f, 8f, 10.4f); cubicTo(10.6f, 10.2f, 12.6f, 12f, 13f, 14.6f); close() }, tinta)
                drawPath(Path().apply { moveTo(13f, 13.4f); cubicTo(15.6f, 13.2f, 17.6f, 11.2f, 18f, 9.2f); cubicTo(15.4f, 9f, 13.4f, 10.8f, 13f, 13.4f); close() }, tinta)
            }
            // La rama de la que cuelga, con sus dos lazadas de cuerda y un brote.
            drawLine(Color(0xFF3E3024), Offset(1.4f, 3f), Offset(24.8f, 2f), 2.6f, StrokeCap.Round); drawLine(RAMA, Offset(1.4f, 2.6f), Offset(24.8f, 1.6f), 1.6f, StrokeCap.Round)
            for (x in listOf(6.6f, 19.4f)) { drawLine(Color(0xFFC8B48A), Offset(x - 0.9f, 0.6f), Offset(x - 0.9f, 4.6f), 0.8f, StrokeCap.Round); drawLine(Color(0xFFC8B48A), Offset(x + 0.9f, 0.6f), Offset(x + 0.9f, 4.6f), 0.8f, StrokeCap.Round); drawLine(Color(0xFF8A7A56), Offset(x - 1.4f, 3.6f), Offset(x + 1.4f, 3.6f), 0.7f) }
            drawLine(RAMA, Offset(22.6f, 1.8f), Offset(24.6f, 0.2f), 0.7f, StrokeCap.Round); drawOval(VERDE_LUZ, Offset(23.6f, -0.6f), Size(2f, 1.2f))
        }
    }
}
