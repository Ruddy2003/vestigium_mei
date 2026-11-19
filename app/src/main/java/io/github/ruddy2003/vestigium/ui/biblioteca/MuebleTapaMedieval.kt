package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Dónde va cada parte del libro del día de Scriptorium, en píxeles: la franja de muro, la mesa, el libro (lomo y tapa), el
 * canto de las hojas (más grueso cuanto más escribiste), la cédula donde la pantalla pone el extracto y la lucerna.
 */
internal class SitioTapaMedieval(val w: Float, val h: Float, u: Float, palabras: Int) {
    /** Donde acaba el muro y empieza la mesa (su borde del fondo), y donde empieza el canto de delante. */
    val muro = h * MuebleTapaMedieval.MURO
    val frente = h * MuebleTapaMedieval.FRENTE
    /** El libro entero (lomo y tapa) y sus dos partes. */
    val libro = Rect(w * MuebleTapaMedieval.X0, h * MuebleTapaMedieval.Y0, w * MuebleTapaMedieval.X1, h * MuebleTapaMedieval.Y1)
    val lomo = Rect(libro.left, libro.top, libro.left + (w * 0.06f).coerceIn(17f * u, 25f * u), libro.bottom)
    val tapa = Rect(lomo.right, libro.top, libro.right, libro.bottom)
    /** Lo grueso del bloque de las hojas que se ve por delante: de 2.5 dp en blanco a 9.5 con miles de palabras. */
    val grueso = (2.5f + 7f * sqrt(palabras.coerceIn(0, 2000) / 2000f)) * u
    /** La cédula suelta sobre la tapa: un poco más que la caja del extracto. */
    val cedula = MuebleTapaMedieval.CAJA.let { k -> Rect(w * (k.left - 0.018f), h * (k.top - 0.03f), w * (k.right + 0.018f), h * (k.bottom + 0.035f)) }
    /** El compartimento del título (la fecha dorada), entre el filete de arriba y la cédula. */
    val titulo = Rect(tapa.left + 19f * u, h * 0.165f, tapa.right - 19f * u, cedula.top - 4f * u)
    /** La llama de la lucerna, a la izquierda del libro, y la base de la lucerna en la mesa. */
    val baseLucerna = Offset(libro.left * 0.55f, h * 0.43f)
    val llama = Offset(baseLucerna.x - 12.4f * u, baseLucerna.y - 15f * u)
}

/**
 * El libro del día de Scriptorium (lo usa `EstanteMedieval`): el códice de hoy, cerrado, sobre la mesa larga del scriptorium,
 * contra el muro de cal y sillarejo de la misma celda que el armario y el Ideario. Es un códice románico de tablas cubiertas
 * de badana teñida (el tinte sale del día), con su gofrado en seco a mano (filetes y florones de hierro suelto, no una red),
 * cinco bollones de latón, las dos correas con su broche en el canto y el lomo de nervios con la cuenta de palabras dorada.
 * La fecha va dorada en el compartimento del título; lo escrito, en una cédula de pergamino suelta que alguien dejó encima
 * (pautada a punta seca; escrita, con su punto de rúbrica). El canto de las hojas engorda con las palabras y, escrito, le
 * cuelgan los registros. A la izquierda, la lucerna de barro; a la derecha, el compás de puntas con que se pica la pauta.
 *
 * La luz: de día, el rayo de la ventana (que queda fuera del cuadro, arriba a la izquierda, como en la biblioteca) cruza la
 * mesa y el lomo, y todo echa su sombra violeta a la derecha. De noche (modo oscuro) solo arde la lucerna: lo cercano se
 * enciende y lo lejano se pierde en la noche del tema, pero la cédula y la fecha se siguen leyendo. En los Sueños entra
 * además la luna por la misma ventana (fría, contra la llama tibia) y el bollón de abajo es un creciente de oro.
 */
internal object MuebleTapaMedieval {
    private val P = PaletaMedieval

    /** La franja de muro y el canto de la mesa, en fracción del alto. */
    const val MURO = 0.1f
    const val FRENTE = 0.93f
    /** El libro, en fracciones del ancho (a lo ancho) y del alto (a lo alto). */
    const val X0 = 0.145f
    const val X1 = 0.895f
    const val Y0 = 0.115f
    const val Y1 = 0.82f
    /** Donde la pantalla pone el extracto: en la cédula, centrada en la tapa (el mismo tamaño que antes). */
    val CAJA = Rect(0.29f, 0.35f, 0.81f, 0.7f)

    /** Cuánta luz de la lucerna le llega a [p] (1 junto a la llama, 0 lejos); de día, toda. */
    private fun cerca(p: Offset, s: SitioTapaMedieval, a: AireMedieval): Float {
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
            if (a.lumbre) halo(s, a)
            with(LibroTapaMedieval) { libro(s, a, e, medidor, ab) { p -> cerca(p, s, a) } }
            if (!oscuro && !e.noche) rayo(s, a, P.SOL, 0.32f)
            lucerna(s, a)
            compas(s, a, e.semilla)
            if (e.noche) rayo(s, a, P.LUNA, if (oscuro) 0.14f else 0.2f)
            if (a.lumbre) llamaLucerna(s, a)
            granoMueble(Rect(0f, 0f, w, h), oscuro, 0.8f)
        }
    }

    // ---------------------------------------------------------------- el muro y la mesa

    /**
     * Pinta [r] con [color] tal como lo deja la luz del momento: de día, liso; de noche, hundido lejos de la lucerna y con la
     * luz de la llama encima, que se apaga con la distancia (un solo degradado, sin bandas).
     */
    private fun DrawScope.alumbrado(r: Rect, color: Color, s: SitioTapaMedieval, a: AireMedieval, lejos: Float = 0.05f) {
        drawRect(a.luz(color, if (a.lumbre) lejos else 1f), r.topLeft, r.size)
        if (!a.lumbre) return
        val alcance = s.w * (if (a.oscuro) 0.78f else 1.1f)
        drawRect(Brush.radialGradient(0f to a.luz(color, 0.95f), 0.3f to a.luz(color, 0.6f).copy(alpha = 0.85f), 0.65f to a.luz(color, 0.3f).copy(alpha = 0.45f),
            1f to Color.Transparent, center = s.llama, radius = alcance), r.topLeft, r.size)
    }

    /**
     * La franja de muro: el sillarejo de caras planas con su mortero (las mismas piedras de la celda del armario), un paño de
     * cal vieja que lo tapa a medias y la sombra de la mesa arrimada.
     */
    private fun DrawScope.muro(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density
        alumbrado(Rect(0f, 0f, s.w, s.muro), P.MORTERO, s, a)
        // Una hilada y media de piedras, de largos desiguales.
        var y = -s.muro * 0.35f; var f = 0
        while (y < s.muro) {
            val alto = s.muro * (0.62f + 0.2f * azar(2100, f))
            var x = -s.w * 0.05f * azar(2101, f); var j = 0
            while (x < s.w) {
                val largo = s.w * (0.05f + 0.11f * azar(2102 + f, j)) + 5f * u
                val arriba = y + (1f + 3f * azar(2103 + f, j)) * u
                val abajo = y + alto - (1f + 2.6f * azar(2104 + f, j)) * u
                piedra(Rect(x + (1f + 1.6f * azar(2105 + f, j)) * u, arriba, x + largo - 1.2f * u, abajo), 2110 + 31 * f + j, s, a)
                // El ripio calzado en la junta, a veces.
                if (azar(2106 + f, j) > 0.7f) piedra(Rect(x + largo * 0.3f, y - 0.5f * u, x + largo * 0.3f + 6f * u, arriba - 0.6f * u), 2190 + 31 * f + j, s, a)
                x += largo; j++
            }
            y += alto; f++
        }
        // El paño de cal vieja, a la derecha, con su canto en sombra.
        val cal = poligonoMedieval(s.w * 0.52f, 0f, s.w * 0.86f, 0f, s.w * 0.84f, s.muro * 0.35f, s.w * 0.79f, s.muro * 0.62f, s.w * 0.7f, s.muro * 0.55f,
            s.w * 0.63f, s.muro * 0.85f, s.w * 0.56f, s.muro * 0.5f)
        drawPath(corridoMedieval(cal, 0.8f * u, 1f * u), a.frio(0.3f))
        drawPath(cal, a.luz(mezcla(P.CAL, P.MORTERO, 0.3f), if (a.lumbre) 0.1f else 1f))
        pinceladas(Rect(s.w * 0.52f, 0f, s.w * 0.86f, s.muro), a.sombra(1f), 2120, 5, u, 0.06f)
        // De día, el muro se enfría hacia la derecha, lejos de la ventana.
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.frio(0.2f)), s.w * 0.3f, s.w), Offset(s.w * 0.3f, 0f), Size(s.w * 0.7f, s.muro))
        // La mesa arrimada al muro: la sombra que deja contra él.
        drawRect(a.sombra(0.32f), Offset(0f, s.muro - 2.6f * u), Size(s.w, 2.6f * u))
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
        val k = if (a.lumbre) cerca(r.center, s, a) else 1f
        drawPath(corridoMedieval(p, 1.2f * u, 1.4f * u), a.frio(0.3f))
        drawPath(p, a.luz(tono, k))
        drawPath(poligonoMedieval(r.left + c(1), r.top, r.right - c(2), r.top, r.left + r.width * 0.55f, r.bottom - c(4)), if (azar(sem, 8) > 0.5f) a.claro(0.1f) else a.frio(0.08f))
        drawLine(a.claro(0.4f), Offset(r.left + c(1), r.top + 0.4f * u), Offset(r.right - c(2), r.top + 0.4f * u), 0.8f * u)
    }

    /**
     * La mesa larga de roble: tres tablas a lo largo con su veta y sus juntas, el roce claro donde se apoyan los brazos, una
     * gota de tinta vieja y la quemadura de la lucerna; delante, el canto con su arista de luz.
     */
    private fun DrawScope.mesa(s: SitioTapaMedieval, a: AireMedieval, sem: Int) {
        val u = density
        val cara = Rect(0f, s.muro, s.w, s.frente)
        alumbrado(cara, P.ROBLE, s, a)
        val veta = a.luz(P.NOGAL_OSC, 0.6f).copy(alpha = 0.3f)
        vetas(cara, veta, 2150, densidad = 0.3f, grosor = 0.7f)
        pinceladas(cara, a.claro(1f), 2160, 9, u, 0.07f)
        pinceladas(cara, a.sombra(1f), 2161, 7, u, 0.05f)
        // Las juntas de las tablas, en sombra, con el canto de luz de la de abajo.
        for (f in listOf(0.36f, 0.73f)) {
            val y = cara.top + cara.height * f
            drawLine(a.sombra(0.36f), Offset(0f, y), Offset(s.w, y), 1.1f * u)
            drawLine(a.claro(0.18f), Offset(0f, y + 1.2f * u), Offset(s.w, y + 1.2f * u), 0.7f * u)
        }
        // El roce de los brazos, delante del libro, y la gota de tinta y la quemadura junto a la lucerna.
        drawOval(a.claro(0.1f), Offset(s.w * 0.3f, s.frente - s.h * 0.1f), Size(s.w * 0.45f, s.h * 0.08f))
        drawOval(a.luz(P.TINTA, 0.5f).copy(alpha = 0.55f), Offset(s.w * (0.925f + 0.02f * azar(sem, 1)), s.h * 0.87f), Size(4.6f * u, 2.6f * u))
        drawCircle(a.luz(P.TINTA, 0.5f).copy(alpha = 0.4f), 0.9f * u, Offset(s.w * 0.918f, s.h * 0.85f))
        drawOval(a.luz(P.NOGAL_OSC, 0.6f).copy(alpha = 0.3f), Offset(s.baseLucerna.x - 16f * u, s.baseLucerna.y - 3f * u), Size(32f * u, 10f * u))
        // El canto de delante: la arista de luz, la cara en sombra y la veta de testa.
        val canto = Rect(0f, s.frente, s.w, s.h)
        alumbrado(canto, mezcla(P.ROBLE, P.NOGAL, 0.45f), s, a, 0.02f)
        drawRect(a.frio(0.2f), Offset(0f, canto.top + canto.height * 0.5f), Size(s.w, canto.height * 0.5f))
        drawLine(a.claro(0.5f), Offset(0f, canto.top + 0.6f * u), Offset(s.w, canto.top + 0.6f * u), 1.1f * u)
        if (a.oscuro) drawLine(a.filo(0.35f), Offset(0f, canto.top + 0.6f * u), Offset(s.w * 0.4f, canto.top + 0.6f * u), 1.1f * u)
    }

    // ---------------------------------------------------------------- la luz

    /** El rayo de la ventana, que entra de arriba a la izquierda y cruza la mesa y el lomo: el sol, o la luna en los Sueños. */
    private fun DrawScope.rayo(s: SitioTapaMedieval, a: AireMedieval, color: Color, fuerza: Float) {
        val w = s.w; val h = s.h
        for ((d, al) in listOf(0.07f to 0.22f, 0.035f to 0.28f, 0f to 0.34f)) {
            drawPath(poligonoMedieval(w * (0.03f - d), s.muro, w * (0.2f + d), s.muro, w * (0.44f + d * 1.3f), h, w * (0.19f - d * 1.3f), h), color.copy(alpha = fuerza * al))
        }
        // En el muro, el rayo da de lleno arriba a la izquierda.
        drawRect(Brush.horizontalGradient(listOf(color.copy(alpha = fuerza * 0.9f), Color.Transparent), 0f, w * 0.45f), Offset.Zero, Size(w * 0.45f, s.muro))
    }

    /** De noche, el resplandor de la lucerna sobre el muro y la mesa (lo demás ya se apagó con la distancia). */
    private fun DrawScope.halo(s: SitioTapaMedieval, a: AireMedieval) {
        val f = if (a.oscuro) 1f else 0.5f
        mancha(s.llama, s.w * 0.55f, P.LLAMA.copy(alpha = 0.2f * f))
        mancha(s.llama, s.w * 0.17f, P.FUEGO.copy(alpha = 0.32f * f))
    }

    // ---------------------------------------------------------------- los objetos

    /**
     * La lucerna de barro vidriado: el cuerpo con su asa de anillo, la piquera hacia la izquierda con la mecha (tiznada de día)
     * y el goterón de aceite; deja su sombra en la mesa.
     */
    private fun DrawScope.lucerna(s: SitioTapaMedieval, a: AireMedieval) {
        val u = density * 1.3f
        val b = s.baseLucerna
        val k = cerca(b, s, a)
        val barro = a.luz(P.BARRO, k)
        val vidriado = a.luz(mezcla(P.BARRO, P.VERDE, 0.6f), k)
        contactoMedieval(Offset(b.x + 3f * u, b.y + 0.5f * u), 24f * u, 5f * u, a, 0.34f)
        // El pie y el cuerpo.
        drawOval(mezcla(barro, a.hondo, 0.25f), Offset(b.x - 6f * u, b.y - 3f * u), Size(12f * u, 4f * u))
        val cuerpo = Path().apply {
            moveTo(b.x - 9f * u, b.y - 4f * u)
            cubicTo(b.x - 10f * u, b.y - 12f * u, b.x + 9f * u, b.y - 13f * u, b.x + 9f * u, b.y - 5f * u)
            cubicTo(b.x + 7f * u, b.y - 1.5f * u, b.x - 7f * u, b.y - 1.5f * u, b.x - 9f * u, b.y - 4f * u)
            close()
        }
        // La piquera, que sale hacia la izquierda y arriba.
        val l = s.llama
        val piquera = Path().apply {
            moveTo(b.x - 6f * u, b.y - 9.5f * u)
            lineTo(l.x - 1.6f * u, l.y + 0.6f * u)
            quadraticTo(l.x - 2.4f * u, l.y + 2.6f * u, l.x + 0.6f * u, l.y + 2.6f * u)
            lineTo(b.x - 3f * u, b.y - 5.5f * u)
            close()
        }
        drawPath(piquera, vidriado)
        drawPath(cuerpo, vidriado)
        // El vidriado no llega abajo: el barro crudo; la luz arriba a la izquierda y la sombra fría a la derecha.
        drawPath(Path().apply {
            moveTo(b.x - 9f * u, b.y - 4f * u)
            cubicTo(b.x - 7f * u, b.y - 1.5f * u, b.x + 7f * u, b.y - 1.5f * u, b.x + 9f * u, b.y - 5f * u)
            lineTo(b.x + 8.6f * u, b.y - 7f * u)
            cubicTo(b.x + 4f * u, b.y - 4.5f * u, b.x - 5f * u, b.y - 4.5f * u, b.x - 9.4f * u, b.y - 6f * u)
            close()
        }, barro)
        drawArc(a.claro(0.55f), 200f, 70f, false, Offset(b.x - 8f * u, b.y - 12f * u), Size(14f * u, 9f * u), style = Stroke(1.1f * u))
        drawOval(a.frio(0.22f), Offset(b.x + 2f * u, b.y - 10f * u), Size(7f * u, 6f * u))
        // La boca de llenar con su aceite, y el asa de anillo, a la derecha.
        drawOval(a.luz(P.NOGAL_OSC, k), Offset(b.x - 2.6f * u, b.y - 11.4f * u), Size(5.2f * u, 2.2f * u))
        drawArc(vidriado, 200f, 250f, false, Offset(b.x + 7f * u, b.y - 12f * u), Size(6.4f * u, 6.4f * u), style = Stroke(1.6f * u))
        // La mecha, tiznada (de día, el tizne en la piquera).
        drawLine(a.luz(P.NEGRO, k), Offset(l.x + 0.4f * u, l.y + 2f * u), Offset(l.x, l.y), 1f * u, StrokeCap.Round)
        if (!a.lumbre) drawCircle(P.NEGRO.copy(alpha = 0.32f), 2f * u, Offset(l.x - 0.6f * u, l.y + 1.8f * u))
    }

    /** La llama de la lucerna, encima de todo, con su propio color. */
    private fun DrawScope.llamaLucerna(s: SitioTapaMedieval, a: AireMedieval) {
        val p = s.llama
        val t = 7.5f * density
        val f = if (a.oscuro) 1f else 0.75f
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
        val x = s.libro.right + (s.w - s.libro.right) * 0.45f
        val k = cerca(Offset(x, s.h * 0.6f), s, a)
        val cabeza = Offset(x + 1f * u, s.h * 0.42f)
        val pataA = Offset(x - 11f * u + 3f * u * azar(sem, 3), s.h * 0.8f)
        val pataB = Offset(x + 15f * u, s.h * 0.74f)
        val hierro = a.luz(P.HIERRO, k)
        for (p in listOf(pataA, pataB)) {
            drawLine(a.sombra(0.32f), cabeza + Offset(2f * u, 2.2f * u), p + Offset(2f * u, 2.2f * u), 2f * u, StrokeCap.Round)
            drawLine(hierro, cabeza, p, 2f * u, StrokeCap.Round)
            drawLine(a.luz(P.HIERRO_LUZ, k), cabeza + Offset(-0.6f * u, 0f), p + Offset(-0.6f * u, 0f), 0.6f * u)
        }
        drawCircle(a.sombra(0.3f), 3.4f * u, cabeza + Offset(1.6f * u, 1.8f * u))
        drawCircle(hierro, 3.4f * u, cabeza)
        drawCircle(a.luz(P.HIERRO_LUZ, k), 1.2f * u, cabeza - Offset(0.7f * u, 0.7f * u))
        if (a.oscuro) drawCircle(a.filo(0.5f * k), 1f * u, cabeza - Offset(1.1f * u, 1.1f * u))
        // Los picados que acaba de hacer en la orilla de la mesa.
        for (j in 0 until 3) drawCircle(a.sombra(0.4f), 0.6f * u, Offset(pataA.x - 1f * u, pataA.y - (4f + 3.5f * j) * u))
    }
}
