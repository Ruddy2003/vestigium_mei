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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * Lo fijo del rincón de la libreta de Botánico ([MuebleLibretaBotanico]): la pared salvia con su paño de luz y la moldura de
 * nogal; la hiedra que trepa por su caña desde la maceta y corre por la moldura; la mesa de escribir de roble con su cajón; el
 * caballete de mesa; delante, la ramita sobre su secante; a la derecha, el tintero con su pluma y el quinqué de pantalla verde.
 * Se pinta una vez por tamaño y modo. La pinza del caballete y el listón de la repisa van aparte ([pinza]), encima del herbario.
 */
internal object RinconLibretaBotanico {
    private val P = PaletaBotanico

    fun DrawScope.rincon(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        pared(s, l)
        mesa(s, l)
        caballete(s, l)
        maceta(s, l)
        hiedra(s, l)
        manojo(s, l)
        ramita(s, l)
        tintero(s, l)
        quinque(s, l)
        if (l.oscuro) charcos(s, l)
    }

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val w = size.width; val U = s.U; val u = density
        val r = Rect(0f, 0f, w, s.mesa + 2f * U)
        if (!l.oscuro) {
            // De día, como en el Ideario: más clara arriba a la izquierda, hacia la sombra a la derecha y abajo.
            drawRect(Brush.horizontalGradient(listOf(mezcla(P.SALVIA, LUZ_MUEBLE, 0.18f), P.SALVIA, mezcla(P.SALVIA, P.SALVIA_OSC, 0.55f)), 0f, w), r.topLeft, r.size)
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), s.mesa * 0.5f, r.bottom), Offset(0f, s.mesa * 0.5f), Size(w, r.bottom - s.mesa * 0.5f))
        } else {
            // De noche: el halo del quinqué en la pared y lo lejano hundido en el verde petróleo.
            val c = Offset(s.llama.x, s.llama.y + 6f * U)
            fun k(d: Float) = l.t(c + Offset(-d * U, d * U * 0.2f)).c(P.SALVIA)
            drawRect(Brush.radialGradient(0f to k(4f), 0.1f to k(30f), 0.24f to k(70f), 0.44f to k(130f), 0.7f to k(210f), 1f to k(320f),
                center = c, radius = 320f * U), r.topLeft, r.size)
        }
        // El paño de la ventana (de día, el sol; de noche, la luna): la misma pared, un paso más clara, con su penumbra.
        val q = l.pano
        val luz = if (l.oscuro) l.t(Offset(q[0] + 30f * U, q[1] + 30f * U), enPano = true).c(P.SALVIA, 0.02f)
            else mezcla(mezcla(P.SALVIA, LUZ_MUEBLE, 0.34f), P.OCASO, 0.06f)
        val b = 5f * U
        clipRect(0f, 0f, w, r.bottom) {
            drawPath(l.camino(q, b), luz, alpha = 0.28f)
            drawPath(l.camino(q, b * 0.5f), luz, alpha = 0.42f)
            drawPath(l.camino(q), luz, alpha = 0.6f)
            drawPath(l.camino(q, -b * 0.5f), luz)
        }
        pinceladas(r, if (l.oscuro) P.NOCHE else P.SALVIA_OSC, 9601, 38, u, 0.1f)
        pinceladas(r, if (l.oscuro) P.NOCHE else mezcla(P.SALVIA, LUZ_MUEBLE, 0.4f), 9602, 18, u, 0.1f)
        // Una grieta fina del enlucido (en el paño, a la vista) y unos clavos viejos.
        listOf(Offset(s.tapa.left * 0.5f + 8f * U, 52f * U), Offset(w - 34f * U, 40f * U)).forEachIndexed { i, p0 ->
            val t = l.t(p0, enPano = true)
            val g = Path().apply {
                moveTo(p0.x, p0.y); var p = p0
                for (j in 0 until 5) { p += Offset(U * (3f + 4f * azar(9610 + i, j)) * (if (j % 2 == 0) 1f else -0.5f), U * (4f + 4f * azar(9620 + i, j))); lineTo(p.x, p.y) }
            }
            drawPath(g, t.s(0.26f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.22f), style = Stroke(0.5f * u)) }
        }
        listOf(Offset(w * 0.62f, 34f * U), Offset(w - 14f * U, 96f * U)).forEach { p ->
            val t = l.t(p)
            drawCircle(t.s(0.38f), 1.1f * u, p); drawCircle(t.b(0.3f), 0.7f * u, p + Offset(0.6f * u, 0.6f * u))
        }
        // La moldura de nogal para colgar cuadros: su bocel con su luz y la sombra que deja en la pared.
        val y0 = s.moldura; val y1 = s.molduraAbajo
        drawRect(l.aLoAncho(0f, w, y1) { it.s(0.3f) }, Offset(0f, y1), Size(w, 2.6f * U))
        drawRect(l.aLoAncho(0f, w, y1) { it.s(0.12f) }, Offset(0f, y1 + 2.6f * U), Size(w, 3.4f * U))
        drawRect(l.aLoAncho(0f, w, y0) { it.c(P.NOGAL, 0.08f) }, Offset(0f, y0), Size(w, y1 - y0))
        drawRect(l.aLoAncho(0f, w, y0) { it.c(mezcla(P.NOGAL, P.ROBLE, 0.4f), 0.1f) }, Offset(0f, y0 + (y1 - y0) * 0.18f), Size(w, (y1 - y0) * 0.36f))
        drawRect(l.aLoAncho(0f, w, y0) { it.c(P.NOGAL_OSC) }, Offset(0f, y1 - (y1 - y0) * 0.28f), Size(w, (y1 - y0) * 0.28f))
        val tm = l.t(Offset(w * 0.3f, y0))
        drawRect(tm.b(0.45f), Offset(0f, y0 + (y1 - y0) * 0.2f), Size(w, 0.8f * u))
        drawRect(tm.s(0.3f), Offset(0f, y0), Size(w, 0.8f * u))
    }

    // ---------------------------------------------------------------- la mesa de escribir

    private fun DrawScope.mesa(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val w = size.width; val h = size.height; val U = s.U; val u = density
        // El tablero, visto desde un poco arriba: más claro delante, con sus vetas; el roble claro del gabinete.
        drawRect(l.aLoAncho(0f, w, (s.mesa + s.frente) / 2, true) { it.c(P.ROBLE, -0.04f) }, Offset(0f, s.mesa), Size(w, s.frente - s.mesa))
        drawRect(l.aLoAncho(0f, w, s.mesa, true) { it.s(0.32f) }, Offset(0f, s.mesa), Size(w, 3f * U))
        drawRect(l.aLoAncho(0f, w, s.mesa, true) { it.s(0.12f) }, Offset(0f, s.mesa + 3f * U), Size(w, 5f * U))
        // El paño de la ventana también en el tablero, con su penumbra (de día).
        if (!l.oscuro) {
            val q = l.panoMesa
            val luz = mezcla(mezcla(P.ROBLE, LUZ_MUEBLE, 0.3f), P.OCASO, 0.06f)
            clipRect(0f, s.mesa, w, s.frente) {
                drawPath(l.camino(q, 5f * U), luz, alpha = 0.3f)
                drawPath(l.camino(q, 2.5f * U), luz, alpha = 0.45f)
                drawPath(l.camino(q), luz, alpha = 0.55f)
            }
        }
        val tt = l.t(Offset(w * 0.5f, s.mesa + 10f * U), true)
        vetas(Rect(0f, s.mesa + 2f * U, w, s.frente - 1f * U), tt.c(P.ROBLE_OSC).copy(alpha = 0.35f), 9630, 0.8f, grosor = 0.6f, ondulacion = 0.5f)
        // El canto de delante, con su bocel claro, y la sombra que deja en el faldón.
        drawRect(l.aLoAncho(0f, w, s.frente, true) { it.c(P.ROBLE_LUZ, 0.04f) }, Offset(0f, s.frente), Size(w, (s.canto - s.frente) * 0.5f))
        drawRect(l.aLoAncho(0f, w, s.frente, true) { it.c(P.ROBLE, -0.02f) }, Offset(0f, s.frente + (s.canto - s.frente) * 0.5f), Size(w, (s.canto - s.frente) * 0.5f))
        drawRect(tt.b(0.55f), Offset(0f, s.frente), Size(w, 1f * u))
        drawRect(tt.s(0.35f), Offset(0f, s.canto - 1f * u), Size(w, 1f * u))
        // El faldón con el cajón: el marco, el frente del cajón con su luz arriba y los dos tiradores de latón.
        drawRect(l.aLoAncho(0f, w, s.canto, true) { it.c(P.ROBLE, -0.12f) }, Offset(0f, s.canto), Size(w, h - s.canto))
        drawRect(tt.s(0.4f), Offset(0f, s.canto), Size(w, 3f * U))
        val cajon = Rect(s.cx - 92f * U, s.canto + 8f * U, s.cx + 92f * U, h + 4f * U)
        drawRect(tt.s(0.35f), cajon.topLeft - Offset(1.4f * u, 1.4f * u), Size(cajon.width + 2.8f * u, cajon.height))
        drawRect(l.aLoAncho(cajon.left, cajon.right, cajon.top, true) { it.c(P.ROBLE, -0.06f) }, cajon.topLeft, cajon.size)
        drawRect(tt.b(0.4f), cajon.topLeft, Size(cajon.width, 1f * u))
        drawRect(tt.b(0.25f), cajon.topLeft, Size(1f * u, cajon.height))
        vetas(Rect(cajon.left, cajon.top + 2f * U, cajon.right, h), tt.c(P.ROBLE_OSC).copy(alpha = 0.3f), 9631, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        listOf(cajon.left + cajon.width * 0.25f, cajon.right - cajon.width * 0.25f).forEach { x ->
            val p = Offset(x, cajon.top + 14f * U)
            val t = l.t(p, true)
            drawCircle(t.s(0.4f), 4.2f * U, p + Offset(1.4f * u, 2f * u))
            drawCircle(t.c(P.LATON_OSC), 4.2f * U, p)
            drawCircle(t.c(P.LATON), 3.2f * U, p)
            drawCircle(t.b(0.7f), 1.1f * U, p - Offset(1.2f * U, 1.2f * U))
        }
    }

    // ---------------------------------------------------------------- el caballete

    /** Las patas, el mástil y la repisa del caballete (detrás del herbario); el listón y la pinza van en [pinza]. */
    private fun DrawScope.caballete(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val mx = s.cx + 10f * U
        val t = l.t(Offset(s.cx, s.repisa), true)
        // La sombra del caballete en la pared, a la derecha (de día).
        if (!l.oscuro) {
            translate(9f * U, 5f * U) {
                drawLine(t.s(0.16f), Offset(mx, s.punta + 6f * U), Offset(s.cx + 104f * U, s.mesa - 2f * U), 5f * U)
                drawRect(t.s(0.14f), Offset(s.repisaIzq, s.repisa), Size(s.repisaDer - s.repisaIzq, s.repisaAbajo - s.repisa))
            }
        }
        // La pata de atrás, más honda, y su pie junto a la pared.
        drawLine(t.c(P.ROBLE_OSC, -0.2f), Offset(mx, s.punta + 4f * U), Offset(mx + 28f * U, s.mesa + 6f * U), 4.4f * U, StrokeCap.Round)
        // Las dos patas de delante, abiertas, con su canto de luz (a la izquierda) y su sombra.
        listOf(-1f, 1f).forEach { lado ->
            val a = Offset(mx + lado * 3f * U, s.punta + 2f * U)
            val b = Offset(s.cx + lado * (if (lado < 0f) 96f else 104f) * U, s.pie)
            val tp = l.t(b, true)
            drawLine(tp.c(P.ROBLE, -0.02f), a, b, 5.4f * U, StrokeCap.Round)
            drawLine(tp.b(0.45f), a - Offset(1.6f * U, 0f), b - Offset(1.6f * U, 0f), 1.1f * u)
            drawLine(tp.s(0.35f), a + Offset(1.8f * U, 0f), b + Offset(1.8f * U, 0f), 1.4f * u)
            contactoBotanico(b + Offset(2f * U, 1f * U), 14f * U, 4f * U, tp, 0.4f)
            if (l.oscuro && lado < 0f) drawLine(P.LUNA_RAS.copy(alpha = 0.35f), a - Offset(2.4f * U, 0f), b - Offset(2.4f * U, 0f), 0.9f * u)
        }
        // El mástil, que sube por detrás del herbario hasta la punta, con su travesaño.
        drawRect(l.aLoAlto(s.punta, s.repisa, mx, true) { it.c(P.ROBLE, 0.02f) }, Offset(mx - 3.4f * U, s.punta), Size(6.8f * U, s.repisa - s.punta))
        drawRect(t.b(0.4f), Offset(mx - 3.4f * U, s.punta), Size(1.2f * u, s.repisa - s.punta))
        drawRect(t.s(0.3f), Offset(mx + 2.2f * U, s.punta), Size(1.2f * U, s.repisa - s.punta))
        drawRoundRect(t.c(P.ROBLE_OSC), Offset(mx - 4.5f * U, s.punta - 2f * U), Size(9f * U, 4f * U), CornerRadius(2f * U))
        // La repisa: su tablero visto desde arriba y su cara, con los dos tacos que la sujetan a las patas.
        val rp = Rect(s.repisaIzq, s.repisa - 4f * U, s.repisaDer, s.repisaAbajo)
        drawRect(t.s(0.3f), Offset(rp.left + 2f * U, rp.bottom), Size(rp.width, 3f * U))
        drawRect(l.aLoAncho(rp.left, rp.right, rp.top, true) { it.c(P.ROBLE_LUZ, -0.02f) }, rp.topLeft, Size(rp.width, 4f * U))
        drawRect(l.aLoAncho(rp.left, rp.right, rp.bottom, true) { it.c(P.ROBLE, -0.04f) }, Offset(rp.left, s.repisa), Size(rp.width, rp.bottom - s.repisa))
        drawRect(t.b(0.5f), Offset(rp.left, s.repisa), Size(rp.width, 0.9f * u))
        drawRect(t.s(0.4f), Offset(rp.left, rp.bottom - 1.2f * u), Size(rp.width, 1.2f * u))
        drawRect(t.b(0.3f), Offset(rp.left, rp.top), Size(1f * u, rp.height))
        drawRect(t.s(0.35f), Offset(rp.right - 1.4f * u, rp.top), Size(1.4f * u, rp.height))
        if (l.oscuro) drawRect(P.LUNA_RAS.copy(alpha = 0.3f), Offset(rp.left, rp.top), Size(1.2f * u, rp.height))
    }

    /** Encima del herbario: el listón de la repisa que sujeta su pie y la pinza de tornillo del mástil que lo prende por arriba. */
    fun DrawScope.pinza(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val t = l.t(Offset(s.cx, s.repisa), true)
        // El listón, que asoma por delante del pie de los pliegos.
        val li = Rect(s.repisaIzq, s.repisa - 4.5f * U, s.repisaDer, s.repisa + 1.5f * U)
        drawRect(t.s(0.35f), Offset(li.left, li.bottom), Size(li.width, 1.6f * U))
        drawRect(l.aLoAncho(li.left, li.right, li.top, true) { it.c(P.ROBLE_LUZ, 0.02f) }, li.topLeft, li.size)
        drawRect(t.b(0.5f), li.topLeft, Size(li.width, 0.9f * u))
        drawRect(t.s(0.3f), Offset(li.left, li.bottom - 1f * u), Size(li.width, 1f * u))
        if (l.oscuro) {
            drawRect(P.LLAMA.copy(alpha = (0.6f * l.k(Offset(li.right, li.top))).coerceAtMost(0.5f)), Offset(li.right - 40f * U, li.top), Size(40f * U, 0.9f * u))
            drawRect(P.LUNA_RAS.copy(alpha = 0.35f), Offset(li.left, li.top), Size(1.2f * u, li.height))
        }
        // La pinza: el taco de roble que baja por el mástil y muerde la tapa, y el tornillo de mariposa de latón.
        val p = s.pinza
        val tp = l.t(p, true)
        val taco = Rect(p.x - 7f * U, p.y - 9f * U, p.x + 7f * U, p.y + 6f * U)
        drawRect(tp.s(0.35f), taco.topLeft + Offset(1.6f * u, 2.2f * u), taco.size)
        drawRect(tp.c(P.ROBLE, 0.04f), taco.topLeft, taco.size)
        drawRect(tp.b(0.45f), taco.topLeft, Size(taco.width, 1f * u))
        drawRect(tp.b(0.3f), taco.topLeft, Size(1f * u, taco.height))
        drawRect(tp.s(0.3f), Offset(taco.right - 1.6f * u, taco.top), Size(1.6f * u, taco.height))
        drawRect(tp.s(0.25f), Offset(taco.left, taco.bottom - 4f * U), Size(taco.width, 1f * u))
        val tor = Offset(p.x, taco.top + 4f * U)
        drawOval(tp.c(P.LATON_OSC), Offset(tor.x - 6.5f * U, tor.y - 2.2f * U), Size(13f * U, 4.4f * U))
        drawOval(tp.c(P.LATON), Offset(tor.x - 6f * U, tor.y - 2.2f * U), Size(12f * U, 3.6f * U))
        drawCircle(tp.c(P.LATON), 2.4f * U, tor)
        destelloBotanico(tor - Offset(1.4f * U, 1f * U), 0.9f * U, tp)
    }

    // ---------------------------------------------------------------- la hiedra

    private fun DrawScope.maceta(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val m = s.maceta
        val t = l.t(Offset(m.x, m.y - 18f * U))
        val top = m.y - 36f * U
        val cuerpo = poligonoBotanico(m.x - 21f * U, top + 7f * U, m.x + 21f * U, top + 7f * U, m.x + 15f * U, m.y, m.x - 15f * U, m.y)
        contactoBotanico(Offset(m.x + 5f * U, m.y), 44f * U, 7f * U, t, 0.42f)
        if (!l.oscuro) translate(12f * U, 5f * U) { drawPath(cuerpo, t.s(0.14f)) }
        drawPath(cuerpo, t.c(P.TERRACOTA, -0.02f))
        clipPath(cuerpo) {
            drawRect(t.b(0.22f), Offset(m.x - 21f * U, top), Size(12f * U, 40f * U))
            drawRect(t.b(0.3f), Offset(m.x - 14f * U, top), Size(2.4f * U, 40f * U))
            drawRect(t.s(0.26f), Offset(m.x + 5f * U, top), Size(17f * U, 40f * U))
            drawRect(t.s(0.12f), Offset(m.x - 21f * U, m.y - 8f * U), Size(42f * U, 8f * U))
            // El salitre blanquecino del barro, en una banda despareja.
            drawRect(t.c(mezcla(P.TERRACOTA, P.CRUDO, 0.45f)).copy(alpha = 0.35f), Offset(m.x - 21f * U, top + 14f * U), Size(42f * U, 2.6f * U))
        }
        // El borde de la maceta y la tierra.
        drawRect(t.c(P.TERRACOTA, 0.04f), Offset(m.x - 23f * U, top), Size(46f * U, 8f * U))
        drawRect(t.b(0.35f), Offset(m.x - 23f * U, top), Size(46f * U, 1f * u))
        drawRect(t.s(0.3f), Offset(m.x - 23f * U, top + 8f * U - 1f * u), Size(46f * U, 1.2f * u))
        drawRect(t.s(0.24f), Offset(m.x + 8f * U, top), Size(15f * U, 8f * U))
        drawOval(t.c(mezcla(P.NOGAL_OSC, P.TINTA, 0.3f)), Offset(m.x - 20f * U, top - 2f * U), Size(40f * U, 4.6f * U))
        if (l.oscuro) drawLine(P.LUNA_RAS.copy(alpha = 0.4f), Offset(m.x - 21f * U, top + 8f * U), Offset(m.x - 15f * U, m.y), 1f * u)
    }

    /** Una hoja de hiedra: cinco lóbulos en punta, el seno del pecíolo, su lado en sombra y los nervios claros. */
    private fun DrawScope.hojaHiedra(c: Offset, r: Float, giro: Float, col: Color, t: TonoBotanico) {
        val u = density
        rotate(giro, c) {
            // Lóbulos anchos y redondeados (la hiedra común), no puntas de estrella.
            val f = Path().apply {
                moveTo(c.x, c.y + r * 0.26f)
                quadraticTo(c.x - r * 0.28f, c.y + r * 0.62f, c.x - r * 0.62f, c.y + r * 0.42f)
                quadraticTo(c.x - r * 0.95f, c.y + r * 0.24f, c.x - r * 0.88f, c.y - r * 0.1f)
                quadraticTo(c.x - r * 0.62f, c.y - r * 0.2f, c.x - r * 0.46f, c.y - r * 0.4f)
                quadraticTo(c.x - r * 0.38f, c.y - r * 0.88f, c.x, c.y - r)
                quadraticTo(c.x + r * 0.38f, c.y - r * 0.88f, c.x + r * 0.46f, c.y - r * 0.4f)
                quadraticTo(c.x + r * 0.62f, c.y - r * 0.2f, c.x + r * 0.88f, c.y - r * 0.1f)
                quadraticTo(c.x + r * 0.95f, c.y + r * 0.24f, c.x + r * 0.62f, c.y + r * 0.42f)
                quadraticTo(c.x + r * 0.28f, c.y + r * 0.62f, c.x, c.y + r * 0.26f)
                close()
            }
            drawPath(f, col)
            clipPath(f) { drawRect(t.s(0.22f), Offset(c.x, c.y - r), Size(r, r * 1.5f)) }
            val n = t.b(0.35f)
            drawLine(n, Offset(c.x, c.y + r * 0.28f), Offset(c.x, c.y - r * 0.8f), 0.6f * u)
            drawLine(n, Offset(c.x, c.y + r * 0.2f), Offset(c.x - r * 0.66f, c.y - r * 0.14f), 0.5f * u)
            drawLine(n, Offset(c.x, c.y + r * 0.2f), Offset(c.x + r * 0.66f, c.y - r * 0.14f), 0.5f * u)
        }
    }

    /**
     * La hiedra: sube de la maceta por una caña atada con rafia hasta la moldura, se enrosca en ella y corre a la derecha, y
     * deja caer dos tallos. Cada hoja con su lado en sombra y su sombra en la pared (de día).
     */
    private fun DrawScope.hiedra(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val w = size.width; val U = s.U; val u = density
        val m = s.maceta
        val caX = m.x + 4f * U
        val tc = l.t(Offset(caX, 90f * U))
        // La caña de bambú, con sus nudos y las ataduras de rafia.
        drawLine(tc.s(0.15f), Offset(caX + 8f * U, s.molduraAbajo + 6f * U), Offset(caX + 10f * U, m.y - 24f * U), 2.6f * U)
        drawLine(tc.c(P.PAJA, -0.08f), Offset(caX, s.molduraAbajo), Offset(caX + 1f * U, m.y - 37f * U), 2.6f * U)
        drawLine(tc.b(0.45f), Offset(caX - 0.8f * U, s.molduraAbajo), Offset(caX + 0.2f * U, m.y - 37f * U), 0.7f * u)
        for (j in 1 until 6) { val y = s.molduraAbajo + (m.y - 37f * U - s.molduraAbajo) * j / 6f; drawLine(tc.c(P.PAJA, -0.25f), Offset(caX - 1.6f * U, y), Offset(caX + 1.8f * U, y), 0.9f * u) }
        // Los tallos: uno que sube enroscado por la caña, otro que corre por la moldura y dos que cuelgan.
        val sube = ArrayList<Offset>()
        val pasos = 14
        for (j in 0..pasos) {
            val f = j / pasos.toFloat()
            val y = m.y - 36f * U + (s.molduraAbajo + 2f * U - (m.y - 36f * U)) * f
            sube += Offset(caX + 3.6f * U * kotlin.math.sin(f * 11f), y)
        }
        val fin = minOf(s.cx - 30f * U, w * 0.55f)
        val corre = ArrayList<Offset>()
        var x = caX
        while (x < fin) { corre += Offset(x, s.molduraAbajo + 1f * U + 1.6f * U * kotlin.math.sin(x / (9f * U))); x += 7f * U }
        val cuelga = listOf(caX + (fin - caX) * 0.45f to 46f * U, caX + (fin - caX) * 0.85f to 30f * U)
        val tallo = Path().apply {
            sube.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            corre.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            cuelga.forEach { (cx, largo) ->
                moveTo(cx, s.molduraAbajo + 1f * U)
                quadraticTo(cx + 6f * U, s.molduraAbajo + largo * 0.5f, cx + 2f * U, s.molduraAbajo + largo)
            }
        }
        if (!l.oscuro) translate(6f * U, 4f * U) { drawPath(tallo, tc.s(0.16f), style = Stroke(1.4f * U)) }
        drawPath(tallo, tc.c(mezcla(P.HOJA_OSC, P.NOGAL, 0.45f)), style = Stroke(1.2f * U, cap = StrokeCap.Round))
        // Las hojas, alternas, más grandes abajo.
        val hojas = ArrayList<Triple<Offset, Float, Float>>()
        sube.forEachIndexed { j, p -> if (j % 2 == 1 || j == pasos) hojas += Triple(p + Offset(if (j % 4 == 1) -7f * U else 7f * U, 0f), (9f - 3f * j / pasos) * U, if (j % 4 == 1) -35f else 35f) }
        corre.forEachIndexed { j, p -> if (j > 0) hojas += Triple(p + Offset(0f, if (j % 2 == 0) 6f * U else -2.5f * U), (5.6f + 1.8f * azar(9650, j)) * U, (if (j % 2 == 0) 160f else 0f) - 25f + 50f * azar(9651, j)) }
        cuelga.forEachIndexed { i, (cx, largo) ->
            for (q in 1..3) hojas += Triple(Offset(cx + 4f * U + (if (q % 2 == 0) 5f else -4f) * U, s.molduraAbajo + largo * q / 3.2f), (4.6f + q * 0.4f) * U, if (q % 2 == 0) 30f else -30f + 10f * i)
        }
        hojas.forEachIndexed { j, (c, r, g) ->
            val t = l.t(c, enPano = true)
            if (!l.oscuro) translate(5f * U, 4f * U) { drawCircle(t.s(0.12f), r * 0.75f, c) }
            val col = if (j % 3 == 0) mezcla(P.HOJA, P.HOJA_CLARA, 0.35f) else if (j % 3 == 1) P.HOJA else mezcla(P.HOJA_OSC, P.HOJA, 0.5f)
            hojaHiedra(c, r, g, t.c(col), t)
        }
        // Las ataduras de rafia a la caña.
        listOf(0.3f, 0.62f).forEach { f -> val y = s.molduraAbajo + (m.y - 37f * U - s.molduraAbajo) * f
            drawLine(tc.c(mezcla(P.PAJA, P.CRUDO, 0.5f)), Offset(caX - 2.4f * U, y), Offset(caX + 2.8f * U, y + 1.2f * U), 1.2f * u) }
    }

    /**
     * El manojo de lavanda puesto a secar, boca abajo, colgado de un clavo de la moldura a la derecha: el cordel, el atado de
     * bramante y los tallos que se abren hacia abajo con sus espigas moradas; con su sombra en la pared (de día).
     */
    private fun DrawScope.manojo(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val w = size.width; val U = s.U; val u = density
        val x = maxOf(w - 36f * U, s.tapa.right + s.atras.x + 28f * U)
        val atado = Offset(x, s.molduraAbajo + 14f * U)
        val t = l.t(Offset(x, 48f * U))
        val tallos = Path(); val puntas = ArrayList<Pair<Offset, Float>>()
        for (j in 0 until 11) {
            val an = 1.5708f + (j - 5f) * 0.075f + (azar(9660, j) - 0.5f) * 0.05f
            val largo = (44f + 12f * azar(9661, j)) * U
            val fin = Offset(atado.x + largo * kotlin.math.cos(an), atado.y + largo * kotlin.math.sin(an))
            tallos.moveTo(atado.x, atado.y); tallos.lineTo(fin.x, fin.y)
            puntas += fin to an
        }
        if (!l.oscuro) translate(8f * U, 5f * U) { drawPath(tallos, t.s(0.16f), style = Stroke(1.6f * U)) }
        drawPath(tallos, t.c(mezcla(P.HOJA_CLARA, P.PAJA, 0.4f), -0.06f), style = Stroke(0.9f * U))
        // Las espigas: granos morados en hilera a lo largo de la punta de cada tallo.
        puntas.forEachIndexed { j, (p, an) ->
            val dx = kotlin.math.cos(an); val dy = kotlin.math.sin(an)
            val col = t.c(mezcla(P.AMATISTA, P.INDIGO, 0.15f + 0.2f * azar(9662, j)))
            for (q in 0 until 6) {
                val k = (q / 6f) * 14f * U
                val c = Offset(p.x - dx * k, p.y - dy * k)
                if (!l.oscuro && q % 2 == 0) drawCircle(t.s(0.14f), 1.6f * U, c + Offset(6f * U, 4f * U))
                drawCircle(col, (1.6f - q * 0.1f) * U, c + Offset(if (q % 2 == 0) 0.8f * U else -0.8f * U, 0f))
            }
            drawCircle(t.b(0.3f), 0.6f * U, p - Offset(0.6f * U, 0.6f * U))
        }
        // El cordel al clavo, el atado de bramante y el clavo.
        drawLine(t.c(P.BRAMANTE, -0.1f), Offset(x, s.molduraAbajo - 1f * U), atado, 0.9f * u)
        clavoBotanico(Offset(x, s.molduraAbajo - 1.6f * U), 1.6f * U, t)
        val nudo = Rect(atado.x - 4.5f * U, atado.y - 2f * U, atado.x + 4.5f * U, atado.y + 6f * U)
        drawRect(t.c(P.BRAMANTE), nudo.topLeft, nudo.size)
        for (q in 0 until 3) drawLine(t.s(0.35f), Offset(nudo.left, nudo.top + nudo.height * (0.3f + 0.25f * q)), Offset(nudo.right, nudo.top + nudo.height * (0.3f + 0.25f * q)), 0.6f * u)
        drawRect(t.b(0.4f), nudo.topLeft, Size(nudo.width * 0.3f, nudo.height))
    }

    // ---------------------------------------------------------------- lo de la mesa

    /** La ramita recién cogida, sobre su secante rosado: el tallo, sus hojas y tres campanillas, esperando la prensa. */
    private fun DrawScope.ramita(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val c = s.ramita
        val t = l.t(c)
        scale(1.22f, 1.22f, c) { ramitaSola(s, c, t) }
    }

    private fun DrawScope.ramitaSola(s: SitioLibretaBotanico, c: Offset, t: TonoBotanico) {
        val U = s.U; val u = density
        rotate(-4f, c) {
            val sec = Rect(c.x - 34f * U, c.y - 6f * U, c.x + 34f * U, c.y + 5f * U)
            drawRect(t.s(0.3f), sec.topLeft + Offset(2f * U, 1.6f * U), sec.size)
            drawRect(t.c(mezcla(P.SECANTE, P.CORAL, 0.22f)), sec.topLeft, sec.size)
            drawRect(t.b(0.3f), sec.topLeft, Size(sec.width, 0.8f * u))
            drawRect(t.s(0.2f), Offset(sec.left, sec.bottom - 1.2f * U), Size(sec.width, 1.2f * U))
        }
        val tallo = Path().apply { moveTo(c.x + 34f * U, c.y + 2f * U); quadraticTo(c.x, c.y - 4f * U, c.x - 30f * U, c.y - 2f * U) }
        drawPath(Path().apply { addPath(tallo, Offset(1.4f * U, 1.6f * U)) }, t.s(0.3f), style = Stroke(1.4f * U))
        drawPath(tallo, t.c(P.HOJA_OSC), style = Stroke(1.2f * U, cap = StrokeCap.Round))
        listOf(0.2f, 0.4f, 0.6f, 0.78f).forEachIndexed { j, f ->
            val p = Offset(c.x + 34f * U - 64f * U * f, c.y + 2f * U - 5f * U * kotlin.math.sin(f * 3.1416f))
            hojaBotanico(p + Offset(0f, if (j % 2 == 0) -3f * U else 3f * U), (10f - 2f * f) * U, if (j % 2 == 0) -60f else -120f, t.c(if (j % 2 == 0) P.HOJA else P.HOJA_CLARA), t)
        }
        // Las campanillas, colgando de la punta, con su boca abierta.
        listOf(Offset(-28f, -6f), Offset(-34f, 1f), Offset(-22f, 3f)).forEachIndexed { j, d ->
            val p = c + Offset(d.x * U, d.y * U)
            val col = t.c(mezcla(P.AMATISTA, P.INDIGO, 0.2f + 0.15f * j))
            rotate(-80f + 30f * j, p) {
                drawPath(poligonoBotanico(p.x - 2f * U, p.y - 3f * U, p.x + 2f * U, p.y - 3f * U, p.x + 4.4f * U, p.y + 3.4f * U, p.x - 4.4f * U, p.y + 3.4f * U), col)
                drawOval(t.c(mezcla(P.AMATISTA, P.TINTA, 0.35f)), Offset(p.x - 4.4f * U, p.y + 2.2f * U), Size(8.8f * U, 2.4f * U))
                drawLine(t.b(0.4f), Offset(p.x - 1.4f * U, p.y - 2.4f * U), Offset(p.x - 3f * U, p.y + 2.6f * U), 0.7f * u)
            }
        }
    }

    /** El tintero de vidrio en su bandeja de nogal, con la tapa de latón abierta y la pluma apoyada. */
    private fun DrawScope.tintero(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val c = s.tintero
        val t = l.t(c + Offset(0f, -8f * U))
        val hacia = if (l.oscuro) -1f else 1f
        // La bandeja.
        val ba = Rect(c.x - 16f * U, c.y - 3f * U, c.x + 18f * U, c.y + 2f * U)
        contactoBotanico(Offset(ba.center.x + 3f * U * hacia, ba.bottom), ba.width * 1.15f, 5f * U, t, 0.4f, hacia)
        drawRect(t.c(P.NOGAL, 0.04f), ba.topLeft, ba.size)
        drawRect(t.b(0.4f), ba.topLeft, Size(ba.width, 0.9f * u))
        drawRect(t.s(0.35f), Offset(ba.left, ba.bottom - 1.2f * u), Size(ba.width, 1.2f * u))
        // El frasco cuadrado de vidrio grueso, con la tinta dentro.
        val fr = Rect(c.x - 10f * U, c.y - 18f * U, c.x + 6f * U, c.y - 3f * U)
        drawRect(t.c(P.VIDRIO_OSC, -0.05f), fr.topLeft, fr.size)
        drawRect(t.c(mezcla(P.INDIGO, P.TINTA, 0.6f)), Offset(fr.left + 1.6f * U, fr.top + 5f * U), Size(fr.width - 3.2f * U, fr.height - 6.6f * U))
        drawRect(t.b(0.5f), Offset(fr.left + 1f * U, fr.top + 1f * U), Size(1.4f * U, fr.height - 2f * U))
        drawRect(t.s(0.3f), Offset(fr.right - 3f * U, fr.top), Size(3f * U, fr.height))
        // El cuello, el aro de latón y la tapa abierta hacia atrás.
        drawRect(t.c(P.LATON), Offset(c.x - 5.4f * U, fr.top - 3f * U), Size(7f * U, 3.4f * U))
        drawPath(poligonoBotanico(c.x - 5.4f * U, fr.top - 3f * U, c.x + 1.6f * U, fr.top - 3f * U, c.x + 3.2f * U, fr.top - 10f * U, c.x - 3.8f * U, fr.top - 10f * U), t.c(P.LATON_OSC))
        drawLine(t.b(0.6f), Offset(c.x - 5f * U, fr.top - 3f * U), Offset(c.x - 3.4f * U, fr.top - 9.6f * U), 0.8f * u)
        destelloBotanico(Offset(fr.left + 2f * U, fr.top + 3f * U), 0.9f * U, t)
        // La pluma: el mango de nogal torneado y el plumín de acero, apoyada en la bandeja.
        val a = Offset(c.x - 3f * U, fr.top - 6f * U); val b = Offset(c.x + 22f * U, c.y - 1f * U)
        drawLine(t.s(0.3f), a + Offset(1.4f * U, 2f * U), b + Offset(1.4f * U, 2f * U), 2.4f * U, StrokeCap.Round)
        drawLine(t.c(P.NOGAL, 0.06f), a, b, 2.2f * U, StrokeCap.Round)
        drawLine(t.b(0.4f), a - Offset(0f, 0.8f * U), b - Offset(0f, 0.8f * U), 0.6f * u)
        drawLine(t.c(P.HIERRO, 0.1f), a, a + (a - b) * 0.18f, 1.3f * U, StrokeCap.Round)
    }

    /** El quinqué de mesa del gabinete (el del Ideario): el pie de latón, el depósito, el tubo de vidrio y la pantalla verde. */
    private fun DrawScope.quinque(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U; val u = density
        val o = l.oscuro
        val p = s.lampara; val x = p.x
        val t = l.t(Offset(x, (p.y - 37f * U)))
        val laton = t.c(P.LATON)
        val pantA = p.y - 94f * U; val pantB = p.y - 74f * U
        val pant = Path().apply {
            moveTo(x - 9f * U, pantA); lineTo(x + 9f * U, pantA)
            cubicTo(x + 15f * U, pantA + 6f * U, x + 26f * U, pantB - 7f * U, x + 29f * U, pantB)
            lineTo(x - 29f * U, pantB)
            cubicTo(x - 26f * U, pantB - 7f * U, x - 15f * U, pantA + 6f * U, x - 9f * U, pantA)
            close()
        }
        val dep = Rect(x - 12f * U, (p.y - 58f * U), x + 12f * U, (p.y - 37f * U))
        val silueta = Path().apply { addPath(pant); addOval(dep); addRect(Rect(x - 3f * U, (p.y - 37f * U), x + 3f * U, p.y)); addRect(Rect(x - 4f * U, (p.y - 115f * U), x + 4f * U, pantA)) }
        if (!o) translate(12f * U, 6f * U) { clipRect(0f, 0f, size.width, s.mesa + 1f * U) { drawPath(silueta, t.s(0.14f)) } }
        contactoBotanico(Offset(x + 3f * U, p.y + 1f * U), 40f * U, 7f * U, t, 0.45f, if (o) 1f else 1f)
        // El pie escalonado y la columna con su nudo.
        drawOval(t.c(P.LATON_OSC), Offset(x - 16f * U, p.y - 4f * U), Size(32f * U, 8f * U))
        drawOval(laton, Offset(x - 15f * U, p.y - 5.4f * U), Size(30f * U, 7f * U))
        drawRect(laton, Offset(x - 9f * U, p.y - 10f * U), Size(18f * U, 6f * U))
        drawRect(t.b(0.35f), Offset(x - 9f * U, p.y - 10f * U), Size(5f * U, 6f * U))
        drawRect(t.s(0.3f), Offset(x + 4f * U, p.y - 10f * U), Size(5f * U, 6f * U))
        val col = Rect(x - 3f * U, dep.bottom, x + 3f * U, p.y - 10f * U)
        drawRect(laton, col.topLeft, col.size)
        clipRect(col.left, col.top, col.right, col.bottom) { cilindroBotanico(col, t, -1f, 1.5f) }
        drawOval(laton, Offset(x - 6f * U, (p.y - 25f * U)), Size(12f * U, 7f * U))
        drawOval(t.s(0.3f), Offset(x - 1f * U, 173f * U), Size(7f * U, 6f * U))
        // El depósito redondo, con su banda.
        drawOval(laton, dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            cilindroBotanico(dep, t, -1f, 1.5f)
            drawRect(t.s(0.22f), Offset(dep.left, dep.center.y + dep.height * 0.2f), Size(dep.width, dep.height))
        }
        drawLine(t.c(P.LATON_OSC), Offset(dep.left + 1f * U, dep.center.y), Offset(dep.right - 1f * U, dep.center.y), 1f * U)
        destelloBotanico(Offset(x - 6f * U, dep.top + 6f * U), 1.6f * U, t)
        // El mechero con su galería y la llave.
        drawRect(laton, Offset(x - 7f * U, (p.y - 65f * U)), Size(14f * U, 7f * U))
        clipRect(x - 7f * U, (p.y - 65f * U), x + 7f * U, (p.y - 58f * U)) { cilindroBotanico(Rect(x - 7f * U, (p.y - 65f * U), x + 7f * U, (p.y - 58f * U)), t, -1f, 1.4f) }
        drawCircle(laton, 2.2f * U, Offset(x + 9f * U, (p.y - 61f * U)))
        // El tubo de vidrio, que pasa por la pantalla.
        val tubo = Rect(x - 4f * U, (p.y - 115f * U), x + 4f * U, (p.y - 65f * U))
        drawRect(t.c(P.VIDRIO).copy(alpha = if (o) 0.45f else 0.3f), tubo.topLeft, tubo.size)
        drawLine(if (o) P.LLAMA.copy(alpha = 0.6f) else t.b(0.7f), Offset(tubo.left + 2f * U, tubo.top + 2f * u), Offset(tubo.left + 2f * U, tubo.bottom - 2f * u), 0.9f * u)
        drawRect(t.s(0.2f), Offset(tubo.right - 2f * U, tubo.top), Size(2f * U, tubo.height))
        if (o) {
            // La llama, dentro del tubo, bajo la pantalla.
            val lc = s.llama
            drawCircle(P.LLAMA.copy(alpha = 0.35f), 14f * U, lc)
            drawOval(P.LLAMA, Offset(lc.x - 2.6f * U, lc.y - 6f * U), Size(5.2f * U, 10f * U))
            drawOval(P.FUEGO, Offset(lc.x - 1.3f * U, lc.y - 3.4f * U), Size(2.6f * U, 5.4f * U))
        }
        // La pantalla de vidrio verde, con su borde blanco y el aro de latón.
        drawPath(pant, if (o) mezcla(P.VERDE, P.VIDRIO_VERDE, 0.55f) else t.c(P.VERDE))
        clipPath(pant) {
            if (o) {
                drawRect(P.VIDRIO_VERDE.copy(alpha = 0.5f), Offset(x - 18f * U, pantA), Size(20f * U, pantB - pantA))
                drawRect(P.NOCHE.copy(alpha = 0.25f), Offset(x + 12f * U, pantA), Size(18f * U, pantB - pantA))
            } else {
                drawRect(t.b(0.32f), Offset(x - 22f * U, pantA), Size(10f * U, pantB - pantA))
                drawRect(t.b(0.5f), Offset(x - 16f * U, pantA), Size(2.6f * U, pantB - pantA))
                drawRect(t.s(0.26f), Offset(x + 8f * U, pantA), Size(22f * U, pantB - pantA))
            }
        }
        drawOval(if (o) P.FUEGO else t.c(mezcla(P.PAPEL, P.SALVIA, 0.25f)), Offset(x - 29f * U, pantB - 2.4f * U), Size(58f * U, 4.8f * U))
        drawArc(laton, 0f, 180f, false, Offset(x - 29f * U, pantB - 2.4f * U), Size(58f * U, 4.8f * U), style = Stroke(1.2f * u))
        drawRect(laton, Offset(x - 9.5f * U, pantA - 2.4f * U), Size(19f * U, 2.4f * U))
    }

    /** De noche: el halo del quinqué en la pared y el tablero, el verde de su pantalla arriba y el brillo de la luna en el paño. */
    private fun DrawScope.charcos(s: SitioLibretaBotanico, l: LuzLibretaBotanico) {
        val U = s.U
        val c = s.llama
        mancha(c + Offset(0f, 10f * U), 150f * U, P.LLAMA.copy(alpha = 0.1f))
        mancha(c + Offset(0f, 6f * U), 56f * U, P.LLAMA.copy(alpha = 0.2f))
        mancha(c - Offset(0f, 40f * U), 46f * U, P.VIDRIO_VERDE.copy(alpha = 0.13f))
        // El charco en el tablero, al pie del quinqué: tendido, sin borde.
        val pie = Offset(c.x, (s.mesa + s.frente) / 2)
        scale(1f, 0.22f, pie) { drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.3f), P.LLAMA.copy(alpha = 0.12f), Color.Transparent), pie, 110f * U), 110f * U, pie) }
        val q = l.pano
        mancha(Offset((q[0] + q[4]) / 2, (q[1] + q[5]) / 2), 50f * U, luzMueble(0.04f))
    }
}
