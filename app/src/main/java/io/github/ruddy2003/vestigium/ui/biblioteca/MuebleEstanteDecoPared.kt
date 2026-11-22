package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.APL_X
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.APL_Y
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.PUERTA_X
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.PUERTA_Y
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LLAMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.PANTALLA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.PAPEL
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Art déco: la pared del club y lo que cuelga de ella
// =====================================================================

/** Dónde se ve la pared: arriba, sobre la cornisa; a los lados del armario; y abajo, entre las patas, hasta el suelo. */
private fun zonasPared(g: GeoDeco): List<Rect> = listOf(
    Rect(0f, 0f, g.w, g.cornisaArriba + 1f),
    Rect(0f, g.cornisaArriba, g.costado + 1f, g.pie),
    Rect(g.w - g.costado - 1f, g.cornisaArriba, g.w, g.pie),
    Rect(0f, g.cajones, g.w, g.suelo + 1f),
)

/**
 * La pared: papel pintado oro viejo con su dibujo de abanicos (una baldosa repetida) en un entrepaño con su moldura dorada,
 * la luz de la escalera arriba a la izquierda, la moldura escalonada de yeso del techo con su filete de latón y su sombra, el
 * riel de colgar y, abajo, entre las patas, el zócalo de laca oscura con su filete. De noche, la pared se hunde en su propio
 * color con la distancia al aplique (un solo degradado) y el aplique la tiñe de llama; al alba, el rosa baja por la
 * escalera; en los Sueños en oscuro, el haz de la calle la cruza en diagonal.
 */
internal fun DrawScope.paredDeco(g: GeoDeco) {
    val a = g.a; val w = g.w; val u = g.u
    val zonas = zonasPared(g)
    for ((i, r) in zonas.withIndex()) {
        if (r.width <= 0.5f || r.height <= 0.5f) continue
        if (i == zonas.size - 1) {
            // El zócalo de laca, entre las patas.
            macasarDeco(g, r, 1801, vertical = false, k = 1f, veta = 0.3f)
            veloDeco(g, r, PaletaDeco.MACASAR, vertical = false, paso = w / 10f)
            fileteDeco(g, 0f, w, r.top + w * 0.02f, 1.6f * u, 6)
            continue
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            // De día, la luz de la escalera en un degradado; de noche el papel va liso: el velo de la luz ya lo modela.
            if (!a.lumbre) drawRect(Brush.linearGradient(listOf(mezcla(PAPEL, MARFIL, 0.45f), PAPEL, mezcla(PAPEL, PaletaDeco.PAPEL_OSC, 0.55f)),
                start = Offset(0f, 0f), end = Offset(w, w * 1.1f)), r.topLeft, r.size)
            else drawRect(PAPEL, r.topLeft, r.size)
            texturaDeco(TexturaDeco.PAPEL, r, Color.White, 0.6f)
            veloPared(g, r)
        }
    }
    // La moldura escalonada del techo: tres gradas de yeso con su filete de latón y su sombra en el papel.
    val m1 = w * 0.016f; val m2 = w * 0.03f; val m3 = w * 0.042f
    val yeso = PaletaDeco.MARFIL
    val tramos = if (a.luna) 72 else if (a.lumbre) 48 else 1
    for (i in 0 until tramos) {
        val xa = w * i / tramos; val xb = w * (i + 1) / tramos + 0.6f
        val k = g.k(Offset((xa + xb) / 2, m2)) * (if (a.luna) 0.5f else 1f)
        drawRect(a.c(yeso, k), Offset(xa, 0f), Size(xb - xa, m1))
        drawRect(a.c(mezcla(yeso, PaletaDeco.PAPEL_OSC, 0.28f), k), Offset(xa, m1), Size(xb - xa, m2 - m1))
        drawRect(a.c(mezcla(yeso, PaletaDeco.PAPEL_OSC, 0.12f), k), Offset(xa, m2), Size(xb - xa, m3 - m2))
    }
    drawRect(a.b(0.4f), Offset(0f, m1), Size(w, 1f * u))
    drawRect(a.b(0.35f), Offset(0f, m2), Size(w, 1f * u))
    drawRect(a.s(0.2f), Offset(0f, m1 - 1.2f * u), Size(w, 1.2f * u))
    fileteDeco(g, 0f, w, m3, 2f * u, 8)
    // El entrepaño: una moldura dorada de dos filetes que enmarca el papel, de lado a lado y hasta la cornisa.
    val ep = Rect(w * 0.03f, w * 0.105f, w * 0.97f, g.cornisaArriba + 2f * u)
    for ((d, gr) in listOf(0f to 2.2f * u, 5f * u to 1f * u)) {
        fileteDeco(g, ep.left + d, ep.right - d, ep.top + d, gr, 8, LATON_OSC)
        fileteVDeco(g, ep.left + d, ep.top + d, ep.bottom, gr, 6, LATON_OSC)
        fileteVDeco(g, ep.right - d - gr, ep.top + d, ep.bottom, gr, 6, LATON_OSC)
    }
    drawRect(a.s(0.25f), Offset(ep.left + 2.2f * u, ep.top + 2.2f * u), Size(ep.width - 4.4f * u, 2f * u))
    drawRect(a.s(0.22f), Offset(0f, m3 + 3f * u), Size(w, w * 0.012f))
    drawRect(a.s(0.1f), Offset(0f, m3 + 3f * u + w * 0.012f), Size(w, w * 0.014f))
    // El riel de colgar, de latón, un poco más abajo.
    fileteDeco(g, 0f, w, w * 0.085f, 1.4f * u, 8, LATON_OSC)
}

/**
 * La luz sobre un trozo de pared [r] que ya está pintado con sus colores de día: de noche, un degradado que la hunde en su
 * color hondo con la distancia al aplique (el mismo alcance que el resto del mueble) y la llama que la tiñe cerca; al alba,
 * el aire lila y el rosa de la escalera; con la calle, el frío de la escalera y lo demás hundido.
 */
private fun DrawScope.veloPared(g: GeoDeco, r: Rect) {
    val a = g.a; val w = g.w
    if (!a.lumbre) return
    if (a.alba) {
        drawRect(Color(0xFF9F86B0).copy(alpha = 0.42f), r.topLeft, r.size, blendMode = BlendMode.Multiply)
        mancha(Offset(w * PUERTA_X, w * PUERTA_Y), w * 1.1f, PaletaDeco.ALBA.copy(alpha = 0.4f))
        mancha(Offset(w * APL_X, w * APL_Y), w * 0.5f, LLAMA.copy(alpha = 0.3f))
        return
    }
    val hondo = a.hondo(PAPEL)
    if (a.luna) { hazEnPared(g, r, hondo); return }
    val centro = Offset(w * APL_X, w * APL_Y)
    val radio = w * 1.6f
    // Las paradas del degradado: lo que la luz deja ver a cada distancia, con el mismo alcance que el resto del mueble.
    // En la pared, que tiene el aplique encima, la luz cae más deprisa que en el mueble: el charco junto al vidrio, y lejos, oscuro.
    val paradas = listOf(0f, 0.03f, 0.07f, 0.12f, 0.2f, 0.3f, 0.45f, 0.65f, 1f).map { f ->
        val dist = f * 1.6f
        val k = if (a.luna) (1.1f * kotlin.math.exp(-3.0f * dist)).coerceIn(0.04f, 1f) else (1.35f * kotlin.math.exp(-3.6f * dist)).coerceIn(0.03f, 1f)
        f to hondo.copy(alpha = (1f - (1.05f * k).coerceAtMost(0.95f)).coerceIn(0f, 1f))
    }
    drawRect(Brush.radialGradient(*paradas.toTypedArray(), center = centro, radius = radio), r.topLeft, r.size)
    // El charco de la llama, solo en el trozo de pared que tiene cerca (arriba): abajo, entre las patas, no llega.
    if (r.top < centro.y + w * 0.45f) {
        mancha(centro, w * 0.45f, LLAMA.copy(alpha = 0.34f))
        mancha(centro, w * 0.2f, LLAMA.copy(alpha = 0.34f))
    }
}

/**
 * Los Sueños en oscuro: la pared en la sombra de la madrugada, salvo el haz de la calle, que entra por la puerta de la
 * escalera y la cruza en diagonal, frío, ancho y con el borde blando ([MuebleEstanteDeco.bordeHaz]), cada vez más débil hacia
 * abajo. Son tres degradados: uno de través al haz (lo que ilumina), uno con su frío, y uno a lo alto que hunde lo de abajo.
 */
private fun DrawScope.hazEnPared(g: GeoDeco, r: Rect, hondo: Color) {
    val w = g.w
    val s = MuebleEstanteDeco.HAZ_SESGO
    val norma = kotlin.math.sqrt(1f + s * s)
    // El degradado corre de través al haz: a lo largo del haz no cambia. A [dx] anchos del medio (a lo ancho), [dx]/norma de través.
    val alcance = 0.42f
    val y0 = w * 0.25f
    val centro = Offset((MuebleEstanteDeco.HAZ_X + s * y0 / w) * w, y0)
    val n = Offset(1f / norma, -s / norma)
    val p = alcance * w / norma
    val ini = Offset(centro.x - n.x * p, centro.y - n.y * p)
    val fin = Offset(centro.x + n.x * p, centro.y + n.y * p)
    val caida = kotlin.math.exp(-0.85f * 0.25f)
    val fr = List(13) { it / 12f }
    val luz = fr.map { f ->
        val k = 0.04f + 0.92f * MuebleEstanteDeco.bordeHaz((2f * f - 1f) * alcance) * caida
        f to hondo.copy(alpha = (1f - (1.05f * k).coerceAtMost(0.95f)).coerceIn(0f, 1f))
    }
    drawRect(Brush.linearGradient(*luz.toTypedArray(), start = ini, end = fin), r.topLeft, r.size)
    val frio = fr.map { f -> f to PaletaDeco.LUNA.copy(alpha = 0.16f * MuebleEstanteDeco.bordeHaz((2f * f - 1f) * alcance)) }
    drawRect(Brush.linearGradient(*frio.toTypedArray(), start = ini, end = fin), r.topLeft, r.size)
    drawRect(Brush.verticalGradient(listOf(hondo.copy(alpha = 0f), hondo.copy(alpha = 0.4f)), startY = 0f, endY = maxOf(1f, g.cornisaArriba)), r.topLeft, r.size)
}

/** La sombra del armario en la pared: de día, a la derecha; de noche, a la izquierda, lejos del aplique. */
internal fun DrawScope.sombraArmarioDeco(g: GeoDeco) {
    val a = g.a
    if (g.izq(Offset(g.w * 0.5f, g.ch))) drawRect(a.s(0.36f), Offset(g.w - g.costado, g.cornisaArriba), Size(g.costado, g.pie - g.cornisaArriba))
    else drawRect(a.s(0.5f), Offset(0f, g.cornisaArriba), Size(g.costado, g.pie - g.cornisaArriba))
}

/**
 * El aplique de abanico de la pared, a la derecha: la placa escalonada de latón, la copa en tres gradas y el vidrio esmerilado
 * en medio círculo con sus costillas de latón. De día, un vidrio lechoso; de noche ARDE: el vidrio casi blanco en el medio y
 * de llama al borde, el abanico de luz que sube por el papel y el charco que baja a la cornisa. En los Sueños en oscuro,
 * apagado: vidrio gris frío con un filo de la luz de la calle.
 */
internal fun DrawScope.apliqueDeco(g: GeoDeco) {
    val a = g.a; val w = g.w; val u = g.u
    val c = Offset(w * APL_X, w * APL_Y)
    val radio = w * 0.082f
    val prendido = a.aplique
    // El abanico de luz que sube por la pared, en tres cuñas, y el charco que baja.
    if (prendido) {
        val fuerza = if (a.alba) 0.5f else 1f
        for ((abre, alfa) in listOf(0.4f to 0.12f, 0.2f to 0.16f)) {
            val cuna = TrazoDeco.poligono(c.x - radio * 0.6f, c.y, c.x - w * abre, maxOf(0f, c.y - w * 0.45f), c.x + w * abre, maxOf(0f, c.y - w * 0.45f), c.x + radio * 0.6f, c.y)
            drawPath(cuna, mezcla(LLAMA, PANTALLA, 0.4f).copy(alpha = alfa * fuerza))
        }
        mancha(Offset(c.x, c.y + w * 0.1f), w * 0.3f, LLAMA.copy(alpha = 0.22f * fuerza))
    }
    // La placa de la pared, escalonada, y su sombra.
    val placa = Rect(c.x - w * 0.02f, c.y - w * 0.006f, c.x + w * 0.02f, c.y + w * 0.085f)
    val kp = if (prendido) 1f else g.k(placa.center)
    val sx = if (prendido) 0f else 3f * u
    drawPath(TrazoDeco.corrido(TrazoDeco.escalon(placa, w * 0.009f), sx, 4f * u), a.s(0.35f))
    drawPath(TrazoDeco.escalon(placa, w * 0.009f), a.c(LATON, kp))
    drawRect(a.b(0.5f, kp), Offset(placa.left + w * 0.006f, placa.top), Size(w * 0.006f, placa.height))
    drawRect(a.s(0.3f), Offset(placa.right - w * 0.008f, placa.top), Size(w * 0.008f, placa.height))
    // La copa, en tres gradas de latón.
    listOf(1f to 0f, 0.62f to 0.009f, 0.3f to 0.018f).forEach { (f, dy) ->
        val r = Rect(c.x - radio * f, c.y + w * dy, c.x + radio * f, c.y + w * (dy + 0.009f))
        drawRect(a.c(LATON, kp), r.topLeft, r.size)
        drawRect(a.b(0.55f, kp), r.topLeft, Size(r.width, 1f * u))
        drawRect(a.s(0.3f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
    }
    // El vidrio.
    val vidrio = Rect(c.x - radio, c.y - radio, c.x + radio, c.y + radio)
    val cuerpo = when {
        prendido -> mezcla(PANTALLA, LLAMA, if (a.alba) 0.35f else 0.18f)
        a.oscuro -> mezcla(mezcla(PaletaDeco.NIQUEL, a.nocheColor, 0.72f), PaletaDeco.LUNA, 0.08f)
        else -> a.c(mezcla(MARFIL, Color(0xFFDCE2E0), 0.5f), 1f)
    }
    if (!prendido) drawArc(a.s(0.3f), 180f, 180f, true, Offset(vidrio.left + 4f * u, vidrio.top + 4f * u), vidrio.size)
    drawArc(cuerpo, 180f, 180f, true, vidrio.topLeft, vidrio.size)
    if (prendido) {
        drawArc(LLAMA.copy(alpha = 0.55f), 180f, 180f, false, Offset(vidrio.left + radio * 0.1f, vidrio.top + radio * 0.1f), Size(radio * 1.8f, radio * 1.8f), style = Stroke(radio * 0.2f))
        drawArc(Color.White.copy(alpha = 0.75f), 180f, 180f, true, Offset(c.x - radio * 0.45f, c.y - radio * 0.45f), Size(radio * 0.9f, radio * 0.9f))
    } else {
        // El esmerilado: la luz del día en la mitad izquierda, la sombra en la derecha.
        drawArc(a.b(0.35f), 180f, 70f, true, vidrio.topLeft, vidrio.size)
        drawArc(a.s(0.16f), 290f, 70f, true, vidrio.topLeft, vidrio.size)
        if (a.oscuro) drawArc(PaletaDeco.LUNA.copy(alpha = 0.7f), 185f, 55f, false, vidrio.topLeft, vidrio.size, style = Stroke(1.2f * u))
    }
    // Las costillas en abanico y el borde escalonado de latón.
    val costillas = Path()
    for (j in 1 until 7) {
        val ang = Math.toRadians(180.0 + 180.0 * j / 7.0)
        costillas.moveTo(c.x + radio * 0.28f * cos(ang).toFloat(), c.y + radio * 0.28f * sin(ang).toFloat())
        costillas.lineTo(c.x + radio * cos(ang).toFloat(), c.y + radio * sin(ang).toFloat())
    }
    val metal = if (prendido) mezcla(LATON_OSC, LLAMA, 0.15f) else a.c(LATON, kp)
    drawPath(costillas, metal, style = Stroke(1.4f * u))
    drawArc(metal, 180f, 180f, false, vidrio.topLeft, vidrio.size, style = Stroke(2f * u))
    drawArc(metal, 180f, 180f, false, Offset(c.x - radio * 0.62f, c.y - radio * 0.62f), Size(radio * 1.24f, radio * 1.24f), style = Stroke(1f * u))
    drawArc(metal, 180f, 180f, true, Offset(c.x - radio * 0.28f, c.y - radio * 0.28f), Size(radio * 0.56f, radio * 0.56f))
    if (!prendido) drawArc(a.b(0.6f, kp), 200f, 50f, false, vidrio.topLeft, vidrio.size, style = Stroke(1f * u))
}

/**
 * La trompeta colgada de su clavija de latón en la pared, un poco ladeada: la boquilla, los tubos con su vuelta, los tres
 * pistones con sus botones de nácar y la campana que se abre, todo de latón con su brillo y su sombra en el papel.
 */
internal fun DrawScope.trompetaDeco(g: GeoDeco) {
    val a = g.a; val w = g.w; val u = g.u
    val l = w * 0.27f
    val o = Offset(w * 0.47f, w * 0.165f)
    val k = g.k(Offset(o.x + l * 0.6f, o.y))
    val izq = g.izq(o)
    val t = l * 0.026f
    // La clavija, el cordón que la sostiene.
    val clavija = Offset(o.x + l * 0.3f, w * 0.1f)
    drawLine(a.c(Color(0xFF3A2A22), k), clavija, Offset(o.x + l * 0.26f, o.y - t), 1.2f * u)
    drawLine(a.c(Color(0xFF3A2A22), k), clavija, Offset(o.x + l * 0.36f, o.y - t), 1.2f * u)
    drawCircle(a.c(LATON_OSC, k), 2.6f * u, clavija)
    drawCircle(a.b(0.6f, k), 1f * u, Offset(clavija.x - 0.8f * u, clavija.y - 0.8f * u))
    rotate(8f, o) {
        val tubos = Path().apply {
            // El tubo de arriba, de la boquilla a la campana.
            moveTo(o.x + l * 0.04f, o.y); lineTo(o.x + l * 0.66f, o.y)
            // La vuelta de abajo, con su curva a la izquierda y a la derecha.
            moveTo(o.x + l * 0.6f, o.y + l * 0.085f); lineTo(o.x + l * 0.22f, o.y + l * 0.085f)
            cubicTo(o.x + l * 0.15f, o.y + l * 0.085f, o.x + l * 0.15f, o.y, o.x + l * 0.22f, o.y)
            moveTo(o.x + l * 0.6f, o.y + l * 0.085f)
            cubicTo(o.x + l * 0.66f, o.y + l * 0.085f, o.x + l * 0.66f, o.y + l * 0.04f, o.x + l * 0.6f, o.y + l * 0.04f)
        }
        val campana = Path().apply {
            moveTo(o.x + l * 0.62f, o.y - t * 0.55f)
            cubicTo(o.x + l * 0.84f, o.y - t * 0.6f, o.x + l * 0.93f, o.y - l * 0.05f, o.x + l, o.y - l * 0.115f)
            lineTo(o.x + l, o.y + l * 0.115f)
            cubicTo(o.x + l * 0.93f, o.y + l * 0.05f, o.x + l * 0.84f, o.y + t * 0.6f, o.x + l * 0.62f, o.y + t * 0.55f)
            close()
        }
        // La sombra en la pared.
        val dx = if (izq) w * 0.016f else -w * 0.016f
        translate(dx, w * 0.014f) {
            drawPath(tubos, a.s(0.26f), style = Stroke(t, cap = StrokeCap.Round))
            drawPath(campana, a.s(0.26f))
        }
        val laton = a.c(LATON, k)
        drawPath(tubos, a.c(LATON_OSC, k), style = Stroke(t * 1.15f, cap = StrokeCap.Round))
        drawPath(tubos, laton, style = Stroke(t * 0.8f, cap = StrokeCap.Round))
        drawPath(TrazoDeco.corrido(tubos, 0f, -t * 0.22f), a.c(LATON_LUZ, k).copy(alpha = 0.8f), style = Stroke(t * 0.25f, cap = StrokeCap.Round))
        // La campana: el cuerpo, su brillo, su sombra y el borde de la boca.
        drawPath(campana, laton)
        drawPath(TrazoDeco.poligono(o.x + l * 0.7f, o.y - t * 0.6f, o.x + l * 0.97f, o.y - l * 0.1f, o.x + l, o.y - l * 0.07f, o.x + l * 0.74f, o.y - t * 0.1f), a.c(LATON_LUZ, k))
        drawPath(TrazoDeco.poligono(o.x + l * 0.72f, o.y + t * 0.3f, o.x + l * 0.98f, o.y + l * 0.06f, o.x + l, o.y + l * 0.115f, o.x + l * 0.7f, o.y + t * 0.6f), a.s(0.25f))
        drawOval(a.c(LATON_OSC, k), Offset(o.x + l * 0.985f, o.y - l * 0.115f), Size(l * 0.03f, l * 0.23f))
        drawOval(a.c(LATON_LUZ, k), Offset(o.x + l * 0.99f, o.y - l * 0.11f), Size(l * 0.014f, l * 0.08f))
        // Los tres pistones, con sus botones.
        for (j in 0 until 3) {
            val px = o.x + l * (0.34f + 0.075f * j)
            val pist = Rect(px, o.y - l * 0.05f, px + l * 0.045f, o.y + l * 0.115f)
            drawRect(laton, pist.topLeft, pist.size)
            drawRect(a.c(LATON_LUZ, k), Offset(pist.left + pist.width * 0.15f, pist.top), Size(pist.width * 0.22f, pist.height))
            drawRect(a.s(0.28f), Offset(pist.right - pist.width * 0.3f, pist.top), Size(pist.width * 0.3f, pist.height))
            drawRect(a.c(LATON_OSC, k), Offset(px - l * 0.004f, pist.top), Size(pist.width + l * 0.008f, l * 0.012f))
            drawRect(a.c(LATON_OSC, k), Offset(px - l * 0.004f, pist.bottom - l * 0.012f), Size(pist.width + l * 0.008f, l * 0.012f))
            drawLine(a.c(LATON, k), Offset(px + pist.width / 2, pist.top), Offset(px + pist.width / 2, pist.top - l * 0.04f), 1.4f * u)
            drawOval(a.c(MARFIL, k), Offset(px - l * 0.002f, pist.top - l * 0.055f), Size(pist.width + l * 0.004f, l * 0.02f))
        }
        // La boquilla.
        drawPath(TrazoDeco.poligono(o.x, o.y - t * 0.9f, o.x + l * 0.06f, o.y - t * 0.4f, o.x + l * 0.06f, o.y + t * 0.4f, o.x, o.y + t * 0.9f), a.c(LATON_LUZ, k))
        destelloDeco(a, Offset(o.x + l * 0.9f, o.y - l * 0.07f), 1.4f * u, k)
    }
}

/**
 * Lo último: la llama del aplique por encima de lo que tiene cerca (el resplandor del vidrio y el reflejo en la tapa del
 * armario), y de noche, abajo a la izquierda, el frío de la escalera; con la calle, el frío arriba a la izquierda.
 */
internal fun DrawScope.luzFinalDeco(g: GeoDeco) {
    val a = g.a; val w = g.w
    val c = Offset(w * APL_X, w * APL_Y)
    if (a.aplique) {
        val f = if (a.alba) 0.5f else 1f
        mancha(c, w * 0.16f, PANTALLA.copy(alpha = 0.35f * f))
    }
}
