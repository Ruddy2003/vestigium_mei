package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * La pared del estante de Abismo ([MuebleEstanteAbismo]): el costado del pañol, con el mismo forro de tablas pintadas de blanco
 * roto que el mamparo del Ideario (sus juntas, las testas, el salitre, los desconchones y los clavos con su chorrera de óxido), y lo
 * que hay en ella: el bao de teca con su curva cruzando el techo, el reverbero del agua en el bao, la tubería de cobre de la bomba
 * con sus abrazaderas y la llave, el ojo de buey de latón con su tapa ciega abierta (de día, el mar claro; de noche, el mar negro
 * con la luz viva; al ocaso, el sol que se pone; con la luna, su camino en el agua), la red del buzo con las esponjas, colgada de
 * un clavo, y el farol de barco colgado del bao, que de noche arde y tiñe la pared.
 */
internal object MuroEstanteAbismo {
    private val P = PaletaAbismo

    /** El ojo de buey: su centro y el radio del aro, en anchos. */
    const val OJO_X = 0.215f
    const val OJO_Y = 0.215f
    const val OJO_R = 0.1f
    /** El bao: hasta dónde baja, en anchos. */
    const val BAO = 0.05f
    /** Dónde baja la tubería hacia la bomba y dónde acaba su racor (ahí se le une la manguera), en anchos. */
    const val TUBO_X = 0.585f
    const val TUBO_FIN = 0.2f

    // ---------------------------------------------------------------- la pared

    /** La pared entera: el forro y, donde cae la mancha de la escotilla, el mismo forro pintado con esa luz (y su penumbra). */
    fun DrawScope.pared(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width
        paredCon(a, apoyo, cerca, null, 0f)
        if (m != null) {
            clipPath(m.forma(w, 0f, 0.05f)) { paredCon(a, apoyo, cerca, m, 0.45f) }
            clipPath(m.forma(w)) { paredCon(a, apoyo, cerca, m, 1f) }
        }
    }

    private fun DrawScope.paredCon(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        fun c(col: Color, f: Float, p: Offset) = a.en(col, f, cerca(p), lit, m)
        val alto = apoyo + w * 0.02f
        // El fondo: de día, más claro hacia la escotilla y en sombra a la derecha; de noche, el halo del farol.
        if (a.oscuro) {
            val fx = MuebleEstanteAbismo.FAROL_X * w; val fy = MuebleEstanteAbismo.FAROL_Y * w
            val dist = floatArrayOf(0f, 0.06f, 0.14f, 0.25f, 0.4f, 0.6f, 0.85f, 1.1f)
            val paradas = Array(dist.size) { i -> (dist[i] / 1.1f) to c(P.MAMPARO, 0f, Offset(fx - dist[i] * w * 0.7f, fy + dist[i] * w * 0.7f)) }
            drawRect(Brush.radialGradient(*paradas, center = Offset(fx, fy), radius = w * 1.1f), Offset.Zero, Size(w, alto))
        } else if (lit > 0f) {
            drawRect(c(P.MAMPARO, 0.04f, Offset(w * 0.4f, w * 0.2f)), Offset.Zero, Size(w, alto))
        } else {
            val l = c(mezcla(P.MAMPARO, P.MAMPARO_LUZ, 0.45f), 0f, Offset.Zero)
            val med = c(P.MAMPARO, 0f, Offset.Zero)
            val osc = c(mezcla(P.MAMPARO, P.MAMPARO_OSC, 0.65f), 0f, Offset.Zero)
            drawRect(Brush.horizontalGradient(listOf(l, med, osc), 0f, w), Offset.Zero, Size(w, alto))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.sombra(0.12f)), apoyo - w * 0.22f, apoyo), Offset(0f, apoyo - w * 0.22f), Size(w, w * 0.24f))
        }
        // Las tablas: la junta de cada una (luz arriba, sombra abajo), una de cada tres un poco más honda, y alguna testa.
        val tabla = w * 0.052f
        var y = BAO * w + tabla * 0.55f
        var i = 0
        while (y < alto) {
            val k = cerca(Offset(w * 0.6f, y))
            if (i % 3 == 1) drawRect(a.sombra(0.05f), Offset(0f, y), Size(w, tabla))
            drawRect(a.sombra(0.3f), Offset(0f, y - 1.2f * u), Size(w, 1.2f * u))
            drawRect(a.sombra(0.11f), Offset(0f, y), Size(w, 2.5f * u))
            drawRect(a.brillo(0.42f * (if (lit > 0f) 1.3f else 1f), k), Offset(0f, y - 2.4f * u), Size(w, 1f * u))
            val xt = w * (0.12f + 0.76f * azar(8101, i))
            drawRect(a.sombra(0.24f), Offset(xt, y), Size(1f * u, tabla))
            drawRect(a.brillo(0.3f, k), Offset(xt + 1f * u, y), Size(0.8f * u, tabla))
            y += tabla; i++
        }
        val zona = Rect(0f, BAO * w, w, alto)
        pinceladas(zona, if (a.oscuro) P.NOCHE else P.MAMPARO_OSC, 8102, 46, u, if (a.oscuro) 0.05f else 0.1f)
        pinceladas(zona, if (a.oscuro) P.LLAMA else mezcla(P.MAMPARO, luzMueble(1f), 0.5f), 8103, 18, u, if (a.oscuro) 0.03f else 0.12f)
        // El salitre: manchas blanquecinas y aguadas, más abajo y bajo el ojo de buey (donde chorrea el agua de la junta).
        for (k in 0 until 8) {
            val p = Offset(w * (if (k < 2) 0.15f + 0.12f * azar(8104, k) else azar(8104, k)), alto * (0.45f + 0.5f * azar(8105, k)))
            val col = c(mezcla(P.MAMPARO, Color(0xFFF6F3EA), 0.6f), 0f, p)
            drawOval(col.copy(alpha = if (a.oscuro) 0.25f else 0.55f), Offset(p.x, p.y), Size(w * (0.05f + 0.06f * azar(8106, k)), w * 0.012f))
            drawOval(a.sombra(0.06f), Offset(p.x + w * 0.01f, p.y + w * 0.01f), Size(w * 0.05f, w * 0.008f))
        }
        // La chorrera de óxido bajo el ojo de buey: el agua de la junta que baja por las tablas.
        val ox = Offset(w * (OJO_X + 0.02f), w * (OJO_Y + OJO_R))
        drawRect(c(P.OXIDO, 0f, ox).copy(alpha = 0.22f), Offset(ox.x - w * 0.006f, ox.y), Size(w * 0.012f, w * 0.11f))
        drawRect(c(P.OXIDO, 0f, ox).copy(alpha = 0.14f), Offset(ox.x - w * 0.014f, ox.y + w * 0.02f), Size(w * 0.03f, w * 0.06f))
        // Los desconchones: la pintura saltada y la teca debajo.
        listOf(Offset(0.46f, 0.12f), Offset(0.66f, 0.36f), Offset(0.94f, 0.3f), Offset(0.04f, 0.44f), Offset(0.36f, 0.42f)).forEachIndexed { j, f ->
            val p = Offset(w * f.x, w * f.y)
            if (p.y > alto - w * 0.02f) return@forEachIndexed
            val s = w * (0.013f + 0.01f * azar(8110, j))
            val forma = poligonoAbismo(p.x, p.y, p.x + s, p.y - s * 0.3f, p.x + s * 1.6f, p.y + s * 0.2f, p.x + s * 0.9f, p.y + s * 0.7f, p.x + s * 0.2f, p.y + s * 0.5f)
            drawPath(forma, c(mezcla(P.TECA, P.MAMPARO_OSC, 0.45f), 0f, p))
            translate(0.8f * u, 0.8f * u) { drawPath(forma, a.sombra(0.25f), style = Stroke(0.6f * u)) }
        }
        // Los clavos de las tablas, con su chorrera de óxido.
        listOf(0.06f, 0.4f, 0.71f, 0.97f).forEachIndexed { j, fx ->
            var yc = BAO * w + tabla * 0.55f + tabla * 0.5f + tabla * (j % 2)
            while (yc < alto - tabla * 0.5f) {
                val p = Offset(w * fx, yc)
                val largo = w * (0.02f + 0.035f * azar(8120 + j, (yc / tabla).toInt()))
                drawRect(c(P.OXIDO, 0f, p).copy(alpha = 0.3f), Offset(p.x - 0.8f * u, p.y), Size(1.6f * u, largo))
                drawRect(c(P.OXIDO, 0f, p).copy(alpha = 0.16f), Offset(p.x - 1.6f * u, p.y + largo * 0.2f), Size(3.2f * u, largo * 0.5f))
                drawCircle(c(mezcla(P.OXIDO, P.TINTA, 0.35f), 0f, p), 1.4f * u, p)
                drawCircle(a.brillo(0.3f, cerca(p)), 0.6f * u, p - Offset(0.5f * u, 0.5f * u))
                yc += tabla * 2
            }
        }
    }

    // ---------------------------------------------------------------- lo que hay en la pared

    /** El bao con su curva, el reverbero, la tubería, el ojo de buey y la red de las esponjas. */
    fun DrawScope.colgado(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        ojoDeBuey(a, cerca, m)
        tuberia(a, cerca, m)
        bao(a, cerca)
        cabillero(a, apoyo, cerca, m)
        red(a, apoyo, cerca)
    }

    /**
     * El cabillero: el listón de teca atornillado a la pared con sus dos cabillas torneadas. De la primera cuelga la sondaleza
     * adujada (el cordel de sondar, con sus marcas de trapo) y, por debajo, el escandallo de plomo; de la segunda, una madeja de
     * piola. La luz de la escotilla los pinta donde cae.
     */
    private fun DrawScope.cabillero(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width; val u = density
        val y = w * 0.135f
        val r = Rect(w * 0.325f, y - w * 0.012f, w * 0.505f, y + w * 0.012f)
        val k = cerca(r.center)
        fun lit(p: Offset) = if (m != null) m.toca(p.x / w, p.y / w) else 0f
        fun c(col: Color, f: Float, p: Offset) = a.en(col, f, cerca(p), lit(p), m)
        sombraEnPared(Path().apply { addRect(r) }, r.center, apoyo, a, cerca, 0.22f)
        drawRect(c(P.TECA, 0f, r.center), r.topLeft, r.size)
        drawRect(c(P.TECA_LUZ, 0.05f, r.center), r.topLeft, Size(r.width, r.height * 0.3f))
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        vetas(r, a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.35f), 8140, densidad = 0.8f, grosor = 0.5f)
        listOf(r.left + w * 0.012f, r.right - w * 0.012f).forEach { x -> clavoAbismo(Offset(x, y), 1.5f * u, a, k, P.PLOMO) }
        // Las cabillas: el puño torneado arriba y la caña que pasa el listón.
        val xs = listOf(w * 0.352f, w * 0.462f)
        xs.forEach { x ->
            val p = Offset(x, y)
            drawRect(a.sombra(0.3f), Offset(x - w * 0.004f + 2f * u, y - w * 0.05f + 2f * u), Size(w * 0.008f, w * 0.07f))
            drawRect(c(P.TECA_OSC, 0f, p), Offset(x - w * 0.004f, y + w * 0.01f), Size(w * 0.008f, w * 0.018f))
            val puno = Path().apply {
                moveTo(x - w * 0.004f, y - w * 0.012f); lineTo(x - w * 0.008f, y - w * 0.03f)
                cubicTo(x - w * 0.011f, y - w * 0.05f, x + w * 0.011f, y - w * 0.05f, x + w * 0.008f, y - w * 0.03f)
                lineTo(x + w * 0.004f, y - w * 0.012f); close()
            }
            drawPath(puno, c(P.TECA_LUZ, 0f, p))
            drawRect(a.sombra(0.3f), Offset(x + w * 0.002f, y - w * 0.045f), Size(w * 0.005f, w * 0.034f))
            drawOval(c(P.TECA_OSC, 0f, p), Offset(x - w * 0.008f, y - w * 0.016f), Size(w * 0.016f, w * 0.006f))
        }
        // La sondaleza adujada, colgada de la primera cabilla: vueltas largas que caen juntas.
        val ps = Offset(xs[0], y - w * 0.008f)
        val largo = w * 0.12f
        val vueltas = Path()
        for (j in 0 until 6) {
            val off = (j - 2.5f) * w * 0.006f
            vueltas.moveTo(ps.x - w * 0.003f, ps.y)
            vueltas.cubicTo(ps.x - w * 0.03f + off, ps.y + largo * 0.4f, ps.x - w * 0.028f + off * 1.4f, ps.y + largo, ps.x + off * 0.6f, ps.y + largo * 1.02f)
            vueltas.cubicTo(ps.x + w * 0.028f + off * 1.4f, ps.y + largo, ps.x + w * 0.03f + off, ps.y + largo * 0.4f, ps.x + w * 0.003f, ps.y)
        }
        val ks = cerca(Offset(ps.x, ps.y + largo * 0.5f))
        val ls = lit(Offset(ps.x, ps.y + largo * 0.5f))
        if (!a.oscuro) translate(w * 0.016f, w * 0.01f) { drawPath(vueltas, a.sombra(0.16f), style = Stroke(2f * u)) }
        drawPath(vueltas, a.en(P.MANILA_OSC, 0f, ks, ls, m), style = Stroke(2.2f * u))
        drawPath(vueltas, a.en(P.MANILA, 0.05f, ks, ls, m), style = Stroke(1.2f * u))
        // Las marcas de trapo de la sondaleza (cada tantas brazas), en las vueltas: rojo, blanco y azul desvaídos.
        listOf(Triple(-0.024f, 0.55f, P.ROJO_TRAPO), Triple(0.02f, 0.72f, P.MAMPARO_LUZ), Triple(-0.012f, 0.9f, P.AZUL_CARTA)).forEach { (dx, fy, col) ->
            val p = Offset(ps.x + w * dx, ps.y + largo * fy)
            drawRect(a.en(col, 0f, ks, ls, m), Offset(p.x - w * 0.004f, p.y - w * 0.003f), Size(w * 0.008f, w * 0.006f))
        }
        // El chicote que baja al escandallo de plomo (con su copa de sebo abajo).
        val pl = Offset(ps.x + w * 0.006f, ps.y + largo + w * 0.055f)
        drawLine(a.en(P.MANILA_OSC, 0f, ks, ls, m), Offset(ps.x + w * 0.002f, ps.y + largo * 1.02f), Offset(pl.x, pl.y - w * 0.002f), 1.2f * u)
        val lw = w * 0.022f; val lh = w * 0.06f
        val plomo = poligonoAbismo(pl.x - lw * 0.32f, pl.y, pl.x + lw * 0.32f, pl.y, pl.x + lw * 0.5f, pl.y + lh, pl.x - lw * 0.5f, pl.y + lh)
        sombraEnPared(plomo, pl, apoyo, a, cerca, 0.22f)
        val kp = cerca(pl); val lp = lit(pl)
        drawPath(plomo, a.en(P.PLOMO, 0f, kp, lp, m))
        clipPath(plomo) { cilindroAbismo(Rect(pl.x - lw * 0.5f, pl.y, pl.x + lw * 0.5f, pl.y + lh), a, kp, if (a.oscuro) 1f else -1f, 1.3f) }
        drawOval(a.en(mezcla(P.PLOMO, P.TINTA, 0.4f), 0f, kp, lp, m), Offset(pl.x - lw * 0.5f, pl.y + lh - w * 0.004f), Size(lw, w * 0.008f))
        drawCircle(a.en(P.PLOMO, 0.1f, kp, lp, m), w * 0.004f, Offset(pl.x, pl.y), style = Stroke(1.2f * u))
        // La madeja de piola de la segunda cabilla.
        val pp = Offset(xs[1], y - w * 0.006f)
        val mad = Path()
        for (j in 0 until 4) {
            val off = (j - 1.5f) * w * 0.005f
            mad.moveTo(pp.x, pp.y); mad.cubicTo(pp.x - w * 0.02f + off, pp.y + w * 0.03f, pp.x - w * 0.016f + off, pp.y + w * 0.07f, pp.x + off, pp.y + w * 0.072f)
            mad.cubicTo(pp.x + w * 0.016f + off, pp.y + w * 0.07f, pp.x + w * 0.02f + off, pp.y + w * 0.03f, pp.x, pp.y)
        }
        val km = cerca(Offset(pp.x, pp.y + w * 0.04f)); val lm = lit(Offset(pp.x, pp.y + w * 0.04f))
        if (!a.oscuro) translate(w * 0.014f, w * 0.009f) { drawPath(mad, a.sombra(0.14f), style = Stroke(1.6f * u)) }
        drawPath(mad, a.en(P.ESPARTO, -0.05f, km, lm, m), style = Stroke(1.6f * u))
        drawPath(mad, a.en(P.ESPARTO, 0.15f, km, lm, m), style = Stroke(0.7f * u))
    }

    /** La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera del farol. No baja de [hasta]. */
    fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: AireAbismo, cerca: (Offset) -> Float, fuerza: Float = 0.18f) {
        val w = size.width
        clipRect(0f, 0f, size.width, hasta) {
            if (!a.oscuro) translate(w * 0.02f, w * 0.012f) { drawPath(p, a.sombra(fuerza)) }
            else {
                val k = cerca(desde)
                if (k > 0.12f) {
                    val dx = (desde.x / w - MuebleEstanteAbismo.FAROL_X).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.014f) { drawPath(p, a.sombra((0.5f * k).coerceAtMost(0.42f))) }
                }
            }
        }
    }

    /**
     * El bao de teca que cruza el techo: la tablazón de la cubierta de arriba, el canto del bao con su luz y su sombra, los pernos,
     * y la curva que lo ata al costado a la derecha. De día, el reverbero del agua que entra por el ojo de buey baila en él.
     */
    private fun DrawScope.bao(a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val y0 = w * 0.012f; val y1 = BAO * w
        val n = if (a.lumbre) 16 else 1
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y1))
            drawRect(a.dia(P.TECA_HONDA, 0f, k * 0.7f), Offset(x0, 0f), Size(x1 - x0, y0))
            drawRect(a.dia(P.TECA, 0f, k), Offset(x0, y0), Size(x1 - x0, y1 - y0))
            drawRect(a.dia(P.TECA_LUZ, 0f, k), Offset(x0, y0), Size(x1 - x0, 1.6f * u))
            drawRect(a.dia(P.TECA_OSC, 0f, k), Offset(x0, y1 - w * 0.008f), Size(x1 - x0, w * 0.008f))
        }
        // Las juntas de la tablazón de arriba.
        var x = w * 0.03f
        while (x < w) { drawRect(a.sombra(0.5f), Offset(x, 0f), Size(1.2f * u, y0)); x += w * 0.11f }
        vetas(Rect(0f, y0, w, y1 - w * 0.008f), a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.45f), 8130, densidad = 0.8f, grosor = 0.6f)
        // La sombra del bao en la pared.
        drawRect(a.sombra(0.34f), Offset(0f, y1), Size(w, 2.4f * u))
        drawRect(a.sombra(0.14f), Offset(0f, y1 + 2.4f * u), Size(w, 5f * u))
        // Los pernos del bao.
        listOf(0.09f, 0.37f, 0.63f, 0.93f).forEach { fx -> clavoAbismo(Offset(w * fx, (y0 + y1) / 2), 1.7f * u, a, cerca(Offset(w * fx, y1)), P.PLOMO) }
        // La curva: la escuadra de teca que ata el bao al costado, con su borde de dentro curvo.
        val cx0 = w * 0.9f; val cy1 = w * 0.17f
        val curva = Path().apply {
            moveTo(cx0, y1); lineTo(w + 2f, y1); lineTo(w + 2f, cy1)
            cubicTo(w * 0.985f, cy1 - w * 0.05f, w * 0.95f, y1 + w * 0.025f, cx0, y1)
            close()
        }
        sombraEnPared(curva, Offset(w * 0.96f, w * 0.1f), w * 0.6f, a, cerca, 0.2f)
        val kc = cerca(Offset(w * 0.95f, w * 0.1f))
        drawPath(curva, a.dia(P.TECA, 0f, kc))
        clipPath(curva) {
            vetas(Rect(cx0, y1, w, cy1), a.dia(P.TECA_OSC, 0f, kc).copy(alpha = 0.4f), 8131, densidad = 0.8f, vertical = true, grosor = 0.6f)
            drawRect(a.dia(P.TECA_OSC, 0f, kc).copy(alpha = 0.6f), Offset(w * 0.985f, y1), Size(w * 0.02f, cy1 - y1))
        }
        val borde = Path().apply { moveTo(w + 2f, cy1); cubicTo(w * 0.985f, cy1 - w * 0.05f, w * 0.95f, y1 + w * 0.025f, cx0, y1) }
        drawPath(borde, a.brillo(0.45f, kc), style = Stroke(1.2f * u))
        clavoAbismo(Offset(w * 0.975f, w * 0.08f), 1.6f * u, a, kc, P.PLOMO)
        // El reverbero del agua: líneas de luz que se anudan y bailan en el bao y en lo alto de la pared, cerca del ojo de buey.
        if (!a.oscuro) {
            val col = if (a.noche) P.OCASO.copy(alpha = 0.5f) else luzMueble(0.62f)
            val r = Path()
            for (j in 0 until 7) {
                val x0 = w * (0.27f + 0.06f * j + 0.02f * azar(8135, j))
                val yy = w * (0.017f + 0.03f * azar(8136, j))
                r.moveTo(x0, yy)
                r.cubicTo(x0 + w * 0.02f, yy - w * 0.008f, x0 + w * 0.035f, yy + w * 0.012f, x0 + w * 0.06f, yy + w * 0.002f)
                r.moveTo(x0 + w * 0.01f, yy + w * 0.014f)
                r.cubicTo(x0 + w * 0.025f, yy + w * 0.004f, x0 + w * 0.03f, yy + w * 0.02f, x0 + w * 0.05f, yy + w * 0.017f)
            }
            clipRect(w * 0.24f, 0f, w * 0.72f, y1 + w * 0.03f) { drawPath(r, col, style = Stroke(1.5f * u, cap = StrokeCap.Round)) }
        }
    }

    /**
     * La tubería de cobre de la bomba de aire (la misma que en el Ideario baja al manómetro): entra por la derecha bajo el bao,
     * sujeta con abrazaderas, pasa por la llave de paso y baja por un codo hasta el racor con su tapón, desconectada mientras se
     * repasa la bomba; verdín en las uniones.
     */
    private fun DrawScope.tuberia(a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width; val u = density
        val y = w * 0.078f
        val g = w * 0.014f
        val xc = w * TUBO_X
        val baja = w * 0.17f
        val tubo = Path().apply {
            moveTo(w + w * 0.02f, y); lineTo(xc + w * 0.03f, y)
            cubicTo(xc + w * 0.005f, y, xc, y + w * 0.005f, xc, y + w * 0.03f); lineTo(xc, baja)
        }
        if (!a.oscuro) translate(w * 0.012f, w * 0.01f) { drawPath(tubo, a.sombra(0.2f), style = Stroke(g)) }
        val k = cerca(Offset(w * 0.75f, y))
        val lit = if (m != null && m.toca(xc / w, w * 0.12f / w, ManchaAbismo.FRENTE) > 0f) 0.6f else 0f
        drawPath(tubo, a.en(P.COBRE, 0f, k, lit, m), style = Stroke(g))
        drawPath(tubo, a.en(P.COBRE_OSC, 0f, k, lit, m).copy(alpha = 0.7f), style = Stroke(g * 0.35f))
        translate(-g * 0.25f, -g * 0.25f) { drawPath(tubo, a.en(P.COBRE_LUZ, 0f, k, lit, m), style = Stroke(g * 0.3f)) }
        translate(-g * 0.25f, -g * 0.3f) { drawPath(tubo, a.brillo(0.6f, k), style = Stroke(g * 0.1f)) }
        // Las abrazaderas.
        listOf(0.95f, 0.8f, 0.66f).forEach { fx ->
            val x = w * fx; val kk = cerca(Offset(x, y))
            drawRect(a.dia(mezcla(P.PLOMO, P.OXIDO, 0.3f), 0f, kk), Offset(x - 2f * u, y - g * 0.7f), Size(4f * u, g * 1.4f))
            drawRect(a.brillo(0.3f, kk), Offset(x - 2f * u, y - g * 0.7f), Size(1f * u, g * 1.4f))
            clavoAbismo(Offset(x, y - g * 0.95f), 1.1f * u, a, kk, mezcla(P.PLOMO, P.OXIDO, 0.4f))
        }
        // La llave de paso: el cuerpo de latón y la palanca, abierta (a lo largo del tubo).
        val lv = Offset(w * 0.72f, y)
        val kv = cerca(lv)
        drawRoundRect(a.sombra(0.3f), Offset(lv.x - g * 0.9f + 1.5f * u, lv.y - g * 0.9f + 2f * u), Size(g * 1.8f, g * 1.8f), CornerRadius(g * 0.3f))
        drawRoundRect(a.dia(P.LATON, 0f, kv), Offset(lv.x - g * 0.9f, lv.y - g * 0.9f), Size(g * 1.8f, g * 1.8f), CornerRadius(g * 0.3f))
        drawRect(a.dia(P.LATON_OSC, 0f, kv), Offset(lv.x + g * 0.35f, lv.y - g * 0.9f), Size(g * 0.55f, g * 1.8f))
        drawRect(a.dia(P.LATON, 0f, kv), Offset(lv.x - g * 0.25f, lv.y - g * 1.6f), Size(g * 0.5f, g * 0.7f))
        drawLine(a.dia(mezcla(P.HIERRO, P.MINIO, 0.5f), 0f, kv), Offset(lv.x, lv.y - g * 1.5f), Offset(lv.x + w * 0.045f, lv.y - g * 1.75f), g * 0.45f, StrokeCap.Round)
        destelloAbismo(Offset(lv.x - g * 0.45f, lv.y - g * 0.5f), 1f * u, a, kv)
        // Las uniones con su verdín y el racor del final, con su tapón.
        listOf(Offset(w * 0.88f, y), Offset(xc, y + w * 0.045f)).forEach { p ->
            val kk = cerca(p)
            drawRect(a.dia(mezcla(P.COBRE, P.LATON, 0.5f), 0f, kk), Offset(p.x - g * 0.65f, p.y - g * 0.65f), Size(g * 1.3f, g * 1.3f))
            drawOval(a.dia(P.VERDIN, 0f, kk).copy(alpha = 0.65f), Offset(p.x - g * 0.5f, p.y + g * 0.1f), Size(g * 1.1f, g * 0.5f))
        }
        val rc = Rect(xc - g * 0.85f, baja - g * 0.2f, xc + g * 0.85f, baja + g * 1.3f)
        val kr = cerca(rc.center)
        drawRect(a.sombra(0.3f), Offset(rc.left + 1.5f * u, rc.top + 2f * u), rc.size)
        drawRect(a.en(P.LATON, 0f, kr, lit, m), rc.topLeft, rc.size)
        for (j in 0 until 3) drawRect(a.dia(P.LATON_OSC, 0f, kr), Offset(rc.left + rc.width * (0.2f + 0.3f * j), rc.top), Size(1f * u, rc.height))
        drawRect(a.brillo(0.55f, kr), rc.topLeft, Size(rc.width, 1f * u))
        drawRect(a.dia(P.LATON_OSC, 0f, kr), Offset(rc.left - g * 0.15f, rc.bottom), Size(rc.width + g * 0.3f, g * 0.5f))
    }

    /**
     * El ojo de buey: el aro de latón con sus pernos y sus tres palomillas, el vidrio grueso con su bisel y lo que se ve del mar a
     * través de él, y la tapa ciega abierta a la izquierda, colgada de su bisagra.
     */
    private fun DrawScope.ojoDeBuey(a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width; val u = density
        val c = Offset(OJO_X * w, OJO_Y * w)
        val ro = OJO_R * w
        val rv = ro * 0.7f
        val k = cerca(c)
        // La tapa ciega, abierta hacia la izquierda: un disco de latón visto de canto, con su goma y su asa.
        val tc = Offset(c.x - ro * 1.42f, c.y + ro * 0.02f)
        val trx = ro * 0.34f; val trY = ro * 0.86f
        val tapa = Path().apply { addOval(Rect(tc.x - trx, tc.y - trY, tc.x + trx, tc.y + trY)) }
        sombraEnPared(tapa, tc, w * 0.6f, a, cerca, 0.2f)
        drawPath(tapa, a.dia(P.LATON_OSC, 0f, k))
        drawOval(a.dia(P.LATON, 0f, k), Offset(tc.x - trx * 0.92f, tc.y - trY * 0.95f), Size(trx * 1.6f, trY * 1.9f))
        drawOval(a.dia(mezcla(P.LATON, P.LATON_OSC, 0.4f), 0f, k), Offset(tc.x - trx * 0.62f, tc.y - trY * 0.72f), Size(trx * 1.1f, trY * 1.44f))
        drawOval(a.dia(P.GOMA, 0f, k), Offset(tc.x - trx * 0.55f, tc.y - trY * 0.66f), Size(trx * 0.95f, trY * 1.32f), style = Stroke(1.6f * u))
        drawArc(a.brillo(0.55f, k), 150f, 80f, false, Offset(tc.x - trx * 0.92f, tc.y - trY * 0.95f), Size(trx * 1.6f, trY * 1.9f), style = Stroke(1.2f * u))
        // El asa de la tapa.
        drawRoundRect(a.dia(P.LATON_OSC, 0f, k), Offset(tc.x - trx * 0.2f, tc.y - trY * 0.25f), Size(trx * 0.35f, trY * 0.5f), CornerRadius(2f * u))
        // La bisagra que la une al aro.
        val bis = Rect(c.x - ro * 1.12f, c.y - ro * 0.12f, c.x - ro * 0.9f, c.y + ro * 0.12f)
        drawRect(a.dia(P.LATON_OSC, 0f, k), bis.topLeft, bis.size)
        drawRect(a.dia(P.LATON, 0f, k), Offset(bis.left, bis.top), Size(bis.width, bis.height * 0.45f))
        // La sombra del aro en la pared.
        val aro = Path().apply { addOval(Rect(c, ro)) }
        sombraEnPared(aro, c, w * 0.6f, a, cerca, 0.22f)
        // El aro de latón: cara, luz arriba a la izquierda, sombra abajo a la derecha.
        val lit = if (m != null) m.toca(c.x / w + 0.07f, c.y / w) else 0f
        drawCircle(a.en(P.LATON, 0f, k, lit, m), ro, c)
        drawArc(a.en(P.LATON_LUZ, 0f, k, lit, m), 160f, 120f, false, Offset(c.x - ro * 0.9f, c.y - ro * 0.9f), Size(ro * 1.8f, ro * 1.8f), style = Stroke(ro * 0.09f))
        drawArc(a.dia(P.LATON_OSC, 0f, k), -20f, 130f, false, Offset(c.x - ro * 0.9f, c.y - ro * 0.9f), Size(ro * 1.8f, ro * 1.8f), style = Stroke(ro * 0.12f))
        drawCircle(a.dia(P.LATON_OSC, 0f, k), ro, c, style = Stroke(1f * u))
        // El escalón del vidrio: el bisel hondo del aro.
        drawCircle(a.dia(P.LATON_OSC, 0f, k), rv + ro * 0.08f, c)
        drawArc(a.sombra(0.5f), 180f, 180f, false, Offset(c.x - rv - ro * 0.08f, c.y - rv - ro * 0.08f), Size((rv + ro * 0.08f) * 2, (rv + ro * 0.08f) * 2), style = Stroke(ro * 0.06f))
        // Los pernos del aro.
        for (j in 0 until 10) {
            val ang = Math.toRadians(18.0 + j * 36.0)
            clavoAbismo(Offset(c.x + ro * 0.84f * cos(ang).toFloat(), c.y + ro * 0.84f * sin(ang).toFloat()), 1.5f * u, a, k)
        }
        // El vidrio y lo que se ve.
        val vidrio = Path().apply { addOval(Rect(c, rv)) }
        clipPath(vidrio) { mar(a, c, rv) }
        // El vidrio grueso: su reflejo (de día, la luz de la escotilla; de noche, el del farol) y su borde.
        if (!a.oscuro) {
            drawArc(luzMueble(0.6f), 200f, 60f, false, Offset(c.x - rv * 0.82f, c.y - rv * 0.82f), Size(rv * 1.64f, rv * 1.64f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawArc(luzMueble(0.35f), 205f, 35f, false, Offset(c.x - rv * 0.66f, c.y - rv * 0.66f), Size(rv * 1.32f, rv * 1.32f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        } else {
            drawArc(P.LLAMA.copy(alpha = 0.55f), -40f, 40f, false, Offset(c.x - rv * 0.82f, c.y - rv * 0.82f), Size(rv * 1.64f, rv * 1.64f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        }
        drawCircle(a.sombra(0.45f), rv, c, style = Stroke(1.4f * u))
        // Las tres palomillas que aprietan el vidrio contra el aro.
        listOf(40f, 160f, 280f).forEach { gr ->
            val ang = Math.toRadians(gr.toDouble())
            val p = Offset(c.x + ro * 1.02f * cos(ang).toFloat(), c.y + ro * 1.02f * sin(ang).toFloat())
            val ala = w * 0.011f
            drawCircle(a.sombra(0.35f), ala * 0.7f, p + Offset(1.5f * u, 2f * u))
            drawPath(poligonoAbismo(p.x - ala * 1.3f, p.y - ala * 0.5f, p.x, p.y - ala * 0.15f, p.x + ala * 1.3f, p.y - ala * 0.5f, p.x + ala * 0.4f, p.y + ala * 0.35f, p.x - ala * 0.4f, p.y + ala * 0.35f),
                a.dia(mezcla(P.LATON, P.COBRE, 0.25f), 0f, k))
            clavoAbismo(p, ala * 0.42f, a, k)
        }
        destelloAbismo(Offset(c.x - ro * 0.55f, c.y - ro * 0.62f), 1.6f * u, a, k)
        // En los Sueños oscuros, la luna manda: su luz fría sale por el ojo de buey y aclara la pared de alrededor.
        if (a.oscuro && a.noche) {
            mancha(c, ro * 2.6f, P.LUNA.copy(alpha = 0.1f))
            drawArc(P.LUNA_RAS.copy(alpha = 0.7f), 120f, 120f, false, Offset(c.x - ro, c.y - ro), Size(ro * 2, ro * 2), style = Stroke(1.6f * u))
        }
    }

    /** Lo que se ve por el ojo de buey, en el círculo de centro [c] y radio [r]: el cielo, el horizonte y el mar del momento. */
    private fun DrawScope.mar(a: AireAbismo, c: Offset, r: Float) {
        val u = density
        val hz = c.y - r * 0.12f
        val arriba = Rect(c.x - r, c.y - r, c.x + r, hz)
        val abajo = Rect(c.x - r, hz, c.x + r, c.y + r)
        when {
            a.oscuro && a.noche -> {
                drawRect(Brush.verticalGradient(listOf(Color(0xFF0E2438), Color(0xFF26445A)), arriba.top, arriba.bottom), arriba.topLeft, arriba.size)
                drawRect(Color(0xFF0A1C28), abajo.topLeft, abajo.size)
                // La luna creciente, alta a la izquierda, y su camino en el agua.
                val lc = Offset(c.x - r * 0.38f, c.y - r * 0.55f)
                mancha(lc, r * 0.5f, P.LUNA.copy(alpha = 0.25f))
                drawCircle(P.LUNA, r * 0.15f, lc)
                drawCircle(Color(0xFF0E2438), r * 0.13f, lc + Offset(r * 0.07f, -r * 0.03f))
                for (j in 0 until 9) {
                    val yy = hz + (c.y + r - hz) * (0.08f + 0.1f * j)
                    val an = r * (0.08f + 0.05f * j) * (0.7f + 0.6f * azar(8150, j))
                    drawRect(P.LUNA.copy(alpha = 0.7f - 0.05f * j), Offset(lc.x - an / 2 + r * 0.05f * (azar(8151, j) - 0.5f), yy), Size(an, 1.2f * u))
                }
            }
            a.oscuro -> {
                drawRect(Brush.verticalGradient(listOf(P.CIELO_NOCHE, Color(0xFF16323F)), arriba.top, arriba.bottom), arriba.topLeft, arriba.size)
                for (j in 0 until 7) drawCircle(Color(0xFFDCE8EE).copy(alpha = 0.5f + 0.4f * azar(8152, j)), (0.5f + 0.6f * azar(8153, j)) * u,
                    Offset(c.x - r + 2 * r * azar(8154, j), c.y - r + (hz - c.y + r) * azar(8155, j)))
                drawRect(P.MAR_NOCHE, abajo.topLeft, abajo.size)
                // La luz viva: la noctiluca que se enciende donde rompe la ola (una cresta, en trozos sueltos) y chispas sueltas.
                val yy = hz + (c.y + r - hz) * 0.42f
                mancha(Offset(c.x - r * 0.1f, yy), r * 0.5f, P.LUZ_VIVA.copy(alpha = 0.16f))
                for (j in 0 until 5) {
                    val x0 = c.x - r * 0.75f + r * 0.32f * j + r * 0.08f * azar(8156, j)
                    val y0 = yy + r * 0.06f * kotlin.math.sin(j * 1.7f) + r * 0.03f * azar(8157, j)
                    val tramo = Path().apply { moveTo(x0, y0); quadraticTo(x0 + r * 0.08f, y0 - r * 0.05f, x0 + r * (0.14f + 0.08f * azar(8158, j)), y0 + r * 0.01f) }
                    drawPath(tramo, P.LUZ_VIVA.copy(alpha = 0.25f), style = Stroke(3f * u, cap = StrokeCap.Round))
                    drawPath(tramo, P.LUZ_VIVA.copy(alpha = 0.85f), style = Stroke(1f * u, cap = StrokeCap.Round))
                }
                for (j in 0 until 22) {
                    val p = Offset(c.x - r * 0.85f + r * 1.7f * azar(8160, j), yy + (c.y + r - yy) * (azar(8161, j) * azar(8162, j)) - r * 0.06f)
                    drawCircle(P.LUZ_VIVA.copy(alpha = 0.45f + 0.5f * azar(8159, j)), (0.5f + 0.8f * azar(8163, j)) * u, p)
                }
            }
            a.noche -> {
                drawRect(Brush.verticalGradient(listOf(P.CIELO_OCASO_ALTO, P.CIELO_OCASO), arriba.top, arriba.bottom), arriba.topLeft, arriba.size)
                // El sol que se pone, medio hundido en el horizonte, y su camino en el agua.
                val sc = Offset(c.x + r * 0.2f, hz)
                mancha(sc, r * 0.6f, Color(0xFFFFD9A0).copy(alpha = 0.5f))
                clipRect(arriba.left, arriba.top, arriba.right, hz) { drawCircle(Color(0xFFFFE4B0), r * 0.16f, sc) }
                drawRect(P.MAR_OCASO, abajo.topLeft, abajo.size)
                for (j in 0 until 9) {
                    val yy = hz + (c.y + r - hz) * (0.06f + 0.1f * j)
                    val an = r * (0.12f + 0.06f * j) * (0.7f + 0.6f * azar(8162, j))
                    drawRect(Color(0xFFFFCB8E).copy(alpha = 0.85f - 0.06f * j), Offset(sc.x - an / 2, yy), Size(an, 1.4f * u))
                }
            }
            else -> {
                drawRect(Brush.verticalGradient(listOf(P.CIELO_ALTO, P.CIELO), arriba.top, arriba.bottom), arriba.topLeft, arriba.size)
                // Una nube baja.
                drawOval(Color.White.copy(alpha = 0.8f), Offset(c.x - r * 0.1f, hz - r * 0.22f), Size(r * 0.6f, r * 0.12f))
                drawOval(Color.White.copy(alpha = 0.7f), Offset(c.x + r * 0.05f, hz - r * 0.3f), Size(r * 0.32f, r * 0.12f))
                drawRect(Brush.verticalGradient(listOf(P.MAR, P.MAR_HONDO), abajo.top, abajo.bottom), abajo.topLeft, abajo.size)
                // Las crestas y el brillo del sol en el agua.
                for (j in 0 until 6) {
                    val yy = hz + (c.y + r - hz) * (0.15f + 0.14f * j)
                    val x0 = c.x - r + 2 * r * azar(8163, j)
                    drawRect(P.ESPUMA.copy(alpha = 0.7f), Offset(x0, yy), Size(r * (0.12f + 0.15f * azar(8164, j)), 1.2f * u))
                }
                for (j in 0 until 10) drawRect(Color.White.copy(alpha = 0.9f), Offset(c.x - r * 0.5f + r * 0.6f * azar(8165, j), hz + (c.y + r - hz) * 0.5f * azar(8166, j) + 1.5f * u),
                    Size(r * (0.03f + 0.05f * azar(8167, j)), 1f * u))
            }
        }
        // El vidrio grueso oscurece el borde.
        drawCircle(Color(0xFF0E2A33).copy(alpha = if (a.oscuro) 0.5f else 0.28f), r, c, style = Stroke(r * 0.16f))
    }

    /**
     * La red del buzo (la que lleva a la cintura para la cosecha), colgada de un clavo bajo la tapa ciega, con tres esponjas dentro:
     * el aro de alambre arriba, la malla de cuerda que se abomba con su peso y el cabo del clavo.
     */
    private fun DrawScope.red(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val clavo = Offset(w * 0.095f, w * 0.335f)
        val k = cerca(clavo)
        val top = clavo.y + w * 0.03f
        val ancho = w * 0.115f
        val bajo = apoyo - w * 0.012f
        val cx = clavo.x + w * 0.005f
        val bolsa = Path().apply {
            moveTo(cx - ancho * 0.42f, top)
            cubicTo(cx - ancho * 0.62f, top + (bajo - top) * 0.45f, cx - ancho * 0.55f, bajo, cx, bajo)
            cubicTo(cx + ancho * 0.58f, bajo, cx + ancho * 0.62f, top + (bajo - top) * 0.4f, cx + ancho * 0.42f, top)
            close()
        }
        sombraEnPared(bolsa, Offset(cx, (top + bajo) / 2), apoyo, a, cerca, 0.2f)
        // El cabo del clavo al aro.
        drawLine(a.dia(P.MANILA_OSC, 0f, k), clavo, Offset(cx - ancho * 0.4f, top), 1.4f * u)
        drawLine(a.dia(P.MANILA_OSC, 0f, k), clavo, Offset(cx + ancho * 0.4f, top), 1.4f * u)
        clavoAbismo(clavo, 1.6f * u, a, k, mezcla(P.PLOMO, P.OXIDO, 0.5f))
        // Las esponjas de dentro.
        clipPath(bolsa) {
            drawPath(bolsa, a.dia(mezcla(P.ESPONJA_OSC, P.TECA_HONDA, 0.3f), 0f, k))
            listOf(Triple(-0.18f, 0.35f, P.ESPONJA_OSC), Triple(0.2f, 0.55f, P.ESPONJA), Triple(-0.12f, 0.8f, mezcla(P.ESPONJA, P.CORCHO, 0.5f))).forEachIndexed { j, (fx, fy, col) ->
                esponjaAbismo(Offset(cx + ancho * fx, top + (bajo - top) * fy), ancho * 0.34f, col, a, k, 8170 + j)
            }
            drawRect(a.sombra(0.25f), Offset(cx + ancho * 0.1f, top), Size(ancho * 0.5f, bajo - top))
            // La malla: rombos de cuerda que siguen la panza.
            val paso = ancho / 5f
            var x = cx - ancho * 1.2f
            val m = Path()
            while (x < cx + ancho * 1.2f) {
                m.moveTo(x, top); m.lineTo(x + (bajo - top) * 0.55f, bajo)
                m.moveTo(x + (bajo - top) * 0.55f, top); m.lineTo(x, bajo)
                x += paso
            }
            drawPath(m, a.dia(mezcla(P.ESPARTO, P.MAMPARO, 0.25f), 0f, k), style = Stroke(1.1f * u))
        }
        drawPath(bolsa, a.sombra(0.3f), style = Stroke(0.8f * u))
        // El aro de alambre.
        val aro = Rect(cx - ancho * 0.44f, top - w * 0.008f, cx + ancho * 0.44f, top + w * 0.008f)
        drawOval(a.dia(mezcla(P.PLOMO, P.OXIDO, 0.35f), 0f, k), aro.topLeft, aro.size, style = Stroke(2f * u))
        drawArc(a.brillo(0.45f, k), 180f, 120f, false, aro.topLeft, aro.size, style = Stroke(0.7f * u))
    }

    /** Una esponja: el contorno blando, el lado de la luz arriba a la izquierda, la sombra abajo, los poros y una boca. */
    fun DrawScope.esponjaAbismo(c: Offset, r: Float, col: Color, a: AireAbismo, k: Float, semilla: Int, achata: Float = 0.82f) {
        val u = density
        val pts = List(11) { j ->
            val an = 2 * Math.PI * j / 11
            val rr = r * (0.74f + 0.44f * azar(semilla, j))
            Offset(c.x + rr * cos(an).toFloat(), c.y + rr * achata * sin(an).toFloat())
        }
        val f = Path().apply {
            moveTo((pts[0].x + pts[1].x) / 2, (pts[0].y + pts[1].y) / 2)
            for (j in 1..11) {
                val p = pts[j % 11]; val q = pts[(j + 1) % 11]
                cubicTo(p.x, p.y, p.x, p.y, (p.x + q.x) / 2, (p.y + q.y) / 2)
            }
            close()
        }
        drawPath(f, a.dia(col, 0f, k))
        clipPath(f) {
            drawCircle(a.sombra(0.3f), r * 1.05f, c + Offset(r * 0.55f, r * 0.6f))
            drawCircle(a.brillo(0.22f, k), r * 0.6f, c - Offset(r * 0.4f, r * 0.45f))
            val esc = (r / (12f * u)).coerceIn(0.5f, 1.4f)
            for (j in 0 until 14) {
                val p = c + Offset(r * (azar(semilla + 5, j) - 0.5f) * 1.7f, r * achata * (azar(semilla + 6, j) - 0.5f) * 1.6f)
                drawCircle(a.dia(mezcla(col, P.TINTA, 0.45f), 0f, k).copy(alpha = 0.5f), (0.45f + 0.6f * azar(semilla + 7, j)) * u * esc, p)
            }
        }
        val o = c + Offset(r * (azar(semilla, 40) - 0.5f) * 0.6f, -r * 0.2f)
        drawOval(a.sombra(0.55f), Offset(o.x - r * 0.13f, o.y - r * 0.08f), Size(r * 0.26f, r * 0.16f))
    }

    // ---------------------------------------------------------------- el farol

    /**
     * El farol de barco (el del Ideario), colgado del bao por su cadena: el gancho, la argolla, la chimenea y el sombrerete, el
     * cristal con sus costillas y su jaula, el depósito de latón. De día, apagado; de noche, encendido, y su luz por encima de lo que
     * tiene cerca: el halo cálido en la pared. En los Sueños oscuros arde bajo.
     */
    fun DrawScope.farolYLuz(a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val cx = MuebleEstanteAbismo.FAROL_X * w
        val llamaY = MuebleEstanteAbismo.FAROL_Y * w
        val encendido = a.lumbre
        val k = if (a.oscuro) 1f else 0f
        val fw = w * 0.085f
        val sombT = llamaY - w * 0.105f; val sombB = llamaY - w * 0.075f
        val crT = sombB; val crB = llamaY + w * 0.04f
        val depB = crB + w * 0.035f
        val todo = Path().apply {
            addRect(Rect(cx - fw * 0.5f, sombT, cx + fw * 0.5f, depB))
            addOval(Rect(cx - fw * 0.15f, sombT - w * 0.03f, cx + fw * 0.15f, sombT))
        }
        if (!a.oscuro) sombraEnPared(todo, Offset(cx, crT), apoyo, a, cerca, 0.18f)
        // El gancho del bao, la cadena y la argolla.
        val gancho = Offset(cx, BAO * w)
        drawArc(a.dia(mezcla(P.PLOMO, P.OXIDO, 0.4f), 0f, 1f), 0f, 180f, false, Offset(cx - w * 0.008f, gancho.y - w * 0.002f), Size(w * 0.016f, w * 0.014f), style = Stroke(2f * u))
        var yc = gancho.y + w * 0.01f
        var j = 0
        while (yc < sombT - w * 0.035f) {
            val esl = if (j % 2 == 0) Rect(cx - w * 0.005f, yc, cx + w * 0.005f, yc + w * 0.016f) else Rect(cx - w * 0.0015f, yc, cx + w * 0.0015f, yc + w * 0.016f)
            drawOval(a.dia(P.LATON_OSC, 0f, maxOf(k, 0.4f)), esl.topLeft, esl.size, style = Stroke(1.4f * u))
            yc += w * 0.012f; j++
        }
        val arg = Rect(cx - w * 0.011f, sombT - w * 0.04f, cx + w * 0.011f, sombT - w * 0.016f)
        drawOval(a.dia(P.LATON, 0f, maxOf(k, 0.4f)), arg.topLeft, arg.size, style = Stroke(1.6f * u))
        // La chimenea y el sombrerete.
        val tl = if (a.oscuro) 0.9f else 1f
        val chim = Rect(cx - fw * 0.13f, sombT - w * 0.022f, cx + fw * 0.13f, sombT)
        drawRect(a.dia(P.LATON, 0f, tl), chim.topLeft, chim.size)
        drawRect(a.dia(mezcla(P.LATON, P.COBRE, 0.3f), 0f, tl), Offset(chim.left - 1.5f * u, chim.top), Size(chim.width + 3f * u, 2f * u))
        val somb = poligonoAbismo(cx - fw * 0.15f, sombT, cx + fw * 0.15f, sombT, cx + fw * 0.52f, sombB, cx - fw * 0.52f, sombB)
        drawPath(somb, a.dia(P.LATON, 0f, tl))
        clipPath(somb) {
            drawRect(if (encendido) P.LLAMA.copy(alpha = 0.18f) else luzMueble(0.32f), Offset(cx - fw * 0.5f, sombT), Size(fw * 0.3f, sombB - sombT))
            drawRect(a.sombra(0.28f), Offset(cx + fw * 0.15f, sombT), Size(fw * 0.4f, sombB - sombT))
        }
        // El cristal: de día, el vidrio con su reflejo; encendido, la llama.
        val cr = Rect(cx - fw * 0.42f, crT, cx + fw * 0.42f, crB)
        if (encendido) {
            val baja = a.oscuro && a.noche
            drawRoundRect(if (baja) Color(0xFFE8B66E) else Color(0xFFFFD98E), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(P.LLAMA.copy(alpha = 0.6f), Offset(cr.left, cr.bottom - cr.height * 0.35f), Size(cr.width, cr.height * 0.35f))
            val lc = Offset(cx, llamaY)
            val esc = if (baja) 0.7f else 1f
            drawOval(Color(0xFFFFB24A), Offset(lc.x - fw * 0.1f * esc, lc.y - fw * 0.22f * esc), Size(fw * 0.2f * esc, fw * 0.36f * esc))
            drawOval(P.FUEGO, Offset(lc.x - fw * 0.05f * esc, lc.y - fw * 0.12f * esc), Size(fw * 0.1f * esc, fw * 0.22f * esc))
            if (!a.oscuro) drawRect(a.sombra(0.12f), cr.topLeft, cr.size)
        } else {
            drawRoundRect(mezcla(P.MAMPARO, P.VIDRIO, 0.25f), cr.topLeft, cr.size, CornerRadius(fw * 0.12f))
            drawRect(a.sombra(0.2f), Offset(cx + fw * 0.12f, cr.top), Size(fw * 0.3f, cr.height))
            drawRect(mezcla(P.TINTA, P.TECA, 0.3f), Offset(cx - fw * 0.03f, crB - cr.height * 0.3f), Size(fw * 0.06f, cr.height * 0.1f))
            drawRect(a.sombra(0.18f), Offset(cr.left, cr.top), Size(cr.width, cr.height * 0.18f))
            drawRect(luzMueble(0.65f), Offset(cx - fw * 0.3f, cr.top + 2f * u), Size(1.6f * u, cr.height * 0.7f))
        }
        listOf(-0.22f, 0f, 0.22f).forEach { fx -> drawLine(if (encendido) Color(0xFFB57A2E).copy(alpha = 0.45f) else a.sombra(0.15f), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 0.8f * u) }
        listOf(-0.42f, -0.14f, 0.14f, 0.42f).forEach { fx -> drawLine(a.dia(mezcla(P.LATON, P.TINTA, 0.35f), 0f, tl), Offset(cx + fw * fx, cr.top), Offset(cx + fw * fx, cr.bottom), 1.1f * u) }
        listOf(0.35f, 0.7f).forEach { fy -> drawLine(a.dia(mezcla(P.LATON, P.TINTA, 0.35f), 0f, tl), Offset(cr.left - 1f * u, cr.top + cr.height * fy), Offset(cr.right + 1f * u, cr.top + cr.height * fy), 1.2f * u) }
        // El depósito de latón.
        val dep = Rect(cx - fw * 0.5f, crB, cx + fw * 0.5f, depB)
        drawRect(a.dia(P.LATON, 0f, tl), dep.topLeft, dep.size)
        clipRect(dep.left, dep.top, dep.right, dep.bottom) { cilindroAbismo(dep, a, if (a.oscuro) 0.9f else 1f, if (a.oscuro) 1f else -1f, 1.4f) }
        drawRect(a.dia(mezcla(P.LATON, P.COBRE, 0.3f), 0f, tl), Offset(dep.left - 1f * u, dep.top), Size(dep.width + 2f * u, 2f * u))
        if (encendido) drawRect(P.LLAMA.copy(alpha = 0.7f), Offset(dep.left, dep.top), Size(dep.width, 1.2f * u))
        drawOval(a.dia(P.VERDIN, 0f, tl).copy(alpha = 0.45f), Offset(dep.left + dep.width * 0.55f, dep.bottom - dep.height * 0.4f), Size(dep.width * 0.3f, dep.height * 0.3f))
        if (!a.oscuro) destelloAbismo(Offset(dep.left + dep.width * 0.25f, dep.top + dep.height * 0.35f), 1.2f * u, a, 1f)
        // La luz del farol, por encima de lo que tiene cerca.
        if (a.oscuro) {
            val f = if (a.noche) 0.55f else 1f
            val l = Offset(cx, llamaY)
            mancha(l + Offset(0f, w * 0.04f), w * 0.46f, P.LLAMA.copy(alpha = 0.09f * f))
            mancha(l + Offset(0f, w * 0.02f), w * 0.2f, P.LLAMA.copy(alpha = 0.2f * f))
            mancha(l, w * 0.06f, Color(0xFFFFE2A8).copy(alpha = 0.35f * f))
        } else if (a.noche) {
            mancha(Offset(cx, llamaY), w * 0.12f, P.LLAMA.copy(alpha = 0.16f))
        }
    }
}
