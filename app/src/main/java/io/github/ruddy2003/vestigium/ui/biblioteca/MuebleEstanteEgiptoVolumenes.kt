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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.ACACIA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.BARRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.FIBRA
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.PAPIRO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.EgiptoTinta.YESO
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.FILO_MUEBLE

// Los volúmenes del almacén de la Tumba (MuebleEstanteEgipto): tres cosas del escriba, según la semilla.

/** La luna de los Sueños: la plata pálida del creciente. */
private val LUNA_EGIPTO = Color(0xFFEDE8D0)

internal fun DrawScope.volumenEgipto(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
    if (size.width < 2f || size.height < 2f) return
    val a = AireEgipto(oscuro, v.noche)
    val (k, f) = luzVolumenEgipto(v, oscuro)
    val m = MuebleEstanteEgipto
    val fx = m.LADO + v.centro * (1f - 2 * m.LADO)
    // De dónde viene la luz: de día, de la puerta (izquierda); de noche, de la lámpara del suelo (a la derecha de casi todo).
    val hacia = if (a.lumbre) (if (m.LAMP_X >= fx) 1f else -1f) else -1f
    when (m.clase(v.semilla)) {
        0 -> rolloEgipto(v, medidor, a, k, f, hacia)
        1 -> tablillaEgipto(v, medidor, a, k, f, hacia)
        else -> fardoEgipto(v, medidor, a, k, f, hacia)
    }
}

/** El cuerpo de un volumen con su bulto: el claro del lado de la luz y la sombra del otro, y los filos de noche. */
private fun DrawScope.bultoEgipto(a: AireEgipto, k: Float, f: Float, hacia: Float, arriba: Float, abajo: Float, x0: Float = 0f, x1: Float = size.width) {
    val u = density
    val w = x1 - x0
    val alto = abajo - arriba
    val luzIzq = !a.oscuro || hacia < 0f
    drawRect(a.claro(0.26f), Offset(x0 + (if (luzIzq) w * 0.12f else w * 0.66f), arriba), Size(w * 0.18f, alto))
    drawRect(a.sombra(0.15f), Offset(x0 + (if (luzIzq) w * 0.62f else 0f), arriba), Size(w * 0.38f, alto))
    drawRect(a.sombra(0.2f), Offset(x0 + (if (luzIzq) w * 0.86f else 0f), arriba), Size(w * 0.14f, alto))
    if (a.oscuro) {
        if (k > 0.08f) drawRect(FILO_MUEBLE.copy(alpha = (0.5f * k).coerceAtMost(0.5f)), Offset(if (hacia > 0) x1 - 1.1f * u else x0, arriba), Size(1.1f * u, alto))
        if (f > 0.08f) drawRect(a.helado(0.45f * f), Offset(if (hacia > 0) x0 else x1 - 1f * u, arriba), Size(1f * u, alto))
    }
}

/** El color del cuerpo: el del ánimo si lo hay; si no, el del material crudo. */
private fun cuerpoEgipto(v: Volumen, a: AireEgipto, k: Float, f: Float, crudo: Color): Color =
    v.tono?.let { a.animo(it, k, f) } ?: a.luz(crudo, k, f)

/** Una tira de papiro pegada con el texto dentro: el día (horizontal) o el mes (en rojo, a lo largo). */
private fun DrawScope.tiraEgipto(r: Rect, texto: String, medidor: TextMeasurer, a: AireEgipto, k: Float, mes: Boolean, semilla: Int) {
    val u = density
    if (r.width < 3f || r.height < 3f) return
    drawRect(a.sombra(0.35f), Offset(r.left + 0.8f * u, r.top + 1f * u), r.size)
    drawRect(a.etiqueta(k, PAPIRO), r.topLeft, r.size)
    // Las fibras del papiro, a lo largo, y el borde arrancado de la derecha.
    val fibras = Path()
    var y = r.top + 1.6f * u
    while (y < r.bottom - 0.5f * u) { fibras.moveTo(r.left, y); fibras.lineTo(r.right, y + 0.3f * u); y += 2.4f * u }
    drawPath(fibras, a.luz(PAPIRO_OSC, k).copy(alpha = 0.22f), style = Stroke(0.5f * u))
    drawRect(a.sombra(0.18f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
    drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
    if (mes) textoEn(medidor, texto, r.deflate(0.8f * u), MuebleEstanteEgipto.LETRA, a.rubrica, 8.5f, vertical = r.height > r.width, peso = FontWeight.SemiBold, minimo = 4.5f)
    else textoEn(medidor, texto, Rect(r.left + 0.5f * u, r.top + 0.3f * u, r.right - 0.5f * u, r.bottom), MuebleEstanteEgipto.LETRA, a.tinta, 13f, peso = FontWeight.Bold, minimo = 6f)
}

/** El sello de barro con el cordel; en los Sueños, la luna de plata en su lugar. */
private fun DrawScope.selloEgipto(c: Offset, rs: Float, luna: Boolean, a: AireEgipto, k: Float) {
    val u = density
    drawOval(a.sombra(0.4f), Offset(c.x - rs + 0.8f * u, c.y - rs * 0.8f + 1f * u), Size(rs * 2f, rs * 1.7f))
    if (luna) {
        val plata = if (a.oscuro) mezcla(LUNA_EGIPTO, a.hondo, 0.12f - 0.12f * k) else LUNA_EGIPTO
        drawCircle(a.luz(EgiptoTinta.TINTA, 0.6f), rs * 1.02f, c)
        drawCircle(plata, rs * 0.86f, c)
        drawCircle(a.luz(EgiptoTinta.TINTA, 0.6f), rs * 0.72f, c + Offset(rs * 0.34f, -rs * 0.2f))
        return
    }
    val barro = a.luz(BARRO, maxOf(k, 0.2f))
    drawOval(barro, Offset(c.x - rs, c.y - rs * 0.85f), Size(rs * 2f, rs * 1.7f))
    drawArc(a.claro(0.45f), 170f, 110f, false, Offset(c.x - rs * 0.8f, c.y - rs * 0.7f), Size(rs * 1.6f, rs * 1.4f), style = Stroke(0.7f * u))
    // La impronta: un óvalo hundido con un trazo, sin signos.
    drawOval(mezcla(barro, a.hondo, 0.4f), Offset(c.x - rs * 0.5f, c.y - rs * 0.4f), Size(rs, rs * 0.8f), style = Stroke(0.7f * u))
    drawLine(a.luz(FIBRA, k), Offset(c.x - rs * 0.3f, c.y + rs * 0.7f), Offset(c.x - rs * 0.6f, c.y + rs * 2f), 0.8f * u)
    drawLine(a.luz(FIBRA, k), Offset(c.x + rs * 0.2f, c.y + rs * 0.7f), Offset(c.x + rs * 0.5f, c.y + rs * 1.8f), 0.8f * u)
}

/**
 * El rollo de papiro de pie, metido en su funda de lino teñido (el color del ánimo): arriba asoma el papiro con las vueltas
 * del rollo (más cuanto más escribiste); en la funda, la tira de papiro con el día, el cordel con su sello de barro (en los
 * Sueños, la luna) y, abajo, la tira con el mes en rojo, como se escribían las fechas.
 */
private fun DrawScope.rolloEgipto(v: Volumen, medidor: TextMeasurer, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val e = minOf(w * 0.34f, 7f * u)
    val papiro = a.luz(mezcla(PAPIRO, PAPIRO_OSC, 0.25f * azar(v.semilla, 7)), k, f)
    // El papiro: el cilindro entero, con su elipse abajo.
    drawRect(papiro, Offset(0f, e / 2), Size(w, h - e))
    drawOval(papiro, Offset(0f, h - e), Size(w, e))
    // La funda de lino, con su borde de arriba doblado y desigual.
    val ini = h * (0.15f + 0.03f * azar(v.semilla, 8))
    val fin = h * 0.93f
    val funda = Path().apply {
        moveTo(0f, ini + 2f * u)
        quadraticTo(w * 0.3f, ini - 1.5f * u, w * 0.55f, ini + 1f * u)
        quadraticTo(w * 0.8f, ini + 2.5f * u, w, ini)
        lineTo(w, fin); quadraticTo(w / 2, fin + e * 0.5f, 0f, fin); close()
    }
    val tela = cuerpoEgipto(v, a, k, f, LINO)
    drawPath(funda, tela)
    clipPath(funda) {
        // La trama del lino y dos pliegues.
        val trama = Path()
        var y = ini
        while (y < fin) { trama.moveTo(0f, y); trama.lineTo(w, y + 0.6f * u); y += 2.6f * u }
        drawPath(trama, a.sombra(0.08f), style = Stroke(0.5f * u))
        drawLine(a.sombra(0.22f), Offset(w * 0.3f, ini + 3f * u), Offset(w * 0.42f, h * 0.6f), 1f * u)
        drawLine(a.claro(0.18f), Offset(w * 0.27f, ini + 3f * u), Offset(w * 0.38f, h * 0.6f), 0.7f * u)
    }
    drawPath(Path().apply { moveTo(0f, ini + 2f * u); quadraticTo(w * 0.3f, ini - 1.5f * u, w * 0.55f, ini + 1f * u); quadraticTo(w * 0.8f, ini + 2.5f * u, w, ini) },
        a.claro(0.4f), style = Stroke(1.2f * u))
    bultoEgipto(a, k, f, hacia, e / 2, h - e / 2)
    // Arriba, las vueltas del rollo: más cuanto más escribiste.
    drawOval(a.luz(PAPIRO_OSC, k, f), Offset.Zero, Size(w, e))
    val vueltas = 2 + (v.lleno * 4).toInt()
    for (j in 1..vueltas) {
        val q = 1f - j / (vueltas + 1f)
        drawOval(papiro, Offset(w * (1 - q) / 2, e * (1 - q) / 2), Size(w * q, e * q), style = Stroke(0.6f * u))
    }
    drawOval(a.claro(0.3f), Offset(w * 0.08f, e * 0.1f), Size(w * 0.5f, e * 0.4f), style = Stroke(0.6f * u))
    // La sombra del tablón de arriba en la cabeza del rollo.
    drawRect(a.sombra(0.16f), Offset.Zero, Size(w, h * 0.05f))
    // El día, en una tira de papiro pegada a la funda.
    tiraEgipto(Rect(w * 0.08f, h * 0.22f, w * 0.92f, h * 0.22f + minOf(h * 0.2f, 22f * u)), v.dia, medidor, a, k, false, v.semilla)
    // El cordel con su sello (de noche, la luna).
    val yc = h * 0.55f
    drawLine(a.sombra(0.3f), Offset(0f, yc + 1.6f * u), Offset(w, yc + 2.2f * u), 1.4f * u)
    drawLine(a.luz(FIBRA, k, f), Offset(0f, yc), Offset(w, yc + 0.8f * u), 1.3f * u)
    drawLine(a.luz(FIBRA, k, f), Offset(0f, yc + 2.4f * u), Offset(w, yc + 3f * u), 1.1f * u)
    selloEgipto(Offset(w * 0.5f, yc + 1.4f * u), minOf(w * 0.2f, 5f * u), v.noche, a, k)
    // El mes, en rojo, en su tira.
    val mw = minOf(w * 0.44f, 12f * u)
    tiraEgipto(Rect(w * 0.5f - mw / 2, h * 0.66f, w * 0.5f + mw / 2, h * 0.9f), v.mes, medidor, a, k, true, v.semilla + 3)
}

/**
 * La tablilla de escriba: una tabla de madera pintada (el color del ánimo) con la cara de yeso donde se escribe, de pie y un
 * poco ladeada, colgada de su cordel por el agujero. Con más palabras asoman detrás otras tablillas. En el yeso, la
 * cuadrícula roja de las prácticas, el día en negro y el mes en rojo; en los Sueños, el creciente pintado bajo el día.
 */
private fun DrawScope.tablillaEgipto(v: Volumen, medidor: TextMeasurer, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val giro = (azar(v.semilla, 11) - 0.5f) * 4f
    val pintura = cuerpoEgipto(v, a, k, f, ACACIA)
    val canto = mezcla(pintura, a.hondo, if (a.oscuro) 0.3f else 0.35f)
    rotate(giro, Offset(w / 2, h)) {
        // Las otras tablillas, detrás (más cuantas más palabras).
        val detras = (v.lleno * 3).toInt().coerceIn(0, 2)
        for (j in detras downTo 1) {
            val dx = j * 2.2f * u
            drawRect(a.sombra(0.3f), Offset(w * 0.08f + dx + 1f * u, h * 0.08f - j * 1.6f * u), Size(w * 0.84f - dx, h * 0.92f + j * 1.6f * u))
            drawRect(mezcla(pintura, a.hondo, 0.2f + 0.12f * j), Offset(w * 0.08f + dx, h * 0.08f - j * 2f * u), Size(w * 0.84f - dx, h * 0.92f + j * 2f * u))
        }
        val tabla = Rect(w * 0.06f, h * 0.08f, w * 0.88f, h)
        // El canto de la tabla, a la derecha, y su sombra.
        drawRect(a.sombra(0.35f), Offset(tabla.right, tabla.top + 2f * u), Size(w * 0.06f, tabla.height - 2f * u))
        drawRect(canto, Offset(tabla.right - 0.5f * u, tabla.top + 1f * u), Size(w * 0.08f, tabla.height - 1f * u))
        drawRoundRect(pintura, tabla.topLeft, tabla.size, CornerRadius(1.6f * u))
        // La pintura dada a brochazos verticales, gastada en las esquinas.
        val brochazos = Path()
        for (j in 0 until 6) {
            val x = tabla.left + tabla.width * (0.08f + 0.16f * j + 0.04f * azar(v.semilla, j))
            brochazos.moveTo(x, tabla.top + 2f * u); brochazos.lineTo(x + 0.6f * u, tabla.bottom - 2f * u)
        }
        drawPath(brochazos, a.sombra(0.1f), style = Stroke(1.6f * u))
        drawCircle(a.luz(ACACIA, k, f).copy(alpha = 0.5f), 2.4f * u, Offset(tabla.left + 1.6f * u, tabla.bottom - 2f * u))
        bultoEgipto(a, k, f, hacia, tabla.top, tabla.bottom, tabla.left, tabla.right)
        drawRect(a.claro(0.35f), tabla.topLeft, Size(tabla.width, 1f * u))
        // El agujero y el cordel que sube.
        val ag = Offset(tabla.center.x, tabla.top + minOf(h * 0.05f, 5f * u))
        drawPath(Path().apply { moveTo(ag.x - 1.4f * u, ag.y); quadraticTo(ag.x - 3f * u, 0f, ag.x + 1f * u, 0.5f * u); quadraticTo(ag.x + 4f * u, 1f * u, ag.x + 1.4f * u, ag.y) },
            a.luz(FIBRA, k, f), style = Stroke(1f * u))
        drawCircle(a.luz(EgiptoTinta.TINTA, 0.3f), 1.5f * u, ag)
        // La cara de yeso, un poco teñida por la pintura que asoma.
        val yeso = Rect(tabla.left + tabla.width * 0.2f, ag.y + 4f * u, tabla.right - tabla.width * 0.18f, tabla.bottom - tabla.height * 0.26f)
        if (yeso.width > 4f && yeso.height > 10f) {
            drawRect(a.etiqueta(k, mezcla(YESO, v.tono ?: ACACIA, 0.1f)), yeso.topLeft, yeso.size)
            drawRect(a.sombra(0.18f), Offset(yeso.right - 1f * u, yeso.top), Size(1f * u, yeso.height))
            drawRect(a.sombra(0.12f), Offset(yeso.left, yeso.bottom - 1f * u), Size(yeso.width, 1f * u))
            // La cuadrícula roja de las prácticas, borrada a medias.
            val rejilla = Path()
            var gx = yeso.left + 3.2f * u
            while (gx < yeso.right - 1f) { rejilla.moveTo(gx, yeso.top + yeso.height * 0.45f); rejilla.lineTo(gx, yeso.bottom - 1f * u); gx += 3.2f * u }
            var gy = yeso.top + yeso.height * 0.45f
            while (gy < yeso.bottom - 1f) { rejilla.moveTo(yeso.left + 1f * u, gy); rejilla.lineTo(yeso.right - 1f * u, gy); gy += 3.2f * u }
            drawPath(rejilla, a.rubrica.copy(alpha = 0.18f), style = Stroke(0.45f * u))
            val dia = Rect(yeso.left, yeso.top + 0.5f * u, yeso.right, yeso.top + minOf(yeso.height * 0.32f, 20f * u))
            textoEn(medidor, v.dia, dia, MuebleEstanteEgipto.LETRA, a.tinta, 13f, peso = FontWeight.Bold, minimo = 6f)
            if (v.noche) {
                val c = Offset(yeso.center.x, dia.bottom + 4f * u)
                val r = minOf(yeso.width * 0.28f, 4.2f * u)
                drawCircle(a.luz(EgiptoTinta.FAYENZA, 0.7f), r, c)
                drawCircle(a.etiqueta(k, mezcla(YESO, v.tono ?: ACACIA, 0.1f)), r * 0.82f, c + Offset(r * 0.42f, -r * 0.18f))
            }
            val mesY = dia.bottom + (if (v.noche) 9f * u else 2f * u)
            textoEn(medidor, v.mes, Rect(yeso.left + 0.5f * u, mesY, yeso.right - 0.5f * u, yeso.bottom - 1.5f * u), MuebleEstanteEgipto.LETRA, a.rubrica, 8.5f,
                vertical = true, peso = FontWeight.SemiBold, minimo = 4.5f)
        }
        drawRect(a.sombra(0.14f), Offset(tabla.left, tabla.top), Size(tabla.width, h * 0.04f))
    }
}

/**
 * El fardo: rollos y hojas envueltos en un paño de lino teñido (el color del ánimo), anudado arriba con sus dos orejas y
 * vencido por su peso; más lleno y redondo cuanto más escribiste, con las puntas de los rollos asomando por el nudo. Del nudo
 * cuelga la tablilla de madera con el día, y el nudo lleva su sello de barro (en los Sueños, la luna); el mes, en rojo, en
 * una tira de papiro cosida al paño.
 */
private fun DrawScope.fardoEgipto(v: Volumen, medidor: TextMeasurer, a: AireEgipto, k: Float, f: Float, hacia: Float) {
    val u = density
    val w = size.width; val h = size.height
    val panza = 0.02f + 0.06f * v.lleno
    val cuello = h * 0.16f
    val cx = w * (0.48f + 0.06f * (azar(v.semilla, 13) - 0.5f))
    // Las puntas de los rollos que asoman por el nudo.
    val puntas = 1 + (v.lleno * 2.5f).toInt().coerceAtMost(2)
    for (j in 0 until puntas) {
        val x = cx + (j - (puntas - 1) / 2f) * 4.2f * u
        val tope = h * (0.02f + 0.04f * j)
        drawRect(a.luz(PAPIRO, k, f), Offset(x - 1.8f * u, tope), Size(3.6f * u, cuello - tope))
        drawOval(a.luz(PAPIRO_OSC, k, f), Offset(x - 1.8f * u, tope - 1f * u), Size(3.6f * u, 2f * u))
    }
    val bulto = Path().apply {
        moveTo(cx - w * 0.1f, cuello)
        quadraticTo(w * (0.0f - panza), h * 0.24f, w * 0.02f, h * 0.7f)
        quadraticTo(w * 0.03f, h, w * 0.2f, h)
        lineTo(w * 0.82f, h)
        quadraticTo(w * 0.99f, h, w * 0.98f, h * 0.7f)
        quadraticTo(w * (1.0f + panza), h * 0.26f, cx + w * 0.1f, cuello)
        close()
    }
    val tela = cuerpoEgipto(v, a, k, f, LINO)
    drawPath(bulto, tela)
    clipPath(bulto) {
        // Los pliegues que bajan del nudo y la trama.
        val pliegues = Path().apply {
            moveTo(cx - 1f * u, cuello); quadraticTo(w * 0.22f, h * 0.5f, w * 0.14f, h * 0.95f)
            moveTo(cx + 1f * u, cuello); quadraticTo(w * 0.7f, h * 0.45f, w * 0.86f, h * 0.9f)
            moveTo(cx, cuello + 2f * u); quadraticTo(cx + w * 0.05f, h * 0.6f, cx - w * 0.04f, h * 0.98f)
        }
        drawPath(pliegues, a.sombra(0.24f), style = Stroke(1.6f * u))
        drawPath(corridoEgipto(pliegues, -1.2f * u), a.claro(0.2f), style = Stroke(0.8f * u))
        val trama = Path()
        var y = cuello
        while (y < h) { trama.moveTo(0f, y); trama.lineTo(w, y + 0.5f * u); y += 2.6f * u }
        drawPath(trama, a.sombra(0.07f), style = Stroke(0.5f * u))
        bultoEgipto(a, k, f, hacia, cuello, h)
        // La sombra del suelo en el pie del fardo, que se aplasta.
        drawRect(a.sombra(0.2f), Offset(0f, h * 0.92f), Size(w, h * 0.08f))
    }
    // El nudo con sus dos orejas.
    val orejas = Path().apply {
        moveTo(cx, cuello + 1f * u)
        quadraticTo(cx - w * 0.3f, cuello - h * 0.1f, cx - w * 0.34f, cuello - h * 0.03f)
        quadraticTo(cx - w * 0.18f, cuello + h * 0.02f, cx, cuello + 1f * u)
        quadraticTo(cx + w * 0.24f, cuello - h * 0.12f, cx + w * 0.3f, cuello - h * 0.06f)
        quadraticTo(cx + w * 0.16f, cuello + h * 0.01f, cx, cuello + 1f * u)
    }
    drawPath(orejas, mezcla(tela, a.hondo, 0.08f))
    drawPath(orejas, a.sombra(0.3f), style = Stroke(0.7f * u))
    drawOval(mezcla(tela, a.hondo, 0.18f), Offset(cx - 3.6f * u, cuello - 2.2f * u), Size(7.2f * u, 5f * u))
    selloEgipto(Offset(cx + 2.5f * u, cuello + 1.5f * u), minOf(w * 0.16f, 3.8f * u), v.noche, a, k)
    // La tablilla de madera con el día, colgada del nudo.
    val ta = minOf(w * 0.76f, 26f * u)
    val tt = Rect(cx - ta / 2, h * 0.26f, cx + ta / 2, h * 0.26f + minOf(h * 0.22f, 18f * u))
    drawLine(a.luz(FIBRA, k, f), Offset(cx, cuello + 2f * u), Offset(cx, tt.top + 1.5f * u), 0.8f * u)
    drawRoundRect(a.sombra(0.38f), Offset(tt.left + 1f * u, tt.top + 1.2f * u), tt.size, CornerRadius(1.6f * u))
    drawRoundRect(a.luz(ACACIA, k, f), tt.topLeft, tt.size, CornerRadius(1.6f * u))
    val cara = tt.deflate(1.1f * u)
    drawRoundRect(a.etiqueta(k, YESO), cara.topLeft, cara.size, CornerRadius(1f * u))
    drawCircle(a.luz(EgiptoTinta.TINTA, 0.3f), 0.9f * u, Offset(cx, tt.top + 1.6f * u))
    textoEn(medidor, v.dia, Rect(cara.left, cara.top + 1.6f * u, cara.right, cara.bottom), MuebleEstanteEgipto.LETRA, a.tinta, 12f, peso = FontWeight.Bold, minimo = 6f)
    // El mes, en su tira cosida.
    val mw = minOf(w * 0.4f, 11f * u)
    val mt = Rect(cx - mw / 2, tt.bottom + 3f * u, cx + mw / 2, h * 0.9f)
    if (mt.height > 8f * u) {
        tiraEgipto(mt, v.mes, medidor, a, k, true, v.semilla + 5)
        // Las puntadas que la sujetan.
        for (j in 0 until 3) drawLine(a.luz(FIBRA, k, f), Offset(mt.left - 1f * u, mt.top + mt.height * (0.15f + 0.35f * j)), Offset(mt.left + 1f * u, mt.top + mt.height * (0.15f + 0.35f * j) + 1f * u), 0.7f * u)
    }
    if (a.oscuro && k > 0.1f) drawPath(orejas, a.filo(0.4f * k), style = Stroke(0.8f * u, cap = StrokeCap.Round))
}
