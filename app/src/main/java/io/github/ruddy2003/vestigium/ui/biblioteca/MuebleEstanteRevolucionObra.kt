package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La librería de roble pintado de Revolución ([MuebleEstanteRevolucion]), al gusto Luis XVI de un carpintero del barrio: la
 * cornisa (el tablero de arriba, el cimacio, la hilera de dentellones y el friso de canales con su junquillo), los casilleros
 * con su fondo de tablas de verde cardenillo (la sombra franca de la tabla de arriba, los cantos de dentro, el piso), las
 * tablas con el canto moldurado, los costados como pilastras acanaladas con su capitel y su basa, las etiquetas impresas de los
 * meses y el cuerpo bajo con sus dos cajones de tirador de latón, el zócalo y las patas de peonza. La pintura sangre de toro
 * está gastada en los cantos (asoma el roble) y lleva huellas de tinta. De día la luz viene de la ventana (izquierda); de
 * noche, cada tramo se apaga con la distancia a la lumbre que le toca.
 */
internal object ObraEstanteRevolucion {
    private val P = PaletaRevolucion

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireRevolucion, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.035f)).toInt().coerceIn(2, 40) else 1

    // ---------------------------------------------------------------- la cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.cornisa(r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        cornisaCon(r, a, cerca, null)
        if (m != null) {
            clipPath(m.forma(size.width, ManchaRevolucion.FRENTE)) { cornisaCon(r, a, cerca, m) }
            clipPath(m.forma(size.width, ManchaRevolucion.FRENTE, papel = true)) { cornisaCon(r, a, cerca, ManchaRevolucion(m.q, m.color, m.fuerza * 0.5f)) }
        }
    }

    private fun DrawScope.cornisaCon(r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, k: Float) = a.en(col, f, k, lit, m)
        val y0 = r.top
        val sup0 = y0 - w * 0.007f; val sup1 = y0 + w * 0.007f
        val tab1 = y0 + w * 0.024f
        val cim1 = y0 + w * 0.036f
        val den1 = y0 + w * 0.058f
        val fil1 = y0 + w * 0.064f
        val fri1 = y0 + w * 0.092f
        val jun1 = y0 + w * 0.099f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y0 + w * 0.04f))
            // El tablero visto desde encima (gastado: la pintura se fue y asoma el roble) y su canto volado.
            drawRect(c(mezcla(P.SANGRE_LUZ, P.ROBLE, 0.35f), -0.05f, k), Offset(x0, sup0), Size(x1 - x0, sup1 - sup0 + 1f))
            drawRect(c(P.SANGRE, 0.04f, k), Offset(x0, sup1), Size(x1 - x0, tab1 - sup1 + 1f))
            // El cimacio: un talón (claro arriba, hondo abajo).
            drawRect(c(P.SANGRE_LUZ, -0.05f, k), Offset(x0, tab1), Size(x1 - x0, (cim1 - tab1) * 0.5f + 1f))
            drawRect(c(P.SANGRE_OSC, 0f, k), Offset(x0, tab1 + (cim1 - tab1) * 0.5f), Size(x1 - x0, (cim1 - tab1) * 0.5f + 1f))
            // El fondo de los dentellones, hondo.
            drawRect(c(P.SANGRE_HONDA, 0f, k), Offset(x0, cim1), Size(x1 - x0, den1 - cim1 + 1f))
            drawRect(c(P.SANGRE_LUZ, 0f, k), Offset(x0, den1), Size(x1 - x0, fil1 - den1 + 1f))
            // El friso y el junquillo; debajo, la faja.
            drawRect(c(P.SANGRE, 0f, k), Offset(x0, fil1), Size(x1 - x0, fri1 - fil1 + 1f))
            drawRect(c(P.SANGRE_LUZ, -0.08f, k), Offset(x0, fri1), Size(x1 - x0, (jun1 - fri1) * 0.5f + 1f))
            drawRect(c(P.SANGRE_OSC, 0f, k), Offset(x0, fri1 + (jun1 - fri1) * 0.5f), Size(x1 - x0, (jun1 - fri1) * 0.5f + 1f))
            drawRect(c(P.SANGRE, -0.08f, k), Offset(x0, jun1), Size(x1 - x0, r.bottom - jun1))
        }
        // Los filetes de luz y las líneas de sombra de cada moldura.
        drawRect(a.claro(0.45f), Offset(0f, sup1), Size(w, 1.1f * u))
        drawRect(a.sombra(0.45f), Offset(0f, tab1 - 1f * u), Size(w, 1.2f * u))
        drawRect(a.claro(0.3f), Offset(0f, den1), Size(w, 0.9f * u))
        drawRect(a.sombra(0.45f), Offset(0f, fil1), Size(w, 1.3f * u))
        drawRect(a.claro(0.3f), Offset(0f, fri1 + 0.4f * u), Size(w, 0.9f * u))
        drawRect(a.sombra(0.4f), Offset(0f, jun1), Size(w, 1.3f * u))
        drawRect(a.sombra(0.5f), Offset(0f, r.bottom - 2.4f * u), Size(w, 2.4f * u))
        // Los dentellones: tacos en hilera, cada uno con su cara, su luz a la izquierda y su sombra a la derecha y abajo.
        val dw = w * 0.012f; val paso = w * 0.021f
        val caras = Path(); val luces = Path(); val sombras = Path()
        var x = paso * 0.3f
        while (x < w) {
            caras.addRect(Rect(x, cim1, x + dw, den1 - w * 0.002f))
            luces.addRect(Rect(x, cim1, x + 0.9f * u, den1 - w * 0.002f))
            sombras.addRect(Rect(x + dw, cim1 + 1f * u, x + dw + 1.8f * u, den1)); sombras.addRect(Rect(x + 1f * u, den1 - w * 0.002f, x + dw + 1.8f * u, den1))
            x += paso
        }
        val kc = cerca(Offset(w * 0.66f, cim1))
        drawPath(sombras, a.sombra(0.5f))
        if (a.lumbre && a.oscuro) {
            for (t in 0 until n) {
                val xa = w * t / n; val xb = w * (t + 1) / n
                clipRect(xa, cim1, xb, den1) { drawPath(caras, c(P.SANGRE, 0f, cerca(Offset((xa + xb) / 2, cim1)))) }
            }
        } else drawPath(caras, c(P.SANGRE, 0f, kc))
        drawPath(luces, a.brillo(0.4f, kc))
        // El friso de canales: ranuras cortas y verticales, en sombra a la izquierda y con luz a la derecha.
        val canales = Path(); val bordes = Path()
        var xc = w * 0.09f
        while (xc < w * 0.91f) {
            canales.addRect(Rect(xc, fil1 + w * 0.006f, xc + w * 0.004f, fri1 - w * 0.005f))
            bordes.addRect(Rect(xc + w * 0.004f, fil1 + w * 0.006f, xc + w * 0.0055f, fri1 - w * 0.005f))
            xc += w * 0.016f
        }
        drawPath(canales, a.sombra(0.42f))
        drawPath(bordes, a.brillo(0.3f, kc))
        // Sobre cada pilastra, el dado del friso, adelantado, con su cuadro rehundido.
        listOf(0f, w * (1f - MuebleEstanteRevolucion.LADO)).forEach { dx ->
            val k = cerca(Offset(dx + w * 0.037f, fil1))
            val dado = Rect(dx + w * 0.006f, fil1 - w * 0.002f, dx + w * (MuebleEstanteRevolucion.LADO - 0.006f), fri1 + w * 0.002f)
            drawRect(a.sombra(0.4f), Offset(dado.left + 1.6f * u, dado.top + 1.4f * u), dado.size)
            drawRect(c(P.SANGRE, 0.05f, k), dado.topLeft, dado.size)
            val rh = dado.deflate(w * 0.008f)
            drawRect(c(P.SANGRE_OSC, 0f, k), rh.topLeft, rh.size)
            drawRect(a.sombra(0.35f), rh.topLeft, Size(rh.width, 1.4f * u)); drawRect(a.sombra(0.35f), rh.topLeft, Size(1.4f * u, rh.height))
            drawRect(a.claro(0.3f), Offset(rh.left, rh.bottom - 1f * u), Size(rh.width, 1f * u))
            drawRect(a.claro(0.35f), dado.topLeft, Size(dado.width, 1f * u))
        }
        // Lo gastado: golpes en el canto del tablero donde asoma el roble, y una huella de tinta.
        for (j in 0 until 9) {
            val gx = w * (0.03f + 0.94f * azar(8310, j))
            val gw = w * (0.01f + 0.025f * azar(8311, j))
            drawRect(c(P.ROBLE, 0.05f, cerca(Offset(gx, sup1))), Offset(gx, sup1 + (tab1 - sup1) * 0.1f * azar(8312, j)), Size(gw, w * (0.002f + 0.003f * azar(8313, j))))
        }
        drawOval(Color(0xFF221A20).copy(alpha = 0.35f), Offset(w * 0.21f, sup1 + w * 0.004f), Size(w * 0.012f, w * 0.008f))
        drawOval(Color(0xFF221A20).copy(alpha = 0.3f), Offset(w * 0.228f, sup1 + w * 0.007f), Size(w * 0.01f, w * 0.007f))
    }

    // ---------------------------------------------------------------- los casilleros

    /** El casillero [r] de la repisa [k]: el fondo de tablas de cardenillo, sus sombras, los cantos de dentro y el piso. */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        fondo(k, r, a, cerca, null)
        if (m != null && a.oscuro) clipPath(m.forma(size.width, 0f)) { fondo(k, r, a, cerca, m) }
    }

    private fun DrawScope.fondo(k: Int, r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        // El fondo pintado se hunde de noche en verde botella; la madera, en sangre de toro honda.
        val av = AireRevolucion(a.oscuro, a.noche, cuerpo = true, fondo = true)
        fun v(col: Color, f: Float, kk: Float) = av.en(col, f, kk, lit, m)
        fun c(col: Color, f: Float, kk: Float) = a.en(col, f, kk, lit, m)
        // Las tablas del fondo, por tramos: un píxel de más arriba (salvo en la primera, que toca la imagen de la corona),
        // abajo y a los lados, que tapan la tabla y los costados: así ninguna junta a medio píxel deja asomar el fondo.
        val n = tramos(a, r.width, w)
        val y0 = if (k == 0) r.top else floor(r.top) - 1f
        val y1 = ceil(r.bottom) + 1f
        for (t in 0 until n) {
            val x0 = if (t == 0) floor(r.left) - 1f else r.left + r.width * t / n
            val x1 = if (t == n - 1) ceil(r.right) + 1f else r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            drawRect(v(P.VERDE, 0f, kk), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        }
        val km = cerca(r.center)
        // De día, el fondo más claro a la izquierda (de donde viene la luz) y más hondo a la derecha.
        if (!a.oscuro) drawRect(a.frio(0.12f), Offset(r.left + r.width * 0.55f, r.top), Size(r.width * 0.45f, r.height))
        // Las juntas de las tablas (cada 0.09 anchos), con su canto de luz, y los brochazos de la pintura.
        val juntas = Path(); val cantos = Path(); val brochazos = Path()
        var x = r.left + w * 0.05f + w * 0.03f * azar(8400, k)
        var j = 0
        while (x < r.right - w * 0.01f) {
            juntas.moveTo(x, r.top); juntas.lineTo(x, r.bottom)
            cantos.moveTo(x + 1.3f * u, r.top); cantos.lineTo(x + 1.3f * u, r.bottom)
            x += w * (0.08f + 0.025f * azar(8401 + k, j)); j++
        }
        for (q in 0 until 12) {
            val bx = r.left + r.width * azar(8410 + k, q); val by = r.top + r.height * (0.18f + 0.55f * azar(8420 + k, q))
            brochazos.moveTo(bx, by); brochazos.lineTo(bx + 1.4f * u, by + r.height * (0.18f + 0.3f * azar(8430 + k, q)))
        }
        drawPath(brochazos, v(P.VERDE_LUZ, 0f, km).copy(alpha = 0.32f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(juntas, v(P.VERDE_HONDO, 0f, km).copy(alpha = 0.85f), style = Stroke(1.2f * u))
        drawPath(cantos, v(P.VERDE_LUZ, 0f, km).copy(alpha = 0.45f), style = Stroke(0.7f * u))
        // Desconchones de la pintura, donde asoma el roble, y un clavo con un jirón de papel.
        for (q in 0 until 3) {
            val px = r.left + r.width * azar(8440 + k, q); val py = r.top + r.height * (0.35f + 0.5f * azar(8450 + k, q))
            val pw = w * (0.01f + 0.02f * azar(8460 + k, q))
            drawPath(poligonoRevolucion(px, py, px + pw, py - pw * 0.25f, px + pw * 0.85f, py + pw * 0.45f, px + pw * 0.1f, py + pw * 0.35f), c(P.ROBLE, 0f, cerca(Offset(px, py))))
        }
        val cx = r.left + r.width * (0.2f + 0.6f * azar(8470, k)); val cy = r.top + r.height * 0.42f
        drawPath(poligonoRevolucion(cx - w * 0.006f, cy + w * 0.002f, cx + w * 0.012f, cy, cx + w * 0.01f, cy + w * 0.02f, cx - w * 0.004f, cy + w * 0.024f), c(P.PAPEL, -0.15f, cerca(Offset(cx, cy))))
        clavoRevolucion(Offset(cx, cy + w * 0.004f), 1.5f * u, a, cerca(Offset(cx, cy)))
        // La sombra franca de la tabla de arriba, y la del costado izquierdo (la luz viene de ahí).
        val franca = w * 0.05f
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.44f), r.topLeft, Size(r.width, franca))
        drawRect(a.sombra(0.18f), Offset(r.left, r.top + franca), Size(r.width, w * 0.025f))
        drawRect(a.sombra(0.1f), Offset(r.left, r.top + franca + w * 0.025f), Size(r.width, w * 0.02f))
        if (!a.oscuro) {
            drawPath(poligonoRevolucion(r.left, r.top, r.left + w * 0.055f, r.top, r.left + w * 0.04f, r.bottom, r.left, r.bottom), a.sombra(0.3f))
        } else {
            drawRect(a.sombra(0.35f), Offset(r.left, r.top), Size(w * 0.03f, r.height))
            drawRect(a.sombra(0.35f), Offset(r.right - w * 0.03f, r.top), Size(w * 0.03f, r.height))
        }
        // El cielo del casillero: la cara de abajo de la tabla de arriba, en escorzo.
        val cielo = w * 0.012f
        drawRect(c(P.SANGRE_HONDA, 0f, km), r.topLeft, Size(r.width, cielo))
        // Los cantos de dentro de los costados: el izquierdo en sombra, el derecho a la luz (de día).
        val e = w * 0.02f
        val kI = cerca(Offset(r.left, r.center.y)); val kD = cerca(Offset(r.right, r.center.y))
        drawPath(poligonoRevolucion(r.left, r.top, r.left + e, r.top + cielo, r.left + e, r.bottom - w * 0.014f, r.left, r.bottom), c(P.SANGRE_OSC, -0.1f, kI))
        drawPath(poligonoRevolucion(r.right, r.top, r.right - e, r.top + cielo, r.right - e, r.bottom - w * 0.014f, r.right, r.bottom), c(P.SANGRE, 0.12f, kD))
        drawLine(a.claro(0.25f), Offset(r.right - e, r.top + cielo), Offset(r.right - e, r.bottom - w * 0.014f), 0.8f * u)
        // El piso: la cara de arriba de la tabla de abajo, en escorzo, con la pintura gastada por el roce de lo guardado.
        val piso = w * 0.016f
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
            drawRect(c(mezcla(P.SANGRE_LUZ, P.ROBLE, 0.4f), -0.12f, cerca(Offset((x0 + x1) / 2, r.bottom))), Offset(x0, r.bottom - piso), Size(minOf(x1, r.right) - x0, piso))
        }
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - piso), Size(r.width, 1.2f * u))
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla [r]: su canto moldurado (bisel, cara, junquillo y filete), los golpes donde asoma el roble y las huellas. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width
        // Debajo, la fila entera de la junta con la repisa de arriba (de píxel entero), para que no asome el fondo.
        val fy = floor(r.top)
        drawRect(a.luz(P.SANGRE, cerca(r.center)), Offset(r.left, fy), Size(r.width, ceil(r.top + 1f) - fy))
        tablaCon(k, r, a, cerca, null)
        if (m != null && a.oscuro) clipPath(m.forma(w, ManchaRevolucion.FRENTE)) { tablaCon(k, r, a, cerca, m) }
    }

    private fun DrawScope.tablaCon(k: Int, r: Rect, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, kk: Float) = a.en(col, f, kk, lit, m)
        val h = r.height
        val n = tramos(a, r.width, w)
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            val ww = minOf(x1, r.right) - x0
            // El bisel de arriba, a la luz; la cara; el junquillo; el filete de abajo, en sombra. Cada franja baja un píxel más
            // y la siguiente la tapa: las juntas a medio píxel no dejan pasar el fondo.
            drawRect(c(P.SANGRE_LUZ, 0.02f, kk), Offset(x0, r.top), Size(ww, h * 0.15f + 1f))
            drawRect(c(P.SANGRE, 0f, kk), Offset(x0, r.top + h * 0.15f), Size(ww, h * 0.5f + 1f))
            drawRect(c(P.SANGRE_LUZ, -0.1f, kk), Offset(x0, r.top + h * 0.65f), Size(ww, h * 0.08f + 1f))
            drawRect(c(P.SANGRE_OSC, 0f, kk), Offset(x0, r.top + h * 0.73f), Size(ww, h * 0.09f + 1f))
            drawRect(c(P.SANGRE, -0.15f, kk), Offset(x0, r.top + h * 0.82f), Size(ww, h * 0.18f))
        }
        val km = cerca(r.center)
        drawRect(a.brillo(0.55f, km), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(a.brillo(0.3f, km), Offset(r.left, r.top + h * 0.65f), Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.4f), Offset(r.left, r.top + h * 0.15f), Size(r.width, 1f * u))
        if (a.oscuro && m == null) {
            // De noche, el filo cálido del canto que mira a la lumbre.
            for (t in 0 until n) {
                val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n + 1.2f
                val ww = minOf(x1, r.right) - x0
                val kk = cerca(Offset((x0 + x1) / 2, r.top))
                drawRect(a.filo((0.75f * kk).coerceAtMost(0.7f)), Offset(x0, r.top), Size(ww, 1.4f * u))
                val kb = cerca(Offset((x0 + x1) / 2, r.bottom + w * 0.05f))
                drawRect(a.filo((0.4f * kb).coerceAtMost(0.4f)), Offset(x0, r.bottom - 1.4f * u), Size(ww, 1.4f * u))
            }
        }
        // Lo gastado: el canto de arriba rozado donde asoma el roble, golpes y una huella de dedo entintado.
        for (j in 0 until 6) {
            val gx = r.left + r.width * azar(8500 + k, j)
            val gw = w * (0.012f + 0.03f * azar(8510 + k, j))
            drawRect(c(P.ROBLE, 0f, cerca(Offset(gx, r.top))), Offset(gx, r.top + h * 0.02f), Size(gw, h * (0.05f + 0.06f * azar(8520 + k, j))))
        }
        for (j in 0 until 4) {
            val gx = r.left + r.width * azar(8530 + k, j)
            drawLine(c(P.SANGRE_OSC, 0f, km), Offset(gx, r.top + h * 0.3f), Offset(gx + w * 0.012f, r.top + h * (0.36f + 0.1f * azar(8540 + k, j))), 1f * u)
        }
        if (azar(8550, k) > 0.4f) {
            val hx = r.left + r.width * (0.1f + 0.15f * azar(8551, k))
            drawOval(Color(0xFF1E1820).copy(alpha = 0.32f), Offset(hx, r.top + h * 0.3f), Size(w * 0.011f, h * 0.22f))
        }
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom), Size(r.width, 2.4f * u))
    }

    // ---------------------------------------------------------------- los costados: pilastras acanaladas

    /** Un costado en [r] ([izq]: el de la izquierda): la pilastra con sus canales, el capitel arriba, la basa abajo y lo gastado. */
    fun DrawScope.costado(r: Rect, izq: Boolean, a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (r.height < 2f) return
        // Por tramos a lo alto: de noche, cada uno con su luz.
        val n = if (a.lumbre) (r.height / (w * 0.05f)).toInt().coerceIn(2, 80) else 1
        val cara = Rect(r.left + w * 0.006f, r.top, r.right - w * 0.006f, r.bottom)
        for (t in 0 until n) {
            val y0 = r.top + r.height * t / n; val y1 = r.top + r.height * (t + 1) / n + 1.2f
            val k = cerca(Offset(r.center.x, (y0 + y1) / 2))
            drawRect(a.dia(P.SANGRE_OSC, 0f, k), Offset(r.left, y0), Size(r.width, minOf(y1, r.bottom) - y0))
            drawRect(a.dia(P.SANGRE, 0.03f, k), Offset(cara.left, y0), Size(cara.width, minOf(y1, r.bottom) - y0))
        }
        val km = cerca(r.center)
        // Los canales: tres ranuras largas, en sombra a la izquierda y con luz a la derecha; en el tercio de abajo, rellenas de
        // su junquillo (las rudenturas).
        val cap = r.top + w * 0.045f; val basa = r.bottom - w * 0.05f
        val rud = basa - (basa - cap) * 0.3f
        val canales = Path(); val luces = Path(); val juncos = Path()
        for (i in 0 until 3) {
            val x = cara.left + cara.width * (0.2f + 0.27f * i)
            val cw = w * 0.009f
            canales.addRoundRect(androidx.compose.ui.geometry.RoundRect(x, cap + w * 0.012f, x + cw, basa - w * 0.008f, cw / 2, cw / 2))
            luces.addRect(Rect(x + cw * 0.65f, cap + w * 0.016f, x + cw, rud))
            juncos.addRoundRect(androidx.compose.ui.geometry.RoundRect(x + cw * 0.15f, rud, x + cw * 0.85f, basa - w * 0.01f, cw / 2, cw / 2))
        }
        if (a.lumbre) {
            for (t in 0 until n) {
                val y0 = r.top + r.height * t / n; val y1 = r.top + r.height * (t + 1) / n
                val k = cerca(Offset(r.center.x, (y0 + y1) / 2))
                clipRect(r.left, y0, r.right, y1) {
                    drawPath(canales, a.dia(P.SANGRE_HONDA, 0f, k))
                    drawPath(luces, a.dia(P.SANGRE_LUZ, 0f, k))
                    drawPath(juncos, a.dia(P.SANGRE, 0.05f, k))
                }
            }
        } else {
            drawPath(canales, a.dia(P.SANGRE_HONDA, 0f, km).copy(alpha = 0.85f))
            drawPath(luces, a.dia(P.SANGRE_LUZ, 0.05f, km))
            drawPath(juncos, a.dia(P.SANGRE, 0.06f, km))
            drawPath(juncos, a.claro(0.25f), style = Stroke(0.7f * u))
        }
        // El capitel: un dado con su moldura y su cuadro rehundido.
        val kc = cerca(Offset(r.center.x, cap))
        drawRect(a.dia(P.SANGRE, 0.06f, kc), Offset(r.left, r.top), Size(r.width, cap - r.top))
        drawRect(a.dia(P.SANGRE_LUZ, 0f, kc), Offset(r.left, cap - w * 0.012f), Size(r.width, w * 0.006f))
        drawRect(a.sombra(0.45f), Offset(r.left, cap - w * 0.006f), Size(r.width, w * 0.006f))
        val rh = Rect(cara.left + w * 0.008f, r.top + w * 0.01f, cara.right - w * 0.008f, cap - w * 0.016f)
        if (rh.height > 2f) {
            drawRect(a.dia(P.SANGRE_OSC, 0f, kc), rh.topLeft, rh.size)
            drawRect(a.sombra(0.35f), rh.topLeft, Size(rh.width, 1.2f * u)); drawRect(a.sombra(0.35f), rh.topLeft, Size(1.2f * u, rh.height))
        }
        // La basa: un plinto con su bocel.
        val kb = cerca(Offset(r.center.x, basa))
        drawRect(a.dia(P.SANGRE, 0.02f, kb), Offset(r.left, basa), Size(r.width, r.bottom - basa))
        drawRect(a.dia(P.SANGRE_LUZ, 0f, kb), Offset(r.left, basa), Size(r.width, w * 0.007f))
        drawRect(a.sombra(0.4f), Offset(r.left, basa + w * 0.007f), Size(r.width, 1.2f * u))
        // Los cantos: el de fuera y el de dentro; de día, la luz por la izquierda.
        drawRect(a.claro(if (izq) 0.35f else 0.2f), Offset(cara.left, r.top), Size(1.1f * u, r.height))
        drawRect(a.sombra(0.4f), Offset(cara.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
        if (a.oscuro) {
            // De noche, el filo del canto que mira a la lumbre, por tramos.
            for (t in 0 until n) {
                val y0 = r.top + r.height * t / n; val y1 = r.top + r.height * (t + 1) / n
                val k = cerca(Offset(r.center.x, (y0 + y1) / 2))
                drawRect(a.filo((0.6f * k).coerceAtMost(0.55f)), Offset(if (izq) cara.right - 1.4f * u else cara.left, y0), Size(1.4f * u, y1 - y0))
            }
        }
        // Lo gastado: el roble que asoma en los cantos a la altura de la mano, y huellas de tinta.
        for (j in 0 until 5) {
            val gy = r.top + r.height * azar(if (izq) 8600 else 8610, j)
            val gx = if (azar(8620, j) > 0.5f) cara.left else cara.right - w * 0.006f
            drawRect(a.dia(P.ROBLE, 0f, cerca(Offset(gx, gy))), Offset(gx, gy), Size(w * 0.006f, w * (0.012f + 0.03f * azar(8630, j))))
        }
        for (j in 0 until 3) {
            val hy = r.top + r.height * (0.3f + 0.5f * azar(if (izq) 8640 else 8650, j))
            drawOval(Color(0xFF1E1820).copy(alpha = 0.28f), Offset(cara.left + cara.width * (0.1f + 0.5f * azar(8660, j)), hy), Size(w * 0.011f, w * 0.016f))
        }
    }

    // ---------------------------------------------------------------- las etiquetas de los meses

    /** La etiqueta impresa de un mes, pegada en el canto de la tabla: papel tostado con su doble filete y el mes en tinta. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireRevolucion, k: Float) {
        val u = density
        val r = p.r
        val giro = (azar(p.texto.hashCode(), 3) - 0.5f) * 1.6f
        rotate(giro, r.center) {
            drawRect(a.sombra(0.4f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
            drawRect(a.etiqueta(P.TOSTADO, k), r.topLeft, r.size)
            // El papel no es liso: la cola que se transparenta en una esquina y el borde de abajo más tostado.
            drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
            drawRect(a.etiqueta(P.PAPEL_OSC, k).copy(alpha = 0.3f), r.topLeft, Size(r.width * 0.06f, r.height))
            val tinta = if (a.oscuro) Color(0xFF140C08) else a.luz(P.TINTA, maxOf(k, 0.75f))
            val f1 = r.deflate(1.4f * u); val f2 = r.deflate(2.8f * u)
            drawRect(tinta.copy(alpha = 0.75f), f1.topLeft, f1.size, style = Stroke(1.1f * u))
            drawRect(tinta.copy(alpha = 0.55f), f2.topLeft, f2.size, style = Stroke(0.5f * u))
            textoEn(medidor, p.texto, Rect(r.left + 3f * u, r.top + 3f * u, r.right - 3f * u, r.bottom - 3f * u), PaletaRevolucion.LETRA, tinta, 10.5f, peso = FontWeight.Bold, minimo = 5f)
            // Una esquina despegada que hace sombra.
            val e = r.height * 0.32f
            drawPath(poligonoRevolucion(r.right - e, r.top, r.right, r.top, r.right, r.top + e), a.sombra(0.3f))
            drawPath(poligonoRevolucion(r.right - e, r.top, r.right - e * 0.15f, r.top + e * 0.85f, r.right - e * 0.9f, r.top + e * 0.6f), a.etiqueta(P.PAPEL, k))
        }
    }

    // ---------------------------------------------------------------- el cuerpo bajo

    /**
     * El cuerpo bajo de [r] (desde el borde de arriba de la base [y0] hasta donde pisan las patas): la faja, los dos cajones con su
     * moldura, su tirador de latón y su bocallave, el zócalo con su talón y las patas de peonza; debajo, el hueco en sombra.
     */
    fun DrawScope.cuerpoBajo(r: Rect, y0: Float, a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val m = MuebleEstanteRevolucion
        val bh = m.BASE * w
        val caj1 = y0 + bh * m.CAJONES
        val zoc1 = caj1 + w * 0.032f
        val suelo = r.bottom
        val n = tramos(a, w, w)
        // El hueco bajo el mueble, entre las patas, en sombra.
        drawRect(a.sombra(if (a.oscuro) 0.9f else 0.7f), Offset(0f, zoc1 - 1f), Size(w, suelo - zoc1 + 2f))
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1.2f
            val k = cerca(Offset((x0 + x1) / 2, (y0 + caj1) / 2))
            // El frente del cuerpo bajo, desde la fila entera de arriba: la faja de arriba y el frente de los cajones.
            drawRect(a.dia(P.SANGRE_OSC, 0f, k), Offset(x0, r.top), Size(x1 - x0, y0 - r.top + w * 0.02f))
            drawRect(a.dia(P.SANGRE, 0f, k), Offset(x0, y0 + w * 0.006f), Size(x1 - x0, caj1 - y0))
            // El zócalo: un talón claro arriba y la faja honda.
            val kz = cerca(Offset((x0 + x1) / 2, caj1 + w * 0.02f))
            drawRect(a.dia(P.SANGRE_LUZ, -0.05f, kz), Offset(x0, caj1), Size(x1 - x0, w * 0.01f + 1f))
            drawRect(a.dia(P.SANGRE, -0.1f, kz), Offset(x0, caj1 + w * 0.01f), Size(x1 - x0, zoc1 - caj1 - w * 0.01f))
        }
        drawRect(a.claro(0.4f), Offset(0f, y0 + w * 0.006f), Size(w, 1.1f * u))
        drawRect(a.sombra(0.45f), Offset(0f, y0 + w * 0.02f), Size(w, 1.3f * u))
        drawRect(a.claro(0.35f), Offset(0f, caj1), Size(w, 1f * u))
        drawRect(a.sombra(0.4f), Offset(0f, caj1 + w * 0.01f), Size(w, 1.2f * u))
        drawRect(a.sombra(0.55f), Offset(0f, zoc1 - 2f * u), Size(w, 2f * u))
        // Los dos cajones.
        val lado = m.LADO * w
        listOf(Rect(lado + w * 0.01f, y0 + w * 0.03f, w * 0.495f, caj1 - w * 0.012f), Rect(w * 0.505f, y0 + w * 0.03f, w - lado - w * 0.01f, caj1 - w * 0.012f)).forEachIndexed { i, cj ->
            cajon(cj, i, a, cerca)
        }
        // Los montantes de las esquinas, con su cuadro rehundido.
        listOf(Rect(0f, y0 + w * 0.026f, lado, caj1 - w * 0.008f), Rect(w - lado, y0 + w * 0.026f, w, caj1 - w * 0.008f)).forEach { mt ->
            val k = cerca(mt.center)
            val rh = mt.deflate(w * 0.014f)
            drawRect(a.dia(P.SANGRE_OSC, 0f, k), rh.topLeft, rh.size)
            drawRect(a.sombra(0.4f), rh.topLeft, Size(rh.width, 1.3f * u)); drawRect(a.sombra(0.4f), rh.topLeft, Size(1.3f * u, rh.height))
            drawRect(a.claro(0.3f), Offset(rh.left, rh.bottom - 1f * u), Size(rh.width, 1f * u))
        }
        // Las patas de peonza: un bulbo torneado que se afila hacia el suelo, con su anillo.
        listOf(w * 0.035f, w * 0.5f, w * 0.965f).forEach { px ->
            val k = cerca(Offset(px, suelo))
            val pw = w * 0.05f
            val pata = Path().apply {
                moveTo(px - pw / 2, zoc1 - 1f)
                lineTo(px + pw / 2, zoc1 - 1f)
                cubicTo(px + pw * 0.62f, zoc1 + (suelo - zoc1) * 0.4f, px + pw * 0.2f, suelo - (suelo - zoc1) * 0.2f, px + pw * 0.12f, suelo)
                lineTo(px - pw * 0.12f, suelo)
                cubicTo(px - pw * 0.2f, suelo - (suelo - zoc1) * 0.2f, px - pw * 0.62f, zoc1 + (suelo - zoc1) * 0.4f, px - pw / 2, zoc1 - 1f)
                close()
            }
            drawPath(pata, a.dia(P.SANGRE, 0f, k))
            clipPath(pata) {
                cilindroRevolucion(Rect(px - pw * 0.62f, zoc1, px + pw * 0.62f, suelo), a, k, if (a.oscuro && px < w * m.ESTUFA_X) 1f else -1f, 1.3f)
                drawRect(a.dia(P.SANGRE_LUZ, 0f, k), Offset(px - pw, zoc1 + (suelo - zoc1) * 0.22f), Size(pw * 2, w * 0.004f))
                drawRect(a.sombra(0.4f), Offset(px - pw, zoc1 + (suelo - zoc1) * 0.22f + w * 0.004f), Size(pw * 2, w * 0.003f))
            }
        }
        // Lo gastado: el roble en los cantos del zócalo, donde lo patean.
        for (j in 0 until 8) {
            val gx = w * azar(8700, j)
            drawRect(a.dia(P.ROBLE, 0f, cerca(Offset(gx, zoc1))), Offset(gx, zoc1 - w * (0.004f + 0.006f * azar(8701, j))), Size(w * (0.01f + 0.03f * azar(8702, j)), w * 0.004f))
        }
        if (a.oscuro) {
            for (t in 0 until n) {
                val x0 = w * t / n; val x1 = w * (t + 1) / n
                drawRect(a.filo((0.6f * cerca(Offset((x0 + x1) / 2, caj1))).coerceAtMost(0.55f)), Offset(x0, caj1), Size(x1 - x0, 1.3f * u))
            }
        }
        drawRect(a.sombra(0.25f), Offset(0f, y0), Size(w, w * 0.004f))
    }

    /** Un cajón en [r]: el frente con su moldura alrededor, el tirador de latón colgante con su roseta y la bocallave. */
    private fun DrawScope.cajon(r: Rect, i: Int, a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(r.center)
        // La junta alrededor (el cajón entra en su hueco) y la moldura del frente: claro arriba a la izquierda, hondo abajo.
        drawRect(a.sombra(0.6f), r.topLeft, r.size)
        val f = r.deflate(w * 0.003f)
        drawRect(a.dia(P.SANGRE, 0.04f, k), f.topLeft, f.size)
        val mo = f.deflate(w * 0.009f)
        drawRect(a.dia(P.SANGRE_LUZ, 0f, k), Offset(f.left, f.top), Size(f.width, w * 0.004f))
        drawRect(a.dia(P.SANGRE_LUZ, 0f, k), Offset(f.left, f.top), Size(w * 0.004f, f.height))
        drawRect(a.sombra(0.35f), Offset(mo.left, mo.top), Size(mo.width, 1.3f * u))
        drawRect(a.sombra(0.35f), Offset(mo.left, mo.top), Size(1.3f * u, mo.height))
        drawRect(a.claro(0.3f), Offset(mo.left, mo.bottom), Size(mo.width, 1f * u))
        drawRect(a.claro(0.3f), Offset(mo.right, mo.top), Size(1f * u, mo.height))
        // Los tiradores de latón: dos rosetas con el asa colgante entre ellas.
        val cy = f.top + f.height * 0.42f
        val cx = f.center.x
        val sep = w * 0.05f
        val asa = Path().apply { moveTo(cx - sep, cy); cubicTo(cx - sep, cy + w * 0.03f, cx + sep, cy + w * 0.03f, cx + sep, cy) }
        drawPath(Path().apply { addPath(asa, Offset(1.4f * u, 1.8f * u)) }, a.sombra(0.45f), style = Stroke(w * 0.0045f, cap = StrokeCap.Round))
        drawPath(asa, a.dia(P.LATON, 0f, k), style = Stroke(w * 0.0045f, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.45f, k), style = Stroke(w * 0.0015f, cap = StrokeCap.Round))
        listOf(cx - sep, cx + sep).forEach { x ->
            drawCircle(a.sombra(0.4f), w * 0.009f, Offset(x + 1.2f * u, cy + 1.4f * u))
            drawCircle(a.dia(P.LATON_OSC, 0f, k), w * 0.009f, Offset(x, cy))
            drawCircle(a.dia(P.LATON, 0f, k), w * 0.0065f, Offset(x, cy))
            drawCircle(a.brillo(0.6f, k), w * 0.0025f, Offset(x - w * 0.002f, cy - w * 0.002f))
        }
        // La bocallave: un escudete de latón con su ojo.
        val bx = cx; val by = f.top + f.height * 0.2f
        drawPath(poligonoRevolucion(bx - w * 0.008f, by - w * 0.008f, bx + w * 0.008f, by - w * 0.008f, bx + w * 0.006f, by + w * 0.01f, bx - w * 0.006f, by + w * 0.01f), a.dia(P.LATON, -0.05f, k))
        drawCircle(a.dia(P.HIERRO, 0f, k), w * 0.0025f, Offset(bx, by - w * 0.001f))
        drawRect(a.dia(P.HIERRO, 0f, k), Offset(bx - w * 0.001f, by), Size(w * 0.002f, w * 0.006f))
        // Lo gastado: el roble alrededor del tirador, donde se agarra, y una huella de tinta.
        drawOval(a.dia(P.ROBLE, 0f, k).copy(alpha = 0.35f), Offset(cx - sep * 0.8f, cy + w * 0.012f), Size(sep * 1.6f, w * 0.02f))
        if (i == 0) drawOval(Color(0xFF1E1820).copy(alpha = 0.3f), Offset(f.left + f.width * 0.7f, f.top + f.height * 0.5f), Size(w * 0.012f, w * 0.016f))
    }
}
