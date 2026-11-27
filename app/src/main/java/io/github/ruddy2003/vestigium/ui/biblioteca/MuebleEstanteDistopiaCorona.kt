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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Lo de arriba del estante, encima de la cornisa (se pinta una vez, en la corona guardada): detrás, los dos paquetes de papel en
 * blanco envueltos en papel de embalar y el fajo de copias atado; delante, la multicopista de tambor de perfil (la pieza que
 * manda), la lata de tinta abierta con su tapa apoyada, la taza de esmalte con los lápices y el flexo estirado hacia la primera
 * repisa.
 */
internal object CoronaEstanteDistopia {
    /** La sombra de [p] en la pared: de día, a la derecha y abajo (el tubo está arriba a la izquierda); de noche, lejos del flexo. */
    private fun DrawScope.sombraEnPared(g: MarcoDistopia, p: Path, x: Float, y: Float, a: Float = 0.24f, largo: Float = 1f) {
        val w = g.w
        val dd = when {
            g.a.oscuro -> {
                val v = Offset(x, y) - g.flexo; val l = v.getDistance().coerceAtLeast(1f)
                v / l * (0.024f * w * largo)
            }
            g.a.noche -> Offset(0.022f, 0.006f) * (w * largo)
            else -> Offset(0.014f, 0.011f) * (w * largo)
        }
        val alfa = if (g.a.oscuro) a * 1.2f * g.luz(x, y).k + 0.1f else a
        translate(dd.x, dd.y) { drawPath(p, g.a.sombra(alfa)) }
    }

    fun DrawScope.detras(g: MarcoDistopia) {
        paquetes(g)
        copias(g)
    }

    fun DrawScope.delante(g: MarcoDistopia) {
        multicopista(g)
        lata(g)
        taza(g)
        flexo(g)
    }

    /** Un punto en anchos, en píxeles. */
    private fun MarcoDistopia.p(x: Float, y: Float) = Offset(x * w, y * w)

    // ---------------------------------------------------------------- los paquetes de papel

    /** Dos paquetes de papel en blanco envueltos en papel de embalar, uno sobre otro y algo corridos, atados con bramante. */
    private fun DrawScope.paquetes(g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cajas = listOf(Rect(0.012f * w, pie - 0.058f * w, 0.19f * w, pie), Rect(0.03f * w, pie - 0.108f * w, 0.176f * w, pie - 0.058f * w))
        val silueta = Path().apply { cajas.forEach { addRect(it) } }
        sombraEnPared(g, silueta, 0.1f * w, pie - 0.05f * w)
        cajas.forEachIndexed { i, r ->
            val l = g.luz(r.center.x, r.center.y, 0.03f)
            val hacia = -1f
            conRejaDistopia(g, 0.03f, r) { s ->
                val ls = if (s == null) l else g.luz(r.center.x, r.center.y, 0.03f, s)
                planosDistopia(Path().apply { addRect(r) }, ls.c(mezcla(P.KRAFT, P.CARTON, 0.3f * i)), ls, hacia, 0.16f, 0.24f)
            }
            // Los pliegues del envoltorio en las puntas: dos triángulos de papel doblado.
            drawPath(poligonoDistopia(Offset(r.left, r.top), Offset(r.left + r.height * 0.5f, r.center.y), Offset(r.left, r.bottom)), l.s(0.18f))
            drawPath(poligonoDistopia(Offset(r.right, r.top), Offset(r.right - r.height * 0.5f, r.center.y), Offset(r.right, r.bottom)), l.s(0.25f))
            drawLine(l.b(0.5f), Offset(r.left, r.top + 0.8f * u), Offset(r.right, r.top + 0.8f * u), 1f * u)
            drawLine(l.s(0.4f), Offset(r.left, r.bottom - 0.6f * u), Offset(r.right, r.bottom - 0.6f * u), 1.2f * u)
            // El bramante: una vuelta a lo alto, corrida del centro.
            val xb = r.left + r.width * (0.38f + 0.12f * i)
            drawLine(l.s(0.35f), Offset(xb + 1.2f * u, r.top), Offset(xb + 1.2f * u, r.bottom), 1.4f * u)
            drawLine(l.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), Offset(xb, r.top), Offset(xb, r.bottom), 1.4f * u)
            // Un desgarrón en el envoltorio del de abajo, que deja ver los cantos de las hojas.
            if (i == 0) {
                val d = Rect(r.left + r.width * 0.6f, r.top + r.height * 0.3f, r.left + r.width * 0.86f, r.top + r.height * 0.75f)
                drawPath(escamaDistopia(d.center, d.width / 2, d.height / 2, 6400, 8), l.c(P.HUESO))
                for (j in 0 until 4) drawLine(l.s(0.22f), Offset(d.left + 2f * u, d.top + d.height * (0.25f + 0.17f * j)), Offset(d.right - 2f * u, d.top + d.height * (0.25f + 0.17f * j)), 0.6f * u)
            }
        }
    }

    /** El fajo de copias recién tiradas, atado en cruz con bramante, a la derecha, junto al flexo. */
    private fun DrawScope.copias(g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val r = Rect(0.69f * w, pie - 0.038f * w, 0.818f * w, pie)
        val l = g.luz(r.center.x, r.center.y, 0.03f)
        sombraEnPared(g, Path().apply { addRect(r) }, r.center.x, r.top, 0.2f)
        // Las hojas: el canto del fajo, en capas un poco desiguales.
        drawRect(l.c(P.HUESO), r.topLeft, r.size)
        for (j in 0 until 7) {
            val y = r.top + r.height * (0.12f + 0.12f * j)
            val dx = (azar(6410, j) - 0.5f) * 0.006f * w
            drawLine(l.s(0.16f), Offset(r.left + dx, y), Offset(r.right + dx, y), 0.6f * u)
        }
        drawRect(l.s(0.25f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        // La hoja de arriba, a la vista, con los renglones de la copia (que no se leen).
        val cara = poligonoDistopia(Offset(r.left + 0.006f * w, r.top - 0.012f * w), Offset(r.right - 0.004f * w, r.top - 0.012f * w), Offset(r.right, r.top), Offset(r.left, r.top))
        drawPath(cara, l.c(mezcla(P.HUESO, P.LUZ_TUBO, 0.3f)))
        drawPath(renglonesDistopia(Rect(r.left + 0.016f * w, r.top - 0.01f * w, r.right - 0.012f * w, r.top - 0.002f * w), 0.0012f * w, 0.0032f * w, 6411), l.c(mezcla(P.BAQUELITA, P.FONDO, 0.4f)).copy(alpha = 0.5f))
        // El bramante en cruz y su nudo.
        val xb = r.left + r.width * 0.56f
        drawLine(l.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), Offset(xb, r.top - 0.008f * w), Offset(xb, r.bottom), 1.3f * u)
        drawLine(l.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1.3f * u)
        drawCircle(l.c(mezcla(P.CARPETA, P.KRAFT, 0.4f)), 2.2f * u, Offset(xb, r.center.y))
    }

    // ---------------------------------------------------------------- la multicopista

    /**
     * La multicopista de tambor, de perfil: el cuerpo de esmalte oliva oscuro con sus patas de goma, el bastidor redondeado del
     * tambor con la tapa de acero y su cubo, la manivela con el pomo de madera, la bandeja de entrada inclinada con el papel en
     * blanco y la de salida con las copias; la palanca de presión y la pintura saltada en los cantos. Se dibuja en unas medidas
     * propias ([K] veces y corrida [DX]) para que mande en la corona.
     */
    private fun DrawScope.multicopista(g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = MuebleEstanteDistopia.APOYO
        val K = 1.18f; val DX = -0.035f; val ox = 0.355f
        fun P(x: Float, y: Float) = Offset((ox + (x - ox) * K + DX) * w, (pie + (y - pie) * K) * w)
        fun S(v: Float) = v * K * w
        val cx = 0.355f; val cy = 0.362f; val rt = 0.07f
        val hacia = if (g.a.oscuro && !g.a.noche) 1f else -1f
        // La silueta para la sombra en la pared.
        val silueta = Path().apply {
            addOval(Rect(P(cx, cy), S(rt * 1.12f)))
            addPath(poligonoDistopia(listOf(P(0.19f, pie), P(0.52f, pie), P(0.515f, 0.42f), P(0.2f, 0.42f))))
            addPath(poligonoDistopia(listOf(P(0.135f, 0.335f), P(0.15f, 0.322f), P(0.29f, 0.41f), P(0.27f, 0.42f))))
        }
        sombraEnPared(g, silueta, P(cx, cy).x, P(cx, cy).y, 0.28f)
        drawOval(g.a.sombra(if (g.a.oscuro) 0.6f else 0.42f), P(0.18f, pie - 0.006f), Size(S(0.36f), S(0.012f)))
        conRejaDistopia(g, 0.05f, Rect(P(0.12f, 0.27f), P(0.63f, pie))) { s ->
            fun l(x: Float, y: Float) = g.luz(P(x, y), 0.05f, s)
            val lc = l(cx, 0.44f)
            // La bandeja de entrada, detrás del tambor, con el papel en blanco.
            val bandeja = poligonoDistopia(P(0.128f, 0.33f), P(0.142f, 0.318f), P(0.3f, 0.412f), P(0.284f, 0.424f))
            drawPath(bandeja, l(0.2f, 0.37f).c(P.ACERO))
            drawLine(l(0.2f, 0.37f).b(0.6f), P(0.142f, 0.318f), P(0.3f, 0.412f), 1f * u)
            val papel = poligonoDistopia(P(0.142f, 0.306f), P(0.156f, 0.294f), P(0.28f, 0.372f), P(0.268f, 0.386f))
            drawPath(papel, l(0.2f, 0.34f).c(P.HUESO))
            for (j in 0 until 4) drawLine(l(0.2f, 0.34f).s(0.2f), P(0.145f + j * 0.003f, 0.31f - j * 0.003f), P(0.27f + j * 0.003f, 0.385f - j * 0.003f), 0.6f * u)
            drawLine(l(0.2f, 0.34f).b(0.6f), P(0.156f, 0.294f), P(0.28f, 0.372f), 0.9f * u)
            // La guía lateral de la bandeja.
            drawPath(poligonoDistopia(P(0.17f, 0.332f), P(0.18f, 0.318f), P(0.2f, 0.33f), P(0.19f, 0.346f)), l(0.18f, 0.335f).c(P.ACERO_OSC))
            // El cuerpo de esmalte, con su costado derecho en sombra.
            val cuerpo = poligonoDistopia(listOf(P(0.19f, pie - 0.006f), P(0.522f, pie - 0.006f), P(0.515f, 0.43f), P(0.47f, 0.408f), P(0.25f, 0.408f), P(0.2f, 0.432f)))
            planosDistopia(cuerpo, lc.c(P.ESMALTE), lc, hacia, 0.12f, 0.2f)
            drawLine(lc.b(0.7f), P(0.25f, 0.409f), P(0.47f, 0.409f), 1.2f * u)
            drawLine(lc.b(0.4f), P(0.2f, 0.432f), P(0.25f, 0.408f), 1f * u)
            // El panel del costado del cuerpo, rehundido, con sus cuatro tornillos.
            val panel = Rect(P(0.29f, 0.44f), P(0.47f, 0.478f))
            drawRect(lc.s(0.22f), panel.topLeft, Size(panel.width, 1.2f * u))
            drawRect(lc.s(0.16f), panel.topLeft, Size(1.2f * u, panel.height))
            drawRect(lc.b(0.3f), Offset(panel.left, panel.bottom - 1f * u), Size(panel.width, 1f * u))
            listOf(panel.topLeft, Offset(panel.right, panel.top), Offset(panel.left, panel.bottom), panel.bottomRight).forEach { p ->
                drawCircle(lc.c(P.HIERRO), 1.1f * u, p + Offset(if (p.x < panel.center.x) 3f * u else -3f * u, if (p.y < panel.center.y) 3f * u else -3f * u))
            }
            // La moldura del zócalo del cuerpo y las patas de goma.
            drawRect(lc.c(mezcla(P.ESMALTE, P.BAQUELITA, 0.5f)), P(0.19f, pie - 0.016f), Size(S(0.332f), S(0.01f)))
            drawLine(lc.b(0.3f), P(0.19f, pie - 0.016f), P(0.522f, pie - 0.016f), 0.8f * u)
            listOf(0.2f, 0.49f).forEach { x -> drawRect(lc.c(P.BAQUELITA), P(x, pie - 0.007f), Size(S(0.024f), S(0.007f))) }
            // El bastidor del tambor: una tapa redondeada de esmalte.
            val lt = l(cx, cy)
            val bastidor = Path().apply { addOval(Rect(P(cx, cy), S(rt))) }
            planosDistopia(bastidor, lt.c(P.ESMALTE), lt, hacia, 0.2f, 0.26f)
            drawArc(lt.b(0.6f), 200f, 70f, false, P(cx - rt, cy - rt), Size(S(rt * 2), S(rt * 2)), style = Stroke(1.2f * u))
            // El volante del tambor, de fundición: la llanta, seis radios finos y parejos (girados: ninguno de pie ni tumbado, para
            // que no formen una cruz) y el cubo de acero; entre los radios, el hueco oscuro.
            val c0 = P(cx, cy)
            drawCircle(mezcla(lt.c(P.ESMALTE), ParedEstanteDistopia.honda(g), 0.55f), S(rt * 0.7f), c0)
            val radios = Path()
            for (i in 0 until 6) {
                val a = (i * 60f + 15f) * 3.1416f / 180f
                val dir = Offset(cos(a), sin(a)); val n = Offset(-dir.y, dir.x)
                val p0 = c0 + dir * S(rt * 0.16f); val p1 = c0 + dir * S(rt * 0.64f)
                val g0 = S(rt * 0.038f)
                radios.moveTo(p0.x + n.x * g0, p0.y + n.y * g0); radios.lineTo(p1.x + n.x * g0, p1.y + n.y * g0)
                radios.lineTo(p1.x - n.x * g0, p1.y - n.y * g0); radios.lineTo(p0.x - n.x * g0, p0.y - n.y * g0); radios.close()
            }
            drawPath(corridoDistopia(radios, -hacia * 1f * u, 1.2f * u), lt.s(0.4f))
            drawPath(radios, lt.c(P.ACERO))
            drawPath(bordeDistopia(radios, hacia * 0.8f * u, 0.8f * u), lt.b(0.45f))
            drawCircle(lt.c(P.ACERO_LUZ), S(rt * 0.7f), c0, style = Stroke(S(rt * 0.09f)))
            drawArc(lt.b(0.85f), 190f, 80f, false, P(cx - rt * 0.7f, cy - rt * 0.7f), Size(S(rt * 1.4f), S(rt * 1.4f)), style = Stroke(1.2f * u))
            drawArc(lt.s(0.4f), 10f, 80f, false, P(cx - rt * 0.7f, cy - rt * 0.7f), Size(S(rt * 1.4f), S(rt * 1.4f)), style = Stroke(1.2f * u))
            drawCircle(lt.c(P.ACERO_LUZ), S(rt * 0.2f), c0)
            drawCircle(lt.s(0.35f), S(rt * 0.2f), c0, style = Stroke(1f * u))
            drawCircle(lt.b(0.6f), S(rt * 0.07f), P(cx - rt * 0.06f, cy - rt * 0.06f))
            // La palanca de presión, cromada, abajo a la izquierda del tambor.
            drawLine(lc.c(P.ACERO_LUZ), P(0.27f, 0.425f), P(0.245f, 0.395f), 2f * u, StrokeCap.Round)
            drawCircle(lc.c(P.BAQUELITA), S(0.007f), P(0.243f, 0.392f))
            // La bandeja de salida, con las copias que van cayendo.
            val salida = poligonoDistopia(P(0.48f, 0.43f), P(0.615f, 0.458f), P(0.612f, 0.466f), P(0.478f, 0.44f))
            drawPath(salida, l(0.55f, 0.45f).c(P.ACERO))
            drawLine(l(0.55f, 0.45f).b(0.55f), P(0.48f, 0.43f), P(0.615f, 0.458f), 0.9f * u)
            val copias = poligonoDistopia(P(0.49f, 0.425f), P(0.605f, 0.449f), P(0.603f, 0.456f), P(0.488f, 0.432f))
            drawPath(copias, l(0.55f, 0.44f).c(P.HUESO))
            // La copia de arriba, con la punta levantada.
            val a0 = P(0.52f, 0.428f); val a1 = P(0.58f, 0.432f); val a2 = P(0.62f, 0.428f); val a3 = P(0.618f, 0.434f); val a4 = P(0.58f, 0.44f); val a5 = P(0.52f, 0.434f)
            drawPath(Path().apply { moveTo(a0.x, a0.y); quadraticTo(a1.x, a1.y, a2.x, a2.y); lineTo(a3.x, a3.y); quadraticTo(a4.x, a4.y, a5.x, a5.y); close() },
                l(0.57f, 0.43f).c(mezcla(P.HUESO, P.LUZ_TUBO, 0.3f)))
            // La pintura saltada en los cantos del cuerpo.
            for (j in 0 until 7) {
                val x = 0.2f + 0.31f * azar(6420, j)
                val y = if (j % 2 == 0) 0.41f else pie - 0.018f
                val ls = l(x, y)
                saltadoDistopia(P(x, y), S(0.003f + 0.004f * azar(6421, j)), 6422 + j, ls.c(mezcla(P.HIERRO, P.ACERO, 0.3f)), ls.b(0.5f))
            }
        }
        // De noche, el filo frío de la luz floja del sótano en los cantos de la izquierda y de arriba (lejos del flexo), para que la
        // máquina no se pierda en lo oscuro.
        if (g.a.oscuro) {
            val frio = P.FRIO.copy(alpha = if (g.a.noche) 0.32f else 0.42f)
            val cuerpoF = poligonoDistopia(listOf(P(0.19f, pie - 0.006f), P(0.522f, pie - 0.006f), P(0.515f, 0.43f), P(0.47f, 0.408f), P(0.25f, 0.408f), P(0.2f, 0.432f)))
            drawPath(bordeDistopia(cuerpoF, 1.8f * u, 1.4f * u), frio)
            drawPath(bordeDistopia(Path().apply { addOval(Rect(P(cx, cy), S(rt))) }, 1.8f * u, 1.4f * u), frio)
            drawPath(bordeDistopia(poligonoDistopia(P(0.128f, 0.33f), P(0.142f, 0.318f), P(0.3f, 0.412f), P(0.284f, 0.424f)), 1.2f * u, 1.4f * u), frio)
            drawPath(bordeDistopia(poligonoDistopia(P(0.142f, 0.306f), P(0.156f, 0.294f), P(0.28f, 0.372f), P(0.268f, 0.386f)), 1f * u, 1.2f * u), frio)
        }
        // La manivela: el brazo desde el cubo y el pomo de madera, por delante de todo lo demás.
        val brazo0 = P(cx, cy); val brazo1 = P(0.432f, 0.31f)
        val lm = g.luz(brazo1, 0.06f)
        drawLine(lm.s(0.35f), brazo0 + Offset(1.6f * u, 2f * u), brazo1 + Offset(1.6f * u, 2f * u), 3.4f * u, StrokeCap.Round)
        drawLine(lm.c(P.ACERO_LUZ), brazo0, brazo1, 3.4f * u, StrokeCap.Round)
        drawLine(lm.b(0.8f), brazo0 - Offset(0f, 1f * u), brazo1 - Offset(0f, 1f * u), 0.9f * u, StrokeCap.Round)
        val pomo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(brazo1.x - S(0.011f), brazo1.y - S(0.019f), brazo1.x + S(0.011f), brazo1.y + S(0.009f)), CornerRadius(S(0.01f)))) }
        drawPath(corridoDistopia(pomo, 1.4f * u, 1.8f * u), lm.s(0.4f))
        cilindroDistopia(pomo, lm.c(P.MADERA), lm, hacia, 0.5f)
        drawCircle(lm.c(P.ACERO_LUZ), 1.6f * u, brazo0)
        if (g.a.oscuro) drawPath(bordeDistopia(pomo, -hacia * 1.4f * u, 0f), lm.filo(0.7f))
    }

    // ---------------------------------------------------------------- la lata de tinta

    /** La lata de tinta de la multicopista, abierta, con la tapa apoyada contra ella y un chorreón negro por el borde. */
    private fun DrawScope.lata(g: MarcoDistopia) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val r = Rect(0.08f * w, pie - 0.042f * w, 0.132f * w, pie)
        val l = g.luz(r.center.x, r.center.y, 0.05f)
        val hacia = if (g.a.oscuro && !g.a.noche) 1f else -1f
        sombraEnPared(g, Path().apply { addRect(r) }, r.center.x, r.top, 0.22f, 0.6f)
        drawOval(g.a.sombra(0.4f), Offset(r.left - 0.004f * w, pie - 0.005f * w), Size(r.width + 0.018f * w, 0.009f * w))
        // La tapa, apoyada de canto a la izquierda: un óvalo inclinado.
        rotate(-18f, Offset(r.left, pie)) {
            val tapa = Rect(r.left - 0.026f * w, pie - 0.05f * w, r.left - 0.012f * w, pie)
            drawOval(l.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.3f)), tapa.topLeft, tapa.size)
            drawOval(l.c(P.BAQUELITA), tapa.topLeft + Offset(tapa.width * 0.35f, tapa.height * 0.15f), Size(tapa.width * 0.4f, tapa.height * 0.7f))
            drawOval(l.b(0.5f), tapa.topLeft + Offset(1f * u, tapa.height * 0.2f), Size(1.4f * u, tapa.height * 0.5f))
        }
        val cuerpo = Path().apply { addRect(r) }
        cilindroDistopia(cuerpo, l.c(mezcla(P.CARTEL, P.BURDEOS, 0.4f)), l, hacia, 0.45f)
        // La faja de papel del fabricante, sin letras: una banda clara con dos filetes.
        drawRect(l.c(P.HUESO).copy(alpha = 0.85f), Offset(r.left, r.top + r.height * 0.32f), Size(r.width, r.height * 0.32f))
        drawRect(l.c(P.BAQUELITA).copy(alpha = 0.5f), Offset(r.left, r.top + r.height * 0.36f), Size(r.width, 0.8f * u))
        drawRect(l.c(P.BAQUELITA).copy(alpha = 0.5f), Offset(r.left, r.top + r.height * 0.58f), Size(r.width, 0.8f * u))
        // La boca abierta con la tinta negra y su brillo, y el chorreón que bajó.
        drawOval(l.c(P.ACERO_LUZ), Offset(r.left, r.top - 0.005f * w), Size(r.width, 0.01f * w))
        drawOval(Color(0xFF0D0E10), Offset(r.left + 1.4f * u, r.top - 0.0035f * w), Size(r.width - 2.8f * u, 0.007f * w))
        drawOval(l.b(0.6f), Offset(r.left + r.width * 0.25f, r.top - 0.003f * w), Size(r.width * 0.2f, 0.002f * w))
        drawPath(Path().apply { moveTo(r.right - 0.012f * w, r.top); quadraticTo(r.right - 0.01f * w, r.top + 0.012f * w, r.right - 0.013f * w, r.top + 0.02f * w) }, Color(0xFF0D0E10), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        if (g.a.oscuro) drawPath(bordeDistopia(cuerpo, -hacia * 1.2f * u, 0f), l.filo(0.6f))
    }

    // ---------------------------------------------------------------- la taza con los lápices

    /**
     * La taza de esmalte hueso con el borde oliva, desportillada (la misma del Ideario), que aquí hace de bote: dos lápices de
     * madera, el bicolor rojo y azul y una regla de acero.
     */
    private fun DrawScope.taza(g: MarcoDistopia) {
        val w = g.w; val u = density
        val cx = 0.668f * w; val pie = g.apoyo
        val r = 0.018f * w; val h = 0.044f * w
        val l = g.luz(cx, pie - h / 2, 0.05f)
        val hacia = if (g.a.oscuro && !g.a.noche) 1f else -1f
        sombraEnPared(g, Path().apply { addRect(Rect(cx - r, pie - h - 0.05f * w, cx + r, pie)) }, cx, pie - h, 0.2f)
        drawOval(g.a.sombra(0.4f), Offset(cx - r * 0.8f, pie - 0.004f * w), Size(r * 2.6f, 0.009f * w))
        // Lo que asoma: la regla, los lápices (uno de punta roja y azul), por detrás del borde.
        val boca = pie - h
        val cosas = listOf(Triple(-0.55f, -0.05f, 0), Triple(0.1f, 0.07f, 1), Triple(0.45f, 0.13f, 2), Triple(-0.2f, -0.12f, 3))
        cosas.forEach { (dx, giro, cual) ->
            val base = Offset(cx + dx * r, boca + 0.012f * w)
            val largo = when (cual) { 0 -> 0.07f; 1 -> 0.062f; 2 -> 0.056f; else -> 0.05f } * w
            val punta = Offset(base.x + sin(giro) * largo, base.y - cos(giro) * largo)
            when (cual) {
                0 -> {
                    drawLine(l.s(0.3f), base + Offset(1.4f * u, 0f), punta + Offset(1.4f * u, 1f * u), 0.006f * w)
                    drawLine(l.c(P.ACERO_LUZ), base, punta, 0.006f * w)
                    drawLine(l.b(0.7f), base - Offset(0.002f * w, 0f), punta - Offset(0.002f * w, 0f), 0.8f * u)
                    for (j in 1 until 8) {
                        val f = j / 8f
                        val q = base + (punta - base) * f
                        drawLine(l.c(P.HIERRO), q, q + Offset(0.003f * w, 0.0003f * w), 0.6f * u)
                    }
                }
                else -> {
                    val madera = if (cual == 2) P.CARTEL else mezcla(P.MADERA, P.CARPETA, 0.45f)
                    drawLine(l.s(0.3f), base + Offset(1.4f * u, 0f), punta + Offset(1.4f * u, 1f * u), 0.0055f * w)
                    drawLine(l.c(madera), base, punta, 0.0055f * w)
                    drawLine(l.b(0.45f), base - Offset(0.0015f * w, 0f), punta - Offset(0.0015f * w, 0f), 0.7f * u)
                    // La punta afilada: la madera clara y la mina; el bicolor, azul en la otra punta (la de abajo, dentro).
                    val dir = (punta - base) / largo
                    val corte = punta - dir * (0.009f * w)
                    drawLine(l.c(mezcla(P.CARPETA, P.HUESO, 0.4f)), corte, punta - dir * (0.003f * w), 0.004f * w)
                    drawLine(l.c(if (cual == 2) P.CARTEL else P.BAQUELITA), punta - dir * (0.003f * w), punta, 0.0022f * w)
                }
            }
        }
        // El asa.
        drawArc(l.c(P.HUESO), -80f, 170f, false, Offset(cx + r * 0.55f, pie - h * 0.82f), Size(r * 1.1f, h * 0.55f), style = Stroke(0.0045f * w))
        drawArc(l.s(0.3f), 0f, 90f, false, Offset(cx + r * 0.55f, pie - h * 0.82f), Size(r * 1.1f, h * 0.55f), style = Stroke(0.002f * w))
        // El cuerpo cilíndrico, el borde oliva y la boca.
        val cuerpo = Path().apply { addRect(Rect(cx - r, pie - h, cx + r, pie)) }
        cilindroDistopia(cuerpo, l.c(P.HUESO), l, hacia, 0.5f)
        drawOval(l.c(P.HUESO), Offset(cx - r, pie - 0.004f * w), Size(2 * r, 0.006f * w))
        drawRect(l.c(P.OLIVA), Offset(cx - r, pie - h), Size(2 * r, 0.004f * w))
        drawOval(l.c(P.OLIVA), Offset(cx - r, pie - h - 0.004f * w), Size(2 * r, 0.008f * w))
        drawOval(l.c(P.BAQUELITA), Offset(cx - r * 0.82f, pie - h - 0.0025f * w), Size(r * 1.64f, 0.005f * w))
        // Lo desportillado: el hierro negro que asoma.
        saltadoDistopia(Offset(cx - r * 0.4f, pie - h + 0.003f * w), 0.0035f * w, 6430, l.c(P.BAQUELITA), l.b(0.5f))
        saltadoDistopia(Offset(cx + r * 0.25f, pie - h * 0.35f), 0.003f * w, 6431, l.c(P.BAQUELITA), l.b(0.5f))
        if (g.a.oscuro) drawPath(bordeDistopia(cuerpo, -hacia * 1.2f * u, 0f), l.filo(0.7f))
    }

    // ---------------------------------------------------------------- el flexo

    /**
     * El flexo del Ideario (el mismo modelo: base de domo, dos tramos de varillas con su muelle, pantalla de chapa oliva), aquí a la
     * derecha de la cornisa y estirado hacia delante, con la pantalla vuelta hacia la primera repisa. De día, apagado; de noche, la
     * luz que manda.
     */
    private fun DrawScope.flexo(g: MarcoDistopia) {
        val w = g.w; val u = density
        val m = MuebleEstanteDistopia
        val metal = mezcla(P.OLIVA, P.ACERO_OSC, 0.45f)
        val o = g.a.flexo > 0f
        val base = Offset(0.862f, m.APOYO)
        val codo = Offset(0.905f, 0.305f)
        val cabeza = Offset(0.792f, 0.39f)
        fun P(p: Offset) = Offset(p.x * w, p.y * w)
        fun l(p: Offset) = g.luz(p.x * w, p.y * w, 0.06f)
        // La sombra del brazo en la pared (de día y en los Sueños en claro).
        if (!g.a.oscuro) {
            val s = Path().apply { moveTo(P(base).x, P(base).y - 0.01f * w); lineTo(P(codo).x, P(codo).y); lineTo(P(cabeza).x, P(cabeza).y) }
            val d = if (g.a.noche) Offset(0.02f, 0.006f) else Offset(0.016f, 0.012f)
            translate(d.x * w, d.y * w) { drawPath(s, g.a.sombra(0.22f), style = Stroke(0.007f * w, cap = StrokeCap.Round)) }
        }
        // La base, un domo pesado.
        val lb = l(base)
        drawOval(g.a.sombra(0.45f), Offset((base.x - 0.034f) * w, (base.y - 0.005f) * w), Size(0.074f * w, 0.011f * w))
        drawOval(lb.c(metal), Offset((base.x - 0.035f) * w, (base.y - 0.014f) * w), Size(0.07f * w, 0.016f * w))
        drawArc(lb.c(metal), 180f, 180f, true, Offset((base.x - 0.022f) * w, (base.y - 0.03f) * w), Size(0.044f * w, 0.03f * w))
        drawArc(lb.b(0.6f), 200f, 60f, false, Offset((base.x - 0.022f) * w, (base.y - 0.03f) * w), Size(0.044f * w, 0.03f * w), style = Stroke(1.2f * u))
        drawCircle(lb.c(P.BAQUELITA), 0.0035f * w, Offset((base.x + 0.018f) * w, (base.y - 0.008f) * w))
        // Los dos tramos del brazo, cada uno de dos varillas, con su muelle.
        fun brazo(a: Offset, b: Offset) {
            val v = b - a; val lv = v.getDistance().coerceAtLeast(0.0001f); val nn = Offset(-v.y / lv, v.x / lv) * 0.005f
            val lm = l(Offset((a.x + b.x) / 2, (a.y + b.y) / 2))
            listOf(nn, -nn).forEachIndexed { i, s ->
                drawLine(lm.c(metal), P(a + s), P(b + s), (if (i == 0) 2.2f else 1.6f) * u, StrokeCap.Round)
            }
            drawLine(lm.b(0.6f), P(a + nn), P(b + nn), 0.7f * u)
            for (j in 0 until 7) {
                val f = 0.25f + j * 0.025f
                val q = a + v * f
                drawLine(lm.c(P.ACERO_LUZ), P(q + nn * 1.6f), P(q - nn * 1.6f + v * 0.012f), 0.8f * u)
            }
        }
        brazo(Offset(base.x, base.y - 0.026f), codo)
        brazo(codo, cabeza)
        listOf(codo, cabeza, Offset(base.x, base.y - 0.026f)).forEach { q ->
            val lq = l(q)
            drawCircle(lq.c(metal), 0.0065f * w, P(q))
            drawCircle(lq.c(P.ACERO_LUZ), 0.0028f * w, P(q))
        }
        // La pantalla: un cono de chapa vuelto hacia abajo y a la izquierda, hacia la primera repisa.
        val boca = Offset(m.FLEXO_X, m.FLEXO_Y)
        val v = boca - cabeza; val lv = sqrt(v.x * v.x + v.y * v.y).coerceAtLeast(0.0001f)
        val dir = v / lv; val perp = Offset(dir.y, -dir.x)
        val ap = cabeza - dir * 0.006f
        val b1 = ap + perp * 0.011f; val b2 = ap - perp * 0.011f
        val m1 = boca + perp * 0.036f; val m2 = boca - perp * 0.036f
        val cono = Path().apply {
            moveTo(P(b2).x, P(b2).y); lineTo(P(b1).x, P(b1).y)
            cubicTo(P(b1 + dir * 0.02f + perp * 0.006f).x, P(b1 + dir * 0.02f + perp * 0.006f).y, P(m1 - dir * 0.02f).x, P(m1 - dir * 0.02f).y, P(m1).x, P(m1).y)
            lineTo(P(m2).x, P(m2).y)
            cubicTo(P(m2 - dir * 0.022f).x, P(m2 - dir * 0.022f).y, P(b2 + dir * 0.008f - perp * 0.008f).x, P(b2 + dir * 0.008f - perp * 0.008f).y, P(b2).x, P(b2).y)
            close()
        }
        val lc = l(cabeza)
        if (!g.a.oscuro) translate(0.016f * w, 0.012f * w) { drawPath(cono, g.a.sombra(0.18f)) }
        drawPath(cono, lc.c(metal))
        clipPath(cono) {
            // El lado de arriba de la pantalla (a la luz de día) y el de abajo (en sombra).
            drawPath(poligonoDistopia(P(b1), P(m1), P(m1 - perp * 0.016f), P(b1 - perp * 0.004f)), if (o) lc.b(0.35f) else lc.b(0.5f))
            drawPath(poligonoDistopia(P(b2), P(m2), P(m2 + perp * 0.014f), P(b2 + perp * 0.004f)), g.a.sombra(0.3f))
        }
        drawPath(cono, mezcla(lc.c(metal), ParedEstanteDistopia.honda(g), 0.4f), style = Stroke(0.8f * u))
        drawCircle(lc.c(metal), 0.007f * w, P(ap))
        // La boca: oscura de día; de noche, la bombilla que arde.
        val bocaC = P(boca)
        val ang = Math.toDegrees(kotlin.math.atan2(perp.y.toDouble(), perp.x.toDouble())).toFloat()
        rotate(ang, bocaC) {
            val r = Rect(bocaC.x - 0.036f * w, bocaC.y - 0.009f * w, bocaC.x + 0.036f * w, bocaC.y + 0.009f * w)
            if (o) {
                drawOval(P.FLEXO, r.topLeft, r.size)
                drawOval(Color(0xFFFFF4DC), Offset(bocaC.x - 0.017f * w, bocaC.y - 0.005f * w), Size(0.034f * w, 0.01f * w))
            } else {
                drawOval(lc.c(mezcla(P.BAQUELITA, metal, 0.3f)), r.topLeft, r.size)
                drawOval(lc.c(mezcla(P.HUESO, P.ACERO_LUZ, 0.5f)), Offset(bocaC.x - 0.012f * w, bocaC.y - 0.004f * w), Size(0.024f * w, 0.008f * w))
            }
            drawOval(lc.c(P.ACERO_LUZ), r.topLeft, r.size, style = Stroke(1.2f * u))
        }
        if (o) {
            mancha(bocaC + Offset(-0.01f * w, 0.02f * w), w * 0.11f, P.FLEXO.copy(alpha = 0.45f * g.a.flexo))
            mancha(bocaC, w * 0.04f, Color(0xFFFFF4DC).copy(alpha = 0.6f * g.a.flexo))
        }
    }
}
