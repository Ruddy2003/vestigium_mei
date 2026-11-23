package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaMongol as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La pared y el suelo del estante de Mongol: arriba, la cubierta de fieltro del techo con las varas pintadas que suben al
 * toono (las mismas que las del Ideario) sobre la cuerda de crin que ciñe el khana; el khana con sus ataduras, que asoma por
 * los lados; y colgado sobre él, el tapiz de fieltro teñido de añil con su orla de bermellón y crema y sus rombos escalonados
 * cosidos. Abajo, el suelo de fieltro con la estera acolchada y, de día, la franja del sol de la puerta.
 */
internal object MuroEstanteMongol {
    /** El alto de la franja del techo, en anchos. */
    const val TECHO = 0.085f
    /** El tapiz: sus bordes de los lados y su arranque, en anchos. */
    const val TAPIZ_IZQ = 0.05f
    const val TAPIZ_DER = 0.95f
    const val TAPIZ_ARRIBA = 0.105f

    fun DrawScope.pared(g: MarcoMongol) {
        val u = density
        val w = g.w; val ch = g.ch
        val a = g.a
        val techo = w * TECHO
        // ---- el fieltro crema de la pared, detrás del khana
        val lp = g.luz(Offset(w * 0.5f, ch * 0.6f), 0.5f)
        drawRect(lp.c(P.CREMA), Offset(0f, techo), Size(w, ch - techo))
        // ---- el khana: el enrejado de listones con sus ataduras, que asoma por los lados del tapiz
        khana(g, Rect(0f, techo, w, ch))
        // ---- el tapiz de añil
        tapiz(g)
        // ---- el techo: la cubierta de fieltro y las varas pintadas
        techo(g)
        // ---- la luz sobre toda la pared: de día, la del toono, que baja de arriba; de noche, la luna y el resplandor del hogar
        if (!a.oscuro && !a.noche) {
            mancha(Offset(w * 0.32f, -w * 0.05f), w * 0.75f, luzMueble(0.22f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), ch * 0.45f, ch), Offset(0f, ch * 0.45f), Size(w, ch * 0.55f))
        } else if (a.noche && !a.oscuro) {
            mancha(Offset(w * 0.5f, -w * 0.1f), w * 0.7f, P.CREPUSCULO.copy(alpha = 0.16f))
            mancha(g.lampara, w * 0.3f, P.LLAMA.copy(alpha = 0.2f))
        } else {
            // La luna que entra por el toono: un haz frío que baja del centro, más fuerte en los Sueños.
            val luna = if (a.noche) 0.12f else 0.025f
            val haz = poligonoMongol(w * 0.36f, 0f, w * 0.64f, 0f, w * 0.78f, ch, w * 0.3f, ch)
            drawPath(haz, Brush.verticalGradient(listOf(P.LUNA.copy(alpha = luna * 1.4f), P.LUNA.copy(alpha = luna * 0.5f), Color.Transparent), 0f, ch))
            mancha(Offset(w * 0.5f, -w * 0.08f), w * 0.4f, P.LUNA.copy(alpha = luna))
            // El halo de la lámpara en el tapiz: tiñe el añil de su color.
            if (a.lampara > 0f) {
                mancha(g.lampara, w * 0.55f, P.FUEGO.copy(alpha = 0.1f * a.lampara))
                mancha(g.lampara, w * 0.24f, P.LLAMA.copy(alpha = 0.2f * a.lampara))
                mancha(g.lampara, w * 0.08f, Color(0xFFFFE7B0).copy(alpha = 0.4f * a.lampara))
            }
        }
    }

    /** El enrejado del khana en [r], con su sombra en el fieltro, el canto a la luz y una atadura de cuero crudo en cada cruce. */
    private fun DrawScope.khana(g: MarcoMongol, r: Rect) {
        val u = density
        val w = g.w
        val paso = w * 0.15f
        val lw = w * 0.0105f
        val crudo = mezcla(P.CRUDO, P.CREMA, 0.35f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (capa in 0..1) {
                var x = -r.height
                var j = 0
                while (x < w + r.height) {
                    val ini = Offset(x, r.top)
                    val fin = if (capa == 0) Offset(x + r.height, r.bottom) else Offset(x - r.height, r.bottom)
                    val l = g.luz(Offset((ini.x + fin.x) / 2, (ini.y + fin.y) / 2), 0.6f)
                    val col = mezcla(mezcla(P.KHANA, P.CREMA, 0.16f), P.ALERCE, 0.3f * azar(820 + capa, j))
                    drawLine(l.s(0.16f), ini + Offset(2.2f * u, 2.4f * u), fin + Offset(2.2f * u, 2.4f * u), lw, StrokeCap.Round)
                    drawLine(l.c(col), ini, fin, lw, StrokeCap.Round)
                    drawLine(l.b(if (capa == 1) 0.38f else 0.16f), ini + Offset(-lw * 0.28f, -lw * 0.1f), fin + Offset(-lw * 0.28f, -lw * 0.1f), lw * 0.24f)
                    x += paso; j++
                }
            }
            // Las ataduras: una vuelta de cuero crudo en cada cruce.
            // Los listones se cruzan a media vuelta de paso: en y = arriba + m·paso/2, cada paso a partir de -alto + m·paso/2.
            var m = 1
            while (r.top + m * paso / 2 < r.bottom + paso) {
                val y = r.top + m * paso / 2
                val x0 = -r.height + m * paso / 2
                var x = x0 - paso * kotlin.math.floor((x0 + paso) / paso)
                m++
                while (x < w + paso) {
                    val p = Offset(x, y)
                    val l = g.luz(p, 0.6f)
                    drawOval(l.s(0.25f), Offset(p.x - lw * 0.6f, p.y - lw * 0.45f), Size(lw * 1.6f, lw * 1.25f))
                    drawOval(l.c(crudo), Offset(p.x - lw * 0.8f, p.y - lw * 0.62f), Size(lw * 1.6f, lw * 1.25f))
                    drawLine(l.c(mezcla(P.CUERO, crudo, 0.4f)), Offset(p.x - lw * 0.55f, p.y - lw * 0.35f), Offset(p.x + lw * 0.5f, p.y + lw * 0.35f), 0.8f * u)
                    x += paso
                }
            }
        }
    }

    /**
     * El tapiz de fieltro de añil colgado sobre el khana: el campo con sus pinceladas y su pelusa, la orla de fieltro bermellón
     * cosida en aplicación con su cordón crema y sus dientes de lobo, y los rombos escalonados de fieltro crema en el campo (sin
     * brazos ni ganchos: ningún signo). Cuelga de lazadas de crin; se ve su grosor en los bordes y su sombra en el khana.
     */
    private fun DrawScope.tapiz(g: MarcoMongol) {
        val u = density
        val w = g.w; val ch = g.ch
        val r = Rect(w * TAPIZ_IZQ, w * TAPIZ_ARRIBA, w * TAPIZ_DER, ch + 2f)
        // El borde de abajo de los lados cuelga un poco ondulado; arriba, el fieltro se vence entre lazada y lazada.
        val forma = Path().apply {
            moveTo(r.left, r.top)
            val lazos = 9
            for (i in 1..lazos) {
                val x0 = r.left + r.width * (i - 1) / lazos; val x1 = r.left + r.width * i / lazos
                quadraticTo((x0 + x1) / 2, r.top + w * 0.008f, x1, r.top)
            }
            lineTo(r.right + w * 0.003f, r.bottom); lineTo(r.left - w * 0.003f, r.bottom); close()
        }
        // Su sombra en el khana y en la pared (a la derecha y abajo de día; de noche, apenas).
        drawPath(corridoMongol(forma, w * 0.012f, w * 0.01f), g.luz(r.center).s(if (g.a.oscuro) 0.4f else 0.24f))
        val l = g.luz(Offset(w * 0.5f, ch * 0.55f), 0.42f)
        drawPath(forma, l.c(P.ANIL))
        clipPath(forma) {
            // El fieltro: manchas de tinte desigual y la pelusa.
            pinceladas(r, l.c(P.ANIL_LUZ), 701, 34, u, 0.16f)
            pinceladas(r, l.c(P.ANIL_OSC), 702, 30, u, 0.2f)
            // La orla: una banda de fieltro bermellón por arriba y por los lados, con su cordón crema y los dientes de lobo.
            val b = w * 0.034f
            val orla = Path().apply {
                addRect(Rect(r.left, r.top, r.right, r.top + b + w * 0.008f))
                addRect(Rect(r.left, r.top, r.left + b, r.bottom))
                addRect(Rect(r.right - b, r.top, r.right, r.bottom))
            }
            drawPath(corridoMongol(orla, w * 0.003f, w * 0.004f), l.s(0.35f))
            drawPath(orla, l.c(P.BERMELLON))
            pinceladas(Rect(r.left, r.top, r.right, r.top + b), l.c(P.BERMELLON_OSC), 703, 12, u, 0.18f)
            val cordon = l.c(mezcla(P.CREMA, P.ORO, 0.2f))
            val y0 = r.top + b + w * 0.008f
            drawLine(cordon, Offset(r.left + b, y0), Offset(r.right - b, y0), 1.6f * u)
            drawLine(cordon, Offset(r.left + b, y0), Offset(r.left + b, r.bottom), 1.6f * u)
            drawLine(cordon, Offset(r.right - b, y0), Offset(r.right - b, r.bottom), 1.6f * u)
            // Los dientes de lobo de la orla de arriba: triángulos crema, cosidos.
            val dientes = Path()
            var x = r.left + b + w * 0.01f
            val dy0 = r.top + w * 0.012f; val dy1 = r.top + b - w * 0.002f
            val dw = w * 0.026f
            while (x + dw < r.right - b) {
                dientes.moveTo(x, dy1); dientes.lineTo(x + dw / 2, dy0); dientes.lineTo(x + dw, dy1); dientes.close()
                x += dw * 1.25f
            }
            var yl = y0 + w * 0.02f
            while (yl + dw < r.bottom) {
                for (xl in listOf(r.left + w * 0.006f, r.right - b + w * 0.006f)) {
                    dientes.moveTo(xl, yl + dw); dientes.lineTo(xl + (b - w * 0.012f) / 2, yl); dientes.lineTo(xl + b - w * 0.012f, yl + dw); dientes.close()
                }
                yl += dw * 1.25f
            }
            drawPath(corridoMongol(dientes, 0.8f * u, 0.9f * u), l.s(0.3f))
            drawPath(dientes, l.c(mezcla(P.CREMA, P.BERMELLON, 0.08f)))
            // Los rombos escalonados del campo, de fieltro crema con su hueco de añil y su punto de bermellón, gastados.
            val filas = listOf(0.2f, 0.33f, 0.46f)
            filas.forEachIndexed { i, fy ->
                var fx = 0.15f + (if (i % 2 == 0) 0f else 0.1f)
                while (fx < 0.88f) {
                    val c = Offset(w * fx, w * fy)
                    if (c.y < ch) rombo(c, w * 0.032f, l, 710 + i * 10 + (fx * 10).toInt())
                    fx += 0.2f
                }
            }
            // El pespunte del borde y el grosor del fieltro (el canto de la izquierda a la luz).
            drawPath(forma, l.c(mezcla(P.CREMA, P.ANIL, 0.4f)).copy(alpha = 0.6f), style = Stroke(1.1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.4f * u), 0f)))
        }
        drawLine(l.b(0.45f), Offset(r.left, r.top + w * 0.01f), Offset(r.left, r.bottom), 1.4f * u)
        drawLine(l.s(0.4f), Offset(r.right, r.top), Offset(r.right, r.bottom), 1.8f * u)
        // Las lazadas de crin de las que cuelga, sobre la cuerda del khana.
        for (i in 0..9) {
            val x = r.left + r.width * i / 9f
            val p = Offset(x, r.top)
            drawLine(l.s(0.3f), p + Offset(1.4f * u, 1.4f * u), Offset(x + 1.4f * u, w * TECHO + 1.4f * u), 1.6f * u)
            drawLine(l.c(P.CRIN), p, Offset(x, w * TECHO), 1.6f * u, StrokeCap.Round)
        }
    }

    /**
     * Un rombo escalonado de fieltro crema cosido en el campo: la silueta en tres escalones por lado (sin brazos ni ganchos), el
     * hueco de añil y el punto de bermellón, su sombra corrida y el pespunte. Algo gastado, para que no grite.
     */
    private fun DrawScope.rombo(c: Offset, r: Float, l: LuzMongol, semilla: Int) {
        val u = density
        val s = r / 3f
        val p = romboEscalonadoMongol(c, r, 4)
        drawPath(corridoMongol(p, 1.2f * u, 1.4f * u), l.s(0.28f))
        drawPath(p, l.c(mezcla(P.CREMA, P.ANIL, 0.34f + 0.1f * azar(semilla, 1))))
        val hueco = poligonoMongol(c.x, c.y - s * 1.1f, c.x + s * 1.1f, c.y, c.x, c.y + s * 1.1f, c.x - s * 1.1f, c.y)
        drawPath(hueco, l.c(P.ANIL))
        drawCircle(l.c(P.BERMELLON), s * 0.36f, c)
        drawPath(p, l.c(mezcla(P.ANIL, P.CREMA, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u), 0f)))
        // Lo gastado: el fieltro crema se ensució con el humo de un lado.
        drawCircle(l.s(0.12f), r * 0.7f, c + Offset(r * 0.4f, r * 0.3f))
    }

    /** La cubierta de fieltro del techo y las varas pintadas que suben al toono, sobre la cuerda de crin que ciñe el khana. */
    private fun DrawScope.techo(g: MarcoMongol) {
        val u = density
        val w = g.w
        val techo = w * TECHO
        val lt = g.luz(Offset(w * 0.5f, techo * 0.5f), 0.18f)
        drawRect(lt.c(mezcla(P.TECHO, P.CREMA, 0.3f)), Offset.Zero, Size(w, techo))
        pinceladas(Rect(0f, 0f, w, techo), lt.c(P.FIELTRO_GRIS), 720, 16, u, 0.14f)
        // Las varas (uni): madera pintada de rojizo, con la punta roja, una franja azul y un filete de oro, como en el Ideario.
        val fuga = Offset(w * 0.5f, -w * 2.6f)
        var xv = -w * 0.02f; var iv = 0
        while (xv < w * 1.04f) {
            val pie = Offset(xv, techo + w * 0.004f)
            val dir = (fuga - pie) / (fuga - pie).getDistance()
            val arriba = pie + dir * (techo * 1.4f)
            val gv = w * 0.016f
            val l = g.luz(Offset(xv, techo * 0.5f), 0.25f)
            drawLine(l.s(0.32f), pie + Offset(gv * 0.6f, 0f), arriba + Offset(gv * 0.6f, 0f), gv * 0.8f)
            drawLine(l.c(P.KHANA), pie, arriba, gv)
            drawLine(l.c(P.BERMELLON), pie, pie + dir * (techo * 0.2f), gv)
            drawLine(l.c(mezcla(P.ANIL, P.ANIL_LUZ, 0.4f)), pie + dir * (techo * 0.32f), pie + dir * (techo * 0.42f), gv)
            drawLine(l.c(P.ORO), pie + dir * (techo * 0.44f), pie + dir * (techo * 0.47f), gv)
            drawLine(l.b(0.35f), pie + Offset(-gv * 0.3f, 0f), arriba + Offset(-gv * 0.3f, 0f), gv * 0.22f)
            xv += w * (0.074f + 0.012f * azar(831, iv)); iv++
        }
        // La cuerda de crin que ciñe la cabeza del khana, torcida.
        val cuerda = Rect(0f, techo - w * 0.006f, w, techo + w * 0.008f)
        val lc = g.luz(Offset(w * 0.5f, techo), 0.5f)
        drawRect(lc.s(0.32f), Offset(0f, cuerda.bottom), Size(w, w * 0.006f))
        drawRect(lc.c(Color(0xFF4E3A2C)), cuerda.topLeft, cuerda.size)
        val torcida = Path()
        var xc = 0f
        while (xc < w) { torcida.moveTo(xc, cuerda.bottom); torcida.lineTo(xc + w * 0.012f, cuerda.top); xc += w * 0.011f }
        drawPath(torcida, lc.c(Color(0xFF8A7058)), style = Stroke(1.4f * u))
    }

    // ---------------------------------------------------------------- el suelo

    /**
     * El suelo, desde el pie del cuerpo cerrado hasta abajo: el fieltro gris del suelo y, encima, la estera de fieltro acolchado
     * (crema, con sus pespuntes de lana parda en líneas escalonadas inventadas y su ribete). De día cruza la franja del sol de la
     * puerta, de borde neto; de noche, el charco del hogar.
     */
    fun DrawScope.suelo(g: MarcoMongol) {
        val u = density
        val w = g.w; val h = g.h
        val y0 = g.suelo
        if (h - y0 < 2f) return
        val a = g.a
        val lf = g.luz(w * 0.4f, (y0 + h) / 2)
        drawRect(lf.c(mezcla(P.FIELTRO_GRIS, P.CUERO, 0.18f)), Offset(0f, y0 - 1f), Size(w, h - y0 + 1f))
        pinceladas(Rect(0f, y0, w, h), lf.c(mezcla(P.FIELTRO_GRIS, P.HOLLIN, 0.3f)), 740, 14, u, 0.16f)
        // La estera: un trapecio (se ve desde arriba), de la izquierda hasta pasado el centro.
        val e0 = y0 + (h - y0) * 0.14f
        val estera = poligonoMongol(w * 0.06f, e0, w * 0.86f, e0, w * 0.93f, h + 2f, -w * 0.02f, h + 2f)
        drawPath(corridoMongol(estera, w * 0.008f, -w * 0.003f), lf.s(0.3f))
        val le = g.luz(w * 0.4f, (e0 + h) / 2)
        drawPath(estera, le.c(mezcla(P.CREMA, P.FIELTRO_GRIS, 0.25f)))
        clipPath(estera) {
            pinceladas(Rect(0f, e0, w, h), le.c(P.FIELTRO_GRIS), 741, 16, u, 0.2f)
            // El ribete de bermellón del borde de atrás y los pespuntes escalonados en líneas paralelas.
            drawRect(le.c(P.ANIL), Offset(0f, e0), Size(w, w * 0.016f))
            drawRect(le.c(P.BERMELLON), Offset(0f, e0 + w * 0.016f), Size(w, w * 0.006f))
            drawRect(le.s(0.25f), Offset(0f, e0 + w * 0.022f), Size(w, 1.2f * u))
            val pespunte = PathEffect.dashPathEffect(floatArrayOf(3.4f * u, 2f * u), 0f)
            val hilo = le.c(mezcla(P.CUERO, P.HOLLIN, 0.45f)).copy(alpha = 0.85f)
            for (q in 0 until 4) {
                val yy = e0 + w * (0.04f + 0.034f * q)
                val esc = Path()
                var x = -w * 0.02f + q * w * 0.01f
                esc.moveTo(x, yy)
                var arriba = true
                while (x < w) {
                    // Dientes de lobo: un zigzag de puntas, sin ángulos rectos (nada de grecas).
                    val nx = x + w * 0.035f
                    esc.lineTo(nx, yy + (if (arriba) w * 0.014f else 0f))
                    x = nx; arriba = !arriba
                }
                drawPath(esc, hilo, style = Stroke(1.4f * u, pathEffect = pespunte))
                drawPath(corridoMongol(esc, 0f, 2.8f * u), hilo, style = Stroke(1.1f * u, pathEffect = pespunte))
            }
            // Lo gastado donde se pisa.
            drawOval(le.s(0.1f), Offset(w * 0.22f, h - w * 0.07f), Size(w * 0.34f, w * 0.06f))
        }
        // La sombra que el cuerpo cerrado deja en el suelo.
        drawRect(lf.s(0.42f), Offset(0f, y0), Size(w, 2.2f * u))
        drawRect(Brush.verticalGradient(listOf(lf.s(0.26f), Color.Transparent), y0, y0 + w * 0.05f), Offset(0f, y0), Size(w, w * 0.05f))
        // La luz: de día, la franja del sol de la puerta, de borde neto; de noche, el charco del hogar.
        if (!a.oscuro) {
            // El sol que cae en el suelo por la puerta (la misma mancha que en el mueble, más viva en el suelo claro).
            clipRect(0f, y0, w, h) { drawPath(poligonoSolMongol(g), luzMueble(if (a.noche) 0.04f else 0.1f)) }
        } else {
            clipRect(0f, y0, w, h) { mancha(g.hogar, w * 0.75f, a.fuego.copy(alpha = if (a.llamas) 0.32f else 0.16f)) }
        }
    }
}
