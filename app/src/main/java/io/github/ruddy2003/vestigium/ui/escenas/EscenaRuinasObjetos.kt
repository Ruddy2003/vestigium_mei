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
 * Los doce objetos del día de la recolectora, sobre la mesa de las semillas (la manta y la cuerda, colgadas de ella).
 * Cada uno recibe [tono], que da el color de una cosa con la luz de ese sitio, y la escala [k].
 */
internal object ObjetosRuinas {
    private val C = ColorRuinas

    /** Los objetos que este cuadro sabe dibujar: los de su calendario. */
    val TODOS = listOf("lata", "lapiz", "mochila", "cuchillo", "tarro", "mapa", "libro", "botas", "cuerda", "silbato", "manta", "semilla")

    /** El objeto del día, apoyado en [sobre] (un punto del tablero de la mesa). [colgado]: de dónde cuelgan la manta y la cuerda. */
    fun DrawScope.objeto(m: Momento, sobre: Offset, k: Float, colgado: Offset, tono: (Color) -> Color) {
        fun p(dx: Float, dy: Float) = Offset(sobre.x + dx * k, sobre.y + dy * k)
        fun sombra(w: Float) = drawOval(Color.Black.copy(alpha = 0.2f), Offset(sobre.x - w * k / 2f, sobre.y - 0.8f * k), Size(w * k, 1.8f * k))
        when (m.ficha.objeto) {
            "lata" -> {
                // Una lata de semillas abierta, con la tapa apoyada y unas semillas fuera.
                sombra(15f)
                val cuerpo = Rect(p(-5f, -9f), p(3f, 0f))
                drawRect(Brush.horizontalGradient(listOf(tono(Color(0xFF8E9696)), tono(Color(0xFFD8DCDA)), tono(C.LATA), tono(Color(0xFF7E8686))), cuerpo.left, cuerpo.right), cuerpo.topLeft, cuerpo.size)
                drawOval(tono(Color(0xFF3E3A34)), p(-5f, -9.8f), Size(8f * k, 1.8f * k)); drawOval(tono(C.LATA), p(-5f, -9.8f), Size(8f * k, 1.8f * k), style = Stroke(0.5f))
                drawRect(tono(Color(0xFFEDE4CC)), p(-4f, -6.6f), Size(6f * k, 4f * k)); for (j in 0..2) drawLine(tono(Color(0xFF6A665C)), p(-3.2f, -5.6f + j * 1.2f), p(1.2f - j * 0.8f, -5.6f + j * 1.2f), 0.4f)
                drawOval(tono(Color(0xFFC8CCCA)), p(3.6f, -1.6f), Size(6.4f * k, 1.6f * k)); drawOval(tono(Color(0xFF8E9696)), p(3.6f, -1.6f), Size(6.4f * k, 1.6f * k), style = Stroke(0.4f))
                val s = listOf(p(-7.4f, -0.5f), p(-8.6f, -0.3f), p(-6.6f, -0.2f), p(10.6f, -0.4f)); drawPoints(s, PointMode.Points, tono(Color(0xFF5E4026)), 1.3f * k, StrokeCap.Round)
            }
            "lapiz" -> {
                // El cabo de lápiz, sobre un papel con rayas (sin letras) y unas etiquetas cortadas.
                sombra(18f)
                drawPath(poligono(p(-9f, -0.4f), p(4f, -1.4f), p(5.6f, 0.2f), p(-7.6f, 0.6f)), tono(Color(0xFFEDE4CC)))
                for (j in 0..2) drawLine(tono(Color(0xFF8A8676)), p(-7f + j * 0.6f, -0.3f + j * 0.34f), p(1.6f + j * 0.6f, -0.9f + j * 0.34f), 0.35f)
                drawLine(tono(Color(0xFFD8A83A)), p(-3f, -2.2f), p(7.6f, -3.6f), 1.5f * k, StrokeCap.Butt); drawLine(tono(Color(0xFFE8D0A0)), p(7.6f, -3.6f), p(9.4f, -3.84f), 1.3f * k); drawLine(tono(Color(0xFF3A342C)), p(9.4f, -3.84f), p(10.2f, -3.94f), 0.6f * k)
                drawLine(tono(C.TELA), p(-3f, -2.2f), p(-4.2f, -2.04f), 1.5f * k)
            }
            "mochila" -> { sombra(17f); mochila(p(0f, -17f), k * 0.95f, tono) }
            "cuchillo" -> {
                // El cuchillo, sobre una tabla, con una vaina abierta al lado.
                sombra(19f)
                drawPath(poligono(p(-9f, -1.4f), p(7f, -1.8f), p(9f, 0f), p(-8f, 0.4f)), tono(lerp(C.MADERA, Color(0xFFD8C8A0), 0.3f)))
                drawPath(poligono(p(-1f, -2.6f), p(8.6f, -3.2f), p(6f, -1.9f), p(-1f, -1.8f)), tono(Color(0xFFD0D6D8))); drawLine(tono(Color.White), p(0f, -2.5f), p(7.6f, -3f), 0.4f)
                drawRoundRect(tono(Color(0xFF6A4630)), p(-7.4f, -3f), Size(6.6f * k, 1.4f * k), CornerRadius(0.6f * k)); for (x in listOf(-6f, -3.4f)) drawCircle(tono(Color(0xFFD8B050)), 0.35f * k, p(x, -2.3f))
                drawPath(Path().apply { moveTo(p(-8f, -0.2f).x, p(-8f, -0.2f).y); cubicTo(p(-6f, -1.6f).x, p(-6f, -1.6f).y, p(-3f, -1.6f).x, p(-3f, -1.6f).y, p(-1.6f, -0.2f).x, p(-1.6f, -0.2f).y) }, tono(Color(0xFF8E9A4A)), style = Stroke(1.1f * k, cap = StrokeCap.Round))
                drawPoints(listOf(p(-6.4f, -0.5f), p(-4.8f, -0.7f), p(-3.2f, -0.5f)), PointMode.Points, tono(Color(0xFF5E4026)), 1f * k, StrokeCap.Round)
            }
            "tarro" -> {
                // Un tarro de cristal con semillas, tapado con una tela atada.
                sombra(11f)
                val forma = Path().apply { moveTo(p(-4f, 0f).x, p(-4f, 0f).y); lineTo(p(-4.4f, -8f).x, p(-4.4f, -8f).y); cubicTo(p(-4.4f, -10f).x, p(-4.4f, -10f).y, p(-2.6f, -10.4f).x, p(-2.6f, -10.4f).y, p(-2.6f, -11f).x, p(-2.6f, -11f).y); lineTo(p(2.6f, -11f).x, p(2.6f, -11f).y); cubicTo(p(2.6f, -10.4f).x, p(2.6f, -10.4f).y, p(4.4f, -10f).x, p(4.4f, -10f).y, p(4.4f, -8f).x, p(4.4f, -8f).y); lineTo(p(4f, 0f).x, p(4f, 0f).y); close() }
                drawPath(forma, tono(Color(0xFFB8D0CC)).copy(alpha = 0.7f)); drawRect(tono(Color(0xFFC8A040)), p(-3.8f, -6f), Size(7.6f * k, 5.8f * k))
                drawPoints(listOf(p(-2.4f, -5f), p(0f, -4f), p(2f, -5.2f), p(-1f, -2.4f), p(2.4f, -2f), p(-2.8f, -1.4f)), PointMode.Points, tono(Color(0xFF8A6A2A)), 1f * k, StrokeCap.Round)
                drawLine(Color.White.copy(alpha = 0.6f), p(-3f, -9f), p(-3f, -2f), 0.7f); drawPath(forma, tono(Color(0xFF7E9A96)), style = Stroke(0.5f))
                drawPath(poligono(p(-3.6f, -10.6f), p(3.6f, -10.6f), p(4.4f, -12.4f), p(-4.4f, -12.4f)), tono(lerp(C.TELA, C.PAJA, 0.3f))); drawLine(tono(Color(0xFFD8C8A0)), p(-3f, -10.7f), p(3f, -10.7f), 0.5f)
            }
            "mapa" -> {
                // Un mapa dibujado a mano: el río, las calles y unas cruces; sujeto con una piedra.
                sombra(22f)
                val hoja = poligono(p(-10f, -1f), p(8f, -3.6f), p(11f, -0.4f), p(-7f, 1f))
                drawPath(hoja, tono(Color(0xFFE8DCB8))); drawPath(hoja, tono(Color(0xFFB8A880)), style = Stroke(0.4f))
                drawPath(Path().apply { moveTo(p(-8f, 0f).x, p(-8f, 0f).y); cubicTo(p(-3f, -2.6f).x, p(-3f, -2.6f).y, p(1f, 0f).x, p(1f, 0f).y, p(8f, -2.4f).x, p(8f, -2.4f).y) }, tono(C.RIO), style = Stroke(0.8f))
                for ((a, b) in listOf(p(-5f, -0.9f) to p(-2f, 0.4f), p(1f, -2.2f) to p(3.6f, -0.6f), p(4.6f, -2.8f) to p(6f, -0.8f))) drawLine(tono(Color(0xFF6A5A48)), a, b, 0.35f)
                for (c in listOf(p(-1f, -1.6f), p(5.4f, -1.6f))) { drawLine(tono(C.TELA), c + Offset(-0.9f, -0.5f), c + Offset(0.9f, 0.5f), 0.5f); drawLine(tono(C.TELA), c + Offset(-0.9f, 0.5f), c + Offset(0.9f, -0.5f), 0.5f) }
                drawOval(tono(Color(0xFF8A8676)), p(-9.6f, -2.6f), Size(4.4f * k, 2.6f * k))
            }
            "libro" -> {
                // El libro de plantas de la biblioteca vieja, abierto: en cada página, un dibujo.
                sombra(22f)
                drawPath(poligono(p(-10f, -1.4f), p(0f, -0.6f), p(10f, -1.4f), p(10.6f, 0.4f), p(-10.6f, 0.4f)), tono(Color(0xFF5E3A2E)))
                drawPath(poligono(p(-9.4f, -3.4f), p(0f, -2f), p(0f, -0.4f), p(-9.8f, -1f)), tono(Color(0xFFEDE4CC))); drawPath(poligono(p(9.4f, -3.4f), p(0f, -2f), p(0f, -0.4f), p(9.8f, -1f)), tono(Color(0xFFE0D6BC)))
                drawLine(tono(Color(0xFF8A8676)), p(0f, -2f), p(0f, -0.4f), 0.5f)
                drawLine(tono(Color(0xFF4E8A40)), p(-5f, -1.2f), p(-5.4f, -2.8f), 0.5f); drawOval(tono(Color(0xFF4E8A40)), p(-6.8f, -2.6f), Size(1.6f * k, 0.8f * k)); drawOval(tono(Color(0xFF4E8A40)), p(-5f, -2.2f), Size(1.6f * k, 0.8f * k))
                drawCircle(tono(C.TELA), 0.7f * k, p(5f, -2f)); drawLine(tono(Color(0xFF4E8A40)), p(5f, -1.4f), p(4.6f, -0.9f), 0.4f); for (j in 0..1) drawLine(tono(Color(0xFF8A8676)), p(2f, -1.2f + j * 0.4f), p(8f, -1.6f + j * 0.4f), 0.3f)
            }
            "botas" -> {
                // Las botas, con el barro seco, una caída sobre la otra.
                sombra(18f)
                for ((dx, alto, tumba) in listOf(Triple(-5f, 11f, 0f), Triple(3.4f, 10f, 1.4f))) {
                    val forma = Path().apply { moveTo(p(dx - 2.4f, 0f).x, p(dx - 2.4f, 0f).y); lineTo(p(dx - 2.4f + tumba, -alto).x, p(dx - 2.4f + tumba, -alto).y); lineTo(p(dx + 2.4f + tumba, -alto).x, p(dx + 2.4f + tumba, -alto).y); lineTo(p(dx + 2.6f, -3.6f).x, p(dx + 2.6f, -3.6f).y); cubicTo(p(dx + 6f, -3.4f).x, p(dx + 6f, -3.4f).y, p(dx + 7f, -1.6f).x, p(dx + 7f, -1.6f).y, p(dx + 6.6f, 0f).x, p(dx + 6.6f, 0f).y); close() }
                    drawPath(forma, Brush.horizontalGradient(listOf(tono(Color(0xFF5E4630)), tono(Color(0xFF8A6A48)), tono(Color(0xFF5E4630))), p(dx - 2.4f, 0f).x, p(dx + 6.6f, 0f).x))
                    drawLine(tono(Color(0xFF3A2E24)), p(dx - 2.4f, -0.4f), p(dx + 6.6f, -0.4f), 0.9f * k); for (j in 0..2) drawLine(tono(Color(0xFFD8C8A0)), p(dx + 1f + tumba * (0.5f + j * 0.16f), -5f - j * 1.8f), p(dx + 2.6f + tumba * (0.5f + j * 0.16f), -5.4f - j * 1.8f), 0.4f)
                    drawOval(tono(Color(0xFF8E8068)).copy(alpha = 0.7f), p(dx + 1f, -2.4f), Size(4f * k, 1.6f * k))
                }
            }
            "cuerda" -> {
                // Un rollo de cuerda colgado del canto de la mesa.
                val c = colgado + Offset(0f, 8f * k)
                drawLine(tono(Color(0xFF6A5A48)), colgado + Offset(0f, -1f), c + Offset(0f, -6f * k), 0.8f)
                for (i in 0..3) drawOval(tono(if (i % 2 == 0) Color(0xFFC8A870) else Color(0xFFA88A54)), Offset(c.x - (5f - i * 0.5f) * k, c.y - (6f - i * 0.4f) * k), Size((10f - i) * k, (12f - i * 0.8f) * k), style = Stroke(1.2f * k))
                drawLine(tono(Color(0xFFA88A54)), c + Offset(-1.6f * k, -6.4f * k), c + Offset(1.6f * k, -5.6f * k), 2f * k, StrokeCap.Round)
                drawLine(tono(Color(0xFFC8A870)), c + Offset(3f * k, 4f * k), c + Offset(4.4f * k, 9f * k), 1f * k, StrokeCap.Round)
            }
            "silbato" -> {
                // El silbato de hueso, con su cordón, sobre un trapo.
                sombra(16f)
                drawPath(poligono(p(-8f, -0.6f), p(6f, -1.4f), p(8f, 0.4f), p(-6f, 0.8f)), tono(lerp(C.TELA, C.PAJA, 0.5f)))
                drawOval(tono(Color(0xFF6A5A48)), p(-7f, -3.6f), Size(8f * k, 3.4f * k), style = Stroke(0.6f))
                drawRoundRect(Brush.verticalGradient(listOf(tono(Color(0xFFF6EED8)), tono(Color(0xFFC8BC9C))), p(0f, -3.4f).y, p(0f, -1f).y), p(-0.6f, -3.4f), Size(8.6f * k, 2.4f * k), CornerRadius(1.1f * k))
                drawRect(tono(Color(0xFF3A342C)), p(4.6f, -3.4f), Size(1.2f * k, 1f * k)); drawCircle(tono(Color(0xFF6A5A48)), 0.5f * k, p(0.4f, -2.2f))
            }
            "manta" -> {
                // La manta, doblada sobre el canto de la mesa: a rayas, con su fleco.
                val x0 = colgado.x - 9f * k; val x1 = colgado.x + 6f * k; val y0 = colgado.y - 4.6f * k; val y1 = colgado.y + 11f * k
                val forma = Path().apply { moveTo(x0, y0 + 2f); lineTo(x1, y0); lineTo(x1 + 1f, y1 - 1f); lineTo(x0 + 1f, y1); close() }
                drawPath(forma, Brush.horizontalGradient(listOf(tono(Color(0xFF8A3E2C)), tono(C.TELA), tono(Color(0xFF9A4630))), x0, x1))
                for (j in 1..4) { val y = y0 + (y1 - y0) * j / 5f; drawLine(tono(if (j % 2 == 0) C.PAJA else Color(0xFF4A5A4A)), Offset(x0 + 0.6f, y + 1f), Offset(x1 + 0.6f, y - 0.6f), 1.1f * k) }
                var x = x0 + 1.4f; while (x < x1 + 1f) { drawLine(tono(C.PAJA), Offset(x, y1 - 0.6f), Offset(x + 0.3f, y1 + 2f * k), 0.5f); x += 1.5f * k }
                drawLine(Color.Black.copy(alpha = 0.2f), Offset(x0, y0 + 2.6f), Offset(x1, y0 + 0.6f), 0.8f)
            }
            "semilla" -> {
                // Un puñado de semillas sobre un papel, y una que ya brotó, en un cacharro.
                sombra(20f)
                drawPath(poligono(p(-10f, -0.6f), p(0f, -1.6f), p(2f, 0.4f), p(-8f, 0.8f)), tono(Color(0xFFEDE4CC)))
                val s = ArrayList<Offset>(); for (i in 0..11) s += p(-8f + (i % 6) * 1.5f + (i / 6) * 0.6f, -0.9f + (i / 6) * 0.6f - (i % 3) * 0.16f)
                drawPoints(s, PointMode.Points, tono(Color(0xFF6A3E26)), 1.5f * k, StrokeCap.Round); drawPoints(s.filterIndexed { i, _ -> i % 4 == 0 }, PointMode.Points, tono(Color(0xFFD8C8A0)), 1.3f * k, StrokeCap.Round)
                drawPath(poligono(p(4.4f, 0f), p(3.6f, -5f), p(9.4f, -5f), p(8.6f, 0f)), tono(C.LADRILLO)); drawOval(tono(C.TIERRA), p(3.6f, -5.6f), Size(5.8f * k, 1.4f * k))
                drawLine(tono(Color(0xFF7EAE4E)), p(6.5f, -5f), p(6.5f, -9f), 0.7f * k, StrokeCap.Round); drawOval(tono(Color(0xFF8EC05A)), p(3.8f, -10.4f), Size(2.8f * k, 1.4f * k)); drawOval(tono(Color(0xFF7EAE4E)), p(6.4f, -10.8f), Size(2.8f * k, 1.4f * k))
            }
            else -> Unit
        }
    }

    /** La mochila de la recolectora, colgada de [clavo]: de lona, con un remiendo, la manta enrollada encima y un cacharro atado. */
    fun DrawScope.mochila(clavo: Offset, k: Float, tono: (Color) -> Color) {
        fun p(dx: Float, dy: Float) = Offset(clavo.x + dx * k, clavo.y + dy * k)
        val lona = Color(0xFF8A8A56)
        // Las correas, que suben al clavo.
        drawPath(Path().apply { moveTo(p(-4f, 6f).x, p(-4f, 6f).y); cubicTo(p(-3f, 1f).x, p(-3f, 1f).y, p(-0.6f, 0f).x, p(-0.6f, 0f).y, p(0f, 0f).x, p(0f, 0f).y); cubicTo(p(0.6f, 0f).x, p(0.6f, 0f).y, p(3f, 1f).x, p(3f, 1f).y, p(4f, 6f).x, p(4f, 6f).y) }, tono(Color(0xFF5E4A38)), style = Stroke(1.1f * k, cap = StrokeCap.Round))
        val cuerpo = Path().apply { moveTo(p(-7f, 17f).x, p(-7f, 17f).y); cubicTo(p(-8.6f, 12f).x, p(-8.6f, 12f).y, p(-7.6f, 6f).x, p(-7.6f, 6f).y, p(-5f, 5f).x, p(-5f, 5f).y); lineTo(p(5f, 5f).x, p(5f, 5f).y); cubicTo(p(7.6f, 6f).x, p(7.6f, 6f).y, p(8.6f, 12f).x, p(8.6f, 12f).y, p(7f, 17f).x, p(7f, 17f).y); close() }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(tono(lerp(lona, Color(0xFF3E3E2A), 0.4f)), tono(lona), tono(lerp(lona, Color(0xFFE0D8A8), 0.25f)), tono(lerp(lona, Color(0xFF3E3E2A), 0.3f))), p(-8f, 0f).x, p(8f, 0f).x))
        // La manta enrollada, la tapa con su hebilla, el bolsillo y el remiendo.
        drawRoundRect(tono(C.TELA), p(-7f, 2.6f), Size(14f * k, 3.4f * k), CornerRadius(1.7f * k)); for (x in listOf(-4f, 4f)) drawLine(tono(Color(0xFF5E4A38)), p(x, 2.4f), p(x, 6.2f), 0.7f * k)
        drawPath(Path().apply { moveTo(p(-6.4f, 6f).x, p(-6.4f, 6f).y); lineTo(p(6.4f, 6f).x, p(6.4f, 6f).y); lineTo(p(5.6f, 10.4f).x, p(5.6f, 10.4f).y); cubicTo(p(2f, 12f).x, p(2f, 12f).y, p(-2f, 12f).x, p(-2f, 12f).y, p(-5.6f, 10.4f).x, p(-5.6f, 10.4f).y); close() }, tono(lerp(lona, Color(0xFF4A4A30), 0.3f)))
        drawRect(tono(Color(0xFFB8A050)), p(-0.8f, 10.4f), Size(1.6f * k, 1.8f * k))
        drawRoundRect(tono(lerp(lona, Color(0xFF4A4A30), 0.2f)), p(-4.4f, 12.6f), Size(8.8f * k, 3.6f * k), CornerRadius(0.8f * k)); drawRect(tono(lerp(C.TELA, C.PAJA, 0.4f)), p(3.4f, 7f), Size(2.6f * k, 2.4f * k))
        // El cacharro de hojalata, atado a un lado.
        drawRoundRect(tono(C.LATA), p(7.4f, 10f), Size(3.2f * k, 4f * k), CornerRadius(0.6f * k)); drawLine(tono(Color(0xFF5E4A38)), p(7.4f, 8f), p(9f, 10f), 0.5f)
    }
}
