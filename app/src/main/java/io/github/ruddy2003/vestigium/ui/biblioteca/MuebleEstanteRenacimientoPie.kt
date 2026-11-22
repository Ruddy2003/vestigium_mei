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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * Lo del pie y de los huecos del estante de Renacimiento. Al pie, sobre las losas y la alfombra: la bala de papel envuelta en
 * arpillera, atada con soga y con la marca del mercader, cortada por la izquierda; la pila de cuadernillos sin encuadernar,
 * atada, cortada por la derecha, con uno suelto apoyado. En los huecos de las repisas: libros tumbados con un canto de pietra
 * serena de pisapapeles, la salvadera de latón con el cortaplumas, o un mazo de cartas plegadas con su lacre.
 */
internal object PieEstanteRenacimiento {
    private val P = PaletaRenacimiento
    private val M = MuebleEstanteRenacimiento

    fun DrawScope.alPie(a: AireRenacimiento, b: Rect, cerca: (Offset) -> Float) {
        val w = size.width
        val pisa = b.top + b.height * M.PIE
        bala(Rect(w * 0.14f, pisa - w * 0.16f, w * 0.43f, pisa), a, a.en(cerca(Offset(w * 0.28f, pisa - w * 0.07f))))
        pila(Rect(w * 0.9f, pisa - w * 0.11f, w * 1.08f, pisa), a, a.en(cerca(Offset(w * 0.95f, pisa - w * 0.05f))))
    }

    /**
     * La bala de papel: un fardo cuadrado envuelto en arpillera, con su cara de arriba, la trama de la tela, las arrugas donde
     * tira la soga, dos vueltas de soga con su nudo y el cabo suelto, la marca del mercader estarcida, el marbete de papel
     * colgado de la soga y su sombra.
     */
    private fun DrawScope.bala(r: Rect, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val w = size.width
        val cara = r.height * 0.2f
        val frente = Rect(r.left, r.top + cara, r.right - r.width * 0.06f, r.bottom)
        contactoRenacimiento(Offset(r.center.x + w * 0.03f, r.bottom), r.width * 1.15f, 6f * u, a, 0.45f)
        // La cara de arriba, vista desde arriba, más clara; y el costado de la derecha, en sombra.
        val arriba = poligonoRenacimiento(frente.left + 2f * u, frente.top, frente.right, frente.top, r.right, r.top + 2f * u, frente.left + r.width * 0.08f, r.top)
        val costado = poligonoRenacimiento(frente.right, frente.top, r.right, r.top + 2f * u, r.right, r.bottom - cara * 0.9f, frente.right, frente.bottom)
        drawPath(arriba, t.c(mezcla(P.ARPILLERA, P.PAPEL, 0.25f)))
        drawPath(costado, t.c(mezcla(P.ARPILLERA, P.NOGAL_OSC, 0.35f)))
        val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(frente, CornerRadius(w * 0.008f))) }
        drawPath(cuerpo, t.c(P.ARPILLERA))
        clipPath(cuerpo) {
            // La trama de la arpillera, en un solo trazo, y las arrugas donde tira la soga.
            val trama = Path()
            var y = frente.top
            var n = 0
            while (y < frente.bottom && n < 200) { trama.addRect(Rect(frente.left, y, frente.right, y + 0.5f * u)); y += maxOf(1.5f * u, w * 0.007f); n++ }
            drawPath(trama, t.s(0.12f))
            drawRect(t.b(0.25f), frente.topLeft, Size(frente.width * 0.2f, frente.height))
            drawRect(t.s(0.25f), Offset(frente.right - frente.width * 0.22f, frente.top), Size(frente.width * 0.22f, frente.height))
            val arrugas = Path()
            for (j in 0 until 5) {
                val x = frente.left + frente.width * (0.12f + 0.17f * j)
                arrugas.moveTo(x, frente.top + frente.height * 0.3f); arrugas.quadraticTo(x + w * 0.012f, frente.center.y, x + w * 0.004f, frente.bottom - frente.height * 0.25f)
            }
            drawPath(arrugas, t.s(0.22f), style = Stroke(0.9f * u))
        }
        drawRect(t.b(0.3f), Offset(frente.left, frente.top), Size(frente.width, 1f * u))
        // La marca del mercader: un redondel con su asta y su banderola (un signo, no una letra).
        val m = Offset(frente.left + frente.width * 0.3f, frente.top + frente.height * 0.55f)
        val tinta = t.c(P.IMPRENTA).copy(alpha = 0.6f)
        val mk = w * 0.02f
        drawCircle(tinta, mk, m, style = Stroke(1.3f * u))
        drawLine(tinta, Offset(m.x - mk, m.y), Offset(m.x + mk, m.y), 1.3f * u)
        drawLine(tinta, Offset(m.x, m.y - mk * 1.9f), Offset(m.x, m.y + mk), 1.3f * u)
        drawLine(tinta, Offset(m.x, m.y - mk * 1.9f), Offset(m.x + mk * 0.75f, m.y - mk * 1.5f), 1.3f * u)
        // La soga: una vuelta a lo ancho y otra a lo alto (que sigue por la cara de arriba), el nudo y el cabo.
        val soga = t.c(P.CANAMO)
        val xs = frente.left + frente.width * 0.64f
        val ys = frente.top + frente.height * 0.4f
        val sogas = Path().apply {
            addRect(Rect(frente.left, ys - 1.3f * u, frente.right, ys + 1.3f * u))
            addRect(Rect(xs - 1.3f * u, frente.top, xs + 1.3f * u, frente.bottom))
            moveTo(xs - 1.3f * u, frente.top); lineTo(xs + r.width * 0.04f - 1.3f * u, r.top); lineTo(xs + r.width * 0.04f + 1.3f * u, r.top); lineTo(xs + 1.3f * u, frente.top); close()
        }
        drawPath(Path().apply { addPath(sogas, Offset(0.9f * u, 1.3f * u)) }, t.s(0.4f))
        drawPath(sogas, soga)
        drawCircle(mezcla(soga, a.hondo, 0.25f), 2.8f * u, Offset(xs, ys))
        drawLine(soga, Offset(xs, ys), Offset(xs + w * 0.02f, ys + frente.height * 0.3f), 1.7f * u, StrokeCap.Round)
        // El marbete de papel colgado del nudo.
        rotate(14f, Offset(xs, ys)) {
            val mb = Rect(xs - w * 0.012f, ys + w * 0.012f, xs + w * 0.028f, ys + w * 0.04f)
            drawLine(soga, Offset(xs, ys), Offset(mb.center.x, mb.top), 0.7f * u)
            drawRect(a.sombra(0.3f), Offset(mb.left + 1f * u, mb.top + 1.2f * u), mb.size)
            drawRect(t.c(P.PAPEL), mb.topLeft, mb.size)
            drawLine(t.c(P.SEPIA).copy(alpha = 0.6f), Offset(mb.left + mb.width * 0.2f, mb.center.y), Offset(mb.right - mb.width * 0.2f, mb.center.y), 0.7f * u)
        }
        drawRect(t.f(0.5f), Offset(frente.right - 1.2f * u, frente.top), Size(1.2f * u, frente.height))
    }

    /** La pila de cuadernillos sin encuadernar, atada con bramante, y uno suelto apoyado; cortada por el borde derecho. */
    private fun DrawScope.pila(r: Rect, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val w = size.width
        contactoRenacimiento(Offset(r.center.x, r.bottom), r.width * 1.2f, 5f * u, a, 0.42f)
        val capas = 6
        for (j in 0 until capas) {
            val y0 = r.top + r.height * j / capas
            val dx = (azar(67, j) - 0.5f) * w * 0.018f
            val col = t.c(mezcla(P.PAPEL, P.PAPEL_OSC, 0.25f + 0.35f * azar(71, j)))
            drawRect(col, Offset(r.left + dx, y0), Size(r.width, r.height / capas + 0.5f))
            drawRect(t.b(0.3f), Offset(r.left + dx, y0), Size(r.width, 0.8f * u))
            drawRect(t.s(0.35f), Offset(r.left + dx, y0 + r.height / capas - 0.8f * u), Size(r.width, 0.8f * u))
        }
        drawRect(t.s(0.22f), Offset(r.left, r.top), Size(w * 0.014f, r.height))
        drawLine(t.c(P.CANAMO), Offset(r.left + w * 0.07f, r.top - 1f * u), Offset(r.left + w * 0.07f, r.bottom), 1.4f * u)
        drawRect(t.f(0.5f), Offset(r.left, r.top), Size(r.width, 1.2f * u))
        // El cuadernillo suelto, apoyado y ladeado contra la pila.
        rotate(-16f, Offset(r.left - w * 0.03f, r.bottom)) {
            val q = Rect(r.left - w * 0.03f, r.bottom - w * 0.1f, r.left - w * 0.012f, r.bottom)
            drawRect(a.sombra(0.3f), Offset(q.left + 1.6f * u, q.top), q.size)
            drawRect(t.c(mezcla(P.PAPEL, P.RUBRICA, 0.1f)), q.topLeft, q.size)
            drawRect(t.b(0.3f), q.topLeft, Size(1.2f * u, q.height))
        }
    }

    // ---------------------------------------------------------------- los huecos de las repisas

    /** Lo que llena el hueco libre de una repisa (de [oc] al costado derecho): cada repisa, otra cosa; en uno largo, dos. */
    fun DrawScope.huecoLibre(k: Int, r: Rect, oc: Float, a: AireRenacimiento, cerca: (Offset) -> Float) {
        val u = density
        val libre = r.right - oc - size.width * 0.035f
        if (libre < 26f * u || r.height < 20f * u) return
        // En un hueco corto no cabe la caja de escribir: va la salvadera o las cartas.
        val cual = Math.floorMod(k * 5 + 1, 3).let { if (it == 1 && libre < 76f * u) (if (k % 2 == 0) 2 else 0) else it }
        val pie = Offset(oc + minOf(libre * 0.5f, 42f * u), r.bottom - r.height * 0.02f)
        objeto(cual, pie, libre, r.height, a, a.en(cerca(Offset(pie.x, r.center.y))), k)
        if (libre > 150f * u) {
            val p2 = Offset(oc + 42f * u + minOf(libre - 80f * u, 110f * u), pie.y)
            objeto((cual + 1) % 3, p2, libre - 84f * u, r.height, a, a.en(cerca(Offset(p2.x, r.center.y))), k + 3)
        }
    }

    private fun DrawScope.objeto(cual: Int, pie: Offset, libre: Float, alto: Float, a: AireRenacimiento, t: TonoRenacimiento, k: Int) {
        val u = density
        when (cual) {
            0 -> librosTumbados(pie, minOf(libre * 0.8f, 96f * u), alto, a, t, k)
            1 -> {
                cassetta(pie, minOf(libre * 0.72f, 88f * u), alto, a, t)
                if (libre > 110f * u) salvadera(Offset(pie.x + minOf(libre * 0.72f, 88f * u) * 0.5f + 16f * u, pie.y), 40f * u, alto, a, t)
            }
            else -> cartas(pie, minOf(libre * 0.8f, 66f * u), alto, a, t, k)
        }
    }

    /** Dos o tres libros tumbados, de cueros distintos con su filete dorado, con un canto de pietra serena encima. */
    private fun DrawScope.librosTumbados(pie: Offset, an: Float, alto: Float, a: AireRenacimiento, t: TonoRenacimiento, s: Int) {
        val u = density
        val cueros = listOf(P.CUERO, mezcla(P.VERDE_FONDO, P.CUERO, 0.25f), mezcla(P.RUBIA, P.CUERO, 0.4f), mezcla(P.AZUL, P.CUERO, 0.4f))
        val n = 2 + s % 2
        var y = pie.y
        contactoRenacimiento(Offset(pie.x, pie.y), an * 1.1f, 4.5f * u, a, 0.42f)
        for (j in 0 until n) {
            val g = alto * (0.16f + 0.05f * azar(s, j))
            val lw = an * (1f - 0.09f * j)
            val x = pie.x - lw / 2 + (azar(s, 10 + j) - 0.5f) * 6f * u
            val tapa = t.c(cueros[(s + j) % cueros.size])
            drawRect(tapa, Offset(x, y - g), Size(lw, g))
            drawRect(t.c(P.PAPEL_OSC), Offset(x + 1.6f * u, y - g + g * 0.22f), Size(lw - 3.2f * u, g * 0.56f))
            val hojas = Path()
            for (l in 1..3) hojas.addRect(Rect(x + 1.6f * u, y - g + g * (0.22f + 0.14f * l), x + lw - 1.6f * u, y - g + g * (0.22f + 0.14f * l) + 0.4f * u))
            drawPath(hojas, t.s(0.22f))
            drawRect(t.c(P.ORO), Offset(x, y - g + g * 0.1f), Size(lw, 0.7f * u))
            drawRect(t.b(0.35f), Offset(x, y - g), Size(lw, 1f * u))
            drawRect(t.s(0.3f), Offset(x + lw * 0.78f, y - g), Size(lw * 0.22f, g))
            drawRect(t.f(0.7f), Offset(x, y - g), Size(lw, 1f * u))
            y -= g
        }
        val piedra = t.c(P.SERENA)
        drawOval(a.sombra(0.35f), Offset(pie.x - an * 0.08f, y - 2.2f * u), Size(an * 0.38f, 4.4f * u))
        val pp = Rect(pie.x - an * 0.17f, y - alto * 0.14f, pie.x + an * 0.17f, y + 0.5f * u)
        drawPath(poligonoRenacimiento(pp.left, pp.bottom, pp.left + pp.width * 0.1f, pp.top + pp.height * 0.2f, pp.left + pp.width * 0.6f, pp.top, pp.right, pp.top + pp.height * 0.35f, pp.right, pp.bottom), piedra)
        drawPath(poligonoRenacimiento(pp.left + pp.width * 0.1f, pp.top + pp.height * 0.2f, pp.left + pp.width * 0.6f, pp.top, pp.left + pp.width * 0.55f, pp.top + pp.height * 0.45f, pp.left + pp.width * 0.12f, pp.top + pp.height * 0.6f), t.b(0.35f))
        drawRect(t.s(0.25f), Offset(pp.left + pp.width * 0.65f, pp.top + pp.height * 0.4f), Size(pp.width * 0.35f, pp.height * 0.6f))
    }

    /**
     * La caja de escribir de nogal: la tapa en pendiente con su canto dorado, el frente con las cantoneras de latón y la
     * bocallave, y una pluma tumbada encima.
     */
    private fun DrawScope.cassetta(pie: Offset, an: Float, alto: Float, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val al = alto * 0.36f
        val x0 = pie.x - an / 2; val x1 = pie.x + an / 2
        val frente = Rect(x0, pie.y - al, x1, pie.y)
        val tapa = poligonoRenacimiento(x0, frente.top, x1, frente.top, x1 - an * 0.04f, frente.top - alto * 0.12f, x0 + an * 0.04f, frente.top - alto * 0.09f)
        contactoRenacimiento(Offset(pie.x + an * 0.05f, pie.y), an * 1.1f, 5f * u, a, 0.45f)
        drawPath(tapa, t.c(P.NOGAL_LUZ))
        drawPath(tapa, t.b(0.2f))
        drawPath(tapa, t.c(P.ORO), style = Stroke(0.8f * u))
        drawRect(t.c(P.NOGAL), frente.topLeft, frente.size)
        vetas(frente, t.c(P.VETA).copy(alpha = 0.35f), (pie.x / u).toInt(), densidad = 0.7f, grosor = 0.5f)
        drawRect(t.b(0.3f), frente.topLeft, Size(frente.width * 0.18f, frente.height))
        drawRect(t.s(0.3f), Offset(frente.right - frente.width * 0.25f, frente.top), Size(frente.width * 0.25f, frente.height))
        drawRect(t.c(P.ORO), Offset(frente.left, frente.top), Size(frente.width, 1f * u))
        // Las cantoneras de latón y la bocallave.
        val lat = t.c(P.LATON)
        val c = minOf(an, al) * 0.26f
        listOf(Offset(x0, frente.top), Offset(x1, frente.top), Offset(x0, pie.y), Offset(x1, pie.y)).forEach { p ->
            val sx = if (p.x < pie.x) 1f else -1f; val sy = if (p.y < frente.center.y) 1f else -1f
            drawPath(poligonoRenacimiento(p.x, p.y, p.x + sx * c, p.y, p.x, p.y + sy * c), lat)
        }
        val bl = Offset(pie.x, frente.center.y)
        drawPath(poligonoRenacimiento(bl.x, bl.y - al * 0.24f, bl.x + al * 0.16f, bl.y, bl.x, bl.y + al * 0.24f, bl.x - al * 0.16f, bl.y), lat)
        drawCircle(t.c(P.NOGAL_OSC), 0.9f * u, bl - Offset(0f, 0.6f * u))
        destelloRenacimiento(Offset(x0 + c * 0.3f, frente.top + c * 0.3f), 0.8f * u, t)
        drawRect(t.f(0.6f), Offset(x1 - 1.2f * u, frente.top), Size(1.2f * u, al))
        // La pluma tumbada sobre la tapa.
        val p0 = Offset(x0 + an * 0.15f, frente.top - alto * 0.1f)
        val p1 = Offset(x1 - an * 0.05f, frente.top - alto * 0.135f)
        drawLine(t.s(0.3f), p0 + Offset(0f, 2f * u), p1 + Offset(0f, 2f * u), 1.2f * u)
        drawLine(t.c(P.PAPEL_OSC), p0, p1, 1f * u)
        drawPath(poligonoRenacimiento(p1.x - an * 0.4f, p1.y + 0.4f * u, p1.x, p1.y, p1.x - an * 0.35f, p1.y - alto * 0.04f), t.c(mezcla(P.PAPEL, P.SERENA_CLARA, 0.3f)))
    }

    /** La salvadera de latón (la arena para secar la tinta), con su tapa agujereada, y el cortaplumas tumbado delante. */
    private fun DrawScope.salvadera(pie: Offset, an: Float, alto: Float, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val sw = minOf(an * 0.6f, alto * 0.32f)
        val sh = alto * 0.42f
        val x = pie.x - sw / 2
        contactoRenacimiento(Offset(pie.x, pie.y), sw * 1.6f, 4f * u, a)
        val laton = t.c(P.LATON)
        val cuerpo = poligonoRenacimiento(x, pie.y, x + sw, pie.y, x + sw * 0.8f, pie.y - sh * 0.5f, x + sw, pie.y - sh, x, pie.y - sh, x + sw * 0.2f, pie.y - sh * 0.5f)
        drawPath(cuerpo, laton)
        clipPath(cuerpo) {
            drawRect(t.b(0.45f), Offset(x + sw * 0.16f, pie.y - sh), Size(sw * 0.12f, sh))
            drawRect(t.s(0.3f), Offset(x + sw * 0.62f, pie.y - sh), Size(sw * 0.38f, sh))
            drawRect(t.c(mezcla(P.LATON, P.NOGAL_OSC, 0.4f)), Offset(x, pie.y - sh * 0.54f), Size(sw, sh * 0.08f))
        }
        drawOval(t.c(mezcla(P.LATON, P.NOGAL_OSC, 0.3f)), Offset(x, pie.y - sh - sw * 0.13f), Size(sw, sw * 0.26f))
        drawOval(t.c(P.LATON_LUZ), Offset(x + sw * 0.1f, pie.y - sh - sw * 0.1f), Size(sw * 0.8f, sw * 0.16f))
        val agujeros = Path()
        for (j in 0 until 5) { val c = Offset(x + sw * (0.25f + 0.12f * j), pie.y - sh - sw * 0.02f); agujeros.addOval(Rect(c.x - 0.6f * u, c.y - 0.5f * u, c.x + 0.6f * u, c.y + 0.5f * u)) }
        drawPath(agujeros, t.s(0.6f))
        destelloRenacimiento(Offset(x + sw * 0.22f, pie.y - sh * 0.75f), 1f * u, t)
        drawRect(t.f(0.6f), Offset(x + sw - 1.2f * u, pie.y - sh), Size(1.2f * u, sh))
        // El cortaplumas, tumbado delante: el mango de hueso y la hoja.
        val c0 = Offset(pie.x + sw * 0.7f, pie.y - 1.6f * u)
        drawLine(a.sombra(0.35f), c0 + Offset(1f * u, 1.4f * u), c0 + Offset(an * 0.55f, 0.2f * u), 2.2f * u)
        drawLine(t.c(P.PAPEL_OSC), c0, c0 + Offset(an * 0.35f, -0.6f * u), 2.6f * u, StrokeCap.Round)
        drawLine(t.c(P.HIERRO_LUZ), c0 + Offset(an * 0.35f, -0.6f * u), c0 + Offset(an * 0.56f, -1.8f * u), 1.5f * u, StrokeCap.Round)
    }

    /** Un mazo de cartas plegadas, atado con un cordón y con su lacre, apoyado de pie y ladeado. */
    private fun DrawScope.cartas(pie: Offset, an: Float, alto: Float, a: AireRenacimiento, t: TonoRenacimiento, s: Int) {
        val u = density
        val cw = minOf(an * 0.6f, alto * 0.34f, 40f * u)
        val chh = alto * 0.5f
        contactoRenacimiento(Offset(pie.x + cw * 0.3f, pie.y), cw * 1.7f, 4f * u, a)
        rotate(10f + 6f * azar(s, 3), Offset(pie.x + cw, pie.y)) {
            for (j in 0 until 4) {
                val dx = j * 1.3f * u; val dy = j * 1.7f * u
                val r = Rect(pie.x + dx, pie.y - chh + dy, pie.x + dx + cw, pie.y)
                drawRect(t.c(mezcla(P.PAPEL, P.PAPEL_OSC, 0.22f * j)), r.topLeft, r.size)
                drawRect(t.s(0.25f), Offset(r.right - 1.1f * u, r.top), Size(1.1f * u, r.height))
                drawRect(t.b(0.3f), r.topLeft, Size(r.width, 0.9f * u))
            }
            drawLine(t.c(P.SEPIA).copy(alpha = 0.5f), Offset(pie.x + cw * 0.15f, pie.y - chh * 0.78f), Offset(pie.x + cw * 0.7f, pie.y - chh * 0.78f), 0.6f * u)
            drawLine(t.c(P.CANAMO), Offset(pie.x - 1f * u, pie.y - chh * 0.5f), Offset(pie.x + cw + 5f * u, pie.y - chh * 0.47f), 1.3f * u)
            val lacre = Offset(pie.x + cw * 0.5f, pie.y - chh * 0.5f)
            drawCircle(a.sombra(0.35f), cw * 0.17f, lacre + Offset(0.8f * u, 1f * u))
            drawCircle(t.c(P.LACRE), cw * 0.17f, lacre)
            drawCircle(t.b(0.35f), cw * 0.05f, lacre - Offset(cw * 0.05f, cw * 0.05f))
            drawRect(t.f(0.6f), Offset(pie.x + cw - 1f * u, pie.y - chh), Size(1f * u, chh))
        }
    }
}
