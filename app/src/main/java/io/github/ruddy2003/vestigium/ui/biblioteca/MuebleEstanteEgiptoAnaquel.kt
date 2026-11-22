package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.HONDO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PALMA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.TIERRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.YESO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// El anaquel del almacén de la Tumba (MuebleEstanteEgipto): los troncos de palmera, los tablones de acacia y las ataduras.

/** Un color que cambia a lo largo de [desde]..[hasta] con la luz de la lámpara (de noche); de día, liso. */
private fun DrawScope.pintarConLuzEgipto(m: MesaEgipto, col: Color, r: Rect, vertical: Boolean, f: Float = 0f) {
    if (!m.a.oscuro) { drawRect(m.a.c(col, 1f, f), r.topLeft, r.size); return }
    val n = 6
    val colores = List(n) { i ->
        val t = i / (n - 1f)
        val p = if (vertical) Offset(r.center.x, r.top + r.height * t) else Offset(r.left + r.width * t, r.center.y)
        m.a.c(col, m.k(p), m.f(p))
    }
    val brocha = if (vertical) Brush.verticalGradient(colores, r.top, r.bottom) else Brush.horizontalGradient(colores, r.left, r.right)
    drawRect(brocha, r.topLeft, r.size)
}

/** El fondo de todos los tramos de una vez (un solo camino): el enlucido a la sombra del anaquel; de noche, según la lámpara. */
internal fun DrawScope.fondosTramosEgipto(m: MesaEgipto, repisas: List<Rect>) {
    if (repisas.isEmpty()) return
    val x0 = m.posteI.right; val x1 = m.posteD.left
    val zona = repisas.map { r -> Rect(x0, r.top, x1, r.bottom) }
    val col = EgiptoTinta.TRAMO
    if (m.a.oscuro) zonaNocheEgipto(m, zona, mezcla(col, EgiptoTinta.NOCHE, 0.35f), m.w * 1.15f)
    else { val c = m.a.c(col, 1f); zona.forEach { r -> drawRect(c, r.topLeft, r.size) } }
}

/**
 * El fondo de un tramo, entre los dos troncos: el enlucido del testero a la sombra del anaquel (más hondo que lo guardado,
 * para que resalte), el sol de la puerta que se cuela hasta el fondo, los troncos de atrás que dan la hondura, la sombra del
 * tablón de arriba en escalones, el costado de la izquierda en sombra y el de la derecha con luz, y la cara de arriba del
 * tablón donde pisan los volúmenes.
 */
internal fun DrawScope.fondoTramoEgipto(m: MesaEgipto, r: Rect) {
    val u = density
    val w = m.w; val a = m.a
    val x0 = m.posteI.right; val x1 = m.posteD.left
    val tramo = Rect(x0, r.top, x1, r.bottom)
    pinceladas(tramo, if (a.oscuro) EgiptoTinta.NOCHE else HONDO, 701 + (r.top / u).toInt(), 8, u, 0.14f)
    // El sol (o la luna) que se cuela entre los troncos y llega al fondo.
    clipRect(x0, r.top, x1, r.bottom) {
        drawPath(m.puertaEn(tramo), a.puerta(if (a.oscuro) 0.32f else 0.42f))
    }
    // Los troncos de atrás, más delgados y en sombra: la hondura del anaquel.
    val g = w * 0.03f
    listOf(x0 + w * 0.018f, x1 - w * 0.018f - g).forEachIndexed { i, x ->
        val tr = Rect(x, r.top, x + g, r.bottom)
        drawRect(a.c(mezcla(PALMA, HONDO, 0.45f), m.k(tr.center), m.f(tr.center)), tr.topLeft, tr.size)
        drawRect(a.s(if (i == 0) 0.3f else 0.12f), tr.topLeft, tr.size)
        drawRect(a.b(0.18f, m.k(tr.center)), Offset(tr.left + g * 0.15f, tr.top), Size(g * 0.18f, tr.height))
        val escamas = Path()
        var y = tr.top + 2f * u; var n = 0
        while (y < tr.bottom) { val xo = tr.left + (if (n % 2 == 0) 0f else g * 0.5f); escamas.moveTo(xo, y); escamas.quadraticTo(xo + g * 0.25f, y + 3f * u, xo + g * 0.5f, y); escamas.moveTo(xo + g * 0.5f, y); escamas.quadraticTo(xo + g * 0.75f, y + 3f * u, xo + g, y); y += 5f * u; n++ }
        clipRect(tr.left, tr.top, tr.right, tr.bottom) { drawPath(escamas, a.s(0.22f), style = Stroke(0.7f * u)) }
    }
    // La sombra del tablón de arriba, más honda a la izquierda, en escalones; el costado izquierdo en sombra, el derecho con luz.
    drawPath(poligonoEgipto(x0, r.top, x1, r.top, x1, r.top + r.height * 0.18f, x0, r.top + r.height * 0.4f), a.s(0.2f))
    drawPath(poligonoEgipto(x0, r.top, x1, r.top, x1, r.top + r.height * 0.07f, x0, r.top + r.height * 0.17f), a.s(0.26f))
    drawPath(poligonoEgipto(x0, r.top, x0 + w * 0.05f, r.top, x0 + w * 0.03f, r.bottom, x0, r.bottom), a.s(0.26f))
    drawRect(a.b(0.22f, m.k(Offset(x1, r.center.y))), Offset(x1 - w * 0.012f, r.top), Size(w * 0.012f, r.height))
    // La cara de arriba del tablón: vista un poco desde arriba, con su veta y el rincón del fondo en sombra.
    val cara = Rect(x0, r.bottom - w * 0.024f, x1, r.bottom)
    drawRect(a.c(mezcla(ACACIA, ACACIA_LUZ, 0.55f), m.k(cara.center), m.f(cara.center)), cara.topLeft, cara.size)
    val vetasCara = Path()
    for (j in 0 until 3) { val y = cara.top + cara.height * (0.3f + 0.22f * j); vetasCara.moveTo(x0, y); vetasCara.quadraticTo((x0 + x1) / 2, y + (azar(j, 711) - 0.5f) * 3f * u, x1, y + 0.5f * u) }
    drawPath(vetasCara, a.c(ACACIA_OSC, m.k(cara.center)).copy(alpha = 0.3f), style = Stroke(0.6f * u))
    drawRect(a.s(0.38f), cara.topLeft, Size(cara.width, 1.6f * u))
    drawRect(a.b(0.25f, m.k(cara.center)), Offset(x0, cara.bottom - 1f * u), Size(cara.width, 1f * u))
}

/** El anaquel: los dos troncos de palmera, los tablones de acacia (el de arriba más grueso, con su cara de arriba) y las ataduras. */
internal fun DrawScope.anaquelEgipto(m: MesaEgipto, d: DisposicionEstante) {
    val w = m.w; val a = m.a
    // La sombra de cada tronco en el muro, corrida a la derecha (la del de la derecha cae fuera del mueble).
    if (!a.oscuro) drawRect(a.s(0.2f), Offset(m.posteI.right, m.posteI.top + w * 0.02f), Size(w * 0.022f, m.suelo - m.posteI.top - w * 0.02f))
    posteEgipto(m, m.posteI, 41)
    posteEgipto(m, m.posteD, 47)
    // Las estacas que sostienen cada tablón, hincadas en los troncos.
    d.tablas.forEach { t -> estacasEgipto(m, t.bottom) }
    estacasEgipto(m, m.frenteArriba.bottom)
    // Los tablones.
    d.tablas.forEachIndexed { i, t -> tablonEgipto(m, Rect(w * (0.003f + 0.004f * azar(i, 3)), t.top, w * (0.997f - 0.004f * azar(i, 4)), t.bottom), 310 + 7 * i, i) }
    tablonArribaEgipto(m)
    // Las ataduras de soga, en cada cruce de tablón y tronco.
    d.tablas.forEach { t ->
        ataduraEgipto(m, Offset(m.posteI.center.x, t.center.y), m.posteI.width, t.height)
        ataduraEgipto(m, Offset(m.posteD.center.x, t.center.y), m.posteD.width, t.height)
    }
    ataduraEgipto(m, Offset(m.posteI.center.x, m.frenteArriba.center.y), m.posteI.width, m.frenteArriba.height)
    ataduraEgipto(m, Offset(m.posteD.center.x, m.frenteArriba.center.y), m.posteD.width, m.frenteArriba.height)
}

/**
 * Un tronco de palmera: el cilindro con su plano de luz, su brillo y su sombra; las escamas de las pencas cortadas, en hileras
 * alternadas, cada una con su borde de sombra y su labio de luz; la fibra que asoma; el corte de arriba con sus anillos y,
 * abajo, la tierra amontonada donde se hinca.
 */
private fun DrawScope.posteEgipto(m: MesaEgipto, r: Rect, semilla: Int) {
    val u = density
    val a = m.a
    val kM = m.k(r.center)
    val der = m.der(r.center.x)
    pintarConLuzEgipto(m, PALMA, r, vertical = true)
    clipRect(r.left, r.top, r.right, r.bottom) {
        // Las escamas de las pencas cortadas: la baldosa pegada a lo alto del tronco.
        val baldosa = EscamasEgipto.de(u, r.width)
        var y = r.top + 3f * u
        val alfa = if (a.oscuro) 0.55f else 1f
        while (y < r.bottom) { drawImage(baldosa, Offset(r.left, y), alpha = alfa); y += baldosa.height }
        // La fibra que asoma entre las escamas.
        val fibra = Path()
        for (j in 0 until 14) {
            val yy = r.top + r.height * azar(semilla, j)
            fibra.moveTo(r.left + r.width * azar(semilla, j + 20), yy); fibra.lineTo(r.left + r.width * azar(semilla, j + 40), yy + 5f * u)
        }
        drawPath(fibra, a.c(FIBRA, kM).copy(alpha = 0.6f), style = Stroke(0.6f * u))
        cilindroEgipto(r, a, kM, 1.25f, der)
        filoEgipto(r, a, kM, der)
        if (a.oscuro) {
            val f = m.f(Offset(r.center.x, m.ch))
            if (f > 0.1f) drawRect(a.helado(0.4f, f), Offset(if (der) r.left else r.right - 1.4f * u, r.top), Size(1.4f * u, m.ch + m.w * 0.4f - r.top))
        }
    }
    // El corte de arriba: la cara del tronco, con sus anillos y la astilla.
    val tapa = Rect(r.left - 0.5f * u, r.top - r.width * 0.15f, r.right + 0.5f * u, r.top + r.width * 0.15f)
    val kT = m.kAlto(tapa.center)
    drawOval(a.c(mezcla(FIBRA, PAPIRO, 0.35f), kT, m.f(tapa.center)), tapa.topLeft, tapa.size)
    for (j in 1..3) drawOval(a.c(PALMA, kT).copy(alpha = 0.45f), tapa.deflate(tapa.width * 0.12f * j).topLeft, tapa.deflate(tapa.width * 0.12f * j).size, style = Stroke(0.7f * u))
    drawOval(a.c(PALMA_OSC, kT), tapa.topLeft, tapa.size, style = Stroke(1f * u))
    drawArc(a.b(0.5f, kT), 190f, 120f, false, tapa.topLeft, tapa.size, style = Stroke(1f * u))
    // La tierra amontonada donde se hinca.
    val pie = Offset(r.center.x, r.bottom)
    drawOval(a.s(0.35f), Offset(pie.x - r.width * 0.75f, pie.y - r.width * 0.12f), Size(r.width * 1.6f, r.width * 0.32f))
    drawPath(poligonoEgipto(pie.x - r.width * 0.8f, pie.y + r.width * 0.06f, pie.x - r.width * 0.45f, pie.y - r.width * 0.12f, pie.x + r.width * 0.5f, pie.y - r.width * 0.1f, pie.x + r.width * 0.85f, pie.y + r.width * 0.06f),
        a.c(mezcla(TIERRA, PALMA_OSC, 0.2f), m.k(pie)))
}

/** Las estacas de madera que sostienen un tablón: asoman de cada tronco hacia dentro, justo debajo de él. */
private fun DrawScope.estacasEgipto(m: MesaEgipto, y: Float) {
    val u = density
    val a = m.a; val w = m.w
    listOf(m.posteI.right - w * 0.006f to 1f, m.posteD.left + w * 0.006f to -1f).forEach { (x, s) ->
        val k = m.k(Offset(x, y))
        val e = Rect(if (s > 0) x else x - w * 0.026f, y, if (s > 0) x + w * 0.026f else x, y + w * 0.012f)
        drawRect(a.s(0.35f), Offset(e.left + 1.2f * u, e.top + 1.6f * u), e.size)
        drawRect(a.c(ACACIA_OSC, k), e.topLeft, e.size)
        drawRect(a.c(ACACIA, k), e.topLeft, Size(e.width, e.height * 0.45f))
        drawOval(a.c(mezcla(ACACIA_LUZ, PAPIRO, 0.3f), k), Offset(if (s > 0) e.right - 2.4f * u else e.left, e.top), Size(2.4f * u, e.height))
    }
}

/**
 * Un tablón de acacia de frente, grueso: la veta, los golpes de la azuela en hileras, un nudo, una raja según el tablón, el
 * canto de arriba con luz, la sombra de abajo y las cabezas más oscuras (la testa).
 */
private fun DrawScope.tablonEgipto(m: MesaEgipto, r: Rect, semilla: Int, i: Int) {
    val u = density
    val a = m.a; val w = m.w
    pintarConLuzEgipto(m, ACACIA, r, vertical = false)
    val k = m.k(r.center)
    vetas(r, (if (a.oscuro) EgiptoTinta.NOCHE else ACACIA_OSC).copy(alpha = 0.34f), semilla, 0.9f, grosor = 0.7f, ondulacion = 0.6f)
    if (!a.lumbre) {
        drawRect(EgiptoTinta.SOL.copy(alpha = 0.14f), r.topLeft, Size(r.width * 0.28f, r.height))
        drawRect(a.s(0.1f), Offset(r.right - r.width * 0.3f, r.top), Size(r.width * 0.3f, r.height))
    }
    // Los golpes de la azuela: cortes cortos en arco, con su sombra y su luz.
    val golpes = Path(); val luces = Path()
    for (j in 0 until 14) {
        val x = r.left + r.width * azar(semilla, j)
        val y = r.top + r.height * (0.25f + 0.5f * azar(semilla + 1, j))
        val l = w * (0.016f + 0.012f * azar(semilla + 2, j))
        golpes.moveTo(x, y); golpes.quadraticTo(x + l / 2, y - l * 0.3f, x + l, y)
        luces.moveTo(x, y + 1f * u); luces.quadraticTo(x + l / 2, y + 1f * u - l * 0.3f, x + l, y + 1f * u)
    }
    drawPath(golpes, a.s(0.26f), style = Stroke(0.8f * u))
    drawPath(luces, a.b(0.26f, k), style = Stroke(0.6f * u))
    // Un nudo, y en uno de cada dos tablones una raja larga.
    val nx = r.left + r.width * (0.2f + 0.6f * azar(semilla, 60))
    drawOval(a.c(ACACIA_OSC, k), Offset(nx - w * 0.012f, r.center.y - w * 0.004f), Size(w * 0.024f, w * 0.009f))
    drawOval(a.s(0.3f), Offset(nx - w * 0.018f, r.center.y - w * 0.008f), Size(w * 0.036f, w * 0.017f), style = Stroke(0.7f * u))
    if (i % 2 == 0) {
        val x0 = r.left + r.width * (0.5f + 0.3f * azar(semilla, 61))
        drawPath(Path().apply { moveTo(x0, r.top + r.height * 0.55f); cubicTo(x0 + w * 0.05f, r.top + r.height * 0.48f, x0 + w * 0.1f, r.top + r.height * 0.64f, x0 + w * 0.16f, r.top + r.height * 0.6f) }, a.s(0.5f), style = Stroke(1.1f * u))
    }
    // Los cantos: el de arriba con luz, el de abajo en sombra, las testas oscuras.
    drawRect(a.c(ACACIA_LUZ, k), r.topLeft, Size(r.width, r.height * 0.16f))
    drawRect(a.b(0.6f, k), r.topLeft, Size(r.width, 1f * u))
    drawRect(a.s(0.36f), Offset(r.left, r.bottom - 1.8f * u), Size(r.width, 1.8f * u))
    drawRect(a.c(ACACIA_OSC, m.k(Offset(r.left, r.center.y))), Offset(r.left, r.top), Size(w * 0.009f, r.height))
    drawRect(a.c(ACACIA_OSC, m.k(Offset(r.right, r.center.y))), Offset(r.right - w * 0.009f, r.top), Size(w * 0.009f, r.height))
    drawRect(a.s(0.2f), Offset(r.right - w * 0.009f, r.top), Size(w * 0.009f, r.height))
    filoEgipto(r, a, k, m.der(r.center.x))
}

/**
 * El tablón de arriba, que hace de repisa alta: más grueso, con su cara de arriba vista un poco desde arriba (gastada, con un
 * cerco y unas gotas de grano), el frente labrado a azuela y la sombra que echa en el muro a la derecha.
 */
private fun DrawScope.tablonArribaEgipto(m: MesaEgipto) {
    val u = density
    val a = m.a; val w = m.w
    val sup = m.caraArriba; val fr = m.frenteArriba
    val k = m.kAlto(fr.center)
    if (!a.oscuro) drawPath(poligonoEgipto(fr.right, sup.top + w * 0.004f, fr.right + w * 0.02f, sup.top + w * 0.016f, fr.right + w * 0.02f, fr.bottom + w * 0.014f, fr.right, fr.bottom), a.s(0.25f))
    // La cara de arriba.
    pintarConLuzEgipto(m, ACACIA_LUZ, sup, vertical = false, f = 0f)
    clipRect(sup.left, sup.top, sup.right, sup.bottom) {
        drawPath(m.puertaEn(sup), a.puerta(if (a.oscuro) 0.3f else 0.4f))
    }
    vetas(sup, (if (a.oscuro) EgiptoTinta.NOCHE else ACACIA_OSC).copy(alpha = 0.28f), 5200, 0.6f, grosor = 0.6f, ondulacion = 0.6f)
    drawRect(a.s(0.2f), sup.topLeft, Size(sup.width, 1.6f * u))
    drawOval(a.s(0.16f), Offset(w * 0.6f, sup.top + sup.height * 0.25f), Size(w * 0.06f, sup.height * 0.5f), style = Stroke(1.2f * u))
    // El frente.
    tablonEgipto(m, fr, 5201, 1)
    // Más golpes de azuela en el frente grueso, en hilera.
    val golpes = Path(); val luces = Path()
    for (j in 0 until 18) {
        val x = fr.left + fr.width * azar(5210, j)
        val y = fr.top + fr.height * (0.3f + 0.5f * azar(5211, j))
        val l = w * (0.016f + 0.012f * azar(5212, j))
        golpes.moveTo(x, y); golpes.quadraticTo(x + l / 2, y - l * 0.3f, x + l, y)
        luces.moveTo(x, y + 1f * u); luces.quadraticTo(x + l / 2, y + 1f * u - l * 0.3f, x + l, y + 1f * u)
    }
    drawPath(golpes, a.s(0.24f), style = Stroke(0.8f * u))
    drawPath(luces, a.b(0.28f, k), style = Stroke(0.6f * u))
}

/**
 * La atadura de soga de fibra de palma que sujeta el tablón al tronco: vueltas en aspa sobre el tablón y vueltas apretadas
 * en el tronco, arriba y abajo; cada vuelta con su borde de sombra, su cuerpo y su brillo, y alguna hebra suelta.
 */
private fun DrawScope.ataduraEgipto(m: MesaEgipto, c: Offset, ancho: Float, alto: Float) {
    val u = density
    val a = m.a
    val k = m.k(c); val f = m.f(c)
    val hw = ancho * 0.58f; val hh = alto * 0.62f
    val soga = Path()
    for (j in 0 until 2) {
        val dx = (j - 0.5f) * 3f * u
        soga.moveTo(c.x - hw + dx, c.y - hh); soga.lineTo(c.x + hw + dx, c.y + hh)
        soga.moveTo(c.x + hw + dx, c.y - hh); soga.lineTo(c.x - hw + dx, c.y + hh)
    }
    // Las vueltas en el tronco, encima y debajo del tablón: un poco curvas, como dan la vuelta.
    for (j in 0 until 3) {
        listOf(c.y - alto * 0.5f - (j + 0.7f) * 2.6f * u, c.y + alto * 0.5f + (j + 0.7f) * 2.6f * u).forEach { y ->
            soga.moveTo(c.x - ancho * 0.5f, y); soga.quadraticTo(c.x, y + 1.6f * u, c.x + ancho * 0.5f, y)
        }
    }
    drawPath(corridoEgipto(soga, 1f * u, 1.6f * u), a.s(0.35f), style = Stroke(2.8f * u, cap = StrokeCap.Round))
    drawPath(soga, a.c(mezcla(FIBRA, PALMA_OSC, 0.35f), k, f), style = Stroke(2.8f * u, cap = StrokeCap.Round))
    drawPath(soga, a.c(FIBRA, k, f), style = Stroke(1.9f * u, cap = StrokeCap.Round))
    drawPath(corridoEgipto(soga, -0.5f * u, -0.6f * u), a.b(0.45f, k), style = Stroke(0.6f * u, cap = StrokeCap.Round))
    // Las hebras sueltas del cabo.
    drawLine(a.c(FIBRA, k, f), Offset(c.x + hw, c.y + hh), Offset(c.x + hw + 3f * u, c.y + hh + 6f * u), 0.8f * u)
    drawLine(a.c(FIBRA, k, f), Offset(c.x + hw, c.y + hh), Offset(c.x + hw + 5f * u, c.y + hh + 3f * u), 0.7f * u)
}

/**
 * El rótulo de cada mes: una tablilla de sicomoro enyesada, con su canto de madera, su filete rojo y su sombra, atada al canto
 * del tablón con dos cordeles que le dan la vuelta.
 */
internal fun DrawScope.rotuloEgipto(m: MesaEgipto, p: PlacaEstante, medidor: TextMeasurer) {
    val u = density
    val a = m.a
    val r = p.r
    val k = m.k(r.center); val f = m.f(r.center)
    val t = Rect(r.left, r.top - 1f * u, r.right, r.bottom + 1f * u)
    drawRoundRect(a.s(0.42f), Offset(t.left + 1.6f * u, t.top + 2f * u), t.size, CornerRadius(2f * u))
    drawRoundRect(a.c(ACACIA, k, f), t.topLeft, t.size, CornerRadius(2f * u))
    drawRect(a.c(ACACIA_OSC, k, f), Offset(t.left + 1f * u, t.bottom - 2f * u), Size(t.width - 2f * u, 2f * u))
    val cara = Rect(t.left + 1.4f * u, t.top + 1.2f * u, t.right - 1.4f * u, t.bottom - 2.4f * u)
    drawRoundRect(a.etiqueta(k, YESO), cara.topLeft, cara.size, CornerRadius(1.4f * u))
    drawRect(a.b(0.5f, k), cara.topLeft, Size(cara.width, 0.8f * u))
    drawRoundRect(a.c(ROJO, maxOf(k, 0.4f)).copy(alpha = 0.7f), Offset(cara.left + 2.4f * u, cara.top + 1.6f * u), Size(cara.width - 4.8f * u, cara.height - 3.2f * u), CornerRadius(1f * u), style = Stroke(0.7f * u))
    // Un golpe en la esquina, donde asoma la madera.
    drawPath(poligonoEgipto(cara.right - 5f * u, cara.bottom, cara.right, cara.bottom - 4f * u, cara.right, cara.bottom), a.c(ACACIA, k, f))
    // Los cordeles que dan la vuelta al tablón.
    listOf(cara.left + 6f * u, cara.right - 6f * u).forEach { x ->
        drawLine(a.s(0.4f), Offset(x + 1f * u, t.top - 2f * u), Offset(x + 1f * u, t.bottom + 2f * u), 1.8f * u)
        drawLine(a.c(FIBRA, k, f), Offset(x, t.top - 2.4f * u), Offset(x, t.bottom + 2.4f * u), 1.4f * u)
        drawLine(a.b(0.4f, k), Offset(x - 0.5f * u, t.top - 2.4f * u), Offset(x - 0.5f * u, t.bottom + 2.4f * u), 0.5f * u)
    }
    textoEn(medidor, p.texto, Rect(cara.left + 9f * u, cara.top + 0.6f * u, cara.right - 9f * u, cara.bottom - 0.6f * u), MuebleEstanteEgipto.LETRA, a.tinta, 10.5f,
        peso = FontWeight.Medium, minimo = 5f)
}

/**
 * La baldosa de las escamas del tronco de palmera, pintada una sola vez por densidad y ancho: hileras alternadas de escamas
 * (las bases de las pencas cortadas), cada una con su borde de sombra y su labio de luz, en colores neutros (sombra violeta y
 * luz crema con transparencia) que van encima del color del tronco, de día y de noche.
 */
private object EscamasEgipto {
    private var clave = 0f to 0
    private var baldosa: ImageBitmap? = null
    fun de(u: Float, ancho: Float): ImageBitmap {
        val an = ancho.roundToInt().coerceAtLeast(4)
        if (clave != (u to an) || baldosa == null) { baldosa = pintar(u, an); clave = u to an }
        return baldosa!!
    }
    private fun pintar(u: Float, an: Int): ImageBitmap {
        val filas = 8
        val alto = 6.5f * u
        val h = (alto * filas).roundToInt().coerceAtLeast(4)
        val img = ImageBitmap(an, h)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(an.toFloat(), h.toFloat())) {
            val g = an / 3f
            val paso = h / filas.toFloat()
            val bordes = Path(); val labios = Path()
            for (n in 0 until filas) {
                val y = n * paso
                var x = -(if (n % 2 == 0) 0f else g * 0.5f)
                while (x < an) {
                    bordes.moveTo(x, y); bordes.quadraticTo(x + g * 0.5f, y + paso * 0.95f, x + g, y)
                    labios.moveTo(x + g * 0.15f, y + paso * 0.25f); labios.quadraticTo(x + g * 0.5f, y + paso * 0.85f, x + g * 0.85f, y + paso * 0.25f)
                    x += g
                }
            }
            drawPath(labios, LUZ_MUEBLE.copy(alpha = 0.32f), style = Stroke(1.1f * u))
            drawPath(bordes, SOMBRA_MUEBLE.copy(alpha = 0.5f), style = Stroke(1.1f * u))
        }
        return img
    }
}
