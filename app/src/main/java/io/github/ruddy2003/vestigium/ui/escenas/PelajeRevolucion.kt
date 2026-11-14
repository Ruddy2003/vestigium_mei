package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Revolución francesa: gris perla, de pecho blanco y ojos verdes, con gorro frigio rojo y collar azul
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/revolucion_*`): gris claro, con el pecho blanco, un gorro rojo de punta
// caída con su escarapela y un collar azul. Sus dos acciones propias: agitar una banderita de tres colores y lanzar
// al aire una escarapela (el de pixel lanza el gorro; aquí el gorro es el accesorio y no se puede quitar).

private val R_ROJO = Color(0xFFB4463C)
private val R_ROJO_LUZ = Color(0xFFD86A58)
private val R_ROJO_OSC = Color(0xFF7C2C28)
private val R_ANIL = Color(0xFF41608E)
private val R_CAL = Color(0xFFF2ECDD)
private val R_MADERA = Color(0xFF8E6A46)

/** El gorro frigio, dibujado con el centro de su base en [c]: el casquete, la punta que cae hacia la nuca y la escarapela. */
private fun DrawScope.gorro(luz: LuzGato, c: Offset, r: Float, hacia: Float, conEscarapela: Boolean) {
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    val forma = Path().apply {
        moveTo(c.x - r * 1.04f, c.y + 1f); cubicTo(c.x - r * 1.1f, c.y - 4f, c.x - r * 0.7f, c.y - 8f, c.x - hacia * r * 0.2f, c.y - 8.6f)
        cubicTo(c.x - hacia * r * 0.9f, c.y - 10.4f, c.x - hacia * r * 1.5f, c.y - 8f, c.x - hacia * r * 1.5f, c.y - 4.6f)
        cubicTo(c.x - hacia * r * 1.2f, c.y - 6.6f, c.x - hacia * r * 0.4f, c.y - 6f, c.x + r * 0.5f, c.y - 6f)
        cubicTo(c.x + r * 0.95f, c.y - 5f, c.x + r * 1.1f, c.y - 2f, c.x + r * 1.04f, c.y + 1f)
        cubicTo(c.x + r * 0.5f, c.y - 0.8f, c.x - r * 0.5f, c.y - 0.8f, c.x - r * 1.04f, c.y + 1f); close()
    }
    drawPath(forma, Brush.horizontalGradient(if (lado > 0f) listOf(luz.tono(R_ROJO_OSC), luz.tono(R_ROJO), luz.tono(R_ROJO_LUZ)) else listOf(luz.tono(R_ROJO_LUZ), luz.tono(R_ROJO), luz.tono(R_ROJO_OSC)), c.x - r * 1.5f, c.x + r * 1.1f))
    drawPath(Path().apply { moveTo(c.x - r * 1.04f, c.y + 1f); cubicTo(c.x - r * 0.5f, c.y - 0.8f, c.x + r * 0.5f, c.y - 0.8f, c.x + r * 1.04f, c.y + 1f) }, luz.tono(R_ROJO_OSC), style = Stroke(1f, cap = StrokeCap.Round))
    if (conEscarapela) { val o = c + Offset(hacia * r * 0.55f, -3.4f); drawCircle(luz.tono(R_ANIL), 1.7f, o); drawCircle(luz.tono(R_CAL), 1.1f, o); drawCircle(luz.tono(R_ROJO_OSC), 0.5f, o) }
}

/** El gorro puesto: entre las orejas, con la punta hacia la nuca y la escarapela de este lado. */
private val gorroPuesto: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo)
    val r = Gatuno.R_CABEZA
    gorro(luz, p.cabeza + Offset(-0.3f * s, -3f), r, if (co < -0.3f) -1f else 1f, p.giro >= -0.25f)
}

private fun poseBandera(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.2f + 0.3f * (q % 2)), morro = 0.3f, orejas = 1f, ojos = if (q == 4) 0.3f else 1f,
        manoC = Offset(11f + listOf(0f, 0.6f, 1.2f, 0.6f, 0f, -0.6f)[q], -24f + listOf(0f, -0.8f, -1.2f, -0.8f, 0f, 0.4f)[q]), manoL = Offset(9f, -12f), cola2 = listOf(2.2f, 2.5f, 2.8f, 2.6f, 2.3f, 2.1f)[q],
    )
}

/** Una banderita de tres colores en su palo, que agita con una mano. */
private val banderita: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val pie = p.manoC + Offset(0.4f, 1f); val punta = pie + Offset(listOf(2f, 3f, 4f, 3f, 2f, 1f)[q], -17f)
    drawLine(luz.tono(R_MADERA), pie, punta, 0.9f, StrokeCap.Round)
    val onda = listOf(0f, 0.8f, 1.4f, 0.8f, 0f, -0.8f)[q]
    for ((i, col) in listOf(R_ANIL, R_CAL, R_ROJO).withIndex()) {
        val x0 = punta.x + 0.4f + i * 3.4f; val x1 = x0 + 3.4f
        drawPath(Path().apply { moveTo(x0, punta.y + onda * i / 3f); lineTo(x1, punta.y + onda * (i + 1) / 3f); lineTo(x1, punta.y + 7f + onda * (i + 1) / 3f); lineTo(x0, punta.y + 7f + onda * i / 3f); close() }, luz.tono(col))
    }
}

private fun poseGorro(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -25f - listOf(0f, 0.6f, 1f, 1f, 0.6f, 0f)[q]), morro = listOf(0.2f, 0.1f, 0f, 0f, 0.1f, 0.2f)[q], orejas = 1f, ojos = 1f,
        manoC = Offset(9f, listOf(-10f, -20f, -24f, -22f, -16f, -8f)[q]), cola2 = listOf(2.1f, 2.4f, 2.7f, 2.6f, 2.3f, 2f)[q],
    )
}

/** La escarapela, lanzada al aire: sube girando y vuelve a la mano. */
private val escarapelaAlAire: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val alza = listOf(0f, 9f, 15f, 16f, 10f, 2f)[q]; val canto = listOf(1f, 0.6f, 0.3f, 0.7f, 1f, 0.9f)[q]
    val o = p.manoC + Offset(1.6f + alza * 0.12f, -3f - alza)
    for ((rr, col) in listOf(3.4f to R_ANIL, 2.3f to R_CAL, 1.2f to R_ROJO)) drawOval(luz.tono(col), Offset(o.x - rr * canto, o.y - rr), Size(rr * 2f * canto, rr * 2f))
    drawLine(luz.tono(R_ANIL), o + Offset(-0.8f, 3f), o + Offset(-1.8f, 6.4f), 0.9f); drawLine(luz.tono(R_ROJO), o + Offset(0.8f, 3f), o + Offset(1.6f, 6.6f), 0.9f)
}

/** El gato de Revolución francesa. */
internal val PELAJE_REVOLUCION = Pelaje(
    manto = Color(0xFFB4AFA8), claro = Color(0xFFF4EFE6), marca = Color(0xFF8E8880), dibujo = DibujoPelaje.BICOLOR, ojos = Color(0xFF7FA468),
    nariz = Color(0xFFE0A0A0), collar = R_ANIL, accesorio = gorroPuesto,
    especiales = listOf(EspecialGato("agita una banderita de tres colores", true, 6, ::poseBandera, banderita), EspecialGato("lanza la escarapela al aire", true, 6, ::poseGorro, escarapelaAlAire)),
)
