package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale

/**
 * Prehistoria: una piel curtida, clara, que cuelga de un asta de reno por tres correas y acaba en dos picos, como las
 * patas de la piel. Lleva pintado un caballo de ocre rojo, de trazo ancho, y debajo una fila de puntos; sin letras. Al
 * mantener el dedo se llena de ocre amarillo desde abajo. **[Diseño propio]**: el caballo es inventado, no copia el de
 * ninguna cueva.
 */
internal object BanderitaCueva : Banderita {
    private val PIEL = Color(0xFFD9C4A0)
    private val PIEL_LUZ = Color(0xFFEBDCC0)
    private val PIEL_OSC = Color(0xFF9A7A56)
    private val OCRE_ROJO = Color(0xFFA4421F)
    private val OCRE = Color(0xFFD29A3E)
    private val OCRE_LUZ = Color(0xFFEFC36A)
    private val CARBON = Color(0xFF2B2523)
    private val ASTA = Color(0xFFE9DEC6)
    private val ASTA_OSC = Color(0xFF9C8A6A)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // La piel: los lados algo comidos y dos picos abajo.
            val cuerpo = Path().apply {
                moveTo(4.4f, 4.4f); lineTo(21.6f, 4.4f); cubicTo(22.4f, 12f, 21f, 20f, 22.2f, 29f); lineTo(20.4f, 37.4f); lineTo(16.6f, 32.6f); lineTo(13f, 34.6f); lineTo(9.4f, 32.6f); lineTo(5.6f, 37.4f)
                lineTo(3.8f, 29f); cubicTo(5f, 20f, 3.6f, 12f, 4.4f, 4.4f); close()
            }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(PIEL_OSC, PIEL, PIEL_LUZ, PIEL), 3.6f, 22.4f))
            // Lo que se ha llenado: el ocre amarillo, que sube desde los picos.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 37.6f - 33.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(OCRE_LUZ, OCRE), y, 37.6f), Offset(0f, y), Size(26f, 37.6f - y))
            }
            clipPath(cuerpo) {
                // Las manchas de la piel y el borde, más oscuro, donde se raspó peor.
                for ((x, y, r) in listOf(Triple(8f, 9f, 3f), Triple(18f, 27f, 3.6f), Triple(15f, 8f, 2.2f))) drawCircle(PIEL_OSC.copy(alpha = 0.16f), r, Offset(x, y))
                drawPath(cuerpo, PIEL_OSC.copy(alpha = 0.5f), style = Stroke(1.6f))
            }
            // El caballo: el contorno de carbón y el relleno de ocre rojo.
            val tinta = if (lleno > 0.55f) CARBON else OCRE_ROJO
            val caballo = Path().apply {
                moveTo(7.6f, 15.6f); cubicTo(9.6f, 14.2f, 12.6f, 14.8f, 14.2f, 15f); lineTo(16f, 12.2f); lineTo(16.6f, 10.6f); lineTo(17.4f, 11.8f); lineTo(18.8f, 13.2f); lineTo(19.8f, 15.4f); lineTo(18.8f, 16f); lineTo(17.4f, 14.8f)
                lineTo(16.6f, 17.6f); lineTo(16.4f, 21.8f); lineTo(15.4f, 21.8f); lineTo(15f, 18.8f); cubicTo(13f, 19.8f, 11f, 19.8f, 10f, 18.8f); lineTo(9.8f, 21.8f); lineTo(8.8f, 21.8f); lineTo(8.4f, 18.4f); cubicTo(7f, 17.6f, 7f, 16.4f, 7.6f, 15.6f); close()
            }
            drawPath(caballo, tinta)
            drawPath(caballo, CARBON.copy(alpha = 0.85f), style = Stroke(0.5f))
            drawLine(CARBON, Offset(14.4f, 14.8f), Offset(16.4f, 11.4f), 0.9f, StrokeCap.Round)                       // la crin, tiesa
            drawPath(Path().apply { moveTo(7.6f, 15.8f); cubicTo(6.2f, 16.6f, 6f, 18.4f, 6.4f, 19.8f) }, CARBON, style = Stroke(0.7f, cap = StrokeCap.Round))
            // La fila de puntos de debajo, hechos con la yema del dedo.
            drawPoints((0..4).map { Offset(7.6f + it * 2.7f, 25.6f) }, PointMode.Points, tinta, 1.5f, StrokeCap.Round)
            drawPath(cuerpo, PIEL_OSC.copy(alpha = 0.6f), style = Stroke(0.5f))
            // Las correas y el asta de reno de la que cuelga, con dos candiles.
            for (x in listOf(6.4f, 13f, 19.6f)) drawLine(PIEL_OSC, Offset(x, 4.8f), Offset(x, 1.6f), 0.8f)
            drawLine(ASTA_OSC, Offset(1.4f, 2.9f), Offset(24.6f, 2.5f), 2.6f, StrokeCap.Round); drawLine(ASTA, Offset(1.4f, 2.5f), Offset(24.6f, 2.1f), 1.6f, StrokeCap.Round)
            drawLine(ASTA, Offset(22.6f, 2.2f), Offset(24.8f, 0.2f), 1.1f, StrokeCap.Round); drawLine(ASTA, Offset(3.2f, 2.4f), Offset(1.4f, 0.4f), 1.1f, StrokeCap.Round)
        }
    }
}
