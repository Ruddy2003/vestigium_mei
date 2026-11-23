package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo que acompaña en los huecos libres de las repisas del estante de Mongol: cosas grandes de la casa (del 60 al 90 % del alto
 * de la repisa), agrupadas y solapadas, distintas según la repisa. La pila alta de fieltros doblados, la mantequera de duelas
 * con su batidor, el odre grande del agua, el saco de grano de pelo tejido, los cuencos de madera encajados, el ovillo y el
 * cucharón.
 */
internal object HuecosEstanteMongol {

    /**
     * El hueco libre de una repisa, de [ocupado] al costado: un grupo de dos cosas grandes, la segunda montada sobre el canto de
     * la primera, y una chica delante, en la junta. Si el hueco es muy ancho (una repisa con pocas entradas), dos grupos
     * distintos: uno cerca de las entradas y otro contra el costado. Si no caben, las que quepan.
     */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, g: MarcoMongol) {
        val w = g.w
        val x0 = ocupado + w * 0.025f
        val x1 = g.x1 - w * 0.02f
        val ancho = x1 - x0
        val H = r.height
        val pie = r.bottom - w * 0.008f
        if (ancho < H * 0.22f) return
        val l = g.luz(Offset((x0 + x1) / 2, r.center.y))
        val grupo = Math.floorMod(k * 7 / 3, 3)
        val monta = H * 0.06f
        val primera = H * medidas(grupo).first
        if (ancho < primera) {
            // Solo cabe una cosa chica: los cuencos encajados.
            cuencosEncajados(Offset(x0 + ancho / 2, pie), minOf(H * 0.36f, ancho * 0.9f), l)
            return
        }
        val largo = H * (medidas(grupo).first + medidas(grupo).second) - monta
        val otro = (grupo + 1 + Math.floorMod(k, 2)) % 3
        val largo2 = H * (medidas(otro).first + medidas(otro).second) - monta
        if (ancho >= largo + largo2 + H * 0.2f) {
            val libre = ancho - largo - largo2
            grupoEn(grupo, x0 + libre * 0.2f, x1, pie, H, l, k)
            grupoEn(otro, x1 - largo2 - libre * 0.12f, x1, pie, H, l, k + 1)
            return
        }
        val sobra = (ancho - largo).coerceAtLeast(0f)
        grupoEn(grupo, x0 + sobra * 0.25f, x1, pie, H, l, k)
    }

    /** Los anchos de las dos cosas de cada grupo, en altos de repisa. */
    private fun medidas(grupo: Int): Pair<Float, Float> = when (grupo) { 0 -> 0.6f to 0.3f; 1 -> 0.5f to 0.48f; else -> 0.3f to 0.48f }

    /** Un grupo desde [xa]: la pila de fieltros y la mantequera; el odre y el saco; o la mantequera y el saco. */
    private fun DrawScope.grupoEn(grupo: Int, xa: Float, x1: Float, pie: Float, H: Float, l: LuzMongol, k: Int) {
        val (a, b) = medidas(grupo)
        val monta = H * 0.06f
        val primera = H * a; val segunda = H * b
        val xb = xa + primera - monta
        val caben = xb + segunda <= x1 + H * 0.02f
        when (grupo) {
            0 -> {
                pilaFieltros(xa, pie, H, primera, l, k)
                if (caben) batidor(xb, pie, H, segunda, l)
                cuencosEncajados(Offset(xb + (if (caben) segunda * 0.1f else -primera * 0.25f), pie), H * 0.34f, l)
            }
            1 -> {
                odre(xa, pie, H, primera, l)
                if (caben) {
                    sacoGrano(xb, pie, H, segunda, l, k)
                    cucharon(Offset(xb + segunda * 0.55f, pie), H, l)
                }
                ovillo(Offset(xb - H * 0.02f, pie), H * 0.075f, l)
            }
            else -> {
                batidor(xa, pie, H, primera, l)
                if (caben) sacoGrano(xb, pie, H, segunda, l, k + 3)
                ovillo(Offset(xa + primera + H * 0.02f, pie), H * 0.07f, l)
            }
        }
    }

    /** La sombra de una cosa en el fondo del casillero: de día a la derecha, de noche a la izquierda y arriba. */
    private fun DrawScope.sombraFondo(forma: Path, l: LuzMongol, H: Float) {
        val dx = if (l.a.oscuro) -H * 0.05f else H * 0.045f
        val dy = if (l.a.oscuro) -H * 0.025f else H * 0.012f
        drawPath(corridoMongol(forma, dx, dy), l.s(if (l.a.oscuro) 0.4f else 0.3f))
    }

    /**
     * La pila alta de fieltros doblados: cinco o seis paños (crudo, rubia, añil, gris), cada uno con el doblez redondo de un
     * lado y los cantos de las capas del otro, su pelusa y su pespunte; un poco corridos unos de otros y el de arriba con la punta
     * caída.
     */
    private fun DrawScope.pilaFieltros(x: Float, pie: Float, H: Float, an: Float, l: LuzMongol, k: Int) {
        val u = density
        val n = 5 + k % 2
        val gr = H * 0.72f / n
        val colores = listOf(P.CREMA, P.BERMELLON, P.ANIL, P.FIELTRO_GRIS, mezcla(P.CREMA, P.CRUDO, 0.5f), P.BERMELLON_OSC)
        val todo = Path().apply { addRect(Rect(x, pie - gr * n, x + an, pie)) }
        sombraFondo(todo, l, H)
        contactoMongol(Offset(x + an / 2, pie), an * 1.1f, H * 0.04f, l)
        for (i in 0 until n) {
            val y1 = pie - gr * i
            val dx = (azar(940 + k, i) - 0.5f) * an * 0.08f
            val r = Rect(x + dx, y1 - gr, x + an + dx, y1)
            val p = Path().apply { addRoundRect(RoundRect(r, CornerRadius(gr * 0.5f, gr * 0.5f))) }
            val col = colores[(i + k) % colores.size]
            drawPath(p, Brush.verticalGradient(listOf(l.c(mezcla(col, P.CREMA, 0.18f)), l.c(col), l.c(mezcla(col, P.HOLLIN, 0.35f))), r.top, r.bottom))
            clipPath(p) {
                // El doblez (de un lado, redondo y a la luz) y los cantos de las capas (del otro).
                val izq = (i + k) % 2 == 0
                drawOval(l.b(0.3f), Offset(if (izq) r.left else r.right - gr * 1.2f, r.top + gr * 0.12f), Size(gr * 1.2f, gr * 0.7f))
                val xc = if (izq) r.right - gr * 0.5f else r.left + gr * 0.5f
                for (q in 1..2) drawLine(l.s(0.35f), Offset(xc - gr * 0.3f, r.top + gr * q / 3f), Offset(xc + gr * 0.3f, r.top + gr * q / 3f), 0.7f * u)
                drawLine(l.c(mezcla(col, P.HOLLIN, 0.45f)).copy(alpha = 0.75f), Offset(r.left + gr * 0.7f, r.top + gr * 0.62f), Offset(r.right - gr * 0.7f, r.top + gr * 0.62f), 0.8f * u,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u), 0f))
            }
            drawRect(l.s(0.45f), Offset(r.left + gr * 0.3f, r.bottom - 1f * u), Size(r.width - gr * 0.6f, 1f * u))
        }
        val top = pie - gr * n
        // La punta del fieltro de arriba, que se vence por el canto y cuelga blanda, con su sombra en las capas de abajo.
        val punta = Path().apply {
            moveTo(x + an * 0.6f, top + gr * 0.12f)
            quadraticTo(x + an * 0.9f, top + gr * 0.02f, x + an * 1.01f, top + gr * 0.35f)
            quadraticTo(x + an * 1.03f, top + gr * 1.3f, x + an * 0.97f, top + gr * 1.75f)
            quadraticTo(x + an * 0.9f, top + gr * 1.5f, x + an * 0.86f, top + gr * 0.85f)
            quadraticTo(x + an * 0.74f, top + gr * 0.5f, x + an * 0.6f, top + gr * 0.12f)
            close()
        }
        drawPath(corridoMongol(punta, -1.4f * u, 1.8f * u), l.s(0.4f))
        drawPath(punta, l.c(mezcla(colores[(n - 1 + k) % colores.size], P.HOLLIN, 0.12f)))
        drawPath(Path().apply { moveTo(x + an * 0.62f, top + gr * 0.14f); quadraticTo(x + an * 0.9f, top + gr * 0.05f, x + an * 1f, top + gr * 0.36f) }, l.b(0.4f), style = Stroke(1f * u))
    }

    /**
     * La mantequera: el cubo alto de duelas, un poco más estrecho arriba, con tres aros de cuero crudo cosidos, la tapa con su
     * agujero y el batidor que asoma con su perilla; un chorreón de nata seca en una duela.
     */
    private fun DrawScope.batidor(x: Float, pie: Float, H: Float, an: Float, l: LuzMongol) {
        val u = density
        val alto = H * 0.62f
        val top = pie - alto
        val cx = x + an / 2
        val cuerpo = poligonoMongol(x + an * 0.06f, top, x + an * 0.94f, top, x + an, pie, x, pie)
        sombraFondo(cuerpo, l, H)
        contactoMongol(Offset(cx, pie), an * 1.15f, H * 0.04f, l)
        // El batidor: el palo que sale de la tapa, con su perilla arriba.
        val palo0 = Offset(cx, top); val palo1 = Offset(cx - an * 0.08f, pie - H * 0.9f)
        drawLine(l.s(0.35f), palo0 + Offset(H * 0.03f, 0f), palo1 + Offset(H * 0.03f, H * 0.01f), an * 0.1f)
        drawLine(l.c(P.ALERCE), palo0, palo1, an * 0.09f, StrokeCap.Round)
        drawLine(l.b(0.4f), palo0 - Offset(an * 0.025f, 0f), palo1 - Offset(an * 0.025f, 0f), an * 0.025f)
        drawCircle(l.c(P.ALERCE_OSC), an * 0.1f, palo1)
        drawCircle(l.b(0.4f), an * 0.04f, palo1 - Offset(an * 0.03f, an * 0.03f))
        drawPath(cuerpo, Brush.horizontalGradient(listOf(l.c(P.ALERCE_LUZ), l.c(P.ALERCE), l.c(P.ALERCE), l.c(P.ALERCE_OSC)), x, x + an))
        clipPath(cuerpo) {
            for (j in 1 until 5) {
                val f = j / 5f
                val xt = x + an * 0.06f + an * 0.88f * f; val xb = x + an * f
                drawLine(l.s(0.5f), Offset(xt, top), Offset(xb, pie), 1.1f * u)
                drawLine(l.b(0.18f), Offset(xt + 1.1f * u, top), Offset(xb + 1.1f * u, pie), 0.6f * u)
            }
            vetas(Rect(x, top, x + an, pie), l.c(P.ALERCE_OSC).copy(alpha = 0.3f), 1310, 0.6f, vertical = true, grosor = 0.6f)
            listOf(0.14f, 0.5f, 0.86f).forEach { f ->
                val y = top + alto * f
                drawRect(l.c(P.CRUDO), Offset(x - 2f, y - H * 0.018f), Size(an + 4f, H * 0.036f))
                drawRect(l.s(0.3f), Offset(x - 2f, y + H * 0.014f), Size(an + 4f, 1.2f * u))
                drawLine(l.c(P.CUERO), Offset(x, y), Offset(x + an, y), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 2f * u), 0f))
            }
            // El chorreón de nata seca.
            drawPath(Path().apply { moveTo(x + an * 0.3f, top); quadraticTo(x + an * 0.26f, top + alto * 0.25f, x + an * 0.31f, top + alto * 0.42f) },
                l.c(P.CUAJADA).copy(alpha = 0.8f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        }
        // La tapa vista desde arriba, con su agujero.
        val tapa = Rect(x + an * 0.02f, top - H * 0.025f, x + an * 0.98f, top + H * 0.025f)
        drawOval(l.c(P.ALERCE_OSC), tapa.topLeft, tapa.size)
        drawOval(l.c(mezcla(P.ALERCE, P.ALERCE_LUZ, 0.3f)), Offset(tapa.left + an * 0.04f, tapa.top + H * 0.004f), Size(tapa.width - an * 0.08f, tapa.height * 0.75f))
        drawOval(l.c(P.HOLLIN), Offset(cx - an * 0.08f, top - H * 0.008f), Size(an * 0.16f, H * 0.016f))
        drawLine(l.c(P.ALERCE), palo0, palo0 + (palo1 - palo0) * 0.04f, an * 0.09f)
        drawArc(l.b(0.5f), 190f, 80f, false, tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
    }

    /**
     * El odre grande del agua: la piel cosida, panzona y vencida abajo, con el cuello atado y su tapón de madera, la costura del
     * canto con sus puntadas, el lustre de la grasa arriba, la mancha de lo mojado bajo el cuello y la correa para llevarlo.
     */
    private fun DrawScope.odre(x: Float, pie: Float, H: Float, an: Float, l: LuzMongol) {
        val u = density
        val alto = H * 0.74f
        val cx = x + an * 0.5f
        val cuello = pie - alto * 0.86f
        val cuerpo = Path().apply {
            moveTo(cx - an * 0.12f, cuello)
            cubicTo(cx - an * 0.22f, cuello + alto * 0.12f, x - an * 0.02f, pie - alto * 0.55f, x + an * 0.02f, pie - alto * 0.18f)
            cubicTo(x + an * 0.04f, pie - alto * 0.02f, x + an * 0.2f, pie, cx, pie)
            cubicTo(x + an * 0.84f, pie, x + an * 1.02f, pie - alto * 0.04f, x + an * 1.0f, pie - alto * 0.24f)
            cubicTo(x + an * 0.98f, pie - alto * 0.58f, cx + an * 0.24f, cuello + alto * 0.12f, cx + an * 0.12f, cuello)
            close()
        }
        sombraFondo(cuerpo, l, H)
        contactoMongol(Offset(cx, pie), an * 1.1f, H * 0.045f, l)
        // La correa, detrás: del cuello a un lado.
        val correa = Path().apply { moveTo(cx + an * 0.1f, cuello + alto * 0.05f); cubicTo(x + an * 1.15f, cuello - alto * 0.05f, x + an * 1.12f, pie - alto * 0.5f, x + an * 0.96f, pie - alto * 0.32f) }
        drawPath(correa, l.c(mezcla(P.CUERO, P.HOLLIN, 0.3f)), style = Stroke(H * 0.025f, cap = StrokeCap.Round))
        drawPath(cuerpo, Brush.radialGradient(listOf(l.c(mezcla(P.CUERO, P.CRUDO, 0.45f)), l.c(P.CUERO), l.c(mezcla(P.CUERO, P.HOLLIN, 0.5f))),
            Offset(x + an * 0.32f, pie - alto * 0.55f), an * 0.85f))
        clipPath(cuerpo) {
            pinceladas(Rect(x, cuello, x + an, pie), l.c(mezcla(P.CUERO, P.CRUDO, 0.5f)), 1320, 10, u, 0.22f)
            drawPath(Path().apply { addOval(Rect(cx - an * 0.16f, cuello, cx + an * 0.2f, cuello + alto * 0.3f)) }, l.c(mezcla(P.CUERO, P.HOLLIN, 0.45f)).copy(alpha = 0.6f))
            drawPath(bordeMongol(cuerpo, -an * 0.08f, -alto * 0.04f), l.s(0.35f))
            drawOval(if (l.a.oscuro) l.filo(0.6f) else l.b(0.45f), Offset(x + an * 0.16f, pie - alto * 0.66f), Size(an * 0.18f, alto * 0.14f))
            drawPath(cuerpo, l.c(mezcla(P.CRUDO, P.CREMA, 0.3f)).copy(alpha = 0.65f), style = Stroke(an * 0.07f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 2.6f * u), 0f)))
            drawPath(cuerpo, l.c(mezcla(P.CUERO, P.HOLLIN, 0.5f)), style = Stroke(an * 0.035f))
        }
        // El cuello atado y el tapón.
        val nk = Rect(cx - an * 0.13f, cuello - alto * 0.06f, cx + an * 0.13f, cuello + 1f * u)
        drawRect(l.c(mezcla(P.CUERO, P.HOLLIN, 0.25f)), nk.topLeft, nk.size)
        drawRect(l.c(P.CRIN), Offset(nk.left - an * 0.02f, nk.top + nk.height * 0.4f), Size(nk.width + an * 0.04f, nk.height * 0.3f))
        val tapon = RoundRect(Rect(cx - an * 0.09f, nk.top - alto * 0.07f, cx + an * 0.09f, nk.top + 1f * u), CornerRadius(an * 0.04f))
        drawPath(Path().apply { addRoundRect(tapon) }, l.c(P.ALERCE))
        drawRect(l.b(0.45f), Offset(tapon.left + an * 0.02f, tapon.top + 1f * u), Size(an * 0.04f, tapon.height - 2f * u))
    }

    /**
     * El saco de grano, de pelo tejido: basto, vencido de un lado, con su trama en espiga y la boca abierta y doblada hacia fuera
     * (el rollo de la vuelta), que deja ver el montón de mijo; unos granos caídos en la tabla.
     */
    private fun DrawScope.sacoGrano(x: Float, pie: Float, H: Float, an: Float, l: LuzMongol, k: Int) {
        val u = density
        val alto = H * (0.6f + 0.06f * azar(1330, k))
        val cx = x + an * 0.5f
        val boca = pie - alto
        val vence = an * 0.06f * (if (k % 2 == 0) 1f else -1f)
        val forma = Path().apply {
            moveTo(x + an * 0.12f, boca + alto * 0.08f)
            cubicTo(x + an * 0.04f, boca + alto * 0.3f, x - an * 0.02f + vence, pie - alto * 0.3f, x + an * 0.04f + vence, pie - alto * 0.06f)
            quadraticTo(x + an * 0.1f, pie, cx, pie)
            quadraticTo(x + an * 0.92f, pie, x + an * 0.98f + vence, pie - alto * 0.08f)
            cubicTo(x + an * 1.04f + vence, pie - alto * 0.32f, x + an * 0.96f, boca + alto * 0.3f, x + an * 0.88f, boca + alto * 0.08f)
            close()
        }
        sombraFondo(forma, l, H)
        contactoMongol(Offset(cx, pie), an * 1.15f, H * 0.045f, l)
        val tela = mezcla(P.FIELTRO_GRIS, P.CUERO, 0.35f)
        drawPath(forma, Brush.horizontalGradient(listOf(l.c(mezcla(tela, P.CREMA, 0.25f)), l.c(tela), l.c(mezcla(tela, P.HOLLIN, 0.45f))), x, x + an))
        clipPath(forma) {
            // La trama en espiga del pelo tejido.
            val trama = Path()
            var y = boca
            var f = 0
            while (y < pie) {
                var xx = x - an * 0.1f
                val s = if (f % 2 == 0) 1f else -1f
                while (xx < x + an * 1.1f) { trama.moveTo(xx, y + H * 0.02f); trama.lineTo(xx + s * H * 0.012f, y); xx += H * 0.022f }
                y += H * 0.024f; f++
            }
            drawPath(trama, l.c(mezcla(tela, P.HOLLIN, 0.35f)).copy(alpha = 0.55f), style = Stroke(0.8f * u))
            drawPath(bordeMongol(forma, -an * 0.08f, -alto * 0.05f), l.s(0.3f))
            drawOval(l.b(0.22f), Offset(x + an * 0.14f, boca + alto * 0.25f), Size(an * 0.32f, alto * 0.3f))
            // Las arrugas del peso.
            drawPath(Path().apply { moveTo(cx - an * 0.1f, boca + alto * 0.2f); quadraticTo(cx - an * 0.2f, pie - alto * 0.4f, cx - an * 0.05f, pie - alto * 0.1f) }, l.s(0.3f), style = Stroke(1.3f * u))
        }
        // La boca abierta: el montón de mijo dentro y, delante, el rollo de la vuelta del saco, con su trama y su sombra.
        val bocaR = Rect(x + an * 0.1f, boca - alto * 0.02f, x + an * 0.9f, boca + alto * 0.16f)
        drawOval(l.c(mezcla(tela, P.HOLLIN, 0.55f)), bocaR.topLeft, bocaR.size)
        val monton = Path().apply {
            moveTo(bocaR.left + an * 0.04f, bocaR.center.y)
            cubicTo(bocaR.left + an * 0.15f, bocaR.top - alto * 0.08f, bocaR.right - an * 0.2f, bocaR.top - alto * 0.1f, bocaR.right - an * 0.04f, bocaR.center.y)
            close()
        }
        drawPath(monton, l.c(mezcla(P.ORO, P.CRUDO, 0.35f)))
        clipPath(monton) {
            for (q in 0 until 24) drawCircle(l.c(if (q % 3 == 0) mezcla(P.ORO, P.CUERO, 0.3f) else mezcla(P.ORO, P.CREMA, 0.3f)), 1f * u,
                Offset(bocaR.left + bocaR.width * azar(1333 + k, q), bocaR.top - alto * 0.08f + alto * 0.14f * azar(1334 + k, q)))
            drawRect(l.b(0.3f), Offset(bocaR.left, bocaR.top - alto * 0.1f), Size(bocaR.width * 0.5f, alto * 0.06f))
        }
        val vuelta = Path().apply {
            moveTo(bocaR.left - an * 0.04f, bocaR.center.y)
            quadraticTo(bocaR.center.x, bocaR.bottom + alto * 0.06f, bocaR.right + an * 0.04f, bocaR.center.y)
            lineTo(bocaR.right + an * 0.03f, bocaR.center.y + alto * 0.1f)
            quadraticTo(bocaR.center.x, bocaR.bottom + alto * 0.16f, bocaR.left - an * 0.03f, bocaR.center.y + alto * 0.1f)
            close()
        }
        drawPath(corridoMongol(vuelta, 0f, 1.6f * u), l.s(0.4f))
        drawPath(vuelta, l.c(mezcla(tela, P.CREMA, 0.18f)))
        drawPath(vuelta, l.c(mezcla(tela, P.HOLLIN, 0.3f)).copy(alpha = 0.5f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1.4f * u), 0f)))
        // El mijo caído.
        for (q in 0 until 9) {
            val p = Offset(x + an * (0.6f + 0.5f * azar(1331 + k, q)), pie - H * 0.012f * azar(1332 + k, q))
            drawCircle(l.c(P.ORO), 0.9f * u, p)
        }
    }

    /** Cuatro cuencos de madera encajados uno en otro, cada uno un poco más chico, con el labio de cada uno a la vista. */
    private fun DrawScope.cuencosEncajados(c: Offset, an: Float, l: LuzMongol) {
        val u = density
        contactoMongol(Offset(c.x, c.y), an * 1.05f, an * 0.12f, l)
        val tonos = listOf(P.ALERCE, mezcla(P.ALERCE, P.CUERO, 0.4f), mezcla(P.ALERCE, P.ALERCE_LUZ, 0.3f), mezcla(P.ALERCE, P.HOLLIN, 0.25f))
        for (i in 0 until 4) {
            val a = an * (1f - 0.09f * i)
            val al = a * 0.38f
            val pie = c.y - an * 0.11f * i
            val cuenco = Path().apply {
                moveTo(c.x - a / 2, pie - al); cubicTo(c.x - a / 2, pie - al * 0.2f, c.x - a * 0.2f, pie, c.x - a * 0.15f, pie)
                lineTo(c.x + a * 0.15f, pie); cubicTo(c.x + a * 0.2f, pie, c.x + a / 2, pie - al * 0.2f, c.x + a / 2, pie - al); close()
            }
            drawPath(cuenco, Brush.horizontalGradient(listOf(l.c(mezcla(tonos[i], P.ALERCE_LUZ, 0.4f)), l.c(tonos[i]), l.c(mezcla(tonos[i], P.HOLLIN, 0.45f))), c.x - a / 2, c.x + a / 2))
            drawOval(l.c(mezcla(tonos[i], P.HOLLIN, 0.4f)), Offset(c.x - a / 2, pie - al - a * 0.05f), Size(a, a * 0.1f))
            drawArc(l.b(0.6f), 180f, 120f, false, Offset(c.x - a / 2, pie - al - a * 0.05f), Size(a, a * 0.1f), style = Stroke(1.1f * u))
        }
    }

    /** Un ovillo de lana teñida de rubia, con sus vueltas cruzadas y la hebra suelta. */
    private fun DrawScope.ovillo(pie: Offset, r: Float, l: LuzMongol) {
        val u = density
        val c = Offset(pie.x, pie.y - r)
        contactoMongol(Offset(c.x, pie.y), r * 2.1f, r * 0.35f, l)
        drawCircle(l.c(mezcla(P.BERMELLON, P.CREMA, 0.2f)), r, c)
        clipPath(Path().apply { addOval(Rect(c, r)) }) {
            for (q in 0 until 7) rotate(q * 26f - 40f, c) { drawLine(l.c(P.BERMELLON_OSC).copy(alpha = 0.45f), Offset(c.x - r, c.y - r * 0.5f + q * r * 0.15f), Offset(c.x + r, c.y - r * 0.3f + q * r * 0.15f), 0.9f * u) }
            drawCircle(l.s(0.35f), r, c + Offset(r * 0.45f, r * 0.3f))
            drawCircle(l.b(0.35f), r * 0.4f, c - Offset(r * 0.35f, r * 0.35f))
        }
        drawPath(Path().apply { moveTo(c.x + r * 0.7f, c.y + r * 0.6f); cubicTo(c.x + r * 1.4f, pie.y, c.x + r * 1.8f, pie.y - r * 0.2f, c.x + r * 2.2f, pie.y - 0.5f * u) },
            l.c(mezcla(P.BERMELLON, P.CREMA, 0.2f)), style = Stroke(0.9f * u, cap = StrokeCap.Round))
    }

    /** El cucharón de asta, apoyado: la cuchara honda de cuerno en la tabla y el mango que sube contra lo de al lado. */
    private fun DrawScope.cucharon(pie: Offset, H: Float, l: LuzMongol) {
        val u = density
        val h0 = Offset(pie.x + H * 0.05f, pie.y - H * 0.03f)
        val h1 = Offset(pie.x + H * 0.2f, pie.y - H * 0.55f)
        drawLine(l.s(0.3f), h0 + Offset(H * 0.02f, 0f), h1 + Offset(H * 0.03f, H * 0.01f), H * 0.03f, StrokeCap.Round)
        drawLine(l.c(mezcla(P.CRUDO, P.CUERO, 0.4f)), h0, h1, H * 0.026f, StrokeCap.Round)
        drawLine(l.b(0.5f), h0 - Offset(H * 0.008f, 0f), h1 - Offset(H * 0.008f, 0f), H * 0.007f)
        val cuch = Rect(pie.x - H * 0.04f, pie.y - H * 0.09f, pie.x + H * 0.11f, pie.y)
        drawOval(l.s(0.35f), cuch.topLeft + Offset(H * 0.015f, H * 0.008f), cuch.size)
        drawOval(l.c(mezcla(P.CRUDO, P.HOLLIN, 0.2f)), cuch.topLeft, cuch.size)
        drawOval(l.c(mezcla(P.CUERO, P.HOLLIN, 0.4f)), Offset(cuch.left + cuch.width * 0.12f, cuch.top + cuch.height * 0.12f), Size(cuch.width * 0.76f, cuch.height * 0.45f))
        drawArc(l.b(0.6f), 190f, 70f, false, cuch.topLeft, cuch.size, style = Stroke(1.2f * u))
    }
}
