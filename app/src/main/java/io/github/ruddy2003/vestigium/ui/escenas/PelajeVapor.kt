package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.sin

// =====================================================================
//  Vapor victoriano: gris atigrado, de pecho claro y ojos de latón, con su chaleco granate de botones dorados
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/vapor_*`): atigrado gris, con un chaleco granate ribeteado y abotonado
// en dorado. Sus dos acciones propias: beber el té de la lata, que humea, y jugar con un trozo de carbón.

private val V_GRANATE = Color(0xFF8E2A2A)
private val V_GRANATE_OSC = Color(0xFF5E1C20)
private val V_LATON = Color(0xFFD2A44A)

/** El chaleco, sobre el lomo, con su cincha por debajo del pecho y sus dos botones. */
private val chaleco: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val dir = p.cadera - p.hombro; val largo = dir.getDistance().coerceAtLeast(0.1f)
    // Hacia el lomo: la perpendicular al cuerpo que mira hacia arriba.
    val lomo = Offset(-dir.y, dir.x).let { if (it.y > 0f) it * -1f else it } / largo
    val a = p.hombro + dir * 0.02f + lomo * 4.2f; val b = p.hombro + dir * 0.56f + lomo * 4.5f
    // La cincha, que baja del chaleco y rodea el pecho.
    drawLine(luz.tono(V_GRANATE_OSC), p.hombro + dir * 0.24f + lomo * 4.2f, p.hombro + dir * 0.18f - lomo * 4.2f, 2f, StrokeCap.Round)
    drawLine(luz.tono(V_GRANATE_OSC), a, b, 6f, StrokeCap.Round)
    drawLine(luz.tono(V_GRANATE), a + lomo * 0.5f, b + lomo * 0.5f, 4.4f, StrokeCap.Round)
    drawLine(luz.tono(V_LATON), a - lomo * 2.3f, b - lomo * 2.3f, 0.5f)
    for (t in listOf(0.3f, 0.62f)) drawCircle(luz.tono(V_LATON), 0.8f, a + (b - a) * t - lomo * 0.6f)
}

private fun poseTe(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, se inclina sobre la lata y da un lengüetazo.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(6.4f + listOf(0f, 0.6f, 1.2f, 1.2f, 0.6f, 0f)[q], -22f + listOf(0f, 1.6f, 3.4f, 3.4f, 1.6f, 0f)[q]), morro = listOf(0.3f, 0.5f, 0.7f, 0.7f, 0.5f, 0.3f)[q], orejas = 1f,
        ojos = if (q in 2..3) 0.3f else 1f, lengua = q in 2..3, cola2 = listOf(2.2f, 2.3f, 2.4f, 2.4f, 2.3f, 2.2f)[q],
    )
}

/** La lata del té, delante de él, con su vaho. */
private val lataDelTe: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val t = Offset(15f, 0f)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(t.x - 4f, -0.8f), Size(8f, 1.6f))
    drawRoundRect(luz.tono(Color(0xFFB8BCC0)), t + Offset(-2.6f, -6f), Size(5.2f, 6f), androidx.compose.ui.geometry.CornerRadius(0.7f)); drawRect(luz.tono(Color(0xFFE9E0C8)), t + Offset(-2.6f, -6f), Size(5.2f, 0.8f))
    drawArc(luz.tono(Color(0xFF4A4C54)), 200f, 140f, false, t + Offset(-2.4f, -9f), Size(4.8f, 6f), style = Stroke(0.4f))
    for (i in 0..1) { val y = -8f - listOf(0f, 1f, 2f, 3f, 4f, 5f)[(q + i * 3) % 6]; drawPath(Path().apply { moveTo(t.x - 0.6f + i * 1.4f, y); cubicTo(t.x - 2f + i * 1.4f, y - 1.6f, t.x + 1f + i * 1.4f, y - 2.4f, t.x - 0.4f + i * 1.4f, y - 4f) }, Color.White.copy(alpha = 0.5f - 0.07f * ((q + i * 3) % 6)), style = Stroke(0.6f, cap = StrokeCap.Round)) }
}

private fun poseCarbon(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.6f, -5.6f), Offset(13.4f, -2.4f), Offset(12.2f, -1f), Offset(10f, -2f), Offset(8.6f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -24.4f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.42f, 0.5f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un trozo de carbón: le da con la mano, rueda un poco y se para. */
private val carbonilla: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.4f, 1.8f, 3.2f, 4f, 4.2f)[q]; val gira = listOf(0f, 0.2f, 0.9f, 1.6f, 2f, 2.1f)[q]
    val o = Offset(14.6f + corre, -1.8f)
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(o.x - 2.6f, -0.7f), Size(5.2f, 1.3f))
    val pts = (0..4).map { i -> val a = gira + i * 1.2566f; o + Offset(kotlin.math.cos(a) * (1.9f + 0.4f * (i % 2)), kotlin.math.sin(a) * (1.6f + 0.3f * (i % 2))) }
    drawPath(Path().apply { moveTo(pts[0].x, pts[0].y); pts.drop(1).forEach { lineTo(it.x, it.y) }; close() }, luz.tono(Color(0xFF2E2C30)))
    drawLine(luz.tono(Color(0xFF8A8E98)), pts[3], pts[4], 0.5f)
    // El polvo negro que deja en el suelo.
    for (i in 0 until q) drawCircle(luz.tono(Color(0xFF2E2C30)).copy(alpha = 0.35f), 0.4f, Offset(14.6f + i * 0.9f, -0.3f))
}

/** El gato de Vapor victoriano. */
internal val PELAJE_VAPOR = Pelaje(
    manto = Color(0xFF8C8C92), claro = Color(0xFFEDE8DE), marca = Color(0xFF55555E), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFE2BE4E),
    nariz = Color(0xFFD99A98), accesorio = chaleco,
    especiales = listOf(EspecialGato("bebe el té de la lata", true, 6, ::poseTe, lataDelTe), EspecialGato("juega con un trozo de carbón", true, 6, ::poseCarbon, carbonilla)),
)
