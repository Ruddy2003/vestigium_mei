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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El bodegón de la corona del estante de Botánico ([MuebleEstanteBotanico]), encima de la cornisa y en la pared: la caja de Ward
 * (la vitrina de vidrio en que el naturalista trajo vivos los helechos del viaje) manda; a su lado, la pila de folios de la flora
 * con el salacot tumbado encima (el del Ideario, que allí cuelga); el microscopio de latón sobre su caja; el quinqué de pared en
 * su brazo (la misma pantalla de vidrio verde que el del Ideario); colgada de la moldura, la caja de insectos con los
 * escarabajos clavados en filas. Cada cosa con su sombra en la pared: de día a la derecha y abajo; de noche, lejos del quinqué.
 */
internal object CoronaEstanteBotanico {
    private val P = PaletaBotanico

    /** La sombra de algo en la pared: de día, corrida a la derecha y abajo; de noche, hacia fuera del quinqué. No baja de [hasta]. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, s: SitioBotanico, a: Float = 0.2f) {
        val w = size.width
        clipRect(0f, 0f, size.width, hasta) {
            if (!s.a.oscuro) {
                val dx = if (s.a.noche) 0.04f else 0.024f
                translate(w * dx, w * (if (s.a.noche) 0.008f else 0.013f)) { drawPath(p, s.t(desde).s(a)) }
            } else {
                val k = s.k(desde)
                if (k > 0.1f) {
                    val dx = (desde.x / w - MuebleEstanteBotanico.LAMP_X).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.012f) { drawPath(p, P.NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    // ---------------------------------------------------------------- lo colgado (antes que la cornisa)

    /** Lo que cuelga de la pared: la caja de insectos y dos ramilletes de hierbas puestos a secar, de la moldura. */
    fun DrawScope.colgado(s: SitioBotanico, apoyo: Float) {
        val w = size.width
        ramillete(w * 0.075f, w * 0.135f, s, 0)
        ramillete(w * 0.335f, w * 0.115f, s, 1)
        cajaInsectos(Rect(w * 0.43f, w * 0.115f, w * 0.655f, w * 0.3f), s)
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
     * La caja de insectos: el marco de nogal con su vidrio, el fondo de papel, y los escarabajos clavados en tres filas, cada
     * uno con su alfiler y su etiquetita (sin letra); cuelga de dos ganchos de la moldura por sus cordones.
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
        val marco = Path().apply { addRect(r) }
        sombraEnPared(marco, r.center, size.height, s, 0.26f)
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
        // Los escarabajos: tres filas de cuatro, de cuerpo ovalado, con sus patas, su alfiler y su etiquetita.
        val colores = listOf(Color(0xFF2E4A2C), Color(0xFF6A3A1E), Color(0xFF1E2622), Color(0xFF4A5A2A), Color(0xFF7A5A22), Color(0xFF2C3A4A))
        for (fila in 0 until 3) for (col in 0 until 4) {
            val cx = f.left + f.width * (col + 0.5f) / 4f
            val cy = f.top + f.height * (fila + 0.42f) / 3f
            val j = fila * 4 + col
            val esc = (0.72f + 0.4f * azar(9210, j)) * f.width * 0.055f
            val c = colores[(j * 5 + fila) % colores.size]
            val tt = s.t(Offset(cx, cy))
            // Las patas: tres por lado, abiertas.
            for (lado in listOf(-1f, 1f)) for (q in 0 until 3) {
                val py = cy - esc * 0.25f + esc * 0.55f * q
                val ex = cx + lado * esc * (1.35f + 0.25f * (q % 2)); val ey = py + esc * (0.45f * (q - 1)) + esc * 0.15f
                drawLine(tt.c(mezcla(c, P.TINTA, 0.5f)), Offset(cx + lado * esc * 0.45f, py), Offset(ex, ey), 0.6f * u)
            }
            drawOval(tt.s(0.3f), Offset(cx - esc * 0.6f + 1f * u, cy - esc * 0.6f + 1.4f * u), Size(esc * 1.2f, esc * 1.9f))
            // La cabeza, el tórax y los élitros.
            drawOval(tt.c(mezcla(c, P.TINTA, 0.35f)), Offset(cx - esc * 0.25f, cy - esc * 0.95f), Size(esc * 0.5f, esc * 0.34f))
            drawOval(tt.c(c, 0.04f), Offset(cx - esc * 0.45f, cy - esc * 0.68f), Size(esc * 0.9f, esc * 0.5f))
            drawOval(tt.c(c), Offset(cx - esc * 0.6f, cy - esc * 0.3f), Size(esc * 1.2f, esc * 1.5f))
            drawLine(tt.c(mezcla(c, P.TINTA, 0.6f)), Offset(cx, cy - esc * 0.28f), Offset(cx, cy + esc * 1.15f), 0.5f * u)
            drawOval(tt.b(0.55f), Offset(cx - esc * 0.42f, cy - esc * 0.15f), Size(esc * 0.28f, esc * 0.7f))
            // El alfiler y la etiquetita con su renglón.
            drawCircle(tt.c(P.LATON_LUZ), 0.8f * u, Offset(cx + esc * 0.15f, cy - esc * 0.1f))
            val ey = cy + esc * 1.45f
            drawRect(tt.c(P.PAPEL), Offset(cx - esc * 0.7f, ey), Size(esc * 1.4f, esc * 0.42f))
            drawLine(tt.c(P.TINTA).copy(alpha = 0.5f), Offset(cx - esc * 0.5f, ey + esc * 0.22f), Offset(cx + esc * (0.2f + 0.3f * azar(9211, j)), ey + esc * 0.22f), 0.5f * u)
        }
        // El vidrio: dos reflejos en diagonal y el filo de luz.
        drawPath(poligonoBotanico(f.left + f.width * 0.08f, f.bottom, f.left + f.width * 0.3f, f.top, f.left + f.width * 0.42f, f.top, f.left + f.width * 0.2f, f.bottom), t.b(0.16f))
        drawPath(poligonoBotanico(f.left + f.width * 0.5f, f.bottom, f.left + f.width * 0.62f, f.top, f.left + f.width * 0.66f, f.top, f.left + f.width * 0.54f, f.bottom), t.b(0.1f))
        drawRect(t.s(0.4f), Offset(f.left, f.top), Size(f.width, 1f * u), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- encima de la cornisa

    /** El bodegón de encima de la cornisa ([apoyo]: donde pisan las cosas), de atrás adelante. */
    fun DrawScope.encima(s: SitioBotanico, apoyo: Float) {
        val w = size.width
        quinquePared(s, apoyo)
        microscopio(Offset(w * 0.69f, apoyo + w * 0.004f), s, apoyo)
        cajaWard(Rect(w * 0.035f, apoyo - w * 0.285f, w * 0.395f, apoyo), s, apoyo)
        folios(Offset(w * 0.49f, apoyo + w * 0.007f), s, apoyo)
        pliegosColgando(w * 0.03f, w * 0.19f, apoyo, s)
    }

    /**
     * Los pliegos de herbario sueltos en la punta de la cornisa: tres hojas de papel y secante que caen por el canto y se
     * curvan, una con su fronda prensada, sujetas con un amonites que hace de pisapapeles. Tapan la esquina de la cornisa y el
     * borde de la bandeja de la caja de Ward.
     */
    private fun DrawScope.pliegosColgando(x0: Float, x1: Float, apoyo: Float, s: SitioBotanico) {
        val w = size.width; val u = density
        val t = s.t(Offset((x0 + x1) / 2, apoyo))
        val cae = w * 0.04f
        val hojas = listOf(Triple(0f, 0.02f, mezcla(P.SECANTE, P.KRAFT, 0.3f)), Triple(0.06f, 0f, P.SECANTE), Triple(0.02f, 0.034f, mezcla(P.PAPEL, P.KRAFT, 0.2f)))
        hojas.forEachIndexed { i, (dx, dy, col) ->
            val a = x0 + (x1 - x0) * dx; val b = x1 - (x1 - x0) * (0.12f * i)
            val top = apoyo - w * 0.012f + dy * w * 0.1f
            val hoja = Path().apply {
                moveTo(a, top)
                lineTo(b, top - w * 0.002f)
                // El borde de delante cae por el canto y se curva hacia fuera.
                cubicTo(b + w * 0.006f, top + cae * 0.4f, b - w * 0.004f, top + cae * 0.8f, b - w * 0.012f, top + cae + w * 0.004f * i)
                lineTo(a + w * 0.01f, top + cae * (0.92f + 0.06f * i))
                cubicTo(a - w * 0.004f, top + cae * 0.7f, a + w * 0.002f, top + cae * 0.3f, a, top)
                close()
            }
            drawPath(Path().apply { addPath(hoja, Offset(w * 0.008f, w * 0.006f)) }, t.s(0.3f))
            drawPath(hoja, t.c(col, 0.02f))
            clipPath(hoja) {
                // La parte de arriba, sobre el tablero, vista en escorzo (más clara); la que cae, más honda abajo.
                drawRect(t.b(0.3f), Offset(a, top - w * 0.004f), Size(b - a, w * 0.012f))
                drawRect(t.s(0.14f), Offset(a, top + cae * 0.55f), Size(b - a, cae))
                if (i == 1) frondaBotanico(Offset(a + (b - a) * 0.3f, top + cae * 0.95f), cae * 1.05f, -70f, t.c(mezcla(P.HOJA, P.KRAFT, 0.4f)), 1f * u, 9)
                if (i == 2) {
                    val et = Rect(b - (b - a) * 0.42f, top + cae * 0.55f, b - (b - a) * 0.08f, top + cae * 0.82f)
                    drawRect(t.c(P.PAPEL), et.topLeft, et.size)
                    drawRect(t.c(P.TINTA).copy(alpha = 0.4f), et.topLeft, et.size, style = Stroke(0.5f * u))
                    for (q in 0 until 2) drawLine(t.c(P.TINTA).copy(alpha = 0.5f), Offset(et.left + et.width * 0.12f, et.top + et.height * (0.38f + 0.3f * q)), Offset(et.right - et.width * (0.2f + 0.2f * q), et.top + et.height * (0.38f + 0.3f * q)), 0.5f * u)
                }
            }
            drawPath(hoja, t.s(0.25f), style = Stroke(0.5f * u))
        }
        // El amonites que hace de pisapapeles: la espira con sus costillas, en su piedra.
        val c = Offset(x0 + (x1 - x0) * 0.42f, apoyo - w * 0.024f)
        val r = w * 0.032f
        drawOval(t.s(0.4f), Offset(c.x - r * 0.9f, apoyo - w * 0.012f), Size(r * 2.2f, w * 0.014f))
        drawCircle(t.c(mezcla(P.PIEDRA, P.KRAFT, 0.35f)), r, c)
        clipPath(Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }) {
            cilindroBotanico(Rect(c.x - r, c.y - r, c.x + r, c.y + r), t, -1f, 1.1f)
            for (j in 0 until 22) {
                val an = j * 0.2856f
                val rr = r * (1f - 0.22f * (j / 22f))
                drawLine(t.s(0.35f), Offset(c.x + rr * 0.55f * kotlin.math.cos(an), c.y + rr * 0.55f * kotlin.math.sin(an)), Offset(c.x + rr * kotlin.math.cos(an + 0.12f), c.y + rr * kotlin.math.sin(an + 0.12f)), 0.8f * u)
            }
        }
        drawCircle(t.s(0.35f), r * 0.55f, c, style = Stroke(1f * u))
        drawCircle(t.c(mezcla(P.PIEDRA, P.KRAFT, 0.2f), 0.05f), r * 0.42f, c)
        drawArc(t.s(0.4f), 200f, 250f, false, Offset(c.x - r * 0.3f, c.y - r * 0.28f), Size(r * 0.55f, r * 0.52f), style = Stroke(0.9f * u))
        drawArc(t.b(0.5f), 200f, 80f, false, Offset(c.x - r, c.y - r), Size(r * 2f, r * 2f), style = Stroke(1f * u))
    }

    /**
     * La caja de Ward: la bandeja de nogal con sus asas de latón, la caja de vidrio con sus montantes de nogal, el tejadillo a dos
     * aguas con su remate, el costado de la derecha (en sombra) y, dentro, la tierra con musgo, los helechos y una palmita; el
     * vidrio con su reflejo y las gotas de la condensación.
     */
    private fun DrawScope.cajaWard(r: Rect, s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val dv = Offset(w * 0.03f, -w * 0.018f)
        val bandeja = Rect(r.left - w * 0.008f, r.bottom - w * 0.042f, r.right + w * 0.008f, r.bottom)
        val cuerpo = Rect(r.left, r.top + r.height * 0.33f, r.right, bandeja.top)
        val cumbre = r.top
        val cx = r.center.x
        val t = s.t(r.center)
        val tejado = poligonoBotanico(cuerpo.left, cuerpo.top, cx, cumbre, cuerpo.right, cuerpo.top)
        val silueta = Path().apply {
            addPath(tejado); addRect(cuerpo); addRect(bandeja)
            addPath(poligonoBotanico(cuerpo.right, cuerpo.top, cuerpo.right + dv.x, cuerpo.top + dv.y, cuerpo.right + dv.x, cuerpo.bottom + dv.y, cuerpo.right, cuerpo.bottom))
        }
        sombraEnPared(silueta, r.center, apoyo + 2f * u, s, 0.2f)
        contactoBotanico(Offset(bandeja.center.x + w * 0.02f, apoyo), bandeja.width * 1.04f, w * 0.012f, t, 0.4f)
        // El costado de la derecha, de vidrio, visto en escorzo: lo de dentro se ve más apagado.
        val costado = poligonoBotanico(cuerpo.right, cuerpo.top, cuerpo.right + dv.x, cuerpo.top + dv.y, cuerpo.right + dv.x, cuerpo.bottom + dv.y, cuerpo.right, cuerpo.bottom)
        drawPath(costado, t.c(mezcla(P.SALVIA_OSC, P.HOJA, 0.4f), -0.1f))
        drawPath(costado, t.c(P.VIDRIO).copy(alpha = 0.16f))
        val agua = poligonoBotanico(cx, cumbre, cx + dv.x, cumbre + dv.y, cuerpo.right + dv.x, cuerpo.top + dv.y, cuerpo.right, cuerpo.top)
        drawPath(agua, t.c(mezcla(P.SALVIA, P.VIDRIO, 0.4f), -0.12f))
        // Dentro: el aire verde del vidrio, la tierra con musgo, los helechos y la palmita.
        val dentro = Path().apply { addPath(tejado); addRect(cuerpo) }
        clipPath(dentro) {
            drawRect(t.c(mezcla(P.SALVIA, P.HOJA, 0.28f), 0.02f), Offset(cuerpo.left, cumbre), Size(cuerpo.width, cuerpo.bottom - cumbre))
            drawRect(t.s(0.16f), Offset(cx, cumbre), Size(cuerpo.width / 2, cuerpo.bottom - cumbre))
            val suelo = cuerpo.bottom - cuerpo.height * 0.16f
            drawRect(t.c(mezcla(P.NOGAL, P.TINTA, 0.3f)), Offset(cuerpo.left, suelo), Size(cuerpo.width, cuerpo.bottom - suelo))
            // La palmita del fondo, a la derecha: sus hojas en abanico.
            val pb = Offset(cuerpo.left + cuerpo.width * 0.74f, suelo)
            for (j in 0 until 7) {
                val g = -150f + 20f * j
                frondaBotanico(pb + Offset(0f, -cuerpo.height * 0.25f), cuerpo.height * (0.42f + 0.1f * azar(9300, j)), g, t.c(mezcla(P.HOJA_OSC, P.HOJA, 0.4f), -0.05f), 1f * u, 7)
            }
            drawLine(t.c(mezcla(P.NOGAL, P.HOJA, 0.4f)), pb, pb + Offset(0f, -cuerpo.height * 0.26f), 2.4f * u)
            // Los helechos: tres grandes y uno todavía enrollado.
            val hb = Offset(cuerpo.left + cuerpo.width * 0.34f, suelo + 2f * u)
            frondaBotanico(hb, cuerpo.height * 1.05f, -98f, t.c(P.HOJA, 0.04f), 1.6f * u, 12)
            frondaBotanico(hb + Offset(cuerpo.width * 0.04f, 0f), cuerpo.height * 0.85f, -58f, t.c(mezcla(P.HOJA, P.HOJA_CLARA, 0.5f), 0.04f), 1.5f * u, 11)
            frondaBotanico(hb - Offset(cuerpo.width * 0.04f, 0f), cuerpo.height * 0.78f, -135f, t.c(mezcla(P.HOJA, P.VERDE, 0.4f)), 1.4f * u, 11)
            frondaBotanico(hb + Offset(cuerpo.width * 0.1f, 0f), cuerpo.height * 0.6f, -40f, t.c(P.HOJA_CLARA, 0.02f), 1.2f * u, 9)
            val bc = Offset(cuerpo.left + cuerpo.width * 0.52f, suelo - cuerpo.height * 0.3f)
            drawPath(Path().apply { moveTo(bc.x - cuerpo.width * 0.02f, suelo); cubicTo(bc.x - cuerpo.width * 0.03f, suelo - cuerpo.height * 0.15f, bc.x + cuerpo.width * 0.04f, bc.y + cuerpo.height * 0.05f, bc.x + cuerpo.width * 0.02f, bc.y) },
                t.c(P.HOJA_CLARA), style = Stroke(1.4f * u))
            drawCircle(t.c(P.HOJA_CLARA), cuerpo.width * 0.022f, bc + Offset(cuerpo.width * 0.005f, 0f), style = Stroke(1.3f * u))
            // El musgo en montones sobre la tierra.
            for (j in 0 until 14) {
                val mx = cuerpo.left + cuerpo.width * (0.03f + 0.94f * j / 13f)
                val mr = cuerpo.width * (0.025f + 0.018f * azar(9301, j))
                drawCircle(t.c(mezcla(P.MUSGO, P.HOJA_CLARA, 0.5f * azar(9302, j)), 0.02f), mr, Offset(mx, suelo + mr * 0.25f))
                drawCircle(t.b(0.25f), mr * 0.45f, Offset(mx - mr * 0.3f, suelo - mr * 0.2f))
            }
            // El vidrio: un velo muy leve, el reflejo largo y la condensación abajo.
            drawRect(t.c(P.VIDRIO).copy(alpha = if (s.a.oscuro) 0.06f else 0.12f), Offset(cuerpo.left, cumbre), Size(cuerpo.width, cuerpo.bottom - cumbre))
            drawPath(poligonoBotanico(cuerpo.left + cuerpo.width * 0.06f, cuerpo.bottom, cuerpo.left + cuerpo.width * 0.2f, cuerpo.top - r.height * 0.1f,
                cuerpo.left + cuerpo.width * 0.27f, cuerpo.top - r.height * 0.1f, cuerpo.left + cuerpo.width * 0.13f, cuerpo.bottom), t.b(0.2f))
            for (j in 0 until 34) {
                val p = Offset(cuerpo.left + cuerpo.width * azar(9303, j), cuerpo.bottom - cuerpo.height * 0.55f * azar(9304, j) * azar(9305, j))
                drawCircle(t.b(0.55f), (0.5f + 0.8f * azar(9306, j)) * u, p)
            }
        }
        // Los montantes de nogal: el marco, los de en medio, el alero y los del tejado.
        val marco = t.c(P.NOGAL, 0.05f)
        val g = 2.2f * u
        listOf(0f, 0.34f, 0.67f, 1f).forEach { f ->
            val x = cuerpo.left + cuerpo.width * f
            drawRect(t.s(0.3f), Offset(x - g / 2 + 1.2f * u, cuerpo.top), Size(g, cuerpo.height))
            drawRect(marco, Offset(x - g / 2, cuerpo.top), Size(g, cuerpo.height))
            drawRect(t.b(0.4f), Offset(x - g / 2, cuerpo.top), Size(0.7f * u, cuerpo.height))
        }
        drawLine(marco, Offset(cuerpo.left, cuerpo.top), Offset(cx, cumbre), g, StrokeCap.Round)
        drawLine(marco, Offset(cx, cumbre), Offset(cuerpo.right, cuerpo.top), g, StrokeCap.Round)
        drawLine(marco, Offset(cuerpo.left - 1f * u, cuerpo.top), Offset(cuerpo.right + 1f * u, cuerpo.top), g * 1.3f)
        drawLine(t.b(0.45f), Offset(cuerpo.left, cuerpo.top - g * 0.5f), Offset(cuerpo.right, cuerpo.top - g * 0.5f), 0.7f * u)
        drawLine(marco, Offset(cx, cumbre), Offset(cx + dv.x, cumbre + dv.y), g)
        drawLine(t.c(P.NOGAL, -0.1f), Offset(cuerpo.right + dv.x, cuerpo.top + dv.y), Offset(cuerpo.right + dv.x, cuerpo.bottom + dv.y), g * 0.8f)
        drawLine(t.c(P.NOGAL, -0.1f), Offset(cuerpo.right, cuerpo.top), Offset(cuerpo.right + dv.x, cuerpo.top + dv.y), g * 0.8f)
        // El remate de la cumbre: una bola de latón.
        clavoBotanico(Offset(cx, cumbre - 2.6f * u), 2.8f * u, t)
        // La bandeja de nogal con su moldura y las asas de latón.
        drawRect(t.c(P.NOGAL, 0.02f), bandeja.topLeft, bandeja.size)
        drawRect(t.c(mezcla(P.NOGAL, P.ROBLE, 0.4f), 0.06f), bandeja.topLeft, Size(bandeja.width, bandeja.height * 0.3f))
        drawRect(t.b(0.5f), bandeja.topLeft, Size(bandeja.width, 1f * u))
        drawRect(t.s(0.35f), Offset(bandeja.left, bandeja.top + bandeja.height * 0.3f), Size(bandeja.width, 1f * u))
        drawRect(t.s(0.25f), Offset(bandeja.right - bandeja.width * 0.18f, bandeja.top), Size(bandeja.width * 0.18f, bandeja.height))
        vetas(Rect(bandeja.left, bandeja.top + bandeja.height * 0.32f, bandeja.right, bandeja.bottom), t.c(P.NOGAL_OSC).copy(alpha = 0.45f), 9310, 0.8f, grosor = 0.5f)
        listOf(bandeja.left + bandeja.width * 0.12f, bandeja.right - bandeja.width * 0.2f).forEach { x ->
            val asa = Rect(x, bandeja.top + bandeja.height * 0.42f, x + bandeja.width * 0.08f, bandeja.top + bandeja.height * 0.9f)
            drawArc(t.s(0.35f), 0f, 180f, false, asa.topLeft + Offset(1f * u, 1.4f * u), asa.size, style = Stroke(1.6f * u))
            drawArc(t.c(P.LATON), 0f, 180f, false, asa.topLeft, asa.size, style = Stroke(1.6f * u))
            listOf(asa.left, asa.right).forEach { e -> clavoBotanico(Offset(e, asa.center.y), 1.4f * u, t) }
        }
    }

    /**
     * La pila de folios de la flora (tres tomos grandes tumbados, con el lomo hacia fuera, sus nervios y su tejuelo sin letra) y
     * encima el salacot de lienzo crudo, tumbado como se deja un sombrero: la copa baja, el ala, la banda y el botón.
     */
    private fun DrawScope.folios(pie: Offset, s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val tomos = listOf(Triple(0.21f, 0.034f, P.TELA), Triple(0.19f, 0.03f, P.CUERO), Triple(0.2f, 0.028f, mezcla(P.NOGAL, P.LACRE, 0.3f)))
        val corr = listOf(0f, -0.01f, 0.012f)
        var y = pie.y
        val rects = ArrayList<Rect>()
        tomos.forEachIndexed { i, (a, g, _) ->
            val cx = pie.x + w * corr[i]
            rects += Rect(cx - w * a / 2, y - w * g, cx + w * a / 2, y)
            y -= w * g
        }
        val todo = Path().apply { rects.forEach { addRect(it) } }
        sombraEnPared(todo, Offset(pie.x, (y + pie.y) / 2), apoyo + 2f * u, s, 0.22f)
        contactoBotanico(Offset(pie.x + w * 0.01f, pie.y), w * 0.22f, w * 0.012f, s.t(pie), 0.42f)
        rects.forEachIndexed { i, r ->
            val t = s.t(r.center)
            val col = tomos[i].third
            drawRect(t.c(col), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindroHorizontal(r, t) }
            // Los nervios del lomo, el tejuelo de otro color con su filete y las puntas gastadas.
            listOf(0.18f, 0.34f, 0.66f, 0.82f).forEach { f ->
                val x = r.left + r.width * f
                drawRect(t.s(0.35f), Offset(x + 0.8f * u, r.top), Size(1f * u, r.height))
                drawRect(t.c(col, 0.12f), Offset(x - 0.8f * u, r.top), Size(1.6f * u, r.height))
            }
            val tj = Rect(r.left + r.width * 0.4f, r.top + r.height * 0.2f, r.left + r.width * 0.6f, r.bottom - r.height * 0.2f)
            drawRect(t.c(if (i == 1) P.VERDE else P.LACRE), tj.topLeft, tj.size)
            drawRect(t.c(P.LATON).copy(alpha = 0.8f), tj.topLeft, tj.size, style = Stroke(0.6f * u))
            drawLine(t.c(P.LATON).copy(alpha = 0.7f), Offset(tj.left + tj.width * 0.2f, tj.center.y), Offset(tj.right - tj.width * 0.2f, tj.center.y), 0.6f * u)
            drawRect(t.c(mezcla(col, P.PAPEL, 0.4f)), Offset(r.left, r.top), Size(r.width * 0.025f, r.height))
            drawRect(t.s(0.4f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        }
        // Una tarjeta que asoma entre dos tomos.
        val r1 = rects[1]
        drawRect(s.t(r1.center).c(P.PAPEL), Offset(r1.right - w * 0.01f, r1.bottom - 2.6f * u), Size(w * 0.035f, 2.6f * u))
        salacot(Offset(pie.x + w * 0.005f, y), s, apoyo)
    }

    /** Un lomo redondo tumbado en [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.cilindroHorizontal(r: Rect, t: TonoBotanico) {
        drawRect(t.b(0.2f), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.32f), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.1f))
        drawRect(t.s(0.18f), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.24f), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El salacot tumbado sobre los folios ([base]: donde apoya el ala): el ala, la copa, la banda de tela y el botón. */
    private fun DrawScope.salacot(base: Offset, s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val sw = w * 0.22f
        val c = Offset(base.x, base.y - sw * 0.035f)
        val dh = sw * 0.36f
        val t = s.t(c)
        val hacia = if (s.a.oscuro && c.x / w < MuebleEstanteBotanico.LAMP_X) 1f else -1f
        val ala = Path().apply {
            moveTo(c.x - sw * 0.52f, c.y + sw * 0.02f)
            cubicTo(c.x - sw * 0.42f, c.y - sw * 0.1f, c.x + sw * 0.42f, c.y - sw * 0.1f, c.x + sw * 0.52f, c.y + sw * 0.02f)
            cubicTo(c.x + sw * 0.4f, c.y + sw * 0.09f, c.x - sw * 0.4f, c.y + sw * 0.09f, c.x - sw * 0.52f, c.y + sw * 0.02f)
            close()
        }
        val copa = Path().apply {
            moveTo(c.x - sw * 0.32f, c.y - sw * 0.02f)
            cubicTo(c.x - sw * 0.33f, c.y - dh * 1.25f, c.x + sw * 0.31f, c.y - dh * 1.28f, c.x + sw * 0.33f, c.y - sw * 0.02f)
            close()
        }
        val todo = Path().apply { addPath(ala); addPath(copa) }
        sombraEnPared(todo, c, apoyo + 2f * u, s, 0.22f)
        drawOval(t.s(0.35f), Offset(c.x - sw * 0.46f, c.y + sw * 0.03f), Size(sw * 0.95f, sw * 0.06f))
        // El ala, con su cara de abajo en sombra y el filo de luz.
        drawPath(ala, t.c(mezcla(P.CRUDO, P.KRAFT, 0.22f)))
        clipPath(ala) {
            drawRect(t.s(0.3f), Offset(c.x - sw / 2, c.y + sw * 0.03f), Size(sw, sw * 0.08f))
            drawRect(t.b(0.3f), Offset(c.x - sw / 2, c.y - sw * 0.08f), Size(sw, sw * 0.04f))
        }
        drawPath(copa, t.c(P.CRUDO, 0.02f))
        clipPath(copa) {
            cilindroBotanico(Rect(c.x - sw * 0.33f, c.y - dh * 1.1f, c.x + sw * 0.33f, c.y), t, hacia, 1.1f)
            listOf(-0.14f, 0.12f).forEach { f -> drawPath(Path().apply { moveTo(c.x + sw * f * 0.3f, c.y - dh * 0.95f); cubicTo(c.x + sw * f, c.y - dh * 0.7f, c.x + sw * f * 1.6f, c.y - dh * 0.3f, c.x + sw * f * 1.9f, c.y) }, t.s(0.24f), style = Stroke(0.6f * u)) }
            drawRect(t.c(mezcla(P.KRAFT, P.CUERO, 0.35f)), Offset(c.x - sw * 0.34f, c.y - dh * 0.4f), Size(sw * 0.68f, dh * 0.3f))
            for (j in 0 until 7) drawLine(t.s(0.3f), Offset(c.x - sw * 0.3f + sw * 0.09f * j, c.y - dh * 0.4f), Offset(c.x - sw * 0.27f + sw * 0.09f * j, c.y - dh * 0.1f), 0.6f * u)
            drawRect(t.b(0.3f), Offset(c.x - sw * 0.34f, c.y - dh * 0.4f), Size(sw * 0.68f, 0.8f * u))
            drawOval(t.c(mezcla(P.CRUDO, P.KRAFT, 0.5f)).copy(alpha = 0.55f), Offset(c.x - sw * 0.16f, c.y - dh * 0.85f), Size(sw * 0.12f, dh * 0.22f))
        }
        drawCircle(t.c(mezcla(P.CRUDO, P.KRAFT, 0.3f)), sw * 0.03f, Offset(c.x, c.y - dh * 1.03f))
        drawCircle(t.b(0.5f), sw * 0.012f, Offset(c.x - sw * 0.01f, c.y - dh * 1.06f))
    }

    /**
     * El microscopio de latón (de tubo, como los de los años treinta del siglo XIX) sobre la tapa de su caja de caoba: la
     * columna, el brazo, el tubo con su ocular y su objetivo, la platina, el espejo y el tornillo de enfoque.
     */
    private fun DrawScope.microscopio(pie: Offset, s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val t = s.t(Offset(pie.x, pie.y - w * 0.12f))
        val hacia = if (s.a.oscuro) (if (pie.x / w < MuebleEstanteBotanico.LAMP_X) 1f else -1f) else -1f
        val caja = Rect(pie.x - w * 0.075f, pie.y - w * 0.042f, pie.x + w * 0.075f, pie.y)
        val col = Rect(pie.x - w * 0.008f, caja.top - w * 0.12f, pie.x + w * 0.008f, caja.top)
        val pieza = Rect(pie.x - w * 0.03f, caja.top - w * 0.012f, pie.x + w * 0.03f, caja.top)
        val platina = Rect(pie.x - w * 0.04f, col.top + w * 0.035f, pie.x + w * 0.04f, col.top + w * 0.044f)
        val tuboA = Offset(pie.x + w * 0.012f, platina.top - w * 0.014f)
        val tuboB = Offset(pie.x - w * 0.012f, platina.top - w * 0.15f)
        val silueta = Path().apply {
            addRect(caja); addRect(col); addRect(pieza); addRect(platina)
            addPath(poligonoBotanico(tuboA.x - w * 0.013f, tuboA.y, tuboA.x + w * 0.013f, tuboA.y, tuboB.x + w * 0.015f, tuboB.y, tuboB.x - w * 0.015f, tuboB.y))
        }
        if (!s.a.oscuro) sombraEnPared(silueta, Offset(pie.x, pie.y - w * 0.1f), apoyo + 2f * u, s, 0.18f)
        contactoBotanico(Offset(pie.x + w * 0.01f, pie.y), caja.width * 1.1f, w * 0.012f, s.t(pie), 0.42f)
        // La caja de caoba con su tapa, su bocallave y su asa.
        val tc = s.t(caja.center)
        drawRect(tc.c(mezcla(P.NOGAL, P.LACRE, 0.25f), 0.05f), caja.topLeft, caja.size)
        drawRect(tc.c(mezcla(P.NOGAL, P.LACRE, 0.25f), 0.2f), caja.topLeft, Size(caja.width, caja.height * 0.28f))
        drawRect(tc.s(0.4f), Offset(caja.left, caja.top + caja.height * 0.28f), Size(caja.width, 1f * u))
        drawRect(tc.b(0.5f), caja.topLeft, Size(caja.width, 0.9f * u))
        drawRect(tc.s(0.28f), Offset(caja.right - caja.width * 0.2f, caja.top), Size(caja.width * 0.2f, caja.height))
        vetas(Rect(caja.left, caja.top + caja.height * 0.3f, caja.right, caja.bottom), tc.c(P.NOGAL_OSC).copy(alpha = 0.4f), 9401, 0.8f, grosor = 0.5f)
        val bl = Offset(caja.center.x, caja.top + caja.height * 0.62f)
        drawCircle(tc.c(P.LATON), w * 0.006f, bl)
        drawRect(tc.c(P.TINTA), Offset(bl.x - 0.5f * u, bl.y - 0.4f * u), Size(1f * u, w * 0.007f))
        // El pie de latón y la columna.
        val laton = t.c(P.LATON)
        drawRect(laton, pieza.topLeft, pieza.size)
        clipRect(pieza.left, pieza.top, pieza.right, pieza.bottom) { cilindroBotanico(pieza, t, hacia, 1.3f) }
        drawRect(laton, col.topLeft, col.size)
        clipRect(col.left, col.top, col.right, col.bottom) { cilindroBotanico(col, t, hacia, 1.4f) }
        // El espejo, redondo, colgado de su horquilla bajo la platina.
        val esp = Offset(pie.x - w * 0.003f, platina.bottom + w * 0.04f)
        drawLine(laton, Offset(pie.x, platina.bottom + w * 0.012f), esp, 1.4f * u)
        drawOval(t.c(P.LATON_OSC), Rect(esp.x - w * 0.02f, esp.y - w * 0.008f, esp.x + w * 0.02f, esp.y + w * 0.008f).topLeft, Size(w * 0.04f, w * 0.016f))
        drawOval(t.c(mezcla(P.VIDRIO, P.SALVIA, 0.3f), 0.1f), Offset(esp.x - w * 0.016f, esp.y - w * 0.006f), Size(w * 0.032f, w * 0.012f))
        destelloBotanico(Offset(esp.x - w * 0.007f, esp.y - w * 0.002f), 1f * u, t)
        // La platina.
        drawRect(t.c(P.LATON_OSC), platina.topLeft + Offset(0f, platina.height * 0.5f), Size(platina.width, platina.height * 0.5f))
        drawRect(laton, platina.topLeft, Size(platina.width, platina.height * 0.55f))
        drawRect(t.b(0.6f), platina.topLeft, Size(platina.width, 0.8f * u))
        // El tubo, un poco inclinado, con sus anillos, el ocular arriba y el objetivo abajo.
        val dir = Offset(tuboB.x - tuboA.x, tuboB.y - tuboA.y)
        val grados = Math.toDegrees(kotlin.math.atan2(dir.x.toDouble(), -dir.y.toDouble())).toFloat()
        val largo = kotlin.math.sqrt(dir.x * dir.x + dir.y * dir.y)
        rotate(grados, tuboA) {
            val tubo = Rect(tuboA.x - w * 0.013f, tuboA.y - largo, tuboA.x + w * 0.013f, tuboA.y)
            drawRect(laton, tubo.topLeft, tubo.size)
            clipRect(tubo.left, tubo.top, tubo.right, tubo.bottom) { cilindroBotanico(tubo, t, hacia, 1.5f) }
            listOf(0.25f, 0.55f).forEach { f ->
                val y = tubo.top + tubo.height * f
                drawRect(t.c(P.LATON_OSC), Offset(tubo.left - 1f * u, y), Size(tubo.width + 2f * u, w * 0.006f))
                drawRect(t.b(0.5f), Offset(tubo.left - 1f * u, y), Size(tubo.width + 2f * u, 0.8f * u))
            }
            val ocu = Rect(tubo.left - w * 0.003f, tubo.top - w * 0.02f, tubo.right + w * 0.003f, tubo.top)
            drawRect(t.c(P.TINTA, 0.12f), ocu.topLeft, ocu.size)
            drawRect(t.b(0.3f), ocu.topLeft, Size(ocu.width * 0.3f, ocu.height))
            val obj = Rect(tubo.left + w * 0.005f, tubo.bottom, tubo.right - w * 0.005f, tubo.bottom + w * 0.014f)
            drawRect(laton, obj.topLeft, obj.size)
            clipRect(obj.left, obj.top, obj.right, obj.bottom) { cilindroBotanico(obj, t, hacia, 1.3f) }
            // El brazo que lo sostiene, del lado de la columna, y el tornillo de enfoque.
            drawRect(t.c(P.LATON_OSC), Offset(tubo.right, tubo.top + tubo.height * 0.4f), Size(w * 0.012f, tubo.height * 0.42f))
            val torn = Offset(tubo.right + w * 0.016f, tubo.top + tubo.height * 0.45f)
            drawCircle(t.c(P.LATON), w * 0.011f, torn)
            for (j in 0 until 6) drawLine(t.s(0.35f), Offset(torn.x - w * 0.011f, torn.y - w * 0.008f + w * 0.0032f * j), Offset(torn.x + w * 0.011f, torn.y - w * 0.008f + w * 0.0032f * j), 0.5f * u)
            drawCircle(t.b(0.5f), w * 0.004f, torn - Offset(w * 0.004f, w * 0.004f))
            destelloBotanico(Offset(tubo.left + tubo.width * 0.3f, tubo.top + tubo.height * 0.2f), 1.2f * u, t)
        }
    }

    /**
     * El quinqué de pared (de la misma clase que el del Ideario, con la misma pantalla de vidrio verde): la roseta clavada en la
     * pared, el brazo de latón en ese, el depósito, el mechero con su llave, el tubo de vidrio y la pantalla. De día, apagado;
     * de noche, la llama bajo la pantalla y el vidrio verde encendido.
     */
    private fun DrawScope.quinquePared(s: SitioBotanico, apoyo: Float) {
        val w = size.width; val u = density
        val o = s.a.oscuro
        val lx = MuebleEstanteBotanico.LAMP_X * w
        val ly = MuebleEstanteBotanico.LAMP_Y * w
        val t = s.t(Offset(lx, ly + w * 0.03f))
        val placa = Offset(w * 0.958f, ly + w * 0.045f)
        val mech = Rect(lx - w * 0.012f, ly + w * 0.008f, lx + w * 0.012f, ly + w * 0.024f)
        val dep = Rect(lx - w * 0.032f, mech.bottom - w * 0.002f, lx + w * 0.032f, mech.bottom + w * 0.05f)
        val tubo = Rect(lx - w * 0.009f, ly - w * 0.13f, lx + w * 0.009f, mech.top)
        val pantB = ly + w * 0.004f
        val pant = Path().apply {
            moveTo(lx - w * 0.021f, pantB - w * 0.062f); lineTo(lx + w * 0.021f, pantB - w * 0.062f)
            cubicTo(lx + w * 0.036f, pantB - w * 0.046f, lx + w * 0.06f, pantB - w * 0.016f, lx + w * 0.066f, pantB)
            lineTo(lx - w * 0.066f, pantB)
            cubicTo(lx - w * 0.06f, pantB - w * 0.016f, lx - w * 0.036f, pantB - w * 0.046f, lx - w * 0.021f, pantB - w * 0.062f)
            close()
        }
        val brazo = Path().apply {
            moveTo(placa.x, placa.y)
            cubicTo(placa.x - w * 0.03f, placa.y + w * 0.03f, dep.right + w * 0.035f, dep.center.y - w * 0.035f, dep.right - w * 0.004f, dep.center.y)
        }
        val todo = Path().apply { addRect(dep); addRect(tubo); addPath(pant); addRect(mech) }
        if (!o) {
            sombraEnPared(todo, Offset(lx, ly), apoyo, s, 0.16f)
            translate(w * 0.02f, w * 0.012f) { drawPath(brazo, t.s(0.22f), style = Stroke(2.6f * u)) }
        }
        // La roseta de la pared y el brazo.
        drawCircle(t.s(0.35f), w * 0.022f, placa + Offset(1.4f * u, 1.8f * u))
        drawCircle(t.c(P.LATON_OSC), w * 0.022f, placa)
        drawCircle(t.c(P.LATON), w * 0.017f, placa)
        drawCircle(t.b(0.5f), w * 0.006f, placa - Offset(w * 0.006f, w * 0.006f))
        drawPath(brazo, t.c(P.LATON_OSC), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(brazo, t.c(P.LATON), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(brazo, t.b(0.5f), style = Stroke(0.6f * u))
        // El depósito, redondo, con su remate colgante.
        val laton = t.c(P.LATON)
        drawOval(laton, dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            cilindroBotanico(dep, t, if (o) -1f else -1f, 1.5f)
            drawRect(t.s(0.22f), Offset(dep.left, dep.center.y + dep.height * 0.15f), Size(dep.width, dep.height))
            drawLine(t.s(0.3f), Offset(dep.left, dep.center.y), Offset(dep.right, dep.center.y), 0.7f * u)
        }
        drawPath(poligonoBotanico(lx - w * 0.008f, dep.bottom - 1f, lx + w * 0.008f, dep.bottom - 1f, lx, dep.bottom + w * 0.024f), laton)
        drawCircle(laton, w * 0.006f, Offset(lx, dep.bottom + w * 0.026f))
        // El mechero, con su llave.
        drawRect(laton, mech.topLeft, mech.size)
        clipRect(mech.left, mech.top, mech.right, mech.bottom) { cilindroBotanico(mech, t, -1f, 1.4f) }
        drawCircle(laton, w * 0.006f, Offset(mech.left - w * 0.006f, mech.center.y))
        // El tubo de vidrio.
        drawRect(t.c(P.VIDRIO).copy(alpha = 0.3f), tubo.topLeft, tubo.size)
        drawLine(if (o) P.LLAMA.copy(alpha = 0.6f) else t.b(0.7f), Offset(tubo.left + tubo.width * 0.25f, tubo.top + 2f * u), Offset(tubo.left + tubo.width * 0.25f, tubo.bottom - 2f * u), 0.9f * u)
        drawRect(t.s(0.2f), Offset(tubo.right - tubo.width * 0.25f, tubo.top), Size(tubo.width * 0.25f, tubo.height))
        if (o) {
            // La llama, dentro del tubo, bajo la pantalla.
            val lc = Offset(lx, ly)
            val f = if (s.a.noche) 0.7f else 1f
            drawCircle(P.LLAMA.copy(alpha = 0.35f * f), w * 0.032f, lc)
            drawOval(P.LLAMA, Offset(lc.x - w * 0.006f, lc.y - w * 0.014f), Size(w * 0.012f, w * 0.022f))
            drawOval(P.FUEGO, Offset(lc.x - w * 0.003f, lc.y - w * 0.008f), Size(w * 0.006f, w * 0.012f))
        }
        // La pantalla de vidrio verde, con su borde y el aro de latón de arriba.
        drawPath(pant, if (o) mezcla(P.VERDE, P.VIDRIO_VERDE, if (s.a.noche) 0.4f else 0.55f) else t.c(P.VERDE))
        clipPath(pant) {
            if (o) {
                drawRect(P.VIDRIO_VERDE.copy(alpha = 0.5f), Offset(lx - w * 0.042f, pantB - w * 0.062f), Size(w * 0.05f, w * 0.062f))
                drawRect(P.NOCHE.copy(alpha = 0.25f), Offset(lx + w * 0.026f, pantB - w * 0.062f), Size(w * 0.042f, w * 0.062f))
            } else {
                drawRect(t.b(0.32f), Offset(lx - w * 0.052f, pantB - w * 0.062f), Size(w * 0.026f, w * 0.062f))
                drawRect(t.b(0.5f), Offset(lx - w * 0.04f, pantB - w * 0.062f), Size(w * 0.007f, w * 0.062f))
                drawRect(t.s(0.26f), Offset(lx + w * 0.02f, pantB - w * 0.062f), Size(w * 0.046f, w * 0.062f))
            }
        }
        drawOval(if (o) P.FUEGO else t.c(mezcla(P.PAPEL, P.SALVIA, 0.25f)), Offset(lx - w * 0.066f, pantB - w * 0.006f), Size(w * 0.132f, w * 0.012f))
        drawArc(laton, 0f, 180f, false, Offset(lx - w * 0.066f, pantB - w * 0.006f), Size(w * 0.132f, w * 0.012f), style = Stroke(1.2f * u))
        drawRect(laton, Offset(lx - w * 0.022f, pantB - w * 0.066f), Size(w * 0.044f, w * 0.006f))
        if (o) drawCircle(P.LLAMA.copy(alpha = 0.3f), w * 0.008f, Offset(lx - w * 0.012f, dep.top + dep.height * 0.3f))
        else drawCircle(LUZ_MUEBLE.copy(alpha = 0.7f), w * 0.005f, Offset(lx - w * 0.014f, dep.top + dep.height * 0.28f))
    }
}
