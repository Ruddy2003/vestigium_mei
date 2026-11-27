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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CAFE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TINTA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.pow

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

private fun linealCiber(x: Float): Float = x.coerceIn(0f, 1f).pow(2.2f)

/** La luminancia relativa de [c] (con la curva del sRGB aproximada a 2.2). */
internal fun luminanciaCiber(c: Color): Float = 0.2126f * linealCiber(c.red) + 0.7152f * linealCiber(c.green) + 0.0722f * linealCiber(c.blue)

/** [c] llevado a la luminancia [l] sin cambiarle el tono. */
internal fun conLuminanciaCiber(c: Color, l: Float): Color {
    val actual = luminanciaCiber(c)
    if (actual < 1e-4f) return Color(l.pow(1f / 2.2f), l.pow(1f / 2.2f), l.pow(1f / 2.2f), c.alpha)
    val f = l / actual
    fun canal(x: Float) = (linealCiber(x) * f).coerceIn(0f, 1f).pow(1f / 2.2f)
    return Color(canal(c.red), canal(c.green), canal(c.blue), c.alpha)
}

/** La luminancia de una etiqueta que con la tinta ([PaletaCiber.TINTA]) da 4.5 a 1 o más. */
private const val ETIQUETA_MINIMA_CIBER = 0.23f

// ---------------------------------------------------------------- la luz de un volumen

/** La luz que le toca a un volumen: el tono de su centro y el aire. */
private class LuzVolumenCiber(val a: AireCiber, val t: TonoCiber) {
    fun c(col: Color, mas: Float = 1f) = t.c(col, mas)
    fun s(al: Float) = t.s(al)
    /** El filo de luz: lejos de la luz, apenas (que no aclare lo que está en penumbra). */
    fun b(al: Float, mas: Float = 1f) = t.b(al * (0.4f + 0.6f * (t.k * mas / 0.6f).coerceIn(0f, 1f)), mas)
    val izq: Boolean get() = t.izq
    /**
     * El ánimo: su color hundido por la distancia a la luz sin perder el tono (lejos, el cian es un azul petróleo hondo y el beis,
     * un pardo), que solo se enciende cerca de una luz.
     */
    fun animo(col: Color, mas: Float = 1f): Color {
        val k = (t.k * mas).coerceIn(0f, 1.3f)
        val apagado = Color(col.red * 0.3f + 0.012f, col.green * 0.3f + 0.02f, col.blue * 0.33f + 0.05f, col.alpha)
        val lit = t.c(col, mas)
        val vivo = mezcla(col, lit, 0.4f)
        return mezcla(apagado, vivo, (0.14f + 1.1f * k.pow(1.2f)).coerceAtMost(0.97f))
    }

    /**
     * El papel de la etiqueta: teñido de la luz que le llega (cian junto a la pantalla, rojo junto a la regleta, ámbar al pie) y,
     * lejos, hundido hasta lo justo para que la tinta siga dando 4.5 a 1: un papel en penumbra del color de su luz, no un blanco.
     */
    fun etiqueta(base: Color = PAPEL): Color {
        val ke = t.k.coerceIn(0f, 1f).pow(1.6f)
        val l = t.l
        val porLuz = Color(base.red * (0.55f + 0.45f * l.red), base.green * (0.55f + 0.45f * l.green), base.blue * (0.55f + 0.45f * l.blue))
        val lejos = conLuminanciaCiber(mezcla(mezcla(base, porLuz, 0.6f), PaletaCiber.PARED, 0.35f), ETIQUETA_MINIMA_CIBER)
        val cerca = conLuminanciaCiber(porLuz, 0.42f)
        val e = mezcla(lejos, cerca, ke)
        return if (luminanciaCiber(e) < ETIQUETA_MINIMA_CIBER) conLuminanciaCiber(e, ETIQUETA_MINIMA_CIBER) else e
    }
}

private const val TAM_DIA_CIBER = 16.5f
private const val TAM_MES_CIBER = 6.8f

/** El mes corto en español («nov.»), de lo que trae el volumen (el tema lo nombra «nov»). */
private fun mesCortoCiber(mes: String): String {
    val letras = mes.filter { it.isLetter() }
    return if (letras.length >= 3) letras.take(3).lowercase() + "." else letras.lowercase()
}

/** El día, grande, y el mes, chico, dentro de [r], en [tinta]. */
private fun DrawScope.fechaCiber(r: Rect, v: Volumen, medidor: TextMeasurer, tinta: Color) {
    val corte = r.top + r.height * 0.7f
    textoEn(medidor, v.dia, Rect(r.left, r.top - r.height * 0.04f, r.right, corte), Letra.terminal, tinta, TAM_DIA_CIBER, peso = FontWeight.Bold)
    textoEn(medidor, mesCortoCiber(v.mes), Rect(r.left, corte - r.height * 0.06f, r.right, r.bottom), Letra.terminal, tinta.copy(alpha = 0.9f), TAM_MES_CIBER, minimo = 4f)
}

/** La etiqueta de papel del día: el papel con su luz, la sombra de abajo, el filo de arriba, el día y el mes y, en los Sueños, la luna. */
private fun DrawScope.etiquetaCiber(r: Rect, v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, base: Color = PAPEL, borde: Color? = null) {
    val u = density
    drawRect(l.s(0.35f), r.translate(1f * u, 1.3f * u).topLeft, r.size)
    drawRect(l.etiqueta(base), r.topLeft, r.size)
    borde?.let { drawRect(l.etiqueta(it), r.topLeft, Size(r.width, r.height * 0.1f)) }
    drawRect(l.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
    drawRect(l.s(0.18f), Offset(r.left, r.bottom - r.height * 0.1f), Size(r.width, r.height * 0.1f))
    fechaCiber(Rect(r.left, r.top + r.height * 0.08f, r.right, r.bottom - r.height * 0.04f), v, medidor, TINTA)
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
    drawPath(luna, mezcla(l.c(Color(0xFFE6ECF8), 1.3f), Color(0xFFDCE4F6), 0.45f))
    drawPath(luna, l.b(0.6f, 1.3f), style = Stroke(0.6f * density))
}

/**
 * Las tres clases de entrada del cuarto de Terminal, elegidas con la semilla del día (la clase sale de la semilla, nunca de su
 * sitio; el sitio solo dice qué luz le llega):
 * - el disquete de 5¼ en su funda de papel del ánimo, con la muesca del pulgar; el disco negro asoma por arriba con su muesca de
 *   protección, el agujero del centro y la etiqueta del día;
 * - la caja de disquetes: el cuerpo de plástico del ánimo con la etiqueta del día y la tapa ahumada, cerrada (los disquetes se ven
 *   tenues dentro) o abierta hacia atrás (los disquetes de pie con sus separadores de color);
 * - la bolsa de cierre: plástico transparente, blando y combado, con el cierre de presión y la solapa; dentro, disquetes de 3½ del
 *   ánimo con su obturador de metal, un poco abiertos en abanico; la etiqueta del día en el de delante.
 * Sin ánimo: la funda blanca, la caja negra y los disquetes grises. En los Sueños, una pegatina de media luna en cada una.
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
        1 -> cajaCiber(v, medidor, l, tinte)
        else -> bolsaCiber(v, medidor, l, tinte)
    }
}

/** El disquete de 5¼ en su funda de papel. */
private fun DrawScope.fundaCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    // Cuánto asoma el disco (de la semilla), y el lado de la funda: cabe a lo ancho y a lo alto, y asoma lo justo para su etiqueta.
    val lab = 24.5f * u
    val asoma = 0.62f + 0.14f * azar(v.semilla, 5)
    var s = minOf(w * 0.96f, h / (1f + asoma))
    val pk = maxOf(s * asoma, lab + s * 0.12f).coerceAtMost(h - s * 0.8f)
    if (s + pk > h) s = h - pk
    val x0 = (w - s) / 2
    val funda = Rect(x0, h - s, x0 + s, h)
    val disco = Rect(funda.left + s * 0.008f, funda.top - pk, funda.right - s * 0.008f, funda.top - pk + s * 0.985f)
    val giro = if (azar(v.semilla, 6) > 0.72f) (if (azar(v.semilla, 7) > 0.5f) 3.5f else -3.5f) else 0f
    asientoCiber(l, funda.left, funda.right, disco.top)
    rotate(giro, Offset(if (giro > 0f) funda.right else funda.left, h)) {
        // El disco negro, por detrás de la funda: la funda de plástico con su brillo, la muesca de protección, el agujero del centro
        // con su anillo y el agujero índice.
        val disc = Path().apply { addRect(disco) }
        val muesca = Path().apply { addRect(Rect(disco.right - s * 0.045f, disco.top + s * 0.17f, disco.right + 1f, disco.top + s * 0.27f)) }
        val forma = Path().apply { op(disc, muesca, PathOperation.Difference) }
        drawPath(forma, l.c(mezcla(NEGRO, NOCHE, 0.1f), 1.3f))
        clipPath(forma) {
            drawRect(l.b(0.3f), disco.topLeft, Size(disco.width * 0.06f, disco.height))
            drawRect(l.b(0.6f), disco.topLeft, Size(disco.width, 1f * u))
            // El brillo del plástico en diagonal.
            drawLine(Color.White.copy(alpha = 0.09f), Offset(disco.left + s * 0.55f, disco.top + s * 0.5f), Offset(disco.left + s * 0.95f, disco.top), s * 0.14f)
            val hub = Offset(disco.center.x, disco.top + s * 0.49f)
            drawCircle(l.c(mezcla(CAFE, NEGRO, 0.3f), 1.2f), s * 0.17f, hub)
            drawCircle(l.c(ACERO_OSC, 1.2f), s * 0.11f, hub)
            drawCircle(NOCHE, s * 0.07f, hub)
            drawCircle(NOCHE, s * 0.024f, Offset(disco.center.x + s * 0.2f, disco.top + s * 0.42f))
        }
        drawPath(forma, l.b(0.35f), style = Stroke(0.8f * u))
        // La etiqueta del día, arriba a la izquierda del disco: a la derecha queda el plástico negro con la muesca.
        val ew = maxOf(s * 0.6f, 23f * u).coerceAtMost(s * 0.86f)
        val et = Rect(disco.left + s * 0.06f, disco.top + s * 0.055f, disco.left + s * 0.06f + ew, disco.top + s * 0.055f + minOf(lab, pk * 0.9f))
        etiquetaCiber(et, v, medidor, l, borde = if (Math.floorMod(v.semilla, 3) == 0) Color(0xFF3F8A5A) else Color(0xFF2F5E9E))
        // La funda: papel del ánimo (blanca sin ánimo), con la muesca del pulgar arriba, las solapas pegadas a los lados y sus planos.
        val papel = tinte?.let { l.animo(it) } ?: l.c(mezcla(PAPEL, Color.White, 0.2f), 0.95f)
        val pulgar = s * 0.13f
        val cuerpo = Path().apply {
            addRect(funda)
            op(this, Path().apply { addOval(Rect(funda.center.x - pulgar, funda.top - pulgar, funda.center.x + pulgar, funda.top + pulgar)) }, PathOperation.Difference)
        }
        drawPath(cuerpo, papel)
        clipPath(cuerpo) {
            planosCiber(funda, l.t, 0.9f)
            // Las solapas de atrás, dobladas y pegadas: dos franjas más oscuras a los lados, y la de abajo.
            drawRect(l.s(0.16f), funda.topLeft, Size(s * 0.07f, s))
            drawRect(l.s(0.16f), Offset(funda.right - s * 0.07f, funda.top), Size(s * 0.07f, s))
            drawRect(l.s(0.12f), Offset(funda.left, funda.bottom - s * 0.06f), Size(s, s * 0.06f))
            // Un pliegue y un roce de uso.
            drawLine(l.s(0.22f), Offset(funda.left + s * 0.2f, funda.top + s * 0.6f), Offset(funda.left + s * 0.55f, funda.top + s * 0.75f), 0.8f * u)
            drawLine(l.b(0.3f), Offset(funda.left + s * 0.2f, funda.top + s * 0.6f + 1f * u), Offset(funda.left + s * 0.55f, funda.top + s * 0.75f + 1f * u), 0.6f * u)
            if (azar(v.semilla, 8) > 0.5f) drawOval(l.c(CAFE).copy(alpha = 0.3f), Offset(funda.left + s * 0.55f, funda.top + s * 0.55f), Size(s * 0.3f, s * 0.22f), style = Stroke(1.2f * u))
            // Lo lleno: más renglones a lápiz en la funda cuantas más palabras.
            val renglones = 1 + (v.lleno * 3).toInt()
            garabatosCiber(Rect(funda.left + s * 0.14f, funda.top + s * 0.25f, funda.right - s * 0.2f, funda.top + s * (0.25f + 0.1f * renglones)), renglones,
                mezcla(papel, NOCHE, 0.55f).copy(alpha = 0.7f), v.semilla, 0.9f * u)
        }
        drawLine(l.b(0.6f), Offset(funda.left, funda.top), Offset(funda.center.x - pulgar, funda.top), 1f * u)
        drawLine(l.b(0.6f), Offset(funda.center.x + pulgar, funda.top), Offset(funda.right, funda.top), 1f * u)
        if (v.noche) lunaCiber(Offset(funda.left + s * 0.78f, funda.bottom - s * 0.22f), s * 0.11f, l)
    }
}

/** La caja de disquetes con su tapa ahumada, cerrada o abierta. */
private fun DrawScope.cajaCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val abierta = azar(v.semilla, 9) > 0.3f
    val cuerpoAlto = maxOf(h * 0.48f, 31f * u)
    val x0 = w * 0.02f; val x1 = w * 0.98f
    val cuerpo = Rect(x0, h - minOf(cuerpoAlto, h * 0.62f), x1, h)
    val arriba = if (abierta) h * 0.04f else h * 0.14f
    asientoCiber(l, x0, x1, arriba)
    val plast = tinte?.let { l.animo(it) } ?: l.c(NEGRO, 1.2f)
    val ahumado = l.c(mezcla(NEGRO, CAFE, 0.4f), 1.1f)
    val cols = listOf(NEGRO, Color(0xFF2F5E9E), Color(0xFFB8452F), Color(0xFF3F8A5A), mezcla(PAPEL, NOTA, 0.2f))
    if (abierta) {
        // La tapa abierta hacia atrás: una banda ahumada de pie detrás, con su filo.
        val tapa = Rect(x0 + w * 0.04f, arriba, x1 - w * 0.04f, cuerpo.top + h * 0.06f)
        drawRect(ahumado.copy(alpha = 0.78f), tapa.topLeft, tapa.size)
        drawRect(l.b(0.5f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(l.b(0.3f), tapa.topLeft, Size(w * 0.04f, tapa.height))
        // Los disquetes de pie, en fila de atrás adelante y un poco abiertos: de cada uno asoma el canto de arriba, negro, con su
        // etiqueta; entre ellos, un separador con su pestaña de color. El de delante, el más bajo.
        val n = 4 + (v.lleno * 3).toInt()
        val hueco = cuerpo.top - arriba
        for (j in 0 until n) {
            val f = j / (n - 1f)
            val top = arriba + hueco * (0.1f + 0.62f * f) + hueco * 0.05f * azar(v.semilla + 1, j)
            val dx = w * 0.02f * (azar(v.semilla + 2, j) - 0.5f)
            val d = Rect(x0 + w * 0.06f + dx, top, x1 - w * 0.06f + dx, cuerpo.top + 1f)
            if (j == n / 2) {
                // El separador: cartulina gris con su pestaña de color, más alta que los disquetes de alrededor.
                val col = if (Math.floorMod(v.semilla, 2) == 0) Color(0xFFE0B04A) else Color(0xFFB8452F)
                val px = if (Math.floorMod(v.semilla ushr 3, 2) == 0) d.left + d.width * 0.1f else d.right - d.width * 0.38f
                drawRect(l.s(0.35f), Offset(px + 1f * u, top - hueco * 0.16f + 1f * u), Size(d.width * 0.28f, hueco * 0.16f))
                drawRect(l.c(col, 1.15f), Offset(px, top - hueco * 0.16f), Size(d.width * 0.28f, hueco * 0.17f))
                drawRect(l.b(0.6f), Offset(px, top - hueco * 0.16f), Size(d.width * 0.28f, 0.8f * u))
            }
            drawRect(l.s(0.4f), Offset(d.left, d.top - 1.5f * u), Size(d.width, 1.5f * u))
            drawRect(l.c(mezcla(NEGRO, NOCHE, 0.1f), 1.25f), d.topLeft, d.size)
            drawRect(l.b(0.55f), d.topLeft, Size(d.width, 0.9f * u))
            val ex = if (j % 2 == 0) d.left + d.width * 0.08f else d.left + d.width * 0.3f
            drawRect(l.c(mezcla(PAPEL, if (j % 3 == 1) NOTA else PAPEL, 0.4f), 0.95f), Offset(ex, d.top + hueco * 0.035f), Size(d.width * 0.55f, hueco * 0.1f))
            drawRect(l.c(cols[Math.floorMod(v.semilla + j, cols.size)], 1.1f), Offset(ex, d.top + hueco * 0.035f), Size(d.width * 0.55f, hueco * 0.022f))
        }
    } else {
        // La tapa cerrada: el tejadillo ahumado inclinado; debajo se ven, tenues, los cantos de los disquetes con sus etiquetas, y el
        // brillo del acrílico.
        val tapa = Path().apply {
            moveTo(x0, cuerpo.top + 1f); lineTo(x0 + w * 0.05f, arriba); lineTo(x1 - w * 0.05f, arriba); lineTo(x1, cuerpo.top + 1f); close()
        }
        drawPath(tapa, l.c(mezcla(NEGRO, NOCHE, 0.2f), 1.1f))
        clipPath(tapa) {
            val hueco = cuerpo.top - arriba
            for (j in 0 until 5) {
                val top = arriba + hueco * (0.18f + 0.15f * j)
                drawRect(l.s(0.5f), Offset(x0, top - 1.5f * u), Size(x1 - x0, 1.5f * u))
                val ex = if (j % 2 == 0) x0 + w * 0.14f else x0 + w * 0.34f
                drawRect(l.c(mezcla(PAPEL, if (j % 3 == 1) NOTA else PAPEL, 0.4f), 0.85f), Offset(ex, top + hueco * 0.03f), Size(w * 0.46f, hueco * 0.08f))
            }
            drawRect(ahumado.copy(alpha = 0.62f), Offset(x0, arriba), Size(x1 - x0, hueco))
            drawLine(Color.White.copy(alpha = 0.16f), Offset(x0 + w * 0.2f, cuerpo.top), Offset(x0 + w * 0.42f, arriba), 3f * u)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(x0 + w * 0.5f, cuerpo.top), Offset(x0 + w * 0.62f, arriba), 1.6f * u)
            drawRect(l.b(0.6f), Offset(x0, arriba), Size(w, 1f * u))
        }
        drawPath(tapa, l.b(0.35f), style = Stroke(0.8f * u))
        // La pestaña del cierre, en el canto de delante.
        drawRect(l.c(mezcla(NEGRO, CAFE, 0.3f)), Offset(w * 0.44f, cuerpo.top - h * 0.02f), Size(w * 0.12f, h * 0.035f))
    }
    // El cuerpo: plástico del ánimo con sus planos, las bisagras, el canto de arriba y la etiqueta del día.
    drawRect(plast, cuerpo.topLeft, cuerpo.size)
    clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
        planosCiber(cuerpo, l.t, 1f)
        drawRect(l.s(0.25f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.08f), Size(cuerpo.width, cuerpo.height * 0.08f))
        // Las nervaduras del plástico a los lados.
        for (j in 0 until 3) {
            drawRect(l.s(0.18f), Offset(cuerpo.left + w * (0.04f + 0.03f * j), cuerpo.top + cuerpo.height * 0.2f), Size(1f * u, cuerpo.height * 0.6f))
            drawRect(l.s(0.18f), Offset(cuerpo.right - w * (0.05f + 0.03f * j), cuerpo.top + cuerpo.height * 0.2f), Size(1f * u, cuerpo.height * 0.6f))
        }
    }
    drawRect(l.b(0.7f), cuerpo.topLeft, Size(cuerpo.width, 1.1f * u))
    drawRect(l.s(0.4f), Offset(cuerpo.left, cuerpo.top + 1.1f * u), Size(cuerpo.width, 1f * u))
    val ew = maxOf(w * 0.6f, 23f * u).coerceAtMost(w * 0.84f)
    val eh = minOf(24.5f * u, cuerpo.height * 0.8f)
    val ey = cuerpo.top + (cuerpo.height - eh) * 0.45f
    val et = Rect(w / 2 - ew / 2, ey, w / 2 + ew / 2, ey + eh)
    etiquetaCiber(et, v, medidor, l, base = mezcla(PAPEL, NOTA, 0.18f))
    if (v.noche) lunaCiber(Offset(cuerpo.right - w * 0.16f, cuerpo.top + cuerpo.height * 0.14f), w * 0.08f, l)
}

/**
 * La bolsa de cierre con los disquetes de 3½: el plástico blando, combado y con sus arrugas, la costura soldada de los lados y de
 * abajo, el cierre de presión azul y la solapa de arriba, doblada hacia un lado; dentro, los disquetes del ánimo uno encima de otro
 * en abanico, que la llenan; la etiqueta del día en el de delante.
 */
private fun DrawScope.bolsaCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?) {
    val u = density; val w = size.width; val h = size.height
    val panza = w * (0.015f + 0.02f * azar(v.semilla, 10) + 0.02f * v.lleno)
    val arriba = h * (0.03f + 0.04f * azar(v.semilla, 11))
    val x0 = w * 0.05f; val x1 = w * 0.95f
    val zy = arriba + h * 0.1f
    val cae = if (azar(v.semilla, 12) > 0.5f) 1f else -1f
    // La bolsa: por arriba la solapa, más angosta y caída hacia un lado; por abajo, la panza.
    val bolsa = Path().apply {
        moveTo(x0 + w * 0.04f, arriba + (if (cae > 0f) 0f else h * 0.03f))
        cubicTo(w * 0.35f, arriba - h * 0.01f, w * 0.65f, arriba + h * 0.02f, x1 - w * 0.04f, arriba + (if (cae > 0f) h * 0.03f else 0f))
        lineTo(x1, zy)
        cubicTo(x1 + panza * 0.4f, h * 0.45f, x1 + panza, h * 0.8f, x1 - w * 0.03f, h - 1f * u)
        cubicTo(w * 0.7f, h + 0.5f * u, w * 0.3f, h + 0.5f * u, x0 + w * 0.03f, h - 1f * u)
        cubicTo(x0 - panza, h * 0.8f, x0 - panza * 0.4f, h * 0.45f, x0, zy)
        close()
    }
    asientoCiber(l, x0, x1, arriba)
    val recuesta = (azar(v.semilla, 13) - 0.5f) * 7f
    rotate(recuesta, Offset(if (recuesta > 0f) x1 else x0, h)) { bolsaDentroCiber(v, medidor, l, tinte, bolsa, panza, arriba, x0, x1, zy) }
}

/** La bolsa ya puesta (recostada o no): lo de dentro, el plástico, el cierre y la luna. */
private fun DrawScope.bolsaDentroCiber(v: Volumen, medidor: TextMeasurer, l: LuzVolumenCiber, tinte: Color?, bolsa: Path, panza: Float,
                                       arriba: Float, x0: Float, x1: Float, zy: Float) {
    val u = density; val w = size.width; val h = size.height
    drawPath(bolsa, l.c(mezcla(NOCHE, ACERO_OSC, 0.35f), 0.8f).copy(alpha = 0.5f))
    // Los disquetes, uno encima de otro en abanico, que llenan la bolsa; el de delante abajo, con la etiqueta.
    val dw = (x1 - x0) * 0.86f
    val dh = dw * 1.04f
    val plast = tinte?.let { l.animo(it) } ?: l.c(mezcla(ACERO, NEGRO, 0.35f), 1.05f)
    val cx = w / 2; val pie = h - h * 0.035f
    val hueco = (pie - zy - h * 0.03f - dh).coerceAtLeast(0f)
    val n = if (hueco > dh * 0.5f) 3 else 2
    clipPath(bolsa) {
        for (j in n - 1 downTo 0) {
            val giro = if (j == 0) 0f else (if ((j + v.semilla) % 2 == 1) -1f else 1f) * (3f + 4f * j)
            val alza = if (n > 1) hueco * j / (n - 1f) else 0f
            val d = Rect(cx - dw / 2, pie - dh - alza, cx + dw / 2, pie - alza)
            rotate(giro, d.center) {
                val col = if (j == 0) plast else mezcla(plast, NOCHE, 0.14f * j)
                drawRect(l.s(0.4f), d.translate(1.5f * u, 2f * u).topLeft, d.size)
                val chaflan = dw * 0.06f
                val cuerpo = poligonoCiber(d.left, d.top, d.right - chaflan, d.top, d.right, d.top + chaflan, d.right, d.bottom, d.left, d.bottom)
                drawPath(cuerpo, col)
                clipPath(cuerpo) {
                    planosCiber(d, l.t, 0.8f)
                    drawRect(l.s(0.3f), Offset(d.left + dw * 0.16f, d.top), Size(dw * 0.66f, dh * 0.33f))
                    drawRect(l.c(ACERO, 1.15f), Offset(d.left + dw * 0.22f, d.top), Size(dw * 0.46f, dh * 0.31f))
                    drawRect(l.b(0.6f), Offset(d.left + dw * 0.22f, d.top), Size(dw * 0.46f, 0.9f * u))
                    drawRect(l.c(mezcla(NEGRO, CAFE, 0.4f)), Offset(d.left + dw * 0.48f, d.top + dh * 0.06f), Size(dw * 0.12f, dh * 0.19f))
                    drawRect(l.b(0.4f), d.topLeft, Size(d.width, 0.8f * u))
                    drawRect(NOCHE.copy(alpha = 0.7f), Offset(d.left + dw * 0.05f, d.bottom - dh * 0.1f), Size(dw * 0.07f, dh * 0.06f))
                }
                if (j == 0) {
                    val eh = minOf(24.5f * u, dh * 0.76f)
                    val et = Rect(d.left + dw * 0.1f, d.bottom - dh * 0.04f - eh, d.right - dw * 0.1f, d.bottom - dh * 0.04f)
                    etiquetaCiber(et, v, medidor, l, borde = if (Math.floorMod(v.semilla, 2) == 0) Color(0xFFB8452F) else Color(0xFF2F5E9E))
                }
            }
        }
        // El plástico encima: los brillos largos, las arrugas de la panza y de la solapa, y lo que se ve más turbio en los bordes.
        drawLine(Color.White.copy(alpha = 0.22f), Offset(x0 + w * 0.08f, zy + h * 0.06f), Offset(x0 + w * 0.14f, h * 0.88f), 3.2f * u, StrokeCap.Round)
        drawLine(Color.White.copy(alpha = 0.12f), Offset(x1 - w * 0.12f, zy + h * 0.15f), Offset(x1 - w * 0.08f, h * 0.7f), 2f * u, StrokeCap.Round)
        for (j in 0 until 4) {
            val y = h * (0.5f + 0.11f * j) + h * 0.03f * azar(v.semilla, 20 + j)
            val xa = if (j % 2 == 0) x0 else x1 - w * 0.25f
            drawLine(l.b(0.35f), Offset(xa + w * 0.02f, y), Offset(xa + w * 0.2f, y + h * 0.025f * (if (j % 2 == 0) 1f else -1f)), 0.9f * u)
            drawLine(l.s(0.25f), Offset(xa + w * 0.02f, y + 1.2f * u), Offset(xa + w * 0.2f, y + 1.2f * u + h * 0.025f * (if (j % 2 == 0) 1f else -1f)), 0.8f * u)
        }
        drawRect(Color.White.copy(alpha = 0.07f), Offset(x0 - panza, 0f), Size(w * 0.06f + panza, h))
        drawRect(Color.White.copy(alpha = 0.07f), Offset(x1 - w * 0.06f, 0f), Size(w * 0.06f + panza, h))
        drawRect(l.s(0.18f), Offset(x1 - w * 0.16f, 0f), Size(w * 0.16f, h))
        // La solapa, sobre el cierre: el plástico doblado, más claro, con dos pliegues.
        drawRect(l.c(mezcla(ACERO, PAPEL, 0.5f), 0.9f).copy(alpha = 0.45f), Offset(0f, 0f), Size(w, zy))
        drawLine(l.b(0.4f), Offset(w * 0.25f, arriba + h * 0.02f), Offset(w * 0.32f, zy - 1f * u), 0.8f * u)
        drawLine(l.b(0.4f), Offset(w * 0.66f, arriba + h * 0.015f), Offset(w * 0.6f, zy - 1f * u), 0.8f * u)
    }
    // El cierre de presión: los dos labios azules, de lado a lado.
    drawLine(l.c(Color(0xFF3E6DB8), 1.2f), Offset(x0, zy), Offset(x1, zy + h * 0.006f), 2f * u)
    drawLine(l.c(Color(0xFF3E6DB8), 1.2f), Offset(x0, zy + 3f * u), Offset(x1, zy + h * 0.006f + 3f * u), 1.2f * u)
    // El borde del plástico, con su filo de luz, y la costura soldada de abajo.
    drawPath(bolsa, l.b(0.7f), style = Stroke(1.3f * u))
    drawLine(l.b(0.45f), Offset(x0 + w * 0.1f, h - 2.5f * u), Offset(x1 - w * 0.1f, h - 2.5f * u), 1f * u)
    if (v.noche) lunaCiber(Offset(x1 - w * 0.2f, zy + h * 0.08f), w * 0.08f, l)
}
