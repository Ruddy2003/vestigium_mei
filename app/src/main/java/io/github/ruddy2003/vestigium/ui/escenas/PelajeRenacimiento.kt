package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Renacimiento: color de papel, con rayas de sepia y la mejilla de sanguina
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/renacimiento_*`): un gato dibujado como en un cuaderno, del color del
// papel, con el sombreado a rayas de tinta sepia y un toque de sanguina en la cara. Aquí es el gato del taller: crema,
// atigrado en sepia claro, con una oreja manchada de azul de ultramar, que es lo que le pasa a quien duerme entre botes
// de color. Sus dos acciones propias: seguir con los ojos las motas de polvo del haz de sol y jugar con un pincel que
// rueda.

private val R_PAPEL = Color(0xFFE6D6B4)
private val R_SEPIA = Color(0xFFA98A60)
private val R_ULTRAMAR = Color(0xFF2F4E9A)
private val R_SANGUINA = Color(0xFFC2664A)

/** La mancha de azul en la oreja y el toque de sanguina en la mejilla. De espaldas no se ve la mejilla. */
private val manchas: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    drawCircle(luz.tono(R_ULTRAMAR).copy(alpha = 0.85f), 1.3f, p.cabeza + Offset(-2.6f, -6.4f))
    drawCircle(luz.tono(R_ULTRAMAR).copy(alpha = 0.6f), 0.7f, p.cabeza + Offset(-0.8f, -7.4f))
    if (p.giro >= -0.25f) drawOval(luz.tono(R_SANGUINA).copy(alpha = 0.5f), p.cabeza + Offset(1.2f, 0.4f), Size(3f, 1.8f))
}

private fun poseMiraElHaz(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la cabeza ladeada hacia arriba, siguiendo algo que baja despacio; cada tanto, parpadea.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4f + listOf(0f, 0.3f, 0.6f, 0.6f, 0.3f, 0f)[q], -27f - 0.3f * (q % 2)), morro = listOf(-0.5f, -0.58f, -0.66f, -0.66f, -0.58f, -0.5f)[q], orejas = 0.9f,
        ojos = if (q == 4) 0.2f else 1f, cola2 = listOf(2.2f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** Las motas de polvo de yeso que bajan en la luz. */
private val motas: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, _, c ->
    val q = Math.floorMod(c, 6)
    for (k in 0..3) drawCircle(Color(0xFFFFF0D2).copy(alpha = 0.55f - 0.1f * k), 0.5f, p.cabeza + Offset(6f + k * 2.6f, -14f + q * 1.5f + k * 3.6f))
}

private fun posePincel(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.6f, -5.6f), Offset(13.8f, -2.4f), Offset(12.6f, -1f), Offset(10.2f, -2f), Offset(8.8f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.6f, -24.4f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.44f, 0.52f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un pincel caído de la mesa: le da con la mano y rueda, dejando una raya de color. */
private val pincel: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.4f, 1.8f, 3f, 3.4f, 1.6f)[q]; val gira = listOf(0f, 0.2f, 0.8f, 1.5f, 1.9f, 2.2f)[q]
    val centro = Offset(17f + corre, -0.9f); val d = Offset(cos(gira), -sin(gira) * 0.35f) * 5f
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(centro.x - 5.4f, -0.6f), Size(10.8f, 1.3f))
    drawLine(luz.tono(R_SANGUINA).copy(alpha = 0.6f), Offset(15f, -0.2f), Offset(15f + corre + 2f, -0.2f), 0.6f, StrokeCap.Round)
    drawLine(luz.tono(Color(0xFFB08A58)), centro - d, centro + d * 0.6f, 0.8f, StrokeCap.Round)
    drawLine(luz.tono(R_SANGUINA), centro + d * 0.6f, centro + d, 1.3f, StrokeCap.Round)
}

/** El gato del taller de Florencia. */
internal val PELAJE_RENACIMIENTO = Pelaje(
    manto = R_PAPEL, claro = Color(0xFFF6EEDA), marca = R_SEPIA, dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFC79A3C),
    nariz = Color(0xFFD99A8A), accesorio = manchas,
    especiales = listOf(EspecialGato("sigue las motas del haz de sol", true, 6, ::poseMiraElHaz, motas), EspecialGato("juega con un pincel", true, 6, ::posePincel, pincel)),
)
