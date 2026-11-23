package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AMATE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AMATE_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AZUL_HONDO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CALIZA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE_OSC
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

// El anaquel de chicozapote del cuarto de guardar (MuebleEstanteMeso): el remate de moldura escalonada, los postes labrados
// a hacha de piedra, las tablas que los atraviesan y se traban con cuñas, los amarres de henequén y las zapatas de caliza.

/** Un color que cambia a lo largo de [r] con la luz de las lumbres (de noche); de día, liso. */
private fun DrawScope.conLuzMeso(m: MesaMeso, col: Color, r: Rect, vertical: Boolean, f: Float = 0f) {
    if (!m.a.oscuro) { drawRect(m.a.c(col, 1f, f), r.topLeft, r.size); return }
    if (!alcanzaMeso(m, r)) { drawRect(m.a.c(col, 0f, m.f(r.center)), r.topLeft, r.size); return }
    val n = 6
    val colores = List(n) { i ->
        val t = i / (n - 1f)
        val p = if (vertical) Offset(r.center.x, r.top + r.height * t) else Offset(r.left + r.width * t, r.center.y)
        m.a.c(col, m.k(p), m.f(p))
    }
    val brocha = if (vertical) Brush.verticalGradient(colores, r.top, r.bottom) else Brush.horizontalGradient(colores, r.left, r.right)
    drawRect(brocha, r.topLeft, r.size)
}

/** Si alguna de las lumbres llega a algo de [r] (de noche). */
private fun alcanzaMeso(m: MesaMeso, r: Rect): Boolean = m.lumbres.any { l ->
    val c = l.centro
    val radio = m.w * alcanceLumbreMeso(l.clase, m.a.lunar)
    r.left < c.x + radio && r.right > c.x - radio && r.top < c.y + radio && r.bottom > c.y - radio
}

// ---------------------------------------------------------------- el remate (arriba, pintado una vez)

/**
 * El remate del anaquel: una viga gruesa de chicozapote a todo lo ancho, como las cornisas de piedra de la ciudad: la cara de
 * arriba (donde pisa el bodegón), el listel que sale con su filo de luz, un escalón en sombra, el friso de tableros rehundidos
 * con restos de cinabrio entre dados lisos, y el delantal que se abre abajo con su labio de luz.
 */
internal fun DrawScope.remateMeso(m: MesaMeso) {
    val u = density
    val w = m.w; val a = m.a; val ch = m.ch
    val y0 = m.apoyo - w * 0.017f
    val y1 = m.apoyo + w * 0.01f
    // La cara de arriba, vista un poco desde arriba, con su veta y el rincón del testero en sombra.
    val cara = Rect(-1f, y0, w + 1f, y1)
    conLuzMeso(m, mezcla(ZAPOTE, ZAPOTE_LUZ, 0.55f), cara, vertical = false, f = m.f(cara.center))
    if (!a.lumbre) clipRect(cara.left, cara.top, cara.right, cara.bottom) { drawPath(m.puertaEn(cara), a.puerta(0.3f)) }
    val veta = Path()
    for (j in 0 until 4) { val y = y0 + (y1 - y0) * (0.25f + 0.2f * j); veta.moveTo(0f, y); veta.quadraticTo(w * 0.5f, y + (azar(j, 701) - 0.5f) * 3f * u, w, y + 0.4f * u) }
    drawPath(veta, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.3f), style = Stroke(0.7f * u))
    drawRect(a.s(0.4f), Offset(0f, y0), Size(w, 1.8f * u))
    // El listel: la tabla de arriba que sale, de canto.
    val listel = Rect(-1f, y1, w + 1f, y1 + w * 0.018f)
    conLuzMeso(m, ZAPOTE, listel, vertical = false)
    drawRect(a.b(0.55f, m.k(Offset(w * 0.8f, y1))), Offset(0f, y1), Size(w, 1.4f * u))
    vetasMeso(listel, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.35f), 702)
    drawRect(a.s(0.35f), Offset(0f, listel.bottom - 1.6f * u), Size(w, 1.6f * u))
    // El escalón, metido, en la sombra del listel.
    val escalon = Rect(-1f, listel.bottom, w + 1f, listel.bottom + w * 0.014f)
    conLuzMeso(m, mezcla(ZAPOTE, ZAPOTE_OSC, 0.45f), escalon, vertical = false)
    drawRect(a.s(0.3f), escalon.topLeft, Size(escalon.width, escalon.height * 0.5f))
    drawRect(a.b(0.3f, m.k(Offset(w * 0.8f, escalon.bottom))), Offset(0f, escalon.bottom - 1.1f * u), Size(w, 1.1f * u))
    // El friso: tableros rehundidos con el cinabrio gastado, entre dados lisos con su chaflán.
    val friso = Rect(-1f, escalon.bottom, w + 1f, ch - w * 0.03f)
    conLuzMeso(m, ZAPOTE, friso, vertical = false)
    vetasMeso(friso, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.3f), 703)
    val dados = 6
    val paso = w / dados
    for (i in 0 until dados) {
        val xd = paso * i
        val tablero = Rect(xd + paso * 0.14f, friso.top + friso.height * 0.2f, xd + paso * 0.94f, friso.bottom - friso.height * 0.2f)
        val k = m.k(tablero.center)
        drawRect(a.c(mezcla(ZAPOTE_OSC, ZAPOTE, 0.35f), k), tablero.topLeft, tablero.size)
        // El cinabrio que queda en el fondo del tablero, comido por el uso.
        val roja = contornoMeso(tablero.deflate(tablero.height * 0.12f), 704 + i, 0.08f)
        drawPath(roja, a.c(CINABRIO, k).copy(alpha = 0.75f))
        clipPath(roja) { pinceladas(tablero, ZAPOTE_OSC, 705 + i, 5, u, 0.3f) }
        // El rehundido: sombra arriba y a la izquierda (lo tapa el borde), luz abajo y a la derecha.
        drawRect(a.s(0.45f), tablero.topLeft, Size(tablero.width, 1.8f * u))
        drawRect(a.s(0.3f), tablero.topLeft, Size(1.6f * u, tablero.height))
        drawRect(a.b(0.35f, k), Offset(tablero.left, tablero.bottom - 1.1f * u), Size(tablero.width, 1.1f * u))
        drawRect(a.b(0.25f, k), Offset(tablero.right - 1.1f * u, tablero.top), Size(1.1f * u, tablero.height))
        // El dado de su izquierda, con su chaflán de luz.
        val dado = Rect(xd - paso * 0.04f, friso.top + friso.height * 0.12f, xd + paso * 0.1f, friso.bottom - friso.height * 0.12f)
        drawRect(a.c(ZAPOTE_LUZ, m.k(dado.center), m.f(dado.center)), dado.topLeft, dado.size)
        drawRect(a.b(0.4f, m.k(dado.center)), dado.topLeft, Size(dado.width, 1.4f * u))
        drawRect(a.s(0.3f), Offset(dado.right - 1.6f * u, dado.top), Size(1.6f * u, dado.height))
        drawRect(a.s(0.35f), Offset(dado.left, dado.bottom - 1.4f * u), Size(dado.width, 1.4f * u))
    }
    // El delantal: se abre hacia abajo; su cara en media sombra y el labio de luz donde vuelve.
    val delantal = Rect(-1f, friso.bottom, w + 1f, ch + 1f)
    conLuzMeso(m, mezcla(ZAPOTE, ZAPOTE_LUZ, 0.2f), delantal, vertical = false)
    drawRect(a.s(0.32f), delantal.topLeft, Size(w + 2f, 1.8f * u))
    drawRect(a.b(0.3f, m.k(Offset(w * 0.8f, delantal.top))), Offset(0f, delantal.top + 1.8f * u), Size(w, 1.2f * u))
    drawRect(a.c(ZAPOTE_LUZ, m.k(Offset(w * 0.6f, ch))), Offset(0f, ch - w * 0.009f), Size(w, w * 0.006f))
    drawRect(a.s(0.45f), Offset(0f, ch - w * 0.003f), Size(w, w * 0.003f + 1f))
    // Las mellas del uso en el canto del listel.
    for (j in 0 until 5) {
        val x = w * (0.08f + 0.86f * azar(j, 706))
        val mella = contornoMeso(Rect(x, y1 - 0.6f * u, x + w * (0.012f + 0.01f * azar(j, 707)), y1 + w * 0.007f), 708 + j, 0.3f)
        drawPath(mella, a.c(ZAPOTE_OSC, m.k(Offset(x, y1))))
        drawPath(bordeMeso(mella, 0f, 1f * u), a.b(0.25f, m.k(Offset(x, y1))))
    }
}

/** Unas vetas tenues a lo largo de [r], en un solo camino. */
private fun DrawScope.vetasMeso(r: Rect, col: Color, semilla: Int) {
    val u = density
    val p = Path()
    val n = (r.height / (2.6f * u)).toInt().coerceIn(2, 14)
    for (j in 0 until n) {
        val y = r.top + r.height * (j + 0.5f) / n
        val ond = (azar(semilla, j) - 0.5f) * r.height * 0.3f
        p.moveTo(r.left, y); p.cubicTo(r.left + r.width * 0.3f, y + ond, r.left + r.width * 0.6f, y - ond, r.right, y + ond * 0.4f)
    }
    clipRect(r.left, r.top, r.right, r.bottom) { drawPath(p, col, style = Stroke(0.6f * u)) }
}

// ---------------------------------------------------------------- el cuerpo (cada dibujo)

/**
 * El fondo de cada hueco: el testero de azul maya en la sombra del anaquel (de día, un azul verde hondo; de noche, añil violeta,
 * con el resplandor de cada lumbre donde llega), con sus manchas de repaso y algún desconchón; la sombra que arroja la tabla de
 * arriba (en escalones, franca arriba y más honda en los rincones), el costado de dentro de cada poste (el que da a la luz,
 * encendido; el otro, en sombra) y la cara de arriba de la tabla donde pisan los volúmenes. En los Sueños de noche, el plano de
 * luna cruza los huecos.
 */
internal fun DrawScope.fondosHuecosMeso(m: MesaMeso, repisas: List<Rect>, ocupado: List<Float>) {
    val u = density
    val w = m.w; val a = m.a
    val x0 = m.posteI.right - 1f; val x1 = m.posteD.left + 1f
    val zona = repisas.map { r -> Rect(x0, r.top - 1f, x1, r.bottom + 1f) }
    // De noche, la luz de las lumbres en el fondo se pinta solo donde se ve: arriba de los volúmenes (que llenan del 80 al
    // 95 % del hueco) y de donde acaban hasta el poste; entre uno y otro queda el fondo hondo, como en una rendija.
    val luz = if (!a.oscuro) zona else zona.flatMapIndexed { i, z ->
        val corte = z.top + (z.bottom - z.top) * 0.3f
        val oc = ocupado.getOrNull(i) ?: x0
        listOf(Rect(z.left, z.top, z.right, corte), Rect(maxOf(z.left, oc - w * 0.01f), corte, z.right, z.bottom)).filter { it.width > 0f }
    }
    if (a.oscuro) zonaNocheMeso(m, zona, mezcla(AZUL_HONDO, MesoTinta.ANIL_HONDO, 0.55f), 1.3f, luz)
    else { val c = a.c(AZUL_HONDO, 1f); zona.forEach { r -> drawRect(c, r.topLeft, r.size) } }
    // Los lados del muro, fuera de los postes: el mismo testero, sin la sombra del anaquel.
    val fin = repisas.lastOrNull()?.bottom?.plus(w) ?: m.ch
    val lados = listOf(Rect(-1f, m.ch - 1f, m.posteI.left + 1f, fin), Rect(m.posteD.right - 1f, m.ch - 1f, w + 1f, fin))
    // Son dos franjas angostas: de noche les basta un degradado de arriba abajo con la luz que les llega.
    if (a.oscuro) lados.forEach { r -> conLuzMeso(m, AZUL, r, vertical = true, f = m.f(r.center)) }
    else panoMeso(m, lados, AZUL)
    if (!a.oscuro) drawRect(a.s(0.3f), Offset(m.posteD.right, m.ch), Size(w * 0.016f, lados[1].height))
    val claro = if (a.oscuro) mezcla(AZUL, MesoTinta.NOCHE, 0.35f) else mezcla(AZUL_HONDO, AZUL, 0.55f)
    val hondo = if (a.oscuro) MesoTinta.NOCHE_SOMBRA else mezcla(AZUL_HONDO, TINTA, 0.35f)
    // Las capas de la sombra de la tabla (hasta 0.36, 0.2, 0.1 y 0.04 del alto del hueco): en cada franja, las que la cubren.
    val capas = listOf(a.s(0.1f), a.s(0.12f), a.s(0.16f), a.s(0.22f))
    val sombraTabla = (4 downTo 1).map { n -> var t = 1f; for (q in 0 until n) t *= 1f - capas[q].alpha; capas[0].copy(alpha = 1f - t) }
    repisas.forEachIndexed { i, r ->
        val tramo = Rect(x0, r.top, x1, r.bottom)
        // Las manchas del repaso, claras y hondas, y un desconchón cada dos repisas.
        pinceladas(tramo, claro, 721 + i * 3, 7, u, 0.24f)
        pinceladas(tramo, hondo, 722 + i * 3, 5, u, 0.18f)
        if (i % 2 == 1) {
            val c = Offset(x0 + (x1 - x0) * (0.2f + 0.6f * azar(i, 723)), r.top + r.height * (0.4f + 0.2f * azar(i, 724)))
            val hoyo = contornoMeso(Rect(c.x - w * 0.03f, c.y - w * 0.014f, c.x + w * 0.03f, c.y + w * 0.014f), 725 + i, 0.3f)
            drawPath(hoyo, a.c(mezcla(AZUL_HONDO, ESTUCO_OSC, 0.45f), m.k(c), m.f(c)))
            drawPath(bordeMeso(hoyo, -1f * u, -1.2f * u), a.s(0.35f))
        }
        // La sombra de la tabla de arriba, en escalones (franca arriba): cuatro franjas que no se pisan, cada una con las capas
        // que le tocan ya juntas (igual que encimarlas, con menos pintura); y la de los rincones.
        var arriba = 0f
        listOf(0.04f, 0.1f, 0.2f, 0.36f).forEachIndexed { n, abajo ->
            drawRect(sombraTabla[n], Offset(x0, r.top + r.height * arriba), Size(x1 - x0, r.height * (abajo - arriba)))
            arriba = abajo
        }
        listOf(0.06f to 0.12f, 0.025f to 0.16f).forEach { (ancho, al) ->
            drawRect(a.s(al), Offset(x0, r.top), Size(w * ancho, r.height))
            drawRect(a.s(al * 0.8f), Offset(x1 - w * ancho, r.top), Size(w * ancho, r.height))
        }
        // Los costados de dentro: el que mira a la luz, encendido; el otro, en sombra.
        val g = w * 0.022f
        val kI = m.k(Offset(x0, r.center.y)); val kD = m.k(Offset(x1, r.center.y))
        val izqLuz = a.oscuro && m.der(x0, r.center.y)
        drawPath(poligonoMeso(x0, r.top, x0 + g, r.top + g * 0.8f, x0 + g, r.bottom - g * 0.4f, x0, r.bottom),
            a.c(if (izqLuz) ZAPOTE else mezcla(ZAPOTE, ZAPOTE_OSC, 0.6f), kI))
        drawPath(poligonoMeso(x1, r.top, x1 - g, r.top + g * 0.8f, x1 - g, r.bottom - g * 0.4f, x1, r.bottom),
            a.c(if (a.oscuro && !m.der(x1, r.center.y)) ZAPOTE else ZAPOTE_LUZ, kD, m.f(Offset(x1, r.center.y))))
        drawRect(a.s(0.25f), Offset(x1 - g, r.top), Size(g * 0.3f, r.height))
        if (a.oscuro) {
            if (izqLuz && kI > 0.06f) drawRect(a.b(0.4f, kI), Offset(x0 + g - 1.2f * u, r.top + g * 0.8f), Size(1.2f * u, r.height - g * 1.2f))
            if (!m.der(x1, r.center.y) && kD > 0.06f) drawRect(a.b(0.4f, kD), Offset(x1 - g, r.top + g * 0.8f), Size(1.2f * u, r.height - g * 1.2f))
        }
        // La cara de arriba de la tabla: vista un poco desde arriba, con la sombra del rincón del fondo.
        val cara = Rect(x0, r.bottom - w * 0.022f, x1, r.bottom + 1f)
        drawRect(a.c(mezcla(ZAPOTE, ZAPOTE_LUZ, 0.6f), m.k(cara.center), m.f(cara.center)), cara.topLeft, cara.size)
        drawRect(a.s(0.42f), cara.topLeft, Size(cara.width, 1.8f * u))
        val vetaCara = Path()
        for (j in 0 until 2) { val y = cara.top + cara.height * (0.4f + 0.3f * j); vetaCara.moveTo(x0, y); vetaCara.quadraticTo((x0 + x1) / 2, y + (azar(i * 2 + j, 722) - 0.5f) * 2.4f * u, x1, y) }
        drawPath(vetaCara, a.c(ZAPOTE_OSC, m.k(cara.center)).copy(alpha = 0.3f), style = Stroke(0.6f * u))
    }
    if (a.lunar) planoLunaMeso(m, Rect(0f, m.ch, w, fin))
}

/**
 * Las tablas de cada repisa, de canto: el chicozapote con su veta, la luz arriba y la sombra abajo, las huellas de la azuela y
 * alguna mella; de noche, cada una con la luz que le llega a lo largo.
 */
internal fun DrawScope.tablasMeso(m: MesaMeso, d: DisposicionEstante) {
    val u = density
    val w = m.w; val a = m.a
    d.tablas.forEachIndexed { i, t ->
        val r = Rect(m.posteI.right - 2f, t.top, m.posteD.left + 2f, t.bottom)
        conLuzMeso(m, ZAPOTE, r, vertical = false, f = m.f(r.center))
        val kc = m.k(r.center)
        drawRect(a.b(0.5f, m.k(Offset(w * 0.8f, r.top))), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(a.b(0.14f, kc), Offset(r.left, r.top + 1.4f * u), Size(r.width, r.height * 0.36f))
        drawRect(a.s(0.22f), Offset(r.left, r.bottom - r.height * 0.36f), Size(r.width, r.height * 0.36f))
        drawRect(a.s(0.4f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        vetasMeso(r, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.32f), 730 + i)
        // Las huellas de la azuela: cucharadas cortas, con su borde de luz.
        val huellas = Path()
        for (j in 0 until 9) {
            val x = r.left + r.width * azar(i * 11 + j, 731); val y = r.top + r.height * (0.25f + 0.5f * azar(i * 11 + j, 732))
            val l = w * (0.025f + 0.025f * azar(i * 11 + j, 733))
            huellas.moveTo(x, y); huellas.quadraticTo(x + l * 0.5f, y + r.height * 0.18f, x + l, y)
        }
        drawPath(huellas, a.s(0.22f), style = Stroke(0.8f * u))
        translate(0f, -0.9f * u) { drawPath(huellas, a.b(0.16f, kc), style = Stroke(0.5f * u)) }
        // Una mella en el canto.
        val xm = r.left + r.width * (0.2f + 0.6f * azar(i, 734))
        val mella = contornoMeso(Rect(xm, r.top - 0.5f * u, xm + w * 0.02f, r.top + r.height * 0.3f), 735 + i, 0.3f)
        drawPath(mella, a.c(ZAPOTE_OSC, kc))
        drawPath(bordeMeso(mella, 0f, 1f * u), a.b(0.22f, kc))
        filoMeso(Rect(r.left, r.top, r.right, r.top + 1f), a, kc, false)
    }
}

/**
 * Los dos postes de chicozapote, de [arriba] a [abajo]: cuadrados, labrados a hacha de piedra (tres planos de luz distinta a lo
 * largo y las cucharadas de la azuela), con su filo de luz del lado de la puerta y su sombra del otro.
 */
internal fun DrawScope.postesMeso(m: MesaMeso, arriba: Float, abajo: Float) {
    listOf(m.posteI, m.posteD).forEachIndexed { i, p -> posteMeso(m, Rect(p.left, arriba, p.right, abajo), 741 + i * 5) }
}

private fun DrawScope.posteMeso(m: MesaMeso, r: Rect, semilla: Int) {
    val u = density
    val w = m.w; val a = m.a
    if (r.height < 1f) return
    val der = m.der(r.center.x, r.center.y)
    val k = m.k(r.center)
    conLuzMeso(m, ZAPOTE, r, vertical = true, f = m.f(r.center))
    clipRect(r.left, r.top, r.right, r.bottom) {
        // El grueso: el chaflán que mira a la luz (claro) y el del otro canto (hondo), y los planos del hacha en la cara.
        val ch = w * 0.008f
        val pl = r.width * 0.36f
        drawRect(if (der) a.s(0.24f) else a.b(0.24f, k), Offset(r.left + ch, r.top), Size(pl, r.height))
        drawRect(if (der) a.b(0.22f, k) else a.s(0.3f), Offset(r.right - ch - pl * 0.8f, r.top), Size(pl * 0.8f, r.height))
        drawRect(a.c(if (der) ZAPOTE_OSC else ZAPOTE_LUZ, k, m.f(r.center)), Offset(r.left, r.top), Size(ch, r.height))
        drawRect(a.c(if (der) ZAPOTE_LUZ else ZAPOTE_OSC, k), Offset(r.right - ch, r.top), Size(ch, r.height))
        // La veta, con dos nudos.
        val vetas = Path()
        for (j in 0 until 7) {
            val x = r.left + r.width * (0.1f + 0.13f * j + 0.04f * azar(semilla, j))
            vetas.moveTo(x, r.top); vetas.cubicTo(x + 2.4f * u, r.top + r.height * 0.3f, x - 2.4f * u, r.top + r.height * 0.6f, x + 1f * u, r.bottom)
        }
        drawPath(vetas, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.38f), style = Stroke(0.8f * u))
        var yn = r.top + w * (0.12f + 0.2f * azar(semilla, 9)); var n = 0
        while (yn < r.bottom - w * 0.04f && n < 12) {
            val c = Offset(r.left + r.width * (0.35f + 0.3f * azar(semilla, n + 10)), yn)
            drawOval(a.c(ZAPOTE_OSC, k), Offset(c.x - w * 0.007f, c.y - w * 0.012f), Size(w * 0.014f, w * 0.024f))
            drawOval(a.c(mezcla(ZAPOTE_OSC, TINTA, 0.4f), k), Offset(c.x - w * 0.003f, c.y - w * 0.006f), Size(w * 0.006f, w * 0.012f))
            drawOval(a.b(0.2f, k), Offset(c.x - w * 0.007f, c.y - w * 0.012f), Size(w * 0.014f, w * 0.024f), style = Stroke(0.6f * u))
            yn += w * (0.45f + 0.3f * azar(semilla, n + 20)); n++
        }
        // Las huellas del hacha: tajos cortos en diagonal, cada uno con su sombra y su borde de luz.
        val huellas = Path()
        var y = r.top + w * 0.01f; var j = 0
        while (y < r.bottom && j < 80) {
            val x = r.left + r.width * (if (j % 2 == 0) 0.14f else 0.5f) + r.width * 0.08f * azar(semilla, j)
            huellas.moveTo(x, y); huellas.lineTo(x + r.width * 0.3f, y + w * 0.012f)
            y += w * (0.02f + 0.012f * azar(semilla, j + 40)); j++
        }
        drawPath(huellas, a.s(0.3f), style = Stroke(1f * u))
        translate(0f, -1f * u) { drawPath(huellas, a.b(0.2f, k), style = Stroke(0.6f * u)) }
    }
    // El filo de luz del lado de la puerta (de noche, de la lumbre) y la sombra del otro.
    drawRect(a.b(if (a.oscuro) 0.7f else 0.45f, k), Offset(if (der) r.right - 1.6f * u else r.left, r.top), Size(1.6f * u, r.height))
    drawRect(a.s(0.38f), Offset(if (der) r.left else r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
    if (!a.oscuro && m.f(r.center) > 0.05f) drawRect(a.puerta(0.25f * m.f(r.center)), r.topLeft, r.size)
}

/**
 * Lo que traba cada tabla en los postes: la espiga que sale por fuera del poste con su cuña de madera metida de arriba abajo,
 * y, encima de la tabla, el amarre de henequén que ciñe el poste (las vueltas cruzadas, con su luz y su sombra).
 */
internal fun DrawScope.amarresMeso(m: MesaMeso, d: DisposicionEstante) {
    val u = density
    val w = m.w; val a = m.a
    d.tablas.forEachIndexed { i, t ->
        listOf(m.posteI, m.posteD).forEachIndexed { s, p ->
            val izq = s == 0
            val k = m.k(Offset(p.center.x, t.center.y)); val f = m.f(Offset(p.center.x, t.center.y))
            // La espiga, de testa: sale por fuera del poste.
            val esp = if (izq) Rect(w * 0.004f, t.top + t.height * 0.16f, p.left + 1f, t.bottom - t.height * 0.16f)
                      else Rect(p.right - 1f, t.top + t.height * 0.16f, w * 0.996f, t.bottom - t.height * 0.16f)
            drawRect(a.s(0.35f), Offset(esp.left + 1.6f * u, esp.top + 1.8f * u), esp.size)
            drawRect(a.c(mezcla(ZAPOTE, ZAPOTE_LUZ, 0.3f), k, f), esp.topLeft, esp.size)
            drawRect(a.b(0.4f, k), esp.topLeft, Size(esp.width, 1.2f * u))
            drawRect(a.s(0.3f), Offset(esp.left, esp.bottom - 1.4f * u), Size(esp.width, 1.4f * u))
            // La cabeza de la cuña, metida de arriba en la espiga (no asoma por debajo), algo ladeada; y la testa de la
            // espiga con dos anillos de la madera.
            val cx = esp.center.x + (if (izq) -1f else 1f) * w * 0.002f
            val cuna = poligonoMeso(cx - w * 0.0065f, esp.top - w * 0.011f, cx + w * 0.004f, esp.top - w * 0.013f, cx + w * 0.003f, esp.top + esp.height * 0.45f, cx - w * 0.0035f, esp.top + esp.height * 0.45f)
            drawPath(corridoMeso(cuna, 1.4f * u, 1.2f * u), a.s(0.35f))
            drawPath(cuna, a.c(mezcla(ZAPOTE_LUZ, MesoTinta.ESTUCO, 0.2f), k, f))
            drawPath(bordeMeso(cuna, w * 0.003f, 0f), a.b(0.3f, k))
            drawRect(a.s(0.35f), Offset(cx - w * 0.006f, esp.top), Size(w * 0.011f, 1.2f * u))
            drawArc(a.s(0.3f), 100f, 160f, false, Offset(esp.left + esp.width * 0.15f, esp.top + esp.height * 0.55f), Size(esp.width * 0.5f, esp.height * 0.4f), style = Stroke(0.6f * u))
            // El amarre de henequén encima de la tabla: cinco vueltas gruesas que abultan más que el poste, cada una con su luz
            // arriba y su sombra abajo, un poco ladeadas; el nudo de un lado y la punta que cuelga.
            val vueltas = 5
            val gr = w * 0.0095f
            val am = Rect(p.left - w * 0.008f, t.top - w * 0.006f - gr * vueltas, p.right + w * 0.008f, t.top - w * 0.006f)
            drawRoundRect(a.s(0.4f), Offset(am.left + 1.6f * u, am.top + 2f * u), am.size, CornerRadius(gr / 2))
            for (v in 0 until vueltas) {
                val y = am.top + gr * v
                val ladeo = (if (v % 2 == 0) 1f else -1f) * w * 0.002f
                val vuelta = Rect(am.left + ladeo, y, am.right + ladeo, y + gr * 1.08f)
                drawRoundRect(a.c(HENEQUEN, k, f), vuelta.topLeft, vuelta.size, CornerRadius(gr / 2))
                drawRoundRect(a.b(0.4f, k), Offset(vuelta.left + gr * 0.4f, y + gr * 0.12f), Size(vuelta.width - gr * 0.8f, gr * 0.25f), CornerRadius(gr * 0.12f))
                drawRoundRect(a.s(0.3f), Offset(vuelta.left, y + gr * 0.62f), Size(vuelta.width, gr * 0.42f), CornerRadius(gr * 0.2f))
                // La torsión de la fibra: rayitas en diagonal.
                val fibra = Path()
                var x = vuelta.left + gr * 0.3f; var q = 0
                while (x < vuelta.right - gr * 0.3f && q < 20) { fibra.moveTo(x, y + gr * 0.15f); fibra.lineTo(x + gr * 0.5f, y + gr * 0.9f); x += gr * 0.9f; q++ }
                drawPath(fibra, a.c(HENEQUEN_OSC, k, f).copy(alpha = 0.6f), style = Stroke(0.6f * u))
            }
            // El nudo de un lado y la punta suelta que cuelga.
            val nudo = Offset(if (izq) am.right - gr * 0.2f else am.left + gr * 0.2f, am.top + gr * 2.5f)
            drawCircle(a.s(0.35f), gr * 0.95f, nudo + Offset(1.2f * u, 1.4f * u))
            drawCircle(a.c(mezcla(HENEQUEN, HENEQUEN_OSC, 0.3f), k, f), gr * 0.9f, nudo)
            drawCircle(a.b(0.45f, k), gr * 0.3f, nudo - Offset(gr * 0.3f, gr * 0.3f))
            val pu = nudo + Offset(0f, gr * 0.6f)
            drawLine(a.c(HENEQUEN, k, f), pu, pu + Offset(if (izq) w * 0.008f else -w * 0.008f, w * 0.03f), 1.6f * u, StrokeCap.Round)
            drawLine(a.c(HENEQUEN_OSC, k, f), pu + Offset(if (izq) w * 0.002f else -w * 0.002f, 0f), pu + Offset(if (izq) w * 0.012f else -w * 0.012f, w * 0.024f), 1.2f * u, StrokeCap.Round)
        }
    }
}

/** El rótulo de cada mes: una tira de amate clavada al canto de la tabla con dos espinas, con su filete rojo y el mes escrito. */
internal fun DrawScope.rotuloMeso(m: MesaMeso, p: PlacaEstante, medidor: TextMeasurer) {
    val u = density
    val a = m.a
    val r = p.r
    if (r.width < 6f || r.height < 4f) return
    val k = m.k(r.center)
    val tira = contornoMeso(r.inflate(0.5f * u), 751, 0.035f)
    drawPath(corridoMeso(tira, 1.4f * u, 1.8f * u), a.s(0.45f))
    drawPath(tira, a.etiqueta(k, AMATE))
    clipPath(tira) {
        val fibras = Path()
        for (j in 0 until 6) { val y = r.top + r.height * azar(j, 752); fibras.moveTo(r.left + r.width * azar(j, 753), y); fibras.lineTo(r.left + r.width * azar(j, 754), y + (azar(j, 755) - 0.5f) * 3f * u) }
        drawPath(fibras, a.c(AMATE_OSC, maxOf(k, 0.5f)).copy(alpha = 0.3f), style = Stroke(0.6f * u))
        drawRect(a.b(0.5f, k), r.topLeft, Size(r.width, 1f * u))
    }
    drawRect(a.rubrica.copy(alpha = 0.85f), Offset(r.left + 2.6f * u, r.top + 1.8f * u), Size(r.width - 5.2f * u, r.height - 3.6f * u), style = Stroke(0.8f * u))
    // Las dos espinas que la clavan.
    listOf(r.left + 1.4f * u, r.right - 1.4f * u).forEach { x ->
        drawCircle(a.c(MesoTinta.TINTA, 0.4f), 1.3f * u, Offset(x, r.center.y))
        drawCircle(a.b(0.5f, k), 0.5f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u))
    }
    textoEn(medidor, p.texto, Rect(r.left + 4f * u, r.top + 0.8f * u, r.right - 4f * u, r.bottom - 0.6f * u), MuebleEstanteMeso.LETRA, a.tinta, 10.5f,
        peso = FontWeight.SemiBold, minimo = 6f)
}

// ---------------------------------------------------------------- abajo (pintado una vez)

/**
 * Lo de abajo del anaquel: el faldón de la última tabla, las patas de los postes y sus zapatas de caliza labrada (la madera
 * no toca el piso), con la sombra en el piso.
 */
internal fun DrawScope.patasMeso(m: MesaMeso, fondo: Float) {
    val u = density
    val w = m.w; val a = m.a
    val zap = m.posteI.bottom
    // El faldón: una tabla más baja entre los postes, con su borde de abajo en escalón.
    val fal = Rect(m.posteI.right - 2f, fondo - 1f, m.posteD.left + 2f, fondo + w * 0.03f)
    drawRect(a.s(0.45f), Offset(fal.left, fal.bottom), Size(fal.width, w * 0.012f))
    conLuzMeso(m, mezcla(ZAPOTE, ZAPOTE_OSC, 0.25f), fal, vertical = false)
    vetasMeso(fal, a.c(ZAPOTE_OSC, 0.4f).copy(alpha = 0.3f), 761)
    drawRect(a.s(0.3f), fal.topLeft, Size(fal.width, 2.4f * u))
    drawRect(a.b(0.3f, m.k(Offset(w * 0.5f, fal.bottom))), Offset(fal.left, fal.bottom - 1.2f * u), Size(fal.width, 1.2f * u))
    val mid = w * 0.5f
    val muesca = poligonoMeso(mid - w * 0.08f, fal.bottom, mid - w * 0.06f, fal.bottom - w * 0.008f, mid + w * 0.06f, fal.bottom - w * 0.008f, mid + w * 0.08f, fal.bottom)
    drawPath(muesca, a.c(MesoTinta.CINABRIO_OSC, m.k(Offset(mid, fal.bottom))))
    drawPath(bordeMeso(muesca, 0f, 1.2f * u), a.s(0.4f))
    postesMeso(m, fondo, zap)
    // Las zapatas de caliza.
    listOf(m.posteI, m.posteD).forEachIndexed { i, p ->
        val z = Rect(p.left - w * 0.012f, zap - 1f, p.right + w * 0.012f, m.piso + w * 0.014f)
        val k = m.k(z.center); val f = m.f(z.center)
        drawOval(a.s(0.45f), Offset(z.left - w * 0.004f, z.bottom - w * 0.008f), Size(z.width + w * 0.03f, w * 0.016f))
        drawRect(a.c(CALIZA, k, f), z.topLeft, z.size)
        drawRect(a.c(mezcla(CALIZA, ESTUCO, 0.5f), k, f), z.topLeft, Size(z.width, w * 0.006f))
        drawRect(a.s(0.25f), Offset(z.right - z.width * 0.3f, z.top), Size(z.width * 0.3f, z.height))
        val pic = Path()
        for (j in 0 until 7) { val x = z.left + z.width * azar(i * 9 + j, 762); val y = z.top + z.height * azar(i * 9 + j, 763); pic.moveTo(x, y); pic.lineTo(x + 2f * u, y + 1.2f * u) }
        drawPath(pic, a.c(CALIZA_OSC, k, f), style = Stroke(0.8f * u))
        drawRect(a.s(0.4f), Offset(p.left, zap), Size(p.width, 1.4f * u))
    }
}
