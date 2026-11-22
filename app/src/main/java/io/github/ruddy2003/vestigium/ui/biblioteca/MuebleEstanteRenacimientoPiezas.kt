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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo de arriba del estante de Renacimiento, un bodegón de la tienda del cartolaio. Colgado del muro: el cordel con dos pliegos
 * recién impresos puestos a secar y la filza, el gancho de hierro donde se ensartan los recibos. Sobre la cornisa, de atrás
 * adelante: la cartera de cuero apoyada en el muro, la resma envuelta en papel azul con las tijeras, el protagonista (el libro
 * impreso abierto en su atril, con los anteojos de remaches encima y la cinta de seda que cae por la cornisa), el reloj de
 * arena, el tintero vidriado de verde con su pluma y, en el canto, una carta plegada con su lacre, cortada por el borde. La
 * resma, la pila de libros y el farol (de noche, la única luz) están en [FarolEstanteRenacimiento].
 */
internal object PiezasEstanteRenacimiento {
    private val P = PaletaRenacimiento
    private val M = MuebleEstanteRenacimiento

    /** La capitular grabada de lo impreso: el cuadro negro con una florecilla de cuatro pétalos y su tallo (no es una letra). */
    private fun DrawScope.capitular(cap: Rect, t: TonoRenacimiento) {
        drawRect(t.c(P.IMPRENTA).copy(alpha = 0.85f), cap.topLeft, cap.size)
        val c = Offset(cap.center.x, cap.top + cap.height * 0.42f)
        val r = minOf(cap.width, cap.height) * 0.16f
        val papel = t.c(P.PAPEL).copy(alpha = 0.8f)
        val flor = Path()
        listOf(Offset(0f, -1f), Offset(1f, 0f), Offset(0f, 1f), Offset(-1f, 0f)).forEach { d ->
            val p = c + d * (r * 1.05f)
            flor.addOval(Rect(p.x - r * 0.62f, p.y - r * 0.62f, p.x + r * 0.62f, p.y + r * 0.62f))
        }
        drawPath(flor, papel)
        drawCircle(t.c(P.RUBRICA), r * 0.42f, c)
        drawLine(papel, Offset(c.x, c.y + r * 1.6f), Offset(c.x + r * 0.6f, cap.bottom - r * 0.4f), r * 0.35f)
    }

    // ---------------------------------------------------------------- colgado del muro

    fun DrawScope.colgado(a: AireRenacimiento, apoyo: Float, cerca: (Offset) -> Float) {
        val u = density
        val w = size.width
        val c0 = Offset(w * 0.371f, w * 0.128f)
        val c1 = Offset(w * 0.6f, w * 0.112f)
        val comba = w * 0.026f
        fun en(t: Float) = Offset(c0.x + (c1.x - c0.x) * t, c0.y + (c1.y - c0.y) * t + comba * 4f * t * (1f - t))
        val cuerda = Path().apply { moveTo(c0.x, c0.y); for (i in 1..16) { val p = en(i / 16f); lineTo(p.x, p.y) } }
        listOf(0.24f, 0.78f).forEachIndexed { j, t ->
            val p = en(t)
            pliego(p, w * (0.092f + 0.008f * j), w * (0.13f + 0.02f * j), (azar(53, j) - 0.5f) * 5f, j, a, a.en(cerca(Offset(p.x, p.y + w * 0.06f))), apoyo)
        }
        sombraEnMuroRenacimiento(cuerda, c0, apoyo, a, 0.25f, 0.3f)
        drawPath(cuerda, a.luz(P.CANAMO, cerca(en(0.5f))), style = Stroke(1.2f * u))
        listOf(c0, c1).forEach { c -> clavo(c, 1.6f * u, a, a.en(cerca(c))) }
        pergamino(Rect(w * 0.62f, w * 0.095f, w * 0.81f, w * 0.335f), a, a.en(cerca(Offset(w * 0.72f, w * 0.22f))), apoyo)
        filza(Offset(w * 0.945f, w * 0.118f), w, a, a.en(cerca(Offset(w * 0.945f, w * 0.2f))), apoyo)
    }

    private fun DrawScope.clavo(c: Offset, r: Float, a: AireRenacimiento, t: TonoRenacimiento) {
        drawCircle(a.sombra(0.45f), r * 1.05f, c + Offset(r * 0.6f, r * 0.7f))
        drawCircle(t.c(P.HIERRO), r, c)
        drawCircle(a.luz(P.HIERRO_LUZ, t.k).copy(alpha = 0.8f), r * 0.4f, c - Offset(r * 0.3f, r * 0.3f))
    }

    /** Un pliego colgado: la hoja que cae del doblez, ondulada; la de atrás que asoma; su sombra en el muro y lo impreso. */
    private fun DrawScope.pliego(doblez: Offset, ancho: Float, largo: Float, giro: Float, j: Int, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        rotate(giro, doblez) {
            val x0 = doblez.x - ancho / 2; val x1 = doblez.x + ancho / 2
            val y0 = doblez.y
            val ond = 1.6f * u * (1f + azar(57, j))
            val hoja = Path().apply {
                moveTo(x0, y0 + 1.5f * u)
                quadraticTo(doblez.x, y0 - 1.5f * u, x1, y0 + 1.5f * u)
                quadraticTo(x1 + ond, y0 + largo * 0.5f, x1 - ond * 0.5f, y0 + largo)
                quadraticTo(doblez.x, y0 + largo + ond * 1.8f, x0 + ond * 0.4f, y0 + largo - ond)
                quadraticTo(x0 - ond, y0 + largo * 0.5f, x0, y0 + 1.5f * u)
                close()
            }
            sombraEnMuroRenacimiento(hoja, doblez, hasta, a, 0.3f, 0.6f)
            drawRect(t.c(P.PAPEL_OSC), Offset(x1 - ancho * 0.12f, y0 + 1f * u), Size(ancho * 0.16f, largo * 0.8f))
            drawPath(hoja, t.c(mezcla(P.PAPEL, P.PAPEL_OSC, 0.12f * azar(59, j))))
            clipPath(hoja) {
                // La comba del papel: una luz larga a la izquierda y la sombra de la onda a la derecha.
                drawRect(t.b(0.35f), Offset(x0, y0), Size(ancho * 0.2f, largo * 1.1f))
                drawRect(t.s(0.14f), Offset(x1 - ancho * 0.3f, y0), Size(ancho * 0.3f, largo * 1.1f))
                drawRect(t.s(0.22f), Offset(x0, y0), Size(ancho, 2.4f * u))
                // Lo impreso: una caja de trazos apretados (no son letras), la capital grabada y la rúbrica roja.
                val tinta = t.c(P.IMPRENTA).copy(alpha = 0.6f)
                val rojo = t.c(P.RUBRICA)
                val mx0 = x0 + ancho * 0.15f; val mx1 = x1 - ancho * 0.15f
                val lineas = 11 + j
                val paso = (largo * 0.66f) / lineas
                val texto = Path()
                for (l in 0 until lineas) {
                    val y = y0 + largo * 0.18f + paso * l
                    val sangria = if (l < 3) ancho * 0.2f else 0f
                    val fin = if (l == lineas - 1) mx0 + (mx1 - mx0) * 0.45f else mx1 - ancho * 0.04f * azar(61 + j, l)
                    texto.addRect(Rect(mx0 + sangria, y - 0.45f * u, fin, y + 0.45f * u))
                }
                drawPath(texto, tinta)
                val cap = Rect(mx0, y0 + largo * 0.18f - 1.2f * u, mx0 + ancho * 0.16f, y0 + largo * 0.18f + paso * 2.4f)
                capitular(cap, t)
                drawLine(rojo, Offset(mx0, y0 + largo * 0.12f), Offset(mx1, y0 + largo * 0.12f), 0.9f * u)
            }
        }
    }

    /** La filza: el gancho de hierro con su anilla y la punta doblada, con los recibos ensartados, cada uno de su papel. */
    private fun DrawScope.filza(clavo: Offset, w: Float, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        val largo = w * 0.17f
        val gancho = Path().apply {
            moveTo(clavo.x, clavo.y)
            lineTo(clavo.x, clavo.y + largo)
            quadraticTo(clavo.x, clavo.y + largo + w * 0.02f, clavo.x - w * 0.018f, clavo.y + largo + w * 0.01f)
        }
        sombraEnMuroRenacimiento(gancho, clavo, hasta, a, 0.3f, 0.4f)
        val papeles = listOf(P.PAPEL, P.PAPEL_OSC, mezcla(P.PAPEL, P.AZUL, 0.2f), P.PAPEL, mezcla(P.PAPEL_OSC, P.RUBRICA, 0.12f), P.PAPEL)
        papeles.forEachIndexed { j, col ->
            val y = clavo.y + largo * (0.28f + 0.11f * j)
            val g = (azar(91, j) - 0.5f) * 28f
            rotate(g, Offset(clavo.x, y)) {
                val r = Rect(clavo.x - w * (0.035f + 0.01f * azar(93, j)), y - w * 0.018f, clavo.x + w * (0.035f + 0.01f * azar(95, j)), y + w * 0.018f)
                drawRect(a.sombra(0.25f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
                drawRect(t.c(col), r.topLeft, r.size)
                drawRect(t.b(0.25f), r.topLeft, Size(r.width, 0.9f * u))
                drawRect(t.s(0.15f), Offset(r.left, r.center.y), Size(r.width, 0.7f * u))
                drawLine(t.c(P.SEPIA).copy(alpha = 0.5f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.3f), Offset(r.left + r.width * (0.5f + 0.3f * azar(97, j)), r.top + r.height * 0.3f), 0.6f * u)
            }
        }
        drawPath(gancho, t.c(P.HIERRO), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawCircle(t.c(P.HIERRO), w * 0.012f, Offset(clavo.x, clavo.y + w * 0.008f), style = Stroke(1.4f * u))
        clavo(clavo, 1.6f * u, a, t)
    }

    // ---------------------------------------------------------------- sobre la cornisa

    fun DrawScope.arriba(a: AireRenacimiento, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val pie = apoyo + w * 0.006f
        fun t(x: Float, y: Float = pie - w * 0.06f) = a.en(cerca(Offset(x, y)))
        with(FarolEstanteRenacimiento) { resma(Offset(w * 0.07f, pie), w * 0.205f, a, t(w * 0.17f), apoyo) }
        atril(Offset(w * 0.455f, pie), w * 0.35f, a, cerca, apoyo)
        reloj(Offset(w * 0.668f, pie), w * 0.074f, a, t(w * 0.668f), apoyo)
        with(FarolEstanteRenacimiento) {
            val pila = pilaLibros(Offset(w * M.LAMP_X, pie), w * 0.19f, a, t(w * M.LAMP_X, pie - w * 0.03f), apoyo)
            farol(Offset(w * M.LAMP_X, pila), a, t(w * M.LAMP_X, w * M.LAMP_Y + w * 0.02f), apoyo)
        }
        tintero(Offset(w * 0.27f, pie + w * 0.002f), w * 0.05f, a, t(w * 0.27f), apoyo)
        carta(Offset(w * 0.955f, pie), w, a, t(w * 0.95f))
    }

    /**
     * Una piel de pergamino entera colgada de dos clavos, como las vendía el cartolaio: el contorno de la piel con el cuello y
     * las patas, translúcida en los bordes, con su color de hueso, las manchas del raspado y su sombra en el muro.
     */
    private fun DrawScope.pergamino(r: Rect, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        fun p(fx: Float, fy: Float) = Offset(r.left + r.width * fx, r.top + r.height * fy)
        val piel = Path().apply {
            val q = listOf(p(0.4f, 0.07f), p(0.44f, 0f), p(0.56f, 0f), p(0.6f, 0.07f), p(0.72f, 0.1f), p(0.86f, 0.03f), p(1f, 0f), p(0.95f, 0.11f),
                p(0.84f, 0.23f), p(0.87f, 0.5f), p(0.84f, 0.7f), p(0.93f, 0.85f), p(1f, 1f), p(0.85f, 0.96f), p(0.7f, 0.9f), p(0.56f, 0.94f),
                p(0.5f, 1f), p(0.44f, 0.94f), p(0.3f, 0.9f), p(0.15f, 0.96f), p(0f, 1f), p(0.07f, 0.85f), p(0.16f, 0.7f), p(0.13f, 0.5f),
                p(0.16f, 0.23f), p(0.05f, 0.11f), p(0f, 0f), p(0.14f, 0.03f), p(0.28f, 0.1f))
            moveTo((q[0].x + q.last().x) / 2, (q[0].y + q.last().y) / 2)
            for (i in q.indices) {
                val a0 = q[i]; val a1 = q[(i + 1) % q.size]
                quadraticTo(a0.x, a0.y, (a0.x + a1.x) / 2, (a0.y + a1.y) / 2)
            }
            close()
        }
        sombraEnMuroRenacimiento(piel, r.center, hasta, a, 0.28f, 0.6f)
        drawPath(piel, t.c(mezcla(P.PAPEL, P.PAPEL_OSC, 0.35f)))
        clipPath(piel) {
            drawRect(t.b(0.3f), Offset(r.left, r.top), Size(r.width * 0.35f, r.height))
            drawRect(t.s(0.12f), Offset(r.left + r.width * 0.6f, r.top), Size(r.width * 0.4f, r.height))
            // Lo que se raspó: manchas más claras y la espina, más oscura, a lo largo.
            drawLine(t.c(P.PAPEL_OSC).copy(alpha = 0.7f), p(0.5f, 0.05f), p(0.5f, 0.95f), r.width * 0.06f)
            drawOval(t.b(0.25f), p(0.2f, 0.3f), Size(r.width * 0.25f, r.height * 0.3f))
            drawOval(t.s(0.08f), p(0.58f, 0.55f), Size(r.width * 0.28f, r.height * 0.22f))
        }
        drawPath(piel, t.c(P.PAPEL_OSC).copy(alpha = 0.8f), style = Stroke(1f * u))
        listOf(p(0.07f, 0.05f), p(0.93f, 0.05f)).forEach { c -> clavo(c, 1.6f * u, a, t) }
        drawRect(t.f(0.4f), Offset(r.right - r.width * 0.1f, r.top + r.height * 0.2f), Size(1f * u, r.height * 0.6f))
    }

    /**
     * El protagonista: el libro impreso abierto en su atril de nogal, con la caja de texto a dos columnas, la capital grabada,
     * las rúbricas y las notas al margen, los anteojos de remaches encima y la cinta de seda que cae por la cornisa.
     */
    private fun DrawScope.atril(pie: Offset, an: Float, a: AireRenacimiento, cerca: (Offset) -> Float, hasta: Float) {
        val u = density
        val w = size.width
        val t = a.en(cerca(Offset(pie.x, pie.y - an * 0.3f)))
        val td = a.en(cerca(Offset(pie.x + an * 0.3f, pie.y - an * 0.3f)))
        val al = an * 0.6f
        val base = Rect(pie.x - an * 0.47f, pie.y - an * 0.1f, pie.x + an * 0.47f, pie.y - an * 0.012f)
        val tab = Rect(pie.x - an * 0.45f, pie.y - al, pie.x + an * 0.45f, base.top)
        sombraEnMuroRenacimiento(poligonoRenacimiento(tab.left, tab.bottom, tab.left + an * 0.02f, tab.top, tab.right - an * 0.02f, tab.top, tab.right, tab.bottom), Offset(pie.x, tab.center.y), hasta, a, 0.3f, 0.6f)
        contactoRenacimiento(Offset(pie.x + an * 0.05f, pie.y), an * 1.08f, 5f * u, a, 0.42f)
        // El atril: la caja de abajo sobre sus bollos, con su cuarterón y su junquillo dorado, y el tablero inclinado.
        listOf(base.left + an * 0.06f, base.right - an * 0.06f).forEach { x -> drawOval(t.c(P.NOGAL_OSC), Offset(x - an * 0.03f, pie.y - an * 0.016f), Size(an * 0.06f, an * 0.02f)) }
        drawRect(t.c(P.NOGAL), base.topLeft, base.size)
        drawRect(t.c(P.NOGAL_LUZ), base.topLeft, Size(base.width, base.height * 0.22f))
        drawRect(t.c(P.ORO), Offset(base.left, base.top + base.height * 0.22f), Size(base.width, 1f * u))
        val cu = Rect(base.left + an * 0.06f, base.top + base.height * 0.36f, base.right - an * 0.06f, base.bottom - base.height * 0.14f)
        drawRect(t.s(0.35f), cu.topLeft, cu.size, style = Stroke(1f * u))
        drawRect(t.b(0.25f), Offset(cu.left + 1f * u, cu.bottom - 0.5f * u), Size(cu.width, 0.8f * u))
        drawRect(t.b(0.4f), base.topLeft, Size(base.width, 0.9f * u))
        drawRect(t.s(0.3f), Offset(base.right - base.width * 0.2f, base.top), Size(base.width * 0.2f, base.height))
        drawRect(td.f(0.6f), Offset(base.right - 1.2f * u, base.top), Size(1.2f * u, base.height))
        val tablero = poligonoRenacimiento(tab.left, tab.bottom, tab.left + an * 0.02f, tab.top, tab.right - an * 0.02f, tab.top, tab.right, tab.bottom)
        drawPath(tablero, t.c(mezcla(P.NOGAL, P.NOGAL_LUZ, 0.5f)))
        drawPath(tablero, t.c(P.ORO), style = Stroke(0.9f * u))
        // El libro abierto: las tapas de cuero que asoman, el grueso de las hojas y las dos hojas que se curvan hacia el lomo.
        val gy = tab.top + al * 0.06f
        val by = tab.bottom - al * 0.05f
        val izq = tab.left + an * 0.015f; val der = tab.right - an * 0.015f
        val lomo = pie.x
        drawRect(t.c(P.CUERO), Offset(izq - an * 0.01f, gy + al * 0.04f), Size(der - izq + an * 0.02f, by - gy))
        drawRect(t.c(P.PAPEL_OSC), Offset(izq + 1f * u, by - 2f * u), Size(der - izq - 2f * u, 4f * u))
        val cantos = Path()
        for (l in 0..2) cantos.addRect(Rect(izq + 2f * u, by + l * 1.2f * u - 1.6f * u, der - 2f * u, by + l * 1.2f * u - 1.2f * u))
        drawPath(cantos, t.s(0.25f))
        fun hoja(fuera: Float, dentro: Float): Path = Path().apply {
            moveTo(fuera, gy + al * 0.06f)
            quadraticTo((fuera + dentro) / 2, gy - al * 0.03f, dentro, gy + al * 0.1f)
            lineTo(dentro, by)
            quadraticTo((fuera + dentro) / 2, by - al * 0.06f, fuera, by - al * 0.02f)
            close()
        }
        val hi = hoja(izq, lomo); val hd = hoja(der, lomo)
        drawPath(hi, t.c(P.PAPEL))
        drawPath(hd, td.c(mezcla(P.PAPEL, P.PAPEL_OSC, 0.2f)))
        // La comba de las hojas: más oscuro hacia el lomo; la luz de la ventana en la de la izquierda.
        clipPath(hi) {
            drawRect(t.s(0.1f), Offset(lomo - an * 0.12f, gy), Size(an * 0.12f, by - gy))
            drawRect(t.s(0.18f), Offset(lomo - an * 0.05f, gy), Size(an * 0.05f, by - gy))
            if (!a.lumbre) mancha(Offset(izq + an * 0.12f, gy + al * 0.3f), an * 0.22f, Color.White.copy(alpha = 0.3f))
        }
        clipPath(hd) {
            drawRect(t.s(0.16f), Offset(lomo, gy), Size(an * 0.06f, by - gy))
            drawRect(td.s(0.06f), Offset(lomo, gy), Size(der - lomo, by - gy))
            drawRect(td.b(0.3f), Offset(der - an * 0.06f, gy), Size(an * 0.06f, by - gy))
        }
        drawLine(t.s(0.55f), Offset(lomo, gy + al * 0.1f), Offset(lomo, by), 0.9f * u)
        // La caja de texto, a dos columnas en cada hoja (trazos, no letras), la capital grabada, las rúbricas y las notas.
        val texto = Path(); val rubricas = Path(); val notas = Path()
        listOf(izq to lomo, lomo to der).forEachIndexed { lado, (x0, x1) ->
            val m0 = x0 + (x1 - x0) * 0.13f; val m1 = x1 - (x1 - x0) * 0.13f
            val col = (m1 - m0) * 0.46f
            for (c in 0..1) {
                val cx0 = m0 + c * (m1 - m0) * 0.54f
                for (l in 0 until 11) {
                    val y = gy + al * (0.17f + 0.058f * l)
                    if (y > by - al * 0.08f) break
                    val corto = lado == 0 && c == 0 && l < 3
                    val r = Rect(cx0 + if (corto) col * 0.42f else 0f, y - 0.4f * u, cx0 + col * (if (l == 10) 0.5f else 1f), y + 0.4f * u)
                    if (l == 5 && c == 1) rubricas.addRect(r) else texto.addRect(r)
                }
            }
            rubricas.addRect(Rect(m0, gy + al * 0.115f - 0.4f * u, m1, gy + al * 0.115f + 0.4f * u))
            val margen = if (lado == 0) x0 + (x1 - x0) * 0.04f else m1 + (x1 - x0) * 0.02f
            for (q in 0 until 3) notas.addRect(Rect(margen, gy + al * (0.3f + 0.08f * q), margen + (x1 - x0) * 0.06f, gy + al * (0.3f + 0.08f * q) + 0.6f * u))
        }
        drawPath(texto, t.c(P.IMPRENTA).copy(alpha = 0.55f))
        drawPath(rubricas, t.c(P.RUBRICA))
        drawPath(notas, t.c(P.SEPIA).copy(alpha = 0.5f))
        val m0 = izq + (lomo - izq) * 0.13f
        val cap = Rect(m0, gy + al * 0.14f, m0 + (lomo - izq) * 0.14f, gy + al * 0.3f)
        capitular(cap, t)
        // El listón de delante, que sostiene las hojas.
        drawRect(t.c(P.NOGAL_LUZ), Offset(tab.left, tab.bottom - al * 0.06f), Size(tab.width, al * 0.06f))
        drawRect(t.b(0.4f), Offset(tab.left, tab.bottom - al * 0.06f), Size(tab.width, 0.9f * u))
        drawRect(td.f(0.7f), Offset(tab.left + tab.width * 0.5f, tab.bottom - al * 0.06f), Size(tab.width * 0.5f, 0.9f * u))
        // La cinta de seda verde, que sale del lomo y cae por delante del atril y de la cornisa.
        val cinta = t.c(P.VERDE_FONDO)
        val c0 = Offset(lomo + 1f * u, by)
        val c1 = Offset(lomo + an * 0.04f, pie.y + w * 0.05f)
        drawLine(a.sombra(0.35f), c0 + Offset(1.6f * u, 1.6f * u), c1 + Offset(1.6f * u, 1.6f * u), 2.6f * u)
        drawLine(cinta, c0, c1, 2.6f * u)
        drawPath(poligonoRenacimiento(c1.x - 1.3f * u, c1.y, c1.x + 1.3f * u, c1.y, c1.x + 1.3f * u, c1.y + 3.4f * u, c1.x, c1.y + 2f * u, c1.x - 1.3f * u, c1.y + 3.4f * u), cinta)
        drawLine(t.b(0.35f), c0 - Offset(0.9f * u, 0f), c1 - Offset(0.9f * u, 0f), 0.6f * u)
        // Los anteojos de remache sobre la hoja derecha: dos aros de hueso con su clavo, el vidrio y la sombra que dejan.
        val o = Offset(lomo + (der - lomo) * 0.5f, gy + al * 0.62f)
        val ra = an * 0.055f
        val aro = td.c(mezcla(P.CUERO, P.NOGAL_OSC, 0.3f))
        listOf(-1f, 1f).forEach { s ->
            val c = Offset(o.x + s * ra * 1.1f, o.y)
            drawCircle(a.sombra(0.3f), ra, c + Offset(2f * u, 2.4f * u), style = Stroke(1.6f * u))
            drawCircle(Color.White.copy(alpha = if (a.oscuro) 0.06f else 0.2f), ra, c)
            drawCircle(aro, ra, c, style = Stroke(1.7f * u))
            drawArc(td.b(0.6f), 200f, 60f, false, c - Offset(ra * 0.7f, ra * 0.7f), Size(ra * 1.4f, ra * 1.4f), style = Stroke(0.8f * u))
        }
        drawLine(aro, Offset(o.x - ra * 0.2f, o.y - ra * 0.9f), Offset(o.x + ra * 0.2f, o.y - ra * 0.9f), 1.6f * u)
        destelloRenacimiento(Offset(o.x + ra * 0.7f, o.y - ra * 0.4f), 0.9f * u, td)
    }

    /** El reloj de arena: dos discos de nogal con su moldura, tres balaustres y las dos ampollas, con la arena casi toda abajo. */
    private fun DrawScope.reloj(pie: Offset, an: Float, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        val al = an * 1.95f
        sombraEnMuroRenacimiento(poligonoRenacimiento(pie.x - an / 2, pie.y, pie.x + an / 2, pie.y, pie.x + an / 2, pie.y - al, pie.x - an / 2, pie.y - al),
            Offset(pie.x, pie.y - al / 2), hasta, a, 0.22f, 0.6f)
        contactoRenacimiento(Offset(pie.x + an * 0.1f, pie.y), an * 1.5f, 4f * u, a)
        val mad = t.c(P.NOGAL)
        val disco = al * 0.085f
        listOf(pie.y - disco, pie.y - al).forEach { y ->
            drawRect(mad, Offset(pie.x - an / 2, y), Size(an, disco))
            drawRect(t.c(P.NOGAL_LUZ), Offset(pie.x - an / 2, y), Size(an, disco * 0.35f))
            drawRect(t.s(0.3f), Offset(pie.x + an * 0.25f, y), Size(an * 0.25f, disco))
        }
        val y0 = pie.y - al + disco; val y1 = pie.y - disco
        val cy = (y0 + y1) / 2
        val vidrio = Color.White.copy(alpha = if (a.oscuro) 0.07f else 0.2f)
        listOf(y0 to cy, cy to y1).forEach { (ya, yb) -> drawOval(vidrio, Offset(pie.x - an * 0.32f, ya), Size(an * 0.64f, yb - ya)) }
        val arena = t.c(Color(0xFFCFA968))
        drawPath(poligonoRenacimiento(pie.x - an * 0.28f, y1 - 1f * u, pie.x + an * 0.28f, y1 - 1f * u, pie.x + an * 0.07f, y1 - (y1 - cy) * 0.58f, pie.x - an * 0.07f, y1 - (y1 - cy) * 0.58f), arena)
        drawPath(poligonoRenacimiento(pie.x - an * 0.12f, cy - (cy - y0) * 0.3f, pie.x + an * 0.12f, cy - (cy - y0) * 0.3f, pie.x, cy), arena)
        drawLine(arena, Offset(pie.x, cy), Offset(pie.x, y1 - (y1 - cy) * 0.58f), 0.6f * u)
        listOf(y0 to cy, cy to y1).forEach { (ya, yb) ->
            drawOval(t.b(0.5f), Offset(pie.x - an * 0.32f, ya), Size(an * 0.64f, yb - ya), style = Stroke(0.6f * u))
            drawLine(t.b(0.8f), Offset(pie.x - an * 0.2f, ya + (yb - ya) * 0.3f), Offset(pie.x - an * 0.16f, ya + (yb - ya) * 0.62f), 1.1f * u)
        }
        listOf(-0.42f, 0f, 0.42f).forEach { f ->
            if (f != 0f) {
                drawLine(mad, Offset(pie.x + an * f, y0), Offset(pie.x + an * f, y1), 2f * u)
                drawCircle(mad, 1.8f * u, Offset(pie.x + an * f, cy))
            }
        }
        drawLine(t.f(0.7f), Offset(pie.x + an * 0.44f, y0), Offset(pie.x + an * 0.44f, y1), 0.8f * u)
    }

    /** El tintero de barro vidriado de verde (el de la tierra de la ciudad), con la pluma de ganso metida, y su sombra. */
    private fun DrawScope.tintero(pie: Offset, an: Float, a: AireRenacimiento, t: TonoRenacimiento, hasta: Float) {
        val u = density
        val al = an * 0.8f
        val boca = Offset(pie.x, pie.y - al)
        val punta = Offset(pie.x - an * 0.6f, pie.y - al - an * 2.1f)
        val barba = poligonoRenacimiento(boca.x - an * 0.12f, boca.y - an * 0.55f, punta.x - an * 0.14f, punta.y + an * 0.2f, punta.x, punta.y,
            punta.x + an * 0.3f, punta.y + an * 0.45f, boca.x + an * 0.06f, boca.y - an * 0.45f)
        sombraEnMuroRenacimiento(barba, boca, hasta, a, 0.25f, 0.5f)
        contactoRenacimiento(Offset(pie.x + an * 0.1f, pie.y), an * 1.5f, 3.6f * u, a)
        drawLine(t.c(P.PAPEL_OSC), boca, punta, 1.1f * u)
        drawPath(barba, t.c(mezcla(P.PAPEL, P.SERENA_CLARA, 0.3f)))
        drawLine(t.s(0.3f), Offset(boca.x - an * 0.03f, boca.y - an * 0.5f), Offset(punta.x + an * 0.12f, punta.y + an * 0.3f), 0.5f * u)
        val cuerpo = Path().apply {
            moveTo(pie.x - an / 2, pie.y)
            cubicTo(pie.x - an * 0.62f, pie.y - al * 0.5f, pie.x - an * 0.4f, pie.y - al * 0.95f, pie.x - an * 0.2f, pie.y - al)
            lineTo(pie.x + an * 0.2f, pie.y - al)
            cubicTo(pie.x + an * 0.4f, pie.y - al * 0.95f, pie.x + an * 0.62f, pie.y - al * 0.5f, pie.x + an / 2, pie.y)
            close()
        }
        drawPath(cuerpo, t.c(Color(0xFF4F7A4E)))
        clipPath(cuerpo) {
            drawRect(t.b(0.3f), Offset(pie.x - an * 0.4f, pie.y - al), Size(an * 0.16f, al))
            drawRect(t.s(0.3f), Offset(pie.x + an * 0.15f, pie.y - al), Size(an * 0.4f, al))
            drawRect(t.c(P.OCRE).copy(alpha = 0.7f), Offset(pie.x - an, pie.y - al * 0.45f), Size(an * 2f, al * 0.08f))
        }
        drawOval(t.c(P.IMPRENTA), Offset(boca.x - an * 0.18f, boca.y - 1.2f * u), Size(an * 0.36f, 2.4f * u))
        destelloRenacimiento(Offset(pie.x - an * 0.25f, pie.y - al * 0.6f), 1f * u, t)
    }

    /** Una carta plegada con su lacre, en el canto de la cornisa, que vuela sobre el borde y se corta en él. */
    private fun DrawScope.carta(pie: Offset, w: Float, a: AireRenacimiento, t: TonoRenacimiento) {
        val u = density
        val r = Rect(pie.x - w * 0.04f, pie.y - w * 0.012f, pie.x + w * 0.07f, pie.y + w * 0.006f)
        drawRect(a.sombra(0.35f), Offset(r.left + 1.5f * u, r.bottom), Size(r.width, w * 0.008f))
        drawRect(t.c(P.PAPEL), r.topLeft, r.size)
        drawRect(t.s(0.15f), Offset(r.left, r.center.y), Size(r.width, r.height / 2))
        drawLine(t.s(0.3f), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.3f, r.bottom), 0.6f * u)
        drawCircle(t.c(P.LACRE), w * 0.011f, Offset(r.left + r.width * 0.3f, r.center.y))
        drawCircle(t.b(0.35f), w * 0.004f, Offset(r.left + r.width * 0.28f, r.center.y - w * 0.004f))
    }
}
