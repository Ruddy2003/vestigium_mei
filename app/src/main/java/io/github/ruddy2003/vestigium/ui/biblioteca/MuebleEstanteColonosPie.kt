package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.BRASA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.ELOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/*
 * El pie de la alacena de Colonos (el piso, el cuerpo bajo, el tapete, la estufa con su tetera, la leña y la cubeta) y lo que
 * hay en los huecos libres de las repisas (en `MuebleEstanteColonosHuecos.kt`).
 */

/** El pie: el piso y el zócalo, el cuerpo bajo, el tapete trenzado y, encima, la estufa, la leña y la cubeta con elotes. */
internal fun DrawScope.pieColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    if (g.h - g.fondo < 4f * u) return
    // La junta de la encimera con el cuerpo bajo, que puede caer a medio píxel.
    filaJuntaColonos(g.fondo, g.borde, w - g.borde, g.c(PaletaColonos.MAIZ_OSC, Offset(w * 0.5f, g.fondo)))
    pisoColonos(g)
    cuerpoBajoColonos(g)
    tapeteColonos(g, Offset(w * 0.54f, g.fondo + w * 0.29f), w * 0.29f, w * 0.058f)
    lenaColonos(g, Offset(w * 0.3f, g.fondo + w * 0.338f))
    cubetaColonos(g, Offset(w * 0.93f, g.fondo + w * 0.345f))
    estufaColonos(g, Offset(w * MuebleEstanteColonos.BRASA_X, g.fondo + w * 0.33f))
}

/**
 * La estufa de lámina, cortada por el borde de la izquierda: el tambor de chapa negra con sus dos aros, el óxido y las
 * chorreras, las tres patas, la puertita con su pestillo y las ranuras por donde se ven las brasas, la plancha de arriba con la
 * tetera de peltre rojo y el tubo que sale por el borde. De noche las brasas arden y alumbran el piso y lo bajo de la alacena.
 */
internal fun DrawScope.estufaColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val r = w * 0.115f
    val top = pie.y - w * 0.2f
    val cuerpo = Rect(pie.x - r, top, pie.x + r, pie.y - w * 0.02f)
    val c = cuerpo.center
    // El hierro de la estufa casi no devuelve la luz de su propia brasa: de noche se queda oscuro y la brasa se ve por la puerta.
    val k = g.k(c) * (if (a.oscuro) 0.4f else 1f); val br = g.br(c)
    contactoColonos(Offset(pie.x + r * 0.2f, pie.y), r * 2.4f, w * 0.02f, a, 0.4f)
    // Las patas.
    listOf(-0.6f, 0.15f, 0.8f).forEach { f ->
        val x = pie.x + r * f
        drawPath(poligonoColonos(x - w * 0.008f, cuerpo.bottom - 1f, x + w * 0.008f, cuerpo.bottom - 1f, x + w * 0.005f, pie.y, x - w * 0.006f, pie.y), a.luz(HIERRO, k, br))
    }
    // El tambor: un cilindro de chapa, con su fondo y su boca en elipse, la luz de cilindro (de día, de la ventana a la
    // izquierda) y la sombra del otro lado; el óxido y sus chorreras; los dos aros que siguen la curva.
    val el = w * 0.022f
    val tambor = Path().apply {
        moveTo(cuerpo.left, cuerpo.top); lineTo(cuerpo.left, cuerpo.bottom - el)
        arcTo(Rect(cuerpo.left, cuerpo.bottom - el * 2, cuerpo.right, cuerpo.bottom), 180f, -180f, false)
        lineTo(cuerpo.right, cuerpo.top)
        arcTo(Rect(cuerpo.left, cuerpo.top - el, cuerpo.right, cuerpo.top + el), 0f, 180f, false)
        close()
    }
    drawPath(tambor, a.luz(HIERRO, k, br))
    clipPath(tambor) {
        val an = cuerpo.width
        drawRect(a.sombra(0.25f), Offset(cuerpo.left, cuerpo.top - el), Size(an * 0.1f, cuerpo.height + el * 2))
        if (!a.oscuro) {
            drawRect(a.claro(0.12f), Offset(cuerpo.left + an * 0.14f, cuerpo.top - el), Size(an * 0.32f, cuerpo.height + el * 2))
            drawRect(a.claro(0.18f), Offset(cuerpo.left + an * 0.24f, cuerpo.top - el), Size(an * 0.1f, cuerpo.height + el * 2))
        } else {
            drawRect(a.brillo(0.2f, k, br), Offset(cuerpo.left + an * 0.36f, cuerpo.top - el), Size(an * 0.26f, cuerpo.height + el * 2))
        }
        drawRect(a.sombra(0.22f), Offset(cuerpo.left + an * 0.62f, cuerpo.top - el), Size(an * 0.38f, cuerpo.height + el * 2))
        drawRect(a.sombra(0.25f), Offset(cuerpo.left + an * 0.82f, cuerpo.top - el), Size(an * 0.18f, cuerpo.height + el * 2))
        val oxido = Path()
        for (i in 0 until 9) {
            val x = cuerpo.left + an * azar(7900, i)
            oxido.addOval(Rect(x, cuerpo.top + cuerpo.height * (0.1f + 0.5f * azar(7901, i)), x + w * 0.007f, cuerpo.top + cuerpo.height * (0.4f + 0.6f * azar(7902, i))))
        }
        drawPath(oxido, a.luz(OXIDO, k, br).copy(alpha = 0.45f))
        listOf(0.16f, 0.84f).forEach { f ->
            val y = cuerpo.top + cuerpo.height * f
            val aro = Rect(cuerpo.left, y - el, cuerpo.right, y + el)
            drawArc(a.luz(HIERRO_LUZ, k, br).copy(alpha = 0.6f), 0f, 180f, false, aro.topLeft, aro.size, style = Stroke(w * 0.005f))
            drawArc(a.sombra(0.4f), 0f, 180f, false, aro.topLeft + Offset(0f, w * 0.005f), aro.size, style = Stroke(1.2f * u))
        }
    }
    // La plancha de arriba y el tubo.
    val plancha = Rect(cuerpo.left - w * 0.006f, top - w * 0.024f, cuerpo.right + w * 0.006f, top + w * 0.02f)
    drawOval(a.luz(HIERRO, k, br), plancha.topLeft + Offset(0f, w * 0.006f), plancha.size)
    drawOval(a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.3f), k, br), plancha.topLeft, plancha.size)
    drawOval(a.luz(HIERRO, k, br), plancha.deflate(w * 0.014f).topLeft, plancha.deflate(w * 0.014f).size, style = Stroke(1f * u))
    drawArc(a.brillo(0.4f, maxOf(k, 0.2f), br), 190f, 100f, false, plancha.topLeft, plancha.size, style = Stroke(1f * u))
    val tubo = Rect(-2f, top - w * 0.1f, pie.x - r * 0.3f, top - w * 0.06f)
    drawRect(a.luz(HIERRO, k, br), Offset(pie.x - r * 0.62f, tubo.bottom - 1f), Size(w * 0.042f, plancha.center.y - tubo.bottom))
    drawRect(a.luz(HIERRO, k, br), tubo.topLeft, tubo.size)
    drawRect(a.brillo(0.22f, maxOf(k, 0.2f), br), Offset(tubo.left, tubo.top + tubo.height * 0.15f), Size(tubo.width, tubo.height * 0.15f))
    drawRect(a.sombra(0.4f), Offset(tubo.left, tubo.bottom - tubo.height * 0.3f), Size(tubo.width, tubo.height * 0.3f))
    drawRect(a.luz(HIERRO_LUZ, k, br).copy(alpha = 0.5f), Offset(pie.x - r * 0.66f, tubo.top - 1f), Size(w * 0.05f, tubo.height + 2f), style = Stroke(1.2f * u))
    // La puertita: el marco, las ranuras con las brasas y el pestillo.
    val br0 = Offset(w * MuebleEstanteColonos.BRASA_X, g.fondo + w * MuebleEstanteColonos.BRASA_DY)
    val puerta = Rect(br0.x - w * 0.04f, br0.y - w * 0.028f, br0.x + w * 0.045f, br0.y + w * 0.028f)
    drawRect(a.sombra(0.5f), Offset(puerta.left + 1f * u, puerta.top + 1.4f * u), puerta.size)
    drawRect(a.luz(mezcla(HIERRO, HIERRO_LUZ, 0.3f), k, br), puerta.topLeft, puerta.size)
    drawRect(a.brillo(0.3f, maxOf(k, 0.3f), br), puerta.topLeft, Size(puerta.width, 1f * u))
    val arde = a.lumbre
    for (i in 0 until 4) {
        val y = puerta.top + puerta.height * (0.2f + 0.18f * i)
        val ranura = Rect(puerta.left + puerta.width * 0.14f, y, puerta.right - puerta.width * 0.22f, y + puerta.height * 0.08f)
        val fuego = if (a.oscuro) mezcla(Color(0xFFFFF0B0), BRASA, 0.1f + 0.22f * i) else if (a.noche) mezcla(BRASA, HIERRO, 0.3f) else mezcla(BRASA, HIERRO, 0.6f)
        drawRect(fuego, ranura.topLeft, ranura.size)
        if (a.oscuro) drawRect(Color.White.copy(alpha = 0.5f - 0.1f * i), Offset(ranura.left + ranura.width * 0.25f, ranura.top), Size(ranura.width * 0.4f, ranura.height * 0.5f))
    }
    drawCircle(a.luz(LATA, k, br), w * 0.006f, Offset(puerta.right - puerta.width * 0.1f, puerta.center.y))
    if (arde && a.oscuro) {
        // El abanico de la brasa en el piso, que se abre desde la puertita hacia quien mira y a la derecha, en tres capas.
        clipRect(0f, g.suelo, w, g.h) {
            for (j in 0 until 3) {
                val e = w * 0.05f * j
                drawPath(poligonoColonos(puerta.left - w * 0.01f, puerta.bottom, puerta.right + w * 0.02f, puerta.bottom,
                    puerta.right + w * 0.42f - e, g.h + 2f, puerta.left - w * 0.08f + e * 0.5f, g.h + 2f), BRASA.copy(alpha = 0.08f))
            }
        }
        mancha(puerta.center, w * 0.34f, BRASA.copy(alpha = 0.24f))
        mancha(puerta.center, w * 0.09f, Color(0xFFFFC77A).copy(alpha = 0.45f))
        mancha(Offset(puerta.center.x + w * 0.14f, pie.y + w * 0.01f), w * 0.24f, BRASA.copy(alpha = 0.16f))
    }
    teteraColonos(g, Offset(pie.x + r * 0.22f, plancha.center.y + w * 0.004f))
}

/** La tetera de peltre rojo sobre la estufa: el cuerpo panzón, la tapa con perilla, el pico, el asa de alambre y su brillo. */
private fun DrawScope.teteraColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val rx = w * 0.05f; val ry = w * 0.036f
    val c = Offset(pie.x, pie.y - ry)
    val k = g.k(c); val br = g.br(c)
    val cuerpo = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
    val pico = Path().apply {
        moveTo(c.x + rx * 0.8f, c.y - ry * 0.1f)
        quadraticTo(c.x + rx * 1.4f, c.y - ry * 0.3f, c.x + rx * 1.55f, c.y - ry * 1.05f)
        lineTo(c.x + rx * 1.4f, c.y - ry * 1.05f)
        quadraticTo(c.x + rx * 1.2f, c.y - ry * 0.4f, c.x + rx * 0.7f, c.y - ry * 0.5f); close()
    }
    val asa = Path().apply { moveTo(c.x - rx * 0.6f, c.y - ry * 0.85f); cubicTo(c.x - rx * 0.5f, c.y - ry * 2.6f, c.x + rx * 0.5f, c.y - ry * 2.6f, c.x + rx * 0.6f, c.y - ry * 0.85f) }
    drawPath(asa, a.luz(HIERRO, k, br), style = Stroke(1.3f * u))
    drawPath(pico, a.luz(LADRILLO, k, br))
    drawOval(a.luz(LADRILLO, k, br), cuerpo.topLeft, cuerpo.size)
    clipPath(Path().apply { addOval(cuerpo) }) {
        drawOval(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(cuerpo.left + rx * 0.3f, cuerpo.top + ry * 0.2f), Size(rx * 0.5f, ry * 0.5f))
        drawRect(a.sombra(0.3f), Offset(c.x + rx * 0.3f, cuerpo.top), Size(rx, cuerpo.height))
        drawRect(a.sombra(0.25f), Offset(cuerpo.left, c.y + ry * 0.5f), Size(cuerpo.width, ry))
    }
    drawOval(a.luz(mezcla(LADRILLO, HIERRO, 0.2f), k, br), Offset(c.x - rx * 0.38f, cuerpo.top - ry * 0.18f), Size(rx * 0.76f, ry * 0.36f))
    drawCircle(a.luz(HIERRO, k, br), w * 0.006f, Offset(c.x, cuerpo.top - ry * 0.2f))
    drawCircle(a.luz(HIERRO, k, br), w * 0.004f, Offset(c.x - rx * 0.45f, c.y + ry * 0.3f))
    if (a.oscuro && k > 0.1f) drawArc(a.brillo(0.7f * k, k, br), 120f, 110f, false, cuerpo.topLeft, cuerpo.size, style = Stroke(1.3f * u))
}

/**
 * La leña apilada junto a la estufa, vista de testa: tres y dos y uno, cada tronco con su corteza, la madera clara con sus
 * anillos y las grietas del secado; algunos partidos en cuña.
 */
internal fun DrawScope.lenaColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val r = w * 0.029f
    contactoColonos(Offset(pie.x, pie.y), r * 7.5f, w * 0.018f, a, 0.38f)
    val pila = listOf(0 to -2, 0 to 0, 0 to 2, 1 to -1, 1 to 1, 2 to 0)
    pila.forEachIndexed { i, (fila, col) ->
        val c = Offset(pie.x + col * r * 1.02f + r * 0.1f * (azar(7910, i) - 0.5f), pie.y - r * (1f + fila * 1.7f))
        val rr = r * (0.85f + 0.25f * azar(7911, i))
        val k = g.k(c); val br = g.br(c)
        val cuna = false
        val forma = Path().apply {
            if (cuna) { val a0 = 120f + 60f * azar(7912, i); moveTo(c.x + rr * 0.1f, c.y + rr * 0.35f); arcTo(Rect(c.x - rr, c.y - rr * 0.95f, c.x + rr * 1.1f, c.y + rr * 1.15f), a0, 130f, false); close() }
            else addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr))
        }
        drawPath(corridoColonos(forma, 1f * u, 1.4f * u), a.sombra(0.4f))
        drawPath(forma, a.luz(mezcla(PINO_OSC, HIERRO, 0.3f), k, br))
        val cara = Path().apply {
            if (cuna) { val a0 = 120f + 60f * azar(7912, i); moveTo(c.x + rr * 0.12f, c.y + rr * 0.3f); arcTo(Rect(c.x - rr * 0.8f, c.y - rr * 0.75f, c.x + rr * 0.92f, c.y + rr * 0.97f), a0 + 6f, 118f, false); close() }
            else addOval(Rect(c.x - rr * 0.82f, c.y - rr * 0.82f, c.x + rr * 0.82f, c.y + rr * 0.82f))
        }
        drawPath(cara, a.luz(PINO, k, br))
        clipPath(cara) {
            val anillos = Path()
            for (j in 1..3) { val q = rr * 0.22f * j; anillos.addOval(Rect(c.x - q + rr * 0.1f, c.y - q, c.x + q + rr * 0.1f, c.y + q)) }
            drawPath(anillos, a.luz(PINO_OSC, k, br).copy(alpha = 0.55f), style = Stroke(0.6f * u))
            drawLine(a.sombra(0.5f), Offset(c.x + rr * 0.1f, c.y), Offset(c.x + rr * 0.9f, c.y - rr * 0.3f), 0.8f * u)
            drawRect(a.sombra(0.2f), Offset(c.x, c.y - rr), Size(rr, rr * 2f))
        }
        if (a.oscuro && br > 0.4f && k > 0.1f) drawArc(a.brillo(0.6f * k, k, br), 140f, 90f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.2f * u))
    }
}

/**
 * La cubeta galvanizada con elotes, cortada por el borde de la derecha: la lámina con sus aros y sus flores de zinc, el borde
 * enrollado, el asa de alambre y tres elotes con sus hojas abiertas.
 */
internal fun DrawScope.cubetaColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val rb = w * 0.07f; val rt = w * 0.088f; val alto = w * 0.12f
    val top = pie.y - alto
    val c = Offset(pie.x, pie.y - alto * 0.5f)
    val k = g.k(c); val br = g.br(c)
    contactoColonos(Offset(pie.x - rb * 0.2f, pie.y), rb * 2.6f, w * 0.018f, a, 0.38f)
    // Los elotes, detrás del borde.
    listOf(-0.5f to -14f, 0.05f to 4f, 0.55f to 18f).forEachIndexed { i, (fx, giro) ->
        val base = Offset(pie.x + rt * fx, top + w * 0.01f)
        elote(g, base, w * 0.11f, w * 0.024f, giro, 7920 + i)
    }
    val cuerpo = poligonoColonos(pie.x - rt, top, pie.x + rt, top, pie.x + rb, pie.y, pie.x - rb, pie.y)
    drawPath(cuerpo, a.luz(LATA, k, br))
    clipPath(cuerpo) {
        // Las flores del zinc: manchas claras y oscuras irregulares.
        val flores = Path()
        for (i in 0 until 14) {
            val x = pie.x - rt + 2 * rt * azar(7930, i); val y = top + alto * azar(7931, i)
            val s = w * (0.008f + 0.01f * azar(7932, i))
            flores.addOval(Rect(x - s, y - s * 0.6f, x + s, y + s * 0.7f))
        }
        drawPath(flores, a.luz(mezcla(LATA, Color.White, 0.3f), k, br).copy(alpha = 0.4f))
        drawRect(a.claro(0.25f), Offset(pie.x - rt * 0.7f, top), Size(rt * 0.35f, alto))
        drawRect(a.sombra(0.32f), Offset(pie.x + rt * 0.25f, top), Size(rt, alto))
        listOf(0.3f, 0.75f).forEach { f ->
            val y = top + alto * f
            drawRect(a.luz(LATA_OSC, k, br), Offset(pie.x - rt, y), Size(2 * rt, w * 0.005f))
            drawRect(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(pie.x - rt, y - 0.8f * u), Size(2 * rt, 0.8f * u))
        }
    }
    // El borde enrollado y el asa de alambre caída.
    drawRoundRect(a.luz(mezcla(LATA, Color.White, 0.2f), k, br), Offset(pie.x - rt - w * 0.004f, top - w * 0.006f), Size(2 * rt + w * 0.008f, w * 0.011f), CornerRadius(w * 0.005f))
    drawRect(a.sombra(0.3f), Offset(pie.x - rt, top + w * 0.005f), Size(2 * rt, 1f * u))
    val asa = Path().apply { moveTo(pie.x - rt, top + w * 0.01f); cubicTo(pie.x - rt * 0.8f, top + alto * 0.7f, pie.x + rt * 0.2f, top + alto * 0.75f, pie.x + rt * 0.5f, top + alto * 0.55f) }
    drawPath(corridoColonos(asa, 1f * u, 1.4f * u), a.sombra(0.35f), style = Stroke(1.2f * u))
    drawPath(asa, a.luz(LATA_OSC, k, br), style = Stroke(1.2f * u))
}

/** Un elote con sus hojas abiertas: la mazorca amarilla con sus granos en hileras y las hojas pajizas a los lados. */
internal fun DrawScope.elote(g: MarcoColonos, base: Offset, largo: Float, ancho: Float, giro: Float, semilla: Int) {
    val u = density; val a = g.a
    val k = g.k(base); val br = g.br(base)
    rotate(giro, base) {
        val mazorca = RoundRect(Rect(base.x - ancho / 2, base.y - largo, base.x + ancho / 2, base.y), CornerRadius(ancho / 2))
        val hojas = Path().apply {
            moveTo(base.x - ancho * 0.5f, base.y)
            quadraticTo(base.x - ancho * 1.3f, base.y - largo * 0.4f, base.x - ancho * 0.9f, base.y - largo * 0.75f)
            quadraticTo(base.x - ancho * 0.5f, base.y - largo * 0.4f, base.x, base.y)
            moveTo(base.x + ancho * 0.5f, base.y)
            quadraticTo(base.x + ancho * 1.4f, base.y - largo * 0.35f, base.x + ancho * 1.1f, base.y - largo * 0.7f)
            quadraticTo(base.x + ancho * 0.5f, base.y - largo * 0.35f, base.x, base.y)
        }
        drawPath(Path().apply { addRoundRect(mazorca) }, a.luz(ELOTE, k, br))
        clipPath(Path().apply { addRoundRect(mazorca) }) {
            val granos = Path()
            for (f in 0 until 9) for (col in 0 until 3) {
                val gx = base.x - ancho * 0.36f + ancho * 0.36f * col; val gy = base.y - largo * (0.08f + 0.1f * f)
                granos.addOval(Rect(gx - ancho * 0.13f, gy - largo * 0.04f, gx + ancho * 0.13f, gy + largo * 0.04f))
            }
            drawPath(granos, a.luz(mezcla(ELOTE, Color.White, 0.2f), k, br), style = Stroke(0.5f * u))
            drawRect(a.sombra(0.25f), Offset(base.x + ancho * 0.1f, base.y - largo), Size(ancho, largo))
        }
        drawPath(hojas, a.luz(mezcla(CRUDO, VERDE, 0.25f + 0.2f * azar(semilla, 1)), k, br))
        drawPath(hojas, a.sombra(0.3f), style = Stroke(0.6f * u))
    }
}
