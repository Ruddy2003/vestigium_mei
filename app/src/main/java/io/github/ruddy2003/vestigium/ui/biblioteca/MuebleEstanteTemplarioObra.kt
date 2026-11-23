package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * El armario-estante de cedro de Templario ([MuebleEstanteTemplario]): la cornisa volada (el tablero de arriba, el cimacio, el
 * taqueado de dos hileras al tresbolillo y el friso tallado a bisel en dientes de sierra), los casilleros con su fondo de tablas
 * pintadas de verde tierra (la sombra franca de la tabla de arriba, el canto de dentro de los costados, el piso), las tablas
 * gruesas con su bocel y sus escuadras de hierro, los costados con su baquetón y sus flejes, las cédulas de los meses y el
 * zócalo de arquillos ciegos. De día, la luz viene del ajimez (izquierda); de noche, cada tramo se apaga con la distancia a la
 * lumbre que le toca.
 */
internal object ObraEstanteTemplario {
    private val P = PaletaTemplario

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireTemplario, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.035f)).toInt().coerceIn(2, 40) else 1

    // ---------------------------------------------------------------- la cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.cornisa(r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        cornisaCon(r, a, cerca, null)
        if (m != null) clipPath(m.forma(size.width, ManchaTemplario.FRENTE)) { cornisaCon(r, a, cerca, m) }
    }

    private fun DrawScope.cornisaCon(r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, k: Float) = a.en(col, f, k, lit, m)
        val y0 = r.top
        val sup0 = y0 - w * 0.008f; val sup1 = y0 + w * 0.009f
        val canto1 = y0 + w * 0.025f
        val cima1 = y0 + w * 0.037f
        val taco1 = y0 + w * 0.065f
        val filete1 = y0 + w * 0.071f
        val friso1 = y0 + w * 0.097f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y0 + w * 0.04f))
            // El tablero de arriba, visto desde encima (más claro y gastado), y su canto.
            drawRect(c(P.CEDRO_LUZ, -0.08f, k), Offset(x0, sup0), Size(x1 - x0, sup1 - sup0))
            drawRect(c(P.CEDRO, 0.06f, k), Offset(x0, sup1), Size(x1 - x0, canto1 - sup1))
            // El cimacio: una media caña que mira abajo (oscura arriba, clara abajo).
            drawRect(c(P.CEDRO_OSC, 0f, k), Offset(x0, canto1), Size(x1 - x0, (cima1 - canto1) * 0.5f))
            drawRect(c(P.CEDRO, -0.05f, k), Offset(x0, canto1 + (cima1 - canto1) * 0.5f), Size(x1 - x0, (cima1 - canto1) * 0.5f))
            // El fondo del taqueado, hondo.
            drawRect(c(P.CEDRO_HONDO, 0f, k), Offset(x0, cima1), Size(x1 - x0, taco1 - cima1))
            drawRect(c(P.CEDRO_LUZ, 0f, k), Offset(x0, taco1), Size(x1 - x0, filete1 - taco1))
            // El friso.
            drawRect(c(P.CEDRO, 0f, k), Offset(x0, filete1), Size(x1 - x0, friso1 - filete1))
            drawRect(c(P.CEDRO_OSC, 0f, k), Offset(x0, friso1), Size(x1 - x0, r.bottom - friso1))
        }
        // Los filetes de luz y las líneas de sombra de cada moldura.
        drawRect(a.claro(0.4f), Offset(0f, sup1), Size(w, 1.1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, canto1 - 1f * u), Size(w, 1f * u))
        drawRect(a.claro(0.3f), Offset(0f, taco1), Size(w, 0.9f * u))
        drawRect(a.sombra(0.45f), Offset(0f, filete1), Size(w, 1.2f * u))
        drawRect(a.claro(0.25f), Offset(0f, friso1 + 0.6f * u), Size(w, 0.9f * u))
        drawRect(a.sombra(0.5f), Offset(0f, r.bottom - 2.4f * u), Size(w, 2.4f * u))
        // El taqueado: dos hileras de tacos al tresbolillo, cada uno con su cara, su luz arriba a la izquierda y su sombra.
        val tw = w * 0.024f; val th = (taco1 - cima1) * 0.42f
        val caras = Path(); val luces = Path(); val sombras = Path()
        for (fila in 0 until 2) {
            val yt = cima1 + (taco1 - cima1) * (if (fila == 0) 0.06f else 0.52f)
            var x = if (fila == 0) 0f else tw
            while (x < w) {
                caras.addRect(Rect(x, yt, x + tw, yt + th))
                luces.addRect(Rect(x, yt, x + tw, yt + 0.9f * u)); luces.addRect(Rect(x, yt, x + 0.9f * u, yt + th))
                sombras.addRect(Rect(x + tw, yt + 1f * u, x + tw + 1.4f * u, yt + th + 1f * u)); sombras.addRect(Rect(x + 1f * u, yt + th, x + tw + 1.4f * u, yt + th + 1.2f * u))
                x += tw * 2
            }
        }
        val kc = cerca(Offset(w * 0.7f, cima1))
        drawPath(sombras, a.sombra(0.5f))
        if (a.lumbre && a.oscuro) {
            // De noche, los tacos por tramos (la luz de la lámpara cae sobre los de la derecha).
            for (t in 0 until n) {
                val x0 = w * t / n; val x1 = w * (t + 1) / n
                clipRect(x0, cima1, x1, taco1) { drawPath(caras, c(P.CEDRO, 0f, cerca(Offset((x0 + x1) / 2, cima1)))) }
            }
        } else drawPath(caras, c(P.CEDRO, 0f, kc))
        drawPath(luces, a.brillo(0.4f, kc))
        // El friso tallado a bisel: dientes de sierra, cada uno con su faceta de luz y la de sombra.
        val dp = w * 0.036f
        val fl = Path(); val fs = Path()
        var xd = 0f
        val ya = filete1 + 1.4f * u; val yb = friso1 - 1.2f * u
        while (xd < w) {
            // El diente que sube (claro a la izquierda, oscuro a la derecha) y el que baja entre dos.
            fl.moveTo(xd, yb); fl.lineTo(xd + dp * 0.5f, ya); fl.lineTo(xd + dp * 0.5f, yb - (yb - ya) * 0.3f); fl.close()
            fs.moveTo(xd + dp * 0.5f, ya); fs.lineTo(xd + dp, yb); fs.lineTo(xd + dp * 0.5f, yb - (yb - ya) * 0.3f); fs.close()
            fs.moveTo(xd, yb); fs.lineTo(xd + dp * 0.5f, yb - (yb - ya) * 0.3f); fs.lineTo(xd + dp, yb); fs.close()
            xd += dp
        }
        drawPath(fl, a.brillo(0.3f, kc))
        drawPath(fs, a.sombra(0.32f))
        // El gastado: golpes en el canto y rozaduras claras.
        for (j in 0 until 7) {
            val x = w * (0.05f + 0.9f * azar(9310, j))
            drawPath(poligonoTemplario(x, sup1, x + w * 0.014f, sup1, x + w * 0.007f, sup1 + w * 0.006f), c(P.CEDRO_LUZ, 0.1f, cerca(Offset(x, sup1))))
        }
        // Las escuadras de hierro de las esquinas, claveteadas.
        listOf(0f, w - w * 0.05f).forEach { x ->
            val k = cerca(Offset(x + w * 0.025f, filete1))
            val pl = Rect(x, canto1, x + w * 0.05f, r.bottom - 1f * u)
            drawRect(a.sombra(0.4f), Offset(pl.left + 1.2f * u, pl.top + 1.2f * u), pl.size)
            drawRect(c(P.HIERRO, 0f, k), pl.topLeft, pl.size)
            drawRect(a.brillo(0.35f, k), pl.topLeft, Size(pl.width, 0.9f * u))
            listOf(0.3f, 0.7f).forEach { fy -> clavoTemplario(Offset(pl.center.x, pl.top + pl.height * fy), 1.5f * u, a, k, P.HIERRO_LUZ) }
        }
    }

    // ---------------------------------------------------------------- los casilleros

    /** El casillero [r] de la repisa [k]: el fondo de tablas de verde tierra, sus sombras, los cantos de dentro y el piso. */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        fondo(k, r, a, cerca, null)
        if (m != null && a.oscuro) clipPath(m.forma(size.width, 0f)) { fondo(k, r, a, cerca, m) }
    }

    private fun DrawScope.fondo(k: Int, r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        // El fondo pintado se hunde de noche en verde botella; la madera, en sangre de toro.
        val av = AireTemplario(a.oscuro, a.noche, cuerpo = true, fondo = true)
        fun c(col: Color, f: Float, kk: Float) = (if (col == P.VERDE || col == P.VERDE_LUZ || col == P.VERDE_HONDO) av else a).en(col, f, kk, lit, m)
        // Las tablas del fondo, por tramos: de noche, cada uno con su luz.
        // Un píxel de más arriba (salvo en la primera, que toca la imagen de la corona), abajo y a los lados, que tapan la
        // tabla y los costados: así ninguna junta a medio píxel deja asomar el fondo.
        val n = tramos(a, r.width, w)
        val y0 = if (k == 0) r.top else floor(r.top) - 1f
        val y1 = ceil(r.bottom) + 1f
        for (t in 0 until n) {
            val x0 = if (t == 0) floor(r.left) - 1f else r.left + r.width * t / n
            val x1 = if (t == n - 1) ceil(r.right) + 1f else r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            drawRect(c(P.VERDE, 0f, kk), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        }
        val km = cerca(r.center)
        // Las juntas de las tablas (cada 0.085 anchos), con su canto de luz, y la pincelada de la pintura.
        val juntas = Path(); val cantos = Path(); val brochazos = Path()
        var x = r.left + w * 0.06f + w * 0.03f * azar(9400, k)
        var j = 0
        while (x < r.right - w * 0.01f) {
            juntas.moveTo(x, r.top); juntas.lineTo(x, r.bottom)
            cantos.moveTo(x + 1.3f * u, r.top); cantos.lineTo(x + 1.3f * u, r.bottom)
            x += w * (0.075f + 0.02f * azar(9401 + k, j)); j++
        }
        for (q in 0 until 10) {
            val bx = r.left + r.width * azar(9410 + k, q); val by = r.top + r.height * (0.2f + 0.5f * azar(9420 + k, q))
            brochazos.moveTo(bx, by); brochazos.lineTo(bx + 1.6f * u, by + r.height * (0.2f + 0.3f * azar(9430 + k, q)))
        }
        drawPath(brochazos, c(P.VERDE_LUZ, 0f, km).copy(alpha = 0.3f), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(juntas, c(P.VERDE_HONDO, 0f, km).copy(alpha = 0.85f), style = Stroke(1.1f * u))
        drawPath(cantos, c(P.VERDE_LUZ, 0f, km).copy(alpha = 0.45f), style = Stroke(0.7f * u))
        // Desconchones de la pintura, donde asoma el cedro, y algún clavo.
        for (q in 0 until 3) {
            val px = r.left + r.width * azar(9440 + k, q); val py = r.top + r.height * (0.35f + 0.55f * azar(9450 + k, q))
            val pw = w * (0.01f + 0.018f * azar(9460 + k, q))
            drawPath(poligonoTemplario(px, py, px + pw, py - pw * 0.2f, px + pw * 0.8f, py + pw * 0.5f, px + pw * 0.1f, py + pw * 0.4f), c(P.CEDRO, 0f, cerca(Offset(px, py))))
        }
        // La sombra franca de la tabla de arriba (y del cielo del casillero), y la del costado izquierdo (la luz viene de ahí).
        val franca = w * 0.045f
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.42f), r.topLeft, Size(r.width, franca))
        drawRect(a.sombra(0.16f), Offset(r.left, r.top + franca), Size(r.width, w * 0.025f))
        drawRect(a.sombra(0.1f), Offset(r.left, r.top + franca + w * 0.025f), Size(r.width, w * 0.02f))
        if (!a.oscuro) {
            drawPath(poligonoTemplario(r.left, r.top, r.left + w * 0.05f, r.top, r.left + w * 0.035f, r.bottom, r.left, r.bottom), a.sombra(0.3f))
        } else {
            drawRect(a.sombra(0.35f), Offset(r.left, r.top), Size(w * 0.03f, r.height))
            drawRect(a.sombra(0.35f), Offset(r.right - w * 0.03f, r.top), Size(w * 0.03f, r.height))
        }
        // El cielo del casillero: la cara de abajo de la tabla de arriba, en escorzo.
        val cielo = w * 0.012f
        drawRect(c(P.CEDRO_HONDO, 0f, km), r.topLeft, Size(r.width, cielo))
        // Los cantos de dentro de los costados: el izquierdo en sombra, el derecho a la luz (de día).
        val e = w * 0.02f
        val kI = cerca(Offset(r.left, r.center.y)); val kD = cerca(Offset(r.right, r.center.y))
        drawPath(poligonoTemplario(r.left, r.top, r.left + e, r.top + cielo, r.left + e, r.bottom - w * 0.014f, r.left, r.bottom), c(P.CEDRO_OSC, -0.1f, kI))
        drawPath(poligonoTemplario(r.right, r.top, r.right - e, r.top + cielo, r.right - e, r.bottom - w * 0.014f, r.right, r.bottom), c(P.CEDRO, 0.12f, kD))
        drawLine(a.claro(0.25f), Offset(r.right - e, r.top + cielo), Offset(r.right - e, r.bottom - w * 0.014f), 0.8f * u)
        // El piso: la cara de arriba de la tabla de abajo, en escorzo, gastada.
        val piso = w * 0.016f
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
            drawRect(c(P.CEDRO_LUZ, -0.12f, cerca(Offset((x0 + x1) / 2, r.bottom))), Offset(x0, r.bottom - piso), Size(minOf(x1, r.right) - x0, piso))
        }
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - piso), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla [r]: su canto grueso con el bocel, las vetas, los golpes y las escuadras de hierro de las puntas. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width
        // Debajo, la fila entera de la junta con la repisa de arriba (de píxel entero), para que no asome el fondo.
        val fy = floor(r.top)
        drawRect(a.luz(P.CEDRO, cerca(r.center)), Offset(r.left, fy), Size(r.width, ceil(r.top + 1f) - fy))
        tablaCon(k, r, a, cerca, null)
        if (m != null && a.oscuro) clipPath(m.forma(w, ManchaTemplario.FRENTE)) { tablaCon(k, r, a, cerca, m) }
    }

    private fun DrawScope.tablaCon(k: Int, r: Rect, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, kk: Float) = a.en(col, f, kk, lit, m)
        val h = r.height
        val n = tramos(a, r.width, w)
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            val ww = minOf(x1, r.right) - x0
            // El bisel de arriba, a la luz; la cara; el bocel; el bisel de abajo, en sombra.
            // Cada franja baja un píxel más y la siguiente la tapa: las juntas a medio píxel no dejan pasar el fondo.
            drawRect(c(P.CEDRO_LUZ, 0.04f, kk), Offset(x0, r.top), Size(ww, h * 0.16f + 1f))
            drawRect(c(P.CEDRO, 0f, kk), Offset(x0, r.top + h * 0.16f), Size(ww, h * 0.5f + 1f))
            drawRect(c(P.CEDRO, 0.1f, kk), Offset(x0, r.top + h * 0.66f), Size(ww, h * 0.12f + 1f))
            drawRect(c(P.CEDRO_OSC, 0f, kk), Offset(x0, r.top + h * 0.78f), Size(ww, h * 0.22f))
        }
        val km = cerca(r.center)
        drawRect(a.brillo(0.55f, km), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(a.sombra(0.3f), Offset(r.left, r.top + h * 0.16f), Size(r.width, 0.8f * u))
        drawRect(a.brillo(0.3f, km), Offset(r.left, r.top + h * 0.66f), Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, r.top + h * 0.78f), Size(r.width, 0.9f * u))
        // Las vetas.
        val vetas = Path()
        for (j in 0 until 4) {
            val yy = r.top + h * (0.24f + 0.12f * j)
            vetas.moveTo(r.left, yy)
            var x = r.left
            var q = 0
            while (x < r.right) { x += w * 0.06f; vetas.lineTo(x, yy + (azar(9500 + k * 7 + j, q) - 0.5f) * 1.6f * u); q++ }
        }
        drawPath(vetas, c(P.CEDRO_HONDO, 0f, km).copy(alpha = 0.35f), style = Stroke(0.7f * u))
        // El gastado del uso: el canto de arriba más claro donde se sacan y se meten las cosas, golpes, un cerco de agua y
        // una mancha de tinta.
        val gx = r.left + r.width * (0.2f + 0.4f * azar(9530, k))
        drawRoundRect(c(P.CEDRO_LUZ, 0.18f, cerca(Offset(gx, r.top))).copy(alpha = 0.6f), Offset(gx, r.top + 0.6f * u), Size(r.width * 0.3f, h * 0.12f), CornerRadius(h * 0.06f))
        for (j in 0 until 6) {
            val x = r.left + r.width * azar(9520 + k, j)
            drawPath(poligonoTemplario(x, r.top, x + w * 0.013f, r.top, x + w * 0.006f, r.top + h * 0.22f), c(P.CEDRO_LUZ, 0.15f, cerca(Offset(x, r.top))))
        }
        val cx = r.left + r.width * (0.1f + 0.8f * azar(9540, k))
        drawOval(a.sombra(0.3f), Offset(cx, r.top + h * 0.28f), Size(w * 0.03f, h * 0.3f), style = Stroke(1.1f * u))
        if (k % 2 == 1) drawOval(c(P.TINTA, 0f, km).copy(alpha = 0.55f), Offset(r.left + r.width * (0.6f + 0.3f * azar(9541, k)), r.top + h * 0.3f), Size(w * 0.016f, h * 0.2f))
        // Las escuadras de hierro de las puntas, que doblan sobre el costado.
        listOf(r.left, r.right - w * 0.045f).forEach { x ->
            val kk = cerca(Offset(x, r.center.y))
            val pl = Rect(x, r.top + h * 0.14f, x + w * 0.045f, r.bottom - h * 0.12f)
            drawRect(a.sombra(0.35f), Offset(pl.left + 1f * u, pl.top + 1.2f * u), pl.size)
            drawRect(c(P.HIERRO, 0f, kk), pl.topLeft, pl.size)
            drawRect(a.brillo(0.3f, kk), pl.topLeft, Size(pl.width, 0.8f * u))
            listOf(0.28f, 0.72f).forEach { fx -> clavoTemplario(Offset(pl.left + pl.width * fx, pl.center.y), 1.4f * u, a, kk, P.HIERRO_LUZ) }
        }
        // La sombra de la tabla en el casillero de abajo la pinta el casillero; aquí, la línea de su canto bajo.
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- los costados

    /** Un costado [r]: el poste de cedro con su chaflán, la acanaladura, el baquetón junto al hueco y los flejes de cada tabla. */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width
        // Debajo, las filas enteras de las juntas con la cornisa y el zócalo.
        drawRect(a.luz(P.CEDRO, cerca(r.center)), Offset(floor(r.left), floor(r.top)), Size(ceil(r.right) - floor(r.left), ceil(r.bottom) - floor(r.top)))
        costadoCon(r, izq, d, a, cerca, null)
        if (m != null && a.oscuro) clipPath(m.forma(w, ManchaTemplario.FRENTE)) { costadoCon(r, izq, d, a, cerca, m) }
    }

    private fun DrawScope.costadoCon(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireTemplario, cerca: (Offset) -> Float, m: ManchaTemplario?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, kk: Float) = a.en(col, f, kk, lit, m)
        val pw = r.width
        // Las franjas del poste, de fuera adentro: el chaflán de fuera, la cara, la acanaladura, la cara, el baquetón.
        val fr = floatArrayOf(0f, 0.12f, 0.42f, 0.52f, 0.66f, 1f)
        val tonos = listOf(P.CEDRO_LUZ to 0.05f, P.CEDRO to 0f, P.CEDRO_HONDO to 0f, P.CEDRO to 0.04f, P.CEDRO to 0f)
        val trozos = if (a.lumbre) maxOf(2, (r.height / (w * 0.06f)).toInt()) else 1
        for (t in 0 until trozos) {
            val y0 = r.top + r.height * t / trozos; val y1 = r.top + r.height * (t + 1) / trozos + 1.2f
            val kk = cerca(Offset(r.center.x, (y0 + y1) / 2))
            for (i in 0 until 5) {
                val fa = fr[i]; val fb = fr[i + 1]
                val xa = if (izq) r.left + pw * fa else r.right - pw * fb
                val (col, f) = tonos[i]
                // La luz del día viene de la izquierda: el chaflán de fuera del costado derecho queda en sombra.
                val ff = if (!izq && i == 0) -0.1f else f
                drawRect(c(col, ff, kk), Offset(xa, y0), Size(pw * (fb - fa) + 1.2f, minOf(y1, r.bottom) - y0))
            }
            // El baquetón: un cilindro de pie junto al hueco.
            val bq = if (izq) Rect(r.left + pw * 0.66f, y0, r.right, minOf(y1, r.bottom)) else Rect(r.left, y0, r.left + pw * 0.34f, minOf(y1, r.bottom))
            clipRect(bq.left, bq.top, bq.right, bq.bottom) { cilindroTemplario(bq, a, kk, if (a.oscuro) (if (izq) 1f else -1f) else -1f, 1.2f) }
        }
        val km = cerca(r.center)
        // Las líneas de la talla: el fondo de la acanaladura y los filos del baquetón.
        val xs = if (izq) listOf(pw * 0.42f, pw * 0.52f, pw * 0.66f) else listOf(pw * 0.58f, pw * 0.48f, pw * 0.34f)
        drawLine(a.sombra(0.45f), Offset(r.left + xs[0], r.top), Offset(r.left + xs[0], r.bottom), 1f * u)
        drawLine(a.claro(0.25f), Offset(r.left + xs[1], r.top), Offset(r.left + xs[1], r.bottom), 0.8f * u)
        drawLine(a.sombra(0.4f), Offset(r.left + xs[2], r.top), Offset(r.left + xs[2], r.bottom), 1f * u)
        // Las vetas, a lo alto, en las caras.
        val vetas = Path()
        for (j in 0 until 3) {
            val xx = r.left + pw * (if (izq) 0.16f + 0.08f * j else 0.6f + 0.1f * j)
            vetas.moveTo(xx, r.top)
            var y = r.top; var q = 0
            while (y < r.bottom) { y += w * 0.05f; vetas.lineTo(xx + (azar(9600 + j + (if (izq) 0 else 9), q) - 0.5f) * 1.6f * u, y); q++ }
        }
        drawPath(vetas, c(P.CEDRO_HONDO, 0f, km).copy(alpha = 0.35f), style = Stroke(0.7f * u))
        // El roce de las manos: la arista de fuera más clara a media altura de cada repisa; algún nudo y un golpe hondo.
        d.repisas.forEachIndexed { j, rp ->
            val kk = cerca(Offset(r.center.x, rp.center.y))
            val xx = if (izq) r.left + pw * 0.64f else r.left + pw * 0.22f
            drawOval(c(P.CEDRO_LUZ, 0.14f, kk).copy(alpha = 0.42f), Offset(xx, rp.top + rp.height * 0.2f), Size(pw * 0.14f, rp.height * 0.6f))
            val ny = rp.top + rp.height * (0.15f + 0.6f * azar(9620 + j, if (izq) 1 else 2))
            val nx = r.left + pw * (if (izq) 0.2f else 0.7f)
            drawOval(c(P.CEDRO_HONDO, 0f, kk).copy(alpha = 0.7f), Offset(nx, ny), Size(pw * 0.14f, pw * 0.26f))
            drawOval(c(P.CEDRO_LUZ, 0f, kk).copy(alpha = 0.5f), Offset(nx + pw * 0.03f, ny + pw * 0.06f), Size(pw * 0.06f, pw * 0.12f))
            if (azar(9630 + j, if (izq) 3 else 4) > 0.6f) drawPath(poligonoTemplario(nx, ny + rp.height * 0.3f, nx + pw * 0.2f, ny + rp.height * 0.28f, nx + pw * 0.1f, ny + rp.height * 0.36f), a.sombra(0.45f))
        }
        // Un remiendo viejo: una chapa de hierro clavada sobre una raja, en el costado de la izquierda.
        if (izq && d.repisas.size > 1) {
            val rp = d.repisas[1]
            val kk = cerca(Offset(r.center.x, rp.center.y))
            val pl = Rect(r.left + pw * 0.14f, rp.top + rp.height * 0.55f, r.left + pw * 0.4f, rp.top + rp.height * 0.78f)
            drawLine(a.sombra(0.5f), Offset(pl.center.x, pl.top - rp.height * 0.12f), Offset(pl.center.x + pw * 0.04f, pl.bottom + rp.height * 0.1f), 1f * u)
            drawRect(a.sombra(0.4f), Offset(pl.left + 1f * u, pl.top + 1.2f * u), pl.size)
            drawRect(c(P.HIERRO, 0f, kk), pl.topLeft, pl.size)
            drawRect(a.brillo(0.3f, kk), pl.topLeft, Size(pl.width, 0.8f * u))
            listOf(Offset(pl.left + pl.width * 0.25f, pl.top + pl.height * 0.25f), Offset(pl.right - pl.width * 0.25f, pl.bottom - pl.height * 0.25f)).forEach { clavoTemplario(it, 1.1f * u, a, kk, P.HIERRO_LUZ) }
            drawRect(c(Color(0xFF8A4A22), 0f, kk).copy(alpha = 0.45f), Offset(pl.center.x - 0.6f * u, pl.bottom), Size(1.4f * u, rp.height * 0.12f))
        }
        // Los flejes de hierro a la altura de cada tabla, con sus clavos.
        d.tablas.forEach { tb ->
            val kk = cerca(Offset(r.center.x, tb.center.y))
            val f = Rect(r.left, tb.top + tb.height * 0.14f, r.right, tb.bottom - tb.height * 0.12f)
            drawRect(a.sombra(0.4f), Offset(f.left, f.bottom), Size(f.width, 1.6f * u))
            drawRect(c(P.HIERRO, 0f, kk), f.topLeft, f.size)
            drawRect(a.brillo(0.35f, kk), f.topLeft, Size(f.width, 0.9f * u))
            listOf(0.3f, 0.7f).forEach { fx -> clavoTemplario(Offset(f.left + f.width * fx, f.center.y), 1.6f * u, a, kk, P.HIERRO_LUZ) }
        }
        // La sombra que el costado echa hacia dentro y el filo de su canto que mira al hueco.
        if (izq) drawRect(a.sombra(0.25f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        else drawRect(a.claro(0.18f), Offset(r.left, r.top), Size(1.2f * u, r.height))
    }

    // ---------------------------------------------------------------- las cédulas de los meses

    /** La cédula de pergamino clavada en el canto de la tabla, con el mes. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireTemplario, k: Float) {
        val w = size.width; val u = density
        val c = p.r.center
        val ancho = minOf(w * 0.66f, (p.r.width / 0.42f) * 0.74f)
        val r = Rect(c.x - ancho / 2, p.r.top - p.r.height * 0.02f, c.x + ancho / 2, p.r.bottom + p.r.height * 0.02f)
        val forma = Path().apply {
            moveTo(r.left + 2f * u, r.top); lineTo(r.right - 1f * u, r.top + 0.6f * u); lineTo(r.right, r.bottom - 1.6f * u)
            lineTo(r.right - 3f * u, r.bottom); lineTo(r.left, r.bottom - 0.4f * u); lineTo(r.left + 0.6f * u, r.top + 2f * u); close()
        }
        drawPath(forma, a.sombra(0.4f), alpha = 1f)
        clipRect(0f, 0f, w, size.height) {
            drawPath(Path().apply { addPath(forma, Offset(1.4f * u, 1.6f * u)) }, a.sombra(0.35f))
        }
        val perg = a.etiqueta(mezcla(P.PERGAMINO, P.PERG_OSC, 0.25f), k)
        drawPath(forma, perg)
        clipPath(forma) {
            drawRect(a.etiqueta(P.PERG_OSC, k).copy(alpha = 0.4f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
            drawRect(a.etiqueta(P.PERG_OSC, k).copy(alpha = 0.3f), r.topLeft, Size(r.width * 0.05f, r.height))
            drawRect(a.etiqueta(P.PERG_OSC, k).copy(alpha = 0.3f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
        }
        // La esquina levantada y los dos clavos.
        drawPath(poligonoTemplario(r.right - 5f * u, r.bottom, r.right, r.bottom - 4f * u, r.right - 1.6f * u, r.bottom - 0.6f * u), a.etiqueta(mezcla(P.PERG_OSC, P.TINTA, 0.2f), k))
        clavoTemplario(Offset(r.left + 4f * u, r.center.y), 1.5f * u, a, k)
        clavoTemplario(Offset(r.right - 4f * u, r.center.y), 1.5f * u, a, k)
        textoEn(medidor, p.texto, Rect(r.left + 9f * u, r.top, r.right - 9f * u, r.bottom), P.LETRA, a.luz(P.TINTA, maxOf(k, 0.7f)), 10.5f, peso = FontWeight.SemiBold, minimo = 6f)
    }

    // ---------------------------------------------------------------- el zócalo (en la imagen guardada de abajo)

    /** El zócalo de [r]: el bocel de arriba, los arquillos ciegos y la faja de abajo, sobre el suelo. */
    fun DrawScope.zocalo(r: Rect, a: AireTemplario, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val h = r.height
        val y1 = r.top + h * 0.18f; val y2 = r.bottom - h * 0.16f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            drawRect(a.dia(P.CEDRO, 0.05f, kk), Offset(x0, r.top), Size(x1 - x0, y1 - r.top))
            drawRect(a.dia(P.CEDRO, 0f, kk), Offset(x0, y1), Size(x1 - x0, y2 - y1))
            drawRect(a.dia(P.CEDRO_OSC, 0f, kk), Offset(x0, y2), Size(x1 - x0, r.bottom - y2))
        }
        val km = cerca(r.center)
        drawRect(a.brillo(0.45f, km), Offset(0f, r.top + 0.6f * u), Size(w, 1f * u))
        drawRect(a.sombra(0.45f), Offset(0f, y1 - 1.2f * u), Size(w, 1.6f * u))
        drawRect(a.brillo(0.3f, km), Offset(0f, y2), Size(w, 0.9f * u))
        // Los arquillos ciegos: el fondo hundido de cada arco, su sombra arriba a la izquierda y el canto de luz abajo a la derecha.
        val aw = w * 0.085f
        var x = w * 0.012f
        var j = 0
        while (x < w) {
            val kk = cerca(Offset(x + aw / 2, r.center.y))
            val arco = Rect(x + aw * 0.16f, y1 + (y2 - y1) * 0.16f, x + aw * 0.84f, y2 - (y2 - y1) * 0.08f)
            val forma = Path().apply {
                moveTo(arco.left, arco.bottom); lineTo(arco.left, arco.top + arco.width / 2)
                arcTo(Rect(arco.left, arco.top, arco.right, arco.top + arco.width), 180f, 180f, false)
                lineTo(arco.right, arco.bottom); close()
            }
            drawPath(forma, a.dia(P.CEDRO_HONDO, 0.08f, kk))
            clipPath(forma) {
                drawRect(a.sombra(0.4f), arco.topLeft, Size(arco.width * 0.3f, arco.height))
                drawRect(a.sombra(0.3f), arco.topLeft, Size(arco.width, arco.height * 0.25f))
            }
            drawPath(forma, a.brillo(0.3f, kk), style = Stroke(0.9f * u))
            // La columnilla entre dos arcos, con su luz.
            drawRect(a.dia(P.CEDRO_LUZ, 0f, kk), Offset(x - aw * 0.04f, y1 + (y2 - y1) * 0.2f), Size(aw * 0.08f, (y2 - y1) * 0.72f))
            x += aw; j++
        }
        // Las patadas: el pie del zócalo rozado y más claro a trechos, y la mugre del suelo.
        for (j in 0 until 5) {
            val x = w * (0.08f + 0.85f * azar(9650, j))
            drawRoundRect(a.dia(P.CEDRO_LUZ, 0.06f, cerca(Offset(x, y2))).copy(alpha = 0.3f), Offset(x, y2 + (r.bottom - y2) * 0.2f), Size(w * (0.03f + 0.05f * azar(9651, j)), (r.bottom - y2) * 0.6f), CornerRadius(2f * u))
        }
        drawRect(a.sombra(0.25f), Offset(0f, r.bottom - (r.bottom - y2) * 0.35f), Size(w, (r.bottom - y2) * 0.35f))
        drawRect(a.sombra(0.5f), Offset(0f, r.bottom - 1.6f * u), Size(w, 1.6f * u))
    }
}
