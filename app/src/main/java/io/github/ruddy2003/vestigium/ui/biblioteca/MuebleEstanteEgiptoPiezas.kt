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

/**
 * Los manojos de mechas de lino retorcido que se reparten para alumbrar la tumba, colgados de una estaca: cada mecha cae con
 * su torsión, atadas arriba con una vuelta de cordel, las puntas deshilachadas.
 */
internal fun DrawScope.mechasEgipto(clavo: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val largo = w * 0.2f
    val cuerdas = Path(); val torsion = Path()
    for (m in 0 until 2) {
        val ox = clavo.x + (m - 0.5f) * w * 0.026f
        for (j in 0 until 6) {
            val x0 = ox + (j - 2.5f) * 0.9f * u
            val x1 = ox + (j - 2.5f) * 2.3f * u + (m - 0.5f) * w * 0.02f
            val y1 = clavo.y + largo * (0.84f + 0.16f * azar(m * 7 + j, 5)) - m * w * 0.02f
            cuerdas.moveTo(x0, clavo.y + 4f * u); cuerdas.quadraticTo((x0 + x1) / 2 + 1.5f * u, (clavo.y + y1) / 2, x1, y1)
            var y = clavo.y + 8f * u
            while (y < y1 - 3f * u) {
                val t = (y - clavo.y) / (y1 - clavo.y)
                val x = x0 + (x1 - x0) * t * t
                torsion.moveTo(x - 1f * u, y); torsion.lineTo(x + 1f * u, y + 1.6f * u)
                y += 3.2f * u
            }
        }
    }
    drawPath(corridoEgipto(cuerdas, 2.4f * u, 2f * u), a.sombra(0.3f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
    drawPath(cuerdas, a.luz(LINO, k, f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    drawPath(torsion, a.luz(mezcla(LINO, PALMA_OSC, 0.5f), k, f).copy(alpha = 0.6f), style = Stroke(0.6f * u))
    drawPath(cuerdas, a.claro(0.25f), style = Stroke(0.6f * u))
    // La vuelta de cordel que los ata y la estaca.
    drawRoundRect(a.luz(FIBRA, k, f), Offset(clavo.x - w * 0.03f, clavo.y + 3f * u), Size(w * 0.06f, 3.2f * u), CornerRadius(1.5f * u))
    drawLine(a.sombra(0.3f), Offset(clavo.x - w * 0.03f, clavo.y + 6f * u), Offset(clavo.x + w * 0.03f, clavo.y + 6f * u), 0.7f * u)
    estacaEgipto(clavo, a, k)
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
        val piel = mezcla(CEBOLLA, PAPIRO, 0.25f * azar(j, 72))
        drawPath(forma, a.luz(piel, k, f))
        // Las vetas de la piel.
        drawPath(Path().apply {
            moveTo(c.x - r * 0.5f, c.y - r * 0.9f); quadraticTo(c.x - r * 0.8f, c.y, c.x - r * 0.4f, c.y + r * 0.8f)
            moveTo(c.x + r * 0.3f, c.y - r * 1f); quadraticTo(c.x + r * 0.6f, c.y, c.x + r * 0.35f, c.y + r * 0.85f)
        }, a.sombra(0.25f), style = Stroke(0.6f * u))
        redondoEgipto(forma, Rect(c.x - r, c.y - r * 1.5f, c.x + r, c.y + r * 1.25f), a, k, -1f)
        drawLine(a.luz(PAPIRO, k, f).copy(alpha = 0.7f), Offset(c.x, c.y + r * 1.15f), Offset(c.x + 0.6f * u, c.y + r * 1.5f), 0.7f * u)
    }
    estacaEgipto(clavo, a, k)
}

/**
 * El mazo de madera y los cinceles de cobre que se devolvieron gastados, atados en haz: el cobre con su filo claro y manchas
 * de verdín, tumbados sobre el tablón.
 */
internal fun DrawScope.cincelesEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    contactoEgipto(Offset(pie.x + w * 0.01f, pie.y), w * 0.2f, 4f * u, a)
    // El mazo: una porra de madera dura, tumbada, con el mango hacia la izquierda.
    val cabeza = Rect(pie.x + w * 0.02f, pie.y - w * 0.05f, pie.x + w * 0.11f, pie.y)
    val mazo = Path().apply {
        moveTo(cabeza.left, cabeza.center.y - cabeza.height * 0.18f)
        quadraticTo(cabeza.left + cabeza.width * 0.3f, cabeza.top, cabeza.right - cabeza.width * 0.2f, cabeza.top + 1f * u)
        quadraticTo(cabeza.right + 1f * u, cabeza.center.y, cabeza.right - cabeza.width * 0.2f, cabeza.bottom)
        lineTo(cabeza.left + cabeza.width * 0.2f, cabeza.bottom)
        quadraticTo(cabeza.left, cabeza.center.y + cabeza.height * 0.2f, cabeza.left, cabeza.center.y - cabeza.height * 0.18f)
        close()
    }
    drawRoundRect(a.luz(ACACIA_OSC, k, f), Offset(pie.x - w * 0.08f, pie.y - w * 0.03f), Size(w * 0.11f, w * 0.016f), CornerRadius(2f * u))
    drawPath(mazo, a.luz(mezcla(ACACIA, PALMA_OSC, 0.35f), k, f))
    redondoEgipto(mazo, cabeza, a, k, -1f)
    // Los cinceles, en haz, encima, atados con un cordel.
    for (j in 0 until 5) {
        val y = pie.y - w * 0.006f - j * 2.6f * u - (if (j > 2) w * 0.01f else 0f)
        val x0 = pie.x - w * 0.1f + j * 1.5f * u; val x1 = pie.x + w * 0.045f + j * 2f * u
        drawLine(a.sombra(0.35f), Offset(x0 + 1f * u, y + 1.2f * u), Offset(x1 + 1f * u, y + 1.2f * u), 2.6f * u)
        drawLine(a.luz(mezcla(COBRE, PALMA_OSC, 0.12f * j), k, f), Offset(x0, y), Offset(x1, y), 2.4f * u, cap = StrokeCap.Square)
        drawLine(a.claro(0.4f), Offset(x0, y - 0.7f * u), Offset(x1 - 3f * u, y - 0.7f * u), 0.6f * u)
        drawCircle(a.luz(VERDIN, k, f).copy(alpha = 0.7f), 1f * u, Offset(x0 + (x1 - x0) * azar(j, 33), y))
        if (a.oscuro) drawLine(a.filo(0.5f * k), Offset(x1 - 2f * u, y), Offset(x1, y), 2.4f * u)
    }
    drawLine(a.luz(FIBRA, k, f), Offset(pie.x - w * 0.035f, pie.y - w * 0.038f), Offset(pie.x - w * 0.03f, pie.y + 1f * u), 1.4f * u)
}

/**
 * La balanza del cobre, que manda sobre el anaquel: el pie de madera con su poste, el astil un poco caído hacia el platillo de
 * los cinceles, los cordeles de los platillos, la plomada que dice si está en fiel, y las pesas de piedra al lado.
 */
internal fun DrawScope.balanzaEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val madera = a.luz(ACACIA, k, f); val maderaOsc = a.luz(ACACIA_OSC, k, f)
    // El pie y el poste.
    contactoEgipto(Offset(pie.x + w * 0.01f, pie.y), w * 0.13f, 5f * u, a)
    val zocalo = Rect(pie.x - w * 0.045f, pie.y - w * 0.026f, pie.x + w * 0.045f, pie.y)
    drawRect(madera, zocalo.topLeft, zocalo.size)
    drawRect(a.claro(0.3f), zocalo.topLeft, Size(zocalo.width, 1.4f * u))
    drawRect(a.sombra(0.3f), Offset(zocalo.right - zocalo.width * 0.25f, zocalo.top), Size(zocalo.width * 0.25f, zocalo.height))
    val alto = w * 0.29f
    val poste = Rect(pie.x - w * 0.012f, zocalo.top - alto, pie.x + w * 0.012f, zocalo.top)
    drawRect(a.sombra(0.25f), Offset(poste.right, poste.top + 6f * u), Size(4f * u, poste.height - 6f * u))
    drawRect(madera, poste.topLeft, poste.size)
    drawRect(a.claro(0.3f), Offset(poste.left + 1f * u, poste.top), Size(1.4f * u, poste.height))
    drawRect(a.sombra(0.3f), Offset(poste.right - poste.width * 0.35f, poste.top), Size(poste.width * 0.35f, poste.height))
    // El astil, caído hacia la izquierda (los cinceles pesan más).
    val eje = Offset(pie.x, poste.top + 3f * u)
    val brazo = w * 0.16f
    val caida = 0.07f
    val pIzq = Offset(eje.x - brazo, eje.y + brazo * caida)
    val pDer = Offset(eje.x + brazo, eje.y - brazo * caida)
    drawLine(a.sombra(0.3f), pIzq + Offset(1.5f * u, 2f * u), pDer + Offset(1.5f * u, 2f * u), 3.6f * u)
    drawLine(maderaOsc, pIzq, pDer, 4.4f * u, cap = StrokeCap.Round)
    drawLine(madera, pIzq + Offset(0f, -0.8f * u), pDer + Offset(0f, -0.8f * u), 1.6f * u, cap = StrokeCap.Round)
    drawCircle(a.luz(COBRE, k, f), 2.4f * u, eje)
    // La plomada que cuelga del astil junto al poste.
    val plomo = Offset(eje.x + 5f * u, eje.y + w * 0.09f)
    drawLine(a.luz(LINO, k, f), Offset(eje.x + 5f * u + 0.4f * u, eje.y + 1f * u), plomo, 0.6f * u)
    drawPath(Path().apply { moveTo(plomo.x - 2.2f * u, plomo.y); lineTo(plomo.x + 2.2f * u, plomo.y); lineTo(plomo.x, plomo.y + 6f * u); close() }, a.luz(PIEDRA, k, f))
    // Los platillos con sus cordeles.
    fun platillo(cuelga: Offset, carga: Int) {
        val hondo = w * 0.12f
        val c = Offset(cuelga.x, cuelga.y + hondo)
        val r = w * 0.052f
        val cord = Path().apply {
            moveTo(cuelga.x, cuelga.y); lineTo(c.x - r, c.y)
            moveTo(cuelga.x, cuelga.y); lineTo(c.x, c.y - 1f * u)
            moveTo(cuelga.x, cuelga.y); lineTo(c.x + r, c.y)
        }
        drawPath(cord, a.luz(mezcla(LINO, FIBRA, 0.4f), k, f), style = Stroke(0.7f * u))
        if (carga == 0) {
            // Los cinceles que se pesan.
            for (j in 0 until 4) {
                val y = c.y - 1.6f * u - j * 1.8f * u
                val x0 = c.x - r * 0.75f + j * 1.2f * u
                drawLine(a.luz(mezcla(COBRE, PALMA_OSC, 0.1f * j), k, f), Offset(x0, y + (j % 2) * 0.6f * u), Offset(x0 + r * 1.2f, y - (j % 2) * 0.8f * u), 2.2f * u)
            }
        } else {
            // Dos pesas de piedra, de cúpula.
            listOf(-0.4f to 0.75f, 0.35f to 0.55f).forEach { (dx, s) ->
                val rr = r * 0.42f * s
                val p0 = Offset(c.x + r * dx, c.y - 1f * u)
                drawArc(a.luz(PIEDRA, k, f), 180f, 180f, true, Offset(p0.x - rr, p0.y - rr), Size(rr * 2, rr * 2))
                drawArc(a.claro(0.4f), 200f, 60f, false, Offset(p0.x - rr * 0.8f, p0.y - rr * 0.8f), Size(rr * 1.6f, rr * 1.6f), style = Stroke(0.8f * u))
            }
        }
        val plato = Rect(c.x - r, c.y - r * 0.2f, c.x + r, c.y + r * 0.34f)
        drawArc(a.luz(COBRE, k, f), 0f, 180f, true, plato.topLeft, plato.size)
        drawArc(a.sombra(0.3f), 0f, 90f, true, plato.topLeft, plato.size)
        drawOval(a.luz(mezcla(COBRE, VERDIN, 0.35f), k, f), Offset(plato.left, plato.top - r * 0.06f), Size(plato.width, r * 0.26f))
        drawLine(a.claro(0.45f), Offset(plato.left + r * 0.2f, plato.top + r * 0.04f), Offset(plato.left + r * 0.9f, plato.top + r * 0.02f), 0.8f * u)
        if (a.oscuro && k > 0.1f) drawArc(a.filo(0.6f * k), -10f, 60f, false, plato.topLeft, plato.size, style = Stroke(1f * u))
    }
    platillo(pIzq, 0)
    platillo(pDer, 1)
    // Las pesas de piedra sobre el tablón, de mayor a menor.
    var x = pie.x + w * 0.11f
    listOf(1f, 0.72f, 0.5f).forEachIndexed { j, s ->
        val rr = w * 0.026f * s
        val p0 = Offset(x + rr, pie.y)
        contactoEgipto(Offset(p0.x + rr * 0.3f, pie.y), rr * 2.4f, 3f * u, a)
        val forma = Path().apply { addArc(Rect(p0.x - rr, p0.y - rr * 1.2f, p0.x + rr, p0.y + rr * 1.2f), 180f, 180f); close() }
        drawPath(forma, a.luz(mezcla(PIEDRA, PALMA_OSC, 0.1f * j), k, f))
        redondoEgipto(forma, Rect(p0.x - rr, p0.y - rr * 1.2f, p0.x + rr, p0.y), a, k, hacia)
        x += rr * 2 + 2.5f * u
    }
}

/** Los panes cónicos de la ración, sobre un plato de hojas de palma trenzadas: la corteza dorada, uno partido. */
internal fun DrawScope.panesEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val plato = Rect(pie.x - w * 0.085f, pie.y - w * 0.014f, pie.x + w * 0.085f, pie.y + 1f * u)
    contactoEgipto(Offset(pie.x + w * 0.01f, pie.y), w * 0.2f, 5f * u, a)
    drawOval(a.luz(FIBRA, k, f), plato.topLeft, plato.size)
    for (j in 0 until 7) drawLine(a.luz(PALMA, k, f).copy(alpha = 0.5f), Offset(plato.left + plato.width * (0.1f + 0.12f * j), plato.top + 1f * u), Offset(plato.left + plato.width * (0.16f + 0.12f * j), plato.bottom - 1f * u), 0.7f * u)
    listOf(Triple(-0.045f, 0.1f, 0.025f), Triple(0.002f, 0.125f, 0.027f), Triple(0.048f, 0.09f, 0.023f)).forEachIndexed { j, (dx, alto, r) ->
        val b = Offset(pie.x + w * dx, plato.center.y)
        val forma = Path().apply {
            moveTo(b.x - w * r, b.y)
            quadraticTo(b.x - w * r * 0.6f, b.y - w * alto * 0.7f, b.x + w * 0.004f, b.y - w * alto)
            quadraticTo(b.x + w * r * 0.7f, b.y - w * alto * 0.6f, b.x + w * r, b.y)
            close()
        }
        drawPath(forma, a.luz(mezcla(PAN, GRANO, 0.2f * j), k, f))
        redondoEgipto(forma, Rect(b.x - w * r, b.y - w * alto, b.x + w * r, b.y), a, k, hacia)
        // La harina en la punta y las grietas de la corteza.
        drawCircle(a.luz(PAPIRO, k, f).copy(alpha = 0.5f), 1.4f * u, Offset(b.x + w * 0.003f, b.y - w * alto * 0.92f))
        drawLine(a.sombra(0.3f), Offset(b.x - w * r * 0.3f, b.y - w * alto * 0.4f), Offset(b.x + w * r * 0.1f, b.y - w * alto * 0.5f), 0.6f * u)
    }
    // Un trozo del pan partido, delante.
    val trozo = Path().apply {
        moveTo(pie.x + w * 0.06f, plato.bottom); lineTo(pie.x + w * 0.085f, plato.bottom - w * 0.02f); lineTo(pie.x + w * 0.11f, plato.bottom); close()
    }
    drawPath(trozo, a.luz(mezcla(PAN, PAPIRO, 0.45f), k, f))
}

/**
 * Al pie, a la izquierda: el costal de lino lleno de trigo, vencido contra el poste, atado arriba con sus orejas; delante, la
 * medida de madera colmada y el trigo que se ha caído al suelo.
 */
internal fun DrawScope.costalEgipto(pie: Offset, w: Float, a: AireEgipto, k: Float, f: Float) {
    val u = density
    val an = w * 0.19f; val al = w * 0.2f
    contactoEgipto(Offset(pie.x + w * 0.02f, pie.y), an * 1.3f, 8f * u, a, 0.42f)
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
        // La trama del lino y los pliegues del peso.
        var y = c.y - al
        while (y < c.y) { drawLine(a.sombra(0.08f), Offset(c.x - an, y), Offset(c.x + an, y + 2f * u), 0.5f * u); y += 3f * u }
        drawPath(Path().apply {
            moveTo(c.x - an * 0.2f, c.y - al * 0.8f); quadraticTo(c.x - an * 0.05f, c.y - al * 0.45f, c.x - an * 0.3f, c.y - al * 0.1f)
            moveTo(c.x + an * 0.05f, c.y - al * 0.78f); quadraticTo(c.x + an * 0.25f, c.y - al * 0.5f, c.x + an * 0.18f, c.y - al * 0.15f)
        }, a.sombra(0.25f), style = Stroke(1.4f * u))
    }
    redondoEgipto(saco, Rect(c.x - an * 0.6f, c.y - al, c.x + an * 0.55f, c.y), a, k, 1f)
    // El atado: el cuello fruncido y las dos orejas del lino.
    val cuello = Offset(c.x - an * 0.09f, c.y - al * 0.92f)
    drawPath(Path().apply {
        moveTo(cuello.x, cuello.y); quadraticTo(cuello.x - an * 0.2f, cuello.y - al * 0.16f, cuello.x - an * 0.24f, cuello.y - al * 0.08f)
        quadraticTo(cuello.x - an * 0.1f, cuello.y - al * 0.02f, cuello.x, cuello.y)
        quadraticTo(cuello.x + an * 0.12f, cuello.y - al * 0.2f, cuello.x + an * 0.2f, cuello.y - al * 0.12f)
        quadraticTo(cuello.x + an * 0.1f, cuello.y - al * 0.02f, cuello.x, cuello.y)
    }, a.luz(mezcla(LINO, PAPIRO, 0.3f), k, f))
    drawLine(a.luz(FIBRA, k, f), cuello + Offset(-3f * u, 1f * u), cuello + Offset(3f * u, 0f), 1.8f * u)
    // El trigo caído y la medida de madera colmada.
    for (j in 0 until 26) {
        val q = Offset(pie.x + w * (0.02f + 0.2f * azar(j, 91)), pie.y + w * (-0.01f + 0.035f * azar(j, 92)))
        drawOval(a.luz(GRANO, k, f), Offset(q.x - 1f * u, q.y - 0.6f * u), Size(2f * u, 1.2f * u))
    }
    val m = Rect(pie.x + w * 0.07f, pie.y - w * 0.075f, pie.x + w * 0.16f, pie.y + w * 0.012f)
    contactoEgipto(Offset(m.center.x + w * 0.012f, m.bottom), m.width * 1.3f, 5f * u, a)
    drawRect(a.luz(ACACIA, k, f), m.topLeft, m.size)
    drawRect(a.claro(0.25f), Offset(m.left + m.width * 0.1f, m.top), Size(m.width * 0.16f, m.height))
    drawRect(a.sombra(0.3f), Offset(m.right - m.width * 0.28f, m.top), Size(m.width * 0.28f, m.height))
    listOf(0.2f, 0.78f).forEach { t -> drawRect(a.luz(ACACIA_OSC, k, f), Offset(m.left - 0.6f * u, m.top + m.height * t), Size(m.width + 1.2f * u, 2.2f * u)) }
    drawArc(a.luz(GRANO, k, f), 180f, 180f, true, Offset(m.left + 0.5f * u, m.top - m.width * 0.22f), Size(m.width - 1f * u, m.width * 0.44f))
    for (j in 0 until 8) drawOval(a.luz(mezcla(GRANO, PALMA_OSC, 0.25f), k, f), Offset(m.left + m.width * (0.12f + 0.1f * j), m.top - m.width * (0.02f + 0.12f * azar(j, 93))), Size(1.8f * u, 1.1f * u))
}

/**
 * La lámpara del almacén, en el suelo: un pie de barro alto y hueco, ensanchado abajo y arriba, y encima el cuenco de grasa
 * con su mecha de lino retorcido (de día, apagada y negra de hollín).
 */
internal fun DrawScope.lamparaEgipto(llama: Offset, piso: Float, w: Float, a: AireEgipto) {
    val u = density
    val k = if (a.lumbre) 1f else 1f
    val top = llama.y + w * 0.016f
    if (piso - top < 4f * u) return
    contactoEgipto(Offset(llama.x + w * 0.012f, piso), w * 0.1f, 5f * u, a)
    val pieB = Path().apply {
        moveTo(llama.x - w * 0.04f, piso)
        quadraticTo(llama.x - w * 0.018f, piso - (piso - top) * 0.3f, llama.x - w * 0.016f, piso - (piso - top) * 0.55f)
        quadraticTo(llama.x - w * 0.02f, top + (piso - top) * 0.15f, llama.x - w * 0.034f, top)
        lineTo(llama.x + w * 0.034f, top)
        quadraticTo(llama.x + w * 0.02f, top + (piso - top) * 0.15f, llama.x + w * 0.016f, piso - (piso - top) * 0.55f)
        quadraticTo(llama.x + w * 0.018f, piso - (piso - top) * 0.3f, llama.x + w * 0.04f, piso)
        close()
    }
    val barro = a.luz(BARRO, if (a.lumbre) 0.75f else k)
    drawPath(pieB, barro)
    redondoEgipto(pieB, Rect(llama.x - w * 0.04f, top, llama.x + w * 0.04f, piso), a, 0.8f, -1f)
    if (a.lumbre) {
        // La llama alumbra desde arriba: el pie se enciende arriba y se hunde abajo.
        clipPath(pieB) { drawRect(a.filo(0.35f), Offset(llama.x - w * 0.04f, top), Size(w * 0.08f, (piso - top) * 0.25f)) }
    }
    // El cuenco de grasa, con el pico de la mecha.
    val cuenco = Rect(llama.x - w * 0.042f, llama.y + w * 0.002f, llama.x + w * 0.042f, top + w * 0.004f)
    drawArc(a.luz(mezcla(BARRO, PAPIRO, 0.15f), if (a.lumbre) 0.95f else 1f), 0f, 180f, true, Offset(cuenco.left, cuenco.top - cuenco.height), Size(cuenco.width, cuenco.height * 2))
    drawOval(a.luz(mezcla(BARRO, PALMA_OSC, 0.4f), if (a.lumbre) 0.9f else 1f), Offset(cuenco.left, cuenco.top - cuenco.height * 0.3f), Size(cuenco.width, cuenco.height * 0.6f))
    drawLine(a.luz(if (a.lumbre) LINO else PALMA_OSC, 1f), Offset(llama.x - w * 0.012f, cuenco.top), Offset(llama.x, llama.y - 1f * u), 1.6f * u)
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
                drawCircle(a.luz(if (j % 3 == 0) FAYENZA else if (j % 3 == 1) EgiptoTinta.ROJO else GRANO, cerca, frio), 1.6f * u, q)
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

/** Gira un dibujo alrededor de [c] solo si hace falta. */
internal inline fun DrawScope.giradoEgipto(grados: Float, c: Offset, crossinline dibujo: DrawScope.() -> Unit) {
    if (grados == 0f) dibujo() else rotate(grados, c) { dibujo() }
}
