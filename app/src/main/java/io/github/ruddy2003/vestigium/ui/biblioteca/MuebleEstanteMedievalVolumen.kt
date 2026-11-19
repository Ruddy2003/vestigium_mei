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
 * románico de tablas cubierto de badana teñida, el libro guardado en su camisa de lana y el legajo de cartas selladas. En
 * las tres, el día va en pergamino con tinta, el color del ánimo es lo más grande del objeto (el cuero, la lana, la correa)
 * y, en los Sueños, la luna de plata está a la vista. [luzDe] dice de qué lado llega la luz: -1, de la izquierda (la ventana
 * de día); 1, de la derecha (el candil de noche, para lo que queda a su izquierda).
 */
internal object VolumenEstanteMedieval {
    private val P = PaletaMedieval

    /** La luz de un lado y la sombra del otro sobre un cuerpo redondeado de [x0] a [x1]. */
    private fun DrawScope.bulto(x0: Float, x1: Float, y0: Float, y1: Float, a: AireMedieval, luzDe: Float, fuerza: Float = 1f) {
        val an = x1 - x0
        val (claroX, sombraX) = if (luzDe < 0) x0 + an * 0.1f to x1 - an * 0.3f else x1 - an * 0.22f to x0
        drawRect(a.claro(0.26f * fuerza), Offset(claroX, y0), Size(an * 0.12f, y1 - y0))
        drawRect(a.sombra(0.16f * fuerza), Offset(sombraX, y0), Size(an * 0.3f, y1 - y0))
        drawRect(a.sombra(0.14f * fuerza), Offset(if (luzDe < 0) x1 - an * 0.12f else x0, y0), Size(an * 0.12f, y1 - y0))
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
        drawCircle(a.sombra(0.3f), r, c + Offset(r * 0.18f, r * 0.22f))
        drawCircle(plata, r, c)
        drawCircle(fondo, r * 0.82f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
    }

    /** Un texto secundario (el mes) sobre [fondo]: tinta sobre lo claro, pergamino sobre lo oscuro. */
    private fun tintaSobre(fondo: Color, a: AireMedieval, k: Float): Color =
        if (fondo.luminance() > 0.32f) P.TINTA.copy(alpha = 0.82f) else a.etiqueta(k).copy(alpha = 0.92f)

    /** Una cédula de pergamino con el día, cosida o clavada: su sombra, el borde algo desigual y el número. */
    private fun DrawScope.cedula(r: Rect, dia: String, medidor: TextMeasurer, a: AireMedieval, k: Float, semilla: Int, cosida: Boolean) {
        val u = density
        val p = poligonoMedieval(r.left, r.top + 0.5f * u * azar(semilla, 61), r.right, r.top, r.right - 0.4f * u * azar(semilla, 62), r.bottom,
            r.left + 0.3f * u, r.bottom - 0.4f * u * azar(semilla, 63))
        drawPath(Path().apply { addPath(p, Offset(0.9f * u, 1.1f * u)) }, a.sombra(0.42f))
        drawPath(p, a.etiqueta(k))
        drawRect(a.sombra(0.1f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
        drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
        if (cosida) {
            val hilo = a.luz(mezcla(P.CUERO, P.TINTA, 0.4f), k)
            for (x in listOf(r.left + 1.6f * u, r.right - 1.6f * u)) {
                drawLine(hilo, Offset(x, r.top - 0.8f * u), Offset(x, r.top + 2.2f * u), 0.7f * u)
                drawLine(hilo, Offset(x, r.bottom - 2.2f * u), Offset(x, r.bottom + 0.8f * u), 0.7f * u)
            }
        }
        textoEn(medidor, dia, Rect(r.left + 1.5f * u, r.top + 0.5f * u, r.right - 1.5f * u, r.bottom), P.LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
    }

    // ---------------------------------------------------------------- el códice románico

    /**
     * El códice románico de tablas, visto por el lomo, el más alto: cubierto de badana teñida del color del ánimo (cruda si no
     * se marcó), con las orejas de la cabezada que asoman arriba, tres nervios dobles, gofrado en seco en los entrenervios, la
     * cédula del día cosida y el mes a pluma. Entre las hojas asoman cédulas de pergamino: más cuanto más escribiste. En los
     * Sueños, una luna de plata en el entrenervio de arriba.
     */
    fun DrawScope.codice(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val top = 5f * u
        val cuero = v.tono?.let { a.animo(mezcla(it, P.TINTE_HONDO, if (a.oscuro) 0.08f else 0.2f), k) }
            ?: a.luz(mezcla(P.BADANA, P.PERG_OSC, 0.15f + 0.4f * azar(s, 3)), k)
        val hondo = mezcla(cuero, a.hondo, 0.3f)
        // Las cédulas que asoman por la cabeza, ladeadas.
        val n = 1 + (v.lleno * 3f).toInt()
        for (j in 0 until n) {
            val x = w * (0.2f + 0.2f * j) + 2f * u * azar(s, 70 + j)
            val inc = (azar(s, 80 + j) - 0.5f) * 3f * u
            drawPath(poligonoMedieval(x, top + 2f * u, x + 3.4f * u, top + 2f * u, x + 3.4f * u + inc, 0.2f * u, x + inc, 0.6f * u), a.etiqueta(k * 0.9f))
        }
        // La oreja de la cabezada, con el hilo de colores del cosido.
        val oreja = Rect(w * 0.28f, top - 3.6f * u, w * 0.72f, top + 3f * u)
        drawRoundRect(mezcla(cuero, P.BADANA, 0.15f), oreja.topLeft, oreja.size, CornerRadius(3.4f * u))
        for (j in 0 until 5) drawLine(if (j % 2 == 0) a.luz(P.RUBRICA, k) else a.etiqueta(k), Offset(oreja.left + oreja.width * (0.15f + 0.17f * j), oreja.top + 1.2f * u), Offset(oreja.left + oreja.width * (0.15f + 0.17f * j), top + 0.6f * u), 0.9f * u)
        // El cuerpo y su redondez.
        val cuerpo = Rect(1f * u, top, w - 1f * u, h)
        drawRoundRect(cuero, cuerpo.topLeft, cuerpo.size, CornerRadius(1.6f * u))
        bulto(cuerpo.left, cuerpo.right, top, h, a, luzDe)
        // El roce de las manos y alguna mancha.
        drawOval(a.claro(0.1f), Offset(w * 0.18f, h * (0.6f + 0.2f * azar(s, 5))), Size(w * 0.5f, h * 0.08f))
        val alto = h - top
        val nervios = listOf(0.13f, 0.42f, 0.9f)
        for (f in nervios) {
            val y = top + alto * f
            for (d in listOf(0f, 3.4f * u)) {
                drawRect(a.sombra(0.32f), Offset(0f, y + d + 1.8f * u), Size(w, 0.9f * u))
                drawRect(mezcla(cuero, hondo, 0.25f), Offset(0f, y + d - 0.6f * u), Size(w, 2.4f * u))
                drawRect(a.claro(0.38f), Offset(if (luzDe < 0) 0f else w * 0.3f, y + d - 0.6f * u), Size(w * 0.7f, 0.7f * u))
            }
        }
        // El entrenervio de arriba: gofrado en rombo o, en los Sueños, la luna.
        val y1 = top + alto * 0.13f
        val p1 = Rect(w * 0.15f, top + 1.5f * u, w * 0.85f, y1 - 1.5f * u)
        if (v.noche) luna(Offset(w * 0.5f, (top + y1) / 2), minOf(w * 0.17f, (y1 - top) * 0.36f, 4.6f * u), cuero, a, k)
        else if (p1.height > 3f * u) {
            val rombo = poligonoMedieval(p1.center.x, p1.top, p1.right, p1.center.y, p1.center.x, p1.bottom, p1.left, p1.center.y)
            drawPath(rombo, a.sombra(0.32f), style = Stroke(0.7f * u))
            drawCircle(a.sombra(0.3f), 0.9f * u, p1.center)
        }
        // La cédula del día, cosida, entre el primer nervio y el segundo.
        val et = Rect(w * 0.1f, y1 + 6f * u, w * 0.9f, y1 + 26f * u)
        cedula(et, v.dia, medidor, a, k, s, cosida = true)
        // El mes a pluma, a lo largo, entre el segundo nervio y el de abajo.
        val y2 = top + alto * 0.42f + 6f * u; val y3 = top + alto * 0.9f - 2f * u
        val mes = Rect(w * 0.24f, y2, w * 0.76f, y3)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(cuero, a, k), 8.5f, vertical = true, minimo = 4.5f)
        // El gofrado del entrenervio de abajo: dos filetes.
        filo(cuerpo.left, cuerpo.right, top, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- el libro en su camisa

    /**
     * El libro guardado en su camisa: la funda de lana teñida del color del ánimo (lino crudo si no se marcó) que lo envuelve
     * entero; lo que sobra por arriba se junta y se ata con una tira de cuero (más vueltas cuanto más escribiste), y se abre
     * encima en un fruncido de pliegues. Se adivinan debajo los nervios del lomo. Del nudo cuelga la cédula del día. En los
     * Sueños, una luna bordada en plata.
     */
    fun DrawScope.camisa(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val tela = v.tono?.let { a.animo(mezcla(it, P.TINTE_HONDO, if (a.oscuro) 0.12f else 0.28f), k) }
            ?: a.luz(mezcla(P.LINO, P.PERG_OSC, 0.3f * azar(s, 3)), k)
        val reves = mezcla(tela, a.hondo, 0.42f)
        val cuello = h * 0.2f
        val ent = 2.8f * u
        // El cuerpo: la funda sobre el libro, que se estrecha hacia el cuello y se abomba apenas.
        val cuerpo = Path().apply {
            moveTo(ent, cuello)
            lineTo(w - ent, cuello)
            cubicTo(w - 0.4f * u, cuello + h * 0.08f, w, h * 0.7f, w - 0.4f * u, h)
            lineTo(0.4f * u, h)
            cubicTo(0f, h * 0.7f, 0.4f * u, cuello + h * 0.08f, ent, cuello)
            close()
        }
        drawPath(cuerpo, tela)
        clipPath(cuerpo) {
            bulto(0f, w, cuello, h, a, luzDe)
            // Los pliegues que bajan del cuello, ondulados.
            for (j in 0 until 3) {
                val x0 = w * (0.3f + 0.2f * j)
                val x1 = w * (0.12f + 0.38f * j + 0.08f * azar(s, 10 + j))
                val pl = Path().apply {
                    moveTo(x0, cuello)
                    cubicTo(x0 + (x1 - x0) * 0.6f, h * 0.38f, x1 - 2f * u, h * 0.7f, x1 + 1f * u, h)
                }
                drawPath(pl, a.sombra(0.24f), style = Stroke(1.7f * u))
                drawPath(Path().apply { addPath(pl, Offset(-1.7f * u, 0f)) }, a.claro(0.17f), style = Stroke(1f * u))
            }
            // Los nervios del lomo, que se adivinan debajo.
            for (f in listOf(0.6f, 0.87f)) {
                val y = h * f
                drawRect(a.claro(0.13f), Offset(0f, y - 2f * u), Size(w, 1.4f * u))
                drawRect(a.sombra(0.2f), Offset(0f, y), Size(w, 1.6f * u))
            }
            // La trama de la lana.
            for (j in 0 until 9) drawLine(a.sombra(0.07f), Offset(0f, cuello + (h - cuello) * j / 9f), Offset(w, cuello + (h - cuello) * j / 9f + 0.5f * u), 0.5f * u)
        }
        // El fruncido de arriba: la tela juntada que se abre en lóbulos, con su revés asomando por el borde.
        val n = if (w > 34f * u) 4 else 3
        val tops = FloatArray(n + 1) { i -> cuello * (0.12f + 0.3f * azar(s, 20 + i)) }
        val fruncido = Path().apply {
            moveTo(ent, cuello)
            cubicTo(ent - 1.4f * u, cuello * 0.75f, 0.2f * u, cuello * 0.5f, 0.6f * u, tops[0])
            for (i in 0 until n) {
                val xa = 0.6f * u + (w - 1.2f * u) * i / n; val xb = 0.6f * u + (w - 1.2f * u) * (i + 1) / n
                val pico = minOf(tops[i], tops[i + 1]) - cuello * (0.12f + 0.12f * azar(s, 30 + i))
                cubicTo(xa + (xb - xa) * 0.15f, pico, xb - (xb - xa) * 0.15f, pico, xb, tops[i + 1])
            }
            cubicTo(w - 0.2f * u, cuello * 0.5f, w - ent + 1.4f * u, cuello * 0.75f, w - ent, cuello)
            close()
        }
        drawPath(Path().apply { addPath(fruncido, Offset(0.8f * u, 1.2f * u)) }, a.sombra(0.3f))
        drawPath(fruncido, mezcla(tela, P.LUNA, if (a.oscuro) 0f else 0.06f))
        clipPath(fruncido) {
            // El revés de la tela en las bocas de los lóbulos y los pliegues que bajan al nudo.
            for (i in 0..n) {
                val xb = 0.6f * u + (w - 1.2f * u) * i / n
                drawOval(reves, Offset(xb - w * 0.08f, tops[i] - 1.6f * u), Size(w * 0.16f, 3.6f * u))
                drawLine(a.sombra(0.32f), Offset(xb, tops[i] + 1f * u), Offset(w / 2 + (xb - w / 2) * 0.3f, cuello), 0.9f * u)
            }
            for (i in 0 until n) {
                val xm = 0.6f * u + (w - 1.2f * u) * (i + 0.5f) / n
                drawLine(a.claro(0.28f), Offset(xm, tops[i] + 1.5f * u), Offset(w / 2 + (xm - w / 2) * 0.3f, cuello - 1f * u), 0.9f * u)
            }
            drawRect(a.sombra(0.2f), Offset(if (luzDe < 0) w * 0.68f else 0f, 0f), Size(w * 0.32f, cuello))
        }
        // La tira de cuero al cuello, con sus vueltas, el nudo y los cabos.
        val vueltas = 1 + (v.lleno * 2.4f).toInt()
        val tira = a.luz(P.CUERO, k)
        for (j in 0 until vueltas) {
            val y = cuello - 1f * u + j * 2.2f * u
            drawLine(a.sombra(0.35f), Offset(ent - 1f * u, y + 1.4f * u), Offset(w - ent + 1f * u, y + 2f * u), 1.6f * u)
            drawLine(tira, Offset(ent - 1f * u, y), Offset(w - ent + 1f * u, y + 0.6f * u), 1.9f * u)
            drawLine(a.claro(0.3f), Offset(ent, y - 0.5f * u), Offset(w * 0.5f, y - 0.3f * u), 0.5f * u)
        }
        val izq = azar(s, 4) > 0.5f
        val nudo = Offset(w * (if (izq) 0.34f else 0.62f), cuello + vueltas * 1.1f * u)
        drawCircle(tira, 2.3f * u, nudo)
        drawCircle(a.claro(0.3f), 0.8f * u, nudo - Offset(0.7f * u, 0.7f * u))
        drawLine(tira, nudo, nudo + Offset(if (izq) -3f * u else 3f * u, 8f * u), 1.2f * u, StrokeCap.Round)
        // La cédula del día, colgada del nudo por un hilo.
        val et = Rect(w * 0.1f, nudo.y + 6f * u, w * 0.9f, nudo.y + 26f * u)
        drawLine(a.luz(P.LINO, k), nudo, Offset(et.center.x, et.top + 1f * u), 0.7f * u)
        cedula(et, v.dia, medidor, a, k, s, cosida = false)
        // El mes, bordado a lo largo; en los Sueños, la luna bordada debajo.
        val pieMes = if (v.noche) h - 13f * u else h - 3f * u
        val mes = Rect(w * 0.24f, et.bottom + 4f * u, w * 0.76f, pieMes)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(tela, a, k), 8f, vertical = true, minimo = 4.5f)
        if (v.noche) luna(Offset(w * 0.5f, h - 7f * u), minOf(w * 0.17f, 4.4f * u), tela, a, k)
        filo(ent, w - ent, cuello, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- el legajo de cartas

    /**
     * El legajo de cartas del archivo, el más bajo: cartas de pergamino dobladas y apiladas de pie, de cantos desparejos, con
     * los dobleces a la vista; la de fuera lleva al dorso la nota con el día y el mes. Las ciñe una correa ancha de badana
     * teñida del color del ánimo, con dos vueltas y el cabo colgando; abajo, por delante, cuelgan los sellos de cera de sus
     * tiras (más cartas y más sellos cuanto más escribiste). En los Sueños, una luna de plata pintada en la esquina del dorso.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireMedieval, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val capas = 3 + (v.lleno * 4f).toInt()
        val paso = 1.1f * u
        // Las cartas de dentro: sus cantos de arriba y de la derecha, cada una a su altura.
        for (j in capas - 1 downTo 1) {
            val top = (1f + 6f * azar(s, 20 + j)) * u
            val r = Rect(1f * u + j * paso * 0.6f, top, w - 1f * u - (capas - 1 - j) * paso * 0.4f, h)
            val tono = mezcla(P.PERGAMINO, P.PERG_OSC, 0.2f + 0.6f * azar(s, 30 + j))
            drawRect(a.luz(tono, k), r.topLeft, r.size)
            drawLine(a.sombra(0.3f), Offset(r.left, r.top + 0.6f * u), Offset(r.right, r.top + 0.6f * u + (azar(s, 40 + j) - 0.5f) * 1.6f * u), 0.6f * u)
            drawRect(a.sombra(0.16f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
        }
        // La carta de fuera, con sus dobleces y sus manchas.
        val fuera = Rect(1f * u, 7.5f * u + 2f * u * azar(s, 5), w - 1f * u - (capas - 1) * paso * 0.4f, h)
        drawRect(a.sombra(0.3f), fuera.topLeft + Offset(1f * u, -0.8f * u), Size(fuera.width, 1.2f * u))
        drawRect(a.etiqueta(k), fuera.topLeft, fuera.size)
        drawRect(a.claro(0.3f), fuera.topLeft, Size(fuera.width, 0.8f * u))
        bulto(fuera.left, fuera.right, fuera.top, fuera.bottom, a, luzDe, 0.6f)
        val xd = fuera.left + fuera.width * (0.32f + 0.1f * azar(s, 6))
        drawLine(a.sombra(0.25f), Offset(xd, fuera.top), Offset(xd + 0.5f * u, fuera.bottom), 0.7f * u)
        drawLine(a.claro(0.3f), Offset(xd + 0.9f * u, fuera.top), Offset(xd + 1.4f * u, fuera.bottom), 0.5f * u)
        drawOval(a.luz(P.PERG_OSC, k).copy(alpha = 0.5f), Offset(fuera.left + fuera.width * 0.5f * azar(s, 7), fuera.bottom - 14f * u), Size(fuera.width * 0.5f, 7f * u))
        // La nota del dorso: el día, grande, y el mes debajo, a pluma; en los Sueños, la luna en la esquina.
        val dia = Rect(fuera.left + 1.5f * u, fuera.top + 2f * u, fuera.right - 1.5f * u, fuera.top + 21f * u)
        textoEn(medidor, v.dia, dia, P.LETRA, a.tinta, 14.5f, peso = FontWeight.Bold)
        drawLine(P.RUBRICA.copy(alpha = if (a.oscuro) 0.55f else 0.75f), Offset(fuera.left + fuera.width * 0.22f, dia.bottom + 0.5f * u), Offset(fuera.right - fuera.width * 0.22f, dia.bottom + 0.2f * u), 0.8f * u)
        val yCorrea = h * 0.6f
        val mes = Rect(fuera.left + 1.5f * u, dia.bottom + 1.5f * u, fuera.right - 1.5f * u, yCorrea - 2f * u)
        // Si el nombre del mes no cabe a lo ancho sin achicarse de más, va abreviado como en los registros («nov.»).
        val cabe = v.mes.length * 5.6f * 0.52f * u <= mes.width
        val nombre = if (cabe || v.mes.length <= 4) v.mes else v.mes.take(3) + "."
        if (mes.height > 5f * u) textoEn(medidor, nombre, mes, P.LETRA, a.tinta.copy(alpha = 0.8f), 7.5f, minimo = 5f)
        if (v.noche) luna(Offset(fuera.right - 4.5f * u, fuera.top + 4.5f * u), 3.2f * u, a.etiqueta(k), a, k)
        // La correa de badana teñida, con dos vueltas ladeadas y el cabo que cuelga.
        val correa = v.tono?.let { a.animo(mezcla(it, P.TINTE_HONDO, if (a.oscuro) 0.05f else 0.15f), k) } ?: a.luz(P.CUERO, k)
        val g = 6.4f * u
        for (j in 0 until 2) {
            val y = yCorrea + j * (g + 1.6f * u)
            val banda = poligonoMedieval(0f, y + 1.4f * u * j, w, y - 1.2f * u + 1.4f * u * j, w, y - 1.2f * u + g + 1.4f * u * j, 0f, y + g + 1.4f * u * j)
            drawPath(Path().apply { addPath(banda, Offset(0f, 1.4f * u)) }, a.sombra(0.35f))
            drawPath(banda, correa)
            drawLine(a.claro(0.32f), Offset(0f, y + 1.4f * u * j + 0.5f * u), Offset(w * 0.7f, y - 0.4f * u + 1.4f * u * j), 0.7f * u)
        }
        val cabo = Offset(w * (0.68f + 0.12f * azar(s, 8)), yCorrea + g * 0.5f)
        drawPath(poligonoMedieval(cabo.x - 2.6f * u, cabo.y, cabo.x + 2.6f * u, cabo.y, cabo.x + 3.4f * u, cabo.y + g * 2.2f, cabo.x - 1.4f * u, cabo.y + g * 2.1f), correa)
        drawRect(a.sombra(0.2f), Offset(cabo.x + 0.6f * u, cabo.y), Size(2f * u, g * 2.1f))
        // Los sellos, colgados de sus tiras, por delante y abajo.
        val sellos = 1 + (v.lleno * 2.2f).toInt().coerceAtMost(2)
        val rs = minOf(w * 0.19f, 5.2f * u)
        for (j in 0 until sellos) {
            val cx = w * (0.26f + 0.3f * j) + 1.5f * u * azar(s, 50 + j)
            val c = Offset(cx, h - rs - 0.6f * u)
            drawLine(a.luz(P.PERG_OSC, k), Offset(cx - 0.6f * u, yCorrea + g * 1.6f), c - Offset(0f, rs * 0.6f), 2f * u)
            val cera = P.CERAS[(s + j) % P.CERAS.size]
            drawCircle(a.sombra(0.4f), rs, c + Offset(0.8f * u, 1f * u))
            drawCircle(a.luz(cera, k), rs, c)
            drawCircle(a.sombra(0.35f), rs * 0.7f, c, style = Stroke(0.6f * u))
            drawLine(a.sombra(0.4f), c - Offset(0f, rs * 0.45f), c + Offset(0f, rs * 0.45f), 0.7f * u)
            drawLine(a.sombra(0.4f), c - Offset(rs * 0.45f, 0f), c + Offset(rs * 0.45f, 0f), 0.7f * u)
            drawCircle(a.claro(0.45f), rs * 0.28f, c - Offset(rs * 0.38f, rs * 0.38f))
            if (a.oscuro && k > 0.06f) drawArc(a.filo(0.6f * k), if (luzDe < 0) 160f else -60f, 110f, false, Offset(c.x - rs, c.y - rs), Size(2 * rs, 2 * rs), style = Stroke(0.8f * u))
        }
        filo(fuera.left, fuera.right, fuera.top, h, a, k, luzDe)
    }
}
