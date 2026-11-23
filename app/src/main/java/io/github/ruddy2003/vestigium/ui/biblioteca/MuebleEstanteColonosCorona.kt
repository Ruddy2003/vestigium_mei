package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.BOMBILLA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CALABAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CORDEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.GIRASOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PELTRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SALVIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/*
 * La corona de la alacena de Colonos: la pared con lo que cuelga (la sartén, la antena, la bombilla) y el bodegón de encima
 * (la lata de girasoles, la radio con su tapete y su batería, la calabaza, la cafetera y las revistas atadas). La radio, la
 * batería y el tapete están en `MuebleEstanteColonosRadio.kt`.
 */

/**
 * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la bombilla, tanto más marcada
 * cuanto más cerca está de ella. No baja de [hasta] (la cara de arriba de la alacena, que la corta).
 */
internal fun DrawScope.sombraParedColonos(g: MarcoColonos, p: Path, desde: Offset, hasta: Float, a0: Float = 0.18f) {
    val w = g.w; val a = g.a
    clipRect(0f, 0f, w, hasta) {
        if (!a.oscuro) translate(w * 0.02f, w * 0.012f) { drawPath(p, a.sombra(a0)) }
        else {
            val k = g.k(desde)
            if (k > 0.1f) {
                val dx = (desde.x / w - MuebleEstanteColonos.LAMP_X).coerceIn(-0.3f, 0.3f) / 0.3f
                val dy = (desde.y / w - MuebleEstanteColonos.LAMP_Y).coerceIn(-0.3f, 0.3f) / 0.3f
                translate(w * 0.035f * dx, w * 0.03f * dy) { drawPath(p, a.hondo.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
            }
        }
    }
}

/**
 * Toda la corona, pintada una vez por tamaño y modo: la pared, la cenefa, los desconchones, la viga, la ventana con su sol, lo
 * que cuelga, la cornisa y el bodegón de encima; de noche, el resplandor de la bombilla encima de lo que tiene cerca.
 */
internal fun DrawScope.coronaColonos(g: MarcoColonos) {
    val w = g.w; val a = g.a
    val vb = w * 0.05f
    // La pared llega hasta abajo de la corona: asoma a los lados de la cornisa y no deja nada transparente en la imagen guardada.
    paredColonos(g, g.ch + 1f)
    cenefaColonos(g, w * 0.06f, w * 0.088f)
    desconchonColonos(g, Offset(w * 0.93f, w * 0.38f), w * 0.034f, 7161, true)
    desconchonColonos(g, Offset(w * 0.62f, w * 0.36f), w * 0.018f, 7162, false)
    val vent = Rect(w * 0.05f, w * 0.108f, w * 0.23f, w * 0.282f)
    ventanaBotellasColonos(g, vent)
    vigaColonos(g, vb)
    coladorColonos(g, Offset(w * 0.6f, w * 0.15f), w * 0.03f)
    sartenColonos(g, Offset(w * 0.69f, w * 0.104f))
    antenaColonos(g, Offset(w * 0.44f, w * 0.27f), Offset(w * 0.575f, w * 0.07f))
    hierbasColonos(g, Offset(w * 0.955f, vb))
    bombillaColonos(g, vb)
    cornisaColonos(g)
    luzVentanaEnTapaColonos(g, vent)
    // El bodegón, de atrás adelante.
    val pie = g.apoyo - w * 0.012f
    girasolesColonos(g, Offset(w * 0.093f, pie))
    revistasColonos(g, Rect(w * 0.79f, pie - w * 0.054f, w * 0.965f, pie + w * 0.002f))
    tapeteGanchilloColonos(g, Rect(w * 0.15f, g.apoyoAtras, w * 0.49f, g.apoyo))
    radioColonos(g, Rect(w * 0.17f, w * 0.258f, w * 0.47f, pie - w * 0.002f))
    charolaColonos(g, Offset(w * 0.755f, pie - w * 0.002f), w * 0.072f)
    cafeteraColonos(g, Offset(w * 0.68f, pie - w * 0.004f))
    bateriaColonos(g, Rect(w * 0.49f, pie - w * 0.07f, w * 0.585f, pie + w * 0.004f))
    cablesRadioColonos(g, Offset(w * 0.465f, pie - w * 0.03f), Offset(w * 0.505f, pie - w * 0.072f), Offset(w * 0.565f, pie - w * 0.072f))
    calabazaColonos(g, Offset(w * 0.445f, g.apoyo - w * 0.034f), w * 0.064f, w * 0.036f)
    // De noche, la bombilla arde encima de todo lo que tiene cerca.
    val l = Offset(w * MuebleEstanteColonos.LAMP_X, w * MuebleEstanteColonos.LAMP_Y)
    if (a.oscuro) {
        val f = if (a.lunar) 0.6f else 1f
        mancha(l + Offset(0f, w * 0.08f), w * 0.55f, BOMBILLA.copy(alpha = 0.1f * f))
        mancha(l, w * 0.14f, BOMBILLA.copy(alpha = 0.3f * f))
        mancha(l, w * 0.045f, Color(0xFFFFF2D0).copy(alpha = 0.6f * f))
    } else if (a.noche) {
        mancha(l, w * 0.16f, BOMBILLA.copy(alpha = 0.32f))
    }
}

// ---------------------------------------------------------------- lo que cuelga

/**
 * La bombilla del generador: el cordón torcido que baja de la viga, el portalámparas, la pantalla hecha de un molde de lata
 * (con sus estrías, sus abolladuras y, por dentro, el blanco que refleja) y el vidrio. De día, apagada: el vidrio claro con su
 * filamento. De noche, arde: el vidrio blanco, el filamento encendido y el dentro de la pantalla iluminado; en los Sueños de
 * noche, baja y naranja (el generador al mínimo).
 */
internal fun DrawScope.bombillaColonos(g: MarcoColonos, viga: Float) {
    val u = density; val w = g.w; val a = g.a
    val x = w * MuebleEstanteColonos.LAMP_X; val yb = w * MuebleEstanteColonos.LAMP_Y
    val rb = w * 0.017f
    val portaY = yb - w * 0.058f
    val pantallaArriba = yb - w * 0.04f; val pantallaAbajo = yb - w * 0.014f
    val ancho = w * 0.068f
    // El cordón torcido.
    val cordon = Path().apply { moveTo(x, viga); lineTo(x, portaY) }
    if (!a.oscuro) translate(w * 0.012f, w * 0.006f) { drawPath(cordon, a.sombra(0.2f), style = Stroke(1.6f * u)) }
    drawPath(cordon, g.c(HIERRO, Offset(x, portaY), 0.6f), style = Stroke(1.6f * u))
    var ty = viga
    val giro = Path()
    while (ty < portaY) { giro.moveTo(x - 0.8f * u, ty); giro.lineTo(x + 0.8f * u, ty + 1.2f * u); ty += 2.2f * u }
    drawPath(giro, a.brillo(0.3f, g.k(Offset(x, portaY)) * 0.6f), style = Stroke(0.5f * u))
    // El vidrio, detrás de la boca de la pantalla.
    val encendida = a.lumbre
    val vidrio = Path().apply {
        moveTo(x - rb * 0.45f, pantallaAbajo - 1f)
        cubicTo(x - rb * 0.5f, yb - rb * 0.6f, x - rb * 1.05f, yb - rb * 0.5f, x - rb, yb)
        cubicTo(x - rb * 0.95f, yb + rb * 1.2f, x + rb * 0.95f, yb + rb * 1.2f, x + rb, yb)
        cubicTo(x + rb * 1.05f, yb - rb * 0.5f, x + rb * 0.5f, yb - rb * 0.6f, x + rb * 0.45f, pantallaAbajo - 1f); close()
    }
    if (encendida) {
        val baja = a.lunar
        val nucleo = if (baja) Color(0xFFFFB25A) else Color(0xFFFFF4D8)
        mancha(Offset(x, yb), rb * 3.2f, (if (baja) BOMBILLA else Color(0xFFFFE7B0)).copy(alpha = 0.55f))
        drawPath(vidrio, mezcla(nucleo, BOMBILLA, 0.25f))
        drawCircle(nucleo, rb * 0.55f, Offset(x, yb + rb * 0.1f))
        drawPath(filamentoColonos(x, yb, rb),
            Color.White, style = Stroke(1f * u))
    } else {
        drawPath(corridoColonos(vidrio, w * 0.008f, w * 0.006f), a.sombra(0.12f))
        drawPath(vidrio, mezcla(PaletaColonos.CAL_LUZ, Color.White, 0.3f).copy(alpha = 0.7f))
        drawPath(vidrio, a.sombra(0.25f), style = Stroke(0.7f * u))
        drawPath(filamentoColonos(x, yb, rb),
            HIERRO.copy(alpha = 0.6f), style = Stroke(0.6f * u))
        drawLine(HIERRO.copy(alpha = 0.4f), Offset(x - rb * 0.2f, pantallaAbajo), Offset(x - rb * 0.3f, yb - rb * 0.1f), 0.5f * u)
        drawLine(HIERRO.copy(alpha = 0.4f), Offset(x + rb * 0.2f, pantallaAbajo), Offset(x + rb * 0.3f, yb - rb * 0.1f), 0.5f * u)
        drawOval(Color.White.copy(alpha = 0.8f), Offset(x - rb * 0.7f, yb - rb * 0.3f), Size(rb * 0.3f, rb * 0.7f))
    }
    // El portalámparas.
    val porta = Rect(x - w * 0.009f, portaY, x + w * 0.009f, pantallaArriba + w * 0.006f)
    drawRect(g.c(HIERRO, porta.center), porta.topLeft, porta.size)
    drawRect(a.brillo(0.35f, g.k(porta.center)), porta.topLeft, Size(1f * u, porta.height))
    // La pantalla: un molde de lata con su boca abierta hacia abajo, vista un poco desde abajo (se le ve el blanco de dentro).
    val boca = Rect(x - ancho, pantallaAbajo - w * 0.007f, x + ancho, pantallaAbajo + w * 0.007f)
    val cono = poligonoColonos(x - w * 0.014f, pantallaArriba, x + w * 0.014f, pantallaArriba, boca.right, boca.center.y, boca.left, boca.center.y)
    if (!a.oscuro) sombraParedColonos(g, Path().apply { addPath(cono); addOval(boca) }, Offset(x, pantallaArriba), g.apoyoAtras, 0.16f)
    val kl = if (encendida) 0.25f else g.k(Offset(x, pantallaArriba))
    drawPath(cono, a.luz(LATA, kl))
    // Las estrías del molde y una abolladura.
    val estrias = Path()
    for (i in 0..10) {
        val f = i / 10f
        estrias.moveTo(x - w * 0.014f + w * 0.028f * f, pantallaArriba)
        estrias.lineTo(boca.left + boca.width * f, boca.center.y)
    }
    drawPath(estrias, a.sombra(0.22f), style = Stroke(0.7f * u))
    drawPath(poligonoColonos(x - w * 0.014f, pantallaArriba, x - w * 0.004f, pantallaArriba, boca.left + boca.width * 0.32f, boca.center.y, boca.left, boca.center.y),
        if (a.oscuro) Color.Transparent else a.claro(0.3f))
    drawOval(a.sombra(0.2f), Offset(x + ancho * 0.25f, pantallaArriba + w * 0.008f), Size(ancho * 0.3f, w * 0.008f))
    // El dentro, por la boca: blanco de lata; encendido, refleja la luz.
    drawOval(if (encendida) mezcla(Color(0xFFFFF0C8), BOMBILLA, if (a.lunar) 0.6f else 0.2f) else a.luz(mezcla(LATA, Color.White, 0.35f), 1f), boca.topLeft, boca.size)
    drawOval(a.luz(LATA_OSC, kl), boca.topLeft, boca.size, style = Stroke(1.2f * u))
    // El vidrio asoma por la boca, delante del blanco.
    clipRect(0f, boca.center.y - 1f, w, g.apoyo) {
        if (encendida) drawPath(vidrio, mezcla(Color(0xFFFFF4D8), BOMBILLA, if (a.lunar) 0.7f else 0.2f))
        else { drawPath(vidrio, mezcla(PaletaColonos.CAL_LUZ, Color.White, 0.4f).copy(alpha = 0.85f)); drawPath(vidrio, a.sombra(0.3f), style = Stroke(0.7f * u)) }
        if (encendida) drawCircle(Color.White.copy(alpha = 0.9f), rb * 0.45f, Offset(x, yb + rb * 0.15f))
        else drawOval(Color.White.copy(alpha = 0.85f), Offset(x - rb * 0.72f, yb - rb * 0.2f), Size(rb * 0.28f, rb * 0.62f))
    }
}

// ---------------------------------------------------------------- el bodegón

/**
 * La lata de girasoles secos, debajo de la ventana: la lata con sus anillos, el óxido y la franja de pintura que le queda; tres
 * tallos con sus hojas secas, una cabeza grande de frente, otra caída de perfil y un botón; los pétalos dorados con las puntas
 * pardas y el centro de pipas en espiral. Rompe la línea de arriba de la alacena.
 */
internal fun DrawScope.girasolesColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val lw = w * 0.07f; val lh = w * 0.078f
    val lata = Rect(pie.x - lw / 2, pie.y - lh, pie.x + lw / 2, pie.y)
    // Los tallos y las hojas, detrás de las cabezas.
    val cabezas = listOf(Offset(w * 0.072f, w * 0.322f) to w * 0.036f, Offset(w * 0.118f, w * 0.282f) to w * 0.024f, Offset(w * 0.15f, w * 0.385f) to w * 0.03f)
    val tallos = Path()
    cabezas.forEachIndexed { i, (c, _) ->
        tallos.moveTo(pie.x + lw * (i - 1) * 0.18f, lata.top + w * 0.004f)
        tallos.quadraticTo(pie.x + lw * (i - 1) * 0.1f + w * 0.01f * (1 - i), (c.y + lata.top) / 2, c.x, c.y + (if (i == 2) -w * 0.012f else w * 0.01f))
    }
    val hojas = Path().apply {
        hojaGirasol(this, Offset(pie.x - lw * 0.1f, w * 0.4f), w * 0.04f, -150f)
        hojaGirasol(this, Offset(pie.x + lw * 0.2f, w * 0.43f), w * 0.036f, -20f)
        hojaGirasol(this, Offset(pie.x + lw * 0.05f, w * 0.36f), w * 0.03f, -60f)
    }
    val todo = Path().apply { addPath(hojas); cabezas.forEach { (c, r) -> addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }; addRect(lata) }
    sombraParedColonos(g, todo, lata.center, g.apoyoAtras)
    val ks = g.k(Offset(pie.x, w * 0.38f))
    drawPath(tallos, a.luz(mezcla(VERDE, PINO_OSC, 0.45f), ks), style = Stroke(2f * u, cap = StrokeCap.Round))
    drawPath(hojas, a.luz(mezcla(VERDE, PINO_OSC, 0.55f), ks))
    drawPath(hojas, a.sombra(0.3f), style = Stroke(0.7f * u))
    // Las cabezas: la de perfil caída (de atrás), el botón y la grande de frente.
    cabezaPerfil(g, cabezas[2].first, cabezas[2].second)
    cabezaGirasol(g, cabezas[1].first, cabezas[1].second, 7710)
    cabezaGirasol(g, cabezas[0].first, cabezas[0].second, 7711)
    // La lata: su cara con los anillos, la franja de salvia que le queda, el óxido, la luz y la sombra, y el borde de arriba.
    contactoColonos(Offset(pie.x, pie.y), lw * 1.2f, w * 0.012f, a, 0.32f)
    val kl = g.k(lata.center); val bl = g.br(lata.center)
    drawRect(a.luz(LATA, kl, bl), lata.topLeft, lata.size)
    drawRect(a.luz(SALVIA, kl, bl), Offset(lata.left, lata.top + lh * 0.3f), Size(lw, lh * 0.42f))
    drawRect(a.luz(OXIDO, kl, bl).copy(alpha = 0.6f), Offset(lata.left, lata.bottom - lh * 0.22f), Size(lw, lh * 0.22f))
    for (i in 1..4) drawLine(a.sombra(0.25f), Offset(lata.left, lata.top + lh * i / 5f), Offset(lata.right, lata.top + lh * i / 5f), 0.7f * u)
    drawRect(a.claro(0.35f), lata.topLeft, Size(lw * 0.22f, lh))
    drawRect(a.sombra(0.28f), Offset(lata.right - lw * 0.3f, lata.top), Size(lw * 0.3f, lh))
    drawRect(a.luz(LATA_OSC, kl, bl), Offset(lata.left - 0.5f * u, lata.top - 1.2f * u), Size(lw + 1f * u, 2.2f * u))
    if (a.oscuro && kl > 0.08f) drawRect(a.brillo(0.6f * kl, kl, bl), Offset(lata.right - 1.3f * u, lata.top), Size(1.3f * u, lh))
}

/** Una hoja de girasol seca: un corazón alargado y un poco enrollado, girado [grados] desde su pecíolo en [p]. */
private fun hojaGirasol(camino: Path, p: Offset, largo: Float, grados: Float) {
    val r = Math.toRadians(grados.toDouble())
    val c = kotlin.math.cos(r).toFloat(); val s = kotlin.math.sin(r).toFloat()
    fun q(x: Float, y: Float) = Offset(p.x + x * c - y * s, p.y + x * s + y * c)
    val a0 = q(0f, 0f); val a1 = q(largo * 0.3f, -largo * 0.34f); val a2 = q(largo, -largo * 0.05f); val a3 = q(largo * 0.35f, largo * 0.24f)
    camino.moveTo(a0.x, a0.y)
    camino.quadraticTo(a1.x, a1.y, a2.x, a2.y)
    camino.quadraticTo(a3.x, a3.y, a0.x, a0.y)
    camino.close()
}

/** Una cabeza de girasol de frente: la corona de pétalos dorados (puntas pardas), el disco de pipas en espiral y su luz. */
private fun DrawScope.cabezaGirasol(g: MarcoColonos, c: Offset, r: Float, semilla: Int) {
    val u = density; val a = g.a
    val k = g.k(c); val br = g.br(c)
    val petalos = Path(); val puntas = Path()
    val n = 16
    for (i in 0 until n) {
        val ang = i * 6.283f / n + azar(semilla, i) * 0.2f
        val l = r * (0.92f + 0.18f * azar(semilla, 20 + i))
        val cx = kotlin.math.cos(ang); val sy = kotlin.math.sin(ang)
        val base = Offset(c.x + cx * r * 0.42f, c.y + sy * r * 0.42f)
        val punta = Offset(c.x + cx * l, c.y + sy * l)
        val lado = Offset(-sy * r * 0.13f, cx * r * 0.13f)
        petalos.moveTo(base.x - lado.x, base.y - lado.y)
        petalos.quadraticTo((base.x + punta.x) / 2 - lado.x * 1.4f, (base.y + punta.y) / 2 - lado.y * 1.4f, punta.x, punta.y)
        petalos.quadraticTo((base.x + punta.x) / 2 + lado.x * 1.4f, (base.y + punta.y) / 2 + lado.y * 1.4f, base.x + lado.x, base.y + lado.y)
        petalos.close()
        puntas.addOval(Rect(punta.x - r * 0.07f, punta.y - r * 0.07f, punta.x + r * 0.07f, punta.y + r * 0.07f))
    }
    drawPath(petalos, a.luz(GIRASOL, k, br))
    drawPath(puntas, a.luz(mezcla(GIRASOL, OXIDO, 0.7f), k, br))
    drawPath(petalos, a.sombra(0.22f), style = Stroke(0.5f * u))
    drawCircle(a.luz(Color(0xFF4A2C17), k, br), r * 0.47f, c)
    // Las pipas: puntos en espiral, más claros hacia fuera.
    val pipas = Path()
    for (i in 0 until 24) {
        val t = i * 2.4f
        val rr = r * 0.44f * kotlin.math.sqrt(i / 24f)
        val q = Offset(c.x + rr * kotlin.math.cos(t), c.y + rr * kotlin.math.sin(t))
        pipas.addOval(Rect(q.x - r * 0.035f, q.y - r * 0.035f, q.x + r * 0.035f, q.y + r * 0.035f))
    }
    drawPath(pipas, a.luz(Color(0xFF7A5430), k, br))
    drawCircle(a.brillo(0.25f, k, br), r * 0.2f, c - Offset(r * 0.15f, r * 0.15f))
}

/** Una cabeza de girasol caída, de perfil: el disco visto de canto con los sépalos verdes y los pétalos que cuelgan. */
private fun DrawScope.cabezaPerfil(g: MarcoColonos, c: Offset, r: Float) {
    val u = density; val a = g.a
    val k = g.k(c); val br = g.br(c)
    rotate(28f, c) {
        val disco = Rect(c.x - r, c.y - r * 0.34f, c.x + r, c.y + r * 0.34f)
        val petalos = Path()
        for (i in 0 until 9) {
            val x = disco.left + disco.width * (i + 0.5f) / 9f
            petalos.moveTo(x - r * 0.1f, disco.bottom - r * 0.1f)
            petalos.quadraticTo(x - r * 0.05f, disco.bottom + r * 0.5f, x + r * 0.04f * (i % 3 - 1), disco.bottom + r * (0.55f + 0.2f * azar(7712, i)))
            petalos.quadraticTo(x + r * 0.08f, disco.bottom + r * 0.3f, x + r * 0.1f, disco.bottom - r * 0.1f)
            petalos.close()
        }
        drawPath(petalos, a.luz(mezcla(GIRASOL, OXIDO, 0.25f), k, br))
        drawOval(a.luz(mezcla(VERDE, PINO_OSC, 0.3f), k, br), disco.topLeft, disco.size)
        val sepalos = Path()
        for (i in 0 until 7) {
            val x = disco.left + disco.width * (i + 0.5f) / 7f
            sepalos.moveTo(x - r * 0.12f, disco.top + r * 0.1f); sepalos.lineTo(x, disco.top - r * 0.22f); sepalos.lineTo(x + r * 0.12f, disco.top + r * 0.1f); sepalos.close()
        }
        drawPath(sepalos, a.luz(VERDE, k, br))
        drawOval(a.sombra(0.3f), disco.topLeft, disco.size, style = Stroke(0.6f * u))
    }
}

/**
 * La calabaza grande, tendida delante: anaranjada con sus gajos, las vetas verdes moteadas, el pedúnculo seco y retorcido,
 * la luz arriba a la izquierda y la sombra abajo a la derecha, y su sombra en la tabla.
 */
internal fun DrawScope.calabazaColonos(g: MarcoColonos, c: Offset, rx: Float, ry: Float) {
    val u = density; val a = g.a
    val k = g.k(c); val br = g.br(c)
    contactoColonos(Offset(c.x + rx * 0.1f, c.y + ry * 0.92f), rx * 2.2f, ry * 0.5f, a, 0.4f)
    val cuerpo = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
    drawOval(a.luz(CALABAZA, k, br), cuerpo.topLeft, cuerpo.size)
    clipPath(Path().apply { addOval(cuerpo) }) {
        // Los gajos: cada uno con su lomo de luz y su surco de sombra; las vetas verdes moteadas en los surcos.
        val surcos = Path(); val verdes = Path()
        for (i in 1..6) {
            val f = i / 7f
            val x = cuerpo.left + cuerpo.width * f
            val curva = (f - 0.5f) * rx * 0.5f
            surcos.moveTo(x, cuerpo.top); surcos.quadraticTo(x + curva, c.y, x, cuerpo.bottom)
            for (j in 0 until 4) {
                val y = cuerpo.top + cuerpo.height * (0.2f + 0.2f * j)
                verdes.addOval(Rect(x + curva * 0.6f - rx * 0.03f, y, x + curva * 0.6f + rx * 0.05f, y + ry * (0.18f + 0.1f * azar(7720 + i, j))))
            }
        }
        drawPath(verdes, a.luz(VERDE, k, br).copy(alpha = 0.6f))
        drawPath(surcos, a.sombra(0.3f), style = Stroke(1.3f * u))
        drawOval(a.brillo(0.3f, k, br), Offset(cuerpo.left + rx * 0.25f, cuerpo.top + ry * 0.15f), Size(rx * 0.7f, ry * 0.45f))
        drawOval(a.sombra(0.32f), Offset(c.x - rx * 0.1f, c.y + ry * 0.1f), Size(rx * 1.3f, ry * 1.1f))
    }
    // El pedúnculo seco, torcido.
    val ped = Path().apply {
        moveTo(c.x - rx * 0.06f, cuerpo.top + ry * 0.12f)
        cubicTo(c.x - rx * 0.1f, cuerpo.top - ry * 0.35f, c.x + rx * 0.12f, cuerpo.top - ry * 0.5f, c.x + rx * 0.22f, cuerpo.top - ry * 0.3f)
    }
    drawPath(ped, a.luz(mezcla(PINO_OSC, VERDE, 0.4f), k, br), style = Stroke(rx * 0.11f, cap = StrokeCap.Round))
    drawPath(ped, a.brillo(0.25f, k, br), style = Stroke(rx * 0.03f, cap = StrokeCap.Round))
    if (a.oscuro && k > 0.08f) drawArc(a.brillo(0.6f * k, k, br), -70f, 80f, false, cuerpo.topLeft, cuerpo.size, style = Stroke(1.4f * u))
}

/**
 * La cafetera de peltre azul moteada: el cuerpo que se abre hacia abajo, la tapa abombada con su perilla, el pico curvo, el
 * asa, los ribetes negros, las motas blancas del esmalte, los saltones negros donde se cayó y su brillo de esmalte.
 */
internal fun DrawScope.cafeteraColonos(g: MarcoColonos, pie: Offset) {
    val u = density; val w = g.w; val a = g.a
    val ab = w * 0.044f; val ar = w * 0.03f; val alto = w * 0.082f
    val top = pie.y - alto
    val cuerpo = poligonoColonos(pie.x - ar, top, pie.x + ar, top, pie.x + ab, pie.y, pie.x - ab, pie.y)
    val pico = Path().apply {
        moveTo(pie.x - ab * 0.82f, pie.y - alto * 0.32f)
        cubicTo(pie.x - ab * 1.3f, pie.y - alto * 0.45f, pie.x - ab * 1.35f, pie.y - alto * 0.85f, pie.x - ab * 1.62f, top - alto * 0.04f)
        lineTo(pie.x - ab * 1.45f, top + alto * 0.02f)
        cubicTo(pie.x - ab * 1.1f, pie.y - alto * 0.75f, pie.x - ab * 1.0f, pie.y - alto * 0.6f, pie.x - ar * 0.95f, pie.y - alto * 0.7f); close()
    }
    val asa = Path().apply {
        moveTo(pie.x + ar * 0.95f, top + alto * 0.15f)
        cubicTo(pie.x + ab * 1.7f, top + alto * 0.05f, pie.x + ab * 1.6f, pie.y - alto * 0.3f, pie.x + ab * 0.85f, pie.y - alto * 0.25f)
    }
    val tapa = Rect(pie.x - ar * 1.02f, top - alto * 0.16f, pie.x + ar * 1.02f, top + alto * 0.06f)
    sombraParedColonos(g, Path().apply { addPath(cuerpo); addPath(pico); addOval(tapa) }, Offset(pie.x, top), g.apoyoAtras)
    contactoColonos(Offset(pie.x, pie.y), ab * 2.6f, w * 0.012f, a, 0.36f)
    val c = Offset(pie.x, pie.y - alto * 0.5f)
    val k = g.k(c); val br = g.br(c)
    val esmalte = a.luz(PELTRE, k, br)
    drawPath(asa, a.luz(HIERRO, k, br), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
    drawPath(pico, esmalte)
    drawPath(cuerpo, esmalte)
    // Las motas blancas del esmalte, en un solo camino.
    val motas = Path()
    for (i in 0 until 40) {
        val fy = azar(7730, i); val fx = azar(7731, i) - 0.5f
        val y = top + alto * fy
        val anchoY = ar + (ab - ar) * fy
        val x = pie.x + fx * 2f * anchoY * 0.92f
        val s = w * (0.0015f + 0.0022f * azar(7732, i))
        motas.addOval(Rect(x - s, y - s, x + s, y + s))
    }
    drawPath(motas, a.luz(PaletaColonos.CRUDO, k, br).copy(alpha = 0.8f))
    // La luz del esmalte (a la izquierda de día; a la derecha de noche, hacia la bombilla) y la sombra del otro lado.
    clipPath(cuerpo) {
        val haciaDer = a.oscuro
        drawRect(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(if (haciaDer) pie.x + ar * 0.35f else pie.x - ab * 0.7f, top), Size(ab * 0.28f, alto))
        drawRect(a.sombra(0.3f), Offset(if (haciaDer) pie.x - ab else pie.x + ab * 0.35f, top), Size(ab * 0.65f, alto))
    }
    // Los ribetes negros, los saltones y la tapa con su perilla.
    drawRect(a.luz(HIERRO, k, br), Offset(pie.x - ab, pie.y - 1.6f * u), Size(ab * 2, 1.6f * u))
    drawCircle(a.luz(HIERRO, k, br), w * 0.004f, Offset(pie.x + ab * 0.5f, pie.y - alto * 0.3f))
    drawCircle(a.luz(HIERRO, k, br), w * 0.003f, Offset(pie.x - ar * 0.3f, top + alto * 0.2f))
    drawOval(esmalte, tapa.topLeft, tapa.size)
    drawOval(a.brillo(0.4f, maxOf(k, 0.3f), br), Offset(tapa.left + tapa.width * 0.15f, tapa.top + 1f), Size(tapa.width * 0.4f, tapa.height * 0.4f))
    drawOval(a.luz(HIERRO, k, br), tapa.topLeft, tapa.size, style = Stroke(1.2f * u))
    drawCircle(a.luz(HIERRO, k, br), w * 0.006f, Offset(pie.x, tapa.top - w * 0.003f))
    drawCircle(a.brillo(0.6f, maxOf(k, 0.3f), br), w * 0.002f, Offset(pie.x - w * 0.002f, tapa.top - w * 0.005f))
    drawPath(pico, a.luz(HIERRO, k, br), style = Stroke(0.7f * u))
    if (a.oscuro && k > 0.08f) drawLine(a.brillo(0.75f * k, k, br), Offset(pie.x + ar, top), Offset(pie.x + ab, pie.y), 1.3f * u)
}

/**
 * Las revistas viejas atadas: un mazo de lomos desteñidos (ladrillo, marino, salvia, maíz y crudo, sin letras), cada uno con
 * el canto de sus hojas, ladeados apenas, atados con dos vueltas de cordel y el nudo arriba.
 */
internal fun DrawScope.revistasColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val n = 6
    val colores = listOf(LADRILLO, MARINO, PaletaColonos.CRUDO, SALVIA, MAIZ, PaletaColonos.CRUDO)
    sombraParedColonos(g, Path().apply { addRect(r) }, r.center, g.apoyoAtras)
    contactoColonos(Offset(r.center.x, r.bottom), r.width * 1.1f, w * 0.012f, a, 0.34f)
    val esp = r.height / n
    for (i in 0 until n) {
        val y = r.bottom - esp * (i + 1)
        val corre = w * 0.006f * (azar(7740, i) - 0.5f)
        val lomo = Rect(r.left + corre, y, r.right + corre - w * 0.01f * azar(7741, i), y + esp)
        val p = lomo.center
        val k = g.k(p); val br = g.br(p)
        drawRect(a.luz(mezcla(colores[i], PaletaColonos.CRUDO, 0.3f), k, br), lomo.topLeft, lomo.size)
        // El canto de las hojas, a la derecha, y una franja de color del lomo (sin letras).
        drawRect(a.luz(PAPEL, k, br), Offset(lomo.right - w * 0.02f, lomo.top + 0.5f * u), Size(w * 0.02f, lomo.height - 1f * u))
        drawRect(a.luz(mezcla(colores[(i + 2) % n], LADRILLO, 0.2f), k, br).copy(alpha = 0.55f), Offset(lomo.left + lomo.width * (0.15f + 0.3f * azar(7742, i)), lomo.top + lomo.height * 0.3f), Size(lomo.width * 0.2f, lomo.height * 0.4f))
        drawRect(a.sombra(0.3f), Offset(lomo.left, lomo.bottom - 0.8f * u), Size(lomo.width, 0.8f * u))
        drawRect(a.brillo(0.3f, k, br), Offset(lomo.left, lomo.top), Size(lomo.width, 0.6f * u))
    }
    // El cordel: dos vueltas y el nudo encima.
    listOf(0.28f, 0.7f).forEach { f ->
        val x = r.left + r.width * f
        drawLine(a.sombra(0.35f), Offset(x + 1f * u, r.top), Offset(x + 1f * u, r.bottom), 1.4f * u)
        drawLine(g.c(CORDEL, Offset(x, r.center.y)), Offset(x, r.top - 1f * u), Offset(x, r.bottom), 1.4f * u)
    }
    val nudo = Offset(r.left + r.width * 0.28f, r.top - 1.5f * u)
    drawCircle(g.c(CORDEL, nudo), 2f * u, nudo)
    drawLine(g.c(CORDEL, nudo), nudo, nudo + Offset(-w * 0.012f, -w * 0.008f), 1.1f * u)
    drawLine(g.c(CORDEL, nudo), nudo, nudo + Offset(w * 0.014f, -w * 0.005f), 1.1f * u)
    if (a.oscuro) {
        val k = g.k(Offset(r.center.x, r.top))
        if (k > 0.08f) drawRect(a.brillo(0.7f * k, k), Offset(r.left, r.top), Size(r.width, 1.3f * u))
    }
}

/** El filamento de la bombilla: un resorte corto y tendido (una onda apretada) entre sus dos alambres. */
private fun filamentoColonos(x: Float, yb: Float, rb: Float): Path = Path().apply {
    moveTo(x - rb * 0.32f, yb - rb * 0.9f); lineTo(x - rb * 0.32f, yb)
    for (i in 0..12) {
        val f = i / 12f
        val px = x - rb * 0.32f + rb * 0.64f * f
        val py = yb - rb * 0.06f * kotlin.math.sin(f * 6.283f * 3f)
        lineTo(px, py)
    }
    lineTo(x + rb * 0.32f, yb - rb * 0.9f)
}
