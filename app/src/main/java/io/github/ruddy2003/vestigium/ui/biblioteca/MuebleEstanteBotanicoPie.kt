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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie del estante de Botánico ([MuebleEstanteBotanico]): el piso de tablas de nogal del gabinete (el del Ideario) con el borde
 * de una alfombra de lana (cenefa de rombos y franjas, campo de granza), y encima, cortados por los bordes, la caja de embarque
 * de los especímenes con la tapa desclavada, la paja y dos frascos asomando; el farol de campo de hojalata con sus vidrios (la
 * luz del pie, de noche); y la maceta grande con una palmera. Cada cosa con su sombra en el piso.
 */
internal object PieEstanteBotanico {
    private val P = PaletaBotanico

    /** Dónde pisa algo que está a [f] de hondo en el piso (0, junto al zócalo; 1, el borde de abajo). */
    private fun pisa(suelo: Float, h: Float, f: Float) = suelo + (h - suelo) * f

    // ---------------------------------------------------------------- el piso y la alfombra

    /** El piso de tablas de nogal que se abren hacia quien mira, y la alfombra encima. */
    fun DrawScope.piso(s: SitioBotanico, b: Rect, suelo: Float, h: Float) {
        val w = size.width; val u = density
        drawRect(s.aLoAncho(0f, w, (suelo + h) / 2, true) { it.c(mezcla(P.NOGAL, P.ROBLE_OSC, 0.3f)) }, Offset(0f, suelo), Size(w, h - suelo + 1f))
        val tc = s.t(Offset(w * 0.5f, h), true)
        for (i in -4..14) {
            val x0 = w * i / 10f
            val x1 = w / 2 + (x0 - w / 2) * 1.5f
            drawLine(tc.s(0.4f), Offset(x0, suelo), Offset(x1, h), 1.3f * u)
            drawLine(tc.b(0.2f), Offset(x0 + 1.3f * u, suelo), Offset(x1 + 1.3f * u, h), 0.7f * u)
        }
        // Las juntas de cabeza de las tablas, alternadas.
        for (j in 0 until 8) {
            val f = azar(9700, j)
            val y = pisa(suelo, h, 0.15f + 0.8f * f)
            val i = (azar(9701, j) * 16).toInt() - 3
            val xa = w * i / 10f; val xb = w * (i + 1) / 10f
            val e = 1f + 0.5f * (y - suelo) / (h - suelo)
            drawLine(tc.s(0.35f), Offset(w / 2 + (xa - w / 2) * e, y), Offset(w / 2 + (xb - w / 2) * e, y), 1f * u)
        }
        pinceladas(Rect(0f, suelo, w, h), if (s.a.oscuro) P.NOCHE else P.ROBLE, 9710, 16, u, 0.14f)
        alfombra(s, suelo, h)
        // La sombra del rincón, bajo el zócalo.
        drawRect(tc.s(0.45f), Offset(0f, suelo), Size(w, 2.6f * u))
        drawRect(tc.s(0.2f), Offset(0f, suelo + 2.6f * u), Size(w, w * 0.03f))
    }

    /**
     * La alfombra de lana, vista en escorzo y cortada por la derecha y por abajo: el fleco de su borde de atrás, la cenefa con
     * sus franjas y su hilera de rombos, y el campo de granza con rombos chicos sueltos. Gastada donde se pisa.
     */
    private fun DrawScope.alfombra(s: SitioBotanico, suelo: Float, h: Float) {
        val w = size.width; val u = density
        val yA = pisa(suelo, h, 0.3f)
        fun x(f: Float, y: Float): Float { val e = 1f + 0.5f * (y - suelo) / (h - suelo); return w / 2 + (w * f - w / 2) * e }
        val izqA = x(0.3f, yA); val izqB = x(0.3f, h)
        // El orillo de la izquierda: dónde empieza la alfombra a cada altura (así nada se sale sin recortes).
        fun orillo(y: Float) = izqA + (izqB - izqA) * (y - yA) / (h - yA)
        val forma = poligonoBotanico(izqA, yA, w + 2f, yA, w + 2f, h + 2f, izqB, h + 2f)
        val t = s.t(Offset(w * 0.6f, (yA + h) / 2), true)
        drawPath(Path().apply { addPath(forma, Offset(w * 0.006f, -w * 0.004f)) }, t.s(0.3f))
        drawPath(forma, s.aLoAncho(izqB, w, (yA + h) / 2, true) { it.c(P.GRANZA) })
        // La cenefa: dos franjas de índigo y, entre ellas, la hilera de rombos de ocre y crudo.
        val c0 = yA + (h - yA) * 0.08f; val c1 = yA + (h - yA) * 0.34f
        fun franja(y0: Float, y1: Float, col: Color) = drawPath(poligonoBotanico(orillo(y0), y0, w + 2f, y0, w + 2f, y1, orillo(y1), y1), col)
        franja(c0, c1, t.c(P.INDIGO))
        franja(c0 + (c1 - c0) * 0.1f, c0 + (c1 - c0) * 0.16f, t.c(P.OCRE))
        franja(c1 - (c1 - c0) * 0.16f, c1 - (c1 - c0) * 0.1f, t.c(P.OCRE))
        val cy = (c0 + c1) / 2
        val ra = (c1 - c0) * 0.3f
        var i = 0
        var xx = orillo(cy) + ra * 1.6f
        while (xx < w + ra * 2 && i < 80) {
            val col = if (i % 2 == 0) P.OCRE else P.CRUDO
            val tt = s.t(Offset(xx, cy), true)
            drawPath(poligonoBotanico(xx, cy - ra, xx + ra * 1.3f, cy, xx, cy + ra, xx - ra * 1.3f, cy), tt.c(col, -0.05f))
            drawPath(poligonoBotanico(xx, cy - ra * 0.4f, xx + ra * 0.5f, cy, xx, cy + ra * 0.4f, xx - ra * 0.5f, cy), tt.c(P.GRANZA))
            xx += ra * 3f; i++
        }
        // El campo: rombos chicos sueltos de índigo y puntos de ocre, en tresbolillo, más anchos hacia adelante.
        for (fila in 0 until 3) {
            val y = c1 + (h - c1) * (0.25f + 0.32f * fila)
            val e = 1f + 0.5f * (y - suelo) / (h - suelo)
            val r0 = (h - yA) * 0.034f * e
            var j = 0
            var px = orillo(y) + r0 * 2f + (if (fila % 2 == 0) 0f else r0 * 3f)
            while (px < w + r0 && j < 60) {
                drawPath(poligonoBotanico(px, y - r0, px + r0 * 1.2f, y, px, y + r0, px - r0 * 1.2f, y), t.c(P.INDIGO, -0.05f).copy(alpha = 0.6f))
                drawCircle(t.c(P.OCRE).copy(alpha = 0.55f), r0 * 0.3f, Offset(px + r0 * 3f, y))
                px += r0 * 6f; j++
            }
        }
        // Lo gastado de pisar.
        pinceladas(Rect(izqB + w * 0.05f, c1, w, h), if (s.a.oscuro) P.NOCHE else P.CRUDO, 9720, 10, u, 0.12f)
        // El fleco del borde de atrás: hebras cortas de lana cruda, en una sola línea quebrada.
        val fl = Path()
        var xf = izqA
        var k = 0
        fl.moveTo(xf, yA)
        while (xf < w && k < 200) {
            val alto = w * (0.012f + 0.006f * azar(9731, k))
            fl.lineTo(xf + 0.8f * u, yA - alto); fl.lineTo(xf + 1.8f * u, yA)
            xf += 3.2f * u; k++
        }
        drawPath(fl, t.c(P.CRUDO, -0.05f), style = Stroke(0.9f * u))
        drawLine(t.c(P.INDIGO), Offset(izqA, yA), Offset(w, yA), 1.4f * u)
        // El canto del lado izquierdo, con su orillo.
        drawLine(t.c(P.INDIGO, -0.1f), Offset(izqA, yA), Offset(izqB, h), 2f * u)
    }
    // ---------------------------------------------------------------- lo que hay en el piso

    /** La caja de embarque, el farol y la palmera, de atrás adelante. */
    fun DrawScope.objetos(s: SitioBotanico, b: Rect, suelo: Float, h: Float) {
        val w = size.width
        palmera(Offset(w * 0.9f, pisa(suelo, h, 0.86f)), s, suelo, h)
        caja(Offset(w * 0.1f, pisa(suelo, h, 0.84f)), s, b)
        farol(Offset(w * MuebleEstanteBotanico.FAROL_X, pisa(suelo, h, 0.68f)), s)
    }

    /**
     * La caja de embarque de los especímenes, de tablas de pino con sus flejes de hierro, la tapa desclavada y puesta de través,
     * la paja que se sale y dos frascos que asoman; una hoja de embarque clavada (con renglones, sin letra). Cortada por la
     * izquierda.
     */
    private fun DrawScope.caja(pie: Offset, s: SitioBotanico, b: Rect) {
        val w = size.width; val u = density
        val cw = w * 0.36f; val chh = w * 0.18f
        val fr = Rect(pie.x - cw * 0.55f, pie.y - chh, pie.x + cw * 0.45f, pie.y)
        val dv = Offset(w * 0.045f, -w * 0.032f)
        val t = s.t(fr.center, true)
        val hacia = if (s.a.oscuro && fr.center.x / w < MuebleEstanteBotanico.FAROL_X) 1f else -1f
        contactoBotanico(Offset(fr.center.x + w * 0.03f, pie.y), cw * 1.15f, w * 0.03f, t, 0.5f)
        // El costado de la derecha, en escorzo (en sombra de día; de noche, del lado del farol).
        val lado = poligonoBotanico(fr.right, fr.top, fr.right + dv.x, fr.top + dv.y, fr.right + dv.x, fr.bottom + dv.y, fr.right, fr.bottom)
        drawPath(lado, t.c(P.PINO, if (hacia > 0f) 0.08f else -0.25f))
        // La boca abierta, vista desde arriba: lo hondo, la paja y los dos frascos.
        val boca = poligonoBotanico(fr.left, fr.top, fr.right, fr.top, fr.right + dv.x, fr.top + dv.y, fr.left + dv.x, fr.top + dv.y)
        drawPath(boca, t.c(P.PINO_OSC, -0.3f))
        listOf(0.38f, 0.62f).forEachIndexed { j, f ->
            val cx = fr.left + fr.width * f + dv.x * 0.5f
            val top = fr.top - w * (0.05f + 0.02f * j)
            val fw = w * 0.04f
            val tt = s.t(Offset(cx, top), true)
            drawRect(tt.c(P.VIDRIO_OSC, 0.1f), Offset(cx - fw / 2, top), Size(fw, fr.top - top + 2f))
            drawRect(tt.b(0.45f), Offset(cx - fw * 0.35f, top), Size(fw * 0.15f, fr.top - top))
            drawRect(tt.c(mezcla(P.KRAFT, P.CRUDO, 0.3f)), Offset(cx - fw * 0.3f, top - w * 0.014f), Size(fw * 0.6f, w * 0.016f))
            drawLine(tt.c(P.BRAMANTE), Offset(cx - fw * 0.5f, top + w * 0.004f), Offset(cx + fw * 0.5f, top + w * 0.004f), 1f * u)
        }
        // La paja: briznas que se salen por arriba y cuelgan por el borde.
        val paja = Path()
        for (j in 0 until 60) {
            val x0 = fr.left + (fr.width + dv.x) * azar(9800, j)
            val y0 = fr.top + dv.y * azar(9801, j)
            val an = -2.6f + 2.2f * azar(9802, j)
            val l = w * (0.02f + 0.035f * azar(9803, j))
            paja.moveTo(x0, y0); paja.lineTo(x0 + l * kotlin.math.cos(an), y0 + l * kotlin.math.sin(an))
        }
        drawPath(paja, t.c(P.PAJA, 0.05f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
        // El frente: tres tablas de pino con sus juntas, la veta y los clavos.
        drawRect(t.c(P.PINO), fr.topLeft, fr.size)
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            vetas(fr, t.c(P.PINO_OSC).copy(alpha = 0.45f), 9810, 0.8f, grosor = 0.6f)
            for (j in 1 until 3) {
                val y = fr.top + fr.height * j / 3f
                drawRect(t.s(0.45f), Offset(fr.left, y - 0.6f * u), Size(fr.width, 1.4f * u))
                drawRect(t.b(0.3f), Offset(fr.left, y + 0.8f * u), Size(fr.width, 0.8f * u))
            }
            drawRect(t.b(0.25f), fr.topLeft, Size(fr.width * 0.25f, fr.height))
            drawRect(t.s(0.25f), Offset(fr.right - fr.width * 0.18f, fr.top), Size(fr.width * 0.18f, fr.height))
        }
        drawRect(t.b(0.5f), fr.topLeft, Size(fr.width, 1f * u))
        // Los flejes de hierro, a lo alto, con sus clavos.
        listOf(0.22f, 0.86f).forEach { f ->
            val x = fr.left + fr.width * f
            drawRect(t.s(0.35f), Offset(x + 1.4f * u, fr.top), Size(w * 0.012f, fr.height))
            drawRect(t.c(P.HIERRO, 0.04f), Offset(x, fr.top), Size(w * 0.012f, fr.height))
            drawRect(t.b(0.3f), Offset(x, fr.top), Size(0.8f * u, fr.height))
            for (q in 0 until 3) drawCircle(t.c(P.HOJALATA, -0.2f), 1f * u, Offset(x + w * 0.006f, fr.top + fr.height * (0.15f + 0.35f * q)))
        }
        // La hoja de embarque clavada, con renglones (sin letra).
        val hoja = Rect(fr.left + fr.width * 0.4f, fr.top + fr.height * 0.22f, fr.left + fr.width * 0.68f, fr.top + fr.height * 0.72f)
        rotate(-3f, hoja.center) {
            drawRect(t.s(0.3f), hoja.topLeft + Offset(1.4f * u, 1.6f * u), hoja.size)
            drawRect(t.c(mezcla(P.PAPEL, P.KRAFT, 0.35f)), hoja.topLeft, hoja.size)
            drawRect(t.c(P.LACRE).copy(alpha = 0.7f), hoja.topLeft, Size(hoja.width, hoja.height * 0.16f))
            for (q in 0 until 4) drawLine(t.c(P.TINTA).copy(alpha = 0.45f), Offset(hoja.left + hoja.width * 0.12f, hoja.top + hoja.height * (0.35f + 0.15f * q)), Offset(hoja.right - hoja.width * (0.15f + 0.2f * (q % 2)), hoja.top + hoja.height * (0.35f + 0.15f * q)), 0.7f * u)
            pinceladas(hoja, if (s.a.oscuro) P.NOCHE else P.KRAFT, 9811, 4, u, 0.3f)
            listOf(hoja.topLeft, Offset(hoja.right, hoja.top)).forEach { p -> drawCircle(t.c(P.HIERRO), 0.9f * u, p + Offset(if (p.x > hoja.center.x) -2f * u else 2f * u, 2f * u)) }
        }
        // La tapa desclavada, apoyada de través contra el frente, con sus clavos torcidos.
        val tapa = Rect(fr.right - fr.width * 0.08f, pie.y - w * 0.2f, fr.right + w * 0.02f, pie.y)
        rotate(14f, Offset(tapa.center.x, pie.y)) {
            drawRect(t.s(0.35f), tapa.topLeft + Offset(w * 0.012f, 0f), tapa.size)
            drawRect(t.c(P.PINO, if (hacia > 0f) 0.05f else -0.08f), tapa.topLeft, tapa.size)
            vetas(tapa, t.c(P.PINO_OSC).copy(alpha = 0.4f), 9812, 0.8f, vertical = true, grosor = 0.5f)
            drawRect(t.b(0.4f), tapa.topLeft, Size(1f * u, tapa.height))
            listOf(0.1f, 0.9f).forEach { f -> drawLine(t.c(P.HIERRO), Offset(tapa.center.x, tapa.top + tapa.height * f), Offset(tapa.center.x + w * 0.012f, tapa.top + tapa.height * f - w * 0.008f), 1f * u) }
        }
    }

    /**
     * El farol de campo: la base de hojalata, el cuerpo de vidrios con sus montantes oscuros (el de frente y el del costado, en
     * escorzo) y sus dos alambres de guarda, dentro la vela en su cubo, el sombrerete cónico con sus respiraderos, la chimenea y
     * la argolla. De día, apagado (la vela se ve tras el vidrio); de noche, la llama dentro y su luz en el piso.
     */
    private fun DrawScope.farol(pie: Offset, s: SitioBotanico) {
        val w = size.width; val u = density
        val fw = w * 0.092f; val fh = w * 0.2f
        val t = s.t(Offset(pie.x, pie.y - fh * 0.4f), true)
        val o = s.a.oscuro
        val luz = if (o) (if (s.a.noche) 0.6f else 1f) else 0f
        if (o) mancha(Offset(pie.x, pie.y - fh * 0.3f), w * 0.24f, P.LLAMA.copy(alpha = 0.22f * luz))
        contactoBotanico(Offset(pie.x + w * 0.012f, pie.y), fw * 1.6f, w * 0.022f, t, if (o) 0.22f else 0.48f)
        val lata = t.c(mezcla(P.HOJALATA, P.HIERRO, 0.5f), if (o) 0.06f else 0f)
        val lataClara = t.c(P.HOJALATA, 0.04f)
        val base = Rect(pie.x - fw * 0.56f, pie.y - fh * 0.08f, pie.x + fw * 0.56f, pie.y)
        val cuerpo = Rect(pie.x - fw / 2, base.top - fh * 0.5f, pie.x + fw / 2, base.top)
        val dv = Offset(fw * 0.3f, -fw * 0.18f)
        // El vidrio del costado, en escorzo, y lo que se ve por él.
        val lado = poligonoBotanico(cuerpo.right, cuerpo.top, cuerpo.right + dv.x, cuerpo.top + dv.y, cuerpo.right + dv.x, cuerpo.bottom + dv.y, cuerpo.right, cuerpo.bottom)
        drawPath(lado, if (o) mezcla(P.LLAMA, P.NOCHE, 0.5f - 0.25f * luz) else t.c(mezcla(P.VIDRIO_OSC, P.NOGAL, 0.4f), -0.1f))
        // El fondo de dentro (el vidrio de atrás y el piso que se ve por él) y la vela en su cubo.
        drawRect(if (o) mezcla(P.LLAMA, P.FUEGO, 0.35f * luz).copy(alpha = 0.5f + 0.4f * luz) else t.c(mezcla(P.NOGAL, P.VIDRIO_OSC, 0.4f), -0.05f), cuerpo.topLeft, cuerpo.size)
        val cubo = Rect(pie.x - fw * 0.16f, cuerpo.bottom - cuerpo.height * 0.16f, pie.x + fw * 0.16f, cuerpo.bottom)
        val vela = Rect(pie.x - fw * 0.09f, cuerpo.bottom - cuerpo.height * 0.52f, pie.x + fw * 0.09f, cubo.top)
        drawRect(t.c(Color(0xFFEDE1BE), if (o) 0.2f * luz else 0f), vela.topLeft, vela.size)
        clipRect(vela.left, vela.top, vela.right, vela.bottom) { cilindroBotanico(vela, t, -1f, 0.8f) }
        drawLine(t.c(P.TINTA), Offset(pie.x, vela.top), Offset(pie.x, vela.top - fh * 0.025f), 0.8f * u)
        drawRect(lataClara, cubo.topLeft, cubo.size)
        clipRect(cubo.left, cubo.top, cubo.right, cubo.bottom) { cilindroBotanico(cubo, t, -1f, 1.2f) }
        if (o) {
            val lc = Offset(pie.x, vela.top - fh * 0.06f)
            drawCircle(P.FUEGO.copy(alpha = 0.55f * luz), fw * 0.3f, lc)
            drawOval(P.LLAMA, Offset(lc.x - fw * 0.075f, lc.y - fw * 0.2f), Size(fw * 0.15f, fw * 0.32f))
            drawOval(P.FUEGO, Offset(lc.x - fw * 0.038f, lc.y - fw * 0.1f), Size(fw * 0.076f, fw * 0.18f))
        } else {
            // El vidrio de frente: el reflejo del día en diagonal.
            drawPath(poligonoBotanico(cuerpo.left + fw * 0.08f, cuerpo.bottom, cuerpo.left + fw * 0.34f, cuerpo.top, cuerpo.left + fw * 0.5f, cuerpo.top, cuerpo.left + fw * 0.24f, cuerpo.bottom), t.b(0.3f))
            drawRect(t.c(P.VIDRIO).copy(alpha = 0.12f), cuerpo.topLeft, cuerpo.size)
        }
        // Los montantes oscuros de las esquinas y los dos alambres de guarda.
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            drawRect(lata, Offset(x - fw * 0.06f, cuerpo.top), Size(fw * 0.12f, cuerpo.height))
            drawRect(t.b(0.35f), Offset(x - fw * 0.06f, cuerpo.top), Size(0.8f * u, cuerpo.height))
        }
        drawLine(lata, Offset(cuerpo.right + dv.x, cuerpo.top + dv.y), Offset(cuerpo.right + dv.x, cuerpo.bottom + dv.y), fw * 0.09f)
        listOf(0.36f, 0.64f).forEach { f ->
            val x = cuerpo.left + cuerpo.width * f
            drawLine(lata.copy(alpha = 0.85f), Offset(x, cuerpo.top + 2f * u), Offset(x, cuerpo.bottom - 2f * u), 1.1f * u)
        }
        drawRect(lata, Offset(cuerpo.left - fw * 0.06f, cuerpo.top - fh * 0.02f), Size(cuerpo.width + fw * 0.12f, fh * 0.03f))
        // La base y el sombrerete con sus respiraderos, la chimenea y la argolla.
        drawRect(lata, base.topLeft, base.size)
        clipRect(base.left, base.top, base.right, base.bottom) { cilindroBotanico(base, t, if (o) 1f else -1f, 1.2f) }
        drawRect(t.b(0.4f), base.topLeft, Size(base.width, 0.8f * u))
        val y0 = cuerpo.top - fh * 0.02f
        val som = poligonoBotanico(cuerpo.left - fw * 0.1f, y0, cuerpo.right + fw * 0.1f + dv.x * 0.4f, y0, pie.x + fw * 0.15f, y0 - fh * 0.15f, pie.x - fw * 0.15f, y0 - fh * 0.15f)
        drawPath(som, lata)
        clipPath(som) { cilindroBotanico(Rect(cuerpo.left - fw * 0.12f, y0 - fh * 0.15f, cuerpo.right + fw * 0.2f, y0), t, -1f, 1.2f) }
        for (q in 0 until 5) drawCircle(t.s(0.65f), 0.9f * u, Offset(pie.x - fw * 0.3f + fw * 0.15f * q, y0 - fh * 0.05f))
        val chim = Rect(pie.x - fw * 0.13f, y0 - fh * 0.22f, pie.x + fw * 0.13f, y0 - fh * 0.15f)
        drawRect(lata, chim.topLeft, chim.size)
        drawRect(lataClara, Offset(chim.left - fw * 0.04f, chim.top - fh * 0.012f), Size(chim.width + fw * 0.08f, fh * 0.018f))
        val ar = Rect(pie.x - fw * 0.26f, chim.top - fh * 0.16f, pie.x + fw * 0.26f, chim.top + fh * 0.01f)
        drawOval(t.s(0.35f), ar.topLeft + Offset(1.4f * u, 1.4f * u), ar.size, style = Stroke(1.8f * u))
        drawOval(t.c(P.HIERRO, 0.12f), ar.topLeft, ar.size, style = Stroke(1.8f * u))
        drawArc(t.b(0.5f), 190f, 90f, false, ar.topLeft, ar.size, style = Stroke(0.7f * u))
        if (o) drawRect(P.LLAMA.copy(alpha = 0.5f * luz), Offset(cuerpo.left, cuerpo.bottom - 1f * u), Size(cuerpo.width, 1.5f * u))
    }

    /** Una hoja de palmera: el raquis que sube y se vence, con sus foliolos largos y caídos a los dos lados. */
    private fun DrawScope.hojaPalma(base: Offset, largo: Float, giro: Float, color: Color, sombra: Color, grosor: Float) {
        val an = Math.toRadians(giro.toDouble())
        val dx = kotlin.math.cos(an).toFloat(); val dy = kotlin.math.sin(an).toFloat()
        val ctrl = Offset(base.x + dx * largo * 0.55f, base.y + dy * largo * 0.55f - largo * 0.12f)
        val fin = Offset(base.x + dx * largo * 0.92f, base.y + dy * largo * 0.5f + largo * 0.22f)
        fun punto(f: Float) = Offset((1 - f) * (1 - f) * base.x + 2 * (1 - f) * f * ctrl.x + f * f * fin.x, (1 - f) * (1 - f) * base.y + 2 * (1 - f) * f * ctrl.y + f * f * fin.y)
        val raquis = Path().apply { moveTo(base.x, base.y); quadraticTo(ctrl.x, ctrl.y, fin.x, fin.y) }
        val folios = Path()
        val n = 14
        for (i in 2..n) {
            val f = i / n.toFloat()
            val p = punto(f); val q = punto((f - 0.04f).coerceAtLeast(0f))
            val tx = p.x - q.x; val ty = p.y - q.y
            val l0 = kotlin.math.sqrt(tx * tx + ty * ty).coerceAtLeast(0.001f)
            val ux = tx / l0; val uy = ty / l0
            val l = largo * 0.26f * (1f - 0.55f * f)
            for (lado in floatArrayOf(-1f, 1f)) {
                // Perpendicular al raquis, vencida hacia abajo por su peso.
                var ex = -uy * lado; var ey = ux * lado
                ex = ex * 0.55f + ux * 0.35f; ey = ey * 0.55f + uy * 0.35f + 0.6f
                val le = kotlin.math.sqrt(ex * ex + ey * ey)
                folios.moveTo(p.x, p.y)
                folios.quadraticTo(p.x + ex / le * l * 0.5f, p.y + ey / le * l * 0.4f, p.x + ex / le * l, p.y + ey / le * l)
            }
        }
        drawPath(Path().apply { addPath(folios, Offset(grosor * 1.2f, grosor * 1.4f)) }, sombra, style = Stroke(grosor * 1.4f, cap = StrokeCap.Round))
        drawPath(folios, color, style = Stroke(grosor * 1.4f, cap = StrokeCap.Round))
        drawPath(raquis, mezcla(color, P.KRAFT, 0.35f), style = Stroke(grosor * 1.2f, cap = StrokeCap.Round))
    }

    /** La maceta grande de barro con una palmera de hojas pinnadas que se arquean, cortada por la derecha. */
    private fun DrawScope.palmera(pie: Offset, s: SitioBotanico, suelo: Float, h: Float) {
        val w = size.width; val u = density
        val pw = w * 0.26f; val ph = w * 0.16f
        val t = s.t(Offset(pie.x, pie.y - ph), true)
        val boca = pie.y - ph
        contactoBotanico(Offset(pie.x - w * 0.01f, pie.y), pw * 1.2f, w * 0.03f, t, 0.5f)
        // Las hojas: arcos largos que se vencen, con sus foliolos caídos, hacia la izquierda y hacia arriba.
        val raiz = Offset(pie.x - pw * 0.05f, boca - w * 0.008f)
        listOf(-172f to 0.26f, -148f to 0.3f, -122f to 0.27f, -98f to 0.22f, -70f to 0.2f, -40f to 0.17f).forEachIndexed { j, (g, l) ->
            val col = t.c(when (j % 3) { 0 -> P.HOJA_OSC; 1 -> P.HOJA; else -> mezcla(P.HOJA, P.HOJA_CLARA, 0.45f) }, 0.02f)
            hojaPalma(raiz, w * l, g, col, t.s(0.25f), 1.1f * u)
        }
        // La maceta: el cuerpo que se angosta, el borde grueso y la tierra.
        val cuerpo = poligonoBotanico(pie.x - pw / 2, boca, pie.x + pw / 2, boca, pie.x + pw * 0.38f, pie.y, pie.x - pw * 0.38f, pie.y)
        drawPath(cuerpo, t.c(P.TERRACOTA))
        clipPath(cuerpo) {
            cilindroBotanico(Rect(pie.x - pw / 2, boca, pie.x + pw / 2, pie.y), t, -1f, 1.2f)
            drawOval(t.c(mezcla(P.TERRACOTA, P.CRUDO, 0.55f)).copy(alpha = 0.45f), Offset(pie.x - pw * 0.15f, pie.y - ph * 0.5f), Size(pw * 0.35f, ph * 0.3f))
            drawOval(t.c(P.TERRACOTA_OSC).copy(alpha = 0.4f), Offset(pie.x + pw * 0.05f, pie.y - ph * 0.3f), Size(pw * 0.3f, ph * 0.2f))
        }
        val borde = Rect(pie.x - pw * 0.56f, boca - w * 0.026f, pie.x + pw * 0.56f, boca)
        drawRect(t.c(mezcla(P.TERRACOTA, P.CUERO, 0.2f)), borde.topLeft, borde.size)
        clipRect(borde.left, borde.top, borde.right, borde.bottom) { cilindroBotanico(borde, t, -1f, 1.2f) }
        drawRect(t.b(0.45f), borde.topLeft, Size(borde.width, 1f * u))
        drawRect(t.s(0.4f), Offset(borde.left, borde.bottom - 1.2f * u), Size(borde.width, 1.2f * u))
        drawLine(t.c(mezcla(P.NOGAL, P.HOJA, 0.5f)), raiz, raiz + Offset(0f, w * 0.012f), 4f * u)
    }
}
