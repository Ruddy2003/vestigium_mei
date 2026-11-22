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
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Las tres clases de entrada del estante de Romano ([MuebleEstanteRomano]), elegidas con la semilla del día, todas de lo que
 * se guardaba en el cuarto del escribiente: el legajo de hojas de madera escritas con tinta (las cartas y partes del
 * campamento), metido en su funda de cuero y atado; la arqueta de madera pintada con cantoneras de hierro y su cerradura de
 * bronce; y la bolsa de cuero cerrada con cordel y sello de plomo. En las tres, el color del ánimo es el cuerpo entero (la
 * funda, la pintura, el cuero) y lo primero que se ve; el día va en una tablilla de aliso o en una placa de hueso; en los
 * Sueños, la luna de plata a la vista. [luzDe] dice de qué lado llega la luz: -1, de la izquierda (la ventana de día); 1, de
 * la derecha (la linterna de noche, para lo que queda a su izquierda).
 */
internal object VolumenEstanteRomano {
    private val P = PaletaRomano

    /** El tamaño del día, en sp, y el alto de su tablilla, en dp. */
    private const val TAM_DIA = 12.5f
    private const val ALTO_TABLILLA = 16f

    /** El color del ánimo del cuerpo, apenas hundido para que sea materia; [crudo] si no se marcó. */
    private fun cuerpo(v: Volumen, a: AireRomano, k: Float, crudo: Color): Color =
        v.tono?.let { a.animo(mezcla(it, P.TINTE_HONDO, if (a.oscuro) 0.04f else 0.1f), k) } ?: a.luz(crudo, k)

    /** La luz de un lado y la sombra fría del otro sobre un cuerpo de [x0] a [x1]. */
    private fun DrawScope.bulto(x0: Float, x1: Float, y0: Float, y1: Float, a: AireRomano, luzDe: Float, fuerza: Float = 1f) {
        val an = x1 - x0
        if (an <= 0f || y1 <= y0) return
        val (claroX, sombraX) = if (luzDe < 0) x0 + an * 0.08f to x1 - an * 0.3f else x1 - an * 0.2f to x0
        drawRect(a.claro(0.24f * fuerza), Offset(claroX, y0), Size(an * 0.12f, y1 - y0))
        drawRect(a.frio(0.16f * fuerza), Offset(sombraX, y0), Size(an * 0.3f, y1 - y0))
        drawRect(a.frio(0.14f * fuerza), Offset(if (luzDe < 0) x1 - an * 0.12f else x0, y0), Size(an * 0.12f, y1 - y0))
    }

    /** El filo de la linterna en el canto que la mira (solo de noche). */
    private fun DrawScope.filo(x0: Float, x1: Float, y0: Float, y1: Float, a: AireRomano, k: Float, luzDe: Float) {
        if (!a.oscuro || k < 0.06f) return
        val u = density
        drawRect(a.filo(0.6f * k), Offset(x0 + 1.5f * u, y0), Size(x1 - x0 - 3f * u, 1f * u))
        drawRect(a.filo(0.45f * k), Offset(if (luzDe < 0) x0 else x1 - 1f * u, y0 + 1.5f * u), Size(1f * u, maxOf(0f, y1 - y0 - 3f * u)))
    }

    /** La luna de plata: un creciente con su sombra. */
    private fun DrawScope.luna(c: Offset, r: Float, fondo: Color, a: AireRomano, k: Float) {
        val plata = if (a.oscuro) mezcla(P.LUNA, a.hondo, 0.22f - 0.22f * k) else P.LUNA
        drawCircle(a.frio(0.35f), r, c + Offset(r * 0.18f, r * 0.22f))
        drawCircle(plata, r, c)
        drawCircle(fondo, r * 0.8f, Offset(c.x + r * 0.42f, c.y - r * 0.26f))
    }

    /** Un texto secundario (el mes) sobre [fondo]: tinta sobre lo claro, hueso sobre lo oscuro. */
    private fun tintaSobre(fondo: Color, a: AireRomano, k: Float): Color =
        if (fondo.luminance() > 0.3f) P.TINTA.copy(alpha = 0.85f) else a.etiqueta(P.HUESO, k).copy(alpha = 0.92f)

    /** La tablilla del día: aliso o hueso, con su sombra, el canto algo desigual y el número en tinta. */
    private fun DrawScope.tablilla(r: Rect, dia: String, medidor: TextMeasurer, a: AireRomano, k: Float, semilla: Int, cara: Color) {
        val u = density
        val p = poligonoRomano(r.left, r.top + 0.5f * u * azar(semilla, 61), r.right, r.top, r.right - 0.4f * u * azar(semilla, 62), r.bottom,
            r.left + 0.3f * u, r.bottom - 0.4f * u * azar(semilla, 63))
        drawPath(corridoRomano(p, 0.9f * u, 1.1f * u), a.frio(0.45f))
        drawPath(p, a.etiqueta(cara, k))
        drawRect(a.sombra(0.1f), Offset(r.right - r.width * 0.15f, r.top), Size(r.width * 0.15f, r.height))
        drawRect(a.claro(0.4f), r.topLeft, Size(r.width, 0.7f * u))
        textoEn(medidor, dia, Rect(r.left + 1f * u, r.top + 0.4f * u, r.right - 1f * u, r.bottom), P.LETRA, P.TINTA, TAM_DIA, peso = FontWeight.Bold)
    }

    // ---------------------------------------------------------------- el legajo de hojas

    /**
     * El legajo, el más alto: un mazo de hojas finas de aliso escritas con tinta, de pie, metido en una funda de cuero teñida del
     * ánimo (cruda si no se marcó) con el labio doblado arriba; asoman los cantos de las hojas (más y más desparejos cuanto más
     * escribiste); dos vueltas de cordel, la tablilla del día colgada del de arriba y el mes a lo largo. En los Sueños, la luna.
     */
    fun DrawScope.legajo(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val asoma = (7f + 4f * v.lleno) * u
        // Los cantos de las hojas que asoman, cada una a su altura.
        val hojas = maxOf(4, ((w - 4f * u) / (2.3f * u)).toInt()).coerceAtMost(24)
        val paso = (w - 4f * u) / hojas
        val clara = a.etiqueta(P.ALISO, k * 0.9f)
        val oscura = a.luz(P.ALISO_OSC, k)
        drawRect(oscura, Offset(2f * u, asoma * 0.45f), Size(w - 4f * u, asoma))
        for (j in 0 until hojas) {
            val x = 2f * u + paso * j
            val y = asoma * (0.15f + 0.55f * azar(s, 100 + j)) * (0.5f + 0.5f * v.lleno)
            drawRect(clara, Offset(x, y), Size(paso * 0.72f, asoma + 1f * u - y))
        }
        // Una o dos hojas sueltas que sobresalen, ladeadas.
        val sueltas = if (v.lleno > 0.4f) 2 else 1
        for (j in 0 until sueltas) {
            val x = w * (0.3f + 0.35f * azar(s, 120 + j))
            rotate((azar(s, 130 + j) - 0.5f) * 14f, Offset(x, asoma)) {
                drawRect(clara, Offset(x - 2f * u, 0.2f * u), Size(4f * u, asoma))
                drawLine(P.TINTA.copy(alpha = 0.35f), Offset(x - 1f * u, 2f * u), Offset(x - 1f * u, asoma - 2f * u), 0.5f * u)
            }
        }
        // La funda de cuero, un poco abombada, con el labio doblado.
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.CANAMO, 0.2f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.3f)
        val top = asoma
        val funda = Path().apply {
            moveTo(1f * u, top)
            lineTo(w - 1f * u, top)
            quadraticTo(w, (top + h) / 2, w - 1f * u, h)
            lineTo(1f * u, h)
            quadraticTo(0f, (top + h) / 2, 1f * u, top)
            close()
        }
        drawPath(funda, cuero)
        drawRect(mezcla(cuero, P.HUESO, 0.14f), Offset(1f * u, top), Size(w - 2f * u, 3.2f * u))
        drawRect(hondo, Offset(1f * u, top + 3.2f * u), Size(w - 2f * u, 0.8f * u))
        bulto(1f * u, w - 1f * u, top, h, a, luzDe)
        // Las arrugas del cuero y el desgaste.
        for (j in 0 until 3) {
            val y = top + (h - top) * (0.3f + 0.25f * j + 0.08f * azar(s, 140 + j))
            drawLine(hondo.copy(alpha = 0.5f), Offset(w * 0.1f, y), Offset(w * (0.35f + 0.3f * azar(s, 150 + j)), y + 1.6f * u), 0.6f * u)
        }
        drawOval(a.claro(0.12f), Offset(w * 0.2f, h * (0.62f + 0.15f * azar(s, 5))), Size(w * 0.45f, h * 0.06f))
        // Los dos cordeles.
        val c1 = top + (h - top) * 0.12f + 2f * u
        val c2 = h - (h - top) * 0.1f
        val cordel = a.luz(P.CANAMO, k)
        for (y in listOf(c1, c2)) {
            drawLine(a.frio(0.4f), Offset(0f, y + 1f * u), Offset(w, y + 1f * u), 1.1f * u)
            drawLine(cordel, Offset(0f, y), Offset(w, y - 0.6f * u), 1.3f * u)
        }
        // La tablilla del día, colgada del cordel de arriba.
        val an = minOf(w - 5f * u, 24f * u)
        val tx = w * (0.48f + 0.06f * (azar(s, 7) - 0.5f))
        val et = Rect(tx - an / 2, c1 + 3f * u, tx + an / 2, c1 + 3f * u + ALTO_TABLILLA * u)
        drawLine(cordel, Offset(tx, c1), Offset(tx, et.top), 0.8f * u)
        tablilla(et, v.dia, medidor, a, k, s, P.ALISO)
        // El mes a lo largo y, en los Sueños, la luna sobre el cordel de abajo.
        val lunaR = minOf(w * 0.17f, 4.6f * u)
        val finMes = if (v.noche) c2 - 3f * u - 2.6f * lunaR else c2 - 3f * u
        val mes = Rect(w * 0.2f, et.bottom + 3f * u, w * 0.8f, finMes)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(cuero, a, k), 8f, vertical = true, minimo = 4.5f)
        if (v.noche) luna(Offset(w * 0.5f, c2 - 2.5f * u - lunaR), lunaR, cuero, a, k)
        filo(1f * u, w - 1f * u, top, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la arqueta

    /**
     * La arqueta, la más baja: caja de tablas a lo ancho pintada del ánimo (cruda, si no se marcó), con la tapa gruesa que vuela,
     * cantoneras y una faja de hierro, la cerradura de bronce con su ojo en escuadra, la placa de hueso del día clavada al
     * frente y el mes pintado debajo. Cuanto más escribiste, más hojas asoman bajo la tapa entreabierta. En los Sueños, la luna
     * en el canto de la tapa.
     */
    fun DrawScope.arqueta(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val pintura = cuerpo(v, a, k, mezcla(P.ROBLE_LUZ, P.CANAMO, 0.3f))
        val hondo = mezcla(pintura, a.hondo, 0.32f)
        val madera = a.luz(P.ROBLE, k)
        val altoTapa = minOf(12f * u, h * 0.18f)
        val entre = 1.5f * u + 3.5f * u * v.lleno
        val cuerpoR = Rect(1.2f * u, altoTapa + entre, w - 1.2f * u, h)
        // Lo que asoma entre la caja y la tapa: cantos de hojas.
        if (entre > 2f * u) {
            drawRect(a.luz(P.ALISO_OSC, k), Offset(3f * u, altoTapa - 1f * u), Size(w - 6f * u, entre + 2f * u))
            var x = 3.5f * u; var j = 0
            while (x < w - 4f * u && j < 30) {
                drawRect(a.etiqueta(P.ALISO, k * 0.9f), Offset(x, altoTapa - 1f * u + entre * 0.4f * azar(s, 160 + j)), Size(1.4f * u, entre + 2f * u))
                x += 2.4f * u; j++
            }
        }
        // La caja.
        drawRect(pintura, cuerpoR.topLeft, cuerpoR.size)
        // Las tablas de la caja, a lo ancho, con sus juntas y su veta.
        for (j in 1 until 4) {
            val y = cuerpoR.top + cuerpoR.height * (j / 4f) + (azar(s, 170 + j) - 0.5f) * 2f * u
            drawLine(hondo.copy(alpha = 0.55f), Offset(cuerpoR.left, y), Offset(cuerpoR.right, y), 0.7f * u)
            drawLine(a.claro(0.18f), Offset(cuerpoR.left, y + 0.8f * u), Offset(cuerpoR.right, y + 0.8f * u), 0.5f * u)
            drawLine(hondo.copy(alpha = 0.2f), Offset(cuerpoR.left + cuerpoR.width * 0.2f, y + cuerpoR.height * 0.1f), Offset(cuerpoR.right - cuerpoR.width * 0.3f, y + cuerpoR.height * 0.11f), 0.5f * u)
        }
        // El zócalo de la caja, más oscuro, y sus dos patas.
        drawRect(hondo, Offset(cuerpoR.left, cuerpoR.bottom - 3.4f * u), Size(cuerpoR.width, 3.4f * u))
        // El desgaste de la pintura en los cantos: asoma la madera.
        drawPath(poligonoRomano(cuerpoR.left, cuerpoR.bottom - 6f * u, cuerpoR.left + 4f * u, cuerpoR.bottom, cuerpoR.left, cuerpoR.bottom), madera)
        drawPath(poligonoRomano(cuerpoR.right, cuerpoR.top + 9f * u, cuerpoR.right - 2.4f * u, cuerpoR.top + 13f * u, cuerpoR.right, cuerpoR.top + 16f * u), madera)
        bulto(cuerpoR.left, cuerpoR.right, cuerpoR.top, cuerpoR.bottom, a, luzDe)
        drawRect(a.sombra(0.3f), Offset(cuerpoR.left, cuerpoR.top), Size(cuerpoR.width, 2.4f * u))
        // La tapa: una tabla gruesa que vuela sobre la caja, con su cara de encima a la luz y el canto con su moldura.
        val encima = altoTapa * 0.32f
        drawRect(mezcla(pintura, P.HUESO, 0.16f), Offset(0f, 0f), Size(w, encima))
        drawRect(pintura, Offset(0f, encima), Size(w, altoTapa - encima))
        drawRect(hondo, Offset(0f, altoTapa - 2f * u), Size(w, 2f * u))
        drawLine(hondo.copy(alpha = 0.6f), Offset(0f, encima + (altoTapa - encima) * 0.45f), Offset(w, encima + (altoTapa - encima) * 0.45f), 0.6f * u)
        drawRect(a.claro(0.35f), Offset(0f, encima), Size(w, 0.8f * u))
        bulto(0f, w, encima, altoTapa, a, luzDe, 0.7f)
        // Las cantoneras de hierro, con sus clavos.
        val hierro = a.luz(P.HIERRO, k)
        val c = minOf(6f * u, cuerpoR.width * 0.25f)
        for ((x, sx) in listOf(cuerpoR.left to 1f, cuerpoR.right to -1f)) for ((y, sy) in listOf(cuerpoR.top to 1f, cuerpoR.bottom to -1f)) {
            drawPath(poligonoRomano(x, y, x + sx * c, y, x + sx * c * 0.4f, y + sy * c * 0.4f, x, y + sy * c), hierro)
            drawCircle(a.claro(0.4f), 0.6f * u, Offset(x + sx * c * 0.3f, y + sy * c * 0.3f))
        }
        // La cerradura de bronce con el ojo en escuadra.
        val cx = w / 2
        val cerr = Rect(cx - minOf(5f * u, w * 0.2f), cuerpoR.top + 1.5f * u, cx + minOf(5f * u, w * 0.2f), cuerpoR.top + 10f * u)
        drawRect(a.frio(0.35f), Offset(cerr.left + 0.8f * u, cerr.top + 0.8f * u), cerr.size)
        drawRect(a.luz(P.BRONCE, k), cerr.topLeft, cerr.size)
        drawRect(a.luz(P.BRONCE_LUZ, k), cerr.topLeft, Size(cerr.width, 0.8f * u))
        drawLine(P.TINTA, Offset(cx, cerr.top + 2.4f * u), Offset(cx, cerr.bottom - 2.4f * u), 1.2f * u)
        drawLine(P.TINTA, Offset(cx, cerr.bottom - 2.4f * u), Offset(cx + 2f * u, cerr.bottom - 2.4f * u), 1.2f * u)
        // La placa de hueso del día, clavada.
        val an = minOf(w - 7f * u, 24f * u)
        val et = Rect(cx - an / 2, cerr.bottom + 3f * u, cx + an / 2, cerr.bottom + 3f * u + ALTO_TABLILLA * u)
        tablilla(et, v.dia, medidor, a, k, s, P.HUESO)
        clavoRomano(Offset(et.left + 1.6f * u, et.top + 1.6f * u), 0.7f * u, a, k)
        clavoRomano(Offset(et.right - 1.8f * u, et.bottom - 1.6f * u), 0.7f * u, a, k)
        // La faja de hierro a lo ancho bajo la placa, el mes pintado debajo (abreviado, como en las fechas romanas) y la luna
        // en el canto de la tapa en los Sueños.
        val faja = et.bottom + 3f * u
        if (faja + 3f * u < cuerpoR.bottom - 4f * u) {
            drawRect(a.frio(0.35f), Offset(cuerpoR.left, faja + 2.2f * u), Size(cuerpoR.width, 0.8f * u))
            drawRect(hierro, Offset(cuerpoR.left, faja), Size(cuerpoR.width, 2.2f * u))
            for (x in listOf(cuerpoR.left + 3f * u, cx, cuerpoR.right - 3f * u)) drawCircle(a.claro(0.4f), 0.6f * u, Offset(x, faja + 1.1f * u))
        }
        val corto = if (v.mes.length > 5) v.mes.take(3) + "." else v.mes
        val mes = Rect(cuerpoR.left + 2f * u, faja + 4f * u, cuerpoR.right - 2f * u, cuerpoR.bottom - 4.5f * u)
        if (mes.height > 6f * u) textoEn(medidor, corto, mes, P.LETRA, tintaSobre(pintura, a, k), 8f, peso = FontWeight.SemiBold, minimo = 4.5f)
        if (v.noche) luna(Offset(cx, encima + (altoTapa - encima) * 0.5f), minOf((altoTapa - encima) * 0.42f, w * 0.15f), pintura, a, k)
        filo(0f, w, 0f, h, a, k, luzDe)
    }

    // ---------------------------------------------------------------- la bolsa de cuero

    /**
     * La bolsa de cuero teñido del ánimo (cruda, si no se marcó), blanda: el cuello fruncido arriba con su volante, atado con
     * dos vueltas de cordel; el vientre que se llena con lo escrito (vacía, se vence y hace más pliegues); de un cabo cuelga el
     * sello de plomo (en los Sueños, la luna) y del otro la tablilla del día; el mes a lo largo del vientre.
     */
    fun DrawScope.bolsa(v: Volumen, medidor: TextMeasurer, a: AireRomano, k: Float, luzDe: Float) {
        val u = density
        val w = size.width; val h = size.height
        val s = v.semilla and 0x7fffffff
        val cuero = cuerpo(v, a, k, mezcla(P.CUERO, P.ROBLE_LUZ, 0.3f * azar(s, 3)))
        val hondo = mezcla(cuero, a.hondo, 0.34f)
        val cuello = h * 0.24f
        val panza = 0.86f + 0.14f * v.lleno
        val anCuello = w * 0.42f
        val cx = w / 2 + (azar(s, 4) - 0.5f) * 2f * u
        // El volante de arriba, fruncido.
        val volante = Path().apply {
            moveTo(cx - anCuello * 0.45f, cuello)
            lineTo(cx - anCuello * 0.75f, cuello * 0.25f)
            lineTo(cx - anCuello * 0.35f, cuello * 0.45f)
            lineTo(cx - anCuello * 0.05f, 0.5f * u)
            lineTo(cx + anCuello * 0.2f, cuello * 0.4f)
            lineTo(cx + anCuello * 0.7f, cuello * 0.15f)
            lineTo(cx + anCuello * 0.45f, cuello)
            close()
        }
        drawPath(volante, mezcla(cuero, hondo, 0.25f))
        drawLine(a.claro(0.3f), Offset(cx - anCuello * 0.7f, cuello * 0.3f), Offset(cx - anCuello * 0.4f, cuello * 0.9f), 0.8f * u)
        drawLine(hondo, Offset(cx - anCuello * 0.05f, 1.5f * u), Offset(cx - anCuello * 0.1f, cuello), 0.6f * u)
        // El vientre.
        val ancho = (w - 2f * u) * panza
        val izq = w / 2 - ancho / 2; val der = w / 2 + ancho / 2
        val vientre = Path().apply {
            moveTo(cx - anCuello / 2, cuello)
            cubicTo(izq - 1f * u, cuello + (h - cuello) * 0.25f, izq, h - 1f * u, w * 0.3f, h)
            lineTo(w * 0.7f, h)
            cubicTo(der, h - 1f * u, der + 1f * u, cuello + (h - cuello) * 0.25f, cx + anCuello / 2, cuello)
            close()
        }
        drawPath(vientre, cuero)
        bulto(izq, der, cuello + 3f * u, h, a, luzDe)
        drawOval(a.claro(0.16f), Offset(w * 0.12f, cuello + (h - cuello) * 0.25f), Size(w * 0.25f, (h - cuello) * 0.5f))
        // Los pliegues: más cuanto más vacía.
        val pliegues = if (v.lleno > 0.5f) 2 else 4
        for (j in 0 until pliegues) {
            val x0 = cx + (j - (pliegues - 1) / 2f) * anCuello * 0.3f
            val x1 = w * (0.15f + 0.7f * (j + 0.5f) / pliegues) + (azar(s, 180 + j) - 0.5f) * 3f * u
            val p = Path().apply {
                moveTo(x0, cuello + 1f * u)
                quadraticTo((x0 + x1) / 2 + (azar(s, 190 + j) - 0.5f) * 4f * u, cuello + (h - cuello) * 0.4f, x1, cuello + (h - cuello) * (0.55f + 0.25f * azar(s, 200 + j)))
            }
            drawPath(p, hondo.copy(alpha = 0.55f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            drawPath(corridoRomano(p, -1f * u, 0f), a.claro(0.18f), style = Stroke(0.6f * u, cap = StrokeCap.Round))
        }
        drawRect(a.sombra(0.25f), Offset(w * 0.22f, h - 1.5f * u), Size(w * 0.56f, 1.5f * u))
        // El cordel del cuello, con su nudo y sus dos cabos.
        val cordel = a.luz(P.CANAMO, k)
        for (d in listOf(-1.3f, 1.3f)) drawLine(cordel, Offset(cx - anCuello * 0.55f, cuello + d * u), Offset(cx + anCuello * 0.55f, cuello + d * u - 0.4f * u), 1.3f * u, cap = StrokeCap.Round)
        drawCircle(cordel, 1.8f * u, Offset(cx + anCuello * 0.42f, cuello))
        val cabo = Offset(cx + anCuello * 0.42f, cuello)
        // El sello de plomo (o la luna) colgado de un cabo.
        val selloR = minOf(w * 0.15f, 4.4f * u)
        val sello = Offset(minOf(w - selloR - 1f * u, cabo.x + 3f * u), cuello + 6f * u + selloR)
        drawLine(cordel, cabo, sello, 0.7f * u)
        if (v.noche) {
            drawCircle(a.luz(mezcla(P.PLOMO, P.TINTA, 0.3f), k), selloR * 1.15f, sello)
            luna(sello, selloR * 0.85f, a.luz(mezcla(P.PLOMO, P.TINTA, 0.3f), k), a, k)
        } else {
            drawCircle(a.frio(0.35f), selloR, sello + Offset(0.6f * u, 0.8f * u))
            drawCircle(a.luz(P.PLOMO, k), selloR, sello)
            drawCircle(a.claro(0.35f), selloR * 0.35f, sello - Offset(selloR * 0.3f, selloR * 0.3f))
            drawCircle(a.luz(mezcla(P.PLOMO, P.TINTA, 0.4f), k), selloR * 0.55f, sello, style = Stroke(0.5f * u))
        }
        // La tablilla del día, colgada del otro cabo, sobre el vientre.
        val an = minOf(w - 5f * u, 24f * u)
        val ty = sello.y + selloR + 2f * u
        val et = Rect(w / 2 - an / 2 - 1.5f * u, ty, w / 2 + an / 2 - 1.5f * u, ty + ALTO_TABLILLA * u)
        drawLine(cordel, Offset(cx - anCuello * 0.3f, cuello + 1f * u), Offset(et.left + 3f * u, et.top), 0.7f * u)
        tablilla(et, v.dia, medidor, a, k, s, P.ALISO)
        // El mes a lo largo del vientre.
        val mes = Rect(w * 0.24f, et.bottom + 3f * u, w * 0.76f, h - 4f * u)
        if (mes.height > 10f * u) textoEn(medidor, v.mes, mes, P.LETRA, tintaSobre(cuero, a, k), 8f, vertical = true, minimo = 4.5f)
        filo(izq, der, cuello + 3f * u, h, a, k, luzDe)
    }
}
