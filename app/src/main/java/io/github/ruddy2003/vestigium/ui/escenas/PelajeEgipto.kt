package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Egipto: dorado de arena, atigrado, con su collar ancho de cuentas
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/egipto_*`): un gato del color de la arena, con las marcas tostadas de
// los gatos del valle, que lleva un collar ancho de cuentas de loza turquesa, rojo y oro. Sus dos acciones propias:
// quedarse sentado muy tieso, como una estatua, con los ojos entornados; y empujar con la mano un escarabajo de loza,
// que resbala por el suelo.

private val E_ARENA = Color(0xFFD8B06C)
private val E_TURQUESA = Color(0xFF52B8AE)
private val E_ROJO = Color(0xFFB4482E)
private val E_ORO = Color(0xFFD6A63E)

private fun unidadE(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** El collar ancho: tres vueltas de cuentas (oro, turquesa y rojo) alrededor del cuello y, por delante, el colgante. */
private val collarAncho: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val cuello = unidadE(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
    for ((j, c) in listOf(E_ORO, E_TURQUESA, E_ROJO).withIndex()) { val baja = cuello * (-0.2f - j * 1.25f); val ancho = 3.1f + j * 0.45f
        drawLine(luz.tono(c), p.cuello + nuca * ancho + baja, p.cuello - nuca * ancho + baja, 1.25f, StrokeCap.Round) }
    if (p.giro >= -0.25f) {
        val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat(); val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
        val sitio = p.cuello - nuca * (3.6f * s) - cuello * 2.9f + Offset(0f, 1f + 2f * co.coerceAtLeast(0f))
        drawOval(luz.tono(E_TURQUESA), Offset(sitio.x - 1f, sitio.y - 0.4f), Size(2f, 2.8f)); drawCircle(luz.tono(E_ORO), 0.6f, sitio)
    }
}

private fun poseEstatua(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, muy erguido, con el morro alto y las orejas tiesas; casi no se mueve: entorna los ojos y la punta de la cola late.
    return Gatuno.SENTADO.copy(
        hombro = Offset(2.2f, -17.4f), cabeza = Offset(3.8f, -27.6f), morro = -0.16f, orejas = 1f,
        ojos = listOf(0.7f, 0.5f, 0.3f, 0.3f, 0.5f, 0.7f)[q], cola1 = 0.9f, cola2 = listOf(2.5f, 2.56f, 2.64f, 2.7f, 2.62f, 2.54f)[q],
    )
}

/** El rayo de sol que le da en el pecho mientras posa: unas motas doradas. */
private val motasDeSol: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    for (k in 0..3) drawCircle(Color(0xFFFFE9B0).copy(alpha = (0.5f - 0.08f * k) * (1f - 0.7f * luz.noche)), 0.5f, p.cabeza + Offset(8f + k * 2.6f - q * 0.6f, -10f + k * 4.4f + q * 1.2f))
}

private fun poseEscarabajo(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.4f, -5.4f), Offset(13.6f, -2.2f), Offset(12.4f, -1f), Offset(10.2f, -2f), Offset(8.8f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.8f, -24.2f + 0.4f * (q % 2)), morro = listOf(0.38f, 0.46f, 0.54f, 0.52f, 0.44f, 0.38f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un escarabajo de loza azul: le da con la mano y resbala un poco, girando sobre su panza lisa. */
private val escarabajo: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.3f, 2.2f, 3.8f, 4.4f, 2f)[q]; val gira = listOf(0f, 0.1f, 0.5f, 0.9f, 1.1f, 0.5f)[q]
    val centro = Offset(17f + corre, -1.7f); val u = Offset(cos(gira), sin(gira) * 0.5f)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(centro.x - 3.2f, -0.7f), Size(6.4f, 1.3f))
    drawOval(luz.tono(E_TURQUESA), Offset(centro.x - 2.8f, centro.y - 1.6f), Size(5.6f, 3.2f))
    drawLine(luz.tono(Color(0xFF2E7A78)), centro - u * 2.6f, centro + u * 2.6f, 0.4f); drawLine(luz.tono(Color(0xFF2E7A78)), centro + Offset(-u.y * 3f, u.x * 1.5f) * 0.5f, centro - Offset(-u.y * 3f, u.x * 1.5f) * 0.5f, 0.4f)
    drawLine(Color.White.copy(alpha = 0.55f), centro + Offset(-1.4f, -0.9f), centro + Offset(-0.2f, -1.2f), 0.4f)
}

/** El gato de Egipto. */
internal val PELAJE_EGIPTO = Pelaje(
    manto = E_ARENA, claro = Color(0xFFF4E6C4), marca = Color(0xFF8E6436), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFB8C860),
    nariz = Color(0xFFC88470), collar = null, accesorio = collarAncho,
    especiales = listOf(EspecialGato("posa como una estatua", true, 6, ::poseEstatua, motasDeSol), EspecialGato("juega con un escarabajo de loza", true, 6, ::poseEscarabajo, escarabajo)),
)
