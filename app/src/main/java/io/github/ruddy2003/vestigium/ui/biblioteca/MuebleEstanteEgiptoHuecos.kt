package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AMARILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CARBON
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FAYENZA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.MARGA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PIEDRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Lo que guardan los tramos libres del almacén de la Tumba (MuebleEstanteEgipto): piezas grandes y modeladas, distintas en
// cada repisa.

/** El ancho de cada clase de pieza de un tramo libre, en dp. */
private val ANCHOS_TRAMO = floatArrayOf(58f, 76f, 64f, 78f, 70f, 62f, 72f, 60f, 40f)

/**
 * Lo que guarda un tramo libre: tantas piezas como quepan (de una a cuatro), de izquierda a derecha, cada repisa con otra
 * serie (un rollo de mechas, el cuenco de fayenza con cuentas, lino doblado, una tinajilla con pesas, la paleta del escriba,
 * óstraca apilados, un canasto con su tapa, ovillos de cuerda, una vasija de alabastro).
 */
internal fun DrawScope.huecoLibreEgipto(m: MesaEgipto, i: Int, r: Rect, oc: Float) {
    val u = density
    val x1 = m.posteD.left - m.w * 0.04f
    if (x1 - oc < 34f * u || r.height < 30f * u) return
    val y = r.bottom - m.w * 0.008f
    val inicio = (i * 4) % ANCHOS_TRAMO.size
    // Cuántas caben (con un respiro entre una y otra) y, con eso, el reparto del sitio: el hueco se llena parejo.
    val clases = ArrayList<Int>()
    var usado = 0f
    while (clases.size < 4) {
        val clase = (inicio + clases.size * 2) % ANCHOS_TRAMO.size
        val an = ANCHOS_TRAMO[clase] * u
        if (oc + 12f * u + usado + an > x1) break
        clases += clase; usado += an + 12f * u
    }
    if (clases.isEmpty()) return
    val anchos = clases.map { ANCHOS_TRAMO[it] * u }
    val hueco = ((x1 - oc - anchos.sum()) / (clases.size + 1)).coerceAtLeast(8f * u)
    var x = oc + hueco
    clases.forEachIndexed { j, clase ->
        val dx = (azar(i, j + 30) - 0.5f) * hueco * 0.4f
        objetoTramoEgipto(m, clase, i * 7 + j, r, Offset(x + anchos[j] / 2 + dx, y), anchos[j])
        x += anchos[j] + hueco
    }
}

/** Una de las piezas de un tramo libre ([clase] de 0 a 8), con su pie en [pie] y de unos [an] de ancho. */
private fun DrawScope.objetoTramoEgipto(m: MesaEgipto, clase: Int, i: Int, r: Rect, pie: Offset, an: Float) {
    val u = density
    val a = m.a
    val c = Offset(pie.x, pie.y - r.height * 0.3f)
    val k = m.k(c); val f = m.f(c)
    val der = m.der(pie.x, pie.y)
    when (clase) {
        0 -> {
            // Un rollo de mechas de lino nuevas, enroscado y atado, con una punta suelta.
            val rr = minOf(an * 0.4f, 30f * u)
            val o = Offset(pie.x, pie.y - rr)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.4f, 6f * u, a)
            val rueda = Path().apply { addOval(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr)) }
            drawPath(rueda, a.c(LINO, k, f))
            clipPath(rueda) {
                for (j in 1..4) drawCircle(a.s(0.22f), rr * (1f - j * 0.2f), o, style = Stroke(1.4f * u))
                for (j in 1..4) drawCircle(a.b(0.25f, k), rr * (1f - j * 0.2f) - 1.4f * u, o, style = Stroke(0.7f * u))
                cilindroEgipto(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr), a, k, 1.1f, der, f)
            }
            drawLine(a.c(ROJO, k, f), Offset(o.x - rr * 0.2f, o.y - rr), Offset(o.x + rr * 0.1f, o.y + rr), 2f * u)
            drawLine(a.c(LINO, k, f), Offset(o.x + rr * 0.8f, o.y + rr * 0.6f), Offset(o.x + rr * 1.6f, pie.y - 0.8f * u), 2.4f * u, cap = StrokeCap.Round)
        }
        1 -> {
            // El cuenco de fayenza azul verdosa con cuentas de colores, y su pétalo pintado en negro.
            val rr = minOf(an * 0.5f, 38f * u)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.3f, 6f * u, a)
            val boca = Rect(pie.x - rr, pie.y - rr * 0.92f, pie.x + rr, pie.y - rr * 0.6f)
            for (j in 0 until 12) {
                val q = Offset(boca.left + boca.width * (0.12f + 0.76f * azar(j, 51)), boca.top - 1f * u - 4f * u * azar(j, 52))
                drawCircle(a.c(when (j % 4) { 0 -> AZUL; 1 -> ROJO; 2 -> AMARILLO; else -> FAYENZA }, k, f), 2f * u, q)
                drawCircle(a.b(0.5f, k), 0.7f * u, q - Offset(0.6f * u, 0.6f * u))
            }
            val cuenco = Path().apply {
                moveTo(boca.left, boca.center.y); quadraticTo(pie.x - rr * 0.8f, pie.y, pie.x, pie.y)
                quadraticTo(pie.x + rr * 0.8f, pie.y, boca.right, boca.center.y); close()
            }
            drawPath(cuenco, a.c(FAYENZA, k, f))
            clipPath(cuenco) {
                cilindroEgipto(Rect(boca.left, boca.top, boca.right, pie.y), a, k, 1.3f, der, f)
                listOf(-0.35f, 0f, 0.35f).forEach { dx -> drawOval(a.c(CARBON, k, f).copy(alpha = 0.7f), Offset(pie.x + rr * dx - rr * 0.08f, pie.y - rr * 0.55f), Size(rr * 0.16f, rr * 0.42f), style = Stroke(1f * u)) }
            }
            drawOval(a.c(mezcla(FAYENZA, CARBON, 0.4f), k, f), boca.topLeft, boca.size, style = Stroke(1.4f * u))
            drawArc(a.b(0.6f, k), 200f, 50f, false, Offset(boca.left + rr * 0.2f, boca.top), Size(rr * 1.6f, rr * 1.2f), style = Stroke(1.3f * u))
            destelloEgipto(Offset(pie.x - rr * 0.45f, pie.y - rr * 0.45f), 1.4f * u, a, k)
        }
        2 -> {
            // Una pila de lino doblado: cada pieza con su orilla teñida (roja, azul, amarilla) y sus pliegues.
            val pw = minOf(an, 64f * u)
            val ph = minOf(r.height * 0.15f, 13f * u)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), pw * 1.15f, 6f * u, a)
            listOf(ROJO, AZUL, AMARILLO, AZUL).forEachIndexed { j, orilla ->
                val dx = (azar(i, j + 60) - 0.5f) * 5f * u
                val p = Rect(pie.x - pw / 2 + dx, pie.y - ph * (j + 1), pie.x + pw / 2 + dx, pie.y - ph * j)
                drawRoundRect(a.c(mezcla(LINO, PAPIRO, 0.2f * (j % 2)), k, f), p.topLeft, p.size, CornerRadius(ph * 0.45f))
                drawRect(a.c(orilla, k, f), Offset(p.left + ph * 0.3f, p.bottom - ph * 0.34f), Size(p.width - ph * 0.6f, ph * 0.16f))
                drawRect(a.b(0.4f, k), Offset(p.left + ph * 0.3f, p.top + 0.6f * u), Size(p.width * 0.6f, 0.9f * u))
                drawRect(a.s(0.22f), Offset(p.left, p.bottom - 1.2f * u), Size(p.width, 1.2f * u))
                drawRect(a.s(0.18f), Offset(p.right - p.width * 0.18f, p.top), Size(p.width * 0.18f, p.height))
            }
        }
        3 -> {
            // Una tinajilla de marga con su banda azul, y dos pesas de piedra delante.
            val al = minOf(r.height * 0.72f, 80f * u)
            val rr = al * 0.36f
            val cu = Path().apply {
                moveTo(pie.x - rr * 0.4f, pie.y)
                cubicTo(pie.x - rr * 1.2f, pie.y - al * 0.25f, pie.x - rr * 1.1f, pie.y - al * 0.75f, pie.x - rr * 0.42f, pie.y - al * 0.92f)
                lineTo(pie.x + rr * 0.42f, pie.y - al * 0.92f)
                cubicTo(pie.x + rr * 1.1f, pie.y - al * 0.75f, pie.x + rr * 1.2f, pie.y - al * 0.25f, pie.x + rr * 0.4f, pie.y)
                close()
            }
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.4f, 6f * u, a)
            drawPath(cu, a.c(MARGA, k, f))
            clipPath(cu) {
                drawRect(a.c(AZUL, k, f), Offset(pie.x - rr * 1.2f, pie.y - al * 0.7f), Size(rr * 2.4f, al * 0.1f))
                drawRect(a.c(ROJO, k, f), Offset(pie.x - rr * 1.2f, pie.y - al * 0.56f), Size(rr * 2.4f, 1.4f * u))
                cilindroEgipto(Rect(pie.x - rr * 1.1f, pie.y - al, pie.x + rr * 1.1f, pie.y), a, k, 1.3f, der, f)
            }
            drawOval(a.c(mezcla(MARGA, PALMA_OSC, 0.3f), k, f), Offset(pie.x - rr * 0.46f, pie.y - al * 0.97f), Size(rr * 0.92f, al * 0.08f))
            destelloEgipto(Offset(pie.x - rr * 0.5f, pie.y - al * 0.6f), 1.3f * u, a, k)
            listOf(1f, 0.7f).forEachIndexed { j, s ->
                val pr = rr * 0.6f * s
                val p0 = Offset(pie.x + rr * 1.3f + j * pr * 2.4f, pie.y)
                val pesa = Path().apply { addArc(Rect(p0.x - pr, p0.y - pr * 1.2f, p0.x + pr, p0.y + pr * 1.2f), 180f, 180f); close() }
                contactoEgipto(Offset(p0.x + pr * 0.3f, p0.y), pr * 2.4f, 3f * u, a)
                drawPath(pesa, a.c(PIEDRA, k, f))
                clipPath(pesa) { cilindroEgipto(Rect(p0.x - pr, p0.y - pr * 1.2f, p0.x + pr, p0.y), a, k, 1.3f, der, f) }
            }
        }
        4 -> {
            // La paleta del escriba, apoyada en el muro: la tabla larga con sus dos pastillas (roja y negra) y la ranura
            // con los cálamos, uno asomando.
            val largo = minOf(r.height * 0.82f, 86f * u); val ancho = largo * 0.17f
            val base = Offset(pie.x - an * 0.3f, pie.y)
            contactoEgipto(Offset(pie.x, pie.y), an * 0.9f, 5f * u, a)
            rotate(22f, base) {
                val p = Rect(base.x - ancho / 2, base.y - largo, base.x + ancho / 2, base.y)
                drawRoundRect(a.s(0.35f), p.topLeft + Offset(2f * u, 1f * u), p.size, CornerRadius(2f * u))
                drawRoundRect(a.c(mezcla(ACACIA, PAPIRO, 0.25f), k, f), p.topLeft, p.size, CornerRadius(2f * u))
                cilindroEgipto(p, a, k, 0.8f, der, f)
                drawRect(a.c(ACACIA_OSC, k, f), Offset(p.center.x - ancho * 0.14f, p.top + largo * 0.3f), Size(ancho * 0.28f, largo * 0.62f))
                drawLine(a.c(mezcla(PALMA, ROJO, 0.2f), k, f), Offset(p.center.x, p.top + largo * 0.16f), Offset(p.center.x, p.top + largo * 0.9f), 1.4f * u, cap = StrokeCap.Round)
                listOf(0.08f to CARBON, 0.18f to ROJO).forEach { (t, col) ->
                    val o = Offset(p.center.x, p.top + largo * t)
                    drawCircle(a.c(mezcla(ACACIA_OSC, CARBON, 0.3f), k, f), ancho * 0.36f, o)
                    drawCircle(a.c(col, k, f), ancho * 0.28f, o)
                    drawCircle(a.b(0.5f, k), ancho * 0.08f, o - Offset(ancho * 0.1f, ancho * 0.1f))
                }
                drawRect(a.b(0.4f, k), Offset(p.left + 1f * u, p.top + 2f * u), Size(1f * u, largo - 4f * u))
            }
        }
        5 -> {
            // Óstraca apilados: lascas de caliza clara, de cantos rotos, una apoyada en la pila, con manchas y rayas de tinta.
            val lascas = listOf(Triple(-0.3f, 0.0f, 0.34f), Triple(0.0f, 0.0f, 0.3f), Triple(-0.12f, 0.24f, 0.28f), Triple(0.16f, 0.2f, 0.26f))
            contactoEgipto(Offset(pie.x + 2f * u, pie.y), an * 1.05f, 5f * u, a)
            lascas.forEachIndexed { j, (dx, dy, t) ->
                val o = Offset(pie.x + an * dx, pie.y - r.height * dy)
                val lw = an * (0.55f + 0.1f * azar(i, j)); val lh = r.height * t
                val forma = poligonoEgipto(o.x - lw * 0.5f, o.y, o.x - lw * 0.42f, o.y - lh * 0.7f, o.x - lw * 0.1f, o.y - lh, o.x + lw * 0.38f, o.y - lh * 0.86f,
                    o.x + lw * 0.5f, o.y - lh * 0.3f, o.x + lw * 0.44f, o.y)
                drawPath(corridoEgipto(forma, 1.6f * u, 1.4f * u), a.s(0.3f))
                drawPath(forma, a.c(mezcla(EgiptoTinta.YESO, PIEDRA, 0.16f + 0.12f * (j % 2)), k, f))
                clipPath(forma) {
                    cilindroEgipto(Rect(o.x - lw * 0.5f, o.y - lh, o.x + lw * 0.5f, o.y), a, k, 0.7f, der, f)
                    // Rayas y manchas de tinta (sin signos): una cuenta de palotes y un borrón.
                    for (q in 0 until 4) drawLine(a.c(TINTA, k).copy(alpha = 0.7f), Offset(o.x - lw * 0.2f + q * 2.4f * u, o.y - lh * 0.62f), Offset(o.x - lw * 0.2f + q * 2.4f * u, o.y - lh * 0.4f), 0.9f * u)
                    drawCircle(a.c(if (j % 2 == 0) ROJO else TINTA, k).copy(alpha = 0.45f), lh * 0.08f, Offset(o.x + lw * 0.18f, o.y - lh * 0.5f))
                }
                drawPath(forma, a.s(0.25f), style = Stroke(0.7f * u))
            }
        }
        6 -> {
            // Un canasto de palma, redondo, con su tapa abombada, su asa de cuerda y una franja teñida.
            val al = minOf(r.height * 0.5f, 52f * u); val cw = an * 0.92f
            val cu = poligonoEgipto(pie.x - cw / 2, pie.y - al, pie.x + cw / 2, pie.y - al, pie.x + cw * 0.44f, pie.y, pie.x - cw * 0.44f, pie.y)
            val caja = Rect(pie.x - cw / 2, pie.y - al, pie.x + cw / 2, pie.y)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), cw * 1.1f, 6f * u, a)
            drawPath(cu, a.c(mezcla(FIBRA, PALMA, 0.2f), k, f))
            clipPath(cu) {
                val vueltas = Path(); var yy = caja.top
                while (yy < caja.bottom) { vueltas.moveTo(caja.left, yy); vueltas.lineTo(caja.right, yy); yy += 3.4f * u }
                drawPath(vueltas, a.s(0.25f), style = Stroke(0.9f * u))
                drawRect(a.c(ROJO, k, f), Offset(caja.left, caja.top + al * 0.38f), Size(caja.width, 3.4f * u))
                drawRect(a.c(AZUL, k, f), Offset(caja.left, caja.top + al * 0.38f + 4.4f * u), Size(caja.width, 2f * u))
                cilindroEgipto(caja, a, k, 1.2f, der, f)
            }
            val tapa = Rect(caja.left - 2f * u, caja.top - al * 0.36f, caja.right + 2f * u, caja.top + al * 0.1f)
            val forma = Path().apply { addArc(tapa, 180f, 180f); close() }
            drawPath(forma, a.c(mezcla(FIBRA, PAPIRO, 0.2f), k, f))
            clipPath(forma) {
                drawRect(a.c(ROJO, k, f), Offset(tapa.left, tapa.top + tapa.height * 0.3f), Size(tapa.width, 2.4f * u))
                cilindroEgipto(tapa, a, k, 1.2f, der, f)
            }
            drawRect(a.s(0.35f), Offset(tapa.left, tapa.center.y - 0.5f * u), Size(tapa.width, 1.4f * u))
            drawArc(a.c(mezcla(FIBRA, PALMA_OSC, 0.4f), k, f), 180f, 180f, false, Offset(pie.x - cw * 0.12f, tapa.top - al * 0.18f), Size(cw * 0.24f, al * 0.36f), style = Stroke(1.8f * u))
        }
        7 -> {
            // Ovillos de cuerda de fibra y de lino, con sus vueltas y un cabo suelto.
            listOf(Triple(-0.24f, 0.36f, FIBRA), Triple(0.2f, 0.3f, LINO), Triple(-0.02f, 0.26f, mezcla(FIBRA, ROJO, 0.3f))).forEachIndexed { j, (dx, rr0, col) ->
                val rr = an * rr0 * 0.62f
                val o = Offset(pie.x + an * dx, pie.y - rr - (if (j == 2) rr * 1.3f else 0f))
                if (j < 2) contactoEgipto(Offset(o.x + 2f * u, pie.y), rr * 2.3f, 4f * u, a)
                val bola = Path().apply { addOval(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr)) }
                drawPath(bola, a.c(col, k, f))
                clipPath(bola) {
                    val vueltas = Path()
                    for (q in 0 until 7) { val yy = o.y - rr + rr * 2 * (q + 0.5f) / 7f; vueltas.moveTo(o.x - rr, yy - rr * 0.3f); vueltas.quadraticTo(o.x, yy + rr * 0.25f, o.x + rr, yy - rr * 0.1f) }
                    drawPath(vueltas, a.s(0.28f), style = Stroke(0.8f * u))
                    cilindroEgipto(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr), a, k, 1.3f, der, f)
                }
                if (j == 1) drawLine(a.c(col, k, f), Offset(o.x + rr * 0.7f, o.y + rr * 0.5f), Offset(o.x + rr * 1.8f, pie.y - 0.6f * u), 1.4f * u, cap = StrokeCap.Round)
            }
        }
        else -> {
            // Una vasija de alabastro: esbelta, de boca abierta, con las vetas de la piedra y su brillo de cera.
            val al = minOf(r.height * 0.74f, 76f * u); val rr = an * 0.48f
            val cu = Path().apply {
                moveTo(pie.x - rr * 0.6f, pie.y)
                cubicTo(pie.x - rr * 1.05f, pie.y - al * 0.3f, pie.x - rr * 0.9f, pie.y - al * 0.7f, pie.x - rr * 0.42f, pie.y - al * 0.84f)
                lineTo(pie.x - rr * 0.75f, pie.y - al); lineTo(pie.x + rr * 0.75f, pie.y - al)
                lineTo(pie.x + rr * 0.42f, pie.y - al * 0.84f)
                cubicTo(pie.x + rr * 0.9f, pie.y - al * 0.7f, pie.x + rr * 1.05f, pie.y - al * 0.3f, pie.x + rr * 0.6f, pie.y)
                close()
            }
            val caja = Rect(pie.x - rr * 1.05f, pie.y - al, pie.x + rr * 1.05f, pie.y)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.4f, 5f * u, a)
            drawPath(cu, a.c(mezcla(EgiptoTinta.YESO, AMARILLO, 0.12f), k, f))
            clipPath(cu) {
                val vetas = Path()
                for (q in 0 until 6) { val yy = caja.top + caja.height * (0.2f + 0.13f * q); vetas.moveTo(caja.left, yy); vetas.quadraticTo(pie.x, yy + al * 0.05f * (if (q % 2 == 0) 1f else -1f), caja.right, yy + al * 0.02f) }
                drawPath(vetas, a.c(mezcla(AMARILLO, ACACIA, 0.4f), k, f).copy(alpha = 0.4f), style = Stroke(1.6f * u))
                cilindroEgipto(caja, a, k, 1.2f, der, f)
            }
            drawOval(a.c(mezcla(EgiptoTinta.YESO, PALMA_OSC, 0.3f), k, f), Offset(pie.x - rr * 0.75f, pie.y - al - al * 0.03f), Size(rr * 1.5f, al * 0.06f))
            destelloEgipto(Offset(pie.x - rr * 0.45f, pie.y - al * 0.55f), 1.6f * u, a, k)
        }
    }
}
