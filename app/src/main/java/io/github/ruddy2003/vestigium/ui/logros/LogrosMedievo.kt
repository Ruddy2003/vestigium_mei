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
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.ideario.cruzPatada
import io.github.ruddy2003.vestigium.ui.ideario.estrella
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Los logros y el viaje de la Edad Media y la Edad Moderna
// =====================================================================

// ---------------------------------------------------------------- ayudantes de este archivo

/** Los tonos del metal del nivel, un poco apagados de noche; [cristal] es el legendario (nivel 4), que lleva destellos. */
private class MetalMedievo(val o: Color, val b: Color, val br: Color, val cristal: Boolean)

private fun metal(e: EstadoPedestal, oscuro: Boolean) =
    if (oscuro) MetalMedievo(mezcla(e.metalOscuro, Color.Black, 0.22f), mezcla(e.base, Color.Black, 0.14f), mezcla(e.brillo, Color.Black, 0.1f), e.nivel >= 4)
    else MetalMedievo(e.metalOscuro, e.base, e.brillo, e.nivel >= 4)

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
private fun DrawScope.remache(c: Offset, r: Float, m: MetalMedievo) {
    drawCircle(m.o, r, c)
    drawCircle(m.b, r * 0.72f, Offset(c.x - r * 0.12f, c.y - r * 0.12f))
    if (r > 1.1f * density) drawCircle(m.br, r * 0.3f, Offset(c.x - r * 0.3f, c.y - r * 0.3f))
}

/** Una barra del metal del nivel: la base, el filo de luz arriba y la sombra abajo. */
private fun DrawScope.barra(r: Rect, m: MetalMedievo) {
    drawRect(m.b, r.topLeft, r.size)
    drawRect(m.br, r.topLeft, Size(r.width, r.height * 0.3f))
    drawRect(m.o, Offset(r.left, r.bottom - r.height * 0.28f), Size(r.width, r.height * 0.28f))
}

/** El destello del cristal (legendario): una estrellita de cuatro puntas, solo si se ve. */
private fun DrawScope.destello(c: Offset, r: Float, m: MetalMedievo) {
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
private fun DrawScope.escuadra(x: Float, y: Float, sx: Float, sy: Float, largo: Float, grueso: Float, m: MetalMedievo) {
    val p = Path().apply {
        moveTo(x, y); lineTo(x + sx * largo, y); lineTo(x + sx * largo, y + sy * grueso); lineTo(x + sx * grueso, y + sy * grueso)
        lineTo(x + sx * grueso, y + sy * largo); lineTo(x, y + sy * largo); close()
    }
    drawPath(p, m.b)
    drawPath(p, m.o, style = Stroke(maxOf(0.5f * density, grueso * 0.18f)))
    remache(Offset(x + sx * grueso * 0.55f, y + sy * grueso * 0.55f), grueso * 0.32f, m)
}

// ---------------------------------------------------------------- ayudantes del salón del hall

/**
 * La planta del salón, en píxeles: [n] franjas de [paso] de ancho con un trofeo (≈ 97 × 118 dp) centrado en cada una y las
 * filas cada 230 dp desde los 60 dp (como mucho 8). [pilar] cabe en el hueco entre dos trofeos vecinos; [semi] es el
 * semiancho de la hornacina que enmarca cada trofeo, y su arco [arranque] lo bastante abajo para no cortar sus esquinas.
 */
private class PlantaMedievo(val w: Float, val h: Float, val u: Float, columnas: Int) {
    val n = columnas.coerceIn(1, 4)
    val paso = w / n
    /** Lo que queda libre a cada lado de un trofeo dentro de su franja. */
    val holgura = ((paso - 97f * u) / 2f).coerceAtLeast(4f * u)
    val pilar = (holgura * 0.62f).coerceIn(6f * u, 26f * u)
    val friso = minOf(40f * u, h * 0.08f)
    val piso = h - minOf(30f * u, h * 0.06f)
    val zocalo = piso - minOf(16f * u, h * 0.03f)
    val filas: List<Float> = buildList { var y = 60f * u; while (y + 100f * u < h && size < 8) { add(y); y += maxOf(230f * u, 1f) } }
    val semi = minOf(paso / 2f - pilar / 2f - 3f * u, 66f * u).coerceAtLeast(20f * u)
    /** Lo que queda entre dos hornacinas vecinas. */
    val entre = (paso - 2f * semi).coerceAtLeast(2f * u)
    fun cx(i: Int) = (i + 0.5f) * paso
    fun arranque(y: Float) = y - 4f * u + sqrt(maxOf(semi * semi - 2500f * u * u, 0f))
    /** La hornacina de la franja [i] en la fila que empieza en [y]: de la clave del arco a la repisa bajo el pedestal. */
    fun vano(i: Int, y: Float) = Rect(cx(i) - semi, arranque(y) - semi, cx(i) + semi, y + 142f * u)
}

/** Un cerco de luz sin degradado: seis círculos de [color] translúcido, cada vez más chicos, que se suman hacia el centro. */
private fun DrawScope.halo(c: Offset, r: Float, color: Color) {
    for (k in 0 until 6) drawCircle(color, r * (1f - k * 0.16f), c)
}

/** Las juntas de una sillería en [r]: hiladas de [alto] y llagas cada [largo] (a matajunta, con algo de irregularidad). */
private fun hiladas(r: Rect, alto: Float, largo: Float, semilla: Int): Path = Path().apply {
    val a = maxOf(alto, 1f); val l = maxOf(largo, 1f)
    var y = r.top; var k = 0
    while (y < r.bottom && k < 100) {
        val y1 = minOf(y + a, r.bottom)
        moveTo(r.left, y); lineTo(r.right, y)
        var x = r.left + (if (k % 2 == 0) 0f else l / 2f) + azar(semilla, k) * l * 0.3f
        var j = 0
        while (x < r.right && j < 40) { moveTo(x, y); lineTo(x, y1); x += l * (0.75f + 0.5f * azar(semilla, k * 61 + j)); j++ }
        y += a; k++
    }
}

/** Juntas paralelas cada [paso] en [r] (tablas, cañas, estrías): verticales o, si no, horizontales. */
private fun juntas(r: Rect, paso: Float, vertical: Boolean): Path = Path().apply {
    val s = maxOf(paso, 1f); var k = 0
    if (vertical) { var x = r.left + s; while (x < r.right && k < 200) { moveTo(x, r.top); lineTo(x, r.bottom); x += s; k++ } }
    else { var y = r.top + s; while (y < r.bottom && k < 200) { moveTo(r.left, y); lineTo(r.right, y); y += s; k++ } }
}

/** Un piso que huye hacia el fondo entre [arriba] y [abajo]: [radios] juntas por lado que convergen al centro y las hiladas cada vez más anchas. */
private fun pisoFuga(w: Float, arriba: Float, abajo: Float, radios: Int, u: Float): Path = Path().apply {
    val n = radios.coerceIn(2, 24)
    for (k in -n..n) { val x = w / 2f + k * w / n; moveTo(x, abajo); lineTo(w / 2f + (x - w / 2f) * 0.62f, arriba) }
    var y = arriba; var paso = 3f * u; var j = 0
    while (y < abajo && j < 30) { moveTo(0f, y); lineTo(w, y); y += maxOf(paso, 1f); paso *= 1.45f; j++ }
}

/** Una llama de vela o de antorcha con su base en [c], de [alto]. */
private fun llama(c: Offset, alto: Float): Path = Path().apply {
    moveTo(c.x, c.y - alto)
    quadraticTo(c.x + alto * 0.42f, c.y - alto * 0.35f, c.x, c.y)
    quadraticTo(c.x - alto * 0.42f, c.y - alto * 0.35f, c.x, c.y - alto)
    close()
}

/** Un punto [k] al azar (estable) dentro de [r]. */
private fun puntoEn(r: Rect, semilla: Int, k: Int) = Offset(r.left + r.width * azar(semilla, k * 2), r.top + r.height * azar(semilla, k * 2 + 1))

/** Como [coerceIn], pero sin romperse si el lienzo es tan chico que el tope queda bajo el piso: entonces, el punto medio. */
private fun Float.acotado(min: Float, max: Float) = if (max < min) (min + max) / 2f else coerceIn(min, max)

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

    // ---------------------------------------------------------------- el salón del hall

    private val SILLAR = Color(0xFFB9A88A)

    /**
     * La sala de gestas: muros de sillería bajo el envigado de roble con sus canes y el friso pintado a escaques; cada trofeo
     * en una hornacina de medio punto con su rosca de dovelas, la clave y la repisa de piedra; en cada pilar, un tapiz
     * colgado de su vara con la estrella de la casa; antorchas en los muros de los lados, la cortina pintada del zócalo y el
     * enlosado con juncos. De noche la piedra se hunde y mandan las antorchas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val piedra = if (oscuro) Color(0xFF5A4834) else SILLAR
        val junta = if (oscuro) Color(0xFF000000) else Color(0xFF5E4A33)
        val fuego = Color(0xFFFFB24E)
        val oro = t(ORO, oscuro)
        // El muro, más ahumado arriba (bajo las vigas), y la sillería con algunas piedras de otro tono.
        drawRect(Brush.verticalGradient(listOf(v(piedra, if (oscuro) 0.62f else 0.4f), v(piedra, if (oscuro) 0.4f else 0.22f)), 0f, h))
        for (k in 0 until 34) {
            val c = puntoEn(Rect(0f, p.friso, w, p.zocalo), e.semilla, k)
            val y0 = p.friso + ((c.y - p.friso) / (20f * u)).toInt() * 20f * u
            drawRect((if (k % 3 == 0) Color.Black else Color.White).copy(alpha = if (oscuro) 0.05f else 0.06f), Offset(c.x, y0), Size((26f + 20f * azar(e.semilla, k + 90)) * u, 20f * u))
        }
        drawPath(hiladas(Rect(0f, p.friso, w, p.zocalo), 20f * u, 42f * u, e.semilla), v(junta, if (oscuro) 0.55f else 0.26f), style = Stroke(0.8f * u))

        // Los pilares entre las franjas: sillares más grandes con su canto de luz, y la imposta de cada fila.
        for (i in 1 until p.n) {
            val x = i * p.paso; val r = Rect(x - p.pilar / 2f, p.friso, x + p.pilar / 2f, p.zocalo)
            caja(v(piedra, if (oscuro) 0.75f else 0.5f), r)
            drawPath(hiladas(r, 26f * u, p.pilar * 0.7f, e.semilla + i), v(junta, if (oscuro) 0.6f else 0.32f), style = Stroke(0.8f * u))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), r.topLeft, Size(1.4f * u, r.height))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
            for (y in p.filas) caja(v(piedra, if (oscuro) 0.85f else 0.6f), Rect(r.left - 3f * u, p.arranque(y) - 2f * u, r.right + 3f * u, p.arranque(y) + 3f * u))
        }

        // Las hornacinas: el fondo liso y en sombra, la rosca de dovelas, la clave, las jambas y la repisa del pedestal.
        val anillo = minOf(8f * u, p.holgura * 0.4f).coerceAtLeast(3f * u)
        for (y in p.filas) for (i in 0 until p.n) {
            val r = p.vano(i, y); val c = Offset(r.center.x, r.top + p.semi)
            drawPath(arcoMedioPunto(r), v(piedra, if (oscuro) 0.2f else 0.08f))
            drawPath(arcoMedioPunto(Rect(r.left + 2f * u, r.top + 2f * u, r.right - 2f * u, r.bottom)), Color.Black.copy(alpha = if (oscuro) 0.22f else 0.05f), style = Stroke(4f * u))
            val ra = p.semi + anillo / 2f
            drawArc(v(piedra, if (oscuro) 0.8f else 0.55f), 180f, 180f, false, Offset(c.x - ra, c.y - ra), Size(2 * ra, 2 * ra), style = Stroke(anillo))
            val dov = Path()
            for (k in 1 until 11) { val a = PI + k * PI / 11; dov.moveTo(c.x + p.semi * cos(a).toFloat(), c.y + p.semi * sin(a).toFloat()); dov.lineTo(c.x + (p.semi + anillo) * cos(a).toFloat(), c.y + (p.semi + anillo) * sin(a).toFloat()) }
            drawPath(dov, v(junta, if (oscuro) 0.6f else 0.35f), style = Stroke(0.7f * u))
            drawArc(v(junta, if (oscuro) 0.6f else 0.3f), 180f, 180f, false, Offset(c.x - p.semi - anillo, c.y - p.semi - anillo), Size(2 * (p.semi + anillo), 2 * (p.semi + anillo)), style = Stroke(0.7f * u))
            val cl = Path().apply { moveTo(c.x - anillo * 0.5f, r.top - anillo * 1.15f); lineTo(c.x + anillo * 0.5f, r.top - anillo * 1.15f); lineTo(c.x + anillo * 0.38f, r.top + 1f * u); lineTo(c.x - anillo * 0.38f, r.top + 1f * u); close() }
            drawPath(cl, v(piedra, if (oscuro) 0.95f else 0.7f)); drawPath(cl, v(junta, 0.35f), style = Stroke(0.6f * u))
            listOf(r.left, r.right).forEach { x -> drawLine(v(junta, if (oscuro) 0.5f else 0.28f), Offset(x, c.y), Offset(x, r.bottom), 0.8f * u) }
            val rep = Rect(r.left - 4f * u, r.bottom - 1f * u, r.right + 4f * u, r.bottom + 4f * u)
            caja(v(piedra, if (oscuro) 0.85f else 0.6f), rep)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(rep.left, rep.bottom), Size(rep.width, 2f * u))
        }

        // Los tapices, uno en cada pilar y en cada fila, colgados de su vara: campo de color, orla de oro, estrellas y la cola en punta.
        val campos = listOf(t(BERMELLON, oscuro), t(LAPIZ, oscuro))
        for ((f, y) in p.filas.withIndex()) for (i in 1 until p.n) {
            val x = i * p.paso; val an = minOf(p.pilar / 2f + 3f * u, p.entre / 2f - 2f * u).coerceAtLeast(4f * u)
            val y0 = y - 30f * u; val y1 = y + 108f * u
            val tela = Path().apply { moveTo(x - an, y0); lineTo(x + an, y0); lineTo(x + an, y1); lineTo(x, y1 - an * 0.9f); lineTo(x - an, y1); close() }
            drawPath(tela, v(campos[(i + f) % 2], if (oscuro) 0.5f else 0.42f))
            drawPath(juntas(Rect(x - an, y0, x + an, y1), 2.6f * u, true), Color.Black.copy(alpha = if (oscuro) 0.08f else 0.05f), style = Stroke(0.6f * u))
            drawPath(tela, v(oro, if (oscuro) 0.55f else 0.6f), style = Stroke(1.2f * u))
            drawLine(v(oro, 0.45f), Offset(x - an + 2.4f * u, y0 + 6f * u), Offset(x + an - 2.4f * u, y0 + 6f * u), 0.7f * u)
            listOf(0.26f, 0.56f).forEach { fy -> drawPath(estrella(Offset(x, y0 + (y1 - y0) * fy), an * 0.62f, 6, 0.48f), v(oro, if (oscuro) 0.6f else 0.65f)) }
            for (k in 0 until 5) { val fx = -an + an * 2f * (k + 0.5f) / 5f; drawPath(rombo(Offset(x + fx, y0 + (y1 - y0) * 0.8f - kotlin.math.abs(fx) * 0.9f), an * 0.14f, an * 0.2f), v(oro, 0.5f)) }
            // La vara con sus remates y el cordón al clavo.
            drawLine(v(t(ROBLE_OSC, oscuro), 0.8f), Offset(x - an - 4f * u, y0), Offset(x + an + 4f * u, y0), 2f * u)
            listOf(-1f, 1f).forEach { s -> drawCircle(v(oro, 0.7f), 1.8f * u, Offset(x + s * (an + 4f * u), y0)) }
            drawPath(Path().apply { moveTo(x - an * 0.8f, y0); lineTo(x, y0 - 8f * u); lineTo(x + an * 0.8f, y0) }, v(t(ROBLE_OSC, oscuro), 0.6f), style = Stroke(0.7f * u))
            drawCircle(v(t(HIERRO, oscuro), 0.8f), 1.4f * u, Offset(x, y0 - 8f * u))
        }

        // El envigado del techo: la viga maestra con sus vetas, los canes tallados y el friso pintado a escaques.
        val roble = t(ROBLE, oscuro); val robleOsc = t(ROBLE_OSC, oscuro)
        val viga = Rect(0f, 0f, w, p.friso * 0.46f)
        caja(v(roble, if (oscuro) 0.7f else 0.5f), viga)
        vetas(viga, v(t(VETA, oscuro), 0.5f), e.semilla, densidad = 0.6f, grosor = 0.6f)
        drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, viga.bottom - 1.6f * u), Size(w, 1.6f * u))
        val ycan = viga.bottom; val acan = p.friso * 0.32f
        var xc = 12f * u; var kc = 0
        while (xc < w && kc < 40) {
            val can = Path().apply { moveTo(xc - 4f * u, ycan); lineTo(xc + 4f * u, ycan); lineTo(xc + 4f * u, ycan + acan * 0.45f); quadraticTo(xc + 4f * u, ycan + acan, xc - 4f * u, ycan + acan); close() }
            drawPath(can, v(robleOsc, if (oscuro) 0.75f else 0.55f))
            drawLine(v(roble, 0.6f), Offset(xc - 3f * u, ycan + acan * 0.3f), Offset(xc + 3f * u, ycan + acan * 0.3f), 0.6f * u)
            xc += maxOf(30f * u, 1f); kc++
        }
        val banda = Rect(0f, p.friso * 0.84f, w, p.friso)
        val esc = maxOf(6f * u, 1f); val pb = Path(); val pl = Path(); var xe = 0f; var ke = 0
        while (xe < w && ke < 120) { (if (ke % 2 == 0) pb else pl).addRect(Rect(xe, banda.top, xe + esc, banda.bottom)); xe += esc; ke++ }
        drawPath(pb, v(t(BERMELLON, oscuro), 0.32f)); drawPath(pl, v(t(LAPIZ, oscuro), 0.3f))
        drawLine(v(oro, 0.5f), Offset(0f, banda.top), Offset(w, banda.top), 0.8f * u); drawLine(v(oro, 0.5f), Offset(0f, banda.bottom), Offset(w, banda.bottom), 0.8f * u)

        // Las antorchas de los muros de los lados, una por fila: la argolla de hierro, el mango, la llama y su luz.
        val xa = (p.holgura * 0.42f).coerceIn(5f * u, 18f * u)
        for (y in p.filas) listOf(xa, w - xa).forEach { x ->
            val b = Offset(x, y + 46f * u); val s = if (x < w / 2) -1f else 1f
            halo(Offset(b.x, b.y - 12f * u), minOf(48f * u, p.holgura * 1.6f + 14f * u), fuego.copy(alpha = if (oscuro) 0.05f else 0.025f))
            // El brazo de hierro clavado al muro, el mango y la cazoleta.
            drawRect(v(t(HIERRO, oscuro), 0.8f), Offset(x + s * 4f * u - 1f * u, b.y + 4f * u), Size(2f * u, 12f * u))
            drawLine(v(t(HIERRO, oscuro), 0.85f), Offset(x + s * 4f * u, b.y + 9f * u), Offset(x, b.y + 7f * u), 1.3f * u)
            drawLine(v(t(ROBLE_OSC, oscuro), 0.9f), Offset(x, b.y + 15f * u), Offset(x, b.y - 2f * u), 2.4f * u)
            drawPath(Path().apply { moveTo(x - 3.4f * u, b.y - 3f * u); lineTo(x + 3.4f * u, b.y - 3f * u); lineTo(x + 2f * u, b.y + 2f * u); lineTo(x - 2f * u, b.y + 2f * u); close() }, v(t(HIERRO, oscuro), 0.9f))
            drawPath(llama(Offset(x, b.y - 4f * u), 11f * u), fuego.copy(alpha = if (oscuro) 0.95f else 0.7f))
            drawPath(llama(Offset(x, b.y - 4f * u), 5.5f * u), Color(0xFFFFF0C2).copy(alpha = if (oscuro) 0.9f else 0.7f))
        }

        // El zócalo con la cortina pintada (festones y pliegues) y el enlosado que huye, con juncos tirados.
        val z = Rect(0f, p.zocalo, w, p.piso)
        caja(v(t(PERGAMINO, oscuro), if (oscuro) 0.16f else 0.3f), z)
        val cortina = Path(); val pliegues = Path(); var xf = 0f; var kf = 0; val pf = maxOf(16f * u, 1f)
        cortina.moveTo(0f, z.top + 3f * u)
        while (xf < w && kf < 80) { cortina.quadraticTo(xf + pf / 2f, z.top + z.height * 0.7f, xf + pf, z.top + 3f * u); pliegues.moveTo(xf + pf / 2f, z.top + z.height * 0.55f); pliegues.lineTo(xf + pf / 2f, z.bottom); xf += pf; kf++ }
        drawPath(cortina, v(t(BERMELLON, oscuro), 0.45f), style = Stroke(1.2f * u))
        drawPath(pliegues, v(t(BERMELLON, oscuro), 0.3f), style = Stroke(0.7f * u))
        drawLine(v(oro, 0.45f), Offset(0f, z.top), Offset(w, z.top), 1f * u)
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(piedra, if (oscuro) 0.55f else 0.38f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 7, u), v(junta, if (oscuro) 0.6f else 0.3f), style = Stroke(0.8f * u))
        val juncos = Path()
        for (k in 0 until 36) { val c = puntoEn(suelo, e.semilla + 5, k); val a = azar(e.semilla, k + 300) * 3f; juncos.moveTo(c.x, c.y); juncos.lineTo(c.x + 7f * u * cos(a), c.y + 2f * u * sin(a)) }
        drawPath(juncos, v(Color(0xFFB7A052), if (oscuro) 0.45f else 0.55f), style = Stroke(0.7f * u))
        // La luz de las antorchas, que se junta en el piso.
        drawRect(Brush.radialGradient(listOf(fuego.copy(alpha = if (oscuro) 0.12f else 0.05f), Color.Transparent), Offset(w / 2f, h), maxOf(w * 0.8f, 1f)), Offset(0f, h - w * 0.8f), Size(w, w * 0.8f))
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

    // ---------------------------------------------------------------- el salón del hall

    private val CAL = Color(0xFFF2EDE2)
    private val ALMAGRA = Color(0xFF8E3A26)
    private val ARENA = Color(0xFFD9B97A)
    private val HIERRO_LANZA = Color(0xFF5E6064)

    /**
     * La sala de armas de la encomienda: muros encalados con la sillería asomando donde se cae la cal, la bóveda de cañón
     * con sus arcos fajones sobre cada pilar, una hornacina apenas apuntada para cada trofeo, en cada pilar el escudo de la
     * cruz patada sobre dos lanzas cruzadas y un candil colgado; en los muros de los lados las saeteras con su haz de sol
     * (de noche, de luna), el zócalo de almagra y las losas con la arena que entra del desierto.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val cal = if (oscuro) Color(0xFF4A4740) else CAL
        val sillar = t(SILLAR, oscuro)
        val junta = if (oscuro) Color.Black else SILLAR_JUNTA
        val luz = if (oscuro) Color(0xFFB8CCF0) else Color(0xFFFFE6A8)
        val llama = Color(0xFFFFB955)
        // El muro encalado: más limpio arriba, más sucio y con arena abajo.
        drawRect(Brush.verticalGradient(listOf(v(cal, if (oscuro) 0.5f else 0.55f), v(t(ARENA, oscuro), if (oscuro) 0.32f else 0.22f)), 0f, h))
        // Donde la cal se cae asoma la sillería.
        for (k in 0 until 16) {
            val c = puntoEn(Rect(0f, p.friso + 20f * u, w, p.zocalo - 20f * u), e.semilla + 3, k)
            val r = Rect(c.x - 18f * u, c.y - 11f * u, c.x + (18f + 22f * azar(e.semilla, k + 40)) * u, c.y + (8f + 10f * azar(e.semilla, k + 41)) * u)
            val parche = contornoIrregular(r, e.semilla + k, 14, 0.22f)
            drawPath(parche, v(sillar, if (oscuro) 0.45f else 0.34f))
            clipPath(parche) { drawPath(hiladas(r, 9f * u, 18f * u, e.semilla + k), v(junta, if (oscuro) 0.6f else 0.4f), style = Stroke(0.6f * u)) }
        }

        // La bóveda de cañón: el intradós en sombra sobre cada franja y los fajones que bajan a los pilares.
        val boveda = Path().apply {
            moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, p.friso)
            for (i in p.n - 1 downTo 0) quadraticTo(p.cx(i), p.friso * 0.15f, i * p.paso, p.friso)
            close()
        }
        drawPath(boveda, v(sillar, if (oscuro) 0.4f else 0.42f))
        drawPath(juntas(Rect(0f, 0f, w, p.friso), 7f * u, false), v(junta, if (oscuro) 0.5f else 0.25f), style = Stroke(0.6f * u))
        for (i in 0..p.n) {
            val x = i * p.paso
            drawRect(v(sillar, if (oscuro) 0.62f else 0.6f), Offset(x - p.pilar / 2f - 2f * u, 0f), Size(p.pilar + 4f * u, p.friso))
            drawPath(juntas(Rect(x - p.pilar / 2f - 2f * u, 0f, x + p.pilar / 2f + 2f * u, p.friso), 9f * u, false), v(junta, 0.45f), style = Stroke(0.6f * u))
        }

        // Los pilares de sillar visto, con su imposta en cada fila.
        for (i in 1 until p.n) {
            val x = i * p.paso; val r = Rect(x - p.pilar / 2f, p.friso, x + p.pilar / 2f, p.zocalo)
            caja(v(sillar, if (oscuro) 0.62f else 0.5f), r)
            drawPath(hiladas(r, 18f * u, p.pilar * 0.8f, e.semilla + 11 * i), v(junta, if (oscuro) 0.6f else 0.42f), style = Stroke(0.7f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            for (y in p.filas) caja(v(sillar, if (oscuro) 0.8f else 0.68f), Rect(r.left - 3f * u, p.arranque(y) - 3f * u, r.right + 3f * u, p.arranque(y) + 2f * u))
        }

        // Las hornacinas, apenas apuntadas, con su rosca de piedra y el alféizar.
        val anillo = minOf(7f * u, p.holgura * 0.36f).coerceAtLeast(3f * u)
        for (y in p.filas) for (i in 0 until p.n) {
            val r = p.vano(i, y)
            val arco = arcoApuntado(r, p.semi * 1.05f)
            drawPath(arco, v(cal, if (oscuro) 0.3f else 0.7f))
            drawPath(arcoApuntado(Rect(r.left + 2f * u, r.top + 3f * u, r.right - 2f * u, r.bottom), p.semi), Color.Black.copy(alpha = if (oscuro) 0.2f else 0.045f), style = Stroke(5f * u))
            drawPath(arcoApuntado(Rect(r.left - anillo / 2f, r.top - anillo * 0.6f, r.right + anillo / 2f, r.bottom), p.semi * 1.05f + anillo * 0.6f), v(sillar, if (oscuro) 0.7f else 0.55f), style = Stroke(anillo))
            drawPath(arcoApuntado(Rect(r.left - anillo, r.top - anillo * 1.2f, r.right + anillo, r.bottom), p.semi * 1.05f + anillo * 1.2f), v(junta, if (oscuro) 0.55f else 0.3f), style = Stroke(0.6f * u))
            caja(v(sillar, if (oscuro) 0.75f else 0.62f), Rect(r.left - 4f * u, r.bottom - 1f * u, r.right + 4f * u, r.bottom + 4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(r.left - 4f * u, r.bottom + 4f * u), Size(r.width + 8f * u, 2f * u))
        }

        // En cada pilar y cada fila: el escudo de la cruz patada sobre dos lanzas cruzadas y, debajo, el candil colgado.
        val asta = t(Color(0xFF5A3E26), oscuro)
        for (y in p.filas) for (i in 1 until p.n) {
            val x = i * p.paso; val er = minOf(12f * u, p.entre / 2f - 1f * u).coerceAtLeast(4f * u)
            val c = Offset(x, y + 18f * u)
            listOf(-1f, 1f).forEach { s ->
                val a = Offset(c.x - s * er * 1.3f, c.y + er * 2.8f); val b = Offset(c.x + s * er * 1.3f, c.y - er * 2.6f)
                drawLine(v(asta, 0.8f), a, b, 1.3f * u)
                drawPath(Path().apply { val d = Offset((b.x - a.x) / 9f, (b.y - a.y) / 9f); moveTo(b.x + d.x * 0.8f, b.y + d.y * 0.8f); lineTo(b.x - d.y * 0.25f, b.y + d.x * 0.25f); lineTo(b.x + d.y * 0.25f, b.y - d.x * 0.25f); close() }, v(t(HIERRO_LANZA, oscuro), 0.85f))
                drawLine(v(t(ROJO, oscuro), 0.7f), Offset(b.x - (b.x - a.x) * 0.14f, b.y - (b.y - a.y) * 0.14f), Offset(b.x - (b.x - a.x) * 0.14f + s * 4f * u, b.y - (b.y - a.y) * 0.14f + 3f * u), 1.6f * u)
            }
            val escudo = Path().apply {
                moveTo(c.x - er * 0.82f, c.y - er * 0.85f); lineTo(c.x + er * 0.82f, c.y - er * 0.85f)
                quadraticTo(c.x + er * 0.88f, c.y + er * 0.35f, c.x, c.y + er); quadraticTo(c.x - er * 0.88f, c.y + er * 0.35f, c.x - er * 0.82f, c.y - er * 0.85f); close()
            }
            drawPath(escudo, v(t(BLANCO, oscuro), if (oscuro) 0.6f else 0.9f))
            drawPath(cruzPatada(Offset(c.x, c.y - er * 0.08f), er * 0.55f), v(t(ROJO, oscuro), if (oscuro) 0.75f else 0.7f))
            drawPath(escudo, v(t(NEGRO, oscuro), 0.5f), style = Stroke(1f * u))
            // El candil: la cadena, el plato de bronce, la llama y su luz.
            val l = Offset(x, y + 92f * u)
            drawLine(v(t(NEGRO, oscuro), 0.5f), Offset(x, c.y + er + 2f * u), Offset(x, l.y - 3f * u), 0.7f * u)
            halo(Offset(l.x, l.y - 4f * u), minOf(30f * u, p.entre * 0.7f), llama.copy(alpha = if (oscuro) 0.05f else 0.02f))
            drawPath(Path().apply { moveTo(x - 6f * u, l.y - 2f * u); lineTo(x + 7f * u, l.y - 2f * u); quadraticTo(x + 4f * u, l.y + 3f * u, x, l.y + 3f * u); quadraticTo(x - 4f * u, l.y + 3f * u, x - 6f * u, l.y - 2f * u); close() }, v(Color(0xFFA67C3A), if (oscuro) 0.8f else 0.75f))
            drawPath(llama(Offset(x + 5f * u, l.y - 2f * u), 6f * u), llama.copy(alpha = if (oscuro) 0.95f else 0.75f))
        }

        // Las saeteras de los muros de los lados, una por fila: el derrame, la rendija y el haz de luz que cae hacia el piso.
        val xs = (p.holgura * 0.42f).coerceIn(5f * u, 18f * u)
        for (y in p.filas) listOf(xs, w - xs).forEach { x ->
            val s = if (x < w / 2) 1f else -1f
            val top = y + 4f * u; val bot = y + 70f * u
            val haz = Path().apply { moveTo(x - 1.5f * u, top + 6f * u); lineTo(x + 1.5f * u, top + 6f * u); lineTo(x + s * 60f * u + 9f * u, bot + 80f * u); lineTo(x + s * 60f * u - 9f * u, bot + 80f * u); close() }
            drawPath(haz, luz.copy(alpha = if (oscuro) 0.05f else 0.12f))
            drawRoundRect(v(sillar, if (oscuro) 0.6f else 0.55f), Offset(x - 5f * u, top - 3f * u), Size(10f * u, bot - top + 6f * u), CornerRadius(5f * u))
            drawRoundRect(v(junta, if (oscuro) 0.5f else 0.4f), Offset(x - 5f * u, top - 3f * u), Size(10f * u, bot - top + 6f * u), CornerRadius(5f * u), style = Stroke(0.6f * u))
            drawRect(if (oscuro) Color(0xFF9DB4E0).copy(alpha = 0.7f) else Color(0xFFFFF4D6), Offset(x - 1.2f * u, top + 2f * u), Size(2.4f * u, bot - top - 4f * u))
            drawRect(if (oscuro) Color(0xFF9DB4E0).copy(alpha = 0.7f) else Color(0xFFFFF4D6), Offset(x - 3.5f * u, (top + bot) / 2f - 1f * u), Size(7f * u, 2f * u))
        }

        // El zócalo de almagra y las losas grandes, con la arena del desierto amontonada en los rincones.
        val z = Rect(0f, p.zocalo, w, p.piso)
        caja(v(t(ALMAGRA, oscuro), if (oscuro) 0.45f else 0.32f), z)
        drawLine(v(t(ALMAGRA, oscuro), 0.6f), Offset(0f, z.top), Offset(w, z.top), 1.2f * u)
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(sillar, if (oscuro) 0.5f else 0.45f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 5, u), v(junta, if (oscuro) 0.6f else 0.45f), style = Stroke(0.9f * u))
        listOf(0f, w).forEach { x -> drawOval(v(t(ARENA, oscuro), if (oscuro) 0.4f else 0.55f), Offset(x - 50f * u, h - 12f * u), Size(100f * u, 24f * u)) }
        val granos = Path()
        for (k in 0 until 30) { val c = puntoEn(suelo, e.semilla + 9, k); granos.addOval(Rect(c, 0.5f * u)) }
        drawPath(granos, v(t(ARENA, oscuro), 0.7f))
        // La luz que entra y se junta en las losas: sol de día, luna de noche.
        drawRect(Brush.radialGradient(listOf(luz.copy(alpha = if (oscuro) 0.08f else 0.14f), Color.Transparent), Offset(w / 2f, h), maxOf(w * 0.7f, 1f)), Offset(0f, h - w * 0.7f), Size(w, w * 0.7f))
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

    // ---------------------------------------------------------------- el salón del hall

    private val ADOBE = Color(0xFFD9BF92)
    private val ADOBE_OSC = Color(0xFFA9875A)
    private val ORO_SEDA = Color(0xFFD6A93E)
    private val CIELO = Color(0xFF8DB4D8)
    private val DUNA = Color(0xFFE2BE7E)

    /**
     * El caravasar: muros de tapial con sus tongadas y la paja asomando, la galería de pilares de laca roja bajo el alero con
     * su celosía de meandro, una ventana lunar que enmarca cada trofeo y deja ver las dunas y el cielo del patio (de noche,
     * estrellas y la luna), farolillos de seda colgados entre las ventanas, fardos de seda en los rincones y la alfombra del
     * camino sobre el ladrillo. De noche el tapial se enfría y los farolillos tiñen de rojo los pilares.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val adobe = if (oscuro) Color(0xFF4A3C30) else ADOBE
        val junta = if (oscuro) Color.Black else ADOBE_OSC
        val laca = t(LACA, oscuro)
        val luzFarol = Color(0xFFFF9A4A)
        // El tapial: tongadas horizontales un poco onduladas y la paja.
        drawRect(Brush.verticalGradient(listOf(v(adobe, if (oscuro) 0.6f else 0.42f), v(adobe, if (oscuro) 0.45f else 0.26f)), 0f, h))
        val tongadas = Path(); var yt = p.friso; var kt = 0
        while (yt < p.zocalo && kt < 80) {
            tongadas.moveTo(0f, yt)
            var x = 0f; var j = 0
            while (x < w && j < 30) { val x2 = x + maxOf(30f * u, 1f); tongadas.quadraticTo(x + 15f * u, yt + (azar(e.semilla, kt * 31 + j) - 0.5f) * 3f * u, x2, yt); x = x2; j++ }
            yt += maxOf(24f * u, 1f); kt++
        }
        drawPath(tongadas, v(junta, if (oscuro) 0.3f else 0.3f), style = Stroke(0.8f * u))
        val paja = Path()
        for (k in 0 until 60) { val c = puntoEn(Rect(0f, p.friso, w, p.zocalo), e.semilla + 2, k); val a = azar(e.semilla, k + 500) * 3.1f; paja.moveTo(c.x, c.y); paja.lineTo(c.x + 4f * u * cos(a), c.y + 4f * u * sin(a)) }
        drawPath(paja, v(Color(0xFFC9A55A), if (oscuro) 0.4f else 0.6f), style = Stroke(0.6f * u))

        // Las ventanas lunares: el patio que se ve por cada una (cielo, dunas y, de noche, estrellas) y su aro de ladrillo.
        val a = p.semi + 2f * u
        val b = maxOf(a, 76f * u)
        val ladrillo = t(Color(0xFF9C8F82), oscuro)
        for ((f, y) in p.filas.withIndex()) for (i in 0 until p.n) {
            val c = Offset(p.cx(i), y + 58f * u)
            val ov = Rect(c.x - a, c.y - b, c.x + a, c.y + b)
            val hueco = Path().apply { addOval(ov) }
            clipPath(hueco) {
                caja(if (oscuro) v(Color(0xFF1C2A55), 0.55f) else v(CIELO, 0.22f), ov)
                if (oscuro) for (k in 0 until 10) drawCircle(Color.White.copy(alpha = 0.25f + 0.4f * azar(e.semilla, f * 50 + i * 10 + k)), 0.6f * u, puntoEn(Rect(ov.left, ov.top, ov.right, c.y + b * 0.3f), e.semilla + f * 7 + i, k))
                if (oscuro && f == 0 && i == 0) { drawCircle(Color(0xFFF2E8C8).copy(alpha = 0.5f), 7f * u, Offset(ov.left + a * 0.45f, ov.top + b * 0.35f)); drawCircle(v(Color(0xFF1C2A55), 0.55f), 6f * u, Offset(ov.left + a * 0.45f + 3f * u, ov.top + b * 0.33f)) }
                val dunas = Path().apply {
                    moveTo(ov.left, c.y + b * 0.42f); quadraticTo(c.x - a * 0.4f, c.y + b * 0.2f, c.x + a * 0.1f, c.y + b * 0.4f)
                    quadraticTo(c.x + a * 0.6f, c.y + b * 0.26f, ov.right, c.y + b * 0.38f); lineTo(ov.right, ov.bottom); lineTo(ov.left, ov.bottom); close()
                }
                drawPath(dunas, v(t(DUNA, oscuro), if (oscuro) 0.4f else 0.32f))
                drawRect(v(adobe, if (oscuro) 0.5f else 0.36f), Offset(ov.left, c.y + b * 0.62f), Size(ov.width, b))
            }
            drawOval(Color.Black.copy(alpha = if (oscuro) 0.2f else 0.05f), ov.topLeft, ov.size, style = Stroke(5f * u))
            val aro = Rect(ov.left - 3f * u, ov.top - 3f * u, ov.right + 3f * u, ov.bottom + 3f * u)
            drawOval(v(ladrillo, if (oscuro) 0.75f else 0.5f), aro.topLeft, aro.size, style = Stroke(6f * u))
            val ladr = Path()
            for (k in 0 until 28) { val t0 = k * 2 * PI / 28; val cs = cos(t0).toFloat(); val sn = sin(t0).toFloat(); ladr.moveTo(c.x + (a) * cs, c.y + (b) * sn); ladr.lineTo(c.x + (a + 6f * u) * cs, c.y + (b + 6f * u) * sn) }
            drawPath(ladr, v(junta, if (oscuro) 0.6f else 0.45f), style = Stroke(0.6f * u))
            // El umbral de piedra bajo el pedestal.
            caja(v(ladrillo, if (oscuro) 0.7f else 0.45f), Rect(c.x - a * 0.86f, y + 140f * u, c.x + a * 0.86f, y + 145f * u))
        }

        // Los pilares de laca roja de la galería (entre las ventanas y en los bordes), con su basa de piedra.
        val ap = minOf(p.pilar * 0.55f, p.entre * 0.4f).coerceAtLeast(4f * u)
        for (i in 0..p.n) {
            val x = i * p.paso
            caja(v(laca, if (oscuro) 0.55f else 0.42f), Rect(x - ap / 2f, p.friso, x + ap / 2f, p.zocalo))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.12f), Offset(x - ap / 2f + 1f * u, p.friso), Size(1.2f * u, p.zocalo - p.friso))
            caja(v(ladrillo, if (oscuro) 0.75f else 0.55f), Rect(x - ap / 2f - 2f * u, p.zocalo - 7f * u, x + ap / 2f + 2f * u, p.zocalo))
        }

        // El alero: la viga, la celosía de meandro en rojo y las ménsulas sobre cada pilar.
        val viga = Rect(0f, 0f, w, p.friso * 0.38f)
        caja(v(t(NEGRO_LACA, oscuro), if (oscuro) 0.75f else 0.55f), viga)
        val celosia = Rect(0f, viga.bottom, w, p.friso * 0.88f)
        caja(v(t(PAPEL, oscuro), if (oscuro) 0.25f else 0.4f), celosia)
        val meandro = Path(); val m = celosia.height * 0.7f; var xm = 0f; var km = 0; val ym = celosia.top + celosia.height * 0.15f
        while (xm < w && km < 60) {
            meandro.moveTo(xm, ym + m); meandro.lineTo(xm, ym); meandro.lineTo(xm + m, ym); meandro.lineTo(xm + m, ym + m * 0.66f); meandro.lineTo(xm + m * 0.33f, ym + m * 0.66f); meandro.lineTo(xm + m * 0.33f, ym + m * 0.33f)
            meandro.moveTo(xm, ym + m); meandro.lineTo(xm + m * 1.25f, ym + m)
            xm += maxOf(m * 1.25f, 1f); km++
        }
        drawPath(meandro, v(laca, if (oscuro) 0.6f else 0.55f), style = Stroke(1.1f * u))
        caja(v(laca, if (oscuro) 0.6f else 0.5f), Rect(0f, celosia.bottom, w, p.friso))
        for (i in 0..p.n) {
            val x = i * p.paso
            drawPath(Path().apply { moveTo(x - ap * 1.6f, p.friso); lineTo(x + ap * 1.6f, p.friso); quadraticTo(x + ap * 0.6f, p.friso + 4f * u, x + ap / 2f, p.friso + 9f * u); lineTo(x - ap / 2f, p.friso + 9f * u); quadraticTo(x - ap * 0.6f, p.friso + 4f * u, x - ap * 1.6f, p.friso); close() }, v(t(NEGRO_LACA, oscuro), if (oscuro) 0.7f else 0.5f))
        }

        // Los farolillos de seda, uno entre cada par de ventanas y en cada fila: cordón, tapas de oro, borla y su luz.
        for (y in p.filas) for (i in 1 until p.n) {
            val x = i * p.paso; val c = Offset(x, y + 2f * u)
            val fr = minOf(11f * u, p.entre * 0.4f).coerceAtLeast(4f * u)
            halo(c, fr * 4.2f, luzFarol.copy(alpha = if (oscuro) 0.05f else 0.025f))
            drawLine(v(t(NEGRO_LACA, oscuro), 0.6f), Offset(x, c.y - fr * 3.2f), Offset(x, c.y - fr), 0.7f * u)
            drawOval(v(t(CORDON, oscuro), if (oscuro) 0.95f else 0.75f), Offset(x - fr, c.y - fr * 1.1f), Size(2 * fr, 2.2f * fr))
            drawOval(Color(0xFFFFD08A).copy(alpha = if (oscuro) 0.45f else 0.3f), Offset(x - fr * 0.55f, c.y - fr * 0.8f), Size(1.1f * fr, 1.6f * fr))
            listOf(-0.5f, 0f, 0.5f).forEach { k -> drawLine(v(t(CORDON, oscuro), 0.9f).copy(alpha = 0.6f), Offset(x + k * fr * 1.4f, c.y - fr), Offset(x + k * fr * 1.4f, c.y + fr), 0.5f * u) }
            listOf(c.y - fr * 1.2f, c.y + fr * 1f).forEach { yy -> caja(v(t(ORO_SEDA, oscuro), 0.85f), Rect(x - fr * 0.6f, yy, x + fr * 0.6f, yy + fr * 0.22f)) }
            drawLine(v(t(CORDON, oscuro), 0.8f), Offset(x, c.y + fr * 1.2f), Offset(x, c.y + fr * 2.6f), 1.4f * u)
        }

        // El zócalo de ladrillo, el piso de ladrillo que huye, la alfombra del camino y los fardos de seda en los rincones.
        val z = Rect(0f, p.zocalo, w, p.piso)
        caja(v(ladrillo, if (oscuro) 0.55f else 0.4f), z)
        drawPath(hiladas(z, z.height / 2f, 14f * u, e.semilla), v(junta, 0.5f), style = Stroke(0.6f * u))
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(t(Color(0xFFB08A62), oscuro), if (oscuro) 0.5f else 0.38f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 9, u), v(junta, if (oscuro) 0.6f else 0.4f), style = Stroke(0.7f * u))
        val alf = Path().apply { moveTo(w * 0.3f, p.piso + 2f * u); lineTo(w * 0.7f, p.piso + 2f * u); lineTo(w * 0.82f, h); lineTo(w * 0.18f, h); close() }
        drawPath(alf, v(laca, if (oscuro) 0.5f else 0.36f))
        drawPath(Path().apply { moveTo(w * 0.33f, p.piso + 5f * u); lineTo(w * 0.67f, p.piso + 5f * u); lineTo(w * 0.77f, h); lineTo(w * 0.23f, h); close() }, v(t(ORO_SEDA, oscuro), 0.5f), style = Stroke(1f * u))
        drawPath(Path().apply { moveTo(w * 0.36f, p.piso + 7f * u); lineTo(w * 0.64f, p.piso + 7f * u); lineTo(w * 0.72f, h); lineTo(w * 0.28f, h); close() }, v(t(JADE, oscuro), 0.4f))
        val sedas = listOf(t(JADE, oscuro), t(ORO_SEDA, oscuro), laca, t(Color(0xFF3F5A8C), oscuro))
        listOf(0f, w).forEachIndexed { lado, x0 ->
            val s = if (lado == 0) 1f else -1f
            for (k in 0 until 3) {
                val fw = 22f * u; val fh = 11f * u
                val r = Rect(x0 + s * (2f * u + k * 4f * u) - (if (s < 0) fw else 0f), h - fh * (k + 1) - 1f * u, x0 + s * (2f * u + k * 4f * u) + (if (s > 0) fw else 0f), h - fh * k - 1f * u)
                drawRoundRect(v(sedas[(k + lado) % 4], if (oscuro) 0.6f else 0.5f), r.topLeft, r.size, CornerRadius(4f * u))
                drawLine(v(t(NEGRO_LACA, oscuro), 0.5f), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.3f, r.bottom), 0.8f * u)
                drawLine(v(t(NEGRO_LACA, oscuro), 0.5f), Offset(r.left + r.width * 0.7f, r.top), Offset(r.left + r.width * 0.7f, r.bottom), 0.8f * u)
            }
        }
        // La luz de los farolillos que baña la galería.
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, luzFarol.copy(alpha = if (oscuro) 0.09f else 0.04f)), p.friso, h), Offset(0f, p.friso), Size(w, h - p.friso))
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
    private fun DrawScope.cuadrifolio(c: Offset, r: Float, m: MetalMedievo) {
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

    // ---------------------------------------------------------------- el salón del hall

    private val BOVEDA = Color(0xFF233A7A)
    private val ORO_G = Color(0xFFD9B45A)
    private val CERA = Color(0xFFF3E6C4)

    /**
     * La nave: la bóveda azul sembrada de estrellas de oro con sus nervios, pilares de haz con capiteles de hojas, una
     * arquería ciega apuntada que enmarca cada trofeo, un rosetón sobre cada pilar entre los arcos, ventanales de lancetas
     * con vidrieras en los muros de los lados que echan manchas de colores sobre la piedra y el enlosado, y un candelero de
     * hierro al pie de cada pilar. De noche las vidrieras se apagan a la luz de la luna y mandan las velas.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val piedra = if (oscuro) Color(0xFF5A5F78) else PIEDRA
        val junta = if (oscuro) Color.Black else MURO
        val vela = Color(0xFFFFC25E)
        // El muro de sillería menuda.
        drawRect(Brush.verticalGradient(listOf(v(piedra, if (oscuro) 0.42f else 0.3f), v(piedra, if (oscuro) 0.3f else 0.18f)), 0f, h))
        drawPath(hiladas(Rect(0f, p.friso, w, p.zocalo), 14f * u, 30f * u, e.semilla), v(junta, if (oscuro) 0.26f else 0.26f), style = Stroke(0.7f * u))

        // Las manchas de luz de colores que echan las vidrieras.
        for (k in 0 until 14) {
            val c = puntoEn(Rect(0f, p.friso + 40f * u, w, h), e.semilla + 4, k)
            drawOval(VIDRIOS[k % 5].copy(alpha = if (oscuro) 0.06f else 0.07f), Offset(c.x - 16f * u, c.y - 7f * u), Size(32f * u, 14f * u))
        }

        // La arquería ciega: un arco apuntado sobre cada trofeo, con su moldura doble y un tracerío de tres lóbulos en la punta.
        for (y in p.filas) for (i in 0 until p.n) {
            val r = p.vano(i, y); val fl = p.semi * 1.15f
            val rr = Rect(r.left, r.top - p.semi * 0.15f, r.right, r.bottom)
            drawPath(arcoApuntado(rr, fl), v(piedra, if (oscuro) 0.16f else 0.1f))
            drawPath(arcoApuntado(Rect(rr.left + 2f * u, rr.top + 3f * u, rr.right - 2f * u, rr.bottom), fl - 3f * u), Color.Black.copy(alpha = if (oscuro) 0.22f else 0.05f), style = Stroke(4f * u))
            drawPath(arcoApuntado(Rect(rr.left - 3f * u, rr.top - 4f * u, rr.right + 3f * u, rr.bottom), fl + 4f * u), v(piedra, if (oscuro) 0.75f else 0.6f), style = Stroke(3f * u))
            drawPath(arcoApuntado(Rect(rr.left - 7f * u, rr.top - 9f * u, rr.right + 7f * u, rr.bottom), fl + 9f * u), v(junta, if (oscuro) 0.6f else 0.35f), style = Stroke(1f * u))
            // El trilóbulo en la punta, sobre la cabeza del trofeo.
            val tc = Offset(r.center.x, rr.top + fl * 0.32f); val tr = minOf(7f * u, p.semi * 0.11f)
            listOf(Offset(0f, -tr), Offset(-tr * 0.95f, tr * 0.5f), Offset(tr * 0.95f, tr * 0.5f)).forEach { o -> drawCircle(v(piedra, if (oscuro) 0.6f else 0.5f), tr * 0.75f, tc + o, style = Stroke(1f * u)) }
            caja(v(piedra, if (oscuro) 0.7f else 0.55f), Rect(r.left - 5f * u, r.bottom - 1f * u, r.right + 5f * u, r.bottom + 4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(r.left - 5f * u, r.bottom + 4f * u), Size(r.width + 10f * u, 2f * u))
        }

        // Los pilares de haz: tres columnillas con anillos y el capitel de hojas en cada fila; encima, el rosetón.
        for (i in 1 until p.n) {
            val x = i * p.paso; val g = p.pilar / 3f
            for (k in -1..1) {
                val cx = x + k * g
                caja(v(piedra, if (oscuro) (if (k == 0) 0.8f else 0.62f) else (if (k == 0) 0.62f else 0.5f)), Rect(cx - g * 0.48f, p.friso, cx + g * 0.48f, p.zocalo))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.16f), Offset(cx - g * 0.3f, p.friso), Offset(cx - g * 0.3f, p.zocalo), 0.8f * u)
            }
            for (y in p.filas) {
                val yc = p.arranque(y) - p.semi * 0.15f
                caja(v(piedra, if (oscuro) 0.85f else 0.68f), Rect(x - p.pilar / 2f - 2f * u, yc - 2f * u, x + p.pilar / 2f + 2f * u, yc + 2f * u))
                val hojas = Path()
                for (k in 0 until 4) { val hx = x - p.pilar / 2f + p.pilar * (k + 0.5f) / 4f; hojas.moveTo(hx - p.pilar / 9f, yc + 9f * u); hojas.quadraticTo(hx - p.pilar / 6f, yc + 3f * u, hx, yc + 2f * u); hojas.quadraticTo(hx + p.pilar / 6f, yc + 3f * u, hx + p.pilar / 9f, yc + 9f * u); hojas.close() }
                drawPath(hojas, v(piedra, if (oscuro) 0.85f else 0.68f))
                caja(v(t(ORO_G, oscuro), 0.5f), Rect(x - p.pilar / 2f, y + 112f * u, x + p.pilar / 2f, y + 114f * u))
                // El rosetón entre los arcos: aro de piedra, pétalos de vidrio y el ojo de oro.
                val rc = Offset(x, y - 6f * u); val rr = minOf(p.entre * 0.48f, 22f * u).coerceAtLeast(6f * u)
                if (oscuro) halo(rc, rr * 1.6f, Color(0xFF8FA8E8).copy(alpha = 0.03f))
                drawCircle(v(t(PLOMO, oscuro), if (oscuro) 0.75f else 0.55f), rr, rc)
                val np = 10
                for (k in 0 until np) {
                    val a = k * 2 * PI / np; val pc = Offset(rc.x + rr * 0.6f * cos(a).toFloat(), rc.y + rr * 0.6f * sin(a).toFloat())
                    drawCircle(v(VIDRIOS[k % 5], if (oscuro) 0.4f else 0.55f), rr * 0.24f, pc)
                    drawLine(v(piedra, if (oscuro) 0.6f else 0.7f), rc, Offset(rc.x + rr * cos(a).toFloat(), rc.y + rr * sin(a).toFloat()), 0.7f * u)
                }
                drawCircle(v(t(ORO_G, oscuro), if (oscuro) 0.6f else 0.75f), rr * 0.2f, rc)
                drawCircle(v(piedra, if (oscuro) 0.8f else 0.7f), rr, rc, style = Stroke(2f * u))
            }
        }

        // Los ventanales de lancetas en los muros de los lados: vidrios de colores entre plomos, de noche apagados por la luna.
        val vw = (p.holgura * 0.55f).coerceIn(5f * u, 18f * u)
        for (y in p.filas) listOf(vw / 2f + 2f * u, w - vw / 2f - 2f * u).forEach { x ->
            val r = Rect(x - vw / 2f, y - 10f * u, x + vw / 2f, y + 110f * u)
            val lanceta = arcoApuntado(r, vw * 0.9f)
            drawPath(lanceta, v(t(PLOMO, oscuro), if (oscuro) 0.8f else 0.6f))
            clipPath(lanceta) {
                val celda = maxOf(vw / 2f, 1f); var yy = r.top; var k = 0
                while (yy < r.bottom && k < 60) {
                    for (j in 0 until 2) drawRect(VIDRIOS[(k * 2 + j + (x / u).toInt()) % 5].copy(alpha = if (oscuro) 0.32f else 0.55f), Offset(r.left + j * celda + 0.6f * u, yy + 0.6f * u), Size(celda - 1.2f * u, celda * 1.3f - 1.2f * u))
                    yy += celda * 1.3f; k++
                }
                if (oscuro) drawRect(Color(0xFF9FB4E8).copy(alpha = 0.12f), r.topLeft, r.size)
            }
            drawPath(lanceta, v(piedra, if (oscuro) 0.8f else 0.65f), style = Stroke(2f * u))
        }

        // La bóveda: el plemento azul con estrellas de oro y los nervios que nacen de los pilares.
        val boveda = Rect(0f, 0f, w, p.friso)
        caja(v(t(BOVEDA, oscuro), if (oscuro) 0.75f else 0.4f), boveda)
        for (k in 0 until 40) { val c = puntoEn(boveda, e.semilla + 8, k); drawPath(estrella(c, 2.2f * u, 5, 0.45f), v(t(ORO_G, oscuro), if (oscuro) 0.75f else 0.7f)) }
        val nervios = Path()
        for (i in 0..p.n) {
            val x = i * p.paso
            if (i < p.n) { nervios.moveTo(x, p.friso); nervios.quadraticTo(x + p.paso * 0.15f, p.friso * 0.1f, x + p.paso / 2f, 0f) }
            if (i > 0) { nervios.moveTo(x, p.friso); nervios.quadraticTo(x - p.paso * 0.15f, p.friso * 0.1f, x - p.paso / 2f, 0f) }
        }
        drawPath(nervios, v(piedra, if (oscuro) 0.8f else 0.7f), style = Stroke(2.4f * u))
        drawRect(v(piedra, if (oscuro) 0.75f else 0.6f), Offset(0f, p.friso - 3f * u), Size(w, 3f * u))
        for (i in 0 until p.n) drawCircle(v(t(ORO_G, oscuro), 0.75f), 2.6f * u, Offset(p.cx(i), 1.6f * u))

        // Los candeleros de hierro al pie de cada pilar: el pie de tres patas, el astil, el plato y la vela encendida.
        for (i in 0..p.n) {
            val x = (i * p.paso).acotado(10f * u, w - 10f * u); val base = p.piso + 2f * u; val alto = minOf(70f * u, (p.piso - p.friso) * 0.2f)
            val top = base - alto
            halo(Offset(x, top - 8f * u), 34f * u, vela.copy(alpha = if (oscuro) 0.05f else 0.02f))
            drawLine(v(t(PLOMO, oscuro), 0.8f), Offset(x, base), Offset(x, top), 1.4f * u)
            listOf(-1f, 1f).forEach { s -> drawLine(v(t(PLOMO, oscuro), 0.8f), Offset(x, base - 8f * u), Offset(x + s * 7f * u, base), 1.2f * u) }
            listOf(0.4f, 0.7f).forEach { f -> drawCircle(v(t(PLOMO, oscuro), 0.8f), 1.6f * u, Offset(x, base - alto * f)) }
            caja(v(t(PLOMO, oscuro), 0.85f), Rect(x - 5f * u, top, x + 5f * u, top + 1.6f * u))
            caja(v(t(CERA, oscuro), if (oscuro) 0.85f else 0.95f), Rect(x - 1.8f * u, top - 11f * u, x + 1.8f * u, top))
            drawPath(llama(Offset(x, top - 11f * u), 6f * u), vela.copy(alpha = if (oscuro) 0.95f else 0.8f))
        }

        // El zócalo moldurado y el enlosado a dos tonos que huye hacia el altar.
        val z = Rect(0f, p.zocalo, w, p.piso)
        caja(v(piedra, if (oscuro) 0.55f else 0.42f), z)
        drawLine(v(piedra, 0.75f), Offset(0f, z.top + 1f * u), Offset(w, z.top + 1f * u), 1.6f * u)
        drawPath(juntas(z, 24f * u, true), v(junta, 0.4f), style = Stroke(0.6f * u))
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(t(LOSA_OSCURA, oscuro), if (oscuro) 0.6f else 0.32f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 8, u), v(piedra, if (oscuro) 0.5f else 0.6f), style = Stroke(1f * u))
        // La luz que baja de las vidrieras (de noche, la luna), más clara a los lados.
        drawRect(Brush.horizontalGradient(listOf((if (oscuro) Color(0xFF8FA8E8) else Color(0xFFFFF3D0)).copy(alpha = if (oscuro) 0.07f else 0.16f), Color.Transparent, (if (oscuro) Color(0xFF8FA8E8) else Color(0xFFFFF3D0)).copy(alpha = if (oscuro) 0.07f else 0.16f)), 0f, w), Offset(0f, p.friso), Size(w, h - p.friso))
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

    // ---------------------------------------------------------------- el salón del hall

    private val ORO_A = Color(0xFFC9A04A)
    private val BRONCE = Color(0xFFA77A38)
    private val AGUA = Color(0xFF6FA3A8)

    /**
     * La sala que da al patio: el alero de cedro con su fila de mocárabes y la faja de inscripción, una arcada de
     * herradura con dovelas de almagra y yeso y su alfiz sobre cada trofeo, columnas de mármol con capitel de cubo entre las
     * franjas, la sebka de yeso tallado en las enjutas, faroles de bronce calados en los lados que salpican la pared de
     * estrellitas de luz, el alicatado de estrellas de ocho puntas y, en el suelo de mármol, la acequia. De noche, el yeso se
     * vuelve verde de sombra y los faroles echan su encaje de luz.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val yeso = if (oscuro) Color(0xFF3E5A54) else YESO
        val sombra = if (oscuro) Color.Black else YESO_SOMBRA
        val farol = Color(0xFFFFC870)
        drawRect(Brush.verticalGradient(listOf(v(yeso, if (oscuro) 0.5f else 0.6f), v(yeso, if (oscuro) 0.32f else 0.35f)), 0f, h))

        // La sebka (red de rombos de yeso tallado) en todo el muro; las hornacinas la tapan donde van los trofeos.
        val sebka = Path(); val ps = maxOf(14f * u, 1f); var xs = -h; var ks = 0
        while (xs < w + h && ks < 400) { sebka.moveTo(xs, p.friso); sebka.lineTo(xs + (p.zocalo - p.friso), p.zocalo); sebka.moveTo(xs + (p.zocalo - p.friso), p.friso); sebka.lineTo(xs, p.zocalo); xs += ps; ks++ }
        clipPath(Path().apply { addRect(Rect(0f, p.friso, w, p.zocalo)) }) { drawPath(sebka, v(sombra, if (oscuro) 0.2f else 0.38f), style = Stroke(0.8f * u)) }

        // Los arcos de herradura con su alfiz: fondo liso, dovelas alternas de almagra y yeso, la rosca y el marco.
        for (y in p.filas) for (i in 0 until p.n) {
            val r0 = p.vano(i, y)
            val r = Rect(r0.left, r0.top - p.semi * 0.12f, r0.right, r0.bottom)
            val rad = r.width / 2f / 0.906f; val c = Offset(r.center.x, r.top + rad)
            val alfiz = Rect(c.x - rad - 7f * u, c.y - rad - 9f * u, c.x + rad + 7f * u, r.bottom)
            caja(v(yeso, if (oscuro) 0.6f else 0.75f), alfiz)
            drawRect(v(t(ORO_A, oscuro), if (oscuro) 0.4f else 0.45f), alfiz.topLeft, alfiz.size, style = Stroke(1.2f * u))
            drawRect(v(sombra, 0.5f), Offset(alfiz.left + 3f * u, alfiz.top + 3f * u), Size(alfiz.width - 6f * u, alfiz.height - 3f * u), style = Stroke(0.6f * u))
            for (k in 0 until 13) drawArc(v(if (k % 2 == 0) t(ALMAGRA, oscuro) else n(MARMOL, oscuro), if (oscuro) 0.55f else 0.42f), 155f + k * 230f / 13f, 230f / 13f, false, Offset(c.x - rad - 3f * u, c.y - rad - 3f * u), Size(2 * rad + 6f * u, 2 * rad + 6f * u), style = Stroke(6f * u))
            listOf(rad - 0f, rad + 6f * u).forEach { rr -> drawArc(v(sombra, if (oscuro) 0.6f else 0.5f), 155f, 230f, false, Offset(c.x - rr, c.y - rr), Size(2 * rr, 2 * rr), style = Stroke(0.6f * u)) }
            drawPath(arcoHerradura(r), v(yeso, if (oscuro) 0.22f else 0.4f))
            drawPath(arcoHerradura(Rect(r.left + 2f * u, r.top + 2f * u, r.right - 2f * u, r.bottom)), Color.Black.copy(alpha = if (oscuro) 0.2f else 0.05f), style = Stroke(4f * u))
            // El ataurique de las albanegas: dos roleos a cada lado.
            listOf(-1f, 1f).forEach { s -> val q = Offset(c.x + s * (rad + 1f * u) * 0.86f, alfiz.top + 6f * u); drawCircle(v(sombra, 0.7f), 3f * u, q, style = Stroke(0.7f * u)); drawCircle(v(t(ORO_A, oscuro), 0.5f), 1f * u, q) }
            caja(v(n(MARMOL, oscuro), if (oscuro) 0.6f else 0.7f), Rect(alfiz.left - 2f * u, r.bottom - 1f * u, alfiz.right + 2f * u, r.bottom + 4f * u))
        }

        // Las columnas de mármol entre las franjas: basa, fuste con collarino y capitel de cubo con su cimacio.
        for (i in 1 until p.n) {
            val x = i * p.paso; val cw = minOf(p.entre * 0.3f, 9f * u).coerceAtLeast(3f * u)
            for (y in p.filas) {
                val yc = p.arranque(y) - p.semi * 0.12f + 3f * u
                caja(v(n(MARMOL, oscuro), if (oscuro) 0.6f else 0.85f), Rect(x - cw / 2f, yc + 8f * u, x + cw / 2f, y + 142f * u))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.06f else 0.4f), Offset(x - cw * 0.2f, yc + 8f * u), Offset(x - cw * 0.2f, y + 142f * u), 0.8f * u)
                caja(v(sombra, 0.6f), Rect(x - cw / 2f - 0.6f * u, yc + 7f * u, x + cw / 2f + 0.6f * u, yc + 9f * u))
                caja(v(n(MARMOL, oscuro), if (oscuro) 0.7f else 0.9f), Rect(x - cw, yc, x + cw, yc + 7f * u))
                caja(v(t(AZUL, oscuro), if (oscuro) 0.4f else 0.3f), Rect(x - cw * 0.7f, yc + 2f * u, x + cw * 0.7f, yc + 5f * u))
                caja(v(t(CEDRO, oscuro), if (oscuro) 0.6f else 0.5f), Rect(x - cw * 1.4f, yc - 3f * u, x + cw * 1.4f, yc))
                caja(v(n(MARMOL, oscuro), if (oscuro) 0.6f else 0.8f), Rect(x - cw * 0.8f, y + 138f * u, x + cw * 0.8f, y + 142f * u))
            }
        }

        // Los faroles de bronce en los lados, uno por fila: cadena, el cuerpo calado, su luz y las estrellitas que echa al muro.
        val xf = (p.holgura * 0.42f).coerceIn(5f * u, 17f * u)
        for ((f, y) in p.filas.withIndex()) listOf(xf, w - xf).forEach { x ->
            val c = Offset(x, y + 30f * u); val fr = minOf(6f * u, xf * 0.6f).coerceAtLeast(3f * u)
            halo(c, 40f * u, farol.copy(alpha = if (oscuro) 0.045f else 0.02f))
            for (k in 0 until 14) { val a = azar(e.semilla, f * 40 + k + (x / u).toInt()) * 6.28f; val d = (14f + 26f * azar(e.semilla, f * 40 + k + 300)) * u
                drawPath(estrella(Offset(c.x + d * cos(a), c.y + d * sin(a)), 1.4f * u, 8, 0.5f), farol.copy(alpha = if (oscuro) 0.45f else 0.25f)) }
            drawLine(v(t(BRONCE, oscuro), 0.7f), Offset(x, y - 30f * u), Offset(x, c.y - fr * 1.8f), 0.8f * u)
            val cuerpo = Path().apply { moveTo(x, c.y - fr * 1.8f); lineTo(x + fr, c.y - fr * 0.6f); lineTo(x + fr * 0.8f, c.y + fr); lineTo(x, c.y + fr * 1.7f); lineTo(x - fr * 0.8f, c.y + fr); lineTo(x - fr, c.y - fr * 0.6f); close() }
            drawPath(cuerpo, v(t(BRONCE, oscuro), if (oscuro) 0.85f else 0.75f))
            drawPath(estrella(c, fr * 0.6f, 8, 0.55f), farol.copy(alpha = if (oscuro) 0.95f else 0.75f))
            drawPath(cuerpo, v(t(NEGRO, oscuro), 0.5f), style = Stroke(0.6f * u))
        }

        // El alero: la viga de cedro, la fila de mocárabes y la faja de inscripción entre filetes de oro.
        val viga = Rect(0f, 0f, w, p.friso * 0.34f)
        caja(v(t(CEDRO, oscuro), if (oscuro) 0.7f else 0.55f), viga)
        vetas(viga, v(t(VETA, oscuro), 0.45f), e.semilla, densidad = 0.5f, grosor = 0.5f)
        val moc = Rect(0f, viga.bottom, w, p.friso * 0.66f)
        caja(v(yeso, if (oscuro) 0.7f else 0.8f), moc)
        val celdas = Path(); val pc = maxOf(9f * u, 1f); var xm = 0f; var km = 0
        while (xm < w && km < 80) {
            val dx = if (km % 2 == 0) 0f else pc / 2f
            celdas.addPath(arcoMedioPunto(Rect(xm + dx + 1f * u, moc.top + 1f * u, xm + dx + pc - 1f * u, moc.center.y)))
            celdas.addPath(arcoMedioPunto(Rect(xm + 1f * u, moc.center.y, xm + pc - 1f * u, moc.bottom)))
            xm += pc; km++
        }
        drawPath(celdas, v(sombra, if (oscuro) 0.3f else 0.55f))
        val faja = Rect(0f, moc.bottom, w, p.friso)
        caja(v(t(AZUL, oscuro), if (oscuro) 0.5f else 0.32f), faja)
        val letra = Path(); var xl = 2f * u; var kl = 0
        while (xl < w && kl < 120) { val a = (2f + 3f * azar(e.semilla, kl)) * u; letra.moveTo(xl, faja.bottom - 2f * u); letra.lineTo(xl, faja.bottom - 2f * u - a); letra.moveTo(xl, faja.bottom - 2.5f * u); letra.quadraticTo(xl + 2f * u, faja.bottom - 4f * u, xl + 4f * u, faja.bottom - 2.5f * u); xl += maxOf(4.5f * u, 1f); kl++ }
        drawPath(letra, v(t(ORO_A, oscuro), if (oscuro) 0.7f else 0.75f), style = Stroke(0.7f * u))
        listOf(faja.top, faja.bottom).forEach { yy -> drawLine(v(t(ORO_A, oscuro), 0.7f), Offset(0f, yy), Offset(w, yy), 0.8f * u) }

        // El alicatado: estrellas de ocho puntas en verde, azul y negro sobre blanco, entre dos fajas de almagra.
        val al = Rect(0f, p.zocalo - 12f * u, w, p.piso)
        caja(v(n(MARMOL, oscuro), if (oscuro) 0.5f else 0.75f), al)
        val colores = listOf(t(VERDE, oscuro), t(AZUL, oscuro), t(NEGRO, oscuro))
        val lado = al.height * 0.62f; var xa = lado / 2f; var ka = 0
        while (xa < w + lado && ka < 60) { drawPath(estrella(Offset(xa, al.center.y), lado * 0.5f, 8, 0.72f, 0f), v(colores[ka % 3], if (oscuro) 0.55f else 0.45f)); xa += maxOf(lado * 1.05f, 1f); ka++ }
        listOf(al.top, al.bottom - 2.4f * u).forEach { yy -> caja(v(t(ALMAGRA, oscuro), if (oscuro) 0.6f else 0.5f), Rect(0f, yy, w, yy + 2.4f * u)) }
        // El piso de mármol y la acequia que corre a lo largo, con sus ondas.
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(n(MARMOL, oscuro), if (oscuro) 0.4f else 0.55f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 6, u), v(sombra, if (oscuro) 0.5f else 0.5f), style = Stroke(0.7f * u))
        val ya = p.piso + (h - p.piso) * 0.5f; val aa = minOf(6f * u, (h - p.piso) * 0.22f)
        caja(v(t(AGUA, oscuro), if (oscuro) 0.55f else 0.5f), Rect(0f, ya - aa, w, ya + aa))
        drawLine(v(n(MARMOL, oscuro), 0.8f), Offset(0f, ya - aa), Offset(w, ya - aa), 1f * u)
        val ondas = Path(); var xo = 0f; var ko = 0
        while (xo < w && ko < 50) { ondas.moveTo(xo, ya); ondas.quadraticTo(xo + 4f * u, ya - 1.2f * u, xo + 8f * u, ya); xo += maxOf(14f * u, 1f); ko++ }
        drawPath(ondas, Color.White.copy(alpha = if (oscuro) 0.2f else 0.5f), style = Stroke(0.6f * u))
        // La luz del patio, que sube del agua y del mármol (de noche, la de los faroles).
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, (if (oscuro) farol else Color.White).copy(alpha = if (oscuro) 0.07f else 0.22f)), p.friso, h), Offset(0f, p.friso), Size(w, h - p.friso))
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

    // ---------------------------------------------------------------- el salón del hall

    private val CIELO_M = Color(0xFF7FB2E5)
    private val HIERRO_M = Color(0xFF2C2A2E)
    private val CUERO = Color(0xFF7A4E2C)

    /**
     * La gran ger: el techo de varas anaranjadas que bajan de la corona (el toono, por donde se ve el cielo y entra un haz de
     * sol), el enrejado del khana en todo el muro, un fieltro acolchado colgado detrás de cada trofeo con su orla de cuernos
     * de carnero pespunteada, los dos postes pintados entre las franjas, el arco y el carcaj y la vihuela de cabeza de caballo
     * colgados en los lados, las alfombras del suelo y la estufa de hierro al centro. De noche, el toono deja ver las
     * estrellas y la estufa enciende el fieltro.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val fieltro = if (oscuro) Color(0xFF6A5644) else FIELTRO; val fieltroOsc = if (oscuro) Color(0xFF4E3E30) else FIELTRO_OSC
        val madera = t(NARANJA, oscuro)
        val fuego = Color(0xFFFF9C48)
        val techo = p.friso * 1.5f
        // El muro de fieltro y, encima, el enrejado del khana (varas cruzadas en rombo).
        drawRect(Brush.verticalGradient(listOf(v(fieltroOsc, if (oscuro) 0.55f else 0.45f), v(fieltro, if (oscuro) 0.4f else 0.3f)), 0f, h))
        val khana = Path(); val pk = maxOf(18f * u, 1f); val alto = p.zocalo - techo; var xk = -alto; var kk = 0
        while (xk < w + alto && kk < 300) { khana.moveTo(xk, techo); khana.lineTo(xk + alto, p.zocalo); khana.moveTo(xk + alto, techo); khana.lineTo(xk, p.zocalo); xk += pk; kk++ }
        clipPath(Path().apply { addRect(Rect(0f, techo, w, p.zocalo)) }) { drawPath(khana, v(t(KHANA, oscuro), if (oscuro) 0.4f else 0.3f), style = Stroke(1.6f * u)) }

        // El haz de sol que baja del toono (de noche, la luz de luna, muy tenue).
        drawPath(Path().apply { moveTo(w / 2f - 22f * u, 0f); lineTo(w / 2f + 22f * u, 0f); lineTo(w / 2f + 90f * u, h); lineTo(w / 2f - 10f * u, h); close() }, (if (oscuro) Color(0xFFBFD0F0) else Color(0xFFFFF2C8)).copy(alpha = if (oscuro) 0.035f else 0.16f))

        // Los fieltros colgados detrás de cada trofeo: la vara, el campo liso y la orla de cuernos pespunteada.
        for ((f, y) in p.filas.withIndex()) for (i in 0 until p.n) {
            val r = Rect(p.cx(i) - p.semi, y - 16f * u, p.cx(i) + p.semi, y + 142f * u)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.07f), Offset(r.left + 2f * u, r.top + 3f * u), r.size)
            caja(v(fieltro, if (oscuro) 0.5f else 0.75f), r)
            val g = minOf(9f * u, p.holgura * 0.5f).coerceAtLeast(4f * u)
            val orla = Path().apply { fillType = PathFillType.EvenOdd; addRect(r); addRect(Rect(r.left + g, r.top + g, r.right - g, r.bottom - g)) }
            drawPath(orla, v(if ((i + f) % 2 == 0) t(ROJO, oscuro) else t(AZUL, oscuro), if (oscuro) 0.6f else 0.5f))
            val cuernos = Path(); val pc = maxOf(g * 2.2f, 1f)
            var xc = r.left + pc / 2f; var kc = 0
            while (xc < r.right - pc / 3f && kc < 30) { cuernos.addPath(cuerno(Offset(xc, r.top + g * 0.55f), g * 0.8f)); cuernos.addPath(cuerno(Offset(xc, r.bottom - g * 0.45f), g * 0.8f)); xc += pc; kc++ }
            var yc = r.top + pc * 1.2f; kc = 0
            while (yc < r.bottom - pc && kc < 40) { cuernos.addPath(cuerno(Offset(r.left + g / 2f, yc), g * 0.7f)); cuernos.addPath(cuerno(Offset(r.right - g / 2f, yc), g * 0.7f)); yc += pc; kc++ }
            drawPath(cuernos, v(n(CREMA, oscuro), if (oscuro) 0.6f else 0.8f), style = Stroke(0.9f * u))
            drawRect(v(n(CREMA, oscuro), 0.6f), Offset(r.left + g + 2f * u, r.top + g + 2f * u), Size(r.width - 2 * g - 4f * u, r.height - 2 * g - 4f * u), style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 2f * u))))
            drawLine(v(madera, 0.85f), Offset(r.left - 3f * u, r.top), Offset(r.right + 3f * u, r.top), 2.2f * u)
        }

        // Los postes pintados entre las franjas: anaranjados con sus fajas azules y los nudos de la suerte.
        for (i in 1 until p.n) {
            val x = i * p.paso; val pw = minOf(p.entre * 0.42f, 12f * u).coerceAtLeast(4f * u)
            caja(v(madera, if (oscuro) 0.7f else 0.6f), Rect(x - pw / 2f, techo, x + pw / 2f, p.zocalo))
            drawLine(Color.White.copy(alpha = if (oscuro) 0.05f else 0.2f), Offset(x - pw * 0.25f, techo), Offset(x - pw * 0.25f, p.zocalo), 0.8f * u)
            for (y in p.filas) for (yy in listOf(y - 4f * u, y + 60f * u, y + 124f * u)) {
                caja(v(t(AZUL, oscuro), if (oscuro) 0.7f else 0.6f), Rect(x - pw / 2f, yy, x + pw / 2f, yy + 5f * u))
                drawPath(rombo(Offset(x, yy + 2.5f * u), pw * 0.3f, 2f * u), v(t(ORO, oscuro), 0.8f))
            }
        }

        // En los lados, una cosa colgada por fila: el arco con el carcaj o la vihuela de cabeza de caballo.
        val xl = (p.holgura * 0.45f).coerceIn(5f * u, 18f * u)
        for ((f, y) in p.filas.withIndex()) listOf(xl, w - xl).forEachIndexed { lado, x ->
            val y0 = y + 6f * u
            if ((f + lado) % 2 == 0) {
                drawArc(v(t(CUERO, oscuro), if (oscuro) 0.8f else 0.7f), 100f, 160f, false, Offset(x - 10f * u, y0), Size(14f * u, 80f * u), style = Stroke(2f * u))
                drawLine(v(n(CREMA, oscuro), 0.6f), Offset(x - 2f * u, y0 + 4f * u), Offset(x - 2f * u, y0 + 76f * u), 0.6f * u)
                drawRoundRect(v(t(ROJO_OSC, oscuro), if (oscuro) 0.8f else 0.65f), Offset(x + 1f * u, y0 + 26f * u), Size(6f * u, 40f * u), CornerRadius(2f * u))
                for (k in 0 until 3) drawLine(v(n(CREMA, oscuro), 0.7f), Offset(x + 2.5f * u + k * 1.5f * u, y0 + 26f * u), Offset(x + 2f * u + k * 2f * u, y0 + 18f * u), 0.8f * u)
            } else {
                drawOval(v(t(CUERO, oscuro), if (oscuro) 0.8f else 0.7f), Offset(x - 6f * u, y0 + 52f * u), Size(12f * u, 22f * u))
                drawLine(v(t(CUERO, oscuro), 0.8f), Offset(x, y0 + 54f * u), Offset(x, y0 + 8f * u), 2f * u)
                drawPath(Path().apply { moveTo(x - 1f * u, y0 + 8f * u); quadraticTo(x - 2f * u, y0, x + 4f * u, y0 + 2f * u); lineTo(x + 5f * u, y0 + 6f * u); lineTo(x + 1f * u, y0 + 8f * u); close() }, v(t(CUERO, oscuro), 0.85f))
                listOf(-1f, 1f).forEach { s -> drawLine(v(n(CREMA, oscuro), 0.5f), Offset(x + s * 0.6f * u, y0 + 10f * u), Offset(x + s * 0.6f * u, y0 + 70f * u), 0.4f * u) }
                drawLine(v(t(AZUL, oscuro), 0.7f), Offset(x, y0 + 8f * u), Offset(x + 2f * u, y0 + 28f * u), 1f * u)
            }
        }

        // El techo: varas anaranjadas desde la corona hasta el muro, la banda de fieltro y la corona con su cielo.
        val rc = minOf(w * 0.22f, 60f * u); val c = Offset(w / 2f, -rc * 0.2f)
        val varas = Path(); var xv = 0f; var kv = 0
        while (xv <= w + 1f && kv < 60) { varas.moveTo(c.x + (xv - c.x) * 0.2f, c.y + (techo - c.y) * 0.2f); varas.lineTo(xv, techo); xv += maxOf(13f * u, 1f); kv++ }
        drawPath(Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, techo); lineTo(0f, techo); close() }, v(fieltroOsc, if (oscuro) 0.5f else 0.5f))
        drawPath(varas, v(madera, if (oscuro) 0.7f else 0.75f), style = Stroke(2f * u))
        drawOval(if (oscuro) v(Color(0xFF14204A), 0.9f) else v(CIELO_M, 0.6f), Offset(c.x - rc, c.y - rc * 0.5f), Size(2 * rc, rc * 1.2f))
        if (oscuro) for (k in 0 until 8) drawCircle(Color.White.copy(alpha = 0.5f), 0.7f * u, puntoEn(Rect(c.x - rc * 0.6f, 0f, c.x + rc * 0.6f, c.y + rc * 0.55f), e.semilla, k))
        drawOval(v(madera, if (oscuro) 0.8f else 0.8f), Offset(c.x - rc, c.y - rc * 0.5f), Size(2 * rc, rc * 1.2f), style = Stroke(4f * u))
        val rayos = Path()
        for (k in 0 until 8) { val a = PI * k / 8; rayos.moveTo(c.x, c.y + rc * 0.1f); rayos.lineTo(c.x + rc * cos(a).toFloat(), c.y + rc * 0.1f + rc * 0.6f * sin(a).toFloat()) }
        clipPath(Path().apply { addOval(Rect(c.x - rc, c.y - rc * 0.5f, c.x + rc, c.y + rc * 0.7f)) }) { drawPath(rayos, v(madera, 0.75f), style = Stroke(1.6f * u)) }
        caja(v(fieltro, if (oscuro) 0.6f else 0.8f), Rect(0f, techo - 6f * u, w, techo))
        caja(v(t(AZUL, oscuro), if (oscuro) 0.6f else 0.5f), Rect(0f, techo - 4.4f * u, w, techo - 2.6f * u))
        drawLine(v(t(ROJO, oscuro), 0.6f), Offset(0f, techo), Offset(w, techo), 1.2f * u)

        // El suelo: las alfombras con su greca y la estufa de hierro al centro, con su chimenea que sube al toono.
        val z = Rect(0f, p.zocalo, w, p.piso)
        caja(v(t(KHANA, oscuro), if (oscuro) 0.55f else 0.4f), z)
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(t(ROJO_OSC, oscuro), if (oscuro) 0.6f else 0.38f), suelo)
        val greca = Path(); var xg = 0f; var kg = 0; val gy = p.piso + (h - p.piso) * 0.35f; val ga = minOf(5f * u, (h - p.piso) * 0.2f)
        while (xg < w && kg < 80) { greca.moveTo(xg, gy + ga); greca.lineTo(xg, gy); greca.lineTo(xg + ga, gy); greca.lineTo(xg + ga, gy + ga); greca.lineTo(xg + 2 * ga, gy + ga); xg += maxOf(2 * ga, 1f); kg++ }
        drawPath(greca, v(t(ORO, oscuro), if (oscuro) 0.6f else 0.6f), style = Stroke(1f * u))
        val est = Rect(w / 2f - 13f * u, h - 22f * u, w / 2f + 13f * u, h - 2f * u)
        halo(est.center, 46f * u, fuego.copy(alpha = if (oscuro) 0.05f else 0.02f))
        drawLine(v(t(HIERRO_M, oscuro), if (oscuro) 0.6f else 0.45f), Offset(w / 2f, est.top), Offset(w / 2f, est.top - 26f * u), 3f * u)
        drawRoundRect(v(t(HIERRO_M, oscuro), if (oscuro) 0.85f else 0.7f), est.topLeft, est.size, CornerRadius(2f * u))
        drawRoundRect(fuego.copy(alpha = if (oscuro) 0.85f else 0.6f), Offset(est.left + 7f * u, est.top + 7f * u), Size(12f * u, 7f * u), CornerRadius(1.5f * u))
        // El calor de la estufa en el fieltro.
        drawRect(Brush.radialGradient(listOf(fuego.copy(alpha = if (oscuro) 0.16f else 0.05f), Color.Transparent), Offset(w / 2f, h), maxOf(w * 1.1f, 1f)), Offset(0f, h - w * 1.1f), Size(w, w * 1.1f))
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
        val hierro = MetalMedievo(t(SUMI, oscuro), t(Color(0xFF3A3530), oscuro), t(Color(0xFF6A625A), oscuro), false)
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

    // ---------------------------------------------------------------- el salón del hall

    private val HINOKI = Color(0xFFB08A5E)
    private val TSUCHI = Color(0xFFC9B38C)
    private val WASHI = Color(0xFFF7F1E3)
    private val TATAMI = Color(0xFFCBC08E)
    private val HERI = Color(0xFF2E3A2C)
    private val PINO = Color(0xFF3A4A3A)

    /**
     * La sala de la posada del camino real: el techo de tablas con sus listones, el ranma calado de olas y la viga nageshi,
     * postes de hinoki con su veta entre las franjas y en los bordes, un shōji de papel con su cuadrícula kumiko detrás de
     * cada trofeo (con la sombra de una rama de pino), el muro de barro entre fila y fila, un farolillo de pared en cada poste,
     * los chōchin rojos de la posada en los lados y el tatami con sus orillas. De noche el papel se vuelve azul de luna y los
     * faroles se encienden.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val madera = t(HINOKI, oscuro); val veta = t(KIRI_VETA, oscuro)
        val barro = if (oscuro) Color(0xFF4A4036) else TSUCHI
        val papel = if (oscuro) Color(0xFF2E3A52) else WASHI
        val luzFarol = Color(0xFFFFC46B)
        // El muro de barro, con su arena.
        drawRect(Brush.verticalGradient(listOf(v(barro, if (oscuro) 0.55f else 0.45f), v(barro, if (oscuro) 0.42f else 0.3f)), 0f, h))
        val arena = Path()
        for (k in 0 until 90) { val c = puntoEn(Rect(0f, p.friso, w, p.zocalo), e.semilla + 1, k); arena.addOval(Rect(c, 0.5f * u)) }
        drawPath(arena, v(t(KIRI_SOMBRA, oscuro), if (oscuro) 0.6f else 0.7f))

        // Los shōji: el papel, la sombra de una rama de pino, la cuadrícula kumiko, el marco y el zócalo de madera.
        for ((f, y) in p.filas.withIndex()) for (i in 0 until p.n) {
            val r = Rect(p.cx(i) - p.semi, y - 18f * u, p.cx(i) + p.semi, y + 142f * u)
            caja(v(papel, if (oscuro) 0.55f else 0.8f), r)
            if ((i + f) % 3 == 0) clipPath(Path().apply { addRect(r) }) {
                val rama = Path(); val b = Offset(r.right, r.top + r.height * 0.18f)
                rama.moveTo(b.x, b.y); rama.quadraticTo(b.x - r.width * 0.3f, b.y - 6f * u, b.x - r.width * 0.55f, b.y + 8f * u)
                drawPath(rama, v(t(PINO, oscuro), if (oscuro) 0.3f else 0.18f), style = Stroke(2.4f * u))
                for (k in 0 until 4) { val q = Offset(b.x - r.width * (0.12f + 0.13f * k), b.y - 2f * u + k * 2.2f * u)
                    for (j in 0 until 7) { val a = PI * (1.1 + j * 0.12); drawLine(v(t(PINO, oscuro), if (oscuro) 0.3f else 0.18f), q, Offset(q.x + 9f * u * cos(a).toFloat(), q.y + 9f * u * sin(a).toFloat() * 0.6f), 0.8f * u) } }
            }
            val kumiko = Path()
            for (k in 1..3) { val x = r.left + r.width * k / 4f; kumiko.moveTo(x, r.top); kumiko.lineTo(x, r.bottom - 16f * u) }
            var yk = r.top + 24f * u; var kk = 0
            while (yk < r.bottom - 16f * u && kk < 12) { kumiko.moveTo(r.left, yk); kumiko.lineTo(r.right, yk); yk += maxOf(24f * u, 1f); kk++ }
            drawPath(kumiko, v(madera, if (oscuro) 0.55f else 0.4f), style = Stroke(1f * u))
            caja(v(madera, if (oscuro) 0.6f else 0.55f), Rect(r.left, r.bottom - 16f * u, r.right, r.bottom))
            drawLine(v(veta, 0.5f), Offset(r.left, r.bottom - 8f * u), Offset(r.right, r.bottom - 8f * u), 0.6f * u)
            drawRect(v(madera, if (oscuro) 0.75f else 0.7f), r.topLeft, r.size, style = Stroke(2.4f * u))
            // El riel de arriba (kamoi) que corre de poste a poste.
            caja(v(madera, if (oscuro) 0.7f else 0.62f), Rect(i * p.paso, r.top - 5f * u, (i + 1) * p.paso, r.top - 1f * u))
        }

        // Los postes de hinoki, entre las franjas y en los bordes, con su veta y un farolillo de pared en cada fila.
        val pw = minOf(p.pilar * 0.7f, p.entre * 0.5f).coerceAtLeast(4f * u)
        for (i in 0..p.n) {
            val x = (i * p.paso).acotado(pw / 2f, w - pw / 2f)
            val r = Rect(x - pw / 2f, p.friso, x + pw / 2f, p.piso)
            caja(v(madera, if (oscuro) 0.7f else 0.62f), r)
            vetas(r, v(veta, 0.5f), e.semilla + i, densidad = 0.8f, vertical = true, grosor = 0.5f)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.25f else 0.08f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            if (i in 1 until p.n) for (y in p.filas) {
                val c = Offset(x, y + 56f * u); val fa = minOf(6f * u, p.entre * 0.3f).coerceAtLeast(3f * u)
                halo(c, 30f * u, luzFarol.copy(alpha = if (oscuro) 0.045f else 0.015f))
                caja(v(t(URUSHI, oscuro), 0.8f), Rect(c.x - fa - 1f * u, c.y - fa * 1.8f, c.x + fa + 1f * u, c.y - fa * 1.5f))
                caja(v(n(ZOGE, oscuro), if (oscuro) 0.95f else 0.9f), Rect(c.x - fa, c.y - fa * 1.5f, c.x + fa, c.y + fa * 1.5f))
                if (oscuro) caja(luzFarol.copy(alpha = 0.3f), Rect(c.x - fa, c.y - fa * 1.5f, c.x + fa, c.y + fa * 1.5f))
                drawPath(juntas(Rect(c.x - fa, c.y - fa * 1.5f, c.x + fa, c.y + fa * 1.5f), fa * 0.75f, false), v(t(URUSHI, oscuro), 0.4f), style = Stroke(0.5f * u))
                caja(v(t(URUSHI, oscuro), 0.8f), Rect(c.x - fa - 1f * u, c.y + fa * 1.5f, c.x + fa + 1f * u, c.y + fa * 1.8f))
            }
        }

        // Los chōchin rojos de la posada en los lados, uno por fila: el farol de papel encerado con su marca y su luz.
        val xc = (p.holgura * 0.42f).coerceIn(6f * u, 18f * u)
        for (y in p.filas) listOf(xc, w - xc).forEach { x ->
            val c = Offset(x, y + 104f * u); val ra = minOf(7f * u, xc * 0.75f)
            halo(c, 36f * u, luzFarol.copy(alpha = if (oscuro) 0.045f else 0.015f))
            drawLine(v(t(URUSHI, oscuro), 0.6f), Offset(x, y + 70f * u), Offset(x, c.y - ra * 1.5f), 0.7f * u)
            drawOval(v(t(BERMELLON, oscuro), if (oscuro) 0.9f else 0.75f), Offset(c.x - ra, c.y - ra * 1.4f), Size(2 * ra, 2.8f * ra))
            drawPath(juntas(Rect(c.x - ra, c.y - ra * 1.2f, c.x + ra, c.y + ra * 1.2f), ra * 0.4f, false), v(t(URUSHI, oscuro), 0.35f), style = Stroke(0.5f * u))
            drawCircle(v(n(ZOGE, oscuro), 0.85f), ra * 0.45f, c, style = Stroke(0.8f * u))
            listOf(c.y - ra * 1.5f, c.y + ra * 1.3f).forEach { yy -> caja(v(t(URUSHI, oscuro), 0.85f), Rect(c.x - ra * 0.6f, yy, c.x + ra * 0.6f, yy + ra * 0.25f)) }
        }

        // Arriba: el techo de tablas con sus listones, el ranma calado de olas y la viga nageshi.
        val cielo = Rect(0f, 0f, w, p.friso * 0.3f)
        caja(v(madera, if (oscuro) 0.6f else 0.5f), cielo)
        drawPath(juntas(cielo, 22f * u, true), v(veta, 0.6f), style = Stroke(1.2f * u))
        val ranma = Rect(0f, cielo.bottom, w, p.friso * 0.8f)
        caja(v(t(URUSHI, oscuro), if (oscuro) 0.5f else 0.25f), ranma)
        val olas = Path(); val ro = ranma.height * 0.42f; var xo = 0f; var ko = 0
        while (xo < w + ro && ko < 80) {
            for (fila in 0..1) { val cy = ranma.top + ranma.height * (0.45f + fila * 0.4f); val cx = xo + (if (fila == 1) ro else 0f)
                olas.addArc(Rect(cx - ro, cy - ro, cx + ro, cy + ro), 180f, 180f); olas.addArc(Rect(cx - ro * 0.6f, cy - ro * 0.6f, cx + ro * 0.6f, cy + ro * 0.6f), 180f, 180f) }
            xo += maxOf(ro * 2f, 1f); ko++
        }
        clipPath(Path().apply { addRect(ranma) }) { drawPath(olas, v(madera, if (oscuro) 0.6f else 0.7f), style = Stroke(1f * u)) }
        val nageshi = Rect(0f, ranma.bottom, w, p.friso)
        caja(v(madera, if (oscuro) 0.75f else 0.68f), nageshi)
        drawLine(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.12f), Offset(0f, nageshi.bottom), Offset(w, nageshi.bottom), 1.2f * u)
        for (i in 0..p.n) drawCircle(v(t(URUSHI, oscuro), 0.6f), 2f * u, Offset((i * p.paso).acotado(4f * u, w - 4f * u), nageshi.center.y))

        // El tatami: las esteras con su trama y la orilla de tela oscura.
        val suelo = Rect(0f, p.zocalo + 6f * u, w, h)
        caja(v(t(TATAMI, oscuro), if (oscuro) 0.55f else 0.5f), suelo)
        drawPath(juntas(suelo, 2.2f * u, false), v(t(KIRI_SOMBRA, oscuro), 0.35f), style = Stroke(0.5f * u))
        val heri = Path(); heri.moveTo(0f, suelo.top + 1.5f * u); heri.lineTo(w, suelo.top + 1.5f * u)
        for (k in -3..3) { val x = w / 2f + k * w * 0.36f; heri.moveTo(x, h); heri.lineTo(w / 2f + (x - w / 2f) * 0.7f, suelo.top) }
        drawPath(heri, v(t(HERI, oscuro), if (oscuro) 0.75f else 0.6f), style = Stroke(2.4f * u))
        // La luz: de día la que pasa por el papel, de noche la de los faroles, que se junta abajo.
        drawRect(Brush.verticalGradient(listOf((if (oscuro) luzFarol else Color.White).copy(alpha = if (oscuro) 0.02f else 0.18f), (if (oscuro) luzFarol else Color.White).copy(alpha = if (oscuro) 0.1f else 0.04f)), p.friso, h), Offset(0f, p.friso), Size(w, h - p.friso))
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

    // ---------------------------------------------------------------- el salón del hall

    private val ROBBIA = Color(0xFF4A6FA5)
    private val COTTO = Color(0xFFC27A52)
    private val CERA_R = Color(0xFFF3E6C4)

    /**
     * La galería a la florentina: el artesonado de casetones con sus rosetas que huye hacia el fondo, el entablamento de
     * pietra serena, pilastras corintias entre las franjas y en los bordes, una arcada de medio punto sobre cada trofeo con
     * la hornacina de concha, un tondo azul y blanco en cada enjuta, el muro de intonaco con sus vetas de cal y el pavimento
     * de cotto y mármol en damero que huye. De día la luz entra rasante por la izquierda; de noche, los candelabros de los
     * lados.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val muro = n(INTONACO, oscuro); val serena = t(SERENA, oscuro)
        val vela = Color(0xFFFFC15A)
        val techo = p.friso * 1.25f
        drawRect(Brush.verticalGradient(listOf(v(muro, if (oscuro) 0.4f else 0.7f), v(muro, if (oscuro) 0.3f else 0.45f)), 0f, h))
        // Las vetas de la cal: trazos largos y tenues.
        for (k in 0 until 18) { val c = puntoEn(Rect(0f, techo, w, p.zocalo), e.semilla + 6, k); drawLine(v(t(SANGUINA, oscuro), if (oscuro) 0.05f else 0.1f), c, Offset(c.x + (20f + 30f * azar(e.semilla, k)) * u, c.y + 3f * u), 0.8f * u) }

        // La arcada: hornacina con su concha, el arco de pietra serena con la clave, y la repisa del pedestal.
        for (y in p.filas) for (i in 0 until p.n) {
            val r = p.vano(i, y); val c = Offset(r.center.x, r.top + p.semi)
            drawPath(arcoMedioPunto(r), v(muro, if (oscuro) 0.22f else 0.85f))
            // La concha: los gajos que salen de abajo hacia el borde de la media cúpula.
            val concha = Path(); val rc = p.semi * 0.92f
            for (k in 1 until 12) { val a = PI + k * PI / 12; concha.moveTo(c.x, c.y + rc * 0.15f); concha.lineTo(c.x + rc * cos(a).toFloat(), c.y + rc * 0.15f + rc * 0.95f * sin(a).toFloat()) }
            clipPath(arcoMedioPunto(r)) {
                drawPath(concha, v(serena, if (oscuro) 0.4f else 0.25f), style = Stroke(0.8f * u))
                drawArc(v(serena, if (oscuro) 0.4f else 0.3f), 180f, 180f, false, Offset(c.x - rc * 0.22f, c.y + rc * 0.15f - rc * 0.2f), Size(rc * 0.44f, rc * 0.4f), style = Stroke(1f * u))
            }
            drawLine(v(serena, if (oscuro) 0.6f else 0.45f), Offset(r.left, c.y + 1f * u), Offset(r.right, c.y + 1f * u), 1f * u)
            drawPath(arcoMedioPunto(Rect(r.left + 2f * u, r.top + 2f * u, r.right - 2f * u, r.bottom)), Color.Black.copy(alpha = if (oscuro) 0.22f else 0.05f), style = Stroke(4f * u))
            val g = minOf(6f * u, p.holgura * 0.35f).coerceAtLeast(2.5f * u); val ra = p.semi + g / 2f
            drawArc(v(serena, if (oscuro) 0.75f else 0.6f), 180f, 180f, false, Offset(c.x - ra, c.y - ra), Size(2 * ra, 2 * ra), style = Stroke(g))
            drawArc(v(serena, if (oscuro) 0.6f else 0.5f), 180f, 180f, false, Offset(c.x - ra - g, c.y - ra - g), Size(2 * (ra + g), 2 * (ra + g)), style = Stroke(0.8f * u))
            listOf(r.left - g, r.right).forEach { x -> caja(v(serena, if (oscuro) 0.7f else 0.55f), Rect(x, c.y, x + g, r.bottom)) }
            drawPath(Path().apply { moveTo(c.x - g * 0.8f, r.top - g * 1.3f); lineTo(c.x + g * 0.8f, r.top - g * 1.3f); lineTo(c.x + g * 0.5f, r.top + g * 0.4f); lineTo(c.x - g * 0.5f, r.top + g * 0.4f); close() }, v(serena, if (oscuro) 0.85f else 0.7f))
            caja(v(serena, if (oscuro) 0.75f else 0.6f), Rect(r.left - g - 3f * u, r.bottom - 1f * u, r.right + g + 3f * u, r.bottom + 4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.08f), Offset(r.left - g - 3f * u, r.bottom + 4f * u), Size(r.width + 2 * g + 6f * u, 2f * u))
        }

        // Las pilastras corintias de pietra serena: estrías, la basa y el capitel de hojas y volutas; los tondos en las enjutas.
        for (i in 0..p.n) {
            val x = (i * p.paso).acotado(p.pilar * 0.3f, w - p.pilar * 0.3f); val pw = if (i == 0 || i == p.n) p.pilar * 0.6f else p.pilar * 0.8f
            val r = Rect(x - pw / 2f, techo + 10f * u, x + pw / 2f, p.zocalo)
            caja(v(serena, if (oscuro) 0.6f else 0.4f), r)
            drawPath(juntas(Rect(r.left + pw * 0.15f, r.top + 12f * u, r.right - pw * 0.15f, r.bottom - 10f * u), pw * 0.7f / 4f, true), v(serena, if (oscuro) 0.85f else 0.65f), style = Stroke(0.8f * u))
            caja(v(serena, if (oscuro) 0.75f else 0.55f), Rect(r.left - 2f * u, r.bottom - 8f * u, r.right + 2f * u, r.bottom))
            val cap = Rect(r.left - 3f * u, r.top - 2f * u, r.right + 3f * u, r.top + 12f * u)
            caja(v(serena, if (oscuro) 0.7f else 0.5f), cap)
            val hojas = Path()
            for (k in 0 until 3) { val hx = cap.left + cap.width * (k + 0.5f) / 3f; hojas.moveTo(hx - cap.width / 7f, cap.bottom); hojas.quadraticTo(hx, cap.top + 2f * u, hx + cap.width / 7f, cap.bottom); hojas.close() }
            drawPath(hojas, v(serena, if (oscuro) 0.9f else 0.7f))
            listOf(cap.left + 1.5f * u, cap.right - 1.5f * u).forEach { vx -> drawCircle(v(serena, if (oscuro) 0.9f else 0.7f), 2f * u, Offset(vx, cap.top + 2f * u), style = Stroke(0.8f * u)) }
            if (i in 1 until p.n) for (y in p.filas) {
                val tc = Offset(i * p.paso, p.arranque(y) - p.semi - 2f * u); val tr = minOf(p.entre * 0.42f, 16f * u).coerceAtLeast(5f * u)
                if (tc.y - tr < techo + 2f * u) continue
                drawCircle(v(t(ROBBIA, oscuro), if (oscuro) 0.6f else 0.55f), tr, tc)
                drawCircle(v(n(Color.White, oscuro), if (oscuro) 0.5f else 0.8f), tr * 0.42f, Offset(tc.x, tc.y - tr * 0.12f))
                drawOval(v(n(Color.White, oscuro), if (oscuro) 0.5f else 0.8f), Offset(tc.x - tr * 0.45f, tc.y + tr * 0.2f), Size(tr * 0.9f, tr * 0.55f))
                for (k in 0 until 10) { val a = k * 2 * PI / 10; drawCircle(v(t(Color(0xFF6E8C3A), oscuro), if (oscuro) 0.6f else 0.6f), tr * 0.16f, Offset(tc.x + tr * 1.08f * cos(a).toFloat(), tc.y + tr * 1.08f * sin(a).toFloat())) }
            }
        }

        // Los candelabros de bronce en los lados, uno por fila: el pie, el astil torneado, los tres brazos y sus velas.
        val xc = (p.holgura * 0.45f).coerceIn(6f * u, 18f * u)
        for (y in p.filas) listOf(xc, w - xc).forEach { x ->
            val top = y + 64f * u; val base = y + 140f * u
            halo(Offset(x, top - 8f * u), 40f * u, vela.copy(alpha = if (oscuro) 0.05f else 0.015f))
            val bronce = t(Color(0xFF9C7A3E), oscuro)
            drawLine(v(bronce, 0.85f), Offset(x, base), Offset(x, top), 1.6f * u)
            listOf(0.25f, 0.5f, 0.75f).forEach { f -> drawOval(v(bronce, 0.85f), Offset(x - 2.6f * u, base - (base - top) * f - 1.5f * u), Size(5.2f * u, 3f * u)) }
            drawPath(Path().apply { moveTo(x - 7f * u, base); lineTo(x + 7f * u, base); lineTo(x + 2f * u, base - 6f * u); lineTo(x - 2f * u, base - 6f * u); close() }, v(bronce, 0.85f))
            drawPath(Path().apply { moveTo(x - 7f * u, top - 3f * u); quadraticTo(x - 6f * u, top + 4f * u, x, top + 3f * u); quadraticTo(x + 6f * u, top + 4f * u, x + 7f * u, top - 3f * u) }, v(bronce, 0.85f), style = Stroke(1.2f * u))
            listOf(-7f, 0f, 7f).forEach { dx -> val vx = x + dx * u; val vt = top - (if (dx == 0f) 6f else 3f) * u
                caja(v(t(CERA_R, oscuro), if (oscuro) 0.85f else 0.95f), Rect(vx - 1.2f * u, vt - 8f * u, vx + 1.2f * u, vt))
                drawPath(llama(Offset(vx, vt - 8f * u), 5f * u), vela.copy(alpha = if (oscuro) 0.95f else 0.75f)) }
        }

        // El artesonado: casetones en perspectiva con su roseta, y el entablamento (arquitrabe, friso y cornisa).
        val art = Rect(0f, 0f, w, techo - 9f * u)
        caja(v(t(NOGAL, oscuro), if (oscuro) 0.6f else 0.42f), art)
        val fuga = Offset(w / 2f, art.bottom + w * 0.6f)
        val cas = Path(); val nCol = (w / maxOf(34f * u, 1f)).toInt().coerceIn(4, 16)
        for (k in 0..nCol) { val x = w * k / nCol; cas.moveTo(x, 0f); cas.lineTo(x + (fuga.x - x) * (art.bottom / fuga.y), art.bottom) }
        var yy = 0f; var paso = art.bottom * 0.42f; var kk = 0
        while (yy < art.bottom && kk < 8) { cas.moveTo(0f, yy); cas.lineTo(w, yy); yy += maxOf(paso, 1f); paso *= 0.72f; kk++ }
        drawPath(cas, v(t(NOGAL_OSC, oscuro), if (oscuro) 0.85f else 0.65f), style = Stroke(1.6f * u))
        for (k in 0 until nCol) drawPath(estrella(Offset(w * (k + 0.5f) / nCol + (fuga.x - w * (k + 0.5f) / nCol) * 0.1f, art.bottom * 0.2f), 3f * u, 8, 0.5f), v(t(BOJ, oscuro), if (oscuro) 0.6f else 0.7f))
        val arq = Rect(0f, art.bottom, w, techo)
        caja(v(serena, if (oscuro) 0.6f else 0.45f), arq)
        val ovos = Path(); var xo = 2f * u; var ko = 0
        while (xo < w && ko < 120) { ovos.addOval(Rect(xo, arq.top + 2f * u, xo + 3f * u, arq.top + 6.5f * u)); xo += maxOf(5f * u, 1f); ko++ }
        drawPath(ovos, v(serena, if (oscuro) 0.85f else 0.7f))
        drawLine(v(serena, if (oscuro) 0.85f else 0.7f), Offset(0f, arq.bottom - 1f * u), Offset(w, arq.bottom - 1f * u), 1.4f * u)

        // El zócalo de pietra serena y el pavimento en damero de cotto y mármol que huye.
        caja(v(serena, if (oscuro) 0.55f else 0.42f), Rect(0f, p.zocalo, w, p.piso))
        drawLine(v(serena, 0.75f), Offset(0f, p.zocalo + 1.5f * u), Offset(w, p.zocalo + 1.5f * u), 1f * u)
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(n(INTONACO, oscuro), if (oscuro) 0.4f else 0.6f), suelo)
        val oscuras = Path(); val nr = 12; val filasP = 5; var y0 = p.piso; var pa = (h - p.piso) * 0.08f
        for (j in 0 until filasP) {
            val y1 = minOf(y0 + pa, h)
            val f0 = 0.62f + 0.38f * (y0 - p.piso) / maxOf(h - p.piso, 1f); val f1 = 0.62f + 0.38f * (y1 - p.piso) / maxOf(h - p.piso, 1f)
            for (k in -nr until nr) if ((k + j) % 2 == 0) {
                val xa = k * w / nr; val xb = (k + 1) * w / nr
                oscuras.moveTo(w / 2f + xa * f0, y0); oscuras.lineTo(w / 2f + xb * f0, y0); oscuras.lineTo(w / 2f + xb * f1, y1); oscuras.lineTo(w / 2f + xa * f1, y1); oscuras.close()
            }
            y0 = y1; pa *= 1.5f
        }
        drawPath(oscuras, v(t(COTTO, oscuro), if (oscuro) 0.5f else 0.45f))
        // La luz: de día entra rasante por la izquierda; de noche, el resplandor de los candelabros a los lados.
        drawRect(Brush.horizontalGradient(if (oscuro) listOf(vela.copy(alpha = 0.08f), Color.Transparent, vela.copy(alpha = 0.08f)) else listOf(Color(0xFFFFF4D8).copy(alpha = 0.3f), Color.Transparent, Color.Black.copy(alpha = 0.04f)), 0f, w), Offset(0f, techo), Size(w, h - techo))
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

    // ---------------------------------------------------------------- el salón del hall

    private val MAR = Color(0xFF3E7A86)
    private val CIELO_P = Color(0xFFA9CAD8)
    private val LATON = Color(0xFFC9A04A)
    private val HIERRO_P = Color(0xFF2E2C2A)

    /**
     * La cámara del capitán, en la popa: arriba, la galería de ventanas emplomadas por donde se ven el mar y el cielo (de
     * noche, la luna sobre el agua); el forro de tablas de roble con sus juntas de brea y cabillas, un panel moldurado detrás
     * de cada trofeo, una cuaderna curva entre las franjas con su farol de latón colgado, ojos de buey en los costados, la
     * cubierta de tablones calafateados que huye y, en los rincones, un barril y un rollo de cabo. De noche mandan los faroles.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val roble = t(ROBLE, oscuro); val robleLuz = t(ROBLE_LUZ, oscuro); val brea = t(BREA, oscuro)
        val luzFarol = Color(0xFFFFC266)
        val forro = if (oscuro) roble else CUBIERTA
        drawRect(Brush.verticalGradient(listOf(v(forro, if (oscuro) 0.6f else 0.55f), v(forro, if (oscuro) 0.48f else 0.42f)), 0f, h))
        // El forro: tablas horizontales con sus juntas de brea a matajunta y las cabillas.
        drawPath(hiladas(Rect(0f, p.friso, w, p.zocalo), 13f * u, 90f * u, e.semilla), v(brea, if (oscuro) 0.6f else 0.35f), style = Stroke(0.9f * u))
        val cabillas = Path()
        for (k in 0 until 50) { val c = puntoEn(Rect(0f, p.friso, w, p.zocalo), e.semilla + 3, k); val yy = p.friso + (((c.y - p.friso) / (13f * u)).toInt() + 0.5f) * 13f * u; cabillas.addOval(Rect(Offset(c.x, yy), 0.9f * u)) }
        drawPath(cabillas, v(brea, if (oscuro) 0.6f else 0.4f))
        vetas(Rect(0f, p.friso, w, p.zocalo), v(t(VETA, oscuro), 0.22f), e.semilla, densidad = 0.3f, grosor = 0.5f)

        // El panel moldurado detrás de cada trofeo: el marco con su bisel y la tabla del centro más lisa.
        for (y in p.filas) for (i in 0 until p.n) {
            val r = Rect(p.cx(i) - p.semi, y - 16f * u, p.cx(i) + p.semi, y + 142f * u)
            caja(v(robleLuz, if (oscuro) 0.55f else 0.36f), r)
            bisel(r, Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Color.Black.copy(alpha = if (oscuro) 0.35f else 0.14f), 2f * u)
            val g = minOf(8f * u, p.holgura * 0.4f).coerceAtLeast(3f * u)
            val dentro = Rect(r.left + g, r.top + g, r.right - g, r.bottom - g)
            caja(v(if (oscuro) roble else CUBIERTA, if (oscuro) 0.4f else 0.2f), dentro)
            bisel(dentro, Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Color.White.copy(alpha = if (oscuro) 0.04f else 0.15f), 1.6f * u)
            caja(v(robleLuz, if (oscuro) 0.7f else 0.55f), Rect(r.left - 4f * u, r.bottom - 1f * u, r.right + 4f * u, r.bottom + 4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.35f else 0.12f), Offset(r.left - 4f * u, r.bottom + 4f * u), Size(r.width + 8f * u, 2f * u))
        }

        // Las cuadernas curvas entre las franjas, con su farol de latón colgado en cada fila.
        for (i in 1 until p.n) {
            val x = i * p.paso; val cw = minOf(p.pilar * 0.75f, p.entre * 0.5f).coerceAtLeast(4f * u)
            // El puntal, con sus dos curvas (las «rodillas») que lo amarran al bao de arriba.
            val puntal = Rect(x - cw / 2f, p.friso, x + cw / 2f, p.zocalo)
            caja(v(t(ROBLE_OSC, oscuro), if (oscuro) 0.8f else 0.55f), puntal)
            vetas(puntal, v(t(VETA, oscuro), 0.4f), e.semilla + i, densidad = 0.6f, vertical = true, grosor = 0.5f)
            drawRect(Color.White.copy(alpha = if (oscuro) 0.04f else 0.14f), puntal.topLeft, Size(1.2f * u, puntal.height))
            listOf(-1f, 1f).forEach { s ->
                val rodilla = Path().apply { moveTo(x + s * cw / 2f, p.friso); lineTo(x + s * (cw / 2f + 16f * u), p.friso); quadraticTo(x + s * (cw / 2f + 3f * u), p.friso + 3f * u, x + s * cw / 2f, p.friso + 18f * u); close() }
                drawPath(rodilla, v(t(ROBLE_OSC, oscuro), if (oscuro) 0.8f else 0.55f))
            }
            for (y in p.filas) {
                val f = Offset(x, y + 40f * u); val fw = minOf(7f * u, p.entre * 0.3f).coerceAtLeast(3f * u)
                halo(f, 44f * u, luzFarol.copy(alpha = if (oscuro) 0.05f else 0.02f))
                drawLine(v(t(HIERRO_P, oscuro), 0.7f), Offset(x, y - 26f * u), Offset(x, f.y - fw * 1.8f), 0.8f * u)
                drawPath(Path().apply { moveTo(x - fw * 0.6f, f.y - fw * 1.8f); lineTo(x + fw * 0.6f, f.y - fw * 1.8f); lineTo(x + fw, f.y - fw * 1.2f); lineTo(x - fw, f.y - fw * 1.2f); close() }, v(t(LATON, oscuro), 0.9f))
                drawRect(luzFarol.copy(alpha = if (oscuro) 0.75f else 0.45f), Offset(x - fw * 0.8f, f.y - fw * 1.2f), Size(fw * 1.6f, fw * 2.2f))
                drawPath(llama(Offset(x, f.y + fw * 0.4f), fw * 1.1f), Color(0xFFFFE6A8).copy(alpha = if (oscuro) 0.95f else 0.7f))
                drawRect(v(t(LATON, oscuro), 0.85f), Offset(x - fw * 0.8f, f.y - fw * 1.2f), Size(fw * 1.6f, fw * 2.2f), style = Stroke(1f * u))
                drawLine(v(t(LATON, oscuro), 0.85f), Offset(x, f.y - fw * 1.2f), Offset(x, f.y + fw), 0.6f * u)
                caja(v(t(LATON, oscuro), 0.9f), Rect(x - fw, f.y + fw, x + fw, f.y + fw * 1.4f))
            }
        }

        // Los ojos de buey en los costados, uno por fila: el aro de latón con sus pernos y el mar que se mece afuera.
        val xo = (p.holgura * 0.42f).coerceIn(6f * u, 18f * u)
        for ((f, y) in p.filas.withIndex()) listOf(xo, w - xo).forEach { x ->
            val c = Offset(x, y + 30f * u); val ro = minOf(9f * u, xo * 0.85f)
            clipPath(Path().apply { addOval(Rect(c, ro)) }) {
                caja(v(if (oscuro) Color(0xFF1A2A48) else CIELO_P, if (oscuro) 0.85f else 0.6f), Rect(c, ro))
                caja(v(t(MAR, oscuro), if (oscuro) 0.75f else 0.6f), Rect(c.x - ro, c.y + ro * 0.1f * (1 + f % 2), c.x + ro, c.y + ro))
                drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.45f), Offset(c.x - ro, c.y + ro * 0.45f), Offset(c.x + ro * 0.3f, c.y + ro * 0.4f), 0.6f * u)
            }
            drawCircle(v(t(LATON, oscuro), 0.85f), ro + 1.4f * u, c, style = Stroke(2.6f * u))
            for (k in 0 until 6) { val a = k * PI / 3; drawCircle(v(t(HIERRO_P, oscuro), 0.6f), 0.7f * u, Offset(c.x + (ro + 1.4f * u) * cos(a).toFloat(), c.y + (ro + 1.4f * u) * sin(a).toFloat())) }
        }

        // La galería de popa: ventanas emplomadas con el cielo y el mar (de noche, la luna sobre el agua) y el bao de abajo.
        val alto = p.friso - 6f * u
        caja(v(t(ROBLE_OSC, oscuro), if (oscuro) 0.85f else 0.6f), Rect(0f, 0f, w, p.friso))
        val nv = p.n * 2
        for (k in 0 until nv) {
            val x0 = w * k / nv + 3f * u; val x1 = w * (k + 1) / nv - 3f * u
            if (x1 - x0 < 8f * u) continue
            val ven = Rect(x0, 3f * u, x1, alto)
            caja(if (oscuro) v(Color(0xFF1A2A48), 0.9f) else v(CIELO_P, 0.75f), ven)
            caja(v(t(MAR, oscuro), if (oscuro) 0.8f else 0.65f), Rect(x0, ven.top + ven.height * 0.58f, x1, alto))
            if (oscuro && k == nv / 2) { drawCircle(Color(0xFFF1E7C6).copy(alpha = 0.7f), 4f * u, Offset((x0 + x1) / 2f, ven.top + ven.height * 0.28f)); drawLine(Color(0xFFF1E7C6).copy(alpha = 0.35f), Offset((x0 + x1) / 2f - 5f * u, ven.top + ven.height * 0.72f), Offset((x0 + x1) / 2f + 5f * u, ven.top + ven.height * 0.72f), 1.2f * u) }
            drawLine(Color.White.copy(alpha = if (oscuro) 0.15f else 0.5f), Offset(x0 + 2f * u, ven.top + ven.height * 0.8f), Offset(x1 - 4f * u, ven.top + ven.height * 0.8f), 0.6f * u)
            val plomo = Path(); val pc = maxOf((x1 - x0) / 3f, 1f)
            var xx = x0 + pc; var kx = 0; while (xx < x1 - 1f && kx < 8) { plomo.moveTo(xx, ven.top); plomo.lineTo(xx, alto); xx += pc; kx++ }
            var yv = ven.top + 7f * u; var kv = 0; while (yv < alto && kv < 10) { plomo.moveTo(x0, yv); plomo.lineTo(x1, yv); yv += maxOf(7f * u, 1f); kv++ }
            drawPath(plomo, v(t(LATON, oscuro), if (oscuro) 0.5f else 0.6f), style = Stroke(0.7f * u))
            drawRect(v(t(ROBLE_OSC, oscuro), 0.9f), ven.topLeft, ven.size, style = Stroke(1.6f * u))
        }
        caja(v(t(ROBLE_OSC, oscuro), if (oscuro) 0.9f else 0.7f), Rect(0f, alto, w, p.friso + 2f * u))
        drawLine(v(t(LATON, oscuro), 0.5f), Offset(0f, alto + 2f * u), Offset(w, alto + 2f * u), 0.7f * u)

        // La cubierta: tablones calafateados que huyen, con sus clavos; en los rincones, un barril y un rollo de cabo.
        caja(v(t(ROBLE_OSC, oscuro), if (oscuro) 0.75f else 0.5f), Rect(0f, p.zocalo, w, p.piso))
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(t(CUBIERTA, oscuro), if (oscuro) 0.55f else 0.42f), suelo)
        val tablones = Path()
        for (k in -10..10) { val x = w / 2f + k * w / 10f; tablones.moveTo(x, h); tablones.lineTo(w / 2f + (x - w / 2f) * 0.55f, p.piso) }
        drawPath(tablones, v(brea, if (oscuro) 0.75f else 0.55f), style = Stroke(1f * u))
        val bx = 4f * u; val by = h - 2f * u; val bw = minOf(18f * u, p.holgura); val bh = 24f * u
        drawRoundRect(v(roble, if (oscuro) 0.75f else 0.6f), Offset(bx, by - bh), Size(bw, bh), CornerRadius(4f * u))
        listOf(0.2f, 0.8f).forEach { f -> caja(v(t(HIERRO_P, oscuro), 0.75f), Rect(bx, by - bh * f - 1.2f * u, bx + bw, by - bh * f + 1.2f * u)) }
        drawPath(juntas(Rect(bx, by - bh, bx + bw, by), bw / 4f, true), v(brea, 0.5f), style = Stroke(0.6f * u))
        val rc = Offset(w - 14f * u, h - 7f * u)
        for (k in 0 until 4) drawOval(v(t(CABO, oscuro), if (oscuro) 0.7f else 0.75f), Offset(rc.x - (12f - k * 2.6f) * u, rc.y - (5f - k * 1.1f) * u - k * 1.6f * u), Size((24f - k * 5.2f) * u, (10f - k * 2.2f) * u), style = Stroke(1.8f * u))
        // La luz del mar que entra por la galería (de noche, los faroles que se juntan abajo).
        drawRect(Brush.verticalGradient(if (oscuro) listOf(Color.Transparent, luzFarol.copy(alpha = 0.09f)) else listOf(Color(0xFFE8F4F8).copy(alpha = 0.3f), Color.Transparent), p.friso, h), Offset(0f, p.friso), Size(w, h - p.friso))
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

    // ---------------------------------------------------------------- el salón del hall

    private val LAUREL = Color(0xFF5E7A3A)
    private val BRONCE_R = Color(0xFFA9864A)
    private val CRISTAL = Color(0xFFF6EBC8)

    /**
     * El panteón: la cúpula de casetones con su óculo que echa un haz de luz cenital, el friso dórico de triglifos con
     * escarapelas en las metopas, columnas dóricas estriadas entre las franjas y en los bordes, una hornacina con frontón
     * para cada trofeo, en cada columna la corona de laurel con su cinta tricolor, en los lados las banderas y los quinqués
     * de pared, y el piso de mármol en losas grandes. De noche el óculo deja ver el cielo y los quinqués doran la piedra.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val p = PlantaMedievo(w, h, u, e.columnas)
        fun v(c: Color, f: Float) = mezcla(e.fondo, c, f)
        val piedra = if (oscuro) Color(0xFF55504A) else PIEDRA
        val junta = if (oscuro) Color.Black else PIEDRA_JUNTA
        val luzQ = Color(0xFFFFCB70)
        val techo = p.friso * 1.3f
        drawRect(Brush.verticalGradient(listOf(v(piedra, if (oscuro) 0.5f else 0.42f), v(piedra, if (oscuro) 0.38f else 0.28f)), 0f, h))
        drawPath(hiladas(Rect(0f, techo, w, p.zocalo), 22f * u, 56f * u, e.semilla), v(junta, if (oscuro) 0.35f else 0.3f), style = Stroke(0.7f * u))

        // El haz de luz que baja del óculo.
        drawPath(Path().apply { moveTo(w / 2f - 14f * u, 0f); lineTo(w / 2f + 14f * u, 0f); lineTo(w / 2f + 70f * u, h); lineTo(w / 2f - 70f * u, h); close() }, (if (oscuro) Color(0xFFB8C6E8) else Color(0xFFFFF6DC)).copy(alpha = if (oscuro) 0.03f else 0.14f))

        // Las hornacinas con frontón: el fondo, el marco, la cornisa y el tímpano, y la repisa.
        for (y in p.filas) for (i in 0 until p.n) {
            val r = Rect(p.cx(i) - p.semi, y - 8f * u, p.cx(i) + p.semi, y + 142f * u)
            caja(v(n(MARMOL, oscuro), if (oscuro) 0.3f else 0.55f), r)
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.22f else 0.05f), Offset(r.left + 2f * u, r.top + 2f * u), Size(r.width - 4f * u, r.height - 2f * u), style = Stroke(4f * u))
            val g = minOf(5f * u, p.holgura * 0.3f).coerceAtLeast(2.5f * u)
            drawRect(v(piedra, if (oscuro) 0.85f else 0.7f), Offset(r.left - g / 2f, r.top - g / 2f), Size(r.width + g, r.height + g / 2f), style = Stroke(g))
            val cor = Rect(r.left - g * 2f, r.top - g * 2.2f, r.right + g * 2f, r.top - g * 0.6f)
            caja(v(piedra, if (oscuro) 0.9f else 0.75f), cor)
            val fr = minOf(22f * u, (y - 8f * u - g * 2.2f) - (y - 36f * u))
            val front = Path().apply { moveTo(cor.left, cor.top); lineTo(r.center.x, cor.top - fr); lineTo(cor.right, cor.top); close() }
            drawPath(front, v(piedra, if (oscuro) 0.6f else 0.5f))
            drawPath(front, v(piedra, if (oscuro) 0.9f else 0.75f), style = Stroke(g * 0.7f))
            val ec = Offset(r.center.x, cor.top - fr * 0.36f); val er = fr * 0.24f
            drawCircle(v(t(AZUL, oscuro), 0.6f), er, ec); drawCircle(v(n(BLANCO, oscuro), 0.7f), er * 0.66f, ec); drawCircle(v(t(ROJO, oscuro), 0.6f), er * 0.33f, ec)
            caja(v(piedra, if (oscuro) 0.85f else 0.7f), Rect(r.left - g - 3f * u, r.bottom - 1f * u, r.right + g + 3f * u, r.bottom + 4f * u))
            drawRect(Color.Black.copy(alpha = if (oscuro) 0.3f else 0.1f), Offset(r.left - g - 3f * u, r.bottom + 4f * u), Size(r.width + 2 * g + 6f * u, 2f * u))
        }

        // Las columnas dóricas estriadas, con su capitel y su basa; en las de en medio, la corona de laurel con la cinta.
        for (i in 0..p.n) {
            val borde = i == 0 || i == p.n
            val x = (i * p.paso).acotado(p.pilar * 0.3f, w - p.pilar * 0.3f); val cw = if (borde) p.pilar * 0.6f else minOf(p.pilar * 0.75f, p.entre * 0.55f)
            val r = Rect(x - cw / 2f, techo + 8f * u, x + cw / 2f, p.zocalo - 6f * u)
            caja(v(piedra, if (oscuro) 0.75f else 0.6f), r)
            drawPath(juntas(r, cw / 5f, true), v(junta, if (oscuro) 0.5f else 0.4f), style = Stroke(0.7f * u))
            drawRect(Color.White.copy(alpha = if (oscuro) 0.05f else 0.18f), Offset(r.left + cw * 0.2f, r.top), Size(cw * 0.15f, r.height))
            caja(v(piedra, if (oscuro) 0.85f else 0.7f), Rect(r.left - 3f * u, r.top - 8f * u, r.right + 3f * u, r.top - 4f * u))
            drawOval(v(piedra, if (oscuro) 0.85f else 0.7f), Offset(r.left - 1.5f * u, r.top - 5f * u), Size(cw + 3f * u, 6f * u))
            caja(v(piedra, if (oscuro) 0.85f else 0.7f), Rect(r.left - 3f * u, r.bottom, r.right + 3f * u, p.zocalo))
            if (!borde) for (y in p.filas) {
                val c = Offset(x, y + 34f * u); val rl = minOf(13f * u, p.entre * 0.45f).coerceAtLeast(5f * u)
                drawCircle(v(t(LAUREL, oscuro), if (oscuro) 0.6f else 0.6f), rl, c, style = Stroke(1.2f * u))
                val hojas = Path()
                for (k in 0 until 16) { val a = k * 2 * PI / 16 + 0.2; val q = Offset(c.x + rl * cos(a).toFloat(), c.y + rl * sin(a).toFloat())
                    hojas.addOval(Rect(q.x - 1.6f * u, q.y - 3f * u, q.x + 1.6f * u, q.y + 3f * u)) }
                drawPath(hojas, v(t(LAUREL, oscuro), if (oscuro) 0.65f else 0.6f))
                listOf(t(AZUL, oscuro), n(BLANCO, oscuro), t(ROJO, oscuro)).forEachIndexed { k, col ->
                    drawLine(v(col, if (oscuro) 0.75f else 0.75f), Offset(c.x - 2.4f * u + k * 2.4f * u, c.y + rl), Offset(c.x - 5f * u + k * 2.4f * u * 2f, c.y + rl + 16f * u), 2f * u) }
            }
        }

        // En los lados: las banderas tricolores colgadas y los quinqués de pared con su globo, alternando por fila.
        val xl = (p.holgura * 0.42f).coerceIn(6f * u, 18f * u)
        for ((f, y) in p.filas.withIndex()) listOf(xl, w - xl).forEach { x ->
            if (f % 2 == 0) {
                val bw = minOf(xl * 1.3f, 18f * u); val top = y + 2f * u; val bot = y + 100f * u
                listOf(t(AZUL, oscuro), n(BLANCO, oscuro), t(ROJO, oscuro)).forEachIndexed { k, col ->
                    val fx = x - bw / 2f + k * bw / 3f
                    drawPath(Path().apply { moveTo(fx, top); lineTo(fx + bw / 3f, top); lineTo(fx + bw / 3f, bot - (if (k == 1) 0f else 4f * u)); quadraticTo(fx + bw / 6f, bot + 2f * u, fx, bot - (if (k == 1) 4f else 0f) * u); close() }, v(col, if (oscuro) 0.6f else 0.55f))
                }
                drawLine(v(t(BRONCE_R, oscuro), 0.85f), Offset(x - bw / 2f - 3f * u, top), Offset(x + bw / 2f + 3f * u, top), 1.6f * u)
                drawCircle(v(t(BRONCE_R, oscuro), 0.85f), 2f * u, Offset(x, top - 3f * u))
            } else {
                val c = Offset(x, y + 40f * u); val rq = minOf(6f * u, xl * 0.6f)
                halo(c, 40f * u, luzQ.copy(alpha = if (oscuro) 0.05f else 0.018f))
                drawLine(v(t(BRONCE_R, oscuro), 0.85f), Offset(x, c.y + rq), Offset(x, c.y + 26f * u), 1.6f * u)
                drawPath(Path().apply { moveTo(x - 5f * u, c.y + 26f * u); lineTo(x + 5f * u, c.y + 26f * u); lineTo(x, c.y + 32f * u); close() }, v(t(BRONCE_R, oscuro), 0.85f))
                drawCircle(v(t(CRISTAL, oscuro), if (oscuro) 0.95f else 0.85f), rq, c)
                if (oscuro) drawCircle(luzQ.copy(alpha = 0.5f), rq * 0.7f, c)
                drawRect(v(t(BRONCE_R, oscuro), 0.85f), Offset(x - rq * 0.5f, c.y + rq * 0.7f), Size(rq, 3f * u))
                drawLine(v(t(HIERRO, oscuro), 0.5f), Offset(x, c.y - rq), Offset(x, c.y - rq - 5f * u), 1.2f * u)
            }
        }

        // La cúpula: casetones en anillos que se achican, con su roseta, y el óculo abierto al cielo.
        val cc = Offset(w / 2f, techo - 10f * u); val rx = w * 0.62f; val ry = techo * 1.05f
        caja(v(piedra, if (oscuro) 0.55f else 0.5f), Rect(0f, 0f, w, techo - 8f * u))
        val anillos = Path(); val radios = Path()
        for (k in 0 until 4) { val f = 1f - k * 0.22f; anillos.addOval(Rect(cc.x - rx * f, cc.y - ry * f, cc.x + rx * f, cc.y + ry * f)) }
        for (k in 0 until 24) { val a = PI + k * PI / 24; radios.moveTo(cc.x + rx * 0.34f * cos(a).toFloat(), cc.y + ry * 0.34f * sin(a).toFloat()); radios.lineTo(cc.x + rx * cos(a).toFloat(), cc.y + ry * sin(a).toFloat()) }
        clipPath(Path().apply { addRect(Rect(0f, 0f, w, techo - 8f * u)) }) {
            drawPath(anillos, v(junta, if (oscuro) 0.35f else 0.5f), style = Stroke(1.2f * u))
            drawPath(radios, v(junta, if (oscuro) 0.35f else 0.5f), style = Stroke(1f * u))
            for (k in 0 until 24) for (j in 0 until 3) { val a = PI + (k + 0.5) * PI / 24; val f = 0.89f - j * 0.22f
                drawCircle(v(t(BRONCE_R, oscuro), if (oscuro) 0.5f else 0.5f), 1.1f * u, Offset(cc.x + rx * f * cos(a).toFloat(), cc.y + ry * f * sin(a).toFloat())) }
            drawOval(if (oscuro) v(Color(0xFF1E2A50), 0.9f) else v(Color(0xFFCFE2F2), 0.8f), Offset(cc.x - rx * 0.12f, cc.y - ry * 0.2f - 6f * u), Size(rx * 0.24f, ry * 0.2f))
        }
        // El entablamento: arquitrabe y friso dórico de triglifos con escarapelas en las metopas.
        val fz = Rect(0f, techo - 8f * u, w, techo + 4f * u)
        caja(v(piedra, if (oscuro) 0.8f else 0.65f), fz)
        val tri = Path(); val esc = Path(); val pt = maxOf(16f * u, 1f); var xt = 4f * u; var kt = 0
        while (xt < w && kt < 60) { for (j in 0 until 3) tri.addRect(Rect(xt + j * 2f * u, fz.top + 1.5f * u, xt + j * 2f * u + 1.1f * u, fz.bottom - 1.5f * u)); esc.addOval(Rect(Offset(xt + pt / 2f + 3f * u, fz.center.y), 2.4f * u)); xt += pt; kt++ }
        drawPath(tri, v(junta, if (oscuro) 0.7f else 0.55f))
        drawPath(esc, v(t(AZUL, oscuro), if (oscuro) 0.6f else 0.5f))
        drawLine(v(junta, 0.6f), Offset(0f, fz.bottom), Offset(w, fz.bottom), 1f * u)

        // El zócalo y el piso de mármol en losas grandes, con su faja de mármol oscuro.
        caja(v(piedra, if (oscuro) 0.7f else 0.55f), Rect(0f, p.zocalo, w, p.piso))
        val suelo = Rect(0f, p.piso, w, h)
        caja(v(n(MARMOL, oscuro), if (oscuro) 0.45f else 0.6f), suelo)
        drawPath(pisoFuga(w, p.piso, h, 5, u), v(junta, if (oscuro) 0.6f else 0.5f), style = Stroke(0.8f * u))
        caja(v(t(TINTA, oscuro), if (oscuro) 0.5f else 0.3f), Rect(0f, p.piso + 2f * u, w, p.piso + 4f * u))
        // La luz: de día baja del óculo y llena el centro; de noche, la de los quinqués junta abajo.
        drawRect(Brush.radialGradient(listOf((if (oscuro) luzQ else Color.White).copy(alpha = if (oscuro) 0.12f else 0.22f), Color.Transparent), Offset(w / 2f, if (oscuro) h else 0f), maxOf(w * 0.8f, 1f)), Offset(0f, 0f), Size(w, h))
    }
}
