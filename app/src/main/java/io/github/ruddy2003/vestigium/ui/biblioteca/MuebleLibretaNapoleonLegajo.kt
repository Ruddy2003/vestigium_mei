package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El legajo de despachos de la libreta de Napoleónico ([MuebleLibretaNapoleon]), en vivo: su sombra en el atril; la cubierta de
 * atrás y el bloque de los pliegos doblados (su canto de abajo, que pisa el listón, y el de la derecha, con una raya por despacho
 * y los pliegos que asoman con su lacre); la cubierta de delante, de vitela flexible con su doblez junto al lomo, con la cartela
 * rayada a tinta donde van el nombre y la cuenta, y el último despacho metido bajo la cinta; la cinta de balduque carmesí con su
 * nudo, cuyos cabos cuelgan por delante del listón. Al abrirse, el nudo se suelta y la cubierta gira sobre el lomo: dentro, el
 * despacho de encima con su membrete, como la hoja de la app.
 */
internal object LegajoLibretaNapoleon {
    private val P = PaletaNapoleon
    /** La vitela de las cubiertas: su cara y su sombra (la de los dobleces y el canto gastado). */
    val VITELA = Color(0xFFEBDFBF)
    val VITELA_OSC = Color(0xFFBFA77A)
    /** La cinta de seda carmesí: su cara, su brillo y su sombra. */
    val CINTA = Color(0xFFB0233A)
    val CINTA_LUZ = Color(0xFFE0606A)
    val CINTA_OSC = Color(0xFF5E0E1C)
    /** El papel pizarra de las carpetas del archivo (el de la cabecera de las tarjetas del índice). */
    val PIZARRA = Color(0xFF52707A)
    /** Los papeles de los despachos: el de hilo, el azulado, el ahuesado y el crudo. */
    private val PAPELES = listOf(Color(0xFFF1E6CC), Color(0xFFDCDCD0), Color(0xFFE2CD9E), Color(0xFFF6EEDA))

    /** El tiempo de abrirse: la primera parte suelta el lazo; el resto abre la cubierta. Devuelve (lazo, cubierta). */
    private fun tiempos(a: Float): Pair<Float, Float> {
        val x = a.coerceIn(0f, 1f)
        return (x / 0.3f).coerceIn(0f, 1f) to ((x - 0.15f) / 0.85f).coerceIn(0f, 1f)
    }

    private fun k(p: Offset, s: SitioLibretaNapoleon, a: AireNapoleon) = MuebleLibretaNapoleon.cerca(p, s, a)

    /** Un degradado de izquierda a derecha de [col] según la luz de la vela a lo ancho de [r] (de día, un color solo). */
    private fun pincel(col: Color, f: Float, r: Rect, s: SitioLibretaNapoleon, a: AireNapoleon): Brush {
        if (!a.oscuro) { val c = a.dia(col, f); return Brush.horizontalGradient(listOf(c, c), r.left, r.right) }
        val y = r.center.y
        return Brush.horizontalGradient(listOf(a.dia(col, f, k(Offset(r.left, y), s, a)), a.dia(col, f, k(Offset(r.center.x, y), s, a)), a.dia(col, f, k(Offset(r.right, y), s, a))), r.left, r.right)
    }

    /**
     * [a], el aire del papel y la cinta (de noche se hunden en el azul del cuarto); [ac], el del atril (en la caoba honda).
     */
    fun DrawScope.legajo(e: EstadoLibreta, s: SitioLibretaNapoleon, a: AireNapoleon, ac: AireNapoleon, medidor: TextMeasurer) {
        val U = s.U
        val t = s.tapa
        val (lazo, abre) = tiempos(e.abierto)
        val m = MuebleLibretaNapoleon.mancha(s, a)
        // La sombra del legajo en la tabla del atril: de día, abajo a la derecha; de noche, la vela la echa a la izquierda.
        clipRect(s.atrilIzq, 0f, s.atrilDer, s.liston) {
            val d = if (a.oscuro) Offset(-10f * U, 3f * U) else Offset(7f * U, 5f * U)
            drawRect(ac.sombra(if (a.oscuro) 0.45f else 0.3f), t.topLeft + d, Size(t.width + s.atras.x, t.height + s.atras.y))
            drawRect(ac.sombra(0.16f), t.topLeft + d * 1.8f, Size(t.width + s.atras.x, t.height + s.atras.y))
        }
        bloque(e, s, a)
        if (abre > 0.01f) despachoDeEncima(s, a)
        val sx = 1f - 0.94f * abre
        scale(sx, 1f, Offset(t.left, t.center.y)) {
            cubierta(s, a, null)
            if (!a.oscuro) clipPath(m.forma(1f, -14f * U)) { cubierta(s, a, m) }
            rotulo(e, s, a, medidor)
            pliego(e, s, a)
            if (abre > 0f) drawPath(silueta(t, U), a.sombra(0.5f * abre))
        }
        if (abre > 0.01f) {
            val borde = t.left + t.width * sx
            for (j in 0 until 3) drawRect(a.sombra(0.12f * (3 - j) / 3f), Offset(borde, t.top), Size((3f + 4f * j) * U, t.height))
        }
        cinta(s, a, null, lazo)
        if (!a.oscuro) clipPath(m.forma(1f, -14f * U)) { cinta(s, a, m, lazo) }
        // De noche: el filo de la vela en el canto de la derecha y el roce frío de la luna en el de la izquierda.
        if (a.oscuro) {
            val kc = k(Offset(t.right, t.center.y), s, a)
            if (abre < 0.5f) drawLine(a.filo(0.7f * kc), Offset(t.right + s.atras.x - 0.8f * U, t.top + s.atras.y + 2f * U), Offset(t.right + s.atras.x - 0.8f * U, t.bottom + s.atras.y - 1f * U), 1.4f * U)
            drawLine(a.lunar(0.3f), Offset(t.left + 0.8f * U, t.top + 3f * U), Offset(t.left + 0.8f * U, t.bottom - 3f * U), 1.2f * U)
        }
    }

    /**
     * La cubierta de atrás y el bloque de los pliegos, que sobresale de la vitela por la derecha y por arriba: su cara de delante
     * (lo que asoma), su canto de abajo (pisa el listón) y el de la derecha, con una raya por despacho y algunos de otro papel; los
     * pliegos que asoman más que los otros, algunos con su lacre partido (más cuantos más despachos).
     */
    private fun DrawScope.bloque(e: EstadoLibreta, s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val t = s.tapa; val b = s.pliegos; val v = s.atras
        val kd = k(Offset(b.right, b.center.y), s, a)
        // La cubierta de atrás, que vuela un poco por debajo.
        drawRoundRect(pincel(VITELA_OSC, -0.05f, t, s, a), Offset(t.left + v.x, t.top + v.y + 1.5f * U), t.size, CornerRadius(3f * U))
        // El bloque entero (la cara de delante y los dos cantos) y la sombra de sus cantos.
        val todo = poligonoNapoleon(b.left, b.top, b.right, b.top, b.right + v.x, b.top + v.y, b.right + v.x, b.bottom + v.y, b.left + v.x, b.bottom + v.y, b.left, b.bottom)
        drawPath(todo, pincel(P.PAPEL, if (a.oscuro) 0f else 0.04f, b, s, a))
        val derecha = poligonoNapoleon(b.right, b.top, b.right + v.x, b.top + v.y, b.right + v.x, b.bottom + v.y, b.right, b.bottom)
        val abajo = poligonoNapoleon(b.left, b.bottom, b.right, b.bottom, b.right + v.x, b.bottom + v.y, b.left + v.x, b.bottom + v.y)
        drawPath(derecha, if (a.oscuro) a.filo(0.18f * kd) else a.sombra(0.2f))
        drawPath(abajo, a.sombra(if (a.oscuro) 0.3f else 0.1f))
        // Unos pliegos de otro papel, a distintas honduras.
        listOf(0.2f to 1, 0.47f to 2, 0.71f to 1).forEach { (f, q) ->
            val d = Offset(v.x * f, v.y * f); val d2 = Offset(v.x * (f + 0.12f), v.y * (f + 0.12f))
            drawPath(poligonoNapoleon(b.left + d.x, b.bottom + d.y, b.right + d.x, b.bottom + d.y, b.right + d.x, b.top + d.y,
                b.right + d2.x, b.top + d2.y, b.right + d2.x, b.bottom + d2.y, b.left + d2.x, b.bottom + d2.y), a.dia(PAPELES[q], -0.1f, kd).copy(alpha = 0.85f))
        }
        // Una raya por despacho (hasta donde caben), cada una con su largo: los pliegos no son parejos.
        val n = (s.grueso / (1.8f * U)).toInt().coerceIn(3, 18)
        val rayas = Path()
        for (j in 0..n) {
            val f = j / n.toFloat()
            val d = Offset(v.x * f, v.y * f)
            val corto = 10f * U * azar(e.semilla, 30 + j)
            rayas.moveTo(b.left + corto + d.x, b.bottom + d.y); rayas.lineTo(b.right + d.x, b.bottom + d.y)
            rayas.lineTo(b.right + d.x, b.top + d.y + corto * 0.6f)
        }
        drawPath(rayas, a.sombra(if (a.oscuro) 0.42f else 0.26f), style = Stroke(0.6f * U))
        // El borde de la cara de delante, desparejo: los pliegos de encima asoman distinto.
        val borde = Path()
        borde.moveTo(t.left + 4f * U, b.top)
        var x = t.left + 4f * U; var j = 0
        while (x < b.right) { x += (10f + 14f * azar(e.semilla, 120 + j)) * U; borde.lineTo(minOf(x, b.right), b.top + (if (j % 2 == 0) 1.6f else 0f) * U); j++ }
        borde.lineTo(b.right, b.bottom)
        drawPath(borde, a.sombra(0.3f), style = Stroke(0.7f * U))
        // Los pliegos que asoman más: por la derecha (con su lacre) y por arriba.
        val cuantos = (2 + e.hojas / 12).coerceAtMost(10)
        for (q in 0 until cuantos) {
            val f = 0.1f + 0.8f * azar(e.semilla, 40 + q)
            val d = Offset(v.x * f, v.y * f)
            val papel = PAPELES[(q + e.semilla) and 3]
            if (q % 3 != 2) {
                val y0 = b.top + (12f + 80f * azar(e.semilla, 50 + q)) * U + d.y
                val alto = (16f + 22f * azar(e.semilla, 60 + q)) * U
                val sale = (3f + 6f * azar(e.semilla, 70 + q)) * U
                val x0 = b.right + d.x
                drawRect(a.sombra(0.22f), Offset(x0, y0 + 1.4f * U), Size(sale + 1f * U, alto))
                drawRect(a.dia(papel, -0.04f, kd), Offset(x0, y0), Size(sale, alto))
                if (q % 2 == 0) drawCircle(a.dia(P.LACRE, 0f, kd), 2.8f * U, Offset(x0 + sale - 0.4f * U, y0 + alto * 0.5f))
            } else {
                val x0 = b.left + (40f + 110f * azar(e.semilla, 80 + q)) * U
                val ancho = (22f + 24f * azar(e.semilla, 90 + q)) * U
                val sale = (3f + 4f * azar(e.semilla, 100 + q)) * U
                val y0 = b.top
                drawRect(a.dia(papel, if (a.oscuro) 0f else 0.06f, k(Offset(x0, y0), s, a)), Offset(x0, y0 - sale), Size(ancho, sale + 1f * U))
                drawLine(a.sombra(0.2f), Offset(x0 + ancho, y0 - sale), Offset(x0 + ancho, y0), 0.8f * U)
            }
        }
    }

    /** El despacho de encima (se ve al abrirse): el papel con el membrete de doble filete y el margen. */
    private fun DrawScope.despachoDeEncima(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val t = s.tapa
        val hoja = Rect(t.left + 2f * U, t.top + 2f * U, t.right - 2f * U, t.bottom - 1f * U)
        drawRect(pincel(P.PAPEL, 0f, hoja, s, a), hoja.topLeft, hoja.size)
        val tinta = a.dia(P.TINTA, 0f, 0.6f).copy(alpha = 0.6f)
        drawLine(tinta, Offset(hoja.left + 30f * U, hoja.top + 22f * U), Offset(hoja.right - 10f * U, hoja.top + 22f * U), 1.4f * U)
        drawLine(tinta.copy(alpha = 0.35f), Offset(hoja.left + 30f * U, hoja.top + 25f * U), Offset(hoja.right - 10f * U, hoja.top + 25f * U), 0.6f * U)
        drawLine(tinta.copy(alpha = 0.25f), Offset(hoja.left + 28f * U, hoja.top), Offset(hoja.left + 28f * U, hoja.bottom), 0.8f * U)
        for (j in 0 until 8) {
            val y = hoja.top + (38f + j * 11f) * U
            if (y < hoja.bottom - 8f * U) drawLine(tinta.copy(alpha = 0.3f), Offset(hoja.left + 34f * U, y), Offset(hoja.right - (if (j % 4 == 3) 60f else 14f) * U, y), 0.8f * U)
        }
    }

    /** La silueta de la cubierta: el canto de arriba un poco ondulado (la vitela se aguanta), las esquinas romas y la de abajo a la derecha, doblada. */
    private fun silueta(t: Rect, U: Float): Path = Path().apply {
        val r = 3f * U; val e = 11f * U
        moveTo(t.left + r, t.top)
        quadraticTo(t.left + t.width * 0.3f, t.top - 1.4f * U, t.left + t.width * 0.55f, t.top + 0.3f * U)
        quadraticTo(t.left + t.width * 0.8f, t.top + 1.4f * U, t.right - r, t.top)
        quadraticTo(t.right, t.top, t.right, t.top + r)
        quadraticTo(t.right + 1.2f * U, t.top + t.height * 0.5f, t.right, t.bottom - e)
        lineTo(t.right - e, t.bottom)
        lineTo(t.left + r, t.bottom)
        quadraticTo(t.left, t.bottom, t.left, t.bottom - r)
        lineTo(t.left, t.top + r)
        quadraticTo(t.left, t.top, t.left + r, t.top)
        close()
    }

    /**
     * La cubierta de delante, de vitela flexible: su cara (más clara arriba a la izquierda, donde mira a la ventana), sus ondas, el
     * doblez junto al lomo, el canto gastado y la cartela rayada a tinta. Con [m], lo mismo dentro de la trama de la ventana.
     */
    private fun DrawScope.cubierta(s: SitioLibretaNapoleon, a: AireNapoleon, m: ManchaNapoleon?) {
        val U = s.U
        val t = s.tapa
        val forma = silueta(t, U)
        if (m != null) drawPath(forma, a.en(VITELA, 0.04f, 1f, 1f, m))
        else if (a.oscuro) drawPath(forma, pincel(VITELA, 0f, t, s, a))
        else drawPath(forma, Brush.linearGradient(listOf(a.dia(VITELA, 0.08f), a.dia(mezcla(VITELA, VITELA_OSC, 0.3f), 0f)), t.topLeft, t.bottomRight))
        // El doblez del lomo.
        drawRect(VITELA_OSC.copy(alpha = 0.5f), Offset(t.left, t.top + 2f * U), Size(4f * U, t.height - 4f * U), blendMode = BlendMode.Multiply)
        drawLine(VITELA_OSC.copy(alpha = 0.7f), Offset(t.left + s.lomo, t.top + 2f * U), Offset(t.left + s.lomo, t.bottom - 2f * U), 1f * U, blendMode = BlendMode.Multiply)
        drawLine(a.claro(0.5f), Offset(t.left + s.lomo + 1f * U, t.top + 3f * U), Offset(t.left + s.lomo + 1f * U, t.bottom - 3f * U), 0.8f * U)
        drawPath(forma, VITELA_OSC.copy(alpha = 0.8f), style = Stroke(1f * U), blendMode = BlendMode.Multiply)
        // La esquina de abajo, doblada: el revés de la vitela, más pardo, con su sombra.
        val e = 11f * U
        if (m == null) drawPath(poligonoNapoleon(t.right, t.bottom - e, t.right - e, t.bottom, t.right - e * 1.05f, t.bottom - e * 0.75f), a.sombra(0.3f))
        val oreja = poligonoNapoleon(t.right, t.bottom - e, t.right - e, t.bottom, t.right - e * 0.92f, t.bottom - e * 0.92f)
        drawPath(oreja, if (m != null) a.en(VITELA_OSC, 0.1f, 1f, 1f, m) else a.dia(mezcla(VITELA, VITELA_OSC, 0.6f), 0f, k(t.bottomRight, s, a)))
        drawPath(oreja, VITELA_OSC.copy(alpha = 0.9f), style = Stroke(0.7f * U), blendMode = BlendMode.Multiply)
        // La cartela: dos filetes de tinta parda con sus esquinas cortadas.
        val r = s.rotulo
        val tinta = Color(0xFF5A3A22)
        listOf(0f to 0.9f, 3f to 0.5f).forEach { (d, g) ->
            val q = r.deflate(d * U); val c = 5f * U
            drawPath(poligonoNapoleon(q.left + c, q.top, q.right - c, q.top, q.right, q.top + c, q.right, q.bottom - c, q.right - c, q.bottom, q.left + c, q.bottom, q.left, q.bottom - c, q.left, q.top + c),
                tinta.copy(alpha = 0.75f), style = Stroke(g * U), blendMode = BlendMode.Multiply)
        }
    }

    /**
     * El último despacho, todavía sin coser: un pliego doblado y cerrado con su lacre, metido bajo la cinta a la derecha (la cinta
     * le pasa por encima). Con su sombra en la vitela.
     */
    private fun DrawScope.pliego(e: EstadoLibreta, s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val t = s.tapa
        val r = Rect(t.right - 64f * U, s.cintaY - 17f * U, t.right - 20f * U, s.cintaY + 10f * U)
        val k = k(r.center, s, a)
        rotate(-6f + 3f * azar(e.semilla, 7), r.center) {
            drawRect(a.sombra(if (a.oscuro) 0.4f else 0.22f), r.topLeft + Offset(1.6f * U, 2f * U), r.size)
            drawRect(a.dia(PAPELES[1], 0.04f, k), r.topLeft, r.size)
            // Los dobleces: la solapa en pico que baja hasta el lacre, y su sombra.
            val pico = Offset(r.center.x, r.top + r.height * 0.62f)
            drawPath(poligonoNapoleon(r.left, r.top, r.right, r.top, pico.x, pico.y), a.dia(PAPELES[1], -0.06f, k))
            drawLine(a.sombra(0.35f), Offset(r.left, r.top), pico, 0.6f * U)
            drawLine(a.sombra(0.35f), Offset(r.right, r.top), pico, 0.6f * U)
            drawRect(a.sombra(0.25f), r.topLeft, r.size, style = Stroke(0.6f * U))
            // El lacre: la gota aplastada, su borde y el brillo.
            drawCircle(a.dia(CINTA_OSC, 0f, k), 4.2f * U, pico + Offset(0.4f * U, 0.6f * U))
            drawCircle(a.dia(P.LACRE, 0f, k), 3.8f * U, pico)
            drawCircle(a.dia(CINTA_OSC, 0f, k), 2.4f * U, pico, style = Stroke(0.7f * U))
            drawCircle(a.brillo(0.5f, k), 0.9f * U, pico - Offset(1.4f * U, 1.4f * U))
        }
    }

    /** El nombre y la cuenta en la cartela, con la tinta de la casa. */
    private fun DrawScope.rotulo(e: EstadoLibreta, s: SitioLibretaNapoleon, a: AireNapoleon, medidor: TextMeasurer) {
        val U = s.U
        val r = s.rotulo
        val tinta = P.TINTA
        textoEn(medidor, e.nombre, Rect(r.left + 7f * U, r.top + 6f * U, r.right - 7f * U, r.top + 31f * U), P.LETRA_TITULO, tinta, 15.5f, peso = FontWeight.Bold, minimo = 8f)
        drawLine(tinta.copy(alpha = 0.55f), Offset(r.center.x - 24f * U, r.top + 34f * U), Offset(r.center.x + 24f * U, r.top + 34f * U), 0.7f * U)
        textoEn(medidor, e.cuenta, Rect(r.left + 7f * U, r.top + 37f * U, r.right - 7f * U, r.bottom - 5f * U), P.LETRA, tinta, 10.5f, minimo = 6f)
    }

    /**
     * La cinta de balduque carmesí que ciñe el legajo a lo ancho: sale de detrás del lomo, cruza la vitela y los pliegos que
     * asoman y da la vuelta por el canto de la derecha; cerca del lomo, el nudo, del que salen los dos cabos, que bajan por la
     * vitela y cuelgan por delante del listón, cortados en cola de golondrina. Al soltarse ([lazo] de 0 a 1) el nudo se deshace,
     * la cinta se afloja y los cabos caen. Con [m], la parte que cae en la trama de la ventana, alumbrada.
     */
    private fun DrawScope.cinta(s: SitioLibretaNapoleon, a: AireNapoleon, m: ManchaNapoleon?, lazo: Float) {
        val U = s.U
        val t = s.tapa; val b = s.pliegos; val v = s.atras
        val kk = k(s.lazo, s, a)
        val kd = k(Offset(b.right, s.cintaY), s, a)
        fun c(col: Color, kx: Float = kk) = if (m != null) a.en(col, 0f, 1f, 1f, m) else a.dia(col, 0f, kx)
        val cara = c(CINTA); val honda = c(CINTA_OSC); val luz = c(CINTA_LUZ)
        val l = s.lazo
        val ancho = 4.4f * U
        val q = 1f - lazo
        if (q > 0.02f) {
            val y = s.cintaY
            // Su sombra en la vitela (de día) y la cinta, que hunde un poco el papel.
            if (m == null && !a.oscuro) drawRect(a.sombra(0.2f * q), Offset(t.left, y + ancho / 2), Size(b.right - t.left, 1.6f * U))
            drawPath(poligonoNapoleon(b.right, y - ancho / 2, b.right + v.x, y - ancho / 2 + v.y, b.right + v.x, y + ancho / 2 + v.y, b.right, y + ancho / 2),
                (if (m == null) c(CINTA_OSC, kd) else honda).let { if (a.oscuro) mezcla(it, c(CINTA, kd), 0.6f * kd) else it }.copy(alpha = q))
            drawRect(cara.copy(alpha = q), Offset(t.left - 1f * U, y - ancho / 2), Size(b.right - t.left + 1f * U, ancho))
            drawRect(honda.copy(alpha = 0.6f * q), Offset(t.left, y + ancho / 2 - 1f * U), Size(b.right - t.left, 1f * U))
            drawLine(luz.copy(alpha = q), Offset(t.left, y - ancho / 2 + 1.1f * U), Offset(b.right, y - ancho / 2 + 1.1f * U), 0.8f * U)
        }
        // Los cabos: bajan por la vitela y cuelgan por delante del listón, cortados en cola de golondrina.
        val caida = lazo * 8f * U
        val fin1 = Offset(l.x - 7f * U - caida * 0.3f, s.listonAbajo + 13f * U + caida)
        val fin2 = Offset(l.x + 6f * U + caida * 0.6f, s.listonAbajo + 3f * U + caida)
        val bajo = s.liston + 1f * U
        val ac = 4f * U
        listOf(fin2 to 1f, fin1 to -1f).forEach { (fin, lado) ->
            val cabo = Path().apply {
                moveTo(l.x - ac / 2, l.y + 2f * U)
                quadraticTo(l.x + lado * 5f * U - ac / 2, (l.y + bajo) / 2, fin.x - ac / 2, bajo)
                lineTo(fin.x - ac / 2, fin.y); lineTo(fin.x, fin.y - 3.4f * U); lineTo(fin.x + ac / 2, fin.y)
                lineTo(fin.x + ac / 2, bajo)
                quadraticTo(l.x + lado * 5f * U + ac / 2, (l.y + bajo) / 2, l.x + ac / 2, l.y + 2f * U); close()
            }
            if (m == null) drawPath(Path().apply { addPath(cabo, Offset(2.6f * U, 1.8f * U)) }, a.sombra(0.28f))
            drawPath(cabo, if (lado < 0f) cara else mezcla(cara, honda, 0.4f))
            drawLine(luz, Offset(fin.x - 0.9f * U, bajo - 1f * U), Offset(fin.x - 0.9f * U, fin.y - 3.4f * U), 0.8f * U)
            if (a.oscuro && m == null && lado < 0f) drawLine(a.lunar(0.35f), Offset(fin.x - ac / 2, bajo), Offset(fin.x - ac / 2, fin.y), 0.8f * U)
        }
        // El nudo: la vuelta de la cinta, apretada, con su pliegue (al soltarse, se afloja y se apaga).
        if (q > 0.02f) {
            val r = 4.6f * U * (0.6f + 0.4f * q)
            if (m == null) drawCircle(a.sombra(0.32f), r * 1.1f, l + Offset(1.6f * U, 1.8f * U))
            rotate(-28f, l) {
                drawRoundRect(honda, Offset(l.x - r * 1.25f, l.y - r * 0.85f), Size(r * 2.5f, r * 1.7f), CornerRadius(r * 0.7f))
                drawRoundRect(cara, Offset(l.x - r * 1.05f, l.y - r * 0.75f), Size(r * 2.1f, r * 1.3f), CornerRadius(r * 0.6f))
                drawLine(honda, Offset(l.x - r * 0.1f, l.y - r * 0.7f), Offset(l.x + r * 0.2f, l.y + r * 0.6f), 0.9f * U)
                drawLine(luz, Offset(l.x - r * 0.8f, l.y - r * 0.35f), Offset(l.x - r * 0.25f, l.y - r * 0.5f), 0.9f * U)
            }
        }
    }
}
