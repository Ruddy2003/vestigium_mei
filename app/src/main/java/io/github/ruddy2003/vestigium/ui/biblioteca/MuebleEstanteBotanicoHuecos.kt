package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que acompaña en los huecos libres de las repisas del estante de Botánico ([MuebleEstanteBotanico]): grupos de dos o tres
 * cosas del gabinete, solapadas, del 70 al 90 % del alto del hueco y distintas de un hueco a otro: la palmatoria con su vela
 * (la luz de en medio, de noche) con dos libros tumbados y el apagavelas; la concha de nautilo en su peana con una rama de coral
 * y conchas chicas; la maceta de barro con un helecho y la regadera de latón; las piedras del viaje con un cristal y el martillo
 * de geólogo.
 */
internal object HuecosEstanteBotanico {
    private val P = PaletaBotanico

    /** Cuántos grupos distintos hay para los huecos: los libros, el nautilo, la maceta y las piedras. */
    const val GRUPOS = 4

    /** Si el hueco de [ocupado] a [derecha] tiene sitio para algo. */
    fun conSitio(ocupado: Float, derecha: Float, w: Float) = (derecha - w * 0.035f) - (ocupado + w * 0.02f) >= w * 0.1f

    /**
     * Lo del hueco de la repisa [k] ([r]), de [ocupado] (donde acaban los volúmenes) a [derecha] (la pilastra). [grupo] es el
     * grupo que le toca (el estante los reparte para que no se repitan dos seguidos); si arde una palmatoria ([velaX]), ese
     * grupo la acompaña a su derecha. En un hueco muy ancho van dos grupos distintos.
     */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, derecha: Float, velaX: Float, grupo: Int, s: SitioBotanico) {
        val w = size.width
        val x0 = ocupado + w * 0.02f
        val x1 = derecha - w * 0.035f
        val ancho = x1 - x0
        if (ancho < w * 0.1f) return
        val y = r.bottom - w * 0.011f
        val h = r.height
        if (velaX >= 0f) {
            palmatoria(Offset(velaX, y), h, s)
            val a = velaX + w * 0.07f
            if (x1 - a > w * 0.2f) grupo(grupo, a, x1, y, h, s, k)
            return
        }
        if (ancho > w * 0.5f) {
            grupo(grupo, x0, x0 + ancho / 2, y, h, s, k)
            grupo(Math.floorMod(grupo + 2, GRUPOS), x0 + ancho / 2, x1, y, h, s, k + 1)
        } else grupo(grupo, x0, x1, y, h, s, k)
    }

    /** Uno de los grupos, de [a] a [b]. */
    private fun DrawScope.grupo(g: Int, a: Float, b: Float, y: Float, h: Float, s: SitioBotanico, sem: Int) {
        when (Math.floorMod(g, GRUPOS)) {
            0 -> libros(Offset(a, y), minOf(size.width * 0.24f, b - a), h, s, sem)
            1 -> nautilo(a, b, y, h, s, sem)
            2 -> maceta(a, b, y, h, s, sem)
            else -> piedras(a, b, y, h, s, sem)
        }
    }

    // ---------------------------------------------------------------- la palmatoria

    /** La palmatoria de latón con su vela de sebo, el asa y el apagavelas colgado; la llama la pone [llamaHueco]. */
    private fun DrawScope.palmatoria(pie: Offset, h: Float, s: SitioBotanico) {
        val w = size.width; val u = density
        val t = s.t(Offset(pie.x, pie.y - h * 0.3f))
        val plato = Rect(pie.x - w * 0.055f, pie.y - h * 0.06f, pie.x + w * 0.055f, pie.y)
        contactoBotanico(Offset(pie.x + w * 0.01f, pie.y), w * 0.13f, h * 0.05f, t, 0.45f)
        // El plato y su asa de aro.
        drawOval(t.c(P.LATON_OSC), plato.topLeft, plato.size)
        drawOval(t.c(P.LATON), Offset(plato.left, plato.top - h * 0.012f), Size(plato.width, plato.height * 0.85f))
        drawOval(t.b(0.5f), Offset(plato.left + plato.width * 0.15f, plato.top - h * 0.006f), Size(plato.width * 0.3f, plato.height * 0.3f))
        val asa = Rect(plato.right - w * 0.006f, pie.y - h * 0.1f, plato.right + w * 0.035f, pie.y - h * 0.035f)
        drawOval(t.c(P.LATON), asa.topLeft, asa.size, style = Stroke(1.8f * u))
        // El cañón y la vela, gastada y con su goterón de sebo.
        val canon = Rect(pie.x - w * 0.013f, pie.y - h * 0.2f, pie.x + w * 0.013f, plato.top)
        drawRect(t.c(P.LATON), canon.topLeft, canon.size)
        clipRect(canon.left, canon.top, canon.right, canon.bottom) { cilindroBotanico(canon, t, if (s.a.oscuro) 1f else -1f, 1.3f) }
        drawRect(t.c(P.LATON_OSC), Offset(canon.left - w * 0.004f, canon.top), Size(canon.width + w * 0.008f, h * 0.018f))
        val vela = Rect(pie.x - w * 0.01f, pie.y - h * (MuebleEstanteBotanico.VELA_Y - 0.035f), pie.x + w * 0.01f, canon.top)
        drawRect(t.c(Color(0xFFEDE1BE), 0.02f), vela.topLeft, vela.size)
        clipRect(vela.left, vela.top, vela.right, vela.bottom) { cilindroBotanico(vela, t, if (s.a.oscuro) 1f else -1f, 0.8f) }
        drawPath(Path().apply { moveTo(vela.right, vela.top + h * 0.03f); quadraticTo(vela.right + w * 0.006f, vela.top + h * 0.1f, vela.right + w * 0.002f, vela.top + h * 0.16f) }, t.c(Color(0xFFE6D6AE)), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawLine(t.c(P.TINTA), Offset(pie.x, vela.top), Offset(pie.x + 0.4f * u, vela.top - h * 0.03f), 0.8f * u)
        // El apagavelas, un cono chico colgado del asa.
        val ap = Offset(asa.right - w * 0.002f, asa.top - h * 0.01f)
        drawPath(poligonoBotanico(ap.x - w * 0.012f, ap.y, ap.x + w * 0.012f, ap.y, ap.x, ap.y - h * 0.09f), t.c(P.LATON_OSC, 0.05f))
        drawLine(t.b(0.4f), Offset(ap.x - w * 0.006f, ap.y), Offset(ap.x - w * 0.001f, ap.y - h * 0.07f), 0.6f * u)
    }

    /** La llama de la palmatoria (encima de todo el estante): de día apagada; de noche arde y echa su halo en la pared. */
    fun DrawScope.llamaHueco(x: Float, r: Rect, s: SitioBotanico) {
        if (!s.a.oscuro) return
        val w = size.width
        val c = Offset(x + 0.4f * density, r.bottom - r.height * MuebleEstanteBotanico.VELA_Y)
        val f = if (s.a.noche) 0.6f else 1f
        mancha(c, w * 0.14f, P.LLAMA.copy(alpha = 0.16f * f))
        drawCircle(P.LLAMA.copy(alpha = 0.3f * f), w * 0.016f, c)
        drawOval(P.LLAMA, Offset(c.x - w * 0.006f, c.y - w * 0.018f), Size(w * 0.012f, w * 0.026f))
        drawOval(P.FUEGO, Offset(c.x - w * 0.003f, c.y - w * 0.01f), Size(w * 0.006f, w * 0.014f))
    }

    /** Dos libros tumbados y uno de pie apoyado en ellos, junto a la palmatoria. */
    private fun DrawScope.libros(pie: Offset, ancho: Float, h: Float, s: SitioBotanico, k: Int) {
        val w = size.width; val u = density
        val t = s.t(Offset(pie.x + ancho / 2, pie.y - h * 0.2f))
        val cols = listOf(P.TELA, P.CUERO, mezcla(P.NOGAL, P.LACRE, 0.3f), P.VERDE)
        contactoBotanico(Offset(pie.x + ancho * 0.5f, pie.y), ancho * 1.05f, h * 0.05f, t, 0.42f)
        var y = pie.y
        for (i in 0 until 2) {
            val bw = ancho * (0.92f - 0.1f * i); val bh = h * (0.1f - 0.015f * i)
            val bx = pie.x + ancho * 0.04f * i
            val r = Rect(bx, y - bh, bx + bw, y)
            val c = cols[(k + i) % cols.size]
            drawRect(t.c(c), r.topLeft, r.size)
            drawRect(t.b(0.3f), r.topLeft, Size(r.width, r.height * 0.3f))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            listOf(0.2f, 0.8f).forEach { f -> drawRect(t.s(0.3f), Offset(r.left + r.width * f, r.top), Size(1.2f * u, r.height)) }
            drawRect(t.c(P.LATON).copy(alpha = 0.6f), Offset(r.left + r.width * 0.42f, r.top + r.height * 0.3f), Size(r.width * 0.16f, r.height * 0.4f))
            y -= bh
        }
        // El de pie, ladeado contra la pilastra del otro lado, alto: lo que da alto al grupo.
        val bw = w * 0.05f; val bh = h * 0.7f
        val bx = pie.x + ancho - bw * 0.9f
        rotate(9f, Offset(bx + bw, pie.y)) {
            val r = Rect(bx, pie.y - bh, bx + bw, pie.y)
            drawRect(t.s(0.3f), Offset(r.left + 2f * u, r.top + 2f * u), r.size)
            drawRect(t.c(cols[(k + 2) % cols.size]), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindroBotanico(r, t, if (s.a.oscuro) -1f else -1f, 1.1f) }
            listOf(0.12f, 0.3f, 0.82f).forEach { f -> drawRect(t.c(P.LATON).copy(alpha = 0.55f), Offset(r.left, r.top + r.height * f), Size(r.width, 1.2f * u)) }
            drawRect(t.c(P.CARTULINA, -0.05f), Offset(r.left + r.width * 0.15f, r.top + r.height * 0.4f), Size(r.width * 0.7f, r.height * 0.16f))
        }
    }

    // ---------------------------------------------------------------- el nautilo

    /** La concha de nautilo en su peana de nogal, con una rama de coral detrás y dos conchas chicas delante. */
    private fun DrawScope.nautilo(a: Float, b: Float, y: Float, h: Float, s: SitioBotanico, sem: Int) {
        val w = size.width; val u = density
        val cx = a + (b - a) * 0.5f
        val t = s.t(Offset(cx, y - h * 0.35f))
        val hacia = if (s.a.oscuro && t.k > 0.15f) 1f else -1f
        // La rama de coral, de pie en su taco, detrás a la izquierda.
        val cb = Offset(cx - w * 0.07f, y - h * 0.04f)
        val ramas = Path()
        fun rama(p: Offset, an: Float, l: Float, n: Int) {
            if (n <= 0 || l < 2f * u) return
            val q = Offset(p.x + l * cos(an), p.y + l * sin(an))
            ramas.moveTo(p.x, p.y); ramas.lineTo(q.x, q.y)
            rama(q, an - 0.45f - 0.15f * azar(9500 + sem, n), l * 0.7f, n - 1)
            rama(q, an + 0.4f + 0.15f * azar(9510 + sem, n), l * 0.66f, n - 1)
        }
        rama(cb, -1.57f, h * 0.26f, 4)
        drawPath(Path().apply { addPath(ramas, Offset(w * 0.012f, w * 0.006f)) }, t.s(0.25f), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(ramas, t.c(P.CORAL), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(ramas, t.b(0.3f), style = Stroke(1f * u, cap = StrokeCap.Round))
        drawRect(t.c(P.NOGAL), Offset(cb.x - w * 0.02f, y - h * 0.05f), Size(w * 0.04f, h * 0.05f))
        // La peana de nogal y el nautilo encima, de pie.
        val rr = h * 0.26f
        val c = Offset(cx + w * 0.03f, y - h * 0.08f - rr)
        val peana = Rect(c.x - rr * 0.6f, y - h * 0.08f, c.x + rr * 0.6f, y)
        contactoBotanico(Offset(peana.center.x + w * 0.01f, y), peana.width * 1.4f, h * 0.05f, t, 0.42f)
        drawRect(t.c(P.NOGAL, 0.04f), peana.topLeft, peana.size)
        drawRect(t.b(0.45f), peana.topLeft, Size(peana.width, 1f * u))
        clipRect(peana.left, peana.top, peana.right, peana.bottom) { cilindroBotanico(peana, t, hacia, 1f) }
        val concha = Path().apply { addOval(Rect(c.x - rr, c.y - rr * 0.92f, c.x + rr * 1.02f, c.y + rr)) }
        drawPath(Path().apply { addPath(concha, Offset(w * 0.014f, w * 0.008f)) }, t.s(0.28f))
        drawPath(concha, t.c(P.NACAR))
        clipPath(concha) {
            // Las franjas pardas que salen del ombligo hacia el borde (como llamas), solo en la mitad de atrás.
            for (j in 0 until 9) {
                val an = 3.6f + 0.33f * j
                val p0 = Offset(c.x + rr * 0.25f * cos(an), c.y + rr * 0.25f * sin(an))
                val p1 = Offset(c.x + rr * 1.05f * cos(an + 0.25f), c.y + rr * 1.05f * sin(an + 0.25f))
                drawLine(t.c(P.FRANJA), p0, p1, rr * (0.1f + 0.04f * azar(9520, j)), StrokeCap.Round)
            }
            drawRect(t.s(0.22f), Offset(if (hacia < 0f) c.x + rr * 0.2f else c.x - rr, c.y - rr), Size(rr * 0.85f, rr * 2f))
            drawCircle(t.b(0.35f), rr * 0.4f, c + Offset(rr * 0.45f * hacia, -rr * 0.4f))
            // La boca, nacarada, abajo a la derecha.
            drawOval(t.c(mezcla(P.NACAR, P.VIDRIO, 0.4f), 0.08f), Offset(c.x + rr * 0.15f, c.y + rr * 0.05f), Size(rr * 0.9f, rr * 0.95f))
            drawOval(t.s(0.3f), Offset(c.x + rr * 0.3f, c.y + rr * 0.25f), Size(rr * 0.6f, rr * 0.6f))
        }
        // La espira, que se ve en el ombligo como un remolino chico y no un ojo: arco abierto con su sombra.
        drawArc(t.s(0.45f), 120f, 260f, false, Offset(c.x - rr * 0.4f, c.y - rr * 0.38f), Size(rr * 0.6f, rr * 0.58f), style = Stroke(1.4f * u))
        drawArc(t.c(P.FRANJA, -0.1f), 160f, 200f, false, Offset(c.x - rr * 0.3f, c.y - rr * 0.26f), Size(rr * 0.34f, rr * 0.32f), style = Stroke(1.2f * u))
        drawPath(concha, t.c(mezcla(P.FRANJA, P.NACAR, 0.5f)).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        // Dos conchas chicas delante, a la derecha: una de abanico y un caracol.
        val q0 = Offset(minOf(b - w * 0.04f, c.x + rr * 1.3f), y - h * 0.035f)
        abanico(q0, h * 0.1f, t)
        val q1 = Offset(peana.left - w * 0.02f, y - h * 0.03f)
        drawOval(t.s(0.3f), Offset(q1.x - h * 0.05f, q1.y + h * 0.015f), Size(h * 0.12f, h * 0.035f))
        drawOval(t.c(mezcla(P.CRUDO, P.FRANJA, 0.3f)), Offset(q1.x - h * 0.05f, q1.y - h * 0.035f), Size(h * 0.1f, h * 0.06f))
        drawArc(t.s(0.4f), 180f, 270f, false, Offset(q1.x - h * 0.035f, q1.y - h * 0.03f), Size(h * 0.05f, h * 0.045f), style = Stroke(0.8f * u))
    }

    /** Una concha de abanico, con sus costillas. */
    private fun DrawScope.abanico(c: Offset, r: Float, t: TonoBotanico) {
        val u = density
        val forma = Path().apply {
            moveTo(c.x, c.y + r * 0.35f)
            lineTo(c.x - r * 0.85f, c.y - r * 0.35f)
            quadraticTo(c.x, c.y - r * 1.15f, c.x + r * 0.85f, c.y - r * 0.35f)
            close()
        }
        drawOval(t.s(0.3f), Offset(c.x - r * 0.8f, c.y + r * 0.15f), Size(r * 1.8f, r * 0.4f))
        drawPath(forma, t.c(mezcla(P.CORAL, P.NACAR, 0.55f)))
        for (j in 0 until 6) {
            val f = -0.75f + 1.5f * j / 5f
            drawLine(t.s(0.3f), Offset(c.x, c.y + r * 0.3f), Offset(c.x + r * 0.8f * f, c.y - r * (0.6f - 0.25f * f * f)), 0.7f * u)
        }
        drawRect(t.c(mezcla(P.CORAL, P.NACAR, 0.3f)), Offset(c.x - r * 0.18f, c.y + r * 0.2f), Size(r * 0.36f, r * 0.18f))
        drawPath(forma, t.b(0.25f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- la maceta con el helecho

    /** La maceta de barro con un helecho de frondas largas y, delante a la derecha, la regadera chica de latón. */
    private fun DrawScope.maceta(a: Float, b: Float, y: Float, h: Float, s: SitioBotanico, sem: Int) {
        val w = size.width; val u = density
        val cx = a + (b - a) * 0.42f
        val t = s.t(Offset(cx, y - h * 0.3f))
        val hacia = if (s.a.oscuro && t.k > 0.15f) 1f else -1f
        val pw = h * 0.36f; val ph = h * 0.3f
        val boca = y - ph
        val maceta = poligonoBotanico(cx - pw / 2, boca, cx + pw / 2, boca, cx + pw * 0.36f, y, cx - pw * 0.36f, y)
        contactoBotanico(Offset(cx + pw * 0.1f, y), pw * 1.25f, h * 0.05f, t, 0.45f)
        // Las frondas: de la tierra, abiertas en abanico y caídas por los lados, hasta casi la tabla de arriba.
        val raiz = Offset(cx, boca - h * 0.02f)
        listOf(-150f to 0.62f, -125f to 0.72f, -100f to 0.66f, -78f to 0.74f, -55f to 0.66f, -30f to 0.56f, -165f to 0.45f, -12f to 0.42f).forEachIndexed { j, (g, l) ->
            val col = t.c(when (j % 3) { 0 -> P.HOJA; 1 -> mezcla(P.HOJA, P.HOJA_CLARA, 0.5f); else -> P.HOJA_OSC }, 0.03f)
            frondaBotanico(raiz, h * l * (0.95f + 0.1f * azar(9600 + sem, j)), g, col, 1.4f * u, 11)
        }
        drawPath(maceta, t.c(P.TERRACOTA))
        clipPath(maceta) {
            cilindroBotanico(Rect(cx - pw / 2, boca, cx + pw / 2, y), t, hacia, 1.2f)
            drawOval(t.c(mezcla(P.TERRACOTA, P.CRUDO, 0.5f)).copy(alpha = 0.5f), Offset(cx - pw * 0.05f, y - ph * 0.45f), Size(pw * 0.3f, ph * 0.25f))
        }
        val borde = Rect(cx - pw * 0.56f, boca - h * 0.05f, cx + pw * 0.56f, boca)
        drawRect(t.c(mezcla(P.TERRACOTA, P.CUERO, 0.2f)), borde.topLeft, borde.size)
        clipRect(borde.left, borde.top, borde.right, borde.bottom) { cilindroBotanico(borde, t, hacia, 1.2f) }
        drawRect(t.b(0.4f), borde.topLeft, Size(borde.width, 0.9f * u))
        // La regadera de latón, delante a la derecha (si cabe).
        if (b - cx < pw * 0.8f + h * 0.14f) return
        val rc = Offset(minOf(b - h * 0.2f, cx + pw * 0.95f), y)
        val rw = h * 0.26f; val rh = h * 0.28f
        val cuerpo = Rect(rc.x - rw / 2, rc.y - rh, rc.x + rw / 2, rc.y)
        val tr = s.t(rc)
        contactoBotanico(Offset(rc.x + rw * 0.1f, rc.y), rw * 1.3f, h * 0.045f, tr, 0.45f)
        // El pitorro largo hacia la izquierda, con su alcachofa, y el asa arriba.
        drawLine(tr.c(P.LATON_OSC), Offset(cuerpo.left + rw * 0.1f, cuerpo.bottom - rh * 0.3f), Offset(cuerpo.left - rw * 0.55f, cuerpo.top - rh * 0.05f), 2.6f * u, StrokeCap.Round)
        drawCircle(tr.c(P.LATON), rw * 0.1f, Offset(cuerpo.left - rw * 0.58f, cuerpo.top - rh * 0.08f))
        drawArc(tr.c(P.LATON), 180f, 180f, false, Offset(cuerpo.left + rw * 0.15f, cuerpo.top - rh * 0.35f), Size(rw * 0.7f, rh * 0.7f), style = Stroke(2f * u))
        drawRect(tr.c(P.LATON), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) { cilindroBotanico(cuerpo, tr, hacia, 1.4f) }
        drawRect(tr.c(P.LATON_OSC), Offset(cuerpo.left, cuerpo.top), Size(rw, 1.6f * u))
        listOf(0.35f, 0.75f).forEach { f -> drawRect(tr.s(0.3f), Offset(cuerpo.left, cuerpo.top + rh * f), Size(rw, 0.8f * u)) }
        destelloBotanico(Offset(cuerpo.left + rw * 0.25f, cuerpo.top + rh * 0.25f), 1.3f * u, tr)
    }

    // ---------------------------------------------------------------- las piedras y el martillo

    /**
     * Las piedras del viaje: una losa de pizarra con un helecho fósil, apoyada contra la pared; delante, un racimo de cristales en
     * prismas (una drusa de cuarzo) y dos cantos; el martillo de geólogo apoyado de pie al lado.
     */
    private fun DrawScope.piedras(a: Float, b: Float, y: Float, h: Float, s: SitioBotanico, sem: Int) {
        val w = size.width; val u = density
        val cx = a + (b - a) * 0.4f
        val t = s.t(Offset(cx, y - h * 0.3f))
        val hacia = if (s.a.oscuro && t.k > 0.15f) 1f else -1f
        val gc = Offset(cx + h * 0.14f, y - h * 0.2f)
        val gr2 = h * 0.25f
        // El martillo, de pie y ladeado contra la pared, detrás: el mango de fresno y la cabeza de hierro.
        val mb = Offset(minOf(b - w * 0.025f, gc.x + gr2 * 1.3f), y)
        val mt = Offset(mb.x - h * 0.16f, y - h * 0.8f)
        drawLine(t.s(0.25f), mb + Offset(w * 0.012f, 0f), mt + Offset(w * 0.02f, w * 0.01f), 3.6f * u, StrokeCap.Round)
        drawLine(t.c(mezcla(P.PINO, P.ROBLE, 0.4f)), mb, mt, 3.4f * u, StrokeCap.Round)
        drawLine(t.b(0.35f), mb - Offset(1f * u, 0f), mt - Offset(1f * u, 0f), 0.9f * u)
        val dir = Offset(mt.x - mb.x, mt.y - mb.y)
        val gr = Math.toDegrees(kotlin.math.atan2(dir.y.toDouble(), dir.x.toDouble())).toFloat() + 90f
        rotate(gr, mt) {
            val cab = Rect(mt.x - h * 0.13f, mt.y - h * 0.035f, mt.x + h * 0.09f, mt.y + h * 0.035f)
            drawRect(t.c(P.HIERRO, 0.05f), cab.topLeft, cab.size)
            drawRect(t.b(0.4f), cab.topLeft, Size(cab.width, cab.height * 0.3f))
            drawPath(poligonoBotanico(cab.left, cab.top, cab.left - h * 0.06f, mt.y - h * 0.008f, cab.left - h * 0.06f, mt.y + h * 0.008f, cab.left, cab.bottom), t.c(P.HIERRO, -0.05f))
        }
        // La losa de pizarra con el helecho fósil, apaisada y rota por un canto, apoyada de lado contra la pared, detrás.
        val lb = Rect(cx - h * 0.58f, y - h * 0.5f, cx + h * 0.08f, y)
        rotate(-9f, Offset(lb.right, lb.bottom)) {
            val losa = poligonoBotanico(lb.left, lb.bottom, lb.left + lb.width * 0.02f, lb.top + lb.height * 0.32f, lb.left + lb.width * 0.16f, lb.top + lb.height * 0.06f,
                lb.left + lb.width * 0.38f, lb.top + lb.height * 0.14f, lb.left + lb.width * 0.52f, lb.top, lb.right - lb.width * 0.06f, lb.top + lb.height * 0.1f,
                lb.right, lb.top + lb.height * 0.42f, lb.right - lb.width * 0.03f, lb.bottom)
            drawPath(Path().apply { addPath(losa, Offset(w * 0.016f, w * 0.006f)) }, t.s(0.35f))
            drawPath(losa, t.c(P.PIZARRA, 0.04f))
            clipPath(losa) {
                drawRect(t.b(0.16f), lb.topLeft, Size(lb.width * 0.25f, lb.height))
                drawRect(t.s(0.25f), Offset(lb.right - lb.width * 0.22f, lb.top), Size(lb.width * 0.22f, lb.height))
                // Las capas de la pizarra y el helecho fósil, más claro que la piedra.
                for (j in 0 until 4) drawLine(t.s(0.25f), Offset(lb.left, lb.top + lb.height * (0.22f + 0.2f * j)), Offset(lb.right, lb.top + lb.height * (0.17f + 0.2f * j)), 0.6f * u)
                drawRect(t.c(mezcla(P.PIZARRA, P.PIEDRA, 0.5f), 0.06f), Offset(lb.left, lb.top), Size(lb.width * 0.5f, lb.height * 0.16f))
                // El helecho fósil: tres frondas que se abren en abanico desde un mismo pie, curvadas como en la planta viva.
                val pieF = Offset(lb.left + lb.width * 0.2f, lb.bottom - lb.height * 0.1f)
                val fosil = t.c(mezcla(P.PIZARRA, P.CUARZO, 0.42f))
                frondaBotanico(pieF, lb.height * 0.78f, -72f, fosil, 1f * u, 12)
                frondaBotanico(pieF, lb.height * 0.88f, -48f, fosil, 1f * u, 12)
                frondaBotanico(pieF, lb.height * 0.6f, -24f, t.c(mezcla(P.PIZARRA, P.CUARZO, 0.3f)), 0.9f * u, 10)
            }
            drawPath(losa, t.b(0.3f), style = Stroke(0.7f * u))
        }
        // La drusa: una piedra de base, irregular, y encima un racimo de cristales de cuarzo con un tinte de amatista, cada uno
        // un prisma con su punta, su cara de luz y su cara de sombra, de alturas y ladeos distintos.
        contactoBotanico(Offset(gc.x + w * 0.01f, y), gr2 * 2.4f, h * 0.06f, t, 0.48f)
        val roca = poligonoBotanico(gc.x - gr2 * 1.05f, y, gc.x - gr2 * 0.95f, y - gr2 * 0.45f, gc.x - gr2 * 0.5f, y - gr2 * 0.7f, gc.x + gr2 * 0.1f, y - gr2 * 0.62f,
            gc.x + gr2 * 0.7f, y - gr2 * 0.75f, gc.x + gr2 * 1.05f, y - gr2 * 0.4f, gc.x + gr2 * 1.1f, y)
        drawPath(roca, t.c(P.PIEDRA, -0.08f))
        drawPath(poligonoBotanico(gc.x - gr2 * 1.05f, y, gc.x - gr2 * 0.95f, y - gr2 * 0.45f, gc.x - gr2 * 0.5f, y - gr2 * 0.7f, gc.x - gr2 * 0.3f, y), t.b(0.25f))
        drawPath(poligonoBotanico(gc.x + gr2 * 0.5f, y, gc.x + gr2 * 0.7f, y - gr2 * 0.75f, gc.x + gr2 * 1.05f, y - gr2 * 0.4f, gc.x + gr2 * 1.1f, y), t.s(0.28f))
        val claro = mezcla(P.CUARZO, P.AMATISTA, 0.35f)
        val sombra = mezcla(P.AMATISTA, P.PIZARRA, 0.3f)
        // (dx desde el centro, alto, ancho, ladeo en grados), de atrás adelante.
        listOf(floatArrayOf(-0.35f, 1.15f, 0.26f, -16f), floatArrayOf(0.4f, 1.3f, 0.28f, 12f), floatArrayOf(0.05f, 1.75f, 0.32f, -3f),
            floatArrayOf(-0.62f, 0.8f, 0.22f, -30f), floatArrayOf(0.72f, 0.85f, 0.22f, 28f), floatArrayOf(-0.12f, 0.95f, 0.24f, 8f)).forEach { (dx, al, an, g) ->
            val b0 = Offset(gc.x + gr2 * dx, y - gr2 * 0.55f)
            rotate(g, b0) {
                val pa = gr2 * an / 2; val pl = gr2 * al
                val cuerpo = poligonoBotanico(b0.x - pa, b0.y, b0.x - pa, b0.y - pl * 0.72f, b0.x, b0.y - pl, b0.x + pa, b0.y - pl * 0.72f, b0.x + pa, b0.y)
                drawPath(Path().apply { addPath(cuerpo, Offset(1.2f * u, 1f * u)) }, t.s(0.3f))
                drawPath(cuerpo, t.c(claro, 0.04f))
                drawPath(poligonoBotanico(b0.x, b0.y, b0.x, b0.y - pl, b0.x + pa, b0.y - pl * 0.72f, b0.x + pa, b0.y), t.c(sombra, -0.05f))
                drawPath(poligonoBotanico(b0.x - pa, b0.y - pl * 0.72f, b0.x, b0.y - pl, b0.x, b0.y - pl * 0.78f), t.b(0.5f))
                drawLine(t.b(0.45f), Offset(b0.x - pa * 0.55f, b0.y - pl * 0.1f), Offset(b0.x - pa * 0.55f, b0.y - pl * 0.66f), 0.7f * u)
            }
        }
        destelloBotanico(Offset(gc.x + gr2 * 0.02f, y - gr2 * 2.15f), 1.3f * u, t)
        // Los dos cantos rodados, delante: uno de pizarra y otro claro con su veta.
        listOf(Triple(gc.x + gr2 * 1.25f, h * 0.09f, P.PIZARRA), Triple(gc.x - gr2 * 1.25f, h * 0.07f, mezcla(P.PIEDRA, P.CRUDO, 0.5f))).forEachIndexed { j, (x, r, col) ->
            if (x < a || x > b) return@forEachIndexed
            val pr = Rect(x - r * 1.2f, y - r * 1.3f, x + r * 1.2f, y)
            drawOval(t.s(0.35f), Offset(pr.left + w * 0.008f, y - r * 0.35f), Size(pr.width, r * 0.5f))
            drawOval(t.c(col), pr.topLeft, pr.size)
            clipPath(Path().apply { addOval(pr) }) {
                drawRect(t.s(0.25f), Offset(pr.left, pr.center.y), Size(pr.width, pr.height / 2))
                drawRect(t.b(0.3f), Offset(pr.left + pr.width * 0.15f, pr.top), Size(pr.width * 0.3f, pr.height * 0.4f))
                if (j == 1) drawLine(t.c(P.CUARZO), Offset(pr.left, pr.center.y + r * 0.2f), Offset(pr.right, pr.center.y - r * 0.3f), 1.2f * u)
            }
        }
        // Una etiqueta de la colecta pegada a la piedra, con su renglón.
        val et = Rect(gc.x + gr2 * 0.3f, y - h * 0.07f, gc.x + gr2 * 0.95f, y - h * 0.025f)
        drawRect(t.c(P.PAPEL, -0.05f), et.topLeft, et.size)
        drawLine(t.c(P.TINTA).copy(alpha = 0.55f), Offset(et.left + et.width * 0.15f, et.center.y), Offset(et.right - et.width * 0.2f, et.center.y), 0.6f * u)
    }
}
