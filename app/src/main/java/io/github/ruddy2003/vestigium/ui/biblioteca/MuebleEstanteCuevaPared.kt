package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * La pared de la boca de la cueva detrás del andamio: caliza que a la sombra se ve gris azulada (la alumbra el cielo) y al sol
 * crema cálida, con sus abultamientos, sus grietas y el techo ennegrecido por los fuegos. El grupo la decoró: un lavado de ocre
 * rojo, hileras de puntos rojos y negros, trazos de dedos en la arcilla blanda y dos manos en negativo sopladas con negro de
 * manganeso. Dentro de cada hueco, la misma roca en la sombra de la repisa de arriba.
 */
internal object ParedEstanteCueva {

    /**
     * La pared de arriba (la corona), pintada dos veces: entera a la luz de la cueva y, recortada a la mancha de la boca, otra
     * vez al sol (de día), al ocaso o a la luna (en los Sueños). Así la luz pinta lo que toca, con su borde neto.
     */
    fun DrawScope.paredArriba(g: MarcoCueva) {
        pared(g, 0f)
        g.mancha(0f)?.let { m -> clipPath(m) { pared(g, 1f) } }
        // El techo de la boca no lo toca la luz que entra: queda fuera de la mancha.
        techo(g, 0f)
    }

    private fun DrawScope.pared(g: MarcoCueva, sol: Float) {
        val w = g.w; val ch = g.ch
        val u = density
        fun l(x: Float, y: Float) = g.luz(x, y, 0f, sol)
        // ---- la roca: de la boca (arriba a la izquierda) hacia dentro, cada vez más honda.
        val c0 = l(0f, 0f).atenuada(0.4f).c(P.CALIZA); val c1 = l(w, ch).atenuada(0.4f).c(P.CALIZA)
        drawRect(Brush.linearGradient(listOf(c0, mezcla(c0, c1, 0.5f), c1), Offset(0f, 0f), Offset(w, ch)), Offset.Zero, Size(w, ch))
        // De noche, el charco de la lámpara en la roca (debajo de todo lo demás).
        if (g.a.lampara > 0f) {
            val lp = g.lampara
            val lc = l(lp.x, lp.y - w * 0.05f)
            mancha(Offset(lp.x, lp.y - w * 0.04f), w * 0.42f, lc.c(P.CALIZA).copy(alpha = 0.8f))
            mancha(Offset(lp.x, lp.y - w * 0.05f), w * 0.22f, mezcla(lc.c(P.CALIZA), P.LLAMA, 0.35f).copy(alpha = 0.7f * g.a.lampara))
        }
        // Los abultamientos de la caliza: anchos y bajos, cada uno con su luz arriba a la izquierda y su sombra abajo.
        BULTOS.forEachIndexed { i, (x, y, r) ->
            val c = Offset(w * x, w * y)
            if (c.y - w * r * 0.6f > ch) return@forEachIndexed
            val ll = l(c.x, c.y)
            val f = piedraCueva(c, w * r * 1.6f, w * r * 0.62f, 3100 + i, 10, 0.2f)
            drawPath(f, ll.c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.22f)).copy(alpha = 0.7f))
            drawPath(bordeCueva(f, -w * 0.012f, -w * 0.01f), ll.s(0.16f))
            drawPath(bordeCueva(f, w * 0.006f, w * 0.007f), ll.b(0.4f))
        }
        // Los lavados de ocre rojo: la roca untada a manos llenas, a manchas desiguales.
        LAVADOS.forEachIndexed { i, (x, y, r) ->
            val c = Offset(w * x, w * y)
            val ll = l(c.x, c.y)
            drawPath(piedraCueva(c, w * r * 1.4f, w * r, 3200 + i, 11, 0.32f), ll.c(P.OCRE_ROJO).copy(alpha = 0.38f))
            drawPath(piedraCueva(c + Offset(w * r * 0.3f, -w * r * 0.2f), w * r * 0.8f, w * r * 0.6f, 3210 + i, 9, 0.35f), ll.c(mezcla(P.OCRE_ROJO, P.CARBON, 0.2f)).copy(alpha = 0.3f))
            for (k in 0 until 9) {
                val q = c + Offset(w * r * 1.3f * (azar(3220 + i, k) - 0.5f) * 2f, w * r * (azar(3230 + i, k) - 0.5f) * 1.8f)
                drawCircle(ll.c(P.OCRE_ROJO).copy(alpha = 0.3f), w * (0.004f + 0.006f * azar(3240 + i, k)), q)
            }
        }
        pinceladas(Rect(0f, 0f, w, ch), l(w * 0.3f, ch * 0.4f).c(P.CALIZA_SOL), 3301, 30, u, 0.12f)
        pinceladas(Rect(0f, 0f, w, ch), l(w * 0.6f, ch * 0.6f).c(P.CALIZA_HONDA), 3302, 26, u, 0.12f)
        // Las grietas: quebradas, con su labio de luz a la izquierda.
        grieta(listOf(Offset(w * 0.6f, w * 0.05f), Offset(w * 0.585f, w * 0.1f), Offset(w * 0.6f, w * 0.14f), Offset(w * 0.582f, w * 0.19f)), l(w * 0.6f, w * 0.12f))
        grieta(listOf(Offset(w * 0.97f, w * 0.2f), Offset(w * 0.955f, w * 0.26f), Offset(w * 0.968f, w * 0.31f), Offset(w * 0.95f, w * 0.37f), Offset(w * 0.962f, w * 0.44f)), l(w * 0.96f, w * 0.3f))
        // ---- lo pintado: la hilera de puntos rojos bajo el techo, en arco, y otra de puntos negros más corta.
        for (i in 0 until 15) {
            val t = i / 14f
            val q = Offset(w * (0.06f + 0.5f * t), w * (0.088f + 0.024f * sin(t * 3.1416f) + 0.004f * azar(3401, i)))
            drawCircle(l(q.x, q.y).c(P.OCRE_ROJO).copy(alpha = 0.85f), w * (0.0062f + 0.0018f * azar(3402, i)), q)
        }
        for (i in 0 until 6) {
            val q = Offset(w * (0.74f + 0.028f * i), w * (0.4f + 0.012f * (i % 2)))
            drawCircle(l(q.x, q.y).c(P.MANGANESO).copy(alpha = 0.8f), w * (0.0058f + 0.0015f * azar(3403, i)), q)
        }
        // Las manos en negativo, sopladas con manganeso (las del Ideario son rojas).
        manoSoplada(Offset(w * 0.275f, w * 0.235f), w * 0.034f, -14f, l(w * 0.275f, w * 0.235f), 3501)
        manoSoplada(Offset(w * 0.79f, w * 0.215f), w * 0.03f, 18f, l(w * 0.79f, w * 0.215f), 3502)
        // Los trazos de dedos en la arcilla blanda: surcos paralelos que ondulan.
        for (k in 0 until 4) {
            val x = w * (0.745f + 0.016f * k)
            val tr = abiertaCueva(List(6) { j -> Offset(x + w * 0.012f * sin(j * 1.2f + k * 0.5f), w * (0.27f + 0.022f * j)) })
            val ll = l(x, w * 0.33f)
            drawPath(tr, ll.s(0.32f), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
            drawPath(corridoCueva(tr, -w * 0.0025f, 0f), ll.b(0.42f), style = Stroke(w * 0.0025f, cap = StrokeCap.Round))
        }
        // ---- el hollín que la lámpara deja en la pared, encima de la llama, y el techo de la boca.
        val lp = g.lampara
        for (k in 0 until 6) {
            val r = w * (0.014f + 0.012f * k)
            val q = Offset(lp.x + w * (0.008f * k + 0.01f * (azar(3601, k) - 0.5f)), lp.y - w * (0.05f + 0.04f * k))
            drawOval(P.HOLLIN.copy(alpha = 0.07f - 0.006f * k), Offset(q.x - r * 0.8f, q.y - r * 1.4f), Size(r * 1.6f, r * 2.8f))
        }
    }

    /** Los abultamientos de la pared de arriba: (x, y, radio), en anchos. */
    private val BULTOS = listOf(Triple(0.1f, 0.2f, 0.11f), Triple(0.45f, 0.08f, 0.14f), Triple(0.86f, 0.3f, 0.1f), Triple(0.3f, 0.42f, 0.12f), Triple(0.66f, 0.46f, 0.13f))
    /** Los lavados de ocre: (x, y, radio). */
    private val LAVADOS = listOf(Triple(0.87f, 0.11f, 0.05f))

    /** El techo de la boca que se cierra arriba: la roca más honda y ennegrecida, con su borde quebrado y su labio de luz. */
    private fun DrawScope.techo(g: MarcoCueva, sol: Float) {
        val w = g.w
        val borde = List(13) { k -> Offset(w * (-0.04f + 1.08f * k / 12f), w * (0.028f + 0.03f * azar(3701, k) + 0.014f * sin(k * 1.7f))) }
        val f = cerradaCueva(borde + listOf(Offset(w * 1.1f, -w * 0.1f), Offset(-w * 0.1f, -w * 0.1f)))
        val l = g.luz(w * 0.4f, w * 0.02f, 0f, sol)
        drawPath(corridoCueva(f, w * 0.004f, w * 0.012f), l.s(0.3f))
        drawPath(f, l.c(P.TECHO))
        drawPath(f, P.HOLLIN.copy(alpha = if (g.a.oscuro) 0.2f else 0.32f))
        drawPath(bordeCueva(f, 0f, -w * 0.006f), l.b(0.36f))
        // El hollín de siglos, más negro encima de la lámpara.
        drawPath(piedraCueva(Offset(g.lampara.x + w * 0.04f, w * 0.03f), w * 0.16f, w * 0.05f, 3702, 10, 0.3f), P.HOLLIN.copy(alpha = 0.3f))
        if (g.a.lampara > 0f) mancha(Offset(g.lampara.x, w * 0.05f), w * 0.2f, P.LLAMA.copy(alpha = 0.16f * g.a.lampara))
    }

    /** Una grieta: el hueco en sombra, quebrado, y a su izquierda el labio que coge la luz. */
    private fun DrawScope.grieta(q: List<Offset>, l: LuzCueva) {
        val u = density
        val p = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        drawPath(p, l.s(0.62f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(p, -1.4f * u, 0f), l.b(0.42f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    /**
     * Una mano en negativo soplada con manganeso: el negro salpicado alrededor, espeso junto a la mano y ralo hacia fuera, y la
     * mano en el color de la roca. Cuatro dedos y el pulgar, abiertos.
     */
    private fun DrawScope.manoSoplada(c: Offset, r: Float, giro: Float, l: LuzCueva, sem: Int) {
        val u = density
        val o = l.c(P.MANGANESO)
        mancha(c + Offset(r * 0.2f, -r * 0.1f), r * 1.7f, o.copy(alpha = 0.34f))
        for (k in 0 until 4) drawPath(piedraCueva(c + Offset(r * (azar(sem, k + 900) - 0.5f) * 1.2f, r * (azar(sem, k + 910) - 0.5f) * 1.2f), r * 0.9f, r * 0.7f, sem + k, 8, 0.4f), o.copy(alpha = 0.18f))
        for (k in 0 until 70) {
            val a = 6.2832f * azar(sem, k)
            val d = r * (0.45f + 1.7f * azar(sem, k + 100).pow(1.3f))
            val q = c + Offset(cos(a) * d * 1.1f, sin(a) * d)
            val tam = (0.35f + 1.2f * azar(sem, k + 300)) * u * (1.5f - d / (r * 2.2f)).coerceIn(0.35f, 1.2f)
            drawCircle(o.copy(alpha = 0.3f + 0.5f * azar(sem, k + 200)), tam, q)
        }
        val roca = l.c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.3f))
        rotate(giro, c) {
            drawOval(roca, Offset(c.x - r * 0.42f, c.y - r * 0.12f), Size(r * 0.84f, r * 0.8f))
            listOf(-0.32f to 0.6f, -0.11f to 0.78f, 0.09f to 0.8f, 0.28f to 0.64f).forEach { (dx, lg) ->
                drawLine(roca, Offset(c.x + r * dx, c.y + r * 0.05f), Offset(c.x + r * dx * 1.18f, c.y - r * lg), r * 0.18f, StrokeCap.Round)
            }
            drawLine(roca, Offset(c.x - r * 0.36f, c.y + r * 0.3f), Offset(c.x - r * 0.76f, c.y - r * 0.05f), r * 0.2f, StrokeCap.Round)
            drawRect(roca, Offset(c.x - r * 0.3f, c.y + r * 0.5f), Size(r * 0.6f, r * 0.55f))
        }
    }

    // ---------------------------------------------------------------- el fondo de cada hueco

    /**
     * El fondo de un hueco: la caliza con la luz de su sitio, lisa abajo (la tapan las entradas) y, arriba, la sombra franca de la
     * plataforma en un degradado de colores llenos, con su borde ondulado; donde llega, la mancha de la boca; su textura (estratos y
     * manchas, con primitivas sueltas), la sombra del poste del lado de la luz y algo suyo según la repisa: puntos de ocre, una
     * mancha de ocre soplado o una de hollín.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, g: MarcoCueva) {
        val w = g.w
        val y = r.center.y
        // La roca, con la sombra de la plataforma de arriba (franca junto al canto y abierta hacia abajo) en el mismo degradado, y,
        // donde la alcanza, la mancha de la boca, que empieza donde acaba esa sombra (la luz entra en diagonal).
        val l = g.luz(r.center.x, r.top)
        conManchaCueva(g, 0f, r) { s ->
            val c = g.luz(r.center.x, y, 0f, s).c(P.CALIZA_HONDA)
            fun sobre(a: Float) = mezcla(c, l.s(1f).copy(alpha = 1f), l.s(a).alpha)
            // Colores llenos: lo de abajo, liso (lo tapan las entradas); la sombra de arriba, en un degradado solo donde está.
            val y0 = if (s != null && s > 0f) r.top + r.height * 0.34f else r.top
            // La junta entre el degradado y lo liso cae en un píxel entero, y lo liso empieza un píxel antes (sin costura).
            val ys = kotlin.math.floor(r.top + r.height * 0.5f)
            drawRect(c, Offset(r.left, ys - 1f), Size(r.width, r.bottom + 2f - ys))
            if (s != null && s > 0f) drawRect(Brush.verticalGradient(listOf(sobre(0.2f), c), y0, ys), Offset(r.left, y0), Size(r.width, ys - y0))
            else drawRect(Brush.verticalGradient(colorStops = arrayOf(0f to sobre(0.8f), 0.4f to sobre(0.5f), 0.8f to sobre(0.16f), 1f to c), startY = r.top, endY = ys),
                r.topLeft, Size(r.width, ys - r.top))
        }
        textura(k, r, g)
        val ola = Path().apply {
            moveTo(r.left, r.top + r.height * 0.1f)
            var x = r.left; var i = 0
            while (x < r.right) {
                val paso = maxOf(4f, w * 0.03f)
                lineTo(x + paso * 0.5f, r.top + r.height * (0.25f + 0.06f * azar(4100 + k, i)))
                x += paso; i++
            }
            lineTo(r.right, r.top + r.height * 0.1f); close()
        }
        drawPath(ola, l.s(0.3f))
        // La sombra del poste: de día cae a la derecha del izquierdo; de noche, lejos de la tea (a la izquierda del derecho).
        if (!g.a.oscuro) drawRect(l.s(0.26f), Offset(r.left, r.top), Size(w * 0.035f, r.height))
        else drawRect(l.s(0.34f), Offset(r.right - w * 0.03f, r.top), Size(w * 0.03f, r.height))
        // Algo de la roca, distinto en cada hueco.
        val s = 4200 + k * 13
        when (Math.floorMod(k, 3)) {
            0 -> for (i in 0 until 5) {
                val q = Offset(r.left + r.width * (0.35f + 0.05f * i), r.top + r.height * (0.4f + 0.04f * (i % 2)))
                drawCircle(g.luz(q.x, q.y).c(P.OCRE_ROJO).copy(alpha = 0.7f), w * 0.0055f, q)
            }
            1 -> {
                // Una mancha de ocre soplado, rala, con sus gotitas.
                val q = Offset(r.left + r.width * (0.5f + 0.25f * azar(s, 1)), r.top + r.height * 0.42f)
                val o = g.luz(q.x, q.y).c(P.OCRE_ROJO)
                mancha(q, w * 0.05f, o.copy(alpha = 0.32f))
                for (j in 0 until 18) {
                    val a = 6.2832f * azar(s, 300 + j); val d = w * 0.045f * azar(s, 320 + j)
                    drawCircle(o.copy(alpha = 0.45f), (0.5f + 0.8f * azar(s, 340 + j)) * density, q + Offset(cos(a) * d, sin(a) * d * 0.8f))
                }
            }
            else -> {
                val q = Offset(r.left + r.width * (0.3f + 0.3f * azar(s, 2)), r.top + r.height * 0.28f)
                drawOval(P.HOLLIN.copy(alpha = 0.22f), Offset(q.x - w * 0.05f, q.y - w * 0.03f), Size(w * 0.1f, w * 0.07f))
                drawOval(P.HOLLIN.copy(alpha = 0.18f), Offset(q.x - w * 0.03f, q.y - w * 0.06f), Size(w * 0.06f, w * 0.06f))
            }
        }
    }

    /**
     * La textura de la caliza en el fondo de un hueco: los estratos (líneas casi horizontales con su labio de luz) y unos poros y
     * manchas, todo en tres caminos.
     */
    private fun DrawScope.textura(k: Int, r: Rect, g: MarcoCueva) {
        val u = density; val w = g.w
        val l = g.luz(r.center.x, r.center.y)
        // Con primitivas sueltas (rectángulos y óvalos), que se pintan mucho más rápido que un camino largo con muchos tramos.
        val hollin = P.HOLLIN.copy(alpha = if (g.a.oscuro) 0.14f else 0.12f)
        for (i in 0 until 7) {
            val c = Offset(r.left + r.width * azar(4540 + k, i), r.top + r.height * (0.3f + 0.65f * azar(4550 + k, i)))
            val rx = w * (0.012f + 0.02f * azar(4560 + k, i))
            drawOval(hollin, Offset(c.x - rx, c.y - rx * 0.35f), Size(rx * 2f, rx * 0.7f))
        }
        val raya = l.s(0.4f); val labio = l.b(if (g.a.oscuro) 0.18f else 0.22f)
        for (i in 0 until 3) {
            var x = r.left
            var y0 = r.top + r.height * (0.3f + 0.22f * i + 0.06f * azar(4530 + k, i))
            var j = 0
            while (x < r.right) {
                val largo = r.width * (0.08f + 0.14f * azar(4531 + k * 3 + i, j))
                if (j % 3 != 2) {
                    drawRect(raya, Offset(x, y0), Size(largo, 1.2f * u))
                    drawRect(labio, Offset(x, y0 - 0.9f * u), Size(largo, 0.7f * u))
                }
                x += largo; y0 += (azar(4535 + k * 3 + i, j) - 0.45f) * 3f * u; j++
            }
        }
    }
}
