package io.github.ruddy2003.vestigium.ui.biblioteca

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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El bodegón de lo alto de la librería (se pinta una vez, en la corona guardada), de izquierda a derecha: la maceta de barro de
 * donde cae la hiedra por el costado, con el farol de tarro delante; la máquina de escribir sobre sus dos libros (en
 * `MuebleEstanteRuinasMaquina.kt`); la damajuana de vidrio verde con un helecho creciendo dentro, detrás; el cuenco de esmalte
 * donde cae la gotera; y la jarra de gres con los cardos secos que sueltan sus vilanos, cortada por el borde.
 */
internal object CoronaEstanteRuinas {
    /** Lo que va detrás de la cornisa: las sombras largas de lo alto en la pared. */
    fun DrawScope.detras(g: MarcoRuinas) {
        val w = g.w
        // De noche, junto a la vela del tarro, el aire mismo brilla un poco (el yeso ya lo pintó su charco).
        val t = g.tarro
        if (g.a.tarro > 0f) mancha(t, w * 0.13f, P.LLAMA.copy(alpha = 0.32f * g.a.tarro))
    }

    fun DrawScope.delante(g: MarcoRuinas) {
        damajuana(g)
        maceta(g)
        with(MaquinaRuinas) { libros(g); maquina(g) }
        jarra(g)
        cuenco(g)
        tarro(g)
        hiedraQueCae(g)
        vilanos(g)
    }

    /** El sitio de la luz de algo de lo alto (con la mancha del lucernario que le toque, en lo que se pinta una vez). */
    private fun luzEn(g: MarcoRuinas, x: Float, y: Float, delante: Float): LuzRuinas =
        g.luz(x, y, delante, ParedEstanteRuinas.solEn(g, Offset(x, y), delante))

    private fun hacia(g: MarcoRuinas, x: Float): Float = if (g.a.oscuro && !g.a.noche) (if (x > g.tarro.x) -1f else 1f) else -1f

    // ---------------------------------------------------------------- la damajuana

    /** La damajuana de vidrio verde: panzuda, con su cuello y el gollete; dentro, un helecho y musgo en el fondo. */
    private fun DrawScope.damajuana(g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * 0.7f
        val rx = w * 0.092f; val ry = w * 0.088f
        val cy = pie - ry * 0.98f
        val cuello = w * 0.022f
        val boca = pie - w * 0.215f
        val forma = cerradaRuinas(listOf(
            Offset(cx - cuello, boca + w * 0.012f), Offset(cx - cuello * 1.1f, cy - ry * 0.95f), Offset(cx - rx * 0.7f, cy - ry * 0.72f), Offset(cx - rx, cy),
            Offset(cx - rx * 0.92f, cy + ry * 0.7f), Offset(cx - rx * 0.5f, pie - 0.5f * u), Offset(cx + rx * 0.5f, pie - 0.5f * u), Offset(cx + rx * 0.92f, cy + ry * 0.7f),
            Offset(cx + rx, cy), Offset(cx + rx * 0.7f, cy - ry * 0.72f), Offset(cx + cuello * 1.1f, cy - ry * 0.95f), Offset(cx + cuello, boca + w * 0.012f)))
        val zona = forma.getBounds()
        conManchaRuinas(g, 0.05f, zona) { s ->
            val l = g.luz(cx, cy, 0.05f, s)
            val h = hacia(g, cx)
            // La sombra en la pared y la de contacto: verdes (la luz pasa por el vidrio).
            drawPath(corridoRuinas(forma, -h * w * 0.03f, w * 0.012f), l.s(0.22f))
            if (!g.a.oscuro) drawPath(corridoRuinas(forma, -h * w * 0.03f, w * 0.012f), P.VIDRIO_VERDE.copy(alpha = 0.18f))
            contactoRuinas(cx, pie, rx * 2f, w * 0.014f, l, h)
            // El vidrio: lo de detrás se ve verde a través; la panza más honda en sus bordes.
            val vidrio = l.c(P.VIDRIO_VERDE)
            drawPath(forma, Brush.radialGradient(listOf(mezcla(vidrio, P.VIDRIO_CLARO, 0.35f).copy(alpha = 0.82f), vidrio.copy(alpha = 0.9f), mezcla(vidrio, g.a.hondo, 0.5f)),
                Offset(cx - h * rx * 0.25f, cy - ry * 0.1f), rx * 1.1f))
            clipPath(forma) {
                // Dentro: la tierra y el musgo del fondo, el helecho que crece.
                drawRect(l.c(mezcla(P.TIERRA, P.VIDRIO_VERDE, 0.35f)), Offset(cx - rx, pie - ry * 0.32f), Size(rx * 2, ry * 0.4f))
                for (k in 0 until 7) drawCircle(l.c(mezcla(P.MUSGO, P.VIDRIO_VERDE, 0.3f)), rx * (0.12f + 0.06f * azar(1501, k)), Offset(cx - rx * 0.8f + rx * 0.27f * k, pie - ry * 0.3f))
                listOf(-34f to 0.95f, -10f to 1.15f, 14f to 1.05f, 38f to 0.85f, -55f to 0.6f, 58f to 0.6f).forEachIndexed { i, (ang, lar) ->
                    frondaRuinas(Offset(cx + rx * 0.05f * (i - 2), pie - ry * 0.25f), ry * lar, ang, 0.3f * (if (ang < 0f) -1f else 1f),
                        mezcla(P.HELECHO, P.VIDRIO_VERDE, 0.35f + 0.1f * (i % 2)), l, h, 1510 + i, 9)
                }
                // El vaho de dentro (gotitas) y el verde del vidrio por encima de lo de dentro.
                drawRect(vidrio.copy(alpha = 0.28f), zona.topLeft, zona.size)
                for (k in 0 until 16) drawCircle(l.c(P.VIDRIO_CLARO).copy(alpha = 0.35f), (0.6f + 0.8f * azar(1520, k)) * u,
                    Offset(cx + rx * (azar(1521, k) - 0.5f) * 1.6f, cy - ry * 0.6f + ry * 0.7f * azar(1522, k)))
            }
            // Los reflejos: una ventana alargada del lado de la luz y un filo en el borde contrario.
            val refl = cerradaRuinas(listOf(Offset(cx + h * rx * 0.62f, cy - ry * 0.5f), Offset(cx + h * rx * 0.45f, cy - ry * 0.62f), Offset(cx + h * rx * 0.78f, cy + ry * 0.1f), Offset(cx + h * rx * 0.66f, cy + ry * 0.3f)))
            drawPath(refl, l.b(0.75f))
            drawPath(bordeRuinas(forma, h * w * 0.006f, 0f), l.b(0.3f))
            drawPath(forma, mezcla(vidrio, g.a.hondo, 0.4f), style = Stroke(1.2f * u))
            // El gollete con su labio y un tallo que asoma por la boca.
            val labio = Rect(cx - cuello * 1.25f, boca, cx + cuello * 1.25f, boca + w * 0.014f)
            drawRoundRect(l.c(mezcla(P.VIDRIO_VERDE, P.VIDRIO_CLARO, 0.3f)), labio.topLeft, labio.size, androidx.compose.ui.geometry.CornerRadius(labio.height * 0.4f))
            drawLine(l.b(0.6f), Offset(labio.left + 1f * u, labio.top + 1f * u), Offset(labio.right - 1f * u, labio.top + 1f * u), 1f * u)
            frondaRuinas(Offset(cx, boca + w * 0.01f), w * 0.07f, 28f, 0.4f, P.HELECHO, l, h, 1530, 7)
            if (l.k > 0.05f) drawPath(bordeRuinas(forma, -h * w * 0.008f, 0f), l.filo(0.6f))
        }
    }

    // ---------------------------------------------------------------- la maceta y la hiedra

    /** La maceta de barro, desportillada y con salitre, con la mata de hiedra encima. */
    private fun DrawScope.maceta(g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val x0 = w * 0.015f; val x1 = w * 0.135f
        val top = pie - w * 0.078f
        val forma = poligonoRuinas(Offset(x0 + w * 0.012f, pie), Offset(x1 - w * 0.012f, pie), Offset(x1, top + w * 0.016f), Offset(x0, top + w * 0.016f))
        val labio = Rect(x0 - w * 0.006f, top, x1 + w * 0.006f, top + w * 0.018f)
        conManchaRuinas(g, 0.04f, Rect(x0, top, x1, pie)) { s ->
            val l = g.luz((x0 + x1) / 2, pie - w * 0.04f, 0.04f, s)
            val h = hacia(g, x0)
            drawPath(corridoRuinas(forma, -h * w * 0.022f, w * 0.008f), l.s(0.3f))
            contactoRuinas((x0 + x1) / 2, pie, x1 - x0, w * 0.012f, l, h)
            modeladoRuinas(forma, l.c(P.BARRO), l, h, 1f, 0.28f)
            // El salitre blanco y el verdín de abajo.
            drawRect(l.c(P.YESO_BLANCO).copy(alpha = 0.3f), Offset(x0 + w * 0.02f, pie - w * 0.025f), Size(x1 - x0 - w * 0.04f, w * 0.012f))
            mancha(Offset(x0 + w * 0.03f, pie - w * 0.01f), w * 0.02f, l.c(P.MUSGO).copy(alpha = 0.5f))
            drawRect(Brush.verticalGradient(listOf(l.c(mezcla(P.BARRO, P.OCRE, 0.3f)), l.c(P.BARRO_OSC)), labio.top, labio.bottom), labio.topLeft, labio.size)
            drawLine(l.b(0.5f), Offset(labio.left, labio.top + 0.8f * u), Offset(labio.right, labio.top + 0.8f * u), 1f * u)
            // El desportillado del labio.
            drawPath(poligonoRuinas(Offset(x1 - w * 0.03f, labio.top - 0.5f), Offset(x1 - w * 0.012f, labio.top - 0.5f), Offset(x1 - w * 0.02f, labio.top + w * 0.01f)), l.c(P.OCRE))
        }
        // La mata de hiedra encima de la maceta: un montón de hojas.
        val h = hacia(g, x0)
        for (k in 0 until 14) {
            val c = Offset(w * (0.02f + 0.11f * azar(1601, k)), top - w * (0.002f + 0.05f * azar(1602, k)))
            val ang = -70f + 140f * azar(1603, k)
            hojaRuinas(c, w * (0.026f + 0.01f * azar(1604, k)), ang, if (k % 3 == 0) P.HIEDRA_LUZ else P.HIEDRA, luzEn(g, c.x, c.y, 0.05f), h)
        }
    }

    /** Las guías de hiedra que caen de la maceta por delante de la cornisa. */
    private fun DrawScope.hiedraQueCae(g: MarcoRuinas) {
        val w = g.w
        val h = hacia(g, 0f)
        val guias = listOf(
            listOf(Offset(0.04f, 0.4f), Offset(0.025f, 0.45f), Offset(0.03f, 0.5f), Offset(0.02f, 0.55f), Offset(0.035f, 0.6f)),
            listOf(Offset(0.1f, 0.405f), Offset(0.12f, 0.46f), Offset(0.105f, 0.52f), Offset(0.115f, 0.57f)),
            listOf(Offset(0.07f, 0.41f), Offset(0.07f, 0.47f), Offset(0.075f, 0.53f)),
        )
        guias.forEachIndexed { i, pts -> talloRuinas(pts.map { it * w }, w * 0.03f, 1610 + i, { p -> luzEn(g, p.x, p.y, 0.06f) }, h, 1, 1.3f) }
    }

    // ---------------------------------------------------------------- el farol de tarro

    /**
     * El farol de tarro: un tarro de vidrio con su aro de rosca, un asa de alambre retorcido y, dentro, una vela gastada sobre su
     * charco de cera. De noche la llama arde (el halo en la pared lo pone [detras]) y el vidrio se enciende.
     */
    private fun DrawScope.tarro(g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * 0.205f
        val an = w * 0.084f; val al = w * 0.104f
        val r = Rect(cx - an / 2, pie - al, cx + an / 2, pie)
        val arde = g.a.tarro
        val l = luzEn(g, cx, pie - al * 0.5f, 0.07f)
        val h = hacia(g, cx)
        val forma = cerradaRuinas(listOf(Offset(r.left + an * 0.08f, r.top + al * 0.1f), Offset(r.right - an * 0.08f, r.top + al * 0.1f), Offset(r.right, r.top + al * 0.3f),
            Offset(r.right, r.bottom - al * 0.08f), Offset(r.right - an * 0.12f, r.bottom), Offset(r.left + an * 0.12f, r.bottom), Offset(r.left, r.bottom - al * 0.08f), Offset(r.left, r.top + al * 0.3f)))
        if (arde <= 0f) drawPath(corridoRuinas(forma, -h * w * 0.02f, w * 0.008f), l.s(0.25f))
        contactoRuinas(cx, pie, an * 1.1f, w * 0.012f, l, h)
        // Dentro: la vela gastada y su cera derramada; la llama si arde.
        val vela = Rect(cx - an * 0.18f, r.bottom - al * 0.46f, cx + an * 0.18f, r.bottom - al * 0.06f)
        drawRect(if (arde > 0f) mezcla(l.c(Color(0xFFEDE2C4)), Color(0xFFFFE6B0), 0.8f * arde) else l.c(Color(0xFFEDE2C4)), vela.topLeft, vela.size)
        drawRect(l.s(0.25f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        drawOval(l.c(Color(0xFFE6D9B6)), Offset(r.left + an * 0.15f, r.bottom - al * 0.1f), Size(an * 0.7f, al * 0.08f))
        drawLine(l.c(P.HIERRO), Offset(cx, vela.top), Offset(cx, vela.top - 2.4f * u), 0.9f * u)
        val llama = Offset(cx, vela.top - al * 0.11f)
        if (arde > 0f) {
            mancha(llama, an * 1.5f, P.LLAMA.copy(alpha = 0.85f * arde))
            mancha(llama, an * 0.5f, Color(0xFFFFF0C0).copy(alpha = 0.7f * arde))
            val f = cerradaRuinas(listOf(Offset(cx, llama.y - al * 0.16f), Offset(cx + an * 0.1f, llama.y + al * 0.02f), Offset(cx, llama.y + al * 0.08f), Offset(cx - an * 0.1f, llama.y + al * 0.02f)))
            drawPath(f, P.FUEGO.copy(alpha = arde))
            drawPath(cerradaRuinas(listOf(Offset(cx, llama.y - al * 0.1f), Offset(cx + an * 0.05f, llama.y + al * 0.02f), Offset(cx, llama.y + al * 0.06f), Offset(cx - an * 0.05f, llama.y + al * 0.02f))), Color(0xFFFFF4C8).copy(alpha = arde))
        }
        // El vidrio: transparente, con su reflejo, y encendido por dentro si arde.
        val vid = if (arde > 0f) P.LLAMA.copy(alpha = 0.4f * arde) else l.c(P.VIDRIO_CLARO).copy(alpha = 0.3f)
        drawPath(forma, vid)
        drawPath(bordeRuinas(forma, -h * an * 0.1f, 0f), (if (arde > 0f) P.LLAMA.copy(alpha = 0.5f * arde) else l.c(P.VIDRIO_CLARO).copy(alpha = 0.5f)))
        drawRect(l.b(0.8f), Offset(r.left + an * (if (h < 0f) 0.14f else 0.76f), r.top + al * 0.32f), Size(an * 0.1f, al * 0.42f))
        drawPath(forma, l.c(mezcla(P.VIDRIO_CLARO, P.VIDRIO_VERDE, 0.5f)).copy(alpha = 0.7f), style = Stroke(1f * u))
        // El aro de rosca de hojalata y el asa de alambre retorcido.
        val aro = Rect(r.left + an * 0.06f, r.top, r.right - an * 0.06f, r.top + al * 0.13f)
        drawRect(Brush.horizontalGradient(listOf(l.c(P.NIQUEL), l.c(mezcla(P.NIQUEL, P.BARRO, 0.3f)), l.s(0.5f)), aro.left, aro.right), aro.topLeft, aro.size)
        for (k in 1..2) drawLine(l.s(0.3f), Offset(aro.left, aro.top + aro.height * k / 3f), Offset(aro.right, aro.top + aro.height * k / 3f), 0.6f * u)
        val asa = Path().apply { moveTo(aro.left, aro.center.y); cubicTo(aro.left - an * 0.1f, r.top - al * 0.55f, aro.right + an * 0.1f, r.top - al * 0.55f, aro.right, aro.center.y) }
        drawPath(corridoRuinas(asa, -h * 1.5f * u, 1.5f * u), l.s(0.3f), style = Stroke(1.1f * u))
        drawPath(asa, l.c(P.HIERRO_LUZ), style = Stroke(1.2f * u))
        if (l.k > 0.1f) drawPath(asa, l.filo(0.6f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- el cuenco de la gotera

    /** El cuenco de esmalte azul con su borde blanco y el hierro donde saltó, con el agua dentro y la gota que cae. */
    private fun DrawScope.cuenco(g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * ParedEstanteRuinas.GOTERA_X
        val an = w * 0.1f; val al = w * 0.038f
        val r = Rect(cx - an / 2, pie - al, cx + an / 2, pie)
        val l = luzEn(g, cx, pie - al, 0.05f)
        val h = hacia(g, cx)
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            cubicTo(r.right - an * 0.04f, r.top + al * 0.7f, r.right - an * 0.2f, r.bottom, cx + an * 0.2f, r.bottom)
            lineTo(cx - an * 0.2f, r.bottom)
            cubicTo(r.left + an * 0.2f, r.bottom, r.left + an * 0.04f, r.top + al * 0.7f, r.left, r.top); close()
        }
        drawPath(corridoRuinas(forma, -h * w * 0.018f, w * 0.006f), l.s(0.3f))
        contactoRuinas(cx, pie, an * 0.6f, w * 0.01f, l, h)
        modeladoRuinas(forma, l.c(P.ESMALTE), l, h, 1f, 0.4f)
        // Una saltadura del esmalte junto al borde, que deja ver el hierro (una sola: dos iguales se leerían como ojos).
        drawPath(bultoRuinas(Offset(r.left + an * 0.66f, r.top + al * 0.22f), an * 0.06f, al * 0.14f, 1701, 7, 0.3f), l.c(P.HIERRO))
        // La boca: el borde blanco y el agua con sus ondas.
        val boca = Rect(r.left, r.top - al * 0.22f, r.right, r.top + al * 0.22f)
        drawOval(l.c(P.ESMALTE_BLANCO), boca.topLeft, boca.size)
        val agua = boca.deflate(an * 0.06f)
        drawOval(l.c(mezcla(P.ESMALTE, P.HIERRO, 0.45f)), agua.topLeft, agua.size)
        for (k in 1..2) drawOval(l.b(0.45f), Offset(agua.center.x - agua.width * 0.17f * k, agua.center.y - agua.height * 0.17f * k), Size(agua.width * 0.34f * k, agua.height * 0.34f * k), style = Stroke(0.7f * u))
        // La gota que cae y otra a medio camino, brillando con la luz que les toca.
        for ((k, yy) in listOf(0.3f, 0.39f).withIndex()) {
            val p = Offset(cx, w * yy)
            val lg = luzEn(g, p.x, p.y, 0.05f)
            val gota = cerradaRuinas(listOf(Offset(p.x, p.y - w * 0.008f), Offset(p.x + w * 0.0035f, p.y + w * 0.002f), Offset(p.x, p.y + w * 0.005f), Offset(p.x - w * 0.0035f, p.y + w * 0.002f)))
            drawPath(gota, lg.c(P.VIDRIO_CLARO).copy(alpha = 0.7f))
            drawCircle(if (g.a.oscuro) P.LLAMA.copy(alpha = 0.8f) else lg.b(0.9f), w * 0.0016f, Offset(p.x - w * 0.001f, p.y))
            if (k == 0) drawLine(lg.c(P.VIDRIO_CLARO).copy(alpha = 0.3f), Offset(p.x, p.y - w * 0.04f), Offset(p.x, p.y - w * 0.012f), 0.7f * u)
        }
    }

    // ---------------------------------------------------------------- la jarra con los cardos

    /** La jarra de gres (crema, con el vidriado pardo de arriba) con tres cardos secos que sueltan sus vilanos. */
    private fun DrawScope.jarra(g: MarcoRuinas) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * 0.965f
        val an = w * 0.075f; val al = w * 0.1f
        val r = Rect(cx - an / 2, pie - al, cx + an / 2, pie)
        val l = luzEn(g, cx, pie - al * 0.5f, 0.04f)
        val h = hacia(g, cx)
        // Los cardos: los tallos secos y las cabezas con sus brácteas, algunas abiertas con el vilano blanco asomando.
        val cabezas = listOf(Offset(cx - w * 0.04f, pie - w * 0.2f), Offset(cx + w * 0.005f, pie - w * 0.235f), Offset(cx - w * 0.075f, pie - w * 0.16f))
        cabezas.forEachIndexed { i, c ->
            val lc = luzEn(g, c.x, c.y, 0.05f)
            drawLine(lc.c(mezcla(P.KRAFT, P.HELECHO, 0.3f)), Offset(cx - w * 0.01f + i * w * 0.006f, r.top + al * 0.1f), c, 1.3f * u)
            // Dos hojas espinosas secas en el tallo.
            val m = Offset((c.x + cx) / 2, (c.y + r.top) / 2)
            drawPath(poligonoRuinas(m, m + Offset(-w * 0.025f, -w * 0.006f), m + Offset(-w * 0.018f, w * 0.002f), m + Offset(-w * 0.03f, w * 0.006f), m + Offset(-w * 0.008f, w * 0.006f)), lc.c(mezcla(P.KRAFT, P.HELECHO, 0.4f)))
            val cab = bultoRuinas(c, w * 0.016f, w * 0.019f, 1801 + i, 9, 0.12f)
            drawPath(corridoRuinas(cab, -h * w * 0.01f, w * 0.006f), lc.s(0.3f))
            drawPath(cab, lc.c(Color(0xFF8A7656)))
            for (k in 0 until 6) {
                val a = -150f + 24f * k
                val p0 = giradoRuinas(c, 0f, -w * 0.016f, a + 90f)
                drawLine(lc.c(Color(0xFF6A5638)), c, p0 + (p0 - c) * 0.3f, 0.8f * u)
            }
            drawPath(bordeRuinas(cab, -h * w * 0.005f, w * 0.005f), lc.b(0.4f))
            // El vilano que asoma arriba: una borla blanca y suelta.
            drawCircle(lc.c(Color(0xFFF4EFE2)).copy(alpha = 0.85f), w * 0.012f, c + Offset(0f, -w * 0.017f))
            for (k in 0 until 7) {
                val a = -160f + 23f * k
                drawLine(lc.c(Color(0xFFF4EFE2)).copy(alpha = 0.7f), c + Offset(0f, -w * 0.015f), giradoRuinas(c + Offset(0f, -w * 0.015f), 0f, -w * 0.02f, a + 90f), 0.5f * u)
            }
        }
        // La jarra: panzuda, con el asa del lado de fuera y el vidriado pardo que chorrea.
        val forma = cerradaRuinas(listOf(Offset(r.left + an * 0.18f, r.top), Offset(r.right - an * 0.18f, r.top), Offset(r.right - an * 0.1f, r.top + al * 0.2f),
            Offset(r.right, r.top + al * 0.6f), Offset(r.right - an * 0.12f, r.bottom), Offset(r.left + an * 0.12f, r.bottom), Offset(r.left, r.top + al * 0.6f), Offset(r.left + an * 0.1f, r.top + al * 0.2f)))
        drawPath(corridoRuinas(forma, -h * w * 0.02f, w * 0.008f), l.s(0.3f))
        contactoRuinas(cx, pie, an, w * 0.012f, l, h)
        modeladoRuinas(forma, l.c(Color(0xFFDCCFAE)), l, h, 1f, 0.3f)
        clipPath(forma) {
            val vidriado = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top); lineTo(r.right, r.top + al * 0.3f)
                var x = r.right; var k = 0
                while (x > r.left) { val x2 = x - an * 0.12f; quadraticTo((x + x2) / 2, r.top + al * (0.36f + 0.12f * azar(1810, k)), x2, r.top + al * 0.3f); x = x2; k++ }
                close()
            }
            drawPath(vidriado, l.c(Color(0xFF6A3A1E)))
            drawRect(l.b(0.3f), Offset(r.left + an * 0.15f, r.top), Size(an * 0.12f, al))
        }
        val asa = Path().apply { moveTo(r.left + an * 0.08f, r.top + al * 0.2f); cubicTo(r.left - an * 0.35f, r.top + al * 0.2f, r.left - an * 0.35f, r.top + al * 0.65f, r.left + an * 0.02f, r.top + al * 0.7f) }
        drawPath(asa, l.c(Color(0xFF6A3A1E)), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
        drawPath(asa, l.b(0.35f), style = Stroke(w * 0.004f, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- los vilanos

    /** Los vilanos que sueltan los cardos y los dientes de león, flotando en el cuarto (en el rayo del lucernario, encendidos). */
    private fun DrawScope.vilanos(g: MarcoRuinas) {
        val w = g.w
        for (i in 0 until 16) {
            val p = Offset(w * (0.06f + 0.9f * azar(1901, i)), w * (0.03f + 0.4f * azar(1902, i)))
            val sol = ParedEstanteRuinas.solEn(g, p, 0.1f) ?: 0f
            val l = g.luz(p.x, p.y, 0.1f, sol)
            val col = if (g.a.oscuro) mezcla(l.c(Color(0xFFF4EFE2)), P.LLAMA, 0.3f * l.k) else l.c(Color(0xFFF7F2E6))
            vilanoRuinas(p, w * (0.012f + 0.008f * azar(1903, i)), -40f + 80f * azar(1904, i), col, (0.45f + 0.45f * maxOf(sol, l.k)).coerceAtMost(0.95f))
        }
    }
}
