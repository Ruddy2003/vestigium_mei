package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CABLE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CAFE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en el hueco libre de una repisa (de [ocupado] al costado derecho): grupos de dos o tres cosas del cuarto, solapadas,
 * del 70 al 90 % del alto del hueco y distintas de una repisa a otra: el módem con el teléfono encima de los manuales; el soldador
 * en su soporte con el estaño y la placa de pruebas; la grabadora de datos con los casetes y el joystick; las cajas de disquetes
 * con la lata; los manuales tumbados con el ratón y uno de pie. Si el hueco es muy ancho, dos grupos. En el tablero de atrás, un
 * gancho con unos alicates o un rollo de cable.
 */
/** El orden en que se reparten los grupos por repisa (que dos repisas seguidas no repitan). */
private val ORDEN_HUECOS_CIBER = intArrayOf(2, 0, 4, 1, 3)

internal fun DrawScope.huecoLibreCiber(k: Int, r: Rect, ocupado: Float, g: MarcoCiber, usados: BooleanArray) {
    val w = g.w
    val xa = ocupado + w * 0.022f
    val xb = r.right - w * 0.034f
    val libre = xb - xa
    if (libre < w * 0.12f || r.height < 8f) return
    val piso = r.bottom - w * 0.009f
    val alto = r.height
    clipRect(r.left, r.top, r.right, r.bottom) {
        // El grupo de cada hueco: el siguiente del orden que todavía no salió en este estante (no se repiten hasta agotar los cinco).
        fun elegir(desde: Int): Int {
            for (j in 0 until 5) {
                val c = ORDEN_HUECOS_CIBER[Math.floorMod(desde + j, 5)]
                if (!usados[c]) { usados[c] = true; return c }
            }
            return ORDEN_HUECOS_CIBER[Math.floorMod(desde, 5)]
        }
        val i = Math.floorMod(k + k / 5 + g.n, 5)
        if (libre > w * 0.44f) {
            val primero = elegir(i)
            ganchoCiber(g, Offset(xa + libre * 0.5f, r.top + alto * 0.2f), k)
            grupoCiber(primero, Rect(xa, r.top, xa + libre * 0.46f, piso), alto, g, k)
            grupoCiber(elegir(i + 3), Rect(xa + libre * 0.54f, r.top, xb, piso), alto, g, k + 7)
        } else {
            if (libre > w * 0.26f) ganchoCiber(g, Offset(xb - libre * 0.2f, r.top + alto * 0.2f), k)
            grupoCiber(elegir(i), Rect(xa, r.top, xb, piso), alto, g, k)
        }
    }
}

private fun DrawScope.grupoCiber(cual: Int, z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    when (cual) {
        0 -> modemTelefonoCiber(z, alto, g, s)
        1 -> soldadorCiber(z, alto, g, s)
        2 -> casetesCiber(z, alto, g, s)
        3 -> cajasLataCiber(z, alto, g, s)
        else -> manualesRatonCiber(z, alto, g, s)
    }
}

/** Un manual de anillas tumbado: el lomo de vinilo de [col] con su portaetiquetas sin texto y el agujero del dedo. */
private fun DrawScope.manualTumbadoCiber(r: Rect, col: Color, g: MarcoCiber) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    val tapa = poligonoCiber(r.left, r.top, r.left + w * 0.008f, r.top - w * 0.01f, r.right + w * 0.004f, r.top - w * 0.01f, r.right, r.top)
    drawPath(tapa, t.c(mezcla(col, Color.White, 0.08f)))
    drawRoundRect(t.c(col), r.topLeft, r.size, CornerRadius(r.height * 0.3f))
    clipRect(r.left, r.top, r.right, r.bottom) { planosCiber(r, t, 0.8f) }
    drawRect(t.b(0.55f), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 0.9f * u))
    val et = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.25f, r.left + r.width * 0.48f, r.bottom - r.height * 0.25f)
    drawRect(t.c(mezcla(PAPEL, col, 0.25f)), et.topLeft, et.size)
    drawCircle(NOCHE.copy(alpha = 0.7f), r.height * 0.2f, Offset(r.left + r.width * 0.76f, r.center.y))
}

/** El módem tumbado sobre dos manuales, con el teléfono de teclas lisas encima, su auricular y el cable en espiral. */
private fun DrawScope.modemTelefonoCiber(z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    val u = density; val w = g.w
    val ancho = minOf(z.width, w * 0.24f)
    val x0 = z.left + (z.width - ancho) * 0.3f
    val t = g.t(x0 + ancho / 2, z.bottom - alto * 0.3f)
    contactoCiber(Offset(x0 + ancho / 2, z.bottom), ancho * 1.1f, w * 0.012f, t, 0.6f)
    val gm = alto * 0.12f
    manualTumbadoCiber(Rect(x0, z.bottom - gm, x0 + ancho, z.bottom), mezcla(PARED, NEGRO, 0.3f), g)
    manualTumbadoCiber(Rect(x0 + w * 0.006f, z.bottom - 2 * gm - w * 0.002f, x0 + ancho - w * 0.004f, z.bottom - gm - w * 0.002f), mezcla(ROJO, NEGRO, 0.55f), g)
    // El módem: la caja beis vista un poco desde arriba, la franja de los pilotos.
    val mb = z.bottom - 2 * gm - w * 0.012f
    val mr = Rect(x0 + ancho * 0.04f, mb - alto * 0.16f, x0 + ancho * 0.96f, mb)
    val tm = g.t(mr.center)
    drawPath(poligonoCiber(mr.left, mr.top, mr.left + w * 0.008f, mr.top - w * 0.01f, mr.right + w * 0.004f, mr.top - w * 0.01f, mr.right, mr.top), tm.c(mezcla(BEIS, Color.White, 0.06f)))
    drawRect(tm.c(BEIS), mr.topLeft, mr.size)
    clipRect(mr.left, mr.top, mr.right, mr.bottom) { planosCiber(mr, tm, 0.9f) }
    drawRect(tm.b(0.7f), mr.topLeft, Size(mr.width, 0.9f * u))
    val fr = Rect(mr.left + mr.width * 0.08f, mr.top + mr.height * 0.35f, mr.right - mr.width * 0.08f, mr.top + mr.height * 0.7f)
    drawRect(tm.c(mezcla(NEGRO, PARED, 0.2f)), fr.topLeft, fr.size)
    val encendidos = listOf(true, Math.floorMod(s, 2) == 0, false, true, false, true)
    val colores = listOf(VERDE, VERDE, AMBAR, AMBAR, ROJO, VERDE)
    for (j in 0 until 6) pilotoCiber(Offset(fr.left + fr.width * (0.1f + 0.16f * j), fr.center.y), 1.4f * u, colores[j], encendidos[j], tm)
    // El teléfono: la base en cuña con el teclado de teclas lisas y el auricular en su horquilla.
    val tb = mr.top - w * 0.008f
    val tw = ancho * 0.62f
    val tx = x0 + ancho * 0.1f
    val tt = g.t(tx + tw / 2, tb - alto * 0.15f)
    contactoCiber(Offset(tx + tw / 2, tb + w * 0.002f), tw * 1.05f, w * 0.01f, tt, 0.6f)
    val base = poligonoCiber(tx, tb, tx + tw * 0.08f, tb - alto * 0.2f, tx + tw * 0.92f, tb - alto * 0.2f, tx + tw, tb)
    drawPath(base, tt.c(mezcla(PAPEL, BEIS, 0.5f)))
    clipPath(base) {
        planosCiber(Rect(tx, tb - alto * 0.2f, tx + tw, tb), tt, 0.9f)
        for (fi in 0 until 3) for (c in 0 until 3) {
            val kx = tx + tw * (0.3f + 0.14f * c); val ky = tb - alto * (0.15f - 0.045f * fi)
            drawRect(NOCHE.copy(alpha = 0.35f), Offset(kx, ky + 1f * u), Size(tw * 0.1f, alto * 0.03f))
            drawRect(tt.c(mezcla(BEIS, Color.White, 0.2f)), Offset(kx, ky), Size(tw * 0.1f, alto * 0.028f))
        }
    }
    drawLine(tt.b(0.6f), Offset(tx + tw * 0.08f, tb - alto * 0.2f), Offset(tx + tw * 0.92f, tb - alto * 0.2f), 1f * u)
    // El auricular, tumbado en la horquilla, más alto que la base.
    val ay = tb - alto * 0.28f
    val aur = Path().apply {
        moveTo(tx - tw * 0.02f, ay + alto * 0.06f)
        cubicTo(tx - tw * 0.04f, ay - alto * 0.04f, tx + tw * 0.08f, ay - alto * 0.03f, tx + tw * 0.2f, ay - alto * 0.02f)
        lineTo(tx + tw * 0.8f, ay - alto * 0.02f)
        cubicTo(tx + tw * 0.92f, ay - alto * 0.03f, tx + tw * 1.04f, ay - alto * 0.04f, tx + tw * 1.02f, ay + alto * 0.06f)
        lineTo(tx + tw * 0.86f, ay + alto * 0.07f)
        cubicTo(tx + tw * 0.8f, ay + alto * 0.02f, tx + tw * 0.2f, ay + alto * 0.02f, tx + tw * 0.14f, ay + alto * 0.07f)
        close()
    }
    drawPath(aur, NOCHE.copy(alpha = 0.4f), style = Stroke(3f * u))
    drawPath(aur, tt.c(mezcla(PAPEL, BEIS, 0.4f)))
    drawPath(aur, tt.b(0.55f), style = Stroke(0.9f * u))
    // El cable en espiral, que baja por delante.
    for (j in 0 until 7) {
        val q = Offset(tx + tw * 1.02f + j * 0.4f * u, ay + alto * 0.07f + j * alto * 0.022f)
        drawOval(tt.c(mezcla(PAPEL, BEIS_OSC, 0.4f)), Offset(q.x - 2f * u, q.y - 1.4f * u), Size(4f * u, 2.8f * u), style = Stroke(0.8f * u))
    }
}

/**
 * El soldador en su soporte de espiral, con la esponja húmeda en su bandeja; el carrete de estaño y, apoyada de canto contra el
 * tablero, la placa de pruebas blanca con su rejilla de agujeros y los cables de colores.
 */
private fun DrawScope.soldadorCiber(z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    val u = density; val w = g.w
    // La placa de pruebas, de canto contra el fondo, un poco ladeada.
    val pw = minOf(z.width * 0.55f, w * 0.14f)
    val px = z.left + z.width * 0.38f
    val placa = Rect(px, z.bottom - alto * 0.78f, px + pw, z.bottom - w * 0.004f)
    val tp = g.t(placa.center)
    rotate(5f, Offset(placa.left, placa.bottom)) {
        drawRect(NOCHE.copy(alpha = 0.4f), placa.translate(w * 0.012f, -w * 0.004f).topLeft, placa.size)
        drawRect(tp.c(mezcla(PAPEL, Color.White, 0.2f)), placa.topLeft, placa.size)
        clipRect(placa.left, placa.top, placa.right, placa.bottom) {
            planosCiber(placa, tp, 0.6f)
            val puntos = ArrayList<Offset>()
            var y = placa.top + w * 0.01f
            while (y < placa.bottom - w * 0.006f) {
                var x = placa.left + w * 0.008f
                while (x < placa.right - w * 0.005f) { if (Math.abs(x - placa.center.x) > w * 0.006f) puntos.add(Offset(x, y)); x += w * 0.0085f }
                y += w * 0.0085f
            }
            drawPoints(puntos, PointMode.Points, tp.c(mezcla(ACERO_OSC, NOCHE, 0.3f)), 1.6f * u, StrokeCap.Square)
            drawRect(tp.c(mezcla(ROJO, PAPEL, 0.3f)), Offset(placa.left, placa.top + w * 0.003f), Size(pw, 1.2f * u))
            drawRect(tp.c(Color(0xFF2F5E9E)), Offset(placa.left, placa.bottom - w * 0.006f), Size(pw, 1.2f * u))
        }
        // Los cables de colores, en arcos cortos.
        listOf(ROJO, Color(0xFF2F8A4E), NOTA, Color(0xFF2F5E9E)).forEachIndexed { j, col ->
            val a0 = Offset(placa.left + pw * (0.15f + 0.18f * j), placa.top + placa.height * (0.25f + 0.13f * j))
            val a1 = Offset(a0.x + pw * (0.2f + 0.1f * azar(6400 + s, j)), a0.y + placa.height * 0.18f)
            drawPath(Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x, a0.y - w * 0.015f, a1.x, a1.y - w * 0.02f, a1.x, a1.y) }, tp.c(col, 1.1f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
        // Un chip clavado.
        drawRect(tp.c(NEGRO), Offset(placa.left + pw * 0.55f, placa.top + placa.height * 0.55f), Size(pw * 0.3f, w * 0.012f))
    }
    // El soporte: la base de hierro, la bandeja con la esponja y la espiral, y el soldador tendido en ella.
    val bx = z.left + z.width * 0.02f
    val bw = minOf(z.width * 0.42f, w * 0.12f)
    val tb = g.t(bx + bw / 2, z.bottom - alto * 0.2f)
    contactoCiber(Offset(bx + bw / 2, z.bottom), bw * 1.2f, w * 0.012f, tb, 0.6f)
    val base = Rect(bx, z.bottom - alto * 0.1f, bx + bw, z.bottom)
    drawRoundRect(tb.c(mezcla(NEGRO, ACERO_OSC, 0.3f)), base.topLeft, base.size, CornerRadius(w * 0.004f))
    drawRect(tb.b(0.5f), base.topLeft, Size(base.width, 0.9f * u))
    drawRoundRect(tb.c(Color(0xFFC9A64A), 0.9f), Offset(base.left + bw * 0.08f, base.top - alto * 0.04f), Size(bw * 0.38f, alto * 0.05f), CornerRadius(w * 0.003f))
    // La espiral, inclinada: anillos de alambre.
    val e0 = Offset(base.right - bw * 0.2f, base.top - w * 0.004f)
    val e1 = Offset(base.left + bw * 0.4f, z.bottom - alto * 0.6f)
    for (j in 0 until 9) {
        val q = e0 + (e1 - e0) * (j / 8f)
        val rr = w * (0.012f - 0.0006f * j)
        drawOval(tb.c(ACERO), Offset(q.x - rr, q.y - rr * 0.45f), Size(rr * 2f, rr * 0.9f), style = Stroke(1.2f * u))
    }
    // El soldador: el mango y la punta de acero.
    val m0 = e1 + Offset(-w * 0.02f, -w * 0.012f); val m1 = e0 + Offset(w * 0.006f, w * 0.002f)
    drawLine(tb.c(mezcla(Color(0xFF2B3E8C), NEGRO, 0.3f)), m0, m0 + (m1 - m0) * 0.45f, w * 0.014f, StrokeCap.Round)
    drawLine(tb.b(0.5f), m0 + Offset(0f, -w * 0.004f), m0 + (m1 - m0) * 0.42f + Offset(0f, -w * 0.004f), 0.8f * u)
    drawLine(tb.c(ACERO), m0 + (m1 - m0) * 0.45f, m1, w * 0.005f, StrokeCap.Round)
    // El carrete de estaño, delante a la derecha.
    val cc = Offset(z.right - minOf(z.width * 0.12f, w * 0.03f), z.bottom - w * 0.022f)
    val tc = g.t(cc)
    contactoCiber(Offset(cc.x, z.bottom), w * 0.06f, w * 0.008f, tc, 0.55f)
    drawCircle(tc.c(mezcla(ROJO, NEGRO, 0.4f)), w * 0.022f, cc)
    drawCircle(tc.c(ACERO, 1.1f), w * 0.016f, cc)
    drawCircle(NOCHE.copy(alpha = 0.7f), w * 0.006f, cc)
    drawArc(tc.b(0.6f), 200f, 90f, false, Offset(cc.x - w * 0.016f, cc.y - w * 0.016f), Size(w * 0.032f, w * 0.032f), style = Stroke(1f * u))
}

/** La grabadora de datos de casete con su pila de casetes en sus cajas y el joystick de base cuadrada con su bola roja. */
private fun DrawScope.casetesCiber(z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    val u = density; val w = g.w
    val gw = minOf(z.width * 0.66f, w * 0.17f)
    val gx = z.left + z.width * 0.02f
    val gr = Rect(gx, z.bottom - alto * 0.26f, gx + gw, z.bottom)
    val t = g.t(gr.center)
    contactoCiber(Offset(gr.center.x, z.bottom), gw * 1.1f, w * 0.012f, t, 0.6f)
    drawRect(t.c(NEGRO), gr.topLeft, gr.size)
    clipRect(gr.left, gr.top, gr.right, gr.bottom) { planosCiber(gr, t, 0.8f) }
    drawRect(t.b(0.6f), gr.topLeft, Size(gr.width, 0.9f * u))
    // Las teclas de la grabadora y la ventanita de la casetera.
    for (j in 0 until 5) {
        val kx = gr.left + gw * (0.06f + 0.085f * j)
        drawRect(t.c(mezcla(ACERO, BEIS, 0.3f)), Offset(kx, gr.top + gr.height * 0.12f), Size(gw * 0.07f, gr.height * 0.28f))
        drawRect(t.b(0.5f), Offset(kx, gr.top + gr.height * 0.12f), Size(gw * 0.07f, 0.7f * u))
    }
    val ven = Rect(gr.left + gw * 0.5f, gr.top + gr.height * 0.18f, gr.right - gw * 0.08f, gr.bottom - gr.height * 0.18f)
    drawRect(t.c(mezcla(NEGRO, CAFE, 0.4f)), ven.topLeft, ven.size)
    drawRect(t.b(0.35f), ven.topLeft, Size(ven.width, 0.8f * u))
    // Los casetes en sus cajas, apilados y desalineados.
    val cw = gw * 0.62f
    var y = gr.top
    for (j in 0 until 3) {
        val dx = gw * 0.04f * (azar(6410 + s, j) - 0.5f) * 2f
        val c = Rect(gr.left + gw * 0.06f + dx, y - alto * 0.07f, gr.left + gw * 0.06f + dx + cw, y)
        val tc = g.t(c.center)
        drawRect(NOCHE.copy(alpha = 0.35f), c.translate(1.2f * u, 1.4f * u).topLeft, c.size)
        drawRect(tc.c(mezcla(PAPEL, BEIS, 0.4f)).copy(alpha = 0.9f), c.topLeft, c.size)
        drawRect(tc.c(listOf(Color(0xFF2F5E9E), ROJO, mezcla(NOTA, BEIS_OSC, 0.3f))[Math.floorMod(s + j, 3)], 0.9f), Offset(c.left + cw * 0.05f, c.top + c.height * 0.2f), Size(cw * 0.9f, c.height * 0.45f))
        drawRect(tc.b(0.55f), c.topLeft, Size(c.width, 0.8f * u))
        y -= alto * 0.07f + w * 0.001f
    }
    // El joystick: la base cuadrada negra, el bastón y la bola roja; el botón rojo de disparo.
    val jx = minOf(z.right - w * 0.045f, gr.right + w * 0.04f)
    val jb = Rect(jx - w * 0.04f, z.bottom - alto * 0.14f, jx + w * 0.04f, z.bottom)
    val tj = g.t(jb.center)
    contactoCiber(Offset(jx, z.bottom), w * 0.1f, w * 0.012f, tj, 0.6f)
    drawRoundRect(tj.c(NEGRO), jb.topLeft, jb.size, CornerRadius(w * 0.006f))
    drawPath(poligonoCiber(jb.left, jb.top, jb.left + w * 0.006f, jb.top - w * 0.008f, jb.right - w * 0.006f, jb.top - w * 0.008f, jb.right, jb.top), tj.c(mezcla(NEGRO, ACERO_OSC, 0.3f)))
    drawRect(tj.b(0.5f), jb.topLeft, Size(jb.width, 0.8f * u))
    val tope = z.bottom - alto * 0.66f
    drawLine(tj.c(NEGRO), Offset(jx, jb.top - w * 0.004f), Offset(jx + w * 0.012f, tope), w * 0.011f, StrokeCap.Round)
    drawLine(tj.b(0.5f), Offset(jx - w * 0.004f, jb.top - w * 0.004f), Offset(jx + w * 0.008f, tope), 0.8f * u)
    drawCircle(tj.c(ROJO, 0.9f), w * 0.018f, Offset(jx + w * 0.013f, tope - w * 0.006f))
    drawCircle(tj.b(0.8f), w * 0.006f, Offset(jx + w * 0.008f, tope - w * 0.012f))
    drawCircle(tj.c(ROJO, 0.9f), w * 0.008f, Offset(jb.left + w * 0.014f, jb.top - w * 0.006f))
    // El cable del joystick, por la repisa.
    drawPath(Path().apply { moveTo(jb.right, jb.bottom - w * 0.01f); cubicTo(jb.right + w * 0.02f, jb.bottom, jb.right + w * 0.01f, jb.bottom - w * 0.004f, minOf(z.right, jb.right + w * 0.04f), jb.bottom - w * 0.002f) },
        tj.c(NEGRO), style = Stroke(1.4f * u))
}

/** Dos cajas de disquetes apiladas (la de arriba con la tapa ahumada abierta y los disquetes de pie) y la lata de refresco. */
private fun DrawScope.cajasLataCiber(z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    val u = density; val w = g.w
    val bw = minOf(z.width * 0.6f, w * 0.15f)
    val bx = z.left + z.width * 0.04f
    val c1 = Rect(bx, z.bottom - alto * 0.24f, bx + bw, z.bottom)
    val t = g.t(c1.center)
    contactoCiber(Offset(c1.center.x, z.bottom), bw * 1.1f, w * 0.012f, t, 0.6f)
    drawRect(t.c(mezcla(BEIS, BEIS_OSC, 0.3f)), c1.topLeft, c1.size)
    clipRect(c1.left, c1.top, c1.right, c1.bottom) { planosCiber(c1, t, 0.9f) }
    drawRect(t.b(0.6f), c1.topLeft, Size(c1.width, 0.9f * u))
    drawRect(t.c(mezcla(NEGRO, CAFE, 0.35f)).copy(alpha = 0.85f), Offset(c1.left, c1.top), Size(c1.width, c1.height * 0.4f))
    drawRect(t.c(mezcla(PAPEL, NOTA, 0.25f), 0.9f), Offset(c1.left + bw * 0.25f, c1.top + c1.height * 0.55f), Size(bw * 0.4f, c1.height * 0.25f))
    // La de arriba, más chica, con la tapa abierta y los disquetes de 3½ de pie.
    val bw2 = bw * 0.78f
    val c2 = Rect(bx + bw * 0.08f, c1.top - alto * 0.2f, bx + bw * 0.08f + bw2, c1.top - w * 0.002f)
    val t2 = g.t(c2.center)
    val tapa = poligonoCiber(c2.left + w * 0.004f, c2.top + c2.height * 0.2f, c2.left + w * 0.01f, c2.top - alto * 0.18f, c2.right - w * 0.004f, c2.top - alto * 0.18f, c2.right, c2.top + c2.height * 0.2f)
    drawPath(tapa, t2.c(mezcla(NEGRO, CAFE, 0.35f)).copy(alpha = 0.72f))
    drawPath(tapa, t2.b(0.45f), style = Stroke(0.8f * u))
    val cols = listOf(Color(0xFF2F5E9E), NEGRO, Color(0xFF3F8A5A), Color(0xFFB8452F), mezcla(BEIS, BEIS_OSC, 0.2f))
    for (j in 0 until 5) {
        val x = c2.left + bw2 * (0.06f + 0.18f * j)
        val d = Rect(x, c2.top - alto * (0.08f + 0.05f * azar(6420 + s, j)), x + bw2 * 0.16f, c2.top + c2.height * 0.3f)
        drawRect(t2.c(cols[Math.floorMod(s + j, 5)]), d.topLeft, d.size)
        drawRect(t2.c(ACERO, 1.1f), Offset(d.left + d.width * 0.3f, d.top), Size(d.width * 0.4f, d.height * 0.2f))
        drawRect(t2.b(0.45f), d.topLeft, Size(d.width, 0.7f * u))
    }
    drawRect(t2.c(NEGRO), Offset(c2.left, c2.top + c2.height * 0.2f), Size(bw2, c2.height * 0.8f))
    drawRect(t2.b(0.5f), Offset(c2.left, c2.top + c2.height * 0.2f), Size(bw2, 0.8f * u))
    // La lata de refresco sin marca, a la derecha.
    val lx = minOf(z.right - w * 0.024f, c1.right + w * 0.03f)
    val la = w * 0.038f; val ll = alto * 0.3f
    val lr = Rect(lx - la / 2, z.bottom - ll, lx + la / 2, z.bottom)
    val tl = g.t(lr.center)
    contactoCiber(Offset(lx, z.bottom), la * 1.5f, w * 0.008f, tl, 0.6f)
    drawRect(tl.c(ACERO), lr.topLeft, lr.size)
    clipRect(lr.left, lr.top, lr.right, lr.bottom) {
        drawRect(tl.c(mezcla(ROJO, NEGRO, 0.2f)), Offset(lr.left, lr.top + ll * 0.3f), Size(la, ll * 0.36f))
        drawRect(tl.c(PAPEL), Offset(lr.left, lr.top + ll * 0.46f), Size(la, ll * 0.04f))
        planosCiber(lr, tl, 1.3f)
    }
    drawOval(tl.c(mezcla(ACERO, Color.White, 0.2f)), Offset(lr.left, lr.top - w * 0.003f), Size(la, w * 0.006f))
    drawOval(tl.c(ACERO_OSC), Offset(lr.left + la * 0.3f, lr.top - w * 0.002f), Size(la * 0.4f, w * 0.004f))
}

/** Tres manuales tumbados, el ratón de bola encima con su cable y otro manual de pie, apoyado detrás. */
private fun DrawScope.manualesRatonCiber(z: Rect, alto: Float, g: MarcoCiber, s: Int) {
    val u = density; val w = g.w
    // El manual de pie, apoyado en el fondo y ladeado.
    val mw = minOf(z.width * 0.2f, w * 0.045f)
    val mx = z.left + z.width * 0.62f
    val pie = Rect(mx, z.bottom - alto * 0.82f, mx + mw, z.bottom)
    val tp = g.t(pie.center)
    rotate(-7f, Offset(pie.left, pie.bottom)) {
        drawRect(NOCHE.copy(alpha = 0.4f), pie.translate(w * 0.01f, 0f).topLeft, pie.size)
        drawRoundRect(tp.c(listOf(Color(0xFF2F5E9E), mezcla(BEIS, BEIS_OSC, 0.4f), mezcla(ROJO, NEGRO, 0.5f))[Math.floorMod(s, 3)]), pie.topLeft, pie.size, CornerRadius(mw * 0.2f))
        clipRect(pie.left, pie.top, pie.right, pie.bottom) { planosCiber(pie, tp, 1f) }
        drawRect(tp.c(mezcla(PAPEL, NOTA, 0.2f)), Offset(pie.left + mw * 0.2f, pie.top + pie.height * 0.12f), Size(mw * 0.6f, pie.height * 0.3f))
        drawCircle(NOCHE.copy(alpha = 0.7f), mw * 0.16f, Offset(pie.center.x, pie.bottom - pie.height * 0.16f))
        drawRect(tp.b(0.5f), pie.topLeft, Size(mw, 0.8f * u))
    }
    // Los tres tumbados.
    val tw = minOf(z.width * 0.62f, w * 0.17f)
    val tx = z.left + z.width * 0.02f
    val gm = alto * 0.11f
    val cols = listOf(mezcla(PARED, NEGRO, 0.3f), mezcla(BEIS, BEIS_OSC, 0.5f), mezcla(Color(0xFF3F8A5A), NEGRO, 0.4f))
    contactoCiber(Offset(tx + tw / 2, z.bottom), tw * 1.1f, w * 0.012f, g.t(tx + tw / 2, z.bottom), 0.6f)
    var y = z.bottom
    for (j in 0 until 3) {
        val dx = w * 0.006f * (if (j == 1) -1f else if (j == 2) 0.7f else 0f)
        manualTumbadoCiber(Rect(tx + dx, y - gm, tx + tw + dx, y), cols[Math.floorMod(s + j, 3)], g)
        y -= gm + w * 0.011f
    }
    // El ratón de bola encima, con su cable que cae por el canto.
    val rc = Offset(tx + tw * 0.55f, y + w * 0.006f)
    val tr = g.t(rc)
    val rat = Path().apply {
        moveTo(rc.x - w * 0.028f, rc.y)
        cubicTo(rc.x - w * 0.028f, rc.y - w * 0.028f, rc.x + w * 0.028f, rc.y - w * 0.028f, rc.x + w * 0.028f, rc.y)
        close()
    }
    drawOval(NOCHE.copy(alpha = 0.4f), Offset(rc.x - w * 0.026f, rc.y - w * 0.004f), Size(w * 0.058f, w * 0.008f))
    drawPath(rat, tr.c(mezcla(BEIS, PAPEL, 0.3f)))
    drawLine(NOCHE.copy(alpha = 0.45f), Offset(rc.x - w * 0.003f, rc.y - w * 0.02f), Offset(rc.x - w * 0.003f, rc.y - w * 0.008f), 0.8f * u)
    drawArc(tr.b(0.6f), 190f, 70f, false, Offset(rc.x - w * 0.028f, rc.y - w * 0.026f), Size(w * 0.056f, w * 0.05f), style = Stroke(0.9f * u))
    drawPath(Path().apply { moveTo(rc.x - w * 0.028f, rc.y - w * 0.006f); cubicTo(rc.x - w * 0.05f, rc.y, tx - w * 0.006f, rc.y + w * 0.01f, tx - w * 0.004f, z.bottom - gm) },
        tr.c(CABLE), style = Stroke(1.3f * u))
}

/** Un gancho del tablero perforado con unos alicates colgados o un rollo de cable, y su sombra en el tablero. */
private fun DrawScope.ganchoCiber(g: MarcoCiber, c: Offset, k: Int) {
    val u = density; val w = g.w
    val t = g.t(c)
    drawLine(NOCHE.copy(alpha = 0.5f), c + Offset(w * 0.006f, w * 0.004f), c + Offset(w * 0.006f, w * 0.03f), 2f * u)
    drawLine(t.c(ACERO), c, c + Offset(0f, w * 0.026f), 2f * u, StrokeCap.Round)
    drawLine(t.c(ACERO), c + Offset(0f, w * 0.026f), c + Offset(w * 0.008f, w * 0.022f), 2f * u, StrokeCap.Round)
    if (Math.floorMod(k, 2) == 0) {
        // Los alicates: dos mangos rojos que se abren, la cabeza de acero arriba.
        val p = c + Offset(0f, w * 0.03f)
        drawLine(NOCHE.copy(alpha = 0.4f), p + Offset(w * 0.008f, w * 0.006f), p + Offset(-w * 0.002f, w * 0.1f), w * 0.01f)
        drawCircle(t.c(ACERO_OSC), w * 0.008f, p)
        drawLine(t.c(ACERO), p, p + Offset(-w * 0.004f, -w * 0.016f), w * 0.006f, StrokeCap.Round)
        drawLine(t.c(ACERO), p, p + Offset(w * 0.004f, -w * 0.016f), w * 0.006f, StrokeCap.Round)
        drawLine(t.c(ROJO, 0.85f), p + Offset(-w * 0.002f, w * 0.008f), p + Offset(-w * 0.014f, w * 0.085f), w * 0.009f, StrokeCap.Round)
        drawLine(t.c(ROJO, 0.85f), p + Offset(w * 0.002f, w * 0.008f), p + Offset(w * 0.012f, w * 0.085f), w * 0.009f, StrokeCap.Round)
        drawLine(t.b(0.5f), p + Offset(-w * 0.005f, w * 0.01f), p + Offset(-w * 0.016f, w * 0.08f), 0.8f * u)
    } else {
        // Un rollo de cable de colores.
        val cc = c + Offset(0f, w * 0.06f)
        for (j in 0 until 4) {
            val rr = Rect(cc.x - w * 0.026f + j * 0.6f * u, cc.y - w * 0.032f, cc.x + w * 0.024f + j * 0.8f * u, cc.y + w * (0.03f + 0.004f * j))
            drawOval(NOCHE.copy(alpha = 0.3f), rr.translate(w * 0.006f, w * 0.004f).topLeft, rr.size, style = Stroke(1.8f * u))
            drawOval(t.c(if (j % 2 == 0) Color(0xFF2F8A4E) else NOTA, 0.9f), rr.topLeft, rr.size, style = Stroke(1.8f * u))
        }
    }
}
