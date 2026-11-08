package io.github.ruddy2003.vestigium.ui.logros

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.arcoMedioPunto
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.bisel
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// =====================================================================
//  Los logros y el viaje de la Edad Media y la Edad Moderna
// =====================================================================

// ---------------------------------------------------------------- ayudantes de este archivo

/** Los tonos del metal del nivel, un poco apagados de noche; [cristal] es el legendario (nivel 4), que lleva destellos. */
private class Metal(val o: Color, val b: Color, val br: Color, val cristal: Boolean)

private fun metal(e: EstadoPedestal, oscuro: Boolean) =
    if (oscuro) Metal(mezcla(e.metalOscuro, Color.Black, 0.22f), mezcla(e.base, Color.Black, 0.14f), mezcla(e.brillo, Color.Black, 0.1f), e.nivel >= 4)
    else Metal(e.metalOscuro, e.base, e.brillo, e.nivel >= 4)

/** El gris translúcido de lo que aún no se gana (como la figura sin ganar): oscuro sobre fondo claro, claro sobre fondo oscuro. */
private fun silueta(oscuro: Boolean) = if (oscuro) Color(0xFFE6E0D6).copy(alpha = 0.11f) else Color(0xFF2E2A26).copy(alpha = 0.11f)

/** La silueta tenue de un pedestal aún no ganado: la forma llena de gris translúcido y su contorno un poco más marcado. */
private fun DrawScope.siluetaDe(p: Path, oscuro: Boolean) {
    val c = silueta(oscuro)
    drawPath(p, c)
    drawPath(p, c.copy(alpha = c.alpha * 1.3f), style = Stroke(maxOf(0.7f * density, size.height * 0.004f)))
}

/** La sombra al pie: dos óvalos de negro translúcido, el de afuera más tenue. */
private fun DrawScope.sombraPie(x0: Float, x1: Float, y: Float, alto: Float, fuerza: Float) {
    drawOval(Color.Black.copy(alpha = fuerza * 0.45f), Offset(x0 - alto, y - alto * 0.55f), Size(x1 - x0 + 2 * alto, alto * 1.5f))
    drawOval(Color.Black.copy(alpha = fuerza), Offset(x0, y - alto * 0.35f), Size(x1 - x0, alto * 0.9f))
}

/** Un clavo o remache del metal del nivel: la cabeza, su luz y, si cabe, el punto de brillo. */
private fun DrawScope.remache(c: Offset, r: Float, m: Metal) {
    drawCircle(m.o, r, c)
    drawCircle(m.b, r * 0.72f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
    if (r > 1.1f * density) drawCircle(m.br, r * 0.3f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
}

/** Una barra del metal del nivel: la base, el filo de luz arriba y la sombra abajo. */
private fun DrawScope.barra(r: Rect, m: Metal) {
    drawRect(m.b, r.topLeft, r.size)
    drawRect(m.br, r.topLeft, Size(r.width, r.height * 0.3f))
    drawRect(m.o, Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
}

/** El destello del cristal (legendario): una estrellita de cuatro puntas, solo si se ve. */
private fun DrawScope.destello(c: Offset, r: Float, m: Metal) {
    if (!m.cristal || r < 1.4f * density) return
    drawPath(estrella(c, r, 4, 0.22f), Color.White.copy(alpha = 0.85f))
    drawCircle(m.br, r * 0.22f, c)
}

private fun DrawScope.caja(c: Color, r: Rect) = drawRect(c, r.topLeft, r.size)

/** Un rectángulo en fracciones del lienzo. */
private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)

/** Un rectángulo con las esquinas cortadas a bisel ([c] de corte). */
private fun chaflan(r: Rect, c: Float): Path = Path().apply {
    moveTo(r.left + c, r.top); lineTo(r.right - c, r.top); lineTo(r.right, r.top + c); lineTo(r.right, r.bottom - c)
    lineTo(r.right - c, r.bottom); lineTo(r.left + c, r.bottom); lineTo(r.left, r.bottom - c); lineTo(r.left, r.top + c); close()
}

/** Un rombo de centro [c] y semidiagonales [rx] y [ry]. */
private fun rombo(c: Offset, rx: Float, ry: Float): Path = Path().apply {
    moveTo(c.x, c.y - ry); lineTo(c.x + rx, c.y); lineTo(c.x, c.y + ry); lineTo(c.x - rx, c.y); close()
}

/** Una escuadra de metal en ele en la esquina ([sx], [sy] = ±1 hacia adentro) con su remache. */
private fun DrawScope.escuadra(x: Float, y: Float, sx: Float, sy: Float, largo: Float, grueso: Float, m: Metal) {
    val p = Path().apply {
        moveTo(x, y); lineTo(x + sx * largo, y); lineTo(x + sx * largo, y + sy * grueso); lineTo(x + sx * grueso, y + sy * grueso)
        lineTo(x + sx * grueso, y + sy * largo); lineTo(x, y + sy * largo); close()
    }
    drawPath(p, m.b)
    drawPath(p, m.o, style = Stroke(maxOf(0.5f * density, grueso * 0.18f)))
    remache(Offset(x + sx * grueso * 0.55f, y + sy * grueso * 0.55f), grueso * 0.32f, m)
}

/**
 * Medieval: el scriptorium. El trofeo descansa en un arcón de roble con dos bandas de hierro claveteadas y la arquería ciega
 * pintada de bermellón y lapislázuli; la cerradura es del metal del nivel. En el hall, una mesa de caballete con su faldón
 * ajedrezado y el escudete del metal; la placa es una etiqueta de pergamino con cantoneras.
 */
object LogrosMedieval : DisenoLogros {
    private val ROBLE = Color(0xFF6E4A2A)
    private val ROBLE_LUZ = Color(0xFF8E6942)
    private val ROBLE_OSC = Color(0xFF3F2A18)
    private val VETA = Color(0xFF2B1B0E)
    private val HIERRO = Color(0xFF2B2A2D)
    private val HIERRO_LUZ = Color(0xFF6E6D73)
    private val PERGAMINO = Color(0xFFEADFBF)
    private val TINTA = Color(0xFF2A1B10)
    private val BERMELLON = Color(0xFFA5321F)
    private val LAPIZ = Color(0xFF2E4C88)
    private val ORO = Color(0xFFDDBE76)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val tapa = fr(0.12f, 0.765f, 0.88f, 0.805f)
        val cuerpo = fr(0.15f, 0.805f, 0.85f, 0.932f)
        val forma = Path().apply {
            addRect(tapa); addRect(cuerpo); addRect(fr(0.16f, 0.93f, 0.26f, 0.958f)); addRect(fr(0.74f, 0.93f, 0.84f, 0.958f))
        }
        if (!e.hecho) { siluetaDe(forma, oscuro); return }
        val m = metal(e, oscuro)
        val roble = t(ROBLE, oscuro); val luz = t(ROBLE_LUZ, oscuro); val osc = t(ROBLE_OSC, oscuro); val veta = t(VETA, oscuro)
        val hierro = t(HIERRO, oscuro); val hierroLuz = t(HIERRO_LUZ, oscuro)
        sombraPie(w * 0.13f, w * 0.87f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        // Las patas: dos tacos de roble.
        listOf(0.16f, 0.74f).forEach { x -> caja(osc, fr(x, 0.93f, x + 0.1f, 0.958f)) }
        // El cuerpo: tablas verticales con sus vetas.
        drawRect(Brush.verticalGradient(listOf(luz, roble, osc), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (fino) {
            vetas(cuerpo, veta.copy(alpha = 0.3f), e.semilla, densidad = 0.7f, vertical = true, grosor = 0.6f)
            for (k in 1..4) { val x = cuerpo.left + cuerpo.width * k / 5f; drawLine(veta.copy(alpha = 0.55f), Offset(x, cuerpo.top), Offset(x, cuerpo.bottom), 0.6f * u) }
            // La arquería ciega pintada: arcos de medio punto alternando bermellón y lapislázuli, con un punto de oro.
            val n = 7; val paso = cuerpo.width / n; val y0 = cuerpo.top + cuerpo.height * 0.14f; val alto = cuerpo.height * 0.34f
            for (k in 0 until n) {
                val x0 = cuerpo.left + k * paso + paso * 0.14f
                drawPath(arcoMedioPunto(Rect(x0, y0, x0 + paso * 0.72f, y0 + alto)), (if (k % 2 == 0) t(BERMELLON, oscuro) else t(LAPIZ, oscuro)).copy(alpha = 0.85f))
                drawCircle(t(ORO, oscuro), maxOf(0.5f * u, paso * 0.07f), Offset(x0 + paso * 0.36f, y0 + alto * 0.5f))
            }
            drawLine(t(ORO, oscuro).copy(alpha = 0.7f), Offset(cuerpo.left, y0 + alto + 1.2f * u), Offset(cuerpo.right, y0 + alto + 1.2f * u), 0.6f * u)
        }
        // La tapa, con su canto biselado.
        caja(roble, tapa)
        bisel(tapa, luz.copy(alpha = 0.85f), osc, maxOf(0.6f * u, tapa.height * 0.2f))
        if (fino) vetas(Rect(tapa.left, tapa.top + tapa.height * 0.25f, tapa.right, tapa.bottom - tapa.height * 0.25f), veta.copy(alpha = 0.3f), e.semilla + 7, densidad = 0.5f, grosor = 0.5f)
        // Las dos bandas de hierro de arriba abajo, claveteadas con el metal del nivel.
        listOf(0.24f, 0.71f).forEach { x ->
            val b = fr(x, 0.765f, x + 0.05f, 0.932f)
            caja(hierro, b)
            drawRect(hierroLuz.copy(alpha = 0.6f), b.topLeft, Size(b.width * 0.22f, b.height))
            val nClavos = if (fino) 4 else 2
            for (k in 0 until nClavos) remache(Offset(b.center.x, b.top + b.height * (0.15f + 0.7f * k / maxOf(nClavos - 1, 1))), maxOf(0.6f * u, b.width * 0.26f), m)
        }
        // La cerradura: la placa del metal del nivel con su bocallave.
        val pl = fr(0.42f, 0.815f, 0.58f, 0.91f)
        drawRoundRect(m.o, pl.topLeft, pl.size, CornerRadius(pl.width * 0.12f))
        drawRoundRect(m.b, Offset(pl.left + pl.width * 0.08f, pl.top + pl.height * 0.08f), Size(pl.width * 0.84f, pl.height * 0.84f), CornerRadius(pl.width * 0.1f))
        drawRect(m.br, Offset(pl.left + pl.width * 0.14f, pl.top + pl.height * 0.12f), Size(pl.width * 0.72f, pl.height * 0.14f))
        val bc = Offset(pl.center.x, pl.top + pl.height * 0.5f); val rb = pl.width * 0.12f
        drawCircle(hierro, rb, bc)
        drawPath(Path().apply { moveTo(bc.x - rb * 0.6f, bc.y); lineTo(bc.x + rb * 0.6f, bc.y); lineTo(bc.x + rb * 0.9f, bc.y + rb * 2.4f); lineTo(bc.x - rb * 0.9f, bc.y + rb * 2.4f); close() }, hierro)
        if (fino) listOf(0.2f, 0.8f).forEach { fx -> remache(Offset(pl.left + pl.width * fx, pl.top + pl.height * 0.2f), pl.width * 0.07f, m); remache(Offset(pl.left + pl.width * fx, pl.bottom - pl.height * 0.18f), pl.width * 0.07f, m) }
        destello(Offset(pl.right - pl.width * 0.15f, pl.top + pl.height * 0.2f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val tablero = fr(0.03f, 0f, 0.97f, 0.24f)
        val faldon = fr(0.1f, 0.24f, 0.9f, 0.52f)
        fun pata(x: Float) = Path().apply { moveTo(w * (x - 0.03f), h * 0.52f); lineTo(w * (x + 0.03f), h * 0.52f); lineTo(w * (x + 0.08f), h * 0.92f); lineTo(w * (x - 0.08f), h * 0.92f); close() }
        if (!e.hecho) { siluetaDe(Path().apply { addRect(tablero); addRect(faldon); addPath(pata(0.24f)); addPath(pata(0.76f)); addRect(fr(0.12f, 0.9f, 0.88f, 1f)) }, oscuro); return }
        val m = metal(e, oscuro)
        val roble = t(ROBLE, oscuro); val luz = t(ROBLE_LUZ, oscuro); val osc = t(ROBLE_OSC, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.08f, h * 0.94f), Size(w * 0.84f, h * 0.06f))
        // Los caballetes, con su pie de travesaño y el larguero que los une, acuñado.
        listOf(0.24f, 0.76f).forEach { x -> drawPath(pata(x), osc); caja(roble, fr(x - 0.12f, 0.88f, x + 0.12f, 0.97f)) }
        caja(roble, fr(0.24f, 0.66f, 0.76f, 0.76f))
        listOf(0.31f, 0.69f).forEach { x -> caja(osc, fr(x - 0.012f, 0.62f, x + 0.012f, 0.8f)) }
        // El faldón ajedrezado de bermellón y lapislázuli, entre filetes de oro.
        caja(osc, faldon)
        val n = (faldon.width / maxOf(3.2f * u, 1f)).toInt().coerceIn(6, 40)
        val lado = faldon.width / n
        for (k in 0 until n) for (f in 0..1) {
            val c = if ((k + f) % 2 == 0) t(BERMELLON, oscuro) else t(LAPIZ, oscuro)
            drawRect(c, Offset(faldon.left + k * lado, faldon.top + faldon.height * (0.12f + f * 0.38f)), Size(lado, faldon.height * 0.38f))
        }
        drawLine(t(ORO, oscuro), Offset(faldon.left, faldon.top + faldon.height * 0.1f), Offset(faldon.right, faldon.top + faldon.height * 0.1f), 0.6f * u)
        drawLine(t(ORO, oscuro), Offset(faldon.left, faldon.bottom - faldon.height * 0.08f), Offset(faldon.right, faldon.bottom - faldon.height * 0.08f), 0.6f * u)
        // El escudete del metal del nivel en medio.
        val ec = Offset(w * 0.5f, faldon.center.y); val er = faldon.height * 0.62f
        val escudo = Path().apply {
            moveTo(ec.x - er * 0.62f, ec.y - er * 0.7f); lineTo(ec.x + er * 0.62f, ec.y - er * 0.7f); lineTo(ec.x + er * 0.62f, ec.y)
            quadraticTo(ec.x + er * 0.5f, ec.y + er * 0.6f, ec.x, ec.y + er * 0.85f); quadraticTo(ec.x - er * 0.5f, ec.y + er * 0.6f, ec.x - er * 0.62f, ec.y); close()
        }
        drawPath(escudo, m.b); drawPath(escudo, m.o, style = Stroke(maxOf(0.6f * u, er * 0.1f)))
        drawLine(m.br, Offset(ec.x - er * 0.4f, ec.y - er * 0.5f), Offset(ec.x + er * 0.4f, ec.y - er * 0.5f), maxOf(0.5f * u, er * 0.08f))
        destello(Offset(ec.x + er * 0.25f, ec.y - er * 0.35f), er * 0.5f, m)
        // El tablero, con su canto.
        caja(roble, tablero)
        bisel(tablero, luz.copy(alpha = 0.85f), osc, maxOf(0.6f * u, tablero.height * 0.18f))
        vetas(Rect(tablero.left, tablero.top + tablero.height * 0.2f, tablero.right, tablero.bottom - tablero.height * 0.2f), t(VETA, oscuro).copy(alpha = 0.3f), e.semilla, densidad = 0.6f, grosor = 0.5f)
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFEFE5C8) else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC8B993) else Color(0xFF5C4430)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        // La etiqueta: pergamino de día, piel curtida de noche, con su sombra.
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size, CornerRadius(1.5f * u))
        drawRoundRect(if (oscuro) Color(0xFF2C2016) else PERGAMINO, r.topLeft, r.size, CornerRadius(1.5f * u))
        // El borde del metal del nivel: un filete doble.
        val g = 2.4f * u
        drawRect(m.b, Offset(r.left + g, r.top + g), Size(r.width - 2 * g, r.height - 2 * g), style = Stroke(1.1f * u))
        drawRect(m.o.copy(alpha = 0.7f), Offset(r.left + g + 1.6f * u, r.top + g + 1.6f * u), Size(r.width - 2 * g - 3.2f * u, r.height - 2 * g - 3.2f * u), style = Stroke(0.5f * u))
        // Las cantoneras de las esquinas, clavadas.
        val l = minOf(w, h) * 0.2f; val gr = 2.6f * u
        escuadra(r.left, r.top, 1f, 1f, l, gr, m); escuadra(r.right, r.top, -1f, 1f, l, gr, m)
        escuadra(r.left, r.bottom, 1f, -1f, l, gr, m); escuadra(r.right, r.bottom, -1f, -1f, l, gr, m)
        destello(Offset(r.right - gr, r.top + gr), 3f * u, m)
    }
}

/**
 * Templario: la encomienda. El trofeo descansa en un sillar de dos hiladas con la colgadura del baucent (negro arriba, blanco
 * abajo, con la cruz patada) colgada de una varilla del metal del nivel. En el hall, la piedra de altar con la cruz grabada
 * en un anillo del metal; la placa es una lápida con una cruz del metal en cada esquina.
 */
object LogrosTemplario : DisenoLogros {
    private val SILLAR = Color(0xFFC4B79C)
    private val SILLAR_JUNTA = Color(0xFF8C826F)
    private val ROJO = Color(0xFFB0241E)
    private val BLANCO = Color(0xFFEFEADF)
    private val NEGRO = Color(0xFF17161A)
    private val CREMA = Color(0xFFEDE3CB)
    private val TINTA = Color(0xFF231A12)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0D0B), 0.36f) else c
    /** La arenisca, de noche más honda que el resto (es lo que más pesa en el dibujo). */
    private fun piedraDe(oscuro: Boolean) = if (oscuro) mezcla(SILLAR, Color(0xFF0F0D0B), 0.58f) else SILLAR

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val cornisa = fr(0.11f, 0.765f, 0.89f, 0.795f)
        val cuerpo = fr(0.15f, 0.795f, 0.85f, 0.93f)
        val zocalo = fr(0.12f, 0.93f, 0.88f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(cornisa); addRect(cuerpo); addRect(zocalo) }, oscuro); return }
        val m = metal(e, oscuro)
        val piedra = piedraDe(oscuro); val junta = t(SILLAR_JUNTA, oscuro)
        val luz = mezcla(piedra, Color.White, 0.25f); val sombra = mezcla(piedra, Color.Black, 0.28f)
        sombraPie(w * 0.11f, w * 0.89f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        caja(mezcla(piedra, Color.Black, 0.1f), zocalo)
        bisel(zocalo, luz.copy(alpha = 0.5f), sombra, maxOf(0.5f * u, zocalo.height * 0.15f))
        // Dos hiladas de sillares, las juntas a matajunta.
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombra), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        val lj = maxOf(0.5f * u, h * 0.004f); val ym = cuerpo.center.y
        drawLine(junta, Offset(cuerpo.left, ym), Offset(cuerpo.right, ym), lj)
        listOf(0.3f, 0.7f).forEach { fx -> drawLine(junta, Offset(cuerpo.left + cuerpo.width * fx, cuerpo.top), Offset(cuerpo.left + cuerpo.width * fx, ym), lj) }
        drawLine(junta, Offset(cuerpo.left + cuerpo.width * 0.5f, ym), Offset(cuerpo.left + cuerpo.width * 0.5f, cuerpo.bottom), lj)
        if (fino) {
            // Las marcas de cantero: un ángulo, una flecha, una cruz de trazo.
            val mk = h * 0.012f; val mc = junta.copy(alpha = 0.8f)
            listOf(0.08f to 0.3f, 0.86f to 0.28f, 0.2f to 0.76f, 0.8f to 0.74f).forEachIndexed { i, (fx, fy) ->
                val c = Offset(cuerpo.left + cuerpo.width * fx, cuerpo.top + cuerpo.height * fy)
                when (i % 3) {
                    0 -> { drawLine(mc, Offset(c.x - mk, c.y + mk), Offset(c.x, c.y - mk), lj * 0.8f); drawLine(mc, Offset(c.x, c.y - mk), Offset(c.x + mk, c.y + mk), lj * 0.8f) }
                    1 -> { drawLine(mc, Offset(c.x, c.y - mk), Offset(c.x, c.y + mk), lj * 0.8f); drawLine(mc, Offset(c.x - mk * 0.7f, c.y - mk * 0.2f), Offset(c.x, c.y - mk), lj * 0.8f); drawLine(mc, Offset(c.x + mk * 0.7f, c.y - mk * 0.2f), Offset(c.x, c.y - mk), lj * 0.8f) }
                    else -> { drawLine(mc, Offset(c.x - mk, c.y), Offset(c.x + mk, c.y), lj * 0.8f); drawLine(mc, Offset(c.x, c.y - mk), Offset(c.x, c.y + mk), lj * 0.8f) }
                }
            }
        }
        // La cornisa con su chaflán.
        caja(luz, cornisa)
        bisel(cornisa, Color.White.copy(alpha = 0.25f), sombra, maxOf(0.6f * u, cornisa.height * 0.22f))
        // La colgadura del baucent: negro arriba, blanco abajo y la cola en pico, con la cruz patada.
        val x0 = w * 0.355f; val x1 = w * 0.645f; val yT = h * 0.8f; val yB = h * 0.895f; val yP = h * 0.918f
        val pano = Path().apply { moveTo(x0, yT); lineTo(x1, yT); lineTo(x1, yB); lineTo(w * 0.5f, yP); lineTo(x0, yB); close() }
        drawPath(pano, t(BLANCO, oscuro))
        drawRect(t(NEGRO, oscuro), Offset(x0, yT), Size(x1 - x0, (yP - yT) * 0.3f))
        if (fino) listOf(0.33f, 0.66f).forEach { fx -> drawLine(Color.Black.copy(alpha = 0.12f), Offset(x0 + (x1 - x0) * fx, yT + (yP - yT) * 0.3f), Offset(x0 + (x1 - x0) * fx, yB), 0.8f * u) }
        drawPath(cruzPatada(Offset(w * 0.5f, yT + (yP - yT) * 0.62f), (yP - yT) * 0.24f), t(ROJO, oscuro))
        drawPath(pano, Color.Black.copy(alpha = 0.25f), style = Stroke(lj * 0.8f))
        // La varilla del metal del nivel con sus remates y la borla al pico.
        barra(Rect(x0 - w * 0.03f, yT - h * 0.008f, x1 + w * 0.03f, yT + h * 0.006f), m)
        listOf(x0 - w * 0.03f, x1 + w * 0.03f).forEach { x -> remache(Offset(x, yT - h * 0.001f), maxOf(0.7f * u, h * 0.011f), m) }
        remache(Offset(w * 0.5f, yP + h * 0.008f), maxOf(0.7f * u, h * 0.01f), m)
        destello(Offset(x1 + w * 0.03f, yT - h * 0.002f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val losa = fr(0.05f, 0f, 0.95f, 0.22f)
        val bloque = fr(0.12f, 0.22f, 0.88f, 0.86f)
        val plinto = fr(0.08f, 0.86f, 0.92f, 1f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(losa); addRect(bloque); addRect(plinto) }, oscuro); return }
        val m = metal(e, oscuro)
        val piedra = piedraDe(oscuro); val junta = t(SILLAR_JUNTA, oscuro)
        val luz = mezcla(piedra, Color.White, 0.22f); val sombra = mezcla(piedra, Color.Black, 0.26f)
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombra), bloque.top, bloque.bottom), bloque.topLeft, bloque.size)
        // Tres sillares, la cruz en el de en medio.
        listOf(0.3f, 0.7f).forEach { fx -> drawLine(junta, Offset(bloque.left + bloque.width * fx, bloque.top), Offset(bloque.left + bloque.width * fx, bloque.bottom), 0.7f * u) }
        val cc = bloque.center; val cr = bloque.height * 0.36f
        drawCircle(sombra.copy(alpha = 0.5f), cr * 1.25f, cc)
        drawCircle(m.b, cr * 1.18f, cc, style = Stroke(maxOf(0.7f * u, cr * 0.16f)))
        drawCircle(m.br.copy(alpha = 0.7f), cr * 1.18f, Offset(cc.x - cr * 0.05f, cc.y - cr * 0.05f), style = Stroke(maxOf(0.4f * u, cr * 0.05f)))
        drawPath(cruzPatada(cc, cr * 0.85f), t(ROJO, oscuro))
        // Las grapas del metal que cosen la losa a los sillares.
        listOf(0.3f, 0.7f).forEach { fx -> barra(Rect(bloque.left + bloque.width * fx - w * 0.035f, h * 0.2f, bloque.left + bloque.width * fx + w * 0.035f, h * 0.29f), m) }
        caja(luz, losa)
        bisel(losa, Color.White.copy(alpha = 0.25f), sombra, maxOf(0.6f * u, losa.height * 0.2f))
        caja(mezcla(piedra, Color.Black, 0.12f), plinto)
        drawLine(sombra, Offset(plinto.left, plinto.top), Offset(plinto.right, plinto.top), 0.7f * u)
        destello(Offset(cc.x + cr * 0.9f, cc.y - cr * 0.9f), cr * 0.6f, m)
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC2B79C) else Color(0xFF5A4A38)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        // La lápida: piedra clara de día y piedra negra de noche, con el filete rehundido.
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        caja(if (oscuro) Color(0xFF221F1C) else Color(0xFFE4DAC4), r)
        val g = 2f * u
        drawRect(m.b, Offset(r.left + g, r.top + g), Size(r.width - 2 * g, r.height - 2 * g), style = Stroke(1.2f * u))
        drawRect(if (oscuro) Color.Black.copy(alpha = 0.4f) else t(SILLAR_JUNTA, false).copy(alpha = 0.6f), Offset(r.left + g + 1.8f * u, r.top + g + 1.8f * u),
            Size(r.width - 2 * g - 3.6f * u, r.height - 2 * g - 3.6f * u), style = Stroke(0.5f * u))
        // Una cruz patada del metal del nivel en cada esquina.
        val cr = 2.5f * u; val d = g + 0.5f * u
        listOf(Offset(r.left + d, r.top + d), Offset(r.right - d, r.top + d), Offset(r.left + d, r.bottom - d), Offset(r.right - d, r.bottom - d)).forEach { c ->
            drawCircle(if (oscuro) Color(0xFF221F1C) else Color(0xFFE4DAC4), cr * 1.2f, c)
            drawPath(cruzPatada(c, cr), m.b)
            drawPath(cruzPatada(c, cr), m.o, style = Stroke(0.4f * u))
        }
        destello(Offset(r.right - d, r.top + d), 3.2f * u, m)
    }
}

/**
 * Seda: el caravasar. El trofeo descansa en una peana de laca roja de tres cuerpos: la tabla, la cintura de laca negra con
 * su greca y el disco de jade, y el faldón calado sobre patas de voluta de nube, con cantoneras y zapatas del metal del
 * nivel. En el hall, la mesa baja de laca negra con patas de casco de caballo y la moneda del metal colgada de su cordón; la
 * placa es una cartela de laca de esquinas cortadas con filete del metal.
 */
object LogrosSeda : DisenoLogros {
    private val LACA = Color(0xFFA8261C)
    private val LACA_LUZ = Color(0xFFC9402E)
    private val LACA_OSC = Color(0xFF6E1610)
    private val NEGRO_LACA = Color(0xFF1E1412)
    private val PAPEL = Color(0xFFF1E6CC)
    private val JADE = Color(0xFF6E9C80)
    private val TINTA = Color(0xFF1E1A18)
    private val CORDON = Color(0xFFB0302A)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF110B09), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val tabla = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val cintura = fr(0.2f, 0.8f, 0.8f, 0.835f)
        val faldon = fr(0.15f, 0.835f, 0.85f, 0.875f)
        // Las patas y el faldón calado: un arco con pico en medio entre dos patas que se abren en voluta.
        val pie = Path().apply {
            fillType = PathFillType.EvenOdd
            moveTo(w * 0.16f, h * 0.875f); lineTo(w * 0.84f, h * 0.875f); lineTo(w * 0.84f, h * 0.935f)
            quadraticTo(w * 0.86f, h * 0.95f, w * 0.88f, h * 0.948f); lineTo(w * 0.88f, h * 0.958f); lineTo(w * 0.74f, h * 0.958f)
            lineTo(w * 0.74f, h * 0.925f); quadraticTo(w * 0.66f, h * 0.89f, w * 0.57f, h * 0.9f); quadraticTo(w * 0.52f, h * 0.9f, w * 0.5f, h * 0.915f)
            quadraticTo(w * 0.48f, h * 0.9f, w * 0.43f, h * 0.9f); quadraticTo(w * 0.34f, h * 0.89f, w * 0.26f, h * 0.925f); lineTo(w * 0.26f, h * 0.958f)
            lineTo(w * 0.12f, h * 0.958f); lineTo(w * 0.12f, h * 0.948f); quadraticTo(w * 0.14f, h * 0.95f, w * 0.16f, h * 0.935f); close()
        }
        if (!e.hecho) {
            siluetaDe(Path().apply { addRoundRect(RoundRect(tabla, CornerRadius(h * 0.012f))); addRect(cintura); addRect(faldon); addPath(pie) }, oscuro); return
        }
        val m = metal(e, oscuro)
        val laca = t(LACA, oscuro); val lacaLuz = t(LACA_LUZ, oscuro); val lacaOsc = t(LACA_OSC, oscuro); val negro = t(NEGRO_LACA, oscuro)
        sombraPie(w * 0.11f, w * 0.89f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        drawPath(pie, laca)
        drawPath(pie, lacaOsc, style = Stroke(maxOf(0.5f * u, h * 0.003f)))
        if (fino) listOf(0.15f, 0.85f).forEach { x -> drawCircle(lacaOsc, h * 0.008f, Offset(w * x, h * 0.944f), style = Stroke(0.8f * u)) }
        // Las zapatas del metal en los pies.
        barra(fr(0.12f, 0.95f, 0.26f, 0.96f), m); barra(fr(0.74f, 0.95f, 0.88f, 0.96f), m)
        // El faldón, con el filete de luz.
        caja(laca, faldon)
        drawRect(lacaLuz, faldon.topLeft, Size(faldon.width, maxOf(0.6f * u, faldon.height * 0.18f)))
        drawRect(lacaOsc, Offset(faldon.left, faldon.bottom - faldon.height * 0.2f), Size(faldon.width, faldon.height * 0.2f))
        // La cintura de laca negra, con la greca roja y el disco de jade.
        caja(negro, cintura)
        if (fino) {
            val n = 9; val paso = cintura.width / n; val g = cintura.height * 0.28f; val lw = maxOf(0.6f * u, h * 0.003f)
            for (k in 0 until n) {
                val x = cintura.left + k * paso + paso * 0.2f; val y = cintura.top + cintura.height * 0.25f; val l = paso * 0.6f
                if (k == n / 2) continue
                drawPath(Path().apply { moveTo(x, y + g * 1.8f); lineTo(x, y); lineTo(x + l, y); lineTo(x + l, y + g * 1.8f); lineTo(x + l * 0.35f, y + g * 1.8f); lineTo(x + l * 0.35f, y + g * 0.8f) },
                    laca.copy(alpha = 0.9f), style = Stroke(lw))
            }
        }
        val jc = cintura.center; val jr = cintura.height * 0.75f
        drawCircle(t(JADE, oscuro), jr, jc)
        drawCircle(mezcla(t(JADE, oscuro), Color.White, 0.3f), jr, jc, style = Stroke(maxOf(0.5f * u, jr * 0.12f)))
        drawCircle(negro, jr * 0.32f, jc)
        // La tabla, con su canto redondeado y las cantoneras del metal.
        drawRoundRect(laca, tabla.topLeft, tabla.size, CornerRadius(h * 0.012f))
        drawRect(lacaLuz, Offset(tabla.left + h * 0.01f, tabla.top), Size(tabla.width - h * 0.02f, maxOf(0.6f * u, tabla.height * 0.22f)))
        drawRect(lacaOsc, Offset(tabla.left, tabla.bottom - tabla.height * 0.22f), Size(tabla.width, tabla.height * 0.22f))
        drawRect(m.b, Offset(tabla.left, tabla.bottom - maxOf(0.6f * u, h * 0.004f)), Size(tabla.width, maxOf(0.6f * u, h * 0.004f)))
        listOf(tabla.left, tabla.right - w * 0.045f).forEach { x -> barra(Rect(x, tabla.top, x + w * 0.045f, tabla.bottom), m) }
        destello(Offset(tabla.right - w * 0.02f, tabla.top + tabla.height * 0.3f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val tablero = fr(0.04f, 0f, 0.96f, 0.22f)
        val faldon = Path().apply {
            moveTo(w * 0.1f, h * 0.22f); lineTo(w * 0.9f, h * 0.22f); lineTo(w * 0.9f, h * 0.44f)
            quadraticTo(w * 0.7f, h * 0.4f, w * 0.56f, h * 0.48f); quadraticTo(w * 0.5f, h * 0.56f, w * 0.44f, h * 0.48f); quadraticTo(w * 0.3f, h * 0.4f, w * 0.1f, h * 0.44f); close()
        }
        fun pata(x: Float, s: Float) = Path().apply {
            moveTo(w * x, h * 0.22f); lineTo(w * (x + s * 0.07f), h * 0.22f); lineTo(w * (x + s * 0.06f), h * 0.78f)
            quadraticTo(w * (x + s * 0.06f), h * 0.92f, w * (x + s * 0.11f), h * 0.94f); lineTo(w * (x + s * 0.11f), h); lineTo(w * x, h); close()
        }
        if (!e.hecho) { siluetaDe(Path().apply { addRect(tablero); addPath(faldon); addPath(pata(0.1f, 1f)); addPath(pata(0.9f, -1f)) }, oscuro); return }
        val m = metal(e, oscuro)
        val negro = t(NEGRO_LACA, oscuro); val laca = t(LACA, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        // El cordón de seda colgado al centro, con la moneda del metal: redonda y con el agujero cuadrado.
        val cc = Offset(w * 0.5f, h * 0.72f); val cr = h * 0.16f
        drawLine(t(CORDON, oscuro), Offset(w * 0.5f, h * 0.5f), Offset(cc.x, cc.y - cr), maxOf(0.7f * u, h * 0.04f))
        drawLine(t(CORDON, oscuro), Offset(cc.x - cr * 0.3f, cc.y + cr), Offset(cc.x - cr * 0.5f, h * 0.98f), 0.8f * u)
        drawLine(t(CORDON, oscuro), Offset(cc.x + cr * 0.3f, cc.y + cr), Offset(cc.x + cr * 0.5f, h * 0.98f), 0.8f * u)
        drawCircle(m.o, cr, cc); drawCircle(m.b, cr * 0.82f, cc)
        drawRect(m.o, Offset(cc.x - cr * 0.3f, cc.y - cr * 0.3f), Size(cr * 0.6f, cr * 0.6f))
        drawArc(m.br, 200f, 70f, false, Offset(cc.x - cr * 0.65f, cc.y - cr * 0.65f), Size(cr * 1.3f, cr * 1.3f), style = Stroke(maxOf(0.5f * u, cr * 0.14f)))
        // Las patas de casco de caballo y el faldón rojo con su pico.
        drawPath(pata(0.1f, 1f), negro); drawPath(pata(0.9f, -1f), negro)
        drawPath(faldon, laca)
        drawPath(faldon, t(LACA_OSC, oscuro), style = Stroke(0.7f * u))
        // El tablero de laca negra, con el filo del metal al frente.
        caja(negro, tablero)
        drawRect(t(Color(0xFF4A3530), oscuro), tablero.topLeft, Size(tablero.width, maxOf(0.6f * u, tablero.height * 0.2f)))
        barra(Rect(tablero.left, tablero.bottom - maxOf(1.2f * u, tablero.height * 0.22f), tablero.right, tablero.bottom), m)
        destello(Offset(cc.x + cr * 0.4f, cc.y - cr * 0.4f), cr * 0.7f, m)
    }

    override val margenPlaca: Dp = 8.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFD2B98A) else Color(0xFF6E1610)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        val c = 4f * u
        drawPath(chaflan(r.translate(0.8f * u, 1.2f * u), c), Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f))
        // El marco de laca negra, la banda roja y el filete del metal; dentro, el papel (de noche, laca negra).
        drawPath(chaflan(r, c), t(NEGRO_LACA, oscuro))
        val r1 = r.deflate(1.6f * u)
        drawPath(chaflan(r1, c * 0.8f), t(LACA, oscuro))
        val r2 = r1.deflate(1.6f * u)
        drawPath(chaflan(r2, c * 0.6f), m.b)
        drawPath(chaflan(r2.deflate(0.5f * u), c * 0.55f), m.br.copy(alpha = 0.6f), style = Stroke(0.4f * u))
        val r3 = r2.deflate(1f * u)
        drawPath(chaflan(r3, c * 0.5f), if (oscuro) Color(0xFF1A1110) else PAPEL)
        destello(Offset(r2.right - c * 0.6f, r2.top + 0.5f * u), 3f * u, m)
    }
}

/**
 * Gótico: la librería del coro. El trofeo descansa en un basamento de piedra con una arquería ciega de tres lancetas, cada
 * una con su vidriera de colores emplomada, cuadrifolios del metal del nivel en las enjutas y la banda de latón (del metal)
 * en el plinto. En el hall, un pilar de haces corto: tres columnillas con anillos del metal bajo una losa moldurada; la
 * placa es una lauda de piedra oscura con la orla del metal y un cuadrifolio en cada esquina.
 */
object LogrosGotico : DisenoLogros {
    private val PIEDRA = Color(0xFF8E8A80)
    private val MURO = Color(0xFF6F6B63)
    private val VITELA = Color(0xFFEDE3C9)
    private val TINTA = Color(0xFF22160E)
    private val LOSA_OSCURA = Color(0xFF4A4640)
    private val PLOMO = Color(0xFF1C1A22)
    private val VIDRIOS = listOf(Color(0xFF2A4C9A), Color(0xFFA0222A), Color(0xFFD8A73A), Color(0xFF2E7A4C), Color(0xFF5A3A8A))
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0A08), 0.34f) else c

    /** Un cuadrifolio: cuatro lóbulos alrededor de [c]. */
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, m: Metal) {
        for (k in 0 until 4) {
            val a = Math.toRadians(45.0 + k * 90.0); val p = Offset(c.x + r * 0.45f * kotlin.math.cos(a).toFloat(), c.y + r * 0.45f * kotlin.math.sin(a).toFloat())
            drawCircle(m.o, r * 0.5f, p)
        }
        for (k in 0 until 4) {
            val a = Math.toRadians(45.0 + k * 90.0); val p = Offset(c.x + r * 0.45f * kotlin.math.cos(a).toFloat(), c.y + r * 0.45f * kotlin.math.sin(a).toFloat())
            drawCircle(m.b, r * 0.38f, p)
        }
        drawCircle(m.br, r * 0.2f, c)
    }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val cornisa = fr(0.1f, 0.77f, 0.9f, 0.8f)
        val cuerpo = fr(0.14f, 0.8f, 0.86f, 0.925f)
        val plinto = fr(0.11f, 0.925f, 0.89f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(cornisa); addRect(cuerpo); addRect(plinto) }, oscuro); return }
        val m = metal(e, oscuro)
        val piedra = if (oscuro) mezcla(PIEDRA, Color(0xFF0C0A08), 0.55f) else PIEDRA
        val hueco = if (oscuro) mezcla(MURO, Color.Black, 0.65f) else mezcla(MURO, Color.Black, 0.25f)
        val luz = mezcla(piedra, Color.White, 0.22f); val sombra = mezcla(piedra, Color.Black, 0.3f)
        sombraPie(w * 0.1f, w * 0.9f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombra), cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        // La arquería ciega: tres lancetas con su vidriera emplomada y una columnilla entre una y otra.
        val n = 3; val paso = cuerpo.width / n
        for (k in 0 until n) {
            val ar = Rect(cuerpo.left + k * paso + paso * 0.18f, cuerpo.top + cuerpo.height * 0.1f, cuerpo.left + (k + 1) * paso - paso * 0.18f, cuerpo.bottom - cuerpo.height * 0.1f)
            val arco = arcoApuntado(ar, ar.width * 0.75f)
            drawPath(arco, hueco)
            if (fino) {
                val vr = ar.deflate(ar.width * 0.14f)
                val v = arcoApuntado(vr, vr.width * 0.75f)
                val c1 = VIDRIOS[(k + e.nivel) % VIDRIOS.size]; val c2 = VIDRIOS[(k + e.nivel + 2) % VIDRIOS.size]
                drawPath(v, t(c1, oscuro))
                drawRect(t(c2, oscuro), Offset(vr.left, vr.top + vr.height * 0.55f), Size(vr.width, vr.height * 0.45f))
                drawCircle(t(VIDRIOS[(k + 1) % VIDRIOS.size], oscuro), vr.width * 0.22f, Offset(vr.center.x, vr.top + vr.height * 0.36f))
                val pl = maxOf(0.5f * u, h * 0.0035f)
                drawLine(t(PLOMO, oscuro), Offset(vr.left, vr.top + vr.height * 0.55f), Offset(vr.right, vr.top + vr.height * 0.55f), pl)
                drawLine(t(PLOMO, oscuro), Offset(vr.center.x, vr.top + vr.height * 0.55f), Offset(vr.center.x, vr.bottom), pl)
                drawPath(v, t(PLOMO, oscuro), style = Stroke(pl))
            }
            drawPath(arco, sombra, style = Stroke(maxOf(0.5f * u, h * 0.004f)))
            if (k > 0) {
                val x = cuerpo.left + k * paso
                drawLine(luz, Offset(x, cuerpo.top + cuerpo.height * 0.15f), Offset(x, cuerpo.bottom - cuerpo.height * 0.08f), maxOf(0.8f * u, w * 0.012f))
                cuadrifolio(Offset(x, cuerpo.top + cuerpo.height * 0.2f), maxOf(h * 0.016f, 1.4f * u), m)
            }
        }
        // La cornisa moldurada: el filo de luz, la gola y el goterón.
        caja(luz, cornisa)
        drawRect(sombra, Offset(cornisa.left, cornisa.bottom - cornisa.height * 0.3f), Size(cornisa.width, cornisa.height * 0.3f))
        drawRect(Color.White.copy(alpha = 0.22f), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.22f))
        // El plinto con la banda de latón grabado (del metal del nivel).
        caja(mezcla(piedra, Color.Black, 0.12f), plinto)
        barra(Rect(plinto.left + plinto.width * 0.04f, plinto.top + plinto.height * 0.25f, plinto.right - plinto.width * 0.04f, plinto.top + plinto.height * 0.62f), m)
        if (fino) cuadrifolio(Offset(plinto.center.x, plinto.top + plinto.height * 0.44f), plinto.height * 0.5f, m)
        destello(Offset(plinto.right - plinto.width * 0.1f, plinto.top + plinto.height * 0.3f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val losa = fr(0.05f, 0f, 0.95f, 0.2f)
        val plinto = fr(0.08f, 0.82f, 0.92f, 1f)
        val xs = listOf(0.27f, 0.5f, 0.73f)
        if (!e.hecho) {
            siluetaDe(Path().apply { addRect(losa); addRect(plinto); xs.forEach { x -> addRect(fr(x - 0.06f, 0.2f, x + 0.06f, 0.82f)) } }, oscuro); return
        }
        val m = metal(e, oscuro)
        val piedra = if (oscuro) mezcla(PIEDRA, Color(0xFF0C0A08), 0.55f) else PIEDRA
        val luz = mezcla(piedra, Color.White, 0.22f); val sombra = mezcla(piedra, Color.Black, 0.3f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        // Las tres columnillas del haz, con su capitel, su basa y un anillo del metal a media altura.
        val fuste = Brush.horizontalGradient(listOf(sombra, luz, piedra, sombra), w * 0.2f, w * 0.8f)
        xs.forEach { x ->
            drawRect(fuste, Offset(w * (x - 0.035f), h * 0.2f), Size(w * 0.07f, h * 0.62f))
            drawPath(Path().apply { moveTo(w * (x - 0.07f), h * 0.2f); lineTo(w * (x + 0.07f), h * 0.2f); lineTo(w * (x + 0.04f), h * 0.32f); lineTo(w * (x - 0.04f), h * 0.32f); close() }, piedra)
            caja(piedra, fr(x - 0.06f, 0.72f, x + 0.06f, 0.82f))
            barra(fr(x - 0.045f, 0.48f, x + 0.045f, 0.56f), m)
        }
        caja(luz, losa)
        drawRect(sombra, Offset(losa.left, losa.bottom - losa.height * 0.3f), Size(losa.width, losa.height * 0.3f))
        cuadrifolio(Offset(w * 0.5f, h * 0.09f), h * 0.12f, m)
        caja(mezcla(piedra, Color.Black, 0.1f), plinto)
        drawLine(luz, Offset(plinto.left, plinto.top), Offset(plinto.right, plinto.top), 0.7f * u)
        destello(Offset(w * 0.73f + w * 0.04f, h * 0.5f), h * 0.2f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) VITELA else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC4B898) else Color(0xFF55402A)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        // La lauda: la losa oscura, la orla del metal del nivel y la vitela lisa en medio (de noche, la losa negra).
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        caja(t(LOSA_OSCURA, oscuro), r)
        val r1 = r.deflate(2.2f * u)
        drawRect(m.b, r1.topLeft, r1.size, style = Stroke(1.4f * u))
        drawRect(m.o.copy(alpha = 0.8f), r1.deflate(1f * u).topLeft, r1.deflate(1f * u).size, style = Stroke(0.4f * u))
        caja(if (oscuro) Color(0xFF1E1C1A) else VITELA, r1.deflate(1.3f * u))
        listOf(r1.topLeft, Offset(r1.right, r1.top), Offset(r1.left, r1.bottom), r1.bottomRight).forEach { c -> cuadrifolio(c, 3.4f * u, m) }
        destello(Offset(r1.right, r1.top), 3f * u, m)
    }
}

/**
 * Andalus: la sala del palacio. El trofeo descansa en un zócalo: la losa de mármol, la faja de yeso con su arquería menuda,
 * el alicatado de estrellas de ocho puntas en verde y azul con la estrella del centro del metal del nivel, y el plinto de
 * almagra. En el hall, una mesa baja de cedro con su filete de taracea y dos arcos de herradura calados; la placa es una
 * tablilla de hueso en marco de cedro, con estrellas del metal en las esquinas.
 */
object LogrosAndalus : DisenoLogros {
    private val YESO = Color(0xFFE8DCC2)
    private val YESO_SOMBRA = Color(0xFFC6B696)
    private val ALMAGRA = Color(0xFF9C3A26)
    private val AZUL = Color(0xFF2C4F86)
    private val VERDE = Color(0xFF2F6E52)
    private val CEDRO = Color(0xFF7A4A2A)
    private val CEDRO_LUZ = Color(0xFF96643C)
    private val CEDRO_OSC = Color(0xFF4E2E18)
    private val VETA = Color(0xFF2E1A0C)
    private val MARMOL = Color(0xFFE6E1D6)
    private val HUESO = Color(0xFFE9DFC6)
    private val NEGRO = Color(0xFF1C1A1E)
    private val TINTA = Color(0xFF221812)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.34f) else c
    /** Lo claro (yeso, mármol, hueso), de noche más hondo. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C0A), 0.58f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val losa = fr(0.11f, 0.765f, 0.89f, 0.792f)
        val faja = fr(0.14f, 0.792f, 0.86f, 0.818f)
        val panel = fr(0.14f, 0.818f, 0.86f, 0.925f)
        val plinto = fr(0.12f, 0.925f, 0.88f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(losa); addRect(faja); addRect(panel); addRect(plinto) }, oscuro); return }
        val m = metal(e, oscuro)
        val yeso = n(YESO, oscuro); val yesoS = n(YESO_SOMBRA, oscuro)
        sombraPie(w * 0.1f, w * 0.9f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        // El plinto de almagra.
        caja(t(ALMAGRA, oscuro), plinto)
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(plinto.left, plinto.bottom - plinto.height * 0.25f), Size(plinto.width, plinto.height * 0.25f))
        // El alicatado: fondo blanco, estrellas de ocho puntas alternadas y la del centro del metal.
        caja(yeso, panel)
        val nE = if (fino) 7 else 5
        val paso = panel.width / nE; val er = minOf(paso * 0.45f, panel.height * 0.4f)
        for (k in 0 until nE) {
            val c = Offset(panel.left + paso * (k + 0.5f), panel.center.y)
            if (k == nE / 2) continue
            drawPath(estrella(c, er, 8, 0.72f, -90f + 22.5f), t(if (k % 2 == 0) VERDE else AZUL, oscuro))
            if (fino) drawCircle(yeso, er * 0.28f, c)
            if (fino && k < nE - 1) drawPath(rombo(Offset(c.x + paso * 0.5f, panel.top + panel.height * 0.14f), er * 0.22f, er * 0.22f), t(ALMAGRA, oscuro))
            if (fino && k < nE - 1) drawPath(rombo(Offset(c.x + paso * 0.5f, panel.bottom - panel.height * 0.14f), er * 0.22f, er * 0.22f), t(ALMAGRA, oscuro))
        }
        val cc = panel.center; val cr = minOf(paso * 0.62f, panel.height * 0.5f)
        drawPath(estrella(cc, cr, 8, 0.7f, -90f + 22.5f), m.o)
        drawPath(estrella(cc, cr * 0.84f, 8, 0.7f, -90f + 22.5f), m.b)
        drawPath(estrella(cc, cr * 0.36f, 8, 0.6f), m.br)
        drawRect(t(NEGRO, oscuro), panel.topLeft, panel.size, style = Stroke(maxOf(0.5f * u, h * 0.0035f)))
        // La faja de yeso con su arquería menuda.
        caja(yesoS, faja)
        if (fino) {
            val na = (faja.width / maxOf(faja.height * 0.9f, 1f)).toInt().coerceIn(4, 40); val pa = faja.width / na
            for (k in 0 until na) drawPath(arcoHerradura(Rect(faja.left + k * pa + pa * 0.18f, faja.top + faja.height * 0.2f, faja.left + (k + 1) * pa - pa * 0.18f, faja.bottom)),
                mezcla(yesoS, Color.Black, 0.25f))
        }
        // La losa de mármol.
        caja(n(MARMOL, oscuro), losa)
        bisel(losa, Color.White.copy(alpha = 0.35f), mezcla(n(MARMOL, oscuro), Color.Black, 0.25f), maxOf(0.6f * u, losa.height * 0.2f))
        destello(Offset(cc.x + cr * 0.55f, cc.y - cr * 0.55f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val tablero = fr(0.04f, 0f, 0.96f, 0.2f)
        val taracea = fr(0.06f, 0.2f, 0.94f, 0.32f)
        val cuerpo = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(fr(0.08f, 0.32f, 0.92f, 1f))
            addPath(arcoHerradura(fr(0.17f, 0.42f, 0.42f, 1.01f)))
            addPath(arcoHerradura(fr(0.58f, 0.42f, 0.83f, 1.01f)))
        }
        if (!e.hecho) { siluetaDe(Path().apply { addRect(tablero); addRect(taracea); addPath(cuerpo) }, oscuro); return }
        val m = metal(e, oscuro)
        val cedro = t(CEDRO, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        drawPath(cuerpo, cedro)
        clipPath(cuerpo) { vetas(fr(0.08f, 0.32f, 0.92f, 1f), t(VETA, oscuro).copy(alpha = 0.25f), e.semilla, densidad = 0.6f, grosor = 0.5f) }
        drawPath(cuerpo, t(CEDRO_OSC, oscuro), style = Stroke(0.7f * u))
        // El filete de taracea: triangulitos de hueso y ébano.
        caja(t(NEGRO, oscuro), taracea)
        val nt = (taracea.width / maxOf(2.4f * u, 1f)).toInt().coerceIn(8, 60); val pt = taracea.width / nt
        for (k in 0 until nt) {
            val x = taracea.left + k * pt
            drawPath(Path().apply { moveTo(x, taracea.bottom); lineTo(x + pt / 2, taracea.top); lineTo(x + pt, taracea.bottom); close() }, n(HUESO, oscuro))
        }
        // La estrella del metal entre los dos arcos.
        val sc = Offset(w * 0.5f, h * 0.62f); val sr = h * 0.2f
        drawPath(estrella(sc, sr, 8, 0.7f, -90f + 22.5f), m.o)
        drawPath(estrella(sc, sr * 0.8f, 8, 0.7f, -90f + 22.5f), m.b)
        drawCircle(m.br, sr * 0.25f, sc)
        // El tablero, con su canto.
        caja(cedro, tablero)
        bisel(tablero, t(CEDRO_LUZ, oscuro), t(CEDRO_OSC, oscuro), maxOf(0.6f * u, tablero.height * 0.2f))
        destello(Offset(sc.x + sr * 0.6f, sc.y - sr * 0.6f), sr * 0.7f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) HUESO else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC9B48A) else Color(0xFF7A3424)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size, CornerRadius(1.5f * u))
        // El marco de cedro, el filete del metal y la tablilla de hueso (de noche, de ébano).
        drawRoundRect(t(CEDRO, oscuro), r.topLeft, r.size, CornerRadius(1.5f * u))
        drawRoundRect(t(CEDRO_OSC, oscuro), r.topLeft, r.size, CornerRadius(1.5f * u), style = Stroke(0.6f * u))
        val r1 = r.deflate(2.4f * u)
        caja(m.b, r1)
        drawRect(m.br.copy(alpha = 0.7f), r1.topLeft, Size(r1.width, 0.4f * u))
        caja(if (oscuro) Color(0xFF1A181C) else HUESO, r1.deflate(0.9f * u))
        // Una estrella de ocho puntas del metal en cada esquina.
        listOf(r1.topLeft, Offset(r1.right, r1.top), Offset(r1.left, r1.bottom), r1.bottomRight).forEach { c ->
            drawPath(estrella(c, 3.4f * u, 8, 0.66f, -90f + 22.5f), m.o)
            drawPath(estrella(c, 2.7f * u, 8, 0.66f, -90f + 22.5f), m.b)
            drawCircle(m.br, 0.7f * u, c)
        }
        destello(Offset(r1.right, r1.top), 3f * u, m)
    }
}

/**
 * Mongol: la ger. El trofeo descansa en un arcón pintado de anaranjado con su almohadón de fieltro cosido en rojo, dos
 * paneles azules con cuernos de carnero, escuadras y medallón del metal del nivel. En el hall, el estrado de fieltros
 * apilados con su pespunte y el medallón del metal; la placa es una tira de fieltro con el cordón doble y un cuerno del
 * metal en cada esquina.
 */
object LogrosMongol : DisenoLogros {
    private val ROJO = Color(0xFFB0301F)
    private val ROJO_OSC = Color(0xFF6E1C12)
    private val NARANJA = Color(0xFFDA7A2C)
    private val AZUL = Color(0xFF2D5A8F)
    private val ORO = Color(0xFFE0B54A)
    private val FIELTRO = Color(0xFFE9E0CC)
    private val FIELTRO_OSC = Color(0xFFC9BEA6)
    private val KHANA = Color(0xFFB5652E)
    private val CREMA = Color(0xFFF0E6CC)
    private val TINTA = Color(0xFF231812)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B09), 0.34f) else c
    /** El fieltro, de noche más hondo. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0F0B09), 0.56f) else c

    /** Un cuerno de carnero: dos volutas que se abren desde [c] (el motivo de los fieltros), de [r] de ancho. */
    private fun cuerno(c: Offset, r: Float): Path = Path().apply {
        moveTo(c.x, c.y + r * 0.5f); lineTo(c.x, c.y)
        cubicTo(c.x, c.y - r * 0.6f, c.x - r * 0.9f, c.y - r * 0.6f, c.x - r * 0.9f, c.y)
        cubicTo(c.x - r * 0.9f, c.y + r * 0.35f, c.x - r * 0.45f, c.y + r * 0.35f, c.x - r * 0.45f, c.y)
        moveTo(c.x, c.y)
        cubicTo(c.x, c.y - r * 0.6f, c.x + r * 0.9f, c.y - r * 0.6f, c.x + r * 0.9f, c.y)
        cubicTo(c.x + r * 0.9f, c.y + r * 0.35f, c.x + r * 0.45f, c.y + r * 0.35f, c.x + r * 0.45f, c.y)
    }

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val cojin = fr(0.12f, 0.762f, 0.88f, 0.795f)
        val arcon = fr(0.15f, 0.795f, 0.85f, 0.93f)
        if (!e.hecho) {
            siluetaDe(Path().apply { addRoundRect(RoundRect(cojin, CornerRadius(h * 0.016f))); addRect(arcon); addRect(fr(0.17f, 0.93f, 0.25f, 0.958f)); addRect(fr(0.75f, 0.93f, 0.83f, 0.958f)) }, oscuro); return
        }
        val m = metal(e, oscuro)
        val naranja = t(NARANJA, oscuro); val rojo = t(ROJO, oscuro); val azul = t(AZUL, oscuro); val oro = t(ORO, oscuro)
        sombraPie(w * 0.12f, w * 0.88f, h * 0.962f, h * 0.02f, if (oscuro) 0.4f else 0.22f)
        listOf(0.17f, 0.75f).forEach { x -> caja(t(ROJO_OSC, oscuro), fr(x, 0.93f, x + 0.08f, 0.958f)) }
        // El arcón: fondo anaranjado, marco rojo y dos paneles azules con cuernos de oro.
        drawRect(Brush.verticalGradient(listOf(mezcla(naranja, Color.White, 0.12f), naranja, t(KHANA, oscuro)), arcon.top, arcon.bottom), arcon.topLeft, arcon.size)
        drawRect(rojo, arcon.topLeft, arcon.size, style = Stroke(maxOf(0.8f * u, h * 0.008f)))
        val lw = maxOf(0.6f * u, h * 0.004f)
        listOf(0.06f, 0.6f).forEach { fx ->
            val p = Rect(arcon.left + arcon.width * fx, arcon.top + arcon.height * 0.18f, arcon.left + arcon.width * (fx + 0.34f), arcon.bottom - arcon.height * 0.18f)
            caja(azul, p)
            drawRect(oro, p.topLeft, p.size, style = Stroke(lw))
            if (fino) {
                drawPath(cuerno(p.center, p.height * 0.42f), oro, style = Stroke(lw, cap = StrokeCap.Round))
                listOf(p.left + p.width * 0.15f, p.right - p.width * 0.15f).forEach { x -> drawCircle(oro, lw * 0.9f, Offset(x, p.center.y)) }
            }
        }
        // El medallón del metal con el nudo sin fin y las escuadras del metal en las esquinas.
        val mc = arcon.center; val mr = arcon.height * 0.3f
        drawCircle(m.o, mr, mc); drawCircle(m.b, mr * 0.84f, mc)
        if (fino) {
            drawPath(rombo(mc, mr * 0.55f, mr * 0.55f), m.o, style = Stroke(lw))
            drawPath(rombo(mc, mr * 0.28f, mr * 0.28f), m.o, style = Stroke(lw))
            drawArc(m.br, 200f, 60f, false, Offset(mc.x - mr * 0.7f, mc.y - mr * 0.7f), Size(mr * 1.4f, mr * 1.4f), style = Stroke(lw))
        } else drawCircle(m.br, mr * 0.3f, Offset(mc.x - mr * 0.2f, mc.y - mr * 0.2f))
        val l = arcon.height * 0.26f; val g = maxOf(1f * u, arcon.height * 0.09f)
        escuadra(arcon.left, arcon.top, 1f, 1f, l, g, m); escuadra(arcon.right, arcon.top, -1f, 1f, l, g, m)
        escuadra(arcon.left, arcon.bottom, 1f, -1f, l, g, m); escuadra(arcon.right, arcon.bottom, -1f, -1f, l, g, m)
        // El almohadón de fieltro con su cordón rojo cosido.
        drawRoundRect(n(FIELTRO, oscuro), cojin.topLeft, cojin.size, CornerRadius(h * 0.016f))
        drawRoundRect(n(FIELTRO_OSC, oscuro), Offset(cojin.left, cojin.center.y), Size(cojin.width, cojin.height / 2), CornerRadius(h * 0.016f))
        drawLine(rojo, Offset(cojin.left + w * 0.02f, cojin.center.y), Offset(cojin.right - w * 0.02f, cojin.center.y), maxOf(0.7f * u, h * 0.005f),
            pathEffect = if (fino) PathEffect.dashPathEffect(floatArrayOf(3f * u, 1.5f * u)) else null)
        destello(Offset(mc.x + mr * 0.5f, mc.y - mr * 0.5f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val capas = listOf(fr(0.16f, 0f, 0.84f, 0.34f), fr(0.1f, 0.32f, 0.9f, 0.66f), fr(0.04f, 0.64f, 0.96f, 1f))
        if (!e.hecho) { siluetaDe(Path().apply { capas.forEach { addRoundRect(RoundRect(it, CornerRadius(h * 0.14f))) } }, oscuro); return }
        val m = metal(e, oscuro)
        val bordes = listOf(t(ROJO, oscuro), t(AZUL, oscuro), t(ROJO_OSC, oscuro))
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        // Tres fieltros apilados, de abajo arriba, cada uno con su ribete y su pespunte de cuernos.
        for (k in 2 downTo 0) {
            val c = capas[k]
            drawRoundRect(mezcla(n(FIELTRO, oscuro), Color.Black, k * 0.06f), c.topLeft, c.size, CornerRadius(h * 0.14f))
            drawRoundRect(bordes[k], c.topLeft, c.size, CornerRadius(h * 0.14f), style = Stroke(maxOf(0.8f * u, h * 0.06f)))
            val nc = (c.width / maxOf(c.height * 1.6f, 1f)).toInt().coerceIn(2, 14); val pc = c.width / nc
            for (i in 0 until nc) {
                if (k == 0 && i == nc / 2) continue
                drawPath(cuerno(Offset(c.left + pc * (i + 0.5f), c.center.y + c.height * 0.05f), c.height * 0.32f), bordes[k].copy(alpha = 0.75f), style = Stroke(0.6f * u))
            }
        }
        // El medallón del metal al frente del fieltro de arriba.
        val mc = Offset(w * 0.5f, capas[0].center.y); val mr = capas[0].height * 0.42f
        drawCircle(m.o, mr, mc); drawCircle(m.b, mr * 0.8f, mc); drawCircle(m.br, mr * 0.28f, Offset(mc.x - mr * 0.2f, mc.y - mr * 0.2f))
        destello(Offset(mc.x + mr * 0.6f, mc.y - mr * 0.5f), mr * 0.8f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) CREMA else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC9BEA6) else Color(0xFF7A2A1C)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        val rad = CornerRadius(3f * u)
        drawRoundRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size, rad)
        // El fieltro (de noche, teñido de azul de medianoche) con el ribete rojo y el cordón doble del metal.
        drawRoundRect(if (oscuro) Color(0xFF14192A) else FIELTRO, r.topLeft, r.size, rad)
        drawRoundRect(t(ROJO, oscuro), r.topLeft, r.size, rad, style = Stroke(1.6f * u))
        val r1 = r.deflate(2.6f * u)
        drawRoundRect(m.b, r1.topLeft, r1.size, CornerRadius(2f * u), style = Stroke(0.8f * u))
        val r2 = r1.deflate(1.4f * u)
        drawRoundRect(m.o.copy(alpha = 0.8f), r2.topLeft, r2.size, CornerRadius(1.5f * u), style = Stroke(0.5f * u))
        // Un cuerno del metal en cada esquina, mirando hacia adentro.
        val cr = 3.2f * u
        listOf(Triple(r1.left + cr, r1.top + cr * 0.9f, 1f), Triple(r1.right - cr, r1.top + cr * 0.9f, 1f), Triple(r1.left + cr, r1.bottom - cr * 0.9f, -1f), Triple(r1.right - cr, r1.bottom - cr * 0.9f, -1f)).forEach { (x, y, s) ->
            val c = Offset(x, y)
            drawPath(cuerno(c, cr).apply { if (s < 0f) transform(androidx.compose.ui.graphics.Matrix().apply { translate(0f, 2 * y); scale(1f, -1f) }) }, m.b, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        }
        destello(Offset(r1.right - cr, r1.top + cr), 3f * u, m)
    }
}

/**
 * Edo: la tienda. El trofeo descansa en un sanbō: la bandeja de paulonia con su reborde de laca negra, el filete
 * bermellón y la caja de laca negra con el vano recortado (kurikata), con los herrajes en ele de los arcones tansu del metal
 * del nivel. En el hall, un tansu bajo de paulonia con dos cajones y sus tiradores de anilla del metal; la placa es una tabla
 * de paulonia con ribete de laca y herrajes del metal en las esquinas.
 */
object LogrosEdo : DisenoLogros {
    private val KIRI = Color(0xFFDCC7A1)
    private val KIRI_LUZ = Color(0xFFEBDDBF)
    private val KIRI_SOMBRA = Color(0xFFB39770)
    private val KIRI_VETA = Color(0xFF9E825A)
    private val URUSHI = Color(0xFF231C18)
    private val URUSHI_LUZ = Color(0xFF4F4137)
    private val SUMI = Color(0xFF1B1A1E)
    private val BERMELLON = Color(0xFFB8322A)
    private val ZOGE = Color(0xFFF1E8D2)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF120E0B), 0.32f) else c
    /** La paulonia, de noche más honda. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF120E0B), 0.55f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val bandeja = fr(0.12f, 0.765f, 0.88f, 0.8f)
        val caja0 = fr(0.17f, 0.806f, 0.83f, 0.93f)
        val base = fr(0.14f, 0.93f, 0.86f, 0.958f)
        val vano = RoundRect(Rect(w * 0.37f, h * 0.835f, w * 0.63f, h * 0.905f), CornerRadius(h * 0.035f))
        val cuerpo = Path().apply { fillType = PathFillType.EvenOdd; addRect(caja0); addRoundRect(vano) }
        if (!e.hecho) { siluetaDe(Path().apply { addRect(bandeja); addPath(cuerpo); addRect(base) }, oscuro); return }
        val m = metal(e, oscuro)
        val urushi = t(URUSHI, oscuro); val urushiLuz = t(URUSHI_LUZ, oscuro)
        sombraPie(w * 0.12f, w * 0.88f, h * 0.962f, h * 0.02f, if (oscuro) 0.42f else 0.22f)
        // La base de laca.
        caja(urushi, base)
        drawRect(urushiLuz, base.topLeft, Size(base.width, maxOf(0.5f * u, base.height * 0.18f)))
        // La caja de laca negra con su vano: por el vano se ve la paulonia de adentro, en sombra.
        drawRoundRect(n(KIRI_SOMBRA, oscuro).let { mezcla(it, Color.Black, 0.35f) }, Offset(vano.left, vano.top), Size(vano.width, vano.height), CornerRadius(h * 0.035f))
        drawPath(cuerpo, urushi)
        drawRect(urushiLuz.copy(alpha = 0.8f), Offset(caja0.left, caja0.top), Size(maxOf(0.6f * u, caja0.width * 0.02f), caja0.height))
        if (oscuro) drawRect(urushiLuz, caja0.topLeft, caja0.size, style = Stroke(maxOf(0.5f * u, h * 0.003f)))
        if (fino) drawRoundRect(urushiLuz, Offset(vano.left, vano.top), Size(vano.width, vano.height), CornerRadius(h * 0.035f), style = Stroke(0.7f * u))
        // Los herrajes en ele del metal, con su remache, en las cuatro esquinas de la caja.
        val l = caja0.height * 0.36f; val g = maxOf(1.1f * u, caja0.height * 0.11f)
        escuadra(caja0.left, caja0.top, 1f, 1f, l, g, m); escuadra(caja0.right, caja0.top, -1f, 1f, l, g, m)
        escuadra(caja0.left, caja0.bottom, 1f, -1f, l, g, m); escuadra(caja0.right, caja0.bottom, -1f, -1f, l, g, m)
        // El filete bermellón y la bandeja de paulonia con su reborde de laca.
        caja(t(BERMELLON, oscuro), fr(0.15f, 0.8f, 0.85f, 0.806f))
        caja(n(KIRI, oscuro), bandeja)
        if (fino) vetas(bandeja, n(KIRI_VETA, oscuro).copy(alpha = 0.55f), e.semilla, densidad = 0.7f, grosor = 0.5f)
        drawRect(n(KIRI_LUZ, oscuro), bandeja.topLeft, Size(bandeja.width, maxOf(0.5f * u, bandeja.height * 0.15f)))
        drawRect(urushi, Offset(bandeja.left, bandeja.top - maxOf(0.8f * u, h * 0.006f)), Size(bandeja.width, maxOf(0.8f * u, h * 0.006f)))
        listOf(bandeja.left, bandeja.right - maxOf(0.8f * u, w * 0.012f)).forEach { x -> drawRect(urushi, Offset(x, bandeja.top - h * 0.006f), Size(maxOf(0.8f * u, w * 0.012f), bandeja.height + h * 0.006f)) }
        destello(Offset(caja0.right - g, caja0.top + g), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val cuerpo = fr(0.06f, 0.04f, 0.94f, 0.92f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(cuerpo); addRect(fr(0.08f, 0.92f, 0.16f, 1f)); addRect(fr(0.84f, 0.92f, 0.92f, 1f)) }, oscuro); return }
        val m = metal(e, oscuro)
        val urushi = t(URUSHI, oscuro); val kiri = n(KIRI, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        listOf(0.08f, 0.84f).forEach { x -> caja(urushi, fr(x, 0.9f, x + 0.08f, 1f)) }
        caja(kiri, cuerpo)
        vetas(cuerpo, n(KIRI_VETA, oscuro).copy(alpha = 0.45f), e.semilla, densidad = 0.6f, grosor = 0.5f)
        // El bastidor de laca: canto de arriba, de abajo y el montante de en medio.
        val lw = maxOf(1f * u, h * 0.08f)
        drawRect(urushi, cuerpo.topLeft, cuerpo.size, style = Stroke(lw))
        drawLine(urushi, Offset(w * 0.5f, cuerpo.top), Offset(w * 0.5f, cuerpo.bottom), lw)
        // Dos cajones, cada uno con su tirador de anilla del metal.
        listOf(0.28f, 0.72f).forEach { x ->
            val c = Offset(w * x, h * 0.42f); val r = h * 0.14f
            drawRoundRect(m.o, Offset(c.x - r * 1.3f, c.y - r * 0.7f), Size(r * 2.6f, r * 1.4f), CornerRadius(r * 0.5f))
            drawRoundRect(m.b, Offset(c.x - r * 1.1f, c.y - r * 0.55f), Size(r * 2.2f, r * 1.1f), CornerRadius(r * 0.4f))
            drawArc(m.b, 0f, 180f, false, Offset(c.x - r * 1.1f, c.y - r * 0.2f), Size(r * 2.2f, r * 2f), style = Stroke(maxOf(0.8f * u, r * 0.3f)))
            drawArc(m.br, 20f, 60f, false, Offset(c.x - r * 1.1f, c.y - r * 0.2f), Size(r * 2.2f, r * 2f), style = Stroke(maxOf(0.4f * u, r * 0.12f)))
        }
        // Las escuadras de hierro de las esquinas.
        val hierro = Metal(t(SUMI, oscuro), t(Color(0xFF3A3530), oscuro), t(Color(0xFF6A625A), oscuro), false)
        val l = h * 0.32f; val g = maxOf(1f * u, h * 0.1f)
        escuadra(cuerpo.left, cuerpo.top, 1f, 1f, l, g, hierro); escuadra(cuerpo.right, cuerpo.top, -1f, 1f, l, g, hierro)
        escuadra(cuerpo.left, cuerpo.bottom, 1f, -1f, l, g, hierro); escuadra(cuerpo.right, cuerpo.bottom, -1f, -1f, l, g, hierro)
        destello(Offset(w * 0.72f + h * 0.14f, h * 0.36f), h * 0.18f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) ZOGE else SUMI
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFCDB894) else Color(0xFF7E2620)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        // El ribete de laca negra y la tabla de paulonia lisa (de noche, laca).
        caja(t(URUSHI, oscuro), r)
        val r1 = r.deflate(2.2f * u)
        caja(if (oscuro) Color(0xFF1C1714) else KIRI_LUZ, r1)
        drawRect(t(BERMELLON, oscuro), r1.topLeft, r1.size, style = Stroke(0.5f * u))
        // Los herrajes en ele del metal.
        val l = minOf(w, h) * 0.24f; val g = 2.6f * u
        escuadra(r.left, r.top, 1f, 1f, l, g, m); escuadra(r.right, r.top, -1f, 1f, l, g, m)
        escuadra(r.left, r.bottom, 1f, -1f, l, g, m); escuadra(r.right, r.bottom, -1f, -1f, l, g, m)
        destello(Offset(r.right - g, r.top + g), 3f * u, m)
    }
}

/**
 * Renacimiento: el studiolo. El trofeo descansa en un pedestal a la florentina: el dado de intonaco blanco entre pilastras
 * de pietra serena, la cornisa con su fila de óvalos, el panel rehundido con el tondo del metal del nivel y dos festones de
 * sanguina. En el hall, una credenza de nogal con taracea de boj, volutas por patas y el escudete del metal; la placa es una
 * cartela de pergamino con los cantos enrollados y el marco del metal.
 */
object LogrosRenacimiento : DisenoLogros {
    private val NOGAL = Color(0xFF5A3B24)
    private val NOGAL_LUZ = Color(0xFF7C5636)
    private val NOGAL_OSC = Color(0xFF3A2416)
    private val VETA = Color(0xFF22140A)
    private val BOJ = Color(0xFFE2C98E)
    private val PERGAMINO = Color(0xFFEFE5CB)
    private val TINTA = Color(0xFF2A1E14)
    private val SANGUINA = Color(0xFFA8503A)
    private val SERENA = Color(0xFF8C9188)
    private val INTONACO = Color(0xFFF0EADB)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.34f) else c
    /** La piedra y el revoque, de noche más hondos. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF100C08), 0.58f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val cornisa = fr(0.1f, 0.765f, 0.9f, 0.79f)
        val friso = fr(0.13f, 0.79f, 0.87f, 0.802f)
        val dado = fr(0.15f, 0.802f, 0.85f, 0.912f)
        val toro = fr(0.12f, 0.912f, 0.88f, 0.93f)
        val plinto = fr(0.1f, 0.93f, 0.9f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(cornisa); addRect(friso); addRect(dado); addRect(toro); addRect(plinto) }, oscuro); return }
        val m = metal(e, oscuro)
        val serena = n(SERENA, oscuro); val serenaLuz = mezcla(serena, Color.White, 0.2f); val serenaOsc = mezcla(serena, Color.Black, 0.3f)
        val blanco = n(INTONACO, oscuro)
        sombraPie(w * 0.1f, w * 0.9f, h * 0.962f, h * 0.02f, if (oscuro) 0.42f else 0.22f)
        // La basa: el plinto y el toro de pietra serena.
        caja(serenaOsc, plinto); caja(serena, toro)
        drawRect(serenaLuz, toro.topLeft, Size(toro.width, toro.height * 0.35f))
        // El dado de intonaco, las pilastras y el panel rehundido.
        caja(blanco, dado)
        listOf(dado.left, dado.right - dado.width * 0.07f).forEach { x ->
            val p = Rect(x, dado.top, x + dado.width * 0.07f, dado.bottom)
            caja(serena, p)
            if (fino) listOf(0.33f, 0.66f).forEach { fx -> drawLine(serenaOsc, Offset(p.left + p.width * fx, p.top + p.height * 0.1f), Offset(p.left + p.width * fx, p.bottom - p.height * 0.1f), 0.6f * u) }
        }
        val panel = Rect(dado.left + dado.width * 0.16f, dado.top + dado.height * 0.14f, dado.right - dado.width * 0.16f, dado.bottom - dado.height * 0.14f)
        drawRect(mezcla(blanco, Color.Black, 0.06f), panel.topLeft, panel.size)
        drawRect(serena, panel.topLeft, panel.size, style = Stroke(maxOf(0.6f * u, h * 0.005f)))
        if (fino) {
            // Dos festones de sanguina colgados del tondo hasta las esquinas del panel.
            val fs = Stroke(maxOf(1f * u, h * 0.008f), cap = StrokeCap.Round)
            listOf(-1f, 1f).forEach { s ->
                val x0 = panel.center.x + s * panel.height * 0.42f; val x1 = panel.center.x + s * panel.width * 0.44f
                drawPath(Path().apply { moveTo(x0, panel.top + panel.height * 0.3f); quadraticTo((x0 + x1) / 2, panel.bottom - panel.height * 0.1f, x1, panel.top + panel.height * 0.2f) }, t(SANGUINA, oscuro), style = fs)
                drawCircle(t(SANGUINA, oscuro), h * 0.006f, Offset(x1, panel.top + panel.height * 0.2f))
            }
        }
        // El tondo del metal del nivel: el aro, la cara y la luz.
        val tc = panel.center; val tr = panel.height * 0.4f
        drawCircle(m.o, tr, tc); drawCircle(m.b, tr * 0.82f, tc)
        drawArc(m.br, 200f, 70f, false, Offset(tc.x - tr * 0.62f, tc.y - tr * 0.62f), Size(tr * 1.24f, tr * 1.24f), style = Stroke(maxOf(0.5f * u, tr * 0.14f)))
        if (fino) drawCircle(m.o.copy(alpha = 0.6f), tr * 0.3f, tc, style = Stroke(0.6f * u))
        // El friso y la cornisa con su fila de óvalos.
        caja(serenaOsc, friso)
        caja(serena, cornisa)
        drawRect(serenaLuz, cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.3f))
        if (fino) {
            val no = (cornisa.width / maxOf(cornisa.height * 0.9f, 1f)).toInt().coerceIn(6, 50); val po = cornisa.width / no
            for (k in 0 until no) drawOval(serenaOsc, Offset(cornisa.left + k * po + po * 0.22f, cornisa.top + cornisa.height * 0.4f), Size(po * 0.56f, cornisa.height * 0.5f))
        }
        destello(Offset(tc.x + tr * 0.55f, tc.y - tr * 0.55f), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val tablero = fr(0.04f, 0f, 0.96f, 0.18f)
        val friso = fr(0.08f, 0.18f, 0.92f, 0.62f)
        if (!e.hecho) {
            siluetaDe(Path().apply { addRect(tablero); addRect(friso); addRect(fr(0.08f, 0.62f, 0.2f, 1f)); addRect(fr(0.8f, 0.62f, 0.92f, 1f)) }, oscuro); return
        }
        val m = metal(e, oscuro)
        val nogal = t(NOGAL, oscuro); val luz = t(NOGAL_LUZ, oscuro); val osc = t(NOGAL_OSC, oscuro); val boj = t(BOJ, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.06f, h * 0.94f), Size(w * 0.88f, h * 0.06f))
        // Las patas de voluta: el rollo arriba, la garra abajo.
        listOf(0.14f to 1f, 0.86f to -1f).forEach { (x, s) ->
            drawPath(Path().apply {
                moveTo(w * (x - 0.05f), h * 0.62f); lineTo(w * (x + 0.05f), h * 0.62f)
                quadraticTo(w * (x + s * 0.0f), h * 0.8f, w * (x + 0.06f), h); lineTo(w * (x - 0.06f), h); quadraticTo(w * x, h * 0.8f, w * (x - 0.05f), h * 0.62f); close()
            }, osc)
            drawCircle(luz, h * 0.1f, Offset(w * x, h * 0.7f), style = Stroke(maxOf(0.6f * u, h * 0.04f)))
        }
        // El friso con dos paneles de taracea (rombos de boj) y el escudete del metal en medio.
        caja(nogal, friso)
        vetas(friso, t(VETA, oscuro).copy(alpha = 0.3f), e.semilla, densidad = 0.6f, grosor = 0.5f)
        listOf(0.12f to 0.42f, 0.58f to 0.88f).forEach { (a, b) ->
            val p = fr(a, 0.26f, b, 0.54f)
            caja(osc, p)
            val nr = (p.width / maxOf(p.height * 0.9f, 1f)).toInt().coerceIn(2, 20); val pr = p.width / nr
            for (k in 0 until nr) drawPath(rombo(Offset(p.left + pr * (k + 0.5f), p.center.y), pr * 0.42f, p.height * 0.4f), boj)
            drawRect(boj, p.topLeft, p.size, style = Stroke(0.5f * u))
        }
        val ec = Offset(w * 0.5f, h * 0.4f); val er = h * 0.17f
        drawPath(rombo(ec, er * 1.2f, er * 1.2f), m.o); drawPath(rombo(ec, er * 0.95f, er * 0.95f), m.b); drawCircle(m.br, er * 0.3f, Offset(ec.x - er * 0.1f, ec.y - er * 0.15f))
        // El tablero moldurado.
        caja(nogal, tablero)
        bisel(tablero, luz, osc, maxOf(0.6f * u, tablero.height * 0.22f))
        destello(Offset(ec.x + er, ec.y - er), er, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) PERGAMINO else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFCBB48C) else Color(0xFF7A3A28)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val rollo = 3.2f * u
        val r = Rect(rollo, 1.5f * u, w - rollo, h - 1.5f * u)
        val fondo = if (oscuro) Color(0xFF241A12) else PERGAMINO
        val canto = if (oscuro) Color(0xFF3A2A1C) else mezcla(PERGAMINO, Color(0xFF8A6A40), 0.35f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        // La cartela: el pergamino liso con el marco del metal y los dos cantos enrollados.
        caja(fondo, r)
        val r1 = r.deflate(1.8f * u)
        drawRect(m.b, r1.topLeft, r1.size, style = Stroke(1.1f * u))
        drawRect(m.o.copy(alpha = 0.7f), r1.deflate(1.2f * u).topLeft, r1.deflate(1.2f * u).size, style = Stroke(0.4f * u))
        listOf(0f to 1f, w to -1f).forEach { (x, s) ->
            val rr = Rect(minOf(x, x + s * rollo * 2f), 0f, maxOf(x, x + s * rollo * 2f), h)
            drawRoundRect(canto, rr.topLeft, rr.size, CornerRadius(rollo))
            drawRoundRect(mezcla(canto, Color.Black, 0.3f), rr.topLeft, rr.size, CornerRadius(rollo), style = Stroke(0.5f * u))
            drawLine(mezcla(canto, Color.White, 0.3f), Offset(rr.center.x - s * rollo * 0.3f, rollo), Offset(rr.center.x - s * rollo * 0.3f, h - rollo), 0.6f * u)
            listOf(rollo, h - rollo).forEach { y -> remache(Offset(rr.center.x, y), rollo * 0.55f, m) }
        }
        destello(Offset(w - rollo, rollo), 3f * u, m)
    }
}

/**
 * Pirata: el camarote. El trofeo descansa en un cofre de marinero sobre la cubierta: tablones calafateados con brea, el
 * cofre de roble más ancho abajo, las asas de cabo a los costados, y las cantoneras y el pasador del metal del nivel. En el
 * hall, una escotilla de enjaretado con sus argollas del metal; la placa es un rótulo de papel con la relinga de cabo y un
 * ollao del metal en cada esquina.
 */
object LogrosPirata : DisenoLogros {
    private val ROBLE = Color(0xFF4E3524)
    private val ROBLE_LUZ = Color(0xFF6E4C33)
    private val ROBLE_OSC = Color(0xFF2E1E14)
    private val VETA = Color(0xFF1C120A)
    private val PAPEL = Color(0xFFE6DCC0)
    private val TINTA = Color(0xFF231A12)
    private val CABO = Color(0xFFB8A070)
    private val CABO_OSC = Color(0xFF7E6A44)
    private val CUBIERTA = Color(0xFF8A6A48)
    private val BREA = Color(0xFF1E1610)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0C0A08), 0.34f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val tapa = fr(0.15f, 0.765f, 0.85f, 0.797f)
        val cofre = Path().apply { moveTo(w * 0.18f, h * 0.797f); lineTo(w * 0.82f, h * 0.797f); lineTo(w * 0.85f, h * 0.915f); lineTo(w * 0.15f, h * 0.915f); close() }
        val cubierta = fr(0.06f, 0.915f, 0.94f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(tapa); addPath(cofre); addRect(cubierta) }, oscuro); return }
        val m = metal(e, oscuro)
        val roble = t(ROBLE, oscuro); val luz = t(ROBLE_LUZ, oscuro); val osc = t(ROBLE_OSC, oscuro); val brea = t(BREA, oscuro)
        val cabo = t(CABO, oscuro); val caboOsc = t(CABO_OSC, oscuro)
        sombraPie(w * 0.06f, w * 0.94f, h * 0.962f, h * 0.018f, if (oscuro) 0.4f else 0.2f)
        // La cubierta: dos tablones calafateados, con sus clavos.
        val cub = t(CUBIERTA, oscuro)
        caja(cub, cubierta)
        drawLine(brea, Offset(cubierta.left, cubierta.center.y), Offset(cubierta.right, cubierta.center.y), maxOf(0.6f * u, h * 0.004f))
        listOf(0.3f, 0.7f).forEach { fx -> drawLine(brea, Offset(cubierta.left + cubierta.width * fx, cubierta.top), Offset(cubierta.left + cubierta.width * fx, cubierta.center.y), maxOf(0.5f * u, h * 0.003f)) }
        drawRect(Color.Black.copy(alpha = 0.25f), Offset(cubierta.left, cubierta.bottom - cubierta.height * 0.15f), Size(cubierta.width, cubierta.height * 0.15f))
        // Las asas de cabo a los costados.
        val sc = Stroke(maxOf(1f * u, h * 0.012f), cap = StrokeCap.Round)
        listOf(0.165f to -1f, 0.835f to 1f).forEach { (x, s) ->
            val p = Path().apply { moveTo(w * x, h * 0.82f); cubicTo(w * (x + s * 0.07f), h * 0.82f, w * (x + s * 0.07f), h * 0.89f, w * x, h * 0.89f) }
            drawPath(p, caboOsc, style = Stroke(sc.width * 1.3f, cap = StrokeCap.Round))
            drawPath(p, cabo, style = sc)
            if (fino) drawPath(p, caboOsc, style = Stroke(sc.width * 0.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.8f * u, 1.6f * u))))
        }
        // El cofre: tablas de roble y su canto.
        drawPath(cofre, roble)
        if (fino) clipPath(cofre) { vetas(fr(0.15f, 0.797f, 0.85f, 0.915f), t(VETA, oscuro).copy(alpha = 0.45f), e.semilla, densidad = 0.8f, grosor = 0.6f) }
        listOf(0.836f, 0.876f).forEach { fy -> drawLine(osc, Offset(w * 0.16f, h * fy), Offset(w * 0.84f, h * fy), maxOf(0.5f * u, h * 0.003f)) }
        drawPath(cofre, osc, style = Stroke(maxOf(0.5f * u, h * 0.004f)))
        // La tapa.
        caja(roble, tapa)
        bisel(tapa, luz, osc, maxOf(0.6f * u, tapa.height * 0.2f))
        // Las cantoneras del metal y el pasador con su candado.
        val g = maxOf(1.2f * u, h * 0.012f); val l = h * 0.04f
        escuadra(tapa.left, tapa.top, 1f, 1f, l, g, m); escuadra(tapa.right, tapa.top, -1f, 1f, l, g, m)
        escuadra(w * 0.15f, h * 0.915f, 1f, -1f, l, g, m); escuadra(w * 0.85f, h * 0.915f, -1f, -1f, l, g, m)
        val pc = w * 0.5f
        barra(Rect(pc - w * 0.035f, tapa.top + tapa.height * 0.3f, pc + w * 0.035f, h * 0.835f), m)
        val cr = h * 0.022f; val cc = Offset(pc, h * 0.86f)
        drawArc(m.o, 180f, 180f, false, Offset(cc.x - cr * 0.7f, cc.y - cr * 1.6f), Size(cr * 1.4f, cr * 2f), style = Stroke(maxOf(0.6f * u, cr * 0.3f)))
        drawRoundRect(m.b, Offset(cc.x - cr, cc.y - cr * 0.6f), Size(cr * 2f, cr * 1.7f), CornerRadius(cr * 0.3f))
        drawRoundRect(m.o, Offset(cc.x - cr, cc.y - cr * 0.6f), Size(cr * 2f, cr * 1.7f), CornerRadius(cr * 0.3f), style = Stroke(maxOf(0.5f * u, cr * 0.15f)))
        if (fino) drawCircle(brea, cr * 0.25f, Offset(cc.x, cc.y + cr * 0.3f))
        // Un cabo adujado en la cubierta, al pie.
        if (fino) listOf(0.03f, 0.022f, 0.014f).forEach { rr ->
            drawOval(cabo, Offset(w * (0.9f - rr * 1.6f), h * (0.93f - rr * 0.5f)), Size(w * rr * 3.2f, h * rr * 1f), style = Stroke(maxOf(0.8f * u, h * 0.006f)))
        }
        destello(Offset(tapa.right - g, tapa.top + g), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val brazola = fr(0.05f, 0.08f, 0.95f, 0.9f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(brazola); addRect(fr(0.02f, 0.88f, 0.98f, 1f)) }, oscuro); return }
        val m = metal(e, oscuro)
        val roble = t(ROBLE, oscuro); val luz = t(ROBLE_LUZ, oscuro); val osc = t(ROBLE_OSC, oscuro)
        // La cubierta y la brazola de la escotilla.
        caja(t(CUBIERTA, oscuro), fr(0.02f, 0.88f, 0.98f, 1f))
        drawLine(t(BREA, oscuro), Offset(w * 0.02f, h * 0.88f), Offset(w * 0.98f, h * 0.88f), 0.6f * u)
        caja(roble, brazola)
        bisel(brazola, luz, osc, maxOf(0.8f * u, h * 0.06f))
        // El enjaretado: la reja de listones sobre la oscuridad de la bodega.
        val reja = Rect(brazola.left + w * 0.08f, brazola.top + h * 0.18f, brazola.right - w * 0.08f, brazola.bottom - h * 0.18f)
        caja(t(BREA, oscuro), reja)
        val lado = maxOf(reja.height / 3f, 2f * u)
        val nx = (reja.width / maxOf(lado, 1f)).toInt().coerceIn(3, 60); val px = reja.width / nx
        val ny = (reja.height / maxOf(lado, 1f)).toInt().coerceIn(2, 10); val py = reja.height / ny
        for (i in 0 until nx) for (j in 0 until ny) drawRect(osc.copy(alpha = 0.6f), Offset(reja.left + i * px + px * 0.2f, reja.top + j * py + py * 0.2f), Size(px * 0.6f, py * 0.6f))
        for (i in 0..nx) drawLine(luz, Offset(reja.left + i * px, reja.top), Offset(reja.left + i * px, reja.bottom), maxOf(0.6f * u, px * 0.2f))
        for (j in 0..ny) drawLine(roble, Offset(reja.left, reja.top + j * py), Offset(reja.right, reja.top + j * py), maxOf(0.6f * u, py * 0.2f))
        // Las argollas del metal a los lados, con su perno.
        listOf(0.085f, 0.915f).forEach { fx ->
            val c = Offset(w * fx, h * 0.5f); val r = h * 0.17f
            drawCircle(m.b, r, Offset(c.x, c.y + r * 0.6f), style = Stroke(maxOf(0.8f * u, r * 0.32f)))
            remache(c, r * 0.55f, m)
        }
        destello(Offset(w * 0.915f, h * 0.4f), h * 0.2f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFC4B084) else Color(0xFF5E4426)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(1.5f * u, 1.5f * u, w - 1.5f * u, h - 1.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        // El papel (de noche, la tabla de roble oscuro).
        caja(if (oscuro) Color(0xFF221810) else PAPEL, r)
        // La relinga: un cabo alrededor, con la torsión marcada.
        val cabo = t(CABO, oscuro); val caboOsc = t(CABO_OSC, oscuro)
        val rc = r.deflate(0.2f * u)
        drawRect(caboOsc, rc.topLeft, rc.size, style = Stroke(2.4f * u))
        drawRect(cabo, rc.topLeft, rc.size, style = Stroke(1.6f * u))
        drawRect(caboOsc, rc.topLeft, rc.size, style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(0.7f * u, 1.6f * u))))
        // El filete del metal por dentro y un ollao del metal en cada esquina.
        val r1 = r.deflate(2.8f * u)
        drawRect(m.b, r1.topLeft, r1.size, style = Stroke(0.6f * u))
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), r.bottomRight).forEach { c ->
            drawCircle(m.o, 2.6f * u, c); drawCircle(m.b, 2.1f * u, c); drawCircle(caboOsc, 1f * u, c)
        }
        destello(Offset(r.right, r.top), 3f * u, m)
    }
}

/**
 * Revolución: la imprenta. El trofeo descansa en el pedestal de un monumento: la cornisa, el dado de sillares con el festón
 * tricolor colgado y la escarapela con aro del metal del nivel, la placa de bronce (del metal) con sus pernos y el zócalo.
 * En el hall, la platina de la prensa: el mármol de imponer sobre el bastidor de hierro con pernos del metal; la placa es una
 * rama de imprenta del metal, con sus cuñas, que sujeta la hoja impresa.
 */
object LogrosRevolucion : DisenoLogros {
    private val HIERRO = Color(0xFF26282A)
    private val HIERRO_LUZ = Color(0xFF5E6266)
    private val PIEDRA = Color(0xFFC8BFA8)
    private val PIEDRA_JUNTA = Color(0xFF948B76)
    private val PAPEL = Color(0xFFEDE4CC)
    private val TINTA = Color(0xFF1E1C1A)
    private val AZUL = Color(0xFF2B3F8C)
    private val BLANCO = Color(0xFFF2EEE4)
    private val ROJO = Color(0xFFB0282A)
    private val MARMOL = Color(0xFFD8D6D0)
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0C), 0.36f) else c
    /** La piedra, de noche más honda. */
    private fun n(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, Color(0xFF0B0D0C), 0.58f) else c

    // ---------------------------------------------------------------- pedestales y placa

    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val cornisa = fr(0.1f, 0.765f, 0.9f, 0.792f)
        val dado = fr(0.15f, 0.792f, 0.85f, 0.928f)
        val zocalo = fr(0.12f, 0.928f, 0.88f, 0.958f)
        if (!e.hecho) { siluetaDe(Path().apply { addRect(cornisa); addRect(dado); addRect(zocalo) }, oscuro); return }
        val m = metal(e, oscuro)
        val piedra = n(PIEDRA, oscuro); val luz = mezcla(piedra, Color.White, 0.22f); val sombra = mezcla(piedra, Color.Black, 0.28f); val junta = n(PIEDRA_JUNTA, oscuro)
        sombraPie(w * 0.1f, w * 0.9f, h * 0.962f, h * 0.02f, if (oscuro) 0.42f else 0.22f)
        caja(sombra, zocalo)
        drawRect(luz.copy(alpha = 0.5f), zocalo.topLeft, Size(zocalo.width, zocalo.height * 0.2f))
        // El dado de sillares.
        drawRect(Brush.verticalGradient(listOf(luz, piedra, sombra), dado.top, dado.bottom), dado.topLeft, dado.size)
        val lj = maxOf(0.5f * u, h * 0.0035f)
        drawLine(junta, Offset(dado.left, dado.center.y), Offset(dado.right, dado.center.y), lj)
        listOf(0.25f, 0.75f).forEach { fx -> drawLine(junta, Offset(dado.left + dado.width * fx, dado.top), Offset(dado.left + dado.width * fx, dado.center.y), lj) }
        drawLine(junta, Offset(dado.left + dado.width * 0.5f, dado.center.y), Offset(dado.right - dado.width * 0.5f, dado.bottom), lj)
        // La placa de bronce (del metal del nivel) con sus cuatro pernos y los renglones grabados.
        val pl = Rect(w * 0.35f, h * 0.87f, w * 0.65f, h * 0.918f)
        drawRect(m.o, pl.topLeft, pl.size); drawRect(m.b, pl.deflate(maxOf(0.5f * u, pl.height * 0.1f)).topLeft, pl.deflate(maxOf(0.5f * u, pl.height * 0.1f)).size)
        drawRect(m.br, Offset(pl.left + pl.width * 0.08f, pl.top + pl.height * 0.14f), Size(pl.width * 0.84f, pl.height * 0.12f))
        if (fino) {
            listOf(0.45f, 0.68f).forEach { fy -> drawLine(m.o, Offset(pl.left + pl.width * 0.2f, pl.top + pl.height * fy), Offset(pl.right - pl.width * 0.2f, pl.top + pl.height * fy), 0.6f * u) }
            listOf(pl.topLeft, Offset(pl.right, pl.top), Offset(pl.left, pl.bottom), pl.bottomRight).forEach { c -> remache(Offset(c.x + (if (c.x < pl.center.x) 1 else -1) * pl.height * 0.2f, c.y + (if (c.y < pl.center.y) 1 else -1) * pl.height * 0.2f), pl.height * 0.1f, m) }
        }
        // El festón tricolor: tres bandas combadas y sus caídas en los extremos.
        val x0 = w * 0.17f; val x1 = w * 0.83f; val y0 = h * 0.797f; val baja = h * 0.05f; val banda = maxOf(1f * u, h * 0.011f)
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, c ->
            val dy = i * banda * 0.9f
            drawPath(Path().apply { moveTo(x0, y0 + dy); quadraticTo(w * 0.5f, y0 + baja * 2f + dy, x1, y0 + dy) }, t(c, oscuro), style = Stroke(banda))
            listOf(x0, x1).forEach { x -> drawRect(t(c, oscuro), Offset(x - banda * 1.5f + i * banda, y0), Size(banda, h * 0.07f - i * h * 0.006f)) }
        }
        // La escarapela con el aro del metal, al centro del festón.
        val ec = Offset(w * 0.5f, y0 + baja + banda); val er = maxOf(2f * u, h * 0.03f)
        drawCircle(m.o, er * 1.18f, ec); drawCircle(m.b, er * 1.05f, ec)
        drawCircle(t(ROJO, oscuro), er * 0.9f, ec); drawCircle(t(BLANCO, oscuro), er * 0.62f, ec); drawCircle(t(AZUL, oscuro), er * 0.34f, ec)
        if (fino) { remache(ec, er * 0.16f, m); drawArc(m.br, 200f, 60f, false, Offset(ec.x - er * 1.1f, ec.y - er * 1.1f), Size(er * 2.2f, er * 2.2f), style = Stroke(0.6f * u)) }
        // La cornisa.
        caja(luz, cornisa)
        drawRect(sombra, Offset(cornisa.left, cornisa.bottom - cornisa.height * 0.3f), Size(cornisa.width, cornisa.height * 0.3f))
        drawRect(Color.White.copy(alpha = 0.2f), cornisa.topLeft, Size(cornisa.width, cornisa.height * 0.2f))
        destello(Offset(ec.x + er, ec.y - er), h * 0.03f, m)
    }

    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val marmol = fr(0.04f, 0f, 0.96f, 0.22f)
        val bastidor = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(fr(0.08f, 0.22f, 0.92f, 0.56f))
            moveTo(w * 0.08f, h * 0.56f); lineTo(w * 0.2f, h * 0.56f); quadraticTo(w * 0.2f, h * 0.9f, w * 0.26f, h); lineTo(w * 0.04f, h); quadraticTo(w * 0.1f, h * 0.85f, w * 0.08f, h * 0.56f); close()
            moveTo(w * 0.92f, h * 0.56f); lineTo(w * 0.8f, h * 0.56f); quadraticTo(w * 0.8f, h * 0.9f, w * 0.74f, h); lineTo(w * 0.96f, h); quadraticTo(w * 0.9f, h * 0.85f, w * 0.92f, h * 0.56f); close()
        }
        if (!e.hecho) { siluetaDe(Path().apply { addRect(marmol); addPath(bastidor) }, oscuro); return }
        val m = metal(e, oscuro)
        val hierro = t(HIERRO, oscuro); val hierroLuz = t(HIERRO_LUZ, oscuro)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.16f), Offset(w * 0.04f, h * 0.94f), Size(w * 0.92f, h * 0.06f))
        // El bastidor de hierro fundido con sus patas curvas.
        drawPath(bastidor, hierro)
        drawLine(hierroLuz, Offset(w * 0.08f, h * 0.25f), Offset(w * 0.92f, h * 0.25f), maxOf(0.6f * u, h * 0.04f))
        drawLine(hierroLuz.copy(alpha = 0.6f), Offset(w * 0.1f, h * 0.48f), Offset(w * 0.9f, h * 0.48f), 0.5f * u)
        // Los pernos del metal y la chapita con la tricolor en medio.
        val np = 6
        for (k in 0 until np) remache(Offset(w * (0.14f + 0.72f * k / (np - 1)), h * 0.38f), h * 0.07f, m)
        val ch = fr(0.42f, 0.29f, 0.58f, 0.49f)
        drawRect(m.o, ch.topLeft, ch.size); drawRect(m.b, ch.deflate(0.5f * u).topLeft, ch.deflate(0.5f * u).size)
        listOf(AZUL, BLANCO, ROJO).forEachIndexed { i, c -> drawRect(t(c, oscuro), Offset(ch.left + ch.width * (0.18f + i * 0.22f), ch.top + ch.height * 0.25f), Size(ch.width * 0.2f, ch.height * 0.5f)) }
        // El mármol de imponer.
        val mm = n(MARMOL, oscuro)
        caja(mm, marmol)
        bisel(marmol, Color.White.copy(alpha = 0.35f), mezcla(mm, Color.Black, 0.3f), maxOf(0.6f * u, marmol.height * 0.2f))
        drawLine(mezcla(mm, Color.Black, 0.18f), Offset(w * 0.2f, h * 0.06f), Offset(w * 0.34f, h * 0.15f), 0.5f * u)
        destello(Offset(ch.right, ch.top), h * 0.2f, m)
    }

    override val margenPlaca: Dp = 9.dp
    override fun tintaPlaca(oscuro: Boolean): Color = if (oscuro) PAPEL else TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFFBCC0C8) else Color(0xFF2B3F8C)

    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = metal(e, oscuro)
        val r = Rect(0.5f * u, 0.5f * u, w - 0.5f * u, h - 0.5f * u)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.4f else 0.14f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size)
        // La rama del metal, con un filo de luz y otro de sombra.
        barra(Rect(r.left, r.top, r.right, r.top + 2.6f * u), m)
        caja(m.b, Rect(r.left, r.top + 2.6f * u, r.right, r.bottom))
        drawRect(m.o, Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        drawRect(m.o, r.topLeft, r.size, style = Stroke(0.5f * u))
        // La hoja impresa (de noche, la plancha entintada).
        val r1 = r.deflate(2.6f * u)
        caja(if (oscuro) Color(0xFF1C1E21) else PAPEL, r1)
        drawRect(m.o.copy(alpha = 0.8f), r1.topLeft, r1.size, style = Stroke(0.5f * u))
        // Las cuñas de hierro que aprietan la hoja: dos arriba y una a cada lado.
        val cuna = t(HIERRO, oscuro)
        listOf(0.3f, 0.7f).forEach { fx -> drawRect(cuna, Offset(r.left + r.width * fx - 2f * u, r.top + 0.6f * u), Size(4f * u, 1.6f * u)) }
        listOf(r.left + 0.6f * u, r.right - 2.2f * u).forEach { x -> drawRect(cuna, Offset(x, r.center.y - 2f * u), Size(1.6f * u, 4f * u)) }
        destello(Offset(r.right - 2f * u, r.top + 1.3f * u), 3f * u, m)
    }
}
