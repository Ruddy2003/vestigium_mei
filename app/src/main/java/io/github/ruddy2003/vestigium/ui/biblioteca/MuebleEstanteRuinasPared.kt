package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.floor

/**
 * La pared del cuarto de lectura y el fondo de los huecos de la librería: el yeso pintado de verde agua que se descascara en capas
 * (debajo, el ocre rosado de una pintura más vieja y, en lo hondo, el ladrillo), el zócalo pintado más oscuro con su filete, las
 * manchas de la gotera, la mancha enrejada del lucernario y el herbario colgado de un cordel. Dentro de cada hueco, la trasera de
 * tablas de roble oscuro, con sus cercos de agua y alguna tabla que falta y deja ver el yeso.
 */
internal object ParedEstanteRuinas {
    /** Donde acaba el yeso claro y empieza el zócalo pintado, en anchos desde arriba. */
    private const val FILETE = 0.315f
    /** La x de la gotera, en anchos. */
    const val GOTERA_X = 0.872f

    // ---------------------------------------------------------------- la pared de arriba

    fun DrawScope.paredArriba(g: MarcoRuinas) {
        val w = g.w
        val hasta = g.apoyo + w * 0.02f
        conManchaRuinas(g, 0f, Rect(0f, 0f, w, hasta)) { s -> yeso(g, hasta, s) }
        desconchones(g)
        gotera(g)
        // Las sombras de las hojas de la hiedra que cuelga sobre el lucernario, dentro de la mancha.
        sombrasDeHojas(g)
        herbario(g)
        hiedraDelTecho(g)
    }

    /** El verde más hondo del zócalo pintado. */
    private val ZOCALO = Color(0xFF4E8A7A)

    /** El yeso: la pintura verde agua arriba y el zócalo pintado más oscuro debajo del filete, con sus pinceladas y su humedad. */
    private fun DrawScope.yeso(g: MarcoRuinas, hasta: Float, s: Float?) {
        val w = g.w; val u = density
        val yf = w * FILETE
        if (g.a.oscuro) {
            // De noche: el yeso hundido en el verde botella y, encima, el charco de la vela que lo pinta con su color encendido.
            drawRect(g.luz(w * 0.95f, w * 0.05f, 0f, s).c(P.YESO), Offset.Zero, Size(w, yf))
            drawRect(g.luz(w * 0.95f, hasta, 0f, s).c(ZOCALO), Offset(0f, yf), Size(w, hasta - yf))
            clipRect(0f, 0f, w, yf) { charcoRuinas(g, g.tarro, w * 0.62f, P.YESO, 0f, s) }
            clipRect(0f, yf, w, hasta) { charcoRuinas(g, g.tarro, w * 0.62f, ZOCALO, 0f, s) }
        } else {
            // De día: de la luz del cuarto (arriba a la izquierda) a la sombra (abajo a la derecha).
            val c0 = g.luz(w * 0.15f, w * 0.06f, 0f, s).c(P.YESO)
            val c1 = g.luz(w * 0.85f, yf, 0f, s).c(P.YESO)
            drawRect(Brush.linearGradient(listOf(c0, mezcla(c0, c1, 0.6f), c1), Offset(0f, 0f), Offset(w * 0.9f, yf + w * 0.2f)), Offset.Zero, Size(w, yf))
            val z0 = g.luz(w * 0.2f, yf + w * 0.08f, 0f, s).c(ZOCALO)
            val z1 = g.luz(w * 0.85f, hasta, 0f, s).c(ZOCALO)
            drawRect(Brush.horizontalGradient(listOf(z0, z1), 0f, w), Offset(0f, yf), Size(w, hasta - yf))
        }
        // El filete que los separa: una línea de pintura almagre gastada, con su luz encima (en tramos, cada uno con su luz).
        for (i in 0 until 5) {
            val x0 = w * i / 5f
            val lf = g.luz(x0 + w * 0.1f, yf, 0f, s)
            drawRect(lf.c(P.LADRILLO_OSC), Offset(x0, yf - 1.6f * u), Size(w / 5f + 1f, 3.2f * u))
            drawRect(lf.b(0.35f), Offset(x0, yf - 2.4f * u), Size(w / 5f + 1f, 0.8f * u))
        }
        // Pinceladas: el yeso nunca es liso (claras arriba, frías abajo) y el rodillo que dejó sus vetas.
        val r = Rect(0f, 0f, w, hasta)
        if (g.a.oscuro) {
            pinceladas(r, g.a.hondo, 811, 60, u, 0.16f)
            pinceladas(r, mezcla(P.YESO_SOMBRA, g.a.hondo, 0.6f), 812, 50, u, 0.12f)
        } else {
            pinceladas(r, g.luz(w * 0.3f, w * 0.1f, 0f, s).c(P.YESO_SOL), 811, 60, u, 0.12f)
            pinceladas(r, g.luz(w * 0.6f, w * 0.3f, 0f, s).c(P.YESO_SOMBRA), 812, 70, u, 0.12f)
        }
        // La humedad que sube del zócalo: manchas con cerco.
        for (i in 0 until 7) {
            val x = w * (0.04f + 0.92f * azar(821, i))
            val y = hasta - w * (0.02f + 0.06f * azar(822, i))
            val rr = w * (0.04f + 0.05f * azar(823, i))
            val hum = g.luz(x, y, 0f, s).c(mezcla(ZOCALO, P.HIEDRA_OSC, 0.45f))
            drawOval(hum.copy(alpha = 0.35f), Offset(x - rr, y - rr * 0.55f), Size(rr * 2, rr * 1.1f))
            drawArc(hum.copy(alpha = 0.5f), 190f, 160f, false, Offset(x - rr, y - rr * 0.55f), Size(rr * 2, rr * 1.1f), style = Stroke(0.8f * u))
        }
        // La pintura cuarteada (unas grietas finas) arriba, a media pared.
        val grieta = abiertaRuinas(listOf(Offset(w * 0.6f, w * 0.0f), Offset(w * 0.63f, w * 0.05f), Offset(w * 0.615f, w * 0.1f), Offset(w * 0.65f, w * 0.15f), Offset(w * 0.64f, w * 0.2f)))
        drawPath(grieta, g.luz(w * 0.63f, w * 0.1f, 0f, s).c(P.YESO_SOMBRA), style = Stroke(1f * u))
        translate(0.8f * u, 0.8f * u) { drawPath(grieta, g.luz(w * 0.63f, w * 0.1f, 0f, s).b(0.3f), style = Stroke(0.6f * u)) }
    }

    /**
     * De noche, el charco de una luz de la casa sobre una superficie de color [col]: un degradado radial con el color que esa
     * superficie tiene con la luz que le llega a cada distancia (lleno junto a la llama, transparente donde ya no llega), encima del
     * color hundido. Así la luz pinta lo que toca, sin velo.
     */
    fun DrawScope.charcoRuinas(g: MarcoRuinas, c: Offset, radio: Float, col: Color, delante: Float = 0f, s: Float? = null) {
        val n = 7
        val stops = Array(n + 1) { i ->
            val f = i / n.toFloat()
            val p = Offset(c.x + radio * f * 0.75f, c.y + radio * f * 0.66f)
            val color = g.luz(p, delante, s).c(col)
            f to color.copy(alpha = if (f < 0.6f) 1f else (1f - (f - 0.6f) / 0.4f).coerceIn(0f, 1f))
        }
        drawCircle(Brush.radialGradient(colorStops = stops, center = c, radius = radio), radio, c)
    }

    /**
     * Los desconchones: donde la pintura verde saltó asoma el ocre rosado de la pintura vieja y, en lo más hondo, el ladrillo con
     * sus juntas. El canto de la pintura levantada tiene su luz arriba a la izquierda y su sombra abajo.
     */
    private fun DrawScope.desconchones(g: MarcoRuinas) {
        val w = g.w; val u = density
        // (x, y, radio a lo ancho, radio a lo alto, con ladrillo)
        val lista = listOf(
            floatArrayOf(0.69f, 0.215f, 0.085f, 0.06f, 1f),
            floatArrayOf(0.2f, 0.355f, 0.06f, 0.028f, 0f),
            floatArrayOf(0.955f, 0.33f, 0.045f, 0.05f, 1f),
            floatArrayOf(0.55f, 0.41f, 0.04f, 0.02f, 0f),
        )
        lista.forEachIndexed { i, e ->
            val c = Offset(w * e[0], w * e[1])
            val l = g.luz(c.x, c.y, 0f, solEn(g, c))
            val forma = desconchonRuinas(c, w * e[2], w * e[3], 830 + i)
            // La sombra que la pintura levantada echa dentro, del lado de abajo a la derecha (de día).
            drawPath(forma, l.c(mezcla(P.OCRE_ROSA, P.YESO_BLANCO, 0.35f)))
            pinceladas(forma.getBounds(), l.c(P.YESO_BLANCO), 840 + i, 8, u, 0.2f)
            if (e[4] > 0.5f) {
                // Lo más hondo: el yeso blanco y, dentro, el ladrillo con sus juntas.
                val hondo = bultoRuinas(c + Offset(w * e[2] * 0.12f, w * e[3] * 0.1f), w * e[2] * 0.66f, w * e[3] * 0.62f, 850 + i, 11, 0.28f)
                drawPath(hondo, l.c(P.YESO_BLANCO))
                val ladrillo = bultoRuinas(c + Offset(w * e[2] * 0.16f, w * e[3] * 0.16f), w * e[2] * 0.5f, w * e[3] * 0.46f, 860 + i, 11, 0.25f)
                clipPath(ladrillo) {
                    val b = ladrillo.getBounds()
                    drawRect(l.c(P.LADRILLO), b.topLeft, b.size)
                    val hilada = w * 0.026f
                    var y = b.top; var j = 0
                    while (y < b.bottom) {
                        drawLine(l.c(P.YESO_BLANCO), Offset(b.left, y), Offset(b.right, y), 1.4f * u)
                        var x = b.left + (if (j % 2 == 0) 0f else w * 0.03f)
                        while (x < b.right) { drawLine(l.c(P.YESO_BLANCO), Offset(x, y), Offset(x, y + hilada), 1.2f * u); x += w * 0.06f }
                        // Un ladrillo más oscuro y uno más claro, para que no sea una plantilla.
                        drawRect(l.c(P.LADRILLO_OSC).copy(alpha = 0.3f), Offset(b.left + w * 0.06f * (j % 3), y + 1f * u), Size(w * 0.05f, hilada - 2f * u))
                        y += hilada; j++
                    }
                    drawRect(l.s(0.3f), Offset(b.left, b.top), Size(b.width, w * 0.012f))
                }
                drawPath(bordeRuinas(hondo, -w * 0.006f, -w * 0.006f), l.s(0.35f))
            }
            // El canto de la pintura: sombra adentro abajo y a la derecha, la luz en el labio de arriba a la izquierda.
            drawPath(bordeRuinas(forma, -w * 0.005f, -w * 0.006f), l.s(0.4f))
            drawPath(bordeRuinas(forma, w * 0.004f, w * 0.004f), l.b(0.5f))
            // Una escama de pintura que todavía cuelga del borde.
            val esc = Offset(c.x + w * e[2] * 0.7f, c.y - w * e[3] * 0.6f)
            val escama = poligonoRuinas(esc, esc + Offset(w * 0.018f, w * 0.004f), esc + Offset(w * 0.012f, w * 0.016f), esc + Offset(-w * 0.002f, w * 0.01f))
            translate(w * 0.004f, w * 0.005f) { drawPath(escama, l.s(0.3f)) }
            drawPath(escama, l.c(mezcla(P.YESO, P.YESO_SOL, 0.3f)))
        }
    }

    /** Cuánto sol del lucernario cae en [c] (para lo que se pinta una sola vez, sin recortar a la mancha). */
    fun solEn(g: MarcoRuinas, c: Offset, delante: Float = 0f): Float? {
        val e = g.enrejado ?: return null
        val o = g.corre(delante)
        return e.en(c.x / g.w - o.x, c.y / g.w - o.y)
    }

    /** La gotera: la mancha oscura que baja del techo, el verdín que le crece y las gotas que caen al cuenco de la cornisa. */
    private fun DrawScope.gotera(g: MarcoRuinas) {
        val w = g.w; val u = density
        val x = w * GOTERA_X
        val l = g.luz(x, w * 0.2f, 0f, solEn(g, Offset(x, w * 0.2f)))
        // La mancha mojada, más ancha abajo, con sus chorreras.
        val mojado = Path().apply {
            moveTo(x - w * 0.02f, 0f); lineTo(x + w * 0.022f, 0f)
            cubicTo(x + w * 0.05f, w * 0.12f, x + w * 0.02f, w * 0.2f, x + w * 0.06f, w * 0.33f)
            lineTo(x + w * 0.045f, g.apoyo); lineTo(x - w * 0.055f, g.apoyo)
            cubicTo(x - w * 0.04f, w * 0.28f, x - w * 0.06f, w * 0.15f, x - w * 0.03f, 0f); close()
        }
        drawPath(mojado, l.c(mezcla(P.YESO_SOMBRA, P.HIEDRA_OSC, 0.3f)).copy(alpha = 0.22f))
        // El verdín que crece en lo mojado: manchas de verde vivo abajo.
        for (i in 0 until 9) {
            val p = Offset(x + w * (azar(871, i) - 0.5f) * 0.08f, w * (0.2f + 0.26f * azar(872, i)))
            mancha(p, w * (0.012f + 0.016f * azar(873, i)), l.c(P.MUSGO).copy(alpha = 0.55f))
        }
        // Las chorreras finas.
        for (i in 0 until 4) {
            val x0 = x + w * (-0.02f + 0.013f * i)
            drawLine(l.c(P.YESO_SOMBRA).copy(alpha = 0.5f), Offset(x0, w * 0.02f), Offset(x0 + w * 0.004f, w * (0.22f + 0.15f * azar(874, i))), 1.1f * u, StrokeCap.Round)
        }
    }

    /** Las sombras de las hojas de la hiedra que cuelga sobre el lucernario: solo dentro de la mancha (de día y en los Sueños). */
    private fun DrawScope.sombrasDeHojas(g: MarcoRuinas) {
        val e = g.enrejado ?: return
        val w = g.w
        val m = e.camino(w, 0.3f)
        val sombra = g.a.sombra(if (g.a.oscuro) 0.3f else 0.2f)
        clipPath(m) {
            val ramas = listOf(
                listOf(Offset(0.02f, 0.0f), Offset(0.08f, 0.06f), Offset(0.12f, 0.13f), Offset(0.19f, 0.2f)),
                listOf(Offset(0.3f, 0.0f), Offset(0.33f, 0.07f), Offset(0.38f, 0.12f)),
                listOf(Offset(0.0f, 0.22f), Offset(0.06f, 0.26f), Offset(0.13f, 0.32f)),
            )
            ramas.forEachIndexed { r, pts ->
                val p = abiertaRuinas(pts.map { it * w })
                drawPath(p, sombra, style = Stroke(w * 0.004f, cap = StrokeCap.Round))
                for (i in 1 until pts.size) {
                    val a = pts[i - 1] * w; val b = pts[i] * w
                    for (j in 0 until 3) {
                        val c = a + (b - a) * ((j + 0.5f) / 3f)
                        val ang = Math.toDegrees(kotlin.math.atan2((b.x - a.x).toDouble(), -(b.y - a.y).toDouble())).toFloat()
                        drawPath(hojaHiedraRuinas(c, w * (0.028f + 0.014f * azar(881 + r, i * 3 + j)), ang + (if (j % 2 == 0) 60f else -60f), 0.9f), sombra)
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------- el herbario

    /**
     * El herbario: un cordel de bramante entre dos clavos, combado, con cuatro pliegos de papel sujetos con pinzas de madera, cada
     * uno con una planta prensada (un helecho, una espiga, una ramita de abedul y una umbela), sin nada escrito.
     */
    private fun DrawScope.herbario(g: MarcoRuinas) {
        val w = g.w; val u = density
        val a = Offset(w * 0.035f, w * 0.075f); val b = Offset(w * 0.965f, w * 0.062f)
        fun cordel(t: Float) = Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t + w * 0.06f * 4f * t * (1f - t))
        val pts = List(11) { cordel(it / 10f) }
        val lc = g.luz(w * 0.5f, w * 0.1f, 0.01f, solEn(g, Offset(w * 0.3f, w * 0.1f), 0.01f))
        val cuerda = abiertaRuinas(pts)
        translate(w * (if (g.a.oscuro) -0.004f else 0.008f), w * 0.01f) { drawPath(cuerda, lc.s(0.25f), style = Stroke(1.2f * u)) }
        drawPath(cuerda, lc.c(P.BRAMANTE), style = Stroke(1.3f * u))
        for (p in listOf(a, b)) clavo(p, g.luz(p.x, p.y, 0f, solEn(g, p)))
        // Los cuatro pliegos, cada uno un poco ladeado y a su altura según cuelga el cordel.
        val donde = listOf(0.15f, 0.36f, 0.6f, 0.8f)
        donde.forEachIndexed { i, t ->
            val arriba = cordel(t)
            val an = w * (0.082f + 0.01f * (i % 2)); val al = an * 1.32f
            val giro = (azar(891, i) - 0.5f) * 6f
            val centro = arriba + Offset(0f, al * 0.5f)
            val l = g.luz(centro.x, centro.y, 0.015f, solEn(g, centro, 0.015f))
            val hacia = if (g.a.oscuro && centro.x > w * 0.6f) 1f else -1f
            rotate(giro, arriba) {
                val hoja = Rect(arriba.x - an * 0.5f, arriba.y - w * 0.004f, arriba.x + an * 0.5f, arriba.y - w * 0.004f + al)
                // La sombra del pliego en la pared, corrida lejos de la luz.
                drawRect(l.s(0.3f), hoja.topLeft + Offset(-hacia * w * 0.012f, w * 0.012f), hoja.size)
                drawRect(l.c(mezcla(P.PAPEL, P.CARTON, 0.15f + 0.1f * (i % 2))), hoja.topLeft, hoja.size)
                // El papel ondulado por la humedad: dos bandas de sombra y luz, y la esquina de abajo levantada.
                drawRect(l.s(0.1f), Offset(hoja.left, hoja.top + al * 0.55f), Size(an, al * 0.2f))
                drawLine(l.b(0.4f), Offset(hoja.left, hoja.top + al * 0.53f), Offset(hoja.right, hoja.top + al * 0.53f), 1f * u)
                drawRect(l.c(P.KRAFT).copy(alpha = 0.18f), Offset(hoja.left, hoja.bottom - al * 0.1f), Size(an, al * 0.1f))
                planta(i, hoja, l, hacia)
                // Las pinzas de madera.
                val pinzas = if (i % 2 == 0) listOf(0.22f, 0.78f) else listOf(0.5f)
                pinzas.forEach { f -> pinza(Offset(hoja.left + an * f, arriba.y), l, hacia) }
            }
        }
    }

    /** Una pinza de madera sobre el cordel, con su muelle. */
    private fun DrawScope.pinza(c: Offset, l: LuzRuinas, hacia: Float) {
        val u = density
        val an = 4.2f * u; val al = 15f * u
        val r = Rect(c.x - an / 2, c.y - al * 0.4f, c.x + an / 2, c.y + al * 0.6f)
        drawRect(l.s(0.3f), r.topLeft + Offset(-hacia * 1.5f * u, 1.5f * u), r.size)
        drawRect(l.c(Color(0xFFC9A774)), r.topLeft, r.size)
        drawRect(l.s(0.25f), Offset(if (hacia < 0f) r.right - an * 0.35f else r.left, r.top), Size(an * 0.35f, al))
        drawLine(l.c(P.NIQUEL), Offset(r.left - 0.6f * u, c.y), Offset(r.right + 0.6f * u, c.y), 1.4f * u)
        drawLine(l.b(0.5f), Offset(r.left + 0.6f * u, r.top), Offset(r.left + 0.6f * u, r.bottom), 0.7f * u)
    }

    /** Un clavo en la pared, con su cabeza, su brillo y su sombra. */
    fun DrawScope.clavo(c: Offset, l: LuzRuinas) {
        val u = density
        drawCircle(l.s(0.4f), 2f * u, c + Offset(1.2f * u, 1.2f * u))
        drawCircle(l.c(Color(0xFF5A3A28)), 2f * u, c)
        drawCircle(l.b(0.6f), 0.8f * u, c - Offset(0.6f * u, 0.6f * u))
    }

    /** La planta prensada de cada pliego, con su tira de papel engomado que la sujeta. */
    private fun DrawScope.planta(i: Int, r: Rect, l: LuzRuinas, hacia: Float) {
        val u = density
        val seca = mezcla(P.HELECHO, P.KRAFT, 0.3f)
        val pie = Offset(r.center.x + r.width * 0.05f, r.bottom - r.height * 0.1f)
        when (i) {
            0 -> frondaRuinas(pie, r.height * 0.78f, -8f, 0.25f, seca, l, hacia, 901, 8)
            1 -> {
                // Una espiga de gramínea: el tallo y las espiguillas a los dos lados.
                val tope = Offset(r.center.x - r.width * 0.08f, r.top + r.height * 0.14f)
                drawLine(l.c(mezcla(P.KRAFT, P.HELECHO, 0.3f)), pie, tope, 1.1f * u)
                for (k in 0 until 9) {
                    val t = 0.08f + k * 0.055f
                    val p = tope + (pie - tope) * t
                    val lado = if (k % 2 == 0) -1f else 1f
                    rotate(lado * 28f, p) { drawOval(l.c(mezcla(P.OCRE, P.KRAFT, 0.4f)), Offset(p.x - 1.6f * u, p.y - 5f * u), Size(3.2f * u, 6.5f * u)) }
                }
                drawLine(l.c(mezcla(P.HELECHO, P.KRAFT, 0.5f)), pie + Offset(0f, -r.height * 0.3f), pie + Offset(r.width * 0.25f, -r.height * 0.42f), 1f * u)
            }
            2 -> {
                // Una ramita de abedul: el tallo pardo y las hojas ovaladas, dentadas, alternas.
                val tope = Offset(r.center.x + r.width * 0.1f, r.top + r.height * 0.12f)
                val tallo = abiertaRuinas(listOf(pie, pie + (tope - pie) * 0.5f + Offset(-r.width * 0.12f, 0f), tope))
                drawPath(tallo, l.c(Color(0xFF6A4630)), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                for (k in 0 until 6) {
                    val t = 0.15f + k * 0.14f
                    val p = pie + (tope - pie) * t + Offset(-r.width * 0.12f * 4f * t * (1f - t), 0f)
                    val lado = if (k % 2 == 0) -1f else 1f
                    val hoja = cerradaRuinas(listOf(Offset(0f, 0f), Offset(lado * 4f * u, -2.5f * u), Offset(lado * 7.5f * u, -1f * u), Offset(lado * 4f * u, 2.2f * u)).map { it + p })
                    drawPath(hoja, l.c(mezcla(P.HIEDRA_LUZ, P.KRAFT, 0.35f + 0.1f * (k % 2))))
                    drawLine(l.c(mezcla(P.HELECHO, P.KRAFT, 0.6f)), p, p + Offset(lado * 6.5f * u, -1f * u), 0.5f * u)
                }
            }
            else -> {
                // Una umbela: los radios desde un punto y sus florecillas blancas en corona.
                val centro = Offset(r.center.x, r.top + r.height * 0.32f)
                drawLine(l.c(mezcla(P.HELECHO, P.KRAFT, 0.4f)), pie, centro, 1.1f * u)
                for (k in 0 until 9) {
                    val ang = -150f + k * 15f
                    val q = giradoRuinas(centro, 0f, -r.width * 0.34f, ang + 90f)
                    drawLine(l.c(mezcla(P.HELECHO, P.KRAFT, 0.45f)), centro, q, 0.6f * u)
                    for (j in 0 until 3) drawCircle(l.c(Color(0xFFF2ECDC)), 1.5f * u, q + Offset((j - 1) * 1.8f * u, -1f * u * (j % 2)))
                }
                // Dos hojas recortadas al pie.
                frondaRuinas(pie + Offset(0f, -r.height * 0.12f), r.height * 0.24f, 40f, 0.2f, mezcla(P.HELECHO, P.KRAFT, 0.35f), l, hacia, 905, 5)
            }
        }
        // La tira de papel engomado que sujeta el tallo.
        rotate(-8f, pie) { drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.45f)), Offset(pie.x - r.width * 0.12f, pie.y - r.height * 0.04f), Size(r.width * 0.24f, r.height * 0.05f)) }
    }

    /** La hiedra que cuelga del lucernario, arriba a la izquierda: tres guías que bajan por delante de la pared. */
    private fun DrawScope.hiedraDelTecho(g: MarcoRuinas) {
        val w = g.w
        val hacia = if (g.a.oscuro && !g.a.noche) 1f else -1f
        val guias = listOf(
            listOf(Offset(0.0f, 0.0f), Offset(0.03f, 0.05f), Offset(0.035f, 0.11f), Offset(0.05f, 0.17f), Offset(0.045f, 0.23f), Offset(0.06f, 0.29f)),
            listOf(Offset(0.05f, 0.0f), Offset(0.09f, 0.04f), Offset(0.11f, 0.09f), Offset(0.12f, 0.14f)),
            listOf(Offset(0.16f, 0.0f), Offset(0.17f, 0.035f), Offset(0.2f, 0.06f)),
        )
        guias.forEachIndexed { i, pts ->
            talloRuinas(pts.map { it * w }, w * 0.034f, 910 + i, { p -> g.luz(p.x, p.y, 0.02f, solEn(g, p, 0.02f)) }, hacia, 1, 1.4f)
        }
    }

    // ---------------------------------------------------------------- el fondo de los huecos

    /**
     * El fondo de un hueco: la trasera de tablas de roble oscuro a lo alto, con la sombra de la tabla de arriba y del costado, sus
     * cercos de agua y, en algunos huecos, una tabla que falta y deja ver el yeso verde de la pared. Abajo, la cara de la balda.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, g: MarcoRuinas) {
        val w = g.w; val u = density
        val hondo = honduraRuinas(g)
        conManchaRuinas(g, 0f, r) { s ->
            // De día, de la luz de la izquierda a la sombra; de noche, de una luz a la otra (la luz tomada en seis sitios).
            val muestras = List(6) { i -> g.luz(r.left + r.width * i / 5f, r.top + r.height * 0.55f, 0f, s).c(P.ROBLE_OSC) }
            drawRect(Brush.horizontalGradient(muestras, r.left, r.right), Offset(r.left, r.top - 1f), Size(r.width, r.height + 1f))
            // La tabla que falta: deja ver el yeso, hondo y frío.
            val falta = Math.floorMod(k * 7 + 3, 5)
            if (falta < 3) {
                val x0 = r.left + r.width * (0.12f + 0.22f * falta + 0.05f * azar(950, k))
                val yeso = g.luz(x0, r.center.y, 0f, s).c(P.YESO_SOMBRA)
                val an = w * 0.07f
                val rota = r.top + r.height * (0.48f + 0.12f * azar(953, k))
                // Arriba falta la tabla y se ve el yeso; abajo queda el trozo, con su rotura en astillas.
                val hueco = poligonoRuinas(listOf(Offset(x0, r.top), Offset(x0 + an, r.top), Offset(x0 + an, rota - r.height * 0.06f), Offset(x0 + an * 0.7f, rota + r.height * 0.02f),
                    Offset(x0 + an * 0.45f, rota - r.height * 0.05f), Offset(x0 + an * 0.2f, rota + r.height * 0.04f), Offset(x0, rota - r.height * 0.02f)))
                drawPath(hueco, mezcla(yeso, hondo, 0.45f))
                drawPath(bordeRuinas(hueco, 0f, -2f * u), g.luz(x0, rota, 0f, s).c(P.GASTADO))
                drawRect(hondo.copy(alpha = 0.4f), Offset(x0 + an - 2f * u, r.top), Size(2f * u, rota - r.top))
            }
        }
        // La veta del roble de la trasera (baldosa pintada una vez): las vetas hondas en el oscuro del hueco y las claras en el color
        // del roble con la luz que le toca. Así el oscuro de la noche tiene forma.
        val lv = g.luz(r.center.x, r.center.y, 0f)
        vetaRuinas(r, mezcla(hondo, Color.Black, 0.2f), if (g.a.oscuro) 0.55f else 0.4f, oscuras = true)
        vetaRuinas(r, lv.c(P.ROBLE), if (g.a.oscuro) 0.42f else 0.32f, oscuras = false)
        // Las juntas de las tablas de la trasera, con su luz.
        val tabla = w * 0.088f
        var x = r.left + tabla * (0.4f + 0.5f * azar(951, k))
        var j = 0
        while (x < r.right - 2f * u) {
            drawLine(hondo.copy(alpha = 0.55f), Offset(x, r.top), Offset(x, r.bottom), 1.2f * u)
            drawLine(g.luz(x, r.center.y, 0f).b(0.12f), Offset(x + 1.2f * u, r.top), Offset(x + 1.2f * u, r.bottom), 0.7f * u)
            x += tabla * (0.9f + 0.2f * azar(952, k * 13 + j)); j++
        }
        // Los cercos del agua que bajó por la trasera: una línea ondulada más clara (el barniz lavado) con sus chorreras debajo.
        val s = 960 + k * 17
        val ycc = r.top + r.height * (0.42f + 0.2f * azar(s, 1))
        val x0c = r.left + r.width * (0.1f + 0.4f * azar(s, 2)); val x1c = x0c + r.width * (0.25f + 0.2f * azar(s, 3))
        val cerco = g.luz((x0c + x1c) / 2, ycc, 0f).c(P.GASTADO).copy(alpha = 0.3f)
        val linea = abiertaRuinas(List(5) { j -> Offset(x0c + (x1c - x0c) * j / 4f, ycc + r.height * 0.04f * (if (j % 2 == 0) 1f else -1f) * azar(s, 10 + j)) })
        drawPath(linea, cerco, style = Stroke(1.2f * u))
        for (j in 0 until 3) {
            val xx = x0c + (x1c - x0c) * (0.2f + 0.3f * j)
            drawLine(cerco.copy(alpha = 0.18f), Offset(xx, ycc), Offset(xx + 0.6f * u, ycc + r.height * (0.15f + 0.2f * azar(s, 20 + j))), 2.4f * u, StrokeCap.Round)
        }
        // La sombra de la tabla de arriba: franca arriba y en diagonal (la luz baja desde la izquierda).
        val ys = r.top + r.height * 0.26f
        drawPath(Path().apply { moveTo(r.left, r.top - 1f); lineTo(r.right, r.top - 1f); lineTo(r.right, ys + r.height * 0.06f); lineTo(r.left, ys - r.height * 0.06f); close() },
            Brush.verticalGradient(listOf(hondo.copy(alpha = 0.78f), hondo.copy(alpha = 0.42f)), r.top, ys))
        // La sombra del costado: de día cae a la derecha del izquierdo; de noche, lejos de la luz que más cerca está.
        if (!g.a.oscuro) drawRect(hondo.copy(alpha = 0.32f), Offset(r.left, r.top), Size(w * 0.05f, r.height))
        else {
            drawRect(hondo.copy(alpha = 0.3f), Offset(r.left, r.top), Size(w * 0.03f, r.height))
            drawRect(hondo.copy(alpha = 0.3f), Offset(r.right - w * 0.03f, r.top), Size(w * 0.03f, r.height))
        }
        // Los cantos de dentro de los costados (se ve su grueso): el izquierdo en sombra, el derecho a la luz.
        val gc = w * 0.016f
        val li = g.luz(r.left, r.center.y, 0.02f)
        val ld = g.luz(r.right, r.center.y, 0.02f)
        drawPath(poligonoRuinas(Offset(r.left, r.top), Offset(r.left + gc, r.top + gc * 0.6f), Offset(r.left + gc, r.bottom - gc * 0.4f), Offset(r.left, r.bottom)),
            mezcla(li.c(P.ROBLE_OSC), hondo, 0.3f))
        drawPath(poligonoRuinas(Offset(r.right, r.top), Offset(r.right - gc, r.top + gc * 0.6f), Offset(r.right - gc, r.bottom - gc * 0.4f), Offset(r.right, r.bottom)),
            ld.c(P.ROBLE))
        drawLine(ld.b(0.3f), Offset(r.right - gc, r.top + gc), Offset(r.right - gc, r.bottom), 0.8f * u)
        if (ld.k > 0.1f) drawLine(ld.filo(0.7f), Offset(r.right - gc * 0.5f, r.top + gc), Offset(r.right - gc * 0.5f, r.bottom), 1.2f * u)
        if (li.k > 0.1f) drawLine(li.filo(0.6f), Offset(r.left + gc * 0.5f, r.top + gc), Offset(r.left + gc * 0.5f, r.bottom), 1.2f * u)
        // Los tetones de latón donde apoya la balda, en cada canto.
        drawCircle(ld.c(P.LATON), 1.6f * u, Offset(r.right - gc * 0.5f, r.bottom - 3.6f * u))
        drawCircle(li.c(P.LATON_OSC), 1.6f * u, Offset(r.left + gc * 0.5f, r.bottom - 3.6f * u))
        // La cara de la balda donde pisan las cosas: un filo de roble más claro que se hunde hacia el fondo.
        val yc = floor(r.bottom - 5f * u)
        drawRect(Brush.verticalGradient(listOf(mezcla(g.luz(r.center.x, r.bottom, 0.02f).c(P.ROBLE), hondo, 0.35f), g.luz(r.center.x, r.bottom, 0.03f).c(P.ROBLE)), yc, r.bottom),
            Offset(r.left, yc), Size(r.width, r.bottom - yc + 1f))
    }

    /** La veta de la trasera, en baldosa sobre [r]: las líneas de [color], hondas o claras, con [alfa]. */
    private fun DrawScope.vetaRuinas(r: Rect, color: Color, alfa: Float, oscuras: Boolean) {
        val b = if (oscuras) VETA_HONDA else VETA_CLARA
        val filtro = ColorFilter.tint(color, BlendMode.SrcIn)
        clipRect(r.left, r.top, r.right, r.bottom) {
            var y = r.top
            while (y < r.bottom) {
                var x = r.left
                while (x < r.right) { drawImage(b, Offset(x, y), alpha = alfa, colorFilter = filtro); x += b.width }
                y += b.height
            }
        }
    }

    /** Las vetas del roble, a lo alto, pintadas una sola vez: unas hondas y otras claras, en dos baldosas para teñirlas aparte. */
    private val VETA_HONDA: ImageBitmap by lazy { baldosaVeta(true) }
    private val VETA_CLARA: ImageBitmap by lazy { baldosaVeta(false) }

    private fun baldosaVeta(hondas: Boolean): ImageBitmap {
        val an = 240; val al = 320
        val img = ImageBitmap(an, al)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(an.toFloat(), al.toFloat())) {
            val n = 13
            for (k in 0 until n) {
                if ((k % 2 == 0) != hondas) continue
                val x0 = an * (k + azar(990, k)) / n
                val amp = 1.5f + 3f * azar(991, k)
                val fase = 6.28f * azar(992, k)
                val p = Path()
                for (i in 0..16) {
                    val y = al * i / 16f
                    // La veta baja a lo largo y vuelve al mismo sitio arriba y abajo, para que la baldosa no deje costura.
                    val x = x0 + amp * kotlin.math.sin(fase + 6.2832f * i / 16f)
                    if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
                }
                drawPath(p, Color.White.copy(alpha = 0.4f + 0.5f * azar(993, k)), style = Stroke(0.8f + 1.8f * azar(994, k)))
            }
            if (hondas) {
                // Un nudo, cerrado sobre sí mismo.
                drawOval(Color.White.copy(alpha = 0.6f), Offset(an * 0.62f, al * 0.4f), Size(14f, 30f), style = Stroke(1.6f))
                drawOval(Color.White.copy(alpha = 0.4f), Offset(an * 0.62f - 4f, al * 0.4f - 8f), Size(22f, 46f), style = Stroke(1.2f))
            }
        }
        return img
    }

    /** El oscuro en que se hunde lo de dentro de los huecos: un pardo de roble de día, violáceo en los Sueños, el aire de la noche de noche. */
    fun honduraRuinas(g: MarcoRuinas): Color = when {
        g.a.oscuro -> g.a.hondo
        g.a.noche -> mezcla(P.ANOCHECER, P.ROBLE_HONDO, 0.4f)
        else -> Color(0xFF22160E)
    }
}
