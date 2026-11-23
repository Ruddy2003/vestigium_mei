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
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ANTE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BARRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BRASA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HOJALATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TIZNE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VETA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * El pie del estante de Vapor (ver [MuebleEstanteVapor]), que se pinta en cada dibujo: el suelo de baldosas geométricas en
 * perspectiva (ante y almagre con su taco negro en cada cruce, la cenefa negra junto al zócalo, las juntas, las gastadas y una
 * partida), alumbrado de un solo trazo por baldosa de cada color (de día, la ventana; de noche, el abanico de la estufa: cerca,
 * naranja; lejos, azul noche y granate hondo); el zócalo; la estufa redonda de hierro, grande, con la puerta entreabierta que
 * enseña la brasa, la tetera con su vapor y el caño; la carbonera de casco con su pala; y a la derecha el paragüero de hierro con
 * los planos enrollados y las banderas del guarda, y la lata de aceite cortada por el borde. Todo con su sombra en las baldosas.
 */
internal object PieEstanteVapor {

    fun DrawScope.pieVivoVapor(a: AireVapor, g: MarcoVapor) {
        val w = g.w
        // Debajo, opaco y en píxeles enteros: las franjas del zócalo se tocan a medio píxel y no deben dejar ver el fondo.
        val y0 = floor(g.fondo); val y1 = minOf(g.h, ceil(g.zocalo) + 1f)
        if (y1 > y0) drawRect(a.luz(mezcla(PINO_OSC, VETA, 0.4f), g.cerca(Offset(w * 0.5f, g.zocalo))), Offset(0f, y0), Size(w, y1 - y0))
        suelo(a, g)
        listOf(0f to g.xa, g.xb to w).forEach { (l, r) ->
            val p = Offset((l + r) / 2, (g.fondo + g.zocalo) / 2)
            drawRect(a.luz(AZUL, g.cerca(p), g.frio(p), g.brasa(p)), Offset(l, g.fondo - 1f), Size(r - l, g.zocalo - g.fondo + 1f))
        }
        with(MuebleCuerpoVapor) { zocaloVapor(a, g) }
        if (a.lumbre) {
            val boca = Offset(g.estufa.x * w, g.estufa.y * w)
            val fuego = if (a.oscuro && !a.noche) 1f else 0.6f
            clipRect(0f, g.fondo - w * 0.16f, w * 0.7f, g.zocalo) {
                mancha(boca, w * 0.5f, BRASA.copy(alpha = 0.34f * fuego))
                mancha(boca, w * 0.22f, Color(0xFFFFB050).copy(alpha = 0.22f * fuego))
            }
        }
        estufa(a, g)
        carbonera(a, g)
    }

    // ---------------------------------------------------------------- el suelo

    /** El color de una baldosa de noche, de su color lejano (azul noche o granate hondo) a su color con la luz [k] de la estufa. */
    private fun nocheSuelo(a: AireVapor, c: Color, lejos: Color, k: Float, br: Float): Color =
        mezcla(mezcla(lejos, a.hondo, if (a.noche) 0.3f else 0f), mezcla(c, BRASA, 0.3f * br), (1.15f * k).coerceIn(0f, 0.9f))

    private fun DrawScope.suelo(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val y0 = g.zocalo; val y1 = g.h
        if (y1 - y0 < 2f) return
        val cx = w * 0.5f
        val filas = 7; val q = 1.2f
        val tot = (Math.pow(q.toDouble(), filas.toDouble()) - 1).toFloat()
        val cen = y0 + (y1 - y0) * 0.08f
        fun y(i: Int) = cen + (y1 - cen) * ((Math.pow(q.toDouble(), i.toDouble()) - 1).toFloat() / tot)
        fun esc(yy: Float) = 0.78f + 0.5f * (yy - y0) / (y1 - y0)
        fun xx(col: Float, yy: Float) = cx + (col * w * 0.075f) * esc(yy)
        // Debajo de las baldosas, opaco y en píxeles enteros: sus juntas a medio píxel no deben dejar ver el fondo.
        drawRect(if (a.oscuro) mezcla(Color(0xFF34161A), a.hondo, 0.3f) else a.luz(mezcla(BARRO, ANTE, 0.4f), 0.4f), Offset(0f, floor(y0)), Size(w, ceil(y1) - floor(y0)))
        // Las baldosas, en tres caminos: las de ante, las de almagre y las gastadas.
        val ante = Path(); val barro = Path(); val gastadas = Path(); val rajas = Path()
        for (i in 0 until filas) {
            val ya = y(i); val yb = y(i + 1)
            for (c in -10 until 10) {
                val q0x = xx(c.toFloat(), ya); val q1x = xx(c + 1f, ya); val q2x = xx(c + 1f, yb); val q3x = xx(c.toFloat(), yb)
                if (q2x < -w * 0.1f || q3x > w * 1.1f) continue
                val camino = if ((c + i) % 2 == 0) ante else barro
                camino.moveTo(q0x, ya); camino.lineTo(q1x, ya); camino.lineTo(q2x, yb); camino.lineTo(q3x, yb); camino.close()
                if (azar(3700 + i, c + 20) < 0.18f) gastadas.addOval(Rect((q0x + q3x) / 2 + (q1x - q0x) * 0.2f, ya + (yb - ya) * 0.3f, (q1x + q2x) / 2 - (q1x - q0x) * 0.2f, yb - (yb - ya) * 0.3f))
                if (i == 3 && c == 2) { rajas.moveTo(q0x + (q1x - q0x) * 0.2f, ya); rajas.lineTo(q0x + (q1x - q0x) * 0.55f, (ya + yb) / 2); rajas.lineTo(q3x + (q2x - q3x) * 0.7f, yb) }
            }
        }
        val e = Offset(g.estufa.x * w, g.estufa.y * w + w * 0.1f)
        if (!a.oscuro) {
            // De día, la luz de la ventana entra por la izquierda y se apaga hacia la derecha.
            fun dia(c: Color) = Brush.horizontalGradient(listOf(a.luz(c, 0.75f, g.frio(Offset(0f, y0)), 0f), a.luz(c, 0.45f, g.frio(Offset(cx, y0)), 0f), a.luz(c, 0.2f, 0f, 0f)), 0f, w)
            drawPath(ante, dia(ANTE)); drawPath(barro, dia(BARRO))
        } else {
            // De noche, el abanico de la estufa: un degradado desde su puerta, naranja cerca y azul noche o granate hondo lejos.
            val radios = listOf(0f, 0.1f, 0.22f, 0.38f, 0.6f, 0.95f)
            fun noche(c: Color, lejos: Color) = Brush.radialGradient(*radios.map { r ->
                (r / 0.95f) to nocheSuelo(a, c, lejos, estufaVapor(e.x / w + r, e.y / w, Offset(e.x / w, e.y / w), a.noche), 1f)
            }.toTypedArray(), center = e, radius = w * 0.95f)
            drawPath(ante, noche(ANTE, Color(0xFF1B2236)))
            drawPath(barro, noche(BARRO, Color(0xFF34161A)))
        }
        drawPath(gastadas, luzMueble(if (a.oscuro) 0.04f else 0.12f))
        // La cenefa negra junto al zócalo y su filete de ante.
        drawRect(a.luz(BARRO_OSC, g.cerca(Offset(cx, y0)), 0f, g.brasa(Offset(w * 0.2f, y0))), Offset(0f, y0), Size(w, cen - y0))
        drawRect(a.luz(ANTE, g.cerca(Offset(cx, cen)), 0f, g.brasa(Offset(w * 0.2f, cen))), Offset(0f, cen - 1.2f * u), Size(w, 1.2f * u))
        // Las juntas y el taco negro de cada cruce.
        val juntas = Path(); val tacos = Path()
        for (i in 0..filas) { val yy = y(i); juntas.moveTo(0f, yy); juntas.lineTo(w, yy) }
        for (c in -10..10) {
            juntas.moveTo(xx(c.toFloat(), cen), cen); juntas.lineTo(xx(c.toFloat(), y1), y1)
            for (i in 0..filas) {
                val yy = y(i); val xc = xx(c.toFloat(), yy)
                val s = w * 0.0095f * esc(yy); val sy = s * (0.45f + 0.35f * (yy - y0) / (y1 - y0))
                if (xc > -s && xc < w + s) { tacos.moveTo(xc, yy - sy); tacos.lineTo(xc + s, yy); tacos.lineTo(xc, yy + sy); tacos.lineTo(xc - s, yy); tacos.close() }
            }
        }
        drawPath(juntas, a.sombra(0.35f), style = Stroke(0.9f * u))
        drawPath(rajas, a.sombra(0.5f), style = Stroke(0.8f * u))
        drawPath(tacos, if (a.oscuro) a.hondo else a.luz(BARRO_OSC, 0.5f))
        // La sombra del mueble pegada al zócalo y, de día, la penumbra de la derecha; de noche, lo bajo se hunde hacia el borde.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.5f), Color.Transparent), y0, y0 + w * 0.04f), Offset(g.xa, y0), Size(g.xb - g.xa, w * 0.04f))
        if (a.oscuro) drawRect(Brush.verticalGradient(listOf(Color.Transparent, a.hondo.copy(alpha = 0.45f)), y0, y1), Offset(0f, y0), Size(w, y1 - y0))
        // La ceniza y el carbón que caen junto a la estufa.
        val ceniza = Path(); val carbon = Path()
        for (j in 0 until 18) {
            val p = Offset(w * (0.17f + 0.22f * azar(3711, j)), y0 + (y1 - y0) * (0.5f + 0.45f * azar(3712, j)))
            val r = (0.8f + 1.4f * azar(3713, j)) * u
            (if (j % 3 == 0) ceniza else carbon).addOval(Rect(p.x - r, p.y - r * 0.7f, p.x + r, p.y + r * 0.7f))
        }
        drawPath(ceniza, a.luz(Color(0xFF8C8A84), g.cerca(Offset(w * 0.25f, y1)), 0f, 0.8f))
        drawPath(carbon, a.luz(NEGRO, 0.3f))
    }

    // ---------------------------------------------------------------- la estufa y la carbonera

    /**
     * La estufa de hierro fundido, redonda y panzona, grande en la esquina (cortada apenas por el borde): las patas, el cuerpo
     * con sus aros, la puerta entreabierta (la hoja con sus micas, girada sobre su bisagra) que deja ver la brasa, el cenicero con su
     * tiro, la tapa abombada con su pomo, el caño que sale hacia la izquierda y la tetera de cobre con su vapor.
     */
    private fun DrawScope.estufa(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val boca = Offset(g.estufa.x * w, g.estufa.y * w)
        val cx = boca.x - w * 0.01f
        val suelo = g.h - w * 0.016f
        val r = w * 0.125f
        val y0 = suelo - w * 0.03f; val y1 = y0 - w * 0.17f
        val k = if (a.oscuro) 0.5f else g.cerca(boca)
        val f = g.frio(boca)
        val fuego = when { a.oscuro && a.noche -> 0.7f; a.oscuro -> 1f; a.noche -> 0.75f; else -> 0.45f }
        // La sombra en el suelo y, de noche, el abanico de la brasa que sale por la puerta.
        drawOval(a.sombra(0.5f), Offset(cx - r * 0.7f, suelo - w * 0.016f), Size(r * 2.5f, w * 0.04f))
        if (a.lumbre) {
            val abanico = Path().apply {
                moveTo(boca.x - w * 0.03f, y0); lineTo(boca.x + w * 0.04f, y0)
                lineTo(boca.x + w * 0.55f, g.h); lineTo(boca.x - w * 0.12f, g.h); close()
            }
            drawPath(abanico, Brush.radialGradient(listOf(BRASA.copy(alpha = 0.42f * fuego), BRASA.copy(alpha = 0.16f * fuego), Color.Transparent), Offset(boca.x, y0), w * 0.55f))
        }
        // Las patas.
        listOf(-0.72f, 0.1f, 0.72f).forEach { s ->
            val px = cx + r * s
            val d = if (s < -0.2f) -1f else if (s > 0.2f) 1f else 0f
            drawPath(poligonoVapor(px - w * 0.011f, y0, px + w * 0.011f, y0, px + w * 0.014f * d + w * 0.006f, suelo, px + w * 0.014f * d - w * 0.006f, suelo), a.luz(HIERRO, k, f))
        }
        // El cuerpo redondo, un poco panzón.
        val cuerpo = Rect(cx - r, y1, cx + r, y0)
        val barril = Path().apply {
            moveTo(cx - r, y1); quadraticTo(cx - r * 1.1f, (y0 + y1) / 2, cx - r, y0); lineTo(cx + r, y0)
            quadraticTo(cx + r * 1.1f, (y0 + y1) / 2, cx + r, y1); close()
        }
        val hierro = a.luz(HIERRO, k, f)
        val claro = if (a.oscuro) a.luz(mezcla(HIERRO, BRASA, 0.4f), 0.65f) else a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.8f), k, f)
        val osc = a.luz(mezcla(HIERRO, NEGRO, 0.6f), k, f)
        drawPath(barril, Brush.horizontalGradient(if (a.oscuro) listOf(osc, hierro, claro, hierro, osc) else listOf(osc, claro, hierro, osc, osc), cx - r * 1.1f, cx + r * 1.1f))
        if (a.lumbre) clipPath(barril) { drawRect(Brush.verticalGradient(listOf(Color.Transparent, BRASA.copy(alpha = 0.3f * fuego)), y1, y0), cuerpo.topLeft, cuerpo.size) }
        listOf(0.04f, 0.5f, 0.96f).forEach { fy ->
            val y = cuerpo.top + cuerpo.height * fy
            val aro = Rect(cx - r * 1.08f, y - w * 0.013f, cx + r * 1.08f, y + w * 0.013f)
            drawArc(a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.35f), k, f), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(w * 0.01f))
            drawArc(a.brillo(0.35f, maxOf(k, 0.4f), if (a.oscuro) 0.8f else 0f), 30f, 80f, false, aro.topLeft - Offset(0f, w * 0.003f), aro.size, style = Stroke(0.8f * u))
        }
        // La boca de la estufa, con la brasa dentro.
        val bo = Rect(boca.x - w * 0.042f, boca.y - w * 0.038f, boca.x + w * 0.042f, boca.y + w * 0.038f)
        drawRect(a.luz(NEGRO, k, f), bo.inflate(w * 0.007f).topLeft, bo.inflate(w * 0.007f).size)
        drawRect(Color(0xFF1A0C08), bo.topLeft, bo.size)
        drawRect(Brush.verticalGradient(listOf(Color(0xFF5A1C0C), mezcla(BRASA, Color(0xFFFFB050), 0.3f * fuego), Color(0xFFFFD27A).copy(alpha = fuego)), bo.top + bo.height * 0.3f, bo.bottom),
            Offset(bo.left, bo.top + bo.height * 0.3f), Size(bo.width, bo.height * 0.7f))
        val ascuas = Path()
        for (j in 0 until 9) {
            val ax = bo.left + bo.width * (0.08f + 0.84f * azar(3720, j)); val ay = bo.top + bo.height * (0.62f + 0.3f * azar(3721, j))
            val ar = w * (0.006f + 0.006f * azar(3722, j))
            ascuas.addOval(Rect(ax - ar, ay - ar * 0.7f, ax + ar, ay + ar * 0.7f))
        }
        drawPath(ascuas, Color(0xFFFFF0C0).copy(alpha = 0.55f + 0.45f * fuego))
        drawPath(ascuas, Color(0xFF7A2410).copy(alpha = 0.6f), style = Stroke(0.6f * u))
        // La hoja de la puerta, abierta hacia la izquierda sobre su bisagra, con sus micas (que aún brillan un poco).
        val hoja = poligonoVapor(bo.left - w * 0.007f, bo.top - w * 0.007f, bo.left - w * 0.05f, bo.top + w * 0.004f, bo.left - w * 0.05f, bo.bottom + w * 0.002f, bo.left - w * 0.007f, bo.bottom + w * 0.007f)
        drawPath(corridoVapor(hoja, -w * 0.006f, w * 0.006f), a.sombra(0.4f))
        drawPath(hoja, a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.25f), k, f))
        val mica = poligonoVapor(bo.left - w * 0.014f, bo.top + w * 0.002f, bo.left - w * 0.043f, bo.top + w * 0.01f, bo.left - w * 0.043f, bo.bottom - w * 0.004f, bo.left - w * 0.014f, bo.bottom)
        drawPath(mica, mezcla(Color(0xFF4A2412), Color(0xFFE08A3C), 0.6f * fuego))
        drawLine(a.luz(NEGRO, k, f), Offset(bo.left - w * 0.028f, bo.top + w * 0.006f), Offset(bo.left - w * 0.028f, bo.bottom - w * 0.002f), w * 0.004f)
        drawLine(a.luz(NEGRO, k, f), Offset(bo.left - w * 0.014f, boca.y), Offset(bo.left - w * 0.043f, boca.y + w * 0.004f), w * 0.004f)
        listOf(bo.top + w * 0.006f, bo.bottom - w * 0.006f).forEach { yy -> drawCircle(a.luz(LATON, k, f, 0.8f), w * 0.004f, Offset(bo.left - w * 0.004f, yy)) }
        // La luz de la brasa en lo de alrededor de la boca.
        if (a.lumbre) mancha(Offset(boca.x, bo.bottom), w * 0.12f, Color(0xFFFFB050).copy(alpha = 0.3f * fuego))
        // El cenicero con su tiro de ventanillas.
        val tiro = Offset(boca.x + w * 0.01f, y0 - w * 0.018f)
        drawCircle(a.luz(HIERRO_LUZ, k, f), w * 0.012f, tiro)
        for (j in 0 until 6) { val t = j * Math.PI / 3; drawCircle(a.luz(NEGRO, k, f), w * 0.0022f, Offset(tiro.x + w * 0.0075f * cos(t).toFloat(), tiro.y + w * 0.0075f * sin(t).toFloat())) }
        // La tapa abombada con su pomo, y el caño que sale hacia la izquierda.
        val tapa = Path().apply {
            moveTo(cx - r * 1.05f, y1); quadraticTo(cx - r * 0.9f, y1 - w * 0.045f, cx, y1 - w * 0.048f); quadraticTo(cx + r * 0.9f, y1 - w * 0.045f, cx + r * 1.05f, y1); close()
        }
        drawPath(tapa, a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.15f), k, f))
        drawPath(tapa, a.brillo(0.3f, maxOf(k, 0.3f)), style = Stroke(0.8f * u))
        val canio = Rect(-w * 0.01f, y1 - w * 0.085f, cx - r * 0.3f, y1 - w * 0.062f)
        drawRect(a.luz(HIERRO, k, f), Offset(cx - r * 0.48f, canio.top), Size(w * 0.024f, y1 - w * 0.025f - canio.top))
        drawRect(a.luz(HIERRO, k, f), canio.topLeft, canio.size)
        drawRect(a.brillo(0.3f, maxOf(k, 0.3f)), canio.topLeft, Size(canio.width, 0.8f * u))
        // La tetera de cobre sobre la tapa, con su pico, su asa y el vapor que sube.
        val tc = Offset(cx + r * 0.32f, y1 - w * 0.036f)
        val kt = if (a.oscuro) 0.75f else k
        val tet = Path().apply {
            moveTo(tc.x - w * 0.042f, tc.y); quadraticTo(tc.x - w * 0.044f, tc.y - w * 0.042f, tc.x, tc.y - w * 0.045f)
            quadraticTo(tc.x + w * 0.044f, tc.y - w * 0.042f, tc.x + w * 0.042f, tc.y); close()
        }
        val pico = Offset(tc.x + w * 0.068f, tc.y - w * 0.04f)
        drawLine(a.luz(COBRE, kt, f, 0.7f), Offset(tc.x + w * 0.034f, tc.y - w * 0.014f), pico, w * 0.009f, StrokeCap.Round)
        drawPath(tet, a.luz(COBRE, kt, f, 0.7f))
        clipPath(tet) {
            drawRect(a.brillo(0.5f, kt, 0.7f), Offset(tc.x - w * 0.028f, tc.y - w * 0.05f), Size(w * 0.01f, w * 0.05f))
            drawRect(a.sombra(0.3f), Offset(tc.x + w * 0.015f, tc.y - w * 0.05f), Size(w * 0.03f, w * 0.05f))
        }
        drawArc(a.luz(HIERRO, kt, f), 180f, 180f, false, Offset(tc.x - w * 0.025f, tc.y - w * 0.072f), Size(w * 0.05f, w * 0.05f), style = Stroke(w * 0.0045f))
        drawCircle(a.luz(COBRE, kt, f, 0.7f), w * 0.006f, Offset(tc.x, tc.y - w * 0.047f))
        val vapor = Path().apply {
            moveTo(pico.x, pico.y); cubicTo(pico.x + w * 0.02f, pico.y - w * 0.02f, pico.x - w * 0.01f, pico.y - w * 0.04f, pico.x + w * 0.012f, pico.y - w * 0.065f)
            moveTo(pico.x + w * 0.006f, pico.y - w * 0.004f); cubicTo(pico.x + w * 0.035f, pico.y - w * 0.03f, pico.x + w * 0.015f, pico.y - w * 0.05f, pico.x + w * 0.04f, pico.y - w * 0.075f)
        }
        val blanco = if (a.oscuro) mezcla(Color(0xFFFFE6C0), a.hondo, 0.35f) else Color(0xFFF4F1EA)
        drawPath(vapor, blanco.copy(alpha = 0.35f), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
        drawPath(vapor, blanco.copy(alpha = 0.45f), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
    }

    /** La carbonera de casco, de hierro pintado de negro con su asa de latón, llena de carbón, con la pala de la estufa metida. */
    private fun DrawScope.carbonera(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val suelo = g.h - w * 0.01f
        val x0 = w * 0.235f; val x1 = w * 0.36f
        val alto = w * 0.085f
        val p = Offset((x0 + x1) / 2, suelo - alto / 2)
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        drawOval(a.sombra(0.45f), Offset(x0 - w * 0.01f, suelo - w * 0.012f), Size(x1 - x0 + w * 0.05f, w * 0.03f))
        // El casco: una panza que sube hacia la boca, inclinada hacia atrás.
        val casco = Path().apply {
            moveTo(x0 + w * 0.008f, suelo); lineTo(x1 - w * 0.01f, suelo)
            quadraticTo(x1 + w * 0.01f, suelo - alto * 0.4f, x1 - w * 0.005f, suelo - alto)
            lineTo(x0 + w * 0.035f, suelo - alto * 0.72f)
            quadraticTo(x0 - w * 0.01f, suelo - alto * 0.4f, x0 + w * 0.008f, suelo); close()
        }
        // El carbón amontonado que asoma por la boca.
        val monton = Path().apply {
            moveTo(x0 + w * 0.035f, suelo - alto * 0.72f); lineTo(x0 + w * 0.05f, suelo - alto * 0.95f); lineTo(x0 + w * 0.075f, suelo - alto * 1.05f)
            lineTo(x1 - w * 0.03f, suelo - alto * 1.12f); lineTo(x1 - w * 0.005f, suelo - alto); close()
        }
        drawPath(monton, a.luz(Color(0xFF16151A), k, f, br))
        for (j in 0 until 6) drawCircle(a.brillo(0.6f, maxOf(k, 0.3f), br), 0.9f * u, Offset(x0 + w * (0.05f + 0.012f * j), suelo - alto * (0.95f + 0.1f * azar(3730, j))))
        drawPath(casco, a.luz(COBRE, k, f, br))
        clipPath(casco) {
            drawRect(a.brillo(if (a.oscuro) 0.45f else 0.35f, maxOf(k, 0.3f), maxOf(br, 0.6f)), Offset(x0 + w * (if (a.oscuro) 0.008f else 0.02f), suelo - alto), Size(w * 0.022f, alto))
            drawRect(a.brillo(0.6f, maxOf(k, 0.3f), maxOf(br, 0.6f)), Offset(x0 + w * (if (a.oscuro) 0.014f else 0.028f), suelo - alto), Size(w * 0.005f, alto))
            drawRect(a.sombra(0.35f), Offset(x1 - w * 0.04f, suelo - alto), Size(w * 0.045f, alto))
            drawRect(a.sombra(0.2f), Offset(x0, suelo - alto * 0.3f), Size(x1 - x0, alto * 0.3f))
            drawRect(a.luz(LATON, k, f, br).copy(alpha = 0.8f), Offset(x0, suelo - alto * 0.22f), Size(x1 - x0, w * 0.004f))
        }
        drawLine(a.luz(LATON, k, f, br), Offset(x0 + w * 0.035f, suelo - alto * 0.72f), Offset(x1 - w * 0.005f, suelo - alto), w * 0.004f)
        // El asa de latón, atrás.
        drawArc(a.luz(LATON, k, f, br), 200f, 140f, false, Offset(x0 + w * 0.03f, suelo - alto * 1.25f), Size(w * 0.06f, w * 0.05f), style = Stroke(w * 0.004f))
        // La pala de la estufa, metida en el carbón: la hoja y el mango con su pomo de latón.
        val hoja = poligonoVapor(x0 + w * 0.06f, suelo - alto * 0.95f, x0 + w * 0.085f, suelo - alto * 1.0f, x0 + w * 0.09f, suelo - alto * 0.78f, x0 + w * 0.065f, suelo - alto * 0.74f)
        drawPath(hoja, a.luz(HIERRO_LUZ, k, f, br))
        val mango0 = Offset(x0 + w * 0.073f, suelo - alto * 0.97f); val mango1 = Offset(x0 + w * 0.04f, suelo - alto * 2.0f)
        drawLine(a.luz(HIERRO, k, f, br), mango0, mango1, w * 0.005f, StrokeCap.Round)
        drawLine(a.brillo(0.4f, maxOf(k, 0.3f), br), mango0 - Offset(w * 0.002f, 0f), mango1 - Offset(w * 0.002f, 0f), 0.6f * u)
        drawCircle(a.luz(LATON, k, f, br), w * 0.008f, mango1)
        drawCircle(a.brillo(0.6f, maxOf(k, 0.3f), br), w * 0.0025f, mango1 - Offset(w * 0.002f, w * 0.002f))
    }

    // ---------------------------------------------------------------- a la derecha: el paragüero y la lata de aceite

    /**
     * El paragüero de hierro pintado de verde oscuro con sus aros de latón, ancho, con tres planos enrollados gruesos (con la
     * espiral a la vista) y las dos banderas del guarda enrolladas en sus palos, que suben por delante del montante; y al lado, la
     * lata de aceite de hojalata, cortada por el borde.
     */
    fun DrawScope.paraguero(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val suelo = g.h - w * 0.018f
        val cx = w * 0.83f
        val top = suelo - w * 0.14f
        val p = Offset(cx, top)
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        drawOval(a.sombra(0.45f), Offset(cx - w * 0.06f, suelo - w * 0.012f), Size(w * 0.16f, w * 0.03f))
        // Las banderas, detrás: el palo y la tela enrollada, con sus vueltas y su atadura.
        listOf(Triple(0.022f, 0.4f, BERMELLON), Triple(0.04f, 0.36f, VERDE)).forEach { (dx, alto, col) ->
            val base = Offset(cx + w * dx, top + w * 0.02f)
            val punta = Offset(base.x + w * 0.05f, base.y - w * alto)
            bandera(base, punta, col, a, g)
        }
        // Los planos enrollados, gruesos, con su espiral arriba.
        listOf(Triple(-0.03f, 0.2f, PAPEL), Triple(-0.006f, 0.25f, Color(0xFF3F6E9A)), Triple(-0.044f, 0.17f, PAPEL_VIEJO)).forEach { (dx, alto, col) ->
            val base = Offset(cx + w * dx, top + w * 0.02f)
            val punta = Offset(base.x + w * 0.012f, base.y - w * alto)
            val q = Offset((base.x + punta.x) / 2, (base.y + punta.y) / 2)
            val kq = g.cerca(q); val fq = g.frio(q); val bq = g.brasa(q)
            val gr = w * 0.019f
            drawLine(a.luz(col, kq, fq, bq), base, punta, gr * 2f)
            drawLine(a.brillo(0.35f, kq, bq), base - Offset(gr * 0.55f, 0f), punta - Offset(gr * 0.55f, 0f), 1f * u)
            drawLine(a.sombra(0.3f), base + Offset(gr * 0.5f, 0f), punta + Offset(gr * 0.5f, 0f), gr * 0.6f)
            drawLine(a.luz(BERMELLON, kq, fq, bq), Offset(punta.x - gr, punta.y + w * 0.04f), Offset(punta.x + gr, punta.y + w * 0.039f), 1.4f * u)
            val boca = Rect(punta.x - gr, punta.y - gr * 0.45f, punta.x + gr, punta.y + gr * 0.45f)
            drawOval(a.luz(mezcla(col, PAPEL, 0.3f), kq, fq, bq), boca.topLeft, boca.size)
            drawArc(a.sombra(0.45f), 0f, 300f, false, boca.deflate(gr * 0.25f).topLeft, boca.deflate(gr * 0.25f).size, style = Stroke(0.7f * u))
            drawArc(a.sombra(0.4f), 40f, 280f, false, boca.deflate(gr * 0.55f).topLeft, boca.deflate(gr * 0.55f).size, style = Stroke(0.6f * u))
        }
        // El vaso de hierro verde, ancho, con sus aros de latón y su boca.
        val vaso = poligonoVapor(cx - w * 0.065f, top, cx + w * 0.065f, top, cx + w * 0.056f, suelo, cx - w * 0.056f, suelo)
        drawPath(vaso, a.luz(Color(0xFF223A2E), k, f, br))
        clipPath(vaso) {
            drawRect(a.brillo(0.28f, maxOf(k, 0.3f), br), Offset(cx - w * (if (a.oscuro) -0.02f else 0.04f), top), Size(w * 0.016f, w * 0.15f))
            drawRect(a.sombra(0.3f), Offset(cx + w * (if (a.oscuro) -0.065f else 0.02f), top), Size(w * 0.05f, w * 0.15f))
            listOf(0.14f, 0.86f).forEach { fy -> drawRect(a.luz(LATON, k, f, br), Offset(cx - w * 0.07f, top + (suelo - top) * fy), Size(w * 0.14f, w * 0.007f)) }
            // Un medallón fundido, liso.
            drawCircle(a.luz(Color(0xFF2E4A3B), k, f, br), w * 0.018f, Offset(cx, top + (suelo - top) * 0.5f))
            drawCircle(a.brillo(0.3f, maxOf(k, 0.3f), br), w * 0.018f, Offset(cx, top + (suelo - top) * 0.5f), style = Stroke(0.7f * u))
        }
        drawOval(a.luz(NEGRO, k, f, br), Offset(cx - w * 0.065f, top - w * 0.008f), Size(w * 0.13f, w * 0.016f))
        drawOval(a.brillo(0.4f, maxOf(k, 0.3f), br), Offset(cx - w * 0.065f, top - w * 0.008f), Size(w * 0.13f, w * 0.016f), style = Stroke(0.8f * u))
        lata(a, g)
    }

    private fun DrawScope.bandera(base: Offset, punta: Offset, col: Color, a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val q = Offset((base.x + punta.x) / 2, (base.y + punta.y) / 2)
        val kq = g.cerca(q); val fq = g.frio(q); val bq = g.brasa(q)
        drawLine(a.luz(NOGAL, kq, fq, bq), base, punta, w * 0.006f, StrokeCap.Round)
        drawCircle(a.luz(NOGAL, kq, fq, bq), w * 0.006f, punta)
        val t0 = Offset(base.x + (punta.x - base.x) * 0.3f, base.y + (punta.y - base.y) * 0.3f)
        val t1 = Offset(base.x + (punta.x - base.x) * 0.86f, base.y + (punta.y - base.y) * 0.86f)
        val dx = t1.x - t0.x; val dy = t1.y - t0.y
        val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
        val nx = -dy / largo; val ny = dx / largo
        val g0 = w * 0.016f; val g1 = w * 0.009f
        val rollo = poligonoVapor(t0.x + nx * g0, t0.y + ny * g0, t1.x + nx * g1, t1.y + ny * g1, t1.x - nx * g1, t1.y - ny * g1, t0.x - nx * g0, t0.y - ny * g0)
        drawPath(rollo, a.luz(col, kq, fq, bq))
        clipPath(rollo) {
            drawLine(a.sombra(0.35f), t0 - Offset(nx * g0 * 0.6f, ny * g0 * 0.6f), t1 - Offset(nx * g1 * 0.6f, ny * g1 * 0.6f), w * 0.008f)
            drawLine(a.brillo(0.35f, kq, bq), t0 + Offset(nx * g0 * 0.5f, ny * g0 * 0.5f), t1 + Offset(nx * g1 * 0.5f, ny * g1 * 0.5f), 1f * u)
            val vueltas = Path()
            for (j in 1 until 6) {
                val q0 = Offset(t0.x + dx * j / 6f, t0.y + dy * j / 6f)
                val gg = g0 + (g1 - g0) * j / 6f
                vueltas.moveTo(q0.x + nx * gg, q0.y + ny * gg); vueltas.lineTo(q0.x - nx * gg + dx * 0.05f, q0.y - ny * gg + dy * 0.05f)
            }
            drawPath(vueltas, a.sombra(0.4f), style = Stroke(0.8f * u))
        }
        val at = Offset(base.x + (punta.x - base.x) * 0.78f, base.y + (punta.y - base.y) * 0.78f)
        drawLine(a.luz(PAPEL_VIEJO, kq, fq, bq), at + Offset(nx * w * 0.011f, ny * w * 0.011f), at - Offset(nx * w * 0.011f, ny * w * 0.011f), 1.2f * u)
    }

    /** La lata de aceite de hojalata, cuadrada, con su tapón y su asa de alambre, cortada por el borde derecho. */
    private fun DrawScope.lata(a: AireVapor, g: MarcoVapor) {
        val u = density; val w = g.w
        val suelo = g.h - w * 0.006f
        val r = Rect(w * 0.915f, suelo - w * 0.13f, w * 1.02f, suelo)
        val p = r.center
        val k = g.cerca(p); val f = g.frio(p); val br = g.brasa(p)
        drawRect(a.sombra(0.4f), Offset(r.left - w * 0.03f, suelo - w * 0.012f), Size(w * 0.06f, w * 0.014f))
        drawRect(a.luz(mezcla(HOJALATA, TIZNE, 0.25f), k, f, br), r.topLeft, r.size)
        drawRect(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(r.left + w * 0.008f, r.top), Size(w * 0.012f, r.height))
        drawRect(a.sombra(0.2f), Offset(r.left + w * 0.05f, r.top), Size(w * 0.06f, r.height))
        // Los rebordes de la chapa y una abolladura.
        listOf(0.06f, 0.94f).forEach { fy -> drawRect(a.sombra(0.35f), Offset(r.left, r.top + r.height * fy), Size(r.width, 1f * u)) }
        drawOval(a.sombra(0.25f), Offset(r.left + w * 0.02f, r.top + r.height * 0.5f), Size(w * 0.03f, w * 0.018f))
        // El tapón y el asa de alambre.
        drawRect(a.luz(LATON, k, f, br), Offset(r.left + w * 0.025f, r.top - w * 0.012f), Size(w * 0.02f, w * 0.012f))
        drawArc(a.luz(HIERRO, k, f, br), 180f, 120f, false, Offset(r.left + w * 0.04f, r.top - w * 0.03f), Size(w * 0.08f, w * 0.05f), style = Stroke(w * 0.003f))
        drawRect(a.sombra(0.3f), Offset(r.left, r.top), Size(r.width, 1f * u))
    }
}
