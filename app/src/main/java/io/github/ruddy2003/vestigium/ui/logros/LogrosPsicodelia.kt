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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.TrazosPsicodelia
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Psicodelia
// =====================================================================

/**
 * Psicodelia: los discos de oro del verano de 1967, en la sala de baile de San Francisco. Cada trofeo descansa en una caja de
 * fruta de madera puesta de cabeza (como las que servían de mueble en los departamentos compartidos), con su etiqueta de papel
 * pintada al frente y los flejes del metal del nivel en las esquinas; en el hall, sobre una pila de fundas de discos, y su
 * placa es la entrada del baile, con su talón. El salón es la sala de baile por dentro: el balcón de arriba con los
 * proyectores del show de luces, la pared donde respiran las luces líquidas, las columnas forradas de carteles entre los
 * trofeos y la pista de madera. De noche, la misma sala con la luz negra: lo fluorescente brilla sobre el negro violeta. En
 * la campaña corre la camioneta pintada y, al llegar a Monterey, espera el escenario del festival.
 */
object LogrosPsicodelia : DisenoLogros {
    private val T = TrazosPsicodelia
    /** La madera de pino de la caja de fruta. */
    private val PINO = Color(0xFFC79A62)
    private val PINO_OSCURO = Color(0xFF8A6238)
    /** La tinta del cartel: el violeta casi negro de la imprenta. */
    private val TINTA = Color(0xFF2A1C32)
    private val SALA = T.SALA
    /** El papel de la entrada y de las etiquetas. */
    private val PAPEL = Color(0xFFF6EEDD)

    /** El color de noche: la misma madera bajo la luz negra, hundida en el violeta de la sala. */
    private fun t(c: Color, oscuro: Boolean): Color = if (oscuro) mezcla(c, SALA, 0.55f) else c

    /** De noche, bajo la luz negra, cada color de cartel pasa a su tinta fluorescente. */
    private fun fl(c: Color, oscuro: Boolean): Color = if (!oscuro) c else when (c) {
        T.MAGENTA -> T.FLUOR_MAGENTA; T.VERDE -> T.LIMON; T.NARANJA -> T.FLUOR_NARANJA; T.VIOLETA -> T.FLUOR_VIOLETA
        T.AMARILLO -> Color(0xFFFFF07A); T.AZUL -> Color(0xFF7FA6FF)
        else -> c
    }

    /** El gris translúcido de lo que aún no se gana. */
    private fun silueta(oscuro: Boolean): Color = if (oscuro) Color(0xFFF2EAF7).copy(alpha = 0.13f) else TINTA.copy(alpha = 0.13f)

    private fun DrawScope.fr(x0: Float, y0: Float, x1: Float, y1: Float) = Rect(size.width * x0, size.height * y0, size.width * x1, size.height * y1)
    private fun DrawScope.fp(x: Float, y: Float) = Offset(size.width * x, size.height * y)

    /** La madera en volumen de cartel: la luz por la izquierda y una franja de sombra de borde duro a la derecha. */
    private fun madera(c: Color, x0: Float, x1: Float): Brush =
        Brush.horizontalGradient(0f to mezcla(c, Color.White, 0.18f), 0.3f to c, 0.8f to c, 0.805f to mezcla(c, TINTA, 0.22f), 1f to mezcla(c, TINTA, 0.3f), startX = x0, endX = x1)

    /** La veta del pino: líneas largas que ondulan, más oscuras, dentro de [r]. */
    private fun DrawScope.veta(r: Rect, c: Color, lw: Float, semilla: Int) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top + lw * 1.5f; var k = 0
            while (y < r.bottom && k < 30) {
                val v = Path().apply {
                    moveTo(r.left, y); var x = r.left; var j = 0
                    while (x < r.right && j < 40) { val paso = r.width * 0.16f; quadraticTo(x + paso / 2, y + (azar(semilla, k * 40 + j) - 0.5f) * lw * 2.4f, x + paso, y); x += paso; j++ }
                }
                drawPath(v, mezcla(c, TINTA, 0.3f).copy(alpha = 0.4f), style = Stroke(lw * 0.45f))
                y += lw * (2.6f + azar(semilla + 5, k) * 2f); k++
            }
        }
    }

    /**
     * La etiqueta de la caja de fruta (propia, sin marca): el cielo azul con el sol naranja que vibra contra él, las colinas
     * verdes y una flor magenta al frente. De noche, bajo la luz negra, la tinta fluorescente brilla.
     */
    private fun DrawScope.etiqueta(r: Rect, oscuro: Boolean, fino: Boolean) {
        val u = density
        if (oscuro) drawRect(Brush.radialGradient(listOf(T.FLUOR_MAGENTA.copy(alpha = 0.3f), Color.Transparent), r.center, r.width * 0.8f), Offset(r.left - r.width * 0.3f, r.top - r.height * 0.6f), Size(r.width * 1.6f, r.height * 2.2f))
        drawRect(fl(T.AZUL, oscuro), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val sol = Offset(r.left + r.width * 0.62f, r.top + r.height * 0.58f)
            drawCircle(fl(T.AMARILLO, oscuro), r.height * 0.5f, sol)
            drawCircle(fl(T.NARANJA, oscuro), r.height * 0.38f, sol)
            if (fino) for (k in 0 until 10) {
                val a = PI.toFloat() * (1f + k / 9f)
                drawLine(fl(T.AMARILLO, oscuro), Offset(sol.x + cos(a) * r.height * 0.56f, sol.y + sin(a) * r.height * 0.56f), Offset(sol.x + cos(a) * r.height * 0.8f, sol.y + sin(a) * r.height * 0.8f), maxOf(r.height * 0.05f, 0.4f * u), StrokeCap.Round)
            }
            drawPath(Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, r.top + r.height * 0.7f)
                quadraticTo(r.left + r.width * 0.25f, r.top + r.height * 0.5f, r.left + r.width * 0.5f, r.top + r.height * 0.72f)
                quadraticTo(r.left + r.width * 0.78f, r.top + r.height * 0.6f, r.right, r.top + r.height * 0.74f)
                lineTo(r.right, r.bottom); close()
            }, fl(T.VERDE, oscuro))
            with(T) { flor(Offset(r.left + r.width * 0.24f, r.top + r.height * 0.62f), r.height * 0.34f, fl(MAGENTA, oscuro), fl(VERDE, oscuro), 6, 0.5f) }
        }
        drawRect(fl(T.AMARILLO, oscuro), r.topLeft, r.size, style = Stroke(maxOf(r.height * 0.07f, 0.6f * u)))
        drawRect(TINTA.copy(alpha = 0.7f), r.topLeft, r.size, style = Stroke(maxOf(r.height * 0.025f, 0.4f * u)))
    }

    // ---------------------------------------------------------------- pedestales y placa

    /**
     * La caja de fruta de pino, puesta de cabeza: la tapa de tablas con su canto claro, el frente de dos tablas con la rendija
     * entre ellas, los postes de las esquinas más oscuros, la etiqueta de papel pegada al frente y los flejes del metal del
     * nivel en las dos puntas, con sus clavos. De noche, la madera hundida en el violeta y la etiqueta que brilla con la luz negra.
     */
    override fun DrawScope.pedestalTrofeo(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val fino = h > 70 * u
        val lw = maxOf(h * 0.0035f, 0.45f * u)
        val tapa = fr(0.05f, 0.765f, 0.95f, 0.79f)
        val frente = fr(0.07f, 0.79f, 0.93f, 0.95f)
        if (!e.hecho) { drawPath(Path().apply { addRect(tapa); addRect(frente) }, silueta(oscuro)); return }
        // La sombra en el piso.
        drawOval(TINTA.copy(alpha = if (oscuro) 0.6f else 0.22f), fp(0.03f, 0.938f), Size(w * 0.96f, h * 0.03f))
        val pino = t(PINO, oscuro)
        // El frente: dos tablas y la rendija oscura entre ellas.
        drawRect(t(TINTA, oscuro).copy(alpha = 0.85f), frente.topLeft, frente.size)
        listOf(0.795f to 0.862f, 0.874f to 0.945f).forEachIndexed { i, (y0, y1) ->
            val r = fr(0.07f, y0, 0.93f, y1)
            drawRect(madera(pino, r.left, r.right), r.topLeft, r.size)
            if (fino) veta(r, pino, lw, e.semilla + i * 7)
            drawLine(Color.White.copy(alpha = if (oscuro) 0.08f else 0.3f), Offset(r.left, r.top + lw * 0.4f), Offset(r.right, r.top + lw * 0.4f), lw * 0.6f)
        }
        // Los postes de las esquinas.
        listOf(0.07f to 0.13f, 0.87f to 0.93f).forEach { (x0, x1) ->
            val r = fr(x0, 0.79f, x1, 0.95f)
            drawRect(madera(t(PINO_OSCURO, oscuro), r.left, r.right), r.topLeft, r.size)
        }
        // La sombra que la tapa echa sobre el frente.
        drawRect(Brush.verticalGradient(listOf(TINTA.copy(alpha = 0.35f), Color.Transparent), h * 0.79f, h * 0.815f), fp(0.07f, 0.79f), Size(w * 0.86f, h * 0.025f))
        // La etiqueta de papel.
        etiqueta(fr(0.33f, 0.808f, 0.67f, 0.928f), oscuro, fino)
        // Los flejes del metal del nivel, con sus clavos.
        listOf(0.155f, 0.845f).forEach { x ->
            val r = fr(x - 0.022f, 0.79f, x + 0.022f, 0.95f)
            drawRect(Brush.horizontalGradient(listOf(e.metalOscuro, e.base, e.brillo, e.base, e.metalOscuro), r.left, r.right), r.topLeft, r.size)
            listOf(0.83f, 0.91f).forEach { y -> drawCircle(e.metalOscuro, maxOf(h * 0.006f, 0.5f * u), fp(x, y)); drawCircle(e.brillo, maxOf(h * 0.0025f, 0.3f * u), fp(x - 0.003f, y - 0.002f)) }
            if (e.nivel >= 4 && fino) drawLine(Color.White.copy(alpha = 0.8f), Offset(r.left + r.width * 0.3f, r.top + h * 0.01f), Offset(r.left + r.width * 0.3f, r.bottom - h * 0.01f), lw * 0.6f)
        }
        // La tapa: las tablas vistas de canto, con la luz arriba.
        drawRect(Brush.verticalGradient(listOf(mezcla(pino, Color.White, if (oscuro) 0.08f else 0.32f), pino, mezcla(pino, TINTA, 0.25f)), tapa.top, tapa.bottom), tapa.topLeft, tapa.size)
        if (fino) listOf(0.35f, 0.65f).forEach { x -> drawLine(mezcla(pino, TINTA, 0.4f), fp(x, 0.767f), fp(x, 0.79f), lw * 0.5f) }
        drawRect(TINTA.copy(alpha = 0.55f), tapa.topLeft, tapa.size, style = Stroke(lw * 0.5f))
        // De noche, el borde violeta de la luz negra sobre la tapa.
        if (oscuro) drawLine(T.FLUOR_VIOLETA.copy(alpha = 0.45f), Offset(tapa.left, tapa.top), Offset(tapa.right, tapa.top), lw * 0.8f)
    }

    /** La pila de fundas de discos en el hall: tres fundas acostadas, corridas, de colores que vibran; la de en medio, del metal del nivel. */
    override fun DrawScope.pedestalHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val capas = listOf(Triple(0.02f, 0.0f, T.MAGENTA), Triple(0.06f, 0.34f, T.NARANJA), Triple(0.0f, 0.66f, T.VERDE))
        if (!e.hecho) { drawPath(Path().apply { capas.forEach { (dx, y, _) -> addRect(Rect(w * dx, h * y, w * (0.98f - dx * 0.5f), h * (y + 0.34f))) } }, silueta(oscuro)); return }
        capas.forEachIndexed { i, (dx, y, col) ->
            val r = Rect(w * dx, h * y, w * (0.98f - dx * 0.5f), h * (y + 0.32f))
            if (i == 1) drawRect(Brush.horizontalGradient(listOf(e.metalOscuro, e.base, e.brillo, e.base, e.metalOscuro), r.left, r.right), r.topLeft, r.size)
            else {
                drawRect(fl(col, oscuro), r.topLeft, r.size)
                drawRect(fl(T.complementario(col), oscuro), Offset(r.left + w * 0.08f, r.top), Size(w * 0.05f, r.height))
            }
            drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = if (oscuro) 0.1f else 0.3f), Color.Transparent, TINTA.copy(alpha = 0.25f)), r.top, r.bottom), r.topLeft, r.size)
            drawRect(TINTA.copy(alpha = 0.6f), r.topLeft, r.size, style = Stroke(0.5f * u))
        }
        if (oscuro) drawRect(Brush.verticalGradient(listOf(T.FLUOR_VIOLETA.copy(alpha = 0.25f), Color.Transparent), 0f, h * 0.5f), Offset.Zero, Size(w, h * 0.5f))
    }

    override val margenPlaca: Dp = 10.dp
    override fun tintaPlaca(oscuro: Boolean): Color = TINTA
    override fun rotuloPlaca(oscuro: Boolean): Color = if (oscuro) Color(0xFF8A1F5C) else Color(0xFFB0246A)

    /**
     * La entrada del baile: el papel con las esquinas redondas, la franja de arcoíris de tinta partida arriba, el talón de la
     * izquierda separado por la línea de perforaciones con sus dos muescas, y el borde del metal del nivel. De noche, el papel
     * brilla un poco con la luz negra.
     */
    override fun DrawScope.placaHall(e: EstadoPedestal, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = density
        val m = minOf(2f * u, h * 0.06f); val rad = minOf(h * 0.18f, 6f * u)
        val r = Rect(m, m, w - m, h - m)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(rad))) }
        if (!e.hecho) { drawPath(forma, silueta(oscuro)); return }
        if (oscuro) drawRect(Brush.radialGradient(listOf(T.FLUOR_VIOLETA.copy(alpha = 0.35f), Color.Transparent), r.center, w * 0.6f), Offset.Zero, size)
        drawPath(Path().apply { addPath(forma, Offset(0.8f * u, 1.2f * u)) }, TINTA.copy(alpha = if (oscuro) 0.5f else 0.2f))
        drawPath(forma, Brush.verticalGradient(listOf(Color.White, if (oscuro) Color(0xFFEDE6F5) else PAPEL), r.top, r.bottom))
        clipPath(forma) {
            // La franja de arcoíris del tintero partido.
            drawRect(Brush.horizontalGradient(T.ARCOIRIS.map { fl(it, oscuro) }, r.left, r.right), r.topLeft, Size(r.width, minOf(h * 0.12f, 4f * u)))
            // El talón y su perforación.
            val xt = r.left + minOf(w * 0.14f, 16f * u)
            drawRect(fl(T.AMARILLO, oscuro).copy(alpha = 0.35f), Offset(r.left, r.top), Size(xt - r.left, r.height))
            drawLine(TINTA.copy(alpha = 0.5f), Offset(xt, r.top + h * 0.12f), Offset(xt, r.bottom), 0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.6f * u)))
            with(T) { flor(Offset((r.left + xt) / 2, r.center.y + h * 0.04f), minOf((xt - r.left) * 0.38f, h * 0.22f), fl(MAGENTA, oscuro), fl(VERDE, oscuro), 6) }
        }
        // Las dos muescas del talón.
        listOf(r.top, r.bottom).forEach { y -> drawArc(TINTA.copy(alpha = 0.5f), if (y == r.top) 0f else 180f, 180f, false, Offset(r.left + minOf(w * 0.14f, 16f * u) - 2f * u, y - 2f * u), Size(4f * u, 4f * u), style = Stroke(0.6f * u)) }
        drawPath(forma, Brush.linearGradient(listOf(e.brillo, e.base, e.metalOscuro, e.base), Offset.Zero, Offset(w, h)), style = Stroke(1.4f * u))
        drawPath(Path().apply { addRoundRect(RoundRect(Rect(r.left + 2.6f * u, r.top + 2.6f * u, r.right - 2.6f * u, r.bottom - 2.6f * u), CornerRadius(maxOf(rad - 2.6f * u, 1f)))) },
            e.base.copy(alpha = 0.55f), style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- el salón del hall

    /** La geometría de la sala: una columna de carteles en el borde de cada columna de trofeos, el balcón arriba y la pista abajo. */
    private class GeoSala(val w: Float, val h: Float, val u: Float, columnas: Int) {
        val n = columnas.coerceIn(1, 6)
        val paso = w / n
        val columna = minOf(paso * 0.08f, 12f * u)
        val balcon = minOf(46f * u, h * 0.11f)
        val pista = minOf(26f * u, h * 0.08f)
        val suelo = h - pista
        val modulo = maxOf(230f * u, 1f)
        fun borde(i: Int) = i * paso
    }

    /**
     * La sala de baile: la pared del fondo donde respiran las luces líquidas (gotas de aceite y tinta proyectadas), las
     * columnas forradas de carteles entre las columnas de trofeos (sin pasar nunca por detrás de uno), el balcón de arriba
     * con su barandal de balaustres torneados y los retroproyectores del show de luces, y abajo la pista de tablas con el
     * reflejo de las luces. De día, la sala con sus luces de casa encendidas y el show apenas; de noche, la luz negra: las
     * luces líquidas, los carteles fluorescentes y el tubo violeta del balcón.
     */
    override fun DrawScope.escenaSalon(e: EstadoSalon, oscuro: Boolean) {
        val g = GeoSala(size.width, size.height, density, e.columnas)
        clipRect(0f, 0f, size.width, size.height) { escenaSala(g, e, oscuro) }
    }

    private fun DrawScope.escenaSala(g: GeoSala, e: EstadoSalon, oscuro: Boolean) {
        val w = g.w; val h = g.h; val u = g.u
        fun m(c: Color, k: Float) = mezcla(e.fondo, c, k)
        // La pared del fondo y las luces líquidas: tenues de día, vivas de noche.
        if (oscuro) drawRect(Brush.verticalGradient(listOf(m(SALA, 0.6f), m(Color(0xFF241536), 0.5f), m(SALA, 0.6f)), 0f, h), Offset.Zero, size)
        else drawRect(m(Color(0xFFF1D9C4), 0.4f), Offset.Zero, size)
        val alto = minOf(h - g.balcon - g.pista, 520f * u)
        var y0 = g.balcon; var k = 0
        while (y0 < g.suelo && k < 12) {
            val colores = if (oscuro) listOf(T.FLUOR_MAGENTA, T.FLUOR_NARANJA, T.LIMON, T.FLUOR_VIOLETA) else listOf(T.MAGENTA, T.NARANJA, T.VERDE, T.AMARILLO)
            with(T) { lucesLiquidas(Size(w, alto), (0.13f + k * 0.37f) % 1f, if (k % 2 == 0) colores else colores.reversed(), if (oscuro) 0.42f else 0.16f, Offset(0f, y0)) }
            y0 += alto * 0.8f; k++
        }
        // Las burbujas que flotan en la proyección.
        repeat(18) { i ->
            val o = Offset(azar(e.semilla, 300 + i) * w, g.balcon + azar(e.semilla, 340 + i) * (g.suelo - g.balcon))
            val r = (3f + azar(e.semilla, 380 + i) * 9f) * u
            drawPath(T.gota(o, r, azar(e.semilla, 420 + i), 0.1f), (if (oscuro) T.FLUOR_VIOLETA else Color.White).copy(alpha = if (oscuro) 0.18f else 0.22f), style = Stroke(0.8f * u))
        }
        // Las columnas forradas de carteles, en el borde de cada columna de trofeos.
        val pegados = listOf(T.NARANJA, T.MAGENTA, T.AZUL, T.VERDE, T.AMARILLO, T.VIOLETA)
        for (i in 0..g.n) {
            val cx = g.borde(i)
            val r = Rect(cx - g.columna, g.balcon, cx + g.columna, g.suelo)
            drawRect(Brush.horizontalGradient(listOf(m(Color(0xFF8C7A64), if (oscuro) 0.3f else 0.3f), m(Color(0xFF6E5E4C), if (oscuro) 0.4f else 0.4f)), r.left, r.right), r.topLeft, r.size)
            var yc = g.balcon + 8f * u; var kc = 0
            while (yc < g.suelo - 20f * u && kc < 40) {
                val alto2 = (26f + azar(e.semilla + i, kc) * 16f) * u
                val col = pegados[(i * 2 + kc) % pegados.size]
                val car = Rect(r.left + 1.2f * u, yc, r.right - 1.2f * u, minOf(yc + alto2, g.suelo - 6f * u))
                val tinte = if (oscuro) fl(col, true) else col
                if (oscuro) drawRect(Brush.radialGradient(listOf(tinte.copy(alpha = 0.14f), Color.Transparent), car.center, car.height), Offset(car.left - car.width, car.top - car.height * 0.3f), Size(car.width * 3, car.height * 1.6f))
                drawRect(m(tinte, if (oscuro) 0.42f else 0.26f), car.topLeft, car.size)
                // En cada cartel, un círculo de su complementario y una banda de letras.
                drawCircle(m(fl(T.complementario(col), oscuro), if (oscuro) 0.4f else 0.24f), car.width * 0.3f, Offset(car.center.x, car.top + car.height * 0.58f))
                drawRect(m(TINTA, if (oscuro) 0.2f else 0.16f), Offset(car.left + car.width * 0.12f, car.top + car.height * 0.12f), Size(car.width * 0.76f, car.height * 0.14f))
                yc += alto2 + 4f * u; kc++
            }
        }
        // El balcón: el piso del balcón, el barandal de balaustres torneados y los retroproyectores del show.
        val frente = Rect(0f, 0f, w, g.balcon)
        drawRect(if (oscuro) m(Color(0xFF0C0614), 0.85f) else m(Color(0xFF6E4A2E), 0.32f), frente.topLeft, frente.size)
        val bar = g.balcon * 0.18f
        val y1 = g.balcon - bar * 1.6f
        var xb = 4f * u; var kb = 0
        while (xb < w && kb < 120) {
            val b = Path().apply {
                moveTo(xb - 1.6f * u, bar * 1.2f); lineTo(xb + 1.6f * u, bar * 1.2f)
                quadraticTo(xb + 4f * u, (bar * 1.2f + y1) / 2, xb + 1.6f * u, y1); lineTo(xb - 1.6f * u, y1)
                quadraticTo(xb - 4f * u, (bar * 1.2f + y1) / 2, xb - 1.6f * u, bar * 1.2f); close()
            }
            drawPath(b, if (oscuro) m(Color(0xFF3A2650), 0.7f) else m(Color(0xFF9A6E44), 0.45f))
            xb += 11f * u; kb++
        }
        drawRect(if (oscuro) m(Color(0xFF2A1A3C), 0.8f) else m(Color(0xFF8A5E3A), 0.55f), Offset(0f, 0f), Size(w, bar * 1.2f))
        drawRect(if (oscuro) m(Color(0xFF2A1A3C), 0.8f) else m(Color(0xFF8A5E3A), 0.55f), Offset(0f, y1), Size(w, g.balcon - y1))
        // Los retroproyectores: uno por columna de trofeos, con su haz que baja a la pared.
        for (i in 0 until g.n) {
            val cx = g.borde(i) + g.paso / 2
            val pr = Offset(cx, bar * 1.2f)
            val haz = Path().apply { moveTo(pr.x - 4f * u, pr.y + 3f * u); lineTo(pr.x + 4f * u, pr.y + 3f * u); lineTo(pr.x + g.paso * 0.4f, g.balcon + 120f * u); lineTo(pr.x - g.paso * 0.4f, g.balcon + 120f * u); close() }
            drawPath(haz, Brush.verticalGradient(listOf((if (oscuro) T.FLUOR_VIOLETA else Color.White).copy(alpha = if (oscuro) 0.16f else 0.18f), Color.Transparent), pr.y, g.balcon + 120f * u))
            drawRect(if (oscuro) m(Color(0xFF4A3A60), 0.8f) else m(Color(0xFF4A4048), 0.75f), Offset(pr.x - 7f * u, pr.y - 6f * u), Size(14f * u, 6f * u))
            drawLine(if (oscuro) m(Color(0xFF4A3A60), 0.8f) else m(Color(0xFF4A4048), 0.75f), Offset(pr.x + 5f * u, pr.y - 6f * u), Offset(pr.x + 5f * u, pr.y - 14f * u), 1.2f * u)
            drawRect(if (oscuro) m(Color(0xFF4A3A60), 0.8f) else m(Color(0xFF4A4048), 0.75f), Offset(pr.x - 1f * u, pr.y - 16f * u), Size(9f * u, 3f * u))
            // El plato de vidrio con las tintas, que brilla.
            val plato = Offset(pr.x - 2f * u, pr.y - 7f * u)
            drawOval(Brush.radialGradient(listOf(T.FLUOR_MAGENTA.copy(alpha = if (oscuro) 0.9f else 0.6f), T.FLUOR_NARANJA.copy(alpha = 0.5f), Color.Transparent), plato, 7f * u), Offset(plato.x - 6f * u, plato.y - 2f * u), Size(12f * u, 4f * u))
        }
        // El tubo de luz negra bajo el barandal, de noche.
        if (oscuro) {
            drawRect(Brush.verticalGradient(listOf(T.FLUOR_VIOLETA.copy(alpha = 0.4f), Color.Transparent), g.balcon, g.balcon + 30f * u), Offset(0f, g.balcon), Size(w, 30f * u))
            drawLine(T.FLUOR_VIOLETA.copy(alpha = 0.9f), Offset(0f, g.balcon - 1.5f * u), Offset(w, g.balcon - 1.5f * u), 1.6f * u)
        } else drawRect(Brush.verticalGradient(listOf(TINTA.copy(alpha = 0.18f), Color.Transparent), g.balcon, g.balcon + 14f * u), Offset(0f, g.balcon), Size(w, 14f * u))
        // La pista de tablas, con el reflejo de las luces.
        drawRect(if (oscuro) m(Color(0xFF2A1A20), 0.7f) else m(Color(0xFFB98A58), 0.55f), Offset(0f, g.suelo), Size(w, g.pista))
        drawLine(m(TINTA, 0.4f), Offset(0f, g.suelo), Offset(w, g.suelo), 1f * u)
        var xt = 0f; var kt = 0
        while (xt < w && kt < 200) { drawLine(m(TINTA, if (oscuro) 0.25f else 0.2f), Offset(xt, g.suelo), Offset(xt - (xt - w / 2) * 0.3f, h), 0.6f * u); xt += 18f * u; kt++ }
        listOf(T.FLUOR_MAGENTA, T.LIMON, T.FLUOR_NARANJA).forEachIndexed { i, col ->
            val o = Offset(w * (0.2f + i * 0.3f), g.suelo + g.pista * 0.5f)
            drawOval(Brush.radialGradient(listOf(col.copy(alpha = if (oscuro) 0.3f else 0.12f), Color.Transparent), o, 40f * u), Offset(o.x - 40f * u, o.y - g.pista * 0.4f), Size(80f * u, g.pista * 0.8f))
        }
    }

    // ---------------------------------------------------------------- las campañas

    /**
     * La camioneta de reparto pintada, de perfil y con el morro a la derecha (va hacia Monterey): la caja de dos tonos con la
     * ola de arcoíris y una flor, la ventanilla, la maleta en el techo y las ruedas; cuando hoy falta cumplir, da saltitos. De
     * noche, con el faro encendido y la pintura que brilla.
     */
    override fun DrawScope.marcadorCampana(e: EstadoMarcador, oscuro: Boolean) {
        val u = density
        val s = e.salto.coerceIn(0f, 1f)
        val p = e.punto
        val f = 1f - 0.35f * s
        drawOval(TINTA.copy(alpha = (if (oscuro) 0.6f else 0.25f) * f), Offset(p.x - 12 * u * f, p.y - 1.6f * u * f), Size(24 * u * f, 3.2f * u * f))
        val y0 = p.y - 2.6f * u - s * 4f * u
        fun q(x: Float, y: Float) = Offset(p.x + x * u, y0 + y * u)
        val caja = Path().apply {
            moveTo(q(-11f, -2f).x, q(-11f, -2f).y); lineTo(q(-11f, -13f).x, q(-11f, -13f).y)
            quadraticTo(q(-11f, -15f).x, q(-11f, -15f).y, q(-8.5f, -15f).x, q(-8.5f, -15f).y)
            lineTo(q(4.5f, -15f).x, q(4.5f, -15f).y); lineTo(q(8f, -10f).x, q(8f, -10f).y)
            lineTo(q(10.5f, -9.5f).x, q(10.5f, -9.5f).y); quadraticTo(q(12f, -9f).x, q(12f, -9f).y, q(12f, -6.5f).x, q(12f, -6.5f).y)
            lineTo(q(12f, -2f).x, q(12f, -2f).y); close()
        }
        // La maleta en el techo.
        drawRoundRect(fl(T.NARANJA, oscuro), q(-8f, -18f), Size(8f * u, 3f * u), CornerRadius(0.8f * u))
        drawRect(TINTA.copy(alpha = 0.6f), q(-8f, -18f), Size(8f * u, 3f * u), style = Stroke(0.4f * u))
        // La caja: blanca arriba y turquesa abajo.
        drawPath(caja, if (oscuro) Color(0xFFE6DDF0) else T.CASA_BLANCA)
        clipPath(caja) {
            drawRect(if (oscuro) Color(0xFF5FF0E0) else T.CASA_TURQUESA, q(-12f, -8f), Size(25f * u, 7f * u))
            T.ARCOIRIS.forEachIndexed { i, c -> drawLine(fl(c, oscuro), q(-11f, -12.5f + i * 0.7f), q(5f, -10.5f + i * 0.7f), 0.7f * u) }
        }
        // La ventanilla de la cabina y una flor.
        drawPath(Path().apply { moveTo(q(4.6f, -14f).x, q(4.6f, -14f).y); lineTo(q(7.6f, -10f).x, q(7.6f, -10f).y); lineTo(q(4.6f, -10f).x, q(4.6f, -10f).y); close() },
            if (oscuro) Color(0xFF2B2140) else Color(0xFFBFDDEA))
        with(T) { flor(q(-5f, -5f), 2.6f * u, fl(MAGENTA, oscuro), fl(VERDE, oscuro), 5) }
        drawPath(caja, TINTA.copy(alpha = 0.75f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
        // El faro.
        val faro = q(11.6f, -6.5f)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF2B8).copy(alpha = 0.6f), Color.Transparent), faro, 8 * u), 8 * u, faro)
        drawCircle(Color(0xFFFFF0B0), 0.9f * u, faro)
        // Las ruedas.
        listOf(-6.5f, 7f).forEach { x -> drawCircle(T.VINILO, 2.4f * u, q(x, -1.6f)); drawCircle(Color(0xFFC8CCD2), 0.9f * u, q(x, -1.6f)) }
    }

    /**
     * La llegada a Monterey: el escenario del festival en chico, con su arco de luces y una gran flor que vibra, las dos
     * pilas de altavoces a los lados, el arcoíris arriba y un tapete de flores al pie. De noche, con el resplandor de las luces.
     */
    override fun DrawScope.victoriaCampana(e: EstadoVictoria, oscuro: Boolean) {
        val u = density
        val ancho = 44f * u; val alto = 46f * u; val mg = 2f * u
        val x = if (size.width > ancho + 2 * mg) e.meta.x.coerceIn(ancho / 2 + mg, size.width - ancho / 2 - mg) else size.width / 2
        val y = if (size.height > alto + 2 * mg) (e.meta.y - 2f * u).coerceIn(alto + mg, size.height - mg) else size.height - mg
        drawOval(TINTA.copy(alpha = if (oscuro) 0.55f else 0.2f), Offset(x - 20 * u, y - 1.6f * u), Size(40 * u, 3.2f * u))
        // El arcoíris arriba del escenario.
        val arco = Offset(x, y - 22f * u)
        if (oscuro) drawCircle(Brush.radialGradient(listOf(T.FLUOR_MAGENTA.copy(alpha = 0.35f), Color.Transparent), arco, 30 * u), 30 * u, arco)
        T.ARCOIRIS.forEachIndexed { i, c ->
            val r = (21f - i * 1.6f) * u
            drawArc(fl(c, oscuro), 180f, 180f, false, Offset(arco.x - r, arco.y - r), Size(r * 2, r * 2), style = Stroke(1.6f * u))
        }
        // La tarima y el fondo del escenario.
        drawRect(if (oscuro) Color(0xFF2A1A3C) else Color(0xFF6E4A2E), Offset(x - 16 * u, y - 6 * u), Size(32 * u, 6 * u))
        drawLine(Color.White.copy(alpha = if (oscuro) 0.2f else 0.4f), Offset(x - 16 * u, y - 6 * u), Offset(x + 16 * u, y - 6 * u), 0.7f * u)
        drawRect(if (oscuro) Color(0xFF1A1126) else Color(0xFFF6EEDD), Offset(x - 13 * u, y - 22 * u), Size(26 * u, 16 * u))
        // La gran flor que vibra, al fondo del escenario.
        with(T) { flor(Offset(x, y - 14 * u), 7f * u, fl(MAGENTA, oscuro), fl(VERDE, oscuro), 7, 0.8f) }
        drawRect(TINTA.copy(alpha = 0.6f), Offset(x - 13 * u, y - 22 * u), Size(26 * u, 16 * u), style = Stroke(0.6f * u))
        // Las dos pilas de altavoces.
        listOf(-1f, 1f).forEach { s ->
            val x0 = x + s * 18f * u - 4f * u
            for (k in 0 until 3) {
                val r = Rect(x0, y - (6f + k * 6f) * u, x0 + 8f * u, y - k * 6f * u)
                drawRect(if (oscuro) Color(0xFF2A2036) else Color(0xFF2E2430), r.topLeft, r.size)
                drawCircle(if (oscuro) Color(0xFF5A4A70) else Color(0xFF5E5262), 2.2f * u, r.center)
                drawRect(TINTA, r.topLeft, r.size, style = Stroke(0.4f * u))
            }
        }
        // El tapete de flores al pie.
        listOf(-12f, -6f, 0f, 6f, 12f).forEachIndexed { i, dx ->
            val c = listOf(T.MAGENTA, T.NARANJA, T.AMARILLO, T.VERDE, T.AZUL)[i]
            with(T) { flor(Offset(x + dx * u, y - 0.5f * u), 2.2f * u, fl(c, oscuro), fl(complementario(c), oscuro), 5) }
        }
    }

    // ---------------------------------------------------------------- objetos de Tu año

    /**
     * Los objetos de Tu año de Psicodelia: la luna, la creciente sobre la niebla que entra por el puente (una línea que ondula);
     * la huella, la suela de una sandalia de cuero con su tira entre los dedos; la pesa, la mancuerna con los discos en forma de
     * flor.
     */
    override fun DrawScope.objetoAnio(e: EstadoObjeto, oscuro: Boolean) {
        val r = e.radio; val c = e.centro; val u = density
        val g = maxOf(r * 0.1f, 0.8f * u)
        when (e.forma) {
            "luna" -> {
                val luna = Path().apply {
                    addOval(Rect(Offset(c.x - r * 0.05f, c.y - r * 0.2f), r * 0.7f))
                }
                clipPath(Path().apply { addOval(Rect(Offset(c.x + r * 0.3f, c.y - r * 0.42f), r * 0.6f)) }, androidx.compose.ui.graphics.ClipOp.Difference) { drawPath(luna, e.color) }
                // La niebla: dos líneas blandas que ondulan bajo la luna.
                listOf(0.62f to 1f, 0.86f to 0.7f).forEach { (dy, l) ->
                    drawPath(Path().apply {
                        moveTo(c.x - r * l, c.y + r * dy)
                        quadraticTo(c.x - r * l * 0.5f, c.y + r * (dy - 0.18f), c.x, c.y + r * dy)
                        quadraticTo(c.x + r * l * 0.5f, c.y + r * (dy + 0.18f), c.x + r * l, c.y + r * dy)
                    }, e.color, style = Stroke(g * 1.1f, cap = StrokeCap.Round))
                }
            }
            "huella" -> {
                // La suela de la sandalia, un poco ladeada, con la tira entre los dedos.
                val suela = Path().apply {
                    moveTo(c.x - r * 0.05f, c.y - r * 0.98f)
                    cubicTo(c.x + r * 0.35f, c.y - r * 0.98f, c.x + r * 0.45f, c.y - r * 0.5f, c.x + r * 0.38f, c.y - r * 0.15f)
                    cubicTo(c.x + r * 0.32f, c.y + r * 0.2f, c.x + r * 0.36f, c.y + r * 0.5f, c.x + r * 0.3f, c.y + r * 0.78f)
                    cubicTo(c.x + r * 0.22f, c.y + r * 1.02f, c.x - r * 0.22f, c.y + r * 1.02f, c.x - r * 0.28f, c.y + r * 0.78f)
                    cubicTo(c.x - r * 0.34f, c.y + r * 0.5f, c.x - r * 0.24f, c.y + r * 0.2f, c.x - r * 0.32f, c.y - r * 0.15f)
                    cubicTo(c.x - r * 0.42f, c.y - r * 0.55f, c.x - r * 0.42f, c.y - r * 0.98f, c.x - r * 0.05f, c.y - r * 0.98f); close()
                }
                drawPath(suela, e.color)
                if (r >= 6f * u) {
                    // La tira: del hueco entre los dedos a los dos lados.
                    val dedo = Offset(c.x - r * 0.02f, c.y - r * 0.68f)
                    drawLine(e.fondo, dedo, Offset(c.x - r * 0.33f, c.y - r * 0.12f), g * 0.9f, StrokeCap.Round)
                    drawLine(e.fondo, dedo, Offset(c.x + r * 0.38f, c.y - r * 0.12f), g * 0.9f, StrokeCap.Round)
                    drawCircle(e.fondo, g * 0.9f, dedo)
                }
            }
            "pesa" -> {
                // La mancuerna: el mango y los dos discos con el canto en forma de flor.
                drawLine(e.color, Offset(c.x - r * 0.62f, c.y), Offset(c.x + r * 0.62f, c.y), r * 0.18f, StrokeCap.Round)
                listOf(-1f, 1f).forEach { s ->
                    val o = Offset(c.x + s * r * 0.6f, c.y)
                    for (k in 0 until 6) {
                        val a = k * PI.toFloat() / 3
                        drawCircle(e.color, r * 0.17f, Offset(o.x + cos(a) * r * 0.2f, o.y + sin(a) * r * 0.24f))
                    }
                    drawCircle(e.color, r * 0.24f, o)
                    if (r >= 6f * u) drawCircle(e.fondo, r * 0.08f, o)
                }
            }
            else -> {}
        }
    }
}
