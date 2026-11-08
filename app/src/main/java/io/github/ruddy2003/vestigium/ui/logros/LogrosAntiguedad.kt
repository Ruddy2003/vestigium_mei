package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Los logros y el viaje de la Antigüedad (y de los dos temas de fantasía que comparten sus materiales: Arcano y Antiguas)
// =====================================================================

/** El gris translúcido de lo que aún no se gana: la misma tenuidad que la figura sin ganar. */
private fun siluetaAnt(oscuro: Boolean): Color =
    if (oscuro) Color(0xFFE6E1E5).copy(alpha = 0.12f) else Color(0xFF1C1B1F).copy(alpha = 0.11f)

/** El metal del nivel a lo ancho, de [x0] a [x1]: oscuro, base, brillo, base y oscuro (un degradado). */
private fun metalAnt(e: EstadoPedestal, x0: Float, x1: Float): Brush =
    Brush.horizontalGradient(listOf(e.metalOscuro, e.base, e.brillo, e.base, e.metalOscuro), x0, x1)

/** La sombra al pie del pedestal: dos óvalos tenues, más marcados de noche; casi nada si el logro aún no se gana. */
private fun DrawScope.sombraPieAnt(x0: Float, x1: Float, y: Float, alto: Float, hecho: Boolean, oscuro: Boolean) {
    val f = (if (hecho) 0.16f else 0.05f) * (if (oscuro) 1.6f else 1f)
    drawOval(Color.Black.copy(alpha = f * 0.5f), Offset(x0 - (x1 - x0) * 0.03f, y - alto * 0.6f), Size((x1 - x0) * 1.06f, alto * 1.3f))
    drawOval(Color.Black.copy(alpha = f), Offset(x0, y - alto * 0.4f), Size(x1 - x0, alto))
}

/** Un clavo o un remache en el metal del nivel: la cabeza oscura, la base y, si cabe, el brillo arriba a la izquierda. */
private fun DrawScope.clavoAnt(c: Offset, r: Float, e: EstadoPedestal) {
    drawCircle(e.metalOscuro, r, c)
    drawCircle(e.base, r * 0.74f, c)
    if (r > 1.2f * density) drawCircle(e.brillo, r * 0.32f, Offset(c.x - r * 0.25f, c.y - r * 0.25f))
}

/** El destello del legendario (nivel 4, el cristal): una estrellita de cuatro puntas. Solo si cabe. */
private fun DrawScope.destelloAnt(c: Offset, r: Float, e: EstadoPedestal) {
    if (e.nivel < 4 || r < 1.5f * density) return
    drawPath(estrella(c, r, 4, 0.22f), e.brillo.copy(alpha = 0.95f))
    drawCircle(Color.White.copy(alpha = 0.9f), r * 0.2f, c)
}

/**
 * [n] runas inventadas en fila, a lo ancho de [ancho] desde [o] (el centro de la fila a la izquierda): un trazo vertical y
 * una o dos ramas, siempre las mismas para cada [semilla]. Ninguna es una letra real.
 */
private fun DrawScope.runasAnt(o: Offset, ancho: Float, alto: Float, color: Color, grosor: Float, n: Int, semilla: Int) {
    val paso = ancho / n
    val a = o.y - alto / 2; val b = o.y + alto / 2
    for (k in 0 until n) {
        val x = o.x + k * paso + paso * 0.2f; val ww = paso * 0.6f; val cx = x + ww / 2
        drawLine(color, Offset(cx, a), Offset(cx, b), grosor, StrokeCap.Round)
        when ((azar(semilla, k) * 4).toInt()) {
            0 -> drawLine(color, Offset(cx, a), Offset(x + ww, a + alto / 3), grosor, StrokeCap.Round)
            1 -> { drawLine(color, Offset(x, a + alto / 4), Offset(cx, o.y), grosor, StrokeCap.Round); drawLine(color, Offset(cx, o.y), Offset(x + ww, a + alto / 4), grosor, StrokeCap.Round) }
            2 -> drawLine(color, Offset(x, o.y + alto / 6), Offset(x + ww, o.y - alto / 6), grosor, StrokeCap.Round)
            else -> { drawLine(color, Offset(cx, o.y - alto / 6), Offset(x + ww, o.y + alto / 6), grosor, StrokeCap.Round); drawLine(color, Offset(cx, o.y - alto / 6), Offset(x, o.y + alto / 6), grosor, StrokeCap.Round) }
        }
    }
}

/** Una mano abierta (la palma, cuatro dedos y el pulgar) de radio [r] con la palma en [c]: la que deja el ocre soplado. */
private fun DrawScope.manoAnt(c: Offset, r: Float, color: Color) {
    drawOval(color, Offset(c.x - r * 0.5f, c.y - r * 0.15f), Size(r, r * 0.95f))
    drawRect(color, Offset(c.x - r * 0.32f, c.y + r * 0.5f), Size(r * 0.64f, r * 0.55f))
    listOf(-0.36f to 0.6f, -0.12f to 0.78f, 0.12f to 0.74f, 0.35f to 0.58f).forEach { (dx, largo) ->
        drawRoundRect(color, Offset(c.x + dx * r - r * 0.11f, c.y - r * 0.05f - largo * r), Size(r * 0.22f, largo * r + r * 0.2f), CornerRadius(r * 0.11f))
    }
    val p = Offset(c.x - r * 0.4f, c.y + r * 0.4f)
    rotate(-52f, p) { drawRoundRect(color, Offset(p.x - r * 0.12f, p.y - r * 0.62f), Size(r * 0.24f, r * 0.66f), CornerRadius(r * 0.12f)) }
}

/** La forma de una tabula ansata: el rectángulo de [x0] a [x1] con una cola de milano de ancho [ans] a cada lado. */
private fun tabulaAnt(x0: Float, y0: Float, x1: Float, y1: Float, ans: Float): Path = Path().apply {
    val a = y1 - y0
    moveTo(x0, y0); lineTo(x1, y0); lineTo(x1, y0 + a * 0.3f); lineTo(x1 + ans, y0 + a * 0.08f); lineTo(x1 + ans, y1 - a * 0.08f)
    lineTo(x1, y1 - a * 0.3f); lineTo(x1, y1); lineTo(x0, y1); lineTo(x0, y1 - a * 0.3f); lineTo(x0 - ans, y1 - a * 0.08f)
    lineTo(x0 - ans, y0 + a * 0.08f); lineTo(x0, y0 + a * 0.3f); close()
}

// =====================================================================
//  Prehistoria
// =====================================================================

object LogrosCueva : DisenoLogros {
    private val ROCA = Color(0xFFB09A7C)
    private val ROCA_LUZ = Color(0xFFD6C6A8)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF4A3B2D)
    private val CALIZA = Color(0xFFE6DAC2)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    /** El ocre amarillo tostado al fuego: el del rótulo. */
    private val TOSTADO = Color(0xFF7E4C14)
    /** El cuero curtido del cordel del collar. */
    private val CUERO = Color(0xFF5A3A1E)
    private val NOCHE = Color(0xFF0F0B08)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.76f) else c
    /** La roca de noche: entre [t] y [hondo]. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.54f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Un canto de caliza sin labrar, plano arriba al centro (donde se apoya la figura): las vetas de los estratos, una grieta,
     * cinco puntos de ocre puestos con el dedo, una mano soplada en negativo, rayas de cuenta con carbón y, tendido al frente,
     * un collar de cuentas en el metal del nivel (más cuentas cuanto más alto). Un degradado: la luz sobre la roca.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val s = e.semilla
        val roca = Path().apply {
            moveTo(w * 0.1f, h * 0.956f)
            cubicTo(w * 0.085f, h * 0.9f, w * 0.11f, h * 0.83f, w * 0.17f, h * 0.802f)
            quadraticTo(w * (0.25f + 0.04f * azar(s, 1)), h * 0.772f, w * 0.38f, h * 0.775f)
            lineTo(w * 0.62f, h * 0.774f)
            quadraticTo(w * (0.76f - 0.04f * azar(s, 2)), h * 0.771f, w * 0.84f, h * 0.804f)
            cubicTo(w * 0.9f, h * 0.83f, w * 0.92f, h * 0.9f, w * 0.9f, h * 0.956f)
            close()
        }
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.024f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(roca, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        drawPath(roca, Brush.verticalGradient(listOf(n(ROCA_LUZ, oscuro), n(ROCA, oscuro), n(ROCA_OSC, oscuro)), h * 0.775f, h * 0.956f))
        clipPath(roca) {
            // El pie hundido en la tierra y la cara de arriba, gastada y más clara.
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), Offset(0f, h * 0.935f), Size(w, h * 0.03f))
            drawRect(n(CALIZA, oscuro).copy(alpha = 0.5f), Offset(w * 0.3f, h * 0.772f), Size(w * 0.4f, h * 0.008f))
            if (fino) {
                // Los estratos: dos vetas que cruzan el canto, y una grieta.
                for (k in 0 until 2) {
                    val y = h * (0.84f + k * 0.05f)
                    val veta = Path().apply {
                        moveTo(0f, y)
                        for (j in 1..6) lineTo(w * j / 6f, y + h * 0.008f * (azar(s, 10 + k * 7 + j) - 0.5f))
                    }
                    drawPath(veta, n(ROCA_HONDA, oscuro).copy(alpha = 0.3f), style = Stroke(lw * 0.8f))
                }
                val g = Path().apply {
                    moveTo(w * 0.8f, h * 0.81f); lineTo(w * 0.77f, h * 0.84f); lineTo(w * 0.79f, h * 0.86f); lineTo(w * 0.75f, h * 0.9f)
                }
                drawPath(g, n(ROCA_HONDA, oscuro).copy(alpha = 0.55f), style = Stroke(lw))
                for (k in 0 until 16) drawCircle(n(ROCA_HONDA, oscuro).copy(alpha = 0.25f), u * (0.4f + 0.6f * azar(s, 40 + k)),
                    Offset(w * (0.14f + 0.72f * azar(s, 60 + k)), h * (0.8f + 0.14f * azar(s, 80 + k))))
            }
        }
        // Cinco puntos de ocre, puestos con la yema del dedo.
        val rp = maxOf(w * 0.012f, 0.8f * u)
        for (k in 0 until 5) drawCircle(t(OCRE, oscuro).copy(alpha = 0.9f), rp * (0.85f + 0.3f * azar(s, 100 + k)), Offset(w * (0.22f + k * 0.05f), h * (0.872f + 0.004f * azar(s, 110 + k))))
        // La mano soplada: el ocre alrededor y, en medio, la roca limpia.
        if (fino) {
            val m = Offset(w * 0.68f, h * 0.872f); val r = h * 0.032f
            drawCircle(t(OCRE, oscuro).copy(alpha = 0.22f), r * 1.75f, Offset(m.x, m.y + r * 0.15f))
            drawCircle(t(OCRE, oscuro).copy(alpha = 0.32f), r * 1.35f, Offset(m.x, m.y + r * 0.1f))
            manoAnt(m, r, n(ROCA, oscuro))
        }
        // Rayas de cuenta, con carbón.
        if (muyFino) {
            for (k in 0 until 4) drawLine(t(CARBON, oscuro).copy(alpha = 0.7f), Offset(w * (0.3f + k * 0.022f), h * 0.905f), Offset(w * (0.302f + k * 0.022f), h * 0.93f), lw, StrokeCap.Round)
            drawLine(t(CARBON, oscuro).copy(alpha = 0.7f), Offset(w * 0.29f, h * 0.928f), Offset(w * 0.375f, h * 0.906f), lw, StrokeCap.Round)
        }
        // El collar tendido al frente: el cordel de cuero y las cuentas de metal.
        val y0 = h * 0.797f; val caida = h * 0.024f
        val cordel = Path().apply { moveTo(w * 0.2f, y0); quadraticTo(w * 0.5f, y0 + caida * 2f, w * 0.8f, y0) }
        drawPath(cordel, t(CUERO, oscuro), style = Stroke(lw))
        val cuentas = if (fino) 3 + e.nivel else 3
        val rc = maxOf(h * 0.0105f, 0.9f * u)
        for (k in 0 until cuentas) {
            val f = (k + 1f) / (cuentas + 1f)
            val x = w * (0.2f + 0.6f * f)
            val y = y0 + caida * 4f * f * (1f - f)
            clavoAnt(Offset(x, y), rc, e)
        }
        destelloAnt(Offset(w * 0.5f, y0 + caida - rc * 1.6f), h * 0.02f, e)
    }

    /**
     * Un dolmen pequeño: la losa de borde desportillado sobre dos piedras, con puntos de ocre, y colgado entre ellas el
     * disco de metal del nivel, atado con un cordel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val s = e.semilla
        val losa = contornoIrregular(Rect(w * 0.05f, h * 0.02f, w * 0.95f, h * 0.44f), s, 30, 0.14f)
        val pieI = Rect(w * 0.13f, h * 0.36f, w * 0.36f, h)
        val pieD = Rect(w * 0.63f, h * 0.38f, w * 0.87f, h)
        if (!e.hecho) {
            drawPath(Path().apply { addPath(losa); addRoundRect(androidx.compose.ui.geometry.RoundRect(pieI, CornerRadius(h * 0.22f))); addRoundRect(androidx.compose.ui.geometry.RoundRect(pieD, CornerRadius(h * 0.22f))) }, siluetaAnt(oscuro))
            return
        }
        val lw = maxOf(0.6f * u, h * 0.03f)
        // El hueco entre los pies, en sombra, y el cordel con el disco.
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.16f), Offset(pieI.right - w * 0.04f, h * 0.84f), Size(pieD.left - pieI.right + w * 0.08f, h * 0.16f))
        listOf(pieI, pieD).forEachIndexed { k, r ->
            drawRoundRect(n(ROCA_OSC, oscuro), r.topLeft, r.size, CornerRadius(h * 0.22f))
            drawRoundRect(n(ROCA, oscuro).copy(alpha = 0.6f), Offset(r.left + r.width * 0.12f, r.top + h * 0.06f), Size(r.width * 0.5f, h * 0.14f), CornerRadius(h * 0.07f))
            drawLine(n(ROCA_HONDA, oscuro).copy(alpha = 0.5f), Offset(r.left + r.width * (0.3f + 0.3f * azar(s, k)), r.top + h * 0.2f), Offset(r.left + r.width * 0.5f, r.bottom - h * 0.12f), lw * 0.6f)
        }
        val cx = w * 0.495f
        drawLine(t(CUERO, oscuro), Offset(cx, h * 0.38f), Offset(cx, h * 0.6f), maxOf(0.6f * u, h * 0.025f))
        clavoAnt(Offset(cx, h * 0.7f), h * 0.13f, e)
        // La losa: luz arriba, puntos de ocre al frente.
        drawPath(losa, n(ROCA, oscuro))
        clipPath(losa) {
            drawRect(n(ROCA_LUZ, oscuro).copy(alpha = 0.7f), Offset(0f, 0f), Size(w, h * 0.13f))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.15f), Offset(0f, h * 0.36f), Size(w, h * 0.1f))
        }
        for (k in 0 until 6) drawCircle(t(OCRE, oscuro).copy(alpha = 0.85f), h * 0.055f, Offset(w * (0.2f + k * 0.055f), h * 0.24f))
        drawCircle(t(OCRE, oscuro).copy(alpha = 0.3f), h * 0.12f, Offset(w * 0.74f, h * 0.24f))
        drawCircle(t(OCRE_AMARILLO, oscuro).copy(alpha = 0.7f), h * 0.05f, Offset(w * 0.74f, h * 0.24f))
    }

    override val margenPlaca: Dp = 9.dp
    /** La cara alisada de la laja: caliza de día; de noche, la roca honda en penumbra. */
    private fun cara(oscuro: Boolean): Color = if (oscuro) hondo(ROCA_HONDA, true) else CALIZA
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) OCRE_AMARILLO else TOSTADO

    /**
     * Una laja de caliza de borde desportillado, con el filo pintado en el metal del nivel y la cara del medio alisada, lisa,
     * donde va el texto. Tres puntos de ocre en el margen izquierdo. Un degradado: el filo de metal.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val borde = contornoIrregular(Rect(0f, 0f, w, h), e.semilla, 36, 0.05f)
        if (!e.hecho) { drawPath(borde, siluetaAnt(oscuro)); return }
        drawPath(borde, n(ROCA, oscuro))
        for (k in 0 until 14) drawCircle(n(ROCA_HONDA, oscuro).copy(alpha = 0.3f), u * 0.5f,
            Offset(w * azar(e.semilla, 200 + k), if (k % 2 == 0) h * (0.02f + 0.06f * azar(e.semilla, 220 + k)) else h * (0.92f + 0.06f * azar(e.semilla, 220 + k))))
        drawPath(borde, metalAnt(e, 0f, w), style = Stroke(1.4f * u))
        val alisada = contornoIrregular(Rect(3.2f * u, 3.2f * u, w - 3.2f * u, h - 3.2f * u), e.semilla + 7, 36, 0.03f)
        drawPath(alisada, cara(oscuro))
        drawPath(alisada, n(ROCA_OSC, oscuro).copy(alpha = 0.5f), style = Stroke(0.6f * u))
        for (k in 0 until 3) drawCircle(t(OCRE, oscuro).copy(alpha = 0.85f), 1.05f * u, Offset(6f * u, h / 2 + (k - 1) * 3.4f * u))
    }
}

// =====================================================================
//  Roma
// =====================================================================

object LogrosRomano : DisenoLogros {
    private val HAYA = Color(0xFFCDA670)
    private val HAYA_LUZ = Color(0xFFE2C48E)
    private val HAYA_VETA = Color(0xFF7A5630)
    private val CERA_NEGRA = Color(0xFF2E241C)
    private val CERA_RAYA = Color(0xFFB59A70)
    /** La cera de abeja sin ennegrecer, la de la tablilla de día. */
    private val CERA_MIEL = Color(0xFFDDCA9C)
    private val LINO = Color(0xFFEDE4CC)
    private val NEGRO = Color(0xFF1E1714)
    private val BRONCE = Color(0xFFB98B4A)
    private val BRONCE_OSC = Color(0xFF6E4E22)
    private val BRONCE_LUZ = Color(0xFFE0B878)
    private val MINIO = Color(0xFFB0302A)
    private val PURPURA = Color(0xFF5B2A4E)
    /** El travertino de la basa: su tono, su luz y su sombra. */
    private val TRAVERTINO = Color(0xFFD3C29E)
    private val TRAVERTINO_LUZ = Color(0xFFE8DCC0)
    private val TRAVERTINO_OSC = Color(0xFFA08C66)
    /** El pórfido rojo de la tabla y el mármol del ara. */
    private val PORFIDO = Color(0xFF6E2232)
    private val MARMOL = Color(0xFFEDE8DE)
    private val MARMOL_OSC = Color(0xFFC4BBA8)
    private val LAUREL = Color(0xFF5E6B3A)
    private val NOCHE = Color(0xFF100D0B)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100D0B), 0.36f) else c
    /** La piedra de noche: oscura de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.54f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La basa de travertino: cornisa con su cimacio (y ovas, si hay sitio), el filete, el dado con los poros de la piedra, el
     * toro y el plinto. Al frente, la tabula ansata de pórfido, enmarcada en el metal del nivel, con dos clavos en las colas y
     * la dedicatoria insinuada. Un degradado: el marco de metal.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val cornisa = Rect(w * 0.12f, h * 0.772f, w * 0.88f, h * 0.798f)
        val filete = Rect(w * 0.15f, h * 0.798f, w * 0.85f, h * 0.806f)
        val dado = Rect(w * 0.17f, h * 0.806f, w * 0.83f, h * 0.918f)
        val toro = Rect(w * 0.14f, h * 0.918f, w * 0.86f, h * 0.932f)
        val plinto = Rect(w * 0.1f, h * 0.932f, w * 0.9f, h * 0.958f)
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { listOf(cornisa, filete, dado, toro, plinto).forEach { addRect(it) } }, siluetaAnt(oscuro)); return }
        val piedra = n(TRAVERTINO, oscuro); val luz = n(TRAVERTINO_LUZ, oscuro); val som = n(TRAVERTINO_OSC, oscuro)
        val lw = maxOf(0.6f * u, h * 0.0035f)
        // El dado y los poros del travertino.
        drawRect(piedra, dado.topLeft, dado.size)
        if (fino) for (k in 0 until 18) {
            val x = dado.left + dado.width * azar(e.semilla, k); val y = dado.top + dado.height * azar(e.semilla, k + 40)
            drawOval(som.copy(alpha = 0.6f), Offset(x, y), Size(u * (0.8f + 1.6f * azar(e.semilla, k + 80)), u * 0.55f))
        }
        bisel(dado, luz.copy(alpha = 0.7f), som.copy(alpha = 0.8f), lw)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), dado.topLeft, Size(dado.width, h * 0.008f))
        drawRect(som, filete.topLeft, filete.size)
        // La cornisa: la corona clara y el cimacio en sombra.
        drawRect(luz, cornisa.topLeft, cornisa.size)
        drawRect(piedra, Offset(cornisa.left, cornisa.top + cornisa.height * 0.5f), Size(cornisa.width, cornisa.height * 0.5f))
        drawLine(som, Offset(cornisa.left, cornisa.bottom - lw / 2), Offset(cornisa.right, cornisa.bottom - lw / 2), lw)
        if (muyFino) {
            val paso = maxOf(h * 0.022f, 1f)
            var x = cornisa.left + paso * 0.5f; var k = 0
            while (x < cornisa.right - paso * 0.3f && k < 80) {
                drawOval(som.copy(alpha = 0.65f), Offset(x - paso * 0.3f, cornisa.top + cornisa.height * 0.56f), Size(paso * 0.6f, cornisa.height * 0.32f))
                x += paso; k++
            }
        }
        // El toro y el plinto.
        drawRoundRect(luz, toro.topLeft, toro.size, CornerRadius(toro.height / 2))
        drawLine(som.copy(alpha = 0.7f), Offset(toro.left + toro.height / 2, toro.bottom - lw / 2), Offset(toro.right - toro.height / 2, toro.bottom - lw / 2), lw)
        drawRect(mezcla(piedra, som, 0.4f), plinto.topLeft, plinto.size)
        bisel(plinto, luz.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.2f), lw)
        // La tabula ansata de pórfido, con su marco de metal y la dedicatoria.
        val x0 = w * 0.34f; val x1 = w * 0.66f; val y0 = h * 0.826f; val y1 = h * 0.898f; val ans = w * 0.065f
        val tab = tabulaAnt(x0, y0, x1, y1, ans)
        drawPath(tab, n(PORFIDO, oscuro))
        drawPath(tab, metalAnt(e, x0 - ans, x1 + ans), style = Stroke(maxOf(h * 0.0065f, 0.8f * u)))
        val letra = e.brillo.copy(alpha = 0.85f)
        if (fino) {
            for (fila in 0 until 2) {
                val y = y0 + (y1 - y0) * (0.3f + fila * 0.38f); val alto = (y1 - y0) * 0.2f
                val letras = if (fila == 0) 7 else 5
                val ancho = (x1 - x0) * (if (fila == 0) 0.76f else 0.54f); val xa = (x0 + x1) / 2 - ancho / 2
                for (k in 0 until letras) {
                    val x = xa + ancho * (k + 0.5f) / letras
                    drawLine(letra, Offset(x, y - alto / 2), Offset(x, y + alto / 2), lw * 0.8f)
                    if (azar(e.semilla, 300 + k + fila * 10) > 0.45f) drawLine(letra, Offset(x, y - alto / 2), Offset(x + ancho / letras * 0.4f, y - alto / 2), lw * 0.8f)
                }
            }
        } else drawLine(letra, Offset(x0 + (x1 - x0) * 0.15f, (y0 + y1) / 2), Offset(x1 - (x1 - x0) * 0.15f, (y0 + y1) / 2), lw)
        val rc = maxOf(h * 0.0068f, 0.9f * u)
        clavoAnt(Offset(x0 - ans * 0.45f, (y0 + y1) / 2), rc, e)
        clavoAnt(Offset(x1 + ans * 0.45f, (y0 + y1) / 2), rc, e)
        destelloAnt(Offset(x1 + ans * 0.1f, y0), h * 0.02f, e)
    }

    /**
     * Un ara de mármol, ancha: la losa de arriba con los dos pulvinos (los rollos de los extremos, con su espiral) y el foco
     * entre ellos, el cuerpo con una guirnalda de laurel y, al centro, la pátera de ofrendas en el metal del nivel; abajo, el
     * zócalo.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val losa = Rect(w * 0.14f, h * 0.12f, w * 0.86f, h * 0.3f)
        val cuerpo = Rect(w * 0.2f, h * 0.3f, w * 0.8f, h * 0.84f)
        val zocalo = Rect(w * 0.11f, h * 0.84f, w * 0.89f, h)
        val rp = h * 0.13f
        if (!e.hecho) {
            drawPath(Path().apply {
                addRect(losa); addRect(cuerpo); addRect(zocalo)
                addOval(Rect(losa.left - rp * 0.4f, losa.top - rp * 0.6f, losa.left + rp * 1.6f, losa.top + rp * 1.4f))
                addOval(Rect(losa.right - rp * 1.6f, losa.top - rp * 0.6f, losa.right + rp * 0.4f, losa.top + rp * 1.4f))
            }, siluetaAnt(oscuro))
            return
        }
        val lw = maxOf(0.6f * u, h * 0.03f)
        val m = n(MARMOL, oscuro); val mo = n(MARMOL_OSC, oscuro)
        drawRect(m, cuerpo.topLeft, cuerpo.size)
        bisel(cuerpo, Color.White.copy(alpha = if (oscuro) 0.08f else 0.4f), mo, lw)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), cuerpo.topLeft, Size(cuerpo.width, h * 0.06f))
        // La guirnalda de laurel, colgada de dos clavos, y sus cintas.
        val verde = t(LAUREL, oscuro)
        drawArc(verde, 15f, 150f, false, Offset(w * 0.27f, h * 0.06f), Size(w * 0.46f, h * 0.62f), style = Stroke(h * 0.08f, cap = StrokeCap.Round))
        listOf(0.29f, 0.71f).forEach { x -> drawLine(t(MINIO, oscuro).copy(alpha = 0.8f), Offset(w * x, h * 0.42f), Offset(w * x + (if (x < 0.5f) -1f else 1f) * w * 0.02f, h * 0.7f), lw * 0.8f) }
        // La pátera de metal, al centro.
        val pc = Offset(w * 0.5f, h * 0.54f); val pr = h * 0.13f
        drawCircle(metalAnt(e, pc.x - pr, pc.x + pr), pr, pc)
        drawCircle(e.metalOscuro, pr, pc, style = Stroke(maxOf(0.5f * u, h * 0.02f)))
        drawCircle(e.metalOscuro.copy(alpha = 0.6f), pr * 0.35f, pc)
        destelloAnt(Offset(pc.x + pr, pc.y - pr), h * 0.14f, e)
        // La losa, los pulvinos y el foco.
        drawRect(t(MARMOL, oscuro), losa.topLeft, losa.size)
        drawLine(mo, Offset(losa.left, losa.bottom - lw / 2), Offset(losa.right, losa.bottom - lw / 2), lw)
        listOf(losa.left + rp * 0.6f, losa.right - rp * 0.6f).forEach { x ->
            val c = Offset(x, losa.top + rp * 0.4f)
            drawCircle(t(MARMOL, oscuro), rp, c)
            drawCircle(mo, rp, c, style = Stroke(lw * 0.8f))
            drawCircle(mo, rp * 0.5f, c, style = Stroke(lw * 0.7f))
            drawCircle(mo, rp * 0.15f, c)
        }
        drawPath(Path().apply { moveTo(w * 0.4f, losa.top); lineTo(w * 0.6f, losa.top); lineTo(w * 0.56f, 0f); lineTo(w * 0.44f, 0f); close() }, mo)
        drawRect(mo, zocalo.topLeft, zocalo.size)
        drawLine(m, Offset(zocalo.left, zocalo.top + lw / 2), Offset(zocalo.right, zocalo.top + lw / 2), lw)
    }

    override val margenPlaca: Dp = 10.dp
    /** El interior de la tabla: mármol de día; de noche, pórfido en penumbra. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) mezcla(PORFIDO, NOCHE, 0.62f) else MARMOL
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) MARMOL else NEGRO
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) CERA_MIEL else MINIO

    /**
     * La tabula ansata: el campo liso de mármol (o de pórfido de noche) con las dos colas de milano en los márgenes, el borde
     * en el metal del nivel, un cimacio fino por dentro y un clavo en cada cola. Un degradado: el borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val ans = minOf(margenPlaca.toPx() * 0.8f, w * 0.1f)
        val bw = 1.6f * u
        val forma = tabulaAnt(ans + bw / 2, bw / 2, w - ans - bw / 2, h - bw / 2, ans)
        if (!e.hecho) { drawPath(forma, siluetaAnt(oscuro)); return }
        drawPath(forma, interior(oscuro))
        drawPath(forma, metalAnt(e, 0f, w), style = Stroke(bw))
        val i = bw + 1.6f * u
        drawRect(e.metalOscuro.copy(alpha = 0.45f), Offset(ans + i, i), Size(w - 2 * (ans + i), h - 2 * i), style = Stroke(0.6f * u))
        clavoAnt(Offset(ans * 0.45f, h / 2), 1.3f * u, e)
        clavoAnt(Offset(w - ans * 0.45f, h / 2), 1.3f * u, e)
    }
}

// =====================================================================
//  Grecia
// =====================================================================

object LogrosHeleno : DisenoLogros {
    private val MARMOL = Color(0xFFEEE8DA)
    private val MARMOL_LUZ = Color(0xFFFAF6EC)
    private val MARMOL_OSC = Color(0xFFCBC1AB)
    private val MARMOL_SOMBRA = Color(0xFF9E947E)
    private val EGEO = Color(0xFF2E5E8E)
    private val BARNIZ = Color(0xFF1A1410)
    private val LLAMA = Color(0xFFF2C46A)
    /** El oro del filete con el foco, oscuro para que se vea sobre el mármol. */
    private val ORO = Color(0xFF8E6A18)
    /** El rojo de los vasos de figuras: el del error. */
    private val ROJO_VASO = Color(0xFFA0301C)
    private val NOCHE = Color(0xFF0F0C0A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.76f) else c
    /** El mármol de noche: oscuro de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.56f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El crepidoma: tres gradas de mármol con sus vetas y sus juntas a matajunta, la arista de cada una con luz. El filete de
     * la grada de arriba y la plaquita del exvoto (con dos clavos) van en el metal del nivel; en la grada de abajo, si hay sitio,
     * corre una greca azul. Un degradado: la plaquita.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val gradas = listOf(
            Rect(w * 0.2f, h * 0.772f, w * 0.8f, h * 0.834f),
            Rect(w * 0.15f, h * 0.834f, w * 0.85f, h * 0.896f),
            Rect(w * 0.1f, h * 0.896f, w * 0.9f, h * 0.958f),
        )
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { gradas.forEach { addRect(it) } }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        gradas.forEachIndexed { k, r ->
            drawRect(n(mezcla(MARMOL, MARMOL_OSC, k * 0.22f), oscuro), r.topLeft, r.size)
            if (fino) {
                vetas(r, n(MARMOL_SOMBRA, oscuro).copy(alpha = 0.22f), e.semilla + k, densidad = 0.3f, grosor = 0.45f, ondulacion = 1.6f)
                val nj = 3 + k
                for (j in 1 until nj + 1) {
                    val x = r.left + r.width * (j - (if (k % 2 == 1) 0.5f else 0f)) / nj
                    if (x > r.left + 1f && x < r.right - 1f) drawLine(n(MARMOL_SOMBRA, oscuro).copy(alpha = 0.5f), Offset(x, r.top), Offset(x, r.bottom), lw * 0.7f)
                }
            }
            drawRect(n(MARMOL_LUZ, oscuro), r.topLeft, Size(r.width, lw * 1.3f))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(r.left, r.bottom - lw), Size(r.width, lw))
        }
        // La greca de la grada de abajo.
        if (muyFino) {
            val g = gradas[2]; val lado = g.height * 0.5f; val yt = g.top + g.height * 0.25f; val yb = yt + lado
            val paso = maxOf(lado * 1.1f, 1f)
            val greca = Path(); var x = g.left + w * 0.04f; var k = 0
            while (x + paso < g.right - w * 0.04f && k < 60) {
                greca.moveTo(x, yb); greca.lineTo(x, yt); greca.lineTo(x + paso * 0.75f, yt); greca.lineTo(x + paso * 0.75f, yt + lado * 0.6f)
                greca.lineTo(x + paso * 0.35f, yt + lado * 0.6f); greca.lineTo(x + paso * 0.35f, yt + lado * 0.3f)
                greca.moveTo(x, yb); greca.lineTo(x + paso, yb)
                x += paso; k++
            }
            drawPath(greca, t(EGEO, oscuro).copy(alpha = 0.75f), style = Stroke(lw * 0.9f))
        }
        // El filete de metal en la arista de arriba y la plaquita del exvoto.
        val g0 = gradas[0]
        drawRect(e.base, g0.topLeft, Size(g0.width, maxOf(h * 0.006f, 0.8f * u)))
        drawLine(e.brillo, g0.topLeft, Offset(g0.right, g0.top), maxOf(0.4f * u, h * 0.002f))
        val g1 = gradas[1]
        val pr = Rect(w * 0.39f, g1.top + g1.height * 0.24f, w * 0.61f, g1.bottom - g1.height * 0.24f)
        drawRect(metalAnt(e, pr.left, pr.right), pr.topLeft, pr.size)
        drawRect(e.metalOscuro, pr.topLeft, pr.size, style = Stroke(lw * 0.8f))
        if (fino) {
            for (k in 0 until 6) {
                val x = pr.left + pr.width * (0.18f + k * 0.13f)
                drawLine(e.metalOscuro.copy(alpha = 0.8f), Offset(x, pr.top + pr.height * 0.3f), Offset(x + pr.width * 0.07f, pr.top + pr.height * 0.3f), lw * 0.7f)
                drawLine(e.metalOscuro.copy(alpha = 0.8f), Offset(x + pr.width * 0.035f, pr.top + pr.height * 0.3f), Offset(x + pr.width * 0.035f, pr.bottom - pr.height * 0.3f), lw * 0.7f)
            }
            clavoAnt(Offset(pr.left - w * 0.025f, pr.center.y), maxOf(h * 0.0062f, 0.9f * u), e)
            clavoAnt(Offset(pr.right + w * 0.025f, pr.center.y), maxOf(h * 0.0062f, 0.9f * u), e)
        }
        destelloAnt(Offset(pr.right, pr.top), h * 0.02f, e)
    }

    /**
     * La basa jónica de una columna, ancha: el plinto, el toro de abajo, la escocia honda con un anillo en el metal del nivel,
     * el toro de arriba y el arranque del fuste con sus estrías.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val plinto = Rect(w * 0.1f, h * 0.74f, w * 0.9f, h)
        val toroB = Rect(w * 0.14f, h * 0.5f, w * 0.86f, h * 0.76f)
        val escocia = Rect(w * 0.2f, h * 0.36f, w * 0.8f, h * 0.52f)
        val toroA = Rect(w * 0.18f, h * 0.18f, w * 0.82f, h * 0.38f)
        val fuste = Rect(w * 0.26f, 0f, w * 0.74f, h * 0.2f)
        if (!e.hecho) {
            drawPath(Path().apply {
                addRect(plinto); addRect(escocia); addRect(fuste)
                addRoundRect(androidx.compose.ui.geometry.RoundRect(toroB, CornerRadius(toroB.height / 2)))
                addRoundRect(androidx.compose.ui.geometry.RoundRect(toroA, CornerRadius(toroA.height / 2)))
            }, siluetaAnt(oscuro))
            return
        }
        val lw = maxOf(0.6f * u, h * 0.03f)
        val m = n(MARMOL, oscuro); val luz = n(MARMOL_LUZ, oscuro); val mo = n(MARMOL_OSC, oscuro); val ms = n(MARMOL_SOMBRA, oscuro)
        drawRect(mo, plinto.topLeft, plinto.size)
        bisel(plinto, luz.copy(alpha = 0.6f), Color.Black.copy(alpha = 0.18f), lw)
        drawRect(m, fuste.topLeft, fuste.size)
        for (k in 1 until 8) { val x = fuste.left + fuste.width * k / 8f; drawLine(ms.copy(alpha = 0.6f), Offset(x, 0f), Offset(x, fuste.bottom), lw * 0.7f) }
        drawRect(ms, escocia.topLeft, escocia.size)
        drawRect(Color.Black.copy(alpha = 0.18f), escocia.topLeft, Size(escocia.width, escocia.height * 0.4f))
        // El anillo de metal en la escocia.
        val ya = escocia.center.y
        drawRect(e.base, Offset(escocia.left, ya - h * 0.04f), Size(escocia.width, h * 0.08f))
        drawLine(e.brillo, Offset(escocia.left, ya - h * 0.035f), Offset(escocia.right, ya - h * 0.035f), maxOf(0.4f * u, h * 0.02f))
        drawLine(e.metalOscuro, Offset(escocia.left, ya + h * 0.035f), Offset(escocia.right, ya + h * 0.035f), maxOf(0.4f * u, h * 0.02f))
        listOf(toroB, toroA).forEach { r ->
            drawRoundRect(m, r.topLeft, r.size, CornerRadius(r.height / 2))
            drawRoundRect(luz, Offset(r.left + r.height * 0.3f, r.top + r.height * 0.14f), Size(r.width - r.height * 0.6f, r.height * 0.26f), CornerRadius(r.height * 0.13f))
            drawLine(mo, Offset(r.left + r.height / 2, r.bottom - lw / 2), Offset(r.right - r.height / 2, r.bottom - lw / 2), lw)
        }
        destelloAnt(Offset(escocia.right - w * 0.04f, ya), h * 0.16f, e)
    }

    override val margenPlaca: Dp = 10.dp
    /** El campo de la estela: mármol con luz de día; de noche, el azul del Egeo hundido. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) mezcla(EGEO, NOCHE, 0.74f) else MARMOL_LUZ
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) LLAMA else EGEO

    /**
     * Una estela de mármol con un frontón bajo en el margen de arriba (el tímpano apenas teñido y tres acróteras), el borde en
     * el metal del nivel y un filete fino por dentro; el campo, liso. Un degradado: el borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val bw = 1.6f * u
        val ped = minOf(margenPlaca.toPx() * 0.7f, h * 0.2f)
        val forma = Path().apply {
            moveTo(bw / 2, h - bw / 2); lineTo(bw / 2, ped); lineTo(w / 2, bw / 2); lineTo(w - bw / 2, ped); lineTo(w - bw / 2, h - bw / 2); close()
        }
        if (!e.hecho) { drawPath(forma, siluetaAnt(oscuro)); return }
        drawPath(forma, interior(oscuro))
        drawPath(Path().apply { moveTo(bw, ped); lineTo(w / 2, bw); lineTo(w - bw, ped); close() }, t(EGEO, oscuro).copy(alpha = if (oscuro) 0.3f else 0.12f))
        drawPath(forma, metalAnt(e, 0f, w), style = Stroke(bw))
        drawLine(e.metalOscuro.copy(alpha = 0.6f), Offset(bw, ped), Offset(w - bw, ped), 0.7f * u)
        val i = bw + 1.6f * u
        drawRect(e.metalOscuro.copy(alpha = 0.4f), Offset(i, ped + 1.6f * u), Size(w - 2 * i, h - ped - 1.6f * u - i), style = Stroke(0.6f * u))
        listOf(Offset(w / 2, bw), Offset(bw * 1.5f, ped), Offset(w - bw * 1.5f, ped)).forEach { clavoAnt(it, 1.2f * u, e) }
    }
}

// =====================================================================
//  Egipto
// =====================================================================

object LogrosEgipto : DisenoLogros {
    private val MADERA = Color(0xFF8E6A3E)
    private val MADERA_VETA = Color(0xFF5E4424)
    private val YESO = Color(0xFFF2EBDA)
    private val LAPIS = Color(0xFF1D3A8A)
    private val ROJO = Color(0xFFB0432A)
    private val VERDE = Color(0xFF3E7A4A)
    private val AMARILLO = Color(0xFFF2CF4A)
    private val NEGRO = Color(0xFF1E160C)
    private val LINO = Color(0xFFE6D8B4)
    /** La arenisca del zócalo y el granito de la mesa de ofrendas. */
    private val ARENISCA = Color(0xFFC9AE7A)
    private val ARENISCA_LUZ = Color(0xFFE0CA98)
    private val ARENISCA_OSC = Color(0xFF9A7E50)
    private val GRANITO = Color(0xFF8A6A4E)
    private val NOCHE = Color(0xFF110D08)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.76f) else c
    /** La piedra de noche: oscura de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.54f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El zócalo de arenisca: la gola (la cornisa cóncava) pintada con hojas de palma de colores, el bocel atado, el bloque un
     * poco en talud con su cenefa de rectángulos y, al frente, un cartucho cuya cuerda va en el metal del nivel, con tres signos
     * dentro (el sol, el agua y el junco). Un degradado: la cuerda del cartucho.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val ta = h * 0.778f; val tb = h * 0.804f
        val listel = Rect(w * 0.12f, h * 0.772f, w * 0.88f, ta)
        val gola = Path().apply {
            moveTo(w * 0.13f, ta); lineTo(w * 0.87f, ta); quadraticTo(w * 0.815f, ta + (tb - ta) * 0.35f, w * 0.81f, tb)
            lineTo(w * 0.19f, tb); quadraticTo(w * 0.185f, ta + (tb - ta) * 0.35f, w * 0.13f, ta); close()
        }
        val bocel = Rect(w * 0.18f, tb, w * 0.82f, h * 0.817f)
        val bloque = Path().apply { moveTo(w * 0.19f, h * 0.817f); lineTo(w * 0.81f, h * 0.817f); lineTo(w * 0.83f, h * 0.94f); lineTo(w * 0.17f, h * 0.94f); close() }
        val base = Rect(w * 0.15f, h * 0.94f, w * 0.85f, h * 0.958f)
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { addRect(listel); addPath(gola); addRect(bocel); addPath(bloque); addRect(base) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        val piedra = n(ARENISCA, oscuro); val luz = n(ARENISCA_LUZ, oscuro); val som = n(ARENISCA_OSC, oscuro)
        // El bloque, con sus juntas y la cenefa.
        drawPath(bloque, piedra)
        clipPath(bloque) {
            drawRect(luz.copy(alpha = 0.5f), Offset(w * 0.17f, h * 0.817f), Size(w * 0.04f, h * 0.13f))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(w * 0.79f, h * 0.817f), Size(w * 0.05f, h * 0.13f))
            if (fino) {
                drawLine(som.copy(alpha = 0.6f), Offset(0f, h * 0.878f), Offset(w * 0.36f, h * 0.878f), lw * 0.7f)
                drawLine(som.copy(alpha = 0.6f), Offset(w * 0.64f, h * 0.878f), Offset(w, h * 0.878f), lw * 0.7f)
                drawLine(som.copy(alpha = 0.6f), Offset(w * 0.27f, h * 0.878f), Offset(w * 0.27f, h * 0.94f), lw * 0.7f)
                drawLine(som.copy(alpha = 0.6f), Offset(w * 0.73f, h * 0.817f), Offset(w * 0.73f, h * 0.878f), lw * 0.7f)
                val colores = listOf(LAPIS, AMARILLO, ROJO, VERDE)
                val paso = maxOf(w * 0.034f, 1f)
                var x = w * 0.2f; var k = 0
                while (x + paso * 0.7f < w * 0.8f && k < 60) {
                    drawRect(t(colores[k % 4], oscuro), Offset(x, h * 0.922f), Size(paso * 0.7f, h * 0.011f))
                    x += paso; k++
                }
                drawLine(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(w * 0.2f, h * 0.92f), Offset(w * 0.8f, h * 0.92f), lw * 0.6f)
            }
        }
        // La gola, con las hojas de palma pintadas, y el listel de arriba.
        drawPath(gola, luz)
        if (fino) clipPath(gola) {
            val colores = listOf(LAPIS, VERDE, ROJO, VERDE)
            val n = 14
            for (k in 0 until n) {
                val x = w * (0.13f + 0.74f * (k + 0.5f) / n)
                drawRect(t(colores[k % 4], oscuro).copy(alpha = 0.85f), Offset(x - w * 0.0075f, ta), Size(w * 0.015f, tb - ta))
            }
        } else drawRect(t(LAPIS, oscuro).copy(alpha = 0.8f), Offset(w * 0.19f, tb - h * 0.006f), Size(w * 0.62f, h * 0.006f))
        drawRect(piedra, listel.topLeft, listel.size)
        drawLine(luz, listel.topLeft, Offset(listel.right, listel.top), lw)
        // El bocel, atado con cintas en diagonal.
        drawRoundRect(luz, bocel.topLeft, bocel.size, CornerRadius(bocel.height / 2))
        if (fino) {
            val paso = maxOf(bocel.height * 1.4f, 1f)
            var x = bocel.left + paso * 0.5f; var k = 0
            while (x < bocel.right - paso * 0.3f && k < 80) {
                drawLine(t(ROJO, oscuro).copy(alpha = 0.7f), Offset(x, bocel.bottom), Offset(x + bocel.height * 0.7f, bocel.top), lw * 0.9f)
                x += paso; k++
            }
        }
        drawLine(som, Offset(bocel.left, bocel.bottom - lw / 2), Offset(bocel.right, bocel.bottom - lw / 2), lw)
        drawRect(som, base.topLeft, base.size)
        drawLine(luz.copy(alpha = 0.6f), base.topLeft, Offset(base.right, base.top), lw)
        // El cartucho: el óvalo de yeso, la cuerda de metal y la barra de abajo; dentro, tres signos.
        val cart = Rect(w * 0.36f, h * 0.832f, w * 0.64f, h * 0.906f)
        drawOval(t(YESO, oscuro), cart.topLeft, cart.size)
        drawOval(metalAnt(e, cart.left, cart.right), cart.topLeft, cart.size, style = Stroke(maxOf(h * 0.0065f, 0.9f * u)))
        drawLine(e.base, Offset(cart.right - cart.width * 0.02f, cart.top + cart.height * 0.2f), Offset(cart.right - cart.width * 0.02f, cart.bottom - cart.height * 0.2f), maxOf(h * 0.008f, 1f * u))
        if (fino) {
            val cy = cart.center.y; val a = cart.height * 0.5f
            drawCircle(t(ROJO, oscuro), a * 0.32f, Offset(cart.left + cart.width * 0.27f, cy))
            val agua = Path().apply {
                val x0 = cart.left + cart.width * 0.4f; val x1 = cart.left + cart.width * 0.6f
                moveTo(x0, cy); for (j in 1..6) lineTo(x0 + (x1 - x0) * j / 6f, cy + (if (j % 2 == 0) a * 0.16f else -a * 0.16f))
            }
            drawPath(agua, t(LAPIS, oscuro), style = Stroke(lw))
            val xj = cart.left + cart.width * 0.75f
            drawLine(t(NEGRO, oscuro), Offset(xj, cy + a * 0.42f), Offset(xj, cy - a * 0.3f), lw)
            drawOval(t(VERDE, oscuro), Offset(xj - a * 0.05f, cy - a * 0.5f), Size(a * 0.22f, a * 0.32f))
        }
        if (muyFino) drawLine(e.metalOscuro.copy(alpha = 0.6f), Offset(cart.left + cart.width * 0.2f, cart.bottom + h * 0.005f), Offset(cart.right - cart.width * 0.2f, cart.bottom + h * 0.005f), lw)
        destelloAnt(Offset(cart.right, cart.top), h * 0.02f, e)
    }

    /**
     * La mesa de ofrendas sobre su bloque: la losa de granito con el filo de lapislázuli y, encima, dos jarras y un pan; en el
     * bloque, el signo hotep (el pan sobre la estera) en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val bloque = Rect(w * 0.18f, h * 0.4f, w * 0.82f, h)
        val losa = Rect(w * 0.08f, h * 0.22f, w * 0.92f, h * 0.42f)
        if (!e.hecho) { drawPath(Path().apply { addRect(bloque); addRect(losa) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.03f)
        drawRect(n(ARENISCA, oscuro), bloque.topLeft, bloque.size)
        bisel(bloque, n(ARENISCA_LUZ, oscuro).copy(alpha = 0.7f), n(ARENISCA_OSC, oscuro), lw)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), bloque.topLeft, Size(bloque.width, h * 0.07f))
        listOf(0.32f, 0.68f).forEach { x -> drawLine(n(ARENISCA_OSC, oscuro).copy(alpha = 0.6f), Offset(w * x, h * 0.5f), Offset(w * x, h), lw * 0.6f) }
        // El signo hotep en metal: la estera y el pan.
        val est = Rect(w * 0.4f, h * 0.76f, w * 0.6f, h * 0.86f)
        drawRect(metalAnt(e, est.left, est.right), est.topLeft, est.size)
        drawLine(e.metalOscuro, Offset(est.left, est.bottom), Offset(est.right, est.bottom), lw * 0.6f)
        drawArc(e.base, 180f, 180f, true, Offset(w * 0.46f, h * 0.6f), Size(w * 0.08f, h * 0.32f))
        drawArc(e.brillo, 200f, 60f, false, Offset(w * 0.465f, h * 0.62f), Size(w * 0.07f, h * 0.28f), style = Stroke(lw * 0.6f))
        destelloAnt(Offset(est.right, est.top), h * 0.16f, e)
        // Las ofrendas sobre la losa.
        drawOval(t(ROJO, oscuro), Offset(w * 0.2f, h * 0.0f), Size(w * 0.07f, h * 0.24f))
        drawRect(t(ROJO, oscuro), Offset(w * 0.215f, 0f), Size(w * 0.04f, h * 0.05f))
        drawOval(t(LAPIS, oscuro), Offset(w * 0.73f, h * 0.02f), Size(w * 0.065f, h * 0.22f))
        drawArc(t(AMARILLO, oscuro), 180f, 180f, true, Offset(w * 0.46f, h * 0.06f), Size(w * 0.09f, h * 0.3f))
        drawRect(n(GRANITO, oscuro), losa.topLeft, losa.size)
        drawRect(t(LAPIS, oscuro), losa.topLeft, Size(losa.width, h * 0.06f))
        drawLine(Color.Black.copy(alpha = 0.25f), Offset(losa.left, losa.bottom - lw / 2), Offset(losa.right, losa.bottom - lw / 2), lw)
    }

    override val margenPlaca: Dp = 9.dp
    /** El campo: yeso de día; de noche, lapislázuli en penumbra. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) mezcla(LAPIS, NOCHE, 0.72f) else YESO
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) YESO else NEGRO
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) AMARILLO else LAPIS

    /**
     * Un tablero de yeso con el borde en el metal del nivel y, en los márgenes de los lados, la cenefa de rectángulos de
     * colores entre dos filetes negros; el campo, liso. Un degradado: el borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val bw = 1.6f * u
        if (!e.hecho) { drawRect(siluetaAnt(oscuro)); return }
        drawRect(interior(oscuro))
        val colores = listOf(LAPIS, ROJO, VERDE, AMARILLO)
        val paso = maxOf(4.2f * u, 1f)
        listOf(bw + 1.2f * u, w - bw - 1.2f * u - 3f * u).forEach { x ->
            var y = bw + 1.4f * u; var k = 0
            while (y + paso * 0.7f < h - bw - 1.2f * u && k < 60) {
                drawRect(t(colores[k % 4], oscuro), Offset(x, y), Size(3f * u, paso * 0.7f))
                y += paso; k++
            }
            drawLine(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(x + 3.6f * u, bw), Offset(x + 3.6f * u, h - bw), 0.6f * u)
            drawLine(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(x - 0.6f * u, bw), Offset(x - 0.6f * u, h - bw), 0.6f * u)
        }
        drawRect(metalAnt(e, 0f, w), Offset(bw / 2, bw / 2), Size(w - bw, h - bw), style = Stroke(bw))
    }
}

// =====================================================================
//  Mesoamérica
// =====================================================================

object LogrosMeso : DisenoLogros {
    private val ESTUCO = Color(0xFFF2E8D2)
    private val CALIZA_OSC = Color(0xFFA8997C)
    private val CINABRIO = Color(0xFFA3301E)
    private val JADE = Color(0xFF2F7D57)
    /** El jade hondo, el del rótulo, para que se lea sobre el estuco. */
    private val JADE_HONDO = Color(0xFF1C5A3C)
    private val NEGRO = Color(0xFF2A1810)
    private val AZUL_MAYA = Color(0xFF5E9EAE)
    /** El rojo de la grana cochinilla: el del error. */
    private val GRANA = Color(0xFFA8144C)
    private val NOCHE = Color(0xFF100C08)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.76f) else c
    /** El estuco de noche: oscuro de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.56f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Un altar en talud y tablero: la cornisa, el tablero con su marco de cinabrio y el panel rehundido, y al centro un espejo
     * de pirita en el metal del nivel, flanqueado (si hay sitio) por el número del nivel en puntos y barra; abajo, el talud de
     * estuco con una sarta de cuentas de jade. Un degradado: el espejo.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val cornisa = Rect(w * 0.14f, h * 0.772f, w * 0.86f, h * 0.788f)
        val tablero = Rect(w * 0.17f, h * 0.788f, w * 0.83f, h * 0.872f)
        val talud = Path().apply { moveTo(w * 0.17f, h * 0.872f); lineTo(w * 0.83f, h * 0.872f); lineTo(w * 0.9f, h * 0.958f); lineTo(w * 0.1f, h * 0.958f); close() }
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { addRect(cornisa); addRect(tablero); addPath(talud) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        val estuco = n(ESTUCO, oscuro); val caliza = n(CALIZA_OSC, oscuro)
        // El talud, con su sarta de jade.
        drawPath(talud, n(mezcla(ESTUCO, CALIZA_OSC, 0.18f), oscuro))
        clipPath(talud) {
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(w * 0.8f, h * 0.872f), Size(w * 0.1f, h * 0.09f))
            drawRect(t(CINABRIO, oscuro), Offset(0f, h * 0.948f), Size(w, h * 0.01f))
            if (fino) for (k in 0 until 12) drawLine(caliza.copy(alpha = 0.35f), Offset(w * (0.15f + 0.7f * azar(e.semilla, k)), h * 0.88f), Offset(w * (0.15f + 0.7f * azar(e.semilla, k)) + u, h * (0.9f + 0.03f * azar(e.semilla, k + 20))), lw * 0.6f)
        }
        val cuentas = if (fino) 9 else 5
        val rj = maxOf(w * 0.016f, 0.9f * u)
        for (k in 0 until cuentas) {
            val c = Offset(w * (0.2f + 0.6f * k / (cuentas - 1f)), h * 0.912f)
            drawCircle(t(JADE, oscuro), rj, c)
            if (fino) drawCircle(Color.White.copy(alpha = 0.45f), rj * 0.35f, Offset(c.x - rj * 0.3f, c.y - rj * 0.3f))
        }
        // El tablero: el marco de cinabrio y el panel rehundido.
        drawRect(t(CINABRIO, oscuro), tablero.topLeft, tablero.size)
        val panel = Rect(tablero.left + w * 0.03f, tablero.top + h * 0.011f, tablero.right - w * 0.03f, tablero.bottom - h * 0.011f)
        drawRect(n(mezcla(ESTUCO, CALIZA_OSC, 0.3f), oscuro), panel.topLeft, panel.size)
        drawRect(Color.Black.copy(alpha = 0.2f), panel.topLeft, Size(panel.width, lw * 1.2f))
        drawRect(Color.Black.copy(alpha = 0.14f), panel.topLeft, Size(lw * 1.2f, panel.height))
        if (muyFino) {
            // Volutas pintadas en el panel, a los lados.
            listOf(0.26f, 0.74f).forEach { x ->
                drawArc(t(AZUL_MAYA, oscuro).copy(alpha = 0.6f), 0f, 270f, false, Offset(w * x - h * 0.014f, h * 0.818f), Size(h * 0.028f, h * 0.028f), style = Stroke(lw))
            }
        }
        // El espejo de pirita y el número del nivel en puntos y barra.
        val ec = Offset(w * 0.5f, tablero.center.y); val er = h * 0.027f
        drawCircle(metalAnt(e, ec.x - er, ec.x + er), er, ec)
        drawCircle(e.metalOscuro, er, ec, style = Stroke(lw))
        if (muyFino) for (k in 0 until 8) {
            val a = k * Math.PI / 4
            drawLine(e.metalOscuro.copy(alpha = 0.5f), ec, Offset(ec.x + (er * 0.9f * kotlin.math.cos(a)).toFloat(), ec.y + (er * 0.9f * kotlin.math.sin(a)).toFloat()), lw * 0.5f)
        }
        if (fino) listOf(0.37f, 0.63f).forEach { x ->
            val cx = w * x; val tinta = t(NEGRO, oscuro).copy(alpha = 0.75f)
            drawRect(tinta, Offset(cx - w * 0.04f, h * 0.84f), Size(w * 0.08f, h * 0.0075f))
            val rp = h * 0.0055f; val sep = w * 0.08f / 4f
            for (k in 0 until e.nivel) drawCircle(tinta, rp, Offset(cx + (k - (e.nivel - 1) / 2f) * sep, h * 0.826f))
        }
        // La cornisa.
        drawRect(estuco, cornisa.topLeft, cornisa.size)
        drawLine(caliza, Offset(cornisa.left, cornisa.bottom - lw / 2), Offset(cornisa.right, cornisa.bottom - lw / 2), lw)
        destelloAnt(Offset(ec.x + er, ec.y - er), h * 0.02f, e)
    }

    /**
     * Un basamento en miniatura: dos cuerpos en talud con su franja de cinabrio, la escalinata del medio con sus alfardas y,
     * en lo alto, la crestería en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val c1 = Path().apply { moveTo(w * 0.06f, h); lineTo(w * 0.94f, h); lineTo(w * 0.88f, h * 0.52f); lineTo(w * 0.12f, h * 0.52f); close() }
        val c2 = Path().apply { moveTo(w * 0.18f, h * 0.52f); lineTo(w * 0.82f, h * 0.52f); lineTo(w * 0.77f, h * 0.14f); lineTo(w * 0.23f, h * 0.14f); close() }
        val escal = Path().apply { moveTo(w * 0.42f, h * 0.14f); lineTo(w * 0.58f, h * 0.14f); lineTo(w * 0.61f, h); lineTo(w * 0.39f, h); close() }
        val cresta = Rect(w * 0.26f, 0f, w * 0.74f, h * 0.14f)
        if (!e.hecho) { drawPath(Path().apply { addPath(c1); addPath(c2); addRect(cresta) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.03f)
        drawPath(c1, n(mezcla(ESTUCO, CALIZA_OSC, 0.3f), oscuro))
        drawPath(c2, n(ESTUCO, oscuro))
        drawRect(t(CINABRIO, oscuro), Offset(w * 0.12f, h * 0.52f), Size(w * 0.76f, h * 0.12f))
        drawRect(t(CINABRIO, oscuro), Offset(w * 0.23f, h * 0.14f), Size(w * 0.54f, h * 0.1f))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.14f), Offset(w * 0.12f, h * 0.64f), Size(w * 0.76f, h * 0.05f))
        // La escalinata.
        drawPath(escal, n(mezcla(ESTUCO, Color.White, 0.2f), oscuro))
        clipPath(escal) { for (k in 1 until 9) { val y = h * (0.14f + 0.86f * k / 9f); drawLine(n(CALIZA_OSC, oscuro), Offset(0f, y), Offset(w, y), lw * 0.6f) } }
        drawLine(n(CALIZA_OSC, oscuro), Offset(w * 0.42f, h * 0.14f), Offset(w * 0.39f, h), lw)
        drawLine(n(CALIZA_OSC, oscuro), Offset(w * 0.58f, h * 0.14f), Offset(w * 0.61f, h), lw)
        // La crestería de metal: almenas escalonadas.
        drawRect(e.base, Offset(cresta.left, h * 0.08f), Size(cresta.width, h * 0.06f))
        for (k in 0 until 7) { val x = cresta.left + cresta.width * (k + 0.2f) / 7f; drawRect(e.base, Offset(x, 0f), Size(cresta.width * 0.6f / 7f, h * 0.1f)) }
        drawLine(e.brillo, Offset(cresta.left, h * 0.085f), Offset(cresta.right, h * 0.085f), maxOf(0.4f * u, h * 0.02f))
        drawLine(e.metalOscuro, Offset(cresta.left, h * 0.14f), Offset(cresta.right, h * 0.14f), maxOf(0.4f * u, h * 0.02f))
        destelloAnt(Offset(cresta.right, 0f + h * 0.05f), h * 0.14f, e)
    }

    override val margenPlaca: Dp = 9.dp
    /** El campo: estuco de día; de noche, jade hondo en penumbra. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) mezcla(JADE_HONDO, NOCHE, 0.7f) else ESTUCO
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) AZUL_MAYA else JADE_HONDO

    /**
     * Una cartela de estuco de esquinas redondas: la banda de cinabrio en el margen, con una tesela de jade en cada esquina, el
     * filo de afuera y el de adentro en el metal del nivel; el campo, liso.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val rad = CornerRadius(4f * u)
        if (!e.hecho) { drawRoundRect(siluetaAnt(oscuro), cornerRadius = rad); return }
        drawRoundRect(t(CINABRIO, oscuro), cornerRadius = rad)
        val b = 3.6f * u
        drawRoundRect(interior(oscuro), Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(2f * u))
        drawRoundRect(e.base, Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(2f * u), style = Stroke(1.1f * u))
        drawRoundRect(e.metalOscuro, Offset(0.5f * u, 0.5f * u), Size(w - u, h - u), rad, style = Stroke(1f * u))
        drawRoundRect(e.brillo.copy(alpha = 0.7f), Offset(1.3f * u, 1.3f * u), Size(w - 2.6f * u, h - 2.6f * u), CornerRadius(3.2f * u), style = Stroke(0.5f * u))
        val q = 2f * u
        listOf(Offset(b * 0.5f, b * 0.5f), Offset(w - b * 0.5f, b * 0.5f), Offset(b * 0.5f, h - b * 0.5f), Offset(w - b * 0.5f, h - b * 0.5f)).forEach { c ->
            drawRect(t(JADE, oscuro), Offset(c.x - q / 2, c.y - q / 2), Size(q, q))
        }
    }
}

// =====================================================================
//  Arcano: el archivo del anticuario
// =====================================================================

object LogrosArcano : DisenoLogros {
    private val PAPEL = Color(0xFFE8DFC8)
    private val PAPEL_VIEJO = Color(0xFFD6C7A0)
    private val FICHA = Color(0xFFF1EADA)
    private val TE = Color(0xFF9C7A48)
    private val RAYADO = Color(0xFF8AA0B8)
    private val MARGEN_ROJO = Color(0xFFB0473A)
    private val TINTA = Color(0xFF231A12)
    private val LATON = Color(0xFFB8963E)
    private val LATON_OSC = Color(0xFF6E5622)
    private val VERDE_RARO = Color(0xFFB8E07A)
    private val TAPETE = Color(0xFF2E4A3A)
    /** El nogal del fichero: su tono, su luz y su veta. */
    private val NOGAL = Color(0xFF5A3C26)
    private val NOGAL_LUZ = Color(0xFF7A5638)
    private val NOGAL_OSC = Color(0xFF34210F)
    /** El rojo del renglón de la ficha, más hondo para que el rótulo se lea. */
    private val ROJO_FICHA = Color(0xFF8E3328)
    private val NOCHE = Color(0xFF0A0806)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.32f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.76f) else c
    /** La madera y el cartón de noche: oscuros de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.42f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Un fichero de nogal: la tapa con su fieltro verde, el cuerpo con las vetas y los cajones de catálogo, cada uno con su
     * portaetiquetas y su tirador en el metal del nivel y su ficha escrita; si hay sitio, un cajón entreabierto deja escapar un
     * brillo verde raro. Sin degradados.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val tapa = Rect(w * 0.12f, h * 0.772f, w * 0.88f, h * 0.792f)
        val cuerpo = Rect(w * 0.15f, h * 0.792f, w * 0.85f, h * 0.934f)
        val zocalo = Rect(w * 0.13f, h * 0.934f, w * 0.87f, h * 0.948f)
        val patas = listOf(Rect(w * 0.15f, h * 0.948f, w * 0.22f, h * 0.958f), Rect(w * 0.78f, h * 0.948f, w * 0.85f, h * 0.958f))
        sombraPieAnt(w * 0.08f, w * 0.92f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { addRect(tapa); addRect(cuerpo); addRect(zocalo); patas.forEach { addRect(it) } }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        val nogal = n(NOGAL, oscuro); val luz = n(NOGAL_LUZ, oscuro); val veta = n(NOGAL_OSC, oscuro)
        drawRect(nogal, cuerpo.topLeft, cuerpo.size)
        if (fino) vetas(cuerpo, veta.copy(alpha = 0.5f), e.semilla, densidad = 0.6f, grosor = 0.5f)
        // Los cajones.
        val cols = 2; val filas = if (fino) 2 else 1
        val g = w * 0.022f
        val cw = (cuerpo.width - g * (cols + 1)) / cols; val ch = (cuerpo.height - g * (filas + 1)) / filas
        for (f in 0 until filas) for (c in 0 until cols) {
            val r = Rect(cuerpo.left + g + c * (cw + g), cuerpo.top + g + f * (ch + g), cuerpo.left + g + c * (cw + g) + cw, cuerpo.top + g + f * (ch + g) + ch)
            val abierto = muyFino && f == 0 && c == 1
            if (abierto) {
                drawRect(Color.Black.copy(alpha = 0.7f), r.topLeft, Size(r.width, h * 0.006f))
                drawRect(t(VERDE_RARO, oscuro).copy(alpha = if (oscuro) 0.7f else 0.5f), Offset(r.left + r.width * 0.1f, r.top + h * 0.0015f), Size(r.width * 0.8f, h * 0.0025f))
            }
            val rr = if (abierto) Rect(r.left, r.top + h * 0.006f, r.right, r.bottom) else r
            drawRect(luz, rr.topLeft, rr.size)
            bisel(rr, Color.White.copy(alpha = if (oscuro) 0.06f else 0.14f), Color.Black.copy(alpha = 0.3f), lw)
            // El portaetiquetas con su ficha, y el tirador.
            val et = Rect(rr.center.x - rr.width * 0.24f, rr.top + rr.height * 0.18f, rr.center.x + rr.width * 0.24f, rr.top + rr.height * (if (fino) 0.5f else 0.46f))
            drawRect(t(FICHA, oscuro), et.topLeft, et.size)
            if (fino) drawLine(t(TINTA, oscuro).copy(alpha = 0.55f), Offset(et.left + et.width * 0.18f, et.center.y), Offset(et.right - et.width * (0.2f + 0.25f * azar(e.semilla, f * 2 + c)), et.center.y), lw * 0.7f)
            drawRect(e.base, et.topLeft, et.size, style = Stroke(maxOf(lw * 0.9f, 0.7f * u)))
            drawLine(e.brillo, Offset(et.left, et.top), Offset(et.right, et.top), lw * 0.5f)
            val tc = Offset(rr.center.x, rr.top + rr.height * 0.74f)
            if (fino) {
                val tr = rr.height * 0.16f
                drawArc(e.metalOscuro, 0f, 180f, false, Offset(tc.x - tr, tc.y - tr * 0.8f), Size(tr * 2, tr * 1.6f), style = Stroke(lw * 1.4f))
                drawArc(e.base, 0f, 180f, false, Offset(tc.x - tr, tc.y - tr * 0.9f), Size(tr * 2, tr * 1.6f), style = Stroke(lw))
                clavoAnt(Offset(tc.x - tr, tc.y - tr * 0.1f), lw * 1.2f, e); clavoAnt(Offset(tc.x + tr, tc.y - tr * 0.1f), lw * 1.2f, e)
            } else clavoAnt(tc, maxOf(rr.height * 0.12f, 0.8f * u), e)
            if (f == 0 && c == 0) destelloAnt(Offset(et.right, et.top), h * 0.02f, e)
        }
        // La tapa con el fieltro, el zócalo y las patas.
        drawRect(luz, tapa.topLeft, tapa.size)
        bisel(tapa, Color.White.copy(alpha = if (oscuro) 0.06f else 0.16f), Color.Black.copy(alpha = 0.3f), lw)
        drawRect(t(TAPETE, oscuro), Offset(tapa.left + w * 0.03f, tapa.top), Size(tapa.width - w * 0.06f, h * 0.005f))
        drawRect(Color.Black.copy(alpha = 0.25f), cuerpo.topLeft, Size(cuerpo.width, h * 0.005f))
        drawRect(veta, zocalo.topLeft, zocalo.size)
        drawLine(luz, zocalo.topLeft, Offset(zocalo.right, zocalo.top), lw)
        patas.forEach { drawRect(veta, it.topLeft, it.size) }
    }

    /**
     * Dos cajas de archivo de cartón apiladas, atadas con un cordel, con su tapa, sus portaetiquetas en el metal del nivel (la
     * ficha dentro) y una mancha de humedad.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cajaB = Rect(w * 0.1f, h * 0.46f, w * 0.9f, h)
        val cajaA = Rect(w * 0.2f, h * 0.02f, w * 0.78f, h * 0.46f)
        if (!e.hecho) { drawPath(Path().apply { addRect(cajaA); addRect(cajaB) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.03f)
        listOf(cajaB to TE, cajaA to PAPEL_VIEJO).forEachIndexed { k, (r, c) ->
            drawRect(n(c, oscuro), r.topLeft, r.size)
            drawRect(n(mezcla(c, Color.Black, 0.18f), oscuro), r.topLeft, Size(r.width, r.height * 0.24f))
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(r.left, r.top + r.height * 0.24f), Offset(r.right, r.top + r.height * 0.24f), lw * 0.6f)
            bisel(r, Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Color.Black.copy(alpha = 0.22f), lw * 0.7f)
            val et = if (k == 0) Rect(w * 0.58f, h * 0.62f, w * 0.8f, h * 0.88f) else Rect(w * 0.28f, h * 0.16f, w * 0.46f, h * 0.4f)
            drawRect(t(FICHA, oscuro), et.topLeft, et.size)
            drawLine(t(TINTA, oscuro).copy(alpha = 0.5f), Offset(et.left + et.width * 0.15f, et.center.y), Offset(et.right - et.width * 0.25f, et.center.y), lw * 0.6f)
            drawRect(e.base, et.topLeft, et.size, style = Stroke(maxOf(lw * 0.8f, 0.6f * u)))
            clavoAnt(Offset(et.left - w * 0.012f, et.center.y), h * 0.05f, e)
            clavoAnt(Offset(et.right + w * 0.012f, et.center.y), h * 0.05f, e)
        }
        drawCircle(Color(0xFF3A2A12).copy(alpha = 0.14f), h * 0.2f, Offset(w * 0.24f, h * 0.76f))
        val cordel = n(LATON_OSC, oscuro)
        drawLine(cordel, Offset(w * 0.52f, h * 0.02f), Offset(w * 0.52f, h), lw * 0.7f)
        drawLine(cordel, Offset(w * 0.1f, h * 0.72f), Offset(w * 0.9f, h * 0.72f), lw * 0.7f)
        drawCircle(cordel, h * 0.05f, Offset(w * 0.52f, h * 0.72f))
        destelloAnt(Offset(w * 0.8f, h * 0.62f), h * 0.14f, e)
    }

    override val margenPlaca: Dp = 9.dp
    /** El campo: la ficha de día; de noche, el tapete verde del archivo. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) mezcla(TAPETE, NOCHE, 0.6f) else FICHA
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) FICHA else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) VERDE_RARO else ROJO_FICHA

    /**
     * Una ficha de catálogo en su portaetiquetas: el marco en el metal del nivel con un remache a cada lado, el renglón rojo
     * en el margen de arriba y la perforación de la varilla en el de abajo; el campo, liso. Un degradado: el marco.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val bw = 1.8f * u; val m = margenPlaca.toPx()
        if (!e.hecho) { drawRoundRect(siluetaAnt(oscuro), cornerRadius = CornerRadius(2f * u)); return }
        drawRoundRect(interior(oscuro), cornerRadius = CornerRadius(2f * u))
        drawLine(t(MARGEN_ROJO, oscuro).copy(alpha = if (oscuro) 0.8f else 0.7f), Offset(bw + 2f * u, m * 0.6f), Offset(w - bw - 2f * u, m * 0.6f), 0.7f * u)
        drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.18f), 1.5f * u, Offset(w / 2, h - m * 0.5f))
        drawRoundRect(metalAnt(e, 0f, w), Offset(bw / 2, bw / 2), Size(w - bw, h - bw), CornerRadius(2f * u), style = Stroke(bw))
        clavoAnt(Offset(m * 0.45f, h / 2), 1.3f * u, e)
        clavoAnt(Offset(w - m * 0.45f, h / 2), 1.3f * u, e)
    }
}

// =====================================================================
//  Antiguas: las tierras de las runas
// =====================================================================

object LogrosAntiguas : DisenoLogros {
    private val MADERA = Color(0xFF7A5232)
    private val NUDO = Color(0xFF3A240F)
    private val TALLA = Color(0xFF5E3E22)
    private val PERGAMINO = Color(0xFFE6D3A6)
    private val PERGAMINO_OSC = Color(0xFFB89C68)
    private val TINTA = Color(0xFF3A2614)
    private val LATON = Color(0xFFC9A04A)
    private val LATON_OSC = Color(0xFF7E6024)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val LLAMA = Color(0xFFF4C66A)
    /** El latón viejo del rótulo, oscuro para que se lea sobre el pergamino. */
    private val LATON_VIEJO = Color(0xFF654A16)
    private val ROJO_MAPA = Color(0xFF9A2A1E)
    /** El granito de la mina: su tono, su luz y su sombra; y el hierro de los herrajes. */
    private val GRANITO = Color(0xFF6E6A63)
    private val GRANITO_LUZ = Color(0xFF908C84)
    private val GRANITO_OSC = Color(0xFF4A4741)
    private val HIERRO = Color(0xFF34322F)
    private val NOCHE = Color(0xFF0D0906)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.76f) else c
    /** La piedra y la madera de noche: oscuras de verdad. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, NOCHE, 0.42f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * Un plinto de granito de la mina, de aristas achaflanadas: la losa de arriba, el cuerpo con sus caras de lado en sombra y el
     * moteado de la piedra, ceñido por un fleje en el metal del nivel con sus remaches y una piedra en rombo al centro; debajo
     * del fleje, runas talladas (en el cristal del legendario, encendidas), y arriba una cenefa en zigzag si hay sitio. Un
     * degradado: el fleje.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 80f * u; val muyFino = h > 140f * u
        val ch = w * 0.025f
        val losa = Path().apply {
            moveTo(w * 0.13f, h * 0.796f); lineTo(w * 0.13f, h * 0.784f); lineTo(w * 0.13f + ch, h * 0.772f); lineTo(w * 0.87f - ch, h * 0.772f)
            lineTo(w * 0.87f, h * 0.784f); lineTo(w * 0.87f, h * 0.796f); close()
        }
        val cuerpo = Rect(w * 0.18f, h * 0.796f, w * 0.82f, h * 0.93f)
        val base = Path().apply {
            moveTo(w * 0.11f, h * 0.958f); lineTo(w * 0.11f, h * 0.942f); lineTo(w * 0.11f + ch, h * 0.93f); lineTo(w * 0.89f - ch, h * 0.93f)
            lineTo(w * 0.89f, h * 0.942f); lineTo(w * 0.89f, h * 0.958f); close()
        }
        sombraPieAnt(w * 0.07f, w * 0.93f, h * 0.958f, h * 0.022f, e.hecho, oscuro)
        if (!e.hecho) { drawPath(Path().apply { addPath(losa); addRect(cuerpo); addPath(base) }, siluetaAnt(oscuro)); return }
        val lw = maxOf(0.6f * u, h * 0.0035f)
        val g = n(GRANITO, oscuro); val luz = n(GRANITO_LUZ, oscuro); val som = n(GRANITO_OSC, oscuro)
        // El cuerpo: la cara del frente y los chaflanes en sombra.
        drawRect(g, cuerpo.topLeft, cuerpo.size)
        drawRect(som, cuerpo.topLeft, Size(w * 0.045f, cuerpo.height))
        drawRect(som, Offset(cuerpo.right - w * 0.045f, cuerpo.top), Size(w * 0.045f, cuerpo.height))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.right - w * 0.045f, cuerpo.top), Size(w * 0.045f, cuerpo.height))
        if (fino) for (k in 0 until 40) {
            val c = Offset(cuerpo.left + cuerpo.width * azar(e.semilla, k), cuerpo.top + cuerpo.height * azar(e.semilla, k + 50))
            drawCircle((if (k % 3 == 0) luz else som).copy(alpha = 0.7f), u * (0.35f + 0.45f * azar(e.semilla, k + 100)), c)
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.2f), cuerpo.topLeft, Size(cuerpo.width, h * 0.007f))
        // La cenefa en zigzag, arriba del fleje.
        if (muyFino) {
            val y0 = h * 0.806f; val y1 = h * 0.83f
            val paso = maxOf(w * 0.04f, 1f)
            val zz = Path(); var x = w * 0.24f; var k = 0
            zz.moveTo(x, y1)
            while (x + paso / 2 < w * 0.76f && k < 60) { zz.lineTo(x + paso / 2, y0); zz.lineTo(x + paso, y1); x += paso; k++ }
            drawPath(zz, som, style = Stroke(lw))
            drawPath(zz, luz.copy(alpha = 0.4f), style = Stroke(lw * 0.5f))
        }
        // Las runas talladas: el surco oscuro y la luz en su labio de abajo.
        if (fino) {
            val ry = h * 0.897f; val alto = h * 0.034f
            val encendidas = e.nivel >= 4
            runasAnt(Offset(w * 0.27f, ry + lw * 0.8f), w * 0.46f, alto, luz.copy(alpha = 0.5f), lw, 6, e.semilla)
            runasAnt(Offset(w * 0.27f, ry), w * 0.46f, alto, if (encendidas) e.brillo else Color.Black.copy(alpha = 0.55f), lw * 1.1f, 6, e.semilla)
        }
        // El fleje de metal, sus remaches y la piedra del centro.
        val fy0 = h * 0.842f; val fy1 = h * 0.866f
        drawRect(metalAnt(e, cuerpo.left, cuerpo.right), Offset(cuerpo.left, fy0), Size(cuerpo.width, fy1 - fy0))
        drawLine(e.metalOscuro, Offset(cuerpo.left, fy1), Offset(cuerpo.right, fy1), lw)
        val remaches = if (fino) 6 else 2
        for (k in 0 until remaches) {
            val f = if (remaches == 2) (if (k == 0) 0.1f else 0.9f) else 0.06f + 0.88f * k / (remaches - 1f)
            val x = cuerpo.left + cuerpo.width * f
            if (kotlin.math.abs(x - w * 0.5f) > w * 0.08f) clavoAnt(Offset(x, (fy0 + fy1) / 2), maxOf(h * 0.0055f, 0.8f * u), e)
        }
        val rc = Offset(w * 0.5f, (fy0 + fy1) / 2); val rr = h * 0.022f
        val rombo = Path().apply { moveTo(rc.x, rc.y - rr); lineTo(rc.x + rr * 1.1f, rc.y); lineTo(rc.x, rc.y + rr); lineTo(rc.x - rr * 1.1f, rc.y); close() }
        drawPath(rombo, e.metalOscuro)
        drawPath(Path().apply { moveTo(rc.x, rc.y - rr * 0.7f); lineTo(rc.x + rr * 0.75f, rc.y); lineTo(rc.x, rc.y + rr * 0.7f); lineTo(rc.x - rr * 0.75f, rc.y); close() }, if (e.nivel >= 4) e.brillo else e.base)
        drawLine(Color.White.copy(alpha = 0.6f), Offset(rc.x - rr * 0.4f, rc.y - rr * 0.1f), Offset(rc.x, rc.y - rr * 0.5f), lw * 0.7f)
        // La losa y la base.
        drawPath(losa, luz)
        drawLine(som, Offset(w * 0.13f, h * 0.796f - lw / 2), Offset(w * 0.87f, h * 0.796f - lw / 2), lw)
        drawPath(base, som)
        drawLine(luz.copy(alpha = 0.6f), Offset(w * 0.11f + ch, h * 0.93f + lw / 2), Offset(w * 0.89f - ch, h * 0.93f + lw / 2), lw)
        destelloAnt(Offset(rc.x + rr, rc.y - rr), h * 0.022f, e)
    }

    /**
     * Un arcón de madera: la tapa combada, el cuerpo de tablones con su veta, dos flejes de hierro con sus clavos, las
     * cantoneras y, al centro, la cerradura en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val tapa = Rect(w * 0.12f, h * 0.02f, w * 0.88f, h * 0.34f)
        val caja = Rect(w * 0.14f, h * 0.3f, w * 0.86f, h * 0.92f)
        val patas = listOf(Rect(w * 0.16f, h * 0.92f, w * 0.24f, h), Rect(w * 0.76f, h * 0.92f, w * 0.84f, h))
        if (!e.hecho) {
            drawPath(Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(tapa, CornerRadius(h * 0.14f))); addRect(caja); patas.forEach { addRect(it) } }, siluetaAnt(oscuro))
            return
        }
        val lw = maxOf(0.6f * u, h * 0.03f)
        val madera = n(MADERA, oscuro); val talla = n(TALLA, oscuro); val nudo = n(NUDO, oscuro); val hierro = n(HIERRO, oscuro)
        patas.forEach { drawRect(nudo, it.topLeft, it.size) }
        drawRect(madera, caja.topLeft, caja.size)
        vetas(caja, nudo.copy(alpha = 0.45f), e.semilla, densidad = 0.5f, grosor = 0.45f)
        listOf(0.55f, 0.75f).forEach { f -> drawLine(nudo.copy(alpha = 0.7f), Offset(caja.left, h * f), Offset(caja.right, h * f), lw * 0.6f) }
        drawRoundRect(talla, tapa.topLeft, tapa.size, CornerRadius(h * 0.14f))
        drawRoundRect(n(mezcla(TALLA, Color.White, 0.18f), oscuro), Offset(tapa.left + w * 0.02f, tapa.top + h * 0.04f), Size(tapa.width - w * 0.04f, h * 0.06f), CornerRadius(h * 0.03f))
        drawLine(Color.Black.copy(alpha = 0.35f), Offset(caja.left, h * 0.34f), Offset(caja.right, h * 0.34f), lw)
        // Los flejes de hierro y sus clavos; las cantoneras.
        listOf(0.25f, 0.75f).forEach { x ->
            drawRect(hierro, Offset(w * x - w * 0.022f, tapa.top), Size(w * 0.044f, caja.bottom - tapa.top))
            for (k in 0 until 4) drawCircle(n(GRANITO_LUZ, oscuro), h * 0.035f, Offset(w * x, h * (0.12f + k * 0.24f)))
        }
        listOf(caja.left, caja.right - w * 0.05f).forEach { x -> drawRect(hierro, Offset(x, caja.bottom - h * 0.16f), Size(w * 0.05f, h * 0.16f)) }
        // La cerradura en el metal del nivel.
        val cer = Rect(w * 0.45f, h * 0.3f, w * 0.55f, h * 0.66f)
        drawRect(metalAnt(e, cer.left, cer.right), cer.topLeft, cer.size)
        drawRect(e.metalOscuro, cer.topLeft, cer.size, style = Stroke(lw * 0.7f))
        drawCircle(nudo, h * 0.055f, Offset(cer.center.x, cer.top + cer.height * 0.42f))
        drawRect(nudo, Offset(cer.center.x - w * 0.006f, cer.top + cer.height * 0.42f), Size(w * 0.012f, cer.height * 0.32f))
        destelloAnt(Offset(cer.right, cer.top), h * 0.14f, e)
    }

    override val margenPlaca: Dp = 9.dp
    /** El campo: pergamino de día; de noche, la madera en penumbra. */
    private fun interior(oscuro: Boolean): Color = if (oscuro) hondo(MADERA, true) else PERGAMINO
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else LATON_VIEJO

    /**
     * Una chapa de esquinas cortadas: el borde en el metal del nivel, un filete grabado por dentro y un remache en cada esquina;
     * el campo de pergamino, liso. Un degradado: el borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val bw = 1.8f * u; val c = 3.4f * u
        val forma = Path().apply {
            val a = bw / 2
            moveTo(a + c, a); lineTo(w - a - c, a); lineTo(w - a, a + c); lineTo(w - a, h - a - c); lineTo(w - a - c, h - a)
            lineTo(a + c, h - a); lineTo(a, h - a - c); lineTo(a, a + c); close()
        }
        if (!e.hecho) { drawPath(forma, siluetaAnt(oscuro)); return }
        drawPath(forma, interior(oscuro))
        drawPath(forma, metalAnt(e, 0f, w), style = Stroke(bw))
        val i = bw + 1.8f * u
        drawRect(t(LATON_OSC, oscuro).copy(alpha = 0.55f), Offset(i + c * 0.4f, i), Size(w - 2 * i - c * 0.8f, h - 2 * i), style = Stroke(0.6f * u))
        val ri = bw + 2.3f * u
        listOf(Offset(ri, ri), Offset(w - ri, ri), Offset(ri, h - ri), Offset(w - ri, h - ri)).forEach { clavoAnt(it, 1.2f * u, e) }
    }
}
