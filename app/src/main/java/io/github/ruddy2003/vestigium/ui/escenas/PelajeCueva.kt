package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Prehistoria: ocre dorado con manchas, de pechera blanca, con un colmillo colgado de una correa
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/cueva_*`): un gato de color ocre dorado, con manchas más oscuras y la
// pechera blanca, que lleva al cuello un colgante pequeño. Aquí el colgante es un colmillo perforado en su correa de
// cuero, como los del collar que Ama ensarta. Sus dos acciones propias: estampar su huella con ocre rojo en una laja
// (como las manos de la pared) y quedarse mirando las chispas que suben de la lumbre.

private val P_OCRE = Color(0xFFCF9E52)
private val P_MANCHA = Color(0xFF8A5A2E)
private val P_ROJO = Color(0xFFA4421F)
private val P_HUESO = Color(0xFFEFE4CC)
private val P_PIEDRA = Color(0xFFB0A493)

private fun unidadP(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** El colgante: un colmillo perforado, en una correa de cuero, por delante del cuello. De espaldas no se ve. */
private val colmillo: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    if (p.giro >= -0.25f) {
        val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
        val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
        val cuello = unidadP(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        // La correa, que rodea el cuello, y el colmillo, que cuelga.
        drawLine(luz.tono(Color(0xFF5E4030)), p.cuello + nuca * 3.2f, p.cuello - nuca * 3f, 0.8f, StrokeCap.Round)
        val sitio = p.cuello - nuca * (3.5f * s) + Offset(0f, 1.2f + 2.4f * co.coerceAtLeast(0f))
        drawPath(Path().apply { moveTo(sitio.x - 0.9f, sitio.y - 0.2f); lineTo(sitio.x + 0.9f, sitio.y - 0.2f); cubicTo(sitio.x + 1f, sitio.y + 2f, sitio.x + 0.4f, sitio.y + 3.4f, sitio.x - 0.5f, sitio.y + 4.2f); cubicTo(sitio.x - 0.4f, sitio.y + 2.6f, sitio.x - 1f, sitio.y + 1.4f, sitio.x - 0.9f, sitio.y - 0.2f); close() }, luz.tono(P_HUESO))
        drawCircle(luz.tono(P_ROJO), 0.5f, sitio + Offset(0f, 0.4f))
    }
}

private fun poseHuella(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado delante de la laja: moja la mano en el ocre, la apoya en la piedra, la retira y mira lo que ha hecho.
    val mano = listOf(Offset(9f, -1.6f), Offset(10.6f, -6f), Offset(13.6f, -13f), Offset(14.4f, -13.6f), Offset(11.6f, -8f), Offset(6f, 0f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f + listOf(0f, 0.3f, 0.7f, 0.7f, 0.4f, 0.2f)[q], -25.6f + listOf(0.6f, 0.2f, 0f, 0f, 0.2f, -0.4f)[q]), morro = listOf(0.34f, 0.2f, 0.06f, 0.06f, 0.1f, 0f)[q], orejas = 1f,
        ojos = if (q == 3) 0.4f else 1f, manoC = mano, cola2 = listOf(2.2f, 2.4f, 2.7f, 2.7f, 2.4f, 2.1f)[q],
    )
}

/** La laja, apoyada delante de él; la concha con el ocre molido; y las huellas rojas que va dejando. */
private val laja: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(13f, -1.2f), Size(15f, 2.4f))
    val piedra = Path().apply { moveTo(15.4f, 0f); lineTo(16.6f, -15f); lineTo(19.4f, -19.6f); lineTo(24.6f, -18.4f); lineTo(26.6f, -9f); lineTo(26f, 0f); close() }
    drawPath(piedra, luz.tono(P_PIEDRA)); drawPath(Path().apply { moveTo(22.6f, 0f); lineTo(23.6f, -18.6f); lineTo(24.6f, -18.4f); lineTo(26.6f, -9f); lineTo(26f, 0f); close() }, luz.tono(Color(0xFF857A6C)))
    // Una huella vieja, arriba; la de ahora aparece cuando retira la mano.
    fun huella(o: Offset, alfa: Float) {
        val tinta = luz.tono(P_ROJO).copy(alpha = alfa)
        drawOval(tinta, Offset(o.x - 1.3f, o.y - 0.4f), Size(2.6f, 2.2f))
        drawPoints(listOf(o + Offset(-1.5f, -1.4f), o + Offset(-0.5f, -2.1f), o + Offset(0.6f, -2.1f), o + Offset(1.6f, -1.3f)), PointMode.Points, tinta, 1f, StrokeCap.Round)
    }
    huella(Offset(20.6f, -14.4f), 0.75f)
    if (q >= 4 || q == 0) huella(Offset(19.4f, -8.6f), 0.95f)
    // La concha con el ocre.
    drawOval(luz.tono(P_HUESO), Offset(8.4f, -1.8f), Size(5.4f, 2.2f)); drawOval(luz.tono(P_ROJO), Offset(9.2f, -1.7f), Size(3.8f, 1.4f))
}

private fun poseChispas(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, quieto, con la cabeza que sube despacio detrás de una chispa; al final parpadea.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.8f + listOf(0f, 0.2f, 0.4f, 0.6f, 0.4f, 0.1f)[q], -26f - listOf(0f, 0.4f, 0.9f, 1.3f, 0.8f, 0.2f)[q]), morro = listOf(-0.1f, -0.24f, -0.4f, -0.54f, -0.4f, -0.16f)[q], orejas = 1f,
        ojos = if (q == 5) 0.3f else 1f, cola2 = listOf(2.3f, 2.4f, 2.5f, 2.6f, 2.5f, 2.3f)[q],
    )
}

/** Las chispas: tres o cuatro puntos de lumbre que suben delante de él, se mecen y se apagan. */
private val chispas: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, _, c ->
    val q = Math.floorMod(c, 6)
    for (k in 0..3) {
        val t = ((q + k * 1.6f) % 6f) / 6f
        val sitio = p.cabeza + Offset(11f + k * 2.6f + sin(t * 6.3f + k) * 2.2f, 6f - 22f * t)
        drawCircle(Color(0xFFFFB45C).copy(alpha = 0.3f * (1f - t)), 1.8f, sitio); drawCircle(Color(0xFFFFE2A8).copy(alpha = (1f - t * t).coerceIn(0f, 1f)), 0.7f, sitio)
    }
}

/** El gato de Prehistoria. */
internal val PELAJE_CUEVA = Pelaje(
    manto = P_OCRE, claro = Color(0xFFF7F0E0), marca = P_MANCHA, dibujo = DibujoPelaje.MANCHAS, ojos = Color(0xFF9DBE6E),
    nariz = Color(0xFFD08E84), collar = null, accesorio = colmillo,
    especiales = listOf(EspecialGato("estampa su huella", true, 6, ::poseHuella, laja), EspecialGato("mira las chispas", true, 6, ::poseChispas, chispas)),
)
