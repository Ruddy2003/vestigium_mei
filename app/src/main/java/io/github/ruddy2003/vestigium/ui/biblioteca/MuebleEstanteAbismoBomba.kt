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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.cos
import kotlin.math.sin

/**
 * La bomba de aire de la escafandra, lo que manda encima del armario de Abismo ([CoronaEstanteAbismo]): la caja de teca sobre sus
 * tacos, con su bastidor y sus dos paneles, las escuadras y el fleje de latón, las asas de cabo, la chapa del fabricante (sin letra)
 * y el manómetro en el frente; encima, las dos cabezas de los cilindros con su corona de tuercas y la salida de aire, de la que la
 * manguera negra (la del Ideario) cae en una vuelta y sube al racor de la tubería de cobre. A cada lado, en su cojinete, un
 * volante de hierro pintado de minio, más alto que la caja, con el eje arriba, la llanta en el aire y su manivela de puño de madera.
 * La luz de la escotilla pinta solo lo que mira hacia arriba (la tapa, las cabezas, lo alto de las llantas); su sombra corta la
 * mancha en la pared.
 */
internal object BombaEstanteAbismo {
    private val P = PaletaAbismo

    /** La bomba con el pie de su frente en [pie] (el centro, sobre la balda). */
    fun DrawScope.bomba(pie: Offset, a: AireAbismo, apoyo: Float, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width; val u = density
        val bw = w * 0.3f; val bh = w * 0.15f
        val dx = w * 0.045f; val dy = -w * 0.032f
        val x0 = pie.x - bw / 2; val x1 = pie.x + bw / 2
        val y1 = pie.y - w * 0.035f; val y0 = y1 - bh
        val k = cerca(Offset(pie.x, y0))
        fun c(col: Color, f: Float = 0f, p: Offset = Offset(pie.x, y0)) = a.dia(col, f, cerca(p))
        /** Lo que mira hacia arriba recibe la luz de la escotilla si cae dentro de su mancha. */
        fun arriba(col: Color, f: Float, p: Offset): Color = a.en(col, f, cerca(p), m?.toca(p.x / w, p.y / w, ManchaAbismo.FRENTE) ?: 0f, m)
        val radio = w * 0.115f
        val ejeY = y0 + bh * 0.32f + dy * 0.5f
        val eje = Offset(x1 + dx * 0.5f + w * 0.012f, ejeY)
        val ejeAtras = Offset(x0 + dx * 0.5f - w * 0.014f, ejeY)
        // Su sombra en la pared (dentro de la mancha de la escotilla, más honda: la bomba le quita la luz) y en la balda.
        val silueta = Path().apply {
            addRect(Rect(x0, y0 + dy, x1 + dx, y1))
            addOval(Rect(eje.x - radio * 0.36f, eje.y - radio, eje.x + radio * 0.36f, eje.y + radio))
            addOval(Rect(ejeAtras.x - radio * 0.36f, ejeAtras.y - radio, ejeAtras.x + radio * 0.36f, ejeAtras.y + radio))
        }
        with(MuroEstanteAbismo) { sombraEnPared(silueta, Offset(pie.x, y0), apoyo, a, cerca, 0.24f) }
        if (m != null && !a.oscuro) clipPath(m.forma(w)) {
            clipRect(0f, 0f, w, apoyo) { translate(w * 0.03f, w * 0.016f) { drawPath(silueta, a.sombra(0.26f)) } }
        }
        contactoAbismo(Offset(pie.x + w * 0.02f, pie.y), bw * 1.3f, w * 0.022f, a, 0.42f)
        // El volante de atrás, con su manivela hacia fuera.
        volante(ejeAtras, radio, a, cerca, m, atras = true)
        // Los tacos de teca bajo la caja.
        listOf(x0 + w * 0.01f, x1 - w * 0.06f).forEach { px ->
            val pr = Rect(px, y1, px + w * 0.05f, pie.y)
            drawRect(c(P.TECA_OSC, -0.05f, pr.center), pr.topLeft, pr.size)
            drawRect(c(P.TECA, 0f, pr.center), Offset(pr.left, pr.top), Size(pr.width * 0.4f, pr.height))
            drawRect(a.brillo(0.35f, k), pr.topLeft, Size(pr.width, 1f * u))
            drawRect(a.sombra(0.4f), Offset(pr.left, pr.top), Size(pr.width, 2f * u))
        }
        // La tapa (con su luz de arriba) y el costado en escorzo, con su cojinete.
        val tapa = poligonoAbismo(x0, y0, x1, y0, x1 + dx, y0 + dy, x0 + dx, y0 + dy)
        drawPath(tapa, arriba(P.TECA_LUZ, 0.08f, Offset(pie.x, y0 + dy * 0.5f)))
        clipPath(tapa) {
            vetas(Rect(x0, y0 + dy, x1 + dx, y0), c(P.TECA_OSC).copy(alpha = 0.35f), 8301, densidad = 0.8f, grosor = 0.5f)
            if (m != null) clipPath(m.forma(w, ManchaAbismo.FRENTE)) { drawPath(tapa, arriba(P.TECA_LUZ, 0.12f, Offset(pie.x, y0))) }
        }
        val costado = poligonoAbismo(x1, y0, x1 + dx, y0 + dy, x1 + dx, y1 + dy, x1, y1)
        drawPath(costado, c(P.TECA_OSC, -0.05f, Offset(x1, y0)))
        clipPath(costado) { vetas(Rect(x1, y0 + dy, x1 + dx, y1), c(P.TECA_HONDA).copy(alpha = 0.4f), 8303, densidad = 0.7f, vertical = true, grosor = 0.5f) }
        cojinete(Offset(x1 + dx * 0.5f, ejeY), bh, a, k)
        // El frente: el bastidor, los dos paneles rehundidos con su moldura y el fleje de latón de abajo.
        drawRect(c(P.TECA), Offset(x0, y0), Size(bw, bh))
        clipRect(x0, y0, x1, y1) { vetas(Rect(x0, y0, x1, y1), c(P.TECA_OSC).copy(alpha = 0.35f), 8302, densidad = 0.7f, grosor = 0.6f) }
        val mar = w * 0.016f
        val pw = (bw - mar * 3) / 2
        for (j in 0 until 2) {
            val pr = Rect(x0 + mar + j * (pw + mar), y0 + bh * 0.4f, x0 + mar + j * (pw + mar) + pw, y1 - mar * 1.4f)
            drawRect(c(P.TECA, -0.1f, pr.center), pr.topLeft, pr.size)
            drawRect(a.sombra(0.38f), pr.topLeft, Size(pr.width, 1.8f * u))
            drawRect(a.sombra(0.26f), pr.topLeft, Size(1.8f * u, pr.height))
            drawRect(a.brillo(0.38f, k), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
            drawRect(a.brillo(0.32f, k), Offset(pr.right - 1f * u, pr.top), Size(1f * u, pr.height))
            // El panel algo levantado en el centro: su luz arriba a la izquierda.
            val ins = Rect(pr.left + pr.width * 0.14f, pr.top + pr.height * 0.2f, pr.right - pr.width * 0.14f, pr.bottom - pr.height * 0.2f)
            drawRect(c(P.TECA, 0.02f, ins.center), ins.topLeft, ins.size)
            drawRect(a.brillo(0.3f, k), ins.topLeft, Size(ins.width, 1f * u))
            drawRect(a.sombra(0.28f), Offset(ins.left, ins.bottom - 1f * u), Size(ins.width, 1f * u))
        }
        val fl = Rect(x0, y1 - w * 0.012f, x1, y1)
        drawRect(c(P.LATON, -0.05f, fl.center), fl.topLeft, fl.size)
        drawRect(a.brillo(0.5f, k), fl.topLeft, Size(fl.width, 1f * u))
        var xf = x0 + w * 0.02f
        while (xf < x1) { clavoAbismo(Offset(xf, fl.center.y), 0.9f * u, a, k, P.LATON_OSC); xf += w * 0.05f }
        // La chapa del fabricante: un óvalo de latón con su borde y dos remaches, sin letra.
        val cf = Offset(x0 + bw * 0.74f, y0 + bh * 0.66f)
        val cr = Size(w * 0.07f, w * 0.03f)
        drawRoundRect(a.sombra(0.35f), Offset(cf.x - cr.width / 2 + 1.2f * u, cf.y - cr.height / 2 + 1.5f * u), cr, CornerRadius(w * 0.006f))
        drawRoundRect(c(P.LATON, 0.05f, cf), Offset(cf.x - cr.width / 2, cf.y - cr.height / 2), cr, CornerRadius(w * 0.006f))
        drawRoundRect(c(P.LATON_OSC, 0f, cf), Offset(cf.x - cr.width * 0.4f, cf.y - cr.height * 0.3f), Size(cr.width * 0.8f, cr.height * 0.6f), CornerRadius(w * 0.003f), style = Stroke(0.8f * u))
        drawRect(a.brillo(0.6f, k), Offset(cf.x - cr.width / 2, cf.y - cr.height / 2), Size(cr.width, 1f * u))
        listOf(-0.44f, 0.44f).forEach { f -> drawCircle(c(P.LATON_OSC, 0f, cf), 1f * u, Offset(cf.x + cr.width * f, cf.y)) }
        // Las escuadras de latón de las esquinas.
        listOf(Offset(x0, y0) to 0, Offset(x1, y0) to 1, Offset(x0, y1 - w * 0.012f) to 2, Offset(x1, y1 - w * 0.012f) to 3).forEach { (p, j) ->
            val e = w * 0.024f
            val sx = if (j % 2 == 0) 1f else -1f; val sy = if (j < 2) 1f else -1f
            val esq = poligonoAbismo(p.x, p.y, p.x + sx * e, p.y, p.x + sx * e, p.y + sy * e * 0.35f, p.x + sx * e * 0.35f, p.y + sy * e * 0.35f, p.x + sx * e * 0.35f, p.y + sy * e, p.x, p.y + sy * e)
            drawPath(esq, c(P.LATON, 0f, p))
            drawPath(esq, a.sombra(0.3f), style = Stroke(0.6f * u))
            clavoAbismo(Offset(p.x + sx * e * 0.2f, p.y + sy * e * 0.2f), 0.8f * u, a, k, P.LATON_OSC)
        }
        // Las asas de cabo, una a cada lado del frente: el cabo pasa por dos cáncamos y cae en una gaza.
        listOf(x0 + w * 0.012f to -1f, x1 - w * 0.012f to 1f).forEach { (ax, s) ->
            val ya = y0 + bh * 0.42f; val yb = y0 + bh * 0.78f
            val asa = Path().apply { moveTo(ax, ya); cubicTo(ax + s * w * 0.03f, ya, ax + s * w * 0.03f, yb, ax, yb) }
            translate(1.5f * u, 2f * u) { drawPath(asa, a.sombra(0.35f), style = Stroke(2.6f * u)) }
            drawPath(asa, c(P.MANILA_OSC, 0f, Offset(ax, ya)), style = Stroke(3f * u, cap = StrokeCap.Round))
            drawPath(asa, c(P.MANILA, 0.05f, Offset(ax, ya)), style = Stroke(1.7f * u, cap = StrokeCap.Round))
            listOf(ya, yb).forEach { y -> drawCircle(c(P.LATON, 0f, Offset(ax, y)), 2.2f * u, Offset(ax, y), style = Stroke(1.4f * u)) }
        }
        // El manómetro en el frente, a la izquierda, sobre el bastidor.
        manometro(Offset(x0 + bw * 0.3f, y0 + bh * 0.26f), w * 0.052f, a, k)
        // Las cabezas de los cilindros sobre la tapa, con su corona de tuercas y el prensaestopas.
        listOf(0.26f, 0.56f).forEach { fx ->
            val tc = Offset(x0 + bw * fx + dx * 0.5f, y0 + dy * 0.5f)
            val tr = w * 0.036f; val alto = w * 0.016f
            drawOval(a.sombra(0.35f), Offset(tc.x - tr + 1.5f * u, tc.y - tr * 0.35f + 2f * u), Size(tr * 2, tr * 0.7f))
            drawRect(c(P.LATON_OSC, 0f, tc), Offset(tc.x - tr, tc.y - alto), Size(tr * 2, alto))
            drawRect(c(P.LATON, 0f, tc), Offset(tc.x - tr, tc.y - alto), Size(tr * 0.5f, alto))
            drawOval(c(P.LATON_OSC, 0f, tc), Offset(tc.x - tr, tc.y - tr * 0.35f), Size(tr * 2, tr * 0.7f))
            val top = Offset(tc.x, tc.y - alto)
            drawOval(arriba(P.LATON, 0.12f, top), Offset(top.x - tr, top.y - tr * 0.35f), Size(tr * 2, tr * 0.7f))
            drawArc(a.brillo(0.6f, k), 180f, 120f, false, Offset(top.x - tr, top.y - tr * 0.35f), Size(tr * 2, tr * 0.7f), style = Stroke(1f * u))
            for (j in 0 until 6) {
                val an = Math.toRadians(j * 60.0 + 30.0)
                val p = Offset(top.x + tr * 0.78f * cos(an).toFloat(), top.y + tr * 0.27f * sin(an).toFloat())
                drawCircle(c(P.LATON_OSC, -0.1f, p), 1.5f * u, p)
                drawCircle(a.brillo(0.5f, k), 0.6f * u, p - Offset(0.4f * u, 0.4f * u))
            }
            drawRect(c(P.LATON_OSC, 0f, top), Offset(top.x - tr * 0.22f, top.y - w * 0.012f), Size(tr * 0.44f, w * 0.012f))
            drawOval(arriba(P.LATON, 0.15f, top), Offset(top.x - tr * 0.22f, top.y - w * 0.015f), Size(tr * 0.44f, tr * 0.16f))
        }
        // La salida de aire, con su racor, y la manguera que cae en una vuelta y sube al racor de la tubería de cobre.
        val sl = Offset(x0 + bw * 0.86f + dx * 0.5f, y0 + dy * 0.5f)
        drawRect(c(P.LATON, 0f, sl), Offset(sl.x - w * 0.009f, sl.y - w * 0.03f), Size(w * 0.018f, w * 0.03f))
        drawRect(c(P.LATON_OSC, 0f, sl), Offset(sl.x - w * 0.013f, sl.y - w * 0.036f), Size(w * 0.026f, w * 0.01f))
        drawRect(a.brillo(0.5f, k), Offset(sl.x - w * 0.009f, sl.y - w * 0.03f), Size(1f * u, w * 0.03f))
        manguera(Offset(sl.x, sl.y - w * 0.036f), Offset(w * MuroEstanteAbismo.TUBO_X, w * MuroEstanteAbismo.TUBO_FIN), a, cerca, k)
        // El filo de la tapa y el canto en sombra de la derecha.
        drawRect(arriba(P.TECA_LUZ, 0.25f, Offset(pie.x, y0)).copy(alpha = 0.9f), Offset(x0, y0), Size(bw, 1.2f * u))
        drawRect(a.sombra(0.35f), Offset(x1 - 1.2f * u, y0), Size(1.2f * u, bh))
        // La trinca: un cabo de cada taco al cáncamo de la balda.
        listOf(Offset(x0 + w * 0.012f, y1 + w * 0.02f) to Offset(x0 - w * 0.03f, pie.y + w * 0.004f), Offset(x1 - w * 0.012f, y1 + w * 0.02f) to Offset(x1 + w * 0.035f, pie.y + w * 0.004f)).forEach { (de, a0) ->
            drawLine(a.sombra(0.3f), de + Offset(1.5f * u, 1.5f * u), a0 + Offset(1.5f * u, 1.5f * u), 2.4f * u)
            drawLine(c(P.MANILA, 0f, de), de, a0, 2.2f * u, StrokeCap.Round)
            drawLine(c(P.MANILA_OSC, 0f, de), de + Offset(0f, 0.8f * u), a0 + Offset(0f, 0.8f * u), 0.7f * u)
            drawCircle(c(P.PLOMO, 0f, a0), w * 0.007f, a0, style = Stroke(1.6f * u))
        }
        // El volante de delante, con su manivela.
        volante(eje, radio, a, cerca, m, atras = false)
        if (a.lumbre) {
            val kf = cerca(Offset(x1, y0))
            drawRect(a.filo(0.6f * kf), Offset(x1 + dx - 1.5f * u, y0 + dy), Size(1.5f * u, bh))
        }
    }

    /** El cojinete de latón del eje en el costado: la caja con sus dos tornillos y la punta del eje. */
    private fun DrawScope.cojinete(c: Offset, bh: Float, a: AireAbismo, k: Float) {
        val u = density; val w = size.width
        val r = Rect(c.x - w * 0.012f, c.y - w * 0.014f, c.x + w * 0.012f, c.y + bh * 0.5f)
        drawRect(a.sombra(0.35f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
        drawRect(a.dia(P.LATON_OSC, 0f, k), r.topLeft, r.size)
        drawRect(a.dia(P.LATON, 0f, k), r.topLeft, Size(r.width * 0.45f, r.height))
        listOf(r.top + r.height * 0.6f, r.bottom - w * 0.006f).forEach { y -> clavoAbismo(Offset(r.center.x, y), 1f * u, a, k, P.LATON_OSC) }
        drawCircle(a.dia(P.HIERRO, 0f, k), w * 0.009f, c)
        drawCircle(a.dia(P.HIERRO_LUZ, 0f, k), w * 0.004f, c - Offset(w * 0.002f, w * 0.002f))
    }

    /** La manguera de goma, de la salida [de] al racor [a0] de la tubería: cae en una vuelta y sube, con sus abrazaderas de latón. */
    private fun DrawScope.manguera(de: Offset, a0: Offset, a: AireAbismo, cerca: (Offset) -> Float, k: Float) {
        val w = size.width; val u = density
        val gm = w * 0.02f
        val mang = Path().apply {
            moveTo(de.x, de.y)
            cubicTo(de.x + w * 0.01f, de.y - w * 0.05f, de.x - w * 0.02f, de.y - w * 0.075f, de.x - w * 0.06f, de.y - w * 0.03f)
            cubicTo(de.x - w * 0.09f, de.y + w * 0.005f, de.x - w * 0.14f, de.y - w * 0.02f, de.x - w * 0.12f, de.y - w * 0.07f)
            cubicTo(de.x - w * 0.1f, de.y - w * 0.11f, a0.x - w * 0.02f, a0.y + w * 0.05f, a0.x, a0.y)
        }
        val km = cerca(Offset(de.x - w * 0.06f, de.y - w * 0.05f))
        if (!a.oscuro) translate(w * 0.014f, w * 0.009f) { drawPath(mang, a.sombra(0.22f), style = Stroke(gm)) }
        drawPath(mang, a.dia(P.GOMA, 0f, km), style = Stroke(gm, cap = StrokeCap.Butt))
        drawPath(mang, a.dia(mezcla(P.GOMA, P.LONA, 0.28f), 0f, km), style = Stroke(gm * 0.32f))
        translate(-gm * 0.22f, -gm * 0.22f) { drawPath(mang, a.brillo(0.42f, km), style = Stroke(gm * 0.13f)) }
        listOf(de, a0).forEach { p ->
            val y = if (p == de) p.y - w * 0.006f else p.y + w * 0.004f
            drawRect(a.dia(P.LATON, 0f, k), Offset(p.x - gm * 0.7f, y - w * 0.004f), Size(gm * 1.4f, w * 0.009f))
            drawRect(a.brillo(0.5f, k), Offset(p.x - gm * 0.7f, y - w * 0.004f), Size(gm * 1.4f, 1f * u))
        }
    }

    /**
     * Un volante de hierro pintado de minio en escorzo, de eje [c] y radio [r]: la llanta gruesa (lo de arriba, con la luz de la
     * escotilla si le cae), los cinco rayos curvos (ni cruz ni asterisco), el cubo, el eje y la manivela con su puño de madera, hacia
     * fuera (a la izquierda en el de atrás).
     */
    private fun DrawScope.volante(c: Offset, r: Float, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, atras: Boolean) {
        val u = density; val w = size.width
        val k = cerca(c)
        fun col(cc: Color, f: Float = 0f) = a.dia(cc, if (atras) f - 0.12f else f, k)
        fun en(t: Double, rr: Float) = Offset(c.x + rr * 0.34f * sin(t).toFloat(), c.y - rr * cos(t).toFloat() - rr * 0.2f * sin(t).toFloat())
        val llanta = Path(); val dentro = Path()
        for (i in 0..36) {
            val t = 2 * Math.PI * i / 36
            val p = en(t, r); val q = en(t, r * 0.84f)
            if (i == 0) { llanta.moveTo(p.x, p.y); dentro.moveTo(q.x, q.y) } else { llanta.lineTo(p.x, p.y); dentro.lineTo(q.x, q.y) }
        }
        val g = r * 0.15f
        if (!atras) translate(r * 0.06f, r * 0.05f) { drawPath(llanta, a.sombra(0.3f), style = Stroke(g)) }
        drawPath(llanta, col(P.MINIO_OSC), style = Stroke(g))
        drawPath(llanta, col(P.MINIO), style = Stroke(g * 0.6f))
        translate(-g * 0.15f, -g * 0.15f) { drawPath(llanta, col(P.MINIO_LUZ).copy(alpha = 0.8f), style = Stroke(g * 0.18f)) }
        // Lo alto de la llanta, con la luz de la escotilla si le cae.
        val alto = en(0.0, r)
        val lit = m?.toca(alto.x / w, alto.y / w, ManchaAbismo.FRENTE) ?: 0f
        if (lit > 0f && m != null) {
            val arco = Path()
            for (i in -5..5) { val p = en(i * 0.12, r * 0.98f); if (i == -5) arco.moveTo(p.x, p.y) else arco.lineTo(p.x, p.y) }
            drawPath(arco, a.en(P.MINIO_LUZ, 0.1f, k, lit, m), style = Stroke(g * 0.45f, cap = StrokeCap.Round))
        }
        drawPath(dentro, col(P.MINIO_OSC).copy(alpha = 0.7f), style = Stroke(1f * u))
        val rayos = Path()
        for (j in 0 until 5) {
            val t = 2 * Math.PI * j / 5 + 0.3
            val p = en(t, r * 0.86f); val mid = en(t + 0.32, r * 0.5f)
            rayos.moveTo(c.x, c.y); rayos.quadraticTo(mid.x, mid.y, p.x, p.y)
        }
        drawPath(rayos, col(P.MINIO_OSC), style = Stroke(r * 0.085f, cap = StrokeCap.Round))
        drawPath(rayos, col(P.MINIO), style = Stroke(r * 0.05f, cap = StrokeCap.Round))
        drawOval(col(P.HIERRO), Offset(c.x - r * 0.08f, c.y - r * 0.17f), Size(r * 0.16f, r * 0.34f))
        drawOval(col(P.HIERRO_LUZ), Offset(c.x - r * 0.045f, c.y - r * 0.12f), Size(r * 0.07f, r * 0.15f))
        // La manivela: el brazo de hierro hasta cerca de la llanta y el puño de madera hacia fuera.
        val s = if (atras) -1f else 1f
        val pm = en(if (atras) 4.0 else 2.3, r * 0.74f)
        drawLine(a.sombra(0.3f), c + Offset(1.5f * u, 1.5f * u), pm + Offset(1.5f * u, 1.5f * u), r * 0.09f, StrokeCap.Round)
        drawLine(col(P.HIERRO), c, pm, r * 0.09f, StrokeCap.Round)
        drawLine(col(P.HIERRO_LUZ), c - Offset(0f, r * 0.02f), pm - Offset(0f, r * 0.02f), r * 0.025f, StrokeCap.Round)
        val puno = if (s > 0) Rect(pm.x, pm.y - r * 0.06f, pm.x + r * 0.36f, pm.y + r * 0.06f) else Rect(pm.x - r * 0.36f, pm.y - r * 0.06f, pm.x, pm.y + r * 0.06f)
        drawRoundRect(a.sombra(0.3f), Offset(puno.left + 1.5f * u, puno.top + 2f * u), puno.size, CornerRadius(r * 0.06f))
        drawRoundRect(col(P.TECA_OSC), puno.topLeft, puno.size, CornerRadius(r * 0.06f))
        drawRoundRect(col(P.TECA_LUZ), Offset(puno.left + r * 0.03f, puno.top + r * 0.018f), Size(puno.width * 0.85f, r * 0.03f), CornerRadius(r * 0.015f))
        drawCircle(col(P.LATON), r * 0.045f, Offset(if (s > 0) puno.right else puno.left, pm.y))
        if (!atras) destelloAbismo(en(5.6, r * 0.95f), 1.4f * u, a, k)
        if (a.lumbre && !atras) drawPath(llanta, a.filo(0.5f * k), style = Stroke(1.2f * u))
    }

    /** El manómetro de la bomba en [c], de radio [r]: el bisel de latón, la esfera de papel, las marcas sin cifras, la aguja y el reflejo. */
    private fun DrawScope.manometro(c: Offset, r: Float, a: AireAbismo, k: Float) {
        val u = density
        fun col(cc: Color, f: Float = 0f) = a.dia(cc, f, k)
        drawCircle(a.sombra(0.35f), r * 1.05f, c + Offset(r * 0.15f, r * 0.18f))
        drawCircle(col(P.LATON), r, c)
        drawArc(col(P.LATON_LUZ), 180f, 120f, false, Offset(c.x - r * 0.88f, c.y - r * 0.88f), Size(r * 1.76f, r * 1.76f), style = Stroke(r * 0.1f))
        drawArc(col(P.LATON_OSC), 0f, 120f, false, Offset(c.x - r * 0.9f, c.y - r * 0.9f), Size(r * 1.8f, r * 1.8f), style = Stroke(r * 0.1f))
        val cara = r * 0.76f
        drawCircle(col(mezcla(P.PAPEL, Color.White, 0.3f)), cara, c)
        drawCircle(a.sombra(0.2f), cara, c + Offset(r * 0.05f, r * 0.08f), style = Stroke(r * 0.1f))
        for (j in 0..10) {
            val ang = Math.toRadians(135.0 + j * 27.0)
            val p0 = Offset(c.x + cara * 0.72f * cos(ang).toFloat(), c.y + cara * 0.72f * sin(ang).toFloat())
            val p1 = Offset(c.x + cara * 0.9f * cos(ang).toFloat(), c.y + cara * 0.9f * sin(ang).toFloat())
            drawLine(col(P.TINTA).copy(alpha = 0.7f), p0, p1, (if (j % 5 == 0) 1.2f else 0.6f) * u)
        }
        drawArc(col(P.MINIO).copy(alpha = 0.85f), 330f, 75f, false, Offset(c.x - cara * 0.8f, c.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(1.3f * u))
        val ang = Math.toRadians(135.0 + 270.0 * 0.36)
        drawLine(col(P.TINTA), c, Offset(c.x + cara * 0.75f * cos(ang).toFloat(), c.y + cara * 0.75f * sin(ang).toFloat()), 1.2f * u, StrokeCap.Round)
        drawCircle(col(P.LATON), 1.6f * u, c)
        if (a.oscuro) drawArc(a.brillo(0.8f, k), 10f, 60f, false, Offset(c.x - cara * 0.85f, c.y - cara * 0.85f), Size(cara * 1.7f, cara * 1.7f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        else drawArc(luzMueble(0.7f), 195f, 70f, false, Offset(c.x - cara * 0.8f, c.y - cara * 0.8f), Size(cara * 1.6f, cara * 1.6f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
    }
}
