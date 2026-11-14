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
 * Horror cósmico: un gallardete de paño verde negro, del color de la tinta del archivo, colgado de un palo de madera
 * de deriva con dos vueltas de cabo. Lleva bordados en color hueso un faro con sus dos haces y, encima, la luna
 * menguante del tema; sin letras. Al mantener el dedo se llena desde abajo con la luz de aceite, y la linterna del
 * faro se enciende. **[Diseño propio]**: el faro es el del pueblo inventado del archivista.
 */
internal object BanderitaArcano : Banderita {
    private val TINTA = Color(0xFF2E4A3E)
    private val TINTA_LUZ = Color(0xFF47685A)
    private val TINTA_OSC = Color(0xFF1C2E28)
    private val HUESO = Color(0xFFE8DEBC)
    private val ACEITE = Color(0xFFFFC36A)
    private val ACEITE_OSC = Color(0xFFC98A3A)
    private val MADERA = Color(0xFF8A6E4E)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño: recto por arriba y acabado en dos picos, como una cola de golondrina.
            val cuerpo = Path().apply { moveTo(4.4f, 3.4f); lineTo(21.6f, 3.4f); lineTo(21.6f, 38.6f); lineTo(13f, 33f); lineTo(4.4f, 38.6f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(TINTA_OSC, TINTA, TINTA_LUZ, TINTA), 4.4f, 21.6f))
            // Lo que se ha llenado: la luz de la lámpara, que sube.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38.6f - 35.2f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ACEITE, ACEITE_OSC), y, 38.6f), Offset(0f, y), Size(26f, 38.6f - y))
            }
            val hilo = if (lleno > 0.75f) TINTA_OSC else HUESO
            clipPath(cuerpo) {
                for (x in listOf(9f, 17f)) drawLine(Color.White.copy(alpha = 0.08f), Offset(x, 3f), Offset(x + 0.6f, 38f), 1.4f)
                // El ribete.
                drawPath(Path().apply { moveTo(6f, 5f); lineTo(20f, 5f); lineTo(20f, 35.4f); lineTo(13f, 30.8f); lineTo(6f, 35.4f); close() }, hilo.copy(alpha = 0.55f), style = Stroke(0.5f))
            }
            // La luna menguante, arriba.
            drawPath(Path().apply { moveTo(15.6f, 7f); cubicTo(11f, 7.6f, 11f, 13f, 15.6f, 13.6f); cubicTo(13.2f, 12.4f, 13.2f, 8.2f, 15.6f, 7f); close() }, if (lleno > 0.75f) TINTA_OSC else HUESO)
            // El faro: la torre, la galería, la linterna con su cúpula y el suelo de la punta.
            val linea = Stroke(0.9f, cap = StrokeCap.Round)
            drawPath(Path().apply { moveTo(10.6f, 28.4f); lineTo(11.6f, 19.6f); lineTo(14.4f, 19.6f); lineTo(15.4f, 28.4f); close() }, hilo, style = linea)
            drawLine(hilo, Offset(10.6f, 19.4f), Offset(15.4f, 19.4f), 0.9f, StrokeCap.Round)
            drawLine(hilo, Offset(11f, 24f), Offset(15f, 24f), 0.7f)
            drawPath(Path().apply { moveTo(11.8f, 19f); lineTo(11.8f, 16.6f); cubicTo(12f, 15f, 14f, 15f, 14.2f, 16.6f); lineTo(14.2f, 19f) }, hilo, style = linea)
            drawPath(Path().apply { moveTo(7f, 28.6f); cubicTo(10f, 27.6f, 16f, 27.6f, 19f, 28.8f) }, hilo, style = linea)
            // Los dos haces, y la linterna, que se enciende al llenarse.
            drawLine(hilo.copy(alpha = 0.8f), Offset(11.2f, 17.4f), Offset(6.4f, 15.6f), 0.6f, StrokeCap.Round); drawLine(hilo.copy(alpha = 0.8f), Offset(14.8f, 17.4f), Offset(19.6f, 15.6f), 0.6f, StrokeCap.Round)
            drawCircle(if (lleno > 0.4f) Color(0xFFFFF4D0) else hilo, 0.9f, Offset(13f, 17.5f))
            // El palo de madera de deriva del que cuelga, con sus dos vueltas de cabo.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFB89A74), MADERA, Color(0xFF54443A)), 0.6f, 3.8f), Offset(1.6f, 0.6f), Size(22.8f, 3.2f), androidx.compose.ui.geometry.CornerRadius(1.6f))
            for (x in listOf(6.4f, 19.6f)) { drawRect(Color(0xFFC9B88A), Offset(x - 1f, 0.4f), Size(2f, 3.8f)); drawLine(Color(0xFF6E5640), Offset(x, 0.4f), Offset(x, 4.2f), 0.3f) }
        }
    }
}
