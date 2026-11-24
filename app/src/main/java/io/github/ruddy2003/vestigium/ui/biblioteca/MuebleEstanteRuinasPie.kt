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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.floor

/**
 * El pie de la librería: el zócalo moldurado (con un trozo comido donde empujan las raíces), el suelo de baldosa hidráulica de
 * dibujo inventado (un círculo que se completa entre cuatro losetas), con una loseta levantada y otra que falta, y lo que hay en
 * él: el anafre con sus brasas y la tetera de esmalte encima, el haz de leña, el cesto de piñas, el diente de león con su bola de
 * vilanos y el helecho del rincón.
 */
internal object PieEstanteRuinas {
    fun DrawScope.pie(g: MarcoRuinas) {
        val w = g.w; val h = g.h
        val y0 = g.fondo
        val suelo = y0 + w * 0.075f
        if (h - suelo < 4f) return
        zocalo(g, y0, suelo)
        baldosas(g, suelo)
        lena(g, suelo)
        anafre(g)
        cubo(g)
        cesto(g)
        dienteDeLeon(g)
        helecho(g)
    }

    private fun hacia(g: MarcoRuinas, x: Float): Float = if (g.a.oscuro) (if (x > g.anafre.x) -1f else 1f) else -1f

    // ---------------------------------------------------------------- el zócalo

    private fun DrawScope.zocalo(g: MarcoRuinas, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val cols = listOf(0f, w * 0.25f, w * 0.5f, w * 0.75f, w)
        fun c(col: Color, y: Float) = cols.map { g.luz(it, y, 0.03f).c(col) }
        // Debajo, la fila de la junta con la última tabla.
        drawRect(g.luz(w * 0.5f, y0, 0.03f).c(P.ROBLE_OSC), Offset(0f, floor(y0)), Size(w, 1f))
        drawRect(Brush.horizontalGradient(c(P.ROBLE, (y0 + y1) / 2), 0f, w), Offset(0f, y0), Size(w, y1 - y0 + 1f))
        // La moldura de arriba (un bocel con su luz) y el filete de abajo, oscuro.
        val m1 = y0 + (y1 - y0) * 0.22f
        drawRect(Brush.verticalGradient(listOf(g.luz(w * 0.4f, y0, 0.04f).c(P.ROBLE_LUZ), g.luz(w * 0.4f, y0, 0.04f).c(P.ROBLE_OSC)), y0, m1), Offset(0f, y0), Size(w, m1 - y0))
        drawRect(Brush.horizontalGradient(c(P.ROBLE_OSC, y1), 0f, w), Offset(0f, y1 - (y1 - y0) * 0.14f), Size(w, (y1 - y0) * 0.14f))
        // Los dados de las pilastras, un poco salientes.
        for ((a, b) in listOf(0f to g.x0, g.x1 to w)) {
            val l = g.luz((a + b) / 2, (y0 + y1) / 2, 0.04f)
            drawRect(l.c(P.ROBLE_LUZ), Offset(a, y0), Size(b - a, y1 - y0))
            drawRect(l.c(P.ROBLE), Offset(a + (b - a) * 0.15f, m1), Size((b - a) * 0.7f, (y1 - m1) * 0.75f))
            drawRect(l.s(0.35f), Offset(if (a < w / 2) b - 2f * u else a, y0), Size(2f * u, y1 - y0))
        }
        // El trozo comido por el agua donde empujan las raíces, con la madera esponjosa y las raíces que salen.
        val l = g.luz(w * 0.52f, y1, 0.03f)
        val hueco = Path().apply {
            moveTo(w * 0.44f, y1 + 1f); lineTo(w * 0.45f, y1 - (y1 - y0) * 0.45f); lineTo(w * 0.48f, y1 - (y1 - y0) * 0.6f)
            lineTo(w * 0.53f, y1 - (y1 - y0) * 0.5f); lineTo(w * 0.56f, y1 - (y1 - y0) * 0.3f); lineTo(w * 0.57f, y1 + 1f); close()
        }
        drawPath(hueco, mezcla(l.c(P.ROBLE_HONDO), g.a.hondo, 0.3f))
        drawPath(bordeRuinas(hueco, 0f, 2f * u), l.c(P.GASTADO))
        for (i in 0 until 3) {
            val r = abiertaRuinas(listOf(Offset(w * (0.48f + 0.02f * i), y1 - (y1 - y0) * 0.3f), Offset(w * (0.47f + 0.03f * i), y1 + w * 0.02f), Offset(w * (0.44f + 0.06f * i), y1 + w * (0.05f + 0.02f * i))))
            drawPath(r, l.c(Color(0xFF8A6A4A)), style = Stroke((2.4f - 0.5f * i) * u, cap = StrokeCap.Round))
        }
        // Las vetas, el barniz comido y el musgo a lo largo del pie.
        for (i in 0 until 5) drawOval(g.luz(w * azar(2401, i), y0, 0.03f).c(P.GASTADO).copy(alpha = 0.25f), Offset(w * azar(2401, i) - w * 0.06f, m1 + (y1 - m1) * 0.3f), Size(w * 0.12f, (y1 - m1) * 0.3f))
        listOf(0.1f, 0.33f, 0.62f, 0.86f).forEachIndexed { i, fx ->
            musgoRuinas(Offset(w * fx, y1), w * (0.05f + 0.03f * (i % 2)), w * 0.014f, g.luz(w * fx, y1, 0.04f), 2410 + i)
        }
        if (g.a.oscuro) {
            val la = g.luz(g.anafre.x, y0, 0.04f)
            if (la.k > 0.05f) drawLine(la.filo(0.8f), Offset(0f, y0 + 1f * u), Offset(w * 0.6f, y0 + 1f * u), 1.4f * u)
        }
    }

    // ---------------------------------------------------------------- el suelo de baldosa hidráulica

    /**
     * La baldosa hidráulica en perspectiva: tres filas que crecen hacia delante; cada cruce de juntas lleva un círculo de terracota
     * que se reparte entre cuatro losetas, con un punto ocre en medio. Una loseta falta (tierra y musgo) y otra está levantada.
     */
    private fun DrawScope.baldosas(g: MarcoRuinas, y0: Float) {
        val w = g.w; val h = g.h; val u = density
        val fondoAlto = h - y0
        val loseta = mezcla(P.OCRE, P.BALDOSA_GRIS, 0.42f)
        val xs = List(7) { w * it / 6f }
        fun fila(y: Float, col: Color) = Brush.horizontalGradient(xs.map { g.luz(it, y, 0f).c(col) }, 0f, w)
        if (g.a.oscuro) {
            // De noche: la baldosa hundida y, encima, el charco de las brasas que la pinta con su color encendido.
            drawRect(g.luz(w * 0.95f, y0, 0f).c(loseta), Offset(0f, y0), Size(w, fondoAlto))
            clipRect(0f, y0, w, h) { with(ParedEstanteRuinas) { charcoRuinas(g, Offset(g.anafre.x, h - w * 0.04f), w * 0.75f, loseta) } }
        } else drawRect(fila(y0 + fondoAlto * 0.5f, loseta), Offset(0f, y0), Size(w, fondoAlto))
        // Las filas: el borde de cada una, en fracción del alto (crecen hacia delante).
        val filas = listOf(0f, 0.22f, 0.5f, 0.86f, 1.3f)
        val cx = w * 0.5f
        fun ancho(t: Float) = w * (0.13f + 0.07f * t)
        fun xEn(i: Float, t: Float): Float = cx + i * ancho(t)
        fun yEn(t: Float) = y0 + fondoAlto * t * 0.78f
        val juntas = Path()
        clipRect(0f, y0, w, h) {
            for (f in filas.indices) {
                val t = filas[f]
                val y = yEn(t)
                if (y > h + fondoAlto) break
                val an = ancho(t)
                val al = fondoAlto * 0.2f * (0.7f + 0.6f * t)
                val cruces = Path(); val puntos = Path()
                val i0 = -((cx + an) / an).toInt() - 1; val i1 = -i0
                for (i in i0..i1) {
                    val x = xEn(i.toFloat(), t)
                    if (x < -an || x > w + an) continue
                    cruces.addOval(Rect(x - an * 0.3f, y - al * 0.3f, x + an * 0.3f, y + al * 0.3f))
                    puntos.addOval(Rect(x - an * 0.07f, y - al * 0.07f, x + an * 0.07f, y + al * 0.07f))
                    if (f + 1 < filas.size) { juntas.moveTo(x, y); juntas.lineTo(xEn(i.toFloat(), filas[f + 1]), yEn(filas[f + 1])) }
                }
                juntas.moveTo(0f, y); juntas.lineTo(w, y)
                drawPath(cruces, fila(y, P.BARRO))
                drawPath(puntos, fila(y, P.BALDOSA_GRIS))
            }
            drawPath(juntas, fila(y0 + fondoAlto * 0.5f, P.TIERRA), style = Stroke(1.2f * u))
            // La tierra que se metió en las juntas y algo de musgo: manchas pardas sueltas.
            for (k in 0 until 6) {
                val p = Offset(w * azar(2430, k), y0 + fondoAlto * (0.25f + 0.7f * azar(2431, k)))
                drawOval(g.luz(p.x, p.y, 0f).c(P.TIERRA).copy(alpha = 0.3f), Offset(p.x - w * 0.05f, p.y - w * 0.01f), Size(w * 0.1f, w * 0.02f))
            }
            // La loseta que falta: tierra con musgo.
            val falta = poligonoRuinas(Offset(xEn(2f, 0.22f), yEn(0.22f)), Offset(xEn(3f, 0.22f), yEn(0.22f)), Offset(xEn(3f, 0.5f), yEn(0.5f)), Offset(xEn(2f, 0.5f), yEn(0.5f)))
            val lf = g.luz(xEn(2.5f, 0.35f), yEn(0.35f), 0f)
            drawPath(falta, lf.c(P.TIERRA))
            drawPath(bordeRuinas(falta, 0f, 2.4f * u), lf.s(0.4f))
            for (k in 0 until 7) drawCircle(lf.c(if (k % 2 == 0) P.MUSGO else P.MUSGO_LUZ), w * (0.008f + 0.006f * azar(2420, k)), Offset(xEn(2.15f + 0.12f * k, 0.36f), yEn(0.3f + 0.1f * azar(2421, k))))
            // La sombra del mueble en el suelo, junto al zócalo.
            drawRect(Brush.verticalGradient(listOf(g.a.sombra(0.6f), g.a.sombra(0f)), y0, y0 + fondoAlto * 0.3f), Offset(0f, y0), Size(w, fondoAlto * 0.3f))
        }
    }

    // ---------------------------------------------------------------- la leña

    /** El haz de leña atado con bramante, tumbado a la izquierda y cortado por el borde. */
    private fun DrawScope.lena(g: MarcoRuinas, y0: Float) {
        val w = g.w; val h = g.h; val u = density
        val c = Offset(w * 0.06f, h - w * 0.055f)
        val l = g.luz(c.x, c.y, 0.05f)
        val hc = hacia(g, c.x)
        contactoRuinas(c.x, h - w * 0.02f, w * 0.24f, w * 0.03f, l, hc, 0.55f)
        rotate(-8f, c) {
            for (i in 0 until 7) {
                val y = c.y - w * 0.05f + w * 0.015f * i
                val x0 = c.x - w * 0.14f + w * 0.01f * (i % 3); val x1 = c.x + w * 0.12f - w * 0.008f * (i % 2)
                val gr = w * (0.011f + 0.004f * (i % 2))
                drawLine(l.c(if (i % 2 == 0) Color(0xFF7A5A3E) else Color(0xFF8E6A46)), Offset(x0, y), Offset(x1, y), gr * 2, StrokeCap.Round)
                drawLine(l.b(0.25f), Offset(x0, y - gr * 0.5f), Offset(x1, y - gr * 0.5f), 0.8f * u)
                drawCircle(l.c(Color(0xFFD9B884)), gr * 0.85f, Offset(x1, y))
                drawCircle(l.c(Color(0xFFA0784A)), gr * 0.4f, Offset(x1, y), style = Stroke(0.6f * u))
            }
            for (fx in listOf(-0.06f, 0.06f)) drawLine(l.c(P.BRAMANTE), Offset(c.x + w * fx, c.y - w * 0.065f), Offset(c.x + w * fx + w * 0.006f, c.y + w * 0.05f), 1.6f * u)
            if (l.k > 0.08f) drawLine(l.filo(0.6f), Offset(c.x - w * 0.12f, c.y - w * 0.06f), Offset(c.x + w * 0.12f, c.y - w * 0.06f), 1.2f * u)
        }
    }

    // ---------------------------------------------------------------- el anafre con la tetera

    /**
     * El anafre de hierro (un cilindro con su boca de rejilla donde se ven las brasas) y la tetera de esmalte azul encima, con su
     * asa, su pico y el borde de la tapa. De noche las brasas arden y lo cercano se enciende.
     */
    private fun DrawScope.anafre(g: MarcoRuinas) {
        val w = g.w; val h = g.h; val u = density
        val cx = g.anafre.x
        val pie = h - w * 0.03f
        val an = w * 0.17f; val al = w * 0.085f
        val r = Rect(cx - an / 2, pie - al, cx + an / 2, pie)
        val l = g.luz(cx, r.center.y, 0.06f)
        val hc = hacia(g, cx)
        val arde = g.a.anafre
        contactoRuinas(cx, pie, an * 1.15f, w * 0.03f, l, hc, 0.55f)
        // El anafre de barro: un cuerpo de tronco de cono, más ancho arriba, con su boca en arco abajo (las brasas o la ceniza) y el
        // labio grueso arriba con tres tetones donde descansa la tetera.
        val cuerpo = poligonoRuinas(Offset(r.left + an * 0.14f, r.bottom), Offset(r.right - an * 0.14f, r.bottom), Offset(r.right, r.top + al * 0.12f), Offset(r.left, r.top + al * 0.12f))
        modeladoRapidoRuinas(cuerpo, l.c(P.BARRO), l, hc, 1.1f, 0.3f)
        drawRect(l.c(P.BARRO_OSC), Offset(r.left + an * 0.12f, r.bottom - al * 0.1f), Size(an * 0.76f, al * 0.1f))
        // El hollín que sube de la boca y el salitre.
        val boca = Path().apply {
            moveTo(cx - an * 0.18f, r.bottom - al * 0.1f); lineTo(cx - an * 0.18f, r.bottom - al * 0.42f)
            quadraticTo(cx, r.bottom - al * 0.72f, cx + an * 0.18f, r.bottom - al * 0.42f); lineTo(cx + an * 0.18f, r.bottom - al * 0.1f); close()
        }
        drawOval(l.c(P.HIERRO).copy(alpha = 0.3f), Offset(cx - an * 0.2f, r.top + al * 0.15f), Size(an * 0.4f, al * 0.45f))
        drawPath(boca, if (arde > 0f) mezcla(P.BRASA, P.FUEGO, 0.25f) else l.c(Color(0xFF3A302A)))
        if (arde > 0f) {
            for (k in 0 until 6) drawCircle(if (k % 2 == 0) P.LLAMA.copy(alpha = arde) else P.BRASA, al * (0.07f + 0.04f * azar(2501, k)),
                Offset(cx - an * 0.13f + an * 0.052f * k, r.bottom - al * (0.18f + 0.1f * azar(2502, k))))
            mancha(Offset(cx, r.bottom - al * 0.3f), an * 0.7f, P.FUEGO.copy(alpha = 0.35f * arde))
        } else for (k in 0 until 4) drawCircle(l.c(Color(0xFF8A847C)), al * 0.07f, Offset(cx - an * 0.1f + an * 0.065f * k, r.bottom - al * 0.17f))
        drawPath(bordeRuinas(boca, 0f, al * 0.08f), l.s(0.5f))
        val labio = Rect(r.left - an * 0.03f, r.top - al * 0.04f, r.right + an * 0.03f, r.top + al * 0.16f)
        drawRoundRect(Brush.verticalGradient(listOf(l.c(mezcla(P.BARRO, P.OCRE, 0.3f)), l.c(P.BARRO_OSC)), labio.top, labio.bottom), labio.topLeft, labio.size, CornerRadius(labio.height * 0.4f))
        drawLine(l.b(0.4f), Offset(labio.left + 2f * u, labio.top + 0.8f * u), Offset(labio.right - 2f * u, labio.top + 0.8f * u), 1f * u)
        if (arde > 0f) drawLine(P.LLAMA.copy(alpha = 0.8f * arde), Offset(labio.left + an * 0.2f, labio.top), Offset(labio.right - an * 0.2f, labio.top), 1.6f * u)
        for (fx in listOf(-0.36f, 0f, 0.36f)) drawRoundRect(l.c(P.BARRO_OSC), Offset(cx + an * fx - 2.4f * u, labio.top - 3f * u), Size(4.8f * u, 3.4f * u), CornerRadius(1.4f * u))
        val borde = labio
        // La tetera: panza ancha, tapa con su pomo, el pico a un lado y el asa de alambre con su puño de madera.
        val ta = w * 0.105f; val tan = w * 0.14f
        val tb = borde.top - 2.4f * u
        val panza = cerradaRuinas(listOf(Offset(cx - tan * 0.32f, tb - ta * 0.78f), Offset(cx + tan * 0.32f, tb - ta * 0.78f), Offset(cx + tan * 0.5f, tb - ta * 0.35f),
            Offset(cx + tan * 0.45f, tb - 1f * u), Offset(cx - tan * 0.45f, tb - 1f * u), Offset(cx - tan * 0.5f, tb - ta * 0.35f)))
        val lt = g.luz(cx, tb - ta * 0.5f, 0.07f)
        val pico = Path().apply {
            moveTo(cx + tan * 0.4f, tb - ta * 0.35f); cubicTo(cx + tan * 0.62f, tb - ta * 0.4f, cx + tan * 0.66f, tb - ta * 0.7f, cx + tan * 0.78f, tb - ta * 0.82f)
            lineTo(cx + tan * 0.72f, tb - ta * 0.62f); cubicTo(cx + tan * 0.6f, tb - ta * 0.45f, cx + tan * 0.56f, tb - ta * 0.2f, cx + tan * 0.38f, tb - ta * 0.14f); close()
        }
        drawPath(pico, lt.c(P.ESMALTE))
        drawPath(bordeRuinas(pico, 0f, ta * 0.05f), lt.b(0.35f))
        modeladoRapidoRuinas(panza, lt.c(P.ESMALTE), lt, hc, 1f, 0.45f)
        drawRect(lt.c(P.ESMALTE_BLANCO), Offset(cx - tan * 0.48f, tb - ta * 0.3f), Size(tan * 0.96f, ta * 0.05f))
        drawPath(bultoRuinas(Offset(cx + tan * 0.28f, tb - ta * 0.62f), tan * 0.03f, ta * 0.035f, 2510, 7, 0.3f), lt.c(P.HIERRO))
        val tapa = Rect(cx - tan * 0.3f, tb - ta * 0.86f, cx + tan * 0.3f, tb - ta * 0.74f)
        drawOval(lt.c(P.ESMALTE_LUZ), tapa.topLeft, tapa.size)
        drawCircle(lt.c(P.HIERRO), tan * 0.05f, Offset(cx, tapa.top))
        val asa = Path().apply { moveTo(cx - tan * 0.3f, tb - ta * 0.76f); cubicTo(cx - tan * 0.25f, tb - ta * 1.35f, cx + tan * 0.25f, tb - ta * 1.35f, cx + tan * 0.3f, tb - ta * 0.76f) }
        drawPath(asa, lt.c(P.HIERRO_LUZ), style = Stroke(1.4f * u))
        drawRoundRect(lt.c(P.ROBLE), Offset(cx - tan * 0.14f, tb - ta * 1.24f), Size(tan * 0.28f, ta * 0.08f), CornerRadius(ta * 0.04f))
        if (lt.k > 0.08f) drawPath(bordeRuinas(panza, -hc * tan * 0.05f, 0f), lt.filo(0.7f))
        // El vapor que sube del pico: una hebra que se tuerce.
        if (arde > 0f) drawPath(abiertaRuinas(listOf(Offset(cx + tan * 0.78f, tb - ta * 0.85f), Offset(cx + tan * 0.86f, tb - ta * 1.1f), Offset(cx + tan * 0.78f, tb - ta * 1.35f), Offset(cx + tan * 0.88f, tb - ta * 1.6f))),
            Color(0xFFF4EFE2).copy(alpha = 0.14f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el cubo de las astillas

    /** El cubo de cinc de las astillas, detrás del cesto: abollado, con su asa y las astillas que asoman. */
    private fun DrawScope.cubo(g: MarcoRuinas) {
        val w = g.w; val h = g.h; val u = density
        val cx = w * 0.875f
        val pie = h - w * 0.055f
        val an = w * 0.12f; val al = w * 0.11f
        val l = g.luz(cx, pie - al * 0.5f, 0.04f)
        val hc = hacia(g, cx)
        contactoRuinas(cx, pie, an * 1.2f, w * 0.025f, l, hc, 0.45f)
        // Las astillas que asoman, unas más claras (recién cortadas).
        for (k in 0 until 7) {
            val x = cx - an * 0.36f + an * 0.12f * k
            val tope = pie - al - w * (0.03f + 0.04f * azar(2701, k))
            drawLine(l.c(if (k % 3 == 0) Color(0xFFD9B884) else Color(0xFF8E6A46)), Offset(x, pie - al * 0.7f), Offset(x + (azar(2702, k) - 0.5f) * an * 0.2f, tope), w * 0.008f, StrokeCap.Butt)
        }
        val forma = poligonoRuinas(Offset(cx - an * 0.4f, pie), Offset(cx + an * 0.4f, pie), Offset(cx + an * 0.5f, pie - al), Offset(cx - an * 0.5f, pie - al))
        modeladoRapidoRuinas(forma, l.c(Color(0xFF9AA09A)), l, hc, 1.1f, 0.4f)
        // Los aros del cinc, la abolladura y el óxido blanco.
        for (f in listOf(0.18f, 0.82f)) drawLine(l.s(0.35f), Offset(cx - an * (0.5f - 0.1f * f), pie - al * (1f - f)), Offset(cx + an * (0.5f - 0.1f * f), pie - al * (1f - f)), 1.4f * u)
        drawOval(l.s(0.22f), Offset(cx + an * 0.05f, pie - al * 0.62f), Size(an * 0.22f, al * 0.26f))
        drawOval(l.c(P.YESO_BLANCO).copy(alpha = 0.3f), Offset(cx - an * 0.3f, pie - al * 0.35f), Size(an * 0.2f, al * 0.14f))
        val boca = Rect(cx - an * 0.52f, pie - al - al * 0.07f, cx + an * 0.52f, pie - al + al * 0.07f)
        drawOval(l.c(Color(0xFFB8BEB8)), boca.topLeft, boca.size, style = Stroke(1.6f * u))
        val asa = Path().apply { moveTo(boca.left + an * 0.02f, boca.center.y); cubicTo(boca.left, pie - al * 1.7f, boca.right, pie - al * 1.7f, boca.right - an * 0.02f, boca.center.y) }
        drawPath(asa, l.c(P.HIERRO_LUZ), style = Stroke(1.2f * u))
        if (l.k > 0.08f) drawPath(bordeRuinas(forma, -hc * 1.4f * u, 0f), l.filo(0.6f))
    }

    // ---------------------------------------------------------------- el cesto de piñas

    private fun DrawScope.cesto(g: MarcoRuinas) {
        val w = g.w; val h = g.h; val u = density
        val cx = w * 0.74f
        val pie = h - w * 0.022f
        val an = w * 0.2f; val al = w * 0.075f
        val l = g.luz(cx, pie - al, 0.05f)
        val hc = hacia(g, cx)
        contactoRuinas(cx, pie, an * 1.1f, w * 0.03f, l, hc, 0.5f)
        // Las piñas que colman el cesto (detrás de su borde).
        for (k in 0 until 6) {
            val c = Offset(cx + an * (-0.36f + 0.145f * k), pie - al - w * (0.006f + 0.012f * (k % 2)))
            val rp = w * (0.024f + 0.004f * (k % 3))
            drawOval(l.c(Color(0xFF6A4628)), Offset(c.x - rp, c.y - rp * 1.2f), Size(rp * 2, rp * 2.2f))
            for (f in 0 until 4) drawArc(l.c(Color(0xFFA0784A)), 0f, 180f, true, Offset(c.x - rp * 0.7f + rp * 0.35f * (f % 2), c.y - rp + rp * 0.5f * f), Size(rp * 0.7f, rp * 0.4f))
            drawOval(l.s(0.25f), Offset(c.x, c.y - rp * 1.2f), Size(rp, rp * 2.2f))
        }
        // El cesto de mimbre: la trama de varas y el borde trenzado.
        val forma = poligonoRuinas(Offset(cx - an * 0.5f, pie - al), Offset(cx + an * 0.5f, pie - al), Offset(cx + an * 0.44f, pie), Offset(cx - an * 0.44f, pie))
        modeladoRapidoRuinas(forma, l.c(Color(0xFFB8925A)), l, hc, 1f, 0.3f)
        clipPath(forma) {
            for (f in 0 until 5) {
                val y = pie - al + al * (f + 0.5f) / 5f
                drawLine(l.s(0.3f), Offset(cx - an * 0.5f, y + al * 0.1f), Offset(cx + an * 0.5f, y + al * 0.1f), 1f * u)
                var x = cx - an * 0.5f + (if (f % 2 == 0) 0f else an * 0.05f)
                while (x < cx + an * 0.5f) { drawRoundRect(l.c(Color(0xFFD0AC70)), Offset(x, y - al * 0.07f), Size(an * 0.07f, al * 0.14f), CornerRadius(al * 0.07f)); x += an * 0.1f }
            }
        }
        val borde = Rect(cx - an * 0.52f, pie - al - al * 0.1f, cx + an * 0.52f, pie - al + al * 0.12f)
        drawRoundRect(l.c(Color(0xFF8E6A3A)), borde.topLeft, borde.size, CornerRadius(borde.height / 2))
        var x = borde.left
        while (x < borde.right) { drawLine(l.c(Color(0xFFC9A26A)), Offset(x, borde.top + 1f * u), Offset(x + borde.height * 0.6f, borde.bottom - 1f * u), 1.2f * u); x += borde.height * 0.5f }
        if (l.k > 0.08f) drawLine(l.filo(0.6f), Offset(borde.left, borde.top + 1f), Offset(borde.right, borde.top + 1f), 1.2f * u)
    }

    // ---------------------------------------------------------------- el diente de león y el helecho

    /** El diente de león que sale entre dos losetas: sus hojas dentadas, el tallo y la bola de vilanos; uno ya se va volando. */
    private fun DrawScope.dienteDeLeon(g: MarcoRuinas) {
        val w = g.w; val h = g.h; val u = density
        val b = Offset(w * 0.535f, h - w * 0.05f)
        val l = g.luz(b.x, b.y - w * 0.08f, 0.04f)
        val hc = hacia(g, b.x)
        for (k in 0 until 4) {
            val ang = -70f + 45f * k
            val tam = w * (0.05f + 0.01f * (k % 2))
            val p = (0 until 6).map { j -> giradoRuinas(b, (if (j % 2 == 0) 0.06f else 0.2f) * tam * (if (j < 3) 1f else -1f), -tam * (j % 3) / 2.4f - tam * 0.1f, ang) }
            val hoja = cerradaRuinas(listOf(b, giradoRuinas(b, tam * 0.16f, -tam * 0.3f, ang), giradoRuinas(b, tam * 0.08f, -tam * 0.5f, ang), giradoRuinas(b, tam * 0.18f, -tam * 0.7f, ang), giradoRuinas(b, 0f, -tam, ang),
                giradoRuinas(b, -tam * 0.18f, -tam * 0.7f, ang), giradoRuinas(b, -tam * 0.08f, -tam * 0.5f, ang), giradoRuinas(b, -tam * 0.16f, -tam * 0.3f, ang)))
            if (p.isNotEmpty()) drawPath(hoja, l.c(if (k % 2 == 0) P.HIEDRA_LUZ else P.HELECHO))
        }
        val tope = Offset(b.x + w * 0.012f, b.y - w * 0.15f)
        drawPath(abiertaRuinas(listOf(b, Offset(b.x - w * 0.006f, b.y - w * 0.08f), tope)), l.c(mezcla(P.HELECHO, P.KRAFT, 0.2f)), style = Stroke(1.4f * u))
        val rr = w * 0.032f
        val bola = tope + Offset(0f, -rr * 0.6f)
        drawCircle(l.c(Color(0xFFF4EFE2)).copy(alpha = 0.35f), rr, bola)
        for (k in 0 until 22) {
            val ang = 360f * k / 22f
            val q = giradoRuinas(bola, 0f, -rr, ang)
            drawLine(l.c(Color(0xFFF4EFE2)).copy(alpha = 0.75f), bola, q, 0.5f * u)
            drawCircle(l.c(Color(0xFFFBF8F0)), 1f * u, q)
        }
        drawCircle(l.c(Color(0xFF8A7A5A)), rr * 0.2f, bola)
        if (l.k > 0.08f) drawCircle(l.filo(0.5f), rr, bola, style = Stroke(1f * u))
        vilanoRuinas(bola + Offset(-hc * w * 0.06f, -w * 0.05f), w * 0.02f, -25f, l.c(Color(0xFFF7F2E6)), 0.85f)
    }

    /** El helecho del rincón derecho, cortado por el borde. */
    private fun DrawScope.helecho(g: MarcoRuinas) {
        val w = g.w; val h = g.h
        val b = Offset(w * 0.99f, h - w * 0.01f)
        val l = g.luz(b.x, b.y - w * 0.08f, 0.04f)
        val hc = hacia(g, b.x)
        listOf(-60f to 0.2f, -35f to 0.25f, -12f to 0.22f, 10f to 0.16f).forEachIndexed { i, (ang, lar) ->
            frondaRuinas(b, w * lar, ang, -0.3f, if (i % 2 == 0) P.HELECHO else mezcla(P.HELECHO, P.HIEDRA, 0.45f), l, hc, 2601 + i, 10)
        }
    }
}
