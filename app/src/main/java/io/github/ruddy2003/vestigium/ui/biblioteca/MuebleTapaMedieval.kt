package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Dónde va cada parte del libro del día de Scriptorium, en píxeles: la franja de muro, la mesa, el libro (lomo y tapa), lo
 * grueso del libro que se ve por abajo y por la derecha (más cuanto más escribiste), la cédula donde la pantalla pone el
 * extracto y la lucerna.
 */
internal class SitioTapaMedieval(val w: Float, val h: Float, u: Float, palabras: Int) {
    /** Donde acaba el muro y empieza la mesa (su borde del fondo), y donde empieza el canto de delante. */
    val muro = h * MuebleTapaMedieval.MURO
    val frente = h * MuebleTapaMedieval.FRENTE
    /** El libro entero (lomo y tapa) y sus dos partes. */
    val libro = Rect(w * MuebleTapaMedieval.X0, h * MuebleTapaMedieval.Y0, w * MuebleTapaMedieval.X1, h * MuebleTapaMedieval.Y1)
    val lomo = Rect(libro.left, libro.top, libro.left + (w * 0.06f).coerceIn(17f * u, 25f * u), libro.bottom)
    val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
    /** El grosor de una tabla y el del bloque de las hojas: de 2.5 dp en blanco a 9.5 con miles de palabras. */
    val tabla = 1.7f * u
    val grueso = (2.5f + 7f * sqrt(palabras.coerceIn(0, 2000) / 2000f)) * u
    /** Hacia dónde se ve lo grueso del libro: abajo y un poco a la derecha (el libro, tumbado, se ve desde arriba y de frente). */
    val fondo = Offset((grueso + 2 * tabla) * 0.5f, grueso + 2 * tabla)
    /** La cédula suelta sobre la tapa: un poco más que la caja del extracto. */
    val cedula = MuebleTapaMedieval.CAJA.let { k -> Rect(w * (k.left - 0.018f), h * (k.top - 0.03f), w * (k.right + 0.018f), h * (k.bottom + 0.035f)) }
    /** El compartimento del título (la fecha dorada), entre el filete de arriba y la cédula. */
    val titulo = Rect(tapa.left + 19f * u, h * 0.18f, tapa.right - 19f * u, cedula.top - 4f * u)
    /** La lucerna, a la izquierda del libro: dónde pisa y dónde arde. */
    val baseLucerna = Offset(libro.left * 0.6f, h * 0.47f)
    val llama = Offset(baseLucerna.x - 16.5f * u, baseLucerna.y - 20f * u)
}

/**
 * El libro del día de Scriptorium (lo usa `EstanteMedieval`): el códice de hoy, cerrado, sobre la mesa larga del scriptorium,
 * contra el muro de sillarejo de la misma celda que el armario de la biblioteca. Es un códice románico de tablas cubiertas de
 * badana teñida de carmesí, siempre la misma (el de los sueños, de índigo): el color no cambia con el día, porque en la app un
 * color que cambia es el ánimo. Lleva su gofrado en seco a mano (filetes y florones de hierro suelto) que la luz rasante
 * levanta, cinco bollones de latón, las dos correas con su manecilla y el lomo de nervios con la cuenta de palabras dorada. La
 * fecha va dorada en el compartimento del título; lo escrito, en una cédula de pergamino suelta que alguien dejó encima
 * (pautada a punta seca; escrita, con su punto de rúbrica). Lo grueso del libro (las tablas y el bloque de las hojas) se ve
 * abajo y a la derecha y engorda con las palabras; escrito, le cuelgan los registros. A la izquierda, la lucerna de barro; a
 * la derecha, el compás de puntas con que se pica la pauta.
 *
 * La luz: de día, el rayo de la ventana (que queda fuera del cuadro, arriba a la izquierda, como en la biblioteca) cruza el
 * muro y la mesa con su borde blando y entibia lo que toca; todo echa su sombra violeta a la derecha. De noche (modo oscuro)
 * arde la lucerna: lo cercano se enciende y se apaga con la distancia, y a la derecha queda un resto frío de la ventana; la
 * cédula y la fecha se siguen leyendo. En los Sueños entra además la luna por la misma ventana (fría, contra la llama tibia) y
 * el bollón de abajo es un creciente de oro.
 */
internal object MuebleTapaMedieval {
    private val P = PaletaMedieval

    /** La franja de muro y el canto de la mesa, en fracción del alto. */
    const val MURO = 0.115f
    const val FRENTE = 0.93f
    /** El libro, en fracciones del ancho (a lo ancho) y del alto (a lo alto). */
    const val X0 = 0.16f
    const val X1 = 0.88f
    const val Y0 = 0.13f
    const val Y1 = 0.815f
    /** Donde la pantalla pone el extracto: en la cédula, centrada en la tapa (el mismo tamaño que antes). */
    val CAJA = Rect(0.29f, 0.35f, 0.81f, 0.7f)

    /** Cuánta luz de la lucerna le llega a [p] (1 junto a la llama, 0 lejos); de día, toda. */
    fun cerca(p: Offset, s: SitioTapaMedieval, a: AireMedieval): Float {
        if (!a.lumbre) return 1f
        val dx = (p.x - s.llama.x) / s.w; val dy = (p.y - s.llama.y) / s.w
        return (1f - sqrt(dx * dx + dy * dy * 1.4f) / 0.95f).coerceIn(0f, 1f).pow(1.1f)
    }

    fun DrawScope.tapa(e: EstadoTapa, medidor: TextMeasurer, oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireMedieval(oscuro, e.noche)
        val s = SitioTapaMedieval(w, h, u, if (e.vacio) 0 else e.palabras)
        val ab = e.abierto.coerceIn(0f, 1f)
        clipRect(0f, 0f, w, h) {
            muro(s, a)
            mesa(s, a, e.semilla)
            lucernaSombra(s, a)
            with(LibroTapaMedieval) { libro(s, a, e, medidor, ab) }
            if (!e.noche && !oscuro) rayo(s, mezcla(P.SOL, P.LLAMA, 0.25f), 0.6f)
            if (e.noche) rayo(s, Color(0xFFCFDDF6), if (oscuro) 0.2f else 0.5f)
            lucerna(s, a)
            compas(s, a, e.semilla)
            if (a.lumbre) llamaLucerna(s, a)
        }
    }

    // ---------------------------------------------------------------- el muro y la mesa

    /**
     * Pinta [r] con [color] tal como lo deja la luz del momento: de día, liso; de noche, la luz de la llama que se apaga con la
     * distancia hacia la derecha (el mismo relleno es el degradado, a lo ancho: la mesa es una franja) y, junto a la lucerna, su
     * charco, más vivo.
     */
    private fun DrawScope.alumbrado(r: Rect, color: Color, s: SitioTapaMedieval, a: AireMedieval) {
        if (!a.lumbre) { drawRect(color, r.topLeft, r.size); return }
        // Lo lejano, liso; la luz, un degradado de dos colores solo donde llega (pintar degradados grandes es lo que más pesa).
        val lejos = lejano(a.luz(color, 0.04f), a)
        val fin = minOf(r.right, s.llama.x + s.w * (if (a.oscuro) 0.62f else 0.9f))
        drawRect(lejos, r.topLeft, r.size)
        drawRect(Brush.horizontalGradient(listOf(a.luz(color, 0.85f), lejos), startX = s.llama.x, endX = fin), r.topLeft, Size(fin - r.left, r.height))
        mancha(s.llama + Offset(2f * density, s.h * 0.13f), s.w * 0.15f, mezcla(a.luz(color, 1f), P.FUEGO, 0.25f).copy(alpha = 0.75f))
    }

    /**
     * La franja de muro: el sillarejo de caras planas con su mortero, de las mismas piedras que la celda del armario (de largos
     * y altos desiguales, con ripios calzados en las juntas), y la sombra de la mesa arrimada.
     */
    private fun DrawScope.muro(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density
        drawRect(a.luz(P.MORTERO, if (a.lumbre) 0.35f else 1f), Offset.Zero, Size(s.w, s.muro))
        var y = -s.muro * 0.3f; var f = 0
        while (y < s.muro) {
            val alto = s.muro * (0.5f + 0.18f * azar(2100, f))
            var x = -s.w * 0.05f * azar(2101, f); var j = 0
            while (x < s.w) {
                val largo = s.w * (0.04f + 0.09f * azar(2102 + f, j)) + 5f * u
                val arriba = y + (1f + 2.6f * azar(2103 + f, j)) * u
                val abajo = y + alto - (1f + 2.2f * azar(2104 + f, j)) * u
                piedra(Rect(x + (1f + 1.6f * azar(2105 + f, j)) * u, arriba, x + largo - 1.2f * u, abajo), 2110 + 31 * f + j, s, a)
                if (azar(2106 + f, j) > 0.72f) piedra(Rect(x + largo * 0.3f, y - 0.5f * u, x + largo * 0.3f + 6f * u, arriba - 0.6f * u), 2190 + 31 * f + j, s, a)
                x += largo; j++
            }
            y += alto; f++
        }
        // De día, el muro se enfría hacia la derecha, lejos de la ventana.
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.frio(0.22f)), s.w * 0.3f, s.w), Offset(s.w * 0.3f, 0f), Size(s.w * 0.7f, s.muro))
        drawRect(a.sombra(0.34f), Offset(0f, s.muro - 2.6f * u), Size(s.w, 2.6f * u))
    }

    /** Una piedra de sillarejo de cara plana: la sombra en el mortero, la cara con su faceta y el filo de luz de arriba. */
    private fun DrawScope.piedra(r: Rect, sem: Int, s: SitioTapaMedieval, a: AireMedieval) {
        if (r.width < 2f || r.height < 2f) return
        val u = density
        val m = minOf(r.width, r.height)
        fun c(i: Int) = m * (0.06f + 0.22f * azar(sem, i))
        val p = poligonoMedieval(r.left + c(1), r.top, r.right - c(2), r.top + 0.6f * u * azar(sem, 3), r.right, r.top + c(2), r.right - 0.6f * u, r.bottom - c(4),
            r.right - c(4), r.bottom, r.left + c(5), r.bottom - 0.5f * u, r.left, r.bottom - c(5), r.left + 0.5f * u, r.top + c(1))
        val sorteo = azar(sem, 6)
        val otra = when { sorteo > 0.72f -> P.PIEDRA_CLARA; sorteo > 0.45f -> P.OCRE; sorteo > 0.2f -> P.GRIS; else -> P.PIEDRA_OSC }
        val tono = mezcla(P.PIEDRA, otra, 0.35f + 0.5f * azar(sem, 7))
        val k = cerca(r.center, s, a)
        drawPath(corridoMedieval(p, 1.2f * u, 1.4f * u), a.frio(0.32f))
        drawPath(p, lejano(a.luz(tono, k), a, 1f - k))
        if (r.width > 14f * u) drawPath(poligonoMedieval(r.left + c(1), r.top, r.right - c(2), r.top, r.left + r.width * 0.55f, r.bottom - c(4)), if (azar(sem, 8) > 0.5f) a.claro(0.1f) else a.frio(0.09f))
        drawLine(if (a.oscuro) a.filo(0.4f * k) else a.claro(0.42f), Offset(r.left + c(1), r.top + 0.4f * u), Offset(r.right - c(2), r.top + 0.4f * u), 0.8f * u)
    }

    /**
     * La mesa larga de roble: tres tablas a lo largo con su veta y sus juntas, el roce claro donde se apoyan los brazos, una
     * gota de tinta vieja y la quemadura de la lucerna; delante, el canto con su arista de luz.
     */
    private fun DrawScope.mesa(s: SitioTapaMedieval, a: AireMedieval, sem: Int) {
        val u = density
        val cara = Rect(0f, s.muro, s.w, s.frente)
        alumbrado(cara, P.ROBLE, s, a)
        vetas(cara, a.luz(P.NOGAL_OSC, 0.6f).copy(alpha = 0.34f), 2150, densidad = 0.2f, grosor = 0.8f, ondulacion = 0.6f)
        pinceladas(cara, a.claro(1f), 2160, 9, u, 0.07f)
        pinceladas(cara, a.sombra(1f), 2161, 7, u, 0.06f)
        for (f in listOf(0.36f, 0.73f)) {
            val y = cara.top + cara.height * f
            drawLine(a.sombra(0.36f), Offset(0f, y), Offset(s.w, y), 1.1f * u)
            drawLine(a.claro(0.18f), Offset(0f, y + 1.2f * u), Offset(s.w, y + 1.2f * u), 0.7f * u)
        }
        drawOval(a.claro(0.1f), Offset(s.w * 0.3f, s.frente - s.h * 0.1f), Size(s.w * 0.45f, s.h * 0.08f))
        drawOval(a.luz(P.TINTA, 0.5f).copy(alpha = 0.55f), Offset(s.w * (0.925f + 0.02f * azar(sem, 1)), s.h * 0.87f), Size(4.6f * u, 2.6f * u))
        drawCircle(a.luz(P.TINTA, 0.5f).copy(alpha = 0.4f), 0.9f * u, Offset(s.w * 0.918f, s.h * 0.85f))
        drawOval(a.luz(P.NOGAL_OSC, 0.6f).copy(alpha = 0.3f), Offset(s.baseLucerna.x - 20f * u, s.baseLucerna.y - 3.5f * u), Size(40f * u, 11f * u))
        // El canto de delante: la arista de luz (de noche, el filo de la lucerna hacia su lado) y la cara en sombra.
        val canto = Rect(0f, s.frente, s.w, s.h)
        drawRect(a.luz(mezcla(P.ROBLE, P.NOGAL, 0.45f), if (a.lumbre) 0.25f else 1f), canto.topLeft, canto.size)
        drawRect(a.frio(0.22f), Offset(0f, canto.top + canto.height * 0.5f), Size(s.w, canto.height * 0.5f))
        drawLine(a.claro(0.5f), Offset(0f, canto.top + 0.6f * u), Offset(s.w, canto.top + 0.6f * u), 1.1f * u)
        if (a.oscuro) drawLine(a.filo(0.4f), Offset(0f, canto.top + 0.6f * u), Offset(s.w * 0.45f, canto.top + 0.6f * u), 1.1f * u)
    }

    // ---------------------------------------------------------------- la luz

    /**
     * El rayo de la ventana, que baja de arriba a la izquierda por el muro y cruza la mesa y el lomo: un solo degradado a lo
     * ancho de la franja, con los bordes blandos, que entibia lo que toca. El sol de día; la luna en los Sueños.
     */
    private fun DrawScope.rayo(s: SitioTapaMedieval, color: Color, fuerza: Float) {
        val w = s.w; val h = s.h
        val x0 = w * 0.05f; val x1 = w * 0.33f
        // La dirección del rayo y su perpendicular, para que el degradado vaya a lo ancho de la franja.
        val d = Offset(x1 - x0, h); val largo = d.getDistance()
        val n = Offset(d.y / largo, -d.x / largo)
        val medio = Offset((x0 + x1) / 2, h / 2)
        val ancho = w * 0.14f
        val brocha = Brush.linearGradient(0f to Color.Transparent, 0.5f to color.copy(alpha = fuerza), 1f to Color.Transparent, start = medio - n * ancho, end = medio + n * ancho)
        val lado = ancho * 1.25f
        drawPath(poligonoMedieval(x0 - lado, 0f, x0 + lado, 0f, x1 + lado, h, x1 - lado, h), brocha)
    }

    /** El resto frío de la ventana. */
    private val RESTO_FRIO = mezcla(P.CIELO_NOCHE, P.LUNA, 0.3f)

    /**
     * De noche, lo que la lucerna no alcanza no se va a un pardo parejo: le queda un resto frío de la ventana. Va en el color de
     * lo lejano (debajo de la luz de la llama), sin otro degradado encima.
     */
    fun lejano(c: Color, a: AireMedieval, cuanto: Float = 1f): Color = if (a.oscuro) mezcla(c, RESTO_FRIO, 0.3f * cuanto) else c

    // ---------------------------------------------------------------- la lucerna

    /** La sombra de la lucerna en la mesa: de día, la que echa el rayo, larga hacia la derecha; de noche, solo la de contacto. */
    private fun DrawScope.lucernaSombra(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density * 1.5f
        val b = s.baseLucerna
        contactoMedieval(Offset(b.x + 2f * u, b.y + 0.4f * u), 22f * u, 4.6f * u, a, 0.4f)
        if (!a.lumbre) drawPath(poligonoMedieval(b.x - 7f * u, b.y - 1f * u, b.x + 8f * u, b.y - 2f * u, b.x + 22f * u, b.y + 4f * u, b.x + 6f * u, b.y + 5f * u), a.sombra(0.2f))
    }

    /**
     * La lucerna de barro vidriado: el cuerpo con su asa de anillo, la piquera hacia la izquierda con la mecha (tiznada de día)
     * y el barro crudo de abajo; la luz de la ventana, o la de su propia llama, en el lado que la mira.
     */
    private fun DrawScope.lucerna(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density * 1.5f
        val b = s.baseLucerna
        val k = cerca(b, s, a)
        val barro = a.luz(P.BARRO, k)
        val vidriado = a.luz(mezcla(P.BARRO, P.VERDE, 0.6f), k)
        val l = s.llama
        drawOval(mezcla(barro, a.hondo, 0.25f), Offset(b.x - 6f * u, b.y - 3f * u), Size(12f * u, 4f * u))
        val cuerpo = Path().apply {
            moveTo(b.x - 9f * u, b.y - 4f * u)
            cubicTo(b.x - 10f * u, b.y - 12f * u, b.x + 9f * u, b.y - 13f * u, b.x + 9f * u, b.y - 5f * u)
            cubicTo(b.x + 7f * u, b.y - 1.5f * u, b.x - 7f * u, b.y - 1.5f * u, b.x - 9f * u, b.y - 4f * u)
            close()
        }
        val piquera = Path().apply {
            moveTo(b.x - 6f * u, b.y - 9.5f * u)
            lineTo(l.x - 1.2f * u, l.y + 0.4f * u)
            quadraticTo(l.x - 1.8f * u, l.y + 2.2f * u, l.x + 0.6f * u, l.y + 2.2f * u)
            lineTo(b.x - 3f * u, b.y - 5.5f * u)
            close()
        }
        drawPath(piquera, vidriado)
        drawPath(cuerpo, vidriado)
        drawPath(Path().apply {
            moveTo(b.x - 9f * u, b.y - 4f * u)
            cubicTo(b.x - 7f * u, b.y - 1.5f * u, b.x + 7f * u, b.y - 1.5f * u, b.x + 9f * u, b.y - 5f * u)
            lineTo(b.x + 8.6f * u, b.y - 7f * u)
            cubicTo(b.x + 4f * u, b.y - 4.5f * u, b.x - 5f * u, b.y - 4.5f * u, b.x - 9.4f * u, b.y - 6f * u)
            close()
        }, barro)
        // La luz en el lado que la mira (de día, la ventana; de noche, su llama) y la sombra fría del otro.
        drawArc(if (a.oscuro) a.filo(0.7f) else a.claro(0.6f), 195f, 80f, false, Offset(b.x - 8.4f * u, b.y - 12f * u), Size(14f * u, 9f * u), style = Stroke(1.1f * u))
        drawOval(a.frio(0.26f), Offset(b.x + 2f * u, b.y - 10.5f * u), Size(7f * u, 7f * u))
        drawOval(a.luz(P.NOGAL_OSC, k), Offset(b.x - 2.6f * u, b.y - 11.4f * u), Size(5.2f * u, 2.2f * u))
        drawArc(vidriado, 200f, 250f, false, Offset(b.x + 7f * u, b.y - 12f * u), Size(6.4f * u, 6.4f * u), style = Stroke(1.6f * u))
        drawLine(a.luz(P.NEGRO, k), Offset(l.x + 0.4f * u, l.y + 1.6f * u), Offset(l.x, l.y), 0.9f * u, StrokeCap.Round)
        if (!a.lumbre) drawCircle(P.NEGRO.copy(alpha = 0.3f), 1.8f * u, Offset(l.x - 0.4f * u, l.y + 1.6f * u))
    }

    /** La llama de la lucerna, encima de todo, con su propio color. */
    private fun DrawScope.llamaLucerna(s: SitioTapaMedieval, a: AireMedieval) {
        val p = s.llama
        val t = 9f * density
        val f = if (a.oscuro) 1f else 0.8f
        drawCircle(P.FUEGO.copy(alpha = 0.25f * f), t * 1.1f, p - Offset(0f, t * 0.45f))
        val lengua = Path().apply {
            moveTo(p.x - t * 0.26f, p.y)
            cubicTo(p.x - t * 0.38f, p.y - t * 0.45f, p.x - t * 0.02f, p.y - t * 0.75f, p.x + t * 0.06f, p.y - t * 1.25f)
            cubicTo(p.x + t * 0.16f, p.y - t * 0.66f, p.x + t * 0.36f, p.y - t * 0.38f, p.x + t * 0.24f, p.y)
            close()
        }
        drawPath(lengua, Color(0xFFFF9A34).copy(alpha = 0.92f * f))
        drawCircle(Color(0xFFFFF4D8).copy(alpha = 0.9f * f), t * 0.17f, p - Offset(0f, t * 0.26f))
    }

    /** El compás de puntas de hierro con que se pica la pauta, abierto en la mesa a la derecha del libro. */
    private fun DrawScope.compas(s: SitioTapaMedieval, a: AireMedieval, sem: Int) {
        val u = density
        val x = s.libro.right + s.fondo.x + (s.w - s.libro.right - s.fondo.x) * 0.5f
        val k = cerca(Offset(x, s.h * 0.6f), s, a)
        val cabeza = Offset(x + 1f * u, s.h * 0.42f)
        val pataA = Offset(x - 11f * u + 3f * u * azar(sem, 3), s.h * 0.8f)
        val pataB = Offset(x + 14f * u, s.h * 0.74f)
        val hierro = a.luz(P.HIERRO, maxOf(k, 0.3f))
        for (p in listOf(pataA, pataB)) {
            drawLine(a.sombra(0.32f), cabeza + Offset(2f * u, 2.2f * u), p + Offset(2f * u, 2.2f * u), 2f * u, StrokeCap.Round)
            drawLine(hierro, cabeza, p, 2f * u, StrokeCap.Round)
            drawLine(a.luz(P.HIERRO_LUZ, maxOf(k, 0.3f)), cabeza + Offset(-0.6f * u, 0f), p + Offset(-0.6f * u, 0f), 0.6f * u)
        }
        drawCircle(a.sombra(0.3f), 3.4f * u, cabeza + Offset(1.6f * u, 1.8f * u))
        drawCircle(hierro, 3.4f * u, cabeza)
        drawCircle(a.luz(P.HIERRO_LUZ, maxOf(k, 0.3f)), 1.2f * u, cabeza - Offset(0.7f * u, 0.7f * u))
        if (a.oscuro) drawCircle(mezcla(P.LUNA, P.CIELO_NOCHE, 0.4f).copy(alpha = 0.5f), 0.9f * u, cabeza + Offset(1.2f * u, -1.2f * u))
        for (j in 0 until 3) drawCircle(a.sombra(0.4f), 0.6f * u, Offset(pataA.x - 1f * u, pataA.y - (4f + 3.5f * j) * u))
    }
}
