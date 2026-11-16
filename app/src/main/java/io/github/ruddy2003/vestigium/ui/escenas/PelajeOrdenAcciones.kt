package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor

// =====================================================================
//  Las dos acciones del gato de Orden (ver `PelajeOrden.kt`) **[Diseño propio]**
// =====================================================================
//
// Guardan la forma de las del de pixel (`gato17.py`: «levantar» y «sostener»), cambiando lo que tiene en las manos:
// - «alza el candil» (en vez de la espada), aquí: el candil de latón, «el signo de la orden y también su lámpara: un vaso
//   de latón con aceite de cabra y una mecha», el mismo de la banderita y del pendón de la torre (sin asa: con ella se
//   leía como una taza). Lo tiene junto al pecho mirando la llama, lo sube con las dos manos por encima de la cabeza,
//   mira a lo lejos como en la ronda de noche y lo baja. La llama alumbra (de noche, su resplandor le da en la cara).
// - «se cubre con una placa de caparazón» (en vez del escudo del engrane), en `PelajeOrdenPlaca.kt`: el peto de un
//   caparazón, abollado, remendado y pintado a mano con un candil.
// Los colores son los del cuadro (`ColorOrden`): el latón, el hierro pavonado, el carmesí, la lumbre.

private val OA_LATON = Color(0xFFC9A04A)
private val OA_LATON_LUZ = Color(0xFFF2D27A)
private val OA_LATON_OSC = Color(0xFF7E6219)
private val OA_LUMBRE = Color(0xFFFFB45A)

/** Cuánto lo ha subido en cada cuadro (0 junto al pecho, 1 por encima de la cabeza) y cuánto se mece en alto. */
private val ALZA = listOf(0f, 0.55f, 1f, 1f, 1f, 0.5f)
private val MECE = listOf(0f, 0f, 0f, 0.6f, -0.4f, 0f)
private val MORRO_CANDIL = listOf(0.35f, 0f, -0.45f, -0.2f, -0.5f, 0.1f)
private val ABAJO_CANDIL = Offset(10.6f, -12.2f)
private val ARRIBA_CANDIL = Offset(14.2f, -30.4f)

/** Dónde agarra el cuello del candil en el cuadro [q]. */
private fun agarreCandil(q: Int): Offset = ABAJO_CANDIL + (ARRIBA_CANDIL - ABAJO_CANDIL) * ALZA[q] + Offset(MECE[q], 0f)

/** Dónde va la otra mano: junto al pecho, del lado del vaso; en alto, debajo del pie, sosteniéndolo. */
private fun otraManoCandil(q: Int): Offset = agarreCandil(q) + Offset(-1.8f, -1.9f) + Offset(0.5f, 3.6f) * ALZA[q]

internal fun poseCandilOrden(c: Int): Pose {
    val q = Math.floorMod(c, 6)
    val a = ALZA[q]
    return Gatuno.SENTADO.copy(
        cabeza = Offset(4.6f + 0.4f * a, -25.6f - 0.4f * a), morro = MORRO_CANDIL[q], orejas = if (q == 3) 0.9f else 1f,
        ojos = listOf(0.8f, 1f, 1f, 0.45f, 1f, 0.9f)[q], manoC = agarreCandil(q) + Offset(0f, 0.4f), manoL = otraManoCandil(q) + Offset(0f, 0.4f),
        cola2 = listOf(2.3f, 2.5f, 2.7f, 2.6f, 2.4f, 2.2f)[q],
    )
}

/** El candil con su llama, que alumbra; y las dos manos: una en el cuello y la otra junto al vaso o debajo del pie. */
internal val candilDeLaOrden: DrawScope.(PuntosGato, LuzGato, Int) -> Unit = { _, luz, c ->
    val q = Math.floorMod(c, 6)
    val g = agarreCandil(q)
    val mecha = g + Offset(0.4f, -4.9f)
    resplandor(mecha + Offset(0f, -2.2f), 14f, 0.22f + 0.5f * luz.noche, OA_LUMBRE)
    vasoDelCandil(luz, g)
    llama(mecha, 4.6f, 0.7f + q * 0.29f, 7)
    dedosOr(luz, otraManoCandil(q), 1.05f)
    dedosOr(luz, g + Offset(-0.1f, -1.1f))
}

/** El vaso de latón con el pie en [g]: el pie, el nudo del cuello, el vaso que se abre hacia arriba, su boca y la mecha. */
private fun DrawScope.vasoDelCandil(luz: LuzGato, g: Offset) {
    val laton = luz.tono(OA_LATON); val osc = luz.tono(OA_LATON_OSC)
    // La llama alumbra su propio latón por arriba, sobre todo de noche.
    val alumbrado = lerp(laton, lerp(OA_LATON_LUZ, OA_LUMBRE, 0.3f), 0.25f + 0.45f * luz.noche)
    val metal = Brush.horizontalGradient(listOf(alumbrado, laton, osc), g.x - 1.7f, g.x + 1.7f)
    // El pie y el cuello, con su nudo.
    drawPath(Path().apply { moveTo(g.x - 1.7f, g.y); lineTo(g.x + 1.7f, g.y); lineTo(g.x + 0.7f, g.y - 0.9f); lineTo(g.x + 0.55f, g.y - 1.5f); lineTo(g.x - 0.55f, g.y - 1.5f); lineTo(g.x - 0.7f, g.y - 0.9f); close() }, metal)
    drawOval(metal, Offset(g.x - 1f, g.y - 1.45f), Size(2f, 0.75f))
    // El vaso, con su luz del lado de la llama y su sombra abajo.
    val vaso = Path().apply {
        moveTo(g.x - 2.3f, g.y - 4.6f); lineTo(g.x + 2.3f, g.y - 4.6f)
        cubicTo(g.x + 2.2f, g.y - 3f, g.x + 1.5f, g.y - 1.8f, g.x + 0.9f, g.y - 1.5f)
        lineTo(g.x - 0.9f, g.y - 1.5f)
        cubicTo(g.x - 1.5f, g.y - 1.8f, g.x - 2.2f, g.y - 3f, g.x - 2.3f, g.y - 4.6f)
        close()
    }
    drawPath(vaso, Brush.verticalGradient(listOf(alumbrado, laton, osc), g.y - 4.6f, g.y - 1.5f))
    clipPath(vaso) { drawLine(osc, Offset(g.x - 3f, g.y - 3.2f), Offset(g.x + 3f, g.y - 3.2f), 0.3f) }
    drawLine(Color.White.copy(alpha = 0.45f * (1f - 0.4f * luz.noche)), Offset(g.x - 1.5f, g.y - 4.1f), Offset(g.x - 1f, g.y - 2.1f), 0.35f, StrokeCap.Round)
    drawPath(vaso, osc.copy(alpha = 0.7f), style = Stroke(0.22f))
    // La boca, con el aceite oscuro y el labio encendido; la mecha.
    drawOval(luz.tono(Color(0xFF3A2A14)), Offset(g.x - 2.35f, g.y - 5.05f), Size(4.7f, 0.9f))
    drawOval(alumbrado, Offset(g.x - 2.35f, g.y - 5.05f), Size(4.7f, 0.9f), style = Stroke(0.3f))
    drawLine(luz.tono(Color(0xFF2A2218)), Offset(g.x + 0.3f, g.y - 4.5f), Offset(g.x + 0.4f, g.y - 5.1f), 0.35f, StrokeCap.Round)
}
