package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CARTON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CHAPA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.FOSFORO_AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.LINO_B
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie: el armario bajo de dos puertas bajo la última tabla, el zócalo negro con sus patas niveladoras, el linóleo a cuadros
 * del cuarto con sus juntas en perspectiva y el charco del monitor ámbar; en el suelo, el monitor ámbar encendido (cortado por el
 * borde izquierdo), la caja del papel continuo con la pila doblada y la papelera de rejilla delante, y un cable.
 */
internal fun DrawScope.pieCiber(g: MarcoCiber) {
    sueloCiber(g)
    armarioCiber(g)
    cableSueloCiber(g)
    cajaPapelCiber(g, Rect(g.w * 0.33f, g.h - g.w * 0.14f, g.w * 0.62f, g.h - g.w * 0.036f))
    papeleraCiber(g, Offset(g.w * 0.84f, g.h - g.w * 0.012f))
    monitorAmbarCiber(g, Rect(-g.w * 0.05f, g.h - g.w * 0.225f, g.w * 0.255f, g.h - g.w * 0.03f))
}

/** El linóleo: losetas a cuadros de dos tonos en perspectiva, con la luz a lo ancho, sus juntas y el charco del monitor ámbar. */
private fun DrawScope.sueloCiber(g: MarcoCiber) {
    val u = density; val w = g.w; val h = g.h
    val y0 = g.suelo
    if (h - y0 < 2f) return
    // Las filas de losetas, más anchas hacia quien mira, y las columnas que se abren.
    val filas = ArrayList<Float>()
    var y = y0; var paso = (h - y0) * 0.16f
    while (y < h && filas.size < 12) { filas.add(y); y += maxOf(2f, paso); paso *= 1.42f }
    filas.add(h)
    val cols = 9
    fun xEn(i: Int, yy: Float): Float {
        val x0 = w * i / (cols - 1f) * 1.1f - w * 0.05f
        return w / 2 + (x0 - w / 2) * (1f + 0.6f * (yy - y0) / (h - y0))
    }
    val claras = Path(); val oscuras = Path()
    for (f in 0 until filas.size - 1) for (c in -1 until cols) {
        val ya = filas[f]; val yb = filas[f + 1]
        val q = poligonoCiber(xEn(c, ya), ya, xEn(c + 1, ya), ya, xEn(c + 1, yb), yb, xEn(c, yb), yb)
        if ((f + c) % 2 == 0) claras.addPath(q) else oscuras.addPath(q)
    }
    val ym = (y0 + h) / 2
    val pa = Array(6) { i -> val f = i / 5f; f to g.t(w * f, ym).c(LINO_B, 0.95f) }
    val pb = Array(6) { i -> val f = i / 5f; f to g.t(w * f, ym).c(LINO, 0.95f) }
    drawRect(Brush.horizontalGradient(*pb, startX = 0f, endX = w), Offset(0f, y0), Size(w, h - y0))
    drawPath(claras, Brush.horizontalGradient(*pa, startX = 0f, endX = w))
    clipRect(0f, y0, w, h) {
        pinceladas(Rect(0f, y0, w, h), NOCHE, 6500, 16, u, 0.2f)
        val juntas = Path()
        filas.forEach { yy -> juntas.moveTo(0f, yy); juntas.lineTo(w, yy) }
        for (c in -1..cols) { juntas.moveTo(xEn(c, y0), y0); juntas.lineTo(xEn(c, h), h) }
        drawPath(juntas, NOCHE.copy(alpha = 0.35f), style = Stroke(1f * u))
        // La mancha gastada delante del armario y el charco del monitor ámbar.
        drawOval(g.t(w * 0.5f, ym).c(mezcla(LINO_B, BEIS, 0.4f)).copy(alpha = 0.18f), Offset(w * 0.3f, y0 + (h - y0) * 0.3f), Size(w * 0.4f, (h - y0) * 0.35f))
        val amb = Offset(w * MuebleEstanteCiber.AMB_X + w * 0.12f, h - w * 0.01f)
        mancha(amb, w * 0.42f, g.a.colores[2].copy(alpha = 0.34f * g.a.fuerzas[2]))
        // La sombra del armario en el suelo.
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.65f), NOCHE.copy(alpha = 0f)), y0, y0 + w * 0.05f), Offset(g.ml, y0), Size(g.mr - g.ml + w * 0.02f, w * 0.05f))
    }
}

/**
 * El armario bajo, de la última tabla al zócalo: dos puertas de chapa con su marco prensado, la rejilla de ventilación arriba,
 * los tiradores hundidos y la cerradura de llave en medio, la junta entre puertas, una abolladura y la pintura saltada; debajo,
 * el zócalo negro metido y las patas niveladoras.
 */
private fun DrawScope.armarioCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val y0 = g.fondo; val y1 = g.armario
    if (y1 <= y0 + 2f) return
    val x0 = g.x0; val x1 = g.x1
    // El zócalo y las patas.
    val z0 = y1; val z1 = g.zocalo
    // El hueco bajo el armario, detrás del zócalo metido: oscuro, de costado a costado (no deja ver el fondo).
    drawRect(g.t(w * 0.5f, z0).c(mezcla(NEGRO, NOCHE, 0.5f), 0.6f), Offset(g.ml, z0 - 1f), Size(g.mr - g.ml, maxOf(g.suelo, z1) - z0 + 2f))
    bandaCiber(g.ml + w * 0.014f, g.mr - w * 0.014f, z0 - 1f, z1, NEGRO, g, 1.1f)
    drawRect(g.t(w * 0.3f, z0).b(0.3f), Offset(g.ml + w * 0.014f, z0), Size(g.mr - g.ml - w * 0.028f, 0.8f * u))
    listOf(g.ml + w * 0.01f, g.mr - w * 0.05f).forEach { x ->
        val t = g.t(x, z1)
        drawRect(t.c(ACERO_OSC), Offset(x + w * 0.012f, z1 - 1f), Size(w * 0.016f, g.pisan - z1))
        drawRoundRect(t.c(NEGRO), Offset(x, g.pisan - w * 0.007f), Size(w * 0.04f, w * 0.008f), CornerRadius(w * 0.003f))
        drawRect(t.b(0.3f), Offset(x, g.pisan - w * 0.007f), Size(w * 0.04f, 0.7f * u))
    }
    // El fondo del armario (la junta de arriba y el canto de las puertas).
    filaJuntaCiber(y0, x0, x1, g.c(CHAPA, Offset(w * 0.5f, y0), 0.9f))
    columnaCiber(x0, x1, y0, y1 + 1f, mezcla(CHAPA, BEIS_OSC, 0.25f), g, n = 4)
    val mitad = (x0 + x1) / 2
    listOf(x0 + w * 0.006f to mitad - w * 0.003f, mitad + w * 0.003f to x1 - w * 0.006f).forEachIndexed { i, (a, b) ->
        val p = Rect(a, y0 + w * 0.008f, b, y1 - w * 0.006f)
        bandaCiber(p.left, p.right, p.top, p.bottom, CHAPA, g)
        pinceladas(p, NOCHE, 6510 + i, 8, u, 0.12f)
        // El marco prensado: el filo de arriba y de la izquierda, la sombra de abajo y de la derecha.
        val m = w * 0.012f
        drawRect(g.t(p.left, p.top).b(0.55f), p.topLeft, Size(p.width, 1.1f * u))
        drawRect(g.t(p.left, p.center.y).b(0.35f), p.topLeft, Size(1.1f * u, p.height))
        drawRect(NOCHE.copy(alpha = 0.4f), Offset(p.left, p.bottom - 1.6f * u), Size(p.width, 1.6f * u))
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(p.right - 1.6f * u, p.top), Size(1.6f * u, p.height))
        val panel = Rect(p.left + m, p.top + m, p.right - m, p.bottom - m)
        drawRect(NOCHE.copy(alpha = 0.3f), panel.topLeft, Size(panel.width, 1.2f * u))
        drawRect(NOCHE.copy(alpha = 0.25f), panel.topLeft, Size(1.2f * u, panel.height))
        drawRect(g.t(panel.left, panel.bottom).b(0.35f), Offset(panel.left, panel.bottom - 1f * u), Size(panel.width, 1f * u))
        // La rejilla de ventilación de arriba.
        for (j in 0 until 6) {
            val x = panel.left + panel.width * (0.1f + 0.14f * j)
            val t = g.t(x, panel.top)
            drawRoundRect(NOCHE.copy(alpha = 0.75f), Offset(x, panel.top + panel.height * 0.14f), Size(panel.width * 0.08f, panel.height * 0.08f), CornerRadius(1.5f * u))
            drawRect(t.b(0.35f), Offset(x, panel.top + panel.height * 0.22f), Size(panel.width * 0.08f, 0.7f * u))
        }
        // El tirador hundido, junto a la junta de en medio.
        val tx = if (i == 0) p.right - w * 0.06f else p.left + w * 0.02f
        val ti = Rect(tx, p.center.y - w * 0.008f, tx + w * 0.04f, p.center.y + w * 0.012f)
        drawRoundRect(NOCHE.copy(alpha = 0.7f), ti.topLeft, ti.size, CornerRadius(w * 0.006f))
        drawRect(g.t(ti.center).b(0.5f), Offset(ti.left + 2f * u, ti.bottom - 1f * u), Size(ti.width - 4f * u, 1f * u))
    }
    // La junta entre las puertas y la cerradura de llave.
    drawRect(NOCHE.copy(alpha = 0.8f), Offset(mitad - w * 0.003f, y0 + w * 0.008f), Size(w * 0.006f, y1 - y0 - w * 0.014f))
    val ce = Offset(mitad - w * 0.022f, y0 + (y1 - y0) * 0.36f)
    val tc = g.t(ce)
    drawCircle(NOCHE.copy(alpha = 0.5f), w * 0.011f, ce + Offset(1f * u, 1.2f * u))
    drawCircle(tc.c(ACERO), w * 0.01f, ce)
    drawCircle(tc.c(mezcla(ACERO, Color.White, 0.2f)), w * 0.007f, ce)
    drawRoundRect(NOCHE, Offset(ce.x - w * 0.0012f, ce.y - w * 0.0045f), Size(w * 0.0024f, w * 0.009f), CornerRadius(w * 0.001f))
    drawArc(tc.b(0.8f), 180f, 90f, false, Offset(ce.x - w * 0.01f, ce.y - w * 0.01f), Size(w * 0.02f, w * 0.02f), style = Stroke(1f * u))
    // La abolladura de la puerta derecha (un patito de chapa) y la pintura saltada abajo.
    val ab = Offset(mitad + (x1 - mitad) * 0.62f, y0 + (y1 - y0) * 0.66f)
    drawOval(NOCHE.copy(alpha = 0.22f), Offset(ab.x - w * 0.03f, ab.y - w * 0.01f), Size(w * 0.06f, w * 0.02f))
    drawArc(g.t(ab).b(0.5f), 200f, 120f, false, Offset(ab.x - w * 0.03f, ab.y - w * 0.008f), Size(w * 0.06f, w * 0.02f), style = Stroke(1f * u))
    for (j in 0 until 4) {
        val p = Offset(x0 + (x1 - x0) * azar(6520, j), y1 - w * (0.01f + 0.02f * azar(6521, j)))
        val t = g.t(p)
        val rr = (1.6f + 2f * azar(6522, j)) * u
        drawOval(t.c(ACERO), Offset(p.x - rr * 1.4f, p.y - rr * 0.7f), Size(rr * 2.8f, rr * 1.4f))
    }
    drawRect(NOCHE.copy(alpha = 0.55f), Offset(x0, y1 - 1.2f * u), Size(x1 - x0, 1.2f * u + 1f))
}

/** El cable negro del monitor, que corre por el suelo hacia la derecha y se mete bajo el armario. */
private fun DrawScope.cableSueloCiber(g: MarcoCiber) {
    val u = density; val w = g.w; val h = g.h
    val t = g.t(w * 0.4f, h - w * 0.06f)
    val p = Path().apply { moveTo(w * 0.2f, h - w * 0.06f); cubicTo(w * 0.36f, h - w * 0.02f, w * 0.42f, h - w * 0.12f, w * 0.56f, g.pisan + w * 0.01f) }
    drawPath(p, NOCHE.copy(alpha = 0.45f), style = Stroke(3.4f * u))
    drawPath(p, t.c(NEGRO), style = Stroke(2.4f * u))
    drawPath(p, t.b(0.35f), style = Stroke(0.7f * u))
}

/**
 * La caja de cartón del papel continuo: abierta, con las solapas de los lados abiertas hacia fuera y la de atrás de pie; dentro,
 * la hoja de arriba de la pila, con sus bandas verdes y los agujeros de arrastre; el frente de cartón con su cinta de embalar.
 */
private fun DrawScope.cajaPapelCiber(g: MarcoCiber, c: Rect) {
    val u = density; val w = g.w
    val t = g.t(c.center)
    val hondo = w * 0.05f
    contactoCiber(Offset(c.center.x + w * 0.02f, c.bottom + w * 0.004f), c.width * 1.15f, w * 0.024f, t, 0.75f)
    // La solapa de atrás, de pie, y las de los lados, abiertas.
    val ta = g.t(c.center.x, c.top - hondo)
    drawPath(poligonoCiber(c.left + w * 0.02f, c.top - hondo, c.left + w * 0.03f, c.top - hondo - w * 0.07f, c.right + w * 0.01f, c.top - hondo - w * 0.065f, c.right + w * 0.012f, c.top - hondo), ta.c(CARTON, 0.9f))
    drawLine(ta.b(0.5f), Offset(c.left + w * 0.03f, c.top - hondo - w * 0.07f), Offset(c.right + w * 0.01f, c.top - hondo - w * 0.065f), 1f * u)
    drawPath(poligonoCiber(c.left, c.top, c.left + w * 0.02f, c.top - hondo, c.left - w * 0.03f, c.top - hondo - w * 0.03f, c.left - w * 0.06f, c.top - w * 0.02f), ta.c(CARTON, 1.1f))
    drawPath(poligonoCiber(c.right, c.top, c.right + w * 0.012f, c.top - hondo, c.right + w * 0.06f, c.top - hondo - w * 0.012f, c.right + w * 0.05f, c.top - w * 0.008f), ta.c(mezcla(CARTON, NOCHE, 0.3f), 0.9f))
    // La boca vista desde arriba, con la pila de papel dentro.
    val boca = poligonoCiber(c.left, c.top, c.left + w * 0.02f, c.top - hondo, c.right + w * 0.012f, c.top - hondo, c.right, c.top)
    drawPath(boca, t.c(mezcla(CARTON, NOCHE, 0.45f)))
    clipPath(boca) {
        val hoja = poligonoCiber(c.left + w * 0.012f, c.top - w * 0.004f, c.left + w * 0.028f, c.top - hondo + w * 0.008f, c.right + w * 0.002f, c.top - hondo + w * 0.008f, c.right - w * 0.01f, c.top - w * 0.004f)
        drawPath(hoja, t.c(PAPEL, 1.15f))
        clipPath(hoja) {
            var y = c.top - hondo
            var i = 0
            while (y < c.top) { if (i % 2 == 0) drawRect(t.c(RAYA, 1.1f), Offset(c.left, y), Size(c.width + w * 0.02f, w * 0.0075f)); y += w * 0.0075f; i++ }
            var x = c.left + w * 0.03f
            while (x < c.right) { drawCircle(t.c(mezcla(CARTON, NOCHE, 0.4f)), 1.2f * u, Offset(x, c.top - w * 0.01f)); x += w * 0.012f }
            drawRect(t.b(0.5f), Offset(c.left, c.top - hondo + w * 0.008f), Size(c.width, 1f * u))
        }
        drawRect(NOCHE.copy(alpha = 0.4f), Offset(c.left, c.top - hondo), Size(c.width + w * 0.02f, w * 0.008f))
    }
    // El frente de cartón: la luz que le llega, la cinta de embalar, un pliegue y el canto de arriba.
    bandaCiber(c.left, c.right, c.top, c.bottom, CARTON, g, 1.05f)
    clipRect(c.left, c.top, c.right, c.bottom) {
        pinceladas(c, NOCHE, 6530, 6, u, 0.14f)
        drawRect(NOCHE.copy(alpha = 0.25f), Offset(c.right - c.width * 0.1f, c.top), Size(c.width * 0.1f, c.height))
        drawRect(t.b(0.3f), c.topLeft, Size(c.width * 0.04f, c.height))
        drawRect(t.c(mezcla(CARTON, AMBAR, 0.3f), 1.1f).copy(alpha = 0.75f), Offset(c.left + c.width * 0.42f, c.top), Size(c.width * 0.16f, c.height * 0.5f))
        drawLine(NOCHE.copy(alpha = 0.3f), Offset(c.left + c.width * 0.15f, c.top + c.height * 0.55f), Offset(c.left + c.width * 0.33f, c.top + c.height * 0.85f), 1f * u)
        drawRect(t.c(mezcla(PAPEL, CARTON, 0.4f), 0.9f), Offset(c.left + c.width * 0.66f, c.top + c.height * 0.4f), Size(c.width * 0.18f, c.height * 0.24f))
    }
    drawRect(t.b(0.6f), c.topLeft, Size(c.width, 1f * u))
}

/** La papelera de rejilla negra con las bolas de papel arrugado que rebosan, delante de la caja. */
private fun DrawScope.papeleraCiber(g: MarcoCiber, pie: Offset) {
    val u = density; val w = g.w
    val a = w * 0.13f; val al = w * 0.12f
    val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
    val t = g.t(r.center)
    contactoCiber(Offset(pie.x, pie.y), a * 1.3f, w * 0.018f, t, 0.75f)
    // Las bolas de papel, encima de la boca.
    listOf(Offset(-0.22f, -0.02f) to 0.2f, Offset(0.12f, -0.05f) to 0.24f, Offset(0.02f, 0.03f) to 0.18f, Offset(0.3f, 0.02f) to 0.15f).forEachIndexed { j, (o, rr) ->
        val c = Offset(r.center.x + a * o.x, r.top + a * o.y)
        val tb = g.t(c)
        drawCircle(tb.c(mezcla(PAPEL, BEIS, 0.2f), 0.9f), a * rr, c)
        drawArc(tb.s(0.35f), 0f, 160f, false, Offset(c.x - a * rr, c.y - a * rr), Size(a * rr * 2, a * rr * 2), style = Stroke(1.4f * u))
        drawLine(tb.s(0.25f), c + Offset(-a * rr * 0.5f, -a * rr * 0.1f), c + Offset(a * rr * 0.3f, a * rr * 0.2f), 0.8f * u)
        drawArc(tb.b(0.5f), 200f, 70f, false, Offset(c.x - a * rr, c.y - a * rr), Size(a * rr * 2, a * rr * 2), style = Stroke(0.9f * u))
        if (j == 1) drawRect(tb.c(RAYA, 0.9f), Offset(c.x - a * rr * 0.5f, c.y - a * rr * 0.2f), Size(a * rr * 0.8f, 1.4f * u))
    }
    // El cuerpo: el cono de rejilla, más ancho arriba; el fondo oscuro detrás de la rejilla.
    val cuerpo = poligonoCiber(r.left, r.top, r.right, r.top, r.right - a * 0.1f, r.bottom, r.left + a * 0.1f, r.bottom)
    drawPath(cuerpo, t.c(mezcla(NEGRO, NOCHE, 0.4f)).copy(alpha = 0.85f))
    clipPath(cuerpo) {
        val rej = Path()
        var x = r.left
        while (x < r.right + a * 0.2f) { rej.moveTo(x, r.top); rej.lineTo(x - a * 0.18f, r.bottom); rej.moveTo(x - a * 0.18f, r.top); rej.lineTo(x, r.bottom); x += a * 0.09f }
        drawPath(rej, t.c(mezcla(ACERO_OSC, NEGRO, 0.3f), 1.1f), style = Stroke(1f * u))
        planosCiber(r, t, 0.9f)
    }
    drawLine(t.c(ACERO_OSC, 1.1f), Offset(r.left, r.top), Offset(r.right, r.top), 2.2f * u)
    drawLine(t.b(0.6f), Offset(r.left, r.top - 1f * u), Offset(r.right, r.top - 1f * u), 0.8f * u)
    drawLine(t.c(ACERO_OSC), Offset(r.left + a * 0.1f, r.bottom - 1f * u), Offset(r.right - a * 0.1f, r.bottom - 1f * u), 1.6f * u)
}

/**
 * El monitor de fósforo ámbar en el suelo, en tres cuartos y cortado por el borde izquierdo: la caja gris beis con el costado y la
 * joroba del tubo, el bisel, el vidrio abombado encendido (renglones de bloques ámbar; en los Sueños, casi apagado con unos
 * puntos), los mandos, el piloto y el resplandor sobre el suelo y el armario.
 */
private fun DrawScope.monitorAmbarCiber(g: MarcoCiber, f: Rect) {
    val u = density; val w = g.w
    val t = g.t(f.center.x + f.width * 0.3f, f.center.y)
    val noche = g.a.noche
    contactoCiber(Offset(f.center.x + w * 0.04f, f.bottom + w * 0.004f), f.width * 1.25f, w * 0.03f, t, 0.8f)
    // El costado derecho y la joroba del tubo, que se van hacia atrás.
    val dv = Offset(f.width * 0.16f, -f.height * 0.08f)
    val lado = poligonoCiber(f.right, f.top, f.right + dv.x, f.top + dv.y, f.right + dv.x, f.bottom + dv.y * 0.3f, f.right, f.bottom)
    val joroba = poligonoCiber(f.right + dv.x, f.top + f.height * 0.12f + dv.y, f.right + dv.x + f.width * 0.14f, f.top + f.height * 0.25f + dv.y,
        f.right + dv.x + f.width * 0.14f, f.bottom - f.height * 0.25f, f.right + dv.x, f.bottom - f.height * 0.1f)
    drawPath(joroba, t.c(mezcla(BEIS_OSC, ACERO_OSC, 0.4f), 0.6f))
    drawPath(lado, t.c(mezcla(BEIS_OSC, ACERO_OSC, 0.3f), 0.75f))
    clipPath(lado) { for (k in 0 until 6) drawLine(NOCHE.copy(alpha = 0.55f), Offset(f.right + dv.x * 0.2f, f.top + f.height * (0.25f + 0.08f * k)), Offset(f.right + dv.x * 0.8f, f.top + f.height * (0.25f + 0.08f * k) + dv.y * 0.6f), 1.5f * u) }
    drawPath(poligonoCiber(f.left, f.top, f.left + dv.x, f.top + dv.y, f.right + dv.x, f.top + dv.y, f.right, f.top), t.c(mezcla(BEIS, ACERO, 0.3f), 0.85f))
    // La cara: gris beis, encendida por su propia pantalla.
    val cara = Path().apply { addRoundRect(RoundRect(f, CornerRadius(w * 0.01f))) }
    drawPath(cara, t.c(mezcla(BEIS, ACERO, 0.35f)))
    clipPath(cara) {
        pinceladas(f, BEIS_OSC, 6540, 8, u, 0.18f)
        drawRect(NOCHE.copy(alpha = 0.25f), Offset(f.right - w * 0.02f, f.top), Size(w * 0.02f, f.height))
    }
    drawRect(t.b(0.6f), Offset(f.left, f.top), Size(f.width, 1.2f * u))
    val bis = Rect(f.left + f.width * 0.08f, f.top + f.height * 0.08f, f.right - f.width * 0.08f, f.bottom - f.height * 0.2f)
    drawRoundRect(mezcla(t.c(BEIS_OSC), NOCHE, 0.4f), bis.topLeft, bis.size, CornerRadius(w * 0.01f))
    val pan = Rect(bis.left + bis.width * 0.05f, bis.top + bis.height * 0.07f, bis.right - bis.width * 0.05f, bis.bottom - bis.height * 0.07f)
    val panP = Path().apply { addRoundRect(RoundRect(pan, CornerRadius(pan.height * 0.12f))) }
    val fondos = if (noche) listOf(Color(0xFF2A1A0C), Color(0xFF140C06), Color(0xFF0A0603)) else listOf(Color(0xFF5C3208), Color(0xFF2C1604), Color(0xFF120802))
    drawPath(panP, Brush.radialGradient(fondos, pan.center, pan.width * 0.7f))
    clipPath(panP) {
        val lh = pan.height / 8f
        val x0 = pan.left + pan.width * 0.07f
        val tinta = FOSFORO_AMBAR.copy(alpha = if (noche) 0.2f else 0.85f)
        for (rr in 0 until 7) {
            val y = pan.top + lh * (0.8f + rr)
            var x = x0 + (if (rr % 3 == 1) pan.width * 0.06f else 0f)
            val fin = pan.left + pan.width * (0.35f + 0.55f * azar(6545, rr))
            var j = 0
            while (x < fin) {
                val l = pan.width * (0.04f + 0.09f * azar(6546 + rr, j))
                drawRect(tinta, Offset(x, y - lh * 0.2f), Size(minOf(l, fin - x), lh * 0.42f))
                x += l + pan.width * 0.03f; j++
            }
        }
        if (!noche) drawRect(FOSFORO_AMBAR, Offset(x0, pan.top + lh * 7.5f - lh * 0.38f), Size(lh * 0.5f, lh * 0.76f))
        var y = pan.top
        while (y < pan.bottom) { drawLine(NOCHE.copy(alpha = 0.22f), Offset(pan.left, y), Offset(pan.right, y), 0.6f * u); y += 2.2f * u }
        drawPath(Path().apply {
            moveTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.55f)
            cubicTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.1f, pan.left + pan.width * 0.3f, pan.top + pan.height * 0.06f, pan.left + pan.width * 0.6f, pan.top + pan.height * 0.06f)
        }, Color(0xFFFFE2B0).copy(alpha = 0.18f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawRoundRect(NOCHE.copy(alpha = 0.45f), pan.topLeft, pan.size, CornerRadius(pan.height * 0.12f), style = Stroke(4f * u))
    }
    if (!noche) mancha(pan.center, pan.width * 0.9f, FOSFORO_AMBAR.copy(alpha = 0.16f))
    // Abajo: la rejilla, los dos mandos y el piloto.
    val yb = (bis.bottom + f.bottom) / 2
    for (k in 0 until 6) drawRect(NOCHE.copy(alpha = 0.45f), Offset(f.left + f.width * (0.2f + 0.035f * k), yb - f.height * 0.04f), Size(f.width * 0.014f, f.height * 0.08f))
    listOf(0.62f, 0.72f).forEach { fx ->
        val mc = Offset(f.left + f.width * fx, yb)
        drawCircle(NOCHE.copy(alpha = 0.5f), f.height * 0.04f, mc + Offset(1f * u, 1.2f * u))
        drawCircle(t.c(mezcla(BEIS_OSC, NEGRO, 0.4f)), f.height * 0.04f, mc)
        drawCircle(t.b(0.6f), f.height * 0.014f, mc - Offset(f.height * 0.014f, f.height * 0.014f))
    }
    pilotoCiber(Offset(f.left + f.width * 0.86f, yb), 1.6f * u, if (noche) AMBAR else VERDE, true, t)
}
