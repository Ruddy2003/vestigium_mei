package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * Lo de arriba del andamio, el bodegón de la boca de la cueva, en tres grupos. En medio manda la piel de reno tensada en su bastidor
 * de varas, apoyada en la pared y a medio raspar: lo raspado, claro; lo que falta, la membrana oscura con la grasa; el raspador
 * de sílex enmangado en asta, apoyado donde se dejó el trabajo. A la izquierda, las tiras de carne que se secan colgadas de la viga
 * al humo de la lámpara de grasa de caliza (sin mango: no es la de arenisca del Ideario). A la derecha, juntos, el haz de azagayas
 * apoyado en la pared, que rompe la línea de arriba, el cesto de corteza colmado de terrones de ocre a su pie y el manojo de
 * hierbas colgado.
 */
internal object CoronaEstanteCueva {
    /** Cuánto están por delante de la pared la piel y lo de la plataforma, en anchos. */
    private const val PIEL = 0.035f
    private const val COSAS = 0.1f
    /** La membrana que falta por raspar y la carne ya raspada, limpia. */
    private val MEMBRANA = Color(0xFF7A4434)
    private val LIMPIA = Color(0xFFDDBB92)
    /** La raya donde se dejó el raspado, de arriba abajo, en fracciones de la caja de la piel: a su izquierda, lo limpio. */
    private val RAYA = listOf(0.8f to -0.05f, 0.74f to 0.08f, 0.68f to 0.2f, 0.64f to 0.32f, 0.6f to 0.44f, 0.52f to 0.56f, 0.46f to 0.68f,
        0.42f to 0.8f, 0.36f to 0.92f, 0.32f to 1.05f)

    /** Lo que queda detrás de la viga: la piel en su bastidor y el haz de azagayas. */
    fun DrawScope.detras(g: MarcoCueva) {
        conManchaCueva(g, PIEL) { s ->
            azagayas(g, s)
            bastidor(g, s)
        }
    }

    /** Lo de delante: lo que pisa la plataforma y lo que cuelga de la viga. */
    fun DrawScope.delante(g: MarcoCueva) {
        conManchaCueva(g, COSAS) { s ->
            carne(g, s)
            hierbas(g, s)
            cestoOcres(g, s)
            lampara(g, s)
            raspador(g, s)
        }
        // La llama de la lámpara, con su propio color (no la toca la mancha de la boca).
        if (g.a.lampara > 0f) {
            val w = g.w
            val lp = g.lampara
            mancha(lp, w * 0.09f, P.LLAMA.copy(alpha = 0.5f * g.a.lampara))
            llamaCueva(Offset(lp.x + w * 0.006f, lp.y + w * 0.01f), w * 0.06f * (0.5f + 0.5f * g.a.lampara), w * 0.013f, 3)
        }
    }

    // ---------------------------------------------------------------- la piel tensada

    /**
     * La piel de reno tensada en su bastidor: cuatro varas con su corteza, atadas en las esquinas (se pasan del cruce); la piel con
     * su contorno de animal (las cuatro patas tiradas hacia las esquinas, el cuello arriba y la cola abajo), vista del lado de la
     * carne y a medio raspar: de un lado de una raya quebrada en diagonal, la carne limpia y clara con las pasadas del raspador; del
     * otro, la membrana parda rojiza con la grasa pegada; en la raya, el borde que se levanta y las virutas. El canto, más oscuro y
     * reseco; la lazada de tiento que la tensa contra las varas desde los ojales del borde. Su sombra en la pared.
     */
    private fun DrawScope.bastidor(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        fun q(x: Float, y: Float) = Offset(w * x, w * y)
        val pie = g.apoyo / w - 0.012f
        val l = g.luz(w * 0.55f, w * 0.32f, PIEL, s)
        // Las cuatro varas: izquierda, derecha, arriba y abajo.
        val varas = listOf(
            q(0.37f, pie) to q(0.388f, 0.1f), q(0.748f, pie) to q(0.728f, 0.095f),
            q(0.33f, 0.128f) to q(0.78f, 0.118f), q(0.342f, pie - 0.03f) to q(0.775f, pie - 0.027f))
        // La sombra del bastidor y de la piel en la pared (a la derecha y abajo).
        val sx = w * 0.016f
        val puntos = PIEL_PUNTOS.map { (x, y) -> q(x, y) }
        val contorno = cerradaCueva(puntos)
        drawPath(corridoCueva(contorno, sx, w * 0.014f), l.s(0.32f))
        varas.forEach { (a, b) -> drawLine(l.s(0.3f), a + Offset(sx, w * 0.012f), b + Offset(sx, w * 0.012f), w * 0.016f, StrokeCap.Round) }
        // Las varas de atrás primero (la de arriba y la de abajo), luego la piel, luego las de los lados.
        listOf(varas[2], varas[3]).forEachIndexed { i, (a, b) -> vara(a, b, w * 0.015f, g.luz((a + b) / 2f, PIEL, s), 900 + i) }
        val lp = g.luz(w * 0.55f, w * 0.3f, PIEL, s)
        // Lo que falta por raspar: la membrana, parda rojiza y oscura.
        drawPath(contorno, lp.c(MEMBRANA))
        clipPath(contorno) {
            val b = contorno.getBounds()
            fun en(fx: Float, fy: Float) = Offset(b.left + b.width * fx, b.top + b.height * fy)
            // La grasa que queda pegada a la membrana, amarillenta, y unas manchas más hondas.
            listOf(Triple(0.74f, 0.6f, 0.1f), Triple(0.7f, 0.22f, 0.07f), Triple(0.86f, 0.42f, 0.06f), Triple(0.6f, 0.86f, 0.07f)).forEachIndexed { i, (fx, fy, r) ->
                drawPath(piedraCueva(en(fx, fy), b.width * r * 0.8f, b.height * r * 0.6f, 906 + i, 9, 0.35f), lp.c(mezcla(P.PIEL, P.OCRE_AMAR, 0.3f)).copy(alpha = 0.2f))
                drawPath(piedraCueva(en(fx + 0.04f, fy + 0.05f), b.width * r * 0.7f, b.height * r * 0.5f, 916 + i, 8, 0.35f), lp.c(P.CARNE).copy(alpha = 0.32f))
            }
            // Lo raspado: la carne limpia y clara, de un lado de la raya donde se dejó el trabajo (en diagonal, quebrada).
            val raya = RAYA.mapIndexed { i, (fx, fy) -> en(fx + 0.025f * (azar(920, i) - 0.5f), fy) }
            val raspado = Path().apply {
                moveTo(b.left - w * 0.02f, b.top - w * 0.02f)
                raya.forEach { lineTo(it.x, it.y) }
                lineTo(b.left - w * 0.02f, b.bottom + w * 0.02f); close()
            }
            drawPath(raspado, lp.c(LIMPIA))
            // Las pasadas del raspador en lo limpio, todas en el mismo sentido, más juntas cerca de la raya.
            val pasadas = Path(); val claras = Path()
            for (k in 0 until 34) {
                val fy = 0.04f + 0.92f * azar(913, k)
                val borde = raya[(fy * (raya.size - 1)).toInt().coerceIn(0, raya.size - 1)]
                val x = borde.x - b.width * (0.03f + 0.42f * azar(914, k) * azar(915, k))
                val y = b.top + b.height * fy
                val p = if (k % 3 == 0) claras else pasadas
                p.moveTo(x, y); p.lineTo(x - w * 0.022f, y - w * 0.014f)
            }
            drawPath(pasadas, lp.c(mezcla(LIMPIA, P.PIEL_OSC, 0.45f)).copy(alpha = 0.4f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            drawPath(claras, lp.b(0.5f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            // El borde de la membrana que se levanta en la raya: su sombra sobre lo limpio y su filo de luz.
            val labio = Path().apply { raya.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }
            drawPath(corridoCueva(labio, -w * 0.004f, w * 0.002f), lp.s(0.4f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
            drawPath(corridoCueva(labio, w * 0.002f, 0f), lp.c(mezcla(MEMBRANA, P.PIEL, 0.45f)), style = Stroke(w * 0.003f, cap = StrokeCap.Round))
            // Las virutas de membrana que quedaron junto a la raya.
            for (k in 0 until 6) {
                val p = raya[1 + k] + Offset(-w * (0.01f + 0.012f * azar(921, k)), w * 0.008f * (azar(922, k) - 0.5f))
                drawOval(lp.c(MEMBRANA).copy(alpha = 0.85f), Offset(p.x - w * 0.005f, p.y - w * 0.0025f), Size(w * 0.01f, w * 0.005f))
            }
            // El canto reseco, más oscuro, y la luz y la sombra de la piel tirante.
            drawPath(contorno, lp.c(P.PIEL_OSC).copy(alpha = 0.5f), style = Stroke(w * 0.018f))
            drawPath(bordeCueva(contorno, w * 0.014f, w * 0.016f), lp.s(0.3f))
            drawPath(bordeCueva(contorno, -w * 0.008f, -w * 0.008f), lp.b(0.42f))
        }
        drawPath(contorno, lp.c(P.PIEL_OSC), style = Stroke(1.2f * u, join = StrokeJoin.Round))
        // La lazada: de cada ojal del borde sale un tiento hasta la vara más cercana.
        lazada(g, puntos, varas, s)
        // Las varas de los lados, delante, y las ataduras de las esquinas.
        listOf(varas[0], varas[1]).forEachIndexed { i, (a, b) -> vara(a, b, w * 0.016f, g.luz((a + b) / 2f, PIEL, s), 902 + i) }
        listOf(q(0.386f, 0.126f), q(0.73f, 0.12f), q(0.372f, pie - 0.03f), q(0.746f, pie - 0.027f)).forEachIndexed { i, p ->
            nudoEsquina(p, w * 0.02f, g.luz(p, PIEL, s), i)
        }
    }

    /**
     * El contorno de la piel dentro del bastidor (en anchos): las cuatro patas tiradas hacia las esquinas, el cuello arriba y la
     * cola abajo; entre las patas, el borde se mete hacia dentro.
     */
    private val PIEL_PUNTOS = listOf(0.555f to 0.142f, 0.585f to 0.17f, 0.64f to 0.182f, 0.684f to 0.158f, 0.712f to 0.138f, 0.704f to 0.182f,
        0.68f to 0.232f, 0.668f to 0.3f, 0.676f to 0.376f, 0.698f to 0.432f, 0.718f to 0.468f, 0.68f to 0.46f, 0.62f to 0.44f, 0.572f to 0.448f,
        0.556f to 0.476f, 0.538f to 0.448f, 0.48f to 0.442f, 0.43f to 0.458f, 0.398f to 0.472f, 0.41f to 0.428f, 0.432f to 0.37f, 0.44f to 0.3f,
        0.43f to 0.232f, 0.41f to 0.188f, 0.398f to 0.142f, 0.43f to 0.162f, 0.48f to 0.182f, 0.527f to 0.17f)

    /** Una vara del bastidor: un palo redondo de [a] a [b], con su luz, su sombra, sus nudos y la punta cortada. */
    private fun DrawScope.vara(a: Offset, b: Offset, grueso: Float, l: LuzCueva, semilla: Int) {
        val u = density
        val p = Path().apply { moveTo(a.x, a.y); lineTo(b.x, b.y) }
        drawPath(p, l.c(P.PINO), style = Stroke(grueso, cap = StrokeCap.Round))
        val n = Offset(b.y - a.y, a.x - b.x).let { it / maxOf(1f, it.getDistance()) }
        // El lado de la luz (arriba a la izquierda) y el de la sombra.
        val signo = if (n.x + n.y < 0f) 1f else -1f
        drawPath(corridoCueva(p, n.x * grueso * 0.28f * signo, n.y * grueso * 0.28f * signo), l.b(0.5f), style = Stroke(grueso * 0.3f, cap = StrokeCap.Round))
        drawPath(corridoCueva(p, -n.x * grueso * 0.3f * signo, -n.y * grueso * 0.3f * signo), l.s(0.35f), style = Stroke(grueso * 0.32f, cap = StrokeCap.Round))
        for (k in 0 until 3) {
            val t = 0.2f + 0.3f * k + 0.08f * azar(semilla, k)
            val c = a + (b - a) * t
            drawOval(l.c(P.CORTEZA_OSC).copy(alpha = 0.75f), Offset(c.x - grueso * 0.22f, c.y - grueso * 0.22f), Size(grueso * 0.44f, grueso * 0.44f))
        }
        drawCircle(l.c(P.PINO_LUZ), grueso * 0.45f, b)
        drawCircle(l.c(P.PINO_OSC).copy(alpha = 0.6f), grueso * 0.45f, b, style = Stroke(0.6f * u))
        if (l.k > 0.05f) drawPath(corridoCueva(p, n.x * grueso * 0.4f * signo, n.y * grueso * 0.4f * signo), l.filo(0.6f), style = Stroke(1.2f * u))
    }

    /** La lazada: de cada ojal del borde de la piel (los puntos del contorno) sale un tiento hasta la vara más cercana. */
    private fun DrawScope.lazada(g: MarcoCueva, puntos: List<Offset>, varas: List<Pair<Offset, Offset>>, s: Float?) {
        val w = g.w; val u = density
        val c = puntos.fold(Offset.Zero) { acc, p -> acc + p } / puntos.size.toFloat()
        val tiento = Path(); val ojales = Path()
        puntos.forEachIndexed { k, p ->
            // El ojal, un poco hacia dentro del borde.
            val o = p + (c - p) * 0.06f
            val v = varas.map { (p0, p1) -> cercano(o, p0, p1) }.minByOrNull { (it - o).getDistance() } ?: return@forEachIndexed
            if ((v - o).getDistance() < w * 0.012f) return@forEachIndexed
            val d = v - o
            val z = v + Offset(-d.y, d.x) / maxOf(1f, d.getDistance()) * (if (k % 2 == 0) 1f else -1f) * w * 0.01f
            tiento.moveTo(o.x, o.y); tiento.lineTo(z.x, z.y)
            ojales.addOval(Rect(o.x - w * 0.004f, o.y - w * 0.004f, o.x + w * 0.004f, o.y + w * 0.004f))
        }
        val l = g.luz(c, PIEL, s)
        drawPath(ojales, l.c(P.PIEL_OSC))
        drawPath(corridoCueva(tiento, 1f * u, 1.2f * u), l.s(0.4f), style = Stroke(1.7f * u, cap = StrokeCap.Round))
        drawPath(tiento, l.c(P.CUERO), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(tiento, -0.4f * u, -0.4f * u), l.b(0.45f), style = Stroke(0.5f * u, cap = StrokeCap.Round))
    }

    private fun cercano(p: Offset, a: Offset, b: Offset): Offset {
        val ab = b - a
        val t = (((p.x - a.x) * ab.x + (p.y - a.y) * ab.y) / maxOf(1e-3f, ab.x * ab.x + ab.y * ab.y)).coerceIn(0.05f, 0.95f)
        return a + ab * t
    }

    /** Una atadura de esquina del bastidor: unas vueltas cruzadas de tiento y su cabo. */
    private fun DrawScope.nudoEsquina(c: Offset, r: Float, l: LuzCueva, i: Int) {
        val u = density
        // Tres vueltas en diagonal, todas en el mismo sentido (en aspa se leerían como una cruz), y una de amarre.
        val p = Path().apply {
            for (j in 0 until 3) {
                val d = (j - 1f) * r * 0.42f
                moveTo(c.x - r * 0.7f + d, c.y - r * 0.6f); lineTo(c.x + r * 0.5f + d, c.y + r * 0.6f)
            }
            moveTo(c.x - r * 0.9f, c.y); lineTo(c.x + r * 0.9f, c.y + r * 0.15f)
        }
        drawPath(corridoCueva(p, 1f * u, 1.2f * u), l.s(0.4f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(p, l.c(P.CUERO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(p, -0.5f * u, -0.6f * u), l.b(0.5f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        if (i % 2 == 0) drawLine(l.c(P.CUERO), c, c + Offset(r * 0.4f, r * 1.4f), 1.6f * u, StrokeCap.Round)
    }

    /**
     * El raspador con que se trabaja la piel: una lasca de sílex de aristas vivas y frente de raspar en bisel, enmangada en un trozo
     * de asta y atada con tendón, apoyada en el bastidor y recostada en la piel justo en la raya donde se dejó el raspado.
     */
    private fun DrawScope.raspador(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val pie = Offset(w * 0.488f, w * 0.482f)
        val cabeza = Offset(w * 0.534f, w * 0.398f)
        val l = g.luz((pie + cabeza) / 2f, COSAS, s)
        val dir = (cabeza - pie) / maxOf(1f, (cabeza - pie).getDistance())
        val n = Offset(-dir.y, dir.x)
        val grueso = w * 0.014f
        // La sombra en la piel, a la derecha y abajo.
        drawLine(l.s(0.4f), pie + Offset(w * 0.012f, 0f), cabeza + Offset(w * 0.012f, w * 0.01f), grueso, StrokeCap.Round)
        // El mango de asta: nudoso, claro, con su luz y su sombra.
        val asta = mezcla(P.HUESO, P.PINO_OSC, 0.3f)
        drawLine(l.c(asta), pie, cabeza - dir * w * 0.012f, grueso, StrokeCap.Round)
        drawLine(l.s(0.35f), pie + n * grueso * 0.28f, cabeza - dir * w * 0.012f + n * grueso * 0.28f, grueso * 0.36f, StrokeCap.Round)
        drawLine(l.b(0.55f), pie - n * grueso * 0.25f, cabeza - dir * w * 0.014f - n * grueso * 0.25f, grueso * 0.22f, StrokeCap.Round)
        for (k in 0 until 3) {
            val c = pie + (cabeza - pie) * (0.18f + 0.22f * k)
            drawCircle(l.c(mezcla(asta, P.PINO_OSC, 0.4f)), grueso * 0.2f, c - n * grueso * 0.42f)
        }
        // La lasca de sílex: piedra de aristas vivas, más ancha que el mango, con su frente de raspar en bisel; las caras de la talla.
        fun p(a: Float, b: Float) = cabeza + dir * (w * a) + n * (grueso * b)
        fun poli(cerrar: Boolean, q: List<Offset>) = Path().apply {
            moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y); if (cerrar) close()
        }
        val lasca = poli(true, listOf(p(-0.016f, 0.6f), p(0.004f, 1.1f), p(0.022f, 0.85f), p(0.028f, 0f), p(0.022f, -0.9f), p(0.002f, -1.05f), p(-0.016f, -0.6f)))
        drawPath(corridoCueva(lasca, w * 0.006f, w * 0.005f), l.s(0.4f))
        drawPath(lasca, l.c(P.SILEX))
        drawPath(poli(true, listOf(p(0.002f, -1.05f), p(0.022f, -0.9f), p(0.012f, -0.1f), p(-0.006f, -0.3f))), l.s(0.34f))
        drawPath(poli(true, listOf(p(0.004f, 1.1f), p(0.022f, 0.85f), p(0.014f, 0.2f), p(-0.004f, 0.4f))), l.b(0.45f))
        // El filo de raspar, gastado y claro.
        drawPath(poli(false, listOf(p(0.022f, 0.85f), p(0.028f, 0f), p(0.022f, -0.9f))), l.c(mezcla(P.SILEX, P.CAOLIN, 0.45f)), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        // La atadura de tendón: unas vueltas sobre la junta.
        for (k in 0 until 3) {
            val c = cabeza - dir * w * (0.008f + 0.005f * k)
            drawLine(l.c(P.CUERO), c + n * grueso * 0.62f, c - n * grueso * 0.62f, 1.6f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- el haz de azagayas

    /** Cuatro azagayas apoyadas en la pared: el astil de madera y la punta de asta atada con tendón y untada de resina. */
    private fun DrawScope.azagayas(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val pie = g.apoyo - w * 0.012f
        listOf(Triple(0.868f, 0.94f, 0.02f), Triple(0.884f, 0.968f, 0.045f), Triple(0.9f, 0.992f, 0.01f), Triple(0.912f, 1.02f, 0.06f)).forEachIndexed { i, (x0, x1, y1) ->
            val a = Offset(w * x0, pie); val b = Offset(w * x1, w * y1)
            val l = g.luz((a + b) / 2f, PIEL, s)
            val dir = (b - a) / maxOf(1f, (b - a).getDistance())
            val punta = b - dir * w * 0.075f
            drawLine(l.s(0.3f), a + Offset(w * 0.01f, w * 0.006f), punta + Offset(w * 0.01f, w * 0.006f), w * 0.008f, StrokeCap.Round)
            drawLine(l.c(mezcla(P.PINO, P.CORTEZA, 0.3f + 0.15f * i)), a, punta, w * 0.008f, StrokeCap.Round)
            drawLine(l.b(0.4f), a + Offset(-w * 0.002f, 0f), punta + Offset(-w * 0.002f, 0f), w * 0.0025f, StrokeCap.Round)
            // La punta de asta: alargada y aguzada, con su bisel atado al astil.
            val n = Offset(-dir.y, dir.x)
            val pt = Path().apply {
                moveTo(punta.x + n.x * w * 0.005f, punta.y + n.y * w * 0.005f)
                lineTo(b.x, b.y)
                lineTo(punta.x - n.x * w * 0.005f, punta.y - n.y * w * 0.005f)
                close()
            }
            drawPath(pt, l.c(P.HUESO))
            drawLine(l.c(P.HUESO_OSC), punta + dir * w * 0.01f, b - dir * w * 0.006f, 0.8f * u)
            // El tendón de la junta, con la resina oscura.
            drawLine(l.c(Color(0xFF3A2418)), punta - dir * w * 0.012f, punta + dir * w * 0.012f, w * 0.011f, StrokeCap.Butt)
            drawLine(l.c(P.CUERO).copy(alpha = 0.6f), punta - dir * w * 0.006f + n * w * 0.004f, punta + dir * w * 0.002f - n * w * 0.004f, 0.8f * u)
        }
    }

    // ---------------------------------------------------------------- lo de la plataforma

    /**
     * La lámpara de grasa: una piedra de caliza ahuecada, redonda y sin mango, con el sebo dentro y la mecha de liquen, el hollín
     * en el borde.
     */
    private fun DrawScope.lampara(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val pie = g.apoyo - w * 0.004f
        val cx = g.lampara.x
        val rx = w * 0.062f
        val top = pie - w * 0.05f
        val l = g.luz(cx, top, COSAS, s)
        drawOval(l.s(0.42f), Offset(cx - rx * 0.9f + (if (g.a.oscuro) 0f else w * 0.012f), pie - w * 0.008f), Size(rx * 2.1f, w * 0.016f))
        val cuerpo = cerradaCueva(listOf(Offset(cx - rx, top + w * 0.006f), Offset(cx - rx * 0.92f, top + (pie - top) * 0.6f), Offset(cx - rx * 0.55f, pie),
            Offset(cx + rx * 0.5f, pie + w * 0.001f), Offset(cx + rx * 0.95f, top + (pie - top) * 0.55f), Offset(cx + rx * 1.02f, top + w * 0.004f), Offset(cx, top + w * 0.014f)))
        bultoCueva(cuerpo, l.c(mezcla(P.CALIZA, P.HUESO_OSC, 0.3f)), l, -1f, 1.1f, 0.4f)
        clipPath(cuerpo) {
            for (k in 0 until 9) drawCircle(l.c(P.HUESO_OSC).copy(alpha = 0.4f), (0.6f + 0.8f * azar(950, k)) * u, Offset(cx + rx * (azar(951, k) - 0.5f) * 1.6f, top + (pie - top) * (0.3f + 0.6f * azar(952, k))))
        }
        // La boca, vista un poco desde arriba: el borde, el sebo, la mecha y el hollín.
        val boca = Rect(cx - rx * 0.95f, top - w * 0.008f, cx + rx * 0.98f, top + w * 0.016f)
        drawOval(l.c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.3f)), boca.topLeft, boca.size)
        drawOval(l.c(Color(0xFFE6D6A6)), Offset(boca.left + boca.width * 0.12f, boca.top + boca.height * 0.2f), Size(boca.width * 0.76f, boca.height * 0.6f))
        drawOval(P.HOLLIN.copy(alpha = 0.45f), Offset(boca.left + boca.width * 0.55f, boca.top + boca.height * 0.05f), Size(boca.width * 0.4f, boca.height * 0.45f))
        drawLine(l.c(P.CARBON), Offset(cx + w * 0.004f, top + w * 0.004f), Offset(cx + w * 0.01f, top - w * 0.004f), 1.6f * u, StrokeCap.Round)
        if (g.a.lampara > 0f) drawOval(P.LLAMA.copy(alpha = 0.55f * g.a.lampara), Offset(boca.left + boca.width * 0.2f, boca.top + boca.height * 0.15f), Size(boca.width * 0.6f, boca.height * 0.5f))
    }

    /**
     * El cesto de los ocres, sobre la plataforma al pie del haz de azagayas: una artesa de corteza de abedul cosida con raíz, con
     * las puntas que suben, colmada de terrones de ocre rojo, amarillo y anaranjado (lo que se muele para pintar).
     */
    private fun DrawScope.cestoOcres(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val pie = g.apoyo - w * 0.004f
        val cx = w * 0.83f
        val l = g.luz(cx, pie - w * 0.03f, COSAS, s)
        val arriba = w * 0.1f; val abajo = arriba * 0.7f
        val yPunta = pie - w * 0.075f; val yMedio = pie - w * 0.045f
        fun borde(x: Float): Float { val t = ((x - cx) / arriba).coerceIn(-1f, 1f); val t2 = t * t; return yMedio - (yMedio - yPunta) * t2 * t2 }
        drawOval(l.s(0.45f), Offset(cx - arriba + w * 0.014f, pie - w * 0.008f), Size(arriba * 2.05f, w * 0.018f))
        // Los terrones, colmados por encima del borde.
        val terrones = listOf(Triple(-0.55f, 0.6f, P.OCRE_ROJO), Triple(-0.25f, 1.05f, P.OCRE_AMAR), Triple(0.1f, 1.2f, P.OCRE_ROJO), Triple(0.42f, 0.9f, P.OCRE_NAR),
            Triple(-0.05f, 0.55f, P.OCRE_NAR), Triple(0.62f, 0.5f, P.OCRE_AMAR), Triple(-0.7f, 0.25f, P.OCRE_AMAR))
        terrones.forEachIndexed { i, (fx, fy, col) ->
            val c = Offset(cx + arriba * fx, yMedio - w * 0.016f * fy)
            val r = w * (0.014f + 0.005f * azar(990, i))
            val f = poligonoCueva(c, r * 1.2f, r * 0.85f, 991 + i, 6)
            bultoCueva(f, l.c(col), l, -1f, 1.3f, 0.42f)
            drawLine(l.b(0.5f), Offset(c.x - r * 0.6f, c.y - r * 0.3f), Offset(c.x + r * 0.2f, c.y - r * 0.6f), r * 0.22f, StrokeCap.Round)
        }
        val f = Path().apply {
            moveTo(cx - arriba, yPunta)
            for (i in 1..14) { val x = cx - arriba + 2 * arriba * i / 14f; lineTo(x, borde(x)) }
            quadraticTo(cx + arriba * 0.95f, pie - w * 0.02f, cx + abajo, pie)
            lineTo(cx - abajo, pie)
            quadraticTo(cx - arriba * 0.95f, pie - w * 0.02f, cx - arriba, yPunta)
            close()
        }
        bultoCueva(f, l.c(mezcla(P.ABEDUL, P.CUERO, 0.18f)), l, -1f, 1.1f, 0.3f)
        clipPath(f) {
            val lent = Path()
            for (k in 0 until 16) {
                val y = yPunta + (pie - yPunta) * azar(992, k); val x = cx - arriba + 2 * arriba * azar(993, k)
                lent.moveTo(x, y); lent.lineTo(x + arriba * (0.08f + 0.12f * azar(994, k)), y + 0.3f * u)
            }
            drawPath(lent, l.c(P.LENTICELA).copy(alpha = 0.66f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            // El polvo rojo que mancha la corteza por fuera.
            drawPath(piedraCueva(Offset(cx + arriba * 0.2f, yMedio + w * 0.008f), arriba * 0.5f, w * 0.008f, 995, 9, 0.4f), l.c(P.OCRE_ROJO).copy(alpha = 0.5f))
            drawRect(l.s(0.24f), Offset(cx + arriba * 0.45f, yPunta), Size(arriba * 0.6f, pie - yPunta))
        }
        listOf(-1f, 1f).forEach { lado ->
            val x0 = cx + lado * arriba * 0.84f
            val punt = Path()
            for (j in 0 until 4) {
                val t = (j + 0.5f) / 4f
                val y = borde(x0) + (pie - borde(x0)) * t * 0.9f
                val xx = x0 + (cx + lado * abajo * 0.9f - x0) * t
                punt.moveTo(xx - 2f * u, y - 0.8f * u); punt.lineTo(xx + 2f * u, y + 0.8f * u)
            }
            drawPath(punt, l.c(P.CORTEZA_OSC), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        }
        val aro = Path().apply { moveTo(cx - arriba, yPunta); for (i in 1..14) { val x = cx - arriba + 2 * arriba * i / 14f; lineTo(x, borde(x)) } }
        drawPath(aro, l.c(mezcla(P.PINO, P.CORTEZA, 0.4f)), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(aro, 0f, -0.8f * u), l.b(0.5f), style = Stroke(1f * u, cap = StrokeCap.Round))
        if (l.k > 0.05f) drawPath(corridoCueva(aro, 0f, -1.2f * u), l.filo(0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- lo colgado de la viga

    /**
     * Las tiras de carne que se secan al humo de la lámpara: anchas y planas, de un rojo pardo hondo, cada una colgada de la viga
     * con su tiento, retorcida (la cara de atrás asoma más oscura donde se tuerce), con la grasa en el canto y su sombra en la pared.
     */
    private fun DrawScope.carne(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val y0 = g.viga + w * 0.017f
        listOf(Triple(0.1f, 0.15f, 0.03f), Triple(0.148f, 0.19f, 0.034f), Triple(0.2f, 0.13f, 0.028f), Triple(0.246f, 0.165f, 0.032f)).forEachIndexed { i, (x, largo, ancho0) ->
            val a = Offset(w * x, y0)
            val l = g.luz(a.x, a.y + w * largo * 0.5f, COSAS, s)
            val top = a.y + w * 0.014f
            val an = w * ancho0
            val lado = if (i % 2 == 0) 1f else -1f
            val tira = Path().apply {
                moveTo(a.x - an * 0.5f, top)
                lineTo(a.x + an * 0.5f, top)
                cubicTo(a.x + an * 0.55f, top + w * largo * 0.3f, a.x + an * (0.1f + 0.3f * lado), top + w * largo * 0.55f, a.x + an * (0.42f + 0.1f * lado), top + w * largo * 0.8f)
                lineTo(a.x + an * 0.12f * lado, top + w * largo)
                lineTo(a.x - an * 0.42f, top + w * largo * 0.84f)
                cubicTo(a.x - an * (0.2f - 0.3f * lado), top + w * largo * 0.55f, a.x - an * 0.55f, top + w * largo * 0.3f, a.x - an * 0.5f, top)
                close()
            }
            drawPath(corridoCueva(tira, w * 0.012f, w * 0.008f), l.s(0.32f))
            bultoCueva(tira, l.c(mezcla(P.CARNE, Color(0xFF4A1A16), 0.3f * azar(980, i))), l, -1f, 1f, 0.26f)
            clipPath(tira) {
                // Donde se tuerce, asoma la cara de atrás, más oscura.
                val vuelta = Path().apply {
                    moveTo(a.x - an, top + w * largo * (0.45f + 0.05f * lado)); lineTo(a.x + an, top + w * largo * (0.55f - 0.05f * lado))
                    lineTo(a.x + an, top + w * largo * (0.68f - 0.05f * lado)); lineTo(a.x - an, top + w * largo * (0.6f + 0.05f * lado)); close()
                }
                drawPath(vuelta, l.c(Color(0xFF3A1412)).copy(alpha = 0.7f))
                // La grasa del canto y las fibras.
                drawLine(l.c(Color(0xFFE2C29E)).copy(alpha = 0.7f), Offset(a.x - an * 0.4f, top + w * largo * 0.05f), Offset(a.x - an * 0.36f, top + w * largo * 0.42f), 1.6f * u, StrokeCap.Round)
                for (j in 0 until 4) drawLine(l.c(Color(0xFF9A3A2C)).copy(alpha = 0.5f), Offset(a.x - an * 0.2f + j * an * 0.14f, top + w * largo * 0.08f), Offset(a.x - an * 0.15f + j * an * 0.14f, top + w * largo * 0.4f), 0.7f * u)
            }
            // El tiento y el nudo en la viga.
            drawLine(l.c(P.CUERO_OSC), Offset(a.x, a.y - w * 0.004f), Offset(a.x, top + w * 0.004f), 1.6f * u)
            drawCircle(l.c(P.CUERO), 1.8f * u, Offset(a.x, top + w * 0.002f))
        }
    }

    /** El manojo de hierbas colgado boca abajo de la viga: los tallos atados arriba, las hojas y las flores secas abiertas abajo. */
    private fun DrawScope.hierbas(g: MarcoCueva, s: Float?) {
        val w = g.w; val u = density
        val a = Offset(w * 0.858f, g.viga + w * 0.018f)
        val l = g.luz(a.x, a.y + w * 0.08f, COSAS, s)
        drawLine(l.c(P.CUERO_OSC), a, a + Offset(0f, w * 0.022f), 1.3f * u)
        val atado = a + Offset(0f, w * 0.026f)
        val tallos = Path(); val hojas = Path(); val flores = Path()
        for (k in 0 until 13) {
            val dx = (k - 6) * w * 0.0042f
            val fin = atado + Offset(dx * 2.1f + w * 0.006f * sin(k * 1.9f), w * (0.11f + 0.04f * azar(990, k)))
            tallos.moveTo(atado.x + dx * 0.2f, atado.y); tallos.quadraticTo(atado.x + dx, atado.y + w * 0.05f, fin.x, fin.y)
            for (j in 1..3) {
                val p = atado + (fin - atado) * (0.3f + 0.22f * j) + Offset(dx * 0.1f * j, 0f)
                hojas.addOval(Rect(p.x - w * 0.006f, p.y - w * 0.0025f, p.x + w * 0.0015f, p.y + w * 0.0025f))
            }
            if (k % 2 == 0) flores.addOval(Rect(fin.x - w * 0.0045f, fin.y - w * 0.002f, fin.x + w * 0.0045f, fin.y + w * 0.0065f))
        }
        drawPath(corridoCueva(tallos, w * 0.008f, w * 0.005f), l.s(0.25f), style = Stroke(1.2f * u))
        drawPath(tallos, l.c(Color(0xFF8C8550)), style = Stroke(1.1f * u, cap = StrokeCap.Round))
        drawPath(hojas, l.c(mezcla(P.ENEBRO, Color(0xFF9A9A5E), 0.5f)))
        drawPath(flores, l.c(Color(0xFFD8B65A)))
        drawRoundRect(l.c(P.CUERO), Offset(atado.x - w * 0.009f, atado.y - w * 0.004f), Size(w * 0.018f, w * 0.009f), androidx.compose.ui.geometry.CornerRadius(w * 0.003f))
        drawRect(l.b(0.4f), Offset(atado.x - w * 0.009f, atado.y - w * 0.004f), Size(w * 0.018f, w * 0.0025f))
    }
}
