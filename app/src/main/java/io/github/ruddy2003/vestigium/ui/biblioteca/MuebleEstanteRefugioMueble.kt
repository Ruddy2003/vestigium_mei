package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRefugio as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La librería de pared de la sala de lectura: chapa esmaltada verde agua de los años cincuenta. La cornisa (el tablero de arriba
 * con su canto redondo cromado, las esquinas redondeadas, el friso con la rejilla del altavoz y el junquillo cromado), los costados
 * con su vuelta redonda, el junquillo y un botón cromado a la altura de cada tabla, las tablas con el canto de aluminio acanalado y
 * su portarrótulos, los rótulos del mes y la luz de guardia ámbar del costado derecho.
 */
internal object MuebleRefugio {
    /** El radio de las esquinas de arriba del mueble, en anchos. */
    private const val RADIO = 0.05f

    // ---------------------------------------------------------------- la cornisa (se pinta una vez, en la corona guardada)

    fun DrawScope.cornisa(g: MarcoRefugio) {
        val w = g.w; val u = density
        val y0 = g.apoyo; val ch = g.ch
        val tope = y0 - w * 0.016f
        val canto0 = y0 + w * 0.008f
        val canto1 = y0 + w * 0.032f
        val friso1 = ch - w * 0.014f
        // La silueta del mueble, con las esquinas de arriba redondeadas: lo de fuera es pared.
        val silueta = Path().apply { addRoundRect(RoundRect(Rect(0f, tope, w, ch + w), CornerRadius(w * RADIO), CornerRadius(w * RADIO), CornerRadius.Zero, CornerRadius.Zero)) }
        // Su sombra en la pared, por las esquinas.
        val ar = g.arrojada(0.6f)
        clipPath(silueta, androidx.compose.ui.graphics.ClipOp.Difference) {
            drawPath(corridoRefugio(silueta, ar.x, ar.y), g.a.sombra(0.3f))
        }
        clipPath(silueta) {
            with(ParedEstanteRefugio) {
                // El tablero de arriba, visto un poco desde arriba: formica crema con su dibujo de motas.
                campo(g, Rect(0f, tope, w, canto0), mezcla(P.VINILO, P.CREMA, 0.4f), 0.04f, paso = 3f)
                // El canto redondo cromado del tablero.
                campo(g, Rect(0f, canto0, w, canto1), P.CROMO, 0.05f, paso = 3f)
                // El friso de esmalte verde agua.
                campo(g, Rect(0f, canto1, w, friso1), P.AGUA, 0.045f, paso = 4f)
                // La banda cromada de abajo.
                campo(g, Rect(0f, friso1, w, ch), P.CROMO_OSC, 0.05f, paso = 3f)
            }
            // Las motas del tablero.
            for (j in 0 until 60) {
                val x = w * azar(9001, j); val yy = tope + (canto0 - tope) * (0.15f + 0.7f * azar(9002, j))
                val col = listOf(P.TEAL, P.CORAL, P.OCRE, P.TINTA)[j % 4]
                drawCircle(g.luz(x, yy, 0.04f).c(col).copy(alpha = 0.5f), (0.5f + 0.6f * azar(9003, j)) * u, Offset(x, yy))
            }
            // El canto cromado: el reflejo del techo arriba, el del suelo abajo y la raya de brillo.
            val lc = g.luz(w * 0.3f, canto0, 0.05f)
            val bandas = List(9) { j -> g.luz(w * j / 8f, canto0, 0.05f) }
            drawRect(Brush.horizontalGradient(bandas.map { it.c(P.CROMO_LUZ) }, 0f, w), Offset(0f, canto0), Size(w, (canto1 - canto0) * 0.32f))
            drawRect(Brush.horizontalGradient(bandas.map { it.c(P.CROMO_OSC) }, 0f, w), Offset(0f, canto0 + (canto1 - canto0) * 0.55f), Size(w, (canto1 - canto0) * 0.3f))
            drawRect(Brush.horizontalGradient(bandas.map { it.b(0.9f) }, 0f, w), Offset(0f, canto0 + (canto1 - canto0) * 0.14f), Size(w, 1.2f * u))
            drawRect(lc.s(0.4f), Offset(0f, canto1 - 1.2f * u), Size(w, 1.2f * u))
            // El friso: el borde de arriba en la sombra del canto, que vuela por delante.
            drawRect(Brush.verticalGradient(listOf(g.a.sombra(if (g.a.oscuro) 0.4f else 0.32f), g.a.sombra(0f)), canto1, canto1 + w * 0.03f), Offset(0f, canto1), Size(w, w * 0.03f))
            pinceladas(Rect(0f, canto1, w, friso1), if (g.a.oscuro) g.a.hondo else P.AGUA_OSC, 9010, 16, u, 0.08f)
            // El junquillo cromado a lo largo del friso.
            val yj = canto1 + (friso1 - canto1) * 0.78f
            cromoRefugio(Rect(0f, yj, w, yj + 2.2f * u), g.luz(w * 0.3f, yj, 0.05f), brillo = 0.7f)
            // La banda de abajo: su brillo y su sombra.
            drawRect(Brush.horizontalGradient(bandas.map { it.b(0.6f) }, 0f, w), Offset(0f, friso1 + 1f * u), Size(w, 1f * u))
            // La rejilla del altavoz del refugio, en medio del friso.
            altavoz(g, Rect(w * 0.37f, canto1 + (friso1 - canto1) * 0.16f, w * 0.63f, canto1 + (friso1 - canto1) * 0.66f))
            friso(g, canto1, friso1)
            // Los dados sobre los costados: un botón cromado en cada punta del friso.
            listOf(g.x0 * 0.5f, (g.x1 + w) * 0.5f).forEach { x ->
                botonCromoRefugio(Offset(x, canto1 + (friso1 - canto1) * 0.42f), w * 0.014f, g.luz(x, canto1, 0.05f))
            }
            // El esmalte saltado en el canto y en el friso, por el roce.
            for (j in 0 until 12) {
                val x = w * (0.03f + 0.94f * azar(9021, j))
                val yy = if (j % 2 == 0) canto1 + 1.4f * u else canto1 + (friso1 - canto1) * (0.2f + 0.6f * azar(9022, j))
                val ls = g.luz(x, yy, 0.05f)
                saltadoRefugio(Offset(x, yy), w * (0.004f + 0.004f * azar(9023, j)), 9030 + j, ls.c(P.CROMO_OSC), ls.b(0.45f), ls.c(P.NARANJA))
            }
            // De noche, el filo de la pantalla en el canto cromado, más fuerte cerca.
            if (g.a.oscuro || g.a.noche) {
                val filos = List(9) { j -> g.luz(w * j / 8f, canto0, 0.05f).filo(0.9f) }
                drawRect(Brush.horizontalGradient(filos, 0f, w), Offset(0f, canto0 + 0.5f * u), Size(w, 1.8f * u))
            }
        }
        // El filo de arriba del tablero, con la luz, siguiendo las esquinas redondas.
        drawPath(Path().apply { addRoundRect(RoundRect(Rect(0.6f * u, tope + 0.6f * u, w - 0.6f * u, ch + w), CornerRadius(w * RADIO), CornerRadius(w * RADIO), CornerRadius.Zero, CornerRadius.Zero)) },
            g.luz(w * 0.2f, tope, 0.04f).b(0.55f), style = Stroke(1.1f * u))
    }

    /**
     * Lo de los lados del friso, citado del panel del armario del Ideario: a la izquierda, la placa de inventario de chapa con sus
     * cuatro remaches y dos renglones estampados (sin letra); a la derecha, la pegatina de revisión con sus casillas y la esquina
     * levantada, y los tres pilotos ámbar con su aro cromado (dos encendidos, uno apagado).
     */
    private fun DrawScope.friso(g: MarcoRefugio, y0: Float, y1: Float) {
        val w = g.w; val u = density
        val hh = y1 - y0
        // La placa de inventario.
        val pl = Rect(w * 0.12f, y0 + hh * 0.2f, w * 0.27f, y0 + hh * 0.62f)
        val lp = g.luz(pl.center, 0.05f)
        drawRect(lp.s(0.35f), pl.topLeft + Offset(1.4f * u, 1.6f * u), pl.size)
        drawRect(lp.c(mezcla(P.ACERO, P.CREMA, 0.3f)), pl.topLeft, pl.size)
        drawRect(lp.b(0.55f), pl.topLeft, Size(pl.width, 1f * u))
        listOf(0.36f to 0.7f, 0.64f to 0.46f).forEach { (fy, fw) ->
            drawRect(lp.s(0.4f), Offset(pl.left + pl.width * 0.14f, pl.top + pl.height * fy), Size(pl.width * fw, 1.2f * u))
            drawRect(lp.b(0.4f), Offset(pl.left + pl.width * 0.14f, pl.top + pl.height * fy + 1.2f * u), Size(pl.width * fw, 0.6f * u))
        }
        listOf(Offset(pl.left + 2f * u, pl.top + 2f * u), Offset(pl.right - 2f * u, pl.top + 2f * u), Offset(pl.left + 2f * u, pl.bottom - 2f * u), Offset(pl.right - 2f * u, pl.bottom - 2f * u))
            .forEach { drawCircle(lp.c(P.ACERO_OSC), 0.9f * u, it) }
        // La pegatina de revisión: papel con su franja naranja, casillas (dos marcadas) y la esquina levantada.
        val pg = Rect(w * 0.68f, y0 + hh * 0.14f, w * 0.78f, y0 + hh * 0.66f)
        val lg = g.luz(pg.center, 0.05f)
        val esq = pg.height * 0.28f
        drawRect(lg.s(0.22f), pg.topLeft + Offset(1.2f * u, 1.2f * u), pg.size)
        drawPath(poligonoRefugio(listOf(Offset(pg.left, pg.top), Offset(pg.right - esq, pg.top), Offset(pg.right, pg.top + esq), Offset(pg.right, pg.bottom), Offset(pg.left, pg.bottom))), lg.c(mezcla(P.ETIQUETA, P.OCRE, 0.15f)))
        drawRect(lg.c(P.NARANJA), pg.topLeft, Size(pg.width - esq, pg.height * 0.18f))
        for (j in 0 until 3) for (i in 0 until 2) {
            val q = Offset(pg.left + pg.width * (0.12f + 0.44f * i), pg.top + pg.height * (0.32f + 0.22f * j))
            val s = pg.height * 0.13f
            drawRect(lg.c(P.TINTA).copy(alpha = 0.6f), q, Size(s, s), style = Stroke(0.6f * u))
            if (i == 0 && j < 2) drawLine(lg.c(P.TINTA).copy(alpha = 0.75f), q + Offset(0f, s * 0.5f), q + Offset(s * 0.9f, -s * 0.2f), 0.8f * u)
            drawLine(lg.c(P.TINTA).copy(alpha = 0.35f), q + Offset(s * 1.5f, s * 0.6f), q + Offset(s * 3f, s * 0.6f), 0.6f * u)
        }
        drawPath(poligonoRefugio(Offset(pg.right - esq, pg.top), Offset(pg.right + esq * 0.2f, pg.top - esq * 0.3f), Offset(pg.right, pg.top + esq)), lg.c(mezcla(P.ETIQUETA, P.CREMA_OSC, 0.6f)))
        // Los tres pilotos.
        for (k in 0 until 3) {
            val c = Offset(w * (0.83f + 0.042f * k), y0 + hh * 0.4f)
            val l = g.luz(c, 0.05f)
            val r = minOf(hh * 0.12f, 3.6f * u)
            drawCircle(l.s(0.35f), r * 1.5f, c + Offset(0.8f * u, 0.8f * u))
            drawCircle(l.c(P.CROMO), r * 1.45f, c)
            drawCircle(l.b(0.5f), r * 1.45f, c, style = Stroke(0.7f * u))
            if (k != 1) {
                drawCircle(P.AMBAR.copy(alpha = 0.3f), r * 2.3f, c)
                drawCircle(P.AMBAR, r, c)
                drawCircle(Color.White.copy(alpha = 0.7f), r * 0.35f, c - Offset(r * 0.3f, r * 0.3f))
            } else {
                drawCircle(l.c(mezcla(P.AMBAR, P.OLIVA_OSC, 0.75f)), r, c)
                drawCircle(l.b(0.3f), r * 0.3f, c - Offset(r * 0.3f, r * 0.3f))
            }
        }
    }

    /**
     * La rejilla del altavoz: el marco cromado de esquinas redondas, la tela oscura y las barras cromadas a lo largo, cada una con su
     * brillo arriba y su sombra debajo. Sin pilotos al lado (con dos luces haría cara).
     */
    private fun DrawScope.altavoz(g: MarcoRefugio, r: Rect) {
        val u = density
        val l = g.luz(r.center, 0.05f)
        val marco = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.height * 0.5f))) }
        drawPath(corridoRefugio(marco, 1.4f * u, 1.8f * u), l.s(0.35f))
        drawPath(marco, l.c(P.CROMO_OSC))
        val dentro = r.deflate(r.height * 0.14f)
        val tela = Path().apply { addRoundRect(RoundRect(dentro, CornerRadius(dentro.height * 0.5f))) }
        drawPath(tela, l.c(mezcla(P.BAQUELITA, P.TEAL, 0.25f)))
        clipPath(tela) {
            val barras = Path(); val brillos = Path(); val sombras = Path()
            val n = 5
            for (i in 0 until n) {
                val y = dentro.top + dentro.height * (i + 0.5f) / n
                barras.addRect(Rect(dentro.left, y - 1.1f * u, dentro.right, y + 1.1f * u))
                brillos.addRect(Rect(dentro.left, y - 1.1f * u, dentro.right, y - 0.4f * u))
                sombras.addRect(Rect(dentro.left, y + 1.1f * u, dentro.right, y + 2f * u))
            }
            drawPath(sombras, l.s(0.4f))
            drawPath(barras, l.c(P.CROMO))
            drawPath(brillos, l.b(0.8f))
        }
        drawPath(marco, l.b(0.6f), style = Stroke(0.9f * u))
    }

    // ---------------------------------------------------------------- las juntas

    /**
     * Las filas de las juntas: donde una tabla toca el hueco de arriba o de abajo, el borde puede caer a medio píxel. Debajo de las
     * dos piezas va esa fila entera, de píxel entero y del color del aluminio, solo a lo ancho de las dos; y lo mismo en las
     * columnas donde los costados tocan los huecos.
     */
    fun DrawScope.juntas(g: MarcoRefugio, d: DisposicionEstante) {
        d.tablas.forEach { r ->
            for (yy in listOf(r.top, r.bottom)) {
                val f = floor(yy)
                drawRect(g.luz(g.w * 0.5f, yy, 0.03f).c(P.CROMO_OSC), Offset(r.left - 1f, f), Size(r.width + 2f, 1f))
            }
        }
        val y0 = floor(g.ch); val y1 = ceil(g.fondo)
        for (xx in listOf(g.x0, g.x1)) {
            val f = floor(xx)
            drawRect(g.luz(xx, (y0 + y1) / 2, 0.03f).c(P.AGUA_OSC), Offset(f, y0), Size(1f, y1 - y0))
        }
    }

    // ---------------------------------------------------------------- las tablas

    /**
     * Una tabla: el canto de aluminio acanalado (cinco estrías, cada una con su brillo arriba y su sombra debajo), el labio del
     * tablero de formica asomando arriba, la sombra de abajo y el esmalte rozado; de noche, el filo de la luz más cercana.
     */
    fun DrawScope.tabla(k: Int, r: Rect, g: MarcoRefugio) {
        val w = g.w; val u = density
        val xs = List(9) { r.left + r.width * it / 8f }
        fun l(x: Float) = g.luz(x, r.center.y, 0.045f)
        fun banda(f0: Float, f1: Float, col: Color) {
            val a = r.top + r.height * f0; val b = r.top + r.height * f1
            drawRect(Brush.horizontalGradient(xs.map { l(it).c(col) }, r.left, r.right), Offset(r.left, a), Size(r.width, b - a + 0.6f))
        }
        banda(0f, 1f, mezcla(P.CROMO, P.CROMO_OSC, 0.35f))
        // El labio del tablero de formica, arriba, con su filo de luz.
        banda(0f, 0.14f, mezcla(P.VINILO, P.CREMA, 0.4f))
        drawRect(Brush.horizontalGradient(xs.map { l(it).b(0.8f) }, r.left, r.right), Offset(r.left, r.top + 0.4f * u), Size(r.width, 1f * u))
        // Las estrías del aluminio: en tres caminos.
        val surcos = Path(); val brillos = Path(); val sombras = Path()
        val n = 5
        val a0 = r.top + r.height * 0.2f; val a1 = r.bottom - r.height * 0.1f
        for (i in 0 until n) {
            val y = a0 + (a1 - a0) * i / n
            val paso = (a1 - a0) / n
            brillos.addRect(Rect(r.left, y, r.right, y + paso * 0.3f))
            sombras.addRect(Rect(r.left, y + paso * 0.62f, r.right, y + paso))
            surcos.addRect(Rect(r.left, y + paso * 0.88f, r.right, y + paso))
        }
        // De día la luz es pareja a lo largo de la tabla: estrías de color liso; de noche, el brillo sigue a cada charco.
        if (g.a.oscuro) drawPath(brillos, Brush.horizontalGradient(xs.map { l(it).c(P.CROMO_LUZ) }, r.left, r.right))
        else drawPath(brillos, l(r.left + r.width * 0.3f).c(P.CROMO_LUZ))
        drawPath(sombras, mezcla(l(r.center.x).c(P.CROMO_OSC), g.a.hondo, if (g.a.oscuro) 0.3f else 0f).copy(alpha = 0.75f))
        drawPath(surcos, g.a.sombra(0.35f))
        // La sombra que echa hacia abajo y el roce del canto.
        drawRect(g.a.sombra(0.4f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        val s = 9100 + k * 13
        for (i in 0 until 4) {
            val x = r.left + r.width * (0.04f + 0.92f * azar(s, 10 + i))
            val ls = l(x)
            drawLine(ls.s(0.35f), Offset(x, r.top + r.height * 0.3f), Offset(x + w * (0.01f + 0.015f * azar(s, 20 + i)), r.top + r.height * 0.34f), 0.8f * u)
        }
        // De noche, el filo de la luz más cercana en el labio.
        if (g.a.oscuro || g.a.noche) {
            val filos = xs.map { l(it).filo(0.95f) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, r.left, r.right), Offset(r.left, r.top + 0.3f * u), Size(r.width, 1.8f * u))
            val brillo = xs.map { l(it).filo(0.4f) }
            if (brillo.any { it.alpha > 0.04f }) drawPath(brillos, Brush.horizontalGradient(brillo, r.left, r.right))
        }
    }

    /**
     * El rótulo del mes: la tarjeta crema tostada en su portarrótulos cromado atornillado al canto de la tabla, con el mes en la letra
     * de terminal; se tiñe con la luz que le toca sin dejar de leerse.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoRefugio) {
        val u = density
        val r = p.r
        val l = g.luz(r.center.x, r.center.y, 0.05f)
        val marco = Rect(r.left - 3f * u, r.top - 0.6f * u, r.right + 3f * u, r.bottom + 0.6f * u)
        drawRoundRect(l.s(0.45f), marco.topLeft + Offset(1.2f * u, 1.6f * u), marco.size, CornerRadius(2f * u))
        drawRoundRect(l.c(P.CROMO_OSC), marco.topLeft, marco.size, CornerRadius(2f * u))
        cromoRefugio(Rect(marco.left + 0.8f * u, marco.top + 0.6f * u, marco.right - 0.8f * u, marco.top + 2.2f * u), l, brillo = 0.8f)
        val t = Rect(r.left, r.top + 1.6f * u, r.right, r.bottom - 0.8f * u)
        val carton = l.tarjeta()
        drawRect(carton, t.topLeft, t.size)
        drawRect(mezcla(carton, P.TEAL, 0.5f), Offset(t.left, t.top + 1.2f * u), Size(t.width, 0.8f * u))
        textoEn(medidor, p.texto, Rect(t.left + 4f * u, t.top + 1.6f * u, t.right - 4f * u, t.bottom), P.LETRA, l.tinta(carton), 13f, espaciado = 0.3f, minimo = 8f)
        // Los dos tornillos del portarrótulos.
        listOf(marco.left + 1.6f * u, marco.right - 1.6f * u).forEach { x -> drawCircle(l.c(P.CROMO_LUZ), 0.9f * u, Offset(x, marco.center.y)) }
        if (l.k > 0.08f) drawRect(l.filo(0.5f), t.topLeft, t.size, style = Stroke(0.9f * u))
    }

    // ---------------------------------------------------------------- los costados

    /**
     * Los costados: la cara de chapa esmaltada verde agua, con su vuelta redonda hacia fuera (a la luz en el izquierdo, en sombra en el
     * derecho), el canto cromado de dentro, el junquillo cromado a lo largo, un botón cromado a la altura de cada tabla, el esmalte
     * saltado y una abolladura.
     */
    fun DrawScope.costados(g: MarcoRefugio, d: DisposicionEstante) {
        val w = g.w; val u = density
        val y0 = g.ch; val y1 = g.fondo
        if (y1 - y0 < 2f) return
        val ys = List(11) { y0 + (y1 - y0) * it / 10f }
        for (izq in listOf(true, false)) {
            val a = if (izq) 0f else g.x1
            val b = if (izq) g.x0 else w
            val an = b - a
            val cx = (a + b) / 2
            // La cara: un solo degradado a lo alto (la luz de cada altura); lo demás, en capas lisas, que pintan más ligero.
            drawRect(Brush.verticalGradient(ys.map { g.luz(cx, it, 0.04f).c(P.AGUA) }, y0, y1), Offset(a, y0), Size(an, y1 - y0))
            if (g.a.techo) drawRect(P.LUZ_TECHO.copy(alpha = if (izq) 0.16f else 0.07f), Offset(a, y0), Size(an * 0.36f, y1 - y0))
            pinceladas(Rect(a, y0, b, y1), if (g.a.oscuro) g.a.hondo else P.AGUA_OSC, if (izq) 9201 else 9202, 10, u, 0.09f)
            // La vuelta redonda de fuera: en el izquierdo mira a las luminarias; en el derecho, a la sombra.
            val fuera = if (izq) a else b - an * 0.16f
            val vueltaCapa = if (izq && !g.a.oscuro) P.LUZ_TECHO.copy(alpha = if (g.a.tarde) 0.16f else 0.3f) else g.a.sombra(if (izq) 0.22f else 0.32f)
            drawRect(vueltaCapa, Offset(fuera, y0), Size(an * 0.16f, y1 - y0))
            drawRect(g.a.sombra(0.25f), Offset(if (izq) a + an * 0.16f else b - an * 0.16f - 1f * u, y0), Size(1f * u, y1 - y0))
            // El canto cromado de dentro, donde el costado se encuentra con los huecos: su reflejo hondo con la luz de cada altura y,
            // de día, la raya clara del cromo (de noche la pone el filo de la luz, solo donde llega).
            val canto = if (izq) b - an * 0.14f else a
            val lc = ys.map { g.luz(canto, it, 0.045f) }
            drawRect(Brush.verticalGradient(lc.map { it.c(P.CROMO_OSC) }, y0, y1), Offset(canto, y0), Size(an * 0.14f, y1 - y0))
            if (!g.a.oscuro) {
                drawRect(P.CROMO_LUZ.copy(alpha = if (g.a.tarde) 0.45f else 0.75f), Offset(canto + an * 0.03f, y0), Size(an * 0.05f, y1 - y0))
                drawRect(lc[2].b(0.85f), Offset(canto + an * 0.04f, y0), Size(1.1f * u, y1 - y0))
            }
            // Un cuarterón rehundido en cada tramo, a la altura de su repisa: el fondo más hondo, el costado de dentro en sombra y el
            // otro a la luz, y el junquillo cromado alrededor.
            val px0 = if (izq) a + an * 0.24f else a + an * 0.2f
            val px1 = if (izq) b - an * 0.22f else b - an * 0.26f
            d.repisas.forEach { rep ->
                val pr = Rect(px0, rep.top + 0.022f * w, px1, rep.bottom - 0.012f * w)
                if (pr.height < 4f * u) return@forEach
                val lp = g.luz(pr.center.x, pr.center.y, 0.04f)
                val rr = pr.width * 0.45f
                val pp = Path().apply { addRoundRect(RoundRect(pr, CornerRadius(rr))) }
                drawPath(pp, lp.c(mezcla(P.AGUA, P.AGUA_OSC, 0.4f)))
                drawRect(lp.s(0.35f), Offset(pr.left + 1f * u, pr.top + rr), Size(2.2f * u, pr.height - 2 * rr))
                drawRect(lp.b(0.4f), Offset(pr.right - 2.6f * u, pr.top + rr), Size(1.6f * u, pr.height - 2 * rr))
                drawPath(pp, lp.c(P.CROMO), style = Stroke(1.3f * u))
                drawPath(corridoRefugio(pp, -0.5f * u, -0.5f * u), lp.b(0.7f), style = Stroke(0.5f * u))
                if (lp.k > 0.06f) drawPath(pp, lp.filo(0.7f), style = Stroke(1f * u))
            }
            // De noche, el filo de la luz más cercana en el canto cromado, pintado solo en el tramo donde llega.
            if (g.a.oscuro || g.a.noche) {
                val filos = lc.map { it.filo(0.95f) }
                val con = filos.indices.filter { filos[it].alpha > 0.03f }
                if (con.isNotEmpty()) {
                    val i0 = maxOf(0, con.first() - 1); val i1 = minOf(ys.size - 1, con.last() + 1)
                    if (i1 > i0) drawRect(Brush.verticalGradient(filos.subList(i0, i1 + 1), ys[i0], ys[i1]), Offset(canto + an * 0.03f, ys[i0]), Size(2f * u, ys[i1] - ys[i0]))
                }
            }
            // Un botón cromado a la altura de cada tabla (los pernos que la sujetan).
            d.tablas.forEach { t ->
                val c = Offset((px0 + px1) / 2, t.center.y)
                botonCromoRefugio(c, w * 0.009f, g.luz(c, 0.045f))
            }
            // El esmalte saltado en los cantos.
            for (i in 0 until 8) {
                val yy2 = y0 + (y1 - y0) * azar(if (izq) 9240 else 9241, i)
                val x = if (i % 2 == 0) (if (izq) a + an * 0.08f else b - an * 0.08f) else (if (izq) b - an * 0.2f else a + an * 0.2f)
                val ls = g.luz(x, yy2, 0.04f)
                saltadoRefugio(Offset(x, yy2), w * (0.004f + 0.005f * azar(9250, i + (if (izq) 0 else 20))), 9260 + i + (if (izq) 0 else 30), ls.c(P.CROMO_OSC), ls.b(0.45f), ls.c(P.NARANJA))
            }
            if (izq) abolladuraRefugio(Offset(cx, y0 + (y1 - y0) * 0.72f), an * 0.3f, 0.035f * w, g.luz(cx, y0 + (y1 - y0) * 0.72f, 0.04f))
        }
    }

    // ---------------------------------------------------------------- la luz de guardia

    /**
     * La luz de guardia del costado derecho: el brazo cromado atornillado, el sombrerete y la cúpula de vidrio ámbar estriado. De día
     * apagada (el ámbar del vidrio, hondo, con su brillo); de noche arde y enciende lo de alrededor.
     */
    fun DrawScope.guardia(g: MarcoRefugio) {
        val w = g.w; val u = density
        val c = g.guardia
        if (c.y < g.ch + 0.06f * w || c.y > g.fondo - 0.02f * w) return
        val arde = g.a.guardia * (if (g.a.oscuro) 1f else 0.6f)
        val l = g.luz(c.x, c.y - 0.03f * w, 0.05f)
        if (arde > 0f && (g.a.oscuro || g.a.noche)) {
            mancha(c + Offset(-0.03f * w, 0.01f * w), w * 0.2f, P.AMBAR.copy(alpha = 0.22f * arde))
            mancha(c, w * 0.07f, P.AMBAR.copy(alpha = 0.5f * arde))
        }
        // La placa de base atornillada al costado.
        val placa = Rect(c.x - 0.022f * w, c.y - 0.06f * w, c.x + 0.022f * w, c.y - 0.036f * w)
        drawRoundRect(l.s(0.4f), placa.topLeft + Offset(1.2f * u, 1.6f * u), placa.size, CornerRadius(2f * u))
        drawRoundRect(l.c(P.CROMO_OSC), placa.topLeft, placa.size, CornerRadius(2f * u))
        cromoRefugio(placa.deflate(1f * u), l, brillo = 0.7f)
        // El sombrerete cromado.
        val som = Path().apply {
            moveTo(c.x - 0.012f * w, placa.bottom); lineTo(c.x + 0.012f * w, placa.bottom)
            lineTo(c.x + 0.021f * w, c.y - 0.014f * w); lineTo(c.x - 0.021f * w, c.y - 0.014f * w); close()
        }
        drawPath(corridoRefugio(som, 1.4f * u, 1.6f * u), l.s(0.35f))
        drawPath(som, Brush.horizontalGradient(listOf(l.c(P.CROMO_LUZ), l.c(P.CROMO), l.c(P.CROMO_OSC)), c.x - 0.021f * w, c.x + 0.021f * w))
        // La cúpula de vidrio ámbar estriado.
        val cup = Rect(c.x - 0.02f * w, c.y - 0.016f * w, c.x + 0.02f * w, c.y + 0.03f * w)
        val vidrio = Path().apply { addArc(Rect(cup.left, cup.top - cup.height, cup.right, cup.bottom), 0f, 180f); close() }
        val tono = if (arde > 0f && (g.a.oscuro || g.a.noche)) mezcla(P.AMBAR, Color.White, 0.25f * arde) else l.c(mezcla(P.AMBAR, P.NARANJA, 0.45f))
        if (arde > 0f && (g.a.oscuro || g.a.noche)) drawPath(vidrio, tono)
        else drawPath(vidrio, Brush.horizontalGradient(listOf(mezcla(tono, Color.White, 0.3f), tono, mezcla(tono, P.BAQUELITA, 0.4f)), cup.left, cup.right))
        clipPath(vidrio) {
            val estrias = Path()
            for (i in 1..5) { val x = cup.left + cup.width * i / 6f; estrias.addRect(Rect(x, cup.top, x + 0.7f * u, cup.bottom)) }
            drawPath(estrias, if (arde > 0f && (g.a.oscuro || g.a.noche)) Color.White.copy(alpha = 0.3f) else l.s(0.25f))
            if (arde > 0f && (g.a.oscuro || g.a.noche)) drawCircle(Color.White.copy(alpha = 0.7f * arde), cup.width * 0.22f, Offset(c.x, cup.top + cup.height * 0.15f))
            else drawRect(l.b(0.7f), Offset(cup.left + cup.width * 0.18f, cup.top), Size(cup.width * 0.1f, cup.height))
        }
        drawRect(l.c(P.CROMO_OSC), Offset(cup.left - 0.6f * u, cup.top - 0.8f * u), Size(cup.width + 1.2f * u, 1.8f * u))
    }
}
