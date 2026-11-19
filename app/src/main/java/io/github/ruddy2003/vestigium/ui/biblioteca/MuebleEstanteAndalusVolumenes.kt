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
 * El color del cuerpo de una entrada: el del ánimo entero (el cuero o la seda teñidos), apenas llevado hacia lo hondo para que
 * tenga cuerpo de cuero sin agrisarse; sin ánimo, el tono [natural] del material.
 */
private fun cuerpoAndalus(v: Volumen, a: AireAndalus, k: Float, natural: Color): Color {
    // Cada pieza tiñe un poco distinto: los vecinos del mismo ánimo no salen iguales.
    val s = v.semilla and 0x7fffffff
    val hacia = if (azar(s, 91) < 0.5f) TINTA_CUERO_ANDALUS else CUERO
    return v.tono?.let { a.animo(mezcla(it, hacia, 0.05f + 0.16f * azar(s, 92)), k) } ?: a.luz(natural, k)
}

/** El roce del uso en los cantos de un rectángulo: trechos más claros donde se gasta el cuero, no un filete entero. */
private fun DrawScope.roceAndalus(r: Rect, a: AireAndalus, semilla: Int) {
    val u = density
    val p = Path()
    for (q in 0 until 4) {
        val t = azar(semilla, 100 + q); val l = 0.12f + 0.2f * azar(semilla, 110 + q)
        when (q) {
            0 -> { p.moveTo(r.left + r.width * t * 0.6f, r.top + 0.6f * u); p.lineTo(r.left + r.width * minOf(1f, t * 0.6f + l), r.top + 0.6f * u) }
            1 -> { p.moveTo(r.left + 0.6f * u, r.top + r.height * t * 0.7f); p.lineTo(r.left + 0.6f * u, r.top + r.height * minOf(1f, t * 0.7f + l)) }
            2 -> { p.moveTo(r.left + r.width * t * 0.6f, r.bottom - 0.6f * u); p.lineTo(r.left + r.width * minOf(1f, t * 0.6f + l), r.bottom - 0.6f * u) }
            else -> { p.moveTo(r.left + 0.6f * u, r.top + r.height * (0.6f + 0.3f * t)); p.lineTo(r.left + 0.6f * u, r.bottom - 1f * u) }
        }
    }
    drawPath(p, a.claro(0.5f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
}

/** Un hierro gofrado en el cuero: el surco con su sombra arriba a la izquierda y su luz abajo a la derecha. */
private fun DrawScope.surcoAndalus(desde: Offset, hasta: Offset, a: AireAndalus, fuerza: Float = 1f) {
    val u = density
    drawLine(a.sombra(0.4f * fuerza), desde, hasta, 0.7f * u)
    drawLine(a.claro(0.38f * fuerza), desde + Offset(0.7f * u, 0.7f * u), hasta + Offset(0.7f * u, 0.7f * u), 0.5f * u)
}

/** El lustre del cuero sobre una cara plana de [ancho]: una banda clara a la izquierda y un filo de brillo, de arriba abajo. */
private fun DrawScope.lustreAndalus(x0: Float, ancho: Float, arriba: Float, abajo: Float, a: AireAndalus, k: Float) {
    val u = density
    drawRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.12f * k) else a.claro(0.2f), Offset(x0 + ancho * 0.1f, arriba), Size(ancho * 0.2f, abajo - arriba))
    drawRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.3f * k) else a.claro(0.5f), Offset(x0 + ancho * 0.17f, arriba), Size(0.8f * u, abajo - arriba))
    drawRect(a.sombra(0.14f), Offset(x0 + ancho * 0.72f, arriba), Size(ancho * 0.28f, abajo - arriba))
}

/**
 * El códice de cordobán de pie, visto por la tapa: el cuero entero teñido del color del ánimo (sin teñir si no se marcó), con
 * su lustre; arriba el corte de las hojas; el marco gofrado en relieve con su hilera de anillos (más hierros cuanto más
 * escribiste) y un filete dorado; el medallón redondo de piel clara con el día; el mes dorado a lo largo y, a la derecha, la
 * solapa que dobla sobre la tapa en su gran pico, con la rueda dorada en la punta (en los Sueños, la luna).
 */
internal fun DrawScope.codiceAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val piel = cuerpoAndalus(v, a, k, PIELES_ANDALUS[s % PIELES_ANDALUS.size])
    val canto = minOf(w * 0.16f, 6f * u)
    val tapa = w - canto
    val corte = 2.6f * u
    // El corte de las hojas, arriba, y la tapa.
    drawRect(a.luz(PERGAMINO, k), Offset(1f * u, 0f), Size(tapa - 1f * u, corte + 1f * u))
    for (j in 1..2) drawLine(a.sombra(0.2f), Offset(1f * u, corte * j / 3f), Offset(tapa, corte * j / 3f), 0.4f * u)
    drawRoundRect(piel, Offset(0f, corte), Size(tapa, h - corte), CornerRadius(1.4f * u))
    // El grano del cordobán y las esquinas gastadas.
    for (j in 0 until 5) {
        val o = Offset(tapa * azar(s, 50 + j), corte + (h - corte) * azar(s, 60 + j))
        drawOval(mezcla(piel, a.hondo, 0.25f).copy(alpha = 0.3f), Offset(o.x - tapa * 0.14f, o.y - 1.6f * u), Size(tapa * 0.28f, 3.2f * u))
    }
    drawPath(poligono(0f, corte, 3.4f * u, corte, 0f, corte + 3.4f * u), a.claro(0.35f))
    drawPath(poligono(0f, h, 3.4f * u, h, 0f, h - 3.4f * u), a.claro(0.28f))
    lustreAndalus(0f, tapa, corte, h, a, k)
    roceAndalus(Rect(0f, corte, tapa, h), a, s)
    // El lomo, a la izquierda: un poco más hondo, redondeado, y la cabezada de seda en zigzag arriba (sin nervios: el lomo
    // de los códices de entonces era liso).
    drawRect(a.sombra(0.18f), Offset(0f, corte), Size(1.6f * u, h - corte))
    val cab = Path()
    var xz = 0.6f * u; var z = 0
    while (xz < tapa * 0.4f && z < 20) { cab.moveTo(xz, corte + 0.2f * u); cab.lineTo(xz + 1f * u, corte - 1.4f * u); cab.lineTo(xz + 2f * u, corte + 0.2f * u); xz += 2f * u; z++ }
    drawRect(a.luz(mezcla(CARMESI, CUERO_OSC, 0.2f), k), Offset(0f, corte - 1.6f * u), Size(tapa * 0.42f, 1.8f * u))
    drawPath(cab, a.luz(PaletaAndalus.ORO, k + 0.1f), style = Stroke(0.6f * u))
    // El marco gofrado: los hierros en relieve, el de fuera dorado, y entre los dos de fuera una hilera de anillos.
    val hierros = 1 + (v.lleno * 2.2f).toInt()
    val dorado = a.luz(ORO, k + 0.1f)
    for (j in 0 until hierros) {
        val m = (2.6f + j * 2.1f) * u
        val r = Rect(m, corte + m, tapa - m, h - m)
        if (j == 0) {
            drawRect(a.sombra(0.35f), r.topLeft + Offset(0.5f * u, 0.6f * u), r.size, style = Stroke(0.6f * u))
            drawRect(dorado, r.topLeft, r.size, style = Stroke(0.7f * u))
        } else {
            surcoAndalus(r.topLeft, Offset(r.right, r.top), a); surcoAndalus(Offset(r.left, r.bottom), r.bottomRight, a)
            surcoAndalus(r.topLeft, Offset(r.left, r.bottom), a); surcoAndalus(Offset(r.right, r.top), r.bottomRight, a)
        }
    }
    val ma = 3.6f * u
    val paso = maxOf(4f * u, 1f)
    // Los anillos, en un solo trazo: el surco en sombra y su borde de luz.
    val anillos = Path(); val luces = Path()
    var y = corte + ma + paso
    while (y < h - ma - paso * 0.5f) {
        anillos.addOval(Rect(ma - 0.75f * u, y - 0.75f * u, ma + 0.75f * u, y + 0.75f * u))
        luces.addOval(Rect(ma - 0.25f * u, y - 0.25f * u, ma + 1.25f * u, y + 1.25f * u))
        y += paso
    }
    drawPath(anillos, a.sombra(0.4f), style = Stroke(0.45f * u))
    drawPath(luces, a.claro(0.35f), style = Stroke(0.3f * u))
    // El medallón: un disco de piel clara incrustado, hundido en su orla de puntos dorados, y el día dentro.
    val cm = Offset(tapa * 0.5f, corte + (h - corte) * 0.26f)
    val rm = minOf(tapa * 0.42f, h * 0.15f)
    drawCircle(a.sombra(0.35f), rm + 1.2f * u, cm + Offset(0.6f * u, 0.8f * u))
    drawCircle(a.etiqueta(k), rm, cm)
    drawArc(a.sombra(0.25f), 180f, 140f, false, Offset(cm.x - rm, cm.y - rm), Size(rm * 2, rm * 2), style = Stroke(1.2f * u))
    drawCircle(dorado, rm, cm, style = Stroke(0.8f * u))
    for (q in 0 until 8) {
        val ang = 0.785f * q
        drawCircle(dorado, 0.6f * u, Offset(cm.x + (rm + 1.6f * u) * kotlin.math.cos(ang), cm.y + (rm + 1.6f * u) * kotlin.math.sin(ang)))
    }
    textoEn(medidor, v.dia, Rect(cm.x - rm, cm.y - rm * 0.8f, cm.x + rm, cm.y + rm * 0.86f), LETRA, a.tinta, 14f, peso = FontWeight.SemiBold)
    // El mes, dorado (o a tinta en los cueros claros), de abajo arriba, a la izquierda del pico de la solapa.
    val pluma = if (piel.luminance() > 0.3f) a.tinta.copy(alpha = 0.85f) else mezcla(dorado, PERGAMINO, 0.35f)
    val mes = Rect(w * 0.08f, cm.y + rm + 4f * u, tapa * 0.55f, h - 5f * u)
    if (mes.height > 9f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8.5f, vertical = true, minimo = 4.5f)
    // La solapa: el canto que tapa las hojas y el pico que dobla sobre la tapa, con su sombra y su filo de luz.
    val solapa = mezcla(piel, a.hondo, 0.14f)
    drawRect(solapa, Offset(tapa, corte * 0.5f), Size(canto, h - corte * 0.5f))
    drawRect(a.sombra(0.35f), Offset(tapa, corte * 0.5f), Size(0.8f * u, h))
    drawRect(a.claro(0.2f), Offset(tapa + canto * 0.3f, corte), Size(0.7f * u, h - corte))
    val ap = Offset(maxOf(tapa * 0.5f, tapa - h * 0.22f), corte + (h - corte) * 0.74f)
    val pico = poligono(tapa, corte + (h - corte) * 0.5f, ap.x, ap.y, tapa, h - 1.5f * u)
    drawPath(poligono(tapa, corte + (h - corte) * 0.5f + 1.4f * u, ap.x + 0.6f * u, ap.y + 1.8f * u, tapa, h), a.sombra(0.34f))
    drawPath(pico, solapa)
    surcoAndalus(Offset(tapa - 1f * u, corte + (h - corte) * 0.5f + 2.5f * u), Offset(ap.x + 2.4f * u, ap.y), a, 0.8f)
    drawPath(pico, dorado.copy(alpha = 0.7f), style = Stroke(0.6f * u))
    drawLine(a.claro(0.35f), Offset(tapa, corte + (h - corte) * 0.5f), ap, 0.7f * u)
    marcaAndalus(Offset(ap.x + (tapa - ap.x) * 0.36f, ap.y), minOf((tapa - ap.x) * 0.2f, 2.8f * u), v.noche, dorado, solapa, a, k)
    drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    filoAndalus(a, k, corte, tapa)
}

/**
 * El estuche del rollo, de pie: un canuto forrado de cuero teñido del color del ánimo (sin teñir si no se marcó), redondo y con
 * su lustre, con anillos gofrados en relieve (más cuanto más escribiste), la tapa de bronce abovedada con su remate (en los
 * Sueños, una media luna de plata), la virola de abajo, el cordón de seda dorada que da dos vueltas y cuelga en borla, la
 * etiqueta de pergamino con el día colgada del remate y el mes dorado a lo largo.
 */
internal fun DrawScope.estucheAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val cuero = cuerpoAndalus(v, a, k, if (s % 2 == 0) CUERO else mezcla(CUERO, CUERO_OSC, 0.45f))
    val bronce = a.luz(BRONCE, k + 0.1f)
    val m = w * 0.08f
    val yTapa = 15f * u
    val cuerpo = Rect(m, yTapa, w - m, h - 5f * u)
    // El canuto, redondo: la luz a la izquierda, el brillo del cuero y la sombra en dos pasos a la derecha.
    drawRect(cuero, cuerpo.topLeft, cuerpo.size)
    drawRect(a.sombra(0.12f), Offset(cuerpo.left, cuerpo.top), Size(cuerpo.width * 0.08f, cuerpo.height))
    drawRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.14f * k) else a.claro(0.26f), Offset(cuerpo.left + cuerpo.width * 0.14f, cuerpo.top), Size(cuerpo.width * 0.16f, cuerpo.height))
    drawRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.32f * k) else a.claro(0.55f), Offset(cuerpo.left + cuerpo.width * 0.2f, cuerpo.top), Size(0.9f * u, cuerpo.height))
    drawRect(a.sombra(0.16f), Offset(cuerpo.left + cuerpo.width * 0.6f, cuerpo.top), Size(cuerpo.width * 0.4f, cuerpo.height))
    drawRect(a.sombra(0.22f), Offset(cuerpo.right - cuerpo.width * 0.16f, cuerpo.top), Size(cuerpo.width * 0.16f, cuerpo.height))
    roceAndalus(Rect(cuerpo.left, cuerpo.top + 2f * u, cuerpo.right, cuerpo.bottom - 4f * u), a, s)
    // La costura del forro, a la derecha, con sus puntadas.
    val xc = cuerpo.left + cuerpo.width * 0.8f
    drawLine(mezcla(cuero, a.hondo, 0.4f), Offset(xc, cuerpo.top), Offset(xc, cuerpo.bottom), 0.7f * u)
    var yp = cuerpo.top + 3f * u
    while (yp < cuerpo.bottom - 2f * u) { drawLine(mezcla(cuero, a.hondo, 0.45f), Offset(xc - 1.2f * u, yp), Offset(xc + 1.2f * u, yp + 1f * u), 0.5f * u); yp += maxOf(3.2f * u, 1f) }
    // Los anillos gofrados, abajo y arriba.
    val anillos = 1 + (v.lleno * 3f).toInt()
    for (j in 0 until anillos) {
        val yy = cuerpo.bottom - 9f * u - j * 3.2f * u
        surcoAndalus(Offset(cuerpo.left, yy), Offset(cuerpo.right, yy + 0.6f * u), a)
    }
    surcoAndalus(Offset(cuerpo.left, yTapa + 24f * u), Offset(cuerpo.right, yTapa + 24.6f * u), a)
    // El cordón de seda dorada: dos vueltas y la borla.
    val cordon = a.luz(mezcla(ORO, CARMESI, 0.2f), k + 0.05f)
    val ys = listOf(h * 0.44f, h * 0.7f)
    for (yc in ys) {
        drawLine(a.sombra(0.35f), Offset(cuerpo.left, yc + 1.4f * u), Offset(cuerpo.right, yc + 2.2f * u), 1.6f * u)
        drawLine(cordon, Offset(cuerpo.left - 0.5f * u, yc), Offset(cuerpo.right + 0.5f * u, yc + 0.8f * u), 1.8f * u)
        drawLine(cordon, Offset(cuerpo.left - 0.5f * u, yc + 2f * u), Offset(cuerpo.right + 0.5f * u, yc + 2.8f * u), 1.4f * u)
        drawLine(a.claro(0.35f), Offset(cuerpo.left, yc - 0.5f * u), Offset(cuerpo.left + cuerpo.width * 0.5f, yc - 0.2f * u), 0.5f * u)
    }
    val nudo = Offset(cuerpo.left + cuerpo.width * 0.3f, ys[1] + 1.4f * u)
    drawCircle(mezcla(cordon, a.hondo, 0.2f), 1.8f * u, nudo)
    drawLine(cordon, nudo, nudo + Offset(-1f * u, 5f * u), 1f * u)
    borlaAndalus(nudo + Offset(-1f * u, 5f * u), minOf(15f * u, h - nudo.y - 6f * u), cordon, a)
    // El mes, dorado (o a tinta en los cueros claros), entre las dos vueltas del cordón.
    val dorado = a.luz(ORO, k + 0.1f)
    val pluma = if (cuero.luminance() > 0.3f) a.tinta.copy(alpha = 0.85f) else mezcla(dorado, PERGAMINO, 0.35f)
    val mes = Rect(cuerpo.left + 1f * u, maxOf(yTapa + 26f * u, ys[0] - 26f * u), cuerpo.right - 1f * u, ys[0] - 2f * u)
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
    // La tapa, de cuero más hondo, abovedada y cosida al borde con su pespunte.
    val cueroTapa = mezcla(cuero, a.luz(TINTA_CUERO_ANDALUS, k), 0.3f)
    drawPath(tapa, cueroTapa)
    drawRect(a.sombra(0.3f), Offset(m, yTapa + 1f * u), Size(w - 2 * m, 1.2f * u))
    drawRect(if (a.oscuro) FILO_MUEBLE.copy(alpha = 0.25f * k) else a.claro(0.4f), Offset(m + (w - 2 * m) * 0.14f, yTapa - 6f * u), Size((w - 2 * m) * 0.12f, 6f * u))
    drawRect(a.sombra(0.24f), Offset(w - m - (w - 2 * m) * 0.3f, yTapa - 5f * u), Size((w - 2 * m) * 0.3f, 6f * u))
    val pespunte = Path()
    var xp = m + 1f * u
    while (xp < w - m - 1f * u) { pespunte.moveTo(xp, yTapa - 1.4f * u); pespunte.lineTo(xp + 1.2f * u, yTapa - 1.4f * u); xp += maxOf(2.4f * u, 1f) }
    drawPath(pespunte, a.luz(mezcla(PERGAMINO, CUERO, 0.3f), k), style = Stroke(0.6f * u))
    drawLine(mezcla(cueroTapa, a.hondo, 0.4f), Offset(w * 0.5f + (w - 2 * m) * 0.18f, yTapa - 6.5f * u), Offset(w * 0.5f + (w - 2 * m) * 0.2f, yTapa + 1f * u), 0.6f * u)
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
 * El legajo de hojas sueltas, más bajo: las hojas en su carpeta de badana teñida del color del ánimo (sin teñir si no se marcó),
 * con su lustre y sus bordes vueltos, atada con una cinta de lino que da la vuelta y se anuda en lazada. Por arriba asoman los
 * cantos de las hojas, desparejos (más cuanto más escribiste); arriba va metida la papeleta con el día y, en la carpeta, el mes
 * y la rueda (en los Sueños, la luna).
 */
internal fun DrawScope.legajoAndalus(v: Volumen, medidor: TextMeasurer, a: AireAndalus, k: Float) {
    val u = density
    val w = size.width; val h = size.height
    val s = v.semilla and 0x7fffffff
    val hojas = 2 + (v.lleno * 4.4f).toInt()
    val asoma = minOf(h * 0.16f, 3f * u + hojas * 1.6f * u)
    // Las hojas que asoman, cada una a su altura y de su tono.
    for (j in 0 until hojas) {
        val dy = (asoma * (j.toFloat() / hojas) + asoma * 0.5f * azar(s, 20 + j) - asoma * 0.25f).coerceAtLeast(0f)
        val dx = (azar(s, 30 + j) - 0.5f) * 4f * u
        val an = (w - 3f * u) * (0.78f + 0.22f * azar(s, 35 + j))
        val ladeo = (azar(s, 45 + j) - 0.5f) * 2.4f * u
        val tono = a.luz(mezcla(PERGAMINO, if (j % 3 == 0) CUERO else PINO, 0.06f + 0.2f * azar(s, 40 + j)), k + 0.05f)
        val x0 = 1.5f * u + dx + (w - 3f * u - an) * azar(s, 50 + j)
        drawPath(poligono(x0, dy + ladeo, x0 + an, dy - ladeo, x0 + an, h, x0, h), tono)
        drawLine(a.sombra(0.22f), Offset(x0, dy + ladeo), Offset(x0 + an, dy - ladeo), 0.6f * u)
    }
    // Una hoja que asoma también por el costado derecho.
    drawRect(a.luz(mezcla(PERGAMINO, PINO, 0.15f), k), Offset(w - 1.6f * u, asoma + 4f * u), Size(1.6f * u, h * 0.4f))
    // La carpeta de badana: la tapa de delante, con su borde vuelto y su lustre.
    val tapa = Rect(0f, asoma, w, h)
    val badana = cuerpoAndalus(v, a, k, mezcla(CUERO, PINO, 0.35f + 0.2f * azar(s, 3)))
    drawRoundRect(badana, tapa.topLeft, tapa.size, CornerRadius(1.6f * u))
    for (j in 0 until 4) {
        val o = Offset(w * azar(s, 70 + j), tapa.top + tapa.height * azar(s, 80 + j))
        drawOval(mezcla(badana, a.hondo, 0.22f).copy(alpha = 0.3f), Offset(o.x - w * 0.16f, o.y - 1.5f * u), Size(w * 0.32f, 3f * u))
    }
    lustreAndalus(0f, w, tapa.top, h, a, k)
    roceAndalus(tapa, a, s)
    drawRect(a.claro(0.42f), Offset(1f * u, tapa.top), Size(w - 2f * u, 0.9f * u))
    surcoAndalus(Offset(2.2f * u, tapa.top + 2.2f * u), Offset(w - 2.2f * u, tapa.top + 2.2f * u), a, 0.8f)
    surcoAndalus(Offset(2.2f * u, tapa.top + 2.2f * u), Offset(2.2f * u, h - 2.2f * u), a, 0.8f)
    // La papeleta con el día, metida arriba.
    val g = 6f * u
    val yc = maxOf(tapa.top + tapa.height * 0.62f, h - g - 5f * u)
    val pap = Rect(w * 0.1f, tapa.top + 3.5f * u, w * 0.9f, minOf(tapa.top + 20.5f * u, yc - 2f * u))
    drawRect(a.sombra(0.35f), pap.topLeft + Offset(0.8f * u, 1f * u), pap.size)
    drawRect(a.etiqueta(k), pap.topLeft, pap.size)
    textoEn(medidor, v.dia, Rect(pap.left + 0.5f * u, pap.top + 0.8f * u, pap.right - 0.5f * u, minOf(pap.bottom, yc) - 0.5f * u), LETRA, a.tinta, 14f, peso = FontWeight.SemiBold)
    // El mes, a tinta o dorado según lo claro de la badana, entre la papeleta y la cinta.
    val pluma = if (badana.luminance() > 0.3f) a.tinta.copy(alpha = 0.88f) else mezcla(a.luz(PaletaAndalus.ORO, k + 0.1f), PERGAMINO, 0.35f)
    val mes = Rect(w * 0.1f, pap.bottom + 2f * u, w * 0.6f, yc - 1.5f * u)
    if (mes.height > 8f * u) textoEn(medidor, v.mes, mes, LETRA, pluma, 8f, vertical = true, minimo = 4.5f)
    // La cinta de lino, que da la vuelta, y la lazada con sus dos cabos.
    val cinta = if (badana.luminance() > 0.4f) a.luz(mezcla(CUERO_OSC, ANIL, 0.25f), k) else a.luz(mezcla(PERGAMINO, CUERO, 0.25f), k)
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
    marcaAndalus(Offset(w * 0.78f, (pap.bottom + yc) / 2), minOf(w * 0.12f, 2.6f * u), v.noche, pluma, badana, a, k)
    drawRect(a.sombra(0.28f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    filoAndalus(a, k, asoma, w)
}
