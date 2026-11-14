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
//  Scriptorium: blanco como el pergamino, con un cordón rojo y una campanilla
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/medieval_*`): un gato blanco, del color del pergamino, con un pañuelo
// rojo de lunares. Aquí lleva un cordón rojo de rúbrica con una campanilla de bronce, para no repetir el pañuelo del
// gato de Roma. Sus dos acciones propias: cazar una pluma de ganso y dejar sus huellas de tinta en una hoja (los
// copistas se quejaban de los gatos en los márgenes de sus libros). **[Diseño propio]**.

private val M_ROJO = Color(0xFFB8432E)
private val M_BRONCE = Color(0xFFC89A48)

private fun unidadM(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** La campanilla, que cuelga del cordón por delante del cuello. De espaldas no se ve. */
private val campanilla: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    if (p.giro >= -0.25f) {
        val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
        val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
        val cuello = unidadM(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.4f * s) + Offset(0f, 2.2f + 2f * co.coerceAtLeast(0f))
        drawPath(Path().apply { moveTo(sitio.x - 0.8f, sitio.y - 1f); lineTo(sitio.x + 0.8f, sitio.y - 1f); lineTo(sitio.x + 1.9f, sitio.y + 2.2f); lineTo(sitio.x - 1.9f, sitio.y + 2.2f); close() }, luz.tono(M_BRONCE))
        drawCircle(luz.tono(Color(0xFF6A4E20)), 0.6f, sitio + Offset(0f, 2.6f))
        drawLine(luz.tono(Color(0xFFF2D684)), sitio + Offset(-0.6f, -0.4f), sitio + Offset(-1.2f, 1.6f), 0.4f)
    }
}

private fun posePluma(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la mano en alto, detrás de la pluma que cae.
    val mano = listOf(Offset(9f, -6f), Offset(11f, -11f), Offset(12.6f, -15f), Offset(13.4f, -11f), Offset(12f, -6f), Offset(9.6f, -3.4f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -25f - listOf(0f, 0.6f, 1.2f, 0.8f, 0.2f, 0f)[q]), morro = listOf(-0.2f, -0.4f, -0.5f, -0.3f, 0.1f, 0.3f)[q], orejas = 1f,
        ojos = 1f, manoC = mano, cola2 = listOf(2.2f, 2.6f, 2.9f, 2.6f, 2.2f, 2f)[q],
    )
}

/** Una pluma de ganso que baja meciéndose: le da con la mano y no la coge. */
private val plumaQueCae: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val o = p.cabeza + Offset(listOf(13f, 11f, 9.6f, 11.4f, 13.6f, 15f)[q], listOf(-16f, -12f, -8f, -3f, 3f, 9f)[q])
    val g = listOf(0.5f, -0.3f, 0.6f, -0.4f, 0.4f, 0f)[q]; val d = Offset(cos(g), sin(g)) * 5.4f; val lado = Offset(-d.y, d.x) * 0.3f
    drawPath(Path().apply { moveTo(o.x - d.x * 0.4f, o.y - d.y * 0.4f); cubicTo(o.x + lado.x * 2f, o.y + lado.y * 2f, o.x + d.x + lado.x, o.y + d.y + lado.y, o.x + d.x, o.y + d.y); cubicTo(o.x + d.x - lado.x, o.y + d.y - lado.y, o.x - lado.x * 1.4f, o.y - lado.y * 1.4f, o.x - d.x * 0.4f, o.y - d.y * 0.4f); close() }, luz.tono(Color.White))
    drawLine(luz.tono(Color(0xFFC8B48A)), o - d, o + d, 0.5f, StrokeCap.Round)
}

private fun poseHuellas(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, mirándose la mano manchada; al final la posa otra vez.
    val mano = listOf(Offset(8.6f, -1f), Offset(9.6f, -5f), Offset(9.6f, -9f), Offset(9.6f, -9.6f), Offset(10.6f, -5f), Offset(11.6f, -1f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.8f, -24f + listOf(0f, 0.6f, 1.2f, 1.2f, 0.6f, 0f)[q]), morro = listOf(0.4f, 0.5f, 0.56f, 0.56f, 0.5f, 0.4f)[q], orejas = listOf(1f, 0.8f, 0.6f, 0.6f, 0.8f, 1f)[q],
        ojos = if (q == 3) 0.3f else 1f, manoC = mano, cola2 = listOf(2f, 2.2f, 2.4f, 2.5f, 2.3f, 2f)[q],
    )
}

/** Una hoja de pergamino en el suelo, con las huellas de tinta que le va dejando. */
private val hojaConHuellas: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    drawOval(Color.Black.copy(alpha = 0.16f), Offset(5f, -0.6f), Size(24f, 2f))
    drawPath(Path().apply { moveTo(6f, -0.4f); lineTo(27f, -0.8f); lineTo(25f, -3.2f); lineTo(8f, -2.8f); close() }, luz.tono(Color(0xFFEFE3C2)))
    for (k in 0..1) drawLine(luz.tono(Color(0xFF2C2622)).copy(alpha = 0.5f), Offset(18f, -1.2f - k * 0.9f), Offset(25f, -1.4f - k * 0.9f), 0.3f)
    val tinta = luz.tono(Color(0xFF2C2622))
    for (k in 0 until (1 + q / 2).coerceAtMost(3)) { val o = Offset(10f + k * 3.2f, -1.6f - (k % 2) * 0.6f); drawCircle(tinta, 0.6f, o); for (dx in listOf(-0.7f, 0f, 0.7f)) drawCircle(tinta, 0.26f, o + Offset(dx, -0.8f)) }
}

/** El gato del scriptorium. */
internal val PELAJE_MEDIEVAL = Pelaje(
    manto = Color(0xFFF2EBDC), claro = Color(0xFFFFFBF2), marca = Color(0xFFD9CDB4), dibujo = DibujoPelaje.LISO, ojos = Color(0xFF6E9CC0),
    nariz = Color(0xFFE2A8A4), collar = M_ROJO, accesorio = campanilla,
    especiales = listOf(EspecialGato("caza una pluma", true, 6, ::posePluma, plumaQueCae), EspecialGato("deja huellas de tinta", true, 6, ::poseHuellas, hojaConHuellas)),
)
