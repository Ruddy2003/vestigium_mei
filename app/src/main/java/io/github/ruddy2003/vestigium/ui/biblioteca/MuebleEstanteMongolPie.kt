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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El pie del estante de Mongol: el cesto de mimbre con las tortas de boñiga seca y su horquilla (cortado por la izquierda) y el
 * hogar (cortado por abajo a la derecha): el aro de hierro con sus patas, la olla tiznada y el fuego (de día brasa con su hilo de
 * humo; de noche, llamas, la luz del cuarto). Lo de los huecos de las repisas está en [HuecosEstanteMongol].
 */
internal object PieEstanteMongol {

    // ---------------------------------------------------------------- al pie

    fun DrawScope.alPie(g: MarcoMongol) {
        cesto(g)
        hogar(g)
    }

    /**
     * El cesto de mimbre de la boñiga, a la izquierda y cortado por el borde: el tejido en hileras que se alternan, el aro del
     * borde, las tortas de boñiga seca colmándolo y la horquilla de madera apoyada.
     */
    private fun DrawScope.cesto(g: MarcoMongol) {
        val u = density
        val w = g.w; val h = g.h
        val pie = h - w * 0.02f
        val x0 = -w * 0.04f; val x1 = w * 0.2f
        val top = pie - w * 0.2f
        val l = g.luz(Offset(w * 0.08f, pie - w * 0.1f))
        val cuerpo = poligonoMongol(x0, top, x1, top, x1 - w * 0.02f, pie, x0 + w * 0.02f, pie)
        // De día, la sombra larga que el cesto y la horquilla echan en el sol de la puerta, hacia la derecha.
        if (!g.a.oscuro) clipPath(poligonoSolMongol(g)) {
            drawPath(poligonoMongol(x1 - w * 0.01f, top - w * 0.03f, x1 + w * 0.2f, top + w * 0.05f, x1 + w * 0.24f, pie, x1 - w * 0.02f, pie), l.s(0.32f))
            drawLine(l.s(0.3f), Offset(w * 0.24f, pie), Offset(w * 0.44f, pie - w * 0.12f), w * 0.012f, StrokeCap.Round)
        }
        drawOval(l.s(0.45f), Offset(x0, pie - w * 0.012f), Size(x1 - x0 + w * 0.06f, w * 0.024f))
        // La horquilla, detrás: dos dientes y su mango.
        val m0 = Offset(w * 0.24f, pie - w * 0.002f); val m1 = Offset(w * 0.12f, top - w * 0.2f)
        drawLine(l.s(0.3f), m0 + Offset(w * 0.012f, 0f), m1 + Offset(w * 0.02f, w * 0.01f), w * 0.01f)
        drawLine(l.c(P.ALERCE), m0, m1, w * 0.01f, StrokeCap.Round)
        drawLine(l.b(0.35f), m0 - Offset(w * 0.003f, 0f), m1 - Offset(w * 0.003f, 0f), w * 0.003f)
        // Las tortas de boñiga, colmando la boca.
        for (i in 0 until 7) {
            val cx = x0 + (x1 - x0) * (0.1f + 0.13f * i)
            val cy = top - w * (0.01f + 0.025f * azar(980, i))
            val rr = w * (0.04f + 0.012f * azar(981, i))
            rotate((azar(982, i) - 0.5f) * 50f, Offset(cx, cy)) {
                drawOval(l.s(0.35f), Offset(cx - rr + w * 0.004f, cy - rr * 0.35f + w * 0.004f), Size(rr * 2, rr * 0.7f))
                drawOval(l.c(mezcla(P.BONIGA, P.FIELTRO_GRIS, 0.3f * azar(983, i))), Offset(cx - rr, cy - rr * 0.35f), Size(rr * 2, rr * 0.7f))
                drawOval(l.b(0.25f), Offset(cx - rr * 0.8f, cy - rr * 0.33f), Size(rr * 1.2f, rr * 0.25f))
                drawLine(l.c(P.HOLLIN).copy(alpha = 0.3f), Offset(cx - rr * 0.5f, cy), Offset(cx + rr * 0.4f, cy + rr * 0.05f), 1f * u)
            }
        }
        drawPath(cuerpo, Brush.horizontalGradient(listOf(l.c(P.MIMBRE), l.c(P.MIMBRE), l.c(mezcla(P.MIMBRE, P.ALERCE_OSC, 0.45f))), x0, x1))
        clipPath(cuerpo) {
            // El tejido de mimbre torcido: hileras de puntadas inclinadas, una hilera hacia un lado y la siguiente hacia el otro
            // (la espiga), cada puntada con su luz arriba y su hueco oscuro, y las varas que asoman cada tanto.
            val filas = 11
            val hh = (pie - top) / filas
            val claro = l.c(mezcla(P.MIMBRE, P.CREMA, 0.3f))
            val hueco = l.s(0.4f)
            for (f in 0 until filas) {
                val y = top + hh * f
                val s = if (f % 2 == 0) 1f else -1f
                val puntadas = Path()
                val huecos = Path()
                var x = x0 - w * 0.02f
                while (x < x1 + w * 0.02f) {
                    puntadas.moveTo(x - s * w * 0.008f, y + hh * 0.85f); puntadas.lineTo(x + s * w * 0.008f, y + hh * 0.15f)
                    huecos.moveTo(x + w * 0.011f - s * w * 0.008f, y + hh * 0.85f); huecos.lineTo(x + w * 0.011f + s * w * 0.008f, y + hh * 0.15f)
                    x += w * 0.022f
                }
                drawPath(huecos, hueco, style = Stroke(w * 0.004f, cap = StrokeCap.Round))
                drawPath(puntadas, claro, style = Stroke(w * 0.0095f, cap = StrokeCap.Round))
                drawPath(corridoMongol(puntadas, -w * 0.002f, -w * 0.002f), l.b(0.3f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, l.s(0.45f)), x0 + (x1 - x0) * 0.5f, x1), Offset(x0, top), Size(x1 - x0, pie - top))
        }
        // El aro del borde.
        drawRect(l.c(mezcla(P.MIMBRE, P.ALERCE_OSC, 0.25f)), Offset(x0, top - w * 0.008f), Size(x1 - x0, w * 0.016f))
        drawRect(l.b(0.4f), Offset(x0, top - w * 0.008f), Size(x1 - x0, 1.4f * u))
    }

    /**
     * El hogar, abajo a la derecha y cortado por el borde: el lecho de ceniza con las tortas de boñiga que arden, el aro de
     * hierro con sus tres patas, la olla tiznada encima con su tapa y su asa, y el fuego (de día, brasa bajo la ceniza y un hilo
     * de humo; de noche, las llamas que lamen el culo de la olla y su charco de luz; en los Sueños, brasas).
     */
    private fun DrawScope.hogar(g: MarcoMongol) {
        val u = density
        val w = g.w; val h = g.h
        val a = g.a
        val c = g.hogar
        val l = g.luz(Offset(c.x, c.y - w * 0.1f))
        val aroY = c.y - w * 0.03f
        val aro = Rect(c.x - w * 0.17f, aroY - w * 0.024f, c.x + w * 0.17f, aroY + w * 0.024f)
        // El charco de luz de la noche, antes que nada.
        if (a.oscuro) {
            // Dos halos (el ancho, de 0.8 anchos, y el de cerca, de 0.38) en una sola pasada: la suma de los dos, punto a punto.
            // Se corta en 0.68 anchos, donde ya casi no alumbra (así pinta menos superficie).
            val a1 = if (a.llamas) 0.2f else 0.1f
            val a2 = if (a.llamas) 0.32f else 0.18f
            val radio = w * 0.68f
            fun suma(d: Float): Float {
                val x1 = a1 * (1f - d / 0.8f)
                val x2 = a2 * (1f - d / 0.38f).coerceAtLeast(0f)
                return 1f - (1f - x1) * (1f - x2)
            }
            val f = a.fuego
            val cc = Offset(c.x, c.y + w * 0.03f)
            drawCircle(Brush.radialGradient(0f to f.copy(alpha = suma(0f)), (0.19f / 0.68f) to f.copy(alpha = suma(0.19f)), (0.38f / 0.68f) to f.copy(alpha = suma(0.38f)),
                (0.58f / 0.68f) to f.copy(alpha = suma(0.58f)), 1f to f.copy(alpha = 0f), center = cc, radius = radio), radio, cc)
        } else if (a.noche) mancha(Offset(c.x, c.y + w * 0.03f), w * 0.32f, a.fuego.copy(alpha = 0.2f))
        // El lecho de ceniza y las tortas de boñiga que arden.
        val lecho = Offset(c.x, c.y + w * 0.065f)
        drawOval(l.c(mezcla(P.FIELTRO_GRIS, P.HOLLIN, 0.35f)), Offset(lecho.x - w * 0.2f, lecho.y - w * 0.03f), Size(w * 0.4f, w * 0.07f))
        drawOval(l.c(mezcla(P.CREMA, P.FIELTRO_GRIS, 0.6f)), Offset(lecho.x - w * 0.16f, lecho.y - w * 0.026f), Size(w * 0.3f, w * 0.03f))
        // Las patas de atrás.
        drawLine(l.c(P.HIERRO), Offset(c.x + w * 0.06f, aroY - w * 0.02f), Offset(c.x + w * 0.08f, lecho.y), w * 0.011f)
        val brasa = if (a.lumbre) P.BRASA else P.FUEGO
        for (i in 0 until 5) {
            val x = lecho.x + w * (-0.12f + 0.06f * i)
            val rr = w * (0.032f + 0.01f * azar(990, i))
            val y = lecho.y - w * 0.012f - w * 0.008f * (i % 2)
            drawOval(l.c(P.BONIGA), Offset(x - rr, y - rr * 0.32f), Size(rr * 2, rr * 0.68f))
            drawOval(brasa.copy(alpha = if (a.oscuro) 0.95f else if (a.noche) 0.75f else 0.55f), Offset(x - rr * 0.75f, y - rr * 0.3f), Size(rr * 1.5f, rr * 0.28f))
            drawOval(Color(0xFFFFD07A).copy(alpha = if (a.oscuro) 0.7f else 0.3f), Offset(x - rr * 0.3f, y - rr * 0.26f), Size(rr * 0.6f, rr * 0.12f))
        }
        if (a.llamas) llamas(Offset(lecho.x, lecho.y - w * 0.02f), w * 0.15f, w)
        else if (a.lumbre) llamas(Offset(lecho.x, lecho.y - w * 0.02f), w * 0.07f, w, brasa = true)
        // La olla de hierro tiznada sobre el aro: panzuda, con su tapa de madera y su asa.
        val ol = Rect(c.x - w * 0.115f, aroY - w * 0.12f, c.x + w * 0.115f, aroY + w * 0.015f)
        val olla = Path().apply {
            moveTo(ol.left + w * 0.012f, ol.top + w * 0.015f)
            cubicTo(ol.left - w * 0.012f, ol.top + ol.height * 0.55f, ol.left + w * 0.04f, ol.bottom, c.x, ol.bottom)
            cubicTo(ol.right - w * 0.04f, ol.bottom, ol.right + w * 0.012f, ol.top + ol.height * 0.55f, ol.right - w * 0.012f, ol.top + w * 0.015f)
            close()
        }
        // El asa, detrás.
        drawPath(Path().apply { moveTo(ol.left + w * 0.015f, ol.top + w * 0.02f); cubicTo(ol.left + w * 0.03f, ol.top - w * 0.09f, ol.right - w * 0.03f, ol.top - w * 0.09f, ol.right - w * 0.015f, ol.top + w * 0.02f) },
            l.c(P.HIERRO), style = Stroke(w * 0.006f))
        drawPath(olla, Brush.radialGradient(listOf(l.c(mezcla(P.HIERRO, P.HIERRO_LUZ, if (a.oscuro) 0.05f else 0.35f)), l.c(P.HIERRO), l.c(P.HOLLIN)),
            Offset(ol.left + ol.width * 0.32f, ol.top + ol.height * 0.3f), ol.width * 0.7f))
        clipPath(olla) {
            pinceladas(ol, l.c(P.HOLLIN), 991, 8, u, 0.35f)
            if (a.lumbre) {
                drawPath(bordeMongol(olla, 0f, -ol.height * 0.3f), a.fuego.copy(alpha = if (a.llamas) 0.6f else 0.35f))
                drawPath(bordeMongol(olla, 0f, -ol.height * 0.1f), Color(0xFFFFD890).copy(alpha = if (a.llamas) 0.55f else 0.25f))
            } else {
                drawRoundRect(luzDuraMongol(0.3f), Offset(ol.left + ol.width * 0.18f, ol.top + ol.height * 0.22f), Size(ol.width * 0.04f, ol.height * 0.42f), CornerRadius(ol.width * 0.02f))
                drawPath(bordeMongol(olla, ol.width * 0.04f, 0f), l.b(0.18f))
            }
        }
        // La tapa de madera con su asidero.
        val tapa = Rect(ol.left + w * 0.006f, ol.top - w * 0.006f, ol.right - w * 0.006f, ol.top + w * 0.026f)
        drawOval(l.c(P.ALERCE_OSC), tapa.topLeft, tapa.size)
        drawOval(l.c(mezcla(P.ALERCE, P.ALERCE_OSC, 0.3f)), Offset(tapa.left + w * 0.01f, tapa.top + w * 0.002f), Size(tapa.width - w * 0.02f, tapa.height * 0.7f))
        drawRoundRect(l.c(P.ALERCE), Offset(c.x - w * 0.02f, tapa.top - w * 0.008f), Size(w * 0.04f, w * 0.014f), CornerRadius(w * 0.006f))
        drawArc(l.b(0.45f), 190f, 80f, false, tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
        // El aro de hierro y las dos patas de delante.
        drawOval(l.s(0.35f), aro.topLeft + Offset(0f, w * 0.006f), aro.size, style = Stroke(w * 0.012f))
        drawArc(l.c(P.HIERRO), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(w * 0.012f))
        drawArc(if (a.oscuro) l.filo(0.95f) else l.b(0.45f), 20f, 140f, false, Offset(aro.left, aro.top + w * 0.004f), aro.size, style = Stroke(w * 0.0035f))
        listOf(c.x - w * 0.15f to -1f, c.x + w * 0.13f to 1f).forEach { (x, s) ->
            val p0 = Offset(x, aroY + w * 0.018f)
            val p1 = Offset(x + s * w * 0.025f, h + 2f)
            drawLine(l.c(P.HIERRO), p0, p1, w * 0.013f, StrokeCap.Round)
            drawLine(if (a.oscuro) l.filo(0.75f) else l.b(0.35f), p0 + Offset(-s * w * 0.004f, 0f), p1 + Offset(-s * w * 0.004f, 0f), w * 0.0035f)
        }
        // El humo: de día, un hilo gris que sube y se deshace; de noche, alumbrado desde abajo.
        val humo = Path().apply {
            moveTo(c.x - w * 0.01f, tapa.top)
            cubicTo(c.x - w * 0.05f, tapa.top - w * 0.06f, c.x + w * 0.03f, tapa.top - w * 0.1f, c.x - w * 0.02f, tapa.top - w * 0.16f)
            if (a.oscuro) cubicTo(c.x - w * 0.06f, tapa.top - w * 0.22f, c.x + w * 0.02f, tapa.top - w * 0.28f, c.x - w * 0.04f, tapa.top - w * 0.36f)
        }
        val colHumo = if (a.oscuro) mezcla(a.fuego, P.FIELTRO_GRIS, 0.5f) else mezcla(P.CREMA, P.FIELTRO_GRIS, 0.4f)
        drawPath(humo, colHumo.copy(alpha = if (a.oscuro) 0.1f else 0.07f), style = Stroke(w * 0.04f, cap = StrokeCap.Round))
        drawPath(corridoMongol(humo, w * 0.012f, w * 0.01f), colHumo.copy(alpha = if (a.oscuro) 0.12f else 0.09f), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
    }

    /**
     * Las llamas del hogar: tres lenguas grandes y dos chicas, ladeadas y de alturas distintas, cada una en tres capas (el rojo
     * de fuera, el naranja y el corazón claro), y unas chispas que suben; con [brasa], solo la lumbre baja de las brasas.
     */
    private fun DrawScope.llamas(base: Offset, alto: Float, w: Float, brasa: Boolean = false) {
        val lenguas = listOf(-0.09f to 0.6f, -0.035f to 1f, 0.02f to 0.82f, 0.075f to 0.55f, 0.115f to 0.35f)
        val capas = listOf(P.FUEGO to 1f, P.LLAMA to 0.72f, Color(0xFFFFF0C2) to 0.42f)
        capas.forEachIndexed { ci, (col, f) ->
            lenguas.forEachIndexed { i, (dx, al0) ->
                val x = base.x + w * dx * (if (brasa) 0.85f else 1f)
                val al = alto * al0 * f * (0.85f + 0.3f * azar(995, i))
                val an = w * 0.055f * (0.5f + 0.5f * f) * (0.7f + 0.3f * al0)
                val ladeo = (azar(996, i) - 0.4f) * an * 0.8f
                val ll = Path().apply {
                    moveTo(x - an / 2, base.y)
                    cubicTo(x - an * 0.7f, base.y - al * 0.45f, x + ladeo - an * 0.2f, base.y - al * 0.7f, x + ladeo, base.y - al)
                    cubicTo(x + ladeo + an * 0.1f, base.y - al * 0.62f, x + an * 0.7f, base.y - al * 0.35f, x + an / 2, base.y)
                    close()
                }
                drawPath(ll, col.copy(alpha = if (brasa) 0.7f else 0.93f))
            }
            if (ci == 0) drawOval(P.FUEGO.copy(alpha = if (brasa) 0.5f else 0.8f), Offset(base.x - w * 0.12f, base.y - alto * 0.12f), Size(w * 0.24f, alto * 0.2f))
        }
        if (!brasa) for (i in 0 until 7) {
            drawCircle(Color(0xFFFFD890).copy(alpha = 0.85f), w * (0.0025f + 0.002f * azar(999, i)),
                Offset(base.x + w * (azar(997, i) - 0.5f) * 0.22f, base.y - alto * (1.05f + 0.9f * azar(998, i))))
        }
    }
}
