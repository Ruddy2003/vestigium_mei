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
//  Horror cósmico: negro, de ojos amarillo verdoso, con collar de cuero y un amuleto verde mar
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/arcano_*`): negro entero, con los ojos de un amarillo verdoso que se
// ven de lejos, un collar pardo y un colgante verde azulado. Aquí el colgante es un trozo de vidrio de mar en su
// alambre. Sus dos acciones propias: seguir con la mirada una polilla de las que rondan la lámpara y quedarse
// erizado mirando un punto de la niebla donde no hay nada.

private val A_NEGRO = Color(0xFF22262A)
private val A_CUERO = Color(0xFF7A5234)
private val A_VIDRIO = Color(0xFF5FA79A)

private fun unidadA(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** El amuleto: un vidrio de mar, verde, colgado del collar por delante del cuello. De espaldas no se ve. */
private val amuleto: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    if (p.giro >= -0.25f) {
        val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
        val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
        val cuello = unidadA(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.5f * s) + Offset(0f, 1.6f + 2.4f * co.coerceAtLeast(0f))
        drawLine(luz.tono(Color(0xFFB8A070)), sitio + Offset(0f, -1.4f), sitio + Offset(0f, 0.2f), 0.4f)
        drawPath(Path().apply { moveTo(sitio.x, sitio.y - 0.2f); lineTo(sitio.x + 1.5f, sitio.y + 1.6f); lineTo(sitio.x + 0.3f, sitio.y + 3.6f); lineTo(sitio.x - 1.4f, sitio.y + 1.9f); close() }, luz.tono(A_VIDRIO))
        drawLine(Color.White.copy(alpha = 0.55f * (1f - 0.5f * luz.noche)), sitio + Offset(-0.5f, 1f), sitio + Offset(0.2f, 0.3f), 0.35f)
    }
}

private fun posePolilla(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la cabeza siguiendo el vuelo; a mitad, le tira un manotazo que no llega.
    val mano = listOf(Offset(5f, 0f), Offset(5f, 0f), Offset(9f, -14f), Offset(11.4f, -20f), Offset(9f, -10f), Offset(5f, 0f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.6f + listOf(0f, 0.4f, 0.8f, 1f, 0.6f, 0.2f)[q], -26.4f - listOf(0f, 0.4f, 0.8f, 1f, 0.5f, 0f)[q]), morro = listOf(-0.3f, -0.42f, -0.56f, -0.6f, -0.46f, -0.3f)[q], orejas = 1f,
        ojos = 1f, manoC = mano, cola2 = listOf(2.3f, 2.5f, 2.8f, 2.9f, 2.6f, 2.3f)[q],
    )
}

/** La polilla: da vueltas por encima de su cabeza, clara, con las alas borrosas. */
private val polilla: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = p.cabeza + listOf(Offset(9f, -9f), Offset(11f, -11.6f), Offset(12.4f, -9.4f), Offset(11.4f, -7f), Offset(13f, -10.4f), Offset(10.4f, -11f))[q]
    val ala = if (q % 2 == 0) 2.6f else 1.2f
    val color = luz.tono(Color(0xFFE8DEBC))
    drawOval(color.copy(alpha = 0.95f), sitio + Offset(-2.8f, -ala), Size(2.8f, ala * 1.4f)); drawOval(color.copy(alpha = 0.95f), sitio + Offset(0f, -ala), Size(2.8f, ala * 1.4f))
    drawLine(luz.tono(Color(0xFF6E5640)), sitio + Offset(0f, -0.6f), sitio + Offset(0f, 0.8f), 0.5f, StrokeCap.Round)
}

private fun poseNiebla(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Tieso, erizado, con las orejas atrás y la cola hinchada: mira fijo hacia delante, y al final parpadea y lo deja.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f + listOf(0f, 0.3f, 0.5f, 0.5f, 0.3f, 0f)[q], -25.6f), morro = listOf(0.06f, 0.02f, 0f, 0f, 0.02f, 0.06f)[q],
        orejas = listOf(0.6f, 0f, -0.6f, -0.7f, -0.2f, 0.6f)[q], ojos = if (q == 5) 0.3f else 1f, erizo = listOf(0f, 0.4f, 0.8f, 0.9f, 0.5f, 0.1f)[q],
        cola1 = listOf(0.75f, 0.9f, 1.1f, 1.1f, 0.9f, 0.75f)[q], cola2 = listOf(2.3f, 2f, 1.7f, 1.7f, 2f, 2.3f)[q],
    )
}

/** Lo que mira: nada. Solo una hebra de niebla que se riza delante de él y se deshace. */
private val hebraDeNiebla: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val alfa = listOf(0.1f, 0.25f, 0.4f, 0.42f, 0.25f, 0.08f)[q]
    val color = luz.tono(Color(0xFFDCE4DA)).copy(alpha = alfa)
    val x = 22f + q * 0.6f
    drawPath(Path().apply { moveTo(x, -2f); cubicTo(x + 5f, -6f, x - 3f, -11f, x + 3f, -16f); cubicTo(x + 6f, -19f, x + 1f, -22f, x + 4f, -25f) }, color, style = Stroke(2.2f, cap = StrokeCap.Round))
    drawPath(Path().apply { moveTo(x + 6f, -1f); cubicTo(x + 10f, -5f, x + 4f, -9f, x + 9f, -13f) }, color.copy(alpha = alfa * 0.7f), style = Stroke(1.6f, cap = StrokeCap.Round))
}

/** El gato de Horror cósmico. */
internal val PELAJE_ARCANO = Pelaje(
    manto = A_NEGRO, claro = Color(0xFF30363A), marca = Color(0xFF14181A), dibujo = DibujoPelaje.LISO, ojos = Color(0xFFCCDA52),
    nariz = Color(0xFF4A3E40), collar = A_CUERO, accesorio = amuleto,
    especiales = listOf(EspecialGato("sigue una polilla", true, 6, ::posePolilla, polilla), EspecialGato("se eriza mirando la niebla", true, 6, ::poseNiebla, hebraDeNiebla)),
)
