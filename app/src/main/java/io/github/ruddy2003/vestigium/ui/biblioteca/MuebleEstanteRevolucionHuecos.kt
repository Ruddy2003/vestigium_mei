package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que acompaña en los huecos libres de las repisas de Revolución ([MuebleEstanteRevolucion]): grupos de dos o tres cosas de
 * la imprenta, altas y solapadas, distintos de un hueco a otro (cada uno sabe cuántos hubo antes). La vela de sebo en su
 * palmatoria de loza (de noche, la luz de en medio), con la botella de gres del aceite de linaza detrás o, en el hueco
 * siguiente, con la galera apoyada y su página compuesta atada con cordel; las resmas apiladas con el ovillo de bramante; la
 * jarra de loza alta con el cuenco y su cuchara; y el tarro de tinta con la bala de entintar tumbada sobre una prueba doblada.
 */
internal object HuecosEstanteRevolucion {
    private val P = PaletaRevolucion

    /** Los grupos que no llevan luz, en el orden en que se reparten. */
    private const val RESMAS = 0
    private const val LOZA = 1
    private const val TINTA = 2

    /** El ancho de cada grupo, en anchos. */
    private fun anchoGrupo(g: Int) = when (g) { RESMAS -> 0.19f; LOZA -> 0.15f; else -> 0.19f }

    /**
     * Lo que acompaña en el hueco de la repisa [r] (de [oc] al costado [xDer]), con la vela en [xc] si la lleva. [k] es cuántos
     * huecos hubo antes: con él cambian la compañía de la vela y el primer grupo, para que dos huecos seguidos no repitan.
     * Devuelve si había sitio.
     */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, xc: Float, a: AireRevolucion, cerca: (Offset) -> Float): Boolean {
        val w = size.width
        var x = oc + w * 0.024f
        val fin = xDer - w * 0.03f
        if (fin - x < w * 0.1f) return false
        val piso = r.bottom - w * 0.006f
        if (xc >= 0f) {
            vela(a, Offset(xc, piso), r, cerca, k % 2 == 0)
            x = xc + w * 0.14f
        }
        var g = Math.floorMod(k * 2 + 1, 3)
        var n = 0
        while (n < 3) {
            val an = w * anchoGrupo(g)
            if (x + an > fin) {
                // Si no cabe el grupo entero pero queda sitio, el más angosto.
                if (fin - x >= w * 0.12f && g != LOZA) { g = LOZA; continue }
                break
            }
            when (g) {
                RESMAS -> resmas(a, Offset(x + an / 2, piso), r, cerca, k)
                LOZA -> loza(a, Offset(x + an / 2, piso), r, cerca, k)
                else -> tinta(a, Offset(x + an / 2, piso), r, cerca, k)
            }
            x += an + w * 0.03f
            g = (g + 1) % 3
            n++
        }
        return true
    }

    /** La llama de la vela que está en [xc] en el hueco [r] (de noche y al ocaso), con su charco chico, después de las tablas. */
    fun DrawScope.llamaHueco(xc: Float, r: Rect, a: AireRevolucion) {
        if (!a.lumbre) return
        val w = size.width
        val p = Offset(xc, r.bottom - r.height * MuebleEstanteRevolucion.VELITA_Y)
        val f = if (a.oscuro && a.noche) 0.7f else if (!a.oscuro) 0.5f else 1f
        if (a.oscuro) drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.28f * f), P.LLAMA.copy(alpha = 0.08f * f), Color.Transparent), p, w * 0.17f), w * 0.17f, p)
        llamaRevolucion(p, w * 0.0095f * (0.75f + 0.25f * f), f)
    }

    // ---------------------------------------------------------------- la vela en su palmatoria, con la botella de gres

    private fun DrawScope.vela(a: AireRevolucion, pie: Offset, r: Rect, cerca: (Offset) -> Float, botella: Boolean) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val llama = r.bottom - r.height * MuebleEstanteRevolucion.VELITA_Y
        val k = cerca(Offset(x, llama))
        val bx = x + w * 0.075f
        val alto = r.height * 0.78f
        val hb = -1f
        if (botella) botella(a, bx, y, alto, k) else galera(a, Offset(bx + w * 0.01f, y), r, k * 0.7f)
        // La palmatoria de loza: el platillo, el cubo y el asa de anillo; la vela de sebo con sus chorreras.
        val plato = w * 0.075f
        contactoRevolucion(Offset(x, y), plato * 1.1f, w * 0.008f, a, 0.38f, hb)
        val vela = Rect(x - w * 0.009f, llama + w * 0.004f, x + w * 0.009f, y - w * 0.026f)
        drawRect(a.dia(P.SEBO, 0f, maxOf(k, if (a.oscuro) 0.9f else 0f)), vela.topLeft, vela.size)
        drawRect(a.sombra(0.22f), Offset(vela.right - vela.width * 0.4f, vela.top), Size(vela.width * 0.4f, vela.height))
        drawRect(a.claro(0.3f), Offset(vela.left + vela.width * 0.15f, vela.top), Size(vela.width * 0.18f, vela.height))
        drawLine(a.dia(P.SEBO, 0.05f, k), Offset(vela.left + w * 0.002f, vela.top + w * 0.004f), Offset(vela.left + w * 0.001f, vela.top + w * 0.04f), w * 0.0035f, StrokeCap.Round)
        if (!a.lumbre) drawLine(P.TINTA, Offset(x, vela.top), Offset(x + w * 0.002f, vela.top - w * 0.008f), w * 0.0022f, StrokeCap.Round)
        val platillo = Path().apply {
            moveTo(x - plato / 2, y - w * 0.008f); lineTo(x + plato / 2, y - w * 0.008f); lineTo(x + plato * 0.42f, y); lineTo(x - plato * 0.42f, y); close()
        }
        drawPath(platillo, a.dia(P.LOZA, 0f, k))
        drawOval(a.dia(P.LOZA, 0.05f, k), Offset(x - plato / 2, y - w * 0.013f), Size(plato, w * 0.01f))
        drawOval(a.dia(P.AZUL_LOZA, 0f, k), Offset(x - plato / 2, y - w * 0.013f), Size(plato, w * 0.01f), style = Stroke(1f * u))
        val cubo = Rect(x - w * 0.013f, y - w * 0.03f, x + w * 0.013f, y - w * 0.01f)
        drawRect(a.dia(P.LOZA, 0f, k), cubo.topLeft, cubo.size)
        drawRect(a.dia(P.AZUL_LOZA, 0f, k), Offset(cubo.left, cubo.top + w * 0.004f), Size(cubo.width, w * 0.002f))
        drawRect(a.sombra(0.25f), Offset(cubo.right - cubo.width * 0.35f, cubo.top), Size(cubo.width * 0.35f, cubo.height))
        drawOval(a.dia(P.LOZA_OSC, 0f, k), Offset(x - plato * 0.72f, y - w * 0.022f), Size(w * 0.022f, w * 0.016f), style = Stroke(w * 0.004f))
        destelloRevolucion(Offset(x - plato * 0.25f, y - w * 0.009f), 1.2f * u, a, k)
        if (a.oscuro) {
            // De noche la vela alumbra el canto de lo que tiene al lado.
            drawRect(a.filo((0.6f * k).coerceAtMost(0.55f)), Offset(bx - w * 0.036f, y - alto * 0.56f), Size(1.4f * u, alto * 0.5f))
        }
    }

    /**
     * La galera apoyada en el fondo del casillero: la bandeja de pino de cantos altos, ladeada, con la página compuesta abajo
     * (el plomo en renglones, sin letras) atada con su cordel de página, y la cuña de madera.
     */
    private fun DrawScope.galera(a: AireRevolucion, pie: Offset, r: Rect, k: Float) {
        val w = size.width; val u = density
        val alto = r.height * 0.86f; val an = w * 0.07f
        contactoRevolucion(Offset(pie.x + w * 0.01f, pie.y), an * 1.2f, w * 0.008f, a, 0.4f, -1f)
        rotate(7f, Offset(pie.x, pie.y)) {
            val g = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
            drawRect(a.sombra(0.35f), Offset(g.left + w * 0.012f, g.top + w * 0.006f), g.size)
            drawRect(a.dia(P.PINO, 0f, k), g.topLeft, g.size)
            val c = w * 0.008f
            val fondo = Rect(g.left + c, g.top, g.right - c, g.bottom - c)
            drawRect(a.dia(P.PINO_OSC, -0.1f, k), fondo.topLeft, fondo.size)
            drawRect(a.dia(P.PINO_LUZ, -0.1f, k), Offset(g.left, g.top), Size(g.width, 1f * u))
            drawRect(a.sombra(0.45f), Offset(fondo.left, fondo.top), Size(fondo.width, 1f * u))
            drawRect(a.claro(0.35f), g.topLeft, Size(1.1f * u, g.height))
            drawRect(a.sombra(0.35f), Offset(g.right - 1.4f * u, g.top), Size(1.4f * u, g.height))
            // La página compuesta: el plomo en renglones con sus blancos, y el cordel que la rodea.
            val pg = Rect(fondo.left + w * 0.004f, fondo.bottom - fondo.height * 0.48f, fondo.right - w * 0.004f, fondo.bottom - w * 0.003f)
            drawRect(a.dia(P.PLOMO, 0f, k), pg.topLeft, pg.size)
            val renglones = Path()
            var y = pg.top + w * 0.005f
            while (y < pg.bottom - w * 0.003f) { renglones.addRect(Rect(pg.left + w * 0.003f, y, pg.right - w * 0.003f, y + w * 0.0018f)); y += w * 0.0055f }
            drawPath(renglones, a.dia(P.PLOMO_LUZ, 0f, k).copy(alpha = 0.7f))
            drawRect(a.dia(P.BRAMANTE, 0f, k), pg.topLeft, pg.size, style = Stroke(1.3f * u))
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(pg.right - w * 0.006f, pg.top), Offset(pg.right + w * 0.004f, pg.top - w * 0.012f), 1.1f * u)
            // La cuña de madera encima de la página.
            drawPath(poligonoRevolucion(pg.left + w * 0.01f, pg.top - w * 0.012f, pg.right - w * 0.02f, pg.top - w * 0.004f, pg.right - w * 0.02f, pg.top, pg.left + w * 0.01f, pg.top), a.dia(P.PINO_LUZ, 0f, k))
            destelloRevolucion(Offset(pg.left + pg.width * 0.3f, pg.top + w * 0.006f), 1.3f * u, a, k)
        }
    }

    /** La botella de gres del aceite de linaza en [bx]: el cuerpo, el hombro, el cuello y su tapón de corcho. */
    private fun DrawScope.botella(a: AireRevolucion, bx: Float, y: Float, alto: Float, k: Float) {
        val w = size.width; val u = density
        val bot = Path().apply {
            moveTo(bx - w * 0.034f, y)
            lineTo(bx - w * 0.036f, y - alto * 0.56f)
            cubicTo(bx - w * 0.036f, y - alto * 0.72f, bx - w * 0.014f, y - alto * 0.74f, bx - w * 0.01f, y - alto * 0.82f)
            lineTo(bx - w * 0.011f, y - alto * 0.95f)
            lineTo(bx + w * 0.011f, y - alto * 0.95f)
            lineTo(bx + w * 0.01f, y - alto * 0.82f)
            cubicTo(bx + w * 0.014f, y - alto * 0.74f, bx + w * 0.036f, y - alto * 0.72f, bx + w * 0.036f, y - alto * 0.56f)
            lineTo(bx + w * 0.034f, y); close()
        }
        contactoRevolucion(Offset(bx + w * 0.006f, y), w * 0.08f, w * 0.008f, a, 0.4f, -1f)
        drawPath(bot, a.dia(P.GRES, 0f, k))
        clipPath(bot) {
            // La mitad de arriba bañada en vidriado pardo (la botella se mete de boca en el baño), con su chorreón.
            drawRect(a.dia(P.GRES_OSC, 0f, k), Offset(bx - w * 0.05f, y - alto), Size(w * 0.1f, alto * 0.52f))
            drawPath(poligonoRevolucion(bx - w * 0.01f, y - alto * 0.49f, bx + w * 0.004f, y - alto * 0.49f, bx - w * 0.002f, y - alto * 0.36f), a.dia(P.GRES_OSC, 0f, k))
            cilindroRevolucion(Rect(bx - w * 0.036f, y - alto, bx + w * 0.036f, y), a, k, -1f, 1.4f)
            // El sello del alfarero: una pella de barro aplastada, lisa y sin marca.
            drawOval(a.dia(P.GRES, -0.12f, k), Offset(bx - w * 0.012f, y - alto * 0.3f), Size(w * 0.022f, w * 0.013f))
            drawOval(a.claro(0.25f), Offset(bx - w * 0.01f, y - alto * 0.3f), Size(w * 0.014f, w * 0.004f))
        }
        drawRect(a.dia(Color(0xFFB08A5A), 0f, k), Offset(bx - w * 0.009f, y - alto), Size(w * 0.018f, alto * 0.06f))
        destelloRevolucion(Offset(bx - w * 0.02f, y - alto * 0.62f), 1.6f * u, a, k)
    }

    // ---------------------------------------------------------------- las resmas con el ovillo

    private fun DrawScope.resmas(a: AireRevolucion, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - r.height * 0.4f))
        val an = w * 0.17f
        val colores = listOf(P.ESTRAZA, Color(0xFF7E8A96), mezcla(P.PAPEL, P.TOSTADO, 0.5f))
        val alto = r.height * 0.78f
        val capas = 3
        var y = pie.y
        contactoRevolucion(Offset(pie.x, pie.y), an * 1.05f, w * 0.009f, a, 0.42f, -1f)
        for (i in 0 until capas) {
            val g = alto / capas * (0.85f + 0.3f * azar(6000 + s, i))
            val dx = (azar(6010 + s, i) - 0.5f) * w * 0.02f
            val rr = Rect(pie.x - an / 2 + dx, y - g, pie.x + an / 2 + dx, y)
            val col = colores[(i + s) % 3]
            drawRect(a.dia(col, 0f, k), rr.topLeft, rr.size)
            drawPath(poligonoRevolucion(rr.left, rr.top, rr.left + rr.height * 0.55f, rr.center.y, rr.left, rr.bottom), a.sombra(0.18f))
            drawPath(poligonoRevolucion(rr.right, rr.top, rr.right - rr.height * 0.55f, rr.center.y, rr.right, rr.bottom), a.sombra(0.12f))
            drawRect(a.claro(0.35f), rr.topLeft, Size(rr.width, 1.2f * u))
            drawRect(a.sombra(0.3f), Offset(rr.left, rr.bottom - rr.height * 0.18f), Size(rr.width, rr.height * 0.18f))
            drawRect(a.sombra(0.45f), Offset(rr.left, rr.bottom - 1f * u), Size(rr.width, 1.2f * u))
            val bx = rr.left + rr.width * (0.3f + 0.35f * azar(6020 + s, i))
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(bx, rr.top), Offset(bx, rr.bottom), 1.5f * u)
            drawLine(a.dia(P.BRAMANTE, 0f, k), Offset(rr.left, rr.center.y), Offset(rr.right, rr.center.y + w * 0.002f), 1.3f * u)
            if (i == 1) {
                // La etiqueta del molino: un papel con su recuadro y renglones, sin letras.
                val et = Rect(rr.left + rr.width * 0.55f, rr.top + rr.height * 0.18f, rr.left + rr.width * 0.88f, rr.top + rr.height * 0.72f)
                drawRect(a.dia(P.PAPEL, 0f, k), et.topLeft, et.size)
                drawRect(a.dia(P.TINTA, 0f, k).copy(alpha = 0.55f), et.deflate(1.2f * u).topLeft, et.deflate(1.2f * u).size, style = Stroke(0.6f * u))
                drawRect(a.dia(P.TINTA, 0f, k).copy(alpha = 0.55f), Offset(et.left + et.width * 0.2f, et.top + et.height * 0.45f), Size(et.width * 0.6f, w * 0.002f))
            }
            if (a.oscuro) drawRect(a.filo((0.55f * k).coerceAtMost(0.5f)), rr.topLeft, Size(rr.width, 1.2f * u))
            y -= g
        }
        // El ovillo de bramante delante, con su cabo suelto.
        val o = Offset(pie.x + an * 0.38f, pie.y - w * 0.024f)
        val rO = w * 0.024f
        contactoRevolucion(Offset(o.x, pie.y), rO * 2.2f, w * 0.007f, a, 0.45f, -1f)
        drawCircle(a.dia(P.BRAMANTE, 0f, k), rO, o)
        val vueltas = Path()
        for (j in 0 until 6) {
            val t = j / 6f
            vueltas.moveTo(o.x - rO * 0.95f, o.y - rO * (0.7f - 1.4f * t)); vueltas.quadraticTo(o.x, o.y - rO * (0.9f - 1.4f * t) + rO * 0.3f, o.x + rO * 0.95f, o.y - rO * (0.55f - 1.4f * t))
        }
        drawPath(vueltas, a.dia(P.BRAMANTE_OSC, 0f, k), style = Stroke(0.8f * u))
        drawCircle(a.sombra(0.25f), rO, o + Offset(rO * 0.35f, rO * 0.2f), style = Stroke(rO * 0.5f))
        drawCircle(a.claro(0.35f), rO * 0.3f, o - Offset(rO * 0.4f, rO * 0.4f))
        val cabo = Path().apply { moveTo(o.x - rO * 0.8f, o.y + rO * 0.5f); quadraticTo(o.x - rO * 2f, pie.y - w * 0.002f, o.x - rO * 3f, pie.y - w * 0.004f) }
        drawPath(cabo, a.dia(P.BRAMANTE, 0f, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- la jarra alta con el cuenco

    private fun DrawScope.loza(a: AireRevolucion, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - r.height * 0.4f))
        val x = pie.x - w * 0.02f
        val al = r.height * 0.8f; val an = w * 0.1f
        // La jarra alta de loza de Rouen: la panza, el cuello, el pico y el asa.
        val cuerpo = Path().apply {
            moveTo(x - an * 0.28f, pie.y)
            cubicTo(x - an * 0.6f, pie.y - al * 0.18f, x - an * 0.58f, pie.y - al * 0.52f, x - an * 0.26f, pie.y - al * 0.7f)
            lineTo(x - an * 0.24f, pie.y - al * 0.9f)
            lineTo(x - an * 0.4f, pie.y - al)
            lineTo(x + an * 0.28f, pie.y - al * 0.98f)
            lineTo(x + an * 0.24f, pie.y - al * 0.86f)
            lineTo(x + an * 0.26f, pie.y - al * 0.7f)
            cubicTo(x + an * 0.58f, pie.y - al * 0.52f, x + an * 0.6f, pie.y - al * 0.18f, x + an * 0.28f, pie.y)
            close()
        }
        val asa = Path().apply {
            moveTo(x + an * 0.26f, pie.y - al * 0.8f)
            cubicTo(x + an * 0.8f, pie.y - al * 0.84f, x + an * 0.74f, pie.y - al * 0.38f, x + an * 0.42f, pie.y - al * 0.3f)
        }
        contactoRevolucion(Offset(x, pie.y), an * 0.95f, w * 0.009f, a, 0.42f, -1f)
        drawPath(asa, a.dia(P.LOZA_OSC, 0f, k), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
        drawPath(asa, a.dia(P.LOZA, 0f, k), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(cuerpo, a.dia(P.LOZA, 0f, k))
        clipPath(cuerpo) {
            cilindroRevolucion(Rect(x - an * 0.6f, pie.y - al, x + an * 0.6f, pie.y), a, k, -1f, 1.4f)
            // La cenefa azul: un filete ancho, una onda doble seguida (sin trazos sueltos que se lean) y el filete del pie.
            val azul = a.dia(P.AZUL_LOZA, 0f, k)
            drawRect(azul, Offset(x - an, pie.y - al * 0.66f), Size(an * 2, w * 0.004f))
            ondaRevolucion(x - an * 0.62f, x + an * 0.62f, pie.y - al * 0.52f, al * 0.025f, an * 0.22f, azul, w * 0.003f)
            ondaRevolucion(x - an * 0.62f - an * 0.11f, x + an * 0.62f, pie.y - al * 0.46f, al * 0.02f, an * 0.22f, azul.copy(alpha = 0.7f), w * 0.0022f)
            drawRect(azul, Offset(x - an, pie.y - al * 0.1f), Size(an * 2, w * 0.004f))
            drawPath(poligonoRevolucion(x + an * 0.04f, pie.y - al * 0.99f, x + an * 0.2f, pie.y - al * 0.985f, x + an * 0.11f, pie.y - al * 0.93f), a.dia(Color(0xFFC07A4E), 0f, k))
        }
        drawPath(cuerpo, a.dia(P.LOZA_OSC, -0.2f, k), style = Stroke(0.8f * u))
        destelloRevolucion(Offset(x - an * 0.3f, pie.y - al * 0.38f), 2f * u, a, k)
        if (a.oscuro) drawPath(cuerpo, a.filo((0.55f * k).coerceAtMost(0.5f)), style = Stroke(1.2f * u))
        // El cuenco con asas de oreja, delante, con la cuchara de palo.
        val c = Offset(pie.x + w * 0.05f, pie.y)
        val ca = w * 0.085f; val cal = w * 0.04f
        contactoRevolucion(c, ca * 1.1f, w * 0.008f, a, 0.45f, -1f)
        val cuenco = Path().apply {
            moveTo(c.x - ca / 2, c.y - cal)
            lineTo(c.x + ca / 2, c.y - cal)
            cubicTo(c.x + ca * 0.48f, c.y - cal * 0.3f, c.x + ca * 0.25f, c.y, c.x, c.y)
            cubicTo(c.x - ca * 0.25f, c.y, c.x - ca * 0.48f, c.y - cal * 0.3f, c.x - ca / 2, c.y - cal); close()
        }
        val cuchara = poligonoRevolucion(c.x + ca * 0.05f, c.y - cal * 0.9f, c.x + ca * 0.12f, c.y - cal * 0.95f, c.x + ca * 0.55f, c.y - cal * 2.1f, c.x + ca * 0.48f, c.y - cal * 2.15f)
        drawPath(cuchara, a.dia(P.PINO, 0f, k))
        drawOval(a.dia(P.LOZA_OSC, 0f, k), Offset(c.x - ca * 0.62f, c.y - cal * 0.98f), Size(ca * 0.2f, cal * 0.25f))
        drawOval(a.dia(P.LOZA_OSC, 0f, k), Offset(c.x + ca * 0.42f, c.y - cal * 0.98f), Size(ca * 0.2f, cal * 0.25f))
        drawPath(cuenco, a.dia(P.LOZA, 0f, k))
        clipPath(cuenco) {
            cilindroRevolucion(Rect(c.x - ca / 2, c.y - cal, c.x + ca / 2, c.y), a, k, -1f, 1.2f)
            drawRect(a.dia(P.AZUL_LOZA, 0f, k), Offset(c.x - ca, c.y - cal * 0.78f), Size(ca * 2, w * 0.003f))
        }
        drawOval(a.dia(P.LOZA_OSC, 0f, k), Offset(c.x - ca / 2, c.y - cal - w * 0.005f), Size(ca, w * 0.01f))
        drawOval(a.dia(P.LOZA, 0.08f, k), Offset(c.x - ca / 2, c.y - cal - w * 0.005f), Size(ca, w * 0.01f), style = Stroke(1.2f * u))
        destelloRevolucion(Offset(c.x - ca * 0.25f, c.y - cal * 0.6f), 1.4f * u, a, k)
    }

    // ---------------------------------------------------------------- el tarro de tinta con la bala de entintar

    private fun DrawScope.tinta(a: AireRevolucion, pie: Offset, r: Rect, cerca: (Offset) -> Float, s: Int) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - r.height * 0.4f))
        // La prueba doblada debajo, con sus renglones y una corrección al margen (un trazo, sin letras).
        val pr = Rect(pie.x - w * 0.095f, pie.y - w * 0.012f, pie.x + w * 0.09f, pie.y)
        contactoRevolucion(Offset(pie.x, pie.y), w * 0.2f, w * 0.008f, a, 0.4f, -1f)
        drawRect(a.dia(P.PAPEL, 0f, k), pr.topLeft, pr.size)
        drawRect(a.claro(0.4f), pr.topLeft, Size(pr.width, 1f * u))
        drawRect(a.sombra(0.25f), Offset(pr.left, pr.bottom - 1.4f * u), Size(pr.width, 1.4f * u))
        // El tarro de tinta: alto, de gres pardo, con la boca negra y la tinta que se escurre.
        val tx = pie.x - w * 0.04f
        val al = r.height * 0.72f; val an = w * 0.075f
        val y = pr.top
        val tarro = Path().apply {
            moveTo(tx - an * 0.45f, y)
            cubicTo(tx - an * 0.56f, y - al * 0.4f, tx - an * 0.54f, y - al * 0.8f, tx - an * 0.4f, y - al * 0.9f)
            lineTo(tx - an * 0.42f, y - al)
            lineTo(tx + an * 0.42f, y - al)
            lineTo(tx + an * 0.4f, y - al * 0.9f)
            cubicTo(tx + an * 0.54f, y - al * 0.8f, tx + an * 0.56f, y - al * 0.4f, tx + an * 0.45f, y)
            close()
        }
        drawPath(tarro, a.dia(P.GRES, -0.1f, k))
        clipPath(tarro) {
            cilindroRevolucion(Rect(tx - an * 0.56f, y - al, tx + an * 0.56f, y), a, k, -1f, 1.4f)
            drawRect(a.dia(P.GRES_OSC, 0f, k), Offset(tx - an, y - al), Size(an * 2, al * 0.14f))
            for (j in 0 until 5) {
                val dx = tx + an * (-0.38f + 0.18f * j + 0.05f * azar(6100 + s, j))
                val largo = al * (0.12f + 0.4f * azar(6110 + s, j))
                drawLine(a.dia(P.TINTA, 0f, k), Offset(dx, y - al * 0.88f), Offset(dx, y - al * 0.88f + largo), w * 0.0045f, StrokeCap.Round)
                drawCircle(a.dia(P.TINTA, 0f, k), w * 0.0036f, Offset(dx, y - al * 0.88f + largo))
            }
            // El cordel que le ata el papel de la tapa, hoy suelto.
            drawRect(a.dia(P.BRAMANTE, 0f, k), Offset(tx - an, y - al * 0.84f), Size(an * 2, w * 0.003f))
        }
        drawOval(a.dia(P.GRES_OSC, -0.1f, k), Offset(tx - an * 0.42f, y - al - w * 0.006f), Size(an * 0.84f, w * 0.012f))
        drawOval(a.dia(P.TINTA, 0f, k), Offset(tx - an * 0.34f, y - al - w * 0.004f), Size(an * 0.68f, w * 0.008f))
        destelloRevolucion(Offset(tx - an * 0.22f, y - al * 0.55f), 1.6f * u, a, k)
        if (a.oscuro) drawPath(tarro, a.filo((0.55f * k).coerceAtMost(0.5f)), style = Stroke(1.2f * u))
        // La bala de entintar tumbada delante, con el mango hacia arriba a la derecha: la copa de madera con su corona de clavos
        // y el cuero relleno de lana, negro de tinta en la cara.
        val c = Offset(pie.x + w * 0.04f, y - w * 0.034f)
        rotate(-28f, c) {
            val rb = w * 0.034f
            val cuero = Path().apply { addOval(Rect(c.x - rb * 0.55f, c.y - rb, c.x + rb * 0.55f, c.y + rb)) }
            val copa = Rect(c.x, c.y - rb * 0.82f, c.x + rb * 0.75f, c.y + rb * 0.82f)
            val mango = Rect(copa.right, c.y - rb * 0.2f, copa.right + rb * 1.9f, c.y + rb * 0.2f)
            drawRoundRect(a.dia(P.PINO, 0f, k), mango.topLeft, mango.size, androidx.compose.ui.geometry.CornerRadius(rb * 0.2f))
            drawRect(a.claro(0.3f), mango.topLeft, Size(mango.width, rb * 0.1f))
            drawRect(a.sombra(0.25f), Offset(mango.left, mango.bottom - rb * 0.12f), Size(mango.width, rb * 0.12f))
            drawOval(a.dia(P.PINO_OSC, 0f, k), Offset(mango.right - rb * 0.25f, mango.top - rb * 0.08f), Size(rb * 0.5f, mango.height + rb * 0.16f))
            drawRect(a.dia(P.PINO_OSC, 0f, k), copa.topLeft, copa.size)
            drawRect(a.claro(0.25f), copa.topLeft, Size(copa.width, rb * 0.14f))
            drawPath(cuero, a.dia(P.CUERO, 0f, k))
            clipPath(cuero) {
                drawRect(a.dia(P.TINTA, 0f, k), Offset(c.x - rb, c.y - rb), Size(rb * 0.7f, rb * 2))
                drawOval(a.claro(0.3f), Offset(c.x - rb * 0.4f, c.y - rb * 0.7f), Size(rb * 0.25f, rb * 0.6f))
            }
            for (j in 0 until 5) clavoRevolucion(Offset(copa.left + rb * 0.12f, copa.top + copa.height * (0.12f + 0.19f * j)), 0.9f * u, a, k, P.HIERRO_LUZ)
        }
    }
}
