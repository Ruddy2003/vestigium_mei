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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sqrt

/**
 * El bodegón de encima de la librería de Horror cósmico ([MuebleEstanteArcano]) y lo colgado de la pared. Manda la lente del faro
 * viejo: la colmena de anillos de vidrio en su armazón de latón, sobre su pie de hierro, guardada aquí después de que la quitaron.
 * A la izquierda, bajo la ventana, la pila de registros con la caja envuelta en hule, atada dos veces y con tres lacres; detrás de
 * la lente, los rollos de cartas apoyados en la pared; colgadas de un clavo, tres boyas de vidrio en su red de piola; el
 * guardabrisas (la vela bajo su campana de vidrio), que de noche arde; el barómetro de columna en la pared y una caja de la aduana
 * con legajos mojados, cortada por el borde.
 */
internal object CoronaEstanteArcano {
    private val P = PaletaArcano
    private val M = MuebleEstanteArcano

    /** El eje de la lente, en anchos. */
    private const val LENTE_X = 0.335f

    /** La sombra de [p] en la pared: de día y al ocaso, a la derecha (la ventana); de noche, lejos del guardabrisas. */
    private fun DrawScope.sombraPared(p: Path, c: Offset, g: MarcoArcano, fuerza: Float = 0.3f, largo: Float = 1f) {
        val w = g.w
        val a = g.a
        val d = if (!a.oscuro) Offset(w * (if (a.noche) 0.03f else 0.02f), w * 0.008f) * largo else {
            val v = c - g.guardabrisas
            val l = v.getDistance().coerceAtLeast(1f)
            Offset(v.x / l, v.y / l + 0.2f) * (w * 0.02f * largo)
        }
        val al = if (a.oscuro) fuerza * (0.4f + 1.3f * g.cerca(c)) else fuerza
        translate(d.x, d.y) { drawPath(p, a.sombra(al)) }
    }

    // ---------------------------------------------------------------- lo colgado

    fun DrawScope.colgado(g: MarcoArcano) {
        boyas(g)
        barometro(g)
    }

    /**
     * Las tres boyas de vidrio verde, cada una en su red de piola, colgadas de un mismo clavo con cabos de largo distinto: una
     * grande arriba, la mediana montada sobre ella y la chica más abajo, en racimo. Cada una con el reflejo de la ventana, la luz
     * que la atraviesa abajo y la malla encima.
     */
    private fun DrawScope.boyas(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val clavo = Offset(w * 0.64f, w * 0.082f)
        val boyas = listOf(Triple(0.626f, 0.19f, 0.046f), Triple(0.68f, 0.25f, 0.035f), Triple(0.642f, 0.306f, 0.027f))
        // Las sombras en la pared y los cabos.
        boyas.forEach { (fx, fy, fr) ->
            val c = Offset(w * fx, w * fy)
            // De noche, junto al guardabrisas, la sombra se queda tenue y corta: un disco oscuro en la pared se leería como un hueco.
            sombraPared(Path().apply { addOval(Rect(c.x - w * fr, c.y - w * fr, c.x + w * fr, c.y + w * fr)) }, c, g, if (g.a.oscuro) 0.1f else 0.26f, if (g.a.oscuro) 0.7f else 1.3f)
        }
        val piola = a.luz(P.PIOLA, g.cerca(clavo))
        boyas.forEach { (fx, fy, fr) ->
            val tope = Offset(w * fx, w * (fy - fr) - 1f * u)
            translate(2f * u, 2f * u) { drawLine(a.sombra(0.3f), clavo, tope, 1f * u) }
            drawLine(piola, clavo, tope, 1.1f * u)
        }
        boyas.forEachIndexed { i, (fx, fy, fr) -> boya(Offset(w * fx, w * fy), w * fr, i, g) }
        clavoArcano(clavo, 2f * u, a, g.cerca(clavo), P.HIERRO_LUZ)
    }

    private fun DrawScope.boya(c: Offset, r: Float, i: Int, g: MarcoArcano) {
        val u = density
        val a = g.a
        val k = g.cerca(c); val kf = g.frio(c)
        val esfera = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        val verde = if (i == 1) mezcla(P.BOYA, P.VIDRIO_OSC, 0.4f) else P.BOYA
        drawPath(esfera, a.luz(mezcla(verde, P.BOYA_OSC, 0.25f), k))
        clipPath(esfera) {
            // Lo hondo del vidrio a la derecha y la luz que lo atraviesa y se junta abajo a la derecha.
            drawCircle(a.luz(P.BOYA_OSC, k).copy(alpha = 0.7f), r, c + Offset(r * 0.45f, r * 0.1f))
            drawCircle(a.luz(mezcla(verde, P.VIDRIO_LUZ, 0.35f), k), r * 0.42f, c + Offset(r * 0.32f, r * 0.42f))
            drawCircle(a.luz(mezcla(verde, P.VIDRIO_LUZ, 0.7f), k).copy(alpha = 0.6f), r * 0.2f, c + Offset(r * 0.38f, r * 0.55f))
            // El burbujeo del vidrio soplado.
            for (j in 0 until 5) {
                val b = c + Offset(r * (azar(8100 + i, j) - 0.5f) * 1.4f, r * (azar(8110 + i, j) - 0.5f) * 1.4f)
                drawCircle(a.luz(P.VIDRIO_LUZ, k).copy(alpha = 0.35f), (0.5f + 0.8f * azar(8120 + i, j)) * u, b)
            }
            // De noche, el calor del guardabrisas en el lado que lo mira.
            if (a.oscuro && k > 0.1f) drawCircle(P.LLAMA.copy(alpha = (0.5f * k).coerceAtMost(0.4f)), r, c + Offset(r * 1.1f, 0f))
        }
        // El reflejo de la ventana: un cuadrito curvo con su cruceta, arriba a la izquierda (de noche, el del faro, tenue).
        val ref = Rect(c.x - r * 0.62f, c.y - r * 0.62f, c.x - r * 0.18f, c.y - r * 0.12f)
        val cRef = if (a.oscuro) P.HAZ.copy(alpha = (0.25f + 0.4f * kf).coerceAtMost(0.6f)) else if (a.noche) mezcla(P.OCASO, Color.White, 0.5f).copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)
        drawRoundRect(cRef, ref.topLeft, ref.size, CornerRadius(r * 0.12f))
        drawLine(a.luz(verde, k), Offset(ref.center.x, ref.top), Offset(ref.center.x - r * 0.04f, ref.bottom), 0.9f * u)
        drawLine(a.luz(verde, k), Offset(ref.left, ref.center.y), Offset(ref.right, ref.center.y - r * 0.04f), 0.9f * u)
        // La red de piola: la malla en rombos curvados sobre la esfera y el nudo de arriba.
        val red = Path()
        for (j in -2..2) {
            val f = j / 3f
            red.moveTo(c.x + r * f * 0.3f, c.y - r)
            red.quadraticTo(c.x + r * (f * 1.6f + 0.3f * f), c.y, c.x + r * f * 0.6f, c.y + r * 0.98f)
        }
        listOf(-0.45f, 0f, 0.45f).forEach { f ->
            val yy = c.y + r * f
            val ancho = r * sqrt((1f - f * f).coerceAtLeast(0f))
            red.moveTo(c.x - ancho, yy); red.quadraticTo(c.x, yy + r * 0.18f, c.x + ancho, yy)
        }
        val piola = a.luz(P.PIOLA, k)
        drawPath(red, a.sombra(0.35f), style = Stroke(1.6f * u))
        drawPath(red, piola, style = Stroke(1f * u))
        drawCircle(piola, 2f * u, Offset(c.x, c.y - r))
        drawPath(esfera, a.luz(P.BOYA_OSC, k).copy(alpha = 0.6f), style = Stroke(1f * u))
    }

    /**
     * El barómetro de columna colgado en la pared: la caja de caoba con su remate redondo, la placa de marfil con sus rayas (sin
     * cifras), el tubo de vidrio con la columna oscura y la cubeta de abajo.
     */
    private fun DrawScope.barometro(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val x = w * 0.928f; val y0 = w * 0.06f; val y1 = w * 0.34f
        val an = w * 0.034f
        val c = Offset(x, (y0 + y1) / 2)
        val k = g.cerca(c)
        val caja = Path().apply {
            moveTo(x - an / 2, y1 - w * 0.03f); lineTo(x - an / 2, y0 + an / 2)
            quadraticTo(x - an / 2, y0, x, y0); quadraticTo(x + an / 2, y0, x + an / 2, y0 + an / 2)
            lineTo(x + an / 2, y1 - w * 0.03f); close()
        }
        sombraPared(caja, c, g, 0.32f)
        val caoba = mezcla(P.TECA_OSC, P.MINIO, 0.25f)
        drawPath(caja, a.luz(caoba, k))
        drawRect(a.brillo(0.25f, k), Offset(x - an / 2, y0 + an * 0.4f), Size(1.6f * u, y1 - y0 - w * 0.05f))
        drawRect(a.sombra(0.3f), Offset(x + an / 2 - an * 0.2f, y0 + an * 0.4f), Size(an * 0.2f, y1 - y0 - w * 0.05f))
        // La placa de marfil, con su vidrio y sus rayas, y el remate.
        val placa = Rect(x - an * 0.34f, y0 + an * 0.45f, x + an * 0.34f, y0 + w * 0.085f)
        drawRect(a.luz(mezcla(P.PAPEL, P.CERA, 0.5f), k), placa.topLeft, placa.size)
        var yy = placa.top + 2f * u
        var j = 0
        while (yy < placa.bottom - 1.5f * u) {
            drawLine(a.luz(P.TINTA, k).copy(alpha = 0.6f), Offset(placa.left + (if (j % 4 == 0) 1f else 3f) * u, yy), Offset(placa.center.x, yy), 0.6f * u)
            yy += 2.2f * u; j++
        }
        drawRect(a.frio(0.3f), Offset(placa.left, placa.top), Size(placa.width * 0.3f, placa.height))
        drawRect(a.sombra(0.3f), placa.topLeft, placa.size, style = Stroke(0.8f * u))
        // El tubo y la columna.
        drawRect(a.luz(mezcla(P.VIDRIO, Color.White, 0.3f), k).copy(alpha = 0.7f), Offset(x - 1.3f * u, placa.bottom), Size(2.6f * u, y1 - w * 0.04f - placa.bottom))
        drawRect(a.luz(P.HIERRO, k), Offset(x - 0.6f * u, placa.top + placa.height * 0.45f), Size(1.2f * u, y1 - w * 0.04f - placa.top - placa.height * 0.45f))
        // La cubeta, redonda, abajo.
        val cb = Offset(x, y1 - w * 0.02f)
        drawCircle(a.sombra(0.35f), w * 0.022f, cb + Offset(2f * u, 2.4f * u))
        drawCircle(a.luz(caoba, k), w * 0.022f, cb)
        drawCircle(a.luz(P.LATON, k), w * 0.012f, cb)
        drawCircle(a.brillo(0.5f, k), w * 0.004f, cb - Offset(w * 0.005f, w * 0.005f))
        clavoArcano(Offset(x, y0 + an * 0.2f), 1.4f * u, a, k, P.LATON)
    }

    // ---------------------------------------------------------------- encima de la librería

    fun DrawScope.encima(g: MarcoArcano) {
        cartas(g)
        aduana(g)
        lente(g)
        registros(g)
        guardabrisas(g)
    }

    /** Los dos rollos de cartas náuticas apoyados en la pared, detrás de la lente, atados con su cinta. */
    private fun DrawScope.cartas(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val ap = g.apoyo
        listOf(Triple(0.425f, 0.5f, 0.018f), Triple(0.46f, 0.54f, 0.015f)).forEachIndexed { i, (xb, xt, rr) ->
            val b = Offset(w * xb, ap); val t = Offset(w * xt, ap - w * (0.25f - 0.03f * i))
            val c = (b + t) / 2f
            val k = g.cerca(c)
            val r = w * rr
            val d = t - b; val l = d.getDistance(); val n = Offset(-d.y / l, d.x / l) * r
            val cuerpo = poligonoArcano(b.x - n.x, b.y - n.y, t.x - n.x, t.y - n.y, t.x + n.x, t.y + n.y, b.x + n.x, b.y + n.y)
            sombraPared(cuerpo, c, g, 0.28f)
            val papel = if (i == 0) P.PAPEL else mezcla(P.PAPEL, P.MANILA, 0.6f)
            drawPath(cuerpo, a.luz(papel, k))
            clipPath(cuerpo) {
                // La luz por la izquierda y la sombra a la derecha, a lo largo del rollo.
                translate(-n.x * 0.9f, -n.y * 0.9f) { drawPath(cuerpo, a.brillo(0.35f, k)) }
                translate(n.x * 1.1f, n.y * 1.1f) { drawPath(cuerpo, a.sombra(0.25f)) }
                for (q in 0 until 3) {
                    val f = 0.2f + 0.3f * q
                    val p = b + d * f
                    drawLine(a.sombra(0.18f), p - n, p + n * 0.6f + d * 0.04f, 0.8f * u)
                }
            }
            // La boca de arriba: la vuelta del papel.
            drawOval(a.luz(mezcla(papel, P.PAPEL_OSC, 0.35f), k), Offset(t.x - r, t.y - r * 0.5f), Size(r * 2, r))
            drawOval(a.sombra(0.4f), Offset(t.x - r * 0.55f, t.y - r * 0.25f), Size(r * 1.1f, r * 0.5f), style = Stroke(0.8f * u))
            // La cinta.
            val p = b + d * 0.62f
            drawLine(a.luz(P.LACRE, k), p - n * 1.05f, p + n * 1.05f, 2.4f * u)
            drawLine(a.luz(P.LACRE, k), p + n * 1.05f, p + n * 1.05f + Offset(1.2f * u, w * 0.03f), 1.2f * u)
        }
    }

    /**
     * La lente del faro viejo, guardada aquí después de que la quitaron: el pie de hierro y, encima, la colmena de vidrio entre sus
     * aros de latón. Abajo y arriba, los anillos de prismas que asoman en dientes (el borde escalonado que la distingue); en medio,
     * el cinturón de prismas finos, y dentro, el mechero apagado que se ve a través del vidrio; arriba, la cúpula con su
     * respiradero. De día, la columna de reflejos de la ventana por la izquierda; de noche, la del guardabrisas por la derecha y el
     * roce frío del faro.
     */
    private fun DrawScope.lente(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val cx = w * LENTE_X
        val ap = g.apoyo
        val c = Offset(cx, ap - w * 0.19f)
        val k = g.cerca(c); val kf = g.frio(c)
        fun yA(f: Float) = ap - w * f
        // Los anillos: (abajo, arriba, radio abajo, radio arriba), en anchos sobre la tapa. Abajo, los prismas se abren hacia arriba;
        // arriba, se cierran; cada uno asoma más que el de encima (o el de debajo), en diente.
        val bajos = listOf(floatArrayOf(0.068f, 0.085f, 0.098f, 0.086f), floatArrayOf(0.085f, 0.102f, 0.106f, 0.094f),
            floatArrayOf(0.102f, 0.119f, 0.112f, 0.1f), floatArrayOf(0.119f, 0.136f, 0.116f, 0.106f))
        val altos = listOf(floatArrayOf(0.224f, 0.242f, 0.106f, 0.118f), floatArrayOf(0.242f, 0.26f, 0.098f, 0.11f),
            floatArrayOf(0.26f, 0.277f, 0.087f, 0.1f), floatArrayOf(0.277f, 0.293f, 0.074f, 0.088f), floatArrayOf(0.293f, 0.308f, 0.058f, 0.073f))
        val faja = floatArrayOf(0.143f, 0.217f, 0.114f)
        val sil = Path().apply {
            moveTo(cx - w * 0.07f, yA(0.068f))
            bajos.forEach { r -> lineTo(cx - w * r[2], yA(r[0]) - 0.5f * u); lineTo(cx - w * r[3], yA(r[1])) }
            lineTo(cx - w * faja[2], yA(faja[0])); lineTo(cx - w * faja[2], yA(faja[1]))
            altos.forEach { r -> lineTo(cx - w * r[3], yA(r[0])); lineTo(cx - w * r[2], yA(r[1])) }
            lineTo(cx + w * 0.058f, yA(0.308f))
            altos.asReversed().forEach { r -> lineTo(cx + w * r[2], yA(r[1])); lineTo(cx + w * r[3], yA(r[0])) }
            lineTo(cx + w * faja[2], yA(faja[1])); lineTo(cx + w * faja[2], yA(faja[0]))
            bajos.asReversed().forEach { r -> lineTo(cx + w * r[3], yA(r[1])); lineTo(cx + w * r[2], yA(r[0]) - 0.5f * u) }
            lineTo(cx + w * 0.07f, yA(0.068f))
            close()
        }
        val pie = poligonoArcano(cx - w * 0.075f, ap, cx + w * 0.075f, ap, cx + w * 0.055f, yA(0.016f), cx + w * 0.04f, yA(0.024f), cx + w * 0.036f, yA(0.058f),
            cx - w * 0.036f, yA(0.058f), cx - w * 0.04f, yA(0.024f), cx - w * 0.055f, yA(0.016f))
        sombraPared(Path().apply { addPath(sil); addPath(pie) }, c, g, 0.34f, 1.2f)
        contactoArcano(Offset(cx, ap), w * 0.18f, w * 0.014f, a, 0.5f, if (a.oscuro) 1f else -1f)
        // El pie de hierro pintado de verde oscuro, con su luz y su sombra.
        val hierro = mezcla(P.HIERRO, P.CASCO, 0.55f)
        drawPath(pie, a.luz(hierro, k))
        clipPath(pie) {
            drawRect(a.brillo(0.28f, k), Offset(cx - w * 0.075f, yA(0.06f)), Size(w * 0.04f, w * 0.06f))
            drawRect(a.sombra(0.35f), Offset(cx + w * 0.015f, yA(0.06f)), Size(w * 0.06f, w * 0.06f))
        }
        drawRect(a.brillo(0.45f, k), Offset(cx - w * 0.055f, yA(0.017f)), Size(w * 0.11f, 1.2f * u))
        // El cuerpo: lo hondo de dentro (la pared a través del vidrio, verdosa y oscura) y el mechero apagado.
        val dentro = if (a.oscuro) mezcla(P.NOCHE_PARED, P.VIDRIO_OSC, 0.35f) else mezcla(P.VIDRIO_OSC, P.MAR_OSC, 0.5f)
        drawPath(sil, a.luz(dentro, k))
        clipPath(sil) {
            // El mechero: el tubo de vidrio y el depósito, desenfocados detrás de los prismas.
            drawRect(a.luz(mezcla(P.HIERRO, P.LATON_OSC, 0.4f), k).copy(alpha = 0.7f), Offset(cx - w * 0.03f, yA(0.15f)), Size(w * 0.06f, w * 0.05f))
            drawRect(a.luz(mezcla(P.VIDRIO_LUZ, P.VIDRIO_OSC, 0.5f), k).copy(alpha = 0.5f), Offset(cx - w * 0.012f, yA(0.24f)), Size(w * 0.024f, w * 0.09f))
            // Los anillos de prismas de abajo y de arriba: cada uno con su cara de luz y su cara de sombra.
            val anillos = bajos.map { it to true } + altos.map { it to false }
            anillos.forEach { (r, abajo) ->
                val y0 = yA(r[0]); val y1 = yA(r[1])
                val rr = w * maxOf(r[2], r[3])
                val medio = (y0 + y1) / 2
                drawRect(a.luz(mezcla(P.VIDRIO, P.VIDRIO_OSC, 0.3f), k).copy(alpha = 0.72f), Offset(cx - rr, y1), Size(rr * 2, y0 - y1))
                // La cara que mira arriba (luz) y la que mira abajo (sombra), según hacia dónde abre el prisma.
                val clara = if (abajo) Rect(cx - rr, y1, cx + rr, medio) else Rect(cx - rr, medio, cx + rr, y0)
                val honda = if (abajo) Rect(cx - rr, medio, cx + rr, y0) else Rect(cx - rr, y1, cx + rr, medio)
                drawRect(a.luz(P.VIDRIO_LUZ, k).copy(alpha = if (a.oscuro) 0.18f else 0.42f), clara.topLeft, clara.size)
                drawRect(a.sombra(0.28f), honda.topLeft, honda.size)
                drawLine(a.luz(P.VIDRIO_LUZ, k).copy(alpha = if (a.oscuro) 0.3f else 0.8f), Offset(cx - rr, if (abajo) y1 + 0.6f * u else y0 - 0.6f * u),
                    Offset(cx + rr, if (abajo) y1 + 0.6f * u else y0 - 0.6f * u), 1f * u)
            }
            // El cinturón de prismas finos.
            val f0 = yA(faja[0]); val f1 = yA(faja[1])
            drawRect(a.luz(mezcla(P.VIDRIO, P.VIDRIO_OSC, 0.2f), k).copy(alpha = 0.62f), Offset(cx - w * faja[2], f1), Size(w * faja[2] * 2, f0 - f1))
            var y = f1 + w * 0.004f
            while (y < f0 - w * 0.004f) {
                drawLine(a.luz(P.VIDRIO_LUZ, k).copy(alpha = if (a.oscuro) 0.2f else 0.55f), Offset(cx - w * faja[2], y), Offset(cx + w * faja[2], y), 1f * u)
                drawLine(a.sombra(0.25f), Offset(cx - w * faja[2], y + 1.4f * u), Offset(cx + w * faja[2], y + 1.4f * u), 1.2f * u)
                y += w * 0.0105f
            }
            // El redondo del vidrio: los lados se oscurecen (se ve el vidrio de canto).
            drawRect(a.sombra(0.3f), Offset(cx - w * 0.12f, yA(0.32f)), Size(w * 0.025f, w * 0.26f))
            drawRect(a.sombra(0.4f), Offset(cx + w * 0.095f, yA(0.32f)), Size(w * 0.025f, w * 0.26f))
            // La columna de reflejos: la ventana de día (por la izquierda), la llama de noche (por la derecha) y el faro, frío.
            if (!a.oscuro) {
                val luz = if (a.noche) mezcla(P.OCASO, Color.White, 0.35f) else Color.White
                drawRect(luz.copy(alpha = 0.45f), Offset(cx - w * 0.072f, yA(0.31f)), Size(w * 0.026f, w * 0.245f))
                drawRect(luz.copy(alpha = 0.22f), Offset(cx - w * 0.04f, yA(0.31f)), Size(w * 0.01f, w * 0.245f))
                drawRect(luz.copy(alpha = 0.18f), Offset(cx + w * 0.05f, yA(0.31f)), Size(w * 0.008f, w * 0.245f))
            } else {
                drawRect(P.LLAMA.copy(alpha = (0.6f * k + 0.06f).coerceAtMost(0.55f)), Offset(cx + w * 0.052f, yA(0.31f)), Size(w * 0.03f, w * 0.245f))
                drawRect(P.HAZ.copy(alpha = (0.4f * kf).coerceAtMost(0.32f)), Offset(cx - w * 0.088f, yA(0.31f)), Size(w * 0.02f, w * 0.245f))
            }
        }
        // Los destellos de los prismas: un punto vivo en cada anillo, en la columna de la luz.
        (bajos + altos).forEach { r ->
            val y = yA((r[0] + r[1]) / 2)
            val rr = w * maxOf(r[2], r[3])
            if (!a.oscuro) drawLine(Color.White.copy(alpha = if (a.noche) 0.7f else 0.95f), Offset(cx - rr * 0.6f, y), Offset(cx - rr * 0.42f, y), 2f * u, StrokeCap.Round)
            else {
                drawLine(P.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.92f)), Offset(cx + rr * 0.45f, y), Offset(cx + rr * 0.66f, y), 1.8f * u, StrokeCap.Round)
                if (kf > 0.1f) drawLine(P.HAZ.copy(alpha = (0.9f * kf).coerceAtMost(0.6f)), Offset(cx - rr * 0.78f, y), Offset(cx - rr * 0.66f, y), 1.4f * u, StrokeCap.Round)
            }
        }
        // Los aros de latón: abajo, a los lados del cinturón y arriba.
        val laton = a.luz(P.LATON, k)
        listOf(floatArrayOf(0.058f, 0.068f, 0.072f), floatArrayOf(0.136f, 0.143f, 0.117f), floatArrayOf(0.217f, 0.224f, 0.117f), floatArrayOf(0.308f, 0.316f, 0.06f)).forEach { r ->
            val ar = Rect(cx - w * r[2], yA(r[1]), cx + w * r[2], yA(r[0]))
            drawRect(laton, ar.topLeft, ar.size)
            drawRect(a.brillo(0.55f, k), ar.topLeft, Size(ar.width * 0.6f, 1f * u))
            drawRect(a.sombra(0.35f), Offset(ar.left, ar.bottom - 1f * u), Size(ar.width, 1f * u))
        }
        // La cúpula con su respiradero.
        val cup = Path().apply {
            moveTo(cx - w * 0.056f, yA(0.316f)); cubicTo(cx - w * 0.054f, yA(0.345f), cx + w * 0.054f, yA(0.345f), cx + w * 0.056f, yA(0.316f)); close()
        }
        drawPath(cup, laton)
        clipPath(cup) {
            drawRect(a.sombra(0.3f), Offset(cx + w * 0.015f, yA(0.35f)), Size(w * 0.05f, w * 0.04f))
            drawRect(a.brillo(0.4f, k), Offset(cx - w * 0.04f, yA(0.345f)), Size(w * 0.02f, w * 0.03f))
        }
        val resp = Rect(cx - w * 0.012f, yA(0.356f), cx + w * 0.012f, yA(0.339f))
        drawRect(a.luz(P.LATON_OSC, k), resp.topLeft, resp.size)
        for (j in 0 until 3) drawLine(a.sombra(0.5f), Offset(resp.left + resp.width * (0.25f + 0.25f * j), resp.top + 1f * u), Offset(resp.left + resp.width * (0.25f + 0.25f * j), resp.bottom - 1f * u), 0.8f * u)
        drawRect(laton, Offset(resp.left - 1f * u, resp.top - 1.6f * u), Size(resp.width + 2f * u, 1.8f * u))
        drawPath(sil, a.luz(P.VIDRIO_OSC, k).copy(alpha = 0.7f), style = Stroke(1f * u))
    }

    /**
     * La pila de registros tumbados, bajo la ventana, y encima la caja envuelta en hule negro, atada dos veces con piola y con
     * tres lacres: lo guardado con demasiado cuidado.
     */
    private fun DrawScope.registros(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val ap = g.apoyo
        val libros = listOf(Triple(0.03f, 0.26f, 0.028f) to Color(0xFF2E4A40), Triple(0.042f, 0.248f, 0.025f) to Color(0xFF6E2A22), Triple(0.024f, 0.236f, 0.023f) to Color(0xFF4E3A28))
        val total = libros.sumOf { it.first.third.toDouble() }.toFloat()
        val c = Offset(w * 0.12f, ap - w * 0.05f)
        val k = g.cerca(c)
        sombraPared(poligonoArcano(w * 0.012f, ap, w * 0.232f, ap, w * 0.232f, ap - w * total, w * 0.19f, ap - w * (total + 0.05f), w * 0.04f, ap - w * (total + 0.05f)), c, g, 0.3f)
        contactoArcano(Offset(w * 0.12f, ap), w * 0.24f, w * 0.012f, a, 0.4f)
        var y = ap
        libros.forEachIndexed { i, (m, col) ->
            val (x0, x1, gr) = m
            val r = Rect(w * x0, y - w * gr, w * x1, y)
            drawRect(a.luz(col, k), r.topLeft, r.size)
            drawRect(a.brillo(0.3f, k), r.topLeft, Size(r.width, r.height * 0.2f))
            drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            // El lomo de piel con sus nervios y las puntas, y las hojas onduladas por la humedad que asoman por el corte.
            listOf(r.left, r.right - r.width * 0.1f).forEach { x -> drawRect(a.luz(P.TECA_OSC, k), Offset(x, r.top), Size(r.width * 0.1f, r.height)) }
            listOf(0.3f, 0.36f, 0.64f, 0.7f).forEach { f -> drawLine(a.sombra(0.4f), Offset(r.left + r.width * f, r.top), Offset(r.left + r.width * f, r.bottom), 1.1f * u) }
            if (i == 1) {
                val et = Rect(r.left + r.width * 0.42f, r.top + r.height * 0.22f, r.left + r.width * 0.58f, r.bottom - r.height * 0.22f)
                drawRect(a.luz(P.MANILA, k), et.topLeft, et.size)
            }
            y -= w * gr
        }
        // La caja envuelta en hule: su cuerpo, el pliegue del hule en la esquina, las dos vueltas de piola y los tres lacres.
        val caja = Rect(w * 0.06f, y - w * 0.056f, w * 0.205f, y)
        drawRect(a.sombra(0.4f), Offset(caja.left + 2f * u, caja.bottom - 1f * u), Size(caja.width, 2.6f * u))
        drawRect(a.luz(P.HULE, k), caja.topLeft, caja.size)
        drawRect(a.luz(mezcla(P.HULE, P.MAR_LUZ, 0.3f), k), caja.topLeft, Size(caja.width, caja.height * 0.18f))
        drawRect(a.brillo(0.45f, k), Offset(caja.left + caja.width * 0.05f, caja.top + caja.height * 0.3f), Size(caja.width * 0.4f, 1.2f * u))
        drawRect(a.sombra(0.35f), Offset(caja.right - caja.width * 0.15f, caja.top), Size(caja.width * 0.15f, caja.height))
        drawPath(poligonoArcano(caja.left, caja.top + caja.height * 0.2f, caja.left + caja.width * 0.18f, caja.bottom, caja.left, caja.bottom), a.luz(mezcla(P.HULE, P.MAR_LUZ, 0.15f), k))
        val piola = a.luz(P.PIOLA, k)
        listOf(0.3f, 0.68f).forEach { f ->
            val x = caja.left + caja.width * f
            drawLine(piola, Offset(x, caja.top - 1f * u), Offset(x + 0.6f * u, caja.bottom), 1.4f * u)
        }
        listOf(Offset(0.3f, 0.32f), Offset(0.68f, 0.32f), Offset(0.49f, 0.7f)).forEach { o ->
            val p = Offset(caja.left + caja.width * o.x, caja.top + caja.height * o.y)
            val lacre = irregularArcano(p, w * 0.011f, w * 0.009f, (o.x * 100).toInt(), 9, 0.25f)
            translate(1f * u, 1.2f * u) { drawPath(lacre, a.sombra(0.4f)) }
            drawPath(lacre, a.luz(P.LACRE, k))
            drawCircle(a.luz(P.LACRE_LUZ, k), w * 0.004f, p - Offset(w * 0.002f, w * 0.002f))
        }
    }

    /**
     * El guardabrisas: el candelero de latón con su platillo, la vela de cera y la campana alta de vidrio, tiznada arriba. De día,
     * la mecha apagada; de noche, la llama con su halo dentro del vidrio.
     */
    private fun DrawScope.guardabrisas(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val ap = g.apoyo
        val cx = w * M.GB_X
        val fl = g.guardabrisas
        val k = if (a.lumbre) 1f else 0f
        val kk = g.cerca(Offset(cx, ap - w * 0.06f))
        // El pie, el fuste y el platillo.
        val pie = poligonoArcano(cx - w * 0.036f, ap, cx + w * 0.036f, ap, cx + w * 0.026f, ap - w * 0.012f, cx + w * 0.008f, ap - w * 0.02f, cx + w * 0.006f, ap - w * 0.05f,
            cx - w * 0.006f, ap - w * 0.05f, cx - w * 0.008f, ap - w * 0.02f, cx - w * 0.026f, ap - w * 0.012f)
        val campana = Path().apply {
            moveTo(cx - w * 0.022f, ap - w * 0.058f)
            cubicTo(cx - w * 0.048f, ap - w * 0.1f, cx - w * 0.042f, ap - w * 0.17f, cx - w * 0.026f, ap - w * 0.205f)
            lineTo(cx - w * 0.03f, ap - w * 0.212f); lineTo(cx + w * 0.03f, ap - w * 0.212f); lineTo(cx + w * 0.026f, ap - w * 0.205f)
            cubicTo(cx + w * 0.042f, ap - w * 0.17f, cx + w * 0.048f, ap - w * 0.1f, cx + w * 0.022f, ap - w * 0.058f)
            close()
        }
        if (!a.oscuro) sombraPared(Path().apply { addPath(pie); addPath(campana) }, Offset(cx, ap - w * 0.1f), g, 0.22f)
        contactoArcano(Offset(cx, ap), w * 0.08f, w * 0.01f, a, 0.45f)
        val laton = a.luz(P.LATON, kk)
        drawPath(pie, laton)
        drawRect(a.brillo(0.5f, kk), Offset(cx - w * 0.026f, ap - w * 0.012f), Size(w * 0.02f, 1.2f * u))
        drawRect(a.sombra(0.3f), Offset(cx + w * 0.002f, ap - w * 0.05f), Size(w * 0.004f, w * 0.03f))
        val plato = Rect(cx - w * 0.026f, ap - w * 0.06f, cx + w * 0.026f, ap - w * 0.05f)
        drawOval(laton, plato.topLeft, plato.size)
        drawOval(a.luz(P.LATON_LUZ, kk), Offset(plato.left + 2f * u, plato.top), Size(plato.width * 0.5f, plato.height * 0.5f))
        // La vela, con su chorreón de cera.
        val vela = Rect(cx - w * 0.0085f, ap - w * 0.138f, cx + w * 0.0085f, ap - w * 0.058f)
        val kv = if (a.oscuro) 1f else 0f
        drawRect(a.luz(P.CERA, maxOf(kk, kv)), vela.topLeft, vela.size)
        drawRect(a.sombra(0.2f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawRoundRect(a.luz(P.CERA, maxOf(kk, kv)), Offset(vela.left - 1f * u, vela.top + w * 0.01f), Size(2.2f * u, w * 0.03f), CornerRadius(1.1f * u))
        drawOval(a.luz(P.CERA_OSC, maxOf(kk, kv)), Offset(vela.left, vela.top - 1f * u), Size(vela.width, 2f * u))
        drawLine(a.luz(P.TINTA, kk), Offset(cx, vela.top), Offset(cx + 0.4f * u, vela.top - 2.6f * u), 1f * u)
        // La llama, de noche (en los Sueños claros, recién prendida y más chica).
        if (a.lumbre) {
            val alto = w * (if (a.oscuro && !a.noche) 0.026f else 0.02f)
            mancha(fl, w * 0.06f, P.LLAMA.copy(alpha = if (a.oscuro) 0.55f else 0.3f))
            val llama = Path().apply {
                moveTo(cx - w * 0.006f, vela.top - 1f * u)
                cubicTo(cx - w * 0.008f, vela.top - alto * 0.5f, cx - w * 0.002f, vela.top - alto * 0.8f, cx + 0.3f * u, vela.top - alto)
                cubicTo(cx + w * 0.003f, vela.top - alto * 0.8f, cx + w * 0.008f, vela.top - alto * 0.5f, cx + w * 0.006f, vela.top - 1f * u)
                close()
            }
            drawPath(llama, mezcla(P.LLAMA, P.BRASA, 0.2f))
            translate(0f, alto * 0.12f) { drawOval(P.FUEGO, Offset(cx - w * 0.003f, vela.top - alto * 0.62f), Size(w * 0.006f, alto * 0.5f)) }
        }
        // La campana de vidrio: casi transparente, con sus brillos, el tiznado de arriba y el filo del borde.
        drawPath(campana, a.luz(P.VIDRIO, kk).copy(alpha = if (a.oscuro) 0.14f + 0.12f * k else 0.3f))
        clipPath(campana) {
            drawRect(a.luz(Color(0xFF2A2420), kk).copy(alpha = 0.35f), Offset(cx - w * 0.05f, ap - w * 0.215f), Size(w * 0.1f, w * 0.03f))
            if (a.oscuro) drawCircle(P.LLAMA.copy(alpha = 0.3f * k), w * 0.04f, fl)
        }
        val filo = if (a.oscuro) P.FUEGO.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.75f)
        val brillo = Path().apply { moveTo(cx - w * 0.03f, ap - w * 0.07f); cubicTo(cx - w * 0.042f, ap - w * 0.11f, cx - w * 0.036f, ap - w * 0.16f, cx - w * 0.024f, ap - w * 0.195f) }
        drawPath(brillo, filo, style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(campana, a.luz(P.VIDRIO_OSC, kk).copy(alpha = 0.8f), style = Stroke(1.3f * u))
        drawLine(filo.copy(alpha = 0.5f), Offset(cx + w * 0.026f, ap - w * 0.08f), Offset(cx + w * 0.03f, ap - w * 0.15f), 1f * u)
    }

    /** La caja de madera de la aduana, cortada por el borde de la derecha, con los legajos mojados que asoman y su asa de cabo. */
    private fun DrawScope.aduana(g: MarcoArcano) {
        val w = g.w; val u = density
        val a = g.a
        val ap = g.apoyo
        val r = Rect(w * 0.79f, ap - w * 0.082f, w * 1.04f, ap)
        val k = g.cerca(r.center)
        // Los legajos que asoman, ondulados y con su borde manchado.
        listOf(Triple(0.81f, 0.075f, -6f), Triple(0.86f, 0.07f, 4f), Triple(0.91f, 0.08f, -2f)).forEachIndexed { i, (fx, fw, giro) ->
            val hoja = Rect(w * fx, r.top - w * 0.028f, w * (fx + fw), r.top + w * 0.02f)
            rotate(giro, Offset(hoja.center.x, hoja.bottom)) {
                drawRect(a.sombra(0.3f), Offset(hoja.left + 1.4f * u, hoja.top + 1.4f * u), hoja.size)
                drawRect(a.luz(if (i == 1) P.MANILA else P.PAPEL, k), hoja.topLeft, hoja.size)
                drawRect(a.luz(P.PAPEL_OSC, k).copy(alpha = 0.5f), hoja.topLeft, Size(hoja.width, hoja.height * 0.22f))
                drawLine(a.luz(P.SALOBRE, k).copy(alpha = 0.5f), Offset(hoja.left, hoja.top + hoja.height * 0.3f), Offset(hoja.right, hoja.top + hoja.height * 0.36f), 1f * u)
            }
        }
        sombraPared(poligonoArcano(r.left, r.bottom, r.left, r.top, w, r.top, w, r.bottom), r.center, g, 0.3f)
        contactoArcano(Offset(r.center.x, ap), r.width, w * 0.012f, a, 0.4f)
        drawRect(a.luz(mezcla(P.TECA, P.TECA_GRIS, 0.45f), k), r.topLeft, r.size)
        drawRect(a.brillo(0.35f, k), r.topLeft, Size(r.width, 1.4f * u))
        listOf(0.36f, 0.7f).forEach { f -> drawLine(a.sombra(0.4f), Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * f), 1.1f * u) }
        drawRect(a.brillo(0.25f, k), r.topLeft, Size(1.6f * u, r.height))
        clavoArcano(Offset(r.left + w * 0.008f, r.top + r.height * 0.18f), 1.3f * u, a, k, P.HIERRO_LUZ)
        clavoArcano(Offset(r.left + w * 0.008f, r.top + r.height * 0.82f), 1.3f * u, a, k, P.HIERRO_LUZ)
        // El asa de cabo, en el frente.
        val asa = Path().apply { moveTo(r.left + w * 0.03f, r.top + r.height * 0.3f); cubicTo(r.left + w * 0.035f, r.top + r.height * 0.75f, r.left + w * 0.085f, r.top + r.height * 0.75f, r.left + w * 0.09f, r.top + r.height * 0.3f) }
        translate(1.6f * u, 2f * u) { drawPath(asa, a.sombra(0.35f), style = Stroke(w * 0.007f, cap = StrokeCap.Round)) }
        drawPath(asa, a.luz(P.LONA_OSC, k), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.35f, k), style = Stroke(0.7f * u))
    }
}
