package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono

/**
 * Los doce objetos del día del escriba, sobre el banco del códice, a su derecha (la manta y el petate, colgados o
 * apoyados en su canto). Cada uno recibe [tono], que da el color de una cosa con la luz de ese sitio, y la escala [k].
 */
internal object ObjetosMeso {
    private val C = ColorMeso

    /** Los objetos que este cuadro sabe dibujar: los de su calendario. */
    val TODOS = listOf("pincel", "concha_tinta", "codice", "vaso_cacao", "copal", "orejera", "petate", "tamal", "manta", "cuchillo", "caracol", "amate")

    /** El objeto del día, apoyado en [sobre] (un punto del tablero del banco). [colgado]: el canto del banco, de donde cuelga la manta. */
    fun DrawScope.objeto(m: Momento, sobre: Offset, k: Float, colgado: Offset, tono: (Color) -> Color) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        fun sombra(w: Float) = drawOval(Color.Black.copy(alpha = 0.2f), Offset(sobre.x - w * k / 2f, sobre.y - 0.8f * k), Size(w * k, 1.8f * k))
        fun vaso(x: Float, ancho: Float, alto: Float, color: Color) {
            val z = Rect(p(x - ancho / 2f, -alto), p(x + ancho / 2f, 0f))
            drawRect(Brush.horizontalGradient(listOf(tono(lerp(color, C.TINTA, 0.3f)), tono(lerp(color, Color.White, 0.3f)), tono(color), tono(lerp(color, C.TINTA, 0.4f))), z.left, z.right), z.topLeft, z.size)
            drawOval(tono(lerp(color, C.TINTA, 0.5f)), Offset(z.left, z.top - 0.6f * k), Size(z.width, 1.4f * k))
        }
        when (m.ficha.objeto) {
            "pincel" -> {
                // El pincel bueno, de pelo fino, cruzado sobre su estuche de caña.
                sombra(18f)
                drawRoundRect(tono(lerp(C.PALMA, C.MAIZ, 0.3f)), p(-8f, -2.2f), Size(12f * k, 1.9f * k), CornerRadius(0.9f * k)); for (x in listOf(-5f, -1f, 2.6f)) drawLine(tono(C.MADERA), p(x, -2.2f), p(x, -0.3f), 0.4f)
                drawLine(tono(C.MADERA), p(-6f, -5.6f), p(5f, -3.2f), 1f * k, StrokeCap.Round); drawLine(tono(C.JADE), p(2.6f, -3.74f), p(4.2f, -3.4f), 1.2f * k)
                drawLine(tono(C.TINTA), p(5f, -3.2f), p(8.4f, -2.2f), 0.9f * k, StrokeCap.Round); drawLine(tono(C.TINTA), p(8.4f, -2.2f), p(9.4f, -1.9f), 0.4f * k, StrokeCap.Round)
            }
            "concha_tinta" -> {
                // La concha grande, partida a lo largo, con la tinta negra dentro y una gota fuera.
                sombra(15f)
                drawPath(Path().apply { moveTo(p(-7f, -0.6f).x, p(-7f, -0.6f).y); cubicTo(p(-7f, -6f).x, p(-7f, -6f).y, p(6f, -7f).x, p(6f, -7f).y, p(7.6f, -1f).x, p(7.6f, -1f).y); cubicTo(p(4f, 0.6f).x, p(4f, 0.6f).y, p(-4f, 0.6f).x, p(-4f, 0.6f).y, p(-7f, -0.6f).x, p(-7f, -0.6f).y) }, tono(Color(0xFFF4E8DC)))
                for (i in 0..4) drawLine(tono(Color(0xFFD8B8A8)), p(-5f + i * 2.6f, -0.2f), p(-3.4f + i * 1.8f, -5.4f), 0.4f)
                drawOval(tono(C.TINTA), p(-4.6f, -4f), Size(9.6f * k, 2.8f * k)); drawOval(Color.White.copy(alpha = 0.3f), p(-3f, -3.6f), Size(3f * k, 0.7f * k))
                drawCircle(tono(C.TINTA), 0.6f * k, p(9.6f, -0.4f))
            }
            "codice" -> {
                // Un códice cerrado, entre sus tapas de madera forradas de piel de jaguar.
                sombra(18f)
                drawRect(tono(C.MADERA), p(-8f, -1.2f), Size(16f * k, 1.2f * k)); drawRect(tono(Color(0xFFF2E8D0)), p(-7.6f, -5f), Size(15.2f * k, 3.8f * k))
                for (i in 0..5) drawLine(tono(C.PALMA), p(-7.6f, -4.6f + i * 0.6f), p(7.6f, -4.6f + i * 0.6f), 0.3f)
                drawRect(tono(Color(0xFFD8A450)), p(-8f, -6.4f), Size(16f * k, 1.5f * k))
                drawPoints(listOf(p(-6f, -5.7f), p(-3f, -5.5f), p(0f, -5.8f), p(3f, -5.5f), p(6f, -5.7f), p(-4.6f, -6f), p(1.6f, -6f)), PointMode.Points, tono(C.TINTA), 0.7f * k, StrokeCap.Round)
                drawLine(tono(C.ROJO), p(-2f, -6.6f), p(-2f, 0f), 0.6f * k)
            }
            "vaso_cacao" -> {
                // El vaso de cacao, alto y pintado, con su espuma.
                sombra(10f)
                vaso(0f, 7f, 11f, lerp(C.BARRO, C.MAIZ, 0.3f))
                drawRect(tono(C.ROJO), p(-3.5f, -9.6f), Size(7f * k, 1f * k)); drawRect(tono(C.TINTA).copy(alpha = 0.8f), p(-3.5f, -2f), Size(7f * k, 0.7f * k))
                for (i in 0..2) drawRoundRect(tono(C.TINTA), p(-2.6f + i * 2f, -7.4f), Size(1.4f * k, 1.6f * k), CornerRadius(0.4f * k), style = Stroke(0.4f))
                drawOval(tono(Color(0xFF8A5A3A)), p(-3.2f, -11.5f), Size(6.4f * k, 1.2f * k)); drawPoints(listOf(p(-1.6f, -11.2f), p(0.2f, -11.5f), p(1.8f, -11.1f)), PointMode.Points, tono(Color(0xFFE8D4B8)), 0.9f * k, StrokeCap.Round)
            }
            "copal" -> {
                // Bolas de copal en una hoja, y el incensario de mano.
                sombra(18f)
                drawPath(poligono(p(-9f, -0.4f), p(-1f, -1.6f), p(1f, 0f), p(-7f, 0.6f)), tono(lerp(C.SELVA, C.HOJA_LUZ, 0.3f)))
                for ((x, y) in listOf(-6.4f to -1.4f, -4f to -1.8f, -2f to -1.2f, -4.6f to -3f)) drawCircle(tono(Color(0xFFF2E6C0)), 1.2f * k, p(x, y))
                drawPath(poligono(p(2.4f, -5f), p(8.6f, -5f), p(7.4f, 0f), p(3.6f, 0f)), tono(C.BARRO)); drawOval(tono(lerp(C.BARRO, C.TINTA, 0.5f)), p(2.4f, -5.7f), Size(6.2f * k, 1.4f * k))
                drawPoints(listOf(p(4.4f, -3.4f), p(6.4f, -3.4f), p(5.4f, -1.8f)), PointMode.Points, tono(C.TINTA), 0.8f * k, StrokeCap.Round)
            }
            "orejera" -> {
                // Un par de orejeras de jade, sobre un paño.
                sombra(15f)
                drawPath(poligono(p(-8f, -0.6f), p(6f, -1.6f), p(8f, 0.2f), p(-6f, 0.8f)), tono(Color(0xFFF2EAD6)))
                for (x in listOf(-3.4f, 3f)) { drawCircle(tono(C.JADE), 2.5f * k, p(x, -3f)); drawCircle(tono(lerp(C.JADE, Color.White, 0.4f)), 2.5f * k, p(x, -3f), style = Stroke(0.5f)); drawCircle(tono(lerp(C.JADE, C.TINTA, 0.6f)), 1f * k, p(x, -3f)); drawCircle(Color.White.copy(alpha = 0.5f), 0.4f * k, p(x - 1.2f, -4.2f)) }
            }
            "petate" -> {
                // Un petate nuevo, enrollado y atado, apoyado en el canto del banco.
                val a = colgado + Offset(3f * k, 9f * k); val b = colgado + Offset(-1f * k, -9f * k)
                drawLine(Color.Black.copy(alpha = 0.2f), a + Offset(1.6f * k, 0f), b + Offset(1.6f * k, 1f), 4.4f * k, StrokeCap.Round)
                drawLine(tono(lerp(C.PALMA, C.AMATE, 0.3f)), a, b, 4.4f * k, StrokeCap.Round); drawLine(tono(lerp(C.PALMA, Color.White, 0.3f)), a + Offset(-1.2f * k, 0f), b + Offset(-1.2f * k, 0f), 0.8f * k)
                for (t in listOf(0.25f, 0.7f)) { val c = a + (b - a) * t; drawLine(tono(C.MADERA), c + Offset(-2.3f * k, 0.4f * k), c + Offset(2.3f * k, -0.4f * k), 0.5f * k) }
                drawOval(tono(C.MADERA), Offset(b.x - 2.2f * k, b.y - 1.2f * k), Size(4.4f * k, 2f * k), style = Stroke(0.5f))
            }
            "tamal" -> {
                // Dos tamales en su hoja, uno abierto, en un plato.
                sombra(17f)
                drawOval(tono(C.BARRO), p(-8f, -1.8f), Size(16f * k, 3f * k)); drawOval(tono(lerp(C.BARRO, Color.White, 0.2f)), p(-7f, -1.9f), Size(14f * k, 2.2f * k))
                drawRoundRect(tono(lerp(C.MAIZ, C.SELVA, 0.35f)), p(-6f, -4.4f), Size(6.4f * k, 3f * k), CornerRadius(1.2f * k)); for (i in 0..2) drawLine(tono(C.SELVA), p(-5f + i * 2f, -4.2f), p(-5f + i * 2f, -1.6f), 0.35f)
                drawPath(poligono(p(0.6f, -1.4f), p(2f, -4.6f), p(7.6f, -4.6f), p(6.4f, -1.4f)), tono(lerp(C.MAIZ, C.SELVA, 0.35f))); drawRoundRect(tono(Color(0xFFF2E2A8)), p(2f, -4f), Size(4.4f * k, 2.2f * k), CornerRadius(1f * k))
            }
            "manta" -> {
                // La manta de algodón, doblada sobre el canto del banco, con su cenefa.
                val c = colgado
                val forma = poligono(c + Offset(-5f * k, -1.4f * k), c + Offset(3f * k, -1.4f * k), c + Offset(3.6f * k, 8f * k), c + Offset(-4.4f * k, 8.6f * k))
                drawPath(forma, tono(Color(0xFFF4EEDF))); drawPath(poligono(c + Offset(-5f * k, -1.4f * k), c + Offset(-3f * k, -3f * k), c + Offset(5f * k, -3f * k), c + Offset(3f * k, -1.4f * k)), tono(Color(0xFFFFFAEE)))
                drawLine(tono(C.ROJO), c + Offset(-4.5f * k, 6.6f * k), c + Offset(3.5f * k, 6.1f * k), 1f * k); drawLine(tono(C.JADE), c + Offset(-4.6f * k, 5f * k), c + Offset(3.4f * k, 4.5f * k), 0.5f * k)
                for (i in 0..5) drawLine(tono(Color(0xFFF4EEDF)), c + Offset((-4.2f + i * 1.5f) * k, 8.5f * k), c + Offset((-4.2f + i * 1.5f) * k, 10f * k), 0.4f)
                drawLine(Color.Black.copy(alpha = 0.14f), c + Offset(-1f * k, -1f * k), c + Offset(-0.6f * k, 8f * k), 0.5f)
            }
            "cuchillo" -> {
                // Un cuchillo de obsidiana, con su mango de madera atado, y unas lascas.
                sombra(18f)
                drawPath(poligono(p(-1f, -1f), p(2f, -3f), p(9.6f, -1.6f), p(2.6f, -0.4f)), tono(Color(0xFF2A2C34))); drawLine(tono(Color(0xFFB8C2D0)), p(1.6f, -2.5f), p(8.6f, -1.7f), 0.4f); drawLine(tono(Color(0xFF6A7080)), p(0f, -1.3f), p(6f, -1.5f), 0.3f)
                drawRoundRect(tono(C.MADERA), p(-8.6f, -2.5f), Size(8f * k, 1.8f * k), CornerRadius(0.8f * k)); for (x in listOf(-2.6f, -1.8f, -1f)) drawLine(tono(C.PALMA), p(x, -2.6f), p(x, -0.6f), 0.5f)
                drawPoints(listOf(p(-5f, -0.2f), p(5f, 0.2f), p(7f, -0.1f)), PointMode.Points, tono(Color(0xFF2A2C34)), 0.9f * k, StrokeCap.Square)
            }
            "caracol" -> {
                // Un caracol grande de mar, de los que suenan como trompeta.
                sombra(16f)
                drawPath(Path().apply { moveTo(p(-8f, -1f).x, p(-8f, -1f).y); cubicTo(p(-6f, -7f).x, p(-6f, -7f).y, p(3f, -9f).x, p(3f, -9f).y, p(7f, -4f).x, p(7f, -4f).y); cubicTo(p(9f, -1f).x, p(9f, -1f).y, p(4f, 0.4f).x, p(4f, 0.4f).y, p(-8f, -1f).x, p(-8f, -1f).y) }, tono(Color(0xFFF2DCC8)))
                for (i in 0..3) drawLine(tono(Color(0xFFC89A84)), p(-5f + i * 3f, -1f), p(-3f + i * 2.4f, -6.4f - i * 0.3f), 0.45f)
                drawOval(tono(Color(0xFFE8A898)), p(2f, -3.6f), Size(4.6f * k, 2.6f * k)); for (i in 0..2) drawPath(poligono(p(-2f + i * 2.6f, -7.4f - i * 0.4f), p(-1.2f + i * 2.6f, -9.4f - i * 0.4f), p(-0.2f + i * 2.6f, -7.6f - i * 0.4f)), tono(Color(0xFFF2DCC8)))
            }
            "amate" -> {
                // Un rollo de papel de amate sin estucar, a medio desenrollar, y la piedra de alisarlo.
                sombra(18f)
                drawPath(poligono(p(-4f, -0.6f), p(8f, -1.8f), p(9.6f, 0f), p(-2.4f, 0.8f)), tono(C.AMATE)); for (i in 0..3) drawLine(tono(lerp(C.AMATE, C.MADERA, 0.4f)), p(-2f + i * 3f, -0.5f + i * -0.3f), p(-0.6f + i * 3f, 0.5f - i * 0.2f), 0.3f)
                drawRoundRect(Brush.verticalGradient(listOf(tono(lerp(C.AMATE, Color.White, 0.3f)), tono(C.AMATE), tono(lerp(C.AMATE, C.MADERA, 0.4f))), p(0f, -4.4f).y, p(0f, -0.4f).y), p(-8.6f, -4.4f), Size(5.4f * k, 4.2f * k), CornerRadius(2f * k))
                drawOval(tono(lerp(C.AMATE, C.MADERA, 0.5f)), p(-8.4f, -3.9f), Size(2f * k, 3.2f * k), style = Stroke(0.4f))
                drawOval(tono(C.PIEDRA_OSC), p(4f, -3.6f), Size(4.6f * k, 2.2f * k)); for (i in 0..2) drawLine(tono(C.TINTA), p(4.8f + i * 1.2f, -3.2f), p(4.8f + i * 1.2f, -1.9f), 0.3f)
            }
            else -> Unit
        }
    }
}
