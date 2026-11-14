package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.volumen
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los doce objetos del día de Tomás, cada uno con su dibujo. Se pintan apoyados en [sobre] (la escotilla, a bordo; el
 * cofre, en el cayo), en unidades propias que quien llama agranda. El mosquete, que es largo, va aparte, apoyado.
 */
internal object ObjetosPirata {
    private val C = ColorPirata

    fun DrawScope.objeto(m: Momento, l: Luces, r: Rayo, sobre: Offset) {
        when (m.ficha.objeto) {
            "cuchillo" -> cuchillo(l, r, sobre)
            "coy" -> coy(l, r, sobre)
            "galleta" -> galleta(l, r, sobre)
            "ron" -> ron(l, r, sobre)
            "cabo" -> cabo(l, r, sobre)
            "campana" -> with(CubiertaPirata) { campana(l, r, sobre + Offset(0f, -0.6f)) }
            "catalejo" -> catalejo(l, r, sobre)
            "polvora" -> polvora(l, r, sobre)
            "bandera" -> bandera(l, r, sobre)
            "dados" -> dados(l, sobre, conCubilete = true)
            "brujula" -> brujula(l, r, sobre)
            else -> Unit                                                        // el mosquete va apoyado; sin objeto, nada
        }
    }

    /** El cuchillo, fuera de su funda de cuero. */
    private fun DrawScope.cuchillo(l: Luces, r: Rayo, s: Offset) {
        val funda = Path().apply { moveTo(s.x - 15f, s.y - 3.4f); lineTo(s.x - 2f, s.y - 3f); lineTo(s.x + 1f, s.y - 1.4f); lineTo(s.x - 2f, s.y); lineTo(s.x - 15f, s.y); close() }
        drawPath(funda, Brush.verticalGradient(listOf(l.tono(Color(0xFFA8703E)), l.tono(Color(0xFF6A4226))), s.y - 3.4f, s.y))
        for (x in listOf(-13f, -10.6f, -8.2f, -5.8f)) drawCircle(l.tono(C.LATON), 0.3f, Offset(s.x + x, s.y - 0.7f))
        drawRect(l.tono(Color(0xFF6A4226)), Offset(s.x - 16.6f, s.y - 4f), Size(2.4f, 4f))
        // La hoja, con su filo de luz, y el mango de palo con su virola.
        val hoja = Path().apply { moveTo(s.x + 3f, s.y - 2.6f); lineTo(s.x + 15f, s.y - 2.4f); cubicTo(s.x + 18f, s.y - 2f, s.x + 20f, s.y - 1f, s.x + 20.6f, s.y - 0.2f); lineTo(s.x + 3f, s.y); close() }
        drawPath(hoja, Brush.verticalGradient(listOf(l.tono(Color(0xFFE6E8EC)), l.tono(Color(0xFF9AA0A8))), s.y - 2.6f, s.y))
        drawLine(Color.White.copy(alpha = 0.7f), Offset(s.x + 4f, s.y - 2.2f), Offset(s.x + 16f, s.y - 2f), 0.3f)
        drawRoundRect(l.tono(Color(0xFF5E3E26)), Offset(s.x - 5.6f, s.y - 3.2f), Size(8.6f, 3.2f), CornerRadius(1.2f)); drawRect(l.tono(C.LATON), Offset(s.x + 2f, s.y - 3.4f), Size(1.2f, 3.6f))
    }

    /** El coy, enrollado y trincado con sus siete vueltas, con las araña de cabos asomando. */
    private fun DrawScope.coy(l: Luces, r: Rayo, s: Offset) {
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(s.x - 17f, s.y - 9f, s.x + 17f, s.y), CornerRadius(4.4f))) }
        drawPath(forma, Brush.verticalGradient(listOf(l.tono(lerp(C.LONA, Color.White, 0.2f)), l.tono(C.LONA), l.tono(mult(C.LONA_OSC, r.color, 0.7f))), s.y - 9f, s.y))
        clipPath(forma) { for (i in 0..6) { val x = s.x - 14.6f + i * 4.9f; drawLine(l.tono(C.CABO), Offset(x, s.y - 9.4f), Offset(x + 0.8f, s.y + 0.4f), 1.1f); drawLine(l.tono(mult(C.LONA_OSC, r.color, 0.5f)).copy(alpha = 0.6f), Offset(x + 1.6f, s.y - 8f), Offset(x + 2.4f, s.y - 1f), 0.4f) } }
        for (lado in listOf(-1f, 1f)) for (d in listOf(-2.4f, 0f, 2.4f)) drawLine(l.tono(C.CABO), Offset(s.x + lado * 16.6f, s.y - 4.6f + d * 0.8f), Offset(s.x + lado * 22f, s.y - 1f + d * 0.2f), 0.5f)
        for (lado in listOf(-1f, 1f)) drawCircle(l.tono(C.HIERRO), 1.1f, Offset(s.x + lado * 22.4f, s.y - 0.8f), style = Stroke(0.5f))
    }

    /** La galleta de barco: tres panes cocidos dos veces, redondos y picados, uno partido, en su plato de estaño. */
    private fun DrawScope.galleta(l: Luces, r: Rayo, s: Offset) {
        drawOval(l.tono(Color(0xFF9AA0A8)), Offset(s.x - 13f, s.y - 2.6f), Size(26f, 4.4f)); drawOval(l.tono(Color(0xFFC8CCD2)), Offset(s.x - 11.6f, s.y - 2.6f), Size(23.2f, 3f))
        for ((dx, dy) in listOf(-4.6f to -0.8f, 5f to -1f, 0f to -3.4f)) {
            val c = Offset(s.x + dx, s.y + dy)
            drawOval(l.tono(Color(0xFFB08E56)), Offset(c.x - 5.4f, c.y - 2f), Size(10.8f, 3.4f)); drawOval(l.tono(Color(0xFFE2C88E)), Offset(c.x - 5.4f, c.y - 2.8f), Size(10.8f, 3.2f))
            for (i in 0..4) drawCircle(l.tono(Color(0xFF8E6E3E)), 0.3f, Offset(c.x - 3.2f + i * 1.6f, c.y - 1.3f - (i % 2) * 0.5f))
        }
        drawPath(poligono(Offset(s.x + 11f, s.y - 0.4f), Offset(s.x + 15.4f, s.y - 1.4f), Offset(s.x + 14.6f, s.y + 0.4f)), l.tono(Color(0xFFE2C88E)))
    }

    /** El ron: una botella de cebolla, de vidrio verde oscuro, y su jarro. */
    private fun DrawScope.ron(l: Luces, r: Rayo, s: Offset) {
        val c = s + Offset(-3f, 0f)
        val botella = Path().apply { moveTo(c.x - 5.6f, c.y); cubicTo(c.x - 9f, c.y - 2f, c.x - 8f, c.y - 9f, c.x - 1.6f, c.y - 10.4f); lineTo(c.x - 1.4f, c.y - 16.4f); lineTo(c.x + 1.4f, c.y - 16.4f); lineTo(c.x + 1.6f, c.y - 10.4f); cubicTo(c.x + 8f, c.y - 9f, c.x + 9f, c.y - 2f, c.x + 5.6f, c.y); close() }
        volumen(l, r, botella, c.x - 8f, c.x + 8f, Color(0xFF3E5A3A))
        drawRect(l.tono(Color(0xFF2E4A2E)), Offset(c.x - 2f, c.y - 16.6f), Size(4f, 1.2f)); drawRect(l.tono(Color(0xFFB89868)), Offset(c.x - 1f, c.y - 18.4f), Size(2f, 2f))
        drawLine(Color.White.copy(alpha = 0.5f), Offset(c.x - 4.6f * r.lat, c.y - 8f), Offset(c.x - 5.4f * r.lat, c.y - 3f), 0.7f, StrokeCap.Round)
        jarro(l, r, s + Offset(11f, 0f))
    }

    /** Un jarro de hojalata, de los de la ración. */
    fun DrawScope.jarro(l: Luces, r: Rayo, s: Offset) {
        val forma = poligono(Offset(s.x - 3f, s.y), Offset(s.x - 3.4f, s.y - 6.4f), Offset(s.x + 3.4f, s.y - 6.4f), Offset(s.x + 3f, s.y))
        volumen(l, r, forma, s.x - 3.4f, s.x + 3.4f, Color(0xFFA8ACB4))
        drawOval(l.tono(Color(0xFF4A3220)), Offset(s.x - 3.4f, s.y - 7.2f), Size(6.8f, 1.6f))
        drawPath(Path().apply { moveTo(s.x + 3.2f, s.y - 5.4f); cubicTo(s.x + 6.4f, s.y - 5.4f, s.x + 6.4f, s.y - 1.4f, s.x + 3.1f, s.y - 1.4f) }, l.tono(Color(0xFF8A8E96)), style = Stroke(0.8f))
    }

    /** Un cabo: una aduja con su gaza y el pasador de hueso con que se trenza. */
    private fun DrawScope.cabo(l: Luces, r: Rayo, s: Offset) {
        val cabo = l.tono(C.CABO); val sombra = l.tono(mult(C.CABO, r.color, 0.85f)); val luz = l.tono(lerp(C.CABO, Color.White, 0.3f))
        for (i in 0..4) { val k = 1f - i / 5.6f; drawOval(if (i % 2 == 0) cabo else sombra, Offset(s.x - 11f * k - 2f, s.y - 2.6f - 5f * k), Size(22f * k, 7.4f * k), style = Stroke(1.9f)) }
        drawOval(luz.copy(alpha = 0.6f), Offset(s.x - 13f, s.y - 7.8f), Size(22f, 7.4f), style = Stroke(0.4f))
        drawPath(Path().apply { moveTo(s.x + 8f, s.y - 2f); cubicTo(s.x + 13f, s.y - 0.4f, s.x + 15f, s.y - 3f, s.x + 17f, s.y - 1.4f) }, cabo, style = Stroke(1.6f, cap = StrokeCap.Round))
        drawOval(cabo, Offset(s.x + 16f, s.y - 3.6f), Size(5f, 3.6f), style = Stroke(1.3f))
        drawPath(poligono(Offset(s.x - 20f, s.y - 0.4f), Offset(s.x - 9f, s.y - 2f), Offset(s.x - 9f, s.y)), l.tono(Color(0xFFEDE4CE)))
    }

    /** El catalejo del piloto: tres tubos de latón, tendido sobre su paño. */
    private fun DrawScope.catalejo(l: Luces, r: Rayo, s: Offset) {
        drawPath(poligono(Offset(s.x - 16f, s.y), Offset(s.x - 12f, s.y - 2.4f), Offset(s.x + 15f, s.y - 2f), Offset(s.x + 18f, s.y)), l.tono(Color(0xFF7A3A46)))
        fun tubo(x0: Float, x1: Float, g: Float, propio: Color) { val f = Path().apply { addRect(Rect(s.x + x0, s.y - 2f - g, s.x + x1, s.y - 2f)) }; drawPath(f, Brush.verticalGradient(listOf(l.tono(lerp(propio, Color.White, 0.5f)), l.tono(propio), l.tono(mult(propio, r.color, 0.9f))), s.y - 2f - g, s.y - 2f)) }
        tubo(-15f, -3f, 5f, Color(0xFF6A4630)); tubo(-3f, 7f, 4f, C.LATON); tubo(7f, 15f, 3f, C.LATON)
        for (x in listOf(-15f, -3.6f, 6.4f, 14.4f)) drawRect(l.tono(lerp(C.LATON, Color(0xFF5A4420), 0.4f)), Offset(s.x + x, s.y - 7.4f + (if (x > 0f) 1.4f else 0f) + (if (x > 10f) 1f else 0f)), Size(1.2f, 5.6f - (if (x > 0f) 1.4f else 0f) - (if (x > 10f) 1f else 0f)))
        drawOval(l.tono(Color(0xFF9CC4D4)), Offset(s.x - 16.4f, s.y - 6.6f), Size(1.6f, 4.2f))
    }

    /** La pólvora: un barrilete con zunchos de cobre (el hierro saca chispas) y el cuerno de cebar. */
    private fun DrawScope.polvora(l: Luces, r: Rayo, s: Offset) {
        val c = s + Offset(-4f, 0f)
        val forma = Path().apply { moveTo(c.x - 5.4f, c.y); cubicTo(c.x - 7.6f, c.y - 4f, c.x - 7.6f, c.y - 11f, c.x - 5.4f, c.y - 15f); lineTo(c.x + 5.4f, c.y - 15f); cubicTo(c.x + 7.6f, c.y - 11f, c.x + 7.6f, c.y - 4f, c.x + 5.4f, c.y); close() }
        volumen(l, r, forma, c.x - 7.4f, c.x + 7.4f, Color(0xFF9A7448))
        clipPath(forma) { for (y in listOf(-13f, -9.6f, -4.6f, -1.6f)) drawLine(l.tono(Color(0xFFB8683A)), Offset(c.x - 8f, c.y + y), Offset(c.x + 8f, c.y + y), 1.2f); for (x in listOf(-3f, 0f, 3f)) drawLine(l.tono(C.MADERA_OSC).copy(alpha = 0.5f), Offset(c.x + x, c.y - 15f), Offset(c.x + x, c.y), 0.3f) }
        drawOval(l.tono(Color(0xFF6E5030)), Offset(c.x - 5.4f, c.y - 16f), Size(10.8f, 2.2f))
        // El cuerno, con su tapón y su correa.
        drawPath(Path().apply { moveTo(s.x + 6f, s.y - 1f); cubicTo(s.x + 10f, s.y - 5f, s.x + 15f, s.y - 5f, s.x + 19f, s.y - 0.6f) }, l.tono(Color(0xFFD8C8A0)), style = Stroke(3f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(s.x + 6.4f, s.y - 1.6f); cubicTo(s.x + 10f, s.y - 5.6f, s.x + 14f, s.y - 5.6f, s.x + 17f, s.y - 2.6f) }, l.tono(Color(0xFF6A5A40)).copy(alpha = 0.6f), style = Stroke(0.5f))
        drawCircle(l.tono(C.MADERA_OSC), 1.3f, Offset(s.x + 19.4f, s.y - 0.5f))
    }

    /** La bandera negra, doblada sobre la escotilla: asoman la calavera y los dos sables. */
    private fun DrawScope.bandera(l: Luces, r: Rayo, s: Offset) {
        val negro = l.tono(C.NEGRO); val pliegue = l.tono(lerp(C.NEGRO, Color(0xFF6A6470), 0.45f)); val blanco = l.tono(Color(0xFFEDE8DC))
        val pano = Path().apply { moveTo(s.x - 18f, s.y - 5f); lineTo(s.x + 16f, s.y - 5.6f); lineTo(s.x + 19f, s.y - 1f); lineTo(s.x + 17f, s.y + 7f); cubicTo(s.x + 6f, s.y + 5.4f, s.x - 8f, s.y + 8.4f, s.x - 17f, s.y + 6.4f); lineTo(s.x - 20f, s.y - 1f); close() }
        drawPath(pano, negro)
        clipPath(pano) {
            drawRect(pliegue, Offset(s.x - 22f, s.y - 6f), Size(44f, 5f))
            for (x in listOf(-9f, 3f, 12f)) drawLine(pliegue.copy(alpha = 0.7f), Offset(s.x + x, s.y - 1f), Offset(s.x + x + 1.6f, s.y + 8f), 0.9f)
            // La calavera y, debajo, los dos sables cruzados.
            val c = s + Offset(-1f, 2.2f)
            drawCircle(blanco, 2.4f, c + Offset(0f, -1.4f)); drawRect(blanco, c + Offset(-1.4f, 0f), Size(2.8f, 1.6f))
            drawCircle(negro, 0.6f, c + Offset(-0.9f, -1.6f)); drawCircle(negro, 0.6f, c + Offset(0.9f, -1.6f))
            for (lado in listOf(-1f, 1f)) drawPath(Path().apply { moveTo(c.x - lado * 5f, c.y + 4.6f); cubicTo(c.x - lado * 1f, c.y + 3.4f, c.x + lado * 3f, c.y + 2f, c.x + lado * 5.4f, c.y - 0.4f) }, blanco, style = Stroke(0.9f, cap = StrokeCap.Round))
        }
        drawLine(l.tono(C.CABO), Offset(s.x - 19f, s.y - 3f), Offset(s.x - 24f, s.y - 1f), 0.8f, StrokeCap.Round)
    }

    /** Los dados de hueso y, si toca, el cubilete de cuero. */
    fun DrawScope.dados(l: Luces, s: Offset, conCubilete: Boolean) {
        val hueso = l.tono(Color(0xFFEFE6CE)); val sombra = l.tono(Color(0xFFB8AC8E)); val punto = l.tono(Color(0xFF2E2622))
        for ((dx, n) in listOf(-4f to 3, 2.6f to 5)) {
            val c = Offset(s.x + dx, s.y)
            drawRoundRect(sombra, Offset(c.x - 2.4f, c.y - 4.4f), Size(5f, 4.6f), CornerRadius(0.8f)); drawRoundRect(hueso, Offset(c.x - 2.4f, c.y - 4.8f), Size(4.4f, 4.2f), CornerRadius(0.8f))
            val puntos = if (n == 3) listOf(Offset(-1.3f, -3.8f), Offset(0f, -2.7f), Offset(1.3f, -1.6f)) else listOf(Offset(-1.3f, -3.8f), Offset(1.3f, -3.8f), Offset(0f, -2.7f), Offset(-1.3f, -1.6f), Offset(1.3f, -1.6f))
            for (p in puntos) drawCircle(punto, 0.36f, c + p + Offset(-0.2f, 0f))
        }
        if (conCubilete) {
            val c = s + Offset(11f, 0f)
            drawPath(poligono(Offset(c.x - 3f, c.y - 7f), Offset(c.x + 3f, c.y - 7f), Offset(c.x + 4.4f, c.y), Offset(c.x - 4.4f, c.y)), Brush.horizontalGradient(listOf(l.tono(Color(0xFF6A4226)), l.tono(Color(0xFFA8703E)), l.tono(Color(0xFF6A4226))), c.x - 4.4f, c.x + 4.4f))
            drawLine(l.tono(Color(0xFF4A2E1A)), Offset(c.x - 4.2f, c.y - 1.4f), Offset(c.x + 4.2f, c.y - 1.4f), 0.5f)
        }
    }

    /** La brújula: una caja de madera con su tapa de vidrio, la rosa pintada y la aguja. */
    private fun DrawScope.brujula(l: Luces, r: Rayo, s: Offset) {
        drawRect(Brush.verticalGradient(listOf(l.tono(C.MADERA), l.tono(mult(C.MADERA_OSC, r.color, 0.6f))), s.y - 5f, s.y), Offset(s.x - 10f, s.y - 5f), Size(20f, 5f))
        val tapa = poligono(Offset(s.x - 8f, s.y - 10f), Offset(s.x + 8f, s.y - 10f), Offset(s.x + 10f, s.y - 5f), Offset(s.x - 10f, s.y - 5f))
        drawPath(tapa, l.tono(lerp(C.MADERA, Color.White, 0.25f)))
        val rosa = Rect(s.x - 6.6f, s.y - 9.6f, s.x + 6.6f, s.y - 5.4f)
        drawOval(l.tono(Color(0xFFF2EAD2)), rosa.topLeft, rosa.size); drawOval(l.tono(C.LATON), rosa.topLeft, rosa.size, style = Stroke(0.7f))
        for (i in 0..7) { val a = i * 0.785f; drawLine(l.tono(Color(0xFF6A5A46)).copy(alpha = 0.8f), rosa.center, rosa.center + Offset(cos(a) * 5.4f, sin(a) * 1.7f), if (i % 2 == 0) 0.4f else 0.25f) }
        drawLine(l.tono(Color(0xFFB8382E)), rosa.center, rosa.center + Offset(-4.6f, -1f), 0.8f, StrokeCap.Round); drawLine(l.tono(C.HIERRO), rosa.center, rosa.center + Offset(4.6f, 1f), 0.8f, StrokeCap.Round)
        drawOval(Color.White.copy(alpha = 0.3f), Offset(s.x - 5f, s.y - 9.4f), Size(5f, 1.6f))
        drawCircle(l.tono(C.LATON), 0.8f, Offset(s.x, s.y - 2.4f))
    }
}
