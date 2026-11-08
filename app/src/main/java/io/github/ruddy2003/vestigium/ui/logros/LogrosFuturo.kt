package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.engrane
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Futuro
// =====================================================================

/** El gris translúcido de lo que aún no se gana: la misma silueta tenue que la figura sin ganar. */
private fun siluetaFuturo(oscuro: Boolean): Color = if (oscuro) Color(0xFFE4E4E4).copy(alpha = 0.13f) else Color(0xFF1E1E1E).copy(alpha = 0.13f)

/** Un rectángulo dado en fracciones del lienzo. */
private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)

/** Un punto dado en fracciones del lienzo. */
private fun DrawScope.pt(x: Float, y: Float) = Offset(size.width * x, size.height * y)

/** Un polígono cerrado con sus vértices en fracciones del lienzo: x0, y0, x1, y1… */
private fun DrawScope.poli(vararg f: Float): Path = Path().apply {
    var k = 0
    while (k + 1 < f.size) { val x = size.width * f[k]; val y = size.height * f[k + 1]; if (k == 0) moveTo(x, y) else lineTo(x, y); k += 2 }
    close()
}

/** La sombra al pie del pedestal, de [x0] a [x1] (fracciones del ancho): dos óvalos bajos, el de adentro más hondo. */
private fun DrawScope.sombraPie(x0: Float, x1: Float, oscuro: Boolean, y: Float = 0.955f) {
    val w = size.width; val h = size.height
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.32f else 0.15f), Offset(w * x0, h * (y - 0.012f)), Size(w * (x1 - x0), h * 0.03f))
    drawOval(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.1f), Offset(w * (x0 + 0.05f), h * (y - 0.006f)), Size(w * (x1 - x0 - 0.1f), h * 0.016f))
}

/**
 * Una chapa en el metal del nivel: la base, el filo de luz arriba, la sombra abajo y su contorno oscuro. La legendaria
 * (cristal, [EstadoPedestal.nivel] 4) lleva además su faceta blanca en diagonal.
 */
private fun DrawScope.chapaNivel(r: Rect, e: EstadoPedestal, radio: Float = 0f) {
    if (r.width < 1f || r.height < 1f) return
    val u = density
    val rr = radio.coerceAtMost(minOf(r.width, r.height) / 2)
    drawRoundRect(e.base, r.topLeft, r.size, CornerRadius(rr))
    val filo = (r.height * 0.24f).coerceIn(minOf(0.5f * u, r.height * 0.4f), r.height * 0.4f)
    drawRect(e.brillo, Offset(r.left + rr * 0.6f, r.top + filo * 0.25f), Size(r.width - rr * 1.2f, filo))
    drawRect(e.metalOscuro.copy(alpha = 0.5f), Offset(r.left + rr * 0.6f, r.bottom - filo), Size(r.width - rr * 1.2f, filo * 0.8f))
    drawRoundRect(e.metalOscuro, r.topLeft, r.size, CornerRadius(rr), style = Stroke(maxOf(minOf(r.width, r.height) * 0.08f, 0.45f * u)))
    if (e.nivel >= 4) drawLine(Color.White.copy(alpha = 0.85f), Offset(r.left + r.width * 0.58f, r.bottom - r.height * 0.18f),
        Offset(r.left + r.width * 0.7f, r.top + r.height * 0.18f), maxOf(r.height * 0.1f, 0.5f * u), StrokeCap.Round)
}

/** Un remache o un tornillo de cabeza redonda: el cuerpo y su punto de luz arriba a la izquierda. */
private fun DrawScope.remache(c: Offset, r: Float, cuerpo: Color, luz: Color) {
    drawCircle(cuerpo, r, c)
    drawCircle(luz, r * 0.42f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
}

/** Un rectángulo con las cuatro esquinas cortadas en [ch]. */
private fun ochavado(r: Rect, ch: Float): Path = Path().apply {
    moveTo(r.left + ch, r.top); lineTo(r.right - ch, r.top); lineTo(r.right, r.top + ch); lineTo(r.right, r.bottom - ch)
    lineTo(r.right - ch, r.bottom); lineTo(r.left + ch, r.bottom); lineTo(r.left, r.bottom - ch); lineTo(r.left, r.top + ch); close()
}

/** Un tubo de neón por [p]: el halo ancho, el brillo, el color y el alma casi blanca (sin degradados). */
private fun DrawScope.tuboNeon(p: Path, color: Color, grosor: Float) {
    fun trazo(g: Float) = Stroke(g, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(p, color.copy(alpha = 0.14f), style = trazo(grosor * 4f))
    drawPath(p, color.copy(alpha = 0.36f), style = trazo(grosor * 2.2f))
    drawPath(p, color, style = trazo(grosor))
    drawPath(p, mezcla(color, Color.White, 0.7f), style = trazo(grosor * 0.35f))
}

/**
 * Distopía: los logros del archivo central. El trofeo descansa en el zócalo de hormigón del archivo, y en el hall cada pieza
 * se expone sobre el cajón de un archivero, con su ficha del registro.
 */
object LogrosDistopia : DisenoLogros {
    private val ACERO = Color(0xFF6F7479)
    private val ACERO_OSC = Color(0xFF43474B)
    private val ACERO_LUZ = Color(0xFF9BA1A6)
    private val ROJO = Color(0xFFA8201C)
    private val NEGRO = Color(0xFF141516)
    private val PAPEL = Color(0xFFE9E4D6)
    /** El papel en oscuro: el papel carbón de la copia, hundido. */
    private val CARBON = Color(0xFF2A2C2F)
    private val HORMIGON = Color(0xFF9A9890)
    private val HORMIGON_OSC = Color(0xFF6C6A64)

    /** El color en oscuro: hundido hacia el negro del archivo. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0C0D), 0.4f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El zócalo del archivo: un bloque de hormigón vaciado, con las juntas del encofrado, sus poros y el número de inventario
     * estarcido; la franja roja de «clasificado» por su canto de arriba; encima, la plancha de acero con su cara de luz y sus
     * tornillos del nivel, y al pie la solera de acero. Al frente, el tarjetero en el metal del nivel con la ficha del registro.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val bloque = fr(0.16f, 0.8f, 0.84f, 0.935f)
        val solera = fr(0.13f, 0.935f, 0.87f, 0.955f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tapa); addRect(bloque); addRect(solera) }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.08f, 0.92f, oscuro)
        // El hormigón vaciado: la cara de la derecha en sombra, el canto de la izquierda con luz y las juntas del encofrado.
        drawRect(t(HORMIGON, oscuro), bloque.topLeft, bloque.size)
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(bloque.right - w * 0.05f, bloque.top), Size(w * 0.05f, bloque.height))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.14f), bloque.topLeft, Size(w * 0.025f, bloque.height))
        val junta = t(HORMIGON_OSC, oscuro).copy(alpha = 0.6f)
        drawLine(junta, pt(0.16f, 0.875f), pt(0.84f, 0.875f), lw)
        if (fino) {
            listOf(0.45f, 0.79f).forEach { x -> drawLine(junta, pt(x, 0.818f), pt(x, 0.935f), lw * 0.8f) }
            for (k in 0 until if (muyFino) 28 else 14) {
                val x = bloque.left + bloque.width * azar(e.semilla, k)
                val y = bloque.top + bloque.height * (0.18f + 0.78f * azar(e.semilla, k + 50))
                drawCircle(junta, (0.35f + 0.6f * azar(e.semilla, k + 90)) * u, Offset(x, y))
            }
        }
        // La franja roja de «clasificado».
        drawRect(t(ROJO, oscuro), bloque.topLeft, Size(bloque.width, h * 0.016f))
        // El número de inventario estarcido: cuatro cifras partidas por el puente de la plantilla.
        val num = t(NEGRO, oscuro).copy(alpha = if (oscuro) 0.8f else 0.62f)
        for (k in 0 until 4) {
            val x = w * (0.205f + k * 0.055f); val cw = w * 0.036f
            val hueca = azar(e.semilla, k + 7) > 0.45f
            if (hueca && fino) {
                drawRect(num, Offset(x, h * 0.887f), Size(cw, h * 0.017f), style = Stroke(cw * 0.3f))
                drawRect(num, Offset(x, h * 0.909f), Size(cw, h * 0.016f), style = Stroke(cw * 0.3f))
            } else {
                drawRect(num, Offset(x, h * 0.886f), Size(cw, h * 0.019f))
                drawRect(num, Offset(x, h * 0.909f), Size(cw, h * 0.017f))
            }
        }
        // El tarjetero del nivel con la ficha (a lienzo chico, solo la chapa).
        val tj = fr(0.52f, 0.826f, 0.77f, 0.914f)
        chapaNivel(tj, e, h * 0.004f)
        if (fino) {
            val ficha = Rect(tj.left + w * 0.016f, tj.top + h * 0.01f, tj.right - w * 0.016f, tj.bottom - h * 0.024f)
            drawRect(t(PAPEL, oscuro), ficha.topLeft, ficha.size)
            drawRect(t(ROJO, oscuro), ficha.topLeft, Size(ficha.width, ficha.height * 0.16f))
            for (k in 0 until 2) drawLine(t(NEGRO, oscuro).copy(alpha = 0.5f), Offset(ficha.left + ficha.width * 0.12f, ficha.top + ficha.height * (0.45f + k * 0.28f)),
                Offset(ficha.right - ficha.width * (0.12f + k * 0.28f), ficha.top + ficha.height * (0.45f + k * 0.28f)), lw * 0.8f)
            // El labio del tarjetero, que sujeta la ficha por abajo.
            drawRect(e.base, Offset(tj.left, ficha.bottom), Size(tj.width, tj.bottom - ficha.bottom))
            drawRect(e.brillo, Offset(tj.left, ficha.bottom), Size(tj.width, maxOf(h * 0.003f, 0.45f * u)))
            drawRect(e.metalOscuro, tj.topLeft, tj.size, style = Stroke(maxOf(h * 0.0025f, 0.45f * u)))
        }
        // La plancha de acero: su cara de arriba con luz, el frente y el canto en sombra.
        drawRect(t(ACERO, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.3f))
        drawRect(t(ACERO_OSC, oscuro), Offset(tapa.left, tapa.bottom - tapa.height * 0.18f), Size(tapa.width, tapa.height * 0.18f))
        if (fino) (if (muyFino) listOf(0.16f, 0.38f, 0.62f, 0.84f) else listOf(0.17f, 0.83f)).forEach { x ->
            val c = pt(x, 0.787f); val r = h * 0.0075f
            remache(c, r, e.base, e.brillo)
            if (muyFino) drawLine(e.metalOscuro, Offset(c.x - r * 0.7f, c.y), Offset(c.x + r * 0.7f, c.y), lw * 0.6f)
        }
        // La solera de acero, al pie.
        drawRect(t(ACERO_OSC, oscuro), solera.topLeft, solera.size)
        drawRect(t(ACERO_LUZ, oscuro).copy(alpha = 0.6f), solera.topLeft, Size(solera.width, maxOf(h * 0.003f, 0.5f * u)))
    }

    /**
     * El archivero del hall: el cajón de arriba de un archivero de acero, con su tapa de canto, la franja roja, el tarjetero del
     * nivel con su ficha, la jaladera, la cerradura y las juntas de los costados.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tapa = fr(0.03f, 0f, 0.97f, 0.18f)
        val cuerpo = fr(0.07f, 0.18f, 0.93f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(tapa); addRect(cuerpo) }, siluetaFuturo(oscuro)); return }
        drawRect(t(ACERO, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(cuerpo.right - w * 0.04f, cuerpo.top), Size(w * 0.04f, cuerpo.height))
        // El frente del cajón, con su bisel.
        val frente = fr(0.11f, 0.32f, 0.89f, 0.92f)
        drawRect(t(mezcla(ACERO, ACERO_LUZ, 0.35f), oscuro), frente.topLeft, frente.size)
        drawRect(t(ACERO_LUZ, oscuro), frente.topLeft, Size(frente.width, lw))
        drawRect(t(ACERO_OSC, oscuro), Offset(frente.left, frente.bottom - lw), Size(frente.width, lw))
        // La franja roja bajo la tapa.
        drawRect(t(ROJO, oscuro), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, h * 0.1f))
        // El tarjetero del nivel, con su ficha, y la jaladera debajo.
        val tj = fr(0.4f, 0.38f, 0.6f, 0.62f)
        chapaNivel(tj, e, u * 0.6f)
        drawRect(t(PAPEL, oscuro), Offset(tj.left + tj.width * 0.12f, tj.top + tj.height * 0.2f), Size(tj.width * 0.76f, tj.height * 0.5f))
        val jal = fr(0.43f, 0.7f, 0.57f, 0.84f)
        drawRoundRect(t(ACERO_OSC, oscuro), jal.topLeft, jal.size, CornerRadius(jal.height / 2))
        drawLine(t(ACERO_LUZ, oscuro), Offset(jal.left + jal.width * 0.15f, jal.top + lw), Offset(jal.right - jal.width * 0.15f, jal.top + lw), lw)
        // La cerradura y las juntas.
        drawCircle(t(ACERO_OSC, oscuro), h * 0.07f, pt(0.82f, 0.48f))
        drawLine(t(ACERO_LUZ, oscuro), pt(0.82f, 0.44f), pt(0.82f, 0.52f), lw * 0.8f)
        listOf(0.09f, 0.91f).forEach { x -> drawLine(t(ACERO_OSC, oscuro), pt(x, 0.2f), pt(x, 1f), lw * 0.8f) }
        // La tapa con su canto.
        drawRect(t(ACERO, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(ACERO_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.45f))
        drawRect(t(ACERO_OSC, oscuro), Offset(tapa.left, tapa.bottom - lw), Size(tapa.width, lw))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E1D3) else NEGRO
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFF07870) else ROJO

    /**
     * La ficha del registro: la tarjeta de papel en su marco de chapa del nivel, sujeta por las cuatro esquineras y con la raya
     * roja del encabezado.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(2 * u)); return }
        // El marco de chapa.
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(2 * u))
        drawRect(e.brillo, Offset(1.5f * u, 0.6f * u), Size(w - 3 * u, 0.9f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(2 * u), style = Stroke(0.8f * u))
        // La ficha.
        val b = 2.6f * u
        drawRect(if (oscuro) CARBON else PAPEL, Offset(b, b), Size(w - 2 * b, h - 2 * b))
        drawLine(e.metalOscuro.copy(alpha = 0.5f), Offset(b, b + 0.4f * u), Offset(w - b, b + 0.4f * u), 0.8f * u)
        drawLine(if (oscuro) Color(0xFFC0443C) else ROJO, Offset(b + 5 * u, b + 2.2f * u), Offset(w - b - 5 * u, b + 2.2f * u), 0.9f * u)
        // Las cuatro esquineras.
        val es = 5.5f * u
        listOf(Offset(0f, 0f) to Offset(1f, 1f), Offset(w, 0f) to Offset(-1f, 1f), Offset(0f, h) to Offset(1f, -1f), Offset(w, h) to Offset(-1f, -1f)).forEach { (o, s) ->
            val p = Path().apply { moveTo(o.x + s.x * b, o.y + s.y * b); lineTo(o.x + s.x * (b + es), o.y + s.y * b); lineTo(o.x + s.x * b, o.y + s.y * (b + es)); close() }
            drawPath(p, e.base)
            drawPath(p, e.metalOscuro, style = Stroke(0.6f * u))
        }
    }
}

/**
 * Ciudad neón (solo existe en oscuro): los logros del callejón. El trofeo descansa en la caja de cerveza de plástico, de
 * cabeza, que sirve de banco en los puestos; en el hall, sobre la caja de luz del letrero, y su placa es el vidrio negro del
 * quiosco con un hilo de neón.
 */
object LogrosNeon : DisenoLogros {
    private val CARCASA = Color(0xFF221C33)
    private val CARCASA_LUZ = Color(0xFF3B3354)
    private val ALAMBRE = Color(0xFFB9C1CE)
    private val MAGENTA = Color(0xFFFF2E8C)
    private val CIAN = Color(0xFF2EE8E0)
    private val VIDRIO = Color(0xFF05040A)
    private val PLASTICO = Color(0xFF2F6B3A)
    private val PLASTICO_LUZ = Color(0xFF4A8C52)
    private val PLASTICO_OSC = Color(0xFF173A1E)
    private val CHARCO = Color(0xFF0D0A18)

    /** El color bajo la luz del callejón: siempre hundido (el tema solo es oscuro). Los neones no pasan por aquí. */
    @Suppress("UNUSED_PARAMETER")
    private fun t(c: Color, oscuro: Boolean): Color = mezcla(c, Color(0xFF06050B), 0.3f)

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El banco del puesto: la caja de cerveza de plástico verde puesta de cabeza, con su borde, los nervios de las esquinas y la
     * rejilla calada; por el canto de arriba corre un tubo magenta entre sus dos electrodos del nivel, y al frente va
     * remachada la chapa del permiso del puesto en el metal del nivel. Abajo, el charco del asfalto que devuelve el neón.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val caja = fr(0.15f, 0.778f, 0.85f, 0.945f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), caja.topLeft, caja.size, CornerRadius(w * 0.018f)); return }
        // El charco, con la sombra de la caja y el reflejo del tubo.
        drawOval(CHARCO, Offset(w * 0.05f, h * 0.932f), Size(w * 0.9f, h * 0.042f))
        drawOval(Color.Black.copy(alpha = 0.5f), Offset(w * 0.12f, h * 0.938f), Size(w * 0.76f, h * 0.02f))
        if (fino) for (k in 0 until 3) drawLine(MAGENTA.copy(alpha = 0.3f - k * 0.08f), pt(0.24f + k * 0.07f, 0.958f + k * 0.006f), pt(0.76f - k * 0.07f, 0.958f + k * 0.006f), lw)
        // La caja, con su borde de arriba (el fondo, de cabeza) y el de abajo.
        val verde = t(PLASTICO, true); val osc = t(PLASTICO_OSC, true); val luz = t(PLASTICO_LUZ, true)
        drawRoundRect(verde, caja.topLeft, caja.size, CornerRadius(w * 0.018f))
        drawRect(luz, Offset(caja.left + w * 0.012f, caja.top), Size(caja.width - w * 0.024f, h * 0.01f))
        drawRect(osc, Offset(caja.left + w * 0.01f, caja.bottom - h * 0.016f), Size(caja.width - w * 0.02f, h * 0.016f))
        listOf(caja.left + w * 0.018f, caja.right - w * 0.048f).forEach { x -> drawRect(osc.copy(alpha = 0.55f), Offset(x, caja.top + h * 0.01f), Size(w * 0.03f, caja.height - h * 0.026f)) }
        // El reflejo magenta del letrero en la cara de arriba.
        drawRect(MAGENTA.copy(alpha = 0.13f), caja.topLeft, Size(caja.width, h * 0.05f))
        // La rejilla calada (a lienzo chico, una sola hilera de huecos).
        val hueco = Color(0xFF07120A)
        val filas = if (fino) 2 else 1; val cols = if (muyFino) 7 else if (fino) 6 else 4
        val gx0 = 0.22f; val gx1 = 0.78f; val gy0 = 0.83f; val gy1 = 0.918f
        val cw = (gx1 - gx0) / cols; val chh = (gy1 - gy0) / filas
        for (f in 0 until filas) for (c in 0 until cols) {
            val r = fr(gx0 + c * cw + cw * 0.16f, gy0 + f * chh + chh * 0.16f, gx0 + (c + 1) * cw - cw * 0.16f, gy0 + (f + 1) * chh - chh * 0.16f)
            drawRoundRect(hueco, r.topLeft, r.size, CornerRadius(minOf(r.width, r.height) * 0.3f))
            if (fino) drawLine(MAGENTA.copy(alpha = 0.22f), r.topLeft, Offset(r.right, r.top), lw * 0.7f)
        }
        // La chapa del permiso del puesto, en el metal del nivel, con sus dos remaches.
        val pl = fr(0.57f, 0.852f, 0.76f, 0.908f)
        chapaNivel(pl, e, h * 0.004f)
        if (fino) {
            listOf(pl.left + pl.width * 0.12f, pl.right - pl.width * 0.12f).forEach { x -> drawCircle(e.metalOscuro, pl.height * 0.1f, Offset(x, pl.center.y)) }
            for (k in 0 until 3) drawRect(e.metalOscuro.copy(alpha = 0.7f), Offset(pl.left + pl.width * (0.28f + k * 0.16f), pl.top + pl.height * 0.32f), Size(pl.width * 0.1f, pl.height * 0.36f))
        }
        // El tubo de neón, con sus soportes de alambre y sus dos electrodos del nivel.
        val yT = h * 0.806f
        if (fino) listOf(0.36f, 0.64f).forEach { x -> drawLine(t(ALAMBRE, true), Offset(w * x, yT), Offset(w * x, yT + h * 0.018f), lw) }
        tuboNeon(Path().apply { moveTo(w * 0.24f, yT); lineTo(w * 0.76f, yT) }, MAGENTA, maxOf(h * 0.0065f, 0.7f * u))
        listOf(0.17f, 0.76f).forEach { x -> chapaNivel(fr(x, 0.794f, x + 0.07f, 0.818f), e, h * 0.004f) }
    }

    /**
     * La caja de luz del callejón: el letrero bajo de chapa violeta con su vidrio negro, el tubo cian que lo recorre en zigzag,
     * el piloto magenta y los dos soportes del nivel que lo atornillan a la repisa.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cuerpo = fr(0.04f, 0.08f, 0.96f, 0.82f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f)); return }
        // Los soportes del nivel, al pie.
        listOf(0.1f, 0.78f).forEach { x -> chapaNivel(fr(x, 0.7f, x + 0.12f, 1f), e, h * 0.05f) }
        drawRoundRect(t(CARCASA, true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f))
        drawRoundRect(t(CARCASA_LUZ, true), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.14f), style = Stroke(maxOf(h * 0.04f, 0.6f * u)))
        val vidrio = fr(0.09f, 0.22f, 0.91f, 0.68f)
        drawRoundRect(VIDRIO, vidrio.topLeft, vidrio.size, CornerRadius(h * 0.08f))
        // El tubo en zigzag.
        val n = 8
        val zig = Path().apply {
            for (k in 0..n) {
                val x = w * (0.14f + 0.62f * k / n); val y = h * (if (k % 2 == 0) 0.34f else 0.56f)
                if (k == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        tuboNeon(zig, CIAN, maxOf(h * 0.045f, 0.7f * u))
        // El piloto magenta y los tornillos.
        drawCircle(MAGENTA.copy(alpha = 0.3f), h * 0.12f, pt(0.85f, 0.45f))
        drawCircle(MAGENTA, h * 0.06f, pt(0.85f, 0.45f))
        listOf(0.08f, 0.92f).forEach { x -> remache(pt(x, 0.45f), h * 0.04f, t(ALAMBRE, true), Color.White) }
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFE6FFFC)
    override fun rotuloPlaca(oscuro: Boolean): Color = Color(0xFFFF8CC2)

    /**
     * La etiqueta del quiosco: el vidrio negro en su chapa del nivel y su carcasa violeta, con un hilo de neón magenta corriendo
     * por dentro del borde.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(true), Offset.Zero, size, CornerRadius(4 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(4 * u))
        drawRect(e.brillo, Offset(3 * u, 0.6f * u), Size(w - 6 * u, 0.9f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        val b1 = 1.8f * u
        drawRoundRect(t(CARCASA, true), Offset(b1, b1), Size(w - 2 * b1, h - 2 * b1), CornerRadius(3 * u))
        val b2 = 3 * u
        drawRoundRect(VIDRIO, Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2), CornerRadius(2.2f * u))
        val b3 = 5 * u
        if (w > 2 * b3 + 4 * u && h > 2 * b3 + 4 * u)
            tuboNeon(Path().apply { addRoundRect(RoundRect(Rect(b3, b3, w - b3, h - b3), CornerRadius(1.6f * u))) }, MAGENTA, 0.75f * u)
    }
}

/**
 * Refugio: los logros del refugio. El trofeo descansa en una caja de suministros de chapa; en el hall, sobre la consola de la
 * terminal, y su placa es la pantalla de fósforo.
 */
object LogrosRefugio : DisenoLogros {
    private val MURO = Color(0xFFD9CFAF)
    private val TUBO_OSC = Color(0xFF4E5557)
    private val AMARILLO = Color(0xFFE5B436)
    private val FOSFORO = Color(0xFF7DFF8C)
    private val PANTALLA = Color(0xFF041208)
    private val CHAPA = Color(0xFF1C6E6A)
    private val CHAPA_LUZ = Color(0xFF3C928B)
    private val CHAPA_OSC = Color(0xFF124A47)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0A0F0F), 0.36f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La caja de suministros: chapa verde azulado con su tapa, dos nervaduras estampadas, esquineras de acero remachadas, los
     * patines al pie y la placa amarilla con el engrane del refugio; los dos cierres de palanca van en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val cuerpo = fr(0.14f, 0.8f, 0.86f, 0.942f)
        val patines = listOf(fr(0.18f, 0.942f, 0.36f, 0.955f), fr(0.64f, 0.942f, 0.82f, 0.955f))
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tapa); addRect(cuerpo); patines.forEach { addRect(it) } }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.1f, 0.9f, oscuro)
        val acero = t(TUBO_OSC, oscuro)
        patines.forEach { drawRect(acero, it.topLeft, it.size) }
        // El cuerpo, con la cara de la derecha en sombra y las dos nervaduras.
        drawRect(t(CHAPA, oscuro), cuerpo.topLeft, cuerpo.size)
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(cuerpo.right - w * 0.05f, cuerpo.top), Size(w * 0.05f, cuerpo.height))
        listOf(0.848f, 0.902f).forEach { y ->
            drawRect(t(CHAPA_LUZ, oscuro), Offset(cuerpo.left, h * y), Size(cuerpo.width, h * 0.008f))
            drawRect(t(CHAPA_OSC, oscuro), Offset(cuerpo.left, h * (y + 0.008f)), Size(cuerpo.width, h * 0.006f))
        }
        // Las esquineras de acero, con su remache.
        val gx = w * 0.07f; val gy = h * 0.04f; val gg = maxOf(w * 0.022f, 0.8f * u)
        listOf(cuerpo.topLeft to Offset(1f, 1f), Offset(cuerpo.right, cuerpo.top) to Offset(-1f, 1f),
            Offset(cuerpo.left, cuerpo.bottom) to Offset(1f, -1f), cuerpo.bottomRight to Offset(-1f, -1f)).forEach { (o, s) ->
            drawRect(acero, Offset(if (s.x > 0) o.x else o.x - gx, if (s.y > 0) o.y else o.y - gg), Size(gx, gg))
            drawRect(acero, Offset(if (s.x > 0) o.x else o.x - gg, if (s.y > 0) o.y else o.y - gy), Size(gg, gy))
            if (fino) remache(Offset(o.x + s.x * gg * 0.5f, o.y + s.y * gg * 0.5f), gg * 0.32f, t(MURO, oscuro), Color.White.copy(alpha = 0.7f))
        }
        // La placa amarilla con el engrane del refugio.
        val pl = fr(0.4f, 0.826f, 0.6f, 0.91f)
        drawRoundRect(t(AMARILLO, oscuro), pl.topLeft, pl.size, CornerRadius(w * 0.008f))
        if (fino) {
            drawPath(engrane(pl.center, pl.height * 0.34f, 8), acero)
            drawCircle(t(AMARILLO, oscuro), pl.height * 0.15f, pl.center)
            drawRoundRect(acero, pl.topLeft, pl.size, CornerRadius(w * 0.008f), style = Stroke(lw))
        } else drawRect(acero, Offset(pl.left + pl.width * 0.25f, pl.center.y - h * 0.008f), Size(pl.width * 0.5f, h * 0.016f))
        // La tapa: un poco más ancha, con su cara de luz.
        drawRect(t(CHAPA_OSC, oscuro), tapa.topLeft, tapa.size)
        drawRect(t(CHAPA_LUZ, oscuro), tapa.topLeft, Size(tapa.width, tapa.height * 0.35f))
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(tapa.left, tapa.bottom - lw), Size(tapa.width, lw))
        // Los cierres de palanca, en el metal del nivel.
        listOf(0.23f, 0.71f).forEach { x ->
            val c = fr(x, 0.785f, x + 0.06f, 0.84f)
            chapaNivel(c, e, w * 0.006f)
            if (fino) {
                drawLine(e.metalOscuro, Offset(c.center.x, c.top + c.height * 0.3f), Offset(c.center.x, c.bottom - c.height * 0.12f), lw * 1.2f, StrokeCap.Round)
                drawCircle(e.brillo, c.width * 0.16f, Offset(c.center.x, c.top + c.height * 0.24f))
            }
        }
        if (muyFino) listOf(0.22f, 0.78f).forEach { x -> drawLine(t(AMARILLO, oscuro).copy(alpha = 0.8f), pt(x - 0.02f, 0.925f), pt(x + 0.02f, 0.925f), lw * 1.4f) }
    }

    /**
     * La consola de la terminal del supervisor: el mueble de chapa crema con la pantalla de fósforo encendida, el piloto ámbar,
     * la rejilla, sus tornillos y la placa del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val cuerpo = fr(0.03f, 0.04f, 0.97f, 0.96f)
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.16f)); return }
        drawRoundRect(t(MURO, oscuro), cuerpo.topLeft, cuerpo.size, CornerRadius(h * 0.16f))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.35f), Offset(cuerpo.left + h * 0.16f, cuerpo.top + lw * 0.5f), Size(cuerpo.width - h * 0.32f, lw))
        drawRect(Color.Black.copy(alpha = 0.15f), Offset(cuerpo.left + h * 0.16f, cuerpo.bottom - lw * 1.5f), Size(cuerpo.width - h * 0.32f, lw))
        // La pantalla, con sus renglones de fósforo y sus líneas de barrido.
        val pan = fr(0.08f, 0.2f, 0.5f, 0.8f)
        drawRoundRect(PANTALLA, pan.topLeft, pan.size, CornerRadius(h * 0.14f))
        drawPath(Path().apply { moveTo(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.22f); lineTo(pan.left + pan.width * 0.17f, pan.top + pan.height * 0.36f)
            lineTo(pan.left + pan.width * 0.1f, pan.top + pan.height * 0.5f) }, FOSFORO, style = Stroke(lw, cap = StrokeCap.Round))
        listOf(0.36f to 0.82f, 0.66f to 0.6f).forEach { (y, x1) -> drawLine(FOSFORO.copy(alpha = 0.8f), Offset(pan.left + pan.width * 0.25f, pan.top + pan.height * y),
            Offset(pan.left + pan.width * x1, pan.top + pan.height * y), lw * 1.2f) }
        var y = pan.top + h * 0.04f; var k = 0
        val paso = maxOf(h * 0.1f, 1f)
        while (y < pan.bottom - h * 0.03f && k < 40) { drawLine(FOSFORO.copy(alpha = 0.07f), Offset(pan.left + h * 0.05f, y), Offset(pan.right - h * 0.05f, y), lw * 0.6f); y += paso; k++ }
        // El piloto ámbar y la placa del nivel.
        drawCircle(AMARILLO.copy(alpha = 0.3f), h * 0.13f, pt(0.59f, 0.34f))
        drawCircle(AMARILLO, h * 0.07f, pt(0.59f, 0.34f))
        chapaNivel(fr(0.54f, 0.56f, 0.64f, 0.8f), e, u * 0.6f)
        // La rejilla.
        for (j in 0 until 6) drawRoundRect(t(TUBO_OSC, oscuro).copy(alpha = 0.75f), pt(0.7f + j * 0.037f, 0.24f), Size(w * 0.018f, h * 0.52f), CornerRadius(w * 0.009f))
        listOf(0.06f, 0.94f).forEach { x -> listOf(0.22f, 0.78f).forEach { yy -> remache(pt(x, yy), h * 0.04f, t(TUBO_OSC, oscuro), Color.White.copy(alpha = 0.5f)) } }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = FOSFORO
    override fun rotuloPlaca(oscuro: Boolean): Color = AMARILLO

    /** La pantalla de fósforo de la terminal: el bisel de chapa crema con el filo del nivel y la pantalla de esquinas redondas. */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(5 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(5 * u))
        drawRect(e.brillo, Offset(4 * u, 0.5f * u), Size(w - 8 * u, 0.8f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(5 * u), style = Stroke(0.8f * u))
        val b1 = 1.6f * u
        drawRoundRect(t(MURO, oscuro), Offset(b1, b1), Size(w - 2 * b1, h - 2 * b1), CornerRadius(4 * u))
        drawRect(Color.Black.copy(alpha = 0.12f), Offset(b1 + 3 * u, h - b1 - 0.9f * u), Size(w - 2 * b1 - 6 * u, 0.8f * u))
        val b2 = 3.6f * u
        drawRoundRect(PANTALLA, Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2), CornerRadius(4 * u))
    }
}

/**
 * Orden: los logros de la orden. El trofeo descansa en la peana de acero de la sala capitular, con su colgadura carmesí; en el
 * hall, sobre un capitel remachado, y su placa es la plancha ochavada del archivo sellado.
 */
object LogrosOrden : DisenoLogros {
    private val ACERO = Color(0xFF3C424A)
    private val ACERO_LUZ = Color(0xFF6A727C)
    private val ACERO_OSC = Color(0xFF262A30)
    private val LATON = Color(0xFFB8933E)
    private val LATON_LUZ = Color(0xFFE0C070)
    private val ESMALTE = Color(0xFF0C0D10)
    private val CARMESI = Color(0xFF9E1B22)
    private val CARMESI_OSC = Color(0xFF6A1016)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.35f) else c

    /** El latón: se hunde menos que el acero, para que siga brillando en oscuro. */
    private fun placa(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF09090B), 0.16f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La peana de la sala capitular: tres cuerpos de acero (la cornisa con su filete de latón, el dado remachado y la basa en dos
     * gradas) y la colgadura carmesí que cae por el frente desde su barra de latón, con sus pliegues, su fleco y la punta en
     * cola de golondrina; sobre el paño, el engrane de la Orden en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cornisa = fr(0.12f, 0.765f, 0.88f, 0.795f)
        val dado = fr(0.18f, 0.795f, 0.82f, 0.925f)
        val grada = fr(0.15f, 0.925f, 0.85f, 0.94f)
        val basa = fr(0.12f, 0.94f, 0.88f, 0.955f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(cornisa); addRect(dado); addRect(grada); addRect(basa) }, siluetaFuturo(oscuro)); return
        }
        sombraPie(0.08f, 0.92f, oscuro)
        // La basa y el dado, con la cara de la derecha en sombra.
        drawRect(t(ACERO_OSC, oscuro), basa.topLeft, basa.size)
        drawRect(t(ACERO, oscuro), grada.topLeft, grada.size)
        drawRect(t(ACERO_LUZ, oscuro), grada.topLeft, Size(grada.width, maxOf(h * 0.003f, 0.5f * u)))
        drawRect(t(ACERO, oscuro), dado.topLeft, dado.size)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(dado.right - w * 0.05f, dado.top), Size(w * 0.05f, dado.height))
        drawRect(t(ACERO_LUZ, oscuro).copy(alpha = 0.5f), dado.topLeft, Size(w * 0.02f, dado.height))
        // Los remaches del dado, a los dos lados del paño.
        if (fino) (if (muyFino) listOf(0.825f, 0.86f, 0.895f) else listOf(0.84f, 0.895f)).forEach { y ->
            listOf(0.23f, 0.77f).forEach { x -> remache(pt(x, y), h * 0.006f, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.55f)) }
        }
        // La colgadura carmesí, con sus pliegues y su fleco de latón.
        val pano = poli(0.35f, 0.8f, 0.65f, 0.8f, 0.64f, 0.906f, 0.5f, 0.884f, 0.36f, 0.906f)
        drawPath(pano, t(CARMESI, oscuro))
        drawPath(poli(0.58f, 0.8f, 0.65f, 0.8f, 0.64f, 0.906f, 0.6f, 0.9f), Color.Black.copy(alpha = 0.18f))
        if (fino) {
            listOf(0.43f, 0.57f).forEach { x -> drawLine(t(CARMESI_OSC, oscuro), pt(x, 0.81f), pt(x + (x - 0.5f) * 0.1f, 0.89f), lw) }
            val n = if (muyFino) 9 else 6
            for (k in 0..n) {
                val f = k / n.toFloat()
                val x = 0.36f + 0.28f * f; val y = if (f < 0.5f) 0.906f - 0.044f * f else 0.884f + 0.044f * (f - 0.5f)
                drawLine(placa(LATON, oscuro), pt(x, y), pt(x, y + 0.014f), lw)
            }
        }
        // El engrane de la Orden, en el metal del nivel.
        val c = pt(0.5f, 0.84f); val r = h * 0.028f
        drawPath(engrane(c, r, 8), e.base)
        if (fino) drawPath(engrane(c, r, 8), e.metalOscuro, style = Stroke(lw * 0.8f))
        drawCircle(e.brillo, r * 0.62f, c)
        drawCircle(t(CARMESI, oscuro), r * 0.34f, c)
        if (e.nivel >= 4 && fino) drawPath(estrella(Offset(c.x + r * 0.9f, c.y - r * 0.9f), r * 0.5f, 4, 0.25f), Color.White.copy(alpha = 0.9f))
        // La barra de latón de la colgadura, con sus remates.
        drawRect(placa(LATON, oscuro), pt(0.33f, 0.795f), Size(w * 0.34f, h * 0.01f))
        listOf(0.33f, 0.67f).forEach { x -> drawCircle(placa(LATON_LUZ, oscuro), h * 0.007f, pt(x, 0.8f)) }
        // La cornisa, con su cara de luz y su filete de latón.
        drawRect(t(ACERO, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ACERO_LUZ, oscuro), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.38f))
        drawRect(placa(LATON, oscuro), Offset(cornisa.left, cornisa.bottom - maxOf(h * 0.005f, 0.6f * u)), Size(cornisa.width, maxOf(h * 0.005f, 0.6f * u)))
        if (muyFino) for (k in 0 until 9) drawCircle(t(ACERO_OSC, oscuro), h * 0.003f, pt(0.16f + k * 0.085f, 0.782f))
    }

    /**
     * El capitel de acero de la sala: el bloque remachado con su cornisa y el filete de latón, el paño carmesí al centro y la
     * cruz de la Orden en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val cornisa = fr(0.02f, 0f, 0.98f, 0.2f)
        val bloque = fr(0.06f, 0.2f, 0.94f, 1f)
        if (!e.hecho) { drawPath(Path().apply { addRect(cornisa); addRect(bloque) }, siluetaFuturo(oscuro)); return }
        drawRect(t(ACERO, oscuro), bloque.topLeft, bloque.size)
        drawRect(Color.Black.copy(alpha = 0.22f), Offset(bloque.right - w * 0.04f, bloque.top), Size(w * 0.04f, bloque.height))
        drawRect(t(ACERO_OSC, oscuro), Offset(bloque.left, bloque.bottom - h * 0.12f), Size(bloque.width, h * 0.12f))
        listOf(0.12f, 0.24f, 0.76f, 0.88f).forEach { x -> remache(pt(x, 0.55f), h * 0.07f, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.5f)) }
        // El paño carmesí.
        drawPath(poli(0.38f, 0.2f, 0.62f, 0.2f, 0.62f, 0.94f, 0.5f, 0.8f, 0.38f, 0.94f), t(CARMESI, oscuro))
        listOf(0.44f, 0.56f).forEach { x -> drawLine(t(CARMESI_OSC, oscuro), pt(x, 0.24f), pt(x, 0.8f), lw * 0.7f) }
        // La cruz de la Orden, en el metal del nivel.
        val c = pt(0.5f, 0.48f); val r = h * 0.22f
        drawPath(cruzPatada(c, r), e.base)
        drawPath(cruzPatada(c, r), e.metalOscuro, style = Stroke(lw * 0.6f))
        drawCircle(e.brillo, r * 0.22f, c)
        // La cornisa con su filete.
        drawRect(t(ACERO, oscuro), cornisa.topLeft, cornisa.size)
        drawRect(t(ACERO_LUZ, oscuro), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.45f))
        drawRect(placa(LATON, oscuro), Offset(cornisa.left, cornisa.bottom - lw), Size(cornisa.width, lw))
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFEDE6D2)
    override fun rotuloPlaca(oscuro: Boolean): Color = LATON_LUZ

    /**
     * La plancha ochavada del archivo sellado: el filete del nivel con las esquinas cortadas, la banda de acero con un remache en
     * cada esquina y el hueco de esmalte negro donde va el nombre.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val todo = Rect(0f, 0f, w, h)
        if (!e.hecho) { drawPath(ochavado(todo, 5 * u), siluetaFuturo(oscuro)); return }
        drawPath(ochavado(todo, 5 * u), e.base)
        drawLine(e.brillo, Offset(5 * u, 0.8f * u), Offset(w - 5 * u, 0.8f * u), 0.9f * u)
        drawPath(ochavado(todo, 5 * u), e.metalOscuro, style = Stroke(0.8f * u))
        val b1 = 1.4f * u
        drawPath(ochavado(Rect(b1, b1, w - b1, h - b1), 4.4f * u), t(ACERO, oscuro))
        val b2 = 3.4f * u
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), ESMALTE)
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), t(ACERO_LUZ, oscuro).copy(alpha = 0.5f), style = Stroke(0.6f * u))
        listOf(Offset(4.6f * u, 2.4f * u), Offset(w - 4.6f * u, 2.4f * u), Offset(4.6f * u, h - 2.4f * u), Offset(w - 4.6f * u, h - 2.4f * u))
            .forEach { remache(it, 0.8f * u, t(ACERO_LUZ, oscuro), Color.White.copy(alpha = 0.55f)) }
    }
}

/**
 * Laboratorio: los logros del laboratorio. El trofeo descansa en el zócalo del escaparate de muestras; en el hall, sobre el gato
 * de laboratorio, y su placa es la etiqueta de la muestra.
 */
object LogrosLaboratorio : DisenoLogros {
    private val GRIS = Color(0xFFAFB9C1)
    private val GRIS_OSC = Color(0xFF6B7781)
    private val GRIS_LUZ = Color(0xFFCDD5DB)
    private val CIAN = Color(0xFF1FB5C9)
    private val CIAN_OSC = Color(0xFF0B6E7C)
    private val ROJO = Color(0xFFD8312A)
    private val TINTA = Color(0xFF12202B)
    private val BLANCO = Color(0xFFF2F5F7)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0E12), 0.38f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El zócalo del escaparate de muestras: la cubierta blanca de resina con su canto cian, el gabinete de acero con dos cajones y
     * sus jaladeras en el metal del nivel, la etiqueta de inventario con su código de barras y su punto rojo de calibración, el
     * rodapié y las patas niveladoras.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cubierta = fr(0.11f, 0.765f, 0.89f, 0.8f)
        val gabinete = fr(0.16f, 0.8f, 0.84f, 0.935f)
        val rodapie = fr(0.18f, 0.935f, 0.82f, 0.946f)
        val patas = listOf(fr(0.2f, 0.946f, 0.26f, 0.955f), fr(0.74f, 0.946f, 0.8f, 0.955f))
        if (!e.hecho) {
            drawPath(Path().apply { addRoundRect(RoundRect(cubierta, CornerRadius(w * 0.012f))); addRect(gabinete); addRect(rodapie); patas.forEach { addRect(it) } },
                siluetaFuturo(oscuro)); return
        }
        sombraPie(0.1f, 0.9f, oscuro)
        patas.forEach { drawRect(t(GRIS_OSC, oscuro), it.topLeft, it.size) }
        drawRect(t(TINTA, oscuro).copy(alpha = 0.85f), rodapie.topLeft, rodapie.size)
        // El gabinete y sus dos cajones.
        drawRect(t(GRIS, oscuro), gabinete.topLeft, gabinete.size)
        drawRect(Color.Black.copy(alpha = 0.16f), Offset(gabinete.right - w * 0.045f, gabinete.top), Size(w * 0.045f, gabinete.height))
        listOf(0.19f, 0.51f).forEach { x ->
            val cj = fr(x, 0.815f, x + 0.3f, 0.922f)
            drawRect(t(GRIS_LUZ, oscuro), cj.topLeft, cj.size)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.08f else 0.5f), cj.topLeft, Size(cj.width, lw))
            drawRect(t(GRIS_OSC, oscuro), Offset(cj.left, cj.bottom - lw), Size(cj.width, lw))
            // La jaladera del nivel.
            chapaNivel(fr(x + 0.09f, 0.83f, x + 0.21f, 0.848f), e, h * 0.006f)
        }
        // La etiqueta de inventario, en el cajón de la izquierda.
        if (fino) {
            val et = fr(0.23f, 0.866f, 0.45f, 0.906f)
            drawRect(t(BLANCO, oscuro), et.topLeft, et.size)
            drawRect(t(GRIS_OSC, oscuro), et.topLeft, et.size, style = Stroke(lw * 0.6f))
            val barras = if (muyFino) 11 else 7
            for (k in 0 until barras) {
                val x = et.left + et.width * (0.08f + 0.6f * k / barras)
                drawLine(t(TINTA, oscuro), Offset(x, et.top + et.height * 0.2f), Offset(x, et.bottom - et.height * 0.2f), lw * (if (azar(e.semilla, k) > 0.5f) 1.4f else 0.7f))
            }
            drawCircle(t(ROJO, oscuro), et.height * 0.22f, Offset(et.right - et.width * 0.15f, et.center.y))
        }
        // La cubierta de resina con su canto cian.
        drawRoundRect(t(BLANCO, oscuro), cubierta.topLeft, cubierta.size, CornerRadius(w * 0.012f))
        drawRect(t(GRIS_LUZ, oscuro), Offset(cubierta.left + w * 0.01f, cubierta.top + cubierta.height * 0.45f), Size(cubierta.width - w * 0.02f, cubierta.height * 0.3f))
        drawRect(t(CIAN, oscuro), Offset(cubierta.left, cubierta.bottom - cubierta.height * 0.25f), Size(cubierta.width, cubierta.height * 0.25f))
        if (muyFino) drawLine(Color.White.copy(alpha = 0.7f), pt(0.14f, 0.769f), pt(0.5f, 0.769f), lw)
    }

    /**
     * El gato de laboratorio: la platina de acero con su canto cian, sostenida por la tijera de dos brazos con sus ejes, el
     * tornillo que la sube con la perilla en el metal del nivel, y la base con su escala grabada.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val platina = fr(0.08f, 0f, 0.92f, 0.2f)
        val base = fr(0.04f, 0.8f, 0.96f, 1f)
        if (!e.hecho) {
            drawPath(Path().apply { addRect(platina); addRect(base); addRect(fr(0.24f, 0.2f, 0.76f, 0.8f)) }, siluetaFuturo(oscuro)); return
        }
        val acero = t(GRIS_OSC, oscuro)
        // La tijera: dos brazos cruzados por cada lado, con sus ejes.
        val brazo = maxOf(h * 0.07f, 0.8f * u)
        listOf(0.26f to 0.74f, 0.74f to 0.26f).forEach { (a, b) -> drawLine(acero, pt(a, 0.2f), pt(b, 0.8f), brazo, StrokeCap.Round) }
        listOf(0.26f to 0.74f, 0.74f to 0.26f).forEach { (a, b) -> drawLine(t(GRIS, oscuro), pt(a, 0.2f), pt(b, 0.8f), brazo * 0.4f, StrokeCap.Round) }
        listOf(pt(0.26f, 0.2f), pt(0.74f, 0.2f), pt(0.26f, 0.8f), pt(0.74f, 0.8f)).forEach { drawCircle(acero, brazo * 0.8f, it) }
        // El tornillo y su perilla del nivel.
        drawLine(t(GRIS_LUZ, oscuro), pt(0.2f, 0.5f), pt(0.86f, 0.5f), lw)
        drawCircle(acero, brazo, pt(0.5f, 0.5f))
        val pr = fr(0.86f, 0.28f, 0.95f, 0.72f)
        chapaNivel(pr, e, pr.width * 0.3f)
        for (k in 1..2) drawLine(e.metalOscuro, Offset(pr.left + pr.width * k / 3f, pr.top + pr.height * 0.2f), Offset(pr.left + pr.width * k / 3f, pr.bottom - pr.height * 0.2f), lw * 0.6f)
        // La base con su escala.
        drawRect(t(GRIS, oscuro), base.topLeft, base.size)
        drawRect(t(GRIS_LUZ, oscuro), base.topLeft, Size(base.width, lw))
        drawRect(Color.Black.copy(alpha = 0.18f), Offset(base.left, base.bottom - lw), Size(base.width, lw))
        for (k in 0..10) drawLine(t(TINTA, oscuro).copy(alpha = 0.6f), Offset(w * (0.1f + k * 0.08f), h * 0.84f), Offset(w * (0.1f + k * 0.08f), h * (if (k % 5 == 0) 0.95f else 0.9f)), lw * 0.5f)
        // La platina con su canto cian.
        drawRect(t(GRIS_LUZ, oscuro), platina.topLeft, platina.size)
        drawRect(Color.White.copy(alpha = if (oscuro) 0.1f else 0.5f), platina.topLeft, Size(platina.width, lw))
        drawRect(t(CIAN, oscuro), Offset(platina.left, platina.bottom - h * 0.06f), Size(platina.width, h * 0.06f))
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6EDF2) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFF6FD8E6) else CIAN_OSC

    /**
     * La etiqueta de la muestra: el papel blanco liso en su marco del nivel, con la franja cian del encabezado al canto de arriba
     * y el punto rojo de la esquina.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(3 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(3 * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(3 * u), style = Stroke(0.8f * u))
        val b = 1.8f * u
        drawRoundRect(if (oscuro) Color(0xFF18222B) else Color.White, Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(2 * u))
        drawRect(t(CIAN, oscuro), Offset(b + 1 * u, b), Size(w - 2 * b - 2 * u, 1.6f * u))
        drawCircle(t(ROJO, oscuro), 1.3f * u, Offset(w - b - 3 * u, h - b - 3 * u))
    }
}

/**
 * Colonos: los logros del almacén. El trofeo descansa en un tocón del claro; en el hall, sobre el banco de pino, y su placa es
 * la pizarra del almacén.
 */
object LogrosColonos : DisenoLogros {
    private val PINO = Color(0xFFC49C66)
    private val PINO_LUZ = Color(0xFFDAB884)
    private val PINO_OSC = Color(0xFF8C6B41)
    private val VETA = Color(0xFF6E5030)
    private val CLAVO = Color(0xFF3A3530)
    private val TIZA = Color(0xFFF2EEE2)
    private val CORTEZA = Color(0xFF5A3A22)
    private val CORTEZA_OSC = Color(0xFF3A2414)
    private val ANILLO = Color(0xFFCDA878)

    /** El color en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El tocón del claro: el tronco cortado a hacha, con la corteza agrietada en vetas, la sombra de un lado, las raíces que se
     * abren al pie y las astillas alrededor; arriba, el corte con sus anillos, la médula y una raja. Clavada al frente, la
     * chapa de hojalata del nivel, un poco torcida, con sus clavos.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tronco = Path().apply {
            moveTo(w * 0.2f, h * 0.79f); lineTo(w * 0.8f, h * 0.79f)
            cubicTo(w * 0.8f, h * 0.86f, w * 0.82f, h * 0.91f, w * 0.87f, h * 0.935f)
            quadraticTo(w * 0.91f, h * 0.95f, w * 0.92f, h * 0.956f); lineTo(w * 0.08f, h * 0.956f)
            quadraticTo(w * 0.09f, h * 0.95f, w * 0.13f, h * 0.935f)
            cubicTo(w * 0.18f, h * 0.91f, w * 0.2f, h * 0.86f, w * 0.2f, h * 0.79f); close()
        }
        val corte = fr(0.2f, 0.765f, 0.8f, 0.815f)
        if (!e.hecho) { drawPath(Path().apply { addPath(tronco); addOval(corte) }, siluetaFuturo(oscuro)); return }
        sombraPie(0.06f, 0.94f, oscuro)
        // El tronco con su corteza.
        drawPath(tronco, t(CORTEZA, oscuro))
        clipPath(tronco) {
            if (fino) vetas(fr(0.08f, 0.79f, 0.92f, 0.96f), t(CORTEZA_OSC, oscuro), e.semilla, densidad = if (muyFino) 1.4f else 0.9f, vertical = true, grosor = 1.1f, ondulacion = 0.6f)
            else for (k in 0 until 5) drawLine(t(CORTEZA_OSC, oscuro), pt(0.26f + k * 0.12f, 0.8f), pt(0.25f + k * 0.125f, 0.95f), lw)
            drawRect(Color.Black.copy(alpha = 0.24f), pt(0.66f, 0.78f), Size(w * 0.3f, h * 0.2f))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.1f), pt(0.08f, 0.78f), Size(w * 0.18f, h * 0.2f))
        }
        // Las raíces que se abren al pie.
        listOf(0.3f to -1f, 0.52f to 1f, 0.7f to 1f).forEach { (x, s) ->
            drawPath(poli(x - 0.03f, 0.956f, x + s * 0.05f, 0.935f, x + 0.03f, 0.956f), t(CORTEZA_OSC, oscuro))
        }
        // El corte, con sus anillos, la médula y la raja.
        drawOval(t(CORTEZA_OSC, oscuro), corte.topLeft, corte.size)
        val cara = Rect(corte.left + w * 0.015f, corte.top + h * 0.003f, corte.right - w * 0.015f, corte.bottom - h * 0.006f)
        drawOval(t(ANILLO, oscuro), cara.topLeft, cara.size)
        val cx = cara.center.x + cara.width * 0.06f; val cy = cara.center.y
        val nAnillos = if (muyFino) 5 else if (fino) 3 else 0
        for (k in 1..nAnillos) {
            val f = k / (nAnillos + 1f)
            drawOval(t(PINO_OSC, oscuro).copy(alpha = 0.55f), Offset(cx - cara.width * 0.5f * f, cy - cara.height * 0.5f * f), Size(cara.width * f, cara.height * f), style = Stroke(lw * 0.8f))
        }
        drawCircle(t(VETA, oscuro), maxOf(h * 0.0035f, 0.5f * u), Offset(cx, cy))
        if (fino) drawLine(t(VETA, oscuro), Offset(cx + cara.width * 0.05f, cy), Offset(cara.right - cara.width * 0.06f, cy - cara.height * 0.12f), lw)
        // Las astillas alrededor del pie.
        if (fino) for (k in 0 until 4) {
            val x = if (k < 2) 0.04f + k * 0.05f else 0.84f + (k - 2) * 0.06f
            drawLine(t(PINO_LUZ, oscuro), pt(x, 0.958f), pt(x + 0.035f, 0.95f + 0.01f * azar(e.semilla, k)), lw * 1.3f, StrokeCap.Round)
        }
        // La chapa de hojalata del nivel, clavada y un poco torcida.
        val pl = fr(0.4f, 0.848f, 0.6f, 0.9f)
        rotate(-4f, pl.center) {
            chapaNivel(pl, e, h * 0.003f)
            val r = maxOf(pl.height * 0.09f, 0.5f * u)
            val clavos = if (fino) listOf(pl.left + pl.width * 0.1f to pl.top + pl.height * 0.22f, pl.right - pl.width * 0.1f to pl.top + pl.height * 0.22f,
                pl.left + pl.width * 0.1f to pl.bottom - pl.height * 0.22f, pl.right - pl.width * 0.1f to pl.bottom - pl.height * 0.22f)
            else listOf(pl.left + pl.width * 0.12f to pl.center.y, pl.right - pl.width * 0.12f to pl.center.y)
            clavos.forEach { (x, y) -> drawCircle(t(CLAVO, oscuro), r, Offset(x, y)) }
        }
    }

    /**
     * El banco de pino del almacén: el tablón con sus vetas y los cantos gastados, las dos patas abiertas con su travesaño y los
     * clavos de forja en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val tablon = fr(0.02f, 0f, 0.98f, 0.34f)
        val patas = listOf(poli(0.12f, 0.34f, 0.21f, 0.34f, 0.17f, 1f, 0.07f, 1f), poli(0.79f, 0.34f, 0.88f, 0.34f, 0.93f, 1f, 0.83f, 1f))
        if (!e.hecho) {
            drawPath(Path().apply { addRect(tablon); patas.forEach { addPath(it) }; addRect(fr(0.16f, 0.58f, 0.84f, 0.7f)) }, siluetaFuturo(oscuro)); return
        }
        // Las patas y el travesaño.
        patas.forEach { drawPath(it, t(PINO_OSC, oscuro)) }
        drawRect(t(PINO_OSC, oscuro), pt(0.16f, 0.58f), Size(w * 0.68f, h * 0.12f))
        drawRect(Color.Black.copy(alpha = 0.2f), pt(0.16f, 0.66f), Size(w * 0.68f, h * 0.04f))
        // El tablón con sus vetas y sus cantos.
        drawRect(t(PINO, oscuro), tablon.topLeft, tablon.size)
        vetas(tablon, t(VETA, oscuro).copy(alpha = 0.5f), e.semilla, densidad = 0.9f, grosor = 0.6f, ondulacion = 0.4f)
        drawRect(t(PINO_LUZ, oscuro), tablon.topLeft, Size(tablon.width, lw))
        drawRect(t(PINO_OSC, oscuro), Offset(tablon.left, tablon.bottom - lw), Size(tablon.width, lw))
        listOf(tablon.left, tablon.right - w * 0.025f).forEach { x -> drawRect(t(VETA, oscuro).copy(alpha = 0.6f), Offset(x, tablon.top), Size(w * 0.025f, tablon.height)) }
        // Los clavos de forja del nivel, encima de cada pata.
        listOf(0.14f, 0.19f, 0.81f, 0.86f).forEach { x -> remache(pt(x, 0.17f), h * 0.065f, e.base, e.brillo) }
        listOf(0.14f, 0.19f, 0.81f, 0.86f).forEach { x -> drawCircle(e.metalOscuro, h * 0.065f, pt(x, 0.17f), style = Stroke(maxOf(h * 0.015f, 0.4f * u))) }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = TIZA
    override fun rotuloPlaca(oscuro: Boolean): Color = PINO_LUZ

    /** La pizarra del almacén: la pizarra lisa en su marco de pino, con el fleje del nivel por dentro y un clavo en cada esquina. */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(2 * u)); return }
        // El marco de pino, con su veta.
        drawRoundRect(t(PINO, oscuro), Offset.Zero, size, CornerRadius(2 * u))
        drawRect(t(PINO_LUZ, oscuro), Offset(1 * u, 0.5f * u), Size(w - 2 * u, 0.8f * u))
        listOf(1.6f * u, h - 1.6f * u).forEach { y -> drawLine(t(VETA, oscuro).copy(alpha = 0.5f), Offset(6 * u, y), Offset(w - 9 * u, y), 0.5f * u) }
        drawRoundRect(t(PINO_OSC, oscuro), Offset.Zero, size, CornerRadius(2 * u), style = Stroke(0.7f * u))
        // El fleje del nivel y la pizarra.
        val b = 3.2f * u
        drawRect(e.base, Offset(b, b), Size(w - 2 * b, h - 2 * b))
        drawRect(e.metalOscuro, Offset(b, b), Size(w - 2 * b, h - 2 * b), style = Stroke(0.5f * u))
        val b2 = b + 1.1f * u
        drawRect(if (oscuro) Color(0xFF1E2522) else Color(0xFF2F3A35), Offset(b2, b2), Size(w - 2 * b2, h - 2 * b2))
        // Los clavos de las esquinas.
        listOf(Offset(1.7f * u, 1.7f * u), Offset(w - 1.7f * u, 1.7f * u), Offset(1.7f * u, h - 1.7f * u), Offset(w - 1.7f * u, h - 1.7f * u))
            .forEach { remache(it, 0.9f * u, t(CLAVO, oscuro), Color.White.copy(alpha = 0.35f)) }
    }
}

/**
 * Ciudad verde: los logros de las ruinas. El trofeo descansa en un bloque de hormigón partido, tomado por el musgo y la hiedra;
 * en el hall, sobre un tambor de columna caído, y su placa es un letrero de lámina rescatado.
 */
object LogrosRuinas : DisenoLogros {
    private val HORMIGON = Color(0xFF8C8D84)
    private val HORMIGON_OSC = Color(0xFF65665E)
    private val HORMIGON_LUZ = Color(0xFFA9AAA0)
    private val MUSGO = Color(0xFF55722A)
    private val MUSGO_LUZ = Color(0xFF86A33C)
    private val HOJA = Color(0xFF3D6A2A)
    private val LUCIERNAGA = Color(0xFFE6FF7A)
    private val OXIDO = Color(0xFF8A4A22)

    /** El color con la luz del momento: en modo oscuro, más hundido. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0A), 0.36f) else c

    /** Una hoja de hiedra: tres lóbulos alrededor de [c], de radio [r], girada [giro] grados. */
    private fun DrawScope.hoja(c: Offset, r: Float, giro: Float, color: Color) {
        rotate(giro, c) {
            drawPath(Path().apply {
                moveTo(c.x, c.y + r); quadraticTo(c.x - r * 1.1f, c.y + r * 0.2f, c.x - r * 0.9f, c.y - r * 0.2f)
                quadraticTo(c.x - r * 0.4f, c.y - r * 0.3f, c.x, c.y - r); quadraticTo(c.x + r * 0.4f, c.y - r * 0.3f, c.x + r * 0.9f, c.y - r * 0.2f)
                quadraticTo(c.x + r * 1.1f, c.y + r * 0.2f, c.x, c.y + r); close()
            }, color)
        }
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * El bloque de hormigón partido: el trozo de losa con su canto roto y la cara del lado en sombra, la grava a la vista, la
     * grieta y las dos varillas oxidadas que asoman; el musgo lo cubre por arriba y la hiedra cuelga por un lado. Atornillada al
     * frente, la placa rescatada en el metal del nivel. De noche, una luciérnaga sobre el musgo.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val bloque = poli(0.15f, 0.79f, 0.38f, 0.786f, 0.44f, 0.776f, 0.6f, 0.781f, 0.66f, 0.8f, 0.85f, 0.795f, 0.87f, 0.83f, 0.85f, 0.87f, 0.86f, 0.952f, 0.14f, 0.954f)
        if (!e.hecho) { drawPath(bloque, siluetaFuturo(oscuro)); return }
        sombraPie(0.08f, 0.92f, oscuro)
        // Las varillas oxidadas, detrás del canto roto.
        val vr = maxOf(w * 0.012f, 0.7f * u)
        drawPath(Path().apply { moveTo(w * 0.69f, h * 0.81f); quadraticTo(w * 0.69f, h * 0.76f, w * 0.72f, h * 0.735f) }, t(OXIDO, oscuro), style = Stroke(vr, cap = StrokeCap.Round))
        if (fino) drawPath(Path().apply { moveTo(w * 0.77f, h * 0.81f); quadraticTo(w * 0.79f, h * 0.775f, w * 0.81f, h * 0.765f) }, t(OXIDO, oscuro), style = Stroke(vr * 0.9f, cap = StrokeCap.Round))
        // El hormigón, con la cara del lado en sombra.
        drawPath(bloque, t(HORMIGON, oscuro))
        drawPath(poli(0.78f, 0.797f, 0.85f, 0.795f, 0.87f, 0.83f, 0.85f, 0.87f, 0.86f, 0.952f, 0.79f, 0.953f), t(HORMIGON_OSC, oscuro))
        drawPath(poli(0.6f, 0.781f, 0.66f, 0.8f, 0.78f, 0.797f, 0.62f, 0.792f), t(HORMIGON_LUZ, oscuro))
        // La grava a la vista y la grieta.
        if (fino) for (k in 0 until if (muyFino) 22 else 10) {
            val x = 0.18f + 0.6f * azar(e.semilla, k); val y = 0.81f + 0.13f * azar(e.semilla, k + 40)
            drawCircle(if (k % 3 == 0) t(HORMIGON_LUZ, oscuro) else t(HORMIGON_OSC, oscuro), (0.4f + 0.7f * azar(e.semilla, k + 80)) * u, pt(x, y))
        }
        drawPath(Path().apply { moveTo(w * 0.6f, h * 0.783f); lineTo(w * 0.57f, h * 0.83f); lineTo(w * 0.6f, h * 0.86f); lineTo(w * 0.55f, h * 0.93f) },
            Color.Black.copy(alpha = 0.45f), style = Stroke(lw, join = StrokeJoin.Round))
        // La placa rescatada, en el metal del nivel, con sus tornillos.
        val pl = fr(0.42f, 0.858f, 0.64f, 0.912f)
        rotate(3f, pl.center) {
            chapaNivel(pl, e, h * 0.004f)
            if (fino) listOf(pl.left + pl.width * 0.1f, pl.right - pl.width * 0.1f).forEach { x -> drawCircle(e.metalOscuro, pl.height * 0.1f, Offset(x, pl.center.y)) }
            if (fino) drawCircle(t(OXIDO, oscuro).copy(alpha = 0.5f), pl.height * 0.16f, Offset(pl.left + pl.width * 0.72f, pl.bottom - pl.height * 0.25f))
        }
        // El musgo por arriba.
        val matas = if (fino) 7 else 4
        for (k in 0 until matas) {
            val x = 0.15f + 0.4f * k / matas; val ww = 0.1f + 0.05f * azar(e.semilla, k + 120)
            drawOval(t(if (k % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), pt(x, 0.772f - 0.006f * azar(e.semilla, k + 130)), Size(w * ww, h * 0.026f))
        }
        // La hiedra que cuelga por la izquierda.
        val tallo = Path().apply { moveTo(w * 0.2f, h * 0.79f); cubicTo(w * 0.16f, h * 0.83f, w * 0.23f, h * 0.87f, w * 0.19f, h * 0.92f) }
        drawPath(tallo, t(HOJA, oscuro), style = Stroke(lw, cap = StrokeCap.Round))
        val hojas = if (fino) listOf(0.18f to 0.82f, 0.22f to 0.86f, 0.18f to 0.9f, 0.205f to 0.935f) else listOf(0.19f to 0.84f, 0.2f to 0.91f)
        hojas.forEachIndexed { i, (x, y) -> hoja(pt(x, y), h * 0.017f, if (i % 2 == 0) -30f else 25f, t(if (i % 2 == 0) HOJA else MUSGO, oscuro)) }
        // La luciérnaga.
        if (oscuro && fino) {
            drawCircle(LUCIERNAGA.copy(alpha = 0.22f), h * 0.014f, pt(0.36f, 0.75f))
            drawCircle(LUCIERNAGA, h * 0.004f, pt(0.36f, 0.75f))
        }
    }

    /**
     * El tambor de columna caído: el trozo de fuste de hormigón tumbado, con sus estrías gastadas y el corte a la vista con las
     * puntas de las varillas; el musgo encima, una mata de hierba al pie y la abrazadera rescatada en el metal del nivel.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val fuste = fr(0.06f, 0.12f, 0.88f, 0.96f)
        val corte = fr(0.8f, 0.12f, 0.96f, 0.96f)
        if (!e.hecho) {
            drawPath(Path().apply { addRoundRect(RoundRect(fuste, CornerRadius(h * 0.2f))); addOval(corte) }, siluetaFuturo(oscuro)); return
        }
        drawRoundRect(t(HORMIGON, oscuro), fuste.topLeft, fuste.size, CornerRadius(h * 0.2f))
        drawRect(t(HORMIGON_LUZ, oscuro), Offset(fuste.left + h * 0.2f, fuste.top + h * 0.06f), Size(fuste.width - h * 0.2f, h * 0.12f))
        drawRect(Color.Black.copy(alpha = 0.2f), Offset(fuste.left + h * 0.15f, fuste.bottom - h * 0.2f), Size(fuste.width - h * 0.15f, h * 0.16f))
        // Las estrías gastadas.
        listOf(0.36f, 0.54f, 0.72f).forEach { y -> drawLine(t(HORMIGON_OSC, oscuro), pt(0.1f, y), pt(0.8f, y + 0.02f), lw * 0.8f) }
        // La abrazadera rescatada, del nivel.
        chapaNivel(fr(0.3f, 0.08f, 0.38f, 1f), e, w * 0.01f)
        // El corte, con las puntas de las varillas.
        drawOval(t(HORMIGON_LUZ, oscuro), corte.topLeft, corte.size)
        drawOval(t(HORMIGON_OSC, oscuro), corte.topLeft, corte.size, style = Stroke(lw))
        listOf(0.4f, 0.62f).forEach { y -> drawCircle(t(OXIDO, oscuro), h * 0.05f, Offset(corte.center.x, h * y)) }
        // El musgo encima y la hierba al pie.
        for (k in 0 until 5) drawOval(t(if (k % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), pt(0.42f + k * 0.07f, 0.02f + 0.04f * azar(e.semilla, k)), Size(w * 0.09f, h * 0.2f))
        for (k in 0 until 5) drawLine(t(HOJA, oscuro), pt(0.06f + k * 0.018f, 1f), pt(0.04f + k * 0.03f, 0.55f + 0.1f * azar(e.semilla, k + 9)), lw, StrokeCap.Round)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFE3E8D6) else Color(0xFF1E2318)
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFB9D86A) else Color(0xFF3F5A1C)

    /**
     * La chapa rescatada: el letrero de lámina con las esquinas redondeadas, el borde en el metal del nivel con sus puntos de
     * óxido, cuatro agujeros de tornillo y una mata de musgo prendida en una esquina.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        if (!e.hecho) { drawRoundRect(siluetaFuturo(oscuro), Offset.Zero, size, CornerRadius(4 * u)); return }
        drawRoundRect(e.base, Offset.Zero, size, CornerRadius(4 * u))
        drawRect(e.brillo, Offset(3 * u, 0.6f * u), Size(w - 6 * u, 0.8f * u))
        drawRoundRect(e.metalOscuro, Offset.Zero, size, CornerRadius(4 * u), style = Stroke(0.8f * u))
        for (k in 0 until 4) drawCircle(t(OXIDO, oscuro).copy(alpha = 0.55f), (0.5f + 0.5f * azar(e.semilla, k)) * u,
            Offset(w * (0.15f + 0.7f * azar(e.semilla, k + 20)), if (k % 2 == 0) 0.9f * u else h - 0.9f * u))
        val b = 1.8f * u
        drawRoundRect(if (oscuro) Color(0xFF1B1E18) else Color(0xFFEAE8DC), Offset(b, b), Size(w - 2 * b, h - 2 * b), CornerRadius(3 * u))
        listOf(Offset(4 * u, 4 * u), Offset(w - 4 * u, 4 * u), Offset(4 * u, h - 4 * u), Offset(w - 4 * u, h - 4 * u)).forEach {
            drawCircle(e.metalOscuro, 1 * u, it); drawCircle(Color.Black.copy(alpha = 0.6f), 0.5f * u, it)
        }
        // La mata de musgo prendida en la esquina de arriba a la izquierda.
        listOf(Offset(0.5f * u, 2.5f * u) to 2.6f, Offset(3 * u, 0.4f * u) to 2.2f, Offset(5.5f * u, 0.8f * u) to 1.6f).forEachIndexed { i, (o, r) ->
            drawCircle(t(if (i % 2 == 0) MUSGO else MUSGO_LUZ, oscuro), r * u, o)
        }
    }
}

/**
 * Espacio: los logros de la nave. El trofeo descansa en la plataforma de acoplamiento; en el hall, sobre el pedestal de
 * levitación del observatorio, y su placa es el visor de datos.
 */
object LogrosEspacio : DisenoLogros {
    private val PANEL = Color(0xFFD6DCE5)
    private val PANEL_OSC = Color(0xFF9AA4B4)
    private val PANEL_HONDO = Color(0xFF6C778A)
    private val CIAN = Color(0xFF5CE1FF)
    private val VERDE = Color(0xFF5CFFB0)
    private val AMBAR = Color(0xFFFFB84A)
    private val CRISTAL = Color(0xFF070C18)
    private val MARINO = Color(0xFF1B2F5C)

    /** El color en modo oscuro, con las luces de la nave bajas. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF05070D), 0.62f) else c

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La plataforma de acoplamiento: la cubierta ochavada de paneles con la rendija de luz cian bajo su canto, el zócalo de paneles
     * con sus juntas y tornillos, las luces de estado, la franja de peligro ámbar y marina y las dos patas de aterrizaje; al
     * centro, el aro de anclaje en el metal del nivel.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u; val muyFino = h > 130 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val cubierta = ochavado(fr(0.1f, 0.765f, 0.9f, 0.8f), h * 0.012f)
        val zocalo = poli(0.14f, 0.8f, 0.86f, 0.8f, 0.86f, 0.91f, 0.83f, 0.935f, 0.17f, 0.935f, 0.14f, 0.91f)
        val patas = listOf(poli(0.2f, 0.935f, 0.3f, 0.935f, 0.32f, 0.955f, 0.18f, 0.955f), poli(0.7f, 0.935f, 0.8f, 0.935f, 0.82f, 0.955f, 0.68f, 0.955f))
        if (!e.hecho) { drawPath(Path().apply { addPath(cubierta); addPath(zocalo); patas.forEach { addPath(it) } }, siluetaFuturo(oscuro)); return }
        sombraPie(0.1f, 0.9f, oscuro)
        patas.forEach { drawPath(it, t(PANEL_HONDO, oscuro)) }
        // El zócalo de paneles, con la cara de la derecha en sombra, sus juntas y sus tornillos.
        drawPath(zocalo, t(PANEL_OSC, oscuro))
        drawPath(poli(0.8f, 0.8f, 0.86f, 0.8f, 0.86f, 0.91f, 0.83f, 0.935f, 0.8f, 0.935f), Color.Black.copy(alpha = 0.16f))
        val junta = t(PANEL_HONDO, oscuro)
        listOf(0.33f, 0.67f).forEach { x -> drawLine(junta, pt(x, 0.81f), pt(x, 0.905f), lw * 0.8f) }
        if (fino) listOf(0.17f, 0.3f, 0.36f, 0.64f, 0.7f, 0.83f).forEach { x -> listOf(0.822f, 0.892f).forEach { y -> drawCircle(junta, h * 0.0035f, pt(x, y)) } }
        // Las luces de estado.
        if (fino) {
            listOf(0.2f to VERDE, 0.25f to VERDE, 0.75f to AMBAR, 0.8f to CIAN).forEach { (x, c) ->
                drawCircle(c.copy(alpha = 0.3f), h * 0.009f, pt(x, 0.857f)); drawCircle(c, h * 0.0045f, pt(x, 0.857f))
            }
        }
        // La franja de peligro.
        val franja = fr(0.15f, 0.905f, 0.85f, 0.925f)
        drawRect(t(MARINO, oscuro), franja.topLeft, franja.size)
        if (fino) {
            val paso = maxOf(w * 0.05f, 1f)
            val n = ((franja.width / paso).toInt() + 2).coerceAtMost(60)
            clipPath(Path().apply { addRect(franja) }) {
                for (k in 0 until n) {
                    val x = franja.left + k * paso
                    drawPath(Path().apply { moveTo(x, franja.bottom); lineTo(x + paso * 0.5f, franja.bottom); lineTo(x + paso * 0.5f + franja.height, franja.top); lineTo(x + franja.height, franja.top); close() }, AMBAR.copy(alpha = if (oscuro) 0.75f else 1f))
                }
            }
        } else drawRect(AMBAR, franja.topLeft, Size(franja.width, franja.height * 0.4f))
        // El aro de anclaje, en el metal del nivel.
        val c = pt(0.5f, 0.858f); val r = h * 0.032f
        drawCircle(e.base, r, c)
        drawCircle(e.brillo, r * 0.78f, c, style = Stroke(maxOf(r * 0.16f, 0.5f * u)))
        drawCircle(e.metalOscuro, r, c, style = Stroke(maxOf(r * 0.12f, 0.45f * u)))
        drawCircle(CRISTAL, r * 0.48f, c)
        drawCircle(CIAN, r * 0.2f, c)
        if (e.nivel >= 4 && fino) drawPath(estrella(Offset(c.x + r * 0.95f, c.y - r * 0.95f), r * 0.55f, 4, 0.25f), Color.White.copy(alpha = 0.9f))
        // La cubierta, su cara de luz y la rendija cian bajo el canto.
        drawPath(cubierta, t(PANEL, oscuro))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f), pt(0.12f, 0.767f), Size(w * 0.76f, h * 0.008f))
        drawRect(t(PANEL_OSC, oscuro), pt(0.11f, 0.792f), Size(w * 0.78f, h * 0.008f))
        drawRect(CIAN.copy(alpha = 0.35f), pt(0.16f, 0.799f), Size(w * 0.68f, h * 0.012f))
        drawRect(CIAN, pt(0.18f, 0.801f), Size(w * 0.64f, maxOf(h * 0.004f, 0.5f * u)))
        if (muyFino) listOf(0.3f, 0.5f, 0.7f).forEach { x -> drawLine(t(PANEL_OSC, oscuro), pt(x, 0.768f), pt(x, 0.792f), lw * 0.7f) }
    }

    /**
     * El pedestal de levitación del observatorio: la losa ochavada de paneles que flota sobre su base, con el haz cian entre las
     * dos, las luces de posición en los extremos y la franja del nivel al frente.
     */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val lw = maxOf(h * 0.03f, 0.5f * u)
        val losa = ochavado(fr(0.03f, 0f, 0.97f, 0.44f), h * 0.12f)
        val base = ochavado(fr(0.2f, 0.74f, 0.8f, 1f), h * 0.08f)
        if (!e.hecho) { drawPath(Path().apply { addPath(losa); addPath(base) }, siluetaFuturo(oscuro)); return }
        // La base y el haz.
        drawPath(base, t(PANEL_OSC, oscuro))
        drawRect(t(PANEL_HONDO, oscuro), pt(0.22f, 0.92f), Size(w * 0.56f, h * 0.06f))
        for (k in 0 until 4) drawRect(CIAN.copy(alpha = 0.32f - k * 0.07f), pt(0.24f - k * 0.02f, 0.5f + k * 0.06f), Size(w * (0.52f + k * 0.04f), h * 0.04f))
        drawRect(CIAN.copy(alpha = 0.6f), pt(0.24f, 0.7f), Size(w * 0.52f, h * 0.04f))
        // La losa, con su cara de luz, sus juntas y la franja del nivel.
        drawPath(losa, t(PANEL, oscuro))
        drawRect(Color.White.copy(alpha = if (oscuro) 0.06f else 0.45f), pt(0.07f, 0.03f), Size(w * 0.86f, lw))
        drawRect(t(PANEL_OSC, oscuro), pt(0.04f, 0.36f), Size(w * 0.92f, h * 0.08f))
        listOf(0.25f, 0.75f).forEach { x -> drawLine(t(PANEL_OSC, oscuro), pt(x, 0.06f), pt(x, 0.36f), lw * 0.7f) }
        chapaNivel(fr(0.34f, 0.1f, 0.66f, 0.3f), e, h * 0.04f)
        listOf(0.09f to VERDE, 0.91f to AMBAR).forEach { (x, c) -> drawCircle(c.copy(alpha = 0.3f), h * 0.1f, pt(x, 0.2f)); drawCircle(c, h * 0.05f, pt(x, 0.2f)) }
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = Color(0xFFE8F3FF)
    override fun rotuloPlaca(oscuro: Boolean): Color = CIAN

    /**
     * El visor de datos: el cristal oscuro ochavado en su marco de panel, con el filo del nivel por fuera y los corchetes cian de
     * las cuatro esquinas.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val todo = Rect(0f, 0f, w, h)
        if (!e.hecho) { drawPath(ochavado(todo, 5 * u), siluetaFuturo(oscuro)); return }
        drawPath(ochavado(todo, 5 * u), e.base)
        drawLine(e.brillo, Offset(5 * u, 0.7f * u), Offset(w - 5 * u, 0.7f * u), 0.8f * u)
        drawPath(ochavado(todo, 5 * u), e.metalOscuro, style = Stroke(0.7f * u))
        val b1 = 1.4f * u
        drawPath(ochavado(Rect(b1, b1, w - b1, h - b1), 4.4f * u), if (oscuro) mezcla(PANEL, Color(0xFF05070D), 0.5f) else PANEL)
        val b2 = 3 * u
        drawPath(ochavado(Rect(b2, b2, w - b2, h - b2), 3.6f * u), CRISTAL)
        // Los corchetes de las esquinas.
        val a = 5 * u; val l = 4 * u
        listOf(Offset(a, a) to Offset(1f, 1f), Offset(w - a, a) to Offset(-1f, 1f), Offset(a, h - a) to Offset(1f, -1f), Offset(w - a, h - a) to Offset(-1f, -1f)).forEach { (o, s) ->
            drawPath(Path().apply { moveTo(o.x, o.y + s.y * l); lineTo(o.x, o.y); lineTo(o.x + s.x * l, o.y) }, CIAN.copy(alpha = 0.8f), style = Stroke(0.8f * u))
        }
    }
}
