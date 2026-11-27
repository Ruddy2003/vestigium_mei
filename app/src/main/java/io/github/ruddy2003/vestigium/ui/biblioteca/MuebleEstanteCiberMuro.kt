package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CABLE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CIAN
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.sin

/**
 * La corona entera, de atrás adelante: la pared azul pizarra con lo que tiene colgado (el banderín impreso, el mazo de cables con
 * sus bridas, las notas, el enchufe y el rollo de cable), el remate de la estantería y el bodegón de encima.
 */
internal fun DrawScope.coronaCiber(g: MarcoCiber) {
    paredArribaCiber(g)
    banderinCiber(g)
    cablesParedCiber(g)
    notaParedCiber(g, Offset(g.w * 0.585f, g.w * 0.2f), g.w * 0.066f, 6f, 1)
    notaParedCiber(g, Offset(g.w * 0.07f, g.w * 0.27f), g.w * 0.06f, -7f, 2)
    enchufeCiber(g, Offset(g.w * 0.93f, g.w * 0.385f))
    rolloCableCiber(g, Offset(g.w * 0.905f, g.w * 0.165f))
    remateCiber(g)
    bodegonCiber(g)
}

/**
 * La pared azul pizarra de arriba: más clara cerca de la pantalla (la luz cae con la distancia), con pinceladas, el resto de cinta
 * de un cartel que ya no está, rozaduras y un agujero de clavo.
 */
private fun DrawScope.paredArribaCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val hasta = g.apoyo
    // La pared llega hasta el pie de la corona: asoma a los lados del remate.
    val fin = g.ch + 1f
    val pantalla = Offset(w * MuebleEstanteCiber.PANT_X, w * MuebleEstanteCiber.PANT_Y)
    // El degradado radial alrededor de la pantalla: cada parada, el color de la pared a esa distancia (hacia arriba y a la derecha).
    val paradas = Array(7) { i ->
        val f = i / 6f
        val p = pantalla + Offset(w * 1.05f * f * 0.8f, -w * 1.05f * f * 0.6f)
        f to g.t(p).c(PARED, 0.55f)
    }
    drawRect(Brush.radialGradient(*paradas, center = pantalla, radius = w * 1.05f), Offset.Zero, Size(w, fin))
    pinceladas(Rect(0f, 0f, w, hasta), NOCHE, 6101, 44, u, 0.16f)
    pinceladas(Rect(0f, 0f, w, hasta), CIAN, 6102, 12, u, 0.03f)
    // La pintura despareja: parches de otra mano de pintura, más claros y más oscuros, y una grieta fina que baja del techo.
    for (k in 0 until 6) {
        val c = Offset(w * (0.05f + 0.9f * azar(6106, k)), hasta * (0.15f + 0.75f * azar(6107, k)))
        val rx = w * (0.08f + 0.1f * azar(6108, k)); val ry = rx * (0.5f + 0.4f * azar(6109, k))
        val col = if (k % 2 == 0) g.t(c).c(mezcla(PARED, Color.White, 0.12f)) else NOCHE
        drawOval(col.copy(alpha = if (k % 2 == 0) 0.1f else 0.14f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
    }
    val grieta = Path().apply {
        moveTo(w * 0.47f, 0f); cubicTo(w * 0.46f, w * 0.03f, w * 0.485f, w * 0.05f, w * 0.475f, w * 0.085f)
        cubicTo(w * 0.47f, w * 0.1f, w * 0.49f, w * 0.12f, w * 0.483f, w * 0.15f)
    }
    drawPath(grieta, NOCHE.copy(alpha = 0.5f), style = Stroke(1.1f * u))
    translate(1f * u, 0.6f * u) { drawPath(grieta, g.t(w * 0.47f, w * 0.08f).b(0.25f), style = Stroke(0.6f * u)) }
    // El halo de la pantalla en la pared: tiñe de su color lo que tiene cerca.
    mancha(pantalla, w * 0.2f, g.a.colores[0].copy(alpha = if (g.a.noche) 0.07f else 0.08f))
    // El resto de cinta y el recuadro más limpio de un cartel que ya no está, a la izquierda del banderín.
    val r = Rect(w * 0.3f, w * 0.215f, w * 0.43f, w * 0.31f)
    val t = g.t(r.center)
    drawRect(t.c(mezcla(PARED, BEIS, 0.08f)).copy(alpha = 0.5f), r.topLeft, r.size)
    listOf(r.topLeft, Offset(r.right - w * 0.02f, r.top)).forEach { q ->
        drawRect(t.c(mezcla(PAPEL, NOTA, 0.3f)).copy(alpha = 0.45f), Offset(q.x - w * 0.004f, q.y - w * 0.006f), Size(w * 0.026f, w * 0.012f))
    }
    // El agujero del clavo y las rozaduras.
    drawCircle(NOCHE.copy(alpha = 0.6f), 1.4f * u, Offset(w * 0.76f, w * 0.31f))
    for (k in 0 until 5) {
        val x = w * (0.05f + 0.9f * azar(6103, k)); val y = w * (0.33f + 0.12f * azar(6104, k))
        drawLine(g.t(x, y).b(0.22f), Offset(x, y), Offset(x + w * (0.02f + 0.03f * azar(6105, k)), y - w * 0.004f), 0.7f * u)
    }
}

/** Dónde caen las cumbres del banderín (en fracción de su largo) y su alto en puntos. */
private val CUMBRES_CIBER = floatArrayOf(0.17f, 0.5f, 0.83f)
private val ALTOS_CIBER = floatArrayOf(5.2f, 6.6f, 4.4f)

/**
 * El banderín: una tira de papel continuo impresa con una escena de puntos (tres montes y estrellas, sin letras),
 * con sus márgenes de arrastre y los agujeros, las líneas de corte entre hojas, combada entre los tres trozos de cinta que la
 * sostienen. La tinta es la de la cinta de la impresora: un gris azulado, más gastado a la derecha.
 */
private fun DrawScope.banderinCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val x0 = w * 0.06f; val x1 = w * 0.79f
    val y0 = w * 0.04f; val alto = w * 0.088f
    val comba = w * 0.012f
    fun cae(x: Float): Float { val f = (x - x0) / (x1 - x0); return comba * sin(f * 2f * Math.PI.toFloat()).let { if (it < 0f) -it else it } }
    val tira = Path().apply {
        moveTo(x0, y0)
        var x = x0
        while (x < x1) { x = minOf(x1, x + w * 0.02f); lineTo(x, y0 + cae(x)) }
        lineTo(x1, y0 + alto + cae(x1))
        while (x > x0) { x = maxOf(x0, x - w * 0.02f); lineTo(x, y0 + alto + cae(x)) }
        close()
    }
    // La sombra en la pared, hacia fuera de la pantalla (abajo y a la derecha).
    translate(w * 0.012f, w * 0.01f) { drawPath(tira, NOCHE.copy(alpha = 0.42f)) }
    val pap = Array(6) { i -> val f = i / 5f; f to g.t(x0 + (x1 - x0) * f, y0 + alto / 2).c(PAPEL, 0.62f) }
    drawPath(tira, Brush.horizontalGradient(*pap, startX = x0, endX = x1))
    clipPath(tira) {
        val margen = w * 0.011f
        // Los agujeros de arrastre y el troquel de los márgenes.
        val agujeros = ArrayList<Offset>()
        var x = x0 + w * 0.008f
        while (x < x1) { agujeros.add(Offset(x, y0 + cae(x) + margen * 0.5f)); agujeros.add(Offset(x, y0 + cae(x) + alto - margen * 0.5f)); x += w * 0.0125f }
        drawPoints(agujeros, PointMode.Points, g.t(w * 0.4f, y0).c(mezcla(PARED, NOCHE, 0.3f)), 2.4f * u, StrokeCap.Round)
        // Las líneas de corte entre hojas.
        var xc = x0 + w * 0.18f
        while (xc < x1) {
            val tc = g.t(xc, y0)
            var yy = y0 + cae(xc)
            while (yy < y0 + cae(xc) + alto) { drawLine(tc.s(0.35f), Offset(xc, yy), Offset(xc, yy + 1.6f * u), 0.6f * u); yy += 3.6f * u }
            xc += w * 0.24f
        }
        // La escena de puntos: una sierra de montes rellena a medio tono (un punto sí y otro no), con su perfil y el suelo
        // enteros, y estrellas de un punto encima. Cada punto, un cuadrito de la cabeza de agujas, grande para que se lea de
        // lejos. Sin luna ni letras: una media luna de cuadritos se leería como una «C». Un solo trazo por tinta.
        val celda = w * 0.0092f
        val filas = 7
        val ytop = y0 + margen * 1.1f
        val cols = ((x1 - x0 - 2f * w * 0.008f) / celda).toInt().coerceIn(0, 240)
        val llenos = Path(); val tenues = Path()
        for (c in 0 until cols) {
            val xx = x0 + w * 0.008f + c * celda
            val yb = cae(xx)
            // Tres montes grandes de distinto alto, con laderas de pendiente desigual, sobre una loma baja.
            var sierra = 1.5f + 0.5f * sin(c * 0.3f)
            for (j in 0 until 3) {
                val pc = cols * CUMBRES_CIBER[j]; val alto = ALTOS_CIBER[j]
                val pend = if (c < pc) 0.24f + 0.06f * j else 0.3f - 0.04f * j
                sierra = maxOf(sierra, alto - Math.abs(c - pc) * pend)
            }
            val monte = sierra.toInt().coerceIn(1, 6)
            for (f in 0 until filas) {
                val yy = ytop + yb + f * celda
                val desdeAbajo = filas - f
                val dentro = desdeAbajo <= monte
                val perfil = desdeAbajo == monte || desdeAbajo == 1
                val estrella = !dentro && desdeAbajo > monte + 1 && azar(6110, c * 13 + f) > 0.955f
                when {
                    perfil || estrella -> llenos.addRect(Rect(xx, yy, xx + celda * 0.82f, yy + celda * 0.82f))
                    dentro && (c + f) % 2 == 0 -> tenues.addRect(Rect(xx, yy, xx + celda * 0.82f, yy + celda * 0.82f))
                }
            }
        }
        val tinta = Array(5) { i -> val f = i / 4f; f to g.t(x0 + (x1 - x0) * f, y0).c(mezcla(TINTA, PARED, 0.25f), 0.9f).copy(alpha = 0.92f - 0.25f * f) }
        drawPath(llenos, Brush.horizontalGradient(*tinta, startX = x0, endX = x1))
        drawPath(tenues, Brush.horizontalGradient(*tinta, startX = x0, endX = x1), alpha = 0.7f)
        // El papel se comba: el canto de arriba con su filo y lo de abajo de cada comba, en sombra.
        drawRect(g.t(x0, y0).b(0.3f), Offset(x0, y0), Size(x1 - x0, 1.2f * u))
    }
    // Los trozos de cinta adhesiva: en los extremos y en medio, translúcidos, con su borde.
    listOf(x0 + w * 0.01f, (x0 + x1) / 2f, x1 - w * 0.01f).forEachIndexed { i, x ->
        val t = g.t(x, y0)
        rotate(-8f + 9f * i, Offset(x, y0 + cae(x))) {
            val r = Rect(x - w * 0.016f, y0 + cae(x) - w * 0.012f, x + w * 0.016f, y0 + cae(x) + w * 0.014f)
            drawRect(t.c(mezcla(PAPEL, NOTA, 0.35f)).copy(alpha = 0.55f), r.topLeft, r.size)
            drawRect(t.b(0.35f), r.topLeft, Size(r.width, 0.8f * u))
        }
    }
}

/**
 * El mazo de tres cables que entra por la izquierda, corre por la pared bajo el banderín sujeto con bridas blancas y baja por
 * detrás de la transportable; y el de la impresora, que baja al enchufe de la derecha.
 */
private fun DrawScope.cablesParedCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val y0 = w * 0.168f
    val cols = listOf(NEGRO to 2.8f, CABLE to 2.2f, mezcla(BEIS_OSC, NEGRO, 0.4f) to 2.4f)
    cols.forEachIndexed { j, (col, gr) ->
        val yy = y0 + j * 2.6f * u
        val p = Path().apply {
            moveTo(-4f * u, yy + 3f * u)
            cubicTo(w * 0.12f, yy + 5f * u, w * 0.26f, yy - 1f * u, w * 0.38f + j * 2f * u, yy)
            cubicTo(w * 0.45f + j * 2f * u, yy, w * 0.455f + j * 2.4f * u, yy + w * 0.04f, w * 0.445f + j * 2.4f * u, w * 0.3f)
        }
        val t = g.t(w * 0.25f, yy)
        translate(w * 0.008f, w * 0.008f) { drawPath(p, NOCHE.copy(alpha = 0.4f), style = Stroke(gr * u)) }
        drawPath(p, t.c(col), style = Stroke(gr * u, cap = StrokeCap.Round))
        drawPath(p, t.b(0.35f), style = Stroke(gr * 0.3f * u))
    }
    listOf(0.1f, 0.22f, 0.34f).forEach { fx ->
        val x = w * fx
        val t = g.t(x, y0)
        drawRect(t.s(0.4f), Offset(x + 1.2f * u, y0 - 1.5f * u), Size(2.2f * u, 10f * u))
        drawRect(t.c(PAPEL, 0.9f), Offset(x, y0 - 2.5f * u), Size(2.2f * u, 10.5f * u))
        drawRect(t.c(PAPEL, 0.9f), Offset(x + 2.2f * u, y0 - 3.5f * u), Size(2.6f * u, 2.2f * u))
        drawRect(t.b(0.4f), Offset(x, y0 - 2.5f * u), Size(0.7f * u, 10.5f * u))
    }
    // El cable de la impresora: sale por detrás de ella y baja, sujeto con una grapa, al enchufe.
    val t = g.t(w * 0.9f, w * 0.34f)
    val p = Path().apply { moveTo(w * 0.87f, w * 0.43f); cubicTo(w * 0.885f, w * 0.38f, w * 0.91f, w * 0.37f, w * 0.93f, w * 0.38f) }
    translate(w * 0.006f, w * 0.004f) { drawPath(p, NOCHE.copy(alpha = 0.45f), style = Stroke(2.4f * u)) }
    drawPath(p, t.c(NEGRO), style = Stroke(2.4f * u))
    drawPath(p, t.b(0.3f), style = Stroke(0.7f * u))
}

/** Una nota adhesiva amarilla en la pared, ladeada [giro] grados, con garabatos, la punta de abajo levantada y su sombra. */
internal fun DrawScope.notaParedCiber(g: MarcoCiber, c: Offset, lado: Float, giro: Float, semilla: Int) {
    val u = density
    val t = g.t(c)
    val r = Rect(c.x - lado / 2, c.y - lado / 2, c.x + lado / 2, c.y + lado / 2)
    rotate(giro, c) {
        val esq = lado * 0.22f
        val hoja = poligonoCiber(r.left, r.top, r.right, r.top, r.right, r.bottom - esq, r.right - esq, r.bottom, r.left, r.bottom)
        translate(lado * 0.1f, lado * 0.12f) { drawPath(hoja, NOCHE.copy(alpha = 0.4f)) }
        drawPath(hoja, t.c(NOTA))
        drawRect(t.c(mezcla(NOTA, BEIS_OSC, 0.3f)), r.topLeft, Size(lado, lado * 0.14f))
        garabatosCiber(Rect(r.left + lado * 0.12f, r.top + lado * 0.24f, r.right - lado * 0.14f, r.bottom - lado * 0.2f), 3,
            t.c(mezcla(PARED, NOCHE, 0.4f)), 6120 + semilla, 1f * u)
        val doblez = poligonoCiber(r.right, r.bottom - esq, r.right - esq * 0.2f, r.bottom - esq * 0.2f, r.right - esq, r.bottom)
        translate(1.2f * u, 1.6f * u) { drawPath(doblez, t.s(0.35f)) }
        drawPath(doblez, t.c(mezcla(NOTA, Color.White, 0.25f)))
    }
}

/** El enchufe de pared: la placa beis con su tornillo, dos tomas, una con la clavija negra de la impresora. */
private fun DrawScope.enchufeCiber(g: MarcoCiber, c: Offset) {
    val u = density; val w = g.w
    val t = g.t(c)
    val r = Rect(c.x - w * 0.026f, c.y - w * 0.036f, c.x + w * 0.026f, c.y + w * 0.036f)
    drawRoundRect(NOCHE.copy(alpha = 0.45f), r.translate(w * 0.006f, w * 0.006f).topLeft, r.size, CornerRadius(w * 0.006f))
    drawRoundRect(t.c(mezcla(BEIS, PAPEL, 0.3f)), r.topLeft, r.size, CornerRadius(w * 0.006f))
    drawRoundRect(t.b(0.4f), r.topLeft, r.size, CornerRadius(w * 0.006f), style = Stroke(0.8f * u))
    listOf(-1f, 1f).forEach { s ->
        val o = Offset(c.x, c.y + s * w * 0.017f)
        drawCircle(t.c(mezcla(BEIS, BEIS_OSC, 0.5f)), w * 0.012f, o)
        drawCircle(NOCHE.copy(alpha = 0.8f), 1.3f * u, o - Offset(w * 0.005f, 0f))
        drawCircle(NOCHE.copy(alpha = 0.8f), 1.3f * u, o + Offset(w * 0.005f, 0f))
    }
    // La clavija de arriba, con su cable.
    val cl = Rect(c.x - w * 0.011f, c.y - w * 0.03f, c.x + w * 0.011f, c.y - w * 0.006f)
    drawRect(t.c(NEGRO), cl.topLeft, cl.size)
    drawRect(t.b(0.4f), cl.topLeft, Size(cl.width, 0.8f * u))
    drawCircle(t.c(ACERO), 1.2f * u, Offset(c.x, c.y + w * 0.005f))
}

/**
 * El rollo de cable alargador colgado de un clavo en la pared, a la derecha del banderín: cinco vueltas negras y grises
 * desiguales, el cabo que cuelga con su clavija y la sombra en la pared.
 */
private fun DrawScope.rolloCableCiber(g: MarcoCiber, clavo: Offset) {
    val u = density; val w = g.w
    val t = g.t(clavo + Offset(0f, w * 0.06f))
    drawCircle(t.c(ACERO), 1.6f * u, clavo)
    for (k in 0 until 5) {
        val rr = Rect(clavo.x - w * 0.034f + k * 0.7f * u, clavo.y - 1f * u, clavo.x + w * 0.032f + k * 0.9f * u, clavo.y + w * (0.115f + 0.007f * k))
        drawOval(NOCHE.copy(alpha = 0.3f), rr.translate(w * 0.01f, w * 0.006f).topLeft, rr.size, style = Stroke(2.2f * u))
        drawOval(t.c(if (k % 2 == 0) NEGRO else mezcla(NEGRO, CABLE, 0.45f)), rr.topLeft, rr.size, style = Stroke(2.2f * u))
        drawArc(t.b(0.4f), 170f, 70f, false, rr.topLeft, rr.size, style = Stroke(0.7f * u))
    }
    val cola = Path().apply { moveTo(clavo.x + w * 0.03f, clavo.y + w * 0.11f); cubicTo(clavo.x + w * 0.04f, clavo.y + w * 0.15f, clavo.x + w * 0.01f, clavo.y + w * 0.16f, clavo.x + w * 0.018f, clavo.y + w * 0.19f) }
    drawPath(cola, t.c(NEGRO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    drawRect(t.c(NEGRO), Offset(clavo.x + w * 0.008f, clavo.y + w * 0.188f), Size(w * 0.02f, w * 0.026f))
    drawRect(t.b(0.4f), Offset(clavo.x + w * 0.008f, clavo.y + w * 0.188f), Size(0.8f * u, w * 0.026f))
    drawRect(t.c(ACERO), Offset(clavo.x + w * 0.012f, clavo.y + w * 0.214f), Size(1.2f * u, w * 0.01f))
    drawRect(t.c(ACERO), Offset(clavo.x + w * 0.021f, clavo.y + w * 0.214f), Size(1.2f * u, w * 0.01f))
}

/**
 * La pared que asoma a los lados de la estantería, de la corona al suelo: el azul pizarra con la luz que le llega a lo alto, sus
 * pinceladas y la sombra del mueble a su derecha; abajo, el rodapié negro.
 */
internal fun DrawScope.paredLadosCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val y0 = g.ch; val y1 = g.suelo
    if (y1 <= y0) return
    columnaCiber(0f, g.ml + 1f, y0, y1, PARED, g, n = 10)
    columnaCiber(g.mr - 1f, w, y0, y1, PARED, g, n = 10)
    pinceladas(Rect(0f, y0, g.ml, y1), NOCHE, 6130, 10, u, 0.16f)
    pinceladas(Rect(g.mr, y0, w, y1), NOCHE, 6131, 10, u, 0.16f)
    // La sombra de la estantería en la pared de la derecha (la luz de arriba viene de la izquierda).
    drawRect(NOCHE.copy(alpha = 0.5f), Offset(g.mr, y0), Size(w * 0.012f, y1 - y0))
    // El rodapié.
    val rod = w * 0.018f
    listOf(0f to g.ml, g.mr to w).forEach { (a, b) ->
        val t = g.t((a + b) / 2, y1)
        drawRect(t.c(NEGRO), Offset(a, y1 - rod), Size(b - a, rod))
        drawRect(t.b(0.3f), Offset(a, y1 - rod), Size(b - a, 0.8f * u))
    }
}
