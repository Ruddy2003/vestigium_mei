package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TURQUESA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TURQUESA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TURQUESA_OSC
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE

/*
 * La alacena de la cocina (Colonos): la cornisa con su faldón recortado, cada casillero con su hondura, los costados con su
 * media caña y lo que quedó de la puerta, las tablas de canto redondo, el rótulo de cada mes y el cuerpo bajo de cajones.
 */

/**
 * La pintura gastada de una pieza: rozaduras finas a lo largo de la veta, por donde asoma lo de debajo ([col]); la mitad
 * pegadas a los cantos, que es donde más roza. Un solo camino para todas.
 */
internal fun DrawScope.gastadoColonos(r: Rect, col: Color, semilla: Int, cuantas: Int, vertical: Boolean, alfa: Float = 0.7f) {
    val u = density
    val p = Path()
    for (k in 0 until cuantas) {
        val largo = (6f + 26f * azar(semilla, 3 * k)) * u
        val grosor = (0.8f + 2.2f * azar(semilla, 3 * k + 1)) * u
        val t = azar(semilla, 3 * k + 2)
        val b = azar(semilla + 7, k)
        val orilla = if (k % 2 == 0) (if (b < 0.5f) b * 0.22f else 1f - (1f - b) * 0.22f) else b
        val rr = if (vertical) Rect(r.left + r.width * orilla - grosor / 2, r.top + r.height * t - largo / 2, r.left + r.width * orilla + grosor / 2, r.top + r.height * t + largo / 2)
        else Rect(r.left + r.width * t - largo / 2, r.top + r.height * orilla - grosor / 2, r.left + r.width * t + largo / 2, r.top + r.height * orilla + grosor / 2)
        p.addRoundRect(androidx.compose.ui.geometry.RoundRect(rr, CornerRadius(grosor / 2)))
    }
    clipRect(r.left, r.top, r.right, r.bottom) { drawPath(p, col.copy(alpha = alfa)) }
}

// ---------------------------------------------------------------- la cornisa

/**
 * La cornisa de la alacena, de [g].apoyo a la corona: la tabla de encima volada (su cara de arriba, donde se apoyan las cosas,
 * y su canto de bocel), el cimacio en cuarto de caña bajo ella, el friso con su hilera de puntos de ladrillo pintados, el
 * filete y el faldón recortado en ondas, por cuyos huecos se ve lo alto del primer casillero. Con sus clavos, la pintura
 * gastada hasta el pino en los cantos y, de noche, el filo de la bombilla en lo que la mira.
 */
internal fun DrawScope.cornisaColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val ap = g.apoyo; val ch = g.ch
    val tapa = Rect(w * 0.003f, ap, w * 0.997f, ap + w * 0.024f)
    val cima = Rect(w * 0.011f, tapa.bottom, w * 0.989f, tapa.bottom + w * 0.022f)
    val friso = Rect(g.borde, cima.bottom, w - g.borde, cima.bottom + w * 0.017f)
    val filete = Rect(g.borde, friso.bottom, w - g.borde, friso.bottom + w * 0.005f)
    val faldon = Rect(g.borde, filete.bottom, w - g.borde, ch)
    val tramos = if (a.lumbre) 30 else 1
    // Lo alto del primer casillero, que se ve por las ondas del faldón: el turquesa en la sombra de la cornisa.
    drawRect(g.c(TURQUESA_OSC, Offset(w * 0.5f, ch), 0.6f), Offset(g.x0, faldon.top), Size(g.x1 - g.x0, faldon.height + 1f))
    drawRect(a.sombra(0.4f), Offset(g.x0, faldon.top), Size(g.x1 - g.x0, faldon.height + 1f))
    for (t in 0 until tramos) {
        val xa = tapa.left + tapa.width * t / tramos; val xb = tapa.left + tapa.width * (t + 1) / tramos + 0.6f
        val p = Offset((xa + xb) / 2, ap)
        drawRect(g.c(mezcla(MAIZ, MAIZ_LUZ, 0.45f), p), Offset(xa, g.apoyoAtras), Size(xb - xa, ap - g.apoyoAtras + 0.5f))
        drawRect(g.c(MAIZ, p), Offset(xa, tapa.top), Size(xb - xa, tapa.height))
        drawRect(g.c(MAIZ, p, 0.8f), Offset(xa, cima.top), Size(xb - xa, cima.height))
        drawRect(g.c(MAIZ, p, 0.9f), Offset(xa, friso.top), Size(xb - xa, friso.height + filete.height + faldon.height))
    }
    // La cara de arriba, con lo gastado donde se apoyan las cosas, y la sombra de la pared en su fondo.
    gastadoColonos(Rect(tapa.left, g.apoyoAtras, tapa.right, ap), g.c(PINO, Offset(w * 0.5f, ap)), 7201, 18, false, 0.5f)
    drawRect(a.sombra(0.22f), Offset(0f, g.apoyoAtras), Size(w, 1.6f * u))
    drawRect(a.claro(0.45f), Offset(tapa.left, ap - 1f * u), Size(tapa.width, 1f * u))
    // El canto de la tabla: el bocel, con su luz arriba y su sombra abajo, la veta y lo gastado.
    gastadoColonos(tapa, g.c(PINO, Offset(w * 0.5f, ap)), 7206, 16, false, 0.55f)
    vetas(tapa, a.sombra(0.2f), 7202, 0.7f, grosor = 0.5f, ondulacion = 0.4f)
    drawRect(a.claro(0.55f), Offset(tapa.left, tapa.top), Size(tapa.width, 1.3f * u))
    drawRect(a.claro(0.18f), Offset(tapa.left, tapa.top + 1.3f * u), Size(tapa.width, tapa.height * 0.32f))
    drawRect(a.sombra(0.26f), Offset(tapa.left, tapa.bottom - tapa.height * 0.34f), Size(tapa.width, tapa.height * 0.34f))
    drawRect(a.sombra(0.5f), Offset(tapa.left, tapa.bottom - 1.2f * u), Size(tapa.width, 1.2f * u))
    // Las testas de la tabla volada, a los lados.
    listOf(tapa.left, tapa.right - w * 0.008f).forEach { x -> drawRect(g.c(PINO_OSC, Offset(x, ap)), Offset(x, tapa.top), Size(w * 0.008f, tapa.height)) }
    // El cimacio: hondo bajo la tabla, se aclara hacia abajo, con su junquillo.
    drawRect(Brush.verticalGradient(listOf(a.sombra(0.62f), a.sombra(0.2f), a.claro(0.22f)), cima.top, cima.bottom), cima.topLeft, cima.size)
    drawRect(a.claro(0.4f), Offset(cima.left, cima.bottom - 2.4f * u), Size(cima.width, 1f * u))
    drawRect(a.sombra(0.3f), Offset(cima.left, cima.bottom - 1.2f * u), Size(cima.width, 1.2f * u))
    // El friso, con su hilera de puntos de ladrillo pintados a mano (gastados, alguno ya no está).
    val puntos = androidx.compose.ui.graphics.Path()
    val paso = w * 0.032f
    var x = friso.left + paso * 0.5f; var i = 0
    while (x < friso.right && i < 60) {
        if (azar(7207, i) > 0.12f) {
            val r = friso.height * (0.2f + 0.05f * azar(7208, i))
            puntos.addOval(Rect(x - r, friso.center.y - r, x + r, friso.center.y + r))
        }
        x += paso; i++
    }
    drawPath(puntos, g.c(LADRILLO, friso.center).copy(alpha = 0.8f))
    gastadoColonos(friso, g.c(MAIZ, friso.center), 7209, 10, false, 0.55f)
    drawRect(a.sombra(0.25f), Offset(friso.left, friso.bottom - 0.8f * u), Size(friso.width, 0.8f * u))
    // El filete pintado de ladrillo.
    drawRect(g.c(LADRILLO, Offset(w * 0.5f, filete.top)), filete.topLeft, filete.size)
    // El faldón recortado: ondas entre los costados, por donde asoma lo de dentro.
    val ondas = 5
    val ancho = (g.x1 - g.x0) / ondas
    val hondo = faldon.height * 0.62f
    val recorte = Path().apply {
        moveTo(g.x0, ch + 1f)
        for (k in 0 until ondas) {
            val xa = g.x0 + ancho * k
            lineTo(xa, ch - hondo * 0.12f)
            cubicTo(xa + ancho * 0.08f, ch - hondo * 1.08f, xa + ancho * 0.92f, ch - hondo * 1.08f, xa + ancho, ch - hondo * 0.12f)
        }
        lineTo(g.x1, ch + 1f); close()
    }
    clipPath(recorte) {
        drawRect(g.c(TURQUESA_OSC, Offset(w * 0.5f, ch), 0.6f), Offset(g.x0, faldon.top), Size(g.x1 - g.x0, faldon.height + 1f))
        drawRect(a.sombra(0.35f), Offset(g.x0, faldon.top), Size(g.x1 - g.x0, faldon.height + 1f))
        // Las tablillas del fondo, que siguen por arriba.
        val tab = Path()
        var xt = g.x0 + w * 0.026f + w * 0.058f * 0.4f
        while (xt < g.x1) { tab.moveTo(xt, faldon.top); tab.lineTo(xt, ch + 1f); xt += w * 0.058f }
        drawPath(tab, a.sombra(0.4f), style = Stroke(1.1f * u))
    }
    // El grueso del recorte: el canto de la tabla del faldón, más oscuro, y su luz abajo.
    clipRect(g.x0, faldon.top, g.x1, ch) {
        drawPath(corridoColonos(recorte, 0f, -1.6f * u), g.c(MAIZ_OSC, Offset(w * 0.5f, ch)), style = Stroke(2.2f * u))
        drawPath(recorte, a.claro(0.35f), style = Stroke(0.8f * u))
    }
    gastadoColonos(faldon, g.c(PINO, Offset(w * 0.5f, ch)), 7203, 14, false, 0.5f)
    vetas(faldon, a.sombra(0.14f), 7204, 0.6f, grosor = 0.5f, ondulacion = 0.3f)
    // El filo de la bombilla en el canto de la tabla, de noche.
    if (a.lumbre) for (t in 0 until 10) {
        val xa = tapa.left + tapa.width * t / 10; val p = Offset(xa + tapa.width / 20, tapa.top)
        val k = g.k(p)
        if (k > 0.08f) drawRect(a.brillo(0.75f * k, k, g.br(p)), Offset(xa, tapa.top), Size(tapa.width / 10 + 0.6f, 1.3f * u))
    }
    // Los clavos de la tabla de encima.
    listOf(0.03f, 0.35f, 0.65f, 0.97f).forEach { fx -> clavoColonos(Offset(w * fx, tapa.top + tapa.height * 0.5f), 1.2f * u, g) }
}

// ---------------------------------------------------------------- los casilleros

/**
 * Un casillero con su hondura: al fondo, las tablillas machihembradas pintadas de turquesa (con su media caña, los saltones de
 * la pintura por donde asoma el maíz de antes y los roces); arriba, la tabla de encima vista por debajo; a los lados, las caras
 * de dentro de los costados (de día, la de la derecha con la luz de la ventana); la cara de la tabla donde pisan los volúmenes;
 * las sombras de la tabla de arriba y del costado; de noche, cada tramo con la luz que le llega.
 */
internal fun DrawScope.casilleroColonos(k: Int, r: Rect, oc: Float, g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val x0 = g.x0; val x1 = g.x1
    if (r.height < 2f || x1 - x0 < 2f) return
    val dI = w * 0.026f; val dT = w * 0.017f; val dB = w * 0.012f
    val fondo = Rect(x0 + dI, r.top + dT, x1 - dI, r.bottom - dB)
    // La junta con la tabla de arriba (puede caer a medio píxel): esa fila, del color de la tabla vista por debajo.
    if (k > 0) filaJuntaColonos(r.top, x0, x1, g.c(MAIZ_OSC, Offset(w * 0.5f, r.top), 0.5f))
    clipRect(x0, r.top, x1, r.bottom) {
        // Debajo de todo, el hueco entero del color de la tabla: las piezas de dentro se tocan en bordes a medio píxel.
        drawRect(g.c(MAIZ_OSC, r.center, 0.6f), r.topLeft, r.size)
        // El fondo turquesa: de noche, cada tablilla con la luz que le llega (las juntas esconden el salto de una a otra).
        val paso = w * 0.058f
        val x00 = fondo.left + paso * (0.4f + 0.3f * azar(7300, k))
        if (!a.lumbre) drawRect(g.c(TURQUESA, r.center, 0.85f), fondo.topLeft, fondo.size)
        else {
            var xa = fondo.left; var xb = x00; var n = 0
            while (xa < fondo.right && n < 40) {
                val p = Offset((xa + xb) / 2, r.center.y)
                drawRect(g.c(TURQUESA, p, 0.85f), Offset(xa, fondo.top), Size(minOf(xb, fondo.right) - xa + 0.6f, fondo.height))
                xa = xb; xb += paso; n++
            }
        }
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.sombra(0.2f), Color.Transparent, a.sombra(0.08f)), fondo.left, fondo.right), fondo.topLeft, fondo.size)
        // Las tablillas: la media caña de cada junta (la sombra y la luz) en dos caminos.
        val juntas = Path(); val luces = Path()
        var x = x00
        while (x < fondo.right - 2f * u) {
            juntas.moveTo(x, fondo.top); juntas.lineTo(x, fondo.bottom)
            luces.moveTo(x + 1.3f * u, fondo.top); luces.lineTo(x + 1.3f * u, fondo.bottom)
            x += paso
        }
        val kf = g.k(fondo.center)
        drawPath(juntas, a.sombra(0.42f), style = Stroke(1.1f * u))
        drawPath(luces, a.brillo(0.22f, kf, g.br(fondo.center)), style = Stroke(0.8f * u))
        // Los saltones de la pintura: el maíz de antes asoma por debajo del turquesa; y los roces abajo, donde se arrastran.
        val saltones = Path()
        for (i in 0 until 4) {
            // Junto a una junta de las tablillas, que es donde salta la pintura.
            val c = Offset(fondo.left + paso * (1f + (fondo.width / paso - 2f) * azar(7301 + k, i)).toInt().toFloat() + paso * 0.4f + 1.5f * u,
                fondo.top + fondo.height * (0.3f + 0.6f * azar(7302 + k, i)))
            val s = w * (0.003f + 0.005f * azar(7303 + k, i))
            saltones.addPath(poligonoColonos(c.x, c.y - s, c.x + s * 1.4f, c.y - s * 0.3f, c.x + s * 0.8f, c.y + s * 1.6f, c.x - s * 0.2f, c.y + s * 0.6f))
        }
        drawPath(saltones, g.c(mezcla(MAIZ, PINO, 0.4f), fondo.center, 0.8f).copy(alpha = 0.6f))
        gastadoColonos(Rect(fondo.left, fondo.bottom - fondo.height * 0.3f, fondo.right, fondo.bottom), g.c(TURQUESA_LUZ, fondo.center, 0.8f), 7304 + k, 9, false, 0.35f)
        // De día, la luz de la ventana entra de arriba a la izquierda, pasada la sombra del costado.
        if (!a.oscuro) {
            val l = fondo.left + 12f * u
            drawPath(poligonoColonos(l, fondo.top, l + fondo.width * 0.5f, fondo.top, l + fondo.width * 0.22f, fondo.bottom, l, fondo.bottom), LUZ_MUEBLE.copy(alpha = if (a.noche) 0.04f else 0.1f))
        }
        // La tabla de arriba vista por debajo y las caras de dentro de los costados.
        val kT = g.k(Offset(r.center.x, r.top))
        drawPath(poligonoColonos(x0, r.top, x1, r.top, x1 - dI, r.top + dT, x0 + dI, r.top + dT), a.luz(MAIZ_OSC, kT * 0.5f, g.br(Offset(r.center.x, r.top))))
        val pI = Offset(x0, r.center.y); val pD = Offset(x1, r.center.y)
        drawPath(poligonoColonos(x0, r.top, x0 + dI, r.top + dT, x0 + dI, r.bottom - dB, x0, r.bottom), a.luz(mezcla(MAIZ, MAIZ_OSC, 0.65f), g.k(pI) * 0.6f, g.br(pI)))
        drawPath(poligonoColonos(x1, r.top, x1 - dI, r.top + dT, x1 - dI, r.bottom - dB, x1, r.bottom), if (a.oscuro) g.c(MAIZ, pD) else a.luz(mezcla(MAIZ, MAIZ_LUZ, 0.3f), g.k(pD)))
        // La cara de la tabla donde pisan los volúmenes: clara, con la sombra del fondo.
        val pB = Offset(r.center.x, r.bottom)
        drawPath(poligonoColonos(x0, r.bottom, x0 + dI, r.bottom - dB, x1 - dI, r.bottom - dB, x1, r.bottom), g.c(mezcla(MAIZ, MAIZ_LUZ, 0.35f), pB))
        gastadoColonos(Rect(x0 + dI, r.bottom - dB, x1 - dI, r.bottom), g.c(PINO, pB), 7305 + k, 8, false, 0.6f)
        drawLine(a.sombra(0.45f), Offset(x0 + dI, r.bottom - dB), Offset(x1 - dI, r.bottom - dB), 1f * u)
        drawLine(a.sombra(0.4f), Offset(x0 + dI, r.top + dT), Offset(x0 + dI, r.bottom - dB), 0.8f * u)
        drawLine(a.sombra(0.3f), Offset(x1 - dI, r.top + dT), Offset(x1 - dI, r.bottom - dB), 0.8f * u)
        drawLine(a.sombra(0.5f), Offset(x0 + dI, r.top + dT), Offset(x1 - dI, r.top + dT), 0.8f * u)
        // La sombra de la tabla de arriba en el fondo: honda junto a ella y deshecha hacia abajo.
        val fz = if (a.oscuro) 1.15f else 1f
        drawRect(a.sombra(0.42f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 5f * u))
        drawRect(a.sombra(0.22f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 14f * u))
        drawRect(a.sombra(0.1f * fz), Offset(fondo.left, fondo.top), Size(fondo.width, 26f * u))
        // El costado de la izquierda le quita la luz a ese lado de día; de noche, la sombra cae del lado contrario a la bombilla.
        if (!a.oscuro) drawRect(a.sombra(0.26f), Offset(fondo.left, fondo.top), Size(10f * u, fondo.height))
        else drawRect(a.sombra(0.2f), Offset(fondo.left, fondo.top), Size(fondo.width * 0.25f, fondo.height))
        // La sombra que el último volumen echa en el fondo, a su derecha (de día).
        if (oc > r.left + 6f * u && !a.oscuro) {
            val largo = 12f * u
            drawPath(poligonoColonos(oc - 2f * u, r.bottom - dB, oc - 2f * u, r.top + r.height * 0.24f, oc + largo, r.top + r.height * 0.34f, oc + largo, r.bottom - dB), a.sombra(0.24f))
        }
        // De noche, el charco de la bombilla donde llega (de la brasa, en los de abajo).
        if (a.oscuro) {
            val kc = g.k(r.center)
            if (kc > 0.12f) {
                val l = Offset(w * MuebleEstanteColonos.LAMP_X, w * MuebleEstanteColonos.LAMP_Y)
                val haciaL = Offset(fondo.right, fondo.top)
                mancha(if (g.br(r.center) > 0.5f) Offset(w * g.brasa.x, w * g.brasa.y) else Offset(minOf(haciaL.x, l.x), fondo.top), w * 0.45f,
                    a.calor(g.br(r.center)).copy(alpha = 0.16f * kc))
            }
        }
    }
}

// ---------------------------------------------------------------- los costados

/**
 * Los dos costados de la alacena, de la corona al cuerpo bajo: el amarillo maíz gastado hasta el pino en los cantos y donde se
 * agarra, la media caña de su canto de dentro, la veta, los clavos; en el de la izquierda, las bisagras de la puerta de vidrio
 * que ya no está (una todavía con sus tornillos; de la otra, solo la huella sin pintar y los agujeros); en el de la derecha,
 * la taravilla de latón que la cerraba. De día, el filo de luz a la izquierda; de noche, por tramos según la luz.
 */
internal fun DrawScope.costadosColonos(g: MarcoColonos, tablas: List<Rect>) {
    val u = density; val w = g.w; val a = g.a
    val top = g.ch; val bot = g.fondo
    if (bot - top < 2f) return
    listOf(true, false).forEach { izq ->
        // Un píxel más hacia dentro, encima del canto del casillero: así no queda junta a medio píxel.
        val r = if (izq) Rect(g.borde, top, g.x0 + 1f, bot) else Rect(g.x1 - 1f, top, w - g.borde, bot)
        val tramos = maxOf(1, ((bot - top) / (w * 0.05f)).toInt()).coerceAtMost(80)
        for (t in 0 until tramos) {
            val y0 = top + (bot - top) * t / tramos; val y1 = top + (bot - top) * (t + 1) / tramos + 0.6f
            drawRect(g.c(MAIZ, Offset(r.center.x, (y0 + y1) / 2)), Offset(r.left, y0), Size(r.width, y1 - y0))
        }
        val kc = g.k(r.center)
        gastadoColonos(r, a.luz(PINO, kc, g.br(r.center)), if (izq) 7401 else 7402, ((bot - top) / (w * 0.035f)).toInt().coerceIn(8, 80), true, 0.7f)
        vetas(r, a.sombra(0.26f), if (izq) 7403 else 7404, 1.2f, vertical = true, grosor = 0.5f, ondulacion = 0.6f)
        // Cada costado son dos tablas: la junta, con su sombra y su luz.
        val xj = r.left + r.width * (if (izq) 0.42f else 0.58f)
        drawLine(a.sombra(0.35f), Offset(xj, top), Offset(xj, bot), 0.9f * u)
        drawLine(a.claro(0.25f), Offset(xj + 0.9f * u, top), Offset(xj + 0.9f * u, bot), 0.6f * u)
        // La media caña del canto de dentro: su sombra y su luz.
        val xm = if (izq) r.right - r.width * 0.26f else r.left + r.width * 0.26f
        drawLine(a.sombra(0.4f), Offset(xm, top), Offset(xm, bot), 1.1f * u)
        drawLine(a.brillo(0.3f, kc, g.br(r.center)), Offset(xm + (if (izq) -1.2f else 1.2f) * u, top), Offset(xm + (if (izq) -1.2f else 1.2f) * u, bot), 0.8f * u)
        // El filo de luz de la ventana en el canto de la izquierda (de día) y la sombra del de la derecha.
        if (izq) drawRect(a.claro(0.45f), Offset(r.left, top), Size(1.3f * u, bot - top))
        else drawRect(a.sombra(0.3f), Offset(r.right - r.width * 0.16f, top), Size(r.width * 0.16f, bot - top))
        // De noche, el filo de la bombilla en el canto que la mira (el de dentro del costado izquierdo, el de fuera del derecho).
        if (a.lumbre) {
            val xf = if (izq) r.right - 1.4f * u else r.left
            for (t in 0 until tramos) {
                val y0 = top + (bot - top) * t / tramos
                val p = Offset(xf, y0 + (bot - top) / tramos / 2)
                val kf = g.k(p)
                if (kf > 0.1f) drawRect(a.brillo(0.6f * kf, kf, g.br(p)), Offset(xf, y0), Size(1.4f * u, (bot - top) / tramos + 0.6f))
            }
        }
    }
    // Las bisagras de la puerta que ya no está, en el costado izquierdo, y la taravilla en el derecho.
    val alto = bot - top
    if (alto > w * 0.3f) {
        bisagraColonos(g, Offset(g.x0 - (g.x0 - g.borde) * 0.4f, top + minOf(alto * 0.12f, w * 0.12f)), true)
        bisagraColonos(g, Offset(g.x0 - (g.x0 - g.borde) * 0.4f, bot - minOf(alto * 0.12f, w * 0.12f)), false)
        taravillaColonos(g, Offset(g.x1 + (w - g.borde - g.x1) * 0.42f, top + alto * 0.5f))
    }
    // El remiendo de lata clavado en el costado izquierdo, abajo, y el trapo colgado de un clavo en el derecho.
    if (alto > w * 0.4f) {
        remiendoCostadoColonos(g, Rect(g.borde + w * 0.006f, bot - minOf(alto * 0.34f, w * 0.34f), g.x0 + w * 0.003f, bot - minOf(alto * 0.34f, w * 0.34f) + w * 0.075f))
        trapoColonos(g, Offset(g.x1 + (w - g.borde - g.x1) * 0.5f, top + minOf(alto * 0.2f, w * 0.26f)))
    }
    // Los clavos que sujetan cada tabla, dos por lado.
    tablas.forEach { t ->
        listOf(g.borde + (g.x0 - g.borde) * 0.55f, g.x1 + (w - g.borde - g.x1) * 0.45f).forEach { x ->
            clavoColonos(Offset(x, t.top + t.height * 0.5f), 1.1f * u, g)
        }
    }
}

/** Un remiendo de lata en [r]: cortado de una lata de antes, con la franja de pintura que le queda, sus clavos y el óxido. */
private fun DrawScope.remiendoCostadoColonos(g: MarcoColonos, r: Rect) {
    val u = density; val w = g.w; val a = g.a
    val c = r.center
    val k = g.k(c); val br = g.br(c)
    val p = poligonoColonos(r.left, r.top + w * 0.004f, r.right, r.top, r.right - w * 0.002f, r.bottom, r.left + w * 0.003f, r.bottom - w * 0.003f)
    drawPath(corridoColonos(p, 1.2f * u, 1.6f * u), a.sombra(0.4f))
    drawPath(p, a.luz(LATA, k, br))
    clipPath(p) {
        drawRect(a.luz(LADRILLO, k, br).copy(alpha = 0.8f), Offset(r.left, r.top + r.height * 0.38f), Size(r.width, r.height * 0.22f))
        drawRect(a.luz(PaletaColonos.CRUDO, k, br).copy(alpha = 0.6f), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.05f))
        drawRect(a.luz(OXIDO, k, br).copy(alpha = 0.5f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
        drawRect(a.claro(0.3f), Offset(r.left, r.top), Size(r.width * 0.25f, r.height))
    }
    drawPath(p, a.sombra(0.35f), style = Stroke(0.6f * u))
    listOf(Offset(r.left + w * 0.006f, r.top + w * 0.007f), Offset(r.right - w * 0.006f, r.top + w * 0.006f), Offset(r.left + w * 0.007f, r.bottom - w * 0.008f),
        Offset(r.right - w * 0.006f, r.bottom - w * 0.007f), Offset(c.x, r.top + w * 0.005f)).forEach { q -> clavoColonos(q, 1f * u, g) }
}

/**
 * El trapo de cocina colgado de un clavo en el costado derecho: lienzo crudo con dos rayas de ladrillo, doblado por el clavo y
 * cayendo en dos paños desiguales, con sus pliegues, su sombra en la madera y el canto deshilachado.
 */
private fun DrawScope.trapoColonos(g: MarcoColonos, clavo: Offset) {
    val u = density; val w = g.w; val a = g.a
    val an = w * 0.052f
    val largo = w * 0.15f
    val k = g.k(clavo); val br = g.br(clavo)
    val pano = Path().apply {
        moveTo(clavo.x - an * 0.35f, clavo.y)
        cubicTo(clavo.x - an * 0.6f, clavo.y + largo * 0.3f, clavo.x - an * 0.55f, clavo.y + largo * 0.7f, clavo.x - an * 0.5f, clavo.y + largo)
        lineTo(clavo.x + an * 0.42f, clavo.y + largo * 0.9f)
        cubicTo(clavo.x + an * 0.5f, clavo.y + largo * 0.6f, clavo.x + an * 0.55f, clavo.y + largo * 0.3f, clavo.x + an * 0.35f, clavo.y)
        close()
    }
    drawPath(corridoColonos(pano, if (a.oscuro) -w * 0.008f else w * 0.01f, w * 0.008f), a.sombra(0.35f))
    drawPath(pano, a.luz(PaletaColonos.CRUDO, k, br))
    clipPath(pano) {
        listOf(0.55f, 0.63f).forEach { f -> drawRect(a.luz(LADRILLO, k, br), Offset(clavo.x - an, clavo.y + largo * f), Size(an * 2, largo * 0.045f)) }
        // Los pliegues: el doblez del clavo y dos caídas.
        val pl = Path().apply {
            moveTo(clavo.x - an * 0.05f, clavo.y + largo * 0.05f); cubicTo(clavo.x - an * 0.15f, clavo.y + largo * 0.4f, clavo.x + an * 0.05f, clavo.y + largo * 0.7f, clavo.x - an * 0.05f, clavo.y + largo)
            moveTo(clavo.x + an * 0.2f, clavo.y + largo * 0.1f); cubicTo(clavo.x + an * 0.3f, clavo.y + largo * 0.4f, clavo.x + an * 0.15f, clavo.y + largo * 0.7f, clavo.x + an * 0.25f, clavo.y + largo)
        }
        drawPath(pl, a.sombra(0.28f), style = Stroke(1.6f * u))
        drawRect(a.sombra(0.2f), Offset(clavo.x + an * 0.15f, clavo.y), Size(an, largo))
        drawRect(a.claro(0.25f), Offset(clavo.x - an * 0.6f, clavo.y), Size(an * 0.2f, largo))
    }
    // El canto deshilachado de abajo.
    val hilos = Path()
    for (i in 0 until 7) {
        val f = i / 6f
        val x = clavo.x - an * 0.5f + an * 0.92f * f; val y = clavo.y + largo - largo * 0.1f * f
        hilos.moveTo(x, y); hilos.lineTo(x + 0.3f * u, y + 1.8f * u)
    }
    drawPath(hilos, a.luz(PaletaColonos.CRUDO, k, br), style = Stroke(0.5f * u))
    clavoColonos(clavo, 1.4f * u, g)
}

/** Una bisagra de pala de hierro con sus tres tornillos ([entera]), o solo la huella sin pintar y los agujeros. */
private fun DrawScope.bisagraColonos(g: MarcoColonos, c: Offset, entera: Boolean) {
    val u = density; val w = g.w; val a = g.a
    val r = Rect(c.x - w * 0.009f, c.y - w * 0.032f, c.x + w * 0.009f, c.y + w * 0.032f)
    // La huella: donde la pala tapaba, la pintura quedó más viva.
    drawRect(g.c(mezcla(MAIZ, MAIZ_LUZ, 0.5f), c), Offset(r.left - 1f * u, r.top - 1f * u), Size(r.width + 2f * u, r.height + 2f * u))
    if (entera) {
        drawRect(a.sombra(0.4f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
        drawRect(g.c(HIERRO, c), r.topLeft, r.size)
        drawRect(g.c(OXIDO, c).copy(alpha = 0.45f), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(a.brillo(0.35f, g.k(c), g.br(c)), r.topLeft, Size(1f * u, r.height))
        // El nudillo (el canuto del eje), en el canto de dentro.
        drawRect(g.c(HIERRO_LUZ, c), Offset(r.right - 1.6f * u, r.top - 1.5f * u), Size(2.6f * u, r.height + 3f * u))
        listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawCircle(g.c(LATA_OSC, c), 1f * u, Offset(r.center.x - 0.8f * u, r.top + r.height * f)) }
    } else {
        listOf(0.2f, 0.5f, 0.8f).forEach { f -> drawCircle(a.sombra(0.6f), 0.9f * u, Offset(r.center.x, r.top + r.height * f)) }
    }
}

/** La taravilla de latón que cerraba la puerta: la chapa, el tornillo y la lengüeta girada. */
private fun DrawScope.taravillaColonos(g: MarcoColonos, c: Offset) {
    val u = density; val w = g.w; val a = g.a
    val k = g.k(c)
    val lengua = poligonoColonos(c.x - w * 0.004f, c.y - w * 0.002f, c.x + w * 0.004f, c.y - w * 0.004f, c.x + w * 0.006f, c.y + w * 0.024f, c.x - w * 0.002f, c.y + w * 0.024f)
    drawPath(corridoColonos(lengua, 1f * u, 1.3f * u), a.sombra(0.4f))
    drawPath(lengua, g.c(PaletaColonos.CORDEL, c))
    drawPath(lengua, a.brillo(0.3f, k), style = Stroke(0.6f * u))
    drawCircle(g.c(LATA_OSC, c), 1.3f * u, c)
}

// ---------------------------------------------------------------- las tablas

/**
 * El canto de una tabla, grueso y de bocel: el maíz gastado en medio (donde se apoya la mano) y en las esquinas, la luz arriba
 * y la sombra abajo, la veta y su sombra en el casillero de debajo. La última es la encimera: vuela más sobre los costados.
 */
internal fun DrawScope.tablaColonos(k: Int, r: Rect, ultima: Boolean, g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    if (r.height < 1f) return
    val t = if (ultima) Rect(w * 0.006f, r.top, w * 0.994f, r.bottom) else Rect(g.x0 - 1f, r.top, g.x1 + 1f, r.bottom)
    // La segunda tabla se rompió y se repuso con una de pino sin pintar, más clara y con su veta a la vista.
    val repuesta = k == 1 && !ultima
    val tramos = if (a.lumbre) 18 else 1
    for (i in 0 until tramos) {
        val xa = t.left + t.width * i / tramos; val xb = t.left + t.width * (i + 1) / tramos + 0.6f
        drawRect(g.c(if (repuesta) PINO else MAIZ, Offset((xa + xb) / 2, t.center.y)), Offset(xa, t.top), Size(xb - xa, t.height))
    }
    val kc = g.k(t.center)
    if (repuesta) {
        vetas(t, g.c(PINO_OSC, t.center).copy(alpha = 0.6f), 7560, 1.4f, grosor = 0.6f, ondulacion = 0.8f)
        // Un nudo de la madera y la pintura que alguien probó y no siguió.
        drawOval(g.c(PINO_OSC, t.center), Offset(t.left + t.width * 0.63f, t.top + t.height * 0.3f), Size(t.height * 0.5f, t.height * 0.32f))
        drawRect(g.c(MAIZ, t.center).copy(alpha = 0.75f), Offset(t.left, t.top), Size(t.width * 0.08f, t.height))
    }
    // Lo gastado: el centro del canto, de tanto apoyar la mano, y alguna esquina.
    if (!repuesta) {
        gastadoColonos(Rect(t.left + t.width * 0.3f, t.top, t.right - t.width * 0.3f, t.bottom), a.luz(PINO, kc, g.br(t.center)), 7500 + k, 10, false, 0.65f)
        gastadoColonos(t, a.luz(PINO, kc, g.br(t.center)), 7520 + k, 6, false, 0.5f)
    }
    vetas(t, a.sombra(0.2f), 7540 + k, 0.6f, grosor = 0.5f, ondulacion = 0.4f)
    // El bocel: luz arriba y sombra abajo; el filo de la bombilla de noche, donde le llega.
    drawRect(a.claro(0.5f), Offset(t.left, t.top), Size(t.width, 1.2f * u))
    drawRect(a.claro(0.16f), Offset(t.left, t.top + 1.2f * u), Size(t.width, t.height * 0.32f))
    drawRect(a.sombra(0.3f), Offset(t.left, t.bottom - t.height * 0.3f), Size(t.width, t.height * 0.3f))
    drawRect(a.sombra(0.5f), Offset(t.left, t.bottom - 1.2f * u), Size(t.width, 1.2f * u))
    if (a.lumbre) for (i in 0 until tramos) {
        val xa = t.left + t.width * i / tramos; val p = Offset(xa + t.width / tramos / 2, t.top)
        val kf = g.k(p)
        if (kf > 0.08f) drawRect(a.brillo(0.7f * kf, kf, g.br(p)), Offset(xa, t.top), Size(t.width / tramos + 0.6f, 1.3f * u))
    }
    if (ultima) {
        // Los cantos de la encimera que vuelan sobre los costados: la testa, con su veta en anillos.
        listOf(t.left to true, t.right to false).forEach { (x, izq) ->
            val e = Rect(if (izq) x else x - w * 0.006f, t.top, if (izq) x + w * 0.006f else x, t.bottom)
            drawRect(g.c(PINO_OSC, e.center), e.topLeft, e.size)
        }
    }
}

/**
 * El rótulo del mes: una tira de hojalata cortada de una lata, clavada al canto con dos clavitos, con el mes escrito con pincel
 * fino; el borde cortado a tijera, el brillo del metal y el filete que le queda de la pintura de la lata.
 */
internal fun DrawScope.rotuloColonos(p: PlacaEstante, medidor: TextMeasurer, g: MarcoColonos) {
    val u = density; val a = g.a
    val r = p.r
    if (r.width < 4f || r.height < 4f) return
    val k = g.k(r.center); val br = g.br(r.center)
    val tira = poligonoColonos(r.left, r.top + 0.6f * u, r.right - 0.8f * u, r.top, r.right, r.bottom - 0.5f * u, r.left + 0.7f * u, r.bottom)
    drawPath(corridoColonos(tira, 1.2f * u, 1.6f * u), a.sombra(0.45f))
    val hoja = a.etiqueta(k, mezcla(LATA, PaletaColonos.PAPEL, 0.55f), br)
    drawPath(tira, hoja)
    drawRect(a.luz(LADRILLO, maxOf(k, 0.4f), br).copy(alpha = 0.7f), Offset(r.left + 1f * u, r.bottom - 2.2f * u), Size(r.width - 2f * u, 1.2f * u))
    drawRect(Color.White.copy(alpha = if (a.oscuro) 0.12f * k else 0.35f), Offset(r.left + 2f * u, r.top + 1f * u), Size(r.width * 0.55f, 0.9f * u))
    drawPath(tira, a.sombra(0.3f), style = Stroke(0.6f * u))
    textoEn(medidor, p.texto, Rect(r.left + r.height * 0.6f, r.top, r.right - r.height * 0.6f, r.bottom - 1.5f * u), PaletaColonos.LETRA, a.tinta, 11.5f)
    listOf(r.left + r.height * 0.3f, r.right - r.height * 0.3f).forEach { x -> clavoColonos(Offset(x, r.center.y), 1.1f * u, g, LATA_OSC) }
}

// ---------------------------------------------------------------- el cuerpo bajo

/**
 * El cuerpo bajo de la alacena, bajo la encimera: dos cajones con su hueco, sus perillas de madera torneada y la mugre de la
 * mano alrededor (el de la derecha, un poco salido); el faldón recortado en arco y las patas cuadradas, con el oscuro de
 * debajo y la sombra en el piso.
 */
internal fun DrawScope.cuerpoBajoColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val top = g.fondo; val cj = g.cajones; val fd = g.faldon; val ps = g.pisan
    if (cj - top < 3f * u) return
    val x0 = g.borde; val x1 = w - g.borde
    // El oscuro bajo el mueble, entre las patas, y su sombra en el piso.
    drawRect(a.sombra(0.55f), Offset(x0, fd), Size(x1 - x0, ps - fd))
    drawRect(a.sombra(0.3f), Offset(x0 - w * 0.01f, ps - 1.5f * u), Size(x1 - x0 + w * 0.03f, w * 0.014f))
    // La caja de los cajones y el faldón.
    val tramos = if (a.lumbre) 16 else 1
    for (i in 0 until tramos) {
        val xa = x0 + (x1 - x0) * i / tramos; val xb = x0 + (x1 - x0) * (i + 1) / tramos + 0.6f
        drawRect(g.c(MAIZ, Offset((xa + xb) / 2, (top + fd) / 2)), Offset(xa, top), Size(xb - xa, fd - top))
    }
    // El faldón recortado: un arco ancho en medio y dos chicos a los lados.
    val arco = Path().apply {
        moveTo(x0 + w * 0.06f, fd + 0.5f)
        cubicTo(x0 + w * 0.09f, cj + (fd - cj) * 0.1f, x1 - w * 0.09f, cj + (fd - cj) * 0.1f, x1 - w * 0.06f, fd + 0.5f); close()
    }
    drawPath(arco, a.sombra(0.6f))
    drawPath(arco, g.c(MAIZ_OSC, Offset(w * 0.5f, fd)), style = Stroke(1.3f * u))
    // Las patas, cuadradas y un poco ahusadas.
    listOf(x0, x1 - w * 0.06f).forEachIndexed { i, x ->
        val p = poligonoColonos(x, fd - 1f, x + w * 0.06f, fd - 1f, x + w * (if (i == 0) 0.052f else 0.06f), ps, x + w * (if (i == 0) 0f else 0.008f), ps)
        drawPath(p, g.c(MAIZ, Offset(x, ps)))
        drawRect(a.sombra(0.3f), Offset(x + w * 0.04f, fd), Size(w * 0.02f, ps - fd))
        gastadoColonos(Rect(x, fd, x + w * 0.06f, ps), g.c(PINO, Offset(x, ps)), 7600 + i, 4, true, 0.7f)
    }
    vetas(Rect(x0, top, x1, fd), a.sombra(0.16f), 7601, 0.6f, grosor = 0.5f, ondulacion = 0.3f)
    gastadoColonos(Rect(x0, top, x1, fd), g.c(PINO, Offset(w * 0.5f, top)), 7602, 18, false, 0.55f)
    // Los dos cajones: el hueco alrededor, el frente (el de la derecha, salido) y la perilla con su mugre.
    val medio = w * 0.5f
    listOf(Rect(x0 + w * 0.03f, top + w * 0.012f, medio - w * 0.008f, cj - w * 0.008f), Rect(medio + w * 0.008f, top + w * 0.012f, x1 - w * 0.03f, cj - w * 0.008f)).forEachIndexed { i, c ->
        val salido = i == 1
        drawRect(a.sombra(if (salido) 0.65f else 0.5f), Offset(c.left - 1.2f * u, c.top - 1.2f * u), Size(c.width + 2.4f * u, c.height + 2.4f * u))
        val f = if (salido) Rect(c.left - 1f * u, c.top + 1.5f * u, c.right + 1f * u, c.bottom + 1.8f * u) else c
        drawRect(g.c(mezcla(MAIZ, MAIZ_LUZ, 0.15f), f.center), f.topLeft, f.size)
        drawRect(a.claro(0.4f), f.topLeft, Size(f.width, 1f * u))
        drawRect(a.sombra(0.25f), Offset(f.left, f.bottom - 1.4f * u), Size(f.width, 1.4f * u))
        gastadoColonos(f, g.c(PINO, f.center), 7610 + i, 7, false, 0.6f)
        // La perilla de madera torneada, con la mugre de la mano alrededor.
        val tc = Offset(f.center.x, f.center.y)
        mancha(tc, f.height * 0.5f, a.sombra(0.18f))
        val rp = w * 0.013f
        drawCircle(a.sombra(0.45f), rp * 1.05f, tc + Offset(1.2f * u, 1.6f * u))
        drawCircle(g.c(PINO_OSC, tc), rp, tc)
        drawCircle(g.c(mezcla(PINO_OSC, PINO, 0.5f), tc), rp * 0.72f, tc - Offset(rp * 0.12f, rp * 0.12f))
        drawCircle(a.brillo(0.55f, g.k(tc), g.br(tc)), rp * 0.26f, tc - Offset(rp * 0.35f, rp * 0.35f))
    }
}
