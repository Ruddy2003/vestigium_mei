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
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * La librería de campaña de Napoleónico ([MuebleEstanteNapoleon]): la caja-cornisa (el tablero de arriba, el bocel, el friso
 * de caoba flameada con su filete de latón embutido y sus rosetas, la gola), las cajas de cada repisa (el forro de paño verde
 * con la sombra franca del techo de la caja, los cantos de dentro y el piso), las tablas (el larguero de abajo de una caja, la
 * junta y el de arriba de la siguiente), los costados con su filete, las cantoneras de latón de cada caja y las bisagras de la
 * puerta que se quitó para el viaje, los portaetiquetas de los meses y la caja-zócalo con sus patas de bollo. De día, la luz
 * viene de la ventana (izquierda); de noche, cada tramo se apaga con la distancia a la luz que le toca.
 */
internal object ObraEstanteNapoleon {
    private val P = PaletaNapoleon

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireNapoleon, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.03f)).toInt().coerceIn(2, 48) else 1

    /** La veta flameada de la caoba (el plumaje de la chapa cortada en la horquilla) en [r], con su eje en el centro. */
    private fun DrawScope.flameado(r: Rect, color: Color, semilla: Int) {
        val u = density
        val cx = r.center.x + (azar(semilla, 1) - 0.5f) * r.width * 0.1f
        val p = Path()
        val n = (r.width / (7f * u)).toInt().coerceIn(4, 60)
        for (i in 0 until n) {
            val f = (i + 0.5f) / n
            val y0 = r.bottom + r.height * 0.1f
            for (s in listOf(-1f, 1f)) {
                val x1 = cx + s * r.width * 0.5f * f * (1.05f + 0.1f * azar(semilla, 10 + i))
                val y1 = r.top - r.height * (0.1f + 0.3f * azar(semilla, 40 + i))
                p.moveTo(cx + s * r.width * 0.02f * f, y0)
                p.quadraticTo(cx + s * r.width * 0.06f * f, r.top + r.height * (0.4f - 0.25f * f), x1, y1)
            }
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(p, color, style = Stroke(0.8f * u)) }
    }

    /** Una roseta de latón (una pátera de ocho pétalos con su botón), de radio [rr]. */
    private fun DrawScope.roseta(c: Offset, rr: Float, a: AireNapoleon, k: Float) {
        drawCircle(a.sombra(0.45f), rr * 1.04f, c + Offset(rr * 0.18f, rr * 0.24f))
        drawCircle(a.luz(P.LATON_OSC, k), rr, c)
        drawCircle(a.luz(P.LATON, k), rr * 0.9f, c)
        for (j in 0 until 8) {
            val an = j * 0.785f + 0.39f
            val q = Offset(c.x + rr * 0.56f * cos(an), c.y + rr * 0.56f * sin(an))
            drawCircle(a.luz(P.LATON_OSC, k), rr * 0.23f, q)
            drawCircle(a.luz(P.LATON_LUZ, k), rr * 0.13f, q - Offset(rr * 0.05f, rr * 0.06f))
        }
        drawCircle(a.luz(P.LATON_OSC, k), rr * 0.3f, c)
        drawCircle(a.luz(P.LATON, k), rr * 0.24f, c)
        drawCircle(a.brillo(0.7f, k), rr * 0.1f, c - Offset(rr * 0.08f, rr * 0.08f))
    }

    /** Una cantonera de latón en L en la esquina [c] ([dx], [dy]: hacia dónde abren sus brazos), con sus tornillos. */
    fun DrawScope.cantonera(c: Offset, brazo: Float, grueso: Float, dx: Float, dy: Float, a: AireNapoleon, k: Float) {
        val u = density
        val x0 = minOf(c.x, c.x + dx * brazo); val y0 = minOf(c.y, c.y + dy * grueso)
        val h1 = Rect(x0, y0, x0 + brazo, y0 + grueso)
        val x2 = minOf(c.x, c.x + dx * grueso); val y2 = minOf(c.y, c.y + dy * brazo)
        val v1 = Rect(x2, y2, x2 + grueso, y2 + brazo)
        val l = Path().apply { addRect(h1); addRect(v1) }
        drawPath(Path().apply { addPath(l, Offset(1.1f * u, 1.4f * u)) }, a.sombra(0.42f))
        drawPath(l, a.luz(P.LATON, k))
        drawRect(a.brillo(0.5f, k), h1.topLeft, Size(h1.width, 0.8f * u))
        drawRect(a.brillo(0.35f, k), v1.topLeft, Size(0.8f * u, v1.height))
        drawRect(a.luz(P.LATON_OSC, k).copy(alpha = 0.7f), Offset(h1.left, h1.bottom - 0.7f * u), Size(h1.width, 0.7f * u))
        drawRect(a.luz(P.LATON_OSC, k).copy(alpha = 0.6f), Offset(v1.right - 0.7f * u, v1.top), Size(0.7f * u, v1.height))
        val t = grueso * 0.24f
        listOf(Offset(c.x + dx * grueso * 0.5f, c.y + dy * grueso * 0.5f), Offset(c.x + dx * brazo * 0.72f, c.y + dy * grueso * 0.5f),
            Offset(c.x + dx * grueso * 0.5f, c.y + dy * brazo * 0.72f)).forEach { p ->
            drawCircle(a.luz(P.LATON_OSC, k), t, p)
            drawLine(a.sombra(0.5f), Offset(p.x - t * 0.7f, p.y - t * 0.4f), Offset(p.x + t * 0.7f, p.y + t * 0.4f), 0.5f * u)
        }
    }

    // ---------------------------------------------------------------- la caja-cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.cornisa(r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        fun c(col: Color, f: Float, k: Float) = a.dia(col, f, k)
        val y0 = r.top
        val sup0 = y0 - w * 0.009f; val sup1 = y0 + w * 0.006f
        val canto1 = y0 + w * 0.02f
        val bocel1 = y0 + w * 0.033f
        val friso0 = y0 + w * 0.037f
        val friso1 = y0 + w * 0.074f
        val gola1 = y0 + w * 0.086f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y0 + w * 0.04f))
            // El tablero de arriba, visto desde encima (más claro y gastado), y su canto.
            drawRect(c(P.CAOBA_LUZ, -0.12f, k), Offset(x0, sup0), Size(x1 - x0, sup1 - sup0))
            drawRect(c(P.CAOBA, 0.04f, k), Offset(x0, sup1), Size(x1 - x0, canto1 - sup1))
            // El bocel: un medio cilindro (claro arriba, hondo abajo).
            drawRect(c(P.CAOBA_LUZ, 0f, k), Offset(x0, canto1), Size(x1 - x0, (bocel1 - canto1) * 0.45f))
            drawRect(c(P.CAOBA, -0.1f, k), Offset(x0, canto1 + (bocel1 - canto1) * 0.45f), Size(x1 - x0, (bocel1 - canto1) * 0.55f))
            drawRect(c(P.CAOBA_HONDO, 0f, k), Offset(x0, bocel1), Size(x1 - x0, friso0 - bocel1))
            // El friso de chapa flameada.
            drawRect(c(P.CAOBA, 0f, k), Offset(x0, friso0), Size(x1 - x0, friso1 - friso0))
            // La gola: hondo arriba, claro abajo.
            drawRect(c(P.CAOBA_OSC, 0f, k), Offset(x0, friso1), Size(x1 - x0, (gola1 - friso1) * 0.5f))
            drawRect(c(P.CAOBA, 0f, k), Offset(x0, friso1 + (gola1 - friso1) * 0.5f), Size(x1 - x0, (gola1 - friso1) * 0.5f))
            // El larguero de arriba de la primera caja.
            drawRect(c(P.CAOBA_OSC, -0.05f, k), Offset(x0, gola1), Size(x1 - x0, r.bottom - gola1))
        }
        val kc = cerca(Offset(w * 0.6f, friso0))
        // La llama de la chapa, simétrica desde el centro, y la de las dos alas.
        flameado(Rect(w * 0.06f, friso0, w * 0.94f, friso1), c(P.CAOBA_LLAMA, 0.08f, kc).copy(alpha = 0.55f), 7101)
        flameado(Rect(w * 0.3f, friso0, w * 0.7f, friso1), c(P.CAOBA_HONDO, 0f, kc).copy(alpha = 0.35f), 7102)
        // Los filetes de luz y las líneas de sombra de cada moldura.
        drawRect(a.claro(0.45f), Offset(0f, sup1), Size(w, 1.1f * u))
        drawRect(a.sombra(0.35f), Offset(0f, canto1 - 1f * u), Size(w, 1f * u))
        drawRect(a.claro(0.35f), Offset(0f, canto1 + 0.8f * u), Size(w, 1f * u))
        drawRect(a.sombra(0.5f), Offset(0f, friso0 - 1.2f * u), Size(w, 1.2f * u))
        drawRect(a.sombra(0.45f), Offset(0f, friso1), Size(w, 1.4f * u))
        drawRect(a.claro(0.22f), Offset(0f, gola1 - 1.4f * u), Size(w, 0.9f * u))
        drawRect(a.sombra(0.6f), Offset(0f, gola1), Size(w, 1.6f * u))
        drawRect(a.sombra(0.4f), Offset(0f, r.bottom - 3f * u), Size(w, 3f * u))
        // El filete de latón embutido en el friso y en el canto del tablero.
        val fl = Rect(w * 0.085f, friso0 + w * 0.008f, w * 0.915f, friso1 - w * 0.008f)
        drawRect(c(P.LATON, 0f, kc), fl.topLeft, fl.size, style = Stroke(1.3f * u))
        drawRect(a.sombra(0.3f), Offset(fl.left, fl.top + 1.3f * u), Size(fl.width, 0.8f * u))
        drawLine(c(P.LATON, -0.05f, kc), Offset(0f, canto1 - 2.4f * u), Offset(w, canto1 - 2.4f * u), 1f * u)
        // El gastado: golpes en el canto del tablero y rozaduras claras.
        for (j in 0 until 8) {
            val x = w * (0.04f + 0.92f * azar(7110, j))
            drawPath(poligonoNapoleon(x, sup1, x + w * 0.012f, sup1, x + w * 0.006f, sup1 + w * 0.005f), c(P.CAOBA_LUZ, 0.12f, cerca(Offset(x, sup1))))
            val y = friso0 + (friso1 - friso0) * azar(7111, j)
            drawLine(a.claro(0.14f), Offset(x, y), Offset(x + w * (0.02f + 0.03f * azar(7112, j)), y + 0.5f * u), 0.8f * u)
        }
        // Las rosetas de latón en las puntas del friso y las cantoneras de las esquinas de la caja.
        listOf(w * 0.045f, w * 0.955f).forEach { x ->
            val k = cerca(Offset(x, friso0))
            roseta(Offset(x, (friso0 + friso1) / 2), w * 0.017f, a, k)
        }
        val br = w * 0.05f; val gr = w * 0.012f
        cantonera(Offset(0f, sup1), br, gr, 1f, 1f, a, cerca(Offset(0f, sup1)))
        cantonera(Offset(w, sup1), br, gr, -1f, 1f, a, cerca(Offset(w, sup1)))
        cantonera(Offset(0f, r.bottom - 2f), br * 0.8f, gr, 1f, -1f, a, cerca(Offset(0f, r.bottom)))
        cantonera(Offset(w, r.bottom - 2f), br * 0.8f, gr, -1f, -1f, a, cerca(Offset(w, r.bottom)))
    }

    // ---------------------------------------------------------------- las cajas de las repisas

    /** El casillero [r] de la repisa [k]: el forro de paño verde, sus sombras, los cantos de dentro y el piso. */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val af = AireNapoleon(a.oscuro, a.noche, cuerpo = true, fondo = true)
        // La luna de los Sueños oscuros roza las cajas de arriba a la izquierda.
        val luna = lunaCuerpoNapoleon(a, r.left / w + 0.2f, r.center.y / w)
        fun c(col: Color, f: Float, kk: Float) = af.dia(col, f, kk).let { if (luna > 0f) af.sol(col, it, luna * 0.7f) else it }
        fun cm(col: Color, f: Float, kk: Float) = a.dia(col, f, kk).let { if (luna > 0f) a.sol(col, it, luna * 0.7f) else it }
        // Una fila de más arriba y abajo: así las juntas con las tablas no dejan asomar el fondo.
        val fr = Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f)
        val n = tramos(a, r.width, w)
        val techo = w * 0.04f
        val lado = w * 0.024f
        val piso = w * 0.016f
        for (t in 0 until n) {
            val x0 = fr.left + fr.width * t / n; val x1 = fr.left + fr.width * (t + 1) / n + 1f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            // El paño: más vivo abajo, donde llega la luz por la boca de la caja, y más hondo arriba, bajo el techo.
            drawRect(c(P.PANO, 0f, kk), Offset(x0, fr.top), Size(x1 - x0, fr.height))
            drawRect(c(P.PANO_OSC, 0f, kk).copy(alpha = 0.55f), Offset(x0, fr.top), Size(x1 - x0, r.height * 0.45f))
            drawRect(c(P.PANO_LUZ, 0f, kk).copy(alpha = if (a.oscuro) 0.25f else 0.35f), Offset(x0, r.top + r.height * 0.58f), Size(x1 - x0, r.height * 0.3f))
        }
        // El pelo del paño: rayitas a lo alto, más claras y más oscuras, y las motas del uso.
        val pelo = Path(); val pelo2 = Path()
        for (j in 0 until (r.width / (5f * u)).toInt().coerceIn(4, 120)) {
            val x = r.left + r.width * azar(7200 + k, j)
            val y = r.top + r.height * (0.2f + 0.75f * azar(7300 + k, j))
            val l = r.height * (0.05f + 0.12f * azar(7400 + k, j))
            if (j % 2 == 0) { pelo.moveTo(x, y); pelo.lineTo(x + 0.6f * u, y + l) } else { pelo2.moveTo(x, y); pelo2.lineTo(x - 0.5f * u, y + l) }
        }
        val kc = cerca(r.center)
        drawPath(pelo, c(P.PANO_LUZ, 0.08f, kc).copy(alpha = 0.35f), style = Stroke(0.8f * u))
        drawPath(pelo2, c(P.PANO_HONDO, 0f, kc).copy(alpha = 0.3f), style = Stroke(0.8f * u))
        // Lo gastado: una rozadura clara donde roza lo que se saca y se mete, y alguna mancha.
        val rx = r.left + r.width * (0.15f + 0.6f * azar(7500, k))
        drawOval(c(P.PANO_LUZ, 0.15f, cerca(Offset(rx, r.bottom))).copy(alpha = 0.3f), Offset(rx, r.top + r.height * 0.55f), Size(w * 0.16f, r.height * 0.3f))
        val mx = r.left + r.width * azar(7501, k)
        drawOval(c(P.PANO_HONDO, 0f, kc).copy(alpha = 0.35f), Offset(mx, r.top + r.height * (0.35f + 0.2f * azar(7502, k))), Size(w * 0.03f, w * 0.022f))
        // El techo de la caja: su cara de abajo (un trapecio de caoba en sombra) y la sombra franca que echa en el paño.
        val kt = cerca(Offset(r.center.x, r.top))
        drawRect(af.sombra(0.55f), Offset(r.left, r.top + techo * 0.8f), Size(r.width, techo * 0.55f))
        drawRect(af.sombra(0.28f), Offset(r.left, r.top + techo * 1.35f), Size(r.width, techo * 0.9f))
        drawPath(poligonoNapoleon(fr.left, fr.top, fr.right, fr.top, r.right - lado, r.top + techo, r.left + lado, r.top + techo), cm(P.CAOBA_OSC, -0.25f, kt))
        drawRect(a.sombra(0.35f), Offset(r.left + lado, r.top + techo - 1.2f * u), Size(r.width - 2 * lado, 1.2f * u))
        // Los cantos de dentro: el de la izquierda en sombra (mira a la derecha, de espaldas a la ventana); el de la derecha,
        // alumbrado por ella. De noche, cada uno según su luz.
        val kl = cerca(Offset(r.left, r.center.y)); val kr = cerca(Offset(r.right, r.center.y))
        drawPath(poligonoNapoleon(fr.left, fr.top, r.left + lado, r.top + techo, r.left + lado, r.bottom - piso, fr.left, fr.bottom), cm(P.CAOBA, -0.32f, kl))
        drawPath(poligonoNapoleon(fr.right, fr.top, r.right - lado, r.top + techo, r.right - lado, r.bottom - piso, fr.right, fr.bottom), cm(P.CAOBA, 0.05f, kr))
        drawRect(a.sombra(0.3f), Offset(r.left + lado, r.top + techo), Size(w * 0.01f, r.height - techo - piso))
        drawRect(a.brillo(0.2f, kr), Offset(r.right - lado - 1f * u, r.top + techo), Size(1f * u, r.height - techo - piso))
        // La veta de los cantos de dentro.
        listOf(0.35f, 0.65f).forEach { f ->
            drawLine(cm(P.CAOBA_OSC, 0f, kl).copy(alpha = 0.5f), Offset(r.left + lado * f, r.top + techo * f), Offset(r.left + lado * f, r.bottom - piso * f), 0.7f * u)
            drawLine(cm(P.CAOBA_OSC, 0f, kr).copy(alpha = 0.4f), Offset(r.right - lado * f, r.top + techo * f), Offset(r.right - lado * f, r.bottom - piso * f), 0.7f * u)
        }
        // El piso de la caja, visto de canto, con la luz que entra por la boca.
        val kp = cerca(Offset(r.center.x, r.bottom))
        drawPath(poligonoNapoleon(r.left + lado, r.bottom - piso, r.right - lado, r.bottom - piso, fr.right, fr.bottom, fr.left, fr.bottom), cm(P.CAOBA_LUZ, -0.1f, kp))
        drawRect(a.sombra(0.4f), Offset(r.left + lado, r.bottom - piso), Size(r.width - 2 * lado, 1f * u))
    }

    // ---------------------------------------------------------------- las tablas (la junta entre dos cajas)

    /** La tabla [r] bajo la repisa [k]: el larguero de abajo de su caja, la junta y el larguero de arriba de la siguiente. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val ultima = k == d.tablas.lastIndex
        val junta = r.top + r.height * (if (ultima) 0.9f else 0.6f)
        val n = tramos(a, r.width, w)
        // Una fila de más arriba y abajo, del color del larguero.
        for (t in 0 until n) {
            val x0 = r.left + r.width * t / n - 1f; val x1 = r.left + r.width * (t + 1) / n + 1f
            val kk = cerca(Offset((x0 + x1) / 2, r.center.y))
            val luna = lunaCuerpoNapoleon(a, (x0 + x1) / 2 / w, r.center.y / w) * 0.6f
            fun c(col: Color, f: Float) = a.dia(col, f, kk).let { if (luna > 0f) a.sol(col, it, luna) else it }
            drawRect(c(P.CAOBA, 0f), Offset(x0, r.top - 1f), Size(x1 - x0, junta - r.top + 1f))
            drawRect(c(P.CAOBA, -0.12f), Offset(x0, junta), Size(x1 - x0, r.bottom - junta + 1f))
        }
        val kc = cerca(r.center)
        // La veta a lo largo de los dos largueros.
        val veta = Path()
        for (j in 0 until 5) {
            val y = r.top + (junta - r.top) * (0.2f + 0.16f * j)
            veta.moveTo(r.left, y)
            var x = r.left
            while (x < r.right) { val x1 = x + w * 0.08f; veta.quadraticTo((x + x1) / 2, y + (azar(7600 + k * 7 + j, (x / w * 20).toInt()) - 0.5f) * 1.6f * u, x1, y); x = x1 }
        }
        clipRect(r.left, r.top, r.right, junta) { drawPath(veta, a.luz(P.CAOBA_OSC, kc).copy(alpha = 0.45f), style = Stroke(0.7f * u)) }
        // El canto de delante del piso de la caja: el filete de luz arriba, el bocel y la sombra; la junta honda.
        drawRect(a.brillo(if (a.oscuro) 0.5f else 0.5f, kc), Offset(r.left, r.top), Size(r.width, 1.3f * u))
        drawRect(a.claro(0.18f), Offset(r.left, r.top + 1.3f * u), Size(r.width, (junta - r.top) * 0.3f))
        drawRect(a.sombra(0.3f), Offset(r.left, junta - (junta - r.top) * 0.22f), Size(r.width, (junta - r.top) * 0.22f))
        drawRect(a.sombra(0.8f), Offset(r.left, junta - 0.6f * u), Size(r.width, 2.2f * u))
        if (!ultima) {
            drawRect(a.claro(0.25f), Offset(r.left, junta + 1.6f * u), Size(r.width, 0.8f * u))
            drawRect(a.sombra(0.25f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        }
        // De noche, el filo de la luz en el canto (más donde más alumbra).
        if (a.lumbre && a.oscuro) {
            for (t in 0 until n) {
                val x0 = r.left + r.width * t / n; val x1 = r.left + r.width * (t + 1) / n
                val kk = cerca(Offset((x0 + x1) / 2, r.top))
                drawRect(a.filo(0.55f * kk), Offset(x0, r.top), Size(x1 - x0, 1.5f * u))
            }
        }
        // Los golpes del viaje en el canto.
        for (j in 0 until 3) {
            val x = r.left + r.width * azar(7700 + k, j)
            drawPath(poligonoNapoleon(x, r.top, x + w * 0.01f, r.top, x + w * 0.004f, r.top + w * 0.005f), a.luz(P.CAOBA_LUZ, cerca(Offset(x, r.top))))
        }
    }

    // ---------------------------------------------------------------- los costados

    /** El costado [r] ([izq]: el de la izquierda): el canto de las cajas apiladas, con su filete, sus cantoneras y las bisagras. */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        // Las juntas de las cajas: a la altura de la junta de cada tabla.
        val juntas = d.tablas.mapIndexed { k, t -> t.top + t.height * (if (k == d.tablas.lastIndex) 0.9f else 0.6f) }
        val cortes = listOf(r.top) + juntas.filter { it > r.top && it < r.bottom } + listOf(r.bottom)
        val tramosV = (r.height / (w * 0.05f)).toInt().coerceIn(1, 120).let { if (a.lumbre) it else 1 }
        // La cara del canto, por tramos a lo alto (de noche, la luz cambia con la altura).
        for (t in 0 until tramosV) {
            val y0 = r.top + r.height * t / tramosV - 1f; val y1 = r.top + r.height * (t + 1) / tramosV + 1f
            val kk = cerca(Offset(r.center.x, (y0 + y1) / 2))
            val luna = lunaCuerpoNapoleon(a, r.center.x / w, (y0 + y1) / 2 / w) * 0.6f
            fun c(col: Color, f: Float) = a.dia(col, f, kk).let { if (luna > 0f) a.sol(col, it, luna) else it }
            drawRect(c(P.CAOBA, if (izq) 0.02f else -0.06f), Offset(r.left - 1f, y0), Size(r.width + 2f, y1 - y0))
            // El canto que mira a la ventana, más claro; el otro, en sombra.
            drawRect(c(P.CAOBA_LUZ, -0.05f), Offset(r.left, y0), Size(r.width * 0.12f, y1 - y0))
            drawRect(c(P.CAOBA_OSC, 0f), Offset(r.right - r.width * 0.14f, y0), Size(r.width * 0.14f, y1 - y0))
        }
        // La veta a lo alto.
        val kc = cerca(r.center)
        val veta = Path()
        for (j in 0 until 6) {
            val x = r.left + r.width * (0.18f + 0.13f * j + 0.04f * azar(7800 + (if (izq) 0 else 1), j))
            veta.moveTo(x, r.top)
            var y = r.top
            while (y < r.bottom) { val y1 = y + w * 0.1f; veta.quadraticTo(x + (azar(7810 + j, (y / w * 10).toInt()) - 0.5f) * 2f * u, (y + y1) / 2, x, y1); y = y1 }
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(veta, a.luz(P.CAOBA_OSC, kc).copy(alpha = 0.4f), style = Stroke(0.7f * u)) }
        // El filete de latón embutido, a lo alto de cada caja.
        val fx = r.left + r.width * (if (izq) 0.5f else 0.42f)
        cortes.zipWithNext().forEach { (y0, y1) ->
            val kk = cerca(Offset(fx, (y0 + y1) / 2))
            drawLine(a.luz(P.LATON, kk), Offset(fx, y0 + w * 0.06f), Offset(fx, y1 - w * 0.06f), 1.3f * u)
            drawLine(a.sombra(0.35f), Offset(fx + 1.2f * u, y0 + w * 0.06f), Offset(fx + 1.2f * u, y1 - w * 0.06f), 0.7f * u)
        }
        // Las juntas: una línea honda con su filo de luz debajo.
        juntas.forEach { y ->
            if (y <= r.top || y >= r.bottom) return@forEach
            drawRect(a.sombra(0.8f), Offset(r.left - 1f, y - 0.6f * u), Size(r.width + 2f, 2.2f * u))
            drawRect(a.claro(0.25f), Offset(r.left, y + 1.6f * u), Size(r.width, 0.8f * u))
        }
        // El filo de la luz en el canto que la mira, de noche.
        if (a.lumbre && a.oscuro) {
            for (t in 0 until tramosV) {
                val y0 = r.top + r.height * t / tramosV; val y1 = r.top + r.height * (t + 1) / tramosV
                val kk = cerca(Offset(r.center.x, (y0 + y1) / 2))
                val lampDer = r.center.x / w < MuebleEstanteNapoleon.LAMP_X
                drawRect(a.filo(0.6f * kk), Offset(if (lampDer) r.right - 1.5f * u else r.left, y0), Size(1.5f * u, y1 - y0))
            }
        }
        // Las cantoneras de cada caja: arriba y abajo, en la esquina de fuera.
        val ex = if (izq) r.left else r.right
        val dx = if (izq) 1f else -1f
        val br = r.width * 0.62f; val gr = w * 0.011f
        cortes.zipWithNext().forEach { (y0, y1) ->
            cantonera(Offset(ex, y0 + 1.5f * u), br, gr, dx, 1f, a, cerca(Offset(ex, y0)))
            cantonera(Offset(ex, y1 - 1.5f * u), br, gr, dx, -1f, a, cerca(Offset(ex, y1)))
        }
        // Las bisagras de la puerta que se quitó para el viaje, en el costado de la izquierda: dos por caja, con su nudo y el
        // agujero vacío del pasador.
        if (izq) {
            cortes.zipWithNext().forEachIndexed { j, (y0, y1) ->
                listOf(0.2f, 0.8f).forEach { f ->
                    val y = y0 + (y1 - y0) * f
                    val kk = cerca(Offset(r.right, y))
                    val hb = Rect(r.right - r.width * 0.3f, y - w * 0.018f, r.right - 1f * u, y + w * 0.018f)
                    drawRect(a.sombra(0.4f), hb.topLeft + Offset(1f * u, 1.2f * u), hb.size)
                    drawRect(a.luz(P.LATON, kk), hb.topLeft, hb.size)
                    drawRect(a.brillo(0.45f, kk), hb.topLeft, Size(hb.width, 0.8f * u))
                    drawRect(a.luz(P.LATON_OSC, kk), Offset(hb.right - 2.6f * u, hb.top), Size(2.6f * u, hb.height))
                    drawRect(a.brillo(0.4f, kk), Offset(hb.right - 2.2f * u, hb.top), Size(0.8f * u, hb.height))
                    listOf(0.28f, 0.72f).forEach { g -> drawCircle(a.luz(P.LATON_OSC, kk), 1f * u, Offset(hb.left + hb.width * 0.4f, hb.top + hb.height * g)) }
                    if ((j + (f * 10).toInt()) % 3 == 0) drawCircle(a.sombra(0.7f), 0.9f * u, Offset(hb.right - 1.3f * u, hb.top - 1.2f * u))
                }
            }
        }
        // Los golpes y rozaduras del viaje.
        for (j in 0 until (r.height / (w * 0.2f)).toInt().coerceIn(1, 40)) {
            val y = r.top + r.height * azar(7900 + (if (izq) 0 else 1), j)
            val x = r.left + r.width * (0.2f + 0.5f * azar(7901, j))
            drawLine(a.claro(0.16f), Offset(x, y), Offset(x + w * 0.01f, y + w * 0.012f), 0.8f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- los portaetiquetas de los meses

    /** El portaetiquetas de latón de un mes, con su tarjeta. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireNapoleon, k: Float) {
        val u = density
        val r0 = p.r
        val r = Rect(r0.left + r0.width * 0.04f, r0.top - 1f * u, r0.right - r0.width * 0.04f, r0.bottom + 1f * u)
        val ore = r.height * 0.55f
        // Las orejas con su tornillo, la sombra del marco en la madera y el marco con su bisel.
        listOf(r.left - ore, r.right).forEach { x ->
            val o = Rect(x, r.top + r.height * 0.22f, x + ore, r.bottom - r.height * 0.22f)
            drawRect(a.sombra(0.4f), o.topLeft + Offset(1f * u, 1.2f * u), o.size)
            drawRect(a.luz(P.LATON, k), o.topLeft, o.size)
            drawRect(a.brillo(0.4f, k), o.topLeft, Size(o.width, 0.8f * u))
            clavoNapoleon(o.center, 1.3f * u, a, k, P.LATON_OSC)
        }
        drawRect(a.sombra(0.45f), r.topLeft + Offset(1.4f * u, 1.8f * u), r.size)
        drawRect(a.luz(P.LATON_OSC, k), r.topLeft, r.size)
        drawRect(a.luz(P.LATON, k), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, r.height - 1.6f * u))
        drawRect(a.brillo(0.55f, k), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, 0.9f * u))
        val t = r.deflate(2.6f * u)
        drawRect(a.luz(P.LATON_OSC, k), t.topLeft - Offset(0.6f * u, 0.6f * u), Size(t.width + 1.2f * u, t.height + 1.2f * u))
        drawRect(a.etiqueta(P.CARTULINA, k), t.topLeft, t.size)
        drawRect(a.sombra(0.22f), t.topLeft, Size(t.width, t.height * 0.2f))
        drawRect(a.etiqueta(P.CARTULINA_OSC, k).copy(alpha = 0.35f), Offset(t.left, t.bottom - t.height * 0.15f), Size(t.width, t.height * 0.15f))
        val tinta = if (a.oscuro) Color(0xFF140C06) else P.TINTA
        textoEn(medidor, p.texto, Rect(t.left + 3f * u, t.top + 0.6f * u, t.right - 3f * u, t.bottom), P.LETRA_TITULO, tinta, 11f, espaciado = 0.8f)
    }

    // ---------------------------------------------------------------- la caja-zócalo

    /** El zócalo de [r] (de debajo de la última tabla hasta el suelo): la caja con su bocel, su friso flameado y las patas. */
    fun DrawScope.zocalo(r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val pata = w * 0.034f
        val caja = Rect(r.left, r.top, r.right, r.bottom - pata)
        val n = tramos(a, w, w)
        val bocel = caja.top + w * 0.014f
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, caja.center.y))
            drawRect(a.luz(P.CAOBA, k), Offset(x0, caja.top), Size(x1 - x0, caja.height))
            drawRect(a.dia(P.CAOBA_LUZ, -0.05f, k), Offset(x0, caja.top), Size(x1 - x0, (bocel - caja.top) * 0.5f))
            drawRect(a.dia(P.CAOBA_OSC, 0f, k), Offset(x0, caja.top + (bocel - caja.top) * 0.5f), Size(x1 - x0, (bocel - caja.top) * 0.5f))
            // Lo de debajo de la caja, entre las patas: la sombra honda.
            drawRect(a.luz(P.CAOBA_HONDO, k * 0.4f), Offset(x0, caja.bottom), Size(x1 - x0, r.bottom - caja.bottom + 1f))
        }
        val kc = cerca(caja.center)
        flameado(Rect(w * 0.06f, bocel + w * 0.006f, w * 0.94f, caja.bottom - w * 0.008f), a.dia(P.CAOBA_LLAMA, 0.06f, kc).copy(alpha = 0.5f), 7950)
        drawRect(a.brillo(0.5f, kc), Offset(0f, caja.top), Size(w, 1.2f * u))
        drawRect(a.sombra(0.45f), Offset(0f, bocel), Size(w, 1.4f * u))
        val fl = Rect(w * 0.085f, bocel + w * 0.012f, w * 0.915f, caja.bottom - w * 0.012f)
        drawRect(a.luz(P.LATON, kc), fl.topLeft, fl.size, style = Stroke(1.2f * u))
        drawRect(a.sombra(0.5f), Offset(0f, caja.bottom - 2f * u), Size(w, 2f * u))
        // La sombra de la última caja en el zócalo.
        drawRect(a.sombra(0.4f), Offset(0f, caja.top), Size(w, 2.6f * u))
        // Las cantoneras de la caja-zócalo.
        val br = w * 0.05f; val gr = w * 0.011f
        cantonera(Offset(0f, caja.top + 2f * u), br, gr, 1f, 1f, a, cerca(Offset(0f, caja.top)))
        cantonera(Offset(w, caja.top + 2f * u), br, gr, -1f, 1f, a, cerca(Offset(w, caja.top)))
        cantonera(Offset(0f, caja.bottom - 1f * u), br, gr, 1f, -1f, a, cerca(Offset(0f, caja.bottom)))
        cantonera(Offset(w, caja.bottom - 1f * u), br, gr, -1f, -1f, a, cerca(Offset(w, caja.bottom)))
        // Las patas de bollo torneadas: tres a la vista, cada una con su anillo, su luz y su sombra en el suelo.
        listOf(0.07f, 0.5f, 0.93f).forEach { f ->
            val cx = w * f
            val k = cerca(Offset(cx, r.bottom))
            val pw = w * 0.07f
            val ap = a.dia(P.CAOBA, 0f, k)
            drawOval(a.sombra(0.5f), Offset(cx - pw * 0.55f, r.bottom - pata * 0.2f), Size(pw * 1.3f, pata * 0.45f))
            drawRect(ap, Offset(cx - pw * 0.4f, caja.bottom - 1f), Size(pw * 0.8f, pata * 0.24f))
            drawOval(ap, Offset(cx - pw / 2, caja.bottom + pata * 0.16f), Size(pw, pata * 0.86f))
            clipPath(Path().apply { addOval(Rect(cx - pw / 2, caja.bottom + pata * 0.16f, cx + pw / 2, r.bottom + pata * 0.02f)) }) {
                cilindroNapoleon(Rect(cx - pw / 2, caja.bottom, cx + pw / 2, r.bottom), a, k, if (a.oscuro && cx / w < MuebleEstanteNapoleon.FAROL_X) 1f else -1f, 1.2f)
            }
            drawRect(a.dia(P.LATON, 0f, k), Offset(cx - pw * 0.42f, caja.bottom + pata * 0.12f), Size(pw * 0.84f, 1.6f * u))
            drawRect(a.sombra(0.4f), Offset(cx - pw * 0.4f, caja.bottom + pata * 0.12f + 1.6f * u), Size(pw * 0.8f, 0.8f * u))
        }
    }
}
