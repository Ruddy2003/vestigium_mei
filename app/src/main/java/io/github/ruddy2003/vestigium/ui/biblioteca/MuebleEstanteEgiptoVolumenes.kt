package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import kotlin.math.roundToInt
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.YESO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// Los volúmenes del almacén de la Tumba (MuebleEstanteEgipto): tres cosas del escriba, según la semilla, cada una modelada
// como los objetos del Ideario (plano de luz, plano de sombra, brillo, detalles finos, su sombra en el muro y en la tabla).

/** La luna de los Sueños: la plata pálida del creciente. */
private val LUNA_EGIPTO = Color(0xFFEDE8D0)

/** Lo que un volumen sabe de su luz: el aire, la lámpara ([k]), la puerta ([f]), de qué lado le llega ([der]) y su ánimo de día. */
private class LuzVolumenEgipto(val a: AireEgipto, val k: Float, val f: Float, val der: Boolean, val tono: Color?)

internal fun DrawScope.volumenEgipto(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireEgipto(oscuro, v.noche)
    val lv = luzVolumenEgipto(v, oscuro)
    // El ánimo, con los colores del mundo (ocre rojo, ocre amarillo, lino crudo, fayenza, azul egipcio), de día también de noche.
    val l = LuzVolumenEgipto(a, lv.k, lv.f, lv.der, v.tono?.let { animoMundoEgipto(tonoDeDiaEgipto(it, oscuro)) })
    when (MuebleEstanteEgipto.clase(v.semilla)) {
        0 -> rolloEgipto(v, medidor, l)
        1 -> tablillaEgipto(v, medidor, l)
        else -> fardoEgipto(v, medidor, l)
    }
}

/** El color del cuerpo: el del ánimo (de día) si lo hay, con la luz que le llega; si no, el del material crudo. */
private fun cuerpoEgipto(l: LuzVolumenEgipto, crudo: Color): Color = l.tono?.let { l.a.animo(it, l.k, l.f) } ?: l.a.c(crudo, l.k, l.f)

/** El mes como cabe en una tira angosta: sin la estación entre paréntesis («Paofi (II Ajet)» queda en «Paofi»). */
private fun mesEgipto(v: Volumen, angosto: Boolean): String = if (angosto) v.mes.substringBefore(" (").trim().ifEmpty { v.mes } else v.mes

/**
 * La sombra que el volumen echa en el muro del fondo (fuera de su lienzo; el vecino de la derecha la tapa donde se encima) y
 * en la tabla: de día, corrida a la derecha y abajo, más honda dentro del sol; de noche, hacia fuera de la lámpara.
 */
private fun DrawScope.sombraVolumenEgipto(forma: Path?, l: LuzVolumenEgipto, caja: Rect = Rect.Zero, radio: CornerRadius = CornerRadius.Zero) {
    val u = density
    val a = l.a
    fun sombra(dx: Float, dy: Float, c: Color) =
        if (forma != null) translate(dx, dy) { drawPath(forma, c) } else drawRoundRect(c, caja.topLeft + Offset(dx, dy), caja.size, radio)
    if (!a.oscuro) sombra(6f * u, 3f * u, a.s(0.2f * (1f + 0.7f * l.f)))
    else {
        if (l.k > 0.12f) sombra(if (l.der) -5f * u else 5f * u, -1.5f * u, EgiptoTinta.NOCHE_SOMBRA.copy(alpha = (0.9f * l.k).coerceAtMost(0.45f)))
        else if (l.f > 0.2f) sombra(5f * u, 3f * u, EgiptoTinta.NOCHE_SOMBRA.copy(alpha = 0.32f * l.f))
    }
    val w = size.width; val h = size.height
    drawOval(a.s(0.42f), Offset(-1f * u, h - 2.6f * u), Size(w + 5f * u, 4.4f * u))
}

/** La luz de la puerta sobre lo que toca: el sol (o el ocaso, o la luna) que entra por la izquierda, con su filo de ese lado. */
private fun DrawScope.puertaEnVolumenEgipto(r: Rect, l: LuzVolumenEgipto) {
    if (l.f < 0.02f) return
    val m = if (l.a.oscuro) 0.7f else 1f
    drawRect(l.a.puerta(0.08f * l.f * m), r.topLeft, r.size)
    drawRect(l.a.puerta(0.3f * l.f * m), r.topLeft, Size(r.width * 0.18f, r.height))
}

/** Una tira de papiro pegada con el texto dentro: el día (horizontal) o el mes (en rojo, a lo largo). */
private fun DrawScope.tiraEgipto(r: Rect, texto: String, medidor: TextMeasurer, l: LuzVolumenEgipto, mes: Boolean) {
    val u = density
    if (r.width < 3f || r.height < 3f) return
    val a = l.a
    drawRect(a.s(0.38f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
    drawRect(a.etiqueta(l.k, if (mes) PAPIRO else mezcla(PAPIRO, YESO, 0.6f)), r.topLeft, r.size)
    drawRect(a.s(0.3f), r.topLeft, r.size, style = Stroke(0.6f * u))
    val fibras = Path()
    var y = r.top + 1.6f * u
    while (y < r.bottom - 0.5f * u) { fibras.moveTo(r.left, y); fibras.lineTo(r.right, y + 0.3f * u); y += 3.4f * u }
    drawPath(fibras, a.c(PAPIRO_OSC, maxOf(l.k, 0.5f)).copy(alpha = 0.25f), style = Stroke(0.5f * u))
    drawRect(a.s(0.2f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
    drawRect(a.b(0.5f, l.k), r.topLeft, Size(r.width, 0.7f * u))
    if (mes) textoEn(medidor, texto, r.deflate(0.8f * u), MuebleEstanteEgipto.LETRA, a.rubrica, 8.5f, vertical = r.height > r.width, peso = FontWeight.SemiBold, minimo = 4.5f)
    else textoEn(medidor, texto, Rect(r.left + 0.5f * u, r.top + 0.3f * u, r.right - 0.5f * u, r.bottom), MuebleEstanteEgipto.LETRA, a.tinta, 13.5f, peso = FontWeight.Bold, minimo = 6f)
}

/** El sello de barro con su impronta y el cordel; en los Sueños, la luna de plata en su lugar. */
private fun DrawScope.selloEgipto(c: Offset, rs: Float, luna: Boolean, l: LuzVolumenEgipto) {
    val u = density
    val a = l.a
    drawOval(a.s(0.4f), Offset(c.x - rs + 1f * u, c.y - rs * 0.8f + 1.2f * u), Size(rs * 2f, rs * 1.7f))
    if (luna) {
        val plata = if (a.oscuro) mezcla(Color(0xFFB8BCD0), LUNA_EGIPTO, (0.4f + l.k).coerceAtMost(1f)) else LUNA_EGIPTO
        drawCircle(a.c(EgiptoTinta.AZUL, maxOf(l.k, 0.4f)), rs * 1.04f, c)
        drawCircle(plata, rs * 0.86f, c)
        drawCircle(a.c(EgiptoTinta.AZUL, maxOf(l.k, 0.4f)), rs * 0.7f, c + Offset(rs * 0.36f, -rs * 0.2f))
        return
    }
    val barro = a.c(BARRO, maxOf(l.k, 0.25f), l.f)
    drawOval(barro, Offset(c.x - rs, c.y - rs * 0.85f), Size(rs * 2f, rs * 1.7f))
    drawArc(a.b(0.5f, l.k), 170f, 110f, false, Offset(c.x - rs * 0.8f, c.y - rs * 0.7f), Size(rs * 1.6f, rs * 1.4f), style = Stroke(0.8f * u))
    drawArc(a.s(0.3f), -10f, 110f, false, Offset(c.x - rs * 0.9f, c.y - rs * 0.75f), Size(rs * 1.8f, rs * 1.5f), style = Stroke(0.9f * u))
    drawOval(mezcla(barro, EgiptoTinta.NOCHE, 0.35f), Offset(c.x - rs * 0.48f, c.y - rs * 0.38f), Size(rs * 0.96f, rs * 0.76f), style = Stroke(0.8f * u))
    drawLine(a.c(FIBRA, l.k, l.f), Offset(c.x - rs * 0.3f, c.y + rs * 0.7f), Offset(c.x - rs * 0.6f, c.y + rs * 2.1f), 0.9f * u)
    drawLine(a.c(FIBRA, l.k, l.f), Offset(c.x + rs * 0.2f, c.y + rs * 0.7f), Offset(c.x + rs * 0.55f, c.y + rs * 1.9f), 0.9f * u)
}

/**
 * El rollo de papiro de pie, metido en su funda de lino teñido (el color del ánimo): arriba asoma el papiro, cilindro con su
 * luz y su sombra, con las vueltas del rollo a la vista (más cuanto más escribiste) y el borde de la hoja de fuera; la funda,
 * con su dobladillo, su trama y dos pliegues; la tira de papiro con el día; el cordel en dos vueltas que siguen el cilindro,
 * con su sello de barro (en los Sueños, la luna); y abajo, la tira con el mes en rojo, como se escribían las fechas.
 */
private fun DrawScope.rolloEgipto(v: Volumen, medidor: TextMeasurer, l: LuzVolumenEgipto) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val e = minOf(w * 0.34f, 8f * u)
    sombraVolumenEgipto(null, l, Rect(0f, 0f, w, h), CornerRadius(w * 0.5f, e * 0.5f))
    val papiro = a.c(mezcla(PAPIRO, PAPIRO_OSC, 0.12f + 0.2f * azar(v.semilla, 7)), l.k, l.f)
    drawOval(papiro, Offset(0f, h - e), Size(w, e))
    // La funda de lino, con su dobladillo de arriba un poco desigual.
    val ini = h * (0.17f + 0.03f * azar(v.semilla, 8))
    val fin = h - e * 0.35f
    // El papiro de la cabeza (lo de abajo lo tapa la funda).
    drawRect(papiro, Offset(0f, e / 2), Size(w, ini + 3f * u - e / 2))
    val funda = Path().apply {
        moveTo(0f, ini + 2f * u)
        quadraticTo(w * 0.3f, ini - 1.6f * u, w * 0.55f, ini + 1f * u)
        quadraticTo(w * 0.8f, ini + 2.6f * u, w, ini)
        lineTo(w, fin); quadraticTo(w / 2, fin + e * 0.55f, 0f, fin); close()
    }
    val tela = cuerpoEgipto(l, LINO)
    drawPath(funda, tela)
    clipRect(0f, ini - 1f * u, w, h) {
        tramaEgipto(ini, a)
        // Dos pliegues: el surco en sombra con su borde de luz.
        listOf(0.3f to 0.42f, 0.7f to 0.64f).forEachIndexed { j, (x0, x1) ->
            val pl = Path().apply { moveTo(w * x0, ini + 3f * u); quadraticTo(w * (x0 + 0.04f), h * (0.45f + 0.1f * j), w * x1, h * (0.7f + 0.1f * j)) }
            drawPath(pl, a.s(0.22f), style = Stroke(1.2f * u))
            drawPath(corridoEgipto(pl, -1.1f * u), a.b(0.22f, l.k), style = Stroke(0.7f * u))
        }
        cilindroEgipto(Rect(0f, 0f, w, h), a, l.k, 1.3f, l.der, l.f)
        puertaEnVolumenEgipto(Rect(0f, 0f, w, h), l)
    }
    // El dobladillo: la vuelta de la tela, más clara, con su sombra debajo.
    val dobladillo = Path().apply { moveTo(0f, ini + 2f * u); quadraticTo(w * 0.3f, ini - 1.6f * u, w * 0.55f, ini + 1f * u); quadraticTo(w * 0.8f, ini + 2.6f * u, w, ini) }
    drawPath(corridoEgipto(dobladillo, 0f, 2.6f * u), a.s(0.25f), style = Stroke(1.4f * u))
    drawPath(corridoEgipto(dobladillo, 0f, 1f * u), mezcla(tela, Color.White, if (a.oscuro) 0.05f else 0.18f), style = Stroke(2.4f * u))
    // El papiro de arriba: el rollo con su luz, las fibras a lo alto y el borde de la hoja de fuera, que da la vuelta.
    clipRect(0f, e / 2, w, ini + 2f * u) {
        val fibras = Path()
        for (j in 0 until 6) { val x = w * (0.1f + 0.16f * j + 0.04f * azar(v.semilla, j + 70)); fibras.moveTo(x, e / 2); fibras.lineTo(x + 0.4f * u, ini + 2f * u) }
        drawPath(fibras, a.c(PAPIRO_OSC, l.k, l.f).copy(alpha = 0.45f), style = Stroke(0.6f * u))
        cilindroEgipto(Rect(0f, 0f, w, ini + 2f * u), a, l.k, 1.3f, l.der, l.f)
        val borde = w * (0.62f + 0.1f * azar(v.semilla, 71))
        drawLine(a.s(0.45f), Offset(borde, e / 2), Offset(borde - 0.6f * u, ini + 2f * u), 1f * u)
        drawLine(a.b(0.4f, l.k), Offset(borde - 1f * u, e / 2), Offset(borde - 1.6f * u, ini + 2f * u), 0.6f * u)
        puertaEnVolumenEgipto(Rect(0f, 0f, w, ini + 2f * u), l)
    }
    // El canto del rollo: la boca honda y, encima, la espiral de las vueltas del papiro (más cuanto más escribiste).
    drawOval(a.c(mezcla(PAPIRO_OSC, EgiptoTinta.TINTA, 0.35f), l.k, l.f), Offset.Zero, Size(w, e))
    val vueltas = 3f + 3f * v.lleno
    val espiral = Path()
    val pasos = 64
    for (j in 0..pasos) {
        val t = j / pasos.toFloat()
        val ang = t * vueltas * 6.2832f
        val rr = 0.14f + 0.84f * t
        val x = w / 2 + rr * (w / 2 - 0.6f * u) * kotlin.math.cos(ang)
        val y = e / 2 + rr * (e / 2 - 0.4f * u) * kotlin.math.sin(ang)
        if (j == 0) espiral.moveTo(x, y) else espiral.lineTo(x, y)
    }
    val grueso = (e / 2) * 0.84f / vueltas * 0.62f
    drawPath(espiral, papiro, style = Stroke(grueso.coerceAtLeast(0.6f * u), cap = StrokeCap.Round))
    drawPath(espiral, a.b(0.35f, l.k), style = Stroke((grueso * 0.35f).coerceAtLeast(0.4f * u)))
    drawCircle(a.c(EgiptoTinta.TINTA, l.k).copy(alpha = 0.6f), (e * 0.07f).coerceAtLeast(0.6f * u), Offset(w / 2, e / 2))
    // El labio de la vuelta de fuera, con su luz del lado de la luz.
    drawOval(a.s(0.35f), Offset.Zero, Size(w, e), style = Stroke(0.8f * u))
    drawArc(a.b(0.6f, l.k), 150f, 120f, false, Offset(w * 0.02f, e * 0.02f), Size(w * 0.96f, e * 0.96f), style = Stroke(0.9f * u))
    drawRect(a.s(0.16f), Offset.Zero, Size(w, h * 0.03f))
    // El día, en una tira de papiro pegada a la funda.
    tiraEgipto(Rect(w * 0.08f, h * 0.23f, w * 0.92f, h * 0.23f + minOf(h * 0.2f, 23f * u)), v.dia, medidor, l, false)
    // El cordel en dos vueltas, curvas como el cilindro, y el sello.
    val yc = h * 0.56f
    val cordel = Path().apply {
        moveTo(0f, yc); quadraticTo(w / 2, yc + 2.2f * u, w, yc)
        moveTo(0f, yc + 2.8f * u); quadraticTo(w / 2, yc + 5f * u, w, yc + 2.8f * u)
    }
    drawPath(corridoEgipto(cordel, 0f, 1.2f * u), a.s(0.35f), style = Stroke(1.6f * u))
    drawPath(cordel, a.c(FIBRA, l.k, l.f), style = Stroke(1.4f * u))
    drawPath(corridoEgipto(cordel, 0f, -0.5f * u), a.b(0.45f, l.k), style = Stroke(0.45f * u))
    selloEgipto(Offset(w * 0.5f, yc + 2.6f * u), minOf(w * 0.2f, 5.4f * u), v.noche, l)
    // El mes, en rojo, en su tira.
    val mw = minOf(w * 0.44f, 12f * u)
    tiraEgipto(Rect(w * 0.5f - mw / 2, h * 0.67f, w * 0.5f + mw / 2, h * 0.9f), mesEgipto(v, w < 36f * u), medidor, l, true)
    filoEgipto(Rect(0f, e / 2, w, h - e / 2), a, l.k, l.der)
    if (a.oscuro && l.f > 0.1f) drawRect(a.helado(0.5f, l.f), Offset(if (l.der) w - 1.2f * u else 0f, e / 2), Size(1.2f * u, h - e))
}

/**
 * La tablilla de escriba: una tabla de madera pintada (el color del ánimo), con su mango arriba y el agujero por donde pasa el
 * cordel, de pie y un poco ladeada; el canto a la vista, la veta bajo la pintura, las esquinas gastadas donde asoma la madera
 * y, en la cara de yeso, la cuadrícula roja de las prácticas, el día en negro y el mes en rojo; en los Sueños, un creciente
 * pintado bajo el día. Con más palabras asoman detrás otras tablillas.
 */
private fun DrawScope.tablillaEgipto(v: Volumen, medidor: TextMeasurer, l: LuzVolumenEgipto) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val giro = (azar(v.semilla, 11) - 0.5f) * 4f
    val pintura = cuerpoEgipto(l, ACACIA)
    val canto = mezcla(pintura, Color.Black, 0.3f)
    rotate(giro, Offset(w / 2, h)) {
        val tabla = Rect(w * 0.02f, h * 0.14f, w * 0.88f, h)
        val mango = Rect(tabla.left + tabla.width * 0.3f, h * 0.03f, tabla.right - tabla.width * 0.3f, tabla.top + 2f * u)
        val silueta = Path().apply {
            addRoundRect(androidx.compose.ui.geometry.RoundRect(tabla, CornerRadius(2.4f * u)))
            addRoundRect(androidx.compose.ui.geometry.RoundRect(mango, CornerRadius(mango.width * 0.45f, mango.width * 0.45f)))
        }
        sombraVolumenEgipto(silueta, l)
        // Las otras tablillas, detrás (más cuantas más palabras): sus cantos asoman a la derecha y arriba.
        val detras = (v.lleno * 3).toInt().coerceIn(0, 2)
        for (k in detras downTo 1) {
            val dx = k * 2.6f * u
            val r = Rect(tabla.left + dx, tabla.top - k * 2.4f * u, tabla.right + dx * 0.4f, h)
            drawRoundRect(mezcla(pintura, a.c(ACACIA_OSC, l.k), 0.3f + 0.15f * k), r.topLeft, r.size, CornerRadius(2.4f * u))
            drawRect(a.b(0.3f, l.k), Offset(r.left + 2f * u, r.top), Size(r.width - 4f * u, 1f * u))
            drawRect(a.s(0.3f), Offset(r.right - 2f * u, r.top + 2f * u), Size(2f * u, r.height - 2f * u))
        }
        // El canto de la tabla, a la derecha, con su grueso.
        val cantoR = Rect(tabla.right - 1f * u, tabla.top + 1.6f * u, w * 0.97f, h)
        drawRect(canto, cantoR.topLeft, cantoR.size)
        drawRect(a.s(0.25f), Offset(cantoR.right - cantoR.width * 0.4f, cantoR.top), Size(cantoR.width * 0.4f, cantoR.height))
        // La tabla y su mango, pintados.
        drawPath(silueta, pintura)
        val veta = Path()
        for (k in 0 until 5) {
            val x = tabla.left + tabla.width * (0.1f + 0.2f * k + 0.05f * azar(v.semilla, k))
            veta.moveTo(x, tabla.top + 2f * u); veta.quadraticTo(x + (azar(v.semilla, k + 9) - 0.5f) * 4f * u, tabla.center.y, x + 0.6f * u, tabla.bottom - 1f * u)
        }
        drawPath(veta, a.s(0.12f), style = Stroke(1.2f * u))
        drawPath(poligonoEgipto(tabla.left, tabla.bottom - h * 0.07f, tabla.left + w * 0.1f, tabla.bottom, tabla.left, tabla.bottom), a.c(ACACIA_LUZ, l.k, l.f))
        cilindroEgipto(tabla, a, l.k, 0.8f, l.der, l.f)
        puertaEnVolumenEgipto(tabla, l)
        drawRect(a.b(0.5f, l.k), Offset(tabla.left + 2.4f * u, tabla.top), Size(tabla.width - 4.8f * u, 1f * u))
        // El mango con su luz y el agujero; el cordel que sale de él.
        drawRect(a.b(0.3f, l.k), Offset(mango.left + 1f * u, mango.top + 2f * u), Size(mango.width * 0.3f, mango.height - 3f * u))
        drawRect(a.s(0.22f), Offset(mango.right - mango.width * 0.3f, mango.top + 2f * u), Size(mango.width * 0.3f - 0.6f * u, mango.height - 3f * u))
        val ag = Offset(mango.center.x, mango.top + mango.height * 0.42f)
        drawCircle(a.c(EgiptoTinta.CARBON, 0.3f), minOf(mango.width * 0.2f, 1.8f * u), ag)
        drawPath(Path().apply { moveTo(ag.x - 1.2f * u, ag.y); quadraticTo(ag.x - 4f * u, 0f, ag.x + 1.4f * u, 0.4f * u); quadraticTo(ag.x + 5f * u, 1f * u, ag.x + 1.2f * u, ag.y) },
            a.c(FIBRA, l.k, l.f), style = Stroke(1.2f * u))
        // La cara de yeso, ancha, con su bisel.
        val yeso = Rect(tabla.left + tabla.width * 0.14f, tabla.top + 4f * u, tabla.right - tabla.width * 0.14f, tabla.bottom - tabla.height * 0.12f)
        if (yeso.width > 4f && yeso.height > 10f) {
            drawRect(a.etiqueta(l.k, mezcla(YESO, l.tono ?: ACACIA, 0.06f)), yeso.topLeft, yeso.size)
            drawRect(a.s(0.24f), Offset(yeso.right - 1.2f * u, yeso.top), Size(1.2f * u, yeso.height))
            drawRect(a.s(0.18f), Offset(yeso.left, yeso.bottom - 1.2f * u), Size(yeso.width, 1.2f * u))
            drawRect(a.b(0.5f, l.k), yeso.topLeft, Size(yeso.width, 0.8f * u))
            val rejilla = Path()
            var gx = yeso.left + 3.6f * u
            while (gx < yeso.right - 1f) { rejilla.moveTo(gx, yeso.top + yeso.height * 0.42f); rejilla.lineTo(gx, yeso.bottom - 1.2f * u); gx += 4.4f * u }
            var gy = yeso.top + yeso.height * 0.42f
            while (gy < yeso.bottom - 1f) { rejilla.moveTo(yeso.left + 1f * u, gy); rejilla.lineTo(yeso.right - 1.2f * u, gy); gy += 4.4f * u }
            drawPath(rejilla, a.rubrica.copy(alpha = 0.22f), style = Stroke(0.5f * u))
            val dia = Rect(yeso.left, yeso.top + 0.5f * u, yeso.right, yeso.top + minOf(yeso.height * 0.3f, 22f * u))
            textoEn(medidor, v.dia, dia, MuebleEstanteEgipto.LETRA, a.tinta, 14f, peso = FontWeight.Bold, minimo = 6f)
            if (v.noche) {
                val c = Offset(yeso.center.x, dia.bottom + 4.6f * u)
                val r = minOf(yeso.width * 0.26f, 4.8f * u)
                drawCircle(a.c(EgiptoTinta.AZUL, maxOf(l.k, 0.5f)), r, c)
                drawCircle(a.etiqueta(l.k, mezcla(YESO, l.tono ?: ACACIA, 0.06f)), r * 0.82f, c + Offset(r * 0.42f, -r * 0.18f))
            }
            val mesY = dia.bottom + (if (v.noche) 10f * u else 2f * u)
            textoEn(medidor, mesEgipto(v, w < 36f * u), Rect(yeso.left + 0.5f * u, mesY, yeso.right - 1.2f * u, yeso.bottom - 1.8f * u), MuebleEstanteEgipto.LETRA, a.rubrica, 8.5f,
                vertical = true, peso = FontWeight.SemiBold, minimo = 4.5f)
        }
        filoEgipto(tabla, a, l.k, l.der)
    }
}

/**
 * El fardo: rollos y hojas envueltos en un paño de lino teñido (el color del ánimo), anudado arriba con sus dos orejas y
 * vencido por su peso; modelado en planos (la luz del lado de la puerta, la sombra del otro, los pliegues que bajan del nudo
 * con su surco y su borde de luz), más lleno cuanto más escribiste, con las puntas de los rollos asomando por el nudo. Del
 * nudo cuelga la tablilla de madera con el día; el nudo lleva su sello (en los Sueños, la luna); el mes, en una tira cosida.
 */
private fun DrawScope.fardoEgipto(v: Volumen, medidor: TextMeasurer, l: LuzVolumenEgipto) {
    val u = density
    val a = l.a
    val w = size.width; val h = size.height
    val panza = 0.03f + 0.05f * v.lleno
    val cuello = h * 0.11f
    val cx = w * (0.48f + 0.06f * (azar(v.semilla, 13) - 0.5f))
    val bulto = Path().apply {
        moveTo(cx - w * 0.12f, cuello)
        quadraticTo(w * (-0.02f - panza), h * 0.14f, w * 0.01f, h * 0.6f)
        quadraticTo(w * 0.02f, h, w * 0.2f, h)
        lineTo(w * 0.82f, h)
        quadraticTo(w * 0.99f, h, w * 0.99f, h * 0.6f)
        quadraticTo(w * (1.02f + panza), h * 0.16f, cx + w * 0.12f, cuello)
        close()
    }
    sombraVolumenEgipto(bulto, l)
    // Las puntas de los rollos que asoman por el nudo.
    val puntas = 1 + (v.lleno * 2.5f).toInt().coerceAtMost(2)
    for (j in 0 until puntas) {
        val x = cx + (j - (puntas - 1) / 2f) * 4.4f * u
        val tope = h * (0.01f + 0.035f * j)
        val pr = Rect(x - 2f * u, tope, x + 2f * u, cuello + 2f * u)
        drawRect(a.c(PAPIRO, l.k, l.f), pr.topLeft, pr.size)
        drawRect(a.s(0.25f), Offset(pr.right - 1.4f * u, pr.top), Size(1.4f * u, pr.height))
        drawOval(a.c(PAPIRO_OSC, l.k, l.f), Offset(pr.left, tope - 1f * u), Size(pr.width, 2.2f * u))
    }
    val tela = cuerpoEgipto(l, LINO)
    drawPath(bulto, tela)
    fun dentro(p: Path) = Path().apply { op(p, bulto, PathOperation.Intersect) }
    clipRect(w * 0.05f, cuello + h * 0.14f, w * 0.95f, h * 0.97f) { tramaEgipto(cuello, a) }
    run {
        // Los planos de los pliegues: el de luz del lado de la puerta y el de sombra del otro.
        drawPath(dentro(poligonoEgipto(cx - 1f * u, cuello, w * 0.06f, h * 0.55f, w * 0.12f, h, w * 0.32f, h)), a.b(0.2f, l.k))
        drawPath(dentro(poligonoEgipto(cx + 1f * u, cuello, w, h * 0.4f, w, h, w * 0.66f, h)), a.s(0.2f))
        val pliegues = Path().apply {
            moveTo(cx - 1f * u, cuello); quadraticTo(w * 0.22f, h * 0.5f, w * 0.15f, h * 0.96f)
            moveTo(cx + 1f * u, cuello); quadraticTo(w * 0.7f, h * 0.45f, w * 0.84f, h * 0.9f)
            moveTo(cx, cuello + 2f * u); quadraticTo(cx + w * 0.06f, h * 0.6f, cx - w * 0.04f, h * 0.98f)
        }
        drawPath(pliegues, a.s(0.28f), style = Stroke(1.8f * u))
        drawPath(corridoEgipto(pliegues, -1.4f * u), a.b(0.25f, l.k), style = Stroke(0.9f * u))
        if (l.f > 0.02f) drawPath(dentro(Path().apply { addRect(Rect(0f, 0f, w * 0.22f, h)) }), a.puerta(0.3f * l.f * (if (a.oscuro) 0.7f else 1f)))
        drawPath(dentro(Path().apply { addRect(Rect(0f, h * 0.9f, w, h)) }), a.s(0.22f))
    }
    // El nudo con sus dos orejas, cada una con su luz arriba y su sombra debajo.
    val orejas = Path().apply {
        moveTo(cx, cuello + 1f * u)
        quadraticTo(cx - w * 0.32f, cuello - h * 0.1f, cx - w * 0.36f, cuello - h * 0.03f)
        quadraticTo(cx - w * 0.2f, cuello + h * 0.02f, cx, cuello + 1f * u)
        quadraticTo(cx + w * 0.26f, cuello - h * 0.12f, cx + w * 0.32f, cuello - h * 0.06f)
        quadraticTo(cx + w * 0.18f, cuello + h * 0.01f, cx, cuello + 1f * u)
    }
    drawPath(orejas, mezcla(tela, Color.White, if (a.oscuro) 0.03f else 0.1f))
    clipPath(orejas) { drawRect(a.s(0.25f), Offset(0f, cuello - h * 0.02f), Size(w, h * 0.06f)) }
    drawPath(orejas, a.s(0.3f), style = Stroke(0.7f * u))
    drawOval(mezcla(tela, Color.Black, 0.16f), Offset(cx - 3.8f * u, cuello - 2.4f * u), Size(7.6f * u, 5.2f * u))
    drawArc(a.b(0.4f, l.k), 190f, 120f, false, Offset(cx - 3.8f * u, cuello - 2.4f * u), Size(7.6f * u, 5.2f * u), style = Stroke(0.8f * u))
    selloEgipto(Offset(cx + 3f * u, cuello + 2f * u), minOf(w * 0.16f, 4.2f * u), v.noche, l)
    // La tablilla de madera con el día, colgada del nudo.
    val ta = minOf(w * 0.78f, 28f * u)
    val tt = Rect(cx - ta / 2, h * 0.27f, cx + ta / 2, h * 0.27f + minOf(h * 0.22f, 19f * u))
    drawLine(a.c(FIBRA, l.k, l.f), Offset(cx, cuello + 2f * u), Offset(cx, tt.top + 1.6f * u), 0.9f * u)
    drawRoundRect(a.s(0.4f), Offset(tt.left + 1.2f * u, tt.top + 1.6f * u), tt.size, CornerRadius(1.6f * u))
    drawRoundRect(a.c(ACACIA, l.k, l.f), tt.topLeft, tt.size, CornerRadius(1.6f * u))
    drawRect(a.c(ACACIA_OSC, l.k, l.f), Offset(tt.left + 1f * u, tt.bottom - 1.6f * u), Size(tt.width - 2f * u, 1.6f * u))
    val cara = Rect(tt.left + 1.1f * u, tt.top + 1.1f * u, tt.right - 1.1f * u, tt.bottom - 2f * u)
    drawRoundRect(a.etiqueta(l.k, YESO), cara.topLeft, cara.size, CornerRadius(1f * u))
    drawRect(a.b(0.5f, l.k), cara.topLeft, Size(cara.width, 0.7f * u))
    drawCircle(a.c(EgiptoTinta.CARBON, 0.3f), 1f * u, Offset(cx, tt.top + 1.8f * u))
    textoEn(medidor, v.dia, Rect(cara.left, cara.top + 1.8f * u, cara.right, cara.bottom), MuebleEstanteEgipto.LETRA, a.tinta, 12.5f, peso = FontWeight.Bold, minimo = 6f)
    // El mes, en su tira cosida.
    val mw = minOf(w * 0.4f, 11f * u)
    val mt = Rect(cx - mw / 2, tt.bottom + 3f * u, cx + mw / 2, h * 0.9f)
    if (mt.height > 8f * u) {
        tiraEgipto(mt, mesEgipto(v, w < 36f * u), medidor, l, true)
        val puntadas = Path()
        for (j in 0 until 3) { puntadas.moveTo(mt.left - 1.2f * u, mt.top + mt.height * (0.15f + 0.35f * j)); puntadas.lineTo(mt.left + 1.2f * u, mt.top + mt.height * (0.15f + 0.35f * j) + 1f * u) }
        drawPath(puntadas, a.c(FIBRA, l.k, l.f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
    }
    if (a.oscuro && l.k > 0.1f) drawPath(orejas, a.b(0.5f, l.k), style = Stroke(0.8f * u, cap = StrokeCap.Round))
}

/**
 * La trama del lino, pintada una sola vez por densidad (más ancha y más alta que cualquier volumen): las pasadas a lo ancho,
 * tenues y un poco desiguales, y alguna hebra suelta. Se pega con una sola imagen dentro del recorte de la tela.
 */
private object TramaEgipto {
    private var densidad = 0f
    private var baldosa: ImageBitmap? = null
    fun de(u: Float): ImageBitmap {
        if (u != densidad || baldosa == null) { baldosa = pintar(u); densidad = u }
        return baldosa!!
    }
    private fun pintar(u: Float): ImageBitmap {
        val an = (50f * u).roundToInt().coerceAtLeast(4); val al = (120f * u).roundToInt().coerceAtLeast(4)
        val img = ImageBitmap(an, al)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(an.toFloat(), al.toFloat())) {
            val pasadas = Path()
            var y = 0f; var j = 0
            while (y < al) { pasadas.moveTo(0f, y); pasadas.lineTo(an.toFloat(), y + 0.6f * u * (azar(j, 3) - 0.3f)); y += 2.4f * u; j++ }
            drawPath(pasadas, SOMBRA_MUEBLE.copy(alpha = 0.1f), style = Stroke(0.5f * u))
            val hebras = Path()
            for (k in 0 until 10) { val x = an * azar(k, 5); val yy = al * azar(k, 6); hebras.moveTo(x, yy); hebras.lineTo(x + 0.6f * u, yy + 5f * u) }
            drawPath(hebras, SOMBRA_MUEBLE.copy(alpha = 0.12f), style = Stroke(0.5f * u))
        }
        return img
    }
}

/** La trama del lino desde [desde] hacia abajo (dentro del recorte de la tela). */
private fun DrawScope.tramaEgipto(desde: Float, a: AireEgipto) = drawImage(TramaEgipto.de(density), Offset(0f, desde), alpha = if (a.oscuro) 0.8f else 1f)
