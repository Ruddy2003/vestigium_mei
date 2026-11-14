package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp

/**
 * Los doce objetos del día del hermano Odo, sobre su banco alto. Cada uno se pinta alrededor del origen (el punto del
 * banco donde se apoya, con unas 34 unidades de ancho) y pide sus colores a [tono], que es la luz del sitio.
 */
internal object ObjetosMedieval {
    private val C = ColorMedieval

    /** Los ids del calendario: lo vigila la prueba. */
    val TODOS = listOf("pluma", "pergamino", "tinta", "cortaplumas", "piedra_pomez", "atril", "salterio", "habito", "campana", "vela", "pan_oro", "tablilla")

    fun DrawScope.objeto(id: String?, en: Offset, k: Float, tono: (Color) -> Color) {
        if (id == null || id !in TODOS) return
        translate(en.x, en.y) { scale(k, k, Offset.Zero) {
            // La sombra con que se asienta en el banco.
            drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(-15f, -1.6f), Size(30f, 4.6f))
            when (id) {
                "pluma" -> {
                    // Dos plumas de ganso: una entera y otra ya cortada, con las virutas del cañón.
                    for ((de, a) in listOf(Offset(-13f, -1.6f) to Offset(12f, -9f), Offset(-9f, -0.6f) to Offset(14f, -2.6f))) {
                        val d = a - de; val lado = Offset(-d.y, d.x) / d.getDistance() * 2.8f; val medio = de + d * 0.3f
                        drawPath(Path().apply { moveTo(medio.x, medio.y); cubicTo(medio.x + lado.x * 1.3f, medio.y + lado.y * 1.3f, a.x + lado.x, a.y + lado.y, a.x, a.y); cubicTo(a.x - lado.x * 0.6f, a.y - lado.y * 0.6f, medio.x - lado.x * 0.8f, medio.y - lado.y * 0.8f, medio.x, medio.y); close() }, tono(Color.White))
                        drawLine(tono(lerp(C.PERGAMINO, C.MADERA, 0.35f)), de, a, 0.8f, StrokeCap.Round); drawLine(tono(C.TINTA), de, de + d * 0.05f, 0.9f)
                    }
                    for (x in listOf(-4f, -1f, 3f)) drawLine(tono(C.PERGAMINO), Offset(x, -0.8f), Offset(x + 1.6f, -1.6f), 0.6f)
                }
                "pergamino" -> {
                    // Una hoja rayada, con una esquina que se levanta, y un rollo atado.
                    drawPath(Path().apply { moveTo(-14f, -1f); lineTo(6f, -1.6f); lineTo(9f, -9f); cubicTo(2f, -10.6f, -6f, -9f, -11f, -9.6f); close() }, tono(C.PERGAMINO))
                    for (i in 0..4) drawLine(tono(lerp(C.PERGAMINO, C.ARENISCA, 0.6f)), Offset(-11.4f + i * 0.5f, -2.6f - i * 1.4f), Offset(5.6f + i * 0.5f, -3f - i * 1.4f), 0.35f)
                    drawRoundRect(tono(lerp(C.PERGAMINO, C.ARENISCA, 0.2f)), Offset(2f, -6f), Size(13f, 5f), CornerRadius(2.5f)); drawOval(tono(lerp(C.PERGAMINO, C.MADERA, 0.4f)), Offset(13f, -6f), Size(2.6f, 5f)); drawLine(tono(C.ROJO), Offset(8f, -6f), Offset(8f, -1f), 0.9f)
                }
                "tinta" -> {
                    // El cuerno de la tinta en su pie de madera, y el tarro de la roja.
                    drawRect(tono(C.MADERA_OSC), Offset(-11f, -4f), Size(13f, 4f)); drawRect(tono(lerp(C.MADERA, C.CAL, 0.2f)), Offset(-11f, -4f), Size(13f, 1f))
                    drawPath(Path().apply { moveTo(-8f, -15f); lineTo(-1.6f, -15f); cubicTo(-2f, -9f, -3f, -5f, -6f, -1f); cubicTo(-7.4f, -5f, -8f, -9f, -8f, -15f); close() }, tono(lerp(C.MADERA_OSC, C.TINTA, 0.4f)))
                    drawOval(tono(C.TINTA), Offset(-8f, -16.2f), Size(6.4f, 2.4f)); drawOval(tono(lerp(C.ARENISCA, C.CAL, 0.4f)), Offset(-8f, -16.2f), Size(6.4f, 2.4f), style = Stroke(0.6f))
                    drawPath(Path().apply { moveTo(5f, -7f); lineTo(12f, -7f); cubicTo(14f, -4f, 13f, -1f, 11.6f, 0f); lineTo(5.4f, 0f); cubicTo(4f, -1f, 3f, -4f, 5f, -7f); close() }, tono(C.LOSA)); drawOval(tono(C.ROJO), Offset(5f, -8.2f), Size(7f, 2.4f))
                }
                "cortaplumas" -> {
                    // La navaja pequeña, de mango de hueso, y un cañón recién cortado.
                    drawPath(Path().apply { moveTo(-13f, -2.6f); lineTo(1f, -4.6f); lineTo(1f, -1.4f); close() }, tono(lerp(C.HIERRO, Color.White, 0.55f)))
                    drawRoundRect(tono(lerp(C.PERGAMINO, C.MADERA, 0.25f)), Offset(1f, -5f), Size(13f, 3.8f), CornerRadius(1.6f)); for (x in listOf(4f, 10f)) drawCircle(tono(C.HIERRO), 0.6f, Offset(x, -3.1f))
                    drawLine(tono(C.PERGAMINO), Offset(-9f, -7f), Offset(3f, -9.4f), 1f, StrokeCap.Round); for (x in listOf(-6f, -3f, 0f)) drawLine(tono(C.PERGAMINO), Offset(x, -0.6f), Offset(x + 1.4f, -1.4f), 0.6f)
                }
                "piedra_pomez" -> {
                    // La piedra pómez, gris y llena de agujeros, sobre un retal ya lijado.
                    drawPath(Path().apply { moveTo(-14f, -0.6f); lineTo(13f, -1.4f); lineTo(11f, -5f); lineTo(-12f, -4.4f); close() }, tono(C.PERGAMINO))
                    drawPath(Path().apply { moveTo(-8f, -3f); cubicTo(-9f, -10f, -2f, -13f, 4f, -11.6f); cubicTo(9f, -10f, 9.6f, -5f, 7f, -3f); close() }, tono(lerp(C.PIZARRA, C.CAL, 0.45f)))
                    for ((x, y) in listOf(-4f to -8f, 0f to -10f, 3f to -6.4f, -1f to -5.6f, 5f to -8.6f, -5.6f to -5f)) drawCircle(tono(C.PIZARRA), 0.8f, Offset(x, y))
                    drawPath(Path().apply { moveTo(-8f, -3f); cubicTo(-9f, -10f, -2f, -13f, 4f, -11.6f) }, tono(Color.White).copy(alpha = 0.5f), style = Stroke(0.7f))
                }
                "atril" -> {
                    // Un atril de mesa: una cuña de madera con su borde y una hoja encima.
                    drawPath(Path().apply { moveTo(-12f, 0f); lineTo(12f, 0f); lineTo(12f, -4f); lineTo(-12f, -15f); close() }, tono(C.MADERA_OSC))
                    drawPath(Path().apply { moveTo(-13f, -15.6f); lineTo(13f, -5f); lineTo(13f, -3f); lineTo(-13f, -13.6f); close() }, tono(lerp(C.MADERA, C.CAL, 0.15f)))
                    drawPath(Path().apply { moveTo(-9f, -15.4f); lineTo(8f, -8.4f); lineTo(6f, -4.6f); lineTo(-11f, -11.6f); close() }, tono(C.PERGAMINO)); for (i in 0..2) drawLine(tono(C.TINTA).copy(alpha = 0.6f), Offset(-8.6f - i * 0.6f, -13.6f + i * 1.2f), Offset(5.6f - i * 0.6f, -7.8f + i * 1.2f), 0.4f)
                    drawRect(tono(C.MADERA_OSC), Offset(11f, -6.6f), Size(2.4f, 4f))
                }
                "salterio" -> {
                    // El libro de los salmos, cerrado: cuero rojo, cinco bullones y dos broches.
                    drawPath(Path().apply { moveTo(-13f, -1f); lineTo(11f, -1f); lineTo(13f, -7f); lineTo(-11f, -7f); close() }, tono(C.ROJO))
                    drawRect(tono(lerp(C.ROJO, C.TINTA, 0.35f)), Offset(-13f, -1f), Size(24f, 1.4f)); drawPath(Path().apply { moveTo(11f, -1f); lineTo(13f, -7f); lineTo(13f, -5.6f); lineTo(11f, 0.4f); close() }, tono(C.PERGAMINO))
                    for ((x, y) in listOf(-9f to -2.4f, 8f to -2.4f, -7f to -5.8f, 10f to -5.8f, 0.4f to -4f)) drawCircle(tono(C.ORO), 0.9f, Offset(x, y))
                    for (x in listOf(-2f, 5f)) drawRect(tono(C.ORO), Offset(x, -1.2f), Size(1.6f, 1.8f))
                }
                "habito" -> {
                    // El hábito de repuesto, doblado: lana negra, con el cinto de cuero encima.
                    drawPath(Path().apply { moveTo(-14f, 0f); lineTo(13f, 0f); cubicTo(14.6f, -3f, 13f, -6f, 12f, -8f); lineTo(-12f, -8.6f); cubicTo(-14.6f, -6f, -14.6f, -3f, -14f, 0f); close() }, tono(C.LANA))
                    for (y in listOf(-2.8f, -5.6f)) drawLine(tono(lerp(C.LANA, C.PIZARRA, 0.5f)), Offset(-13.4f, y), Offset(12.6f, y + 0.4f), 0.5f)
                    drawPath(Path().apply { moveTo(-8f, -8.4f); cubicTo(-4f, -12f, 5f, -12f, 8f, -8.2f) }, tono(C.MADERA_OSC), style = Stroke(1.5f)); drawRect(tono(C.ORO), Offset(-1.4f, -11.8f), Size(2.8f, 2.4f))
                }
                "campana" -> {
                    // Una campanilla de bronce, de mano, con su badajo.
                    drawPath(Path().apply { moveTo(-3f, -15f); cubicTo(-4f, -8f, -8f, -5f, -9.6f, -1f); lineTo(9.6f, -1f); cubicTo(8f, -5f, 4f, -8f, 3f, -15f); close() }, tono(lerp(C.ORO, C.MADERA, 0.35f)))
                    drawPath(Path().apply { moveTo(-3f, -15f); cubicTo(-4f, -8f, -8f, -5f, -9.6f, -1f); lineTo(-5f, -1f); cubicTo(-4f, -6f, -1.6f, -9f, -1f, -15f); close() }, tono(lerp(C.ORO, Color.White, 0.3f)))
                    drawRect(tono(lerp(C.ORO, C.MADERA_OSC, 0.5f)), Offset(-10.4f, -2f), Size(20.8f, 1.6f)); drawRect(tono(C.MADERA_OSC), Offset(-1.2f, -20f), Size(2.4f, 5.4f)); drawCircle(tono(C.MADERA_OSC), 2f, Offset(0f, -20.6f)); drawCircle(tono(C.HIERRO), 1.3f, Offset(1.6f, 0f))
                }
                "vela" -> {
                    // Una vela de cera de abeja, apagada, en su palmatoria: aquí no se enciende ninguna.
                    drawOval(tono(lerp(C.ORO, C.MADERA_OSC, 0.4f)), Offset(-9f, -3f), Size(18f, 4.4f)); drawArc(tono(lerp(C.ORO, C.MADERA_OSC, 0.4f)), -60f, 200f, false, Offset(7f, -5f), Size(6f, 5f), style = Stroke(1.2f))
                    drawRect(tono(lerp(C.ORO, C.PERGAMINO, 0.6f)), Offset(-2.4f, -19f), Size(4.8f, 17f)); drawLine(tono(Color.White).copy(alpha = 0.5f), Offset(-1.4f, -18f), Offset(-1.4f, -3f), 0.7f)
                    drawLine(tono(C.TINTA), Offset(0f, -19f), Offset(0.6f, -21.4f), 0.6f); for (x in listOf(-2.4f, 1.6f)) drawLine(tono(lerp(C.ORO, C.PERGAMINO, 0.7f)), Offset(x, -17f), Offset(x, -12f - x), 1.1f, StrokeCap.Round)
                }
                "pan_oro" -> {
                    // El librillo del pan de oro: una hoja que brilla entre dos pieles, y el bruñidor de diente.
                    drawPath(Path().apply { moveTo(-13f, -1f); lineTo(5f, -1f); lineTo(7f, -8f); lineTo(-11f, -8f); close() }, tono(lerp(C.PERGAMINO, C.ARENISCA, 0.3f)))
                    drawPath(Path().apply { moveTo(-10f, -2f); lineTo(3f, -2f); lineTo(4.6f, -7f); lineTo(-8.4f, -7f); close() }, tono(C.ORO)); drawPath(Path().apply { moveTo(-8f, -3f); lineTo(-2f, -3f); lineTo(-0.6f, -6.4f); lineTo(-6.6f, -6.4f); close() }, tono(lerp(C.ORO, Color.White, 0.55f)))
                    drawLine(tono(C.MADERA), Offset(6f, -2f), Offset(14f, -7f), 1.6f, StrokeCap.Round); drawCircle(tono(lerp(C.PERGAMINO, Color.White, 0.4f)), 1.5f, Offset(5.4f, -1.6f))
                }
                "tablilla" -> {
                    // Dos tablillas de cera, atadas por un lado, con su punzón.
                    drawPath(Path().apply { moveTo(-14f, -1f); lineTo(4f, -1f); lineTo(6f, -9f); lineTo(-12f, -9f); close() }, tono(C.MADERA)); drawPath(Path().apply { moveTo(-11.6f, -2.2f); lineTo(2.6f, -2.2f); lineTo(4f, -7.8f); lineTo(-10.2f, -7.8f); close() }, tono(lerp(C.TINTA, C.VERDE, 0.2f)))
                    for (i in 0..2) drawLine(tono(C.PERGAMINO).copy(alpha = 0.55f), Offset(-9.6f + i * 0.4f, -3.6f - i * 1.5f), Offset(0f + i * 1.4f, -3.8f - i * 1.5f), 0.4f)
                    drawPath(Path().apply { moveTo(-1f, -9.4f); lineTo(14f, -7f); lineTo(13f, -4f); lineTo(4.6f, -5.4f) }, tono(lerp(C.MADERA, C.MADERA_OSC, 0.4f)))
                    drawLine(tono(lerp(C.HIERRO, C.CAL, 0.4f)), Offset(-6f, -11f), Offset(9f, -10f), 0.9f, StrokeCap.Round); drawCircle(tono(lerp(C.HIERRO, C.CAL, 0.4f)), 1f, Offset(9.4f, -10f))
                }
            }
        } }
    }
}
