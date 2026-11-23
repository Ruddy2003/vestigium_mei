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
    val w = size.width; val h = size.height
    // La sombra se ve solo por el lado contrario a la luz (lo demás lo tapa el volumen): se pinta solo esa mitad.
    val derecha = Rect(w * 0.5f, -4f * u, w + 10f * u, h + 6f * u)
    val izquierda = Rect(-10f * u, -4f * u, w * 0.5f, h + 6f * u)
    if (!a.oscuro) clipRect(derecha.left, derecha.top, derecha.right, derecha.bottom) { translate(6f * u, 3f * u) { drawPath(forma, a.s(0.22f * (1f + 0.6f * l.f))) } }
    else {
        val lado = if (l.der) izquierda else derecha
        if (l.k > 0.12f) clipRect(lado.left, lado.top, lado.right, lado.bottom) { translate(if (l.der) -5f * u else 5f * u, -1.5f * u) { drawPath(forma, MesoTinta.NOCHE_SOMBRA.copy(alpha = (0.9f * l.k).coerceAtMost(0.45f))) } }
        else if (l.f > 0.2f) clipRect(derecha.left, derecha.top, derecha.right, derecha.bottom) { translate(5f * u, 3f * u) { drawPath(forma, MesoTinta.NOCHE_SOMBRA.copy(alpha = 0.32f * l.f)) } }
    }
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
    drawPath(forma, a.etiqueta(l.k, base, l.f))
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

// ---------------------------------------------------------------- el códice plegado

/**
 * El códice plegado en biombo, de pie y visto por el canto de los pliegues: a los lados, las dos tapas de madera estucadas y
 * pintadas del color del ánimo (la de delante a la luz, la de atrás en sombra), y entre ellas el fuelle de las hojas de amate,
 * cara de luz y cara de sombra, con el filete rojo de cada página arriba y abajo y algún pliegue salido (más pliegues cuanto
 * más escribiste); una faja ancha del color del ánimo que lo ciñe, con su nudo y su cuenta de jade (en los Sueños, la media
 * luna de concha); arriba, la etiqueta de estuco con el día y el mes, atada con un cordel.
 */
private fun DrawScope.codiceMeso(v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val silueta = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, w, h), CornerRadius(1.4f * u))) }
    sombraVolumenMeso(silueta, l)
    val pintura = cuerpoMeso(l, ESTUCO)
    val tapaI = minOf(maxOf(w * 0.17f, 3.6f * u), 6.4f * u)
    val tapaD = tapaI * 0.7f
    val fuelle = Rect(tapaI, h * 0.012f, w - tapaD, h * 0.988f)
    // El fuelle de amate: caras alternas de luz y de sombra, con su filete rojo arriba y abajo.
    val caras = 2 * (3 + (v.lleno * 5f).toInt())
    val paso = fuelle.width / caras
    val luz = Path(); val sombra = Path(); val filetes = Path(); val pliegues = Path()
    val salido = (azar(v.semilla, 31) * caras).toInt()
    for (i in 0 until caras) {
        val x0 = fuelle.left + paso * i
        val alza = (if (i % 2 == 0) 0f else 1.2f * u) + (if (i == salido) -2.6f * u else 0f)
        val cara = Rect(x0, fuelle.top + alza, x0 + paso + 0.4f, fuelle.bottom - (if (i % 2 == 0) 0f else 0.8f * u))
        (if (i % 2 == 0) luz else sombra).addRect(cara)
        filetes.moveTo(cara.left, cara.top + 2.2f * u); filetes.lineTo(cara.right, cara.top + 2.2f * u)
        filetes.moveTo(cara.left, cara.bottom - 2.2f * u); filetes.lineTo(cara.right, cara.bottom - 2.2f * u)
        pliegues.moveTo(x0, cara.top); pliegues.lineTo(x0, cara.bottom)
    }
    val amate = mezcla(AMATE, ESTUCO, 0.55f)
    drawPath(luz, a.c(amate, l.k, l.f))
    drawPath(sombra, a.c(mezcla(amate, AMATE_OSC, 0.45f), l.k, l.f))
    drawPath(filetes, a.c(CINABRIO, l.k, l.f).copy(alpha = 0.75f), style = Stroke(0.7f * u))
    drawPath(pliegues, a.s(0.35f), style = Stroke(0.6f * u))
    clipRect(fuelle.left, fuelle.top - 4f * u, fuelle.right, fuelle.bottom) {
        drawRect(a.s(0.14f), Offset(fuelle.left, fuelle.top), Size(fuelle.width, fuelle.height * 0.06f))
        if (a.oscuro && l.k > 0.06f) drawPath(luz, a.b(0.25f, l.k))
    }
    // Las dos tapas, de canto y un poco vueltas: la de delante a la luz, la de atrás en sombra; su grueso de madera.
    val tI = Path().apply { addRoundRect(RoundRect(Rect(0f, 0f, tapaI, h), CornerRadius(1.4f * u))) }
    val tD = Path().apply { addRoundRect(RoundRect(Rect(w - tapaD, h * 0.006f, w, h), CornerRadius(1.2f * u))) }
    drawPath(tI, pintura)
    drawPath(tD, mezcla(pintura, Color.Black, 0.22f))
    clipPath(tI) {
        drawRect(a.b(if (l.der) 0.06f else 0.24f, l.k), Offset(0f, 0f), Size(tapaI * 0.45f, h))
        drawRect(a.s(if (l.der) 0.2f else 0.1f), Offset(tapaI * 0.7f, 0f), Size(tapaI * 0.3f, h))
        drawRect(a.c(mezcla(ZAPOTE, ESTUCO, 0.2f), l.k, l.f), Offset(tapaI - 1.2f * u, 0f), Size(1.2f * u, h))
        puertaEnVolumenMeso(Rect(0f, 0f, tapaI, h), l)
        // El gasto del estuco en las esquinas.
        drawPath(poligonoMeso(0f, 0f, 3f * u, 0f, 0f, 4f * u), a.c(ESTUCO, l.k, l.f))
        drawPath(poligonoMeso(0f, h, 3f * u, h, 0f, h - 4f * u), a.c(ZAPOTE, l.k, l.f))
    }
    clipPath(tD) { drawRect(a.s(0.2f), Offset(w - tapaD, 0f), Size(tapaD * 0.5f, h)) }
    drawRect(a.b(0.5f, l.k), Offset(1.4f * u, 0f), Size(tapaI - 2f * u, 0.9f * u))
    // La faja ancha del color del ánimo que lo ciñe, con su doblez y su nudo; de él cuelga la cuenta.
    val fy = h * 0.56f; val fa = minOf(h * 0.12f, 11f * u)
    val faja = Path().apply { moveTo(-0.5f, fy); quadraticTo(w * 0.5f, fy + fa * 0.18f, w + 0.5f, fy); lineTo(w + 0.5f, fy + fa); quadraticTo(w * 0.5f, fy + fa * 1.18f, -0.5f, fy + fa); close() }
    drawPath(corridoMeso(faja, 0f, 1.6f * u), a.s(0.35f))
    val tela = mezcla(pintura, Color.Black, 0.12f)
    drawPath(faja, tela)
    clipPath(faja) {
        drawRect(a.b(0.3f, l.k), Offset(0f, fy), Size(w, fa * 0.26f))
        drawRect(a.s(0.25f), Offset(0f, fy + fa * 0.7f), Size(w, fa * 0.4f))
        drawLine(a.c(if (luminanciaMeso(tela) > 0.3f) TINTA else ESTUCO, l.k, l.f).copy(alpha = 0.5f), Offset(0f, fy + fa * 0.5f), Offset(w, fy + fa * 0.55f), 0.6f * u)
    }
    val nudo = Offset(w * 0.36f, fy + fa * 0.55f)
    drawOval(mezcla(tela, Color.Black, 0.18f), Offset(nudo.x - 2.6f * u, nudo.y - 2.2f * u), Size(5.2f * u, 4.4f * u))
    drawArc(a.b(0.45f, l.k), 190f, 120f, false, Offset(nudo.x - 2.6f * u, nudo.y - 2.2f * u), Size(5.2f * u, 4.4f * u), style = Stroke(0.7f * u))
    val cuelga = Offset(nudo.x + 1.2f * u, nudo.y + minOf(h * 0.12f, 11f * u))
    drawLine(a.c(HENEQUEN, l.k, l.f), nudo, cuelga, 0.9f * u)
    if (v.noche) lunaConchaMeso(cuelga + Offset(0f, 2.6f * u), minOf(w * 0.15f, 4f * u), l, a.c(AMATE_OSC, l.k))
    else {
        val rc = minOf(w * 0.11f, 2.8f * u)
        drawCircle(a.s(0.35f), rc, cuelga + Offset(0.9f * u, 1.6f * u))
        drawCircle(a.c(JADE, l.k, l.f), rc, cuelga + Offset(0f, rc * 0.7f))
        drawCircle(a.c(JADE_LUZ, l.k, l.f).copy(alpha = 0.7f), rc * 0.36f, cuelga + Offset(-rc * 0.3f, rc * 0.4f))
    }
    // La etiqueta con el día, atada arriba con un cordel que da la vuelta.
    val et = Rect(1.4f * u, h * 0.065f, w - 1.4f * u, h * 0.065f + minOf(h * 0.3f, 27f * u))
    drawLine(a.c(HENEQUEN, l.k, l.f), Offset(0f, et.top + 2f * u), Offset(w, et.top + 1.4f * u), 0.9f * u)
    cartelaMeso(et, v, medidor, l, mezcla(ESTUCO, AMATE, 0.35f), filete = true, irregular = false)
    // La sombra de la vecina de la luz y el filo de la lumbre.
    sombraVecinaMeso(silueta, l)
    filoMeso(Rect(0f, 0f, w, h), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.5f, l.f), Offset(if (l.der) w - 1.2f * u else 0f, 0f), Size(1.2f * u, h))
}

/**
 * La sombra que echa la vecina del lado de la luz (o el poste) sobre el volumen: una franja honda en su costado de ese lado,
 * dentro de su [silueta]. De día la luz viene de la izquierda; de noche, de la lumbre.
 */
private fun DrawScope.sombraVecinaMeso(silueta: Path, l: LuzVolumenMeso) {
    val w = size.width; val h = size.height
    if (l.a.oscuro && l.k < 0.1f) return
    val fuerza = if (l.a.oscuro) 0.3f * l.k.coerceAtMost(1f) else 0.22f
    clipPath(silueta) {
        val x = if (l.der) w * 0.84f else 0f
        drawRect(l.a.s(fuerza), Offset(x, 0f), Size(w * 0.16f, h))
        drawRect(l.a.s(fuerza * 0.6f), Offset(if (l.der) w * 0.74f else w * 0.16f, 0f), Size(w * 0.1f, h))
    }
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
        for (j in 0 until 3) drawOval(a.s(0.07f), Offset(w * azar(v.semilla, j + 30), h * (0.3f + 0.6f * azar(v.semilla, j + 31))), Size(w * 0.3f, h * 0.06f))
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
    sombraVecinaMeso(cuerpo, l)
    filoMeso(Rect(w * 0.1f, cuello, w * 0.9f, h), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.45f, l.f), Offset(if (l.der) w * 0.88f else w * 0.06f, h * 0.3f), Size(1.2f * u, h * 0.55f))
}

// ---------------------------------------------------------------- el rollo de manta

/**
 * El rollo de manta: un lienzo de algodón tejido y teñido del color del ánimo, enrollado y de pie. Arriba, la boca con la
 * espiral de sus vueltas (más cuanto más escribiste) y la tela cruda de dentro; la orilla de fuera, que se suelta y deja una
 * punta doblada con sus hilos; el cuerpo blando, que se hace cintura donde aprietan los dos amarres y se hincha entre ellos,
 * con sus pliegues, sus franjas tejidas y la franja cruda donde va escrito el día; los flecos de abajo; la luz de un lado y la
 * sombra del otro.
 */
private fun DrawScope.rolloMeso(v: Volumen, medidor: TextMeasurer, l: LuzVolumenMeso) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val e = minOf(w * 0.42f, 11f * u)
    val tela = cuerpoMeso(l, ALGODON)
    val crudo = mezcla(ALGODON, AMATE, 0.35f)
    val cruda = a.c(crudo, l.k, l.f)
    // Las cinturas de los amarres y la panza entre ellas.
    val y1 = h * 0.6f; val y2 = h * 0.8f
    val cin = w * 0.055f
    fun borde(izq: Boolean): List<Offset> {
        val s = if (izq) 1f else -1f
        val x = if (izq) 0f else w
        return listOf(Offset(x, e / 2), Offset(x + s * w * 0.01f, h * 0.35f), Offset(x + s * cin, y1), Offset(x - s * w * 0.008f, (y1 + y2) / 2),
            Offset(x + s * cin, y2), Offset(x + s * w * 0.004f, h - e * 0.5f))
    }
    val izq = borde(true); val der = borde(false)
    val cuerpo = Path().apply {
        moveTo(izq[0].x, izq[0].y)
        for (i in 1 until izq.size) { val p = izq[i - 1]; val q = izq[i]; quadraticTo(p.x, (p.y + q.y) / 2, q.x, q.y) }
        quadraticTo(w / 2, h + e * 0.5f, der.last().x, der.last().y)
        for (i in der.size - 2 downTo 0) { val p = der[i + 1]; val q = der[i]; quadraticTo(p.x, (p.y + q.y) / 2, q.x, q.y) }
        close()
    }
    sombraVolumenMeso(cuerpo, l)
    drawPath(cuerpo, tela)
    val ox = w * (0.66f + 0.1f * azar(v.semilla, 21))
    clipPath(cuerpo) {
        // Las franjas tejidas: dos finas oscuras arriba, la cruda ancha del día, una de dientes y otra abajo.
        val oscura = a.c(mezcla(TINTA, l.tono ?: ALGODON, 0.25f), l.k, l.f)
        val y0 = h * 0.14f
        val alto = minOf(h * 0.3f, 27f * u)
        drawRect(oscura.copy(alpha = 0.7f), Offset(0f, y0 - 3.4f * u), Size(w, 1.2f * u))
        drawRect(if (a.oscuro) a.etiqueta(l.k, crudo, l.f) else cruda, Offset(0f, y0), Size(w, alto))
        val y3 = y0 + alto
        drawRect(oscura.copy(alpha = 0.7f), Offset(0f, y3), Size(w, 1.2f * u))
        val dientes = Path()
        var x = 0f; var n = 0
        val pasoD = 4f * u
        while (x < w && n < 20) { dientes.moveTo(x, y3 + 5.6f * u); dientes.lineTo(x + pasoD * 0.5f, y3 + 2.6f * u); dientes.lineTo(x + pasoD, y3 + 5.6f * u); x += pasoD; n++ }
        drawPath(dientes, a.c(if (l.tono == null) CINABRIO else ALGODON, l.k, l.f).copy(alpha = 0.85f), style = Stroke(0.9f * u))
        drawRect(cruda.copy(alpha = 0.8f), Offset(0f, h * 0.88f), Size(w, 1.6f * u))
        drawRect(oscura.copy(alpha = 0.6f), Offset(0f, h * 0.88f + 2.6f * u), Size(w, 1f * u))
        // Los pliegues blandos que bajan en diagonal.
        val pl = Path().apply {
            moveTo(w * 0.22f, y3 + 6f * u); quadraticTo(w * 0.3f, (y3 + y1) / 2, w * 0.18f, y1 - 1f * u)
            moveTo(w * 0.5f, y1 + 2f * u); quadraticTo(w * 0.58f, (y1 + y2) / 2, w * 0.46f, y2 - 1f * u)
        }
        drawPath(pl, a.s(0.25f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(corridoMeso(pl, -1f * u), a.b(0.2f, l.k), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // La orilla de fuera, que se suelta: su borde alzado y su sombra.
        drawLine(a.s(0.38f), Offset(ox + 1.2f * u, e / 2), Offset(ox + 1.6f * u, h), 1.6f * u)
        drawLine(a.b(0.35f, l.k), Offset(ox - 0.4f * u, e / 2), Offset(ox, h), 0.7f * u)
        // El cilindro: la luz de un lado, la sombra del otro, y la panza entre los amarres un poco más clara.
        cilindroMeso(Rect(0f, 0f, w, h), a, l.k, 1.35f, l.der, l.f)
        drawOval(a.b(0.12f, l.k), Offset(w * 0.1f, y1 + 2f * u), Size(w * 0.5f, y2 - y1 - 4f * u))
        puertaEnVolumenMeso(Rect(0f, 0f, w, h), l)
    }
    drawPath(cuerpo, a.s(0.3f), style = Stroke(0.7f * u))
    // Los flecos de abajo, que salen bajo la orilla.
    val flecos = Path()
    for (j in 0 until 5) { val x = ox - 2f * u + j * 1.4f * u; flecos.moveTo(x, h - e * 0.25f); flecos.lineTo(x + 0.5f * u * (j - 2), h + 1.6f * u) }
    drawPath(flecos, tela, style = Stroke(0.9f * u, cap = StrokeCap.Round))
    // Arriba: la boca del rollo con la espiral de sus vueltas.
    drawOval(a.c(mezcla(ALGODON, TINTA, 0.3f), l.k, l.f), Offset.Zero, Size(w, e))
    val vueltas = 2.5f + 3.5f * v.lleno
    val espiral = Path()
    val pasos = 54
    for (j in 0..pasos) {
        val t = j / pasos.toFloat()
        val ang = t * vueltas * 6.2832f
        val rr = 0.1f + 0.88f * t
        val px = w / 2 + rr * (w / 2 - 0.6f * u) * kotlin.math.cos(ang)
        val py = e / 2 + rr * (e / 2 - 0.4f * u) * kotlin.math.sin(ang)
        if (j == 0) espiral.moveTo(px, py) else espiral.lineTo(px, py)
    }
    val grueso = (e / 2) * 0.88f / vueltas * 0.62f
    drawPath(espiral, tela, style = Stroke(grueso.coerceAtLeast(0.7f * u), cap = StrokeCap.Round))
    drawPath(espiral, mezcla(cruda, tela, 0.25f), style = Stroke((grueso * 0.42f).coerceAtLeast(0.45f * u)))
    drawCircle(a.c(TINTA, l.k).copy(alpha = 0.6f), (e * 0.07f).coerceAtLeast(0.6f * u), Offset(w / 2, e / 2))
    drawOval(a.s(0.35f), Offset.Zero, Size(w, e), style = Stroke(0.8f * u))
    drawArc(a.b(0.55f, l.k), 150f, 120f, false, Offset(w * 0.02f, e * 0.02f), Size(w * 0.96f, e * 0.96f), style = Stroke(0.9f * u))
    // La esquina de la orilla, que se suelta y se dobla hacia fuera bajo la boca: el revés crudo, con sus hilos en la punta.
    val py0 = e * 0.85f; val py1 = py0 + minOf(h * 0.14f, 12f * u)
    val punta = poligonoMeso(ox, py0, w + w * 0.04f, py0 + (py1 - py0) * 0.25f, ox + w * 0.04f, py1)
    drawPath(corridoMeso(punta, 1f * u, 1.2f * u), a.s(0.32f))
    drawPath(punta, mezcla(tela, cruda, 0.45f))
    drawPath(bordeMeso(punta, -w * 0.04f, 0f), a.s(0.2f))
    val hilos = Path()
    for (j in 0 until 3) { val p = Offset(w + w * 0.03f, py0 + (py1 - py0) * (0.2f + 0.06f * j)); hilos.moveTo(p.x, p.y); hilos.lineTo(p.x + w * 0.05f, p.y + (py1 - py0) * 0.2f) }
    drawPath(hilos, mezcla(tela, cruda, 0.5f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
    // El día, escrito en la franja cruda (con la tinta del escriba; no lleva cartela).
    val y0 = h * 0.14f
    val alto = minOf(h * 0.3f, 27f * u)
    val dia = Rect(1.2f * u, y0 + 0.6f * u, w - 1.2f * u, y0 + 0.6f * u + minOf(alto * 0.68f, 18f * u))
    textoEn(medidor, v.dia, dia, MuebleEstanteMeso.LETRA, a.tinta, 16f, peso = FontWeight.Bold, minimo = 8f)
    textoEn(medidor, mesCortoMeso(v), Rect(1.6f * u, dia.bottom - 1.4f * u, w - 1.6f * u, y0 + alto - 1f * u), MuebleEstanteMeso.LETRA, a.rubrica, 8.5f, peso = FontWeight.SemiBold, minimo = 5f)
    // Los dos amarres, que aprietan y hacen cintura.
    listOf(y1, y2).forEachIndexed { i, ya ->
        val am = Path().apply { moveTo(cin - 0.5f * u, ya); quadraticTo(w / 2, ya + e * 0.3f, w - cin + 0.5f * u, ya) }
        drawPath(corridoMeso(am, 0f, 1.2f * u), a.s(0.38f), style = Stroke(2f * u))
        drawPath(am, a.c(HENEQUEN, l.k, l.f), style = Stroke(1.8f * u))
        drawPath(corridoMeso(am, 0f, -0.6f * u), a.b(0.45f, l.k), style = Stroke(0.5f * u))
        if (i == 0) {
            val nudo = Offset(w * 0.3f, ya + e * 0.22f)
            drawCircle(a.c(HENEQUEN_OSC, l.k, l.f), 2f * u, nudo)
            drawLine(a.c(HENEQUEN, l.k, l.f), nudo, nudo + Offset(-1.4f * u, 4.4f * u), 1f * u, StrokeCap.Round)
            if (v.noche) {
                val c = Offset(nudo.x + 1.4f * u, nudo.y + 6.4f * u)
                drawLine(a.c(HENEQUEN, l.k, l.f), nudo, c, 0.7f * u)
                lunaConchaMeso(c, minOf(w * 0.14f, 3.8f * u), l, tela)
            }
        }
    }
    sombraVecinaMeso(cuerpo, l)
    filoMeso(Rect(0f, e / 2, w, h - e / 2), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.5f, l.f), Offset(if (l.der) w - 1.2f * u else 0f, e / 2), Size(1.2f * u, h - e))
}
