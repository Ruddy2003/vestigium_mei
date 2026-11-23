package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El rincón de la libreta de momentos de Tierras Antiguas ([MuebleLibretaAntiguas]), salvo el hogar ([HogarLibretaAntiguas]) y el
 * libro ([LibroLibretaAntiguas]): la pared de lapislázuli con su sembrado de oro; la viga, la zapata y el pie derecho de roble;
 * el friso de tablas y el suelo; el paño de luz de la ventana de la izquierda; el jalón apoyado; la mesa del cartógrafo, y lo que
 * hay en ella: el mapa que cuelga, el reloj de arena y los cuencos de colores con el pincel y el compás de puntas.
 */
internal object RinconLibretaAntiguas {
    private val P = PaletaAntiguas
    private val CALIDO_MADERA = Color(0xFFC0702A)
    /** Los colores del cartógrafo: el lapislázuli molido, la malaquita y el bermellón. */
    private val AZUL_MOLIDO = Color(0xFF3C63C8)
    private val MALAQUITA = Color(0xFF2F8A5E)
    private val BERMELLON = Color(0xFFC23A26)
    private val BARRO = Color(0xFFB0643A)
    private val ARENA = Color(0xFFDDB66E)
    /** El sol en el enlucido azul: un azul más claro y algo tibio, no un blanco. */
    private val SOL_EN_AZUL = Color(0xFF7898DE)

    // ---------------------------------------------------------------- la pared

    fun DrawScope.pared(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val w = s.w; val h = s.h; val u = density
        val a = L.a
        val r = Rect(0f, 0f, w, s.friso + 2f)
        fun pc(c: Color) = colorParedAntiguas(a, c)
        drawRect(pc(P.LAPIS), r.topLeft, r.size)
        // De día, más clara hacia la ventana (a la izquierda) y más honda hacia el hogar.
        if (!L.oscuro) drawRect(Brush.horizontalGradient(listOf(P.LAPIS_LUZ.copy(alpha = 0.26f), Color.Transparent, P.LAPIS_HONDO.copy(alpha = 0.22f)), 0f, w), r.topLeft, r.size)
        pinceladas(r, pc(P.LAPIS_LUZ), 7501, 44, u, 0.14f)
        pinceladas(r, pc(P.LAPIS_HONDO), 7502, 38, u, 0.17f)
        sembrado(r, pc(P.ORO))
        // Dos grietas finas del enlucido.
        val g = Path()
        listOf(0.62f to 0.12f, 0.2f to 0.62f).forEachIndexed { i, (fx, fy) ->
            var q = Offset(h * fx + (if (i == 0) (w - h * 1.4f).coerceAtLeast(0f) * 0.4f else 0f), h * fy)
            g.moveTo(q.x, q.y)
            for (j in 0 until 6) { q = Offset(q.x + (azar(7510 + i, j) - 0.4f) * 8f * u, q.y + (5f + 5f * azar(7512 + i, j)) * u); g.lineTo(q.x, q.y) }
        }
        drawPath(g, pc(P.LAPIS_HONDO).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawPath(g, a.claro(0.18f), style = Stroke(0.5f * u))
        // De noche, el charco del fuego: lejos, el añil casi negro.
        with(L) { charco(r, a.hondo, P.CALIDO_PARED) }
    }

    /** El sembrado de oro viejo de la sala: rombos chicos al tresbolillo, con un punto a cada lado, gastados. */
    private fun DrawScope.sembrado(r: Rect, oro: Color) {
        val u = density
        val paso = 32f * u
        val rombos = Path(); val puntos = Path()
        var fila = 0
        var yy = r.top + paso * 0.5f
        while (yy < r.bottom + paso) {
            var xx = r.left + (if (fila % 2 == 0) paso * 0.3f else paso * 0.8f)
            var col = 0
            while (xx < r.right + paso) {
                val z = azar(7520 + fila * 31, col)
                if (z > 0.16f) {
                    val q = 3.6f * u * (if (z > 0.85f) 0.7f else 1f)
                    rombos.addPath(poligonoAntiguas(xx, yy - q, xx + q * 0.72f, yy, xx, yy + q, xx - q * 0.72f, yy))
                    if (z > 0.3f) {
                        puntos.addOval(Rect(xx - q * 1.9f - 1f * u, yy - 1f * u, xx - q * 1.9f + 1f * u, yy + 1f * u))
                        puntos.addOval(Rect(xx + q * 1.9f - 1f * u, yy - 1f * u, xx + q * 1.9f + 1f * u, yy + 1f * u))
                    }
                }
                xx += paso; col++
            }
            yy += paso * 0.72f; fila++
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(rombos, oro.copy(alpha = 0.4f))
            drawPath(puntos, oro.copy(alpha = 0.34f))
        }
    }

    // ---------------------------------------------------------------- la madera de la sala

    /**
     * La viga del techo con su zapata, el pie derecho de la izquierda (de roble, achaflanado), el friso de tablas con su moldura
     * y su rodapié, y el suelo de tablas. De noche, el charco del fuego en pardo rojizo.
     */
    fun DrawScope.madera(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val w = s.w; val h = s.h; val u = density
        val am = L.am
        fun cm(c: Color, f: Float = 0f) = am.dia(c, f, 0f).let { if (L.oscuro) c else it }
        // ---- el friso de tablas, de pie, con su moldura arriba y el rodapié
        val fr = Rect(0f, s.friso, w, s.suelo)
        drawRect(cm(P.ROBLE), fr.topLeft, fr.size)
        var x = s.poste
        var k = 0
        while (x < w) {
            val an = h * (0.075f + 0.02f * azar(7530, k))
            drawRect(cm(if (k % 2 == 0) P.ROBLE else mezcla(P.ROBLE, P.ROBLE_LUZ, 0.18f)), Offset(x, fr.top), Size(an, fr.height))
            for (j in 0 until 3) drawLine(cm(P.ROBLE_OSC).copy(alpha = 0.4f), Offset(x + an * (0.25f + 0.25f * j), fr.top), Offset(x + an * (0.22f + 0.27f * j), fr.bottom), 0.6f * u)
            drawLine(cm(P.ROBLE_HONDO), Offset(x, fr.top), Offset(x, fr.bottom), 1.4f * u)
            drawLine(cm(P.ROBLE_LUZ).copy(alpha = 0.6f), Offset(x + 1.4f * u, fr.top), Offset(x + 1.4f * u, fr.bottom), 0.7f * u)
            x += an; k++
        }
        // La moldura de arriba: el canto a la luz, el bocel y su sombra en las tablas.
        drawRect(cm(P.ROBLE_LUZ), Offset(0f, fr.top - h * 0.012f), Size(w, h * 0.022f))
        drawRect(am.claro(0.4f), Offset(0f, fr.top - h * 0.012f), Size(w, 1.2f * u))
        drawRect(cm(P.ROBLE_OSC), Offset(0f, fr.top + h * 0.006f), Size(w, h * 0.006f))
        drawRect(am.sombra(0.35f), Offset(0f, fr.top + h * 0.012f), Size(w, h * 0.012f))
        drawRect(cm(P.ROBLE_OSC), Offset(0f, s.suelo - h * 0.03f), Size(w, h * 0.03f))
        drawRect(am.claro(0.25f), Offset(0f, s.suelo - h * 0.03f), Size(w, 1f * u))
        // ---- el suelo de tablas
        val su = Rect(0f, s.suelo, w, h)
        drawRect(cm(P.SUELO), su.topLeft, su.size)
        if (!L.oscuro) drawRect(Brush.horizontalGradient(listOf(P.SUELO_LUZ.copy(alpha = 0.45f), Color.Transparent), 0f, w * 0.6f), su.topLeft, su.size)
        listOf(0.3f, 0.62f).forEach { f -> drawLine(cm(P.ROBLE_OSC), Offset(0f, s.suelo + (h - s.suelo) * f), Offset(w, s.suelo + (h - s.suelo) * f), 1f * u) }
        for (j in 0 until 9) {
            val y0 = s.suelo + (h - s.suelo) * (if (j % 2 == 0) 0f else 0.3f)
            val xx = w * (0.07f + 0.11f * j) + h * 0.03f * azar(7535, j)
            drawLine(cm(P.ROBLE_OSC), Offset(xx, y0), Offset(xx, y0 + (h - s.suelo) * 0.3f), 1f * u)
        }
        pinceladas(su, cm(P.ROBLE_OSC), 7536, 14, u, 0.14f)
        drawRect(am.sombra(0.45f), Offset(0f, s.suelo), Size(w, 2.2f * u))
        // ---- la viga del techo, con su cara de abajo en sombra, las marcas de la azuela y sus clavos
        val vg = Rect(0f, 0f, w, s.viga)
        drawRect(cm(P.ROBLE), vg.topLeft, vg.size)
        if (!L.oscuro) drawRect(Brush.horizontalGradient(listOf(P.ROBLE_LUZ.copy(alpha = 0.5f), Color.Transparent), 0f, w * 0.5f), vg.topLeft, vg.size)
        drawRect(cm(P.ROBLE_OSC), Offset(0f, s.viga - h * 0.012f), Size(w, h * 0.012f))
        for (j in 0 until (w / (h * 0.05f)).toInt()) {
            val xx = h * 0.05f * j + h * 0.02f * azar(7540, j)
            drawLine(cm(P.ROBLE_OSC).copy(alpha = 0.6f), Offset(xx, h * 0.01f), Offset(xx + h * 0.025f, h * 0.014f + h * 0.01f * azar(7541, j)), 1f * u)
        }
        drawRect(am.sombra(0.4f), Offset(0f, s.viga), Size(w, 2.6f * u))
        // ---- la zapata sobre el pie derecho: el canto curvo, un rombo tallado
        val z = Path().apply {
            moveTo(0f, s.viga); lineTo(s.zapata, s.viga); lineTo(s.zapata, s.viga + h * 0.016f)
            quadraticTo(s.zapata - h * 0.03f, s.viga + h * 0.02f, s.zapata - h * 0.05f, s.viga + h * 0.034f)
            cubicTo(s.zapata - h * 0.08f, s.viga + h * 0.05f, s.poste + h * 0.03f, s.viga + h * 0.04f, s.poste, s.viga + h * 0.062f)
            lineTo(0f, s.viga + h * 0.062f); close()
        }
        drawPath(z, cm(P.ROBLE_LUZ, 0.05f))
        clipPath(z) {
            drawRect(am.sombra(0.35f), Offset(0f, s.viga + h * 0.046f), Size(s.zapata, h * 0.03f))
            drawRect(am.claro(0.35f), Offset(0f, s.viga), Size(s.zapata, 1.2f * u))
        }
        drawPath(romboLibretaAntiguas(Offset(s.poste + h * 0.035f, s.viga + h * 0.024f), h * 0.012f, h * 0.014f), cm(P.ROBLE_OSC))
        drawPath(z, am.sombra(0.4f), style = Stroke(0.8f * u))
        // ---- el pie derecho, achaflanado, con el chaflán de la luz y el de la sombra
        val po = Rect(h * 0.004f, s.viga, s.poste, s.suelo)
        drawRect(am.sombra(0.4f), Offset(po.right, po.top), Size(h * 0.022f, po.height))
        drawRect(cm(P.ROBLE), po.topLeft, po.size)
        drawRect(cm(P.ROBLE_LUZ, 0.1f), Offset(po.left, po.top + h * 0.09f), Size(h * 0.012f, po.height - h * 0.15f))
        drawRect(cm(P.ROBLE_OSC), Offset(po.right - h * 0.012f, po.top + h * 0.09f), Size(h * 0.012f, po.height - h * 0.15f))
        for (j in 0 until 4) drawLine(cm(P.ROBLE_OSC).copy(alpha = 0.5f), Offset(po.left + po.width * (0.3f + 0.13f * j), po.top), Offset(po.left + po.width * (0.32f + 0.12f * j), po.bottom), 0.6f * u)
        // Los remates del chaflán: arriba y abajo, en pico.
        listOf(po.top + h * 0.09f, po.bottom - h * 0.06f).forEachIndexed { i, y ->
            val d = if (i == 0) -1f else 1f
            drawPath(poligonoAntiguas(po.left, y, po.left + h * 0.012f, y, po.left, y + d * h * 0.02f), cm(P.ROBLE_LUZ, 0.15f))
            drawPath(poligonoAntiguas(po.right, y, po.right - h * 0.012f, y, po.right, y + d * h * 0.02f), cm(P.ROBLE_HONDO))
        }
        // De noche, el charco del fuego en la madera: lejos, el pardo rojizo.
        with(L) {
            charco(fr, am.hondo, CALIDO_MADERA)
            charco(su, am.hondo, CALIDO_MADERA)
            charco(vg, am.hondo, CALIDO_MADERA)
            charco(po, am.hondo, CALIDO_MADERA)
            clipPath(z) { charco(Rect(0f, s.viga, s.zapata, s.viga + h * 0.07f), am.hondo, CALIDO_MADERA) }
        }
    }

    // ---------------------------------------------------------------- el paño de luz de la ventana

    /**
     * El paño de luz de la ventana de la izquierda: uno solo, sesgado, partido a lo largo por la sombra fina del parteluz. Cae en
     * la pared y se quiebra en lo que está delante de ella: en un plano más cercano ([plano]: 0 la pared, 1 el pie derecho, 1.6 la
     * tapa del libro) la luz llega antes y el paño se corre hacia la ventana (arriba a la izquierda). La luna está más alta:
     * su paño cae más abajo.
     */
    fun panoForma(s: SitioLibretaAntiguas, luna: Boolean, plano: Float): Path {
        val h = s.h
        val x0 = h * 0.07f + (if (luna) h * 0.03f else 0f) - h * 0.028f * plano
        val y0 = h * 0.11f + (if (luna) h * 0.06f else 0f) - h * 0.022f * plano
        val ancho = h * 0.2f; val alto = h * 0.34f
        val sesgo = 0.6f
        val raya = h * 0.009f / (2f * ancho)
        fun p(fx: Float, fy: Float): Offset {
            val y = y0 + alto * fy
            return Offset(x0 + (y - y0) * sesgo + ancho * (1f + 0.12f * fy) * fx, y)
        }
        val forma = Path()
        listOf(0f to 0.5f - raya, 0.5f + raya to 1f).forEach { (fa, fb) ->
            val q0 = p(fa, 0f); val q1 = p(fb, 0f); val q2 = p(fb, 1f); val q3 = p(fa, 1f)
            forma.moveTo(q0.x, q0.y); forma.lineTo(q1.x, q1.y); forma.lineTo(q2.x, q2.y); forma.lineTo(q3.x, q3.y); forma.close()
        }
        return forma
    }

    /**
     * De día, el sol de la ventana: en el enlucido azul lo aclara hacia un azul tibio con un halo corto; en el roble del pie
     * derecho, corrido, lo calienta. De noche no hay paño (sobre el añil se leería como un cristal apoyado): la luna solo deja su
     * filo frío en los cantos de la izquierda (el pie derecho, aquí; el jalón, el reloj de arena y el canto del libro, en los suyos).
     */
    fun DrawScope.pano(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h
        if (L.oscuro) {
            drawLine(L.am.lunar(0.9f), Offset(h * 0.006f, s.viga + h * 0.07f), Offset(h * 0.006f, s.suelo - h * 0.02f), 1.4f * density)
            return
        }
        val pared = panoForma(s, L.oscuro, 0f)
        val poste = panoForma(s, L.oscuro, 1f)
        clipRect(s.poste, s.viga, s.xJamba, s.suelo) {
            if (!L.oscuro) {
                drawPath(pared, P.SOL.copy(alpha = 0.14f), style = Stroke(h * 0.014f), blendMode = BlendMode.Screen)
                drawPath(pared, SOL_EN_AZUL.copy(alpha = 0.9f))
                drawPath(pared, P.SOL.copy(alpha = 0.14f), blendMode = BlendMode.Screen)
            } else {
                drawPath(pared, P.LUNA.copy(alpha = 0.08f), style = Stroke(h * 0.018f), blendMode = BlendMode.Screen)
                drawPath(pared, P.LUNA.copy(alpha = 0.24f), blendMode = BlendMode.Screen)
            }
        }
        clipRect(h * 0.004f, s.viga + h * 0.062f, s.poste, s.suelo) {
            if (!L.oscuro) {
                drawPath(poste, P.SOL.copy(alpha = 0.42f), blendMode = BlendMode.Screen)
                drawPath(poste, P.SOL.copy(alpha = 0.1f))
            } else drawPath(poste, P.LUNA.copy(alpha = 0.16f), blendMode = BlendMode.Screen)
        }
    }

    // ---------------------------------------------------------------- el jalón

    /**
     * El jalón del agrimensor: una vara de fresno a la vista, con tres bandas pintadas cerca de la punta (negro, ocre y negro,
     * desgastadas), apoyada en la pared detrás de la mesa, con su regatón de hierro abajo.
     */
    fun DrawScope.jalon(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val b = Offset(s.poste + h * 0.035f, s.suelo - h * 0.004f)
        val t = Offset(s.poste + h * 0.098f, h * 0.125f)
        val mid = (b + t) / 2f
        val g = h * 0.0125f
        val dir = (t - b) / (t - b).getDistance()
        val nrm = Offset(-dir.y, dir.x)
        val largo = (t - b).getDistance()
        // Su sombra en la pared: de día, a la derecha; de noche, la de la luna, tenue.
        val sombraD = if (!L.oscuro) Offset(h * 0.03f, h * 0.004f) else Offset(h * 0.022f, h * 0.008f)
        drawLine(L.a.sombra(0.3f), b + sombraD, t + sombraD, g, StrokeCap.Round)
        // La vara de fresno, con su veta.
        val fresno = Color(0xFFAE8A5C)
        drawLine(L.cosa(fresno, mid), b, t, g)
        drawLine(L.cosa(mezcla(fresno, P.ROBLE_OSC, 0.45f), mid).copy(alpha = 0.55f), b - nrm * (g * 0.12f), t - nrm * (g * 0.12f), 0.6f * u)
        // Las bandas pintadas, gastadas: el negro y el ocre, con rozaduras donde asoma la madera.
        val negro = Color(0xFF241C16)
        listOf(Triple(0.66f, 0.035f, negro), Triple(0.715f, 0.05f, P.OCRE), Triple(0.785f, 0.035f, negro)).forEachIndexed { i, (f, l, col) ->
            val p0 = b + dir * (largo * f); val p1 = p0 + dir * (h * l)
            drawLine(L.cosa(col, p0).copy(alpha = 0.92f), p0, p1, g)
            for (j in 0 until 2) {
                val q = p0 + dir * (h * l * (0.25f + 0.45f * j + 0.1f * azar(7560 + i, j)))
                drawLine(L.cosa(fresno, q).copy(alpha = 0.6f), q - nrm * (g * 0.45f), q + dir * (h * 0.004f) - nrm * (g * 0.05f), 0.8f * u)
            }
        }
        // El canto a la luz (a la izquierda) y el de la sombra; de noche, la luna en el de la izquierda.
        drawLine(if (L.oscuro) L.a.lunar(0.5f * (1f + L.l(mid))) else L.a.claro(0.4f), b + nrm * (g * 0.32f), t + nrm * (g * 0.32f), 0.8f * u)
        drawLine(L.am.sombra(0.35f), b - nrm * (g * 0.34f), t - nrm * (g * 0.34f), 1f * u)
        // La punta, la madera de testa más honda; abajo, el regatón de hierro con su punta.
        drawLine(L.cosa(mezcla(fresno, P.ROBLE_OSC, 0.5f), t), t - dir * (h * 0.006f), t, g)
        drawLine(L.cosa(P.HIERRO, b), b, b + dir * (h * 0.04f), g * 1.15f)
        drawLine(L.cosa(P.HIERRO_LUZ, b), b + dir * (h * 0.004f) + nrm * (g * 0.3f), b + dir * (h * 0.04f) + nrm * (g * 0.3f), 0.7f * u)
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa del cartógrafo, de roble: el tablero visto un poco desde arriba, el canto con su bocel, el faldón con una cadena de
     * rombos tallados y un cajón con su argolla, las patas torneadas (de jarrón y bollo) y la chambrana. Su sombra en el friso y
     * en el suelo.
     */
    fun DrawScope.mesa(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val x0 = s.mesa0; val x1 = s.mesa1
        fun pl(f: Float) = Offset(x0 + (x1 - x0) * f, (s.mesaFrente + s.suelo) / 2)
        /** Un color de la madera a lo largo de la mesa: de noche, del lado lejano al del fuego. */
        fun largo(c: Color, top: Float, alto: Float, izq: Float = x0, der: Float = x1): Brush? =
            if (!L.oscuro) null else Brush.horizontalGradient(listOf(L.cosa(c, Offset(izq, top + alto / 2)), L.cosa(c, Offset(der, top + alto / 2))), izq, der)
        fun pinta(c: Color, r: Rect) { val b = largo(c, r.top, r.height, r.left, r.right); if (b != null) drawRect(b, r.topLeft, r.size) else drawRect(c, r.topLeft, r.size) }
        // ---- las sombras: en el suelo, bajo la mesa; en el friso, detrás (de día, corrida a la derecha)
        val corre = if (L.oscuro) -h * 0.03f else h * 0.04f
        drawRect(am.sombra(if (L.oscuro) 0.3f else 0.22f), Offset(x0 + corre, s.canto), Size(x1 - x0, s.suelo - s.canto))
        drawOval(am.sombra(0.4f), Offset(x0 - h * 0.02f + corre * 0.5f, s.suelo - h * 0.012f), Size(x1 - x0 + h * 0.06f, h * 0.05f))
        // ---- las patas de atrás (más hondas) y la chambrana
        val anP = h * 0.044f
        val patas = listOf(x0 + h * 0.012f, x1 - h * 0.012f - anP)
        patas.forEach { px -> pata(px + h * 0.028f, s.faldon - h * 0.02f, s.suelo - h * 0.02f, anP * 0.85f, s, L, atras = true) }
        val ch = Rect(patas[0] + anP, s.suelo - h * 0.07f, patas[1], s.suelo - h * 0.052f)
        pinta(P.ROBLE, ch)
        drawRect(am.claro(0.3f), ch.topLeft, Size(ch.width, 1f * u))
        drawRect(am.sombra(0.35f), Offset(ch.left, ch.bottom - 1.2f * u), Size(ch.width, 1.2f * u))
        // ---- el faldón, con su cadena de rombos y el cajón
        val fa = Rect(x0, s.canto, x1, s.faldon)
        pinta(P.ROBLE, fa)
        drawRect(am.sombra(0.45f), Offset(fa.left, fa.top), Size(fa.width, h * 0.008f))
        drawRect(am.sombra(0.3f), Offset(fa.left, fa.bottom - 1.6f * u), Size(fa.width, 1.6f * u))
        val cajon = Rect(maxOf(s.mapa.right + h * 0.04f, x0 + (x1 - x0) * 0.62f), fa.top + h * 0.012f, x1 - h * 0.05f, fa.bottom - h * 0.01f)
        var xr = x0 + h * 0.04f
        while (xr < x1 - h * 0.03f) {
            if (xr < cajon.left - h * 0.02f || xr > cajon.right + h * 0.02f || cajon.width < h * 0.1f) {
                val c = Offset(xr, fa.center.y)
                // Un rombo tallado: el hueco hondo, el canto de abajo a la luz y la punta de dentro.
                drawPath(romboLibretaAntiguas(c + Offset(0.6f * u, 0.8f * u), h * 0.013f, h * 0.018f), am.claro(0.3f))
                drawPath(romboLibretaAntiguas(c, h * 0.013f, h * 0.018f), L.cosa(P.ROBLE_OSC, c))
                drawPath(romboLibretaAntiguas(c, h * 0.005f, h * 0.007f), L.cosa(P.ROBLE_LUZ, c))
            }
            xr += h * 0.05f
        }
        if (cajon.width >= h * 0.1f) {
            drawRect(am.sombra(0.45f), cajon.topLeft, cajon.size, style = Stroke(1.4f * u))
            drawRect(am.claro(0.25f), Offset(cajon.left, cajon.bottom), Size(cajon.width, 1f * u))
            val ar = Offset(cajon.center.x, cajon.center.y)
            drawCircle(L.cosa(P.LATON_OSC, ar), h * 0.009f, ar)
            drawCircle(L.cosa(P.LATON, ar), h * 0.009f, ar + Offset(0f, h * 0.006f), style = Stroke(1.6f * u))
            drawCircle(am.brillo(0.6f, L.k(ar)), 1f * u, ar + Offset(-h * 0.005f, h * 0.004f))
        }
        listOf(x0 + h * 0.012f, x1 - h * 0.012f).forEach { xc -> remacheAntiguas(Offset(xc, fa.center.y), 1.5f * u, am, L.k(Offset(xc, fa.center.y)), P.LATON) }
        // ---- las patas de delante
        patas.forEach { px -> pata(px, s.faldon, s.suelo, anP, s, L, atras = false) }
        // ---- el tablero, un poco desde arriba, y su canto con el bocel
        val tablero = poligonoAntiguas(x0, s.mesaAtras, x1, s.mesaAtras, x1 + h * 0.008f, s.mesaFrente, x0 - h * 0.008f, s.mesaFrente)
        val bt = largo(P.ROBLE_LUZ, s.mesaAtras, s.mesaFrente - s.mesaAtras)
        if (bt != null) drawPath(tablero, bt) else drawPath(tablero, P.ROBLE_LUZ)
        clipPath(tablero) {
            for (j in 0 until 4) drawLine(L.cosa(P.ROBLE, pl(0.5f)).copy(alpha = 0.55f), Offset(x0, s.mesaAtras + (s.mesaFrente - s.mesaAtras) * (0.2f + 0.22f * j)), Offset(x1, s.mesaAtras + (s.mesaFrente - s.mesaAtras) * (0.24f + 0.2f * j)), 0.7f * u)
            if (!L.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent), x0, x0 + (x1 - x0) * 0.5f), Offset(x0 - h * 0.01f, s.mesaAtras), Size(x1 - x0 + h * 0.02f, s.mesaFrente - s.mesaAtras))
            drawRect(am.sombra(0.3f), Offset(x0 - h * 0.01f, s.mesaAtras), Size(x1 - x0 + h * 0.02f, h * 0.008f))
        }
        val cn = Rect(x0 - h * 0.008f, s.mesaFrente, x1 + h * 0.008f, s.canto)
        pinta(P.ROBLE, cn)
        drawRect(am.claro(0.5f), Offset(cn.left, cn.top), Size(cn.width, 1.2f * u))
        drawRect(if (L.oscuro) am.filo(0.3f) else am.claro(0.2f), Offset(cn.left, cn.top + cn.height * 0.3f), Size(cn.width, cn.height * 0.25f))
        drawRect(am.sombra(0.4f), Offset(cn.left, cn.bottom - 1.6f * u), Size(cn.width, 1.6f * u))
        // De noche, el filo del fuego en el canto derecho de la mesa.
        if (L.oscuro) drawRect(am.filo(0.6f * L.k(Offset(x1, s.mesaFrente))), Offset(x1 + h * 0.004f, s.mesaFrente), Size(h * 0.004f, s.canto - s.mesaFrente))
    }

    /** Una pata torneada: el dado de arriba, el anillo, el jarrón, el cuello y el bollo del pie, con su luz y su sombra. */
    private fun DrawScope.pata(x: Float, arriba: Float, abajo: Float, an: Float, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas, atras: Boolean) {
        val h = s.h; val u = density
        val am = L.am
        val c = Offset(x + an / 2, (arriba + abajo) / 2)
        val madera = L.cosa(if (atras) P.ROBLE_OSC else P.ROBLE, c)
        val luz = L.cosa(if (atras) P.ROBLE else P.ROBLE_LUZ, c)
        val alto = abajo - arriba
        val partes = listOf(
            Rect(x, arriba, x + an, arriba + alto * 0.22f),
            Rect(x - an * 0.08f, arriba + alto * 0.22f, x + an * 1.08f, arriba + alto * 0.28f),
            Rect(x - an * 0.12f, arriba + alto * 0.3f, x + an * 1.12f, arriba + alto * 0.62f),
            Rect(x + an * 0.22f, arriba + alto * 0.6f, x + an * 0.78f, arriba + alto * 0.8f),
            Rect(x - an * 0.1f, arriba + alto * 0.78f, x + an * 1.1f, abajo),
        )
        partes.forEachIndexed { i, r ->
            if (i == 0 || i == 3) drawRect(madera, r.topLeft, r.size) else drawOval(madera, r.topLeft, r.size)
            // La luz a la izquierda (de día) y la sombra a la derecha; de noche, el filo del fuego a la derecha si le llega.
            val luzR = Rect(r.left + r.width * 0.14f, r.top + r.height * 0.1f, r.left + r.width * 0.36f, r.bottom - r.height * 0.1f)
            if (i == 0 || i == 3) drawRect(if (L.oscuro) luz.copy(alpha = 0.4f) else luz, luzR.topLeft, luzR.size)
            else drawOval(if (L.oscuro) luz.copy(alpha = 0.4f) else luz, luzR.topLeft, luzR.size)
            if (i == 0 || i == 3) drawRect(am.sombra(0.35f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
            else drawOval(am.sombra(0.3f), Offset(r.left + r.width * 0.55f, r.top + r.height * 0.12f), Size(r.width * 0.42f, r.height * 0.76f))
            if (L.oscuro && !atras) drawLine(am.filo(0.7f * L.k(r.center)), Offset(r.right - 0.8f * u, r.top + r.height * 0.2f), Offset(r.right - 0.8f * u, r.bottom - r.height * 0.2f), 1.2f * u)
        }
        if (!atras) contactoAntiguas(Offset(x + an / 2 + h * 0.01f, abajo), an * 1.6f, h * 0.014f, am, 0.45f)
    }

    // ---------------------------------------------------------------- lo que hay en la mesa

    fun DrawScope.cosas(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        mapaColgado(s, L)
        s.reloj?.let { reloj(it, s, L) }
        s.cuencos?.let { cuencos(it, s, L) }
    }

    /**
     * El mapa desplegado que sale de debajo del libro y cuelga por el borde de la mesa: el pliego con sus dobleces, el canto
     * de la mesa marcado en la curva, la orilla de abajo ondulada y la esquina vuelta (se ve el revés); en él, a tinta y aguadas,
     * un camino inventado.
     */
    private fun DrawScope.mapaColgado(s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val m = s.mapa
        val c = m.center
        val perg = L.cosa(P.VITELA, c)
        val pergOsc = L.cosa(P.VITELA_OSC, c)
        // Lo que va sobre el tablero (sale de debajo del atril).
        drawPath(poligonoAntiguas(m.left + h * 0.01f, s.mesaAtras + h * 0.008f, m.right - h * 0.006f, s.mesaAtras + h * 0.008f, m.right + h * 0.002f, s.mesaFrente, m.left, s.mesaFrente), perg)
        // El cuerpo que cuelga, con la orilla de abajo ondulada y la esquina de la derecha vuelta.
        val vuelta = h * 0.05f
        val cuerpo = Path().apply {
            moveTo(m.left, s.mesaFrente); lineTo(m.right + h * 0.002f, s.mesaFrente)
            lineTo(m.right + h * 0.004f, m.bottom - vuelta)
            lineTo(m.right - vuelta, m.bottom)
            quadraticTo(m.left + m.width * 0.6f, m.bottom - h * 0.012f, m.left + m.width * 0.35f, m.bottom - h * 0.002f)
            quadraticTo(m.left + m.width * 0.15f, m.bottom + h * 0.006f, m.left - h * 0.003f, m.bottom - h * 0.006f)
            close()
        }
        drawPath(Path().apply { addPath(cuerpo, Offset(if (L.oscuro) -h * 0.012f else h * 0.014f, h * 0.008f)) }, am.sombra(0.32f))
        drawPath(cuerpo, perg)
        clipPath(cuerpo) {
            mapaDibujo(Rect(m.left, s.canto, m.right, m.bottom), L, 7600)
            // La curva sobre el canto de la mesa: más clara arriba, con la sombra debajo.
            drawRect(am.claro(0.3f), Offset(m.left, s.mesaFrente), Size(m.width, (s.canto - s.mesaFrente) * 0.5f))
            drawRect(am.sombra(0.22f), Offset(m.left, s.canto), Size(m.width, h * 0.012f))
            // Los dobleces: uno a lo largo y uno a lo ancho, con su lado de luz y su lado de sombra.
            val xd = m.left + m.width * 0.5f; val yd = (s.canto + m.bottom) / 2
            drawLine(am.sombra(0.25f), Offset(xd, s.canto), Offset(xd, m.bottom), 1.2f * u)
            drawLine(am.claro(0.3f), Offset(xd + 1.2f * u, s.canto), Offset(xd + 1.2f * u, m.bottom), 0.8f * u)
            drawLine(am.sombra(0.2f), Offset(m.left, yd), Offset(m.right, yd), 1f * u)
            drawRect(am.sombra(0.12f), Offset(xd, s.canto), Size(m.right - xd, m.bottom - s.canto))
            // La orilla gastada.
            drawPath(cuerpo, pergOsc.copy(alpha = 0.7f), style = Stroke(2.4f * u))
            if (L.oscuro) drawRect(am.filo(0.25f * L.k(Offset(m.right, c.y))), Offset(m.right - h * 0.03f, s.mesaFrente), Size(h * 0.03f, m.height))
        }
        // La esquina vuelta: el revés del pliego, más hondo.
        val esquina = poligonoAntiguas(m.right + h * 0.004f, m.bottom - vuelta, m.right - vuelta, m.bottom, m.right - vuelta * 0.55f, m.bottom - vuelta * 0.6f)
        drawPath(esquina, mezcla(pergOsc, perg, 0.3f))
        drawPath(esquina, am.sombra(0.35f), style = Stroke(0.8f * u))
    }

    /**
     * Un camino inventado, a tinta y aguadas, dentro de [r]: el río que baja con su lago, la cadena de montes con su lado en sombra,
     * el bosque de puntos, la senda roja punteada hasta el círculo de la parada y una rosa de cuatro puntas; el filete del borde.
     * Ninguna costa ni ninguna forma de un mapa conocido.
     */
    fun DrawScope.mapaDibujo(r: Rect, L: LuzLibretaAntiguas, sem: Int) {
        val u = density
        val c = r.center
        val tinta = L.cosa(P.TINTA, c).copy(alpha = 0.7f)
        val agua = L.cosa(Color(0xFF3F6E9A), c)
        val monte = L.cosa(Color(0xFFB48A4E), c)
        val bosque = L.cosa(Color(0xFF4E7A3C), c)
        val rojo = L.cosa(P.RUBIA, c)
        fun q(fx: Float, fy: Float) = Offset(r.left + r.width * fx, r.top + r.height * fy)
        // El borde.
        val m = minOf(r.width, r.height) * 0.07f
        drawRect(tinta.copy(alpha = 0.4f), Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m), style = Stroke(0.7f * u))
        // El río y su lago.
        val rio = Path().apply {
            val a0 = q(0.12f, 0.08f); moveTo(a0.x, a0.y)
            val b1 = q(0.05f + 0.1f * azar(sem, 1), 0.4f); val b2 = q(0.4f, 0.32f); val b3 = q(0.42f, 0.6f)
            cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
            val d1 = q(0.45f, 0.82f); val d2 = q(0.75f, 0.7f); val d3 = q(0.92f, 0.9f)
            cubicTo(d1.x, d1.y, d2.x, d2.y, d3.x, d3.y)
        }
        drawPath(rio, agua.copy(alpha = 0.35f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
        drawPath(rio, agua, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        val lago = q(0.62f, 0.72f)
        drawOval(agua.copy(alpha = 0.45f), Offset(lago.x - r.width * 0.09f, lago.y - r.height * 0.06f), Size(r.width * 0.18f, r.height * 0.11f))
        drawOval(agua.copy(alpha = 0.8f), Offset(lago.x - r.width * 0.09f, lago.y - r.height * 0.06f), Size(r.width * 0.18f, r.height * 0.11f), style = Stroke(0.8f * u))
        // La cadena de montes: cada uno con su cara de luz y su cara de sombra.
        for (j in 0 until 5) {
            val b = q(0.5f + 0.09f * j, 0.3f + 0.06f * (j % 2) + 0.03f * azar(sem, 10 + j))
            val al = r.height * (0.12f + 0.06f * azar(sem, 20 + j)); val an = al * 0.75f
            drawPath(poligonoAntiguas(b.x - an, b.y, b.x, b.y - al, b.x, b.y), monte.copy(alpha = 0.55f))
            drawPath(poligonoAntiguas(b.x, b.y - al, b.x + an, b.y, b.x, b.y), mezcla(monte, tinta, 0.45f).copy(alpha = 0.7f))
            drawPath(Path().apply { moveTo(b.x - an, b.y); lineTo(b.x, b.y - al); lineTo(b.x + an, b.y) }, tinta, style = Stroke(0.8f * u))
        }
        // El bosque.
        for (j in 0 until 11) {
            val p = q(0.14f + 0.2f * azar(sem, 30 + j), 0.5f + 0.32f * azar(sem, 40 + j))
            drawCircle(bosque.copy(alpha = 0.75f), r.width * 0.022f, p)
            drawCircle(tinta.copy(alpha = 0.5f), 0.6f * u, p + Offset(0f, r.width * 0.02f))
        }
        // La senda punteada hasta la parada.
        val senda = Path().apply {
            val a0 = q(0.18f, 0.92f); moveTo(a0.x, a0.y)
            val b1 = q(0.35f, 0.6f); val b2 = q(0.55f, 0.55f); val b3 = q(0.7f, 0.42f)
            cubicTo(b1.x, b1.y, b2.x, b2.y, b3.x, b3.y)
        }
        drawPath(senda, rojo, style = Stroke(1.1f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.4f * u))))
        val parada = q(0.7f, 0.42f)
        drawCircle(rojo, 2.6f * u, parada, style = Stroke(1f * u))
        drawCircle(rojo, 1f * u, parada)
        // La rosa de cuatro puntas, arriba a la derecha.
        val ro = q(0.84f, 0.16f); val rr = minOf(r.width, r.height) * 0.08f
        drawPath(poligonoAntiguas(ro.x, ro.y - rr, ro.x + rr * 0.22f, ro.y, ro.x, ro.y + rr, ro.x - rr * 0.22f, ro.y), tinta)
        drawPath(poligonoAntiguas(ro.x - rr, ro.y, ro.x, ro.y - rr * 0.22f, ro.x + rr, ro.y, ro.x, ro.y + rr * 0.22f), tinta.copy(alpha = 0.5f))
        drawCircle(tinta.copy(alpha = 0.5f), rr * 0.55f, ro, style = Stroke(0.6f * u))
    }

    /**
     * El reloj de arena: los dos platos torneados, tres columnillas, el vidrio con sus dos ampollas (la arena arriba, el hilo que
     * cae y el montón de abajo) y el brillo del vidrio a la luz.
     */
    private fun DrawScope.reloj(pie: Offset, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        val al = h * 0.17f; val an = h * 0.068f
        val c = Offset(pie.x, pie.y - al / 2)
        val madera = L.cosa(P.ROBLE, c); val maderaLuz = L.cosa(P.ROBLE_LUZ, c)
        // Su sombra: de día en el tablero, a la derecha; de noche, alargada hacia la izquierda (lejos del fuego).
        contactoAntiguas(Offset(pie.x + (if (L.oscuro) -h * 0.02f else h * 0.025f), pie.y), an * 1.6f, h * 0.018f, am, 0.45f, if (L.oscuro) -1f else 1f)
        // El vidrio: dos ampollas que se tocan en el cuello.
        val top = pie.y - al + h * 0.016f; val bot = pie.y - h * 0.016f; val cuello = c.y
        val ar = an * 0.36f
        val vidrio = Path().apply {
            moveTo(c.x - ar * 0.7f, top)
            cubicTo(c.x - ar * 1.15f, top + (cuello - top) * 0.5f, c.x - ar * 0.2f, cuello - (cuello - top) * 0.15f, c.x - ar * 0.08f, cuello)
            cubicTo(c.x - ar * 0.2f, cuello + (bot - cuello) * 0.15f, c.x - ar * 1.15f, cuello + (bot - cuello) * 0.5f, c.x - ar * 0.7f, bot)
            lineTo(c.x + ar * 0.7f, bot)
            cubicTo(c.x + ar * 1.15f, cuello + (bot - cuello) * 0.5f, c.x + ar * 0.2f, cuello + (bot - cuello) * 0.15f, c.x + ar * 0.08f, cuello)
            cubicTo(c.x + ar * 0.2f, cuello - (cuello - top) * 0.15f, c.x + ar * 1.15f, top + (cuello - top) * 0.5f, c.x + ar * 0.7f, top)
            close()
        }
        // La columnilla de atrás, vista por el vidrio.
        drawRect(L.cosa(P.ROBLE_OSC, c), Offset(c.x - h * 0.003f, top), Size(h * 0.006f, bot - top))
        drawPath(vidrio, L.cosa(P.CUARZO, c).copy(alpha = 0.22f))
        val arena = L.cosa(ARENA, c)
        clipPath(vidrio) {
            drawRect(arena, Offset(c.x - ar, cuello - (cuello - top) * 0.45f), Size(ar * 2, (cuello - top) * 0.45f))
            drawOval(arena, Offset(c.x - ar * 1.2f, bot - (bot - cuello) * 0.42f), Size(ar * 2.4f, (bot - cuello) * 0.6f))
            drawRect(arena, Offset(c.x - ar, bot - (bot - cuello) * 0.15f), Size(ar * 2, (bot - cuello) * 0.15f))
            drawRect(am.sombra(0.25f), Offset(c.x + ar * 0.2f, top), Size(ar, bot - top))
        }
        drawLine(arena, Offset(c.x, cuello), Offset(c.x, bot - (bot - cuello) * 0.35f), 0.8f * u)
        drawPath(vidrio, L.cosa(P.CUARZO, c).copy(alpha = 0.5f), style = Stroke(0.8f * u))
        // El brillo del vidrio: de día, el sol de la izquierda; de noche, el fuego de la derecha.
        val lado = if (L.oscuro) 1f else -1f
        drawLine(if (L.oscuro) P.FUEGO.copy(alpha = (0.9f * L.k(c)).coerceAtMost(0.8f)) else Color.White.copy(alpha = 0.75f),
            Offset(c.x + lado * ar * 0.6f, top + (cuello - top) * 0.2f), Offset(c.x + lado * ar * 0.45f, cuello - (cuello - top) * 0.3f), 1.2f * u, StrokeCap.Round)
        drawLine(if (L.oscuro) P.FUEGO.copy(alpha = (0.7f * L.k(c)).coerceAtMost(0.6f)) else Color.White.copy(alpha = 0.55f),
            Offset(c.x + lado * ar * 0.5f, cuello + (bot - cuello) * 0.3f), Offset(c.x + lado * ar * 0.62f, bot - (bot - cuello) * 0.25f), 1f * u, StrokeCap.Round)
        // Las dos columnillas de delante y los platos.
        listOf(-1f, 1f).forEach { sg ->
            val x = c.x + sg * an * 0.42f
            drawRect(madera, Offset(x - h * 0.004f, top), Size(h * 0.008f, bot - top))
            drawOval(madera, Offset(x - h * 0.007f, c.y - h * 0.012f), Size(h * 0.014f, h * 0.024f))
            drawRect(maderaLuz.copy(alpha = 0.7f), Offset(x - h * 0.003f, top), Size(h * 0.002f, bot - top))
        }
        listOf(top - h * 0.016f, bot).forEach { y ->
            drawRect(madera, Offset(c.x - an / 2, y), Size(an, h * 0.016f))
            drawRect(maderaLuz, Offset(c.x - an / 2, y), Size(an, h * 0.005f))
            drawRect(am.sombra(0.35f), Offset(c.x + an * 0.2f, y), Size(an * 0.3f, h * 0.016f))
        }
        if (L.oscuro) {
            drawLine(am.filo(0.7f * L.k(c)), Offset(c.x + an / 2, top - h * 0.016f), Offset(c.x + an / 2, pie.y), 1f * u)
            drawLine(am.lunar(0.8f), Offset(c.x - an / 2, top - h * 0.016f), Offset(c.x - an / 2, pie.y), 1f * u)
            drawLine(am.lunar(0.6f), Offset(c.x - ar * 0.7f, top + (cuello - top) * 0.2f), Offset(c.x - ar * 0.55f, cuello - (cuello - top) * 0.3f), 0.9f * u)
        }
        else drawLine(am.claro(0.5f), Offset(c.x - an / 2, top - h * 0.016f), Offset(c.x - an / 2, pie.y), 1f * u)
    }

    /**
     * Los cuencos de colores del cartógrafo (el lapislázuli molido, la malaquita y el bermellón), el pincel cruzado sobre el de en
     * medio y, detrás, el compás de puntas de pie, abierto, con su charnela de latón.
     */
    private fun DrawScope.cuencos(pie: Offset, s: SitioLibretaAntiguas, L: LuzLibretaAntiguas) {
        val h = s.h; val u = density
        val am = L.am
        // El compás, de pie detrás: las puntas en la mesa y la charnela arriba.
        val ch = Offset(pie.x + h * 0.012f, pie.y - h * 0.15f)
        val pi = Offset(pie.x - h * 0.045f, pie.y - h * 0.004f); val pd = Offset(pie.x + h * 0.062f, pie.y - h * 0.006f)
        val acero = L.cosa(P.HIERRO_LUZ, ch)
        listOf(pi, pd).forEach { p ->
            drawLine(am.sombra(0.3f), ch + Offset(h * 0.008f, h * 0.004f), p + Offset(h * 0.012f, 0f), 1.6f * u)
            drawLine(acero, ch, p, 1.7f * u, StrokeCap.Round)
            drawLine(if (L.oscuro) am.filo(0.6f * L.k(ch)) else am.claro(0.6f), ch + Offset(-0.6f * u, 0f), p + Offset(-0.6f * u, 0f), 0.5f * u)
        }
        drawCircle(L.cosa(P.LATON_OSC, ch), h * 0.011f, ch)
        drawCircle(L.cosa(P.LATON, ch), h * 0.008f, ch)
        destelloAntiguas(ch - Offset(h * 0.003f, h * 0.003f), 1f * u, am, L.k(ch))
        drawLine(L.cosa(P.LATON, ch), ch, ch - Offset(0f, h * 0.018f), 1.4f * u, StrokeCap.Round)
        // Los tres cuencos de barro, con el color molido dentro.
        listOf(AZUL_MOLIDO to -0.05f, MALAQUITA to 0f, BERMELLON to 0.05f).forEachIndexed { i, (col, dx) ->
            val c = Offset(pie.x + h * dx + h * 0.004f * i, pie.y - h * 0.002f * (i % 2))
            val an = h * 0.046f; val al = h * 0.026f
            contactoAntiguas(Offset(c.x + (if (L.oscuro) -h * 0.008f else h * 0.01f), c.y), an * 1.3f, h * 0.012f, am, 0.4f, if (L.oscuro) -1f else 1f)
            val cuerpo = Path().apply {
                moveTo(c.x - an / 2, c.y - al)
                quadraticTo(c.x - an * 0.5f, c.y, c.x, c.y)
                quadraticTo(c.x + an * 0.5f, c.y, c.x + an / 2, c.y - al)
                close()
            }
            drawPath(cuerpo, L.cosa(BARRO, c))
            clipPath(cuerpo) {
                drawRect(am.sombra(0.3f), Offset(c.x + an * 0.12f, c.y - al), Size(an * 0.4f, al))
                drawRect(if (L.oscuro) am.filo(0.35f * L.k(c)) else am.claro(0.3f), Offset(c.x - an * 0.4f, c.y - al), Size(an * 0.14f, al))
            }
            drawOval(L.cosa(mezcla(BARRO, P.ROBLE_OSC, 0.4f), c), Offset(c.x - an / 2, c.y - al - h * 0.007f), Size(an, h * 0.014f))
            drawOval(L.cosa(col, c), Offset(c.x - an * 0.42f, c.y - al - h * 0.005f), Size(an * 0.84f, h * 0.01f))
            drawOval(L.cosa(mezcla(col, Color.White, 0.3f), c), Offset(c.x - an * 0.3f, c.y - al - h * 0.004f), Size(an * 0.3f, h * 0.005f))
        }
        // El pincel, cruzado sobre el cuenco de en medio: el mango, la virola de latón y la punta teñida de azul.
        val p0 = Offset(pie.x - h * 0.03f, pie.y - h * 0.035f); val p1 = Offset(pie.x + h * 0.06f, pie.y - h * 0.045f)
        drawLine(am.sombra(0.3f), p0 + Offset(h * 0.004f, h * 0.005f), p1 + Offset(h * 0.004f, h * 0.005f), 1.8f * u, StrokeCap.Round)
        drawLine(L.cosa(Color(0xFF8A5A2E), p1), p0 + (p1 - p0) * 0.3f, p1, 1.8f * u, StrokeCap.Round)
        drawLine(L.cosa(P.LATON, p0), p0 + (p1 - p0) * 0.18f, p0 + (p1 - p0) * 0.3f, 2.2f * u)
        drawLine(L.cosa(AZUL_MOLIDO, p0), p0, p0 + (p1 - p0) * 0.18f, 2f * u, StrokeCap.Round)
    }
}
