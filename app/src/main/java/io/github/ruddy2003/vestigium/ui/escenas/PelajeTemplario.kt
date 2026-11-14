package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

// =====================================================================
//  Templario: blanco, con la capucha y la cola grises, con su sobreveste de lino y la cruz roja
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/templario_*`): blanco, con gris por arriba de la cabeza y en la cola,
// que lleva una sobreveste de lino con la cruz roja en el costado, el cinturón de cuero y la malla en el cuello. Sus dos
// acciones propias, las mismas que el de pixel: alzar el estandarte blanco y negro y afilar la espada.

private val T_LINO = Color(0xFFF2ECDC)
private val T_LINO_OSC = Color(0xFFC9C0AE)
private val T_CRUZ = Color(0xFFB0281E)
private val T_MALLA = Color(0xFF8A8E96)
private val T_CUERO = Color(0xFF6E4A2A)
private val T_NEGRO = Color(0xFF2B2725)
private val T_MADERA = Color(0xFF8E6A44)

/** La sobreveste sobre el lomo, con la cruz roja en el costado, el cinturón y la malla del cuello. */
private val sobreveste: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val dir = p.cadera - p.hombro; val largo = dir.getDistance().coerceAtLeast(0.1f)
    val lomo = Offset(-dir.y, dir.x).let { if (it.y > 0f) it * -1f else it } / largo
    val a = p.hombro + dir * 0.02f + lomo * 4.2f; val b = p.hombro + dir * 0.86f + lomo * 4.4f
    // El lino: una banda ancha sobre el lomo que cae por el costado.
    drawLine(luz.tono(T_LINO_OSC), a - lomo * 0.6f, b - lomo * 0.6f, 7.2f, StrokeCap.Round)
    drawLine(luz.tono(T_LINO), a + lomo * 0.6f, b + lomo * 0.6f, 5.6f, StrokeCap.Round)
    // La cruz roja, patada, en el costado.
    val c = a + (b - a) * 0.5f - lomo * 0.4f
    drawLine(luz.tono(T_CRUZ), c - lomo * 2.4f, c + lomo * 2.4f, 1.3f); drawLine(luz.tono(T_CRUZ), c - dir / largo * 2.4f, c + dir / largo * 2.4f, 1.3f)
    // El cinturón de cuero.
    drawLine(luz.tono(T_CUERO), p.hombro + dir * 0.62f + lomo * 7f, p.hombro + dir * 0.6f - lomo * 2.4f, 1f)
    // La malla del cuello: unos anillos grises.
    for (k in 0..3) drawCircle(luz.tono(T_MALLA), 0.9f, p.cuello + Offset(-1.6f + k * 1.1f, 1.2f + (k % 2) * 0.6f))
}

private fun poseEstandarte(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.6f), morro = -0.1f, orejas = 1f, ojos = if (q == 3) 0.3f else 1f,
        manoC = Offset(10f, -18f - listOf(0f, 1.6f, 3f, 3.4f, 2f, 0.6f)[q]), cola2 = listOf(2.3f, 2.5f, 2.7f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El estandarte blanco y negro en su asta, que alza con la mano y ondea. */
private val estandarte: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val pie = Offset(p.manoC.x + 0.6f, 0f); val punta = Offset(p.manoC.x + 1.4f, p.manoC.y - 16f)
    drawLine(luz.tono(T_MADERA), pie, punta, 0.9f, StrokeCap.Round)
    val onda = listOf(0f, 0.8f, 1.4f, 0.8f, 0f, -0.8f)[q]
    val x0 = punta.x + 0.4f; val x1 = x0 + 9f
    drawPath(Path().apply { moveTo(x0, punta.y); lineTo(x1, punta.y + onda); lineTo(x1, punta.y + 3f + onda); lineTo(x0, punta.y + 3f); close() }, luz.tono(T_NEGRO))
    drawPath(Path().apply { moveTo(x0, punta.y + 3f); lineTo(x1, punta.y + 3f + onda); lineTo(x1 - 2f, punta.y + 5.6f + onda); lineTo(x1, punta.y + 8f + onda); lineTo(x0, punta.y + 7f); close() }, luz.tono(T_LINO))
}

private fun poseAfilar(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9.6f, -4f), Offset(12f, -4.4f), Offset(14.4f, -4.8f), Offset(12f, -4.4f), Offset(9.6f, -4f), Offset(11f, -4.2f))[q]
    return Gatuno.SENTADO.copy(cabeza = Offset(6.4f, -23f), morro = 0.6f, orejas = 0.8f, ojos = 0.7f, manoC = mano, cola2 = listOf(2.1f, 2.2f, 2.3f, 2.2f, 2.1f, 2.2f)[q])
}

/** Una espada corta en el suelo y la piedra de afilar, que pasa con la mano adelante y atrás; salta una chispa. */
private val afilar: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(6f, -0.8f), Size(20f, 1.6f))
    drawLine(luz.tono(Color(0xFFB8BCC4)), Offset(8f, -1.4f), Offset(24f, -1.8f), 1.3f, StrokeCap.Round)
    drawLine(luz.tono(T_MALLA), Offset(6f, -3.2f), Offset(6.4f, 0.4f), 1f); drawLine(luz.tono(T_CUERO), Offset(3f, -1.2f), Offset(6f, -1.3f), 1.4f, StrokeCap.Round)
    drawLine(luz.tono(Color(0xFF58524E)), p.manoC + Offset(-1.6f, 1.4f), p.manoC + Offset(1.6f, 1.4f), 1.6f, StrokeCap.Round)
    if (q == 2) drawCircle(Color(0xFFFFE6A8), 0.5f, p.manoC + Offset(2.6f, 0.4f))
}

/** El gato de Templario. */
internal val PELAJE_TEMPLARIO = Pelaje(
    manto = Color(0xFFF6F1E4), claro = Color(0xFFFFFCF4), marca = Color(0xFF9A9CA2), dibujo = DibujoPelaje.PUNTAS, ojos = Color(0xFF8AAFC8),
    nariz = Color(0xFFDC9A98), accesorio = sobreveste,
    especiales = listOf(EspecialGato("alza el estandarte blanco y negro", true, 6, ::poseEstandarte, estandarte), EspecialGato("afila la espada", true, 6, ::poseAfilar, afilar)),
)
