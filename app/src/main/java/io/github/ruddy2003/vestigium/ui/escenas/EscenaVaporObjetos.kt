package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor

/** Los doce objetos del día del fogonero, sobre el arca de las herramientas (la pala, apoyada en ella). */
internal object ObjetosVapor {
    private val C = ColorVapor

    /** El objeto de hoy, con el pie en [sobre] (la tapa del arca) y agrandado [k] veces. [arca]: el frente del arca, donde se apoya la pala. */
    fun DrawScope.objetoDelDia(m: Momento, p: PincelVapor, sobre: Offset, k: Float, arca: Rect) {
        fun c(propio: Color, mira: Float = 0f): Color = p.cara(sobre, propio, mira)
        fun luz(propio: Color): Color = p.arriba(sobre, propio)
        fun q(dx: Float, dy: Float) = Offset(sobre.x + dx, sobre.y + dy)
        if (m.ficha.objeto == "pala") {
            // La pala del fogonero, de mango corto, apoyada en el arca: la hoja abajo, gastada y brillante por el filo.
            val a = Offset(arca.left + 22f, arca.bottom + 7f); val b = Offset(arca.left + 40f, arca.top - 26f)
            drawLine(c(C.MADERA), a + (b - a) * 0.3f, b, 2f, StrokeCap.Round); drawLine(luz(lerp(C.MADERA, C.HUESO, 0.4f)), a + (b - a) * 0.32f + Offset(-0.5f, 0f), b + Offset(-0.5f, 0f), 0.5f)
            drawPath(poligono(listOf(a + Offset(-7f, -2f), a + Offset(6f, 1f), a + Offset(9f, -13f), a + Offset(-2f, -15f))), c(lerp(C.HIERRO, C.HUESO, 0.25f)))
            drawLine(luz(Color.White), a + Offset(-7f, -2f), a + Offset(6f, 1f), 0.8f)
            drawPath(Path().apply { moveTo(b.x - 4f, b.y + 1f); lineTo(b.x + 4f, b.y - 1f) }, c(C.MADERA), style = Stroke(2.2f, cap = StrokeCap.Round))
            return
        }
        scale(k, k, sobre) {
            when (m.ficha.objeto) {
                "carbon" -> {
                    // Tres trozos de carbón del bueno, con sus caras que brillan.
                    for ((x, s) in listOf(-8f to 5f, 0f to 6.4f, 8f to 4.4f)) {
                        drawPath(poligono(listOf(q(x - s, 0f), q(x - s * 0.7f, -s * 0.9f), q(x + s * 0.2f, -s * 1.2f), q(x + s, -s * 0.5f), q(x + s * 0.8f, 0f))), c(C.HOLLIN))
                        drawPath(poligono(q(x - s * 0.7f, -s * 0.9f), q(x + s * 0.2f, -s * 1.2f), q(x, -s * 0.4f)), luz(lerp(C.HOLLIN, C.PIZARRA, 0.8f)))
                        drawLine(luz(Color.White).copy(alpha = 0.7f), q(x - s * 0.5f, -s * 0.86f), q(x + s * 0.1f, -s * 1.1f), 0.4f)
                    }
                }
                "lata" -> {
                    // La lata del té, con su tapa que hace de taza y su asa de alambre.
                    drawRoundRect(c(lerp(C.HIERRO, C.HUESO, 0.55f)), q(-5f, -12f), Size(10f, 12f), CornerRadius(1f)); drawRect(c(lerp(C.HIERRO, C.HUESO, 0.3f), -1f), q(-5f, -12f), Size(2.4f, 12f))
                    drawRect(luz(C.HUESO), q(-5.6f, -13.6f), Size(11.2f, 2.2f)); drawArc(c(C.HIERRO), 180f, 180f, false, q(-5f, -20f), Size(10f, 13f), style = Stroke(0.6f))
                    for (y in listOf(-8f, -4f)) drawLine(c(C.HIERRO).copy(alpha = 0.5f), q(-5f, y), q(5f, y), 0.4f)
                    drawRoundRect(c(lerp(C.HIERRO, C.HUESO, 0.55f)), q(8f, -5f), Size(6f, 5f), CornerRadius(0.8f)); drawOval(c(lerp(C.MADERA, C.HOLLIN, 0.3f)), q(8.6f, -5.6f), Size(4.8f, 1.6f))
                }
                "manometro" -> {
                    // El manómetro de la caldera: la caja de latón, la esfera blanca con su raya roja y la aguja.
                    drawRect(c(C.COBRE), q(-1.6f, -3f), Size(3.2f, 3f)); drawCircle(c(C.LATON), 8.4f, q(0f, -11f)); drawCircle(luz(lerp(C.LATON, Color.White, 0.5f)), 8.4f, q(0f, -11f), style = Stroke(0.7f))
                    drawCircle(c(C.HUESO), 6.6f, q(0f, -11f)); drawArc(c(C.ROJO), 300f, 50f, false, q(-5.4f, -16.4f), Size(10.8f, 10.8f), style = Stroke(1f))
                    for (i in 0..8) { val a = 2.6f + i * 0.52f; drawLine(c(C.HOLLIN), q(kotlin.math.cos(a) * 4.6f, -11f + kotlin.math.sin(a) * 4.6f), q(kotlin.math.cos(a) * 5.8f, -11f + kotlin.math.sin(a) * 5.8f), 0.4f) }
                    drawLine(c(C.HOLLIN), q(0f, -11f), q(3.4f, -14.6f), 0.7f, StrokeCap.Round); drawCircle(c(C.HOLLIN), 0.9f, q(0f, -11f))
                }
                "silbato" -> {
                    // El silbato de la máquina, desmontado: la campana de latón, su asiento y la palanca.
                    drawRect(c(C.COBRE), q(-3f, -3f), Size(6f, 3f)); drawRect(c(C.LATON), q(-1.4f, -7f), Size(2.8f, 4f))
                    drawRoundRect(c(C.LATON), q(-3.6f, -17f), Size(7.2f, 9f), CornerRadius(3.4f, 3.4f)); drawRect(luz(lerp(C.LATON, Color.White, 0.6f)), q(0.6f, -15.6f), Size(1.2f, 6.4f))
                    drawRect(c(lerp(C.LATON, C.HOLLIN, 0.3f), -1f), q(-3.6f, -14f), Size(1.6f, 6f)); drawCircle(c(C.LATON), 1.1f, q(0f, -17.8f))
                    drawLine(c(C.HIERRO), q(-2f, -5f), q(-10f, -8f), 0.9f, StrokeCap.Round); drawCircle(c(C.HIERRO), 1f, q(-10f, -8f))
                }
                "farol" -> {
                    // El farol de mano: de hojalata negra, con su asa y su lente; de noche, encendido.
                    val enc = p.l.noche > 0.2f
                    drawRoundRect(c(lerp(C.HOLLIN, C.HIERRO, 0.4f)), q(-5f, -13f), Size(10f, 13f), CornerRadius(1f)); drawPath(poligono(q(-4f, -13f), q(0f, -17f), q(4f, -13f)), c(lerp(C.HOLLIN, C.HIERRO, 0.4f)))
                    drawRect(c(C.HIERRO), q(-1.4f, -19f), Size(2.8f, 2.4f)); drawArc(c(C.HIERRO), 180f, 180f, false, q(-5.4f, -24f), Size(10.8f, 10f), style = Stroke(0.7f))
                    drawCircle(if (enc) Color(0xFFFFF0C0) else c(lerp(C.HUESO, C.PIZARRA, 0.5f)), 3.4f, q(0f, -6.6f)); drawCircle(c(C.LATON), 3.4f, q(0f, -6.6f), style = Stroke(0.7f))
                    if (enc) resplandor(q(0f, -6.6f), 16f, 0.8f * p.l.noche, C.GAS)
                }
                "gorra" -> {
                    // Su gorra de visera, de pana, con el brillo de la grasa.
                    drawPath(Path().apply { moveTo(q(-9f, -1f).x, q(0f, -1f).y); cubicTo(q(-10f, -9f).x, q(0f, -9f).y, q(6f, -10f).x, q(0f, -10f).y, q(8f, -2.4f).x, q(0f, -2.4f).y); close() }, c(C.AZUL_PANA))
                    drawPath(poligono(q(4f, -3f), q(13f, -1.6f), q(12.4f, 0f), q(2f, 0f)), c(lerp(C.HOLLIN, C.AZUL_PANA, 0.3f), 1f)); drawRect(c(lerp(C.AZUL_PANA, C.HOLLIN, 0.5f)), q(-9f, -2f), Size(17f, 2f))
                    drawPath(Path().apply { moveTo(q(-7f, -5f).x, q(0f, -5f).y); cubicTo(q(-5f, -8.4f).x, q(0f, -8.4f).y, q(1f, -9f).x, q(0f, -9f).y, q(4f, -7f).x, q(0f, -7f).y) }, luz(lerp(C.AZUL_PANA, C.HUESO, 0.4f)), style = Stroke(0.6f, cap = StrokeCap.Round))
                    drawCircle(c(C.LATON), 0.7f, q(-0.6f, -9f))
                }
                "reloj" -> {
                    // El reloj de bolsillo, con su tapa abierta y su leontina.
                    drawPath(Path().apply { moveTo(q(-5f, -11f).x, q(0f, -11f).y); cubicTo(q(-12f, -8f).x, q(0f, -8f).y, q(-12f, -1f).x, q(0f, -1f).y, q(-16f, 0f).x, q(0f, 0f).y) }, c(C.LATON), style = Stroke(0.7f))
                    drawCircle(c(lerp(C.LATON, C.HOLLIN, 0.2f)), 6f, q(7.4f, -6f)); drawCircle(c(C.LATON), 6.6f, q(0f, -7f)); drawCircle(c(C.HUESO), 5.2f, q(0f, -7f))
                    for (i in 0..11) { val a = i * 0.5236f; drawCircle(c(C.HOLLIN), 0.3f, q(kotlin.math.cos(a) * 4.2f, -7f + kotlin.math.sin(a) * 4.2f)) }
                    drawLine(c(C.HOLLIN), q(0f, -7f), q(0f, -10.6f), 0.5f); drawLine(c(C.HOLLIN), q(0f, -7f), q(2.6f, -5.6f), 0.6f); drawRect(c(C.LATON), q(-1f, -15.4f), Size(2f, 2.4f)); drawCircle(c(C.LATON), 1.4f, q(0f, -16.4f), style = Stroke(0.6f))
                }
                "estopa" -> {
                    // Un puñado de estopa manchada de aceite y la aceitera de pico.
                    for ((x, y, w) in listOf(Triple(-6f, -3f, 6f), Triple(-1f, -5f, 5.4f), Triple(-8f, -6f, 4f), Triple(1f, -2f, 5f))) mancha(q(x, y), w, w * 0.6f, c(C.HUESO), 1f)
                    for (i in 0..7) drawLine(c(lerp(C.HUESO, C.HIERRO, 0.3f)), q(-11f + i * 2f, -1f - (i % 3) * 2f), q(-9f + i * 2f, -7f + (i % 2) * 2f), 0.3f)
                    mancha(q(-4f, -3f), 3f, 1.6f, c(lerp(C.HOLLIN, C.COBRE, 0.3f)), 0.6f)
                    drawPath(poligono(q(6f, 0f), q(7f, -7f), q(12f, -7f), q(13f, 0f)), c(C.COBRE)); drawLine(c(C.COBRE), q(11f, -6f), q(17f, -12f), 0.8f, StrokeCap.Round); drawRect(luz(lerp(C.COBRE, Color.White, 0.4f)), q(9.6f, -6f), Size(1f, 5f))
                }
                "horario" -> {
                    // El horario de servicio: un cuadernillo de columnas (sin letras) y un lápiz.
                    val hoja = poligono(q(-11f, 0f), q(-9f, -9f), q(9f, -9f), q(11f, 0f))
                    drawPath(hoja, c(C.HUESO)); drawLine(c(C.MADERA), q(0f, -9f), q(0f, 0f), 0.5f); drawPath(poligono(q(-11f, 0f), q(11f, 0f), q(11f, 1f), q(-11f, 1f)), c(lerp(C.HUESO, C.MADERA, 0.4f)))
                    for (i in 0..4) for (lado in listOf(-1f, 1f)) drawLine(c(C.HIERRO).copy(alpha = 0.55f), q(lado * 1.6f + lado * 0.2f * i, -7.6f + i * 1.6f), q(lado * (8.6f + 0.36f * i), -7.6f + i * 1.6f), 0.35f)
                    for (lado in listOf(-1f, 1f)) drawLine(c(C.ROJO).copy(alpha = 0.6f), q(lado * 4.6f, -8.4f), q(lado * 5.4f, -0.6f), 0.3f)
                    drawLine(c(C.LATON), q(4f, -11f), q(14f, -8f), 1.1f, StrokeCap.Round); drawLine(c(C.HOLLIN), q(14f, -8f), q(15.2f, -7.6f), 0.7f)
                }
                "pipa" -> {
                    // La pipa de barro y la petaca del tabaco.
                    drawPath(Path().apply { moveTo(q(-10f, -3f).x, q(0f, -3f).y); cubicTo(q(-4f, -3.4f).x, q(0f, -3.4f).y, q(0f, -3f).x, q(0f, -3f).y, q(3f, -2f).x, q(0f, -2f).y) }, c(C.HUESO), style = Stroke(1.2f, cap = StrokeCap.Round))
                    drawPath(poligono(listOf(q(2f, -1f), q(2.4f, -8f), q(7f, -8f), q(6.6f, -1f))), c(C.HUESO)); drawOval(c(lerp(C.HOLLIN, C.MADERA, 0.4f)), q(2.6f, -8.8f), Size(4.2f, 1.6f)); drawRect(c(lerp(C.HUESO, C.HIERRO, 0.3f), -1f), q(2.2f, -8f), Size(1.2f, 7f))
                    drawRoundRect(c(lerp(C.MADERA, C.HOLLIN, 0.3f)), q(9f, -5f), Size(9f, 5f), CornerRadius(1.6f)); drawLine(luz(lerp(C.MADERA, C.HUESO, 0.4f)), q(10f, -4.4f), q(17f, -4.4f), 0.5f)
                }
                "carnet" -> {
                    // El carné del sindicato: una cartulina doblada con su sello.
                    drawPath(poligono(q(-8f, 0f), q(-9f, -10f), q(0f, -11f), q(0f, 0f)), c(lerp(C.HUESO, C.LATON, 0.2f), -1f)); drawPath(poligono(q(0f, 0f), q(0f, -11f), q(9f, -10f), q(8f, 0f)), c(lerp(C.HUESO, C.LATON, 0.12f), 1f))
                    for (i in 0..3) drawLine(c(C.HIERRO).copy(alpha = 0.5f), q(1.6f, -8.6f + i * 1.8f), q(6.6f, -8.2f + i * 1.8f), 0.35f)
                    drawCircle(c(C.ROJO).copy(alpha = 0.75f), 2.6f, q(-4.4f, -5.6f), style = Stroke(0.7f)); drawCircle(c(C.ROJO).copy(alpha = 0.75f), 0.9f, q(-4.4f, -5.6f))
                }
                else -> {}
            }
        }
    }
}
