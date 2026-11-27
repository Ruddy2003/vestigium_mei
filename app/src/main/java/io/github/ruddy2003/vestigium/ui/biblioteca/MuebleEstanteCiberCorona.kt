package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AZUL_SALVA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CAFE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CHAPA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CIAN
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CIAN_CLARO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El remate de la estantería, de [MarcoCiber.apoyoAtras] al pie de la corona: la tapa vista un poco desde arriba (rozada, con un
 * cerco de taza), el canto redondo con su filo de luz y las tapas negras de las esquinas, el friso hundido con la rejilla
 * troquelada y la placa de aluminio sin letras, y el pliegue de abajo. La sombra en la pared, a la derecha.
 */
internal fun DrawScope.remateCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    val x0 = g.ml; val x1 = g.mr
    val vuelo = w * 0.011f
    val ap = g.apoyo; val ch = g.ch
    drawRect(NOCHE.copy(alpha = 0.5f), Offset(x1 + vuelo, ap + 3f * u), Size(w * 0.014f, ch - ap))
    // Debajo de las bandas del remate, la chapa de un solo tono: las juntas entre bandas que caen a medio píxel no dejan ver
    // el fondo.
    drawRect(g.t(w * 0.3f, (ap + ch) / 2).c(mezcla(CHAPA, BEIS_OSC, 0.3f)), Offset(x0 - vuelo, g.apoyoAtras), Size(x1 - x0 + 2 * vuelo, ch + 1f - g.apoyoAtras))
    // La tapa vista desde arriba: chapa algo más clara, el canto de atrás en sombra, rayas y el cerco de la taza.
    bandaCiber(x0 - vuelo, x1 + vuelo, g.apoyoAtras, ap, CHAPA, g, 0.62f)
    drawRect(NOCHE.copy(alpha = 0.45f), Offset(x0 - vuelo, g.apoyoAtras), Size(x1 - x0 + 2 * vuelo, 1.6f * u))
    for (j in 0 until 8) {
        val x = x0 + (x1 - x0) * azar(6201, j)
        val t = g.t(x, ap)
        drawLine(t.b(0.3f), Offset(x, g.apoyoAtras + (ap - g.apoyoAtras) * (0.3f + 0.4f * azar(6202, j))), Offset(x + w * 0.03f, g.apoyoAtras + (ap - g.apoyoAtras) * (0.4f + 0.3f * azar(6203, j))), 0.6f * u)
    }
    val cerco = Offset(w * 0.47f, (g.apoyoAtras + ap) / 2)
    drawOval(g.t(cerco).c(CAFE).copy(alpha = 0.5f), Offset(cerco.x - w * 0.02f, cerco.y - (ap - g.apoyoAtras) * 0.32f), Size(w * 0.04f, (ap - g.apoyoAtras) * 0.64f), style = Stroke(1f * u))
    // El canto redondo: el filo de luz, la panza y lo de abajo, que no ve la luz.
    val c0 = ap; val c1 = ap + w * 0.028f
    bandaCiber(x0 - vuelo, x1 + vuelo, c0, c1, CHAPA, g, 0.72f)
    bandaCiber(x0 - vuelo, x1 + vuelo, c0, c0 + w * 0.009f, mezcla(CHAPA, Color.White, 0.08f), g, 0.82f)
    drawRect(g.t(w * 0.3f, c0).b(0.7f), Offset(x0 - vuelo, c0), Size(x1 - x0 + 2 * vuelo, 1.3f * u))
    drawRect(NOCHE.copy(alpha = 0.32f), Offset(x0 - vuelo, c1 - w * 0.009f), Size(x1 - x0 + 2 * vuelo, w * 0.009f))
    drawRect(NOCHE.copy(alpha = 0.55f), Offset(x0 - vuelo, c1 - 1.2f * u), Size(x1 - x0 + 2 * vuelo, 1.2f * u))
    // Las tapas negras de plástico de las dos esquinas del canto.
    listOf(x0 - vuelo to x0 + w * 0.018f, x1 - w * 0.018f to x1 + vuelo).forEach { (a, b) ->
        val t = g.t((a + b) / 2, c0)
        drawRoundRect(t.c(NEGRO), Offset(a, c0 - 0.5f * u), Size(b - a, c1 - c0 + 1f * u), CornerRadius(w * 0.006f))
        drawRect(t.b(0.5f), Offset(a + 1f * u, c0 + 0.5f * u), Size(b - a - 2f * u, 0.9f * u))
    }
    // El friso hundido: la sombra del canto encima, la chapa, la rejilla troquelada y la placa de aluminio.
    val f0 = c1; val f1 = ch - w * 0.016f
    bandaCiber(x0, x1, f0, f1, mezcla(CHAPA, BEIS_OSC, 0.3f), g, 0.8f)
    pinceladas(Rect(x0, f0, x1, f1), NOCHE, 6210, 10, u, 0.12f)
    drawRect(NOCHE.copy(alpha = 0.45f), Offset(x0, f0), Size(x1 - x0, w * 0.008f))
    rejillaFrisoCiber(g, Rect(w * 0.12f, f0 + (f1 - f0) * 0.28f, w * 0.46f, f1 - (f1 - f0) * 0.18f))
    placaFrisoCiber(g, Rect(w * 0.62f, f0 + (f1 - f0) * 0.26f, w * 0.8f, f1 - (f1 - f0) * 0.18f))
    listOf(w * 0.05f, w * 0.95f).forEach { x -> remacheCiber(Offset(x, (f0 + f1) / 2), 2.2f * u, g.t(x, f0)) }
    // La pintura saltada en el canto y en el friso: asoma el acero.
    listOf(Offset(w * 0.27f, c0 + w * 0.012f), Offset(w * 0.71f, c0 + w * 0.006f), Offset(w * 0.55f, f1 - w * 0.01f), Offset(w * 0.9f, f0 + w * 0.03f)).forEachIndexed { k, p ->
        val t = g.t(p)
        val r = (1.8f + 2.2f * azar(6220, k)) * u
        drawOval(t.c(ACERO), Offset(p.x - r * 1.4f, p.y - r * 0.8f), Size(r * 2.8f, r * 1.6f))
        drawArc(t.s(0.45f), 180f, 180f, false, Offset(p.x - r * 1.4f, p.y - r * 0.8f), Size(r * 2.8f, r * 1.6f), style = Stroke(0.6f * u))
    }
    // El pliegue de abajo.
    bandaCiber(x0, x1, f1, ch + 1f, mezcla(CHAPA, Color.White, 0.04f), g, 0.8f)
    drawRect(g.t(w * 0.3f, f1).b(0.5f), Offset(x0, f1), Size(x1 - x0, 1f * u))
    drawRect(NOCHE.copy(alpha = 0.4f), Offset(x0, ch - 1.4f * u), Size(x1 - x0, 1.4f * u))
}

/** La rejilla troquelada del friso: tres hileras de ranuras horizontales con su labio de luz abajo. */
private fun DrawScope.rejillaFrisoCiber(g: MarcoCiber, r: Rect) {
    val u = density
    val filas = 3; val porFila = 6
    val alto = r.height / filas
    val largo = r.width / porFila
    for (f in 0 until filas) for (c in 0 until porFila) {
        val x = r.left + c * largo + (if (f % 2 == 1) largo * 0.5f else 0f)
        if (x + largo * 0.8f > r.right) continue
        val y = r.top + f * alto + alto * 0.25f
        val t = g.t(x, y)
        drawRoundRect(NOCHE.copy(alpha = 0.8f), Offset(x, y), Size(largo * 0.78f, alto * 0.42f), CornerRadius(alto * 0.21f))
        drawRect(t.b(0.45f), Offset(x + largo * 0.06f, y + alto * 0.42f), Size(largo * 0.66f, 0.7f * u))
    }
}

/** La placa de aluminio del fabricante, sin letras: el marco, dos franjas grabadas y los cuatro remaches. */
private fun DrawScope.placaFrisoCiber(g: MarcoCiber, r: Rect) {
    val u = density
    val t = g.t(r.center)
    drawRect(t.s(0.4f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
    drawRect(t.c(ALUMINIO), r.topLeft, r.size)
    drawRect(t.c(mezcla(ACERO_OSC, NOCHE, 0.3f)), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.3f), Size(r.width * 0.55f, r.height * 0.16f))
    drawRect(t.c(mezcla(ACERO_OSC, NOCHE, 0.1f)), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.6f), Size(r.width * 0.34f, r.height * 0.1f))
    drawRect(t.b(0.6f), r.topLeft, Size(r.width, 0.8f * u))
    drawRect(t.s(0.35f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
    listOf(Offset(r.left + 3f * u, r.top + 3f * u), Offset(r.right - 3f * u, r.top + 3f * u), Offset(r.left + 3f * u, r.bottom - 3f * u), Offset(r.right - 3f * u, r.bottom - 3f * u)).forEach {
        drawCircle(t.c(ACERO_OSC), 1.3f * u, it)
        drawCircle(t.b(0.6f), 0.5f * u, it - Offset(0.4f * u, 0.4f * u))
    }
}

/** Lo de encima del remate, de atrás adelante. */
internal fun DrawScope.bodegonCiber(g: MarcoCiber) {
    val w = g.w; val ap = g.apoyo
    papelImpresoraCiber(g)
    cajaDisquetesArribaCiber(g, Rect(w * 0.026f, ap - w * 0.066f, w * 0.1f, ap - w * 0.004f))
    transportableCiber(g, Rect(w * 0.108f, ap - w * 0.232f, w * 0.452f, ap - w * 0.006f))
    vasoLapicesCiber(g, Offset(w * 0.478f, ap - w * 0.006f))
    impresoraCiber(g, Rect(w * 0.505f, ap - w * 0.1f, w * 0.872f, ap - w * 0.004f))
    tazaCiber(g, Offset(w * 0.925f, ap - w * 0.008f))
}

/**
 * La computadora transportable: la caja de plástico beis con la tapa de arriba vista desde arriba, el asa abatible levantada, la
 * pantalla de tubo encendida (renglones de bloques y el cursor; en los Sueños, el salvapantallas de la luna), los mandos, las dos
 * disqueteras de 5¼ con su palanca y su piloto, la rejilla, la nota adhesiva y el teclado tumbado delante con su cable en espiral.
 */
private fun DrawScope.transportableCiber(g: MarcoCiber, f: Rect) {
    val u = density; val w = g.w
    val t = g.t(f.center.x, f.bottom - w * 0.03f)
    contactoCiber(Offset(f.center.x + w * 0.02f, f.bottom + w * 0.003f), f.width * 1.08f, w * 0.016f, t, 0.7f)
    // La sombra en la pared: la caja tapa la luz de la otra luz del cuarto (la pared se ilumina por la propia pantalla, así que
    // lo que queda es un oscuro corto a la derecha y arriba).
    drawRoundRect(NOCHE.copy(alpha = 0.35f), Offset(f.left + w * 0.02f, f.top - w * 0.012f), f.size, CornerRadius(w * 0.012f))
    // La caja en tres cuartos: el costado derecho, que se va hacia atrás en la sombra (la pantalla no lo alumbra), con su rejilla,
    // y la tapa de arriba, vista desde arriba, con el asa.
    val dv = Offset(w * 0.034f, -w * 0.02f)
    val costado = poligonoCiber(f.right - w * 0.006f, f.top + w * 0.004f, f.right + dv.x, f.top + dv.y, f.right + dv.x, f.bottom + dv.y, f.right - w * 0.006f, f.bottom)
    drawPath(costado, mezcla(g.a.hondo(BEIS), g.t(f.right, f.center.y).c(BEIS_OSC, 0.7f), 0.55f))
    clipPath(costado) {
        for (k in 0 until 7) {
            val y = f.top + f.height * (0.2f + 0.09f * k)
            drawLine(NOCHE.copy(alpha = 0.55f), Offset(f.right + dv.x * 0.2f, y + dv.y * 0.2f), Offset(f.right + dv.x * 0.8f, y + dv.y * 0.8f), 1.6f * u)
        }
    }
    drawLine(g.a.colores[0].copy(alpha = 0.3f), Offset(f.right + dv.x, f.top + dv.y), Offset(f.right + dv.x, f.bottom + dv.y), 0.8f * u)
    val arriba = poligonoCiber(f.left + w * 0.006f, f.top + w * 0.002f, f.left + w * 0.006f + dv.x, f.top + dv.y, f.right + dv.x, f.top + dv.y, f.right - w * 0.006f, f.top + w * 0.002f)
    drawPath(arriba, Brush.horizontalGradient(listOf(g.t(f.left, f.top).c(mezcla(BEIS, Color.White, 0.08f), 1.05f), g.t(f.right, f.top).c(BEIS, 0.85f)), f.left, f.right + dv.x))
    drawLine(g.t(f.left, f.top).b(0.6f), Offset(f.left + w * 0.006f + dv.x, f.top + dv.y), Offset(f.right + dv.x, f.top + dv.y), 0.9f * u)
    val asaY = f.top - w * 0.07f
    val pa = f.left + f.width * 0.24f; val pb = f.right - f.width * 0.14f
    val asa = Path().apply {
        moveTo(pa, f.top - w * 0.016f)
        cubicTo(pa, asaY + w * 0.01f, pa + w * 0.02f, asaY, pa + w * 0.05f, asaY)
        lineTo(pb - w * 0.05f, asaY)
        cubicTo(pb - w * 0.02f, asaY, pb, asaY + w * 0.01f, pb, f.top - w * 0.016f)
    }
    translate(w * 0.014f, w * 0.008f) { drawPath(asa, NOCHE.copy(alpha = 0.35f), style = Stroke(w * 0.014f)) }
    val ta = g.t(f.center.x, asaY)
    drawPath(asa, ta.c(NEGRO), style = Stroke(w * 0.014f, cap = StrokeCap.Round))
    drawPath(asa, ta.b(0.6f), style = Stroke(w * 0.003f))
    listOf(pa, pb).forEach { x ->
        drawRoundRect(ta.c(mezcla(BEIS_OSC, NEGRO, 0.3f)), Offset(x - w * 0.014f, f.top - w * 0.026f), Size(w * 0.028f, w * 0.022f), CornerRadius(w * 0.004f))
        remacheCiber(Offset(x, f.top - w * 0.015f), 1.8f * u, ta)
    }
    // La cara: plástico beis alumbrado por su propia pantalla, más claro alrededor del vidrio; el canto derecho redondeado en sombra.
    val cara = Path().apply { addRoundRect(RoundRect(f, CornerRadius(w * 0.012f))) }
    val pantC = Offset(w * MuebleEstanteCiber.PANT_X, w * MuebleEstanteCiber.PANT_Y)
    drawPath(cara, Brush.radialGradient(listOf(t.c(BEIS, 1.25f), t.c(BEIS, 0.95f), t.c(BEIS_OSC, 0.85f)), pantC, f.width * 0.7f))
    clipPath(cara) {
        pinceladas(f, BEIS_OSC, 6230, 12, u, 0.18f)
        drawRect(NOCHE.copy(alpha = 0.28f), Offset(f.right - w * 0.022f, f.top), Size(w * 0.022f, f.height))
        drawRect(t.b(0.5f), f.topLeft, Size(w * 0.006f, f.height))
        drawRect(NOCHE.copy(alpha = 0.22f), Offset(f.left, f.bottom - w * 0.012f), Size(f.width, w * 0.012f))
        drawRect(mezcla(BEIS_OSC, AMBAR, 0.35f).copy(alpha = 0.12f), f.topLeft, Size(f.width, f.height * 0.3f))
        // Las juntas de la caja: la de la tapa delantera y la del costado.
        drawLine(NOCHE.copy(alpha = 0.4f), Offset(f.left, f.top + w * 0.008f), Offset(f.right, f.top + w * 0.008f), 1f * u)
    }
    drawRect(t.b(0.7f), Offset(f.left + w * 0.01f, f.top), Size(f.width - w * 0.02f, 1.2f * u))
    pantallaTransportableCiber(g, f)
    // Las dos disqueteras, a la derecha de la pantalla.
    val dx0 = f.left + f.width * 0.6f; val dx1 = f.right - f.width * 0.05f
    listOf(0.1f, 0.33f).forEachIndexed { i, fy ->
        val y0 = f.top + f.height * fy
        val bahia = Rect(dx0, y0, dx1, y0 + f.height * 0.19f)
        drawRect(NOCHE.copy(alpha = 0.3f), bahia.translate(1.2f * u, 1.5f * u).topLeft, bahia.size)
        drawRect(t.c(mezcla(BEIS, BEIS_OSC, 0.4f)), bahia.topLeft, bahia.size)
        drawRect(t.b(0.5f), bahia.topLeft, Size(bahia.width, 0.8f * u))
        // La ranura del disco y la palanca (una abierta, la otra cerrada).
        val ry = bahia.center.y - f.height * 0.012f
        drawRoundRect(NOCHE.copy(alpha = 0.85f), Offset(bahia.left + bahia.width * 0.08f, ry), Size(bahia.width * 0.72f, f.height * 0.024f), CornerRadius(1.2f * u))
        val px = bahia.left + bahia.width * 0.44f
        if (i == 0) {
            drawRoundRect(t.c(NEGRO), Offset(px - w * 0.005f, bahia.top + bahia.height * 0.12f), Size(w * 0.01f, bahia.height * 0.76f), CornerRadius(w * 0.003f))
            drawRect(t.b(0.5f), Offset(px - w * 0.005f, bahia.top + bahia.height * 0.12f), Size(0.8f * u, bahia.height * 0.76f))
        } else {
            drawRoundRect(t.c(NEGRO), Offset(px - bahia.width * 0.16f, ry - w * 0.002f), Size(bahia.width * 0.32f, w * 0.009f), CornerRadius(w * 0.003f))
            drawRect(t.b(0.5f), Offset(px - bahia.width * 0.16f, ry - w * 0.002f), Size(bahia.width * 0.32f, 0.8f * u))
        }
        pilotoCiber(Offset(bahia.right - bahia.width * 0.1f, bahia.top + bahia.height * 0.25f), 1.4f * u, if (i == 0) VERDE else ROJO, i == 0 || g.a.noche.not(), t)
    }
    // La rejilla de abajo a la derecha y los dos mandos bajo la pantalla.
    for (k in 0 until 7) drawRect(NOCHE.copy(alpha = 0.5f), Offset(dx0 + (dx1 - dx0) * (0.08f + 0.13f * k), f.top + f.height * 0.62f), Size((dx1 - dx0) * 0.05f, f.height * 0.2f))
    listOf(0.16f, 0.27f).forEach { fx ->
        val mc = Offset(f.left + f.width * fx, f.top + f.height * 0.84f)
        val rm = f.height * 0.045f
        drawCircle(NOCHE.copy(alpha = 0.5f), rm, mc + Offset(1f * u, 1.2f * u))
        drawCircle(t.c(mezcla(BEIS_OSC, NEGRO, 0.35f)), rm, mc)
        drawCircle(t.b(0.6f), rm * 0.35f, mc - Offset(rm * 0.35f, rm * 0.35f))
    }
    for (k in 0 until 6) drawRect(NOCHE.copy(alpha = 0.45f), Offset(f.left + f.width * (0.36f + 0.03f * k), f.top + f.height * 0.79f), Size(f.width * 0.012f, f.height * 0.1f))
    pilotoCiber(Offset(f.left + f.width * 0.5f, f.top + f.height * 0.84f), 1.5f * u, VERDE, true, t)
    // La nota adhesiva pegada en la esquina de arriba a la derecha.
    val nc = Offset(f.right - f.width * 0.09f, f.top + f.height * 0.2f)
    val nl = f.width * 0.13f
    rotate(7f, nc) {
        val r = Rect(nc.x - nl / 2, nc.y - nl / 2, nc.x + nl / 2, nc.y + nl / 2)
        drawRect(NOCHE.copy(alpha = 0.4f), r.translate(2f * u, 2.5f * u).topLeft, r.size)
        drawRect(t.c(NOTA, 1.1f), r.topLeft, r.size)
        drawRect(t.c(mezcla(NOTA, BEIS_OSC, 0.35f)), r.topLeft, Size(nl, nl * 0.14f))
        garabatosCiber(Rect(r.left + nl * 0.14f, r.top + nl * 0.26f, r.right - nl * 0.12f, r.bottom - nl * 0.18f), 3, t.c(mezcla(PARED, NOCHE, 0.3f)), 6240, 1f * u)
    }
    tecladoTumbadoCiber(g, Rect(f.left + f.width * 0.06f, f.bottom - w * 0.004f, f.right - f.width * 0.1f, f.bottom + w * 0.004f + w * 0.02f))
}

/**
 * La pantalla de la transportable: el bisel hundido y el vidrio abombado. En el diario, el fósforo cian con los renglones de
 * bloques (no letras), una fila en vídeo inverso y el cursor; en los Sueños, el salvapantallas: estrellas y una luna creciente
 * pálida sobre azul hondo. El barrido, el reflejo curvo y el resplandor que sale del vidrio.
 */
private fun DrawScope.pantallaTransportableCiber(g: MarcoCiber, f: Rect) {
    val u = density; val w = g.w
    val bis = Rect(f.left + f.width * 0.05f, f.top + f.height * 0.09f, f.left + f.width * 0.56f, f.top + f.height * 0.72f)
    drawRoundRect(mezcla(g.t(bis.center).c(BEIS_OSC), NOCHE, 0.35f), bis.topLeft, bis.size, CornerRadius(w * 0.012f))
    drawRoundRect(NOCHE.copy(alpha = 0.55f), bis.topLeft, Size(bis.width, 3f * u), CornerRadius(w * 0.006f))
    val pan = Rect(bis.left + bis.width * 0.07f, bis.top + bis.height * 0.08f, bis.right - bis.width * 0.07f, bis.bottom - bis.height * 0.08f)
    val panP = Path().apply { addRoundRect(RoundRect(pan, CornerRadius(pan.height * 0.12f))) }
    val noche = g.a.noche
    val fondos = if (noche) listOf(Color(0xFF1A3478), AZUL_SALVA, Color(0xFF050C26)) else listOf(Color(0xFF0F4F5C), Color(0xFF062328), Color(0xFF031114))
    drawPath(panP, Brush.radialGradient(fondos, pan.center - Offset(pan.width * 0.08f, pan.height * 0.1f), pan.width * 0.75f))
    clipPath(panP) {
        val lh = pan.height / 9f
        val x0 = pan.left + pan.width * 0.07f
        if (!noche) {
            val tinta = CIAN.copy(alpha = 0.85f)
            // La cabecera en vídeo inverso, los renglones de bloques y la barra de avance.
            drawRect(CIAN.copy(alpha = 0.8f), Offset(pan.left, pan.top + lh * 0.3f), Size(pan.width, lh * 0.8f))
            for (k in 0 until 3) drawRect(Color(0xFF042228), Offset(x0 + k * pan.width * 0.2f, pan.top + lh * 0.5f), Size(pan.width * (0.08f + 0.05f * azar(6250, k)), lh * 0.4f))
            for (r in 0 until 6) {
                val y = pan.top + lh * (1.9f + r)
                var x = x0 + (if (r % 3 == 2) pan.width * 0.06f else 0f)
                val fin = pan.left + pan.width * (0.4f + 0.5f * azar(6251, r))
                var j = 0
                while (x < fin) {
                    val l = pan.width * (0.04f + 0.09f * azar(6252 + r, j))
                    drawRect(tinta, Offset(x, y - lh * 0.2f), Size(minOf(l, fin - x), lh * 0.42f))
                    x += l + pan.width * 0.03f; j++
                }
            }
            val yp = pan.top + lh * 8.1f
            drawPath(poligonoCiber(x0, yp - lh * 0.25f, x0 + lh * 0.32f, yp, x0, yp + lh * 0.25f), tinta)
            drawRect(CIAN_CLARO, Offset(x0 + lh * 0.65f, yp - lh * 0.38f), Size(lh * 0.5f, lh * 0.76f))
        } else {
            // El salvapantallas: estrellas de un punto y una luna creciente recortada (no llena: no parece un ojo).
            val estrellas = List(26) { k -> Offset(pan.left + pan.width * azar(6255, k), pan.top + pan.height * azar(6256, k)) }
            drawPoints(estrellas, PointMode.Points, Color(0xFFDDE6FF).copy(alpha = 0.85f), 1.6f * u, StrokeCap.Round)
            val lc = Offset(pan.left + pan.width * 0.62f, pan.top + pan.height * 0.42f)
            val lr = pan.height * 0.26f
            mancha(lc, lr * 2.6f, LUNA.copy(alpha = 0.3f))
            val luna = Path().apply {
                addOval(Rect(lc.x - lr, lc.y - lr, lc.x + lr, lc.y + lr))
                op(this, Path().apply { addOval(Rect(lc.x - lr * 0.45f, lc.y - lr * 1.2f, lc.x + lr * 1.45f, lc.y + lr * 0.7f)) }, androidx.compose.ui.graphics.PathOperation.Difference)
            }
            drawPath(luna, Color(0xFFE8EEFF))
        }
        // El barrido, el reflejo curvo del vidrio y las esquinas más hondas.
        var y = pan.top
        while (y < pan.bottom) { drawLine(NOCHE.copy(alpha = 0.2f), Offset(pan.left, y), Offset(pan.right, y), 0.6f * u); y += 2.2f * u }
        drawPath(Path().apply {
            moveTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.55f)
            cubicTo(pan.left + pan.width * 0.06f, pan.top + pan.height * 0.1f, pan.left + pan.width * 0.3f, pan.top + pan.height * 0.06f, pan.left + pan.width * 0.6f, pan.top + pan.height * 0.06f)
        }, CIAN_CLARO.copy(alpha = 0.2f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawRoundRect(NOCHE.copy(alpha = 0.45f), pan.topLeft, pan.size, CornerRadius(pan.height * 0.12f), style = Stroke(4f * u))
    }
    // El resplandor que sale del vidrio sobre la cara de la caja y el canto del remate.
    val col = g.a.colores[0]
    mancha(pan.center, pan.width * 1.1f, col.copy(alpha = if (noche) 0.12f else 0.14f))
    drawRect(CIAN_CLARO.copy(alpha = 0.35f), Offset(bis.left + w * 0.01f, bis.bottom - 1f * u), Size(bis.width - w * 0.02f, 1f * u))
}

/** El teclado de la transportable, tumbado delante: la losa beis vista desde arriba con sus hileras de teclas lisas y su cable. */
private fun DrawScope.tecladoTumbadoCiber(g: MarcoCiber, r: Rect) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    drawRect(NOCHE.copy(alpha = 0.5f), Offset(r.left + w * 0.01f, r.bottom - 1f * u), Size(r.width, w * 0.006f))
    val losa = poligonoCiber(r.left + w * 0.006f, r.top, r.right - w * 0.006f, r.top, r.right, r.bottom - w * 0.007f, r.left, r.bottom - w * 0.007f)
    drawPath(losa, t.c(mezcla(BEIS, BEIS_OSC, 0.25f)))
    // Las hileras de teclas: cuadritos claros con su sombra, sin letras.
    val filas = 3
    val alto = (r.height - w * 0.009f) / filas
    for (fi in 0 until filas) {
        val y = r.top + alto * fi + alto * 0.18f
        val n = 22
        val paso = (r.width - w * 0.03f) / n
        for (k in 0 until n) {
            if (fi == 2 && k in 7..14) continue
            val x = r.left + w * 0.015f + paso * k + (if (fi == 1) paso * 0.3f else 0f)
            if (x + paso > r.right - w * 0.01f) continue
            drawRect(NOCHE.copy(alpha = 0.4f), Offset(x, y + alto * 0.5f), Size(paso * 0.82f, alto * 0.18f))
            drawRect(t.c(mezcla(BEIS, PAPEL, 0.3f)), Offset(x, y), Size(paso * 0.82f, alto * 0.56f))
        }
        if (fi == 2) drawRect(t.c(mezcla(BEIS, PAPEL, 0.3f)), Offset(r.left + w * 0.015f + paso * 7, y), Size(paso * 7.8f, alto * 0.56f))
    }
    drawRect(t.c(BEIS_OSC), Offset(r.left, r.bottom - w * 0.007f), Size(r.width, w * 0.007f))
    drawRect(t.b(0.6f), Offset(r.left, r.bottom - w * 0.007f), Size(r.width, 0.8f * u))
}

/**
 * El papel de la impresora: la tira impresa sale por arriba, sube inclinada hacia la pared y se riza hacia atrás (se ve el rollo
 * del rizo, con su cara de atrás en sombra), con sus bandas verdes, los renglones, los márgenes de arrastre con los agujeros y la
 * línea de corte entre hojas. Detrás, a la derecha, la pila de lo ya impreso, doblada en zigzag.
 */
private fun DrawScope.papelImpresoraCiber(g: MarcoCiber) {
    val u = density; val w = g.w; val ap = g.apoyo
    // La pila de lo impreso, detrás de la impresora a la derecha: los pliegues de canto, uno sobre otro.
    val pila = Rect(w * 0.74f, ap - w * 0.175f, w * 0.905f, ap - w * 0.05f)
    var yp = pila.top; var kp = 0
    while (yp < pila.bottom) {
        val dx = w * 0.005f * (if (kp % 2 == 0) 1f else -1f)
        val tk = g.t(pila.center.x, yp)
        drawRoundRect(tk.c(PAPEL, 0.95f), Offset(pila.left + dx, yp), Size(pila.width, 3.4f * u), CornerRadius(1.7f * u))
        drawRect(tk.s(0.35f), Offset(pila.left + dx, yp + 2.8f * u), Size(pila.width, 0.7f * u))
        if (kp % 3 == 1) drawRect(tk.c(RAYA, 0.95f), Offset(pila.left + dx + pila.width * 0.12f, yp + 1.1f * u), Size(pila.width * 0.76f, 1.1f * u))
        yp += 3.4f * u; kp++
    }
    drawRect(NOCHE.copy(alpha = 0.3f), Offset(pila.right - pila.width * 0.15f, pila.top), Size(pila.width * 0.15f, pila.height))
    val x0 = w * 0.56f; val x1 = w * 0.805f
    val base = ap - w * 0.085f
    val tope = ap - w * 0.235f
    val inc = w * 0.012f
    val hoja = Path().apply {
        moveTo(x0, base)
        cubicTo(x0 - w * 0.008f, base - w * 0.06f, x0 + inc * 0.5f, tope + w * 0.04f, x0 + inc, tope)
        lineTo(x1 + inc, tope)
        cubicTo(x1 + inc * 0.5f, tope + w * 0.04f, x1 + w * 0.008f, base - w * 0.06f, x1, base)
        close()
    }
    translate(w * 0.016f, w * 0.006f) { drawPath(hoja, NOCHE.copy(alpha = 0.4f)) }
    val pap = Array(6) { i -> val f = i / 5f; f to g.t(x0 + (x1 - x0) * f, (base + tope) / 2).c(PAPEL, 0.78f) }
    drawPath(hoja, Brush.horizontalGradient(*pap, startX = x0, endX = x1))
    clipPath(hoja) {
        val margen = (x1 - x0) * 0.08f
        val banda = w * 0.02f
        var y = tope + banda * 0.4f
        var i = 0
        while (y < base) {
            if (i % 2 == 0) drawRect(g.t(x0, y).c(RAYA, 0.8f), Offset(x0 + margen, y), Size(x1 - x0 - 2 * margen + inc, banda))
            y += banda; i++
        }
        // Los renglones impresos: guiones de largo al azar (formas, no letras).
        var k = 0
        y = tope + w * 0.012f
        while (y < base) {
            var x = x0 + margen * 1.4f + (if (k % 4 == 2) w * 0.02f else 0f)
            val fin = x0 + margen + (x1 - x0 - 2 * margen) * (0.35f + 0.55f * azar(6260, k))
            var j = 0
            while (x < fin) {
                val l = w * (0.008f + 0.025f * azar(6261 + k, j))
                drawRect(g.t(x, y).c(mezcla(TINTA, PARED, 0.3f)).copy(alpha = 0.75f), Offset(x, y), Size(minOf(l, fin - x), 1.3f * u))
                x += l + w * 0.006f; j++
            }
            y += w * 0.0105f; k++
        }
        val agujeros = ArrayList<Offset>()
        var yy = tope + margen * 0.5f
        while (yy < base) { agujeros.add(Offset(x0 + margen * 0.5f + inc * 0.5f, yy)); agujeros.add(Offset(x1 - margen * 0.5f + inc * 0.5f, yy)); yy += w * 0.0125f }
        drawPoints(agujeros, PointMode.Points, mezcla(g.t(x0, tope).c(PARED), NOCHE, 0.3f), 2.4f * u, StrokeCap.Round)
        val tc = g.t(x0, tope + w * 0.09f)
        var xx = x0
        while (xx < x1 + inc) { drawLine(tc.s(0.4f), Offset(xx, tope + w * 0.09f), Offset(xx + 1.6f * u, tope + w * 0.09f), 0.6f * u); xx += 3.6f * u }
        // La hoja se comba hacia la pared: arriba, la sombra del rizo; el borde derecho, más lejos de la pantalla, en sombra.
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.35f), NOCHE.copy(alpha = 0f)), tope, tope + w * 0.03f), Offset(x0, tope), Size(x1 - x0 + inc, w * 0.03f))
        drawRect(NOCHE.copy(alpha = 0.16f), Offset(x1 - (x1 - x0) * 0.2f, tope), Size((x1 - x0) * 0.25f, base - tope))
        drawRect(g.t(x0, tope).b(0.3f), Offset(x0 - w * 0.01f, tope), Size(w * 0.016f, base - tope))
    }
    // El rizo de arriba: la hoja se enrolla hacia atrás; se ve el rollo, con su cara de atrás más oscura y el filo de luz.
    val rizo = Rect(x0 + inc - w * 0.004f, tope - w * 0.03f, x1 + inc + w * 0.004f, tope + w * 0.004f)
    drawRoundRect(g.t(x0, tope).c(mezcla(PAPEL, BEIS_OSC, 0.4f), 0.9f), rizo.topLeft, rizo.size, CornerRadius(rizo.height / 2))
    drawRoundRect(NOCHE.copy(alpha = 0.3f), Offset(rizo.left, rizo.center.y), Size(rizo.width, rizo.height / 2), CornerRadius(rizo.height / 2))
    drawLine(g.t(x0, tope).b(0.7f), Offset(rizo.left + rizo.height * 0.4f, rizo.top + 1.2f * u), Offset(rizo.right - rizo.height * 0.4f, rizo.top + 1.2f * u), 1.2f * u)
}

/**
 * La impresora de agujas: la caja beis baja con la tapa de acrílico ahumado sobre el carro (se ven el cabezal y la cinta), la
 * ranura del papel, la perilla del rodillo a la derecha, el panel con tres botones y dos pilotos, la rejilla y las patas.
 */
private fun DrawScope.impresoraCiber(g: MarcoCiber, r: Rect) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    contactoCiber(Offset(r.center.x + w * 0.01f, r.bottom + w * 0.003f), r.width * 1.06f, w * 0.014f, t, 0.7f)
    // El costado derecho, que se va hacia atrás en la sombra, y la tapa vista desde arriba.
    val dv = Offset(w * 0.026f, -w * 0.016f)
    val costado = poligonoCiber(r.right - w * 0.004f, r.top + r.height * 0.3f, r.right + dv.x, r.top + r.height * 0.3f + dv.y, r.right + dv.x, r.bottom + dv.y, r.right - w * 0.004f, r.bottom)
    drawPath(costado, mezcla(g.a.hondo(BEIS), g.t(r.right, r.center.y).c(BEIS_OSC, 0.7f), 0.5f))
    // La perilla del rodillo, que asoma por el costado derecho.
    val per = Rect(r.right + dv.x * 0.3f, r.top + r.height * 0.32f, r.right + dv.x * 0.3f + w * 0.018f, r.top + r.height * 0.62f)
    drawRoundRect(t.c(NEGRO), per.topLeft, per.size, CornerRadius(w * 0.006f))
    for (k in 0 until 4) drawLine(t.b(0.4f), Offset(per.left + per.width * 0.3f, per.top + per.height * (0.2f + 0.2f * k)), Offset(per.right, per.top + per.height * (0.2f + 0.2f * k)), 0.7f * u)
    // La caja: la parte de atrás alta y el frente bajo; vista de frente, la tapa inclinada es una banda clara arriba.
    val cuerpo = Path().apply { addRoundRect(RoundRect(Rect(r.left, r.top + r.height * 0.3f, r.right, r.bottom), CornerRadius(w * 0.008f))) }
    val tapa = poligonoCiber(r.left + w * 0.01f, r.top + r.height * 0.32f, r.left + w * 0.03f, r.top, r.right - w * 0.03f, r.top, r.right - w * 0.01f, r.top + r.height * 0.32f)
    drawPath(tapa, Brush.horizontalGradient(List(5) { i -> g.t(r.left + r.width * i / 4f, r.top).c(mezcla(BEIS, Color.White, 0.05f)) }, r.left, r.right))
    // El acrílico ahumado sobre el carro, con el cabezal y la cinta.
    val acr = poligonoCiber(r.left + r.width * 0.16f, r.top + r.height * 0.3f, r.left + r.width * 0.2f, r.top + r.height * 0.04f, r.right - r.width * 0.2f, r.top + r.height * 0.04f, r.right - r.width * 0.16f, r.top + r.height * 0.3f)
    drawPath(acr, t.c(mezcla(NEGRO, CAFE, 0.3f)))
    clipPath(acr) {
        drawRect(t.c(NEGRO), Offset(r.left + r.width * 0.34f, r.top + r.height * 0.1f), Size(r.width * 0.1f, r.height * 0.18f))
        drawRect(t.c(ACERO_OSC), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.22f), Size(r.width * 0.6f, r.height * 0.03f))
        drawRect(t.b(0.45f), Offset(r.left + r.width * 0.18f, r.top + r.height * 0.05f), Size(r.width * 0.64f, 1f * u))
        drawLine(Color.White.copy(alpha = 0.12f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.3f), Offset(r.left + r.width * 0.38f, r.top), 5f * u)
    }
    drawPath(cuerpo, Brush.horizontalGradient(List(5) { i -> g.t(r.left + r.width * i / 4f, r.center.y).c(BEIS) }, r.left, r.right))
    clipPath(cuerpo) {
        pinceladas(r, BEIS_OSC, 6270, 10, u, 0.18f)
        drawRect(NOCHE.copy(alpha = 0.3f), Offset(r.right - w * 0.018f, r.top), Size(w * 0.018f, r.height))
        drawRect(NOCHE.copy(alpha = 0.25f), Offset(r.left, r.bottom - w * 0.01f), Size(r.width, w * 0.01f))
        drawRect(mezcla(BEIS_OSC, AMBAR, 0.3f).copy(alpha = 0.1f), Offset(r.left, r.top), Size(r.width, r.height * 0.5f))
    }
    drawRect(t.b(0.6f), Offset(r.left + w * 0.008f, r.top + r.height * 0.3f), Size(r.width - w * 0.016f, 1f * u))
    // La ranura de salida del papel, delante del carro, con el borde que sujeta la hoja.
    drawRect(NOCHE.copy(alpha = 0.7f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.36f), Size(r.width * 0.76f, 1.6f * u))
    // El panel de la derecha: tres botones y dos pilotos.
    val pn = Rect(r.right - r.width * 0.3f, r.top + r.height * 0.5f, r.right - r.width * 0.06f, r.bottom - r.height * 0.18f)
    drawRect(t.c(mezcla(NEGRO, PARED, 0.2f)), pn.topLeft, pn.size)
    drawRect(t.b(0.4f), Offset(pn.left, pn.bottom - 0.8f * u), Size(pn.width, 0.8f * u))
    for (k in 0 until 3) {
        val b = Rect(pn.left + pn.width * (0.06f + 0.2f * k), pn.top + pn.height * 0.25f, pn.left + pn.width * (0.2f + 0.2f * k), pn.bottom - pn.height * 0.25f)
        drawRect(t.c(mezcla(BEIS, BEIS_OSC, 0.3f)), b.topLeft, b.size)
        drawRect(t.b(0.5f), b.topLeft, Size(b.width, 0.7f * u))
    }
    pilotoCiber(Offset(pn.left + pn.width * 0.78f, pn.center.y), 1.4f * u, VERDE, true, t)
    pilotoCiber(Offset(pn.left + pn.width * 0.92f, pn.center.y), 1.4f * u, AMBAR, !g.a.noche, t)
    // La rejilla de la izquierda.
    for (k in 0 until 8) drawRect(NOCHE.copy(alpha = 0.45f), Offset(r.left + r.width * (0.06f + 0.03f * k), r.top + r.height * 0.55f), Size(r.width * 0.012f, r.height * 0.26f))
}

/**
 * La caja de disquetes de la esquina izquierda, con la tapa ahumada abierta hacia atrás: el cuerpo negro, los disquetes de pie
 * (de colores, con su etiqueta) y un separador.
 */
private fun DrawScope.cajaDisquetesArribaCiber(g: MarcoCiber, r: Rect) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    contactoCiber(Offset(r.center.x, r.bottom + w * 0.002f), r.width * 1.1f, w * 0.012f, t, 0.6f)
    // La tapa abierta, de pie detrás.
    val tapa = poligonoCiber(r.left + w * 0.004f, r.top + r.height * 0.45f, r.left + w * 0.012f, r.top - r.height * 0.35f, r.right - w * 0.004f, r.top - r.height * 0.35f, r.right, r.top + r.height * 0.45f)
    drawPath(tapa, t.c(mezcla(NEGRO, CAFE, 0.35f)).copy(alpha = 0.75f))
    drawPath(tapa, t.b(0.45f), style = Stroke(0.8f * u))
    // Los disquetes, de pie, asomando.
    val cols = listOf(Color(0xFF2F5E9E), Color(0xFFB8452F), NEGRO, Color(0xFF3F8A5A), mezcla(BEIS, BEIS_OSC, 0.3f))
    for (k in 0 until 5) {
        val x = r.left + r.width * (0.08f + 0.17f * k)
        val alto = r.height * (0.55f + 0.12f * azar(6280, k))
        val d = Rect(x, r.top + r.height * 0.4f - alto * 0.5f, x + r.width * 0.15f, r.top + r.height * 0.6f)
        drawRect(t.c(cols[k]), d.topLeft, d.size)
        drawRect(t.c(mezcla(PAPEL, NOTA, 0.2f), 0.9f), Offset(d.left + d.width * 0.15f, d.top + d.height * 0.08f), Size(d.width * 0.7f, d.height * 0.25f))
        drawRect(t.b(0.45f), d.topLeft, Size(d.width, 0.7f * u))
    }
    // El cuerpo de delante.
    val cuerpo = Rect(r.left, r.top + r.height * 0.45f, r.right, r.bottom)
    drawRect(t.c(NEGRO), cuerpo.topLeft, cuerpo.size)
    drawRect(t.b(0.5f), cuerpo.topLeft, Size(cuerpo.width, 0.9f * u))
    drawRect(NOCHE.copy(alpha = 0.3f), Offset(cuerpo.right - cuerpo.width * 0.2f, cuerpo.top), Size(cuerpo.width * 0.2f, cuerpo.height))
    drawRect(t.c(mezcla(PAPEL, NOTA, 0.25f), 0.85f), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top + cuerpo.height * 0.3f), Size(cuerpo.width * 0.45f, cuerpo.height * 0.35f))
}

/** El vaso de lápices: una lata con dos bolígrafos, un destornillador y una regla, ladeados. */
private fun DrawScope.vasoLapicesCiber(g: MarcoCiber, pie: Offset) {
    val u = density; val w = g.w
    val a = w * 0.034f; val al = w * 0.05f
    val t = g.t(pie.x, pie.y - al)
    contactoCiber(Offset(pie.x + a * 0.2f, pie.y), a * 1.5f, w * 0.01f, t, 0.65f)
    // Lo que asoma: la regla, el destornillador y los bolígrafos.
    rotate(-9f, pie) { drawRect(t.c(mezcla(BEIS, PAPEL, 0.4f)), Offset(pie.x - a * 0.35f, pie.y - al - w * 0.06f), Size(w * 0.01f, w * 0.08f)) }
    rotate(6f, pie) {
        drawRect(t.c(ACERO), Offset(pie.x + a * 0.05f, pie.y - al - w * 0.075f), Size(w * 0.003f, w * 0.05f))
        drawRoundRect(t.c(ROJO, 0.8f), Offset(pie.x - w * 0.002f, pie.y - al - w * 0.035f), Size(w * 0.009f, w * 0.04f), CornerRadius(w * 0.003f))
    }
    rotate(14f, pie) { drawRect(t.c(Color(0xFF2B3E8C)), Offset(pie.x + a * 0.2f, pie.y - al - w * 0.045f), Size(w * 0.005f, w * 0.06f)) }
    rotate(-2f, pie) { drawRect(t.c(NEGRO), Offset(pie.x - a * 0.1f, pie.y - al - w * 0.05f), Size(w * 0.005f, w * 0.06f)) }
    val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
    drawRect(t.c(ACERO), r.topLeft, r.size)
    clipRect(r.left, r.top, r.right, r.bottom) {
        planosCiber(r, t, 1.3f)
        for (k in 0 until 4) drawRect(t.s(0.15f), Offset(r.left, r.top + al * (0.2f + 0.2f * k)), Size(a, 0.8f * u))
    }
    drawOval(t.c(mezcla(ACERO, Color.White, 0.2f)), Offset(r.left, r.top - w * 0.003f), Size(a, w * 0.006f))
    drawOval(NOCHE.copy(alpha = 0.7f), Offset(r.left + a * 0.1f, r.top - w * 0.002f), Size(a * 0.8f, w * 0.004f))
}

/** La taza beis con el café, el chorreón que bajó por fuera y el asa, la del Ideario. */
internal fun DrawScope.tazaCiber(g: MarcoCiber, pie: Offset, escala: Float = 1f) {
    val u = density; val w = g.w
    val a = w * 0.044f * escala; val al = w * 0.05f * escala
    val t = g.t(Offset(pie.x, pie.y - al / 2))
    val ry = a * 0.2f
    val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
    contactoCiber(Offset(pie.x + a * 0.2f, pie.y), a * 1.5f, 2.5f * u * escala, t, 0.7f)
    drawArc(t.c(BEIS_OSC), -80f, 160f, false, Offset(r.right - a * 0.25f, r.top + al * 0.2f), Size(a * 0.5f, al * 0.5f), style = Stroke(2.4f * u * escala))
    val cuerpo = Path().apply {
        moveTo(r.left, r.top); lineTo(r.left + a * 0.03f, r.bottom - ry)
        arcTo(Rect(r.left + a * 0.03f, r.bottom - 2 * ry, r.right - a * 0.03f, r.bottom), 180f, -180f, false)
        lineTo(r.right, r.top); close()
    }
    drawPath(cuerpo, t.c(BEIS))
    clipPath(cuerpo) {
        planosCiber(r, t, 1.2f)
        drawRect(t.c(CAFE).copy(alpha = 0.55f), Offset(r.left + a * 0.3f, r.top), Size(a * 0.07f, al * 0.45f))
        drawOval(t.c(ACERO_OSC), Offset(r.left + a * 0.62f, r.top + al * 0.1f), Size(a * 0.12f, al * 0.07f))
    }
    drawOval(t.c(mezcla(BEIS, Color.White, 0.15f)), Offset(r.left, r.top - ry), Size(a, 2 * ry))
    drawOval(t.c(CAFE), Offset(r.left + a * 0.1f, r.top - ry * 0.6f), Size(a * 0.8f, ry * 1.3f))
    drawArc(t.b(0.6f), 160f, 120f, false, Offset(r.left, r.top - ry), Size(a, 2 * ry), style = Stroke(0.8f * u))
}
