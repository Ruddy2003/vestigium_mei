package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.LETRA
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteDeco.escalonDeco
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.NIQUEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.SIN_ANIMO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.asin

// =====================================================================
//  Art déco: las tres entradas
// =====================================================================

/** La estraza de las fundas que asoman del álbum y el cartón crudo que asoma en los cantos gastados. */
private val ESTRAZA_VOL = Color(0xFFB58D5E)
private val CARTON_CRUDO = Color(0xFFC9AE84)

/** Las escalas del ánimo de Art déco, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_DECO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.DECO, false) to juegoTonos(TemaId.DECO, true) }

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el ánimo neutro pasa del
 * crema a un casi negro, pensado para la interfaz), y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto
 * del cuarto: conserva su color de día y la luz lo apaga con la distancia al aplique. Se ubica el color en la escala de noche
 * (el tramo y la fracción que más se le acercan) y se toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaDeco(t: Color?, oscuro: Boolean): Color {
    if (t == null) return SIN_ANIMO
    if (!oscuro) return t
    val (dia, noche) = TONOS_DECO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor: Color = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = sq(t.red - a0.red - dr * f) + sq(t.green - a0.green - dg * f) + sq(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

private fun sq(x: Float) = x * x

/** Qué tan claro es un color (para escribir encima en tinta o en marfil). */
private fun claridad(c: Color): Float = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

/** El nombre del mes sin los adornos del rótulo («‹ Noviembre ›» queda en «Noviembre»), para que quepa en el volumen. */
private fun mesLimpio(mes: String): String = mes.filter { it.isLetterOrDigit() || it == ' ' }.trim().ifEmpty { mes.trim() }

/** La tinta que se lee sobre un fondo: la de escribir sobre lo claro, marfil sobre lo oscuro. */
private fun tintaSobre(fondo: Color, a: AireDeco): Color = if (claridad(fondo) > 0.5f) a.tinta else mezcla(MARFIL, Color.White, 0.3f)

/** El mes, a lo largo, en tinta o en marfil según lo claro del fondo, para que se lea sobre cualquier ánimo. */
private fun DrawScope.mesDeco(v: Volumen, medidor: TextMeasurer, r: Rect, fondo: Color, a: AireDeco) {
    textoEn(medidor, mesLimpio(v.mes), r, LETRA, tintaSobre(fondo, a), 8.5f, vertical = true, peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 5.5f)
}

/** La etiqueta de marfil con las esquinas en escalón y el día. */
private fun DrawScope.etiquetaDeco(v: Volumen, medidor: TextMeasurer, r: Rect, a: AireDeco, k: Float) {
    val u = density
    if (r.width <= 2f || r.height <= 2f) return
    val e = minOf(r.width, r.height) * 0.2f
    val forma = escalonDeco(r, e)
    drawPath(TrazoDeco.corrido(forma, 0.8f * u, 1f * u), a.sombra(0.35f))
    drawPath(forma, a.etiqueta(maxOf(k, 0.3f)))
    drawPath(forma, a.sombra(0.3f), style = Stroke(0.5f * u))
    textoEn(medidor, v.dia, Rect(r.left + 1.5f * u, r.top + 1f * u, r.right - 1.5f * u, r.bottom - 1f * u), LETRA, a.tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 6f)
}

/** El sello: de día, un sol de níquel con su corona de rayos; en los Sueños, una luna creciente. [fondo] es lo que tiene detrás. */
private fun DrawScope.selloDeco(c: Offset, r: Float, noche: Boolean, fondo: Color, a: AireDeco, k: Float) {
    val u = density
    if (r < 1.5f * u) return
    if (noche) {
        drawCircle(a.luz(Color(0xFFF1E7C4), maxOf(k, 0.6f)), r, c)
        drawCircle(fondo, r * 0.86f, Offset(c.x + r * 0.42f, c.y - r * 0.2f))
    } else {
        val metal = a.luz(mezcla(NIQUEL, MARFIL, 0.3f), maxOf(k, 0.45f))
        val vuelta = (2.0 * Math.PI * r * 0.8f).toFloat() / 10f
        drawCircle(metal, r * 0.8f, c, style = Stroke(r * 0.36f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(vuelta * 0.45f, vuelta * 0.55f))))
        drawCircle(metal, r * 0.45f, c)
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
}

/**
 * La caja del rollo de pianola: rígida, alta y derecha, de cartón forrado de papel del color del ánimo, con los cantos gastados
 * donde asoma el cartón crudo. La tapa monta sobre la caja (más ancha, más oscura, con su labio y su sombra) y en el cuerpo va
 * la etiqueta redonda de papel con el día; el mes, a lo largo; el sello, abajo. Cuanto más escribiste, más se levanta la tapa
 * y deja ver el rollo de papel y su brida. Devuelve el cuerpo.
 */
internal fun DrawScope.rolloDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val tapaAlto = h * 0.15f
    val sube = h * (0.015f + 0.07f * v.lleno)
    val cuerpo = Rect(w * 0.1f, tapaAlto + sube - 1f * u, w * 0.9f, h)
    drawRect(a.sombra(0.3f), Offset(if (izq) cuerpo.right - 1f * u else cuerpo.left - 3f * u, cuerpo.top + 4f * u), Size(4f * u, cuerpo.height - 4f * u))
    drawRect(tono, cuerpo.topLeft, cuerpo.size)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        // El cartón crudo en los cantos, donde el papel se gastó de sacar y meter la caja.
        val crudo = a.luz(CARTON_CRUDO, k)
        drawRect(crudo, Offset(cuerpo.left, cuerpo.top + cuerpo.height * (0.2f + 0.3f * azar(v.semilla, 2))), Size(1.2f * u, cuerpo.height * 0.25f))
        drawRect(crudo, Offset(cuerpo.right - 1.2f * u, cuerpo.bottom - cuerpo.height * 0.18f), Size(1.2f * u, cuerpo.height * 0.18f))
        drawRect(crudo.copy(alpha = 0.7f), Offset(cuerpo.left, cuerpo.bottom - 1.6f * u), Size(cuerpo.width, 1.6f * u))
        // Los dos filetes impresos del papel.
        val filete = mezcla(tono, MACASAR_NEGRO, 0.35f).copy(alpha = 0.7f)
        for (f in listOf(0.17f, 0.83f)) drawLine(filete, Offset(cuerpo.left + cuerpo.width * f, cuerpo.top), Offset(cuerpo.left + cuerpo.width * f, cuerpo.bottom), 0.8f * u)
        ladosDeco(cuerpo, a, k, izq, redondo = false)
    }
    // El rollo que asoma bajo la tapa levantada: el papel crema y la brida oscura a cada lado.
    if (sube > 1.5f * u) {
        val hueco = Rect(w * 0.14f, tapaAlto, w * 0.86f, tapaAlto + sube)
        drawRect(a.luz(mezcla(MARFIL, ESTRAZA_VOL, 0.2f), k), hueco.topLeft, hueco.size)
        drawRect(a.luz(MACASAR_NEGRO, k), hueco.topLeft, Size(hueco.width * 0.14f, hueco.height))
        drawRect(a.luz(MACASAR_NEGRO, k), Offset(hueco.right - hueco.width * 0.14f, hueco.top), Size(hueco.width * 0.14f, hueco.height))
    }
    // La tapa, más ancha y más oscura, que monta: su sombra en la caja, el labio claro y las esquinas gastadas.
    val tapa = Rect(0f, 0f, w, tapaAlto)
    drawRect(a.sombra(0.45f), Offset(cuerpo.left, tapa.bottom), Size(cuerpo.width, 2.5f * u))
    drawRect(mezcla(tono, MACASAR_NEGRO, 0.38f), tapa.topLeft, tapa.size)
    drawRect(a.claro(0.35f), Offset(0f, tapa.bottom - 1.2f * u), Size(w, 1.2f * u))
    drawRect(a.luz(CARTON_CRUDO, k).copy(alpha = 0.8f), Offset(0f, 0f), Size(w * 0.16f, 1.4f * u))
    ladosDeco(tapa, a, k, izq, redondo = false)
    // La etiqueta redonda de papel, con el día.
    val radio = minOf(w * 0.36f, h * 0.11f).coerceAtLeast(5f * u)
    val ce = Offset(w * 0.5f, cuerpo.top + 3f * u + radio)
    drawCircle(a.sombra(0.35f), radio, Offset(ce.x + 0.8f * u, ce.y + 1f * u))
    drawCircle(a.etiqueta(maxOf(k, 0.3f)), radio, ce)
    drawCircle(mezcla(tono, MACASAR_NEGRO, 0.3f), radio * 0.84f, ce, style = Stroke(0.7f * u))
    textoEn(medidor, v.dia, Rect(ce.x - radio * 0.8f, ce.y - radio * 0.7f, ce.x + radio * 0.8f, ce.y + radio * 0.7f), LETRA, a.tinta, 12f, peso = FontWeight.SemiBold, minimo = 6f)
    val sello = Offset(w * 0.5f, h - minOf(w * 0.3f, 9f * u) - 3f * u)
    mesDeco(v, medidor, Rect(w * 0.22f, ce.y + radio + 3f * u, w * 0.78f, sello.y - minOf(w * 0.24f, 7f * u) - 2f * u), tono, a)
    selloDeco(sello, minOf(w * 0.22f, 6f * u), v.noche, tono, a, k)
    return cuerpo
}

/**
 * El álbum de discos de tela: bajo y macizo, de lomo redondo, con la trama cruzada de la tela, las cofias, los filetes de níquel
 * estampados, la etiqueta del día arriba, el mes corto y, abajo, el disco negro en su medallón de níquel (en los Sueños, la
 * luna). Cuanto más escribiste, más fundas de estraza asoman por arriba. Devuelve el cuerpo.
 */
internal fun DrawScope.albumDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val cuerpo = Rect(0f, h * 0.07f, w, h)
    // Las fundas que asoman por arriba: una o más según lo escrito.
    val fundas = 1 + (v.lleno * 3.2f).toInt()
    for (j in 0 until fundas) {
        val x = w * (0.1f + 0.21f * j + 0.04f * azar(v.semilla, 10 + j))
        val alto = h * (0.04f + 0.035f * azar(v.semilla, 20 + j))
        drawRect(a.luz(mezcla(ESTRAZA_VOL, MARFIL, 0.25f * (j % 2)), k), Offset(x, cuerpo.top - alto), Size(w * 0.17f, alto + 2f * u))
    }
    drawRect(a.sombra(0.3f), Offset(if (izq) cuerpo.right - 1f * u else cuerpo.left - 3f * u, cuerpo.top + 4f * u), Size(4f * u, cuerpo.height - 4f * u))
    drawRoundRect(tono, cuerpo.topLeft, cuerpo.size, CornerRadius(w * 0.14f, w * 0.1f))
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        // La trama de la tela: hilos de través y a lo alto, cada juego en un trazado.
        val trama = Path(); val urdimbre = Path()
        val paso = maxOf(1f, 1.8f * u)
        var y = cuerpo.top
        while (y < cuerpo.bottom) { trama.moveTo(cuerpo.left, y); trama.lineTo(cuerpo.right, y); y += paso }
        var x = cuerpo.left
        while (x < cuerpo.right) { urdimbre.moveTo(x, cuerpo.top); urdimbre.lineTo(x, cuerpo.bottom); x += paso }
        val hilo = mezcla(tono, MACASAR_NEGRO, 0.45f)
        drawPath(trama, hilo.copy(alpha = 0.22f), style = Stroke(0.5f * u))
        drawPath(urdimbre, a.claro(0.4f).copy(alpha = if (a.oscuro) 0.04f else 0.14f), style = Stroke(0.4f * u))
        // El lomo redondo y las cofias, un poco más hondas.
        ladosDeco(cuerpo, a, k, izq, redondo = true)
        drawRect(a.sombra(0.25f), cuerpo.topLeft, Size(w, h * 0.04f))
        drawRect(a.sombra(0.3f), Offset(0f, cuerpo.bottom - h * 0.045f), Size(w, h * 0.045f))
        // Los filetes estampados en níquel, en un trazado.
        val filetes = Path()
        for (yy in listOf(0.14f, 0.165f, 0.9f, 0.925f)) filetes.addRect(Rect(w * 0.08f, h * yy, w * 0.92f, h * yy + 0.9f * u))
        drawPath(filetes, a.luz(NIQUEL, maxOf(k, 0.45f)))
    }
    val et = Rect(w * 0.12f, h * 0.21f, w * 0.88f, h * 0.21f + maxOf(w * 0.55f, 16f * u).coerceAtMost(h * 0.24f))
    etiquetaDeco(v, medidor, et, a, k)
    val rd = minOf(w * 0.4f, h * 0.15f)
    val cd = Offset(w * 0.5f, h * 0.88f - rd - 1f * u)
    // El mes, corto y de través bajo la etiqueta: el álbum es bajo y no le cabe a lo largo.
    val limpio = mesLimpio(v.mes)
    val corto = limpio.take(3).let { if (limpio.length > 3) "$it." else it }
    textoEn(medidor, corto, Rect(w * 0.06f, et.bottom + 1f * u, w * 0.94f, minOf(cd.y - rd - 1f * u, et.bottom + 11f * u)), LETRA, tintaSobre(tono, a), 8f, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 5.5f)
    if (v.noche) selloDeco(cd, rd * 0.9f, true, tono, a, k)
    else {
        // El disco estampado: el negro del disco en un medallón de níquel, los surcos con su brillo y la etiqueta.
        val metal = a.luz(NIQUEL, maxOf(k, 0.45f))
        drawCircle(metal, rd, cd)
        drawCircle(a.luz(Color(0xFF17130F), maxOf(k, 0.5f)), rd * 0.86f, cd)
        drawArc(metal.copy(alpha = 0.7f), 200f, 70f, false, Offset(cd.x - rd * 0.66f, cd.y - rd * 0.66f), Size(rd * 1.32f, rd * 1.32f), style = Stroke(0.8f * u))
        drawCircle(a.luz(mezcla(base, MARFIL, 0.5f), maxOf(k, 0.5f)), rd * 0.32f, cd)
    }
    return cuerpo
}

/**
 * La carpeta de partituras: blanda y panzona, más baja que la caja, de cartulina del color del ánimo con su fibra y un sol
 * naciente impreso; el canto de arriba vencido, las hojas pautadas que asoman desparejas (más cuanto más escribiste) y el
 * cordón que le da la vuelta, con su nudo. Si la semilla lo dice, va ladeada, apoyada en la vecina de la izquierda. Devuelve el
 * cuerpo.
 */
internal fun DrawScope.carpetaDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val tope = h * 0.1f
    val ladeada = azar(v.semilla, 5) < 0.45f
    val x0 = if (ladeada) w * 0.14f else w * 0.05f
    val x1 = w * 0.96f
    val grados = if (ladeada) -Math.toDegrees(asin(((x0 + 2f * u) / maxOf(1f, h - tope)).coerceIn(0f, 0.3f).toDouble())).toFloat() else 0f
    rotate(grados, Offset(x1, h)) {
        // Las hojas que asoman por arriba, desparejas, con su canto.
        val hojas = 2 + (v.lleno * 4f).toInt()
        val cantos = Path()
        for (j in 0 until hojas) {
            val sube = h * (0.03f + 0.07f * azar(v.semilla, 30 + j))
            val ha = x0 + (x1 - x0) * (0.04f + 0.04f * j); val hb = x1 - (x1 - x0) * (0.03f * (hojas - j))
            drawRect(a.luz(mezcla(MARFIL, ESTRAZA_VOL, 0.1f * (j % 3)), k), Offset(ha, tope - sube), Size(hb - ha, sube + 4f * u))
            cantos.moveTo(ha, tope - sube); cantos.lineTo(hb, tope - sube + (azar(v.semilla, 60 + j) - 0.5f) * 2f * u)
        }
        drawPath(cantos, a.sombra(0.3f), style = Stroke(0.6f * u))
        // La tapa blanda y panzona: los costados abombados, el canto de arriba vencido y el pie derecho.
        val forma = Path().apply {
            moveTo(x0, h)
            cubicTo(x0 - w * 0.07f, h * 0.72f, x0 - w * 0.06f, h * 0.36f, x0 + w * 0.01f, tope)
            cubicTo(x0 + (x1 - x0) * 0.35f, tope + h * 0.04f, x0 + (x1 - x0) * 0.65f, tope - h * 0.012f, x1 - w * 0.01f, tope + h * 0.02f)
            cubicTo(x1 + w * 0.05f, h * 0.36f, x1 + w * 0.06f, h * 0.72f, x1, h)
            close()
        }
        drawPath(TrazoDeco.corrido(forma, if (izq) 2.5f * u else -2.5f * u, 2f * u), a.sombra(0.3f))
        drawPath(forma, tono)
        clipPath(forma) {
            // La fibra de la cartulina y el sol naciente impreso abajo.
            val fibra = Path()
            for (j in 0 until 14) {
                val fx = x0 + (x1 - x0) * azar(v.semilla, 70 + j); val fy = tope + (h - tope) * azar(v.semilla, 90 + j)
                fibra.moveTo(fx, fy); fibra.lineTo(fx + (azar(v.semilla, 110 + j) - 0.5f) * 5f * u, fy + (azar(v.semilla, 130 + j) - 0.5f) * 3f * u)
            }
            drawPath(fibra, mezcla(tono, MACASAR_NEGRO, 0.4f).copy(alpha = 0.35f), style = Stroke(0.5f * u))
            val impreso = mezcla(tono, MACASAR_NEGRO, 0.28f).copy(alpha = 0.6f)
            val c = Offset((x0 + x1) / 2, h * 0.99f)
            drawCircle(impreso, (x1 - x0) * 0.46f, c, style = Stroke(1.1f * u))
            drawCircle(impreso, (x1 - x0) * 0.28f, c, style = Stroke(1.1f * u))
            ladosDeco(Rect(x0 - w * 0.07f, 0f, x1 + w * 0.06f, h), a, k, izq, redondo = true)
            drawRect(a.sombra(0.18f), Offset(0f, tope), Size(w, h * 0.04f))
        }
        // El cordón: cruza el frente, da la vuelta por los costados (se ve el arco) y se anuda con dos puntas.
        val yc = h * 0.6f
        val cordon = a.luz(Color(0xFF2A1E1A), k)
        drawLine(cordon, Offset(x0 - w * 0.06f, yc), Offset(x1 + w * 0.05f, yc + 1f * u), 1.8f * u)
        drawArc(cordon, 90f, 180f, false, Offset(x0 - w * 0.1f, yc - 2f * u), Size(w * 0.08f, 4f * u), style = Stroke(1.2f * u))
        drawCircle(cordon, 2.4f * u, Offset(x0 + (x1 - x0) * 0.62f, yc + 0.6f * u))
        val nudo = Offset(x0 + (x1 - x0) * 0.62f, yc)
        drawLine(cordon, nudo, Offset(nudo.x - w * 0.08f, yc + h * 0.09f), 1f * u, StrokeCap.Round)
        drawLine(cordon, nudo, Offset(nudo.x + w * 0.1f, yc + h * 0.07f), 1f * u, StrokeCap.Round)
        val et = Rect(x0 + (x1 - x0) * 0.1f, h * 0.16f, x1 - (x1 - x0) * 0.1f, h * 0.16f + maxOf((x1 - x0) * 0.58f, 16f * u).coerceAtMost(h * 0.22f))
        etiquetaDeco(v, medidor, et, a, k)
        mesDeco(v, medidor, Rect(x0 + (x1 - x0) * 0.18f, et.bottom + 3f * u, x1 - (x1 - x0) * 0.18f, yc - 3f * u), tono, a)
        selloDeco(Offset((x0 + x1) / 2, yc + (h - yc) * 0.55f), minOf((x1 - x0) * 0.22f, 6f * u), v.noche, tono, a, k)
    }
    return Rect(x0, tope, x1, h)
}
