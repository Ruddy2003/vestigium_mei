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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las tres clases de entrada del estante de Scriptorium ([MuebleEstanteMedieval]), elegidas con la semilla del día: el códice
 * románico de tablas cubierto de badana teñida, el libro guardado en su camisa de lana atada y el legajo de cartas en su
 * carpeta de badana. En las tres, el color del ánimo es el cuerpo entero (el cuero, la lana, la carpeta) y lo primero que se
 * ve; el día va en una cédula de pergamino chica; en los Sueños, la luna de plata está a la vista. [luzDe] dice de qué lado
 * llega la luz: -1, de la izquierda (la ventana de día); 1, de la derecha (el candil de noche, para lo que queda a su izquierda).
 */
internal object VolumenEstanteMedieval {
    private val P = PaletaMedieval

    /** El tamaño del día en la cédula, en sp. */
    private const val TAM_DIA = 12.5f
    /** El alto de la cédula del día, en dp. */
    private const val ALTO_CEDULA = 16f

    /** El color del ánimo del cuerpo, apenas hundido para que sea materia; [crudo] si no se marcó. */
    private fun cuerpo(v: Volumen, a: AireMedieval, k: Float, crudo: Color): Color =
        v.tono?.let { a.animo(mezcla(it, P.TINTE_HONDO, if (a.oscuro) 0.04f else 0.1f), k) } ?: a.luz(crudo, k)

    /** La luz de un lado y la sombra fría del otro sobre un cuerpo redondeado de [x0] a [x1]. */
    private fun DrawScope.bulto(x0: Float, x1: Float, y0: Float, y1: Float, a: AireMedieval, luzDe: Float, fuerza: Float = 1f) {
        val an = x1 - x0
        val (claroX, sombraX) = if (luzDe < 0) x0 + an * 0.1f to x1 - an * 0.3f else x1 - an * 0.22f to x0
        drawRect(a.claro(0.26f * fuerza), Offset(claroX, y0), Size(an * 0.12f, y1 - y0))
        drawRect(a.frio(0.16f * fuerza), Offset(sombraX, y0), Size(an * 0.3f, y1 - y0))
        drawRect(a.frio(0.14f * fuerza), Offset(if (luzDe < 0) x1 - an * 0.12f else x0, y0), Size(an * 0.12f, y1 - y0))
    }

    /** El filo del candil en el canto que lo mira (solo de noche). */
    private fun DrawScope.filo(x0: Float, x1: Float, y0: Float, y1: Float, a: AireMedieval, k: Float, luzDe: Float) {
        if (!a.oscuro || k < 0.06f) return
        val u = density
        drawRect(a.filo(0.6f * k), Offset(x0 + 1.5f * u, y0), Size(x1 - x0 - 3f * u, 1f * u))
        drawRect(a.filo(0.45f * k), Offset(if (luzDe < 0) x0 else x1 - 1f * u, y0 + 1.5f * u), Size(1f * u, y1 - y0 - 3f * u))
    }

    /** La luna de plata: un creciente con su brillo. */
    private fun DrawScope.luna(c: Offset, r: Float, fondo: Color, a: AireMedieval, k: Float) {
        val plata = if (a.oscuro) mezcla(P.LUNA, a.hondo, 0.25f - 0.25f * k) else P.LUNA
        drawCircle(a.frio(0.35f), r, c + Offset(r * 0.18f, r * 0.22f))
        drawCircle(plata, r, c)
        drawCircle(fondo, r * 0.82f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
    }

    /** Un texto secundario (el mes) sobre [fondo]: tinta sobre lo claro, pergamino sobre lo oscuro. */
    private fun tintaSobre(fondo: Color, a: AireMedieval, k: Float): Color =
        if (fondo.luminance() > 0.32f) P.TINTA.copy(alpha = 0.85f) else a.etiqueta(k).copy(alpha = 0.92f)

    /** Una cédula de pergamino chica con el día, cosida o atada: su sombra, el borde algo desigual y el número. */
    private fun DrawScope.cedula(r: Rect, dia: String, medidor: TextMeasurer, a: AireMedieval, k: Float, semilla: Int, cosida: Boolean) {
        val u = density
        val p = poligonoMedieval(r.left, r.top + 0.5f * u * azar(semilla, 61), r.right, r.top, r.right - 0.4f * u * azar(semilla, 62), r.bottom,
            r.left + 0.3f * u, r.bottom - 0.4f * u * azar(semilla, 63))
        drawPath(corridoMedieval(p, 0.9f * u, 1.1f * u), a.frio(0.45f))
        drawPath(p, a.etiqueta(k))
        drawRect(a.sombra(0.1f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
        drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
        if (cosida) {
            val hilo = a.luz(mezcla(P.CUERO, P.TINTA, 0.4f), k)
            for (x in listOf(r.left + 1.4f * u, r.right - 1.4f * u)) {
                drawLine(hilo, Offset(x, r.top - 0.8f * u), Offset(x, r.top + 2f * u), 0.7f * u)
                drawLine(hilo, Offset(x, r.bottom - 2f * u), Offset(x, r.bottom + 0.8f * u), 0.7f * u)
            }
        }
        textoEn(medidor, dia, Rect(r.left + 1.2f * u, r.top + 0.4f * u, r.right - 1.2f * u, r.bottom), P.LETRA, a.tinta, TAM_DIA, peso = FontWeight.Bold)
    }

    // ---------------------------------------------------------------- el códice románico

    /**
     * El códice románico de tablas, visto por el lomo, el más alto: cubierto entero de badana teñida del color del ánimo (cruda
     * si no se marcó), con la oreja de la cabezada que asoma arriba con su hilo de colores, tres nervios dobles, gofrado en seco
     * en rombos en los entrenervios, la cédula del día cosida y el mes a pluma. Entre las hojas asoman cédulas de pergamino: más
     * cuanto más escribiste. En los Sueños, una luna de plata en el entrenervio de arriba.
     */
    fun DrawScope.codice(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 5f * u
        val cuero = cuerpo(v, a, k, mezcla(P.BADANA, P.PERG_OSC, 0.15f + 0.4f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.32f)
        val n = 1 + (v.lleno * 3f).toInt()
        for (j in 0 until n) {
            val x = w * (0.2f + 0.2f * j) + 2f * u * azar(s, 70 + j)
            val inc = (azar(s, 80 + j) - 0.5f) * 3f * u
            drawPath(poligonoMedieval(x, top + 2f * u, x + 3.4f * u, top + 2f * u, x + 3.4f * u + inc, 0.2f * u, x + inc, 0.6f * u), a.etiqueta(k * 0.9f))
        }
        val oreja = Rect(w * 0.28f, top - 3.6f * u, w * 0.72f, top + 3f * u)
        drawRoundRect(mezcla(cuero, P.BADANA, 0.12f), oreja.topLeft, oreja.size, CornerRadius(3.4f * u))
        for (j in 0 until 5) drawLine(if (j % 2 == 0) a.luz(P.RUBRICA, k) else a.luz(P.VERDE, k), Offset(oreja.left + oreja.width * (0.15f + 0.17f * j), oreja.top + 1.2f * u), Offset(oreja.left + oreja.width * (0.15f + 0.17f * j), top + 0.6f * u), 0.9f * u)
        val caja = Rect(1f * u, top, w - 1f * u, h)
        drawRoundRect(cuero, caja.topLeft, caja.size, CornerRadius(1.6f * u))
        bulto(caja.left, caja.right, top, h, a, luzDe)
        drawOval(a.claro(0.1f), Offset(w * 0.18f, h * (0.6f + 0.2f * azar(s, 5))), Size(w * 0.5f, h * 0.08f))
        val alto = h - top
        val nervios = listOf(0.12f, 0.36f, 0.9f)
        for (f in nervios) {
            val y = top + alto * f
            for (d in listOf(0f, 3.4f * u)) {
                drawRect(a.frio(0.34f), Offset(0f, y + d + 1.8f * u), Size(w, 0.9f * u))
                drawRect(mezcla(cuero, hondo, 0.3f), Offset(0f, y + d - 0.6f * u), Size(w, 2.4f * u))
                drawRect(a.claro(0.4f), Offset(if (luzDe < 0) 0f else w * 0.3f, y + d - 0.6f * u), Size(w * 0.7f, 0.7f * u))
            }
        }
        // El entrenervio de arriba: un rombo gofrado o, en los Sueños, la luna.
        val y1 = top + alto * 0.12f
        val p1 = Rect(w * 0.15f, top + 1.5f * u, w * 0.85f, y1 - 1.5f * u)
        if (v.noche) luna(Offset(w * 0.5f, (top + y1) / 2), minOf(w * 0.17f, (y1 - top) * 0.38f, 4.6f * u), cuero, a, k)
        else if (p1.height > 3f * u) gofrado(p1, hondo)
        // La cédula del día, cosida, chica, entre el primer nervio y el segundo.
        val et = Rect(w * 0.15f, y1 + 6f * u, w * 0.85f, y1 + 6f * u + ALTO_CEDULA * u)
        cedula(et, v.dia, medidor, a, k, s, cosida = true)
        // El mes a pluma, a lo largo, entre el segundo nervio y el de abajo, con un rombo gofrado a cada lado.
        val y2 = top + alto * 0.36f + 6f * u; val y3 = top + alto * 0.9f - 2f * u
        val mes = Rect(w * 0.26f, y2, w * 0.74f, y3)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(cuero, a, k), 8.5f, vertical = true, minimo = 4.5f)
        drawLine(hondo, Offset(w * 0.18f, y2), Offset(w * 0.18f, y3), 0.7f * u)
        drawLine(hondo, Offset(w * 0.82f, y2), Offset(w * 0.82f, y3), 0.7f * u)
        filo(caja.left, caja.right, top, h, a, k, luzDe)
    }

    /** El gofrado en seco de un entrenervio: un rombo con sus diagonales y un punto. */
    private fun DrawScope.gofrado(r: Rect, color: Color) {
        val u = density
        val rombo = poligonoMedieval(r.center.x, r.top, r.right, r.center.y, r.center.x, r.bottom, r.left, r.center.y)
        drawPath(rombo, color, style = Stroke(0.8f * u))
        drawLine(color, Offset(r.left, r.top), Offset(r.right, r.bottom), 0.5f * u)
        drawLine(color, Offset(r.right, r.top), Offset(r.left, r.bottom), 0.5f * u)
        drawCircle(color, 0.9f * u, r.center)
    }

    // ---------------------------------------------------------------- el libro en su camisa

    /**
     * El libro guardado en su camisa: la funda de lana teñida del color del ánimo (lino crudo si no se marcó) que lo envuelve
     * entero; lo que sobra por arriba se junta en el cuello con un cordón (más vueltas cuanto más escribiste) y se abre encima
     * en lóbulos que caen por los lados. Del cordón cuelgan sus dos cabos y la cédula del día. Se adivinan debajo los nervios
     * del lomo. En los Sueños, una luna bordada en plata.
     */
    fun DrawScope.camisa(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val tela = cuerpo(v, a, k, mezcla(P.LINO, P.PERG_OSC, 0.3f * azar(s, 3)))
        val reves = mezcla(tela, a.hondo, 0.4f)
        val cuello = h * 0.24f
        val ent = w * 0.22f
        val hombro = cuello + h * 0.1f
        // El cuerpo: del cuello estrecho a los hombros, y abajo el libro envuelto.
        val cuerpoP = Path().apply {
            moveTo(ent, cuello)
            lineTo(w - ent, cuello)
            cubicTo(w - ent * 0.4f, cuello + h * 0.03f, w - 0.2f * u, hombro - h * 0.04f, w - 0.3f * u, hombro)
            cubicTo(w + 0.2f * u, h * 0.6f, w, h * 0.85f, w - 0.4f * u, h)
            lineTo(0.4f * u, h)
            cubicTo(0f, h * 0.85f, -0.2f * u, h * 0.6f, 0.3f * u, hombro)
            cubicTo(0.2f * u, hombro - h * 0.04f, ent * 0.4f, cuello + h * 0.03f, ent, cuello)
            close()
        }
        drawPath(cuerpoP, tela)
        clipPath(cuerpoP) {
            bulto(0f, w, cuello, h, a, luzDe)
            // Los pliegues que bajan del cuello y se abren.
            for (j in 0 until 4) {
                val x0 = w * (0.3f + 0.13f * j)
                val x1 = w * (0.06f + 0.29f * j + 0.06f * azar(s, 10 + j))
                val pl = Path().apply {
                    moveTo(x0, cuello)
                    cubicTo(x0 + (x1 - x0) * 0.7f, hombro, x1 - 2f * u, h * 0.7f, x1 + 1f * u, h)
                }
                drawPath(pl, a.frio(0.26f), style = Stroke(1.7f * u))
                drawPath(corridoMedieval(pl, -1.7f * u, 0f), a.claro(0.18f), style = Stroke(1f * u))
            }
            for (f in listOf(0.64f, 0.88f)) {
                val y = h * f
                drawRect(a.claro(0.12f), Offset(0f, y - 2f * u), Size(w, 1.4f * u))
                drawRect(a.frio(0.18f), Offset(0f, y), Size(w, 1.6f * u))
            }
            for (j in 0 until 9) drawLine(a.sombra(0.06f), Offset(0f, cuello + (h - cuello) * j / 9f), Offset(w, cuello + (h - cuello) * j / 9f + 0.5f * u), 0.5f * u)
        }
        // El fruncido de arriba: lóbulos más anchos que el cuello, que caen por los lados, con el revés asomando.
        val n = if (w > 34f * u) 4 else 3
        val tops = FloatArray(n + 1) { i -> cuello * (0.1f + 0.25f * azar(s, 20 + i)) }
        val cae = cuello + h * 0.05f
        val fruncido = Path().apply {
            moveTo(ent, cuello)
            cubicTo(ent * 0.5f, cuello - 2f * u, 0.4f * u, cuello + 1f * u, 0.8f * u, cae)
            cubicTo(-0.6f * u, cuello * 0.7f, -0.2f * u, cuello * 0.4f, 0.8f * u, tops[0])
            for (i in 0 until n) {
                val xa = 0.8f * u + (w - 1.6f * u) * i / n; val xb = 0.8f * u + (w - 1.6f * u) * (i + 1) / n
                val pico = minOf(tops[i], tops[i + 1]) - cuello * (0.1f + 0.12f * azar(s, 30 + i))
                cubicTo(xa + (xb - xa) * 0.15f, pico, xb - (xb - xa) * 0.15f, pico, xb, tops[i + 1])
            }
            cubicTo(w + 0.2f * u, cuello * 0.4f, w + 0.6f * u, cuello * 0.7f, w - 0.8f * u, cae)
            cubicTo(w - 0.4f * u, cuello + 1f * u, w - ent * 0.5f, cuello - 2f * u, w - ent, cuello)
            close()
        }
        drawPath(corridoMedieval(fruncido, 0.8f * u, 1.3f * u), a.frio(0.32f))
        drawPath(fruncido, tela)
        clipPath(fruncido) {
            for (i in 0..n) {
                val xb = 0.8f * u + (w - 1.6f * u) * i / n
                drawOval(reves, Offset(xb - w * 0.09f, tops[i] - 1.6f * u), Size(w * 0.18f, 3.8f * u))
                drawLine(a.frio(0.34f), Offset(xb, tops[i] + 1.5f * u), Offset(w / 2 + (xb - w / 2) * 0.35f, cuello), 0.9f * u)
            }
            for (i in 0 until n) {
                val xm = 0.8f * u + (w - 1.6f * u) * (i + 0.5f) / n
                drawLine(a.claro(0.3f), Offset(xm, tops[i] + 2f * u), Offset(w / 2 + (xm - w / 2) * 0.35f, cuello - 1f * u), 1f * u)
            }
            // Los lóbulos que caen por los lados, con su revés.
            drawOval(reves, Offset(-w * 0.04f, cuello * 0.62f), Size(w * 0.2f, cae - cuello * 0.6f))
            drawOval(reves, Offset(w * 0.84f, cuello * 0.62f), Size(w * 0.2f, cae - cuello * 0.6f))
            drawRect(a.frio(0.18f), Offset(if (luzDe < 0) w * 0.66f else 0f, 0f), Size(w * 0.34f, cae))
        }
        // El cordón al cuello: vueltas trenzadas, el nudo y los dos cabos con su nudillo.
        val vueltas = 1 + (v.lleno * 2.4f).toInt()
        val cordon = a.luz(mezcla(P.CUERO, P.OCRE, 0.35f), k)
        for (j in 0 until vueltas) {
            val y = cuello - 0.6f * u + j * 2.2f * u
            drawLine(a.frio(0.4f), Offset(ent - 1.6f * u, y + 1.4f * u), Offset(w - ent + 1.6f * u, y + 2f * u), 1.8f * u)
            drawLine(cordon, Offset(ent - 1.6f * u, y), Offset(w - ent + 1.6f * u, y + 0.6f * u), 2f * u, StrokeCap.Round)
            var x = ent
            while (x < w - ent) { drawLine(a.frio(0.4f), Offset(x, y - 0.9f * u), Offset(x + 1.2f * u, y + 1f * u), 0.5f * u); x += 2f * u }
        }
        val izq = azar(s, 4) > 0.5f
        val nudo = Offset(w * (if (izq) 0.38f else 0.6f), cuello + vueltas * 1.1f * u)
        drawCircle(cordon, 2.4f * u, nudo)
        drawCircle(a.claro(0.3f), 0.8f * u, nudo - Offset(0.7f * u, 0.7f * u))
        for ((dx, largo) in listOf(-3f to 9f, 3.5f to 12f)) {
            val fin = nudo + Offset(dx * u, largo * u)
            drawLine(cordon, nudo, fin, 1.3f * u, StrokeCap.Round)
            drawCircle(cordon, 1.5f * u, fin)
        }
        // La cédula del día, chica, atada al cordón por un hilo.
        val et = Rect(w * 0.15f, nudo.y + 13f * u, w * 0.85f, nudo.y + 13f * u + ALTO_CEDULA * u)
        drawLine(a.luz(P.LINO, k), nudo, Offset(et.center.x, et.top + 1f * u), 0.7f * u)
        cedula(et, v.dia, medidor, a, k, s, cosida = false)
        val pieMes = if (v.noche) h - 13f * u else h - 3f * u
        val mes = Rect(w * 0.26f, et.bottom + 4f * u, w * 0.74f, pieMes)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(tela, a, k), 8f, vertical = true, minimo = 4.5f)
        if (v.noche) luna(Offset(w * 0.5f, h - 7f * u), minOf(w * 0.17f, 4.4f * u), tela, a, k)
        filo(0.5f * u, w - 0.5f * u, hombro, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- el legajo de cartas

    /**
     * El legajo de cartas del archivo, el más bajo: las cartas de pergamino dobladas y de pie, que asoman arriba con sus cantos
     * desparejos, guardadas en una carpeta de badana teñida del color del ánimo (cruda si no se marcó), con su solapa de borde
     * redondo y la correa que la cierra; bajo la correa, la cédula del día; abajo, por delante, los sellos de cera colgando de
     * sus tiras (más cartas y más sellos cuanto más escribiste). En los Sueños, una luna de plata en la solapa.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val capas = 3 + (v.lleno * 4f).toInt()
        val yCarpeta = h * 0.2f
        // Las cartas que asoman por arriba, cada una a su altura y algo ladeada.
        for (j in capas - 1 downTo 0) {
            val top = (1f + 6f * azar(s, 20 + j)) * u
            val x0 = 1.6f * u + j * 0.9f * u; val x1 = w - 1.6f * u - (capas - 1 - j) * 0.7f * u
            val inc = (azar(s, 40 + j) - 0.5f) * 2f * u
            val hoja = poligonoMedieval(x0, top + inc, x1, top - inc, x1, yCarpeta + 4f * u, x0, yCarpeta + 4f * u)
            drawPath(hoja, a.luz(mezcla(P.PERGAMINO, P.PERG_OSC, 0.15f + 0.6f * azar(s, 30 + j)), k))
            drawLine(a.frio(0.35f), Offset(x0, top + inc + 0.6f * u), Offset(x1, top - inc + 0.6f * u), 0.6f * u)
        }
        // La carpeta: badana teñida, con su redondez.
        val badana = cuerpo(v, a, k, P.BADANA)
        val hondo = mezcla(badana, a.hondo, 0.35f)
        val carpeta = Rect(0.4f * u, yCarpeta, w - 0.4f * u, h)
        drawRoundRect(badana, carpeta.topLeft, carpeta.size, CornerRadius(2.4f * u))
        bulto(carpeta.left, carpeta.right, carpeta.top, carpeta.bottom, a, luzDe)
        drawRect(a.claro(0.3f), Offset(carpeta.left + 2f * u, carpeta.top), Size(carpeta.width - 4f * u, 1f * u))
        // La solapa, que baja por delante con su borde redondo y su pespunte.
        val yS = h * 0.46f
        val solapa = Path().apply {
            moveTo(carpeta.left, carpeta.top + 1f * u); lineTo(carpeta.right, carpeta.top + 1f * u); lineTo(carpeta.right, yS - h * 0.06f)
            cubicTo(carpeta.right, yS + h * 0.02f, carpeta.left, yS + h * 0.02f, carpeta.left, yS - h * 0.06f); close()
        }
        drawPath(corridoMedieval(solapa, 0f, 1.6f * u), a.frio(0.35f))
        drawPath(solapa, mezcla(badana, P.LUNA, if (a.oscuro) 0f else 0.05f))
        clipPath(solapa) {
            var x = carpeta.left + 2.4f * u
            while (x < carpeta.right - 2f * u) { drawLine(hondo, Offset(x, yS - h * 0.035f + 0.02f * h * kotlin.math.sin((x / w) * 3.14f)), Offset(x + 1.6f * u, yS - h * 0.035f + 0.02f * h * kotlin.math.sin((x / w) * 3.14f)), 0.7f * u); x += 3f * u }
            drawRect(a.claro(0.12f), Offset(carpeta.left, carpeta.top), Size(carpeta.width * 0.4f, yS))
        }
        if (v.noche) luna(Offset(w * 0.5f, (carpeta.top + yS) / 2 - 1f * u), minOf(w * 0.17f, 4.4f * u), badana, a, k)
        // La correa que la ciñe y, bajo ella, la cédula del día.
        val yC = yS + h * 0.06f
        drawRect(a.frio(0.4f), Offset(0f, yC + 2.8f * u), Size(w, 1.2f * u))
        drawRect(a.luz(P.CUERO, k), Offset(0f, yC), Size(w, 3f * u))
        drawRect(a.claro(0.3f), Offset(0f, yC), Size(w * 0.6f, 0.7f * u))
        // La cédula, metida por arriba bajo la correa: la correa le pisa solo el borde.
        val et = Rect(w * 0.15f, yC + 1.2f * u, w * 0.85f, yC + 1.2f * u + ALTO_CEDULA * u)
        cedula(et, v.dia, medidor, a, k, s, cosida = false)
        drawRect(a.frio(0.35f), Offset(et.left - 1f * u, yC + 3f * u), Size(et.width + 2f * u, 0.9f * u))
        drawRect(a.luz(P.CUERO, k), Offset(et.left - 1f * u, yC), Size(et.width + 2f * u, 3f * u))
        // El mes, en la carpeta, abreviado si no cabe (como en los registros).
        val pieMes = h - 2f * minOf(w * 0.17f, 4.6f * u) - 4.5f * u
        val mes = Rect(w * 0.1f, et.bottom + 1.5f * u, w * 0.9f, pieMes)
        val cabe = v.mes.length * 6f * 0.52f * u <= mes.width
        val nombre = if (cabe || v.mes.length <= 4) v.mes else v.mes.take(3) + "."
        if (mes.height > 5f * u) textoEn(medidor, nombre, mes, P.LETRA, tintaSobre(badana, a, k), 7.5f, minimo = 5f)
        // Los sellos colgando de sus tiras, por delante y abajo.
        val sellos = 1 + (v.lleno * 2.2f).toInt().coerceAtMost(2)
        val rs = minOf(w * 0.17f, 4.6f * u)
        for (j in 0 until sellos) {
            val cx = w * (0.26f + 0.3f * j) + 1.5f * u * azar(s, 50 + j)
            val c = Offset(cx, h - rs - 0.6f * u)
            // La tira sale por abajo de la carpeta, sin tapar el mes.
            drawLine(a.luz(P.PERG_OSC, k), Offset(cx - 0.6f * u, c.y - rs - 3.5f * u), c - Offset(0f, rs * 0.6f), 1.8f * u)
            val cera = P.CERAS[(s + j) % P.CERAS.size]
            drawCircle(a.frio(0.4f), rs, c + Offset(0.8f * u, 1f * u))
            drawCircle(a.luz(cera, k), rs, c)
            drawCircle(a.sombra(0.35f), rs * 0.7f, c, style = Stroke(0.6f * u))
            drawLine(a.sombra(0.4f), c - Offset(0f, rs * 0.45f), c + Offset(0f, rs * 0.45f), 0.7f * u)
            drawLine(a.sombra(0.4f), c - Offset(rs * 0.45f, 0f), c + Offset(rs * 0.45f, 0f), 0.7f * u)
            drawCircle(a.claro(0.45f), rs * 0.28f, c - Offset(rs * 0.38f, rs * 0.38f))
            if (a.oscuro && k > 0.06f) drawArc(a.filo(0.6f * k), if (luzDe < 0) 160f else -60f, 110f, false, Offset(c.x - rs, c.y - rs), Size(2 * rs, 2 * rs), style = Stroke(0.8f * u))
        }
        filo(carpeta.left, carpeta.right, carpeta.top, h, a, k, luzDe)
    }
}
