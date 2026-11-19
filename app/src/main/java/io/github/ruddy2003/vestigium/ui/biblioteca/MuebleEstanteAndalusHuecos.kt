package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.VERDE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LOZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CARMESI
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.MANGANESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.MELADO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PERGAMINO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.borde
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.contacto
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.corrido
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.ovalo
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Al-Ándalus: lo que se deja en los huecos libres de las repisas y lo que cuelga del muro
// =====================================================================

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
    val blanca = Math.floorMod(semilla, 3) != 0
    val fondo = if (blanca) LOZA else MELADO
    val melado = a.luz(fondo, cerca)
    val trazo = a.luz(MANGANESO, cerca)
    drawPath(corrido(plato, r * 0.035f, r * 0.05f), a.luz(mezcla(fondo, MANGANESO, 0.45f), cerca))
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
    drawPath(pozo, a.luz(if (blanca) LOZA else mezcla(MELADO, BARRO, 0.22f), cerca))
    if (blanca) {
        // Verde y manganeso: una palmeta en cada cuarto, de cobre verde perfilada de manganeso, y un botón en el medio.
        val verde = a.luz(VERDE, cerca)
        for (q in 0 until 4) rotate(90f * q + (semilla % 2) * 45f, c) {
            val hoja = Path().apply { moveTo(c.x, c.y - r * 0.08f); cubicTo(c.x + r * 0.22f, c.y - r * 0.18f, c.x + r * 0.16f, c.y - r * 0.44f, c.x, c.y - r * 0.52f); cubicTo(c.x - r * 0.16f, c.y - r * 0.44f, c.x - r * 0.22f, c.y - r * 0.18f, c.x, c.y - r * 0.08f); close() }
            drawPath(hoja, verde)
            drawPath(hoja, trazo, style = Stroke(0.9f * u))
            drawCircle(trazo, r * 0.04f, Offset(c.x + r * 0.3f, c.y - r * 0.3f))
        }
        drawCircle(verde, r * 0.1f, c)
        drawCircle(trazo, r * 0.1f, c, style = Stroke(0.8f * u))
    } else if (semilla % 2 == 0) {
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
