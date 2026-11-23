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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que la casa guarda en los huecos de las repisas del estante de Galia ([MuebleEstanteGalia]), de donde acaban las entradas
 * hasta el poste: grupos de dos o tres cosas solapadas, del 70 al 90 % del alto del hueco, distintos de un hueco a otro. El
 * tonelillo de duelas con aros de mimbre y la escudilla de barro negro; la rueca con su copo de lana, el ovillo y el huso con
 * su fusayola; la botella de barro pintado a franjas y el pan redondo; el haz de leña menuda con el hacha de mano. En los huecos
 * con sitio (nunca dos repisas seguidas), la lamparilla de sebo en su cuenco, sobre una laja, junto a la botella: de noche arde.
 */
internal object HuecosEstanteGalia {
    private val P = PaletaGalia

    /** Lo del hueco de la repisa [k] ([r]), de [ocupado] (donde acaba la última entrada) a [tope] (el poste), y su lamparilla en [xLamp]. */
    fun DrawScope.hueco(k: Int, r: Rect, ocupado: Float, tope: Float, xLamp: Float, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width
        val x0 = ocupado + w * 0.022f; val x1 = tope - w * 0.028f
        val libre = x1 - x0
        if (libre < w * 0.07f) return
        val suelo = r.bottom - w * 0.009f
        val h = r.height
        clipRect(r.left, r.top, r.right, r.bottom + w * 0.01f) {
            if (xLamp >= 0f) {
                // El grupo de la lamparilla y, si sobra mucho sitio, otro detrás.
                lamparilla(Offset(xLamp, suelo), a, cerca)
                tonel(Offset(xLamp + w * 0.115f, suelo), h * 0.7f, 9701 + k, a, cerca)
                if (libre > w * 0.42f) grupo((k + 2) % 4, x0 + w * 0.2f, x1, suelo, h, k, a, cerca)
            } else {
                if (libre > w * 0.45f) {
                    grupo(k % 4, x0, x0 + libre * 0.48f, suelo, h, k, a, cerca)
                    grupo((k + 1) % 4, x0 + libre * 0.52f, x1, suelo, h, k + 7, a, cerca)
                } else grupo(k % 4, x0, x1, suelo, h, k, a, cerca)
            }
        }
    }

    /** Uno de los cuatro grupos, en el tramo de [x0] a [x1], de pie en [suelo], en un hueco de alto [h]. */
    private fun DrawScope.grupo(v: Int, x0: Float, x1: Float, suelo: Float, h: Float, s: Int, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width
        val ancho = x1 - x0
        when (v) {
            0 -> {
                tonel(Offset(x0 + minOf(ancho * 0.36f, w * 0.07f), suelo), h * 0.74f, 9710 + s, a, cerca)
                if (ancho > w * 0.13f) escudilla(Offset(x0 + minOf(ancho * 0.72f, w * 0.15f), suelo), w * 0.075f, a, cerca)
            }
            1 -> {
                rueca(Offset(x0 + minOf(ancho * 0.42f, w * 0.08f), suelo), h * 0.9f, a, cerca)
                if (ancho > w * 0.11f) ovillo(Offset(x0 + minOf(ancho * 0.7f, w * 0.13f), suelo), w * 0.034f, P.LANA_ROJA, a, cerca)
                if (ancho > w * 0.18f) huso(Offset(x0 + minOf(ancho * 0.6f, w * 0.17f), suelo), a, cerca)
            }
            2 -> {
                botella(Offset(x0 + minOf(ancho * 0.34f, w * 0.055f), suelo), h * 0.8f, 9720 + s, a, cerca)
                if (ancho > w * 0.12f) pan(Offset(x0 + minOf(ancho * 0.72f, w * 0.13f), suelo), w * 0.085f, a, cerca)
            }
            else -> {
                lena(Offset(x0 + minOf(ancho * 0.38f, w * 0.065f), suelo), h * 0.82f, 9730 + s, a, cerca)
                if (ancho > w * 0.12f) hacha(Offset(x0 + minOf(ancho * 0.66f, w * 0.13f), suelo), h * 0.62f, a, cerca)
            }
        }
    }

    /** La sombra de una silueta en el barro de detrás (a la derecha de día; casi detrás de noche) y la de contacto. */
    private fun DrawScope.sombras(sil: Path, pie: Offset, ancho: Float, a: AireGalia) {
        val w = size.width
        translate(w * (if (a.oscuro) 0.006f else 0.02f), -w * 0.004f) { drawPath(sil, a.sombra(0.3f)) }
        contactoGalia(pie + Offset(w * 0.006f, 0f), ancho * 1.15f, w * 0.012f, a, 0.45f)
    }

    // ---------------------------------------------------------------- las cosas

    /** El tonelillo de duelas, de pie: la panza, las duelas curvas, los aros de mimbre torcido y la tapa con su tarugo. */
    private fun DrawScope.tonel(pie: Offset, alto: Float, s: Int, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val an = alto * 0.62f
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - an * 0.4f, pie.y)
            cubicTo(pie.x - an * 0.58f, pie.y - alto * 0.3f, pie.x - an * 0.58f, pie.y - alto * 0.7f, pie.x - an * 0.4f, pie.y - alto)
            lineTo(pie.x + an * 0.4f, pie.y - alto)
            cubicTo(pie.x + an * 0.58f, pie.y - alto * 0.7f, pie.x + an * 0.58f, pie.y - alto * 0.3f, pie.x + an * 0.4f, pie.y)
            close()
        }
        sombras(sil, pie, an, a)
        drawPath(sil, a.dia(P.ROBLE, 0.04f, k))
        clipPath(sil) {
            // Las duelas: se curvan con la panza, con su junta y un tono distinto cada una.
            for (j in -3..3) {
                val f = j / 3.6f
                val p = Path().apply { moveTo(pie.x + an * 0.4f * f, pie.y); quadraticTo(pie.x + an * 0.6f * f, pie.y - alto * 0.5f, pie.x + an * 0.4f * f, pie.y - alto) }
                drawPath(p, a.luz(P.ROBLE_HONDO, k).copy(alpha = 0.7f), style = Stroke(0.8f * u))
                if (azar(s, j + 3) > 0.5f) drawPath(Path().apply { addPath(p, Offset(an * 0.06f, 0f)) }, a.dia(P.ROBLE_LUZ, 0f, k).copy(alpha = 0.3f), style = Stroke(an * 0.1f))
            }
            cilindroGalia(Rect(pie.x - an * 0.58f, pie.y - alto, pie.x + an * 0.58f, pie.y), a, k, if (a.oscuro) 1f else -1f, 1.3f)
            // Los aros de mimbre torcido: dos arriba y dos abajo.
            listOf(0.1f, 0.18f, 0.82f, 0.9f).forEach { f ->
                val y = pie.y - alto * f
                drawRect(a.dia(P.MIMBRE_OSC, 0f, k), Offset(pie.x - an * 0.6f, y - w * 0.005f), Size(an * 1.2f, w * 0.01f))
                var x = pie.x - an * 0.6f
                while (x < pie.x + an * 0.6f) { drawLine(a.dia(P.MIMBRE_LUZ, 0f, k), Offset(x, y + w * 0.004f), Offset(x + w * 0.006f, y - w * 0.004f), 1f * u); x += w * 0.008f }
                drawRect(a.sombra(0.35f), Offset(pie.x - an * 0.6f, y + w * 0.005f), Size(an * 1.2f, 1f * u))
            }
            // El tarugo del agujero de la panza.
            drawCircle(a.dia(P.ROBLE_LUZ, 0f, k), w * 0.007f, Offset(pie.x - an * 0.08f, pie.y - alto * 0.5f))
            drawCircle(a.sombra(0.4f), w * 0.007f, Offset(pie.x - an * 0.08f, pie.y - alto * 0.5f), style = Stroke(0.8f * u))
        }
        // La tapa, en escorzo.
        val tapa = Rect(pie.x - an * 0.4f, pie.y - alto - an * 0.09f, pie.x + an * 0.4f, pie.y - alto + an * 0.09f)
        ovaloGalia(a.dia(P.ROBLE_LUZ, 0.02f, k), tapa)
        ovaloGalia(a.sombra(0.3f), tapa, style = Stroke(1f * u))
        drawLine(a.sombra(0.3f), Offset(tapa.center.x - an * 0.1f, tapa.top + 1f * u), Offset(tapa.center.x - an * 0.12f, tapa.bottom - 1f * u), 0.8f * u)
    }

    /** La escudilla de barro negro bruñido, baja y ancha, con su brillo. */
    private fun DrawScope.escudilla(pie: Offset, ancho: Float, a: AireGalia, cerca: (Offset) -> Float) {
        val u = density
        val alto = ancho * 0.42f
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - ancho * 0.18f, pie.y)
            cubicTo(pie.x - ancho * 0.42f, pie.y - alto * 0.1f, pie.x - ancho * 0.5f, pie.y - alto * 0.6f, pie.x - ancho * 0.5f, pie.y - alto)
            lineTo(pie.x + ancho * 0.5f, pie.y - alto)
            cubicTo(pie.x + ancho * 0.5f, pie.y - alto * 0.6f, pie.x + ancho * 0.42f, pie.y - alto * 0.1f, pie.x + ancho * 0.18f, pie.y)
            close()
        }
        sombras(sil, pie, ancho, a)
        drawPath(sil, a.dia(P.NEGRO, 0f, k))
        clipPath(sil) {
            drawCircle(a.dia(P.NEGRO_LUZ, 0f, k).copy(alpha = 0.8f), ancho * 0.3f, Offset(pie.x - ancho * 0.22f, pie.y - alto * 0.75f))
            drawCircle(a.frio(0.4f), ancho * 0.4f, Offset(pie.x + ancho * 0.32f, pie.y))
        }
        ovaloGalia(a.dia(mezcla(P.NEGRO, P.NEGRO_LUZ, 0.35f), 0f, k), Rect(pie.x - ancho * 0.5f, pie.y - alto - ancho * 0.07f, pie.x + ancho * 0.5f, pie.y - alto + ancho * 0.07f))
        ovaloGalia(a.luz(P.HOLLIN, k), Rect(pie.x - ancho * 0.44f, pie.y - alto - ancho * 0.05f, pie.x + ancho * 0.44f, pie.y - alto + ancho * 0.05f))
        // Dentro, unos brazaletes de vidrio azul que asoman de canto, con su brillo.
        listOf(-0.16f to -14f, 0.12f to 18f).forEach { (dx, g) ->
            val c = Offset(pie.x + ancho * dx, pie.y - alto - ancho * 0.08f)
            rotate(g, c) {
                ovaloGalia(a.dia(P.VIDRIO, 0f, k), Rect(c.x - ancho * 0.14f, c.y - ancho * 0.15f, c.x + ancho * 0.14f, c.y + ancho * 0.15f), Stroke(ancho * 0.05f))
                drawArc(a.brillo(0.7f, k), 200f, 70f, false, Offset(c.x - ancho * 0.14f, c.y - ancho * 0.15f), Size(ancho * 0.28f, ancho * 0.3f), style = Stroke(1f * u))
            }
        }
        // Lo que tapa el borde de delante de la escudilla.
        drawPath(Path().apply { moveTo(pie.x - ancho * 0.5f, pie.y - alto); quadraticTo(pie.x, pie.y - alto + ancho * 0.12f, pie.x + ancho * 0.5f, pie.y - alto); lineTo(pie.x + ancho * 0.5f, pie.y - alto + ancho * 0.04f); quadraticTo(pie.x, pie.y - alto + ancho * 0.16f, pie.x - ancho * 0.5f, pie.y - alto + ancho * 0.04f); close() }, a.dia(mezcla(P.NEGRO, P.NEGRO_LUZ, 0.3f), 0f, k))
        drawLine(a.brillo(0.7f, k), Offset(pie.x - ancho * 0.38f, pie.y - alto * 0.8f), Offset(pie.x - ancho * 0.3f, pie.y - alto * 0.3f), 1.4f * u, StrokeCap.Round)
    }

    /** La rueca apoyada en el barro, con el copo de lana cruda atado arriba con una cinta, y su sombra. */
    private fun DrawScope.rueca(pie: Offset, alto: Float, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        rotate(8f, pie) {
            val top = Offset(pie.x, pie.y - alto)
            drawLine(a.sombra(0.3f), pie + Offset(w * 0.018f, -w * 0.004f), top + Offset(w * 0.018f, 0f), w * 0.008f, StrokeCap.Round)
            drawLine(a.dia(P.HAYA_OSC, 0f, k), pie, top, w * 0.0075f, StrokeCap.Round)
            drawLine(a.brillo(0.4f, k), pie - Offset(w * 0.002f, 0f), top - Offset(w * 0.002f, 0f), 0.8f * u)
            // El copo: una masa de lana cruda, esponjosa, de bordes deshilachados.
            val cc = Offset(pie.x, pie.y - alto * 0.8f)
            val rx = w * 0.033f; val ry = alto * 0.17f
            val copo = Path()
            for (i in 0..20) {
                val t = i / 20f * 6.283f
                val rr = 1f + 0.14f * kotlin.math.sin(t * 5f) + 0.06f * kotlin.math.sin(t * 11f)
                val p = Offset(cc.x + rx * rr * kotlin.math.cos(t), cc.y + ry * rr * kotlin.math.sin(t))
                if (i == 0) copo.moveTo(p.x, p.y) else copo.lineTo(p.x, p.y)
            }
            copo.close()
            translate(w * (if (a.oscuro) 0.005f else 0.016f), 0f) { drawPath(copo, a.sombra(0.28f)) }
            drawPath(copo, a.dia(P.LANA_CRUDA, 0f, k))
            clipPath(copo) {
                drawCircle(a.claro(0.35f), rx * 0.8f, cc - Offset(rx * 0.4f, ry * 0.3f))
                drawCircle(a.sombra(0.3f), rx * 1.1f, cc + Offset(rx * 0.7f, ry * 0.4f))
                val hebras = Path()
                for (q in 0 until 12) {
                    val y = cc.y - ry + ry * 2f * azar(9740, q)
                    hebras.moveTo(cc.x - rx, y); hebras.quadraticTo(cc.x, y + ry * 0.2f * (azar(9741, q) - 0.5f), cc.x + rx, y + ry * 0.1f)
                }
                drawPath(hebras, a.dia(P.BLANCO_SOMBRA, 0f, k).copy(alpha = 0.6f), style = Stroke(0.7f * u))
            }
            // La cinta roja que lo ciñe.
            drawLine(a.dia(P.LANA_ROJA, 0f, k), Offset(cc.x - rx * 0.95f, cc.y + ry * 0.35f), Offset(cc.x + rx * 0.95f, cc.y + ry * 0.2f), w * 0.006f)
            drawLine(a.dia(P.LANA_ROJA, 0f, k), Offset(cc.x - rx * 0.9f, cc.y - ry * 0.35f), Offset(cc.x + rx * 0.9f, cc.y - ry * 0.45f), w * 0.005f)
        }
    }

    /** Un ovillo de lana teñida, con sus vueltas cruzadas y su cabo suelto. */
    private fun DrawScope.ovillo(pie: Offset, r: Float, lana: Color, a: AireGalia, cerca: (Offset) -> Float) {
        val u = density
        val c = Offset(pie.x, pie.y - r * 0.95f)
        val k = cerca(c)
        val sil = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        sombras(sil, pie, r * 2f, a)
        drawCircle(a.dia(lana, 0f, k), r, c)
        clipPath(sil) {
            val vueltas = Path()
            for (q in 0 until 7) {
                val ang = q * 26f - 40f
                vueltas.addOval(Rect(c.x - r * 1.05f, c.y - r * 0.42f, c.x + r * 1.05f, c.y + r * 0.42f))
                rotate(ang, c) { drawPath(vueltas, a.dia(mezcla(lana, P.HOLLIN, 0.35f), 0f, k).copy(alpha = 0.5f), style = Stroke(0.8f * u)) }
                vueltas.reset()
            }
            drawCircle(a.claro(0.3f), r * 0.6f, c - Offset(r * 0.35f, r * 0.35f))
            drawCircle(a.sombra(0.35f), r * 0.9f, c + Offset(r * 0.55f, r * 0.5f))
        }
        val cabo = Path().apply { moveTo(c.x + r * 0.6f, c.y + r * 0.7f); quadraticTo(c.x + r * 1.4f, pie.y + r * 0.1f, c.x + r * 2.2f, pie.y - r * 0.02f) }
        drawPath(cabo, a.dia(lana, 0f, k), style = Stroke(1f * u, cap = StrokeCap.Round))
    }

    /** El huso acostado: la varilla con la husada de hilo crudo y la fusayola de barro. */
    private fun DrawScope.huso(pie: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(pie)
        val y = pie.y - w * 0.008f
        drawLine(a.sombra(0.35f), Offset(pie.x - w * 0.05f, pie.y), Offset(pie.x + w * 0.05f, pie.y), w * 0.006f)
        drawLine(a.dia(P.HAYA, 0f, k), Offset(pie.x - w * 0.055f, y), Offset(pie.x + w * 0.055f, y - w * 0.003f), w * 0.004f, StrokeCap.Round)
        ovaloGalia(a.dia(P.LANA_CRUDA, -0.05f, k), Rect(pie.x - w * 0.03f, y - w * 0.011f, pie.x + w * 0.016f, y + w * 0.007f))
        ovaloGalia(a.sombra(0.3f), Rect(pie.x - w * 0.03f, y - w * 0.002f, pie.x + w * 0.016f, y + w * 0.007f))
        ovaloGalia(a.dia(P.PARDO, 0f, k), Rect(pie.x + w * 0.022f, y - w * 0.012f, pie.x + w * 0.032f, y + w * 0.006f))
        ovaloGalia(a.brillo(0.35f, k), Rect(pie.x + w * 0.023f, y - w * 0.011f, pie.x + w * 0.027f, y - w * 0.002f))
        drawLine(a.sombra(0.4f), Offset(pie.x + w * 0.033f, y - w * 0.01f), Offset(pie.x + w * 0.033f, y + w * 0.005f), 0.8f * u)
    }

    /**
     * La botella de barro pintado: la panza ovalada sobre un pie, el cuello alto y el labio vuelto; engalba crema con franjas
     * pardas y rojas y una hilera de puntos. [s] cambia un poco las franjas.
     */
    private fun DrawScope.botella(pie: Offset, alto: Float, s: Int, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val an = alto * 0.5f
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - an * 0.2f, pie.y)
            lineTo(pie.x - an * 0.24f, pie.y - alto * 0.05f)
            cubicTo(pie.x - an * 0.56f, pie.y - alto * 0.18f, pie.x - an * 0.56f, pie.y - alto * 0.52f, pie.x - an * 0.17f, pie.y - alto * 0.72f)
            lineTo(pie.x - an * 0.13f, pie.y - alto * 0.94f)
            lineTo(pie.x - an * 0.2f, pie.y - alto)
            lineTo(pie.x + an * 0.2f, pie.y - alto)
            lineTo(pie.x + an * 0.13f, pie.y - alto * 0.94f)
            lineTo(pie.x + an * 0.17f, pie.y - alto * 0.72f)
            cubicTo(pie.x + an * 0.56f, pie.y - alto * 0.52f, pie.x + an * 0.56f, pie.y - alto * 0.18f, pie.x + an * 0.24f, pie.y - alto * 0.05f)
            lineTo(pie.x + an * 0.2f, pie.y)
            close()
        }
        sombras(sil, pie, an, a)
        drawPath(sil, a.dia(mezcla(P.BARRO, P.ROJO, 0.25f), 0f, k))
        clipPath(sil) {
            val caja = Rect(pie.x - an * 0.6f, pie.y - alto, pie.x + an * 0.6f, pie.y)
            // Donde se ha ido la engalba asoma el barro pardo.
            for (q in 0 until 3) drawOval(a.dia(P.PARDO, 0f, k).copy(alpha = 0.6f), Offset(caja.left + caja.width * azar(s, 40 + q), pie.y - alto * (0.15f + 0.6f * azar(s, 43 + q))), Size(an * 0.18f, alto * 0.05f))
            // Las franjas pintadas: pardas anchas y rojas finas; entre ellas, una hilera de puntos.
            val f0 = 0.3f + 0.04f * azar(s, 1)
            listOf(f0 to 0.05f, f0 + 0.08f to 0.016f, 0.62f to 0.04f, 0.7f to 0.014f).forEachIndexed { i, (f, g) ->
                drawRect(a.dia(if (i % 2 == 0) P.HOLLIN else P.ROJO_OSC, 0f, k), Offset(caja.left, pie.y - alto * f - alto * g), Size(caja.width, alto * g))
            }
            var x = caja.left + an * 0.1f
            while (x < caja.right) { drawCircle(a.dia(P.HOLLIN, 0f, k), w * 0.0032f, Offset(x, pie.y - alto * (f0 + 0.15f))); x += an * 0.16f }
            cilindroGalia(caja, a, k, if (a.oscuro) 1f else -1f, 1.35f)
            drawRect(a.sombra(0.35f), Offset(caja.left, pie.y - alto * 0.76f), Size(caja.width, alto * 0.04f))
        }
        ovaloGalia(a.luz(P.HOLLIN, k), Rect(pie.x - an * 0.16f, pie.y - alto - an * 0.04f, pie.x + an * 0.16f, pie.y - alto + an * 0.04f))
        drawLine(a.brillo(0.6f, k), Offset(pie.x - an * 0.36f, pie.y - alto * 0.5f), Offset(pie.x - an * 0.34f, pie.y - alto * 0.24f), 1.4f * u, StrokeCap.Round)
    }

    /** El pan redondo de escanda, con la corteza tostada, abierta en grietas, y la harina en lo alto. */
    private fun DrawScope.pan(pie: Offset, ancho: Float, a: AireGalia, cerca: (Offset) -> Float) {
        val u = density
        val alto = ancho * 0.48f
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - ancho * 0.48f, pie.y)
            cubicTo(pie.x - ancho * 0.54f, pie.y - alto * 0.7f, pie.x - ancho * 0.2f, pie.y - alto, pie.x + ancho * 0.04f, pie.y - alto)
            cubicTo(pie.x + ancho * 0.36f, pie.y - alto, pie.x + ancho * 0.56f, pie.y - alto * 0.6f, pie.x + ancho * 0.48f, pie.y)
            close()
        }
        sombras(sil, pie, ancho, a)
        drawPath(sil, a.dia(Color(0xFFB07838), 0f, k))
        clipPath(sil) {
            drawCircle(a.dia(Color(0xFFD9A964), 0f, k), ancho * 0.36f, Offset(pie.x - ancho * 0.12f, pie.y - alto * 0.72f))
            drawCircle(a.claro(0.3f), ancho * 0.22f, Offset(pie.x - ancho * 0.16f, pie.y - alto * 0.86f))
            drawCircle(a.sombra(0.35f), ancho * 0.5f, Offset(pie.x + ancho * 0.36f, pie.y + alto * 0.1f))
            // Las grietas de la corteza, irregulares (sin cortes en cruz ni en fila).
            val grietas = Path().apply {
                moveTo(pie.x - ancho * 0.3f, pie.y - alto * 0.62f); quadraticTo(pie.x - ancho * 0.1f, pie.y - alto * 0.86f, pie.x + ancho * 0.12f, pie.y - alto * 0.72f)
                moveTo(pie.x + ancho * 0.05f, pie.y - alto * 0.5f); lineTo(pie.x + ancho * 0.26f, pie.y - alto * 0.58f)
            }
            drawPath(grietas, a.dia(Color(0xFF6E4420), 0f, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
            drawPath(Path().apply { addPath(grietas, Offset(0f, -1.2f * u)) }, a.dia(Color(0xFFF0D8A8), 0f, k).copy(alpha = 0.7f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        }
    }

    /** El haz de leña menuda, de pie, atado con una vencejo de mimbre torcido. */
    private fun DrawScope.lena(pie: Offset, alto: Float, s: Int, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val an = w * 0.07f
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        val sil = Path().apply {
            moveTo(pie.x - an * 0.5f, pie.y); lineTo(pie.x - an * 0.34f, pie.y - alto * 0.5f); lineTo(pie.x - an * 0.56f, pie.y - alto)
            lineTo(pie.x + an * 0.5f, pie.y - alto * 0.97f); lineTo(pie.x + an * 0.34f, pie.y - alto * 0.5f); lineTo(pie.x + an * 0.52f, pie.y); close()
        }
        sombras(sil, pie, an, a)
        drawPath(sil, a.dia(P.ROBLE_OSC, 0f, k))
        // Las astillas: palos de distintos tonos, cada uno con su luz.
        for (j in 0 until 9) {
            val f = j / 8f - 0.5f
            val xb = pie.x + an * 0.9f * f; val xt = pie.x + an * 1.0f * f + an * 0.1f * (azar(s, j) - 0.5f)
            val yt = pie.y - alto * (0.94f + 0.06f * azar(s, j + 10))
            val g = an * (0.1f + 0.05f * azar(s, j + 20))
            val col = a.dia(listOf(P.ROBLE, P.ROBLE_LUZ, P.HAYA_OSC)[j % 3], 0f, k)
            drawLine(col, Offset(xb, pie.y), Offset(pie.x + (xb - pie.x) * 0.6f, pie.y - alto * 0.5f), g)
            drawLine(col, Offset(pie.x + (xb - pie.x) * 0.6f, pie.y - alto * 0.5f), Offset(xt, yt), g)
            drawLine(a.brillo(0.3f, k), Offset(pie.x + (xb - pie.x) * 0.6f - g * 0.3f, pie.y - alto * 0.5f), Offset(xt - g * 0.3f, yt), 0.7f * u)
            drawCircle(a.dia(P.HAYA_LUZ, -0.05f, k), g * 0.45f, Offset(xt, yt))
        }
        clipPath(sil) { cilindroGalia(Rect(pie.x - an * 0.6f, pie.y - alto, pie.x + an * 0.6f, pie.y), a, k, if (a.oscuro) 1f else -1f, 1f) }
        // El vencejo, dos vueltas en la cintura.
        listOf(0.46f, 0.52f).forEach { f -> drawLine(a.dia(P.MIMBRE_OSC, 0f, k), Offset(pie.x - an * 0.38f, pie.y - alto * f), Offset(pie.x + an * 0.38f, pie.y - alto * f - w * 0.002f), w * 0.006f, StrokeCap.Round) }
    }

    /** El hacha de mano apoyada: el mango de fresno y la cabeza de hierro de ojo, con su filo claro. */
    private fun DrawScope.hacha(pie: Offset, alto: Float, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(pie - Offset(0f, alto * 0.5f))
        rotate(-14f, pie) {
            val top = Offset(pie.x, pie.y - alto)
            drawLine(a.sombra(0.3f), pie + Offset(w * 0.016f, 0f), top + Offset(w * 0.016f, 0f), w * 0.009f, StrokeCap.Round)
            drawLine(a.dia(P.HAYA, -0.05f, k), pie, top, w * 0.009f, StrokeCap.Round)
            drawLine(a.brillo(0.4f, k), pie - Offset(w * 0.002f, 0f), top - Offset(w * 0.002f, 0f), 0.8f * u)
            // La cabeza: el ojo alrededor del mango y la hoja que se abre hacia la izquierda, con su filo.
            val cab = Path().apply {
                moveTo(top.x + w * 0.008f, top.y + w * 0.006f)
                lineTo(top.x - w * 0.012f, top.y + w * 0.006f)
                quadraticTo(top.x - w * 0.03f, top.y + w * 0.004f, top.x - w * 0.042f, top.y - w * 0.006f)
                lineTo(top.x - w * 0.044f, top.y + w * 0.04f)
                quadraticTo(top.x - w * 0.03f, top.y + w * 0.03f, top.x - w * 0.012f, top.y + w * 0.028f)
                lineTo(top.x + w * 0.008f, top.y + w * 0.028f)
                close()
            }
            drawPath(Path().apply { addPath(cab, Offset(w * 0.012f, w * 0.004f)) }, a.sombra(0.3f))
            drawPath(cab, a.luz(P.HIERRO, k))
            clipPath(cab) {
                drawRect(a.dia(P.HIERRO_LUZ, 0f, k).copy(alpha = 0.8f), Offset(top.x - w * 0.046f, top.y - w * 0.01f), Size(w * 0.008f, w * 0.06f))
                drawRect(a.brillo(0.35f, k), Offset(top.x - w * 0.03f, top.y + w * 0.004f), Size(w * 0.02f, 0.9f * u))
            }
        }
    }

    /** La lamparilla de sebo: un cuenco de barro con su pico pellizcado, el sebo cuajado y la mecha, sobre una laja. */
    private fun DrawScope.lamparilla(pie: Offset, a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = if (a.lumbre) 1f else cerca(pie)
        // La laja, un poco más ancha que el cuenco.
        val laja = Rect(pie.x - w * 0.04f, pie.y - w * 0.014f, pie.x + w * 0.042f, pie.y)
        contactoGalia(pie, w * 0.09f, w * 0.01f, a, 0.4f)
        drawRect(a.dia(P.PIEDRA, 0f, k), laja.topLeft, laja.size)
        drawRect(a.claro(0.35f), laja.topLeft, Size(laja.width, 1f * u))
        drawRect(a.sombra(0.35f), Offset(laja.right - w * 0.016f, laja.top), Size(w * 0.016f, laja.height))
        // El cuenco y su pico.
        val y = laja.top
        val cuenco = Path().apply {
            moveTo(pie.x - w * 0.03f, y - w * 0.018f)
            quadraticTo(pie.x - w * 0.028f, y, pie.x, y)
            quadraticTo(pie.x + w * 0.024f, y, pie.x + w * 0.03f, y - w * 0.016f)
            lineTo(pie.x + w * 0.046f, y - w * 0.022f)
            lineTo(pie.x + w * 0.03f, y - w * 0.024f)
            close()
        }
        drawPath(cuenco, a.dia(P.PARDO, 0f, k))
        clipPath(cuenco) { drawRect(a.sombra(0.3f), Offset(pie.x + w * 0.005f, y - w * 0.03f), Size(w * 0.05f, w * 0.03f)) }
        ovaloGalia(a.dia(P.GRASA, -0.05f, k), Rect(pie.x - w * 0.027f, y - w * 0.024f, pie.x + w * 0.028f, y - w * 0.014f))
        drawLine(a.luz(P.HOLLIN, k), Offset(pie.x + w * 0.036f, y - w * 0.022f), Offset(pie.x + w * 0.04f, y - w * 0.03f), 1.4f * u, StrokeCap.Round)
    }

    /** La llama de la lamparilla (de noche y en los Sueños), con su halo, por encima de las tablas. */
    fun DrawScope.llamaHueco(x: Float, r: Rect, a: AireGalia) {
        if (!a.lumbre) return
        val w = size.width
        val f = if (a.oscuro && a.noche) 0.6f else if (a.oscuro) 1f else 0.65f
        val c = Offset(x + w * 0.04f, r.bottom - w * 0.009f - w * 0.014f - w * 0.042f)
        clipRect(r.left, r.top - w * 0.02f, r.right, r.bottom) {
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.3f * f), P.LLAMA.copy(alpha = 0f)), c, w * 0.11f), w * 0.11f, c)
        }
        val alto = w * 0.032f * (0.7f + 0.3f * f); val an = w * 0.008f
        val base = Offset(c.x, c.y + w * 0.012f)
        fun lengua(h: Float, g: Float, col: Color) = drawPath(Path().apply {
            moveTo(base.x - g, base.y)
            cubicTo(base.x - g * 1.1f, base.y - h * 0.5f, base.x - g * 0.1f, base.y - h * 0.75f, base.x + g * 0.15f, base.y - h)
            cubicTo(base.x + g * 0.4f, base.y - h * 0.6f, base.x + g * 1.1f, base.y - h * 0.4f, base.x + g, base.y)
            close()
        }, col)
        lengua(alto, an, P.BRASA.copy(alpha = 0.85f))
        lengua(alto * 0.75f, an * 0.72f, P.LLAMA)
        lengua(alto * 0.45f, an * 0.42f, P.FUEGO)
    }
}
