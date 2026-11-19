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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CARMESI
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.MANGANESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.MELADO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ORO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PERGAMINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.VIDRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.borde
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.contacto
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.ovalo
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.perfil
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.poligono
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.redondo
import io.github.ruddy2003.vestigium.ui.ideario.arcoHerradura
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Al-Ándalus: lo que hay sobre la cornisa, colgado del muro, al pie y en los huecos de las repisas
// =====================================================================

/**
 * El candil de piquera de bronce sobre su pie: el platillo, el fuste con dos nudos y el candil de cazoleta lenticular, con la
 * piquera larga (en su punta arde la llama: [llamaCandilAndalus]) y el asa de anilla con su palmeta. [s] es su alto.
 */
internal fun DrawScope.candilAndalus(pie: Offset, s: Float, a: AireAndalus, cerca: Float, caida: Offset) {
    val u = density
    val bronce = a.luz(BRONCE, maxOf(cerca, 0.7f))
    val hondo = a.luz(mezcla(BRONCE, CEDRO_OSC, 0.55f), maxOf(cerca, 0.6f))
    val pieF = perfil(pie.x, pie.y, s, floatArrayOf(0.06f, -0.12f, 0.08f, -0.08f, 0.22f, -0.03f, 0.24f, 0f))
    val fuste = Rect(pie.x - s * 0.028f, pie.y - s * 0.5f, pie.x + s * 0.028f, pie.y - s * 0.1f)
    val platillo = ovalo(Offset(pie.x, pie.y - s * 0.5f), s * 0.2f, s * 0.035f)
    val cuerpo = perfil(pie.x, pie.y - s * 0.52f, s, floatArrayOf(0.04f, -0.13f, 0.14f, -0.1f, 0.2f, -0.06f, 0.16f, -0.02f, 0.08f, 0f))
    val piquera = poligono(pie.x + s * 0.12f, pie.y - s * 0.62f, pie.x + s * 0.43f, pie.y - s * 0.77f, pie.x + s * 0.45f, pie.y - s * 0.73f, pie.x + s * 0.16f, pie.y - s * 0.55f)
    // La sombra en el muro, de todo junto.
    translate(caida.x, caida.y) {
        val sc = a.sombra(if (a.oscuro) 0.35f else 0.2f)
        drawPath(pieF, sc); drawRect(sc, fuste.topLeft, fuste.size); drawPath(cuerpo, sc); drawPath(piquera, sc)
    }
    contacto(Offset(pie.x + 2f * u, pie.y), s * 0.55f, s * 0.06f, a)
    drawPath(pieF, bronce)
    redondo(pieF, a, cerca, 1f, brillo = 0.3f)
    drawRect(bronce, fuste.topLeft, fuste.size)
    drawRect(a.claro(0.4f), fuste.topLeft, Size(fuste.width * 0.3f, fuste.height))
    drawRect(a.sombra(0.3f), Offset(fuste.right - fuste.width * 0.35f, fuste.top), fuste.size.copy(width = fuste.width * 0.35f))
    for (j in 0..1) {
        val c = Offset(pie.x, pie.y - s * (0.2f + 0.16f * j))
        drawOval(bronce, Offset(c.x - s * 0.055f, c.y - s * 0.03f), Size(s * 0.11f, s * 0.06f))
        drawOval(a.claro(0.4f), Offset(c.x - s * 0.045f, c.y - s * 0.026f), Size(s * 0.04f, s * 0.025f))
    }
    drawPath(platillo, bronce)
    drawPath(borde(platillo, 0f, s * 0.012f), a.claro(0.4f))
    // El asa de anilla, a la izquierda, con su palmeta para el pulgar.
    drawArc(bronce, 90f, 180f, false, Offset(pie.x - s * 0.3f, pie.y - s * 0.67f), Size(s * 0.18f, s * 0.13f), style = Stroke(s * 0.035f))
    val palmeta = Path().apply {
        moveTo(pie.x - s * 0.17f, pie.y - s * 0.66f)
        cubicTo(pie.x - s * 0.26f, pie.y - s * 0.72f, pie.x - s * 0.24f, pie.y - s * 0.84f, pie.x - s * 0.2f, pie.y - s * 0.86f)
        cubicTo(pie.x - s * 0.17f, pie.y - s * 0.8f, pie.x - s * 0.14f, pie.y - s * 0.72f, pie.x - s * 0.13f, pie.y - s * 0.66f)
        close()
    }
    drawPath(palmeta, bronce)
    drawPath(piquera, bronce)
    drawLine(a.claro(0.4f), Offset(pie.x + s * 0.14f, pie.y - s * 0.6f), Offset(pie.x + s * 0.43f, pie.y - s * 0.765f), 0.7f * u)
    drawPath(cuerpo, bronce)
    redondo(cuerpo, a, cerca, 1f, brillo = 0.35f)
    // La boca de llenar, con su tapadera, y el grabado de la cazoleta.
    drawOval(hondo, Offset(pie.x - s * 0.05f, pie.y - s * 0.67f), Size(s * 0.1f, s * 0.03f))
    drawArc(hondo, 20f, 140f, false, Offset(pie.x - s * 0.16f, pie.y - s * 0.62f), Size(s * 0.32f, s * 0.08f), style = Stroke(0.6f * u))
    // La mecha en la punta: de día, apagada y tiznada.
    drawCircle(a.luz(TINTA, cerca), s * 0.016f, Offset(pie.x + s * 0.44f, pie.y - s * 0.765f))
    if (a.oscuro) drawPath(borde(cuerpo, -s * 0.02f, s * 0.02f), FILO_MUEBLE.copy(alpha = 0.7f))
}

/** La llama del candil, con su color propio: tres lenguas, de la naranja de fuera a la blanca de dentro. */
internal fun DrawScope.llamaCandilAndalus(p: Offset, s: Float) {
    drawCircle(LLAMA.copy(alpha = 0.16f), s * 1.1f, p - Offset(0f, s * 0.5f))
    drawCircle(LLAMA.copy(alpha = 0.22f), s * 0.66f, p - Offset(0f, s * 0.5f))
    fun lengua(k: Float, ladeo: Float) = Path().apply {
        moveTo(p.x - s * 0.17f * k, p.y)
        cubicTo(p.x - s * 0.3f * k, p.y - s * 0.4f * k, p.x + ladeo * s - s * 0.05f * k, p.y - s * 0.7f * k, p.x + ladeo * s, p.y - s * 1.15f * k)
        cubicTo(p.x + ladeo * s + s * 0.12f * k, p.y - s * 0.6f * k, p.x + s * 0.3f * k, p.y - s * 0.35f * k, p.x + s * 0.17f * k, p.y)
        close()
    }
    drawPath(lengua(1f, 0.08f), Color(0xFFFF9A2E))
    drawPath(lengua(0.72f, 0.05f), LLAMA)
    drawPath(lengua(0.4f, 0.02f), Color(0xFFFFF6DC))
}

/**
 * El laúd, apoyado de pie en el muro sobre la cornisa e inclinado [giro] grados: la caja de duelas, la tapa de pino con su
 * gran rosa calada y dos chicas, el puente, las cuerdas dobles, el mástil corto y el clavijero echado hacia atrás.
 */
internal fun DrawScope.laudAndalus(pie: Offset, largo: Float, giro: Float, a: AireAndalus, cerca: Float, haciaX: Float, caida: Offset) {
    val u = density
    val L = largo
    val cx = pie.x
    val caja = perfil(cx, pie.y, L, floatArrayOf(0.045f, -0.52f, 0.1f, -0.45f, 0.155f, -0.33f, 0.185f, -0.2f, 0.17f, -0.08f, 0.11f, -0.015f, 0.05f, 0f))
    val mastil = poligono(cx - L * 0.042f, pie.y - L * 0.5f, cx + L * 0.042f, pie.y - L * 0.5f, cx + L * 0.034f, pie.y - L * 0.79f, cx - L * 0.034f, pie.y - L * 0.79f)
    val clavijero = poligono(cx - L * 0.036f, pie.y - L * 0.785f, cx + L * 0.036f, pie.y - L * 0.785f, cx + L * 0.05f, pie.y - L * 0.9f, cx + L * 0.005f, pie.y - L * 0.93f)
    rotate(giro, pie) {
        translate(caida.x, caida.y) {
            val sc = a.sombra(if (a.oscuro) 0.38f else 0.22f)
            drawPath(caja, sc); drawPath(mastil, sc); drawPath(clavijero, sc)
        }
    }
    contacto(Offset(pie.x + L * 0.02f, pie.y), L * 0.36f, L * 0.03f, a)
    rotate(giro, pie) {
        // La caja de duelas, que asoma por el lado de la sombra.
        drawPath(corrido(caja, L * 0.022f, -L * 0.004f), a.luz(mezcla(CEDRO, CEDRO_OSC, 0.3f), cerca))
        clipPath(corrido(caja, L * 0.022f, -L * 0.004f)) {
            for (j in 0 until 7) {
                val x = cx + L * (0.1f + 0.016f * j)
                drawLine(if (j % 2 == 0) a.luz(CEDRO_LUZ, cerca) else a.luz(CEDRO_OSC, cerca), Offset(x, pie.y - L * 0.5f), Offset(x + L * 0.05f, pie.y), 1f * u)
            }
        }
        // La tapa de pino, con su veta fina.
        val tapa = a.luz(PINO, cerca)
        drawPath(caja, tapa)
        clipPath(caja) {
            vetas(Rect(cx - L * 0.2f, pie.y - L * 0.53f, cx + L * 0.2f, pie.y), mezcla(PINO, CEDRO, 0.6f).copy(alpha = 0.3f), 1281, densidad = 0.7f, vertical = true, grosor = 0.5f, ondulacion = 0.3f)
            // El golpeador de piel oscura bajo las cuerdas y el filete de la orilla.
            drawOval(a.luz(mezcla(CUERO_OSC, CEDRO, 0.3f), cerca), Offset(cx - L * 0.085f, pie.y - L * 0.2f), Size(L * 0.17f, L * 0.11f))
        }
        drawPath(caja, a.luz(CEDRO_OSC, cerca), style = Stroke(1.1f * u))
        redondo(caja, a, cerca, haciaX, fuerza = 0.8f)
        // Las rosas caladas: una grande y dos chicas, de lazo sencillo.
        listOf(Triple(0f, -0.36f, 0.055f), Triple(-0.09f, -0.27f, 0.026f), Triple(0.09f, -0.27f, 0.026f)).forEach { (dx, dy, rr) ->
            val c = Offset(cx + L * dx, pie.y + L * dy)
            drawCircle(a.luz(mezcla(CEDRO_OSC, TINTA, 0.5f), cerca), L * rr, c)
            for (q in 0 until 6) {
                val ang = 6.283f * q / 6
                drawCircle(tapa, L * rr * 0.45f, Offset(c.x + L * rr * 0.5f * cos(ang), c.y + L * rr * 0.5f * sin(ang)), style = Stroke(0.7f * u))
            }
            drawCircle(tapa, L * rr, c, style = Stroke(0.8f * u))
        }
        // El mástil y el clavijero, de madera oscura, con las clavijas.
        drawPath(mastil, a.luz(CEDRO_OSC, cerca))
        drawLine(a.claro(0.3f), Offset(cx - L * 0.03f, pie.y - L * 0.5f), Offset(cx - L * 0.025f, pie.y - L * 0.78f), 0.7f * u)
        drawPath(clavijero, a.luz(mezcla(CEDRO_OSC, CEDRO, 0.3f), cerca))
        for (j in 0 until 5) {
            val y = pie.y - L * (0.8f + 0.024f * j)
            val xi = cx - L * 0.034f + L * 0.008f * j
            drawLine(a.luz(CEDRO, cerca), Offset(xi, y), Offset(xi - L * 0.03f, y - L * 0.004f), 1.6f * u, StrokeCap.Round)
            drawLine(a.luz(CEDRO, cerca), Offset(xi + L * 0.07f, y + L * 0.002f), Offset(xi + L * 0.095f, y - L * 0.002f), 1.6f * u, StrokeCap.Round)
        }
        // El puente y las cuerdas dobles.
        val puente = Rect(cx - L * 0.07f, pie.y - L * 0.095f, cx + L * 0.07f, pie.y - L * 0.08f)
        drawRect(a.luz(CEDRO_OSC, cerca), puente.topLeft, puente.size)
        val cuerda = a.luz(mezcla(PERGAMINO, CEDRO_LUZ, 0.3f), cerca + 0.1f)
        for (j in 0 until 5) {
            val xb = puente.left + puente.width * (0.1f + 0.2f * j)
            val xc = cx - L * 0.026f + L * 0.013f * j
            drawLine(cuerda, Offset(xb, puente.top), Offset(xc, pie.y - L * 0.785f), 0.45f * u)
            drawLine(cuerda, Offset(xb + 1.2f * u, puente.top), Offset(xc + 0.6f * u, pie.y - L * 0.785f), 0.45f * u)
        }
        drawRect(a.luz(mezcla(PERGAMINO, CEDRO, 0.2f), cerca), Offset(cx - L * 0.036f, pie.y - L * 0.79f), Size(L * 0.072f, 1.6f * u))
        if (a.oscuro) drawPath(borde(caja, -haciaX * L * 0.012f, L * 0.01f), FILO_MUEBLE.copy(alpha = (0.1f + 0.6f * cerca).coerceAtMost(0.7f)))
    }
}

/**
 * El astrolabio de bronce colgado de un clavo por su anilla: el trono calado, la madre con su limbo graduado, la lámina y, encima,
 * la araña calada (el anillo de la eclíptica y los punteros de las estrellas), con su eje y su cuña.
 */
internal fun DrawScope.astrolabioAndalus(clavo: Offset, r: Float, a: AireAndalus, cerca: Float, haciaX: Float, caida: Offset) {
    val u = density
    val c = clavo + Offset(0f, r * 1.97f)
    val disco = ovalo(c, r, r)
    val trono = Path().apply {
        moveTo(c.x - r * 0.32f, c.y - r * 0.92f)
        cubicTo(c.x - r * 0.34f, c.y - r * 1.12f, c.x - r * 0.18f, c.y - r * 1.22f, c.x, c.y - r * 1.3f)
        cubicTo(c.x + r * 0.18f, c.y - r * 1.22f, c.x + r * 0.34f, c.y - r * 1.12f, c.x + r * 0.32f, c.y - r * 0.92f)
        close()
    }
    val bronce = a.luz(BRONCE, cerca)
    val hondo = a.luz(mezcla(BRONCE, CEDRO_OSC, 0.6f), cerca)
    translate(caida.x, caida.y) { drawPath(disco, a.sombra(if (a.oscuro) 0.3f else 0.24f)); drawPath(trono, a.sombra(if (a.oscuro) 0.3f else 0.24f)) }
    // El cordón y la anilla.
    val anilla = Offset(c.x, c.y - r * 1.38f)
    drawLine(a.luz(mezcla(CUERO, CARMESI, 0.4f), cerca), clavo, Offset(anilla.x - r * 0.06f, anilla.y), 1f * u)
    drawLine(a.luz(mezcla(CUERO, CARMESI, 0.4f), cerca), clavo, Offset(anilla.x + r * 0.06f, anilla.y), 1f * u)
    drawCircle(bronce, r * 0.1f, anilla, style = Stroke(r * 0.04f))
    drawCircle(a.sombra(0.4f), 1.7f * u, clavo + Offset(0.7f * u, 0.8f * u))
    drawCircle(a.luz(mezcla(BRONCE, CEDRO_OSC, 0.4f), cerca), 1.5f * u, clavo)
    drawPath(trono, bronce)
    drawCircle(hondo, r * 0.08f, Offset(c.x, c.y - r * 1.08f))
    drawPath(borde(trono, r * 0.04f, r * 0.04f), a.claro(0.4f))
    // La madre y el limbo.
    drawPath(disco, bronce)
    val limbo = Path()
    for (k in 0 until 48) {
        val ang = 6.283f * k / 48
        val r0 = if (k % 4 == 0) r * 0.8f else r * 0.86f
        limbo.moveTo(c.x + r0 * cos(ang), c.y + r0 * sin(ang)); limbo.lineTo(c.x + r * 0.93f * cos(ang), c.y + r * 0.93f * sin(ang))
    }
    drawPath(limbo, hondo, style = Stroke(0.5f * u))
    drawCircle(hondo, r * 0.79f, c, style = Stroke(0.7f * u))
    // La lámina, más clara, con sus almucantarades.
    drawCircle(a.luz(mezcla(BRONCE, PERGAMINO, 0.3f), cerca), r * 0.78f, c)
    for (j in 1..4) drawCircle(hondo.copy(alpha = 0.5f), r * (0.16f * j), Offset(c.x, c.y - r * 0.18f + r * 0.04f * j), style = Stroke(0.45f * u))
    drawLine(hondo.copy(alpha = 0.6f), Offset(c.x - r * 0.78f, c.y), Offset(c.x + r * 0.78f, c.y), 0.5f * u)
    // La araña: el anillo de la eclíptica, descentrado, la barra y los punteros.
    val arana = bronce
    drawCircle(arana, r * 0.5f, Offset(c.x, c.y - r * 0.24f), style = Stroke(r * 0.06f))
    drawLine(arana, Offset(c.x - r * 0.76f, c.y + r * 0.08f), Offset(c.x + r * 0.76f, c.y - r * 0.08f), r * 0.04f)
    for (k in 0 until 6) {
        val ang = 0.5f + 1.05f * k
        val o = Offset(c.x + r * 0.62f * cos(ang), c.y + r * 0.62f * sin(ang))
        val p = Path().apply {
            moveTo(c.x + r * 0.4f * cos(ang + 0.25f), c.y + r * 0.4f * sin(ang + 0.25f))
            cubicTo(o.x, o.y, o.x + r * 0.05f, o.y - r * 0.08f, o.x + r * 0.12f * cos(ang - 0.6f), o.y + r * 0.12f * sin(ang - 0.6f))
        }
        drawPath(p, arana, style = Stroke(r * 0.035f, cap = StrokeCap.Round))
    }
    drawCircle(hondo, r * 0.07f, c)
    drawCircle(a.claro(0.5f), r * 0.03f, c - Offset(r * 0.02f, r * 0.02f))
    redondo(disco, a, cerca, haciaX, brillo = 0.25f, fuerza = 0.8f)
}

/**
 * Códices tumbados, como se guardaban, en pila: unos enseñan la solapa de cuero que les tapa el canto y otros el corte de las
 * hojas entre sus dos tapas, con el título marcado con unos trazos. [izq] es la esquina de abajo a la izquierda.
 */
internal fun DrawScope.librosTumbadosAndalus(izq: Offset, an: Float, cuantos: Int, a: AireAndalus, cerca: Float, semilla: Int, caida: Offset) {
    val u = density
    val g = an * 0.1f
    val pieles = listOf(CUERO, mezcla(CARMESI, CUERO_OSC, 0.45f), mezcla(ANIL, CUERO_OSC, 0.5f), mezcla(MELADO, CUERO, 0.5f))
    translate(caida.x, caida.y) { drawRect(a.sombra(if (a.oscuro) 0.36f else 0.2f), Offset(izq.x + an * 0.05f, izq.y - cuantos * g), Size(an * 0.95f, cuantos * g)) }
    contacto(Offset(izq.x + an * 0.52f, izq.y), an * 1.12f, an * 0.05f, a, 0.36f)
    for (j in 0 until cuantos) {
        val dx = (azar(semilla, j) - 0.5f) * an * 0.08f
        val largo = an * (0.86f + 0.14f * azar(semilla, j + 10))
        val r = Rect(izq.x + dx + (an - largo) * 0.5f, izq.y - (j + 1) * g, izq.x + dx + (an + largo) * 0.5f, izq.y - j * g)
        val piel = a.luz(pieles[Math.floorMod(semilla + j * 3, pieles.size)], cerca)
        if ((semilla + j) % 2 == 0) {
            // La solapa que tapa el canto, con su filete gofrado.
            drawRoundRect(piel, r.topLeft, r.size, CornerRadius(1.2f * u))
            drawRect(mezcla(piel, a.hondo, 0.3f), Offset(r.left + 2f * u, r.top + r.height * 0.3f), Size(r.width - 4f * u, 0.6f * u))
            drawRect(mezcla(piel, a.hondo, 0.3f), Offset(r.left + 2f * u, r.bottom - r.height * 0.3f), Size(r.width - 4f * u, 0.6f * u))
        } else {
            // El corte de las hojas entre las dos tapas, con su marca.
            drawRect(piel, r.topLeft, r.size)
            val hojas = Rect(r.left + 1f * u, r.top + r.height * 0.22f, r.right - 1.5f * u, r.bottom - r.height * 0.22f)
            drawRect(a.luz(PERGAMINO, cerca), hojas.topLeft, hojas.size)
            for (q in 1..2) drawLine(a.sombra(0.18f), Offset(hojas.left, hojas.top + hojas.height * q / 3f), Offset(hojas.right, hojas.top + hojas.height * q / 3f), 0.4f * u)
            for (q in 0 until 3) drawLine(a.luz(TINTA, cerca).copy(alpha = 0.6f), Offset(r.center.x - an * 0.1f + q * an * 0.07f, hojas.center.y), Offset(r.center.x - an * 0.07f + q * an * 0.07f, hojas.center.y), 0.9f * u)
        }
        drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 0.8f * u))
        drawRect(a.sombra(0.26f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        drawRect(a.sombra(0.35f), Offset(r.left, r.bottom - 0.9f * u), Size(r.width, 0.9f * u))
        if (a.oscuro) drawRect(FILO_MUEBLE.copy(alpha = 0.45f * cerca), r.topLeft, Size(r.width, 0.8f * u))
    }
    // La tira de papel que hace de registro, colgando del de en medio.
    val y = izq.y - g * 1.5f
    drawPath(poligono(izq.x + an * 0.82f, y - g * 0.2f, izq.x + an * 0.86f, y - g * 0.2f, izq.x + an * 0.87f, y + g * 1.6f, izq.x + an * 0.83f, y + g * 1.5f), a.luz(PERGAMINO, cerca))
}

/** Una redoma de vidrio verdoso: la panza redonda, el cuello largo y el labio vuelto; se transparenta lo de detrás. */
internal fun DrawScope.redomaAndalus(pie: Offset, alto: Float, a: AireAndalus, cerca: Float, haciaX: Float, caida: Offset) {
    val u = density
    val forma = perfil(pie.x, pie.y, alto, floatArrayOf(0.09f, -1f, 0.06f, -0.96f, 0.045f, -0.88f, 0.045f, -0.62f, 0.12f, -0.5f, 0.24f, -0.34f, 0.25f, -0.18f, 0.18f, -0.04f, 0.1f, 0f))
    translate(caida.x, caida.y) { drawPath(forma, a.sombra(if (a.oscuro) 0.3f else 0.14f)) }
    contacto(Offset(pie.x + 1.5f * u, pie.y), alto * 0.5f, alto * 0.06f, a, 0.3f)
    drawPath(forma, a.luz(VIDRIO, cerca).copy(alpha = 0.58f))
    drawPath(forma, a.luz(mezcla(VIDRIO, CEDRO_OSC, 0.5f), cerca), style = Stroke(0.9f * u))
    clipPath(forma) {
        drawOval(a.luz(mezcla(VIDRIO, CEDRO_OSC, 0.4f), cerca).copy(alpha = 0.5f), Offset(pie.x - alto * 0.24f, pie.y - alto * 0.12f), Size(alto * 0.48f, alto * 0.16f))
    }
    drawRoundRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.6f * cerca) else a.claro(0.7f), Offset(pie.x - alto * 0.15f, pie.y - alto * 0.38f), Size(alto * 0.04f, alto * 0.18f), CornerRadius(alto * 0.02f))
    drawLine(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.5f * cerca) else a.claro(0.6f), Offset(pie.x - alto * 0.025f, pie.y - alto * 0.86f), Offset(pie.x - alto * 0.025f, pie.y - alto * 0.66f), 0.8f * u)
}

/**
 * El atril plegable de madera (dos tablas que se cruzan en tijera), visto de lado, con un códice abierto en la horquilla; cada
 * pata lleva calado un arquillo de herradura. [pie] es el centro entre las dos patas, en el suelo.
 */
internal fun DrawScope.atrilAndalus(pie: Offset, an: Float, alto: Float, a: AireAndalus, cerca: Float, haciaX: Float) {
    val u = density
    val g = an * 0.05f
    val cruce = Offset(pie.x, pie.y - alto * 0.42f)
    fun tabla(l: Float): Path {
        val p0 = Offset(pie.x - l * an * 0.3f, pie.y); val p1 = Offset(pie.x + l * an * 0.36f, pie.y - alto)
        return poligono(p0.x - g, p0.y, p0.x + g, p0.y, p1.x + g * 0.6f, p1.y, p1.x - g * 0.6f, p1.y)
    }
    // La sombra tendida en el suelo, hacia la derecha.
    drawPath(poligono(pie.x - an * 0.36f, pie.y, pie.x + an * 0.4f, pie.y, pie.x + an * 0.9f, pie.y + an * 0.05f, pie.x - an * 0.1f, pie.y + an * 0.05f), a.sombra(if (a.oscuro) 0.4f else 0.26f))
    val madera = a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.25f), cerca)
    for (l in listOf(1f, -1f)) {
        val t = tabla(l)
        drawPath(t, if (l > 0) mezcla(madera, a.hondo, 0.25f) else madera)
        drawPath(borde(t, 1f * u, 0f), a.claro(0.3f))
        // El arquillo calado de la pata.
        val pa = Offset(pie.x - l * an * 0.23f, pie.y - alto * 0.1f)
        drawPath(arcoHerradura(Rect(pa.x - g * 0.55f, pa.y - alto * 0.14f, pa.x + g * 0.55f, pa.y)), a.sombra(0.6f))
    }
    drawCircle(a.luz(mezcla(BRONCE, CEDRO_OSC, 0.3f), cerca), g * 0.5f, cruce)
    // El códice abierto en la horquilla: las dos mitades del bloque de hojas, abiertas en V, y la tapa debajo.
    val izq = Offset(pie.x - an * 0.44f, pie.y - alto * 0.98f); val der = Offset(pie.x + an * 0.42f, pie.y - alto * 1.02f)
    val fondo = Offset(pie.x - an * 0.02f, pie.y - alto * 0.72f)
    drawPath(poligono(izq.x - 2f * u, izq.y + 3f * u, fondo.x, fondo.y + 3f * u, der.x + 2f * u, der.y + 3f * u, der.x + 2f * u, der.y + 6f * u, fondo.x, fondo.y + 6f * u, izq.x - 2f * u, izq.y + 6f * u), a.luz(mezcla(CARMESI, CUERO_OSC, 0.4f), cerca))
    val hoja = a.luz(PERGAMINO, cerca + 0.08f)
    drawPath(poligono(izq.x, izq.y, fondo.x, fondo.y - alto * 0.04f, fondo.x, fondo.y + 2.5f * u, izq.x, izq.y + 3f * u), hoja)
    drawPath(poligono(der.x, der.y, fondo.x, fondo.y - alto * 0.04f, fondo.x, fondo.y + 2.5f * u, der.x, der.y + 3f * u), mezcla(hoja, a.hondo, 0.12f))
    drawLine(a.sombra(0.3f), Offset(fondo.x, fondo.y - alto * 0.04f), Offset(fondo.x, fondo.y + 2.5f * u), 0.8f * u)
    // Una hoja levantada, a medio pasar, y el registro de seda.
    drawPath(Path().apply { moveTo(fondo.x, fondo.y - alto * 0.04f); cubicTo(fondo.x + an * 0.05f, fondo.y - alto * 0.3f, der.x - an * 0.1f, der.y - alto * 0.32f, der.x - an * 0.04f, der.y - alto * 0.2f) }, hoja, style = Stroke(1.2f * u))
    drawLine(a.luz(CARMESI, cerca), Offset(fondo.x, fondo.y), Offset(fondo.x + an * 0.04f, fondo.y + alto * 0.28f), 1.2f * u)
    if (a.oscuro) drawLine(FILO_MUEBLE.copy(alpha = 0.5f * cerca), izq, Offset(fondo.x, fondo.y - alto * 0.04f), 0.8f * u)
}

/** La alcarraza de barro claro sobre su posajarra: la panza que suda (las manchas húmedas), el cuello, las dos asas y unos trazos de manganeso. */
internal fun DrawScope.alcarrazaAndalus(pie: Offset, alto: Float, a: AireAndalus, cerca: Float, haciaX: Float) {
    val u = density
    val barro = a.luz(BARRO, cerca)
    // La posajarra: un aro bajo de barro rojizo.
    val pos = Rect(pie.x - alto * 0.2f, pie.y - alto * 0.12f, pie.x + alto * 0.2f, pie.y)
    drawPath(poligono(pos.left, pos.bottom, pos.right, pos.bottom, pos.right + alto * 0.4f, pos.bottom + alto * 0.07f, pos.left + alto * 0.2f, pos.bottom + alto * 0.07f), a.sombra(if (a.oscuro) 0.4f else 0.26f))
    val jarra = perfil(pie.x, pie.y - alto * 0.06f, alto, floatArrayOf(0.1f, -0.94f, 0.085f, -0.9f, 0.07f, -0.74f, 0.13f, -0.64f, 0.27f, -0.48f, 0.3f, -0.32f, 0.24f, -0.14f, 0.12f, -0.02f, 0.06f, 0f))
    // Las asas, que salen del cuello y bajan al hombro.
    for (l in listOf(-1f, 1f)) {
        drawPath(Path().apply {
            moveTo(pie.x + l * alto * 0.075f, pie.y - alto * 0.86f)
            cubicTo(pie.x + l * alto * 0.24f, pie.y - alto * 0.88f, pie.x + l * alto * 0.26f, pie.y - alto * 0.72f, pie.x + l * alto * 0.2f, pie.y - alto * 0.63f)
        }, mezcla(barro, a.hondo, 0.15f), style = Stroke(alto * 0.03f, cap = StrokeCap.Round))
    }
    drawPath(jarra, barro)
    clipPath(jarra) {
        // Lo húmedo: el barro que suda se oscurece abajo y en manchas.
        drawOval(a.luz(mezcla(BARRO, CEDRO, 0.35f), cerca).copy(alpha = 0.6f), Offset(pie.x - alto * 0.3f, pie.y - alto * 0.3f), Size(alto * 0.6f, alto * 0.4f))
        for (k in 0 until 5) {
            val o = Offset(pie.x + alto * (azar(1301, k) - 0.5f) * 0.4f, pie.y - alto * (0.2f + 0.3f * azar(1302, k)))
            drawOval(a.luz(mezcla(BARRO, CEDRO, 0.4f), cerca).copy(alpha = 0.4f), Offset(o.x - alto * 0.03f, o.y - alto * 0.05f), Size(alto * 0.06f, alto * 0.1f))
        }
        // Los trazos de manganeso del hombro: tres pinceladas y una banda.
        val trazo = a.luz(MANGANESO, cerca)
        drawLine(trazo, Offset(pie.x - alto * 0.3f, pie.y - alto * 0.6f), Offset(pie.x + alto * 0.3f, pie.y - alto * 0.6f), 1.2f * u)
        for (k in -2..2) drawLine(trazo, Offset(pie.x + k * alto * 0.08f, pie.y - alto * 0.58f), Offset(pie.x + k * alto * 0.1f, pie.y - alto * 0.47f), 1.1f * u, StrokeCap.Round)
    }
    redondo(jarra, a, cerca, haciaX, fuerza = 1f)
    drawOval(a.luz(mezcla(BARRO, CEDRO_OSC, 0.5f), cerca), Offset(pie.x - alto * 0.085f, pie.y - alto * 1.015f), Size(alto * 0.17f, alto * 0.04f))
    // La posajarra, delante de la panza.
    drawRoundRect(a.luz(mezcla(BARRO, CEDRO, 0.45f), cerca), pos.topLeft, pos.size, CornerRadius(alto * 0.03f))
    drawRect(a.claro(0.3f), pos.topLeft, Size(pos.width, 1f * u))
    drawRect(a.sombra(0.3f), Offset(pos.right - pos.width * 0.3f, pos.top), Size(pos.width * 0.3f, pos.height))
    // Una gota que corre por la panza.
    drawLine(a.luz(mezcla(BARRO, CEDRO, 0.5f), cerca), Offset(pie.x + alto * 0.12f, pie.y - alto * 0.4f), Offset(pie.x + alto * 0.13f, pie.y - alto * 0.24f), 0.9f * u, StrokeCap.Round)
}

/** El arcón de cedro, cortado por el borde derecho: la tapa que vuela, los herrajes de bronce, la cerradura y sus patas. */
internal fun DrawScope.arconAndalus(pie: Offset, an: Float, alto: Float, a: AireAndalus, cerca: Float) {
    val u = density
    val cuerpo = Rect(pie.x - an * 0.3f, pie.y - alto, pie.x + an * 0.9f, pie.y - alto * 0.08f)
    drawPath(poligono(cuerpo.left, pie.y, cuerpo.right, pie.y, cuerpo.right + an * 0.3f, pie.y + alto * 0.12f, cuerpo.left + an * 0.18f, pie.y + alto * 0.12f), a.sombra(if (a.oscuro) 0.42f else 0.26f))
    val madera = a.luz(CEDRO, cerca)
    drawRect(a.luz(CEDRO_OSC, cerca), Offset(cuerpo.left + an * 0.04f, cuerpo.bottom), Size(an * 0.06f, pie.y - cuerpo.bottom))
    drawRect(madera, cuerpo.topLeft, cuerpo.size)
    vetas(cuerpo, CEDRO_OSC.copy(alpha = 0.4f), 1331, densidad = 0.6f, grosor = 0.8f)
    // La tapa.
    val tapa = Rect(cuerpo.left - an * 0.02f, cuerpo.top - alto * 0.06f, cuerpo.right, cuerpo.top + alto * 0.12f)
    drawRect(a.luz(mezcla(CEDRO, CEDRO_LUZ, 0.3f), cerca), tapa.topLeft, tapa.size)
    drawRect(a.claro(0.4f), tapa.topLeft, Size(tapa.width, 1.1f * u))
    drawRect(a.sombra(0.45f), Offset(cuerpo.left, tapa.bottom), Size(cuerpo.width, 2f * u))
    // Los herrajes: dos flejes con sus clavos y la cerradura.
    val bronce = a.luz(BRONCE, cerca)
    for (x in listOf(cuerpo.left + an * 0.08f, cuerpo.left + an * 0.62f)) {
        drawRect(bronce, Offset(x, tapa.top), Size(an * 0.04f, cuerpo.bottom - tapa.top))
        drawRect(a.sombra(0.3f), Offset(x + an * 0.03f, tapa.top), Size(an * 0.01f, cuerpo.bottom - tapa.top))
        for (j in 0 until 4) drawCircle(a.luz(mezcla(BRONCE, CEDRO_OSC, 0.3f), cerca), 1f * u, Offset(x + an * 0.02f, tapa.top + (cuerpo.bottom - tapa.top) * (0.15f + 0.24f * j)))
    }
    val cerr = Rect(cuerpo.left + an * 0.3f, tapa.bottom + alto * 0.04f, cuerpo.left + an * 0.42f, tapa.bottom + alto * 0.36f)
    drawRoundRect(bronce, cerr.topLeft, cerr.size, CornerRadius(an * 0.02f))
    drawPath(arcoHerradura(Rect(cerr.center.x - an * 0.012f, cerr.top + cerr.height * 0.3f, cerr.center.x + an * 0.012f, cerr.top + cerr.height * 0.7f)), a.luz(TINTA, cerca))
    drawRect(a.claro(0.3f), cuerpo.topLeft, Size(an * 0.03f, cuerpo.height))
    // Un cojín de cuero con su borla, encima.
    val coj = Rect(cuerpo.left + an * 0.14f, tapa.top - alto * 0.22f, cuerpo.left + an * 0.7f, tapa.top + 1f * u)
    drawRoundRect(a.luz(mezcla(CUERO, CARMESI, 0.3f), cerca), coj.topLeft, coj.size, CornerRadius(coj.height * 0.5f))
    drawRoundRect(a.claro(0.25f), coj.topLeft + Offset(coj.width * 0.1f, 1f * u), Size(coj.width * 0.5f, coj.height * 0.3f), CornerRadius(coj.height * 0.2f))
    drawRect(a.sombra(0.25f), Offset(coj.left + coj.width * 0.08f, coj.bottom - coj.height * 0.3f), Size(coj.width * 0.84f, coj.height * 0.3f))
    for (k in 0 until 6) drawLine(a.luz(ORO, cerca).copy(alpha = 0.7f), Offset(coj.left + coj.width * (0.2f + 0.12f * k), coj.top + coj.height * 0.35f), Offset(coj.left + coj.width * (0.25f + 0.12f * k), coj.top + coj.height * 0.6f), 0.7f * u)
    drawLine(a.luz(CARMESI, cerca), Offset(coj.left + coj.width * 0.06f, coj.center.y), Offset(coj.left - an * 0.01f, coj.center.y + alto * 0.18f), 1.4f * u, StrokeCap.Round)
}

// ---------------------------------------------------------------- lo que se deja en los huecos libres de las repisas

/**
 * Lo del tema en lo que queda libre de la repisa [k], a la derecha de [oc] (hasta donde llegan los volúmenes): según cuánto
 * hueco haya, una pila de códices tumbados, un ataifor de loza melada de canto contra el muro, rollos sueltos atados, una
 * redoma. Cada repisa escoge lo suyo, para que no se repita.
 */
internal fun DrawScope.huecoLibreAndalus(k: Int, r: Rect, oc: Float, a: AireAndalus, cerca: Float, haciaX: Float) {
    val u = density
    val libre = (r.right - oc) / u
    val y = r.bottom - 1.6f * u
    val sc = Offset(if (a.oscuro) 8f * u else 5f * u, 0f)
    when {
        libre >= 150f -> if (k % 2 == 0) {
            librosTumbadosAndalus(Offset(oc + 8f * u, y), 58f * u, 4, a, cerca, 40 + k, sc)
            ataiforAndalus(Offset(r.right - 44f * u, y), 35f * u, a, cerca, k)
            redomaAndalus(Offset(oc + 36f * u, y - 4 * 5.8f * u), 30f * u, a, cerca, haciaX, sc)
        } else {
            rollosAndalus(Offset(oc + 6f * u, y), 74f * u, 3, a, cerca, k)
            ataiforAndalus(Offset(r.right - 42f * u, y), 33f * u, a, cerca, k + 1)
        }
        libre >= 74f -> when (k % 3) {
            0 -> { librosTumbadosAndalus(Offset(oc + 7f * u, y), 56f * u, 3, a, cerca, 50 + k, sc); redomaAndalus(Offset(oc + 40f * u, y - 3 * 5.6f * u), 28f * u, a, cerca, haciaX, sc) }
            1 -> ataiforAndalus(Offset((oc + r.right) / 2 + 2f * u, y), 33f * u, a, cerca, k)
            else -> rollosAndalus(Offset(oc + 6f * u, y), minOf(64f * u, r.right - oc - 10f * u), 2, a, cerca, k)
        }
        libre >= 44f -> when (k % 3) {
            0 -> ataiforAndalus(Offset((oc + r.right) / 2, y), minOf(22f * u, (r.right - oc) * 0.4f), a, cerca, k)
            1 -> redomaAndalus(Offset((oc + r.right) / 2, y), 34f * u, a, cerca, haciaX, sc)
            else -> librosTumbadosAndalus(Offset(oc + 4f * u, y), r.right - oc - 10f * u, 2, a, cerca, 60 + k, sc)
        }
        libre >= 24f -> redomaAndalus(Offset((oc + r.right) / 2, y), 30f * u, a, cerca, haciaX, sc)
    }
}

/**
 * Un ataifor de loza melada puesto de canto contra el muro: el ala con su filete de manganeso, el pocillo hondo (la sombra del
 * borde cae dentro, arriba a la izquierda) con la estrella de ocho o las hojas en aspa, el brillo del vidriado y el canto del
 * plato asomando abajo.
 */
internal fun DrawScope.ataiforAndalus(pie: Offset, r: Float, a: AireAndalus, cerca: Float, semilla: Int) {
    val u = density
    val c = Offset(pie.x, pie.y - r * 0.94f)
    val plato = ovalo(c, r, r * 0.94f)
    drawPath(corrido(plato, if (a.oscuro) 7f * u else 4.5f * u, -2.5f * u), a.sombra(if (a.oscuro) 0.36f else 0.22f))
    contacto(Offset(pie.x + 2f * u, pie.y), r * 1.1f, 3f * u, a)
    val melado = a.luz(MELADO, cerca)
    val trazo = a.luz(MANGANESO, cerca)
    drawPath(corrido(plato, r * 0.035f, r * 0.05f), a.luz(mezcla(MELADO, MANGANESO, 0.55f), cerca))
    drawPath(plato, melado)
    // El ala: un filete y una hilera de trazos cortos.
    drawPath(ovalo(c, r * 0.9f, r * 0.85f), trazo.copy(alpha = 0.85f), style = Stroke(0.8f * u))
    for (q in 0 until 20) {
        val ang = 6.283f * q / 20
        val o = Offset(c.x + r * 0.78f * cos(ang), c.y + r * 0.73f * sin(ang))
        drawLine(trazo.copy(alpha = 0.8f), o, Offset(c.x + r * 0.7f * cos(ang + 0.12f), c.y + r * 0.66f * sin(ang + 0.12f)), 0.8f * u, StrokeCap.Round)
    }
    // El pocillo, hondo: la sombra del borde arriba a la izquierda y la luz abajo a la derecha.
    val pozo = ovalo(c, r * 0.6f, r * 0.56f)
    drawPath(pozo, a.luz(mezcla(MELADO, BARRO, 0.22f), cerca))
    if (semilla % 2 == 0) {
        for (q in 0..1) rotate(45f * q, c) { drawRect(trazo, Offset(c.x - r * 0.3f, c.y - r * 0.3f), Size(r * 0.6f, r * 0.6f), style = Stroke(1f * u)) }
        drawCircle(trazo, r * 0.09f, c)
    } else {
        for (q in 0 until 4) rotate(45f + 90f * q, c) {
            drawPath(Path().apply { moveTo(c.x, c.y); cubicTo(c.x + r * 0.18f, c.y - r * 0.12f, c.x + r * 0.17f, c.y - r * 0.38f, c.x, c.y - r * 0.48f); cubicTo(c.x - r * 0.17f, c.y - r * 0.38f, c.x - r * 0.18f, c.y - r * 0.12f, c.x, c.y); close() }, trazo)
        }
    }
    drawPath(borde(pozo, r * 0.12f, r * 0.11f), a.sombra(0.26f))
    drawPath(borde(pozo, -r * 0.07f, -r * 0.07f), a.claro(0.3f))
    // El brillo del vidriado.
    val brillo = if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.7f * cerca) else a.claro(0.75f)
    drawArc(brillo, 200f, 55f, false, Offset(c.x - r * 0.84f, c.y - r * 0.8f), Size(r * 1.68f, r * 1.6f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
    drawCircle(brillo, 1.1f * u, Offset(c.x + r * 0.2f, c.y + r * 0.25f))
}

/**
 * La regla de cuerdas del copista (la mistara), colgada de un clavo: un cartón con hilos tendidos en hileras, que se aprieta
 * bajo la hoja para marcar los renglones. [clavo] es de donde cuelga; [an] su ancho.
 */
internal fun DrawScope.mistaraAndalus(clavo: Offset, an: Float, a: AireAndalus, cerca: Float, caida: Offset) {
    val u = density
    val alto = an * 1.35f
    val tabla = Rect(clavo.x - an / 2, clavo.y + an * 0.32f, clavo.x + an / 2, clavo.y + an * 0.32f + alto)
    rotate(-4f, clavo) {
        drawRect(a.sombra(if (a.oscuro) 0.3f else 0.22f), tabla.topLeft + caida, tabla.size)
        val cuerda = a.luz(mezcla(CUERO, PERGAMINO, 0.5f), cerca)
        drawLine(cuerda, clavo, Offset(tabla.left + an * 0.2f, tabla.top), 0.8f * u)
        drawLine(cuerda, clavo, Offset(tabla.right - an * 0.2f, tabla.top), 0.8f * u)
        drawRect(a.luz(mezcla(PERGAMINO, CUERO, 0.28f), cerca), tabla.topLeft, tabla.size)
        drawRect(a.claro(0.35f), tabla.topLeft, Size(an * 0.1f, alto))
        drawRect(a.sombra(0.18f), Offset(tabla.right - an * 0.12f, tabla.top), Size(an * 0.12f, alto))
        drawRect(a.sombra(0.3f), Offset(tabla.left, tabla.bottom - 1f * u), Size(an, 1f * u))
        // Los hilos tendidos, con el relieve que dejan.
        val n = 11
        for (j in 1 until n) {
            val y = tabla.top + alto * j / n
            drawLine(a.sombra(0.32f), Offset(tabla.left + an * 0.08f, y + 0.6f * u), Offset(tabla.right - an * 0.08f, y + 0.6f * u), 0.6f * u)
            drawLine(a.luz(mezcla(PERGAMINO, CUERO, 0.6f), cerca), Offset(tabla.left + an * 0.08f, y), Offset(tabla.right - an * 0.08f, y), 0.7f * u)
        }
        for (x in listOf(tabla.left + an * 0.08f, tabla.right - an * 0.08f)) drawLine(a.luz(CUERO_OSC, cerca).copy(alpha = 0.6f), Offset(x, tabla.top + alto * 0.06f), Offset(x, tabla.bottom - alto * 0.06f), 0.6f * u)
    }
    drawCircle(a.sombra(0.4f), 1.6f * u, clavo + Offset(0.6f * u, 0.7f * u))
    drawCircle(a.luz(mezcla(BRONCE, CEDRO_OSC, 0.4f), cerca), 1.4f * u, clavo)
}

/** Rollos de pergamino sueltos, tumbados uno sobre otro y atados con un cordel, con la boca hacia la derecha. */
internal fun DrawScope.rollosAndalus(izq: Offset, largo: Float, cuantos: Int, a: AireAndalus, cerca: Float, semilla: Int) {
    val u = density
    val d = minOf(12f * u, largo * 0.2f)
    contacto(Offset(izq.x + largo * 0.5f, izq.y), largo * 1.05f, 4f * u, a, 0.36f)
    for (j in 0 until cuantos) {
        val l = largo * (0.78f + 0.22f * azar(semilla, j + 5))
        val x = izq.x + largo * 0.16f * azar(semilla, j)
        val yy = izq.y - d / 2 - j * d * 0.86f
        val cuerpo = Rect(x, yy - d / 2, x + l, yy + d / 2)
        drawRoundRect(a.luz(mezcla(PERGAMINO, CUERO, 0.12f + 0.1f * j), cerca), cuerpo.topLeft, cuerpo.size, CornerRadius(d / 2))
        drawRect(a.claro(0.35f), Offset(cuerpo.left + d * 0.3f, cuerpo.top + d * 0.12f), Size(l - d * 0.6f, d * 0.16f))
        drawRect(a.sombra(0.22f), Offset(cuerpo.left + d * 0.3f, cuerpo.bottom - d * 0.3f), Size(l - d * 0.6f, d * 0.3f))
        // La boca, con sus vueltas, y el cordel.
        val boca = Offset(cuerpo.right - d / 2, yy)
        drawCircle(a.luz(PERGAMINO, cerca + 0.05f), d / 2, boca)
        for (q in 1..2) drawCircle(a.luz(mezcla(PERGAMINO, CUERO, 0.5f), cerca), d / 2 * (1f - 0.3f * q), boca, style = Stroke(0.5f * u))
        drawLine(a.luz(mezcla(CARMESI, CUERO, 0.3f), cerca), Offset(cuerpo.left + l * 0.4f, cuerpo.top), Offset(cuerpo.left + l * 0.42f, cuerpo.bottom), 1.3f * u)
    }
}
