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
//  Pirata: atigrado naranja, de pecho blanco y ojos de ámbar, con pañuelo rojo y parche en un ojo
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/pirata_*`): rayado en naranja, con el pecho blanco, un pañuelo rojo
// anudado a la nuca y un parche negro. Sus dos acciones propias: otear con el catalejo y jugar con un doblón.

private val P_MANTO = Color(0xFFE39A4C)
private val P_ROJO = Color(0xFFB8382E)
private val P_ROJO_LUZ = Color(0xFFE0604A)
private val P_ROJO_OSC = Color(0xFF7A2420)
private val P_LATON = Color(0xFFDAB556)
private val P_NEGRO = Color(0xFF1E1A1C)

/**
 * El pañuelo y el parche. El pañuelo es un casquete rojo que ciñe lo alto de la cabeza, entre las orejas, con el nudo
 * y sus dos puntas a la nuca; el parche tapa el ojo de este lado, con su cinta. De espaldas solo se ve el nudo.
 */
private val panueloYParche: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val r = Gatuno.R_CABEZA
    val c = p.cabeza + Offset(-0.3f * s, -2.6f)
    // El nudo y sus dos puntas, a la nuca (de frente quedan detrás de la cabeza y no se ven).
    if (co < 0.8f) {
        val nudo = c + Offset(-r * 0.98f * (if (co < 0f) 0.3f else s), -0.2f)
        for (d in listOf(Offset(-4.4f, 3.4f), Offset(-2.2f, 5.4f))) drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + d.x * 0.4f, nudo.y + d.y * 0.1f, nudo.x + d.x * 0.8f, nudo.y + d.y * 0.6f, nudo.x + d.x, nudo.y + d.y) }, luz.tono(P_ROJO_OSC), style = Stroke(1.5f, cap = StrokeCap.Round))
        drawCircle(luz.tono(P_ROJO), 1.4f, nudo)
    }
    // El casquete: de sien a sien, bajo en la frente.
    val casquete = Path().apply {
        moveTo(c.x - r * 1.02f, c.y + 1.2f); cubicTo(c.x - r * 1.1f, c.y - 2.6f, c.x - r * 0.5f, c.y - 4.6f, c.x, c.y - 4.5f)
        cubicTo(c.x + r * 0.5f, c.y - 4.6f, c.x + r * 1.1f, c.y - 2.6f, c.x + r * 1.02f, c.y + 1.2f)
        cubicTo(c.x + r * 0.5f, c.y - 0.6f, c.x - r * 0.5f, c.y - 0.6f, c.x - r * 1.02f, c.y + 1.2f); close()
    }
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    drawPath(casquete, Brush.horizontalGradient(if (lado > 0f) listOf(luz.tono(P_ROJO_OSC), luz.tono(P_ROJO), luz.tono(P_ROJO_LUZ)) else listOf(luz.tono(P_ROJO_LUZ), luz.tono(P_ROJO), luz.tono(P_ROJO_OSC)), c.x - r, c.x + r))
    clipPath(casquete) { for (i in 0..1) drawPath(Path().apply { moveTo(c.x - r * 1.2f, c.y - 1.6f + i * 1.6f); cubicTo(c.x - r * 0.4f, c.y - 3.6f + i * 1.6f, c.x + r * 0.4f, c.y - 3.6f + i * 1.6f, c.x + r * 1.2f, c.y - 1.6f + i * 1.6f) }, luz.tono(P_ROJO_OSC).copy(alpha = 0.6f), style = Stroke(0.4f)) }
    // El parche y su cinta: de perfil, sobre el ojo que se ve; de frente, sobre el de este lado.
    if (p.giro >= -0.25f) {
        val ojo = p.cabeza + Offset(r * (0.44f * s + 0.46f * co.coerceAtLeast(0f) * (1f - s)), 0.3f)
        drawLine(luz.tono(P_NEGRO), ojo + Offset(-r * 0.2f, -1.4f), c + Offset(-r * 0.5f * s - r * 0.6f * (1f - s), -1.4f), 0.6f)
        drawLine(luz.tono(P_NEGRO), ojo + Offset(r * 0.2f, -1.2f), c + Offset(r * 0.9f, -0.2f), 0.6f)
        drawOval(luz.tono(P_NEGRO), Offset(ojo.x - r * 0.3f, ojo.y - r * 0.34f), Size(r * 0.6f, r * 0.66f))
        drawOval(luz.tono(Color(0xFF4A444C)).copy(alpha = 0.7f), Offset(ojo.x - r * 0.2f, ojo.y - r * 0.28f), Size(r * 0.22f, r * 0.2f))
    }
}

private fun poseCatalejo(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f + listOf(0f, 0.3f, 0.6f, 0.6f, 0.3f, 0f)[q], -25.6f + 0.3f * (q % 2)), morro = listOf(0.2f, 0.16f, 0.1f, 0.1f, 0.16f, 0.2f)[q], orejas = 1f,
        ojos = if (q == 3) 0.3f else 1f, manoC = Offset(11.4f, -22.4f), manoL = Offset(9f, -12f), cola2 = listOf(2.3f, 2.5f, 2.7f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El catalejo de latón, que sostiene con una mano delante del ojo bueno y va barriendo el horizonte. */
private val catalejoDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val alza = listOf(-0.5f, -1f, -1.6f, -1.6f, -1f, -0.5f)[q]
    val a = p.cabeza + Offset(4.6f, 0.4f); val b = a + Offset(13f, alza)
    val d = (b - a).let { it / it.getDistance() }; val n = Offset(-d.y, d.x)
    fun tramo(k0: Float, k1: Float, g: Float, propio: Color) { val f = Path().apply { val p0 = a + (b - a) * k0; val p1 = a + (b - a) * k1; moveTo(p0.x + n.x * g, p0.y + n.y * g); lineTo(p1.x + n.x * g, p1.y + n.y * g); lineTo(p1.x - n.x * g, p1.y - n.y * g); lineTo(p0.x - n.x * g, p0.y - n.y * g); close() }; drawPath(f, luz.tono(propio)) }
    tramo(0f, 0.46f, 1.7f, Color(0xFF6A4630)); tramo(0.46f, 0.78f, 1.4f, P_LATON); tramo(0.78f, 1f, 1.1f, P_LATON)
    for (k in listOf(0f, 0.46f, 0.78f, 1f)) { val o = a + (b - a) * k; drawLine(luz.tono(Color(0xFF8A6A22)), o + n * 1.9f, o - n * 1.9f, 0.6f) }
    drawLine(Color.White.copy(alpha = 0.5f), a + (b - a) * 0.5f - n * 0.7f, b - n * 0.5f, 0.3f)
}

private fun poseDoblon(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -4f), Offset(11.6f, -6.6f), Offset(13.8f, -3.2f), Offset(12.6f, -1.2f), Offset(10f, -2.4f), Offset(8.6f, -3.4f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -24.6f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.42f, 0.5f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un doblón de oro, que rueda de canto cuando le da con la mano y vuelve. */
private val doblon: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = Offset(15.8f + listOf(0f, 0.4f, 2f, 3f, 2.2f, 0.8f)[q], -2.6f)
    val canto = listOf(1f, 0.8f, 0.4f, 0.7f, 1f, 0.9f)[q]
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(sitio.x - 2.6f, -0.7f), Size(5.4f, 1.2f))
    val moneda = Rect(sitio.x - 2.6f * canto, sitio.y - 2.6f, sitio.x + 2.6f * canto, sitio.y + 2.6f)
    drawOval(luz.tono(Color(0xFF9A7426)), moneda.topLeft + Offset(0.3f, 0.3f), moneda.size)
    drawOval(Brush.horizontalGradient(listOf(luz.tono(Color(0xFFB8922E)), luz.tono(P_LATON), luz.tono(Color(0xFFFFF0B0))), moneda.left, moneda.right), moneda.topLeft, moneda.size)
    drawOval(luz.tono(Color(0xFF9A7426)).copy(alpha = 0.7f), Offset(moneda.left + moneda.width * 0.2f, moneda.top + 0.9f), Size(moneda.width * 0.6f, moneda.height - 1.8f), style = Stroke(0.3f))
}

/** El gato de Pirata. */
internal val PELAJE_PIRATA = Pelaje(
    manto = P_MANTO, claro = Color(0xFFF8EEDC), marca = Color(0xFFB8682A), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFD9B24A),
    nariz = Color(0xFFE0A0A0), accesorio = panueloYParche,
    especiales = listOf(EspecialGato("otea con el catalejo", true, 6, ::poseCatalejo, catalejoDelGato), EspecialGato("juega con un doblón", true, 6, ::poseDoblon, doblon)),
)
