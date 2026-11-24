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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo que hay en los huecos de las repisas de Horror cósmico ([MuebleEstanteArcano]), en grupos de dos o tres cosas que llenan de
 * siete a nueve décimos del alto: la vela pegada en su plato de lata (la que alumbra de noche) con dos cuadernos tumbados; el aro
 * salvavidas apoyado con un rollo de piola alquitranada delante; la red colgada de una cabilla con sus corchos y una boya; la pila de registros
 * tumbados con un rollo de carta apoyado detrás. Cada hueco, otro grupo; en uno muy ancho, dos.
 */
internal object HuecosEstanteArcano {
    private val P = PaletaArcano
    private val M = MuebleEstanteArcano

    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, xVela: Float, g: MarcoArcano) {
        val w = g.w
        val x0 = oc + w * 0.022f; val x1 = xDer - w * 0.03f
        if (x1 - x0 < w * 0.1f) return
        val piso = r.bottom - w * 0.008f
        if (xVela >= 0f) {
            vela(Offset(xVela, piso), r, g)
            val resto = x1 - (xVela + w * 0.11f)
            if (resto > w * 0.17f) grupo(k + 1, xVela + w * 0.11f, x1, piso, r, g)
            return
        }
        if (x1 - x0 > w * 0.42f) {
            val medio = (x0 + x1) / 2
            grupo(k, x0, medio - w * 0.02f, piso, r, g)
            grupo(k + 1, medio + w * 0.02f, x1, piso, r, g)
        } else grupo(k, x0, x1, piso, r, g)
    }

    /** Uno de los grupos, según la repisa, entre [x0] y [x1]. */
    private fun DrawScope.grupo(k: Int, x0: Float, x1: Float, piso: Float, r: Rect, g: MarcoArcano) {
        val w = g.w
        if (x1 - x0 < w * 0.1f) return
        when (Math.floorMod(k * 7 + 3, 3)) {
            0 -> salvavidas(x0, x1, piso, r, g)
            1 -> red(x0, x1, piso, r, g)
            else -> registros(x0, x1, piso, r, g)
        }
    }

    // ---------------------------------------------------------------- el farolillo del costado

    /**
     * El farolillo de hojalata colgado de una escarpia en el costado de la izquierda, a media altura: el tejadillo de punta con su
     * anilla y sus respiraderos, los vidrios ambarinos entre los listones de lata, la vela de dentro y la bandeja. De noche (y en
     * los Sueños claros, recién prendido) arde y alumbra los tramos de en medio.
     */
    fun DrawScope.farolillo(g: MarcoArcano) {
        if (g.n == 0) return
        val w = g.w; val u = density
        val a = g.am
        val cx = w * M.FL_X
        val fy = g.yFarolillo
        val k = maxOf(g.cerca(Offset(cx, fy)), if (a.oscuro) 0.85f else 0f)
        val an = w * 0.05f
        val top = fy - w * 0.044f; val bajo = fy + w * 0.034f
        val lata = mezcla(P.CINC, P.HIERRO, 0.35f)
        // La escarpia y la anilla.
        val gancho = Offset(cx, top - w * 0.035f)
        drawLine(a.sombra(0.35f), gancho + Offset(1.2f * u, 1.4f * u), Offset(cx + 1.2f * u, top - w * 0.012f), 1.2f * u)
        drawCircle(a.luz(lata, k), w * 0.007f, Offset(cx, top - w * 0.026f), style = Stroke(1.2f * u))
        drawLine(a.luz(lata, k), Offset(cx, top - w * 0.02f), Offset(cx, top - w * 0.014f), 1.2f * u)
        clavoArcano(gancho, 1.8f * u, a, k, P.HIERRO_LUZ)
        // La sombra del farolillo en el costado (de día, a la derecha; de noche no hay: la luz está dentro).
        if (!a.oscuro) drawRect(a.sombra(0.3f), Offset(cx - an / 2 + w * 0.01f, top + w * 0.006f), Size(an, bajo - top))
        // El tejadillo de punta con sus respiraderos.
        val techo = poligonoArcano(cx - an * 0.62f, top, cx + an * 0.62f, top, cx + an * 0.12f, top - w * 0.016f, cx - an * 0.12f, top - w * 0.016f)
        drawPath(techo, a.luz(lata, k))
        drawPath(poligonoArcano(cx + an * 0.05f, top - w * 0.016f, cx + an * 0.12f, top - w * 0.016f, cx + an * 0.62f, top, cx + an * 0.2f, top), a.sombra(0.3f))
        for (j in 0 until 3) drawCircle(a.sombra(0.6f), 0.8f * u, Offset(cx - an * 0.28f + j * an * 0.28f, top - w * 0.006f))
        drawRect(a.brillo(0.4f, k), Offset(cx - an * 0.62f, top - 1f * u), Size(an * 0.8f, 1f * u))
        // Los vidrios, la vela y la llama.
        val vid = Rect(cx - an / 2, top, cx + an / 2, bajo)
        val ambar = mezcla(P.CERA, P.LLAMA, 0.35f)
        if (a.lumbre) {
            mancha(Offset(cx, fy), w * 0.1f, P.LLAMA.copy(alpha = if (a.oscuro) 0.42f else 0.2f))
            drawRect(mezcla(ambar, P.LLAMA, 0.3f).copy(alpha = if (a.oscuro) 0.9f else 0.6f), vid.topLeft, vid.size)
        } else {
            drawRect(a.luz(mezcla(P.VIDRIO_OSC, ambar, 0.35f), k), vid.topLeft, vid.size)
            drawRect(Color.White.copy(alpha = 0.25f), Offset(vid.left, vid.top), Size(vid.width * 0.35f, vid.height))
        }
        val vela = Rect(cx - w * 0.006f, fy + w * 0.006f, cx + w * 0.006f, bajo - w * 0.002f)
        drawRect(if (a.lumbre) mezcla(P.CERA, P.FUEGO, 0.4f) else a.luz(P.CERA, k), vela.topLeft, vela.size)
        if (a.lumbre) {
            val alto = w * (if (a.oscuro && !a.noche) 0.022f else 0.016f)
            val f = Path().apply {
                moveTo(cx - w * 0.005f, vela.top - 0.5f * u)
                cubicTo(cx - w * 0.006f, vela.top - alto * 0.5f, cx - w * 0.001f, vela.top - alto * 0.85f, cx, vela.top - alto)
                cubicTo(cx + w * 0.001f, vela.top - alto * 0.85f, cx + w * 0.006f, vela.top - alto * 0.5f, cx + w * 0.005f, vela.top - 0.5f * u)
                close()
            }
            drawPath(f, P.FUEGO)
            drawPath(f, P.LLAMA, style = Stroke(1f * u))
        } else drawLine(a.luz(P.TINTA, k), Offset(cx, vela.top), Offset(cx, vela.top - 2f * u), 0.9f * u)
        // Los listones de lata: los cantos, el del medio y la puerta con su pestillo.
        val list = a.luz(lata, k)
        listOf(vid.left, vid.right - 2f * u).forEach { x -> drawRect(list, Offset(x, vid.top), Size(2f * u, vid.height)) }
        drawRect(list, Offset(vid.left, vid.top), Size(vid.width, 2f * u))
        drawRect(a.brillo(0.45f, k), Offset(vid.left, vid.top), Size(1f * u, vid.height))
        drawRect(a.luz(P.LATON, k), Offset(vid.right - 3.4f * u, fy - 1f * u), Size(3f * u, 2f * u))
        // La bandeja de abajo.
        drawRect(a.luz(lata, k), Offset(cx - an * 0.6f, bajo), Size(an * 1.2f, w * 0.008f))
        drawRect(a.sombra(0.4f), Offset(cx - an * 0.6f, bajo + w * 0.008f), Size(an * 1.2f, 1.4f * u))
        drawRect(a.brillo(0.35f, k), Offset(cx - an * 0.6f, bajo), Size(an * 1.2f, 1f * u))
    }

    // ---------------------------------------------------------------- la vela

    /**
     * La vela larga pegada en su plato de lata con los chorreones de cera, con dos cuadernos tumbados al lado. De noche (y en los
     * Sueños claros, recién prendida) arde: su halo en el paño, en la tabla de arriba y en lo que tiene cerca.
     */
    private fun DrawScope.vela(p: Offset, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.am
        val k = g.cerca(Offset(p.x, p.y - r.height * 0.4f))
        val llama = Offset(p.x, r.bottom - r.height * M.VELA_Y)
        // Los cuadernos tumbados, a la derecha de la vela, uno sobre otro (de hule negro, de cartón y de tela verde), y el tintero.
        val c0 = Rect(p.x + w * 0.018f, p.y - w * 0.026f, p.x + w * 0.122f, p.y)
        val c1 = Rect(p.x + w * 0.026f, c0.top - w * 0.022f, p.x + w * 0.114f, c0.top)
        val c2 = Rect(p.x + w * 0.02f, c1.top - w * 0.02f, p.x + w * 0.104f, c1.top)
        contactoArcano(Offset(c0.center.x, p.y), c0.width * 1.1f, w * 0.01f, a, 0.4f, 1f)
        listOf(c0 to P.HULE, c1 to mezcla(P.MINIO, P.TECA_OSC, 0.4f), c2 to mezcla(P.CASCO, P.MAR, 0.4f)).forEach { (cr, col) ->
            drawRect(a.luz(col, k), cr.topLeft, cr.size)
            drawRect(a.luz(P.PAPEL, k), Offset(cr.left, cr.top + cr.height * 0.25f), Size(cr.width * 0.94f, cr.height * 0.5f))
            for (j in 1..3) drawLine(a.sombra(0.25f), Offset(cr.left, cr.top + cr.height * (0.25f + 0.12f * j)), Offset(cr.left + cr.width * 0.94f, cr.top + cr.height * (0.25f + 0.12f * j)), 0.6f * u)
            drawRect(a.brillo(0.4f, k), cr.topLeft, Size(cr.width, 1f * u))
            drawRect(a.sombra(0.35f), Offset(cr.right - cr.width * 0.06f, cr.top), Size(cr.width * 0.06f, cr.height))
        }
        // El tintero de vidrio cuadrado encima, con su pluma.
        val ti = Rect(c2.left + c2.width * 0.5f, c2.top - w * 0.03f, c2.left + c2.width * 0.5f + w * 0.032f, c2.top)
        drawRect(a.sombra(0.35f), Offset(ti.left + 1.4f * u, ti.bottom - 1f * u), Size(ti.width, 2f * u))
        drawRect(a.luz(mezcla(P.VIDRIO, P.HIERRO, 0.5f), k), ti.topLeft, ti.size)
        drawRect(a.luz(Color(0xFF1A1E2A), k), Offset(ti.left + 1.4f * u, ti.top + ti.height * 0.4f), Size(ti.width - 2.8f * u, ti.height * 0.55f))
        drawRect(a.brillo(0.6f, k), Offset(ti.left + 1.4f * u, ti.top + 1.4f * u), Size(1.2f * u, ti.height * 0.7f))
        drawRect(a.luz(P.HIERRO, k), Offset(ti.center.x - w * 0.008f, ti.top - w * 0.006f), Size(w * 0.016f, w * 0.006f))
        drawLine(a.luz(P.TECA_OSC, k), Offset(ti.center.x, ti.top - w * 0.002f), Offset(ti.center.x + w * 0.03f, ti.top - w * 0.06f), 1.6f * u, StrokeCap.Round)
        drawLine(a.luz(P.LATON, k), Offset(ti.center.x, ti.top - w * 0.002f), Offset(ti.center.x + w * 0.006f, ti.top - w * 0.013f), 1.6f * u)
        // El plato de lata con su asa de anilla.
        val plato = Rect(p.x - w * 0.035f, p.y - w * 0.012f, p.x + w * 0.035f, p.y)
        contactoArcano(Offset(p.x, p.y), plato.width * 1.2f, w * 0.01f, a, 0.45f, 1f)
        drawOval(a.luz(P.CINC_OSC, k), plato.topLeft, plato.size)
        drawOval(a.luz(P.CINC, k), Offset(plato.left + 1f * u, plato.top), Size(plato.width - 2f * u, plato.height * 0.55f))
        drawCircle(a.luz(P.CINC_OSC, k), w * 0.01f, Offset(plato.left - w * 0.004f, plato.center.y), style = Stroke(1.3f * u))
        // La vela: alta, con sus chorreones; la cera que se juntó en el plato.
        val vela = Rect(p.x - w * 0.009f, llama.y + w * 0.012f, p.x + w * 0.009f, p.y - w * 0.006f)
        val cera = if (a.lumbre) a.luz(P.CERA, maxOf(k, 0.9f)) else P.CERA
        drawRect(cera, vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawRoundRect(cera, Offset(vela.left - 1.2f * u, vela.top + w * 0.01f), Size(2.4f * u, w * 0.04f), CornerRadius(1.2f * u))
        drawRoundRect(cera, Offset(vela.right - 1f * u, vela.top + w * 0.02f), Size(2f * u, w * 0.025f), CornerRadius(1f * u))
        drawOval(cera, Offset(p.x - w * 0.022f, p.y - w * 0.016f), Size(w * 0.044f, w * 0.01f))
        drawOval(a.luz(P.CERA_OSC, k), Offset(vela.left, vela.top - 1f * u), Size(vela.width, 2f * u))
        drawLine(a.luz(P.TINTA, k), Offset(p.x, vela.top), Offset(p.x + 0.4f * u, vela.top - 2.4f * u), 1f * u)
        if (a.lumbre) {
            val alto = w * (if (a.oscuro && !a.noche) 0.026f else 0.018f)
            mancha(llama, w * 0.08f, P.LLAMA.copy(alpha = if (a.oscuro) 0.4f else 0.22f))
            val f = Path().apply {
                moveTo(p.x - w * 0.0065f, vela.top - 1f * u)
                cubicTo(p.x - w * 0.008f, vela.top - alto * 0.5f, p.x - w * 0.002f, vela.top - alto * 0.8f, p.x + 0.3f * u, vela.top - alto)
                cubicTo(p.x + w * 0.003f, vela.top - alto * 0.8f, p.x + w * 0.008f, vela.top - alto * 0.5f, p.x + w * 0.0065f, vela.top - 1f * u)
                close()
            }
            drawPath(f, mezcla(P.LLAMA, P.BRASA, 0.2f))
            drawOval(P.FUEGO, Offset(p.x - w * 0.003f, vela.top - alto * 0.55f), Size(w * 0.006f, alto * 0.45f))
            // El filo de la llama en el canto de la tabla de arriba.
            drawRect(P.LLAMA.copy(alpha = if (a.oscuro) 0.3f else 0.15f), Offset(p.x - w * 0.12f, r.top), Size(w * 0.24f, 1.6f * u))
        }
    }

    // ---------------------------------------------------------------- el salvavidas y el rollo de piola

    /**
     * El aro salvavidas del barco, de corcho forrado de lona, apoyado en el paño y algo ladeado, con su guirnalda de cabo cogida
     * en tres ligadas desiguales; delante, tapándolo en parte, un rollo de piola alquitranada con su chicote.
     */
    private fun DrawScope.salvavidas(x0: Float, x1: Float, piso: Float, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.am
        val rr = minOf(r.height * 0.36f, (x1 - x0) * 0.42f)
        val c = Offset(x0 + rr * 1.15f, piso - rr * 1.02f)
        val k = g.cerca(c)
        val hondo = rr * 0.36f
        val fuera = Path().apply { addOval(Rect(c.x - rr, c.y - rr * 0.96f, c.x + rr, c.y + rr * 0.96f)) }
        val dentro = Path().apply { addOval(Rect(c.x - rr + hondo * 1.55f, c.y - rr * 0.96f + hondo * 1.5f, c.x + rr - hondo * 1.55f, c.y + rr * 0.96f - hondo * 1.5f)) }
        val aro = Path().apply { op(fuera, dentro, androidx.compose.ui.graphics.PathOperation.Difference) }
        rotate(-7f, c) {
            // La sombra en el paño (por el hueco se ve el paño, con la sombra del canto de dentro).
            translate(if (a.oscuro) -w * 0.012f else w * 0.014f, w * 0.006f) { drawPath(aro, a.sombra(0.32f)) }
            val lona = mezcla(P.LONA, P.YESO, 0.45f)
            drawPath(aro, a.luz(lona, k))
            clipPath(aro) {
                // El redondo del corcho: luz arriba a la izquierda, sombra abajo a la derecha, y la sombra del canto de dentro.
                drawOval(a.brillo(0.35f, k), Offset(c.x - rr, c.y - rr), Size(rr * 1.2f, rr * 1.1f))
                drawOval(a.sombra(0.3f), Offset(c.x - rr * 0.2f, c.y - rr * 0.1f), Size(rr * 1.3f, rr * 1.15f))
                drawOval(a.sombra(0.35f), Offset(c.x - rr + hondo * 1.4f, c.y - rr * 0.96f + hondo * 1.3f), Size(2 * rr - hondo * 2.8f, (2 * rr - hondo * 2.8f) * 0.5f))
                // La mugre y el salitre de la lona, y la costura de fuera.
                pinceladas(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr), a.luz(P.SALOBRE, k), 8250, 8, u, 0.22f)
            }
            drawPath(dentro, a.sombra(0.3f), style = Stroke(1.2f * u))
            drawPath(fuera, a.luz(mezcla(P.LONA_OSC, P.TECA_OSC, 0.3f), k), style = Stroke(1f * u))
            // Las cuatro ligadas y la guirnalda de cabo que cuelga entre ellas, por fuera del aro.
            val cabo = a.luz(mezcla(P.LONA_OSC, P.CORCHO, 0.4f), k)
            // Tres ligadas desiguales (cuatro en aspa harían una cruz dentro de un círculo).
            val angs = listOf(-2.55f, -0.35f, 1.75f)
            val pts = angs.map { t -> Offset(c.x + rr * kotlin.math.cos(t), c.y + rr * 0.96f * kotlin.math.sin(t)) }
            for (i in pts.indices) {
                val p0 = pts[i]; val p1 = pts[(i + 1) % pts.size]
                val m = (p0 + p1) / 2f
                val fuerade = Offset(m.x - c.x, m.y - c.y)
                val l = fuerade.getDistance().coerceAtLeast(1f)
                val ctrl = m + Offset(fuerade.x / l, fuerade.y / l) * (rr * 0.32f) + Offset(0f, rr * 0.12f)
                val guirnalda = Path().apply { moveTo(p0.x, p0.y); quadraticTo(ctrl.x, ctrl.y, p1.x, p1.y) }
                translate(1.4f * u, 1.8f * u) { drawPath(guirnalda, a.sombra(0.35f), style = Stroke(w * 0.006f)) }
                drawPath(guirnalda, cabo, style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                drawPath(guirnalda, a.sombra(0.3f), style = Stroke(w * 0.006f, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 2.2f * u))))
            }
            angs.forEachIndexed { i, t ->
                val p = Offset(c.x + rr * 0.82f * kotlin.math.cos(t), c.y + rr * 0.8f * kotlin.math.sin(t))
                rotate(t * 57.3f + 90f, p) {
                    drawRect(a.luz(mezcla(P.LONA_OSC, P.TECA_OSC, 0.2f), k), Offset(p.x - hondo * 0.95f, p.y - w * 0.006f), Size(hondo * 1.9f, w * 0.012f))
                    drawRect(a.sombra(0.3f), Offset(p.x - hondo * 0.95f, p.y + w * 0.004f), Size(hondo * 1.9f, w * 0.002f))
                }
            }
        }
        // El rollo de piola, delante y a la derecha.
        rolloPiola(Offset(minOf(x1 - w * 0.06f, c.x + rr * 0.5f), piso - w * 0.014f), piso, g)
    }

    /** Un rollo de piola alquitranada: vueltas apiladas, con el chicote suelto sobre la tabla. */
    private fun DrawScope.rolloPiola(rc: Offset, piso: Float, g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.am
        val rk = g.cerca(rc)
        contactoArcano(Offset(rc.x, piso), w * 0.13f, w * 0.012f, a, 0.45f, 1f)
        val piola = a.luz(mezcla(P.PIOLA, P.TECA, 0.25f), rk)
        for (q in 0 until 4) {
            val rr = Rect(rc.x - w * 0.055f, rc.y - w * 0.012f - q * w * 0.009f, rc.x + w * 0.055f, rc.y + w * 0.012f - q * w * 0.009f)
            drawOval(a.sombra(0.4f), rr.topLeft + Offset(0f, 1.6f * u), rr.size, style = Stroke(w * 0.008f))
            drawOval(piola, rr.topLeft, rr.size, style = Stroke(w * 0.008f))
            drawArc(a.brillo(0.4f, rk), 190f, 120f, false, rr.topLeft, rr.size, style = Stroke(0.8f * u))
        }
        val chicote = Path().apply { moveTo(rc.x + w * 0.05f, rc.y); cubicTo(rc.x + w * 0.07f, rc.y + w * 0.01f, rc.x + w * 0.06f, piso, rc.x + w * 0.085f, piso - 1f * u) }
        drawPath(chicote, piola, style = Stroke(w * 0.007f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- la red colgada y la boya

    /**
     * La red de pesca colgada de una cabilla clavada arriba en el fondo: cae en pliegues de malla, con la relinga de corchos en
     * diagonal, y se amontona en la tabla; delante, una boya de vidrio en su red.
     */
    private fun DrawScope.red(x0: Float, x1: Float, piso: Float, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.am
        val an = minOf(x1 - x0, w * 0.22f)
        val rx = x0 + (x1 - x0 - an) * 0.3f
        val arriba = r.top + r.height * 0.14f
        val clavo = Offset(rx + an * 0.45f, arriba)
        val c = Offset(rx + an / 2, (arriba + piso) / 2)
        val k = g.cerca(c)
        // La silueta: estrecha arriba (recogida en la cabilla) y abierta abajo, donde se amontona.
        val red = Path().apply {
            moveTo(clavo.x - w * 0.01f, arriba)
            cubicTo(rx + an * 0.2f, arriba + r.height * 0.25f, rx - an * 0.02f, piso - r.height * 0.3f, rx - an * 0.06f, piso)
            lineTo(rx + an * 1.02f, piso)
            cubicTo(rx + an * 0.96f, piso - r.height * 0.22f, rx + an * 0.7f, arriba + r.height * 0.3f, clavo.x + w * 0.01f, arriba)
            close()
        }
        translate(if (a.oscuro) -w * 0.012f else w * 0.014f, w * 0.004f) { drawPath(red, a.sombra(0.3f)) }
        val malla = mezcla(P.LONA_OSC, P.MAR_OSC, 0.4f)
        drawPath(red, a.luz(malla, k))
        clipPath(red) {
            // La malla en rombos, y los pliegues que bajan.
            val hilos = Path()
            var x = rx - an
            while (x < rx + an * 2) { hilos.moveTo(x, piso); hilos.lineTo(x + r.height, piso - r.height * 1.1f); hilos.moveTo(x + r.height, piso); hilos.lineTo(x, piso - r.height * 1.1f); x += w * 0.012f }
            drawPath(hilos, a.luz(mezcla(malla, P.LONA, 0.6f), k).copy(alpha = 0.55f), style = Stroke(0.7f * u))
            for (j in 0 until 4) {
                val px = rx + an * (0.18f + 0.22f * j)
                val p = Path().apply { moveTo(clavo.x, arriba); quadraticTo(px - an * 0.05f, (arriba + piso) / 2, px, piso) }
                drawPath(p, a.sombra(0.35f), style = Stroke(3f * u, cap = StrokeCap.Round))
                drawPath(Path().apply { addPath(p, Offset(2.6f * u, 0f)) }, a.luz(mezcla(malla, P.LONA, 0.5f), k).copy(alpha = 0.5f), style = Stroke(1f * u))
            }
            drawRect(a.sombra(0.25f), Offset(rx + an * 0.65f, arriba), Size(an * 0.5f, piso - arriba))
            if (!a.oscuro) drawRect(a.claro(0.1f), Offset(rx, arriba), Size(an * 0.25f, piso - arriba))
        }
        // La cabilla clavada en el fondo, de la que cuelga.
        drawRect(a.sombra(0.4f), Offset(clavo.x - w * 0.012f + 1.6f * u, clavo.y - w * 0.006f + 2f * u), Size(w * 0.03f, w * 0.01f))
        drawRoundRect(a.luz(P.TECA, k), Offset(clavo.x - w * 0.012f, clavo.y - w * 0.006f), Size(w * 0.03f, w * 0.01f), CornerRadius(w * 0.005f))
        drawRect(a.brillo(0.4f, k), Offset(clavo.x - w * 0.01f, clavo.y - w * 0.005f), Size(w * 0.02f, 0.9f * u))
        // La relinga con sus corchos, en diagonal por delante.
        val p0 = Offset(clavo.x, arriba + r.height * 0.06f); val p1 = Offset(rx + an * 0.95f, piso - r.height * 0.1f)
        val relinga = Path().apply { moveTo(p0.x, p0.y); quadraticTo(rx + an * 0.2f, (p0.y + p1.y) / 2 + r.height * 0.15f, p1.x, p1.y) }
        drawPath(relinga, a.luz(P.PIOLA, k), style = Stroke(1.3f * u))
        for (j in 0 until 4) {
            val t = 0.18f + 0.22f * j
            val q0 = p0 * ((1 - t) * (1 - t)); val q1 = Offset(rx + an * 0.2f, (p0.y + p1.y) / 2 + r.height * 0.15f) * (2 * t * (1 - t)); val q2 = p1 * (t * t)
            val p = q0 + q1 + q2
            val cr = Rect(p.x - w * 0.012f, p.y - w * 0.008f, p.x + w * 0.012f, p.y + w * 0.008f)
            drawRoundRect(a.sombra(0.35f), cr.topLeft + Offset(1f * u, 1.4f * u), cr.size, CornerRadius(w * 0.006f))
            drawRoundRect(a.luz(P.CORCHO, k), cr.topLeft, cr.size, CornerRadius(w * 0.006f))
            drawRect(a.brillo(0.35f, k), Offset(cr.left + 1f * u, cr.top + 0.8f * u), Size(cr.width * 0.5f, 0.9f * u))
            drawLine(a.sombra(0.3f), Offset(cr.center.x, cr.top), Offset(cr.center.x, cr.bottom), 0.7f * u)
        }
        // La boya de vidrio, delante, a la derecha, sobre la tabla.
        val br = w * 0.034f
        val bc = Offset(minOf(x1 - br * 1.2f, rx + an * 1.0f), piso - br * 0.98f)
        val bk = g.cerca(bc)
        contactoArcano(Offset(bc.x, piso), br * 2.2f, br * 0.3f, a, 0.45f, 1f)
        val esfera = Path().apply { addOval(Rect(bc.x - br, bc.y - br, bc.x + br, bc.y + br)) }
        drawPath(esfera, a.luz(mezcla(P.BOYA, P.VIDRIO, 0.3f), bk))
        clipPath(esfera) {
            drawCircle(a.luz(P.BOYA_OSC, bk).copy(alpha = 0.6f), br, bc + Offset(br * 0.45f, br * 0.1f))
            drawCircle(a.luz(mezcla(P.BOYA, P.VIDRIO_LUZ, 0.5f), bk), br * 0.38f, bc + Offset(br * 0.3f, br * 0.45f))
            if (a.oscuro && bk > 0.1f) drawCircle(P.LLAMA.copy(alpha = (0.5f * bk).coerceAtMost(0.4f)), br, bc + Offset(br * 1.1f, 0f))
        }
        val ref = Rect(bc.x - br * 0.6f, bc.y - br * 0.6f, bc.x - br * 0.2f, bc.y - br * 0.15f)
        drawRoundRect(if (a.oscuro) P.HAZ.copy(alpha = 0.2f + 0.3f * g.frio(bc)) else Color.White.copy(alpha = 0.75f), ref.topLeft, ref.size, CornerRadius(br * 0.1f))
        val mallaBoya = Path()
        for (j in -2..2) { val f = j / 3f; mallaBoya.moveTo(bc.x + br * f * 0.3f, bc.y - br); mallaBoya.quadraticTo(bc.x + br * f * 1.9f, bc.y, bc.x + br * f * 0.6f, bc.y + br) }
        listOf(-0.4f, 0.1f, 0.55f).forEach { f -> val yy = bc.y + br * f; val anc = br * kotlin.math.sqrt((1f - f * f).coerceAtLeast(0f)); mallaBoya.moveTo(bc.x - anc, yy); mallaBoya.quadraticTo(bc.x, yy + br * 0.18f, bc.x + anc, yy) }
        drawPath(mallaBoya, a.luz(P.PIOLA, bk), style = Stroke(1f * u))
        drawPath(esfera, a.luz(P.BOYA_OSC, bk).copy(alpha = 0.6f), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- los registros tumbados y la carta

    /** Una pila de registros tumbados, hinchados por la humedad, con un rollo de carta apoyado detrás, alto y ladeado. */
    private fun DrawScope.registros(x0: Float, x1: Float, piso: Float, r: Rect, g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.am
        val an = minOf(x1 - x0, w * 0.2f)
        val bx = x0 + (x1 - x0 - an) * 0.5f
        // El rollo de carta, detrás, apoyado en el paño.
        val rb = Offset(bx + an * 0.78f, piso); val rt = Offset(bx + an * 0.95f, piso - r.height * 0.84f)
        val rk = g.cerca((rb + rt) / 2f)
        val d = rt - rb; val l = d.getDistance(); val n = Offset(-d.y / l, d.x / l) * (w * 0.016f)
        val rollo = poligonoArcano(rb.x - n.x, rb.y - n.y, rt.x - n.x, rt.y - n.y, rt.x + n.x, rt.y + n.y, rb.x + n.x, rb.y + n.y)
        translate(if (a.oscuro) -w * 0.01f else w * 0.012f, 0f) { drawPath(rollo, a.sombra(0.3f)) }
        drawPath(rollo, a.luz(P.PAPEL, rk))
        clipPath(rollo) { translate(n.x * 1.1f, n.y * 1.1f) { drawPath(rollo, a.sombra(0.25f)) }; translate(-n.x, -n.y) { drawPath(rollo, a.brillo(0.3f, rk)) } }
        drawOval(a.luz(P.PAPEL_OSC, rk), Offset(rt.x - n.getDistance(), rt.y - n.getDistance() * 0.5f), Size(n.getDistance() * 2, n.getDistance()))
        val cin = rb + d * 0.55f
        drawLine(a.luz(P.PIOLA, rk), cin - n * 1.05f, cin + n * 1.05f, 2f * u)
        // La pila: cuatro registros, cada uno con su lomo de piel, sus planos de tela y las hojas onduladas que asoman.
        val telas = listOf(Color(0xFF2E4A40), Color(0xFF6E2A22), Color(0xFF4E3A28), Color(0xFF3A3A52))
        val k = g.cerca(Offset(bx + an * 0.4f, piso - r.height * 0.2f))
        contactoArcano(Offset(bx + an * 0.4f, piso), an * 0.95f, w * 0.012f, a, 0.45f, 1f)
        var y = piso
        for (i in 0 until 4) {
            val gr = w * (0.024f - 0.003f * (i % 2))
            val sx = an * 0.04f * (azar(8400 + i, 1) - 0.5f)
            val lr = Rect(bx + sx + an * 0.02f * i, y - gr, bx + sx + an * (0.78f - 0.04f * i), y)
            drawRect(a.luz(telas[(i + (x0.toInt() / 7)) % 4], k), lr.topLeft, lr.size)
            drawRect(a.luz(P.TECA_OSC, k), lr.topLeft, Size(lr.width * 0.14f, lr.height))
            drawRect(a.luz(P.TECA_OSC, k), Offset(lr.right - lr.width * 0.14f, lr.top), Size(lr.width * 0.14f, lr.height))
            listOf(0.38f, 0.62f).forEach { f -> drawLine(a.sombra(0.35f), Offset(lr.left + lr.width * f, lr.top), Offset(lr.left + lr.width * f, lr.bottom), 1f * u) }
            drawRect(a.brillo(0.3f, k), lr.topLeft, Size(lr.width, 1f * u))
            drawRect(a.sombra(0.35f), Offset(lr.left, lr.bottom - lr.height * 0.25f), Size(lr.width, lr.height * 0.25f))
            if (i == 1) drawRect(a.luz(P.MANILA, k), Offset(lr.left + lr.width * 0.44f, lr.top + lr.height * 0.25f), Size(lr.width * 0.12f, lr.height * 0.5f))
            y -= gr
        }
        // Un legajo suelto encima, con su cinta, un poco girado.
        val leg = Rect(bx + an * 0.08f, y - w * 0.012f, bx + an * 0.6f, y)
        rotate(-3f, leg.center) {
            drawRect(a.sombra(0.35f), Offset(leg.left + 1.4f * u, leg.top + 1.6f * u), leg.size)
            drawRect(a.luz(P.MANILA, k), leg.topLeft, leg.size)
            drawRect(a.luz(P.PAPEL, k), Offset(leg.left + 1f * u, leg.top + 1f * u), Size(leg.width - 3f * u, leg.height * 0.4f))
            drawLine(a.luz(P.LACRE, k), Offset(leg.center.x, leg.top), Offset(leg.center.x, leg.bottom), 1.6f * u)
        }
    }
}
