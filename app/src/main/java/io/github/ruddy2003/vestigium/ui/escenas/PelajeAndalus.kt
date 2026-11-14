package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Al-Ándalus: atigrado color albaricoque, de pecho crema y ojos verdes, con turbante blanco y collar azul
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/andalus_*`): rayado en tonos de albaricoque, con un turbante blanco de
// banda verde prendido con un broche de oro, y un collar añil del que cuelga una medalla. Sus dos acciones propias:
// jugar con un ovillo de seda y sostener un candil.

private val A_MANTO = Color(0xFFDDA07C)
private val A_VERDE = Color(0xFF2F7A5C)
private val A_ORO = Color(0xFFDAB150)
private val A_ANIL = Color(0xFF2F5C88)
private val A_SEDA = Color(0xFFEBCB62)

private fun unidadA(v: Offset, siNo: Offset = Offset(0f, -1f)) = sqrt(v.x * v.x + v.y * v.y).let { if (it < 0.001f) siNo else v / it }

/**
 * El turbante y la medalla. El turbante es una toca blanca enrollada, con una banda verde que la cruza y un broche de
 * oro por delante; tapa las orejas, como en el gato de pixel. De espaldas no se ven ni el broche ni la medalla.
 */
private val turbanteYMedalla: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    val angulo = (PI / 2 * (1f - p.giro.coerceIn(-1f, 1f))).toFloat()
    val s = sin(angulo).coerceIn(0f, 1f); val co = cos(angulo).coerceIn(-1f, 1f)
    // La medalla, que cuelga del collar por delante del cuello.
    if (p.giro >= -0.25f) {
        val cuello = unidadA(p.cabeza - p.hombro); val nuca = Offset(cuello.y, -cuello.x)
        val sitio = p.cuello - nuca * (3.5f * s) + Offset(0f, 1.3f + 2.4f * co.coerceAtLeast(0f))
        drawCircle(luz.tono(Color(0xFF8A6A22)), 1.6f, sitio + Offset(0.15f, 0.25f)); drawCircle(luz.tono(A_ORO), 1.4f, sitio)
        drawCircle(luz.tono(Color(0xFFFFF0C0)), 0.3f, sitio + Offset(-0.5f, -0.5f))
    }
    // El turbante: una toca que cubre lo alto de la cabeza.
    val c = p.cabeza + Offset(-0.4f * s, -4.2f)
    val r = Gatuno.R_CABEZA
    val toca = Path().apply {
        moveTo(c.x - r * 1.04f, c.y + 2.4f); cubicTo(c.x - r * 1.2f, c.y - 3.2f, c.x - r * 0.6f, c.y - 6.6f, c.x, c.y - 6.4f)
        cubicTo(c.x + r * 0.6f, c.y - 6.6f, c.x + r * 1.2f, c.y - 3.2f, c.x + r * 1.04f, c.y + 2.4f)
        cubicTo(c.x + r * 0.5f, c.y + 0.6f, c.x - r * 0.5f, c.y + 0.6f, c.x - r * 1.04f, c.y + 2.4f); close()
    }
    val lado = if (luz.rayo.lat >= 0f) 1f else -1f
    drawPath(toca, Brush.horizontalGradient(if (lado > 0f) listOf(luz.tono(Color(0xFFCFC6B4)), luz.tono(Color(0xFFF6F0E2)), luz.tono(Color(0xFFFFFCF4))) else listOf(luz.tono(Color(0xFFFFFCF4)), luz.tono(Color(0xFFF6F0E2)), luz.tono(Color(0xFFCFC6B4))), c.x - r, c.x + r))
    clipPath(toca) {
        // Las vueltas de la tela y la banda verde, que la cruza al sesgo.
        for (i in 0..2) drawPath(Path().apply { moveTo(c.x - r * 1.2f, c.y - 3.4f + i * 2.4f); cubicTo(c.x - r * 0.4f, c.y - 5.4f + i * 2.4f, c.x + r * 0.4f, c.y - 5.4f + i * 2.4f, c.x + r * 1.2f, c.y - 3.4f + i * 2.4f) }, luz.tono(Color(0xFFBDB29C)).copy(alpha = 0.7f), style = Stroke(0.45f))
        drawPath(Path().apply { moveTo(c.x - r * 1.2f, c.y + 0.6f); cubicTo(c.x - r * 0.4f, c.y - 3.4f, c.x + r * 0.5f, c.y - 2.6f, c.x + r * 1.2f, c.y - 4.6f) }, luz.tono(A_VERDE), style = Stroke(1.5f, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(c.x - r * 1.2f, c.y + 2f); cubicTo(c.x - r * 0.3f, c.y - 0.6f, c.x + r * 0.5f, c.y - 0.2f, c.x + r * 1.2f, c.y - 1.6f) }, luz.tono(A_VERDE).copy(alpha = 0.85f), style = Stroke(0.8f, cap = StrokeCap.Round))
    }
    drawPath(toca, luz.tono(Color(0xFF9A8E78)).copy(alpha = 0.7f), style = Stroke(0.4f))
    // El broche, por delante: de perfil, hacia el hocico; de frente, en medio.
    if (p.giro >= -0.25f) {
        val broche = c + Offset(r * 0.74f * s, -1.4f)
        drawCircle(luz.tono(Color(0xFF8A6A22)), 1.5f, broche + Offset(0.1f, 0.2f)); drawCircle(luz.tono(A_ORO), 1.3f, broche); drawCircle(luz.tono(Color(0xFFB02A3C)), 0.5f, broche)
    }
}

private fun poseOvillo(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -4f), Offset(11.6f, -6.4f), Offset(13.6f, -3.4f), Offset(12.6f, -1.2f), Offset(10f, -2.4f), Offset(8.6f, -3.4f))[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.4f, -24.6f + 0.4f * (q % 2)), morro = listOf(0.36f, 0.42f, 0.5f, 0.5f, 0.42f, 0.36f)[q], orejas = 1f,
        ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q],
    )
}

/** El ovillo de seda amarilla, que rueda cuando le da con la mano, y el cabo que se le va soltando. */
private val ovillo: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val q = Math.floorMod(c, 6)
    val sitio = Offset(15.6f + listOf(0f, 0.4f, 1.8f, 2.6f, 2f, 0.8f)[q], -2.8f)
    val giro = q * 0.5f
    // El cabo, que va del ovillo a la mano.
    drawPath(Path().apply { moveTo(sitio.x - 1f, sitio.y + 2.2f); cubicTo(sitio.x - 4f, sitio.y + 3.4f, p.manoC.x + 3f, 0.6f, p.manoC.x + 1f, p.manoC.y + 0.4f) }, luz.tono(A_SEDA), style = Stroke(0.4f, cap = StrokeCap.Round))
    drawOval(Color.Black.copy(alpha = 0.2f), Offset(sitio.x - 3f, -0.8f), Size(6.4f, 1.4f))
    val bola = Path().apply { addOval(Rect(sitio.x - 2.9f, sitio.y - 2.9f, sitio.x + 2.9f, sitio.y + 2.9f)) }
    drawPath(bola, Brush.horizontalGradient(listOf(luz.tono(Color(0xFFB8963A)), luz.tono(A_SEDA), luz.tono(Color(0xFFFFF2C0))), sitio.x - 3f, sitio.x + 3f))
    clipPath(bola) { for (i in 0..3) { val a = giro + i * 0.8f; drawLine(luz.tono(Color(0xFFA8862A)).copy(alpha = 0.8f), sitio + Offset(cos(a) * 3.4f, sin(a) * 3.4f), sitio - Offset(cos(a) * 3.4f, sin(a) * 3.4f) + Offset(0.8f, 0f), 0.35f) } }
}

private fun poseCandil(c: Int): Pose {
    val q = Math.floorMod(c, 4)
    return Gatuno.SENTADO.copy(
        cabeza = Offset(5.2f, -25.2f + 0.3f * (q % 2)), morro = listOf(0.28f, 0.34f, 0.3f, 0.26f)[q], ojos = listOf(1f, 1f, 0.2f, 1f)[q], orejas = 0.95f,
        manoC = Offset(9.2f, -11.4f), manoL = Offset(10.6f, -11f), cola2 = listOf(2.3f, 2.4f, 2.5f, 2.4f)[q],
    )
}

/** El candil de barro, con su pico y su llama pequeña y amarilla, que tiembla. */
private val candilDelGato: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, c ->
    val base = p.manoC + Offset(1.2f, -1.2f)
    val forma = Path().apply {
        moveTo(base.x - 2.6f, base.y); cubicTo(base.x - 4f, base.y - 0.8f, base.x - 3.6f, base.y - 3f, base.x - 1.2f, base.y - 3.2f); lineTo(base.x + 1.6f, base.y - 3.1f)
        cubicTo(base.x + 3f, base.y - 2.8f, base.x + 4.4f, base.y - 2.6f, base.x + 5.6f, base.y - 2.5f); lineTo(base.x + 5.6f, base.y - 1.6f); cubicTo(base.x + 4f, base.y - 1f, base.x + 3f, base.y - 0.3f, base.x + 2.2f, base.y); close()
    }
    drawPath(forma, Brush.verticalGradient(listOf(luz.tono(Color(0xFFE2A878)), luz.tono(Color(0xFF9A5E3A))), base.y - 3.2f, base.y))
    drawLine(luz.tono(A_VERDE).copy(alpha = 0.8f), Offset(base.x - 2.6f, base.y - 1.8f), Offset(base.x + 1.6f, base.y - 1.9f), 0.45f)
    val reloj = 0.4f + c * 0.37f
    resplandor(base + Offset(5.6f, -4.4f), 10f, 0.28f + 0.5f * luz.noche)
    llama(base + Offset(5.6f, -2.2f), 3.4f, reloj, 7)
}

/** El gato de Al-Ándalus. */
internal val PELAJE_ANDALUS = Pelaje(
    manto = A_MANTO, claro = Color(0xFFF8EADA), marca = Color(0xFFB9704C), dibujo = DibujoPelaje.ATIGRADO, ojos = Color(0xFF4E9E7C),
    nariz = Color(0xFFE0A0A0), collar = A_ANIL, accesorio = turbanteYMedalla,
    especiales = listOf(EspecialGato("juega con un ovillo de seda", true, 6, ::poseOvillo, ovillo), EspecialGato("sostiene un candil", true, 4, ::poseCandil, candilDelGato)),
)
