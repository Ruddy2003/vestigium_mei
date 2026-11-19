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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.MuebleEstanteAndalus.LETRA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.BRONCE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CARMESI
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CEDRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.CUERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ANIL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.LUNA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.ORO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PERGAMINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaAndalus.PINO
import io.github.ruddy2003.vestigium.ui.biblioteca.TrazoAndalus.poligono
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE

// =====================================================================
//  Al-Ándalus: las tres clases de entrada del estante
// =====================================================================

/** Los cordobanes sin teñir de los códices sin ánimo: castaño, rojizo, pardo oscuro y miel. */
/** Lo hondo del cordobán: el ánimo se tiñe hacia aquí para que sea cuero teñido y no pastel. */
private val TINTA_CUERO_ANDALUS = Color(0xFF2A1810)

private val PIELES_ANDALUS = listOf(Color(0xFF7A4628), Color(0xFF8C4A2C), Color(0xFF5A3420), Color(0xFF9A6A38))

/** La marca de la entrada: de día, una rueda dorada de ocho radios; en los Sueños, la media luna. */
private fun DrawScope.marcaAndalus(c: Offset, tam: Float, luna: Boolean, color: Color, fondo: Color, a: AireAndalus, k: Float) {
    val u = density
    if (luna) {
        drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.2f - 0.2f * k) else LUNA, tam, c)
        drawCircle(fondo, tam * 0.84f, Offset(c.x + tam * 0.42f, c.y - tam * 0.24f))
    } else {
        drawCircle(color, tam, c, style = Stroke(0.7f * u))
        for (q in 0 until 4) rotate(45f * q, c) { drawLine(color, Offset(c.x - tam * 0.8f, c.y), Offset(c.x + tam * 0.8f, c.y), 0.6f * u) }
        drawCircle(color, tam * 0.3f, c)
    }
}

/** El filo del candil en lo que lo mira: la cabeza y el costado izquierdo del volumen. */
private fun DrawScope.filoAndalus(a: AireAndalus, k: Float, arriba: Float, ancho: Float) {
    if (!a.oscuro || k < 0.1f) return
    val u = density
    drawRect(FILO_MUEBLE.copy(alpha = 0.6f * k), Offset(1f * u, arriba), Size(ancho - 2f * u, 1f * u))
    drawRect(FILO_MUEBLE.copy(alpha = 0.34f * k), Offset(0f, arriba + 1.5f * u), Size(0.9f * u, size.height - arriba - 1.5f * u))
}

/**
 * El códice de cordobán de pie, visto por la tapa: el cuero teñido del color del ánimo (sin teñir si no se marcó), arriba el
 * corte de las hojas, el marco gofrado con su hilera de anillos (más filetes cuanto más escribiste), el medallón redondo de piel
 * clara con el día, el mes dorado a lo largo y, a la derecha, la solapa que dobla sobre la tapa en su gran pico, con la rueda
 * dorada en la punta (en los Sueños, la luna).
 */
internal fun DrawScope.codiceAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val piel = v.tono?.let { mezcla(a.animo(it, k), a.luz(TINTA_CUERO_ANDALUS, k), 0.3f) } ?: a.luz(PIELES_ANDALUS[s % PIELES_ANDALUS.size], k)
    val hondo = mezcla(piel, a.hondo, 0.35f)
    val canto = minOf(w * 0.16f, 6f * u)
    val tapa = w - canto
    val corte = 2.6f * u
    // El corte de las hojas, arriba, y la tapa.
    drawRect(a.luz(PERGAMINO, k), Offset(1f * u, 0f), Size(tapa - 1f * u, corte + 1f * u))
    for (j in 1..2) drawLine(a.sombra(0.2f), Offset(1f * u, corte * j / 3f), Offset(tapa, corte * j / 3f), 0.4f * u)
    drawRoundRect(piel, Offset(0f, corte), Size(tapa, h - corte), CornerRadius(1.4f * u))
    // El grano del cordobán: unas manchas más hondas y las esquinas gastadas, más claras.
    for (j in 0 until 5) {
        val o = Offset(tapa * azar(s, 50 + j), corte + (h - corte) * azar(s, 60 + j))
        drawOval(mezcla(piel, a.hondo, 0.3f).copy(alpha = 0.35f), Offset(o.x - tapa * 0.14f, o.y - 1.6f * u), Size(tapa * 0.28f, 3.2f * u))
    }
    drawPath(poligono(0f, corte, 3.4f * u, corte, 0f, corte + 3.4f * u), a.claro(0.3f))
    drawPath(poligono(0f, h, 3.4f * u, h, 0f, h - 3.4f * u), a.claro(0.24f))
    // La luz a la izquierda, la sombra hacia el canto, el pie.
    drawRect(a.claro(0.24f), Offset(0.6f * u, corte), Size(w * 0.09f, h - corte))
    drawRect(a.sombra(0.16f), Offset(tapa - w * 0.12f, corte), Size(w * 0.12f, h - corte))
    // El marco gofrado: uno a tres filetes y, entre los dos de fuera, una hilera de anillos.
    val filetes = 1 + (v.lleno * 2.2f).toInt()
    val dorado = a.luz(ORO, k + 0.1f)
    for (j in 0 until filetes) {
        val m = (2.6f + j * 2f) * u
        drawRect(if (j == 0) dorado.copy(alpha = 0.8f) else hondo.copy(alpha = 0.8f), Offset(m, corte + m), Size(tapa - 2 * m, h - corte - 2 * m), style = Stroke(0.6f * u))
    }
    val ma = 3.6f * u
    val paso = maxOf(4f * u, 1f)
    var y = corte + ma + paso
    while (y < h - ma - paso * 0.5f) { drawCircle(hondo, 0.7f * u, Offset(ma, y), style = Stroke(0.4f * u)); y += paso }
    // El medallón: un disco de piel clara incrustado, con su orla dorada de puntos, y el día dentro.
    val cm = Offset(tapa * 0.5f, corte + (h - corte) * 0.26f)
    val rm = minOf(tapa * 0.42f, h * 0.15f)
    drawCircle(a.sombra(0.3f), rm + 1f * u, cm + Offset(0.6f * u, 0.8f * u))
    drawCircle(a.etiqueta(k), rm, cm)
    drawCircle(dorado, rm, cm, style = Stroke(0.8f * u))
    for (q in 0 until 8) {
        val ang = 0.785f * q
        drawCircle(dorado, 0.6f * u, Offset(cm.x + (rm + 1.6f * u) * kotlin.math.cos(ang), cm.y + (rm + 1.6f * u) * kotlin.math.sin(ang)))
    }
    textoEn(medidor, v.dia, Rect(cm.x - rm, cm.y - rm * 0.8f, cm.x + rm, cm.y + rm * 0.86f), LETRA, a.tinta, 14f, peso = FontWeight.SemiBold)
    // El mes, dorado, de abajo arriba, en la mitad de abajo (a la izquierda del pico de la solapa).
    val pluma = if (piel.luminance() > 0.32f) a.tinta.copy(alpha = 0.85f) else mezcla(dorado, PERGAMINO, 0.3f)
    val mes = Rect(w * 0.08f, cm.y + rm + 4f * u, tapa * 0.55f, h - 5f * u)
    if (mes.height > 9f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8.5f, vertical = true, minimo = 4.5f)
    // La solapa: el canto que tapa las hojas y el pico que dobla sobre la tapa.
    val solapa = mezcla(piel, a.hondo, 0.12f)
    drawRect(solapa, Offset(tapa, corte * 0.5f), Size(canto, h - corte * 0.5f))
    drawRect(a.sombra(0.35f), Offset(tapa, corte * 0.5f), Size(0.8f * u, h))
    val ap = Offset(maxOf(tapa * 0.5f, tapa - h * 0.22f), corte + (h - corte) * 0.74f)
    val pico = poligono(tapa, corte + (h - corte) * 0.5f, ap.x, ap.y, tapa, h - 1.5f * u)
    drawPath(poligono(tapa, corte + (h - corte) * 0.5f + 1.4f * u, ap.x + 0.6f * u, ap.y + 1.6f * u, tapa, h), a.sombra(0.3f))
    drawPath(pico, solapa)
    drawPath(pico, dorado.copy(alpha = 0.7f), style = Stroke(0.6f * u))
    drawLine(a.claro(0.25f), Offset(tapa, corte + (h - corte) * 0.5f), ap, 0.6f * u)
    marcaAndalus(Offset(ap.x + (tapa - ap.x) * 0.36f, ap.y), minOf((tapa - ap.x) * 0.2f, 2.8f * u), v.noche, dorado, solapa, a, k)
    drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    filoAndalus(a, k, corte, tapa)
}

/**
 * El estuche del rollo, de pie: un canuto de cuero gofrado con su tapa de bronce abovedada y su remate (en los Sueños, una
 * media luna de plata en lo alto), la virola de abajo, dos vueltas de cinta de seda del color del ánimo con su borla, la
 * etiqueta de pergamino con el día colgada del remate y el mes dorado entre las cintas. Más anillos gofrados cuanto más
 * escribiste.
 */
internal fun DrawScope.estucheAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val cuero = a.luz(if (s % 3 == 0) mezcla(CUERO, CUERO_OSC, 0.5f) else if (s % 3 == 1) CUERO else mezcla(CEDRO, CUERO_OSC, 0.3f), k)
    val bronce = a.luz(BRONCE, k + 0.1f)
    val m = w * 0.08f
    val yTapa = 15f * u
    val cuerpo = Rect(m, yTapa, w - m, h - 5f * u)
    // El canuto, redondo: la luz a la izquierda y la sombra en dos pasos a la derecha.
    drawRect(cuero, cuerpo.topLeft, cuerpo.size)
    drawRect(a.claro(0.3f), Offset(cuerpo.left + cuerpo.width * 0.12f, cuerpo.top), Size(cuerpo.width * 0.14f, cuerpo.height))
    drawRect(a.sombra(0.16f), Offset(cuerpo.left + cuerpo.width * 0.62f, cuerpo.top), Size(cuerpo.width * 0.38f, cuerpo.height))
    drawRect(a.sombra(0.22f), Offset(cuerpo.right - cuerpo.width * 0.16f, cuerpo.top), Size(cuerpo.width * 0.16f, cuerpo.height))
    // Los anillos gofrados.
    val anillos = 1 + (v.lleno * 3f).toInt()
    val dorado = a.luz(ORO, k + 0.1f)
    for (j in 0 until anillos) {
        val yy = cuerpo.bottom - 10f * u - j * 3.2f * u
        drawLine(dorado.copy(alpha = 0.75f), Offset(cuerpo.left, yy), Offset(cuerpo.right, yy + 0.6f * u), 0.6f * u)
    }
    // La cinta de seda: dos vueltas, con su nudo y su borla.
    val cinta = v.tono?.let { a.animo(it, k) } ?: a.luz(mezcla(CARMESI, CUERO, 0.2f), k)
    val g = 7f * u
    val ys = listOf(h * 0.4f, h * 0.68f)
    for (yc in ys) {
        drawRect(a.sombra(0.3f), Offset(cuerpo.left, yc + g), Size(cuerpo.width, 1.1f * u))
        drawRect(cinta, Offset(cuerpo.left - 0.5f * u, yc), Size(cuerpo.width + 1f * u, g))
        drawRect(a.claro(0.3f), Offset(cuerpo.left, yc), Size(cuerpo.width * 0.6f, 0.8f * u))
        drawRect(a.sombra(0.22f), Offset(cuerpo.right - cuerpo.width * 0.3f, yc), Size(cuerpo.width * 0.3f, g))
        drawLine(mezcla(cinta, a.hondo, 0.3f), Offset(cuerpo.left, yc + g * 0.5f), Offset(cuerpo.right, yc + g * 0.55f), 0.5f * u)
    }
    val nudo = Offset(cuerpo.left + cuerpo.width * 0.3f, ys[1] + g * 0.5f)
    drawLine(cinta, nudo, Offset(nudo.x - 1.5f * u, ys[1] + g + 9f * u), 1.6f * u, StrokeCap.Round)
    drawOval(cinta, Offset(nudo.x - 1.8f * u, ys[1] + g + 8f * u), Size(3.6f * u, 6f * u))
    for (q in -1..1) drawLine(mezcla(cinta, a.hondo, 0.35f), Offset(nudo.x + q * 0.9f * u - 0.2f * u, ys[1] + g + 10f * u), Offset(nudo.x + q * 1.1f * u - 0.2f * u, ys[1] + g + 14f * u), 0.5f * u)
    drawCircle(mezcla(cinta, a.hondo, 0.25f), 2f * u, nudo)
    // El mes, dorado entre las dos cintas.
    val pluma = if (cuero.luminance() > 0.3f) a.tinta.copy(alpha = 0.85f) else mezcla(dorado, PERGAMINO, 0.3f)
    val mes = Rect(cuerpo.left + 1f * u, ys[0] + g + 3f * u, cuerpo.right - 1f * u, ys[1] - 3f * u)
    if (mes.height > 9f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8.5f, vertical = true, minimo = 4.5f)
    // La virola de abajo y la tapa abovedada de bronce, con su remate.
    drawRect(bronce, Offset(cuerpo.left - 0.5f * u, cuerpo.bottom - 4f * u), Size(cuerpo.width + 1f * u, 4f * u))
    drawRect(a.claro(0.4f), Offset(cuerpo.left, cuerpo.bottom - 4f * u), Size(cuerpo.width, 0.8f * u))
    drawRect(a.luz(mezcla(CUERO_OSC, CEDRO_OSC, 0.5f), k), Offset(cuerpo.left + 1f * u, cuerpo.bottom), Size(cuerpo.width - 2f * u, h - cuerpo.bottom))
    val tapa = Path().apply {
        moveTo(m - 1f * u, yTapa + 1f * u)
        lineTo(m - 1f * u, yTapa - 3f * u)
        cubicTo(m - 1f * u, yTapa - 8.5f * u, w - m + 1f * u, yTapa - 8.5f * u, w - m + 1f * u, yTapa - 3f * u)
        lineTo(w - m + 1f * u, yTapa + 1f * u)
        close()
    }
    drawPath(tapa, bronce)
    drawRect(a.sombra(0.3f), Offset(m, yTapa + 1f * u), Size(w - 2 * m, 1.2f * u))
    drawRect(a.claro(0.4f), Offset(m + (w - 2 * m) * 0.14f, yTapa - 6f * u), Size((w - 2 * m) * 0.14f, 6f * u))
    drawRect(a.sombra(0.24f), Offset(w - m - (w - 2 * m) * 0.3f, yTapa - 5f * u), Size((w - 2 * m) * 0.3f, 6f * u))
    val remate = Offset(w * 0.5f, 3.6f * u)
    if (v.noche) {
        drawCircle(if (a.oscuro) mezcla(LUNA, a.hondo, 0.2f - 0.2f * k) else LUNA, 3.3f * u, remate)
        drawCircle(a.luz(mezcla(CEDRO_OSC, ANIL, 0.3f), k), 2.7f * u, remate + Offset(1.5f * u, -0.9f * u))
    } else {
        drawLine(bronce, Offset(remate.x, remate.y + 2f * u), Offset(remate.x, yTapa - 6f * u), 1.2f * u)
        drawCircle(bronce, 2.2f * u, remate)
        drawCircle(a.claro(0.5f), 0.7f * u, remate - Offset(0.7f * u, 0.7f * u))
    }
    // La etiqueta con el día, colgada del remate por un cordel y caída sobre el canuto.
    val et = Rect(w * 0.1f, yTapa + 3f * u, w * 0.9f, yTapa + 21f * u)
    val lado = if (azar(v.semilla, 8) < 0.5f) -1f else 1f
    val ojal = Offset(et.center.x + lado * et.width * 0.2f, et.top + 2f * u)
    drawLine(a.luz(mezcla(CUERO, PERGAMINO, 0.4f), k), Offset(remate.x, remate.y + 2.5f * u), ojal, 0.7f * u)
    rotate(lado * (3f + 3f * azar(v.semilla, 9)), ojal) {
        drawRect(a.sombra(0.42f), et.topLeft + Offset(0.9f * u, 1.2f * u), et.size)
        drawRoundRect(a.etiqueta(k), et.topLeft, et.size, CornerRadius(1.2f * u))
        drawRect(a.sombra(0.12f), Offset(et.right - et.width * 0.18f, et.top), Size(et.width * 0.18f, et.height))
        drawCircle(a.luz(CUERO, k), 0.9f * u, ojal)
        textoEn(medidor, v.dia, Rect(et.left + 0.6f * u, et.top + 1.8f * u, et.right - 0.6f * u, et.bottom - 0.3f * u), LETRA, a.tinta, 14f, peso = FontWeight.SemiBold)
    }
    if (a.oscuro && k >= 0.1f) {
        drawRect(FILO_MUEBLE.copy(alpha = 0.34f * k), Offset(cuerpo.left, cuerpo.top), Size(0.9f * u, cuerpo.height))
        drawPath(tapa, FILO_MUEBLE.copy(alpha = 0.3f * k), style = Stroke(0.8f * u))
    }
}

/**
 * El legajo de hojas sueltas, más bajo: las hojas entre dos tablas finas de álamo, atadas con una cinta de seda del color del
 * ánimo que da la vuelta y se anuda en lazada. Por arriba asoman los cantos de las hojas, desparejos (más cuanto más
 * escribiste); bajo la cinta va metida la papeleta con el día y, en la tabla, el mes a tinta y la rueda (en los Sueños, la luna).
 */
internal fun DrawScope.legajoAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val hojas = 2 + (v.lleno * 4.4f).toInt()
    val asoma = minOf(h * 0.16f, 3f * u + hojas * 1.6f * u)
    // Las hojas que asoman, cada una a su altura y de su tono.
    for (j in 0 until hojas) {
        val dy = asoma * (j.toFloat() / hojas) + asoma * 0.3f * azar(s, 20 + j) - asoma * 0.15f
        val dx = (azar(s, 30 + j) - 0.5f) * 3f * u
        val tono = a.luz(mezcla(PERGAMINO, if (j % 3 == 0) CUERO else PINO, 0.08f + 0.14f * azar(s, 40 + j)), k + 0.05f)
        drawRect(tono, Offset(1.5f * u + dx, dy.coerceAtLeast(0f)), Size(w - 3f * u, asoma + 2f * u))
        drawRect(a.sombra(0.16f), Offset(1.5f * u + dx, dy.coerceAtLeast(0f)), Size(w - 3f * u, 0.6f * u))
    }
    // La tabla de delante, de álamo claro, con su veta.
    val tabla = Rect(0f, asoma, w, h)
    val alamo = a.luz(mezcla(PINO, CEDRO, 0.3f + 0.2f * azar(s, 3)), k)
    drawRoundRect(alamo, tabla.topLeft, tabla.size, CornerRadius(1.2f * u))
    vetas(tabla, mezcla(CEDRO, CEDRO_OSC, 0.5f).copy(alpha = if (a.oscuro) 0.3f else 0.4f), s % 97, densidad = 0.8f, vertical = true, grosor = 0.5f, ondulacion = 0.5f)
    drawRect(a.claro(0.3f), Offset(0.6f * u, tabla.top), Size(w * 0.1f, tabla.height))
    drawRect(a.sombra(0.18f), Offset(w * 0.86f, tabla.top), Size(w * 0.14f, tabla.height))
    drawRect(a.claro(0.4f), Offset(1f * u, tabla.top), Size(w - 2f * u, 0.8f * u))
    // La papeleta con el día, metida bajo la cinta.
    val g = 7.5f * u
    val yc = maxOf(tabla.top + tabla.height * 0.62f, h - g - 5f * u)
    val pap = Rect(w * 0.1f, tabla.top + 3f * u, w * 0.9f, minOf(tabla.top + 20f * u, yc - 2f * u))
    drawRect(a.sombra(0.35f), pap.topLeft + Offset(0.8f * u, 1f * u), pap.size)
    drawRect(a.etiqueta(k), pap.topLeft, pap.size)
    textoEn(medidor, v.dia, Rect(pap.left + 0.5f * u, pap.top + 0.8f * u, pap.right - 0.5f * u, minOf(pap.bottom, yc) - 0.5f * u), LETRA, a.tinta, 14f, peso = FontWeight.SemiBold)
    // El mes, a tinta en la tabla, entre la papeleta y la cinta.
    val mes = Rect(w * 0.1f, pap.bottom + 2f * u, w * 0.6f, yc - 1.5f * u)
    if (mes.height > 8f * u) textoEn(medidor, v.mes, mes, LETRA, a.tinta.copy(alpha = 0.9f), 8f, vertical = true, minimo = 4.5f)
    // La cinta, que da la vuelta, y la lazada con sus dos cabos.
    val cinta = v.tono?.let { a.animo(it, k) } ?: a.luz(mezcla(ANIL, CUERO, 0.2f), k)
    drawRect(a.sombra(0.32f), Offset(0f, yc + g), Size(w, 1.1f * u))
    drawRect(cinta, Offset(0f, yc), Size(w, g))
    drawRect(a.claro(0.3f), Offset(0f, yc), Size(w * 0.6f, 0.8f * u))
    drawRect(a.sombra(0.2f), Offset(w * 0.8f, yc), Size(w * 0.2f, g))
    val lazo = Offset(w * 0.66f, yc + g * 0.5f)
    val ojo = minOf(w * 0.2f, 5f * u)
    for (l in listOf(-1f, 1f)) {
        val lazada = Path().apply {
            moveTo(lazo.x, lazo.y)
            cubicTo(lazo.x + l * ojo * 0.6f, lazo.y - ojo * 1.2f, lazo.x + l * ojo * 1.5f, lazo.y - ojo * 0.6f, lazo.x + l * ojo * 1.1f, lazo.y + ojo * 0.05f)
            close()
        }
        drawPath(lazada, cinta)
        drawPath(lazada, mezcla(cinta, a.hondo, 0.35f), style = Stroke(0.5f * u))
        drawLine(cinta, lazo, Offset(lazo.x + l * ojo * 0.5f, minOf(lazo.y + ojo * 2.2f, h - 1.5f * u)), 1.8f * u, StrokeCap.Round)
    }
    drawCircle(mezcla(cinta, a.hondo, 0.25f), 1.6f * u, lazo)
    // La marca, abajo a la derecha.
    marcaAndalus(Offset(w * 0.78f, (pap.bottom + yc) / 2), minOf(w * 0.12f, 2.6f * u), v.noche, a.tinta.copy(alpha = 0.7f), alamo, a, k)
    drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    filoAndalus(a, k, asoma, w)
}
