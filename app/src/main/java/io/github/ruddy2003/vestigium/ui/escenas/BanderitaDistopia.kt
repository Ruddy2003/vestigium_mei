package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
 * Distopía: una colgadura del Estado, larga y recta, que cuelga de una barra de hierro y acaba en un pico entrante.
 * Es gris, del color del hormigón, con el ojo del emblema dibujado en línea (una almendra larga con su párpado, no un
 * disco): sin letras ni cara. Al mantener el dedo se va tiñendo de
 * rojo desde abajo, y la pupila se enciende. **[Diseño propio]**: el emblema es inventado y no copia ningún símbolo real.
 */
internal object BanderitaDistopia : Banderita {
    private val GRIS = Color(0xFF8E9388)
    private val GRIS_LUZ = Color(0xFFB4B8AA)
    private val GRIS_OSC = Color(0xFF666A62)
    private val ROJO = Color(0xFFB5372B)
    private val ROJO_LUZ = Color(0xFFDA5A44)
    private val HUESO = Color(0xFFEDE6D0)
    private val HIERRO = Color(0xFF4C5054)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con su pico entrante.
            val cuerpo = Path().apply { moveTo(4f, 3f); lineTo(22f, 3f); lineTo(22f, 38.4f); lineTo(13f, 32f); lineTo(4f, 38.4f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(GRIS_OSC, GRIS, GRIS_LUZ, GRIS), 4f, 22f))
            // Lo que se ha llenado: el rojo de la consigna, que sube desde el pico.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38.4f - 35.4f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ROJO_LUZ, ROJO), y, 38.4f), Offset(0f, y), Size(26f, 38.4f - y))
            }
            clipPath(cuerpo) {
                for (x in listOf(9f, 17.4f)) drawLine(Color.White.copy(alpha = 0.1f), Offset(x, 3f), Offset(x + 0.6f, 38f), 1.4f)
                // Las dos franjas de arriba y de abajo.
                drawRect(HUESO.copy(alpha = 0.85f), Offset(4f, 6.6f), Size(18f, 1.2f)); drawRect(HUESO.copy(alpha = 0.85f), Offset(4f, 27f), Size(18f, 1.2f))
            }
            // El ojo, en línea: la almendra, larga; el párpado; el aro del iris y el punto de la pupila.
            val c = Offset(13f, 17.8f); val w = 15.4f; val h = 3.9f
            val linea = Stroke(1.1f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            drawCircle(if (lleno > 0.6f) Color(0xFF7C2820) else GRIS_OSC, 2.3f, c)
            drawPath(Path().apply { moveTo(c.x - w / 2f, c.y + 0.4f); cubicTo(c.x - w * 0.22f, c.y - h, c.x + w * 0.2f, c.y - h, c.x + w / 2f, c.y + 0.4f); cubicTo(c.x + w * 0.2f, c.y + h * 0.92f, c.x - w * 0.22f, c.y + h * 0.92f, c.x - w / 2f, c.y + 0.4f); close() }, HUESO, style = linea)
            drawPath(Path().apply { moveTo(c.x - w * 0.47f, c.y - h * 0.6f); cubicTo(c.x - w * 0.2f, c.y - h * 1.85f, c.x + w * 0.2f, c.y - h * 1.85f, c.x + w * 0.47f, c.y - h * 0.6f) }, HUESO, style = Stroke(0.9f, cap = StrokeCap.Round))
            drawCircle(HUESO, 2.3f, c, style = Stroke(0.95f)); drawCircle(if (lleno > 0.6f) Color(0xFFFFD0B0) else HUESO, 0.8f, c)
            // La barra de hierro de la que cuelga, con sus dos argollas.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF8A9096), HIERRO, Color(0xFF2E3236)), 0.6f, 3.6f), Offset(1.6f, 0.6f), Size(22.8f, 3f), CornerRadius(1.5f))
            for (x in listOf(6f, 20f)) drawRect(Color(0xFF2E3236), Offset(x - 0.7f, 0.4f), Size(1.4f, 3.8f))
        }
    }
}
