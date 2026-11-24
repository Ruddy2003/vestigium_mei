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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El rincón de la libreta de Horror cósmico ([MuebleLibretaArcano]), lo que no cambia: la pared encalada de azulete con su
 * humedad, la repisa alta con los fardos de periódicos, el morral colgado, la mesa de teca con su cajón, el atril, la lámpara de
 * estudiante, el tazón de lata, el bote de engrudo y las tijeras. Se pinta una vez por tamaño y modo.
 */
internal object RinconLibretaArcano {
    private val P = PaletaArcano

    /** La cal de azulete de las casas de pescadores y su sombra. */
    val AZULETE = Color(0xFFA9C6C1)
    val AZULETE_OSC = Color(0xFF7C9E9A)
    /** El vidrio verde de la pantalla, por fuera y por dentro (opalina blanca). */
    val PANTALLA = Color(0xFF2E6A48)
    val PANTALLA_LUZ = Color(0xFF6AAE84)
    val OPALINA = Color(0xFFEDEBDD)
    /** El esmalte del tazón y su filete azul. */
    val ESMALTE = Color(0xFFE6E2D2)
    val ESMALTE_AZUL = Color(0xFF2E4E7A)

    fun DrawScope.rincon(s: SitioLibretaArcano, a: AireArcano, am: AireArcano) {
        pared(s, a)
        repisa(s, a, am)
        morral(s, a)
        mesa(s, am)
        lampara(s, am)
        tazon(s, am)
        bote(s, am)
        tijeras(s, am)
    }

    // ---------------------------------------------------------------- la pared

    /** La cal de azulete: de día, más clara junto a la ventana (fuera, a la izquierda); de noche, la lámpara la entibia. */
    private fun DrawScope.pared(s: SitioLibretaArcano, a: AireArcano) {
        val w = s.w; val U = s.U
        val alto = s.mesaFondo + 2f
        if (a.oscuro) {
            val radios = floatArrayOf(0f, 40f, 90f, 150f, 230f, 330f, 460f)
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 460f to a.luz(AZULETE, MuebleLibretaArcano.cerca(Offset(s.llama.x + radios[i] * U, s.llama.y + radios[i] * U * 0.3f), s, a)) },
                center = s.llama, radius = 460f * U), Offset.Zero, Size(w, alto))
            // El roce frío de la luna por la izquierda.
            drawRect(Brush.horizontalGradient(listOf(P.FRIO_NOCHE.copy(alpha = 0.5f), P.FRIO_NOCHE.copy(alpha = 0f)), 0f, w * 0.5f), Offset.Zero, Size(w * 0.5f, alto))
        } else {
            drawRect(Brush.horizontalGradient(listOf(mezcla(AZULETE, Color.White, 0.18f), AZULETE, mezcla(AZULETE, AZULETE_OSC, 0.6f)), 0f, w), Offset.Zero, Size(w, alto))
        }
        // Las manos de cal: pinceladas anchas y desiguales.
        pinceladas(Rect(0f, 0f, w, alto), s.c(a, AZULETE_OSC, Offset(w / 2, 90f * U)), 8601, 30, U, 0.14f)
        pinceladas(Rect(0f, 0f, w, alto), s.c(a, mezcla(AZULETE, Color.White, 0.4f), Offset(w / 2, 90f * U)), 8602, 20, U, 0.12f)
        // La humedad que sube desde la mesa, con su cerco de salitre, y una gotera seca bajo la repisa.
        var xh = -20f * U; var jh = 0
        while (xh < w) {
            val c = Offset(xh + 30f * U * azar(8610, jh), alto - (8f + 16f * azar(8611, jh)) * U)
            val m = irregularArcano(c, (40f + 26f * azar(8612, jh)) * U, (12f + 8f * azar(8613, jh)) * U, 8614 + jh, 14, 0.18f)
            drawPath(m, s.c(a, mezcla(AZULETE_OSC, P.SALOBRE, 0.45f), c).copy(alpha = 0.2f))
            xh += 70f * U; jh++
        }
        listOf(Offset(w * 0.3f, s.repisaAbajo + 4f * U), Offset(w * 0.68f, s.repisaAbajo + 2f * U)).forEachIndexed { i, p ->
            val m = irregularArcano(p + Offset(0f, 14f * U), 16f * U, 12f * U, 8620 + i, 10, 0.3f)
            drawPath(m, s.c(a, P.SALOBRE, p).copy(alpha = 0.2f))
            drawLine(s.c(a, P.SALOBRE, p).copy(alpha = 0.3f), p + Offset(2f * U, 20f * U), p + Offset(3f * U, (44f + 20f * i) * U), 2.4f * U, StrokeCap.Round)
        }
        // La sombra de la mesa contra la pared.
        drawRect(a.sombra(0.35f), Offset(0f, s.mesaFondo - 5f * U), Size(w, 5f * U))
    }

    // ---------------------------------------------------------------- la repisa

    /** La repisa de tabla sobre dos ménsulas, con los fardos de periódicos atados, la lata y el tintero. */
    private fun DrawScope.repisa(s: SitioLibretaArcano, a: AireArcano, am: AireArcano) {
        val w = s.w; val U = s.U
        val y0 = s.repisaArriba; val y1 = s.repisaAbajo
        // Las ménsulas (como las curvas de la librería), con su sombra en la pared.
        listOf(w * 0.17f, w * 0.79f).forEach { x ->
            val m = poligonoArcano(x - 4f * U, y1, x + 4f * U, y1, x + 4f * U, y1 + 6f * U, x - 4f * U + 2f * U, y1 + 26f * U, x - 4f * U, y1 + 26f * U)
            translate(4f * U, 4f * U) { drawPath(m, a.sombra(0.3f)) }
            drawPath(m, s.c(am, P.TECA, Offset(x, y1)))
            drawLine(s.c(am, P.TECA_LUZ, Offset(x, y1), 0.1f), Offset(x - 4f * U, y1), Offset(x - 4f * U, y1 + 26f * U), 1f * U)
        }
        // Lo de encima: los fardos de periódicos atados, la lata y el tintero.
        fardos(s, a)
        lataRepisa(s, a)
        tintero(s, a)
        // La tabla, con su canto de luz y su sombra en la pared.
        drawRect(a.sombra(0.35f), Offset(0f, y1), Size(w, 6f * U))
        drawRect(s.c(am, P.TECA, Offset(w / 2, y0)), Offset(0f, y0), Size(w, y1 - y0))
        drawRect(s.c(am, P.TECA_LUZ, Offset(w / 2, y0), 0.08f), Offset(0f, y0), Size(w, 1.6f * U))
        vetas(Rect(0f, y0, w, y1), s.c(am, P.TECA_OSC, Offset(w / 2, y0)).copy(alpha = 0.4f), 8650, densidad = 0.6f, grosor = 0.6f, ondulacion = 0.3f)
        drawRect(am.sombra(0.3f), Offset(0f, y1 - 1.6f * U), Size(w, 1.6f * U))
    }

    /** Tres fardos de periódicos doblados, uno sobre otro y algo corridos, cada uno con su vuelta de bramante. */
    private fun DrawScope.fardos(s: SitioLibretaArcano, a: AireArcano) {
        val w = s.w; val U = s.U
        var y = s.repisaArriba
        listOf(Triple(0.02f, 0.27f, 11f), Triple(0.035f, 0.255f, 10f), Triple(0.015f, 0.265f, 9f)).forEachIndexed { i, (x0, x1, alto) ->
            val r = Rect(w * x0, y - alto * U, w * x1, y)
            val c = r.center
            translate(3f * U, 2f * U) { drawRect(a.sombra(0.25f), r.topLeft, r.size) }
            drawRect(s.c(a, if (i == 1) P.MANILA else P.PAPEL, c, -0.04f), r.topLeft, r.size)
            // Los pliegos que se ven por el canto, y el sucio del borde.
            var yy = r.top + 1.6f * U
            while (yy < r.bottom - 1f * U) { drawLine(s.c(a, P.PAPEL_OSC, c).copy(alpha = 0.5f), Offset(r.left, yy), Offset(r.right - (1f + 3f * azar(8660 + i, (yy / U).toInt())) * U, yy), 0.6f * U); yy += 1.8f * U }
            drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 1f * U))
            drawRect(a.sombra(0.2f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
            // El bramante, una vuelta a lo alto con su nudo.
            val bx = r.left + r.width * (0.4f + 0.2f * i)
            drawLine(s.c(a, P.LONA_OSC, c), Offset(bx, r.top - 0.5f * U), Offset(bx + 0.5f * U, r.bottom), 1.2f * U)
            drawCircle(s.c(a, P.LONA_OSC, c), 1.6f * U, Offset(bx, r.top + 1f * U))
            y -= alto * U
        }
    }

    /** La lata de galletas, sin letrero, con la pintura gastada en el canto, y su tapa. */
    private fun DrawScope.lataRepisa(s: SitioLibretaArcano, a: AireArcano) {
        val w = s.w; val U = s.U
        val r = Rect(w * 0.56f, s.repisaArriba - 22f * U, w * 0.56f + 34f * U, s.repisaArriba)
        val c = r.center
        translate(3f * U, 2f * U) { drawRect(a.sombra(0.25f), r.topLeft, r.size) }
        drawRect(s.c(a, mezcla(P.CASCO, P.MAR, 0.3f), c), r.topLeft, r.size)
        cilindroArcano(r, a, MuebleLibretaArcano.cerca(c, s, a), -1f)
        drawRect(s.c(a, P.LATON_OSC, c), Offset(r.left, r.top + r.height * 0.4f), Size(r.width, 2f * U))
        drawRect(s.c(a, mezcla(P.CASCO, P.MAR, 0.4f), c, 0.05f), Offset(r.left - 1.4f * U, r.top - 1f * U), Size(r.width + 2.8f * U, 5f * U))
        drawRect(a.claro(0.35f), Offset(r.left - 1.4f * U, r.top - 1f * U), Size(r.width + 2.8f * U, 1f * U))
        for (j in 0 until 4) drawPath(irregularArcano(Offset(r.left + r.width * azar(8670, j), r.top + r.height * (0.3f + 0.6f * azar(8671, j))), 3f * U, 2f * U, 8672 + j, 7, 0.4f), s.c(a, P.CINC, c))
    }

    /** El tintero de vidrio verde oscuro, con su tapón. */
    private fun DrawScope.tintero(s: SitioLibretaArcano, a: AireArcano) {
        val w = s.w; val U = s.U
        val x = w * 0.56f + 50f * U
        val r = Rect(x, s.repisaArriba - 13f * U, x + 14f * U, s.repisaArriba)
        val c = r.center
        drawRect(a.sombra(0.3f), Offset(r.left + 3f * U, r.top + 2f * U), r.size)
        drawRoundRect(s.c(a, P.BOYA_OSC, c), r.topLeft, r.size, CornerRadius(2f * U))
        drawRect(s.c(a, Color(0xFF14181E), c), Offset(r.left + 1.6f * U, r.top + r.height * 0.4f), Size(r.width - 3.2f * U, r.height * 0.5f))
        drawRect(a.claro(0.5f), Offset(r.left + 1.6f * U, r.top + 2f * U), Size(1.2f * U, r.height * 0.6f))
        drawRect(s.c(a, P.CORCHO, c), Offset(c.x - 3f * U, r.top - 4f * U), Size(6f * U, 5f * U))
    }

    // ---------------------------------------------------------------- el morral

    /** El morral de lona colgado de un clavo en la pared de la izquierda, por su correa, cortado por el borde. */
    private fun DrawScope.morral(s: SitioLibretaArcano, a: AireArcano) {
        val U = s.U
        val clavo = Offset(30f * U, 66f * U)
        val c = Offset(20f * U, 120f * U)
        val bolsa = Path().apply {
            moveTo(-12f * U, 96f * U)
            cubicTo(10f * U, 92f * U, 40f * U, 94f * U, 50f * U, 100f * U)
            cubicTo(54f * U, 128f * U, 50f * U, 148f * U, 42f * U, 156f * U)
            cubicTo(20f * U, 162f * U, 0f, 160f * U, -12f * U, 156f * U)
            close()
        }
        translate(6f * U, 4f * U) { drawPath(bolsa, a.sombra(0.3f)) }
        val lona = mezcla(P.LONA, P.LONA_OSC, 0.4f)
        drawPath(bolsa, s.c(a, lona, c))
        clipPath(bolsa) {
            drawRect(a.claro(0.18f), Offset(-12f * U, 90f * U), Size(20f * U, 70f * U))
            drawRect(a.sombra(0.25f), Offset(36f * U, 90f * U), Size(20f * U, 70f * U))
            for (j in 0 until 3) drawLine(a.sombra(0.25f), Offset((4f + 14f * j) * U, 104f * U), Offset((8f + 14f * j) * U, 156f * U), 2f * U, StrokeCap.Round)
        }
        // La solapa, con su correa y su hebilla de hierro.
        val solapa = Path().apply { moveTo(-12f * U, 96f * U); cubicTo(14f * U, 92f * U, 40f * U, 94f * U, 50f * U, 100f * U); lineTo(46f * U, 124f * U); quadraticTo(18f * U, 130f * U, -12f * U, 126f * U); close() }
        translate(0f, 2.4f * U) { drawPath(solapa, a.sombra(0.3f)) }
        drawPath(solapa, s.c(a, lona, c, 0.06f))
        drawPath(solapa, s.c(a, P.LONA_OSC, c), style = Stroke(1f * U))
        drawRect(s.c(a, P.TECA_OSC, c), Offset(22f * U, 118f * U), Size(6f * U, 22f * U))
        drawRect(s.c(a, P.HIERRO_LUZ, c), Offset(20.5f * U, 126f * U), Size(9f * U, 7f * U), style = Stroke(1.4f * U))
        // La correa hasta el clavo.
        drawLine(s.c(a, P.TECA_OSC, c), Offset(44f * U, 100f * U), clavo, 3f * U, StrokeCap.Round)
        drawLine(s.c(a, P.TECA_OSC, c), Offset(-6f * U, 96f * U), clavo, 3f * U, StrokeCap.Round)
        clavoArcano(clavo, 2f * U, a, MuebleLibretaArcano.cerca(clavo, s, a), P.HIERRO_LUZ)
    }

    // ---------------------------------------------------------------- la mesa

    /** La mesa de teca: el tablero en escorzo con su veta y su mancha de tinta, el canto y el faldón con su cajón. */
    private fun DrawScope.mesa(s: SitioLibretaArcano, am: AireArcano) {
        val w = s.w; val h = s.h; val U = s.U
        val y0 = s.mesaFondo; val y1 = s.mesaCanto
        val tablero = Rect(0f, y0, w, y1)
        if (am.oscuro) {
            val radios = floatArrayOf(0f, 50f, 110f, 190f, 300f, 460f)
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 460f to am.luz(P.TECA_LUZ, MuebleLibretaArcano.cerca(Offset(s.llama.x + radios[i] * U, s.pisa), s, am)) },
                center = Offset(s.llama.x, s.pisa), radius = 460f * U), tablero.topLeft, tablero.size)
        } else drawRect(Brush.horizontalGradient(listOf(mezcla(P.TECA_LUZ, Color.White, 0.08f), P.TECA_LUZ, mezcla(P.TECA_LUZ, P.TECA, 0.6f)), 0f, w), tablero.topLeft, tablero.size)
        vetas(tablero, am.sombra(0.25f), 8700, densidad = 0.6f, grosor = 0.6f, ondulacion = 0.5f)
        // La mancha de tinta vieja y un cerco del tazón.
        drawPath(irregularArcano(Offset(w * 0.2f, y0 + 9f * U), 14f * U, 3f * U, 8701, 10, 0.4f), s.c(am, Color(0xFF1C2230), Offset(w * 0.2f, y0)).copy(alpha = 0.45f))
        drawOval(s.c(am, P.TECA_OSC, Offset(w * 0.7f, y0)).copy(alpha = 0.5f), Offset(s.tazon.x - 12f * U, y0 + 4f * U), Size(26f * U, 6f * U), style = Stroke(1f * U))
        drawRect(am.sombra(0.4f), Offset(0f, y0), Size(w, 2f * U))
        // El canto del tablero, con su luz.
        val canto = Rect(0f, y1, w, s.mesaCantoAbajo)
        drawRect(s.c(am, P.TECA, Offset(w / 2, y1)), canto.topLeft, canto.size)
        if (am.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.35f), Color.Transparent), s.llama.x - 160f * U, s.llama.x + 160f * U), canto.topLeft, canto.size)
        drawRect(s.c(am, P.TECA_LUZ, Offset(w / 2, y1), 0.15f), Offset(0f, y1), Size(w, 1.4f * U))
        drawRect(am.sombra(0.5f), Offset(0f, s.mesaCantoAbajo), Size(w, 3f * U))
        // El faldón con su cajón y el tirador de latón.
        val f0 = s.mesaCantoAbajo
        drawRect(s.c(am, P.TECA_OSC, Offset(w / 2, f0 + 20f * U)), Offset(0f, f0 + 3f * U), Size(w, h - f0))
        vetas(Rect(0f, f0, w, h), am.sombra(0.25f), 8702, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.4f)
        val cj = Rect(s.cx - 70f * U, f0 + 10f * U, s.cx + 70f * U, h + 4f * U)
        drawRect(am.sombra(0.5f), cj.topLeft, cj.size, style = Stroke(1.6f * U))
        drawRect(s.c(am, P.TECA_LUZ, cj.center, -0.1f).copy(alpha = 0.6f), Offset(cj.left + 1.6f * U, cj.top + 1.6f * U), Size(cj.width - 3.2f * U, 1f * U))
        val tir = Offset(cj.center.x, cj.top + 18f * U)
        drawRoundRect(am.sombra(0.4f), Offset(tir.x - 13f * U, tir.y - 1.6f * U), Size(28f * U, 6f * U), CornerRadius(3f * U))
        drawRoundRect(s.c(am, P.LATON, tir), Offset(tir.x - 14f * U, tir.y - 3f * U), Size(28f * U, 6f * U), CornerRadius(3f * U))
        drawRect(am.brillo(0.5f, MuebleLibretaArcano.cerca(tir, s, am)), Offset(tir.x - 12f * U, tir.y - 2.4f * U), Size(20f * U, 1f * U))
    }

    // ---------------------------------------------------------------- la lámpara

    /**
     * La lámpara de estudiante: el pie redondo y pesado, la columna, el depósito del petróleo en su brazo a la derecha y, a la
     * izquierda, el mechero con su tubo de vidrio y la pantalla de vidrio verde forrado de opalina. De noche arde: la opalina se
     * enciende, el vidrio verde brilla y la luz cae en un charco sobre la mesa.
     */
    private fun DrawScope.lampara(s: SitioLibretaArcano, am: AireArcano) {
        val U = s.U
        val px = s.lampara.x; val pie = s.lampara.y
        val k = if (am.oscuro) 1f else 0f
        // Lo de arriba se dibuja respecto de la llama (el diseño la tenía a 104 dp).
        val dy = s.llama.y - 104f * U
        val laton = s.c(am, P.LATON, Offset(px, 120f * U))
        val latonOsc = s.c(am, P.LATON_OSC, Offset(px, 120f * U))
        // El charco de luz en la mesa y en la pared (de noche).
        if (am.oscuro) {
            mancha(Offset(s.llama.x, s.pisa - 2f * U), 110f * U, P.LLAMA.copy(alpha = 0.32f))
            mancha(Offset(s.llama.x, s.llama.y + 6f * U), 60f * U, P.LLAMA.copy(alpha = 0.3f))
        } else {
            // La sombra de la lámpara en la pared, a la derecha.
            drawRect(am.sombra(0.16f), Offset(px + 6f * U, 60f * U + dy), Size(4f * U, pie - 62f * U - dy))
            drawOval(am.sombra(0.16f), Offset(s.llama.x - 22f * U, 90f * U + dy), Size(54f * U, 22f * U))
        }
        // El pie y su sombra en la mesa.
        contactoArcano(Offset(px, pie), 58f * U, 7f * U, am, 0.5f, if (am.oscuro) 1f else -1f)
        drawOval(latonOsc, Offset(px - 24f * U, pie - 7f * U), Size(48f * U, 9f * U))
        drawRect(latonOsc, Offset(px - 24f * U, pie - 11f * U), Size(48f * U, 7f * U))
        drawOval(laton, Offset(px - 24f * U, pie - 15f * U), Size(48f * U, 9f * U))
        drawOval(am.brillo(0.45f, k + 0.3f), Offset(px - 20f * U, pie - 14f * U), Size(20f * U, 3f * U))
        // La columna, con sus anillos.
        drawRect(laton, Offset(px - 2.4f * U, 64f * U + dy), Size(4.8f * U, pie - 76f * U - dy))
        drawRect(latonOsc, Offset(px + 0.8f * U, 64f * U + dy), Size(1.6f * U, pie - 76f * U - dy))
        drawRect(am.brillo(0.5f, k + 0.3f), Offset(px - 2f * U, 64f * U + dy), Size(1f * U, pie - 76f * U - dy))
        listOf(pie - 40f * U, 108f * U + dy).forEach { y -> drawRect(laton, Offset(px - 4f * U, y), Size(8f * U, 3f * U)) }
        drawCircle(laton, 4f * U, Offset(px, 62f * U + dy))
        // El depósito, a la derecha, en su brazo: un cilindro de latón con su tapa redonda.
        val dep = Rect(px + 7f * U, 82f * U + dy, px + 25f * U, 106f * U + dy)
        drawRect(laton, Offset(px, 104f * U + dy), Size(12f * U, 3f * U))
        drawRect(laton, dep.topLeft, dep.size)
        cilindroArcano(dep, am, k + 0.3f, if (am.oscuro) -1f else -1f)
        drawOval(laton, Offset(dep.left, dep.top - 3f * U), Size(dep.width, 6f * U))
        drawCircle(latonOsc, 2.4f * U, Offset(dep.center.x, dep.top - 3f * U))
        // El brazo del mechero, a la izquierda.
        drawRect(laton, Offset(s.llama.x, 110f * U + dy), Size(px - s.llama.x, 3f * U))
        drawRect(laton, Offset(s.llama.x - 7f * U, 104f * U + dy), Size(14f * U, 8f * U))
        drawRect(latonOsc, Offset(s.llama.x - 7f * U, 110f * U + dy), Size(14f * U, 2f * U))
        // El tubo de vidrio que asoma por encima de la pantalla, tiznado en la boca.
        val tubo = Rect(s.llama.x - 4.5f * U, 64f * U + dy, s.llama.x + 4.5f * U, 92f * U + dy)
        drawRect(if (am.oscuro) P.LLAMA.copy(alpha = 0.45f) else s.c(am, P.VIDRIO, tubo.center).copy(alpha = 0.4f), tubo.topLeft, tubo.size)
        drawRect(s.c(am, Color(0xFF2A2420), tubo.center).copy(alpha = 0.4f), tubo.topLeft, Size(tubo.width, 5f * U))
        drawRect(Color.White.copy(alpha = if (am.oscuro) 0.45f else 0.6f), Offset(tubo.left + 1.4f * U, tubo.top + 5f * U), Size(1.2f * U, tubo.height - 6f * U))
        drawRect(am.sombra(0.3f), tubo.topLeft, tubo.size, style = Stroke(0.8f * U))
        // La pantalla: la cúpula de vidrio verde, el borde de opalina de dentro y su aro de latón.
        val cup = Path().apply {
            moveTo(s.llama.x - 30f * U, 106f * U + dy)
            cubicTo(s.llama.x - 30f * U, 92f * U + dy, s.llama.x - 18f * U, 84f * U + dy, s.llama.x - 6f * U, 83f * U + dy)
            lineTo(s.llama.x + 6f * U, 83f * U + dy)
            cubicTo(s.llama.x + 18f * U, 84f * U + dy, s.llama.x + 30f * U, 92f * U + dy, s.llama.x + 30f * U, 106f * U + dy)
            close()
        }
        if (!am.oscuro) translate(8f * U, 6f * U) { drawPath(cup, am.sombra(0.2f)) }
        drawPath(cup, if (am.oscuro) mezcla(PANTALLA, P.LLAMA, 0.25f) else PANTALLA)
        clipPath(cup) {
            if (am.oscuro) {
                drawCircle(mezcla(PANTALLA_LUZ, P.LLAMA, 0.35f).copy(alpha = 0.8f), 22f * U, Offset(s.llama.x, 108f * U + dy))
                drawCircle(mezcla(PANTALLA_LUZ, P.FUEGO, 0.3f).copy(alpha = 0.6f), 10f * U, Offset(s.llama.x, 104f * U + dy))
            } else {
                drawRect(PANTALLA_LUZ.copy(alpha = 0.6f), Offset(s.llama.x - 24f * U, 84f * U + dy), Size(10f * U, 22f * U))
                drawRect(mezcla(PANTALLA, Color.Black, 0.3f).copy(alpha = 0.6f), Offset(s.llama.x + 14f * U, 84f * U + dy), Size(16f * U, 22f * U))
            }
            drawLine(Color.White.copy(alpha = if (am.oscuro) 0.35f else 0.55f), Offset(s.llama.x - 22f * U, 101f * U + dy), Offset(s.llama.x - 12f * U, 87f * U + dy), 1.6f * U, StrokeCap.Round)
        }
        // La boca de la pantalla: la opalina blanca por dentro (de noche, encendida).
        val boca = Rect(s.llama.x - 30f * U, 103f * U + dy, s.llama.x + 30f * U, 110f * U + dy)
        drawOval(if (am.oscuro) mezcla(OPALINA, P.FUEGO, 0.5f) else s.c(am, OPALINA, boca.center, -0.05f), boca.topLeft, boca.size)
        drawOval(s.c(am, P.LATON, boca.center), boca.topLeft, boca.size, style = Stroke(1.4f * U))
        if (am.oscuro) {
            drawOval(P.FUEGO.copy(alpha = 0.9f), Offset(s.llama.x - 9f * U, 105f * U + dy), Size(18f * U, 4f * U))
            mancha(Offset(s.llama.x, 108f * U + dy), 30f * U, P.FUEGO.copy(alpha = 0.35f))
        }
    }

    // ---------------------------------------------------------------- lo de encima de la mesa

    /** El tazón de lata esmaltado del café, con su filete azul, sus saltaduras y el asa. */
    private fun DrawScope.tazon(s: SitioLibretaArcano, am: AireArcano) {
        val U = s.U
        val p = s.tazon
        val r = Rect(p.x - 13f * U, p.y - 24f * U, p.x + 13f * U, p.y)
        val k = MuebleLibretaArcano.cerca(r.center, s, am)
        contactoArcano(Offset(p.x, p.y), 34f * U, 5f * U, am, 0.5f, if (am.oscuro) 1f else -1f)
        // El asa, a la derecha.
        drawArc(s.c(am, ESMALTE, r.center, -0.05f), -80f, 160f, false, Offset(r.right - 6f * U, r.top + 5f * U), Size(12f * U, 12f * U), style = Stroke(2.6f * U))
        drawRect(s.c(am, ESMALTE, r.center), r.topLeft, r.size)
        cilindroArcano(r, am, k, if (am.oscuro) 1f else -1f, 1.2f)
        drawRect(s.c(am, ESMALTE_AZUL, r.center), Offset(r.left, r.top), Size(r.width, 2.4f * U))
        drawOval(s.c(am, Color(0xFF2A1E14), r.center), Offset(r.left + 1f * U, r.top - 2f * U), Size(r.width - 2f * U, 4f * U))
        drawOval(s.c(am, ESMALTE_AZUL, r.center), Offset(r.left, r.top - 2.4f * U), Size(r.width, 4.8f * U), style = Stroke(1.2f * U))
        // Las saltaduras del esmalte, negras.
        listOf(Offset(0.3f, 0.55f), Offset(0.7f, 0.8f), Offset(0.15f, 0.25f)).forEachIndexed { i, o ->
            drawPath(irregularArcano(Offset(r.left + r.width * o.x, r.top + r.height * o.y), (1.6f + 1f * i) * U, 1.4f * U, 8720 + i, 7, 0.4f), s.c(am, Color(0xFF1A1A1E), r.center))
        }
        if (am.oscuro) drawRect(P.LLAMA.copy(alpha = 0.5f * k), Offset(r.right - 1.4f * U, r.top), Size(1.4f * U, r.height))
    }

    /** El bote de engrudo de barro vidriado, con su pincel plano metido, a la izquierda del diario. */
    private fun DrawScope.bote(s: SitioLibretaArcano, am: AireArcano) {
        val U = s.U
        val p = s.bote
        val r = Rect(p.x - 13f * U, p.y - 22f * U, p.x + 13f * U, p.y)
        val k = MuebleLibretaArcano.cerca(r.center, s, am)
        val f = MuebleLibretaArcano.frio(r.center, s)
        contactoArcano(Offset(p.x, p.y), 34f * U, 5f * U, am, 0.5f)
        // El pincel, metido y apoyado en el borde.
        drawLine(s.c(am, P.TECA, r.center), Offset(p.x + 2f * U, r.top + 6f * U), Offset(p.x + 12f * U, r.top - 26f * U), 3f * U, StrokeCap.Round)
        drawRect(s.c(am, P.CINC, r.center), Offset(p.x + 1f * U, r.top - 2f * U), Size(5f * U, 7f * U))
        val barro = mezcla(P.CORCHO, P.TECA, 0.35f)
        drawRect(s.c(am, barro, r.center), r.topLeft, r.size)
        cilindroArcano(r, am, k, if (am.oscuro) 1f else -1f)
        drawRect(s.c(am, mezcla(P.CASCO, P.MAR_LUZ, 0.4f), r.center), Offset(r.left, r.top), Size(r.width, r.height * 0.45f))
        drawOval(s.c(am, P.YESO, r.center, 0.05f), Offset(r.left + 1f * U, r.top - 2.4f * U), Size(r.width - 2f * U, 5f * U))
        drawOval(s.c(am, mezcla(P.CASCO, P.MAR_LUZ, 0.4f), r.center), Offset(r.left, r.top - 2.4f * U), Size(r.width, 5f * U), style = Stroke(1.2f * U))
        // Una chorrera de engrudo seca.
        drawLine(s.c(am, P.YESO, r.center).copy(alpha = 0.8f), Offset(r.left + 6f * U, r.top + 1f * U), Offset(r.left + 6.6f * U, r.top + 9f * U), 2.4f * U, StrokeCap.Round)
        if (am.oscuro && f > 0.05f) drawRect(am.frio(0.7f * f), Offset(r.left, r.top), Size(1.2f * U, r.height))
    }

    /** Las tijeras abiertas, tumbadas en el canto de la mesa, con sus ojos de distinto tamaño (de costura). */
    private fun DrawScope.tijeras(s: SitioLibretaArcano, am: AireArcano) {
        val U = s.U
        val eje = Offset(s.libro.left - 26f * U, s.mesaCanto - 3f * U)
        val k = MuebleLibretaArcano.cerca(eje, s, am)
        val hierro = s.c(am, P.CINC, eje, -0.1f)
        rotate(-8f, eje) {
            translate(1.6f * U, 2.4f * U) {
                drawLine(am.sombra(0.4f), eje, eje + Offset(34f * U, -6f * U), 2.6f * U, StrokeCap.Round)
                drawLine(am.sombra(0.4f), eje, eje + Offset(32f * U, 4f * U), 2.6f * U, StrokeCap.Round)
            }
            drawLine(hierro, eje, eje + Offset(34f * U, -6f * U), 2.6f * U, StrokeCap.Round)
            drawLine(hierro, eje, eje + Offset(32f * U, 4f * U), 2.6f * U, StrokeCap.Round)
            drawLine(am.brillo(0.6f, k + 0.3f), eje + Offset(4f * U, -1.6f * U), eje + Offset(30f * U, -6f * U), 0.8f * U)
            drawOval(hierro, Offset(eje.x - 20f * U, eje.y - 9f * U), Size(15f * U, 9f * U), style = Stroke(2.2f * U))
            drawOval(hierro, Offset(eje.x - 13f * U, eje.y + 1f * U), Size(11f * U, 7f * U), style = Stroke(2.2f * U))
            drawCircle(s.c(am, P.LATON, eje), 1.8f * U, eje)
        }
    }

    /** Lo de delante del diario: el listón del atril donde apoya, con su canto de luz y su sombra en la mesa. */
    fun DrawScope.delante(s: SitioLibretaArcano, am: AireArcano) {
        val U = s.U
        val r = s.atril
        drawRect(am.sombra(0.45f), Offset(r.left + 3f * U, r.bottom), Size(r.width, 3f * U))
        drawRect(s.c(am, P.TECA, r.center), r.topLeft, r.size)
        drawRect(s.c(am, P.TECA_LUZ, r.center, 0.1f), r.topLeft, Size(r.width, 1.4f * U))
        drawRect(am.sombra(0.35f), Offset(r.left, r.bottom - 2f * U), Size(r.width, 2f * U))
        if (am.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, P.LLAMA.copy(alpha = 0.4f)), r.left, r.right), r.topLeft, Size(r.width, 1.6f * U))
        listOf(r.left + 6f * U, r.right - 6f * U).forEach { x -> clavoArcano(Offset(x, r.center.y), 1.4f * U, am, MuebleLibretaArcano.cerca(Offset(x, r.center.y), s, am), P.COBRE) }
    }
}
