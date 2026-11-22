package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.MARGA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CEBOLLA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FAYENZA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.GRANO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAN
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PIEDRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.VERDIN
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE

// Lo que hay en el almacén de la Tumba (MuebleEstanteEgipto): lo que el escriba pesa, cuenta y reparte.

/** La sombra que algo deja en lo que pisa, corrida hacia el lado contrario a la luz. */
private fun DrawScope.contactoEgipto(c: Offset, ancho: Float, alto: Float, a: AireEgipto, fuerza: Float = 0.36f) =
    drawOval(a.sombra(fuerza), Offset(c.x - ancho * 0.42f, c.y - alto / 2), Size(ancho, alto))

/** El bulto de una forma redonda: el claro del lado de la luz y la sombra del otro, dentro de su silueta. */
private fun DrawScope.redondoEgipto(forma: Path, caja: Rect, a: AireEgipto, k: Float, hacia: Float) {
    val luzIzq = !a.oscuro || hacia < 0f
    clipPath(forma) {
        val xc = if (luzIzq) caja.left + caja.width * 0.12f else caja.left + caja.width * 0.62f
        drawOval(a.claro(0.28f), Offset(xc, caja.top + caja.height * 0.1f), Size(caja.width * 0.26f, caja.height * 0.5f))
        val xs = if (luzIzq) caja.left + caja.width * 0.6f else caja.left - caja.width * 0.1f
        drawOval(a.sombra(0.3f), Offset(xs, caja.top - caja.height * 0.1f), Size(caja.width * 0.6f, caja.height * 1.3f))
        drawRect(a.sombra(0.2f), Offset(caja.left, caja.bottom - caja.height * 0.2f), Size(caja.width, caja.height * 0.2f))
        if (a.oscuro && k > 0.1f) {
            val x = if (hacia > 0) caja.right - caja.width * 0.12f else caja.left
            drawRect(a.filo(0.45f * k), Offset(x, caja.top), Size(caja.width * 0.12f, caja.height))
        }
    }
}

/** Una estaca de madera hincada en la junta del adobe, con su sombra. */
private fun DrawScope.estacaEgipto(c: Offset, a: AireEgipto, k: Float) {
    val u = density
    drawCircle(a.sombra(0.4f), 3.2f * u, c + Offset(1.6f * u, 1.6f * u))
    drawCircle(a.luz(ACACIA_OSC, k), 3f * u, c)
    drawCircle(a.luz(ACACIA, k), 2f * u, c - Offset(0.5f * u, 0.5f * u))
}

/** La ristra de cebollas de la ración, colgada por sus tallos trenzados de una estaca junto al borde, cortada por él. */
internal fun DrawScope.cebollasEgipto(clavo: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val trenza = Path().apply { moveTo(clavo.x, clavo.y); quadraticTo(clavo.x - 4f * u, clavo.y + w * 0.06f, clavo.x - 2f * u, clavo.y + w * 0.1f) }
    drawPath(trenza, a.luz(mezcla(PAPIRO, FIBRA, 0.5f), k, f), style = Stroke(3.2f * u, cap = StrokeCap.Round))
    drawPath(trenza, a.sombra(0.3f), style = Stroke(0.8f * u))
    val centros = listOf(-0.022f to 0.105f, 0.012f to 0.112f, -0.034f to 0.14f, 0.0f to 0.145f, 0.03f to 0.142f, -0.016f to 0.175f, 0.016f to 0.178f)
    centros.forEachIndexed { j, (dx, dy) ->
        val c = Offset(clavo.x + w * dx, clavo.y + w * dy)
        val r = w * (0.021f + 0.005f * azar(j, 71))
        val forma = Path().apply {
            moveTo(c.x, c.y - r * 1.5f)
            quadraticTo(c.x + r * 1.25f, c.y - r * 0.6f, c.x + r * 0.95f, c.y + r * 0.45f)
            quadraticTo(c.x, c.y + r * 1.25f, c.x - r * 0.95f, c.y + r * 0.45f)
            quadraticTo(c.x - r * 1.25f, c.y - r * 0.6f, c.x, c.y - r * 1.5f)
            close()
        }
        drawPath(corridoEgipto(forma, 1.6f * u, 1.8f * u), a.sombra(0.3f))
        drawPath(forma, a.luz(mezcla(CEBOLLA, PAPIRO, 0.25f * azar(j, 72)), k, f))
        drawPath(Path().apply {
            moveTo(c.x - r * 0.5f, c.y - r * 0.9f); quadraticTo(c.x - r * 0.8f, c.y, c.x - r * 0.4f, c.y + r * 0.8f)
            moveTo(c.x + r * 0.3f, c.y - r * 1f); quadraticTo(c.x + r * 0.6f, c.y, c.x + r * 0.35f, c.y + r * 0.85f)
        }, a.sombra(0.25f), style = Stroke(0.6f * u))
        redondoEgipto(forma, Rect(c.x - r, c.y - r * 1.5f, c.x + r, c.y + r * 1.25f), a, k, -1f)
    }
    estacaEgipto(clavo, a, k)
}

/** La estera de junco enrollada y atada, de pie y apoyada en el muro: los juncos a lo largo, dos ataduras y la espiral arriba. */
internal fun DrawScope.esteraEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val r = w * 0.036f; val alto = w * 0.28f
    contactoEgipto(Offset(pie.x + w * 0.012f, pie.y), r * 3f, 5f * u, a)
    rotate(-5f, pie) {
        val cuerpo = Rect(pie.x - r, pie.y - alto, pie.x + r, pie.y)
        drawRect(a.sombra(0.25f), Offset(cuerpo.right, cuerpo.top + 6f * u), Size(5f * u, alto - 6f * u))
        drawRect(a.luz(mezcla(FIBRA, PAPIRO, 0.3f), k, f), cuerpo.topLeft, cuerpo.size)
        val juncos = Path()
        var x = cuerpo.left + 1.6f * u
        while (x < cuerpo.right) { juncos.moveTo(x, cuerpo.top + 2f * u); juncos.lineTo(x + 0.4f * u, cuerpo.bottom); x += 2.2f * u }
        drawPath(juncos, a.luz(PALMA, k, f).copy(alpha = 0.45f), style = Stroke(0.6f * u))
        drawRect(a.claro(0.3f), Offset(cuerpo.left + r * 0.25f, cuerpo.top), Size(r * 0.35f, alto))
        drawRect(a.sombra(0.28f), Offset(cuerpo.right - r * 0.7f, cuerpo.top), Size(r * 0.7f, alto))
        listOf(0.25f, 0.72f).forEach { t ->
            val y = cuerpo.top + alto * t
            drawRect(a.luz(mezcla(ROJO, FIBRA, 0.35f), k, f), Offset(cuerpo.left - 0.5f * u, y), Size(r * 2 + 1f * u, 2.6f * u))
            drawRect(a.sombra(0.3f), Offset(cuerpo.left, y + 2.6f * u), Size(r * 2, 0.8f * u))
        }
        val boca = Rect(cuerpo.left, cuerpo.top - r * 0.4f, cuerpo.right, cuerpo.top + r * 0.4f)
        drawOval(a.luz(mezcla(FIBRA, PALMA_OSC, 0.25f), k, f), boca.topLeft, boca.size)
        for (j in 1..3) drawOval(a.luz(PAPIRO, k, f).copy(alpha = 0.6f), boca.deflate(r * 0.22f * j).topLeft, boca.deflate(r * 0.22f * j).size, style = Stroke(0.7f * u))
    }
}

/**
 * Una tinaja de marga crema, pintada con bandas de azul pálido y rojo como la loza de la época: una franja azul en el hombro
 * con su festón de pétalos colgando y filetes rojos; abajo, rayas ([motivo] 0) o un zigzag azul (1). La boca, tapada con
 * barro (0) o con un paño de lino atado (1).
 */
internal fun DrawScope.tinajaEgipto(pie: Offset, alto: Float, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float, motivo: Int) {
    val u = density
    val r = alto * 0.38f
    contactoEgipto(Offset(pie.x + r * 0.2f, pie.y), r * 2.4f, 6f * u, a, 0.4f)
    val cuerpo = Path().apply {
        moveTo(pie.x - r * 0.42f, pie.y)
        cubicTo(pie.x - r * 1.15f, pie.y - alto * 0.2f, pie.x - r * 1.1f, pie.y - alto * 0.72f, pie.x - r * 0.5f, pie.y - alto * 0.86f)
        lineTo(pie.x - r * 0.42f, pie.y - alto * 0.95f)
        lineTo(pie.x + r * 0.42f, pie.y - alto * 0.95f)
        lineTo(pie.x + r * 0.5f, pie.y - alto * 0.86f)
        cubicTo(pie.x + r * 1.1f, pie.y - alto * 0.72f, pie.x + r * 1.15f, pie.y - alto * 0.2f, pie.x + r * 0.42f, pie.y)
        close()
    }
    val caja = Rect(pie.x - r * 1.05f, pie.y - alto * 0.95f, pie.x + r * 1.05f, pie.y)
    drawPath(cuerpo, a.luz(MARGA, k, f))
    clipPath(cuerpo) {
        val azul = a.luz(EgiptoTinta.AZUL, k, f); val rojo = a.luz(ROJO, k, f)
        val y0 = pie.y - alto * 0.76f; val y1 = pie.y - alto * 0.64f
        drawRect(azul, Offset(caja.left, y0), Size(caja.width, y1 - y0))
        // El festón de pétalos que cuelga de la franja.
        var x = caja.left + r * 0.12f
        while (x < caja.right) { drawOval(azul, Offset(x - r * 0.1f, y1 - r * 0.1f), Size(r * 0.2f, r * 0.3f)); x += r * 0.25f }
        drawRect(rojo, Offset(caja.left, y0 - 1.8f * u), Size(caja.width, 1.3f * u))
        drawRect(rojo, Offset(caja.left, y1 + r * 0.24f), Size(caja.width, 1.1f * u))
        val y2 = pie.y - alto * 0.4f
        if (motivo == 0) {
            drawRect(rojo, Offset(caja.left, y2), Size(caja.width, 1.4f * u))
            drawRect(azul, Offset(caja.left, y2 + 3f * u), Size(caja.width, 3f * u))
            drawRect(rojo, Offset(caja.left, y2 + 7.5f * u), Size(caja.width, 1.1f * u))
        } else {
            val zig = Path()
            var zx = caja.left; var arribaZ = true
            zig.moveTo(zx, y2)
            while (zx < caja.right) { zx += r * 0.22f; arribaZ = !arribaZ; zig.lineTo(zx, if (arribaZ) y2 else y2 + r * 0.22f) }
            drawPath(zig, azul, style = Stroke(2f * u))
            drawRect(rojo, Offset(caja.left, y2 + r * 0.34f), Size(caja.width, 1.1f * u))
        }
        // La pintura gastada donde se roza.
        drawOval(a.luz(MARGA, k, f).copy(alpha = 0.5f), Offset(pie.x - r * 0.9f, y0 - r * 0.05f), Size(r * 0.5f, r * 0.3f))
    }
    redondoEgipto(cuerpo, caja, a, k, hacia)
    // El brillo de la marga alisada, del lado de la luz.
    if (!a.oscuro) drawArc(a.claro(0.4f), 160f, 60f, false, Offset(caja.left + r * 0.25f, caja.top + alto * 0.18f), Size(r * 1.2f, alto * 0.6f), style = Stroke(1.6f * u))
    // La boca.
    val boca = Rect(pie.x - r * 0.48f, pie.y - alto - r * 0.08f, pie.x + r * 0.48f, pie.y - alto * 0.93f)
    drawOval(a.luz(mezcla(MARGA, PALMA_OSC, 0.15f), k, f), boca.topLeft, boca.size)
    if (motivo == 0) {
        drawArc(a.luz(BARRO, k, f), 180f, 180f, true, Offset(boca.left + r * 0.04f, boca.top - r * 0.26f), Size(boca.width - r * 0.08f, r * 0.6f))
        drawArc(a.claro(0.35f), 200f, 60f, false, Offset(boca.left + r * 0.1f, boca.top - r * 0.2f), Size(boca.width * 0.7f, r * 0.45f), style = Stroke(1f * u))
    } else {
        val pano = Path().apply {
            moveTo(boca.left - r * 0.1f, boca.bottom + r * 0.12f)
            quadraticTo(pie.x, boca.top - r * 0.35f, boca.right + r * 0.1f, boca.bottom + r * 0.12f)
            lineTo(boca.right + r * 0.02f, boca.bottom + r * 0.36f); lineTo(boca.left + r * 0.04f, boca.bottom + r * 0.32f); close()
        }
        drawPath(pano, a.luz(LINO, k, f))
        drawLine(a.luz(FIBRA, k, f), Offset(boca.left - r * 0.04f, boca.bottom + r * 0.16f), Offset(boca.right + r * 0.04f, boca.bottom + r * 0.18f), 1.4f * u)
        drawPath(pano, a.sombra(0.25f), style = Stroke(0.7f * u))
    }
}

/**
 * La balanza del cobre, que manda sobre el anaquel: el pie de madera, el poste con su remate, el astil de madera un poco caído
 * hacia el platillo de los cinceles, los platillos de bronce con su brillo colgados de tres cordeles, la plomada del fiel y
 * las pesas de piedra al lado.
 */
internal fun DrawScope.balanzaEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val madera = a.luz(ACACIA, k, f); val maderaOsc = a.luz(ACACIA_OSC, k, f)
    contactoEgipto(Offset(pie.x + w * 0.012f, pie.y), w * 0.17f, 6f * u, a, 0.4f)
    val zocalo = Rect(pie.x - w * 0.06f, pie.y - w * 0.032f, pie.x + w * 0.06f, pie.y)
    drawRect(madera, zocalo.topLeft, zocalo.size)
    drawRect(a.claro(0.35f), zocalo.topLeft, Size(zocalo.width, 1.6f * u))
    drawRect(a.sombra(0.32f), Offset(zocalo.right - zocalo.width * 0.25f, zocalo.top), Size(zocalo.width * 0.25f, zocalo.height))
    drawLine(maderaOsc.copy(alpha = 0.4f), Offset(zocalo.left + 3f * u, zocalo.center.y), Offset(zocalo.right - 3f * u, zocalo.center.y + 1f * u), 0.7f * u)
    val alto = w * 0.34f
    val poste = Rect(pie.x - w * 0.013f, zocalo.top - alto, pie.x + w * 0.013f, zocalo.top)
    drawRect(madera, poste.topLeft, poste.size)
    drawRect(a.claro(0.35f), Offset(poste.left + 1.2f * u, poste.top), Size(2f * u, poste.height))
    drawRect(a.sombra(0.32f), Offset(poste.right - poste.width * 0.38f, poste.top), Size(poste.width * 0.38f, poste.height))
    if (a.oscuro && k > 0.1f) drawRect(a.filo(0.5f * k), Offset(poste.right - 1.2f * u, poste.top), Size(1.2f * u, poste.height))
    // El remate del poste, donde pivota el astil.
    drawRoundRect(maderaOsc, Offset(poste.left - w * 0.006f, poste.top - w * 0.02f), Size(poste.width + w * 0.012f, w * 0.026f), CornerRadius(2f * u))
    val eje = Offset(pie.x, poste.top - w * 0.007f)
    val brazo = w * 0.17f
    val caida = 0.07f
    val pIzq = Offset(eje.x - brazo, eje.y + brazo * caida)
    val pDer = Offset(eje.x + brazo, eje.y - brazo * caida)
    // El astil: una barra de madera con cuerpo, sus regatones de bronce y su sombra.
    val g = w * 0.008f
    val astil = poligonoEgipto(pIzq.x, pIzq.y - g, pDer.x, pDer.y - g, pDer.x, pDer.y + g, pIzq.x, pIzq.y + g)
    drawPath(corridoEgipto(astil, 1.6f * u, 2.4f * u), a.sombra(0.3f))
    drawPath(astil, madera)
    drawLine(a.claro(0.4f), Offset(pIzq.x, pIzq.y - g * 0.6f), Offset(pDer.x, pDer.y - g * 0.6f), 1f * u)
    drawLine(a.sombra(0.35f), Offset(pIzq.x, pIzq.y + g * 0.7f), Offset(pDer.x, pDer.y + g * 0.7f), 1f * u)
    listOf(pIzq, pDer).forEach { p -> drawCircle(a.luz(BRONCE, k, f), g * 1.3f, p); drawCircle(a.claro(0.5f), g * 0.5f, p - Offset(g * 0.4f, g * 0.4f)) }
    drawCircle(a.luz(BRONCE, k, f), g * 1.1f, eje)
    // La plomada del fiel.
    val plomo = Offset(eje.x + w * 0.02f, eje.y + w * 0.1f)
    drawLine(a.luz(LINO, k, f), Offset(eje.x + w * 0.02f, eje.y + g), plomo, 0.7f * u)
    drawPath(Path().apply { moveTo(plomo.x - 2.6f * u, plomo.y); lineTo(plomo.x + 2.6f * u, plomo.y); lineTo(plomo.x, plomo.y + 7f * u); close() }, a.luz(PIEDRA, k, f))
    fun platillo(cuelga: Offset, carga: Int) {
        val c = Offset(cuelga.x, cuelga.y + w * 0.13f)
        val r = w * 0.064f
        val cord = Path().apply {
            moveTo(cuelga.x, cuelga.y); lineTo(c.x - r * 0.96f, c.y)
            moveTo(cuelga.x, cuelga.y); lineTo(c.x + r * 0.15f, c.y - r * 0.12f)
            moveTo(cuelga.x, cuelga.y); lineTo(c.x + r * 0.96f, c.y)
        }
        drawPath(cord, a.luz(mezcla(LINO, FIBRA, 0.4f), k, f), style = Stroke(0.8f * u))
        if (carga == 0) {
            // Los cinceles de cobre que se pesan, con su filo claro.
            for (j in 0 until 4) {
                val y = c.y - 2f * u - j * 2.4f * u
                val x0 = c.x - r * 0.8f + j * 1.6f * u
                val x1 = x0 + r * 1.35f
                drawLine(a.sombra(0.3f), Offset(x0 + 1f * u, y + 1.2f * u), Offset(x1 + 1f * u, y + 1.2f * u), 2.8f * u)
                drawLine(a.luz(mezcla(COBRE, PALMA_OSC, 0.12f * j), k, f), Offset(x0, y), Offset(x1, y), 2.6f * u, cap = StrokeCap.Square)
                drawLine(a.luz(mezcla(COBRE, PAPIRO, 0.55f), k, f), Offset(x1 - 3f * u, y - 0.6f * u), Offset(x1 + 1f * u, y - 0.6f * u), 1.2f * u)
                drawCircle(a.luz(VERDIN, k, f), 1.1f * u, Offset(x0 + r * 0.4f * (j + 1) / 2f, y))
            }
        } else {
            listOf(-0.4f to 0.8f, 0.35f to 0.6f).forEach { (dx, s) ->
                val rr = r * 0.44f * s
                val p0 = Offset(c.x + r * dx, c.y - 1f * u)
                drawArc(a.luz(PIEDRA, k, f), 180f, 180f, true, Offset(p0.x - rr, p0.y - rr), Size(rr * 2, rr * 2))
                drawArc(a.claro(0.45f), 200f, 60f, false, Offset(p0.x - rr * 0.8f, p0.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(0.9f * u))
            }
        }
        val plato = Rect(c.x - r, c.y - r * 0.22f, c.x + r, c.y + r * 0.38f)
        drawArc(a.luz(BRONCE, k, f), 0f, 180f, true, plato.topLeft, plato.size)
        drawArc(a.sombra(0.32f), 0f, 80f, true, plato.topLeft, plato.size)
        drawOval(a.luz(mezcla(BRONCE, PAPIRO, 0.25f), k, f), Offset(plato.left, plato.top - r * 0.07f), Size(plato.width, r * 0.3f))
        drawOval(a.luz(BRONCE, k, f), Offset(plato.left, plato.top - r * 0.07f), Size(plato.width, r * 0.3f), style = Stroke(1f * u))
        // El brillo del bronce.
        drawArc(luzMuebleEgipto(a, 0.7f), 110f, 50f, false, Offset(plato.left + r * 0.1f, plato.top - r * 0.2f), Size(plato.width * 0.9f, r * 0.9f), style = Stroke(1.4f * u))
        if (a.oscuro && k > 0.1f) drawArc(a.filo(0.6f * k), -10f, 60f, false, plato.topLeft, plato.size, style = Stroke(1.2f * u))
    }
    platillo(pIzq, 0)
    platillo(pDer, 1)
    // Las pesas de piedra sobre el tablón, de mayor a menor.
    var x = pie.x + w * 0.075f
    listOf(1f, 0.72f, 0.5f).forEachIndexed { j, s ->
        val rr = w * 0.03f * s
        val p0 = Offset(x + rr, pie.y)
        contactoEgipto(Offset(p0.x + rr * 0.3f, pie.y), rr * 2.4f, 3f * u, a)
        val forma = Path().apply { addArc(Rect(p0.x - rr, p0.y - rr * 1.2f, p0.x + rr, p0.y + rr * 1.2f), 180f, 180f); close() }
        drawPath(forma, a.luz(mezcla(PIEDRA, PALMA_OSC, 0.1f * j), k, f))
        redondoEgipto(forma, Rect(p0.x - rr, p0.y - rr * 1.2f, p0.x + rr, p0.y), a, k, hacia)
        x += rr * 2 + 2.5f * u
    }
}

/** El brillo puntual de un metal: crema de día, la llama de noche. */
private fun luzMuebleEgipto(a: AireEgipto, alfa: Float) =
    if (a.oscuro) FILO_MUEBLE.copy(alpha = alfa * 0.6f) else EgiptoTinta.SOL.copy(alpha = alfa)

/** El cesto de palma con dos franjas de palma teñida (roja y verde), lleno de panes cónicos de la ración. */
internal fun DrawScope.cestoEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val an = w * 0.15f; val al = w * 0.1f
    contactoEgipto(Offset(pie.x + w * 0.012f, pie.y), an * 1.25f, 6f * u, a, 0.4f)
    // Los panes que asoman.
    listOf(Triple(-0.035f, 0.07f, 0.02f), Triple(0.005f, 0.09f, 0.022f), Triple(0.04f, 0.065f, 0.019f)).forEachIndexed { j, (dx, alto, r) ->
        val b = Offset(pie.x + w * dx, pie.y - al + 2f * u)
        val pan = Path().apply {
            moveTo(b.x - w * r, b.y)
            quadraticTo(b.x - w * r * 0.6f, b.y - w * alto * 0.7f, b.x + w * 0.003f, b.y - w * alto)
            quadraticTo(b.x + w * r * 0.7f, b.y - w * alto * 0.6f, b.x + w * r, b.y)
            close()
        }
        drawPath(pan, a.luz(mezcla(PAN, GRANO, 0.2f * j), k, f))
        redondoEgipto(pan, Rect(b.x - w * r, b.y - w * alto, b.x + w * r, b.y), a, k, hacia)
        drawCircle(a.luz(PAPIRO, k, f).copy(alpha = 0.5f), 1.4f * u, Offset(b.x + w * 0.002f, b.y - w * alto * 0.9f))
    }
    val cuerpo = poligonoEgipto(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y - al, pie.x + an * 0.44f, pie.y, pie.x - an * 0.44f, pie.y)
    val caja = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
    drawPath(cuerpo, a.luz(FIBRA, k, f))
    clipPath(cuerpo) {
        // Las vueltas de la espiral cosida.
        val vueltas = Path()
        var y = caja.top + 2.6f * u
        while (y < caja.bottom) { vueltas.moveTo(caja.left, y); vueltas.lineTo(caja.right, y); y += 3f * u }
        drawPath(vueltas, a.luz(PALMA, k, f).copy(alpha = 0.55f), style = Stroke(0.8f * u))
        // Las dos franjas de palma teñida, en damero.
        listOf(0.3f to ROJO, 0.62f to EgiptoTinta.VERDIN).forEach { (t, col) ->
            val yb = caja.top + al * t
            var x = caja.left
            var n = 0
            while (x < caja.right) {
                if (n % 2 == 0) drawRect(a.luz(col, k, f), Offset(x, yb), Size(4f * u, 3f * u))
                else drawRect(a.luz(col, k, f), Offset(x, yb + 3f * u), Size(4f * u, 3f * u))
                x += 4f * u; n++
            }
        }
    }
    redondoEgipto(cuerpo, caja, a, k, hacia)
    drawRoundRect(a.luz(mezcla(FIBRA, PALMA_OSC, 0.3f), k, f), Offset(caja.left - 1f * u, caja.top - 1.6f * u), Size(an + 2f * u, 3.4f * u), CornerRadius(1.6f * u))
    drawRect(a.claro(0.35f), Offset(caja.left, caja.top - 1.4f * u), Size(an, 0.8f * u))
}

/**
 * Al pie, a la izquierda: el costal de lino lleno de trigo, vencido contra el poste, atado arriba con sus orejas, y el trigo
 * que se ha caído al suelo.
 */
internal fun DrawScope.costalEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val an = w * 0.23f; val al = w * 0.25f
    contactoEgipto(Offset(pie.x + w * 0.02f, pie.y), an * 1.3f, 9f * u, a, 0.42f)
    val c = Offset(pie.x - w * 0.02f, pie.y)
    val saco = Path().apply {
        moveTo(c.x - an * 0.52f, c.y)
        quadraticTo(c.x - an * 0.66f, c.y - al * 0.5f, c.x - an * 0.2f, c.y - al * 0.86f)
        quadraticTo(c.x - an * 0.08f, c.y - al * 0.98f, c.x + an * 0.02f, c.y - al * 0.88f)
        quadraticTo(c.x + an * 0.48f, c.y - al * 0.72f, c.x + an * 0.54f, c.y - al * 0.2f)
        quadraticTo(c.x + an * 0.56f, c.y, c.x + an * 0.4f, c.y)
        close()
    }
    drawPath(saco, a.luz(LINO, k, f))
    clipPath(saco) {
        val trama = Path()
        var y = c.y - al
        while (y < c.y) { trama.moveTo(c.x - an, y); trama.lineTo(c.x + an, y + 2f * u); y += 3f * u }
        drawPath(trama, a.sombra(0.08f), style = Stroke(0.5f * u))
        // Una franja azul tejida en el lino, y los pliegues del peso.
        drawRect(a.luz(EgiptoTinta.AZUL, k, f), Offset(c.x - an, c.y - al * 0.42f), Size(an * 2, 2.2f * u))
        drawRect(a.luz(EgiptoTinta.AZUL, k, f), Offset(c.x - an, c.y - al * 0.42f + 4f * u), Size(an * 2, 1.2f * u))
        drawPath(Path().apply {
            moveTo(c.x - an * 0.2f, c.y - al * 0.8f); quadraticTo(c.x - an * 0.05f, c.y - al * 0.45f, c.x - an * 0.3f, c.y - al * 0.1f)
            moveTo(c.x + an * 0.05f, c.y - al * 0.78f); quadraticTo(c.x + an * 0.25f, c.y - al * 0.5f, c.x + an * 0.18f, c.y - al * 0.15f)
        }, a.sombra(0.25f), style = Stroke(1.6f * u))
    }
    redondoEgipto(saco, Rect(c.x - an * 0.6f, c.y - al, c.x + an * 0.55f, c.y), a, k, 1f)
    val cuello = Offset(c.x - an * 0.09f, c.y - al * 0.92f)
    drawPath(Path().apply {
        moveTo(cuello.x, cuello.y); quadraticTo(cuello.x - an * 0.2f, cuello.y - al * 0.16f, cuello.x - an * 0.24f, cuello.y - al * 0.08f)
        quadraticTo(cuello.x - an * 0.1f, cuello.y - al * 0.02f, cuello.x, cuello.y)
        quadraticTo(cuello.x + an * 0.12f, cuello.y - al * 0.2f, cuello.x + an * 0.2f, cuello.y - al * 0.12f)
        quadraticTo(cuello.x + an * 0.1f, cuello.y - al * 0.02f, cuello.x, cuello.y)
    }, a.luz(mezcla(LINO, PAPIRO, 0.3f), k, f))
    drawLine(a.luz(FIBRA, k, f), cuello + Offset(-3f * u, 1f * u), cuello + Offset(3f * u, 0f), 2f * u)
    for (j in 0 until 30) {
        val q = Offset(pie.x + w * (0.04f + 0.22f * azar(j, 91)), pie.y + w * (-0.01f + 0.035f * azar(j, 92)))
        drawOval(a.luz(GRANO, k, f), Offset(q.x - 1f * u, q.y - 0.6f * u), Size(2f * u, 1.2f * u))
    }
}

/** La medida de madera para el grano, colmada, con sus aros y un poco de trigo derramado. */
internal fun DrawScope.medidaEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val m = Rect(pie.x - w * 0.06f, pie.y - w * 0.105f, pie.x + w * 0.06f, pie.y)
    contactoEgipto(Offset(m.center.x + w * 0.015f, m.bottom), m.width * 1.4f, 7f * u, a, 0.4f)
    drawRect(a.luz(ACACIA, k, f), m.topLeft, m.size)
    val duelas = Path()
    var x = m.left + m.width / 6
    while (x < m.right - 1f) { duelas.moveTo(x, m.top); duelas.lineTo(x, m.bottom); x += m.width / 6 }
    drawPath(duelas, a.luz(ACACIA_OSC, k, f).copy(alpha = 0.4f), style = Stroke(0.7f * u))
    drawRect(a.claro(0.28f), Offset(m.left + m.width * 0.1f, m.top), Size(m.width * 0.16f, m.height))
    drawRect(a.sombra(0.32f), Offset(m.right - m.width * 0.28f, m.top), Size(m.width * 0.28f, m.height))
    listOf(0.16f, 0.8f).forEach { t -> drawRect(a.luz(ACACIA_OSC, k, f), Offset(m.left - 0.8f * u, m.top + m.height * t), Size(m.width + 1.6f * u, 2.6f * u)) }
    drawArc(a.luz(GRANO, k, f), 180f, 180f, true, Offset(m.left + 0.5f * u, m.top - m.width * 0.24f), Size(m.width - 1f * u, m.width * 0.48f))
    for (j in 0 until 10) drawOval(a.luz(mezcla(GRANO, PALMA_OSC, 0.25f), k, f), Offset(m.left + m.width * (0.1f + 0.08f * j), m.top - m.width * (0.02f + 0.14f * azar(j, 93))), Size(2f * u, 1.2f * u))
    for (j in 0 until 14) {
        val q = Offset(m.right + w * 0.04f * azar(j, 94), m.bottom - w * 0.012f * azar(j, 95))
        drawOval(a.luz(GRANO, k, f), Offset(q.x - 1f * u, q.y - 0.6f * u), Size(2f * u, 1.2f * u))
    }
}

/** Una tinaja grande medio enterrada en el suelo (fresca para el agua), tapada con un tiesto; el montón de tierra alrededor. */
internal fun DrawScope.tinajaEnterradaEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val r = w * 0.085f
    val hombro = Path().apply {
        moveTo(pie.x - r, pie.y)
        cubicTo(pie.x - r, pie.y - r * 0.7f, pie.x - r * 0.55f, pie.y - r * 0.95f, pie.x - r * 0.42f, pie.y - r * 1.02f)
        lineTo(pie.x + r * 0.42f, pie.y - r * 1.02f)
        cubicTo(pie.x + r * 0.55f, pie.y - r * 0.95f, pie.x + r, pie.y - r * 0.7f, pie.x + r, pie.y)
        close()
    }
    val caja = Rect(pie.x - r, pie.y - r * 1.02f, pie.x + r, pie.y)
    drawPath(hombro, a.luz(MARGA, k, f))
    clipPath(hombro) {
        drawRect(a.luz(ROJO, k, f), Offset(caja.left, pie.y - r * 0.62f), Size(caja.width, 1.6f * u))
        drawRect(a.luz(EgiptoTinta.AZUL, k, f), Offset(caja.left, pie.y - r * 0.55f), Size(caja.width, 3.4f * u))
    }
    redondoEgipto(hombro, caja, a, k, hacia)
    // El cuello con su labio y el tapón de barro.
    val cuelloT = Rect(pie.x - r * 0.36f, pie.y - r * 1.28f, pie.x + r * 0.36f, pie.y - r * 0.98f)
    drawRect(a.luz(MARGA, k, f), cuelloT.topLeft, cuelloT.size)
    drawRect(a.sombra(0.25f), Offset(cuelloT.right - cuelloT.width * 0.3f, cuelloT.top), Size(cuelloT.width * 0.3f, cuelloT.height))
    drawRoundRect(a.luz(mezcla(MARGA, PALMA_OSC, 0.12f), k, f), Offset(pie.x - r * 0.46f, pie.y - r * 1.36f), Size(r * 0.92f, r * 0.14f), CornerRadius(2f * u))
    drawArc(a.luz(BARRO, k, f), 180f, 180f, true, Offset(pie.x - r * 0.34f, pie.y - r * 1.52f), Size(r * 0.68f, r * 0.36f))
    drawArc(a.claro(0.3f), 200f, 60f, false, Offset(pie.x - r * 0.3f, pie.y - r * 1.5f), Size(r * 0.5f, r * 0.3f), style = Stroke(1f * u))
    // La tierra amontonada alrededor de la boca enterrada.
    val tierra = Path().apply {
        moveTo(pie.x - r * 1.5f, pie.y + r * 0.12f)
        quadraticTo(pie.x - r * 1.1f, pie.y - r * 0.22f, pie.x - r * 0.9f, pie.y - r * 0.05f)
        lineTo(pie.x + r * 0.9f, pie.y - r * 0.05f)
        quadraticTo(pie.x + r * 1.1f, pie.y - r * 0.22f, pie.x + r * 1.5f, pie.y + r * 0.12f)
        close()
    }
    drawPath(tierra, a.luz(mezcla(EgiptoTinta.TIERRA, PALMA_OSC, 0.28f), k, f))
    drawPath(tierra, a.claro(0.2f), style = Stroke(0.8f * u))
}

/**
 * La lámpara del almacén, en el suelo: un pie de barro alto y hueco, ensanchado abajo y arriba, y encima el cuenco de grasa
 * con su mecha de lino retorcido (de día, apagada y negra de hollín).
 */
internal fun DrawScope.lamparaEgipto(llama: Offset, piso: Float, w: Float, a: AireEgipto) {
    val u = density
    val top = llama.y + w * 0.02f
    if (piso - top < 4f * u) return
    contactoEgipto(Offset(llama.x + w * 0.014f, piso), w * 0.13f, 6f * u, a, 0.4f)
    val an = w * 0.052f
    val pieB = Path().apply {
        moveTo(llama.x - an, piso)
        quadraticTo(llama.x - an * 0.45f, piso - (piso - top) * 0.3f, llama.x - an * 0.4f, piso - (piso - top) * 0.55f)
        quadraticTo(llama.x - an * 0.5f, top + (piso - top) * 0.15f, llama.x - an * 0.85f, top)
        lineTo(llama.x + an * 0.85f, top)
        quadraticTo(llama.x + an * 0.5f, top + (piso - top) * 0.15f, llama.x + an * 0.4f, piso - (piso - top) * 0.55f)
        quadraticTo(llama.x + an * 0.45f, piso - (piso - top) * 0.3f, llama.x + an, piso)
        close()
    }
    drawPath(pieB, a.luz(BARRO, if (a.lumbre) 0.75f else 1f))
    redondoEgipto(pieB, Rect(llama.x - an, top, llama.x + an, piso), a, 0.8f, -1f)
    // Dos rayas rojas pintadas en el pie.
    clipPath(pieB) {
        drawRect(a.luz(ROJO, if (a.lumbre) 0.75f else 1f), Offset(llama.x - an, top + (piso - top) * 0.2f), Size(an * 2, 1.6f * u))
        drawRect(a.luz(ROJO, if (a.lumbre) 0.75f else 1f), Offset(llama.x - an, piso - (piso - top) * 0.25f), Size(an * 2, 1.6f * u))
        if (a.lumbre) drawRect(a.filo(0.35f), Offset(llama.x - an, top), Size(an * 2, (piso - top) * 0.25f))
    }
    val cuenco = Rect(llama.x - w * 0.05f, llama.y + w * 0.002f, llama.x + w * 0.05f, top + w * 0.006f)
    drawArc(a.luz(mezcla(BARRO, PAPIRO, 0.15f), if (a.lumbre) 0.95f else 1f), 0f, 180f, true, Offset(cuenco.left, cuenco.top - cuenco.height), Size(cuenco.width, cuenco.height * 2))
    drawOval(a.luz(mezcla(BARRO, PALMA_OSC, 0.4f), if (a.lumbre) 0.9f else 1f), Offset(cuenco.left, cuenco.top - cuenco.height * 0.3f), Size(cuenco.width, cuenco.height * 0.6f))
    drawLine(a.luz(if (a.lumbre) LINO else PALMA_OSC, 1f), Offset(llama.x - w * 0.014f, cuenco.top), Offset(llama.x, llama.y - 1f * u), 1.8f * u)
}

/** La llama de la mecha, con su halo; encima de todo y con su propio color. */
internal fun DrawScope.llamaEgipto(p: Offset, w: Float, a: AireEgipto) {
    val u = density
    val alto = w * (if (a.lunar) 0.028f else 0.04f)
    mancha(p - Offset(0f, alto * 0.4f), w * (if (a.lunar) 0.1f else 0.15f), LLAMA.copy(alpha = if (a.oscuro) 0.55f else 0.35f))
    val lengua = Path().apply {
        moveTo(p.x - alto * 0.26f, p.y)
        quadraticTo(p.x - alto * 0.3f, p.y - alto * 0.55f, p.x + alto * 0.06f, p.y - alto)
        quadraticTo(p.x + alto * 0.28f, p.y - alto * 0.45f, p.x + alto * 0.24f, p.y)
        close()
    }
    drawPath(lengua, LLAMA)
    drawPath(Path().apply {
        moveTo(p.x - alto * 0.12f, p.y); quadraticTo(p.x - alto * 0.1f, p.y - alto * 0.4f, p.x + alto * 0.04f, p.y - alto * 0.6f)
        quadraticTo(p.x + alto * 0.14f, p.y - alto * 0.3f, p.x + alto * 0.1f, p.y); close()
    }, androidx.compose.ui.graphics.Color(0xFFFFF6DA))
    drawCircle(FILO_MUEBLE.copy(alpha = 0.5f), 1.4f * u, Offset(p.x, p.y + 0.5f * u))
}

/**
 * Lo que guarda un hueco libre de una repisa, según la repisa: un rollo de mechas nuevas, el cuenco de fayenza con cuentas,
 * las pesas de piedra en fila o un pan cónico con un manojo de cebollas tiernas.
 */
internal fun DrawScope.huecoLibreEgipto(k: Int, r: Rect, oc: Float, x1: Float, a: AireEgipto, cerca: Float, frio: Float) {
    val u = density
    val libre = x1 - oc - 6f * u
    if (libre < 28f * u || r.height < 30f * u) return
    val pie = Offset(minOf(oc + 10f * u + libre * 0.5f, x1 - 24f * u), r.bottom)
    val an = minOf(libre * 0.7f, 64f * u)
    when ((k + (azar(k, 41) * 3).toInt()) % 4) {
        0 -> {
            // Un rollo de mechas nuevas, enroscado y atado.
            contactoEgipto(Offset(pie.x + 2f * u, pie.y), an, 5f * u, a)
            val rr = minOf(an * 0.36f, 20f * u)
            val c = Offset(pie.x, pie.y - rr)
            drawCircle(a.luz(LINO, cerca, frio), rr, c)
            for (j in 1..3) drawCircle(a.sombra(0.25f), rr * (1f - j * 0.24f), c, style = Stroke(1f * u))
            redondoEgipto(Path().apply { addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr)) }, Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr), a, cerca, -1f)
            drawLine(a.luz(FIBRA, cerca, frio), Offset(c.x - rr * 0.2f, c.y - rr), Offset(c.x + rr * 0.1f, c.y + rr), 1.6f * u)
            drawLine(a.luz(LINO, cerca, frio), Offset(c.x + rr * 0.8f, c.y + rr * 0.6f), Offset(c.x + rr * 1.5f, pie.y - 0.6f * u), 2f * u, cap = StrokeCap.Round)
        }
        1 -> {
            // El cuenco de fayenza azul con cuentas de colores.
            val rr = minOf(an * 0.46f, 26f * u)
            contactoEgipto(Offset(pie.x + 2f * u, pie.y), rr * 2.3f, 5f * u, a)
            val boca = Rect(pie.x - rr, pie.y - rr * 0.9f, pie.x + rr, pie.y - rr * 0.6f)
            for (j in 0 until 9) {
                val q = Offset(boca.left + boca.width * (0.15f + 0.7f * azar(j, 51)), boca.top - 1f * u - 3f * u * azar(j, 52))
                drawCircle(a.luz(if (j % 3 == 0) FAYENZA else if (j % 3 == 1) ROJO else GRANO, cerca, frio), 1.6f * u, q)
            }
            val cuenco = Path().apply {
                moveTo(boca.left, boca.center.y); quadraticTo(pie.x - rr * 0.8f, pie.y, pie.x, pie.y)
                quadraticTo(pie.x + rr * 0.8f, pie.y, boca.right, boca.center.y); close()
            }
            drawPath(cuenco, a.luz(FAYENZA, cerca, frio))
            redondoEgipto(cuenco, Rect(boca.left, boca.top, boca.right, pie.y), a, cerca, -1f)
            drawOval(a.luz(mezcla(FAYENZA, EgiptoTinta.TINTA, 0.35f), cerca, frio), boca.topLeft, boca.size, style = Stroke(1.2f * u))
            // El pétalo pintado en negro, gastado.
            drawLine(a.luz(EgiptoTinta.TINTA, cerca, frio).copy(alpha = 0.5f), Offset(pie.x - rr * 0.3f, pie.y - rr * 0.5f), Offset(pie.x, pie.y - rr * 0.15f), 0.9f * u)
            drawLine(a.luz(EgiptoTinta.TINTA, cerca, frio).copy(alpha = 0.5f), Offset(pie.x + rr * 0.3f, pie.y - rr * 0.5f), Offset(pie.x, pie.y - rr * 0.15f), 0.9f * u)
            drawArc(a.claro(0.55f), 200f, 50f, false, Offset(boca.left + rr * 0.2f, boca.top), Size(rr * 1.6f, rr * 1.2f), style = Stroke(1.2f * u))
        }
        2 -> {
            // Las pesas de piedra en fila, de mayor a menor, y un trozo de cobre.
            var x = pie.x - an * 0.45f
            listOf(1f, 0.75f, 0.55f, 0.4f).forEachIndexed { j, s ->
                val rr = minOf(an * 0.2f, 14f * u) * s
                val forma = Path().apply { addArc(Rect(x, pie.y - rr * 2.2f, x + rr * 2, pie.y + rr * 2.2f), 180f, 180f); close() }
                contactoEgipto(Offset(x + rr * 1.3f, pie.y), rr * 2.6f, 3f * u, a)
                drawPath(forma, a.luz(mezcla(PIEDRA, PALMA_OSC, 0.12f * j), cerca, frio))
                redondoEgipto(forma, Rect(x, pie.y - rr * 2.2f, x + rr * 2, pie.y), a, cerca, -1f)
                x += rr * 2 + 2f * u
            }
            drawLine(a.luz(COBRE, cerca, frio), Offset(x + 1f * u, pie.y - 1.4f * u), Offset(x + 9f * u, pie.y - 1.4f * u), 2.6f * u)
        }
        else -> {
            // Un pan cónico, tumbado, con un manojo de cebollas tiernas.
            val al = minOf(an * 0.7f, 38f * u)
            contactoEgipto(Offset(pie.x + 2f * u, pie.y), an, 5f * u, a)
            val pan = Path().apply {
                moveTo(pie.x - al * 0.9f, pie.y - al * 0.12f)
                quadraticTo(pie.x - al * 0.2f, pie.y - al * 0.7f, pie.x + al * 0.5f, pie.y - al * 0.62f)
                quadraticTo(pie.x + al * 0.7f, pie.y - al * 0.3f, pie.x + al * 0.45f, pie.y)
                lineTo(pie.x - al * 0.75f, pie.y)
                close()
            }
            drawPath(pan, a.luz(PAN, cerca, frio))
            redondoEgipto(pan, Rect(pie.x - al * 0.9f, pie.y - al * 0.7f, pie.x + al * 0.7f, pie.y), a, cerca, -1f)
            for (j in 0 until 4) {
                val x0 = pie.x + al * (0.3f + 0.12f * j)
                drawLine(a.luz(EgiptoTinta.VERDIN, cerca, frio), Offset(x0, pie.y - 3f * u), Offset(x0 + al * 0.7f, pie.y - al * (0.5f + 0.1f * j)), 1.2f * u)
                drawCircle(a.luz(mezcla(PAPIRO, CEBOLLA, 0.3f), cerca, frio), 3f * u, Offset(x0, pie.y - 3f * u))
            }
        }
    }
}
