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

/** Las escalas del ánimo de Romano, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_ROMANO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ROMANO, false) to juegoTonos(TemaId.ROMANO, true) }

private fun cuadrado(x: Float) = x * x

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el ánimo neutro pasa del
 * travertino a un casi negro, pensado para la interfaz) y un volumen claro se volvía negro de noche. Aquí el volumen es un
 * objeto del cuarto: conserva su color de día y la linterna lo apaga con la distancia. Se ubica el color en la escala de noche
 * (el tramo y la fracción que más se le acercan) y se toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaRomano(t: Color, oscuro: Boolean): Color {
    if (!oscuro) return t
    val (dia, noche) = TONOS_ROMANO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadrado(t.red - a0.red - dr * f) + cuadrado(t.green - a0.green - dg * f) + cuadrado(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

/** El color con más cuerpo: se aleja del gris de su misma claridad. */
private fun saturado(c: Color, f: Float): Color {
    val g = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    return Color((g + (c.red - g) * f).coerceIn(0f, 1f), (g + (c.green - g) * f).coerceIn(0f, 1f), (g + (c.blue - g) * f).coerceIn(0f, 1f), c.alpha)
}

/**
 * Las tres clases de entrada del estante de Romano ([MuebleEstanteRomano]), elegidas con la semilla del día, todas de lo que
 * se guardaba en el cuarto de los oficiales y modeladas como los objetos del Ideario: el legajo de hojas de madera escritas
 * con tinta en su funda de cuero teñido, con la solapa cosida, la correa y la hebilla de bronce; la arqueta de tablas pintada
 * y herrada, con la tapa abierta y los legajos dentro; y la bolsa de cuero, blanda y llena, cerrada con cordel y sello de plomo.
 * En las tres, el ánimo es el color del cuerpo entero (el tinte del cuero, la pintura de la caja), con cuerpo; el día y el mes
 * van en una tablilla de aliso o una placa de hueso; en los Sueños, la luna de plata a la vista. [luzDe] dice de qué lado
 * llega la luz: -1, de la izquierda (la ventana de día); 1, de la derecha (la linterna de noche, para lo que queda a su izquierda).
 */
internal object VolumenEstanteRomano {
    private val P = PaletaRomano
    /** El color del cuero curtido y el del aparejo de la madera, sobre los que se tiñe el ánimo. */
    private val CURTIDO = Color(0xFFD8B98F)
    private val APAREJO = Color(0xFFE8D6B6)

    /** El día y el mes de la tablilla, en sp, y su alto, en dp. */
    private const val TAM_DIA = 12.5f
    private const val TAM_MES = 6.4f
    private const val ALTO_TABLILLA = 22f

    /**
     * El color del ánimo del cuerpo: el de día, teñido en la [materia] como se tiñe el cuero o se pinta la madera (el color se
     * multiplica por el de la materia, así lo claro no queda en pastel), con más cuerpo y apenas hundido; [crudo] si no se marcó.
     */
    private fun cuerpo(v: Volumen, a: AireRomano, k: Float, crudo: Color, materia: Color): Color = v.tono?.let {
        val t = tonoDeDiaRomano(it, a.oscuro)
        val tenido = mezcla(t, Color(t.red * materia.red, t.green * materia.green, t.blue * materia.blue, 1f), 0.55f)
        a.animo(mezcla(saturado(tenido, 1.4f), P.TINTE_HONDO, 0.08f), k)
    } ?: a.luz(crudo, k)

    /** El mes como en las fechas romanas: abreviado («NOV.»). */
    private fun mesCorto(mes: String): String = if (mes.length > 5) mes.take(3) + "." else mes

    /** Lo redondo de un cuerpo dentro de [r] (ir dentro de su recorte): el claro, el brillo, la sombra y el canto hondo. */
    private fun DrawScope.redondo(r: Rect, a: AireRomano, k: Float, luzDe: Float, fuerza: Float = 1f) {
        val der = luzDe > 0f
        fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
        drawRect(a.brillo(0.2f * fuerza, k), Offset(x(0.04f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(a.brillo(0.38f * fuerza, k), Offset(x(0.16f, 0.07f), r.top), Size(r.width * 0.07f, r.height))
        drawRect(a.sombra(0.18f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
        drawRect(a.sombra(0.26f * fuerza), Offset(x(0.84f, 0.16f), r.top), Size(r.width * 0.16f, r.height))
    }

    /** El filo de la linterna en el canto que la mira y, de noche, el roce frío de la luna en el canto izquierdo. */
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

    /** Una costura de punto de [p0] a [p1], en un solo camino. */
    private fun costura(camino: Path, p0: Offset, p1: Offset, paso: Float) {
        val d = (p1 - p0).getDistance()
        if (d < 1f || paso < 0.5f) return
        val n = (d / paso).toInt().coerceAtMost(60)
        for (i in 0 until n) {
            val f0 = i / d * paso; val f1 = (i * paso + paso * 0.55f) / d
            camino.moveTo(p0.x + (p1.x - p0.x) * f0, p0.y + (p1.y - p0.y) * f0)
            camino.lineTo(p0.x + (p1.x - p0.x) * f1, p0.y + (p1.y - p0.y) * f1)
        }
    }

    /**
     * La tablilla del día: aliso o hueso, con su sombra, el canto algo desigual, la veta, el número grande y, debajo, el mes
     * abreviado.
     */
    private fun DrawScope.tablilla(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, semilla: Int, cara: Color) {
        val u = density
        val p = poligonoRomano(r.left, r.top + 0.5f * u * azar(semilla, 61), r.right, r.top, r.right - 0.4f * u * azar(semilla, 62), r.bottom,
            r.left + 0.3f * u, r.bottom - 0.4f * u * azar(semilla, 63))
        drawPath(corridoRomano(p, 1.1f * u, 1.4f * u), a.sombra(0.5f))
        drawPath(p, a.etiqueta(cara, k))
        // La veta, fina y arriba, para que no cruce el número.
        drawLine(mezcla(a.etiqueta(cara, k), P.TINTA, 0.18f).copy(alpha = 0.4f), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.09f), Offset(r.right - r.width * 0.15f, r.top + r.height * 0.11f), 0.5f * u)
        drawRect(a.sombra(0.12f), Offset(r.right - r.width * 0.14f, r.top), Size(r.width * 0.14f, r.height))
        drawRect(a.sombra(0.12f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawRect(a.claro(0.5f), r.topLeft, Size(r.width, 0.8f * u))
        val corte = r.top + r.height * 0.66f
        textoEn(medidor, v.dia, Rect(r.left + 1f * u, r.top + 0.4f * u, r.right - 1f * u, corte), P.LETRA, P.TINTA, TAM_DIA, peso = FontWeight.Bold)
        drawLine(P.TINTA.copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, corte), Offset(r.right - r.width * 0.2f, corte), 0.5f * u)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left + 0.5f * u, corte, r.right - 0.5f * u, r.bottom - 0.3f * u), P.LETRA, P.TINTA.copy(alpha = 0.85f), TAM_MES,
            peso = FontWeight.SemiBold, minimo = 4f)
    }

    // ---------------------------------------------------------------- el legajo

    /**
     * El legajo, el más alto: el mazo de hojas finas de aliso que asoma arriba, desparejo (más cuanto más escribiste), con una
     * hoja suelta escrita; la funda de cuero teñido del ánimo, de cantos rectos y esquinas gastadas, con su luz, su brillo y su
     * sombra, cosida a los lados; la solapa redonda que cae por delante con su costura; la correa con su hebilla de bronce, y la
     * tablilla del día colgada de una anilla. En los Sueños, la luna repujada en la solapa.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val lp = (7f + 5f * v.lleno) * u
        // El mazo de hojas que asoma: los cantos finos y apretados, de dos tonos parecidos, con la boca desigual; luz arriba
        // y sombra al lado contrario de la luz.
        val hojas = maxOf(8, ((w - 5f * u) / (1.3f * u)).toInt()).coerceAtMost(36)
        val paso = (w - 5f * u) / hojas
        val clara = a.etiqueta(P.ALISO, k)
        val media = a.etiqueta(mezcla(P.ALISO, P.ALISO_OSC, 0.35f), k)
        val boca = Path()
        boca.moveTo(2.5f * u, lp + 1f * u)
        for (j in 0..hojas) {
            val x = 2.5f * u + paso * j
            val y = lp * (0.12f + 0.55f * azar(s, 100 + j) * (0.5f + 0.5f * v.lleno) + 0.2f * (if (j % 5 == 0) 1f else 0f))
            boca.lineTo(x, y)
        }
        boca.lineTo(w - 2.5f * u, lp + 1f * u)
        boca.close()
        drawPath(boca, clara)
        clipPath(boca) {
            for (j in 0 until hojas step 2) drawRect(media, Offset(2.5f * u + paso * j, 0f), Size(paso, lp + 1f * u))
            drawRect(a.sombra(0.22f), Offset(if (luzDe > 0) 2.5f * u else w * 0.6f, 0f), Size(w * 0.38f, lp + 1f * u))
            drawRect(a.sombra(0.3f), Offset(0f, lp - 1.5f * u), Size(w, 2.5f * u))
        }
        // Una hoja suelta, más alta y ladeada, con renglones de tinta.
        val hx = w * (0.32f + 0.3f * azar(s, 120))
        rotate((azar(s, 121) - 0.5f) * 12f, Offset(hx, lp)) {
            drawRect(a.sombra(0.25f), Offset(hx - 2.2f * u, 0.8f * u), Size(5.4f * u, lp))
            drawRect(clara, Offset(hx - 3f * u, 0f), Size(5.4f * u, lp))
            for (j in 0 until 3) drawLine(P.TINTA.copy(alpha = 0.4f), Offset(hx - 2.2f * u, 2f * u + j * 2.2f * u), Offset(hx + 1.6f * u, 2f * u + j * 2.2f * u), 0.5f * u)
        }
        // La funda.
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.CANAMO, 0.2f * azar(s, 3)), CURTIDO)
        val hondo = mezcla(cuero, a.hondo, 0.35f)
        val funda = RoundRect(Rect(0.8f * u, lp, w - 0.8f * u, h), CornerRadius(3.5f * u))
        val forma = Path().apply { addRoundRect(funda) }
        drawPath(forma, cuero)
        clipPath(forma) {
            // El cuero de una funda plana: la cara casi lisa, el canto de la luz claro y el otro hondo, sin cilindro.
            val claroX = if (luzDe < 0) 0.8f * u else w - 3.6f * u
            val hondoX = if (luzDe < 0) w - 4.4f * u else 0.8f * u
            drawRect(a.brillo(0.3f, k), Offset(claroX, lp), Size(2.8f * u, h - lp))
            drawRect(a.sombra(0.3f), Offset(hondoX, lp), Size(3.6f * u, h - lp))
            drawRect(a.sombra(0.1f), Offset(if (luzDe < 0) w * 0.55f else 0.8f * u, lp), Size(w * 0.45f, h - lp))
            // El roce del cuero: motas y el brillo de la panza.
            for (j in 0 until 6) drawCircle(hondo.copy(alpha = 0.35f), 0.6f * u, Offset(w * azar(s, 130 + j), lp + (h - lp) * azar(s, 140 + j)))
            drawOval(a.brillo(0.16f, k), Offset(w * 0.12f, h * (0.6f + 0.1f * azar(s, 5))), Size(w * 0.5f, h * 0.12f))
            drawRect(a.sombra(0.3f), Offset(0f, h - 4f * u), Size(w, 4f * u))
        }
        // La costura de los costados.
        val hilo = a.luz(mezcla(P.CANAMO, P.HUESO, 0.3f), k).copy(alpha = 0.85f)
        val puntos = Path()
        costura(puntos, Offset(2.6f * u, lp + 6f * u), Offset(2.6f * u, h - 3f * u), 2.6f * u)
        costura(puntos, Offset(w - 2.6f * u, lp + 6f * u), Offset(w - 2.6f * u, h - 3f * u), 2.6f * u)
        // La solapa, que cae por delante en lengua redonda.
        val fh = (h - lp) * 0.27f
        val solapa = Path().apply {
            moveTo(0.8f * u, lp)
            lineTo(w - 0.8f * u, lp)
            lineTo(w - 0.8f * u, lp + fh * 0.5f)
            cubicTo(w - 0.8f * u, lp + fh * 0.95f, w * 0.72f, lp + fh, w * 0.5f, lp + fh)
            cubicTo(w * 0.28f, lp + fh, 0.8f * u, lp + fh * 0.95f, 0.8f * u, lp + fh * 0.5f)
            close()
        }
        drawPath(corridoRomano(solapa, 0.8f * u, 1.8f * u), a.sombra(0.4f))
        drawPath(solapa, mezcla(cuero, hondo, 0.25f))
        clipPath(solapa) {
            redondo(Rect(0.8f * u, lp, w - 0.8f * u, lp + fh), a, k, luzDe, 0.9f)
            drawRect(a.brillo(0.4f, k), Offset(0f, lp), Size(w, 1f * u))
        }
        val borde = Path().apply {
            moveTo(2.4f * u, lp + fh * 0.5f)
            cubicTo(2.4f * u, lp + fh * 0.88f, w * 0.3f, lp + fh - 2f * u, w * 0.5f, lp + fh - 2f * u)
            cubicTo(w * 0.7f, lp + fh - 2f * u, w - 2.4f * u, lp + fh * 0.88f, w - 2.4f * u, lp + fh * 0.5f)
        }
        drawPath(borde, hilo, style = Stroke(0.7f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))))
        drawPath(puntos, hilo, style = Stroke(0.7f * u))
        // La correa y la hebilla de bronce.
        val cx = w / 2
        val ca = w * 0.12f
        val yb = lp + fh + 6f * u
        drawRect(a.sombra(0.3f), Offset(cx - ca + 1f * u, lp + fh - 2f * u), Size(ca * 2, yb - lp - fh + 12f * u))
        drawRect(mezcla(cuero, hondo, 0.5f), Offset(cx - ca, lp + fh - 3f * u), Size(ca * 2, yb - lp - fh + 12f * u))
        drawRect(a.brillo(0.25f, k), Offset(cx - ca, lp + fh - 3f * u), Size(0.8f * u, yb - lp - fh + 12f * u))
        val hb = Rect(cx - ca * 1.9f, yb - 3f * u, cx + ca * 1.9f, yb + 3f * u)
        drawRect(a.sombra(0.35f), Offset(hb.left + 1f * u, hb.top + 1.2f * u), hb.size, style = Stroke(1.6f * u))
        drawRect(a.luz(P.BRONCE, k), hb.topLeft, hb.size, style = Stroke(1.6f * u))
        drawLine(a.brillo(0.6f, k), Offset(hb.left, hb.top), Offset(hb.right, hb.top), 0.7f * u)
        drawLine(a.luz(P.BRONCE_LUZ, k), Offset(hb.left + ca * 0.4f, yb), Offset(cx + ca * 0.4f, yb), 1f * u)
        // La tablilla, colgada de una anilla bajo la hebilla.
        val an = minOf(w - 5f * u, 26f * u)
        val ty = yb + 7f * u
        val et = Rect(cx - an / 2, ty, cx + an / 2, ty + ALTO_TABLILLA * u)
        drawCircle(a.luz(P.BRONCE, k), 1.8f * u, Offset(cx, yb + 4.4f * u), style = Stroke(0.9f * u))
        tablilla(et, v, medidor, a, k, s, P.ALISO)
        if (v.noche) luna(Offset(cx, lp + fh * 0.48f), minOf(w * 0.15f, fh * 0.3f, 4.8f * u), mezcla(cuero, hondo, 0.25f), a, k)
        filo(0.8f * u, w - 0.8f * u, lp, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la arqueta

    /**
     * La arqueta: la caja de tablas pintada del ánimo (cruda, si no se marcó), con la pintura gastada en los cantos, las
     * cantoneras de hierro claveteadas, la cerradura de bronce y la placa de hueso del día; la tapa abierta de pie detrás, con su
     * cara de dentro de madera, el canto pintado y sus bisagras; dentro, los legajos atados de pie (más cuanto más escribiste).
     * En los Sueños, la luna de plata clavada en la tapa.
     */
    fun DrawScope.arqueta(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val pintura = cuerpo(v, a, k, mezcla(P.ROBLE_LUZ, P.CANAMO, 0.3f), APAREJO)
        val hondo = mezcla(pintura, a.hondo, 0.32f)
        val madera = a.luz(P.ROBLE, k)
        val boca = h * 0.47f
        // La tapa abierta y echada atrás, detrás de la caja: en escorzo (más angosta arriba), su cara de dentro de madera con
        // su veta y su sombra, y el canto pintado alrededor.
        val arriba = boca * 0.3f
        val tapa = Rect(2f * u, arriba, w - 2f * u, boca + 2f * u)
        val mete = 3f * u
        val tapaP = poligonoRomano(tapa.left + mete, tapa.top, tapa.right - mete, tapa.top, tapa.right, tapa.bottom, tapa.left, tapa.bottom)
        drawPath(tapaP, pintura)
        val dentro = Rect(tapa.left + 2.4f * u, tapa.top + 2.4f * u, tapa.right - 2.4f * u, tapa.bottom)
        val dentroP = poligonoRomano(dentro.left + mete, dentro.top, dentro.right - mete, dentro.top, dentro.right, dentro.bottom, dentro.left, dentro.bottom)
        drawPath(dentroP, a.luz(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.35f), k))
        clipPath(dentroP) {
            for (j in 1 until 5) drawLine(a.luz(P.ROBLE_OSC, k).copy(alpha = 0.35f), Offset(dentro.left, dentro.top + dentro.height * j / 5f), Offset(dentro.right, dentro.top + dentro.height * j / 5f + 0.6f * u), 0.5f * u)
            drawRect(a.sombra(0.3f), Offset(dentro.left, dentro.top), Size(dentro.width, 2.4f * u))
            drawRect(a.sombra(0.2f), Offset(if (luzDe < 0) dentro.right - dentro.width * 0.3f else dentro.left, dentro.top), Size(dentro.width * 0.3f, dentro.height))
        }
        drawLine(a.brillo(0.45f, k), Offset(tapa.left + mete, tapa.top), Offset(tapa.right - mete, tapa.top), 0.9f * u)
        if (v.noche) luna(Offset(w / 2, dentro.top + dentro.height * 0.38f), minOf(dentro.height * 0.3f, w * 0.16f, 5f * u), a.luz(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.35f), k), a, k)
        // Los legajos de pie dentro de la caja.
        val n = 2 + (v.lleno * 2.5f).toInt()
        val anL = (w - 6f * u) / n
        for (j in 0 until n) {
            val x = 3f * u + anL * j
            val alto = (5f + 9f * v.lleno + 4f * azar(s, 160 + j)) * u
            val r = Rect(x + 0.4f * u, boca - alto, x + anL - 0.4f * u, boca + 1f * u)
            drawRect(a.etiqueta(P.ALISO, k * 0.9f), r.topLeft, r.size)
            for (q in 1 until 4) drawLine(a.luz(P.ALISO_OSC, k), Offset(r.left + r.width * q / 4f, r.top), Offset(r.left + r.width * q / 4f, r.bottom), 0.5f * u)
            drawRect(a.sombra(0.25f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
            drawLine(a.luz(P.CANAMO, k), Offset(r.left, r.top + r.height * 0.45f), Offset(r.right, r.top + r.height * 0.45f), 1.1f * u)
        }
        // El canto de arriba de la caja (el grueso de las tablas).
        drawRect(madera, Offset(0.6f * u, boca - 1f * u), Size(w - 1.2f * u, 3f * u))
        drawRect(a.brillo(0.4f, k), Offset(0.6f * u, boca - 1f * u), Size(w - 1.2f * u, 0.8f * u))
        // La caja.
        val caja = Rect(0.6f * u, boca + 2f * u, w - 0.6f * u, h)
        drawRect(pintura, caja.topLeft, caja.size)
        redondo(caja, a, k, luzDe, 0.9f)
        for (t in 1 until 3) {
            val y = caja.top + caja.height * (t / 3f) + (azar(s, 170 + t) - 0.5f) * 1.5f * u
            drawLine(hondo.copy(alpha = 0.6f), Offset(caja.left, y), Offset(caja.right, y), 0.8f * u)
            drawLine(a.brillo(0.2f, k), Offset(caja.left, y + 0.9f * u), Offset(caja.right, y + 0.9f * u), 0.5f * u)
        }
        // La pintura gastada: asoma la madera en los cantos.
        drawPath(poligonoRomano(caja.left, caja.bottom - 7f * u, caja.left + 4f * u, caja.bottom - 2f * u, caja.left + 2f * u, caja.bottom, caja.left, caja.bottom), madera)
        drawPath(poligonoRomano(caja.right, caja.top + 4f * u, caja.right - 3f * u, caja.top + 8f * u, caja.right, caja.top + 11f * u), madera)
        drawRect(hondo, Offset(caja.left, caja.bottom - 2.6f * u), Size(caja.width, 2.6f * u))
        // Las cantoneras de hierro, con sus clavos.
        val hierro = a.luz(P.HIERRO, k)
        val c = minOf(6.5f * u, caja.width * 0.22f)
        val cantoneras = Path()
        for ((cx0, sx) in listOf(caja.left to 1f, caja.right to -1f)) for ((cy0, sy) in listOf(caja.top to 1f, caja.bottom to -1f))
            cantoneras.addPath(poligonoRomano(cx0, cy0, cx0 + sx * c, cy0, cx0 + sx * c, cy0 + sy * 1.8f * u, cx0 + sx * 1.8f * u, cy0 + sy * 1.8f * u, cx0 + sx * 1.8f * u, cy0 + sy * c, cx0, cy0 + sy * c))
        drawPath(corridoRomano(cantoneras, 0.6f * u, 0.8f * u), a.sombra(0.35f))
        drawPath(cantoneras, hierro)
        for ((cx0, sx) in listOf(caja.left to 1f, caja.right to -1f)) for ((cy0, sy) in listOf(caja.top to 1f, caja.bottom to -1f))
            drawCircle(a.brillo(0.6f, k), 0.7f * u, Offset(cx0 + sx * c * 0.6f, cy0 + sy * 0.9f * u))
        // Las dos fajas de hierro que bajan por el frente, con sus clavos.
        for (fx in listOf(0.13f, 0.87f)) {
            val x = caja.left + caja.width * fx
            drawRect(a.sombra(0.3f), Offset(x - 0.4f * u, caja.top), Size(2.4f * u, caja.height))
            drawRect(hierro, Offset(x - 1f * u, caja.top), Size(2f * u, caja.height))
            drawRect(a.brillo(0.35f, k), Offset(x - 1f * u, caja.top), Size(0.6f * u, caja.height))
            for (q in 1 until 4) drawCircle(a.brillo(0.5f, k), 0.55f * u, Offset(x, caja.top + caja.height * q / 4f))
        }
        // Las bisagras de la tapa.
        for (fx in listOf(0.25f, 0.75f)) {
            drawRect(hierro, Offset(w * fx - 2f * u, boca - 4f * u), Size(4f * u, 7f * u))
            drawRect(a.brillo(0.4f, k), Offset(w * fx - 2f * u, boca - 4f * u), Size(1f * u, 7f * u))
        }
        // La cerradura de bronce y la placa de hueso del día.
        val cx = w / 2
        val cerr = Rect(cx - minOf(4.5f * u, w * 0.15f), caja.top + 3f * u, cx + minOf(4.5f * u, w * 0.15f), caja.top + 11f * u)
        drawRect(a.sombra(0.4f), Offset(cerr.left + 0.8f * u, cerr.top + 1f * u), cerr.size)
        drawRect(a.luz(P.BRONCE, k), cerr.topLeft, cerr.size)
        drawRect(a.brillo(0.55f, k), cerr.topLeft, Size(cerr.width, 0.8f * u))
        drawLine(P.TINTA, Offset(cx, cerr.top + 2.2f * u), Offset(cx, cerr.bottom - 2.2f * u), 1.2f * u)
        drawLine(P.TINTA, Offset(cx, cerr.bottom - 2.2f * u), Offset(cx + 1.8f * u, cerr.bottom - 2.2f * u), 1.2f * u)
        val alt = minOf(ALTO_TABLILLA * u, caja.bottom - cerr.bottom - 6f * u)
        val an = minOf(w - 7f * u, 26f * u)
        if (alt > 10f * u) {
            val et = Rect(cx - an / 2, cerr.bottom + 2.5f * u, cx + an / 2, cerr.bottom + 2.5f * u + alt)
            tablilla(et, v, medidor, a, k, s, P.HUESO)
            remacheRomano(Offset(et.left + 1.7f * u, et.top + 1.7f * u), 0.7f * u, a, k)
            remacheRomano(Offset(et.right - 1.9f * u, et.top + 1.7f * u), 0.7f * u, a, k)
        }
        filo(caja.left, caja.right, caja.top, caja.bottom, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la bolsa

    /**
     * La bolsa de cuero teñido del ánimo (cruda, si no se marcó), blanda y llena: el cuello fruncido de lado con su volante en
     * facetas, atado con dos vueltas de cordel; la panza redonda que se aplasta abajo, con su brillo, su sombra y sus pliegues
     * (más cuanto más vacía); el sello de plomo (en los Sueños, la luna) y la tablilla del día colgados del cordel.
     */
    fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.ROBLE_LUZ, 0.3f * azar(s, 3)), CURTIDO)
        val hondo = mezcla(cuero, a.hondo, 0.36f)
        val lado = if (azar(s, 4) > 0.5f) 1f else -1f
        val nx = w * (0.5f - 0.08f * lado)
        val ny = h * 0.26f
        val cu = w * 0.15f
        // El volante, en facetas de luz y de sombra, que cae hacia un lado.
        val pts = listOf(Offset(nx - cu, ny), Offset(nx - cu * 1.7f, ny - h * 0.1f), Offset(nx - cu * 0.7f, ny - h * 0.15f), Offset(nx - cu * 0.1f, ny - h * 0.25f),
            Offset(nx + cu * 0.6f, ny - h * 0.17f), Offset(nx + lado * cu * 2.2f, ny - h * 0.08f), Offset(nx + cu, ny))
        for (i in 0 until pts.size - 1) {
            val f = poligonoRomano(nx, ny + 1f * u, pts[i].x, pts[i].y, pts[i + 1].x, pts[i + 1].y)
            drawPath(f, if (i % 2 == 0) mezcla(cuero, hondo, 0.12f) else mezcla(cuero, hondo, 0.4f))
        }
        drawLine(a.brillo(0.35f, k), pts[1], pts[2], 0.8f * u)
        drawLine(a.brillo(0.35f, k), pts[3], pts[4], 0.8f * u)
        // La panza.
        val llena = 0.5f + 0.5f * v.lleno
        val panza = Path().apply {
            moveTo(nx - cu, ny)
            cubicTo(nx - cu * 2.6f, ny + h * 0.08f * llena, 0f, h * (0.6f - 0.12f * v.lleno), 0.8f * u, h - 3f * u)
            quadraticTo(w * 0.5f, h + 1f * u, w - 0.8f * u, h - 3f * u)
            cubicTo(w, h * (0.6f - 0.12f * v.lleno), nx + cu * 2.6f, ny + h * 0.08f * llena, nx + cu, ny)
            close()
        }
        drawPath(panza, cuero)
        clipPath(panza) {
            redondo(Rect(0f, ny, w, h), a, k, luzDe, 1.1f)
            drawOval(a.brillo(0.22f, k), Offset(w * (if (luzDe < 0) 0.08f else 0.5f), ny + (h - ny) * 0.22f), Size(w * 0.38f, (h - ny) * 0.4f))
            drawRect(a.sombra(0.32f), Offset(0f, h - (h - ny) * 0.16f), Size(w, (h - ny) * 0.16f))
            for (j in 0 until 6) drawCircle(hondo.copy(alpha = 0.35f), 0.6f * u, Offset(w * azar(s, 210 + j), ny + (h - ny) * azar(s, 220 + j)))
            // Los pliegues: más cuanto más vacía.
            val pliegues = if (v.lleno > 0.5f) 2 else 4
            for (j in 0 until pliegues) {
                val x0 = nx + (j - (pliegues - 1) / 2f) * cu * 0.7f
                val x1 = w * (0.12f + 0.76f * (j + 0.5f) / pliegues) + (azar(s, 180 + j) - 0.5f) * 3f * u
                val p = Path().apply {
                    moveTo(x0, ny + 1f * u)
                    quadraticTo((x0 + x1) / 2 + (azar(s, 190 + j) - 0.5f) * 5f * u, ny + (h - ny) * 0.35f, x1, ny + (h - ny) * (0.45f + 0.3f * azar(s, 200 + j)))
                }
                drawPath(p, hondo.copy(alpha = 0.6f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
                drawPath(corridoRomano(p, -1.2f * u, 0f), a.brillo(0.28f, k), style = Stroke(0.7f * u, cap = StrokeCap.Round))
            }
        }
        // El cordel del cuello, con su nudo, y los cabos.
        val cordel = a.luz(P.CANAMO, k)
        for (d in listOf(-1.4f, 1.4f)) {
            drawLine(a.sombra(0.3f), Offset(nx - cu * 1.15f, ny + d * u + 1f * u), Offset(nx + cu * 1.15f, ny + d * u + 0.4f * u), 1.4f * u)
            drawLine(cordel, Offset(nx - cu * 1.15f, ny + d * u), Offset(nx + cu * 1.15f, ny + d * u - 0.6f * u), 1.4f * u, cap = StrokeCap.Round)
        }
        val nudo = Offset(nx + lado * cu * 0.8f, ny)
        drawCircle(cordel, 2f * u, nudo)
        drawCircle(a.brillo(0.4f, k), 0.7f * u, nudo - Offset(0.6f * u, 0.6f * u))
        // El sello de plomo (o la luna) colgado de un cabo.
        val selloR = minOf(w * 0.13f, 4.4f * u)
        val sello = Offset((nudo.x + lado * 3f * u).coerceIn(selloR + 1f * u, w - selloR - 1f * u), ny + 5f * u + selloR)
        drawLine(cordel, nudo, sello, 0.8f * u)
        val plomo = a.luz(mezcla(P.PLOMO, P.TINTA, 0.3f), k)
        if (v.noche) {
            drawCircle(plomo, selloR * 1.15f, sello)
            luna(sello, selloR * 0.85f, plomo, a, k)
        } else {
            drawCircle(a.sombra(0.4f), selloR, sello + Offset(0.6f * u, 0.8f * u))
            drawCircle(a.luz(P.PLOMO, k), selloR, sello)
            drawCircle(a.brillo(0.4f, k), selloR * 0.35f, sello - Offset(selloR * 0.3f, selloR * 0.3f))
            drawCircle(plomo, selloR * 0.55f, sello, style = Stroke(0.5f * u))
        }
        // La tablilla del día, colgada del otro cabo, sobre la panza.
        val an = minOf(w - 5f * u, 26f * u)
        val ty = minOf(sello.y + selloR + 2.5f * u, h - ALTO_TABLILLA * u - 5f * u)
        val tx = w / 2 - lado * 1.5f * u
        val et = Rect(tx - an / 2, ty, tx + an / 2, ty + ALTO_TABLILLA * u)
        drawLine(cordel, Offset(nx - lado * cu * 0.6f, ny + 1f * u), Offset(et.center.x, et.top), 0.8f * u)
        tablilla(et, v, medidor, a, k, s, P.ALISO)
        filo(0.8f * u, w - 0.8f * u, ny + 4f * u, h, a, k, luzDe)
    }
}
