package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CORDEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.CRUDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.ELOTE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.GIRASOL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LADRILLO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LATA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.MARINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.OXIDO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.PINO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaColonos.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** El amarillo de imprenta de las cajas. */
private val MAIZ_CAJA = Color(0xFFE0A62E)

/**
 * Las tres clases de entrada de la alacena de Colonos, elegidas con la semilla del día: tres cosas de la cocina que se
 * entienden de un vistazo y llenan el hueco:
 * - la caja de cereal de antes de la guerra cortada en diagonal como revistero, impresa del color del ánimo (un medio sol sobre
 *   franjas de campo, sin letras), con las hojas dentro y la tarjeta del día sujeta con una pinza de ropa;
 * - el cuaderno de tapas de lámina: dos tapas cortadas de una lata grande, pintadas del ánimo, con remaches, rayones y óxido,
 *   unidas arriba por anillas de alambre, con su etiqueta de papel y la liga de cámara de llanta;
 * - el fardo de hojas envuelto en tela de costal floreada del ánimo, atado con cordel, con su nudo y su tarjeta de envío.
 * Sin ánimo: la caja, de cartón pardo sin imprimir; las tapas, de lata desnuda; el fardo, de arpillera. En los Sueños, la
 * luna: en la caja el dibujo es una luna en lugar del sol; en el cuaderno, una luna pintada; en el fardo, un remiendo de luna.
 */
internal fun DrawScope.volumenColonos(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 4f || size.height < 4f) return
    val a = AireColonos(oscuro, v.noche, true)
    val p = lugarVolumenColonos(v.repisa, v.centro)
    val brasa = brasaNominalColonos(v.repisas)
    val candil = candilNominalColonos(v.repisas)
    val k = luzColonos(p.x, p.y, a, brasa, candil)
    val br = parteBrasaColonos(p.x, p.y, a, brasa, candil)
    val f = if (a.noche) rayoVentanaColonos(p.x, p.y) else 0f
    // De dónde viene la luz: de día, de la ventana (izquierda); de noche, de la que más le llega (bombilla, brasa o candil).
    val hacia = haciaColonos(p.x, p.y, a, brasa, candil)
    val t = tenidoColonos(tonoDeDiaColonos(v.tono, oscuro))
    val l = LuzVolumenColonos(a, k, br, f, hacia)
    when (MuebleEstanteColonos.clase(v.semilla)) {
        0 -> cajaCerealColonos(v, medidor, l, t)
        1 -> cuadernoLataColonos(v, medidor, l, t)
        else -> fardoColonos(v, medidor, l, t)
    }
}

/** La luz que le llega a un volumen: el aire, cuánta ([k]), cuánta de la brasa ([br]), de la luna ([f]) y de qué lado ([hacia]). */
private class LuzVolumenColonos(val a: AireColonos, val k: Float, val br: Float, val f: Float, val hacia: Float) {
    fun c(col: Color, mas: Float = 1f) = a.luz(col, k * mas, br, f)
    fun animo(col: Color) = a.animo(col, k, br, f)
    fun brillo(al: Float) = a.brillo(al, maxOf(k, if (a.oscuro) 0f else 1f), br)
    fun sombra(al: Float) = a.sombra(al)
    val izq: Boolean get() = hacia < 0f
}

private const val TAM_DIA_COLONOS = 17f
private const val TAM_MES_COLONOS = 6.4f

private fun mesCortoColonos(mes: String): String = if (mes.length > 5) mes.take(3) + "." else mes

/** El día, grande, y el mes, chico, dentro de [r], en tinta. */
private fun DrawScope.fechaColonos(r: Rect, v: Volumen, medidor: TextMeasurer, tinta: Color) {
    val corte = r.top + r.height * 0.7f
    textoEn(medidor, v.dia, Rect(r.left, r.top, r.right, corte), PaletaColonos.LETRA, tinta, TAM_DIA_COLONOS, peso = FontWeight.Bold)
    textoEn(medidor, mesCortoColonos(v.mes), Rect(r.left, corte - r.height * 0.04f, r.right, r.bottom), PaletaColonos.LETRA, tinta.copy(alpha = 0.85f), TAM_MES_COLONOS, minimo = 4f)
}

/** La sombra del volumen en la tabla, corrida hacia donde no da la luz, y el plano de sombra de su costado. */
private fun DrawScope.asientoColonos(l: LuzVolumenColonos, x0: Float, x1: Float) {
    val u = density; val w = size.width; val h = size.height
    val ancho = x1 - x0
    // La sombra que echa en el fondo del casillero, en la rendija con el vecino (del lado contrario a la luz).
    if (l.izq) { drawRect(l.sombra(0.42f), Offset(x1, h * 0.14f), Size(w - x1, h * 0.86f)); drawRect(l.sombra(0.2f), Offset(x1 - 1.6f * u, h * 0.2f), Size(1.6f * u, h * 0.8f)) }
    else { drawRect(l.sombra(0.42f), Offset(0f, h * 0.14f), Size(x0, h * 0.86f)); drawRect(l.sombra(0.2f), Offset(x0, h * 0.2f), Size(1.6f * u, h * 0.8f)) }
    drawOval(l.sombra(0.45f), Offset(x0 + (if (l.izq) ancho * 0.08f else -ancho * 0.06f), h - 2.6f * u), Size(ancho, 3.6f * u))
}

/** Un creciente de luna, con su sombra y su brillo. */
private fun DrawScope.lunaColonos(c: Offset, r: Float, fondo: Color, l: LuzVolumenColonos) {
    val plata = if (l.a.oscuro) mezcla(LUNA, l.a.hondo, 0.15f - 0.15f * l.k) else LUNA
    drawCircle(l.sombra(0.35f), r, c + Offset(r * 0.18f, r * 0.22f))
    drawCircle(plata, r, c)
    drawCircle(fondo, r * 0.8f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
}

// ---------------------------------------------------------------- la caja de cereal

/**
 * La caja de cereal de antes de la guerra, de pie, cortada en diagonal como revistero: la cara grande impresa del ánimo con su
 * filete, el recuadro crema con un medio sol saliendo sobre dos franjas de campo (en los Sueños, una luna) y la franja de abajo;
 * la cara angosta del frente, en sombra; por el corte, la boca de cartón pardo y las hojas que asoman (más cuanto más
 * escribiste); el cartón rozado en las esquinas y un doblez; la tarjeta del día sujeta con una pinza de ropa.
 */
private fun DrawScope.cajaCerealColonos(v: Volumen, medidor: TextMeasurer, l: LuzVolumenColonos, t: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val a = l.a
    val x0 = (1.2f + 1.4f * azar(s, 78)) * u; val x1 = w - (2.4f + 2.8f * azar(s, 77)) * u
    val frente = (x1 - x0) * 0.17f
    val xf = x1 - frente
    val bajo = h * 0.36f; val alto = h * 0.035f
    val sinAnimo = t == null
    val carton = Color(0xFFB79A6E)
    val cuerpo = if (t != null) l.animo(t) else l.c(carton)
    val claroImpreso = if (t != null) l.animo(mezcla(t, Color.White, if (t.luminance3() > 0.6f) 0.0f else 0.35f)) else l.c(mezcla(carton, CRUDO, 0.3f))
    asientoColonos(l, x0, x1)
    // Las hojas de dentro, detrás de la cara: asoman por el corte, más altas hacia el fondo.
    val n = 2 + (v.lleno * 3.5f).toInt()
    for (i in 0 until n) {
        val fx = (i + 0.5f) / n
        val xa = x0 + (xf - x0) * (fx - 0.42f / n) - 1f * u
        val tope = alto + (bajo - alto) * (1f - fx) * 0.75f - h * 0.05f * azar(s, 40 + i) - 2f * u
        rotate(-4f + 8f * azar(s, 50 + i), Offset(xa, tope + h * 0.2f)) {
            val hoja = Rect(xa, tope, xa + (xf - x0) * 0.42f, h * 0.6f)
            drawRect(l.sombra(0.3f), Offset(hoja.left + 1f * u, hoja.top + 1f * u), hoja.size)
            drawRect(l.c(if (i % 2 == 0) PAPEL else mezcla(PAPEL, CRUDO, 0.6f)), hoja.topLeft, hoja.size)
            for (j in 1..3) drawLine(l.c(MARINO).copy(alpha = 0.3f), Offset(hoja.left + 1f * u, hoja.top + 2.5f * u * j), Offset(hoja.right - 1f * u, hoja.top + 2.5f * u * j), 0.5f * u)
        }
    }
    // La boca: el cartón pardo de dentro, en sombra, apenas por encima del corte.
    drawPath(poligonoColonos(x0, bajo, xf, alto, xf, alto + 3f * u, x0, bajo + 3f * u), l.c(mezcla(carton, HIERRO, 0.45f)))
    // La cara angosta del frente, de arriba abajo, y la cara grande cortada.
    val angosta = poligonoColonos(xf, alto, x1, alto + 1.5f * u, x1, h - 1.2f * u, xf, h)
    drawPath(angosta, mezcla(cuerpo, if (l.izq) a.hondo else Color.White, if (l.izq) 0.32f else 0.1f))
    val cara = poligonoColonos(x0, bajo, xf, alto, xf, h, x0, h)
    drawPath(cara, cuerpo)
    clipPath(cara) {
        // El filete impreso y la franja de abajo.
        drawRect(claroImpreso.copy(alpha = if (sinAnimo) 0.2f else 0.7f), Offset(x0 + 1.6f * u, h * 0.5f), Size(xf - x0 - 3.2f * u, 0.8f * u))
        // La franja de abajo y la cinta: de otro color según la caja.
        val tintas = listOf(mezcla(cuerpo, a.hondo, 0.35f), l.c(MAIZ_CAJA), l.c(LADRILLO), l.c(CRUDO), l.c(VERDE))
        drawRect(tintas[(azar(s, 79) * tintas.size).toInt().coerceIn(0, tintas.size - 1)], Offset(x0, h * 0.89f), Size(xf - x0, h * 0.11f))
        // La cinta ondulada del impreso, debajo de la tarjeta.
        if (!sinAnimo) {
            val cinta = Path().apply {
                moveTo(x0, h * 0.47f); quadraticTo(x0 + (xf - x0) * 0.5f, h * 0.43f, xf, h * 0.47f)
                lineTo(xf, h * 0.51f); quadraticTo(x0 + (xf - x0) * 0.5f, h * 0.47f, x0, h * 0.51f); close()
            }
            val cintas = listOf(mezcla(GIRASOL, CRUDO, 0.4f), CRUDO, LADRILLO, MAIZ_CAJA)
            drawPath(cinta, l.c(cintas[(azar(s, 84) * cintas.size).toInt().coerceIn(0, cintas.size - 1)]).copy(alpha = 0.85f))
        }
        // El recuadro con el dibujo: un medio sol saliendo (o la luna) sobre dos franjas de campo.
        if (!sinAnimo) {
            val rec = Rect(x0 + 2.2f * u, h * 0.55f, xf - 2.2f * u, h * 0.84f)
            drawRoundRect(l.c(mezcla(CRUDO, Color.White, 0.3f)), rec.topLeft, rec.size, CornerRadius(2f * u))
            clipRect(rec.left, rec.top, rec.right, rec.bottom) { dibujoCajaColonos(rec, v, l, Math.floorMod(s xor (s ushr 5), 3)) }
            drawRoundRect(cuerpo.copy(alpha = 0.6f), rec.topLeft, rec.size, CornerRadius(2f * u), style = Stroke(0.8f * u))
        }
        // El cartón rozado en las esquinas y en el corte, y un doblez.
        val rozado = l.c(mezcla(carton, CRUDO, 0.4f)).copy(alpha = 0.75f)
        drawPath(poligonoColonos(x0, h, x0 + 3f * u, h, x0, h - 4f * u), rozado)
        drawPath(poligonoColonos(xf, h, xf - 2.5f * u, h, xf, h - 3f * u), rozado)
        drawLine(rozado, Offset(x0, bajo), Offset(xf, alto), 1.4f * u)
        drawLine(l.sombra(0.25f), Offset(x0 + (xf - x0) * 0.2f, h * 0.88f), Offset(x0 + (xf - x0) * 0.55f, h * 0.62f), 0.7f * u)
        // La luz y la sombra de la cara: el plano de luz del lado de donde viene, el de sombra del otro.
        if (l.izq) {
            drawRect(l.brillo(0.22f), Offset(x0, 0f), Size((xf - x0) * 0.18f, h))
            drawRect(l.sombra(0.16f), Offset(xf - (xf - x0) * 0.22f, 0f), Size((xf - x0) * 0.22f, h))
        } else {
            drawRect(l.sombra(0.22f), Offset(x0, 0f), Size((xf - x0) * 0.3f, h))
            drawRect(l.brillo(0.3f), Offset(xf - (xf - x0) * 0.14f, 0f), Size((xf - x0) * 0.14f, h))
        }
    }
    drawLine(l.brillo(if (a.oscuro) 0.7f else 0.5f), Offset(x0, bajo), Offset(xf, alto), 0.9f * u)
    drawLine(l.sombra(0.4f), Offset(xf, alto), Offset(xf, h), 0.7f * u)
    // La tarjeta del día, sujeta al corte con una pinza de ropa.
    val cx = x0 + (xf - x0) * 0.5f
    val yCorte = bajo + (alto - bajo) * 0.5f
    val tarjeta = Rect(x0 + 1.4f * u, yCorte - 1f * u, xf - 0.6f * u, yCorte + h * 0.27f)
    val giro = -3f + 6f * azar(s, 61)
    rotate(giro, Offset(cx, tarjeta.top)) {
        drawRect(l.sombra(0.4f), Offset(tarjeta.left + 1f * u, tarjeta.top + 1.4f * u), tarjeta.size)
        drawRect(a.etiqueta(l.k, if (azar(s, 80) > 0.5f) PAPEL else mezcla(PAPEL, ELOTE, 0.3f), l.br), tarjeta.topLeft, tarjeta.size)
        drawRect(l.c(LADRILLO, 2f).copy(alpha = 0.6f), Offset(tarjeta.left, tarjeta.top + tarjeta.height * 0.24f), Size(tarjeta.width, 0.7f * u))
        drawRect(l.sombra(0.12f), Offset(tarjeta.left, tarjeta.bottom - 1.2f * u), Size(tarjeta.width, 1.2f * u))
        fechaColonos(Rect(tarjeta.left, tarjeta.top + tarjeta.height * 0.27f, tarjeta.right, tarjeta.bottom), v, medidor, a.tinta)
    }
    pinzaColonos(Offset(cx, tarjeta.top + 1f * u), h * 0.17f, l)
    if (a.oscuro && l.k > 0.06f) drawRect(a.brillo(0.6f * l.k, l.k, l.br), Offset(if (l.izq) x0 else x1 - 1.3f * u, alto), Size(1.3f * u, h - alto))
}

/**
 * El dibujo impreso de la caja de cereal, sin letras, uno de tres según la semilla: el medio sol saliendo sobre dos franjas de
 * campo; el tazón con su montón de hojuelas y la cuchara; o la mazorca con sus dos hojas sobre un sol. En los Sueños, la luna
 * en lugar del sol (o en la esquina).
 */
private fun DrawScope.dibujoCajaColonos(rec: Rect, v: Volumen, l: LuzVolumenColonos, cual: Int) {
    val u = density
    val fondo = l.c(mezcla(CRUDO, Color.White, 0.3f))
    val horizonte = rec.top + rec.height * 0.62f
    val rs = minOf(rec.width * 0.34f, rec.height * 0.42f)
    when (cual) {
        0 -> {
            if (v.noche) lunaColonos(Offset(rec.center.x, horizonte - rs * 0.75f), rs * 0.6f, fondo, l)
            else drawCircle(l.c(mezcla(GIRASOL, LADRILLO, 0.25f)), rs, Offset(rec.center.x, horizonte))
            val campo = Path().apply {
                moveTo(rec.left, horizonte); quadraticTo(rec.center.x, horizonte - rec.height * 0.12f, rec.right, horizonte + rec.height * 0.04f)
                lineTo(rec.right, rec.bottom); lineTo(rec.left, rec.bottom); close()
            }
            drawPath(campo, l.c(VERDE))
            val surco = Path().apply {
                moveTo(rec.left, horizonte + rec.height * 0.2f); quadraticTo(rec.center.x, horizonte + rec.height * 0.08f, rec.right, horizonte + rec.height * 0.24f)
                lineTo(rec.right, rec.bottom); lineTo(rec.left, rec.bottom); close()
            }
            drawPath(surco, l.c(ELOTE))
        }
        1 -> {
            // El tazón: medio óvalo con su borde, el montón de hojuelas y la cuchara.
            val tz = Rect(rec.left + rec.width * 0.12f, rec.top + rec.height * 0.48f, rec.right - rec.width * 0.12f, rec.bottom - rec.height * 0.08f)
            val copos = Path()
            for (i in 0 until 9) {
                val cx = tz.left + tz.width * (0.12f + 0.76f * azar(v.semilla, 150 + i)); val cy = tz.top - tz.height * 0.25f * azar(v.semilla, 160 + i)
                copos.addOval(Rect(cx - rec.width * 0.08f, cy - rec.width * 0.05f, cx + rec.width * 0.08f, cy + rec.width * 0.05f))
            }
            drawPath(copos, l.c(ELOTE))
            drawLine(l.c(LATA), Offset(tz.right - tz.width * 0.2f, tz.top), Offset(tz.right + tz.width * 0.05f, tz.top - tz.height * 0.75f), 1.2f * u)
            drawArc(l.c(LADRILLO), 0f, 180f, true, Offset(tz.left, tz.top - tz.height * 0.9f), Size(tz.width, tz.height * 1.9f))
            drawRect(l.c(Color.White).copy(alpha = 0.7f), Offset(tz.left + tz.width * 0.1f, tz.top + tz.height * 0.15f), Size(tz.width * 0.8f, 0.8f * u))
            if (v.noche) lunaColonos(Offset(rec.left + rec.width * 0.24f, rec.top + rec.height * 0.24f), rec.width * 0.13f, fondo, l)
        }
        else -> {
            // La mazorca, de pie sobre un sol: sus granos en hileras y sus dos hojas.
            if (v.noche) lunaColonos(Offset(rec.center.x, rec.top + rec.height * 0.36f), rs * 0.62f, fondo, l)
            else drawCircle(l.c(mezcla(GIRASOL, Color.White, 0.25f)), rs * 0.95f, Offset(rec.center.x, rec.top + rec.height * 0.42f))
            val m = Rect(rec.center.x - rec.width * 0.11f, rec.top + rec.height * 0.16f, rec.center.x + rec.width * 0.11f, rec.bottom - rec.height * 0.1f)
            val hojas = Path().apply {
                moveTo(m.center.x, m.bottom); quadraticTo(m.left - rec.width * 0.26f, m.center.y, m.left - rec.width * 0.12f, m.top + m.height * 0.3f)
                quadraticTo(m.left, m.center.y, m.center.x, m.bottom)
                moveTo(m.center.x, m.bottom); quadraticTo(m.right + rec.width * 0.26f, m.center.y, m.right + rec.width * 0.1f, m.top + m.height * 0.25f)
                quadraticTo(m.right, m.center.y, m.center.x, m.bottom)
            }
            drawOval(l.c(ELOTE), m.topLeft, m.size)
            val granos = Path()
            for (f in 1..4) granos.moveTo(m.left + m.width * 0.1f, m.top + m.height * f / 5f).also { granos.lineTo(m.right - m.width * 0.1f, m.top + m.height * f / 5f) }
            drawPath(granos, l.c(mezcla(ELOTE, LADRILLO, 0.35f)), style = Stroke(0.6f * u))
            drawPath(hojas, l.c(VERDE))
        }
    }
}

/** Luminancia aproximada (para no aclarar el impreso de un crema). */
private fun Color.luminance3(): Float = 0.3f * red + 0.59f * green + 0.11f * blue

/** La pinza de ropa de madera: las dos patas, el muelle de alambre y su sombra. */
private fun DrawScope.pinzaColonos(arriba: Offset, largo: Float, l: LuzVolumenColonos) {
    val u = density
    val an = 2.4f * u
    val r = Rect(arriba.x - an, arriba.y - largo * 0.66f, arriba.x + an, arriba.y + largo * 0.3f)
    drawRoundRect(l.sombra(0.45f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size, CornerRadius(an * 0.6f))
    drawRoundRect(l.c(PINO), r.topLeft, r.size, CornerRadius(an * 0.6f))
    drawLine(l.c(PINO_OSC), Offset(r.center.x, r.top + 1f * u), Offset(r.center.x, r.bottom), 0.7f * u)
    drawRect(l.brillo(0.4f), Offset(r.left + 0.4f * u, r.top), Size(0.8f * u, r.height))
    drawRect(l.c(LATA), Offset(r.left - 0.4f * u, r.top + r.height * 0.36f), Size(r.width + 0.8f * u, 1.4f * u))
    drawRect(l.sombra(0.3f), Offset(r.left - 0.4f * u, r.top + r.height * 0.36f + 1.4f * u), Size(r.width + 0.8f * u, 0.6f * u))
}

// ---------------------------------------------------------------- el cuaderno de tapas de lámina

/**
 * El cuaderno de tapas de lámina: la tapa de delante, cortada de una lata y pintada del ánimo, con sus esquinas redondeadas a
 * lima, los remaches, los rayones por donde brilla la lata, el óxido en los bordes y una abolladura; el canto de las hojas a la
 * derecha (más grueso cuanto más escribiste) y la tapa de atrás, de lata desnuda; las anillas de alambre arriba; la etiqueta
 * de papel con el día y la liga de cámara de llanta. En los Sueños, una luna pintada.
 */
private fun DrawScope.cuadernoLataColonos(v: Volumen, medidor: TextMeasurer, l: LuzVolumenColonos, t: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val a = l.a
    val x0 = (1.2f + 1.4f * azar(s, 78)) * u; val x1 = w - (2.4f + 2.8f * azar(s, 77)) * u
    val hojas = (2.2f + v.lleno * 3.4f) * u
    val top = h * 0.075f
    val tapa = Rect(x0, top, x1 - hojas, h - 0.6f * u)
    val pintura = if (t != null) l.animo(t) else l.c(LATA)
    asientoColonos(l, x0, x1)
    // La tapa de atrás (lata desnuda) y el canto de las hojas.
    drawRoundRect(l.c(LATA_OSC), Offset(tapa.left + 1.2f * u, tapa.top + 1f * u), Size(x1 - x0 - 1.2f * u, tapa.height), CornerRadius(1.6f * u))
    val canto = Rect(tapa.right - 1f * u, tapa.top + 1.6f * u, x1 - 0.6f * u, tapa.bottom - 0.8f * u)
    drawRect(l.c(mezcla(PAPEL, CRUDO, 0.4f)), canto.topLeft, canto.size)
    var y = canto.top + 1.2f * u
    val rayas = Path()
    while (y < canto.bottom - 0.5f * u) { rayas.moveTo(canto.left, y); rayas.lineTo(canto.right, y); y += 1.3f * u }
    drawPath(rayas, l.sombra(0.22f), style = Stroke(0.4f * u))
    drawRect(l.sombra(if (l.izq) 0.3f else 0.1f), canto.topLeft, canto.size)
    // Las anillas de alambre, detrás (la mitad que pasa por atrás) y por delante.
    val n = ((tapa.width / (6f * u)).toInt()).coerceIn(3, 7)
    val atras = Path(); val delante = Path()
    for (i in 0 until n) {
        val ax = tapa.left + tapa.width * (i + 0.5f) / n
        val anillo = Rect(ax - 1.6f * u, top - 3.6f * u, ax + 1.6f * u, top + 3.4f * u)
        atras.addArc(anillo, 180f, 180f)
        delante.addArc(Rect(anillo.left, anillo.top, anillo.right, anillo.bottom), 0f, 90f)
    }
    drawPath(atras, l.c(LATA_OSC), style = Stroke(1f * u))
    // La tapa pintada.
    val forma = RoundRect(tapa, CornerRadius(2f * u))
    val tapaP = Path().apply { addRoundRect(forma) }
    drawPath(tapaP, pintura)
    clipPath(tapaP) {
        // El brillo de la lata pintada: una franja en diagonal, y el plano de sombra del otro lado.
        val bx = if (l.izq) tapa.left + tapa.width * 0.18f else tapa.right - tapa.width * 0.42f
        drawPath(poligonoColonos(bx, tapa.top, bx + tapa.width * 0.22f, tapa.top, bx + tapa.width * 0.02f, tapa.bottom, bx - tapa.width * 0.2f, tapa.bottom), l.brillo(0.2f))
        drawPath(poligonoColonos(bx + tapa.width * 0.28f, tapa.top, bx + tapa.width * 0.34f, tapa.top, bx + tapa.width * 0.14f, tapa.bottom, bx + tapa.width * 0.08f, tapa.bottom), l.brillo(0.14f))
        drawRect(l.brillo(0.45f), Offset(tapa.left, tapa.top), Size(tapa.width, 0.9f * u))
        drawRect(l.sombra(0.25f), Offset(tapa.left, tapa.bottom - 1.6f * u), Size(tapa.width, 1.6f * u))
        drawRect(l.sombra(0.2f), Offset(if (l.izq) tapa.right - tapa.width * 0.2f else tapa.left, tapa.top), Size(tapa.width * 0.2f, tapa.height))
        // Los rayones (brilla la lata) y el óxido en los bordes.
        val rayones = Path()
        for (i in 0 until 4) {
            val ry = tapa.top + tapa.height * (0.45f + 0.5f * azar(s, 70 + i))
            val rx = tapa.left + tapa.width * azar(s, 74 + i)
            rayones.moveTo(rx, ry); rayones.lineTo(rx + tapa.width * (0.2f + 0.25f * azar(s, 78 + i)), ry - tapa.height * 0.05f * azar(s, 82 + i))
        }
        drawPath(rayones, l.c(LATA).copy(alpha = 0.8f), style = Stroke(0.6f * u))
        val oxido = Path()
        for (i in 0 until 5) {
            val ox = if (i % 2 == 0) tapa.left else tapa.right
            val oy = tapa.top + tapa.height * azar(s, 90 + i)
            oxido.addOval(Rect(ox - 1.6f * u, oy, ox + 1.6f * u, oy + (2f + 4f * azar(s, 95 + i)) * u))
        }
        oxido.addOval(Rect(tapa.left + tapa.width * 0.3f, tapa.bottom - 1.6f * u, tapa.left + tapa.width * 0.7f, tapa.bottom + 1.6f * u))
        drawPath(oxido, l.c(OXIDO).copy(alpha = 0.75f))
        // Lo que queda de la litografía de la lata: un filete crudo y una franja honda, abajo, rayados.
        drawRect(l.c(mezcla(CRUDO, GIRASOL, 0.3f)).copy(alpha = 0.75f), Offset(tapa.left, tapa.bottom - tapa.height * 0.13f), Size(tapa.width, tapa.height * 0.022f))
        drawRect(mezcla(pintura, a.hondo, 0.4f), Offset(tapa.left, tapa.bottom - tapa.height * 0.1f), Size(tapa.width, tapa.height * 0.06f))
        // La abolladura.
        val ab = Offset(tapa.left + tapa.width * (0.3f + 0.4f * azar(s, 99)), tapa.top + tapa.height * 0.86f)
        drawArc(l.sombra(0.3f), 200f, 140f, false, Offset(ab.x - 3f * u, ab.y - 2f * u), Size(6f * u, 4f * u), style = Stroke(0.8f * u))
        drawArc(l.brillo(0.35f), 20f, 140f, false, Offset(ab.x - 3f * u, ab.y - 2f * u), Size(6f * u, 4f * u), style = Stroke(0.8f * u))
        if (v.noche) lunaColonos(Offset(tapa.center.x - tapa.width * 0.12f, tapa.top + tapa.height * 0.58f), minOf(tapa.width * 0.16f, 4.5f * u), pintura, l)
    }
    drawPath(tapaP, l.sombra(0.35f), style = Stroke(0.7f * u))
    // Los remaches de las esquinas.
    listOf(Offset(tapa.left + 2.2f * u, tapa.top + 3.6f * u), Offset(tapa.right - 2.2f * u, tapa.top + 3.6f * u), Offset(tapa.left + 2.2f * u, tapa.bottom - 2.6f * u),
        Offset(tapa.right - 2.2f * u, tapa.bottom - 2.6f * u)).forEach { p ->
        drawCircle(l.sombra(0.4f), 0.9f * u, p + Offset(0.4f * u, 0.5f * u))
        drawCircle(l.c(LATA), 0.9f * u, p)
    }
    // Los agujeros y las anillas por delante.
    for (i in 0 until n) {
        val ax = tapa.left + tapa.width * (i + 0.5f) / n
        drawCircle(a.hondo.copy(alpha = 0.8f), 0.8f * u, Offset(ax, top + 2.6f * u))
    }
    drawPath(Path().apply {
        for (i in 0 until n) {
            val ax = tapa.left + tapa.width * (i + 0.5f) / n
            moveTo(ax, top + 2.6f * u); cubicTo(ax + 0.4f * u, top - 1f * u, ax + 1.8f * u, top - 3.8f * u, ax + 0.2f * u, top - 3.6f * u)
        }
    }, l.c(LATA), style = Stroke(1.1f * u, cap = StrokeCap.Round))
    drawPath(delante, l.brillo(0.5f), style = Stroke(0.4f * u))
    // La etiqueta de papel pegada, con su doble filete y el día.
    val et = Rect(tapa.left + 2.2f * u, tapa.top + tapa.height * 0.12f, tapa.right - 2.2f * u, tapa.top + tapa.height * 0.44f)
    drawRect(l.sombra(0.3f), Offset(et.left + 0.8f * u, et.top + 1f * u), et.size)
    drawRoundRect(a.etiqueta(l.k, PAPEL, l.br), et.topLeft, et.size, CornerRadius(1.6f * u))
    drawRoundRect(l.c(MARINO, 1.6f).copy(alpha = 0.55f), Offset(et.left + 1.2f * u, et.top + 1.2f * u), Size(et.width - 2.4f * u, et.height - 2.4f * u), CornerRadius(1f * u), style = Stroke(0.5f * u))
    fechaColonos(Rect(et.left + 1f * u, et.top + 1.2f * u, et.right - 1f * u, et.bottom - 0.8f * u), v, medidor, a.tinta)
    // La esquina de abajo doblada (canto doblado), en unos: se ve el revés de la lata y la sombra del doblez.
    if (azar(s, 82) > 0.55f) {
        val d = minOf(tapa.width * 0.3f, 7f * u)
        drawPath(poligonoColonos(tapa.right, tapa.bottom - d, tapa.right, tapa.bottom, tapa.right - d, tapa.bottom), l.c(mezcla(PAPEL, CRUDO, 0.4f)))
        drawPath(poligonoColonos(tapa.right, tapa.bottom - d, tapa.right - d, tapa.bottom, tapa.right - d * 0.92f, tapa.bottom - d * 0.92f), l.c(LATA))
        drawLine(l.sombra(0.45f), Offset(tapa.right, tapa.bottom - d), Offset(tapa.right - d, tapa.bottom), 0.8f * u)
    }
    // La esquina remendada con cinta de tela, en otros.
    if (azar(s, 83) > 0.62f) {
        rotate(-40f, Offset(tapa.left + 3f * u, tapa.top + 5f * u)) {
            drawRect(l.sombra(0.25f), Offset(tapa.left - 3f * u + 0.6f * u, tapa.top + 3f * u + 0.8f * u), Size(12f * u, 4f * u))
            drawRect(l.c(mezcla(CRUDO, ELOTE, 0.35f)).copy(alpha = 0.92f), Offset(tapa.left - 3f * u, tapa.top + 3f * u), Size(12f * u, 4f * u))
        }
    }
    // Lo que lo cierra: la liga de cámara de llanta (negra o roja) o un cordel con su nudo.
    val ly = tapa.top + tapa.height * 0.72f
    val cierre = azar(s, 81)
    if (cierre < 0.72f) {
        val goma = if (cierre < 0.42f) Color(0xFF232227) else Color(0xFF7A2A22)
        drawRect(l.sombra(0.35f), Offset(x0 - 0.4f * u, ly + 1.2f * u), Size(x1 - x0 + 0.8f * u, 2.2f * u))
        drawRect(l.c(goma), Offset(x0 - 0.4f * u, ly), Size(x1 - x0 + 0.8f * u, 2.4f * u))
        drawRect(l.brillo(0.3f), Offset(x0, ly + 0.3f * u), Size(x1 - x0, 0.5f * u))
    } else {
        drawRect(l.sombra(0.3f), Offset(x0 - 0.4f * u, ly + 1f * u), Size(x1 - x0 + 0.8f * u, 1.3f * u))
        drawRect(l.c(CORDEL), Offset(x0 - 0.4f * u, ly), Size(x1 - x0 + 0.8f * u, 1.3f * u))
        val nx = x0 + (x1 - x0) * 0.62f
        drawCircle(l.c(CORDEL), 1.6f * u, Offset(nx, ly + 0.6f * u))
        drawLine(l.c(CORDEL), Offset(nx, ly + 0.6f * u), Offset(nx - 1.6f * u, ly + 5f * u), 0.9f * u)
        drawLine(l.c(CORDEL), Offset(nx, ly + 0.6f * u), Offset(nx + 1.8f * u, ly + 4.4f * u), 0.9f * u)
    }
    if (a.oscuro && l.k > 0.06f) drawRect(a.brillo(0.6f * l.k, l.k, l.br), Offset(if (l.izq) tapa.left else tapa.right - 1.3f * u, tapa.top + 2f * u), Size(1.3f * u, tapa.height - 4f * u))
}

// ---------------------------------------------------------------- el fardo de tela de costal

/**
 * El fardo de hojas envuelto en tela de costal floreada del ánimo: blando, asentado y más ancho abajo, con la solapa doblada
 * arriba y su borde deshilachado, los pliegues, la luz de un lado; atado con dos vueltas de cordel y el nudo arriba, del que
 * cuelga la tarjeta de envío con el día. Sin ánimo, de arpillera. En los Sueños, un remiendo con forma de luna.
 */
private fun DrawScope.fardoColonos(v: Volumen, medidor: TextMeasurer, l: LuzVolumenColonos, t: Color?) {
    val u = density; val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val a = l.a
    val x0 = (1.2f + 1.4f * azar(s, 78)) * u; val x1 = w - (2.4f + 2.8f * azar(s, 77)) * u
    val cx = (x0 + x1) / 2
    val top = h * 0.1f
    val arpillera = Color(0xFFA88A5E)
    val tela = if (t != null) l.animo(t) else l.c(arpillera)
    asientoColonos(l, x0, x1)
    val bulto = Path().apply {
        moveTo(x0 + 1.2f * u, h)
        cubicTo(x0 - 0.6f * u, h * 0.72f, x0 + 0.6f * u, h * 0.32f, x0 + 2f * u, top + h * 0.06f)
        quadraticTo(x0 + 3f * u, top, cx, top + 1f * u)
        quadraticTo(x1 - 3f * u, top, x1 - 2f * u, top + h * 0.06f)
        cubicTo(x1 - 0.6f * u, h * 0.32f, x1 + 0.6f * u, h * 0.72f, x1 - 1.2f * u, h)
        close()
    }
    drawPath(bulto, tela)
    clipPath(bulto) {
        // El estampado de florecitas (o la trama de la arpillera), teñido de la luz.
        if (t != null) {
            val crudo = l.c(mezcla(CRUDO, Color.White, 0.2f), 1.1f)
            drawRect(TelaColonos.pincel(Math.floorMod(s xor (s ushr 4), 4), u), Offset.Zero, Size(w, h), alpha = if (t.luminance3() > 0.6f) 0.5f else 0.75f,
                colorFilter = ColorFilter.tint(if (t.luminance3() > 0.6f) l.c(LADRILLO) else crudo, BlendMode.Modulate))
        } else {
            val trama = Path()
            var yy = top; while (yy < h) { trama.moveTo(0f, yy); trama.lineTo(w, yy); yy += 1.6f * u }
            drawPath(trama, l.sombra(0.18f), style = Stroke(0.6f * u))
        }
        // Los pliegues: tres curvas de sombra con su lomo de luz.
        val pliegues = Path(); val lomos = Path()
        for (i in 0 until 3) {
            val px = x0 + (x1 - x0) * (0.22f + 0.28f * i + 0.08f * azar(s, 110 + i))
            pliegues.moveTo(px, h * 0.45f); pliegues.cubicTo(px - 1.6f * u, h * 0.6f, px + 1.6f * u, h * 0.8f, px - 0.6f * u, h)
            lomos.moveTo(px + 1.4f * u, h * 0.5f); lomos.cubicTo(px - 0.2f * u, h * 0.62f, px + 3f * u, h * 0.82f, px + 1f * u, h)
        }
        drawPath(pliegues, l.sombra(0.28f), style = Stroke(1.4f * u))
        drawPath(lomos, l.brillo(0.16f), style = Stroke(1f * u))
        // El plano de luz y el de sombra del bulto.
        drawRect(l.sombra(0.26f), Offset(if (l.izq) x1 - (x1 - x0) * 0.3f else x0, 0f), Size((x1 - x0) * 0.3f, h))
        drawRect(l.brillo(0.16f), Offset(if (l.izq) x0 else x1 - (x1 - x0) * 0.2f, 0f), Size((x1 - x0) * 0.2f, h))
        drawRect(l.sombra(0.3f), Offset(0f, h * 0.9f), Size(w, h * 0.1f))
        // La solapa doblada arriba: una V con su sombra debajo y el borde deshilachado.
        val punta = Offset(cx + (x1 - x0) * 0.06f * (azar(s, 120) - 0.5f), top + h * 0.26f)
        val solapa = poligonoColonos(x0, top + h * 0.02f, x1, top + h * 0.02f, punta.x, punta.y)
        drawPath(corridoColonos(solapa, 0f, 1.6f * u), l.sombra(0.35f))
        drawPath(solapa, mezcla(tela, Color.White, if (a.oscuro) 0.03f else 0.08f))
        val hilos = Path()
        for (i in 0 until 6) {
            val f = (i + 0.5f) / 6f
            val bx = x0 + (punta.x - x0) * f; val by = top + h * 0.02f + (punta.y - top - h * 0.02f) * f
            hilos.moveTo(bx, by); hilos.lineTo(bx - 0.4f * u, by + 1.6f * u)
        }
        drawPath(hilos, l.c(CRUDO).copy(alpha = 0.6f), style = Stroke(0.5f * u))
        if (v.noche) lunaColonos(Offset(cx - (x1 - x0) * 0.12f, h * 0.84f), minOf((x1 - x0) * 0.15f, 4.5f * u), tela, l)
    }
    drawPath(bulto, l.sombra(0.3f), style = Stroke(0.7f * u))
    // El cordel: dos vueltas que siguen la panza y la que sube al nudo (termina en la primera vuelta).
    val cordel = l.c(CORDEL)
    val v1 = h * 0.5f; val v2 = h * 0.8f
    listOf(v1, v2).forEach { yv ->
        val vuelta = Path().apply { moveTo(x0, yv); quadraticTo(cx, yv + 2.4f * u, x1, yv) }
        drawPath(corridoColonos(vuelta, 0f, 1.2f * u), l.sombra(0.3f), style = Stroke(1.3f * u))
        drawPath(vuelta, cordel, style = Stroke(1.3f * u))
    }
    val nudo = Offset(cx, top + 0.6f * u)
    drawLine(cordel, nudo, Offset(cx, v1 + 1.2f * u), 1.2f * u)
    drawCircle(cordel, 1.9f * u, nudo)
    drawLine(cordel, nudo, nudo + Offset(-2.6f * u, -2.4f * u), 1f * u)
    drawLine(cordel, nudo, nudo + Offset(2.8f * u, -1.8f * u), 1f * u)
    drawCircle(l.brillo(0.5f), 0.7f * u, nudo - Offset(0.6f * u, 0.6f * u))
    // La tarjeta de envío, colgada del cordel, con su ojal reforzado.
    val tw = minOf(x1 - x0 - 2f * u, 26f * u)
    val tr = Rect(cx - tw / 2, top + h * 0.13f, cx + tw / 2, top + h * 0.13f + h * 0.25f)
    val giro = -4f + 8f * azar(s, 130)
    drawLine(cordel, Offset(cx, top + h * 0.06f), Offset(cx, tr.top + 2f * u), 0.7f * u)
    rotate(giro, Offset(cx, tr.top)) {
        val m = tr.height * 0.18f
        val tarjeta = poligonoColonos(tr.left + m, tr.top, tr.right - m, tr.top, tr.right, tr.top + m, tr.right, tr.bottom, tr.left, tr.bottom, tr.left, tr.top + m)
        drawPath(corridoColonos(tarjeta, 1f * u, 1.4f * u), l.sombra(0.42f))
        drawPath(tarjeta, a.etiqueta(l.k, mezcla(PAPEL, ELOTE, 0.32f), l.br))
        drawCircle(a.etiqueta(l.k, PAPEL, l.br), 1.6f * u, Offset(tr.center.x, tr.top + 2.2f * u))
        drawCircle(a.hondo.copy(alpha = 0.7f), 0.7f * u, Offset(tr.center.x, tr.top + 2.2f * u))
        fechaColonos(Rect(tr.left + 0.6f * u, tr.top + 3.6f * u, tr.right - 0.6f * u, tr.bottom - 0.4f * u), v, medidor, a.tinta)
    }
    if (a.oscuro && l.k > 0.06f) drawPath(bulto, a.brillo(0.35f * l.k, l.k, l.br), style = Stroke(1f * u))
}
