package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.contornoIrregular
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sin

/**
 * El armario de olivo del estante de Arcontes ([MuebleEstanteArcontes]), hecho por un carpintero de la comunidad con el oficio
 * de las arquetas del Ideario: la cornisa de gola (el caveto que vuela, el bocel y el friso de muescas a punta de cuchillo, con
 * sus esquinas de bronce), los costados de tablón con su ranura y una chapa de bronce clavada donde entra cada tabla, las tablas
 * gruesas con su bocel, el rótulo de cada mes en una tira de papiro atada con cordel y, abajo, el zócalo de sillares de caliza
 * labrados a la manera herodiana. No tiene fondo: entre las tablas se ve la roca de la cueva ([RocaEstanteArcontes.fondoHueco]).
 */
internal object ObraEstanteArcontes {
    private val P = PaletaArcontes

    // ---------------------------------------------------------------- la veta del olivo, en baldosas

    private var densidadVeta = -1f
    private var vetaH: ImageBitmap? = null
    private var vetaV: ImageBitmap? = null

    /**
     * La veta del olivo en una baldosa neutra (sombra violeta y luz crema), pintada una vez por densidad: líneas onduladas que se
     * arremolinan alrededor de dos nudos y cierran con la baldosa de al lado. [vertical] la pinta girada, para los costados.
     */
    private fun pintarVeta(u: Float, vertical: Boolean): ImageBitmap {
        val largo = 220f * u; val corto = 64f * u
        val img = ImageBitmap(ceil(if (vertical) corto else largo).toInt().coerceAtLeast(1), ceil(if (vertical) largo else corto).toInt().coerceAtLeast(1))
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(if (vertical) corto else largo, if (vertical) largo else corto)) {
            val nudos = listOf(Offset(largo * 0.27f, corto * 0.42f), Offset(largo * 0.74f, corto * 0.66f))
            val hondas = Path(); val claras = Path()
            var y0 = -2f * u; var j = 0
            while (y0 < corto + 2f * u && j < 60) {
                val fase = azar(7501, j) * 6.28f
                val p = if (j % 4 == 3) claras else hondas
                val pasos = 44
                for (i in 0..pasos) {
                    val x = largo * i / pasos
                    var y = y0 + 1.6f * u * sin(fase + 6.2832f * 2 * i / pasos) + 0.8f * u * sin(fase * 2f + 6.2832f * 5 * i / pasos)
                    for (n in nudos) {
                        val dx = (x - n.x) / (18f * u)
                        val lado = if (y0 < n.y) -1f else 1f
                        val cerca = exp(-dx * dx) * exp(-((y0 - n.y) / (14f * u)).let { it * it })
                        y += lado * 9f * u * cerca
                    }
                    val q = if (vertical) Offset(y, x) else Offset(x, y)
                    if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
                }
                y0 += (2.2f + 2.6f * azar(7502, j)) * u; j++
            }
            // La veta parda del olivo, marcada (sobre el olivo miel, para que el mueble tenga su dibujo), y alguna línea de luz.
            drawPath(hondas, P.OLIVO_VETA.copy(alpha = 0.5f), style = Stroke(0.8f * u))
            drawPath(claras, LUZ_MUEBLE.copy(alpha = 0.22f), style = Stroke(0.9f * u))
            // Los nudos: anillos y el corazón oscuro.
            for (n in nudos) {
                val c = if (vertical) Offset(n.y, n.x) else n
                for (a in 1..3) {
                    val rx = a * 3.2f * u; val ry = a * 1.3f * u
                    val (ax, ay) = if (vertical) ry to rx else rx to ry
                    drawOval(P.OLIVO_VETA.copy(alpha = 0.5f - 0.08f * a), Offset(c.x - ax, c.y - ay), Size(2 * ax, 2 * ay), style = Stroke(0.7f * u))
                }
                val (bx, by) = if (vertical) 1.2f * u to 2.8f * u else 2.8f * u to 1.2f * u
                drawOval(P.OLIVO_HONDO.copy(alpha = 0.75f), Offset(c.x - bx, c.y - by), Size(2 * bx, 2 * by))
            }
        }
        return img
    }

    /** Cuánto se ve la veta parda: de noche, poco (lejos de la llama el olivo se hunde en el azul violáceo). La pone el estante. */
    var vetaNoche = 1f

    /** La veta sobre [r], en mosaico, empezando en [desde] (para que cada pieza tenga la suya). */
    fun DrawScope.veta(r: Rect, vertical: Boolean, fuerza: Float, desde: Float = 0f) {
        if (r.width < 1f || r.height < 1f) return
        val u = density
        if (densidadVeta != u) { vetaH = pintarVeta(u, false); vetaV = pintarVeta(u, true); densidadVeta = u }
        val b = (if (vertical) vetaV else vetaH) ?: return
        val a = (fuerza * vetaNoche).coerceIn(0f, 1f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top - (if (vertical) desde % b.height else 0f)
            while (y < r.bottom) {
                var x = r.left - (if (vertical) 0f else desde % b.width)
                while (x < r.right) { drawImage(b, Offset(x, y), alpha = a); x += b.width }
                y += b.height
            }
        }
    }

    // ---------------------------------------------------------------- la cornisa

    /** El alto de cada parte de la cornisa, en anchos, de arriba abajo: la tapa, el caveto, el bocel, el friso y el filete. */
    private const val TAPA = 0.014f
    private const val CAVETO = 0.04f
    private const val BOCEL = 0.014f
    private const val FRISO = 0.035f

    /**
     * La cornisa en [r] (desde donde pisan las cosas hasta el arranque de las repisas): la tapa vista desde arriba, el caveto que
     * vuela con su luz arriba y su sombra debajo, el bocel, el friso de muescas a punta de cuchillo con sus esquinas y su chapa de
     * bronce, y el filete; con la sombra que echa en la pared y la mancha del sol que se quiebra en cada moldura.
     */
    fun DrawScope.cornisa(r: Rect, s: SitioArcontes, wc: Float) {
        val w = size.width; val u = density
        // Sus altos y sus adornos, a la medida de un teléfono grande ([wc]): en un estante ancho no crecen.
        val y0 = r.top - wc * TAPA
        val yc = r.top + wc * CAVETO
        val yb = yc + wc * BOCEL
        val yf = yb + wc * FRISO
        val ya = r.bottom
        // La sombra que la cornisa echa en la roca de los lados (por fuera de los costados) y el arranque de la pared.
        val t0 = s.t(Offset(w * 0.5f, r.top))
        drawRect(t0.s(0.22f), Offset(0f, y0 - 2f * u), Size(w, 3f * u))
        conManchaArcontes(s) {
            // La tapa: la cara de arriba, vista apenas, con su canto de luz.
            val x0t = w * 0.004f; val x1t = w * 0.996f
            drawRect(s.aLoAncho(x0t, x1t, y0, true) { it.c(P.OLIVO_LUZ, 0.05f) }, Offset(x0t, y0), Size(x1t - x0t, r.top - y0 + 1f))
            veta(Rect(x0t, y0, x1t, r.top), false, 0.35f, 17f)
            drawRect(s.aLoAncho(x0t, x1t, r.top, true) { it.b(0.7f) }, Offset(x0t, r.top - 1.2f * u), Size(x1t - x0t, 1.2f * u))
            // El caveto: vuela arriba y se mete abajo; arriba, el filo en luz; en su hueco, la sombra.
            val cav = Path().apply {
                moveTo(x0t, r.top); lineTo(x1t, r.top)
                quadraticTo(w * 0.988f, r.top + (yc - r.top) * 0.75f, w * 0.982f, yc)
                lineTo(w * 0.018f, yc)
                quadraticTo(w * 0.012f, r.top + (yc - r.top) * 0.75f, x0t, r.top)
                close()
            }
            val tc = s.t(Offset(w * 0.5f, (r.top + yc) / 2), true)
            drawPath(cav, s.aLoAncho(x0t, x1t, (r.top + yc) / 2, true) { it.c(P.OLIVO, 0.02f) })
            clipPath(cav) {
                veta(Rect(x0t, r.top, x1t, yc), false, 0.4f, 63f)
                drawRect(s.aLoAncho(x0t, x1t, r.top, true) { it.c(P.OLIVO_LUZ, 0.1f) }, Offset(x0t, r.top), Size(x1t - x0t, (yc - r.top) * 0.22f))
                drawRect(tc.s(0.16f), Offset(x0t, r.top + (yc - r.top) * 0.5f), Size(x1t - x0t, (yc - r.top) * 0.2f))
                drawRect(tc.s(0.34f), Offset(x0t, r.top + (yc - r.top) * 0.7f), Size(x1t - x0t, (yc - r.top) * 0.3f + 1f))
            }
            drawLine(tc.b(0.5f), Offset(x0t, r.top + 0.6f * u), Offset(x1t, r.top + 0.6f * u), 1f * u)
            // El bocel: un cordón redondo, con su luz arriba y su sombra abajo.
            val xb0 = w * 0.016f; val xb1 = w * 0.984f
            drawRect(s.aLoAncho(xb0, xb1, yc, true) { it.c(P.OLIVO, -0.05f) }, Offset(xb0, yc), Size(xb1 - xb0, yb - yc))
            drawRect(s.aLoAncho(xb0, xb1, yc, true) { it.c(P.OLIVO_LUZ, 0.12f) }, Offset(xb0, yc + (yb - yc) * 0.15f), Size(xb1 - xb0, (yb - yc) * 0.3f))
            drawRect(tc.s(0.32f), Offset(xb0, yb - (yb - yc) * 0.3f), Size(xb1 - xb0, (yb - yc) * 0.3f))
            drawRect(tc.s(0.45f), Offset(xb0, yb - 1f * u), Size(xb1 - xb0, 1.4f * u))
            // El friso: la tabla lisa con las muescas a punta de cuchillo (como el frente de las arquetas).
            val xf0 = w * 0.02f; val xf1 = w * 0.98f
            drawRect(s.aLoAncho(xf0, xf1, (yb + yf) / 2, true) { it.c(P.OLIVO) }, Offset(xf0, yb), Size(xf1 - xf0, yf - yb + 1f))
            veta(Rect(xf0, yb, xf1, yf), false, 0.45f, 131f)
            muescas(xf0 + wc * 0.05f, xf1 - wc * 0.05f, yb + (yf - yb) * 0.16f, (yf - yb) * 0.3f, s, false)
            muescas(xf0 + wc * 0.05f, xf1 - wc * 0.05f, yf - (yf - yb) * 0.16f, (yf - yb) * 0.3f, s, true)
            drawLine(tc.s(0.28f), Offset(xf0 + wc * 0.05f, (yb + yf) / 2), Offset(xf1 - wc * 0.05f, (yb + yf) / 2), 0.7f * u)
            drawLine(tc.b(0.3f), Offset(xf0 + wc * 0.05f, (yb + yf) / 2 + 0.8f * u), Offset(xf1 - wc * 0.05f, (yb + yf) / 2 + 0.8f * u), 0.5f * u)
            // Las esquinas de bronce del friso y la chapa de en medio.
            esquinaBronce(Offset(xf0, yb), wc * 0.045f, yf - yb, true, s)
            esquinaBronce(Offset(xf1, yb), wc * 0.045f, yf - yb, false, s)
            chapa(Rect(w * 0.5f - wc * 0.03f, yb + (yf - yb) * 0.22f, w * 0.5f + wc * 0.03f, yf - (yf - yb) * 0.22f), s)
            // El filete de abajo, retranqueado, y la sombra del friso sobre él.
            drawRect(s.aLoAncho(xf0, xf1, ya, true) { it.c(P.OLIVO_OSC) }, Offset(xf0, yf), Size(xf1 - xf0, ya - yf + 2f))
            drawRect(tc.s(0.4f), Offset(xf0, yf), Size(xf1 - xf0, 2f * u))
            drawRect(s.aLoAncho(xf0, xf1, ya, true) { it.b(0.3f) }, Offset(xf0, yf + (ya - yf) * 0.45f), Size(xf1 - xf0, 0.8f * u))
            // Los cantos de los extremos: el de la izquierda mira a la boca; el de la derecha, en sombra.
            drawRect(tc.b(0.35f), Offset(x0t, r.top), Size(1.2f * u, yc - r.top))
            drawRect(tc.s(0.35f), Offset(x1t - 1.4f * u, r.top), Size(1.4f * u, yc - r.top))
        }
    }

    /** Una fila de muescas a punta de cuchillo: triángulos alternos en sombra y en luz, hacia abajo (o hacia arriba, [arriba]). */
    private fun DrawScope.muescas(x0: Float, x1: Float, y: Float, alto: Float, s: SitioArcontes, arriba: Boolean) {
        val hondas = Path(); val claras = Path()
        var x = x0; var j = 0
        val paso = alto * 0.62f
        while (x + alto < x1 && j < 400) {
            val p = if (j % 2 == 0) hondas else claras
            if (!arriba) { p.moveTo(x, y); p.lineTo(x + alto * 0.5f, y + alto); p.lineTo(x + alto, y); p.close() }
            else { p.moveTo(x, y); p.lineTo(x + alto * 0.5f, y - alto); p.lineTo(x + alto, y); p.close() }
            x += paso; j++
        }
        val t = s.t(Offset((x0 + x1) / 2, y), true)
        drawPath(hondas, t.s(0.42f))
        drawPath(claras, t.b(0.4f))
    }

    /** La chapa de bronce con cuatro clavos, con su cardenillo en las juntas. */
    fun DrawScope.chapa(r: Rect, s: SitioArcontes) {
        val u = density
        val t = s.t(r.center, true)
        drawRect(t.s(0.4f), r.topLeft + Offset(1f * u, 1.2f * u), r.size)
        drawRect(Brush.verticalGradient(listOf(t.c(P.BRONCE_LUZ), t.c(P.BRONCE), t.c(P.BRONCE_OSC)), r.top, r.bottom), r.topLeft, r.size)
        drawRect(t.c(P.CARDENILLO).copy(alpha = 0.45f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width * 0.45f, r.height * 0.22f))
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.8f * u))
        val m = minOf(r.width, r.height) * 0.2f
        listOf(Offset(r.left + m, r.top + m), Offset(r.right - m, r.bottom - m)).forEach {
            clavoArcontes(it, minOf(1.3f * u, m * 0.7f), t, P.BRONCE_OSC)
        }
    }

    /** La esquina de bronce en ángulo, clavada, en [e] (la esquina de arriba del friso), hacia dentro. */
    private fun DrawScope.esquinaBronce(e: Offset, largo: Float, alto: Float, izquierda: Boolean, s: SitioArcontes) {
        val u = density
        val sx = if (izquierda) 1f else -1f
        val t = s.t(e + Offset(sx * largo / 2, alto / 2), true)
        val p = Path().apply {
            moveTo(e.x, e.y); lineTo(e.x + sx * largo, e.y); lineTo(e.x + sx * largo * 0.55f, e.y + alto * 0.42f)
            lineTo(e.x + sx * alto * 0.42f, e.y + alto * 0.42f); lineTo(e.x + sx * alto * 0.42f, e.y + alto); lineTo(e.x, e.y + alto); close()
        }
        translate(1f * u, 1.2f * u) { drawPath(p, t.s(0.4f)) }
        drawPath(p, Brush.linearGradient(listOf(t.c(P.BRONCE_LUZ), t.c(P.BRONCE), t.c(P.BRONCE_OSC)), e, e + Offset(sx * largo, alto)))
        drawPath(p, t.c(P.BRONCE_OSC), style = Stroke(0.6f * u))
        clavoArcontes(e + Offset(sx * alto * 0.2f, alto * 0.22f), 1.2f * u, t, P.BRONCE_OSC)
        clavoArcontes(e + Offset(sx * largo * 0.55f, alto * 0.2f), 1.1f * u, t, P.BRONCE_OSC)
        clavoArcontes(e + Offset(sx * alto * 0.2f, alto * 0.75f), 1.1f * u, t, P.BRONCE_OSC)
    }

    // ---------------------------------------------------------------- cada hueco

    /** Las caras de dentro de un hueco, en anchos: la tabla de arriba vista por debajo, los cantos de los costados y la tabla de abajo vista desde arriba. */
    const val TECHO = 0.022f
    const val CANTO = 0.024f
    const val PISO = 0.018f

    /**
     * Un hueco con su hondura: al fondo, la roca de la cueva; arriba, la tabla de encima vista por debajo; a los lados, el canto
     * de dentro de cada costado (el de la derecha mira a la boca y recibe su luz de día); abajo, la tabla vista desde arriba.
     */
    fun DrawScope.hueco(k: Int, r: Rect, d: DisposicionEstante, s: SitioArcontes) {
        val w = size.width; val u = density
        val techo = w * TECHO; val canto = w * CANTO; val piso = w * PISO
        with(RocaEstanteArcontes) { fondoHueco(k, r, s, techo, d.ocupado.getOrNull(k) ?: r.left) }
        val tc = s.t(r.center, true)
        // La tabla de arriba, por debajo: olivo en sombra.
        val arriba = poligonoArcontes(r.left - 1f, r.top - 1f, r.right + 1f, r.top - 1f, r.right - canto, r.top + techo, r.left + canto, r.top + techo)
        drawPath(arriba, s.aLoAncho(r.left, r.right, r.top, true) { it.c(P.OLIVO_HONDO, -0.1f) })
        drawLine(tc.s(0.4f), Offset(r.left + canto, r.top + techo), Offset(r.right - canto, r.top + techo), 1f * u)
        // Los cantos de dentro de los costados: el de la izquierda mira a la derecha (en sombra de día); el de la derecha, a la boca.
        val izq = poligonoArcontes(r.left - 1f, r.top - 1f, r.left + canto, r.top + techo, r.left + canto, r.bottom - piso, r.left - 1f, r.bottom + 1f)
        val der = poligonoArcontes(r.right + 1f, r.top - 1f, r.right + 1f, r.bottom + 1f, r.right - canto, r.bottom - piso, r.right - canto, r.top + techo)
        val ti = s.t(Offset(r.left, r.center.y), true); val td = s.t(Offset(r.right, r.center.y), true)
        drawPath(izq, s.aLoAlto(r.top, r.bottom, r.left + canto / 2, true) { it.c(P.OLIVO_OSC, if (it.a.oscuro) 0f else -0.18f) })
        drawPath(der, s.aLoAlto(r.top, r.bottom, r.right - canto / 2, true) { it.c(P.OLIVO, if (it.a.oscuro) 0f else 0.06f) })
        veta(Rect(r.left, r.top + techo, r.left + canto, r.bottom - piso), true, 0.3f, 31f * k)
        veta(Rect(r.right - canto, r.top + techo, r.right, r.bottom - piso), true, 0.35f, 47f * k)
        drawLine(ti.s(0.3f), Offset(r.left + canto, r.top + techo), Offset(r.left + canto, r.bottom - piso), 0.8f * u)
        drawLine(td.b(0.4f), Offset(r.right - canto, r.top + techo), Offset(r.right - canto, r.bottom - piso), 0.8f * u)
        // De noche, la lumbre de la llama en el canto que la mira (el filo).
        if (s.a.oscuro) {
            drawLine(ti.filo(0.35f), Offset(r.left + canto - 1f * u, r.top + techo), Offset(r.left + canto - 1f * u, r.bottom - piso), 1.2f * u)
            drawLine(td.filo(0.35f), Offset(r.right - canto + 1f * u, r.top + techo), Offset(r.right - canto + 1f * u, r.bottom - piso), 1.2f * u)
        }
        // La tabla de abajo, vista desde arriba: el olivo gastado por lo que se saca y se pone, con su veta.
        val suelo = poligonoArcontes(r.left - 1f, r.bottom + 1f, r.left + canto, r.bottom - piso, r.right - canto, r.bottom - piso, r.right + 1f, r.bottom + 1f)
        drawPath(suelo, s.aLoAncho(r.left, r.right, r.bottom, true) { it.c(P.OLIVO_LUZ, if (it.a.oscuro) 0f else -0.04f) })
        veta(Rect(r.left + canto, r.bottom - piso, r.right - canto, r.bottom), false, 0.35f, 71f * k)
        drawLine(tc.s(0.3f), Offset(r.left + canto, r.bottom - piso), Offset(r.right - canto, r.bottom - piso), 0.8f * u)
        // La sombra de la tabla de arriba en el fondo, más larga a la derecha (la luz baja desde la izquierda).
        if (!s.a.lumbre) drawPath(poligonoArcontes(r.left + canto, r.top + techo, r.right - canto, r.top + techo, r.right - canto, r.top + techo + w * 0.03f,
            r.left + canto, r.top + techo + w * 0.012f), tc.s(0.18f))
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla de debajo de cada hueco, de frente: olivo grueso, el bocel de arriba que recibe la luz, la veta y los clavos de las puntas. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, s: SitioArcontes) {
        val u = density
        val tc = s.t(r.center, true)
        val f = Rect(r.left - 1f, r.top, r.right + 1f, r.bottom + 1f)
        drawRect(s.aLoAncho(f.left, f.right, r.center.y, true) { it.c(P.OLIVO) }, f.topLeft, f.size)
        veta(f, false, 0.5f, 97f * k + 13f)
        val bocel = r.height * 0.28f
        drawRect(s.aLoAncho(f.left, f.right, r.center.y, true) { it.c(P.OLIVO_LUZ, 0.08f) }, Offset(f.left, r.top), Size(f.width, bocel))
        drawRect(tc.s(0.3f), Offset(f.left, r.top + bocel), Size(f.width, 1.2f * u))
        drawRect(s.aLoAncho(f.left, f.right, r.center.y, true) { it.b(0.6f) }, Offset(f.left, r.top + bocel * 0.25f), Size(f.width, 0.9f * u))
        drawRect(tc.s(0.38f), Offset(f.left, r.bottom - 2f * u), Size(f.width, 2f * u))
        // Gastado: rozones claros en el canto, donde se apoyan las manos.
        for (j in 0 until 4) {
            val x = r.left + r.width * (0.1f + 0.8f * azar(7601 + k, j))
            drawRect(tc.b(0.16f), Offset(x, r.top + bocel + 1.5f * u), Size(r.width * (0.03f + 0.05f * azar(7602 + k, j)), 1.4f * u))
        }
        // Los clavos de madera de las puntas, que la sujetan a los costados.
        listOf(r.left + 6f * u, r.right - 6f * u).forEach { x ->
            val p = Offset(x, r.center.y + bocel * 0.4f)
            val t = s.t(p, true)
            drawCircle(t.s(0.45f), 2.2f * u, p + Offset(0.6f * u, 0.8f * u))
            drawCircle(t.c(P.OLIVO_OSC, -0.1f), 2f * u, p)
            drawCircle(t.b(0.3f), 0.8f * u, p - Offset(0.6f * u, 0.6f * u))
        }
    }

    /** El rótulo del mes: una tira de papiro de borde deshilachado atada a la tabla con dos vueltas de cordel de lino. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, s: SitioArcontes) {
        val u = density
        val r = p.r
        val t = s.t(r.center, true)
        val tam = 8.6f
        val ancho = (capaEn(medidor, p.texto, Rect(0f, 0f, 4000f, r.height + 4f * u), P.LETRA, t.tinta(), tam, peso = FontWeight.Bold)?.size?.width?.toFloat() ?: r.width) + 26f * u
        val an = ancho.coerceIn(r.width * 0.5f, r.width * 1.5f)
        val tira = Rect(r.center.x - an / 2, r.top - 1f * u, r.center.x + an / 2, r.bottom + 1f * u)
        val forma = contornoIrregular(tira, 83 + p.texto.length, 44, 0.022f)
        translate(1f * u, 1.4f * u) { drawPath(forma, t.s(0.4f)) }
        drawPath(forma, t.etiqueta(P.PAPIRO_ETIQUETA))
        val fibras = Path()
        var yf = tira.top + 1.4f * u; var jf = 0
        while (yf < tira.bottom - 1f * u && jf < 20) {
            fibras.moveTo(tira.left + 1.5f * u, yf); fibras.lineTo(tira.right - 1.5f * u, yf + (azar(7701, jf) - 0.5f) * u)
            yf += 1.7f * u; jf++
        }
        drawPath(fibras, P.FIBRA.copy(alpha = 0.18f), style = Stroke(0.5f * u))
        drawPath(forma, P.FIBRA.copy(alpha = 0.5f), style = Stroke(0.6f * u))
        // El cordel: dos vueltas a cada punta, por encima y por debajo de la tabla.
        val cordel = t.c(P.CORDEL)
        listOf(tira.left + 4.5f * u, tira.right - 4.5f * u).forEach { x ->
            for (v in 0 until 2) {
                val xx = x + v * 1.8f * u
                drawLine(t.s(0.4f), Offset(xx + 0.5f * u, r.top - 3f * u), Offset(xx + 1.1f * u, r.bottom + 3.5f * u), 1.5f * u, StrokeCap.Round)
                drawLine(cordel, Offset(xx, r.top - 3f * u), Offset(xx + 0.6f * u, r.bottom + 3.5f * u), 1.2f * u, StrokeCap.Round)
                drawLine(t.b(0.4f), Offset(xx - 0.3f * u, r.top - 2f * u), Offset(xx + 0.2f * u, r.bottom + 2f * u), 0.4f * u)
            }
        }
        textoEn(medidor, p.texto, Rect(tira.left + 10f * u, tira.top - 2f * u, tira.right - 10f * u, tira.bottom + 2f * u), P.LETRA, t.tinta(), tam, peso = FontWeight.Bold, minimo = 5f)
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Un costado ([izquierda] o el de la derecha) en [r]: por fuera, la roca de la cueva que asoma; el tablón de olivo con su
     * veta, su ranura, el canto de fuera y el de dentro, y en cada tabla, la chapa de bronce clavada y las cabezas de las clavijas.
     */
    fun DrawScope.costado(r: Rect, izquierda: Boolean, d: DisposicionEstante, s: SitioArcontes) {
        val w = size.width; val u = density
        val roca = w * MuebleEstanteArcontes.ROCA_LADO
        // La roca de fuera: debajo, todo el lado del color de la roca honda (así la junta con el costado no deja asomar nada) y,
        // encima, el borde de la cueva.
        val x0 = if (izquierda) roca else r.left
        val x1 = if (izquierda) r.right else w - roca
        val lado = if (izquierda) Rect(0f, r.top, x1, r.bottom) else Rect(x0, r.top, w, r.bottom)
        drawRect(s.aLoAlto(lado.top, lado.bottom, lado.center.x) { it.c(mezclaRapida(P.CALIZA_SOMBRA, P.ROCA, 0.7f), -0.2f) }, lado.topLeft, lado.size)
        with(RocaEstanteArcontes) { bordes(s, r.top - 2f, r.bottom + 2f, listOf(!izquierda)) }
        val f = Rect(x0, r.top - 1f, x1, r.bottom + 1f)
        // La sombra que el costado de la derecha echa en la roca de fuera.
        if (!izquierda) drawRect(s.t(Offset(x1, r.center.y)).s(0.3f), Offset(x1, r.top), Size(minOf(roca, 4f * u), r.height))
        drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.c(P.OLIVO, if (izquierda) 0.03f else -0.04f) }, f.topLeft, f.size)
        veta(f, true, 0.5f, if (izquierda) 23f else 151f)
        // La ranura a lo largo: el surco en sombra y su labio en luz.
        val xr = f.left + f.width * (if (izquierda) 0.36f else 0.64f)
        drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.s(0.4f) }, Offset(xr - 1.1f * u, f.top), Size(2.2f * u, f.height))
        drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.b(0.4f) }, Offset(xr + 1.1f * u, f.top), Size(0.8f * u, f.height))
        // Los cantos: el que mira a la boca (a la izquierda), en luz; el otro, en sombra.
        drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.b(0.55f) }, Offset(f.left, f.top), Size(1.3f * u, f.height))
        drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.s(0.4f) }, Offset(f.right - 1.6f * u, f.top), Size(1.6f * u, f.height))
        if (s.a.oscuro) drawRect(s.aLoAlto(f.top, f.bottom, f.center.x, true) { it.filo(0.4f) },
            Offset(if (izquierda) f.right - 1.4f * u else f.left, f.top), Size(1.4f * u, f.height))
        // En cada tabla: la chapa de bronce clavada sobre la junta y las cabezas de las dos clavijas.
        d.tablas.forEach { tb ->
            val ch = Rect(f.left + f.width * 0.18f, tb.top - tb.height * 0.2f, f.right - f.width * 0.18f, tb.bottom + tb.height * 0.2f)
            chapa(ch, s)
            listOf(tb.top - tb.height * 0.75f, tb.bottom + tb.height * 0.75f).forEach { y ->
                val p = Offset(f.center.x, y)
                val t = s.t(p, true)
                drawCircle(t.s(0.4f), 1.9f * u, p + Offset(0.5f * u, 0.7f * u))
                drawCircle(t.c(P.OLIVO_OSC, -0.15f), 1.7f * u, p)
                drawCircle(t.b(0.3f), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
            }
        }
    }

    // ---------------------------------------------------------------- el zócalo

    /**
     * El zócalo en [r]: dos hiladas de sillares de caliza labrados a la manera herodiana (el margen liso picado alrededor y la
     * almohadilla en medio, un poco salida), con sus juntas, la cara de arriba donde pisa el armario y su sombra en el suelo.
     */
    fun DrawScope.zocalo(r: Rect, s: SitioArcontes, wc: Float) {
        val w = size.width; val u = density
        val hiladas = 2
        val alto = r.height / hiladas
        for (i in 0 until hiladas) {
            val y0 = r.top + alto * i; val y1 = y0 + alto
            var x = -wc * (0.05f + 0.1f * azar(7801, i)); var j = 0
            while (x < w && j < 60) {
                val largo = wc * (0.2f + 0.13f * azar(7802 + i, j))
                val b = Rect(x, y0, x + largo, y1)
                sillar(b, i * 31 + j, s, wc)
                x += largo; j++
            }
        }
        // La cara de arriba, donde descansa el armario: un filo de luz y, encima, la sombra del mueble.
        val t = s.t(Offset(w / 2, r.top))
        drawRect(t.s(0.5f), Offset(0f, r.top), Size(w, 2.4f * u))
        drawRect(s.aLoAncho(0f, w, r.top) { it.b(0.35f) }, Offset(0f, r.top + 2.4f * u), Size(w, 0.9f * u))
    }

    /**
     * Un sillar herodiano en [b]: el margen liso y más claro alrededor (labrado a cincel fino), la almohadilla picada y un poco
     * salida, de contorno desparejo (su filo de arriba en luz y su sombra abajo a la derecha), la junta honda y alguna esquina
     * saltada.
     */
    private fun DrawScope.sillar(b: Rect, semilla: Int, s: SitioArcontes, wc: Float) {
        val u = density
        val t = s.t(b.center)
        val margen = minOf(wc * 0.01f, b.height * 0.2f)
        val tono = mezclaRapida(P.CALIZA, P.CALIZA_SOMBRA, 0.12f + 0.22f * azar(7811, semilla))
        drawRect(s.aLoAncho(b.left, b.right, b.center.y) { it.c(tono, 0.07f) }, b.topLeft, Size(b.width + 1f, b.height + 1f))
        val al = Rect(b.left + margen, b.top + margen * 0.9f, b.right - margen, b.bottom - margen * 1.1f)
        if (al.width > 4f && al.height > 4f) {
            val radio = androidx.compose.ui.geometry.CornerRadius(margen * (0.6f + 0.6f * azar(7816, semilla)))
            drawRoundRect(t.s(0.35f), al.topLeft + Offset(1.4f * u, 1.6f * u), al.size, radio)
            drawRoundRect(t.c(tono, -0.02f), al.topLeft, al.size, radio)
            clipRect(al.left + 1f * u, al.top + 1f * u, al.right - 1f * u, al.bottom - 1f * u) {
                with(RocaEstanteArcontes) { textura(al, if (s.a.oscuro) 0.7f else 1f, semilla * 13f) }
                // El picado de la almohadilla, más bruto que el margen: golpes sueltos.
                val golpes = Path()
                for (j in 0 until (al.width * al.height / (u * u * 90f)).toInt().coerceIn(4, 26)) {
                    val p = Offset(al.left + al.width * azar(7812 + semilla, j), al.top + al.height * azar(7813 + semilla, j))
                    val a = 0.6f + 1.6f * azar(7817 + semilla, j)
                    golpes.moveTo(p.x, p.y); golpes.lineTo(p.x + kotlin.math.cos(a) * 2f * u, p.y + kotlin.math.sin(a) * 2f * u)
                }
                drawPath(golpes, t.s(0.22f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                drawRect(t.s(0.18f), Offset(al.left, al.bottom - al.height * 0.3f), Size(al.width, al.height * 0.3f))
            }
            drawRoundRect(t.b(0.35f), al.topLeft - Offset(0.6f * u, 0.6f * u), al.size, radio, style = Stroke(0.8f * u))
        }
        // La junta: honda, de grueso desparejo.
        drawRect(t.s(0.5f), Offset(b.left - 0.3f * u, b.top), Size(1.2f * u + 0.6f * u * azar(7818, semilla), b.height))
        drawRect(t.s(0.45f), Offset(b.left, b.bottom - 1.2f * u), Size(b.width, 1.2f * u))
        drawRect(t.b(0.25f), Offset(b.left, b.top), Size(b.width, 0.8f * u))
        if (azar(7814, semilla) > 0.55f) {
            val c = Offset(if (azar(7815, semilla) > 0.5f) b.left else b.right, b.top)
            val sx = if (c.x == b.left) 1f else -1f
            drawPath(poligonoArcontes(c.x, c.y, c.x + sx * margen * 2.2f, c.y, c.x, c.y + margen * 1.6f), t.c(tono, -0.25f))
        }
    }
}
