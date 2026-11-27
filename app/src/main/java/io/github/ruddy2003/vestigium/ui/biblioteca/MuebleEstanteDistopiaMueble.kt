package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La estantería de chapa del archivo, del mismo taller que el archivero del Ideario: la cornisa prensada (el bocel de arriba, la
 * gola, el filete, el friso con sus dos rejillas de lamas y la placa de inventario en blanco, el junquillo), los costados con su
 * canto redondeado y su columna de ranuras, las baldas de labio con el canal del portaetiquetas, los rótulos del mes y la
 * lámpara de inspección enjaulada colgada del costado izquierdo.
 */
internal object MuebleDistopia {
    // ---------------------------------------------------------------- la cornisa (se pinta una vez, en la corona guardada)

    fun DrawScope.cornisa(g: MarcoDistopia) {
        val w = g.w; val u = density
        val y0 = g.apoyo; val ch = g.ch
        val alto = ch - y0
        fun y(f: Float) = y0 + alto * f
        with(ParedEstanteDistopia) {
            conRejaDistopia(g, 0.05f, Rect(0f, y0, w, ch)) { s ->
                // Debajo de todo, la chapa en sombra: las juntas entre una moldura y otra no dejan asomar el fondo.
                campo(g, Rect(0f, y0, w, ch), P.ACERO_OSC, 0.05f, s, paso = 8f, muestras = 9)
                // El bocel de arriba: la luz en lo alto y la sombra debajo.
                campo(g, Rect(0f, y(0f), w, y(0.09f)), mezcla(P.ACERO, P.ACERO_LUZ, 0.55f), 0.06f, s, paso = 3f, muestras = 9)
                campo(g, Rect(0f, y(0.09f), w, y(0.13f)), P.ACERO, 0.06f, s, paso = 3f, muestras = 9)
                // La gola, que mira hacia abajo: en sombra.
                campo(g, Rect(0f, y(0.13f), w, y(0.22f)), mezcla(P.ACERO_OSC, P.ACERO, 0.4f), 0.05f, s, paso = 3f, muestras = 9)
                // El filete y el friso.
                campo(g, Rect(0f, y(0.22f), w, y(0.28f)), mezcla(P.ACERO, P.ACERO_LUZ, 0.35f), 0.055f, s, paso = 3f, muestras = 9)
                campo(g, Rect(0f, y(0.28f), w, y(0.78f)), P.ACERO, 0.05f, s, paso = 4f, muestras = 11)
                // El junquillo y la banda de abajo.
                campo(g, Rect(0f, y(0.78f), w, y(0.87f)), mezcla(P.ACERO, P.ACERO_LUZ, 0.3f), 0.055f, s, paso = 3f, muestras = 9)
                campo(g, Rect(0f, y(0.87f), w, ch), mezcla(P.ACERO, P.ACERO_OSC, 0.45f), 0.05f, s, paso = 3f, muestras = 9)
            }
        }
        val lm = g.luz(w * 0.3f, y0, 0.06f)
        // Los filos: el de arriba del bocel, el de la gola, el del filete y el del junquillo.
        drawLine(lm.b(0.8f), Offset(0f, y(0.012f)), Offset(w, y(0.012f)), 1.3f * u)
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(0.5f), g.a.sombra(0.1f)), y(0.13f), y(0.22f)), Offset(0f, y(0.13f)), Size(w, y(0.22f) - y(0.13f)))
        drawLine(lm.b(0.55f), Offset(0f, y(0.225f)), Offset(w, y(0.225f)), 1f * u)
        drawRect(g.a.sombra(0.3f), Offset(0f, y(0.28f)), Size(w, 1.6f * u))
        drawLine(lm.b(0.5f), Offset(0f, y(0.79f)), Offset(w, y(0.79f)), 1f * u)
        drawRect(g.a.sombra(0.4f), Offset(0f, y(0.86f)), Size(w, 1.4f * u))
        // Los dados sobre los costados: un poco salientes, con su canto a la luz, su sombra a un lado y un tornillo.
        val f0 = y(0.28f); val f1 = y(0.78f)
        for ((a, b) in listOf(0f to g.x0, g.x1 to w)) {
            val ld = g.luz((a + b) / 2, (f0 + f1) / 2, 0.06f)
            drawRect(ld.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.25f)), Offset(a, f0), Size(b - a, f1 - f0))
            drawRect(ld.b(0.45f), Offset(a, f0), Size(b - a, 1.2f * u))
            drawRect(ld.s(0.35f), Offset(if (a < w / 2) b - 2f * u else a, f0), Size(2f * u, f1 - f0))
            tornilloDistopia(Offset((a + b) / 2, (f0 + f1) / 2), 2.4f * u, ld, 7000 + a.toInt())
        }
        // Las dos rejillas de lamas del friso (las mismas del zócalo del archivero).
        for ((fa, fb) in listOf(0.16f to 0.37f, 0.63f to 0.84f)) rejilla(g, Rect(w * fa, y(0.38f), w * fb, y(0.68f)), 7)
        // La placa de inventario, remachada en el centro, con su tarjetero y una tarjeta en blanco (dos renglones que no se leen).
        val pl = Rect(w * 0.43f, y(0.33f), w * 0.57f, y(0.73f))
        val lp = g.luz(pl.center.x, pl.center.y, 0.065f)
        drawRect(lp.s(0.4f), pl.topLeft + Offset(1.6f * u, 2f * u), pl.size)
        drawRect(lp.c(P.ACERO_LUZ), pl.topLeft, pl.size)
        drawRect(lp.b(0.7f), pl.topLeft, Size(pl.width, 1f * u))
        drawRect(lp.s(0.3f), Offset(pl.left, pl.bottom - 1.2f * u), Size(pl.width, 1.2f * u))
        val tj = Rect(pl.left + pl.width * 0.16f, pl.top + pl.height * 0.22f, pl.right - pl.width * 0.16f, pl.bottom - pl.height * 0.22f)
        drawRect(lp.c(mezcla(P.HUESO, P.CARPETA, 0.35f)), tj.topLeft, tj.size)
        drawRect(lp.c(P.CARTEL).copy(alpha = 0.6f), Offset(tj.left, tj.top + tj.height * 0.18f), Size(tj.width, 0.8f * u))
        drawPath(renglonesDistopia(Rect(tj.left + tj.width * 0.08f, tj.top + tj.height * 0.42f, tj.right - tj.width * 0.08f, tj.bottom - tj.height * 0.08f), 0.0024f * w, 0.007f * w, 7010),
            lp.c(P.BAQUELITA).copy(alpha = 0.45f))
        drawRect(lp.s(0.2f), tj.topLeft, Size(tj.width, 1.2f * u))
        listOf(pl.left + 2.6f * u, pl.right - 2.6f * u).forEach { x ->
            listOf(pl.top + 2.6f * u, pl.bottom - 2.6f * u).forEach { yy ->
                drawCircle(lp.c(P.HIERRO), 1.3f * u, Offset(x, yy)); drawCircle(lp.b(0.5f), 0.5f * u, Offset(x - 0.4f * u, yy - 0.4f * u))
            }
        }
        // El friso gastado: pintura saltada en el bocel y en los cantos, rozaduras y una abolladura.
        pinceladas(Rect(0f, f0, w, f1), if (g.a.oscuro) g.a.hondo else P.ACERO_OSC, 7020, 16, u, 0.08f)
        for (j in 0 until 14) {
            val x = w * (0.02f + 0.96f * azar(7021, j))
            val yy = if (j % 3 == 0) y(0.04f) else if (j % 3 == 1) y(0.8f) else y(0.3f + 0.4f * azar(7022, j))
            val ls = g.luz(x, yy, 0.06f)
            saltadoDistopia(Offset(x, yy), w * (0.004f + 0.006f * azar(7023, j)), 7030 + j, ls.c(P.HIERRO), ls.b(0.45f), ls.c(P.OXIDO))
        }
        abolladuraDistopia(Offset(w * 0.27f, y(0.58f)), w * 0.03f, alto * 0.14f, g.luz(w * 0.27f, y(0.58f), 0.06f))
        // De noche, el filo del flexo en el bocel, más fuerte cerca.
        if (g.a.oscuro || g.a.noche) {
            val filos = List(9) { j -> g.luz(w * j / 8f, y(0.02f), 0.06f).filo(0.8f) }
            drawRect(Brush.horizontalGradient(filos, 0f, w), Offset(0f, y(0.004f)), Size(w, 1.6f * u))
        }
    }

    /** Una rejilla de lamas prensada en la chapa: cada lama con su hueco hondo arriba y el labio de luz abajo. */
    private fun DrawScope.rejilla(g: MarcoDistopia, r: Rect, lamas: Int) {
        val u = density
        val paso = r.height / maxOf(1, lamas)
        val l = g.luz(r.center.x, r.center.y, 0.05f)
        val huecos = Path(); val labios = Path()
        for (i in 0 until lamas) {
            val y = r.top + paso * i
            huecos.addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(r.left, y + paso * 0.18f, r.right, y + paso * 0.6f), CornerRadius(paso * 0.2f)))
            labios.addRect(Rect(r.left + paso * 0.2f, y + paso * 0.6f, r.right - paso * 0.2f, y + paso * 0.6f + 0.9f * u))
        }
        drawPath(huecos, mezcla(l.c(P.HIERRO), ParedEstanteDistopia.honda(g), 0.4f))
        drawPath(labios, l.b(0.5f))
        if (g.a.oscuro && l.k > 0.1f) drawPath(labios, l.filo(0.6f))
    }

    // ---------------------------------------------------------------- las juntas

    /**
     * Las filas de las juntas: donde una tabla toca el hueco de arriba o de abajo, el borde puede caer a medio píxel. Debajo de
     * las dos piezas va esa fila entera, de píxel entero y del color de la chapa, solo a lo ancho de las dos; y lo mismo en las
     * columnas donde los costados tocan los huecos.
     */
    fun DrawScope.juntas(g: MarcoDistopia, d: DisposicionEstante) {
        d.tablas.forEach { r ->
            for (yy in listOf(r.top, r.bottom)) {
                val f = floor(yy)
                drawRect(g.luz(g.w * 0.5f, yy, 0.03f).c(P.ACERO_OSC), Offset(r.left - 1f, f), Size(r.width + 2f, 1f))
            }
        }
        val y0 = floor(g.ch); val y1 = ceil(g.fondo)
        for (xx in listOf(g.x0, g.x1)) {
            val f = floor(xx)
            drawRect(g.luz(xx, (y0 + y1) / 2, 0.03f).c(P.ACERO_OSC), Offset(f, y0), Size(1f, y1 - y0))
        }
    }

    // ---------------------------------------------------------------- las baldas

    /**
     * Una balda: el labio de chapa doblada (el doblez redondo arriba, la cara con el canal del portaetiquetas a todo lo largo, la
     * vuelta de abajo), un tornillo en cada punta, la pintura saltada en el doblez y algún resto de tarjeta en el canal.
     */
    fun DrawScope.balda(k: Int, r: Rect, g: MarcoDistopia) {
        val w = g.w; val u = density
        val xs = List(9) { r.left + r.width * it / 8f }
        fun l(x: Float) = g.luz(x, r.center.y, 0.04f)
        fun banda(f0: Float, f1: Float, col: Color) {
            val a = r.top + r.height * f0; val b = r.top + r.height * f1
            drawRect(Brush.horizontalGradient(xs.map { l(it).c(col) }, r.left, r.right), Offset(r.left, a), Size(r.width, b - a + 0.6f))
        }
        banda(0f, 1f, P.ACERO)
        // El doblez de arriba, a la luz, y su filo.
        banda(0f, 0.16f, mezcla(P.ACERO, P.ACERO_LUZ, 0.5f))
        drawLine(l(r.left + r.width * 0.2f).b(0.75f), Offset(r.left, r.top + 1f * u), Offset(r.right, r.top + 1f * u), 1.1f * u)
        // El canal del portaetiquetas: hundido, con la sombra de su labio de arriba y la luz en el de abajo.
        val c0 = r.top + r.height * 0.27f; val c1 = r.top + r.height * 0.79f
        drawRect(Brush.horizontalGradient(xs.map { mezcla(l(it).c(P.ACERO_OSC), P.ACERO, 0.3f) }, r.left, r.right), Offset(r.left, c0), Size(r.width, c1 - c0))
        drawRect(g.a.sombra(0.4f), Offset(r.left, c0), Size(r.width, (c1 - c0) * 0.24f))
        drawLine(l(r.center.x).b(0.55f), Offset(r.left, c1), Offset(r.right, c1), 0.9f * u)
        drawLine(l(r.center.x).b(0.35f), Offset(r.left, c0 - 0.8f * u), Offset(r.right, c0 - 0.8f * u), 0.8f * u)
        // La vuelta de abajo, en sombra.
        drawRect(Brush.horizontalGradient(xs.map { mezcla(l(it).c(P.ACERO_OSC), ParedEstanteDistopia.honda(g), 0.25f) }, r.left, r.right), Offset(r.left, r.bottom - r.height * 0.13f), Size(r.width, r.height * 0.13f))
        // El resto de una tarjeta vieja en el canal, en algunas baldas, lejos del centro (donde va el rótulo).
        val s = 7100 + k * 13
        if (azar(s, 1) < 0.6f) {
            val x = r.left + r.width * (if (azar(s, 2) < 0.5f) 0.08f + 0.12f * azar(s, 3) else 0.78f + 0.1f * azar(s, 3))
            val lt = l(x)
            val t = Rect(x, c0 + 1.6f * u, x + w * (0.03f + 0.03f * azar(s, 4)), c1 - 1f * u)
            drawRect(lt.c(mezcla(P.CARPETA, P.CARTON, 0.4f)), t.topLeft, t.size)
            drawLine(lt.s(0.3f), Offset(t.right, t.top), Offset(t.right - 1.6f * u, t.bottom), 1f * u)
        }
        // Los tornillos de las puntas, que la sujetan a los costados.
        tornilloDistopia(Offset(r.left + 0.014f * w, r.center.y + r.height * 0.05f), 2.2f * u, l(r.left), s + 5)
        tornilloDistopia(Offset(r.right - 0.014f * w, r.center.y + r.height * 0.05f), 2.2f * u, l(r.right), s + 6)
        // La pintura saltada en el doblez, por el roce de las cajas.
        for (i in 0 until 5) {
            val x = r.left + r.width * (0.04f + 0.92f * azar(s, 10 + i))
            val ls = l(x)
            saltadoDistopia(Offset(x, r.top + r.height * (0.06f + 0.1f * azar(s, 20 + i))), w * (0.004f + 0.005f * azar(s, 30 + i)), s + 40 + i, ls.c(P.HIERRO), ls.b(0.45f), ls.c(P.OXIDO))
        }
        // De noche, el filo de la bombilla más cercana en el doblez, y el de la estufa en la vuelta de abajo (que mira al suelo).
        if (g.a.oscuro || g.a.noche) {
            val filos = xs.map { l(it).filo(0.85f) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, r.left, r.right), Offset(r.left, r.top + 0.4f * u), Size(r.width, 1.6f * u))
        }
        if (g.a.oscuro && g.a.estufa > 0f) {
            val filos = xs.map { x -> P.RESISTENCIA.copy(alpha = (0.85f * g.a.estufa * MuebleEstanteDistopia.kEstufa(x / w, r.bottom / w, g.h / w)).coerceIn(0f, 0.85f)) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, r.left, r.right), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.6f * u))
        }
    }

    /**
     * El rótulo del mes: una tarjeta de manila metida en el canal de la balda (los labios del canal pisan sus bordes), con la raya
     * roja de las fichas y el mes a máquina; se tiñe con la luz que le toca sin dejar de leerse.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoDistopia) {
        val u = density
        val r = p.r
        val l = g.luz(r.center.x, r.center.y, 0.045f)
        val t = Rect(r.left, r.top + r.height * 0.08f, r.right, r.bottom - r.height * 0.06f)
        val carton = mezcla(l.etiqueta(), if (g.a.oscuro) l.etiqueta() else l.c(P.CARPETA), 0.5f)
        drawRect(l.s(0.35f), t.topLeft + Offset(1.2f * u, 1.4f * u), t.size)
        drawRect(carton, t.topLeft, t.size)
        drawRect(mezcla(carton, P.CARTEL, 0.55f), Offset(t.left, t.top + t.height * 0.16f), Size(t.width, 0.8f * u))
        drawRect(mezcla(carton, P.KRAFT, 0.3f).copy(alpha = 0.35f), Offset(t.left, t.bottom - t.height * 0.2f), Size(t.width, t.height * 0.2f))
        textoEn(medidor, p.texto, Rect(t.left + 4f * u, t.top + t.height * 0.2f, t.right - 4f * u, t.bottom - 0.6f * u), P.LETRA, l.tinta(carton), 10.5f,
            peso = FontWeight.Medium, espaciado = 0.4f, minimo = 6.5f)
        // Los labios del canal, que pisan la tarjeta arriba y abajo.
        drawRect(l.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.3f)), Offset(t.left - 2f * u, t.top - 1f * u), Size(t.width + 4f * u, 1.6f * u))
        drawRect(l.s(0.35f), Offset(t.left - 2f * u, t.top + 0.6f * u), Size(t.width + 4f * u, 1f * u))
        drawRect(l.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.3f)), Offset(t.left - 2f * u, t.bottom - 0.6f * u), Size(t.width + 4f * u, 1.4f * u))
        if (l.k > 0.08f) drawRect(l.filo(0.5f), t.topLeft, t.size, style = Stroke(0.9f * u))
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Los costados: la cara de delante de chapa con su canto redondeado hacia dentro (que coge la luz), la vuelta de fuera en
     * sombra, la columna de ranuras para las ménsulas, un tornillo a la altura de cada balda, la pintura saltada y algún chorreón de
     * óxido; en el izquierdo, el cable de la lámpara grapado de trecho en trecho.
     */
    fun DrawScope.costados(g: MarcoDistopia, d: DisposicionEstante) {
        val w = g.w; val u = density
        val y0 = g.ch; val y1 = g.fondo
        if (y1 - y0 < 2f) return
        val ys = List(9) { y0 + (y1 - y0) * it / 8f }
        for (izq in listOf(true, false)) {
            val a = if (izq) 0f else g.x1
            val b = if (izq) g.x0 else w
            val an = b - a
            val cx = (a + b) / 2
            // La cara, con la luz de cada altura.
            drawRect(Brush.verticalGradient(ys.map { g.luz(cx, it, 0.04f).c(P.ACERO) }, y0, y1), Offset(a, y0), Size(an, y1 - y0))
            // De día, la luz del tubo viene de la izquierda: más clara en el lado de la izquierda de cada costado.
            if (g.a.tubo) drawRect(Brush.horizontalGradient(listOf(P.LUZ_TUBO.copy(alpha = if (izq) 0.22f else 0.1f), Color.Transparent), a, a + an * 0.6f), Offset(a, y0), Size(an * 0.6f, y1 - y0))
            pinceladas(Rect(a, y0, b, y1), if (g.a.oscuro) g.a.hondo else P.ACERO_OSC, if (izq) 7201 else 7202, 10, u, 0.09f)
            // La vuelta de fuera (en sombra) y el canto redondeado de dentro: su luz y su sombra.
            val fuera = if (izq) a else b - an * 0.12f
            drawRect(Brush.verticalGradient(ys.map { mezcla(g.luz(cx, it, 0.04f).c(P.ACERO_OSC), ParedEstanteDistopia.honda(g), 0.2f) }, y0, y1), Offset(fuera, y0), Size(an * 0.12f, y1 - y0))
            val canto = if (izq) b - an * 0.2f else a
            drawRect(Brush.verticalGradient(ys.map { g.luz(canto, it, 0.045f).c(mezcla(P.ACERO, P.ACERO_LUZ, 0.45f)) }, y0, y1), Offset(canto, y0), Size(an * 0.2f, y1 - y0))
            drawRect(Brush.verticalGradient(ys.map { g.luz(canto, it, 0.045f).b(0.7f) }, y0, y1), Offset(canto + an * 0.03f, y0), Size(1.2f * u, y1 - y0))
            drawRect(g.a.sombra(0.4f), Offset(if (izq) b - 1.6f * u else a + an * 0.2f - 1.2f * u, y0), Size(1.4f * u, y1 - y0))
            drawRect(g.a.sombra(0.3f), Offset(if (izq) canto - 1.2f * u else a + an * 0.2f, y0), Size(1.2f * u, y1 - y0))
            // La columna de ranuras: pares de ranuras chicas, cada una con su luz en el borde de abajo. En un solo camino.
            val xr = if (izq) a + an * 0.44f else a + an * 0.5f
            val ranuras = Path(); val bordes = Path()
            val paso = maxOf(4f * u, 0.032f * w)
            var yy = y0 + paso * 0.6f; var j = 0
            while (yy < y1 - paso * 0.4f && j < 400) {
                val r1 = Rect(xr - 0.003f * w, yy, xr + 0.003f * w, yy + 0.016f * w)
                ranuras.addRoundRect(androidx.compose.ui.geometry.RoundRect(r1, CornerRadius(0.003f * w)))
                bordes.addRect(Rect(r1.left, r1.bottom, r1.right + 0.6f * u, r1.bottom + 0.8f * u))
                yy += paso; j++
            }
            drawPath(ranuras, mezcla(g.luz(cx, (y0 + y1) / 2, 0.04f).c(P.HIERRO), ParedEstanteDistopia.honda(g), 0.5f))
            drawPath(bordes, g.luz(cx, (y0 + y1) / 2, 0.04f).b(0.35f))
            // El filo de la bombilla más cercana en el canto de dentro.
            if (g.a.oscuro || g.a.noche) {
                val filos = ys.map { g.luz(canto, it, 0.045f).filo(0.8f) }
                if (filos.any { it.alpha > 0.04f }) drawRect(Brush.verticalGradient(filos, y0, y1), Offset(if (izq) b - an * 0.12f else a + an * 0.04f, y0), Size(1.8f * u, y1 - y0))
            }
            // Un tornillo a la altura de cada balda, cerca del canto, y alguno con su chorreón de óxido.
            d.tablas.forEachIndexed { k, t ->
                val c = Offset(if (izq) b - an * 0.38f else a + an * 0.36f, t.center.y)
                val lt = g.luz(c.x, c.y, 0.04f)
                if (azar(7210 + k, if (izq) 1 else 2) < 0.4f) drawRect(lt.c(P.OXIDO).copy(alpha = 0.3f), Offset(c.x - 0.8f * u, c.y), Size(1.6f * u, (0.05f + 0.07f * azar(7220 + k, 3)) * w))
                tornilloDistopia(c, 2.3f * u, lt, 7230 + k * 2 + (if (izq) 0 else 1))
            }
            // La pintura saltada en los cantos.
            for (i in 0 until 9) {
                val yy2 = y0 + (y1 - y0) * azar(if (izq) 7240 else 7241, i)
                val x = if (i % 2 == 0) (if (izq) b - an * 0.1f else a + an * 0.1f) else (if (izq) a + an * 0.08f else b - an * 0.08f)
                val ls = g.luz(x, yy2, 0.04f)
                saltadoDistopia(Offset(x, yy2), w * (0.004f + 0.006f * azar(7250, i + (if (izq) 0 else 20))), 7260 + i + (if (izq) 0 else 30), ls.c(P.HIERRO), ls.b(0.45f), ls.c(P.OXIDO))
            }
            if (!izq) abolladuraDistopia(Offset(cx, y0 + (y1 - y0) * 0.62f), an * 0.3f, 0.04f * w, g.luz(cx, y0 + (y1 - y0) * 0.62f, 0.04f))
        }
        // El cable de la lámpara, que baja por el costado izquierdo desde arriba, grapado de trecho en trecho.
        val xc = 0.013f * w
        val gancho = g.lampara.y - 0.075f * w
        if (gancho > y0) {
            val lc = g.luz(xc, (y0 + gancho) / 2, 0.045f)
            drawLine(lc.s(0.35f), Offset(xc + 1.2f * u, y0), Offset(xc + 1.2f * u, gancho), 1.6f * u)
            drawLine(lc.c(P.BAQUELITA), Offset(xc, y0), Offset(xc, gancho), 1.6f * u)
            var yg = y0 + 0.06f * w; var j = 0
            while (yg < gancho - 0.02f * w && j < 60) {
                val lg = g.luz(xc, yg, 0.045f)
                drawRect(lg.c(P.ACERO_LUZ), Offset(xc - 2.2f * u, yg), Size(4.4f * u, 1.4f * u))
                yg += 0.12f * w; j++
            }
        }
    }

    // ---------------------------------------------------------------- la lámpara de inspección

    /**
     * La lámpara de inspección colgada de un gancho en el costado izquierdo: el mango de baquelita, la jaula de alambre con su aro
     * de arriba y el de abajo, y la bombilla dentro. De día apagada (el vidrio claro); de noche arde y enciende el costado.
     */
    fun DrawScope.lampara(g: MarcoDistopia) {
        val w = g.w; val u = density
        val c = g.lampara
        if (c.y < g.ch + 0.05f * w || c.y > g.fondo) return
        val arde = g.a.lampara
        val l = g.luz(c.x, c.y - 0.04f * w, 0.05f)
        // El halo en el costado y en lo de alrededor.
        if (arde > 0f) {
            mancha(c, w * 0.26f, P.BOMBILLA.copy(alpha = 0.26f * arde))
            mancha(c, w * 0.09f, P.BOMBILLA.copy(alpha = 0.5f * arde))
        }
        val gancho = Offset(c.x, c.y - 0.075f * w)
        // El gancho en S, atornillado al costado.
        drawCircle(l.c(P.HIERRO), 1.8f * u, gancho - Offset(0f, 0.006f * w))
        drawArc(l.c(P.HIERRO), 180f, 180f, false, Offset(gancho.x - 0.006f * w, gancho.y - 0.004f * w), Size(0.012f * w, 0.012f * w), style = Stroke(1.4f * u))
        // El mango de baquelita, con su aro de colgar.
        val mango = Rect(c.x - 0.009f * w, c.y - 0.068f * w, c.x + 0.009f * w, c.y - 0.03f * w)
        drawCircle(l.c(P.ACERO_LUZ), 0.006f * w, Offset(c.x, mango.top - 0.002f * w), style = Stroke(1.3f * u))
        drawRoundRect(l.s(0.4f), mango.topLeft + Offset(1.4f * u, 1.6f * u), mango.size, CornerRadius(3f * u))
        drawRoundRect(Brush.horizontalGradient(listOf(l.c(mezcla(P.BAQUELITA, P.MADERA, 0.4f)), l.c(P.BAQUELITA), l.c(P.BAQUELITA)), mango.left, mango.right), mango.topLeft, mango.size, CornerRadius(3f * u))
        drawLine(l.b(0.5f), Offset(mango.left + 1.4f * u, mango.top + 2f * u), Offset(mango.left + 1.4f * u, mango.bottom - 2f * u), 0.8f * u)
        for (i in 0 until 3) drawLine(l.s(0.4f), Offset(mango.left, mango.top + mango.height * (0.25f + 0.2f * i)), Offset(mango.right, mango.top + mango.height * (0.25f + 0.2f * i)), 0.7f * u)
        // La bombilla, de pera, dentro de la jaula.
        val jaula = Rect(c.x - 0.022f * w, c.y - 0.03f * w, c.x + 0.022f * w, c.y + 0.045f * w)
        val vidrio = if (arde > 0f) mezcla(P.BOMBILLA, Color.White, 0.5f) else l.c(mezcla(P.HUESO, P.ACERO_LUZ, 0.4f))
        val pera = cerradaDistopia(listOf(Offset(c.x - 0.006f * w, jaula.top + 0.004f * w), Offset(c.x + 0.006f * w, jaula.top + 0.004f * w), Offset(c.x + 0.015f * w, c.y + 0.016f * w),
            Offset(c.x, jaula.bottom - 0.006f * w), Offset(c.x - 0.015f * w, c.y + 0.016f * w)))
        drawPath(pera, vidrio)
        if (arde > 0f) drawCircle(Color.White, 0.006f * w, Offset(c.x, c.y + 0.012f * w))
        else {
            drawPath(bordeDistopia(pera, 0.004f * w, 0f), l.b(0.5f))
            drawPath(bordeDistopia(pera, -0.005f * w, -0.002f * w), l.s(0.3f))
        }
        // La jaula: los alambres de arriba abajo, curvados, y los dos aros. En un solo camino.
        val alambres = Path()
        for (i in 0..5) {
            val f = i / 5f
            val x = jaula.left + jaula.width * f
            alambres.moveTo(c.x + (x - c.x) * 0.55f, jaula.top)
            alambres.quadraticTo(x + (x - c.x) * 0.35f, c.y + 0.008f * w, c.x + (x - c.x) * 0.5f, jaula.bottom)
        }
        drawPath(alambres, (if (arde > 0f) mezcla(P.ACERO_OSC, P.BOMBILLA, 0.35f) else l.c(P.HIERRO)), style = Stroke(1f * u))
        drawOval(l.c(P.ACERO_LUZ), Offset(jaula.left + jaula.width * 0.2f, jaula.top - 0.003f * w), Size(jaula.width * 0.6f, 0.007f * w), style = Stroke(1.6f * u))
        drawOval(l.c(P.ACERO_LUZ), Offset(jaula.left + jaula.width * 0.24f, jaula.bottom - 0.004f * w), Size(jaula.width * 0.52f, 0.007f * w), style = Stroke(1.6f * u))
        if (arde > 0f) drawPath(alambres, P.BOMBILLA.copy(alpha = 0.5f), style = Stroke(0.5f * u))
    }
}
