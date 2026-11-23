package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El bodegón de arriba del estante de Galia ([MuebleEstanteGalia]), en la imagen guardada. Sobre el dintel, la despensa de la
 * casa: manda la tinaja grande de barro negro bruñido, con su cordón apretado a dedo y la tapa de tablas con una piedra
 * encima; detrás, apoyada en el barro, el ánfora del vino de Italia con su tapón sellado; delante, el cubo de duelas con aros
 * de bronce y el cucharón; a la derecha, la pila de mantas de lana a cuadros, con la de arriba caída por el canto del dintel.
 * Colgados: el cuerno de beber de su correa, de una clavija; y de la viga, la ristra de cebollas, el jamón y la hoja de tocino.
 */
internal object CoronaEstanteGalia {
    private val P = PaletaGalia

    // ---------------------------------------------------------------- lo colgado

    fun DrawScope.colgado(a: AireGalia, apoyo: Float, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width
        cuerno(a, cerca, m)
        ristra(Offset(w * 0.564f, w * 0.162f), a, cerca, m)
        jamon(Offset(w * 0.663f, w * 0.162f), a, cerca, m)
        tocino(Offset(w * 0.743f, w * 0.162f), a, cerca, m)
    }

    /**
     * El cuerno de beber, colgado de su correa de una clavija del barro: la correa va del bocal a la contera y cuelga en uve;
     * el cuerno, de cuerno de buey pulido (crema con vetas pardas hacia la punta), con el bocal de bronce y la contera en botón.
     */
    private fun DrawScope.cuerno(a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val clavo = Offset(w * 0.125f, w * 0.212f)
        val k = cerca(clavo)
        val boca = Offset(w * 0.215f, w * 0.262f)
        val punta = Offset(w * 0.045f, w * 0.262f)
        // La forma: un cuerno grueso y curvo que se adelgaza hacia la punta, con la boca arriba a la derecha.
        val ctrl = Offset(w * 0.1f, w * 0.37f)
        val forma = Path()
        val n = 16
        val arriba = ArrayList<Offset>(); val abajo = ArrayList<Offset>()
        for (i in 0..n) {
            val t = i / n.toFloat()
            val s = 1f - t
            val p = punta * (s * s) + ctrl * (2 * s * t) + boca * (t * t)
            val d = (ctrl - punta) * (2 * s) + (boca - ctrl) * (2 * t)
            val l = d.getDistance().coerceAtLeast(1f)
            val nrm = Offset(-d.y / l, d.x / l)
            val g = w * (0.005f + 0.024f * Math.pow(t.toDouble(), 1.4).toFloat())
            arriba.add(p - nrm * g); abajo.add(p + nrm * g)
        }
        forma.moveTo(arriba[0].x, arriba[0].y)
        for (i in 1..n) forma.lineTo(arriba[i].x, arriba[i].y)
        for (i in n downTo 0) forma.lineTo(abajo[i].x, abajo[i].y)
        forma.close()
        // La correa: del bocal y de la contera sube a la clavija.
        val correa = a.dia(P.CUERO, 0f, k)
        val tc = Path().apply { moveTo(boca.x - w * 0.012f, boca.y - w * 0.008f); quadraticTo(w * 0.17f, w * 0.23f, clavo.x, clavo.y) }
        val tp = Path().apply { moveTo(punta.x + w * 0.01f, punta.y - w * 0.004f); quadraticTo(w * 0.09f, w * 0.24f, clavo.x, clavo.y) }
        // Las sombras en el barro, corridas abajo a la derecha (de día) o casi detrás (de noche).
        val dx = if (a.oscuro) w * 0.004f else w * 0.014f
        translate(dx, dx * 0.8f) {
            drawPath(forma, a.sombra(0.3f))
            drawPath(tc, a.sombra(0.25f), style = Stroke(2.2f * u))
            drawPath(tp, a.sombra(0.25f), style = Stroke(2.2f * u))
        }
        drawPath(tc, correa, style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(tp, correa, style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(forma, a.dia(P.CUERNO, 0f, k))
        clipPath(forma) {
            // Las vetas pardas, más hacia la punta, y el lomo a la luz y la panza en sombra.
            for (j in 0 until 7) {
                val t = 0.05f + 0.6f * azar(8500, j)
                val s = 1f - t
                val p = punta * (s * s) + ctrl * (2 * s * t) + boca * (t * t)
                drawLine(a.dia(P.CUERNO_OSC, 0f, k).copy(alpha = 0.55f - 0.5f * t), p - Offset(w * 0.01f, w * 0.03f), p + Offset(w * 0.006f, w * 0.03f), (1.4f + 2f * azar(8501, j)) * u)
            }
            drawRect(Brush.linearGradient(listOf(a.dia(P.CUERNO_OSC, 0f, k).copy(alpha = 0.7f), Color.Transparent), punta, punta + Offset(w * 0.07f, 0f)), Offset(w * 0.04f, w * 0.24f), Size(w * 0.18f, w * 0.12f))
            drawPath(Path().apply { moveTo(arriba[2].x, arriba[2].y); for (i in 3..n) lineTo(arriba[i].x, arriba[i].y) }, a.brillo(0.6f, k), style = Stroke(1.6f * u))
            drawPath(Path().apply { moveTo(abajo[3].x, abajo[3].y); for (i in 4..n) lineTo(abajo[i].x, abajo[i].y) }, a.sombra(0.4f), style = Stroke(3f * u))
        }
        luzDeLaPuertaGalia(forma, a, m, 0f)
        // El bocal de bronce, una banda que sigue la boca, y la contera.
        val nb = (abajo[n] - arriba[n])
        val b0 = arriba[n - 1]; val b1 = abajo[n - 1]
        drawLine(a.dia(P.BRONCE, 0f, k), b0 + (b0 - b1) * 0.08f, b1 + (b1 - b0) * 0.08f, w * 0.012f)
        drawLine(a.dia(P.BRONCE_LUZ, 0f, k), arriba[n] , abajo[n], w * 0.006f, StrokeCap.Round)
        drawLine(a.brillo(0.7f, k), b0, b0 + nb * 0.3f, 1.2f * u)
        drawCircle(a.dia(P.BRONCE, 0f, k), w * 0.0065f, punta)
        drawCircle(a.brillo(0.6f, k), w * 0.0025f, punta - Offset(w * 0.002f, w * 0.002f))
        // La clavija de roble del barro.
        drawCircle(a.sombra(0.45f), w * 0.009f, clavo + Offset(1f * u, 1.3f * u))
        drawCircle(a.dia(P.ROBLE, 0.05f, k), w * 0.008f, clavo)
        drawCircle(a.brillo(0.45f, k), w * 0.003f, clavo - Offset(w * 0.003f, w * 0.003f))
    }

    /** La ristra de cebollas: la trenza de paja colgada del gancho y las cebollas apiñadas, cada una con su luz y su cuello. */
    private fun DrawScope.ristra(gancho: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val k = cerca(gancho + Offset(0f, w * 0.08f))
        val largo = w * 0.15f
        val trenza = Path().apply { moveTo(gancho.x, gancho.y); quadraticTo(gancho.x + w * 0.006f, gancho.y + largo * 0.5f, gancho.x - w * 0.004f, gancho.y + largo) }
        drawPath(Path().apply { addPath(trenza, Offset(w * 0.01f, w * 0.008f)) }, a.sombra(0.25f), style = Stroke(w * 0.008f))
        drawPath(trenza, a.dia(P.JUNCO, 0f, k), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
        // Las cebollas, de abajo arriba y alternando lados (las de arriba tapan a las de abajo).
        for (i in 10 downTo 0) {
            val t = 0.16f + 0.084f * i
            val lado = if (i % 2 == 0) -1f else 1f
            val r = w * (0.0165f + 0.004f * azar(8510, i))
            val c = Offset(gancho.x + lado * w * (0.012f + 0.004f * azar(8511, i)) + w * 0.002f, gancho.y + largo * t)
            val sil = Path().apply {
                moveTo(c.x - r, c.y)
                cubicTo(c.x - r, c.y + r * 1.15f, c.x + r, c.y + r * 1.15f, c.x + r, c.y)
                cubicTo(c.x + r, c.y - r * 0.6f, c.x + r * 0.25f, c.y - r * 0.9f, c.x, c.y - r * 1.5f)
                cubicTo(c.x - r * 0.25f, c.y - r * 0.9f, c.x - r, c.y - r * 0.6f, c.x - r, c.y)
                close()
            }
            translate(w * 0.006f, w * 0.005f) { drawPath(sil, a.sombra(0.22f)) }
            drawPath(sil, a.dia(P.CEBOLLA, 0f, k))
            clipPath(sil) {
                drawCircle(a.dia(P.CEBOLLA_LUZ, 0f, k), r * 0.7f, c - Offset(r * 0.35f, r * 0.2f))
                drawCircle(a.sombra(0.3f), r * 1.1f, c + Offset(r * 0.75f, r * 0.5f))
                for (q in -1..1) drawLine(a.luz(P.ROJO_OSC, k).copy(alpha = 0.35f), Offset(c.x + q * r * 0.45f, c.y - r * 0.9f), Offset(c.x + q * r * 0.55f, c.y + r * 0.8f), 0.6f * u)
            }
            drawCircle(a.brillo(0.6f, k), r * 0.16f, c - Offset(r * 0.42f, r * 0.3f))
            luzDeLaPuertaGalia(sil, a, m, 0f)
        }
    }

    /** El jamón curado al humo, colgado por la caña de su cordel: la corteza ahumada, el corte con la carne y su grasa. */
    private fun DrawScope.jamon(gancho: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val k = cerca(gancho + Offset(0f, w * 0.1f))
        val cuerda = gancho + Offset(0f, w * 0.03f)
        drawLine(a.dia(P.JUNCO, -0.1f, k), gancho + Offset(w * 0.008f, w * 0.018f), cuerda, 1.4f * u)
        val top = cuerda.y; val bot = top + w * 0.14f
        val cx = cuerda.x
        val sil = Path().apply {
            moveTo(cx - w * 0.006f, top)
            lineTo(cx + w * 0.007f, top)
            cubicTo(cx + w * 0.012f, top + w * 0.04f, cx + w * 0.042f, top + w * 0.07f, cx + w * 0.036f, bot - w * 0.02f)
            quadraticTo(cx + w * 0.02f, bot + w * 0.004f, cx - w * 0.012f, bot - w * 0.004f)
            cubicTo(cx - w * 0.036f, bot - w * 0.02f, cx - w * 0.03f, top + w * 0.06f, cx - w * 0.008f, top + w * 0.03f)
            close()
        }
        translate(w * (if (a.oscuro) 0.004f else 0.013f), w * 0.01f) { drawPath(sil, a.sombra(0.3f)) }
        drawPath(sil, a.dia(mezcla(P.CARNE, P.ROBLE_OSC, 0.45f), 0f, k))
        clipPath(sil) {
            // La corteza ahumada: dorada al lado de la luz y negra de humo al otro, con su brillo de grasa.
            drawRect(a.dia(mezcla(P.CARNE, P.LANA_OCRE, 0.35f), 0f, k), Offset(cx - w * 0.04f, top), Size(w * 0.03f, bot - top))
            drawRect(a.sombra(0.4f), Offset(cx + w * 0.016f, top), Size(w * 0.03f, bot - top))
            // El corte de abajo: la carne roja, la orla de grasa y el hueso.
            val corte = Rect(cx - w * 0.028f, bot - w * 0.034f, cx + w * 0.026f, bot + w * 0.004f)
            drawOval(a.dia(P.GRASA, 0f, k), corte.topLeft, corte.size)
            drawOval(a.dia(P.CARNE, 0f, k), corte.topLeft + Offset(w * 0.004f, w * 0.004f), Size(corte.width - w * 0.008f, corte.height - w * 0.007f))
            drawCircle(a.dia(P.GRASA, 0.1f, k), w * 0.004f, corte.center + Offset(w * 0.006f, 0f))
            drawLine(a.brillo(0.5f, k), Offset(cx - w * 0.02f, top + w * 0.05f), Offset(cx - w * 0.026f, bot - w * 0.04f), 1.4f * u, StrokeCap.Round)
        }
        luzDeLaPuertaGalia(sil, a, m, 0f)
        // El cordel atado a la caña, con su nudo.
        drawLine(a.dia(P.JUNCO, -0.1f, k), Offset(cx - w * 0.008f, top + w * 0.006f), Offset(cx + w * 0.009f, top + w * 0.008f), 1.6f * u)
        drawCircle(a.dia(P.JUNCO, -0.15f, k), 1.8f * u, Offset(cx, top + w * 0.007f))
    }

    /** La hoja de tocino, colgada de un gancho por un agujero: la corteza ahumada y, en el canto, las vetas de grasa y carne. */
    private fun DrawScope.tocino(gancho: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val k = cerca(gancho + Offset(0f, w * 0.08f))
        val top = gancho.y + w * 0.02f
        val r = Rect(gancho.x - w * 0.026f, top, gancho.x + w * 0.03f, top + w * 0.11f)
        val sil = poligonoGalia(r.left + w * 0.004f, r.top, r.right, r.top + w * 0.003f, r.right - w * 0.003f, r.bottom, r.left, r.bottom - w * 0.004f)
        translate(w * (if (a.oscuro) 0.004f else 0.013f), w * 0.01f) { drawPath(sil, a.sombra(0.3f)) }
        drawPath(sil, a.dia(mezcla(P.CARNE, P.ROBLE, 0.5f), 0f, k))
        clipPath(sil) {
            drawRect(a.dia(mezcla(P.CARNE, P.LANA_OCRE, 0.3f), 0f, k), r.topLeft, Size(r.width * 0.35f, r.height))
            drawRect(a.sombra(0.35f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            // El canto de abajo, cortado: franjas de grasa y de carne.
            for (j in 0 until 4) drawRect(a.dia(if (j % 2 == 0) P.GRASA else P.CARNE, 0f, k), Offset(r.left, r.bottom - w * (0.006f + 0.0055f * j)), Size(r.width, w * 0.0055f))
            drawLine(a.brillo(0.45f, k), Offset(r.left + w * 0.006f, r.top + w * 0.012f), Offset(r.left + w * 0.006f, r.bottom - w * 0.03f), 1.2f * u)
        }
        luzDeLaPuertaGalia(sil, a, m, 0f)
        // El agujero con el gancho que lo pasa.
        val ag = Offset(gancho.x + w * 0.006f, top + w * 0.008f)
        drawCircle(a.luz(P.ROBLE_HONDO, k), w * 0.0045f, ag)
        drawLine(a.luz(P.HIERRO, k), gancho + Offset(w * 0.008f, w * 0.018f), ag, 1.6f * u)
    }

    // ---------------------------------------------------------------- lo de encima del dintel

    fun DrawScope.encima(a: AireGalia, apoyo: Float, ch: Float, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width
        anfora(Offset(w * 0.245f, apoyo), a, cerca, m)
        tinaja(Offset(w * 0.405f, apoyo), a, cerca, m)
        cubo(Offset(w * 0.555f, apoyo + w * 0.002f), a, cerca, m)
        mantas(apoyo, ch, a, cerca, m)
    }

    /**
     * El ánfora del vino de Italia, apoyada en el barro y ladeada: el pivote macizo, la panza larga, la carena del hombro, el
     * cuello alto con sus dos asas y el labio grueso, sellado con un tapón y una torta de cal. Con las rayas del torno en el
     * hombro, el roce del uso y su sombra en la pared.
     */
    private fun DrawScope.anfora(pie: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val l = w * 0.315f; val an = w * 0.074f
        val k = cerca(pie - Offset(0f, l * 0.5f))
        fun q(x: Float, y: Float) = Offset(pie.x + x * an, pie.y - y * l)
        val cuerpo = Path().apply {
            val p0 = q(-0.07f, 0f); moveTo(p0.x, p0.y)
            val a1 = q(-0.1f, 0.06f); lineTo(a1.x, a1.y)
            val b1 = q(-0.5f, 0.2f); val b2 = q(-0.52f, 0.5f); val b3 = q(-0.5f, 0.63f); cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
            val c1 = q(-0.46f, 0.69f); val c2 = q(-0.17f, 0.7f); quadraticTo(c1.x, c1.y, c2.x, c2.y)
            val d1 = q(-0.16f, 0.93f); lineTo(d1.x, d1.y)
            val e1 = q(-0.22f, 0.935f); lineTo(e1.x, e1.y)
            val e2 = q(-0.22f, 1f); lineTo(e2.x, e2.y)
            val e3 = q(0.22f, 1f); lineTo(e3.x, e3.y)
            val e4 = q(0.22f, 0.935f); lineTo(e4.x, e4.y)
            val d2 = q(0.16f, 0.93f); lineTo(d2.x, d2.y)
            val c3 = q(0.17f, 0.7f); lineTo(c3.x, c3.y)
            val c4 = q(0.46f, 0.69f); val c5 = q(0.5f, 0.63f); quadraticTo(c4.x, c4.y, c5.x, c5.y)
            val f1 = q(0.52f, 0.5f); val f2 = q(0.5f, 0.2f); val f3 = q(0.1f, 0.06f); cubicTo(f1.x, f1.y, f2.x, f2.y, f3.x, f3.y)
            val g1 = q(0.07f, 0f); lineTo(g1.x, g1.y)
            close()
        }
        val asas = Path().apply {
            listOf(-1f, 1f).forEach { s ->
                val h0 = q(s * 0.16f, 0.88f); val h1 = q(s * 0.44f, 0.9f); val h2 = q(s * 0.44f, 0.74f); val h3 = q(s * 0.36f, 0.68f)
                moveTo(h0.x, h0.y); lineTo(h1.x, h1.y); lineTo(h2.x, h2.y); lineTo(h3.x, h3.y)
            }
        }
        rotate(11f, pie) {
            // La sombra en la pared, a la derecha.
            translate(w * (if (a.oscuro) 0.006f else 0.02f), w * 0.004f) {
                drawPath(cuerpo, a.sombra(0.28f)); drawPath(asas, a.sombra(0.28f), style = Stroke(w * 0.012f, join = StrokeJoin.Round))
            }
            drawPath(asas, a.dia(P.ROJO_OSC, 0f, k), style = Stroke(w * 0.012f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(asas, a.dia(P.ROJO, 0f, k), style = Stroke(w * 0.007f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(cuerpo, a.dia(P.ROJO, 0f, k))
            clipPath(cuerpo) {
                val caja = Rect(pie.x - an * 0.55f, pie.y - l, pie.x + an * 0.55f, pie.y)
                cilindroGalia(caja, a, k, if (a.oscuro) 1f else -1f, 1.3f)
                // La engalba crema que se ha ido gastando y las rayas del torno en el hombro.
                drawRect(a.dia(mezcla(P.ROJO_LUZ, P.BLANCO, 0.5f), 0f, k).copy(alpha = 0.25f), Offset(caja.left, pie.y - l * 0.62f), Size(caja.width, l * 0.4f))
                for (j in 0 until 5) {
                    val y = pie.y - l * (0.5f + 0.035f * j)
                    drawLine(a.sombra(0.18f), Offset(caja.left, y), Offset(caja.right, y), 0.8f * u)
                }
                drawRect(a.sombra(0.35f), Offset(caja.left, pie.y - l * 0.7f), Size(caja.width, l * 0.012f))
                drawRect(a.sombra(0.3f), Offset(caja.left, pie.y - l * 0.07f), Size(caja.width, l * 0.07f))
                // Un desconchado del labio.
                val dl = q(0.08f, 0.99f)
                drawPath(poligonoGalia(dl.x, dl.y, dl.x + an * 0.12f, dl.y, dl.x + an * 0.06f, dl.y + l * 0.03f), a.dia(P.ROJO_LUZ, 0.1f, k))
            }
            // El tapón: la torta de cal que sella la boca, con su comba.
            val t0 = q(-0.2f, 1.02f); val t1 = q(0.2f, 1.02f)
            ovaloGalia(a.dia(P.CENIZA, 0.15f, k), Rect(t0.x, t0.y - an * 0.06f, t1.x, t1.y + an * 0.06f))
            ovaloGalia(a.brillo(0.4f, k), Rect(t0.x + an * 0.06f, t0.y - an * 0.05f, t1.x - an * 0.12f, t1.y))
            luzDeLaPuertaGalia(cuerpo, a, m, 0f)
        }
    }

    /**
     * La tinaja grande de barro negro bruñido: la panza ancha, el hombro con un cordón apretado a dedo, el cuello corto y el
     * labio vuelto; las pasadas del bruñido brillan a la luz. Encima, la tapa de tablas con su asa de cuerda y una piedra.
     */
    private fun DrawScope.tinaja(pie: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val an = w * 0.235f; val al = w * 0.188f
        val k = cerca(pie - Offset(0f, al * 0.5f))
        fun q(x: Float, y: Float) = Offset(pie.x + x * an, pie.y - y * al)
        val cuerpo = Path().apply {
            val p0 = q(-0.26f, 0f); moveTo(p0.x, p0.y)
            val a1 = q(-0.5f, 0.06f); val a2 = q(-0.56f, 0.5f); val a3 = q(-0.4f, 0.82f); cubicTo(a1.x, a1.y, a2.x, a2.y, a3.x, a3.y)
            val b1 = q(-0.3f, 0.9f); val b2 = q(-0.24f, 0.92f); quadraticTo(b1.x, b1.y, b2.x, b2.y)
            val c1 = q(-0.25f, 0.97f); lineTo(c1.x, c1.y)
            val c2 = q(-0.31f, 1f); lineTo(c2.x, c2.y)
            val c3 = q(0.31f, 1f); lineTo(c3.x, c3.y)
            val c4 = q(0.25f, 0.97f); lineTo(c4.x, c4.y)
            val d1 = q(0.24f, 0.92f); lineTo(d1.x, d1.y)
            val d2 = q(0.3f, 0.9f); val d3 = q(0.4f, 0.82f); quadraticTo(d2.x, d2.y, d3.x, d3.y)
            val e1 = q(0.56f, 0.5f); val e2 = q(0.5f, 0.06f); val e3 = q(0.26f, 0f); cubicTo(e1.x, e1.y, e2.x, e2.y, e3.x, e3.y)
            close()
        }
        // La sombra en la pared y la de contacto en el tablero.
        translate(w * (if (a.oscuro) 0.006f else 0.022f), w * 0.004f) { drawPath(cuerpo, a.sombra(0.3f)) }
        contactoGalia(pie + Offset(w * 0.01f, 0f), an * 0.8f, w * 0.012f, a, 0.45f)
        val negro = a.dia(P.NEGRO, 0f, k)
        drawPath(cuerpo, negro)
        clipPath(cuerpo) {
            val caja = Rect(pie.x - an * 0.58f, pie.y - al, pie.x + an * 0.58f, pie.y)
            // La luz de la panza: un claro grande arriba a la izquierda (o hacia la tea, de noche) y la sombra abajo a la derecha.
            val hacia = if (a.oscuro) 1f else -1f
            val cl = Offset(pie.x + hacia * an * 0.2f, pie.y - al * 0.62f)
            drawCircle(Brush.radialGradient(listOf(a.dia(P.NEGRO_LUZ, 0f, k), a.dia(P.NEGRO_LUZ, 0f, k).copy(alpha = 0f)), cl, an * 0.42f), an * 0.42f, cl)
            drawCircle(a.frio(0.4f), an * 0.55f, Offset(pie.x - hacia * an * 0.38f, pie.y + al * 0.02f))
            // Las pasadas del bruñido: trazos curvos y lustrosos que siguen la vuelta de la panza.
            val brunido = Path()
            for (j in 0 until 9) {
                val t = azar(8620, j)
                val x = caja.left + caja.width * (0.15f + 0.4f * t)
                val y0 = pie.y - al * (0.72f - 0.2f * azar(8621, j)); val l = al * (0.12f + 0.16f * azar(8622, j))
                brunido.moveTo(x, y0); brunido.quadraticTo(x - an * 0.03f, y0 + l * 0.5f, x - an * 0.01f, y0 + l)
            }
            drawPath(brunido, a.brillo(0.12f, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawLine(a.brillo(0.75f, k), Offset(cl.x - an * 0.12f, cl.y - al * 0.12f), Offset(cl.x - an * 0.17f, cl.y + al * 0.14f), 2.2f * u, StrokeCap.Round)
            drawCircle(a.brillo(0.85f, k), 1.6f * u, Offset(cl.x - an * 0.1f, cl.y - al * 0.2f))
            // El hombro: el cordón aplicado, con las huellas de los dedos en hilera.
            val yc = pie.y - al * 0.78f
            val dedos = Path(); val luces = Path()
            var x = caja.left; var j = 0
            while (x < caja.right) {
                val y = yc + (x - pie.x) * (x - pie.x) / (an * an) * al * 0.12f
                dedos.addOval(Rect(x, y - w * 0.006f, x + w * 0.012f, y + w * 0.006f))
                luces.addOval(Rect(x + w * 0.002f, y - w * 0.006f, x + w * 0.007f, y - w * 0.002f))
                x += w * 0.0125f; j++
            }
            drawPath(dedos, a.dia(mezcla(P.NEGRO, P.NEGRO_LUZ, 0.25f), 0f, k))
            drawPath(dedos, a.sombra(0.5f), style = Stroke(0.7f * u))
            drawPath(luces, a.brillo(0.35f, k))
            // El cuello en sombra bajo el labio, y la sombra del pie.
            drawRect(a.sombra(0.45f), Offset(caja.left, pie.y - al * 0.97f), Size(caja.width, al * 0.05f))
            drawRect(a.sombra(0.4f), Offset(caja.left, pie.y - al * 0.05f), Size(caja.width, al * 0.05f))
        }
        luzDeLaPuertaGalia(cuerpo, a, m, 0f)
        // La tapa de tablas, en escorzo, con sus juntas, y el asa de cuerda.
        val tapa = Rect(pie.x - an * 0.34f, pie.y - al - w * 0.014f, pie.x + an * 0.34f, pie.y - al + w * 0.012f)
        drawOval(a.sombra(0.4f), tapa.topLeft + Offset(w * 0.004f, w * 0.005f), tapa.size)
        drawOval(a.dia(P.ROBLE_LUZ, -0.05f, k), tapa.topLeft, tapa.size)
        ovaloGalia(a.dia(P.ROBLE, 0f, k), Rect(tapa.left, tapa.center.y, tapa.right, tapa.bottom + w * 0.002f))
        ovaloGalia(a.dia(P.ROBLE_LUZ, -0.05f, k), Rect(tapa.left, tapa.top, tapa.right, tapa.bottom - w * 0.004f))
        clipPath(Path().apply { addOval(Rect(tapa.left, tapa.top, tapa.right, tapa.bottom - w * 0.004f)) }) {
            listOf(0.34f, 0.66f).forEach { f -> drawLine(a.sombra(0.4f), Offset(tapa.left + tapa.width * f, tapa.top), Offset(tapa.left + tapa.width * f - w * 0.004f, tapa.bottom), 1f * u) }
        }
        // El asa de la tapa: una cuerda de tilo torcida, en arco, metida por dos agujeros.
        val asa = Path().apply { moveTo(tapa.center.x - an * 0.07f, tapa.center.y - w * 0.002f); cubicTo(tapa.center.x - an * 0.07f, tapa.top - w * 0.03f, tapa.center.x + an * 0.07f, tapa.top - w * 0.03f, tapa.center.x + an * 0.07f, tapa.center.y - w * 0.002f) }
        drawPath(Path().apply { addPath(asa, Offset(w * 0.004f, w * 0.004f)) }, a.sombra(0.35f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.dia(P.JUNCO_OSC, 0f, k), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.dia(P.JUNCO, 0.05f, k), style = Stroke(w * 0.003f, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(w * 0.005f, w * 0.004f))))
    }

    /** El cubo de duelas de roble, con dos aros de bronce remachados, el asa de hierro y el cucharón de madera metido. */
    private fun DrawScope.cubo(pie: Offset, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val an = w * 0.088f; val al = w * 0.088f
        val k = cerca(pie - Offset(0f, al * 0.5f))
        val boca = Rect(pie.x - an * 0.45f, pie.y - al - an * 0.08f, pie.x + an * 0.45f, pie.y - al + an * 0.08f)
        // El cucharón: el mango sube por detrás de la boca, ladeado.
        val m0 = Offset(pie.x + an * 0.12f, pie.y - al * 0.7f); val m1 = Offset(pie.x - an * 0.36f, pie.y - al * 1.62f)
        drawLine(a.sombra(0.3f), m0 + Offset(w * 0.006f, w * 0.004f), m1 + Offset(w * 0.006f, w * 0.004f), w * 0.009f, StrokeCap.Round)
        drawLine(a.dia(P.HAYA, 0f, k), m0, m1, w * 0.008f, StrokeCap.Round)
        drawLine(a.brillo(0.4f, k), m0 - Offset(w * 0.002f, 0f), m1 - Offset(w * 0.002f, 0f), 0.8f * u, StrokeCap.Round)
        val cuerpo = Path().apply {
            moveTo(boca.left, boca.center.y); lineTo(pie.x - an * 0.5f, pie.y)
            quadraticTo(pie.x, pie.y + an * 0.1f, pie.x + an * 0.5f, pie.y)
            lineTo(boca.right, boca.center.y); close()
        }
        translate(w * (if (a.oscuro) 0.005f else 0.016f), w * 0.003f) { drawPath(cuerpo, a.sombra(0.3f)) }
        contactoGalia(pie, an * 1.05f, w * 0.01f, a, 0.4f)
        drawPath(cuerpo, a.dia(P.ROBLE, 0f, k))
        clipPath(cuerpo) {
            // Las duelas: franjas de un tono y otro, con su junta.
            var x = pie.x - an * 0.55f; var j = 0
            while (x < pie.x + an * 0.55f) {
                drawRect(a.dia(if (j % 2 == 0) P.ROBLE_LUZ else P.ROBLE, -0.06f, k).copy(alpha = 0.5f), Offset(x, boca.top), Size(an * 0.14f, al + an * 0.2f))
                drawLine(a.luz(P.ROBLE_HONDO, k), Offset(x, boca.top), Offset(x + (x - pie.x) * 0.06f, pie.y + an * 0.1f), 0.8f * u)
                x += an * 0.14f; j++
            }
            cilindroGalia(Rect(pie.x - an * 0.52f, boca.top, pie.x + an * 0.52f, pie.y + an * 0.1f), a, k, if (a.oscuro) 1f else -1f, 1.2f)
            // Los dos aros de bronce, con sus remaches.
            listOf(0.22f, 0.72f).forEach { f ->
                val y = pie.y - al * f
                drawRect(a.dia(P.BRONCE, 0f, k), Offset(pie.x - an * 0.6f, y - w * 0.005f), Size(an * 1.2f, w * 0.01f))
                drawRect(a.brillo(0.5f, k), Offset(pie.x - an * 0.6f, y - w * 0.005f), Size(an * 1.2f, 0.9f * u))
                drawRect(a.dia(P.VERDIN, 0f, k).copy(alpha = 0.5f), Offset(pie.x + an * 0.1f, y + w * 0.001f), Size(an * 0.3f, w * 0.004f))
                drawRect(a.sombra(0.35f), Offset(pie.x - an * 0.6f, y + w * 0.005f), Size(an * 1.2f, 1f * u))
                listOf(-0.3f, 0.05f, 0.38f).forEach { fx -> drawCircle(a.dia(P.BRONCE_LUZ, 0f, k), 1.1f * u, Offset(pie.x + an * fx, y)) }
            }
        }
        luzDeLaPuertaGalia(cuerpo, a, m, 0f)
        // La boca, con el agua oscura dentro y el canto de las duelas.
        ovaloGalia(a.dia(P.ROBLE_LUZ, 0f, k), boca)
        ovaloGalia(a.dia(mezcla(P.NEGRO, P.FRIO, 0.3f), 0f, k), boca.deflate(w * 0.004f))
        drawArc(a.brillo(0.5f, k), 200f, 80f, false, boca.deflate(w * 0.006f).topLeft, boca.deflate(w * 0.006f).size, style = Stroke(0.9f * u))
        // El asa de hierro, de oreja a oreja, caída hacia atrás.
        val asa = Path().apply { moveTo(boca.left + w * 0.002f, boca.center.y); quadraticTo(pie.x + an * 0.1f, boca.top - an * 0.5f, boca.right - w * 0.002f, boca.center.y) }
        drawPath(asa, a.luz(P.HIERRO, k), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        // El cazo del cucharón, que asoma por el borde.
        ovaloGalia(a.dia(P.HAYA_OSC, 0f, k), Rect(m0.x - an * 0.14f, boca.top - an * 0.02f, m0.x + an * 0.16f, boca.top + an * 0.1f))
    }

    /**
     * La pila de mantas de lana a cuadros, dobladas, en el extremo derecho del dintel: tres, de cuadros distintos; la de arriba,
     * medio abierta, cae por el canto del dintel con sus flecos.
     */
    private fun DrawScope.mantas(apoyo: Float, ch: Float, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val x0 = w * 0.6f; val x1 = w * 0.845f
        val k = cerca(Offset((x0 + x1) / 2, apoyo - w * 0.05f))
        val alto = w * 0.044f
        val lanas = listOf(Triple(P.LANA_VERDE, P.LANA_ROJA, P.LANA_OCRE), Triple(P.GLASTO, P.LANA_OCRE, P.LANA_CRUDA), Triple(P.LANA_ROJA, P.LANA_VERDE, P.LANA_OCRE))
        // La sombra de la pila en el barro.
        translate(w * (if (a.oscuro) 0.005f else 0.02f), 0f) { drawRect(a.sombra(0.28f), Offset(x0 + w * 0.01f, apoyo - alto * 3.1f), Size(x1 - x0, alto * 3.1f)) }
        contactoGalia(Offset((x0 + x1) / 2, apoyo), x1 - x0, w * 0.012f, a, 0.4f)
        for (i in 0 until 3) {
            val y1 = apoyo - alto * i; val y0 = y1 - alto
            val dx = w * (0.008f * (i % 2) - 0.004f * i)
            val r = Rect(x0 + dx, y0, x1 + dx - w * 0.012f * i, y1)
            val (fondo, raya, hilo) = lanas[i]
            val sil = Path().apply {
                moveTo(r.left + alto * 0.4f, r.top); lineTo(r.right - alto * 0.2f, r.top + w * 0.001f)
                quadraticTo(r.right + alto * 0.3f, r.center.y, r.right - alto * 0.15f, r.bottom)
                lineTo(r.left + alto * 0.3f, r.bottom)
                quadraticTo(r.left - alto * 0.35f, r.center.y, r.left + alto * 0.4f, r.top); close()
            }
            drawPath(sil, a.dia(fondo, 0f, k))
            clipPath(sil) { cuadros(r, raya, hilo, a, k, 6f * u) }
            clipPath(sil) {
                // El doblez de delante, redondo: luz arriba, sombra abajo; en la punta, las capas de la lana doblada.
                drawRect(a.claro(0.28f), r.topLeft, Size(r.width, alto * 0.2f))
                drawRect(a.sombra(0.4f), Offset(r.left, r.bottom - alto * 0.28f), Size(r.width, alto * 0.28f))
                drawRect(a.sombra(0.3f), Offset(r.right - alto * 0.7f, r.top), Size(alto * 0.7f, r.height))
                for (q in 1..3) {
                    val yq = r.top + r.height * q / 4f
                    drawLine(a.sombra(0.45f), Offset(r.right - alto * 0.75f, yq), Offset(r.right + alto, yq + alto * 0.04f), 1.1f * u)
                    drawLine(a.dia(fondo, 0.15f, k).copy(alpha = 0.6f), Offset(r.right - alto * 0.75f, yq - 1.2f * u), Offset(r.right + alto, yq - 1.2f * u), 0.7f * u)
                }
            }
            luzDeLaPuertaGalia(sil, a, m, 0f)
        }
        // La de arriba, caída por el canto del dintel: un paño colgado con sus pliegues y sus flecos.
        val c0 = x0 + w * 0.045f; val c1 = x0 + w * 0.15f
        val y0 = apoyo - w * 0.004f; val y1 = apoyo + w * 0.066f
        val kc = cerca(Offset((c0 + c1) / 2, (y0 + y1) / 2))
        val pano = Path().apply {
            moveTo(c0, y0); lineTo(c1, y0)
            cubicTo(c1 + w * 0.006f, y0 + w * 0.03f, c1 - w * 0.004f, y1 - w * 0.02f, c1 + w * 0.002f, y1)
            for (q in 6 downTo 0) lineTo(c0 + (c1 - c0) * q / 6f, y1 - (if (q % 2 == 0) 0f else w * 0.004f))
            cubicTo(c0 - w * 0.004f, y1 - w * 0.03f, c0 + w * 0.004f, y0 + w * 0.03f, c0, y0)
            close()
        }
        translate(w * (if (a.oscuro) 0.003f else 0.008f), w * 0.006f) { drawPath(pano, a.sombra(0.35f)) }
        drawPath(pano, a.dia(P.LANA_ROJA, 0f, kc))
        clipPath(pano) {
            cuadros(Rect(c0 - w * 0.01f, y0, c1 + w * 0.01f, y1), P.LANA_VERDE, P.LANA_OCRE, a, kc, 6f * u)
            for (q in 0 until 3) {
                val xp = c0 + (c1 - c0) * (0.2f + 0.3f * q)
                drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.sombra(0.4f), Color.Transparent), xp - w * 0.008f, xp + w * 0.008f), Offset(xp - w * 0.008f, y0), Size(w * 0.016f, y1 - y0))
                drawLine(a.claro(0.25f), Offset(xp - w * 0.011f, y0), Offset(xp - w * 0.011f, y1), 1f * u)
            }
            drawRect(a.claro(0.3f), Offset(c0, y0), Size(c1 - c0, w * 0.006f))
        }
        luzDeLaPuertaGalia(pano, a, m, ManchaGalia.FRENTE)
        val flecos = Path()
        for (q in 0 until 14) {
            val xf = c0 + (c1 - c0) * (q + 0.5f) / 14f
            flecos.moveTo(xf, y1 - w * 0.002f); flecos.lineTo(xf + w * 0.0015f, y1 + w * (0.008f + 0.006f * azar(8600, q)))
        }
        drawPath(flecos, a.dia(P.LANA_ROJA, -0.05f, kc), style = Stroke(1f * u, cap = StrokeCap.Round))
        // El filo de la tea en el borde derecho de la pila (de noche).
        if (a.lumbre) drawRect(a.filo(0.6f * cerca(Offset(x1, apoyo - alto))), Offset(x1 - w * 0.004f, apoyo - alto * 3f), Size(1.6f * u, alto * 3f))
    }

    /** Los cuadros de una lana en [r]: franjas anchas de [raya] que se cruzan y, entre ellas, un hilo fino de [hilo]. */
    fun DrawScope.cuadros(r: Rect, raya: Color, hilo: Color, a: AireGalia, k: Float, paso: Float) {
        val u = density
        val ancha = a.dia(raya, 0f, k).copy(alpha = 0.62f)
        val fino = a.dia(hilo, 0f, k).copy(alpha = 0.8f)
        val p = Path(); val f = Path()
        var x = r.left + paso * 0.3f
        while (x < r.right) { p.addRect(Rect(x, r.top, x + paso * 0.34f, r.bottom)); f.moveTo(x + paso * 0.66f, r.top); f.lineTo(x + paso * 0.66f, r.bottom); x += paso }
        var y = r.top + paso * 0.2f
        while (y < r.bottom) { p.addRect(Rect(r.left, y, r.right, y + paso * 0.34f)); f.moveTo(r.left, y + paso * 0.66f); f.lineTo(r.right, y + paso * 0.66f); y += paso }
        drawPath(p, ancha)
        drawPath(f, fino, style = Stroke(0.6f * u))
    }
}
