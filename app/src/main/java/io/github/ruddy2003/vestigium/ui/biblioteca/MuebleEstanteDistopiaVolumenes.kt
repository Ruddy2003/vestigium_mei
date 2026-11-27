package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las entradas del estante de Distopía, tres objetos del archivo del ministerio (la clase sale de la semilla, nunca del sitio):
 * - **el archivador de palanca**: alto y rígido, de cartón forrado de tela del color del ánimo (rojo de cartel, lino gris o azul
 *   pizarra), con el canto de metal abajo, el ojal del dedo con su aro y la ventanilla del lomo con la tarjeta del día;
 * - **la cápsula del correo neumático**: el cilindro en que el expediente del día viaja por los tubos, de pie, con un tope de
 *   fieltro oscuro en cada extremo (más ancho que el cuerpo), la tapa de bayoneta, el cuerpo esmaltado del color del ánimo, la
 *   tarjeta bajo su fleje y la ventanilla por la que se ve el papel enrollado; si se escribió mucho, va sin tapa y asoma el rollo;
 * - **el legajo atado**: la camisa de cartón del color del ánimo doblada sobre un fajo de papeles que asoman desiguales, atada con
 *   balduque y cerrada con un precinto de plomo; la carátula del día, pegada en la camisa.
 * El día, en Oswald a unos 16 sp, y el mes corto a máquina, en manila de valor medio; en los Sueños, una media luna de hojalata.
 */

/** Los meses cortos en español, para la etiqueta. */
private val MESES_CORTOS_DISTOPIA = listOf("ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic.")
private val MESES_LARGOS_DISTOPIA = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

/** El mes corto de una entrada: el tema lo dice por su número («MES 11»); si viene por su nombre, también; si no, tal cual. */
private fun mesCortoDistopia(mes: String): String {
    val n = mes.filter { it.isDigit() }.toIntOrNull()
    if (n != null && n in 1..12) return MESES_CORTOS_DISTOPIA[n - 1]
    val i = MESES_LARGOS_DISTOPIA.indexOfFirst { mes.trim().lowercase().startsWith(it) }
    return if (i >= 0) MESES_CORTOS_DISTOPIA[i] else mes
}

internal fun DrawScope.volumenDistopia(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val m = MuebleEstanteDistopia
    val a = AireDistopia(oscuro, v.noche)
    val fila = m.FILA / m.ANCHO_NOMINAL
    val x = m.LADO + v.centro.coerceIn(0f, 1f) * (1f - 2 * m.LADO)
    val y = m.CORONA + v.repisa * fila + (m.ALTO_REPISA.value / m.ANCHO_NOMINAL) * 0.55f
    val total = m.CORONA + v.repisas * fila + m.BASE
    val l = m.luz(a, x, y, total, v.repisas, 0.06f)
    val hacia = m.hacia(a, x, y, total, v.repisas)
    val tono = tonoDeDiaDistopia(v.tono, oscuro)
    when (m.clase(v.semilla)) {
        0 -> archivadorDistopia(v, tono, medidor, l, hacia)
        1 -> capsulaDistopia(v, tono, medidor, l, hacia)
        else -> legajoDistopia(v, tono, medidor, l, hacia)
    }
}

/** La sombra que la entrada echa detrás (en el fondo y en la vecina), lejos de la luz. */
private fun DrawScope.sombraDetrasDistopia(forma: Path, l: LuzDistopia, hacia: Float) {
    val u = density
    drawPath(corridoDistopia(forma, -hacia * 5f * u, if (l.a.oscuro) -1.5f * u else 2.4f * u), l.s(if (l.a.oscuro) 0.5f else 0.42f))
}

/** La sombra de contacto en el doblez de la balda. */
private fun DrawScope.contactoDistopia(x0: Float, x1: Float, l: LuzDistopia, hacia: Float) {
    val u = density; val h = size.height
    drawOval(l.s(0.55f), Offset(x0 - hacia * 2f * u, h - 3f * u), Size(x1 - x0 + 3f * u, 5f * u))
}

/** La caja de la etiqueta: tan ancha como cabe el día (unos 16 sp) sin pasar del ancho del objeto [an], con su arriba en [y]. */
private fun DrawScope.rectEtiquetaDistopia(x0: Float, an: Float, y: Float, alto: Float = 29f): Rect {
    val u = density
    val ancho = minOf(an * 0.94f, 30f * u).coerceAtLeast(minOf(an, 21f * u))
    val cx = x0 + an / 2
    return Rect(cx - ancho / 2, y, cx + ancho / 2, y + alto * u)
}

/**
 * La tarjeta del día (manila de valor medio, teñida por la luz que le toca): la raya roja de las fichas arriba, el día en Oswald
 * (unos 16 sp) y el mes corto a máquina. [borde]: el filete de la ventanilla o de la abrazadera lo pone quien la llama.
 */
private fun DrawScope.tarjetaDistopia(v: Volumen, medidor: TextMeasurer, l: LuzDistopia, r: Rect, raya: Boolean = true) {
    val u = density
    val carton = l.etiqueta()
    drawRect(carton, r.topLeft, r.size)
    if (raya) drawRect(mezcla(carton, P.CARTEL, 0.6f), Offset(r.left, r.top + 2.6f * u), Size(r.width, 0.8f * u))
    // El papel gastado abajo, más sucio.
    drawRect(mezcla(carton, P.KRAFT, 0.4f).copy(alpha = 0.3f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
    val tinta = l.tinta(carton)
    val zonaDia = Rect(r.left - 0.4f * u, r.top + 3.6f * u, r.right + 0.4f * u, r.top + 20.2f * u)
    textoEn(medidor, v.dia, zonaDia, P.LETRA, tinta, 16f, peso = FontWeight.Bold, minimo = 9f)
    val zonaMes = Rect(r.left + 0.4f * u, zonaDia.bottom - 1.4f * u, r.right - 0.4f * u, r.bottom - 0.8f * u)
    textoEn(medidor, mesCortoDistopia(v.mes), zonaMes, P.LETRA_MES, tinta, 8.5f, peso = FontWeight.Medium, minimo = 6f)
    if (l.k > 0.08f) drawRect(l.filo(0.45f), r.topLeft, r.size, style = Stroke(0.9f * u))
}

/**
 * La media luna de los Sueños: un creciente recortado de hojalata (no un círculo entero, que se leería como un ojo), sujeto con
 * un remache, con su brillo de metal y su borde frío.
 */
private fun DrawScope.mediaLunaDistopia(c: Offset, r: Float, l: LuzDistopia, hacia: Float) {
    val u = density
    val luna = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val mordida = Path().apply { addOval(Rect(c.x - r * 0.35f, c.y - r * 1.2f, c.x + r * 1.65f, c.y + r * 0.8f)) }
    val creciente = Path().apply { op(luna, mordida, PathOperation.Difference) }
    drawPath(corridoDistopia(creciente, -hacia * 1.2f * u, 1.2f * u), l.s(0.5f))
    val claro = mezcla(l.c(Color(0xFFF2F0E6)), Color(0xFFF2F0E6), 0.55f); val oscuroL = mezcla(l.c(Color(0xFFB4BCBC)), Color(0xFFB4BCBC), 0.4f)
    drawPath(creciente, Brush.linearGradient(listOf(claro, oscuroL), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
    drawPath(creciente, mezcla(P.ACERO_LUZ, P.HIERRO, 0.5f), style = Stroke(0.6f * u))
    drawCircle(mezcla(P.HIERRO, P.ACERO, 0.4f), 0.9f * u, Offset(c.x - r * 0.62f, c.y))
    drawPath(creciente, l.frio(0.5f))
}

// ---------------------------------------------------------------- el archivador de palanca

/** La tela gris de cuando no hay ánimo: cartón jaspeado gris, sin teñir. */
private val SIN_ANIMO_TELA_DISTOPIA = Color(0xFF8C8E88)

private fun DrawScope.archivadorDistopia(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzDistopia, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    // Algo más estrecho que su sitio, y alguno ladeado contra el vecino.
    val giro = if (azar(s, 6) < 0.24f) (if (azar(s, 7) < 0.5f) -3f else 3f) * (0.6f + 0.4f * azar(s, 8)) else 0f
    val an = w * (if (giro != 0f) 0.84f else 0.88f + 0.08f * azar(s, 3))
    val x0 = (w - an) * (if (giro > 0f) 0.1f else if (giro < 0f) 0.9f else 0.5f)
    val top = h * (0.012f + 0.02f * azar(s, 5))
    val telaBase = tono?.let { materialDelAnimoDistopia(it, P.TELAS) } ?: SIN_ANIMO_TELA_DISTOPIA
    val tela = if (tono != null) l.animo(telaBase) else l.c(telaBase)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        val lomo = Path().apply { addRoundRect(RoundRect(Rect(x0, top, x0 + an, h - 0.5f * u), CornerRadius(1.6f * u))) }
        sombraDetrasDistopia(lomo, l, hacia)
        contactoDistopia(x0, x0 + an, l, hacia)
        planosDistopia(lomo, tela, l, hacia, 0.2f, 0.26f)
        clipPath(lomo) {
            // La trama de la tela: hilos finos a lo largo, en un solo camino, y el roce de los cantos (la tela clarea).
            val trama = Path()
            var xx = x0 + an * 0.1f; var j = 0
            while (xx < x0 + an * 0.92f && j < 30) { trama.moveTo(xx, top); trama.lineTo(xx, h); xx += 2.4f * u; j++ }
            drawPath(trama, l.s(0.07f), style = Stroke(0.6f * u))
            drawRect(mezcla(tela, P.HUESO, 0.25f).copy(alpha = 0.5f), Offset(x0, top), Size(an, 1.6f * u))
            // Las esquinas de arriba gastadas: el cartón gris asoma bajo la tela.
            drawPath(poligonoDistopia(Offset(x0, top), Offset(x0 + an * 0.16f, top), Offset(x0, top + h * 0.03f)), l.c(Color(0xFF8E8676)))
            if (azar(s, 9) < 0.5f) drawPath(poligonoDistopia(Offset(x0 + an, top), Offset(x0 + an * 0.86f, top), Offset(x0 + an, top + h * 0.025f)), l.c(Color(0xFF8E8676)))
            // Los dos surcos de las bisagras de las tapas, a lo largo, junto a los cantos.
            for (fx in listOf(0.08f, 0.92f)) {
                drawLine(l.s(0.3f), Offset(x0 + an * fx, top + 2f * u), Offset(x0 + an * fx, h - 3f * u), 1.1f * u)
                drawLine(l.b(0.22f), Offset(x0 + an * fx + 1.1f * u, top + 2f * u), Offset(x0 + an * fx + 1.1f * u, h - 3f * u), 0.6f * u)
            }
            // El sello de goma de «revisado» en algunos: un recuadro rojo desvaído y torcido, sin letras.
            if (azar(s, 11) < 0.35f) {
                val c = Offset(x0 + an * 0.5f, h * (0.56f + 0.06f * azar(s, 12)))
                rotate(-12f + 24f * azar(s, 13), c) {
                    drawRect(mezcla(tela, P.CARTEL, 0.65f).copy(alpha = 0.7f), Offset(c.x - an * 0.36f, c.y - an * 0.2f), Size(an * 0.72f, an * 0.4f), style = Stroke(1.2f * u))
                    drawRect(mezcla(tela, P.CARTEL, 0.5f).copy(alpha = 0.5f), Offset(c.x - an * 0.26f, c.y - an * 0.04f), Size(an * 0.52f, 1.2f * u))
                }
            }
        }
        // El canto de metal de abajo, remachado.
        val lm = Rect(x0, h - 4.6f * u, x0 + an, h - 0.5f * u)
        drawRect(Brush.verticalGradient(listOf(l.c(P.ACERO_LUZ), l.c(P.ACERO), l.c(P.ACERO_OSC)), lm.top, lm.bottom), lm.topLeft, lm.size)
        drawLine(l.b(0.7f), Offset(lm.left, lm.top + 0.6f * u), Offset(lm.right, lm.top + 0.6f * u), 0.8f * u)
        drawCircle(l.c(P.HIERRO), 0.9f * u, Offset(x0 + an * 0.5f, lm.center.y))
        // El ojal del dedo con su aro de metal, abajo.
        val o = Offset(x0 + an * 0.5f, h * (0.8f - 0.03f * azar(s, 14)))
        val ra = minOf(an * 0.26f, 7f * u)
        drawCircle(l.a.hondo.copy(alpha = 0.85f), ra, o)
        drawCircle(l.s(0.6f), ra * 0.8f, o + Offset(-hacia * ra * 0.2f, ra * 0.2f))
        drawCircle(l.c(P.ACERO_LUZ), ra, o, style = Stroke(1.9f * u))
        drawArc(l.b(0.85f), if (hacia < 0f) 190f else 280f, 70f, false, Offset(o.x - ra, o.y - ra), Size(ra * 2, ra * 2), style = Stroke(0.8f * u))
        drawArc(l.s(0.5f), if (hacia < 0f) 10f else 100f, 70f, false, Offset(o.x - ra, o.y - ra), Size(ra * 2, ra * 2), style = Stroke(0.8f * u))
        // La ventanilla del lomo: el marco de metal prensado con la tarjeta del día dentro y el brillo del celuloide en una esquina.
        val eti = rectEtiquetaDistopia(x0, an, top + h * (0.05f + 0.06f * azar(s, 15)))
        val marco = eti.inflate(1.6f * u)
        drawRect(l.s(0.4f), marco.topLeft + Offset(-hacia * 1f * u, 1.2f * u), marco.size)
        drawRect(l.c(mezcla(P.ACERO_LUZ, P.ACERO, 0.3f)), marco.topLeft, marco.size)
        drawRect(l.b(0.6f), marco.topLeft, Size(marco.width, 0.9f * u))
        tarjetaDistopia(v, medidor, l, eti)
        drawPath(poligonoDistopia(Offset(eti.left, eti.top), Offset(eti.left + eti.width * 0.3f, eti.top), Offset(eti.left, eti.top + eti.height * 0.22f)), l.b(0.35f))
        drawRect(l.s(0.25f), Offset(eti.left, eti.top), Size(eti.width, 1f * u))
        if (l.k > 0.06f) drawLine(l.filo(0.75f), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, top + 2f * u), Offset(if (hacia < 0f) x0 + 1f * u else x0 + an - 1f * u, h - 5f * u), 1.2f * u)
        if (l.luna > 0.05f) drawLine(l.frio(0.6f), Offset(x0 + 1f * u, top + 2f * u), Offset(x0 + 1f * u, h - 5f * u), 1f * u)
        if (v.noche) mediaLunaDistopia(Offset(x0 + an * 0.5f, eti.bottom + (o.y - ra - eti.bottom) * 0.5f), minOf(an * 0.24f, 6f * u), l, hacia)
    }
}

// ---------------------------------------------------------------- la cápsula del correo neumático

/** El esmalte de cuando no hay ánimo: el aluminio sin pintar. */
private val SIN_ANIMO_ESMALTE_DISTOPIA = Color(0xFFA9ADA6)

/**
 * La cápsula del correo neumático, de pie: un tope de fieltro oscuro en cada extremo, más ancho que el cuerpo (lo que la frena en el
 * tubo); arriba, la tapa de acero pavonado con sus dos orejetas de bayoneta; el cuerpo de esmalte del color del ánimo con la
 * tarjeta de destino bajo su fleje y, debajo, la ventanilla por la que se ve el expediente enrollado dentro. Alguna, recostada
 * contra la vecina; alguna, más corta; si se escribió mucho, va sin tapa y el rollo asoma por arriba.
 */
private fun DrawScope.capsulaDistopia(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzDistopia, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 26) < 0.28f) (if (azar(s, 27) < 0.5f) -3.5f else 3.5f) else 0f
    val an = minOf(w * (0.62f + 0.12f * azar(s, 22)), 27f * u).coerceAtLeast(minOf(w * 0.78f, 19f * u))
    val topeAn = minOf(an * 1.26f, w * 0.97f)
    val x0 = (w - an) * (if (giro > 0f) 0.3f else if (giro < 0f) 0.7f else 0.35f + 0.3f * azar(s, 23))
    val cx = x0 + an / 2
    val abierta = v.lleno > 0.45f && azar(s, 24) < 0.6f
    val top = h * (if (abierta) 0.08f else 0.012f)
    val esmalteBase = tono?.let { materialDelAnimoDistopia(it, P.ESMALTES) } ?: SIN_ANIMO_ESMALTE_DISTOPIA
    val esmalte = if (tono != null) l.animo(esmalteBase) else l.c(esmalteBase)
    val fieltro = l.c(mezcla(P.OLIVA, P.BAQUELITA, 0.6f))
    val pavonado = mezcla(P.ACERO, P.HIERRO, 0.45f)
    val capAl = h * 0.06f
    val topeAl = h * 0.085f
    val baseAl = h * 0.035f
    val topeA = Rect(cx - topeAn / 2, top + capAl * 0.8f, cx + topeAn / 2, top + capAl * 0.8f + topeAl)
    val topeB = Rect(cx - topeAn / 2, h - 0.5f * u - baseAl - topeAl, cx + topeAn / 2, h - 0.5f * u - baseAl)
    val cuerpoR = Rect(x0, topeA.bottom - 1.5f * u, x0 + an, topeB.top + 1.5f * u)
    rotate(giro, Offset(if (giro > 0f) cx + topeAn / 2 else cx - topeAn / 2, h)) {
        // La silueta (para la sombra en el fondo): el cuerpo, los topes y las tapas.
        val forma = Path().apply {
            addRect(Rect(x0, top, x0 + an, h - 0.5f * u))
            addRoundRect(RoundRect(topeA, CornerRadius(topeAl * 0.45f)))
            addRoundRect(RoundRect(topeB, CornerRadius(topeAl * 0.45f)))
        }
        sombraDetrasDistopia(forma, l, hacia)
        contactoDistopia(cx - topeAn / 2, cx + topeAn / 2, l, hacia)
        // Si va abierta: el rollo de papel que asoma por la boca, con su vuelta.
        if (abierta) {
            val rw = an * 0.64f
            val rollo = Rect(cx - rw / 2, h * 0.012f, cx + rw / 2, topeA.top + topeAl * 0.4f)
            drawRect(l.c(P.HUESO), rollo.topLeft, rollo.size)
            drawRect(l.s(0.2f), Offset(if (hacia < 0f) rollo.right - rw * 0.3f else rollo.left, rollo.top), Size(rw * 0.3f, rollo.height))
            drawOval(l.c(mezcla(P.HUESO, P.CARPETA, 0.35f)), Offset(rollo.left, rollo.top - rw * 0.12f), Size(rw, rw * 0.24f))
            drawArc(l.s(0.35f), 180f, 300f, false, Offset(rollo.left + rw * 0.25f, rollo.top - rw * 0.06f), Size(rw * 0.5f, rw * 0.12f), style = Stroke(0.7f * u))
        }
        // El cuerpo esmaltado, con su volumen de cilindro y sus roces.
        val cuerpo = Path().apply { addRect(cuerpoR) }
        cilindroDistopia(cuerpo, esmalte, l, hacia, 0.5f)
        clipPath(cuerpo) {
            for (j in 0 until 2) {
                val y = cuerpoR.top + cuerpoR.height * (0.15f + 0.65f * azar(s, 30 + j))
                drawLine(l.b(0.25f), Offset(x0 + an * 0.15f, y), Offset(x0 + an * (0.45f + 0.3f * azar(s, 33 + j)), y - 0.6f * u), 0.7f * u)
            }
        }
        // La tarjeta de destino, arriba del cuerpo, metida bajo su fleje de acero.
        val eti = rectEtiquetaDistopia(x0, an, cuerpoR.top + 3.4f * u)
        drawRect(l.s(0.4f), eti.topLeft + Offset(-hacia * 1.2f * u, 1.4f * u), eti.size)
        tarjetaDistopia(v, medidor, l, eti, raya = false)
        val fleje = Rect(x0 - 0.6f * u, eti.top - 2.2f * u, x0 + an + 0.6f * u, eti.top + 1.2f * u)
        drawRect(Brush.verticalGradient(listOf(l.c(P.ACERO_LUZ), l.c(P.ACERO)), fleje.top, fleje.bottom), fleje.topLeft, fleje.size)
        drawRect(l.s(0.35f), Offset(fleje.left, fleje.bottom), Size(fleje.width, 0.9f * u))
        // La ventanilla, debajo: el marco de acero y, llenándola de lado a lado, el expediente enrollado (papel manila claro, con
        // dos o tres vueltas del canto a la vista y la sombra del marco arriba). Sin brillo: el celuloide viejo está mate.
        val ve = Rect(cx - an * 0.3f, eti.bottom + 3.2f * u, cx + an * 0.3f, cuerpoR.bottom - 3f * u)
        if (ve.height > 6f * u) {
            val vent = Path().apply { addRoundRect(RoundRect(ve, CornerRadius(minOf(ve.width * 0.3f, 3f * u)))) }
            clipPath(vent) {
                // El rollo: el papel con su volumen (más claro del lado de la luz, más hondo del otro).
                val papel = l.c(mezcla(P.CARPETA, P.HUESO, 0.45f))
                val hondo = mezcla(papel, l.s(1f).copy(alpha = 1f), 0.3f)
                drawRect(Brush.horizontalGradient(if (hacia < 0f) listOf(mezcla(papel, Color.White, 0.08f), papel, hondo) else listOf(hondo, papel, mezcla(papel, Color.White, 0.08f)), ve.left, ve.right), ve.topLeft, ve.size)
                // Las vueltas del canto: el borde de cada hoja, que baja en diagonal suave, con su sombra debajo y su filo de luz.
                val vueltas = if (ve.height > 18f * u) 3 else 2
                for (j in 0 until vueltas) {
                    val y0 = ve.top + ve.height * (0.3f + 0.24f * j + 0.06f * azar(s, 70 + j))
                    val caida = ve.height * 0.1f
                    drawLine(l.s(0.45f), Offset(ve.left, y0), Offset(ve.right, y0 + caida), 1f * u)
                    drawLine(l.b(0.3f), Offset(ve.left, y0 - 1f * u), Offset(ve.right, y0 + caida - 1f * u), 0.6f * u)
                }
                // La sombra que el marco echa arriba, dentro.
                drawRect(Brush.verticalGradient(listOf(l.s(0.5f), l.s(0f)), ve.top, ve.top + ve.height * 0.2f), ve.topLeft, Size(ve.width, ve.height * 0.2f))
            }
            drawPath(vent, l.c(mezcla(P.ACERO_LUZ, P.ACERO, 0.3f)), style = Stroke(1.3f * u))
        }
        if (l.k > 0.06f) drawPath(bordeDistopia(cuerpo, -hacia * 1.4f * u, 0f), l.filo(0.75f))
        if (l.luna > 0.05f) drawPath(bordeDistopia(cuerpo, 1.2f * u, 0f), l.frio(0.55f))
        // Los topes de fieltro, oscuros y más anchos que el cuerpo, con su pespunte y su pelo.
        for (t in listOf(topeA, topeB)) {
            val tp = Path().apply { addRoundRect(RoundRect(t, CornerRadius(t.height * 0.45f))) }
            drawPath(corridoDistopia(tp, -hacia * 1.4f * u, 1.6f * u), l.s(0.45f))
            cilindroDistopia(tp, fieltro, l, hacia, 0.14f)
            val pes = Path()
            var xp = t.left + t.height * 0.4f; var jp = 0
            while (xp < t.right - t.height * 0.4f && jp < 30) { pes.addRect(Rect(xp, t.center.y - 0.4f * u, xp + 1.4f * u, t.center.y + 0.4f * u)); xp += 2.8f * u; jp++ }
            drawPath(pes, l.b(0.28f))
            drawLine(l.b(0.3f), Offset(t.left + t.height * 0.4f, t.top + 0.8f * u), Offset(t.right - t.height * 0.4f, t.top + 0.8f * u), 0.7f * u)
            drawLine(l.s(0.5f), Offset(t.left + t.height * 0.3f, t.bottom - 0.6f * u), Offset(t.right - t.height * 0.3f, t.bottom - 0.6f * u), 1f * u)
            if (l.k > 0.06f) drawPath(bordeDistopia(tp, -hacia * 1.2f * u, 0f), l.filo(0.6f))
        }
        // La tapa de abajo, corta, de acero pavonado.
        val base = Path().apply { addRoundRect(RoundRect(Rect(x0 + an * 0.06f, topeB.bottom - 1f * u, x0 + an * 0.94f, h - 0.5f * u), CornerRadius(an * 0.2f, baseAl * 0.5f))) }
        cilindroDistopia(base, l.c(pavonado), l, hacia, 0.6f)
        // Arriba: la tapa pavonada con sus dos orejetas de bayoneta (o, sin tapa, la boca con el rollo).
        if (!abierta) {
            val tapa = Rect(x0 + an * 0.04f, top, x0 + an * 0.96f, topeA.top + 1.5f * u)
            listOf(tapa.left - an * 0.1f, tapa.right - an * 0.04f).forEach { ox ->
                drawRoundRect(l.c(mezcla(pavonado, P.ACERO_LUZ, 0.25f)), Offset(ox, tapa.bottom - capAl * 0.72f), Size(an * 0.14f, capAl * 0.5f), CornerRadius(1.2f * u))
            }
            val tp = Path().apply { addRoundRect(RoundRect(tapa, CornerRadius(an * 0.24f, capAl * 0.55f))) }
            cilindroDistopia(tp, l.c(pavonado), l, hacia, 0.7f)
            drawLine(l.b(0.6f), Offset(tapa.left + an * 0.15f, top + 1.2f * u), Offset(tapa.right - an * 0.15f, top + 1.2f * u), 0.9f * u)
            drawLine(l.s(0.45f), Offset(tapa.left, tapa.bottom - capAl * 0.3f), Offset(tapa.right, tapa.bottom - capAl * 0.3f), 0.8f * u)
        } else {
            val boca = Rect(x0 + an * 0.04f, topeA.top - capAl * 0.3f, x0 + an * 0.96f, topeA.top + capAl * 0.3f)
            drawOval(l.c(pavonado), boca.topLeft, boca.size)
            val rw = an * 0.64f
            drawRect(l.c(P.HUESO), Offset(cx - rw / 2, h * 0.012f), Size(rw, boca.center.y - h * 0.012f))
            drawRect(l.s(0.2f), Offset(if (hacia < 0f) cx + rw * 0.2f else cx - rw / 2, h * 0.012f), Size(rw * 0.3f, boca.center.y - h * 0.012f))
        }
        if (v.noche) mediaLunaDistopia(Offset(cx, ve.top + minOf(ve.height * 0.5f, 9f * u)), minOf(an * 0.24f, 5.6f * u), l, hacia)
    }
}

// ---------------------------------------------------------------- el legajo atado

/** La camisa de cuando no hay ánimo: el cartón gris de siempre. */
private val SIN_ANIMO_CAMISA_DISTOPIA = Color(0xFF9A9888)

private fun DrawScope.legajoDistopia(v: Volumen, tono: Color?, medidor: TextMeasurer, l: LuzDistopia, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla
    val giro = if (azar(s, 50) < 0.28f) (if (azar(s, 49) < 0.5f) -3.5f else 3.5f) else 0f
    val an = w * (if (giro != 0f) 0.84f else 0.9f + 0.06f * azar(s, 51))
    val x0 = (w - an) * (if (giro > 0f) 0.12f else if (giro < 0f) 0.88f else 0.5f)
    val papeles = h * (0.06f + 0.07f * v.lleno + 0.02f * azar(s, 52))
    val top = h * 0.012f + papeles
    val panza = an * (0.012f + 0.022f * v.lleno)
    val camisaBase = tono?.let { materialDelAnimoDistopia(it, P.CAMISAS) } ?: SIN_ANIMO_CAMISA_DISTOPIA
    val camisa = if (tono != null) l.animo(camisaBase) else l.c(camisaBase)
    rotate(giro, Offset(if (giro > 0f) x0 + an else x0, h)) {
        // La camisa doblada: un bloque de cartón de cantos vivos, apenas abombado por los papeles de dentro; el lomo del doblez,
        // redondeado, del lado de la luz.
        val e = minOf(an * 0.12f, 3f * u)
        val forma = Path().apply {
            moveTo(x0 + e, top)
            lineTo(x0 + an - e * 0.6f, top + h * 0.006f)
            quadraticTo(x0 + an, top + h * 0.006f, x0 + an, top + e)
            quadraticTo(x0 + an + panza, h * 0.55f, x0 + an, h - 2f * u)
            lineTo(x0, h - 2f * u)
            quadraticTo(x0 - panza, h * 0.55f, x0, top + e)
            quadraticTo(x0, top, x0 + e, top)
            close()
        }
        // Los papeles que asoman por arriba: hojas de distintos papeles, desiguales, alguna doblada.
        val hojas = 2 + (v.lleno * 4f).toInt()
        val colores = listOf(P.HUESO, mezcla(P.HUESO, P.CARPETA, 0.45f), Color(0xFFE6D8D2), mezcla(P.HUESO, P.ACERO_LUZ, 0.4f))
        val silueta = Path().apply { addPath(forma); addRect(Rect(x0 + an * 0.06f, top - papeles, x0 + an * 0.92f, top + 2f)) }
        sombraDetrasDistopia(silueta, l, hacia)
        contactoDistopia(x0 - panza * 0.5f, x0 + an + panza * 0.5f, l, hacia)
        for (j in 0 until hojas) {
            val alto = papeles * (0.45f + 0.55f * azar(s, 60 + j))
            val hx0 = x0 + an * (0.08f + 0.1f * azar(s, 70 + j))
            val hx1 = x0 + an * (0.82f + 0.12f * azar(s, 80 + j))
            val inc = (azar(s, 90 + j) - 0.5f) * papeles * 0.5f
            val hoja = poligonoDistopia(Offset(hx0, top + 2f), Offset(hx0, top - alto + inc), Offset(hx1, top - alto - inc), Offset(hx1, top + 2f))
            drawPath(hoja, l.c(colores[(j + s) and 3]))
            drawLine(l.b(0.5f), Offset(hx0, top - alto + inc), Offset(hx1, top - alto - inc), 0.8f * u)
            drawLine(l.s(0.25f), Offset(hx1, top - alto - inc), Offset(hx1, top), 0.8f * u)
        }
        // Una hoja doblada por la esquina, encima de todo.
        val ex = x0 + an * (0.2f + 0.4f * azar(s, 95))
        drawPath(poligonoDistopia(Offset(ex, top - papeles * 0.6f), Offset(ex + an * 0.18f, top - papeles * 0.85f), Offset(ex + an * 0.12f, top - papeles * 0.4f)), l.c(mezcla(P.HUESO, P.CARPETA, 0.2f)))
        planosDistopia(forma, camisa, l, hacia, 0.2f, 0.34f)
        clipPath(forma) {
            // El lomo del doblez: una franja redondeada en el lado de la luz, más clara, con su pliegue.
            val lomoX = if (hacia < 0f) x0 else x0 + an * 0.86f
            drawRect(l.b(0.18f), Offset(lomoX, top), Size(an * 0.14f, h))
            drawLine(l.s(0.3f), Offset(x0 + an * (if (hacia < 0f) 0.15f else 0.85f), top + 2f * u), Offset(x0 + an * (if (hacia < 0f) 0.15f else 0.85f), h - 2f * u), 1f * u)
            // Del otro lado, la boca de la camisa: el canto del fajo de papeles entre las dos tapas, desigual.
            val bx = if (hacia < 0f) x0 + an * 0.9f else x0
            val canto = Path().apply {
                moveTo(bx, top + h * 0.03f)
                for (j in 1..6) lineTo(bx + an * 0.1f * (0.3f + 0.7f * azar(s, 110 + j)), top + (h - top) * j / 6f)
                lineTo(bx, h - 2f * u); close()
            }
            drawPath(canto, l.c(mezcla(P.HUESO, P.CARPETA, 0.3f)))
            drawPath(canto, l.s(0.25f), style = Stroke(0.6f * u))
            // El cartón gastado en la cabeza y el pie, y una mancha de humedad.
            drawRect(mezcla(camisa, P.HUESO, 0.2f).copy(alpha = 0.5f), Offset(x0 - panza, top), Size(an + 2 * panza, 1.8f * u))
            drawOval(l.c(P.OXIDO).copy(alpha = 0.18f), Offset(x0 + an * (0.1f + 0.4f * azar(s, 96)), h * (0.78f + 0.08f * azar(s, 97))), Size(an * 0.5f, h * 0.1f))
        }
        if (l.k > 0.06f) drawPath(bordeDistopia(forma, -hacia * 1.4f * u, 0f), l.filo(0.75f))
        if (l.luna > 0.05f) drawPath(bordeDistopia(forma, 1.2f * u, 0f), l.frio(0.55f))
        // La carátula pegada en la camisa, con el día; un poco torcida.
        val eti = rectEtiquetaDistopia(x0, an, top + h * (0.08f + 0.06f * azar(s, 99)))
        rotate((azar(s, 100) - 0.5f) * 4f, eti.center) {
            drawRect(l.s(0.3f), eti.topLeft + Offset(-hacia * 0.8f * u, 1f * u), eti.size)
            tarjetaDistopia(v, medidor, l, eti)
            // Las esquinas de la carátula, despegadas, con su sombra.
            drawPath(poligonoDistopia(Offset(eti.right, eti.bottom), Offset(eti.right - 4.4f * u, eti.bottom), Offset(eti.right, eti.bottom - 4.4f * u)), l.s(0.35f))
        }
        // El balduque: una vuelta a lo ancho, apretada, con el nudo delante y el precinto de plomo colgando de su cordel.
        val yb = eti.bottom + (h - eti.bottom) * (0.42f + 0.14f * azar(s, 101))
        val banda = Path().apply {
            moveTo(x0 - panza * 0.6f, yb - 1.6f * u); quadraticTo(x0 + an * 0.5f, yb + 0.6f * u, x0 + an + panza, yb - 1.6f * u)
            lineTo(x0 + an + panza, yb + 1.6f * u); quadraticTo(x0 + an * 0.5f, yb + 3.6f * u, x0 - panza * 0.6f, yb + 1.6f * u); close()
        }
        drawPath(corridoDistopia(banda, -hacia * 0.8f * u, 1.4f * u), l.s(0.4f))
        drawPath(banda, l.c(P.CARTEL))
        drawPath(bordeDistopia(banda, 0f, 1.2f * u), l.b(0.35f))
        val nudo = Offset(x0 + an * (0.3f + 0.4f * azar(s, 102)), yb + 0.8f * u)
        drawOval(l.c(P.BURDEOS), Offset(nudo.x - 2.6f * u, nudo.y - 2.4f * u), Size(5.2f * u, 4.8f * u))
        drawOval(l.b(0.4f), Offset(nudo.x - 1.6f * u, nudo.y - 1.8f * u), Size(2f * u, 1.4f * u))
        // Los dos cabos del balduque, cortos, y el cordel con el plomo.
        drawLine(l.c(P.CARTEL), nudo, nudo + Offset(-2.6f * u, 5f * u), 1.8f * u, StrokeCap.Butt)
        drawLine(l.c(P.CARTEL), nudo, nudo + Offset(2.2f * u, 5.6f * u), 1.8f * u, StrokeCap.Butt)
        val plomo = Offset(nudo.x + 0.6f * u, minOf(h - 6f * u, nudo.y + 11f * u))
        drawLine(l.c(mezcla(P.HUESO, P.CARPETA, 0.5f)), nudo, plomo, 0.7f * u)
        drawCircle(l.s(0.45f), 3f * u, plomo + Offset(-hacia * 0.8f * u, 1f * u))
        drawCircle(l.c(P.PLOMO), 3f * u, plomo)
        drawCircle(l.s(0.3f), 1.7f * u, plomo, style = Stroke(0.6f * u))
        drawArc(l.b(0.7f), 190f, 80f, false, Offset(plomo.x - 3f * u, plomo.y - 3f * u), Size(6f * u, 6f * u), style = Stroke(0.8f * u))
        if (v.noche) mediaLunaDistopia(Offset(x0 + an * (if (nudo.x > x0 + an * 0.5f) 0.28f else 0.72f), yb + (h - yb) * 0.5f), minOf(an * 0.24f, 5.6f * u), l, hacia)
    }
}
