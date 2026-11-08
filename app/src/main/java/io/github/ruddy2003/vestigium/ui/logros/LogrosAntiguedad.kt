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
import kotlin.math.roundToInt

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

// ---------------------------------------------------------------- ayudantes del salón del hall

/**
 * Un halo de luz sin degradado (para no gastar los dos que admite cada dibujo): [capas] círculos concéntricos, cada uno más
 * chico, que se suman hacia el centro. El alfa de [color] es el del centro.
 */
private fun DrawScope.haloAnt(c: Offset, r: Float, color: Color, capas: Int = 6) {
    if (r <= 0f) return
    val a = color.alpha / capas
    for (k in 0 until capas) drawCircle(color.copy(alpha = a), r * (1f - k / (capas + 0.4f)), c)
}

/** Una llama en forma de gota con la base en [b] y [alto] de alto: la de fuera y, adentro, el corazón más claro. */
private fun DrawScope.llamaAnt(b: Offset, alto: Float, fuera: Color, dentro: Color) {
    fun gota(a: Float, an: Float) = Path().apply {
        moveTo(b.x, b.y - a)
        cubicTo(b.x + an * 0.2f, b.y - a * 0.62f, b.x + an, b.y - a * 0.36f, b.x + an * 0.55f, b.y - a * 0.06f)
        quadraticTo(b.x, b.y + a * 0.08f, b.x - an * 0.55f, b.y - a * 0.06f)
        cubicTo(b.x - an, b.y - a * 0.36f, b.x - an * 0.2f, b.y - a * 0.62f, b.x, b.y - a)
        close()
    }
    drawPath(gota(alto, alto * 0.42f), fuera)
    drawPath(gota(alto * 0.55f, alto * 0.24f), dentro)
}

/**
 * Las filas del salón: cuántas de ≈ 230 dp caben en el alto (de 1 a 7) y el alto de cada una. La primera empieza a 12 dp
 * de arriba, como la cuadrícula del hall.
 */
private fun DrawScope.filasSalonAnt(): Pair<Int, Float> {
    val util = size.height - 24f * density
    val filas = (util / (230f * density)).roundToInt().coerceIn(1, 7)
    return filas to util / filas
}

/** Qué tan lejos queda [x] del centro de su franja de trofeos: 0 en el centro, 1 en la junta entre franjas. */
private fun cercaJuntaAnt(x: Float, paso: Float): Float {
    val f = x / maxOf(paso, 1f)
    return (kotlin.math.abs(f - kotlin.math.floor(f) - 0.5f) * 2f).coerceIn(0f, 1f)
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

    // ---------------------------------------------------------------- el salón del hall

    /** Un bisonte de perfil, de [ancho] de ancho con el centro en [c]; [lado] = 1 mira a la derecha, −1 a la izquierda. */
    private fun bisonteCueva(c: Offset, ancho: Float, lado: Float): Path = Path().apply {
        val a = ancho
        fun p(x: Float, y: Float) = Offset(c.x + lado * x * a, c.y + y * a)
        fun m(x: Float, y: Float) { val q = p(x, y); moveTo(q.x, q.y) }
        fun l(x: Float, y: Float) { val q = p(x, y); lineTo(q.x, q.y) }
        fun q2(x1: Float, y1: Float, x2: Float, y2: Float) { val q = p(x1, y1); val r = p(x2, y2); quadraticTo(q.x, q.y, r.x, r.y) }
        fun c3(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
            val q = p(x1, y1); val r = p(x2, y2); val s = p(x3, y3); cubicTo(q.x, q.y, r.x, r.y, s.x, s.y)
        }
        m(-0.46f, -0.03f)
        c3(-0.3f, -0.12f, -0.06f, -0.13f, 0.08f, -0.22f)          // el lomo, que sube hasta la giba
        c3(0.18f, -0.29f, 0.3f, -0.26f, 0.35f, -0.13f)            // la giba y la cerviz
        q2(0.45f, -0.11f, 0.5f, 0.0f)                             // la frente
        l(0.49f, 0.09f)                                           // el morro
        q2(0.42f, 0.15f, 0.34f, 0.11f)                            // la barba
        l(0.31f, 0.22f); l(0.27f, 0.23f); l(0.24f, 0.11f)         // la pata de delante
        q2(0.0f, 0.13f, -0.24f, 0.08f)                            // el vientre
        l(-0.27f, 0.22f); l(-0.32f, 0.22f); l(-0.37f, 0.06f)      // la pata de atrás
        q2(-0.47f, 0.05f, -0.46f, -0.03f)
        close()
        // El cuerno y la cola.
        m(0.37f, -0.13f); q2(0.41f, -0.22f, 0.47f, -0.2f); q2(0.42f, -0.17f, 0.4f, -0.11f); close()
        m(-0.46f, -0.02f); q2(-0.53f, 0.04f, -0.5f, 0.12f); q2(-0.49f, 0.04f, -0.45f, 0.0f); close()
    }

    /**
     * La sala de los bisontes, en lo hondo de la cueva. La pared de caliza con sus estratos y sus poros y, detrás de cada
     * trofeo, un bisonte pintado (de ocre o de carbón, mirando a un lado y al otro) muy tenue; entre las columnas de trofeos,
     * coladas de roca que bajan del techo hasta el suelo, con manos en negativo, hileras de puntos de ocre y rayas de cuenta;
     * arriba, el techo con estalactitas, más largas sobre las coladas; abajo, el suelo de tierra con cantos, conchas con
     * pigmento y las lámparas de grasa. De día entra la luz por la boca de la cueva, arriba a la izquierda; de noche alumbran
     * solo las lámparas, en el suelo y en las repisas de las coladas. Dos degradados: la roca de arriba abajo y la luz de la boca.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 31
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.7f, 1f) else k)
        val piso = h - 14f * u
        val pw = minOf(9f * u, paso * 0.055f)
        val fuego = Color(0xFFFFB347); val brasa = Color(0xFFFFE2A0)

        // La roca: más honda arriba, donde empieza el techo, y abajo, junto al suelo.
        drawRect(Brush.verticalGradient(listOf(v(ROCA_HONDA, 0.36f), v(ROCA, 0.16f), v(ROCA_LUZ, 0.08f), v(ROCA_OSC, 0.24f)), 0f, h))
        // La boca de la cueva: la luz del día, o de noche un poco de luna, arriba a la izquierda.
        val boca = Offset(-w * 0.05f, -h * 0.02f); val rb = maxOf(w * 0.95f, 1f)
        drawCircle(Brush.radialGradient(listOf((if (oscuro) Color(0xFF7088A8).copy(alpha = 0.10f) else CALIZA.copy(alpha = 0.55f)), Color.Transparent), boca, rb), rb, boca)

        // Los estratos de la caliza: vetas onduladas a lo ancho, cada ≈ 34 dp, y los poros.
        var y = 26f * u; var k = 0
        while (y < piso && k < 48) {
            val p = Path().apply {
                moveTo(0f, y)
                for (j in 1..8) {
                    val x = w * j / 8f
                    quadraticTo(x - w / 16f, y + (azar(s, k * 9 + j) - 0.5f) * 10f * u, x, y + (azar(s, k * 9 + j + 4) - 0.5f) * 4f * u)
                }
            }
            drawPath(p, v(ROCA_HONDA, 0.08f + 0.07f * azar(s, 300 + k)), style = Stroke((0.6f + 0.8f * azar(s, 400 + k)) * u))
            y += maxOf(34f * u * (0.7f + 0.6f * azar(s, 500 + k)), 1f); k++
        }
        val poros = (w * h / (2600f * u * u)).toInt().coerceIn(20, 90)
        for (j in 0 until poros) {
            val x = w * azar(s, 1000 + j); val yy = piso * azar(s, 1200 + j)
            val kk = 0.1f + 0.22f * cercaJuntaAnt(x, paso)
            drawCircle(v(ROCA_HONDA, kk), (0.5f + 1.1f * azar(s, 1400 + j)) * u, Offset(x, yy))
        }

        // Detrás de cada trofeo, un bisonte pintado: ocre o carbón, tenue, alternando hacia dónde mira.
        val carbon = if (oscuro) mezcla(e.fondo, Color.Black, 0.22f) else v(CARBON, 0.13f)
        for (r in 0 until filas) for (i in 0 until cols) {
            val cx = (i + 0.5f) * paso; val cy = 12f * u + r * fila + fila * 0.3f
            val ocre = (r + i) % 2 == 0
            val lado = if ((r * 3 + i) % 2 == 0) 1f else -1f
            val an = minOf(paso * 0.82f, 160f * u)
            drawPath(bisonteCueva(Offset(cx, cy), an, lado), if (ocre) v(mezcla(OCRE, OCRE_AMARILLO, 0.35f), if (oscuro) 0.16f else 0.17f) else carbon)
            // El contorno de carbón por encima del lomo, a medio hacer, como en las pinturas.
            if (ocre) drawPath(bisonteCueva(Offset(cx, cy), an, lado), v(CARBON, if (oscuro) 0.05f else 0.12f), style = Stroke(1.4f * u))
        }

        // Las coladas de roca entre las columnas de trofeos: de techo a suelo, onduladas, con el pie ensanchado.
        for (i in 0..cols) {
            val x = i * paso
            val colada = Path().apply {
                moveTo(x - pw * 1.7f, 0f)
                var yy = 0f; var j = 0
                while (yy < piso && j < 40) { yy = minOf(yy + maxOf(38f * u, 1f), piso); lineTo(x - pw * (0.75f + 0.5f * azar(s, 600 + i * 50 + j)), yy); j++ }
                quadraticTo(x - pw * 1.2f, h - 4f * u, x - pw * 2.2f, h)
                lineTo(x + pw * 2.2f, h)
                quadraticTo(x + pw * 1.2f, h - 4f * u, x + pw * 0.9f, piso)
                j = 0
                while (yy > 0f && j < 40) { yy = maxOf(yy - maxOf(38f * u, 1f), 0f); lineTo(x + pw * (0.75f + 0.5f * azar(s, 700 + i * 50 + j)), yy); j++ }
                lineTo(x + pw * 1.7f, 0f); close()
            }
            drawPath(colada, v(ROCA_OSC, 0.34f))
            // Los pliegues de la colada: la luz a la izquierda, la sombra a la derecha.
            for (j in 0 until 3) {
                val dx = pw * (-0.5f + 0.5f * j)
                val pl = Path().apply {
                    moveTo(x + dx, 0f)
                    var yy = 0f; var q = 0
                    while (yy < piso && q < 40) { yy = minOf(yy + maxOf(46f * u, 1f), piso); lineTo(x + dx + pw * 0.22f * (azar(s, 900 + i * 60 + j * 20 + q) - 0.5f), yy); q++ }
                }
                drawPath(pl, if (j == 0) v(ROCA_LUZ, 0.32f) else v(ROCA_HONDA, 0.28f), style = Stroke((0.7f + 0.3f * j) * u))
            }
            drawPath(colada, v(ROCA_HONDA, 0.3f), style = Stroke(0.8f * u))
        }

        // En cada colada y en cada fila: la repisa con su lámpara, una mano en negativo, puntos de ocre y rayas de cuenta.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(11f * u, w - 11f * u)
            val y0 = 12f * u + r * fila
            if (oscuro) haloAnt(Offset(x, y0 + fila * 0.62f), 38f * u, fuego.copy(alpha = 0.2f), 6)
            val m = Offset(x, y0 + fila * 0.26f)
            haloAnt(m, 11f * u, v(OCRE, 0.6f).copy(alpha = 0.75f), 5)
            manoAnt(m, 6f * u, v(ROCA_OSC, 0.34f))
            for (j in 0 until 4) drawCircle(v(OCRE, 0.5f), 1.3f * u, Offset(x + (if (j % 2 == 0) -1.6f else 1.6f) * u, y0 + fila * 0.42f + j * 5.5f * u))
            val marcas = 3 + (r + i) % 3
            for (j in 0 until marcas) drawLine(v(CARBON, if (oscuro) 0.5f else 0.4f), Offset(x - 4f * u, y0 + fila * 0.52f + j * 2.6f * u), Offset(x + 4f * u, y0 + fila * 0.52f + j * 2.6f * u - 0.8f * u), 0.9f * u, StrokeCap.Round)
            // La repisa de roca y la lámpara de grasa: un canto ahuecado con su mecha.
            val yr = y0 + fila * 0.66f
            drawOval(v(ROCA_HONDA, 0.4f), Offset(x - 7f * u, yr), Size(14f * u, 4f * u))
            drawArc(v(ROCA_LUZ, 0.4f), 180f, 180f, true, Offset(x - 4.5f * u, yr - 2f * u), Size(9f * u, 5f * u))
            llamaAnt(Offset(x, yr - 0.6f * u), 6f * u, if (oscuro) fuego.copy(alpha = 0.85f) else v(fuego, 0.55f), if (oscuro) brasa else v(brasa, 0.6f))
        }

        // El techo, con estalactitas: largas sobre las coladas y cortas sobre los trofeos.
        val techo = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, 7f * u)
            var x = w; var j = 0
            while (x > 0f && j < 90) {
                val an = (7f + 9f * azar(s, 800 + j)) * u
                val junta = cercaJuntaAnt(x - an / 2, paso)
                val largo = if (junta > 0.8f) (14f + 22f * azar(s, 850 + j)) * u else (3f + 6f * azar(s, 870 + j)) * u
                lineTo(x - an * 0.4f, 7f * u + largo); lineTo(x - an, 7f * u + azar(s, 890 + j) * 2f * u)
                x -= maxOf(an, 1f); j++
            }
            lineTo(0f, 7f * u); close()
        }
        drawPath(techo, v(ROCA_HONDA, 0.42f))
        drawPath(techo, v(ROCA_LUZ, 0.2f), style = Stroke(0.7f * u))

        // El suelo: tierra apisonada de borde irregular, con cantos y, al pie de cada colada, conchas con pigmento.
        val suelo = Path().apply {
            moveTo(0f, h); lineTo(0f, piso)
            for (j in 1..16) lineTo(w * j / 16f, piso + (azar(s, 1600 + j) - 0.5f) * 3f * u)
            lineTo(w, h); close()
        }
        drawPath(suelo, v(ROCA_HONDA, 0.36f))
        for (j in 0 until 18) {
            val x = w * azar(s, 1700 + j); val yy = piso + (3f + 8f * azar(s, 1720 + j)) * u
            val kk = 0.22f + 0.2f * cercaJuntaAnt(x, paso)
            drawOval(if (j % 3 == 0) v(ROCA_LUZ, kk) else v(ROCA_OSC, kk), Offset(x, yy), Size((2.5f + 4f * azar(s, 1740 + j)) * u, (1.5f + 2f * azar(s, 1760 + j)) * u))
        }
        for (i in 0..cols) {
            val x = (i * paso).coerceIn(14f * u, w - 14f * u)
            val c = Offset(x + 9f * u * (if (i % 2 == 0) 1f else -1f), piso + 7f * u)
            if (oscuro) haloAnt(c, 30f * u, fuego.copy(alpha = 0.22f), 6)
            drawArc(v(CALIZA, 0.5f), 180f, 180f, true, Offset(c.x - 4.5f * u, c.y - 3f * u), Size(9f * u, 6f * u))
            drawOval(v(OCRE, 0.6f), Offset(c.x - 3f * u, c.y - 1.4f * u), Size(6f * u, 2.2f * u))
            // La lámpara del suelo, junto a la concha.
            val l = Offset(x - 8f * u * (if (i % 2 == 0) 1f else -1f), piso + 6f * u)
            drawArc(v(CARBON, if (oscuro) 0.6f else 0.3f), 0f, 180f, true, Offset(l.x - 6f * u, l.y - 3f * u), Size(12f * u, 7f * u))
            llamaAnt(Offset(l.x, l.y - 0.5f * u), 8f * u, if (oscuro) fuego.copy(alpha = 0.9f) else v(fuego, 0.6f), if (oscuro) brasa else v(brasa, 0.7f))
        }
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

    // ---------------------------------------------------------------- el salón del hall

    /** Una lucerna de barro de perfil sobre su ménsula de bronce, con la base en [b]; si [encendida], con la llama. */
    private fun DrawScope.lucernaRomano(b: Offset, u: Float, barro: Color, bronce: Color, llama: Color, corazon: Color) {
        drawLine(bronce, Offset(b.x - 6f * u, b.y + 1f * u), Offset(b.x + 6f * u, b.y + 1f * u), 1.4f * u, StrokeCap.Round)
        drawLine(bronce, Offset(b.x, b.y + 1f * u), Offset(b.x, b.y + 6f * u), 1.2f * u)
        drawLine(bronce, Offset(b.x, b.y + 6f * u), Offset(b.x - 3f * u, b.y + 8f * u), 1f * u, StrokeCap.Round)
        val cuerpo = Path().apply {
            moveTo(b.x - 5f * u, b.y); quadraticTo(b.x - 5.5f * u, b.y - 3.4f * u, b.x - 1f * u, b.y - 3.4f * u)
            lineTo(b.x + 3f * u, b.y - 2.8f * u); quadraticTo(b.x + 6.5f * u, b.y - 2.6f * u, b.x + 6.5f * u, b.y - 1.2f * u)
            quadraticTo(b.x + 6.4f * u, b.y, b.x + 3f * u, b.y); close()
        }
        drawPath(cuerpo, barro)
        drawLine(bronce, Offset(b.x - 5f * u, b.y - 2.6f * u), Offset(b.x - 7f * u, b.y - 4.6f * u), 1f * u, StrokeCap.Round)
        llamaAnt(Offset(b.x + 5.4f * u, b.y - 2.6f * u), 6.5f * u, llama, corazon)
    }

    /** Una corona de laurel de radio [r]: dos ramas de hojas que se cierran arriba, atadas abajo con la cinta. */
    private fun DrawScope.coronaRomano(c: Offset, r: Float, hoja: Color, cinta: Color) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 7) {
            val a = Math.toRadians((90.0 + lado * (20.0 + k * 22.0))).toFloat()
            val p = Offset(c.x + r * kotlin.math.cos(a), c.y + r * kotlin.math.sin(a))
            rotate(Math.toDegrees(a.toDouble()).toFloat() + lado * 60f, p) { drawOval(hoja, Offset(p.x - r * 0.28f, p.y - r * 0.11f), Size(r * 0.56f, r * 0.22f)) }
        }
        drawLine(cinta, Offset(c.x, c.y + r), Offset(c.x - r * 0.35f, c.y + r * 1.5f), r * 0.1f, StrokeCap.Round)
        drawLine(cinta, Offset(c.x, c.y + r), Offset(c.x + r * 0.3f, c.y + r * 1.55f), r * 0.1f, StrokeCap.Round)
    }

    /**
     * El atrio de una domus. Arriba, el borde del compluvio abierto al cielo, con las tejas y las antefijas de barro en forma
     * de palmeta, y el entablamento con sus dentículos y la greca. Entre las columnas de trofeos, pilastras de mármol
     * acanaladas con capitel corintio, y en cada una, por fila, una lucerna sobre su ménsula y, a filas alternas, una corona
     * de laurel colgada. Detrás de cada trofeo, la pared pintada al modo pompeyano: el panel rojo con su marco de ocre,
     * candelabros pintados a los lados, la guirnalda arriba y la franja negra abajo. Abajo, el piso de mosaico blanco y negro,
     * el zócalo de pórfido y el impluvio con su agua. De día el cielo es azul y el agua lo refleja; de noche, el cielo es hondo
     * con sus estrellas, la luna cae en el impluvio y las lucernas alumbran las pilastras. Dos degradados: el cielo y el agua.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 47
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.6f, 1f) else k)
        val barro = Color(0xFFB0643A); val ocre = Color(0xFFC9A04A)
        val llama = Color(0xFFFFB347); val corazon = Color(0xFFFFE6A8)
        val pw = minOf(8f * u, paso * 0.05f)
        val piso = h - 16f * u
        val cielo = 9f * u

        // La pared, enlucida, y los paneles pompeyanos detrás de cada trofeo, uno por fila.
        drawRect(if (oscuro) v(TRAVERTINO, 0.1f) else v(TRAVERTINO, 0.26f), Offset(0f, cielo), Size(w, piso - cielo))
        for (r in 0 until filas) for (i in 0 until cols) {
            val y0 = 12f * u + r * fila + 8f * u; val y1 = 12f * u + (r + 1) * fila - 22f * u
            val x0 = i * paso + pw * 2.4f; val x1 = (i + 1) * paso - pw * 2.4f
            if (x1 - x0 < 30f * u || y1 - y0 < 60f * u) continue
            // El panel rojo, con su marco de ocre y un filete de ocre adentro.
            drawRect(v(MINIO, if (oscuro) 0.2f else 0.13f), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawRect(v(ocre, 0.34f), Offset(x0, y0), Size(x1 - x0, y1 - y0), style = Stroke(1.6f * u))
            drawRect(v(ocre, 0.22f), Offset(x0 + 4f * u, y0 + 4f * u), Size(x1 - x0 - 8f * u, y1 - y0 - 8f * u), style = Stroke(0.7f * u))
            // Los candelabros pintados a los lados: un tallo fino con sus discos y su remate.
            for (x in listOf(x0 + 9f * u, x1 - 9f * u)) {
                drawLine(v(ocre, 0.32f), Offset(x, y0 + 10f * u), Offset(x, y1 - 8f * u), 0.9f * u)
                var yy = y0 + 22f * u; var j = 0
                while (yy < y1 - 14f * u && j < 12) { drawOval(v(ocre, 0.3f), Offset(x - 3f * u, yy), Size(6f * u, 1.8f * u)); yy += maxOf(26f * u, 1f); j++ }
                drawPath(Path().apply { moveTo(x - 3f * u, y0 + 12f * u); quadraticTo(x, y0 + 4f * u, x + 3f * u, y0 + 12f * u); close() }, v(ocre, 0.32f))
            }
            // La guirnalda: hojas de laurel que cuelgan entre los candelabros, con dos cintas.
            val g = Path().apply { moveTo(x0 + 9f * u, y0 + 12f * u); quadraticTo((x0 + x1) / 2, y0 + 34f * u, x1 - 9f * u, y0 + 12f * u) }
            drawPath(g, v(LAUREL, 0.3f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
            drawPath(g, v(LAUREL, 0.4f), style = Stroke(0.8f * u))
            for (x in listOf(x0 + 9f * u, x1 - 9f * u)) drawLine(v(MINIO, 0.4f), Offset(x, y0 + 12f * u), Offset(x + 1.5f * u, y0 + 22f * u), 0.9f * u, StrokeCap.Round)
            // La franja negra de abajo (la predela), con una línea de ocre.
            drawRect(v(NEGRO, 0.12f), Offset(x0, y1 + 3f * u), Size(x1 - x0, 12f * u))
            drawLine(v(ocre, 0.3f), Offset(x0, y1 + 5f * u), Offset(x1, y1 + 5f * u), 0.7f * u)
        }

        // El cielo del compluvio, arriba, con sus estrellas de noche.
        drawRect(Brush.verticalGradient(listOf(mezcla(e.fondo, if (oscuro) Color(0xFF1A2440) else Color(0xFF7FB2E0), 0.6f), mezcla(e.fondo, if (oscuro) Color(0xFF2A3658) else Color(0xFFBBD6EE), 0.5f)), 0f, cielo), Offset.Zero, Size(w, cielo))
        if (oscuro) for (j in 0 until 14) drawCircle(Color(0xFFF2E8C8).copy(alpha = 0.4f + 0.4f * azar(s, j)), (0.4f + 0.5f * azar(s, 20 + j)) * u, Offset(w * azar(s, 40 + j), cielo * 0.8f * azar(s, 60 + j)))

        // Las pilastras: el fuste acanalado, la basa y el capitel corintio.
        val capY = cielo + 12f * u
        for (i in 0..cols) {
            val x = i * paso
            drawRect(v(MARMOL, 0.5f), Offset(x - pw, capY), Size(2 * pw, piso - capY))
            for (j in -1..1) drawLine(v(MARMOL_OSC, 0.42f), Offset(x + j * pw * 0.5f, capY + 12f * u), Offset(x + j * pw * 0.5f, piso - 10f * u), 0.8f * u)
            drawRect(v(MARMOL_OSC, 0.4f), Offset(x + pw * 0.45f, capY), Size(pw * 0.55f, piso - capY))
            drawRect(v(TRAVERTINO_OSC, 0.5f), Offset(x - pw, capY), Size(2 * pw, piso - capY), style = Stroke(0.8f * u))
            // La basa: dos toros.
            drawRoundRect(v(MARMOL, 0.55f), Offset(x - pw * 1.35f, piso - 7f * u), Size(pw * 2.7f, 3.4f * u), CornerRadius(1.6f * u))
            drawRoundRect(v(MARMOL_OSC, 0.5f), Offset(x - pw * 1.5f, piso - 3.6f * u), Size(pw * 3f, 3.6f * u), CornerRadius(1.6f * u))
            // El capitel: el cáliz, dos coronas de hojas de acanto y las volutas bajo el ábaco.
            val cap = Path().apply { moveTo(x - pw, capY + 10f * u); lineTo(x - pw * 1.7f, capY); lineTo(x + pw * 1.7f, capY); lineTo(x + pw, capY + 10f * u); close() }
            drawPath(cap, v(MARMOL, 0.55f))
            for (fila2 in 0..1) for (j in -1..1) {
                val hx = x + j * pw * (0.85f - fila2 * 0.15f); val hy = capY + 10f * u - fila2 * 4f * u
                drawPath(Path().apply { moveTo(hx - pw * 0.4f, hy); quadraticTo(hx - pw * 0.45f, hy - 4.5f * u, hx, hy - 6f * u); quadraticTo(hx + pw * 0.45f, hy - 4.5f * u, hx + pw * 0.4f, hy); close() }, v(MARMOL_OSC, 0.5f))
            }
            for (l in listOf(-1f, 1f)) drawCircle(v(MARMOL_OSC, 0.6f), 1.5f * u, Offset(x + l * pw * 1.35f, capY + 1.8f * u), style = Stroke(0.8f * u))
            drawRect(v(MARMOL, 0.6f), Offset(x - pw * 1.9f, capY - 2f * u), Size(pw * 3.8f, 2f * u))
        }

        // En cada pilastra, por fila: la lucerna en su ménsula (con su luz de noche) y, a filas alternas, una corona de laurel.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = i * paso; val y0 = 12f * u + r * fila
            val b = Offset(x.coerceIn(8f * u, w - 8f * u), y0 + fila * 0.6f)
            if (oscuro) haloAnt(Offset(b.x + 4f * u, b.y - 5f * u), 40f * u, llama.copy(alpha = 0.2f), 7)
            lucernaRomano(b, u, v(barro, 0.6f), v(BRONCE, 0.6f), if (oscuro) llama.copy(alpha = 0.9f) else v(llama, 0.55f), if (oscuro) corazon else v(corazon, 0.65f))
            if ((r + i) % 2 == 0) coronaRomano(Offset(x.coerceIn(9f * u, w - 9f * u), y0 + fila * 0.28f), 6.5f * u, v(LAUREL, 0.55f), v(MINIO, 0.5f))
        }

        // El entablamento: el borde de las tejas, las antefijas, los dentículos y la greca.
        drawRect(v(barro, 0.45f), Offset(0f, cielo - 1.5f * u), Size(w, 2.5f * u))
        var x = 6f * u; var j = 0
        while (x < w && j < 60) {
            drawPath(Path().apply {
                moveTo(x - 2.6f * u, cielo); lineTo(x - 2.6f * u, cielo - 3f * u)
                quadraticTo(x - 4f * u, cielo - 7.5f * u, x, cielo - 8.5f * u); quadraticTo(x + 4f * u, cielo - 7.5f * u, x + 2.6f * u, cielo - 3f * u)
                lineTo(x + 2.6f * u, cielo); close()
            }, v(barro, 0.5f))
            drawLine(v(TRAVERTINO_LUZ, 0.5f), Offset(x, cielo - 1f * u), Offset(x, cielo - 6.5f * u), 0.6f * u)
            x += maxOf(16f * u, 1f); j++
        }
        drawRect(v(MARMOL, 0.46f), Offset(0f, cielo + 1f * u), Size(w, 10f * u))
        x = 1f * u; j = 0
        while (x < w && j < 160) { drawRect(v(MARMOL_OSC, 0.42f), Offset(x, cielo + 1.5f * u), Size(2.2f * u, 2.6f * u)); x += maxOf(4f * u, 1f); j++ }
        val gy = cielo + 5f * u; val ga = 4.5f * u
        val greca = Path().apply {
            moveTo(0f, gy + ga); var gx = 0f; var q = 0
            while (gx < w && q < 80) {
                lineTo(gx, gy); lineTo(gx + ga, gy); lineTo(gx + ga, gy + ga * 0.66f); lineTo(gx + ga * 0.4f, gy + ga * 0.66f)
                lineTo(gx + ga * 0.4f, gy + ga * 0.33f); moveTo(gx + ga * 1.25f, gy); lineTo(gx + ga * 1.25f, gy + ga); lineTo(gx + ga * 2f, gy + ga)
                moveTo(gx + ga * 2f, gy + ga); gx += maxOf(ga * 2f, 1f); q++
            }
        }
        drawPath(greca, v(MINIO, 0.36f), style = Stroke(0.8f * u))

        // El piso: el zócalo de pórfido, el mosaico blanco y negro y el impluvio al centro, con su agua.
        drawRect(v(PORFIDO, 0.22f), Offset(0f, piso - 3f * u), Size(w, 3f * u))
        drawRect(v(MARMOL, 0.4f), Offset(0f, piso), Size(w, h - piso))
        val t3 = 3.4f * u
        x = 0f; j = 0
        while (x < w && j < 200) {
            val kk = 0.22f + 0.22f * cercaJuntaAnt(x, paso)
            drawRect(v(NEGRO, kk), Offset(x, piso + (if (j % 2 == 0) 2f * u else 2f * u + t3)), Size(t3 - 0.6f * u, t3 - 0.6f * u))
            x += maxOf(t3, 1f); j++
        }
        drawLine(v(NEGRO, 0.3f), Offset(0f, piso + 2f * u + 2 * t3 + 1.2f * u), Offset(w, piso + 2f * u + 2 * t3 + 1.2f * u), 0.9f * u)
        val pila = Rect(w * 0.5f - minOf(w * 0.17f, 70f * u), piso + 3f * u, w * 0.5f + minOf(w * 0.17f, 70f * u), h - 2.5f * u)
        drawRect(v(MARMOL, 0.6f), Offset(pila.left - 2.5f * u, pila.top - 2f * u), Size(pila.width + 5f * u, pila.height + 4.5f * u))
        drawRect(Brush.verticalGradient(listOf(if (oscuro) Color(0xFF223050) else Color(0xFF6FA2D0), if (oscuro) Color(0xFF151C30) else Color(0xFF4A7EB0)), pila.top, pila.bottom), pila.topLeft, pila.size, alpha = if (oscuro) 0.8f else 0.42f)
        for (q in 0 until 4) {
            val qx = pila.left + pila.width * (0.15f + 0.22f * q)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.25f else 0.4f), Offset(qx, pila.top + pila.height * (0.35f + 0.15f * (q % 2))), Offset(qx + 9f * u, pila.top + pila.height * (0.35f + 0.15f * (q % 2))), 0.8f * u, StrokeCap.Round)
        }
        // La luna (o el sol) que cae en el agua.
        drawOval((if (oscuro) Color(0xFFF2E8C8) else Color.White).copy(alpha = if (oscuro) 0.55f else 0.5f), Offset(pila.center.x + pila.width * 0.18f - 5f * u, pila.top + 1.5f * u), Size(10f * u, 2.6f * u))
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

    // ---------------------------------------------------------------- el salón del hall

    /** El escudo redondo de bronce (el hoplon) con radio [r]: el borde, el cuenco y, al centro, el sol de dieciséis rayos. */
    private fun DrawScope.hoplonHeleno(c: Offset, r: Float, bronce: Color, borde: Color, sol: Color, u: Float) {
        drawCircle(borde, r, c)
        drawCircle(bronce, r * 0.84f, c)
        drawCircle(borde, r * 0.84f, c, style = Stroke(0.6f * u))
        drawPath(estrella(c, r * 0.62f, 16, 0.42f), sol)
        drawCircle(sol, r * 0.16f, c)
        drawLine(borde, Offset(c.x, c.y - r), Offset(c.x, c.y - r - 4f * u), 0.8f * u)
    }

    /** Un pínax, la tablita pintada que se colgaba de ofrenda: el fondo negro de barniz, el marco y un ánfora en rojo. */
    private fun DrawScope.pinaxHeleno(c: Offset, u: Float, barniz: Color, rojo: Color, marco: Color) {
        val a = 13f * u; val b = 10f * u
        drawLine(marco, Offset(c.x - a * 0.3f, c.y - b * 0.5f), Offset(c.x, c.y - b * 0.5f - 4f * u), 0.6f * u)
        drawLine(marco, Offset(c.x + a * 0.3f, c.y - b * 0.5f), Offset(c.x, c.y - b * 0.5f - 4f * u), 0.6f * u)
        drawRect(barniz, Offset(c.x - a / 2, c.y - b / 2), Size(a, b))
        drawRect(marco, Offset(c.x - a / 2, c.y - b / 2), Size(a, b), style = Stroke(0.7f * u))
        val anfora = Path().apply {
            moveTo(c.x - 1f * u, c.y - 3.6f * u); lineTo(c.x + 1f * u, c.y - 3.6f * u)
            quadraticTo(c.x + 3.4f * u, c.y - 1.4f * u, c.x + 2.2f * u, c.y + 1.6f * u); lineTo(c.x + 0.6f * u, c.y + 3.6f * u)
            lineTo(c.x - 0.6f * u, c.y + 3.6f * u); lineTo(c.x - 2.2f * u, c.y + 1.6f * u)
            quadraticTo(c.x - 3.4f * u, c.y - 1.4f * u, c.x - 1f * u, c.y - 3.6f * u); close()
        }
        drawPath(anfora, rojo)
    }

    /**
     * La stoa abierta al mar. Entre las columnas de trofeos, columnas jónicas de mármol (el fuste acanalado, la basa y el capitel
     * con sus volutas y los ovas), y en cada una, por fila, un escudo de bronce con el sol de Vergina o un pínax pintado colgado
     * de ofrenda. Arriba, el entablamento: la cornisa, los dentículos, el friso con la greca azul y las tres fajas del
     * arquitrabe. Detrás de los trofeos, el cielo y, abajo, el Egeo: las islas en el horizonte, una vela, las olas. Abajo del
     * todo, el estilóbato de tres gradas. De día el sol pálido, nubes largas y el mar azul; de noche, las estrellas, la luna con
     * su reguero sobre el agua, el lado de las columnas que ve la luna con luz fría y, al pie, los trípodes con su fuego.
     * Dos degradados: el cielo y el mar.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 59
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.6f, 1f) else k)
        val bronce = Color(0xFFB07A3A); val luna = Color(0xFFF2EAD0)
        val piso = h - 15f * u
        val hz = piso - minOf(64f * u, h * 0.12f)
        val techo = 20f * u
        val pw = minOf(7.5f * u, paso * 0.048f)

        // El cielo y el mar.
        drawRect(Brush.verticalGradient(listOf(
            if (oscuro) mezcla(e.fondo, Color(0xFF1A2050), 0.32f) else mezcla(e.fondo, EGEO, 0.1f),
            if (oscuro) mezcla(e.fondo, Color(0xFF3A3A6A), 0.36f) else mezcla(e.fondo, Color.White, 0.3f)), techo, hz), Offset(0f, techo), Size(w, hz - techo))
        drawRect(Brush.verticalGradient(listOf(v(EGEO, 0.3f), v(EGEO, 0.16f)), hz, piso), Offset(0f, hz), Size(w, piso - hz))
        // Las estrellas o las nubes.
        if (oscuro) {
            val estrellas = (w * hz / (1800f * u * u)).toInt().coerceIn(20, 80)
            for (j in 0 until estrellas) drawCircle(luna.copy(alpha = 0.18f + 0.4f * azar(s, j) * (0.3f + cercaJuntaAnt(w * azar(s, 100 + j), paso))),
                (0.4f + 0.7f * azar(s, 200 + j)) * u, Offset(w * azar(s, 100 + j), techo + (hz - techo) * azar(s, 300 + j)))
        } else for (j in 0 until (filas * 3).coerceAtMost(18)) {
            val c = Offset(w * azar(s, 400 + j), techo + (hz - techo - 20f * u) * azar(s, 420 + j))
            drawOval(Color.White.copy(alpha = 0.6f), Offset(c.x - 30f * u, c.y), Size(60f * u * (0.6f + azar(s, 440 + j)), 3.5f * u))
            drawOval(Color.White.copy(alpha = 0.4f), Offset(c.x - 18f * u, c.y - 2.5f * u), Size(30f * u * (0.6f + azar(s, 450 + j)), 3f * u))
        }
        // El sol o la luna, bajos, entre la última columna de trofeos y el borde, con su reguero en el agua.
        val astro = Offset(w - paso * 0.12f, hz - 24f * u)
        haloAnt(astro, 26f * u, (if (oscuro) luna else Color.White).copy(alpha = if (oscuro) 0.22f else 0.5f), 6)
        drawCircle(if (oscuro) luna.copy(alpha = 0.85f) else mezcla(e.fondo, Color(0xFFFFF4D6), 0.9f), 7f * u, astro)
        if (oscuro) for (j in 0 until 3) drawCircle(Color(0xFFB8B0A0).copy(alpha = 0.5f), (1.2f + 0.8f * j) * u * 0.6f, Offset(astro.x + (j - 1) * 2.6f * u, astro.y + (j % 2 - 0.5f) * 3f * u))
        // Unas gaviotas sobre el mar, junto a las columnas.
        for (j in 0 until 2 * cols) {
            val c = Offset((j / 2 + (if (j % 2 == 0) 0.12f else 0.88f)) * paso, hz - (40f + 70f * azar(s, 650 + j)) * u)
            val an = (3f + 2f * azar(s, 660 + j)) * u
            drawPath(Path().apply { moveTo(c.x - an, c.y - an * 0.3f); quadraticTo(c.x - an * 0.5f, c.y - an * 0.6f, c.x, c.y); quadraticTo(c.x + an * 0.5f, c.y - an * 0.6f, c.x + an, c.y - an * 0.3f) },
                v(BARNIZ, if (oscuro) 0.25f else 0.3f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        for (j in 0 until 9) {
            val yy = hz + 3f * u + j * (piso - hz - 6f * u) / 9f; val an = (5f + j * 1.6f) * u * (0.6f + 0.6f * azar(s, 500 + j))
            drawLine((if (oscuro) luna else Color.White).copy(alpha = if (oscuro) 0.4f else 0.5f), Offset(astro.x - an, yy), Offset(astro.x + an * 0.6f, yy), 0.9f * u, StrokeCap.Round)
        }
        // Las islas en el horizonte y una vela.
        for (j in 0 until 4) {
            val cx = w * (0.12f + 0.26f * j + 0.08f * azar(s, 600 + j)); val an = (30f + 40f * azar(s, 610 + j)) * u; val al = (5f + 8f * azar(s, 620 + j)) * u
            drawPath(Path().apply { moveTo(cx - an / 2, hz); quadraticTo(cx - an * 0.2f, hz - al * 1.3f, cx + an * 0.05f, hz - al); quadraticTo(cx + an * 0.3f, hz - al * 0.6f, cx + an / 2, hz); close() },
                if (oscuro) mezcla(e.fondo, Color(0xFF2A2A4A), 0.7f) else mezcla(e.fondo, Color(0xFF7E8E9A), 0.25f))
        }
        val vela = Offset(paso * 0.5f - 30f * u, hz + 9f * u)
        drawPath(Path().apply { moveTo(vela.x - 7f * u, vela.y); lineTo(vela.x + 7f * u, vela.y); lineTo(vela.x + 5f * u, vela.y + 2.4f * u); lineTo(vela.x - 5.5f * u, vela.y + 2.4f * u); close() }, v(BARNIZ, 0.4f))
        drawLine(v(BARNIZ, 0.4f), Offset(vela.x, vela.y), Offset(vela.x, vela.y - 10f * u), 0.7f * u)
        drawRect(v(MARMOL_LUZ, 0.6f), Offset(vela.x - 4f * u, vela.y - 9f * u), Size(8f * u, 7f * u))
        // Las olas: arcos cortos, más donde no hay trofeos.
        for (j in 0 until 36) {
            val c = Offset(w * azar(s, 700 + j), hz + 4f * u + (piso - hz - 8f * u) * azar(s, 720 + j))
            val kk = 0.25f + 0.35f * cercaJuntaAnt(c.x, paso)
            drawArc((if (oscuro) luna else Color.White).copy(alpha = kk * (if (oscuro) 0.5f else 0.9f)), 200f, 140f, false, Offset(c.x - 4f * u, c.y - 1.5f * u), Size(8f * u, 3f * u), style = Stroke(0.7f * u))
        }

        // Las columnas jónicas.
        val capY = techo + 8f * u
        for (i in 0..cols) {
            val x = i * paso
            val fuste = Path().apply {
                moveTo(x - pw, piso - 6f * u); quadraticTo(x - pw * 1.08f, (capY + piso) / 2, x - pw * 0.86f, capY)
                lineTo(x + pw * 0.86f, capY); quadraticTo(x + pw * 1.08f, (capY + piso) / 2, x + pw, piso - 6f * u); close()
            }
            drawPath(fuste, v(MARMOL, 0.55f))
            drawPath(fuste, v(MARMOL_SOMBRA, 0.4f), style = Stroke(0.8f * u))
            for (j in -2..2) drawLine(v(MARMOL_SOMBRA, 0.32f), Offset(x + j * pw * 0.36f, capY + 4f * u), Offset(x + j * pw * 0.4f, piso - 8f * u), 0.7f * u)
            // La luz de la luna en el lado que la ve; de día, la sombra del otro lado.
            if (oscuro) drawRect(luna.copy(alpha = 0.1f), Offset(x + pw * 0.2f, capY), Size(pw * 0.75f, piso - 6f * u - capY))
            else drawRect(MARMOL_SOMBRA.copy(alpha = 0.16f), Offset(x - pw, capY), Size(pw * 0.7f, piso - 6f * u - capY))
            // La basa y el plinto.
            drawRoundRect(v(MARMOL, 0.6f), Offset(x - pw * 1.3f, piso - 6.5f * u), Size(pw * 2.6f, 3f * u), CornerRadius(1.5f * u))
            drawRect(v(MARMOL_OSC, 0.55f), Offset(x - pw * 1.45f, piso - 3.5f * u), Size(pw * 2.9f, 3.5f * u))
            // El capitel: el equino con los ovas, las volutas y el ábaco.
            drawRect(v(MARMOL, 0.6f), Offset(x - pw * 1.2f, capY - 4f * u), Size(pw * 2.4f, 4f * u))
            for (j in -1..1) drawOval(v(MARMOL_SOMBRA, 0.45f), Offset(x + j * pw * 0.6f - 1.2f * u, capY - 3.4f * u), Size(2.4f * u, 2.8f * u))
            drawRect(v(MARMOL, 0.62f), Offset(x - pw * 2f, capY - 7f * u), Size(pw * 4f, 3.2f * u))
            for (l in listOf(-1f, 1f)) {
                val c = Offset(x + l * pw * 1.6f, capY - 3f * u)
                drawCircle(v(MARMOL, 0.62f), 3.2f * u, c)
                drawArc(v(MARMOL_SOMBRA, 0.55f), 0f, 300f, false, Offset(c.x - 2.4f * u, c.y - 2.4f * u), Size(4.8f * u, 4.8f * u), style = Stroke(0.7f * u))
                drawCircle(v(MARMOL_SOMBRA, 0.6f), 0.8f * u, c)
            }
            drawRect(v(MARMOL_LUZ, 0.62f), Offset(x - pw * 1.9f, capY - 8.6f * u), Size(pw * 3.8f, 1.8f * u))
        }

        // En cada columna, por fila: el escudo de bronce o el pínax colgado.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(10f * u, w - 10f * u); val y0 = 12f * u + r * fila
            if ((r + i) % 2 == 0) hoplonHeleno(Offset(x, y0 + fila * 0.34f), 8.5f * u, v(bronce, 0.55f), v(BARNIZ, 0.35f), v(ORO, 0.7f), u)
            else pinaxHeleno(Offset(x, y0 + fila * 0.36f), u, v(BARNIZ, 0.6f), v(ROJO_VASO, 0.7f), v(ORO, 0.5f))
        }

        // El entablamento.
        drawRect(v(MARMOL, 0.5f), Offset(0f, 0f), Size(w, techo))
        drawRect(v(MARMOL_LUZ, 0.55f), Offset(0f, 0f), Size(w, 3.4f * u))
        var x = 1f * u; var j = 0
        while (x < w && j < 160) { drawRect(v(MARMOL_SOMBRA, 0.45f), Offset(x, 4f * u), Size(2.2f * u, 2.6f * u)); x += maxOf(4f * u, 1f); j++ }
        drawRect(v(EGEO, 0.2f), Offset(0f, 7.5f * u), Size(w, 6f * u))
        val ga = 5f * u; val gy = 8f * u
        val greca = Path().apply {
            var gx = 0f; var q = 0
            moveTo(0f, gy + ga)
            while (gx < w && q < 80) {
                lineTo(gx, gy); lineTo(gx + ga, gy); lineTo(gx + ga, gy + ga * 0.7f); lineTo(gx + ga * 0.4f, gy + ga * 0.7f); lineTo(gx + ga * 0.4f, gy + ga * 0.35f)
                moveTo(gx + ga * 1.3f, gy); lineTo(gx + ga * 1.3f, gy + ga); lineTo(gx + ga * 2f, gy + ga)
                gx += maxOf(ga * 2f, 1f); q++
            }
        }
        drawPath(greca, v(EGEO, 0.55f), style = Stroke(0.8f * u))
        for (k in 0 until 3) drawLine(v(MARMOL_SOMBRA, 0.4f), Offset(0f, 14.5f * u + k * 1.9f * u), Offset(w, 14.5f * u + k * 1.9f * u), 0.6f * u)
        drawLine(v(MARMOL_SOMBRA, 0.5f), Offset(0f, techo), Offset(w, techo), 0.9f * u)

        // El estilóbato: tres gradas con sus juntas.
        for (k in 0 until 3) {
            val y0 = piso + k * 5f * u
            drawRect(v(if (k == 0) MARMOL_LUZ else MARMOL, 0.5f - k * 0.04f), Offset(0f, y0), Size(w, 5f * u))
            drawLine(v(MARMOL_SOMBRA, 0.5f), Offset(0f, y0 + 5f * u), Offset(w, y0 + 5f * u), 0.7f * u)
            x = (k * 13f + 6f) * u; j = 0
            while (x < w && j < 40) { drawLine(v(MARMOL_SOMBRA, 0.35f), Offset(x, y0), Offset(x, y0 + 5f * u), 0.6f * u); x += maxOf(38f * u, 1f); j++ }
        }
        // De noche, los trípodes de bronce con su fuego, al pie de las columnas.
        if (oscuro) for (i in 0..cols) {
            val x = (i * paso + (if (i == cols) -16f else 16f) * u).coerceIn(10f * u, w - 10f * u); val b = Offset(x, piso + 1f * u)
            haloAnt(Offset(b.x, b.y - 12f * u), 44f * u, LLAMA.copy(alpha = 0.22f), 7)
            for (l in listOf(-1f, 0f, 1f)) drawLine(v(bronce, 0.6f), Offset(b.x + l * 4.5f * u, b.y), Offset(b.x + l * 2f * u, b.y - 8f * u), 0.9f * u, StrokeCap.Round)
            drawArc(v(bronce, 0.7f), 0f, 180f, true, Offset(b.x - 5f * u, b.y - 11f * u), Size(10f * u, 6f * u))
            llamaAnt(Offset(b.x, b.y - 8.6f * u), 10f * u, LLAMA.copy(alpha = 0.85f), Color(0xFFFFF1C8))
        }
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

    // ---------------------------------------------------------------- el salón del hall

    /** Un cartucho vertical de [alto] con el centro en [c]: la cuerda, el nudo abajo y tres signos (el sol, el agua, el junco). */
    private fun DrawScope.cartuchoEgipto(c: Offset, alto: Float, u: Float, cuerda: Color, fondo: Color, signo: Color, sol: Color) {
        val an = alto * 0.42f
        drawRoundRect(fondo, Offset(c.x - an / 2, c.y - alto / 2), Size(an, alto), CornerRadius(an / 2))
        drawRoundRect(cuerda, Offset(c.x - an / 2, c.y - alto / 2), Size(an, alto), CornerRadius(an / 2), style = Stroke(1f * u))
        drawLine(cuerda, Offset(c.x - an * 0.55f, c.y + alto / 2 + 1.2f * u), Offset(c.x + an * 0.55f, c.y + alto / 2 + 1.2f * u), 1.2f * u)
        drawCircle(sol, an * 0.2f, Offset(c.x, c.y - alto * 0.26f))
        val ag = Path().apply {
            val y = c.y; moveTo(c.x - an * 0.3f, y)
            for (k in 1..6) lineTo(c.x - an * 0.3f + an * 0.1f * k, y + (if (k % 2 == 0) 0f else -1.2f * u))
        }
        drawPath(ag, signo, style = Stroke(0.7f * u))
        drawLine(signo, Offset(c.x, c.y + alto * 0.12f), Offset(c.x, c.y + alto * 0.36f), 0.8f * u)
        drawOval(signo, Offset(c.x - 1.3f * u, c.y + alto * 0.1f), Size(2.6f * u, 3f * u))
    }

    /**
     * La sala hipóstila. Arriba, el techo azul de lapislázuli con sus estrellas de oro y el arquitrabe con el friso jeker
     * (los nudos de junco, en rojo y azul). Entre las columnas de trofeos, columnas papiriformes: el fuste de tallos atados,
     * la base redonda, las cinco ataduras y el capitel abierto con sus sépalos pintados; en cada una, por fila, un cartucho
     * con tres signos y, de noche, una antorcha. Detrás de los trofeos, los muros de arenisca en sillares, con los registros
     * pintados (bandas de rojo, amarillo y azul) entre fila y fila. Abajo, el piso de losas y, al pie de las columnas, jarros
     * con lotos. De día entran por las celosías altas haces de luz en diagonal; de noche, solo las antorchas, y las estrellas
     * del techo brillan. Dos degradados: el techo y la sombra del piso.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 71
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.6f, 1f) else k)
        val llama = Color(0xFFFFB347); val corazon = Color(0xFFFFE6A8)
        val techo = 12f * u; val arq = 21f * u
        val piso = h - 14f * u
        val cw = minOf(9f * u, paso * 0.06f)

        // Los muros de arenisca: los sillares, más tenues detrás de los trofeos.
        drawRect(v(ARENISCA, 0.16f), Offset(0f, arq), Size(w, piso - arq))
        val sh = 22f * u; val sw = 46f * u
        var y = arq; var k = 0
        while (y < piso && k < 70) {
            drawLine(v(ARENISCA_OSC, 0.18f), Offset(0f, y), Offset(w, y), 0.6f * u)
            var x = if (k % 2 == 0) 0f else sw / 2; var j = 0
            while (x < w && j < 40) {
                val kk = 0.1f + 0.18f * cercaJuntaAnt(x, paso)
                drawLine(v(ARENISCA_OSC, kk), Offset(x, y), Offset(x, minOf(y + sh, piso)), 0.6f * u)
                if (azar(s, k * 40 + j) > 0.8f) drawRect(v(ARENISCA_LUZ, 0.2f), Offset(x + 2f * u, y + 2f * u), Size(sw * 0.5f, sh * 0.3f))
                x += maxOf(sw, 1f); j++
            }
            y += maxOf(sh, 1f); k++
        }
        // Los registros pintados entre fila y fila: tres bandas finas.
        for (r in 1 until filas) {
            val yr = 12f * u + r * fila - 7f * u
            drawRect(v(ROJO, 0.22f), Offset(0f, yr), Size(w, 1.6f * u))
            drawRect(v(AMARILLO, 0.26f), Offset(0f, yr + 2.4f * u), Size(w, 1.6f * u))
            drawRect(v(LAPIS, 0.22f), Offset(0f, yr + 4.8f * u), Size(w, 1.6f * u))
        }
        // Los haces de luz de las celosías altas (de día).
        if (!oscuro) for (i in 0 until cols) {
            val x0 = i * paso + paso * 0.2f; val an = paso * 0.16f; val caida = minOf(h * 0.5f, 360f * u)
            drawPath(Path().apply { moveTo(x0, arq); lineTo(x0 + an, arq); lineTo(x0 + an + caida * 0.45f, arq + caida); lineTo(x0 + caida * 0.45f, arq + caida); close() },
                Color.White.copy(alpha = 0.16f))
        }

        // Las columnas papiriformes.
        val capTop = arq; val capBot = arq + 16f * u
        for (i in 0..cols) {
            val x = i * paso
            val fuste = Path().apply {
                moveTo(x - cw * 0.7f, piso - 4f * u); quadraticTo(x - cw * 1.25f, piso - 22f * u, x - cw, piso - 40f * u)
                lineTo(x - cw * 0.85f, capBot); lineTo(x + cw * 0.85f, capBot); lineTo(x + cw, piso - 40f * u)
                quadraticTo(x + cw * 1.25f, piso - 22f * u, x + cw * 0.7f, piso - 4f * u); close()
            }
            drawPath(fuste, v(ARENISCA_LUZ, 0.5f))
            drawPath(fuste, v(ARENISCA_OSC, 0.4f), style = Stroke(0.8f * u))
            for (j in -1..1) drawLine(v(ARENISCA_OSC, 0.3f), Offset(x + j * cw * 0.5f, capBot + 8f * u), Offset(x + j * cw * 0.55f, piso - 8f * u), 0.7f * u)
            drawRect(if (oscuro) Color.Black.copy(alpha = 0.2f) else v(ARENISCA_OSC, 0.18f), Offset(x + cw * 0.3f, capBot), Size(cw * 0.6f, piso - capBot - 4f * u))
            // La base redonda.
            drawOval(v(ARENISCA, 0.55f), Offset(x - cw * 1.4f, piso - 5f * u), Size(cw * 2.8f, 6f * u))
            // Las ataduras, en colores.
            for (j in 0 until 5) drawRect(v(if (j % 2 == 0) LAPIS else ROJO, 0.4f), Offset(x - cw * 0.86f, capBot + j * 1.6f * u), Size(cw * 1.72f, 0.9f * u))
            // El capitel abierto con sus sépalos pintados.
            val cap = Path().apply {
                moveTo(x - cw * 0.85f, capBot); quadraticTo(x - cw * 1.7f, capTop + 7f * u, x - cw * 2.1f, capTop)
                lineTo(x + cw * 2.1f, capTop); quadraticTo(x + cw * 1.7f, capTop + 7f * u, x + cw * 0.85f, capBot); close()
            }
            drawPath(cap, v(ARENISCA_LUZ, 0.55f))
            for (j in -2..2) {
                val bx = x + j * cw * 0.7f
                drawPath(Path().apply { moveTo(bx, capBot - 1f * u); lineTo(bx - cw * 0.35f, capTop + 2f * u); lineTo(bx + cw * 0.35f, capTop + 2f * u); close() },
                    v(if (j % 2 == 0) VERDE else LAPIS, 0.32f))
            }
            drawPath(cap, v(ARENISCA_OSC, 0.4f), style = Stroke(0.8f * u))
        }

        // En cada columna, por fila: el cartucho y, de noche, la antorcha con su luz.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(6f * u, w - 6f * u); val y0 = 12f * u + r * fila
            cartuchoEgipto(Offset(x, y0 + fila * 0.32f), 22f * u, u, v(AMARILLO, 0.55f), v(YESO, 0.5f), v(NEGRO, 0.5f), v(ROJO, 0.55f))
            val tb = Offset(x, y0 + fila * 0.62f)
            if (oscuro) haloAnt(Offset(tb.x, tb.y - 8f * u), 42f * u, llama.copy(alpha = 0.22f), 7)
            drawLine(v(MADERA, 0.6f), Offset(tb.x, tb.y + 9f * u), Offset(tb.x, tb.y - 2f * u), 2f * u, StrokeCap.Round)
            drawLine(v(NEGRO, 0.5f), Offset(tb.x - 3f * u, tb.y + 3f * u), Offset(tb.x + 3f * u, tb.y + 3f * u), 1f * u)
            drawPath(Path().apply { moveTo(tb.x - 2.5f * u, tb.y - 2f * u); lineTo(tb.x + 2.5f * u, tb.y - 2f * u); lineTo(tb.x + 1.6f * u, tb.y + 1f * u); lineTo(tb.x - 1.6f * u, tb.y + 1f * u); close() }, v(LINO, 0.55f))
            if (oscuro) llamaAnt(Offset(tb.x, tb.y - 2f * u), 9f * u, llama.copy(alpha = 0.9f), corazon)
        }

        // El techo de lapislázuli con sus estrellas.
        drawRect(Brush.verticalGradient(listOf(v(LAPIS, if (oscuro) 0.5f else 0.42f), v(LAPIS, if (oscuro) 0.36f else 0.3f)), 0f, techo), Offset.Zero, Size(w, techo))
        val estrellas = (w / (13f * u)).toInt().coerceIn(6, 60)
        for (j in 0 until estrellas) {
            val c = Offset(w * (j + 0.2f + 0.6f * azar(s, j)) / estrellas, techo * (0.25f + 0.5f * azar(s, 100 + j)))
            drawPath(estrella(c, 2.4f * u, 5, 0.4f), if (oscuro) AMARILLO.copy(alpha = 0.55f + 0.35f * azar(s, 200 + j)) else v(AMARILLO, 0.7f))
        }
        // El arquitrabe con el friso jeker.
        drawRect(v(ARENISCA_LUZ, 0.5f), Offset(0f, techo), Size(w, arq - techo))
        var x = 2f * u; var j = 0
        while (x < w && j < 120) {
            val c = v(if (j % 2 == 0) ROJO else LAPIS, 0.45f)
            drawRect(c, Offset(x - 0.9f * u, techo + 2.5f * u), Size(1.8f * u, 4.5f * u))
            drawCircle(c, 1.4f * u, Offset(x, techo + 2.2f * u))
            x += maxOf(5f * u, 1f); j++
        }
        drawLine(v(AMARILLO, 0.5f), Offset(0f, arq - 1f * u), Offset(w, arq - 1f * u), 0.8f * u)

        // El piso de losas, más hondo hacia el frente, y los jarros con lotos al pie de las columnas.
        drawRect(Brush.verticalGradient(listOf(v(ARENISCA, 0.34f), v(ARENISCA_OSC, 0.42f)), piso, h), Offset(0f, piso), Size(w, h - piso))
        drawLine(v(ROJO, 0.4f), Offset(0f, piso + 1.5f * u), Offset(w, piso + 1.5f * u), 0.9f * u)
        x = 14f * u; j = 0
        while (x < w && j < 40) { drawLine(v(ARENISCA_OSC, 0.4f), Offset(x, piso + 3f * u), Offset(x - 4f * u, h), 0.6f * u); x += maxOf(32f * u, 1f); j++ }
        for (i in 0..cols) {
            val bx = (i * paso + (if (i == cols) -1f else 1f) * (cw * 1.5f + 7f * u)).coerceIn(8f * u, w - 8f * u); val by = piso + 9f * u
            drawPath(Path().apply { moveTo(bx - 2.5f * u, by - 9f * u); lineTo(bx + 2.5f * u, by - 9f * u); quadraticTo(bx + 6f * u, by - 4f * u, bx + 2.4f * u, by); lineTo(bx - 2.4f * u, by); quadraticTo(bx - 6f * u, by - 4f * u, bx - 2.5f * u, by - 9f * u); close() }, v(ROJO, 0.45f))
            for (l in listOf(-1f, 0f, 1f)) {
                val tip = Offset(bx + l * 4f * u, by - 18f * u + kotlin.math.abs(l) * 3f * u)
                drawLine(v(VERDE, 0.5f), Offset(bx, by - 9f * u), tip, 0.7f * u)
                drawPath(Path().apply { moveTo(tip.x - 2f * u, tip.y); lineTo(tip.x, tip.y - 4f * u); lineTo(tip.x + 2f * u, tip.y); close() }, v(LAPIS, 0.5f))
            }
        }
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

    // ---------------------------------------------------------------- el salón del hall

    /** El número [num] (1 a 19) en puntos y barras, centrado en [c]: las barras abajo, los puntos encima. */
    private fun DrawScope.numeroMeso(c: Offset, num: Int, u: Float, color: Color) {
        val barras = num / 5; val puntos = num % 5
        for (b in 0 until barras) drawRoundRect(color, Offset(c.x - 5f * u, c.y + b * 3f * u), Size(10f * u, 1.9f * u), CornerRadius(0.9f * u))
        for (p in 0 until puntos) drawCircle(color, 1.1f * u, Offset(c.x + (p - (puntos - 1) / 2f) * 3f * u, c.y - 2.6f * u))
    }

    /** Un bloque glífico inventado de lado [l]: el cartucho redondeado y adentro un ojo, un círculo y dos rayas, según [semilla]. */
    private fun DrawScope.glifoMeso(o: Offset, l: Float, u: Float, borde: Color, trazo: Color, semilla: Int) {
        drawRoundRect(borde, o, Size(l, l), CornerRadius(l * 0.3f), style = Stroke(0.8f * u))
        val c = Offset(o.x + l / 2, o.y + l / 2)
        when ((azar(semilla, 1) * 3).toInt()) {
            0 -> { drawOval(trazo, Offset(c.x - l * 0.28f, c.y - l * 0.14f), Size(l * 0.56f, l * 0.28f), style = Stroke(0.7f * u)); drawCircle(trazo, l * 0.07f, c) }
            1 -> { drawCircle(trazo, l * 0.22f, c, style = Stroke(0.7f * u)); drawLine(trazo, Offset(c.x - l * 0.3f, c.y + l * 0.3f), Offset(c.x + l * 0.3f, c.y + l * 0.3f), 0.7f * u) }
            else -> for (k in 0 until 3) drawLine(trazo, Offset(o.x + l * 0.22f, o.y + l * (0.3f + k * 0.2f)), Offset(o.x + l * 0.78f, o.y + l * (0.3f + k * 0.2f)), 0.7f * u)
        }
    }

    /**
     * La cámara de un palacio maya. Cada columna de trofeos queda bajo su bóveda en saledizo (el falso arco: las jambas rectas y
     * los lados en gradas que se cierran arriba con una losa); detrás del trofeo, el mural pintado sobre estuco con su fondo
     * azul maya entre dos franjas de cinabrio. Entre las bóvedas, estelas de caliza talladas con bloques glíficos y, por fila,
     * el número de la fila en puntos y barras. Arriba, el friso de cinabrio con la greca escalonada; abajo, las gradas de la
     * plaza, cuentas de jade y los incensarios con el humo del copal. De día el estuco luce claro; de noche alumbran los
     * incensarios y las teas de las estelas. Dos degradados: el estuco del muro y la luz del suelo.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 83
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.6f, 1f) else k)
        val llama = Color(0xFFFFA040); val corazon = Color(0xFFFFE2A0)
        val friso = 13f * u
        val piso = h - 15f * u
        val ew = minOf(10f * u, paso * 0.065f)

        // El muro de estuco, más cálido abajo.
        drawRect(Brush.verticalGradient(listOf(v(ESTUCO, if (oscuro) 0.08f else 0.2f), v(CALIZA_OSC, if (oscuro) 0.1f else 0.18f)), friso, piso), Offset(0f, friso), Size(w, piso - friso))
        // Los murales: el fondo azul maya entre franjas de cinabrio, uno por fila detrás de cada trofeo, con figuras tenues.
        for (r in 0 until filas) for (i in 0 until cols) {
            val x0 = i * paso + ew * 2.2f; val x1 = (i + 1) * paso - ew * 2.2f
            val y0 = 12f * u + r * fila + 22f * u; val y1 = 12f * u + (r + 1) * fila - 30f * u
            if (x1 - x0 < 30f * u || y1 - y0 < 50f * u) continue
            drawRect(v(AZUL_MAYA, 0.16f), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawRect(v(CINABRIO, 0.3f), Offset(x0, y0 - 4f * u), Size(x1 - x0, 4f * u))
            drawRect(v(CINABRIO, 0.3f), Offset(x0, y1), Size(x1 - x0, 4f * u))
            // Dos figuras de perfil con su penacho, a los lados del trofeo.
            for (l in listOf(-1f, 1f)) {
                val fx = (x0 + x1) / 2 + l * (x1 - x0) * 0.4f; val fy = y1 - 6f * u
                val c = v(CINABRIO, 0.22f)
                drawRoundRect(c, Offset(fx - 4f * u, fy - 28f * u), Size(8f * u, 22f * u), CornerRadius(3f * u))
                drawCircle(c, 4f * u, Offset(fx, fy - 33f * u))
                for (p in 0 until 3) drawLine(v(JADE, 0.3f), Offset(fx, fy - 36f * u), Offset(fx - l * (4f + 3f * p) * u, fy - (44f - p * 3f) * u), 1.2f * u, StrokeCap.Round)
                drawLine(c, Offset(fx - 2f * u, fy - 6f * u), Offset(fx - 2f * u, fy), 1.5f * u); drawLine(c, Offset(fx + 2f * u, fy - 6f * u), Offset(fx + 2f * u, fy), 1.5f * u)
            }
        }

        // Las bóvedas en saledizo: jambas y lados escalonados que se cierran arriba.
        for (i in 0 until cols) {
            val x0 = i * paso + ew * 1.2f; val x1 = (i + 1) * paso - ew * 1.2f
            val g = minOf((x1 - x0) * 0.05f, 6f * u); val top = friso + 2f * u
            val boveda = Path().apply {
                moveTo(x0, piso); lineTo(x0, top + 5 * g)
                for (k in 0 until 4) { lineTo(x0 + (k + 1) * g, top + (5 - k) * g); lineTo(x0 + (k + 1) * g, top + (4 - k) * g) }
                lineTo(x1 - 4 * g, top + g)
                for (k in 3 downTo 0) { lineTo(x1 - k * g, top + (4 - k) * g); lineTo(x1 - k * g, top + (5 - k) * g) }
                lineTo(x1, piso)
            }
            drawPath(boveda, v(CALIZA_OSC, 0.34f), style = Stroke(3f * u))
            drawPath(boveda, v(ESTUCO, 0.5f), style = Stroke(1f * u))
            // La losa de cierre y la viga de chicozapote que cruza la bóveda.
            drawRect(v(CALIZA_OSC, 0.4f), Offset(x0 + 4 * g, top - 1f * u), Size(x1 - x0 - 8 * g, 3f * u))
            drawRect(v(NEGRO, 0.28f), Offset(x0 + 2 * g, top + 3.6f * g), Size(x1 - x0 - 4 * g, 1.6f * u))
        }

        // Las estelas entre las bóvedas, talladas con bloques glíficos.
        for (i in 0..cols) {
            val x = i * paso
            drawRoundRect(v(CALIZA_OSC, 0.42f), Offset(x - ew, friso), Size(2 * ew, piso - friso), CornerRadius(ew * 0.4f))
            drawRect(if (oscuro) Color.Black.copy(alpha = 0.18f) else v(CALIZA_OSC, 0.2f), Offset(x + ew * 0.35f, friso), Size(ew * 0.65f, piso - friso))
            val l = ew * 1.2f
            var y = friso + 8f * u; var j = 0
            while (y < piso - l - 6f * u && j < 60) {
                glifoMeso(Offset(x - l / 2, y), l, u, v(NEGRO, 0.22f), v(NEGRO, 0.2f), s + i * 97 + j)
                y += maxOf(l + 4f * u, 1f); j++
            }
        }

        // Por fila, en cada estela: el número en puntos y barras sobre una placa de estuco rojo y una tea.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(9f * u, w - 9f * u); val y0 = 12f * u + r * fila
            val pc = Offset(x, y0 + fila * 0.2f)
            drawRoundRect(v(CINABRIO, 0.5f), Offset(pc.x - 7f * u, pc.y - 6f * u), Size(14f * u, 14f * u), CornerRadius(2f * u))
            numeroMeso(Offset(pc.x, pc.y + 1f * u), (r * (cols + 1) + i) % 13 + 1, u, v(ESTUCO, 0.8f))
            val tb = Offset(x, y0 + fila * 0.6f)
            if (oscuro) haloAnt(Offset(tb.x, tb.y - 6f * u), 40f * u, llama.copy(alpha = 0.2f), 7)
            drawLine(v(NEGRO, 0.5f), Offset(tb.x - 3f * u, tb.y + 1f * u), Offset(tb.x + 3f * u, tb.y + 1f * u), 1.4f * u, StrokeCap.Round)
            drawLine(v(CALIZA_OSC, 0.6f), Offset(tb.x, tb.y + 1f * u), Offset(tb.x, tb.y + 7f * u), 1.4f * u)
            llamaAnt(Offset(tb.x, tb.y), 8f * u, if (oscuro) llama.copy(alpha = 0.9f) else v(llama, 0.5f), if (oscuro) corazon else v(corazon, 0.6f))
        }

        // El friso: la franja de cinabrio y la greca escalonada.
        drawRect(v(CINABRIO, 0.4f), Offset.Zero, Size(w, 4f * u))
        drawRect(v(ESTUCO, 0.4f), Offset(0f, 4f * u), Size(w, friso - 4f * u))
        val ga = friso - 6f * u
        var x = 0f; var j = 0
        while (x < w && j < 60) {
            val p = Path().apply {
                moveTo(x, 4f * u + ga + 1f * u)
                lineTo(x, 5f * u); lineTo(x + ga * 1.2f, 5f * u); lineTo(x + ga * 1.2f, 5f * u + ga * 0.5f); lineTo(x + ga * 0.6f, 5f * u + ga * 0.5f)
                moveTo(x + ga * 1.2f, 5f * u + ga); lineTo(x + ga * 1.8f, 5f * u + ga); lineTo(x + ga * 1.8f, 5f * u + ga * 0.5f); lineTo(x + ga * 2.4f, 5f * u + ga * 0.5f)
            }
            drawPath(p, v(JADE_HONDO, 0.45f), style = Stroke(1.3f * u))
            x += maxOf(ga * 2.4f, 1f); j++
        }
        drawRect(v(CINABRIO, 0.4f), Offset(0f, friso - 1.6f * u), Size(w, 1.6f * u))

        // El suelo: las gradas de la plaza, las cuentas de jade y los incensarios con su humo.
        drawRect(v(ESTUCO, 0.36f), Offset(0f, piso), Size(w, h - piso))
        for (k in 0 until 3) drawLine(v(CALIZA_OSC, 0.45f), Offset(0f, piso + k * 5f * u), Offset(w, piso + k * 5f * u), 0.8f * u)
        val luz = Offset(w / 2, h)
        drawRect(Brush.radialGradient(listOf((if (oscuro) llama else Color.White).copy(alpha = if (oscuro) 0.12f else 0.3f), Color.Transparent), luz, maxOf(w * 0.6f, 1f)), Offset(0f, piso), Size(w, h - piso))
        for (q in 0 until 10) {
            val c = Offset(w * azar(s, 900 + q), piso + (6f + 6f * azar(s, 920 + q)) * u)
            drawCircle(v(JADE, 0.5f), 1.4f * u, c)
        }
        for (i in 0..cols) {
            val bx = (i * paso + (if (i == cols) -1f else 1f) * (ew + 10f * u)).coerceIn(10f * u, w - 10f * u); val by = piso + 9f * u
            if (oscuro) haloAnt(Offset(bx, by - 8f * u), 36f * u, llama.copy(alpha = 0.22f), 6)
            // El humo del copal, que sube ondulando junto a la estela.
            val humo = Path().apply {
                moveTo(bx, by - 8f * u)
                for (q in 1..5) quadraticTo(bx + (if (q % 2 == 0) -5f else 5f) * u, by - (8f + q * 11f - 5.5f) * u, bx, by - (8f + q * 11f) * u)
            }
            drawPath(humo, (if (oscuro) Color(0xFFB0B8B8) else Color(0xFF8A8A80)).copy(alpha = 0.3f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { moveTo(bx - 6f * u, by - 7f * u); lineTo(bx + 6f * u, by - 7f * u); lineTo(bx + 4f * u, by); lineTo(bx - 4f * u, by); close() }, v(CINABRIO, 0.5f))
            for (q in -1..1) drawCircle(v(ESTUCO, 0.6f), 0.8f * u, Offset(bx + q * 3f * u, by - 3.5f * u))
            llamaAnt(Offset(bx, by - 7f * u), 5f * u, if (oscuro) llama.copy(alpha = 0.9f) else v(llama, 0.5f), if (oscuro) corazon else v(corazon, 0.6f))
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

    // ---------------------------------------------------------------- el salón del hall

    /**
     * El sótano del archivo del pueblo. Los muros de ladrillo, con manchas de humedad y el salitre que sube; a los lados de cada
     * trofeo, las estanterías de nogal con sus legajos atados, cajas de fichas y carpetas (más claras cuanto más lejos del
     * trofeo); entre las columnas de trofeos, la cañería de hierro con sus bridas y, por fila, una bombilla en su jaula. Arriba,
     * las vigas del techo y el tablero de recortes con alfileres e hilos verdes de uno a otro. Abajo, el piso de losas húmedas
     * con charcos que reflejan la luz y la niebla que entra a ras del suelo. De día la luz es la de unas bombillas pobres; de
     * noche las bombillas dan su luz ámbar y la niebla tiene un brillo verde que no debería tener. Dos degradados: la humedad del
     * muro y la niebla.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 97
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.5f, 1f) else k)
        val ladrillo = Color(0xFF8A4A32); val ambar = Color(0xFFF2C46A)
        val techo = 14f * u
        val piso = h - 14f * u
        val tw = minOf(5f * u, paso * 0.032f)

        // El muro de ladrillo: hiladas a soga, más tenues detrás de los trofeos, y la humedad que sube del piso.
        drawRect(v(ladrillo, 0.1f), Offset(0f, techo), Size(w, piso - techo))
        val lh = 8f * u; val lw = 22f * u
        var y = techo; var k = 0
        while (y < piso && k < 180) {
            var x = if (k % 2 == 0) 0f else -lw / 2; var j = 0
            while (x < w && j < 40) {
                val kk = 0.05f + 0.2f * cercaJuntaAnt(x + lw / 2, paso) * azar(s, k * 41 + j)
                drawRect(v(ladrillo, kk), Offset(x + 0.6f * u, y + 0.6f * u), Size(lw - 1.2f * u, lh - 1.2f * u))
                x += maxOf(lw, 1f); j++
            }
            y += maxOf(lh, 1f); k++
        }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) TAPETE else TE).copy(alpha = if (oscuro) 0.3f else 0.22f)), piso - 120f * u, piso), Offset(0f, piso - 120f * u), Size(w, 120f * u))
        // El salitre: un borde blanquecino irregular a media altura de la humedad.
        val salitre = Path().apply {
            moveTo(0f, piso - 60f * u)
            for (j in 1..20) lineTo(w * j / 20f, piso - (52f + 18f * azar(s, 50 + j)) * u)
        }
        drawPath(salitre, v(PAPEL, 0.3f), style = Stroke(1.2f * u))

        // Las estanterías a los lados de cada trofeo: los parales, las tablas y los legajos.
        for (i in 0 until cols) {
            val x0 = i * paso + tw * 2.6f; val x1 = (i + 1) * paso - tw * 2.6f; val cx = (i + 0.5f) * paso
            for (x in listOf(x0, x1 - 3f * u)) drawRect(v(NOGAL, 0.34f), Offset(x, techo), Size(3f * u, piso - techo))
            for (r in 0 until filas) for (t2 in 0..1) {
                val yb = 12f * u + r * fila + fila * (0.3f + 0.34f * t2)
                drawRect(v(NOGAL, 0.3f), Offset(x0, yb), Size(x1 - x0, 2.6f * u))
                drawRect(v(NOGAL_OSC, 0.3f), Offset(x0, yb + 2.6f * u), Size(x1 - x0, 1f * u))
                // Los legajos de pie, solo donde se ven: entre el paral y el trofeo.
                for (lado in listOf(-1f, 1f)) {
                    var x = if (lado < 0) x0 + 4f * u else cx + 52f * u
                    val fin = if (lado < 0) cx - 52f * u else x1 - 4f * u
                    var q = 0
                    while (x < fin - 3f * u && q < 14) {
                        val g = (3f + 4f * azar(s, i * 900 + r * 60 + t2 * 30 + q + (if (lado < 0) 0 else 15))) * u
                        val al = (16f + 14f * azar(s, i * 900 + r * 60 + t2 * 30 + q + 400)) * u
                        val gg = minOf(g, fin - x)
                        val kk = 0.2f + 0.2f * cercaJuntaAnt(x, paso)
                        val col = if (q % 4 == 2) v(PAPEL_VIEJO, kk + 0.08f) else if (q % 3 == 0) v(TE, kk) else v(NOGAL_LUZ, kk)
                        if (q % 5 == 4) {
                            // Una caja de fichas con su tirador de latón.
                            drawRect(v(PAPEL_VIEJO, kk + 0.06f), Offset(x, yb - 12f * u), Size(minOf(12f * u, fin - x), 12f * u))
                            drawCircle(v(LATON, kk + 0.15f), 0.9f * u, Offset(x + minOf(6f * u, (fin - x) / 2), yb - 6f * u))
                            x += maxOf(13f * u, 1f)
                        } else {
                            drawRect(col, Offset(x, yb - al), Size(gg, al))
                            drawLine(v(MARGEN_ROJO, kk + 0.08f), Offset(x, yb - al * 0.6f), Offset(x + gg, yb - al * 0.6f), 0.7f * u)
                            x += maxOf(g + 0.8f * u, 1f)
                        }
                        q++
                    }
                }
            }
        }

        // La cañería de hierro entre las columnas de trofeos, con sus bridas.
        for (i in 0..cols) {
            val x = i * paso
            drawRect(v(LATON_OSC, 0.4f), Offset(x - tw, techo), Size(2 * tw, piso - techo))
            drawRect(v(LATON, 0.22f), Offset(x - tw * 0.6f, techo), Size(tw * 0.5f, piso - techo))
            var yy = techo + 30f * u; var j = 0
            while (yy < piso && j < 30) { drawRect(v(LATON_OSC, 0.55f), Offset(x - tw * 1.5f, yy), Size(tw * 3f, 2.4f * u)); yy += maxOf(70f * u, 1f); j++ }
        }
        // Por fila, en la cañería: la bombilla en su jaula, con su luz.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(7f * u, w - 7f * u); val c = Offset(x, 12f * u + r * fila + fila * 0.56f)
            haloAnt(c, if (oscuro) 46f * u else 24f * u, ambar.copy(alpha = if (oscuro) 0.26f else 0.16f), 7)
            drawRect(v(TINTA, 0.5f), Offset(c.x - 3f * u, c.y - 9f * u), Size(6f * u, 3f * u))
            drawCircle(if (oscuro) Color(0xFFFFF0C0) else v(ambar, 0.6f), 3.6f * u, c)
            for (l in -1..1) drawLine(v(TINTA, 0.5f), Offset(c.x + l * 2.6f * u, c.y - 6f * u), Offset(c.x + l * 3.2f * u, c.y + 4f * u), 0.6f * u)
            drawArc(v(TINTA, 0.5f), 0f, 180f, false, Offset(c.x - 4.4f * u, c.y - 1f * u), Size(8.8f * u, 6.4f * u), style = Stroke(0.6f * u))
        }

        // Arriba: las vigas del techo y el tablero de recortes con alfileres e hilos verdes.
        drawRect(v(NOGAL_OSC, 0.4f), Offset.Zero, Size(w, techo))
        var x = 10f * u; var j = 0
        while (x < w && j < 30) { drawRect(v(NOGAL, 0.5f), Offset(x, 0f), Size(8f * u, techo)); drawLine(v(NOGAL_OSC, 0.6f), Offset(x + 8f * u, 0f), Offset(x + 8f * u, techo), 0.7f * u); x += maxOf(46f * u, 1f); j++ }
        val alfileres = (0..cols * 2).map { q -> Offset((q * paso / 2f + (azar(s, 300 + q) - 0.5f) * 10f * u).coerceIn(4f * u, w - 4f * u), techo + 4f * u + azar(s, 320 + q) * 8f * u) }
        alfileres.forEachIndexed { q, a ->
            if (q % 2 == 0) {
                drawRect(v(FICHA, 0.55f), Offset(a.x - 6f * u, a.y - 2f * u), Size(12f * u, 9f * u))
                for (l in 0 until 3) drawLine(v(TINTA, 0.35f), Offset(a.x - 4.5f * u, a.y + (1f + l * 2f) * u), Offset(a.x + 4.5f * u, a.y + (1f + l * 2f) * u), 0.5f * u)
            }
        }
        alfileres.zipWithNext().forEachIndexed { q, (a, b) ->
            val c = if (q % 3 == 1 && q + 2 < alfileres.size) alfileres[q + 2] else b
            drawLine(v(if (oscuro) VERDE_RARO else TAPETE, 0.5f), a, c, 0.8f * u)
        }
        alfileres.forEach { drawCircle(v(MARGEN_ROJO, 0.7f), 1.5f * u, it) }

        // El piso de losas húmedas, con charcos que reflejan las bombillas, y la niebla.
        drawRect(v(TINTA, 0.22f), Offset(0f, piso), Size(w, h - piso))
        x = 8f * u; j = 0
        while (x < w && j < 40) { drawLine(v(TINTA, 0.35f), Offset(x, piso), Offset(x - 6f * u, h), 0.6f * u); x += maxOf(30f * u, 1f); j++ }
        drawLine(v(TINTA, 0.4f), Offset(0f, piso + 6f * u), Offset(w, piso + 6f * u), 0.6f * u)
        for (i in 0..cols) {
            val c = Offset((i * paso).coerceIn(12f * u, w - 12f * u), piso + 8f * u)
            drawOval(v(RAYADO, 0.3f), Offset(c.x - 14f * u, c.y - 2.4f * u), Size(28f * u, 4.8f * u))
            drawOval(ambar.copy(alpha = if (oscuro) 0.5f else 0.3f), Offset(c.x - 3f * u, c.y - 1f * u), Size(6f * u, 2f * u))
        }
        val nb = minOf(70f * u, h * 0.1f)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) VERDE_RARO.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.4f)), Color.Transparent), h - nb, h), Offset(0f, h - nb), Size(w, nb))
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

    // ---------------------------------------------------------------- el salón del hall

    /** Un estandarte de dos colas de [ancho] × [alto] colgado de su asta en [o] (arriba al centro), con un emblema. */
    private fun DrawScope.estandarteAntiguas(o: Offset, ancho: Float, alto: Float, u: Float, tela: Color, borde: Color, asta: Color, emblema: Color) {
        drawLine(asta, Offset(o.x - ancho * 0.7f, o.y), Offset(o.x + ancho * 0.7f, o.y), 1.6f * u, StrokeCap.Round)
        val p = Path().apply {
            moveTo(o.x - ancho / 2, o.y); lineTo(o.x + ancho / 2, o.y); lineTo(o.x + ancho / 2, o.y + alto)
            lineTo(o.x, o.y + alto * 0.8f); lineTo(o.x - ancho / 2, o.y + alto); close()
        }
        drawPath(p, tela)
        drawPath(p, borde, style = Stroke(0.9f * u))
        drawPath(estrella(Offset(o.x, o.y + alto * 0.34f), ancho * 0.28f, 4, 0.4f), emblema)
        drawLine(emblema, Offset(o.x - ancho * 0.3f, o.y + alto * 0.58f), Offset(o.x + ancho * 0.3f, o.y + alto * 0.58f), 0.8f * u)
    }

    /**
     * El gran salón de piedra de una fortaleza antigua. Los muros de sillares de granito, cada uno de su tono; sobre cada
     * columna de trofeos, un arco de medio punto con sus dovelas, y arriba las vigas del techo con sus cabezas talladas.
     * Entre los arcos, pilares de granito con basa y capitel, una banda de runas inventadas y, por fila, un estandarte de dos
     * colas o un escudo redondo con su bloca de latón, y una antorcha en su argolla de hierro. Abajo, las losas del piso con
     * juncos esparcidos y una alfombra larga al centro. De día entra la luz de una ventana alta; de noche, solo las antorchas y
     * el resplandor del hogar al fondo. Dos degradados: la luz de la ventana (o del hogar) y la sombra del piso.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cols = e.columnas.coerceIn(1, 4); val paso = w / cols
        val (filas, fila) = filasSalonAnt()
        val s = e.semilla + 113
        fun v(c: Color, k: Float) = mezcla(e.fondo, n(c, oscuro), if (oscuro) minOf(k * 1.6f, 1f) else k)
        val tela1 = Color(0xFF1F3A5A); val tela2 = ROJO_MAPA
        val techo = 12f * u
        val piso = h - 14f * u
        val pw = minOf(9f * u, paso * 0.058f)

        // Los sillares de granito, cada uno de su tono, más tenues detrás de los trofeos.
        drawRect(v(GRANITO, 0.1f), Offset(0f, techo), Size(w, piso - techo))
        val sh = 16f * u; val sw = 34f * u
        var y = techo; var k = 0
        while (y < piso && k < 100) {
            var x = if (k % 2 == 0) 0f else -sw / 2; var j = 0
            while (x < w && j < 40) {
                val junta = cercaJuntaAnt(x + sw / 2, paso)
                val kk = (0.04f + 0.12f * azar(s, k * 43 + j)) * (0.5f + junta)
                drawRect(v(if (azar(s, k * 43 + j + 7) > 0.5f) GRANITO_LUZ else GRANITO, kk), Offset(x + 0.7f * u, y + 0.7f * u), Size(sw - 1.4f * u, sh - 1.4f * u))
                x += maxOf(sw * (0.8f + 0.4f * azar(s, k * 43 + j + 9)), 1f); j++
            }
            y += maxOf(sh, 1f); k++
        }
        // La luz: de día, la de una ventana alta a la izquierda; de noche, el resplandor del hogar al fondo, abajo.
        val foco = if (oscuro) Offset(w / 2, h + 20f * u) else Offset(w * 0.15f, 0f)
        val rf = maxOf(if (oscuro) minOf(w * 0.9f, 300f * u) else w, 1f)
        drawCircle(Brush.radialGradient(listOf((if (oscuro) LLAMA.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.4f)), Color.Transparent), foco, rf), rf, foco)

        // Los arcos de medio punto con sus dovelas, uno sobre cada columna de trofeos.
        for (i in 0 until cols) {
            val x0 = i * paso + pw; val x1 = (i + 1) * paso - pw
            val r = (x1 - x0) / 2; val cy = techo + minOf(r * 0.45f, 40f * u) + 6f * u
            val caja = Rect(x0, cy - r * 0.55f, x1, cy + r * 0.55f)
            drawArc(v(GRANITO_OSC, 0.4f), 180f, 180f, false, caja.topLeft, caja.size, style = Stroke(7f * u))
            drawArc(v(GRANITO_LUZ, 0.4f), 180f, 180f, false, Offset(caja.left + 3f * u, caja.top + 3f * u), Size(caja.width - 6f * u, caja.height - 6f * u), style = Stroke(0.8f * u))
            for (d in 1 until 11) {
                val a = Math.toRadians(180.0 + d * 180.0 / 11).toFloat()
                val rx = caja.width / 2; val ry = caja.height / 2
                val p0 = Offset(caja.center.x + (rx - 3.5f * u) * kotlin.math.cos(a), caja.center.y + (ry - 3.5f * u) * kotlin.math.sin(a))
                val p1 = Offset(caja.center.x + (rx + 3.5f * u) * kotlin.math.cos(a), caja.center.y + (ry + 3.5f * u) * kotlin.math.sin(a))
                drawLine(v(NUDO, 0.3f), p0, p1, 0.7f * u)
            }
            // La clave del arco, un poco más grande.
            drawRect(v(GRANITO_LUZ, 0.45f), Offset(caja.center.x - 3f * u, caja.top - 4.5f * u), Size(6f * u, 9f * u))
        }

        // Los pilares: el fuste con sus juntas, la basa, el capitel y una banda de runas.
        for (i in 0..cols) {
            val x = i * paso
            drawRect(v(GRANITO, 0.42f), Offset(x - pw, techo), Size(2 * pw, piso - techo))
            drawRect(if (oscuro) Color.Black.copy(alpha = 0.2f) else v(GRANITO_OSC, 0.2f), Offset(x + pw * 0.3f, techo), Size(pw * 0.7f, piso - techo))
            drawRect(v(GRANITO_LUZ, 0.3f), Offset(x - pw, techo), Size(pw * 0.3f, piso - techo))
            var yy = techo + 20f * u; var j = 0
            while (yy < piso && j < 80) { drawLine(v(GRANITO_OSC, 0.4f), Offset(x - pw, yy), Offset(x + pw, yy), 0.6f * u); yy += maxOf(20f * u, 1f); j++ }
            drawRect(v(GRANITO_LUZ, 0.5f), Offset(x - pw * 1.4f, techo + 2f * u), Size(pw * 2.8f, 5f * u))
            drawRect(v(GRANITO_OSC, 0.5f), Offset(x - pw * 1.2f, techo + 7f * u), Size(pw * 2.4f, 2f * u))
            drawRect(v(GRANITO_LUZ, 0.5f), Offset(x - pw * 1.4f, piso - 6f * u), Size(pw * 2.8f, 6f * u))
        }

        // Por fila, en cada pilar: las runas, el estandarte o el escudo, y la antorcha en su argolla.
        for (r in 0 until filas) for (i in 0..cols) {
            val x = (i * paso).coerceIn(10f * u, w - 10f * u); val y0 = 12f * u + r * fila
            runasAnt(Offset(x - pw * 0.7f, y0 + fila * 0.12f), pw * 1.4f, 7f * u, v(TALLA, 0.5f), 0.8f * u, 2, s + r * 7 + i)
            if ((r + i) % 3 != 2) {
                val tela = if ((r + i) % 2 == 0) tela1 else tela2
                estandarteAntiguas(Offset(x, y0 + fila * 0.2f), minOf(16f * u, pw * 2.2f), 44f * u, u, v(tela, 0.4f), v(LATON, 0.5f), v(MADERA, 0.6f), v(LATON_LUZ, 0.6f))
            } else {
                val c = Offset(x, y0 + fila * 0.32f); val rr = minOf(9f * u, pw * 1.2f)
                drawCircle(v(MADERA, 0.5f), rr, c)
                for (q in 0 until 4) drawLine(v(TALLA, 0.6f), Offset(c.x - rr, c.y + (q - 1.5f) * rr * 0.45f), Offset(c.x + rr, c.y + (q - 1.5f) * rr * 0.45f), 0.6f * u)
                drawCircle(v(HIERRO, 0.6f), rr, c, style = Stroke(1.2f * u))
                drawCircle(v(LATON, 0.65f), rr * 0.32f, c)
            }
            val tb = Offset(x, y0 + fila * 0.62f)
            if (oscuro) haloAnt(Offset(tb.x, tb.y - 10f * u), 48f * u, LLAMA.copy(alpha = 0.24f), 7)
            else haloAnt(Offset(tb.x, tb.y - 10f * u), 16f * u, LLAMA.copy(alpha = 0.18f), 4)
            drawCircle(v(HIERRO, 0.6f), 2.4f * u, Offset(tb.x, tb.y + 4f * u), style = Stroke(1f * u))
            drawLine(v(MADERA, 0.65f), Offset(tb.x, tb.y + 9f * u), Offset(tb.x, tb.y - 3f * u), 2.2f * u, StrokeCap.Round)
            drawRect(v(HIERRO, 0.6f), Offset(tb.x - 2.2f * u, tb.y - 4f * u), Size(4.4f * u, 2.4f * u))
            llamaAnt(Offset(tb.x, tb.y - 4f * u), 11f * u, if (oscuro) LLAMA.copy(alpha = 0.92f) else v(LLAMA, 0.6f), if (oscuro) Color(0xFFFFF1C8) else v(Color(0xFFFFF1C8), 0.7f))
        }

        // Arriba, las vigas del techo con sus cabezas talladas.
        drawRect(v(MADERA, 0.4f), Offset.Zero, Size(w, techo))
        vetas(Rect(0f, 0f, w, techo), v(NUDO, 0.4f), s, densidad = 0.8f, grosor = 0.6f)
        var x = paso / 2; var j = 0
        while (x < w && j < 12) {
            drawRoundRect(v(TALLA, 0.55f), Offset(x - 7f * u, techo - 3f * u), Size(14f * u, 7f * u), CornerRadius(3f * u))
            drawCircle(v(LATON, 0.5f), 1.4f * u, Offset(x, techo + 0.5f * u))
            x += maxOf(paso, 1f); j++
        }
        drawLine(v(NUDO, 0.5f), Offset(0f, techo), Offset(w, techo), 0.9f * u)

        // Las losas del piso, la alfombra al centro y los juncos.
        drawRect(Brush.verticalGradient(listOf(v(GRANITO, 0.34f), v(GRANITO_OSC, 0.44f)), piso, h), Offset(0f, piso), Size(w, h - piso))
        x = 0f; j = 0
        while (x < w && j < 40) { drawLine(v(GRANITO_OSC, 0.5f), Offset(x, piso), Offset(x + (x - w / 2) * 0.05f, h), 0.7f * u); x += maxOf(28f * u, 1f); j++ }
        drawLine(v(GRANITO_OSC, 0.5f), Offset(0f, piso + 6f * u), Offset(w, piso + 6f * u), 0.6f * u)
        val al = minOf(w * 0.3f, 120f * u)
        drawRect(v(ROJO_MAPA, 0.32f), Offset(w / 2 - al / 2, piso + 2f * u), Size(al, h - piso - 2f * u))
        drawRect(v(LATON, 0.4f), Offset(w / 2 - al / 2 + 3f * u, piso + 4f * u), Size(al - 6f * u, h - piso - 6f * u), style = Stroke(0.8f * u))
        for (q in 0 until 16) {
            val c = Offset(w * azar(s, 700 + q), piso + (3f + 9f * azar(s, 720 + q)) * u)
            if (kotlin.math.abs(c.x - w / 2) < al / 2) continue
            val a = (azar(s, 740 + q) - 0.5f) * 0.8f
            drawLine(v(PERGAMINO_OSC, 0.5f), c, Offset(c.x + 9f * u * kotlin.math.cos(a), c.y + 9f * u * kotlin.math.sin(a)), 0.6f * u)
        }
    }
}
