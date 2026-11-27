package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en los huecos libres de las repisas del estante de Navidad ([MuebleEstanteNavidad]): grupos de dos o tres cosas de la
 * casa, solapadas y distintas de un hueco a otro. La caja de esferas de vidrio con la tapa apoyada y el carrete de cinta; la jarra
 * de loza con ramas y las naranjas con clavos; los ovillos de lana con las agujas; el fajo de cartas atado con cinta y las piñas.
 * Donde hay sitio (nunca en dos repisas seguidas ni en la primera), la palmatoria con su vela: de noche, la luz de en medio.
 */
internal object HuecosEstanteNavidad {
    private val P = PaletaNavidad

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
        if (xVela >= 0f) {
            val lv = luzEn(Offset(xVela, piso - alto * 0.3f), a, cerca, xVela)
            palmatoriaNavidad(Offset(xVela, piso), platillo(w), vela(alto), LuzNavidad(a, maxOf(lv.k, if (a.oscuro) 0.9f else 0f), 0f, null, 0f, lv.hacia), 9800 + k)
            x = xVela + w * 0.075f
        }
        // Uno o dos grupos, según el sitio; cambia de un hueco a otro.
        var g = Math.floorMod(k * 3 + (if (xVela >= 0f) 1 else 0), 4)
        var vueltas = 0
        while (x1 - x > w * 0.12f && vueltas < 3) {
            val usado = when (g) {
                0 -> esferas(x, x1, piso, alto, a, cerca, xVela)
                1 -> jarraConRamas(x, x1, piso, alto, a, cerca, xVela)
                2 -> labor(x, x1, piso, alto, a, cerca, xVela)
                else -> cartas(x, x1, piso, alto, a, cerca, xVela)
            }
            x += usado + w * 0.03f
            g = (g + 1) % 4
            vueltas++
        }
        // En lo que sobra, una piña suelta.
        if (x1 - x > w * 0.05f) {
            val c = Offset(x + w * 0.025f, piso - w * 0.024f)
            pinaNavidad(c, w * 0.02f, w * 0.026f, 70f, luzEn(c, a, cerca, xVela))
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
            // La cinta roja en cruz con su lazo.
            drawRect(l.c(P.CARMIN), Offset(frente.left, frente.top + frente.height * 0.55f), Size(frente.width, w * 0.012f))
            drawRect(l.c(P.CARMIN), Offset(frente.center.x - w * 0.006f, frente.top), Size(w * 0.012f, frente.height))
            lazoNavidad(Offset(frente.center.x, frente.top + frente.height * 0.55f + w * 0.006f), w * 0.05f, P.CARMIN, l, 0.6f)
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
