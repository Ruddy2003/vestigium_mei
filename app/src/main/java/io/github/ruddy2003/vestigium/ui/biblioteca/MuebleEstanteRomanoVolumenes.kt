package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Las tres clases de entrada del estante de Romano ([MuebleEstanteRomano]), elegidas con la semilla del día, todas de lo que
 * se guardaba en el cuarto del escribiente, con tres siluetas: el legajo de hojas de madera escritas con tinta, alto y
 * panzudo en su funda de cuero con solapa cosida, con las hojas que asoman desparejas; la arqueta de tablas herrada, baja y
 * ancha, con la tapa abierta y las hojas dentro; y la bolsa de cuero, blanda y vencida, atada con cordel y sello de plomo.
 * En las tres, el color del ánimo es el cuerpo entero; el día y el mes van en una tablilla de aliso o en una placa de hueso,
 * nunca a lo largo (no son lomos de libro); en los Sueños, la luna de plata a la vista. [luzDe] dice de qué lado llega la luz:
 * -1, de la izquierda (la ventana de día); 1, de la derecha (la linterna de noche, para lo que queda a su izquierda).
 */
internal object VolumenEstanteRomano {
    private val P = PaletaRomano

    /** El día y el mes de la tablilla, en sp, y su alto, en dp. */
    private const val TAM_DIA = 12f
    private const val TAM_MES = 6.2f
    private const val ALTO_TABLILLA = 21f

    /** El color del ánimo del cuerpo, el de día, apenas hundido para que sea materia; [crudo] si no se marcó. */
    private fun cuerpo(v: Volumen, a: AireRomano, k: Float, crudo: Color): Color =
        v.tono?.let { a.animo(mezcla(tonoDeDiaRomano(it, a.oscuro), P.TINTE_HONDO, 0.1f), k) } ?: a.luz(crudo, k)

    /** El mes como en las fechas romanas: abreviado («NOV.»). */
    private fun mesCorto(mes: String): String = if (mes.length > 5) mes.take(3) + "." else mes

    /** La luz de un lado y la sombra fría del otro sobre un cuerpo de [x0] a [x1]. */
    private fun DrawScope.bulto(x0: Float, x1: Float, y0: Float, y1: Float, a: AireRomano, luzDe: Float, fuerza: Float = 1f) {
        val an = x1 - x0
        if (an <= 0f || y1 <= y0) return
        val (claroX, sombraX) = if (luzDe < 0) x0 + an * 0.1f to x1 - an * 0.3f else x1 - an * 0.22f to x0
        drawRect(a.claro(0.22f * fuerza), Offset(claroX, y0), Size(an * 0.14f, y1 - y0))
        drawRect(a.frio(0.16f * fuerza), Offset(sombraX, y0), Size(an * 0.3f, y1 - y0))
    }

    /** El filo de la linterna en el canto que la mira y, de noche, el roce frío de la luna en el canto izquierdo. */
    private fun DrawScope.filo(x0: Float, x1: Float, y0: Float, y1: Float, a: AireRomano, k: Float, luzDe: Float) {
        if (!a.oscuro) return
        val u = density
        if (k > 0.06f) {
            drawRect(a.filo(0.6f * k), Offset(x0 + 1.5f * u, y0), Size(maxOf(0f, x1 - x0 - 3f * u), 1f * u))
            drawRect(a.filo(0.45f * k), Offset(if (luzDe < 0) x0 else x1 - 1f * u, y0 + 1.5f * u), Size(1f * u, maxOf(0f, y1 - y0 - 3f * u)))
        }
        drawRect(a.lunar(0.4f * (1f - k)), Offset(x0 + 0.4f * u, y0 + 2f * u), Size(1f * u, maxOf(0f, y1 - y0 - 4f * u)))
    }

    /** La luna de plata: un creciente con su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, fondo: Color, a: AireRomano, k: Float) {
        val plata = if (a.oscuro) mezcla(P.LUNA, a.hondo, 0.2f - 0.2f * k) else P.LUNA
        drawCircle(a.frio(0.35f), r, c + Offset(r * 0.18f, r * 0.22f))
        drawCircle(plata, r, c)
        drawCircle(fondo, r * 0.8f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
    }

    /**
     * La tablilla del día: aliso o hueso, con su sombra, el canto algo desigual, el número grande y, debajo, el mes abreviado.
     */
    private fun DrawScope.tablilla(r: Rect, v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, semilla: Int, cara: Color) {
        val u = density
        val p = poligonoRomano(r.left, r.top + 0.5f * u * azar(semilla, 61), r.right, r.top, r.right - 0.4f * u * azar(semilla, 62), r.bottom,
            r.left + 0.3f * u, r.bottom - 0.4f * u * azar(semilla, 63))
        drawPath(corridoRomano(p, 0.9f * u, 1.1f * u), a.frio(0.45f))
        drawPath(p, a.etiqueta(cara, k))
        drawRect(a.sombra(0.1f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
        drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
        val corte = r.top + r.height * 0.66f
        textoEn(medidor, v.dia, Rect(r.left + 1f * u, r.top + 0.4f * u, r.right - 1f * u, corte), P.LETRA, P.TINTA, TAM_DIA, peso = FontWeight.Bold)
        drawLine(P.TINTA.copy(alpha = 0.3f), Offset(r.left + r.width * 0.2f, corte), Offset(r.right - r.width * 0.2f, corte), 0.5f * u)
        textoEn(medidor, mesCorto(v.mes), Rect(r.left + 0.5f * u, corte, r.right - 0.5f * u, r.bottom - 0.3f * u), P.LETRA, P.TINTA.copy(alpha = 0.85f), TAM_MES,
            peso = FontWeight.SemiBold, minimo = 4f)
    }

    // ---------------------------------------------------------------- el legajo de hojas

    /**
     * El legajo, el más alto: un mazo de hojas finas de aliso escritas con tinta, de pie, metido en una funda de cuero teñida
     * del ánimo (cruda si no se marcó), panzuda y con las esquinas gastadas; la solapa doblada por delante con su costura y la
     * correa con su botón de hueso; las hojas asoman desparejas (más cuanto más escribiste) y un cordel ciñe la panza, del que
     * cuelga la tablilla. En los Sueños, la luna bajo la tablilla.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val asoma = (9f + 6f * v.lleno) * u
        // Las hojas que asoman, cada una a su altura y algo ladeadas.
        val hojas = maxOf(4, ((w - 7f * u) / (2.6f * u)).toInt()).coerceAtMost(20)
        val paso = (w - 7f * u) / hojas
        val clara = a.etiqueta(P.ALISO, k * 0.9f)
        val oscura = a.luz(P.ALISO_OSC, k)
        drawRect(oscura, Offset(3.5f * u, asoma * 0.5f), Size(w - 7f * u, asoma))
        for (j in 0 until hojas) {
            val x = 3.5f * u + paso * j
            val y = asoma * (0.05f + 0.75f * azar(s, 100 + j)) * (0.4f + 0.6f * v.lleno)
            rotate((azar(s, 110 + j) - 0.5f) * 10f, Offset(x, asoma)) {
                drawRect(clara, Offset(x, y), Size(paso * 0.78f, asoma + 1f * u - y))
                if (j % 3 == 1) drawLine(P.TINTA.copy(alpha = 0.3f), Offset(x + paso * 0.35f, y + 1.5f * u), Offset(x + paso * 0.35f, asoma - 1f * u), 0.4f * u)
            }
        }
        // La funda, panzuda: más angosta en la boca y abajo, redonda en las esquinas.
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.CANAMO, 0.2f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.3f)
        val top = asoma
        val funda = Path().apply {
            moveTo(2.6f * u, top)
            lineTo(w - 2.6f * u, top)
            cubicTo(w + 0.4f * u, top + (h - top) * 0.3f, w + 0.2f * u, h - 6f * u, w - 3.5f * u, h - 0.5f * u)
            lineTo(3.5f * u, h - 0.5f * u)
            cubicTo(-0.2f * u, h - 6f * u, -0.4f * u, top + (h - top) * 0.3f, 2.6f * u, top)
            close()
        }
        drawPath(funda, cuero)
        bulto(0.5f * u, w - 0.5f * u, top, h, a, luzDe)
        // Las arrugas de la panza y el roce de las esquinas.
        for (j in 0 until 3) {
            val y = top + (h - top) * (0.45f + 0.16f * j + 0.06f * azar(s, 140 + j))
            val p = Path().apply {
                moveTo(w * 0.12f, y)
                quadraticTo(w * 0.35f, y + 2f * u, w * (0.45f + 0.25f * azar(s, 150 + j)), y + 0.5f * u)
            }
            drawPath(p, hondo.copy(alpha = 0.55f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        }
        drawOval(a.claro(0.14f), Offset(w * 0.15f, h * (0.62f + 0.12f * azar(s, 5))), Size(w * 0.4f, h * 0.06f))
        // La solapa doblada por delante, con su costura de punto y la correa con el botón de hueso.
        val sol = top + (h - top) * 0.2f
        val solapa = Path().apply {
            moveTo(2.6f * u, top)
            lineTo(w - 2.6f * u, top)
            lineTo(w - 2.4f * u, sol - 3f * u)
            quadraticTo(w * 0.5f, sol + 4f * u, 2.4f * u, sol - 3f * u)
            close()
        }
        drawPath(corridoRomano(solapa, 0.6f * u, 1.4f * u), a.sombra(0.35f))
        drawPath(solapa, mezcla(cuero, P.HUESO, 0.08f))
        drawRect(a.claro(0.3f), Offset(2.6f * u, top), Size(w - 5.2f * u, 0.9f * u))
        var x = 4f * u; var i = 0
        while (x < w - 4f * u && i < 30) {
            val t = (x - 2.4f * u) / maxOf(1f, w - 4.8f * u)
            val y = sol - 3f * u + 7f * u * t * (1f - t) * 4f * 0.5f - 1.6f * u
            drawLine(hondo, Offset(x, y), Offset(x + 1.2f * u, y + 0.2f * u), 0.6f * u)
            x += 2.4f * u; i++
        }
        val boton = Offset(w * 0.5f, sol + 2.5f * u)
        drawCircle(a.frio(0.4f), 1.9f * u, boton + Offset(0.5f * u, 0.6f * u))
        drawCircle(a.etiqueta(P.HUESO, k), 1.8f * u, boton)
        // El cordel que ciñe la panza y la tablilla colgada.
        val c1 = top + (h - top) * 0.36f
        val cordel = a.luz(P.CANAMO, k)
        drawLine(a.frio(0.4f), Offset(0f, c1 + 1.2f * u), Offset(w, c1 + 0.2f * u), 1.1f * u)
        drawLine(cordel, Offset(0f, c1), Offset(w, c1 - 1f * u), 1.3f * u)
        drawCircle(cordel, 1.5f * u, Offset(w * 0.3f, c1 - 0.3f * u))
        val an = minOf(w - 5f * u, 24f * u)
        val tx = w * (0.52f + 0.06f * (azar(s, 7) - 0.5f))
        val et = Rect(tx - an / 2, c1 + 3f * u, tx + an / 2, c1 + 3f * u + ALTO_TABLILLA * u)
        drawLine(cordel, Offset(tx, c1), Offset(tx, et.top), 0.8f * u)
        tablilla(et, v, medidor, a, k, s, P.ALISO)
        if (v.noche) {
            val r = minOf(w * 0.17f, 4.6f * u)
            if (et.bottom + 3f * u + 2 * r < h - 3f * u) luna(Offset(w * 0.5f, et.bottom + 3f * u + r), r, cuero, a, k)
        }
        filo(0.5f * u, w - 0.5f * u, top, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la arqueta

    /**
     * La arqueta, baja y ancha: caja de tablas a lo ancho pintada del ánimo (cruda, si no se marcó), con la tapa abierta de pie
     * detrás (se ve su cara de dentro, de madera, con el canto pintado), cantoneras y una faja de hierro, el pasador de bronce
     * y la placa de hueso del día clavada al frente. Dentro asoman las hojas: más cuanto más escribiste. En los Sueños, la
     * luna de plata en la tapa.
     */
    fun DrawScope.arqueta(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val pintura = cuerpo(v, a, k, mezcla(P.ROBLE_LUZ, P.CANAMO, 0.3f))
        val hondo = mezcla(pintura, a.hondo, 0.32f)
        val madera = a.luz(P.ROBLE, k)
        val altoCaja = h / 1.5f
        val boca = h - altoCaja
        // La tapa abierta, de pie detrás: la cara de dentro (madera) con su canto pintado y sus bisagras.
        val tapa = Rect(2f * u, 0f, w - 2f * u, boca + 2f * u)
        drawRect(mezcla(madera, a.hondo, 0.25f), tapa.topLeft, tapa.size)
        drawRect(pintura, tapa.topLeft, Size(tapa.width, 2.4f * u))
        drawRect(pintura, tapa.topLeft, Size(2.2f * u, tapa.height))
        drawRect(pintura, Offset(tapa.right - 2.2f * u, tapa.top), Size(2.2f * u, tapa.height))
        for (j in 1 until 3) drawLine(a.luz(P.ROBLE_OSC, k).copy(alpha = 0.5f), Offset(tapa.left + 2.2f * u, tapa.height * j / 3f), Offset(tapa.right - 2.2f * u, tapa.height * j / 3f), 0.6f * u)
        if (v.noche) luna(Offset(w / 2, tapa.height * 0.45f), minOf(tapa.height * 0.3f, w * 0.15f), mezcla(madera, a.hondo, 0.25f), a, k)
        // Las hojas que asoman de la caja.
        val asoma = (2f + 7f * v.lleno) * u
        var x = 3.5f * u; var j = 0
        while (x < w - 4f * u && j < 30) {
            val alto = asoma * (0.4f + 0.6f * azar(s, 160 + j))
            drawRect(a.etiqueta(P.ALISO, k * 0.9f), Offset(x, boca - alto), Size(1.8f * u, alto + 1f * u))
            drawLine(a.luz(P.ALISO_OSC, k), Offset(x + 1.8f * u, boca - alto), Offset(x + 1.8f * u, boca), 0.5f * u)
            x += 2.6f * u; j++
        }
        // La caja.
        val caja = Rect(0.8f * u, boca, w - 0.8f * u, h)
        drawRect(pintura, caja.topLeft, caja.size)
        for (t in 1 until 3) {
            val y = caja.top + caja.height * (t / 3f) + (azar(s, 170 + t) - 0.5f) * 1.5f * u
            drawLine(hondo.copy(alpha = 0.55f), Offset(caja.left, y), Offset(caja.right, y), 0.7f * u)
            drawLine(a.claro(0.18f), Offset(caja.left, y + 0.8f * u), Offset(caja.right, y + 0.8f * u), 0.5f * u)
        }
        drawRect(a.claro(0.35f), caja.topLeft, Size(caja.width, 1f * u))
        drawPath(poligonoRomano(caja.left, caja.bottom - 5f * u, caja.left + 3.5f * u, caja.bottom, caja.left, caja.bottom), madera)
        bulto(caja.left, caja.right, caja.top, caja.bottom, a, luzDe)
        drawRect(hondo, Offset(caja.left, caja.bottom - 2.4f * u), Size(caja.width, 2.4f * u))
        // Las cantoneras y la faja de hierro.
        val hierro = a.luz(P.HIERRO, k)
        val c = minOf(5f * u, caja.width * 0.2f)
        for ((cx0, sx) in listOf(caja.left to 1f, caja.right to -1f)) for ((cy0, sy) in listOf(caja.top to 1f, caja.bottom to -1f)) {
            drawPath(poligonoRomano(cx0, cy0, cx0 + sx * c, cy0, cx0 + sx * c * 0.4f, cy0 + sy * c * 0.4f, cx0, cy0 + sy * c), hierro)
            drawCircle(a.claro(0.4f), 0.6f * u, Offset(cx0 + sx * c * 0.3f, cy0 + sy * c * 0.3f))
        }
        // El pasador de bronce que cuelga de la tapa abierta y la placa de hueso del día.
        val cx = w / 2
        drawRect(a.luz(P.BRONCE, k), Offset(cx - 1.4f * u, boca - 3f * u), Size(2.8f * u, 5f * u))
        drawRect(a.luz(P.BRONCE_LUZ, k), Offset(cx - 1.4f * u, boca - 3f * u), Size(0.8f * u, 5f * u))
        val alt = minOf(ALTO_TABLILLA * u, caja.height - 5f * u)
        val an = minOf(w - 6f * u, 24f * u)
        val et = Rect(cx - an / 2, caja.top + (caja.height - alt) * 0.45f, cx + an / 2, caja.top + (caja.height - alt) * 0.45f + alt)
        tablilla(et, v, medidor, a, k, s, P.HUESO)
        clavoRomano(Offset(et.left + 1.6f * u, et.top + 1.6f * u), 0.7f * u, a, k)
        clavoRomano(Offset(et.right - 1.8f * u, et.top + 1.6f * u), 0.7f * u, a, k)
        filo(caja.left, caja.right, caja.top, caja.bottom, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la bolsa de cuero

    /**
     * La bolsa de cuero teñido del ánimo (cruda, si no se marcó), blanda y vencida: el cuello atado de lado con dos vueltas de
     * cordel, el volante de arriba que se dobla y cae hacia un costado; la panza que se aplasta abajo y se llena con lo
     * escrito (vacía, hace más pliegues); de un cabo cuelga el sello de plomo (en los Sueños, la luna) y del otro la tablilla.
     */
    fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.ROBLE_LUZ, 0.3f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.34f)
        val lado = if (azar(s, 4) > 0.5f) 1f else -1f
        val nx = w * (0.5f - 0.1f * lado)
        val ny = h * 0.3f
        val cu = w * 0.17f
        val lleno = 0.5f + 0.5f * v.lleno
        // El volante, que se dobla y cae hacia un lado.
        val volante = Path().apply {
            moveTo(nx - cu, ny)
            lineTo(nx - cu * 1.5f, ny - h * 0.1f)
            lineTo(nx - cu * 0.6f, ny - h * 0.16f)
            lineTo(nx + cu * 0.2f, ny - h * 0.24f)
            quadraticTo(nx + lado * cu * 2.4f, ny - h * 0.22f, nx + lado * cu * 2.2f, ny - h * 0.02f)
            lineTo(nx + lado * cu * 1.4f, ny - h * 0.06f)
            lineTo(nx + cu, ny)
            close()
        }
        drawPath(volante, mezcla(cuero, hondo, 0.2f))
        drawLine(hondo, Offset(nx - cu * 0.2f, ny - h * 0.2f), Offset(nx - cu * 0.1f, ny), 0.6f * u)
        drawLine(a.claro(0.3f), Offset(nx - cu * 1.4f, ny - h * 0.09f), Offset(nx - cu * 0.7f, ny - 0.5f * u), 0.8f * u)
        // La panza, vencida: los hombros caen y abajo se aplasta.
        val vientre = Path().apply {
            moveTo(nx - cu, ny)
            cubicTo(nx - cu * 2.2f, ny + h * 0.1f * lleno, 0f, h * (0.62f - 0.1f * v.lleno), 0.6f * u, h - 1.5f * u)
            quadraticTo(w * 0.5f, h + 0.8f * u, w - 0.6f * u, h - 1.5f * u)
            cubicTo(w, h * (0.6f - 0.1f * v.lleno), nx + cu * 2.2f, ny + h * 0.1f * lleno, nx + cu, ny)
            close()
        }
        drawPath(vientre, cuero)
        bulto(0.6f * u, w - 0.6f * u, ny + 4f * u, h, a, luzDe)
        drawOval(a.claro(0.16f), Offset(w * 0.1f, ny + (h - ny) * 0.3f), Size(w * 0.25f, (h - ny) * 0.45f))
        // Los pliegues: más cuanto más vacía.
        val pliegues = if (v.lleno > 0.5f) 2 else 4
        for (j in 0 until pliegues) {
            val x0 = nx + (j - (pliegues - 1) / 2f) * cu * 0.6f
            val x1 = w * (0.12f + 0.76f * (j + 0.5f) / pliegues) + (azar(s, 180 + j) - 0.5f) * 3f * u
            val p = Path().apply {
                moveTo(x0, ny + 1f * u)
                quadraticTo((x0 + x1) / 2 + (azar(s, 190 + j) - 0.5f) * 5f * u, ny + (h - ny) * 0.35f, x1, ny + (h - ny) * (0.45f + 0.3f * azar(s, 200 + j)))
            }
            drawPath(p, hondo.copy(alpha = 0.55f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(corridoRomano(p, -1f * u, 0f), a.claro(0.18f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        }
        // Donde se aplasta en la tabla.
        drawRect(a.sombra(0.3f), Offset(w * 0.08f, h - 2f * u), Size(w * 0.84f, 2f * u))
        // El cordel del cuello, con su nudo y sus dos cabos.
        val cordel = a.luz(P.CANAMO, k)
        for (d in listOf(-1.3f, 1.3f)) drawLine(cordel, Offset(nx - cu * 1.1f, ny + d * u), Offset(nx + cu * 1.1f, ny + d * u - 0.6f * u), 1.3f * u, cap = StrokeCap.Round)
        val nudo = Offset(nx + lado * cu * 0.8f, ny)
        drawCircle(cordel, 1.8f * u, nudo)
        // El sello de plomo (o la luna) colgado de un cabo.
        val selloR = minOf(w * 0.14f, 4.2f * u)
        val sello = Offset((nudo.x + lado * 3f * u).coerceIn(selloR + 1f * u, w - selloR - 1f * u), ny + 5f * u + selloR)
        drawLine(cordel, nudo, sello, 0.7f * u)
        val plomo = a.luz(mezcla(P.PLOMO, P.TINTA, 0.3f), k)
        if (v.noche) {
            drawCircle(plomo, selloR * 1.15f, sello)
            luna(sello, selloR * 0.85f, plomo, a, k)
        } else {
            drawCircle(a.frio(0.35f), selloR, sello + Offset(0.6f * u, 0.8f * u))
            drawCircle(a.luz(P.PLOMO, k), selloR, sello)
            drawCircle(a.claro(0.35f), selloR * 0.35f, sello - Offset(selloR * 0.3f, selloR * 0.3f))
            drawCircle(plomo, selloR * 0.55f, sello, style = Stroke(0.5f * u))
        }
        // La tablilla del día, colgada del otro cabo, sobre la panza.
        val an = minOf(w - 5f * u, 24f * u)
        val ty = minOf(sello.y + selloR + 2f * u, h - ALTO_TABLILLA * u - 3f * u)
        val tx = (w / 2 - lado * 1.5f * u)
        val et = Rect(tx - an / 2, ty, tx + an / 2, ty + ALTO_TABLILLA * u)
        drawLine(cordel, Offset(nx - lado * cu * 0.6f, ny + 1f * u), Offset(et.center.x, et.top), 0.7f * u)
        tablilla(et, v, medidor, a, k, s, P.ALISO)
        filo(0.6f * u, w - 0.6f * u, ny + 4f * u, h, a, k, luzDe)
    }
}
