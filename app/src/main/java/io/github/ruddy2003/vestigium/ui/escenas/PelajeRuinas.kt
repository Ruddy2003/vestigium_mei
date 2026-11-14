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
//  Ciudad verde: una gata de tres colores (blanca, con manchas naranjas y negras), con un cordel al cuello y una hoja
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/ruinas_*`): blanco, con una mancha naranja en el lomo y en la cabeza,
// otra negra en la grupa, la punta de la cola negra, los ojos verdes y un cordel de cáñamo al cuello del que cuelga
// una hoja. Sus dos acciones propias: oler un brote que ha salido en una grieta y jugar con un vilano.

private val R_BLANCO = Color(0xFFF2ECDC)
private val R_NARANJA = Color(0xFFDC8A3C)
private val R_NEGRO = Color(0xFF2E2A2A)
private val R_CORDEL = Color(0xFFB89A62)
private val R_HOJA = Color(0xFF4E8A40)

private fun unidadR(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/** La mancha negra de la grupa, la punta negra de la cola, la oreja negra y la hoja que cuelga del cordel. */
private val manchasYHoja: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    val r = Gatuno.R_CABEZA
    val negro = luz.tono(R_NEGRO)
    // La grupa y la punta de la cola.
    drawOval(negro, Offset(p.cadera.x - 4.6f, p.cadera.y - 5.4f), Size(6.4f, 5.6f))
    drawCircle(negro, 1.5f, p.puntaCola); drawCircle(negro, 1.3f, p.puntaCola + unidadR(p.cadera - p.puntaCola) * 1.6f)
    // La oreja de este lado, negra.
    val oreja = p.cabeza + Offset(r * (0.5f * s + 0.52f * co.coerceAtLeast(0f) * (1f - s)) - (if (co < 0f) r * 0.5f else 0f), -r * 0.92f)
    drawPath(Path().apply { moveTo(oreja.x - 1.9f, oreja.y + 1.6f); lineTo(oreja.x + 0.5f, oreja.y - 3.6f); lineTo(oreja.x + 2.2f, oreja.y + 1.4f); close() }, negro)
    if (p.giro >= -0.25f) {
        // La hoja, que cuelga del cordel por delante del cuello.
        val cuello = unidadR(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.5f * s) + Offset(0f, 1.6f + 2.4f * co.coerceAtLeast(0f))
        drawLine(luz.tono(R_CORDEL), sitio + Offset(0f, -1.4f), sitio, 0.5f)
        drawPath(Path().apply { moveTo(sitio.x, sitio.y); cubicTo(sitio.x - 2.2f, sitio.y + 0.8f, sitio.x - 1.8f, sitio.y + 3f, sitio.x, sitio.y + 4f); cubicTo(sitio.x + 1.8f, sitio.y + 3f, sitio.x + 2.2f, sitio.y + 0.8f, sitio.x, sitio.y); close() }, luz.tono(R_HOJA))
        drawLine(luz.tono(Color(0xFF2E5A2E)), sitio + Offset(0f, 0.4f), sitio + Offset(0f, 3.6f), 0.35f)
    }
}

private fun poseBrote(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentada, con la cabeza baja hacia el brote: lo huele, se aparta, vuelve.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(7.4f + listOf(0f, 0.6f, 1f, 1f, 0.4f, 0f)[q], -19f + listOf(0f, 1f, 1.8f, 1.8f, 0.8f, 0f)[q]), morro = listOf(0.62f, 0.72f, 0.82f, 0.82f, 0.7f, 0.62f)[q], orejas = 1f,
        ojos = if (q == 3) 0.3f else 1f, cola2 = listOf(2.2f, 2.3f, 2.5f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El brote que ha salido en una grieta de la azotea: dos hojas y, a veces, una florecilla. */
private val brote: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val mece = listOf(0f, 0.2f, 0.6f, 0.7f, 0.3f, 0f)[q]
    val pie = Offset(17f, -0.2f)
    drawLine(luz.tono(Color(0xFF4A463C)), pie + Offset(-3f, 0.2f), pie + Offset(3f, 0f), 0.5f)
    drawPath(Path().apply { moveTo(pie.x, pie.y); cubicTo(pie.x - 0.4f, pie.y - 3f, pie.x + 0.6f + mece, pie.y - 5f, pie.x + mece, pie.y - 7f) }, luz.tono(R_HOJA), style = Stroke(0.7f, cap = StrokeCap.Round))
    drawOval(luz.tono(Color(0xFF7EAE4E)), Offset(pie.x - 3.4f + mece * 0.5f, pie.y - 5.4f), Size(3.2f, 1.6f)); drawOval(luz.tono(R_HOJA), Offset(pie.x + 0.4f + mece * 0.5f, pie.y - 6.2f), Size(3.2f, 1.6f))
    drawCircle(luz.tono(Color(0xFFF0D860)), 1f, pie + Offset(mece, -7.6f))
}

private fun poseVilano(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -5f), Offset(11f, -9f), Offset(13f, -12f), Offset(12.4f, -8f), Offset(10f, -4f), Offset(8.6f, -3f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.4f - listOf(0f, 0.6f, 1.2f, 0.8f, 0.2f, 0f)[q]), morro = listOf(-0.2f, -0.34f, -0.46f, -0.3f, -0.1f, 0f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** Un vilano que pasa flotando: le da con la mano, sube, y vuelve a bajar despacio. */
private val vilano: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = Offset(15.4f + listOf(0f, -0.6f, 0.6f, 1.8f, 1.2f, 0.4f)[q], -14f - listOf(0f, 2f, 5f, 6.4f, 4f, 1.4f)[q])
    val blanco = luz.tono(Color(0xFFF6F2E4))
    for (i in 0..6) { val a = -2.6f + i * 0.34f; drawLine(blanco.copy(alpha = 0.9f), sitio, sitio + Offset(cos(a) * 2.6f, sin(a) * 2.6f), 0.3f) }
    drawLine(blanco, sitio, sitio + Offset(0.2f, 2.4f), 0.3f); drawCircle(luz.tono(Color(0xFF6A5240)), 0.5f, sitio + Offset(0.2f, 2.6f))
}

/** La gata de la Ciudad verde. */
internal val PELAJE_RUINAS = Pelaje(
    manto = R_BLANCO, claro = Color(0xFFFAF6EC), marca = R_NARANJA, dibujo = DibujoPelaje.MANCHAS, ojos = Color(0xFF8EB84E),
    nariz = Color(0xFFE2A09A), collar = R_CORDEL, accesorio = manchasYHoja,
    especiales = listOf(EspecialGato("huele un brote", true, 6, ::poseBrote, brote), EspecialGato("juega con un vilano", true, 6, ::poseVilano, vilano)),
)
