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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.ESTRAZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MACASAR_NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.MARFIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.SIN_ANIMO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDeco.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.asin

// =====================================================================
//  Art déco: las tres entradas
// =====================================================================

/** El cartón crudo que asoma en los cantos gastados. */
private val CARTON = Color(0xFFCDB389)

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

/**
 * El ánimo con cuerpo: la escala del tema, que en la interfaz va lavada, sube de saturación para que el volumen sea un objeto
 * de color (cartón teñido, tela, cartulina) y no un pastel sobre gris; el crema muy claro toma un poco de cuerpo de papel.
 * Es el mismo orden de colores, solo más lleno. Lo que no tiene ánimo queda en su gris piedra.
 */
internal fun cuerpoDeco(t: Color, sinAnimo: Boolean): Color {
    if (sinAnimo) return t
    val l = 0.3f * t.red + 0.59f * t.green + 0.11f * t.blue
    fun f(c: Float) = (l + (c - l) * 1.5f).coerceIn(0f, 1f)
    val s = Color(f(t.red), f(t.green), f(t.blue), t.alpha)
    return if (l > 0.78f) mezcla(s, Color(0xFFCDB07A), 0.2f) else s
}

/** Qué tan claro es un color (para escribir encima en tinta o en marfil). */
private fun claridad(c: Color): Float = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue

/** El nombre del mes sin los adornos del rótulo («‹ Noviembre ›» queda en «Noviembre»), para que quepa en el volumen. */
private fun mesLimpio(mes: String): String = mes.filter { it.isLetterOrDigit() || it == ' ' }.trim().ifEmpty { mes.trim() }

/** La tinta que se lee sobre un fondo: la de escribir sobre lo claro, marfil sobre lo oscuro. */
private fun tintaSobre(fondo: Color): Color = if (claridad(fondo) > 0.5f) TINTA else mezcla(MARFIL, Color.White, 0.3f)

/** El mes, a lo largo, en tinta o en marfil según lo claro del fondo, para que se lea sobre cualquier ánimo. */
private fun DrawScope.mesDeco(v: Volumen, medidor: TextMeasurer, r: Rect, fondo: Color) {
    textoEn(medidor, mesLimpio(v.mes), r, LETRA, tintaSobre(fondo), 8.5f, vertical = true, peso = FontWeight.SemiBold, espaciado = 1.2f, minimo = 5.5f)
}

/** La etiqueta de marfil con las esquinas en escalón, su doble filete y el día. */
private fun DrawScope.etiquetaDeco(v: Volumen, medidor: TextMeasurer, r: Rect, a: AireDeco, k: Float) {
    val u = density
    if (r.width <= 2f || r.height <= 2f) return
    val e = minOf(r.width, r.height) * 0.2f
    val forma = TrazoDeco.escalon(r, e)
    drawPath(TrazoDeco.corrido(forma, 0.9f * u, 1.2f * u), a.s(0.4f))
    drawPath(forma, a.etiqueta(maxOf(k, 0.3f)))
    drawPath(TrazoDeco.escalon(Rect(r.left + 1.6f * u, r.top + 1.6f * u, r.right - 1.6f * u, r.bottom - 1.6f * u), e * 0.6f), TINTA.copy(alpha = 0.45f), style = Stroke(0.5f * u))
    drawRect(a.b(0.4f, k), Offset(r.left + e, r.top), Size(r.width - 2 * e, 0.8f * u))
    textoEn(medidor, v.dia, Rect(r.left + 2f * u, r.top + 1.5f * u, r.right - 2f * u, r.bottom - 1.5f * u), LETRA, TINTA, 12.5f, peso = FontWeight.SemiBold, minimo = 6f)
}

/** El sello: de día, un sol de latón con su corona de rayos; en los Sueños, una luna creciente. [fondo] es lo que tiene detrás. */
private fun DrawScope.selloDeco(c: Offset, r: Float, noche: Boolean, fondo: Color, a: AireDeco, k: Float) {
    val u = density
    if (r < 1.5f * u) return
    if (noche) {
        drawCircle(a.c(Color(0xFFF1E7C4), maxOf(k, 0.7f)), r, c)
        drawCircle(fondo, r * 0.86f, Offset(c.x + r * 0.42f, c.y - r * 0.2f))
    } else {
        val metal = a.c(LATON, maxOf(k, 0.45f))
        val vuelta = (2.0 * Math.PI * r * 0.8f).toFloat() / 10f
        drawCircle(metal, r * 0.8f, c, style = Stroke(r * 0.36f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(vuelta * 0.45f, vuelta * 0.55f))))
        drawCircle(metal, r * 0.45f, c)
        drawCircle(a.b(0.5f, k), r * 0.18f, Offset(c.x - r * 0.15f, c.y - r * 0.15f))
    }
}

/** Una cantonera de latón en la esquina ([x], [y]) de un cuerpo: [dx] y [dy] dicen hacia dónde abre. */
private fun DrawScope.cantoneraDeco(a: AireDeco, x: Float, y: Float, lado: Float, dx: Float, dy: Float, k: Float) {
    val p = TrazoDeco.poligono(x, y, x + dx * lado, y, x, y + dy * lado)
    drawPath(p, a.c(LATON, k))
    drawLine(a.c(LATON_LUZ, k), Offset(x, y), Offset(x + dx * lado * 0.8f, y), 0.8f * density)
    drawLine(a.c(LATON_OSC, k), Offset(x + dx * lado, y), Offset(x, y + dy * lado), 0.8f * density)
}

/**
 * La caja del rollo de pianola: rígida, alta y derecha, de cartón entelado del color del ánimo con su trama, los cantos
 * gastados donde asoma el cartón crudo y dos filetes impresos. La tapa monta sobre la caja (más ancha, de tela más oscura,
 * con su labio, su sombra y cantoneras de latón) y en el cuerpo va la etiqueta redonda de papel con el día; el mes, a lo
 * largo; el sello, abajo. Cuanto más escribiste, más se levanta la tapa y deja ver el rollo de papel y su brida. Devuelve el
 * cuerpo.
 */
internal fun DrawScope.rolloDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val tapaAlto = h * 0.15f
    val sube = h * (0.012f + 0.07f * v.lleno)
    val cuerpo = Rect(w * 0.07f, tapaAlto + sube - 1f * u, w * 0.93f, h)
    contactoDeco(a, Offset(w * 0.5f + (if (izq) 2f else -2f) * u, h - 1f * u), w * 1.05f, 4f * u, 0.45f)
    drawRect(tono, cuerpo.topLeft, cuerpo.size)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        val filete = mezcla(tono, MACASAR_NEGRO, 0.4f).copy(alpha = 0.75f)
        for (f in listOf(0.18f, 0.82f)) drawLine(filete, Offset(cuerpo.left + cuerpo.width * f, cuerpo.top), Offset(cuerpo.left + cuerpo.width * f, cuerpo.bottom), 0.9f * u)
        carasDeco(a, cuerpo, k, izq, 1.1f)
        // El cartón crudo en los cantos, donde la tela se gastó de sacar y meter la caja.
        val crudo = a.c(CARTON, k)
        drawRect(crudo, Offset(cuerpo.left, cuerpo.top + cuerpo.height * (0.25f + 0.3f * azar(v.semilla, 2))), Size(1.3f * u, cuerpo.height * 0.22f))
        drawRect(crudo, Offset(cuerpo.right - 1.3f * u, cuerpo.bottom - cuerpo.height * 0.2f), Size(1.3f * u, cuerpo.height * 0.2f))
        drawRect(crudo.copy(alpha = 0.8f), Offset(cuerpo.left, cuerpo.bottom - 1.6f * u), Size(cuerpo.width, 1.6f * u))
    }
    cantoneraDeco(a, cuerpo.left, cuerpo.bottom, w * 0.22f, 1f, -1f, k)
    cantoneraDeco(a, cuerpo.right, cuerpo.bottom, w * 0.22f, -1f, -1f, k)
    // El rollo que asoma bajo la tapa levantada: el papel crema enrollado y la brida oscura a cada lado.
    if (sube > 1.5f * u) {
        val hueco = Rect(w * 0.12f, tapaAlto, w * 0.88f, tapaAlto + sube)
        drawRect(a.c(mezcla(MARFIL, ESTRAZA, 0.15f), k), hueco.topLeft, hueco.size)
        drawRect(a.c(MACASAR_NEGRO, k), hueco.topLeft, Size(hueco.width * 0.13f, hueco.height))
        drawRect(a.c(MACASAR_NEGRO, k), Offset(hueco.right - hueco.width * 0.13f, hueco.top), Size(hueco.width * 0.13f, hueco.height))
        drawRect(a.s(0.3f), Offset(hueco.left, hueco.bottom - hueco.height * 0.4f), Size(hueco.width, hueco.height * 0.4f))
    }
    // La tapa: más ancha, de tela más oscura, con su labio claro, su sombra en la caja y las cantoneras.
    val tapa = Rect(0f, 0f, w, tapaAlto)
    drawRect(a.s(0.5f), Offset(cuerpo.left, tapa.bottom), Size(cuerpo.width, 3f * u))
    drawRect(mezcla(tono, MACASAR_NEGRO, 0.28f), tapa.topLeft, tapa.size)
    carasDeco(a, tapa, k, izq, 1.1f)
    drawRect(a.b(0.5f, k), Offset(0f, tapa.bottom - 1.4f * u), Size(w, 1.4f * u))
    cantoneraDeco(a, 0f, 0f, w * 0.24f, 1f, 1f, k)
    cantoneraDeco(a, w, 0f, w * 0.24f, -1f, 1f, k)
    // La etiqueta redonda de papel, con el día.
    val radio = minOf(w * 0.38f, h * 0.12f).coerceAtLeast(5f * u)
    val ce = Offset(w * 0.5f, cuerpo.top + 4f * u + radio)
    drawCircle(a.s(0.4f), radio, Offset(ce.x + 1f * u, ce.y + 1.4f * u))
    drawCircle(a.etiqueta(maxOf(k, 0.3f)), radio, ce)
    drawCircle(TINTA.copy(alpha = 0.5f), radio * 0.86f, ce, style = Stroke(0.6f * u))
    drawCircle(a.c(LATON, maxOf(k, 0.4f)), radio, ce, style = Stroke(1f * u))
    drawArc(a.b(0.45f, k), 200f, 60f, false, Offset(ce.x - radio, ce.y - radio), Size(radio * 2, radio * 2), style = Stroke(0.8f * u))
    textoEn(medidor, v.dia, Rect(ce.x - radio * 0.78f, ce.y - radio * 0.68f, ce.x + radio * 0.78f, ce.y + radio * 0.68f), LETRA, TINTA, 12.5f, peso = FontWeight.SemiBold, minimo = 6f)
    val sello = Offset(w * 0.5f, h - minOf(w * 0.3f, 9f * u) - 4f * u)
    mesDeco(v, medidor, Rect(w * 0.24f, ce.y + radio + 3f * u, w * 0.76f, sello.y - minOf(w * 0.24f, 7f * u) - 2f * u), tono)
    selloDeco(sello, minOf(w * 0.22f, 6.5f * u), v.noche, tono, a, k)
    return cuerpo
}

/**
 * El álbum de discos de tela: bajo y macizo, de lomo redondo, con la trama cruzada de la tela, las cofias, los filetes de latón
 * estampados, la etiqueta del día arriba, el mes corto y, abajo, el disco negro en su medallón de latón (en los Sueños, la
 * luna). Cuanto más escribiste, más fundas de estraza asoman por arriba. Devuelve el cuerpo.
 */
internal fun DrawScope.albumDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val cuerpo = Rect(0f, h * 0.06f, w, h)
    // Las fundas que asoman por arriba: una o más según lo escrito.
    val fundas = 1 + (v.lleno * 3.2f).toInt()
    for (j in 0 until fundas) {
        val x = w * (0.1f + 0.21f * j + 0.04f * azar(v.semilla, 10 + j))
        val alto = h * (0.04f + 0.035f * azar(v.semilla, 20 + j))
        drawRect(a.c(mezcla(ESTRAZA, MARFIL, 0.25f * (j % 2)), k), Offset(x, cuerpo.top - alto), Size(w * 0.17f, alto + 2f * u))
        drawRect(a.b(0.4f, k), Offset(x, cuerpo.top - alto), Size(w * 0.17f, 0.8f * u))
    }
    contactoDeco(a, Offset(w * 0.5f + (if (izq) 2f else -2f) * u, h - 1f * u), w * 1.05f, 4f * u, 0.45f)
    drawRoundRect(tono, cuerpo.topLeft, cuerpo.size, CornerRadius(w * 0.16f, w * 0.12f))
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        // La trama de la tela: hilos de través y a lo alto, cada juego en un trazado.
        val trama = Path(); val urdimbre = Path()
        val paso = maxOf(1f, 1.8f * u)
        var y = cuerpo.top
        while (y < cuerpo.bottom) { trama.moveTo(cuerpo.left, y); trama.lineTo(cuerpo.right, y); y += paso }
        var x = cuerpo.left
        while (x < cuerpo.right) { urdimbre.moveTo(x, cuerpo.top); urdimbre.lineTo(x, cuerpo.bottom); x += paso }
        drawPath(trama, mezcla(tono, MACASAR_NEGRO, 0.5f).copy(alpha = 0.25f), style = Stroke(0.5f * u))
        drawPath(urdimbre, a.b(0.4f, k).copy(alpha = if (a.oscuro) 0.06f * k else 0.14f), style = Stroke(0.4f * u))
        // El lomo redondo y las cofias, un poco más hondas y gastadas.
        cilindroDeco(a, cuerpo, k, izq, 1.2f)
        drawRect(a.s(0.3f), cuerpo.topLeft, Size(w, h * 0.045f))
        drawRect(a.s(0.34f), Offset(0f, cuerpo.bottom - h * 0.05f), Size(w, h * 0.05f))
        drawRect(a.b(0.3f, k), Offset(0f, cuerpo.top + h * 0.045f), Size(w, 0.8f * u))
        // Los filetes estampados en latón, en un trazado.
        val filetes = Path()
        for (yy in listOf(0.13f, 0.155f, 0.9f, 0.925f)) filetes.addRect(Rect(w * 0.08f, h * yy, w * 0.92f, h * yy + 1f * u))
        drawPath(filetes, a.c(LATON, maxOf(k, 0.45f)))
    }
    val et = Rect(w * 0.12f, h * 0.2f, w * 0.88f, h * 0.2f + maxOf(w * 0.55f, 17f * u).coerceAtMost(h * 0.24f))
    etiquetaDeco(v, medidor, et, a, k)
    val rd = minOf(w * 0.4f, h * 0.15f)
    val cd = Offset(w * 0.5f, h * 0.87f - rd - 1f * u)
    // El mes, corto y de través bajo la etiqueta: el álbum es bajo y no le cabe a lo largo.
    val limpio = mesLimpio(v.mes)
    val corto = limpio.take(3).let { if (limpio.length > 3) "$it." else it }
    textoEn(medidor, corto, Rect(w * 0.06f, et.bottom + 1f * u, w * 0.94f, minOf(cd.y - rd - 1f * u, et.bottom + 11f * u)), LETRA, tintaSobre(tono), 8f, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 5.5f)
    if (v.noche) selloDeco(cd, rd * 0.9f, true, tono, a, k)
    else {
        // El disco estampado: el negro del disco en un medallón de latón, los surcos con su brillo y la etiqueta.
        val metal = a.c(LATON, maxOf(k, 0.45f))
        drawCircle(a.s(0.4f), rd, Offset(cd.x + 1f * u, cd.y + 1.2f * u))
        drawCircle(metal, rd, cd)
        drawCircle(a.c(Color(0xFF17130F), maxOf(k, 0.5f)), rd * 0.84f, cd)
        drawArc(a.b(0.6f, maxOf(k, 0.5f)), 200f, 70f, false, Offset(cd.x - rd * 0.66f, cd.y - rd * 0.66f), Size(rd * 1.32f, rd * 1.32f), style = Stroke(0.9f * u))
        drawCircle(a.b(0.15f, maxOf(k, 0.5f)), rd * 0.5f, cd, style = Stroke(0.5f * u))
        drawCircle(a.c(mezcla(base, MARFIL, 0.4f), maxOf(k, 0.5f)), rd * 0.3f, cd)
        drawArc(a.c(LATON_LUZ, maxOf(k, 0.45f)), 200f, 60f, false, Offset(cd.x - rd, cd.y - rd), Size(rd * 2, rd * 2), style = Stroke(0.9f * u))
    }
    return cuerpo
}

/**
 * La carpeta de partituras: blanda y panzona, de cartulina del color del ánimo con el lomo de tela, su fibra y un sol naciente
 * impreso; el canto de arriba vencido, las hojas pautadas que asoman desparejas (más cuanto más escribiste) y el cordón grueso
 * que le da la vuelta, con su nudo y dos puntas. Si la semilla lo dice, va ladeada, apoyada en la vecina de la izquierda.
 * Devuelve el cuerpo.
 */
internal fun DrawScope.carpetaDeco(v: Volumen, base: Color, medidor: TextMeasurer, a: AireDeco, k: Float, izq: Boolean): Rect {
    val u = density
    val w = size.width; val h = size.height
    val tono = a.animo(base, k)
    val tope = h * 0.1f
    val ladeada = azar(v.semilla, 5) < 0.45f
    val x0 = if (ladeada) w * 0.14f else w * 0.04f
    val x1 = w * 0.97f
    contactoDeco(a, Offset(w * 0.55f, h - 1f * u), w * 1.05f, 4f * u, 0.45f)
    val grados = if (ladeada) -Math.toDegrees(asin(((x0 + 2f * u) / maxOf(1f, h - tope)).coerceIn(0f, 0.3f).toDouble())).toFloat() else 0f
    rotate(grados, Offset(x1, h)) {
        // Las hojas que asoman por arriba, desparejas, con su pauta y su canto.
        val hojas = 2 + (v.lleno * 4f).toInt()
        val cantos = Path(); val pauta = Path()
        for (j in 0 until hojas) {
            val sube = h * (0.03f + 0.07f * azar(v.semilla, 30 + j))
            val ha = x0 + (x1 - x0) * (0.04f + 0.04f * j); val hb = x1 - (x1 - x0) * (0.03f * (hojas - j))
            drawRect(a.c(mezcla(MARFIL, ESTRAZA, 0.1f * (j % 3)), k), Offset(ha, tope - sube), Size(hb - ha, sube + 4f * u))
            cantos.moveTo(ha, tope - sube); cantos.lineTo(hb, tope - sube + (azar(v.semilla, 60 + j) - 0.5f) * 2f * u)
            pauta.moveTo(ha + 1f * u, tope - sube * 0.45f); pauta.lineTo(hb - 1f * u, tope - sube * 0.45f)
        }
        drawPath(cantos, a.s(0.35f), style = Stroke(0.7f * u))
        drawPath(pauta, TINTA.copy(alpha = 0.3f), style = Stroke(0.5f * u))
        // La tapa blanda y panzona: los costados abombados, el canto de arriba vencido y el pie derecho.
        val forma = Path().apply {
            moveTo(x0, h)
            cubicTo(x0 - w * 0.07f, h * 0.72f, x0 - w * 0.06f, h * 0.36f, x0 + w * 0.01f, tope)
            cubicTo(x0 + (x1 - x0) * 0.35f, tope + h * 0.04f, x0 + (x1 - x0) * 0.65f, tope - h * 0.012f, x1 - w * 0.01f, tope + h * 0.02f)
            cubicTo(x1 + w * 0.05f, h * 0.36f, x1 + w * 0.06f, h * 0.72f, x1, h)
            close()
        }
        drawPath(TrazoDeco.corrido(forma, if (izq) 2.5f * u else -2.5f * u, 2f * u), a.s(0.35f))
        drawPath(forma, tono)
        clipPath(forma) {
            // La fibra de la cartulina, el lomo de tela, el sol naciente impreso abajo y la luz del cuerpo blando.
            val fibra = Path()
            for (j in 0 until 16) {
                val fx = x0 + (x1 - x0) * azar(v.semilla, 70 + j); val fy = tope + (h - tope) * azar(v.semilla, 90 + j)
                fibra.moveTo(fx, fy); fibra.lineTo(fx + (azar(v.semilla, 110 + j) - 0.5f) * 5f * u, fy + (azar(v.semilla, 130 + j) - 0.5f) * 3f * u)
            }
            drawPath(fibra, mezcla(tono, MACASAR_NEGRO, 0.4f).copy(alpha = 0.35f), style = Stroke(0.5f * u))
            val lomo = mezcla(tono, MACASAR_NEGRO, 0.28f)
            drawRect(lomo, Offset(x0 - w * 0.07f, tope), Size((x1 - x0) * 0.2f + w * 0.07f, h))
            val impreso = mezcla(tono, MACASAR_NEGRO, 0.3f).copy(alpha = 0.7f)
            val c = Offset((x0 + x1) / 2 + (x1 - x0) * 0.08f, h * 0.99f)
            drawCircle(impreso, (x1 - x0) * 0.44f, c, style = Stroke(1.2f * u))
            drawCircle(impreso, (x1 - x0) * 0.27f, c, style = Stroke(1.2f * u))
            cilindroDeco(a, Rect(x0 - w * 0.07f, 0f, x1 + w * 0.06f, h), k, izq, 0.9f)
            drawRect(a.s(0.2f), Offset(0f, tope), Size(w, h * 0.04f))
            drawRect(a.b(0.4f, k), Offset(0f, tope + h * 0.005f), Size(w, 0.8f * u))
        }
        // El cordón: grueso, con su luz; cruza el frente, da la vuelta por los costados y se anuda con dos puntas.
        val yc = h * 0.6f
        val cordon = a.c(Color(0xFF2A1E1A), k)
        val luzCordon = a.b(0.45f, k)
        drawLine(cordon, Offset(x0 - w * 0.06f, yc), Offset(x1 + w * 0.05f, yc + 1f * u), 2.2f * u, StrokeCap.Round)
        drawLine(luzCordon, Offset(x0 - w * 0.06f, yc - 0.7f * u), Offset(x1 + w * 0.05f, yc + 0.3f * u), 0.6f * u)
        drawArc(cordon, 90f, 180f, false, Offset(x0 - w * 0.1f, yc - 2.4f * u), Size(w * 0.08f, 4.8f * u), style = Stroke(1.6f * u))
        val nudo = Offset(x0 + (x1 - x0) * 0.62f, yc + 0.5f * u)
        drawOval(cordon, Offset(nudo.x - 3.2f * u, nudo.y - 2.4f * u), Size(6.4f * u, 4.8f * u))
        drawOval(cordon, Offset(nudo.x - 7f * u, nudo.y - 4.2f * u), Size(5f * u, 3.6f * u), style = Stroke(1.4f * u))
        drawOval(luzCordon, Offset(nudo.x - 2f * u, nudo.y - 1.8f * u), Size(2.4f * u, 1.2f * u))
        drawLine(cordon, nudo, Offset(nudo.x - w * 0.1f, yc + h * 0.1f), 1.4f * u, StrokeCap.Round)
        drawLine(cordon, nudo, Offset(nudo.x + w * 0.12f, yc + h * 0.08f), 1.4f * u, StrokeCap.Round)
        val et = Rect(x0 + (x1 - x0) * 0.1f, h * 0.16f, x1 - (x1 - x0) * 0.1f, h * 0.16f + maxOf((x1 - x0) * 0.58f, 16f * u).coerceAtMost(h * 0.22f))
        etiquetaDeco(v, medidor, et, a, k)
        mesDeco(v, medidor, Rect(x0 + (x1 - x0) * 0.2f, et.bottom + 3f * u, x1 - (x1 - x0) * 0.16f, yc - 4f * u), tono)
        selloDeco(Offset((x0 + x1) / 2 + (x1 - x0) * 0.06f, yc + (h - yc) * 0.55f), minOf((x1 - x0) * 0.22f, 6f * u), v.noche, tono, a, k)
    }
    return Rect(x0, tope, x1, h)
}
