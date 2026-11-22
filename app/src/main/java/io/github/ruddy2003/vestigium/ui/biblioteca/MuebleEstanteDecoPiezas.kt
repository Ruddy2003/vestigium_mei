package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.ESTRAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.GRANATE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_MIEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Art déco: lo de encima, lo de los huecos y lo del suelo
// =====================================================================

/** Los colores de las etiquetas de los discos (sin letras): granate, verde, azul de cobalto y crema. */
private val ETIQUETAS_DECO = listOf(Color(0xFF9A2C30), Color(0xFF2F6A52), Color(0xFF2B4C8C), Color(0xFFE0C98C))
/** El nogal del metrónomo y el cuero negro del estuche. */
private val NOGAL = Color(0xFF7A4A28)
private val CUERO = Color(0xFF2A211E)

/** La sombra de una forma en la pared: corrida hacia donde no está la luz y un poco hacia abajo. */
private fun DrawScope.sombraEnParedDeco(g: GeoDeco, p: Path, desde: Offset, alfa: Float = 0.26f) {
    val a = g.a; val w = g.w
    val dx = if (g.izq(desde)) w * 0.02f else -w * (0.02f + 0.03f * g.k(desde))
    translate(dx, w * 0.012f) { drawPath(p, a.s(alfa)) }
}

// ---------------------------------------------------------------- encima

/**
 * Lo de encima del armario, de atrás adelante, apoyado en la tapa: los discos en sus fundas contra la pared, el gramófono
 * portátil abierto (el protagonista), el metrónomo, la taza con su plato y la hoja pautada que cuelga del canto.
 */
internal fun DrawScope.encimaDeco(g: GeoDeco) {
    val w = g.w; val p = g.apoyo
    fundasEncimaDeco(g, Offset(w * 0.5f, p), w * 0.17f)
    gramofonoDeco(g, Rect(w * 0.085f, p - w * 0.105f, w * 0.465f, p))
    metronomoDeco(g, Offset(w * 0.69f, p), w * 0.2f)
    tazaDeco(g, Offset(w * 0.8f, p + g.cara * 0.2f), w * 0.07f)
    hojaColgandoDeco(g)
}

/** Tres discos en sus fundas apoyados en la pared: dos de estraza con el agujero que deja ver la etiqueta y uno con su portada. */
private fun DrawScope.fundasEncimaDeco(g: GeoDeco, pie: Offset, lado: Float) {
    val a = g.a; val u = g.u
    val k = g.k(Offset(pie.x + lado * 0.5f, pie.y - lado * 0.5f))
    for (j in 0 until 3) {
        val l = lado * (1f - 0.1f * j)
        val x = pie.x + j * lado * 0.26f
        val giro = when (j) { 0 -> 9f; 1 -> 4f; else -> -3f }
        rotate(giro, Offset(x + l / 2, pie.y)) {
            val r = Rect(x, pie.y - l, x + l, pie.y)
            sombraEnParedDeco(g, Path().apply { addRect(r) }, r.center, 0.24f)
            if (j == 1) {
                // La portada: un abanico impreso en tres tintas, sin letras.
                drawRect(a.c(Color(0xFF1F3B4D), k), r.topLeft, r.size)
                val c = Offset(r.center.x, r.bottom - l * 0.18f)
                for ((f, col) in listOf(0.62f to Color(0xFFE0B04A), 0.46f to Color(0xFFC4523B), 0.3f to MARFIL)) {
                    drawArc(a.c(col, k), 180f, 180f, true, Offset(c.x - l * f, c.y - l * f), Size(l * f * 2, l * f * 2))
                }
                drawRect(a.c(Color(0xFF1F3B4D), k), Offset(r.left, c.y), Size(l, r.bottom - c.y))
                for (i in 0 until 4) drawRect(a.c(MARFIL, k), Offset(r.left + l * (0.12f + 0.2f * i), r.top + l * 0.1f), Size(l * 0.12f, l * 0.04f))
            } else {
                drawRect(a.c(mezcla(ESTRAZA, MARFIL, 0.2f * j), k), r.topLeft, r.size)
                drawCircle(a.c(TINTA, k), l * 0.22f, r.center)
                drawCircle(a.c(ETIQUETAS_DECO[j % ETIQUETAS_DECO.size], k), l * 0.14f, r.center)
                drawCircle(a.c(TINTA, k), l * 0.015f + 0.6f * u, r.center)
                drawArc(a.b(0.4f, k), 200f, 60f, false, Offset(r.center.x - l * 0.2f, r.center.y - l * 0.2f), Size(l * 0.4f, l * 0.4f), style = Stroke(0.8f * u))
            }
            carasDeco(a, r, k, true, 0.8f)
            drawRect(a.s(0.3f), r.topLeft, r.size, style = Stroke(0.7f * u))
        }
    }
}

/**
 * El gramófono portátil abierto, en [r] (el frente de su caja): la caja de cuero granate con su costura, sus esquineros y su
 * cierre de latón y el asa de cuero; la cubierta vista desde arriba con el plato de fieltro verde y el disco negro con sus
 * surcos y su etiqueta; el brazo de níquel con su pivote y el diafragma sobre el disco; la lata de agujas; la tapa levantada
 * detrás, con su forro acolchado y el disco de repuesto en su pinza; y la manivela con el puño de madera. Es lo que manda.
 */
private fun DrawScope.gramofonoDeco(g: GeoDeco, r: Rect) {
    val a = g.a; val u = g.u; val w = g.w
    val k = g.k(r.center)
    val izq = g.izq(r.center)
    val cubierta = w * 0.05f
    val tapaAlto = w * 0.23f
    val fondo = r.top - cubierta
    // La tapa: echada hacia atrás, más angosta arriba.
    val tapa = TrazoDeco.poligono(r.left + w * 0.012f, fondo, r.left + w * 0.03f, fondo - tapaAlto, r.right - w * 0.03f, fondo - tapaAlto, r.right - w * 0.012f, fondo)
    val silueta = Path().apply { addPath(tapa); addRect(Rect(r.left, fondo, r.right, r.bottom)) }
    sombraEnParedDeco(g, silueta, r.center, 0.3f)
    contactoDeco(a, Offset(r.center.x + (if (izq) 1f else -1f) * w * 0.012f, r.bottom), r.width * 1.06f, w * 0.016f, if (a.oscuro) 0.55f else 0.4f)
    drawPath(tapa, a.c(GRANATE, k))
    clipPath(tapa) { texturaDeco(TexturaDeco.CUERO, Rect(r.left, fondo - tapaAlto, r.right, fondo), a.c(Color.White, maxOf(k, 0.2f)), 0.8f) }
    val forro = TrazoDeco.poligono(r.left + w * 0.024f, fondo, r.left + w * 0.04f, fondo - tapaAlto + w * 0.013f, r.right - w * 0.04f, fondo - tapaAlto + w * 0.013f, r.right - w * 0.024f, fondo)
    val tela = Color(0xFFC79A5A)
    drawPath(forro, a.c(tela, k))
    clipPath(forro) {
        // El acolchado: rombos de pespunte con su botón en cada cruce, y la sombra de la cubierta abajo.
        val x0 = r.left + w * 0.024f; val x1 = r.right - w * 0.024f; val y0 = fondo - tapaAlto; val y1 = fondo
        val paso = (x1 - x0) / 6f
        val costura = Path()
        var i = -6
        while (i < 12) {
            costura.moveTo(x0 + paso * i, y1); costura.lineTo(x0 + paso * (i + 6), y0)
            costura.moveTo(x0 + paso * i, y0); costura.lineTo(x0 + paso * (i + 6), y1)
            i++
        }
        drawPath(costura, a.s(0.22f), style = Stroke(0.8f * u))
        drawPath(TrazoDeco.corrido(costura, 0.8f * u, 0f), a.b(0.2f, k), style = Stroke(0.6f * u))
        drawRect(a.s(0.3f), Offset(x0, y1 - w * 0.02f), Size(x1 - x0, w * 0.02f))
        drawRect(a.b(0.12f, k), Offset(x0, y0), Size((x1 - x0) * 0.3f, y1 - y0))
        // El disco de repuesto, en su pinza de latón.
        val dc = Offset(x0 + (x1 - x0) * 0.58f, y0 + (y1 - y0) * 0.44f)
        val dr = (x1 - x0) * 0.26f
        drawCircle(a.s(0.35f), dr, Offset(dc.x + 2f * u, dc.y + 3f * u))
        drawCircle(a.c(TINTA, k), dr, dc)
        drawCircle(a.b(0.18f, k), dr * 0.8f, dc, style = Stroke(0.7f * u))
        drawCircle(a.b(0.12f, k), dr * 0.6f, dc, style = Stroke(0.6f * u))
        drawCircle(a.c(ETIQUETAS_DECO[2], k), dr * 0.3f, dc)
        drawRect(a.c(LATON, k), Offset(dc.x - w * 0.008f, dc.y + dr * 0.78f), Size(w * 0.016f, dr * 0.5f))
    }
    drawPath(tapa, a.s(0.4f), style = Stroke(0.8f * u))
    drawLine(a.b(0.45f, k), Offset(r.left + w * 0.03f, fondo - tapaAlto), Offset(r.right - w * 0.03f, fondo - tapaAlto), 1.2f * u)
    // La cubierta, vista desde arriba: la plancha oscura con su canto.
    val cub = TrazoDeco.poligono(r.left, r.top, r.left + w * 0.012f, fondo, r.right - w * 0.012f, fondo, r.right, r.top)
    drawPath(cub, a.c(mezcla(MACASAR_NEGRO, GRANATE, 0.3f), k))
    drawRect(a.b(0.25f, k), Offset(r.left, r.top - 1f * u), Size(r.width, 1f * u))
    // El plato con su fieltro verde y el disco.
    val pc = Offset(r.left + r.width * 0.36f, fondo + cubierta * 0.52f)
    val prx = r.width * 0.27f; val pry = cubierta * 0.42f
    drawOval(a.c(Color(0xFF1E1A16), k), Offset(pc.x - prx, pc.y - pry + 1.5f * u), Size(prx * 2, pry * 2))
    drawOval(a.c(Color(0xFF2F5A3E), k), Offset(pc.x - prx, pc.y - pry), Size(prx * 2, pry * 2))
    val drx = prx * 0.9f; val dry = pry * 0.86f
    drawOval(a.c(TINTA, k), Offset(pc.x - drx, pc.y - dry), Size(drx * 2, dry * 2))
    for (f in listOf(0.82f, 0.64f)) drawOval(a.b(0.14f, k), Offset(pc.x - drx * f, pc.y - dry * f), Size(drx * 2 * f, dry * 2 * f), style = Stroke(0.6f * u))
    drawArc(a.b(0.5f, k), 195f, 60f, false, Offset(pc.x - drx * 0.9f, pc.y - dry * 0.9f), Size(drx * 1.8f, dry * 1.8f), style = Stroke(1.2f * u))
    drawOval(a.c(ETIQUETAS_DECO[0], k), Offset(pc.x - drx * 0.3f, pc.y - dry * 0.3f), Size(drx * 0.6f, dry * 0.6f))
    drawCircle(a.c(NIQUEL, k), 1.2f * u, pc)
    // La lata de agujas, abierta, a la derecha de la cubierta.
    val lata = Offset(r.right - r.width * 0.1f, fondo + cubierta * 0.62f)
    drawOval(a.c(Color(0xFF2B4C8C), k), Offset(lata.x - w * 0.016f, lata.y - w * 0.006f), Size(w * 0.032f, w * 0.012f))
    drawOval(a.c(LATON_LUZ, k), Offset(lata.x - w * 0.011f, lata.y - w * 0.004f), Size(w * 0.022f, w * 0.007f))
    // El brazo: el pivote, el tubo de níquel curvo y el diafragma sobre el disco.
    val pivote = Offset(r.right - r.width * 0.22f, fondo + cubierta * 0.25f)
    val diafragma = Offset(pc.x + drx * 0.45f, pc.y + dry * 0.05f)
    val brazo = Path().apply {
        moveTo(pivote.x, pivote.y)
        cubicTo(pivote.x - w * 0.01f, pivote.y - w * 0.045f, diafragma.x + w * 0.05f, diafragma.y - w * 0.05f, diafragma.x + w * 0.014f, diafragma.y - w * 0.016f)
    }
    drawPath(TrazoDeco.corrido(brazo, w * 0.006f, w * 0.01f), a.s(0.4f), style = Stroke(3.4f * u, cap = StrokeCap.Round))
    drawPath(brazo, a.c(NIQUEL_OSC, k), style = Stroke(3.8f * u, cap = StrokeCap.Round))
    drawPath(brazo, a.c(NIQUEL, k), style = Stroke(2.6f * u, cap = StrokeCap.Round))
    drawPath(TrazoDeco.corrido(brazo, -0.6f * u, -0.8f * u), a.c(Color.White, k).copy(alpha = if (a.oscuro) 0.5f * k else 0.85f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    val pv = Rect(pivote.x - w * 0.016f, pivote.y - w * 0.01f, pivote.x + w * 0.016f, pivote.y + w * 0.012f)
    drawOval(a.c(NIQUEL_OSC, k), Offset(pv.left, pv.bottom - w * 0.01f), Size(pv.width, w * 0.012f))
    drawRect(a.c(NIQUEL, k), Offset(pv.left + w * 0.004f, pv.top), Size(pv.width - w * 0.008f, pv.height - w * 0.004f))
    drawRect(a.c(Color.White, k).copy(alpha = if (a.oscuro) 0.4f * k else 0.7f), Offset(pv.left + w * 0.008f, pv.top), Size(w * 0.004f, pv.height - w * 0.004f))
    val dr = w * 0.02f
    drawCircle(a.s(0.35f), dr, Offset(diafragma.x + 2f * u, diafragma.y + 3f * u))
    drawCircle(a.c(NIQUEL, k), dr, diafragma)
    drawCircle(a.c(Color(0xFFD8CFB0), k), dr * 0.72f, diafragma)
    drawCircle(a.c(NIQUEL_OSC, k), dr * 0.72f, diafragma, style = Stroke(0.7f * u))
    drawCircle(a.c(NIQUEL_OSC, k), dr * 0.22f, diafragma)
    destelloDeco(a, Offset(diafragma.x - dr * 0.5f, diafragma.y - dr * 0.5f), 1.2f * u, k)
    destelloDeco(a, Offset(pv.left + w * 0.01f, pv.top + w * 0.004f), 1f * u, k)
    // La caja: el cuero granate con su grano, la luz y la sombra de sus caras, la costura, los esquineros, el cierre y el asa.
    drawRect(a.c(GRANATE, k), r.topLeft, r.size)
    clipRect(r.left, r.top, r.right, r.bottom) {
        texturaDeco(TexturaDeco.CUERO, r, a.c(Color.White, maxOf(k, 0.2f)), 0.9f)
        carasDeco(a, r, k, izq, 1.1f)
        drawRect(a.s(0.3f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
    }
    val m = w * 0.008f
    drawRect(a.c(MARFIL, k).copy(alpha = 0.5f), Offset(r.left + m, r.top + m), Size(r.width - 2 * m, r.height - 2 * m),
        style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u))))
    val esq = w * 0.022f
    for ((x, y) in listOf(r.left to r.top, r.right - esq to r.top, r.left to r.bottom - esq, r.right - esq to r.bottom - esq)) {
        drawRect(a.c(LATON, k), Offset(x, y), Size(esq, esq))
        drawRect(a.c(LATON_LUZ, k), Offset(x, y), Size(esq, 1.2f * u))
        drawRect(a.c(LATON_OSC, k), Offset(x, y + esq - 1.2f * u), Size(esq, 1.2f * u))
        drawCircle(a.c(LATON_OSC, k), 0.9f * u, Offset(x + esq / 2, y + esq / 2))
    }
    val cierre = Rect(r.center.x - w * 0.016f, r.top + r.height * 0.12f, r.center.x + w * 0.016f, r.top + r.height * 0.42f)
    drawRect(a.s(0.35f), Offset(cierre.left + 1.5f * u, cierre.top + 1.5f * u), cierre.size)
    drawPath(TrazoDeco.escalon(cierre, w * 0.005f), a.c(LATON, k))
    drawRect(a.c(LATON_LUZ, k), Offset(cierre.left + w * 0.005f, cierre.top + w * 0.003f), Size(cierre.width - w * 0.01f, w * 0.004f))
    destelloDeco(a, Offset(cierre.left + w * 0.008f, cierre.top + w * 0.006f), 1.1f * u, k)
    // El asa de cuero, colgando del frente, con sus dos herrajes.
    val asa = Rect(r.center.x - r.width * 0.16f, r.top + r.height * 0.5f, r.center.x + r.width * 0.16f, r.top + r.height * 0.9f)
    drawArc(a.s(0.35f), 0f, 180f, false, Offset(asa.left + 1.5f * u, asa.top - asa.height + 2f * u), Size(asa.width, asa.height * 2f), style = Stroke(3.4f * u))
    drawArc(a.c(mezcla(GRANATE, MACASAR_NEGRO, 0.45f), k), 0f, 180f, false, Offset(asa.left, asa.top - asa.height), Size(asa.width, asa.height * 2f), style = Stroke(3.4f * u))
    drawArc(a.b(0.3f, k), 20f, 70f, false, Offset(asa.left, asa.top - asa.height), Size(asa.width, asa.height * 2f), style = Stroke(1f * u))
    for (x in listOf(asa.left, asa.right)) drawRect(a.c(LATON, k), Offset(x - w * 0.007f, asa.top - w * 0.005f), Size(w * 0.014f, w * 0.012f))
    // La manivela, por el costado: el eje de níquel, el brazo y el puño de madera.
    val eje = Offset(r.right, r.top + r.height * 0.45f)
    drawLine(a.c(NIQUEL_OSC, k), eje, Offset(eje.x + w * 0.024f, eje.y), 2.8f * u, StrokeCap.Round)
    drawLine(a.c(NIQUEL, k), eje, Offset(eje.x + w * 0.024f, eje.y), 1.8f * u, StrokeCap.Round)
    drawLine(a.c(NIQUEL, k), Offset(eje.x + w * 0.024f, eje.y), Offset(eje.x + w * 0.024f, eje.y + w * 0.042f), 2.2f * u, StrokeCap.Round)
    drawRoundRect(a.c(MACASAR_NEGRO, k), Offset(eje.x + w * 0.03f, eje.y + w * 0.026f), Size(w * 0.014f, w * 0.034f), CornerRadius(w * 0.007f))
    drawRect(a.b(0.35f, k), Offset(eje.x + w * 0.032f, eje.y + w * 0.03f), Size(w * 0.003f, w * 0.024f))
    if (a.lumbre && !izq) drawRect(a.foco.copy(alpha = (0.55f * k).coerceAtMost(0.5f)), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
}

/** El metrónomo de pirámide, de nogal, con el frente abierto: la escala de marfil y el péndulo de níquel con su pesa de latón. */
private fun DrawScope.metronomoDeco(g: GeoDeco, pie: Offset, alto: Float) {
    val a = g.a; val u = g.u
    val k = g.k(Offset(pie.x, pie.y - alto * 0.5f))
    val izq = g.izq(pie)
    val base = alto * 0.5f; val cima = alto * 0.13f
    val cuerpo = TrazoDeco.poligono(pie.x - base / 2, pie.y, pie.x - cima / 2, pie.y - alto, pie.x + cima / 2, pie.y - alto, pie.x + base / 2, pie.y)
    sombraEnParedDeco(g, cuerpo, pie, 0.28f)
    contactoDeco(a, Offset(pie.x, pie.y), base * 1.25f, alto * 0.05f, 0.45f)
    drawPath(cuerpo, a.c(NOGAL, k))
    clipPath(cuerpo) {
        drawRect(a.b(0.22f, k), Offset(if (izq) pie.x - base / 2 else pie.x, pie.y - alto), Size(base / 2, alto))
        drawRect(a.s(0.28f), Offset(if (izq) pie.x else pie.x - base / 2, pie.y - alto), Size(base / 2, alto))
        val vetas = Path()
        for (j in 0 until 7) { vetas.moveTo(pie.x - base / 2 + base * (0.08f + 0.14f * j), pie.y); vetas.lineTo(pie.x - cima / 2 + cima * (0.15f * j), pie.y - alto) }
        drawPath(vetas, a.c(MACASAR_NEGRO, k).copy(alpha = 0.3f), style = Stroke(0.7f * u))
    }
    // El hueco del frente, la escala de marfil y el péndulo.
    val hueco = TrazoDeco.poligono(pie.x - base * 0.2f, pie.y - alto * 0.1f, pie.x - cima * 0.3f, pie.y - alto * 0.86f, pie.x + cima * 0.3f, pie.y - alto * 0.86f, pie.x + base * 0.2f, pie.y - alto * 0.1f)
    drawPath(hueco, a.c(mezcla(NOGAL, MACASAR_NEGRO, 0.55f), k))
    val escala = Rect(pie.x - base * 0.06f, pie.y - alto * 0.8f, pie.x + base * 0.06f, pie.y - alto * 0.22f)
    drawRect(a.c(MARFIL, k), escala.topLeft, escala.size)
    val marcas = Path()
    for (j in 0 until 9) { val y = escala.top + escala.height * j / 9f; marcas.moveTo(escala.left, y); marcas.lineTo(escala.left + escala.width * (if (j % 2 == 0) 0.6f else 0.35f), y) }
    drawPath(marcas, a.c(TINTA, k).copy(alpha = 0.6f), style = Stroke(0.5f * u))
    val eje = Offset(pie.x, pie.y - alto * 0.14f)
    val punta = Offset(pie.x + alto * 0.17f, pie.y - alto * 0.98f)
    drawLine(a.s(0.35f), Offset(eje.x + 2f * u, eje.y), Offset(punta.x + 2f * u, punta.y + 1f * u), 1.6f * u)
    drawLine(a.c(NIQUEL, k), eje, punta, 1.6f * u, StrokeCap.Round)
    val pesa = Offset(eje.x + (punta.x - eje.x) * 0.6f, eje.y + (punta.y - eje.y) * 0.6f)
    val pr = Rect(pesa.x - alto * 0.045f, pesa.y - alto * 0.035f, pesa.x + alto * 0.045f, pesa.y + alto * 0.035f)
    drawPath(TrazoDeco.escalon(pr, alto * 0.012f), a.c(LATON, k))
    drawRect(a.c(LATON_LUZ, k), Offset(pr.left + pr.width * 0.15f, pr.top + 1f * u), Size(pr.width * 0.3f, pr.height - 2f * u))
    destelloDeco(a, Offset(pr.left + pr.width * 0.25f, pr.top + pr.height * 0.3f), 1f * u, k)
    // El zócalo, escalonado.
    drawRect(a.c(MACASAR_NEGRO, k), Offset(pie.x - base * 0.56f, pie.y - alto * 0.05f), Size(base * 1.12f, alto * 0.05f))
    drawRect(a.b(0.35f, k), Offset(pie.x - base * 0.56f, pie.y - alto * 0.05f), Size(base * 1.12f, 1f * u))
    drawRect(a.c(LATON, k), Offset(pie.x - base * 0.5f, pie.y - alto * 0.065f), Size(base, alto * 0.015f))
    if (a.lumbre && !izq) drawLine(a.foco.copy(alpha = (0.6f * k).coerceAtMost(0.55f)), Offset(pie.x + base / 2, pie.y), Offset(pie.x + cima / 2, pie.y - alto), 1.4f * u)
}

/** La taza de loza con su plato y su cucharilla: marfil con un filete verde y otro de oro; dentro, lo oscuro del café. */
private fun DrawScope.tazaDeco(g: GeoDeco, pie: Offset, ancho: Float) {
    val a = g.a; val u = g.u
    val k = g.k(pie)
    val izq = g.izq(pie)
    contactoDeco(a, Offset(pie.x + (if (izq) 1f else -1f) * ancho * 0.12f, pie.y), ancho * 1.7f, ancho * 0.18f, if (a.oscuro) 0.5f else 0.36f)
    val loza = a.c(MARFIL, k)
    drawOval(loza, Offset(pie.x - ancho * 0.78f, pie.y - ancho * 0.16f), Size(ancho * 1.56f, ancho * 0.24f))
    drawOval(a.s(0.2f), Offset(pie.x - ancho * 0.55f, pie.y - ancho * 0.12f), Size(ancho * 1.1f, ancho * 0.14f))
    drawArc(a.c(Color(0xFFC9A24C), k), 0f, 180f, false, Offset(pie.x - ancho * 0.78f, pie.y - ancho * 0.16f), Size(ancho * 1.56f, ancho * 0.24f), style = Stroke(0.8f * u))
    // La cucharilla, sobre el plato.
    drawLine(a.c(NIQUEL, k), Offset(pie.x + ancho * 0.25f, pie.y - ancho * 0.08f), Offset(pie.x + ancho * 0.75f, pie.y - ancho * 0.02f), 1.4f * u, StrokeCap.Round)
    val taza = Path().apply {
        moveTo(pie.x - ancho * 0.5f, pie.y - ancho * 0.76f)
        lineTo(pie.x + ancho * 0.5f, pie.y - ancho * 0.76f)
        cubicTo(pie.x + ancho * 0.48f, pie.y - ancho * 0.32f, pie.x + ancho * 0.3f, pie.y - ancho * 0.12f, pie.x, pie.y - ancho * 0.12f)
        cubicTo(pie.x - ancho * 0.3f, pie.y - ancho * 0.12f, pie.x - ancho * 0.48f, pie.y - ancho * 0.32f, pie.x - ancho * 0.5f, pie.y - ancho * 0.76f)
        close()
    }
    drawArc(loza, -90f, 180f, false, Offset(pie.x + ancho * 0.36f, pie.y - ancho * 0.66f), Size(ancho * 0.34f, ancho * 0.36f), style = Stroke(ancho * 0.08f))
    drawPath(taza, loza)
    clipPath(taza) {
        drawRect(a.s(0.24f), Offset(if (izq) pie.x + ancho * 0.12f else pie.x - ancho * 0.5f, pie.y - ancho), Size(ancho * 0.4f, ancho))
        drawRect(a.b(0.3f, k), Offset(if (izq) pie.x - ancho * 0.36f else pie.x + ancho * 0.24f, pie.y - ancho), Size(ancho * 0.1f, ancho))
        drawRect(a.c(Color(0xFF2F6A52), k), Offset(pie.x - ancho, pie.y - ancho * 0.62f), Size(ancho * 2f, ancho * 0.06f))
    }
    drawLine(a.c(LATON, k), Offset(pie.x - ancho * 0.5f, pie.y - ancho * 0.75f), Offset(pie.x + ancho * 0.5f, pie.y - ancho * 0.75f), 1f * u)
    drawOval(a.c(Color(0xFF3A2216), k), Offset(pie.x - ancho * 0.45f, pie.y - ancho * 0.8f), Size(ancho * 0.9f, ancho * 0.09f))
    drawOval(a.b(0.4f, k), Offset(pie.x - ancho * 0.2f, pie.y - ancho * 0.79f), Size(ancho * 0.25f, ancho * 0.03f))
}

/** Dos hojas pautadas que se quedaron en la tapa del armario y cuelgan por el canto, a la izquierda: cruzan la línea del mueble. */
private fun DrawScope.hojaColgandoDeco(g: GeoDeco) {
    val a = g.a; val u = g.u; val w = g.w
    val apoyo = g.cornisaArriba - g.cara * 0.5f
    val k = g.k(Offset(w * 0.1f, apoyo))
    for (j in 0 until 2) {
        val x0 = w * (0.026f + 0.012f * j); val x1 = w * (0.16f + 0.01f * j)
        val abajo = g.cornisaArriba + g.losa + w * (0.05f - 0.012f * j)
        val hoja = Path().apply {
            moveTo(x0, apoyo)
            lineTo(x1, apoyo - w * 0.003f)
            cubicTo(x1 + w * 0.012f, apoyo + w * 0.02f, x1 - w * 0.004f, abajo - w * 0.02f, x1 - w * 0.01f, abajo)
            lineTo(x0 + w * 0.014f, abajo + w * 0.005f)
            cubicTo(x0 + w * 0.006f, abajo - w * 0.022f, x0 - w * 0.008f, apoyo + w * 0.022f, x0, apoyo)
            close()
        }
        drawPath(TrazoDeco.corrido(hoja, 3f * u, 4f * u), a.s(0.35f))
        drawPath(hoja, a.c(mezcla(MARFIL, ESTRAZA, 0.1f + 0.1f * j), k))
        clipPath(hoja) {
            val pauta = Path()
            for (gr in 0 until 2) for (l in 0 until 5) {
                val y = apoyo + w * (0.014f + 0.03f * gr) + l * w * 0.0036f
                pauta.moveTo(x0, y); pauta.lineTo(x1, y + w * 0.002f)
            }
            drawPath(pauta, a.c(TINTA, k).copy(alpha = 0.6f), style = Stroke(0.6f * u))
            if (j == 1) {
                val notas = ArrayList<Offset>(10)
                for (i in 0 until 9) notas.add(Offset(x0 + (x1 - x0) * (0.12f + 0.1f * i), apoyo + w * (0.014f + 0.03f * (i % 2)) + w * 0.0036f * (i * 3 % 5)))
                drawPoints(notas, PointMode.Points, a.c(TINTA, k).copy(alpha = 0.85f), 2.4f * u, StrokeCap.Round)
                for (n in notas) drawLine(a.c(TINTA, k).copy(alpha = 0.7f), Offset(n.x + 1f * u, n.y), Offset(n.x + 1f * u, n.y - w * 0.012f), 0.6f * u)
            }
            drawRect(a.s(0.22f), Offset(x0, g.cornisaArriba), Size(x1 - x0, w * 0.012f))
            drawRect(a.b(0.3f, k), Offset(x0, apoyo), Size((x1 - x0) * 0.2f, abajo - apoyo))
        }
    }
}

// ---------------------------------------------------------------- el suelo

/** Lo del suelo, delante del armario: la pila de discos tumbada a la izquierda y el estuche de la trompeta, cortado por el borde. */
internal fun DrawScope.objetosSueloDeco(g: GeoDeco) {
    val w = g.w; val h = g.h
    val piso = h - g.suelo
    if (piso <= 4f) return
    pilaSueloDeco(g, Rect(w * 0.24f, g.suelo + piso * 0.36f, w * 0.45f, g.suelo + piso * 0.8f))
    estucheDeco(g, Rect(w * 0.52f, g.suelo + piso * 0.12f, w * 1.06f, g.suelo + piso * 0.92f))
    taburetePianoDeco(g, Offset(w * 0.07f, h - piso * 0.08f), w * 0.24f)
}

/**
 * El taburete del piano, redondo y de tornillo, cortado por el borde izquierdo y delante del armario: el asiento de terciopelo
 * granate capitoné con su botón, el canto de Macasar con su fila de tachuelas de latón, la columna de tornillo y tres patas
 * abiertas que acaban en garra de latón con su bola de vidrio.
 */
private fun DrawScope.taburetePianoDeco(g: GeoDeco, pies: Offset, ancho: Float) {
    val a = g.a; val u = g.u
    val alto = ancho * 1.0f
    val k = g.k(Offset(pies.x, pies.y - alto * 0.8f))
    val izq = g.izq(pies)
    val cx = pies.x
    val asiento = pies.y - alto
    val rx = ancho / 2; val ry = ancho * 0.13f
    val canto = ancho * 0.12f
    contactoDeco(a, Offset(cx + rx * 0.1f, pies.y), ancho * 1.1f, ancho * 0.1f, 0.5f)
    // Las patas: tres, abiertas, de Macasar, con la garra y la bola.
    val arranque = asiento + canto + alto * 0.36f
    for ((dx, frente) in listOf(-0.42f to false, 0.42f to false, 0f to true)) {
        val fin = Offset(cx + rx * dx * (if (frente) 1f else 1.15f), pies.y - (if (frente) 0f else ancho * 0.07f))
        val pata = Path().apply {
            moveTo(cx - ancho * 0.045f + dx * ancho * 0.05f, arranque)
            quadraticTo(cx + dx * rx * 0.3f - ancho * 0.01f, arranque + (fin.y - arranque) * 0.7f, fin.x - ancho * 0.025f, fin.y - ancho * 0.05f)
            lineTo(fin.x + ancho * 0.025f, fin.y - ancho * 0.05f)
            quadraticTo(cx + dx * rx * 0.3f + ancho * 0.06f, arranque + (fin.y - arranque) * 0.7f, cx + ancho * 0.045f + dx * ancho * 0.05f, arranque)
            close()
        }
        drawPath(pata, a.c(if (frente) PaletaDeco.MACASAR else MACASAR_NEGRO, k))
        drawPath(TrazoDeco.corrido(pata, -0.8f * u, 0f), a.b(0.25f, k), style = Stroke(0.8f * u))
        // La garra de latón y la bola de vidrio.
        drawRect(a.c(LATON, k), Offset(fin.x - ancho * 0.025f, fin.y - ancho * 0.06f), Size(ancho * 0.05f, ancho * 0.025f))
        drawCircle(a.c(Color(0xFFB9D3D6), k), ancho * 0.03f, Offset(fin.x, fin.y - ancho * 0.03f))
        drawCircle(a.b(0.7f, k), ancho * 0.01f, Offset(fin.x - ancho * 0.01f, fin.y - ancho * 0.04f))
    }
    // La columna de tornillo.
    val col = Rect(cx - ancho * 0.05f, asiento + canto, cx + ancho * 0.05f, arranque + ancho * 0.02f)
    drawRect(a.c(LATON_OSC, k), col.topLeft, col.size)
    val rosca = Path()
    var y = col.top
    while (y < col.bottom) { rosca.moveTo(col.left, y + ancho * 0.012f); rosca.lineTo(col.right, y); y += maxOf(1.5f * u, ancho * 0.022f) }
    drawPath(rosca, a.c(LATON_LUZ, k), style = Stroke(0.9f * u))
    drawRect(a.s(0.3f), Offset(col.right - col.width * 0.35f, col.top), Size(col.width * 0.35f, col.height))
    drawRect(a.c(MACASAR_NEGRO, k), Offset(cx - ancho * 0.09f, arranque - ancho * 0.02f), Size(ancho * 0.18f, ancho * 0.05f))
    // El canto del asiento, con sus tachuelas, y el asiento de terciopelo visto desde arriba.
    val cantoR = Rect(cx - rx, asiento, cx + rx, asiento + canto)
    drawRect(a.c(PaletaDeco.MACASAR, k), cantoR.topLeft, cantoR.size)
    drawOval(a.c(PaletaDeco.MACASAR, k), Offset(cx - rx, asiento + canto - ry), Size(rx * 2, ry * 2))
    cilindroDeco(a, Rect(cx - rx, asiento, cx + rx, asiento + canto), k, izq)
    val tachuelas = ArrayList<Offset>(14)
    for (j in 0 until 13) tachuelas.add(Offset(cx - rx * 0.92f + rx * 1.84f * j / 12f, asiento + canto * 0.35f + ry * 0.35f * (1f - kotlin.math.abs(j - 6) / 6f)))
    drawPoints(tachuelas, PointMode.Points, a.c(LATON_LUZ, k), 2.4f * u, StrokeCap.Round)
    val tapa = Rect(cx - rx, asiento - ry, cx + rx, asiento + ry)
    drawOval(a.c(Color(0xFF5E1621), k), tapa.topLeft, tapa.size)
    drawOval(a.c(GRANATE, k), Offset(tapa.left + rx * 0.06f, tapa.top + ry * 0.12f), Size(rx * 1.88f, ry * 1.6f))
    // El capitoné: pliegues hacia el botón y el lustre del terciopelo.
    for (j in 0 until 8) {
        val ang = Math.toRadians(22.5 + 45.0 * j)
        drawLine(a.s(0.3f), Offset(cx, asiento), Offset(cx + rx * 0.85f * kotlin.math.cos(ang).toFloat(), asiento + ry * 0.85f * kotlin.math.sin(ang).toFloat()), 0.8f * u)
    }
    drawOval(a.b(0.25f, k), Offset(cx - rx * 0.7f, asiento - ry * 0.7f), Size(rx * 0.8f, ry * 0.6f))
    drawCircle(a.c(LATON, k), ancho * 0.018f, Offset(cx, asiento))
    val kf = g.fr(Offset(cx + rx, asiento))
    if (kf > 0.02f) drawLine(LUNA.copy(alpha = 0.6f * kf), Offset(cx + rx, asiento), Offset(cx + rx, asiento + canto), 1.4f * u)
}

/** La pila de discos tumbada en el suelo, en sus fundas, un poco desordenada, y uno suelto apoyado en ella. */
private fun DrawScope.pilaSueloDeco(g: GeoDeco, r: Rect) {
    val a = g.a; val u = g.u
    val k = g.k(r.center)
    val n = 7
    val grueso = r.height / n
    contactoDeco(a, Offset(r.center.x + r.width * 0.05f, r.bottom), r.width * 1.15f, grueso * 1.6f, 0.5f)
    for (j in 0 until n) {
        val y = r.bottom - (j + 1) * grueso
        val corre = (azar(991, j) - 0.5f) * r.width * 0.1f
        val col = when (j % 3) { 0 -> ESTRAZA; 1 -> Color(0xFF1F3B4D); else -> mezcla(MARFIL, ESTRAZA, 0.3f) }
        val f = Rect(r.left + corre, y, r.right + corre, y + grueso)
        drawRect(a.c(col, k), f.topLeft, f.size)
        drawRect(a.b(0.4f, k), f.topLeft, Size(f.width, 1f * u))
        drawRect(a.s(0.3f), Offset(f.left, f.bottom - 1f * u), Size(f.width, 1f * u))
        drawRect(a.s(0.25f), Offset(f.right - f.width * 0.2f, f.top), Size(f.width * 0.2f, f.height))
    }
    val kf = g.fr(r.center)
    if (kf > 0.02f) drawRect(LUNA.copy(alpha = 0.5f * kf), Offset(r.left, r.top), Size(1.4f * u, r.height))
}

/**
 * El estuche de la trompeta, tumbado en el suelo y cortado por el borde derecho: cuero negro de grano, con la campana
 * abombada a la derecha y la punta angosta a la izquierda, el canto cosido, la línea de la tapa, el asa de cuero con sus
 * herrajes, dos cierres grandes y la cerradura de latón que brillan, y sus patitas.
 */
private fun DrawScope.estucheDeco(g: GeoDeco, r: Rect) {
    val a = g.a; val u = g.u
    if (r.width <= 4f * u || r.height <= 4f * u) return
    val k = g.k(r.center)
    val izq = g.izq(r.center)
    val x0 = r.left; val x1 = r.right; val y1 = r.bottom; val al = r.height; val la = r.width
    val forma = Path().apply {
        moveTo(x0 + al * 0.3f, y1)
        lineTo(x1 - al * 0.55f, y1)
        cubicTo(x1 - al * 0.1f, y1, x1, y1 - al * 0.25f, x1, y1 - al * 0.5f)
        cubicTo(x1, y1 - al * 0.85f, x1 - al * 0.2f, y1 - al, x1 - al * 0.6f, y1 - al)
        cubicTo(x1 - la * 0.3f, y1 - al, x1 - la * 0.42f, y1 - al * 0.58f, x0 + la * 0.28f, y1 - al * 0.56f)
        lineTo(x0 + al * 0.3f, y1 - al * 0.56f)
        cubicTo(x0, y1 - al * 0.56f, x0, y1, x0 + al * 0.3f, y1)
        close()
    }
    contactoDeco(a, Offset(r.center.x + (if (izq) 1f else -1f) * la * 0.04f, y1), la * 1.06f, al * 0.2f, 0.55f)
    drawPath(forma, a.c(CUERO, k))
    clipPath(forma) {
        texturaDeco(TexturaDeco.CUERO, Rect(x0, y1 - al, x1, y1), a.c(Color.White, maxOf(k, 0.2f)), 1f)
        // La luz de la tapa abombada, el brillo del cuero y la sombra de abajo.
        drawRect(a.b(0.18f, k), Offset(x0, y1 - al), Size(la, al * 0.3f))
        drawRect(a.b(0.3f, k), Offset(x0, y1 - al * 0.82f), Size(la, al * 0.05f))
        drawRect(a.s(0.35f), Offset(x0, y1 - al * 0.2f), Size(la, al * 0.2f))
        // La línea de la tapa, con su costura.
        val tapa = Path().apply {
            moveTo(x0, y1 - al * 0.3f); lineTo(x1 - la * 0.4f, y1 - al * 0.3f)
            cubicTo(x1 - la * 0.25f, y1 - al * 0.3f, x1 - la * 0.15f, y1 - al * 0.42f, x1, y1 - al * 0.42f)
        }
        drawPath(tapa, a.s(0.7f), style = Stroke(1.6f * u))
        drawPath(TrazoDeco.corrido(tapa, 0f, 1.6f * u), a.b(0.3f, k), style = Stroke(0.7f * u))
        drawPath(TrazoDeco.corrido(tapa, 0f, -3f * u), a.c(Color(0xFF6B5A4E), k).copy(alpha = 0.7f),
            style = Stroke(0.7f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.8f * u))))
    }
    drawPath(forma, a.c(Color(0xFF120D0B), k), style = Stroke(1f * u))
    // El asa, arriba de la campana, con sus dos herrajes.
    val asa = Offset(x1 - la * 0.34f, y1 - al * 0.82f)
    drawArc(a.s(0.4f), 180f, 180f, false, Offset(asa.x - la * 0.085f + 2f * u, asa.y - al * 0.34f + 2f * u), Size(la * 0.17f, al * 0.52f), style = Stroke(3.4f * u))
    drawArc(a.c(CUERO, k), 180f, 180f, false, Offset(asa.x - la * 0.085f, asa.y - al * 0.34f), Size(la * 0.17f, al * 0.52f), style = Stroke(3.6f * u))
    drawArc(a.b(0.35f, k), 200f, 60f, false, Offset(asa.x - la * 0.085f, asa.y - al * 0.34f), Size(la * 0.17f, al * 0.52f), style = Stroke(1f * u))
    for (s in listOf(-1f, 1f)) {
        val hx = asa.x + s * la * 0.085f
        drawRect(a.c(LATON, k), Offset(hx - la * 0.012f, asa.y - al * 0.06f), Size(la * 0.024f, al * 0.1f))
        drawRect(a.c(LATON_LUZ, k), Offset(hx - la * 0.012f, asa.y - al * 0.06f), Size(la * 0.024f, 1f * u))
    }
    // Los cierres y la cerradura, de latón, con su brillo.
    for ((f, grande) in listOf(0.2f to false, 0.47f to true, 0.72f to false)) {
        val c = Offset(x0 + la * f, y1 - al * (if (f > 0.6f) 0.4f else 0.3f))
        val an = la * (if (grande) 0.04f else 0.034f); val alt = al * 0.24f
        val cr = Rect(c.x - an / 2, c.y - alt / 2, c.x + an / 2, c.y + alt / 2)
        drawRect(a.s(0.45f), Offset(cr.left + 1.5f * u, cr.top + 1.5f * u), cr.size)
        drawPath(TrazoDeco.escalon(cr, an * 0.2f), a.c(LATON, k))
        drawRect(a.c(LATON_LUZ, k), Offset(cr.left + an * 0.2f, cr.top + 1f * u), Size(an * 0.25f, alt - 2f * u))
        drawRect(a.c(LATON_OSC, k), Offset(cr.left, cr.center.y), Size(an, 1f * u))
        if (grande) drawCircle(a.c(TINTA, 1f), an * 0.14f, Offset(c.x, c.y + alt * 0.18f))
        destelloDeco(a, Offset(cr.left + an * 0.3f, cr.top + alt * 0.25f), 1.3f * u, k)
        val kf = g.fr(c)
        if (kf > 0.02f) drawRect(LUNA.copy(alpha = 0.8f * kf), Offset(cr.left, cr.top), Size(1f * u, alt))
    }
    drawPoints(listOf(Offset(x0 + la * 0.12f, y1), Offset(x0 + la * 0.45f, y1), Offset(x1 - la * 0.2f, y1)), PointMode.Points, a.c(LATON, k), 3f * u, StrokeCap.Round)
    val kf = g.fr(Offset(x0, y1 - al * 0.3f))
    if (kf > 0.02f) drawPath(forma, LUNA.copy(alpha = 0.5f * kf), style = Stroke(1.2f * u))
}
