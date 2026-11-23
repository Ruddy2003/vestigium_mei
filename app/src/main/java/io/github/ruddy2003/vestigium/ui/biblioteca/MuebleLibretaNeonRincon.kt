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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El rincón del pasaplatos sin el pincho (se pinta una vez por tamaño; ver [MuebleLibretaNeon]): la pared de azulejo jade con la
 * tubería de cobre que la cruza, la nevera de bebidas a la izquierda, la encimera de acero con su canto enrollado y su frente
 * de puertas, el trapo colgado, el bote de los rotuladores, el timbre de mostrador, la bolsa de un pedido y la lámpara de calor.
 */
internal object RinconLibretaNeon {
    private val P = PaletaLibretaNeon
    private val Q = PaletaNeon
    private val COBRE = Color(0xFFB8703A)
    private val KRAFT = Color(0xFFA88C68)

    fun DrawScope.rincon(g: EscenaLibretaNeon) {
        pared(g)
        tuberia(g)
        encimera(g)
        trapo(g)
        nevera(g)
        bote(g)
        timbre(g)
        // En un teléfono angosto no cabe la bolsa entre la lámpara y el borde: se queda fuera del cuadro.
        if (g.w - g.lampara.x >= g.h * 0.27f) bolsa(g)
        lampara(g)
    }

    // ---------------------------------------------------------------- la pared

    /**
     * El azulejo jade de la casa, con su junta: cada uno con el color que le da la luz (el charco cálido de la lámpara, la luz fría
     * de la nevera, lo lejano hundido), su canto alto encendido y el bajo en sombra, la junta sucia, alguno rajado y alguno de otra
     * hornada; el remate de la encimera (la peana de acero que sube por la pared) y la sombra de la lámpara.
     */
    private fun DrawScope.pared(g: EscenaLibretaNeon) {
        val u = g.u; val w = g.w; val h = g.h
        val alto = g.atras
        drawRect(g.luz.t(w * 0.6f, alto * 0.5f).c(Q.JUNTA), Offset.Zero, Size(w, alto))
        val lado = h * 0.082f
        val junta = maxOf(1f, 1.1f * u)
        var f = 0
        var y = alto - lado
        // De abajo arriba, para que la fila de la encimera quede entera.
        while (y > -lado) {
            var c = 0; var x = 0f
            while (x < w) {
                val sem = c * 131 + f * 977
                val centro = Offset(x + lado / 2, y + lado / 2)
                val t = g.luz.t(centro)
                if (azar(sem, 14) > 0.55f) drawRect(t.s(0.2f + 0.2f * azar(sem, 15)), Offset(x - junta, y + lado - junta * 1.3f), Size(lado + junta, junta * 2.4f))
                val jx = (azar(sem, 11) - 0.5f) * 1.3f * u; val jy = (azar(sem, 12) - 0.5f) * 1.3f * u
                val cara = Rect(x + junta / 2 + jx, y + junta / 2 + jy, x + lado - junta / 2 + jx, y + lado - junta / 2 + jy)
                val z = azar(sem, 9)
                val base = when {
                    z > 0.95f -> mezcla(Q.HUESO, Q.JADE_LUZ, 0.3f)
                    z > 0.86f -> mezcla(Q.JADE, Color(0xFF468F9E), 0.4f)
                    else -> mezcla(Q.JADE, if (azar(sem, 1) > 0.5f) Q.JADE_LUZ else Q.JADE_OSC, 0.3f * azar(sem, 2))
                }
                drawRect(t.c(base), cara.topLeft, cara.size)
                drawRect(t.b(0.4f), cara.topLeft, Size(cara.width, maxOf(1f, 0.9f * u)))
                drawRect(t.s(0.32f), Offset(cara.left, cara.bottom - 1.2f * u), Size(cara.width, 1.2f * u))
                drawRect(t.b(0.22f), Offset(if (t.izq) cara.left else cara.right - 1.1f * u, cara.top), Size(1.1f * u, cara.height))
                // El reflejo del vidriado, del color de la luz que le toca (más vivo cerca de ella).
                if (t.k > 0.35f && azar(sem, 4) > 0.3f) {
                    val rx = cara.left + cara.width * (if (t.izq) 0.18f else 0.58f)
                    drawLine(t.b(0.5f, 1.1f), Offset(rx, cara.top + cara.height * 0.22f), Offset(rx + cara.width * 0.22f, cara.top + cara.height * 0.22f), 1.1f * u, StrokeCap.Round)
                }
                if (azar(sem, 5) > 0.9f) {
                    val p = Path().apply {
                        moveTo(cara.left + cara.width * azar(sem, 6), cara.top)
                        lineTo(cara.left + cara.width * (0.3f + 0.4f * azar(sem, 7)), cara.top + cara.height * 0.45f)
                        lineTo(cara.left + cara.width * azar(sem, 8), cara.bottom)
                    }
                    drawPath(p, t.s(0.5f), style = Stroke(0.8f * u))
                }
                x += lado; c++
            }
            y -= lado; f++
        }
        // El hollín y la grasa encima del pasaplatos: manchas largas y tenues que suben de la encimera.
        for (j in 0 until 6) {
            val x = g.xNevera + (w - g.xNevera) * azar(6511, j); val yy = alto - h * (0.05f + 0.25f * azar(6512, j))
            drawOval(g.luz.t(x, yy).s(0.1f + 0.08f * azar(6513, j)), Offset(x - h * 0.03f, yy - h * 0.12f), Size(h * (0.04f + 0.04f * azar(6514, j)), h * (0.14f + 0.1f * azar(6515, j))))
        }
        // El remate de acero de la encimera contra la pared, con su luz arriba.
        val r0 = alto - h * 0.026f
        tiraAcero(g, g.xNevera, w, r0, alto, 1f)
        drawRect(g.luz.t(w * 0.6f, r0).b(0.5f), Offset(g.xNevera, r0), Size(w - g.xNevera, 1f * u))
    }

    /** Una tira de acero de [x0] a [x1] entre [y0] e [y1], con la luz que le llega a lo largo (un degradado de diez paradas). */
    private fun DrawScope.tiraAcero(g: EscenaLibretaNeon, x0: Float, x1: Float, y0: Float, y1: Float, mas: Float, col: Color = P.ACERO) {
        if (x1 <= x0 || y1 <= y0) return
        val ym = (y0 + y1) / 2
        val paradas = Array(10) { i -> val f = i / 9f; f to g.luz.t(x0 + (x1 - x0) * f, ym).c(col, mas) }
        drawRect(Brush.horizontalGradient(*paradas, startX = x0, endX = x1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
    }

    /**
     * La tubería de cobre que cruza la pared, con sus abrazaderas atornilladas, la llave de paso de volante rojo y el gancho del
     * que cuelga la lámpara; su sombra en el azulejo.
     */
    private fun DrawScope.tuberia(g: EscenaLibretaNeon) {
        val u = g.u; val w = g.w; val h = g.h
        val y = h * 0.105f; val d = h * 0.036f
        val x0 = g.xNevera - h * 0.02f
        drawRect(g.luz.t(w * 0.6f, y).s(0.35f), Offset(x0, y + d * 0.55f), Size(w - x0, d * 0.7f))
        tiraAcero(g, x0, w, y - d / 2, y + d / 2, 1.15f, COBRE)
        tiraAcero(g, x0, w, y - d / 2 + d * 0.12f, y - d / 2 + d * 0.3f, 1.4f, mezcla(COBRE, Color.White, 0.35f))
        drawRect(g.luz.t(w * 0.6f, y).s(0.35f), Offset(x0, y + d * 0.22f), Size(w - x0, d * 0.28f))
        // La pátina verde en algunos tramos.
        for (j in 0 until 5) {
            val x = x0 + (w - x0) * azar(6531, j)
            drawOval(g.luz.t(x, y).c(Color(0xFF4E8A6E)).copy(alpha = 0.5f), Offset(x, y - d * 0.3f), Size(h * (0.03f + 0.04f * azar(6532, j)), d * 0.5f))
        }
        // Las abrazaderas.
        var x = x0 + h * 0.2f
        while (x < w) {
            val t = g.luz.t(x, y)
            drawRect(t.c(Q.ALUMINIO, 0.9f), Offset(x - d * 0.22f, y - d * 0.62f), Size(d * 0.44f, d * 1.24f))
            drawRect(t.b(0.5f), Offset(x - d * 0.22f, y - d * 0.62f), Size(d * 0.44f, 0.8f * u))
            clavoNeon(Offset(x, y - d * 0.78f), 1.2f * u, t, Q.ALUMINIO)
            x += h * 0.42f
        }
        // La llave de paso, cerca de la derecha: el cuerpo, el vástago y el volante rojo de radios.
        val lx = w - h * 0.16f
        val t = g.luz.t(lx, y)
        drawRect(t.c(Q.LATON), Offset(lx - d * 0.6f, y - d * 0.62f), Size(d * 1.2f, d * 1.24f))
        drawRect(t.c(Q.LATON_OSC), Offset(lx - d * 0.12f, y - d * 1.4f), Size(d * 0.24f, d * 0.8f))
        val vol = Offset(lx, y - d * 1.45f)
        drawOval(t.s(0.4f), Offset(vol.x - d * 0.7f + 1.4f * u, vol.y - d * 0.22f + 1.6f * u), Size(d * 1.4f, d * 0.44f))
        drawOval(t.c(Color(0xFFB8282C), 1.1f), Offset(vol.x - d * 0.7f, vol.y - d * 0.22f), Size(d * 1.4f, d * 0.44f), style = Stroke(2.2f * u))
        drawLine(t.c(Color(0xFFB8282C)), Offset(vol.x - d * 0.6f, vol.y), Offset(vol.x + d * 0.6f, vol.y), 1.4f * u)
        // El gancho en S del que cuelga la lámpara.
        val gx = g.lampara.x
        val tg = g.luz.t(gx, y)
        drawArc(tg.c(Q.HIERRO, 1.2f), 180f, 270f, false, Offset(gx - d * 0.45f, y - d * 0.75f), Size(d * 0.9f, d * 0.9f), style = Stroke(1.6f * u))
        drawArc(tg.c(Q.HIERRO, 1.2f), 0f, 200f, false, Offset(gx - d * 0.35f, y + d * 0.1f), Size(d * 0.7f, d * 0.7f), style = Stroke(1.6f * u))
    }

    // ---------------------------------------------------------------- la encimera

    /**
     * La encimera de acero: la cara de arriba, cepillada a lo largo, con el charco de la lámpara y su reflejo alargado, los cercos
     * de vasos y los rayones; el canto enrollado (su brillo y su sombra); el frente de dos puertas con su tirador hundido; y lo de
     * debajo, en sombra.
     */
    private fun DrawScope.encimera(g: EscenaLibretaNeon) {
        val u = g.u; val w = g.w; val h = g.h
        val x0 = g.xNevera
        // La cara de arriba, en franjas con la luz de cada una (la lámpara cerca, la nevera a la izquierda).
        tiraAcero(g, x0, w, g.atras, g.canto, 1.05f)
        clipRect(x0, g.atras, w, g.canto) {
            for (j in 0 until 18) {
                val yy = g.atras + (g.canto - g.atras) * azar(6541, j)
                drawLine(g.luz.t(w * 0.5f, yy).b(0.12f), Offset(x0, yy), Offset(w, yy + 0.5f * u), 0.5f * u)
            }
            // El reflejo de la lámpara en el acero, alargado hacia el que mira, y el de la nevera.
            val rl = g.lampara.x - h * 0.02f
            mancha(Offset(rl, g.atras + (g.canto - g.atras) * 0.55f), h * 0.2f, P.CALOR.copy(alpha = 0.35f))
            drawOval(P.CALOR_ALMA.copy(alpha = 0.35f), Offset(rl - h * 0.05f, g.atras + (g.canto - g.atras) * 0.3f), Size(h * 0.1f, (g.canto - g.atras) * 0.55f))
            drawRect(P.FRIO.copy(alpha = 0.12f), Offset(x0, g.atras), Size(h * 0.12f, g.canto - g.atras))
            // Cercos de vasos y rayones.
            for (j in 0 until 3) {
                val c = Offset(x0 + (w - x0) * (0.15f + 0.7f * azar(6551, j)), g.atras + (g.canto - g.atras) * (0.3f + 0.5f * azar(6552, j)))
                drawOval(g.luz.t(c).s(0.22f), Offset(c.x - h * 0.035f, c.y - h * 0.01f), Size(h * 0.07f, h * 0.02f), style = Stroke(1f * u))
            }
            for (j in 0 until 7) {
                val c = Offset(x0 + (w - x0) * azar(6561, j), g.atras + (g.canto - g.atras) * azar(6562, j))
                drawLine(g.luz.t(c).b(0.3f), c, c + Offset(h * (0.03f + 0.05f * azar(6563, j)), h * 0.006f * (azar(6564, j) - 0.5f)), 0.6f * u)
            }
        }
        // El canto enrollado: brillo arriba, sombra abajo.
        val e0 = g.canto; val e1 = g.frente
        tiraAcero(g, x0, w, e0, e1, 1f, P.ACERO_OSC)
        tiraAcero(g, x0, w, e0 + (e1 - e0) * 0.18f, e0 + (e1 - e0) * 0.42f, 1.3f, P.ACERO_LUZ)
        drawRect(g.luz.t(w * 0.5f, e1).s(0.45f), Offset(x0, e1 - 1.4f * u), Size(w - x0, 1.4f * u))
        // El frente: dos puertas de acero cepillado a lo alto, con el tirador hundido, y su junta.
        tiraAcero(g, x0, w, e1, h, 0.75f, P.ACERO_OSC)
        drawRect(Q.SOMBRA.copy(alpha = 0.3f), Offset(x0, e1), Size(w - x0, h - e1))
        clipRect(x0, e1, w, h) {
            var x = x0 + 2f * u
            while (x < w) { drawLine(g.luz.t(x, e1 + h * 0.08f).b(0.1f), Offset(x, e1), Offset(x, h), 0.5f * u); x += 3.4f * u * (0.7f + 0.6f * azar(6571, (x / u).toInt())) }
            val medio = x0 + (w - x0) * 0.5f
            listOf(x0 + h * 0.04f, medio + h * 0.04f).forEach { px ->
                val t = g.luz.t(px, e1 + h * 0.06f)
                drawRoundRect(Q.SOMBRA.copy(alpha = 0.7f), Offset(px, e1 + h * 0.045f), Size(h * 0.16f, h * 0.022f), CornerRadius(2f * u))
                drawRect(t.b(0.35f), Offset(px, e1 + h * 0.045f + h * 0.022f), Size(h * 0.16f, 0.8f * u))
            }
            drawLine(Q.SOMBRA.copy(alpha = 0.7f), Offset(medio, e1), Offset(medio, h), 1.2f * u)
            drawLine(g.luz.t(medio, e1).b(0.3f), Offset(medio + 1.2f * u, e1), Offset(medio + 1.2f * u, h), 0.6f * u)
        }
    }

    /** El trapo de cocina colgado del canto, entre el pincho y la lámpara: verde petróleo con dos franjas crudas, doblado. */
    private fun DrawScope.trapo(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val x = (g.xPincho + g.lampara.x) / 2 - h * 0.02f
        val an = h * 0.15f
        val t = g.luz.t(x + an / 2, g.frente + h * 0.05f)
        val tela = t.c(Q.PETROLEO_LUZ, 1.1f)
        // Lo que queda sobre la encimera y lo que cuelga por delante, más largo de un lado.
        val arriba = Rect(x, g.canto - h * 0.03f, x + an, g.canto + h * 0.004f)
        drawRect(t.s(0.4f), Offset(arriba.left + 2f * u, arriba.top + 2f * u), arriba.size)
        drawRect(mezcla(tela, Q.SOMBRA, 0.12f), arriba.topLeft, arriba.size)
        val cuelga = Path().apply {
            moveTo(x, g.canto); lineTo(x + an, g.canto); lineTo(x + an + h * 0.006f, h * 0.97f)
            quadraticTo(x + an * 0.5f, h * 0.99f, x - h * 0.004f, h * 0.93f); close()
        }
        drawPath(Path().apply { addPath(cuelga, Offset(3f * u, 2f * u)) }, t.s(0.45f))
        drawPath(cuelga, tela)
        clipPath(cuelga) {
            listOf(0.68f, 0.76f).forEach { fy -> drawRect(t.c(Q.HUESO, 0.8f), Offset(x - 2f * u, g.canto + (h - g.canto) * fy), Size(an + 4f * u, h * 0.012f)) }
            drawRect(t.s(0.3f), Offset(x + an * 0.62f, g.canto), Size(an * 0.38f, h))
            drawLine(t.s(0.35f), Offset(x + an * 0.3f, g.canto + h * 0.02f), Offset(x + an * 0.36f, h * 0.95f), 1f * u)
            drawRect(t.b(0.4f), Offset(x, g.canto), Size(an, 1.2f * u))
        }
    }

    // ---------------------------------------------------------------- la nevera

    /**
     * La nevera de bebidas, de pie a la izquierda y cortada por el borde: el cuerpo verde petróleo; tras el vidrio de la puerta,
     * el tubo que la alumbra arriba, las rejillas con las botellas y las latas (de color, sin etiqueta), el vaho abajo y el reflejo;
     * el tirador cromado; la rejilla del motor abajo. Su luz fría sale por la derecha.
     */
    private fun DrawScope.nevera(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val x1 = g.xNevera
        val cuerpo = Rect(-h * 0.02f, -h * 0.02f, x1, g.h)
        val tc = g.luz.t(x1, h * 0.4f)
        drawRect(tc.c(Q.PETROLEO, 0.9f), cuerpo.topLeft, cuerpo.size)
        drawRect(tc.b(0.45f), Offset(x1 - 2f * u, 0f), Size(2f * u, g.h))
        drawRect(Q.SOMBRA.copy(alpha = 0.45f), Offset(x1, 0f), Size(3f * u, g.h))
        val vidrio = Rect(0f, h * 0.035f, x1 - h * 0.035f, h * 0.83f)
        // Dentro: el fondo frío, encendido arriba por el tubo.
        drawRect(Brush.verticalGradient(listOf(Color(0xFF7FD8EE), Color(0xFF2A6C84), Color(0xFF0E2C3A)), vidrio.top, vidrio.bottom), vidrio.topLeft, vidrio.size)
        clipRect(vidrio.left, vidrio.top, vidrio.right, vidrio.bottom) {
            drawRect(Color(0xFFEFFCFF), Offset(vidrio.left, vidrio.top + h * 0.01f), Size(vidrio.width, h * 0.012f))
            mancha(Offset(vidrio.center.x, vidrio.top + h * 0.02f), vidrio.width * 0.9f, Color.White.copy(alpha = 0.35f))
            val baldas = listOf(0.3f, 0.53f, 0.76f)
            baldas.forEachIndexed { b, fy ->
                val y = vidrio.top + vidrio.height * fy
                var x = vidrio.left + 1.5f * u; var k = 0
                val fondo = 1f - fy * 0.7f
                while (x < vidrio.right) {
                    val s = b * 31 + k
                    val lata = azar(s, 1) > 0.55f
                    val col = listOf(Color(0xFF3E8A4A), Color(0xFFB8682A), Color(0xFFD8E4EA), Color(0xFFC83A3A), Color(0xFF3A6AC8), Color(0xFFE8C83A))[(azar(s, 2) * 6).toInt().coerceIn(0, 5)]
                    val bw = if (lata) h * 0.042f else h * 0.038f
                    val alto = if (lata) h * 0.075f else h * (0.13f + 0.03f * azar(s, 3))
                    val c = mezcla(col, Color(0xFF0E2C3A), 0.35f + 0.3f * (1f - fondo))
                    if (lata) {
                        drawRect(c, Offset(x, y - alto), Size(bw, alto))
                        drawRect(mezcla(c, Color.White, 0.35f), Offset(x, y - alto), Size(bw, 1.4f * u))
                        drawRect(Color.White.copy(alpha = 0.3f * fondo), Offset(x + bw * 0.2f, y - alto), Size(bw * 0.12f, alto))
                    } else {
                        val botella = Path().apply {
                            moveTo(x, y); lineTo(x, y - alto * 0.62f); quadraticTo(x, y - alto * 0.76f, x + bw * 0.36f, y - alto * 0.8f)
                            lineTo(x + bw * 0.36f, y - alto); lineTo(x + bw * 0.64f, y - alto); lineTo(x + bw * 0.64f, y - alto * 0.8f)
                            quadraticTo(x + bw, y - alto * 0.76f, x + bw, y - alto * 0.62f); lineTo(x + bw, y); close()
                        }
                        drawPath(botella, c.copy(alpha = 0.92f))
                        drawRect(mezcla(c, Color.White, 0.25f).copy(alpha = 0.7f), Offset(x, y - alto * 0.45f), Size(bw, alto * 0.18f))
                        drawRect(Color.White.copy(alpha = 0.35f * fondo), Offset(x + bw * 0.18f, y - alto * 0.6f), Size(bw * 0.12f, alto * 0.5f))
                        drawRect(mezcla(c, Color.Black, 0.3f), Offset(x + bw * 0.34f, y - alto - 1.5f * u), Size(bw * 0.32f, 2f * u))
                    }
                    x += bw + h * 0.008f; k++
                }
                // La balda de rejilla, con su canto encendido.
                drawRect(Color(0xFFB8E8F4).copy(alpha = 0.8f), Offset(vidrio.left, y), Size(vidrio.width, 1.4f * u))
                drawRect(Color(0xFF0A1A22).copy(alpha = 0.5f), Offset(vidrio.left, y + 1.4f * u), Size(vidrio.width, 2.4f * u))
            }
            // El vaho de abajo y el reflejo en diagonal.
            for (j in 0 until 24) {
                val p = Offset(vidrio.left + vidrio.width * azar(6581, j), vidrio.bottom - vidrio.height * 0.2f * azar(6582, j))
                drawCircle(Color.White.copy(alpha = 0.18f), (0.8f + 1.4f * azar(6583, j)) * u, p)
            }
            drawRect(Color.White.copy(alpha = 0.12f), Offset(vidrio.left, vidrio.bottom - vidrio.height * 0.14f), Size(vidrio.width, vidrio.height * 0.14f))
            drawPath(poligonoNeon(vidrio.left + vidrio.width * 0.5f, vidrio.top, vidrio.left + vidrio.width * 0.75f, vidrio.top, vidrio.left + vidrio.width * 0.25f, vidrio.bottom, vidrio.left, vidrio.bottom), Color.White.copy(alpha = 0.07f))
        }
        // El marco de la puerta, el burlete y el tirador cromado.
        drawRect(tc.c(Q.PETROLEO, 1f), Offset(vidrio.right, vidrio.top), Size(x1 - vidrio.right, vidrio.height))
        drawRect(Q.SOMBRA.copy(alpha = 0.6f), Offset(vidrio.left, vidrio.bottom), Size(x1 - vidrio.left, 2f * u))
        val tir = Rect(vidrio.right + h * 0.008f, h * 0.3f, vidrio.right + h * 0.022f, h * 0.6f)
        drawRoundRect(Q.SOMBRA.copy(alpha = 0.5f), tir.topLeft + Offset(1.6f * u, 1.6f * u), tir.size, CornerRadius(2f * u))
        drawRoundRect(tc.c(P.ACERO_LUZ, 1.2f), tir.topLeft, tir.size, CornerRadius(2f * u))
        drawRect(tc.b(0.8f, 1.3f), Offset(tir.right - 1.4f * u, tir.top + 2f * u), Size(1f * u, tir.height - 4f * u))
        // La rejilla del motor, abajo.
        val rj = Rect(0f, h * 0.86f, x1 - h * 0.02f, h)
        drawRect(tc.c(Color(0xFF12191E)), rj.topLeft, rj.size)
        var y = rj.top + 2f * u
        while (y < rj.bottom) { drawRect(tc.c(Q.PETROLEO_LUZ, 0.7f), Offset(rj.left, y), Size(rj.width, 1.2f * u)); y += 3.6f * u }
    }

    // ---------------------------------------------------------------- lo de la encimera

    /** El bote de lata (una de tomate, sin etiqueta, con sus aros) con los rotuladores fluorescentes de marcar los pedidos. */
    private fun DrawScope.bote(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val cx = g.xNevera + h * 0.085f
        val pie = g.atras + (g.canto - g.atras) * 0.55f
        val r = Rect(cx - h * 0.04f, pie - h * 0.1f, cx + h * 0.04f, pie)
        val t = g.luz.t(r.center)
        drawOval(t.s(0.45f), Offset(r.left - h * 0.005f, pie - h * 0.012f), Size(r.width * 1.5f, h * 0.024f))
        // Los rotuladores: el cuerpo gris y el capuchón de su color, inclinados.
        listOf(Triple(-0.5f, Color(0xFFFF4FA0), 0.15f), Triple(-0.15f, Color(0xFFE8E040), 0.2f), Triple(0.2f, Color(0xFF4FE07A), 0.13f), Triple(0.55f, Color(0xFF4FD8F0), 0.17f)).forEach { (dx, col, alto) ->
            val b = Offset(cx + dx * h * 0.045f, r.top + h * 0.01f)
            val p = Offset(b.x + dx * h * 0.03f, r.top - h * alto * 0.75f)
            drawLine(t.c(Color(0xFF3A3A44)), b, p, h * 0.016f)
            val q = Offset(p.x + (p.x - b.x) * 0.3f, p.y + (p.y - b.y) * 0.3f)
            drawLine(t.c(col, 1.2f), p, q, h * 0.018f, StrokeCap.Round)
            drawLine(t.b(0.5f), Offset(p.x - h * 0.006f, p.y), Offset(q.x - h * 0.006f, q.y), 0.8f * u)
        }
        // La lata: el cilindro con sus aros, el borde de arriba y la luz de la nevera de un lado.
        drawRect(t.c(Q.ALUMINIO, 0.95f), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            planosNeon(r, t, 1.1f)
            for (j in 1 until 5) drawLine(t.s(0.3f), Offset(r.left, r.top + r.height * j / 5f), Offset(r.right, r.top + r.height * j / 5f), 0.8f * u)
            drawRect(Color(0xFF8A4A26).copy(alpha = 0.4f), Offset(r.left, r.bottom - r.height * 0.15f), Size(r.width, r.height * 0.15f))
        }
        drawOval(t.c(Q.ALUMINIO, 1.2f), Offset(r.left, r.top - h * 0.008f), Size(r.width, h * 0.016f), style = Stroke(1.2f * u))
    }

    /** El timbre de mostrador: la base negra, la campana cromada con la lámpara reflejada en ella y el botón de arriba. */
    private fun DrawScope.timbre(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val cx = g.xTimbre
        val pie = g.atras + (g.canto - g.atras) * 0.62f
        val t = g.luz.t(cx, pie - h * 0.04f)
        val rb = h * 0.075f
        drawOval(t.s(0.5f), Offset(cx - rb * 1.5f, pie - h * 0.012f), Size(rb * 2.6f, h * 0.03f))
        drawOval(t.c(Color(0xFF1C1A22), 1.1f), Offset(cx - rb, pie - h * 0.02f), Size(rb * 2, h * 0.028f))
        drawRect(t.c(Color(0xFF1C1A22), 1.1f), Offset(cx - rb, pie - h * 0.03f), Size(rb * 2, h * 0.018f))
        drawLine(t.b(0.4f), Offset(cx - rb, pie - h * 0.03f), Offset(cx + rb, pie - h * 0.03f), 0.8f * u)
        val dome = Rect(cx - rb * 0.86f, pie - h * 0.125f, cx + rb * 0.86f, pie - h * 0.022f)
        val campana = Path().apply {
            moveTo(dome.left, dome.bottom)
            cubicTo(dome.left, dome.top + dome.height * 0.1f, dome.right, dome.top + dome.height * 0.1f, dome.right, dome.bottom); close()
        }
        drawPath(campana, mezcla(P.ACERO_LUZ, t.c(P.ACERO_LUZ, 1.3f), 0.6f))
        clipPath(campana) {
            drawRect(Q.SOMBRA.copy(alpha = 0.35f), Offset(dome.left, dome.top), Size(dome.width * 0.3f, dome.height))
            drawRect(P.FRIO.copy(alpha = 0.25f), Offset(dome.left, dome.top), Size(dome.width * 0.14f, dome.height))
            // La lámpara de calor reflejada: una mancha roja y su alma, arriba a la derecha.
            drawOval(P.CALOR.copy(alpha = 0.75f), Offset(dome.left + dome.width * 0.56f, dome.top + dome.height * 0.12f), Size(dome.width * 0.26f, dome.height * 0.34f))
            drawOval(P.CALOR_ALMA.copy(alpha = 0.9f), Offset(dome.left + dome.width * 0.62f, dome.top + dome.height * 0.18f), Size(dome.width * 0.1f, dome.height * 0.14f))
            drawRect(Color.White.copy(alpha = 0.25f), Offset(dome.left + dome.width * 0.4f, dome.top), Size(dome.width * 0.05f, dome.height))
            drawRect(Q.SOMBRA.copy(alpha = 0.3f), Offset(dome.left, dome.bottom - dome.height * 0.14f), Size(dome.width, dome.height * 0.14f))
        }
        // El botón: el vástago y la cabeza.
        val bt = Offset(cx, dome.top + dome.height * 0.06f)
        drawLine(t.c(Q.ALUMINIO), bt, bt + Offset(0f, -h * 0.025f), 1.6f * u)
        drawOval(t.c(Color(0xFF1C1A22), 1.2f), Offset(bt.x - h * 0.016f, bt.y - h * 0.034f), Size(h * 0.032f, h * 0.014f))
        drawOval(t.b(0.6f), Offset(bt.x - h * 0.012f, bt.y - h * 0.034f), Size(h * 0.018f, h * 0.006f))
    }

    /**
     * La bolsa de papel kraft de un pedido que espera al repartidor, cortada por la derecha: el fuelle del costado, la boca
     * abierta con su borde dentado y el fondo de dentro en sombra, las dos asas de papel retorcido, la tapa blanca del envase que
     * asoma, el ticket del pedido grapado delante (renglones, sin letra) y las arrugas.
     */
    private fun DrawScope.bolsa(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val cx = g.xBolsa
        val pie = g.atras + (g.canto - g.atras) * 0.7f
        val an = h * 0.27f
        val r = Rect(cx - an / 2, pie - h * 0.27f, cx + an / 2, pie)
        val t = g.luz.t(r.left + an * 0.2f, r.center.y)
        drawOval(t.s(0.45f), Offset(r.left - h * 0.06f, pie - h * 0.014f), Size(an * 1.2f, h * 0.03f))
        val papel = mezcla(KRAFT, t.c(KRAFT, 1.1f), 0.7f)
        // Las asas de detrás (la de la cara de atrás de la bolsa), la tapa del envase y el fondo de dentro.
        val asaAtras = t.c(mezcla(KRAFT, Q.SOMBRA, 0.3f))
        drawArc(asaAtras, 200f, 140f, false, Offset(r.left + an * 0.22f, r.top - h * 0.13f), Size(an * 0.6f, h * 0.2f), style = Stroke(2.4f * u))
        drawRect(mezcla(papel, Q.SOMBRA, 0.55f), Offset(r.left, r.top - h * 0.018f), Size(an, h * 0.03f))
        val tapa = Rect(r.left + an * 0.18f, r.top - h * 0.045f, r.left + an * 0.78f, r.top + h * 0.004f)
        drawRoundRect(t.c(P.PAPEL, 1.1f), tapa.topLeft, tapa.size, androidx.compose.ui.geometry.CornerRadius(2.4f * u))
        drawRect(t.b(0.5f), Offset(tapa.left + 2f * u, tapa.top), Size(tapa.width - 4f * u, 1f * u))
        drawRect(t.s(0.3f), Offset(tapa.left, tapa.bottom - h * 0.012f), Size(tapa.width, h * 0.012f))
        // El cuerpo, con la boca dentada.
        val cuerpo = Path().apply {
            moveTo(r.left, r.bottom); lineTo(r.left, r.top)
            var x = r.left; var arriba = true
            while (x < r.right) { x += 3f * u; lineTo(minOf(x, r.right), if (arriba) r.top - 1.6f * u else r.top); arriba = !arriba }
            lineTo(r.right, r.bottom); close()
        }
        drawPath(cuerpo, papel)
        clipPath(cuerpo) {
            // El fuelle del costado izquierdo (de cara a la lámpara, con su pliegue en V) y las arrugas.
            val fu = an * 0.16f
            drawRect(mezcla(papel, Q.SOMBRA, 0.25f), Offset(r.left, r.top - 2f * u), Size(fu, r.height + 2f * u))
            drawPath(poligonoNeon(r.left, r.top, r.left + fu, r.top, r.left + fu * 0.5f, r.top + h * 0.05f), mezcla(papel, Q.SOMBRA, 0.45f))
            drawLine(t.s(0.4f), Offset(r.left + fu * 0.5f, r.top + h * 0.05f), Offset(r.left + fu * 0.5f, r.bottom - h * 0.03f), 1f * u)
            drawLine(t.b(0.45f), Offset(r.left + 0.8f * u, r.top), Offset(r.left + 0.8f * u, r.bottom), 0.8f * u)
            for (j in 0 until 3) {
                val y = r.top + r.height * (0.3f + 0.2f * j)
                drawLine(t.s(0.18f), Offset(r.left + fu, y), Offset(r.right, y + h * 0.01f * (azar(6591, j) - 0.5f)), 0.8f * u)
            }
            drawRect(Q.SOMBRA.copy(alpha = 0.3f), Offset(r.left + an * 0.55f, r.top - 2f * u), Size(an * 0.45f, r.height + 2f * u))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - h * 0.03f), Size(an, h * 0.03f))
        }
        // El asa de delante, de papel retorcido, pegada por dentro de la boca.
        val asa = Rect(r.left + an * 0.2f, r.top - h * 0.12f, r.left + an * 0.82f, r.top + h * 0.07f)
        drawArc(t.s(0.35f), 195f, 150f, false, asa.topLeft + Offset(1.4f * u, 1.6f * u), asa.size, style = Stroke(2.8f * u))
        drawArc(mezcla(KRAFT, t.c(KRAFT, 1.2f), 0.6f), 195f, 150f, false, asa.topLeft, asa.size, style = Stroke(2.8f * u))
        drawArc(t.b(0.5f), 215f, 70f, false, asa.topLeft + Offset(0f, -0.8f * u), asa.size, style = Stroke(0.7f * u))
        // El ticket del pedido, grapado delante.
        val tk = Rect(r.left + an * 0.24f, r.top + h * 0.05f, r.left + an * 0.62f, r.top + h * 0.18f)
        drawRect(t.s(0.35f), tk.topLeft + Offset(1.4f * u, 1.6f * u), tk.size)
        drawRect(t.c(P.PAPEL, 1.1f), tk.topLeft, tk.size)
        for (j in 0 until 4) drawLine(P.IMPRESO.copy(alpha = 0.4f), Offset(tk.left + 3f * u, tk.top + h * (0.03f + 0.025f * j)), Offset(tk.left + tk.width * (0.5f + 0.4f * azar(6595, j)), tk.top + h * (0.03f + 0.025f * j)), 0.8f * u)
        drawLine(t.c(Q.ALUMINIO, 1.3f), Offset(tk.left + tk.width * 0.3f, tk.top + 1.5f * u), Offset(tk.left + tk.width * 0.7f, tk.top + 1.5f * u), 1.1f * u)
    }

    // ---------------------------------------------------------------- la lámpara de calor

    /**
     * La lámpara de calor del pasaplatos, colgada de la tubería por su cadena: la pantalla cónica de aluminio (encendida por dentro
     * en el borde), el portalámparas, la bombilla roja que asoma con su alma clara y el resplandor alrededor (de la pared y de lo
     * que tiene debajo; lo demás lo pinta la luz de cada cosa).
     */
    private fun DrawScope.lampara(g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val b = g.lampara
        // El resplandor, hasta donde se ve.
        mancha(b, h * 0.6f, P.CALOR.copy(alpha = 0.28f))
        mancha(b + Offset(0f, h * 0.03f), h * 0.2f, P.CALOR_ALMA.copy(alpha = 0.3f))
        val arriba = h * 0.105f
        val cuello = b.y - h * 0.165f
        // La cadena, de eslabones.
        var y = arriba + h * 0.018f; var k = 0
        val acero = g.luz.t(b.x, cuello).c(P.ACERO, 1.1f)
        while (y < cuello) {
            if (k % 2 == 0) drawOval(acero, Offset(b.x - 1.6f * u, y), Size(3.2f * u, h * 0.022f), style = Stroke(1f * u))
            else drawLine(acero, Offset(b.x, y), Offset(b.x, y + h * 0.022f), 1.2f * u)
            y += h * 0.02f; k++
        }
        // El portalámparas y la pantalla cónica.
        val t = g.luz.t(b.x, b.y - h * 0.06f)
        drawRect(t.c(Color(0xFF26242C), 1.2f), Offset(b.x - h * 0.026f, cuello), Size(h * 0.052f, h * 0.04f))
        drawRect(t.b(0.4f), Offset(b.x + h * 0.016f, cuello), Size(1.2f * u, h * 0.04f))
        val rb = h * 0.125f; val bordeY = b.y - h * 0.01f
        val pantalla = poligonoNeon(b.x - h * 0.034f, cuello + h * 0.035f, b.x + h * 0.034f, cuello + h * 0.035f, b.x + rb, bordeY, b.x - rb, bordeY)
        drawPath(Path().apply { addPath(pantalla, Offset(-3f * u, 3f * u)) }, t.s(0.35f))
        drawPath(pantalla, t.c(Q.ALUMINIO, 0.85f))
        clipPath(pantalla) {
            // Los planos del cono: el de la izquierda en sombra (la nevera le da un filo frío), el brillo largo, y el rojo que lo
            // enciende desde dentro en el borde.
            drawRect(Q.SOMBRA.copy(alpha = 0.4f), Offset(b.x - rb, cuello), Size(rb * 0.7f, bordeY - cuello))
            drawRect(P.FRIO.copy(alpha = 0.18f), Offset(b.x - rb, cuello), Size(rb * 0.2f, bordeY - cuello))
            drawLine(Color.White.copy(alpha = 0.45f), Offset(b.x + h * 0.012f, cuello + h * 0.04f), Offset(b.x + rb * 0.45f, bordeY - h * 0.01f), 1.4f * u)
            drawRect(P.CALOR.copy(alpha = 0.35f), Offset(b.x - rb, bordeY - h * 0.03f), Size(rb * 2, h * 0.03f))
        }
        // La boca: el borde enrollado y la bombilla roja con su alma.
        drawOval(Color(0xFF7A1A10), Offset(b.x - rb, bordeY - h * 0.016f), Size(rb * 2, h * 0.032f))
        drawOval(P.CALOR, Offset(b.x - rb * 0.62f, bordeY - h * 0.02f), Size(rb * 1.24f, h * 0.06f))
        drawOval(P.CALOR_ALMA, Offset(b.x - rb * 0.3f, bordeY - h * 0.004f), Size(rb * 0.6f, h * 0.03f))
        drawOval(t.c(Q.ALUMINIO, 1.2f), Offset(b.x - rb, bordeY - h * 0.016f), Size(rb * 2, h * 0.032f), style = Stroke(1.4f * u))
        // El cable que sube enroscado a la cadena.
        drawPath(Path().apply {
            moveTo(b.x + h * 0.02f, cuello); cubicTo(b.x + h * 0.05f, cuello - h * 0.04f, b.x - h * 0.03f, arriba + h * 0.06f, b.x + h * 0.01f, arriba + h * 0.02f)
            lineTo(b.x + h * 0.2f, arriba + h * 0.025f)
        }, t.c(Color(0xFF1C1A22), 1.1f), style = Stroke(1.6f * u))
    }
}
