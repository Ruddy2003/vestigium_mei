package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// =====================================================================
//  Japón Edo: blanco con manchas negras y anaranjadas, con su collar rojo y su cascabel
// =====================================================================
//
// Sale del gato de pixel del tema (`assets/gato/edo_*`): el gato de tres colores de las casas de té, blanco con manchas
// de tinta y de naranja, con un collar bermellón y un cascabel de oro. Sus dos acciones propias: llamar a la suerte con
// la mano alzada, como los gatos de la buena fortuna, con una moneda de oro; y jugar con un palito de bolitas de arroz
// que rueda (el de pixel mira saltar una carpa; aquí no hay estanque).

private val E_BERMELLON = Color(0xFFC4452E)
private val E_ORO = Color(0xFFE3B341)
private val E_ORO_LUZ = Color(0xFFF6E39A)

/** El cascabel de oro, colgado del collar por delante. */
private val cascabel: DrawScope.(PuntosGato, LuzGato) -> Unit = { p, luz ->
    if (p.giro >= -0.25f) {
        val cuello = (p.cabeza - p.hombro).let { val d = it.getDistance().coerceAtLeast(0.01f); it / d }
        val sitio = p.cuello - cuello * 2.6f + Offset(1.6f, 1.6f)
        drawCircle(luz.tono(E_ORO), 1.5f, sitio); drawCircle(luz.tono(E_ORO_LUZ), 0.5f, sitio + Offset(-0.5f, -0.5f))
        drawLine(luz.tono(Color(0xFF6A4A1E)), sitio + Offset(-0.9f, 0.4f), sitio + Offset(0.9f, 0.4f), 0.35f)
    }
}

private fun poseSuerte(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    // Sentado, con la mano alzada que llama: sube y baja.
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.6f, -26.2f), morro = -0.05f, orejas = 1f, ojos = if (q == 3) 0.3f else 1f, giro = 0.5f,
        manoC = Offset(8f, -26f + listOf(0f, 2.4f, 4f, 2.4f, 0f, -1f)[q]), cola2 = listOf(2.3f, 2.4f, 2.5f, 2.4f, 2.3f, 2.2f)[q],
    )
}

/** La moneda de oro, ovalada, que sostiene con la otra mano contra el pecho. */
private val moneda: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { p, luz, _ ->
    val c = p.hombro + Offset(3.6f, 5f)
    drawOval(luz.tono(E_ORO), Offset(c.x - 2.6f, c.y - 4f), Size(5.2f, 8f)); drawOval(luz.tono(Color(0xFFA67A1E)), Offset(c.x - 2.6f, c.y - 4f), Size(5.2f, 8f), style = Stroke(0.4f))
    for (k in -1..1) drawLine(luz.tono(Color(0xFFA67A1E)), Offset(c.x - 1.6f, c.y + k * 2f), Offset(c.x + 1.6f, c.y + k * 2f), 0.3f)
    drawCircle(Color(0xFFFFF6D0).copy(alpha = 0.8f * (1f - 0.5f * luz.noche)), 0.6f, c + Offset(-1f, -2f))
}

private fun poseDango(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val mano = listOf(Offset(9f, -3.4f), Offset(11.4f, -5.6f), Offset(13.6f, -2.4f), Offset(12.4f, -1f), Offset(10f, -2f), Offset(8.8f, -3f))[q]
    return Gatuno.SENTADO.copy(cabeza = Offset(5.8f, -24f + 0.4f * (q % 2)), morro = listOf(0.4f, 0.46f, 0.54f, 0.52f, 0.44f, 0.4f)[q], orejas = 1f, ojos = if (q == 4) 0.3f else 1f, manoC = mano, cola2 = listOf(2.1f, 2.4f, 2.7f, 2.5f, 2.2f, 2f)[q])
}

/** Un palito de bolitas de arroz caído: le da con la mano y rueda un poco. */
private val dango: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val corre = listOf(0f, 0.4f, 2f, 3.4f, 4f, 2.2f)[q]
    val a = Offset(13f + corre, -1.6f); val b = a + Offset(9f, -0.8f)
    drawOval(Color.Black.copy(alpha = 0.18f), Offset(a.x - 1f, -0.7f), Size(11f, 1.3f))
    drawLine(luz.tono(Color(0xFF9A9452)), a, b, 0.5f, StrokeCap.Round)
    for (k in 0 until 3) { val o = a + (b - a) * (0.2f + k * 0.3f); drawCircle(luz.tono(listOf(Color(0xFFF2E8D6), Color(0xFFE8B8B0), Color(0xFFB8C890))[k]), 1.5f, o) }
}

/** El gato de Japón Edo. */
internal val PELAJE_EDO = Pelaje(
    manto = Color(0xFFFBF8F1), claro = Color(0xFFFFFFFF), marca = Color(0xFF2A282C), dibujo = DibujoPelaje.MANCHAS, ojos = Color(0xFFD8B25A),
    nariz = Color(0xFFE89A9A), collar = E_BERMELLON, accesorio = cascabel,
    especiales = listOf(EspecialGato("llama a la suerte con la mano alzada", true, 6, ::poseSuerte, moneda), EspecialGato("juega con un palito de dangos", true, 6, ::poseDango, dango)),
)

