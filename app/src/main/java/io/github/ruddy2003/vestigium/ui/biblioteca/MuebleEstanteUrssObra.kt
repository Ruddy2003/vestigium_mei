package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La estantería de roble barnizado de la biblioteca del club: la cornisa escalonada en tres pasos con el friso (su filete negro y
 * su raya roja, y la chapita de inventario), los costados gruesos con su canto redondeado y los tornillos de latón de cada tabla,
 * las tablas con su canto, el portaetiquetas de latón de cada mes (el de los cajones del fichero del Ideario) y la lámpara de pinza
 * sujeta al costado izquierdo.
 */
internal object ObraEstanteUrss {
    /** Las bandas de la cornisa, en anchos desde arriba: la tapa vista desde arriba, su canto, los tres pasos, el friso y el bocel. */
    private const val TAPA0 = 0.468f
    private const val CANTO0 = 0.5f
    private const val PASO1 = 0.514f
    private const val PASO2 = 0.524f
    private const val PASO3 = 0.534f
    private const val FRISO0 = 0.542f
    private const val BOCEL0 = 0.598f

    /**
     * Una franja horizontal de roble a todo lo ancho de [x0] a [x1] y de [y0] a [y1], con la luz de cada punto a lo ancho, su veta y,
     * de día, más clara a la izquierda.
     */
    private fun DrawScope.franja(g: MarcoUrss, x0: Float, x1: Float, y0: Float, y1: Float, col: Color, delante: Float, veta: Float = 0.7f, mancha: Float? = null) {
        if (y1 - y0 < 0.3f || x1 - x0 < 1f) return
        val ym = (y0 + y1) / 2
        val cols = List(17) { j -> g.luz(x0 + (x1 - x0) * j / 16f, ym, delante, mancha).c(col) }
        drawRect(Brush.horizontalGradient(cols, x0, x1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        if (veta > 0f) drawRect(TexturasUrss.vetaH(density), Offset(x0, y0), Size(x1 - x0, y1 - y0), alpha = veta)
    }

    // ---------------------------------------------------------------- la cornisa (se pinta una vez, en la corona guardada)

    fun DrawScope.cornisa(g: MarcoUrss) {
        val w = g.w; val u = density
        val ch = g.ch
        val y = { f: Float -> w * f }
        conPanosUrss(g, 0.03f, Rect(0f, y(TAPA0) - w * 0.04f, w, ch)) { s ->
            // La sombra de la tapa en la pared, por encima (la luz viene de arriba: casi nada) y la cara de arriba, vista desde arriba.
            franja(g, 0f, w, y(TAPA0), y(CANTO0), mezcla(P.ROBLE, P.ROBLE_LUZ, 0.45f), 0.02f, 0.55f, s)
            // El barniz de la tapa: el reflejo largo de la luz y el cerco de una taza.
            val lt = g.luz(w * 0.3f, y(TAPA0), 0.02f, s)
            drawRect(lt.b(0.3f), Offset(w * 0.04f, y(TAPA0) + (y(CANTO0) - y(TAPA0)) * 0.45f), Size(w * 0.5f, 1.3f * u))
            drawRect(lt.s(0.3f), Offset(0f, y(TAPA0)), Size(w, 1.4f * u))
            drawOval(g.luz(w * 0.86f, y(TAPA0), 0.02f, s).s(0.18f), Offset(w * 0.845f, y(TAPA0) + w * 0.008f), Size(w * 0.05f, w * 0.02f), style = Stroke(1f * u))
            // El canto de la tapa: la arista de arriba redondeada (coge la luz) y la cara.
            franja(g, 0f, w, y(CANTO0), y(PASO1), P.ROBLE, 0.03f, 0.8f, s)
            drawRect(g.luz(w * 0.25f, y(CANTO0), 0.03f, s).b(0.75f), Offset(0f, y(CANTO0)), Size(w, 1.3f * u))
            // Los tres pasos: cada uno más metido, el de en medio de cara a la luz y los otros dos en sombra.
            val m1 = w * 0.006f; val m2 = w * 0.012f; val m3 = w * 0.018f
            franja(g, m1, w - m1, y(PASO1), y(PASO2), mezcla(P.ROBLE, P.ROBLE_OSC, 0.55f), 0.025f, 0.5f, s)
            franja(g, m2, w - m2, y(PASO2), y(PASO3), mezcla(P.ROBLE, P.ROBLE_LUZ, 0.25f), 0.022f, 0.6f, s)
            drawRect(g.luz(w * 0.25f, y(PASO2), 0.022f, s).b(0.55f), Offset(m2, y(PASO2)), Size(w - 2 * m2, 1f * u))
            franja(g, m3, w - m3, y(PASO3), y(FRISO0), mezcla(P.ROBLE, P.ROBLE_OSC, 0.45f), 0.02f, 0.5f, s)
            // El friso: el roble, con el filete negro y la raya roja que pintó el carpintero del club, gastados.
            franja(g, 0f, w, y(FRISO0), y(BOCEL0), P.ROBLE, 0.02f, 0.85f, s)
            drawRect(g.a.sombra(if (g.a.oscuro) 0.4f else 0.3f), Offset(0f, y(FRISO0)), Size(w, w * 0.006f))
            val fil0 = y(FRISO0) + (y(BOCEL0) - y(FRISO0)) * 0.66f
            val filN = List(17) { j -> g.luz(w * j / 16f, fil0, 0.02f, s).c(P.NEGRO) }
            drawRect(Brush.horizontalGradient(filN, 0f, w), Offset(0f, fil0), Size(w, w * 0.011f))
            val filR = List(17) { j -> g.luz(w * j / 16f, fil0, 0.02f, s).c(P.ROJO) }
            drawRect(Brush.horizontalGradient(filR, 0f, w), Offset(0f, fil0 - w * 0.009f), Size(w, w * 0.004f))
            // Lo gastado del filete: el roble que asoma donde rozan las manos.
            for (k in 0 until 9) {
                val x = w * (0.04f + 0.92f * azar(7401, k))
                drawRect(g.luz(x, fil0, 0.02f, s).c(P.ROBLE).copy(alpha = 0.75f), Offset(x, fil0 + w * 0.002f * azar(7402, k)), Size(w * (0.008f + 0.02f * azar(7403, k)), w * 0.005f))
            }
            // El bocel de abajo: medio redondo, con la luz arriba y la sombra abajo.
            franja(g, 0f, w, y(BOCEL0), ch, mezcla(P.ROBLE, P.ROBLE_LUZ, 0.2f), 0.03f, 0.5f, s)
            drawRect(g.luz(w * 0.3f, y(BOCEL0), 0.03f, s).b(0.6f), Offset(0f, y(BOCEL0) + 1f * u), Size(w, 1.2f * u))
            drawRect(g.a.sombra(0.35f), Offset(0f, ch - (ch - y(BOCEL0)) * 0.35f), Size(w, (ch - y(BOCEL0)) * 0.35f))
        }
        // La chapita de inventario, a la derecha del friso, con sus dos remaches y su número estampado (bloques que no se leen).
        val chapa = Rect(w * 0.79f, y(FRISO0) + w * 0.009f, w * 0.885f, y(FRISO0) + w * 0.03f)
        val lc = g.luz(chapa.center, 0.025f)
        drawRoundRect(lc.s(0.35f), chapa.topLeft + Offset(1.2f * u, 1.4f * u), chapa.size, CornerRadius(w * 0.003f))
        drawRoundRect(lc.c(P.METAL), chapa.topLeft, chapa.size, CornerRadius(w * 0.003f))
        drawRect(lc.b(0.6f), chapa.topLeft + Offset(w * 0.004f, 0f), Size(chapa.width - w * 0.008f, 0.9f * u))
        drawPath(bloquesUrss(Offset(chapa.left + w * 0.014f, chapa.center.y - w * 0.003f), chapa.width - w * 0.028f, w * 0.006f, 7410), lc.c(P.NEGRO).copy(alpha = 0.7f))
        listOf(chapa.left + w * 0.006f, chapa.right - w * 0.006f).forEach { x -> drawCircle(lc.s(0.5f), 1f * u, Offset(x, chapa.center.y)) }
        // Los golpes del canto de la tapa: el barniz saltado, más claro.
        for (k in 0 until 6) {
            val x = w * (0.05f + 0.9f * azar(7420, k))
            val l = g.luz(x, y(CANTO0), 0.03f)
            drawPath(escamaUrss(Offset(x, y(CANTO0) + w * 0.006f), w * (0.004f + 0.005f * azar(7421, k)), w * 0.003f, 7422 + k, 7), l.c(P.ROBLE_LUZ).copy(alpha = 0.8f))
        }
    }

    // ---------------------------------------------------------------- en cada dibujo

    /**
     * Las juntas: debajo de las piezas que se tocan en un borde que puede caer a medio píxel (las tablas con los huecos y los
     * costados con los huecos), la fila o la columna entera de la junta, del color de la tabla.
     */
    fun DrawScope.juntas(g: MarcoUrss, d: DisposicionEstante) {
        d.tablas.forEach { r ->
            for (yy in listOf(r.top, r.bottom)) {
                val f = floor(yy)
                drawRect(g.luz(g.w * 0.5f, yy, 0.03f).c(P.ROBLE_OSC), Offset(r.left - 1f, f), Size(r.width + 2f, 1f))
            }
        }
        val y0 = floor(g.ch); val y1 = ceil(g.fondo)
        for (xx in listOf(g.x0, g.x1)) {
            val f = floor(xx)
            drawRect(g.luz(xx, (y0 + y1) / 2, 0.03f).c(P.ROBLE_OSC), Offset(f, y0), Size(1f, y1 - y0))
        }
    }

    /**
     * Una tabla: el canto de roble con su veta, la arista de arriba redondeada que coge la luz, la de abajo en sombra, algún golpe
     * en el barniz y, de noche, el filo de la bombilla más cercana.
     */
    fun DrawScope.tabla(k: Int, r: Rect, g: MarcoUrss) {
        val w = g.w; val u = density
        val xs = List(9) { r.left + r.width * it / 8f }
        fun l(x: Float) = g.luz(x, r.center.y, 0.04f)
        drawRect(Brush.horizontalGradient(xs.map { l(it).c(P.ROBLE) }, r.left, r.right), r.topLeft, Size(r.width, r.height + 0.6f))
        drawRect(TexturasUrss.vetaH(u), r.topLeft, r.size, alpha = 0.85f)
        // La arista de arriba, redondeada y a la luz; la de abajo, en sombra.
        drawRect(Brush.horizontalGradient(xs.map { l(it).c(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.55f)) }, r.left, r.right), r.topLeft, Size(r.width, r.height * 0.14f))
        drawLine(l(r.left + r.width * 0.2f).b(0.7f), Offset(r.left, r.top + 1f * u), Offset(r.right, r.top + 1f * u), 1.1f * u)
        drawRect(Brush.horizontalGradient(xs.map { mezcla(l(it).c(P.ROBLE_OSC), ParedEstanteUrss.honda(g), 0.2f) }, r.left, r.right), Offset(r.left, r.bottom - r.height * 0.16f), Size(r.width, r.height * 0.16f))
        // De día, el barniz brilla más a la izquierda, cerca de la luz.
        if (g.a.dia) drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.14f), Color.Transparent), r.left, r.left + r.width * 0.5f), Offset(r.left, r.top + r.height * 0.2f), Size(r.width * 0.5f, r.height * 0.5f))
        // Golpes en el barniz, lejos del centro (donde va el rótulo).
        val s = 7500 + k * 13
        for (i in 0 until 4) {
            val f = azar(s, 10 + i)
            val x = r.left + r.width * (if (f < 0.5f) 0.04f + 0.3f * f else 0.66f + 0.3f * (f - 0.5f))
            val ls = l(x)
            drawPath(escamaUrss(Offset(x, r.top + r.height * (0.2f + 0.5f * azar(s, 20 + i))), w * (0.003f + 0.004f * azar(s, 30 + i)), w * 0.002f, s + 40 + i, 7), ls.c(P.ROBLE_LUZ).copy(alpha = 0.7f))
        }
        // De noche, el filo de la bombilla más cercana en la arista de arriba.
        if (g.a.oscuro || g.a.noche) {
            val filos = xs.map { l(it).filo(0.85f) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, r.left, r.right), Offset(r.left, r.top), Size(r.width, 1.8f * u))
        }
        // La sombra que la tabla echa en lo de debajo (el hueco o la base).
        drawRect(g.a.sombra(0.3f), Offset(r.left, r.bottom), Size(r.width, 2f * u))
    }

    /**
     * El rótulo del mes: el portaetiquetas de latón del fichero del Ideario, atornillado al canto, con su tarjeta de cartulina
     * amarillenta (de valor medio) y el mes escrito a máquina; de noche, la tarjeta teñida por la luz que le toca.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoUrss) {
        val u = density
        val r = p.r
        val l = g.luz(r.center.x, r.center.y, 0.045f)
        val marco = Rect(r.left - 3f * u, r.top + r.height * 0.02f, r.right + 3f * u, r.bottom - r.height * 0.02f)
        val t = Rect(marco.left + 3.2f * u, marco.top + 2.6f * u, marco.right - 3.2f * u, marco.bottom - 2.2f * u)
        drawRect(l.s(0.4f), marco.topLeft + Offset(1.4f * u, 1.6f * u), marco.size)
        drawRoundRect(l.c(P.LATON), marco.topLeft, marco.size, CornerRadius(1.4f * u))
        drawRect(l.b(0.6f), marco.topLeft + Offset(2f * u, 0.4f * u), Size(marco.width - 4f * u, 1f * u))
        drawRect(l.s(0.3f), Offset(marco.left, marco.bottom - 1.2f * u), Size(marco.width, 1.2f * u))
        val carton = mezcla(l.etiqueta(), l.c(ETIQUETA_URSS), if (g.a.oscuro) 0f else 0.5f)
        drawRect(carton, t.topLeft, t.size)
        drawRect(mezcla(carton, P.ESTRAZA, 0.3f).copy(alpha = 0.35f), Offset(t.left, t.bottom - t.height * 0.25f), Size(t.width, t.height * 0.25f))
        drawRect(l.s(0.3f), t.topLeft, Size(t.width, 1.4f * u))
        textoEn(medidor, p.texto, Rect(t.left + 3f * u, t.top + t.height * 0.1f, t.right - 3f * u, t.bottom - 0.4f * u), P.LETRA, l.tinta(carton), 10f,
            espaciado = 0.6f, minimo = 6.5f)
        // Los dos tornillos que lo sujetan al canto, a los lados.
        tornilloUrss(Offset(marco.left - 3.4f * u, marco.center.y), 1.9f * u, l, 7601)
        tornilloUrss(Offset(marco.right + 3.4f * u, marco.center.y), 1.9f * u, l, 7602)
        if (l.k > 0.08f) drawRoundRect(l.filo(0.6f), marco.topLeft, marco.size, CornerRadius(1.4f * u), style = Stroke(1f * u))
    }

    /**
     * Los costados: la cara de delante de roble con la veta a lo alto, el canto de dentro redondeado (coge la luz), el de fuera en
     * sombra, el tornillo de latón de cada tabla, los golpes del barniz y, de noche, el filo de la bombilla más cercana.
     */
    fun DrawScope.costados(g: MarcoUrss, d: DisposicionEstante) {
        val w = g.w; val u = density
        val y0 = g.ch; val y1 = g.fondo
        if (y1 - y0 < 2f) return
        val ys = List(11) { y0 + (y1 - y0) * it / 10f }
        for (izq in listOf(true, false)) {
            val a = if (izq) 0f else g.x1
            val b = if (izq) g.x0 else w
            val an = b - a
            val cx = (a + b) / 2
            drawRect(Brush.verticalGradient(ys.map { g.luz(cx, it, 0.04f).c(P.ROBLE) }, y0, y1), Offset(a, y0), Size(an, y1 - y0))
            drawRect(TexturasUrss.vetaV(u), Offset(a, y0), Size(an, y1 - y0), alpha = 0.9f)
            // De día, la luz viene de la izquierda: más clara en el lado de la izquierda de cada costado.
            if (g.a.dia) drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = if (izq) 0.2f else 0.1f), Color.Transparent), a, a + an * 0.6f), Offset(a, y0), Size(an * 0.6f, y1 - y0))
            // La vuelta de fuera (en sombra) y el canto redondeado de dentro: su luz y su sombra.
            val fuera = if (izq) a else b - an * 0.1f
            drawRect(Brush.verticalGradient(ys.map { mezcla(g.luz(cx, it, 0.04f).c(P.ROBLE_OSC), ParedEstanteUrss.honda(g), 0.25f) }, y0, y1), Offset(fuera, y0), Size(an * 0.1f, y1 - y0))
            val canto = if (izq) b - an * 0.22f else a
            drawRect(Brush.verticalGradient(ys.map { g.luz(canto, it, 0.045f).c(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.4f)) }, y0, y1), Offset(canto, y0), Size(an * 0.22f, y1 - y0))
            drawRect(Brush.verticalGradient(ys.map { g.luz(canto, it, 0.045f).b(0.65f) }, y0, y1), Offset(canto + an * 0.05f, y0), Size(1.3f * u, y1 - y0))
            drawRect(g.a.sombra(0.4f), Offset(if (izq) b - 1.6f * u else a + an * 0.22f - 1.2f * u, y0), Size(1.4f * u, y1 - y0))
            drawRect(g.a.sombra(0.28f), Offset(if (izq) canto - 1.2f * u else a + an * 0.22f, y0), Size(1.2f * u, y1 - y0))
            // El junquillo: la moldura fina a lo largo de la cara, hundida (su sombra) y con su filo de luz.
            val xj = if (izq) a + an * 0.34f else a + an * 0.62f
            drawRect(Brush.verticalGradient(ys.map { g.luz(xj, it, 0.04f).s(0.32f) }, y0, y1), Offset(xj, y0), Size(1.3f * u, y1 - y0))
            drawRect(Brush.verticalGradient(ys.map { g.luz(xj, it, 0.04f).b(0.4f) }, y0, y1), Offset(xj + 1.3f * u, y0), Size(0.8f * u, y1 - y0))
            // El roce de las manos en el canto de dentro, a media altura: el barniz gastado, más claro.
            val ym = y0 + (y1 - y0) * (if (izq) 0.42f else 0.55f)
            drawRect(g.luz(canto, ym, 0.045f).c(P.ROBLE_LUZ).copy(alpha = 0.55f), Offset(canto + an * 0.02f, ym - w * 0.06f), Size(an * 0.16f, w * 0.12f))
            // Los tornillos de latón de cada tabla, con su arandela.
            d.tablas.forEachIndexed { k, t ->
                val c = Offset(if (izq) a + an * 0.45f else a + an * 0.55f, t.center.y)
                val l = g.luz(c, 0.04f)
                drawCircle(l.c(mezcla(P.LATON, P.ROBLE_OSC, 0.4f)), 3.2f * u, c)
                tornilloUrss(c, 2.2f * u, l, 7700 + k * 2 + (if (izq) 0 else 1))
            }
            // Los golpes del barniz y un arañazo largo.
            for (i in 0 until 6) {
                val yy = y0 + (y1 - y0) * azar(if (izq) 7710 else 7711, i)
                val x = a + an * (0.2f + 0.5f * azar(if (izq) 7712 else 7713, i))
                drawPath(escamaUrss(Offset(x, yy), w * (0.003f + 0.004f * azar(7714, i)), w * 0.002f, 7715 + i, 7), g.luz(x, yy, 0.04f).c(P.ROBLE_LUZ).copy(alpha = 0.6f))
            }
            // De noche, el filo de la bombilla más cercana en el canto de dentro.
            if (g.a.oscuro || g.a.noche) {
                val filos = ys.map { g.luz(canto, it, 0.045f).filo(0.8f) }
                if (filos.any { it.alpha > 0.04f }) drawRect(Brush.verticalGradient(filos, y0, y1), Offset(if (izq) b - an * 0.12f else a + an * 0.04f, y0), Size(1.8f * u, y1 - y0))
            }
            if (izq) {
                // El número de inventario de la estantería, a plantilla con pintura cruda, abajo (bloques que no se leen).
                val yn = y1 - w * 0.07f
                val ln = g.luz(cx, yn, 0.04f)
                drawPath(bloquesUrss(Offset(a + an * 0.18f, yn), an * 0.62f, w * 0.009f, 7730, 2, 1.9f), ln.c(P.CARTULINA_OSC).copy(alpha = 0.6f))
            } else {
                // El portaetiquetas de latón de la sección, arriba, con su tarjeta (renglones que no se leen).
                val pr = Rect(a + an * 0.18f, y0 + w * 0.035f, b - an * 0.22f, y0 + w * 0.11f)
                val lp = g.luz(pr.center, 0.045f)
                drawRect(lp.s(0.35f), pr.topLeft + Offset(1.4f * u, 1.6f * u), pr.size)
                drawRoundRect(lp.c(P.LATON), pr.topLeft, pr.size, CornerRadius(1.2f * u))
                val tj = pr.deflate(2.6f * u)
                drawRect(lp.etiqueta(), tj.topLeft, tj.size)
                drawRect(mezcla(lp.etiqueta(), P.ROJO, 0.7f), Offset(tj.left, tj.top + 2f * u), Size(tj.width, 0.9f * u))
                drawPath(bloquesUrss(Offset(tj.left + 2f * u, tj.top + 5f * u), tj.width - 4f * u, 1.4f * u, 7731, 5, 2.2f), lp.tinta(lp.etiqueta()).copy(alpha = 0.65f))
                drawRect(lp.b(0.6f), pr.topLeft + Offset(1.2f * u, 0f), Size(pr.width - 2.4f * u, 1f * u))
                tornilloUrss(Offset(pr.center.x, pr.top - 2.4f * u), 1.4f * u, lp, 7732)
            }
        }
    }

    // ---------------------------------------------------------------- la lámpara de pinza

    /**
     * La lámpara de pinza sujeta al canto del costado izquierdo, a la altura de la tabla de en medio: el sargento de hierro negro
     * con su tornillo y su mariposa, el brazo de dos tramos con la rótula y la pantalla cónica de chapa esmaltada negra, blanca por
     * dentro, mirando a la derecha y abajo, a la repisa de debajo. De noche, la boca encendida, la bombilla y su halo; de día,
     * apagada, con su sombra en el costado.
     */
    fun DrawScope.pinza(g: MarcoUrss, d: DisposicionEstante) {
        val w = g.w; val u = density
        val boca = g.pinza
        val t = MuebleEstanteUrss.tablaPinza(g.n)
        val ty = if (t >= 0 && t < d.tablas.size) d.tablas[t].center.y else g.ch - w * 0.03f
        val l = g.luz(boca.x - w * 0.02f, boca.y - w * 0.015f, 0.05f)
        // El sargento: abraza el canto del costado a la altura de la tabla.
        val sx = w * 0.012f; val sy = ty - w * 0.022f
        val sarg = Path().apply {
            moveTo(sx, sy); lineTo(sx + w * 0.05f, sy); lineTo(sx + w * 0.05f, sy + w * 0.01f); lineTo(sx + w * 0.01f, sy + w * 0.01f)
            lineTo(sx + w * 0.01f, sy + w * 0.034f); lineTo(sx + w * 0.05f, sy + w * 0.034f); lineTo(sx + w * 0.05f, sy + w * 0.044f); lineTo(sx, sy + w * 0.044f); close()
        }
        val hierro = mezcla(P.NEGRO, P.ACERO_OSC, 0.35f)
        drawPath(corridoUrss(sarg, w * 0.004f, w * 0.005f), l.s(0.4f))
        drawPath(sarg, l.c(hierro))
        drawLine(l.b(0.5f), Offset(sx + 1f * u, sy + 1f * u), Offset(sx + w * 0.05f, sy + 1f * u), 1f * u)
        // El tornillo del sargento y su mariposa.
        val tor = Offset(sx + w * 0.032f, sy + w * 0.044f)
        drawLine(l.c(P.METAL), tor, tor + Offset(0f, w * 0.018f), 1.8f * u)
        drawLine(l.c(hierro), tor + Offset(-w * 0.012f, w * 0.018f), tor + Offset(w * 0.012f, w * 0.018f), 2.4f * u, StrokeCap.Round)
        // El brazo: de lo alto del sargento sube un tramo, la rótula, y otro tramo baja a la pantalla.
        val base = Offset(sx + w * 0.03f, sy)
        val rotula = Offset(base.x - w * 0.006f, base.y - w * 0.04f)
        val ejeP = Offset(boca.x - w * 0.034f, boca.y - w * 0.026f)
        val brazo = Path().apply { moveTo(base.x, base.y); lineTo(rotula.x, rotula.y); lineTo(ejeP.x, ejeP.y) }
        translate(w * 0.004f, w * 0.004f) { drawPath(brazo, l.s(0.35f), style = Stroke(w * 0.006f, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
        drawPath(brazo, l.c(P.METAL), style = Stroke(w * 0.005f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(brazo, l.b(0.6f), style = Stroke(1f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(l.c(hierro), w * 0.008f, rotula)
        drawCircle(l.b(0.6f), w * 0.003f, rotula - Offset(w * 0.002f, w * 0.002f))
        // La pantalla cónica: del culo (arriba a la izquierda) a la boca (abajo a la derecha), con la boca en elipse.
        val eje = Offset(0.848f, 0.53f)
        val perp = Offset(-eje.y, eje.x)
        val culo = Offset(boca.x - eje.x * w * 0.05f, boca.y - eje.y * w * 0.05f)
        val rb = w * 0.024f; val rc = w * 0.008f
        val cono = poligonoUrss(culo + perp * rc, boca + perp * rb, boca - perp * rb, culo - perp * rc)
        drawPath(corridoUrss(cono, w * 0.006f, w * 0.008f), l.s(if (g.a.oscuro) 0.3f else 0.32f))
        drawPath(cono, l.c(hierro))
        clipPath(cono) {
            drawPath(corridoUrss(cono, -perp.x * w * 0.014f, -perp.y * w * 0.014f), l.b(0.3f))
            drawLine(l.b(0.7f), culo - perp * (rc * 0.4f), boca - perp * (rb * 0.6f), 1.2f * u)
        }
        drawCircle(l.c(hierro), rc * 1.2f, culo)
        // La boca: el aro y lo de dentro (blanco esmaltado), y la bombilla.
        val bocaP = Path().apply {
            addOval(Rect(boca.x - rb * 0.42f, boca.y - rb, boca.x + rb * 0.42f, boca.y + rb))
        }
        val encendida = g.a.pinza > 0f
        val giro = Math.toDegrees(kotlin.math.atan2(eje.y.toDouble(), eje.x.toDouble())).toFloat()
        rotate(giro, boca) {
            if (encendida) {
                drawPath(bocaP, P.LLAMA)
                drawOval(Color(0xFFFFF6DE), Offset(boca.x - rb * 0.22f, boca.y - rb * 0.5f), Size(rb * 0.44f, rb))
            } else {
                drawPath(bocaP, l.c(mezcla(P.CARTULINA, P.ACERO, 0.3f)))
                drawOval(l.s(0.3f), Offset(boca.x - rb * 0.3f, boca.y - rb * 0.2f), Size(rb * 0.6f, rb * 1.1f))
                drawOval(l.c(mezcla(P.CARTULINA, P.METAL, 0.5f)), Offset(boca.x - rb * 0.16f, boca.y - rb * 0.36f), Size(rb * 0.32f, rb * 0.6f))
            }
            drawPath(bocaP, l.c(mezcla(hierro, P.METAL, 0.3f)), style = Stroke(1.4f * u))
        }
        // De noche: el halo de la boca en el costado y en la tabla.
        if (encendida) {
            mancha(boca + Offset(w * 0.012f, w * 0.01f), w * 0.09f, P.LLAMA.copy(alpha = 0.34f))
            mancha(boca + Offset(w * 0.05f, w * 0.04f), w * 0.2f, P.LLAMA.copy(alpha = 0.1f))
        }
        // El cable de tela, que baja del culo de la pantalla por el canto del costado.
        val cable = abiertaUrss(listOf(culo, culo + Offset(-w * 0.012f, w * 0.01f), Offset(w * 0.016f, sy + w * 0.07f), Offset(w * 0.014f, sy + w * 0.2f)))
        drawPath(cable, l.s(0.3f), style = Stroke(2f * u))
        drawPath(cable, l.c(mezcla(P.PARQUET_OSC, P.NEGRO, 0.4f)), style = Stroke(1.8f * u, cap = StrokeCap.Round))
    }
}
