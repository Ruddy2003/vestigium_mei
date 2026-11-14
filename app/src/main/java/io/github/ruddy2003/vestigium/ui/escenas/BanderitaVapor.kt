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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Vapor victoriano: un banderín de ferroviario, verde como la chapa de la máquina, con un filete de latón y una rueda
 * de radios bordada, colgado de una barra de hierro con dos remaches; acaba en dos picos. Sin letras. Al mantener el
 * dedo se va llenando desde abajo con el rojo del fuego, y el cubo de la rueda se enciende.
 * **[Diseño propio]**: no copia el emblema de ninguna compañía.
 */
internal object BanderitaVapor : Banderita {
    private val VERDE = Color(0xFF2F6A46)
    private val VERDE_LUZ = Color(0xFF4A8A62)
    private val VERDE_OSC = Color(0xFF1E4A30)
    private val LATON = Color(0xFFD2A44A)
    private val FUEGO = Color(0xFFD9502E)
    private val FUEGO_LUZ = Color(0xFFFF9A48)
    private val HIERRO = Color(0xFF4A4C54)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con sus dos picos.
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 38.4f); lineTo(17.5f, 33f); lineTo(13f, 38.4f); lineTo(8.5f, 33f); lineTo(4f, 38.4f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(VERDE_OSC, VERDE, VERDE_LUZ, VERDE), 4f, 22f))
            // Lo que se ha llenado: el rojo del hogar, que sube desde los picos.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38.4f - 35.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(FUEGO_LUZ, FUEGO), y, 38.4f), Offset(0f, y), Size(26f, 38.4f - y))
            }
            clipPath(cuerpo) {
                for (x in listOf(9f, 17.4f)) drawLine(Color.White.copy(alpha = 0.1f), Offset(x, 3f), Offset(x + 0.6f, 38f), 1.4f)
                // El filete de latón, todo alrededor.
                drawRect(LATON.copy(alpha = 0.9f), Offset(5.6f, 5.4f), Size(14.8f, 24f), style = Stroke(0.7f))
            }
            // La rueda: la llanta, el cubo y sus ocho radios, con su muñón.
            val c = Offset(13f, 17.4f)
            drawCircle(LATON, 6.2f, c, style = Stroke(1.4f)); drawCircle(LATON.copy(alpha = 0.6f), 4.9f, c, style = Stroke(0.4f))
            for (i in 0 until 8) { val a = i * 0.7854f + 0.2f; drawLine(LATON, c + Offset(cos(a), sin(a)) * 1.6f, c + Offset(cos(a), sin(a)) * 5.4f, 0.8f, StrokeCap.Round) }
            drawCircle(if (lleno > 0.6f) Color(0xFFFFE0A0) else LATON, 1.8f, c); drawCircle(VERDE_OSC, 0.6f, c)
            drawCircle(LATON, 1f, c + Offset(cos(0.9f), sin(0.9f)) * 3.4f)
            // La barra de hierro de la que cuelga, con sus dos remaches.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF8A9096), HIERRO, Color(0xFF2E3236)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(6f, 20f)) drawCircle(Color(0xFFB8BCC0), 0.8f, Offset(x, 2.1f))
        }
    }
}
