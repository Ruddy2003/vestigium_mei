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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CALABAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CORDEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PELTRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SALVIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/*
 * Lo que hay en los huecos libres de las repisas de la alacena de Colonos: cosas de cocina con uso, distintas por repisa.
 */

/**
 * El hueco libre de la repisa [k], de [oc] (donde acaban sus volúmenes) al costado derecho: según la repisa, los platos de
 * peltre de pie en la ranura, el jarro con las cucharas de palo y el trapo, el molinillo de café, la plancha de hierro con
 * dos elotes, o los sobres de semillas atados con una calabacita. Si no cabe nada, queda vacío.
 */
internal fun DrawScope.huecoLibreColonos(k: Int, r: Rect, oc: Float, g: MarcoColonos) {
    val w = g.w
    val libre = g.x1 - w * 0.03f - (oc + w * 0.025f)
    if (libre < w * 0.1f) return
    val x0 = oc + w * 0.025f
    val piso = r.bottom - w * 0.006f
    val alto = r.height
    // Lo que le toca a esta repisa; si no cabe, lo siguiente que quepa.
    val anchos = floatArrayOf(0.15f, 0.1f, 0.11f, 0.14f, 0.14f)
    val primero = Math.floorMod(k * 3 + 1, 5)
    val salto = if (k % 2 == 0) 2 else 3
    val cual = (0 until 5).map { (primero + it * salto) % 5 }.firstOrNull { libre >= w * anchos[it] } ?: return
    clipRectColonos(r) {
        when (cual) {
            0 -> platosColonos(g, x0, piso, minOf(libre, w * 0.3f), alto)
            1 -> jarroColonos(g, Offset(x0 + w * 0.05f, piso), alto, libre)
            2 -> molinilloColonos(g, Offset(x0 + w * 0.06f, piso), alto)
            3 -> planchaColonos(g, Offset(x0 + w * 0.06f, piso), alto, libre)
            else -> semillasColonos(g, Offset(x0 + w * 0.04f, piso), alto, libre)
        }
    }
}

private inline fun DrawScope.clipRectColonos(r: Rect, bloque: DrawScope.() -> Unit) =
    clipRect(r.left, r.top, r.right, r.bottom + 2f, block = bloque)

/** Dos o tres platos de peltre blanco con el borde azul, de pie en la ranura y apoyados en el fondo, uno delante de otro. */
private fun DrawScope.platosColonos(g: MarcoColonos, x0: Float, piso: Float, ancho: Float, alto: Float) {
    val u = density; val w = g.w; val a = g.a
    val n = if (ancho > w * 0.22f) 3 else 2
    val rp = minOf(alto * 0.4f, w * 0.07f, ancho / (2f + 0.75f * (n - 1)))
    for (i in 0 until n) {
        val c = Offset(x0 + rp + i * rp * 0.75f, piso - rp * 0.98f)
        val k = g.k(c); val br = g.br(c)
        rotate(-8f + 5f * i, Offset(c.x, piso)) {
            drawOval(a.sombra(0.35f), Offset(c.x - rp + w * 0.008f, c.y - rp + w * 0.006f), Size(rp * 2, rp * 2))
            drawCircle(a.luz(PELTRE, k, br), rp, c)
            drawCircle(a.luz(mezcla(CRUDO, Color.White, 0.35f), k, br), rp * 0.9f, c)
            drawCircle(a.sombra(0.14f), rp * 0.6f, c, style = Stroke(1f * u))
            drawCircle(a.luz(mezcla(CRUDO, LATA, 0.4f), k, br), rp * 0.58f, c)
            // Un saltón del esmalte y el brillo.
            drawCircle(a.luz(HIERRO, k, br), w * 0.004f, Offset(c.x + rp * 0.75f, c.y + rp * 0.3f))
            drawArc(a.brillo(0.55f, maxOf(k, 0.3f), br), 200f, 70f, false, Offset(c.x - rp * 0.8f, c.y - rp * 0.8f), Size(rp * 1.6f, rp * 1.6f), style = Stroke(1.2f * u))
            drawCircle(a.sombra(0.3f), rp, c, style = Stroke(0.7f * u))
        }
    }
}

/** El jarro de peltre verde salvia con tres cucharas de palo y, al lado, el trapo de cocina doblado con sus rayas. */
private fun DrawScope.jarroColonos(g: MarcoColonos, pie: Offset, alto: Float, libre: Float) {
    val u = density; val w = g.w; val a = g.a
    val ja = w * 0.035f; val jh = minOf(alto * 0.4f, w * 0.085f)
    val cuerpo = Rect(pie.x - ja, pie.y - jh, pie.x + ja, pie.y)
    val k = g.k(cuerpo.center); val br = g.br(cuerpo.center)
    // Las cucharas, detrás del borde.
    listOf(-0.5f to -12f, 0.1f to 4f, 0.6f to 16f).forEach { (fx, giro) ->
        val b = Offset(pie.x + ja * fx, cuerpo.top + jh * 0.2f)
        rotate(giro, b) {
            val largo = jh * 1.35f
            drawLine(a.luz(PINO, k, br), b, Offset(b.x, b.y - largo), w * 0.008f, StrokeCap.Round)
            drawOval(a.luz(PINO, k, br), Offset(b.x - w * 0.011f, b.y - largo - w * 0.022f), Size(w * 0.022f, w * 0.03f))
            drawOval(a.sombra(0.2f), Offset(b.x - w * 0.006f, b.y - largo - w * 0.016f), Size(w * 0.012f, w * 0.018f))
        }
    }
    contactoColonos(Offset(pie.x, pie.y), ja * 2.6f, w * 0.012f, a, 0.38f)
    drawPath(Path().apply { moveTo(cuerpo.right, cuerpo.top + jh * 0.2f); cubicTo(cuerpo.right + ja * 0.8f, cuerpo.top + jh * 0.2f, cuerpo.right + ja * 0.8f, cuerpo.bottom - jh * 0.3f, cuerpo.right, cuerpo.bottom - jh * 0.25f) },
        a.luz(SALVIA, k, br), style = Stroke(w * 0.008f))
    drawRect(a.luz(SALVIA, k, br), cuerpo.topLeft, cuerpo.size)
    drawRect(a.brillo(0.3f, maxOf(k, 0.3f), br), Offset(cuerpo.left + ja * 0.25f, cuerpo.top), Size(ja * 0.25f, jh))
    drawRect(a.sombra(0.3f), Offset(cuerpo.right - ja * 0.6f, cuerpo.top), Size(ja * 0.6f, jh))
    drawRect(a.luz(MARINO, k, br), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width, 1.6f * u))
    drawOval(a.luz(mezcla(SALVIA, HIERRO, 0.5f), k, br), Offset(cuerpo.left, cuerpo.top - w * 0.005f), Size(cuerpo.width, w * 0.01f))
    drawCircle(a.luz(HIERRO, k, br), w * 0.004f, Offset(cuerpo.left + ja * 0.6f, cuerpo.bottom - jh * 0.3f))
    // El trapo doblado.
    if (libre > w * 0.2f) {
        val t = Rect(cuerpo.right + w * 0.035f, pie.y - w * 0.03f, cuerpo.right + w * 0.12f, pie.y)
        val kt = g.k(t.center)
        contactoColonos(Offset(t.center.x, pie.y), t.width * 1.1f, w * 0.01f, a, 0.32f)
        drawRoundRect(a.luz(CRUDO, kt, br), t.topLeft, t.size, CornerRadius(w * 0.006f))
        listOf(0.25f, 0.45f).forEach { f -> drawRect(a.luz(LADRILLO, kt, br), Offset(t.left, t.top + t.height * f), Size(t.width, t.height * 0.1f)) }
        drawRect(a.sombra(0.25f), Offset(t.left, t.top + t.height * 0.62f), Size(t.width, 1f * u))
        drawRect(a.sombra(0.2f), Offset(t.right - t.width * 0.2f, t.top), Size(t.width * 0.2f, t.height))
    }
}

/** El molinillo de café: la caja de nogal con su cajoncito y su perilla, la tapa de lata, la manivela y su puño de madera. */
private fun DrawScope.molinilloColonos(g: MarcoColonos, pie: Offset, alto: Float) {
    val u = density; val w = g.w; val a = g.a
    val lado = minOf(alto * 0.42f, w * 0.085f)
    val caja = Rect(pie.x - lado / 2, pie.y - lado, pie.x + lado / 2, pie.y)
    val k = g.k(caja.center); val br = g.br(caja.center)
    contactoColonos(Offset(pie.x, pie.y), lado * 1.3f, w * 0.012f, a, 0.4f)
    drawRect(a.luz(NOGAL, k, br), caja.topLeft, caja.size)
    drawRect(a.claro(0.2f), caja.topLeft, Size(lado * 0.2f, lado))
    drawRect(a.sombra(0.3f), Offset(caja.right - lado * 0.25f, caja.top), Size(lado * 0.25f, lado))
    // El cajoncito con su perilla.
    val cj = Rect(caja.left + lado * 0.15f, caja.top + lado * 0.55f, caja.right - lado * 0.15f, caja.bottom - lado * 0.12f)
    drawRect(a.sombra(0.45f), cj.topLeft, cj.size, style = Stroke(1f * u))
    drawCircle(a.luz(LATA, k, br), w * 0.005f, cj.center)
    // La tapa de lata, la copa y la manivela con su puño.
    val tapa = Rect(caja.left - w * 0.004f, caja.top - w * 0.012f, caja.right + w * 0.004f, caja.top)
    drawRect(a.luz(LATA, k, br), tapa.topLeft, tapa.size)
    drawOval(a.luz(mezcla(LATA, HIERRO, 0.3f), k, br), Offset(caja.center.x - lado * 0.3f, tapa.top - lado * 0.16f), Size(lado * 0.6f, lado * 0.22f))
    val eje = Offset(caja.center.x, tapa.top - lado * 0.12f)
    val mano = Offset(eje.x + lado * 0.55f, eje.y - lado * 0.22f)
    drawLine(a.luz(HIERRO, k, br), eje, mano, w * 0.006f, StrokeCap.Round)
    drawLine(a.luz(NOGAL, k, br), mano, mano + Offset(0f, -lado * 0.26f), w * 0.011f, StrokeCap.Round)
    drawCircle(a.luz(HIERRO_LUZ, k, br), w * 0.005f, eje)
    drawRect(a.brillo(0.4f, maxOf(k, 0.3f), br), tapa.topLeft, Size(tapa.width, 0.9f * u))
}

/**
 * La plancha de hierro, tendida sobre su suela y vista de lado: la punta a la izquierda, la espalda recta, la cara de arriba en
 * curva con su luz, el filo de la suela y el asa en arco sobre sus dos patas; y dos elotes tendidos a su lado.
 */
private fun DrawScope.planchaColonos(g: MarcoColonos, pie: Offset, alto: Float, libre: Float) {
    val u = density; val w = g.w; val a = g.a
    val l = minOf(alto * 0.75f, w * 0.12f)
    val x0 = pie.x - l * 0.3f; val x1 = x0 + l
    val y = pie.y
    val k = g.k(pie); val br = g.br(pie)
    val cuerpo = Path().apply {
        moveTo(x0, y); lineTo(x1, y); lineTo(x1, y - l * 0.3f)
        cubicTo(x1 - l * 0.35f, y - l * 0.36f, x0 + l * 0.25f, y - l * 0.26f, x0, y - l * 0.04f); close()
    }
    val asa = Path().apply {
        moveTo(x0 + l * 0.3f, y - l * 0.28f); lineTo(x0 + l * 0.34f, y - l * 0.5f)
        lineTo(x1 - l * 0.22f, y - l * 0.5f); lineTo(x1 - l * 0.16f, y - l * 0.32f)
    }
    contactoColonos(Offset(pie.x + l * 0.2f, y), l * 1.1f, w * 0.012f, a, 0.42f)
    drawPath(corridoColonos(asa, w * 0.006f, w * 0.004f), a.sombra(0.25f), style = Stroke(w * 0.012f))
    drawPath(asa, a.luz(HIERRO, k, br), style = Stroke(w * 0.011f))
    drawLine(a.luz(PINO_OSC, k, br), Offset(x0 + l * 0.38f, y - l * 0.5f), Offset(x1 - l * 0.26f, y - l * 0.5f), w * 0.016f, StrokeCap.Round)
    drawLine(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(x0 + l * 0.4f, y - l * 0.505f - w * 0.004f), Offset(x1 - l * 0.3f, y - l * 0.505f - w * 0.004f), 0.8f * u)
    drawPath(cuerpo, a.luz(HIERRO, k, br))
    clipPath(cuerpo) {
        drawRect(a.brillo(0.22f, maxOf(k, 0.3f), br), Offset(x0, y - l * 0.36f), Size(l, l * 0.1f))
        drawRect(a.sombra(0.3f), Offset(x0, y - l * 0.07f), Size(l, l * 0.07f))
    }
    drawLine(a.luz(HIERRO_LUZ, k, br), Offset(x0 + l * 0.02f, y - 0.8f * u), Offset(x1, y - 0.8f * u), 1f * u)
    if (libre > w * 0.24f) {
        elote(g, Offset(x1 + w * 0.02f, y - w * 0.012f), w * 0.1f, w * 0.022f, 80f, 7950)
        elote(g, Offset(x1 + w * 0.035f, y - w * 0.004f), w * 0.1f, w * 0.022f, 96f, 7951)
    }
}

/**
 * Los sobres de semillas atados con cordel, de pie contra el fondo: papel de estraza con el dibujo de la planta (una hoja, una
 * mazorca, una flor de calabaza), sin letras; y una calabacita a su lado.
 */
private fun DrawScope.semillasColonos(g: MarcoColonos, pie: Offset, alto: Float, libre: Float) {
    val u = density; val w = g.w; val a = g.a
    val sw = w * 0.05f; val sh = minOf(alto * 0.5f, w * 0.08f)
    val k = g.k(pie); val br = g.br(pie)
    contactoColonos(Offset(pie.x + sw * 0.8f, pie.y), sw * 3f, w * 0.012f, a, 0.36f)
    for (i in 0 until 3) {
        val x = pie.x + i * sw * 0.55f
        rotate(-6f + 6f * i, Offset(x + sw / 2, pie.y)) {
            val s = Rect(x, pie.y - sh, x + sw, pie.y)
            drawRect(a.sombra(0.3f), Offset(s.left + 1.5f * u, s.top + 1f * u), s.size)
            drawRect(a.luz(mezcla(PAPEL, PINO, 0.45f), k, br), s.topLeft, s.size)
            drawPath(poligonoColonos(s.left, s.top, s.right, s.top, s.center.x, s.top + sh * 0.2f), a.sombra(0.22f))
            // El dibujo de la planta.
            val d = Offset(s.center.x, s.top + sh * 0.55f)
            when (i) {
                0 -> { drawOval(a.luz(VERDE, k, br), Offset(d.x - sw * 0.14f, d.y - sh * 0.18f), Size(sw * 0.28f, sh * 0.36f)); drawLine(a.luz(PINO_OSC, k, br), d + Offset(0f, sh * 0.18f), d + Offset(0f, -sh * 0.12f), 0.6f * u) }
                1 -> { drawOval(a.luz(PaletaColonos.ELOTE, k, br), Offset(d.x - sw * 0.1f, d.y - sh * 0.2f), Size(sw * 0.2f, sh * 0.4f)); drawOval(a.luz(VERDE, k, br), Offset(d.x - sw * 0.22f, d.y), Size(sw * 0.14f, sh * 0.22f)) }
                else -> { for (j in 0 until 5) { val ang = j * 1.2566f; drawCircle(a.luz(CALABAZA, k, br), sw * 0.07f, d + Offset(sw * 0.1f * kotlin.math.cos(ang), sw * 0.1f * kotlin.math.sin(ang))) }; drawCircle(a.luz(PaletaColonos.GIRASOL, k, br), sw * 0.05f, d) }
            }
        }
    }
    drawLine(g.c(CORDEL, pie), Offset(pie.x - 1f * u, pie.y - sh * 0.35f), Offset(pie.x + sw * 2.15f, pie.y - sh * 0.3f), 1.2f * u)
    if (libre > w * 0.24f) {
        val c = Offset(pie.x + sw * 3.1f, pie.y - w * 0.022f)
        contactoColonos(Offset(c.x, pie.y), w * 0.06f, w * 0.01f, a, 0.36f)
        drawOval(a.luz(VERDE, k, br), Offset(c.x - w * 0.028f, c.y - w * 0.022f), Size(w * 0.056f, w * 0.044f))
        drawOval(a.brillo(0.3f, maxOf(k, 0.3f), br), Offset(c.x - w * 0.02f, c.y - w * 0.018f), Size(w * 0.02f, w * 0.012f))
        drawOval(a.sombra(0.25f), Offset(c.x - w * 0.005f, c.y - w * 0.008f), Size(w * 0.03f, w * 0.03f))
        drawLine(a.luz(PINO_OSC, k, br), Offset(c.x, c.y - w * 0.02f), Offset(c.x + w * 0.008f, c.y - w * 0.032f), 1.6f * u)
    }
}
