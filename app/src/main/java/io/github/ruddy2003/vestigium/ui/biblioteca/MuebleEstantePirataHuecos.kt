package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
 * Lo que hay en los huecos de las repisas del estante de Pirata ([MuebleEstantePirata]). En cada hueco con sitio, una luz: la
 * palmatoria de latón con su vela o el candelero alto (de día, apagados; de noche, encendidos, y alumbran a sus vecinos, el
 * fondo de minio y el canto de la tabla de arriba: ver [MuebleEstantePirata.xLuzHueco]). Si cabe algo más, a su lado y grande
 * (de siete a nueve décimos del hueco), según la repisa: el legajo de cartas atado con su lacre, la bolsa de cuero con su
 * jareta y monedas lisas, la caja de tabaco con la pipa de barro apoyada, la jarra del grog de barro vidriado o los rollos de
 * cartas de pie atados con cordel.
 */
internal object HuecosEstantePirata {
    private val P = PaletaPirata

    /** Lo que se deja en el hueco libre de la repisa [k], de [oc] (hasta donde llegan los volúmenes) a [xDer] (el montante). */
    fun DrawScope.hueco(k: Int, r: Rect, oc: Float, xDer: Float, a: AirePirata, cerca: (Offset) -> Float) {
        val w = size.width
        val xl = MuebleEstantePirata.xLuzHueco(oc, xDer, w) ?: return
        val x1 = xDer - w * 0.035f
        val h = r.height; val piso = r.bottom
        // La luz le llega a lo que acompaña por la izquierda: de día, de la ventana; de noche, de la vela de su hueco.
        val ha = -1f
        clipRect(oc, r.top, xDer, r.bottom) {
            // Lo que acompaña, detrás de la luz y a su derecha (uno o, si el hueco es ancho, dos, distintos según la repisa).
            val resto = x1 - (xl + w * 0.07f)
            val cuantos = if (resto >= w * 0.36f) 2 else if (resto >= w * 0.09f) 1 else 0
            for (j in 0 until cuantos) {
                val c = xl + w * 0.07f + resto * (if (cuantos == 1) 0.5f else if (j == 0) 0.3f else 0.76f)
                val kc = cerca(Offset(c, r.center.y))
                when (Math.floorMod(k * 3 + 2 + j * 2, 5)) {
                    0 -> legajo(Offset(c, piso), h, a, kc, ha)
                    1 -> bolsa(Offset(c, piso), h, a, kc, ha)
                    2 -> tabaco(Offset(c, piso), h, a, kc, ha)
                    3 -> jarra(Offset(c, piso), h, a, kc, ha)
                    else -> rollos(Offset(c, piso), h, a, kc, ha)
                }
            }
            val kl = if (a.lumbre) 1f else cerca(Offset(xl, r.center.y))
            if (k % 2 == 0) palmatoria(Offset(xl, piso), h, a, kl) else candelero(Offset(xl, piso), h, a, kl)
        }
    }

    /** La llama de la vela del hueco de la repisa [k] y su resplandor (de noche; va encima de la luz del cuerpo). */
    fun DrawScope.llamaHueco(k: Int, r: Rect, d: DisposicionEstante, a: AirePirata) {
        if (!a.lumbre) return
        val w = size.width; val u = density
        val oc = d.ocupado.getOrNull(k) ?: return
        val xl = MuebleEstantePirata.xLuzHueco(oc, d.derecha.left, w) ?: return
        val f = if (a.oscuro) 1f else 0.6f
        val l = Offset(xl, r.bottom - r.height * MuebleEstantePirata.VELA_Y)
        clipRect(d.izquierda.right, r.top - r.height * 0.15f, d.derecha.left, r.bottom) {
            mancha(l + Offset(0f, r.height * 0.06f), w * 0.2f, Color(0xFFE07E2A).copy(alpha = 0.26f * f))
            mancha(l, w * 0.06f, P.FUEGO.copy(alpha = 0.5f * f))
        }
        val s = r.height / 300f / u * 2.625f
        val llama = Path().apply {
            moveTo(l.x, l.y - 13f * u * s)
            quadraticTo(l.x + 4.6f * u * s, l.y - 2f * u * s, l.x, l.y + 4f * u * s)
            quadraticTo(l.x - 4.6f * u * s, l.y - 2f * u * s, l.x, l.y - 13f * u * s)
            close()
        }
        drawPath(llama, P.LLAMA)
        drawOval(P.FUEGO, Offset(l.x - 1.6f * u * s, l.y - 6.5f * u * s), Size(3.2f * u * s, 8f * u * s))
    }

    /** La vela de sebo, de [abajo] a [arriba] en [x], con su goterón y su pabilo (la llama la pone [llamaHueco]). */
    private fun DrawScope.velaDeSebo(x: Float, arriba: Float, abajo: Float, g: Float, a: AirePirata, k: Float) {
        val u = density
        val cera = Color(0xFFEFE3C2)
        drawRect(a.dia(cera, 0f, k), Offset(x - g / 2, arriba), Size(g, abajo - arriba))
        drawRect(a.sombra(0.22f), Offset(x + g * 0.12f, arriba), Size(g * 0.38f, abajo - arriba))
        drawRect(a.brillo(0.35f, k), Offset(x - g * 0.32f, arriba), Size(g * 0.14f, abajo - arriba))
        drawLine(a.dia(cera, 0.1f, k), Offset(x - g * 0.42f, arriba + 2f * u), Offset(x - g * 0.42f, arriba + (abajo - arriba) * 0.45f), 1.8f * u, StrokeCap.Round)
        drawLine(a.luz(P.TINTA, k), Offset(x, arriba), Offset(x + 0.6f * u, arriba - 3f * u), 1.1f * u)
    }

    /** La palmatoria de latón: el platillo con su asa de anillo y su vela (la llama arde a la altura [MuebleEstantePirata.VELA_Y]). */
    private fun DrawScope.palmatoria(pie: Offset, h: Float, a: AirePirata, k: Float) {
        val u = density
        val r = h * 0.2f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), r * 2.5f, 6f * u, a)
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r, pie.y - r * 0.34f), Size(r * 2, r * 0.38f))
        drawOval(a.luz(P.LATON, k), Offset(pie.x - r, pie.y - r * 0.46f), Size(r * 2, r * 0.36f))
        drawOval(a.brillo(0.5f, k), Offset(pie.x - r * 0.7f, pie.y - r * 0.42f), Size(r * 0.7f, r * 0.12f))
        drawArc(a.luz(P.LATON_OSC, k), 200f, 300f, false, Offset(pie.x + r * 0.82f, pie.y - r * 0.62f), Size(r * 0.6f, r * 0.55f), style = Stroke(2.2f * u))
        drawRect(a.luz(P.LATON, k), Offset(pie.x - r * 0.26f, pie.y - r * 0.85f), Size(r * 0.52f, r * 0.5f))
        drawRect(a.sombra(0.3f), Offset(pie.x + r * 0.08f, pie.y - r * 0.85f), Size(r * 0.18f, r * 0.5f))
        velaDeSebo(pie.x, pie.y - h * MuebleEstantePirata.VELA_Y + h * 0.035f, pie.y - r * 0.82f, r * 0.4f, a, k)
        destelloPirata(Offset(pie.x - r * 0.55f, pie.y - r * 0.34f), 1.4f * u, a, k)
    }

    /** El candelero alto de latón torneado: el pie ancho, el fuste con sus anillos, el mechero y su vela corta. */
    private fun DrawScope.candelero(pie: Offset, h: Float, a: AirePirata, k: Float) {
        val u = density
        val r = h * 0.17f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), r * 2.6f, 6f * u, a)
        val boca = pie.y - h * MuebleEstantePirata.VELA_Y + h * 0.13f
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r, pie.y - r * 0.34f), Size(r * 2, r * 0.4f))
        drawPath(poligonoPirata(pie.x - r * 0.95f, pie.y - r * 0.16f, pie.x - r * 0.3f, pie.y - r * 0.6f, pie.x + r * 0.3f, pie.y - r * 0.6f, pie.x + r * 0.95f, pie.y - r * 0.16f), a.luz(P.LATON, k))
        // El fuste, por franjas, con tres anillos.
        val fuste = Rect(pie.x - r * 0.16f, boca, pie.x + r * 0.16f, pie.y - r * 0.55f)
        drawRect(a.luz(P.LATON, k), fuste.topLeft, fuste.size)
        drawRect(a.brillo(0.5f, k), Offset(fuste.left + fuste.width * 0.15f, fuste.top), Size(fuste.width * 0.25f, fuste.height))
        drawRect(a.sombra(0.3f), Offset(fuste.right - fuste.width * 0.35f, fuste.top), Size(fuste.width * 0.35f, fuste.height))
        listOf(0.2f, 0.55f, 0.85f).forEach { f ->
            val y = fuste.top + fuste.height * f
            drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r * 0.36f, y - r * 0.1f), Size(r * 0.72f, r * 0.22f))
            drawOval(a.luz(P.LATON, k), Offset(pie.x - r * 0.32f, y - r * 0.13f), Size(r * 0.64f, r * 0.16f))
        }
        // El platillo del mechero y la vela.
        drawOval(a.luz(P.LATON_OSC, k), Offset(pie.x - r * 0.5f, boca - r * 0.1f), Size(r, r * 0.24f))
        drawOval(a.luz(P.LATON, k), Offset(pie.x - r * 0.5f, boca - r * 0.16f), Size(r, r * 0.2f))
        velaDeSebo(pie.x, pie.y - h * MuebleEstantePirata.VELA_Y + h * 0.035f, boca - r * 0.08f, r * 0.42f, a, k)
        destelloPirata(Offset(pie.x - r * 0.08f, fuste.top + fuste.height * 0.4f), 1.4f * u, a, k)
    }

    /** El legajo de cartas dobladas, alto y de canto contra el fondo, atado con su cinta (una sola vuelta) y su lacre. */
    private fun DrawScope.legajo(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.5f; val al = h * 0.74f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.15f, 6f * u, a, hacia = -ha)
        for (j in 0 until 5) {
            val dx = (j - 2f) * an * 0.045f; val dy = j * al * 0.03f
            val hoja = Rect(pie.x - an / 2 + dx, pie.y - al + dy, pie.x + an / 2 + dx, pie.y)
            drawRect(a.dia(mezcla(P.PAPEL, P.PAPEL_OSC, 0.12f + 0.22f * azar(551, j)), 0f, k), hoja.topLeft, hoja.size)
            drawRect(a.sombra(0.25f), hoja.topLeft, hoja.size, style = Stroke(0.7f * u))
        }
        val f = Rect(pie.x - an / 2 + an * 0.09f, pie.y - al + al * 0.12f, pie.x + an / 2 + an * 0.09f, pie.y)
        drawRect(a.sombra(0.2f), Offset(f.left, f.top), Size(f.width * 0.25f, f.height))
        drawLine(a.sombra(0.25f), Offset(f.left, f.top + f.height * 0.36f), Offset(f.right, f.top + f.height * 0.36f), 0.9f * u)
        for (j in 0 until 4) drawLine(a.luz(P.TINTA, k).copy(alpha = 0.2f), Offset(f.left + f.width * 0.15f, f.top + f.height * (0.5f + 0.09f * j)), Offset(f.left + f.width * (0.45f + 0.35f * azar(552, j)), f.top + f.height * (0.5f + 0.09f * j)), 0.8f * u)
        // La cinta, una vuelta a lo ancho, y el lacre algo corrido del centro.
        val yc = f.top + f.height * 0.62f
        drawLine(a.luz(Color(0xFF2E4A6A), k), Offset(f.left - an * 0.12f, yc), Offset(f.right, yc), 2.6f * u)
        val sello = Offset(f.left + f.width * 0.64f, yc)
        drawCircle(a.sombra(0.4f), h * 0.055f, sello + Offset(1f * u, 1.2f * u))
        drawCircle(a.luz(P.LACRE, k), h * 0.055f, sello)
        drawCircle(a.luz(mezcla(P.LACRE, P.TINTA, 0.4f), k), h * 0.034f, sello, style = Stroke(0.9f * u))
        drawCircle(a.brillo(0.45f, k), h * 0.014f, sello - Offset(h * 0.018f, h * 0.018f))
    }

    /** La bolsa de cuero grande con su jareta, panzuda y apoyada, y unas monedas lisas que se salieron (sin cuño). */
    private fun DrawScope.bolsa(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.58f; val al = h * 0.7f
        val x = pie.x - an * 0.15f
        contactoPirata(Offset(x, pie.y - 1f * u), an * 1.3f, 7f * u, a, hacia = -ha)
        val cuero = Color(0xFF8A5A30)
        val cuello = pie.y - al * 0.7f
        val panza = Path().apply {
            moveTo(x - an * 0.14f, cuello)
            cubicTo(x - an * 0.62f, cuello + al * 0.12f, x - an * 0.64f, pie.y, x - an * 0.22f, pie.y)
            lineTo(x + an * 0.22f, pie.y)
            cubicTo(x + an * 0.64f, pie.y, x + an * 0.62f, cuello + al * 0.12f, x + an * 0.14f, cuello)
            close()
        }
        val frunce = Path().apply {
            moveTo(x - an * 0.14f, cuello)
            quadraticTo(x - an * 0.34f, pie.y - al * 0.92f, x - an * 0.22f, pie.y - al)
            quadraticTo(x - an * 0.05f, pie.y - al * 0.9f, x + an * 0.06f, pie.y - al * 1.02f)
            quadraticTo(x + an * 0.24f, pie.y - al * 0.92f, x + an * 0.28f, pie.y - al * 0.97f)
            quadraticTo(x + an * 0.28f, pie.y - al * 0.84f, x + an * 0.14f, cuello)
            close()
        }
        drawPath(frunce, a.luz(mezcla(cuero, P.TINTA, 0.15f), k))
        drawLine(a.sombra(0.4f), Offset(x - an * 0.07f, cuello), Offset(x - an * 0.13f, pie.y - al * 0.95f), 1.2f * u)
        drawLine(a.sombra(0.4f), Offset(x + an * 0.07f, cuello), Offset(x + an * 0.15f, pie.y - al * 0.95f), 1.2f * u)
        drawPath(panza, a.luz(cuero, k))
        clipPath(panza) {
            drawOval(a.brillo(0.3f, k), Offset(x - an * 0.46f, pie.y - al * 0.58f), Size(an * 0.36f, al * 0.34f))
            drawOval(a.sombra(0.34f), Offset(x + an * 0.06f, pie.y - al * 0.72f), Size(an * 0.62f, al * 0.85f))
            drawLine(a.sombra(0.3f), Offset(x - an * 0.1f, cuello), Offset(x - an * 0.28f, pie.y - al * 0.25f), 1.4f * u)
            drawLine(a.sombra(0.3f), Offset(x + an * 0.06f, cuello), Offset(x + an * 0.2f, pie.y - al * 0.3f), 1.4f * u)
            drawLine(a.luz(P.CABO, k).copy(alpha = 0.6f), Offset(x - an * 0.48f, pie.y - al * 0.2f), Offset(x + an * 0.46f, pie.y - al * 0.2f), 0.9f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2.2f * u)))
        }
        drawLine(a.luz(P.CABO_OSC, k), Offset(x - an * 0.2f, cuello), Offset(x + an * 0.2f, cuello), 2.6f * u, StrokeCap.Round)
        drawLine(a.luz(P.CABO, k), Offset(x - an * 0.18f, cuello - 0.6f * u), Offset(x + an * 0.18f, cuello - 0.6f * u), 1.3f * u, StrokeCap.Round)
        val nudo = Offset(x + an * 0.13f, cuello)
        val cabos = Path().apply {
            moveTo(nudo.x, nudo.y); quadraticTo(nudo.x + an * 0.2f, nudo.y + al * 0.08f, nudo.x + an * 0.22f, nudo.y + al * 0.3f)
            moveTo(nudo.x, nudo.y); quadraticTo(nudo.x + an * 0.06f, nudo.y + al * 0.12f, nudo.x + an * 0.02f, nudo.y + al * 0.2f)
        }
        drawPath(cabos, a.luz(P.CABO, k), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawCircle(a.luz(P.CABO_OSC, k), 2.2f * u, nudo)
        listOf(Offset(an * 0.62f, 0f) to 0.12f, Offset(an * 0.88f, 0f) to 0.1f, Offset(an * 0.74f, -h * 0.035f) to 0.11f).forEach { (dd, sz) ->
            val c = Offset(x, pie.y) + dd - Offset(0f, h * 0.02f)
            val rr = h * sz * 0.5f
            drawOval(a.sombra(0.3f), Offset(c.x - rr + 1f * u, c.y - rr * 0.3f + 1f * u), Size(rr * 2, rr * 0.7f))
            drawOval(a.luz(Color(0xFF9A7A2A), k), Offset(c.x - rr, c.y - rr * 0.35f), Size(rr * 2, rr * 0.7f))
            drawOval(a.luz(Color(0xFFE0BC50), k), Offset(c.x - rr, c.y - rr * 0.45f), Size(rr * 2, rr * 0.66f))
            destelloPirata(Offset(c.x - rr * 0.4f, c.y - rr * 0.2f), 1.1f * u, a, k)
        }
    }

    /** La caja de tabaco de madera con su tapa corrediza y la pipa de barro blanco, larga, tumbada encima. */
    private fun DrawScope.tabaco(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.56f; val al = h * 0.34f
        val caja = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
        // La pipa, tumbada sobre la tapa: la cazoleta apoyada en ella y la caña que sale ladeada por encima del canto.
        val barro = Color(0xFFE8E0D0)
        val cz = Offset(caja.left + an * 0.2f, caja.top - h * 0.012f)
        val tope = Offset(caja.right + an * 0.22f, caja.top - h * 0.1f)
        drawLine(a.sombra(0.3f), cz + Offset(2f * u, 2.5f * u), tope + Offset(3f * u, 2f * u), h * 0.028f, StrokeCap.Round)
        drawLine(a.dia(barro, 0f, k), cz, tope, h * 0.024f, StrokeCap.Round)
        drawLine(a.brillo(0.4f, k), cz + Offset(-0.8f * u, -0.6f * u), tope + Offset(-0.8f * u, -0.6f * u), h * 0.007f)
        val cazoleta = poligonoPirata(cz.x - h * 0.045f, cz.y - h * 0.12f, cz.x + h * 0.045f, cz.y - h * 0.12f, cz.x + h * 0.03f, cz.y + h * 0.015f, cz.x - h * 0.025f, cz.y + h * 0.015f)
        drawPath(cazoleta, a.dia(barro, 0f, k))
        drawRect(a.sombra(0.26f), Offset(cz.x + h * 0.008f, cz.y - h * 0.12f), Size(h * 0.035f, h * 0.135f))
        drawOval(a.luz(Color(0xFF3A2A20), k), Offset(cz.x - h * 0.042f, cz.y - h * 0.13f), Size(h * 0.084f, h * 0.026f))
        // La caja.
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.15f, 6f * u, a, hacia = -ha)
        val madera = Color(0xFF7A4E2C)
        drawRect(a.luz(madera, k), caja.topLeft, caja.size)
        drawRect(a.dia(mezcla(madera, P.ROBLE_LUZ, 0.4f), 0f, k), caja.topLeft, Size(caja.width, al * 0.16f))
        drawRect(a.sombra(0.3f), Offset(caja.right - an * 0.2f, caja.top), Size(an * 0.2f, al))
        val veta = Path()
        for (j in 0 until 3) { val y = caja.top + al * (0.35f + 0.22f * j); veta.moveTo(caja.left, y); veta.quadraticTo(caja.center.x, y + 1.5f * u, caja.right, y - 0.8f * u) }
        drawPath(veta, a.sombra(0.2f), style = Stroke(0.8f * u))
        drawRect(a.luz(P.LATON, k), Offset(caja.center.x - an * 0.06f, caja.top + al * 0.3f), Size(an * 0.12f, al * 0.18f))
        drawRect(a.sombra(0.35f), caja.topLeft, caja.size, style = Stroke(0.8f * u))
    }

    /** La jarra del grog, de barro con la mitad de arriba vidriada de verde, su asa y la boca con su pico. */
    private fun DrawScope.jarra(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        val an = h * 0.46f; val al = h * 0.74f
        contactoPirata(Offset(pie.x, pie.y - 1f * u), an * 1.3f, 7f * u, a, hacia = -ha)
        val top = pie.y - al
        // El asa, detrás a la derecha.
        drawArc(a.luz(P.BARRO, k), -80f, 170f, false, Offset(pie.x + an * 0.22f, top + al * 0.14f), Size(an * 0.5f, al * 0.46f), style = Stroke(h * 0.04f, cap = StrokeCap.Round))
        val cuerpo = Path().apply {
            moveTo(pie.x - an * 0.28f, top)
            lineTo(pie.x + an * 0.3f, top)
            cubicTo(pie.x + an * 0.3f, top + al * 0.2f, pie.x + an * 0.56f, top + al * 0.42f, pie.x + an * 0.48f, top + al * 0.78f)
            quadraticTo(pie.x + an * 0.42f, pie.y, pie.x + an * 0.26f, pie.y)
            lineTo(pie.x - an * 0.26f, pie.y)
            quadraticTo(pie.x - an * 0.42f, pie.y, pie.x - an * 0.48f, top + al * 0.78f)
            cubicTo(pie.x - an * 0.56f, top + al * 0.42f, pie.x - an * 0.3f, top + al * 0.2f, pie.x - an * 0.28f, top)
            close()
        }
        drawPath(cuerpo, a.luz(P.BARRO, k))
        clipPath(cuerpo) {
            // El vidriado verde de arriba, con su chorrera.
            val vid = Path().apply {
                moveTo(pie.x - an, top); lineTo(pie.x + an, top); lineTo(pie.x + an, top + al * 0.5f)
                quadraticTo(pie.x + an * 0.2f, top + al * 0.56f, pie.x + an * 0.05f, top + al * 0.68f)
                quadraticTo(pie.x - an * 0.05f, top + al * 0.54f, pie.x - an, top + al * 0.48f); close()
            }
            drawPath(vid, a.luz(P.VIDRIADO, k))
            drawRect(a.brillo(0.34f, k), Offset(pie.x - an * 0.38f, top), Size(an * 0.18f, al))
            drawRect(a.brillo(0.5f, k), Offset(pie.x - an * 0.3f, top + al * 0.15f), Size(an * 0.05f, al * 0.35f))
            drawRect(a.sombra(0.34f), Offset(pie.x + an * 0.12f, top), Size(an * 0.5f, al))
            for (j in 0 until 4) drawLine(a.sombra(0.16f), Offset(pie.x - an, top + al * (0.6f + 0.1f * j)), Offset(pie.x + an, top + al * (0.6f + 0.1f * j)), 0.8f * u)
        }
        drawOval(a.luz(mezcla(P.VIDRIADO, P.VERDE_HONDO, 0.5f), k), Offset(pie.x - an * 0.3f, top - al * 0.035f), Size(an * 0.6f, al * 0.07f))
        drawOval(a.luz(Color(0xFF1E140C), k), Offset(pie.x - an * 0.22f, top - al * 0.02f), Size(an * 0.44f, al * 0.04f))
        drawPath(poligonoPirata(pie.x - an * 0.28f, top, pie.x - an * 0.42f, top - al * 0.035f, pie.x - an * 0.24f, top + al * 0.03f), a.luz(P.VIDRIADO, k))
        destelloPirata(Offset(pie.x - an * 0.26f, top + al * 0.28f), 1.6f * u, a, k)
    }

    /** Los rollos de cartas de pie, apoyados unos en otros y atados con un cordel, con sus cantos en espiral. */
    private fun DrawScope.rollos(pie: Offset, h: Float, a: AirePirata, k: Float, ha: Float) {
        val u = density
        contactoPirata(Offset(pie.x, pie.y - 1f * u), h * 0.5f, 6f * u, a, hacia = -ha)
        listOf(Triple(-0.12f, 0.88f, 6f), Triple(0.02f, 0.8f, -4f), Triple(0.14f, 0.74f, 8f)).forEachIndexed { j, (dx, al, giro) ->
            val x = pie.x + dx * h
            val g = h * (0.13f - 0.01f * j)
            rotate(giro, Offset(x, pie.y)) {
                val r = Rect(x - g / 2, pie.y - h * al, x + g / 2, pie.y)
                val papel = mezcla(P.PAPEL, P.PAPEL_OSC, 0.15f + 0.2f * j)
                drawRect(a.dia(papel, 0f, k), r.topLeft, r.size)
                drawRect(a.brillo(0.3f, k), Offset(r.left + g * 0.15f, r.top), Size(g * 0.2f, r.height))
                drawRect(a.sombra(0.28f), Offset(r.right - g * 0.35f, r.top), Size(g * 0.35f, r.height))
                drawOval(a.dia(mezcla(papel, P.PAPEL_OSC, 0.4f), 0f, k), Offset(r.left, r.top - g * 0.2f), Size(g, g * 0.4f))
                drawArc(a.sombra(0.45f), 200f, 260f, false, Offset(x - g * 0.28f, r.top - g * 0.1f), Size(g * 0.56f, g * 0.2f), style = Stroke(0.7f * u))
                drawLine(a.luz(P.TINTA, k).copy(alpha = 0.3f), Offset(r.left + g * 0.3f, r.top + r.height * 0.2f), Offset(r.left + g * 0.6f, r.top + r.height * 0.32f), 0.6f * u)
            }
        }
        // El cordel que los ata, con su lazo caído a un lado.
        val y = pie.y - h * 0.4f
        drawLine(a.luz(P.CABO_OSC, k), Offset(pie.x - h * 0.2f, y + 1.4f * u), Offset(pie.x + h * 0.22f, y - 1.4f * u), 2.2f * u, StrokeCap.Round)
        drawLine(a.luz(P.CABO, k), Offset(pie.x - h * 0.2f, y), Offset(pie.x + h * 0.22f, y - 2.6f * u), 1.2f * u, StrokeCap.Round)
        val cabo = Path().apply { moveTo(pie.x + h * 0.2f, y - 2f * u); quadraticTo(pie.x + h * 0.28f, y + h * 0.06f, pie.x + h * 0.24f, y + h * 0.16f) }
        drawPath(cabo, a.luz(P.CABO, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
    }
}
