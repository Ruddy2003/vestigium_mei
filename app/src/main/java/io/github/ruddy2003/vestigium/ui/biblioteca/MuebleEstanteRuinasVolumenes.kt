package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de la Ciudad verde, tres objetos del cuarto (la clase sale de la semilla, nunca del sitio):
 * - **el libro salvado**: tapa dura de tela del color del ánimo (óxido, lino crudo o hiedra), el lomo con sus nervios, la cabeza
 *   comida por el sol, el cerco del agua al pie y, de marcapáginas, una fronda de helecho, una espiga o la cinta; alto y delgado;
 * - **la botella de las hojas enrolladas**: vidrio del color del ánimo (ámbar, claro o verde), las hojas del día enrolladas dentro,
 *   su tapón de corcho y el bramante al cuello; de tres hechuras (hombro redondo, hombro caído o frasco de botica);
 * - **el fardo de lona encerada**: el cuaderno envuelto contra las goteras en lona del color del ánimo, blando y panzudo, doblado
 *   arriba y atado con dos vueltas de bramante, con una ramita metida bajo el cordel.
 * Cada una lleva su etiqueta de cartón con el día a lápiz grueso (unos 16 sp) y el mes corto; en los Sueños, una media luna
 * recortada de hojalata colgada del mismo bramante.
 */

/** Los meses del tema (como los nombra la Ciudad verde) y los cortos en español, para la etiqueta. */
private val MESES_RUINAS = listOf("Escarcha", "Savia", "Brotes", "Lluvias", "Flores", "Enjambres", "Moras", "Cigarras", "Semillas", "Hojarasca", "Setas", "Raíces")
private val MESES_CORTOS_RUINAS = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")

/** El mes corto de una entrada: se ubica el nombre del tema; si no está, queda tal cual. */
private fun mesCortoRuinas(mes: String): String {
    val i = MESES_RUINAS.indexOfFirst { it.equals(mes.trim(), ignoreCase = true) }
    return if (i in MESES_CORTOS_RUINAS.indices) MESES_CORTOS_RUINAS[i] else mes
}

internal fun DrawScope.volumenRuinas(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteRuinas
    val a = AireRuinas(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total, v.repisas, 0.06f)
    val hacia = m.hacia(a, x, y, total, v.repisas)
    val tono = tonoDeDiaRuinas(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> libroRuinas(v, tono, medidor, l, hacia)
        1 -> botellaRuinas(v, tono, medidor, l, hacia)
        else -> fardoRuinas(v, tono, medidor, l, hacia)
    }
}

/**
 * El volumen de una entrada en planos de gouache, de un solo relleno: el plano de luz (el mismo color, más claro), el tono entero y
 * el plano de sombra (el mismo color, más hondo, con la sombra de color), con los bordes nítidos entre ellos. Así el color del
 * ánimo queda con cuerpo, sin lavarse hacia el crema ni hacia el gris.
 */
private fun DrawScope.planosRuinas(f: Path, base: Color, l: LuzRuinas, hacia: Float, luzAncho: Float, sombraAncho: Float) {
    val r = f.getBounds()
    if (r.width < 1f) { drawPath(f, base); return }
    val claro = Color((base.red * 1.2f + 0.06f).coerceAtMost(1f), (base.green * 1.17f + 0.05f).coerceAtMost(1f), (base.blue * 1.12f + 0.04f).coerceAtMost(1f), base.alpha)
    val luz = if (l.a.oscuro) mezcla(base, mezcla(claro, l.tinte, 0.25f), (0.15f + 1.4f * l.k).coerceAtMost(1f)) else claro
    val honda = if (l.a.oscuro) mezcla(base, l.a.hondo, 0.5f)
        else mezcla(Color(base.red * 0.7f, base.green * 0.7f, base.blue * 0.74f, base.alpha), l.a.sombra(1f).copy(alpha = 1f), 0.14f)
    val media = mezcla(base, honda, 0.55f)
    val izq = if (hacia < 0f) luz else honda
    val der = if (hacia < 0f) honda else luz
    val a1 = if (hacia < 0f) luzAncho else sombraAncho
    val a2 = 1f - (if (hacia < 0f) sombraAncho else luzAncho)
    val stops = if (hacia < 0f) arrayOf(0f to izq, a1 to izq, a1 to base, a2 to base, a2 to media, 1f to der)
        else arrayOf(0f to izq, a1 to media, a1 to base, a2 to base, a2 to der, 1f to der)
    drawPath(f, Brush.horizontalGradient(colorStops = stops, startX = r.left, endX = r.right))
}

/** La sombra que la entrada echa detrás (en la trasera y en la vecina), lejos de la luz. */
private fun DrawScope.sombraDetras(forma: Path, l: LuzRuinas, hacia: Float) {
    val u = density
    drawPath(corridoRuinas(forma, -hacia * 5f * u, if (l.a.oscuro) -1.5f * u else 2.4f * u), l.s(if (l.a.oscuro) 0.5f else 0.42f))
}

/** La sombra de contacto en la cara de la balda. */
private fun DrawScope.contactoVol(x0: Float, x1: Float, l: LuzRuinas, hacia: Float) {
    val u = density; val h = size.height
    drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, h - 3f * u), Size(x1 - x0 + 3f * u, 5f * u))
}

// ---------------------------------------------------------------- el libro salvado

/** La tela del lino crudo cuando no hay ánimo: sin teñir, un poco gris. */
private val SIN_ANIMO_TELA = Color(0xFFB8AE96)

private fun DrawScope.libroRuinas(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRuinas, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    // Algo más estrecho que su sitio (le queda aire con el vecino), y alguno ladeado contra él.
    val giro = if (azar(s, 6) < 0.32f) (if (azar(s, 7) < 0.5f) -4f else 4f) * (0.6f + 0.4f * azar(s, 8)) else 0f
    val an = w * (if (giro != 0f) 0.8f else 0.84f + 0.1f * azar(s, 3))
    val x0 = (w - an) * (if (giro > 0f) 0.12f else if (giro < 0f) 0.88f else 0.3f + 0.4f * azar(s, 4))
    val top = h * (0.03f + 0.05f * azar(s, 5))
    val telaBase = tono?.let { materialDelAnimoRuinas(it, P.TELAS) } ?: SIN_ANIMO_TELA
    val tela = if (tono != null) l.animo(telaBase) else l.c(telaBase)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        // El lomo, hinchado por la humedad: los cantos se abomban un poco.
        val panza = an * 0.04f
        val lomo = Path().apply {
            moveTo(x0 + panza, top + h * 0.012f); quadraticTo(x0 + an * 0.5f, top - h * 0.014f, x0 + an - panza, top + h * 0.012f)
            quadraticTo(x0 + an + panza, (top + h) / 2, x0 + an - panza * 0.5f, h - 1f * u)
            lineTo(x0 + panza * 0.5f, h - 1f * u)
            quadraticTo(x0 - panza, (top + h) / 2, x0 + panza, top + h * 0.012f); close()
        }
        sombraDetras(lomo, l, hacia)
        contactoVol(x0, x0 + an, l, hacia)
        planosRuinas(lomo, tela, l, hacia, 0.24f, 0.3f)
        clipPath(lomo) {
            // La cabeza comida por el sol: la tela se aclara un poco arriba.
            drawRect(Brush.verticalGradient(listOf(l.c(P.GASTADO).copy(alpha = 0.3f), l.c(P.GASTADO).copy(alpha = 0f)), top, top + h * 0.14f), Offset(x0 - panza, top), Size(an + 2 * panza, h * 0.14f))
            // El cerco del agua al pie: la tela más oscura hasta una línea ondulada.
            val nivel = h * (0.8f + 0.08f * azar(s, 8))
            val cerco = Path().apply {
                moveTo(x0 - panza, h); lineTo(x0 - panza, nivel)
                quadraticTo(x0 + an * 0.3f, nivel - h * 0.03f, x0 + an * 0.55f, nivel + h * 0.01f)
                quadraticTo(x0 + an * 0.8f, nivel + h * 0.03f, x0 + an + panza, nivel - h * 0.01f)
                lineTo(x0 + an + panza, h); close()
            }
            drawPath(cerco, l.s(0.22f))
            // Los cajos de la tapa dura: dos surcos a lo largo, junto a los cantos.
            for (fx in listOf(0.09f, 0.91f)) {
                drawLine(l.s(0.3f), Offset(x0 + an * fx, top + h * 0.02f), Offset(x0 + an * fx, h - 2f * u), 1.2f * u)
                drawLine(l.b(0.25f), Offset(x0 + an * fx + 1.2f * u, top + h * 0.02f), Offset(x0 + an * fx + 1.2f * u, h - 2f * u), 0.7f * u)
            }
            // Los nervios del lomo, cada uno con su luz arriba y su sombra abajo.
            val nervios = listOf(0.09f, 0.16f, 0.88f)
            for (f in nervios) {
                val yy = top + (h - top) * f
                drawRect(l.s(0.35f), Offset(x0 - panza, yy + 1.2f * u), Size(an + 2 * panza, 1.6f * u))
                drawRect(l.b(0.45f), Offset(x0 - panza, yy - 0.4f * u), Size(an + 2 * panza, 1.2f * u))
            }
        }
        // La cofia de arriba, deshilachada: el cartón gris a la vista y unas hebras.
        drawRect(l.c(Color(0xFF8E8676)), Offset(x0 + panza + an * 0.1f, top - 0.5f * u), Size(an * 0.32f, 2f * u))
        for (k in 0 until 3) drawLine(l.c(mezcla(telaBase, P.GASTADO, 0.5f)), Offset(x0 + an * (0.5f + 0.1f * k), top), Offset(x0 + an * (0.52f + 0.1f * k), top - 2.2f * u), 0.6f * u)
        if (l.k > 0.06f) drawLine(l.filo(0.75f), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, top + 2f * u), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, h - 2f * u), 1.2f * u)
        // El marcapáginas que asoma por la cabeza: una fronda de helecho, una espiga o la cinta de tela.
        val cual = (azar(s, 9) * 3).toInt()
        val bx = x0 + an * (0.3f + 0.4f * azar(s, 10))
        when (cual) {
            0 -> frondaRuinas(Offset(bx, top + 2f * u), h * 0.2f, (azar(s, 11) - 0.5f) * 30f, 0.25f, P.HELECHO, l, hacia, s, 6)
            1 -> {
                val tope = Offset(bx + an * 0.12f, top - h * 0.12f)
                drawLine(l.c(mezcla(P.KRAFT, P.HELECHO, 0.3f)), Offset(bx, top + 2f * u), tope, 1f * u)
                for (k in 0 until 5) rotate(if (k % 2 == 0) 25f else -25f, tope + (Offset(bx, top) - tope) * (k * 0.14f)) {
                    val p = tope + (Offset(bx, top) - tope) * (k * 0.14f)
                    drawOval(l.c(mezcla(P.OCRE, P.KRAFT, 0.4f)), Offset(p.x - 1.3f * u, p.y - 3.4f * u), Size(2.6f * u, 4.6f * u))
                }
            }
            else -> {
                val cinta = l.c(mezcla(P.LADRILLO, P.GASTADO, 0.3f))
                drawPath(Path().apply { moveTo(bx - 1.6f * u, top); lineTo(bx + 1.6f * u, top); lineTo(bx + 2.4f * u, top - h * 0.08f); lineTo(bx + 0.4f * u, top - h * 0.065f); lineTo(bx - 0.8f * u, top - h * 0.085f); close() }, cinta)
            }
        }
        // El tejuelo de la biblioteca, pegado en el lomo: la semillera escribió encima el día y el mes.
        val eti = rectEtiqueta(x0, an, h * (0.42f + 0.12f * azar(s, 12)))
        val telaClara = 0.3f * telaBase.red + 0.59f * telaBase.green + 0.11f * telaBase.blue > 0.55f
        tejueloRuinas(v, medidor, l, eti.deflate(0.6f * u), hacia, telaClara)
        if (v.noche) mediaLunaRuinas(Offset(x0 + an * 0.5f, top + h * 0.2f), minOf(an * 0.28f, 6f * u), l, hacia)
    }
}

/** La caja de la etiqueta: tan ancha como cabe el día (unos 16 sp) sin pasar del ancho del objeto [an], con su arriba en [y]. */
private fun DrawScope.rectEtiqueta(x0: Float, an: Float, y: Float): Rect {
    val u = density
    val ancho = minOf(an * 0.96f, 30f * u).coerceAtLeast(minOf(an, 21f * u))
    val cx = x0 + an / 2
    return Rect(cx - ancho / 2, y, cx + ancho / 2, y + 29f * u)
}

// ---------------------------------------------------------------- la botella de las hojas enrolladas

/** El vidrio cuando no hay ánimo: claro, sin teñir. */
private val SIN_ANIMO_VIDRIO = Color(0xFFBFCBC4)

private fun DrawScope.botellaRuinas(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRuinas, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val hechura = (azar(s, 21) * 3).toInt()
    val an = w * (0.86f + 0.08f * azar(s, 22))
    val x0 = (w - an) * 0.5f
    val cx = x0 + an / 2
    val top = h * 0.02f
    // El cuello, el hombro y la panza según la hechura: hombro redondo (vino), caído (borgoña) o el frasco de botica (corto y cuadrado).
    val cuello = an * (if (hechura == 2) 0.42f else 0.3f).coerceAtMost(1f)
    val hombro = h * (when (hechura) { 0 -> 0.42f; 1 -> 0.36f; else -> 0.3f })
    val boca = top + h * 0.06f
    val pts = when (hechura) {
        0 -> listOf(Offset(cx - cuello / 2, boca), Offset(cx - cuello / 2, hombro - h * 0.1f), Offset(x0 + an * 0.04f, hombro + h * 0.02f), Offset(x0, hombro + h * 0.12f),
            Offset(x0, h - 3f * u), Offset(x0 + an, h - 3f * u), Offset(x0 + an, hombro + h * 0.12f), Offset(x0 + an * 0.96f, hombro + h * 0.02f), Offset(cx + cuello / 2, hombro - h * 0.1f), Offset(cx + cuello / 2, boca))
        1 -> listOf(Offset(cx - cuello / 2, boca), Offset(cx - cuello * 0.55f, hombro - h * 0.12f), Offset(x0 + an * 0.12f, hombro + h * 0.1f), Offset(x0, hombro + h * 0.24f),
            Offset(x0, h - 3f * u), Offset(x0 + an, h - 3f * u), Offset(x0 + an, hombro + h * 0.24f), Offset(x0 + an * 0.88f, hombro + h * 0.1f), Offset(cx + cuello * 0.55f, hombro - h * 0.12f), Offset(cx + cuello / 2, boca))
        else -> listOf(Offset(cx - cuello / 2, boca), Offset(cx - cuello / 2, hombro - h * 0.02f), Offset(x0 + an * 0.02f, hombro + h * 0.02f), Offset(x0, hombro + h * 0.06f),
            Offset(x0, h - 3f * u), Offset(x0 + an, h - 3f * u), Offset(x0 + an, hombro + h * 0.06f), Offset(x0 + an * 0.98f, hombro + h * 0.02f), Offset(cx + cuello / 2, hombro - h * 0.02f), Offset(cx + cuello / 2, boca))
    }
    val forma = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        // Los tramos rectos y las curvas del hombro: el cuello baja recto, el hombro se curva, la panza baja recta y el culo es redondo.
        lineTo(pts[1].x, pts[1].y); cubicTo(pts[1].x, pts[2].y, pts[2].x, pts[2].y, pts[3].x, pts[3].y)
        lineTo(pts[4].x, pts[4].y - 2f * u); quadraticTo(pts[4].x, pts[4].y + 2f * u, pts[4].x + 4f * u, pts[4].y + 2f * u)
        lineTo(pts[5].x - 4f * u, pts[5].y + 2f * u); quadraticTo(pts[5].x, pts[5].y + 2f * u, pts[5].x, pts[5].y - 2f * u)
        lineTo(pts[6].x, pts[6].y); cubicTo(pts[7].x, pts[7].y, pts[8].x, pts[7].y, pts[8].x, pts[8].y)
        lineTo(pts[9].x, pts[9].y); close()
    }
    val vidrioBase = tono?.let { materialDelAnimoRuinas(it, P.VIDRIOS) } ?: SIN_ANIMO_VIDRIO
    val vidrio = if (tono != null) l.animo(vidrioBase) else l.c(vidrioBase)
    sombraDetras(forma, l, hacia)
    contactoVol(x0, x0 + an, l, hacia)
    // El vidrio: lo de detrás se ve a través, oscurecido y teñido; los bordes más hondos (el vidrio es más grueso de canto).
    planosRuinas(forma, vidrio, l, hacia, 0.2f, 0.3f)
    clipPath(forma) {
        // Las hojas del día enrolladas dentro, de pie, teñidas por el vidrio: más cuanto más escribiste.
        val rollos = 1 + (v.lleno * 3.2f).toInt()
        val papel = mezcla(l.c(P.PAPEL), vidrio, 0.55f)
        val sombraPapel = mezcla(papel, vidrio, 0.5f)
        for (k in 0 until rollos) {
            val rw = an * (0.62f / rollos.coerceAtLeast(2) + 0.12f)
            val rx = x0 + an * 0.1f + (an * 0.8f - rw) * (if (rollos == 1) 0.5f else k / (rollos - 1f))
            val rt = hombro - h * (0.06f + 0.1f * azar(s, 30 + k))
            val rr = Rect(rx, rt, rx + rw, h - 4f * u)
            drawRoundRect(papel, rr.topLeft, rr.size, CornerRadius(rw * 0.25f))
            drawRect(sombraPapel, Offset(rr.right - rw * 0.35f, rr.top), Size(rw * 0.35f, rr.height))
            drawOval(mezcla(sombraPapel, vidrio, 0.3f), Offset(rr.left, rr.top - rw * 0.15f), Size(rw, rw * 0.3f))
            drawArc(papel, 180f, 270f, false, Offset(rr.left + rw * 0.2f, rr.top - rw * 0.1f), Size(rw * 0.6f, rw * 0.2f), style = Stroke(0.7f * u))
        }
        // El vidrio otra vez encima de lo de dentro: la panza deja pasar menos donde es más gruesa.
        drawRect(vidrio.copy(alpha = 0.36f), Offset(x0, top), Size(an, h))
        // El culo grueso del vidrio, más hondo, con su filo de luz.
        drawRect(mezcla(vidrio, l.a.hondo, 0.35f), Offset(x0, h - h * 0.07f), Size(an, h * 0.07f))
        drawLine(l.b(0.45f), Offset(x0 + an * 0.15f, h - h * 0.07f), Offset(x0 + an * 0.85f, h - h * 0.07f), 0.8f * u)
        // Las burbujas del vidrio soplado y un rayón.
        for (k in 0 until 3) drawCircle(l.b(0.35f), (0.5f + 0.5f * azar(s, 40 + k)) * u, Offset(x0 + an * (0.2f + 0.6f * azar(s, 43 + k)), hombro + (h - hombro) * (0.2f + 0.6f * azar(s, 46 + k))))
    }
    // Los reflejos del vidrio: la ventana larga del lado de la luz, el filo del otro lado y el brillo del hombro.
    val lado = if (hacia < 0f) x0 + an * 0.18f else x0 + an * 0.72f
    drawRoundRect(l.b(0.85f), Offset(lado, hombro + h * 0.1f), Size(an * 0.1f, (h - hombro) * 0.66f), CornerRadius(an * 0.05f))
    drawCircle(l.b(0.95f), an * 0.05f, Offset(lado + an * 0.05f, hombro + h * 0.06f))
    drawLine(l.b(0.35f), Offset(if (hacia < 0f) x0 + an - 1.4f * u else x0 + 1.4f * u, hombro + h * 0.14f), Offset(if (hacia < 0f) x0 + an - 1.4f * u else x0 + 1.4f * u, h - h * 0.1f), 1f * u)
    drawLine(l.b(0.6f), Offset(cx + (if (hacia < 0f) -1f else 1f) * cuello * 0.25f, boca + h * 0.03f), Offset(cx + (if (hacia < 0f) -1f else 1f) * cuello * 0.25f, hombro - h * 0.12f), 1.2f * u)
    if (l.k > 0.06f) drawPath(bordeRuinas(forma, -hacia * 1.4f * u, 0f), l.filo(0.8f))
    if (l.luna > 0.05f) drawPath(bordeRuinas(forma, -hacia * 1.2f * u, 0f), l.frio(0.6f))
    drawPath(forma, mezcla(vidrio, l.a.hondo, 0.45f), style = Stroke(0.9f * u))
    // El labio de la boca y el tapón de corcho que sobresale.
    val labio = Rect(cx - cuello * 0.62f, boca - h * 0.005f, cx + cuello * 0.62f, boca + h * 0.035f)
    drawRoundRect(mezcla(vidrio, P.VIDRIO_CLARO, 0.25f), labio.topLeft, labio.size, CornerRadius(labio.height * 0.4f))
    drawLine(l.b(0.6f), Offset(labio.left + 1f * u, labio.top + 1f * u), Offset(labio.right - 1f * u, labio.top + 1f * u), 0.8f * u)
    val corcho = Rect(cx - cuello * 0.48f, top, cx + cuello * 0.48f, boca + h * 0.005f)
    drawRect(Brush.horizontalGradient(listOf(l.c(mezcla(P.CORCHO, Color.White, 0.2f)), l.c(P.CORCHO), mezcla(l.c(P.CORCHO), l.a.hondo, 0.35f)), corcho.left, corcho.right), corcho.topLeft, corcho.size)
    for (k in 0 until 3) drawCircle(l.c(P.KRAFT_OSC), 0.6f * u, Offset(corcho.left + corcho.width * (0.25f + 0.25f * k), corcho.top + corcho.height * (0.3f + 0.3f * (k % 2))))
    drawRect(l.c(mezcla(P.CORCHO, Color.White, 0.3f)), corcho.topLeft, Size(corcho.width, 1.2f * u))
    // El bramante al cuello, con dos vueltas, y la etiqueta que cuelga sobre la panza.
    val yc = boca + h * 0.07f
    for (k in 0 until 2) drawLine(l.c(P.BRAMANTE), Offset(cx - cuello * 0.58f, yc + k * 2.2f * u), Offset(cx + cuello * 0.58f, yc + k * 2.2f * u + 0.6f * u), 1.3f * u)
    val eti = rectEtiqueta(x0, an, hombro + h * (0.1f + 0.06f * azar(s, 24)))
    etiquetaRuinas(v, medidor, l, eti, Offset(cx + cuello * 0.4f, yc + 1.5f * u), hacia)
    if (v.noche) mediaLunaRuinas(Offset(cx - cuello * 0.9f - 3f * u, yc + h * 0.07f), minOf(an * 0.26f, 5.5f * u), l, hacia)
}

// ---------------------------------------------------------------- el fardo de lona encerada

/** La lona cruda cuando no hay ánimo. */
private val SIN_ANIMO_LONA = Color(0xFFB2A88A)

private fun DrawScope.fardoRuinas(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzRuinas, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    // Alguno recostado contra el vecino (más angosto, para no salirse de su sitio).
    val giro = if (azar(s, 50) < 0.3f) (if (azar(s, 49) < 0.5f) -4f else 4f) else 0f
    val an = w * (if (giro != 0f) 0.8f else 0.88f + 0.08f * azar(s, 51))
    val x0 = (w - an) * (if (giro > 0f) 0.15f else if (giro < 0f) 0.85f else 0.5f)
    val top = h * (0.02f + 0.04f * azar(s, 52))
    val panza = an * (0.02f + 0.03f * v.lleno)
    val lonaBase = tono?.let { materialDelAnimoRuinas(it, P.LONAS) } ?: SIN_ANIMO_LONA
    val lona = if (tono != null) l.animo(lonaBase) else l.c(lonaBase)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        // El paquete: un bloque de esquinas blandas (se nota el cuaderno dentro), un poco abombado.
        val r0 = an * 0.14f
        val forma = cerradaRuinas(listOf(
            Offset(x0 + r0, top), Offset(x0 + an * 0.5f, top - h * 0.006f), Offset(x0 + an - r0, top), Offset(x0 + an + panza * 0.2f, top + r0),
            Offset(x0 + an + panza, h * 0.55f), Offset(x0 + an, h - 3f * u), Offset(x0 + an * 0.5f, h - 1.5f * u), Offset(x0, h - 3f * u),
            Offset(x0 - panza, h * 0.55f), Offset(x0 - panza * 0.2f, top + r0)))
        sombraDetras(forma, l, hacia)
        contactoVol(x0 - panza * 0.5f, x0 + an + panza * 0.5f, l, hacia)
        // El canto de los papeles que asoma por un lado, del lado de la sombra (la lona no lo tapa entero).
        val xpap = if (hacia < 0f) x0 + an - an * 0.02f else x0 - an * 0.06f
        val papel = Rect(xpap, h * 0.36f, xpap + an * 0.08f, h * 0.86f)
        drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.25f)), papel.topLeft, papel.size)
        var yh = papel.top + 1.6f * u
        while (yh < papel.bottom) { drawLine(l.s(0.25f), Offset(papel.left, yh), Offset(papel.right, yh), 0.6f * u); yh += 2.2f * u }
        planosRuinas(forma, lona, l, hacia, 0.24f, 0.3f)
        // Los cordeles en cruz, corridos del centro, y los pliegues de paquete que tiran hacia el nudo.
        val cx = x0 + an * (0.34f + 0.3f * azar(s, 53))
        val cy = h * (0.5f + 0.14f * azar(s, 54))
        val sol = top + h * (0.2f + 0.04f * azar(s, 56))
        clipPath(forma) {
            // La solapa de arriba: la punta del envoltorio doblada hacia delante (un trapecio, con sus dos pliegues de lado).
            val solapa = poligonoRuinas(Offset(x0 - panza, top - 2f), Offset(x0 + an + panza, top - 2f), Offset(x0 + an * 0.82f, sol), Offset(x0 + an * 0.2f, sol + h * 0.01f))
            drawPath(corridoRuinas(solapa, 0f, 2.4f * u), l.s(0.42f))
            drawPath(solapa, mezcla(lona, Color.White, if (l.a.oscuro) 0.04f else 0.1f))
            drawPath(bordeRuinas(solapa, 0f, -1.6f * u), l.b(0.4f))
            // Las orejas de los lados de la solapa, en sombra.
            drawPath(poligonoRuinas(Offset(x0 - panza, top), Offset(x0 + an * 0.2f, sol + h * 0.01f), Offset(x0 - panza, sol + h * 0.06f)), l.s(0.28f))
            drawPath(poligonoRuinas(Offset(x0 + an + panza, top), Offset(x0 + an * 0.82f, sol), Offset(x0 + an + panza, sol + h * 0.05f)), l.s(0.28f))
            // Los pliegues que tira el cordel hacia el nudo.
            val pl = Path()
            for ((ex, ey) in listOf(x0 to sol + h * 0.08f, x0 + an to sol + h * 0.12f, x0 + an * 0.05f to h - 4f * u, x0 + an * 0.95f to h - 6f * u)) {
                val m = Offset((ex + cx) / 2 + (if (ex < cx) 2f else -2f) * u, (ey + cy) / 2)
                pl.moveTo(cx + (ex - cx) * 0.18f, cy + (ey - cy) * 0.18f); pl.quadraticTo(m.x, m.y, cx + (ex - cx) * 0.75f, cy + (ey - cy) * 0.75f)
            }
            drawPath(pl, l.s(0.32f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
            translate(-hacia * 1.2f * u, -1f * u) { drawPath(pl, l.b(0.3f), style = Stroke(0.8f * u, cap = StrokeCap.Round)) }
            // El brillo de la cera, del lado de la luz.
            val bx = if (hacia < 0f) x0 + an * 0.1f else x0 + an * 0.74f
            drawOval(l.b(0.6f), Offset(bx, sol + h * 0.06f), Size(an * 0.12f, h * 0.2f))
            drawOval(l.b(0.75f), Offset(bx + an * 0.03f, sol + h * 0.09f), Size(an * 0.04f, h * 0.1f))
            // El pespunte de cuando era toldo, a lo largo del otro lado.
            val xp = x0 + an * (if (hacia < 0f) 0.84f else 0.14f)
            var yp = sol + h * 0.04f
            while (yp < h - 6f * u) { drawLine(l.c(mezcla(lonaBase, P.BRAMANTE, 0.5f)).copy(alpha = 0.7f), Offset(xp, yp), Offset(xp, yp + 2.2f * u), 0.8f * u); yp += 4.4f * u }
            drawOval(l.c(P.GASTADO).copy(alpha = 0.28f), Offset(x0 - panza, h - h * 0.12f), Size(an * 0.3f, h * 0.1f))
        }
        if (l.k > 0.06f) drawPath(bordeRuinas(forma, -hacia * 1.4f * u, 0f), l.filo(0.7f))
        // El cordel en cruz, con su sombra; el de arriba abajo pasa por encima de la solapa.
        val vertical = Path().apply { moveTo(cx + an * 0.03f, top - 1f); quadraticTo(cx - an * 0.02f, cy, cx + an * 0.02f, h - 2f * u) }
        val horizontal = Path().apply { moveTo(x0 - panza, cy + 1.2f * u); quadraticTo(x0 + an * 0.5f, cy + 3f * u, x0 + an + panza, cy) }
        for (c in listOf(vertical, horizontal)) {
            drawPath(corridoRuinas(c, -hacia * 1f * u, 1.4f * u), l.s(0.42f), style = Stroke(1.6f * u))
            drawPath(c, l.c(P.BRAMANTE), style = Stroke(1.7f * u))
        }
        // El nudo en el cruce y su cabo suelto (sin lazo).
        val nudo = Offset(cx, cy + 1.4f * u)
        drawCircle(l.c(mezcla(P.BRAMANTE, P.KRAFT_OSC, 0.35f)), 2.6f * u, nudo)
        drawCircle(l.b(0.5f), 1f * u, nudo - Offset(0.8f * u, 0.8f * u))
        drawLine(l.c(P.BRAMANTE), nudo, nudo + Offset(-hacia * 4.5f * u, -3.5f * u), 1.2f * u, StrokeCap.Round)
        // La ramita de romero metida bajo el cordel de arriba abajo.
        val pie = Offset(cx - an * 0.04f, cy - h * 0.06f)
        val tope = Offset(cx + an * 0.16f, top - h * 0.05f)
        drawLine(l.c(mezcla(P.HIEDRA_OSC, P.KRAFT, 0.3f)), pie, tope, 1.1f * u)
        for (k in 0 until 6) {
            val p = pie + (tope - pie) * (0.2f + k * 0.14f)
            drawLine(l.c(if (azar(s, 60 + k) < 0.5f) Color(0xFF6E8A6A) else Color(0xFF8AA284)), p, p + Offset((if (k % 2 == 0) -1f else 1f) * 3f * u, -2.2f * u), 1.1f * u, StrokeCap.Round)
        }
        // La etiqueta, atada al cordel junto al nudo y colgando delante.
        val eti = rectEtiqueta(x0, an, cy + h * 0.05f)
        val etiAjustada = if (eti.bottom > h - 2f * u) eti.translate(0f, h - 2f * u - eti.bottom) else eti
        etiquetaRuinas(v, medidor, l, etiAjustada, nudo, hacia)
        if (v.noche) mediaLunaRuinas(Offset(x0 + an * (if (cx > x0 + an * 0.5f) 0.25f else 0.75f), top + h * 0.13f), minOf(an * 0.26f, 5.5f * u), l, hacia)
    }
}

// ---------------------------------------------------------------- la etiqueta, el tejuelo y la luna

/**
 * El tejuelo del libro: la etiqueta de papel kraft que la biblioteca pegó en el lomo, con su filete impreso alrededor y una esquina
 * despegada; encima, el día a lápiz grueso (unos 16 sp) y el mes corto.
 */
private fun DrawScope.tejueloRuinas(v: Volumen, medidor: TextMeasurer, l: LuzRuinas, r: Rect, hacia: Float, sobreClaro: Boolean) {
    val u = density
    val giro = (azar(v.semilla, 83) - 0.5f) * 3f
    rotate(giro, r.center) {
        val carton = l.etiqueta(oscura = sobreClaro)
        drawRoundRect(carton, r.topLeft, r.size, CornerRadius(1.6f * u))
        // El filete impreso y el papel gastado en el borde de abajo.
        drawRoundRect(mezcla(carton, P.KRAFT_OSC, 0.55f), r.topLeft + Offset(1.6f * u, 1.6f * u), Size(r.width - 3.2f * u, r.height - 3.2f * u), CornerRadius(1f * u), style = Stroke(0.7f * u))
        drawRect(mezcla(carton, P.KRAFT_OSC, 0.35f).copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.18f), Size(r.width, r.height * 0.18f))
        // La esquina de arriba despegada: su revés más claro y su sombra en la tela.
        val e = Offset(if (hacia < 0f) r.right else r.left, r.top)
        val lado = if (hacia < 0f) -1f else 1f
        val esquina = poligonoRuinas(e, e + Offset(lado * 6f * u, 0f), e + Offset(0f, 6f * u))
        drawPath(esquina, l.s(0.45f))
        drawPath(poligonoRuinas(e + Offset(lado * 6f * u, 0f), e + Offset(0f, 6f * u), e + Offset(lado * 4.6f * u, 4.6f * u)), mezcla(carton, Color.White, 0.25f))
        val tinta = l.tinta(carton)
        val zonaDia = Rect(r.left - 0.4f * u, r.top + 3.4f * u, r.right + 0.4f * u, r.top + 19.4f * u)
        textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Normal, minimo = 9f)
        val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1.2f * u, r.right - 0.4f * u, r.bottom - 1.6f * u)
        textoEn(medidor, mesCortoRuinas(v.mes), zonaMes, P.LETRA_MES, tinta, 8.5f, peso = FontWeight.SemiBold, minimo = 6f)
        if (l.k > 0.08f) drawRoundRect(l.filo(0.5f), r.topLeft, r.size, CornerRadius(1.6f * u), style = Stroke(0.9f * u))
    }
}

/**
 * La etiqueta de cartón kraft: el rectángulo con las esquinas de arriba cortadas, el ojal reforzado y, si hay [nudo], el bramante
 * que va del ojal al nudo; el día a lápiz grueso (unos 16 sp) y debajo el mes corto. Se tiñe con la luz que le toca y no pierde la
 * lectura.
 */
private fun DrawScope.etiquetaRuinas(v: Volumen, medidor: TextMeasurer, l: LuzRuinas, r: Rect, nudo: Offset?, hacia: Float) {
    val u = density
    val corte = minOf(r.width * 0.22f, 5f * u)
    val forma = poligonoRuinas(Offset(r.left + corte, r.top), Offset(r.right - corte, r.top), Offset(r.right, r.top + corte), Offset(r.right, r.bottom),
        Offset(r.left, r.bottom), Offset(r.left, r.top + corte))
    val ojal = Offset(r.center.x, r.top + 3.4f * u)
    if (nudo != null) {
        drawLine(l.s(0.35f), nudo + Offset(-hacia * 0.8f * u, 0.8f * u), ojal + Offset(-hacia * 0.8f * u, 0.8f * u), 1.1f * u)
        drawLine(l.c(P.BRAMANTE), nudo, ojal, 1.1f * u)
    }
    val giro = 0f
    rotate(giro, ojal) {
        drawPath(corridoRuinas(forma, -hacia * 1.6f * u, (if (l.a.oscuro) -0.6f else 1.8f) * u), l.s(0.5f))
        val carton = l.etiqueta()
        drawPath(forma, carton)
        // El cartón con su canto algo más oscuro, una mancha de agua y la luz del lado de la luz.
        drawPath(forma, mezcla(carton, P.KRAFT_OSC, 0.55f), style = Stroke(0.9f * u))
        drawOval(mezcla(carton, P.KRAFT_OSC, 0.4f).copy(alpha = 0.3f), Offset(r.left + r.width * (0.15f + 0.4f * azar(v.semilla, 81)), r.bottom - r.height * 0.3f), Size(r.width * 0.5f, r.height * 0.22f))
        // El ojal: el refuerzo redondo y el agujero.
        drawCircle(mezcla(carton, P.KRAFT_OSC, 0.3f), 2.4f * u, ojal)
        drawCircle(l.a.hondo.copy(alpha = 0.85f), 1f * u, ojal)
        val tinta = l.tinta(carton)
        val zonaDia = Rect(r.left - 0.4f * u, r.top + 5.6f * u, r.right + 0.4f * u, r.top + 21.4f * u)
        textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Normal, minimo = 9f)
        val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1.2f * u, r.right - 0.4f * u, r.bottom - 0.4f * u)
        textoEn(medidor, mesCortoRuinas(v.mes), zonaMes, P.LETRA_MES, tinta, 8.5f, peso = FontWeight.SemiBold, minimo = 6f)
        if (l.k > 0.08f) drawPath(bordeRuinas(forma, -hacia * 1f * u, 0f), l.filo(0.6f))
    }
}

/**
 * La media luna de los Sueños: un creciente recortado de hojalata (no un círculo entero, que se leería como un ojo), colgado de un
 * hilo, con su brillo de metal y su borde frío.
 */
private fun DrawScope.mediaLunaRuinas(c: Offset, r: Float, l: LuzRuinas, hacia: Float) {
    val u = density
    val luna = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val mordida = Path().apply { addOval(Rect(c.x - r * 0.35f, c.y - r * 1.2f, c.x + r * 1.65f, c.y + r * 0.8f)) }
    val creciente = Path().apply { op(luna, mordida, PathOperation.Difference) }
    drawLine(l.c(P.BRAMANTE).copy(alpha = 0.8f), Offset(c.x - r * 0.3f, c.y - r * 2f), Offset(c.x - r * 0.3f, c.y - r * 0.9f), 0.7f * u)
    drawPath(corridoRuinas(creciente, -hacia * 1.2f * u, 1.2f * u), l.s(0.5f))
    val claro = mezcla(l.c(Color(0xFFF4F2E8)), Color(0xFFF4F2E8), 0.55f); val oscuroL = mezcla(l.c(Color(0xFFB8C0BE)), Color(0xFFB8C0BE), 0.4f)
    drawPath(creciente, Brush.linearGradient(listOf(claro, oscuroL), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
    drawPath(creciente, mezcla(P.NIQUEL, P.HIERRO, 0.5f), style = Stroke(0.6f * u))
    drawPath(creciente, l.frio(0.5f))
}
