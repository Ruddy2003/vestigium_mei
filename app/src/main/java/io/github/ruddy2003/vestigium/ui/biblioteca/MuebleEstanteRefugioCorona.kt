package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo de encima de la librería (se pinta una vez, en la corona guardada), de atrás adelante: las fundas de discos apoyadas en la
 * pared; el monitor de las bombas, que manda (la caja de esmalte coral con la cara crema, la pantalla redonda de fósforo con su
 * retícula y la onda, los mandos, el asa cromada y el cable que sube a la canaleta); la garrafa de agua purificada bajo el grifo
 * de la tubería; el casco del Ideario boca arriba, de frutero, con los tomates de la tomatera; y el vaso de agua delante.
 */
internal object CoronaEstanteRefugio {
    /** Dónde cae el grifo de la tubería (y la boca de la garrafa), en anchos. */
    const val GRIFO_X = 0.585f

    /** Lo que va detrás de la cornisa: el cable del monitor, que sube por la pared hasta la canaleta. */
    fun DrawScope.detras(g: MarcoRefugio) {
        val w = g.w; val u = density
        val l = g.luz(w * 0.43f, w * 0.2f, 0.01f)
        val cable = Path().apply { moveTo(w * 0.415f, w * 0.3f); cubicTo(w * 0.44f, w * 0.2f, w * 0.425f, w * 0.12f, w * 0.44f, w * 0.04f) }
        val ar = g.arrojada(0.5f)
        drawPath(corridoRefugio(cable, ar.x, ar.y), l.s(0.18f), style = Stroke(2.2f * u))
        drawPath(cable, l.c(mezcla(P.TINTA, P.OLIVA_OSC, 0.3f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(cable, l.b(0.25f), style = Stroke(0.7f * u))
    }

    /** Lo que pisa el tablero de arriba, de atrás adelante. */
    fun DrawScope.delante(g: MarcoRefugio) {
        discos(g)
        monitor(g)
        garrafa(g)
        casco(g)
        vaso(g)
    }

    /** La sombra de contacto de lo que pisa el tablero, corrida al lado contrario a la luz. */
    private fun DrawScope.contacto(g: MarcoRefugio, x0: Float, x1: Float, y: Float, a: Float = 0.42f) {
        val u = density
        val l = g.luz((x0 + x1) / 2, y, 0.04f)
        val dx = if (g.a.oscuro) 0f else (x1 - x0) * 0.06f
        drawOval(l.s(a), Offset(x0 + dx, y - 2.4f * u), Size(x1 - x0, 4.8f * u))
    }

    // ---------------------------------------------------------------- las fundas de discos

    /**
     * Tres fundas de discos apoyadas en la pared, ladeadas y encimadas, cada una con su dibujo de la época (sin letras): la verde
     * azulada con un bumerán mostaza, la mostaza con su franja de damero y la coral con una línea ondulada crema; de la de delante
     * asoma el disco negro con sus surcos y su etiqueta.
     */
    private fun DrawScope.discos(g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val lado = w * 0.15f
        val fundas = listOf(Triple(0.66f, -9f, 0), Triple(0.7f, -4f, 1), Triple(0.74f, 3f, 2))
        fundas.forEachIndexed { i, (fx, giro, tipo) ->
            val x0 = w * fx
            val r = Rect(x0, pie - lado, x0 + lado, pie)
            val l = g.luz(r.center, 0.02f + 0.004f * i)
            val pivote = Offset(if (giro < 0f) r.left else r.right, pie)
            rotate(giro, pivote) {
                val ar = g.arrojada(0.5f)
                drawRect(l.s(0.26f), r.topLeft + ar, r.size)
                // Del de en medio asoma el disco por arriba, medio sacado.
                if (i == 1) disco(Offset(r.center.x + lado * 0.06f, r.top + lado * 0.02f), lado * 0.46f, l)
                val f = Path().apply { addRect(r) }
                val base = when (tipo) { 0 -> P.TEAL; 1 -> P.MANTEQUILLA; else -> P.CORAL }
                planosRefugio(f, l.c(base), l, -1f, 0.08f, 0.2f)
                clipRect(r.left, r.top, r.right, r.bottom) {
                    when (tipo) {
                        0 -> {
                            val c = Offset(r.left + lado * 0.5f, r.top + lado * 0.48f)
                            val bum = Path().apply {
                                moveTo(c.x - lado * 0.36f, c.y + lado * 0.14f)
                                quadraticTo(c.x - lado * 0.1f, c.y - lado * 0.3f, c.x + lado * 0.04f, c.y - lado * 0.3f)
                                quadraticTo(c.x + lado * 0.12f, c.y - lado * 0.05f, c.x + lado * 0.38f, c.y + lado * 0.16f)
                                quadraticTo(c.x + lado * 0.04f, c.y - lado * 0.06f, c.x - lado * 0.36f, c.y + lado * 0.14f)
                                close()
                            }
                            drawPath(bum, l.c(P.MANTEQUILLA))
                            for (j in 0 until 7) drawCircle(l.c(P.VINILO), lado * 0.02f, Offset(r.left + lado * (0.12f + 0.12f * j), r.top + lado * (0.78f + 0.06f * (j % 2))))
                        }
                        1 -> {
                            val y = r.top + lado * 0.7f
                            val cel = lado / 10f
                            for (j in 0 until 10) for (q in 0 until 2) {
                                if ((j + q) % 2 == 0) drawRect(l.c(P.TINTA), Offset(r.left + cel * j, y + cel * q), Size(cel, cel))
                            }
                            drawCircle(l.c(P.CORAL), lado * 0.16f, Offset(r.left + lado * 0.36f, r.top + lado * 0.34f))
                            drawCircle(l.c(P.TEAL), lado * 0.08f, Offset(r.left + lado * 0.64f, r.top + lado * 0.42f))
                        }
                        else -> {
                            val onda = Path().apply {
                                moveTo(r.left, r.top + lado * 0.55f)
                                var x = r.left; var k = 0
                                while (x < r.right && k < 8) { quadraticTo(x + lado * 0.1f, r.top + lado * (if (k % 2 == 0) 0.4f else 0.7f), x + lado * 0.2f, r.top + lado * 0.55f); x += lado * 0.2f; k++ }
                            }
                            drawPath(onda, l.c(P.VINILO), style = Stroke(lado * 0.06f))
                            drawRect(l.c(P.TEAL), Offset(r.left, r.top + lado * 0.84f), Size(lado, lado * 0.07f))
                        }
                    }
                    // El cartón gastado: los cantos clarean y una mancha de humedad.
                    drawRect(l.b(0.3f), r.topLeft, Size(lado, 1.2f * u))
                    drawRect(l.b(0.25f), r.topLeft, Size(1.2f * u, lado))
                    pinceladas(r, if (g.a.oscuro) g.a.hondo else P.CREMA_OSC, 9400 + i, 6, u, 0.16f)
                }
                drawRect(l.s(0.35f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, lado))
            }
        }
        contacto(g, w * 0.65f, w * 0.9f, pie, 0.38f)
    }

    /** Un disco de vinilo: el negro con sus surcos que brillan en un arco, la etiqueta coral y el agujero. */
    private fun DrawScope.disco(c: Offset, r: Float, l: LuzRefugio) {
        val u = density
        drawCircle(l.s(0.3f), r, c + Offset(1.4f * u, 1.6f * u))
        drawCircle(l.c(Color(0xFF16181A)), r, c)
        var rr = r * 0.4f; var j = 0
        while (rr < r * 0.97f && j < 30) { drawCircle(l.c(Color(0xFF2A2E30)), rr, c, style = Stroke(0.5f * u)); rr += 1.8f * u; j++ }
        drawArc(l.b(0.5f), 200f, 50f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(r * 0.35f))
        drawCircle(l.c(P.CORAL), r * 0.32f, c)
        drawCircle(l.c(P.MANTEQUILLA), r * 0.32f, c, style = Stroke(0.8f * u))
        drawCircle(l.c(Color(0xFF16181A)), r * 0.05f, c)
    }

    // ---------------------------------------------------------------- el monitor de las bombas

    /**
     * El monitor de las bombas, que manda: la caja de esmalte coral con sus esquinas redondas, el costado en escorzo con las rejillas
     * y la cara crema; la pantalla redonda de fósforo en su bisel cromado, con la retícula y la onda de la presión; los mandos de
     * baquelita, dos palancas y el piloto ámbar; el asa cromada arriba y las patas de goma. La pantalla brilla por sí misma de día y
     * de noche; en los Sueños en oscuro, en reposo (la onda se aplana y se apaga).
     */
    private fun DrawScope.monitor(g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val f = Rect(w * 0.055f, pie - w * 0.222f, w * 0.415f, pie - w * 0.008f)
        val tw = f.width; val th = f.height
        val l = g.luz(f.center, 0.03f)
        val dv = Offset(tw * 0.09f, -th * 0.1f)
        val lado = poligonoRefugio(Offset(f.right - th * 0.06f, f.top + th * 0.04f), Offset(f.right + dv.x, f.top + dv.y + th * 0.08f), Offset(f.right + dv.x, f.bottom + dv.y), Offset(f.right, f.bottom))
        val arriba = poligonoRefugio(Offset(f.left + th * 0.08f, f.top), Offset(f.left + th * 0.08f + dv.x, f.top + dv.y), Offset(f.right + dv.x - th * 0.06f, f.top + dv.y), Offset(f.right - th * 0.06f, f.top))
        val cara = Path().apply { addRoundRect(RoundRect(f, CornerRadius(th * 0.12f))) }
        val todo = Path().apply { addPath(cara); addPath(lado); addPath(arriba) }
        // Su sombra en la pared (de día) y en el tablero.
        if (!g.a.oscuro) { val ar = g.arrojada(1.6f); drawPath(corridoRefugio(todo, ar.x, ar.y), l.s(0.2f)) }
        contacto(g, f.left - tw * 0.02f, f.right + dv.x, pie, 0.5f)
        // Las patas de goma.
        listOf(f.left + tw * 0.1f, f.right - tw * 0.14f).forEach { x ->
            drawRoundRect(l.c(P.BAQUELITA), Offset(x, f.bottom - 1f * u), Size(tw * 0.05f, pie - f.bottom + 1.4f * u), CornerRadius(1.2f * u))
        }
        // El costado y la tapa de arriba, en escorzo; las rejillas del costado.
        drawPath(lado, l.c(mezcla(P.CORAL, P.BAQUELITA, 0.42f)))
        drawPath(arriba, l.c(mezcla(P.CORAL, P.CREMA, 0.3f)))
        for (k in 0 until 6) {
            val y = f.top + th * (0.25f + 0.09f * k)
            drawLine(l.s(0.45f), Offset(f.right + dv.x * 0.25f, y + dv.y * 0.3f), Offset(f.right + dv.x * 0.8f, y + dv.y * 0.75f), 1.4f * u)
            drawLine(l.b(0.2f), Offset(f.right + dv.x * 0.25f, y + dv.y * 0.3f + 1.4f * u), Offset(f.right + dv.x * 0.8f, y + dv.y * 0.75f + 1.4f * u), 0.6f * u)
        }
        // La caja coral, con su volumen.
        planosRefugio(cara, l.c(P.CORAL), l, g.hacia(f.center.x, f.center.y), 0.1f, 0.18f)
        clipPath(cara) {
            drawRect(l.b(0.55f), f.topLeft, Size(tw, 1.3f * u))
            pinceladas(f, if (g.a.oscuro) g.a.hondo else mezcla(P.CORAL, P.BAQUELITA, 0.4f), 9501, 8, u, 0.12f)
            // El esmalte saltado en la esquina de abajo y un golpe.
            saltadoRefugio(Offset(f.left + tw * 0.04f, f.bottom - th * 0.06f), tw * 0.018f, 9502, l.c(P.CROMO_OSC), l.b(0.4f))
            saltadoRefugio(Offset(f.right - tw * 0.08f, f.top + th * 0.05f), tw * 0.012f, 9503, l.c(P.CROMO_OSC), l.b(0.4f))
        }
        // La cara crema, rehundida, con su filo.
        val panel = Rect(f.left + tw * 0.035f, f.top + th * 0.08f, f.right - tw * 0.035f, f.bottom - th * 0.1f)
        val panelP = Path().apply { addRoundRect(RoundRect(panel, CornerRadius(th * 0.08f))) }
        drawPath(panelP, l.c(mezcla(P.VINILO, P.MANTEQUILLA, 0.2f)))
        clipPath(panelP) {
            drawRect(l.s(0.3f), panel.topLeft, Size(panel.width, 2f * u))
            drawRect(l.s(0.2f), panel.topLeft, Size(2f * u, panel.height))
            pinceladas(panel, if (g.a.oscuro) g.a.hondo else P.CREMA_OSC, 9504, 8, u, 0.12f)
        }
        drawRect(l.b(0.6f), Offset(panel.left + th * 0.05f, panel.bottom - 1f * u), Size(panel.width - th * 0.1f, 1f * u))
        // La pantalla redonda: el bisel cromado, el vidrio y el fósforo.
        val c = g.pantalla
        val rp = w * 0.069f
        drawCircle(l.s(0.4f), rp * 1.2f, c + Offset(1.6f * u, 2f * u))
        drawCircle(l.c(P.CROMO_OSC), rp * 1.2f, c)
        drawCircle(Brush.linearGradient(listOf(l.c(P.CROMO_LUZ), l.c(P.CROMO), l.c(P.CROMO_OSC)), c - Offset(rp, rp), c + Offset(rp, rp)), rp * 1.14f, c)
        drawCircle(l.c(P.CROMO_OSC), rp * 1.03f, c)
        pantalla(g, c, rp)
        // Los mandos: tres perillas de baquelita, dos palancas y el piloto ámbar.
        val mx = panel.left + panel.width * 0.72f
        listOf(Offset(mx, panel.top + panel.height * 0.24f), Offset(mx + panel.width * 0.16f, panel.top + panel.height * 0.24f), Offset(mx + panel.width * 0.08f, panel.top + panel.height * 0.56f)).forEachIndexed { i, p ->
            val rm = th * (if (i == 2) 0.11f else 0.085f)
            drawCircle(l.s(0.4f), rm * 1.1f, p + Offset(1f * u, 1.4f * u))
            drawCircle(l.c(P.CROMO), rm * 1.15f, p)
            drawCircle(l.c(P.BAQUELITA), rm, p)
            drawCircle(l.c(mezcla(P.BAQUELITA, P.CREMA, 0.25f)), rm * 0.6f, p)
            val a = 0.8f + 1.3f * i
            drawLine(l.c(P.MANTEQUILLA), p, p + Offset(rm * 0.8f * cos(a), rm * 0.8f * sin(a)), 1f * u)
            drawCircle(l.b(0.6f), rm * 0.25f, p - Offset(rm * 0.35f, rm * 0.35f))
            // Las marcas de la escala, sin cifras.
            for (k in 0 until 5) {
                val b = 2.4f + 0.55f * k
                drawLine(l.c(P.TINTA).copy(alpha = 0.55f), p + Offset(rm * 1.3f * cos(b), rm * 1.3f * sin(b)), p + Offset(rm * 1.55f * cos(b), rm * 1.55f * sin(b)), 0.7f * u)
            }
        }
        listOf(panel.left + panel.width * 0.69f, panel.left + panel.width * 0.79f).forEachIndexed { i, x ->
            val y = panel.top + panel.height * 0.85f
            drawCircle(l.c(P.CROMO_OSC), th * 0.035f, Offset(x, y))
            val sube = if (i == 0) -1f else 1f
            drawLine(l.c(P.CROMO), Offset(x, y), Offset(x + th * 0.02f, y + sube * th * 0.08f), 1.8f * u, StrokeCap.Round)
            drawCircle(l.b(0.6f), 0.8f * u, Offset(x + th * 0.02f, y + sube * th * 0.08f))
        }
        val pc = Offset(panel.left + panel.width * 0.92f, panel.top + panel.height * 0.84f)
        drawCircle(l.c(P.CROMO), th * 0.04f, pc)
        drawCircle(P.AMBAR.copy(alpha = 0.3f), th * 0.07f, pc)
        drawCircle(P.AMBAR, th * 0.026f, pc)
        drawCircle(Color.White.copy(alpha = 0.7f), th * 0.009f, pc - Offset(th * 0.008f, th * 0.008f))
        // El asa cromada de arriba, abatida hacia atrás, con sus dos pernos.
        val ax0 = f.left + tw * 0.18f; val ax1 = f.right - tw * 0.2f
        val asa = Path().apply { moveTo(ax0, f.top + th * 0.02f); lineTo(ax0 + tw * 0.03f, f.top - th * 0.13f); lineTo(ax1 + dv.x * 0.4f, f.top - th * 0.13f + dv.y * 0.4f); lineTo(ax1 + dv.x * 0.3f, f.top + dv.y * 0.3f) }
        drawPath(corridoRefugio(asa, 1.2f * u, 1.6f * u), l.s(0.3f), style = Stroke(2.6f * u))
        drawPath(asa, l.c(P.CROMO_OSC), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawLine(l.b(0.85f), Offset(ax0 + tw * 0.03f, f.top - th * 0.13f - 0.8f * u), Offset(ax1 + dv.x * 0.4f, f.top - th * 0.13f + dv.y * 0.4f - 0.8f * u), 0.9f * u)
        listOf(ax0, ax1 + dv.x * 0.3f).forEach { x -> botonCromoRefugio(Offset(x, f.top + th * 0.02f + (if (x > ax0) dv.y * 0.3f else 0f)), th * 0.035f, l) }
        // Los filos de la cara.
        drawPath(bordeRefugio(cara, 1.2f * u, 1.2f * u), l.b(0.5f))
        if (g.a.oscuro) drawPath(bordeRefugio(cara, -1.2f * u, 0f), l.filo(0.5f))
    }

    /**
     * La pantalla de fósforo: el vidrio abombado verde hondo, la retícula (cuatro líneas a lo ancho y cuatro a lo alto, ninguna por
     * el medio: no hace cruz), la onda de la presión con su estela, las líneas de barrido y el reflejo curvo del vidrio. Brilla por
     * sí misma; de noche echa su halo; en reposo (los Sueños en oscuro), la onda se aplana y se apaga.
     */
    private fun DrawScope.pantalla(g: MarcoRefugio, c: Offset, r: Float) {
        val u = density
        val reposo = g.a.oscuro && g.a.noche
        val vidrio = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        if (g.a.oscuro) {
            mancha(c, r * 4.2f, P.FOSFORO.copy(alpha = (if (reposo) 0.12f else 0.2f)))
            mancha(c, r * 2f, P.FOSFORO.copy(alpha = (if (reposo) 0.14f else 0.24f)))
        } else if (g.a.tarde) mancha(c, r * 2.4f, P.FOSFORO.copy(alpha = 0.14f))
        drawPath(vidrio, Brush.radialGradient(listOf(if (reposo) Color(0xFF16402A) else Color(0xFF235E3A), P.PANTALLA), c, r))
        clipPath(vidrio) {
            val ret = Path()
            for (i in 1..4) {
                val t = -r + 2 * r * (i / 5f)
                ret.addRect(Rect(c.x + t - 0.35f * u, c.y - r, c.x + t + 0.35f * u, c.y + r))
                ret.addRect(Rect(c.x - r, c.y + t - 0.35f * u, c.x + r, c.y + t + 0.35f * u))
            }
            drawPath(ret, P.FOSFORO.copy(alpha = if (reposo) 0.1f else 0.2f))
            // La onda de la presión: una senoide algo torcida, con su estela más ancha y tenue.
            val onda = Path()
            val amp = if (reposo) r * 0.05f else r * 0.34f
            var k = 0
            while (k <= 40) {
                val t = k / 40f
                val x = c.x - r + 2 * r * t
                val y = c.y + amp * sin(t * 12.6f + 0.6f) * (0.85f + 0.15f * sin(t * 5f))
                if (k == 0) onda.moveTo(x, y) else onda.lineTo(x, y)
                k++
            }
            drawPath(onda, P.FOSFORO.copy(alpha = if (reposo) 0.25f else 0.35f), style = Stroke(4f * u, cap = StrokeCap.Round))
            drawPath(onda, (if (reposo) mezcla(P.FOSFORO, P.PANTALLA, 0.4f) else P.FOSFORO_CLARO), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            // Las líneas de barrido.
            val barrido = Path()
            var y = c.y - r; var j = 0
            while (y < c.y + r && j < 200) { barrido.addRect(Rect(c.x - r, y, c.x + r, y + 0.6f * u)); y += 2.2f * u; j++ }
            drawPath(barrido, P.NOCHE.copy(alpha = 0.2f))
            // El reflejo curvo del vidrio, arriba a la izquierda.
            val reflejo = Path().apply { moveTo(c.x - r * 0.78f, c.y + r * 0.1f); cubicTo(c.x - r * 0.8f, c.y - r * 0.6f, c.x - r * 0.3f, c.y - r * 0.82f, c.x + r * 0.2f, c.y - r * 0.82f) }
            drawPath(reflejo, if (g.a.oscuro) P.FOSFORO_CLARO.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.4f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
            // La sombra del bisel dentro, abajo a la derecha.
            drawPath(Path().apply { op(vidrio, Path().apply { addOval(Rect(c.x - r * 1.04f, c.y - r * 1.08f, c.x + r * 0.96f, c.y + r * 0.92f)) }, PathOperation.Difference) }, P.NOCHE.copy(alpha = 0.5f))
        }
    }

    // ---------------------------------------------------------------- la garrafa y el vaso

    /**
     * La garrafa de vidrio del agua purificada, bajo el grifo: el cuerpo de hombros redondos, el cuello con su labio, el agua con su
     * superficie, su hondo y sus burbujas, el vidrio con su brillo largo y su borde, y la pared que se ve a través, más verde. De
     * noche el fósforo la enciende por dentro.
     */
    private fun DrawScope.garrafa(g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * GRIFO_X
        val an = w * 0.118f; val al = w * 0.2f
        val r = Rect(cx - an / 2, pie - al, cx + an / 2, pie)
        val l = g.luz(r.center, 0.03f)
        val cuello = w * 0.036f
        val hombro = r.top + al * 0.36f
        val forma = Path().apply {
            moveTo(cx - cuello / 2, r.top)
            lineTo(cx - cuello / 2, r.top + al * 0.1f)
            cubicTo(cx - cuello / 2, r.top + al * 0.22f, r.left, r.top + al * 0.2f, r.left, hombro)
            lineTo(r.left, r.bottom - an * 0.12f)
            quadraticTo(r.left, r.bottom, r.left + an * 0.12f, r.bottom)
            lineTo(r.right - an * 0.12f, r.bottom)
            quadraticTo(r.right, r.bottom, r.right, r.bottom - an * 0.12f)
            lineTo(r.right, hombro)
            cubicTo(r.right, r.top + al * 0.2f, cx + cuello / 2, r.top + al * 0.22f, cx + cuello / 2, r.top + al * 0.1f)
            lineTo(cx + cuello / 2, r.top)
            close()
        }
        if (!g.a.oscuro) { val ar = g.arrojada(1.1f); drawPath(corridoRefugio(forma, ar.x, ar.y), l.s(0.12f)) }
        contacto(g, r.left - an * 0.04f, r.right + an * 0.04f, pie, 0.45f)
        // El vidrio: la pared vista a través, más verde y algo más honda.
        drawPath(forma, l.c(mezcla(P.AGUA_VIDRIO, P.CREMA, 0.45f)).copy(alpha = 0.55f))
        clipPath(forma) {
            // El agua, hasta los hombros.
            val nivel = r.top + al * 0.42f
            val agua = Rect(r.left, nivel, r.right, r.bottom)
            val claroA = l.c(P.AGUA_VIDRIO); val hondoA = l.c(P.AGUA_HONDA)
            drawRect(Brush.verticalGradient(listOf(claroA.copy(alpha = 0.8f), mezcla(claroA, hondoA, 0.5f).copy(alpha = 0.85f), hondoA.copy(alpha = 0.9f)), nivel, r.bottom), agua.topLeft, agua.size)
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, hondoA.copy(alpha = 0.4f)), r.left + an * 0.5f, r.right), Offset(r.left + an * 0.5f, nivel), Size(an * 0.5f, r.bottom - nivel))
            // La superficie: el menisco claro y su reflejo.
            drawOval(l.c(mezcla(P.AGUA_VIDRIO, P.CROMO_LUZ, 0.4f)), Offset(r.left, nivel - an * 0.05f), Size(an, an * 0.1f))
            drawOval(l.c(P.AGUA_HONDA).copy(alpha = 0.4f), Offset(r.left + an * 0.15f, nivel - an * 0.02f), Size(an * 0.7f, an * 0.05f))
            // Las burbujas.
            for (j in 0 until 6) {
                val b = Offset(r.left + an * (0.25f + 0.5f * azar(9600, j)), nivel + (r.bottom - nivel) * (0.15f + 0.75f * azar(9601, j)))
                drawCircle(l.b(0.5f), (0.7f + 0.8f * azar(9602, j)) * u, b, style = Stroke(0.5f * u))
            }
            // Las juntas de los bloques de la pared, vistas a través del agua, torcidas.
            drawLine(l.s(0.18f), Offset(r.left, r.top + al * 0.3f), Offset(r.right, r.top + al * 0.33f), 1f * u)
            // El hondo del vidrio del lado de la sombra y el brillo largo del lado de la luz.
            drawRect(l.s(0.25f), Offset(r.right - an * 0.14f, r.top), Size(an * 0.14f, al))
            drawRect(l.b(0.75f), Offset(r.left + an * 0.14f, hombro), Size(an * 0.07f, al * 0.5f))
            drawRect(l.b(0.4f), Offset(r.left + an * 0.25f, hombro + al * 0.06f), Size(an * 0.03f, al * 0.4f))
            if (g.a.oscuro) drawRect(l.filo(0.5f), Offset(r.left, r.top), Size(an * 0.08f, al))
        }
        drawPath(forma, l.c(mezcla(P.AGUA_HONDA, P.CROMO_OSC, 0.4f)).copy(alpha = 0.7f), style = Stroke(1.3f * u))
        drawPath(bordeRefugio(forma, 1.6f * u, 0f), l.b(0.55f))
        // El labio del cuello.
        drawRoundRect(l.c(mezcla(P.AGUA_VIDRIO, P.CROMO, 0.4f)), Offset(cx - cuello * 0.62f, r.top - 1.6f * u), Size(cuello * 1.24f, 3.6f * u), CornerRadius(1.6f * u))
        drawRect(l.b(0.7f), Offset(cx - cuello * 0.5f, r.top - 1.2f * u), Size(cuello, 0.8f * u))
    }

    /** El vaso de agua delante de la garrafa, con su agua, su brillo y su sombra. */
    private fun DrawScope.vaso(g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = g.apoyo + w * 0.004f
        val cx = w * 0.655f
        val an = w * 0.04f; val al = w * 0.052f
        val l = g.luz(cx, pie - al / 2, 0.045f)
        contacto(g, cx - an * 0.6f, cx + an * 0.7f, pie, 0.4f)
        val forma = poligonoRefugio(Offset(cx - an / 2, pie - al), Offset(cx + an / 2, pie - al), Offset(cx + an * 0.42f, pie), Offset(cx - an * 0.42f, pie))
        drawPath(forma, l.c(mezcla(P.AGUA_VIDRIO, P.CREMA, 0.5f)).copy(alpha = 0.5f))
        clipPath(forma) {
            drawRect(l.c(P.AGUA_VIDRIO).copy(alpha = 0.7f), Offset(cx - an, pie - al * 0.6f), Size(an * 2, al * 0.6f))
            drawRect(l.s(0.2f), Offset(cx + an * 0.25f, pie - al), Size(an * 0.3f, al))
            drawRect(l.b(0.7f), Offset(cx - an * 0.3f, pie - al * 0.9f), Size(an * 0.1f, al * 0.8f))
        }
        drawPath(forma, l.c(P.AGUA_HONDA).copy(alpha = 0.6f), style = Stroke(1f * u))
        drawOval(l.c(mezcla(P.AGUA_VIDRIO, P.CROMO_LUZ, 0.4f)), Offset(cx - an / 2, pie - al - an * 0.08f), Size(an, an * 0.16f), style = Stroke(1f * u))
    }

    // ---------------------------------------------------------------- el casco de frutero

    /**
     * El casco oliva del Ideario, boca arriba en el tablero, de frutero: la calota con su franja crema y un golpe, el ala vista desde
     * arriba (un óvalo con el hondo de dentro), el barboquejo que cuelga por fuera y los tres tomates de la tomatera, uno verde.
     */
    private fun DrawScope.casco(g: MarcoRefugio) {
        val w = g.w; val u = density
        val pie = g.apoyo
        val cx = w * 0.915f
        val cw = w * 0.15f
        val alto = w * 0.062f
        val boca = pie - alto
        val l = g.luz(cx, pie - alto / 2, 0.04f)
        contacto(g, cx - cw * 0.42f, cx + cw * 0.5f, pie, 0.45f)
        // La calota, boca arriba: media elipse hacia abajo.
        val calota = Path().apply {
            moveTo(cx - cw * 0.44f, boca)
            cubicTo(cx - cw * 0.44f, pie - alto * 0.05f, cx + cw * 0.44f, pie - alto * 0.05f, cx + cw * 0.44f, boca)
            close()
        }
        val ar = g.arrojada(0.8f)
        if (!g.a.oscuro) drawPath(corridoRefugio(calota, ar.x, ar.y), l.s(0.18f))
        // El hondo de dentro del ala (lo de atrás), antes que los tomates.
        val ala = Rect(cx - cw / 2, boca - cw * 0.08f, cx + cw / 2, boca + cw * 0.08f)
        drawOval(l.c(mezcla(P.OLIVA, P.OLIVA_LUZ, 0.3f)), ala.topLeft, ala.size)
        drawOval(l.c(mezcla(P.OLIVA_OSC, P.NOCHE, 0.3f)), Offset(ala.left + cw * 0.06f, ala.top + cw * 0.025f), Size(cw * 0.88f, cw * 0.12f))
        // Los tomates, asomando por la boca.
        listOf(Triple(Offset(cx - cw * 0.14f, boca - cw * 0.02f), cw * 0.12f, mezcla(P.TOMATE, P.NARANJA, 0.2f)),
            Triple(Offset(cx + cw * 0.13f, boca - cw * 0.01f), cw * 0.105f, P.TOMATE),
            Triple(Offset(cx + cw * 0.01f, boca - cw * 0.08f), cw * 0.09f, mezcla(P.HOJA, P.OCRE, 0.45f))).forEach { (p, r, col) ->
            val lt = g.luz(p, 0.045f)
            drawCircle(lt.s(0.3f), r, p + Offset(r * 0.25f, r * 0.2f))
            drawCircle(lt.c(col), r, p)
            drawCircle(lt.s(0.22f), r * 0.6f, p + Offset(r * 0.32f, r * 0.28f))
            drawCircle(lt.b(0.7f), r * 0.26f, p - Offset(r * 0.35f, r * 0.38f))
            // El cáliz: cinco puntas de hoja, chicas.
            for (k in 0 until 5) {
                val a = -1.57f + k * 1.2566f
                drawLine(lt.c(P.HOJA), p - Offset(0f, r * 0.85f), p - Offset(0f, r * 0.85f) + Offset(r * 0.38f * cos(a), r * 0.24f * sin(a)), 1.1f * u, StrokeCap.Round)
            }
        }
        // El ala de delante (el canto que mira a quien mira) y la calota encima.
        val delanteAla = Path().apply { addArc(ala, 0f, 180f) }
        drawPath(delanteAla, l.c(mezcla(P.OLIVA, P.OLIVA_LUZ, 0.45f)), style = Stroke(cw * 0.035f))
        planosRefugio(calota, l.c(P.OLIVA), l, g.hacia(cx, boca), 0.14f, 0.3f)
        clipPath(calota) {
            // La franja crema pintada y un golpe con su luz.
            drawRect(l.c(mezcla(P.CREMA, P.OCRE, 0.2f)), Offset(cx - cw / 2, boca + alto * 0.28f), Size(cw, alto * 0.16f))
            drawRect(l.s(0.15f), Offset(cx + cw * 0.12f, boca + alto * 0.28f), Size(cw * 0.4f, alto * 0.16f))
            drawOval(l.s(0.22f), Offset(cx - cw * 0.22f, boca + alto * 0.52f), Size(cw * 0.1f, alto * 0.22f))
            drawArc(l.b(0.45f), 90f, 140f, false, Offset(cx - cw * 0.22f, boca + alto * 0.52f), Size(cw * 0.1f, alto * 0.22f), style = Stroke(0.8f * u))
        }
        drawLine(l.b(0.7f), Offset(ala.left + cw * 0.04f, boca + 0.6f * u), Offset(ala.right - cw * 0.04f, boca + 0.6f * u), 1f * u)
        // El barboquejo, que cuelga por fuera del lado de la izquierda con su hebilla.
        val bq = Path().apply { moveTo(cx - cw * 0.4f, boca + 1f * u); cubicTo(cx - cw * 0.56f, boca + alto * 0.4f, cx - cw * 0.5f, pie - 2f * u, cx - cw * 0.38f, pie - 1f * u) }
        drawPath(bq, l.c(mezcla(P.NARANJA, P.TINTA, 0.62f)), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawRect(l.c(P.ACERO), Offset(cx - cw * 0.53f, boca + alto * 0.45f), Size(cw * 0.05f, alto * 0.16f), style = Stroke(1f * u))
    }
}
