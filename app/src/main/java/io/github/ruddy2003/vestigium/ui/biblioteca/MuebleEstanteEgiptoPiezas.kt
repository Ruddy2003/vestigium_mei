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
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AMARILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BRONCE_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CARBON
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.CEBOLLA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.COBRE
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FAYENZA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.GRANO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.MARGA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAN
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PIEDRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.VERDIN
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

// Lo de arriba del almacén de la Tumba (MuebleEstanteEgipto): el bodegón sobre el tablón alto, lo colgado del muro y lo que
// guardan los tramos libres.

/** Una estaca de madera hincada en el muro, con su sombra. */
private fun DrawScope.estacaMuroEgipto(m: MesaEgipto, c: Offset) {
    val u = density
    val a = m.a
    val k = m.kAlto(c); val f = m.f(c)
    drawCircle(a.s(0.4f), 3.4f * u, c + Offset(1.8f * u, 1.8f * u))
    drawCircle(a.c(ACACIA_OSC, k, f), 3.2f * u, c)
    drawCircle(a.c(ACACIA, k, f), 2.1f * u, c - Offset(0.6f * u, 0.6f * u))
}

/**
 * Lo colgado del muro: a la izquierda, dentro del sol, la hoz de madera con sus dientes de sílex (su sombra cae en la mancha
 * de luz); a la derecha, junto al borde y cortada por él, la ristra de cebollas de la ración.
 */
internal fun DrawScope.colgadoEgipto(m: MesaEgipto) {
    hozEgipto(m, Offset(m.w * 0.062f, m.w * 0.05f))
    cebollasEgipto(m, Offset(m.w * 0.955f, m.ch * 0.2f))
}

/** La hoz de madera, colgada de su estaca por un cordel: el mango, la hoja curva y los dientes de sílex en el filo de dentro. */
private fun DrawScope.hozEgipto(m: MesaEgipto, clavo: Offset) {
    val u = density
    val w = m.w * 1.25f; val a = m.a
    val c = clavo + Offset(w * 0.01f, w * 0.1f)
    val k = m.kAlto(c); val f = m.f(c)
    // El mango, casi vertical, y la hoja que se curva hacia la derecha y abajo.
    val mango = poligonoEgipto(clavo.x - w * 0.006f, clavo.y + w * 0.03f, clavo.x + w * 0.008f, clavo.y + w * 0.03f, clavo.x + w * 0.012f, clavo.y + w * 0.1f, clavo.x - w * 0.004f, clavo.y + w * 0.1f)
    val hoja = Path().apply {
        moveTo(clavo.x - w * 0.006f, clavo.y + w * 0.1f)
        cubicTo(clavo.x - w * 0.01f, clavo.y + w * 0.17f, clavo.x + w * 0.06f, clavo.y + w * 0.22f, clavo.x + w * 0.11f, clavo.y + w * 0.16f)
        cubicTo(clavo.x + w * 0.065f, clavo.y + w * 0.19f, clavo.x + w * 0.018f, clavo.y + w * 0.16f, clavo.x + w * 0.012f, clavo.y + w * 0.1f)
        close()
    }
    val todo = Path().apply { addPath(mango); addPath(hoja) }
    sombraMuroEgipto(m, todo, c, 0.24f)
    // El cordel que la cuelga.
    drawLine(a.c(FIBRA, k, f), clavo, Offset(clavo.x + w * 0.001f, clavo.y + w * 0.032f), 1.2f * u)
    drawPath(mango, a.c(mezcla(ACACIA, ROJO, 0.2f), k, f))
    drawRect(a.b(0.35f, k), Offset(clavo.x - w * 0.004f, clavo.y + w * 0.03f), Size(w * 0.004f, w * 0.07f))
    // Las vueltas de cuero en la empuñadura.
    for (j in 0 until 4) drawLine(a.c(mezcla(CARBON, ACACIA_OSC, 0.4f), k, f), Offset(clavo.x - w * 0.007f, clavo.y + w * (0.045f + 0.012f * j)), Offset(clavo.x + w * 0.01f, clavo.y + w * (0.05f + 0.012f * j)), 1.6f * u)
    drawPath(hoja, a.c(ACACIA_OSC, k, f))
    clipPath(hoja) {
        drawRect(a.b(0.3f, k), Offset(clavo.x - w * 0.02f, clavo.y + w * 0.1f), Size(w * 0.04f, w * 0.12f))
        vetas(Rect(clavo.x - w * 0.02f, clavo.y + w * 0.1f, clavo.x + w * 0.12f, clavo.y + w * 0.22f), a.s(0.25f), 731, 0.6f, grosor = 0.5f)
    }
    // Los dientes de sílex en el filo de dentro.
    for (j in 0 until 7) {
        val t = 0.15f + j * 0.12f
        val x = clavo.x + w * (0.014f + 0.09f * t)
        val y = clavo.y + w * (0.1f + 0.075f * kotlin.math.sin(t * 2.8f))
        drawPath(poligonoEgipto(x, y, x + w * 0.008f, y - w * 0.004f, x + w * 0.004f, y + w * 0.006f), a.c(mezcla(PIEDRA, PAPIRO, 0.45f), k, f))
    }
    estacaMuroEgipto(m, clavo)
}

/** La ristra de cebollas de la ración, colgada por sus tallos trenzados de una estaca junto al borde, cortada por él. */
private fun DrawScope.cebollasEgipto(m: MesaEgipto, clavo: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val k = m.kAlto(clavo + Offset(0f, w * 0.1f)); val f = m.f(clavo)
    val trenza = Path().apply { moveTo(clavo.x, clavo.y); quadraticTo(clavo.x - 4f * u, clavo.y + w * 0.06f, clavo.x - 2f * u, clavo.y + w * 0.1f) }
    val bulto = Path()
    val centros = listOf(-0.024f to 0.11f, 0.014f to 0.118f, -0.038f to 0.148f, 0.0f to 0.152f, 0.034f to 0.15f, -0.018f to 0.186f, 0.018f to 0.19f)
    centros.forEach { (dx, dy) -> bulto.addOval(Rect(clavo.x + w * dx - w * 0.024f, clavo.y + w * dy - w * 0.032f, clavo.x + w * dx + w * 0.024f, clavo.y + w * dy + w * 0.028f)) }
    sombraMuroEgipto(m, bulto, clavo + Offset(0f, w * 0.15f), 0.22f)
    drawPath(trenza, a.c(mezcla(PAPIRO, FIBRA, 0.5f), k, f), style = Stroke(3.4f * u, cap = StrokeCap.Round))
    drawPath(trenza, a.s(0.3f), style = Stroke(0.8f * u))
    centros.forEachIndexed { j, (dx, dy) ->
        val c = Offset(clavo.x + w * dx, clavo.y + w * dy)
        val r = w * (0.022f + 0.005f * azar(j, 71))
        val forma = Path().apply {
            moveTo(c.x, c.y - r * 1.5f)
            quadraticTo(c.x + r * 1.25f, c.y - r * 0.6f, c.x + r * 0.95f, c.y + r * 0.45f)
            quadraticTo(c.x, c.y + r * 1.25f, c.x - r * 0.95f, c.y + r * 0.45f)
            quadraticTo(c.x - r * 1.25f, c.y - r * 0.6f, c.x, c.y - r * 1.5f)
            close()
        }
        drawPath(forma, a.c(mezcla(CEBOLLA, PAPIRO, 0.22f * azar(j, 72)), k, f))
        clipPath(forma) {
            cilindroEgipto(Rect(c.x - r, c.y - r * 1.5f, c.x + r, c.y + r * 1.25f), a, k, 1.2f, m.der(c.x), f)
            drawPath(Path().apply {
                moveTo(c.x - r * 0.5f, c.y - r * 0.9f); quadraticTo(c.x - r * 0.8f, c.y, c.x - r * 0.4f, c.y + r * 0.8f)
                moveTo(c.x + r * 0.3f, c.y - r * 1f); quadraticTo(c.x + r * 0.6f, c.y, c.x + r * 0.35f, c.y + r * 0.85f)
            }, a.s(0.25f), style = Stroke(0.6f * u))
        }
        destelloEgipto(Offset(c.x - r * 0.4f, c.y - r * 0.4f), 1.1f * u, a, k)
    }
    estacaMuroEgipto(m, clavo)
}

/**
 * El bodegón sobre el tablón alto, de atrás adelante: la tinaja alta de la derecha (detrás del cesto), la tinaja grande de la
 * izquierda con la chica delante, la balanza del cobre que manda, sus pesas, y el cesto de palma teñida con los panes.
 */
internal fun DrawScope.bodegonEgipto(m: MesaEgipto) {
    val w = m.w
    val y = m.apoyo
    tinajaEgipto(m, Offset(w * 0.815f, y - w * 0.006f), w * 0.27f, 2)
    tinajaEgipto(m, Offset(w * 0.17f, y - w * 0.004f), w * 0.25f, 0)
    tinajaEgipto(m, Offset(w * 0.29f, y + w * 0.008f), w * 0.14f, 1)
    balanzaEgipto(m, Offset(w * 0.49f, y))
    cestoEgipto(m, Offset(w * 0.69f, y + w * 0.01f))
}

/**
 * Una tinaja de marga crema, pintada con bandas de azul y rojo como la loza de la época (sin figuras ni signos): según
 * [motivo], la franja azul del hombro con su festón de pétalos y rayas abajo (0), la boca tapada con un paño de lino atado
 * y un zigzag (1), o la franja roja con filetes azules y su tapón de barro (2). Con el plano de luz, el de sombra, el reflejo
 * del lado oscuro, el brillo de la marga alisada, su sombra en el muro y en el tablón.
 */
private fun DrawScope.tinajaEgipto(m: MesaEgipto, pie: Offset, alto: Float, motivo: Int) {
    val u = density
    val a = m.a
    val r = alto * 0.36f
    val c = Offset(pie.x, pie.y - alto * 0.5f)
    val k = m.kAlto(c); val f = m.f(c)
    val der = m.der(pie.x)
    val cuerpo = Path().apply {
        moveTo(pie.x - r * 0.4f, pie.y)
        cubicTo(pie.x - r * 1.18f, pie.y - alto * 0.22f, pie.x - r * 1.12f, pie.y - alto * 0.72f, pie.x - r * 0.5f, pie.y - alto * 0.86f)
        lineTo(pie.x - r * 0.4f, pie.y - alto * 0.94f)
        lineTo(pie.x + r * 0.4f, pie.y - alto * 0.94f)
        lineTo(pie.x + r * 0.5f, pie.y - alto * 0.86f)
        cubicTo(pie.x + r * 1.12f, pie.y - alto * 0.72f, pie.x + r * 1.18f, pie.y - alto * 0.22f, pie.x + r * 0.4f, pie.y)
        close()
    }
    val caja = Rect(pie.x - r * 1.1f, pie.y - alto * 0.94f, pie.x + r * 1.1f, pie.y)
    sombraMuroEgipto(m, cuerpo, c, 0.22f)
    contactoEgipto(Offset(pie.x + r * 0.25f, pie.y), r * 2.3f, m.w * 0.012f, a, 0.42f)
    drawPath(cuerpo, a.c(MARGA, k, f))
    clipPath(cuerpo) {
        val azul = a.c(AZUL, k, f); val rojo = a.c(ROJO, k, f); val negro = a.c(CARBON, k, f)
        when (motivo) {
            0 -> {
                val y0 = pie.y - alto * 0.78f; val y1 = pie.y - alto * 0.66f
                drawRect(azul, Offset(caja.left, y0), Size(caja.width, y1 - y0))
                var x = caja.left + r * 0.1f
                while (x < caja.right) { drawOval(azul, Offset(x - r * 0.1f, y1 - r * 0.1f), Size(r * 0.2f, r * 0.32f)); x += r * 0.25f }
                drawRect(negro, Offset(caja.left, y0 - 1.6f * u), Size(caja.width, 1.2f * u))
                drawRect(rojo, Offset(caja.left, y1 + r * 0.26f), Size(caja.width, 1.4f * u))
                val y2 = pie.y - alto * 0.4f
                drawRect(rojo, Offset(caja.left, y2), Size(caja.width, 1.6f * u))
                drawRect(azul, Offset(caja.left, y2 + 3.4f * u), Size(caja.width, 3.4f * u))
                drawRect(rojo, Offset(caja.left, y2 + 8.4f * u), Size(caja.width, 1.2f * u))
            }
            1 -> {
                val y2 = pie.y - alto * 0.5f
                val zig = Path()
                var zx = caja.left; var arriba = true
                zig.moveTo(zx, y2)
                while (zx < caja.right) { zx += r * 0.24f; arriba = !arriba; zig.lineTo(zx, if (arriba) y2 else y2 + r * 0.24f) }
                drawPath(zig, azul, style = Stroke(2.4f * u))
                drawRect(rojo, Offset(caja.left, y2 - r * 0.16f), Size(caja.width, 1.4f * u))
                drawRect(rojo, Offset(caja.left, y2 + r * 0.38f), Size(caja.width, 1.4f * u))
            }
            else -> {
                val y0 = pie.y - alto * 0.7f
                drawRect(rojo, Offset(caja.left, y0), Size(caja.width, alto * 0.08f))
                drawRect(azul, Offset(caja.left, y0 - 2.6f * u), Size(caja.width, 1.6f * u))
                drawRect(azul, Offset(caja.left, y0 + alto * 0.08f + 1f * u), Size(caja.width, 1.6f * u))
                // Pétalos colgando de la franja roja, en negro fino.
                var x = caja.left + r * 0.15f
                while (x < caja.right) { drawArc(negro, 0f, 180f, false, Offset(x - r * 0.09f, y0 + alto * 0.08f + 3f * u), Size(r * 0.18f, r * 0.22f), style = Stroke(0.8f * u)); x += r * 0.24f }
                for (j in 0 until 3) drawRect(azul.copy(alpha = 0.8f), Offset(caja.left, pie.y - alto * (0.28f - 0.05f * j)), Size(caja.width, 1.2f * u))
            }
        }
        // Las marcas del torno, el bulto, el reflejo del lado oscuro y lo gastado de la pintura.
        for (j in 0 until 5) drawLine(a.s(0.08f), Offset(caja.left, pie.y - alto * (0.15f + 0.12f * j)), Offset(caja.right, pie.y - alto * (0.15f + 0.12f * j)), 0.6f * u)
        cilindroEgipto(caja, a, k, 1.35f, der, f)
        if (!a.oscuro) drawRect(a.b(0.12f), Offset(if (der) caja.left + caja.width * 0.04f else caja.right - caja.width * 0.1f, caja.top), Size(caja.width * 0.05f, caja.height))
        filoEgipto(caja, a, k, der)
    }
    // El brillo de la marga alisada.
    destelloEgipto(Offset(if (der) pie.x + r * 0.55f else pie.x - r * 0.55f, pie.y - alto * 0.62f), 1.6f * u, a, k)
    // La boca: el labio, el cuello y lo que la tapa.
    val boca = Rect(pie.x - r * 0.48f, pie.y - alto - r * 0.06f, pie.x + r * 0.48f, pie.y - alto * 0.92f)
    drawOval(a.c(mezcla(MARGA, PALMA_OSC, 0.2f), k, f), boca.topLeft, boca.size)
    drawArc(a.b(0.4f, k), 180f, 140f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
    when (motivo) {
        1 -> {
            val pano = Path().apply {
                moveTo(boca.left - r * 0.12f, boca.bottom + r * 0.14f)
                quadraticTo(pie.x, boca.top - r * 0.4f, boca.right + r * 0.12f, boca.bottom + r * 0.14f)
                lineTo(boca.right + r * 0.04f, boca.bottom + r * 0.4f); lineTo(boca.left + r * 0.02f, boca.bottom + r * 0.36f); close()
            }
            drawPath(pano, a.c(LINO, k, f))
            clipPath(pano) { drawRect(a.s(0.22f), Offset(pie.x + r * 0.1f, boca.top - r * 0.4f), Size(r, r)) }
            drawLine(a.c(FIBRA, k, f), Offset(boca.left - r * 0.06f, boca.bottom + r * 0.2f), Offset(boca.right + r * 0.06f, boca.bottom + r * 0.22f), 1.6f * u)
            drawLine(a.c(FIBRA, k, f), Offset(boca.right + r * 0.02f, boca.bottom + r * 0.22f), Offset(boca.right + r * 0.18f, boca.bottom + r * 0.5f), 1f * u)
        }
        else -> {
            val tapon = Path().apply {
                moveTo(boca.left + r * 0.02f, boca.center.y)
                cubicTo(boca.left, boca.top - r * 0.42f, boca.right, boca.top - r * 0.46f, boca.right - r * 0.02f, boca.center.y)
                close()
            }
            drawPath(tapon, a.c(mezcla(BARRO, PALMA_OSC, if (motivo == 0) 0.15f else 0.35f), k, f))
            clipPath(tapon) {
                drawRect(a.b(0.3f, k), Offset(boca.left, boca.top - r * 0.5f), Size(boca.width * 0.4f, r * 0.6f))
                drawRect(a.s(0.25f), Offset(boca.left + boca.width * 0.62f, boca.top - r * 0.5f), Size(boca.width * 0.4f, r * 0.6f))
                listOf(-0.2f, 0.15f).forEach { fx -> drawOval(a.s(0.3f), Offset(pie.x + r * fx, boca.top - r * 0.2f), Size(r * 0.2f, r * 0.08f)) }
            }
        }
    }
}

/**
 * La balanza del cobre, que manda sobre el anaquel: el pie de madera con su veta, el poste con su remate, el astil de madera con
 * grueso, un poco caído hacia el platillo de los cinceles, los platillos de bronce con su brillo colgados de tres cordeles,
 * la plomada del fiel y las pesas de piedra al lado.
 */
private fun DrawScope.balanzaEgipto(m: MesaEgipto, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val alto = w * 0.36f
    val cM = Offset(pie.x, pie.y - alto * 0.5f)
    val k = m.kAlto(cM); val f = m.f(cM)
    val der = m.der(pie.x)
    val zocalo = Rect(pie.x - w * 0.065f, pie.y - w * 0.034f, pie.x + w * 0.065f, pie.y)
    val poste = Rect(pie.x - w * 0.014f, zocalo.top - alto, pie.x + w * 0.014f, zocalo.top)
    val eje = Offset(pie.x, poste.top - w * 0.008f)
    val brazo = w * 0.18f
    val caida = 0.07f
    val pIzq = Offset(eje.x - brazo, eje.y + brazo * caida)
    val pDer = Offset(eje.x + brazo, eje.y - brazo * caida)
    val g = w * 0.009f
    val astil = poligonoEgipto(pIzq.x, pIzq.y - g, pDer.x, pDer.y - g, pDer.x, pDer.y + g, pIzq.x, pIzq.y + g)
    // Su sombra en el muro: el poste, el astil y los platillos.
    val silueta = Path().apply {
        addRect(poste); addPath(astil)
        addOval(Rect(pIzq.x - w * 0.07f, pIzq.y + w * 0.13f, pIzq.x + w * 0.07f, pIzq.y + w * 0.17f))
        addOval(Rect(pDer.x - w * 0.07f, pDer.y + w * 0.13f, pDer.x + w * 0.07f, pDer.y + w * 0.17f))
    }
    sombraMuroEgipto(m, silueta, cM, 0.22f)
    contactoEgipto(Offset(pie.x + w * 0.012f, pie.y), w * 0.17f, w * 0.014f, a, 0.42f)
    // El pie y el poste, de madera con su veta.
    drawRect(a.c(ACACIA, k, f), zocalo.topLeft, zocalo.size)
    vetas(zocalo, a.c(ACACIA_OSC, k).copy(alpha = 0.35f), 741, 0.7f, grosor = 0.6f)
    drawRect(a.c(ACACIA_LUZ, k, f), zocalo.topLeft, Size(zocalo.width, 2f * u))
    drawRect(a.b(0.5f, k), zocalo.topLeft, Size(zocalo.width, 0.9f * u))
    drawRect(a.s(0.32f), Offset(zocalo.right - zocalo.width * 0.22f, zocalo.top), Size(zocalo.width * 0.22f, zocalo.height))
    drawRect(a.c(ACACIA, k, f), poste.topLeft, poste.size)
    vetas(poste, a.c(ACACIA_OSC, k).copy(alpha = 0.35f), 742, 0.8f, vertical = true, grosor = 0.6f)
    cilindroEgipto(poste, a, k, 1.2f, der, f)
    filoEgipto(poste, a, k, der)
    // El remate del poste, donde pivota el astil.
    val remate = Rect(poste.left - w * 0.008f, poste.top - w * 0.024f, poste.right + w * 0.008f, poste.top + w * 0.006f)
    drawRoundRect(a.c(ACACIA_OSC, k, f), remate.topLeft, remate.size, CornerRadius(2f * u))
    drawRect(a.b(0.4f, k), Offset(remate.left + 1f * u, remate.top + 1f * u), Size(remate.width * 0.3f, remate.height - 2f * u))
    // El astil, con grueso: su cara de frente, su canto de arriba con luz y su sombra abajo; los regatones de bronce.
    drawPath(astil, a.c(ACACIA, k, f))
    clipPath(astil) {
        drawRect(a.c(ACACIA_LUZ, k, f), Offset(pIzq.x, minOf(pIzq.y, pDer.y) - g), Size(pDer.x - pIzq.x, g * 0.9f))
        drawRect(a.s(0.3f), Offset(pIzq.x, maxOf(pIzq.y, pDer.y) + g * 0.2f), Size(pDer.x - pIzq.x, g))
    }
    drawLine(a.b(0.5f, k), Offset(pIzq.x, pIzq.y - g * 0.75f), Offset(pDer.x, pDer.y - g * 0.75f), 0.8f * u)
    listOf(pIzq, pDer).forEach { p ->
        drawCircle(a.c(BRONCE, k, f), g * 1.5f, p)
        drawCircle(a.c(BRONCE_LUZ, k, f), g * 0.6f, p - Offset(g * 0.45f, g * 0.45f))
    }
    drawCircle(a.c(BRONCE, k, f), g * 1.2f, eje)
    destelloEgipto(eje - Offset(g * 0.4f, g * 0.4f), 1f * u, a, k)
    // La plomada del fiel.
    val plomo = Offset(eje.x + w * 0.024f, eje.y + w * 0.11f)
    drawLine(a.c(LINO, k, f), Offset(eje.x + w * 0.024f, eje.y + g), plomo, 0.8f * u)
    drawPath(poligonoEgipto(plomo.x - 2.8f * u, plomo.y, plomo.x + 2.8f * u, plomo.y, plomo.x, plomo.y + 8f * u), a.c(PIEDRA, k, f))
    drawLine(a.b(0.4f, k), Offset(plomo.x - 2f * u, plomo.y + 0.5f * u), Offset(plomo.x - 0.4f * u, plomo.y + 5f * u), 0.7f * u)
    platilloEgipto(m, pIzq, 0)
    platilloEgipto(m, pDer, 1)
    // Las pesas de piedra sobre el tablón, de mayor a menor.
    var x = pie.x + w * 0.08f
    listOf(1f, 0.72f, 0.5f).forEachIndexed { j, s ->
        val rr = w * 0.032f * s
        val p0 = Offset(x + rr, pie.y)
        contactoEgipto(Offset(p0.x + rr * 0.3f, pie.y), rr * 2.4f, 3f * u, a)
        val forma = Path().apply { addArc(Rect(p0.x - rr, p0.y - rr * 1.25f, p0.x + rr, p0.y + rr * 1.25f), 180f, 180f); close() }
        drawPath(forma, a.c(mezcla(PIEDRA, PALMA_OSC, 0.1f * j), k, f))
        clipPath(forma) { cilindroEgipto(Rect(p0.x - rr, p0.y - rr * 1.25f, p0.x + rr, p0.y), a, k, 1.3f, der, f) }
        destelloEgipto(Offset(p0.x - rr * 0.4f, p0.y - rr * 0.8f), 0.9f * u, a, k)
        x += rr * 2 + 2.5f * u
    }
}

/** Un platillo de bronce colgado de tres cordeles: con los cinceles de cobre ([carga] 0) o con dos pesas (1). */
private fun DrawScope.platilloEgipto(m: MesaEgipto, cuelga: Offset, carga: Int) {
    val u = density
    val w = m.w; val a = m.a
    val c = Offset(cuelga.x, cuelga.y + w * 0.14f)
    val k = m.kAlto(c); val f = m.f(c)
    val r = w * 0.07f
    val cord = Path().apply {
        moveTo(cuelga.x, cuelga.y); lineTo(c.x - r * 0.96f, c.y)
        moveTo(cuelga.x, cuelga.y); lineTo(c.x + r * 0.15f, c.y - r * 0.14f)
        moveTo(cuelga.x, cuelga.y); lineTo(c.x + r * 0.96f, c.y)
    }
    drawPath(cord, a.c(mezcla(LINO, FIBRA, 0.4f), k, f), style = Stroke(0.9f * u))
    if (carga == 0) {
        // Los cinceles de cobre que se pesan, con el filo claro y alguna mancha de verdín.
        for (j in 0 until 4) {
            val y = c.y - 2.4f * u - j * 2.6f * u
            val x0 = c.x - r * 0.8f + j * 1.8f * u
            val x1 = x0 + r * 1.35f
            drawLine(a.s(0.3f), Offset(x0 + 1f * u, y + 1.2f * u), Offset(x1 + 1f * u, y + 1.2f * u), 3f * u)
            drawLine(a.c(mezcla(COBRE, PALMA_OSC, 0.12f * j), k, f), Offset(x0, y), Offset(x1, y), 2.8f * u, cap = StrokeCap.Square)
            drawLine(a.c(mezcla(COBRE, BRONCE_LUZ, 0.6f), k, f), Offset(x1 - 3.4f * u, y - 0.7f * u), Offset(x1 + 1f * u, y - 0.7f * u), 1.2f * u)
            drawLine(a.b(0.4f, k), Offset(x0, y - 0.9f * u), Offset(x1 - 4f * u, y - 0.9f * u), 0.6f * u)
            drawCircle(a.c(VERDIN, k, f), 1.2f * u, Offset(x0 + r * 0.4f * (j + 1) / 2f, y))
        }
    } else {
        listOf(-0.4f to 0.85f, 0.35f to 0.62f).forEach { (dx, s) ->
            val rr = r * 0.44f * s
            val p0 = Offset(c.x + r * dx, c.y - 1f * u)
            val pesa = Path().apply { addArc(Rect(p0.x - rr, p0.y - rr, p0.x + rr, p0.y + rr), 180f, 180f); close() }
            drawPath(pesa, a.c(PIEDRA, k, f))
            clipPath(pesa) { cilindroEgipto(Rect(p0.x - rr, p0.y - rr, p0.x + rr, p0.y), a, k, 1.3f, luna = f) }
        }
    }
    // El platillo: el cuenco de bronce con su luz, su sombra, el labio y el brillo.
    val plato = Rect(c.x - r, c.y - r * 0.22f, c.x + r, c.y + r * 0.42f)
    val cuenco = Path().apply { addArc(plato, 0f, 180f); close() }
    drawPath(cuenco, a.c(BRONCE, k, f))
    clipPath(cuenco) { cilindroEgipto(plato, a, k, 1.4f, m.der(c.x), f) }
    drawOval(a.c(mezcla(BRONCE, BRONCE_LUZ, 0.45f), k, f), Offset(plato.left, plato.top - r * 0.08f), Size(plato.width, r * 0.32f))
    drawOval(a.c(mezcla(BRONCE, PALMA_OSC, 0.3f), k, f), Offset(plato.left, plato.top - r * 0.08f), Size(plato.width, r * 0.32f), style = Stroke(1f * u))
    drawArc(a.c(BRONCE_LUZ, k, f), 120f, 50f, false, Offset(plato.left + r * 0.08f, plato.top - r * 0.3f), Size(plato.width * 0.9f, r * 0.95f), style = Stroke(1.6f * u))
    destelloEgipto(Offset(plato.left + r * 0.35f, plato.top + r * 0.2f), 1.3f * u, a, k)
    if (a.oscuro && f > 0.1f) drawArc(a.helado(0.6f, f), 180f, 60f, false, plato.topLeft, plato.size, style = Stroke(1.2f * u))
}

/** El cesto de palma con dos franjas de palma teñida (roja y verde, en damero), lleno de panes cónicos de la ración. */
private fun DrawScope.cestoEgipto(m: MesaEgipto, pie: Offset) {
    val u = density
    val w = m.w; val a = m.a
    val an = w * 0.17f; val al = w * 0.11f
    val c = Offset(pie.x, pie.y - al * 0.6f)
    val k = m.kAlto(c); val f = m.f(c)
    val der = m.der(pie.x)
    val cuerpo = poligonoEgipto(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y - al, pie.x + an * 0.43f, pie.y, pie.x - an * 0.43f, pie.y)
    val caja = Rect(pie.x - an / 2, pie.y - al, pie.x + an / 2, pie.y)
    // Los panes que asoman, con su sombra en el muro.
    val panes = listOf(Triple(-0.038f, 0.085f, 0.024f), Triple(0.006f, 0.11f, 0.026f), Triple(0.046f, 0.078f, 0.022f))
    val siluetaPanes = Path()
    panes.forEach { (dx, alto, r) ->
        val b = Offset(pie.x + w * dx, pie.y - al + 3f * u)
        siluetaPanes.addPath(poligonoEgipto(b.x - w * r, b.y, b.x, b.y - w * alto, b.x + w * r, b.y))
    }
    sombraMuroEgipto(m, Path().apply { addPath(cuerpo); addPath(siluetaPanes) }, c, 0.22f)
    contactoEgipto(Offset(pie.x + w * 0.012f, pie.y), an * 1.2f, w * 0.012f, a, 0.42f)
    panes.forEachIndexed { j, (dx, alto, r) ->
        val b = Offset(pie.x + w * dx, pie.y - al + 3f * u)
        val pan = Path().apply {
            moveTo(b.x - w * r, b.y)
            quadraticTo(b.x - w * r * 0.6f, b.y - w * alto * 0.7f, b.x + w * 0.003f, b.y - w * alto)
            quadraticTo(b.x + w * r * 0.7f, b.y - w * alto * 0.6f, b.x + w * r, b.y)
            close()
        }
        drawPath(pan, a.c(mezcla(PAN, GRANO, 0.22f * j), k, f))
        clipPath(pan) {
            cilindroEgipto(Rect(b.x - w * r, b.y - w * alto, b.x + w * r, b.y), a, k, 1.3f, der, f)
            drawLine(a.s(0.3f), Offset(b.x - w * r * 0.3f, b.y - w * alto * 0.45f), Offset(b.x + w * r * 0.2f, b.y - w * alto * 0.55f), 0.7f * u)
        }
        drawCircle(a.c(PAPIRO, k, f).copy(alpha = 0.6f), 1.5f * u, Offset(b.x + w * 0.002f, b.y - w * alto * 0.88f))
    }
    drawPath(cuerpo, a.c(FIBRA, k, f))
    clipPath(cuerpo) {
        // Las vueltas de la espiral cosida: luz arriba, sombra abajo en cada vuelta.
        val paso = 3.2f * u
        var y = caja.top
        val luces = Path(); val sombras = Path()
        while (y < caja.bottom) { luces.moveTo(caja.left, y + 0.6f * u); luces.lineTo(caja.right, y + 0.6f * u); sombras.moveTo(caja.left, y + paso - 0.6f * u); sombras.lineTo(caja.right, y + paso - 0.6f * u); y += paso }
        // Las dos franjas de palma teñida, en damero.
        listOf(0.26f to ROJO, 0.58f to VERDIN).forEach { (t, col) ->
            val yb = caja.top + al * t
            var x = caja.left; var n = 0
            while (x < caja.right) {
                drawRect(a.c(col, k, f), Offset(x, yb + (if (n % 2 == 0) 0f else paso)), Size(4.4f * u, paso))
                x += 4.4f * u; n++
            }
        }
        drawPath(luces, a.b(0.3f, k), style = Stroke(0.9f * u))
        drawPath(sombras, a.s(0.25f), style = Stroke(1f * u))
        cilindroEgipto(caja, a, k, 1.2f, der, f)
        filoEgipto(caja, a, k, der)
    }
    // El borde, con su grueso.
    drawRoundRect(a.c(mezcla(FIBRA, PALMA_OSC, 0.3f), k, f), Offset(caja.left - 1.2f * u, caja.top - 2f * u), Size(an + 2.4f * u, 4f * u), CornerRadius(2f * u))
    drawRect(a.b(0.45f, k), Offset(caja.left, caja.top - 1.6f * u), Size(an, 0.9f * u))
}

/**
 * Lo que guarda un tramo libre, según la repisa: un rollo de mechas de lino nuevas, el cuenco de fayenza con cuentas, una pila
 * de lino doblado con sus orillas teñidas, o una tinajilla con sus pesas.
 */
internal fun DrawScope.huecoLibreEgipto(m: MesaEgipto, i: Int, r: Rect, oc: Float) {
    val u = density
    val x1 = m.posteD.left - m.w * 0.05f
    val libre = x1 - oc - 6f * u
    if (libre < 30f * u || r.height < 30f * u) return
    // Por repisa, en ciclo: dos tramos libres seguidos nunca guardan lo mismo.
    val clase = i % 4
    val y = r.bottom - m.w * 0.008f
    if (libre > 170f * u) {
        // Mucho sitio: dos cosas, una cerca de los volúmenes y otra hacia el tronco, de clases distintas.
        objetoTramoEgipto(m, clase, i, r, Offset(oc + 12f * u + libre * 0.28f, y), minOf(libre * 0.4f, 84f * u))
        objetoTramoEgipto(m, (clase + 2) % 4, i + 7, r, Offset(oc + libre * 0.76f, y), minOf(libre * 0.36f, 80f * u))
    } else objetoTramoEgipto(m, clase, i, r, Offset(minOf(oc + 10f * u + libre * 0.5f, x1 - 26f * u), y), minOf(libre * 0.75f, 84f * u))
}

/** Una de las cosas de un tramo libre ([clase] de 0 a 3), con su pie en [pie] y de unos [an] de ancho. */
private fun DrawScope.objetoTramoEgipto(m: MesaEgipto, clase: Int, i: Int, r: Rect, pie: Offset, an: Float) {
    val u = density
    val a = m.a
    val c = Offset(pie.x, pie.y - r.height * 0.3f)
    val k = m.k(c); val f = m.f(c)
    val der = m.der(pie.x)
    when (clase) {
        0 -> {
            // Un rollo de mechas de lino nuevas, enroscado y atado, con una punta suelta.
            val rr = minOf(an * 0.4f, 30f * u)
            val o = Offset(pie.x, pie.y - rr)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.4f, 6f * u, a)
            val rueda = Path().apply { addOval(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr)) }
            drawPath(rueda, a.c(LINO, k, f))
            clipPath(rueda) {
                for (j in 1..4) drawCircle(a.s(0.22f), rr * (1f - j * 0.2f), o, style = Stroke(1.4f * u))
                for (j in 1..4) drawCircle(a.b(0.25f, k), rr * (1f - j * 0.2f) - 1.4f * u, o, style = Stroke(0.7f * u))
                cilindroEgipto(Rect(o.x - rr, o.y - rr, o.x + rr, o.y + rr), a, k, 1.1f, der, f)
            }
            drawLine(a.c(ROJO, k, f), Offset(o.x - rr * 0.2f, o.y - rr), Offset(o.x + rr * 0.1f, o.y + rr), 2f * u)
            drawLine(a.c(LINO, k, f), Offset(o.x + rr * 0.8f, o.y + rr * 0.6f), Offset(o.x + rr * 1.6f, pie.y - 0.8f * u), 2.4f * u, cap = StrokeCap.Round)
        }
        1 -> {
            // El cuenco de fayenza azul verdosa con cuentas de colores, y su pétalo pintado en negro.
            val rr = minOf(an * 0.5f, 38f * u)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.3f, 6f * u, a)
            val boca = Rect(pie.x - rr, pie.y - rr * 0.92f, pie.x + rr, pie.y - rr * 0.6f)
            for (j in 0 until 12) {
                val q = Offset(boca.left + boca.width * (0.12f + 0.76f * azar(j, 51)), boca.top - 1f * u - 4f * u * azar(j, 52))
                drawCircle(a.c(when (j % 4) { 0 -> AZUL; 1 -> ROJO; 2 -> AMARILLO; else -> FAYENZA }, k, f), 2f * u, q)
                drawCircle(a.b(0.5f, k), 0.7f * u, q - Offset(0.6f * u, 0.6f * u))
            }
            val cuenco = Path().apply {
                moveTo(boca.left, boca.center.y); quadraticTo(pie.x - rr * 0.8f, pie.y, pie.x, pie.y)
                quadraticTo(pie.x + rr * 0.8f, pie.y, boca.right, boca.center.y); close()
            }
            drawPath(cuenco, a.c(FAYENZA, k, f))
            clipPath(cuenco) {
                cilindroEgipto(Rect(boca.left, boca.top, boca.right, pie.y), a, k, 1.3f, der, f)
                listOf(-0.35f, 0f, 0.35f).forEach { dx -> drawOval(a.c(CARBON, k, f).copy(alpha = 0.7f), Offset(pie.x + rr * dx - rr * 0.08f, pie.y - rr * 0.55f), Size(rr * 0.16f, rr * 0.42f), style = Stroke(1f * u)) }
            }
            drawOval(a.c(mezcla(FAYENZA, CARBON, 0.4f), k, f), boca.topLeft, boca.size, style = Stroke(1.4f * u))
            drawArc(a.b(0.6f, k), 200f, 50f, false, Offset(boca.left + rr * 0.2f, boca.top), Size(rr * 1.6f, rr * 1.2f), style = Stroke(1.3f * u))
            destelloEgipto(Offset(pie.x - rr * 0.45f, pie.y - rr * 0.45f), 1.4f * u, a, k)
        }
        2 -> {
            // Una pila de lino doblado: cada pieza con su orilla teñida (roja, azul, amarilla) y sus pliegues.
            val pw = minOf(an, 64f * u)
            val ph = minOf(r.height * 0.15f, 13f * u)
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), pw * 1.15f, 6f * u, a)
            listOf(ROJO, AZUL, AMARILLO, AZUL).forEachIndexed { j, orilla ->
                val dx = (azar(i, j + 60) - 0.5f) * 5f * u
                val p = Rect(pie.x - pw / 2 + dx, pie.y - ph * (j + 1), pie.x + pw / 2 + dx, pie.y - ph * j)
                drawRoundRect(a.c(mezcla(LINO, PAPIRO, 0.2f * (j % 2)), k, f), p.topLeft, p.size, CornerRadius(ph * 0.45f))
                drawRect(a.c(orilla, k, f), Offset(p.left + ph * 0.3f, p.bottom - ph * 0.34f), Size(p.width - ph * 0.6f, ph * 0.16f))
                drawRect(a.b(0.4f, k), Offset(p.left + ph * 0.3f, p.top + 0.6f * u), Size(p.width * 0.6f, 0.9f * u))
                drawRect(a.s(0.22f), Offset(p.left, p.bottom - 1.2f * u), Size(p.width, 1.2f * u))
                drawRect(a.s(0.18f), Offset(p.right - p.width * 0.18f, p.top), Size(p.width * 0.18f, p.height))
            }
        }
        else -> {
            // Una tinajilla de marga con su banda azul, y dos pesas de piedra delante.
            val al = minOf(r.height * 0.72f, 80f * u)
            val rr = al * 0.36f
            val cu = Path().apply {
                moveTo(pie.x - rr * 0.4f, pie.y)
                cubicTo(pie.x - rr * 1.2f, pie.y - al * 0.25f, pie.x - rr * 1.1f, pie.y - al * 0.75f, pie.x - rr * 0.42f, pie.y - al * 0.92f)
                lineTo(pie.x + rr * 0.42f, pie.y - al * 0.92f)
                cubicTo(pie.x + rr * 1.1f, pie.y - al * 0.75f, pie.x + rr * 1.2f, pie.y - al * 0.25f, pie.x + rr * 0.4f, pie.y)
                close()
            }
            contactoEgipto(Offset(pie.x + 3f * u, pie.y), rr * 2.4f, 6f * u, a)
            drawPath(cu, a.c(MARGA, k, f))
            clipPath(cu) {
                drawRect(a.c(AZUL, k, f), Offset(pie.x - rr * 1.2f, pie.y - al * 0.7f), Size(rr * 2.4f, al * 0.1f))
                drawRect(a.c(ROJO, k, f), Offset(pie.x - rr * 1.2f, pie.y - al * 0.56f), Size(rr * 2.4f, 1.4f * u))
                cilindroEgipto(Rect(pie.x - rr * 1.1f, pie.y - al, pie.x + rr * 1.1f, pie.y), a, k, 1.3f, der, f)
            }
            drawOval(a.c(mezcla(MARGA, PALMA_OSC, 0.3f), k, f), Offset(pie.x - rr * 0.46f, pie.y - al * 0.97f), Size(rr * 0.92f, al * 0.08f))
            destelloEgipto(Offset(pie.x - rr * 0.5f, pie.y - al * 0.6f), 1.3f * u, a, k)
            listOf(1f, 0.7f).forEachIndexed { j, s ->
                val pr = rr * 0.6f * s
                val p0 = Offset(pie.x + rr * 1.3f + j * pr * 2.4f, pie.y)
                val pesa = Path().apply { addArc(Rect(p0.x - pr, p0.y - pr * 1.2f, p0.x + pr, p0.y + pr * 1.2f), 180f, 180f); close() }
                contactoEgipto(Offset(p0.x + pr * 0.3f, p0.y), pr * 2.4f, 3f * u, a)
                drawPath(pesa, a.c(PIEDRA, k, f))
                clipPath(pesa) { cilindroEgipto(Rect(p0.x - pr, p0.y - pr * 1.2f, p0.x + pr, p0.y), a, k, 1.3f, der, f) }
            }
        }
    }
}
