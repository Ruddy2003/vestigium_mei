package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

// =====================================================================
//  Mesoamérica: color de barro cocido, con su collar de jade y su pluma
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/meso_*`): un gato del color de la terracota, de vientre crema, con
// un collar de cuentas de jade y una pluma verde detrás de la oreja. Sus dos acciones propias: probar el cacao de un
// vaso (mete el hocico y se relame la espuma) y seguir con la cabeza y la mano a un colibrí que se le para delante.

private val M_BARRO = Color(0xFFC0714A)
private val M_JADE = Color(0xFF4F9A82)
private val M_PLUMA = Color(0xFF2F8E66)

/** La pluma verde, detrás de la oreja: sale de la cabeza hacia atrás y arriba. */
private val pluma: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val atras = if (p.cabeza.x >= p.cuello.x) -1f else 1f
    val a = p.cabeza + Offset(atras * 3.4f, -4.6f); val b = a + Offset(atras * 5.4f, -7.4f)
    drawPath(Path().apply { moveTo(a.x, a.y); cubicTo(a.x + atras * 4.4f, a.y - 1.4f, b.x + atras * 1.6f, b.y + 2.4f, b.x, b.y); cubicTo(b.x - atras * 2.6f, b.y + 1.6f, a.x - atras * 0.4f, a.y - 3.4f, a.x, a.y) }, luz.tono(M_PLUMA))
    drawLine(luz.tono(Color(0xFF9AD8B4)), a, b, 0.45f, StrokeCap.Round)
    drawCircle(luz.tono(Color(0xFFD8B048)), 0.8f, a)
}

private fun poseCacao(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado: baja el hocico al vaso, bebe y levanta la cabeza para relamerse.
    return Gatuno.SENTADO.copy(
        cabeza = listOf(Offset(6f, -23.6f), Offset(8.4f, -19.6f), Offset(9.4f, -17f), Offset(9.4f, -16.6f), Offset(7.6f, -21f), Offset(5.6f, -24.6f))[q],
        morro = listOf(0.3f, 0.7f, 0.95f, 0.95f, 0.5f, 0.1f)[q], orejas = listOf(1f, 0.9f, 0.8f, 0.8f, 0.9f, 1f)[q],
        ojos = listOf(1f, 0.7f, 0.2f, 0.2f, 0.6f, 0.4f)[q], cola2 = listOf(2.1f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El vaso de cacao, delante: alto, de barro pintado, con su espuma. */
private val vasoDeCacao: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6); val x = 16.4f
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(x - 4f, -0.8f), Size(8f, 1.6f))
    drawRect(luz.tono(Color(0xFFC48A56)), Offset(x - 2.8f, -9f), Size(5.6f, 9f)); drawRect(luz.tono(Color(0xFF8A5236)), Offset(x + 1.2f, -9f), Size(1.6f, 9f))
    drawRect(luz.tono(Color(0xFFB0503C)), Offset(x - 2.8f, -7.6f), Size(5.6f, 1f)); drawRect(luz.tono(Color(0xFF2E2622)), Offset(x - 2.8f, -2f), Size(5.6f, 0.6f))
    drawOval(luz.tono(Color(0xFFE8D4B8)), Offset(x - 2.8f, -9.9f), Size(5.6f, 1.8f))
    if (q == 2 || q == 3) drawCircle(luz.tono(Color(0xFFE8D4B8)), 0.6f, Offset(x - 3.6f + q * 0.3f, -10.6f))
}

private fun poseColibri(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la cabeza en alto siguiendo al pájaro; a mitad le lanza la mano, sin alcanzarlo.
    val mano = listOf(Offset(8.6f, -3f), Offset(10f, -7f), Offset(12.6f, -13f), Offset(13.4f, -15f), Offset(11f, -8f), Offset(8.8f, -3.2f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5f + listOf(0f, 0.4f, 0.8f, 1f, 0.5f, 0f)[q], -26.4f - listOf(0f, 0.4f, 0.8f, 0.8f, 0.4f, 0f)[q]), morro = listOf(-0.2f, -0.3f, -0.4f, -0.4f, -0.3f, -0.2f)[q], orejas = 1f,
        ojos = 1f, manoC = mano, cola2 = listOf(2f, 2.5f, 3f, 2.8f, 2.4f, 2f)[q],
    )
}

/** Un colibrí verde, quieto en el aire delante de él, que se aparta cuando le lanza la mano. */
private val colibri: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val o = Offset(18f + listOf(0f, 0.4f, 2f, 3.4f, 2f, 0.6f)[q], -21f - listOf(0f, 0.6f, 2.4f, 3.6f, 1.6f, 0.2f)[q] + sin(q * 2.1f) * 0.4f)
    drawOval(luz.tono(Color(0xFF2F8E66)), Offset(o.x - 1.8f, o.y - 1.1f), Size(3.8f, 2.2f)); drawCircle(luz.tono(Color(0xFF3AA87A)), 1f, o + Offset(-2.2f, -0.7f))
    drawLine(luz.tono(Color(0xFF2E2622)), o + Offset(-3f, -0.7f), o + Offset(-6f, -0.2f), 0.4f); drawCircle(luz.tono(Color(0xFFB0503C)), 0.5f, o + Offset(-2f, 0f))
    drawLine(luz.tono(Color(0xFF1E5E46)), o + Offset(1.8f, 0.2f), o + Offset(4f, 1.4f), 0.7f)
    // Las alas, que no se ven de tan deprisa: un borrón arriba o abajo.
    val arriba = q % 2 == 0
    drawOval(Color.White.copy(alpha = 0.5f), Offset(o.x - 1.4f, if (arriba) o.y - 4.4f else o.y + 0.4f), Size(3.4f, 3.8f))
}

/** El gato de Mesoamérica. */
internal val PELAJE_MESO = Pelaje(
    manto = M_BARRO, claro = Color(0xFFF3E4CC), marca = Color(0xFF8E4A30), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFFE0C048),
    nariz = Color(0xFFD9908A), collar = M_JADE, accesorio = pluma,
    especiales = listOf(EspecialGato("prueba el cacao", true, 6, ::poseCacao, vasoDeCacao), EspecialGato("sigue a un colibrí", true, 6, ::poseColibri, colibri)),
)
