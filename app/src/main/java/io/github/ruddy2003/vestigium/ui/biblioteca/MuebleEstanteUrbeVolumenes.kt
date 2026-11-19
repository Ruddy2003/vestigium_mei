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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteUrbe.LETRA
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
import kotlin.math.cbrt

// =====================================================================
//  Urbe: las tres entradas (el libro de bolsillo, la caja de fotos y la carpeta con su goma)
// =====================================================================

/** El color de una entrada sin ánimo marcado: un gris de cartón. */
private val SIN_ANIMO = Color(0xFF9C9284)

private fun linealUrbe(c: Float) = if (c <= 0.04045f) c / 12.92f else Math.pow((c + 0.055) / 1.055, 2.4).toFloat()
private fun srgbUrbe(c: Float): Float { val x = c.coerceIn(0f, 1f); return if (x <= 0.0031308f) 12.92f * x else (1.055 * Math.pow(x.toDouble(), 1 / 2.4) - 0.055).toFloat() }

/**
 * El color del ánimo, más vivo: el mismo tono y la misma claridad, con más color (el croma de Oklab por [f]). La escala del tema
 * va del almagra al verde de las persianas pasando por el travertino, y en un lomo de 26 dp sus tonos de en medio se leían grises.
 */
internal fun vivoUrbe(c: Color, f: Float): Color {
    val r = linealUrbe(c.red); val g = linealUrbe(c.green); val b = linealUrbe(c.blue)
    val l = cbrt(0.4122214708f * r + 0.5363325363f * g + 0.0514459929f * b)
    val m = cbrt(0.2119034982f * r + 0.6806995451f * g + 0.1073969566f * b)
    val s = cbrt(0.0883024619f * r + 0.2817188376f * g + 0.6299787005f * b)
    val L = 0.2104542553f * l + 0.7936177850f * m - 0.0040720468f * s
    val A = (1.9779984951f * l - 2.4285922050f * m + 0.4505937099f * s) * f
    val B = (0.0259040371f * l + 0.7827717662f * m - 0.8086757660f * s) * f
    val l2 = L + 0.3963377774f * A + 0.2158037573f * B
    val m2 = L - 0.1055613458f * A - 0.0638541728f * B
    val s2 = L - 0.0894841775f * A - 1.2914855480f * B
    val l3 = l2 * l2 * l2; val m3 = m2 * m2 * m2; val s3 = s2 * s2 * s2
    return Color(
        srgbUrbe(4.0767416621f * l3 - 3.3077115913f * m3 + 0.2309699292f * s3),
        srgbUrbe(-1.2684380046f * l3 + 2.6097574011f * m3 - 0.3413193965f * s3),
        srgbUrbe(-0.0041960863f * l3 - 0.7034186147f * m3 + 1.7076147010f * s3),
        c.alpha,
    )
}

/** El cuerpo de una entrada: el color del ánimo, más vivo, con la luz que le llega; o el gris de cartón si no se marcó. */
private fun cuerpoUrbe(v: Volumen, a: AireUrbe, k: Float): Color =
    v.tono?.let { a.animo(vivoUrbe(it, if (a.oscuro) 1.35f else 1.75f), k) } ?: a.luz(SIN_ANIMO, k)

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
private fun DrawScope.filoUrbe(a: AireUrbe, k: Float, r: Rect) {
    if (!a.lumbre || k < 0.06f) return
    val u = density
    drawRect(a.filo(0.6f * k), Offset(r.left + 1f * u, r.top), Size(r.width - 2f * u, 1f * u))
    val x = if (a.luna) r.left else r.right - 0.9f * u
    drawRect(a.filo(0.34f * k), Offset(x, r.top + 1.5f * u), Size(0.9f * u, r.height - 1.5f * u))
}

/** La etiqueta chica del día: papel con su sombra y el número, en tinta que guarda el contraste aunque el papel se apague. */
private fun DrawScope.etiquetaUrbe(v: Volumen, medidor: TextMeasurer, r: Rect, a: AireUrbe, k: Float, tam: Float = 12.5f) {
    val u = density
    drawRect(a.sombra(0.3f), r.topLeft + Offset(0.6f * u, 0.8f * u), r.size)
    drawRect(a.etiqueta(k), r.topLeft, r.size)
    drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
    textoEn(medidor, v.dia, Rect(r.left + 0.3f * u, r.top + 0.4f * u, r.right - 0.3f * u, r.bottom - 0.2f * u), LETRA, a.tintaDia, tam, peso = FontWeight.Bold, minimo = 7f)
}

/**
 * El libro de bolsillo: bajo y grueso, de tapa blanda del color del ánimo, con el lomo redondo (la cabeza y el pie en curva, la
 * luz en la loma y la sombra en el costado) y arrugado de abrirlo, más cuanto más escribiste; la etiqueta chica con el día,
 * el mes a lo largo del lomo como el título, el sello de la colección abajo (en los Sueños, la luna) y los papelitos que se
 * dejaron de marcapáginas asomando por la cabeza. Devuelve dónde está su cuerpo.
 */
internal fun DrawScope.libroUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Rect {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val top = 6f * u
    val curva = minOf(1.1f * u, w * 0.04f)
    // Los papelitos que asoman: un recibo, un billete viejo, un papel doblado.
    val n = 1 + (v.lleno * 3f).toInt()
    val papeles = listOf(CREMA, mezcla(CREMA, OCRE, 0.35f), mezcla(CREMA, CIELO, 0.4f))
    for (j in 0 until n) {
        val bw = w * (0.16f + 0.1f * azar(s, 10 + j))
        val x = w * 0.12f + (w * 0.76f - bw) * azar(s, 12 + j)
        val y0 = top * (0.05f + 0.45f * azar(s, 14 + j))
        drawPath(poligono(x, y0 + 0.8f * u, x + bw, y0, x + bw, top + 2f * u, x, top + 2f * u), a.luz(papeles[(j + s) % 3], k))
        drawLine(a.sombra(0.25f), Offset(x + bw, y0), Offset(x + bw, top + 2f * u), 0.6f * u)
    }
    val cuerpo = cuerpoUrbe(v, a, k)
    // El lomo: la cabeza y el pie en curva, porque el lomo es redondo.
    val lomo = Path().apply {
        moveTo(0f, top + curva + 1.5f * u)
        cubicTo(0f, top + curva * 0.3f, w * 0.12f, top, w * 0.3f, top)
        lineTo(w * 0.7f, top)
        cubicTo(w * 0.88f, top, w, top + curva * 0.3f, w, top + curva + 1.5f * u)
        lineTo(w, h - 1.2f * u)
        lineTo(0f, h - 1.2f * u)
        close()
    }
    drawPath(lomo, cuerpo)
    clipPath(lomo) {
        // La loma a la luz, el costado en sombra (en dos pasos) y el canto de las hojas en la cabeza.
        drawRect(a.claro(0.2f), Offset(w * 0.12f, 0f), Size(w * 0.14f, h))
        drawRect(a.sombra(0.12f), Offset(w * 0.74f, 0f), Size(w * 0.26f, h))
        drawRect(a.sombra(0.16f), Offset(w * 0.9f, 0f), Size(w * 0.1f, h))
        // Las tapas blandas, que se ven apenas por los cantos, un poco abiertas de tanto usarlo.
        drawRect(mezcla(cuerpo, a.hondo, 0.22f), Offset(0f, 0f), Size(1.2f * u, h))
        drawRect(mezcla(cuerpo, a.hondo, 0.3f), Offset(w - 1.2f * u, 0f), Size(1.2f * u, h))
        // Las arrugas: líneas claras a lo largo, cortadas, y algún quiebre de través.
        val arrugas = 3 + (v.lleno * 4f).toInt()
        for (j in 0 until arrugas) {
            val x = w * (0.2f + 0.6f * azar(s, 20 + j))
            val y0 = top + (h - top) * (0.24f + 0.25f * azar(s, 22 + j))
            val y1 = y0 + (h - top) * (0.2f + 0.45f * azar(s, 24 + j))
            val q = Offset(x + (azar(s, 26 + j) - 0.5f) * 1.6f * u, (y0 + y1) / 2)
            drawLine(a.claro(0.7f), Offset(x, y0), q, 0.8f * u)
            drawLine(a.claro(0.6f), q, Offset(x + 0.5f * u, minOf(y1, h - 3f * u)), 0.8f * u)
            drawLine(a.sombra(0.18f), Offset(x + 0.9f * u, y0 + 1f * u), Offset(x + 1.3f * u, minOf(y1, h - 3f * u)), 0.5f * u)
        }
        for (j in 0 until 2) {
            val y = top + (h - top) * (0.5f + 0.25f * azar(s, 28 + j))
            drawLine(a.claro(0.35f), Offset(w * 0.15f, y), Offset(w * (0.45f + 0.3f * azar(s, 29 + j)), y + 1f * u), 0.6f * u)
        }
        drawRect(a.claro(0.4f), Offset(w * 0.2f, top), Size(w * 0.6f, 0.8f * u))
    }
    // La etiqueta chica, con el día.
    val et = Rect(w * 0.16f, top + curva + 5f * u, w * 0.84f, top + curva + 5f * u + 14f * u)
    etiquetaUrbe(v, medidor, et, a, k)
    // El mes, a lo largo del lomo, y el sello abajo (la luna en los Sueños).
    val pluma = plumaUrbe(cuerpo, a, k)
    val yMarca = h - minOf(w * 0.24f, 8f * u)
    val mes = Rect(w * 0.22f, et.bottom + 3f * u, w * 0.78f, yMarca - 8f * u)
    if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.8f, minimo = 4.5f)
    listOf(yMarca - 6.5f * u, yMarca - 4.8f * u, et.top - 2.2f * u).forEach { y -> drawLine(pluma.copy(alpha = 0.55f), Offset(w * 0.08f, y), Offset(w * 0.92f, y), 0.6f * u) }
    marcaUrbe(Offset(w / 2, yMarca), minOf(w * 0.15f, 3.4f * u), v.noche, cuerpo, pluma, a, k)
    val r = Rect(0f, top, w, h - 1.2f * u)
    filoUrbe(a, k, r)
    return r
}

/**
 * La caja de fotos, la más baja y ancha: forrada de papel del color del ánimo, de caras planas, con la tapa que sobresale por
 * los dos lados (con su labio y su sombra), las esquinas del forro dobladas, el portaetiquetas de latón con el día, el mes
 * debajo, el tirador de latón con su anilla abajo y, en la tapa, la pegatina redonda (la luna, en los Sueños). Cuanto más
 * escribiste, más se levanta la tapa y asoman los cantos de las fotos. Devuelve dónde está su cuerpo.
 */
internal fun DrawScope.cajaFotosUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Rect {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val cuerpo = cuerpoUrbe(v, a, k)
    val tapaAl = minOf(h * 0.15f, 10f * u)
    val alza = v.lleno * 4f * u
    val vuelo = minOf(2.6f * u, w * 0.1f)
    val tapaArr = 1f * u
    val caja = Rect(vuelo, tapaArr + tapaAl * 0.7f + alza, w - vuelo, h)
    drawRect(cuerpo, caja.topLeft, caja.size)
    drawRect(a.claro(0.3f), caja.topLeft, Size(1.2f * u, caja.height))
    drawRect(a.sombra(0.12f), Offset(caja.right - caja.width * 0.2f, caja.top), Size(caja.width * 0.2f, caja.height))
    drawRect(a.sombra(0.3f), Offset(caja.right - 1.2f * u, caja.top), Size(1.2f * u, caja.height))
    // El papel del forro: su grano, las esquinas dobladas y la junta.
    for (j in 0 until 3) drawLine(a.sombra(0.05f), Offset(caja.left, caja.top + caja.height * (0.4f + 0.18f * j)), Offset(caja.right, caja.top + caja.height * (0.4f + 0.18f * j) + 1f * u), 0.6f * u)
    drawPath(poligono(caja.left, h, caja.left, h - 5f * u, caja.left + 5f * u, h), mezcla(cuerpo, a.hondo, 0.2f))
    drawPath(poligono(caja.right, h, caja.right, h - 5f * u, caja.right - 5f * u, h), mezcla(cuerpo, a.hondo, 0.26f))
    drawLine(mezcla(cuerpo, a.hondo, 0.2f), Offset(caja.left + caja.width * 0.64f, caja.top), Offset(caja.left + caja.width * 0.64f, h), 0.6f * u)
    // Los cantos de las fotos, entre la caja y la tapa levantada.
    if (alza > 0.8f * u) for (j in 0 until 3) {
        val x0 = caja.left + caja.width * (0.06f + 0.1f * j)
        drawRect(a.luz(if (j == 1) mezcla(CREMA, OCRE, 0.2f) else CREMA, k), Offset(x0, caja.top - alza * (0.5f + 0.4f * azar(s, 30 + j))), Size(caja.width * (0.8f - 0.12f * j), 1.1f * u))
    }
    // La tapa, que sobresale, con su labio claro abajo y su sombra en la caja.
    val tapa = Rect(0f, tapaArr + alza * 0.2f, w, tapaArr + alza * 0.2f + tapaAl)
    drawRect(a.sombra(0.38f), Offset(caja.left, tapa.bottom), Size(caja.width, 2.4f * u))
    drawRect(mezcla(cuerpo, a.hondo, 0.1f), tapa.topLeft, tapa.size)
    drawRect(a.claro(0.42f), tapa.topLeft, Size(w, 1f * u))
    drawRect(a.claro(0.3f), tapa.topLeft, Size(1f * u, tapa.height))
    drawRect(a.sombra(0.28f), Offset(w - 1.2f * u, tapa.top), Size(1.2f * u, tapa.height))
    drawRect(a.luz(CREMA, k).copy(alpha = 0.7f), Offset(0f, tapa.bottom - 1.6f * u), Size(w, 0.8f * u))
    drawRect(a.sombra(0.32f), Offset(0f, tapa.bottom - 0.6f * u), Size(w, 0.6f * u))
    val rs = minOf(w * 0.13f, tapa.height * 0.34f)
    val cm = Offset(w / 2, tapa.center.y - 0.3f * u)
    drawCircle(a.etiqueta(k), rs * 1.12f, cm)
    marcaUrbe(cm, rs * 0.78f, v.noche, a.etiqueta(k), a.tintaDia.copy(alpha = 0.8f), a, k)
    // El portaetiquetas de latón con la tarjeta del día.
    val por = Rect(caja.left + caja.width * 0.12f, caja.top + 4f * u, caja.right - caja.width * 0.12f, caja.top + 4f * u + 15f * u)
    drawRect(a.sombra(0.3f), por.topLeft + Offset(0.6f * u, 0.8f * u), por.size)
    drawRect(a.luz(LATON, maxOf(k, 0.2f)), por.topLeft, por.size)
    drawRect(a.claro(0.5f), por.topLeft, Size(por.width, 0.7f * u))
    etiquetaUrbe(v, medidor, por.deflate(1.3f * u), a, k, 12f)
    // El tirador de latón abajo, con su anilla.
    val ty = h - minOf(h * 0.16f, 9f * u)
    val tp = Rect(w / 2 - caja.width * 0.22f, ty - 1.6f * u, w / 2 + caja.width * 0.22f, ty + 1.6f * u)
    drawRoundRect(a.sombra(0.3f), tp.topLeft + Offset(0.6f * u, 0.8f * u), tp.size, CornerRadius(1.6f * u))
    drawRoundRect(a.luz(LATON, maxOf(k, 0.2f)), tp.topLeft, tp.size, CornerRadius(1.6f * u))
    drawArc(a.luz(mezcla(LATON, TINTA, 0.25f), maxOf(k, 0.2f)), 0f, 180f, false, Offset(w / 2 - caja.width * 0.16f, ty - caja.width * 0.12f), Size(caja.width * 0.32f, caja.width * 0.3f), style = Stroke(1.3f * u))
    // El mes, entre la tarjeta y el tirador.
    val pluma = plumaUrbe(cuerpo, a, k)
    val mes = Rect(w * 0.24f, por.bottom + 2.5f * u, w * 0.76f, ty - caja.width * 0.05f - 3f * u)
    if (mes.height > 9f * u) textoEn(medidor, v.mes.take(3), mes, LETRA, pluma, 7.5f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.5f, minimo = 4.5f)
    val r = Rect(0f, tapa.top, w, h)
    filoUrbe(a, k, r)
    return r
}

/**
 * La carpeta de cartón prensado, alta y delgada, de pie y vista por el lomo: el cartón del color del ánimo con su grano; las
 * hojas que asoman por arriba y por el costado, desparejas, más cuanto más escribiste (papel, papel de calco, alguna
 * amarillenta); la etiqueta chica con el día; el mes; la goma negra que la ciñe, con su remache; y la pegatina abajo (la luna,
 * en los Sueños). Si va muy llena, el fuelle del lomo. Devuelve dónde está su cuerpo.
 */
internal fun DrawScope.carpetaUrbe(v: Volumen, medidor: TextMeasurer, a: AireUrbe, k: Float): Rect {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val an = maxOf(w * 0.74f, minOf(w, 18f * u))
    val arriba = minOf(h * 0.15f, 16f * u)
    // Las hojas que asoman: por arriba, desparejas, y alguna por el costado.
    val n = 2 + (v.lleno * 4f).toInt()
    val hojas = listOf(CREMA, mezcla(CIELO, CREMA, 0.55f), mezcla(CREMA, OCRE, 0.3f), mezcla(YESO, CREMA, 0.5f))
    for (j in 0 until n) {
        val x0 = an * (0.05f + 0.08f * azar(s, 40 + j))
        val x1 = minOf(w, an * (0.95f + 0.22f * azar(s, 41 + j)))
        val y0 = arriba * (0.05f + 0.75f * azar(s, 42 + j))
        val ladeo = (azar(s, 43 + j) - 0.5f) * 3f * u
        drawPath(poligono(x0, y0 + ladeo, x1, y0 - ladeo, x1, arriba + 3f * u, x0, arriba + 3f * u), a.luz(hojas[(j + s) % hojas.size], k))
        drawLine(a.sombra(0.22f), Offset(x0, y0 + ladeo), Offset(x1, y0 - ladeo), 0.5f * u)
    }
    if (w - an > 3f * u) for (j in 0 until minOf(n, 3)) {
        val y = arriba + (h - arriba) * (0.2f + 0.22f * j + 0.08f * azar(s, 44 + j))
        drawPath(poligono(an - 2f * u, y, an + (w - an) * (0.5f + 0.4f * azar(s, 45 + j)), y + 1.2f * u, an + (w - an) * 0.6f, y + (h - arriba) * 0.12f, an - 2f * u, y + (h - arriba) * 0.12f), a.luz(hojas[(j + 1) % hojas.size], k))
    }
    val cuerpo = cuerpoUrbe(v, a, k)
    drawRoundRect(cuerpo, Offset(0f, arriba), Size(an, h - arriba), CornerRadius(1.6f * u))
    // El grano del cartón prensado y el bulto: el claro a la izquierda y la sombra a la derecha.
    for (j in 0 until 7) drawLine(a.sombra(0.07f), Offset(0f, arriba + (h - arriba) * (0.08f + 0.13f * j)), Offset(an, arriba + (h - arriba) * (0.08f + 0.13f * j) + 0.8f * u), 0.6f * u)
    drawRect(a.claro(0.24f), Offset(an * 0.08f, arriba), Size(an * 0.12f, h - arriba))
    drawRect(a.sombra(0.16f), Offset(an * 0.76f, arriba), Size(an * 0.24f, h - arriba))
    drawRect(a.sombra(0.3f), Offset(an - 1f * u, arriba), Size(1f * u, h - arriba))
    drawRect(a.claro(0.3f), Offset(1f * u, arriba), Size(an - 2f * u, 0.9f * u))
    if (v.lleno > 0.45f) listOf(0.14f, 0.86f).forEach { f ->
        drawLine(a.sombra(0.24f), Offset(an * f, arriba + 2f * u), Offset(an * f, h - 2f * u), 0.8f * u)
        drawLine(a.claro(0.25f), Offset(an * f + 1f * u, arriba + 2f * u), Offset(an * f + 1f * u, h - 2f * u), 0.5f * u)
    }
    // La etiqueta chica del día.
    val et = Rect(an * 0.1f, arriba + 4f * u, an * 0.9f, arriba + 4f * u + 14f * u)
    etiquetaUrbe(v, medidor, et, a, k, 11.5f)
    // La goma, abajo, con el remache; el mes entre la etiqueta y la goma.
    val yg = h * 0.8f
    val pluma = plumaUrbe(cuerpo, a, k)
    val mes = Rect(an * 0.18f, et.bottom + 3f * u, an * 0.82f, yg - 4f * u)
    if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8f, vertical = true, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 4.5f)
    val goma = a.luz(mezcla(TINTA, Color(0xFF39424A), 0.4f), maxOf(k, 0.15f))
    drawRect(a.sombra(0.35f), Offset(0f, yg + 2.6f * u), Size(an, 1.2f * u))
    drawRect(goma, Offset(0f, yg), Size(an, 2.6f * u))
    drawRect(a.claro(0.3f), Offset(0f, yg + 0.3f * u), Size(an * 0.6f, 0.6f * u))
    drawCircle(a.luz(mezcla(LATON, YESO, 0.3f), maxOf(k, 0.2f)), 1.6f * u, Offset(an * 0.2f, yg + 1.3f * u))
    drawLine(goma, Offset(an - 1f * u, yg + 1.3f * u), Offset(minOf(w, an + 2f * u), yg + 2.6f * u), 1.6f * u, StrokeCap.Round)
    val yMarca = yg + (h - yg) * 0.55f
    val rs = minOf(an * 0.17f, 3.6f * u)
    drawCircle(a.etiqueta(k), rs * 1.1f, Offset(an / 2, yMarca))
    marcaUrbe(Offset(an / 2, yMarca), rs * 0.75f, v.noche, a.etiqueta(k), a.tintaDia.copy(alpha = 0.8f), a, k)
    drawRect(a.sombra(0.25f), Offset(0f, h - 1.4f * u), Size(an, 1.4f * u))
    val r = Rect(0f, arriba, an, h)
    filoUrbe(a, k, r)
    return r
}
