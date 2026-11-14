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
 * Los doce objetos del día de Giulia. Se dibujan igual en los dos cuadros: sobre la mesa de trabajo de la capilla y
 * sobre la mesa de la azotea. Cada uno se pinta alrededor del origen (el punto de la mesa donde se apoya, con unas 34
 * unidades de ancho) y pide sus colores a [tono], que es la luz del sitio.
 */
internal object ObjetosUrbe {
    private val C = ColorUrbe

    /** Los ids del calendario: lo vigila la prueba. */
    val TODOS = listOf("hisopo", "bisturi", "lampara", "motoneta", "telefono", "mono", "moka", "cuentahilos", "pigmento", "cuaderno", "llaves", "andamio")

    fun DrawScope.objeto(id: String?, en: Offset, k: Float, tono: (Color) -> Color) {
        if (id == null || id !in TODOS) return
        translate(en.x, en.y) { scale(k, k, Offset.Zero) {
            // La sombra con que se asienta en la mesa.
            drawOval(C.SOMBRA.copy(alpha = 0.3f), Offset(-15f, -1.6f), Size(30f, 4.6f))
            when (id) {
                "hisopo" -> {
                    // Un tarro de hisopos y tres ya usados, con la punta sucia.
                    drawRect(tono(lerp(C.CAL, C.AZUL, 0.2f)).copy(alpha = 0.85f), Offset(-11f, -12f), Size(9f, 12f)); drawRect(tono(C.ACERO), Offset(-11.6f, -13f), Size(10.2f, 1.6f))
                    for (dx in listOf(-9f, -6.6f, -4.4f)) { drawLine(tono(C.MADERA), Offset(dx, -11f), Offset(dx + 0.6f, -19f), 0.7f); drawCircle(tono(C.CAL), 1.3f, Offset(dx + 0.7f, -20f)) }
                    for ((i, x) in listOf(2f, 6f, 9f).withIndex()) { drawLine(tono(C.MADERA), Offset(x, -1.4f - i), Offset(x + 12f, -3f + i * 0.6f), 0.7f); drawCircle(tono(listOf(C.HOLLIN, C.OCRE, C.CAL)[i]), 1.3f, Offset(x, -1.4f - i)) }
                }
                "bisturi" -> {
                    // El bisturí en su bandeja de acero, con una gasa.
                    drawRoundRect(tono(C.ACERO), Offset(-14f, -4f), Size(28f, 4.6f), CornerRadius(1.4f)); drawRoundRect(tono(lerp(C.ACERO, Color.White, 0.45f)), Offset(-13f, -3.6f), Size(26f, 2.8f), CornerRadius(1f))
                    drawLine(tono(C.HIERRO), Offset(-9f, -2.4f), Offset(4f, -3f), 1.2f, StrokeCap.Round); drawPath(Path().apply { moveTo(4f, -3.8f); lineTo(11f, -3.3f); lineTo(4f, -2.2f); close() }, tono(Color.White))
                    drawRect(tono(C.CAL), Offset(-13f, -7f), Size(6f, 3.4f))
                }
                "lampara" -> {
                    // La lámpara de luz ultravioleta: una caja negra con su tubo, que da un violeta apagado.
                    drawRoundRect(tono(C.HIERRO), Offset(-13f, -8f), Size(24f, 8f), CornerRadius(2f)); drawRoundRect(Color(0xFF8A6CE0), Offset(-11f, -6.4f), Size(20f, 3f), CornerRadius(1.5f))
                    drawRoundRect(Color(0xFFC8B4FF).copy(alpha = 0.8f), Offset(-10f, -5.6f), Size(18f, 1.2f), CornerRadius(0.6f)); drawOval(Color(0xFF8A6CE0).copy(alpha = 0.22f), Offset(-16f, -10f), Size(30f, 12f))
                    drawRect(tono(C.HIERRO), Offset(11f, -6f), Size(5f, 3.6f)); drawLine(tono(C.HOLLIN), Offset(16f, -4f), Offset(19f, -0.6f), 0.8f)
                }
                "motoneta" -> {
                    // De la motoneta, lo que sube a la obra: el casco y las llaves.
                    val casco = Path().apply { moveTo(-11f, -1f); cubicTo(-13f, -17f, 9f, -19f, 10f, -5f); lineTo(10f, -1f); close() }
                    drawPath(casco, tono(C.CAL)); drawPath(Path().apply { moveTo(-2f, -9f); cubicTo(3f, -11f, 9f, -9f, 10f, -5f); lineTo(10f, -3f); lineTo(-1f, -3f); close() }, tono(lerp(C.HIERRO, C.AZUL, 0.3f)))
                    drawLine(tono(C.ROJO), Offset(-10.6f, -8f), Offset(-3f, -15.4f), 1.6f); drawLine(Color.White.copy(alpha = 0.5f), Offset(1f, -8.6f), Offset(6f, -8.2f), 0.7f)
                    drawCircle(tono(C.ACERO), 1.6f, Offset(14f, -1.6f), style = Stroke(0.6f)); drawLine(tono(C.ACERO), Offset(15f, -1f), Offset(18.6f, -0.2f), 1f)
                }
                "telefono" -> {
                    drawRoundRect(tono(C.HIERRO), Offset(-9f, -3.4f), Size(20f, 3.6f), CornerRadius(1.2f)); drawPath(Path().apply { moveTo(-8f, -3.4f); lineTo(10f, -3.4f); lineTo(8f, -5.2f); lineTo(-6f, -5.2f); close() }, tono(lerp(C.HIERRO, C.AZUL, 0.35f)))
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(-4f, -4.8f), Offset(2f, -4.8f), 0.6f)
                    drawPath(Path().apply { moveTo(11f, -1.6f); cubicTo(15f, -1f, 14f, -3.4f, 18f, -2.4f) }, tono(C.CAL), style = Stroke(0.8f, cap = StrokeCap.Round))
                }
                "mono" -> {
                    // El mono, doblado: blanco, o lo fue, con manchas de todos los colores de la capilla.
                    val tela = Path().apply { moveTo(-15f, -1f); cubicTo(-16f, -8f, -8f, -10f, 0f, -9f); cubicTo(8f, -11f, 15f, -8f, 15f, -1f); close() }
                    drawPath(tela, tono(C.CAL)); drawPath(Path().apply { moveTo(-13f, -5f); cubicTo(-6f, -6.6f, 6f, -6f, 13.6f, -4.6f) }, tono(lerp(C.CAL, C.SOMBRA, 0.4f)), style = Stroke(0.6f))
                    for ((i, q) in listOf(Offset(-8f, -6.6f), Offset(-2f, -3f), Offset(5f, -6f), Offset(9f, -2.6f), Offset(-11f, -2.4f)).withIndex()) drawCircle(tono(listOf(C.AZUL, C.OCRE, C.ROJO, C.VERDE, C.HOLLIN)[i]).copy(alpha = 0.75f), 1.1f + i % 2 * 0.5f, q)
                    drawRect(tono(lerp(C.CAL, C.SOMBRA, 0.25f)), Offset(-3f, -8f), Size(6f, 3.6f))
                }
                "moka" -> {
                    // La moka de ocho caras y su taza.
                    drawPath(Path().apply { moveTo(-9f, 0f); lineTo(-7f, -8f); lineTo(1f, -8f); lineTo(3f, 0f); close() }, tono(C.ACERO)); drawPath(Path().apply { moveTo(-7f, -8f); lineTo(-8.6f, -16f); lineTo(2.6f, -16f); lineTo(1f, -8f); close() }, tono(lerp(C.ACERO, Color.White, 0.25f)))
                    drawPath(Path().apply { moveTo(-8.6f, -16f); lineTo(-3f, -19f); lineTo(2.6f, -16f); close() }, tono(C.ACERO)); drawCircle(tono(C.HIERRO), 1f, Offset(-3f, -19.6f))
                    drawLine(tono(C.HIERRO), Offset(2.6f, -15f), Offset(6.6f, -13f), 1.6f); drawLine(tono(C.HIERRO), Offset(6.6f, -13f), Offset(5f, -8f), 1.6f); drawLine(tono(lerp(C.ACERO, C.HOLLIN, 0.3f)), Offset(-6f, -7.6f), Offset(-7.4f, -15f), 0.5f); drawLine(Color.White.copy(alpha = 0.5f), Offset(-1.4f, -7f), Offset(-0.4f, -1f), 0.7f)
                    drawRect(tono(C.CAL), Offset(9f, -5f), Size(6f, 5f)); drawOval(tono(C.MADERA_OSC), Offset(9.4f, -5.8f), Size(5.2f, 1.6f)); drawArc(tono(C.CAL), -90f, 180f, false, Offset(13.6f, -4f), Size(3.4f, 3.4f), style = Stroke(0.8f))
                }
                "cuentahilos" -> {
                    // La lupa plegable, abierta sobre una fotografía de la pared.
                    drawPath(Path().apply { moveTo(-14f, 0f); lineTo(10f, 0f); lineTo(13f, -4.4f); lineTo(-11f, -4.4f); close() }, tono(lerp(C.CAL, C.OCRE, 0.3f))); drawLine(tono(C.AZUL), Offset(-9f, -1.4f), Offset(6f, -1.4f), 0.6f); drawLine(tono(C.ROJO), Offset(-7f, -3f), Offset(8f, -3f), 0.6f)
                    drawRect(tono(C.HIERRO), Offset(-5f, -4.4f), Size(10f, 1.2f)); drawLine(tono(C.HIERRO), Offset(4.4f, -4.4f), Offset(5f, -12f), 1.2f); drawRect(tono(C.HIERRO), Offset(-4f, -13f), Size(10f, 1.4f)); drawOval(tono(lerp(C.CAL, C.AZUL, 0.3f)).copy(alpha = 0.8f), Offset(-2.6f, -13.4f), Size(7f, 2f))
                }
                "pigmento" -> {
                    // Tres tarros de pigmento en polvo, y un poco de azul fuera.
                    for ((i, c) in listOf(C.AZUL, C.ROJO, C.OCRE).withIndex()) { val x = -13f + i * 9f
                        drawRect(tono(lerp(C.CAL, c, 0.15f)).copy(alpha = 0.9f), Offset(x, -11f), Size(7f, 11f)); drawRect(tono(c), Offset(x + 0.6f, -7f + i), Size(5.8f, 6.6f - i)); drawRect(tono(C.MADERA_OSC), Offset(x - 0.4f, -12.6f), Size(7.8f, 2f)); drawLine(Color.White.copy(alpha = 0.35f), Offset(x + 1.2f, -10f), Offset(x + 1.2f, -1f), 0.6f) }
                    drawOval(tono(C.AZUL), Offset(13f, -2f), Size(6f, 2f))
                }
                "cuaderno" -> {
                    // El cuaderno de la obra, abierto, con un croquis y el lápiz. Sin letras: rayas.
                    drawPath(Path().apply { moveTo(-15f, -1f); lineTo(-12f, -7f); lineTo(0f, -6f); lineTo(12f, -7f); lineTo(15f, -1f); lineTo(0f, 0f); close() }, tono(C.CAL)); drawLine(tono(lerp(C.CAL, C.SOMBRA, 0.5f)), Offset(0f, -6f), Offset(0f, 0f), 0.7f)
                    for (k in 0..2) drawLine(tono(C.HOLLIN).copy(alpha = 0.5f), Offset(-11.4f + k * 0.5f, -5.4f + k * 1.4f), Offset(-2f, -4.8f + k * 1.4f), 0.4f)
                    drawRect(tono(C.AZUL).copy(alpha = 0.7f), Offset(3f, -5.4f), Size(4f, 2.6f)); drawRect(tono(C.HOLLIN).copy(alpha = 0.5f), Offset(2.4f, -6f), Size(8f, 4.4f), style = Stroke(0.4f))
                    drawLine(tono(C.ROJO), Offset(8f, -0.4f), Offset(18f, -2.6f), 1.1f, StrokeCap.Round); drawLine(tono(C.HOLLIN), Offset(18f, -2.6f), Offset(19.4f, -2.9f), 0.9f)
                }
                "llaves" -> {
                    // El manojo de la iglesia: un aro de hierro y cinco llaves, una muy pequeña.
                    drawCircle(tono(C.HIERRO), 5f, Offset(-7f, -4.6f), style = Stroke(1.2f))
                    for ((i, dx) in listOf(6f, 10f, 13.6f, 16f, 8f).withIndex()) { val y = -1.4f - i * 0.5f; val largo = if (i == 4) 5f else dx
                        drawLine(tono(if (i % 2 == 0) C.HIERRO else lerp(C.ORO, C.HIERRO, 0.4f)), Offset(-3.4f, -3f - i * 0.4f), Offset(-3.4f + largo, y), 1.1f, StrokeCap.Round); drawRect(tono(C.HIERRO), Offset(-4.6f + largo, y - 0.4f), Size(1.6f, 2f)) }
                }
                "andamio" -> {
                    // Del andamio: un tubo corto, dos abrazaderas y la llave de apretarlas.
                    drawLine(tono(lerp(C.ACERO, C.HIERRO, 0.3f)), Offset(-15f, -2.4f), Offset(9f, -3.6f), 3.6f, StrokeCap.Butt); drawLine(tono(lerp(C.ACERO, Color.White, 0.5f)), Offset(-15f, -3.4f), Offset(9f, -4.6f), 0.9f); drawOval(tono(C.HOLLIN), Offset(7.6f, -5.6f), Size(2.4f, 3.8f))
                    for (x in listOf(-9f, 0f)) { drawRect(tono(lerp(C.NARANJA, C.HIERRO, 0.45f)), Offset(x, -6.4f), Size(4f, 6.2f)); drawCircle(tono(C.HIERRO), 0.9f, Offset(x + 2f, -7f)) }
                    drawLine(tono(C.HIERRO), Offset(11f, -0.6f), Offset(19f, -2f), 1.4f, StrokeCap.Round); drawCircle(tono(C.HIERRO), 1.6f, Offset(19.4f, -2.1f), style = Stroke(0.9f))
                }
                else -> Unit
            }
        } }
    }
}
