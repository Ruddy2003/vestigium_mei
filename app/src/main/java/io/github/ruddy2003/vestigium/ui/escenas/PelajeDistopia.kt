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
//  Distopía: blanco hueso, con una oreja y el cerco de un ojo rojos, y el collar gris con su chapa
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/distopia_*`): blanco, con la oreja y el contorno del ojo de este lado
// en rojo. Aquí lleva además el collar gris de todos, con su chapa (sin número: una muesca). Sus dos acciones propias:
// quedarse mirando a la cámara y jugar con una octavilla.

private val D_HUESO = Color(0xFFE9E5D8)
private val D_ROJO = Color(0xFFB5372B)
private val D_GRIS = Color(0xFF7C8078)

private fun unidadD(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** La oreja roja, el cerco rojo del ojo y la chapa del collar. De espaldas solo se ve la oreja. */
private val marcasYChapa: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val r = Gatuno.R_CABEZA
    // La oreja de este lado, teñida de rojo por dentro.
    val oreja = p.cabeza + Offset(r * (0.5f * s + 0.52f * co.coerceAtLeast(0f) * (1f - s)) - (if (co < 0f) r * 0.5f else 0f), -r * 0.92f)
    drawPath(Path().apply { moveTo(oreja.x - 1.9f, oreja.y + 1.6f); lineTo(oreja.x + 0.5f, oreja.y - 3.6f); lineTo(oreja.x + 2.2f, oreja.y + 1.4f); close() }, luz.tono(D_ROJO))
    if (p.giro >= -0.25f) {
        // El cerco del ojo.
        val ojo = p.cabeza + Offset(r * (0.44f * s + 0.46f * co.coerceAtLeast(0f) * (1f - s)), 0.3f)
        drawOval(luz.tono(D_ROJO), Offset(ojo.x - r * 0.3f, ojo.y - r * 0.32f), Size(r * 0.6f, r * 0.64f), style = Stroke(0.8f))
        drawLine(luz.tono(D_ROJO), ojo + Offset(0f, r * 0.32f), ojo + Offset(-0.3f, r * 0.62f), 0.8f, StrokeCap.Round)
        // La chapa, que cuelga del collar por delante del cuello.
        val cuello = unidadD(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.5f * s) + Offset(0f, 1.4f + 2.4f * co.coerceAtLeast(0f))
        drawRoundRect(luz.tono(Color(0xFF8A9096)), sitio + Offset(-1.3f, -0.4f), Size(2.6f, 2.8f), androidx.compose.ui.geometry.CornerRadius(0.5f))
        drawLine(luz.tono(Color(0xFF3C3A38)), sitio + Offset(-0.6f, 1.1f), sitio + Offset(0.6f, 1.1f), 0.4f)
    }
}

private fun poseCamara(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado muy tieso, con la cabeza alzada hacia la cámara; cada tanto, parpadea.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.4f + listOf(0f, 0.2f, 0.3f, 0.3f, 0.2f, 0f)[q], -27f - 0.3f * (q % 2)), morro = listOf(-0.42f, -0.46f, -0.5f, -0.5f, -0.46f, -0.42f)[q], orejas = 1f,
        ojos = if (q == 4) 0.2f else 1f, cola2 = listOf(2.3f, 2.3f, 2.4f, 2.5f, 2.4f, 2.3f)[q],
    )
}

/** El piloto rojo de la cámara, reflejado en el suelo delante de él: es lo que mira. */
private val pilotoDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val late = listOf(0.5f, 0.7f, 1f, 1f, 0.7f, 0.5f)[q]
    drawOval(Color(0xFFFF4A3A).copy(alpha = 0.22f * late + 0.1f * luz.noche), Offset(13f, -1.4f), Size(9f, 2.4f))
    drawCircle(Color(0xFFFF4A3A).copy(alpha = 0.7f * late), 0.9f, Offset(17.4f, -0.4f))
}

private fun poseOctavilla(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.6f), Offset(11.4f, -6f), Offset(13.6f, -2.6f), Offset(12.4f, -1f), Offset(10f, -2f), Offset(8.6f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -24.6f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.42f, 0.5f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Una octavilla que el viento trajo: le da con la mano, se levanta de una punta y vuelve a caer. */
private val octavilla: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val alza = listOf(0f, 0.6f, 2.6f, 3.4f, 1.6f, 0.2f)[q]; val corre = listOf(0f, 0.3f, 1.4f, 2.2f, 2.4f, 1f)[q]
    val a = Offset(13.4f + corre, -0.4f); val b = a + Offset(7f, -alza)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(a.x - 0.6f, -0.7f), Size(8.4f, 1.3f))
    drawPath(Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(b.x - 0.8f, b.y - 2.6f - alza * 0.2f); lineTo(a.x - 1.2f, a.y - 2.4f); close() }, luz.tono(Color(0xFFEDE6D0)))
    drawLine(luz.tono(D_ROJO), a + Offset(0.4f, -1.9f), b + Offset(-1f, -2f - alza * 0.2f), 0.7f)
    drawLine(luz.tono(Color(0xFF3C3A38)).copy(alpha = 0.5f), a + Offset(0.6f, -1f), b + Offset(-1.6f, -1f), 0.3f)
}

/** El gato de Distopía. */
internal val PELAJE_DISTOPIA = Pelaje(
    manto = D_HUESO, claro = Color(0xFFF6F3EA), marca = Color(0xFFC9C5B8), dibujo = DibujoPelaje.LISO, ojos = Color(0xFFD9584A),
    nariz = Color(0xFFD99A98), collar = D_GRIS, accesorio = marcasYChapa,
    especiales = listOf(EspecialGato("mira a la cámara", true, 6, ::poseCamara, pilotoDelGato), EspecialGato("juega con una octavilla", true, 6, ::poseOctavilla, octavilla)),
)
