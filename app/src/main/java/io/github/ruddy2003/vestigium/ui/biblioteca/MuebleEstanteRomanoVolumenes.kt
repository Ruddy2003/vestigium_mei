package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
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
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos

/** Las escalas del ánimo de Romano, la de día y la de noche (bajo, medio, alto). */
private val ESCALAS_ROMANO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ROMANO, false) to juegoTonos(TemaId.ROMANO, true) }

/**
 * Los tintes del cuero y las pinturas de la madera con que se dice el ánimo, en el mismo orden que la escala del tema (de lo
 * peor a lo mejor): granate, rojo teja, ocre, verde y verde azulado hondo. Así el ánimo es un color con cuerpo, no un pastel.
 */
private val TINTES_ROMANO = listOf(Color(0xFF85252A), Color(0xFFA5452B), Color(0xFFB8872F), Color(0xFF4E7A41), Color(0xFF2C5E5E))

private fun cuadrado(x: Float) = x * x

/**
 * Dónde cae un color del ánimo en la escala del tema, de 0 (bajo) a 1 (alto): se busca el tramo y la fracción que más se le
 * acercan en la escala que se usó para pintarlo (la de día o, en el modo oscuro, la de noche, que cambia de colores). Si no
 * está en la escala, `null`.
 */
internal fun puntoEnEscalaRomano(t: Color, oscuro: Boolean): Float? {
    val escala = if (oscuro) ESCALAS_ROMANO.second else ESCALAS_ROMANO.first
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadrado(t.red - a0.red - dr * f) + cuadrado(t.green - a0.green - dg * f) + cuadrado(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/** El tinte que corresponde a un punto [p] de la escala (de 0 a 1), entre los de [TINTES_ROMANO]. */
private fun tinteRomano(p: Float): Color {
    val x = p.coerceIn(0f, 1f) * (TINTES_ROMANO.size - 1)
    val i = x.toInt().coerceAtMost(TINTES_ROMANO.size - 2)
    return mezcla(TINTES_ROMANO[i], TINTES_ROMANO[i + 1], x - i)
}

/**
 * Las tres clases de entrada del estante de Romano ([MuebleEstanteRomano]), elegidas con la semilla del día: tres cosas del
 * cuarto de los oficiales que se entienden de un vistazo y llenan el hueco:
 * - el legajo de hojas de madera: el mazo de hojas finas de aliso visto de canto, con sus muescas y el cordel cruzado, metido
 *   hasta media altura en su funda de cuero teñido y cosido; la tablilla de madera atada al cordel;
 * - las dos arquetas apiladas: cajas de tablas pintadas, con sus cantoneras, fajas y clavos de bronce, la cerradura y el asa;
 *   la placa de hueso del día clavada, chica, con sus dos taladros;
 * - la bolsa de cuero: blanda, ancha abajo y asentada, algo ladeada, con el cuello fruncido ancho, el cordel con sus vueltas,
 *   el sello de plomo y el marbete de plomo del día.
 * El ánimo es el tinte del cuero o la pintura de la madera (granate, teja, ocre, verde), en el orden de la escala. [luzDe] dice
 * de qué lado llega la luz: -1, de la izquierda (la ventana de día); 1, de la derecha (la linterna de noche).
 */
internal object VolumenEstanteRomano {
    private val P = PaletaRomano

    /** El día y el mes de las etiquetas, en sp, y su tamaño, en dp. */
    private const val TAM_DIA = 12f
    private const val TAM_MES = 5.4f
    private const val ALTO_ETIQUETA = 18f
    private const val ANCHO_ETIQUETA = 21f
    /** El marfil cálido del hueso y de la madera clara de las etiquetas, y el plomo nuevo del marbete. */
    private val MARFIL = Color(0xFFEBDDBC)
    private val ALISO_TABLILLA = Color(0xFFE3CC9C)
    private val PLOMO_CLARO = Color(0xFFB9BAB6)

    /** El color del cuerpo: el tinte del ánimo en su sitio de la escala, o [crudo] si no se marcó, con la luz del momento. */
    private fun cuerpo(v: Volumen, a: AireRomano, k: Float, crudo: Color): Color {
        val t = v.tono ?: return a.luz(crudo, k)
        val p = puntoEnEscalaRomano(t, a.oscuro)
        val base = if (p != null) tinteRomano(p) else mezcla(t, P.TINTE_HONDO, 0.2f)
        return a.animo(base, k)
    }

    private fun mesCorto(mes: String): String = if (mes.length > 5) mes.take(3) + "." else mes

    /** El filo de la linterna en lo que la mira y, de noche, el roce frío de la luna en el canto izquierdo. */
    private fun DrawScope.filo(x0: Float, x1: Float, y0: Float, y1: Float, a: AireRomano, k: Float, luzDe: Float) {
        if (!a.oscuro) return
        val u = density
        if (k > 0.06f) {
            drawRect(a.filo(0.65f * k), Offset(x0 + 1.5f * u, y0), Size(maxOf(0f, x1 - x0 - 3f * u), 1.1f * u))
            drawRect(a.filo(0.5f * k), Offset(if (luzDe < 0) x0 else x1 - 1.2f * u, y0 + 1.5f * u), Size(1.2f * u, maxOf(0f, y1 - y0 - 3f * u)))
        }
        drawRect(a.lunar(0.16f * (1f - k)), Offset(x0 + 0.4f * u, y0 + 2f * u), Size(1f * u, maxOf(0f, y1 - y0 - 4f * u)))
    }

    /** La luna de plata: un creciente con su sombra y su brillo. */
    private fun DrawScope.luna(c: Offset, r: Float, fondo: Color, a: AireRomano, k: Float) {
        val plata = if (a.oscuro) mezcla(P.LUNA, a.hondo, 0.18f - 0.18f * k) else P.LUNA
        drawCircle(a.sombra(0.4f), r, c + Offset(r * 0.18f, r * 0.22f))
        drawCircle(plata, r, c)
        drawCircle(fondo, r * 0.8f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
        drawCircle(Color.White.copy(alpha = 0.6f), r * 0.16f, Offset(c.x - r * 0.5f, c.y + r * 0.1f))
    }

    /** Una costura de punto de [p0] a [p1], añadida a [camino]. */
    private fun costura(camino: Path, p0: Offset, p1: Offset, paso: Float) {
        val d = (p1 - p0).getDistance()
        if (d < 1f || paso < 0.5f) return
        val n = (d / paso).toInt().coerceAtMost(60)
        for (i in 0 until n) {
            val f0 = i * paso / d; val f1 = (i * paso + paso * 0.55f) / d
            camino.moveTo(p0.x + (p1.x - p0.x) * f0, p0.y + (p1.y - p0.y) * f0)
            camino.lineTo(p0.x + (p1.x - p0.x) * f1, p0.y + (p1.y - p0.y) * f1)
        }
    }

    /** El número del día, grande, y el mes, chico, dentro de [r], en tinta. */
    private fun DrawScope.fecha(r: Rect, v: Volumen, medidor: TextMeasurer, tinta: Color) {
        val corte = r.top + r.height * 0.7f
        textoEn(medidor, v.dia, Rect(r.left, r.top, r.right, corte), P.LETRA, tinta, TAM_DIA, peso = FontWeight.Bold)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left, corte - r.height * 0.06f, r.right, r.bottom), P.LETRA, tinta.copy(alpha = 0.8f), TAM_MES, peso = FontWeight.SemiBold, minimo = 4f)
    }

    /** La tablilla de madera clara atada al cordel: el agujero, la veta, el canto de luz, su sombra y la fecha. */
    private fun DrawScope.tablillaMadera(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, giro: Float) {
        val u = density
        rotate(giro, Offset(r.center.x, r.top)) {
            drawRoundRect(a.sombra(0.45f), Offset(r.left + 1f * u, r.top + 1.4f * u), r.size, CornerRadius(1.5f * u))
            drawRoundRect(a.etiqueta(ALISO_TABLILLA, k), r.topLeft, r.size, CornerRadius(1.5f * u))
            for (j in 0 until 3) drawLine(mezcla(a.etiqueta(ALISO_TABLILLA, k), P.ALISO_OSC, 0.45f).copy(alpha = 0.45f),
                Offset(r.left + 1.5f * u, r.top + r.height * (0.12f + 0.38f * j)), Offset(r.right - 1.5f * u, r.top + r.height * (0.14f + 0.38f * j)), 0.4f * u)
            drawRect(a.claro(0.5f), r.topLeft, Size(r.width, 0.7f * u))
            drawRect(a.sombra(0.15f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            fecha(Rect(r.left + 1f * u, r.top + 2.2f * u, r.right - 1f * u, r.bottom), v, medidor, P.TINTA)
            drawCircle(a.luz(P.TINTE_HONDO, k), 0.8f * u, Offset(r.center.x, r.top + 1.6f * u))
        }
    }

    /** La placa de hueso clavada: marfil cálido, con su bisel, sus dos taladros con clavo de bronce y la fecha. */
    private fun DrawScope.placaHueso(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float) {
        val u = density
        drawRoundRect(a.sombra(0.45f), Offset(r.left + 0.8f * u, r.top + 1.2f * u), r.size, CornerRadius(2f * u))
        drawRoundRect(a.etiqueta(MARFIL, k), r.topLeft, r.size, CornerRadius(2f * u))
        drawRoundRect(a.claro(0.4f), r.topLeft, Size(r.width, r.height * 0.3f), CornerRadius(2f * u))
        drawRect(a.sombra(0.14f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        fecha(Rect(r.left + 3.4f * u, r.top + 0.6f * u, r.right - 3.4f * u, r.bottom), v, medidor, P.TINTA)
        remacheRomano(Offset(r.left + 1.9f * u, r.center.y), 0.9f * u, a, k, P.BRONCE)
        remacheRomano(Offset(r.right - 1.9f * u, r.center.y), 0.9f * u, a, k, P.BRONCE)
    }

    /** El marbete de plomo: la lámina gris clara cortada a mano, con su agujero, el brillo del metal y la fecha rayada. */
    private fun DrawScope.marbetePlomo(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, semilla: Int, giro: Float) {
        val u = density
        rotate(giro, Offset(r.center.x, r.top)) {
            val p = poligonoRomano(r.left, r.top + 0.8f * u * azar(semilla, 61), r.right - 0.6f * u, r.top, r.right, r.bottom - 1f * u * azar(semilla, 62),
                r.left + 0.5f * u, r.bottom)
            drawPath(corridoRomano(p, 1f * u, 1.4f * u), a.sombra(0.45f))
            drawPath(p, a.etiqueta(PLOMO_CLARO, k))
            drawRect(a.claro(0.45f), Offset(r.left, r.top + 0.5f * u), Size(r.width * 0.6f, 0.8f * u))
            drawRect(a.sombra(0.12f), Offset(r.left, r.bottom - r.height * 0.35f), Size(r.width, r.height * 0.35f))
            fecha(Rect(r.left + 1f * u, r.top + 2.2f * u, r.right - 1f * u, r.bottom), v, medidor, P.TINTA)
            drawCircle(a.luz(P.TINTE_HONDO, k), 0.8f * u, Offset(r.center.x, r.top + 1.6f * u))
        }
    }

    // ---------------------------------------------------------------- el legajo

    /**
     * El legajo de hojas de madera, de pie: el mazo rectangular de hojas finas de aliso visto de canto (cada canto, una raya),
     * desparejo arriba, con las dos muescas a los lados y el cordel que lo ata cruzado; hasta media altura, metido en su funda de
     * cuero teñido del ánimo, con el labio doblado, la costura a los lados, su luz, su sombra y su brillo; la tablilla de madera
     * atada al cordel. Más grueso cuanto más escribiste. En los Sueños, la luna repujada en la funda.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val x0 = 1.2f * u; val x1 = w - 1.2f * u
        val boca = h * 0.44f
        // El mazo de hojas: la boca desigual arriba, el cuerpo de madera clara y los cantos.
        val hojas = Path().apply {
            moveTo(x0, boca + 2f * u)
            lineTo(x0, 2f * u + 2f * u * azar(s, 1))
            var x = x0
            var i = 0
            val paso = maxOf(2f * u, (x1 - x0) / 9f)
            while (x < x1 && i < 30) {
                val y = 0.6f * u + 3.4f * u * azar(s, 10 + i)
                lineTo(x, y); x = minOf(x1, x + paso); lineTo(x, y); i++
            }
            lineTo(x1, boca + 2f * u)
            close()
        }
        val madera = a.etiqueta(P.ALISO, k)
        drawPath(hojas, madera)
        clipPath(hojas) {
            val cantos = Path()
            var x = x0 + 1.2f * u
            var i = 0
            while (x < x1 && i < 60) { cantos.moveTo(x, 0f); cantos.lineTo(x, boca + 2f * u); x += 1.5f * u; i++ }
            drawPath(cantos, a.luz(P.ALISO_OSC, k).copy(alpha = 0.55f), style = Stroke(0.5f * u))
            drawRect(a.brillo(0.3f, k), Offset(if (luzDe < 0) x0 else x1 - (x1 - x0) * 0.25f, 0f), Size((x1 - x0) * 0.25f, boca))
            drawRect(a.sombra(0.25f), Offset(if (luzDe < 0) x1 - (x1 - x0) * 0.3f else x0, 0f), Size((x1 - x0) * 0.3f, boca + 2f * u))
            drawRect(a.sombra(0.3f), Offset(0f, boca - 3f * u), Size(w, 5f * u))
        }
        // Las muescas a los lados y el cordel cruzado que ata el mazo.
        val ym = boca * 0.32f
        val muesca = 2.6f * u
        drawPath(poligonoRomano(x0 - 0.5f, ym - muesca, x0 + muesca, ym, x0 - 0.5f, ym + muesca), a.sombra(0.55f))
        drawPath(poligonoRomano(x1 + 0.5f, ym - muesca, x1 - muesca, ym, x1 + 0.5f, ym + muesca), a.sombra(0.55f))
        val cordel = a.luz(P.CANAMO, k)
        val nudo = Offset(w * (0.5f + 0.1f * (azar(s, 4) - 0.5f)), boca + 6f * u)
        val atadura = Path().apply {
            moveTo(x0 + muesca * 0.6f, ym); lineTo(x1 - muesca * 0.6f, ym)
            moveTo(x0 + muesca * 0.6f, ym); lineTo(nudo.x, nudo.y)
            moveTo(x1 - muesca * 0.6f, ym); lineTo(nudo.x, nudo.y)
        }
        drawPath(corridoRomano(atadura, 0.6f * u, 0.8f * u), a.sombra(0.35f), style = Stroke(1.2f * u))
        drawPath(atadura, cordel, style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // La funda de cuero, hasta media altura.
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.CANAMO, 0.2f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.38f)
        val funda = Rect(x0 - 0.4f * u, boca, x1 + 0.4f * u, h)
        val forma = Path().apply { addRoundRect(RoundRect(funda, CornerRadius(2f * u))) }
        drawPath(forma, cuero)
        clipPath(forma) {
            val claroX = if (luzDe < 0) funda.left else funda.right - funda.width * 0.3f
            val hondoX = if (luzDe < 0) funda.right - funda.width * 0.28f else funda.left
            drawRect(a.brillo(0.2f, k), Offset(claroX, funda.top), Size(funda.width * 0.3f, funda.height))
            drawRect(a.sombra(0.3f), Offset(hondoX, funda.top), Size(funda.width * 0.28f, funda.height))
            drawRect(a.brillo(0.42f, k), Offset(funda.left + funda.width * (if (luzDe < 0) 0.16f else 0.78f), funda.top + 5f * u), Size(1.2f * u, funda.height - 9f * u))
            for (j in 0 until 6) drawCircle(hondo.copy(alpha = 0.4f), 0.6f * u, Offset(w * azar(s, 130 + j), funda.top + funda.height * azar(s, 140 + j)))
            drawRect(a.sombra(0.35f), Offset(funda.left, funda.bottom - 3.5f * u), Size(funda.width, 3.5f * u))
            // El labio doblado arriba.
            drawRect(mezcla(cuero, hondo, 0.5f), Offset(funda.left, funda.top), Size(funda.width, 3.2f * u))
            drawRect(a.brillo(0.35f, k), Offset(funda.left, funda.top + 3.2f * u), Size(funda.width, 0.8f * u))
        }
        val puntos = Path()
        costura(puntos, Offset(funda.left + 2.2f * u, funda.top + 5f * u), Offset(funda.left + 2.2f * u, funda.bottom - 3f * u), 2.4f * u)
        costura(puntos, Offset(funda.right - 2.2f * u, funda.top + 5f * u), Offset(funda.right - 2.2f * u, funda.bottom - 3f * u), 2.4f * u)
        drawPath(puntos, a.luz(mezcla(P.CANAMO, P.HUESO, 0.4f), k).copy(alpha = 0.85f), style = Stroke(0.7f * u))
        // El nudo y la tablilla atada.
        drawCircle(cordel, 1.8f * u, nudo)
        val an = minOf(ANCHO_ETIQUETA * u, w - 6f * u)
        val et = Rect(nudo.x - an / 2, nudo.y + 2.5f * u, nudo.x + an / 2, nudo.y + 2.5f * u + ALTO_ETIQUETA * u)
        drawLine(cordel, nudo, Offset(et.center.x, et.top + 1.6f * u), 0.8f * u)
        tablillaMadera(et, v, medidor, a, k, (azar(s, 7) - 0.5f) * 8f)
        if (v.noche) {
            val r = minOf(w * 0.15f, 4.6f * u)
            if (et.bottom + 4f * u + 2 * r < h - 4f * u) luna(Offset(w * 0.5f, et.bottom + 4f * u + r), r, cuero, a, k)
        }
        filo(funda.left, funda.right, 0f, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- las arquetas

    /**
     * Dos arquetas apiladas, la chica encima y corrida a un lado: cajas de tablas pintadas del ánimo (la de arriba, un poco más
     * honda), con la veta que asoma, la pintura gastada en los cantos, las cantoneras y la faja de bronce con sus clavos y su
     * brillo, la tapa de la de arriba con su asa, la cerradura de la de abajo; la placa de hueso del día, chica, clavada en la de
     * abajo. En los Sueños, la luna en la de arriba.
     */
    fun DrawScope.arqueta(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val pintura = cuerpo(v, a, k, mezcla(P.ROBLE_LUZ, P.CANAMO, 0.3f))
        val corte = h * 0.42f
        val lado = if (azar(s, 4) > 0.5f) 1f else -1f
        val anA = w * 0.82f
        val xa = if (lado > 0) w - anA - 0.6f * u else 0.6f * u
        val arriba = Rect(xa, h * 0.04f, xa + anA, corte + 1f * u)
        val abajo = Rect(0.6f * u, corte, w - 0.6f * u, h)
        caja(abajo, mezcla(pintura, P.TINTE_HONDO, 0f), a, k, luzDe, s, tapa = false)
        // La sombra que deja la de arriba en la cara de encima de la de abajo.
        drawRect(a.sombra(0.4f), Offset(arriba.left + (if (luzDe < 0) 2f * u else -2f * u), corte - 1f * u), Size(anA, 3f * u))
        caja(arriba, mezcla(pintura, P.TINTE_HONDO, 0.18f), a, k, luzDe, s + 9, tapa = true)
        // La cerradura de la de abajo y el asa de la de arriba.
        val cx = abajo.center.x
        val cerr = Rect(cx - 3.2f * u, abajo.top + 4.5f * u, cx + 3.2f * u, abajo.top + 11f * u)
        drawRect(a.sombra(0.4f), Offset(cerr.left + 0.7f * u, cerr.top + 0.9f * u), cerr.size)
        drawRect(a.luz(P.BRONCE, k), cerr.topLeft, cerr.size)
        drawRect(a.brillo(0.6f, k), cerr.topLeft, Size(cerr.width, 0.8f * u))
        drawLine(P.TINTA, Offset(cx, cerr.top + 1.8f * u), Offset(cx, cerr.bottom - 1.8f * u), 1.1f * u)
        drawLine(P.TINTA, Offset(cx, cerr.bottom - 1.8f * u), Offset(cx + 1.6f * u, cerr.bottom - 1.8f * u), 1.1f * u)
        val asa = Offset(arriba.center.x, arriba.top + arriba.height * 0.45f)
        val anAsa = minOf(9f * u, arriba.width * 0.4f)
        drawRect(a.sombra(0.35f), Offset(asa.x - anAsa / 2 + 0.8f * u, asa.y + 1f * u), Size(anAsa, 1.8f * u))
        drawRect(a.luz(P.BRONCE, k), Offset(asa.x - anAsa / 2, asa.y), Size(anAsa, 1.8f * u))
        drawRect(a.brillo(0.6f, k), Offset(asa.x - anAsa / 2, asa.y), Size(anAsa, 0.6f * u))
        for (sx in listOf(-1f, 1f)) {
            val pl = Rect(asa.x + sx * anAsa / 2 - 1.6f * u, asa.y - 1.8f * u, asa.x + sx * anAsa / 2 + 1.6f * u, asa.y + 3.4f * u)
            drawRect(a.luz(P.BRONCE_OSC, k), pl.topLeft, pl.size)
            remacheRomano(pl.center, 0.7f * u, a, k, P.BRONCE)
        }
        // La placa de hueso del día, chica frente a la caja.
        val an = minOf(ANCHO_ETIQUETA * u, abajo.width - 8f * u)
        val et = Rect(cx - an / 2, cerr.bottom + 4f * u, cx + an / 2, cerr.bottom + 4f * u + ALTO_ETIQUETA * u)
        if (et.bottom < abajo.bottom - 4f * u) placaHueso(et, v, medidor, a, k)
        if (v.noche) luna(Offset(arriba.center.x + arriba.width * 0.28f * -lado, arriba.top + arriba.height * 0.62f), minOf(arriba.height * 0.2f, w * 0.12f, 4.4f * u), mezcla(pintura, P.TINTE_HONDO, 0.18f), a, k)
        filo(abajo.left, abajo.right, abajo.top, abajo.bottom, a, k, luzDe)
        filo(arriba.left, arriba.right, arriba.top, arriba.bottom, a, k, luzDe)
    }

    /**
     * Una caja de tablas pintada en [r]: la cara de encima (o la tapa) a la luz, las tablas con sus juntas y su veta, la
     * pintura gastada en los cantos, la luz de un lado y la sombra del otro, las cantoneras y una faja de bronce con sus clavos.
     */
    private fun DrawScope.caja(r: Rect, pintura: Color, a: AireRomano, k: Float, luzDe: Float, s: Int, tapa: Boolean) {
        val u = density
        if (r.width <= 2f || r.height <= 2f) return
        val hondo = mezcla(pintura, a.hondo, 0.35f)
        val madera = a.luz(P.ROBLE_LUZ, k)
        val encima = minOf(3.4f * u, r.height * 0.12f)
        drawRect(pintura, r.topLeft, r.size)
        // La cara de encima: la tapa, que vuela un poco, o el canto de las tablas.
        drawRect(mezcla(pintura, P.HUESO, 0.22f), r.topLeft, Size(r.width, encima))
        if (tapa) {
            drawRect(hondo, Offset(r.left, r.top + encima + 2.6f * u), Size(r.width, 1f * u))
            drawRect(a.brillo(0.3f, k), Offset(r.left, r.top + encima), Size(r.width, 0.8f * u))
        }
        // Las tablas: juntas, veta y la pintura gastada que deja ver la madera.
        val tablas = 2 + (r.height / (22f * u)).toInt().coerceIn(0, 3)
        for (t in 1 until tablas) {
            val y = r.top + encima + (r.height - encima) * t / tablas
            drawLine(hondo, Offset(r.left, y), Offset(r.right, y), 0.8f * u)
            drawLine(a.brillo(0.2f, k), Offset(r.left, y + 0.9f * u), Offset(r.right, y + 0.9f * u), 0.5f * u)
        }
        val veta = Path()
        for (j in 0 until 4) {
            val y = r.top + encima + (r.height - encima) * (0.15f + 0.22f * j + 0.06f * azar(s, 20 + j))
            veta.moveTo(r.left + r.width * 0.1f * azar(s, 30 + j), y)
            veta.quadraticTo(r.center.x, y + 1.2f * u * (azar(s, 40 + j) - 0.5f), r.right - r.width * 0.15f * azar(s, 50 + j), y + 0.8f * u)
        }
        drawPath(veta, hondo.copy(alpha = 0.35f), style = Stroke(0.5f * u))
        drawPath(poligonoRomano(r.left, r.bottom - 7f * u, r.left + 3.5f * u, r.bottom - 2f * u, r.left + 2f * u, r.bottom, r.left, r.bottom), madera)
        drawPath(poligonoRomano(r.right, r.top + encima + 3f * u, r.right - 2.6f * u, r.top + encima + 7f * u, r.right, r.top + encima + 10f * u), madera)
        // La luz y la sombra.
        drawRect(a.brillo(0.22f, k), Offset(if (luzDe < 0) r.left else r.right - r.width * 0.2f, r.top + encima), Size(r.width * 0.2f, r.height - encima))
        drawRect(a.sombra(0.28f), Offset(if (luzDe < 0) r.right - r.width * 0.22f else r.left, r.top + encima), Size(r.width * 0.22f, r.height - encima))
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - 2.6f * u), Size(r.width, 2.6f * u))
        // Las cantoneras de bronce y la faja con sus clavos.
        val c = minOf(5.5f * u, r.width * 0.2f, r.height * 0.3f)
        val bronce = a.luz(P.BRONCE, k)
        val cantoneras = Path()
        for ((cx0, sx) in listOf(r.left to 1f, r.right to -1f)) for ((cy0, sy) in listOf(r.top to 1f, r.bottom to -1f))
            cantoneras.addPath(poligonoRomano(cx0, cy0, cx0 + sx * c, cy0, cx0 + sx * c, cy0 + sy * 1.6f * u, cx0 + sx * 1.6f * u, cy0 + sy * 1.6f * u, cx0 + sx * 1.6f * u, cy0 + sy * c, cx0, cy0 + sy * c))
        drawPath(corridoRomano(cantoneras, 0.6f * u, 0.8f * u), a.sombra(0.35f))
        drawPath(cantoneras, bronce)
        drawPath(cantoneras, a.brillo(0.45f, k), style = Stroke(0.5f * u))
        val fy = r.bottom - r.height * 0.22f
        drawRect(a.sombra(0.3f), Offset(r.left, fy + 2.2f * u), Size(r.width, 0.8f * u))
        drawRect(bronce, Offset(r.left, fy), Size(r.width, 2.2f * u))
        drawRect(a.brillo(0.55f, k), Offset(r.left, fy), Size(r.width, 0.6f * u))
        var x = r.left + 4f * u
        var i = 0
        while (x < r.right - 3f * u && i < 20) { drawCircle(a.brillo(0.7f, k), 0.55f * u, Offset(x, fy + 1.1f * u)); x += 5f * u; i++ }
    }

    // ---------------------------------------------------------------- la bolsa

    /**
     * La bolsa de cuero teñido del ánimo, blanda y asentada, algo ladeada: ancha abajo, con su panza, su brillo, su sombra y
     * sus pliegues (más cuanto más vacía); el cuello fruncido y ancho, con el volante que se abre en ondas; dos vueltas de
     * cordel con su nudo; el sello de plomo colgando (en los Sueños, la luna) y el marbete de plomo del día.
     */
    fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.ROBLE_LUZ, 0.3f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.38f)
        val lado = if (azar(s, 5) > 0.5f) 1f else -1f
        rotate(lado * 3.5f, Offset(w / 2, h)) {
            val ny = h * 0.2f
            val cu = w * 0.3f
            val nx = w * 0.5f
            val llena = 0.6f + 0.4f * v.lleno
            // La sombra donde se asienta.
            drawOval(a.sombra(0.4f), Offset(w * 0.02f, h - 3f * u), Size(w * 0.96f, 4.5f * u))
            // El volante del cuello, corto y abierto en ondas.
            val volante = Path().apply {
                moveTo(nx - cu, ny)
                quadraticTo(nx - cu * 1.45f, ny - h * 0.05f, nx - cu * 1.3f, ny - h * 0.11f)
                quadraticTo(nx - cu * 0.85f, ny - h * 0.08f, nx - cu * 0.45f, ny - h * 0.14f)
                quadraticTo(nx, ny - h * 0.09f, nx + cu * 0.4f, ny - h * 0.15f)
                quadraticTo(nx + cu * 0.85f, ny - h * 0.08f, nx + cu * 1.35f, ny - h * 0.1f)
                quadraticTo(nx + cu * 1.45f, ny - h * 0.04f, nx + cu, ny)
                close()
            }
            drawPath(volante, mezcla(cuero, hondo, 0.2f))
            clipPath(volante) {
                for (j in 0 until 4) {
                    val x = nx - cu + cu * 0.5f * j + cu * 0.2f
                    drawLine(hondo.copy(alpha = 0.6f), Offset(x, ny), Offset(x + (j - 1.5f) * 1.5f * u, ny - h * 0.2f), 1.2f * u)
                    drawLine(a.brillo(0.3f, k), Offset(x - 1.6f * u, ny), Offset(x - 1.6f * u + (j - 1.5f) * 1.5f * u, ny - h * 0.2f), 0.7f * u)
                }
                drawRect(a.sombra(0.3f), Offset(0f, ny - h * 0.05f), Size(w, h * 0.05f))
            }
            // La panza: un saco ancho de arriba abajo, con los hombros cortos, que se aplasta y se ensancha al asentarse.
            val hombro = ny + h * (0.12f - 0.04f * llena)
            val panza = Path().apply {
                moveTo(nx - cu, ny)
                cubicTo(nx - cu * 1.4f, ny + h * 0.03f, 1.6f * u, hombro - h * 0.04f, 1.2f * u, hombro + h * 0.06f)
                cubicTo(0.2f * u, h * 0.55f, -0.4f * u, h * 0.85f, 1.6f * u, h - 3f * u)
                quadraticTo(3f * u, h - 0.4f * u, w * 0.22f, h - 0.4f * u)
                lineTo(w * 0.78f, h - 0.4f * u)
                quadraticTo(w - 3f * u, h - 0.4f * u, w - 1.6f * u, h - 3f * u)
                cubicTo(w + 0.4f * u, h * 0.85f, w - 0.2f * u, h * 0.55f, w - 1.2f * u, hombro + h * 0.06f)
                cubicTo(w - 1.6f * u, hombro - h * 0.04f, nx + cu * 1.4f, ny + h * 0.03f, nx + cu, ny)
                close()
            }
            drawPath(panza, cuero)
            clipPath(panza) {
                drawRect(a.brillo(0.22f, k), Offset(if (luzDe < 0) 0f else w * 0.68f, ny), Size(w * 0.32f, h - ny))
                drawRect(a.sombra(0.3f), Offset(if (luzDe < 0) w * 0.7f else 0f, ny), Size(w * 0.3f, h - ny))
                drawOval(a.brillo(0.3f, k), Offset(w * (if (luzDe < 0) 0.14f else 0.5f), ny + (h - ny) * 0.3f), Size(w * 0.34f, (h - ny) * 0.36f))
                drawOval(a.brillo(0.5f, k), Offset(w * (if (luzDe < 0) 0.2f else 0.62f), ny + (h - ny) * 0.38f), Size(w * 0.08f, (h - ny) * 0.12f))
                drawRect(a.sombra(0.35f), Offset(0f, h - (h - ny) * 0.14f), Size(w, (h - ny) * 0.14f))
                for (j in 0 until 6) drawCircle(hondo.copy(alpha = 0.4f), 0.6f * u, Offset(w * azar(s, 210 + j), ny + (h - ny) * azar(s, 220 + j)))
                val pliegues = if (v.lleno > 0.5f) 3 else 5
                for (j in 0 until pliegues) {
                    val x0 = nx + (j - (pliegues - 1) / 2f) * cu * 0.45f
                    val x1 = w * (0.08f + 0.84f * (j + 0.5f) / pliegues) + (azar(s, 180 + j) - 0.5f) * 3f * u
                    val p = Path().apply {
                        moveTo(x0, ny + 1f * u)
                        quadraticTo((x0 + x1) / 2 + (azar(s, 190 + j) - 0.5f) * 5f * u, ny + (h - ny) * 0.3f, x1, ny + (h - ny) * (0.36f + 0.25f * azar(s, 200 + j)))
                    }
                    drawPath(p, hondo.copy(alpha = 0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
                    drawPath(corridoRomano(p, -1.3f * u, 0f), a.brillo(0.3f, k), style = Stroke(0.7f * u, cap = StrokeCap.Round))
                }
            }
            // Dos vueltas de cordel en el cuello y el nudo.
            val cordel = a.luz(P.CANAMO, k)
            for (d in listOf(-1.5f, 1.5f)) {
                drawLine(a.sombra(0.35f), Offset(nx - cu * 1.05f, ny + d * u + 1f * u), Offset(nx + cu * 1.05f, ny + d * u + 0.4f * u), 1.5f * u)
                drawLine(cordel, Offset(nx - cu * 1.05f, ny + d * u), Offset(nx + cu * 1.05f, ny + d * u - 0.6f * u), 1.5f * u, cap = StrokeCap.Round)
            }
            val nudo = Offset(nx + cu * 0.55f * lado, ny)
            drawCircle(cordel, 2.1f * u, nudo)
            drawCircle(a.brillo(0.45f, k), 0.7f * u, nudo - Offset(0.6f * u, 0.6f * u))
            // El sello de plomo que cuelga de un cabo, por el costado.
            val selloR = minOf(w * 0.11f, 3.8f * u)
            val sello = Offset((nudo.x + lado * cu * 0.5f).coerceIn(selloR + 1f * u, w - selloR - 1f * u), ny + 4f * u + selloR)
            drawLine(cordel, nudo, sello, 0.8f * u)
            val plomo = a.luz(mezcla(P.PLOMO, P.TINTA, 0.3f), k)
            if (v.noche) {
                drawCircle(plomo, selloR * 1.15f, sello)
                luna(sello, selloR * 0.85f, plomo, a, k)
            } else {
                drawCircle(a.sombra(0.4f), selloR, sello + Offset(0.6f * u, 0.8f * u))
                drawCircle(a.luz(P.PLOMO, k), selloR, sello)
                drawCircle(a.brillo(0.45f, k), selloR * 0.35f, sello - Offset(selloR * 0.3f, selloR * 0.3f))
                drawCircle(plomo, selloR * 0.55f, sello, style = Stroke(0.5f * u))
            }
            // El marbete de plomo del día, colgado del otro cabo sobre la panza.
            val an = minOf(ANCHO_ETIQUETA * u, w - 7f * u)
            val ty = ny + h * 0.2f
            val tx = w / 2 - lado * w * 0.08f
            val et = Rect(tx - an / 2, ty, tx + an / 2, ty + ALTO_ETIQUETA * u)
            drawLine(cordel, Offset(nx - lado * cu * 0.3f, ny + 1f * u), Offset(et.center.x, et.top + 1.6f * u), 0.8f * u)
            marbetePlomo(et, v, medidor, a, k, s, -lado * 4f)
            filo(0.8f * u, w - 0.8f * u, ny + 4f * u, h, a, k, luzDe)
        }
    }
}
