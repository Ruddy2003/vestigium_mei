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
//  Art déco: lo de los huecos libres de las repisas
// =====================================================================

/**
 * Lo de un hueco libre de la repisa [k], de [oc] al costado, de izquierda a derecha mientras quepa: según la repisa, la pila
 * de discos de canto, la sordina tumbada, la caja de cañas, la lata de agujas, el rollo de partituras con las escobillas o
 * el cenicero con su hilo de humo. De noche, el frío de la escalera los deja ver aunque el aplique no llegue.
 */
internal fun DrawScope.huecoDeco(g: GeoDeco, k: Int, r: Rect, oc: Float) {
    val u = g.u
    val fin = r.right - 8f * u
    if (fin - oc < 22f * u || r.height < 10f * u) return
    val alto = r.height
    val orden = when (Math.floorMod(k, 3)) {
        0 -> listOf(0, 1, 2, 3)
        1 -> listOf(1, 4, 0, 2)
        else -> listOf(4, 0, 5, 1)
    }
    clipRect(r.left, r.top, r.right, r.bottom) {
        var x = oc + 6f * u
        for (pieza in orden) {
            val libre = fin - x
            val pide = when (pieza) {
                0 -> minOf(libre, alto * 0.8f).takeIf { it >= 18f * u } ?: 0f
                1 -> alto * 0.7f
                2 -> alto * 0.5f
                3 -> alto * 0.36f
                4 -> alto * 1.0f
                else -> alto * 0.46f
            }
            if (pide <= 0f || pide > libre) continue
            val sitio = Rect(x, r.bottom - alto, x + pide, r.bottom - 4f * u)
            val kp = maxOf(g.k(sitio.center), 0.5f * g.fr(sitio.center))
            val izq = g.izq(sitio.center)
            when (pieza) {
                0 -> pilaDeCantoDeco(g.a, sitio, kp, izq, k)
                1 -> sordinaDeco(g.a, sitio, kp, izq)
                2 -> cajaCanasDeco(g.a, sitio, kp, izq)
                3 -> lataAgujasDeco(g.a, sitio, kp, izq)
                4 -> rolloEscobillasDeco(g.a, sitio, kp, izq)
                else -> ceniceroDeco(g.a, sitio, kp)
            }
            x += pide + 6f * u
        }
    }
}

/** Los discos de canto, en sus fundas, uno contra otro: el canto de estraza, de marfil o de cartón de color, con el disco que asoma. */
private fun DrawScope.pilaDeCantoDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean, semilla: Int) {
    val u = density
    val n = (s.width / (3.2f * u)).toInt().coerceIn(3, 40)
    val gr = s.width / n
    val alto = s.height * 0.86f
    contactoDeco(a, Offset(s.center.x + (if (izq) 3f else -3f) * u, s.bottom), s.width * 1.15f, 5f * u, 0.5f)
    val pila = Path().apply {
        moveTo(s.left, s.bottom)
        for (i in 0 until n) {
            val y = s.bottom - alto * (0.92f + 0.08f * azar(1201 + semilla, i))
            lineTo(s.left + gr * i, y); lineTo(s.left + gr * (i + 1), y)
        }
        lineTo(s.right, s.bottom); close()
    }
    drawPath(pila, a.c(ESTRAZA, k))
    val claras = Path(); val color = Path(); val discos = Path(); val juntas = Path(); val luces = Path()
    for (i in 0 until n) {
        val xi = s.left + gr * i
        val top = s.bottom - alto * (0.92f + 0.08f * azar(1201 + semilla, i))
        val z = azar(1202 + semilla, i)
        when {
            z < 0.28f -> claras.addRect(Rect(xi, top, xi + gr, s.bottom))
            z < 0.42f -> color.addRect(Rect(xi, top, xi + gr, s.bottom))
        }
        if (azar(1203 + semilla, i) > 0.78f) discos.addRect(Rect(xi + gr * 0.15f, top - alto * 0.05f, xi + gr * 0.85f, top + 1f * u))
        juntas.moveTo(xi, top); juntas.lineTo(xi, s.bottom)
        luces.moveTo(xi + gr * 0.2f, top + 0.8f * u); luces.lineTo(xi + gr * 0.8f, top + 0.8f * u)
    }
    drawPath(claras, a.c(mezcla(MARFIL, ESTRAZA, 0.25f), k))
    drawPath(color, a.c(Color(0xFF1F3B4D), k))
    drawPath(discos, a.c(TINTA, k))
    drawPath(juntas, a.s(0.4f), style = Stroke(0.7f * u))
    drawPath(luces, a.b(0.5f, k), style = Stroke(0.8f * u))
    carasDeco(a, Rect(s.left, s.bottom - alto * 0.92f, s.right, s.bottom), k, izq, 0.9f)
}

/** La sordina recta, tumbada: el cono de aluminio con el fondo ancho y redondo, el aro y las tiras de corcho de la punta. */
private fun DrawScope.sordinaDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val largo = s.width
    val diam = minOf(s.height * 0.42f, largo * 0.5f)
    val eje = s.bottom - diam / 2
    val punta = diam * 0.36f
    contactoDeco(a, Offset(s.center.x + (if (izq) 2f else -2f) * u, s.bottom), largo * 1.1f, 5f * u, 0.5f)
    val cono = Path().apply {
        moveTo(s.left, eje - punta / 2)
        lineTo(s.right - diam * 0.3f, eje - diam / 2)
        quadraticTo(s.right, eje - diam / 2, s.right, eje)
        quadraticTo(s.right, eje + diam / 2, s.right - diam * 0.3f, eje + diam / 2)
        lineTo(s.left, eje + punta / 2)
        close()
    }
    drawPath(cono, a.c(NIQUEL, k))
    clipPath(cono) {
        drawRect(a.b(0.6f, k), Offset(s.left, eje - diam * 0.38f), Size(largo, diam * 0.12f))
        drawRect(a.b(0.25f, k), Offset(s.left, eje - diam * 0.26f), Size(largo, diam * 0.14f))
        drawRect(a.s(0.3f), Offset(s.left, eje + diam * 0.1f), Size(largo, diam * 0.4f))
        drawRect(a.c(NIQUEL_OSC, k), Offset(s.left + largo * 0.64f, eje - diam), Size(1.6f * u, diam * 2f))
    }
    drawOval(a.c(NIQUEL_OSC, k), Offset(s.right - diam * 0.14f, eje - diam * 0.42f), Size(diam * 0.14f, diam * 0.84f))
    val corcho = a.c(Color(0xFFA8794A), k)
    for (dy in listOf(-0.62f, 0.38f)) {
        drawRect(corcho, Offset(s.left + largo * 0.05f, eje + punta * dy), Size(largo * 0.24f, punta * 0.26f))
        drawRect(a.b(0.3f, k), Offset(s.left + largo * 0.05f, eje + punta * dy), Size(largo * 0.24f, 0.8f * u))
    }
    destelloDeco(a, Offset(s.left + largo * 0.5f, eje - diam * 0.3f), 1.2f * u, k)
}

/** La caja de cañas del clarinete, tumbada: cartón verde con su faja de marfil y su canto, y dos cañas sueltas encima. */
private fun DrawScope.cajaCanasDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val alto = s.height * 0.2f
    val caja = Rect(s.left, s.bottom - alto, s.right, s.bottom)
    val tapa = Rect(caja.left, caja.top - alto * 0.3f, caja.right, caja.top)
    contactoDeco(a, Offset(caja.center.x, s.bottom), caja.width * 1.15f, 5f * u, 0.5f)
    drawRect(a.c(mezcla(Color(0xFF2F6A52), Color.White, 0.25f), k), tapa.topLeft, tapa.size)
    drawRect(a.c(Color(0xFF2F6A52), k), caja.topLeft, caja.size)
    drawRect(a.c(MARFIL, k), Offset(caja.left + caja.width * 0.3f, tapa.top), Size(caja.width * 0.2f, caja.bottom - tapa.top))
    drawRect(a.c(LATON, k), Offset(caja.left + caja.width * 0.66f, caja.top + alto * 0.3f), Size(caja.width * 0.18f, alto * 0.14f))
    carasDeco(a, caja, k, izq)
    val canas = Path().apply {
        moveTo(tapa.left + tapa.width * 0.1f, tapa.top - 1.2f * u); lineTo(tapa.left + tapa.width * 0.62f, tapa.top - 2.4f * u)
        moveTo(tapa.left + tapa.width * 0.3f, tapa.top - 3.4f * u); lineTo(tapa.left + tapa.width * 0.86f, tapa.top - 1.6f * u)
    }
    drawPath(canas, a.c(Color(0xFFD8BE7A), k), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    drawPath(TrazoDeco.corrido(canas, 0f, -0.6f * u), a.b(0.5f, k), style = Stroke(0.6f * u, cap = StrokeCap.Round))
}

/** La lata de agujas del gramófono: baja y redonda, azul con la tapa de latón pintada (un abanico, sin letras). */
private fun DrawScope.lataAgujasDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val la = s.width
    val lata = Rect(s.left, s.bottom - la * 0.62f, s.right, s.bottom)
    contactoDeco(a, Offset(lata.center.x, s.bottom), la * 1.3f, la * 0.16f, 0.5f)
    drawRect(a.c(Color(0xFF2B4C8C), k), lata.topLeft, lata.size)
    drawArc(a.c(MARFIL, k), 180f, 180f, true, Offset(lata.center.x - la * 0.26f, lata.top + la * 0.14f), Size(la * 0.52f, la * 0.52f))
    drawArc(a.c(Color(0xFFC4523B), k), 180f, 180f, true, Offset(lata.center.x - la * 0.14f, lata.top + la * 0.26f), Size(la * 0.28f, la * 0.28f))
    cilindroDeco(a, lata, k, izq)
    drawOval(a.c(LATON, k), Offset(lata.left, lata.top - la * 0.1f), Size(la, la * 0.2f))
    drawOval(a.c(LATON_LUZ, k), Offset(lata.left + la * 0.15f, lata.top - la * 0.07f), Size(la * 0.5f, la * 0.08f))
}

/** El cenicero de níquel con el cigarrillo apoyado y su hilo de humo (el humo del club). */
private fun DrawScope.ceniceroDeco(a: AireDeco, s: Rect, k: Float) {
    val u = density
    val an = s.width; val cx = s.center.x; val suelo = s.bottom
    contactoDeco(a, Offset(cx, suelo), an * 1.2f, an * 0.12f, 0.5f)
    drawOval(a.c(NIQUEL_OSC, k), Offset(cx - an / 2, suelo - an * 0.24f), Size(an, an * 0.16f))
    drawRect(a.c(NIQUEL, k), Offset(cx - an / 2, suelo - an * 0.16f), Size(an, an * 0.16f))
    drawRect(a.b(0.6f, k), Offset(cx - an * 0.4f, suelo - an * 0.15f), Size(an * 0.1f, an * 0.14f))
    drawRect(a.s(0.3f), Offset(cx + an * 0.2f, suelo - an * 0.16f), Size(an * 0.3f, an * 0.16f))
    drawLine(a.c(MARFIL, k), Offset(cx - an * 0.1f, suelo - an * 0.22f), Offset(cx + an * 0.45f, suelo - an * 0.32f), 2.2f * u)
    drawCircle(Color(0xFFE8763C).copy(alpha = if (a.lumbre) 0.95f else 0.7f), 1.4f * u, Offset(cx + an * 0.46f, suelo - an * 0.32f))
    val humo = Path().apply {
        moveTo(cx + an * 0.46f, suelo - an * 0.34f)
        cubicTo(cx + an * 0.62f, suelo - s.height * 0.42f, cx + an * 0.08f, suelo - s.height * 0.58f, cx + an * 0.36f, suelo - s.height * 0.86f)
    }
    drawPath(humo, a.b(0.5f, maxOf(k, 0.4f)).copy(alpha = if (a.oscuro) 0.25f else 0.4f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
}

/** El rollo de partituras, tumbado y atado con cordel rojo, y las escobillas de la batería apoyadas detrás, en diagonal. */
private fun DrawScope.rolloEscobillasDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val alto = s.height * 0.34f
    val largo = s.width
    val suelo = s.bottom
    val e0 = Offset(s.left + largo * 0.12f, suelo - alto * 0.5f)
    val e1 = Offset(s.left + largo * 0.85f, suelo - s.height * 0.9f)
    val m = Offset(e0.x + (e1.x - e0.x) * 0.55f, e0.y + (e1.y - e0.y) * 0.55f)
    drawLine(a.c(MACASAR_NEGRO, k), e0, m, 3f * u, StrokeCap.Round)
    drawLine(a.b(0.35f, k), Offset(e0.x, e0.y - 0.8f * u), Offset(m.x, m.y - 0.8f * u), 0.7f * u)
    drawRect(a.c(LATON, k), Offset(m.x - 2f * u, m.y - 2f * u), Size(4f * u, 4f * u))
    val abanico = Path()
    for (j in 0 until 9) { abanico.moveTo(m.x, m.y); abanico.lineTo(e1.x + (j - 4) * 1.6f * u, e1.y + (j - 4) * (j - 4) * 0.4f * u) }
    drawPath(abanico, a.c(NIQUEL, k), style = Stroke(0.7f * u))
    val rollo = Rect(s.left, suelo - alto, s.left + largo, suelo)
    contactoDeco(a, Offset(rollo.center.x, suelo), largo, alto * 0.25f, 0.5f)
    drawRoundRect(a.c(mezcla(MARFIL, ESTRAZA, 0.18f), k), rollo.topLeft, rollo.size, CornerRadius(alto * 0.3f))
    clipRect(rollo.left, rollo.top, rollo.right, rollo.bottom) {
        val pauta = Path()
        for (l in 0 until 5) { val y = rollo.top + alto * (0.3f + 0.08f * l); pauta.moveTo(rollo.left + alto * 0.3f, y); pauta.lineTo(rollo.right - alto * 0.5f, y) }
        drawPath(pauta, a.c(TINTA, k).copy(alpha = 0.3f), style = Stroke(0.5f * u))
        drawRect(a.b(0.4f, k), Offset(rollo.left + alto * 0.3f, rollo.top + alto * 0.1f), Size(largo - alto * 0.6f, alto * 0.14f))
        drawRect(a.s(0.3f), Offset(rollo.left, rollo.bottom - alto * 0.32f), Size(largo, alto * 0.32f))
        // El extremo que mira a la luz, más claro; el otro, en sombra.
        drawRect(if (izq) a.b(0.2f, k) else a.s(0.2f), Offset(rollo.left, rollo.top), Size(alto * 0.4f, alto))
    }
    drawOval(a.c(mezcla(MARFIL, ESTRAZA, 0.4f), k), Offset(rollo.right - alto * 0.5f, rollo.top), Size(alto * 0.5f, alto))
    for (f in listOf(0.45f, 0.3f, 0.15f)) drawOval(a.s(0.3f), Offset(rollo.right - alto * 0.25f - alto * f * 0.5f, rollo.center.y - alto * f), Size(alto * f, alto * f * 2f), style = Stroke(0.6f * u))
    val cordel = a.c(Color(0xFF9A2C30), k)
    for (f in listOf(0.3f, 0.68f)) {
        drawLine(cordel, Offset(rollo.left + largo * f, rollo.top), Offset(rollo.left + largo * f, rollo.bottom), 2f * u)
        drawLine(a.b(0.35f, k), Offset(rollo.left + largo * f - 0.6f * u, rollo.top), Offset(rollo.left + largo * f - 0.6f * u, rollo.bottom), 0.6f * u)
    }
    drawCircle(cordel, 2.2f * u, Offset(rollo.left + largo * 0.68f, rollo.top + 1f * u))
}
