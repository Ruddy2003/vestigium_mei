package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
 * - **el rollo de carteles**: redondo, de pie, con la espiral del papel arriba, el pliego de fuera impreso del color del ánimo con su
 *   margen crudo y un trozo de banda de otro color, atado con bramante; el día en una etiqueta de cartón colgada del bramante;
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
private fun DrawScope.rectEtiquetaUrss(x0: Float, an: Float, y: Float, alto: Float = 30f): Rect {
    val u = density
    val ancho = minOf(an * 0.9f, 31f * u).coerceAtLeast(minOf(an, 22f * u))
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
    val zonaDia = Rect(r.left - 0.4f * u, r.top + 3.8f * u, r.right + 0.4f * u, r.top + 20.6f * u)
    textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, minimo = 9f)
    val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1.2f * u, r.right - 0.4f * u, r.bottom - 0.8f * u)
    textoEn(medidor, mesCortoUrss(v.mes), zonaMes, P.LETRA, tinta.copy(alpha = 0.85f), 8f, minimo = 6f)
    if (l.k > 0.08f) drawRect(l.filo(0.45f), r.topLeft, r.size, style = Stroke(0.9f * u))
}

/** La tinta de lo estampado sobre [fondo]: negra sobre lo rojo y lo crudo, roja sobre lo negro. */
private fun tintaSobreUrss(fondo: Color): Color = if (luminanciaUrss(fondo) < 0.05f) P.ROJO else if (fondo.red > fondo.green * 1.6f) P.NEGRO else (if (fondo.red > 0.7f) P.ROJO else P.NEGRO)

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
            while (xh < x0 + an * 0.82f && jh < 30) { canto.moveTo(xh, top - hondo); canto.lineTo(xh, top + 1f * u); xh += 1.5f * u; jh++ }
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
        planosUrss(lomo, tela, l, hacia, 0.2f, 0.26f)
        clipPath(lomo) {
            // La trama de la tela: hilos finos a lo largo, en un solo camino.
            val trama = Path()
            var xx = x0 + an * 0.06f; var j = 0
            while (xx < x0 + an * 0.96f && j < 40) { trama.moveTo(xx, top); trama.lineTo(xx, h); xx += 2.2f * u; j++ }
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
            // La composición estampada en el lomo, en la tinta que contraste: campos y bandas a todo el ancho y una cuña en diagonal,
            // sin piezas sueltas que se toquen (así no se forma ninguna letra) y sin discos.
            val tinta = l.c(tintaSobreUrss(telaBase))
            val z0 = top + h * 0.12f; val z1 = top + h * 0.4f
            val dz = z1 - z0
            when ((azar(s, 11) * 3f).toInt()) {
                0 -> {
                    // Un campo entero del otro color y una banda fina debajo.
                    drawRect(tinta, Offset(x0, z0), Size(an, dz * 0.5f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.68f), Size(an, dz * 0.09f))
                }
                1 -> {
                    // La cuña: una banda ancha en diagonal que cruza el lomo.
                    drawPath(poligonoUrss(Offset(x0, z1), Offset(x0 + an, z0 + dz * 0.15f), Offset(x0 + an, z0 + dz * 0.45f), Offset(x0, z1 + dz * 0.3f)), tinta)
                }
                else -> {
                    // Tres bandas a todo el ancho, de grueso decreciente.
                    drawRect(tinta, Offset(x0, z0 + dz * 0.05f), Size(an, dz * 0.24f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.42f), Size(an, dz * 0.13f))
                    drawRect(tinta, Offset(x0, z0 + dz * 0.68f), Size(an, dz * 0.06f))
                }
            }
            // Lo gastado de la tela en las esquinas: asoma el cartón.
            val carton = l.c(mezcla(P.CARTON, P.CARTULINA_OSC, 0.4f))
            drawPath(poligonoUrss(Offset(x0, top), Offset(x0 + an * 0.16f, top), Offset(x0, top + h * 0.03f)), carton)
            if (azar(s, 12) < 0.6f) drawPath(poligonoUrss(Offset(x0 + an, h), Offset(x0 + an * 0.84f, h), Offset(x0 + an, h - h * 0.03f)), carton)
        }
        // El tejuelo de la biblioteca: la cartulina con su marco impreso, el día y el mes.
        val eti = rectEtiquetaUrss(x0, an, h * (0.58f + 0.06f * azar(s, 15)))
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

private fun DrawScope.rolloUrss(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzUrss, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val doble = w > 38f * u && azar(s, 20) < 0.45f
    val an = w * (if (doble) 0.66f else 0.76f + 0.12f * azar(s, 3))
    val x0 = if (doble) w * 0.04f else (w - an) * (0.3f + 0.4f * azar(s, 4))
    val ry = an * 0.17f
    val papelBase = tono?.let { materialDelAnimoUrss(it, P.PAPELES) } ?: SIN_ANIMO_PAPEL_URSS
    val papel = if (tono != null) l.animo(papelBase) else l.c(papelBase)
    val crudo = l.c(mezcla(P.CARTULINA, P.CARTON, 0.18f))
    // El rollo de atrás, más delgado y más alto, apoyado en la pared: papel crudo con su banda.
    if (doble) {
        val an2 = w * 0.36f
        val x2 = w - an2 - w * 0.03f
        val top2 = h * 0.02f
        rotate(4f, Offset(x2, h)) {
            val f2 = Path().apply { addRoundRect(RoundRect(Rect(x2, top2, x2 + an2, h), CornerRadius(an2 * 0.5f, an2 * 0.17f))) }
            sombraDetrasUrss(f2, l, hacia)
            cilindroUrss(f2, l.c(mezcla(P.CARTULINA, P.CARTON, 0.3f)), l, hacia, 0.3f)
            clipPath(f2) { drawRect(l.c(if (azar(s, 21) < 0.5f) P.ROJO else P.NEGRO), Offset(x2, h * 0.42f), Size(an2, h * 0.09f)) }
            drawOval(l.c(P.CARTULINA), Offset(x2, top2), Size(an2, an2 * 0.34f))
            drawOval(l.s(0.4f), Offset(x2 + an2 * 0.3f, top2 + an2 * 0.08f), Size(an2 * 0.4f, an2 * 0.18f))
        }
    }
    val top = h * (0.02f + 0.06f * azar(s, 5))
    val cuerpo = Path().apply {
        moveTo(x0, top + ry); lineTo(x0, h - ry * 0.6f)
        cubicTo(x0 + an * 0.2f, h + ry * 0.2f, x0 + an * 0.8f, h + ry * 0.2f, x0 + an, h - ry * 0.6f)
        lineTo(x0 + an, top + ry)
        cubicTo(x0 + an * 0.8f, top + ry * 1.9f, x0 + an * 0.2f, top + ry * 1.9f, x0, top + ry)
        close()
    }
    sombraDetrasUrss(cuerpo, l, hacia)
    contactoUrss(x0, x0 + an, l, hacia)
    // El papel es mate: el volumen en planos de gouache, sin el brillo largo del metal.
    planosUrss(cuerpo, papel, l, hacia, 0.24f, 0.3f)
    clipPath(cuerpo) {
        // El margen crudo del pliego arriba y abajo (el cartel enrollado con lo impreso hacia fuera).
        val margen = h * (0.07f + 0.03f * azar(s, 6))
        val crudoP = Path().apply { addRect(Rect(x0, top, x0 + an, top + ry + margen)); addRect(Rect(x0, h - margen * 0.8f, x0 + an, h + ry)) }
        planosUrss(crudoP, crudo, l, hacia, 0.24f, 0.3f)
        // Lo impreso de otro color: una banda ancha en diagonal y otra fina que dan la vuelta al rollo (sin discos).
        val tinta = l.c(tintaSobreUrss(papelBase))
        val yb = h * (0.6f + 0.12f * azar(s, 7))
        drawPath(poligonoUrss(Offset(x0, yb), Offset(x0 + an, yb - h * 0.12f), Offset(x0 + an, yb - h * 0.05f), Offset(x0, yb + h * 0.07f)), tinta)
        if (azar(s, 8) < 0.6f) drawRect(tinta, Offset(x0, top + ry + margen + h * 0.05f), Size(an, h * 0.025f))
        // El volumen redondo encima de lo impreso, mate.
        drawRect(Brush.horizontalGradient(if (hacia < 0f) listOf(l.b(0.18f), Color.Transparent, Color.Transparent, l.s(0.32f)) else listOf(l.s(0.32f), Color.Transparent, Color.Transparent, l.b(0.18f)), x0, x0 + an), Offset(x0, top), Size(an, h - top))
        // El borde libre del pliego, a lo largo, con su sombrita.
        val xe = x0 + an * (if (hacia < 0f) 0.76f else 0.24f)
        drawLine(l.s(0.45f), Offset(xe + 1f * u, top + ry), Offset(xe + 1f * u, h), 1.1f * u)
        drawLine(l.b(0.5f), Offset(xe, top + ry), Offset(xe, h), 0.7f * u)
    }
    // La boca de arriba: la espiral del papel, vuelta a vuelta (el crudo de dentro y el color en la última), y el hueco del centro.
    val boca = Rect(x0, top, x0 + an, top + ry * 2f)
    drawOval(l.c(mezcla(P.CARTULINA, Color.White, 0.1f)), boca.topLeft, boca.size)
    val espiral = Path()
    val vueltas = 6
    for (k in 1..vueltas) {
        val f = 1f - k * 0.13f
        val dx = (k % 2) * an * 0.012f
        espiral.addOval(Rect(boca.center.x - boca.width * f / 2 + dx, boca.center.y - boca.height * f / 2, boca.center.x + boca.width * f / 2 + dx, boca.center.y + boca.height * f / 2))
    }
    drawPath(espiral, l.s(0.22f), style = Stroke(0.6f * u))
    drawOval(papel, Offset(boca.left + 0.6f * u, boca.top + 0.4f * u), Size(boca.width - 1.2f * u, boca.height - 0.8f * u), style = Stroke(1.8f * u))
    drawOval(l.c(mezcla(P.CARTON, P.NEGRO, 0.55f)), Offset(boca.center.x - an * 0.09f, boca.center.y - ry * 0.16f), Size(an * 0.18f, ry * 0.32f))
    drawArc(l.b(0.55f), 180f, 110f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
    // La punta del pliego de fuera, que se despega arriba junto al borde libre.
    val xp = x0 + an * (if (hacia < 0f) 0.76f else 0.24f)
    val sg = if (hacia < 0f) 1f else -1f
    val punta = poligonoUrss(Offset(xp, top + ry * 1.6f), Offset(xp + sg * an * 0.2f, top + ry * 0.6f), Offset(xp + sg * an * 0.08f, top + ry * 2.6f + h * 0.03f))
    drawPath(corridoUrss(punta, sg * 1.2f * u, 1.2f * u), l.s(0.3f))
    drawPath(punta, crudo)
    drawPath(punta, l.b(0.3f), style = Stroke(0.6f * u))
    // El bramante: una vuelta alta con la etiqueta colgada y otra baja; de cada vuelta, la parte de delante.
    val cuerda = l.c(P.BRAMANTE)
    fun vuelta(y: Float) {
        val p = Path().apply { moveTo(x0, y); cubicTo(x0 + an * 0.25f, y + ry * 0.8f, x0 + an * 0.75f, y + ry * 0.8f, x0 + an, y) }
        drawPath(corridoUrss(p, 0f, 1.2f * u), l.s(0.35f), style = Stroke(1.6f * u))
        drawPath(p, cuerda, style = Stroke(1.5f * u))
    }
    val yAlto = h * (0.27f + 0.05f * azar(s, 9))
    vuelta(yAlto)
    vuelta(h * 0.86f)
    // La etiqueta de cartón, colgada de la vuelta alta por su hilo, con las esquinas de arriba cortadas y su ojal.
    val nudo = Offset(x0 + an * (0.42f + 0.16f * azar(s, 10)), yAlto + ry * 0.6f)
    val eti = rectEtiquetaUrss(x0, an, nudo.y + 6f * u, 31f)
    val ladeo = (-5f + 10f * azar(s, 11))
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
    if (l.k > 0.06f) drawLine(l.filo(0.7f), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, top + ry), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, h - ry), 1.3f * u)
    if (l.luna > 0.05f) drawLine(l.frio(0.6f), Offset(x0 + 1f * u, top + ry), Offset(x0 + 1f * u, h - ry), 1f * u)
    if (v.noche) mediaLunaUrss(Offset(x0 + an * 0.5f, minOf(eti.bottom + (h * 0.86f - eti.bottom) * 0.5f, h * 0.8f)), minOf(an * 0.24f, 6f * u), l, hacia)
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
    // La lata de hojalata.
    cilindroUrss(lata, l.c(mezcla(P.METAL, P.ACERO, 0.25f)), l, hacia, 0.65f, Rect(x0, yt, x0 + an, h))
    clipPath(lata) {
        // Los aros prensados de la hojalata.
        listOf(0.16f, 0.9f).forEach { f ->
            val yy = yt + alto * f
            drawLine(l.s(0.35f), Offset(x0, yy), Offset(x0 + an, yy), 1f * u)
            drawLine(l.b(0.4f), Offset(x0, yy + 1f * u), Offset(x0 + an, yy + 1f * u), 0.7f * u)
        }
        // La pintura que chorreó desde el borde: un manto de bordes caídos y unas gotas largas, más cuanto más escribiste.
        val baja = alto * (0.2f + 0.1f * azar(s, 7) + 0.08f * v.lleno)
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
        drawPath(manto, pintura)
        drawPath(manto, Brush.horizontalGradient(if (hacia < 0f) listOf(l.b(0.3f), Color.Transparent, l.s(0.35f)) else listOf(l.s(0.35f), Color.Transparent, l.b(0.3f)), x0, x0 + an))
        drawPath(corridoUrss(manto, -hacia * 0.8f * u, 0f), l.b(0.35f), style = Stroke(0.7f * u))
        // La etiqueta de papel de la fábrica, pegada abajo: el día y el mes, y la banda del color de la pintura.
        val eti = rectEtiquetaUrss(x0, an, (h - ry * 0.6f - 32f * u).coerceAtLeast(yt + baja * 0.6f), 30f)
        drawRect(l.s(0.3f), eti.topLeft + Offset(-hacia * 1f * u, 1f * u), eti.size)
        etiquetaUrss(v, medidor, l, eti, raya = false)
        drawRect(pintura, Offset(eti.left, eti.top), Size(eti.width, 2.6f * u))
        // Una gota que bajó por encima de la etiqueta, por el borde.
        val xg = if (hacia < 0f) eti.right - 2.4f * u else eti.left + 1f * u
        drawRoundRect(pintura, Offset(xg, eti.top), Size(1.6f * u, eti.height * (0.25f + 0.35f * azar(s, 8))), CornerRadius(0.8f * u))
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
