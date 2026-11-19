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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.SEDA_VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.SEDA_ANIL
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
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
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
            val sc = a.sombra(if (a.oscuro) 0.26f else 0.2f)
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
    translate(caida.x, caida.y) { drawPath(disco, a.sombra(if (a.oscuro) 0.24f else 0.22f)); drawPath(trono, a.sombra(if (a.oscuro) 0.24f else 0.22f)) }
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
internal fun DrawScope.redomaAndalus(pie: Offset, alto: Float, a: AireAndalus, cerca: Float, haciaX: Float, caida: Offset, alSol: Boolean = false) {
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
    if (alSol) {
        // La luz de la celosía la atraviesa: deja una mancha verde y clara en lo que pisa y un destello en el hombro.
        val luz = if (a.lumbre) PaletaAndalus.LUZ_LUNA else mezcla(VIDRIO, PaletaAndalus.SOL, 0.4f)
        drawOval(luz.copy(alpha = if (a.lumbre) 0.35f else 0.55f), Offset(pie.x + alto * 0.02f, pie.y - alto * 0.03f), Size(alto * 0.34f, alto * 0.06f))
        val d = Offset(pie.x - alto * 0.12f, pie.y - alto * 0.44f)
        val brillo = if (a.lumbre) PaletaAndalus.LUZ_LUNA else Color.White
        drawLine(brillo.copy(alpha = 0.9f), d - Offset(alto * 0.09f, 0f), d + Offset(alto * 0.09f, 0f), 0.8f * u, StrokeCap.Round)
        drawLine(brillo.copy(alpha = 0.9f), d - Offset(0f, alto * 0.09f), d + Offset(0f, alto * 0.09f), 0.8f * u, StrokeCap.Round)
        drawCircle(brillo, 1.3f * u, d)
    }
}

/**
 * El códice abierto sobre un cojín de seda índigo, en el suelo: el cojín mullido con su banda tejida de oro y almagra (rombos
 * y puntos, sin letra), sus pliegues y una borla en cada esquina; encima, el libro abierto, con las hojas pautadas que se
 * curvan hacia el lomo, el cuero de la tapa asomando por debajo y el registro colgando. [pie] es el centro de abajo del cojín.
 */
internal fun DrawScope.cojinCodiceAndalus(pie: Offset, an: Float, a: AireAndalus, cerca: Float, haciaX: Float) {
    val u = density
    val hh = an * 0.3f
    val c = Rect(pie.x - an / 2, pie.y - hh, pie.x + an / 2, pie.y)
    val forma = Path().apply {
        moveTo(c.left + an * 0.04f, c.top + hh * 0.3f)
        cubicTo(c.left + an * 0.25f, c.top - hh * 0.06f, c.right - an * 0.25f, c.top - hh * 0.06f, c.right - an * 0.04f, c.top + hh * 0.3f)
        cubicTo(c.right + an * 0.02f, c.top + hh * 0.55f, c.right + an * 0.01f, c.bottom - hh * 0.25f, c.right - an * 0.03f, c.bottom - hh * 0.05f)
        cubicTo(c.right - an * 0.25f, c.bottom + hh * 0.06f, c.left + an * 0.25f, c.bottom + hh * 0.06f, c.left + an * 0.03f, c.bottom - hh * 0.05f)
        cubicTo(c.left - an * 0.01f, c.bottom - hh * 0.25f, c.left - an * 0.02f, c.top + hh * 0.55f, c.left + an * 0.04f, c.top + hh * 0.3f)
        close()
    }
    contacto(Offset(pie.x + an * 0.06f, pie.y), an * 1.1f, hh * 0.22f, a, 0.4f)
    val seda = a.luz(SEDA_ANIL, cerca)
    // Las borlas de atrás, que quedan detrás del cojín.
    for (l in listOf(-1f, 1f)) borlaAndalus(Offset(pie.x + l * (an * 0.46f), c.top + hh * 0.3f), hh * 0.32f, a.luz(ORO, cerca), a)
    drawPath(forma, seda)
    clipPath(forma) {
        // La banda tejida, que sigue la panza del cojín.
        val y0 = c.top + hh * 0.5f
        val banda = Path().apply {
            moveTo(c.left - 2f * u, y0); cubicTo(c.left + an * 0.3f, y0 + hh * 0.08f, c.right - an * 0.3f, y0 + hh * 0.08f, c.right + 2f * u, y0)
            lineTo(c.right + 2f * u, y0 + hh * 0.2f); cubicTo(c.right - an * 0.3f, y0 + hh * 0.28f, c.left + an * 0.3f, y0 + hh * 0.28f, c.left - 2f * u, y0 + hh * 0.2f); close()
        }
        drawPath(banda, a.luz(mezcla(ORO, SEDA_ANIL, 0.15f), cerca))
        for (q in 0 until 9) {
            val x = c.left + an * (0.08f + 0.105f * q)
            val y = y0 + hh * (0.1f + 0.06f * (1f - kotlin.math.abs(q - 4) / 4f))
            drawPath(poligono(x, y - hh * 0.07f, x + an * 0.03f, y, x, y + hh * 0.07f, x - an * 0.03f, y), a.luz(if (q % 2 == 0) CARMESI else SEDA_ANIL, cerca))
            drawCircle(a.luz(CARMESI, cerca), hh * 0.02f, Offset(x + an * 0.052f, y))
        }
        // Los pliegues que salen de las esquinas, el claro de la panza y la sombra de abajo.
        for (l in listOf(-1f, 1f)) {
            drawPath(Path().apply { moveTo(pie.x + l * an * 0.47f, c.bottom - hh * 0.1f); cubicTo(pie.x + l * an * 0.38f, c.bottom - hh * 0.25f, pie.x + l * an * 0.32f, c.bottom - hh * 0.3f, pie.x + l * an * 0.24f, c.bottom - hh * 0.28f) }, a.sombra(0.3f), style = Stroke(1f * u))
            drawPath(Path().apply { moveTo(pie.x + l * an * 0.46f, c.top + hh * 0.34f); cubicTo(pie.x + l * an * 0.38f, c.top + hh * 0.3f, pie.x + l * an * 0.33f, c.top + hh * 0.36f, pie.x + l * an * 0.26f, c.top + hh * 0.34f) }, a.sombra(0.25f), style = Stroke(0.9f * u))
        }
        drawOval(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.25f * cerca) else a.claro(0.3f), Offset(c.left + an * 0.12f, c.top + hh * 0.02f), Size(an * 0.42f, hh * 0.3f))
        drawRect(a.sombra(0.3f), Offset(c.left, c.bottom - hh * 0.22f), Size(an, hh * 0.3f))
    }
    for (l in listOf(-1f, 1f)) borlaAndalus(Offset(pie.x + l * (an * 0.47f), c.bottom - hh * 0.06f), hh * 0.4f, a.luz(ORO, cerca), a)
    // El libro abierto encima: la tapa de cuero que asoma, el bloque de hojas y las dos páginas que se curvan hacia el lomo.
    val lc = Offset(pie.x + an * 0.02f, c.top + hh * 0.2f)
    val bw = an * 0.62f; val bh = hh * 0.62f
    val cuero = a.luz(mezcla(CARMESI, CUERO_OSC, 0.35f), cerca)
    drawPath(poligono(lc.x - bw * 0.52f, lc.y - bh * 0.42f, lc.x + bw * 0.52f, lc.y - bh * 0.42f, lc.x + bw * 0.54f, lc.y + bh * 0.56f, lc.x - bw * 0.54f, lc.y + bh * 0.56f), cuero)
    val hoja = a.luz(PERGAMINO, cerca + 0.08f)
    val canto = a.luz(mezcla(PERGAMINO, CUERO, 0.3f), cerca)
    drawPath(poligono(lc.x - bw * 0.5f, lc.y + bh * 0.3f, lc.x, lc.y + bh * 0.42f, lc.x + bw * 0.5f, lc.y + bh * 0.3f, lc.x + bw * 0.5f, lc.y + bh * 0.48f, lc.x, lc.y + bh * 0.52f, lc.x - bw * 0.5f, lc.y + bh * 0.48f), canto)
    for (l in listOf(-1f, 1f)) {
        val pagina = Path().apply {
            moveTo(lc.x, lc.y - bh * 0.32f)
            cubicTo(lc.x + l * bw * 0.1f, lc.y - bh * 0.48f, lc.x + l * bw * 0.35f, lc.y - bh * 0.42f, lc.x + l * bw * 0.5f, lc.y - bh * 0.36f)
            lineTo(lc.x + l * bw * 0.5f, lc.y + bh * 0.3f)
            cubicTo(lc.x + l * bw * 0.35f, lc.y + bh * 0.26f, lc.x + l * bw * 0.1f, lc.y + bh * 0.3f, lc.x, lc.y + bh * 0.42f)
            close()
        }
        drawPath(pagina, if (l > 0) mezcla(hoja, a.hondo, 0.08f) else hoja)
        // Los renglones pautados con la regla de cuerdas: líneas finas, sin letra.
        clipPath(pagina) {
            for (j in 0 until 6) {
                val y = lc.y - bh * 0.24f + j * bh * 0.1f
                drawLine(a.luz(mezcla(PERGAMINO, TINTA, 0.3f), cerca).copy(alpha = 0.5f), Offset(lc.x + l * bw * 0.07f, y + bh * 0.02f), Offset(lc.x + l * bw * 0.44f, y), 0.5f * u)
            }
            drawRect(a.sombra(0.18f), Offset(lc.x - bw * 0.06f, lc.y - bh), Size(bw * 0.12f, bh * 2f))
        }
    }
    drawLine(a.luz(CARMESI, cerca), Offset(lc.x, lc.y + bh * 0.4f), Offset(lc.x + bw * 0.04f, lc.y + bh * 1.05f), 1.4f * u, StrokeCap.Round)
    if (a.oscuro) drawLine(FILO_MUEBLE.copy(alpha = 0.5f * cerca), Offset(lc.x - bw * 0.5f, lc.y - bh * 0.36f), Offset(lc.x, lc.y - bh * 0.32f), 0.8f * u)
}

/** Una borla de seda: el nudo redondo, la cabeza y la falda de hilos que se abre, con su luz a la izquierda. */
internal fun DrawScope.borlaAndalus(arriba: Offset, largo: Float, color: Color, a: AireAndalus) {
    val u = density
    val cab = largo * 0.22f
    drawCircle(mezcla(color, a.hondo, 0.2f), cab * 0.6f, arriba)
    val falda = poligono(arriba.x - cab * 0.45f, arriba.y + cab * 0.4f, arriba.x + cab * 0.45f, arriba.y + cab * 0.4f, arriba.x + cab * 0.9f, arriba.y + largo, arriba.x - cab * 0.9f, arriba.y + largo)
    drawPath(falda, color)
    for (q in -2..2) drawLine(mezcla(color, a.hondo, 0.35f), Offset(arriba.x + q * cab * 0.15f, arriba.y + cab * 0.6f), Offset(arriba.x + q * cab * 0.36f, arriba.y + largo), 0.5f * u)
    drawRect(mezcla(color, a.hondo, 0.3f), Offset(arriba.x - cab * 0.5f, arriba.y + cab * 0.35f), Size(cab, cab * 0.25f))
    drawLine(a.claro(0.4f), Offset(arriba.x - cab * 0.3f, arriba.y + cab * 0.7f), Offset(arriba.x - cab * 0.6f, arriba.y + largo * 0.9f), 0.6f * u)
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
    // Un paño de seda verde echado encima: se ve su vuelo sobre la tapa y cae por delante en pliegues que se abren en abanico,
    // con las dos puntas más bajas que el medio, su banda tejida y su fleco.
    val sedaV = a.luz(SEDA_VERDE, cerca)
    val x0 = cuerpo.left + an * 0.1f; val x1 = cuerpo.left + an * 0.6f
    val yT = tapa.top - alto * 0.04f
    val bajo = cuerpo.top + alto * 0.62f
    val hem = listOf(Offset(x0 - an * 0.04f, bajo + alto * 0.12f), Offset(x0 + (x1 - x0) * 0.22f, bajo - alto * 0.02f), Offset(x0 + (x1 - x0) * 0.42f, bajo + alto * 0.06f),
        Offset(x0 + (x1 - x0) * 0.62f, bajo - alto * 0.04f), Offset(x0 + (x1 - x0) * 0.82f, bajo + alto * 0.03f), Offset(x1 + an * 0.05f, bajo + alto * 0.16f))
    val pano = Path().apply {
        moveTo(x0 + an * 0.02f, yT)
        cubicTo(x0 + (x1 - x0) * 0.3f, yT - alto * 0.05f, x0 + (x1 - x0) * 0.7f, yT - alto * 0.04f, x1 - an * 0.01f, yT + alto * 0.01f)
        cubicTo(x1 + an * 0.04f, tapa.bottom, x1 + an * 0.04f, cuerpo.top + alto * 0.3f, hem.last().x, hem.last().y)
        for (q in hem.size - 2 downTo 0) {
            val p0 = hem[q + 1]; val p1 = hem[q]
            quadraticTo((p0.x + p1.x) / 2, maxOf(p0.y, p1.y) + alto * 0.05f, p1.x, p1.y)
        }
        cubicTo(x0 - an * 0.03f, cuerpo.top + alto * 0.3f, x0 - an * 0.02f, tapa.bottom, x0 + an * 0.02f, yT)
        close()
    }
    drawPath(corrido(pano, 3f * u, 2.5f * u), a.sombra(0.3f))
    drawPath(pano, sedaV)
    clipPath(pano) {
        // El vuelo sobre la tapa, más claro, y el quiebro del canto.
        drawRect(a.luz(mezcla(SEDA_VERDE, LUZ_MUEBLE, 0.28f), cerca), Offset(x0 - an * 0.05f, yT - alto * 0.06f), Size(x1 - x0 + an * 0.15f, tapa.bottom - yT + alto * 0.04f))
        drawRect(a.sombra(0.28f), Offset(x0 - an * 0.05f, tapa.bottom), Size(x1 - x0 + an * 0.15f, alto * 0.03f))
        // Los pliegues: cada uno nace junto al canto y se abre hacia su punta del borde, con su valle en sombra y su lomo a la luz.
        for (q in 0 until hem.size) {
            val pt = hem[q]
            val nace = Offset(x0 + (x1 - x0) * (0.12f + 0.76f * q / (hem.size - 1)), tapa.bottom + alto * 0.02f)
            val valle = Path().apply { moveTo(nace.x, nace.y); cubicTo(nace.x, nace.y + alto * 0.2f, pt.x + an * 0.01f, pt.y - alto * 0.25f, pt.x + an * 0.018f, pt.y) }
            drawPath(valle, a.sombra(0.32f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawPath(corrido(valle, -an * 0.03f, 0f), a.claro(0.28f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
        // La banda tejida, que sigue la caída.
        val banda = Path().apply {
            moveTo(hem.first().x, hem.first().y - alto * 0.16f)
            for (q in 1 until hem.size) lineTo(hem[q].x, hem[q].y - alto * 0.16f)
            for (q in hem.size - 1 downTo 0) lineTo(hem[q].x, hem[q].y - alto * 0.09f)
            close()
        }
        drawPath(banda, a.luz(mezcla(ORO, SEDA_VERDE, 0.2f), cerca))
        for (q in 0 until hem.size - 1) {
            val m = (hem[q] + hem[q + 1]) / 2f
            drawCircle(a.luz(CARMESI, cerca), alto * 0.018f, Offset(m.x, m.y - alto * 0.125f))
        }
    }
    // El fleco del borde.
    for (q in 0 until hem.size - 1) for (z in 0 until 4) {
        val t = z / 4f
        val p = hem[q] + (hem[q + 1] - hem[q]) * t
        drawLine(a.luz(mezcla(ORO, SEDA_VERDE, 0.3f), cerca), Offset(p.x, p.y + 1f * u), Offset(p.x + 0.3f * u, p.y + alto * 0.08f), 0.6f * u)
    }
}
