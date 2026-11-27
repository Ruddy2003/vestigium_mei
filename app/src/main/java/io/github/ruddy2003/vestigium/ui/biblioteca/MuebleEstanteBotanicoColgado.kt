package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * La sombra de algo en la pared del estante de Botánico: de día, corrida a la derecha y abajo (al ocaso, más larga); de noche,
 * hacia fuera del quinqué y tanto más marcada cuanto más cerca está de él. No baja de [hasta].
 */
internal fun DrawScope.sombraEnParedBotanico(p: Path, desde: Offset, hasta: Float, s: SitioBotanico, a: Float = 0.2f) {
    val w = size.width
    clipRect(0f, 0f, size.width, hasta) {
        if (!s.a.oscuro) {
            val dx = if (s.a.noche) 0.04f else 0.024f
            // Al ocaso, solo hay sombra donde cae el sol: fuera de su parche, apenas.
            val f = if (s.a.noche) 0.15f + 0.85f * s.a.luzDia(desde.x / w, desde.y / w) else 1f
            translate(w * dx, w * (if (s.a.noche) 0.008f else 0.013f)) { drawPath(p, s.t(desde).s(a * f)) }
        } else {
            val k = s.k(desde)
            if (k > 0.1f) {
                val dx = (desde.x / w - MuebleEstanteBotanico.LAMP_X).coerceIn(-0.3f, 0.3f) / 0.3f
                translate(w * 0.04f * dx, w * 0.012f) { drawPath(p, PaletaBotanico.NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
            }
        }
    }
}

/**
 * Lo que cuelga de la moldura de la pared en la corona del estante de Botánico ([MuebleEstanteBotanico]): dos ramilletes de
 * hierbas puestos a secar boca abajo y la caja de insectos con sus escarabajos y sus mariposas clavados, cada uno con su alfiler,
 * su sombra en el papel y su etiquetita (sin letra).
 */
internal object ColgadoEstanteBotanico {
    private val P = PaletaBotanico

    /** Lo que cuelga de la pared: la caja de insectos y dos ramilletes de hierbas puestos a secar, de la moldura. */
    fun DrawScope.colgado(s: SitioBotanico, apoyo: Float) {
        val w = size.width
        ramillete(w * 0.075f, w * 0.135f, s, 0)
        ramillete(w * 0.335f, w * 0.115f, s, 1)
        cajaInsectos(Rect(w * 0.425f, w * 0.11f, w * 0.665f, w * 0.305f), s)
    }

    /**
     * Un ramillete de hierbas puesto a secar, boca abajo: cuelga de un clavo de la moldura por un cordel, atado con bramante, y
     * sus tallos se abren hacia abajo con las hojas secas y las cabezuelas de flor, de [largo] de alto.
     */
    private fun DrawScope.ramillete(x: Float, largo: Float, s: SitioBotanico, cual: Int) {
        val w = size.width; val u = density
        val railY = w * (MuroEstanteBotanico.MOLDURA_Y + MuroEstanteBotanico.MOLDURA_ALTO)
        val atado = Offset(x, railY + w * 0.03f)
        val t = s.t(Offset(x, atado.y + largo * 0.5f))
        val seco = if (cual == 0) mezcla(P.HOJA, P.KRAFT, 0.45f) else mezcla(P.HOJA_OSC, P.KRAFT, 0.3f)
        val flor = if (cual == 0) Color(0xFF8A6A8E) else Color(0xFFC9A54A)
        // Los tallos, abiertos en abanico hacia abajo, y lo que dejan en la pared.
        val tallos = Path(); val hojas = Path(); val puntas = ArrayList<Offset>()
        for (j in 0 until 9) {
            val an = 1.5708f + (j - 4f) * 0.11f + (azar(9250 + cual, j) - 0.5f) * 0.08f
            val l = largo * (0.75f + 0.25f * azar(9260 + cual, j))
            val fin = Offset(atado.x + l * kotlin.math.cos(an), atado.y + l * kotlin.math.sin(an))
            val c = Offset(atado.x + l * 0.5f * kotlin.math.cos(an) + (azar(9270 + cual, j) - 0.5f) * w * 0.01f, atado.y + l * 0.5f)
            tallos.moveTo(atado.x, atado.y); tallos.quadraticTo(c.x, c.y, fin.x, fin.y)
            puntas += fin
            for (q in 1 until 5) {
                val f = q / 5f
                val p = Offset(atado.x + (fin.x - atado.x) * f, atado.y + (fin.y - atado.y) * f)
                val lado = if ((q + j) % 2 == 0) 1f else -1f
                val hl = largo * 0.1f * (1.1f - f * 0.4f)
                hojas.moveTo(p.x, p.y)
                hojas.quadraticTo(p.x + lado * hl * 0.6f, p.y + hl * 0.2f, p.x + lado * hl * 0.8f, p.y + hl * 0.75f)
            }
        }
        val sombra = Path().apply { addPath(tallos); addPath(hojas) }
        if (!s.a.oscuro) drawPath(Path().apply { addPath(sombra, Offset(w * 0.022f, w * 0.012f)) }, t.s(0.18f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(tallos, t.c(mezcla(seco, P.KRAFT, 0.3f), -0.05f), style = Stroke(1f * u, cap = StrokeCap.Round))
        drawPath(hojas, t.c(seco), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(hojas, t.b(0.25f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        // Las cabezuelas de flor secas en las puntas.
        puntas.forEachIndexed { j, p ->
            if (j % 2 == 1) return@forEachIndexed
            drawCircle(t.s(0.3f), 2.6f * u, p + Offset(0.8f * u, 1f * u))
            drawCircle(t.c(flor, -0.05f), 2.4f * u, p)
            drawCircle(t.b(0.4f), 0.9f * u, p - Offset(0.7f * u, 0.7f * u))
        }
        // El cordel al clavo de la moldura y el atado de bramante.
        drawLine(t.c(P.BRAMANTE, -0.1f), Offset(x, railY - 1f * u), atado, 0.9f * u)
        clavoBotanico(Offset(x, railY - 1.6f * u), 1.4f * u, t)
        val nudo = Rect(atado.x - w * 0.012f, atado.y - w * 0.004f, atado.x + w * 0.012f, atado.y + w * 0.016f)
        drawRect(t.c(P.BRAMANTE), nudo.topLeft, nudo.size)
        for (q in 0 until 3) drawLine(t.s(0.35f), Offset(nudo.left, nudo.top + nudo.height * (0.3f + 0.25f * q)), Offset(nudo.right, nudo.top + nudo.height * (0.3f + 0.25f * q)), 0.6f * u)
        drawRect(t.b(0.4f), nudo.topLeft, Size(nudo.width * 0.3f, nudo.height))
    }

    /**
     * La caja de insectos: el marco de nogal con su vidrio y el fondo de papel; dentro, cuatro piezas grandes en dos filas, que
     * alternan escarabajos y mariposas de alas abiertas (se leen a la primera), cada una con su alfiler, su sombra y su
     * etiquetita. Cuelga de dos ganchos de la moldura por sus cordones.
     */
    private fun DrawScope.cajaInsectos(r: Rect, s: SitioBotanico) {
        val w = size.width; val u = density
        val t = s.t(r.center)
        val railY = w * (MuroEstanteBotanico.MOLDURA_Y + MuroEstanteBotanico.MOLDURA_ALTO)
        // Los cordones a los ganchos de la moldura y los ganchos.
        listOf(r.left + r.width * 0.12f to r.left + r.width * 0.2f, r.right - r.width * 0.12f to r.right - r.width * 0.2f).forEach { (xb, xg) ->
            drawLine(t.s(0.25f), Offset(xg + 1.5f * u, railY + 1.5f * u), Offset(xb + 1.5f * u, r.top + 1.5f * u), 0.8f * u)
            drawLine(t.c(mezcla(P.BRAMANTE, P.NOGAL, 0.3f)), Offset(xg, railY), Offset(xb, r.top + 1f * u), 0.9f * u)
            drawArc(t.c(P.LATON), 0f, 180f, false, Offset(xg - 2.2f * u, railY - 3f * u), Size(4.4f * u, 4.4f * u), style = Stroke(1.2f * u))
            drawCircle(t.b(0.6f), 0.6f * u, Offset(xg - 1.2f * u, railY - 0.4f * u))
        }
        sombraEnParedBotanico(Path().apply { addRect(r) }, r.center, size.height, s, 0.26f)
        drawRect(t.c(P.NOGAL, 0.04f), r.topLeft, r.size)
        val m = r.width * 0.06f
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1.2f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(1.2f * u, r.height))
        drawRect(t.s(0.35f), Offset(r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        drawRect(t.s(0.4f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        vetas(Rect(r.left, r.top, r.right, r.top + m), t.c(P.NOGAL_OSC).copy(alpha = 0.4f), 9201, 0.9f, grosor = 0.5f)
        val f = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        // El fondo: papel sobre corcho, con la sombra del marco en él (el vidrio está más adelante).
        drawRect(t.c(mezcla(P.PAPEL, P.CARTULINA, 0.35f)), f.topLeft, f.size)
        drawRect(t.s(0.3f), f.topLeft, Size(f.width, f.height * 0.1f))
        drawRect(t.s(0.2f), f.topLeft, Size(f.width * 0.06f, f.height))
        pinceladas(f, if (s.a.oscuro) P.NOCHE else P.KRAFT, 9202, 6, u, 0.12f)
        // Cuatro piezas grandes en dos filas de dos, cruzadas: mariposa y escarabajo arriba, escarabajo y polilla abajo.
        val tipos = intArrayOf(1, 0, 0, 2)
        for (j in 0 until 4) {
            val col = j % 2; val fila = j / 2
            val cx = f.left + f.width * (if (col == 0) 0.28f else 0.72f)
            val cy = f.top + f.height * (if (fila == 0) 0.3f else 0.73f)
            val tt = s.t(Offset(cx, cy))
            val e = f.width * 0.145f
            when (tipos[j]) {
                0 -> escarabajo(Offset(cx, cy - e * 0.1f), e * (if (fila == 0) 0.8f else 0.72f), if (fila == 0) Color(0xFF2E4A2C) else Color(0xFF6A3A1E), tt)
                1 -> mariposa(Offset(cx, cy - e * 0.05f), e * 1.12f, Color(0xFFC9963A), Color(0xFF3A2A1E), tt)
                else -> mariposa(Offset(cx, cy - e * 0.05f), e * 1.02f, Color(0xFFD9CBA6), Color(0xFF7A6A52), tt)
            }
            // La etiquetita con su renglón.
            val ey = cy + e * 0.74f
            drawRect(tt.c(P.PAPEL), Offset(cx - e * 0.4f, ey), Size(e * 0.8f, e * 0.2f))
            drawLine(tt.c(P.TINTA).copy(alpha = 0.5f), Offset(cx - e * 0.3f, ey + e * 0.1f), Offset(cx + e * (0.05f + 0.2f * azar(9211, j)), ey + e * 0.1f), 0.5f * u)
        }
        // El vidrio: dos reflejos en diagonal y el filo de luz.
        drawPath(poligonoBotanico(f.left + f.width * 0.08f, f.bottom, f.left + f.width * 0.3f, f.top, f.left + f.width * 0.42f, f.top, f.left + f.width * 0.2f, f.bottom), t.b(0.16f))
        drawPath(poligonoBotanico(f.left + f.width * 0.5f, f.bottom, f.left + f.width * 0.62f, f.top, f.left + f.width * 0.66f, f.top, f.left + f.width * 0.54f, f.bottom), t.b(0.1f))
        drawRect(t.s(0.4f), Offset(f.left, f.top), Size(f.width, 1f * u), style = Stroke(0.6f * u))
    }

    /** Un escarabajo grande clavado, visto desde arriba: la cabeza, el tórax, los élitros con su raya y su brillo, y seis patas cortas y dobladas. */
    private fun DrawScope.escarabajo(c: Offset, e: Float, col: Color, t: TonoBotanico) {
        val u = density
        val pata = t.c(mezcla(col, P.TINTA, 0.55f))
        val patas = Path()
        for (lado in floatArrayOf(-1f, 1f)) for (q in 0 until 3) {
            val py = c.y - e * 0.05f + e * 0.28f * q
            val rodilla = Offset(c.x + lado * e * 0.52f, py + e * (0.06f * (q - 1)))
            val pie = Offset(c.x + lado * e * 0.68f, py + e * (0.22f * (q - 1)) + e * 0.08f)
            patas.moveTo(c.x + lado * e * 0.28f, py); patas.lineTo(rodilla.x, rodilla.y); patas.lineTo(pie.x, pie.y)
        }
        drawPath(Path().apply { addPath(patas, Offset(1f * u, 1.2f * u)) }, t.s(0.25f), style = Stroke(0.9f * u))
        drawPath(patas, pata, style = Stroke(0.9f * u, cap = StrokeCap.Round))
        drawOval(t.s(0.32f), Offset(c.x - e * 0.36f + 1.2f * u, c.y - e * 0.55f + 1.6f * u), Size(e * 0.72f, e * 1.25f))
        drawOval(t.c(mezcla(col, P.TINTA, 0.4f)), Offset(c.x - e * 0.15f, c.y - e * 0.62f), Size(e * 0.3f, e * 0.2f))
        drawOval(t.c(col, 0.02f), Offset(c.x - e * 0.27f, c.y - e * 0.46f), Size(e * 0.54f, e * 0.3f))
        drawOval(t.c(col), Offset(c.x - e * 0.36f, c.y - e * 0.2f), Size(e * 0.72f, e * 0.9f))
        drawLine(t.c(mezcla(col, P.TINTA, 0.6f)), Offset(c.x, c.y - e * 0.18f), Offset(c.x, c.y + e * 0.68f), 0.7f * u)
        drawOval(t.b(0.55f), Offset(c.x - e * 0.25f, c.y - e * 0.08f), Size(e * 0.16f, e * 0.42f))
        drawOval(t.s(0.25f), Offset(c.x + e * 0.08f, c.y - e * 0.05f), Size(e * 0.2f, e * 0.6f))
        // El alfiler, de cabeza de latón.
        drawCircle(t.c(P.LATON_LUZ), 1f * u, Offset(c.x + e * 0.08f, c.y - e * 0.3f))
    }

    /**
     * Una mariposa (o una polilla, más pálida) de alas abiertas, clavada: las alas de delante y las de detrás, con su borde más
     * oscuro y una hilera de puntos claros en el margen (sin ocelos), el cuerpo, las antenas con su botón y el alfiler.
     */
    private fun DrawScope.mariposa(c: Offset, e: Float, ala: Color, borde: Color, t: TonoBotanico) {
        val u = density
        fun alas(lado: Float): Pair<Path, Path> {
            val delante = Path().apply {
                moveTo(c.x + lado * e * 0.04f, c.y - e * 0.12f)
                cubicTo(c.x + lado * e * 0.3f, c.y - e * 0.62f, c.x + lado * e * 0.86f, c.y - e * 0.66f, c.x + lado * e * 0.9f, c.y - e * 0.44f)
                cubicTo(c.x + lado * e * 0.86f, c.y - e * 0.18f, c.x + lado * e * 0.5f, c.y + e * 0.02f, c.x + lado * e * 0.04f, c.y + e * 0.02f)
                close()
            }
            val detras = Path().apply {
                moveTo(c.x + lado * e * 0.04f, c.y + e * 0.0f)
                cubicTo(c.x + lado * e * 0.5f, c.y + e * 0.02f, c.x + lado * e * 0.66f, c.y + e * 0.3f, c.x + lado * e * 0.5f, c.y + e * 0.48f)
                cubicTo(c.x + lado * e * 0.32f, c.y + e * 0.62f, c.x + lado * e * 0.1f, c.y + e * 0.4f, c.x + lado * e * 0.04f, c.y + e * 0.18f)
                close()
            }
            return delante to detras
        }
        val (d0, t0) = alas(-1f); val (d1, t1) = alas(1f)
        val todo = Path().apply { addPath(d0); addPath(t0); addPath(d1); addPath(t1) }
        drawPath(Path().apply { addPath(todo, Offset(1.4f * u, 1.8f * u)) }, t.s(0.28f))
        val cAla = t.c(ala, 0.02f); val cOsc = t.c(mezcla(ala, borde, 0.35f))
        listOf(t0, t1).forEach { drawPath(it, cOsc) }
        listOf(d0, d1).forEach { drawPath(it, cAla) }
        // El borde oscuro del margen y la hilera de puntos claros, en las alas de delante.
        listOf(d0, d1).forEach { drawPath(it, t.c(borde).copy(alpha = 0.85f), style = Stroke(e * 0.09f)) }
        listOf(t0, t1).forEach { drawPath(it, t.c(borde).copy(alpha = 0.6f), style = Stroke(e * 0.06f)) }
        for (lado in floatArrayOf(-1f, 1f)) for (q in 0 until 3) {
            drawCircle(t.c(P.PAPEL, 0.02f).copy(alpha = 0.85f), e * 0.035f, Offset(c.x + lado * e * (0.62f + 0.09f * q), c.y - e * (0.52f - 0.1f * q)))
        }
        // La luz en las alas de delante (las de la izquierda reciben la luz) y las venas.
        drawPath(d0, t.b(0.25f))
        for (lado in floatArrayOf(-1f, 1f)) {
            drawLine(t.c(borde).copy(alpha = 0.45f), Offset(c.x + lado * e * 0.06f, c.y - e * 0.1f), Offset(c.x + lado * e * 0.7f, c.y - e * 0.5f), 0.5f * u)
            drawLine(t.c(borde).copy(alpha = 0.35f), Offset(c.x + lado * e * 0.06f, c.y - e * 0.04f), Offset(c.x + lado * e * 0.74f, c.y - e * 0.28f), 0.5f * u)
        }
        // El cuerpo, las antenas y el alfiler.
        drawOval(t.c(mezcla(borde, P.TINTA, 0.4f)), Offset(c.x - e * 0.06f, c.y - e * 0.2f), Size(e * 0.12f, e * 0.6f))
        drawOval(t.b(0.4f), Offset(c.x - e * 0.04f, c.y - e * 0.16f), Size(e * 0.03f, e * 0.36f))
        for (lado in floatArrayOf(-1f, 1f)) {
            val punta = Offset(c.x + lado * e * 0.2f, c.y - e * 0.52f)
            drawLine(t.c(borde), Offset(c.x, c.y - e * 0.18f), punta, 0.6f * u)
            drawCircle(t.c(borde), 1f * u, punta)
        }
        drawCircle(t.c(P.LATON_LUZ), 1f * u, Offset(c.x, c.y - e * 0.08f))
    }
}
