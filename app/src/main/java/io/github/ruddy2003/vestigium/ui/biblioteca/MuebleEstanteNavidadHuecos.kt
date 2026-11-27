package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en los huecos libres de las repisas del estante de Navidad ([MuebleEstanteNavidad]): grupos de dos o tres cosas de la
 * casa, solapadas y distintas de un hueco a otro. La caja de esferas de vidrio con la tapa apoyada y el carrete de cinta; la jarra
 * de loza con ramas y las naranjas con clavos; los ovillos de lana con las agujas; el fajo de cartas atado con cinta y las piñas;
 * los platos buenos de canto con el paño doblado; los mitones de lana colgados de un clavito; la botella de licor con su copita;
 * la tetera esmaltada con su taza.
 * Donde hay sitio (nunca en dos repisas seguidas ni en la primera), la palmatoria con su vela, al fondo del hueco junto
 * al costado: de noche, la luz de en medio.
 */
internal object HuecosEstanteNavidad {
    private val P = PaletaNavidad

    /** Con qué grupo empieza el hueco de cada repisa (así no se repiten de una repisa a la siguiente). */
    private val ORDEN = intArrayOf(0, 3, 6, 1, 4, 7, 2, 5)

    /** La luz de lo que está en [p]: la del momento y, de noche, desde la luz que más alumbra. */
    private fun luzEn(p: Offset, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): LuzNavidad {
        val h = if (!a.oscuro) -1f else if (xLuz >= 0f) (if (xLuz > p.x) 1f else -1f) else -1f
        return LuzNavidad(a, cerca(p), 0f, null, 0f, h)
    }

    /** El radio del platillo de la palmatoria de un hueco y lo que mide su vela, en un lienzo de ancho [w] y un hueco de alto [alto]. */
    private fun platillo(w: Float) = w * 0.04f
    private fun vela(alto: Float) = alto * 0.42f

    /** Dónde arde la vela del hueco [r] con la palmatoria en [x] (lo mismo que devuelve [palmatoriaNavidad]). */
    fun llamaEn(x: Float, r: Rect, w: Float): Offset {
        val rx = platillo(w)
        val piso = r.bottom - w * 0.008f
        return Offset(x + rx * 0.03f, piso - rx * (0.24f * 1.6f + 0.32f + 0.42f) - vela(r.height))
    }

    /** El hueco [r] de la repisa [k], libre desde [oc] hasta [der]; [xVela] es dónde arde su vela (o -1). */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, der: Float, xVela: Float, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width
        val x0 = oc + w * 0.02f
        val x1 = der - w * 0.03f
        if (x1 - x0 < w * 0.07f) return
        val piso = r.bottom - w * 0.008f
        val alto = r.height
        var x = x0
        // La vela va al fondo, junto al costado: lo demás se acomoda delante de ella.
        val fin = if (xVela >= 0f) xVela - w * 0.06f else x1
        // Uno, dos o tres grupos, según el sitio; cada repisa empieza tres grupos más allá que la de arriba, así que dos repisas
        // seguidas nunca repiten grupo.
        var g = ORDEN[Math.floorMod(k, ORDEN.size)]
        var vueltas = 0
        while (fin - x > w * 0.12f && vueltas < 3) {
            val usado = when (g) {
                0 -> esferas(x, fin, piso, alto, a, cerca, xVela)
                1 -> jarraConRamas(x, fin, piso, alto, a, cerca, xVela)
                2 -> labor(x, fin, piso, alto, a, cerca, xVela)
                3 -> cartas(x, fin, piso, alto, a, cerca, xVela)
                4 -> platos(x, fin, piso, alto, a, cerca, xVela)
                5 -> mitones(x, fin, piso, alto, a, cerca, xVela)
                6 -> botella(x, fin, piso, alto, a, cerca, xVela)
                else -> tetera(x, fin, piso, alto, a, cerca, xVela)
            }
            x += usado + w * 0.03f
            g = (g + 1) % 8
            vueltas++
        }
        // En lo que sobra, una piña suelta.
        if (fin - x > w * 0.05f) {
            val c = Offset(x + w * 0.025f, piso - w * 0.024f)
            pinaNavidad(c, w * 0.02f, w * 0.026f, 70f, luzEn(c, a, cerca, xVela))
        }
        if (xVela >= 0f) {
            val lv = luzEn(Offset(xVela, piso - alto * 0.3f), a, cerca, xVela)
            palmatoriaNavidad(Offset(xVela, piso), platillo(w), vela(alto), LuzNavidad(a, maxOf(lv.k, if (a.oscuro) 0.9f else 0f), 0f, null, 0f, lv.hacia), 9800 + k)
        }
    }

    /** La caja de cartón de las esferas: la tapa apoyada detrás, los huecos de cartón con cuatro esferas y el carrete de cinta. */
    private fun DrawScope.esferas(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val ancho = minOf(w * 0.22f, x1 - x0)
        val l = luzEn(Offset(x0 + ancho / 2, piso - alto * 0.3f), a, cerca, xLuz)
        val carton = Color(0xFFC9A877)
        // La tapa, de pie detrás, ladeada, con su sombra en el fondo.
        val tapa = Rect(x0 + ancho * 0.08f, piso - alto * 0.78f, x0 + ancho * 0.72f, piso - alto * 0.1f)
        rotate(-6f, Offset(tapa.left, tapa.bottom)) {
            drawRect(l.sombra(0.3f), tapa.topLeft + Offset(-l.hacia * w * 0.016f, w * 0.01f), tapa.size)
            drawRect(l.c(carton), tapa.topLeft, tapa.size)
            drawRect(l.c(P.CARMIN), Offset(tapa.left, tapa.top + tapa.height * 0.38f), Size(tapa.width, tapa.height * 0.08f))
            drawRect(l.c(P.FILETE), Offset(tapa.left, tapa.top + tapa.height * 0.5f), Size(tapa.width, tapa.height * 0.05f))
            drawRect(l.sombra(0.22f), Offset(tapa.right - tapa.width * 0.2f, tapa.top), Size(tapa.width * 0.2f, tapa.height))
            drawRect(l.claro(0.3f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        }
        // La caja abierta, baja, con las esferas asomando en sus huecos.
        val caja = Rect(x0, piso - alto * 0.3f, x0 + ancho * 0.86f, piso)
        drawOval(l.sombra(0.45f), Offset(caja.left - w * 0.01f, piso - w * 0.012f), Size(caja.width + w * 0.03f, w * 0.022f))
        val cols = listOf(P.VIDRIO_ROJO, P.VIDRIO_PLATA, P.VIDRIO_ORO, P.VIDRIO_AZUL)
        val r = caja.width / 9f
        for (i in 0 until 4) {
            val c = Offset(caja.left + caja.width * (0.14f + 0.24f * i), caja.top - r * 0.3f)
            esferaNavidad(c, r, cols[(i + (x0 / w * 10).toInt()) % 4], l, i % 2 == 0)
        }
        drawRect(l.c(carton, -0.05f), caja.topLeft, caja.size)
        drawRect(l.c(mezcla(carton, P.TINTA, 0.25f)), caja.topLeft, Size(caja.width, caja.height * 0.14f))
        for (i in 1..3) drawLine(l.sombra(0.3f), Offset(caja.left + caja.width * 0.26f * i, caja.top), Offset(caja.left + caja.width * 0.26f * i, caja.top + caja.height * 0.2f), 1f * u)
        drawRect(l.sombra(0.25f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
        drawRect(l.claro(0.3f), caja.topLeft, Size(caja.width, 1.2f * u))
        // El carrete de cinta roja, delante, de lado.
        val cc = Offset(caja.right - w * 0.004f, piso - w * 0.03f)
        val lc = luzEn(cc, a, cerca, xLuz)
        drawOval(lc.sombra(0.4f), Offset(cc.x - w * 0.035f, piso - w * 0.008f), Size(w * 0.075f, w * 0.014f))
        drawCircle(lc.c(P.PINO), w * 0.03f, cc)
        drawCircle(lc.c(P.CARMIN), w * 0.024f, cc)
        drawCircle(lc.c(mezcla(P.CARMIN, P.TINTA, 0.35f)), w * 0.024f, cc, style = Stroke(1f * u))
        drawCircle(lc.c(P.PINO_OSC), w * 0.008f, cc)
        drawCircle(lc.sombra(0.3f), w * 0.03f, cc + Offset(w * 0.006f, w * 0.004f), style = Stroke(w * 0.008f))
        drawLine(lc.c(P.CARMIN), cc + Offset(w * 0.01f, w * 0.022f), Offset(cc.x + w * 0.05f, piso - w * 0.002f), w * 0.008f, StrokeCap.Round)
        return ancho + w * 0.04f
    }

    /** La jarra de loza con ramas de abeto y acebo, y dos naranjas con clavos delante. */
    private fun DrawScope.jarraConRamas(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width
        val ancho = minOf(w * 0.12f, (x1 - x0) * 0.6f)
        val pie = Offset(x0 + ancho * 0.6f, piso)
        val altoJ = alto * 0.5f
        val l = luzEn(Offset(pie.x, piso - altoJ * 0.5f), a, cerca, xLuz)
        val boca = Offset(pie.x, piso - altoJ * 0.98f)
        ramaAbetoNavidad(boca + Offset(-ancho * 0.1f, 0f), boca + Offset(-ancho * 0.6f, -alto * 0.3f), l, w * 0.022f)
        ramaAbetoNavidad(boca + Offset(ancho * 0.1f, 0f), boca + Offset(ancho * 0.55f, -alto * 0.26f), l, w * 0.022f)
        aceboNavidad(boca + Offset(-ancho * 0.05f, -alto * 0.18f), w * 0.042f, 15f, l)
        jarraNavidad(pie, ancho, altoJ, l)
        if (x1 - x0 > ancho * 1.3f + w * 0.05f) {
            listOf(Offset(pie.x + ancho * 0.55f, piso - w * 0.02f) to w * 0.02f, Offset(pie.x + ancho * 0.85f, piso - w * 0.017f) to w * 0.017f).forEachIndexed { i, (c, rr) ->
                naranjaNavidad(c, rr, luzEn(c, a, cerca, xLuz), 9850 + i)
            }
            return ancho * 1.05f + w * 0.03f
        }
        return ancho * 1.1f
    }

    /** La labor: los ovillos de lana (rojo, crema y verde), uno encima de otro, con las dos agujas clavadas y las tijeras. */
    private fun DrawScope.labor(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val r = w * 0.038f
        val c1 = Offset(x0 + r * 1.1f, piso - r)
        val c2 = Offset(c1.x + r * 1.75f, piso - r * 0.85f)
        val c3 = Offset(c1.x + r * 0.95f, c1.y - r * 1.45f)
        val l = luzEn(c3, a, cerca, xLuz)
        // Las agujas de tejer, clavadas en el de arriba, con su bolita.
        listOf(Offset(-0.5f, -1f) to -12f, Offset(0.6f, -1f) to 14f).forEach { (d, _) ->
            val p0 = c3 + Offset(d.x * r * 0.3f, r * 0.2f)
            val p1 = Offset(p0.x + d.x * r * 1.1f, p0.y - alto * 0.42f)
            drawLine(l.sombra(0.25f), p0 + Offset(w * 0.008f, w * 0.004f), p1 + Offset(w * 0.008f, w * 0.004f), 1.8f * u)
            drawLine(l.c(P.VIDRIO_PLATA, -0.1f), p0, p1, 1.8f * u, StrokeCap.Round)
            drawLine(l.brillo(0.6f), p0 - Offset(0.6f * u, 0f), p1 - Offset(0.6f * u, 0f), 0.6f * u)
            drawCircle(l.c(P.CARMIN), 2.6f * u, p1)
            drawCircle(l.brillo(0.6f), 0.9f * u, p1 - Offset(0.8f * u, 0.8f * u))
        }
        ovilloNavidad(c1, r, P.LANA_ROJA, luzEn(c1, a, cerca, xLuz), 9860)
        ovilloNavidad(c2, r * 0.85f, P.LANA_VERDE, luzEn(c2, a, cerca, xLuz), 9861)
        ovilloNavidad(c3, r * 0.92f, P.LANA_CREMA, l, 9862)
        // Las tijeras, tumbadas delante.
        val t = Offset(c2.x + r * 0.4f, piso - w * 0.004f)
        val lt = luzEn(t, a, cerca, xLuz)
        if (x1 - x0 > r * 4.5f) {
            drawLine(lt.c(P.VIDRIO_PLATA, -0.15f), t, t + Offset(w * 0.06f, -w * 0.004f), 1.8f * u, StrokeCap.Round)
            drawLine(lt.c(P.VIDRIO_PLATA, -0.25f), t + Offset(0f, -w * 0.006f), t + Offset(w * 0.058f, -w * 0.002f), 1.4f * u, StrokeCap.Round)
            drawCircle(lt.c(P.HIERRO_LUZ), w * 0.008f, t + Offset(-w * 0.006f, -w * 0.002f), style = Stroke(1.4f * u))
            drawCircle(lt.c(P.HIERRO_LUZ), w * 0.008f, t + Offset(-w * 0.004f, -w * 0.014f), style = Stroke(1.4f * u))
        }
        return r * 3.6f + w * 0.02f
    }

    /** El fajo de cartas atado con cinta roja, de pie y ladeado contra el costado, y dos piñas delante. */
    private fun DrawScope.cartas(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val ancho = minOf(w * 0.11f, (x1 - x0) * 0.55f)
        val fajo = Rect(x0 + w * 0.01f, piso - alto * 0.66f, x0 + w * 0.01f + ancho, piso)
        val l = luzEn(fajo.center, a, cerca, xLuz)
        rotate(7f, Offset(fajo.right, fajo.bottom)) {
            drawRect(l.sombra(0.3f), fajo.topLeft + Offset(-l.hacia * w * 0.016f, w * 0.01f), fajo.size)
            // Las cartas, una detrás de otra, de papeles de distinto tono y con los cantos que no casan.
            val tonos = listOf(P.PAPEL, P.CARTULINA, mezcla(P.PAPEL, P.SALVIA, 0.25f), P.CREMA, mezcla(P.PAPEL, P.CARMIN, 0.12f))
            for (i in 0 until 5) {
                val dx = w * 0.006f * (azar(9870, i) - 0.5f) * 2f
                val dy = w * 0.01f * azar(9871, i)
                drawRect(l.c(tonos[i], -0.04f * i), Offset(fajo.left + dx, fajo.top + dy), Size(fajo.width, fajo.height - dy))
                drawLine(l.sombra(0.2f), Offset(fajo.left + dx, fajo.top + dy), Offset(fajo.right + dx, fajo.top + dy), 0.8f * u)
            }
            // La solapa del sobre de delante y su sello de lacre.
            val frente = fajo
            drawPath(poligonoNavidad(frente.left, frente.top + w * 0.008f, frente.right, frente.top + w * 0.008f, frente.center.x, frente.top + frente.height * 0.32f), l.sombra(0.12f))
            drawLine(l.c(P.CARTULINA_OSC), Offset(frente.left, frente.top + w * 0.008f), Offset(frente.center.x, frente.top + frente.height * 0.32f), 0.8f * u)
            drawLine(l.c(P.CARTULINA_OSC), Offset(frente.right, frente.top + w * 0.008f), Offset(frente.center.x, frente.top + frente.height * 0.32f), 0.8f * u)
            // El cordel crudo que lo ata, descentrado (dos vueltas a lo alto, cerca del canto, y una a lo ancho, abajo), con su nudo y
            // una ramita de acebo metida debajo.
            val cx = frente.left + frente.width * 0.3f
            listOf(0f, w * 0.006f).forEach { d -> drawLine(l.c(P.CARTULINA, -0.12f), Offset(cx + d, frente.top), Offset(cx + d, frente.bottom), 1.4f * u) }
            drawLine(l.c(P.CARTULINA, -0.12f), Offset(frente.left, frente.top + frente.height * 0.7f), Offset(frente.right, frente.top + frente.height * 0.72f), 1.4f * u)
            aceboNavidad(Offset(cx + w * 0.004f, frente.top + frente.height * 0.7f), w * 0.05f, -20f, l)
            drawCircle(l.c(P.CARTULINA_OSC), 2.2f * u, Offset(cx + w * 0.003f, frente.top + frente.height * 0.71f))
            drawRect(l.sombra(0.2f), Offset(frente.right - frente.width * 0.18f, frente.top), Size(frente.width * 0.18f, frente.height))
            drawRect(l.claro(0.3f), frente.topLeft, Size(1.2f * u, frente.height))
        }
        if (x1 - x0 > ancho + w * 0.08f) {
            val c1 = Offset(fajo.right + w * 0.035f, piso - w * 0.028f)
            pinaNavidad(c1, w * 0.024f, w * 0.03f, 8f, luzEn(c1, a, cerca, xLuz))
            val c2 = Offset(c1.x + w * 0.035f, piso - w * 0.02f)
            pinaNavidad(c2, w * 0.018f, w * 0.024f, 75f, luzEn(c2, a, cerca, xLuz))
            return ancho + w * 0.1f
        }
        return ancho + w * 0.02f
    }

    /**
     * Los platos buenos de loza, de canto contra el fondo (el de delante con su filete verde y su corona de acebo pintada), y el
     * paño doblado delante, con sus rayas rojas.
     */
    private fun DrawScope.platos(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val r = minOf(alto * 0.36f, (x1 - x0) * 0.42f)
        val c0 = Offset(x0 + r * 1.05f, piso - r * 1.02f)
        for (i in 0 until 3) {
            val c = c0 + Offset(r * 0.14f * (2 - i), -r * 0.04f * (2 - i))
            val l = luzEn(c, a, cerca, xLuz)
            val ov = Rect(c.x - r, c.y - r, c.x + r, c.y + r)
            drawOval(l.sombra(0.3f), ov.topLeft + Offset(-l.hacia * r * 0.18f, r * 0.08f), ov.size)
            drawOval(l.c(P.LOZA, -0.05f * (2 - i)), ov.topLeft, ov.size)
            drawOval(l.c(P.FILETE), ov.deflate(r * 0.08f).topLeft, ov.deflate(r * 0.08f).size, style = Stroke(r * 0.05f))
            if (i == 2) {
                val fondo = ov.deflate(r * 0.3f)
                drawOval(l.c(P.LOZA, -0.12f), fondo.topLeft, fondo.size)
                val hojas = Path(); val bayas = Path()
                for (j in 0 until 10) {
                    val an = j * 0.628f
                    val p = Offset(c.x + r * 0.5f * kotlin.math.cos(an), c.y + r * 0.5f * kotlin.math.sin(an))
                    hojas.addOval(Rect(p.x - r * 0.07f, p.y - r * 0.04f, p.x + r * 0.07f, p.y + r * 0.04f))
                    if (j % 2 == 0) bayas.addOval(Rect(Offset(c.x + r * 0.38f * kotlin.math.cos(an + 0.3f), c.y + r * 0.38f * kotlin.math.sin(an + 0.3f)), r * 0.045f))
                }
                drawPath(hojas, l.c(P.FILETE_LUZ))
                drawPath(bayas, l.c(P.CARMIN))
                drawArc(l.sombra(0.2f), 200f, 140f, false, fondo.topLeft, fondo.size, style = Stroke(r * 0.06f))
                drawArc(l.brillo(0.6f), 150f, 70f, false, ov.deflate(r * 0.02f).topLeft, ov.deflate(r * 0.02f).size, style = Stroke(1.4f * u))
            }
        }
        // El paño doblado, tumbado delante: tres capas de lino con sus rayas rojas en el canto.
        val pano = Rect(x0 + r * 0.5f, piso - w * 0.04f, x0 + r * 2.3f, piso)
        val lp = luzEn(pano.center, a, cerca, xLuz)
        drawRect(lp.sombra(0.4f), Offset(pano.left + w * 0.006f, pano.bottom - w * 0.006f), Size(pano.width, w * 0.008f))
        drawRoundRect(lp.c(P.LANA_CREMA), pano.topLeft, pano.size, CornerRadius(w * 0.008f))
        for (j in 1..2) drawLine(lp.sombra(0.3f), Offset(pano.left + w * 0.004f, pano.top + pano.height * j / 3f), Offset(pano.right - w * 0.004f, pano.top + pano.height * j / 3f), 0.9f * u)
        listOf(0.18f, 0.24f, 0.3f).forEach { f -> drawRect(lp.c(P.CARMIN), Offset(pano.left + pano.width * f, pano.top), Size(pano.width * 0.025f, pano.height)) }
        drawRect(lp.claro(0.35f), pano.topLeft, Size(pano.width, 1.2f * u))
        return r * 2.4f
    }

    /**
     * Los mitones de lana atados con su cordón, colgados de un clavito y caídos uno sobre otro: el rojo y el verde, de punto, con
     * su puño crema y el pulgar; y dos piñas al pie.
     */
    private fun DrawScope.mitones(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val ancho = w * 0.075f
        val clavo = Offset(x0 + ancho * 1.1f, piso - alto * 0.86f)
        val l0 = luzEn(clavo, a, cerca, xLuz)
        clavoNavidad(clavo, 1.8f * u, a, l0.k)
        listOf(P.LANA_ROJA to -1f, P.LANA_VERDE to 1f).forEachIndexed { i, (col, s0) ->
            val top = Offset(clavo.x + s0 * ancho * 0.45f, clavo.y + alto * (0.18f + 0.06f * i))
            val l = luzEn(top + Offset(0f, alto * 0.2f), a, cerca, xLuz)
            drawLine(l.c(P.LANA_CREMA, -0.1f), clavo, top, 1.3f * u)
            rotate(s0 * 6f, top) {
                val cuerpo = Path().apply {
                    moveTo(top.x - ancho * 0.42f, top.y)
                    lineTo(top.x + ancho * 0.42f, top.y)
                    lineTo(top.x + ancho * 0.46f, top.y + alto * 0.32f)
                    cubicTo(top.x + ancho * 0.48f, top.y + alto * 0.5f, top.x - ancho * 0.48f, top.y + alto * 0.5f, top.x - ancho * 0.46f, top.y + alto * 0.32f)
                    close()
                }
                val pulgar = Path().apply { addOval(Rect(top.x + s0 * ancho * 0.5f - ancho * 0.16f, top.y + alto * 0.1f, top.x + s0 * ancho * 0.5f + ancho * 0.16f, top.y + alto * 0.28f)) }
                drawPath(corridoNavidad(cuerpo, -l.hacia * w * 0.01f, w * 0.008f), l.sombra(0.3f))
                drawPath(pulgar, l.c(col, -0.1f))
                drawPath(cuerpo, l.c(col))
                clipPath(cuerpo) {
                    val punto = Path()
                    var yy = top.y + alto * 0.1f; var j = 0
                    while (yy < top.y + alto * 0.5f && j < 40) { var xx = top.x - ancho * 0.5f + (if (j % 2 == 0) 0f else 1.6f * u); while (xx < top.x + ancho * 0.5f) { punto.moveTo(xx, yy); punto.lineTo(xx + 1.2f * u, yy + 2.2f * u); punto.lineTo(xx + 2.4f * u, yy); xx += 3.4f * u }; yy += 2.8f * u; j++ }
                    drawPath(punto, l.c(mezcla(col, P.TINTA, 0.35f)).copy(alpha = 0.4f), style = Stroke(0.6f * u))
                    drawRect(l.c(P.LANA_CREMA), Offset(top.x - ancho, top.y), Size(ancho * 2f, alto * 0.08f))
                    drawRect(l.c(P.LANA_CREMA), Offset(top.x - ancho, top.y + alto * 0.24f), Size(ancho * 2f, alto * 0.025f))
                    drawRect(l.sombra(0.25f), Offset(top.x + ancho * 0.12f * (-l.hacia) - ancho * 0.1f, top.y), Size(ancho * 0.4f, alto * 0.5f))
                }
                drawPath(cuerpo, l.c(mezcla(col, P.TINTA, 0.4f)).copy(alpha = 0.6f), style = Stroke(0.9f * u))
            }
        }
        if (x1 - x0 > ancho * 2.4f + w * 0.05f) {
            val c = Offset(x0 + ancho * 2.4f, piso - w * 0.026f)
            pinaNavidad(c, w * 0.022f, w * 0.028f, -10f, luzEn(c, a, cerca, xLuz))
            return ancho * 2.4f + w * 0.03f
        }
        return ancho * 2.2f
    }

    /**
     * La botella de licor de guindas hecho en casa, alta, de vidrio verde oscuro con el corcho y una cinta roja al cuello (sin
     * etiqueta), y la copita delante, con su reflejo.
     */
    private fun DrawScope.botella(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val ancho = minOf(w * 0.075f, (x1 - x0) * 0.45f)
        val cx = x0 + ancho * 0.6f
        val h = alto * 0.8f
        val l = luzEn(Offset(cx, piso - h * 0.4f), a, cerca, xLuz)
        val hombro = piso - h * 0.55f; val cuello = piso - h * 0.82f
        val vidrio = Path().apply {
            moveTo(cx - ancho / 2, piso)
            lineTo(cx - ancho / 2, hombro)
            cubicTo(cx - ancho / 2, hombro - h * 0.12f, cx - ancho * 0.16f, cuello + h * 0.06f, cx - ancho * 0.15f, cuello)
            lineTo(cx - ancho * 0.15f, piso - h)
            lineTo(cx + ancho * 0.15f, piso - h)
            lineTo(cx + ancho * 0.15f, cuello)
            cubicTo(cx + ancho * 0.16f, cuello + h * 0.06f, cx + ancho / 2, hombro - h * 0.12f, cx + ancho / 2, hombro)
            lineTo(cx + ancho / 2, piso); close()
        }
        drawPath(corridoNavidad(vidrio, -l.hacia * w * 0.014f, w * 0.006f), l.sombra(0.3f))
        drawOval(l.sombra(0.45f), Offset(cx - ancho * 0.6f, piso - w * 0.008f), Size(ancho * 1.25f, w * 0.016f))
        drawPath(vidrio, l.c(Color(0xFF243E2A)))
        clipPath(vidrio) {
            // El licor de dentro, más rojo hasta el hombro, y el vidrio con su luz y su sombra.
            drawRect(l.c(Color(0xFF5A1620)), Offset(cx - ancho, hombro + h * 0.05f), Size(ancho * 2f, piso - hombro))
            drawRect(l.c(Color(0xFF3E6A48)).copy(alpha = 0.5f), Offset(cx + l.hacia * ancho * 0.3f - ancho * 0.12f, piso - h), Size(ancho * 0.24f, h))
            drawRect(l.sombra(0.3f), Offset(cx - l.hacia * ancho * 0.35f - ancho * 0.15f, piso - h), Size(ancho * 0.3f, h))
            drawRect(l.brillo(0.75f), Offset(cx + l.hacia * ancho * 0.28f - 1f * u, hombro - h * 0.05f), Size(1.6f * u, h * 0.5f))
        }
        // El corcho y la cinta roja anudada al cuello.
        drawRect(l.c(P.PINO), Offset(cx - ancho * 0.13f, piso - h - h * 0.06f), Size(ancho * 0.26f, h * 0.08f))
        drawRect(l.c(P.CARMIN), Offset(cx - ancho * 0.17f, cuello + h * 0.02f), Size(ancho * 0.34f, h * 0.035f))
        lazoNavidad(Offset(cx + ancho * 0.1f, cuello + h * 0.04f), ancho * 0.6f, P.CARMIN, l, 0.8f)
        // La copita, delante a la derecha.
        val c = Offset(cx + ancho * 0.85f, piso)
        if (x1 - x0 > ancho * 1.5f + w * 0.03f) {
            val lc = luzEn(Offset(c.x, piso - w * 0.03f), a, cerca, xLuz)
            val cw = w * 0.034f
            drawOval(lc.sombra(0.4f), Offset(c.x - cw * 0.6f, piso - w * 0.005f), Size(cw * 1.3f, w * 0.01f))
            drawLine(lc.c(P.VIDRIO_PLATA, -0.2f), Offset(c.x, piso - w * 0.004f), Offset(c.x, piso - w * 0.022f), 1.4f * u)
            drawOval(lc.c(P.VIDRIO_PLATA, -0.1f).copy(alpha = 0.7f), Offset(c.x - cw * 0.4f, piso - w * 0.008f), Size(cw * 0.8f, w * 0.006f))
            val copa = Path().apply {
                moveTo(c.x - cw / 2, piso - w * 0.05f); lineTo(c.x + cw / 2, piso - w * 0.05f)
                quadraticTo(c.x + cw * 0.45f, piso - w * 0.022f, c.x, piso - w * 0.022f)
                quadraticTo(c.x - cw * 0.45f, piso - w * 0.022f, c.x - cw / 2, piso - w * 0.05f); close()
            }
            drawPath(copa, lc.c(Color(0xFF7A2030)))
            drawPath(copa, lc.c(P.VIDRIO_PLATA, 0.1f).copy(alpha = 0.7f), style = Stroke(1f * u))
            drawLine(lc.brillo(0.7f), Offset(c.x - cw * 0.3f, piso - w * 0.047f), Offset(c.x - cw * 0.15f, piso - w * 0.03f), 1f * u)
            return ancho * 1.5f + w * 0.03f
        }
        return ancho * 1.1f
    }

    /** La tetera esmaltada de rojo, con su borde y su asa negros y el pico, y la taza de loza blanca con su plato delante. */
    private fun DrawScope.tetera(x0: Float, x1: Float, piso: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, xLuz: Float): Float {
        val w = size.width; val u = density
        val ancho = minOf(w * 0.15f, (x1 - x0) * 0.7f)
        val cx = x0 + ancho * 0.55f
        val h = ancho * 0.72f
        val l = luzEn(Offset(cx, piso - h * 0.5f), a, cerca, xLuz)
        val esmalte = Color(0xFFB23126); val filo = Color(0xFF1E1A1C)
        val cuerpo = Path().apply {
            moveTo(cx - ancho * 0.36f, piso)
            cubicTo(cx - ancho * 0.52f, piso - h * 0.3f, cx - ancho * 0.44f, piso - h * 0.78f, cx - ancho * 0.2f, piso - h * 0.82f)
            lineTo(cx + ancho * 0.2f, piso - h * 0.82f)
            cubicTo(cx + ancho * 0.44f, piso - h * 0.78f, cx + ancho * 0.52f, piso - h * 0.3f, cx + ancho * 0.36f, piso)
            close()
        }
        drawOval(l.sombra(0.45f), Offset(cx - ancho * 0.42f, piso - w * 0.008f), Size(ancho * 0.95f, w * 0.016f))
        drawPath(corridoNavidad(cuerpo, -l.hacia * w * 0.018f, w * 0.006f), l.sombra(0.3f))
        // El asa de atrás (a la izquierda) y el pico (a la derecha).
        drawArc(l.c(filo), 100f, 160f, false, Offset(cx - ancho * 0.62f, piso - h * 0.75f), Size(ancho * 0.36f, h * 0.55f), style = Stroke(ancho * 0.05f))
        val pico = Path().apply {
            moveTo(cx + ancho * 0.34f, piso - h * 0.3f); quadraticTo(cx + ancho * 0.56f, piso - h * 0.42f, cx + ancho * 0.64f, piso - h * 0.7f)
            lineTo(cx + ancho * 0.58f, piso - h * 0.72f); quadraticTo(cx + ancho * 0.5f, piso - h * 0.5f, cx + ancho * 0.3f, piso - h * 0.48f); close()
        }
        drawPath(pico, l.c(esmalte, -0.1f))
        drawPath(cuerpo, l.c(esmalte))
        clipPath(cuerpo) {
            drawRect(l.claro(0.3f), Offset(cx - ancho * 0.45f, piso - h), Size(ancho * 0.16f, h))
            drawRect(l.sombra(0.3f), Offset(cx + ancho * 0.18f, piso - h), Size(ancho * 0.3f, h))
            drawOval(l.brillo(0.8f), Offset(cx - ancho * 0.25f, piso - h * 0.62f), Size(ancho * 0.06f, h * 0.22f))
            // Una saltadura del esmalte, cerca del pie, que deja ver el hierro.
            drawOval(l.c(filo), Offset(cx + ancho * 0.06f, piso - h * 0.16f), Size(ancho * 0.06f, h * 0.05f))
        }
        drawLine(l.c(filo), Offset(cx - ancho * 0.36f, piso - 0.6f * u), Offset(cx + ancho * 0.36f, piso - 0.6f * u), 1.6f * u)
        // La tapa con su pomo.
        drawOval(l.c(filo), Offset(cx - ancho * 0.22f, piso - h * 0.88f), Size(ancho * 0.44f, h * 0.1f))
        drawOval(l.c(esmalte), Offset(cx - ancho * 0.18f, piso - h * 0.92f), Size(ancho * 0.36f, h * 0.08f))
        drawCircle(l.c(filo), ancho * 0.045f, Offset(cx, piso - h * 0.95f))
        // La taza con su plato, delante a la derecha.
        if (x1 - x0 > ancho * 1.35f + w * 0.03f) {
            val tc = Offset(cx + ancho * 0.66f, piso)
            val lt = luzEn(Offset(tc.x, piso - w * 0.02f), a, cerca, xLuz)
            val tw = w * 0.055f
            drawOval(lt.sombra(0.4f), Offset(tc.x - tw * 0.75f, piso - w * 0.006f), Size(tw * 1.6f, w * 0.012f))
            drawOval(lt.c(P.LOZA, -0.06f), Offset(tc.x - tw * 0.75f, piso - w * 0.01f), Size(tw * 1.5f, w * 0.01f))
            val taza = Path().apply {
                moveTo(tc.x - tw / 2, piso - w * 0.042f); lineTo(tc.x + tw / 2, piso - w * 0.042f)
                quadraticTo(tc.x + tw * 0.48f, piso - w * 0.008f, tc.x, piso - w * 0.008f)
                quadraticTo(tc.x - tw * 0.48f, piso - w * 0.008f, tc.x - tw / 2, piso - w * 0.042f); close()
            }
            drawArc(lt.c(P.LOZA, -0.1f), -70f, 140f, false, Offset(tc.x + tw * 0.36f, piso - w * 0.038f), Size(tw * 0.3f, w * 0.022f), style = Stroke(1.6f * u))
            drawPath(taza, lt.c(P.LOZA))
            drawLine(lt.c(P.FILETE), Offset(tc.x - tw * 0.48f, piso - w * 0.035f), Offset(tc.x + tw * 0.48f, piso - w * 0.035f), 1.4f * u)
            drawOval(lt.c(P.CORTEZA, -0.1f), Offset(tc.x - tw * 0.46f, piso - w * 0.046f), Size(tw * 0.92f, w * 0.008f))
            drawPath(taza, lt.sombra(0.2f), style = Stroke(0.8f * u))
            return ancho * 1.35f + w * 0.03f
        }
        return ancho * 1.05f
    }

    /** La llama de la vela de un hueco, encima de todo (de noche y al ocaso de los Sueños), con su halo en el fondo del hueco. */
    fun DrawScope.llamaHueco(x: Float, r: Rect, a: AireNavidad) {
        if (!a.lumbre) return
        val w = size.width
        val p = llamaEn(x, r, w)
        if (a.oscuro) {
            val f = if (a.noche) 0.55f else 1f
            mancha(p, w * 0.16f, P.LLAMA.copy(alpha = 0.2f * f))
            mancha(p, w * 0.06f, P.LLAMA.copy(alpha = 0.3f * f))
        }
        llamaNavidad(p, w * 0.028f, if (a.oscuro && a.noche) 0.6f else 1f)
    }
}
