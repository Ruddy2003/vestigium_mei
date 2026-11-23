package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.sin

/**
 * El rincón de la libreta de Prehistoria, lo que no cambia de un dibujo a otro (se pinta una vez por tamaño y modo): la pared de
 * caliza con sus abultamientos, sus estratos, el techo tiznado y la grieta del candil; a la izquierda, la boca de la cueva, con su
 * labio de roca que enmarca un trozo de afuera; la repisa natural de la roca donde se asienta la bolsa, con su canto y su sombra
 * en el suelo; en la repisa, la piel de hoy extendida y a medio pintar, que cae por el canto; en la pared, la tea clavada en una
 * grieta, que de noche arde con su charco.
 */
internal object RinconLibretaCueva {
    /** Cuánto están por delante de la pared la repisa, la piel de hoy y la tea, en altos (la mancha de la boca se les corre). */
    private const val REPISA = 0.05f
    private const val TEA = 0.04f

    fun DrawScope.rincon(g: MarcoLibretaCueva) {
        conManchaLibretaCueva(g, 0f) { s -> pared(g, s) }
        afuera(g)
        conManchaLibretaCueva(g, REPISA) { s -> repisa(g, s) }
        conManchaLibretaCueva(g, TEA) { s -> tea(g, s) }
        conManchaLibretaCueva(g, REPISA + 0.01f) { s -> pielDeHoy(g, s) }
        if (g.a.oscuro) llama(g)
    }

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(g: MarcoLibretaCueva, s: Float) {
        val w = g.w; val h = g.h; val u = density
        fun l(x: Float, y: Float) = g.luz(Offset(x, y), 0f, s)
        // La roca: más clara junto a la boca, hundida hacia dentro; fuera de la mancha, más oscura y pareja.
        val roca = if (s > 0f) P.CALIZA else mezcla(P.CALIZA, P.TECHO, 0.3f)
        val xs = listOf(0f, w * 0.35f, w * 0.7f, w)
        drawRect(Brush.horizontalGradient(xs.map { l(it, h * 0.45f).c(roca) }, 0f, w), Offset.Zero, size)
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l(w * 0.6f, h * 0.7f).s(0.22f)), h * 0.35f, g.repisa), Offset(0f, h * 0.35f), Size(w, g.repisa - h * 0.35f))
        // De noche, el charco de la tea en la roca (debajo de lo demás).
        if (g.a.oscuro) {
            val t = g.tea
            mancha(Offset(t.x, t.y + h * 0.06f), h * 0.42f, l(t.x, t.y).c(P.CALIZA).copy(alpha = 0.7f))
            mancha(Offset(t.x, t.y + h * 0.03f), h * 0.2f, mezcla(l(t.x, t.y).c(P.CALIZA), P.LLAMA, 0.45f).copy(alpha = 0.7f))
        }
        // Los abultamientos de la caliza, con su luz del lado de la boca y su sombra del otro.
        BULTOS.forEachIndexed { i, (fx, fy, r) ->
            val c = Offset(g.boca + (w - g.boca) * fx, h * fy)
            val ll = l(c.x, c.y)
            val f = piedraCueva(c, h * r * 1.7f, h * r * 0.7f, 7100 + i, 10, 0.2f)
            drawPath(f, ll.c(mezcla(roca, P.CALIZA_SOL, 0.08f)).copy(alpha = 0.35f))
            drawPath(bordeCueva(f, -h * 0.04f, -h * 0.03f), ll.s(0.07f))
            drawPath(bordeCueva(f, h * 0.012f, h * 0.014f), ll.b(0.14f))
        }
        // Los estratos: dos vetas largas y onduladas, con su labio de luz.
        for (k in 0 until 2) {
            val y0 = h * (0.3f + 0.2f * k)
            val q = List(9) { j -> Offset(g.boca + (w - g.boca) * j / 8f, y0 + h * 0.025f * sin(j * 1.3f + k * 2f) + h * 0.01f * azar(7200 + k, j)) }
            val p = abiertaCueva(q)
            val ll = l(w * 0.6f, y0)
            drawPath(p, ll.s(0.22f), style = Stroke(1.6f * u, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(h * 0.22f, h * 0.06f))))
            drawPath(corridoCueva(p, 0f, -1.3f * u), ll.b(0.22f), style = Stroke(0.8f * u, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(h * 0.22f, h * 0.06f))))
        }
        // La grieta del candil (la bolsa cuelga de él) y la de la tea.
        grieta(listOf(Offset(g.candil.x - h * 0.05f, 0f), Offset(g.candil.x - h * 0.01f, h * 0.05f), g.candil, Offset(g.candil.x + h * 0.03f, h * 0.15f)), l(g.candil.x, g.candil.y))
        val base = baseTea(g)
        grieta(listOf(Offset(base.x + h * 0.02f, base.y - h * 0.08f), base, Offset(base.x - h * 0.015f, base.y + h * 0.1f), Offset(base.x + h * 0.01f, base.y + h * 0.18f)), l(base.x, base.y))
        // Unos puntos de ocre de otros días, pocos, en la pared de la derecha.
        for (j in 0 until 5) {
            val q = Offset(g.tea.x - h * (0.36f - 0.045f * j), h * (0.62f + 0.012f * (j % 2)))
            drawCircle(l(q.x, q.y).c(P.OCRE_ROJO).copy(alpha = 0.75f), h * 0.009f, q)
        }
        // El techo de la boca que se cierra arriba, tiznado, y el hollín de la tea en la pared.
        val borde = List(11) { k -> Offset(w * (-0.05f + 1.1f * k / 10f), h * (0.05f + 0.035f * azar(7300, k) + 0.015f * sin(k * 1.9f))) }
        val techo = cerradaCueva(borde + listOf(Offset(w * 1.1f, -h * 0.2f), Offset(-w * 0.1f, -h * 0.2f)))
        val lt = l(w * 0.5f, 0f)
        drawPath(corridoCueva(techo, 0f, h * 0.02f), lt.s(0.28f))
        drawPath(techo, lt.c(P.TECHO))
        drawPath(techo, P.HOLLIN.copy(alpha = if (g.a.oscuro) 0.2f else 0.3f))
        drawPath(bordeCueva(techo, 0f, -h * 0.012f), lt.b(0.3f))
        for (k in 0 until 5) {
            val r = h * (0.03f + 0.022f * k)
            val q = Offset(g.tea.x - h * 0.01f * k, g.tea.y - h * (0.05f + 0.045f * k))
            drawOval(P.HOLLIN.copy(alpha = 0.08f - 0.008f * k), Offset(q.x - r * 0.8f, q.y - r * 1.3f), Size(r * 1.6f, r * 2.6f))
        }
    }

    /** Los abultamientos de la pared: (x en fracción de lo que va de la boca al borde, y en altos, radio en altos). */
    private val BULTOS = listOf(Triple(0.08f, 0.24f, 0.1f), Triple(0.42f, 0.16f, 0.12f), Triple(0.78f, 0.42f, 0.11f), Triple(0.3f, 0.56f, 0.1f), Triple(0.95f, 0.15f, 0.09f))

    private fun DrawScope.grieta(q: List<Offset>, l: LuzCueva) {
        val u = density
        val p = Path().apply { moveTo(q[0].x, q[0].y); for (i in 1 until q.size) lineTo(q[i].x, q[i].y) }
        drawPath(p, l.s(0.62f), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(corridoCueva(p, -1.4f * u, 0f), l.b(0.36f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- la boca y afuera

    /** El labio de la boca, de arriba abajo: lo de su izquierda es afuera. */
    private fun labio(g: MarcoLibretaCueva): List<Offset> {
        val h = g.h; val b = g.boca
        return listOf(Offset(b + h * 0.16f, -h * 0.02f), Offset(b + h * 0.07f, h * 0.12f), Offset(b + h * 0.02f, h * 0.3f), Offset(b - h * 0.02f, h * 0.48f),
            Offset(b - h * 0.03f, h * 0.62f), Offset(b - h * 0.005f, g.repisa - h * 0.02f), Offset(b + h * 0.02f, g.suelo))
    }

    /**
     * Afuera, por la boca: de día, el valle en la bruma (el cielo pálido, la loma lejana y la ladera de enfrente); de noche, el
     * cielo hondo con la luna y unas estrellas, y las lomas negras. Encima, el labio de roca de la boca, que lo enmarca, con su
     * cara encendida por la luz de afuera, y la hierba del umbral.
     */
    private fun DrawScope.afuera(g: MarcoLibretaCueva) {
        val h = g.h; val u = density
        val o = g.a.oscuro
        val lab = labio(g)
        val hueco = Path().apply {
            moveTo(-h * 0.1f, -h * 0.1f); lineTo(lab[0].x, -h * 0.1f)
            seguirCurva(this, lab)
            lineTo(-h * 0.1f, g.suelo); close()
        }
        val horizonte = h * 0.6f
        clipPath(hueco) {
            val cielo = if (o) listOf(Color(0xFF0E1430), Color(0xFF1C2448), Color(0xFF2A2F4E)) else listOf(Color(0xFFC9D6DC), Color(0xFFE9E6D6), Color(0xFFF6E8C8))
            drawRect(Brush.verticalGradient(cielo, 0f, horizonte), Offset(-h, 0f), Size(g.boca + h * 1.2f, horizonte))
            if (o) {
                // La luna, creciente y recortada, y unas estrellas.
                val luna = Offset(g.boca * 0.42f, h * 0.17f)
                drawCircle(P.LUNA.copy(alpha = 0.18f), h * 0.09f, luna)
                drawCircle(P.LUNA, h * 0.04f, luna)
                drawCircle(Color(0xFF151B3A), h * 0.036f, luna + Offset(h * 0.018f, -h * 0.012f))
                for (k in 0 until 6) drawCircle(P.LUNA.copy(alpha = 0.7f), (0.6f + 0.6f * azar(7400, k)) * u, Offset(g.boca * azar(7401, k), h * (0.05f + 0.4f * azar(7402, k))))
            }
            // La loma lejana, en la bruma, y la ladera de enfrente, más cerca y más oscura.
            val lejos = abiertaCueva(List(6) { k -> Offset(-h * 0.1f + (g.boca + h * 0.3f) * k / 5f, horizonte - h * (0.1f + 0.06f * sin(k * 1.7f + 1f))) })
            lejos.lineTo(g.boca + h * 0.3f, g.suelo); lejos.lineTo(-h * 0.1f, g.suelo); lejos.close()
            drawPath(lejos, if (o) Color(0xFF1A2038) else Color(0xFFAAB7B4))
            val cerca = abiertaCueva(List(5) { k -> Offset(-h * 0.1f + (g.boca + h * 0.3f) * k / 4f, horizonte + h * (0.02f + 0.05f * sin(k * 2.1f + 0.4f))) })
            cerca.lineTo(g.boca + h * 0.3f, g.suelo); cerca.lineTo(-h * 0.1f, g.suelo); cerca.close()
            drawPath(cerca, if (o) Color(0xFF12162A) else Color(0xFF8E9A7E))
            // El umbral: la tierra de la boca con su hierba, que baja hacia afuera.
            val umbral = Path().apply {
                moveTo(-h * 0.1f, g.repisa + h * 0.02f); quadraticTo(g.boca * 0.5f, g.repisa - h * 0.03f, g.boca + h * 0.1f, g.repisa - h * 0.01f)
                lineTo(g.boca + h * 0.1f, g.suelo); lineTo(-h * 0.1f, g.suelo); close()
            }
            drawPath(umbral, if (o) Color(0xFF1A1620) else mezcla(P.TIERRA, Color(0xFF8E9A7E), 0.35f))
            val hierba = Path()
            for (k in 0 until 14) {
                val x = g.boca * (0.05f + 0.9f * k / 13f)
                val y = g.repisa - h * (0.01f + 0.012f * sin(k * 0.7f))
                hierba.moveTo(x, y + h * 0.01f); hierba.lineTo(x + h * (0.012f * (azar(7403, k) - 0.5f)), y - h * (0.03f + 0.03f * azar(7404, k)))
            }
            drawPath(hierba, if (o) Color(0xFF232838) else Color(0xFF6E7A4E), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
        // El labio de roca: una banda gruesa a lo largo del borde, con su cara de dentro encendida por la luz de afuera.
        val curva = abiertaCueva(lab)
        val roca = mezcla(P.CALIZA, P.TECHO, 0.45f)
        val lb = g.luz(Offset(g.boca, h * 0.4f), 0.02f, 0f)
        drawPath(corridoCueva(curva, h * 0.03f, 0f), lb.c(mezcla(roca, P.HOLLIN, 0.3f)), style = Stroke(h * 0.07f, cap = StrokeCap.Round))
        drawPath(curva, lb.c(roca), style = Stroke(h * 0.05f, cap = StrokeCap.Round))
        val filo = if (o) P.LUNA.copy(alpha = 0.55f) else mezcla(P.CALIZA_SOL, Color.White, 0.3f).copy(alpha = 0.85f)
        drawPath(corridoCueva(curva, -h * 0.02f, 0f), filo, style = Stroke(h * 0.012f, cap = StrokeCap.Round))
    }

    /** Sigue con [p] la curva suave que pasa por [q] (como [abiertaCueva]), desde su primer punto. */
    private fun seguirCurva(p: Path, q: List<Offset>) {
        p.lineTo(q[0].x, q[0].y)
        for (i in 0 until q.size - 1) {
            val a = q[maxOf(i - 1, 0)]; val b = q[i]; val c = q[i + 1]; val d = q[minOf(i + 2, q.size - 1)]
            val c1 = b + (c - a) / 6f; val c2 = c - (d - b) / 6f
            p.cubicTo(c1.x, c1.y, c2.x, c2.y, c.x, c.y)
        }
    }

    // ---------------------------------------------------------------- la repisa y el suelo

    /** La repisa natural de la roca: su cara de arriba, el canto con sus grietas y su sombra en el suelo de tierra. */
    private fun DrawScope.repisa(g: MarcoLibretaCueva, s: Float) {
        val w = g.w; val h = g.h; val u = density
        val x0 = g.boca - h * 0.02f
        fun l(x: Float, y: Float) = g.luz(Offset(x, y), REPISA, s)
        // El suelo de tierra pisada bajo la repisa, con unos guijarros, y la sombra de la repisa.
        val xs = listOf(x0, (x0 + w) / 2f, w)
        drawRect(Brush.horizontalGradient(xs.map { l(it, g.suelo).c(P.TIERRA) }, x0, w), Offset(x0, g.canto), Size(w - x0, h - g.canto))
        drawRect(Brush.verticalGradient(listOf(l(w * 0.6f, g.suelo).s(0.5f), Color.Transparent), g.suelo, g.suelo + h * 0.07f), Offset(x0, g.suelo), Size(w - x0, h * 0.07f))
        for (k in 0 until 7) {
            val c = Offset(x0 + (w - x0) * azar(7500, k), g.suelo + (h - g.suelo) * (0.35f + 0.55f * azar(7501, k)))
            val r = h * (0.01f + 0.012f * azar(7502, k))
            val ll = l(c.x, c.y)
            drawOval(ll.s(0.35f), Offset(c.x - r, c.y - r * 0.2f), Size(r * 2.2f, r * 0.9f))
            drawOval(ll.c(mezcla(P.CALIZA, P.TIERRA, 0.4f)), Offset(c.x - r, c.y - r * 0.55f), Size(r * 2f, r * 1.1f))
        }
        // El canto: la cara de delante, de roca más oscura, con su borde de abajo quebrado.
        val canto = Path().apply {
            moveTo(x0, g.repisa)
            var x = x0; var i = 0
            while (x < w) { x = minOf(w + h * 0.05f, x + h * 0.08f); lineTo(x, g.repisa + h * 0.004f * sin(i * 2.3f)); i++ }
            lineTo(w + h * 0.05f, g.suelo)
            x = w + h * 0.05f; i = 0
            while (x > x0) { x -= h * 0.06f; lineTo(maxOf(x0, x), g.suelo + h * (0.008f + 0.012f * azar(7510, i))); i++ }
            close()
        }
        drawPath(canto, Brush.horizontalGradient(xs.map { l(it, g.canto).c(mezcla(P.CALIZA, P.TECHO, 0.45f)) }, x0, w))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, l(w * 0.6f, g.suelo).s(0.36f)), g.canto, g.suelo), Offset(x0, g.canto), Size(w - x0, g.suelo - g.canto))
        // La roca del canto no es lisa: bultos redondeados, uno junto a otro, cada uno con su luz arriba y su sombra abajo.
        var xb = x0 + h * 0.04f; var k = 0
        while (xb < w + h * 0.1f) {
            val rx = h * (0.07f + 0.05f * azar(7520, k)); val ry = (g.suelo - g.canto) * (0.42f + 0.12f * azar(7521, k))
            val c = Offset(xb, (g.canto + g.suelo) / 2f + (g.suelo - g.canto) * 0.08f * (azar(7522, k) - 0.5f))
            val ll = l(c.x, c.y)
            val f = piedraCueva(c, rx, ry, 7530 + k, 9, 0.16f)
            bultoCueva(f, ll.c(mezcla(P.CALIZA, P.TECHO, 0.32f + 0.12f * azar(7523, k))), ll, g.hacia(c.x), 0.9f, 0.3f)
            drawPath(bordeCueva(f, 0f, ry * 0.35f), ll.b(0.32f))
            xb += rx * (1.5f + 0.3f * azar(7524, k)); k++
        }
        // La cara de arriba, donde pisa la bolsa: una franja clara de roca gastada, con el filo de luz en el borde.
        val cara = Path().apply {
            moveTo(x0 + h * 0.02f, g.repisa - h * 0.03f)
            var x = x0 + h * 0.02f; var i = 0
            while (x < w) { x = minOf(w + h * 0.05f, x + h * 0.09f); lineTo(x, g.repisa - h * (0.03f + 0.006f * sin(i * 1.7f))); i++ }
            lineTo(w + h * 0.05f, g.repisa + h * 0.006f); lineTo(x0, g.repisa + h * 0.006f); close()
        }
        drawPath(cara, Brush.horizontalGradient(xs.map { l(it, g.repisa).c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.12f)) }, x0, w))
        drawRect(Brush.horizontalGradient(xs.map { l(it, g.repisa).b(0.5f) }, x0, w), Offset(x0, g.repisa - h * 0.002f), Size(w - x0, h * 0.008f))
    }

    // ---------------------------------------------------------------- la tea

    /** Donde la tea entra en la grieta de la pared. */
    private fun baseTea(g: MarcoLibretaCueva) = Offset(g.tea.x + g.h * 0.08f, g.tea.y + g.h * 0.3f)

    /** La tea de pino clavada en la grieta, inclinada: el palo con su corteza, dos vueltas de tiento y la cabeza de astillas. */
    private fun DrawScope.tea(g: MarcoLibretaCueva, s: Float) {
        val h = g.h; val u = density
        val t = g.tea; val base = baseTea(g)
        val l = g.luz((t + base) / 2f, TEA, s)
        val dir = (t - base) / (t - base).getDistance()
        val n = Offset(-dir.y, dir.x)
        val palo = Path().apply {
            val a = base + n * h * 0.012f; val b = base - n * h * 0.012f; val c = t - dir * h * 0.04f - n * h * 0.01f; val d = t - dir * h * 0.04f + n * h * 0.01f
            moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(d.x, d.y); close()
        }
        drawPath(corridoCueva(palo, h * 0.015f, h * 0.01f), l.s(0.4f))
        bultoCueva(palo, l.c(mezcla(P.CORTEZA, P.PINO_OSC, 0.4f)), l, g.hacia(t.x), 1f, 0.3f)
        listOf(0.45f, 0.62f).forEach { f ->
            val c = base + (t - base) * f
            drawLine(l.c(P.CUERO), c + n * h * 0.016f, c - n * h * 0.016f + dir * h * 0.008f, 2f * u, StrokeCap.Round)
        }
        val cabeza = piedraCueva(t - dir * h * 0.02f, h * 0.024f, h * 0.034f, 7600, 7, 0.2f)
        bultoCueva(cabeza, l.c(Color(0xFF2E2019)), l, g.hacia(t.x), 1f, 0.2f)
        for (k in 0 until 4) {
            val c = t - dir * h * 0.04f + n * h * (0.016f * (k - 1.5f))
            drawLine(l.c(P.CARBON).copy(alpha = 0.7f), c, c + dir * h * 0.04f, 0.8f * u)
        }
        // La boca de la grieta, honda, encima del palo.
        drawOval(P.HOLLIN.copy(alpha = 0.7f), Offset(base.x - h * 0.016f, base.y - h * 0.014f), Size(h * 0.03f, h * 0.026f))
    }

    /** De noche, la llama de la tea: su resplandor y la llama en capas, con unas chispas. */
    private fun DrawScope.llama(g: MarcoLibretaCueva) {
        val h = g.h; val u = density
        val t = g.tea
        mancha(t, h * 0.2f, P.LLAMA.copy(alpha = 0.5f))
        llamaCueva(Offset(t.x, t.y + h * 0.01f), h * 0.16f, h * 0.035f, 5)
        drawCircle(P.LLAMA.copy(alpha = 0.85f), 1.1f * u, Offset(t.x + h * 0.02f, t.y - h * 0.2f))
        drawCircle(P.FUEGO.copy(alpha = 0.7f), 0.9f * u, Offset(t.x - h * 0.015f, t.y - h * 0.24f))
    }

    // ---------------------------------------------------------------- la piel de hoy

    /**
     * La piel de hoy, extendida en la repisa a la derecha de la bolsa y a medio pintar: vista un poco desde arriba, con la orilla
     * desigual, y la punta de delante que cae por el canto. Pintado, lo de hoy: una hilera de puntos de ocre, una raya ondulada
     * de carbón y unos pares de puntos; lo demás, aún en blanco.
     */
    private fun DrawScope.pielDeHoy(g: MarcoLibretaCueva, s: Float) {
        val w = g.w; val h = g.h; val u = density
        val x0 = g.cx + h * 0.1f; val x1 = minOf(w - h * 0.03f, g.cx + h * 0.52f)
        if (x1 < x0 + h * 0.1f) return
        val y0 = g.repisa - h * 0.075f; val y1 = g.repisa + h * 0.004f
        val l = g.luz(Offset((x0 + x1) / 2f, g.repisa), REPISA + 0.01f, s)
        val an = x1 - x0
        // La cara de arriba, en escorzo, con su orilla desigual; la punta de delante cae por el canto.
        // Con la orilla de una piel: una pata que asoma atrás a la derecha y la orilla de delante, desigual.
        val cara = cerradaCueva(listOf(Offset(x0, y0 + h * 0.03f), Offset(x0 + an * 0.25f, y0 + h * 0.004f), Offset(x0 + an * 0.55f, y0), Offset(x0 + an * 0.74f, y0 + h * 0.004f),
            Offset(x0 + an * 0.84f, y0 - h * 0.022f), Offset(x0 + an * 0.9f, y0 - h * 0.018f), Offset(x0 + an * 0.9f, y0 + h * 0.014f),
            Offset(x1, y0 + h * 0.035f), Offset(x1 - an * 0.06f, y1), Offset(x0 + an * 0.7f, y1 + h * 0.004f), Offset(x0 + an * 0.3f, y1), Offset(x0 + an * 0.04f, y1 - h * 0.006f)))
        val cae = cerradaCueva(listOf(Offset(x0 + an * 0.38f, y1 - h * 0.004f), Offset(x0 + an * 0.72f, y1 - h * 0.002f), Offset(x0 + an * 0.7f, g.canto + h * 0.03f),
            Offset(x0 + an * 0.6f, g.canto + h * 0.055f), Offset(x0 + an * 0.47f, g.canto + h * 0.045f), Offset(x0 + an * 0.4f, g.canto + h * 0.02f)))
        drawPath(corridoCueva(cae, h * 0.012f, h * 0.008f), l.s(0.42f))
        val crudo = mezcla(P.PIEL, P.HUESO, 0.45f)
        bultoCueva(cae, l.c(mezcla(crudo, P.PIEL_OSC, 0.25f)), l, g.hacia(x0), 1.1f, 0.3f)
        drawPath(corridoCueva(cara, h * 0.006f, h * 0.008f), l.s(0.36f))
        drawPath(cara, l.c(crudo))
        drawPath(bordeCueva(cara, 0f, -h * 0.01f), l.s(0.2f))
        drawPath(cara, l.c(P.PIEL_OSC).copy(alpha = 0.6f), style = Stroke(1f * u))
        // Lo pintado, en lo que deja ver la bolsa más gorda: la hilera de puntos de ocre, la raya ondulada de carbón y unos pares
        // de puntos amarillos, en escorzo; lo demás, aún en blanco.
        val xa = maxOf(x0 + an * 0.1f, g.cx + h * 0.31f); val xz = x1 - an * 0.08f
        if (xz > xa + h * 0.06f) {
            val n = ((xz - xa) / (h * 0.035f)).toInt().coerceIn(2, 8)
            for (j in 0 until n) {
                val q = Offset(xa + (xz - xa) * (j + 0.5f) / n, y0 + h * (0.026f + 0.004f * (j % 2)))
                drawOval(l.c(P.OCRE_ROJO), Offset(q.x - h * 0.009f, q.y - h * 0.0045f), Size(h * 0.018f, h * 0.009f))
            }
            val onda = abiertaCueva(List(5) { j -> Offset(xa + (xz - xa) * j / 4f, y0 + h * (0.05f + 0.007f * (if (j % 2 == 0) -1 else 1))) })
            drawPath(onda, l.c(P.CARBON).copy(alpha = 0.85f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            val q = Offset(xa + (xz - xa) * 0.3f, y0 + h * 0.066f)
            drawOval(l.c(P.OCRE_AMAR), Offset(q.x - h * 0.008f, q.y - h * 0.004f), Size(h * 0.016f, h * 0.008f))
            drawOval(l.c(P.OCRE_AMAR), Offset(q.x + h * 0.014f, q.y - h * 0.004f), Size(h * 0.016f, h * 0.008f))
        }
    }
}
