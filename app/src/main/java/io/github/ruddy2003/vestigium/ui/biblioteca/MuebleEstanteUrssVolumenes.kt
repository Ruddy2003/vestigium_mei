package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de Soviético, tres objetos de la biblioteca y del taller de carteles del club (la clase sale de la
 * semilla, nunca del sitio):
 * - **el libro de tela**: alto y rígido, de tela del color del ánimo (roja, lino crudo o negra), con la cabezada, los filetes en
 *   seco, una composición de bandas y un disco estampada en el lomo (sin letras), el tejuelo con el día y la ficha de préstamo
 *   que asoma por arriba con su raya roja (la de las fichas del Ideario);
 * - **el cartel enrollado**: redondo, largo y delgado, de pie o apoyado en diagonal, con la espiral del papel arriba y el pliego de
 *   fuera impreso del color del ánimo, con su margen crudo y una banda de otro color, atado con bramante; el día en una etiqueta de
 *   cartón colgada a plomo del bramante;
 * - **el bote de pintura de carteles**: la lata abierta, chorreada de su pintura (roja, cruda o negra), la superficie de la pintura
 *   arriba, el asa de alambre y la brocha apoyada en diagonal; el día en la etiqueta de papel de la lata.
 * El día, en la letra del tema a unos 16 sp, y debajo el mes corto, en cartulina amarillenta de valor medio; en los Sueños, una
 * media luna de papel plateado.
 */

/** Los meses cortos en español, para la etiqueta. */
private val MESES_CORTOS_URSS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")
private val MESES_LARGOS_URSS = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

/** El mes corto de una entrada: el tema lo nombra en ruso («НОЯБРЬ»); si viene en español o por su número, también. */
private fun mesCortoUrss(mes: String): String {
    val t = mes.trim()
    val i = Temas.MESES_RUSOS.indexOfFirst { t.uppercase().startsWith(it) }
    if (i >= 0) return MESES_CORTOS_URSS[i]
    val n = t.filter { it.isDigit() }.toIntOrNull()
    if (n != null && n in 1..12) return MESES_CORTOS_URSS[n - 1]
    val j = MESES_LARGOS_URSS.indexOfFirst { t.lowercase().startsWith(it) }
    return if (j >= 0) MESES_CORTOS_URSS[j] else t
}

internal fun DrawScope.volumenUrss(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteUrss
    val a = AireUrss(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total, v.repisas, 0.06f)
    val hacia = m.hacia(a, x, y, total, v.repisas)
    val tono = tonoDeDiaUrss(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> libroUrss(v, tono, medidor, l, hacia)
        1 -> rolloUrss(v, tono, medidor, l, hacia)
        else -> boteUrss(v, tono, medidor, l, hacia)
    }
}

/** La sombra que la entrada echa detrás (en la pared del fondo y en la vecina), lejos de la luz. */
private fun DrawScope.sombraDetrasUrss(forma: Path, l: LuzUrss, hacia: Float) {
    val u = density
    drawPath(corridoUrss(forma, -hacia * 5f * u, if (l.a.oscuro) -1.5f * u else 2.4f * u), l.s(if (l.a.oscuro) 0.5f else 0.4f))
}

/** La sombra de contacto en la tabla. */
private fun DrawScope.contactoUrss(x0: Float, x1: Float, l: LuzUrss, hacia: Float) {
    val u = density; val h = size.height
    drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, h - 3f * u), Size(x1 - x0 + 3f * u, 5f * u))
}

/** La caja de la etiqueta: tan ancha como cabe el día (unos 16 sp) sin pasar del ancho del objeto [an], con su arriba en [y]. */
private fun DrawScope.rectEtiquetaUrss(x0: Float, an: Float, y: Float, alto: Float = 29f): Rect {
    val u = density
    val ancho = minOf(an * 0.86f, 28f * u).coerceAtLeast(minOf(an, 22f * u))
    val cx = x0 + an / 2
    return Rect(cx - ancho / 2, y, cx + ancho / 2, y + alto * u)
}

/**
 * La etiqueta del día (cartulina amarillenta de valor medio, teñida por la luz que le toca): el día en la letra del tema (unos
 * 16 sp) y el mes corto. [raya]: la raya roja de las fichas del Ideario, arriba.
 */
private fun DrawScope.etiquetaUrss(v: Volumen, medidor: TextMeasurer, l: LuzUrss, r: Rect, raya: Boolean = true) {
    val u = density
    val carton = l.etiqueta()
    drawRect(carton, r.topLeft, r.size)
    if (raya) drawRect(mezcla(carton, P.ROJO, 0.65f), Offset(r.left, r.top + 2.4f * u), Size(r.width, 0.9f * u))
    drawRect(mezcla(carton, P.ESTRAZA, 0.4f).copy(alpha = 0.3f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
    val tinta = l.tinta(carton)
    val zonaDia = Rect(r.left - 0.4f * u, r.top + 2.8f * u, r.right + 0.4f * u, r.top + 19.6f * u)
    textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, minimo = 9f)
    val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1.2f * u, r.right - 0.4f * u, r.bottom - 0.8f * u)
    textoEn(medidor, mesCortoUrss(v.mes), zonaMes, P.LETRA, tinta.copy(alpha = 0.85f), 8f, minimo = 6f)
    if (l.k > 0.08f) drawRect(l.filo(0.45f), r.topLeft, r.size, style = Stroke(0.9f * u))
}

/** La tinta de lo estampado sobre [fondo]: negra sobre lo rojo y lo crudo, roja sobre lo negro. */
/** La tinta de lo estampado sobre [fondo]: cruda sobre lo oscuro (el topo y el carbón), negra sobre lo rojo, el coral y el crudo. */
private fun tintaSobreUrss(fondo: Color): Color = if (luminanciaUrss(fondo) < 0.12f) mezcla(P.CARTULINA, P.CARTON, 0.25f) else P.NEGRO

// ---------------------------------------------------------------- el libro de tela

/** La tela de cuando no hay ánimo: el gris azulado de las telas de oficio. */
private val SIN_ANIMO_TELA_URSS = Color(0xFF5E6A70)

private fun DrawScope.libroUrss(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzUrss, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 6) < 0.22f) (if (azar(s, 7) < 0.5f) -3f else 3f) * (0.6f + 0.4f * azar(s, 8)) else 0f
    val an = w * (if (giro != 0f) 0.84f else 0.88f + 0.08f * azar(s, 3))
    val x0 = (w - an) * (if (giro > 0f) 0.1f else if (giro < 0f) 0.9f else 0.5f)
    val top = h * (0.085f + 0.02f * azar(s, 5))
    val telaBase = tono?.let { materialDelAnimoUrss(it, P.TELAS) } ?: SIN_ANIMO_TELA_URSS
    val tela = if (tono != null) l.animo(telaBase) else l.c(telaBase)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        val lomo = Path().apply { addRoundRect(RoundRect(Rect(x0, top, x0 + an, h - 0.5f * u), CornerRadius(2.4f * u, 1.4f * u))) }
        // La cabeza del libro, vista desde arriba: el canto de las dos tapas y el bloque de las hojas entre ellas.
        val hondo = minOf(an * 0.2f, 5.5f * u)
        val cabeza = poligonoUrss(Offset(x0 + 0.6f * u, top + 1f * u), Offset(x0 + an - 0.6f * u, top + 1f * u), Offset(x0 + an - an * 0.06f, top - hondo), Offset(x0 + an * 0.06f, top - hondo))
        sombraDetrasUrss(Path().apply { addPath(lomo); addPath(cabeza) }, l, hacia)
        contactoUrss(x0, x0 + an, l, hacia)
        drawPath(cabeza, l.c(mezcla(telaBase, P.NEGRO, 0.25f)))
        val hojas = poligonoUrss(Offset(x0 + an * 0.14f, top + 0.6f * u), Offset(x0 + an * 0.86f, top + 0.6f * u), Offset(x0 + an * 0.82f, top - hondo + 1.2f * u), Offset(x0 + an * 0.18f, top - hondo + 1.2f * u))
        drawPath(hojas, l.c(mezcla(P.CARTULINA, P.CARTON, 0.15f)))
        clipPath(hojas) {
            val canto = Path()
            var xh = x0 + an * 0.2f; var jh = 0
            while (xh < x0 + an * 0.82f && jh < 16) { canto.moveTo(xh, top - hondo); canto.lineTo(xh, top + 1f * u); xh += 2.2f * u; jh++ }
            drawPath(canto, l.s(0.16f), style = Stroke(0.5f * u))
            drawRect(l.b(0.35f), Offset(x0, top - hondo), Size(an, 1f * u))
        }
        // La ficha de préstamo que asoma de entre las hojas, con su raya roja (la de las fichas del Ideario); más arriba si escribiste
        // más.
        val fx = x0 + an * (0.22f + 0.18f * azar(s, 9))
        val fa = an * (0.34f + 0.1f * azar(s, 10))
        val fy = (top - hondo * 0.5f - h * (0.04f + 0.035f * v.lleno)).coerceAtLeast(0.5f * u)
        val ficha = Rect(fx, fy, fx + fa, top)
        drawRect(l.s(0.35f), ficha.topLeft + Offset(-hacia * 1.2f * u, 0f), ficha.size)
        drawRect(l.c(P.CARTULINA), ficha.topLeft, ficha.size)
        drawRect(l.c(P.ROJO), Offset(ficha.left, ficha.top + 2f * u), Size(ficha.width, 0.9f * u))
        drawRect(l.b(0.4f), ficha.topLeft, Size(1f * u, ficha.height))
        planosUrss(lomo, tela, l, hacia, 0.2f, 0.26f, tostado = true)
        // Lo de encima del lomo va recortado a su rectángulo (más barato que a su forma; las esquinas casi no se redondean).
        clipRect(x0, top, x0 + an, h - 0.5f * u) {
            // La trama de la tela: unos hilos finos a lo largo, en un solo camino.
            val trama = Path()
            var xx = x0 + an * 0.08f; var j = 0
            while (xx < x0 + an * 0.94f && j < 14) { trama.moveTo(xx, top); trama.lineTo(xx, h); xx += 3.6f * u; j++ }
            drawPath(trama, l.s(0.07f), style = Stroke(0.6f * u))
            // Los cajos: el surco de cada tapa junto a los cantos del lomo, con su filo de luz.
            listOf(0.1f, 0.9f).forEach { fx ->
                drawLine(l.s(0.4f), Offset(x0 + an * fx, top + 2f * u), Offset(x0 + an * fx, h - 2f * u), 1.1f * u)
                drawLine(l.b(0.25f), Offset(x0 + an * fx + 1.1f * u, top + 2f * u), Offset(x0 + an * fx + 1.1f * u, h - 2f * u), 0.6f * u)
            }
            // La cabezada a rayas, arriba, y la sombra del pie.
            for (k in 0 until 7) drawRect((if (k % 2 == 0) l.c(P.ROJO) else l.c(P.CARTULINA)).copy(alpha = 0.8f), Offset(x0 + an * (0.16f + 0.1f * k), top), Size(an * 0.06f, 1.8f * u))
            drawRect(l.s(0.25f), Offset(x0, h - 3.2f * u), Size(an, 2.6f * u))
            // Los filetes en seco, arriba y abajo: la raya hundida y su filo de luz.
            listOf(top + h * 0.075f, top + h * 0.09f, h * 0.93f, h * 0.945f).forEach { yy ->
                drawLine(l.s(0.35f), Offset(x0 + an * 0.12f, yy), Offset(x0 + an * 0.88f, yy), 0.8f * u)
                drawLine(l.b(0.25f), Offset(x0 + an * 0.12f, yy + 0.8f * u), Offset(x0 + an * 0.88f, yy + 0.8f * u), 0.6f * u)
            }
            // Las franjas de remate estampadas en el lomo, negras o crudas según la tela (sin piezas sueltas que se toquen, así no se
            // forma ninguna letra, y sin discos): la tela del ánimo manda.
            val tinta = l.c(tintaSobreUrss(telaBase))
            val z0 = top + h * 0.12f; val z1 = top + h * 0.4f
            val dz = z1 - z0
            when ((azar(s, 11) * 3f).toInt()) {
                0 -> {
                    // Una franja ancha arriba y una fina debajo; y otra fina al pie.
                    drawRect(tinta, Offset(x0, z0 + dz * 0.08f), Size(an, dz * 0.17f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.34f), Size(an, dz * 0.06f))
                    drawRect(tinta, Offset(x0, h * 0.875f), Size(an, h * 0.022f))
                }
                1 -> {
                    // La cuña: una banda en diagonal que cruza el lomo.
                    drawPath(poligonoUrss(Offset(x0, z1), Offset(x0 + an, z0 + dz * 0.25f), Offset(x0 + an, z0 + dz * 0.42f), Offset(x0, z1 + dz * 0.17f)), tinta)
                }
                else -> {
                    // Tres franjas finas, de grueso decreciente, arriba; una al pie.
                    drawRect(tinta, Offset(x0, z0 + dz * 0.06f), Size(an, dz * 0.1f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.24f), Size(an, dz * 0.06f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.37f), Size(an, dz * 0.035f))
                    drawRect(tinta, Offset(x0, h * 0.875f), Size(an, h * 0.016f))
                }
            }
            // Lo gastado de la tela en las esquinas: asoma el cartón.
            val carton = l.c(mezcla(P.CARTON, P.CARTULINA_OSC, 0.4f))
            drawPath(poligonoUrss(Offset(x0, top), Offset(x0 + an * 0.16f, top), Offset(x0, top + h * 0.03f)), carton)
            if (azar(s, 12) < 0.6f) drawPath(poligonoUrss(Offset(x0 + an, h), Offset(x0 + an * 0.84f, h), Offset(x0 + an, h - h * 0.03f)), carton)
        }
        // El tejuelo de la biblioteca: la cartulina con su marco impreso, el día y el mes.
        val eti = rectEtiquetaUrss(x0, an, h * (0.6f + 0.06f * azar(s, 15)))
        drawRect(l.s(0.35f), eti.topLeft + Offset(-hacia * 1.2f * u, 1.2f * u), eti.size)
        etiquetaUrss(v, medidor, l, eti, raya = false)
        drawRect(l.c(P.NEGRO).copy(alpha = 0.55f), eti.deflate(1.4f * u).topLeft, eti.deflate(1.4f * u).size, style = Stroke(0.6f * u))
        if (l.k > 0.06f) drawLine(l.filo(0.75f), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, top + 2f * u), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, h - 4f * u), 1.2f * u)
        if (l.luna > 0.05f) drawLine(l.frio(0.6f), Offset(x0 + 1f * u, top + 2f * u), Offset(x0 + 1f * u, h - 4f * u), 1f * u)
        if (v.noche) mediaLunaUrss(Offset(x0 + an * 0.5f, top + h * 0.46f), minOf(an * 0.24f, 6.5f * u), l, hacia)
    }
}

// ---------------------------------------------------------------- el rollo de carteles

/** El papel de cuando no hay ánimo: el papel de estraza sin imprimir. */
private val SIN_ANIMO_PAPEL_URSS = Color(0xFFB8996A)

/**
 * El cartel enrollado: un rollo largo y delgado de pie, apoyado en diagonal contra su vecino cuando le sobra sitio, con la espiral
 * del papel arriba, el pliego de fuera impreso del color del ánimo con su margen crudo y una banda de otro color, el borde libre
 * y la punta despegada; atado con bramante en dos vueltas, y de la de arriba cuelga a plomo la etiqueta del día.
 */
private fun DrawScope.rolloUrss(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzUrss, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val d = minOf(w * 0.8f, 27f * u)
    val sobra = w - d
    // Si le sobra sitio, se apoya en diagonal hacia un lado (el pie al otro).
    val izq = azar(s, 4) < 0.5f
    val giro = if (sobra > 4f * u) (Math.toDegrees(kotlin.math.asin((sobra * 0.85f / h).coerceIn(0f, 0.22f).toDouble())).toFloat()) * (if (izq) -1f else 1f) else 0f
    val xa = if (giro == 0f) sobra * 0.5f else if (izq) sobra else 0f
    val pivote = Offset(if (izq) xa else xa + d, h)
    val papelBase = tono?.let { materialDelAnimoUrss(it, P.PAPELES) } ?: SIN_ANIMO_PAPEL_URSS
    val papel = if (tono != null) l.animo(papelBase) else l.c(papelBase)
    val crudo = l.c(mezcla(P.CARTULINA, P.CARTON, 0.18f))
    val tinta = l.c(tintaSobreUrss(papelBase))
    val ry = d * 0.19f
    val top = h * (0.02f + 0.05f * azar(s, 5))
    val cuerpo = Path().apply {
        moveTo(xa, top + ry); lineTo(xa, h - ry * 0.6f)
        cubicTo(xa + d * 0.2f, h + ry * 0.2f, xa + d * 0.8f, h + ry * 0.2f, xa + d, h - ry * 0.6f)
        lineTo(xa + d, top + ry)
        cubicTo(xa + d * 0.8f, top + ry * 1.9f, xa + d * 0.2f, top + ry * 1.9f, xa, top + ry)
        close()
    }
    val caja = Rect(xa, top, xa + d, h)
    val yAlto = h * (0.27f + 0.05f * azar(s, 9))
    // El punto de la vuelta de arriba (de donde cuelga la etiqueta), ya girado con el rollo.
    val ang = Math.toRadians(giro.toDouble())
    val px = xa + d * (0.4f + 0.2f * azar(s, 10)) - pivote.x; val py = yAlto + d * 0.16f - pivote.y
    val nudo = Offset(pivote.x + (px * kotlin.math.cos(ang) - py * kotlin.math.sin(ang)).toFloat(), pivote.y + (px * kotlin.math.sin(ang) + py * kotlin.math.cos(ang)).toFloat())
    rotate(giro, pivote) {
        sombraDetrasUrss(cuerpo, l, hacia)
        cilindroUrss(cuerpo, papel, l, hacia, 0f, caja, tostado = true)
        clipPath(cuerpo) {
            // Solo un margen crudo arriba, angosto (el pliego enrollado con lo impreso hacia fuera): el papel del color manda.
            val margen = h * (0.025f + 0.015f * azar(s, 6))
            val crudoP = Path().apply { addRect(Rect(xa, top, xa + d, top + ry + margen)) }
            cilindroUrss(crudoP, crudo, l, hacia, 0f, caja, tostado = true)
            // Lo impreso de otro color: una banda ancha en diagonal y, en algunos, otra fina.
            val yb = h * (0.55f + 0.18f * azar(s, 7))
            drawPath(poligonoUrss(Offset(xa, yb), Offset(xa + d, yb - h * 0.1f), Offset(xa + d, yb - h * 0.04f), Offset(xa, yb + h * 0.06f)), tinta)
            if (azar(s, 8) < 0.5f) drawRect(tinta, Offset(xa, top + ry + margen + h * 0.04f), Size(d, h * 0.022f))
            drawRect(Brush.horizontalGradient(if (hacia < 0f) listOf(l.b(0.16f), Color.Transparent, Color.Transparent, l.s(0.3f)) else listOf(l.s(0.3f), Color.Transparent, Color.Transparent, l.b(0.16f)), xa, xa + d), Offset(xa, top), Size(d, h - top))
            // El borde libre del pliego, a lo largo.
            val xe = xa + d * (if (hacia < 0f) 0.74f else 0.26f)
            drawLine(l.s(0.45f), Offset(xe + 0.9f * u, top + ry), Offset(xe + 0.9f * u, h), 1f * u)
            drawLine(l.b(0.45f), Offset(xe, top + ry), Offset(xe, h), 0.6f * u)
        }
        // La boca: la espiral del papel y el hueco del centro.
        val boca = Rect(xa, top, xa + d, top + ry * 2f)
        drawOval(l.c(mezcla(P.CARTULINA, Color.White, 0.1f)), boca.topLeft, boca.size)
        val espiral = Path()
        for (k in 1..4) {
            val f = 1f - k * 0.18f
            val dx = (k % 2) * d * 0.015f
            espiral.addOval(Rect(boca.center.x - boca.width * f / 2 + dx, boca.center.y - boca.height * f / 2, boca.center.x + boca.width * f / 2 + dx, boca.center.y + boca.height * f / 2))
        }
        drawPath(espiral, l.s(0.24f), style = Stroke(0.6f * u))
        drawOval(papel, Offset(boca.left + 0.5f * u, boca.top + 0.3f * u), Size(boca.width - 1f * u, boca.height - 0.6f * u), style = Stroke(1.5f * u))
        drawOval(l.c(mezcla(P.CARTON, P.NEGRO, 0.55f)), Offset(boca.center.x - d * 0.08f, boca.center.y - ry * 0.15f), Size(d * 0.16f, ry * 0.3f))
        drawArc(l.b(0.5f), 180f, 110f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
        // La punta del pliego de fuera, despegada arriba.
        val xp = xa + d * (if (hacia < 0f) 0.74f else 0.26f)
        val sg = if (hacia < 0f) 1f else -1f
        val punta = poligonoUrss(Offset(xp, top + ry * 1.7f), Offset(xp + sg * d * 0.22f, top + ry * 0.8f), Offset(xp + sg * d * 0.1f, top + ry * 2.4f + h * 0.03f))
        drawPath(corridoUrss(punta, sg * 1.2f * u, 1.2f * u), l.s(0.3f))
        drawPath(punta, crudo)
        drawPath(punta, l.b(0.3f), style = Stroke(0.6f * u))
        // El bramante: dos vueltas, con el nudo arriba.
        val cuerda = l.c(P.BRAMANTE)
        listOf(yAlto, h * 0.86f).forEach { y ->
            val p = Path().apply { moveTo(xa, y); cubicTo(xa + d * 0.25f, y + d * 0.22f, xa + d * 0.75f, y + d * 0.22f, xa + d, y) }
            drawPath(corridoUrss(p, 0f, 1.2f * u), l.s(0.35f), style = Stroke(1.6f * u))
            drawPath(p, cuerda, style = Stroke(1.5f * u))
        }
        if (l.k > 0.06f) drawLine(l.filo(0.7f), Offset(if (hacia < 0f) xa + 1f * u else xa + d - 1f * u, top + ry), Offset(if (hacia < 0f) xa + 1f * u else xa + d - 1f * u, h - ry), 1.2f * u)
        if (l.luna > 0.05f) drawLine(l.frio(0.6f), Offset(xa + 1f * u, top + ry), Offset(xa + 1f * u, h - ry), 1f * u)
    }
    contactoUrss(xa, xa + d, l, hacia)
    // La etiqueta de cartón, colgada a plomo del nudo por su hilo, con las esquinas de arriba cortadas y su ojal.
    val cuerda = l.c(P.BRAMANTE)
    val eti0 = rectEtiquetaUrss(0f, w, nudo.y + 6f * u, 30f)
    val eti = Rect((nudo.x - eti0.width / 2).coerceIn(0f, (w - eti0.width).coerceAtLeast(0f)), eti0.top, 0f, eti0.bottom).let { Rect(it.left, it.top, it.left + eti0.width, it.bottom) }
    val ladeo = (-4f + 8f * azar(s, 11))
    drawLine(cuerda, nudo, Offset(eti.center.x, eti.top + 2.6f * u), 1.1f * u)
    drawCircle(cuerda, 1.8f * u, nudo)
    rotate(ladeo, Offset(eti.center.x, eti.top)) {
        val c = 3.6f * u
        val forma = poligonoUrss(listOf(Offset(eti.left + c, eti.top), Offset(eti.right - c, eti.top), Offset(eti.right, eti.top + c), Offset(eti.right, eti.bottom), Offset(eti.left, eti.bottom), Offset(eti.left, eti.top + c)))
        drawPath(corridoUrss(forma, -hacia * 1.4f * u, 1.6f * u), l.s(0.4f))
        clipPath(forma) { etiquetaUrss(v, medidor, l, eti, raya = false) }
        drawPath(forma, l.c(mezcla(P.CARTON, P.NEGRO, 0.2f)).copy(alpha = 0.6f), style = Stroke(0.7f * u))
        drawCircle(l.c(P.METAL), 1.9f * u, Offset(eti.center.x, eti.top + 2.6f * u), style = Stroke(1f * u))
    }
    if (v.noche) mediaLunaUrss(Offset(eti.center.x, minOf(eti.bottom + (h * 0.86f - eti.bottom) * 0.5f, h * 0.8f)), minOf(d * 0.3f, 6f * u), l, hacia)
}

// ---------------------------------------------------------------- el bote de pintura

/** La pintura de cuando no hay ánimo: el gris de la imprimación. */
private val SIN_ANIMO_PINTURA_URSS = Color(0xFF8C9088)

private fun DrawScope.boteUrss(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzUrss, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val an = w * (0.86f + 0.1f * azar(s, 3))
    val x0 = (w - an) * 0.5f
    val ry = an * 0.15f
    val alto = minOf(h * (0.62f + 0.06f * azar(s, 4)), an * 1.7f)
    val yt = h - alto
    val pinturaBase = tono?.let { materialDelAnimoUrss(it, P.PINTURAS) } ?: SIN_ANIMO_PINTURA_URSS
    val pintura = if (tono != null) l.animo(pinturaBase) else l.c(pinturaBase)
    val izq = azar(s, 5) < 0.5f
    // La brocha: el mango de madera en diagonal, que sale de la pintura hacia arriba, con su virola y la pintura que la manchó.
    val pieB = Offset(x0 + an * (if (izq) 0.62f else 0.38f), yt + ry * 0.6f)
    val cima = Offset(x0 + an * (if (izq) 0.08f else 0.92f) + (if (izq) -1f else 1f) * w * 0.04f, h * (0.04f + 0.05f * azar(s, 6)))
    val dir = (cima - pieB) / (cima - pieB).getDistance()
    val perp = Offset(-dir.y, dir.x)
    val gm = maxOf(2.2f * u, an * 0.07f)
    val mango = poligonoUrss(pieB + perp * gm, cima + perp * (gm * 0.8f), cima - perp * (gm * 0.8f), pieB - perp * gm)
    // El asa de alambre, echada hacia atrás.
    val asa = Path().apply {
        moveTo(x0 + an * 0.02f, yt + ry * 0.4f)
        cubicTo(x0 + an * 0.02f, yt - an * 0.55f, x0 + an * 0.98f, yt - an * 0.55f, x0 + an * 0.98f, yt + ry * 0.4f)
    }
    val lata = Path().apply {
        moveTo(x0, yt); lineTo(x0, h - ry * 0.6f)
        cubicTo(x0 + an * 0.2f, h + ry * 0.2f, x0 + an * 0.8f, h + ry * 0.2f, x0 + an, h - ry * 0.6f)
        lineTo(x0 + an, yt); close()
    }
    sombraDetrasUrss(Path().apply { addPath(lata); addPath(mango) }, l, hacia)
    contactoUrss(x0, x0 + an, l, hacia)
    drawPath(asa, l.s(0.3f), style = Stroke(1.4f * u))
    drawPath(corridoUrss(asa, 0.8f * u, -0.8f * u), l.c(P.METAL), style = Stroke(1.2f * u))
    // La lata esmaltada del color de su pintura, entera; la hojalata solo asoma en los rebordes de arriba y de abajo.
    val caja = Rect(x0, yt, x0 + an, h)
    cilindroUrss(lata, pintura, l, hacia, 0.5f, caja, tostado = true)
    clipPath(lata) {
        // Los rebordes de hojalata, en un color con su brillo y su sombra a los lados (sin degradado: pesa menos).
        val metal = l.c(mezcla(P.METAL, P.ACERO, 0.25f))
        val sombraM = sombraPropiaUrss(metal, l, false)
        listOf(Rect(x0, yt - 1f, x0 + an, yt + alto * 0.07f), Rect(x0, h - alto * 0.08f, x0 + an, h + ry)).forEach { rb ->
            drawRect(metal, rb.topLeft, rb.size)
            drawRect(if (l.a.oscuro) l.a.hondo.copy(alpha = 0.5f) else sombraM, Offset(if (hacia < 0f) x0 + an * 0.7f else x0, rb.top), Size(an * 0.3f, rb.height))
            drawRect(l.b(0.5f), Offset(if (hacia < 0f) x0 + an * 0.16f else x0 + an * 0.74f, rb.top), Size(an * 0.08f, rb.height))
        }
        listOf(yt + alto * 0.07f, h - alto * 0.08f).forEach { yy ->
            drawLine(l.s(0.4f), Offset(x0, yy), Offset(x0 + an, yy), 1f * u)
            drawLine(l.b(0.35f), Offset(x0, yy + 1f * u), Offset(x0 + an, yy + 1f * u), 0.6f * u)
        }
        // La pintura que chorreó desde el borde por encima del reborde y del esmalte: el mismo color, más brillante, con su relieve
        // (la sombra bajo cada lengua y el filo de luz), y unas gotas largas, más cuanto más escribiste.
        val baja = alto * (0.18f + 0.1f * azar(s, 7) + 0.08f * v.lleno)
        val manto = Path().apply {
            moveTo(x0 - 1f, yt - 1f)
            lineTo(x0 + an + 1f, yt - 1f)
            val n = 7
            for (k in n downTo 0) {
                val x = x0 + an * k / n
                val largo = baja * (0.55f + 0.7f * azar(s, 30 + k)) + (if (k % 3 == 1) alto * 0.25f * azar(s, 40 + k) else 0f)
                lineTo(x + an * 0.03f, yt + largo)
                lineTo(x - an * 0.03f, yt + largo * 0.92f)
            }
            close()
        }
        drawPath(corridoUrss(manto, -hacia * 0.8f * u, 1.4f * u), sombraPropiaUrss(pintura, l, true).copy(alpha = 0.8f))
        drawPath(manto, mezcla(pintura, Color.White, if (l.a.oscuro) 0.03f else 0.08f))
        drawPath(corridoUrss(manto, hacia * 0.8f * u, -0.4f * u), l.b(0.45f), style = Stroke(0.7f * u))
        // La etiqueta de papel de la fábrica, pegada abajo: el día y el mes, y la banda del color de la pintura.
        val eti = rectEtiquetaUrss(x0, an, (h - alto * 0.08f - 30f * u).coerceAtLeast(yt + baja * 0.6f))
        drawRect(l.s(0.3f), eti.topLeft + Offset(-hacia * 1f * u, 1f * u), eti.size)
        etiquetaUrss(v, medidor, l, eti, raya = false)
        drawRect(mezcla(pintura, P.NEGRO, 0.15f), Offset(eti.left, eti.top), Size(eti.width, 2.4f * u))
    }
    // La boca: el borde enrollado de la lata, lo de dentro (hojalata en sombra) y la superficie de la pintura, con su brillo.
    val boca = Rect(x0, yt - ry, x0 + an, yt + ry)
    drawOval(l.c(mezcla(P.ACERO_OSC, P.NEGRO, 0.3f)), boca.topLeft, boca.size)
    val nivel = Rect(x0 + an * 0.06f, yt - ry * 0.62f, x0 + an * 0.94f, yt + ry * 0.9f)
    drawOval(pintura, nivel.topLeft, nivel.size)
    drawOval(l.b(0.45f), Offset(nivel.left + nivel.width * 0.15f, nivel.top + nivel.height * 0.2f), Size(nivel.width * 0.4f, nivel.height * 0.28f))
    drawOval(l.c(P.METAL), boca.topLeft, boca.size, style = Stroke(1.6f * u))
    drawArc(l.b(0.8f), 160f, 120f, false, boca.topLeft, boca.size, style = Stroke(1f * u))
    // La brocha encima: el mango, la virola de hojalata manchada y el agujero del mango.
    val lm = l
    drawPath(corridoUrss(mango, -hacia * 1.6f * u, 1.4f * u), lm.s(0.35f))
    planosUrss(mango, lm.c(mezcla(P.ROBLE_LUZ, P.CARTULINA, 0.2f)), lm, hacia, 0.3f, 0.3f)
    val v0 = pieB + dir * (gm * 0.6f); val v1 = pieB + dir * (gm * 3.4f)
    val virola = poligonoUrss(v0 + perp * (gm * 1.15f), v1 + perp * (gm * 1.05f), v1 - perp * (gm * 1.05f), v0 - perp * (gm * 1.15f))
    drawPath(virola, lm.c(P.METAL))
    drawPath(virola, pintura.copy(alpha = 0.7f))
    drawLine(lm.b(0.5f), v0 + perp * (gm * 0.5f), v1 + perp * (gm * 0.5f), 0.8f * u)
    drawCircle(lm.c(P.NEGRO).copy(alpha = 0.7f), gm * 0.35f, cima - dir * (gm * 2.2f))
    // La mancha de pintura en el mango, cerca de la virola.
    drawCircle(pintura, gm * 0.5f, pieB + dir * (gm * 4.4f) + perp * (gm * 0.3f))
    if (l.k > 0.06f) drawLine(l.filo(0.7f), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, yt + ry), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, h - ry), 1.3f * u)
    if (l.luna > 0.05f) drawLine(l.frio(0.6f), Offset(x0 + 1f * u, yt + ry), Offset(x0 + 1f * u, h - ry), 1f * u)
    if (v.noche) mediaLunaUrss(Offset(x0 + an * (if (izq) 0.26f else 0.74f), yt + alto * 0.36f), minOf(an * 0.18f, 6f * u), l, hacia)
}
