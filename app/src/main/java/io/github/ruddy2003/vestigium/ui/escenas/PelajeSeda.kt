package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Ruta de la Seda: color de arena, con su mantilla roja de ribete añil
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/seda_*`): un gato del color de los camellos, de puntas algo más
// tostadas, que lleva al lomo una mantilla como la de una albarda: roja, con el ribete añil. Sus dos acciones propias:
// oler el viento con el morro en alto (los camelleros viejos huelen la tormenta un día antes) y darle a una moneda de
// cobre, de las de agujero cuadrado, que rueda.

private val S_ARENA = Color(0xFFDCC19A)
private val S_ROJO = Color(0xFF9A3430)
private val S_ANIL = Color(0xFF3E6682)

private fun unidadS(v: Offset, siNo: Offset = Offset(1f, 0f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** La mantilla, echada sobre el lomo entre el hombro y la cadera. De frente y de espaldas se ve como una franja. */
private val mantilla: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val d = p.cadera - p.hombro; val u = unidadS(d); val largo = sqrt(d.x * d.x + d.y * d.y)
    if (largo > 3f) {
        // Hacia arriba del lomo: la perpendicular que mira al cielo.
        val n = Offset(-u.y, u.x).let { if (it.y > 0f) Offset(-it.x, -it.y) else it }
        val a = p.hombro + u * (largo * 0.12f); val b = p.hombro + u * (largo * 0.9f)
        drawPath(Path().apply { moveTo(a.x + n.x * 4.4f, a.y + n.y * 4.4f); lineTo(b.x + n.x * 4.6f, b.y + n.y * 4.6f); lineTo(b.x - n.x * 1.6f, b.y - n.y * 1.6f); lineTo(a.x - n.x * 1.6f, a.y - n.y * 1.6f); close() }, luz.tono(S_ROJO))
        drawLine(luz.tono(S_ANIL), a - n * 1.6f, b - n * 1.6f, 1.3f, StrokeCap.Round)
        drawLine(luz.tono(Color(0xFFD2A244)), a + n * 1.6f, b + n * 1.6f, 0.5f)
    }
}

private fun poseHuele(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con el morro en alto, olfateando; las orejas se le van hacia atrás con el viento.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.6f + listOf(0f, 0.3f, 0.6f, 0.6f, 0.3f, 0f)[q], -27.2f - 0.3f * (q % 2)), morro = listOf(-0.5f, -0.6f, -0.7f, -0.66f, -0.58f, -0.5f)[q], orejas = listOf(0.9f, 0.8f, 0.6f, 0.6f, 0.8f, 0.9f)[q],
        ojos = if (q == 3) 0.3f else 0.8f, cola2 = listOf(2.2f, 2.4f, 2.6f, 2.5f, 2.3f, 2.2f)[q],
    )
}

/** Unos granos de arena que pasan con el viento. */
private val arena: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, _, c ->
    val q = Math.floorMod(c, 6)
    for (k in 0..3) drawCircle(Color(0xFFE8CC94).copy(alpha = 0.6f - 0.1f * k), 0.5f, p.cabeza + Offset(16f - q * 3.2f - k * 4f, -8f + k * 2.4f + (q % 2)))
}

private fun poseMoneda(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.6f, -5.6f), Offset(13.8f, -2.4f), Offset(12.6f, -1f), Offset(10.2f, -2f), Offset(8.8f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.6f, -24.4f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.44f, 0.52f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Una moneda de cobre con su agujero cuadrado: le da con la mano y rueda de canto un poco. */
private val moneda: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.4f, 2f, 3.6f, 4.4f, 2f)[q]; val gira = listOf(0f, 0.3f, 0.9f, 1.5f, 2.1f, 2.7f)[q]
    val centro = Offset(17f + corre, -2.6f); val ancho = 2.6f * (0.35f + 0.65f * kotlin.math.abs(cos(gira)))
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(centro.x - 3f, -0.6f), Size(6f, 1.2f))
    drawOval(luz.tono(Color(0xFFC89A46)), Offset(centro.x - ancho, centro.y - 2.6f), Size(ancho * 2f, 5.2f))
    drawRect(luz.tono(Color(0xFF5C4232)), Offset(centro.x - ancho * 0.3f, centro.y - 0.8f), Size(ancho * 0.6f, 1.6f))
    drawLine(Color.White.copy(alpha = 0.5f), centro + Offset(-ancho * 0.6f, -1.6f + sin(gira) * 0.3f), centro + Offset(-ancho * 0.3f, -2.2f), 0.4f)
}

/** El gato de la Ruta de la Seda. */
internal val PELAJE_SEDA = Pelaje(
    manto = S_ARENA, claro = Color(0xFFF3E8D2), marca = Color(0xFFA9865C), dibujo = DibujoPelaje.PUNTAS, ojos = Color(0xFF7FAE9A),
    nariz = Color(0xFFD99A98), collar = null, accesorio = mantilla,
    especiales = listOf(EspecialGato("huele el viento", true, 6, ::poseHuele, arena), EspecialGato("juega con una moneda", true, 6, ::poseMoneda, moneda)),
)
