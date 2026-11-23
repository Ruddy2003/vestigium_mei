package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ALGODON
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AMATE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.AMATE_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.CINABRIO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ESTUCO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.HENEQUEN_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.JADE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.JADE_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.VENADO
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.MesoTinta.ZAPOTE_OSC
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Los volúmenes del cuarto de guardar (MuebleEstanteMeso): tres cosas del escriba, según la semilla, cada una modelada como los
// objetos del Ideario (plano de luz, plano de sombra, brillo, detalles finos, su sombra en el muro y en la tabla).

/** Lo que un volumen sabe de su luz: el aire, las lumbres ([k]), la puerta ([f]), de qué lado le llega ([der]) y su ánimo de día. */
private class LuzVolumenMeso(val a: AireMeso, val k: Float, val f: Float, val der: Boolean, val tono: Color?)

/** El nácar de la media luna de concha de los Sueños. */
private val NACAR_MESO = Color(0xFFF2EEE2)

internal fun DrawScope.volumenMeso(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireMeso(oscuro, v.noche)
    val lv = luzVolumenMeso(v, oscuro)
    val l = LuzVolumenMeso(a, lv.k, lv.f, lv.der, v.tono?.let { animoMundoMeso(tonoDeDiaMeso(it, oscuro)) })
    when (MuebleEstanteMeso.clase(v.semilla)) {
        0 -> codiceMeso(v, medidor, l)
        1 -> bolsaMeso(v, medidor, l)
        else -> rolloMeso(v, medidor, l)
    }
}

/** El color del cuerpo: el del ánimo (de día) si lo hay, con la luz que le llega; si no, el del material crudo. */
private fun cuerpoMeso(l: LuzVolumenMeso, crudo: Color): Color = l.tono?.let { l.a.animo(it, l.k, l.f) } ?: l.a.c(crudo, l.k, l.f)

/** El mes corto: lo que va entre paréntesis («Sak'–Mak (nov.)» queda en «nov.»); si no hay, entero. */
private fun mesCortoMeso(v: Volumen): String {
    val i = v.mes.indexOf('('); val j = v.mes.indexOf(')')
    return if (i >= 0 && j > i + 1) v.mes.substring(i + 1, j).trim() else v.mes
}

/**
 * La sombra que el volumen echa en el muro del fondo (fuera de su lienzo; el vecino de la derecha la tapa donde se encima) y
 * en la tabla: de día, corrida a la derecha y abajo, más honda dentro del sol; de noche, hacia fuera de la lumbre.
 */
private fun DrawScope.sombraVolumenMeso(forma: Path, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    if (!a.oscuro) translate(6f * u, 3f * u) { drawPath(forma, a.s(0.22f * (1f + 0.6f * l.f))) }
    else {
        if (l.k > 0.12f) translate(if (l.der) -5f * u else 5f * u, -1.5f * u) { drawPath(forma, MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.9f * l.k).coerceAtMost(0.45f))) }
        else if (l.f > 0.2f) translate(5f * u, 3f * u) { drawPath(forma, MesoTinta.NOCHE_SOMBRA.copy(alpha = 0.32f * l.f)) }
    }
    val w = size.width; val h = size.height
    drawOval(a.s(0.45f), Offset(-1f * u, h - 2.6f * u), Size(w + 5f * u, 4.6f * u))
}

/** La luz de la puerta sobre lo que toca: el sol (o el ocaso, o la luna) que entra por la izquierda, con su filo de ese lado. */
private fun DrawScope.puertaEnVolumenMeso(r: Rect, l: LuzVolumenMeso) {
    if (l.f < 0.02f) return
    val m = if (l.a.oscuro) 0.7f else 1f
    drawRect(l.a.puerta(0.08f * l.f * m), r.topLeft, r.size)
    drawRect(l.a.puerta(0.3f * l.f * m), r.topLeft, Size(r.width * 0.18f, r.height))
}

/**
 * La cartela con el día (y el mes, chico, debajo): estuco o amate claro que casi no se apaga de noche, con su filete rojo, su
 * sombra y su filo de luz. Mide lo justo para el día a unos 16 sp.
 */
private fun DrawScope.cartelaMeso(r: Rect, v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso, base: Color, filete: Boolean, irregular: Boolean) {
    val u = density
    if (r.width < 4f || r.height < 6f) return
    val a = l.a
    val forma = if (irregular) contornoMeso(r.inflate(0.4f * u), v.semilla and 0xffff, 0.04f) else Path().apply { addRoundRect(RoundRect(r, CornerRadius(2f * u))) }
    drawPath(corridoMeso(forma, 1.2f * u, 1.4f * u), a.s(0.4f))
    drawPath(forma, a.etiqueta(l.k, base))
    clipPath(forma) {
        if (irregular) {
            val fibras = Path()
            for (j in 0 until 5) { val y = r.top + r.height * azar(v.semilla, j + 60); fibras.moveTo(r.left, y); fibras.lineTo(r.right, y + (azar(v.semilla, j + 61) - 0.5f) * 3f * u) }
            drawPath(fibras, a.c(AMATE_OSC, maxOf(l.k, 0.5f)).copy(alpha = 0.22f), style = Stroke(0.5f * u))
        }
        drawRect(a.b(0.5f, l.k), r.topLeft, Size(r.width, 0.8f * u))
        drawRect(a.s(0.16f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
    }
    if (filete) drawRoundRect(a.rubrica.copy(alpha = 0.85f), Offset(r.left + 1.6f * u, r.top + 1.6f * u), Size(r.width - 3.2f * u, r.height - 3.2f * u), CornerRadius(1.2f * u), style = Stroke(0.7f * u))
    val alto = 18f * u
    val dia = Rect(r.left + 0.6f * u, r.top + 0.8f * u, r.right - 0.6f * u, minOf(r.bottom, r.top + alto))
    textoEn(medidor, v.dia, dia, MuebleEstanteMeso.LETRA, a.tinta, 16f, peso = FontWeight.Bold, minimo = 8f)
    val mes = Rect(r.left + 1.6f * u, dia.bottom - 1.6f * u, r.right - 1.6f * u, r.bottom - 1.4f * u)
    if (mes.height > 5f * u) textoEn(medidor, mesCortoMeso(v), mes, MuebleEstanteMeso.LETRA, a.rubrica, 8.5f, peso = FontWeight.SemiBold, minimo = 5f)
}

/** La media luna de concha de los Sueños, colgada de un hilo en [c]: el nácar con su brillo y su canto. */
private fun DrawScope.lunaConchaMeso(c: Offset, r: Float, l: LuzVolumenMeso, fondo: Color) {
    val u = density
    val a = l.a
    val nacar = if (a.oscuro) mezcla(Color(0xFFB8BED6), NACAR_MESO, (0.45f + l.k).coerceAtMost(1f)) else NACAR_MESO
    drawCircle(a.s(0.4f), r, c + Offset(1f * u, 1.2f * u))
    drawCircle(nacar, r, c)
    drawCircle(fondo, r * 0.82f, c + Offset(r * 0.42f, -r * 0.2f))
    drawArc(a.b(0.6f, l.k), 120f, 120f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(0.6f * u))
}

// ---------------------------------------------------------------- el códice cerrado

/**
 * El códice cerrado, de pie: las tapas de madera estucadas y pintadas del color del ánimo, con su filete pintado, una franja de
 * dientes abajo y las esquinas gastadas donde asoma el chicozapote; en el canto, los pliegues del biombo (más cuanto más
 * escribiste) entre las dos tapas; el cordón de henequén que lo ciñe con su cuenta de jade (en los Sueños, la media luna de
 * concha); la cartela de estuco con el día y el mes.
 */
private fun DrawScope.codiceMeso(v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val canto = minOf(maxOf(w * 0.17f, 4.5f * u), 7.5f * u)
    val tapa = Rect(0f, 0f, w - canto, h)
    val silueta = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(1.6f * u))) }
    sombraVolumenMeso(silueta, l)
    // El canto: la tapa de atrás (una raya), las hojas plegadas en zigzag y el borde de la tapa de delante.
    val hojas = Rect(tapa.right, h * 0.012f, w - 1.2f * u, h * 0.988f)
    drawRect(a.c(ZAPOTE_OSC, l.k, l.f), Offset(w - 1.6f * u, 0f), Size(1.6f * u, h))
    drawRect(a.c(mezcla(AMATE, ESTUCO, 0.5f), l.k, l.f), hojas.topLeft, hojas.size)
    val pliegues = 4 + (v.lleno * 7f).toInt()
    val zig = Path()
    for (j in 0 until pliegues) {
        val x = hojas.left + hojas.width * (j + 0.5f) / pliegues
        zig.moveTo(x, hojas.top); zig.lineTo(x, hojas.bottom)
    }
    drawPath(zig, a.s(0.32f), style = Stroke(0.6f * u))
    drawRect(a.s(0.18f), Offset(hojas.left, hojas.top), Size(hojas.width * 0.4f, hojas.height))
    drawRect(a.c(CINABRIO, l.k, l.f).copy(alpha = 0.5f), Offset(hojas.left, hojas.top), Size(hojas.width, 1.2f * u))
    drawRect(a.c(CINABRIO, l.k, l.f).copy(alpha = 0.5f), Offset(hojas.left, hojas.bottom - 1.2f * u), Size(hojas.width, 1.2f * u))
    // La tapa de delante, con su grueso de madera a la derecha.
    val forma = Path().apply { addRoundRect(RoundRect(tapa, CornerRadius(1.6f * u))) }
    val pintura = cuerpoMeso(l, ESTUCO)
    drawPath(forma, pintura)
    clipPath(forma) {
        // El grano del estuco pintado y las pasadas del pincel.
        val pasadas = Path()
        for (j in 0 until 6) { val y = h * (0.1f + 0.15f * j + 0.04f * azar(v.semilla, j)); pasadas.moveTo(0f, y); pasadas.quadraticTo(tapa.width * 0.5f, y + (azar(v.semilla, j + 7) - 0.5f) * 4f * u, tapa.width, y + 1f * u) }
        drawPath(pasadas, a.s(0.08f), style = Stroke(2.2f * u))
        // El filete pintado a todo alrededor y la franja de dientes de abajo.
        val tinta = if (luminanciaMeso(pintura) > 0.35f) a.c(TINTA, l.k, l.f) else a.c(ESTUCO, l.k, l.f)
        drawRect(tinta.copy(alpha = 0.75f), Offset(2.4f * u, 2.4f * u), Size(tapa.width - 4.8f * u, h - 4.8f * u), style = Stroke(0.8f * u))
        val fy = h * 0.87f
        drawLine(tinta.copy(alpha = 0.75f), Offset(2.4f * u, fy), Offset(tapa.width - 2.4f * u, fy), 0.8f * u)
        val dientes = Path()
        var x = 3.6f * u; var n = 0
        val paso = maxOf(3.4f * u, (tapa.width - 7.2f * u) / 6f)
        while (x + paso <= tapa.width - 3f * u && n < 12) { dientes.moveTo(x, fy + 4.6f * u); dientes.lineTo(x + paso * 0.5f, fy + 1.2f * u); dientes.lineTo(x + paso, fy + 4.6f * u); dientes.close(); x += paso; n++ }
        drawPath(dientes, a.c(CINABRIO, l.k, l.f).copy(alpha = if (l.tono != null && luminanciaMeso(pintura) < 0.2f) 0.6f else 0.85f))
        // El volumen de la tabla: el plano de luz de la puerta, la sombra del otro lado.
        drawRect(a.b(0.16f, l.k), Offset(if (l.der) tapa.width * 0.75f else 0f, 0f), Size(tapa.width * 0.25f, h))
        drawRect(a.s(0.18f), Offset(if (l.der) 0f else tapa.width * 0.78f, 0f), Size(tapa.width * 0.22f, h))
        puertaEnVolumenMeso(tapa, l)
        // Las esquinas gastadas: asoma el estuco blanco y, debajo, la madera.
        for ((cx, cy) in listOf(0f to 0f, tapa.width to h, 0f to h)) {
            val p = poligonoMeso(cx, cy, cx + (if (cx > 0f) -4f else 4f) * u, cy, cx, cy + (if (cy > 0f) -5f else 5f) * u)
            drawPath(p, a.c(ESTUCO, l.k, l.f))
            drawPath(poligonoMeso(cx, cy, cx + (if (cx > 0f) -2f else 2f) * u, cy, cx, cy + (if (cy > 0f) -2.6f else 2.6f) * u), a.c(ZAPOTE, l.k, l.f))
        }
        // El grueso de la tabla, a la derecha, en madera.
        drawRect(a.c(mezcla(ZAPOTE, ESTUCO, 0.2f), l.k, l.f), Offset(tapa.width - 1.4f * u, 0f), Size(1.4f * u, h))
    }
    drawRect(a.b(0.55f, l.k), Offset(1.6f * u, 0f), Size(tapa.width - 3.2f * u, 1f * u))
    // La cartela con el día.
    val cw = tapa.width - 4f * u
    val cartela = Rect(2f * u, h * 0.065f, 2f * u + cw, h * 0.065f + minOf(h * 0.3f, 27f * u))
    cartelaMeso(cartela, v, medidor, l, mezcla(ESTUCO, AMATE, 0.35f), filete = true, irregular = false)
    // El cordón que ciñe el códice: dos vueltas, que se hunden en el canto, y el nudo con su cuenta.
    val yc = h * 0.63f
    val cordon = Path().apply { moveTo(-1f, yc); lineTo(w + 1f, yc + 0.4f * u); moveTo(-1f, yc + 2.6f * u); lineTo(w + 1f, yc + 3f * u) }
    drawPath(corridoMeso(cordon, 0f, 1.2f * u), a.s(0.35f), style = Stroke(1.6f * u))
    drawPath(cordon, a.c(HENEQUEN, l.k, l.f), style = Stroke(1.4f * u))
    drawPath(corridoMeso(cordon, 0f, -0.5f * u), a.b(0.4f, l.k), style = Stroke(0.4f * u))
    val nudo = Offset(tapa.width * 0.32f, yc + 1.3f * u)
    drawCircle(a.c(HENEQUEN_OSC, l.k, l.f), 2.2f * u, nudo)
    drawCircle(a.b(0.45f, l.k), 0.8f * u, nudo - Offset(0.7f * u, 0.7f * u))
    val cuelga = Offset(nudo.x + 1.2f * u, nudo.y + minOf(h * 0.1f, 9f * u))
    drawLine(a.c(HENEQUEN, l.k, l.f), nudo, cuelga, 0.9f * u)
    if (v.noche) lunaConchaMeso(cuelga + Offset(0f, 2.6f * u), minOf(w * 0.15f, 4f * u), l, pintura)
    else {
        val rc = minOf(w * 0.11f, 2.8f * u)
        drawCircle(a.s(0.35f), rc, cuelga + Offset(0.9f * u, 1.6f * u))
        drawCircle(a.c(JADE, l.k, l.f), rc, cuelga + Offset(0f, rc * 0.7f))
        drawCircle(a.c(JADE_LUZ, l.k, l.f).copy(alpha = 0.7f), rc * 0.36f, cuelga + Offset(-rc * 0.3f, rc * 0.4f))
    }
    filoMeso(Rect(0f, 0f, w, h), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.5f, l.f), Offset(if (l.der) w - 1.2f * u else 0f, 0f), Size(1.2f * u, h))
}

/** La luminancia aproximada de un color (para elegir la tinta del filete). */
private fun luminanciaMeso(c: Color): Float = 0.2126f * c.red * c.red + 0.7152f * c.green * c.green + 0.0722f * c.blue * c.blue

// ---------------------------------------------------------------- la bolsa de piel de venado

/**
 * La bolsa de piel de venado, curtida y teñida del color del ánimo: panzona y vencida por su peso, fruncida en el cuello por
 * una correa de dos vueltas con su nudo; la boca fruncida en pliegues y, por ella, las hojas de amate dobladas que asoman (más
 * cuanto más escribiste); la costura de tiento por un costado, los flecos de abajo, las arrugas que bajan del cuello; colgada
 * de la correa, la etiqueta de amate con el día (en los Sueños, además, la media luna de concha).
 */
private fun DrawScope.bolsaMeso(v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val cuello = h * 0.26f
    val bocaY = h * 0.115f
    val panza = 0.02f + 0.04f * v.lleno
    val cx = w * (0.5f + 0.04f * (azar(v.semilla, 13) - 0.5f))
    val cuerpo = Path().apply {
        moveTo(cx - w * 0.27f, cuello)
        cubicTo(w * (0.0f - panza), h * 0.36f, w * (-0.01f - panza), h * 0.8f, w * 0.12f, h * 0.97f)
        quadraticTo(w * 0.5f, h * 1.01f, w * 0.88f, h * 0.97f)
        cubicTo(w * (1.01f + panza), h * 0.8f, w * (1.0f + panza), h * 0.36f, cx + w * 0.27f, cuello)
        close()
    }
    // La boca abierta en abanico: el borde de arriba festoneado por el fruncido.
    val boca = Path().apply {
        moveTo(cx - w * 0.27f, cuello + 1f * u)
        lineTo(cx - w * 0.45f, bocaY + h * 0.01f)
        quadraticTo(cx - w * 0.34f, bocaY + h * 0.035f, cx - w * 0.22f, bocaY - h * 0.005f)
        quadraticTo(cx - w * 0.11f, bocaY + h * 0.03f, cx, bocaY - h * 0.01f)
        quadraticTo(cx + w * 0.11f, bocaY + h * 0.03f, cx + w * 0.22f, bocaY - h * 0.005f)
        quadraticTo(cx + w * 0.34f, bocaY + h * 0.035f, cx + w * 0.45f, bocaY + h * 0.005f)
        lineTo(cx + w * 0.27f, cuello + 1f * u)
        close()
    }
    sombraVolumenMeso(Path().apply { addPath(cuerpo); addPath(boca) }, l)
    val piel = cuerpoMeso(l, VENADO)
    // Lo hondo de la boca y, saliendo de ella, las hojas de amate dobladas (más cuanto más escribiste).
    drawOval(mezcla(piel, TINTA, 0.6f), Offset(cx - w * 0.4f, bocaY - h * 0.02f), Size(w * 0.8f, h * 0.05f))
    val cuantas = 1 + (v.lleno * 3.2f).toInt().coerceAtMost(3)
    val ah = minOf(w * 0.3f, 8f * u)
    for (j in 0 until cuantas) {
        val x = cx + (j - (cuantas - 1) / 2f) * ah * 0.62f
        val tope = h * (0.005f + 0.03f * ((j + 1) % 2)) + 0.4f * u
        val giro = (j - (cuantas - 1) / 2f) * 6f
        rotate(giro, Offset(x, bocaY + h * 0.03f)) {
            val hoja = poligonoMeso(x - ah / 2, bocaY + h * 0.03f, x - ah / 2 + 0.4f * u, tope + 0.8f * u, x + ah / 2, tope, x + ah / 2 - 0.3f * u, bocaY + h * 0.03f)
            drawPath(corridoMeso(hoja, 1f * u, 0.6f * u), a.s(0.3f))
            drawPath(hoja, a.c(if (j % 2 == 0) AMATE else mezcla(AMATE, ESTUCO, 0.55f), l.k, l.f))
            drawRect(a.c(CINABRIO, l.k, l.f).copy(alpha = 0.8f), Offset(x - ah / 2 + 1f * u, tope + 1.6f * u), Size(ah - 2f * u, 0.8f * u))
            drawRect(a.s(0.18f), Offset(x + ah * 0.05f, tope + 0.8f * u), Size(ah * 0.45f, bocaY - tope + h * 0.03f))
            drawRect(a.b(0.4f, l.k), Offset(x - ah / 2, tope), Size(ah, 0.7f * u))
        }
    }
    // La boca fruncida, en pliegues de luz y de sombra, con el canto de la piel vuelto.
    drawPath(boca, mezcla(piel, Color.Black, 0.06f))
    clipPath(boca) {
        for (j in 0 until 7) {
            val x = cx - w * 0.45f + w * 0.9f * j / 6f
            drawLine(if (j % 2 == 0) a.b(0.3f, l.k) else a.s(0.32f), Offset(x, bocaY - h * 0.01f), Offset(cx + (x - cx) * 0.6f, cuello), 1.8f * u)
        }
        drawRect(a.s(0.25f), Offset(0f, cuello - h * 0.04f), Size(w, h * 0.04f))
    }
    drawPath(boca, a.s(0.35f), style = Stroke(0.7f * u))
    // El cuerpo.
    drawPath(cuerpo, piel)
    clipPath(cuerpo) {
        // Las arrugas que bajan del cuello: surcos con su borde de luz.
        val arrugas = Path()
        listOf(-0.16f to 0.32f, -0.05f to 0.52f, 0.08f to 0.44f, 0.17f to 0.3f).forEach { (dx, l0) ->
            arrugas.moveTo(cx + w * dx * 0.6f, cuello + 1f * u); arrugas.quadraticTo(cx + w * dx * 1.6f, cuello + h * l0 * 0.5f, cx + w * dx * 2.6f, cuello + h * l0)
        }
        drawPath(arrugas, a.s(0.25f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(corridoMeso(arrugas, -1f * u), a.b(0.18f, l.k), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // La piel: unas manchas del curtido.
        for (j in 0 until 5) drawOval(a.s(0.06f), Offset(w * azar(v.semilla, j + 30), h * (0.3f + 0.6f * azar(v.semilla, j + 31))), Size(w * 0.3f, h * 0.06f))
        // El volumen: la panza redonda con su luz, su brillo y su sombra.
        cilindroMeso(Rect(0f, 0f, w, h), a, l.k, 1.25f, l.der, l.f)
        drawOval(a.b(0.28f, l.k), Offset(if (l.der) w * 0.6f else w * 0.14f, h * 0.48f), Size(w * 0.18f, h * 0.22f))
        drawRect(a.s(0.25f), Offset(0f, h * 0.88f), Size(w, h * 0.12f))
        puertaEnVolumenMeso(Rect(0f, 0f, w, h), l)
        // La costura de tiento por el costado de la luz.
        val cosX = w * 0.17f
        val costura = Path()
        var y = cuello + h * 0.08f; var n = 0
        while (y < h * 0.9f && n < 30) { costura.moveTo(cosX - 1.4f * u, y); costura.lineTo(cosX + 1.4f * u, y + 1.6f * u); y += 3.4f * u; n++ }
        drawLine(a.s(0.3f), Offset(cosX, cuello + h * 0.06f), Offset(cosX - w * 0.03f, h * 0.92f), 0.8f * u)
        drawPath(costura, a.c(mezcla(VENADO, ESTUCO, 0.5f), l.k, l.f), style = Stroke(0.9f * u))
    }
    drawPath(cuerpo, a.s(0.3f), style = Stroke(0.7f * u))
    // Los flecos de abajo, a la derecha.
    val flecos = Path()
    for (j in 0 until 4) { val x = w * (0.62f + 0.08f * j); flecos.moveTo(x, h * 0.95f); flecos.lineTo(x + 0.8f * u, h - 0.2f * u) }
    drawPath(flecos, mezcla(piel, Color.Black, 0.12f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
    // La correa del cuello, dos vueltas, con su nudo y sus puntas.
    val correa = mezcla(a.c(mezcla(VENADO, ZAPOTE_OSC, 0.5f), l.k, l.f), piel, 0.15f)
    listOf(0f, 2.8f * u).forEach { dy ->
        val c0 = Path().apply { moveTo(cx - w * 0.29f, cuello - 0.6f * u + dy); quadraticTo(cx, cuello + 1.8f * u + dy, cx + w * 0.29f, cuello - 0.6f * u + dy) }
        drawPath(corridoMeso(c0, 0f, 1f * u), a.s(0.35f), style = Stroke(1.8f * u))
        drawPath(c0, correa, style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawPath(corridoMeso(c0, 0f, -0.6f * u), a.b(0.35f, l.k), style = Stroke(0.5f * u))
    }
    val nudo = Offset(cx + w * 0.1f, cuello + 2.6f * u)
    drawCircle(correa, 2.2f * u, nudo)
    drawCircle(a.b(0.4f, l.k), 0.7f * u, nudo - Offset(0.7f * u, 0.7f * u))
    // La etiqueta de amate con el día, colgada del nudo.
    val ew = minOf(w * 0.86f, 30f * u)
    val et = Rect(cx - ew / 2, h * 0.31f, cx + ew / 2, h * 0.31f + minOf(h * 0.36f, 28f * u))
    drawLine(a.c(HENEQUEN, l.k, l.f), nudo, Offset(et.center.x + 1f * u, et.top + 1.4f * u), 0.9f * u)
    cartelaMeso(et, v, medidor, l, AMATE, filete = false, irregular = true)
    drawCircle(a.c(TINTA, 0.3f).copy(alpha = 0.6f), 0.9f * u, Offset(et.center.x + 1f * u, et.top + 1.6f * u))
    if (v.noche) {
        val c = Offset(nudo.x + 2f * u, et.bottom + 5.4f * u)
        drawLine(a.c(HENEQUEN, l.k, l.f), nudo, c, 0.7f * u)
        lunaConchaMeso(c, minOf(w * 0.15f, 4f * u), l, piel)
    }
    filoMeso(Rect(w * 0.1f, cuello, w * 0.9f, h), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.45f, l.f), Offset(if (l.der) w * 0.88f else w * 0.06f, h * 0.3f), Size(1.2f * u, h * 0.55f))
}

// ---------------------------------------------------------------- el rollo de manta

/**
 * El rollo de manta: un lienzo de algodón tejido y teñido del color del ánimo, enrollado y de pie: arriba, la espiral de sus
 * vueltas (más cuanto más escribiste) con la tela cruda de dentro; el cuerpo con sus franjas tejidas y una franja cruda ancha
 * donde va escrito el día, la orilla de fuera con sus flecos, dos amarres que siguen el cilindro (de uno cuelga, en los Sueños,
 * la media luna de concha) y su luz y su sombra de cilindro.
 */
private fun DrawScope.rolloMeso(v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val e = minOf(w * 0.36f, 9f * u)
    val silueta = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(w * 0.5f, e * 0.5f))) }
    sombraVolumenMeso(silueta, l)
    val tela = cuerpoMeso(l, ALGODON)
    val crudo = mezcla(ALGODON, AMATE, 0.35f)
    val cruda = a.c(crudo, l.k, l.f)
    val cuerpo = Path().apply {
        moveTo(0f, e / 2); lineTo(0f, h - e / 2)
        quadraticTo(w / 2, h + e * 0.5f, w, h - e / 2)
        lineTo(w, e / 2); close()
    }
    drawPath(cuerpo, tela)
    clipPath(cuerpo) {
        // Las franjas tejidas: dos finas oscuras arriba, la cruda ancha del día, una de dientes, y otra abajo.
        val oscura = a.c(mezcla(TINTA, l.tono ?: ALGODON, 0.25f), l.k, l.f)
        val y0 = h * 0.14f
        drawRect(oscura.copy(alpha = 0.7f), Offset(0f, y0 - 3.4f * u), Size(w, 1.2f * u))
        drawRect(cruda, Offset(0f, y0), Size(w, minOf(h * 0.3f, 27f * u)))
        val y1 = y0 + minOf(h * 0.3f, 27f * u)
        drawRect(oscura.copy(alpha = 0.7f), Offset(0f, y1), Size(w, 1.2f * u))
        val dientes = Path()
        var x = 0f; var n = 0
        val paso = 4f * u
        while (x < w && n < 20) { dientes.moveTo(x, y1 + 5.6f * u); dientes.lineTo(x + paso * 0.5f, y1 + 2.6f * u); dientes.lineTo(x + paso, y1 + 5.6f * u); x += paso; n++ }
        drawPath(dientes, a.c(if (l.tono == null) CINABRIO else ALGODON, l.k, l.f).copy(alpha = 0.85f), style = Stroke(0.9f * u))
        drawRect(cruda.copy(alpha = 0.8f), Offset(0f, h * 0.86f), Size(w, 1.6f * u))
        drawRect(oscura.copy(alpha = 0.6f), Offset(0f, h * 0.86f + 2.6f * u), Size(w, 1f * u))
        // La trama del tejido, tenue.
        val trama = Path()
        var y = e / 2; var m = 0
        while (y < h && m < 70) { trama.moveTo(0f, y); trama.quadraticTo(w / 2, y + e * 0.2f, w, y); y += 2.2f * u; m++ }
        drawPath(trama, a.s(0.07f), style = Stroke(0.5f * u))
        // La orilla de fuera: la vuelta de la tela, con su borde alzado y su sombra.
        val ox = w * (0.66f + 0.1f * azar(v.semilla, 21))
        drawLine(a.s(0.35f), Offset(ox + 1f * u, e / 2), Offset(ox + 1f * u, h), 1.4f * u)
        drawLine(a.b(0.35f, l.k), Offset(ox - 0.4f * u, e / 2), Offset(ox - 0.4f * u, h), 0.7f * u)
        // El cilindro.
        cilindroMeso(Rect(0f, 0f, w, h), a, l.k, 1.3f, l.der, l.f)
        puertaEnVolumenMeso(Rect(0f, 0f, w, h), l)
    }
    // Los flecos de la orilla de fuera, que salen por el borde de abajo.
    val ox = w * (0.66f + 0.1f * azar(v.semilla, 21))
    val flecos = Path()
    for (j in 0 until 4) { val x = ox + j * 1.4f * u; flecos.moveTo(x, h - e * 0.2f); flecos.lineTo(x + 0.6f * u, h + 1.4f * u) }
    drawPath(flecos, tela, style = Stroke(0.9f * u, cap = StrokeCap.Round))
    // Arriba: la boca del rollo con la espiral de sus vueltas.
    drawOval(a.c(mezcla(ALGODON, TINTA, 0.3f), l.k, l.f), Offset.Zero, Size(w, e))
    val vueltas = 2.5f + 3.5f * v.lleno
    val espiral = Path()
    val pasos = 64
    for (j in 0..pasos) {
        val t = j / pasos.toFloat()
        val ang = t * vueltas * 6.2832f
        val rr = 0.12f + 0.86f * t
        val px = w / 2 + rr * (w / 2 - 0.6f * u) * kotlin.math.cos(ang)
        val py = e / 2 + rr * (e / 2 - 0.4f * u) * kotlin.math.sin(ang)
        if (j == 0) espiral.moveTo(px, py) else espiral.lineTo(px, py)
    }
    val grueso = (e / 2) * 0.86f / vueltas * 0.6f
    drawPath(espiral, tela, style = Stroke(grueso.coerceAtLeast(0.6f * u), cap = StrokeCap.Round))
    drawPath(espiral, mezcla(cruda, tela, 0.3f), style = Stroke((grueso * 0.4f).coerceAtLeast(0.4f * u)))
    drawCircle(a.c(TINTA, l.k).copy(alpha = 0.6f), (e * 0.07f).coerceAtLeast(0.6f * u), Offset(w / 2, e / 2))
    drawOval(a.s(0.35f), Offset.Zero, Size(w, e), style = Stroke(0.8f * u))
    drawArc(a.b(0.55f, l.k), 150f, 120f, false, Offset(w * 0.02f, e * 0.02f), Size(w * 0.96f, e * 0.96f), style = Stroke(0.9f * u))
    // El día, escrito en la franja cruda (con la tinta del escriba; no lleva cartela).
    val y0 = h * 0.14f
    val alto = minOf(h * 0.3f, 27f * u)
    val dia = Rect(1.2f * u, y0 + 0.6f * u, w - 1.2f * u, y0 + 0.6f * u + minOf(alto * 0.68f, 18f * u))
    val tintaDia = a.tinta
    // De noche, la franja se alumbra para que el día se lea (4.5 a 1 o más).
    if (a.oscuro) drawRect(a.etiqueta(l.k, crudo), Offset(0f, y0), Size(w, alto))
    if (a.oscuro) cilindroMeso(Rect(0f, y0, w, y0 + alto), a, l.k, 0.5f, l.der, l.f)
    textoEn(medidor, v.dia, dia, MuebleEstanteMeso.LETRA, tintaDia, 16f, peso = FontWeight.Bold, minimo = 8f)
    textoEn(medidor, mesCortoMeso(v), Rect(1.6f * u, dia.bottom - 1.4f * u, w - 1.6f * u, y0 + alto - 1f * u), MuebleEstanteMeso.LETRA, a.rubrica, 8.5f, peso = FontWeight.SemiBold, minimo = 5f)
    // Los dos amarres, curvos como el cilindro.
    listOf(h * 0.6f, h * 0.78f).forEachIndexed { i, ya ->
        val am = Path().apply { moveTo(-0.5f * u, ya); quadraticTo(w / 2, ya + e * 0.32f, w + 0.5f * u, ya) }
        drawPath(corridoMeso(am, 0f, 1.2f * u), a.s(0.35f), style = Stroke(1.8f * u))
        drawPath(am, a.c(HENEQUEN, l.k, l.f), style = Stroke(1.6f * u))
        drawPath(corridoMeso(am, 0f, -0.6f * u), a.b(0.4f, l.k), style = Stroke(0.5f * u))
        if (i == 0) {
            val nudo = Offset(w * 0.3f, ya + e * 0.22f)
            drawCircle(a.c(HENEQUEN_OSC, l.k, l.f), 1.8f * u, nudo)
            if (v.noche) {
                val c = Offset(nudo.x, nudo.y + 6.4f * u)
                drawLine(a.c(HENEQUEN, l.k, l.f), nudo, c, 0.7f * u)
                lunaConchaMeso(c, minOf(w * 0.14f, 3.8f * u), l, tela)
            }
        }
    }
    filoMeso(Rect(0f, e / 2, w, h - e / 2), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.5f, l.f), Offset(if (l.der) w - 1.2f * u else 0f, e / 2), Size(1.2f * u, h - e))
}
