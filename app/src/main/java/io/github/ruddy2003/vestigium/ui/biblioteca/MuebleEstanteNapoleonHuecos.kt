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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que hay en los huecos libres de las repisas de Napoleónico ([MuebleEstanteNapoleon]), en grupos de dos o tres cosas
 * solapadas, del 70 al 90 % del alto del hueco y distintos de un hueco a otro: el candelero de latón con su vela (que de noche
 * arde: la luz de en medio) junto a dos libros tumbados; la cafetera de plata con su mango de ébano y la taza de porcelana
 * con su filete de oro; la jarra de estaño con la hogaza de pan de munición y el cuchillo; la caja de las pistolas con el frasco
 * de pólvora de latón encima.
 */
internal object HuecosEstanteNapoleon {
    private val P = PaletaNapoleon

    /**
     * El hueco de la repisa [k] ([r]), de [oc] (donde acaban las entradas) a [der] (el costado): el candelero si [xc] (su x)
     * no es -1, y uno o dos grupos más según el sitio.
     */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, der: Float, xc: Float, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width
        val x0 = oc + w * 0.025f
        val x1 = der - w * 0.03f
        if (x1 - x0 < w * 0.07f) return
        var x = x0
        if (xc >= 0f) {
            candelero(Offset(xc, r.bottom), r, a, cerca)
            x = xc + w * 0.06f
        }
        // Los grupos, uno tras otro mientras quepan; cuál toca sale de la repisa, así cambian de un hueco a otro.
        var g = k * 3 + 1
        while (x1 - x > w * 0.1f) {
            val ancho = when (g % 3) { 0 -> 0.19f; 1 -> 0.17f; else -> 0.2f } * w
            if (x + ancho > x1 + w * 0.02f) {
                // No cabe el grupo entero: va lo más alto de él solo.
                if (x1 - x > w * 0.08f) solo(g % 3, Offset(x + (x1 - x) / 2, r.bottom), r, a, cerca)
                break
            }
            when (g % 3) {
                0 -> cafe(Offset(x, r.bottom), r, a, cerca)
                1 -> jarra(Offset(x, r.bottom), r, a, cerca)
                else -> pistolas(Offset(x, r.bottom), r, a, cerca)
            }
            x += ancho + w * 0.02f; g++
        }
    }

    /** Lo más alto de un grupo, solo, centrado en [c] (para un hueco estrecho). */
    private fun DrawScope.solo(g: Int, c: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width
        when (g) {
            0 -> cafetera(Offset(c.x, c.y), r, a, cerca)
            1 -> jarraSola(Offset(c.x, c.y), r, a, cerca)
            else -> frasco(Offset(c.x, c.y - r.height * 0.02f), r.height * 0.62f, a, cerca(Offset(c.x, c.y - r.height * 0.3f)))
        }
    }

    /** La sombra que deja algo en el paño de atrás (corrida a la derecha de día; de noche, hacia fuera de su luz) y en la tabla. */
    private fun DrawScope.sombraDetras(p: Path, a: AireNapoleon, dx: Float) {
        val w = size.width
        drawPath(Path().apply { addPath(p, Offset(w * 0.014f * dx, w * 0.006f)) }, a.sombra(if (a.oscuro) 0.4f else 0.24f))
    }

    // ---------------------------------------------------------------- el candelero (y la luz de en medio)

    /** El candelero de latón de fuste acanalado con su vela, junto a dos libros tumbados y las despabiladeras en su bandejita. */
    private fun DrawScope.candelero(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        val k = cerca(Offset(pie.x, pie.y - hh * 0.4f))
        // Los dos libros tumbados detrás, uno encima de otro, ladeados.
        val libros = listOf(Rect(pie.x + w * 0.005f, pie.y - hh * 0.14f, pie.x + w * 0.165f, pie.y) to Color(0xFF2B3F5E),
            Rect(pie.x + w * 0.018f, pie.y - hh * 0.25f, pie.x + w * 0.15f, pie.y - hh * 0.14f) to Color(0xFF6A2A20))
        libros.forEachIndexed { i, (lr, col) ->
            val kl = cerca(lr.center)
            rotate(if (i == 1) -2.5f else 0f, lr.center) {
                sombraDetras(Path().apply { addRect(lr) }, a, 1f)
                drawRect(a.dia(col, 0f, kl), lr.topLeft, lr.size)
                // El canto de las hojas (claro, a rayas finas) por delante y las tapas.
                val hojas = Rect(lr.left + w * 0.006f, lr.top + lr.height * 0.18f, lr.right - w * 0.004f, lr.bottom - lr.height * 0.18f)
                drawRect(a.dia(P.PAPEL, -0.12f, kl), hojas.topLeft, hojas.size)
                var y = hojas.top + 1.2f * u
                while (y < hojas.bottom) { drawLine(a.dia(P.CARTULINA_OSC, 0f, kl).copy(alpha = 0.5f), Offset(hojas.left, y), Offset(hojas.right, y), 0.5f * u); y += 1.8f * u }
                drawRect(a.brillo(0.4f, kl), lr.topLeft, Size(lr.width, 1f * u))
                drawRect(a.sombra(0.3f), Offset(lr.left, lr.bottom - 1.2f * u), Size(lr.width, 1.2f * u))
            }
        }
        // Las despabiladeras en su bandejita, encima de los libros.
        val bj = Rect(pie.x + w * 0.04f, pie.y - hh * 0.275f, pie.x + w * 0.13f, pie.y - hh * 0.25f)
        ovaloNapoleon(a.dia(P.LATON_OSC, 0f, k), bj)
        ovaloNapoleon(a.dia(P.LATON, 0f, k), bj.deflate(0.8f * u))
        drawLine(a.dia(Color(0xFF5E5A56), 0f, k), Offset(bj.left + w * 0.01f, bj.center.y), Offset(bj.right - w * 0.005f, bj.top), 1.2f * u)
        // El candelero: el pie redondo con su moldura, el fuste acanalado con su anillo y el mechero de arriba.
        val laton = a.dia(P.LATON, 0f, k); val latonOsc = a.dia(P.LATON_OSC, 0f, k)
        contactoNapoleon(Offset(pie.x, pie.y), w * 0.09f, hh * 0.04f, a, 0.45f)
        val base = Path().apply {
            moveTo(pie.x - w * 0.038f, pie.y)
            cubicTo(pie.x - w * 0.038f, pie.y - hh * 0.05f, pie.x - w * 0.012f, pie.y - hh * 0.05f, pie.x - w * 0.01f, pie.y - hh * 0.1f)
            lineTo(pie.x + w * 0.01f, pie.y - hh * 0.1f)
            cubicTo(pie.x + w * 0.012f, pie.y - hh * 0.05f, pie.x + w * 0.038f, pie.y - hh * 0.05f, pie.x + w * 0.038f, pie.y)
            close()
        }
        sombraDetras(base, a, 1f)
        drawPath(base, laton)
        clipPath(base) { cilindroNapoleon(Rect(pie.x - w * 0.04f, pie.y - hh * 0.1f, pie.x + w * 0.04f, pie.y), a, k, if (a.oscuro) 1f else -1f, 1.6f) }
        ovaloNapoleon(latonOsc, Rect(pie.x - w * 0.04f, pie.y - hh * 0.012f, pie.x + w * 0.04f, pie.y + hh * 0.006f))
        val fuste = Rect(pie.x - w * 0.009f, pie.y - hh * 0.46f, pie.x + w * 0.009f, pie.y - hh * 0.1f)
        drawRect(a.sombra(0.3f), Offset(fuste.left + w * 0.012f, fuste.top + w * 0.006f), fuste.size)
        drawRect(laton, fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) {
            cilindroNapoleon(fuste, a, k, if (a.oscuro) 1f else -1f, 1.6f)
            listOf(0.3f, 0.55f, 0.8f).forEach { f -> drawLine(latonOsc.copy(alpha = 0.6f), Offset(fuste.left + fuste.width * f, fuste.top), Offset(fuste.left + fuste.width * f, fuste.bottom), 0.6f * u) }
        }
        ovaloNapoleon(laton, Rect(pie.x - w * 0.014f, pie.y - hh * 0.13f, pie.x + w * 0.014f, pie.y - hh * 0.1f))
        ovaloNapoleon(latonOsc, Rect(pie.x - w * 0.016f, pie.y - hh * 0.3f, pie.x + w * 0.016f, pie.y - hh * 0.275f))
        // El mechero y su arandela.
        drawRect(laton, Offset(pie.x - w * 0.012f, pie.y - hh * 0.5f), Size(w * 0.024f, hh * 0.04f))
        ovaloNapoleon(latonOsc, Rect(pie.x - w * 0.024f, pie.y - hh * 0.475f, pie.x + w * 0.024f, pie.y - hh * 0.455f))
        ovaloNapoleon(a.dia(P.LATON_LUZ, 0f, k), Rect(pie.x - w * 0.022f, pie.y - hh * 0.478f, pie.x + w * 0.02f, pie.y - hh * 0.468f))
        // La vela, gastada, con su chorrete de cera.
        val vw = w * 0.016f
        val cima = pie.y - hh * 0.76f
        val vela = Rect(pie.x - vw / 2, cima, pie.x + vw / 2, pie.y - hh * 0.5f)
        val cera = if (a.oscuro) a.luz(Color(0xFFF4ECD8), maxOf(k, 0.85f)) else a.dia(Color(0xFFF2EAD6), 0f, k)
        drawRect(cera, vela.topLeft, vela.size)
        drawRect(a.sombra(0.22f), Offset(vela.right - vela.width * 0.35f, vela.top), Size(vela.width * 0.35f, vela.height))
        ovaloNapoleon(cera, Rect(vela.left - w * 0.003f, vela.top + hh * 0.03f, vela.left + w * 0.004f, vela.top + hh * 0.1f))
        drawLine(Color(0xFF2A1A10), Offset(pie.x, cima), Offset(pie.x, cima - hh * 0.03f), 0.9f * u)
        if (a.oscuro) drawLine(a.filo(0.7f), Offset(fuste.left, fuste.top), Offset(fuste.left, fuste.bottom), 1f * u)
    }

    /** La llama del candelero del hueco [r] (en [x]), de noche: su halo y su lengua. */
    fun DrawScope.llamaHueco(x: Float, r: Rect, a: AireNapoleon) {
        if (!a.oscuro) return
        val w = size.width
        val c = Offset(x, r.bottom - r.height * 0.76f - r.height * 0.06f)
        val f = if (a.noche) 0.6f else 1f
        mancha(c, w * 0.16f, P.LLAMA.copy(alpha = 0.2f * f))
        mancha(c, w * 0.05f, P.LLAMA.copy(alpha = 0.45f * f))
        ovaloNapoleon(P.LLAMA.copy(alpha = f), Rect(c.x - w * 0.006f, c.y - w * 0.018f, c.x + w * 0.006f, c.y + w * 0.012f))
        ovaloNapoleon(P.FUEGO.copy(alpha = f), Rect(c.x - w * 0.003f, c.y - w * 0.008f, c.x + w * 0.003f, c.y + w * 0.009f))
    }

    // ---------------------------------------------------------------- la cafetera y la taza

    /** La cafetera de plata de panza de pera, con su pico, su mango de ébano y la tapa abombada con su remate. */
    private fun DrawScope.cafetera(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        val k = cerca(Offset(pie.x, pie.y - hh * 0.4f))
        val cw = w * 0.055f
        val alto = hh * 0.76f
        val cuerpo = Path().apply {
            moveTo(pie.x - cw * 0.7f, pie.y)
            cubicTo(pie.x - cw * 1.25f, pie.y - alto * 0.2f, pie.x - cw * 1.1f, pie.y - alto * 0.5f, pie.x - cw * 0.62f, pie.y - alto * 0.72f)
            lineTo(pie.x - cw * 0.66f, pie.y - alto * 0.8f)
            lineTo(pie.x + cw * 0.66f, pie.y - alto * 0.8f)
            lineTo(pie.x + cw * 0.62f, pie.y - alto * 0.72f)
            cubicTo(pie.x + cw * 1.1f, pie.y - alto * 0.5f, pie.x + cw * 1.25f, pie.y - alto * 0.2f, pie.x + cw * 0.7f, pie.y)
            close()
        }
        val pico = Path().apply {
            moveTo(pie.x + cw * 0.9f, pie.y - alto * 0.4f)
            cubicTo(pie.x + cw * 1.5f, pie.y - alto * 0.5f, pie.x + cw * 1.5f, pie.y - alto * 0.68f, pie.x + cw * 1.85f, pie.y - alto * 0.78f)
            lineTo(pie.x + cw * 1.95f, pie.y - alto * 0.74f)
            cubicTo(pie.x + cw * 1.7f, pie.y - alto * 0.6f, pie.x + cw * 1.55f, pie.y - alto * 0.4f, pie.x + cw * 1.0f, pie.y - alto * 0.28f)
            close()
        }
        val tapa = Path().apply {
            moveTo(pie.x - cw * 0.7f, pie.y - alto * 0.8f)
            cubicTo(pie.x - cw * 0.65f, pie.y - alto * 0.93f, pie.x + cw * 0.65f, pie.y - alto * 0.93f, pie.x + cw * 0.7f, pie.y - alto * 0.8f)
            close()
        }
        sombraDetras(Path().apply { addPath(cuerpo); addPath(pico); addPath(tapa) }, a, 1f)
        contactoNapoleon(pie, cw * 2.2f, hh * 0.04f, a, 0.45f)
        val plata = a.dia(P.PLATA, 0f, k)
        drawPath(pico, plata)
        drawPath(pico, a.sombra(0.25f))
        drawPath(cuerpo, plata)
        clipPath(cuerpo) {
            // La plata refleja: claros y oscuros francos en franjas, y el paño verde reflejado en un costado.
            cilindroNapoleon(Rect(pie.x - cw * 1.2f, pie.y - alto, pie.x + cw * 1.2f, pie.y), a, k, if (a.oscuro) 1f else -1f, 1.8f)
            drawRect(a.dia(P.PANO, 0f, k).copy(alpha = 0.35f), Offset(pie.x + cw * 0.4f, pie.y - alto), Size(cw * 0.5f, alto))
            drawRect(a.brillo(0.85f, maxOf(k, 0.3f)), Offset(pie.x - cw * 0.55f, pie.y - alto * 0.6f), Size(cw * 0.14f, alto * 0.45f))
            drawRect(a.sombra(0.35f), Offset(pie.x - cw * 1.3f, pie.y - alto * 0.75f), Size(cw * 2.6f, alto * 0.03f))
        }
        drawPath(tapa, plata)
        drawPath(tapa, a.sombra(0.2f))
        drawCircle(a.dia(P.PLATA, 0f, k), cw * 0.22f, Offset(pie.x, pie.y - alto * 0.94f))
        drawCircle(a.brillo(0.7f, maxOf(k, 0.3f)), cw * 0.08f, Offset(pie.x - cw * 0.06f, pie.y - alto * 0.955f))
        // El mango de ébano, recto y de lado, con su virola.
        val m0 = Offset(pie.x - cw * 0.9f, pie.y - alto * 0.48f)
        drawLine(a.dia(Color(0xFF1E1714), 0f, k), m0, m0 + Offset(-cw * 1.1f, -alto * 0.12f), w * 0.012f, StrokeCap.Round)
        drawLine(a.brillo(0.3f, k), m0 + Offset(0f, -w * 0.004f), m0 + Offset(-cw * 1.0f, -alto * 0.12f - w * 0.004f), 0.8f * u)
        drawRect(plata, Rect(m0.x - w * 0.004f, m0.y - w * 0.008f, m0.x + w * 0.004f, m0.y + w * 0.006f).topLeft, Size(w * 0.008f, w * 0.014f))
        if (a.oscuro) drawPath(cuerpo, a.filo(0.6f * k), style = Stroke(1.2f * u))
    }

    /** La cafetera y, delante, la taza de porcelana en su plato, con el filete de oro. */
    private fun DrawScope.cafe(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        cafetera(Offset(pie.x + w * 0.065f, pie.y), r, a, cerca)
        val c = Offset(pie.x + w * 0.13f, pie.y)
        val k = cerca(c)
        val por = a.dia(P.PORCELANA, 0f, k)
        val oro = a.dia(P.GALON, 0f, k)
        contactoNapoleon(c, w * 0.11f, hh * 0.035f, a, 0.4f)
        // El plato.
        val plato = Rect(c.x - w * 0.05f, c.y - hh * 0.04f, c.x + w * 0.05f, c.y)
        ovaloNapoleon(a.sombra(0.25f), plato.translate(0f, 1f * u))
        ovaloNapoleon(por, plato)
        ovaloNapoleon(oro, plato, style = Stroke(0.8f * u))
        // La taza: el cuerpo recto que se abre arriba, el asa en anillo y el filete de oro.
        val taza = poligonoNapoleon(c.x - w * 0.034f, c.y - hh * 0.2f, c.x + w * 0.034f, c.y - hh * 0.2f, c.x + w * 0.026f, c.y - hh * 0.03f, c.x - w * 0.026f, c.y - hh * 0.03f)
        drawPath(taza, por)
        clipPath(taza) { cilindroNapoleon(Rect(c.x - w * 0.034f, c.y - hh * 0.2f, c.x + w * 0.034f, c.y), a, k, if (a.oscuro) 1f else -1f, 1.2f) }
        drawRect(oro, Offset(c.x - w * 0.034f, c.y - hh * 0.195f), Size(w * 0.068f, 1.2f * u))
        drawRect(oro, Offset(c.x - w * 0.028f, c.y - hh * 0.06f), Size(w * 0.056f, 0.8f * u))
        val asa = Rect(c.x + w * 0.026f, c.y - hh * 0.17f, c.x + w * 0.05f, c.y - hh * 0.08f)
        ovaloNapoleon(por, asa, style = Stroke(w * 0.005f))
        ovaloNapoleon(a.sombra(0.3f), Rect(c.x - w * 0.033f, c.y - hh * 0.205f, c.x + w * 0.033f, c.y - hh * 0.19f))
        ovaloNapoleon(a.dia(Color(0xFF3A2216), 0f, k), Rect(c.x - w * 0.029f, c.y - hh * 0.202f, c.x + w * 0.029f, c.y - hh * 0.194f))
    }

    // ---------------------------------------------------------------- la jarra, el pan y el cuchillo

    /** La jarra de estaño con su tapa de bisagra y su asa. */
    private fun DrawScope.jarraSola(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        val k = cerca(Offset(pie.x, pie.y - hh * 0.35f))
        val jw = w * 0.05f
        val alto = hh * 0.66f
        val cuerpo = Path().apply {
            moveTo(pie.x - jw * 0.95f, pie.y)
            cubicTo(pie.x - jw * 1.15f, pie.y - alto * 0.3f, pie.x - jw * 1.1f, pie.y - alto * 0.55f, pie.x - jw * 0.7f, pie.y - alto * 0.85f)
            lineTo(pie.x + jw * 0.85f, pie.y - alto * 0.88f)
            cubicTo(pie.x + jw * 1.1f, pie.y - alto * 0.55f, pie.x + jw * 1.15f, pie.y - alto * 0.3f, pie.x + jw * 0.95f, pie.y)
            close()
        }
        sombraDetras(cuerpo, a, 1f)
        contactoNapoleon(pie, jw * 2.4f, hh * 0.04f, a, 0.45f)
        val est = a.dia(P.ESTANO, 0f, k)
        drawPath(cuerpo, est)
        clipPath(cuerpo) {
            cilindroNapoleon(Rect(pie.x - jw * 1.2f, pie.y - alto, pie.x + jw * 1.2f, pie.y), a, k, if (a.oscuro) 1f else -1f, 1.5f)
            listOf(0.25f, 0.7f).forEach { f -> drawRect(a.sombra(0.3f), Offset(pie.x - jw * 1.3f, pie.y - alto * f), Size(jw * 2.6f, 1.4f * u)) }
            drawRect(a.claro(0.3f), Offset(pie.x - jw * 1.3f, pie.y - alto * 0.7f + 1.4f * u), Size(jw * 2.6f, 0.8f * u))
        }
        // La tapa de bisagra, algo levantada, y el asa.
        val tapa = poligonoNapoleon(pie.x - jw * 0.75f, pie.y - alto * 0.86f, pie.x + jw * 0.9f, pie.y - alto * 0.9f, pie.x + jw * 0.85f, pie.y - alto * 0.97f, pie.x - jw * 0.5f, pie.y - alto * 0.95f)
        drawPath(tapa, a.dia(P.ESTANO, 0.05f, k))
        drawPath(tapa, a.sombra(0.35f), style = Stroke(0.8f * u))
        drawCircle(a.dia(P.ESTANO, -0.1f, k), w * 0.007f, Offset(pie.x + jw * 0.95f, pie.y - alto * 0.98f))
        val asa = Path().apply { moveTo(pie.x + jw * 0.95f, pie.y - alto * 0.8f); cubicTo(pie.x + jw * 1.9f, pie.y - alto * 0.8f, pie.x + jw * 1.8f, pie.y - alto * 0.3f, pie.x + jw * 1.0f, pie.y - alto * 0.2f) }
        drawPath(asa, a.sombra(0.3f), style = Stroke(w * 0.01f))
        drawPath(asa, est, style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, a.brillo(0.4f, k), style = Stroke(0.8f * u))
        if (a.oscuro) drawPath(cuerpo, a.filo(0.5f * k), style = Stroke(1.2f * u))
    }

    /** La jarra de estaño con la hogaza de pan de munición delante y el cuchillo encima. */
    private fun DrawScope.jarra(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        jarraSola(Offset(pie.x + w * 0.055f, pie.y), r, a, cerca)
        // La hogaza redonda, con su corteza cuarteada y la harina.
        val c = Offset(pie.x + w * 0.115f, pie.y)
        val k = cerca(c)
        val hog = Rect(c.x - w * 0.06f, c.y - hh * 0.27f, c.x + w * 0.06f, c.y)
        contactoNapoleon(c, w * 0.14f, hh * 0.05f, a, 0.45f)
        val pan = Path().apply { addArc(hog, 180f, 180f); lineTo(hog.left, hog.bottom - hh * 0.02f); close() }
        drawPath(pan, a.dia(Color(0xFFB07A3E), 0f, k))
        clipPath(pan) {
            cilindroNapoleon(hog, a, k, if (a.oscuro) 1f else -1f, 1.3f)
            ovaloNapoleon(a.dia(Color(0xFF7A4A22), 0f, k).copy(alpha = 0.5f), Rect(hog.left, hog.top + hog.height * 0.55f, hog.right, hog.bottom + hog.height * 0.3f))
            // Los cortes de la corteza, abiertos y claros.
            listOf(-0.3f, 0f, 0.3f).forEach { f ->
                val x = c.x + hog.width * f * 0.5f
                drawLine(a.dia(Color(0xFFE6C48A), 0f, k), Offset(x - w * 0.012f, hog.top + hog.height * 0.25f), Offset(x + w * 0.012f, hog.top + hog.height * 0.12f), 1.6f * u, StrokeCap.Round)
                drawLine(a.sombra(0.3f), Offset(x - w * 0.012f, hog.top + hog.height * 0.25f + 1.2f * u), Offset(x + w * 0.012f, hog.top + hog.height * 0.12f + 1.2f * u), 0.8f * u)
            }
            ovaloNapoleon(Color(0xFFF2E8D8).copy(alpha = if (a.oscuro) 0.08f else 0.25f), Rect(hog.left + hog.width * 0.15f, hog.top, hog.left + hog.width * 0.6f, hog.top + hog.height * 0.25f))
        }
        // El cuchillo de mango de cuerno, cruzado sobre la hogaza.
        rotate(-18f, Offset(c.x, hog.top + hog.height * 0.4f)) {
            val y = hog.top + hog.height * 0.4f
            drawRect(a.sombra(0.3f), Offset(c.x - w * 0.05f, y + 1.6f * u), Size(w * 0.13f, w * 0.008f))
            drawRect(a.dia(Color(0xFFBFC4C6), 0f, k), Offset(c.x - w * 0.02f, y), Size(w * 0.1f, w * 0.008f))
            drawRect(a.brillo(0.6f, k), Offset(c.x - w * 0.02f, y), Size(w * 0.1f, 0.7f * u))
            drawRect(a.dia(Color(0xFF3A2A1E), 0f, k), Offset(c.x - w * 0.06f, y - w * 0.001f), Size(w * 0.045f, w * 0.01f))
        }
    }

    // ---------------------------------------------------------------- las pistolas y el frasco de pólvora

    /** El frasco de pólvora de latón, de pera, con su pico y su cordón, de alto [alto], de pie en [pie]. */
    private fun DrawScope.frasco(pie: Offset, alto: Float, a: AireNapoleon, k: Float) {
        val w = size.width; val u = density
        val fw = alto * 0.3f
        val cuerpo = Path().apply {
            moveTo(pie.x, pie.y)
            cubicTo(pie.x - fw * 1.05f, pie.y, pie.x - fw * 1.05f, pie.y - alto * 0.55f, pie.x - fw * 0.25f, pie.y - alto * 0.78f)
            lineTo(pie.x + fw * 0.25f, pie.y - alto * 0.78f)
            cubicTo(pie.x + fw * 1.05f, pie.y - alto * 0.55f, pie.x + fw * 1.05f, pie.y, pie.x, pie.y)
            close()
        }
        sombraDetras(cuerpo, a, 1f)
        drawPath(cuerpo, a.dia(P.LATON, 0f, k))
        clipPath(cuerpo) {
            cilindroNapoleon(Rect(pie.x - fw, pie.y - alto, pie.x + fw, pie.y), a, k, if (a.oscuro) 1f else -1f, 1.6f)
            // El repujado: un óvalo en el centro con su cerco.
            ovaloNapoleon(a.dia(P.LATON_OSC, 0f, k).copy(alpha = 0.7f), Rect(pie.x - fw * 0.45f, pie.y - alto * 0.5f, pie.x + fw * 0.45f, pie.y - alto * 0.15f), style = Stroke(1f * u))
        }
        // El pico con su resorte y el cordón verde que sube.
        drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(pie.x - fw * 0.16f, pie.y - alto), Size(fw * 0.32f, alto * 0.23f))
        drawRect(a.brillo(0.5f, k), Offset(pie.x - fw * 0.14f, pie.y - alto), Size(0.8f * u, alto * 0.23f))
        drawLine(a.dia(P.LATON_OSC, 0f, k), Offset(pie.x + fw * 0.16f, pie.y - alto * 0.9f), Offset(pie.x + fw * 0.4f, pie.y - alto * 0.8f), 1.2f * u)
        val cordon = Path().apply { moveTo(pie.x - fw * 0.7f, pie.y - alto * 0.55f); cubicTo(pie.x - fw * 1.3f, pie.y - alto * 1.1f, pie.x + fw * 1.4f, pie.y - alto * 1.15f, pie.x + fw * 0.7f, pie.y - alto * 0.55f) }
        drawPath(cordon, a.dia(P.VERDE, 0.1f, k), style = Stroke(1.4f * u))
        if (a.oscuro) drawPath(cuerpo, a.filo(0.6f * k), style = Stroke(1.2f * u))
    }

    /** La caja de las pistolas, cerrada, con su placa de latón, el frasco de pólvora encima y la baqueta apoyada. */
    private fun DrawScope.pistolas(pie: Offset, r: Rect, a: AireNapoleon, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val hh = r.height
        val caja = Rect(pie.x, pie.y - hh * 0.3f, pie.x + w * 0.2f, pie.y)
        val k = cerca(caja.center)
        sombraDetras(Path().apply { addRect(caja) }, a, 1f)
        contactoNapoleon(Offset(caja.center.x, pie.y), caja.width * 1.05f, hh * 0.04f, a, 0.45f)
        drawRect(a.dia(P.CAOBA, 0f, k), caja.topLeft, caja.size)
        val junta = caja.top + caja.height * 0.32f
        drawRect(a.dia(P.CAOBA_LUZ, -0.05f, k), caja.topLeft, Size(caja.width, w * 0.008f))
        drawRect(a.sombra(0.55f), Offset(caja.left, junta), Size(caja.width, 1.2f * u))
        drawRect(a.claro(0.3f), Offset(caja.left, junta + 1.2f * u), Size(caja.width, 0.7f * u))
        val veta = Path()
        for (j in 0 until 6) { val y = junta + (caja.bottom - junta) * (0.15f + 0.14f * j); veta.moveTo(caja.left, y); veta.quadraticTo(caja.center.x, y + (j % 2 - 0.5f) * 3f * u, caja.right, y) }
        drawPath(veta, a.dia(P.CAOBA_OSC, 0f, k).copy(alpha = 0.4f), style = Stroke(0.7f * u))
        // La placa de latón ovalada (lisa) en la tapa y el escudo de la cerradura.
        ovaloNapoleon(a.dia(P.LATON, 0f, k), Rect(caja.center.x - w * 0.025f, caja.top + w * 0.009f, caja.center.x + w * 0.025f, junta - w * 0.004f))
        ovaloNapoleon(a.brillo(0.5f, k), Rect(caja.center.x - w * 0.018f, caja.top + w * 0.01f, caja.center.x, caja.top + w * 0.014f))
        drawCircle(a.dia(P.LATON, 0f, k), w * 0.007f, Offset(caja.center.x, junta + w * 0.016f))
        drawCircle(a.dia(P.CAOBA_HONDO, 0f, k), w * 0.002f, Offset(caja.center.x, junta + w * 0.015f))
        with(ObraEstanteNapoleon) {
            cantonera(Offset(caja.left, caja.top), w * 0.018f, w * 0.005f, 1f, 1f, a, k)
            cantonera(Offset(caja.right, caja.top), w * 0.018f, w * 0.005f, -1f, 1f, a, k)
        }
        if (a.oscuro) drawRect(a.filo(0.5f * k), caja.topLeft, Size(caja.width, 1.4f * u))
        // El frasco de pólvora de pie encima de la caja, y la baqueta apoyada en el paño.
        val bq0 = Offset(caja.right + w * 0.006f, pie.y); val bq1 = Offset(caja.right - w * 0.03f, pie.y - hh * 0.84f)
        drawLine(a.sombra(0.3f), bq0 + Offset(w * 0.008f, 0f), bq1 + Offset(w * 0.008f, w * 0.004f), w * 0.006f)
        drawLine(a.dia(Color(0xFF6A4A2A), 0f, k), bq0, bq1, w * 0.005f, StrokeCap.Round)
        drawCircle(a.dia(P.LATON, 0f, k), w * 0.004f, bq1)
        frasco(Offset(caja.left + caja.width * 0.42f, caja.top), hh * 0.5f, a, cerca(Offset(caja.center.x, caja.top - hh * 0.2f)))
    }
}
