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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña en los huecos libres de las repisas de Templario ([MuebleEstanteTemplario]): grupos de dos o tres cosas del
 * castillo, altas y solapadas, distintos de una repisa a otra. El candil de barro de piquera en su pie de hierro con el
 * albarelo azul de Siria (de noche, la luz de en medio); la cota de malla doblada en su cesto de palma con la orza del vinagre
 * (se limpia con arena y vinagre); la aljaba de virotes con la madeja de cuerda de ballesta; y los rollos de pergamino de pie
 * en su cesta, con el cuerno de tinta.
 */
internal object HuecosEstanteTemplario {
    private val P = PaletaTemplario

    /** Los grupos que no llevan luz, en el orden en que se reparten. */
    private const val MALLA = 0
    private const val ALJABA = 1
    private const val ROLLOS = 2

    /** El ancho de cada grupo, en anchos. */
    private fun anchoGrupo(g: Int) = when (g) { MALLA -> 0.2f; ALJABA -> 0.16f; else -> 0.17f }

    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, xc: Float, a: AireTemplario, cerca: (Offset) -> Float) {
        val w = size.width
        var x = oc + w * 0.022f
        val fin = xDer - w * 0.028f
        if (fin - x < w * 0.1f) return
        val piso = r.bottom - w * 0.006f
        if (xc >= 0f) {
            candil(a, Offset(xc, piso), r, cerca)
            x = xc + w * 0.13f
        }
        var g = Math.floorMod(k * 2 + 1, 3)
        var n = 0
        while (n < 3) {
            val an = w * anchoGrupo(g)
            if (x + an > fin) {
                // Si no cabe el grupo entero pero queda sitio, el más angosto.
                if (fin - x >= w * 0.1f && g != ALJABA) { g = ALJABA; continue }
                break
            }
            when (g) {
                MALLA -> malla(a, Offset(x + an / 2, piso), r, cerca, k)
                ALJABA -> aljaba(a, Offset(x + an / 2, piso), r, cerca, k)
                else -> rollos(a, Offset(x + an / 2, piso), r, cerca, k)
            }
            x += an + w * 0.03f
            g = (g + 1) % 3
            n++
        }
    }

    /** La llama del candil que está en [xc] en el hueco [r] (de noche y al ocaso), con su charco chico, después de las tablas. */
    fun DrawScope.llamaHueco(xc: Float, r: Rect, a: AireTemplario) {
        if (!a.lumbre) return
        val w = size.width
        val p = Offset(xc + w * 0.034f, r.bottom - r.height * MuebleEstanteTemplario.CANDIL_Y - w * 0.012f)
        val f = if (a.oscuro && a.noche) 0.7f else if (!a.oscuro) 0.5f else 1f
        if (a.oscuro) drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.26f * f), P.LLAMA.copy(alpha = 0.08f * f), Color.Transparent), p, w * 0.16f), w * 0.16f, p)
        val s = w * 0.011f * (0.7f + 0.3f * f)
        val llama = Path().apply {
            moveTo(p.x + s * 0.3f, p.y - s * 2.2f)
            cubicTo(p.x + s * 0.9f, p.y - s * 1.1f, p.x + s * 0.7f, p.y, p.x, p.y + s * 0.2f)
            cubicTo(p.x - s * 0.7f, p.y, p.x - s * 0.6f, p.y - s * 1f, p.x + s * 0.3f, p.y - s * 2.2f); close()
        }
        drawCircle(P.LLAMA.copy(alpha = 0.35f), s * 2.4f, p - Offset(0f, s * 0.8f))
        drawPath(llama, Color(0xFFFFA048))
        drawOval(P.FUEGO, Offset(p.x - s * 0.3f, p.y - s * 1f), Size(s * 0.6f, s * 1.1f))
    }

    /** El candil de barro de piquera en su pie de hierro (de tres patas, con la varilla y el plato), con el albarelo y un cuenco. */
    private fun DrawScope.candil(a: AireTemplario, pie: Offset, r: Rect, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val plato = r.bottom - r.height * MuebleEstanteTemplario.CANDIL_Y + w * 0.004f
        val k = cerca(Offset(x, plato))
        val hacia = if (a.oscuro) 1f else -1f
        // El albarelo azul, detrás a la derecha.
        val ax = x + w * 0.07f
        val alto = r.height * 0.74f
        val alb = Path().apply {
            moveTo(ax - w * 0.032f, y); lineTo(ax + w * 0.032f, y)
            lineTo(ax + w * 0.03f, y - alto * 0.12f)
            cubicTo(ax + w * 0.024f, y - alto * 0.4f, ax + w * 0.024f, y - alto * 0.6f, ax + w * 0.03f, y - alto * 0.84f)
            lineTo(ax + w * 0.022f, y - alto * 0.9f); lineTo(ax + w * 0.024f, y - alto)
            lineTo(ax - w * 0.024f, y - alto); lineTo(ax - w * 0.022f, y - alto * 0.9f); lineTo(ax - w * 0.03f, y - alto * 0.84f)
            cubicTo(ax - w * 0.024f, y - alto * 0.6f, ax - w * 0.024f, y - alto * 0.4f, ax - w * 0.03f, y - alto * 0.12f); close()
        }
        contactoTemplario(Offset(ax + w * 0.006f, y), w * 0.075f, w * 0.008f, a, 0.4f, hacia)
        drawPath(alb, a.dia(P.AZUL, 0f, k))
        clipPath(alb) {
            // Las bandas de la decoración en azul oscuro y el blanco del barro donde salta el vidriado.
            listOf(0.18f, 0.3f, 0.72f, 0.8f).forEach { fy -> drawRect(a.dia(mezcla(P.AZUL, Color(0xFF101830), 0.5f), 0f, k), Offset(ax - w * 0.04f, y - alto * fy), Size(w * 0.08f, alto * 0.02f)) }
            // La cenefa de la panza: una hilera de dientes chicos de lado a lado entre dos filetes, y puntos (nada que se lea
            // como letra).
            val dientes = Path()
            var xx = ax - w * 0.04f
            val yb = y - alto * 0.47f; val ya = y - alto * 0.53f
            while (xx < ax + w * 0.04f) { dientes.moveTo(xx, yb); dientes.lineTo(xx + w * 0.0035f, ya); dientes.lineTo(xx + w * 0.007f, yb); dientes.close(); xx += w * 0.007f }
            val oscuroAzul = a.dia(mezcla(P.AZUL, Color(0xFF101830), 0.5f), 0f, k)
            drawPath(dientes, oscuroAzul)
            drawRect(oscuroAzul, Offset(ax - w * 0.04f, ya - alto * 0.02f), Size(w * 0.08f, alto * 0.008f))
            drawRect(oscuroAzul, Offset(ax - w * 0.04f, yb + alto * 0.012f), Size(w * 0.08f, alto * 0.008f))
            for (j in 0 until 6) drawCircle(oscuroAzul, w * 0.0022f, Offset(ax - w * 0.028f + w * 0.011f * j, y - alto * 0.62f))
            drawRect(a.dia(P.BARRO, 0.1f, k), Offset(ax - w * 0.04f, y - alto * 0.06f), Size(w * 0.08f, alto * 0.06f))
            cilindroTemplario(Rect(ax - w * 0.032f, y - alto, ax + w * 0.032f, y), a, k, hacia, 1.4f)
            drawRoundRect(a.claro(0.7f), Offset(ax - w * 0.018f, y - alto * 0.7f), Size(w * 0.004f, alto * 0.25f), CornerRadius(w * 0.002f))
        }
        // La tela atada que lo tapa.
        drawPath(poligonoTemplario(ax - w * 0.028f, y - alto, ax - w * 0.02f, y - alto * 1.05f, ax + w * 0.02f, y - alto * 1.06f, ax + w * 0.03f, y - alto * 0.99f), a.dia(P.LINO_OSC, 0f, k))
        drawLine(a.dia(P.CRUZ, -0.1f, k), Offset(ax - w * 0.026f, y - alto * 0.94f), Offset(ax + w * 0.026f, y - alto * 0.94f), 1.4f * u)
        // El pie de hierro del candil: tres patas, la varilla y el plato.
        val pie3 = Path().apply {
            listOf(-1f, 0f, 1f).forEach { s -> moveTo(x, y - w * 0.018f); quadraticTo(x + s * w * 0.016f, y - w * 0.012f, x + s * w * 0.026f + (if (s == 0f) w * 0.006f else 0f), y + (if (s == 0f) w * 0.002f else 0f)) }
            moveTo(x, y - w * 0.018f); lineTo(x, plato)
        }
        contactoTemplario(Offset(x + w * 0.004f, y), w * 0.06f, w * 0.006f, a, 0.4f, hacia)
        drawPath(pie3, a.luz(P.HIERRO, k).let { if (a.oscuro) it else a.dia(P.HIERRO, 0f, k) }, style = Stroke(w * 0.004f, cap = StrokeCap.Round))
        drawLine(a.brillo(0.35f, k), Offset(x - w * 0.0012f, plato), Offset(x - w * 0.0012f, y - w * 0.02f), 0.7f * u)
        drawOval(a.dia(P.HIERRO, 0f, k), Offset(x - w * 0.024f, plato - w * 0.003f), Size(w * 0.048f, w * 0.008f))
        // El candil: un platillo de barro con la piquera a la derecha y el asa a la izquierda.
        val cand = Path().apply {
            moveTo(x - w * 0.026f, plato - w * 0.004f)
            cubicTo(x - w * 0.026f, plato - w * 0.018f, x + w * 0.012f, plato - w * 0.02f, x + w * 0.03f, plato - w * 0.012f)
            lineTo(x + w * 0.036f, plato - w * 0.014f); lineTo(x + w * 0.032f, plato - w * 0.006f)
            cubicTo(x + w * 0.02f, plato - w * 0.002f, x - w * 0.016f, plato, x - w * 0.026f, plato - w * 0.004f); close()
        }
        drawArc(a.dia(P.BARRO_OSC, 0f, k), 90f, 180f, false, Offset(x - w * 0.036f, plato - w * 0.02f), Size(w * 0.02f, w * 0.016f), style = Stroke(w * 0.004f))
        drawPath(cand, a.dia(P.BARRO, 0f, k))
        drawLine(a.dia(P.BARRO, 0.2f, k), Offset(x - w * 0.022f, plato - w * 0.014f), Offset(x + w * 0.026f, plato - w * 0.015f), 1f * u)
        drawOval(a.dia(Color(0xFF3A2614), 0f, k), Offset(x - w * 0.016f, plato - w * 0.016f), Size(w * 0.03f, w * 0.005f))
        // El cuenco de barro, delante a la izquierda.
        val cx = x - w * 0.035f
        val cu = Path().apply {
            moveTo(cx - w * 0.026f, y - w * 0.026f)
            cubicTo(cx - w * 0.024f, y - w * 0.006f, cx - w * 0.012f, y, cx, y); cubicTo(cx + w * 0.012f, y, cx + w * 0.024f, y - w * 0.006f, cx + w * 0.026f, y - w * 0.026f); close()
        }
        contactoTemplario(Offset(cx + w * 0.004f, y), w * 0.06f, w * 0.006f, a, 0.4f, hacia)
        drawPath(cu, a.dia(P.BARRO_OSC, 0.05f, k))
        clipPath(cu) { cilindroTemplario(Rect(cx - w * 0.026f, y - w * 0.026f, cx + w * 0.026f, y), a, k, hacia, 1.2f) }
        drawLine(a.dia(P.BARRO, 0.2f, k), Offset(cx - w * 0.026f, y - w * 0.026f), Offset(cx + w * 0.026f, y - w * 0.026f), 1.2f * u)
    }

    /** La cota de malla doblada en su cesto de palma (en pliegues, con una manga que cae por el borde), con la orza del vinagre detrás. */
    private fun DrawScope.malla(a: AireTemplario, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val k = cerca(Offset(x, y - r.height * 0.4f))
        val hacia = if (a.oscuro) 1f else -1f
        // La orza, detrás a la izquierda: alta, de barro con vidriado pardo en la boca.
        val ox = x - w * 0.055f
        val alto = r.height * (0.76f + 0.06f * azar(s, 3))
        val orza = Path().apply {
            moveTo(ox - w * 0.022f, y)
            cubicTo(ox - w * 0.04f, y - alto * 0.3f, ox - w * 0.042f, y - alto * 0.62f, ox - w * 0.018f, y - alto * 0.86f)
            lineTo(ox - w * 0.02f, y - alto); lineTo(ox + w * 0.02f, y - alto); lineTo(ox + w * 0.018f, y - alto * 0.86f)
            cubicTo(ox + w * 0.042f, y - alto * 0.62f, ox + w * 0.04f, y - alto * 0.3f, ox + w * 0.022f, y); close()
        }
        drawPath(orza, a.dia(P.BARRO, 0f, k))
        clipPath(orza) {
            drawRect(a.dia(Color(0xFF6A3A1C), 0f, k), Offset(ox - w * 0.05f, y - alto), Size(w * 0.1f, alto * 0.34f))
            for (j in 0 until 4) drawRoundRect(a.dia(Color(0xFF6A3A1C), 0f, k), Offset(ox - w * 0.03f + w * 0.018f * j, y - alto * 0.7f), Size(w * 0.007f, alto * (0.08f + 0.1f * azar(s, j))), CornerRadius(w * 0.003f))
            cilindroTemplario(Rect(ox - w * 0.042f, y - alto, ox + w * 0.042f, y), a, k, hacia, 1.4f)
        }
        drawOval(a.dia(Color(0xFF2E1A0E), 0f, k), Offset(ox - w * 0.018f, y - alto - w * 0.003f), Size(w * 0.036f, w * 0.006f))
        // El cesto, delante y a la derecha.
        val cw = w * 0.14f; val chh = r.height * 0.3f
        val cx = x + w * 0.025f
        val boca = y - chh
        contactoTemplario(Offset(cx + w * 0.008f, y), cw * 1.05f, w * 0.01f, a, 0.42f, hacia)
        // La malla doblada: tres pliegues gruesos encimados que rebosan del cesto, el de arriba más corto.
        val pliegues = listOf(
            Rect(cx - cw * 0.5f, boca - w * 0.034f, cx + cw * 0.46f, boca + w * 0.012f),
            Rect(cx - cw * 0.42f, boca - w * 0.062f, cx + cw * 0.34f, boca - w * 0.02f),
            Rect(cx - cw * 0.3f, boca - w * 0.084f, cx + cw * 0.16f, boca - w * 0.05f),
        )
        pliegues.forEach { p ->
            // Un pliegue de malla: plano arriba, con la vuelta redonda a los lados y el borde de abajo ondulado por el peso.
            val forma = Path().apply {
                moveTo(p.left + p.height * 0.3f, p.top)
                lineTo(p.right - p.height * 0.3f, p.top + p.height * 0.04f)
                quadraticTo(p.right, p.top + p.height * 0.1f, p.right, p.top + p.height * 0.55f)
                quadraticTo(p.right, p.bottom, p.right - p.height * 0.4f, p.bottom)
                var xx = p.right - p.height * 0.4f
                var q = 0
                while (xx > p.left + p.height * 0.4f) { xx -= p.width * 0.12f; lineTo(maxOf(xx, p.left + p.height * 0.4f), p.bottom - (if (q % 2 == 0) p.height * 0.12f else 0f)); q++ }
                quadraticTo(p.left, p.bottom, p.left, p.top + p.height * 0.55f)
                quadraticTo(p.left, p.top + p.height * 0.06f, p.left + p.height * 0.3f, p.top); close()
            }
            mallaEn(forma, p, a, k, hacia)
            // El canto del pliegue (abajo, en sombra) y la luz de arriba, que lo separan del de debajo.
            drawPath(forma, a.sombra(0.4f), style = Stroke(1.2f * u))
            drawLine(a.brillo(0.35f, k), Offset(p.left + p.height * 0.4f, p.top + 1f * u), Offset(p.right - p.height * 0.4f, p.top + 1f * u), 1f * u)
        }
        // El cesto, con su trenzado; la arena que se sale entre las palmas.
        val cesto = Path().apply {
            moveTo(cx - cw / 2, boca); lineTo(cx + cw / 2, boca)
            lineTo(cx + cw * 0.44f, y); lineTo(cx - cw * 0.44f, y); close()
        }
        drawPath(cesto, a.dia(P.PALMA, -0.05f, k))
        clipPath(cesto) {
            val t = Path()
            for (fila in 0 until 5) {
                val yy = boca + chh * (fila + 0.5f) / 5f
                var xx = cx - cw / 2
                while (xx < cx + cw / 2) { t.moveTo(xx, yy - chh * 0.07f); t.lineTo(xx + w * 0.008f, yy + chh * 0.07f); xx += w * 0.012f }
            }
            drawPath(t, a.dia(P.PALMA_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(1f * u))
            cilindroTemplario(Rect(cx - cw / 2, boca, cx + cw / 2, y), a, k, hacia, 1.2f)
            drawRect(a.dia(P.CALIZA, 0.1f, k).copy(alpha = 0.5f), Offset(cx - cw / 2, y - chh * 0.18f), Size(cw, chh * 0.18f))
        }
        drawLine(a.dia(P.PALMA, 0.15f, k), Offset(cx - cw / 2, boca), Offset(cx + cw / 2, boca), 2.2f * u)
        drawLine(a.sombra(0.35f), Offset(cx - cw / 2, boca + 1.6f * u), Offset(cx + cw / 2, boca + 1.6f * u), 1f * u)
        // La manga que cae por el borde de la derecha, ahusada, con su puño de cuero.
        // La manga, que cuelga por el borde del cesto casi hasta la tabla, ahusada, con el puño de cuero.
        val manga = Path().apply {
            moveTo(cx + cw * 0.24f, boca - w * 0.012f)
            cubicTo(cx + cw * 0.52f, boca - w * 0.02f, cx + cw * 0.64f, boca + chh * 0.25f, cx + cw * 0.62f, boca + chh * 0.86f)
            lineTo(cx + cw * 0.44f, boca + chh * 0.9f)
            cubicTo(cx + cw * 0.46f, boca + chh * 0.35f, cx + cw * 0.38f, boca + w * 0.012f, cx + cw * 0.2f, boca + w * 0.01f); close()
        }
        mallaEn(manga, Rect(cx + cw * 0.18f, boca - w * 0.025f, cx + cw * 0.66f, boca + chh * 0.95f), a, k, hacia)
        drawPath(manga, a.sombra(0.45f), style = Stroke(1.1f * u))
        drawLine(a.dia(P.CUERO, 0f, k), Offset(cx + cw * 0.44f, boca + chh * 0.88f), Offset(cx + cw * 0.62f, boca + chh * 0.84f), 3f * u)
        // Un puñado de arena en la tabla.
        drawOval(a.dia(P.CALIZA, 0.05f, k), Offset(cx + cw * 0.56f, y - w * 0.006f), Size(w * 0.03f, w * 0.007f))
    }

    /** Las anillas de una malla dentro de [p]: hileras al tresbolillo, cada anilla con su luz y su sombra (en dos caminos). */
    private fun DrawScope.mallaEn(p: Path, r: Rect, a: AireTemplario, k: Float, hacia: Float) {
        val u = density
        drawPath(p, a.dia(mezcla(P.HIERRO, P.HIERRO_LUZ, 0.2f), -0.12f, k))
        clipPath(p) {
            // Las anillas, como escamas chicas al tresbolillo: el borde de abajo de cada una, en sombra, y un punto de brillo
            // en hilera arriba (la malla brilla por filas, no de una pieza).
            val paso = 2.5f * u
            val escamas = Path(); val brillos = Path()
            var y = r.top; var fila = 0
            while (y < r.bottom + paso) {
                var x = r.left + (if (fila % 2 == 0) 0f else paso / 2)
                while (x < r.right + paso) {
                    escamas.addArc(Rect(x - paso * 0.5f, y - paso * 0.5f, x + paso * 0.5f, y + paso * 0.5f), 0f, 180f)
                    brillos.addOval(Rect(x - paso * 0.28f, y - paso * 0.36f, x + paso * 0.04f, y - paso * 0.16f))
                    x += paso
                }
                y += paso * 0.62f; fila++
            }
            drawPath(escamas, a.sombra(0.55f), style = Stroke(0.7f * u))
            drawPath(brillos, a.dia(P.HIERRO_LUZ, 0.15f, k).copy(alpha = 0.85f))
            val izq = hacia < 0f
            drawRect(a.sombra(0.35f), Offset(if (izq) r.right - r.width * 0.4f else r.left, r.top), Size(r.width * 0.4f, r.height))
            drawRect(a.brillo(0.2f, k), Offset(if (izq) r.left else r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
        }
    }

    /** La aljaba de cuero con los virotes (cortos y gruesos, con sus plumas de cuero) y la madeja de cuerda de ballesta. */
    private fun DrawScope.aljaba(a: AireTemplario, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val x = pie.x - w * 0.02f; val y = pie.y
        val k = cerca(Offset(x, y - r.height * 0.4f))
        val hacia = if (a.oscuro) 1f else -1f
        val alto = r.height * 0.62f
        val aw = w * 0.05f
        // Los virotes que asoman, cada uno con su emplumado.
        val virotes = Path(); val plumas = Path()
        for (j in 0 until 5) {
            val bx = x - aw * 0.34f + aw * 0.17f * j + (azar(s, 20 + j) - 0.5f) * w * 0.004f
            val tope = y - alto - r.height * (0.1f + 0.12f * azar(s, 30 + j))
            val inc = (j - 2) * w * 0.004f
            virotes.moveTo(bx, y - alto * 0.8f); virotes.lineTo(bx + inc, tope)
            plumas.moveTo(bx + inc - w * 0.006f, tope + w * 0.004f); plumas.lineTo(bx + inc, tope - w * 0.004f); plumas.lineTo(bx + inc + w * 0.006f, tope + w * 0.004f)
            plumas.lineTo(bx + inc + w * 0.006f, tope + w * 0.022f); plumas.lineTo(bx + inc, tope + w * 0.016f); plumas.lineTo(bx + inc - w * 0.006f, tope + w * 0.022f); plumas.close()
        }
        drawPath(virotes, a.dia(P.PALMA_OSC, 0.05f, k), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
        drawPath(plumas, a.dia(P.CUERO, 0.05f, k))
        drawPath(plumas, a.dia(P.LINO_OSC, 0f, k), style = Stroke(0.7f * u))
        // La aljaba: un tubo de cuero que se ensancha arriba, con su boca de cuero vuelto y dos correas.
        val tubo = Path().apply {
            moveTo(x - aw * 0.42f, y); lineTo(x + aw * 0.42f, y)
            lineTo(x + aw * 0.55f, y - alto); lineTo(x - aw * 0.55f, y - alto); close()
        }
        contactoTemplario(Offset(x + w * 0.006f, y), aw * 1.5f, w * 0.008f, a, 0.42f, hacia)
        drawPath(tubo, a.dia(P.CUERO, 0f, k))
        clipPath(tubo) {
            cilindroTemplario(Rect(x - aw * 0.55f, y - alto, x + aw * 0.55f, y), a, k, hacia, 1.4f)
            listOf(0.22f, 0.7f).forEach { fy -> drawRect(a.dia(P.CUERO_OSC, 0f, k), Offset(x - aw, y - alto * fy - w * 0.005f), Size(aw * 2, w * 0.01f)) }
            // Las puntadas de la costura y la labor de la boca.
            val punt = Path()
            var yy = y - alto * 0.95f
            while (yy < y) { punt.moveTo(x + aw * 0.1f, yy); punt.lineTo(x + aw * 0.1f, yy + w * 0.004f); yy += w * 0.009f }
            drawPath(punt, a.dia(P.LINO_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        }
        drawRect(a.dia(P.CUERO, 0.15f, k), Offset(x - aw * 0.58f, y - alto - w * 0.004f), Size(aw * 1.16f, w * 0.012f))
        // La madeja de cuerda, tumbada delante a la derecha.
        val mx = x + w * 0.065f; val my = y - w * 0.022f
        val madeja = Path()
        for (j in 0 until 5) madeja.addOval(Rect(mx - w * 0.035f + j * 0.8f * u, my - w * 0.02f + j * 0.5f * u, mx + w * 0.035f - j * 0.6f * u, my + w * 0.02f - j * 0.3f * u))
        contactoTemplario(Offset(mx + w * 0.004f, y), w * 0.08f, w * 0.008f, a, 0.4f, hacia)
        drawPath(madeja, a.dia(P.LINO_OSC, 0f, k), style = Stroke(w * 0.004f))
        drawPath(madeja, a.brillo(0.25f, k), style = Stroke(w * 0.0012f))
        drawLine(a.dia(P.CRUZ, -0.1f, k), Offset(mx - w * 0.006f, my - w * 0.022f), Offset(mx - w * 0.004f, my + w * 0.022f), 2f * u)
    }

    /** Los rollos de pergamino de pie en su cesta honda, atados con cordel, y el cuerno de tinta delante. */
    private fun DrawScope.rollos(a: AireTemplario, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val k = cerca(Offset(x, y - r.height * 0.4f))
        val hacia = if (a.oscuro) 1f else -1f
        val cw = w * 0.11f; val chh = r.height * 0.4f
        // Los rollos de pie, que asoman por encima de la cesta a distintas alturas.
        for (j in 0 until 4) {
            val rx = x - cw * 0.33f + cw * 0.22f * j
            val alto = r.height * (0.66f + 0.18f * azar(s, 40 + j))
            val rw = w * 0.024f
            val rr = Rect(rx - rw / 2, y - alto, rx + rw / 2, y - chh * 0.5f)
            val col = mezcla(P.PERGAMINO, P.PERG_OSC, 0.12f * j)
            rotate((j - 1.5f) * 4f, Offset(rx, y)) {
                drawRect(a.dia(col, 0f, k), rr.topLeft, rr.size)
                drawRect(a.sombra(0.25f), Offset(rr.right - rw * 0.35f, rr.top), Size(rw * 0.35f, rr.height))
                drawRect(a.brillo(0.3f, k), Offset(rr.left + rw * 0.15f, rr.top), Size(rw * 0.15f, rr.height))
                drawOval(a.dia(col, 0.1f, k), Offset(rr.left, rr.top - w * 0.004f), Size(rw, w * 0.008f))
                drawOval(a.dia(P.PERG_OSC, -0.1f, k), Offset(rr.left + rw * 0.3f, rr.top - w * 0.002f), Size(rw * 0.4f, w * 0.004f), style = Stroke(0.7f * u))
                drawRect(a.dia(P.CRUZ, -0.05f, k), Offset(rr.left, rr.top + rr.height * 0.25f), Size(rw, w * 0.004f))
            }
        }
        // La cesta honda.
        val cesta = Path().apply {
            moveTo(x - cw / 2, y - chh); lineTo(x + cw / 2, y - chh)
            cubicTo(x + cw * 0.52f, y - chh * 0.4f, x + cw * 0.44f, y, x + cw * 0.36f, y)
            lineTo(x - cw * 0.36f, y); cubicTo(x - cw * 0.44f, y, x - cw * 0.52f, y - chh * 0.4f, x - cw / 2, y - chh); close()
        }
        contactoTemplario(Offset(x + w * 0.006f, y), cw * 1.1f, w * 0.009f, a, 0.42f, hacia)
        drawPath(cesta, a.dia(P.PALMA, -0.1f, k))
        clipPath(cesta) {
            val t = Path()
            for (fila in 0 until 6) {
                val yy = y - chh + chh * (fila + 0.5f) / 6f
                var xx = x - cw / 2 + (if (fila % 2 == 0) 0f else w * 0.006f)
                while (xx < x + cw / 2) { t.addRect(Rect(xx, yy - chh * 0.05f, xx + w * 0.007f, yy + chh * 0.05f)); xx += w * 0.013f }
            }
            drawPath(t, a.dia(P.PALMA_OSC, 0f, k).copy(alpha = 0.6f))
            cilindroTemplario(Rect(x - cw / 2, y - chh, x + cw / 2, y), a, k, hacia, 1.3f)
        }
        drawLine(a.dia(P.PALMA, 0.15f, k), Offset(x - cw / 2, y - chh), Offset(x + cw / 2, y - chh), 2.2f * u)
        // El cuerno de tinta, tumbado delante.
        val tx = x + cw * 0.55f; val ty = y - w * 0.008f
        val cuerno = Path().apply { moveTo(tx - w * 0.006f, ty + w * 0.006f); quadraticTo(tx + w * 0.02f, ty + w * 0.004f, tx + w * 0.036f, ty - w * 0.012f); lineTo(tx + w * 0.032f, ty - w * 0.018f); quadraticTo(tx + w * 0.014f, ty - w * 0.006f, tx - w * 0.008f, ty - w * 0.006f); close() }
        contactoTemplario(Offset(tx + w * 0.016f, y), w * 0.05f, w * 0.006f, a, 0.4f, hacia)
        drawPath(cuerno, a.dia(P.CUERNO, 0f, k))
        drawPath(cuerno, a.dia(P.CUERNO_OSC, 0f, k), style = Stroke(0.7f * u))
        drawOval(a.dia(P.TINTA, 0f, k), Offset(tx - w * 0.009f, ty - w * 0.006f), Size(w * 0.005f, w * 0.012f))
    }
}
