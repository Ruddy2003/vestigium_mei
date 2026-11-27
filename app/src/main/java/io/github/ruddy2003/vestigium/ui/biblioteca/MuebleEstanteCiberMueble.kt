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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CHAPA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TABLERO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/** La letra de la cinta de rotular del mes: clara sobre la cinta, como los temas del Ideario. */
private val TEXTO_CINTA_CIBER = Color(0xFFBDEFF5)

/**
 * El fondo de un casillero ([r], el hueco de la repisa [k]): el tablero perforado de aglomerado con la luz que le llega a lo
 * ancho y sus agujeros en rejilla (cada uno con su labio de luz), manchas y roces; el charco de la luz que tiene cerca; la sombra
 * franca de la tabla de arriba y la del costado que no da a la luz; los cantos de dentro de los costados, con su columna de
 * ranuras para las grapas; y la cara de arriba de la repisa de abajo, donde pisa lo guardado.
 */
internal fun DrawScope.casilleroCiber(k: Int, r: Rect, g: MarcoCiber) {
    val u = density; val w = g.w
    if (r.width <= 1f || r.height <= 1f) return
    bandaCiber(r.left, r.right, r.top, r.bottom, TABLERO, g, 0.92f, ym = r.top + r.height * 0.6f)
    clipRect(r.left, r.top, r.right, r.bottom) {
        // El aglomerado: manchas de humedad y roces.
        pinceladas(r, NOCHE, 6300 + k, 9, u, 0.16f)
        // Los agujeros, en una sola pasada por tercio (cada tercio con el labio de su luz).
        val paso = w * 0.031f
        if (paso > 1f) {
            for (tercio in 0 until 3) {
                val xa = r.left + r.width * tercio / 3f; val xb = r.left + r.width * (tercio + 1) / 3f
                val huecos = ArrayList<Offset>(); val labios = ArrayList<Offset>()
                var y = r.top + paso * 0.55f
                while (y < r.bottom - paso * 0.2f) {
                    var x = r.left + paso * 0.5f
                    while (x < r.right) {
                        if (x >= xa && x < xb) { huecos.add(Offset(x, y)); labios.add(Offset(x + 0.4f * u, y + 1.3f * u)) }
                        x += paso
                    }
                    y += paso
                }
                val t = g.t((xa + xb) / 2, r.top + r.height * 0.6f)
                drawPoints(labios, PointMode.Points, t.b(0.3f), 2.6f * u, StrokeCap.Round)
                drawPoints(huecos, PointMode.Points, NOCHE.copy(alpha = 0.86f), 2.6f * u, StrokeCap.Round)
            }
        }
        // Un roce claro donde se arrastran las cosas y el cerco de algo húmedo.
        val roce = Offset(r.left + r.width * (0.2f + 0.6f * azar(6310, k)), r.bottom - r.height * 0.22f)
        drawOval(g.t(roce).c(mezcla(TABLERO, BEIS, 0.4f)).copy(alpha = 0.35f), Offset(roce.x - w * 0.06f, roce.y - w * 0.01f), Size(w * 0.12f, w * 0.02f))
        // El charco de cada luz que le llega de cerca.
        charcosCasilleroCiber(r, g)
        // La sombra franca de la tabla de arriba y el degradado que la sigue.
        val sTop = r.height * 0.12f
        drawRect(NOCHE.copy(alpha = 0.6f), r.topLeft, Size(r.width, sTop))
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.42f), NOCHE.copy(alpha = 0f)), r.top + sTop, r.top + r.height * 0.46f), Offset(r.left, r.top + sTop), Size(r.width, r.height * 0.34f))
        // Los cantos de dentro de los costados, con su columna de ranuras.
        val canto = w * 0.024f
        val tl = g.t(r.left, r.center.y); val tr = g.t(r.right, r.center.y)
        drawPath(poligonoCiber(r.left, r.top, r.left + canto, r.top + canto * 1.3f, r.left + canto, r.bottom, r.left, r.bottom), tl.c(mezcla(CHAPA, BEIS_OSC, 0.35f), if (tl.izq) 0.6f else 1.05f))
        drawPath(poligonoCiber(r.right, r.top, r.right - canto, r.top + canto * 1.3f, r.right - canto, r.bottom, r.right, r.bottom), tr.c(mezcla(CHAPA, BEIS_OSC, 0.35f), if (tr.izq) 1.05f else 0.6f))
        val ranuras = ArrayList<Offset>()
        var y = r.top + canto * 1.8f
        while (y < r.bottom - w * 0.01f) {
            ranuras.add(Offset(r.left + canto * 0.5f, y)); ranuras.add(Offset(r.left + canto * 0.5f, y + w * 0.006f))
            ranuras.add(Offset(r.right - canto * 0.5f, y)); ranuras.add(Offset(r.right - canto * 0.5f, y + w * 0.006f))
            y += w * 0.02f
        }
        drawPoints(ranuras, PointMode.Lines, NOCHE.copy(alpha = 0.75f), 2.2f * u, StrokeCap.Butt)
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(r.left + canto, r.top), Size(1.2f * u, r.height))
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(r.right - canto - 1.2f * u, r.top), Size(1.2f * u, r.height))
        // La sombra del costado que no da a la luz, sobre el fondo.
        if (tl.izq) drawRect(NOCHE.copy(alpha = 0.3f), Offset(r.right - canto - w * 0.035f, r.top), Size(w * 0.035f, r.height))
        else drawRect(NOCHE.copy(alpha = 0.3f), Offset(r.left + canto, r.top), Size(w * 0.035f, r.height))
        // La cara de arriba de la repisa de abajo (vista desde arriba), con su filo.
        val cara = w * 0.016f
        bandaCiber(r.left, r.right, r.bottom - cara, r.bottom, mezcla(CHAPA, Color.White, 0.05f), g, 0.95f)
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(r.left, r.bottom - cara), Size(r.width, 1.3f * u))
    }
}

/** Los charcos de las luces que tiene cerca un casillero: el de cada luz, donde le llega más. */
private fun DrawScope.charcosCasilleroCiber(r: Rect, g: MarcoCiber) {
    val w = g.w
    val m = MuebleEstanteCiber
    val k = FloatArray(3)
    val donde = arrayOf(
        Offset((w * m.PANT_X).coerceIn(r.left, r.right), r.top + r.height * 0.55f),
        Offset(r.right - r.width * 0.1f, r.top + r.height * 0.5f),
        Offset((w * m.AMB_X).coerceIn(r.left, r.right) + r.width * 0.05f, r.top + r.height * 0.6f),
    )
    for (i in 0 until 3) {
        if (g.a.fuerzas[i] <= 0f) continue
        val p = donde[i]
        lucesCiber(p.x / w, p.y / w, g.a, g.regY, g.ambY, k)
        val ki = k[i]
        if (ki < 0.22f) continue
        mancha(p, r.height * (0.7f + 0.9f * ki), g.a.colores[i].copy(alpha = ((if (i == 0) 0.3f else 0.38f) * ki).coerceAtMost(0.4f)))
    }
}

/**
 * Los costados de la estantería, de la corona al zócalo: la cara de delante de la chapa doblada con la luz que le llega a lo
 * alto, el canto de fuera redondeado con su filo y el de dentro en sombra, la columna de ranuras cuadradas, los tornillos de
 * cada repisa y la pintura saltada.
 */
internal fun DrawScope.costadosCiber(g: MarcoCiber, tablas: List<Rect>) {
    val u = density; val w = g.w
    val y0 = g.ch; val y1 = g.armario
    if (y1 <= y0) return
    listOf(g.ml to g.x0, g.x1 to g.mr).forEachIndexed { lado, (a, b) ->
        columnaCiber(a, b, y0, y1, CHAPA, g, if (lado == 0) 1.02f else 0.9f, n = 10)
        pinceladas(Rect(a, y0, b, y1), NOCHE, 6320 + lado, 12, u, 0.12f)
        // El canto de fuera redondeado y el de dentro, la junta de la chapa doblada.
        val ancho = b - a
        if (lado == 0) {
            columnaCiber(a, a + ancho * 0.16f, y0, y1, mezcla(CHAPA, Color.White, 0.16f), g, 1.1f, n = 6)
            drawRect(NOCHE.copy(alpha = 0.4f), Offset(b - ancho * 0.12f, y0), Size(ancho * 0.12f, y1 - y0))
        } else {
            drawRect(NOCHE.copy(alpha = 0.3f), Offset(b - ancho * 0.2f, y0), Size(ancho * 0.2f, y1 - y0))
            columnaCiber(a, a + ancho * 0.1f, y0, y1, mezcla(CHAPA, Color.White, 0.1f), g, 1.05f, n = 6)
        }
        // La columna de ranuras cuadradas, cada una con su labio de luz abajo.
        val xr = a + ancho * 0.5f
        val ranuras = ArrayList<Offset>(); val labios = ArrayList<Offset>()
        var y = y0 + w * 0.03f
        while (y < y1 - w * 0.02f) { ranuras.add(Offset(xr, y)); labios.add(Offset(xr, y + 2.4f * u)); y += w * 0.024f }
        drawPoints(labios, PointMode.Points, g.t(xr, (y0 + y1) / 2).b(0.35f), 4.2f * u, StrokeCap.Butt)
        drawPoints(ranuras, PointMode.Points, NOCHE.copy(alpha = 0.82f), 3.8f * u, StrokeCap.Butt)
        // Los tornillos de las repisas.
        tablas.forEach { tb -> remacheCiber(Offset(xr, tb.center.y), 2.6f * u, g.t(xr, tb.center.y)) }
        // La pintura saltada en el canto: asoma el acero.
        for (j in 0 until 4) {
            val p = Offset(if (lado == 0) a + ancho * 0.15f else b - ancho * 0.2f, y0 + (y1 - y0) * azar(6330 + lado, j))
            val t = g.t(p)
            val rr = (1.5f + 2f * azar(6331 + lado, j)) * u
            drawOval(t.c(ACERO), Offset(p.x - rr * 0.8f, p.y - rr * 1.3f), Size(rr * 1.6f, rr * 2.6f))
            drawArc(t.s(0.45f), 90f, 180f, false, Offset(p.x - rr * 0.8f, p.y - rr * 1.3f), Size(rr * 1.6f, rr * 2.6f), style = Stroke(0.6f * u))
        }
    }
    // La etiqueta de inventario del banco, de aluminio y sin letras, en el costado izquierdo, y su raya.
    val ey = y0 + w * 0.12f
    val et = Rect(g.ml + w * 0.008f, ey, g.x0 - w * 0.01f, ey + w * 0.05f)
    if (et.width > 2f && et.bottom < y1) {
        val t = g.t(et.center)
        drawRect(t.s(0.4f), et.translate(1f * u, 1.4f * u).topLeft, et.size)
        drawRect(t.c(ALUMINIO), et.topLeft, et.size)
        drawRect(t.c(mezcla(ROJO, NOCHE, 0.4f)), Offset(et.left, et.top + et.height * 0.12f), Size(et.width, et.height * 0.16f))
        drawRect(t.b(0.5f), et.topLeft, Size(et.width, 0.7f * u))
    }
}

/**
 * Una tabla (el canto de una repisa, [r]): la chapa doblada con el canto redondo de arriba y su filo de luz, el canal
 * portaetiquetas de aluminio a lo largo con sus dos labios, el pliegue de abajo en sombra, la pintura saltada y, donde no va el
 * mes, una tarjeta vieja en el canal.
 */
internal fun DrawScope.tablaCiber(k: Int, r: Rect, ultima: Boolean, g: MarcoCiber) {
    val u = density; val w = g.w
    if (r.width <= 1f || r.height <= 1f) return
    val h = r.height
    bandaCiber(r.left, r.right, r.top, r.bottom + 1f, CHAPA, g)
    bandaCiber(r.left, r.right, r.top, r.top + h * 0.16f, mezcla(CHAPA, Color.White, 0.2f), g, 1.1f)
    drawRect(g.t(w * 0.3f, r.top).b(0.75f), r.topLeft, Size(r.width, 1.2f * u))
    drawRect(NOCHE.copy(alpha = 0.25f), Offset(r.left, r.top + h * 0.16f), Size(r.width, 1f * u))
    // El canal portaetiquetas de aluminio.
    val c0 = r.top + h * 0.26f; val c1 = r.bottom - h * 0.24f
    val cx0 = r.left + w * 0.018f; val cx1 = r.right - w * 0.018f
    bandaCiber(cx0, cx1, c0, c1, mezcla(ALUMINIO, ACERO_OSC, 0.35f), g)
    bandaCiber(cx0, cx1, c0, c0 + h * 0.08f, ALUMINIO, g, 1.15f)
    bandaCiber(cx0, cx1, c1 - h * 0.08f, c1, ALUMINIO, g, 1.05f)
    drawRect(NOCHE.copy(alpha = 0.4f), Offset(cx0, c0 + h * 0.08f), Size(cx1 - cx0, 1.2f * u))
    drawRect(g.t(w * 0.3f, c1).b(0.5f), Offset(cx0, c1 - 0.8f * u), Size(cx1 - cx0, 0.8f * u))
    listOf(cx0, cx1).forEach { x -> remacheCiber(Offset(x + (if (x == cx0) w * 0.008f else -w * 0.008f), (c0 + c1) / 2), 1.6f * u, g.t(x, c0)) }
    // Una tarjeta vieja en el canal (las tablas sin mes): papel amarillento con un garabato.
    if (azar(6340, k) > 0.45f) {
        val tx = r.left + r.width * (0.12f + 0.55f * azar(6341, k))
        val tr = Rect(tx, c0 + h * 0.1f, tx + w * 0.11f, c1 - h * 0.1f)
        val t = g.t(tr.center)
        drawRect(t.c(mezcla(PAPEL, NOTA, 0.35f), 0.85f), tr.topLeft, tr.size)
        garabatosCiber(Rect(tr.left + tr.width * 0.1f, tr.top + tr.height * 0.2f, tr.right - tr.width * 0.1f, tr.bottom - tr.height * 0.2f), 1, t.c(mezcla(PARED, NOCHE, 0.3f)), 6342 + k, 0.9f * u)
    }
    // El pliegue de abajo y la pintura saltada.
    drawRect(NOCHE.copy(alpha = 0.5f), Offset(r.left, r.bottom - h * 0.1f), Size(r.width, h * 0.1f))
    for (j in 0 until 2) {
        val p = Offset(r.left + r.width * azar(6345 + k, j), r.top + h * (0.12f + 0.75f * azar(6346 + k, j)))
        if (p.y > c0 && p.y < c1) continue
        val t = g.t(p)
        val rr = (1.6f + 1.8f * azar(6347 + k, j)) * u
        drawOval(t.c(ACERO), Offset(p.x - rr * 1.4f, p.y - rr * 0.7f), Size(rr * 2.8f, rr * 1.4f))
    }
    if (ultima) drawRect(NOCHE.copy(alpha = 0.35f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
}

/**
 * El mes, en cinta de rotular en relieve pegada en el canal (la de los temas del Ideario): oscura, con los extremos cortados en
 * bisel, el brillo del plástico arriba, su sombra y la letra clara encima. El color de la cinta (negra, azul o roja) sale del
 * texto.
 */
internal fun DrawScope.rotuloCiber(p: PlacaEstante, medidor: TextMeasurer, g: MarcoCiber) {
    val u = density
    val r = p.r
    val capa = capaEn(medidor, p.texto, Rect(r.left, r.top, r.right, r.bottom), Letra.terminal, TEXTO_CINTA_CIBER, 11f, espaciado = 1.2f, minimo = 7f) ?: return
    val alto = minOf(r.height * 0.78f, capa.size.height * 1.35f)
    val ancho = minOf(r.width, capa.size.width + alto * 1.4f)
    val c = r.center
    val cinta = Rect(c.x - ancho / 2, c.y - alto / 2, c.x + ancho / 2, c.y + alto / 2)
    val t = g.t(c)
    val col = when (Math.floorMod(p.texto.hashCode(), 3)) { 0 -> NEGRO; 1 -> mezcla(PARED, NOCHE, 0.2f); else -> mezcla(ROJO, NOCHE, 0.6f) }
    val corte = alto * 0.2f
    val forma = poligonoCiber(cinta.left + corte, cinta.top, cinta.right, cinta.top, cinta.right - corte, cinta.bottom, cinta.left, cinta.bottom)
    translate(1.2f * u, 1.6f * u) { drawPath(forma, NOCHE.copy(alpha = 0.6f)) }
    drawPath(forma, mezcla(mezcla(col, NOCHE, 0.4f), col, (0.45f * t.k).coerceAtMost(0.45f)))
    clipPath(forma) {
        drawRect(t.b(0.35f), cinta.topLeft, Size(cinta.width, alto * 0.14f))
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(cinta.left, cinta.bottom - alto * 0.14f), Size(cinta.width, alto * 0.14f))
    }
    // La letra en relieve: su sombra corrida y la letra clara, apenas teñida por la luz que le toca.
    drawText(capa, topLeft = Offset(c.x - capa.size.width / 2f + 0.7f * u, c.y - capa.size.height / 2f + 0.8f * u), alpha = 0.45f, color = NOCHE)
    drawText(capa, topLeft = Offset(c.x - capa.size.width / 2f, c.y - capa.size.height / 2f), color = mezcla(TEXTO_CINTA_CIBER, t.l, 0.15f * t.k.coerceAtMost(1f)))
}

/**
 * La regleta atornillada de pie en el costado derecho, la del Ideario en otra postura: el cuerpo negro, el interruptor rojo de
 * balancín encendido con su resplandor, cuatro tomas beis (dos con su clavija y su cable) y los tornillos.
 */
internal fun DrawScope.reglaCiber(g: MarcoCiber) {
    val u = density; val w = g.w
    if (g.fondo - g.ch < w * 0.2f) return
    val x0 = g.x1 + w * 0.006f; val x1 = g.mr - w * 0.004f
    val alto = w * 0.27f
    val y0 = (g.regY * w - w * 0.025f).coerceIn(g.ch + w * 0.02f, maxOf(g.ch + w * 0.02f, g.fondo - alto - w * 0.02f))
    val r = Rect(x0, y0, x1, y0 + alto)
    val t = g.t(r.center)
    drawRoundRect(NOCHE.copy(alpha = 0.55f), r.translate(-w * 0.006f, w * 0.006f).topLeft, r.size, CornerRadius(w * 0.006f))
    columnaCiber(r.left, r.right, r.top, r.bottom, NEGRO, g, 1.2f, n = 6)
    drawRect(t.b(0.45f), Offset(r.left, r.top), Size(1f * u, r.height))
    // El interruptor de balancín, encendido.
    val sw = Rect(r.left + r.width * 0.18f, r.top + w * 0.012f, r.right - r.width * 0.18f, r.top + w * 0.05f)
    mancha(sw.center, w * 0.1f, ROJO.copy(alpha = if (g.a.noche) 0.22f else 0.34f))
    drawRect(ROJO.copy(alpha = 0.35f), Offset(sw.left - 2f * u, sw.top - 2f * u), Size(sw.width + 4f * u, sw.height + 4f * u))
    drawRect(mezcla(ROJO, NOCHE, 0.2f), sw.topLeft, sw.size)
    drawRect(mezcla(ROJO, Color.White, if (g.a.noche) 0.25f else 0.4f), sw.topLeft, Size(sw.width, sw.height * 0.45f))
    // Las tomas: dos con clavija y cable.
    for (k in 0 until 4) {
        val c = Offset(r.center.x, r.top + w * (0.085f + 0.05f * k))
        val tk = g.t(c)
        val s = r.width * 0.64f
        drawRoundRect(tk.c(BEIS_OSC), Offset(c.x - s / 2, c.y - s / 2), Size(s, s), CornerRadius(s * 0.2f))
        if (k == 1 || k == 3) {
            drawRect(tk.c(NEGRO), Offset(c.x - s * 0.42f, c.y - s * 0.4f), Size(s * 0.84f, s * 0.8f))
            drawRect(tk.b(0.5f), Offset(c.x - s * 0.42f, c.y - s * 0.4f), Size(s * 0.84f, 0.8f * u))
            val cab = Path().apply {
                moveTo(c.x - s * 0.42f, c.y)
                if (k == 1) cubicTo(c.x - w * 0.03f, c.y, c.x - w * 0.02f, c.y - w * 0.1f, g.mr - w * 0.004f, c.y - w * 0.16f)
                else cubicTo(c.x - w * 0.03f, c.y + w * 0.02f, c.x - w * 0.01f, c.y + w * 0.12f, c.x + w * 0.004f, c.y + w * 0.22f)
            }
            drawPath(cab, tk.c(NEGRO), style = Stroke(2.2f * u))
            drawPath(cab, tk.b(0.35f), style = Stroke(0.6f * u))
        } else {
            drawCircle(NOCHE, s * 0.08f, c - Offset(0f, s * 0.18f))
            drawCircle(NOCHE, s * 0.08f, c + Offset(0f, s * 0.18f))
        }
    }
    listOf(r.top + w * 0.006f, r.bottom - w * 0.008f).forEach { y -> remacheCiber(Offset(r.center.x, y), 1.6f * u, t) }
}
