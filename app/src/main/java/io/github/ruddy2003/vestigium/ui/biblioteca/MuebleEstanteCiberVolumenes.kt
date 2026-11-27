package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CAFE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// ---------------------------------------------------------------- el color del ánimo

/** La escala del ánimo del tema (la misma de día y de noche: el petróleo, el casi negro de en medio y el cian). */
private val ESCALA_CIBER: List<Color> by lazy { juegoTonos(TemaId.CIBER, true).take(3) }

/**
 * Los colores con que se dice el ánimo en lo guardado, en el orden de la escala del tema (de lo peor a lo mejor): el petróleo, un
 * verde azulado gris, el beis de las máquinas, el agua clara y el cian. En la interfaz el ánimo de en medio es un casi negro (el
 * fondo); en un objeto del cuarto se leería como un agujero, así que aquí es el beis del plástico y del papel sin teñir.
 */
private val TINTES_CIBER = listOf(Color(0xFF2E8A9A), Color(0xFF5E9EA0), Color(0xFFDCC99A), Color(0xFF8CE4EC), Color(0xFF22D8F2))

/** Dónde cae [c] en la escala del tema (de 0 a 1), o null si no está en ella. */
private fun enEscalaCiber(c: Color): Float? {
    val e = ESCALA_CIBER
    if (e.size < 3) return null
    var mejor = -1f; var dist = Float.MAX_VALUE
    for (i in 0..60) {
        val x = i / 60f
        val p = if (x < 0.5f) mezcla(e[0], e[1], x * 2) else mezcla(e[1], e[2], (x - 0.5f) * 2)
        val d = (p.red - c.red).pow(2) + (p.green - c.green).pow(2) + (p.blue - c.blue).pow(2)
        if (d < dist) { dist = d; mejor = x }
    }
    return if (dist < 0.02f) mejor else null
}

/** El tinte que corresponde a un color del ánimo: su punto en la escala del tema, llevado a [TINTES_CIBER]. */
internal fun tenidoCiber(t: Color?): Color? {
    if (t == null) return null
    val x = enEscalaCiber(t) ?: return t
    val q = x.coerceIn(0f, 1f) * (TINTES_CIBER.size - 1)
    val i = q.toInt().coerceAtMost(TINTES_CIBER.size - 2)
    return mezcla(TINTES_CIBER[i], TINTES_CIBER[i + 1], q - i)
}

// ---------------------------------------------------------------- la luz de un volumen

/** La luz que le toca a un volumen: el tono de su centro y el aire. */
private class LuzVolumenCiber(val a: AireCiber, val t: TonoCiber) {
    fun c(col: Color, mas: Float = 1f) = t.c(col, mas)
    fun s(al: Float) = t.s(al)
    /** El filo de luz: lejos de la luz, apenas (que no aclare lo que está en penumbra). */
    fun b(al: Float, mas: Float = 1f) = t.b(al * (0.3f + 0.7f * (t.k * mas / 0.6f).coerceIn(0f, 1f)), mas)
    val izq: Boolean get() = t.izq
    /**
     * El ánimo: su color hundido por la distancia a la luz sin perder el tono (lejos, el cian es un azul petróleo hondo y el beis,
     * un pardo azulado), que solo se enciende cerca de una luz.
     */
    fun animo(col: Color, mas: Float = 1f): Color {
        val k = (t.k * mas).coerceIn(0f, 1.3f)
        val apagado = mezcla(Color(col.red * 0.26f, col.green * 0.26f, col.blue * 0.28f, col.alpha), a.hondo(col), 0.35f)
        val lit = t.c(col, mas)
        val vivo = mezcla(col, lit, 0.4f)
        return mezcla(apagado, vivo, (0.08f + 1.15f * k.pow(1.25f)).coerceAtMost(0.97f))
    }
}

private const val TAM_DIA_CIBER = 16.5f
private const val TAM_MES_CIBER = 7f
/** La letra de la cinta de rotular: el fósforo pálido de los rótulos del Ideario. */
private val LETRA_CINTA_CIBER = Color(0xFFBDEFF5)

/** El mes corto en español («nov.»), de lo que trae el volumen (el tema lo nombra «nov»). */
private fun mesCortoCiber(mes: String): String {
    val letras = mes.filter { it.isLetter() }
    return if (letras.length >= 3) letras.take(3).lowercase() + "." else letras.lowercase()
}

/** El color de la cinta de rotular del día, de la semilla: azul noche, negra o roja oscura (las del Ideario). */
private fun colorCintaCiber(semilla: Int): Color = when (Math.floorMod(semilla ushr 4, 3)) {
    0 -> mezcla(PARED, NOCHE, 0.45f)
    1 -> NEGRO
    else -> mezcla(ROJO, NOCHE, 0.66f)
}

/** Una tira de cinta de rotular de [r]: los extremos cortados en bisel, su sombra, el brillo del plástico y el canto de abajo. */
private fun DrawScope.tiraCiber(r: Rect, col: Color, l: LuzVolumenCiber) {
    val u = density
    val corte = r.height * 0.2f
    val forma = poligonoCiber(r.left + corte, r.top, r.right, r.top, r.right - corte, r.bottom, r.left, r.bottom)
    translate(1.1f * u, 1.5f * u) { drawPath(forma, NOCHE.copy(alpha = 0.65f)) }
    drawPath(forma, mezcla(mezcla(col, NOCHE, 0.35f), col, (0.55f * l.t.k).coerceAtMost(0.55f)))
    clipPath(forma) {
        drawRect(l.b(0.4f), r.topLeft, Size(r.width, r.height * 0.13f))
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(r.left, r.bottom - r.height * 0.13f), Size(r.width, r.height * 0.13f))
    }
}

/**
 * La etiqueta del día en cinta de rotular en relieve, como los rótulos del Ideario: una tira oscura con el día grande en claro,
 * como grabado, y debajo otra más corta con el mes; [arriba] es el centro del canto de arriba de la tira del día. La letra pálida
 * se apaga lejos de la luz sin bajar de 4.5 a 1 sobre la cinta. Devuelve el alto que ocupa.
 */
private fun DrawScope.cintaDiaCiber(arriba: Offset, v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, maxAncho: Float, giro: Float = 0f): Float {
    val u = density
    val k = l.t.k.coerceIn(0f, 1f)
    val letra = mezcla(mezcla(LETRA_CINTA_CIBER, NOCHE, 0.34f), mezcla(LETRA_CINTA_CIBER, l.t.l, 0.12f), k.pow(0.8f))
    val dia = capaEn(medidor, v.dia, Rect(0f, 0f, maxOf(8f, maxAncho - 7f * u), 60f * u), Letra.terminal, letra, TAM_DIA_CIBER, peso = FontWeight.Bold) ?: return 0f
    val mes = capaEn(medidor, mesCortoCiber(v.mes), Rect(0f, 0f, maxOf(8f, maxAncho - 6f * u), 30f * u), Letra.terminal, letra, TAM_MES_CIBER, minimo = 4f)
    val col = colorCintaCiber(v.semilla)
    val h1 = dia.size.height * 0.92f
    val r1 = Rect(arriba.x - dia.size.width / 2f - 4.5f * u, arriba.y, arriba.x + dia.size.width / 2f + 4.5f * u, arriba.y + h1)
    val dx = 2.5f * u * (azar(v.semilla, 30) - 0.5f) * 2f
    val r2 = mes?.let { Rect(arriba.x + dx - it.size.width / 2f - 3.5f * u, r1.bottom + 1.6f * u, arriba.x + dx + it.size.width / 2f + 3.5f * u, r1.bottom + 1.6f * u + it.size.height * 0.95f) }
    rotate(giro, Offset(arriba.x, arriba.y + h1)) {
        tiraCiber(r1, col, l)
        val o1 = Offset(r1.center.x - dia.size.width / 2f, r1.center.y - dia.size.height / 2f)
        drawText(dia, topLeft = o1 + Offset(0.6f * u, 0.7f * u), color = NOCHE, alpha = 0.55f)
        drawText(dia, topLeft = o1)
        if (mes != null && r2 != null) {
            rotate((azar(v.semilla, 31) - 0.5f) * 5f, r2.center) {
                tiraCiber(r2, col, l)
                val o2 = Offset(r2.center.x - mes.size.width / 2f, r2.center.y - mes.size.height / 2f)
                drawText(mes, topLeft = o2 + Offset(0.5f * u, 0.6f * u), color = NOCHE, alpha = 0.55f)
                drawText(mes, topLeft = o2)
            }
        }
    }
    return (r2?.bottom ?: r1.bottom) - arriba.y
}

/** La sombra del volumen en el fondo del casillero (del lado contrario a la luz) y la de contacto en la repisa. */
private fun DrawScope.asientoCiber(l: LuzVolumenCiber, x0: Float, x1: Float, arriba: Float) {
    val u = density; val w = size.width; val h = size.height
    val ancho = x1 - x0
    if (l.izq) drawRect(l.s(0.42f), Offset(x1, arriba + h * 0.05f), Size(w - x1, h - arriba - h * 0.05f))
    else drawRect(l.s(0.42f), Offset(0f, arriba + h * 0.05f), Size(x0, h - arriba - h * 0.05f))
    drawOval(l.s(0.6f), Offset(x0 + (if (l.izq) ancho * 0.1f else -ancho * 0.08f), h - 3f * u), Size(ancho, 4.6f * u))
}

/** La pegatina de una media luna recortada (los Sueños): plateada, con su sombra y su filo; no es una luna llena (no parece un ojo). */
private fun DrawScope.lunaCiber(c: Offset, r: Float, l: LuzVolumenCiber) {
    val p = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val hueco = Path().apply { addOval(Rect(c.x - r * 0.3f, c.y - r * 1.15f, c.x + r * 1.5f, c.y + r * 0.62f)) }
    val luna = Path().apply { op(p, hueco, PathOperation.Difference) }
    drawPath(Path().apply { addPath(luna, Offset(r * 0.16f, r * 0.2f)) }, l.s(0.45f))
    drawPath(luna, mezcla(l.c(Color(0xFFE6ECF8), 1.3f), Color(0xFFB8C6E6), 0.4f))
    drawPath(luna, l.b(0.6f, 1.3f), style = Stroke(0.6f * density))
}

/**
 * Las tres clases de entrada del cuarto de Terminal, elegidas con la semilla del día (la clase sale de la semilla, nunca de su
 * sitio; el sitio solo dice qué luz le llega). Tres siluetas que no se confunden a tamaño de teléfono:
 * - cuadrada: el disquete de 5¼ en su funda de papel del ánimo, con la muesca del pulgar; el disco negro asoma por arriba con su
 *   muesca de protección; con más palabras, a veces dos fundas, una detrás de otra;
 * - redonda: el carrete de cinta magnética de pie, con la brida del ánimo y sus ventanas, la cinta enrollada parda (más gruesa
 *   cuanto más escribiste) y el cubo;
 * - en trapecio: el revistero de cartón del ánimo, visto de costado, con el corte inclinado, el agujero para el dedo y los listados
 *   de papel continuo que asoman.
 * El día y el mes van en cinta de rotular en relieve. Sin ánimo: la funda blanca, la brida gris y el cartón crudo. En los Sueños,
 * una pegatina de media luna en cada uno.
 */
internal fun DrawScope.volumenCiber(v: Volumen, medidor: TextMeasurer) {
    if (size.width < 4f || size.height < 4f) return
    val a = AireCiber(v.noche)
    val p = lugarVolumenCiber(v.repisa, v.centro)
    val t = tonoCiber(p.x, p.y, a, regNominalCiber(v.repisas), ambNominalCiber(v.repisas))
    val l = LuzVolumenCiber(a, t)
    val tinte = tenidoCiber(v.tono)
    when (MuebleEstanteCiber.clase(v.semilla)) {
        0 -> fundaCiber(v, medidor, l, tinte)
        1 -> carreteCiber(v, medidor, l, tinte)
        else -> revisteroCiber(v, medidor, l, tinte)
    }
}

// ---------------------------------------------------------------- la funda

/** El disquete de 5¼ en su funda de papel; con más palabras, a veces dos. */
private fun DrawScope.fundaCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val asoma = 0.42f + 0.12f * azar(v.semilla, 5)
    val dos = w > h * 0.8f && azar(v.semilla, 14) > 0.2f
    val s = minOf(w * (if (dos) 0.8f else 0.96f), h / (1f + asoma))
    val papel = tinte?.let { l.animo(it) } ?: l.c(mezcla(PAPEL, Color.White, 0.2f), 0.95f)
    val giro = if (!dos && azar(v.semilla, 6) > 0.72f) (if (azar(v.semilla, 7) > 0.5f) 3.5f else -3.5f) else 0f
    val x0 = if (dos) w * 0.02f else (w - s) / 2
    asientoCiber(l, x0, if (dos) w * 0.98f else x0 + s, h - s * (1f + asoma))
    if (dos) {
        // La de atrás: corrida a la derecha, con el disco asomando más, y algo en sombra.
        val s2 = s * 0.96f
        val pk2 = minOf(s2 * (asoma + 0.08f), h - s2)
        unaFundaCiber(Rect(w * 0.98f - s2, h - s2, w * 0.98f, h), pk2, mezcla(papel, NOCHE, 0.22f), l, v.semilla + 3, v, false)
    }
    rotate(giro, Offset(if (giro > 0f) x0 + s else x0, h)) {
        val f = Rect(x0, h - s, x0 + s, h)
        unaFundaCiber(f, s * asoma, papel, l, v.semilla, v, true)
        val alto = cintaDiaCiber(Offset(f.center.x, f.top + s * 0.2f), v, medidor, l, s * 0.92f)
        // Lo lleno: renglones a lápiz bajo la etiqueta, más cuantas más palabras.
        val renglones = 1 + (v.lleno * 2).toInt()
        val y0 = f.top + s * 0.24f + alto
        if (y0 < f.bottom - s * 0.12f) garabatosCiber(Rect(f.left + s * 0.16f, y0, f.right - s * 0.22f, minOf(f.bottom - s * 0.08f, y0 + s * 0.08f * renglones)),
            renglones, mezcla(papel, NOCHE, 0.55f).copy(alpha = 0.7f), v.semilla, 0.9f * u)
        if (v.noche) lunaCiber(Offset(f.left + s * 0.82f, f.top + s * 0.12f), s * 0.08f, l)
    }
}

/** Una funda con su disco: el disco negro detrás (asoma [pk]), la funda de [papel] con la muesca del pulgar, sus solapas y sus planos. */
private fun DrawScope.unaFundaCiber(f: Rect, pk: Float, papel: Color, l: LuzVolumenCiber, semilla: Int, v: Volumen, delante: Boolean) {
    val u = density
    val s = f.width
    val disco = Rect(f.left + s * 0.008f, f.top - pk, f.right - s * 0.008f, f.top - pk + s * 0.985f)
    val disc = Path().apply { addRect(disco) }
    val muesca = Path().apply { addRect(Rect(disco.right - s * 0.045f, disco.top + s * 0.1f, disco.right + 1f, disco.top + s * 0.18f)) }
    val forma = Path().apply { op(disc, muesca, PathOperation.Difference) }
    drawPath(forma, l.c(mezcla(NEGRO, NOCHE, 0.1f), 1.3f))
    clipPath(forma) {
        drawRect(l.b(0.6f), disco.topLeft, Size(disco.width, 1f * u))
        drawLine(Color.White.copy(alpha = 0.08f), Offset(disco.left + s * 0.55f, disco.top + s * 0.4f), Offset(disco.left + s * 0.9f, disco.top), s * 0.12f)
        val hub = Offset(disco.center.x, disco.top + s * 0.49f)
        drawCircle(l.c(mezcla(CAFE, BEIS, 0.35f), 1.3f), s * 0.17f, hub)
        drawCircle(l.c(mezcla(CAFE, NEGRO, 0.2f), 1.2f), s * 0.135f, hub)
        drawCircle(NOCHE, s * 0.09f, hub)
        drawArc(l.b(0.6f), 200f, 100f, false, Offset(hub.x - s * 0.17f, hub.y - s * 0.17f), Size(s * 0.34f, s * 0.34f), style = Stroke(0.9f * u))
        // La etiquetita del disco, arriba a la izquierda, con un garabato (sin letras).
        val eh = minOf(s * 0.11f, pk * 0.55f)
        if (eh > 2f * u) {
            drawRect(l.c(mezcla(PAPEL, if (Math.floorMod(semilla, 3) == 0) NOTA else PAPEL, 0.4f), 0.85f), Offset(disco.left + s * 0.07f, disco.top + s * 0.035f), Size(s * 0.56f, eh))
            drawLine(l.c(TINTA, 0.8f).copy(alpha = 0.6f), Offset(disco.left + s * 0.12f, disco.top + s * 0.035f + eh * 0.55f), Offset(disco.left + s * (0.3f + 0.2f * azar(semilla, 9)), disco.top + s * 0.035f + eh * 0.55f), 0.9f * u)
        }
    }
    val pulgar = s * 0.13f
    val cuerpo = Path().apply {
        addRect(f)
        op(this, Path().apply { addOval(Rect(f.center.x - pulgar, f.top - pulgar, f.center.x + pulgar, f.top + pulgar)) }, PathOperation.Difference)
    }
    drawPath(cuerpo, papel)
    clipPath(cuerpo) {
        planosCiber(f, l.t, 0.9f)
        drawRect(l.s(0.16f), f.topLeft, Size(s * 0.07f, s))
        drawRect(l.s(0.16f), Offset(f.right - s * 0.07f, f.top), Size(s * 0.07f, s))
        drawRect(l.s(0.12f), Offset(f.left, f.bottom - s * 0.06f), Size(s, s * 0.06f))
        if (delante) {
            drawLine(l.s(0.22f), Offset(f.left + s * 0.6f, f.top + s * 0.62f), Offset(f.left + s * 0.88f, f.top + s * 0.8f), 0.8f * u)
            if (azar(v.semilla, 8) > 0.55f) drawOval(l.c(CAFE).copy(alpha = 0.3f), Offset(f.left + s * 0.08f, f.top + s * 0.6f), Size(s * 0.3f, s * 0.22f), style = Stroke(1.2f * u))
        }
    }
    drawLine(l.b(0.6f), Offset(f.left, f.top), Offset(f.center.x - pulgar, f.top), 1f * u)
    drawLine(l.b(0.6f), Offset(f.center.x + pulgar, f.top), Offset(f.right, f.top), 1f * u)
}

// ---------------------------------------------------------------- el carrete

/** El pardo de la cinta magnética enrollada. */
private val CINTA_CIBER = Color(0xFF5A3A24)

/**
 * El carrete de cinta magnética de pie: la brida de atrás vista por las ventanas, la cinta parda enrollada con sus anillos de brillo
 * (más gruesa cuanto más escribiste), la brida de delante del ánimo con seis ventanas en rueda (seis: tres harían cara), el borde
 * con su filo, el cubo con el anillo de escritura en algunos, y la cinta de rotular del día pegada sobre el cubo.
 */
private fun DrawScope.carreteCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val d = minOf(w * 0.98f, h)
    val rr = d / 2
    val c = Offset(w / 2, h - rr)
    asientoCiber(l, c.x - rr * 0.85f, c.x + rr * 0.85f, h - d)
    val brida = tinte?.let { l.animo(it) } ?: l.c(mezcla(ACERO, NEGRO, 0.25f))
    // La brida de atrás y lo que se ve de la cinta por las ventanas.
    drawCircle(mezcla(brida, NOCHE, 0.5f), rr * 0.97f, c)
    val rc = rr * (0.56f + 0.36f * (0.15f + 0.85f * v.lleno))
    drawCircle(l.c(CINTA_CIBER, 1.1f), rc, c)
    for (j in 0 until 4) drawCircle(l.c(mezcla(CINTA_CIBER, NOCHE, 0.3f)), rr * (0.52f + 0.1f * j).coerceAtMost(rc / rr), c, style = Stroke(0.7f * u))
    drawArc(l.b(0.55f), if (l.izq) 190f else 290f, 60f, false, Offset(c.x - rc * 0.86f, c.y - rc * 0.86f), Size(rc * 1.72f, rc * 1.72f), style = Stroke(1.6f * u))
    // La brida de delante: el disco del ánimo menos las seis ventanas.
    val nv = 6
    val ventanas = Path()
    val ri = rr * 0.5f; val re = rr * 0.86f
    val giro = 30f * azar(v.semilla, 16)
    for (j in 0 until nv) {
        val a0 = Math.toRadians((giro + j * 360f / nv + 6f).toDouble()); val a1 = Math.toRadians((giro + (j + 1) * 360f / nv - 6f).toDouble())
        val p = Path()
        val pasos = 6
        for (q in 0..pasos) { val a = a0 + (a1 - a0) * q / pasos; val x = c.x + re * cos(a).toFloat(); val y = c.y + re * sin(a).toFloat(); if (q == 0) p.moveTo(x, y) else p.lineTo(x, y) }
        for (q in pasos downTo 0) { val a = a0 + (a1 - a0) * q / pasos; p.lineTo(c.x + ri * cos(a).toFloat(), c.y + ri * sin(a).toFloat()) }
        p.close()
        ventanas.addPath(p)
    }
    val disco = Path().apply { addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr)) }
    val frente = Path().apply { op(disco, ventanas, PathOperation.Difference) }
    drawPath(frente, brida)
    clipPath(frente) {
        planosCiber(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr), l.t, 0.8f)
        drawLine(Color.White.copy(alpha = 0.1f), Offset(c.x - rr * 0.7f, c.y + rr * 0.3f), Offset(c.x + rr * 0.1f, c.y - rr * 0.9f), rr * 0.18f)
    }
    drawPath(ventanas, l.s(0.45f), style = Stroke(1f * u))
    // El borde de la brida: el filo del lado de la luz y la sombra del otro.
    drawArc(l.b(0.75f), if (l.izq) 150f else 270f, 120f, false, Offset(c.x - rr, c.y - rr), Size(d, d), style = Stroke(1.3f * u))
    drawArc(l.s(0.5f), if (l.izq) 330f else 90f, 120f, false, Offset(c.x - rr + 0.6f * u, c.y - rr + 0.6f * u), Size(d - 1.2f * u, d - 1.2f * u), style = Stroke(1.4f * u))
    // El cubo: un aro un poco más claro y, en algunos, el anillo de escritura de color.
    drawCircle(mezcla(brida, Color.White, 0.08f), rr * 0.46f, c, style = Stroke(1.2f * u))
    if (azar(v.semilla, 17) > 0.45f) drawCircle(l.c(if (azar(v.semilla, 18) > 0.5f) ROJO else AMBAR, 0.9f), rr * 0.4f, c, style = Stroke(2.2f * u))
    drawCircle(NOCHE.copy(alpha = 0.8f), rr * 0.11f, c)
    drawCircle(l.c(ACERO, 1.1f), rr * 0.13f, c, style = Stroke(1.2f * u))
    // La cinta de rotular del día, sobre el cubo.
    val alto = 30f * u
    cintaDiaCiber(Offset(c.x, c.y - alto * 0.52f), v, medidor, l, d * 0.72f, (azar(v.semilla, 19) - 0.5f) * 6f)
    if (v.noche) lunaCiber(Offset(c.x + rr * 0.62f, c.y - rr * 0.6f), rr * 0.15f, l)
}

// ---------------------------------------------------------------- el revistero

/**
 * El revistero de cartón del ánimo visto de costado: el corte inclinado de la boca (la parte alta a un lado según la semilla), el
 * agujero para el dedo por donde se ve el papel, los cantos doblados y gastados; detrás, lo que asoma por la boca: la pila de
 * listados de papel continuo con sus bandas verdes y sus pliegues, y un manual de anillas o una funda de disquete. El día, en cinta
 * de rotular en la parte baja del costado.
 */
private fun DrawScope.revisteroCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val altaDer = azar(v.semilla, 15) > 0.5f
    val x0 = w * 0.04f; val x1 = w * 0.96f
    val alta = h * (0.9f + 0.08f * azar(v.semilla, 20)); val baja = h * (0.48f + 0.05f * azar(v.semilla, 21))
    val yIz = h - (if (altaDer) baja else alta); val yDe = h - (if (altaDer) alta else baja)
    asientoCiber(l, x0, x1, minOf(yIz, yDe))
    // Lo que asoma por la boca: los listados (de canto, con sus pliegues) y otra cosa al lado alto.
    val tope = minOf(yIz, yDe) - h * 0.05f
    val pil = Rect(x0 + w * 0.06f, tope + h * 0.06f, x1 - w * 0.06f, h * 0.7f)
    var y = pil.top
    var k = 0
    while (y < pil.bottom) {
        val dx = w * 0.012f * (if (k % 2 == 0) 1f else -1f)
        drawRoundRect(l.c(PAPEL, 0.95f), Offset(pil.left + dx, y), Size(pil.width, 2.8f * u), CornerRadius(1.4f * u))
        drawRect(l.s(0.3f), Offset(pil.left + dx, y + 2.3f * u), Size(pil.width, 0.6f * u))
        if (k % 3 == 1) drawRect(l.c(RAYA, 0.95f), Offset(pil.left + dx + pil.width * 0.12f, y + 0.9f * u), Size(pil.width * 0.76f, 1f * u))
        y += 2.8f * u; k++
    }
    val xa = if (altaDer) x1 - w * 0.3f else x0 + w * 0.05f
    if (Math.floorMod(v.semilla ushr 2, 2) == 0) {
        // Un manual de anillas, de canto, más alto que los listados.
        val m = Rect(xa, tope - h * 0.02f, xa + w * 0.24f, h * 0.7f)
        val col = mezcla(PARED, NEGRO, 0.2f)
        drawRect(l.s(0.4f), m.translate(1.5f * u, 0f).topLeft, m.size)
        drawRoundRect(l.c(col, 1.05f), m.topLeft, m.size, CornerRadius(w * 0.02f))
        drawRect(l.b(0.5f), m.topLeft, Size(m.width, 0.9f * u))
        drawRect(l.c(mezcla(PAPEL, col, 0.3f), 0.9f), Offset(m.left + m.width * 0.2f, m.top + h * 0.05f), Size(m.width * 0.6f, h * 0.1f))
    } else {
        // Una funda de disquete metida de canto.
        val f = Rect(xa, tope - h * 0.01f, xa + w * 0.28f, h * 0.7f)
        drawRect(l.s(0.4f), f.translate(1.5f * u, 0f).topLeft, f.size)
        drawRect(l.c(mezcla(NEGRO, NOCHE, 0.1f), 1.2f), f.topLeft, f.size)
        drawRect(l.c(mezcla(PAPEL, NOTA, 0.3f), 0.85f), Offset(f.left + f.width * 0.1f, f.top + h * 0.03f), Size(f.width * 0.6f, h * 0.06f))
        drawRect(l.b(0.5f), f.topLeft, Size(f.width, 0.9f * u))
    }
    // El costado: cartón del ánimo con el corte inclinado, los planos, el canto doblado de la boca y el agujero para el dedo.
    val carton = tinte?.let { l.animo(it) } ?: l.c(mezcla(PaletaCiber.CARTON, BEIS, 0.2f))
    val costado = poligonoCiber(x0, h, x1, h, x1, yDe, x0, yIz)
    drawPath(costado, carton)
    clipPath(costado) {
        planosCiber(Rect(x0, 0f, x1, h), l.t, 0.9f)
        // El agujero para el dedo, alargado, cerca de la parte alta: por él se ve el papel de dentro.
        val ax = if (altaDer) x1 - w * 0.3f else x0 + w * 0.08f
        val ay = minOf(yIz, yDe) + h * 0.12f
        val agujero = Rect(ax, ay, ax + w * 0.22f, ay + h * 0.07f)
        drawRoundRect(NOCHE.copy(alpha = 0.55f), agujero.topLeft, agujero.size, CornerRadius(agujero.height / 2))
        drawRoundRect(l.c(PAPEL, 0.7f), Offset(agujero.left + 1.2f * u, agujero.top + agujero.height * 0.45f), Size(agujero.width - 2.4f * u, agujero.height * 0.5f), CornerRadius(agujero.height / 3))
        drawArc(l.b(0.5f), 0f, 180f, false, agujero.topLeft, Size(agujero.width, agujero.height), style = Stroke(0.8f * u))
        // El canto de abajo, el roce de la esquina y una arruga del cartón.
        drawRect(l.s(0.3f), Offset(x0, h - h * 0.05f), Size(x1 - x0, h * 0.05f))
        drawLine(l.s(0.25f), Offset(x0 + w * 0.1f, h * 0.9f), Offset(x0 + w * 0.3f, h * 0.84f), 0.8f * u)
        drawOval(l.c(mezcla(BEIS, PAPEL, 0.3f)).copy(alpha = 0.3f), Offset(if (altaDer) x0 - w * 0.03f else x1 - w * 0.09f, h - h * 0.12f), Size(w * 0.12f, h * 0.12f))
    }
    // El canto doblado de la boca, con su filo, y los cantos de los lados.
    drawLine(l.c(mezcla(BEIS, PAPEL, 0.2f), 1.15f), Offset(x0, yIz), Offset(x1, yDe), 2.2f * u, StrokeCap.Round)
    drawLine(l.b(0.7f), Offset(x0, yIz - 1f * u), Offset(x1, yDe - 1f * u), 0.8f * u)
    drawLine(if (l.izq) l.b(0.5f) else l.s(0.4f), Offset(x0, yIz), Offset(x0, h), 1.2f * u)
    drawLine(if (l.izq) l.s(0.4f) else l.b(0.4f), Offset(x1, yDe), Offset(x1, h), 1.2f * u)
    // La cinta del día, en la parte baja del costado.
    val cy = h - 33f * u - h * 0.04f
    val cx = (x0 + x1) / 2 + (if (altaDer) w * 0.04f else -w * 0.04f)
    cintaDiaCiber(Offset(cx, cy), v, medidor, l, (x1 - x0) * 0.86f, (azar(v.semilla, 22) - 0.5f) * 5f)
    if (v.noche) lunaCiber(Offset(if (altaDer) x1 - w * 0.16f else x0 + w * 0.16f, minOf(yIz, yDe) + h * 0.3f), w * 0.07f, l)
}
