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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.ADOBE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.ALMAGRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CAL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CAL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.JUNTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MAIZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.SALVIA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TEJA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.TEJA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VIGA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/*
 * La pared de la cocina (Colonos): adobe enjarrado y encalado con añil, con sus brochazos, los desconchones donde asoma el
 * barro y algún adobe, la cenefa pintada bajo la viga y, abajo, el zócalo de almagre; la viga con el cable de la luz en
 * sus aisladores; las franjas de pared a los lados de la alacena; y el piso de barro cocido con el tapete trenzado. La
 * ventana de botellas está en `MuebleEstanteColonosPared.kt`.
 */

/** El color de la cal en un punto: de día, más clara hacia la ventana; de noche, la luz que le llega, cálida cerca de ella. */
internal fun calEnColonos(g: MarcoColonos, p: Offset, base: Color = CAL): Color {
    val a = g.a
    if (a.oscuro) {
        val k = g.k(p)
        // La cal es clara: lejos de la luz se hunde más deprisa que lo demás, para que el charco de la bombilla se vea; cerca,
        // la luz cálida la tiñe de su color. La luna, en los Sueños, apenas la enfría.
        val kw = k * k * (1.25f - 0.25f * k)
        return mezcla(a.luz(base, kw, g.br(p), g.f(p) * 0.4f), a.calor(g.br(p)), 0.4f * kw * k)
    }
    val sol = rayoVentanaColonos(p.x / g.w, p.y / g.w)
    val c = mezcla(mezcla(CAL_OSC, base, 0.35f + 0.65f * sol), CAL_LUZ, 0.32f * sol * sol)
    return a.luz(c, g.k(p), 0f, g.f(p))
}

/**
 * La pared de cal azul de la corona, de arriba hasta [hasta]: de día, más clara hacia la ventana y en sombra lejos de ella;
 * de noche, el charco cálido de la bombilla (más abajo que arriba: la pantalla tapa la viga), hundida en el azul marino lejos
 * de él. Encima, los brochazos de la cal, desiguales.
 */
internal fun DrawScope.paredColonos(g: MarcoColonos, hasta: Float) {
    val u = density; val w = g.w; val a = g.a
    val centro = if (a.oscuro) Offset(w * MuebleEstanteColonos.LAMP_X, w * (MuebleEstanteColonos.LAMP_Y + 0.06f))
    else Offset(w * MuebleEstanteColonos.VENT_X, w * MuebleEstanteColonos.VENT_Y)
    // Las paradas del degradado salen de puntos reales de la pared, hacia abajo a la izquierda (lejos de la luz).
    val dir = if (a.oscuro) Offset(-0.82f, 0.57f) else Offset(0.87f, 0.5f)
    val paradas = listOf(0f, 0.06f, 0.14f, 0.24f, 0.36f, 0.52f, 0.72f, 1f).map { f ->
        f to calEnColonos(g, centro + dir * (w * 1.15f * f))
    }.toTypedArray()
    drawRect(Brush.radialGradient(*paradas, center = centro, radius = w * 1.15f), Offset.Zero, Size(w, hasta))
    // Los brochazos de la cal: unos más claros, otros más azules; de noche, apenas.
    if (!a.oscuro) pinceladas(Rect(0f, 0f, w, hasta), CAL_OSC, 7101, 26, u, 0.07f)
    if (!a.oscuro) pinceladas(Rect(0f, 0f, w, hasta), CAL_LUZ, 7102, 16, u, 0.1f)
    // Las pasadas de la llana en el enjarre: ondas largas y anchas, apenas más claras o más oscuras que la cal.
    if (!a.oscuro) {
        val ondas = Path(); val claras = Path()
        for (i in 0 until 14) {
            val y = hasta * (0.12f + 0.84f * i / 14f) + w * 0.01f * (azar(7103, i) - 0.5f)
            val x0 = w * (-0.1f + 0.5f * azar(7104, i)); val x1 = x0 + w * (0.35f + 0.5f * azar(7105, i))
            val camino = if (i % 2 == 0) ondas else claras
            camino.moveTo(x0, y); camino.cubicTo(x0 + (x1 - x0) * 0.3f, y - w * 0.012f, x0 + (x1 - x0) * 0.7f, y + w * 0.012f, x1, y)
        }
        drawPath(ondas, CAL_OSC.copy(alpha = 0.07f), style = Stroke(w * 0.012f))
        drawPath(claras, CAL_LUZ.copy(alpha = 0.1f), style = Stroke(w * 0.009f))
    }
    // Donde la cal quedó delgada asoma el barro de debajo: manchas tibias y desiguales, más abajo (donde se roza) y en los
    // rincones; de noche apenas.
    pinceladas(Rect(0f, hasta * 0.55f, w, hasta), BARRO, 7106, 14, u, if (a.oscuro) 0.03f else 0.09f)
    pinceladas(Rect(w * 0.82f, w * 0.06f, w, hasta), BARRO, 7107, 6, u, if (a.oscuro) 0.03f else 0.08f)
    // Dos grietas finas del enjarre.
    val grietas = Path().apply {
        moveTo(w * 0.585f, w * 0.115f); lineTo(w * 0.592f, w * 0.15f); lineTo(w * 0.588f, w * 0.18f)
        moveTo(w * 0.905f, w * 0.1f); lineTo(w * 0.9f, w * 0.135f); lineTo(w * 0.908f, w * 0.17f)
    }
    drawPath(grietas, a.sombra(if (a.oscuro) 0.22f else 0.25f), style = Stroke(0.6f * u))
    // La sombra de la viga, honda junto a ella.
    val vb = w * 0.05f
    drawRect(a.sombra(0.34f), Offset(0f, vb), Size(w, w * 0.006f))
    drawRect(a.sombra(0.14f), Offset(0f, vb), Size(w, w * 0.022f))
}

/**
 * La cenefa pintada a mano bajo la viga, de [y0] a [y1]: una enredadera de salvia que ondula, con sus hojas y unas florecitas
 * de ladrillo y maíz en las crestas; gastada y tapada a trechos por la cal.
 */
internal fun DrawScope.cenefaColonos(g: MarcoColonos, y0: Float, y1: Float) {
    val u = density; val w = g.w; val a = g.a
    val alto = y1 - y0
    val yc = y0 + alto * 0.5f
    val onda = w * 0.12f
    val tallo = Path(); val hojas = Path(); val flores = Path(); val botones = Path()
    var x = -onda * 0.5f
    tallo.moveTo(x, yc)
    var i = 0
    while (x < w + onda && i < 30) {
        val x1 = x + onda
        tallo.cubicTo(x + onda * 0.3f, yc - alto * 0.42f, x + onda * 0.7f, yc + alto * 0.42f, x1, yc)
        // Una hoja arriba y otra abajo, y la flor en la cresta.
        val ha = Offset(x + onda * 0.22f, yc - alto * 0.22f); val hb = Offset(x + onda * 0.75f, yc + alto * 0.2f)
        hojas.moveTo(ha.x, ha.y); hojas.quadraticTo(ha.x - alto * 0.3f, ha.y - alto * 0.38f, ha.x + alto * 0.22f, ha.y - alto * 0.42f); hojas.quadraticTo(ha.x + alto * 0.1f, ha.y - alto * 0.1f, ha.x, ha.y)
        hojas.moveTo(hb.x, hb.y); hojas.quadraticTo(hb.x + alto * 0.32f, hb.y + alto * 0.36f, hb.x - alto * 0.2f, hb.y + alto * 0.4f); hojas.quadraticTo(hb.x - alto * 0.08f, hb.y + alto * 0.1f, hb.x, hb.y)
        if (azar(7110, i) > 0.15f) {
            val fc = Offset(x + onda * 0.5f, yc)
            for (j in 0 until 5) {
                val ang = j * 1.2566f - 1.5708f
                flores.addOval(Rect(fc.x + alto * 0.13f * kotlin.math.cos(ang) - alto * 0.09f, fc.y + alto * 0.13f * kotlin.math.sin(ang) - alto * 0.09f,
                    fc.x + alto * 0.13f * kotlin.math.cos(ang) + alto * 0.09f, fc.y + alto * 0.13f * kotlin.math.sin(ang) + alto * 0.09f))
            }
            botones.addOval(Rect(fc.x - alto * 0.07f, fc.y - alto * 0.07f, fc.x + alto * 0.07f, fc.y + alto * 0.07f))
        }
        x = x1; i++
    }
    val k = if (a.oscuro) g.k(Offset(w * 0.6f, y0)) else 1f
    drawPath(tallo, a.luz(SALVIA, k).copy(alpha = 0.6f), style = Stroke(1.1f * u))
    drawPath(hojas, a.luz(SALVIA, k).copy(alpha = 0.55f))
    drawPath(flores, a.luz(ALMAGRE, k).copy(alpha = 0.6f))
    drawPath(botones, a.luz(MAIZ, k).copy(alpha = 0.75f))
    // La cal de encima, a brochazos: tapa la pintura a trozos.
    for (t in 0 until 6) {
        val xa = w * t / 6f; val xb = w * (t + 1) / 6f
        pinceladas(Rect(xa, y0 - alto * 0.2f, xb, y1 + alto * 0.2f), calEnColonos(g, Offset((xa + xb) / 2, y1)), 7112 + t, 3, u, 0.55f)
    }
}

/**
 * Un desconchón de la cal: donde se cayó, asoma el barro del enjarre, hundido apenas: el borde de cal que lo rodea echa su
 * sombra dentro (arriba a la izquierda) y tiene su canto de luz abajo a la derecha; en el grande asoman dos adobes con sus
 * juntas. [r] es su tamaño.
 */
internal fun DrawScope.desconchonColonos(g: MarcoColonos, c: Offset, r: Float, semilla: Int, adobes: Boolean) {
    val u = density; val a = g.a
    val p = Path()
    val n = 14
    for (i in 0 until n) {
        val ang = (i.toFloat() / n) * 6.283f
        val rr = r * (0.7f + 0.45f * azar(semilla, i)) * (if (i % 5 == 0) 0.75f else 1f)
        val x = c.x + rr * kotlin.math.cos(ang) * 1.4f
        val y = c.y + rr * kotlin.math.sin(ang) * 0.75f
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    val k = g.k(c)
    val barro = if (a.oscuro) g.c(mezcla(BARRO, CAL_OSC, 0.45f), c) else a.luz(mezcla(BARRO, CAL, 0.4f), k)
    drawPath(corridoColonos(p, 1f * u, 1.2f * u), a.claro(0.5f))
    drawPath(p, barro)
    clipPath(p) {
        if (adobes) {
            val al = r * 0.62f
            for (f in 0 until 3) {
                val y = c.y - r * 0.9f + al * f
                val corre = if (f % 2 == 0) 0f else r * 0.65f
                var x = c.x - r * 2.2f - corre
                var j = 0
                while (x < c.x + r * 2.2f && j < 10) {
                    val b = Rect(x + 1f * u, y + 1f * u, x + r * 1.3f - 1f * u, y + al - 1f * u)
                    drawRect(if (a.oscuro) g.c(mezcla(mezcla(ADOBE, BARRO, 0.4f), CAL_OSC, 0.3f), c) else a.luz(mezcla(mezcla(ADOBE, BARRO, 0.35f + 0.3f * azar(semilla + f, j)), CAL, 0.25f), k), b.topLeft, b.size)
                    drawRect(a.sombra(0.18f), Offset(b.left, b.bottom - 1f * u), Size(b.width, 1f * u))
                    x += r * 1.3f; j++
                }
            }
        }
        // La paja del enjarre.
        val paja = Path()
        for (i in 0 until 8) {
            val q = Offset(c.x + r * (azar(semilla + 9, i) - 0.5f) * 2.4f, c.y + r * (azar(semilla + 10, i) - 0.5f) * 1.1f)
            paja.moveTo(q.x, q.y); paja.lineTo(q.x + r * 0.28f, q.y + r * 0.06f * (azar(semilla, 20 + i) - 0.5f))
        }
        drawPath(paja, (if (a.oscuro) g.c(CRUDO, c, 0.6f) else mezcla(BARRO, CRUDO, 0.5f)).copy(alpha = 0.55f), style = Stroke(0.6f * u))
        // La sombra del borde de cal dentro del hueco, arriba a la izquierda.
        drawPath(corridoColonos(p, 2.2f * u, 2.4f * u), a.sombra(0.25f), style = Stroke(4.6f * u))
    }
}

/**
 * La viga de madera labrada a hacha, de lado a lado arriba (de [0] a [abajo]): sus caras, las marcas del hacha, las grietas
 * a lo largo y el canto de abajo; y el cable de la luz, torcido, en sus aisladores de porcelana, que baja a la bombilla.
 */
internal fun DrawScope.vigaColonos(g: MarcoColonos, abajo: Float) {
    val u = density; val w = g.w; val a = g.a
    val tramos = if (a.oscuro) 24 else 1
    for (t in 0 until tramos) {
        val xa = w * t / tramos; val xb = w * (t + 1) / tramos + 0.6f
        val p = Offset((xa + xb) / 2, abajo * 0.5f)
        drawRect(g.c(VIGA, p), Offset(xa, 0f), Size(xb - xa, abajo))
        drawRect(g.c(mezcla(VIGA, PINO_OSC, 0.5f), p), Offset(xa, abajo * 0.62f), Size(xb - xa, abajo * 0.38f))
    }
    vetas(Rect(0f, 0f, w, abajo * 0.62f), a.sombra(0.4f), 7120, 0.8f, grosor = 0.6f, ondulacion = 0.4f)
    // Las marcas del hacha: facetas más claras, en hileras.
    for (i in 0 until 16) {
        val x = w * (i + azar(7121, i) * 0.6f) / 16f
        val p = Offset(x, abajo * 0.3f)
        drawOval(a.brillo(0.16f, g.k(p)).copy(alpha = if (a.oscuro) 0.12f * g.k(p) else 0.14f), Offset(x, abajo * (0.08f + 0.3f * azar(7122, i))), Size(w * 0.05f, abajo * 0.22f))
    }
    // Dos grietas a lo largo.
    drawLine(a.sombra(0.6f), Offset(w * 0.08f, abajo * 0.36f), Offset(w * 0.37f, abajo * 0.42f), 0.9f * u)
    drawLine(a.sombra(0.55f), Offset(w * 0.55f, abajo * 0.24f), Offset(w * 0.92f, abajo * 0.3f), 0.8f * u)
    drawRect(a.claro(0.3f), Offset(0f, abajo * 0.62f), Size(w, 0.9f * u))
    drawRect(a.sombra(0.5f), Offset(0f, abajo - 1.2f * u), Size(w, 1.2f * u))

    // El cable torcido bajo la viga, en sus aisladores; entra por la izquierda y baja a la bombilla.
    val y = abajo + w * 0.004f
    val lx = w * MuebleEstanteColonos.LAMP_X
    val cable = Path().apply { moveTo(-2f, y); lineTo(lx, y) }
    drawPath(corridoColonos(cable, 1f * u, 1.6f * u), a.sombra(0.35f), style = Stroke(1.8f * u))
    drawPath(cable, g.c(HIERRO, Offset(lx, y)), style = Stroke(1.8f * u))
    var tx = 0f
    val torcido = Path()
    while (tx < lx) { torcido.moveTo(tx, y - 0.7f * u); torcido.lineTo(tx + 1.4f * u, y + 0.7f * u); tx += 2.4f * u }
    drawPath(torcido, a.brillo(0.35f, g.k(Offset(lx, y))), style = Stroke(0.5f * u))
    listOf(0.06f, 0.33f, 0.6f).forEach { fx -> aisladorColonos(g, Offset(w * fx, y)) }
}

/** Un aislador de porcelana: el carrete blanco con su ranura, su brillo y su sombra en la viga. */
internal fun DrawScope.aisladorColonos(g: MarcoColonos, p: Offset) {
    val u = density; val a = g.a
    val r = g.w * 0.0085f
    val k = g.k(p)
    drawCircle(a.sombra(0.35f), r, p + Offset(1.2f * u, 1.5f * u))
    drawCircle(g.c(CRUDO, p), r, p)
    drawCircle(a.sombra(0.25f), r * 0.6f, p, style = Stroke(0.7f * u))
    drawCircle(a.brillo(0.7f, k), r * 0.3f, p - Offset(r * 0.35f, r * 0.35f))
}

/**
 * Las franjas de pared que asoman a los lados de la alacena (de [g].ch al rincón): la cal azul y, abajo, el zócalo de almagre
 * con su filete; la sombra de la alacena en la de la derecha (de día) o en la de la izquierda (de noche, la bombilla está a la
 * derecha).
 */
internal fun DrawScope.paredLadosColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val b = g.borde
    val zoc = g.fondo + w * 0.035f
    // Cada franja, un píxel más ancha hacia la alacena (el costado la tapa): sin junta a medio píxel.
    listOf(0f, w - b - 1f).forEach { x ->
        val tramos = maxOf(1, ((g.suelo - g.ch) / (w * 0.15f)).toInt()).coerceAtMost(30)
        for (t in 0 until tramos) {
            val y0 = g.ch + (g.suelo - g.ch) * t / tramos; val y1 = g.ch + (g.suelo - g.ch) * (t + 1) / tramos + 0.6f
            val p = Offset(x + b / 2, (y0 + y1) / 2)
            val col = if (y0 >= zoc) ALMAGRE else CAL
            drawRect(if (a.oscuro || col == ALMAGRE) g.c(col, p) else calEnColonos(g, p), Offset(x, y0), Size(b + 1f, y1 - y0))
        }
        if (zoc < g.suelo) {
            val p = Offset(x + b / 2, zoc)
            drawRect(g.c(ALMAGRE, p), Offset(x, zoc), Size(b + 1f, g.suelo - zoc))
            drawRect(g.c(mezcla(ALMAGRE, LADRILLO, 0.5f), p), Offset(x, zoc - 1.2f * u), Size(b + 1f, 1.2f * u))
        }
    }
    // La sombra de la alacena en la pared: de día a su derecha; de noche, al lado contrario de la bombilla.
    if (!a.oscuro) drawRect(a.sombra(0.32f), Offset(w - b, g.ch), Size(b * 0.7f, g.suelo - g.ch))
    else drawRect(a.sombra(0.4f), Offset(b * 0.3f, g.ch), Size(b * 0.7f, g.suelo - g.ch))
}

/**
 * El piso de barro cocido, del rincón ([g].suelo) al borde de abajo: baldosas cuadradas que se abren hacia quien mira, de tres
 * tonos, con sus juntas, el desgaste y la sombra del rincón; y el zócalo de almagre en la pared, detrás de las patas.
 */
internal fun DrawScope.pisoColonos(g: MarcoColonos) {
    val u = density; val w = g.w; val a = g.a
    val top = g.fondo; val s = g.suelo; val h = g.h
    if (h - s < 2f) return
    // El zócalo de almagre detrás del cuerpo bajo (asoma entre las patas) y el filete de cal.
    drawRect(g.c(ALMAGRE, Offset(w * 0.5f, (top + s) / 2)), Offset(0f, top), Size(w, s - top))
    drawRect(g.c(CAL, Offset(w * 0.5f, top)), Offset(0f, top), Size(w, w * 0.03f))
    // Las baldosas, de atrás adelante: cada hilera más alta que la anterior y las juntas que se abren hacia el centro.
    val tonos = listOf(Path(), Path(), Path())
    val juntas = Path()
    val filas = 4
    var y0 = s
    val altos = FloatArray(filas) { i -> 0.8f + 0.25f * i }
    val suma = altos.sum()
    val cx = w * 0.5f
    for (f in 0 until filas) {
        val y1 = y0 + (h - s) * altos[f] / suma
        val ab0 = 1f + 0.55f * f / filas; val ab1 = 1f + 0.55f * (f + 1) / filas
        val cols = 7
        for (c in -1..cols) {
            val xa = w * c / cols; val xb = w * (c + 1) / cols
            val q = poligonoColonos(cx + (xa - cx) * ab0, y0, cx + (xb - cx) * ab0, y0, cx + (xb - cx) * ab1, y1, cx + (xa - cx) * ab1, y1)
            tonos[((azar(7150 + f, c + 3) * 3).toInt()).coerceIn(0, 2)].addPath(q)
        }
        juntas.moveTo(0f, y0); juntas.lineTo(w, y0)
        for (c in -1..cols + 1) { val x = w * c / cols; juntas.moveTo(cx + (x - cx) * ab0, y0); juntas.lineTo(cx + (x - cx) * ab1, y1) }
        y0 = y1
    }
    val kp = g.k(Offset(w * 0.5f, (s + h) / 2)); val bp = g.br(Offset(w * 0.2f, (s + h) / 2))
    listOf(TEJA, mezcla(TEJA, TEJA_LUZ, 0.45f), mezcla(TEJA, JUNTA, 0.25f)).forEachIndexed { i, col ->
        drawPath(tonos[i], a.luz(col, kp, bp))
    }
    drawPath(juntas, a.luz(JUNTA, kp, bp), style = Stroke(1.3f * u))
    pinceladas(Rect(0f, s, w, h), if (a.oscuro) a.hondo else TEJA_LUZ, 7151, 18, u, if (a.oscuro) 0.08f else 0.14f)
    // De día, la luz de la ventana en el piso, desde la izquierda; de noche, la estufa (lo pinta [estufaColonos]).
    if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(LUZ_MUEBLE.copy(alpha = if (a.noche) 0.06f else 0.12f), Color.Transparent), 0f, w * 0.7f), Offset(0f, s), Size(w * 0.7f, h - s))
    else drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.hondo.copy(alpha = 0.35f)), w * 0.3f, w), Offset(w * 0.3f, s), Size(w * 0.7f, h - s))
    // La sombra del rincón.
    drawRect(a.sombra(0.4f), Offset(0f, s), Size(w, 2.5f * u))
    drawRect(a.sombra(0.16f), Offset(0f, s), Size(w, w * 0.02f))
}

/**
 * El tapete de trapo trenzado, ovalado, delante de la alacena: vueltas de trenza de tres hebras, cada hebra de un trapo distinto
 * (ladrillo, maíz, salvia, crudo, pardo y marino, desteñidos), así que cada vuelta es moteada y no una franja lisa; el trenzado
 * en diagonal, la sombra entre vueltas y la del borde en el piso.
 */
internal fun DrawScope.tapeteColonos(g: MarcoColonos, c: Offset, rx: Float, ry: Float) {
    val u = density; val a = g.a
    val trapos = listOf(LADRILLO, MAIZ, SALVIA, CRUDO, PINO_OSC, MARINO).map { mezcla(it, CRUDO, 0.22f) }
    val n = 8
    val k = g.k(c); val br = g.br(c)
    // Visto en el piso: el largo a lo ancho, el ancho achatado por la perspectiva ([ry] / [rx] del ancho de verdad).
    val anchoReal = rx * 0.62f
    val p = ry / anchoReal
    val paso = anchoReal * 0.94f / n
    drawOval(a.sombra(0.35f), Offset(c.x - rx + 2f * u, c.y - ry + 3f * u), Size(rx * 2, ry * 2))
    for (i in 0 until n) {
        val d = paso * i
        val ax = rx - d; val ay = (anchoReal - d) * p
        val r = Rect(c.x - ax, c.y - ay, c.x + ax, c.y + ay)
        val esq = CornerRadius(anchoReal - d, ay)
        val h0 = trapos[(i * 2) % trapos.size]; val h1 = trapos[(i * 2 + 3) % trapos.size]; val h2 = trapos[(i + 4) % trapos.size]
        drawRoundRect(a.luz(mezcla(mezcla(h0, h1, 0.5f), h2, 0.33f), k, br), r.topLeft, r.size, esq)
        // Las tres hebras de la trenza: óvalos chicos en diagonal a lo largo de la vuelta, de su trapo cada uno.
        val hebras = listOf(Path(), Path(), Path())
        val largo = 2f * (ax - (anchoReal - d)) + 3.14f * (anchoReal - d)
        val pasos = (largo / (paso * 0.55f)).toInt().coerceIn(8, 90)
        val cx0 = ax - (anchoReal - d)
        for (j in 0 until pasos) {
            val t = j.toFloat() / pasos
            // Recorre la vuelta: el tramo recto de arriba, la curva derecha, el recto de abajo, la curva izquierda.
            val s0 = t * largo
            val recto = 2f * cx0; val curva = 3.14f * (anchoReal - d) / 2f
            val rr = anchoReal - d - paso * 0.5f
            val (x, y) = when {
                s0 < recto / 2 -> (c.x - cx0 + s0 * 2) to (c.y - rr * p)
                s0 < recto / 2 + curva -> { val ang = -1.5708f + (s0 - recto / 2) / curva * 3.1416f; (c.x + cx0 + rr * kotlin.math.cos(ang)) to (c.y + rr * p * kotlin.math.sin(ang)) }
                s0 < recto + curva -> (c.x + cx0 - (s0 - recto / 2 - curva) * 2) to (c.y + rr * p)
                else -> { val ang = 1.5708f + (s0 - recto - curva) / curva * 3.1416f; (c.x - cx0 + rr * kotlin.math.cos(ang)) to (c.y + rr * p * kotlin.math.sin(ang)) }
            }
            val dx = paso * 0.32f; val dy = paso * 0.2f * p
            hebras[j % 3].addOval(Rect(x - dx, y - dy, x + dx, y + dy))
        }
        listOf(h0, h1, h2).forEachIndexed { q, col -> drawPath(hebras[q], a.luz(col, k, br)) }
        drawRoundRect(a.sombra(0.24f), r.topLeft, r.size, esq, style = Stroke(0.9f * u))
    }
    drawOval(a.brillo(0.1f, k, br), Offset(c.x - rx * 0.8f, c.y - ry * 0.85f), Size(rx * 0.9f, ry * 0.6f))
}
