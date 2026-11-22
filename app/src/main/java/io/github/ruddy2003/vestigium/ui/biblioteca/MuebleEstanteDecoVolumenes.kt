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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.LETRA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.escalonDeco
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.SIN_ANIMO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

// =====================================================================
//  Art déco: las tres entradas
// =====================================================================

/** La estraza de las fundas que asoman del álbum. */
private val ESTRAZA_VOL = Color(0xFFB58D5E)

/** Qué tan claro es un color (para escribir encima en tinta o en marfil). */
private fun claridad(c: Color): Float = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

/** El nombre del mes sin los adornos del rótulo («‹ Noviembre ›» queda en «Noviembre»), para que quepa en el volumen. */
private fun mesLimpio(mes: String): String = mes.filter { it.isLetterOrDigit() || it == ' ' }.trim().ifEmpty { mes.trim() }

/** El mes, a lo largo, en tinta o en marfil según lo claro del fondo, para que se lea sobre cualquier ánimo. */
private fun DrawScope.mesDeco(v: Volumen, medidor: TextMeasurer, r: Rect, fondo: Color, a: AireDeco) {
    val tinta = if (claridad(fondo) > 0.5f) a.tinta else mezcla(MARFIL, Color.White, 0.3f)
    textoEn(medidor, mesLimpio(v.mes), r, LETRA, tinta, 8.5f, vertical = true, peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 5.5f)
}

/** La etiqueta de marfil con las esquinas en escalón y el día. */
private fun DrawScope.etiquetaDeco(v: Volumen, medidor: TextMeasurer, r: Rect, a: AireDeco, k: Float) {
    val u = density
    if (r.width <= 2f || r.height <= 2f) return
    val e = minOf(r.width, r.height) * 0.2f
    drawPath(escalonDeco(Rect(r.left + 0.8f * u, r.top + 1f * u, r.right + 0.8f * u, r.bottom + 1f * u), e), a.sombra(0.35f))
    drawPath(escalonDeco(r, e), a.etiqueta(maxOf(k, 0.3f)))
    drawPath(escalonDeco(Rect(r.left + 1.4f * u, r.top + 1.4f * u, r.right - 1.4f * u, r.bottom - 1.4f * u), e * 0.7f), a.sombra(0.25f), style = Stroke(0.5f * u))
    textoEn(medidor, v.dia, Rect(r.left + 1.5f * u, r.top + 1f * u, r.right - 1.5f * u, r.bottom - 1f * u), LETRA, a.tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 6f)
}

/** El sello: de día, un sol de níquel con sus rayos; en los Sueños, una luna creciente. [fondo] es lo que tiene detrás. */
private fun DrawScope.selloDeco(c: Offset, r: Float, noche: Boolean, fondo: Color, a: AireDeco, k: Float) {
    val u = density
    if (r < 1.5f * u) return
    val metal = a.luz(mezcla(NIQUEL, MARFIL, 0.3f), maxOf(k, 0.45f))
    if (noche) {
        drawCircle(a.luz(Color(0xFFF1E7C4), maxOf(k, 0.6f)), r, c)
        drawCircle(fondo, r * 0.86f, Offset(c.x + r * 0.42f, c.y - r * 0.2f))
    } else {
        for (j in 0 until 8) {
            val ang = Math.toRadians(j * 45.0).toFloat()
            drawLine(metal, Offset(c.x + r * 0.55f * kotlin.math.cos(ang), c.y + r * 0.55f * kotlin.math.sin(ang)),
                Offset(c.x + r * kotlin.math.cos(ang), c.y + r * kotlin.math.sin(ang)), 0.9f * u, StrokeCap.Round)
        }
        drawCircle(metal, r * 0.42f, c)
        drawCircle(a.sombra(0.3f), r * 0.42f, c, style = Stroke(0.5f * u))
    }
}

/** La luz y la sombra de los dos lados de un cuerpo: el que mira la luz, claro y con su filo; el otro, hundido. */
private fun DrawScope.ladosDeco(r: Rect, a: AireDeco, k: Float, izq: Boolean, redondo: Boolean) {
    val u = density
    val claro = r.width * (if (redondo) 0.26f else 0.14f)
    val hondo = r.width * (if (redondo) 0.3f else 0.18f)
    drawRect(a.claro(if (redondo) 0.3f else 0.22f), Offset(if (izq) r.left else r.right - claro, r.top), Size(claro, r.height))
    drawRect(a.sombra(if (redondo) 0.32f else 0.26f), Offset(if (izq) r.right - hondo else r.left, r.top), Size(hondo, r.height))
    if (a.lumbre && k > 0.05f) drawRect(a.filo(0.55f * k), Offset(if (izq) r.left else r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
    drawRect(a.sombra(0.4f), Offset(if (izq) r.right - 1f * u else r.left, r.top), Size(1f * u, r.height))
}

/**
 * La caja del rollo de pianola: alta y derecha, de cartón forrado de papel del color del ánimo con dos filetes impresos, la
 * tapa más ancha y más oscura con su filete de níquel, la etiqueta de marfil con el día, el mes a lo largo y el sello abajo.
 * Cuanto más escribiste, más se levanta la tapa y deja ver el rollo de papel y su brida.
 */
internal fun DrawScope.rolloDeco(v: Volumen, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(v.tono ?: SIN_ANIMO, k)
    val tapaAlto = h * 0.13f
    val sube = h * (0.015f + 0.075f * v.lleno)
    val cuerpo = Rect(w * 0.05f, tapaAlto + sube, w * 0.95f, h)
    // La sombra de la caja en el fondo del casillero, del lado contrario a la luz.
    drawRect(a.sombra(0.3f), Offset(if (izq) cuerpo.right - 1f * u else cuerpo.left - 3f * u, cuerpo.top + 4f * u), Size(4f * u, cuerpo.height - 4f * u))
    drawRect(tono, cuerpo.topLeft, cuerpo.size)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        // Los dos filetes impresos y el grano del papel.
        val oscuro = mezcla(tono, MACASAR_NEGRO, 0.35f)
        for (f in listOf(0.16f, 0.84f)) drawLine(oscuro.copy(alpha = 0.7f), Offset(cuerpo.left + cuerpo.width * f, cuerpo.top), Offset(cuerpo.left + cuerpo.width * f, cuerpo.bottom), 0.8f * u)
        drawLine(a.claro(0.25f), Offset(cuerpo.left, cuerpo.top + cuerpo.height * 0.5f + azar(v.semilla, 3) * cuerpo.height * 0.2f), Offset(cuerpo.right, cuerpo.top + cuerpo.height * 0.52f + azar(v.semilla, 4) * cuerpo.height * 0.2f), 0.6f * u)
        ladosDeco(cuerpo, a, k, izq, redondo = false)
        // El desgaste del canto de abajo.
        drawRect(a.sombra(0.25f), Offset(cuerpo.left, cuerpo.bottom - 3f * u), Size(cuerpo.width, 3f * u))
    }
    // El rollo que asoma bajo la tapa levantada: el papel crema y la brida oscura a cada lado.
    if (sube > 1f * u) {
        val hueco = Rect(w * 0.1f, tapaAlto, w * 0.9f, tapaAlto + sube)
        drawRect(a.luz(mezcla(MARFIL, ESTRAZA_VOL, 0.2f), k), hueco.topLeft, hueco.size)
        drawRect(a.luz(MACASAR_NEGRO, k), hueco.topLeft, Size(hueco.width * 0.12f, hueco.height))
        drawRect(a.luz(MACASAR_NEGRO, k), Offset(hueco.right - hueco.width * 0.12f, hueco.top), Size(hueco.width * 0.12f, hueco.height))
        drawRect(a.sombra(0.35f), hueco.topLeft, Size(hueco.width, minOf(hueco.height, 2f * u)))
    }
    // La tapa, más ancha y más oscura, con el filete de níquel y su sombra en el cuerpo.
    val tapa = Rect(0f, 0f, w, tapaAlto)
    drawRect(a.sombra(0.4f), Offset(cuerpo.left, tapa.bottom + sube), Size(cuerpo.width, 2.5f * u))
    drawRect(mezcla(tono, MACASAR_NEGRO, 0.42f), tapa.topLeft, tapa.size)
    drawRect(a.luz(NIQUEL, maxOf(k, 0.4f)), Offset(0f, tapa.bottom - tapa.height * 0.32f), Size(w, 1.1f * u))
    drawRect(a.claro(0.3f), tapa.topLeft, Size(w, 1.2f * u))
    ladosDeco(tapa, a, k, izq, redondo = false)
    // La etiqueta con el día, el mes a lo largo y el sello.
    val et = Rect(w * 0.14f, cuerpo.top + h * 0.05f, w * 0.86f, cuerpo.top + h * 0.05f + maxOf(w * 0.62f, 17f * u).coerceAtMost(h * 0.2f))
    etiquetaDeco(v, medidor, et, a, k)
    val sello = Offset(w * 0.5f, h - minOf(w * 0.3f, 9f * u) - 3f * u)
    mesDeco(v, medidor, Rect(w * 0.18f, et.bottom + 3f * u, w * 0.82f, sello.y - minOf(w * 0.24f, 7f * u) - 2f * u), tono, a)
    selloDeco(sello, minOf(w * 0.24f, 6.5f * u), v.noche, tono, a, k)
    return cuerpo
}

/**
 * El álbum de discos de tela: bajo y macizo, de lomo redondo, con el grano cruzado de la tela, dos pares de filetes de
 * níquel estampados, la etiqueta del día arriba, el mes a lo largo y, abajo, el disco estampado con sus surcos (en los Sueños,
 * la luna). Cuanto más escribiste, más fundas de estraza asoman por arriba.
 */
internal fun DrawScope.albumDeco(v: Volumen, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(v.tono ?: SIN_ANIMO, k)
    val cuerpo = Rect(0f, h * 0.06f, w, h)
    // Las fundas que asoman por arriba: una o más según lo escrito.
    val fundas = 1 + (v.lleno * 3.2f).toInt()
    for (j in 0 until fundas) {
        val x = w * (0.1f + 0.21f * j + 0.04f * azar(v.semilla, 10 + j))
        val alto = h * (0.035f + 0.03f * azar(v.semilla, 20 + j))
        drawRect(a.luz(mezcla(ESTRAZA_VOL, MARFIL, 0.25f * (j % 2)), k), Offset(x, cuerpo.top - alto), Size(w * 0.17f, alto + 2f * u))
        drawRect(a.sombra(0.25f), Offset(x + w * 0.15f, cuerpo.top - alto), Size(w * 0.02f, alto + 2f * u))
    }
    drawRect(a.sombra(0.3f), Offset(if (izq) cuerpo.right - 1f * u else cuerpo.left - 3f * u, cuerpo.top + 4f * u), Size(4f * u, cuerpo.height - 4f * u))
    val radio = CornerRadius(w * 0.14f, w * 0.1f)
    drawRoundRect(tono, cuerpo.topLeft, cuerpo.size, radio)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        // El grano cruzado de la tela, en un solo trazado.
        val tela = Path()
        val paso = maxOf(1f, 2.2f * u)
        var y = cuerpo.top
        while (y < cuerpo.bottom) { tela.moveTo(cuerpo.left, y); tela.lineTo(cuerpo.right, y + paso * 0.3f); y += paso }
        drawPath(tela, mezcla(tono, MACASAR_NEGRO, 0.4f).copy(alpha = 0.22f), style = Stroke(0.5f * u))
        // El lomo redondo: la luz en la curva que mira la lámpara o la escalera.
        ladosDeco(cuerpo, a, k, izq, redondo = true)
        // Las cofias, arriba y abajo, un poco más hondas.
        drawRect(a.sombra(0.22f), cuerpo.topLeft, Size(w, h * 0.035f))
        drawRect(a.sombra(0.28f), Offset(0f, cuerpo.bottom - h * 0.04f), Size(w, h * 0.04f))
        // Los filetes estampados en níquel.
        val metal = a.luz(NIQUEL, maxOf(k, 0.45f))
        for (yy in listOf(0.13f, 0.155f, 0.9f, 0.925f)) drawRect(metal, Offset(w * 0.08f, h * yy), Size(w * 0.84f, 0.9f * u))
    }
    val et = Rect(w * 0.12f, h * 0.2f, w * 0.88f, h * 0.2f + maxOf(w * 0.55f, 16f * u).coerceAtMost(h * 0.24f))
    etiquetaDeco(v, medidor, et, a, k)
    val rd = minOf(w * 0.4f, h * 0.15f)
    val cd = Offset(w * 0.5f, h * 0.88f - rd - 1f * u)
    // El mes, corto y de través bajo la etiqueta: el álbum es bajo y no le cabe a lo largo.
    val limpio = mesLimpio(v.mes)
    val corto = limpio.take(3).let { if (limpio.length > 3) "$it." else it }
    val tintaMes = if (claridad(tono) > 0.5f) a.tinta else mezcla(MARFIL, Color.White, 0.3f)
    textoEn(medidor, corto, Rect(w * 0.06f, et.bottom + 1f * u, w * 0.94f, minOf(cd.y - rd - 1f * u, et.bottom + 11f * u)), LETRA, tintaMes, 8f, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 5.5f)
    if (v.noche) selloDeco(cd, rd * 0.9f, true, tono, a, k)
    else {
        // El disco estampado: el negro del disco en un medallón de níquel, los surcos con su brillo y la etiqueta.
        val metal = a.luz(NIQUEL, maxOf(k, 0.45f))
        drawCircle(metal, rd, cd)
        drawCircle(a.luz(Color(0xFF17130F), maxOf(k, 0.5f)), rd * 0.86f, cd)
        drawCircle(metal.copy(alpha = 0.35f), rd * 0.66f, cd, style = Stroke(0.5f * u))
        drawArc(metal.copy(alpha = 0.7f), 200f, 70f, false, Offset(cd.x - rd * 0.72f, cd.y - rd * 0.72f), Size(rd * 1.44f, rd * 1.44f), style = Stroke(0.8f * u))
        drawCircle(a.luz(mezcla(tono, MARFIL, 0.5f), maxOf(k, 0.5f)), rd * 0.32f, cd)
        drawCircle(a.luz(Color(0xFF17130F), k), rd * 0.06f + 0.4f * u, cd)
    }
    return cuerpo
}

/**
 * La carpeta de partituras: blanda, de cartulina del color del ánimo, un poco ladeada y con el canto de arriba vencido; asoman
 * las hojas pautadas (más cuanto más escribiste) y la ata un cordón con su nudo. Lleva la etiqueta del día, el mes a lo largo
 * y, abajo, un sol naciente impreso en abanico (en los Sueños, la luna).
 */
internal fun DrawScope.carpetaDeco(v: Volumen, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(v.tono ?: SIN_ANIMO, k)
    val ladeo = w * 0.1f * (if (azar(v.semilla, 5) < 0.5f) 1f else -1f)
    val tope = h * 0.08f
    // Las hojas que asoman por arriba, desparejas.
    val hojas = 2 + (v.lleno * 4f).toInt()
    for (j in 0 until hojas) {
        val sube = h * (0.03f + 0.07f * azar(v.semilla, 30 + j))
        val x0 = w * (0.08f + 0.03f * j); val x1 = w * (0.92f - 0.025f * (hojas - j))
        drawRect(a.luz(mezcla(MARFIL, ESTRAZA_VOL, 0.08f * (j % 3)), k), Offset(x0 + ladeo, tope - sube), Size(x1 - x0, sube + 4f * u))
        drawLine(a.sombra(0.25f), Offset(x0 + ladeo, tope - sube), Offset(x1 + ladeo, tope - sube), 0.5f * u)
    }
    // La tapa blanda: el canto de arriba vencido, los costados apenas abombados y el pie derecho.
    val forma = Path().apply {
        moveTo(0f, h)
        cubicTo(-w * 0.02f, h * 0.6f, ladeo * 0.6f, h * 0.3f, ladeo, tope)
        cubicTo(w * 0.35f + ladeo, tope + h * 0.03f, w * 0.65f + ladeo, tope - h * 0.01f, w + ladeo, tope + h * 0.015f)
        cubicTo(w + ladeo * 0.6f, h * 0.3f, w * 1.02f, h * 0.6f, w, h)
        close()
    }
    drawPath(TrazoDeco.corrido(forma, if (izq) 2.5f * u else -2.5f * u, 2f * u), a.sombra(0.3f))
    drawPath(forma, tono)
    val cuerpo = Rect(0f, tope, w, h)
    clipPath(forma) {
        // El sol naciente impreso abajo, en abanico, del color del ánimo más hondo.
        val impreso = mezcla(tono, MACASAR_NEGRO, 0.28f)
        val c = Offset(w * 0.5f, h * 0.99f)
        for (j in 0 until 3) drawCircle(impreso.copy(alpha = 0.6f), w * (0.48f - 0.14f * j), c, style = Stroke(1.1f * u))
        // Las arrugas de la cartulina, que se doblan con el uso.
        for (j in 0 until 3) {
            val y = h * (0.3f + 0.2f * j + 0.06f * azar(v.semilla, 40 + j))
            drawLine(a.claro(0.3f), Offset(0f, y), Offset(w, y + (azar(v.semilla, 50 + j) - 0.5f) * 4f * u), 0.7f * u)
        }
        ladosDeco(Rect(0f, 0f, w, h), a, k, izq, redondo = false)
        drawRect(a.sombra(0.15f), Offset(0f, tope), Size(w, h * 0.04f))
    }
    // El cordón que la ata, con su nudo y las dos puntas.
    val yc = h * 0.6f
    val cordon = a.luz(Color(0xFF2A1E1A), k)
    drawLine(cordon, Offset(0f, yc), Offset(w, yc + 1f * u), 1.8f * u)
    drawCircle(cordon, 2.4f * u, Offset(w * 0.62f, yc + 0.6f * u))
    drawLine(cordon, Offset(w * 0.62f, yc), Offset(w * 0.55f, yc + h * 0.08f), 0.9f * u, StrokeCap.Round)
    drawLine(cordon, Offset(w * 0.62f, yc), Offset(w * 0.7f, yc + h * 0.07f), 0.9f * u, StrokeCap.Round)
    val et = Rect(w * 0.13f + ladeo * 0.5f, h * 0.14f, w * 0.87f + ladeo * 0.5f, h * 0.14f + maxOf(w * 0.58f, 16f * u).coerceAtMost(h * 0.2f))
    etiquetaDeco(v, medidor, et, a, k)
    mesDeco(v, medidor, Rect(w * 0.2f, et.bottom + 3f * u, w * 0.8f, yc - 3f * u), tono, a)
    selloDeco(Offset(w * 0.5f, yc + (h - yc) * 0.55f), minOf(w * 0.22f, 6f * u), v.noche, tono, a, k)
    return cuerpo
}
