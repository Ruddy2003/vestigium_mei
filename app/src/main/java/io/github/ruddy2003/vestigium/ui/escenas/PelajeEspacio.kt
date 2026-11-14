package io.github.ruddy2003.vestigium.ui.escenas

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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Espacio: gris atigrado, de pecho claro y ojos de ámbar, con el peto blanco de ribete naranja y el aro del casco
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/espacio_*`): atigrado gris, con un peto blanco ribeteado de naranja y
// la burbuja del casco. Dentro de la nave no lleva la burbuja: le queda el aro del cuello, de aluminio. Sus dos
// acciones propias: ponerse la burbuja del casco y darle con la mano a una tuerca que gira en el aire.

private val E_MANTO = Color(0xFF8E96A6)
private val E_BLANCO = Color(0xFFF0EEE8)
private val E_NARANJA = Color(0xFFE8843C)
private val E_ALUMINIO = Color(0xFFB4BCCC)
private val E_OSCURO = Color(0xFF4A5064)

/** El peto, sobre el lomo, con su cincha por debajo del pecho, y el aro del cuello. */
private val petoYAro: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val s = sin((PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()).coerceIn(0f, 1f)
    val dir = p.cadera - p.hombro; val largo = dir.getDistance().coerceAtLeast(0.1f)
    // Hacia el lomo: la perpendicular al cuerpo que mira hacia arriba.
    val lomo = Offset(-dir.y, dir.x).let { if (it.y > 0f) it * -1f else it } / largo
    val a = p.hombro + dir * 0.04f + lomo * 4.5f; val b = p.hombro + dir * 0.52f + lomo * 4.7f
    // La cincha, que baja del peto y rodea el pecho.
    val alto = p.hombro + dir * 0.26f + lomo * 4.4f; val bajo = p.hombro + dir * 0.2f - lomo * 4f
    drawLine(luz.tono(E_NARANJA), alto, bajo, 1.5f, StrokeCap.Round)
    // El peto: una manta corta, blanca, con el ribete naranja y la hebilla.
    drawLine(luz.tono(E_NARANJA), a, b, 4.4f, StrokeCap.Round)
    drawLine(Brush.linearGradient(listOf(luz.tono(E_BLANCO), luz.tono(Color(0xFFCDD0DA))), a, b), a, b, 3.2f, StrokeCap.Round)
    drawCircle(luz.tono(E_OSCURO), 0.8f, a + (b - a) * 0.5f)
    // El aro del cuello: una elipse de aluminio alrededor de la base de la cabeza.
    val c = p.cuello + Offset(0.4f * s, 0.6f)
    drawOval(luz.tono(E_OSCURO), Offset(c.x - 4.3f, c.y - 1.5f), Size(8.6f, 3f), style = Stroke(1.5f))
    drawOval(luz.tono(E_ALUMINIO), Offset(c.x - 4.3f, c.y - 1.9f), Size(8.6f, 3f), style = Stroke(1.1f))
    drawCircle(luz.tono(E_NARANJA), 0.7f, c + Offset(3.6f * s - 1.2f * (1f - s), 0.2f))
}

private fun poseBurbuja(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.2f + listOf(0f, -0.4f, -0.8f, -0.8f, -0.4f, 0f)[q]), morro = 0.3f, orejas = if (q in 2..3) 0.7f else 1f,
        ojos = if (q == 4) 0.3f else 1f, cola2 = listOf(2.1f, 2.3f, 2.6f, 2.6f, 2.3f, 2.1f)[q],
    )
}

/** La burbuja del casco: una bola de vidrio alrededor de la cabeza, con su brillo, apoyada en el aro. */
private val burbuja: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val r = Gatuno.R_CABEZA * 1.62f; val o = p.cabeza + Offset(0.2f, -1.2f + listOf(-7f, -3f, 0f, 0f, 0f, -3f)[q])
    drawCircle(Color(0xFFBFE0FF).copy(alpha = 0.16f), r, o)
    drawCircle(luz.tono(Color(0xFFD8ECFF)).copy(alpha = 0.8f), r, o, style = Stroke(0.7f))
    drawArc(Color.White.copy(alpha = 0.7f), 200f, 58f, false, Offset(o.x - r * 0.78f, o.y - r * 0.78f), Size(r * 1.56f, r * 1.56f), style = Stroke(0.9f, cap = StrokeCap.Round))
    drawArc(Color.White.copy(alpha = 0.3f), 20f, 40f, false, Offset(o.x - r * 0.8f, o.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(0.6f, cap = StrokeCap.Round))
}

private fun poseTuerca(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -9f), Offset(11.6f, -13f), Offset(13.6f, -16f), Offset(12.6f, -13f), Offset(10.4f, -10f), Offset(9f, -8f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.6f, -25f - 0.5f * (q % 2)), morro = listOf(0.3f, 0.2f, 0.12f, 0.16f, 0.24f, 0.3f)[q], orejas = 1f,
        ojos = if (q == 5) 0.3f else 1f, manoC = mano, cola2 = listOf(2.2f, 2.5f, 2.8f, 2.6f, 2.3f, 2.1f)[q],
    )
}

/** Una tuerca que flota delante de él, girando: le da con la mano y se va despacio hacia arriba, y vuelve. */
private val tuerca: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = Offset(16.6f + listOf(0f, 0.6f, 1.8f, 2.4f, 1.6f, 0.4f)[q], -17f + listOf(0f, -1f, -4f, -6f, -4f, -1f)[q])
    val ang = q * 0.52f
    val hex = Path().apply { for (i in 0..5) { val t = ang + i * (PI / 3).toFloat(); val x = sitio.x + cos(t) * 2.6f; val y = sitio.y + sin(t) * 2.6f * 0.8f; if (i == 0) moveTo(x, y) else lineTo(x, y) }; close() }
    drawPath(hex, Brush.horizontalGradient(listOf(luz.tono(Color(0xFF7A8296)), luz.tono(E_ALUMINIO), luz.tono(Color(0xFFF0F4FF))), sitio.x - 2.6f, sitio.x + 2.6f))
    drawOval(luz.tono(E_OSCURO), Offset(sitio.x - 1.1f, sitio.y - 0.9f), Size(2.2f, 1.8f))
    drawPath(hex, luz.tono(E_OSCURO).copy(alpha = 0.6f), style = Stroke(0.3f))
}

/** El gato de Espacio. */
internal val PELAJE_ESPACIO = Pelaje(
    manto = E_MANTO, claro = Color(0xFFE6E4E0), marca = Color(0xFF4E5466), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFE0A848),
    nariz = Color(0xFFD89A9A), accesorio = petoYAro,
    especiales = listOf(EspecialGato("se pone la burbuja del casco", true, 6, ::poseBurbuja, burbuja), EspecialGato("le da a una tuerca que flota", true, 6, ::poseTuerca, tuerca)),
)
