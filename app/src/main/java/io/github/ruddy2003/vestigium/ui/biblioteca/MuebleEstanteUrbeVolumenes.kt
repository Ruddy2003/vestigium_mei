package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LETRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.AZUCAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CIELO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.CREMA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.OCRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.TINTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrbe.YESO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoUrbe.poligono
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Urbe: las tres entradas (el libro de bolsillo, la caja de fotos y la carpeta con su goma)
// =====================================================================

/** El color de una entrada sin ánimo marcado: un gris de cartón. */
private val SIN_ANIMO = Color(0xFF9C9284)

/** El cuerpo de una entrada: el color del ánimo con la luz que le llega, o el gris de cartón si no se marcó. */
private fun cuerpoUrbe(v: Volumen, a: AireUrbe, k: Float): Color = v.tono?.let { a.animo(it, k) } ?: a.luz(SIN_ANIMO, k)

/** La tinta que se lee sobre [fondo]: oscura sobre lo claro y crema sobre lo oscuro. */
private fun plumaUrbe(fondo: Color, a: AireUrbe, k: Float): Color =
    if (fondo.luminance() > 0.3f) a.tinta.copy(alpha = 0.88f) else a.etiqueta(maxOf(k, 0.3f)).copy(alpha = 0.92f)

/**
 * La marca de la entrada: de día, un sello redondo con una portada de arco (un emblema inventado, no una letra); en los
 * Sueños, la luna.
 */
private fun DrawScope.marcaUrbe(c: Offset, r: Float, luna: Boolean, fondo: Color, trazo: Color, a: AireUrbe, k: Float) {
    val u = density
    if (luna) {
        val l = if (a.oscuro) mezcla(LUNA, a.hondo, 0.25f - 0.25f * k) else mezcla(LUNA, CREMA, 0.4f)
        drawCircle(l, r, c)
        drawCircle(fondo, r * 0.84f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
    } else {
        drawCircle(trazo, r, c, style = Stroke(0.8f * u))
        drawArc(trazo, 180f, 180f, false, Offset(c.x - r * 0.42f, c.y - r * 0.5f), Size(r * 0.84f, r * 0.84f), style = Stroke(0.8f * u))
        drawLine(trazo, Offset(c.x - r * 0.42f, c.y - r * 0.08f), Offset(c.x - r * 0.42f, c.y + r * 0.5f), 0.8f * u)
        drawLine(trazo, Offset(c.x + r * 0.42f, c.y - r * 0.08f), Offset(c.x + r * 0.42f, c.y + r * 0.5f), 0.8f * u)
    }
}

/** El filo de la luz que manda en lo que la mira: la cabeza y el costado (derecho con la lámpara, izquierdo con la luna). */
private fun DrawScope.filoUrbe(a: AireUrbe, k: Float, arriba: Float) {
    if (!a.lumbre || k < 0.06f) return
    val u = density
    drawRect(a.filo(0.6f * k), Offset(1f * u, arriba), Size(size.width - 2f * u, 1f * u))
    val x = if (a.luna) 0f else size.width - 0.9f * u
    drawRect(a.filo(0.34f * k), Offset(x, arriba + 1.5f * u), Size(0.9f * u, size.height - arriba - 1.5f * u))
}

/** El bulto de algo de cartón o de tela: el claro a la izquierda y la sombra, en dos pasos, a la derecha. */
private fun DrawScope.bultoUrbe(a: AireUrbe, arriba: Float, izq: Float = 0f, der: Float = size.width) {
    val w = der - izq; val h = size.height - arriba
    drawRect(a.claro(0.22f), Offset(izq + w * 0.07f, arriba), Size(w * 0.12f, h))
    drawRect(a.sombra(0.14f), Offset(izq + w * 0.74f, arriba), Size(w * 0.26f, h))
    drawRect(a.sombra(0.14f), Offset(izq + w * 0.88f, arriba), Size(w * 0.12f, h))
}

/**
 * El libro de bolsillo, de pie: la tapa blanda del color del ánimo, el lomo un poco redondo con las arrugas blancas de haberlo
 * abierto (más cuanto más escribiste), la franja crema con el día, el mes escrito a lo largo del lomo, el sello de la colección
 * abajo (en los Sueños, la luna) y los papelitos que se dejaron de marcapáginas asomando por arriba.
 */
internal fun DrawScope.libroUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Float {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val top = 6f * u
    // Los papelitos que asoman: un recibo, un billete de tranvía viejo, un papel doblado.
    val n = 1 + (v.lleno * 3f).toInt()
    val papeles = listOf(CREMA, mezcla(CREMA, OCRE, 0.35f), mezcla(CREMA, CIELO, 0.4f))
    for (j in 0 until n) {
        val bw = w * (0.18f + 0.1f * azar(s, 10 + j))
        val x = w * 0.1f + (w * 0.8f - bw) * azar(s, 12 + j)
        val y0 = top * (0.05f + 0.5f * azar(s, 14 + j))
        drawPath(poligono(x, y0 + 0.8f * u, x + bw, y0, x + bw, top + 2f * u, x, top + 2f * u), a.luz(papeles[(j + s) % 3], k))
        drawLine(a.sombra(0.25f), Offset(x + bw, y0), Offset(x + bw, top + 2f * u), 0.6f * u)
    }
    val cuerpo = cuerpoUrbe(v, a, k)
    drawRoundRect(cuerpo, Offset(0f, top), Size(w, h - top), CornerRadius(2.6f * u))
    // El canto de las hojas, que se ve apenas por arriba.
    drawRect(a.luz(mezcla(CREMA, OCRE, 0.15f), k), Offset(1.5f * u, top), Size(w - 3f * u, 1.3f * u))
    bultoUrbe(a, top + 1.3f * u)
    drawRect(a.sombra(0.3f), Offset(w - 1f * u, top + 2f * u), Size(1f * u, h - top - 3f * u))
    // La franja crema de arriba, con el día.
    val franja = Rect(w * 0.09f, top + h * 0.045f, w * 0.91f, top + h * 0.045f + 19f * u)
    drawRect(a.sombra(0.25f), franja.topLeft + Offset(0.6f * u, 0.8f * u), franja.size)
    drawRect(a.etiqueta(k), franja.topLeft, franja.size)
    drawRect(a.luz(TINTA, k).copy(alpha = 0.6f), Offset(franja.left, franja.bottom - 1.2f * u), Size(franja.width, 0.8f * u))
    textoEn(medidor, v.dia, Rect(franja.left + 0.4f * u, franja.top + 0.6f * u, franja.right - 0.4f * u, franja.bottom - 1.6f * u), LETRA, a.tinta, 15f, peso = FontWeight.Bold)
    // El mes, a lo largo del lomo, como el título.
    val pluma = plumaUrbe(cuerpo, a, k)
    val yMarca = h - minOf(w * 0.3f, 9f * u)
    val mes = Rect(w * 0.2f, franja.bottom + 3f * u, w * 0.8f, yMarca - 6f * u)
    if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8.5f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.8f, minimo = 4.5f)
    // Las arrugas del lomo: líneas claras que se cortan.
    val arrugas = 1 + (v.lleno * 3.5f).toInt()
    for (j in 0 until arrugas) {
        val x = w * (0.22f + 0.56f * azar(s, 20 + j))
        val y0 = franja.bottom + (h - franja.bottom) * (0.05f + 0.3f * azar(s, 22 + j))
        val y1 = y0 + (h - franja.bottom) * (0.25f + 0.4f * azar(s, 24 + j))
        drawLine(a.claro(0.42f), Offset(x, y0), Offset(x + 0.5f * u, minOf(y1, h - 3f * u)), 0.7f * u)
        drawLine(a.sombra(0.14f), Offset(x + 0.9f * u, y0 + 2f * u), Offset(x + 1.4f * u, minOf(y1, h - 3f * u)), 0.5f * u)
    }
    // El sello de la colección, abajo (la luna en los Sueños).
    marcaUrbe(Offset(w / 2, yMarca), minOf(w * 0.17f, 3.8f * u), v.noche, cuerpo, pluma, a, k)
    // La esquina de abajo, rozada.
    drawRect(a.claro(0.18f), Offset(1.5f * u, h - 1.6f * u), Size(w - 3f * u, 1f * u))
    filoUrbe(a, k, top)
    return top
}

/**
 * La caja de fotos forrada de papel del color del ánimo, la más baja: de caras planas y cantos vivos, con las esquinas del
 * forro dobladas; la tapa encajada, un poco más ancha, con el filo blanco del papel y su sombra en la caja; el portaetiquetas
 * de latón con la tarjeta del día y del mes; y la pegatina redonda abajo (la luna, en los Sueños). Cuanto más escribiste, más
 * se levanta la tapa y asoman los cantos de las fotos.
 */
internal fun DrawScope.cajaFotosUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Float {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val cuerpo = cuerpoUrbe(v, a, k)
    val tapaAl = minOf(h * 0.14f, 11f * u)
    val alza = v.lleno * 4.5f * u
    val tapaArr = 1f * u
    val caja = Rect(2f * u, tapaArr + tapaAl * 0.6f + alza, w - 2f * u, h)
    // La caja: caras planas, con el canto de la izquierda a la luz y el de la derecha en sombra.
    drawRect(cuerpo, caja.topLeft, caja.size)
    drawRect(a.claro(0.3f), caja.topLeft, Size(1.2f * u, caja.height))
    drawRect(a.sombra(0.12f), Offset(caja.right - caja.width * 0.18f, caja.top), Size(caja.width * 0.18f, caja.height))
    drawRect(a.sombra(0.28f), Offset(caja.right - 1.2f * u, caja.top), Size(1.2f * u, caja.height))
    // El papel del forro: su grano, las esquinas dobladas y la junta del papel en el medio.
    for (j in 0 until 4) drawLine(a.sombra(0.05f), Offset(caja.left, caja.top + caja.height * (0.3f + 0.17f * j)), Offset(caja.right, caja.top + caja.height * (0.3f + 0.17f * j) + 1f * u), 0.6f * u)
    drawPath(poligono(caja.left, h, caja.left, h - 5f * u, caja.left + 5f * u, h), mezcla(cuerpo, a.hondo, 0.18f))
    drawPath(poligono(caja.right, h, caja.right, h - 5f * u, caja.right - 5f * u, h), mezcla(cuerpo, a.hondo, 0.24f))
    drawLine(mezcla(cuerpo, a.hondo, 0.2f), Offset(caja.left + caja.width * 0.62f, caja.top), Offset(caja.left + caja.width * 0.62f, h), 0.6f * u)
    // Los cantos de las fotos, entre la caja y la tapa levantada.
    if (alza > 0.8f * u) for (j in 0 until 3) {
        val x0 = caja.left + caja.width * (0.06f + 0.1f * j)
        drawRect(a.luz(if (j == 1) mezcla(CREMA, OCRE, 0.2f) else CREMA, k), Offset(x0, caja.top - alza * (0.5f + 0.4f * azar(s, 30 + j))), Size(caja.width * (0.8f - 0.12f * j), 1.1f * u))
    }
    // La tapa, encajada, más ancha que la caja, con el filo blanco del papel y su sombra.
    val tapa = Rect(0f, tapaArr + alza * 0.2f, w, tapaArr + alza * 0.2f + tapaAl)
    drawRect(a.sombra(0.36f), Offset(caja.left, tapa.bottom), Size(caja.width, 2.2f * u))
    drawRect(mezcla(cuerpo, a.hondo, 0.08f), tapa.topLeft, tapa.size)
    drawRect(a.claro(0.4f), tapa.topLeft, Size(w, 1f * u))
    drawRect(a.claro(0.3f), tapa.topLeft, Size(1f * u, tapa.height))
    drawRect(a.sombra(0.26f), Offset(w - 1.2f * u, tapa.top), Size(1.2f * u, tapa.height))
    drawRect(a.luz(CREMA, k).copy(alpha = 0.7f), Offset(0f, tapa.bottom - 1.4f * u), Size(w, 0.8f * u))
    drawRect(a.sombra(0.3f), Offset(0f, tapa.bottom - 0.6f * u), Size(w, 0.6f * u))
    // El portaetiquetas de latón con la tarjeta del día.
    val por = Rect(w * 0.12f, caja.top + minOf(h * 0.05f, 4f * u), w * 0.88f, caja.top + minOf(h * 0.05f, 4f * u) + 20f * u)
    drawRect(a.sombra(0.3f), por.topLeft + Offset(0.6f * u, 0.8f * u), por.size)
    drawRect(a.luz(LATON, maxOf(k, 0.2f)), por.topLeft, por.size)
    val tarjeta = por.deflate(1.4f * u)
    drawRect(a.etiqueta(k), tarjeta.topLeft, tarjeta.size)
    drawRect(a.claro(0.5f), Offset(por.left, por.top), Size(por.width, 0.8f * u))
    textoEn(medidor, v.dia, Rect(tarjeta.left + 1f * u, tarjeta.top + 0.3f * u, tarjeta.right - 1f * u, tarjeta.bottom - 0.3f * u), LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
    // El mes, debajo, a lo largo.
    val pluma = plumaUrbe(cuerpo, a, k)
    val mes = Rect(w * 0.2f, por.bottom + 3f * u, w * 0.8f, h - 4f * u)
    if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 4.5f)
    // En la tapa, la pegatina redonda (la luna, en los Sueños).
    val rs = minOf(w * 0.16f, tapa.height * 0.36f)
    val cm = Offset(w / 2, tapa.center.y - 0.3f * u)
    drawCircle(a.sombra(0.25f), rs * 1.12f, cm + Offset(0.5f * u, 0.6f * u))
    drawCircle(a.etiqueta(k), rs * 1.12f, cm)
    marcaUrbe(cm, rs * 0.78f, v.noche, a.etiqueta(k), a.tinta.copy(alpha = 0.8f), a, k)
    filoUrbe(a, k, tapa.top)
    return tapa.top
}

/**
 * La carpeta de cartón prensado con su goma, de pie, vista por el lomo: el cartón del color del ánimo con su grano; las hojas
 * que asoman por arriba, desparejas (más cuanto más escribiste; si va muy llena, el fuelle del lomo); la etiqueta del día; el
 * mes; la goma negra que la ciñe, con su remache; y la pegatina abajo (la luna, en los Sueños).
 */
internal fun DrawScope.carpetaUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Float {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val arriba = minOf(h * 0.14f, 13f * u)
    // Las hojas que asoman: papel, papel de calco azulado y alguna amarillenta.
    val n = 2 + (v.lleno * 4f).toInt()
    val hojas = listOf(CREMA, mezcla(CIELO, CREMA, 0.55f), mezcla(CREMA, OCRE, 0.3f), mezcla(YESO, CREMA, 0.5f))
    for (j in 0 until n) {
        val x0 = w * (0.04f + 0.07f * azar(s, 40 + j))
        val x1 = w * (0.9f + 0.06f * azar(s, 41 + j))
        val y0 = arriba * (0.05f + 0.75f * azar(s, 42 + j))
        val ladeo = (azar(s, 43 + j) - 0.5f) * 3f * u
        drawPath(poligono(x0, y0 + ladeo, x1, y0 - ladeo, x1, arriba + 2f * u, x0, arriba + 2f * u), a.luz(hojas[(j + s) % hojas.size], k))
        drawLine(a.sombra(0.2f), Offset(x0, y0 + ladeo), Offset(x1, y0 - ladeo), 0.5f * u)
    }
    val cuerpo = cuerpoUrbe(v, a, k)
    drawRoundRect(cuerpo, Offset(0f, arriba), Size(w, h - arriba), CornerRadius(1.6f * u))
    // El grano del cartón prensado.
    for (j in 0 until 7) drawLine(a.sombra(0.07f), Offset(0f, arriba + (h - arriba) * (0.08f + 0.13f * j)), Offset(w, arriba + (h - arriba) * (0.08f + 0.13f * j) + 0.8f * u), 0.6f * u)
    bultoUrbe(a, arriba)
    drawRect(a.claro(0.3f), Offset(1f * u, arriba), Size(w - 2f * u, 0.9f * u))
    // El fuelle del lomo, si va llena.
    if (v.lleno > 0.45f) listOf(0.14f, 0.86f).forEach { f ->
        drawLine(a.sombra(0.24f), Offset(w * f, arriba + 2f * u), Offset(w * f, h - 2f * u), 0.8f * u)
        drawLine(a.claro(0.25f), Offset(w * f + 1f * u, arriba + 2f * u), Offset(w * f + 1f * u, h - 2f * u), 0.5f * u)
    }
    // La etiqueta del día, pegada en el lomo.
    val et = Rect(w * 0.12f, arriba + h * 0.05f, w * 0.88f, arriba + h * 0.05f + 19f * u)
    drawRect(a.sombra(0.3f), et.topLeft + Offset(0.6f * u, 0.8f * u), et.size)
    drawRect(a.etiqueta(k), et.topLeft, et.size)
    drawRect(a.luz(mezcla(AZUCAR, TINTA, 0.2f), k).copy(alpha = 0.7f), et.topLeft, Size(et.width, 1.6f * u))
    textoEn(medidor, v.dia, Rect(et.left + 0.4f * u, et.top + 1.6f * u, et.right - 0.4f * u, et.bottom - 0.4f * u), LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
    // La goma que la ciñe, con el remache donde se cose.
    val yg = h * 0.8f
    val pluma = plumaUrbe(cuerpo, a, k)
    val mes = Rect(w * 0.2f, et.bottom + 3f * u, w * 0.8f, yg - 4f * u)
    if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 4.5f)
    val goma = a.luz(mezcla(TINTA, Color(0xFF39424A), 0.4f), maxOf(k, 0.15f))
    drawRect(a.sombra(0.35f), Offset(0f, yg + 2.6f * u), Size(w, 1.2f * u))
    drawRect(goma, Offset(0f, yg), Size(w, 2.6f * u))
    drawRect(a.claro(0.3f), Offset(0f, yg + 0.3f * u), Size(w * 0.6f, 0.6f * u))
    drawCircle(a.luz(mezcla(LATON, YESO, 0.3f), maxOf(k, 0.2f)), 1.6f * u, Offset(w * 0.2f, yg + 1.3f * u))
    drawCircle(a.claro(0.5f), 0.6f * u, Offset(w * 0.2f - 0.4f * u, yg + 0.9f * u))
    // La pegatina de abajo (la luna en los Sueños).
    val yMarca = yg + (h - yg) * 0.55f
    val rs = minOf(w * 0.18f, 4f * u)
    drawCircle(a.etiqueta(k), rs * 1.1f, Offset(w / 2, yMarca))
    marcaUrbe(Offset(w / 2, yMarca), rs * 0.75f, v.noche, a.etiqueta(k), a.tinta.copy(alpha = 0.8f), a, k)
    drawRect(a.sombra(0.25f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    filoUrbe(a, k, arriba)
    return arriba
}
