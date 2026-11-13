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
 * Pirata: la bandera negra, colgada de su verga como un gallardete, con la punta partida en dos farpas. Lleva la
 * calavera y, debajo, dos sables cruzados, sin ninguna letra. Al mantener el dedo se va tiñendo de rojo desde abajo:
 * la roja era la que se izaba después, la de «sin cuartel».
 */
internal object BanderitaPirata : Banderita {
    private val NEGRO = Color(0xFF221D22)
    private val NEGRO_LUZ = Color(0xFF45404A)
    private val ROJO = Color(0xFFB8382E)
    private val ROJO_LUZ = Color(0xFFE0604A)
    private val HUESO = Color(0xFFF1EBDC)
    private val MADERA = Color(0xFFB8905A)
    private val MADERA_OSC = Color(0xFF6A4E30)

    override fun DrawScope.dibujar(lleno: Float) {
        val k = size.width / Banderita.ANCHO
        scale(k, k, Offset.Zero) {
            // El paño, con sus dos farpas.
            val cuerpo = Path().apply { moveTo(3.6f, 3f); lineTo(22.4f, 3f); lineTo(22.4f, 38f); lineTo(13f, 31f); lineTo(3.6f, 38f); close() }
            drawPath(Path().apply { addPath(cuerpo, Offset(0.9f, 1.2f)) }, Color.Black.copy(alpha = 0.28f))
            drawPath(cuerpo, Brush.horizontalGradient(listOf(NEGRO, NEGRO_LUZ, NEGRO, NEGRO), 3.6f, 22.4f))
            // Lo que se ha llenado: el rojo, que sube desde las farpas.
            if (lleno > 0f) clipPath(cuerpo) {
                val y = 38f - 35f * lleno.coerceIn(0f, 1f)
                drawRect(Brush.verticalGradient(listOf(ROJO_LUZ, ROJO), y, 38f), Offset(0f, y), Size(26f, 38f - y))
            }
            clipPath(cuerpo) { for (x in listOf(8.6f, 17.6f)) drawLine(Color.White.copy(alpha = 0.08f), Offset(x, 3f), Offset(x + 0.8f, 38f), 1.4f) }
            // La calavera: el cráneo, las cuencas, la nariz y la quijada con sus dientes.
            val c = Offset(13f, 12.4f)
            drawCircle(HUESO, 5f, c); drawRoundRect(HUESO, Offset(c.x - 3f, c.y + 2.6f), Size(6f, 4f), androidx.compose.ui.geometry.CornerRadius(1f))
            val hueco = if (lleno > 0.6f) ROJO else NEGRO
            drawCircle(hueco, 1.5f, c + Offset(-2f, 0.4f)); drawCircle(hueco, 1.5f, c + Offset(2f, 0.4f))
            drawPath(Path().apply { moveTo(c.x, c.y + 1.8f); lineTo(c.x - 0.8f, c.y + 3.4f); lineTo(c.x + 0.8f, c.y + 3.4f); close() }, hueco)
            for (x in listOf(-1.5f, 0f, 1.5f)) drawLine(hueco, Offset(c.x + x, c.y + 4.6f), Offset(c.x + x, c.y + 6.6f), 0.5f)
            // Los dos sables cruzados, con su guarnición.
            for (lado in listOf(-1f, 1f)) {
                drawPath(Path().apply { moveTo(c.x - lado * 6.6f, c.y + 16.4f); cubicTo(c.x - lado * 2f, c.y + 14.6f, c.x + lado * 3.6f, c.y + 11.6f, c.x + lado * 6.6f, c.y + 8f) }, HUESO, style = Stroke(1.3f, cap = StrokeCap.Round))
                drawLine(HUESO, Offset(c.x - lado * 5.6f, c.y + 14.4f), Offset(c.x - lado * 3.8f, c.y + 17f), 1.1f, StrokeCap.Round)
            }
            // La verga de la que cuelga, con sus dos cabos.
            drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFE2C088), MADERA, MADERA_OSC), 0.6f, 3.6f), Offset(1.4f, 0.6f), Size(23.2f, 3f), androidx.compose.ui.geometry.CornerRadius(1.5f))
            for (x in listOf(5f, 21f)) drawLine(Color(0xFFD8C08A), Offset(x, 0.4f), Offset(x + 0.6f, 4f), 0.9f)
        }
    }
}
