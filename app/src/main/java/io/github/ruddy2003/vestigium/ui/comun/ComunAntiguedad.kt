package io.github.ruddy2003.vestigium.ui.comun

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Las piezas comunes de la Antigüedad (y de los dos temas de fantasía que comparten sus materiales: Arcano y Antiguas)
// =====================================================================

/**
 * Los renglones tenues de un campo de varios renglones: líneas al paso de 24 sp, la primera un paso bajo [arriba] (el margen
 * de arriba del texto), hasta antes de [abajo]. Con [luz], cada renglón lleva debajo una raya clara, como si estuviera grabado.
 */
private fun DrawScope.renglonesAntiguedad(izq: Float, der: Float, arriba: Float, abajo: Float, color: Color, grosor: Float, luz: Color? = null) {
    val paso = maxOf(24.sp.toPx(), 1f)
    var y = arriba + paso
    var k = 0
    while (y < abajo && k < 200) {
        drawLine(color, Offset(izq, y), Offset(der, y), grosor)
        if (luz != null) drawLine(luz, Offset(izq, y + grosor), Offset(der, y + grosor), grosor * 0.8f)
        y += paso; k++
    }
}

/**
 * Prehistoria: el campo es una laja de caliza de borde desportillado, con el grano de la roca en la orilla y la cara del medio
 * alisada a mano, lisa, donde va el texto. A la izquierda, tres puntos de ocre puestos con el dedo; abajo a la derecha, unas
 * rayas de cuenta con carbón. Con el foco se enciende una brasa junto a los puntos y el contorno de la cara se pinta de ocre
 * amarillo; con un error, de hematites. Un degradado: la brasa.
 */
object ComunCueva : DisenoComun {
    private val ROCA = Color(0xFFB09A7C)
    private val ROCA_LUZ = Color(0xFFD6C6A8)
    private val ROCA_OSC = Color(0xFF7A6650)
    private val ROCA_HONDA = Color(0xFF4A3B2D)
    private val CALIZA = Color(0xFFE6DAC2)
    private val OCRE = Color(0xFFA2401F)
    private val OCRE_AMARILLO = Color(0xFFC98A2E)
    private val CARBON = Color(0xFF221A15)
    private val BRASA = Color(0xFFE8804F)
    private val FUEGO = Color(0xFFF4C76E)
    /** El ocre amarillo tostado al fuego: el del rótulo. */
    private val TOSTADO = Color(0xFF7E4C14)
    /** El rojo de hematites molida: el del error, más vivo que el ocre de los puntos. */
    private val HEMATITES = Color(0xFFB0201A)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B08), 0.76f) else c
    /** La cara alisada de la laja, donde va el texto. */
    private fun cara(oscuro: Boolean): Color = hondo(CALIZA, oscuro)

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), cara(oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) FUEGO else TOSTADO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFFF8C78) else HEMATITES

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cara = cara(oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, cara, 0.45f)
        // La laja entera, desportillada, con su sombra.
        val fuera = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        val puntos = ((fuera.width + fuera.height) * 2 / (7 * u)).toInt().coerceIn(24, 220)
        val laja = contornoIrregular(fuera, s, puntos, 2.2f * u / minOf(fuera.width, fuera.height).coerceAtLeast(1f))
        translate(0f, 1.5f * u) { drawPath(laja, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f)) }
        drawPath(laja, a(t(ROCA, oscuro)))
        // El grano de la roca (la cara alisada lo tapa en el medio).
        val granos = (w * h / (40 * u * u)).toInt().coerceIn(20, 400)
        clipPath(laja) {
            for (j in 0 until granos) drawCircle(a(t(if (j % 3 == 0) ROCA_LUZ else ROCA_OSC, oscuro)).copy(alpha = 0.5f), (0.5f + 0.7f * azar(s, j)) * u,
                Offset(w * azar(s, j + 500), h * azar(s, j + 1000)))
        }
        // La cara alisada: lisa y más clara, con su canto.
        val dentro = Rect(fuera.left + 4 * u, fuera.top + 4 * u, fuera.right - 4 * u, fuera.bottom - 3.5f * u)
        val alisada = contornoIrregular(dentro, s + 7, (puntos * 0.8f).toInt().coerceAtLeast(16), 0.9f * u / minOf(dentro.width, dentro.height).coerceAtLeast(1f))
        drawPath(alisada, cara)
        drawPath(alisada, a(t(ROCA_HONDA, oscuro)).copy(alpha = 0.45f), style = Stroke(1 * u))
        if (e.multilinea) renglonesAntiguedad(dentro.left + 12 * u, dentro.right - 6 * u, margenCampoY.toPx(), dentro.bottom - 4 * u,
            tintaCampo(oscuro).copy(alpha = if (oscuro) 0.1f else 0.12f), 0.8f * u)
        // Los tres puntos de ocre, a la izquierda, a la altura del primer renglón.
        val cy = minOf(h / 2, dentro.top + 18 * u)
        val cx = dentro.left + 4.5f * u
        val encendido = e.enfocado && e.habilitado
        if (encendido && !e.error) clipPath(laja) { mancha(Offset(cx, cy), 24 * u, BRASA.copy(alpha = if (oscuro) 0.3f else 0.2f)) }
        val punto = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) FUEGO else BRASA
            else -> a(if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE).copy(alpha = 0.8f)
        }
        for (j in -1..1) drawCircle(punto, (1.6f + 0.3f * azar(s, 40 + j)) * u, Offset(cx + (azar(s, 50 + j) - 0.5f) * 1.2f * u, cy + j * 6 * u))
        // Las rayas de cuenta, abajo a la derecha: cuatro de pie y una cruzada.
        val carbon = a(if (oscuro) CALIZA.copy(alpha = 0.3f) else CARBON.copy(alpha = 0.45f))
        val y0 = dentro.bottom - 8 * u; val y1 = dentro.bottom - 2.5f * u
        val x0 = dentro.right - 17 * u
        for (j in 0 until 4) drawLine(carbon, Offset(x0 + j * 2.6f * u, y0 + azar(s, 60 + j) * u), Offset(x0 + j * 2.6f * u + 0.4f * u, y1), 1 * u, StrokeCap.Round)
        drawLine(carbon, Offset(x0 - 1.5f * u, y1 - 1 * u), Offset(x0 + 9.5f * u, y0 + 1 * u), 1 * u, StrokeCap.Round)
        // El contorno pintado: ocre amarillo con el foco, hematites con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) FUEGO else OCRE_AMARILLO
            else -> null
        }
        if (linea != null) drawPath(alisada, linea.copy(alpha = 0.95f), style = Stroke(1.9f * u))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: un trazo de carbón hecho a mano sobre la roca, que tiembla un poco (pocos tramos curvos, a lo más 16 por
     * mitad), cortado al centro por los tres puntos de ocre del campo, puestos en fila. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val s = e.semilla
        val f = if (e.tenue) 0.5f else 1f
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = (if (oscuro) 0.26f else 0.4f) * f)
        val ocre = (if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE).copy(alpha = (if (oscuro) 0.8f else 0.72f) * f)
        val hueco = 13 * u
        val tramo = maxOf(34 * u, 1f)
        for (lado in 0..1) {
            val x0 = if (lado == 0) 2 * u else w / 2 + hueco
            val x1 = if (lado == 0) w / 2 - hueco else w - 2 * u
            if (x1 - x0 < 4 * u) continue
            val n = ((x1 - x0) / tramo).toInt().coerceIn(1, 16)
            val p = Path().apply {
                moveTo(x0, cy + (azar(s, 70 + lado) - 0.5f) * 0.6f * u)
                for (j in 1..n) {
                    val xa = x0 + (x1 - x0) * (j - 0.5f) / n
                    val xb = x0 + (x1 - x0) * j / n
                    quadraticTo(xa, cy + (azar(s, 100 * lado + j) - 0.5f) * 1.8f * u, xb, cy + (azar(s, 100 * lado + j + 50) - 0.5f) * 0.6f * u)
                }
            }
            drawPath(p, carbon, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        }
        for (j in -1..1) drawCircle(ocre, (1.3f + 0.3f * azar(s, 40 + j)) * u, Offset(w / 2 + j * 5.5f * u, cy + (azar(s, 50 + j) - 0.5f) * 1.2f * u))
    }

    /** El texto de los botones: el ocre de los puntos, tostado un poco para que se lea sobre la roca clara; de noche, la brasa. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) BRASA else mezcla(OCRE, CARBON, 0.16f)

    /**
     * Botón de texto: una pasada de ocre con el dedo bajo la palabra, curva y con un hilo más fino que se le escapa. Al
     * tocarlo, la mano unta de ocre la roca detrás del texto (una mancha de borde irregular) y la pasada se aviva; apagado,
     * el ocre queda en polvo. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cy = h / 2
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
        val apagado = if (e.habilitado) 1f else 0.3f
        val tocado = e.presionado && e.habilitado
        if (tocado && w > 12 * u) {
            val r = Rect(4 * u, cy - 15 * u, w - 4 * u, cy + 15 * u)
            val puntos = ((r.width + r.height) * 2 / (8 * u)).toInt().coerceIn(12, 120)
            drawPath(contornoIrregular(r, s + 3, puntos, 2.4f * u / minOf(r.width, r.height).coerceAtLeast(1f)), ocre.copy(alpha = if (oscuro) 0.2f else 0.15f))
        }
        val x0 = minOf(12 * u, w * 0.25f); val x1 = w - x0
        val y = cy + 11 * u
        val alfa = (if (tocado) 0.9f else 0.5f) * apagado
        val pasada = Path().apply { moveTo(x0, y + 0.6f * u); quadraticTo(w / 2, y - 1.4f * u, x1, y - 0.2f * u) }
        drawPath(pasada, ocre.copy(alpha = alfa), style = Stroke(2.1f * u, cap = StrokeCap.Round))
        val hilo = Path().apply { moveTo(x0 + 3 * u, y + 1.9f * u); quadraticTo(w / 2, y + 0.3f * u, x1 + 2 * u, y + 1.1f * u) }
        drawPath(hilo, ocre.copy(alpha = alfa * 0.5f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** La piel curtida de la bolsa del vacío. */
    private val PIEL = Color(0xFF9A6A42)

    /**
     * Un trazo de carbón hecho a mano de [x0] a [x1] a la altura [y], que tiembla hasta [temblor] (pocos tramos curvos, a lo
     * más 12).
     */
    private fun DrawScope.trazoCarbon(x0: Float, x1: Float, y: Float, s: Int, color: Color, grosor: Float, temblor: Float) {
        if (x1 - x0 < 2f) return
        val n = ((x1 - x0) / maxOf(40 * density, 1f)).toInt().coerceIn(1, 12)
        val p = Path().apply {
            moveTo(x0, y + (azar(s, 1) - 0.5f) * temblor * 0.4f)
            for (j in 1..n) {
                val xa = x0 + (x1 - x0) * (j - 0.5f) / n; val xb = x0 + (x1 - x0) * j / n
                quadraticTo(xa, y + (azar(s, 10 + j) - 0.5f) * temblor, xb, y + (azar(s, 40 + j) - 0.5f) * temblor * 0.4f)
            }
        }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: casi nada sobre la roca: un trazo de carbón fino abajo, que tiembla, y a la izquierda un punto de ocre puesto con el
     * dedo (dos, uno sobre otro, si la fila lleva detalle). Al tocarla la mano unta la roca de ocre (una mancha de borde
     * irregular, muy tenue) y el punto se aviva. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.45f) else OCRE
        if (e.presionado && w > 8 * u && h > 8 * u) {
            val r = Rect(2 * u, 3 * u, w - 2 * u, h - 3 * u)
            val puntos = ((r.width + r.height) * 2 / (14 * u)).toInt().coerceIn(12, 90)
            drawPath(contornoIrregular(r, s + 5, puntos, 2f * u / minOf(r.width, r.height).coerceAtLeast(1f)), ocre.copy(alpha = if (oscuro) 0.16f else 0.08f))
        }
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.17f else 0.24f)
        trazoCarbon(margenFila.toPx(), w - 6 * u, h - 1.4f * u, s, carbon, 0.9f * u, 1.4f * u)
        val punto = ocre.copy(alpha = if (e.presionado) 0.95f else 0.62f)
        val x = 5.5f * u
        if (e.conDetalle) {
            drawCircle(punto, (1.4f + 0.3f * azar(s, 3)) * u, Offset(x, h / 2 - 3.4f * u))
            drawCircle(punto, (1.2f + 0.3f * azar(s, 4)) * u, Offset(x + 0.6f * u, h / 2 + 3.4f * u))
        } else drawCircle(punto, (1.5f + 0.3f * azar(s, 3)) * u, Offset(x, h / 2))
    }

    /** Flecha: una punta de sílex tallada en forma de hoja, con la punta a la derecha, la escotadura del astil y sus facetas. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val x0 = w * 0.14f; val x1 = w * 0.92f; val xm = w * 0.5f
        val a = h * 0.27f
        val punta = Path().apply {
            moveTo(x1, cy)
            quadraticTo(xm, cy - a * 1.25f, x0, cy - a * 0.5f)
            lineTo(x0 + w * 0.1f, cy)
            lineTo(x0, cy + a * 0.5f)
            quadraticTo(xm, cy + a * 1.25f, x1, cy)
            close()
        }
        val piedra = if (oscuro) mezcla(ROCA_LUZ, ROCA, 0.35f) else mezcla(ROCA_OSC, CARBON, 0.35f)
        drawPath(punta, piedra)
        drawPath(punta, (if (oscuro) CALIZA else CARBON).copy(alpha = 0.55f), style = Stroke(0.7f * u))
        val faceta = (if (oscuro) CARBON else CALIZA).copy(alpha = 0.45f)
        for (j in 0 until 3) {
            val x = x0 + w * (0.22f + 0.17f * j)
            drawLine(faceta, Offset(x, cy - 0.3f * u), Offset(x + w * 0.08f, cy - a * (0.62f - 0.12f * j)), 0.6f * u)
            drawLine(faceta, Offset(x + w * 0.04f, cy + 0.4f * u), Offset(x + w * 0.11f, cy + a * (0.55f - 0.12f * j)), 0.6f * u)
        }
    }

    /** Los dedos de la mano (de la base a la punta, en un lienzo de 24): índice, medio, anular, meñique y pulgar. */
    private val DEDOS = floatArrayOf(9.7f, 11f, 8.9f, 4.7f, 12.1f, 10.6f, 12.2f, 3.6f, 14.5f, 11f, 15.3f, 4.9f, 16.3f, 12.7f, 18.1f, 7.7f, 8.4f, 15.2f, 4.6f, 11.6f)

    /** Si el punto ([x], [y], en el lienzo de 24) cae en la mano, con [holgura] de más. */
    private fun enMano(x: Float, y: Float, holgura: Float): Boolean {
        val dx = (x - 12.4f) / (4.6f + holgura); val dy = (y - 15f) / (4.8f + holgura)
        if (dx * dx + dy * dy < 1f) return true
        if (x > 9.6f - holgura && x < 14.4f + holgura && y > 17.5f) return true
        for (j in 0 until 5) {
            val ax = DEDOS[4 * j]; val ay = DEDOS[4 * j + 1]; val bx = DEDOS[4 * j + 2]; val by = DEDOS[4 * j + 3]
            val vx = bx - ax; val vy = by - ay
            val t = (((x - ax) * vx + (y - ay) * vy) / (vx * vx + vy * vy)).coerceIn(0f, 1f)
            val ex = ax + vx * t - x; val ey = ay + vy * t - y
            if (ex * ex + ey * ey < (1.2f + holgura) * (1.2f + holgura)) return true
        }
        return false
    }

    /**
     * Marca de fijar: una mano, como las de las paredes de las cuevas. Fija, es la mano en positivo, untada de ocre y apoyada
     * en la roca; sin fijar, la mano en negativo: solo el soplo de ocre alrededor, tenue, y la mano vacía en medio. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        fun p(x: Float, y: Float) = Offset(ox + x * k, oy + y * k)
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.55f) else OCRE
        fun mano(c: Color) {
            drawOval(c, p(7.8f, 10.2f), Size(9.2f * k, 9.6f * k))
            for (j in 0 until 5) drawLine(c, p(DEDOS[4 * j], DEDOS[4 * j + 1]), p(DEDOS[4 * j + 2], DEDOS[4 * j + 3]), 2.4f * k, StrokeCap.Round)
            drawRect(c, p(9.7f, 17.6f), Size(4.6f * k, 5.4f * k))
        }
        if (fija) mano(ocre)
        else {
            // La mano en negativo: la silueta apenas (en una capa, para que los dedos no se oscurezcan donde se cruzan con la
            // palma) y el soplo de ocre en una orla alrededor.
            drawContext.canvas.saveLayer(Rect(0f, 0f, size.width, size.height), Paint().apply { alpha = if (oscuro) 0.3f else 0.22f })
            mano(ocre)
            drawContext.canvas.restore()
            val soplo = ocre.copy(alpha = if (oscuro) 0.62f else 0.55f)
            for (j in 0 until 360) {
                val x = 1f + 22f * azar(7, j); val y = 1f + 22f * azar(7, j + 400)
                if (!enMano(x, y, 1.5f) || enMano(x, y, 0.45f)) continue
                drawCircle(soplo, (0.35f + 0.3f * azar(7, j + 800)) * k, p(x, y))
            }
        }
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) ROCA_LUZ else ROCA_HONDA

    /** La laja del vacío: clara de día; de noche, la roca honda de la cueva. */
    private fun fondoVacio(oscuro: Boolean): Color = if (oscuro) hondo(ROCA, true) else mezcla(CALIZA, Color.White, 0.3f)

    /**
     * Vacío: un rincón liso de la pared, todavía sin pintar: una laja clara de borde desportillado, con el grano de la roca
     * apenas, el contorno repasado con carbón a tramos sueltos (no cerrado, para que no parezca un botón) y, abajo a la derecha,
     * una sola raya de cuenta, la primera. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val puntos = ((r.width + r.height) * 2 / (9 * u)).toInt().coerceIn(20, 200)
        val laja = contornoIrregular(r, s, puntos, 2.6f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(laja, fondoVacio(oscuro))
        val granos = (w * h / (170 * u * u)).toInt().coerceIn(10, 260)
        for (j in 0 until granos) {
            val c = if (j % 3 == 0) ROCA_LUZ else ROCA_OSC
            drawCircle(t(c, oscuro).copy(alpha = if (oscuro) 0.22f else 0.2f), (0.4f + 0.6f * azar(s, j)) * u,
                Offset(r.left + 6 * u + (r.width - 12 * u) * azar(s, j + 500), r.top + 6 * u + (r.height - 12 * u) * azar(s, j + 1000)))
        }
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.2f else 0.28f)
        drawPath(laja, carbon, style = Stroke(1 * u, cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(30 * u, 10 * u), 20 * u * azar(s, 9))))
        val x = r.right - 12 * u
        drawLine(carbon.copy(alpha = carbon.alpha * 1.5f), Offset(x, r.bottom - 13 * u), Offset(x + 0.6f * u, r.bottom - 6 * u), 1.1f * u, StrokeCap.Round)
    }

    /**
     * Lo vacío: la bolsa de piel, abierta y sin nada: la boca abierta muestra el fondo oscuro, la costura baja por el medio, el
     * cordón cuelga suelto y en la piel van los tres puntos de ocre del tema. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val piel = t(PIEL, oscuro)
        val borde = if (oscuro) mezcla(PIEL, CALIZA, 0.35f) else mezcla(PIEL, CARBON, 0.55f)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.15f), Offset(f(0.22f), f(0.88f)), Size(f(0.56f), f(0.08f)))
        val cuerpo = Path().apply {
            moveTo(f(0.33f), f(0.32f))
            cubicTo(f(0.34f), f(0.42f), f(0.08f), f(0.5f), f(0.12f), f(0.74f))
            cubicTo(f(0.16f), f(0.94f), f(0.84f), f(0.94f), f(0.88f), f(0.74f))
            cubicTo(f(0.92f), f(0.5f), f(0.66f), f(0.42f), f(0.67f), f(0.32f))
            close()
        }
        drawPath(cuerpo, piel)
        drawPath(cuerpo, borde, style = Stroke(1.1f * u))
        // Los pliegues del fruncido, bajo la boca.
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply { moveTo(f(0.5f + l * 0.1f), f(0.38f)); quadraticTo(f(0.5f + l * 0.16f), f(0.48f), f(0.5f + l * 0.24f), f(0.52f)) },
                borde.copy(alpha = 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
        // La boca abierta, con lo hondo de la bolsa vacía.
        drawOval(piel, Offset(f(0.26f), f(0.24f)), Size(f(0.48f), f(0.15f)))
        drawOval(if (oscuro) Color(0xFF0B0806) else mezcla(CARBON, PIEL, 0.25f), Offset(f(0.31f), f(0.27f)), Size(f(0.38f), f(0.09f)))
        drawOval(borde, Offset(f(0.26f), f(0.24f)), Size(f(0.48f), f(0.15f)), style = Stroke(1 * u))
        // La costura: puntadas cortas por el medio.
        for (j in 0 until 4) {
            val y = f(0.55f + 0.085f * j)
            drawLine(borde.copy(alpha = 0.8f), Offset(f(0.47f), y), Offset(f(0.53f), y + f(0.03f)), 0.8f * u, StrokeCap.Round)
        }
        // El cordón suelto.
        val cordon = Path().apply { moveTo(f(0.7f), f(0.33f)); quadraticTo(f(0.94f), f(0.4f), f(0.86f), f(0.62f)); quadraticTo(f(0.82f), f(0.7f), f(0.9f), f(0.76f)) }
        drawPath(cordon, borde, style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawCircle(borde, f(0.025f), Offset(f(0.9f), f(0.76f)))
        // Los tres puntos de ocre.
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.55f) else OCRE
        for (j in 0 until 3) drawCircle(ocre, f(0.028f), Offset(f(0.26f + j * 0.07f), f(0.66f + (j % 2) * 0.03f)))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) CALIZA else CARBON

    /**
     * Aviso: una laja lisa, un tono más honda que la roca, y a la izquierda la marca pintada con el dedo: una franja de ocre que
     * baja temblando de arriba abajo y, junto a ella, uno de los signos que se repiten en las paredes, según [EstadoAviso.semilla]:
     * los tres puntos, el zigzag o el claviforme (una raya con su abultamiento). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val r = Rect(0f, 0f, w, h)
        val puntos = ((r.width + r.height) * 2 / (12 * u)).toInt().coerceIn(16, 160)
        val laja = contornoIrregular(r, s + 11, puntos, 1.4f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(laja, if (oscuro) hondo(ROCA_OSC, true) else mezcla(CALIZA, ROCA, 0.22f))
        // La franja de ocre, untada con el dedo.
        val ocre = if (oscuro) mezcla(OCRE, OCRE_AMARILLO, 0.5f) else OCRE
        val n = (h / maxOf(14 * u, 1f)).toInt().coerceIn(1, 20)
        val franja = Path().apply {
            moveTo(7 * u, 5 * u)
            for (j in 1..n) {
                val ya = 5 * u + (h - 10 * u) * (j - 0.5f) / n; val yb = 5 * u + (h - 10 * u) * j / n
                quadraticTo(7 * u + (azar(s, 20 + j) - 0.5f) * 2.2f * u, ya, 7 * u + (azar(s, 50 + j) - 0.5f) * 0.8f * u, yb)
            }
        }
        drawPath(franja, ocre.copy(alpha = 0.85f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        // El signo, a la altura del primer renglón.
        val signo = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.6f else 0.7f)
        val cx = 17 * u; val y0 = 11 * u
        when (((s % 3) + 3) % 3) {
            0 -> for (j in 0 until 3) drawCircle(ocre, (1.3f + 0.3f * azar(s, j)) * u, Offset(cx + (azar(s, j + 5) - 0.5f) * u, y0 + 1.5f * u + j * 4.5f * u))
            1 -> drawPath(Path().apply {
                moveTo(cx - 2.5f * u, y0); lineTo(cx + 2.5f * u, y0 + 3 * u); lineTo(cx - 2.5f * u, y0 + 6 * u)
                lineTo(cx + 2.5f * u, y0 + 9 * u); lineTo(cx - 2.5f * u, y0 + 12 * u)
            }, signo, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            else -> {
                drawLine(signo, Offset(cx - 1 * u, y0), Offset(cx - 1 * u, y0 + 13 * u), 1.3f * u, StrokeCap.Round)
                drawPath(Path().apply { moveTo(cx - 1 * u, y0 + 2 * u); quadraticTo(cx + 4.5f * u, y0 + 3.5f * u, cx - 1 * u, y0 + 7 * u) }, signo, style = Stroke(1.2f * u, cap = StrokeCap.Round))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) FUEGO else mezcla(OCRE, CARBON, 0.3f)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) ROCA_LUZ else ROCA_HONDA

    /**
     * Cifra: un canto rodado del río, liso y de esquinas muy redondas, con su sombra, el grano apenas, una luz de agua arriba a
     * la izquierda y, en el margen de arriba a la derecha, las rayas de cuenta con carbón (cuatro de pie y una cruzada). Sin
     * degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 8 * u || h < 8 * u) return
        val radio = CornerRadius(minOf(18 * u, h * 0.32f), minOf(16 * u, h * 0.3f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f), Offset(0.5f * u, 1.8f * u), Size(w - 1 * u, h - 2 * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 2 * u)
        drawRoundRect(if (oscuro) hondo(ROCA_LUZ, true) else ROCA_LUZ, r.topLeft, r.size, radio)
        val granos = (w * h / (90 * u * u)).toInt().coerceIn(8, 80)
        for (j in 0 until granos) drawCircle(t(if (j % 2 == 0) ROCA_OSC else CALIZA, oscuro).copy(alpha = 0.28f), (0.35f + 0.5f * azar(s, j)) * u,
            Offset(r.left + 8 * u + (r.width - 16 * u) * azar(s, j + 300), r.top + 6 * u + (r.height - 12 * u) * azar(s, j + 600)))
        drawArc(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), 190f, 60f, false, Offset(r.left + 3 * u, r.top + 2.5f * u),
            Size(minOf(36 * u, r.width * 0.6f), r.height - 5 * u), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), r.topLeft, r.size, radio, style = Stroke(0.8f * u))
        // Las rayas de cuenta, chicas, en el margen de arriba.
        val carbon = (if (oscuro) CALIZA else CARBON).copy(alpha = if (oscuro) 0.3f else 0.38f)
        val x0 = r.right - 19 * u; val ya = r.top + 3 * u; val yb = r.top + 8.2f * u
        if (x0 > r.left + 30 * u) {
            for (j in 0 until 4) drawLine(carbon, Offset(x0 + j * 2.2f * u, ya + azar(s, 60 + j) * 0.6f * u), Offset(x0 + j * 2.2f * u + 0.3f * u, yb), 0.8f * u, StrokeCap.Round)
            drawLine(carbon, Offset(x0 - 1.2f * u, yb - 0.6f * u), Offset(x0 + 7.8f * u, ya + 0.6f * u), 0.8f * u, StrokeCap.Round)
        }
    }
}

/**
 * Roma: el campo es una tablilla de cera (tabula cerata): el marco de haya con sus vetas y, en el rebaje, la cera lisa, color
 * miel de día y ennegrecida de noche. A la izquierda, en el marco, los dos agujeros con el cordón de lino que la une a las
 * otras hojas; a la derecha, dos clavillos de bronce. Con el foco el borde de la cera se marca de púrpura; con un error, de
 * minio. Con varios renglones, las rayas trazadas con el estilo. Sin degradados.
 */
object ComunRomano : DisenoComun {
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

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100D0B), 0.36f) else c
    /** La cera del rebaje, donde va el texto. */
    private fun cera(oscuro: Boolean): Color = if (oscuro) mezcla(CERA_NEGRA, Color(0xFF100D0B), 0.3f) else CERA_MIEL

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), cera(oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(PURPURA, LINO, 0.62f) else PURPURA
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07A64) else mezcla(MINIO, NEGRO, 0.15f)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val cera = cera(oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, cera, 0.45f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(3 * u))
        // El marco de haya.
        drawRoundRect(a(t(HAYA, oscuro)), r.topLeft, r.size, CornerRadius(3 * u))
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(HAYA_VETA, oscuro)).copy(alpha = 0.4f), s, 0.8f) }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.06f else 0.28f), Color.Black.copy(alpha = 0.25f), 1.2f * u)
        // El rebaje con la cera, lisa, y su hondura: sombra arriba, luz abajo.
        val c = Rect(r.left + 7 * u, r.top + 4.5f * u, r.right - 5 * u, r.bottom - 4.5f * u)
        drawRect(cera, c.topLeft, c.size)
        bisel(c, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Color.White.copy(alpha = if (oscuro) 0.05f else 0.35f), 1.3f * u)
        // Las rayas trazadas con el estilo: la cera abierta deja ver el fondo.
        if (e.multilinea) renglonesAntiguedad(c.left + 6 * u, c.right - 6 * u, margenCampoY.toPx(), c.bottom - 3 * u,
            if (oscuro) CERA_RAYA.copy(alpha = 0.22f) else CERA_NEGRA.copy(alpha = 0.16f), 0.8f * u,
            luz = Color.White.copy(alpha = if (oscuro) 0.03f else 0.3f))
        // Los dos agujeros del cordón, en el marco de la izquierda, y el cordón de lino.
        val hx = r.left + 3.4f * u
        val ya = c.top + 6 * u; val yb = c.bottom - 6 * u
        if (yb > ya) {
            drawPath(Path().apply { moveTo(hx, ya); quadraticTo(r.left - 0.6f * u, (ya + yb) / 2, hx, yb) },
                a(t(LINO, oscuro)), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
        listOf(ya, yb).forEach { y ->
            drawCircle(a(t(Color(0xFF3A2814), oscuro)), 1.9f * u, Offset(hx, y))
            drawCircle(a(t(LINO, oscuro)), 0.9f * u, Offset(hx, y))
        }
        // Los clavillos de bronce de la derecha.
        val clavo = if (e.enfocado && e.habilitado) BRONCE_LUZ else BRONCE
        listOf(r.top + 2.4f * u, r.bottom - 2.4f * u).forEach { y ->
            val o = Offset(r.right - 2.5f * u, y)
            drawCircle(a(t(BRONCE_OSC, oscuro)), 1.8f * u, o)
            drawCircle(a(t(clavo, oscuro)), 1.3f * u, o)
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(o.x - 0.4f * u, o.y - 0.4f * u))
        }
        // El borde de la cera: púrpura con el foco, minio con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            e.enfocado && e.habilitado -> rotuloCampo(oscuro)
            else -> null
        }
        if (linea != null) {
            drawRect(linea, Offset(c.left + 0.9f * u, c.top + 0.9f * u), Size(c.width - 1.8f * u, c.height - 1.8f * u), style = Stroke(1.8f * u))
            drawLine(if (oscuro) BRONCE_LUZ.copy(alpha = 0.5f) else HAYA_LUZ, Offset(r.left + 7 * u, r.top + 0.8f * u), Offset(r.right - 6 * u, r.top + 0.8f * u), 0.8f * u)
        }
        // El ensayo del dedo: con el campo vacío, la cera recién alisada brilla un poco más.
        if (e.vacio && !oscuro) drawLine(Color.White.copy(alpha = 0.18f), Offset(c.left + 4 * u, c.top + 3 * u), Offset(c.right - 4 * u, c.top + 3 * u), 1.2f * u)
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * La hoja de hiedra (hedera distinguenda) de las inscripciones, acostada con la punta hacia la derecha, de unos 8 dp de
     * largo con su tallo enroscado, centrada en [c].
     */
    private fun DrawScope.hedera(c: Offset, color: Color, u: Float) {
        val x = c.x; val y = c.y
        val hoja = Path().apply {
            moveTo(x + 4 * u, y)
            cubicTo(x + 1 * u, y - 1 * u, x - 1 * u, y - 4 * u, x - 3 * u, y - 2.6f * u)
            cubicTo(x - 4.4f * u, y - 1.6f * u, x - 3.6f * u, y - 0.3f * u, x - 2.3f * u, y)
            cubicTo(x - 3.6f * u, y + 0.3f * u, x - 4.4f * u, y + 1.6f * u, x - 3 * u, y + 2.6f * u)
            cubicTo(x - 1 * u, y + 4 * u, x + 1 * u, y + 1 * u, x + 4 * u, y)
            close()
        }
        drawPath(hoja, color)
        val tallo = Path().apply { moveTo(x - 2.3f * u, y); quadraticTo(x - 5.5f * u, y - 0.6f * u, x - 6.4f * u, y + 1.6f * u) }
        drawPath(tallo, color, style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    /**
     * Separador: la raya grabada de una inscripción (una línea fina con su luz debajo, como cortada en la piedra) y, al centro,
     * la hoja de hiedra entre dos interpuntos triangulares. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) LINO.copy(alpha = 0.2f) else HAYA_VETA.copy(alpha = 0.45f)).let { it.copy(alpha = it.alpha * f) }
        val luz = Color.White.copy(alpha = (if (oscuro) 0.03f else 0.5f) * f)
        val tinta = (if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA).copy(alpha = (if (oscuro) 0.7f else 0.65f) * f)
        val hueco = 16 * u
        listOf(2 * u to w / 2 - hueco, w / 2 + hueco to w - 2 * u).forEach { (a, b) ->
            if (b - a > 2 * u) {
                drawLine(raya, Offset(a, cy), Offset(b, cy), 1 * u)
                drawLine(luz, Offset(a, cy + 1 * u), Offset(b, cy + 1 * u), 0.6f * u)
            }
        }
        hedera(Offset(w / 2 + 0.8f * u, cy), tinta, u)
        listOf(-1f, 1f).forEach { lado ->
            val x = w / 2 + lado * 10.5f * u
            drawPath(Path().apply { moveTo(x - 1.3f * u, cy - 1.1f * u); lineTo(x + 1.3f * u, cy - 1.1f * u); lineTo(x, cy + 1.3f * u); close() }, tinta)
        }
    }

    /** El texto de los botones: el pórfido de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD2AE5A) else Color(0xFF7C2433)

    /**
     * Botón de texto: una tabula ansata, la cartela de las inscripciones: su contorno es un hilo púrpura casi invisible y lo que
     * se ve son sus dos asas en cola de milano, rellenas, a los lados de la palabra. Al tocarlo la cartela se llena de un
     * púrpura tenue y su contorno se marca; apagado, casi no se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val color = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val ansa = minOf(5 * u, w * 0.1f)
        val izq = 3 * u; val der = w - 3 * u
        val cuerpoIzq = izq + ansa; val cuerpoDer = der - ansa
        val arriba = cy - 13 * u; val abajo = cy + 13 * u
        if (cuerpoDer - cuerpoIzq < 4 * u) return
        val cuerpo = Rect(cuerpoIzq, arriba, cuerpoDer, abajo)
        if (tocado) drawRect(color.copy(alpha = if (oscuro) 0.16f else 0.1f), cuerpo.topLeft, cuerpo.size)
        drawRect(color.copy(alpha = (if (tocado) 0.6f else 0.2f) * apagado), cuerpo.topLeft, cuerpo.size, style = Stroke((if (tocado) 1.1f else 0.8f) * u))
        // Las asas: un trapecio que nace angosto en el lado de la cartela y se abre hacia afuera.
        val asas = Path().apply {
            moveTo(cuerpoDer, cy - 2.5f * u); lineTo(der, cy - 6 * u); lineTo(der, cy + 6 * u); lineTo(cuerpoDer, cy + 2.5f * u); close()
            moveTo(cuerpoIzq, cy - 2.5f * u); lineTo(izq, cy - 6 * u); lineTo(izq, cy + 6 * u); lineTo(cuerpoIzq, cy + 2.5f * u); close()
        }
        drawPath(asas, color.copy(alpha = (if (tocado) 0.85f else 0.5f) * apagado))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro cocido del ánfora del vacío. */
    private val TERRACOTA = Color(0xFFB8643A)
    /** La piedra de la lápida y de la cara de las teselas: un mármol blanco que tira al lino. */
    private val LAPIDA = Color(0xFFF4F1E8)
    /** La piedra honda de noche. */
    private val LAPIDA_NOCHE = Color(0xFF1B1714)
    /** El oro del número de noche. */
    private val ORO = Color(0xFFD2AE5A)

    /** Un interpunto triangular de las inscripciones, con la punta abajo, de lado [l], centrado en [c]. */
    private fun DrawScope.interpunto(c: Offset, l: Float, color: Color) {
        drawPath(Path().apply { moveTo(c.x - l / 2, c.y - l * 0.42f); lineTo(c.x + l / 2, c.y - l * 0.42f); lineTo(c.x, c.y + l * 0.48f); close() }, color)
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: la raya grabada de las inscripciones abajo (una línea fina con su luz debajo, como cortada en la piedra) y, a la
     * izquierda, un interpunto triangular (dos, si la fila lleva detalle). Al tocarla, la piedra se tiñe de púrpura apenas, el
     * interpunto se vuelve de minio y arriba brilla un hilo de bronce. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val purpura = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        if (e.presionado) {
            drawRect(purpura.copy(alpha = if (oscuro) 0.12f else 0.07f), Offset.Zero, size)
            drawLine((if (oscuro) BRONCE_LUZ else BRONCE).copy(alpha = 0.5f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 0.8f * u)
        }
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) {
            drawLine((if (oscuro) LINO.copy(alpha = 0.14f) else HAYA_VETA.copy(alpha = 0.32f)), Offset(x0, h - 1.5f * u), Offset(w, h - 1.5f * u), 0.8f * u)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.03f else 0.45f), Offset(x0, h - 0.6f * u), Offset(w, h - 0.6f * u), 0.6f * u)
        }
        val tinta = if (e.presionado) (if (oscuro) Color(0xFFF07A64) else MINIO) else purpura.copy(alpha = 0.55f)
        if (e.conDetalle) {
            interpunto(Offset(5.5f * u, h / 2 - 3.2f * u), 3 * u, tinta)
            interpunto(Offset(5.5f * u, h / 2 + 3.2f * u), 3 * u, tinta)
        } else interpunto(Offset(5.5f * u, h / 2), 3.2f * u, tinta)
    }

    /** Flecha: la hoja de hiedra de las inscripciones, más grande, con la punta hacia la derecha y su tallo enroscado. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val k = size.minDimension / 20f * 1.45f
        hedera(Offset(size.width * 0.6f, size.height / 2), if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA, k)
    }

    /** Una hoja de laurel con la base en [b], la punta hacia el ángulo [ang] (en radianes), de [largo] y [ancho]. */
    private fun hojaLaurel(b: Offset, ang: Float, largo: Float, ancho: Float): Path {
        val dx = kotlin.math.cos(ang); val dy = kotlin.math.sin(ang)
        val nx = -dy; val ny = dx
        val m = Offset(b.x + dx * largo * 0.5f, b.y + dy * largo * 0.5f)
        return Path().apply {
            moveTo(b.x, b.y)
            quadraticTo(m.x + nx * ancho, m.y + ny * ancho, b.x + dx * largo, b.y + dy * largo)
            quadraticTo(m.x - nx * ancho, m.y - ny * ancho, b.x, b.y)
            close()
        }
    }

    /**
     * Marca de fijar: la corona de laurel, dos ramas que suben en arco y casi se cierran arriba, atadas abajo con la cinta.
     * Fija, las hojas son de oro y la cinta de púrpura; sin fijar, solo el contorno de las hojas, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        val c = Offset(ox + 12 * k, oy + 12.5f * k)
        val r = 8 * k
        val oro = if (oscuro) ORO else mezcla(BRONCE, BRONCE_OSC, 0.45f)
        val contorno = (if (oscuro) LINO else HAYA_VETA).copy(alpha = if (oscuro) 0.5f else 0.6f)
        for (lado in listOf(-1f, 1f)) {
            // La rama: un arco desde abajo hasta casi arriba.
            val rama = Path()
            for (j in 0..10) {
                val a = Math.toRadians(90.0 + lado * (25.0 + j * 13.5)).toFloat()
                val p = Offset(c.x + r * kotlin.math.cos(a), c.y + r * kotlin.math.sin(a))
                if (j == 0) rama.moveTo(p.x, p.y) else rama.lineTo(p.x, p.y)
            }
            drawPath(rama, if (fija) oro else contorno, style = Stroke(0.8f * k))
            for (j in 0 until 5) {
                val a = Math.toRadians(90.0 + lado * (32.0 + j * 27.0)).toFloat()
                val b = Offset(c.x + r * kotlin.math.cos(a), c.y + r * kotlin.math.sin(a))
                // La hoja sigue la rama hacia arriba y se abre un poco hacia afuera.
                val tangente = a + lado * (Math.PI.toFloat() / 2) + lado * 0.5f
                val hoja = hojaLaurel(b, tangente, 4.6f * k, 1.5f * k)
                val hoja2 = hojaLaurel(b, tangente - lado * 1.15f, 3.6f * k, 1.2f * k)
                if (fija) { drawPath(hoja, oro); drawPath(hoja2, oro) }
                else { drawPath(hoja, contorno, style = Stroke(0.7f * k)); drawPath(hoja2, contorno, style = Stroke(0.7f * k)) }
            }
        }
        val cinta = if (fija) (if (oscuro) mezcla(PURPURA, LINO, 0.55f) else PURPURA) else contorno
        val nudo = Offset(c.x, c.y + r + 0.4f * k)
        drawCircle(cinta, 1.2f * k, nudo)
        drawLine(cinta, nudo, Offset(nudo.x - 2.6f * k, nudo.y + 2.6f * k), 0.9f * k, StrokeCap.Round)
        drawLine(cinta, nudo, Offset(nudo.x + 2.6f * k, nudo.y + 2.6f * k), 0.9f * k, StrokeCap.Round)
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, CERA_RAYA, 0.3f) else mezcla(NEGRO, HAYA_VETA, 0.4f)

    /** Una raya grabada: la línea honda y su luz al lado, como cortada en la piedra. */
    private fun DrawScope.grabado(a: Offset, b: Offset, oscuro: Boolean, fuerza: Float) {
        val u = density
        drawLine((if (oscuro) LINO else HAYA_VETA).copy(alpha = (if (oscuro) 0.2f else 0.45f) * fuerza), a, b, 0.9f * u)
        drawLine(Color.White.copy(alpha = (if (oscuro) 0.04f else 0.7f) * fuerza), Offset(a.x + 0.7f * u, a.y + 0.7f * u), Offset(b.x + 0.7f * u, b.y + 0.7f * u), 0.6f * u)
    }

    /**
     * Vacío: una lápida lisa, todavía sin inscribir: la losa clara con el marco doble grabado (una raya y otra más fina por
     * dentro) y un interpunto en cada esquina del marco de dentro. Sin contorno pintado ni sombra que la levante, para que no
     * parezca un botón. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 24 * u) return
        drawRect(if (oscuro) LAPIDA_NOCHE else LAPIDA, Offset.Zero, size)
        val m1 = 5 * u; val m2 = 9 * u
        listOf(m1 to 1f, m2 to 0.6f).forEach { (m, f) ->
            grabado(Offset(m, m), Offset(w - m, m), oscuro, f); grabado(Offset(m, h - m), Offset(w - m, h - m), oscuro, f)
            grabado(Offset(m, m), Offset(m, h - m), oscuro, f); grabado(Offset(w - m, m), Offset(w - m, h - m), oscuro, f)
        }
        val tinta = (if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA).copy(alpha = 0.5f)
        listOf(Offset(m2 + 4 * u, m2 + 4 * u), Offset(w - m2 - 4 * u, m2 + 4 * u), Offset(m2 + 4 * u, h - m2 - 4 * u), Offset(w - m2 - 4 * u, h - m2 - 4 * u))
            .forEach { interpunto(it, 2.6f * u, tinta) }
    }

    /**
     * Lo vacío: un ánfora de barro, de pie en su aro, con la boca abierta y oscura: no queda nada dentro. Lleva sus dos asas,
     * el labio, una luz en el vientre y el rótulo pintado en el hombro (dos rayitas de tinta). Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val barro = t(TERRACOTA, oscuro)
        val borde = if (oscuro) mezcla(TERRACOTA, LINO, 0.35f) else mezcla(TERRACOTA, NEGRO, 0.5f)
        // El aro donde se apoya la punta.
        drawOval(borde, Offset(f(0.38f), f(0.86f)), Size(f(0.24f), f(0.08f)), style = Stroke(1 * u))
        val cuerpo = Path().apply {
            moveTo(f(0.44f), f(0.13f)); lineTo(f(0.44f), f(0.25f))
            cubicTo(f(0.24f), f(0.29f), f(0.24f), f(0.52f), f(0.46f), f(0.84f))
            lineTo(f(0.5f), f(0.93f)); lineTo(f(0.54f), f(0.84f))
            cubicTo(f(0.76f), f(0.52f), f(0.76f), f(0.29f), f(0.56f), f(0.25f))
            lineTo(f(0.56f), f(0.13f)); close()
        }
        drawPath(cuerpo, barro)
        drawPath(cuerpo, borde, style = Stroke(1 * u))
        // La luz en el vientre.
        drawPath(Path().apply { moveTo(f(0.36f), f(0.36f)); quadraticTo(f(0.33f), f(0.5f), f(0.42f), f(0.66f)) },
            Color.White.copy(alpha = if (oscuro) 0.18f else 0.4f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        // Las asas, del cuello al hombro.
        drawPath(Path().apply { moveTo(f(0.44f), f(0.15f)); cubicTo(f(0.3f), f(0.13f), f(0.28f), f(0.24f), f(0.33f), f(0.31f)) }, borde, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(f(0.56f), f(0.15f)); cubicTo(f(0.7f), f(0.13f), f(0.72f), f(0.24f), f(0.67f), f(0.31f)) }, borde, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // El labio y la boca vacía.
        drawRect(borde, Offset(f(0.41f), f(0.08f)), Size(f(0.18f), f(0.05f)))
        drawOval(if (oscuro) Color(0xFF0A0706) else NEGRO.copy(alpha = 0.85f), Offset(f(0.43f), f(0.065f)), Size(f(0.14f), f(0.035f)))
        // El rótulo pintado en el hombro.
        val tinta = (if (oscuro) LINO else NEGRO).copy(alpha = 0.55f)
        drawLine(tinta, Offset(f(0.42f), f(0.35f)), Offset(f(0.58f), f(0.35f)), 0.8f * u)
        drawLine(tinta, Offset(f(0.44f), f(0.39f)), Offset(f(0.55f), f(0.39f)), 0.8f * u)
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO

    /**
     * Aviso: la nota rubricada de los códices: un fondo de cera clara (honda de noche) con la raya grabada arriba y abajo, y a
     * la izquierda la rúbrica de minio, una barra de arriba abajo; junto a ella, a la altura del primer renglón, la marca que
     * abre la nota, según [EstadoAviso.semilla]: la hoja de hiedra, un interpunto o los tres puntos (∴). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(CERA_NEGRA, Color(0xFF100D0B), 0.25f) else mezcla(CERA_MIEL, Color.White, 0.55f), Offset.Zero, size)
        grabado(Offset(0f, 0.6f * u), Offset(w, 0.6f * u), oscuro, 0.8f)
        grabado(Offset(0f, h - 1.6f * u), Offset(w, h - 1.6f * u), oscuro, 0.8f)
        val minio = if (oscuro) Color(0xFFE0705A) else MINIO
        drawRect(minio, Offset(4 * u, 4 * u), Size(3 * u, maxOf(h - 8 * u, 0f)))
        val tinta = if (oscuro) mezcla(PURPURA, LINO, 0.6f) else PURPURA
        val cy = 17 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> hedera(Offset(16.5f * u, cy), tinta, u * 0.85f)
            1 -> interpunto(Offset(15.5f * u, cy), 4 * u, minio)
            else -> {
                drawCircle(tinta, 1.2f * u, Offset(15.5f * u, cy - 2.6f * u))
                drawCircle(tinta, 1.2f * u, Offset(13 * u, cy + 1.8f * u))
                drawCircle(tinta, 1.2f * u, Offset(18 * u, cy + 1.8f * u))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) ORO else Color(0xFF7C2433)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, CERA_RAYA, 0.3f) else mezcla(NEGRO, HAYA_VETA, 0.4f)

    /**
     * Cifra: un emblema de mosaico: la cara lisa de piedra clara donde va el número, rodeada de una orla de teselas negras
     * puestas a mano (cada una un poco corrida), con una de pórfido cada cuatro. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 12 * u || h < 12 * u) return
        drawRect(if (oscuro) LAPIDA_NOCHE else LAPIDA, Offset.Zero, size)
        val lado = 3.2f * u
        val paso = maxOf(4.2f * u, 1f)
        val negra = if (oscuro) mezcla(LINO, LAPIDA_NOCHE, 0.62f) else mezcla(NEGRO, HAYA_VETA, 0.2f)
        val porfido = if (oscuro) mezcla(PURPURA, LINO, 0.35f) else Color(0xFF7C2433)
        var k = 0
        fun tesela(x: Float, y: Float) {
            val c = if (k % 4 == 0) porfido else negra
            drawRect(c, Offset(x + (azar(s, k) - 0.5f) * 0.5f * u, y + (azar(s, k + 400) - 0.5f) * 0.5f * u), Size(lado, lado))
            k++
        }
        val nx = ((w - 2 * u) / paso).toInt().coerceIn(1, 120)
        val ny = ((h - 2 * u) / paso).toInt().coerceIn(1, 80)
        val dx = (w - 2 * u - lado) / maxOf(nx - 1, 1); val dy = (h - 2 * u - lado) / maxOf(ny - 1, 1)
        for (j in 0 until nx) { tesela(1 * u + j * dx, 1 * u); tesela(1 * u + j * dx, h - 1 * u - lado) }
        for (j in 1 until ny - 1) { tesela(1 * u, 1 * u + j * dy); tesela(w - 1 * u - lado, 1 * u + j * dy) }
        // La juntura de cal entre la orla y la cara.
        drawRect(negra.copy(alpha = 0.25f), Offset(5.5f * u, 5.5f * u), Size(w - 11 * u, h - 11 * u), style = Stroke(0.6f * u))
    }
}

/**
 * Grecia: el campo es una losa de mármol con su filete pintado de azul del Egeo, que en cada esquina se vuelve una llave de la
 * greca (el meandro). La cara del medio queda lisa, con apenas unas vetas. Con el foco el filete se vuelve de oro, más grueso,
 * y aparece un segundo filete fino por dentro; con un error, se pinta de rojo de barniz. Con varios renglones, rayas grabadas.
 * Sin degradados.
 */
object ComunHeleno : DisenoComun {
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

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0C0A), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 11.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(MARMOL, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.6f) else EGEO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO_VASO

    /** Una llave de la greca en la esquina [o], de lado [k], que se mete hacia [sx], [sy] (±1): una espiral cuadrada. */
    private fun DrawScope.llave(o: Offset, k: Float, sx: Float, sy: Float, color: Color, grosor: Float) {
        val pts = listOf(0f to 1f, 0f to 0f, 1f to 0f, 1f to 1f, 0.34f to 1f, 0.34f to 0.34f, 0.67f to 0.34f, 0.67f to 0.67f)
        val p = Path()
        pts.forEachIndexed { i, (x, y) ->
            val px = o.x + sx * x * k; val py = o.y + sy * y * k
            if (i == 0) p.moveTo(px, py) else p.lineTo(px, py)
        }
        drawPath(p, color, style = Stroke(grosor))
    }

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val marmol = hondo(MARMOL, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, marmol, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.2f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size)
        drawRect(marmol, r.topLeft, r.size)
        // Unas vetas apenas, que cruzan la losa.
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (j in 0 until 3) {
                val x = r.left + r.width * azar(s, 41 + j)
                drawLine(hondo(MARMOL_OSC, oscuro).copy(alpha = 0.45f), Offset(x, r.top), Offset(x + (30 + 40 * azar(s, 44 + j)) * u, r.bottom), 0.6f * u)
            }
        }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.06f else 0.55f), Color.Black.copy(alpha = if (oscuro) 0.3f else 0.16f), 1.5f * u)
        if (e.multilinea) renglonesAntiguedad(r.left + 12 * u, r.right - 12 * u, margenCampoY.toPx(), r.bottom - 9 * u,
            (if (oscuro) Color.Black.copy(alpha = 0.35f) else MARMOL_SOMBRA.copy(alpha = 0.3f)), 0.7f * u,
            luz = Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f))
        // El filete con sus llaves en las esquinas.
        val encendido = e.enfocado && e.habilitado
        val color = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) LLAMA else ORO
            else -> a(if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.25f).copy(alpha = 0.75f) else EGEO)
        }
        val g = (if (encendido || e.error) 1.8f else 1.2f) * u
        val m = 4 * u
        val k = 8 * u
        val f = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        if (f.width > 2 * k + 2 * u) {
            drawLine(color, Offset(f.left + k, f.top), Offset(f.right - k, f.top), g)
            drawLine(color, Offset(f.left + k, f.bottom), Offset(f.right - k, f.bottom), g)
        }
        if (f.height > 2 * k + 2 * u) {
            drawLine(color, Offset(f.left, f.top + k), Offset(f.left, f.bottom - k), g)
            drawLine(color, Offset(f.right, f.top + k), Offset(f.right, f.bottom - k), g)
        }
        val gl = (if (encendido || e.error) 1.4f else 1.1f) * u
        llave(Offset(f.left, f.top), k, 1f, 1f, color, gl)
        llave(Offset(f.right, f.top), k, -1f, 1f, color, gl)
        llave(Offset(f.left, f.bottom), k, 1f, -1f, color, gl)
        llave(Offset(f.right, f.bottom), k, -1f, -1f, color, gl)
        // Con el foco, un filete fino por dentro.
        if (encendido || e.error) {
            val i = Rect(f.left + k + 2 * u, f.top + 2.6f * u, f.right - k - 2 * u, f.bottom - 2.6f * u)
            if (i.width > 0f && i.height > 0f) {
                drawLine(color.copy(alpha = 0.6f), Offset(i.left, i.top), Offset(i.right, i.top), 0.6f * u)
                drawLine(color.copy(alpha = 0.6f), Offset(i.left, i.bottom), Offset(i.right, i.bottom), 0.6f * u)
            }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: el filete azul del Egeo a los dos lados y, al centro, un tramo corto de greca (cuatro vueltas del meandro, de
     * 5 dp de alto, en un solo trazo). Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val azul = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO
        val filete = azul.copy(alpha = (if (oscuro) 0.42f else 0.5f) * f)
        val greca = azul.copy(alpha = (if (oscuro) 0.6f else 0.7f) * f)
        val k = 5 * u
        val vueltas = 4
        val x0 = w / 2 - vueltas * k / 2
        val arriba = cy - k / 2
        // Cada vuelta: sube, cruza arriba, baja a media altura, vuelve un poco y baja a la base, que sigue a la vuelta siguiente.
        val p = Path().apply {
            moveTo(x0 - 3 * u, arriba + k)
            lineTo(x0, arriba + k)
            for (j in 0 until vueltas) {
                val x = x0 + j * k
                lineTo(x, arriba); lineTo(x + 0.66f * k, arriba); lineTo(x + 0.66f * k, arriba + 0.62f * k)
                lineTo(x + 0.33f * k, arriba + 0.62f * k); lineTo(x + 0.33f * k, arriba + k); lineTo(x + k, arriba + k)
            }
            lineTo(x0 + vueltas * k + 3 * u, arriba + k)
        }
        drawPath(p, greca, style = Stroke(1 * u))
        val hueco = vueltas * k / 2 + 6 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(filete, Offset(2 * u, cy), Offset(w / 2 - hueco, cy), 1 * u)
            drawLine(filete, Offset(w / 2 + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
            drawCircle(greca, 1 * u, Offset(w / 2 - hueco + 3 * u, cy))
            drawCircle(greca, 1 * u, Offset(w / 2 + hueco - 3 * u, cy))
        }
    }

    /** El texto de los botones: el violeta del tema de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFD8B45C) else Color(0xFF5A2A82)

    /**
     * Botón de texto: un filete azul bajo la palabra que en cada punta se enrosca hacia arriba en una llave de la greca, como
     * las esquinas del campo. Al tocarlo el filete se vuelve de oro y el mármol de detrás se lava de azul muy tenue; apagado,
     * apenas se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val azul = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO
        if (tocado) drawRoundRect(azul.copy(alpha = if (oscuro) 0.14f else 0.08f), Offset(4 * u, cy - 15 * u), Size(maxOf(w - 8 * u, 0f), 30 * u), CornerRadius(2 * u))
        val color = if (tocado) (if (oscuro) LLAMA else ORO) else azul.copy(alpha = 0.6f * apagado)
        val k = 4.5f * u
        val x0 = minOf(10 * u, w * 0.2f); val x1 = w - x0
        val y = cy + 11.5f * u
        if (x1 - x0 < 2 * k + 2 * u) return
        val g = (if (tocado) 1.3f else 1f) * u
        drawLine(color, Offset(x0 + k, y), Offset(x1 - k, y), g)
        llave(Offset(x0, y), k, 1f, -1f, color, g)
        llave(Offset(x1, y), k, -1f, -1f, color, g)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro de los vasos áticos, anaranjado, el de la copa y el del óstracon. */
    private val ARCILLA = Color(0xFFD27A4A)

    /** El Egeo con la luz del momento: de noche, aclarado para que se vea. */
    private fun egeo(oscuro: Boolean): Color = if (oscuro) mezcla(EGEO, MARMOL_LUZ, 0.35f) else EGEO

    override val margenFila: Dp = 12.dp

    /**
     * Fila: el filete azul del Egeo, fino, abajo, y a la izquierda una llave pequeña de la greca (con detalle, dos, una sobre
     * otra en espejo). Al tocarla el mármol se lava de azul muy tenue y la llave se vuelve de oro. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val azul = egeo(oscuro)
        if (e.presionado) drawRect(azul.copy(alpha = if (oscuro) 0.12f else 0.07f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine(azul.copy(alpha = if (oscuro) 0.26f else 0.28f), Offset(x0, h - 1 * u), Offset(w - 4 * u, h - 1 * u), 0.8f * u)
        val color = if (e.presionado) (if (oscuro) LLAMA else ORO) else azul.copy(alpha = 0.55f)
        val k = 4.4f * u
        if (e.conDetalle) {
            llave(Offset(3.4f * u, h / 2 - 1 * u), k, 1f, -1f, color, 0.9f * u)
            llave(Offset(3.4f * u, h / 2 + 1 * u), k, 1f, 1f, color, 0.9f * u)
        } else llave(Offset(3.4f * u, h / 2 - k / 2), k, 1f, 1f, color, 0.9f * u)
    }

    /**
     * Flecha: una flecha de las de los vasos de figuras negras: el astil fino con sus dos plumas atrás y la punta de aletas,
     * negra de barniz (de noche, clara). Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val color = if (oscuro) MARMOL_LUZ.copy(alpha = 0.8f) else BARNIZ.copy(alpha = 0.82f)
        drawLine(color, Offset(w * 0.1f, cy), Offset(w * 0.6f, cy), 1 * u, StrokeCap.Round)
        drawPath(Path().apply {
            moveTo(w * 0.94f, cy); lineTo(w * 0.56f, cy - h * 0.24f); lineTo(w * 0.64f, cy); lineTo(w * 0.56f, cy + h * 0.24f); close()
        }, color)
        val pluma = egeo(oscuro)
        for (j in 0..1) {
            val x = w * (0.1f + j * 0.1f)
            drawLine(pluma, Offset(x, cy), Offset(x - w * 0.08f, cy - h * 0.16f), 0.9f * u, StrokeCap.Round)
            drawLine(pluma, Offset(x, cy), Offset(x - w * 0.08f, cy + h * 0.16f), 0.9f * u, StrokeCap.Round)
        }
    }

    /**
     * Marca de fijar: el sol de Vergina, la estrella de dieciséis rayos de las tumbas macedonias, con su disco en medio. Fija,
     * de oro; sin fijar, solo su contorno en el azul del Egeo, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val sol = estrella(c, 10 * k, 16, 0.42f)
        if (fija) {
            val oro = if (oscuro) LLAMA else ORO
            drawPath(sol, oro)
            drawCircle(if (oscuro) mezcla(LLAMA, BARNIZ, 0.35f) else mezcla(ORO, BARNIZ, 0.35f), 2.6f * k, c)
        } else {
            val azul = egeo(oscuro).copy(alpha = if (oscuro) 0.8f else 0.5f)
            drawPath(sol, azul, style = Stroke(0.8f * u))
            drawCircle(azul, 2.6f * k, c, style = Stroke(0.8f * u))
        }
    }

    override val tamDibujoVacio: Dp = 44.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL_LUZ, EGEO, 0.22f) else Color(0xFF4E4760)

    /** El mármol de los recuadros (vacío y aviso): claro de día; de noche, en penumbra. */
    private fun losa(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL, Color(0xFF0F0C0A), 0.84f) else MARMOL

    /**
     * Vacío: una metopa sin relieve: la losa lisa de mármol, coronada por la fila de dentículos de la cornisa (los dientes
     * cuadrados de piedra) sobre su filete, y abajo un filete doble; los lados quedan abiertos, sin marco, para que no parezca
     * un botón. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 16 * u || h < 16 * u) return
        drawRect(losa(oscuro), Offset.Zero, size)
        val diente = if (oscuro) Color.Black.copy(alpha = 0.45f) else MARMOL_SOMBRA.copy(alpha = 0.5f)
        val paso = maxOf(6 * u, 1f)
        var x = 4 * u; var k = 0
        while (x + 3.4f * u < w - 3 * u && k < 200) {
            drawRect(diente, Offset(x, 2 * u), Size(3.4f * u, 3.6f * u))
            x += paso; k++
        }
        val azul = egeo(oscuro).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawLine(azul, Offset(3 * u, 7.2f * u), Offset(w - 3 * u, 7.2f * u), 0.9f * u)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f), Offset(3 * u, 8.2f * u), Offset(w - 3 * u, 8.2f * u), 0.6f * u)
        drawLine(azul, Offset(3 * u, h - 6 * u), Offset(w - 3 * u, h - 6 * u), 0.9f * u)
        drawLine(azul.copy(alpha = azul.alpha * 0.6f), Offset(3 * u, h - 3.6f * u), Offset(w - 3 * u, h - 3.6f * u), 0.6f * u)
    }

    /**
     * Lo vacío: una cílica, la copa baja de beber, vista un poco desde arriba: el tondo del fondo (el medallón que se pintaba)
     * quedó sin figura. De día, de figuras negras (la copa negra de barniz y el tondo de barro); de noche, de figuras rojas (la
     * copa de barro y el fondo negro). Lleva sus dos asas, el pie y el filete de barro reservado. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val cuerpo = if (oscuro) t(ARCILLA, true) else BARNIZ
        val fondo = if (oscuro) Color(0xFF0B0908) else ARCILLA
        val linea = if (oscuro) mezcla(ARCILLA, MARMOL_LUZ, 0.3f) else ARCILLA
        // Las asas, que suben a los lados.
        drawArc(cuerpo, 180f, 180f, false, Offset(f(0.02f), f(0.26f)), Size(f(0.2f), f(0.18f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawArc(cuerpo, 180f, 180f, false, Offset(f(0.78f), f(0.26f)), Size(f(0.2f), f(0.18f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        // La copa: media elipse abajo, la boca arriba.
        drawArc(cuerpo, 0f, 180f, true, Offset(f(0.1f), f(0.14f)), Size(f(0.8f), f(0.48f)))
        drawOval(cuerpo, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)))
        drawOval(fondo, Offset(f(0.14f), f(0.32f)), Size(f(0.72f), f(0.12f)))
        // El tondo vacío, con su círculo de meandro sugerido por un anillo.
        drawOval(if (oscuro) linea.copy(alpha = 0.7f) else BARNIZ.copy(alpha = 0.8f), Offset(f(0.34f), f(0.345f)), Size(f(0.32f), f(0.07f)), style = Stroke(0.8f * u))
        // El filete reservado en el vientre.
        drawArc(linea, 20f, 140f, false, Offset(f(0.16f), f(0.2f)), Size(f(0.68f), f(0.36f)), style = Stroke(0.8f * u))
        // El tallo y el pie.
        drawRect(cuerpo, Offset(f(0.46f), f(0.6f)), Size(f(0.08f), f(0.2f)))
        drawOval(cuerpo, Offset(f(0.3f), f(0.78f)), Size(f(0.4f), f(0.1f)))
        drawArc(linea, 180f, 180f, false, Offset(f(0.31f), f(0.785f)), Size(f(0.38f), f(0.08f)), style = Stroke(0.7f * u))
        if (oscuro) drawOval(linea.copy(alpha = 0.6f), Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)), style = Stroke(0.7f * u))
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) MARMOL_LUZ else BARNIZ

    /**
     * Aviso: una losa de mármol con una columna a la izquierda, de lo alto del aviso: la basa, el fuste con sus estrías y, según
     * [EstadoAviso.semilla], el capitel jónico (dos volutas) o el dórico (el equino y el ábaco), dibujada a línea en el azul
     * del Egeo. Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(losa(oscuro), Offset.Zero, size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.6f), Offset(0f, 0.5f * u), Offset(w, 0.5f * u), 1 * u)
        val azul = egeo(oscuro)
        val fuste = if (oscuro) mezcla(MARMOL_OSC, Color(0xFF0F0C0A), 0.65f) else MARMOL_LUZ
        val cx = 10 * u
        val arriba = 4 * u; val abajo = h - 4 * u
        if (abajo - arriba < 16 * u) return
        val capitel = arriba + 5 * u
        val basa = abajo - 3 * u
        // El fuste, con su éntasis apenas, y las estrías.
        drawRect(fuste, Offset(cx - 3.2f * u, capitel), Size(6.4f * u, basa - capitel))
        listOf(-3.2f, 3.2f).forEach { d -> drawLine(azul, Offset(cx + d * u, capitel), Offset(cx + d * u, basa), 0.9f * u) }
        listOf(-1.1f, 1.1f).forEach { d -> drawLine(azul.copy(alpha = 0.45f), Offset(cx + d * u, capitel + 1.5f * u), Offset(cx + d * u, basa - 1.5f * u), 0.6f * u) }
        // La basa: dos toros.
        drawRect(fuste, Offset(cx - 5 * u, basa), Size(10 * u, 3 * u))
        drawRect(azul, Offset(cx - 5 * u, basa), Size(10 * u, 3 * u), style = Stroke(0.8f * u))
        drawLine(azul, Offset(cx - 5 * u, basa + 1.5f * u), Offset(cx + 5 * u, basa + 1.5f * u), 0.6f * u)
        if (e.semilla % 2 == 0) {
            // Jónico: el ábaco fino y las dos volutas.
            drawRect(fuste, Offset(cx - 6 * u, arriba), Size(12 * u, 2.4f * u))
            drawRect(azul, Offset(cx - 6 * u, arriba), Size(12 * u, 2.4f * u), style = Stroke(0.8f * u))
            listOf(-1f, 1f).forEach { l ->
                val c = Offset(cx + l * 4.4f * u, arriba + 4.4f * u)
                drawCircle(fuste, 2.1f * u, c)
                drawCircle(azul, 2.1f * u, c, style = Stroke(0.8f * u))
                drawCircle(azul, 0.8f * u, c)
            }
        } else {
            // Dórico: el equino que se abre y el ábaco cuadrado.
            val equino = Path().apply {
                moveTo(cx - 3.2f * u, capitel); lineTo(cx - 5.4f * u, arriba + 2.6f * u); lineTo(cx + 5.4f * u, arriba + 2.6f * u); lineTo(cx + 3.2f * u, capitel); close()
            }
            drawPath(equino, fuste); drawPath(equino, azul, style = Stroke(0.8f * u))
            drawRect(fuste, Offset(cx - 6 * u, arriba), Size(12 * u, 2.6f * u))
            drawRect(azul, Offset(cx - 6 * u, arriba), Size(12 * u, 2.6f * u), style = Stroke(0.8f * u))
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) LLAMA else BARNIZ
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(MARMOL_LUZ, ARCILLA, 0.3f) else mezcla(BARNIZ, ARCILLA, 0.4f)

    /**
     * Cifra: un óstracon, el tiesto de vasija roto donde se escribía el voto: la cara de barro por dentro, de bordes de
     * fractura en ángulo (cada esquina rota a su manera) y, arriba, la franja de barniz negro que quedó del vaso. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 16 * u || h < 16 * u) return
        fun j(k: Int) = (1 + 3 * azar(s, k)) * u
        val tiesto = Path().apply {
            moveTo(j(0), j(1) + 2 * u)
            lineTo(w * 0.42f, j(2)); lineTo(w - j(3) - 3 * u, j(4)); lineTo(w - j(5), h * 0.38f)
            lineTo(w - j(6) - 1 * u, h - j(7)); lineTo(w * 0.55f, h - j(8) * 0.6f); lineTo(j(9) + 2 * u, h - j(10))
            lineTo(j(11) * 0.6f, h * 0.55f); close()
        }
        translate(0.6f * u, 1.4f * u) { drawPath(tiesto, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.16f)) }
        val barro = if (oscuro) mezcla(ARCILLA, Color(0xFF0F0C0A), 0.8f) else mezcla(ARCILLA, Color.White, 0.5f)
        drawPath(tiesto, barro)
        clipPath(tiesto) {
            drawRect(if (oscuro) Color(0xFF060504) else BARNIZ, Offset.Zero, Size(w, 5.5f * u))
            drawLine(if (oscuro) mezcla(ARCILLA, Color.Black, 0.5f) else ARCILLA, Offset(0f, 6.4f * u), Offset(w, 6.4f * u), 0.7f * u)
        }
        drawPath(tiesto, (if (oscuro) ARCILLA else mezcla(ARCILLA, BARNIZ, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.8f * u))
    }
}

/**
 * Egipto: el campo es la tablilla del escriba, de madera cubierta de yeso blanco, liso, con la banda de rectángulos de colores
 * (azul de lapislázuli, rojo y verde) pintada arriba y abajo entre dos rayas negras, como el borde de las pinturas de las
 * tumbas. Con el foco la banda toma todo su color y el borde del yeso se marca de lapislázuli; con un error, de rojo. Con
 * varios renglones, las líneas rojas de guía, apenas visibles. Sin degradados.
 */
object ComunEgipto : DisenoComun {
    private val MADERA = Color(0xFF8E6A3E)
    private val MADERA_VETA = Color(0xFF5E4424)
    private val YESO = Color(0xFFF2EBDA)
    private val LAPIS = Color(0xFF1D3A8A)
    private val ROJO = Color(0xFFB0432A)
    private val VERDE = Color(0xFF3E7A4A)
    private val AMARILLO = Color(0xFFF2CF4A)
    private val NEGRO = Color(0xFF1E160C)
    private val LINO = Color(0xFFE6D8B4)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110D08), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 14.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(YESO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFF9DB0EE) else LAPIS
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A70) else ROJO

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val yeso = hondo(YESO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, yeso, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(5 * u))
        // La tabla de madera y el yeso encima.
        drawRoundRect(a(t(MADERA, oscuro)), r.topLeft, r.size, CornerRadius(5 * u))
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(MADERA_VETA, oscuro)).copy(alpha = 0.35f), s, 0.6f) }
        val y = Rect(r.left + 3 * u, r.top + 3 * u, r.right - 3 * u, r.bottom - 3 * u)
        drawRoundRect(yeso, y.topLeft, y.size, CornerRadius(3 * u))
        // Las líneas rojas de guía, con varios renglones.
        val guia = (if (oscuro) mezcla(ROJO, AMARILLO, 0.3f) else ROJO).copy(alpha = if (oscuro) 0.2f else 0.15f)
        if (e.multilinea) renglonesAntiguedad(y.left + 8 * u, y.right - 8 * u, margenCampoY.toPx(), y.bottom - 8 * u, guia, 0.7f * u)
        // La banda de rectángulos de colores, arriba y abajo, entre rayas negras.
        val encendido = e.enfocado && e.habilitado
        val viveza = if (encendido) 1f else 0.6f
        val colores = listOf(LAPIS, ROJO, VERDE)
        val raya = a(t(NEGRO, oscuro)).copy(alpha = if (oscuro) 0.7f else 0.55f)
        val paso = maxOf(7 * u, 1f)
        listOf(y.top + 2 * u, y.bottom - 4.6f * u).forEach { yb ->
            drawLine(raya, Offset(y.left + 3 * u, yb - 0.7f * u), Offset(y.right - 3 * u, yb - 0.7f * u), 0.6f * u)
            drawLine(raya, Offset(y.left + 3 * u, yb + 3.3f * u), Offset(y.right - 3 * u, yb + 3.3f * u), 0.6f * u)
            var x = y.left + 4 * u; var k = 0
            while (x + 5 * u < y.right - 3 * u && k < 300) {
                val col = colores[k % 3].let { if (oscuro) mezcla(it, Color.White, 0.18f) else it }
                drawRect(a(col).copy(alpha = viveza), Offset(x, yb), Size(5 * u, 2.6f * u))
                x += paso; k++
            }
        }
        // El borde del yeso: lapislázuli con el foco, rojo con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> rotuloCampo(oscuro)
            else -> a(t(MADERA_VETA, oscuro)).copy(alpha = 0.45f)
        }
        val g = (if (e.error || encendido) 1.8f else 0.8f) * u
        drawRoundRect(linea, Offset(y.left + g / 2, y.top + g / 2), Size(y.width - g, y.height - g), CornerRadius(3 * u), style = Stroke(g))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 10.dp

    /**
     * Separador: una raya negra fina, como las que encierran las bandas de las tumbas, y al centro un tramo corto de la banda de
     * rectángulos de colores (lapislázuli, rojo, verde, rojo, lapislázuli) entre dos rayas más finas. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) LINO.copy(alpha = 0.22f) else NEGRO.copy(alpha = 0.38f)).let { it.copy(alpha = it.alpha * f) }
        val colores = listOf(LAPIS, ROJO, VERDE, ROJO, LAPIS)
        val paso = 6 * u
        val ancho = colores.size * paso - 1.6f * u
        val x0 = w / 2 - ancho / 2
        colores.forEachIndexed { j, c ->
            val col = if (oscuro) mezcla(c, Color.White, 0.22f) else c
            drawRect(col.copy(alpha = (if (oscuro) 0.75f else 0.8f) * f), Offset(x0 + j * paso, cy - 1.3f * u), Size(4.4f * u, 2.6f * u))
        }
        drawLine(raya, Offset(x0 - 2 * u, cy - 2.6f * u), Offset(x0 + ancho + 2 * u, cy - 2.6f * u), 0.6f * u)
        drawLine(raya, Offset(x0 - 2 * u, cy + 2.6f * u), Offset(x0 + ancho + 2 * u, cy + 2.6f * u), 0.6f * u)
        val hueco = ancho / 2 + 6 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(2 * u, cy), Offset(w / 2 - hueco, cy), 1 * u)
            drawLine(raya, Offset(w / 2 + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
        }
    }

    /** El texto de los botones: el lapislázuli del tema de día, el oro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3B341) else Color(0xFF1D2F7A)

    /**
     * Botón de texto: un cartucho, el óvalo alargado que encierra los nombres reales, sugerido solo por sus dos extremos
     * curvos, como un paréntesis a cada lado de la palabra, y la barra de la cuerda atada a la derecha. Al tocarlo el cartucho
     * se cierra, se llena de oro tenue y se marca; apagado, casi no se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val lapis = if (oscuro) Color(0xFF9DB0EE) else LAPIS
        val alto = 26 * u
        val izq = 3 * u; val der = w - 6 * u
        if (der - izq < alto * 0.6f) return
        val radio = CornerRadius(minOf(alto, der - izq) / 2)
        val o = Offset(izq, cy - alto / 2); val tam = Size(der - izq, alto)
        val color = lapis.copy(alpha = (if (tocado) 0.8f else 0.5f) * apagado)
        val g = (if (tocado) 1.2f else 1f) * u
        if (tocado) {
            drawRoundRect(AMARILLO.copy(alpha = if (oscuro) 0.16f else 0.24f), o, tam, radio)
            drawRoundRect(color, o, tam, radio, style = Stroke(g))
        } else {
            val d = minOf(alto, der - izq)
            drawArc(color, 115f, 130f, false, Offset(izq, cy - alto / 2), Size(d, alto), style = Stroke(g, cap = StrokeCap.Round))
            drawArc(color, -65f, 130f, false, Offset(der - d, cy - alto / 2), Size(d, alto), style = Stroke(g, cap = StrokeCap.Round))
        }
        drawLine(color, Offset(der + 2.2f * u, cy - 9 * u), Offset(der + 2.2f * u, cy + 9 * u), (if (tocado) 1.6f else 1.3f) * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papiro de la hoja del vacío. */
    private val PAPIRO = Color(0xFFF0E4C0)
    /** La fayenza, la loza vidriada de color turquesa. */
    private val FAYENZA = Color(0xFF2E8C8A)
    /** El lapislázuli y el verde aclarados para la noche. */
    private val LAPIS_NOCHE = Color(0xFF9DB0EE)
    private val VERDE_NOCHE = Color(0xFF8CC79A)

    override val margenFila: Dp = 12.dp

    /**
     * Fila: abajo, la raya negra fina que encierra las bandas de las tumbas, y a la izquierda una columnita de tres rectángulos
     * de color (lapislázuli, rojo y verde) de la banda. Al tocarla el yeso se calienta de amarillo y los rectángulos toman todo
     * su color. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(AMARILLO.copy(alpha = if (oscuro) 0.08f else 0.16f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine((if (oscuro) LINO.copy(alpha = 0.16f) else NEGRO.copy(alpha = 0.26f)), Offset(x0, h - 1 * u), Offset(w, h - 1 * u), 0.7f * u)
        val viveza = if (e.presionado) 1f else 0.6f
        val colores = listOf(LAPIS, ROJO, VERDE)
        val alto = if (e.conDetalle) 3.6f * u else 2.6f * u
        val y0 = h / 2 - (3 * alto + 2 * u) / 2
        colores.forEachIndexed { j, c ->
            val col = if (oscuro) mezcla(c, Color.White, 0.25f) else c
            drawRect(col.copy(alpha = viveza), Offset(3.6f * u, y0 + j * (alto + 1 * u)), Size(3.4f * u, alto))
        }
    }

    /**
     * Flecha: un capullo de loto acostado, con la punta a la derecha: el tallo verde, los dos sépalos que lo abrazan y el
     * capullo de lapislázuli. Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val verde = if (oscuro) VERDE_NOCHE else VERDE
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        drawLine(verde, Offset(w * 0.06f, cy + h * 0.04f), Offset(w * 0.46f, cy), 1.2f * u, StrokeCap.Round)
        val capullo = Path().apply {
            moveTo(w * 0.42f, cy)
            cubicTo(w * 0.5f, cy - h * 0.28f, w * 0.78f, cy - h * 0.16f, w * 0.94f, cy)
            cubicTo(w * 0.78f, cy + h * 0.16f, w * 0.5f, cy + h * 0.28f, w * 0.42f, cy)
            close()
        }
        drawPath(capullo, lapis)
        listOf(-1f, 1f).forEach { l ->
            drawPath(Path().apply {
                moveTo(w * 0.4f, cy); quadraticTo(w * 0.5f, cy + l * h * 0.3f, w * 0.72f, cy + l * h * 0.14f); quadraticTo(w * 0.56f, cy + l * h * 0.1f, w * 0.4f, cy); close()
            }, verde)
        }
    }

    /**
     * Marca de fijar: un escarabajo sagrado visto desde arriba: la cabeza con su escudo, el coselete, los élitros partidos por la
     * raya del medio y las seis patas. Fijo, de lapislázuli con el contorno de oro; sin fijar, solo su contorno, tenue. Sin
     * degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val ox = (size.width - 24 * k) / 2; val oy = (size.height - 24 * k) / 2
        fun p(x: Float, y: Float) = Offset(ox + x * k, oy + y * k)
        val lapis = if (oscuro) LAPIS_NOCHE else LAPIS
        val oro = if (oscuro) AMARILLO else mezcla(AMARILLO, MADERA_VETA, 0.45f)
        val linea = if (fija) oro else (if (oscuro) LINO else NEGRO).copy(alpha = 0.45f)
        val g = (if (fija) 0.8f else 0.75f) * u
        // Las patas, de a tres por lado.
        listOf(floatArrayOf(8f, 10.5f, 4f, 8.5f), floatArrayOf(7.4f, 14f, 3.5f, 15f), floatArrayOf(8f, 17.5f, 5f, 21f)).forEach { a ->
            drawLine(linea, p(a[0], a[1]), p(a[2], a[3]), g * 1.2f, StrokeCap.Round)
            drawLine(linea, p(24 - a[0], a[1]), p(24 - a[2], a[3]), g * 1.2f, StrokeCap.Round)
        }
        val cabeza = Rect(p(9.4f, 3.2f), p(14.6f, 7.6f))
        val coselete = Rect(p(7.4f, 6.6f), p(16.6f, 11.4f))
        val elitros = Rect(p(7f, 10.6f), p(17f, 21.4f))
        if (fija) {
            drawOval(lapis, cabeza.topLeft, cabeza.size); drawOval(lapis, coselete.topLeft, coselete.size); drawOval(lapis, elitros.topLeft, elitros.size)
        }
        drawOval(linea, cabeza.topLeft, cabeza.size, style = Stroke(g))
        drawOval(linea, coselete.topLeft, coselete.size, style = Stroke(g))
        drawOval(linea, elitros.topLeft, elitros.size, style = Stroke(g))
        drawLine(linea, p(12f, 11.4f), p(12f, 21f), g)
    }

    override val tamDibujoVacio: Dp = 40.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, Color(0xFFB9A887), 0.4f) else Color(0xFF55452A)

    /** El papiro con la luz del momento: de noche, en penumbra. */
    private fun papiro(oscuro: Boolean): Color = if (oscuro) mezcla(PAPIRO, Color(0xFF110D08), 0.84f) else PAPIRO

    /**
     * Vacío: una hoja de papiro sin escribir, con sus fibras cruzadas (las de la cara, a lo largo, y las de atrás, de pie, más
     * tenues) y en cada punta el borde enrollado del rollo, con su luz. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 12 * u) return
        val rollo = 6 * u
        drawRect(papiro(oscuro), Offset(rollo, 0f), Size(w - 2 * rollo, h))
        val fibra = if (oscuro) LINO.copy(alpha = 0.06f) else MADERA.copy(alpha = 0.1f)
        val pasoY = maxOf(3.6f * u, 1f)
        var y = 2 * u; var k = 0
        while (y < h - 1 * u && k < 120) {
            drawLine(fibra, Offset(rollo, y), Offset(w - rollo, y + (azar(s, k) - 0.5f) * 0.8f * u), (0.5f + 0.5f * azar(s, k + 50)) * u)
            y += pasoY; k++
        }
        val pasoX = maxOf(8 * u, 1f)
        var x = rollo + 4 * u; k = 0
        while (x < w - rollo && k < 120) {
            drawLine(fibra.copy(alpha = fibra.alpha * 0.55f), Offset(x, 0f), Offset(x, h), 0.6f * u)
            x += pasoX; k++
        }
        // Los bordes enrollados.
        val enrollado = if (oscuro) mezcla(PAPIRO, Color(0xFF110D08), 0.7f) else mezcla(PAPIRO, MADERA, 0.3f)
        listOf(0f, w - rollo).forEach { x0 ->
            drawRoundRect(enrollado, Offset(x0, 0f), Size(rollo, h), CornerRadius(rollo / 2))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), Offset(x0 + rollo * 0.35f, 2 * u), Offset(x0 + rollo * 0.35f, h - 2 * u), 1 * u)
            drawLine(MADERA_VETA.copy(alpha = if (oscuro) 0.5f else 0.35f), Offset(x0 + rollo * 0.8f, 1 * u), Offset(x0 + rollo * 0.8f, h - 1 * u), 0.7f * u)
        }
    }

    /**
     * Lo vacío: un naos, la capilla de madera donde se guardaba la imagen del dios, con las dos puertas abiertas y nada
     * dentro: el hueco oscuro. Lleva la cornisa de gola con su banda de colores, el disco solar al centro y el zócalo. Sin
     * degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val madera = t(if (oscuro) mezcla(MADERA, LINO, 0.25f) else MADERA, oscuro)
        val borde = if (oscuro) mezcla(MADERA, LINO, 0.5f) else MADERA_VETA
        val negro = if (oscuro) Color(0xFF080604) else NEGRO
        // El zócalo y la caja.
        drawRect(borde, Offset(f(0.18f), f(0.86f)), Size(f(0.64f), f(0.07f)))
        drawRect(madera, Offset(f(0.26f), f(0.32f)), Size(f(0.48f), f(0.54f)))
        drawRect(negro, Offset(f(0.33f), f(0.4f)), Size(f(0.34f), f(0.46f)))
        drawRect(borde, Offset(f(0.26f), f(0.32f)), Size(f(0.48f), f(0.54f)), style = Stroke(0.9f * u))
        // Las puertas abiertas, giradas hacia fuera.
        listOf(-1f, 1f).forEach { l ->
            val xb = f(0.5f) + l * f(0.17f); val xa = f(0.5f) + l * f(0.38f)
            val puerta = Path().apply { moveTo(xb, f(0.4f)); lineTo(xa, f(0.45f)); lineTo(xa, f(0.81f)); lineTo(xb, f(0.86f)); close() }
            drawPath(puerta, madera)
            drawPath(puerta, borde, style = Stroke(0.9f * u))
            drawLine(borde.copy(alpha = 0.6f), Offset((xa + xb) / 2, f(0.5f)), Offset((xa + xb) / 2, f(0.78f)), 0.6f * u)
        }
        // La cornisa de gola, con la banda de colores.
        drawPath(Path().apply { moveTo(f(0.24f), f(0.32f)); lineTo(f(0.2f), f(0.16f)); lineTo(f(0.8f), f(0.16f)); lineTo(f(0.76f), f(0.32f)); close() }, madera)
        val colores = listOf(LAPIS, ROJO, VERDE)
        for (j in 0 until 6) {
            val c = colores[j % 3].let { if (oscuro) mezcla(it, Color.White, 0.25f) else it }
            drawRect(c, Offset(f(0.25f + j * 0.088f), f(0.2f)), Size(f(0.06f), f(0.07f)))
        }
        drawLine(borde, Offset(f(0.24f), f(0.32f)), Offset(f(0.76f), f(0.32f)), 1.2f * u)
        drawLine(borde, Offset(f(0.19f), f(0.16f)), Offset(f(0.81f), f(0.16f)), 1.2f * u)
        drawCircle(AMARILLO, f(0.045f), Offset(f(0.5f), f(0.1f)))
        drawLine(AMARILLO, Offset(f(0.38f), f(0.1f)), Offset(f(0.44f), f(0.1f)), 1 * u)
        drawLine(AMARILLO, Offset(f(0.56f), f(0.1f)), Offset(f(0.62f), f(0.1f)), 1 * u)
    }

    override val margenAviso: Dp = 24.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) LINO else NEGRO

    /**
     * Aviso: una franja de yeso con, a la izquierda, la banda de rectángulos de colores puesta de pie entre sus dos rayas negras
     * y, junto a ella, a la altura del primer renglón, un signo según [EstadoAviso.semilla]: el anj (la vida), la pluma de Maat
     * (lo justo) o el pilar dyed (lo estable). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(if (oscuro) mezcla(MADERA, Color(0xFF110D08), 0.8f) else YESO, Offset.Zero, size)
        val raya = (if (oscuro) LINO else NEGRO).copy(alpha = if (oscuro) 0.45f else 0.6f)
        drawLine(raya, Offset(3 * u, 3 * u), Offset(3 * u, h - 3 * u), 0.7f * u)
        drawLine(raya, Offset(8.6f * u, 3 * u), Offset(8.6f * u, h - 3 * u), 0.7f * u)
        val colores = listOf(LAPIS, ROJO, VERDE)
        val paso = maxOf(6 * u, 1f)
        var y = 4 * u; var k = 0
        while (y + 4 * u < h - 3 * u && k < 200) {
            val c = colores[k % 3].let { if (oscuro) mezcla(it, Color.White, 0.25f) else it }
            drawRect(c, Offset(4 * u, y), Size(3.6f * u, 4.4f * u))
            y += paso; k++
        }
        val signo = if (oscuro) AMARILLO else mezcla(LAPIS, NEGRO, 0.2f)
        val cx = 16 * u; val y0 = 9 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                drawOval(signo, Offset(cx - 2.2f * u, y0), Size(4.4f * u, 5 * u), style = Stroke(1.2f * u))
                drawLine(signo, Offset(cx - 4 * u, y0 + 5.6f * u), Offset(cx + 4 * u, y0 + 5.6f * u), 1.4f * u)
                drawLine(signo, Offset(cx, y0 + 5 * u), Offset(cx, y0 + 14 * u), 1.6f * u)
            }
            1 -> {
                val pluma = Path().apply { moveTo(cx - 0.4f * u, y0 + 14 * u); cubicTo(cx - 3.4f * u, y0 + 8 * u, cx - 2.4f * u, y0 + 1 * u, cx + 1.8f * u, y0); cubicTo(cx + 2.6f * u, y0 + 5 * u, cx + 1.8f * u, y0 + 10 * u, cx - 0.4f * u, y0 + 14 * u); close() }
                drawPath(pluma, signo)
                drawLine(if (oscuro) mezcla(MADERA, Color(0xFF110D08), 0.8f) else YESO, Offset(cx - 0.3f * u, y0 + 13 * u), Offset(cx + 1.2f * u, y0 + 2 * u), 0.6f * u)
            }
            else -> {
                drawLine(signo, Offset(cx, y0 + 3 * u), Offset(cx, y0 + 14 * u), 2.4f * u)
                for (j in 0 until 4) drawLine(signo, Offset(cx - 3.4f * u, y0 + j * 1.8f * u), Offset(cx + 3.4f * u, y0 + j * 1.8f * u), 1 * u)
                drawLine(signo, Offset(cx - 3 * u, y0 + 14 * u), Offset(cx + 3 * u, y0 + 14 * u), 1.2f * u)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3B341) else Color(0xFF1D2F7A)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(LINO, FAYENZA, 0.25f) else mezcla(FAYENZA, NEGRO, 0.55f)

    /**
     * Cifra: un azulejo de fayenza, la loza vidriada turquesa de los templos: la cara clara y lisa donde va el número, el borde de
     * vidriado más hondo con su raya negra, el brillo del vidrio arriba a la izquierda y un punto de oro en cada esquina. Sin
     * degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 12 * u || h < 12 * u) return
        val radio = CornerRadius(3 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), radio)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        drawRoundRect(t(FAYENZA, oscuro), r.topLeft, r.size, radio)
        val c = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
        drawRoundRect(if (oscuro) mezcla(FAYENZA, Color(0xFF110D08), 0.8f) else mezcla(FAYENZA, Color.White, 0.8f), c.topLeft, c.size, CornerRadius(1.5f * u))
        drawRoundRect((if (oscuro) Color.Black else NEGRO).copy(alpha = 0.5f), c.topLeft, c.size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.12f else 0.6f), Offset(c.left + 3 * u, c.top + 1.6f * u), Offset(c.left + minOf(24 * u, c.width * 0.4f), c.top + 1.6f * u), 1.2f * u, StrokeCap.Round)
        listOf(Offset(r.left + 1.3f * u, r.top + 1.3f * u), Offset(r.right - 1.3f * u, r.top + 1.3f * u), Offset(r.left + 1.3f * u, r.bottom - 1.3f * u), Offset(r.right - 1.3f * u, r.bottom - 1.3f * u))
            .forEach { drawCircle(AMARILLO, 0.9f * u, it) }
    }
}

/**
 * Mesoamérica: el campo es un tablero de estuco bruñido, liso, con su marco pintado de cinabrio; en cada esquina, el escalón
 * de la greca, y a media altura de cada lado una cuenta de jade incrustada. Con el foco el borde del estuco se pinta de jade y
 * las cuentas brillan; con un error, de grana (la cochinilla), que tira a morado y no se confunde con el cinabrio. Con varios
 * renglones, líneas de guía tenues. Sin degradados.
 */
object ComunMeso : DisenoComun {
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

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 56.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(ESTUCO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.42f) else JADE_HONDO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF58AB4) else GRANA

    /** El escalón de la greca en la esquina [o], de lado [k], hacia [sx], [sy] (±1). */
    private fun escalon(o: Offset, k: Float, sx: Float, sy: Float): Path {
        val pts = listOf(0f to 0f, 1f to 0f, 1f to 0.34f, 0.67f to 0.34f, 0.67f to 0.67f, 0.34f to 0.67f, 0.34f to 1f, 0f to 1f)
        return Path().apply {
            pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(o.x + sx * x * k, o.y + sy * y * k) else lineTo(o.x + sx * x * k, o.y + sy * y * k) }
            close()
        }
    }

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val estuco = hondo(ESTUCO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, estuco, 0.5f)
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, CornerRadius(4 * u))
        // El marco de cinabrio y el estuco dentro.
        val rojo = a(t(CINABRIO, oscuro))
        drawRoundRect(rojo, r.topLeft, r.size, CornerRadius(4 * u))
        val b = 3.4f * u
        val c = Rect(r.left + b, r.top + b, r.right - b, r.bottom - b)
        drawRoundRect(estuco, c.topLeft, c.size, CornerRadius(1.5f * u))
        // El grano del estuco, muy fino, y el brillo del bruñido arriba.
        val granos = (c.width * c.height / (90 * u * u)).toInt().coerceIn(10, 200)
        for (j in 0 until granos) drawCircle(hondo(CALIZA_OSC, oscuro).copy(alpha = 0.16f), (0.3f + 0.5f * azar(s, j)) * u,
            Offset(c.left + c.width * azar(s, j + 300), c.top + c.height * azar(s, j + 600)))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.4f), Offset(c.left + 6 * u, c.top + 1.6f * u), Offset(c.right - 6 * u, c.top + 1.6f * u), 1.2f * u)
        if (e.multilinea) renglonesAntiguedad(c.left + 10 * u, c.right - 10 * u, margenCampoY.toPx(), c.bottom - 6 * u,
            hondo(CALIZA_OSC, oscuro).copy(alpha = if (oscuro) 0.5f else 0.32f), 0.7f * u)
        // La raya negra que separa el rojo del estuco.
        drawRoundRect(a(t(NEGRO, oscuro)).copy(alpha = 0.55f), c.topLeft, c.size, CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        // El borde del estuco: jade con el foco, grana con el error.
        val encendido = e.enfocado && e.habilitado
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) mezcla(JADE, Color.White, 0.35f) else JADE
            else -> null
        }
        if (linea != null) drawRoundRect(linea, Offset(c.left + 0.9f * u, c.top + 0.9f * u), Size(c.width - 1.8f * u, c.height - 1.8f * u),
            CornerRadius(1.5f * u), style = Stroke(1.8f * u))
        // Los escalones de las esquinas, encima del borde.
        val k = minOf(7.5f * u, c.height / 3)
        escalon(Offset(c.left, c.top), k, 1f, 1f).let { drawPath(it, rojo) }
        escalon(Offset(c.right, c.top), k, -1f, 1f).let { drawPath(it, rojo) }
        escalon(Offset(c.left, c.bottom), k, 1f, -1f).let { drawPath(it, rojo) }
        escalon(Offset(c.right, c.bottom), k, -1f, -1f).let { drawPath(it, rojo) }
        // Las cuentas de jade incrustadas a media altura de cada lado.
        val jade = a(if (oscuro) mezcla(JADE, Color.Black, 0.25f) else JADE)
        val cy = minOf(h / 2, c.top + 18 * u)
        listOf(r.left + b / 2 + 0.3f * u, r.right - b / 2 - 0.3f * u).forEach { x ->
            val o = Offset(x, cy)
            drawCircle(a(t(NEGRO, oscuro)).copy(alpha = 0.6f), 2.5f * u, o)
            drawCircle(if (encendido) mezcla(jade, AZUL_MAYA, 0.35f) else jade, 2.1f * u, o)
            drawCircle(Color.White.copy(alpha = if (encendido) 0.85f else 0.35f), (if (encendido) 1f else 0.7f) * u, Offset(o.x - 0.7f * u, o.y - 0.7f * u))
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * Separador: una raya de cinabrio y, al centro, una pirámide escalonada doble (tres escalones arriba y tres abajo, como
     * las de las grecas) con una cuenta de jade incrustada en medio. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val rojo = if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO
        val raya = rojo.copy(alpha = (if (oscuro) 0.45f else 0.5f) * f)
        val a = 8 * u; val b = 4.5f * u
        val cx = w / 2
        val medios = listOf(-1f to -1f / 3, -2f / 3 to -1f / 3, -2f / 3 to -2f / 3, -1f / 3 to -2f / 3, -1f / 3 to -1f,
            1f / 3 to -1f, 1f / 3 to -2f / 3, 2f / 3 to -2f / 3, 2f / 3 to -1f / 3, 1f to -1f / 3)
        val piramide = Path().apply {
            medios.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(cx + x * a, cy + y * b) else lineTo(cx + x * a, cy + y * b) }
            medios.reversed().forEach { (x, y) -> lineTo(cx + x * a, cy - y * b) }
            close()
        }
        drawPath(piramide, rojo.copy(alpha = (if (oscuro) 0.7f else 0.75f) * f))
        val jade = if (oscuro) mezcla(JADE, Color.White, 0.2f) else JADE
        drawCircle(jade.copy(alpha = f), 1.5f * u, Offset(cx, cy))
        drawCircle(Color.White.copy(alpha = 0.4f * f), 0.5f * u, Offset(cx - 0.5f * u, cy - 0.5f * u))
        val hueco = a + 4 * u
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(2 * u, cy), Offset(cx - hueco, cy), 1 * u)
            drawLine(raya, Offset(cx + hueco, cy), Offset(w - 2 * u, cy), 1 * u)
        }
    }

    /** El texto de los botones: el cinabrio del tema, un poco más hondo para que se lea sobre el estuco; de noche, el azul maya. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF63C5DD) else mezcla(CINABRIO, NEGRO, 0.14f)

    /**
     * Botón de texto: una raya de cinabrio bajo la palabra que en cada punta sube en dos escalones hacia afuera, como el
     * arranque de una greca escalonada. Al tocarlo la raya se vuelve de jade y el estuco de detrás toma un verde muy tenue;
     * apagado, apenas se ve. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val jade = if (oscuro) mezcla(JADE, Color.White, 0.35f) else JADE
        if (tocado) drawRoundRect(jade.copy(alpha = if (oscuro) 0.14f else 0.1f), Offset(4 * u, cy - 15 * u), Size(maxOf(w - 8 * u, 0f), 30 * u), CornerRadius(1.5f * u))
        val color = if (tocado) jade else (if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO).copy(alpha = 0.6f * apagado)
        val q = 3 * u
        val x0 = minOf(14 * u, w * 0.3f); val x1 = w - x0
        val y = cy + 11.5f * u
        if (x1 - x0 < 4 * u) return
        val p = Path().apply {
            moveTo(x0 - q, y - 2 * q); lineTo(x0 - q, y - q); lineTo(x0, y - q); lineTo(x0, y)
            lineTo(x1, y); lineTo(x1, y - q); lineTo(x1 + q, y - q); lineTo(x1 + q, y - 2 * q)
        }
        drawPath(p, color, style = Stroke((if (tocado) 1.4f else 1.1f) * u))
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El barro bruñido del cajete del vacío. */
    private val BARRO = Color(0xFFC08A5A)

    /** El cinabrio con la luz del momento: de noche, aclarado con estuco para que se vea. */
    private fun cinabrio(oscuro: Boolean): Color = if (oscuro) mezcla(CINABRIO, ESTUCO, 0.3f) else CINABRIO
    /** El jade con la luz del momento. */
    private fun jade(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.3f) else JADE
    /** El estuco de los recuadros: claro de día; de noche, en penumbra. */
    private fun estucoRecuadro(oscuro: Boolean): Color = if (oscuro) mezcla(ESTUCO, Color(0xFF100C08), 0.86f) else mezcla(ESTUCO, Color.White, 0.3f)

    /** Una cuenta de jade incrustada, de radio [r], con su engaste negro y su brillo. */
    private fun DrawScope.cuenta(c: Offset, r: Float, oscuro: Boolean, viva: Boolean) {
        drawCircle(t(NEGRO, oscuro).copy(alpha = 0.55f), r * 1.2f, c)
        drawCircle(if (viva) mezcla(jade(oscuro), AZUL_MAYA, 0.35f) else jade(oscuro), r, c)
        drawCircle(Color.White.copy(alpha = if (viva) 0.8f else 0.35f), r * 0.4f, Offset(c.x - r * 0.35f, c.y - r * 0.35f))
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: una raya de cinabrio fina abajo y, a la izquierda, una cuenta de jade incrustada (con detalle, una más chica
     * debajo). Al tocarla el estuco toma un verde muy tenue y la cuenta brilla. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(jade(oscuro).copy(alpha = if (oscuro) 0.12f else 0.08f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine(cinabrio(oscuro).copy(alpha = if (oscuro) 0.3f else 0.3f), Offset(x0, h - 1 * u), Offset(w - 4 * u, h - 1 * u), 0.9f * u)
        if (e.conDetalle) {
            cuenta(Offset(5.5f * u, h / 2 - 2.6f * u), 1.8f * u, oscuro, e.presionado)
            cuenta(Offset(5.5f * u, h / 2 + 3.2f * u), 1.2f * u, oscuro, e.presionado)
        } else cuenta(Offset(5.5f * u, h / 2), 1.9f * u, oscuro, e.presionado)
    }

    /** Flecha: una punta escalonada, como la mitad de una pirámide acostada, de cinabrio, con una cuenta de jade en medio. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val w = size.width; val h = size.height
        val cy = h / 2
        val x = floatArrayOf(0.18f, 0.4f, 0.62f, 0.86f)
        val y = floatArrayOf(0.38f, 0.27f, 0.15f)
        val p = Path().apply {
            moveTo(w * x[0], cy - h * y[0])
            for (j in 0 until 3) { lineTo(w * x[j + 1], cy - h * y[j]); if (j < 2) lineTo(w * x[j + 1], cy - h * y[j + 1]) }
            lineTo(w * x[3], cy + h * y[2])
            for (j in 2 downTo 0) { lineTo(w * x[j + 1], cy + h * y[j]); lineTo(w * x[j], cy + h * y[j]) }
            close()
        }
        drawPath(p, cinabrio(oscuro))
        cuenta(Offset(w * 0.46f, cy), h * 0.08f, oscuro, false)
    }

    /**
     * Marca de fijar: la flor de cuatro pétalos del glifo kin (el sol, el día): cuatro pétalos en cruz alrededor de un centro.
     * Fija, los pétalos de cinabrio y el centro de jade; sin fijar, solo el contorno, tenue. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val largo = 7.2f * k; val ancho = 5.6f * k; val d = 5.6f * k
        val petalos = listOf(
            Rect(c.x - ancho / 2, c.y - d - largo / 2, c.x + ancho / 2, c.y - d + largo / 2),
            Rect(c.x - ancho / 2, c.y + d - largo / 2, c.x + ancho / 2, c.y + d + largo / 2),
            Rect(c.x - d - largo / 2, c.y - ancho / 2, c.x - d + largo / 2, c.y + ancho / 2),
            Rect(c.x + d - largo / 2, c.y - ancho / 2, c.x + d + largo / 2, c.y + ancho / 2),
        )
        if (fija) {
            petalos.forEach { drawOval(cinabrio(oscuro), it.topLeft, it.size) }
            drawCircle(t(NEGRO, oscuro), 3.4f * k, c)
            drawCircle(jade(oscuro), 2.6f * k, c)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.9f * k, Offset(c.x - 0.9f * k, c.y - 0.9f * k))
        } else {
            val linea = cinabrio(oscuro).copy(alpha = if (oscuro) 0.8f else 0.5f)
            petalos.forEach { drawOval(linea, it.topLeft, it.size, style = Stroke(0.8f * u)) }
            drawCircle(linea, 2.6f * k, c, style = Stroke(0.8f * u))
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(ESTUCO, CALIZA_OSC, 0.35f) else Color(0xFF5E4630)

    /**
     * Vacío: una página de códice plegado, de papel de amate cubierto de estuco, todavía sin pintar: las rayas rojas de los
     * registros arriba y abajo (dobles, como se trazaban para guiar al tlacuilo) y, en cada pliegue, una marca corta arriba y
     * abajo, donde la tira se dobla en biombo. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 20 * u) return
        drawRect(estucoRecuadro(oscuro), Offset.Zero, size)
        val granos = (w * h / (220 * u * u)).toInt().coerceIn(10, 180)
        for (j in 0 until granos) drawCircle(hondo(CALIZA_OSC, oscuro).copy(alpha = 0.2f), (0.3f + 0.5f * azar(s, j)) * u, Offset(w * azar(s, j + 300), h * azar(s, j + 600)))
        val rojo = cinabrio(oscuro).copy(alpha = if (oscuro) 0.55f else 0.6f)
        listOf(4 * u, h - 4 * u).forEach { y ->
            drawLine(rojo, Offset(2 * u, y), Offset(w - 2 * u, y), 1 * u)
            val y2 = if (y < h / 2) y + 2.4f * u else y - 2.4f * u
            drawLine(rojo.copy(alpha = rojo.alpha * 0.6f), Offset(2 * u, y2), Offset(w - 2 * u, y2), 0.6f * u)
        }
        val pliegue = (if (oscuro) Color.Black else CALIZA_OSC).copy(alpha = if (oscuro) 0.5f else 0.5f)
        listOf(w / 3, 2 * w / 3).forEach { x ->
            drawLine(pliegue, Offset(x, 7.5f * u), Offset(x, 13 * u), 0.8f * u)
            drawLine(pliegue, Offset(x, h - 13 * u), Offset(x, h - 7.5f * u), 0.8f * u)
        }
    }

    /**
     * Lo vacío: un cajete trípode, el cuenco de barro sobre tres soportes, visto un poco desde arriba: la boca abierta muestra el
     * fondo, vacío; en la pared, la banda de cinabrio con sus puntos negros. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val barro = t(BARRO, oscuro)
        val borde = if (oscuro) mezcla(BARRO, ESTUCO, 0.35f) else mezcla(BARRO, NEGRO, 0.5f)
        // Los tres soportes.
        listOf(0.27f, 0.5f, 0.73f).forEachIndexed { j, x ->
            val y0 = if (j == 1) 0.7f else 0.66f
            val pata = Path().apply { moveTo(f(x - 0.06f), f(y0)); lineTo(f(x + 0.06f), f(y0)); lineTo(f(x + 0.035f), f(y0 + 0.22f)); lineTo(f(x - 0.035f), f(y0 + 0.22f)); close() }
            drawPath(pata, barro); drawPath(pata, borde, style = Stroke(0.8f * u))
            drawCircle(borde, f(0.012f), Offset(f(x), f(y0 + 0.1f)))
        }
        // El cuerpo del cuenco.
        val cuerpo = Path().apply {
            moveTo(f(0.1f), f(0.38f)); lineTo(f(0.9f), f(0.38f))
            cubicTo(f(0.88f), f(0.62f), f(0.72f), f(0.74f), f(0.5f), f(0.74f))
            cubicTo(f(0.28f), f(0.74f), f(0.12f), f(0.62f), f(0.1f), f(0.38f)); close()
        }
        drawPath(cuerpo, barro)
        clipPath(cuerpo) {
            drawRect(cinabrio(oscuro), Offset(0f, f(0.44f)), Size(m, f(0.08f)))
            for (j in 0 until 6) drawCircle(t(NEGRO, oscuro), f(0.014f), Offset(f(0.2f + j * 0.12f), f(0.48f)))
        }
        drawPath(cuerpo, borde, style = Stroke(1 * u))
        // La boca abierta, con el fondo vacío.
        drawOval(barro, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)))
        drawOval(if (oscuro) Color(0xFF0A0806) else mezcla(BARRO, NEGRO, 0.6f), Offset(f(0.15f), f(0.325f)), Size(f(0.7f), f(0.11f)))
        drawOval(borde, Offset(f(0.1f), f(0.3f)), Size(f(0.8f), f(0.16f)), style = Stroke(1 * u))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) ESTUCO else NEGRO

    /**
     * Aviso: una franja de estuco con, a la izquierda, la greca escalonada de pie (los escalones que suben y bajan de arriba
     * abajo, de cinabrio) y, junto a ella, a la altura del primer renglón, un signo según [EstadoAviso.semilla]: la vírgula de la
     * palabra (la voluta que en los códices sale de la boca del que habla), una cuenta de jade o la flor kin. Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(estucoRecuadro(oscuro), Offset.Zero, size)
        drawLine(Color.White.copy(alpha = if (oscuro) 0.04f else 0.5f), Offset(0f, 0.6f * u), Offset(w, 0.6f * u), 1 * u)
        val rojo = cinabrio(oscuro)
        val paso = maxOf(4 * u, 1f)
        val greca = Path().apply {
            var y = 3 * u; var k = 0
            moveTo(4 * u, y)
            while (y + paso < h - 3 * u && k < 200) {
                val x = if (k % 2 == 0) 9 * u else 4 * u
                lineTo(x, y); y += paso; lineTo(x, y); k++
            }
        }
        drawPath(greca, rojo, style = Stroke(1.4f * u))
        val cx = 18 * u; val cy = 16 * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                val voz = Path().apply {
                    moveTo(cx - 4 * u, cy + 4 * u)
                    cubicTo(cx - 4 * u, cy - 4 * u, cx + 4.5f * u, cy - 5 * u, cx + 4 * u, cy)
                    cubicTo(cx + 3.6f * u, cy + 3 * u, cx - 0.5f * u, cy + 3 * u, cx, cy - 0.2f * u)
                }
                drawPath(voz, (if (oscuro) AZUL_MAYA else mezcla(AZUL_MAYA, NEGRO, 0.35f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            }
            1 -> cuenta(Offset(cx, cy), 3 * u, oscuro, true)
            else -> {
                listOf(Offset(0f, -3f), Offset(0f, 3f), Offset(-3f, 0f), Offset(3f, 0f)).forEach { d -> drawCircle(rojo, 1.9f * u, Offset(cx + d.x * u, cy + d.y * u)) }
                drawCircle(jade(oscuro), 1.5f * u, Offset(cx, cy))
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFF63C5DD) else mezcla(CINABRIO, NEGRO, 0.2f)
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(JADE, Color.White, 0.5f) else JADE_HONDO

    /**
     * Cifra: un bloque de glifo, el cuadrete de esquinas redondas en que se pintaba cada palabra: el estuco liso, el borde de
     * cinabrio con su raya negra por dentro y, en el margen de arriba a la derecha, el siete en la cuenta de barras y puntos
     * (dos puntos sobre una barra), tenue. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 12 * u || h < 12 * u) return
        val radio = CornerRadius(minOf(9 * u, h * 0.2f))
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), radio)
        val r = Rect(0.8f * u, 0.8f * u, w - 0.8f * u, h - 1.6f * u)
        drawRoundRect(estucoRecuadro(oscuro), r.topLeft, r.size, radio)
        drawRoundRect(cinabrio(oscuro), r.topLeft, r.size, radio, style = Stroke(1.8f * u))
        val dentro = Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u)
        drawRoundRect(t(NEGRO, oscuro).copy(alpha = if (oscuro) 0.6f else 0.45f), dentro.topLeft, dentro.size, CornerRadius(maxOf(radio.x - 2.6f * u, 1f)), style = Stroke(0.6f * u))
        val tinta = (if (oscuro) ESTUCO else NEGRO).copy(alpha = 0.38f)
        val x1 = r.right - 8 * u
        if (x1 - 9 * u > r.left + 30 * u) {
            drawRoundRect(tinta, Offset(x1 - 8 * u, r.top + 7 * u), Size(8 * u, 1.6f * u), CornerRadius(0.8f * u))
            drawCircle(tinta, 0.95f * u, Offset(x1 - 5.6f * u, r.top + 5 * u))
            drawCircle(tinta, 0.95f * u, Offset(x1 - 2.4f * u, r.top + 5 * u))
        }
    }
}

/**
 * Horror cósmico: el campo es una ficha del catálogo de la biblioteca, de papel amarillento, clavada con dos tachuelas de
 * latón; a la izquierda, el doble margen rojo; abajo, la raya azul de escribir (con varios renglones, el rayado entero) y, en
 * la esquina, el cerco desvaído de una taza de té. Con el foco el margen se marca y la ficha se rodea de un resplandor verdoso,
 * algo que no debería estar ahí; con un error, el borde se pinta de rojo. Sin degradados.
 */
object ComunArcano : DisenoComun {
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

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.32f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 54.dp
    override val margenCampoX: Dp = 18.dp
    override val margenCampoY: Dp = 10.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(FICHA, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) mezcla(VERDE_RARO, PAPEL, 0.35f) else TAPETE
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF0806C) else Color(0xFFA0281E)

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val ficha = hondo(FICHA, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, ficha, 0.5f)
        val encendido = e.enfocado && e.habilitado
        val r = Rect(2.5f * u, 2 * u, w - 2.5f * u, h - 3 * u)
        // El resplandor verdoso con el foco, por fuera de la ficha.
        if (encendido && !e.error) {
            drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.18f else 0.3f), Offset(r.left - 1 * u, r.top - 1 * u), Size(r.width + 2 * u, r.height + 2.5f * u), style = Stroke(2.2f * u))
        }
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.22f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size)
        drawRect(ficha, r.topLeft, r.size)
        // El amarillo del canto de arriba y unas pecas del tiempo en las esquinas.
        drawRect(hondo(PAPEL_VIEJO, oscuro).copy(alpha = 0.5f), r.topLeft, Size(r.width, 2.5f * u))
        for (j in 0 until 6) {
            val izq = j % 2 == 0
            val x = if (izq) r.left + 3 * u + 10 * u * azar(s, j) else r.right - 3 * u - 10 * u * azar(s, j)
            val y = if (j < 4) r.bottom - 2 * u - 6 * u * azar(s, j + 10) else r.top + 4 * u + 4 * u * azar(s, j + 10)
            drawCircle(hondo(TE, oscuro).copy(alpha = 0.18f), (0.6f + 0.8f * azar(s, j + 20)) * u, Offset(x, y))
        }
        // El cerco de la taza, en la esquina de abajo a la derecha.
        clipRect(r.left, r.top, r.right, r.bottom) {
            val cc = Offset(r.right - 13 * u, r.bottom + 2 * u)
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.35f else 0.22f), 11 * u, cc, style = Stroke(1.3f * u))
            drawArc(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.25f else 0.15f), 190f, 120f, false, Offset(cc.x - 9.5f * u, cc.y - 9.5f * u), Size(19 * u, 19 * u), style = Stroke(0.8f * u))
        }
        // El rayado azul: la raya de escribir, o todos los renglones.
        val raya = a(if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = if (oscuro) 0.55f else 0.6f)
        if (e.multilinea) renglonesAntiguedad(r.left + 6 * u, r.right, margenCampoY.toPx(), r.bottom - 4 * u, raya, 0.7f * u)
        else drawLine(raya, Offset(r.left + 6 * u, r.bottom - 7 * u), Offset(r.right, r.bottom - 7 * u), 0.7f * u)
        // El doble margen rojo.
        val rojo = a(if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = if (encendido || e.error) 0.95f else 0.55f)
        val xm = r.left + 12 * u
        val gm = (if (encendido) 1.1f else 0.8f) * u
        drawLine(rojo, Offset(xm, r.top), Offset(xm, r.bottom), gm)
        drawLine(rojo, Offset(xm - 3 * u, r.top), Offset(xm - 3 * u, r.bottom), gm)
        // Las dos tachuelas de latón, arriba.
        listOf(r.left + 5 * u, r.right - 5 * u).forEach { x ->
            val o = Offset(x, r.top + 5 * u)
            drawCircle(Color.Black.copy(alpha = 0.3f), 2.2f * u, Offset(o.x + 0.5f * u, o.y + 0.7f * u))
            drawCircle(a(t(LATON, oscuro)), 2 * u, o)
            drawCircle(a(t(LATON_OSC, oscuro)), 2 * u, o, style = Stroke(0.5f * u))
            drawCircle(Color.White.copy(alpha = 0.45f), 0.6f * u, Offset(o.x - 0.6f * u, o.y - 0.6f * u))
        }
        // El borde: verde de la lámpara con el foco, rojo con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) VERDE_RARO.copy(alpha = 0.75f) else TAPETE
            else -> a(hondo(PAPEL_VIEJO, oscuro)).copy(alpha = if (oscuro) 0.6f else 0.8f)
        }
        val g = (if (e.error || encendido) 1.6f else 0.7f) * u
        drawRect(linea, Offset(r.left + g / 2, r.top + g / 2), Size(r.width - g, r.height - g), style = Stroke(g))
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /** Un asterisco de seis rayos, de radio [r], centrado en [c]. */
    private fun DrawScope.asterisco(c: Offset, r: Float, color: Color, grosor: Float) {
        for (j in 0 until 3) {
            val ang = Math.toRadians(90.0 + j * 60.0)
            val dx = (r * kotlin.math.cos(ang)).toFloat(); val dy = (r * kotlin.math.sin(ang)).toFloat()
            drawLine(color, Offset(c.x - dx, c.y - dy), Offset(c.x + dx, c.y + dy), grosor, StrokeCap.Round)
        }
    }

    /**
     * Separador: la raya azul de una ficha del catálogo, cruzada a la izquierda por el doble margen rojo (dos rayitas), y al
     * centro un asterismo (⁂), los tres asteriscos con que los libros viejos cortan una sección. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height; val cy = h / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = (if (oscuro) 0.55f else 0.65f) * f)
        val rojo = (if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = 0.5f * f)
        val tinta = (if (oscuro) PAPEL else TINTA).copy(alpha = (if (oscuro) 0.45f else 0.5f) * f)
        val cx = w / 2
        val hueco = 10 * u
        drawLine(raya, Offset(2 * u, cy), Offset(cx - hueco, cy), 0.8f * u)
        drawLine(raya, Offset(cx + hueco, cy), Offset(w - 2 * u, cy), 0.8f * u)
        val xm = 14 * u
        drawLine(rojo, Offset(xm, cy - 3.5f * u), Offset(xm, cy + 3.5f * u), 0.7f * u)
        drawLine(rojo, Offset(xm - 2.5f * u, cy - 3.5f * u), Offset(xm - 2.5f * u, cy + 3.5f * u), 0.7f * u)
        asterisco(Offset(cx, cy - 2.1f * u), 1.6f * u, tinta, 0.6f * u)
        asterisco(Offset(cx - 2.6f * u, cy + 2.1f * u), 1.6f * u, tinta, 0.6f * u)
        asterisco(Offset(cx + 2.6f * u, cy + 2.1f * u), 1.6f * u, tinta, 0.6f * u)
    }

    /** El texto de los botones: el verde del tapete de día, el amarillo de la lámpara de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6D58C) else Color(0xFF2D5446)

    /**
     * Botón de texto: la raya azul de escribir bajo la palabra y, a la izquierda, el doble margen rojo de la ficha. Al tocarlo
     * aparece detrás el resplandor verdoso de la ficha enfocada, algo que no debería estar ahí; apagado, la tinta se desvaía.
     * Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        if (tocado && w > 8 * u) drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.16f else 0.26f), Offset(4 * u, cy - 15 * u), Size(w - 8 * u, 30 * u))
        val raya = (if (oscuro) mezcla(RAYADO, PAPEL, 0.1f) else RAYADO).copy(alpha = (if (tocado) 0.95f else 0.6f) * apagado)
        val rojo = (if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO).copy(alpha = (if (tocado) 0.9f else 0.5f) * apagado)
        val y = cy + 11 * u
        drawLine(raya, Offset(4 * u, y), Offset(maxOf(w - 4 * u, 4 * u), y), (if (tocado) 1.1f else 0.8f) * u)
        val xm = 7.5f * u
        drawLine(rojo, Offset(xm, cy - 11 * u), Offset(xm, y + 2.5f * u), 0.7f * u)
        drawLine(rojo, Offset(xm - 2.5f * u, cy - 11 * u), Offset(xm - 2.5f * u, y + 2.5f * u), 0.7f * u)
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El papel con la luz del momento, para los recuadros: claro de día; de noche, en penumbra. */
    private fun hoja(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0806), 0.85f) else c
    /** El margen rojo de la ficha, de noche aclarado con papel. */
    private fun margenRojo(oscuro: Boolean): Color = if (oscuro) mezcla(MARGEN_ROJO, PAPEL, 0.25f) else MARGEN_ROJO

    /** La mano que señala (☞, la manícula de los márgenes de los libros viejos), con el índice hacia la derecha, en [r]. */
    private fun DrawScope.manicula(r: Rect, color: Color, grosor: Float) {
        val w = r.width; val h = r.height
        fun p(x: Float, y: Float) = Offset(r.left + x * w, r.top + y * h)
        // El puño de la camisa, lleno.
        drawRect(color, p(0f, 0.3f), Size(w * 0.16f, h * 0.44f))
        // La mano cerrada y el índice extendido, a línea.
        drawRoundRect(color, p(0.2f, 0.3f), Size(w * 0.36f, h * 0.44f), CornerRadius(h * 0.14f), style = Stroke(grosor))
        drawRoundRect(color, p(0.5f, 0.3f), Size(w * 0.48f, h * 0.14f), CornerRadius(h * 0.07f), style = Stroke(grosor))
        // Los dedos doblados, debajo.
        drawLine(color, p(0.3f, 0.5f), p(0.53f, 0.5f), grosor * 0.8f, StrokeCap.Round)
        drawLine(color, p(0.3f, 0.62f), p(0.5f, 0.62f), grosor * 0.8f, StrokeCap.Round)
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: el renglón de un fichero: la raya azul de escribir abajo y, a la izquierda, el doble margen rojo de la ficha en dos
     * rayas cortas (más largas si la fila lleva detalle). Al tocarla, detrás aparece el resplandor verdoso, algo que no debería
     * estar ahí, y el margen se marca. Sin degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(VERDE_RARO.copy(alpha = if (oscuro) 0.12f else 0.24f), Offset.Zero, size)
        val raya = (if (oscuro) mezcla(RAYADO, Color.Black, 0.3f) else RAYADO).copy(alpha = if (oscuro) 0.4f else 0.5f)
        drawLine(raya, Offset(0f, h - 1 * u), Offset(w, h - 1 * u), 0.7f * u)
        val rojo = margenRojo(oscuro).copy(alpha = if (e.presionado) 0.9f else 0.5f)
        val medio = (if (e.conDetalle) 11f else 7f) * u
        listOf(4.5f, 7f).forEach { x -> drawLine(rojo, Offset(x * u, h / 2 - medio), Offset(x * u, h / 2 + medio), 0.7f * u) }
    }

    /** Flecha: la manícula, la mano de tinta que señala con el índice, como en los márgenes de los libros viejos. */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val w = size.width; val h = size.height
        manicula(Rect(w * 0.04f, h * 0.18f, w * 0.98f, h * 0.86f), (if (oscuro) PAPEL else TINTA).copy(alpha = 0.82f), 0.9f * density)
    }

    /**
     * Marca de fijar: una tachuela de latón vista desde arriba, la misma que clava la ficha del campo. Clavada, la cabeza de
     * latón con su canto, su brillo y su sombra; sin clavar, solo el agujerito que dejaría y el cerco tenue de la cabeza. Sin
     * degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        if (fija) {
            drawCircle(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), 7.4f * k, Offset(c.x + 1 * k, c.y + 1.4f * k))
            drawCircle(if (oscuro) LATON else LATON, 7 * k, c)
            drawCircle(LATON_OSC, 7 * k, c, style = Stroke(1 * u))
            drawCircle(LATON_OSC.copy(alpha = 0.5f), 4.2f * k, c, style = Stroke(0.6f * u))
            drawCircle(Color.White.copy(alpha = 0.55f), 1.8f * k, Offset(c.x - 2.4f * k, c.y - 2.4f * k))
        } else {
            val linea = (if (oscuro) PAPEL else TINTA).copy(alpha = if (oscuro) 0.6f else 0.42f)
            drawCircle(linea, 7 * k, c, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.2f * u, 1.8f * u))))
            drawCircle(linea, 1 * k, c)
        }
    }

    override val tamDibujoVacio: Dp = 42.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, RAYADO, 0.25f) else Color(0xFF4A4436)

    /**
     * Vacío: una página en blanco de un libro viejo: el papel con las pecas del tiempo, el filete doble de imprenta que encuadra
     * una página que nunca se imprimió y, en una esquina, el cerco desvaído de una taza. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 24 * u) return
        drawRect(hoja(FICHA, oscuro), Offset.Zero, size)
        drawRect(hoja(PAPEL_VIEJO, oscuro).copy(alpha = 0.5f), Offset.Zero, Size(w, 2 * u))
        for (j in 0 until 10) {
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.3f else 0.16f), (0.6f + 1f * azar(s, j + 20)) * u,
                Offset(w * azar(s, j), h * azar(s, j + 10)))
        }
        val tinta = (if (oscuro) PAPEL else TINTA).copy(alpha = if (oscuro) 0.22f else 0.3f)
        drawRect(tinta, Offset(5 * u, 5 * u), Size(w - 10 * u, h - 10 * u), style = Stroke(1 * u))
        drawRect(tinta.copy(alpha = tinta.alpha * 0.7f), Offset(7.5f * u, 7.5f * u), Size(w - 15 * u, h - 15 * u), style = Stroke(0.5f * u))
        clipRect(0f, 0f, w, h) {
            val cc = Offset(w - 20 * u, h - 2 * u)
            drawCircle(hondo(TE, oscuro).copy(alpha = if (oscuro) 0.35f else 0.2f), 14 * u, cc, style = Stroke(1.3f * u))
        }
    }

    /**
     * Lo vacío: un marco ovalado de retrato colgado de su clavo, con el cordón y el aro de latón con cuentas, y dentro nada:
     * la oscuridad, con un reflejo verdoso apenas y, si se mira bien, dos puntitos que no deberían estar ahí. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val laton = t(LATON, oscuro)
        val linea = if (oscuro) mezcla(LATON, PAPEL, 0.3f) else LATON_OSC
        // El clavo y el cordón.
        drawCircle(linea, f(0.03f), Offset(f(0.5f), f(0.05f)))
        drawLine(linea, Offset(f(0.5f), f(0.05f)), Offset(f(0.32f), f(0.22f)), 0.8f * u)
        drawLine(linea, Offset(f(0.5f), f(0.05f)), Offset(f(0.68f), f(0.22f)), 0.8f * u)
        // El aro y su hondo oscuro.
        val fuera = Rect(f(0.18f), f(0.16f), f(0.82f), f(0.96f))
        drawOval(laton, fuera.topLeft, fuera.size)
        drawOval(linea, fuera.topLeft, fuera.size, style = Stroke(0.8f * u))
        val dentro = Rect(f(0.26f), f(0.25f), f(0.74f), f(0.87f))
        drawOval(if (oscuro) Color(0xFF040605) else mezcla(TAPETE, TINTA, 0.55f), dentro.topLeft, dentro.size)
        drawOval(linea, dentro.topLeft, dentro.size, style = Stroke(0.8f * u))
        // Las cuentas del aro.
        for (j in 0 until 12) {
            val a = j * Math.PI.toFloat() / 6
            drawCircle(linea, f(0.014f), Offset(f(0.5f) + f(0.28f) * kotlin.math.cos(a), f(0.56f) + f(0.36f) * kotlin.math.sin(a)))
        }
        // El reflejo verdoso y lo que mira desde dentro.
        drawOval(VERDE_RARO.copy(alpha = if (oscuro) 0.12f else 0.16f), Offset(f(0.31f), f(0.31f)), Size(f(0.22f), f(0.3f)))
        val ojo = VERDE_RARO.copy(alpha = if (oscuro) 0.55f else 0.45f)
        drawCircle(ojo, f(0.016f), Offset(f(0.44f), f(0.55f)))
        drawCircle(ojo, f(0.016f), Offset(f(0.56f), f(0.55f)))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA

    /**
     * Aviso: un recorte de periódico pegado, de papel amarillento con la esquina de arriba a la derecha cortada en diagonal; a la
     * izquierda, el doble margen rojo de arriba abajo y, a la altura del primer renglón, la marca que abre la nota según
     * [EstadoAviso.semilla]: el calderón (¶), la manícula (☞) o el asterismo (⁂). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val corte = minOf(8 * u, h / 3)
        val recorte = Path().apply { moveTo(0f, 0f); lineTo(w - corte, 0f); lineTo(w, corte); lineTo(w, h); lineTo(0f, h); close() }
        drawPath(recorte, hoja(PAPEL_VIEJO, oscuro))
        drawPath(recorte, (if (oscuro) PAPEL else TE).copy(alpha = if (oscuro) 0.14f else 0.35f), style = Stroke(0.7f * u))
        val rojo = margenRojo(oscuro).copy(alpha = 0.6f)
        drawLine(rojo, Offset(5 * u, 0f), Offset(5 * u, h), 0.7f * u)
        drawLine(rojo, Offset(7.6f * u, 0f), Offset(7.6f * u, h), 0.7f * u)
        val tinta = (if (oscuro) mezcla(VERDE_RARO, PAPEL, 0.35f) else TAPETE)
        val cx = 16.5f * u; val cy = 16.5f * u
        when (((e.semilla % 3) + 3) % 3) {
            0 -> {
                drawArc(tinta, 90f, 180f, true, Offset(cx - 3.6f * u, cy - 5 * u), Size(5.2f * u, 5.2f * u))
                drawLine(tinta, Offset(cx - 0.6f * u, cy - 5 * u), Offset(cx - 0.6f * u, cy + 5 * u), 1 * u)
                drawLine(tinta, Offset(cx + 1.6f * u, cy - 5 * u), Offset(cx + 1.6f * u, cy + 5 * u), 1 * u)
                drawLine(tinta, Offset(cx - 1 * u, cy - 4.6f * u), Offset(cx + 2.4f * u, cy - 4.6f * u), 0.9f * u)
            }
            1 -> manicula(Rect(cx - 6 * u, cy - 4.5f * u, cx + 6 * u, cy + 3.5f * u), tinta, 0.8f * u)
            else -> {
                asterisco(Offset(cx, cy - 2.4f * u), 1.9f * u, tinta, 0.7f * u)
                asterisco(Offset(cx - 3 * u, cy + 2.4f * u), 1.9f * u, tinta, 0.7f * u)
                asterisco(Offset(cx + 3 * u, cy + 2.4f * u), 1.9f * u, tinta, 0.7f * u)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6D58C) else TAPETE
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(PAPEL, RAYADO, 0.3f) else mezcla(TINTA, TE, 0.35f)

    /**
     * Cifra: el portaetiquetas de latón de un cajón del fichero de la biblioteca: el marco de latón con su canto, los dos
     * tornillos a los lados (de ranura) y, dentro, la tarjeta de papel con la raya azul abajo, donde va el número. Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 16 * u || h < 16 * u) return
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.45f else 0.2f), Offset(0.5f * u, 1.5f * u), Size(w - 1 * u, h - 1.5f * u), CornerRadius(2 * u))
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 1.5f * u)
        val laton = t(LATON, oscuro)
        drawRoundRect(laton, r.topLeft, r.size, CornerRadius(2 * u))
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.1f else 0.4f), Color.Black.copy(alpha = 0.3f), 0.8f * u)
        val c = Rect(r.left + 3.4f * u, r.top + 3.2f * u, r.right - 3.4f * u, r.bottom - 3.2f * u)
        drawRect(hoja(FICHA, oscuro), c.topLeft, c.size)
        bisel(c, Color.Black.copy(alpha = 0.3f), Color.White.copy(alpha = if (oscuro) 0.04f else 0.3f), 0.8f * u)
        drawLine((if (oscuro) mezcla(RAYADO, Color.Black, 0.35f) else RAYADO).copy(alpha = 0.55f), Offset(c.left + 2 * u, c.bottom - 3 * u), Offset(c.right - 2 * u, c.bottom - 3 * u), 0.6f * u)
        listOf(r.left + 1.7f * u, r.right - 1.7f * u).forEach { x ->
            val o = Offset(x, r.center.y)
            drawCircle(t(LATON_OSC, oscuro), 1.3f * u, o)
            drawLine(t(LATON, oscuro), Offset(o.x, o.y - 1 * u), Offset(o.x, o.y + 1 * u), 0.5f * u)
        }
    }
}

/**
 * Tierras Antiguas: el campo es un marco de madera tallada, con sus vetas y una hilera de muescas arriba y abajo, que sujeta una
 * hoja de pergamino de borde irregular, lisa; en las cuatro esquinas, cantoneras de latón remachadas, como las de los tomos.
 * Con el foco las muescas toman la luz de la vela y el pergamino se ribetea de oro; con un error, de rojo de mapa. Con varios
 * renglones, rayas tenues de pauta. Sin degradados.
 */
object ComunAntiguas : DisenoComun {
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

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.36f) else c
    /** Hundido de verdad para el modo oscuro: lo que lleva letra encima queda en penumbra. */
    private fun hondo(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0D0906), 0.76f) else c

    // ---------------------------------------------------------------- los campos de texto

    override val altoCampo: Dp = 58.dp
    override val margenCampoX: Dp = 16.dp
    override val margenCampoY: Dp = 12.dp
    override fun tintaCampo(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    override fun pistaCampo(oscuro: Boolean): Color = mezcla(tintaCampo(oscuro), hondo(PERGAMINO, oscuro), 0.42f)
    override fun rotuloCampo(oscuro: Boolean): Color = if (oscuro) LATON_LUZ else LATON_VIEJO
    override fun errorCampo(oscuro: Boolean): Color = if (oscuro) Color(0xFFF08A6A) else ROJO_MAPA

    override fun DrawScope.marcoCampo(e: EstadoMarco, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        val perg = hondo(PERGAMINO, oscuro)
        fun a(c: Color) = if (e.habilitado) c else mezcla(c, perg, 0.45f)
        val encendido = e.enfocado && e.habilitado
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 2.5f * u)
        val radio = CornerRadius(4 * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f), Offset(r.left + 0.5f * u, r.top + 1.5f * u), r.size, radio)
        // La madera tallada.
        drawRoundRect(a(t(MADERA, oscuro)), r.topLeft, r.size, radio)
        clipRect(r.left, r.top, r.right, r.bottom) { vetas(r, a(t(NUDO, oscuro)).copy(alpha = 0.45f), s, 0.7f) }
        bisel(r, Color.White.copy(alpha = if (oscuro) 0.05f else 0.15f), Color.Black.copy(alpha = 0.3f), 1.5f * u)
        // La hoja de pergamino, de borde irregular, con su sombra de asiento.
        val p = Rect(r.left + 6 * u, r.top + 6 * u, r.right - 6 * u, r.bottom - 6 * u)
        val puntos = ((p.width + p.height) * 2 / (6 * u)).toInt().coerceIn(20, 240)
        val hoja = contornoIrregular(p, s + 17, puntos, 0.7f * u / minOf(p.width, p.height).coerceAtLeast(1f))
        drawPath(hoja, perg)
        drawPath(hoja, a(t(NUDO, oscuro)).copy(alpha = 0.4f), style = Stroke(1 * u))
        if (e.multilinea) renglonesAntiguedad(p.left + 8 * u, p.right - 8 * u, margenCampoY.toPx(), p.bottom - 4 * u,
            hondo(PERGAMINO_OSC, oscuro).copy(alpha = if (oscuro) 0.7f else 0.45f), 0.7f * u)
        // Las muescas de la talla, arriba y abajo de la hoja.
        val muesca = if (encendido) (if (oscuro) LLAMA else LATON_LUZ) else a(t(TALLA, oscuro))
        val pm = maxOf(8 * u, 1f)
        var x = p.left + 8 * u; var k = 0
        while (x < p.right - 10 * u && k < 300) {
            drawLine(muesca, Offset(x, r.top + 2 * u), Offset(x + 2 * u, r.top + 4.4f * u), 1.1f * u, StrokeCap.Round)
            drawLine(muesca, Offset(x, r.bottom - 2 * u), Offset(x + 2 * u, r.bottom - 4.4f * u), 1.1f * u, StrokeCap.Round)
            x += pm; k++
        }
        // El ribete del pergamino: oro con el foco, rojo de mapa con el error.
        val linea = when {
            e.error -> errorCampo(oscuro)
            encendido -> if (oscuro) LLAMA else LATON_OSC
            else -> null
        }
        if (linea != null) drawPath(hoja, linea, style = Stroke(1.8f * u))
        // Las cantoneras de latón remachadas en las cuatro esquinas.
        val marco = Path().apply { addRoundRect(RoundRect(r, radio)) }
        val lado = minOf(13 * u, r.height / 3)
        val laton = a(t(if (encendido) LATON_LUZ else LATON, oscuro))
        clipPath(marco) {
            listOf(Triple(r.left, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f))
                .forEach { (cx, cy, d) ->
                    val (sx, sy) = d
                    val pieza = Path().apply {
                        moveTo(cx, cy); lineTo(cx + sx * lado, cy)
                        quadraticTo(cx + sx * lado * 0.55f, cy + sy * lado * 0.55f, cx, cy + sy * lado); close()
                    }
                    drawPath(pieza, laton)
                    drawPath(pieza, a(t(LATON_OSC, oscuro)), style = Stroke(0.7f * u))
                    val o = Offset(cx + sx * 3.6f * u, cy + sy * 3.6f * u)
                    drawCircle(a(t(LATON_OSC, oscuro)), 1.1f * u, o)
                    drawCircle(Color.White.copy(alpha = 0.45f), 0.45f * u, Offset(o.x - 0.3f * u, o.y - 0.3f * u))
                }
        }
    }

    // ---------------------------------------------------------------- separadores y botones de texto

    override val altoSeparador: Dp = 12.dp

    /**
     * Separador: una raya de tinta de mapa, rematada en un punto en cada punta, y al centro una rosa de los vientos pequeña de
     * latón (una estrella de cuatro puntas sobre otra girada), entre dos puntos. Sin degradados.
     */
    override fun DrawScope.separador(e: EstadoSeparador, oscuro: Boolean) {
        val u = density
        val w = size.width; val cy = size.height / 2
        val f = if (e.tenue) 0.5f else 1f
        val raya = (if (oscuro) PERGAMINO.copy(alpha = 0.22f) else TINTA.copy(alpha = 0.38f)).let { it.copy(alpha = it.alpha * f) }
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = (if (oscuro) 0.75f else 0.85f) * f)
        val laton2 = (if (oscuro) mezcla(LATON, Color.Black, 0.4f) else LATON).copy(alpha = (if (oscuro) 0.6f else 0.7f) * f)
        val c = Offset(w / 2, cy)
        drawPath(estrella(c, 3.2f * u, 4, 0.32f, -45f), laton2)
        drawPath(estrella(c, 4.8f * u, 4, 0.26f), laton)
        val hueco = 12 * u
        listOf(-1f, 1f).forEach { lado -> drawCircle(laton, 0.9f * u, Offset(c.x + lado * 8 * u, cy)) }
        if (w / 2 - hueco > 4 * u) {
            drawLine(raya, Offset(4 * u, cy), Offset(c.x - hueco, cy), 1 * u)
            drawLine(raya, Offset(c.x + hueco, cy), Offset(w - 4 * u, cy), 1 * u)
            drawCircle(raya, 1.3f * u, Offset(3 * u, cy))
            drawCircle(raya, 1.3f * u, Offset(w - 3 * u, cy))
        }
    }

    /** El texto de los botones: el verde del tema de día, el verde claro de noche. */
    override fun tintaBotonTexto(oscuro: Boolean): Color = if (oscuro) Color(0xFF8CC79A) else Color(0xFF2E5A3E)

    /**
     * Botón de texto: cuatro cantoneras pequeñas de latón en las esquinas, como las del campo (un triángulo de lado curvo con
     * su remache); nada más, para que la palabra quede sola en medio. Al tocarlo el hueco se llena de pergamino tenue y el
     * latón toma la luz de la vela; apagado, el latón se oscurece. Sin degradados.
     */
    override fun DrawScope.botonTexto(e: EstadoBotonTexto, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2
        val apagado = if (e.habilitado) 1f else 0.35f
        val tocado = e.presionado && e.habilitado
        val r = Rect(3 * u, cy - 14 * u, w - 3 * u, cy + 14 * u)
        if (r.width < 8 * u) return
        if (tocado) drawRoundRect((if (oscuro) LATON else PERGAMINO).copy(alpha = if (oscuro) 0.12f else 0.45f), r.topLeft, r.size, CornerRadius(2 * u))
        val laton = if (tocado) (if (oscuro) LLAMA else LATON) else (if (oscuro) mezcla(LATON, Color.Black, 0.2f) else LATON).copy(alpha = (if (oscuro) 0.7f else 0.85f) * apagado)
        val borde = (if (oscuro) mezcla(LATON_OSC, Color.Black, 0.3f) else LATON_OSC).copy(alpha = (if (tocado) 1f else 0.8f) * apagado)
        val lado = minOf(7 * u, r.width / 3)
        listOf(Triple(r.left, r.top, 1f to 1f), Triple(r.right, r.top, -1f to 1f), Triple(r.left, r.bottom, 1f to -1f), Triple(r.right, r.bottom, -1f to -1f))
            .forEach { (x, y, d) ->
                val (sx, sy) = d
                val pieza = Path().apply {
                    moveTo(x, y); lineTo(x + sx * lado, y)
                    quadraticTo(x + sx * lado * 0.45f, y + sy * lado * 0.45f, x, y + sy * lado); close()
                }
                drawPath(pieza, laton)
                drawPath(pieza, borde, style = Stroke(0.6f * u))
                drawCircle(borde, 0.8f * u, Offset(x + sx * 2 * u, y + sy * 2 * u))
            }
    }

    // ---------------------------------------------------------------- filas, vacío, aviso y cifra

    /** El rojo de mapa, de noche aclarado para que se vea. */
    private fun rojoMapa(oscuro: Boolean): Color = if (oscuro) Color(0xFFE0705E) else ROJO_MAPA
    /** El pergamino de los recuadros: claro de día; de noche, en penumbra. */
    private fun pergamino(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, Color(0xFF0D0906), 0.85f) else mezcla(PERGAMINO, Color.White, 0.35f)

    /** Un rombo de latón pequeño (una gema engastada), de medio ancho [a] y medio alto [b], centrado en [c]. */
    private fun rombo(c: Offset, a: Float, b: Float): Path = Path().apply {
        moveTo(c.x, c.y - b); lineTo(c.x + a, c.y); lineTo(c.x, c.y + b); lineTo(c.x - a, c.y); close()
    }

    override val margenFila: Dp = 12.dp

    /**
     * Fila: abajo, el camino punteado de un mapa (rayitas de tinta, una sola línea con guiones) y, a la izquierda, una gema de
     * latón en rombo (con detalle, dos, una sobre otra). Al tocarla, la luz de la vela lava la fila y la gema se enciende. Sin
     * degradados.
     */
    override fun DrawScope.fila(e: EstadoFila, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (e.presionado) drawRect(LLAMA.copy(alpha = if (oscuro) 0.09f else 0.18f), Offset.Zero, size)
        val x0 = margenFila.toPx()
        if (w - x0 > 4 * u) drawLine((if (oscuro) PERGAMINO.copy(alpha = 0.2f) else TINTA.copy(alpha = 0.3f)), Offset(x0, h - 1.2f * u), Offset(w - 4 * u, h - 1.2f * u), 0.9f * u,
            StrokeCap.Round, PathEffect.dashPathEffect(floatArrayOf(3 * u, 3.5f * u)))
        val laton = if (e.presionado) (if (oscuro) LLAMA else LATON) else (if (oscuro) LATON else LATON_OSC).copy(alpha = 0.7f)
        if (e.conDetalle) {
            drawPath(rombo(Offset(5.5f * u, h / 2 - 3.4f * u), 1.8f * u, 2.6f * u), laton)
            drawPath(rombo(Offset(5.5f * u, h / 2 + 3.4f * u), 1.5f * u, 2.2f * u), laton)
        } else drawPath(rombo(Offset(5.5f * u, h / 2), 2 * u, 3 * u), laton)
    }

    /**
     * Flecha: la aguja de una brújula, acostada: la mitad que señala, de rojo de mapa, llena, y la otra de latón, a línea, con el
     * pivote en medio. Sin degradados.
     */
    override fun DrawScope.flechaFila(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val cy = h / 2; val cx = w * 0.5f
        val a = h * 0.17f
        val punta = Path().apply { moveTo(w * 0.95f, cy); lineTo(cx, cy - a); lineTo(cx, cy + a); close() }
        val cola = Path().apply { moveTo(w * 0.06f, cy); lineTo(cx, cy - a); lineTo(cx, cy + a); close() }
        drawPath(punta, rojoMapa(oscuro))
        drawPath(cola, (if (oscuro) LATON else LATON_OSC), style = Stroke(0.9f * u))
        drawCircle(if (oscuro) LATON_LUZ else LATON_OSC, 1.3f * u, Offset(cx, cy))
    }

    /** El contorno de una gota de lacre de [r] de radio, que se corrió al aplastarla: un círculo de borde irregular (22 lados). */
    private fun gotaLacre(c: Offset, r: Float): Path = Path().apply {
        val n = 22
        for (j in 0..n) {
            val a = j * 2 * Math.PI.toFloat() / n
            val rr = r * (0.9f + 0.1f * azar(23, j % n))
            val p = Offset(c.x + rr * kotlin.math.cos(a), c.y + rr * kotlin.math.sin(a))
            if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }

    /**
     * Marca de fijar: un sello de lacre. Sellado, la gota de lacre rojo aplastada, con el cerco del sello y la rosa de los vientos
     * hundida en medio; sin sellar, solo el contorno de la gota con guiones y la rosa a línea, tenues. Sin degradados.
     */
    override fun DrawScope.marcaFija(fija: Boolean, oscuro: Boolean) {
        val u = density
        val k = size.minDimension / 24f
        val c = Offset(size.width / 2, size.height / 2)
        val gota = gotaLacre(c, 10 * k)
        val rosa = estrella(c, 5 * k, 4, 0.34f)
        if (fija) {
            val rojo = rojoMapa(oscuro)
            translate(0.4f * k, 0.9f * k) { drawPath(gota, Color.Black.copy(alpha = if (oscuro) 0.5f else 0.25f)) }
            drawPath(gota, rojo)
            drawCircle(mezcla(rojo, Color.Black, 0.3f), 6.8f * k, c, style = Stroke(0.9f * u))
            drawPath(rosa, mezcla(rojo, Color.Black, 0.35f))
            drawCircle(Color.White.copy(alpha = 0.35f), 1.4f * k, Offset(c.x - 5 * k, c.y - 5 * k))
        } else {
            val linea = (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.6f else 0.42f)
            drawPath(gota, linea, style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
            drawPath(rosa, linea, style = Stroke(0.7f * u))
        }
    }

    override val tamDibujoVacio: Dp = 44.dp
    override fun tintaVacio(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, PERGAMINO_OSC, 0.4f) else Color(0xFF574430)

    /**
     * Vacío: un mapa en blanco, la tierra que nadie ha recorrido todavía: la hoja de pergamino de borde irregular con su filete
     * de encuadre (la línea que cierra el mapa) y, abajo a la derecha, la rosa de los vientos pequeña y tenue, sin camino que
     * señalar. Sin degradados.
     */
    override fun DrawScope.marcoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        val s = e.semilla
        if (w < 24 * u || h < 24 * u) return
        val r = Rect(1 * u, 1 * u, w - 1 * u, h - 1 * u)
        val puntos = ((r.width + r.height) * 2 / (7 * u)).toInt().coerceIn(20, 240)
        val perg = contornoIrregular(r, s + 3, puntos, 1.4f * u / minOf(r.width, r.height).coerceAtLeast(1f))
        drawPath(perg, pergamino(oscuro))
        drawPath(perg, (if (oscuro) PERGAMINO else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.14f else 0.6f), style = Stroke(0.8f * u))
        val tinta = (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.2f else 0.28f)
        drawRect(tinta, Offset(6 * u, 6 * u), Size(w - 12 * u, h - 12 * u), style = Stroke(0.8f * u))
        val c = Offset(w - 18 * u, h - 18 * u)
        val laton = (if (oscuro) LATON else LATON_OSC).copy(alpha = if (oscuro) 0.4f else 0.45f)
        drawPath(estrella(c, 4.6f * u, 4, 0.32f, -45f), laton.copy(alpha = laton.alpha * 0.7f))
        drawPath(estrella(c, 7 * u, 4, 0.24f), laton)
        drawCircle(laton, 9 * u, c, style = Stroke(0.5f * u))
    }

    /**
     * Lo vacío: un cofre abierto y sin nada: la caja de madera con sus flejes de latón, la tapa curva echada hacia atrás, el
     * hondo oscuro a la vista y la cerradura con su gema al frente. Sin degradados.
     */
    override fun DrawScope.dibujoVacio(e: EstadoVacio, oscuro: Boolean) {
        val u = density
        val m = size.minDimension
        fun f(v: Float) = v * m
        val madera = t(if (oscuro) mezcla(MADERA, PERGAMINO, 0.2f) else MADERA, oscuro)
        val linea = if (oscuro) mezcla(MADERA, PERGAMINO, 0.55f) else NUDO
        val laton = t(LATON, oscuro)
        drawOval(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.15f), Offset(f(0.1f), f(0.86f)), Size(f(0.8f), f(0.08f)))
        // La tapa, echada hacia atrás: media caña vista por dentro.
        drawArc(madera, 180f, 180f, true, Offset(f(0.14f), f(0.08f)), Size(f(0.72f), f(0.4f)))
        drawArc(linea, 180f, 180f, true, Offset(f(0.14f), f(0.08f)), Size(f(0.72f), f(0.4f)), style = Stroke(0.9f * u))
        drawArc(laton, 180f, 180f, false, Offset(f(0.24f), f(0.14f)), Size(f(0.52f), f(0.3f)), style = Stroke(1.2f * u))
        // El hondo del cofre, vacío.
        drawRect(if (oscuro) Color(0xFF070504) else mezcla(NUDO, Color.Black, 0.3f), Offset(f(0.15f), f(0.28f)), Size(f(0.7f), f(0.22f)))
        // La caja.
        drawRect(madera, Offset(f(0.12f), f(0.48f)), Size(f(0.76f), f(0.38f)))
        drawRect(linea, Offset(f(0.12f), f(0.48f)), Size(f(0.76f), f(0.38f)), style = Stroke(0.9f * u))
        listOf(0.22f, 0.74f).forEach { x -> drawRect(laton, Offset(f(x), f(0.48f)), Size(f(0.05f), f(0.38f))) }
        drawLine(laton, Offset(f(0.12f), f(0.5f)), Offset(f(0.88f), f(0.5f)), 1.2f * u)
        // La cerradura con su gema.
        drawRect(laton, Offset(f(0.43f), f(0.54f)), Size(f(0.14f), f(0.15f)))
        drawPath(rombo(Offset(f(0.5f), f(0.615f)), f(0.035f), f(0.05f)), rojoMapa(oscuro))
    }

    override val margenAviso: Dp = 26.dp
    override fun tintaAviso(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA

    /**
     * Aviso: una hoja de pergamino clavada a un listón de madera tallada, a la izquierda, con sus remaches de latón de trecho en
     * trecho; junto al listón, a la altura del primer renglón, la marca de la nota según [EstadoAviso.semilla]: la rosa de los
     * vientos, la llama de la vela o la runa algiz (la de la protección). Sin degradados.
     */
    override fun DrawScope.aviso(e: EstadoAviso, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        drawRect(pergamino(oscuro), Offset.Zero, size)
        drawLine((if (oscuro) PERGAMINO else PERGAMINO_OSC).copy(alpha = if (oscuro) 0.14f else 0.6f), Offset(0f, h - 0.5f * u), Offset(w, h - 0.5f * u), 1 * u)
        // El listón con sus remaches.
        val liston = Rect(1.5f * u, 0f, 9 * u, h)
        drawRect(t(TALLA, oscuro), liston.topLeft, liston.size)
        clipRect(liston.left, liston.top, liston.right, liston.bottom) { vetas(liston, t(NUDO, oscuro).copy(alpha = 0.5f), e.semilla, 0.7f, vertical = true) }
        val paso = maxOf(13 * u, 1f)
        var y = 6 * u; var k = 0
        while (y < h - 3 * u && k < 100) {
            drawCircle(t(LATON_OSC, oscuro), 1.3f * u, Offset(liston.center.x, y))
            drawCircle(t(LATON_LUZ, oscuro), 0.6f * u, Offset(liston.center.x - 0.3f * u, y - 0.3f * u))
            y += paso; k++
        }
        val cx = 17.5f * u; val cy = 16 * u
        val laton = if (oscuro) LATON else LATON_OSC
        when (((e.semilla % 3) + 3) % 3) {
            0 -> { drawPath(estrella(Offset(cx, cy), 3.6f * u, 4, 0.32f, -45f), laton.copy(alpha = 0.6f)); drawPath(estrella(Offset(cx, cy), 5.4f * u, 4, 0.26f), laton) }
            1 -> {
                val llama = Path().apply { moveTo(cx, cy - 6 * u); cubicTo(cx + 4 * u, cy - 1 * u, cx + 3.4f * u, cy + 4 * u, cx, cy + 4.4f * u); cubicTo(cx - 3.4f * u, cy + 4 * u, cx - 4 * u, cy - 1 * u, cx, cy - 6 * u); close() }
                drawPath(llama, if (oscuro) LLAMA else mezcla(LLAMA, ROJO_MAPA, 0.45f))
                drawOval(if (oscuro) mezcla(LLAMA, Color.White, 0.5f) else LLAMA, Offset(cx - 1.2f * u, cy - 0.4f * u), Size(2.4f * u, 3.8f * u))
            }
            else -> {
                val tinta = if (oscuro) PERGAMINO else TINTA
                drawLine(tinta, Offset(cx, cy - 5 * u), Offset(cx, cy + 5 * u), 1.2f * u, StrokeCap.Round)
                drawLine(tinta, Offset(cx, cy), Offset(cx - 3.6f * u, cy - 4.6f * u), 1.2f * u, StrokeCap.Round)
                drawLine(tinta, Offset(cx, cy), Offset(cx + 3.6f * u, cy - 4.6f * u), 1.2f * u, StrokeCap.Round)
            }
        }
    }

    override val margenCifra: Dp = 10.dp
    override fun tintaCifra(oscuro: Boolean): Color = if (oscuro) LLAMA else ROJO_MAPA
    override fun rotuloCifra(oscuro: Boolean): Color = if (oscuro) mezcla(PERGAMINO, LATON, 0.35f) else LATON_VIEJO

    /**
     * Cifra: un marbete de pergamino, la etiqueta que se ata a un tomo o a un cofre: el pergamino con las dos esquinas de la
     * derecha cortadas en punta, su borde de tinta y, cerca de la punta, el ojal con su arandela de latón y el cordón que se va.
     * Sin degradados.
     */
    override fun DrawScope.cifra(e: EstadoCifra, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 24 * u || h < 16 * u) return
        val corte = minOf(10 * u, h * 0.3f)
        val marbete = Path().apply {
            moveTo(0.5f * u, 0.5f * u); lineTo(w - corte, 0.5f * u); lineTo(w - 0.5f * u, corte); lineTo(w - 0.5f * u, h - 1.5f * u - corte)
            lineTo(w - corte, h - 1.5f * u); lineTo(0.5f * u, h - 1.5f * u); close()
        }
        translate(0.6f * u, 1.2f * u) { drawPath(marbete, Color.Black.copy(alpha = if (oscuro) 0.45f else 0.18f)) }
        drawPath(marbete, pergamino(oscuro))
        drawPath(marbete, (if (oscuro) PERGAMINO else TINTA).copy(alpha = if (oscuro) 0.22f else 0.4f), style = Stroke(0.8f * u))
        val o = Offset(w - 6.5f * u, (h - 1.5f * u) / 2)
        drawCircle(t(LATON, oscuro), 2.8f * u, o)
        drawCircle(t(LATON_OSC, oscuro), 2.8f * u, o, style = Stroke(0.6f * u))
        drawCircle(if (oscuro) Color(0xFF070504) else TINTA.copy(alpha = 0.75f), 1.2f * u, o)
        drawPath(Path().apply { moveTo(o.x, o.y); quadraticTo(o.x + 4 * u, o.y + 3 * u, w, o.y + 1.6f * u) }, (if (oscuro) PERGAMINO else NUDO).copy(alpha = 0.6f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }
}
