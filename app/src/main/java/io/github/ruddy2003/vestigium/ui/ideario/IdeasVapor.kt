package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Vapor
// =====================================================================

/**
 * Vapor: el casillero de la caseta de los maquinistas en un depósito de locomotoras del norte de Inglaterra, hacia 1880 (la
 * otra vida de Albert, el fogonero). Es un interior sin ventana: un muro de ladrillo tiznado con media altura de tabla
 * machihembrada pintada de verde ferrocarril, y la luz de día que entra de arriba a la izquierda, sucia de humo. Cruza la
 * pared una tubería de vapor de verdad, con su calorifugado de cuerda y yeso, sus bridas y abrazaderas y una válvula de volante
 * que gotea (la chorrera baja por el ladrillo); la tubería dobla y baja por el borde derecho. En la pared, una regleta con tres
 * llaves fijas colgadas, el tablón de avisos (un horario en columnas ilegibles, un aviso con su franja roja y una papeleta con
 * la esquina levantada) y la placa de una locomotora, de latón sobre rojo, con su número. El mueble es un casillero de caoba
 * barnizada con cantoneras de latón atornilladas, el número esmaltado de cada casilla, el tizne de las manos en los cantos y
 * un larguero abajo con su portatarjetas de latón (la placa de «y N más»). Encima, el punto focal, a la izquierda: el farol
 * de señales de mano, grande, con su lente de ojo de buey, la lente roja del costado, la chimenea con su sombrerete y el asa
 * de alambre, y delante la aceitera de cobre de pico largo; además, la lata del té de hojalata con la fiambrera, un trapo de
 * algodón en un montón con un pico que cae por el canto, el manómetro suelto con su sifón de cobre, la gorra de maquinista y
 * el reloj de bolsillo abierto, con la cadena que cuelga por el canto. Al pie, sobre losas con polvo de carbón, el cubo de
 * carbón con su cogedor (cortado por el borde izquierdo), la pala del fogonero apoyada en el casillero y la caja de
 * herramientas (cortada por el derecho). Cada tema es, según la semilla, una gaveta remachada (de latón, de cobre o de chapa
 * verde) con su portatarjetas, su tirador y las tarjetas que asoman (más con más fichas); un casillero abierto con una pila
 * de fajos de partes de servicio atada con cinta (más alta con más fichas) y el silbato de tubo del guarda colgado de su
 * cadena; o una caja de hojalata de mechas pintada (roja, azul pizarra o mostaza) con la tapa de bisagra abierta y los rollos
 * de mecha que asoman. Lo que no tiene tema es una lata de galletas con la tapa ladeada y tarjetas sueltas; un hueco libre
 * guarda un trapo colgado de una escarpia, una tuerca y un perno. Al abrir uno, lo guardado sube. De noche todo se hunde en
 * el azul de hollín: alumbra solo el farol (un halo cálido en la pared y en la tapa, el filo en el latón y el cobre), su
 * lente roja tiñe la pared hacia la izquierda, y los casilleros de arriba quedan encendidos y los de abajo casi perdidos.
 * Por dentro, caoba con rieles de latón. Las fichas son tarjetas perforadas de esquina cortada.
 */
object IdeasVapor : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la caseta (sale de la del cuadro)
    /** El ladrillo tiznado, su junta de mortero y el hollín. */
    private val LADRILLO = Color(0xFF94503A)
    private val MORTERO = Color(0xFFB4A587)
    private val HOLLIN = Color(0xFF2E2B30)
    /** El verde ferrocarril de la tabla, de la chapa y de las cajas. */
    private val VERDE = Color(0xFF2F6046)
    /** La caoba barnizada del casillero, su luz y su sombra. */
    private val CAOBA = Color(0xFF6A3422)
    private val CAOBA_LUZ = Color(0xFF9A5A34)
    private val CAOBA_OSC = Color(0xFF3A1A10)
    /** El latón, su luz y su sombra; el cobre. */
    private val LATON = Color(0xFFC9A04A)
    private val LATON_LUZ = Color(0xFFE8CC7E)
    private val LATON_OSC = Color(0xFF8A6A24)
    private val COBRE = Color(0xFFB0663A)
    /** El hierro, la hojalata y el yeso del calorifugado. */
    private val HIERRO = Color(0xFF3E3D44)
    private val HOJALATA = Color(0xFFA2A49E)
    private val YESO = Color(0xFFD6CCB4)
    /** El papel, la cartulina, la tinta; el rojo de la señal y de la placa; el azul pizarra de la gorra; la mostaza. */
    private val HUESO = Color(0xFFE9E0C8)
    private val CARTULINA = Color(0xFFE8DCB8)
    private val TINTA = Color(0xFF231B14)
    private val ROJO = Color(0xFF9C3D24)
    private val PIZARRA = Color(0xFF3E4A5C)
    private val MOSTAZA = Color(0xFFB08A34)
    /** La llama del farol y el rojo de su lente encendida. */
    private val LLAMA = Color(0xFFFFC56E)
    private val ROJO_SENAL = Color(0xFFFF5A3E)
    /** La noche de la caseta: azul de hollín. */
    private val NOCHE = Color(0xFF14121B)
    /** La tarjeta de noche lejos del farol: con [TINTA] da unos 6:1 en el título y 4.6:1 en la cuenta. */
    private val PAPEL_LEJOS = Color(0xFF9E9681)
    /** Las del interior y las fichas (sin cambios). */
    private val VIDRIO = Color(0xFFF4EFE0)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.42f
    override val corona = 0.6f
    override val base = 0.3f
    override val lado = 0.07f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la tapa, su canto de frente y lo bajo de la moldura, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.87f
    private const val CANTO = 0.95f
    /** En la base: el rincón del muro con el suelo, lo alto del zócalo, donde pisa el casillero y las cosas del pie. */
    private const val SUELO = 0.42f
    private const val ZOCALO = 0.34f
    private const val PISAN = 0.5f
    private const val PIE = 0.96f
    /** Los costados del casillero, en fracción del ancho. */
    private const val IZQ = 0.025f
    private const val DER = 0.975f
    /** El piso de cada hueco y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f
    /** Donde empieza la tabla verde, en fracción del ancho (por encima, ladrillo). */
    private const val DADO = 0.3f

    // ---------------------------------------------------------------- la luz y la noche

    /** La lente del farol, en fracciones del ancho. */
    private val LUZ = Offset(0.313f, 0.393f)
    /** La lente roja del costado del farol: tiñe la pared hacia la izquierda. */
    private val LENTE_ROJA = Offset(0.218f, 0.393f)

    /** Lo que el farol da a su alrededor, en la pared y sobre la tapa: cae con la distancia a la lente. */
    private fun radial(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        return 1.35f * exp(-3.3f * sqrt(dx * dx + dy * dy))
    }

    /** Cuánto le llega del farol a un punto [p] (en fracciones del ancho): cae con la distancia; abajo, casi nada. */
    private fun luz(p: Offset): Float = maxOf(radial(p), luzCuerpo(p)).coerceIn(0.03f, 1f)

    /**
     * La luz que el farol echa hacia abajo sobre el frente del casillero: la primera fila encendida, la segunda todavía clara,
     * la tercera a media luz, la cuarta y la quinta casi perdidas; un poco menos en la columna de la derecha, más lejos del
     * farol. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.55f to 0.92f, 0.79f to 0.84f, 1.12f to 0.62f, 1.45f to 0.34f, 1.79f to 0.13f, 2.12f to 0.05f, 2.5f to 0.035f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.06f).coerceAtLeast(0f)).coerceIn(0.72f, 1f)
        return k * lado
    }

    /** Cuánto rojo de la lente del costado le llega a un punto de la pared (solo a su izquierda y por encima de la tapa). */
    private fun rojo(p: Offset): Float {
        val dx = LENTE_ROJA.x - p.x
        if (dx <= 0f || p.y > 0.49f) return 0f
        val cono = 0.035f + 0.5f * dx
        val v = 1f - abs(p.y - LENTE_ROJA.y) / cono
        if (v <= 0f) return 0f
        return (v.pow(0.8f) * 0.95f * exp(-2.4f * dx)).coerceIn(0f, 1f)
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia el
     * suyo según [k] (lo que le llega del farol), teñido de llama, y de rojo por [rojo] (la lente del costado).
     */
    private class Tono(val o: Boolean, val k: Float, val rojo: Float = 0f, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        val l = mezcla(v, LLAMA, 0.22f * k)
        return if (rojo > 0.01f) mezcla(l, mezcla(ROJO_SENAL, col, 0.35f), 0.45f * rojo) else l
    }

    /** La sombra: de día, violeta hondo; de noche, la noche de la caseta. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo del farol (rojizo donde llega la lente roja). */
    private fun Tono.b(a: Float): Color =
        if (!o) luzMueble(a) else mezcla(LLAMA, ROJO_SENAL, rojo).copy(alpha = (a * 0.9f * maxOf(k, rojo)).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono {
        val f = p / anchoW
        return Tono(esNoche, if (esNoche) luz(f) else 0f, if (esNoche) rojo(f) else 0f, dia)
    }

    /** Lo mismo para lo que está en la pared, lejos del frente del casillero: solo le llega lo que el farol da alrededor. */
    private fun tnPared(p: Offset): Tono {
        val f = p / anchoW
        return Tono(esNoche, if (esNoche) radial(f).coerceIn(0.03f, 1f) else 0f, if (esNoche) rojo(f) else 0f, 0f)
    }

    /** De noche la luz viene del farol: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera del farol, tanto más marcada
     * cuanto más cerca está de él. No baja de [hasta] (la tapa o el suelo que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.011f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, -w * 0.008f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa, la tabla o el suelo, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.2f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El filo cálido del farol en un objeto, de noche: en su canto de arriba y en el costado que mira a la luz. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello del farol en el latón, el cobre o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Un tornillo o un remache de [col] con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, col: Color = LATON) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.6f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** El trazo de una cifra, de pincel redondo, dentro de [r] (los números esmaltados de las casillas y el de la placa). */
    private fun trazoCifra(ch: Char, r: Rect): Path = Path().apply {
        fun x(f: Float) = r.left + r.width * f
        fun y(f: Float) = r.top + r.height * f
        fun m(a: Float, b: Float) = moveTo(x(a), y(b))
        fun l(a: Float, b: Float) = lineTo(x(a), y(b))
        fun q(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float) = cubicTo(x(a), y(b), x(c), y(d), x(e), y(f))
        when (ch) {
            '0' -> addOval(Rect(x(0.12f), y(0f), x(0.88f), y(1f)))
            '1' -> { m(0.22f, 0.26f); l(0.56f, 0f); l(0.56f, 1f); m(0.24f, 1f); l(0.86f, 1f) }
            '2' -> { m(0.1f, 0.26f); q(0.1f, -0.08f, 0.9f, -0.08f, 0.9f, 0.28f); q(0.9f, 0.56f, 0.3f, 0.72f, 0.08f, 1f); l(0.94f, 1f) }
            '3' -> { m(0.1f, 0.12f); q(0.3f, -0.07f, 0.88f, -0.04f, 0.86f, 0.25f); q(0.84f, 0.43f, 0.6f, 0.47f, 0.42f, 0.47f); q(0.7f, 0.47f, 0.92f, 0.55f, 0.92f, 0.74f); q(0.92f, 1.07f, 0.25f, 1.06f, 0.06f, 0.85f) }
            '4' -> { m(0.72f, 1f); l(0.72f, 0f); l(0.05f, 0.7f); l(0.97f, 0.7f) }
            '5' -> { m(0.86f, 0f); l(0.22f, 0f); l(0.15f, 0.45f); q(0.35f, 0.3f, 0.93f, 0.32f, 0.93f, 0.68f); q(0.93f, 1.08f, 0.25f, 1.06f, 0.06f, 0.84f) }
            '6' -> { m(0.84f, 0.1f); q(0.55f, -0.1f, 0.1f, 0.1f, 0.1f, 0.62f); q(0.1f, 1.12f, 0.92f, 1.12f, 0.92f, 0.68f); q(0.92f, 0.3f, 0.25f, 0.3f, 0.12f, 0.6f) }
            '7' -> { m(0.06f, 0f); l(0.94f, 0f); l(0.4f, 1f) }
            '8' -> { addOval(Rect(x(0.2f), y(0f), x(0.8f), y(0.46f))); addOval(Rect(x(0.1f), y(0.46f), x(0.9f), y(1f))) }
            '9' -> { m(0.16f, 0.9f); q(0.45f, 1.1f, 0.9f, 0.9f, 0.9f, 0.38f); q(0.9f, -0.12f, 0.08f, -0.12f, 0.08f, 0.32f); q(0.08f, 0.7f, 0.75f, 0.7f, 0.88f, 0.4f) }
            else -> {}
        }
    }

    /** Un número pintado a pincel dentro de [r], con cifras de trazo redondo. */
    private fun DrawScope.cifras(txt: String, r: Rect, col: Color, grosor: Float) {
        val cw = r.width / txt.length
        txt.forEachIndexed { i, ch ->
            val caja = Rect(r.left + cw * i + cw * 0.14f, r.top, r.left + cw * (i + 1) - cw * 0.14f, r.bottom)
            drawPath(trazoCifra(ch, caja), col, style = Stroke(grosor, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
        }
    }
    /** Remaches a lo largo de una línea (el interior, sin cambios). */
    private fun DrawScope.remaches(a: Offset, b: Offset, paso: Float, color: Color) {
        val u = density
        val l = kotlin.math.hypot(b.x - a.x, b.y - a.y)
        val n = (l / paso).toInt()
        for (k in 0..n) {
            val f = if (n == 0) 0f else k / n.toFloat()
            val p = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)
            drawCircle(color, 1.4f * u, p)
            drawCircle(Color.White.copy(alpha = 0.35f), 0.5f * u, Offset(p.x - 0.4f * u, p.y - 0.4f * u))
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val ch = d.corona.height
        val b = d.base; val hb = b.height
        val sup = ch * SUP; val frente = ch * FRENTE; val canto = ch * CANTO
        val pieA = sup + (frente - sup) * 0.3f
        val pieF = sup + (frente - sup) * 0.85f
        val suelo = b.top + hb * SUELO

        // ---- el muro de ladrillo, la tabla verde y el suelo de losas
        pared(w, ch, suelo)
        piso(w, suelo, alto)

        // ---- en la pared: la tubería, las llaves, el tablón y la placa de la locomotora
        tuberia(w, ch, sup, suelo)
        llaves(w, sup)
        tablon(Rect(w * 0.43f, w * 0.106f, w * 0.69f, w * 0.318f))
        placaLocomotora(Rect(w * 0.75f, w * 0.135f, w * 0.93f, w * 0.215f))

        // ---- la luz roja de la lente del costado, en la pared (de noche)
        if (o) haz(w, sup)

        // ---- el casillero y su tapa
        casillero(d, ch, canto, b)
        tapa(w, sup, frente, canto)

        // ---- lo de encima, de atrás adelante
        lataDelTe(Offset(w * 0.095f, pieA), sup)
        gorra(Offset(w * 0.795f, pieA + (frente - sup) * 0.25f), sup)
        manometroSuelto(Offset(w * 0.665f, pieA), sup)
        farol(Offset(w * (LUZ.x - 0.0034f), pieA), sup)
        fiambrera(Offset(w * 0.15f, pieF), sup)
        trapo(Offset(w * 0.575f, pieF), frente, canto)
        aceitera(Offset(w * 0.46f, pieF), sup)
        reloj(Offset(w * 0.9f, pieF), frente, canto)

        // ---- al pie: el cubo de carbón, la pala y la caja de herramientas
        cubo(Offset(w * 0.08f, b.top + hb * PIE), hb)
        pala(b, w, hb)
        cajaHerramientas(Offset(w * 0.9f, b.top + hb * PIE), hb)

        // ---- la placa de «y N más»: el portatarjetas del larguero
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            val dentro = Rect(r.left + 3f * u, r.top + 2.5f * u, r.right - 3f * u, r.bottom - 2.5f * u)
            drawRect(if (o) PAPEL_LEJOS else CARTULINA, dentro.topLeft, dentro.size)
            drawRect(t.s(0.15f), dentro.topLeft, Size(dentro.width, 1.5f * u))
        }

        // ---- la luz del farol, por encima de lo que tiene cerca
        val lente = LUZ * w
        if (o) {
            mancha(lente + Offset(0f, w * 0.06f), w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(lente + Offset(0f, w * 0.03f), w * 0.2f, LLAMA.copy(alpha = 0.2f))
            mancha(lente - Offset(0f, w * 0.13f), w * 0.1f, LLAMA.copy(alpha = 0.12f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El muro: ladrillo con su junta de mortero y el tizne del humo arriba (de noche, el halo del farol), y desde [DADO] hacia
     * abajo la tabla machihembrada pintada de verde ferrocarril con su moldura, su rodapié y los golpes donde salta la pintura.
     */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val dado = w * DADO
        // El fondo de mortero, en un solo trazo con la luz (de día) o el halo (de noche).
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(MORTERO, LUZ_MUEBLE, 0.2f), MORTERO, mezcla(MORTERO, SOMBRA_MUEBLE, 0.25f)), 0f, w), Offset.Zero, Size(w, dado))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f)), 0f).c(MORTERO)
            drawRect(Brush.radialGradient(0f to k(0.05f), 0.12f to k(0.14f), 0.28f to k(0.3f), 0.5f to k(0.55f), 1f to k(1.1f),
                center = LUZ * w, radius = w * 1.1f), Offset.Zero, Size(w, dado))
        }
        // Los ladrillos, uno por uno, a soga; cada hilada corrida media pieza.
        val hl = w * 0.026f; val lb = w * 0.075f; val junta = 1.4f * u
        var fila = 0
        var y = 0f
        while (y < dado) {
            var x = if (fila % 2 == 0) -lb * 0.5f else -lb * 0.05f
            var j = 0
            while (x < w) {
                val r = Rect(x + junta / 2, y + junta / 2, x + lb - junta / 2, minOf(y + hl - junta / 2, dado))
                val t = tn(r.center, if (o) 0f else 0.3f * (1f - r.center.x / w).pow(1.5f) * (1f - 0.5f * r.center.y / dado))
                val v = azar(5100 + fila, j)
                val col = when {
                    v < 0.18f -> mezcla(LADRILLO, HOLLIN, 0.35f)
                    v < 0.34f -> mezcla(LADRILLO, MOSTAZA, 0.18f)
                    v < 0.5f -> mezcla(LADRILLO, CAOBA, 0.4f)
                    else -> LADRILLO
                }
                drawRect(t.c(col), r.topLeft, r.size)
                drawRect(t.b(0.28f), r.topLeft, Size(r.width, 1f * u))
                drawRect(t.s(0.22f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
                if (azar(5200 + fila, j) < 0.2f) drawOval(t.s(0.18f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.25f), Size(r.width * 0.3f, r.height * 0.4f))
                x += lb; j++
            }
            y += hl; fila++
        }
        // El tizne del humo: arriba, y más espeso sobre la tubería.
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(HOLLIN.copy(alpha = 0.06f), HOLLIN.copy(alpha = 0.3f), HOLLIN.copy(alpha = 0.42f)), 0f, w), Offset.Zero, Size(w, dado * 0.4f))
            mancha(Offset(w * 0.1f, w * 0.14f), w * 0.5f, luzMueble(0.3f))
        } else drawRect(NOCHE.copy(alpha = 0.25f), Offset.Zero, Size(w, w * 0.06f))
        pinceladas(Rect(0f, 0f, w, dado), if (o) NOCHE else HOLLIN, 5101, 34, u, 0.1f)

        // La tabla verde machihembrada: las tablas verticales con su junta, la moldura de arriba y el rodapié.
        val tz = tn(Offset(w * 0.5f, (dado + suelo) / 2))
        drawRect(tz.c(VERDE), Offset(0f, dado), Size(w, suelo - dado))
        var x = w * 0.012f
        var i = 0
        while (x < w) {
            val tt = tn(Offset(x, (dado + suelo) / 2))
            drawLine(tt.s(0.4f), Offset(x, dado), Offset(x, suelo), 1.3f * u)
            drawLine(tt.b(0.22f), Offset(x + 1.3f * u, dado), Offset(x + 1.3f * u, suelo), 0.8f * u)
            // La pintura saltada en el borde de algunas tablas.
            if (azar(5300, i) < 0.35f) drawOval(tt.c(mezcla(CAOBA_LUZ, YESO, 0.3f)), Offset(x + w * 0.004f, dado + (suelo - dado) * (0.25f + 0.6f * azar(5301, i))), Size(w * 0.012f, w * 0.006f))
            x += w * 0.046f; i++
        }
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.14f), Color.Transparent, sombraMueble(0.16f)), 0f, w), Offset(0f, dado), Size(w, suelo - dado))
        pinceladas(Rect(0f, dado, w, suelo), if (o) NOCHE else HOLLIN, 5302, 16, u, 0.1f)
        // La moldura que separa el ladrillo de la tabla.
        val mt = tn(Offset(w * 0.5f, dado))
        drawRect(mt.s(0.3f), Offset(0f, dado - w * 0.004f), Size(w, w * 0.016f))
        drawRect(mt.c(mezcla(VERDE, HOLLIN, 0.35f)), Offset(0f, dado - w * 0.008f), Size(w, w * 0.014f))
        drawRect(mt.b(0.45f), Offset(0f, dado - w * 0.008f), Size(w, 1f * u))
        drawRect(mt.s(0.3f), Offset(0f, dado + w * 0.006f), Size(w, 1.5f * u))
    }

    /** El suelo de losas de piedra, con sus juntas que se abren hacia quien mira, el polvo de carbón y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(mezcla(HOJALATA, MOSTAZA, 0.18f)), Offset(0f, suelo), Size(w, alto - suelo))
        if (!esNoche) drawRect(Brush.verticalGradient(listOf(sombraMueble(0.18f), Color.Transparent), suelo, alto), Offset(0f, suelo), Size(w, alto - suelo))
        // Las juntas: dos hiladas de losas, abiertas en perspectiva.
        val ym = suelo + (alto - suelo) * 0.42f
        drawLine(t.s(0.4f), Offset(0f, ym), Offset(w, ym), 1.4f * u)
        drawLine(t.b(0.2f), Offset(0f, ym + 1.4f * u), Offset(w, ym + 1.4f * u), 0.8f * u)
        for (k in -2..8) {
            val x0 = w * (k + 0.35f * (k % 2)) / 5f
            val x1 = w / 2 + (x0 - w / 2) * 1.4f
            val f = if (k % 2 == 0) 0f else 0.42f
            val a = Offset(x0 + (x1 - x0) * f, suelo + (alto - suelo) * f)
            val e = if (k % 2 == 0) Offset(x0 + (x1 - x0) * 0.42f, ym) else Offset(x1, alto)
            drawLine(t.s(0.4f), a, e, 1.3f * u)
            drawLine(t.b(0.18f), a + Offset(1.3f * u, 0f), e + Offset(1.3f * u, 0f), 0.7f * u)
        }
        // El polvo de carbón, más espeso junto al cubo y al pie del casillero.
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else HOLLIN, 5400, 26, u, 0.16f)
        if (!esNoche) pinceladas(Rect(w * 0.35f, suelo + (alto - suelo) * 0.3f, w * 0.8f, alto), LUZ_MUEBLE, 5404, 6, u, 0.06f)
        for (k in 0 until 9) {
            val p = Offset(w * (0.31f + 0.2f * azar(5405, k) * azar(5405, k + 20)), suelo + (alto - suelo) * (0.55f + 0.4f * azar(5406, k)))
            val rr = w * (0.007f + 0.009f * azar(5407, k))
            drawOval(t.s(0.35f), Offset(p.x - rr * 0.6f, p.y + rr * 0.2f), Size(rr * 2.4f, rr * 0.7f))
            drawPath(poligono(p.x - rr, p.y + rr * 0.4f, p.x - rr * 0.4f, p.y - rr * 0.7f, p.x + rr * 0.8f, p.y - rr * 0.4f, p.x + rr, p.y + rr * 0.4f), t.c(mezcla(HOLLIN, PIZARRA, 0.25f)))
            drawLine(t.b(0.6f), Offset(p.x - rr * 0.8f, p.y + rr * 0.1f), Offset(p.x - rr * 0.4f, p.y - rr * 0.6f), 0.8f * u)
        }
        for (k in 0 until 22) {
            val p = Offset(w * (0.02f + 0.35f * azar(5401, k)), suelo + (alto - suelo) * (0.4f + 0.55f * azar(5402, k)))
            drawCircle(t.c(HOLLIN), (0.8f + 1.4f * azar(5403, k)) * u, p)
        }
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
        if (esNoche) drawRect(NOCHE.copy(alpha = 0.45f), Offset(0f, suelo), Size(w, alto - suelo))
    }

    // ---------------------------------------------------------------- en la pared

    /**
     * La tubería de vapor: corre por la pared con su calorifugado de cuerda y yeso (las vueltas de la cuerda, el yeso
     * cuarteado y tiznado), las bridas de hierro con sus tornillos, dos abrazaderas al muro y la válvula de volante que gotea; a
     * la derecha dobla con un codo y baja por el borde.
     */
    private fun DrawScope.tuberia(w: Float, ch: Float, sup: Float, suelo: Float) {
        val u = density
        val py = w * 0.072f; val pr = w * 0.019f
        val xc = w * 0.982f
        // La sombra en el muro.
        val forma = Path().apply {
            addRect(Rect(-w * 0.01f, py - pr, xc + pr, py + pr))
            addRect(Rect(xc - pr, py, xc + pr, suelo))
        }
        sombraEnPared(forma, Offset(w * 0.3f, py), suelo, 0.22f)
        // Las abrazaderas al muro, detrás.
        listOf(w * 0.12f, w * 0.52f).forEach { x ->
            val t = tn(Offset(x, py))
            drawRect(t.c(HIERRO), Offset(x - w * 0.006f, py - pr * 1.3f), Size(w * 0.012f, pr * 2.6f))
            clavo(Offset(x, py - pr * 1.45f), 1.4f * u, t, HIERRO)
            clavo(Offset(x, py + pr * 1.45f), 1.4f * u, t, HIERRO)
        }
        // El tramo horizontal y el que baja, por trozos (cada uno con su luz).
        fun tramo(r: Rect, horizontal: Boolean) {
            val t = tnPared(r.center)
            drawRect(t.c(YESO), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                if (horizontal) tumbado(r, t, 1.3f) else cilindro(r, t, 1.3f, luzDerecha(r.center.x))
                // Las vueltas de la cuerda, en diagonal.
                val paso = w * 0.011f
                if (horizontal) {
                    var x = r.left - pr
                    while (x < r.right + pr) {
                        drawLine(t.s(0.22f), Offset(x, r.bottom), Offset(x + pr * 0.7f, r.top), 1.1f * u)
                        drawLine(t.b(0.18f), Offset(x + 1.2f * u, r.bottom), Offset(x + pr * 0.7f + 1.2f * u, r.top), 0.6f * u)
                        x += paso
                    }
                    // El tizne de arriba.
                    drawRect(t.c(HOLLIN).copy(alpha = if (esNoche) 0.3f else 0.22f), r.topLeft, Size(r.width, r.height * 0.22f))
                } else {
                    var y = r.top
                    while (y < r.bottom) {
                        drawLine(t.s(0.22f), Offset(r.left, y + pr * 0.7f), Offset(r.right, y), 1.1f * u)
                        y += paso
                    }
                }
            }
        }
        val pasos = 8
        for (k in 0 until pasos) {
            val x0 = -w * 0.01f + (xc + pr + w * 0.01f) * k / pasos
            val x1 = -w * 0.01f + (xc + pr + w * 0.01f) * (k + 1) / pasos
            tramo(Rect(x0, py - pr, x1 + 0.5f, py + pr), true)
        }
        val bajada = 6
        for (k in 0 until bajada) {
            val y0 = py + pr + (suelo - py - pr) * k / bajada
            val y1 = py + pr + (suelo - py - pr) * (k + 1) / bajada
            tramo(Rect(xc - pr, y0, xc + pr, y1 + 0.5f), false)
        }
        // El codo.
        val tc = tn(Offset(xc, py))
        drawRect(tc.c(YESO), Offset(xc - pr, py - pr), Size(pr * 2, pr * 2))
        drawRect(tc.s(0.2f), Offset(xc, py), Size(pr, pr))
        // El yeso cuarteado: grietas y un trozo caído donde asoma la cuerda.
        listOf(0.08f, 0.33f, 0.71f).forEachIndexed { i, fx ->
            val t = tn(Offset(w * fx, py))
            val p0 = Offset(w * fx, py - pr * 0.6f)
            drawPath(Path().apply { moveTo(p0.x, p0.y); lineTo(p0.x + w * 0.006f, p0.y + pr * 0.5f); lineTo(p0.x + w * 0.002f, p0.y + pr * 1.1f); lineTo(p0.x + w * 0.01f, p0.y + pr * 1.5f) }, t.s(0.35f), style = Stroke(0.7f * u))
            if (i == 1) {
                drawOval(t.c(mezcla(MOSTAZA, CAOBA_LUZ, 0.4f)), Offset(p0.x + w * 0.015f, py - pr * 0.1f), Size(w * 0.03f, pr * 0.9f))
                for (j in 0 until 4) drawLine(t.s(0.3f), Offset(p0.x + w * (0.018f + 0.007f * j), py + pr * 0.75f), Offset(p0.x + w * (0.022f + 0.007f * j), py - pr * 0.05f), 0.7f * u)
            }
        }
        // Las bridas: dos discos de hierro con sus tornillos.
        listOf(w * 0.27f, w * 0.64f).forEach { x ->
            val t = tn(Offset(x, py))
            val r = Rect(x - w * 0.007f, py - pr * 1.35f, x + w * 0.007f, py + pr * 1.35f)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 1.5f * u), r.size)
            drawRect(t.c(HIERRO), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.4f) }
            drawRect(t.c(mezcla(HIERRO, HOLLIN, 0.4f)), Offset(r.left - w * 0.009f, py - pr * 1.05f), Size(w * 0.009f, pr * 2.1f))
            listOf(-0.95f, 0f, 0.95f).forEach { f -> clavo(Offset(x, py + pr * f), 1.3f * u, t, HOJALATA) }
        }
        // La válvula con su volante, que gotea.
        val vx = w * 0.865f
        val tv = tn(Offset(vx, py))
        val cuerpo = Path().apply { addOval(Rect(vx - w * 0.025f, py - pr * 1.25f, vx + w * 0.025f, py + pr * 1.25f)) }
        sombraEnPared(cuerpo, Offset(vx, py), suelo, 0.25f)
        drawPath(cuerpo, tv.c(LATON))
        clipPath(cuerpo) { cilindro(Rect(vx - w * 0.025f, py - pr * 1.25f, vx + w * 0.025f, py + pr * 1.25f), tv, 1.5f) }
        drawRect(tv.c(LATON_OSC), Offset(vx - w * 0.006f, py - pr * 2.6f), Size(w * 0.012f, pr * 1.4f))
        // El volante, visto un poco desde abajo: un aro con cuatro radios, pintado de rojo y gastado.
        val vc = Offset(vx, py - pr * 2.75f)
        val rv = w * 0.034f
        translate(w * 0.008f, w * 0.006f) { drawOval(tv.s(0.25f), Offset(vc.x - rv, vc.y - rv * 0.32f), Size(rv * 2, rv * 0.64f), style = Stroke(2.6f * u)) }
        for (k in 0 until 4) {
            val a = k * Math.PI / 2 + 0.5
            drawLine(tv.c(mezcla(ROJO, HIERRO, 0.3f)), vc, Offset(vc.x + rv * cos(a).toFloat(), vc.y + rv * 0.32f * sin(a).toFloat()), 1.8f * u)
        }
        drawOval(tv.c(ROJO), Offset(vc.x - rv, vc.y - rv * 0.32f), Size(rv * 2, rv * 0.64f), style = Stroke(2.8f * u))
        drawArc(tv.b(0.6f), 180f, 120f, false, Offset(vc.x - rv, vc.y - rv * 0.32f), Size(rv * 2, rv * 0.64f), style = Stroke(1f * u))
        drawArc(tv.c(HIERRO).copy(alpha = 0.6f), 20f, 60f, false, Offset(vc.x - rv, vc.y - rv * 0.32f), Size(rv * 2, rv * 0.64f), style = Stroke(2.8f * u))
        drawCircle(tv.c(LATON_OSC), 2.2f * u, vc)
        destello(Offset(vx - w * 0.012f, py - pr * 0.6f), 1.4f * u, tv)
        // La gota y la chorrera en el ladrillo, hasta la moldura.
        val gx = vx + w * 0.004f
        val tg = tn(Offset(gx, w * 0.2f))
        drawPath(Path().apply {
            moveTo(gx - w * 0.004f, py + pr * 1.2f)
            cubicTo(gx - w * 0.008f, w * 0.15f, gx - w * 0.002f, w * 0.22f, gx - w * 0.006f, w * DADO)
            lineTo(gx + w * 0.012f, w * DADO)
            cubicTo(gx + w * 0.008f, w * 0.22f, gx + w * 0.01f, w * 0.15f, gx + w * 0.005f, py + pr * 1.2f)
            close()
        }, tg.c(HOLLIN).copy(alpha = 0.32f))
        drawLine(tg.c(mezcla(LADRILLO, VERDE, 0.4f)).copy(alpha = 0.5f), Offset(gx, py + pr * 1.4f), Offset(gx - w * 0.002f, w * 0.26f), 1.2f * u)
        val gota = Offset(gx, py + pr * 1.25f + w * 0.012f)
        drawPath(Path().apply { moveTo(gota.x, gota.y - w * 0.01f); cubicTo(gota.x + w * 0.005f, gota.y - w * 0.002f, gota.x + w * 0.005f, gota.y + w * 0.004f, gota.x, gota.y + w * 0.004f); cubicTo(gota.x - w * 0.005f, gota.y + w * 0.004f, gota.x - w * 0.005f, gota.y - w * 0.002f, gota.x, gota.y - w * 0.01f); close() },
            tg.c(mezcla(HUESO, PIZARRA, 0.35f)))
        destello(gota + Offset(-w * 0.0015f, -w * 0.001f), 0.8f * u, tg)
    }

    /** La regleta de madera con tres llaves fijas colgadas de sus clavijas, cada una de su largo y un poco ladeada. */
    private fun DrawScope.llaves(w: Float, sup: Float) {
        val u = density
        val reg = Rect(w * 0.02f, w * 0.13f, w * 0.215f, w * 0.148f)
        val t0 = tn(reg.center)
        sombraEnPared(Path().apply { addRect(reg) }, reg.center, sup, 0.24f)
        drawRect(t0.c(mezcla(CAOBA_LUZ, MOSTAZA, 0.3f)), reg.topLeft, reg.size)
        drawRect(t0.b(0.4f), reg.topLeft, Size(reg.width, 1f * u))
        drawRect(t0.s(0.3f), Offset(reg.left, reg.bottom - 1.2f * u), Size(reg.width, 1.2f * u))
        clavo(Offset(reg.left + 3f * u, reg.center.y), 1.3f * u, t0, HIERRO)
        clavo(Offset(reg.right - 3f * u, reg.center.y), 1.3f * u, t0, HIERRO)
        listOf(Triple(0.055f, 0.15f, -5f), Triple(0.115f, 0.12f, 4f), Triple(0.172f, 0.095f, -2f)).forEachIndexed { i, (fx, largo, giro) ->
            val clav = Offset(w * fx, reg.bottom - 1f * u)
            val t = tn(clav + Offset(0f, w * largo / 2))
            val rc = w * (0.016f - 0.002f * i)
            val l = w * largo
            val ancho = w * (0.01f - 0.0012f * i)
            val llave = Path().apply {
                addRoundRect(RoundRect(Rect(clav.x - ancho / 2, clav.y + rc * 0.6f, clav.x + ancho / 2, clav.y + l - rc * 0.6f), CornerRadius(ancho / 2)))
            }
            rotate(giro, clav) {
                sombraEnPared(llave, clav + Offset(0f, l / 2), sup, 0.3f)
                translate(w * 0.02f, w * 0.01f) {
                    if (!esNoche) {
                        drawArc(sombraMueble(0.25f), -50f, 280f, false, Offset(clav.x - rc, clav.y + rc * 0.15f - rc), Size(rc * 2, rc * 2), style = Stroke(rc * 0.75f))
                        drawArc(sombraMueble(0.25f), 130f, 280f, false, Offset(clav.x - rc * 0.85f, clav.y + l - rc * 0.85f), Size(rc * 1.7f, rc * 1.7f), style = Stroke(rc * 0.65f))
                    }
                }
                val acero = mezcla(HOJALATA, HIERRO, 0.35f + 0.15f * i)
                drawPath(llave, t.c(acero))
                clipPath(llave) {
                    drawRect(t.b(0.35f), Offset(clav.x - ancho / 2, clav.y), Size(ancho * 0.35f, l))
                    drawRect(t.s(0.3f), Offset(clav.x + ancho * 0.15f, clav.y), Size(ancho * 0.35f, l))
                }
                // Las bocas: la de arriba abraza la clavija, la de abajo abierta hacia abajo.
                drawArc(t.c(acero), -50f, 280f, false, Offset(clav.x - rc, clav.y + rc * 0.15f - rc), Size(rc * 2, rc * 2), style = Stroke(rc * 0.75f))
                drawArc(t.b(0.5f), 180f, 90f, false, Offset(clav.x - rc, clav.y + rc * 0.15f - rc), Size(rc * 2, rc * 2), style = Stroke(rc * 0.25f))
                drawArc(t.c(acero), 130f, 280f, false, Offset(clav.x - rc * 0.85f, clav.y + l - rc * 0.85f), Size(rc * 1.7f, rc * 1.7f), style = Stroke(rc * 0.65f))
                drawArc(t.s(0.35f), 0f, 120f, false, Offset(clav.x - rc * 0.85f, clav.y + l - rc * 0.85f), Size(rc * 1.7f, rc * 1.7f), style = Stroke(rc * 0.3f))
                // El óxido y la grasa.
                drawOval(t.c(mezcla(ROJO, CAOBA, 0.5f)).copy(alpha = 0.45f), Offset(clav.x - ancho * 0.4f, clav.y + l * 0.55f), Size(ancho * 0.8f, l * 0.12f))
            }
            clavo(clav, 1.5f * u, t0, LATON_OSC)
        }
    }

    /**
     * El tablón de avisos: un marco de pino barnizado con el fondo de tabla y, clavados con chinchetas, el horario de servicio
     * (el encabezado y cuatro columnas de renglones ilegibles), un aviso con su franja roja, un poco ladeado, y una papeleta
     * amarilla con la esquina levantada.
     */
    private fun DrawScope.tablon(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        sombraEnPared(Path().apply { addRect(r) }, r.center, w * DADO + w * 0.2f, 0.26f)
        val m = w * 0.009f
        drawRect(t.c(mezcla(CAOBA_LUZ, MOSTAZA, 0.35f)), r.topLeft, r.size)
        val dentro = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
        drawRect(t.c(mezcla(CAOBA_LUZ, HUESO, 0.25f)), dentro.topLeft, dentro.size)
        vetas(dentro, (if (esNoche) NOCHE else CAOBA).copy(alpha = 0.25f), 5500, 0.6f, vertical = true, grosor = 0.5f)
        // Agujeros de chinchetas viejas.
        for (k in 0 until 10) drawCircle(t.s(0.35f), 0.7f * u, Offset(dentro.left + dentro.width * azar(5501, k), dentro.top + dentro.height * azar(5502, k)))
        drawRect(t.s(0.3f), dentro.topLeft, Size(dentro.width, 2f * u))
        drawRect(t.s(0.2f), dentro.topLeft, Size(2f * u, dentro.height))
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(1f * u, r.height))
        drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.3f * u), Size(r.width, 1.3f * u))
        drawRect(t.s(0.3f), Offset(r.right - 1.3f * u, r.top), Size(1.3f * u, r.height))
        // El horario.
        val h = Rect(dentro.left + dentro.width * 0.05f, dentro.top + dentro.height * 0.07f, dentro.left + dentro.width * 0.55f, dentro.bottom - dentro.height * 0.05f)
        val th = tn(h.center)
        drawRect(th.s(0.25f), Offset(h.left + 1.5f * u, h.top + 2f * u), h.size)
        drawRect(th.c(HUESO), h.topLeft, h.size)
        drawRect(th.c(mezcla(HUESO, MOSTAZA, 0.25f)).copy(alpha = 0.6f), Offset(h.left, h.bottom - h.height * 0.25f), Size(h.width, h.height * 0.25f))
        drawRect(th.c(TINTA).copy(alpha = 0.75f), Offset(h.left + h.width * 0.12f, h.top + h.height * 0.05f), Size(h.width * 0.76f, h.height * 0.045f))
        drawRect(th.c(TINTA).copy(alpha = 0.4f), Offset(h.left + h.width * 0.25f, h.top + h.height * 0.12f), Size(h.width * 0.5f, h.height * 0.02f))
        for (cIdx in 0 until 4) {
            val x0 = h.left + h.width * (0.06f + 0.235f * cIdx)
            if (cIdx > 0) drawLine(th.c(TINTA).copy(alpha = 0.35f), Offset(x0 - h.width * 0.02f, h.top + h.height * 0.18f), Offset(x0 - h.width * 0.02f, h.bottom - h.height * 0.05f), 0.5f * u)
            for (rIdx in 0 until 13) {
                val y = h.top + h.height * (0.22f + 0.055f * rIdx)
                val l = h.width * (0.12f + 0.07f * azar(5510 + cIdx, rIdx))
                drawLine(th.c(TINTA).copy(alpha = 0.55f), Offset(x0, y), Offset(x0 + l, y), 0.7f * u)
            }
        }
        drawLine(th.c(TINTA).copy(alpha = 0.5f), Offset(h.left + h.width * 0.04f, h.top + h.height * 0.19f), Offset(h.right - h.width * 0.04f, h.top + h.height * 0.19f), 0.6f * u)
        drawRect(th.s(0.12f), Offset(h.right - h.width * 0.12f, h.top), Size(h.width * 0.12f, h.height))
        listOf(Offset(h.left + 3f * u, h.top + 3f * u), Offset(h.right - 3f * u, h.top + 3f * u)).forEach { clavo(it, 1.6f * u, th, ROJO) }
        // El aviso con su franja roja, ladeado.
        val a = Rect(dentro.left + dentro.width * 0.6f, dentro.top + dentro.height * 0.06f, dentro.right - dentro.width * 0.05f, dentro.top + dentro.height * 0.5f)
        val ta = tn(a.center)
        rotate(3f, Offset(a.center.x, a.top)) {
            drawRect(ta.s(0.25f), Offset(a.left + 1.5f * u, a.top + 2f * u), a.size)
            drawRect(ta.c(mezcla(HUESO, CARTULINA, 0.5f)), a.topLeft, a.size)
            drawRect(ta.c(ROJO), Offset(a.left, a.top + a.height * 0.08f), Size(a.width, a.height * 0.16f))
            for (rIdx in 0 until 5) drawLine(ta.c(TINTA).copy(alpha = 0.5f), Offset(a.left + a.width * 0.1f, a.top + a.height * (0.38f + 0.12f * rIdx)), Offset(a.left + a.width * (0.6f + 0.3f * azar(5520, rIdx)), a.top + a.height * (0.38f + 0.12f * rIdx)), 0.7f * u)
            clavo(Offset(a.center.x, a.top + 2.5f * u), 1.6f * u, ta, LATON)
        }
        // La papeleta amarilla, con la esquina de abajo levantada.
        val p = Rect(dentro.left + dentro.width * 0.62f, dentro.top + dentro.height * 0.58f, dentro.right - dentro.width * 0.08f, dentro.bottom - dentro.height * 0.08f)
        val tp = tn(p.center)
        rotate(-4f, p.center) {
            drawRect(tp.s(0.25f), Offset(p.left + 1.5f * u, p.top + 2f * u), p.size)
            val papel = poligono(p.left, p.top, p.right, p.top, p.right, p.bottom - p.height * 0.3f, p.right - p.width * 0.25f, p.bottom, p.left, p.bottom)
            drawPath(papel, tp.c(mezcla(MOSTAZA, HUESO, 0.55f)))
            drawPath(poligono(p.right, p.bottom - p.height * 0.3f, p.right - p.width * 0.25f, p.bottom, p.right - p.width * 0.2f, p.bottom - p.height * 0.24f), tp.c(mezcla(MOSTAZA, HUESO, 0.3f)))
            drawPath(poligono(p.right, p.bottom - p.height * 0.3f, p.right - p.width * 0.25f, p.bottom, p.right - p.width * 0.2f, p.bottom - p.height * 0.24f), tp.b(0.3f))
            for (rIdx in 0 until 3) drawLine(tp.c(TINTA).copy(alpha = 0.45f), Offset(p.left + p.width * 0.12f, p.top + p.height * (0.3f + 0.2f * rIdx)), Offset(p.left + p.width * 0.7f, p.top + p.height * (0.3f + 0.2f * rIdx)), 0.7f * u)
            clavo(Offset(p.left + p.width * 0.5f, p.top + 2.5f * u), 1.5f * u, tp, VERDE)
        }
    }

    /** La placa de una locomotora: latón fundido con su reborde, el fondo pintado de rojo, el número en relieve y dos pernos. */
    private fun DrawScope.placaLocomotora(r: Rect) {
        val u = density
        val t = tn(r.center)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.height * 0.35f))) }
        sombraEnPared(forma, r.center, anchoW * DADO, 0.3f)
        drawPath(forma, t.c(LATON))
        clipPath(forma) {
            drawRect(t.b(0.3f), r.topLeft, Size(r.width, r.height * 0.25f))
            drawRect(t.s(0.25f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
        }
        val dentro = Rect(r.left + r.height * 0.14f, r.top + r.height * 0.14f, r.right - r.height * 0.14f, r.bottom - r.height * 0.14f)
        drawRoundRect(t.c(mezcla(ROJO, CAOBA, 0.25f)), dentro.topLeft, dentro.size, CornerRadius(r.height * 0.24f))
        drawRoundRect(t.s(0.35f), dentro.topLeft, dentro.size, CornerRadius(r.height * 0.24f), style = Stroke(1.2f * u))
        drawRoundRect(t.b(0.45f), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, r.height - 1.6f * u), CornerRadius(r.height * 0.33f), style = Stroke(0.8f * u))
        // El número en relieve: su sombra abajo a la derecha y su filo de luz.
        val nr = Rect(dentro.left + dentro.width * 0.24f, dentro.top + dentro.height * 0.2f, dentro.right - dentro.width * 0.24f, dentro.bottom - dentro.height * 0.2f)
        translate(1f * u, 1.2f * u) { cifras("247", nr, t.s(0.5f), 2.6f * u) }
        cifras("247", nr, t.c(LATON), 2.4f * u)
        translate(-0.5f * u, -0.5f * u) { cifras("247", nr, t.b(0.35f), 0.8f * u) }
        clavo(Offset(dentro.left + dentro.width * 0.1f, r.center.y), 2f * u, t, LATON_OSC)
        clavo(Offset(dentro.right - dentro.width * 0.1f, r.center.y), 2f * u, t, LATON_OSC)
        destello(Offset(r.left + r.width * 0.2f, r.top + 1.8f * u), 1.2f * u, t)
    }

    /** El haz rojo de la lente del costado del farol: un abanico que tiñe el muro hacia la izquierda (solo de noche). */
    private fun DrawScope.haz(w: Float, sup: Float) {
        val l = LENTE_ROJA * w
        val tinte = Brush.radialGradient(listOf(ROJO_SENAL.copy(alpha = 0.17f), ROJO_SENAL.copy(alpha = 0.08f), Color.Transparent), l, w * 0.32f)
        clipRect(0f, 0f, w, sup) {
            listOf(0.17f, 0.12f, 0.07f).forEach { ab ->
                drawPath(poligono(l.x, l.y - w * 0.018f, -w * 0.02f, l.y - w * ab, -w * 0.02f, l.y + w * ab * 1.05f, l.x, l.y + w * 0.018f), tinte)
            }
        }
    }
    // ---------------------------------------------------------------- el casillero

    /** El ancho del marco de cada hueco, en fracción del ancho del mueble. */
    private const val MARCO = 0.013f

    private fun DrawScope.casillero(d: Disposicion, ch: Float, canto: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, canto, xR, b.top + hb * ZOCALO)

        // Su sombra en la pared (a la derecha) y en el suelo.
        val t0 = tn(Offset(xR, ch * 1.4f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.024f, ch * SUP + w * 0.03f, xR + w * 0.024f, suelo, xR, suelo), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.34f), Offset(xL + w * 0.02f, pisan - 2f * u), Size(xR - xL + w * 0.012f, hb * 0.06f))
        drawRect(tp.s(0.16f), Offset(xL + w * 0.03f, pisan + hb * 0.06f - 2f * u), Size(xR - xL, hb * 0.06f))

        // El frente de caoba.
        if (!o) drawRect(CAOBA, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.4f, cuerpo.top + cuerpo.height * i / 5f)).c(CAOBA) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.18f), Color.Transparent, sombraMueble(0.22f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent, NOCHE.copy(alpha = 0.4f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.35f), 5600, 0.55f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        pinceladas(cuerpo, if (o) NOCHE else CAOBA_LUZ, 5601, 24, u, 0.1f)

        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra. El tizne de las manos.
        for (k in 0 until 10) {
            val y0 = canto + cuerpo.height * k / 10f
            val t = tn(Offset(xL, y0 + cuerpo.height / 20f))
            drawRect(t.b(0.45f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f))
            val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
            drawRect(tr.c(CAOBA_OSC), Offset(xR - w * 0.009f, y0), Size(w * 0.009f, cuerpo.height / 10f))
        }
        listOf(xL + w * 0.012f, xR - w * 0.03f).forEachIndexed { i, x ->
            for (k in 0 until 5) {
                val y = canto + cuerpo.height * (0.1f + 0.18f * k + 0.06f * azar(5602 + i, k))
                drawOval(tn(Offset(x, y)).c(HOLLIN).copy(alpha = 0.32f), Offset(x, y), Size(w * 0.018f, w * 0.05f * (0.6f + 0.6f * azar(5603 + i, k))))
            }
        }

        // Los marcos de los huecos, con su grosor y su sombra, y el número esmaltado de cada casilla.
        val m = w * MARCO
        d.huecos.forEachIndexed { i, r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.3f), Offset(marco.left + 2.2f * u, marco.top + 2.6f * u), marco.size)
            drawRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.35f)), marco.topLeft, marco.size)
            drawRect(t.b(0.55f), marco.topLeft, Size(marco.width, 1.2f * u))
            drawRect(t.b(0.3f), marco.topLeft, Size(1.2f * u, marco.height))
            drawRect(t.s(0.35f), Offset(marco.left, marco.bottom - 1.4f * u), Size(marco.width, 1.4f * u))
            drawRect(t.s(0.28f), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
            // El número: un óvalo esmaltado de blanco con el número azul, en el larguero de encima, con sus dos clavitos.
            val tc = tn(Offset(r.center.x, r.top - d.separacion / 2))
            val nc = Offset(r.center.x, r.top - m - (d.separacion - m) * 0.48f)
            val ov = Rect(nc.x - w * 0.03f, nc.y - w * 0.0125f, nc.x + w * 0.03f, nc.y + w * 0.0125f)
            drawOval(tc.s(0.35f), ov.topLeft + Offset(1.2f * u, 1.4f * u), ov.size)
            drawOval(tc.c(mezcla(PIZARRA, HOLLIN, 0.3f)), ov.topLeft, ov.size)
            drawOval(if (o) tc.c(HUESO) else HUESO, Offset(ov.left + 1.5f * u, ov.top + 1.5f * u), Size(ov.width - 3f * u, ov.height - 3f * u))
            val txt = (i + 1).toString()
            val nw = ov.height * 0.42f * txt.length
            cifras(txt, Rect(nc.x - nw / 2, nc.y - ov.height * 0.26f, nc.x + nw / 2, nc.y + ov.height * 0.26f), tc.c(PIZARRA), 1.3f * u)
            // El esmalte saltado en el borde.
            if (i % 3 == 1) drawCircle(tc.c(HIERRO), 1.3f * u, Offset(ov.right - ov.width * 0.18f, ov.bottom - ov.height * 0.25f))
            drawArc(tc.b(0.6f), 200f, 70f, false, Offset(ov.left + 1.5f * u, ov.top + 1.5f * u), Size(ov.width - 3f * u, ov.height - 3f * u), style = Stroke(0.7f * u))
        }

        // El larguero de abajo (con el portatarjetas de la placa) y su moldura.
        val ultima = d.filas.last()
        val lar = Rect(xL, ultima.bottom + m + 1f * u, xR, cuerpo.bottom)
        val tl = tn(lar.center)
        drawRect(tl.s(0.25f), Offset(xL, lar.top), Size(xR - xL, 1.4f * u))
        drawRect(tl.b(0.3f), Offset(xL, lar.top + 1.4f * u), Size(xR - xL, 0.8f * u))
        val pr = d.enBase(placa)
        val portaR = Rect(pr.left - 2.5f * u, pr.top - 2.5f * u, pr.right + 2.5f * u, pr.bottom + 2.5f * u)
        drawRect(tl.s(0.3f), Offset(portaR.left + 1.2f * u, portaR.top + 1.5f * u), portaR.size)
        drawRect(tl.c(LATON), portaR.topLeft, portaR.size)
        drawRect(tl.b(0.55f), portaR.topLeft, Size(portaR.width, 0.9f * u))
        drawRect(tl.s(0.3f), Offset(portaR.left, portaR.bottom - 1f * u), Size(portaR.width, 1f * u))
        drawRect(tl.c(mezcla(CAOBA, CAOBA_OSC, 0.55f)), pr.topLeft, pr.size)
        drawRect(tl.s(0.3f), pr.topLeft, Size(pr.width, 2f * u))
        listOf(portaR.left + 1.4f * u, portaR.right - 1.4f * u).forEach { x -> drawCircle(tl.c(LATON_OSC), 0.9f * u, Offset(x, portaR.center.y)) }

        // Las cantoneras de latón en las cuatro esquinas, con sus tornillos.
        listOf(Offset(xL, canto) to 0, Offset(xR, canto) to 1, Offset(xL, cuerpo.bottom) to 2, Offset(xR, cuerpo.bottom) to 3).forEach { (p, i) ->
            val s = w * 0.05f
            val dx = if (i % 2 == 0) 1f else -1f
            val dy = if (i < 2) 1f else -1f
            val esc = poligono(p.x, p.y, p.x + dx * s, p.y, p.x + dx * s, p.y + dy * s * 0.28f, p.x + dx * s * 0.28f, p.y + dy * s * 0.28f, p.x + dx * s * 0.28f, p.y + dy * s, p.x, p.y + dy * s)
            val t = tn(p)
            translate(1.4f * u, 1.6f * u) { drawPath(esc, t.s(0.35f)) }
            drawPath(esc, t.c(LATON))
            clipPath(esc) { drawRect(t.b(0.35f), Offset(p.x - s, p.y - s), Size(s * 2, s * 0.65f + (if (dy > 0) s * 0.35f else 0f))) }
            listOf(Offset(0.62f, 0.14f), Offset(0.14f, 0.62f)).forEach { f -> clavo(Offset(p.x + dx * s * f.x, p.y + dy * s * f.y), 1.2f * u, t, LATON_OSC) }
        }

        // El zócalo, con su moldura y los golpes de las botas.
        val tz = tn(Offset(w * 0.5f, b.top + hb * ZOCALO))
        val zoc = Rect(xL - w * 0.008f, b.top + hb * ZOCALO, xR + w * 0.008f, pisan)
        drawRect(tz.c(mezcla(CAOBA, CAOBA_OSC, 0.45f)), zoc.topLeft, zoc.size)
        vetas(zoc, (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.4f), 5610, 0.6f, grosor = 0.5f)
        drawRect(tz.c(CAOBA_LUZ), zoc.topLeft, Size(zoc.width, zoc.height * 0.16f))
        drawRect(tz.b(0.5f), zoc.topLeft, Size(zoc.width, 1f * u))
        drawRect(tz.s(0.3f), Offset(zoc.left, zoc.top + zoc.height * 0.16f), Size(zoc.width, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(zoc.right - w * 0.012f, zoc.top), Size(w * 0.012f, zoc.height))
        for (k in 0 until 6) {
            val x = zoc.left + zoc.width * (0.12f + 0.76f * azar(5611, k))
            drawLine(tz.c(HOLLIN).copy(alpha = 0.5f), Offset(x, zoc.top + zoc.height * 0.45f), Offset(x + w * 0.03f, zoc.top + zoc.height * (0.5f + 0.3f * azar(5612, k))), 1.2f * u)
            drawLine(tz.b(0.25f), Offset(x + 1f * u, zoc.top + zoc.height * 0.4f), Offset(x + w * 0.025f, zoc.top + zoc.height * 0.48f), 0.6f * u)
        }
    }

    /**
     * La tapa del casillero: la tabla vista un poco desde arriba (con su veta, el cerco de aceite, rozaduras y polvo de carbón),
     * su canto con la moldura, la esquina golpeada, y la sombra de la cornisa en el friso.
     */
    private fun DrawScope.tapa(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ - w * 0.014f; val xR = w * DER + w * 0.014f
        val t = tn(Offset(w * 0.55f, sup))
        val tabla = Rect(xL, sup, xR, frente)
        if (!o) drawRect(mezcla(CAOBA_LUZ, CAOBA, 0.3f), tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> tn(Offset(xL + (xR - xL) * i / 5f, sup)).c(mezcla(CAOBA_LUZ, CAOBA, 0.3f)) }, xL, xR), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.28f), Color.Transparent, sombraMueble(0.14f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else CAOBA_OSC).copy(alpha = 0.32f), 5700, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.3f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        // El polvo de carbón y unas rozaduras.
        pinceladas(tabla, if (o) NOCHE else HOLLIN, 5701, 12, u, 0.14f)
        for (k in 0 until 5) {
            val x = w * (0.06f + 0.85f * azar(5702, k))
            drawLine(tn(Offset(x, sup)).b(0.3f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(5703, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(5704, k))), 0.6f * u)
        }
        // El canto: la moldura en dos planos (el de arriba recibe la luz).
        val m1 = frente + (canto - frente) * 0.45f
        val tramos = if (o) 28 else 1
        val at = (xR - xL) / tramos
        for (k in 0 until tramos) {
            val x0 = xL + at * k
            val tk = tn(Offset(x0 + at / 2, frente))
            drawRect(tk.c(CAOBA_LUZ), Offset(x0, frente), Size(at + 0.5f, m1 - frente))
            drawRect(tk.c(mezcla(CAOBA, CAOBA_OSC, 0.3f)), Offset(x0, m1), Size(at + 0.5f, canto - m1))
            drawRect(tk.b(0.7f), Offset(x0, frente), Size(at + 0.5f, 1.2f * u))
            drawRect(tk.b(0.3f), Offset(x0, m1 + 1.2f * u), Size(at + 0.5f, 0.8f * u))
        }
        drawRect(t.s(0.3f), Offset(xL, m1), Size(xR - xL, 0.9f * u))
        drawRect(t.s(0.35f), Offset(xL, canto - 1f * u), Size(xR - xL, 1f * u))
        // La esquina golpeada, arriba a la derecha: la madera viva que asoma.
        val tg = tn(Offset(xR, frente))
        drawPath(poligono(xR, frente, xR - w * 0.035f, frente, xR - w * 0.012f, m1 + (canto - m1) * 0.5f, xR, canto - 1f * u), tg.c(mezcla(CAOBA, CAOBA_LUZ, 0.55f)))
        drawPath(Path().apply { moveTo(xR - w * 0.035f, frente); lineTo(xR - w * 0.012f, m1 + (canto - m1) * 0.5f); lineTo(xR, canto - 1f * u) }, tg.s(0.4f), style = Stroke(0.7f * u))
        // La sombra de la cornisa en el frente.
        drawRect(t.s(0.32f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.4f * u))
        drawRect(t.s(0.14f), Offset(w * IZQ, canto + 2.4f * u), Size(w * (DER - IZQ), 3f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El farol de señales de mano, grande: el cuerpo redondo de chapa pintada con su puerta, sus bisagras, la costura de
     * remaches y la pintura gastada; el aro de la base; la lente de ojo de buey con su bisel de latón, el vidrio grueso y el
     * mechero que se adivina dentro; la lente roja del costado izquierdo; el tejadillo, la chimenea con sus respiraderos, el
     * sombrerete, el asa de alambre por encima y el asidero de atrás. De noche, la llama detrás de la lente, la lente roja
     * encendida y la luz que se escapa por los respiraderos.
     */
    private fun DrawScope.farol(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t0 = tn(Offset(pie.x, pie.y - w * 0.1f))
        // De noche el farol no se alumbra a sí mismo: su chapa queda a media luz y manda el filo.
        val t = if (o) Tono(true, t0.k * 0.6f, 0f, 0f) else t0
        val fw = w * 0.17f; val fh = w * 0.165f
        val cx = pie.x
        val aro = Rect(cx - fw * 0.56f, pie.y - w * 0.02f, cx + fw * 0.56f, pie.y)
        val cuerpo = Rect(cx - fw / 2, aro.top - fh, cx + fw / 2, aro.top + 1f * u)
        val yTej = cuerpo.top - w * 0.05f
        val tej = Path().apply {
            moveTo(cuerpo.left - fw * 0.05f, cuerpo.top + 1f * u)
            cubicTo(cuerpo.left + fw * 0.02f, cuerpo.top - w * 0.03f, cx - fw * 0.27f, yTej + w * 0.01f, cx - fw * 0.2f, yTej)
            lineTo(cx + fw * 0.2f, yTej)
            cubicTo(cx + fw * 0.27f, yTej + w * 0.01f, cuerpo.right - fw * 0.02f, cuerpo.top - w * 0.03f, cuerpo.right + fw * 0.05f, cuerpo.top + 1f * u)
            close()
        }
        val chim = Rect(cx - fw * 0.17f, yTej - w * 0.046f, cx + fw * 0.17f, yTej + 1f * u)
        val som = Path().apply {
            moveTo(cx - fw * 0.31f, chim.top + w * 0.005f)
            cubicTo(cx - fw * 0.3f, chim.top - w * 0.032f, cx + fw * 0.3f, chim.top - w * 0.032f, cx + fw * 0.31f, chim.top + w * 0.005f)
            close()
        }
        val silueta = Path().apply { addRect(aro); addRect(cuerpo); addPath(tej); addRect(chim); addPath(som) }
        if (!o) sombraEnPared(silueta, Offset(cx, pie.y - fh / 2), sup, 0.26f)
        contacto(Offset(cx + fw * 0.08f, pie.y), fw * 1.35f, 5f * u, 0.45f)
        val pintura = mezcla(VERDE, HOLLIN, 0.62f)
        // El asidero de atrás, a la derecha: una pletina doblada.
        val asidero = Path().apply {
            moveTo(cuerpo.right - 1f * u, cuerpo.top + fh * 0.2f)
            cubicTo(cuerpo.right + fw * 0.3f, cuerpo.top + fh * 0.16f, cuerpo.right + fw * 0.3f, cuerpo.top + fh * 0.74f, cuerpo.right - 1f * u, cuerpo.top + fh * 0.76f)
        }
        if (!o) translate(w * 0.02f, w * 0.01f) { clipRect(0f, 0f, size.width, sup) { drawPath(asidero, sombraMueble(0.2f), style = Stroke(w * 0.012f)) } }
        drawPath(asidero, t.c(pintura), style = Stroke(w * 0.014f, cap = StrokeCap.Round))
        drawPath(asidero, t.b(0.35f), style = Stroke(0.9f * u))
        // El asa de alambre, en arco por encima, un poco vencida a la derecha (va detrás de la chimenea).
        val a0 = Offset(cuerpo.left - fw * 0.01f, cuerpo.top + fh * 0.05f); val a1 = Offset(cuerpo.right + fw * 0.01f, cuerpo.top + fh * 0.05f)
        val cima = chim.top - w * 0.085f
        val asa = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x - fw * 0.06f, cima, a1.x + fw * 0.2f, cima, a1.x, a1.y) }
        if (!o) translate(w * 0.022f, w * 0.011f) { clipRect(0f, 0f, size.width, sup) { drawPath(asa, sombraMueble(0.2f), style = Stroke(1.8f * u)) } }
        drawPath(asa, t.c(HIERRO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.5f), style = Stroke(0.7f * u))
        // El aro de la base.
        val aroP = poligono(aro.left, aro.bottom, aro.left + fw * 0.05f, aro.top, aro.right - fw * 0.05f, aro.top, aro.right, aro.bottom)
        drawPath(aroP, t.c(mezcla(pintura, HOLLIN, 0.3f)))
        clipPath(aroP) { cilindro(aro, t, 1.5f) }
        drawRect(t.b(0.5f), Offset(aro.left + fw * 0.05f, aro.top), Size(aro.width - fw * 0.1f, 0.9f * u))
        // El cuerpo redondo.
        drawRect(t.c(pintura), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            cilindro(cuerpo, t, 1.5f)
            drawRect(t.b(0.2f), Offset(cuerpo.left + fw * 0.18f, cuerpo.top), Size(fw * 0.03f, fh))
            drawRect(t.b(0.1f), Offset(cuerpo.right - fw * 0.07f, cuerpo.top), Size(fw * 0.04f, fh))
            // La pintura gastada donde se agarra: asoma la chapa.
            listOf(Offset(0.07f, 0.72f), Offset(0.84f, 0.2f), Offset(0.14f, 0.1f), Offset(0.72f, 0.9f), Offset(0.3f, 0.93f)).forEachIndexed { k, f ->
                drawOval(t.c(HOJALATA).copy(alpha = 0.7f), Offset(cuerpo.left + fw * f.x, cuerpo.top + fh * f.y), Size(fw * (0.05f + 0.06f * azar(5800, k)), fh * 0.03f))
            }
            // La chorrera de aceite quemado que baja del tejadillo.
            drawRect(t.c(HOLLIN).copy(alpha = 0.4f), Offset(cuerpo.left + fw * 0.66f, cuerpo.top), Size(fw * 0.035f, fh * 0.3f))
            drawRect(t.c(HOLLIN).copy(alpha = 0.3f), Offset(cuerpo.left + fw * 0.72f, cuerpo.top), Size(fw * 0.02f, fh * 0.16f))
        }
        // La puerta de la lente: su junta, las dos bisagras de latón a la derecha y el pestillo a la izquierda.
        val puerta = Rect(cuerpo.left + fw * 0.11f, cuerpo.top + fh * 0.07f, cuerpo.right - fw * 0.07f, cuerpo.bottom - fh * 0.07f)
        drawRect(t.s(0.4f), puerta.topLeft, puerta.size, style = Stroke(1.1f * u))
        drawRect(t.b(0.25f), puerta.topLeft + Offset(1.1f * u, 1.1f * u), puerta.size, style = Stroke(0.7f * u))
        listOf(0.2f, 0.8f).forEach { f ->
            val by = puerta.top + puerta.height * f
            drawRect(t.s(0.35f), Offset(puerta.right - fw * 0.035f + 1f * u, by - fh * 0.035f + 1.2f * u), Size(fw * 0.07f, fh * 0.07f))
            drawRect(t.c(LATON_OSC), Offset(puerta.right - fw * 0.035f, by - fh * 0.035f), Size(fw * 0.07f, fh * 0.07f))
            drawRect(t.b(0.5f), Offset(puerta.right - fw * 0.035f, by - fh * 0.035f), Size(fw * 0.07f, 0.8f * u))
        }
        // La costura con sus remaches, a la izquierda, y los aros de refuerzo arriba y abajo.
        for (k in 0 until 7) clavo(Offset(cuerpo.left + fw * 0.055f, cuerpo.top + fh * (0.1f + 0.13f * k)), 1f * u, t, mezcla(pintura, HOJALATA, 0.45f))
        listOf(cuerpo.top + 1.5f * u, cuerpo.bottom - 4f * u).forEach { y ->
            drawRect(t.c(mezcla(pintura, HOLLIN, 0.4f)), Offset(cuerpo.left - 1.4f * u, y), Size(fw + 2.8f * u, 3.2f * u))
            drawRect(t.b(0.5f), Offset(cuerpo.left - 1.4f * u, y), Size(fw * 0.5f, 0.9f * u))
            drawRect(t.s(0.3f), Offset(cuerpo.left + fw * 0.6f, y), Size(fw * 0.4f + 1.4f * u, 3.2f * u))
        }
        // La lente roja del costado izquierdo: un tubo corto con su vidrio, visto de lado.
        val lcY = cuerpo.top + fh * 0.52f
        val lr = Rect(cuerpo.left - fw * 0.075f, lcY - fh * 0.2f, cuerpo.left + fw * 0.075f, lcY + fh * 0.2f)
        drawRect(t.c(LATON_OSC), Offset(lr.center.x, lr.top - 1.5f * u), Size(fw * 0.09f, lr.height + 3f * u))
        drawOval(t.s(0.35f), lr.topLeft + Offset(1.2f * u, 2f * u), Size(lr.width + 3f * u, lr.height + 3f * u))
        drawOval(t.c(LATON), lr.topLeft - Offset(1.8f * u, 1.8f * u), Size(lr.width + 3.6f * u, lr.height + 3.6f * u))
        drawOval(if (o) ROJO_SENAL else mezcla(ROJO, HOLLIN, 0.15f), lr.topLeft, lr.size)
        if (o) drawOval(Color(0xFFFFB9A2), Offset(lr.left + lr.width * 0.28f, lr.top + lr.height * 0.22f), Size(lr.width * 0.42f, lr.height * 0.5f))
        else {
            drawOval(mezcla(ROJO, HOLLIN, 0.5f), Offset(lr.left + lr.width * 0.35f, lr.top + lr.height * 0.3f), Size(lr.width * 0.5f, lr.height * 0.55f))
            drawOval(luzMueble(0.7f), Offset(lr.left + lr.width * 0.18f, lr.top + lr.height * 0.12f), Size(lr.width * 0.26f, lr.height * 0.26f))
        }
        // La lente de ojo de buey, con su bisel de latón.
        val lc = Offset(cx + fw * 0.02f, lcY)
        val rl = fw * 0.34f
        drawCircle(t.s(0.45f), rl * 1.24f, lc + Offset(1.8f * u, 2.6f * u))
        drawCircle(t0.c(LATON), rl * 1.24f, lc)
        drawCircle(t0.c(LATON_OSC), rl * 1.08f, lc)
        drawArc(t0.b(0.75f), 165f, 110f, false, Offset(lc.x - rl * 1.16f, lc.y - rl * 1.16f), Size(rl * 2.32f, rl * 2.32f), style = Stroke(1.8f * u))
        drawArc(t0.s(0.4f), -15f, 110f, false, Offset(lc.x - rl * 1.16f, lc.y - rl * 1.16f), Size(rl * 2.32f, rl * 2.32f), style = Stroke(1.8f * u))
        for (k in 0 until 4) { val ang = Math.toRadians(45.0 + 90.0 * k); clavo(Offset(lc.x + rl * 1.16f * cos(ang).toFloat(), lc.y + rl * 1.16f * sin(ang).toFloat()), 0.9f * u, t0, LATON_OSC) }
        if (o) {
            drawCircle(LLAMA, rl, lc)
            drawCircle(Color(0xFFFFE2A8), rl * 0.74f, lc)
            drawCircle(Color(0xFFFFF6E0), rl * 0.42f, lc)
            listOf(0.56f, 0.84f).forEach { f -> drawCircle(Color(0xFFFFB050).copy(alpha = 0.6f), rl * f, lc, style = Stroke(1.1f * u)) }
            drawCircle(Color(0xFFFFE0A0).copy(alpha = 0.85f), rl * 1.24f, lc, style = Stroke(1.1f * u))
        } else {
            drawCircle(mezcla(PIZARRA, HOJALATA, 0.6f), rl, lc)
            drawCircle(mezcla(HOLLIN, PIZARRA, 0.3f), rl * 0.66f, lc + Offset(rl * 0.05f, rl * 0.07f))
            // El mechero y su mecha, que se adivinan detrás del vidrio.
            drawRect(mezcla(LATON_OSC, HOLLIN, 0.7f), Offset(lc.x - rl * 0.16f, lc.y + rl * 0.26f), Size(rl * 0.38f, rl * 0.22f))
            drawRect(mezcla(HOJALATA, HOLLIN, 0.7f), Offset(lc.x - rl * 0.02f, lc.y + rl * 0.12f), Size(rl * 0.1f, rl * 0.14f))
            listOf(0.66f, 0.84f).forEach { f -> drawCircle(luzMueble(0.22f), rl * f, lc, style = Stroke(1f * u)) }
            drawArc(sombraMueble(0.3f), -20f, 130f, false, Offset(lc.x - rl * 0.9f, lc.y - rl * 0.9f), Size(rl * 1.8f, rl * 1.8f), style = Stroke(rl * 0.2f))
            drawArc(luzMueble(0.9f), 195f, 75f, false, Offset(lc.x - rl * 0.78f, lc.y - rl * 0.78f), Size(rl * 1.56f, rl * 1.56f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
            drawCircle(luzMueble(0.95f), rl * 0.1f, lc + Offset(-rl * 0.36f, -rl * 0.44f))
            drawArc(luzMueble(0.4f), 25f, 60f, false, Offset(lc.x - rl * 0.86f, lc.y - rl * 0.86f), Size(rl * 1.72f, rl * 1.72f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
        // El tejadillo, la chimenea con sus respiraderos y el sombrerete.
        drawPath(tej, t.c(mezcla(pintura, HOLLIN, 0.2f)))
        clipPath(tej) {
            cilindro(Rect(cuerpo.left - fw * 0.05f, yTej, cuerpo.right + fw * 0.05f, cuerpo.top + 1f * u), t, 1.15f)
            drawRect(t.c(HOLLIN).copy(alpha = 0.35f), Offset(cx - fw * 0.2f, yTej), Size(fw * 0.4f, w * 0.014f))
        }
        drawRect(t.c(pintura), chim.topLeft, chim.size)
        clipRect(chim.left, chim.top, chim.right, chim.bottom) { cilindro(chim, t, 1.2f) }
        for (k in 0 until 4) {
            val c = Offset(chim.left + chim.width * (0.17f + 0.22f * k), chim.top + chim.height * 0.6f)
            drawOval(if (o) LLAMA.copy(alpha = 0.9f) else t.c(HOLLIN), Offset(c.x - 1.3f * u, c.y - 2.2f * u), Size(2.6f * u, 4.4f * u))
        }
        drawRect(t.s(0.3f), Offset(chim.left, chim.top + w * 0.005f), Size(chim.width, 2f * u))
        drawPath(som, t.c(mezcla(pintura, HOLLIN, 0.25f)))
        clipPath(som) { cilindro(Rect(cx - fw * 0.31f, chim.top - w * 0.03f, cx + fw * 0.31f, chim.top + w * 0.005f), t, 1.15f) }
        drawRect(t.s(0.35f), Offset(cx - fw * 0.31f, chim.top + w * 0.003f), Size(fw * 0.62f, 1.3f * u))
        if (o) drawRect(LLAMA.copy(alpha = 0.6f), Offset(chim.left, chim.top + w * 0.006f), Size(chim.width, 1.2f * u))
        drawCircle(t.c(mezcla(pintura, HOJALATA, 0.2f)), w * 0.007f, Offset(cx, chim.top - w * 0.022f))
        // Las orejas del asa.
        listOf(a0, a1).forEach { p -> drawCircle(t.c(mezcla(pintura, HOLLIN, 0.3f)), 3f * u, p); drawCircle(t.c(HOJALATA), 1.2f * u, p) }
    }

    /**
     * La aceitera de pico largo, de cobre: el cuerpo cónico con su aro de la base, la tapa abombada con el botón de la bomba,
     * el asa de atrás y el pico largo y fino en diagonal, con su gota de aceite y el cerco en la tapa.
     */
    private fun DrawScope.aceitera(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.03f))
        val der = luzDerecha(pie.x)
        val bw = w * 0.108f; val tw = w * 0.04f; val bh = w * 0.076f
        val cuerpo = poligono(pie.x - bw / 2, pie.y, pie.x - tw / 2, pie.y - bh, pie.x + tw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val pico0 = Offset(pie.x + tw * 0.2f, pie.y - bh - w * 0.006f)
        val pico1 = Offset(pie.x + w * 0.178f, pie.y - bh - w * 0.1f)
        val picoP = Path().apply { moveTo(pico0.x, pico0.y); cubicTo(pico0.x + w * 0.05f, pico0.y - w * 0.036f, pico1.x - w * 0.06f, pico1.y + w * 0.06f, pico1.x, pico1.y) }
        // El cerco de aceite y la sombra.
        drawOval(t.c(HOLLIN).copy(alpha = 0.35f), Offset(pie.x - bw * 0.3f, pie.y - w * 0.004f), Size(bw * 1.3f, w * 0.01f))
        if (!esNoche) {
            sombraEnPared(cuerpo, Offset(pie.x, pie.y - bh / 2), sup, 0.2f)
            translate(w * 0.022f, w * 0.011f) { clipRect(0f, 0f, size.width, sup) { drawPath(picoP, sombraMueble(0.18f), style = Stroke(1.8f * u)) } }
        }
        contacto(Offset(pie.x + bw * 0.08f, pie.y), bw * 1.25f, 4f * u, 0.42f)
        // El asa de atrás, a la izquierda.
        val asa = Path().apply { moveTo(pie.x - tw * 0.6f, pie.y - bh * 0.9f); cubicTo(pie.x - bw * 0.95f, pie.y - bh * 1.1f, pie.x - bw * 0.95f, pie.y - bh * 0.3f, pie.x - bw * 0.42f, pie.y - bh * 0.22f) }
        drawPath(asa, t.c(mezcla(COBRE, CAOBA_OSC, 0.3f)), style = Stroke(w * 0.007f, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.4f), style = Stroke(0.7f * u))
        // El cuerpo.
        drawPath(cuerpo, t.c(COBRE))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y), t, 1.6f, der)
            drawLine(t.s(0.3f), Offset(pie.x - bw / 2, pie.y - bh * 0.35f), Offset(pie.x + bw / 2, pie.y - bh * 0.35f), 0.8f * u)
            drawLine(t.b(0.4f), Offset(pie.x - bw / 2, pie.y - bh * 0.35f + 1f * u), Offset(pie.x + bw / 2, pie.y - bh * 0.35f + 1f * u), 0.6f * u)
            // Una abolladura y la pátina.
            drawOval(t.s(0.25f), Offset(pie.x + bw * 0.08f, pie.y - bh * 0.7f), Size(bw * 0.14f, bh * 0.2f))
            drawOval(t.c(mezcla(COBRE, VERDE, 0.5f)).copy(alpha = 0.4f), Offset(pie.x - bw * 0.4f, pie.y - bh * 0.2f), Size(bw * 0.25f, bh * 0.12f))
        }
        drawRect(t.c(mezcla(COBRE, CAOBA_OSC, 0.25f)), Offset(pie.x - bw / 2, pie.y - w * 0.006f), Size(bw, w * 0.006f))
        drawRect(t.b(0.5f), Offset(pie.x - bw / 2, pie.y - w * 0.006f), Size(bw, 0.8f * u))
        // La tapa abombada, con el botón de la bomba.
        drawArc(t.c(LATON), 180f, 180f, true, Offset(pie.x - tw * 0.62f, pie.y - bh - w * 0.012f), Size(tw * 1.24f, w * 0.024f))
        drawArc(t.b(0.6f), 200f, 60f, false, Offset(pie.x - tw * 0.5f, pie.y - bh - w * 0.01f), Size(tw, w * 0.02f), style = Stroke(1f * u))
        drawRect(t.c(LATON_OSC), Offset(pie.x - w * 0.004f, pie.y - bh - w * 0.026f), Size(w * 0.008f, w * 0.016f))
        drawCircle(t.c(LATON), w * 0.007f, Offset(pie.x, pie.y - bh - w * 0.028f))
        destello(Offset(pie.x - w * 0.002f, pie.y - bh - w * 0.03f), 1.1f * u, t)
        // El pico largo.
        drawPath(picoP, t.c(mezcla(COBRE, CAOBA_OSC, 0.15f)), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        drawPath(picoP, t.b(0.55f), style = Stroke(0.8f * u, cap = StrokeCap.Round))
        drawCircle(t.c(LATON), 2.4f * u, pico0)
        val gota = pico1 + Offset(w * 0.002f, w * 0.008f)
        drawOval(t.c(mezcla(MOSTAZA, CAOBA, 0.4f)), Offset(gota.x - 1.2f * u, gota.y - 1.6f * u), Size(2.4f * u, 3.2f * u))
        destello(gota - Offset(0.4f * u, 0.6f * u), 0.6f * u, t)
    }

    /** El manómetro suelto, apoyado en la pared: el aro de latón, la esfera con su escala, la marca roja, la aguja, el racor y el sifón de cobre enroscado. */
    private fun DrawScope.manometroSuelto(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val r = w * 0.043f
        val c = Offset(pie.x, pie.y - w * 0.072f)
        val t = tn(c)
        val der = luzDerecha(c.x)
        val sifon = Path().apply {
            moveTo(c.x, c.y + r * 1.4f)
            cubicTo(c.x, c.y + r * 1.9f, c.x - r * 1.25f, c.y + r * 1.8f, c.x - r * 1.1f, c.y + r * 1.35f)
            cubicTo(c.x - r * 0.95f, c.y + r * 0.9f, c.x - r * 0.25f, c.y + r * 1.05f, c.x - r * 0.4f, pie.y - w * 0.004f)
            lineTo(c.x + r * 1.6f, pie.y - w * 0.004f)
        }
        sombraEnPared(Path().apply { addOval(Rect(c.x - r * 1.1f, c.y - r * 1.1f, c.x + r * 1.1f, c.y + r * 1.1f)) }, c, sup, 0.24f)
        contacto(Offset(c.x + r * 0.3f, pie.y), r * 3.6f, 4f * u, 0.38f)
        // El racor y el sifón.
        drawRect(t.c(LATON_OSC), Offset(c.x - r * 0.14f, c.y + r * 0.95f), Size(r * 0.28f, r * 0.5f))
        drawRect(t.c(LATON), Offset(c.x - r * 0.24f, c.y + r * 1.15f), Size(r * 0.48f, r * 0.22f))
        drawPath(sifon, t.s(0.3f), style = Stroke(2.8f * u, cap = StrokeCap.Round))
        drawPath(sifon, t.c(COBRE), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(sifon, t.b(0.55f), style = Stroke(0.7f * u, cap = StrokeCap.Round))
        // El aro y la esfera.
        drawCircle(t.c(LATON), r * 1.1f, c)
        drawArc(t.b(0.65f), 170f, 110f, false, Offset(c.x - r * 1.03f, c.y - r * 1.03f), Size(r * 2.06f, r * 2.06f), style = Stroke(1.5f * u))
        drawArc(t.s(0.4f), -10f, 110f, false, Offset(c.x - r * 1.03f, c.y - r * 1.03f), Size(r * 2.06f, r * 2.06f), style = Stroke(1.5f * u))
        drawCircle(t.c(mezcla(HUESO, MOSTAZA, 0.12f)), r * 0.86f, c)
        drawArc(t.s(0.25f), -40f, 140f, false, Offset(c.x - r * 0.86f, c.y - r * 0.86f), Size(r * 1.72f, r * 1.72f), style = Stroke(r * 0.18f))
        // La escala, la marca roja de la presión de servicio y la aguja.
        for (k in 0..12) {
            val a = Math.toRadians(135.0 + k * 22.5)
            val f0 = if (k % 3 == 0) 0.56f else 0.64f
            drawLine(t.c(TINTA).copy(alpha = 0.75f), Offset(c.x + r * f0 * cos(a).toFloat(), c.y + r * f0 * sin(a).toFloat()), Offset(c.x + r * 0.74f * cos(a).toFloat(), c.y + r * 0.74f * sin(a).toFloat()), (if (k % 3 == 0) 1.2f else 0.6f) * u)
        }
        drawArc(t.c(ROJO), 135f + 270f * 0.78f, 270f * 0.08f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(2f * u))
        val a = Math.toRadians(135.0 + 270.0 * 0.12)
        drawLine(t.c(TINTA), c, Offset(c.x + r * 0.66f * cos(a).toFloat(), c.y + r * 0.66f * sin(a).toFloat()), 1.4f * u, StrokeCap.Round)
        drawCircle(t.c(LATON_OSC), r * 0.1f, c)
        // El vidrio: un reflejo.
        drawArc(if (esNoche) t.b(0.7f) else luzMueble(0.7f), if (der) 290f else 200f, 50f, false, Offset(c.x - r * 0.7f, c.y - r * 0.7f), Size(r * 1.4f, r * 1.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        destello(Offset(c.x - r * 0.75f, c.y - r * 0.6f), 1.2f * u, t)
    }

    /**
     * La gorra de maquinista, de sarga azul pizarra, dejada sobre la tapa: la copa blanda y caída hacia atrás, con su plato
     * visto un poco desde arriba, la cinta, la visera de charol hacia quien mira y las manchas de grasa de las manos.
     */
    private fun DrawScope.gorra(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val gw = w * 0.17f; val gh = w * 0.078f
        val t = tn(Offset(pie.x, pie.y - gh / 2))
        val der = luzDerecha(pie.x)
        rotate(-3f, pie) {
            val izq = pie.x - gw / 2; val dcha = pie.x + gw / 2
            val cima = pie.y - gh
            // La copa: más ancha que la cinta, blanda, con un hundido arriba.
            val copa = Path().apply {
                moveTo(izq + gw * 0.12f, pie.y - gh * 0.3f)
                cubicTo(izq - gw * 0.05f, pie.y - gh * 0.42f, izq - gw * 0.02f, cima + gh * 0.12f, izq + gw * 0.22f, cima + gh * 0.03f)
                cubicTo(izq + gw * 0.42f, cima - gh * 0.04f, dcha - gw * 0.4f, cima + gh * 0.1f, dcha - gw * 0.18f, cima + gh * 0.08f)
                cubicTo(dcha + gw * 0.04f, cima + gh * 0.16f, dcha + gw * 0.05f, pie.y - gh * 0.4f, dcha - gw * 0.1f, pie.y - gh * 0.28f)
                close()
            }
            val cinta = Path().apply {
                moveTo(izq + gw * 0.11f, pie.y - gh * 0.34f)
                cubicTo(izq + gw * 0.3f, pie.y - gh * 0.22f, dcha - gw * 0.3f, pie.y - gh * 0.2f, dcha - gw * 0.09f, pie.y - gh * 0.32f)
                lineTo(dcha - gw * 0.11f, pie.y - gh * 0.04f)
                cubicTo(dcha - gw * 0.3f, pie.y + gh * 0.07f, izq + gw * 0.3f, pie.y + gh * 0.05f, izq + gw * 0.13f, pie.y - gh * 0.07f)
                close()
            }
            val silueta = Path().apply { addPath(copa); addPath(cinta) }
            sombraEnPared(silueta, pie, sup, 0.24f)
            contacto(Offset(pie.x + gw * 0.05f, pie.y), gw * 1.1f, 5f * u, 0.42f)
            val sarga = PIZARRA
            drawPath(copa, t.c(sarga))
            clipPath(copa) {
                // El plato de la copa, que mira arriba y recibe la luz; el lado en sombra y el hundido.
                drawOval(t.b(0.2f), Offset(if (der) pie.x - gw * 0.1f else izq + gw * 0.04f, cima - gh * 0.05f), Size(gw * 0.62f, gh * 0.42f))
                drawRect(t.s(0.3f), Offset(if (der) izq else pie.x + gw * 0.22f, cima), Size(gw * 0.32f, gh))
                drawOval(t.s(0.26f), Offset(pie.x - gw * 0.12f, cima + gh * 0.12f), Size(gw * 0.36f, gh * 0.16f))
                // El vuelo de la copa sobre la cinta: su sombra por debajo.
                drawPath(Path().apply { moveTo(izq, pie.y - gh * 0.42f); cubicTo(izq + gw * 0.3f, pie.y - gh * 0.26f, dcha - gw * 0.3f, pie.y - gh * 0.24f, dcha, pie.y - gh * 0.4f) }, t.s(0.3f), style = Stroke(gh * 0.14f))
                // Las costuras de los gajos y la grasa de las manos.
                drawLine(t.s(0.25f), Offset(pie.x - gw * 0.02f, cima + gh * 0.06f), Offset(pie.x - gw * 0.1f, pie.y - gh * 0.3f), 0.7f * u)
                drawLine(t.s(0.2f), Offset(pie.x + gw * 0.2f, cima + gh * 0.1f), Offset(pie.x + gw * 0.26f, pie.y - gh * 0.32f), 0.7f * u)
                drawOval(t.c(HOLLIN).copy(alpha = 0.45f), Offset(izq + gw * 0.1f, cima + gh * 0.32f), Size(gw * 0.16f, gh * 0.2f))
                drawOval(t.c(HOLLIN).copy(alpha = 0.3f), Offset(pie.x + gw * 0.16f, cima + gh * 0.24f), Size(gw * 0.1f, gh * 0.16f))
            }
            // La cinta, más oscura, con su botón.
            drawPath(cinta, t.c(mezcla(sarga, HOLLIN, 0.5f)))
            clipPath(cinta) {
                drawRect(t.b(0.18f), Offset(if (der) dcha - gw * 0.4f else izq + gw * 0.12f, pie.y - gh * 0.4f), Size(gw * 0.2f, gh * 0.5f))
                drawRect(t.s(0.25f), Offset(if (der) izq else dcha - gw * 0.3f, pie.y - gh * 0.4f), Size(gw * 0.3f, gh * 0.5f))
            }
            clavo(Offset(izq + gw * 0.17f, pie.y - gh * 0.19f), 1.3f * u, t, LATON)
            // La visera de charol: una media luna hacia quien mira, con su brillo.
            val vis = Path().apply {
                moveTo(izq + gw * 0.16f, pie.y - gh * 0.1f)
                cubicTo(izq + gw * 0.12f, pie.y + gh * 0.3f, pie.x + gw * 0.2f, pie.y + gh * 0.36f, pie.x + gw * 0.3f, pie.y - gh * 0.02f)
                cubicTo(pie.x + gw * 0.12f, pie.y + gh * 0.08f, izq + gw * 0.3f, pie.y + gh * 0.04f, izq + gw * 0.16f, pie.y - gh * 0.1f)
                close()
            }
            translate(if (der) -1.5f * u else 1.5f * u, 2f * u) { drawPath(vis, t.s(0.35f)) }
            drawPath(vis, t.c(mezcla(HOLLIN, PIZARRA, 0.15f)))
            drawPath(Path().apply { moveTo(izq + gw * 0.2f, pie.y + gh * 0.1f); cubicTo(izq + gw * 0.26f, pie.y + gh * 0.25f, pie.x + gw * 0.05f, pie.y + gh * 0.27f, pie.x + gw * 0.16f, pie.y + gh * 0.17f) },
                if (esNoche) t.b(0.8f) else luzMueble(0.4f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            if (esNoche) drawPath(Path().apply { moveTo(izq + gw * 0.06f, cima + gh * 0.3f); cubicTo(izq + gw * 0.1f, cima + gh * 0.02f, izq + gw * 0.4f, cima - gh * 0.02f, pie.x, cima + gh * 0.05f) }, t.b(0.6f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        }
    }

    /**
     * El reloj de bolsillo abierto, de latón: la caja tumbada con su esfera de esmalte, las agujas, la tapa levantada detrás
     * con sus anillos grabados, la corona y la cadena que cruza la tapa y cuelga por el canto.
     */
    private fun DrawScope.reloj(pie: Offset, frente: Float, canto: Float) {
        val u = density
        val w = anchoW
        val t = tn(pie)
        val rw = w * 0.052f; val rh = w * 0.022f
        val caja = Rect(pie.x - rw / 2, pie.y - rh, pie.x + rw / 2, pie.y)
        // La cadena: de la corona, por la tapa, y cuelga por el canto.
        val corona = Offset(caja.left - w * 0.004f, caja.center.y)
        val cadena = Path().apply {
            moveTo(corona.x, corona.y)
            cubicTo(corona.x - w * 0.04f, corona.y + w * 0.004f, corona.x - w * 0.05f, frente - w * 0.002f, corona.x - w * 0.035f, frente + 1f * u)
            cubicTo(corona.x - w * 0.03f, canto + w * 0.03f, corona.x - w * 0.005f, canto + w * 0.045f, corona.x + w * 0.004f, canto + w * 0.02f)
        }
        if (!esNoche) translate(w * 0.004f, w * 0.004f) { drawPath(cadena, sombraMueble(0.3f), style = Stroke(1.6f * u)) }
        drawPath(cadena, t.c(LATON_OSC), style = Stroke(1.9f * u, cap = StrokeCap.Round))
        drawPath(cadena, t.c(LATON), style = Stroke(1.2f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))))
        drawCircle(t.c(LATON), 2f * u, Offset(corona.x + w * 0.004f, canto + w * 0.02f), style = Stroke(1f * u))
        // La tapa levantada, detrás.
        val tc = Offset(pie.x + w * 0.002f, caja.top - w * 0.018f)
        val rt = w * 0.024f
        drawCircle(t.s(0.3f), rt, tc + Offset(w * 0.004f, w * 0.003f))
        drawCircle(t.c(mezcla(LATON, LATON_OSC, 0.2f)), rt, tc)
        drawCircle(t.c(LATON_LUZ).copy(alpha = 0.5f), rt * 0.78f, tc, style = Stroke(0.7f * u))
        drawCircle(t.s(0.25f), rt * 0.55f, tc, style = Stroke(0.6f * u))
        drawArc(t.b(0.6f), 200f, 70f, false, Offset(tc.x - rt * 0.9f, tc.y - rt * 0.9f), Size(rt * 1.8f, rt * 1.8f), style = Stroke(1.2f * u))
        // La caja y la esfera, vistas desde arriba.
        contacto(Offset(pie.x + w * 0.005f, pie.y), rw * 1.3f, 3.5f * u, 0.4f)
        drawOval(t.c(LATON_OSC), Offset(caja.left, caja.top + rh * 0.2f), caja.size)
        drawOval(t.c(LATON), caja.topLeft, caja.size)
        val esf = Rect(caja.left + rw * 0.1f, caja.top + rh * 0.12f, caja.right - rw * 0.1f, caja.bottom - rh * 0.12f)
        drawOval(t.c(HUESO), esf.topLeft, esf.size)
        for (k in 0 until 12) {
            val a = k * Math.PI / 6
            val p = Offset(esf.center.x + esf.width * 0.4f * cos(a).toFloat(), esf.center.y + esf.height * 0.4f * sin(a).toFloat())
            drawCircle(t.c(TINTA).copy(alpha = 0.6f), 0.5f * u, p)
        }
        drawLine(t.c(TINTA), esf.center, esf.center + Offset(-esf.width * 0.2f, -esf.height * 0.2f), 0.9f * u)
        drawLine(t.c(TINTA), esf.center, esf.center + Offset(esf.width * 0.3f, -esf.height * 0.08f), 0.7f * u)
        drawOval(if (esNoche) t.b(0.6f) else luzMueble(0.6f), Offset(esf.left + esf.width * 0.15f, esf.top + esf.height * 0.1f), Size(esf.width * 0.3f, esf.height * 0.25f))
        // La corona.
        drawCircle(t.c(LATON), 1.8f * u, corona)
        drawRect(t.c(LATON_OSC), Offset(corona.x, corona.y - 0.8f * u), Size(w * 0.004f, 1.6f * u))
        destello(Offset(caja.left + rw * 0.15f, caja.top + rh * 0.15f), 1f * u, t)
    }

    /**
     * La lata del té del fogonero (el termo de vacío no existía en 1880): un bote de hojalata con sus aros, el hombro, el
     * cuello y la tapa que hace de taza, el asa de alambre caída, las abolladuras y el fondo tiznado de calentarlo en la pala.
     */
    private fun DrawScope.lataDelTe(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val tw = w * 0.074f; val th = w * 0.098f
        val r = Rect(pie.x - tw / 2, pie.y - th, pie.x + tw / 2, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        val yH = r.top - w * 0.02f
        val hombro = poligono(r.left, r.top + 1f * u, pie.x - tw * 0.3f, yH, pie.x + tw * 0.3f, yH, r.right, r.top + 1f * u)
        val taza = Rect(pie.x - tw * 0.36f, yH - w * 0.034f, pie.x + tw * 0.36f, yH + w * 0.004f)
        sombraEnPared(Path().apply { addRect(r); addPath(hombro); addRect(taza) }, r.center, sup, 0.24f)
        contacto(Offset(pie.x + tw * 0.08f, pie.y), tw * 1.4f, 4.5f * u, 0.42f)
        val lata = mezcla(HOJALATA, PIZARRA, 0.22f)
        drawRect(t.c(lata), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            cilindro(r, t, 1.9f, der)
            listOf(0.08f, 0.5f, 0.86f).forEach { f -> drawRect(t.s(0.26f), Offset(r.left, r.top + th * f), Size(tw, 1.3f * u)); drawRect(t.b(0.35f), Offset(r.left, r.top + th * f + 1.3f * u), Size(tw, 0.9f * u)) }
            // La costura soldada, una abolladura y el tizne del fondo.
            drawRect(t.s(0.18f), Offset(r.left + tw * 0.68f, r.top), Size(1.2f * u, th))
            drawOval(t.s(0.28f), Offset(r.left + tw * 0.38f, r.top + th * 0.22f), Size(tw * 0.26f, th * 0.16f))
            drawOval(t.b(0.3f), Offset(r.left + tw * 0.36f, r.top + th * 0.2f), Size(tw * 0.14f, th * 0.06f))
            drawRect(t.c(HOLLIN).copy(alpha = 0.55f), Offset(r.left, r.bottom - th * 0.14f), Size(tw, th * 0.14f))
            drawRect(t.c(HOLLIN).copy(alpha = 0.25f), Offset(r.left, r.bottom - th * 0.28f), Size(tw, th * 0.14f))
            drawOval(t.c(mezcla(ROJO, CAOBA, 0.5f)).copy(alpha = 0.4f), Offset(r.left + tw * 0.1f, r.top + th * 0.6f), Size(tw * 0.18f, th * 0.1f))
        }
        drawPath(hombro, t.c(mezcla(lata, HOLLIN, 0.12f)))
        clipPath(hombro) { cilindro(Rect(r.left, yH, r.right, r.top + 1f * u), t, 1.8f, der) }
        // La tapa, que hace de taza.
        drawRect(t.c(mezcla(lata, LATON, 0.18f)), taza.topLeft, taza.size)
        clipRect(taza.left, taza.top, taza.right, taza.bottom) { cilindro(taza, t, 1.8f, der) }
        drawRect(t.b(0.65f), taza.topLeft, Size(taza.width, 1f * u))
        drawRect(t.s(0.38f), Offset(taza.left, taza.bottom - 1.3f * u), Size(taza.width, 1.3f * u))
        drawRect(t.s(0.25f), Offset(taza.left, taza.top + taza.height * 0.3f), Size(taza.width, 0.9f * u))
        // El asa de alambre, caída por delante.
        val a0 = Offset(r.left + 1f * u, r.top + th * 0.05f); val a1 = Offset(r.right - 1f * u, r.top + th * 0.05f)
        val asa = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x - tw * 0.3f, r.top + th * 0.52f, a1.x - tw * 0.05f, r.top + th * 0.62f, a1.x, a1.y) }
        translate(if (der) -1.2f * u else 1.2f * u, 1.6f * u) { clipRect(r.left, r.top, r.right, r.bottom) { drawPath(asa, t.s(0.3f), style = Stroke(1.5f * u)) } }
        drawPath(asa, t.c(HIERRO), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.45f), style = Stroke(0.6f * u))
        listOf(a0, a1).forEach { p -> drawCircle(t.c(mezcla(lata, HOLLIN, 0.3f)), 1.8f * u, p) }
        destello(Offset(if (der) taza.right - tw * 0.16f else taza.left + tw * 0.16f, taza.top + 1.8f * u), 1.1f * u, t)
        filo(r, t, der)
    }
    /** La fiambrera de hojalata, tumbada delante del termo: la caja de esquinas redondas, la tapa con su pestaña y un golpe de óxido. */
    private fun DrawScope.fiambrera(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val fw = w * 0.11f; val fh = w * 0.04f
        val r = Rect(pie.x - fw / 2, pie.y - fh, pie.x + fw / 2, pie.y)
        val t = tn(r.center)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(fh * 0.35f))) }
        sombraEnPared(forma, r.center, sup, 0.2f)
        contacto(Offset(pie.x + fw * 0.06f, pie.y), fw * 1.15f, 4f * u, 0.42f)
        val lata = mezcla(VERDE, HOJALATA, 0.35f)
        drawPath(forma, t.c(lata))
        clipPath(forma) {
            tumbado(r, t, 1.3f)
            drawRect(t.s(0.25f), Offset(r.right - fw * 0.12f, r.top), Size(fw * 0.12f, fh))
            drawRect(t.c(HOJALATA).copy(alpha = 0.6f), Offset(r.left + fw * 0.6f, r.top + fh * 0.6f), Size(fw * 0.08f, fh * 0.14f))
            drawOval(t.c(mezcla(ROJO, CAOBA, 0.5f)).copy(alpha = 0.55f), Offset(r.left + fw * 0.12f, r.top + fh * 0.62f), Size(fw * 0.1f, fh * 0.2f))
        }
        // La tapa: la línea de cierre y la pestaña.
        drawLine(t.s(0.4f), Offset(r.left + 1f * u, r.top + fh * 0.36f), Offset(r.right - 1f * u, r.top + fh * 0.36f), 1f * u)
        drawLine(t.b(0.4f), Offset(r.left + 1f * u, r.top + fh * 0.36f + 1f * u), Offset(r.right - 1f * u, r.top + fh * 0.36f + 1f * u), 0.6f * u)
        drawRect(t.c(HOJALATA), Offset(pie.x - fw * 0.06f, r.top + fh * 0.26f), Size(fw * 0.12f, fh * 0.2f))
        drawRect(t.b(0.5f), r.topLeft + Offset(fh * 0.3f, 0f), Size(fw - fh * 0.6f, 0.9f * u))
        filo(r, t, luzDerecha(r.center.x))
    }

    /**
     * El trapo de algodón dejado en un montón sobre la tapa, con un pico que cuelga por el canto: los dobleces blandos, la
     * lista roja cerca del borde, las manchas de grasa y los hilos sueltos.
     */
    private fun DrawScope.trapo(pie: Offset, frente: Float, canto: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, frente))
        val der = luzDerecha(pie.x)
        val algodon = mezcla(mezcla(HUESO, HOJALATA, 0.42f), MOSTAZA, 0.08f)
        val a = w * 0.064f
        val arriba = pie.y - w * 0.034f
        // El pico que cuelga por delante del canto.
        val xa = pie.x - a * 0.02f; val xb = pie.x + a * 0.94f
        val punta = Offset(pie.x + a * 0.66f, canto + w * 0.06f)
        val pico = Path().apply {
            moveTo(xa, pie.y - w * 0.006f)
            cubicTo(xa - w * 0.006f, frente + w * 0.012f, xa + w * 0.012f, canto + w * 0.012f, punta.x - w * 0.014f, punta.y - w * 0.012f)
            cubicTo(punta.x - w * 0.006f, punta.y + w * 0.004f, punta.x + w * 0.006f, punta.y + w * 0.003f, punta.x + w * 0.012f, punta.y - w * 0.012f)
            cubicTo(xb - w * 0.006f, canto + w * 0.014f, xb + w * 0.008f, frente + w * 0.014f, xb, pie.y - w * 0.006f)
            close()
        }
        clipRect(0f, frente, size.width, size.height) { translate(if (der) -w * 0.01f else w * 0.012f, w * 0.01f) { drawPath(pico, t.s(0.36f)) } }
        drawPath(pico, t.c(mezcla(algodon, HOLLIN, 0.08f)))
        clipPath(pico) {
            // El doblez del medio: una cara a la luz y la otra en sombra; el quiebro del canto de la tapa.
            val medio = pie.x + a * 0.5f
            drawPath(poligono(medio, frente - w * 0.01f, xb + w * 0.02f, frente - w * 0.01f, xb + w * 0.02f, punta.y, punta.x, punta.y + w * 0.01f), if (der) t.b(0.2f) else t.s(0.24f))
            drawPath(poligono(medio, frente - w * 0.01f, xa - w * 0.02f, frente - w * 0.01f, xa - w * 0.02f, punta.y, punta.x, punta.y + w * 0.01f), if (der) t.s(0.2f) else t.b(0.16f))
            drawPath(Path().apply { moveTo(medio - w * 0.014f, frente); cubicTo(medio - w * 0.02f, canto, punta.x - w * 0.012f, canto + w * 0.03f, punta.x - w * 0.004f, punta.y) }, t.s(0.2f), style = Stroke(w * 0.007f))
            drawRect(t.s(0.26f), Offset(xa - w * 0.02f, frente - 1f * u), Size(xb - xa + w * 0.04f, 2.6f * u))
            drawRect(t.s(0.12f), Offset(xa - w * 0.02f, frente), Size(xb - xa + w * 0.04f, canto - frente))
            // Las dos listas rojas cerca de la punta, y la grasa.
            listOf(0.026f, 0.017f).forEachIndexed { i, dy ->
                drawLine(t.c(ROJO).copy(alpha = 0.75f), Offset(xa - w * 0.01f, punta.y - w * dy - w * 0.004f), Offset(xb + w * 0.01f, punta.y - w * dy + w * 0.003f), (if (i == 0) 2.2f else 1f) * u)
            }
            drawOval(t.c(mezcla(HOLLIN, CAOBA, 0.4f)).copy(alpha = 0.3f), Offset(pie.x + a * 0.2f, frente + w * 0.012f), Size(a * 0.7f, w * 0.014f))
        }
        for (k in 0 until 3) drawLine(t.c(algodon), punta + Offset(w * 0.006f * (k - 1), -w * 0.004f), punta + Offset(w * 0.008f * (k - 1), w * 0.012f), 0.7f * u)
        // El montón sobre la tapa.
        val monton = Path().apply {
            moveTo(pie.x - a, pie.y + w * 0.003f)
            cubicTo(pie.x - a * 1.14f, pie.y - w * 0.012f, pie.x - a * 0.95f, arriba + w * 0.016f, pie.x - a * 0.6f, arriba + w * 0.014f)
            cubicTo(pie.x - a * 0.4f, arriba + w * 0.002f, pie.x - a * 0.2f, arriba - w * 0.008f, pie.x + a * 0.12f, arriba - w * 0.004f)
            cubicTo(pie.x + a * 0.5f, arriba - w * 0.004f, pie.x + a * 1.12f, pie.y - w * 0.02f, pie.x + a, pie.y + w * 0.003f)
            cubicTo(pie.x + a * 0.5f, pie.y + w * 0.008f, pie.x - a * 0.5f, pie.y + w * 0.008f, pie.x - a, pie.y + w * 0.003f)
            close()
        }
        contacto(Offset(pie.x + a * 0.1f, pie.y + w * 0.003f), a * 2.3f, 4.5f * u, 0.4f)
        drawPath(monton, t.c(algodon))
        clipPath(monton) {
            // Las caras que miran arriba, claras; lo de abajo y el lado que no mira a la luz, en sombra.
            drawOval(t.b(0.3f), Offset(if (der) pie.x - a * 0.2f else pie.x - a * 0.95f, arriba - w * 0.012f), Size(a * 1.2f, w * 0.03f))
            drawRect(t.s(0.2f), Offset(pie.x - a * 1.2f, pie.y - w * 0.01f), Size(a * 2.4f, w * 0.02f))
            drawRect(t.s(0.18f), Offset(if (der) pie.x - a * 1.2f else pie.x + a * 0.55f, arriba - w * 0.02f), Size(a * 0.65f, w * 0.06f))
            // Los dobleces: valles en sombra con su lomo de luz al lado.
            listOf(Triple(-0.62f, 0.3f, -18f), Triple(-0.1f, 0.25f, 12f), Triple(0.42f, 0.35f, -8f)).forEach { (fx, fy, g) ->
                val p = Offset(pie.x + a * fx, arriba + w * 0.034f * fy)
                rotate(g, p) {
                    val valle = Path().apply { moveTo(p.x, p.y); cubicTo(p.x + w * 0.008f, p.y + w * 0.01f, p.x - w * 0.004f, p.y + w * 0.022f, p.x + w * 0.006f, p.y + w * 0.034f) }
                    drawPath(valle, t.s(0.3f), style = Stroke(w * 0.006f, cap = StrokeCap.Round))
                    translate(if (der) w * 0.006f else -w * 0.006f, 0f) { drawPath(valle, t.b(0.4f), style = Stroke(w * 0.003f, cap = StrokeCap.Round)) }
                }
            }
            drawOval(t.c(mezcla(HOLLIN, CAOBA, 0.4f)).copy(alpha = 0.32f), Offset(pie.x + a * 0.05f, arriba + w * 0.01f), Size(a * 0.6f, w * 0.008f))
            drawOval(t.c(mezcla(MOSTAZA, CAOBA, 0.5f)).copy(alpha = 0.4f), Offset(pie.x - a * 0.7f, arriba + w * 0.016f), Size(a * 0.4f, w * 0.01f))
            drawLine(t.c(ROJO).copy(alpha = 0.7f), Offset(pie.x - a * 1.02f, pie.y - w * 0.008f), Offset(pie.x - a * 0.6f, arriba + w * 0.012f), 1.7f * u)
        }
        if (esNoche) drawPath(Path().apply { moveTo(pie.x - a * 0.5f, arriba + w * 0.006f); cubicTo(pie.x - a * 0.3f, arriba - w * 0.012f, pie.x + a * 0.1f, arriba - w * 0.006f, pie.x + a * 0.28f, arriba + w * 0.008f) }, t.b(0.5f), style = Stroke(1.1f * u))
    }
    // ---------------------------------------------------------------- al pie

    /**
     * El cubo de carbón de chapa, cortado por el borde izquierdo: la boca vista un poco desde arriba, el carbón en montón con
     * sus brillos, el cogedor metido, los aros, el óxido y el asa de alambre caída.
     */
    private fun DrawScope.cubo(pie: Offset, hb: Float) {
        val u = density
        val w = anchoW
        val bw = w * 0.25f; val bh = w * 0.15f
        val t = tn(Offset(pie.x, pie.y - bh / 2))
        val boca = pie.y - bh
        val forma = poligono(pie.x - bw / 2, boca, pie.x + bw / 2, boca, pie.x + bw * 0.4f, pie.y, pie.x - bw * 0.4f, pie.y)
        translate(w * 0.035f, w * 0.006f) { drawPath(forma, t.s(0.3f)) }
        contacto(Offset(pie.x + bw * 0.1f, pie.y), bw * 1.2f, w * 0.022f, 0.45f)
        // El fondo de la boca y el carbón en montón, que asoma por encima.
        drawOval(t.c(mezcla(HIERRO, HOLLIN, 0.6f)), Offset(pie.x - bw / 2, boca - w * 0.02f), Size(bw, w * 0.04f))
        val monton = Path().apply {
            moveTo(pie.x - bw * 0.5f, boca + w * 0.006f)
            cubicTo(pie.x - bw * 0.36f, boca - w * 0.05f, pie.x + bw * 0.12f, boca - w * 0.07f, pie.x + bw * 0.47f, boca + w * 0.006f)
            close()
        }
        drawPath(monton, t.c(HOLLIN))
        for (k in 0 until 18) {
            val fx = azar(6000, k) - 0.5f
            val p = Offset(pie.x + bw * fx * 0.86f, boca - w * 0.05f * azar(6001, k) * (1f - 1.6f * abs(fx)).coerceAtLeast(0.1f) + w * 0.004f)
            val rr = w * (0.009f + 0.01f * azar(6002, k))
            drawPath(poligono(p.x - rr, p.y, p.x - rr * 0.3f, p.y - rr, p.x + rr, p.y - rr * 0.4f, p.x + rr * 0.6f, p.y + rr * 0.5f), t.c(mezcla(HOLLIN, PIZARRA, 0.35f * azar(6003, k))))
            drawPath(poligono(p.x - rr, p.y, p.x - rr * 0.3f, p.y - rr, p.x + rr * 0.1f, p.y - rr * 0.2f), t.b(0.22f))
            drawLine(t.b(0.65f), Offset(p.x - rr * 0.8f, p.y - rr * 0.1f), Offset(p.x - rr * 0.3f, p.y - rr * 0.9f), 0.9f * u)
        }
        // El cogedor, metido en el carbón: la cuchara de chapa y el mango de madera hacia arriba.
        val m0 = Offset(pie.x + bw * 0.14f, boca - w * 0.028f); val m1 = Offset(pie.x + bw * 0.36f, boca - w * 0.115f)
        drawPath(poligono(m0.x - w * 0.045f, m0.y + w * 0.022f, m0.x - w * 0.012f, m0.y - w * 0.018f, m0.x + w * 0.022f, m0.y + w * 0.002f, m0.x - w * 0.006f, m0.y + w * 0.034f), t.c(mezcla(HIERRO, HOJALATA, 0.3f)))
        drawLine(t.b(0.5f), Offset(m0.x - w * 0.043f, m0.y + w * 0.02f), Offset(m0.x - w * 0.012f, m0.y - w * 0.016f), 0.9f * u)
        drawLine(t.s(0.3f), m0 + Offset(2.4f * u, 2f * u), m1 + Offset(2.4f * u, 2f * u), w * 0.014f)
        drawLine(t.c(mezcla(CAOBA_LUZ, MOSTAZA, 0.3f)), m0, m1, w * 0.015f, StrokeCap.Round)
        drawLine(t.b(0.45f), m0 + Offset(-1.2f * u, -1.2f * u), m1 + Offset(-1.2f * u, -1.2f * u), 1f * u)
        drawCircle(t.c(mezcla(CAOBA, CAOBA_OSC, 0.3f)), w * 0.01f, m1)
        // El cubo de chapa, con sus aros, el óxido y las abolladuras.
        drawPath(forma, t.c(mezcla(HIERRO, PIZARRA, 0.25f)))
        clipPath(forma) {
            cilindro(Rect(pie.x - bw / 2, boca, pie.x + bw / 2, pie.y), t, 1.8f)
            listOf(0.2f, 0.84f).forEach { f -> drawRect(t.c(HOLLIN), Offset(pie.x - bw / 2, boca + bh * f), Size(bw, w * 0.007f)); drawRect(t.b(0.4f), Offset(pie.x - bw / 2, boca + bh * f), Size(bw, 0.9f * u)) }
            drawOval(t.c(mezcla(ROJO, CAOBA, 0.4f)).copy(alpha = 0.55f), Offset(pie.x + bw * 0.08f, boca + bh * 0.42f), Size(bw * 0.17f, bh * 0.24f))
            drawOval(t.c(mezcla(ROJO, CAOBA, 0.4f)).copy(alpha = 0.4f), Offset(pie.x - bw * 0.1f, boca + bh * 0.7f), Size(bw * 0.2f, bh * 0.1f))
            drawOval(t.s(0.25f), Offset(pie.x + bw * 0.24f, boca + bh * 0.3f), Size(bw * 0.1f, bh * 0.2f))
            drawRect(t.c(HOLLIN).copy(alpha = 0.4f), Offset(pie.x - bw / 2, pie.y - bh * 0.1f), Size(bw, bh * 0.1f))
        }
        // El reborde de la boca, por delante, y el asa de alambre caída.
        val borde = Path().apply { moveTo(pie.x - bw / 2, boca); cubicTo(pie.x - bw * 0.3f, boca + w * 0.022f, pie.x + bw * 0.3f, boca + w * 0.022f, pie.x + bw / 2, boca) }
        drawPath(borde, t.c(HOJALATA), style = Stroke(2.6f * u))
        drawPath(borde, t.b(0.5f), style = Stroke(0.9f * u))
        val asa = Path().apply { moveTo(pie.x - bw * 0.47f, boca + bh * 0.06f); cubicTo(pie.x - bw * 0.2f, boca + bh * 0.62f, pie.x + bw * 0.28f, boca + bh * 0.6f, pie.x + bw * 0.47f, boca + bh * 0.06f) }
        translate(1.5f * u, 2f * u) { drawPath(asa, t.s(0.35f), style = Stroke(1.8f * u)) }
        drawPath(asa, t.c(mezcla(HOJALATA, HIERRO, 0.4f)), style = Stroke(2f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.45f), style = Stroke(0.6f * u))
        clavo(Offset(pie.x + bw * 0.47f, boca + bh * 0.06f), 2f * u, t, HIERRO)
    }

    /**
     * La pala del fogonero, apoyada en el casillero: la hoja larga de hierro con los costados levantados, el nervio, el filo
     * gastado y brillante y el polvo de carbón; el cubo de la hoja, el mango de fresno y el puño en D con su travesaño.
     */
    private fun DrawScope.pala(b: Rect, w: Float, hb: Float) {
        val u = density
        val pieP = Offset(w * 0.25f, b.top + hb * 0.975f)
        val t = tn(Offset(pieP.x + w * 0.03f, b.top + hb * 0.55f))
        val giro = 10f
        val hw = w * 0.105f; val hh = w * 0.118f
        val largo = w * 0.118f
        val cima = pieP.y - hh - largo
        val madera = mezcla(CAOBA_LUZ, MOSTAZA, 0.4f)
        contacto(Offset(pieP.x + w * 0.02f, pieP.y), hw * 1.5f, w * 0.02f, 0.45f)
        rotate(giro, pieP) {
            // Su sombra, en el zócalo y el larguero (corrida a la derecha).
            if (!esNoche) translate(w * 0.03f, w * 0.004f) {
                drawRect(sombraMueble(0.26f), Offset(pieP.x - w * 0.008f, cima - w * 0.03f), Size(w * 0.016f, largo + w * 0.04f))
                drawRect(sombraMueble(0.26f), Offset(pieP.x - hw * 0.45f, pieP.y - hh), Size(hw * 0.9f, hh * 0.6f))
            }
            // El mango y el puño en D: la horquilla y el travesaño.
            drawRect(t.c(madera), Offset(pieP.x - w * 0.008f, cima), Size(w * 0.016f, largo + w * 0.02f))
            drawRect(t.b(0.4f), Offset(pieP.x - w * 0.008f, cima), Size(w * 0.005f, largo + w * 0.02f))
            drawRect(t.s(0.3f), Offset(pieP.x + w * 0.003f, cima), Size(w * 0.005f, largo + w * 0.02f))
            val yT = cima - w * 0.04f
            val horq = Path().apply {
                moveTo(pieP.x - w * 0.009f, cima + w * 0.004f); lineTo(pieP.x - w * 0.03f, yT + w * 0.006f); lineTo(pieP.x - w * 0.03f, yT - w * 0.006f)
                lineTo(pieP.x + w * 0.03f, yT - w * 0.006f); lineTo(pieP.x + w * 0.03f, yT + w * 0.006f); lineTo(pieP.x + w * 0.009f, cima + w * 0.004f); close()
            }
            val ojo = poligono(pieP.x - w * 0.005f, cima - w * 0.004f, pieP.x - w * 0.019f, yT + w * 0.007f, pieP.x + w * 0.019f, yT + w * 0.007f, pieP.x + w * 0.005f, cima - w * 0.004f)
            drawPath(Path().apply { op(horq, ojo, androidx.compose.ui.graphics.PathOperation.Difference) }, t.c(madera))
            drawRect(t.b(0.45f), Offset(pieP.x - w * 0.03f, yT - w * 0.006f), Size(w * 0.06f, 1f * u))
            drawRect(t.s(0.3f), Offset(pieP.x - w * 0.03f, yT + w * 0.004f), Size(w * 0.06f, 1.2f * u))
            listOf(-1f, 1f).forEach { s -> clavo(Offset(pieP.x + s * w * 0.024f, yT), 1f * u, t, HIERRO) }
            // El cubo de la hoja, que abraza el mango, con su remache.
            val casquillo = poligono(pieP.x - w * 0.012f, pieP.y - hh - w * 0.03f, pieP.x + w * 0.012f, pieP.y - hh - w * 0.03f, pieP.x + w * 0.02f, pieP.y - hh + w * 0.012f, pieP.x - w * 0.02f, pieP.y - hh + w * 0.012f)
            drawPath(casquillo, t.c(HIERRO))
            clipPath(casquillo) { cilindro(Rect(pieP.x - w * 0.02f, pieP.y - hh - w * 0.03f, pieP.x + w * 0.02f, pieP.y - hh + w * 0.012f), t, 1.6f) }
            clavo(Offset(pieP.x, pieP.y - hh - w * 0.014f), 1.2f * u, t, HOJALATA)
            // La hoja.
            val hoja = Path().apply {
                moveTo(pieP.x - hw * 0.44f, pieP.y - hh + w * 0.004f)
                cubicTo(pieP.x - hw * 0.2f, pieP.y - hh - w * 0.008f, pieP.x + hw * 0.2f, pieP.y - hh - w * 0.008f, pieP.x + hw * 0.44f, pieP.y - hh + w * 0.004f)
                lineTo(pieP.x + hw * 0.5f, pieP.y - w * 0.014f)
                cubicTo(pieP.x + hw * 0.5f, pieP.y, pieP.x + hw * 0.42f, pieP.y, pieP.x + hw * 0.38f, pieP.y)
                lineTo(pieP.x - hw * 0.38f, pieP.y)
                cubicTo(pieP.x - hw * 0.42f, pieP.y, pieP.x - hw * 0.5f, pieP.y, pieP.x - hw * 0.5f, pieP.y - w * 0.014f)
                close()
            }
            drawPath(hoja, t.c(mezcla(HIERRO, PIZARRA, 0.2f)))
            clipPath(hoja) {
                // Los costados levantados (el de la luz, claro; el otro, en sombra), el nervio y el filo gastado.
                drawRect(t.b(0.3f), Offset(pieP.x - hw / 2, pieP.y - hh - w * 0.01f), Size(hw * 0.12f, hh + w * 0.01f))
                drawRect(t.s(0.3f), Offset(pieP.x + hw * 0.36f, pieP.y - hh - w * 0.01f), Size(hw * 0.14f, hh + w * 0.01f))
                drawRect(t.s(0.18f), Offset(pieP.x - hw * 0.38f, pieP.y - hh - w * 0.01f), Size(hw * 0.1f, hh + w * 0.01f))
                drawPath(poligono(pieP.x - w * 0.012f, pieP.y - hh, pieP.x + w * 0.012f, pieP.y - hh, pieP.x + w * 0.003f, pieP.y - hh * 0.35f, pieP.x - w * 0.003f, pieP.y - hh * 0.35f), t.b(0.18f))
                drawRect(t.c(HOJALATA), Offset(pieP.x - hw / 2, pieP.y - hh * 0.2f), Size(hw, hh * 0.2f))
                drawRect(t.c(HOJALATA).copy(alpha = 0.5f), Offset(pieP.x - hw / 2, pieP.y - hh * 0.32f), Size(hw, hh * 0.12f))
                drawRect(t.b(0.55f), Offset(pieP.x - hw / 2, pieP.y - hh * 0.2f), Size(hw * 0.5f, 1f * u))
                for (k in 0 until 5) drawLine(t.s(0.3f), Offset(pieP.x - hw * (0.3f - 0.14f * k), pieP.y - hh * 0.02f), Offset(pieP.x - hw * (0.3f - 0.14f * k) + w * 0.004f, pieP.y - hh * (0.2f + 0.1f * azar(6050, k))), 0.7f * u)
                drawOval(t.c(HOLLIN).copy(alpha = 0.5f), Offset(pieP.x - hw * 0.25f, pieP.y - hh * 0.8f), Size(hw * 0.45f, hh * 0.3f))
                drawOval(t.c(mezcla(ROJO, CAOBA, 0.4f)).copy(alpha = 0.45f), Offset(pieP.x + hw * 0.05f, pieP.y - hh * 0.55f), Size(hw * 0.25f, hh * 0.16f))
            }
            drawPath(hoja, t.b(0.3f), style = Stroke(0.8f * u))
        }
    }

    /**
     * La caja de herramientas de madera, cortada por el borde derecho: los testeros que suben, el asa de travesaño, y dentro
     * el martillo, una llave fija y un trapo; las tablas clavadas, tiznadas por abajo.
     */
    private fun DrawScope.cajaHerramientas(pie: Offset, hb: Float) {
        val u = density
        val w = anchoW
        val cw = w * 0.3f; val chh = w * 0.082f
        val r = Rect(pie.x - cw / 2, pie.y - chh, pie.x + cw / 2, pie.y)
        val t = tn(r.center)
        val madera = mezcla(CAOBA_LUZ, MOSTAZA, 0.42f)
        contacto(Offset(r.center.x, pie.y), cw * 1.15f, w * 0.022f, 0.45f)
        // El testero de la izquierda, que sube en pico, y el travesaño del asa.
        val tx = r.left + cw * 0.05f
        val testero = poligono(tx - w * 0.022f, r.top + 1f * u, tx - w * 0.008f, r.top - w * 0.085f, tx + w * 0.008f, r.top - w * 0.085f, tx + w * 0.022f, r.top + 1f * u)
        if (!esNoche) translate(w * 0.02f, w * 0.008f) { drawPath(testero, sombraMueble(0.22f)) }
        // Lo que asoma: el martillo (la cabeza de hierro y el mango), una llave fija y un trapo.
        val m0 = Offset(r.left + cw * 0.3f, r.top + 2f * u); val m1 = Offset(r.left + cw * 0.2f, r.top - w * 0.07f)
        drawLine(t.c(mezcla(madera, HUESO, 0.15f)), m0, m1, w * 0.013f, StrokeCap.Round)
        drawLine(t.b(0.4f), m0 + Offset(-1.2f * u, 0f), m1 + Offset(-1.2f * u, 0f), 0.9f * u)
        rotate(-8f, m1) {
            val cab = Rect(m1.x - w * 0.034f, m1.y - w * 0.014f, m1.x + w * 0.03f, m1.y + w * 0.012f)
            drawRect(t.s(0.3f), cab.topLeft + Offset(2f * u, 2f * u), cab.size)
            drawRect(t.c(HIERRO), cab.topLeft, cab.size)
            clipRect(cab.left, cab.top, cab.right, cab.bottom) { tumbado(cab, t, 1.6f) }
            drawPath(poligono(cab.left, cab.top, cab.left - w * 0.018f, cab.center.y, cab.left, cab.bottom), t.c(HIERRO))
            drawRect(t.c(HOJALATA), Offset(cab.right - w * 0.006f, cab.top), Size(w * 0.006f, cab.height))
        }
        val l0 = Offset(r.left + cw * 0.42f, r.top + 2f * u); val l1 = Offset(r.left + cw * 0.5f, r.top - w * 0.045f)
        drawLine(t.c(mezcla(HOJALATA, HIERRO, 0.3f)), l0, l1, w * 0.01f)
        drawArc(t.c(mezcla(HOJALATA, HIERRO, 0.3f)), -130f, 280f, false, Offset(l1.x - w * 0.014f, l1.y - w * 0.024f), Size(w * 0.028f, w * 0.028f), style = Stroke(w * 0.009f))
        drawLine(t.b(0.5f), l0 + Offset(-1f * u, 0f), l1 + Offset(-1f * u, 0f), 0.8f * u)
        drawPath(testero, t.c(madera))
        clipPath(testero) { drawRect(t.b(0.25f), Offset(tx - w * 0.022f, r.top - w * 0.09f), Size(w * 0.016f, w * 0.1f)); drawRect(t.s(0.25f), Offset(tx + w * 0.006f, r.top - w * 0.09f), Size(w * 0.02f, w * 0.1f)) }
        val yA = r.top - w * 0.07f
        drawRect(t.s(0.3f), Offset(tx, yA + w * 0.008f), Size(r.right - tx, w * 0.006f))
        drawRect(t.c(mezcla(madera, CAOBA, 0.25f)), Offset(tx - w * 0.004f, yA - w * 0.007f), Size(r.right - tx + w * 0.004f, w * 0.014f))
        drawRect(t.b(0.5f), Offset(tx - w * 0.004f, yA - w * 0.007f), Size(r.right - tx + w * 0.004f, 1f * u))
        drawRect(t.c(HOLLIN).copy(alpha = 0.3f), Offset(tx + cw * 0.3f, yA - w * 0.007f), Size(cw * 0.2f, w * 0.014f))
        clavo(Offset(tx, yA), 1.3f * u, t, HIERRO)
        // La caja: dos tablas con su veta, los clavos y el tizne de abajo.
        drawRect(t.c(madera), r.topLeft, r.size)
        vetas(r, (if (esNoche) NOCHE else CAOBA_OSC).copy(alpha = 0.35f), 6100, 0.7f, grosor = 0.5f)
        drawRect(t.b(0.5f), r.topLeft, Size(cw, 1.2f * u))
        drawRect(t.s(0.3f), Offset(r.left, r.top + chh * 0.5f), Size(cw, 1.3f * u))
        drawRect(t.b(0.25f), Offset(r.left, r.top + chh * 0.5f + 1.3f * u), Size(cw, 0.8f * u))
        drawRect(t.b(0.2f), r.topLeft, Size(cw * 0.08f, chh))
        listOf(r.left + cw * 0.045f, r.left + cw * 0.48f).forEach { x -> clavo(Offset(x, r.top + chh * 0.25f), 1.2f * u, t, HIERRO); clavo(Offset(x, r.top + chh * 0.75f), 1.2f * u, t, HIERRO) }
        // Un trapo que cuelga por el borde y una mancha de aceite.
        val tr = Path().apply {
            moveTo(r.left + cw * 0.56f, r.top - w * 0.012f)
            cubicTo(r.left + cw * 0.62f, r.top - w * 0.03f, r.left + cw * 0.72f, r.top - w * 0.022f, r.left + cw * 0.76f, r.top - w * 0.006f)
            lineTo(r.left + cw * 0.74f, r.top + chh * 0.5f); lineTo(r.left + cw * 0.69f, r.top + chh * 0.36f); lineTo(r.left + cw * 0.64f, r.top + chh * 0.56f); lineTo(r.left + cw * 0.58f, r.top + chh * 0.4f)
            close()
        }
        translate(1.8f * u, 2f * u) { drawPath(tr, t.s(0.3f)) }
        drawPath(tr, t.c(mezcla(mezcla(HUESO, HOJALATA, 0.5f), MOSTAZA, 0.2f)))
        clipPath(tr) {
            drawPath(poligono(r.left + cw * 0.66f, r.top - w * 0.02f, r.left + cw * 0.77f, r.top - w * 0.02f, r.left + cw * 0.75f, r.top + chh * 0.6f, r.left + cw * 0.69f, r.top + chh * 0.36f), t.s(0.24f))
            drawRect(t.b(0.3f), Offset(r.left + cw * 0.56f, r.top - w * 0.03f), Size(cw * 0.22f, w * 0.022f))
            drawRect(t.c(HOLLIN).copy(alpha = 0.3f), Offset(r.left + cw * 0.56f, r.top + chh * 0.22f), Size(cw * 0.22f, chh * 0.1f))
            drawLine(t.c(ROJO).copy(alpha = 0.6f), Offset(r.left + cw * 0.56f, r.top + chh * 0.04f), Offset(r.left + cw * 0.78f, r.top + chh * 0.04f), 1.4f * u)
        }
        drawOval(t.c(HOLLIN).copy(alpha = 0.3f), Offset(r.left + cw * 0.2f, r.top + chh * 0.6f), Size(cw * 0.14f, chh * 0.25f))
        drawRect(t.c(HOLLIN).copy(alpha = 0.38f), Offset(r.left, r.bottom - chh * 0.2f), Size(cw, chh * 0.2f))
    }
    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x4FCC9A5DL.toInt()
        x = x xor (x ushr 15)
        x *= 0xC40F3609L.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del farol.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, 0f, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x - 0.02f)
        fondoHueco(w, h, t, der)
        if (c.vacio) { vacio(w, h, c, t, der); return }
        val m = mezclar(c.semilla)
        // Nada de lo guardado se sale del hueco (ni su sombra): así el aura al tocar no calca sombras sobre el marco.
        clipRect(0f, 0f, w, h) {
            // Al levantarlo, la sombra que deja en la tabla.
            if (ab > 0f) drawRect(t.s(0.35f * ab), Offset(w * 0.07f, h * (PISO - 0.03f)), Size(w * 0.88f, h * 0.03f))
            // La muestra suelta (el borde de un compartimento abierto, sobre una ficha): siempre la gaveta, y sale más, para
            // que se vean sus tarjetas en la franja de arriba.
            translate(0f, -(if (dentro) SALIDA else 0.17f) * h * ab) {
                when {
                    c.sinTema -> galletas(w, h, c, t, der)
                    else -> when (if (dentro) Math.floorMod(m, 3) else 0) {
                        0 -> gaveta(w, h, c, m, t, der)
                        1 -> casilla(w, h, c, m, t, der, SALIDA * h * ab)
                        else -> mechas(w, h, c, m, t, der)
                    }
                }
            }
        }
    }

    /**
     * El fondo de un hueco: las tablas de pino del fondo, la sombra de la tabla de arriba en escalones, el costado en sombra
     * (el derecho de día; de noche, el que no mira al farol) y la tabla del piso con su canto. De día, la fila de arriba un
     * poco más clara; de noche, el farol según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        drawRect(t.c(mezcla(CAOBA_OSC, CAOBA, 0.45f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.1f), Offset(if (der) w * 0.55f else 0f, 0f), Size(w * 0.45f, h))
        listOf(0.24f, 0.5f, 0.77f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h))
            drawRect(t.b(0.16f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h))
        }
        // La sombra de la tabla de arriba, en escalones, y la de los costados.
        listOf(0.36f to 0.1f, 0.22f to 0.12f, 0.1f to 0.22f, 0.04f to 0.2f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.24f), Offset(if (der) 0f else w * 0.91f, 0f), Size(w * 0.09f, h))
        drawRect(t.s(0.14f), Offset(if (der) 0f else w * 0.84f, 0f), Size(w * 0.16f, h))
        drawRect(t.s(0.12f), Offset(if (der) w * 0.975f else 0f, 0f), Size(w * 0.025f, h))
        val piso = h * PISO
        drawRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.35f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.5f), Offset(0f, piso), Size(w, 1f * u))
        drawRect(t.s(0.2f), Offset(0f, piso + (h - piso) * 0.6f), Size(w, (h - piso) * 0.4f))
    }

    /** La sombra de lo guardado en el fondo del hueco: de día, a la derecha y abajo; de noche, al lado contrario del farol. */
    private fun DrawScope.sombraFondo(r: Rect, w: Float, h: Float, t: Tono, der: Boolean, radio: Float = 0f) {
        val dx = if (der) -w * 0.03f else w * 0.035f
        drawRoundRect(t.s(0.32f), Offset(r.left + dx, r.top + h * 0.025f), r.size, CornerRadius(radio))
    }

    /** La tarjeta del tema, con su sombra y el filete; de noche, más hundida cuanto más lejos del farol. */
    private fun DrawScope.tarjeta(w: Float, h: Float, t: Tono, papel: Color = CARTULINA, filete: Boolean = true) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.3f), Offset(r.left + 1.2f * u, r.top + 1.8f * u), r.size)
        val fondo = if (t.o) mezcla(mezcla(papel, PAPEL_LEJOS, 0.92f), mezcla(papel, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(papel)
        drawRect(fondo, r.topLeft, r.size)
        if (filete) drawRect(TINTA.copy(alpha = 0.4f), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(0.7f * u))
        drawRect(t.s(0.1f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
    }

    /** El marco de latón de un portatarjetas alrededor del rótulo, con sus dos remaches. */
    private fun DrawScope.portatarjetas(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val pr = Rect(r.left - 3f * u, r.top - 3f * u, r.right + 3f * u, r.bottom + 3f * u)
        drawRect(t.s(0.35f), Offset(pr.left + 1.5f * u, pr.top + 2f * u), pr.size)
        drawRect(t.c(LATON), pr.topLeft, pr.size)
        drawRect(t.b(0.6f), pr.topLeft, Size(pr.width, 1f * u))
        drawRect(t.s(0.35f), Offset(pr.left, pr.bottom - 1.2f * u), Size(pr.width, 1.2f * u))
        destello(Offset(if (der) pr.right - 4f * u else pr.left + 4f * u, pr.top + 1.2f * u), 1.1f * u, t)
    }

    /** Las tarjetas que asoman por arriba de [x0]..[x1], desde [arriba]: más y más altas cuanto más guarda; alguna con su pestaña de color. */
    private fun DrawScope.tarjetas(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono, m: Int) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantas = (2 + n).coerceIn(2, 9)
        val sube = h * (0.07f + 0.014f * n)
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val tw = (x1 - x0) * 0.21f
            val x = x0 + (x1 - x0 - tw) * (f + 0.06f * (azar(c.semilla, i + 30) - 0.5f)).coerceIn(0f, 1f)
            val y = arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 7f
            rotate(g, Offset(x + tw / 2, arriba)) {
                drawRect(t.s(0.25f), Offset(x + 1.5f * u, y + 1.5f * u), Size(tw, arriba - y))
                drawRect(t.c(if (i % 3 == 1) HUESO else CARTULINA), Offset(x, y), Size(tw, arriba - y + 2f * u))
                drawRect(t.b(0.45f), Offset(x, y), Size(tw, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + tw * 0.85f, y), Size(tw * 0.15f, arriba - y))
                // Las perforaciones de la tarjeta.
                for (j in 0 until 3) if (azar(c.semilla + j, i) < 0.6f) drawRect(t.c(TINTA).copy(alpha = 0.6f), Offset(x + tw * (0.2f + 0.22f * j), y + 2.5f * u), Size(1.4f * u, 2.6f * u))
                // Una pestaña de color.
                if (i == Math.floorMod(m ushr 6, cuantas)) {
                    val pc = when (Math.floorMod(m ushr 9, 3)) { 0 -> ROJO; 1 -> VERDE; else -> PIZARRA }
                    drawRect(t.c(pc), Offset(x + tw * 0.55f, y - h * 0.035f), Size(tw * 0.38f, h * 0.04f))
                }
            }
        }
    }

    /**
     * La gaveta remachada, un poco sacada: el frente de latón, de cobre o de chapa verde con su ribete de latón, según la
     * semilla, los remaches del borde, el filete grabado, el tirador de asa con sus dos botones, el portatarjetas con la
     * tarjeta, los golpes y las huellas de grasa; por arriba asoman las tarjetas.
     */
    private fun DrawScope.gaveta(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val fr = Rect(w * 0.02f, h * 0.25f, w * 0.98f, piso)
        val clase = Math.floorMod(m ushr 3, 3)
        val metal = when (clase) { 0 -> LATON; 1 -> COBRE; else -> mezcla(VERDE, PIZARRA, 0.25f) }
        val ribete = if (clase == 2) LATON else mezcla(metal, CAOBA_OSC, 0.35f)
        sombraFondo(Rect(fr.left, fr.top - h * 0.05f, fr.right, fr.bottom), w, h, t, der)
        // La boca del cajón y sus costados, con las tarjetas dentro.
        drawRect(t.c(CAOBA_OSC), Offset(fr.left + w * 0.02f, fr.top - h * 0.06f), Size(fr.width - w * 0.04f, h * 0.07f))
        tarjetas(fr.left + w * 0.05f, fr.right - w * 0.05f, fr.top + h * 0.01f, h, c, t, m)
        drawRect(t.c(mezcla(CAOBA, CAOBA_LUZ, 0.2f)), Offset(fr.left + w * 0.015f, fr.top - h * 0.06f), Size(w * 0.014f, h * 0.07f))
        drawRect(t.c(CAOBA), Offset(fr.right - w * 0.029f, fr.top - h * 0.06f), Size(w * 0.014f, h * 0.07f))
        // El frente.
        drawRect(t.c(metal), fr.topLeft, fr.size)
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            val lx = if (der) fr.right - fr.width * 0.22f else fr.left + fr.width * 0.06f
            drawRect(t.b(0.16f), Offset(lx, fr.top), Size(fr.width * 0.16f, fr.height))
            drawRect(t.b(0.12f), fr.topLeft, Size(fr.width, fr.height * 0.2f))
            drawRect(t.s(0.2f), Offset(if (der) fr.left else fr.right - fr.width * 0.18f, fr.top), Size(fr.width * 0.18f, fr.height))
            pinceladas(fr, if (t.o) NOCHE else mezcla(metal, LUZ_MUEBLE, 0.4f), c.semilla, 8, u, 0.12f)
            // Las huellas de grasa junto al tirador y un golpe.
            drawOval(t.c(HOLLIN).copy(alpha = 0.3f), Offset(fr.center.x - w * 0.12f, fr.top + fr.height * 0.12f), Size(w * 0.08f, h * 0.06f))
            drawOval(t.s(0.2f), Offset(fr.left + fr.width * (0.65f + 0.2f * azar(m, 1)), fr.top + fr.height * 0.08f), Size(w * 0.04f, h * 0.03f))
        }
        // El ribete del borde y los remaches.
        drawRect(t.c(ribete), fr.topLeft, fr.size, style = Stroke(2.4f * u))
        drawRect(t.b(0.55f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(t.s(0.35f), Offset(fr.left, fr.bottom - 1.4f * u), Size(fr.width, 1.4f * u))
        val paso = w * 0.065f
        var x = fr.left + w * 0.025f
        while (x < fr.right - w * 0.015f) {
            clavo(Offset(x, fr.top + w * 0.018f), 1.2f * u, t, ribete)
            clavo(Offset(x, fr.bottom - w * 0.018f), 1.2f * u, t, ribete)
            x += paso
        }
        // El filete grabado.
        val fil = Rect(fr.left + w * 0.04f, fr.top + h * 0.06f, fr.right - w * 0.04f, fr.bottom - h * 0.06f)
        drawRect(t.s(0.3f), fil.topLeft, fil.size, style = Stroke(0.8f * u))
        drawRect(t.b(0.3f), fil.topLeft + Offset(0.8f * u, 0.8f * u), fil.size, style = Stroke(0.6f * u))
        // El tirador de asa, con sus dos botones.
        val ty = fr.top + h * 0.12f
        val b0 = Offset(w * 0.38f, ty); val b1 = Offset(w * 0.62f, ty)
        val asa = Path().apply { moveTo(b0.x, b0.y); cubicTo(b0.x, ty + h * 0.13f, b1.x, ty + h * 0.13f, b1.x, b1.y) }
        translate(if (der) -2f * u else 2f * u, 2.5f * u) { drawPath(asa, t.s(0.35f), style = Stroke(2.6f * u)) }
        drawPath(asa, t.c(LATON_OSC), style = Stroke(3f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.6f), style = Stroke(0.9f * u))
        listOf(b0, b1).forEach { p -> drawCircle(t.s(0.35f), 3.8f * u, p + Offset(1f * u, 1.3f * u)); drawCircle(t.c(LATON), 3.6f * u, p); drawCircle(t.b(0.6f), 1.3f * u, p - Offset(1f * u, 1f * u)) }
        destello(Offset(w * 0.5f, ty + h * 0.095f), 1.2f * u, t)
        portatarjetas(w, h, t, der)
        tarjeta(w, h, t)
        filo(fr, t, der)
    }

    /**
     * El casillero abierto: los fajos de partes de servicio apilados, cada uno con su cubierta de papel de color (más fajos y
     * más alta la pila con más fichas), atados todos con una cinta que se anuda arriba en un lazo (roja, azul pizarra o verde
     * según la semilla); la tarjeta del tema va sujeta delante. A la derecha cuelga de su cadena el silbato de tubo del guarda.
     */
    private fun DrawScope.casilla(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, sube: Float) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(0, 10)
        val clase = Math.floorMod(m ushr 3, 3)
        val cinta = when (clase) { 0 -> ROJO; 1 -> mezcla(PIZARRA, HOJALATA, 0.15f); else -> mezcla(VERDE, HOLLIN, 0.1f) }
        val cubierta = when (clase) { 0 -> mezcla(PIZARRA, HOJALATA, 0.5f); 1 -> mezcla(MOSTAZA, HUESO, 0.35f); else -> mezcla(ROJO, HUESO, 0.5f) }
        val alto = h * (0.54f + 0.016f * n)
        val pila = Rect(w * 0.055f, piso - alto, w * 0.945f, piso)
        sombraFondo(Rect(pila.left, pila.top - h * 0.03f, pila.right, pila.bottom), w, h, t, der)
        // Los fajos, de abajo arriba: el canto de las hojas, la cubierta de color y alguna hoja que se sale.
        val fajos = (2 + n / 3).coerceIn(2, 5)
        val hf = alto / fajos
        var ultimo = pila
        for (i in 0 until fajos) {
            val y1 = piso - hf * i; val y0 = y1 - hf
            val dx = if (i == 0) 0f else w * 0.035f * (azar(c.semilla, i + 3) - 0.5f)
            val ancho = pila.width * (1f - 0.07f * azar(c.semilla, i + 9))
            val r = Rect(pila.center.x - ancho / 2 + dx, y0, pila.center.x + ancho / 2 + dx, y1)
            ultimo = r
            val papel = when ((i + clase) % 3) { 0 -> HUESO; 1 -> CARTULINA; else -> mezcla(HUESO, cubierta, 0.3f) }
            drawRect(t.c(papel), r.topLeft, r.size)
            for (j in 1 until 5) {
                val y = y0 + hf * j / 5f
                drawLine(t.s(0.12f + 0.12f * azar(c.semilla + i, j)), Offset(r.left, y), Offset(r.right - w * 0.015f * azar(c.semilla, j + i * 7), y), 0.7f * u)
            }
            // La cubierta de papel de color del fajo (su canto) y las hojas que se salen por los lados.
            drawRect(t.c(cubierta), r.topLeft, Size(r.width, hf * 0.13f))
            drawRect(t.c(cubierta), Offset(r.left, y1 - hf * 0.09f), Size(r.width, hf * 0.09f))
            for (j in 0 until 4) {
                val y = y0 + hf * (0.2f + 0.17f * j)
                val largo = w * (0.008f + 0.022f * azar(c.semilla + i * 5, j + 50))
                if (j % 2 == 0) drawRect(t.c(HUESO), Offset(r.right - 0.5f * u, y), Size(largo, 1.5f * u))
                else drawRect(t.c(CARTULINA), Offset(r.left - largo + 0.5f * u, y), Size(largo, 1.5f * u))
            }
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.9f * u))
            drawRect(t.s(0.3f), Offset(r.left, y1 - 1.3f * u), Size(r.width, 1.3f * u))
            drawRect(t.b(0.14f), Offset(if (der) r.right - r.width * 0.14f else r.left, y0), Size(r.width * 0.14f, hf))
            drawRect(t.s(0.18f), Offset(if (der) r.left else r.right - r.width * 0.12f, y0), Size(r.width * 0.12f, hf))
        }
        // La cara de arriba del último fajo, vista un poco desde arriba.
        val arriba = ultimo.top - h * 0.04f
        drawPath(poligono(ultimo.left + w * 0.035f, arriba, ultimo.right - w * 0.035f, arriba, ultimo.right, ultimo.top, ultimo.left, ultimo.top), t.c(mezcla(cubierta, LUZ_MUEBLE, 0.25f)))
        drawLine(t.b(0.5f), Offset(ultimo.left + w * 0.035f, arriba), Offset(ultimo.right - w * 0.035f, arriba), 0.9f * u)
        // El parte de encima, con su encabezado impreso, y su esquina levantada.
        drawPath(poligono(ultimo.left + w * 0.11f, arriba + h * 0.006f, ultimo.right - w * 0.2f, arriba + h * 0.006f, ultimo.right - w * 0.17f, ultimo.top - h * 0.004f, ultimo.left + w * 0.085f, ultimo.top - h * 0.004f), t.c(HUESO))
        drawLine(t.c(TINTA).copy(alpha = 0.5f), Offset(ultimo.left + w * 0.16f, arriba + h * 0.02f), Offset(ultimo.right - w * 0.32f, arriba + h * 0.02f), 1f * u)
        drawPath(poligono(ultimo.right - w * 0.2f, arriba + h * 0.006f, ultimo.right - w * 0.17f, ultimo.top - h * 0.004f, ultimo.right - w * 0.215f, arriba - h * 0.022f), t.c(mezcla(HUESO, LUZ_MUEBLE, 0.5f)))
        // La cinta: baja por delante y se anuda arriba en un lazo.
        val cx = w * (0.36f + 0.1f * azar(c.semilla, 11))
        val ac = w * 0.034f
        drawRect(t.c(cinta), Offset(cx - ac / 2, arriba), Size(ac, piso - arriba))
        drawRect(t.b(0.3f), Offset(cx - ac / 2, arriba), Size(ac * 0.3f, piso - arriba))
        drawRect(t.s(0.25f), Offset(cx + ac * 0.2f, arriba), Size(ac * 0.3f, piso - arriba))
        drawRect(t.s(0.3f), Offset(cx + ac / 2, ultimo.top), Size(1.4f * u, piso - ultimo.top))
        // El nudo, arriba, con sus dos cabos: uno queda sobre el fajo y el otro cae por delante.
        val nudo = Offset(cx, arriba + h * 0.012f)
        val cabos = listOf(
            Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x - w * 0.03f, nudo.y - h * 0.012f, nudo.x - w * 0.06f, nudo.y - h * 0.03f, nudo.x - w * 0.095f, nudo.y - h * 0.022f) },
            Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + w * 0.03f, nudo.y + h * 0.01f, nudo.x + w * 0.05f, nudo.y + h * 0.05f, nudo.x + w * 0.07f, nudo.y + h * 0.1f) },
        )
        cabos.forEachIndexed { i, cabo ->
            translate(if (der) -1.5f * u else 1.5f * u, 2f * u) { drawPath(cabo, t.s(0.32f), style = Stroke(w * 0.026f)) }
            drawPath(cabo, t.c(mezcla(cinta, TINTA, if (i == 0) 0.05f else 0.18f)), style = Stroke(w * 0.027f))
            drawPath(cabo, t.b(0.3f), style = Stroke(0.8f * u))
        }
        drawRoundRect(t.s(0.3f), Offset(nudo.x - w * 0.022f + 1.2f * u, nudo.y - h * 0.026f + 1.6f * u), Size(w * 0.046f, h * 0.052f), CornerRadius(w * 0.012f))
        drawRoundRect(t.c(mezcla(cinta, TINTA, 0.25f)), Offset(nudo.x - w * 0.022f, nudo.y - h * 0.026f), Size(w * 0.046f, h * 0.052f), CornerRadius(w * 0.012f))
        drawLine(t.b(0.4f), Offset(nudo.x - w * 0.012f, nudo.y - h * 0.016f), Offset(nudo.x + w * 0.008f, nudo.y - h * 0.018f), 0.9f * u)
        // El silbato cuelga del fondo del hueco: no sube con el fajo al abrir.
        translate(0f, sube) { silbato(w, h, t, der) }
        filo(Rect(ultimo.left + w * 0.035f, arriba, ultimo.right - w * 0.035f, piso), t, der)
        tarjeta(w, h, t)
        // Los dos ojales de latón de la tarjeta.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        listOf(r.left + 3.2f * u, r.right - 3.2f * u).forEach { x -> drawCircle(t.c(LATON), 1.5f * u, Offset(x, r.top + 3.2f * u)); drawCircle(t.c(CAOBA_OSC), 0.6f * u, Offset(x, r.top + 3.2f * u)) }
    }

    /** El silbato de tubo del guarda, de latón, colgado de su cadena en una escarpia del fondo: la anilla, los anillos torneados, la ventana y la boquilla. */
    private fun DrawScope.silbato(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        val esc = Offset(w * 0.84f, h * 0.045f)
        val anilla = Offset(w * 0.835f, h * 0.15f)
        drawLine(t.s(0.3f), esc + Offset(if (der) -3f * u else 3f * u, 2f * u), anilla + Offset(if (der) -3f * u else 3f * u, 2f * u), 1.3f * u)
        drawLine(t.c(LATON_OSC), esc, anilla, 1.7f * u)
        drawLine(t.c(LATON_LUZ), esc, anilla, 1.1f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.4f * u)))
        clavo(esc, 1.8f * u, t, HIERRO)
        rotate(7f, anilla) {
            val sw = w * 0.05f; val sl = h * 0.25f
            val tubo = Rect(anilla.x - sw / 2, anilla.y + h * 0.035f, anilla.x + sw / 2, anilla.y + h * 0.035f + sl)
            drawRoundRect(t.s(0.32f), tubo.topLeft + Offset(if (der) -w * 0.02f else w * 0.022f, h * 0.02f), tubo.size, CornerRadius(sw * 0.3f))
            drawCircle(t.c(LATON_OSC), w * 0.016f, anilla + Offset(0f, h * 0.012f), style = Stroke(1.6f * u))
            drawRect(t.c(LATON_OSC), Offset(anilla.x - sw * 0.2f, tubo.top - h * 0.015f), Size(sw * 0.4f, h * 0.02f))
            val cuerpo = Path().apply { addRoundRect(RoundRect(tubo, CornerRadius(sw * 0.3f))) }
            drawPath(cuerpo, t.c(LATON))
            clipPath(cuerpo) {
                cilindro(tubo, t, 1.7f, der)
                // Los anillos torneados y la ventana.
                listOf(0.12f, 0.17f, 0.62f).forEach { f -> drawRect(t.s(0.3f), Offset(tubo.left, tubo.top + sl * f), Size(sw, 1f * u)); drawRect(t.b(0.3f), Offset(tubo.left, tubo.top + sl * f + 1f * u), Size(sw, 0.7f * u)) }
                drawRect(t.c(CAOBA_OSC), Offset(tubo.left + sw * 0.22f, tubo.top + sl * 0.26f), Size(sw * 0.56f, sl * 0.12f))
                drawRect(t.s(0.3f), Offset(tubo.left, tubo.bottom - sl * 0.2f), Size(sw, sl * 0.2f))
            }
            // La boquilla.
            drawRect(t.c(LATON_OSC), Offset(anilla.x - sw * 0.32f, tubo.bottom - 1f * u), Size(sw * 0.64f, h * 0.035f))
            drawRect(t.b(0.45f), Offset(anilla.x - sw * 0.32f, tubo.bottom - 1f * u), Size(sw * 0.18f, h * 0.035f))
            destello(Offset(if (der) tubo.right - sw * 0.28f else tubo.left + sw * 0.28f, tubo.top + sl * 0.07f), 1.2f * u, t)
        }
    }

    /**
     * La caja de hojalata de mechas: pintada de rojo óxido, azul pizarra o mostaza según la semilla, con el reborde enrollado
     * y la tapa de bisagra abierta hacia atrás (se ve su cara de hojalata con la etiqueta ovalada del fabricante); dentro, los
     * rollos de mecha de algodón de pie, que asoman por la boca (más rollos con más fichas), y un cabo que cuelga por delante.
     * Los golpes donde salta la pintura y la etiqueta de papel pegada, con una esquina que se despega.
     */
    private fun DrawScope.mechas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 3, 3)
        val pintura = when (clase) { 0 -> mezcla(ROJO, CAOBA, 0.12f); 1 -> mezcla(PIZARRA, VERDE, 0.2f); else -> MOSTAZA }
        val n = c.n.coerceIn(0, 10)
        val caja = Rect(w * 0.04f, h * 0.4f, w * 0.96f, piso)
        val alta = h * 0.07f
        // La tapa abierta hacia atrás: su cara de hojalata con el reborde.
        val tapa = poligono(caja.left + w * 0.012f, caja.top, caja.right - w * 0.012f, caja.top, caja.right - w * 0.06f, alta, caja.left + w * 0.06f, alta)
        translate(if (der) -w * 0.025f else w * 0.03f, h * 0.025f) { drawPath(tapa, t.s(0.3f)) }
        sombraFondo(caja, w, h, t, der)
        drawPath(tapa, t.c(mezcla(HOJALATA, LATON, 0.14f)))
        clipPath(tapa) {
            val lx = if (der) caja.right - caja.width * 0.42f else caja.left + caja.width * 0.1f
            drawPath(poligono(lx, caja.top, lx + caja.width * 0.14f, caja.top, lx + caja.width * 0.26f, alta, lx + caja.width * 0.12f, alta), t.b(0.3f))
            drawRect(t.s(0.22f), Offset(if (der) caja.left else caja.right - caja.width * 0.22f, alta), Size(caja.width * 0.22f, caja.top - alta))
            drawRect(t.s(0.2f), Offset(caja.left, caja.top - h * 0.1f), Size(caja.width, h * 0.1f))
            // La etiqueta ovalada del fabricante, pegada por dentro.
            val oc = Offset(w * 0.5f, alta + (caja.top - alta) * 0.36f)
            val ow = w * 0.4f; val oh = h * 0.17f
            drawOval(t.c(HUESO), Offset(oc.x - ow / 2, oc.y - oh / 2), Size(ow, oh))
            drawOval(t.c(pintura), Offset(oc.x - ow / 2 + 2f * u, oc.y - oh / 2 + 2f * u), Size(ow - 4f * u, oh - 4f * u), style = Stroke(1.5f * u))
            drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(oc.x - ow * 0.28f, oc.y - oh * 0.1f), Offset(oc.x + ow * 0.28f, oc.y - oh * 0.1f), 1.6f * u)
            drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(oc.x - ow * 0.18f, oc.y + oh * 0.16f), Offset(oc.x + ow * 0.18f, oc.y + oh * 0.16f), 0.9f * u)
        }
        drawPath(tapa, t.c(mezcla(pintura, HOLLIN, 0.15f)), style = Stroke(2.6f * u))
        drawLine(t.b(0.55f), Offset(caja.left + w * 0.06f, alta), Offset(caja.right - w * 0.06f, alta), 1f * u)
        // La boca de la caja y los rollos de mecha, de pie.
        drawRect(t.c(mezcla(HOLLIN, CAOBA_OSC, 0.4f)), Offset(caja.left + w * 0.015f, caja.top - h * 0.03f), Size(caja.width - w * 0.03f, h * 0.04f))
        val rollos = (2 + n / 2).coerceAtMost(6)
        val mecha = mezcla(HUESO, MOSTAZA, 0.16f)
        for (i in 0 until rollos) {
            val rr = h * (0.1f + 0.025f * azar(c.semilla, i + 62)) * (if (rollos > 4) 0.88f else 1f)
            val p = Offset(caja.left + caja.width * (i + 0.5f) / rollos + w * 0.025f * (azar(c.semilla, i + 60) - 0.5f), caja.top - rr * (0.2f + 0.45f * azar(c.semilla, i + 61)))
            drawCircle(t.s(0.3f), rr, p + Offset(if (der) -rr * 0.2f else rr * 0.22f, rr * 0.12f))
            drawCircle(t.c(if (i % 3 == 1) mezcla(mecha, HOJALATA, 0.25f) else mecha), rr, p)
            // Las vueltas de la mecha, su hilo de color y el ojo del rollo.
            listOf(0.8f, 0.6f, 0.4f).forEach { f -> drawCircle(t.s(0.2f), rr * f, p, style = Stroke(0.8f * u)) }
            val hilo = when ((i + clase) % 3) { 0 -> ROJO; 1 -> PIZARRA; else -> VERDE }
            drawCircle(t.c(hilo).copy(alpha = 0.75f), rr * 0.7f, p, style = Stroke(1.1f * u))
            drawCircle(t.c(CAOBA_OSC), rr * 0.17f, p)
            drawArc(t.b(0.5f), if (der) 280f else 190f, 70f, false, Offset(p.x - rr * 0.92f, p.y - rr * 0.92f), Size(rr * 1.84f, rr * 1.84f), style = Stroke(1.2f * u))
            drawArc(t.s(0.22f), if (der) 100f else 10f, 80f, false, Offset(p.x - rr * 0.9f, p.y - rr * 0.9f), Size(rr * 1.8f, rr * 1.8f), style = Stroke(rr * 0.2f))
        }
        // La caja.
        drawRect(t.c(pintura), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            val lx = if (der) caja.right - caja.width * 0.2f else caja.left + caja.width * 0.07f
            drawRect(t.b(0.16f), Offset(lx, caja.top), Size(caja.width * 0.14f, caja.height))
            drawRect(t.b(0.1f), caja.topLeft, Size(caja.width, caja.height * 0.22f))
            drawRect(t.s(0.22f), Offset(if (der) caja.left else caja.right - caja.width * 0.2f, caja.top), Size(caja.width * 0.2f, caja.height))
            pinceladas(caja, if (t.o) NOCHE else mezcla(pintura, LUZ_MUEBLE, 0.4f), c.semilla, 6, u, 0.1f)
            // El panel embutido y los golpes donde salta la pintura.
            drawRect(t.s(0.25f), Offset(caja.left + w * 0.03f, caja.top + h * 0.05f), Size(caja.width - w * 0.06f, caja.height - h * 0.09f), style = Stroke(0.9f * u))
            drawRect(t.b(0.3f), Offset(caja.left + w * 0.03f + 1f * u, caja.top + h * 0.05f + 1f * u), Size(caja.width - w * 0.06f, caja.height - h * 0.09f), style = Stroke(0.6f * u))
            for (k in 0 until 4) drawOval(t.c(HOJALATA), Offset(caja.left + caja.width * (if (k % 2 == 0) 0.01f else 0.93f) + w * 0.01f * azar(m, k), caja.top + caja.height * (0.1f + 0.75f * azar(m + 1, k))), Size(w * 0.025f, h * 0.025f))
            drawOval(t.c(mezcla(ROJO, CAOBA, 0.5f)).copy(alpha = 0.5f), Offset(caja.left + caja.width * 0.85f, caja.bottom - h * 0.1f), Size(w * 0.05f, h * 0.05f))
        }
        // El reborde enrollado y las dos bisagras.
        drawRect(t.c(mezcla(pintura, HOJALATA, 0.3f)), Offset(caja.left - 1f * u, caja.top - 1.5f * u), Size(caja.width + 2f * u, 3.4f * u))
        drawRect(t.b(0.6f), Offset(caja.left - 1f * u, caja.top - 1.5f * u), Size(caja.width + 2f * u, 0.9f * u))
        drawRect(t.s(0.3f), Offset(caja.left, caja.top + 1.9f * u), Size(caja.width, 1.6f * u))
        // Un cabo de mecha que cuelga por delante.
        if (n >= 2) {
            val x = caja.left + caja.width * (0.1f + 0.12f * azar(c.semilla, 70))
            val cabo = Path().apply { moveTo(x, caja.top - h * 0.02f); cubicTo(x + w * 0.025f, caja.top + h * 0.03f, x - w * 0.02f, caja.top + h * 0.07f, x + w * 0.008f, caja.top + h * 0.115f) }
            translate(if (der) -1.5f * u else 1.5f * u, 1.5f * u) { drawPath(cabo, t.s(0.3f), style = Stroke(w * 0.02f)) }
            drawPath(cabo, t.c(mecha), style = Stroke(w * 0.02f, cap = StrokeCap.Round))
            drawPath(cabo, t.c(ROJO).copy(alpha = 0.7f), style = Stroke(1f * u))
        }
        filo(Rect(caja.left, caja.top, caja.right, caja.bottom), t, der)
        tarjeta(w, h, t, mezcla(CARTULINA, HUESO, 0.4f), filete = Math.floorMod(m ushr 5, 2) == 0)
        // La esquina de la etiqueta que se despega.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val e = r.height * 0.2f
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right, r.bottom), t.c(pintura))
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.2f, r.bottom - e * 0.2f), t.c(mezcla(CARTULINA, MOSTAZA, 0.3f)))
    }

    /**
     * Lo que no tiene tema: una lata de galletas de litografía burdeos y oro, con sus dos cenefas, la tapa ladeada encima y
     * las tarjetas sueltas que se salen (más con más fichas).
     */
    private fun DrawScope.galletas(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val lata = Rect(w * 0.045f, h * 0.31f, w * 0.955f, piso)
        val n = c.n.coerceIn(0, 10)
        val roja = mezcla(ROJO, CAOBA_OSC, 0.35f)
        sombraFondo(Rect(lata.left, lata.top - h * 0.06f, lata.right, lata.bottom), w, h, t, der, w * 0.03f)
        // Las tarjetas sueltas que asoman por arriba, revueltas.
        val sueltas = (2 + n / 2).coerceAtMost(6)
        for (i in 0 until sueltas) {
            val x = lata.left + lata.width * (0.06f + 0.8f * i / sueltas)
            val alto = h * (0.2f + 0.08f * azar(c.semilla, i + 81))
            rotate(-16f + 32f * azar(c.semilla, i + 80), Offset(x + w * 0.08f, lata.top)) {
                drawRect(t.s(0.22f), Offset(x + 1.5f * u, lata.top - alto + 1.5f * u), Size(w * 0.17f, alto))
                drawRect(t.c(if (i % 2 == 0) CARTULINA else HUESO), Offset(x, lata.top - alto), Size(w * 0.17f, alto + h * 0.03f))
                drawRect(t.b(0.45f), Offset(x, lata.top - alto), Size(w * 0.17f, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + w * 0.145f, lata.top - alto), Size(w * 0.025f, alto))
                for (j in 0 until 3) if (azar(c.semilla + j, i + 5) < 0.6f) drawRect(t.c(TINTA).copy(alpha = 0.55f), Offset(x + w * (0.03f + 0.04f * j), lata.top - alto + 2.5f * u), Size(1.4f * u, 2.6f * u))
            }
        }
        val forma = Path().apply { addRoundRect(RoundRect(lata, CornerRadius(w * 0.03f))) }
        drawPath(forma, t.c(roja))
        clipPath(forma) {
            // Las cenefas de oro de la litografía, arriba y abajo, con su greca de puntos.
            listOf(lata.top + h * 0.085f, lata.bottom - h * 0.055f).forEach { y ->
                drawRect(t.c(LATON), Offset(lata.left, y), Size(lata.width, h * 0.045f))
                drawRect(t.b(0.4f), Offset(lata.left, y), Size(lata.width, 0.8f * u))
                var x = lata.left + w * 0.02f
                while (x < lata.right) { drawCircle(t.c(roja), 1.3f * u, Offset(x, y + h * 0.0225f)); x += w * 0.032f }
            }
            val lx = if (der) lata.right - lata.width * 0.2f else lata.left + lata.width * 0.07f
            drawRect(t.b(0.18f), Offset(lx, lata.top), Size(lata.width * 0.13f, lata.height))
            drawRect(t.s(0.24f), Offset(if (der) lata.left else lata.right - lata.width * 0.2f, lata.top), Size(lata.width * 0.2f, lata.height))
            drawOval(t.c(HOJALATA).copy(alpha = 0.7f), Offset(lata.right - w * 0.1f, lata.top + lata.height * 0.42f), Size(w * 0.03f, h * 0.04f))
            drawOval(t.c(HOJALATA).copy(alpha = 0.6f), Offset(lata.left + w * 0.01f, lata.top + lata.height * 0.2f), Size(w * 0.02f, h * 0.05f))
        }
        // La tapa ladeada encima, con su reborde de oro.
        rotate(-5f, Offset(lata.right, lata.top)) {
            val tp = Rect(lata.left - w * 0.012f, lata.top - h * 0.075f, lata.right + w * 0.012f, lata.top + h * 0.015f)
            drawRect(t.s(0.32f), Offset(tp.left + 2f * u, tp.bottom), Size(tp.width, 2.4f * u))
            drawRoundRect(t.c(mezcla(roja, LATON, 0.12f)), tp.topLeft, tp.size, CornerRadius(w * 0.018f))
            drawRect(t.c(LATON), Offset(tp.left, tp.bottom - h * 0.028f), Size(tp.width, h * 0.018f))
            drawRect(t.b(0.25f), Offset(if (der) tp.right - tp.width * 0.25f else tp.left + tp.width * 0.06f, tp.top), Size(tp.width * 0.16f, tp.height))
            drawRect(t.b(0.55f), tp.topLeft + Offset(w * 0.012f, 0f), Size(tp.width - w * 0.024f, 1f * u))
            destello(Offset(if (der) tp.right - w * 0.06f else tp.left + w * 0.06f, tp.top + 1.5f * u), 1.2f * u, t)
            filo(tp, t, der)
        }
        tarjeta(w, h, t, mezcla(CARTULINA, HUESO, 0.6f), filete = false)
    }

    /**
     * Un hueco libre: un trapo de algodón colgado de una escarpia, con sus pliegues y su lista roja, y en la tabla una tuerca
     * y un perno sueltos.
     */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val izq = Math.floorMod(c.semilla ushr 2, 2) == 0
        val cl = Offset(if (izq) w * 0.3f else w * 0.68f, h * 0.13f)
        val a = w * 0.19f
        val bajo = h * 0.74f
        // Las puntas del dobladillo: lomos (cuelgan más) y valles, de izquierda a derecha.
        val orla = listOf(Offset(-0.98f, -0.07f), Offset(-0.7f, 0.03f), Offset(-0.42f, -0.06f), Offset(-0.08f, 0.06f), Offset(0.26f, -0.05f), Offset(0.6f, 0.035f), Offset(0.9f, -0.09f))
            .map { Offset(cl.x + a * it.x, bajo + h * it.y) }
        val forma = Path().apply {
            moveTo(cl.x - w * 0.018f, cl.y)
            cubicTo(cl.x - w * 0.06f, cl.y + h * 0.16f, cl.x - a * 1.02f, bajo - h * 0.3f, orla[0].x, orla[0].y)
            for (k in 1 until orla.size) { val p0 = orla[k - 1]; val p1 = orla[k]; cubicTo((p0.x + p1.x) / 2, p0.y, (p0.x + p1.x) / 2, p1.y, p1.x, p1.y) }
            cubicTo(cl.x + a * 0.86f, bajo - h * 0.32f, cl.x + w * 0.055f, cl.y + h * 0.15f, cl.x + w * 0.018f, cl.y)
            close()
        }
        translate(if (der) -w * 0.03f else w * 0.035f, h * 0.03f) { drawPath(forma, t.s(0.34f)) }
        val algodon = mezcla(HUESO, HOJALATA, 0.32f)
        drawPath(forma, t.c(algodon))
        clipPath(forma) {
            // Los pliegues, que nacen en la escarpia: la cara de cada lomo que no mira a la luz, en sombra.
            for (k in listOf(1, 3, 5)) {
                val lomo = orla[k]
                val valle = if (der) orla[k - 1] else orla[k + 1]
                drawPath(poligono(cl.x, cl.y, lomo.x, lomo.y, valle.x, valle.y), t.s(0.24f))
                drawPath(poligono(cl.x, cl.y, lomo.x + (valle.x - lomo.x) * 0.55f, lomo.y, valle.x, valle.y), t.s(0.14f))
                drawLine(t.b(0.5f), Offset(cl.x + (lomo.x - cl.x) * 0.25f, cl.y + (lomo.y - cl.y) * 0.25f), lomo, 1.1f * u)
            }
            // La lista roja cerca del dobladillo, que sigue los pliegues, y la grasa.
            val lista = Path().apply {
                orla.forEachIndexed { k, p ->
                    val q = Offset(p.x, p.y - h * 0.075f)
                    if (k == 0) moveTo(q.x, q.y) else { val p0 = orla[k - 1]; cubicTo((p0.x + q.x) / 2, p0.y - h * 0.075f, (p0.x + q.x) / 2, q.y, q.x, q.y) }
                }
            }
            drawPath(lista, t.c(ROJO).copy(alpha = 0.75f), style = Stroke(2.2f * u))
            drawOval(t.c(HOLLIN).copy(alpha = 0.4f), Offset(cl.x - a * 0.45f, cl.y + h * 0.3f), Size(a * 0.5f, h * 0.1f))
            drawOval(t.c(mezcla(MOSTAZA, CAOBA, 0.5f)).copy(alpha = 0.35f), Offset(cl.x + a * 0.1f, cl.y + h * 0.18f), Size(a * 0.35f, h * 0.08f))
            // El frunce junto a la escarpia.
            drawOval(t.s(0.25f), Offset(cl.x - w * 0.03f, cl.y + h * 0.02f), Size(w * 0.06f, h * 0.07f))
        }
        clavo(cl + Offset(0f, h * 0.015f), 2f * u, t, HIERRO)
        filo(Rect(orla[0].x, orla[0].y - h * 0.3f, orla[6].x, orla[0].y), Tono(t.o, t.k * 0.5f, 0f, 0f), der)
        // La tuerca y el perno, sueltos en la tabla.
        val nc = Offset(if (izq) w * 0.66f else w * 0.2f, piso - h * 0.055f)
        val rr = h * 0.075f
        drawOval(t.s(0.35f), Offset(nc.x - rr * 1.1f + (if (der) -rr * 0.3f else rr * 0.3f), piso - h * 0.022f), Size(rr * 2.3f, h * 0.035f))
        val hexa = Path().apply {
            for (k in 0 until 6) { val ang = Math.toRadians(k * 60.0); val p = Offset(nc.x + rr * cos(ang).toFloat(), nc.y + rr * 0.5f * sin(ang).toFloat()); if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            close()
        }
        drawRect(t.c(mezcla(HIERRO, HOJALATA, 0.3f)), Offset(nc.x - rr, nc.y), Size(rr * 2, rr * 0.55f))
        drawRect(t.s(0.3f), Offset(if (der) nc.x - rr else nc.x + rr * 0.5f, nc.y), Size(rr * 0.5f, rr * 0.55f))
        drawRect(t.b(0.3f), Offset(if (der) nc.x + rr * 0.5f else nc.x - rr, nc.y), Size(rr * 0.5f, rr * 0.55f))
        drawPath(hexa, t.c(mezcla(HIERRO, HOJALATA, 0.55f)))
        drawOval(t.c(CAOBA_OSC), Offset(nc.x - rr * 0.42f, nc.y - rr * 0.21f), Size(rr * 0.84f, rr * 0.42f))
        drawLine(t.b(0.55f), Offset(nc.x - rr, nc.y), Offset(nc.x - rr * 0.5f, nc.y - rr * 0.43f), 0.9f * u)
        // El perno, tumbado: la cabeza hexagonal y la caña roscada.
        val px = nc.x + rr * 1.7f
        val py = piso - h * 0.04f
        val cana = Rect(px + rr * 0.5f, py - h * 0.026f, px + rr * 2.6f, py + h * 0.026f)
        drawOval(t.s(0.3f), Offset(px - rr * 0.2f, piso - h * 0.018f), Size(rr * 3.1f, h * 0.03f))
        drawRect(t.c(mezcla(HIERRO, HOJALATA, 0.45f)), cana.topLeft, cana.size)
        clipRect(cana.left, cana.top, cana.right, cana.bottom) {
            tumbado(cana, t, 1.5f)
            var x = cana.left + cana.width * 0.3f
            while (x < cana.right) { drawLine(t.s(0.4f), Offset(x, cana.bottom), Offset(x + 1.6f * u, cana.top), 0.8f * u); x += 2.4f * u }
        }
        val cabeza = Rect(px, py - h * 0.045f, px + rr * 0.6f, py + h * 0.04f)
        drawRect(t.c(mezcla(HIERRO, HOJALATA, 0.3f)), cabeza.topLeft, cabeza.size)
        clipRect(cabeza.left, cabeza.top, cabeza.right, cabeza.bottom) { tumbado(cabeza, t, 1.5f) }
        // Una mancha de aceite en el canto de la tabla.
        drawOval(t.c(HOLLIN).copy(alpha = 0.3f), Offset(nc.x - rr * 0.6f, piso + 1f * u), Size(rr * 3f, (h - piso) * 0.7f))
    }
    override val rotulo = Rect(0.06f, 0.53f, 0.94f, 0.83f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.caldera
    override val salida = -SALIDA
    override val placa = Rect(0.35f, 0.07f, 0.65f, 0.27f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo de caoba, con dos rieles de latón a lo largo.
        drawRect(t(CAOBA_INTERIOR, oscuro))
        vetas(Rect(0f, 0f, w, h), CAOBA_OSC_INTERIOR.copy(alpha = 0.4f), 71, 1.2f, vertical = true, grosor = 0.7f, ondulacion = 0.5f)
        listOf(w * 0.3f, w * 0.7f).forEach { x ->
            drawRect(t(LATON_OSC, oscuro), Offset(x - 3 * u, 0f), Size(6 * u, h))
            drawLine(t(LATON_LUZ, oscuro), Offset(x - 1.5f * u, 0f), Offset(x - 1.5f * u, h), 1f * u)
        }
        // Las paredes de latón remachadas.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(LATON, oscuro), r.topLeft, r.size) }
        remaches(Offset(pl / 2, pl), Offset(pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        remaches(Offset(w - pl / 2, pl), Offset(w - pl / 2, h - pl), 20 * u, t(LATON_OSC, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.4f)
    }

    /** La caoba del interior y de las fichas, como estaba antes (el casillero de fuera tiene la suya). */
    private val CAOBA_INTERIOR = Color(0xFF5A2A1A)
    private val CAOBA_OSC_INTERIOR = Color(0xFF2E140C)

    override fun tintaInterior(oscuro: Boolean) = VIDRIO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 4

    /** La tarjeta perforada, con la esquina de arriba a la izquierda cortada. */
    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val k = minOf(s.width, s.height) * 0.1f
        moveTo(k, 0f); lineTo(s.width, 0f); lineTo(s.width, s.height); lineTo(0f, s.height); lineTo(0f, k); close()
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(CARTULINA.copy(alpha = 0.45f))
        // Dos bandas de perforaciones, arriba y abajo, con su patrón propio.
        val paso = 5.5f * u * g
        val hw = 1.8f * u * g; val hh = 3.4f * u * g
        listOf(4 * u * g, 9 * u * g, h - 12 * u * g).forEachIndexed { fila, y ->
            var x = minOf(w, h) * 0.12f; var k = 0
            while (x < w - 4 * u) {
                if (azar(semilla + fila * 31, k) < 0.55f) drawRect(TINTA.copy(alpha = 0.75f), Offset(x, y), Size(hw, hh))
                else drawRect(TINTA.copy(alpha = 0.12f), Offset(x, y), Size(hw, hh), style = Stroke(0.5f * u))
                x += paso; k++
            }
        }
        drawLine(ROJO.copy(alpha = 0.5f), Offset(0f, 14.5f * u * g), Offset(w, 14.5f * u * g), 0.7f * u)
    }

    override val margenes = MargenesFicha(18.dp, 12.dp, 16.dp, 32.dp, 24.dp, 28.dp)
    override val proporcionFicha = 1.3f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.8f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El sello de la patente, con sus dos cintas.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x - s * 0.18f, c.y); lineTo(c.x - s * 0.3f, c.y + s * 0.46f); lineTo(c.x - s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawPath(Path().apply { moveTo(c.x + s * 0.18f, c.y); lineTo(c.x + s * 0.3f, c.y + s * 0.46f); lineTo(c.x + s * 0.08f, c.y + s * 0.36f); close() }, LATON_OSC)
                drawCircle(ROJO, s * 0.3f, Offset(c.x, c.y - s * 0.08f)); drawCircle(LATON_LUZ, s * 0.2f, Offset(c.x, c.y - s * 0.08f), style = Stroke(s * 0.05f))
            }
            "convence" -> { drawPath(engrane(c, s * 0.46f, 8), LATON_OSC); drawCircle(LATON_LUZ, s * 0.16f, c) }
            "piensa" -> manometro(c, s * 0.46f, LATON_OSC, VIDRIO, ROJO, 0.4f)
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un perno de cabeza hexagonal. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(estrella(c, s * 0.46f, 3, 1f, 0f), LATON_OSC)
        drawCircle(LATON_LUZ, s * 0.22f, c)
        drawCircle(LATON_OSC, s * 0.22f, c, style = Stroke(s * 0.05f))
    }

    override val desorden = 0f
}
