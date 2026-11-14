package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Roma: gris travertino, atigrado, con un pañuelo rojo al cuello
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/urbe_*`): un gato de calle romano, gris cálido con rayas más oscuras y
// un pañuelo rojo anudado. Sus dos acciones propias: quedarse mirando hacia arriba, al fresco (o a las golondrinas), y
// jugar con un hisopo que rueda.

private val U_GRIS = Color(0xFFA6A197)
private val U_ROJO = Color(0xFFB4432E)

private fun unidadU(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** El nudo del pañuelo, con sus dos puntas, por delante del cuello. De espaldas no se ve. */
private val panuelo: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    if (p.giro >= -0.25f) {
        val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
        val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
        val cuello = unidadU(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.4f * s) + Offset(0f, 1.2f + 2.2f * co.coerceAtLeast(0f))
        drawPath(Path().apply { moveTo(sitio.x - 1.4f, sitio.y - 0.6f); lineTo(sitio.x + 1.6f, sitio.y - 0.4f); lineTo(sitio.x + 2.6f, sitio.y + 4.4f); lineTo(sitio.x + 0.4f, sitio.y + 2.2f); lineTo(sitio.x - 1.8f, sitio.y + 4f); close() }, luz.tono(U_ROJO))
        drawCircle(luz.tono(Color(0xFF8A3020)), 1f, sitio)
    }
}

private fun poseMiraArriba(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la cabeza echada atrás, como quien mira una bóveda; cada tanto, parpadea.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(3.6f + listOf(0f, 0.2f, 0.4f, 0.4f, 0.2f, 0f)[q], -27.6f - 0.3f * (q % 2)), morro = listOf(-0.7f, -0.78f, -0.86f, -0.86f, -0.78f, -0.7f)[q], orejas = 0.8f,
        ojos = if (q == 4) 0.2f else 1f, cola2 = listOf(2.2f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** Unas motas de polvo de la obra, que bajan en la luz: es lo que sigue con los ojos. */
private val motas: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, _, c ->
    val q = Math.floorMod(c, 6)
    for (k in 0..2) drawCircle(Color(0xFFFFF0D2).copy(alpha = 0.5f - 0.1f * k), 0.5f, p.cabeza + Offset(7f + k * 3f, -13f + q * 1.6f + k * 4f))
}

private fun poseHisopo(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.6f, -5.6f), Offset(13.8f, -2.4f), Offset(12.6f, -1f), Offset(10.2f, -2f), Offset(8.8f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.6f, -24.4f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.44f, 0.52f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un hisopo caído del andamio: le da con la mano y rueda un poco. */
private val hisopo: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.4f, 1.8f, 3f, 3.4f, 1.6f)[q]; val gira = listOf(0f, 0.2f, 0.8f, 1.5f, 1.9f, 2.2f)[q]
    val centro = Offset(17f + corre, -0.9f); val d = Offset(cos(gira), -sin(gira) * 0.35f) * 4.6f
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(centro.x - 5f, -0.6f), Size(10f, 1.3f))
    drawLine(luz.tono(Color(0xFFB98B56)), centro - d, centro + d, 0.7f, StrokeCap.Round)
    drawCircle(luz.tono(Color(0xFFF0E9D8)), 1.2f, centro + d); drawCircle(luz.tono(Color(0xFF6C6258)), 1.1f, centro - d)
}

/** El gato de Roma. */
internal val PELAJE_URBE = Pelaje(
    manto = U_GRIS, claro = Color(0xFFE9E3D6), marca = Color(0xFF6E685E), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFD9A441),
    nariz = Color(0xFFD99A98), collar = U_ROJO, accesorio = panuelo,
    especiales = listOf(EspecialGato("mira hacia arriba", true, 6, ::poseMiraArriba, motas), EspecialGato("juega con un hisopo", true, 6, ::poseHisopo, hisopo)),
)
