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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.ESTRAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Art déco: lo de los huecos libres de las repisas
// =====================================================================

/** Lo que puede ir en un hueco: cada cosa se entiende de un vistazo y es del club. */
private enum class PiezaHueco { DISCOS, SIFON, CENICERO, PARTITURAS, BAQUETAS, CLARINETE, COCTELERA, COPA }

/**
 * Qué va en cada repisa (por su número, de cuatro en cuatro), de izquierda a derecha mientras quepa. Dos listas seguidas (y la
 * última con la primera) no comparten nada, así dos repisas vecinas nunca repiten lo mismo.
 */
private val ORDEN_HUECOS = listOf(
    listOf(PiezaHueco.DISCOS, PiezaHueco.SIFON, PiezaHueco.CENICERO),
    listOf(PiezaHueco.CLARINETE, PiezaHueco.PARTITURAS, PiezaHueco.BAQUETAS),
    listOf(PiezaHueco.COCTELERA, PiezaHueco.COPA, PiezaHueco.DISCOS),
    listOf(PiezaHueco.BAQUETAS, PiezaHueco.PARTITURAS, PiezaHueco.CLARINETE),
)

/** Cuánto ancho pide cada cosa, en altos del hueco. */
private fun PiezaHueco.ancho(): Float = when (this) {
    PiezaHueco.DISCOS -> 1.02f
    PiezaHueco.SIFON -> 0.36f
    PiezaHueco.CENICERO -> 0.5f
    PiezaHueco.PARTITURAS -> 0.9f
    PiezaHueco.BAQUETAS -> 0.5f
    PiezaHueco.CLARINETE -> 0.5f
    PiezaHueco.COCTELERA -> 0.32f
    PiezaHueco.COPA -> 0.36f
}

/**
 * Lo de un hueco libre de la repisa [k], de [oc] al costado, de izquierda a derecha mientras quepa: discos de pie con sus
 * fundas de color, el sifón de soda, el cenicero de cristal con su hilo de humo, las partituras atadas, las baquetas y las
 * escobillas en su lata, el clarinete apoyado, la coctelera de níquel y la copa. Todo grande (llena la altura del hueco) y
 * modelado. De noche, el frío de la escalera los deja ver aunque el aplique no llegue.
 */
internal fun DrawScope.huecoDeco(g: GeoDeco, k: Int, r: Rect, oc: Float) {
    val u = g.u
    val fin = r.right - 8f * u
    if (fin - oc < 22f * u || r.height < 10f * u) return
    val alto = r.height - 4f * u
    clipRect(r.left, r.top, r.right, r.bottom) {
        var x = oc + 6f * u
        for (pieza in ORDEN_HUECOS[Math.floorMod(k, ORDEN_HUECOS.size)]) {
            val pide = alto * pieza.ancho()
            if (pide > fin - x) continue
            val s = Rect(x, r.bottom - 4f * u - alto, x + pide, r.bottom - 4f * u)
            val kp = maxOf(g.k(s.center), 0.5f * g.fr(s.center))
            val izq = g.izq(s.center)
            when (pieza) {
                PiezaHueco.DISCOS -> discosDePieDeco(g.a, s, kp, izq, k)
                PiezaHueco.SIFON -> sifonDeco(g.a, s, kp, izq)
                PiezaHueco.CENICERO -> ceniceroDeco(g.a, s, kp)
                PiezaHueco.PARTITURAS -> partiturasAtadasDeco(g.a, s, kp, izq)
                PiezaHueco.BAQUETAS -> baquetasDeco(g.a, s, kp, izq)
                PiezaHueco.CLARINETE -> clarineteDeco(g.a, s, kp, izq)
                PiezaHueco.COCTELERA -> cocteleraDeco(g.a, s, kp, izq)
                PiezaHueco.COPA -> copaDeco(g.a, s, kp)
            }
            x += pide + 5f * u
        }
    }
}

/**
 * Tres discos de pie en sus fundas, uno delante del otro y un poco ladeados: el de atrás con su portada de abanicos en tres
 * tintas, el del medio de color con bandas escalonadas, el de delante de estraza con el agujero que deja ver la etiqueta.
 */
private fun DrawScope.discosDePieDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean, semilla: Int) {
    val u = density
    val l = s.height * 0.86f
    contactoDeco(a, Offset(s.center.x, s.bottom), s.width * 1.05f, 5f * u, 0.5f)
    val fondos = listOf(Color(0xFF1F3B4D), Color(0xFFB24A36), mezcla(ESTRAZA, MARFIL, 0.1f))
    for (j in 0 until 3) {
        val x = s.left + j * (s.width - l) / 2f
        val giro = when (j) { 0 -> -5f; 1 -> 3f; else -> -1.5f }
        rotate(giro, Offset(x + l / 2, s.bottom)) {
            val r = Rect(x, s.bottom - l, x + l, s.bottom)
            drawRect(a.s(0.35f), Offset(r.left + (if (izq) 3f else -3f) * u, r.top + 3f * u), r.size)
            drawRect(a.c(fondos[(j + semilla) % 3], k), r.topLeft, r.size)
            when ((j + semilla) % 3) {
                0 -> {
                    val c = Offset(r.center.x, r.bottom - l * 0.16f)
                    for ((f, col) in listOf(0.6f to Color(0xFFE0B04A), 0.44f to Color(0xFFC4523B), 0.28f to MARFIL)) {
                        drawArc(a.c(col, k), 180f, 180f, true, Offset(c.x - l * f, c.y - l * f), Size(l * f * 2, l * f * 2))
                    }
                    drawRect(a.c(Color(0xFF1F3B4D), k), Offset(r.left, c.y), Size(l, r.bottom - c.y))
                }
                1 -> for (i in 0 until 3) drawRect(a.c(if (i % 2 == 0) MARFIL else Color(0xFF1F3B4D), k), Offset(r.left, r.top + l * (0.18f + 0.08f * i)), Size(l * (0.9f - 0.18f * i), l * 0.05f))
                else -> {
                    drawCircle(a.c(TINTA, k), l * 0.24f, r.center)
                    drawCircle(a.c(Color(0xFF9A2C30), k), l * 0.15f, r.center)
                    drawCircle(a.c(TINTA, k), l * 0.02f + 0.6f * u, r.center)
                    drawArc(a.b(0.45f, k), 200f, 60f, false, Offset(r.center.x - l * 0.22f, r.center.y - l * 0.22f), Size(l * 0.44f, l * 0.44f), style = Stroke(1f * u))
                }
            }
            carasDeco(a, r, k, izq, 1.2f)
            drawRect(a.s(0.4f), r.topLeft, r.size, style = Stroke(0.8f * u))
        }
    }
}

/**
 * El sifón de soda: el cuerpo de vidrio azul verdoso con sus estrías, el brillo largo y lo oscuro del otro lado, el agua
 * hasta su nivel, y la cabeza de níquel escalonada con la palanca y el pico.
 */
private fun DrawScope.sifonDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val an = s.width * 0.8f
    val cx = s.left + s.width * 0.42f
    val cuerpo = Rect(cx - an / 2, s.bottom - s.height * 0.68f, cx + an / 2, s.bottom)
    contactoDeco(a, Offset(cx, s.bottom), an * 1.3f, 5f * u, 0.5f)
    val vidrio = Color(0xFF5E9AAE)
    drawRoundRect(a.c(vidrio, k).copy(alpha = 0.9f), cuerpo.topLeft, cuerpo.size, CornerRadius(an * 0.22f))
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        drawRect(a.c(Color(0xFF2F6478), k), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.16f), Size(an, cuerpo.height))
        val estrias = Path()
        for (j in 1 until 6) { val x = cuerpo.left + an * j / 6f; estrias.moveTo(x, cuerpo.top); estrias.lineTo(x, cuerpo.bottom) }
        drawPath(estrias, a.b(0.25f, k), style = Stroke(0.7f * u))
        cilindroDeco(a, cuerpo, k, izq, 1.4f)
        drawRect(a.b(0.7f, k), Offset(if (izq) cuerpo.left + an * 0.16f else cuerpo.right - an * 0.24f, cuerpo.top + cuerpo.height * 0.08f), Size(an * 0.08f, cuerpo.height * 0.8f))
        drawRect(a.b(0.45f, k), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.16f), Size(an, 1f * u))
    }
    // La cabeza: el cuello, dos gradas de níquel, la palanca y el pico.
    val cuello = Rect(cx - an * 0.24f, cuerpo.top - s.height * 0.05f, cx + an * 0.24f, cuerpo.top + 2f * u)
    drawRect(a.c(NIQUEL_OSC, k), cuello.topLeft, cuello.size)
    val g1 = Rect(cx - an * 0.36f, cuello.top - s.height * 0.08f, cx + an * 0.36f, cuello.top)
    val g2 = Rect(cx - an * 0.24f, g1.top - s.height * 0.06f, cx + an * 0.24f, g1.top)
    for (gr in listOf(g1, g2)) {
        drawRect(a.c(NIQUEL, k), gr.topLeft, gr.size)
        drawRect(a.b(0.7f, k), Offset(gr.left + gr.width * 0.15f, gr.top), Size(gr.width * 0.12f, gr.height))
        drawRect(a.s(0.35f), Offset(gr.right - gr.width * 0.25f, gr.top), Size(gr.width * 0.25f, gr.height))
    }
    val pico = Path().apply {
        moveTo(g1.right, g1.top + g1.height * 0.3f); lineTo(g1.right + s.width * 0.42f, g1.top + g1.height * 0.45f)
        lineTo(g1.right + s.width * 0.42f, g1.top + g1.height * 0.9f); lineTo(g1.right, g1.bottom); close()
    }
    drawPath(pico, a.c(NIQUEL, k))
    drawLine(a.b(0.7f, k), Offset(g1.right, g1.top + g1.height * 0.36f), Offset(g1.right + s.width * 0.4f, g1.top + g1.height * 0.5f), 0.8f * u)
    val palanca = Path().apply { moveTo(cx - an * 0.1f, g2.top); quadraticTo(cx + an * 0.1f, g2.top - s.height * 0.07f, cx + an * 0.6f, g2.top - s.height * 0.03f) }
    drawPath(palanca, a.c(NIQUEL_OSC, k), style = Stroke(2.6f * u, cap = StrokeCap.Round))
    drawPath(palanca, a.c(NIQUEL, k), style = Stroke(1.4f * u, cap = StrokeCap.Round))
    destelloDeco(a, Offset(g2.left + g2.width * 0.25f, g2.top + g2.height * 0.3f), 1.2f * u, k)
}

/** El cenicero de cristal tallado, con el cigarrillo apoyado, su brasa y un hilo de humo que sube hasta arriba del hueco. */
private fun DrawScope.ceniceroDeco(a: AireDeco, s: Rect, k: Float) {
    val u = density
    val an = s.width; val cx = s.center.x; val suelo = s.bottom
    val al = s.height * 0.13f
    contactoDeco(a, Offset(cx, suelo), an * 1.1f, al * 0.5f, 0.5f)
    val vaso = TrazoDeco.poligono(cx - an / 2, suelo - al, cx + an / 2, suelo - al, cx + an * 0.4f, suelo, cx - an * 0.4f, suelo)
    drawPath(vaso, a.c(Color(0xFFA9C4C9), k).copy(alpha = 0.85f))
    clipPath(vaso) {
        val tallas = Path()
        for (j in 0 until 7) { val x = cx - an / 2 + an * (j + 0.5f) / 7f; tallas.moveTo(x, suelo - al); tallas.lineTo(x + an * 0.04f, suelo) }
        drawPath(tallas, a.b(0.5f, k), style = Stroke(0.8f * u))
        drawRect(a.s(0.3f), Offset(cx - an / 2, suelo - al * 0.35f), Size(an, al * 0.35f))
    }
    drawOval(a.c(Color(0xFFDCEBEE), k), Offset(cx - an / 2, suelo - al - al * 0.3f), Size(an, al * 0.6f))
    drawOval(a.c(Color(0xFF7E9AA0), k), Offset(cx - an * 0.36f, suelo - al - al * 0.18f), Size(an * 0.72f, al * 0.36f))
    drawLine(a.b(0.8f, k), Offset(cx - an * 0.4f, suelo - al - al * 0.15f), Offset(cx - an * 0.1f, suelo - al - al * 0.28f), 1f * u)
    // El cigarrillo y su brasa.
    drawLine(a.c(MARFIL, k), Offset(cx - an * 0.12f, suelo - al - al * 0.1f), Offset(cx + an * 0.42f, suelo - al - al * 0.6f), 2.4f * u, StrokeCap.Round)
    drawLine(a.c(Color(0xFFC79A5A), k), Offset(cx - an * 0.12f, suelo - al - al * 0.1f), Offset(cx + an * 0.02f, suelo - al - al * 0.22f), 2.4f * u, StrokeCap.Round)
    drawCircle(Color(0xFFE8763C).copy(alpha = if (a.lumbre) 0.95f else 0.75f), 1.6f * u, Offset(cx + an * 0.43f, suelo - al - al * 0.6f))
    val humo = Path().apply {
        moveTo(cx + an * 0.43f, suelo - al - al * 0.7f)
        cubicTo(cx + an * 0.6f, suelo - s.height * 0.4f, cx + an * 0.05f, suelo - s.height * 0.6f, cx + an * 0.3f, suelo - s.height * 0.92f)
    }
    drawPath(humo, a.b(0.5f, maxOf(k, 0.4f)).copy(alpha = if (a.oscuro) 0.3f else 0.5f), style = Stroke(2f * u, cap = StrokeCap.Round))
    drawPath(TrazoDeco.corrido(humo, 3f * u, 2f * u), a.b(0.3f, maxOf(k, 0.4f)).copy(alpha = if (a.oscuro) 0.15f else 0.3f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
}

/**
 * Las partituras atadas, tumbadas: el canto de las hojas con su pauta, la tapa de cartulina verde con su abanico impreso y la
 * cinta granate que las cruza, con su lazo arriba.
 */
private fun DrawScope.partiturasAtadasDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val al = s.height * 0.3f
    val p = Rect(s.left, s.bottom - al, s.right, s.bottom)
    contactoDeco(a, Offset(p.center.x, s.bottom), p.width * 1.08f, 5f * u, 0.5f)
    // El canto de las hojas, con sus capas.
    drawRect(a.c(MARFIL, k), p.topLeft, p.size)
    val capas = Path()
    for (j in 1 until 9) { val y = p.top + p.height * j / 9f; capas.moveTo(p.left, y); capas.lineTo(p.right, y + (j % 3 - 1) * 0.6f * u) }
    drawPath(capas, a.c(ESTRAZA, k).copy(alpha = 0.7f), style = Stroke(0.6f * u))
    // La tapa de arriba, vista de canto y un poco desde arriba.
    val tapa = Rect(p.left - 2f * u, p.top - al * 0.3f, p.right + 2f * u, p.top + al * 0.12f)
    drawRect(a.c(Color(0xFF2F6A52), k), tapa.topLeft, tapa.size)
    drawArc(a.c(Color(0xFFE0B04A), k), 180f, 180f, false, Offset(tapa.center.x - tapa.width * 0.2f, tapa.top + tapa.height * 0.2f), Size(tapa.width * 0.4f, tapa.height * 1.4f), style = Stroke(1.2f * u))
    drawRect(a.b(0.45f, k), tapa.topLeft, Size(tapa.width, 1f * u))
    carasDeco(a, p, k, izq, 1.2f)
    // La cinta que las cruza, y el lazo.
    val cinta = a.c(Color(0xFF9A2C30), k)
    val cxc = p.left + p.width * 0.62f
    drawRect(cinta, Offset(cxc - 1.6f * u, tapa.top), Size(3.2f * u, p.bottom - tapa.top))
    drawRect(a.b(0.4f, k), Offset(cxc - 1.6f * u, tapa.top), Size(1f * u, p.bottom - tapa.top))
    drawOval(cinta, Offset(cxc - 9f * u, tapa.top - 5f * u), Size(8f * u, 6f * u), style = Stroke(2f * u))
    drawOval(cinta, Offset(cxc + 1f * u, tapa.top - 5f * u), Size(8f * u, 6f * u), style = Stroke(2f * u))
    drawCircle(cinta, 2.2f * u, Offset(cxc, tapa.top - 1f * u))
}

/**
 * Las baquetas y las escobillas de la batería, metidas en una lata de galletas pintada: dos baquetas de arce con su punta,
 * y dos escobillas con el mango negro y el abanico de alambre.
 */
private fun DrawScope.baquetasDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val an = s.width * 0.7f
    val cx = s.center.x
    val lata = Rect(cx - an / 2, s.bottom - s.height * 0.34f, cx + an / 2, s.bottom)
    contactoDeco(a, Offset(cx, s.bottom), an * 1.3f, 5f * u, 0.5f)
    // Lo que sale de la lata (detrás de su boca).
    val boca = lata.top + 3f * u
    val arce = a.c(Color(0xFFD9B07A), k)
    for ((dx, dy) in listOf(-0.42f to 0.98f, 0.1f to 0.95f)) {
        val p0 = Offset(cx + an * dx * 0.3f, boca + s.height * 0.1f)
        val p1 = Offset(cx + an * dx * 1.1f, s.bottom - s.height * dy)
        drawLine(a.s(0.35f), Offset(p0.x + 2f * u, p0.y), Offset(p1.x + 2f * u, p1.y + 1f * u), 3f * u, StrokeCap.Round)
        drawLine(arce, p0, p1, 2.6f * u, StrokeCap.Round)
        drawLine(a.b(0.5f, k), Offset(p0.x - 0.7f * u, p0.y), Offset(p1.x - 0.7f * u, p1.y), 0.7f * u)
        drawOval(arce, Offset(p1.x - 2.2f * u, p1.y - 3.4f * u), Size(4.4f * u, 4.4f * u))
    }
    for (dx in listOf(0.5f, 0.9f)) {
        val p0 = Offset(cx + an * dx * 0.3f, boca + s.height * 0.1f)
        val m = Offset(cx + an * dx * 0.55f, s.bottom - s.height * 0.62f)
        val fin = Offset(cx + an * dx * 0.75f, s.bottom - s.height * 0.9f)
        drawLine(a.c(MACASAR_NEGRO, k), p0, m, 3f * u, StrokeCap.Round)
        drawRect(a.c(NIQUEL, k), Offset(m.x - 2f * u, m.y - 1f * u), Size(4f * u, 3f * u))
        val abanico = Path()
        for (j in 0 until 9) { abanico.moveTo(m.x, m.y); abanico.lineTo(fin.x + (j - 4) * 1.5f * u, fin.y + (j - 4) * (j - 4) * 0.3f * u) }
        drawPath(abanico, a.c(NIQUEL, k), style = Stroke(0.7f * u))
    }
    // La lata: azul con su faja de marfil y su abanico pintado, el borde de la boca y su brillo de metal.
    drawRect(a.c(Color(0xFF2B4C8C), k), lata.topLeft, lata.size)
    drawRect(a.c(MARFIL, k), Offset(lata.left, lata.top + lata.height * 0.36f), Size(an, lata.height * 0.26f))
    drawArc(a.c(Color(0xFFC4523B), k), 180f, 180f, true, Offset(cx - lata.height * 0.12f, lata.top + lata.height * 0.4f), Size(lata.height * 0.24f, lata.height * 0.24f))
    cilindroDeco(a, lata, k, izq, 1.3f)
    drawOval(a.c(NIQUEL, k), Offset(lata.left, lata.top - 2f * u), Size(an, 4f * u))
    drawOval(a.c(NIQUEL_OSC, k), Offset(lata.left + 2f * u, lata.top - 1f * u), Size(an - 4f * u, 2.4f * u))
}

/** El clarinete, apoyado de pie en diagonal: el ébano negro con sus anillos y llaves de níquel, la campana abajo y la boquilla arriba. */
private fun DrawScope.clarineteDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val abajo = Offset(s.left + s.width * 0.25f, s.bottom)
    val arriba = Offset(s.right - s.width * 0.18f, s.top + s.height * 0.04f)
    val grueso = maxOf(3f * u, s.height * 0.05f)
    contactoDeco(a, Offset(abajo.x, s.bottom), s.width * 0.7f, 4f * u, 0.5f)
    drawLine(a.s(0.35f), Offset(abajo.x + 3f * u, abajo.y), Offset(arriba.x + 3f * u, arriba.y + 2f * u), grueso, StrokeCap.Round)
    drawLine(a.c(Color(0xFF15100E), k), abajo, arriba, grueso, StrokeCap.Round)
    drawLine(a.b(0.4f, k), Offset(abajo.x - grueso * 0.25f, abajo.y), Offset(arriba.x - grueso * 0.25f, arriba.y), grueso * 0.2f)
    fun en(t: Float) = Offset(abajo.x + (arriba.x - abajo.x) * t, abajo.y + (arriba.y - abajo.y) * t)
    // La campana.
    val c0 = en(0.02f)
    drawPath(TrazoDeco.poligono(c0.x - grueso * 1.6f, c0.y, c0.x + grueso * 1.6f, c0.y, en(0.14f).x + grueso * 0.6f, en(0.14f).y, en(0.14f).x - grueso * 0.6f, en(0.14f).y), a.c(Color(0xFF15100E), k))
    drawLine(a.c(NIQUEL, k), Offset(c0.x - grueso * 1.6f, c0.y - 1f * u), Offset(c0.x + grueso * 1.6f, c0.y - 1f * u), 1.6f * u)
    // Los anillos y las llaves.
    for (t in listOf(0.15f, 0.42f, 0.6f, 0.82f)) {
        val p = en(t)
        drawLine(a.c(NIQUEL, k), Offset(p.x - grueso * 0.7f, p.y + grueso * 0.2f), Offset(p.x + grueso * 0.7f, p.y - grueso * 0.2f), 1.4f * u)
    }
    for (j in 0 until 6) {
        val p = en(0.25f + 0.08f * j)
        drawCircle(a.c(NIQUEL, k), grueso * 0.28f, Offset(p.x + grueso * 0.5f, p.y))
        drawLine(a.c(NIQUEL_OSC, k), Offset(p.x + grueso * 0.5f, p.y), Offset(p.x + grueso * 0.9f, p.y - grueso * 0.5f), 0.7f * u)
    }
    // La boquilla y su abrazadera.
    val b0 = en(0.9f)
    drawLine(a.c(TINTA, k), b0, arriba, grueso * 0.8f, StrokeCap.Round)
    drawLine(a.c(NIQUEL, k), Offset(b0.x - grueso * 0.5f, b0.y + grueso * 0.1f), Offset(b0.x + grueso * 0.5f, b0.y - grueso * 0.1f), 2f * u)
    destelloDeco(a, en(0.42f), 1f * u, k)
    if (!izq) drawLine(a.foco.copy(alpha = (0.5f * k).coerceAtMost(0.45f)), Offset(abajo.x + grueso * 0.4f, abajo.y), Offset(arriba.x + grueso * 0.4f, arriba.y), 0.8f * u)
}

/** La coctelera de níquel: el cuerpo con su faja, la tapa en tres gradas y el tapón; brillos francos y su sombra. */
private fun DrawScope.cocteleraDeco(a: AireDeco, s: Rect, k: Float, izq: Boolean) {
    val u = density
    val an = s.width * 0.82f
    val cx = s.center.x
    val cuerpo = Rect(cx - an / 2, s.bottom - s.height * 0.6f, cx + an / 2, s.bottom)
    contactoDeco(a, Offset(cx, s.bottom), an * 1.3f, 5f * u, 0.5f)
    drawRect(a.c(NIQUEL, k), cuerpo.topLeft, cuerpo.size)
    cilindroDeco(a, cuerpo, k, izq, 1.6f)
    drawRect(a.b(0.8f, k), Offset(if (izq) cuerpo.left + an * 0.18f else cuerpo.right - an * 0.26f, cuerpo.top), Size(an * 0.08f, cuerpo.height))
    drawRect(a.c(LATON, k), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.2f), Size(an, 2.6f * u))
    drawRect(a.c(LATON_LUZ, k), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.2f), Size(an, 0.8f * u))
    var y = cuerpo.top
    for ((f, alt) in listOf(0.9f to 0.08f, 0.7f to 0.07f, 0.48f to 0.07f, 0.26f to 0.06f)) {
        val g = Rect(cx - an * f / 2, y - s.height * alt, cx + an * f / 2, y)
        drawRect(a.c(NIQUEL, k), g.topLeft, g.size)
        cilindroDeco(a, g, k, izq, 1.5f)
        drawRect(a.c(NIQUEL_OSC, k), Offset(g.left, g.bottom - 0.8f * u), Size(g.width, 0.8f * u))
        y = g.top
    }
    destelloDeco(a, Offset(cuerpo.left + an * 0.22f, cuerpo.top + cuerpo.height * 0.4f), 1.4f * u, k)
}

/** La copa de champán, ancha y baja, de vidrio: el cáliz, el tallo y el pie, con su brillo. */
private fun DrawScope.copaDeco(a: AireDeco, s: Rect, k: Float) {
    val u = density
    val an = s.width * 0.92f
    val cx = s.center.x
    val vidrio = a.c(Color(0xFFCFE0E2), k)
    contactoDeco(a, Offset(cx, s.bottom), an * 0.9f, 4f * u, 0.45f)
    drawOval(vidrio.copy(alpha = 0.8f), Offset(cx - an * 0.34f, s.bottom - s.height * 0.04f), Size(an * 0.68f, s.height * 0.06f))
    drawLine(vidrio, Offset(cx, s.bottom - s.height * 0.02f), Offset(cx, s.bottom - s.height * 0.42f), 1.8f * u)
    val caliz = Path().apply {
        moveTo(cx - an / 2, s.bottom - s.height * 0.58f)
        lineTo(cx + an / 2, s.bottom - s.height * 0.58f)
        quadraticTo(cx + an * 0.42f, s.bottom - s.height * 0.42f, cx, s.bottom - s.height * 0.42f)
        quadraticTo(cx - an * 0.42f, s.bottom - s.height * 0.42f, cx - an / 2, s.bottom - s.height * 0.58f)
        close()
    }
    drawPath(caliz, vidrio.copy(alpha = 0.55f))
    drawPath(caliz, a.c(Color(0xFFE8C97A), k).copy(alpha = 0.35f))
    drawPath(caliz, a.b(0.6f, k), style = Stroke(0.9f * u))
    drawOval(a.b(0.5f, k), Offset(cx - an / 2, s.bottom - s.height * 0.6f), Size(an, s.height * 0.04f), style = Stroke(0.9f * u))
    drawLine(a.b(0.8f, k), Offset(cx - an * 0.34f, s.bottom - s.height * 0.55f), Offset(cx - an * 0.18f, s.bottom - s.height * 0.47f), 1f * u)
}
