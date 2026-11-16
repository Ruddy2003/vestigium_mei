package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

// =====================================================================
//  Botánico
// =====================================================================

/**
 * Botánico: el gabinete de trabajo de un naturalista de expedición, hacia 1830, de vuelta del viaje. Es un interior sin
 * ventana: una pared de estudio pintada de verde salvia, con su zócalo de madera, y la luz de día que entra de arriba a la
 * izquierda, fuera del cuadro. El mueble es un gabinete de roble claro con su cornisa moldurada, el marco de cada hueco, el
 * filete de los cantos, la veta, un cerco de vaso en la tapa y una esquina golpeada; abajo, tres cajoncitos de semillas con
 * su portaetiquetas de latón (el del medio lleva la placa de «y N más»), el zócalo y las patas. Encima, el punto focal: la
 * prensa de plantas de pie contra la pared, sus dos rejillas de madera, la pila de papel secante y dos correas con hebilla,
 * con una fronda de helecho y un pliego que asoman; delante, tumbada, la caja de herborizar de hojalata verde con su correa;
 * la campana de vidrio con los helechos (reflejos y condensación) sobre su peana; el frasco de semillas con su tapón de
 * corcho; el quinqué de latón con su pantalla de vidrio verde; la pila de cuadernos de campo atados con cinta y la lupa de
 * mango tumbada. En la pared, un termómetro en su tabla, una lámina botánica enmarcada (la planta con su raíz, a trazo fino),
 * un pliego de herbario clavado con alfileres y un salacot colgado de un gancho. Al pie, sobre el piso de tablas, la red de
 * mariposas en diagonal (el mango sale por el borde izquierdo) y el baúl de viaje con sus etiquetas de embarque gastadas,
 * cortado por el borde derecho. Cada tema es, según la semilla, una caja de herbario de tela verde con el lomo de cuero y dos
 * correas con hebilla, una caja de hojalata lacada en negro o en verde con su asa, su filete dorado y la tapa entreabierta, o
 * un cajón de vitrina con frente de vidrio y bandejitas de semillas, bellotas y cápsulas (más llenas cuanto más guarda); en
 * las tres, la etiqueta de papel con el tema y los pliegos que asoman (más con más fichas). Lo que no tiene tema es una
 * carpeta de cartón atada con cintas y la etiqueta en blanco; un hueco libre lleva una maceta de barro con un esqueje o unas
 * hojas secas sueltas. Al abrir uno, sale hacia arriba. De noche todo se hunde en verde petróleo: alumbra solo el quinqué
 * (un halo cálido en la pared y la tapa, el tinte verde de su pantalla arriba, el filo en el latón y en los vidrios), los
 * huecos de arriba a la derecha encendidos y los de abajo a la izquierda casi perdidos. Por dentro, papel secante con un
 * helecho prensado. Las fichas son pliegos de herbario con su ejemplar sujeto con tiras de papel.
 */
object IdeasBotanico : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del gabinete
    /** La pared verde salvia y su sombra. */
    private val SALVIA = Color(0xFFA3B08F)
    private val SALVIA_OSC = Color(0xFF7C8B6C)
    /** El roble claro del gabinete, su luz y su sombra; el nogal del zócalo, del piso y de los marcos. */
    private val ROBLE = Color(0xFFB88C58)
    private val ROBLE_LUZ = Color(0xFFD8B27C)
    private val ROBLE_OSC = Color(0xFF7A5632)
    private val NOGAL = Color(0xFF5A3E26)
    /** El verde botella de la caja de herborizar, de la hojalata y de la pantalla del quinqué. */
    private val VERDE = Color(0xFF34533A)
    /** La tela verde de las cajas de herbario y el cuero. */
    private val TELA = Color(0xFF4E6A3A)
    private val CUERO = Color(0xFF7A4A28)
    private val CUERO_OSC = Color(0xFF4A2A14)
    /** El latón, el papel, el secante, el cartón, el vidrio y la tinta. */
    private val LATON = Color(0xFFC2A055)
    private val PAPEL = Color(0xFFF1E9D2)
    private val SECANTE = Color(0xFFE4D8BC)
    private val KRAFT = Color(0xFFAE8B58)
    private val VIDRIO = Color(0xFFDDEBEB)
    private val TINTA = Color(0xFF2A2318)
    /** La hoja, el barro de la maceta, el lacre de la cinta y las etiquetas, la tela cruda del salacot. */
    private val HOJA = Color(0xFF5A7A3A)
    private val TERRACOTA = Color(0xFFB0603A)
    private val LACRE = Color(0xFF9A3A2A)
    /** Las lacas de las cajas de hojalata: burdeos (del lacre) y ocre mostaza (del latón). */
    private val BURDEOS = Color(0xFF6A2A28)
    private val MOSTAZA = Color(0xFFA9832F)
    private val CRUDO = Color(0xFFD9C9A0)
    /** La llama del quinqué y el resplandor de su pantalla verde. */
    private val LLAMA = Color(0xFFFFC56E)
    private val VIDRIO_VERDE = Color(0xFF7CC99A)
    /** La noche del gabinete: verde petróleo hondo. */
    private val NOCHE = Color(0xFF0B1C1D)
    /**
     * La etiqueta de noche lejos del quinqué: con [TINTA] da unos 6:1 en el título y 4.6:1 en la cuenta (al 78 %); cerca del
     * quinqué sube hacia [PAPEL] con un poco de llama.
     */
    private val PAPEL_LEJOS = Color(0xFF9E977E)
    /** Las del interior y las fichas (sin cambios). */
    private val FLOR = Color(0xFF9A5A8A)
    private val ROJO = Color(0xFFA8322A)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.35f
    override val corona = 0.62f
    override val base = 0.3f
    override val lado = 0.06f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la tapa, su canto de frente y lo bajo de la moldura, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.86f
    private const val CANTO = 0.93f
    /** En la base: el rincón de la pared con el piso, lo alto del zócalo, las patas, donde pisa el gabinete y las cosas del pie. */
    private const val SUELO = 0.4f
    private const val ZOCALO = 0.42f
    private const val PATAS = 0.52f
    private const val PISAN = 0.58f
    private const val PIE = 0.97f
    /** Los costados del gabinete, en fracción del ancho. */
    private const val IZQ = 0.025f
    private const val DER = 0.975f
    /** El piso de cada hueco (la tabla) y cuánto sube su contenido al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del quinqué, en fracciones del ancho. */
    private val LUZ = Offset(0.735f, 0.338f)

    /** Cuánto le llega del quinqué a un punto [p] (en fracciones del ancho): cae con la distancia; abajo a la izquierda, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.75f else 1f)
        val radial = 1.38f * kotlin.math.exp(-3.4f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que la pantalla echa hacia abajo sobre el frente del gabinete: cae despacio de fila en fila (la primera, encendida;
     * la segunda, a medias; la tercera, tenue; la cuarta y la quinta, casi perdidas), un poco menos en la columna de la
     * izquierda, más lejos del quinqué. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.62f to 0.85f, 0.82f to 0.76f, 1.19f to 0.5f, 1.55f to 0.25f, 1.92f to 0.075f, 2.29f to 0.04f, 2.7f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.5f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.7f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia el
     * suyo según [k] (lo que le llega del quinqué), teñido de llama abajo y del verde de la pantalla arriba ([arriba]).
     */
    private class Tono(val o: Boolean, val k: Float, val arriba: Boolean, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, if (arriba) VIDRIO_VERDE else LLAMA, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del gabinete. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama (o el verde de la pantalla, arriba). */
    private fun Tono.b(a: Float): Color =
        if (!o) luzMueble(a) else (if (arriba) mezcla(VIDRIO_VERDE, LUZ_MUEBLE, 0.4f) else LLAMA).copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono {
        val f = p / anchoW
        val arriba = f.y < LUZ.y - 0.05f && abs(f.x - LUZ.x) < 0.22f
        return Tono(esNoche, if (esNoche) luz(f) else 0f, arriba, dia)
    }

    /** De noche la luz viene del quinqué: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera del quinqué, tanto más marcada
     * cuanto más cerca está de él. No baja de [hasta] (la tapa o el piso que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.011f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.012f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la tapa, la tabla o el piso, corrida a la derecha. */
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

    /** Una hoja: el óvalo con su lado en sombra y su nervio, girada [giro] grados alrededor de su centro. */
    private fun DrawScope.hoja(c: Offset, largo: Float, giro: Float, col: Color, t: Tono) {
        val u = density
        val a = largo * 0.24f
        val l = largo / 2
        rotate(giro, c) {
            val forma = Path().apply {
                moveTo(c.x, c.y + l)
                cubicTo(c.x + a * 1.3f, c.y + l * 0.45f, c.x + a * 0.9f, c.y - l * 0.55f, c.x, c.y - l)
                cubicTo(c.x - a * 0.9f, c.y - l * 0.55f, c.x - a * 1.3f, c.y + l * 0.45f, c.x, c.y + l)
                close()
            }
            drawPath(forma, col)
            clipPath(forma) { drawRect(t.s(0.2f), Offset(c.x, c.y - l), Size(a * 1.4f, largo)) }
            drawLine(t.b(0.4f), Offset(c.x, c.y - l * 0.85f), Offset(c.x, c.y + l * 1.15f), 0.6f * u)
            for (j in 1..3) {
                val y = c.y + l * (0.5f - 0.38f * j)
                drawLine(t.b(0.25f), Offset(c.x, y + l * 0.12f), Offset(c.x - a * 0.6f, y - l * 0.05f), 0.4f * u)
                drawLine(t.s(0.2f), Offset(c.x, y + l * 0.12f), Offset(c.x + a * 0.6f, y - l * 0.05f), 0.4f * u)
            }
        }
    }

    /** Una fronda de helecho: un tallo curvo con pares de foliolos que se achican hacia la punta. */
    internal fun DrawScope.helecho(base: Offset, largo: Float, giro: Float, color: Color, grosor: Float) {
        val pasos = 12
        var p = base
        var a = Math.toRadians(giro.toDouble())
        for (k in 0 until pasos) {
            val f = 1f - k / pasos.toFloat()
            val l = largo / pasos
            val q = Offset(p.x + l * cos(a).toFloat(), p.y + l * sin(a).toFloat())
            drawLine(color, p, q, grosor * (0.4f + 0.6f * f))
            val hoja = largo * 0.16f * f
            listOf(-1.1, 1.1).forEach { da ->
                val b = a + da
                val e = Offset(q.x + hoja * cos(b).toFloat(), q.y + hoja * sin(b).toFloat())
                drawLine(color, q, e, grosor * 1.6f * f, StrokeCap.Round)
            }
            p = q; a += 0.06
        }
    }

    /**
     * El filo cálido del quinqué en un recipiente, de noche: en su canto de arriba y en el costado que mira a la luz ([der]: el
     * derecho), tanto más vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello del quinqué en el latón o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Un tornillo de latón con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(LATON), r, p)
        drawCircle(t.b(0.6f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
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
        val pieF = sup + (frente - sup) * 0.9f
        val suelo = b.top + hb * SUELO

        // ---- la pared, el zócalo de madera y el piso de tablas
        pared(w, ch, suelo)
        piso(w, suelo, alto)

        // ---- en la pared: el termómetro, la lámina, el pliego de herbario y el salacot
        termometro(Rect(w * 0.035f, ch * 0.08f, w * 0.068f, ch * 0.4f))
        lamina(Rect(w * 0.33f, ch * 0.05f, w * 0.505f, ch * 0.43f))
        pliego(Rect(w * 0.555f, ch * 0.04f, w * 0.68f, ch * 0.33f))
        salacot(Offset(w * 0.86f, ch * 0.09f))

        // ---- el gabinete y su tapa
        gabinete(d, ch, canto, b)
        tapa(w, sup, frente, canto)

        // ---- lo de encima, de atrás adelante
        prensa(Offset(w * 0.2f, pieA), sup)
        campana(Offset(w * 0.585f, pieA), sup)
        quinque(Offset(w * 0.735f, pieA), sup)
        cuadernos(Offset(w * 0.875f, pieA), sup)
        vasculo(Offset(w * 0.425f, pieF), sup)
        frasco(Offset(w * 0.66f, pieF), sup)
        lupa(Offset(w * 0.88f, pieF))

        // ---- al pie: el baúl, cortado por el borde derecho, y la red de mariposas
        baul(Offset(w * 0.86f, b.top + hb * PIE), w, hb)
        red(b, w, hb)

        // ---- la placa de «y N más»: el portaetiquetas del cajoncito del medio
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(t.c(LATON), r.topLeft, r.size)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            val dentro = Rect(r.left + 3f * u, r.top + 2.5f * u, r.right - 3f * u, r.bottom - 2.5f * u)
            drawRect(if (o) PAPEL_LEJOS else PAPEL, dentro.topLeft, dentro.size)
            drawRect(t.s(0.15f), dentro.topLeft, Size(dentro.width, 1.5f * u))
        }

        // ---- la luz del quinqué, por encima de lo que tiene cerca
        val llama = LUZ * w
        if (o) {
            mancha(llama + Offset(0f, w * 0.05f), w * 0.42f, LLAMA.copy(alpha = 0.1f))
            mancha(llama + Offset(0f, w * 0.06f), w * 0.17f, LLAMA.copy(alpha = 0.22f))
            mancha(llama - Offset(0f, w * 0.1f), w * 0.14f, VIDRIO_VERDE.copy(alpha = 0.13f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La pared verde salvia: la mancha de luz arriba a la izquierda (de noche, el halo del quinqué), pinceladas, unas grietas
     * finas y los agujeros de clavos viejos; abajo, el zócalo de tablas de nogal con su moldura.
     */
    private fun DrawScope.pared(w: Float, ch: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val dado = suelo - w * 0.2f
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(SALVIA, LUZ_MUEBLE, 0.25f), SALVIA, mezcla(SALVIA, SALVIA_OSC, 0.55f)), 0f, w), Offset.Zero, Size(w, dado))
            mancha(Offset(w * 0.05f, 0f), w * 0.75f, luzMueble(0.42f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.14f)), ch * 0.5f, dado), Offset(0f, ch * 0.5f), Size(w, dado - ch * 0.5f))
        } else {
            fun k(d: Float) = Tono(true, luz(LUZ + Offset(d, 0f)), false).c(SALVIA)
            drawRect(Brush.radialGradient(0f to k(0.05f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), Offset.Zero, Size(w, dado))
        }
        pinceladas(Rect(0f, 0f, w, dado), if (o) NOCHE else SALVIA_OSC, 4001, 40, u, 0.1f)
        pinceladas(Rect(0f, 0f, w, dado), if (o) NOCHE else mezcla(SALVIA, LUZ_MUEBLE, 0.4f), 4002, 18, u, 0.1f)
        // Grietas finas del revoque y clavos viejos.
        listOf(Offset(0.24f, 0.06f), Offset(0.7f, 0.42f), Offset(0.97f, 0.25f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, ch * f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 4) { q += Offset(w * (0.008f + 0.012f * azar(4010 + i, j)) * (if (j % 2 == 0) 1f else -0.4f), w * (0.012f + 0.01f * azar(4020 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.22f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.25f), style = Stroke(0.5f * u)) }
        }
        listOf(Offset(0.27f, 0.36f), Offset(0.53f, 0.5f), Offset(0.96f, 0.6f)).forEach { f ->
            val p = Offset(w * f.x, ch * f.y)
            val t = tn(p)
            drawCircle(t.s(0.35f), 1.1f * u, p)
            drawCircle(t.b(0.3f), 0.7f * u, p + Offset(0.6f * u, 0.6f * u))
        }
        // El zócalo de madera: la moldura de arriba, las tablas y la mugre del rincón.
        val tz = tn(Offset(w * 0.5f, dado))
        val madera = mezcla(NOGAL, ROBLE_OSC, 0.5f)
        drawRect(tz.c(madera), Offset(0f, dado), Size(w, suelo - dado))
        var x = w * 0.02f
        while (x < w) {
            val tt = tn(Offset(x, (dado + suelo) / 2))
            drawLine(tt.s(0.35f), Offset(x, dado), Offset(x, suelo), 1.2f * u)
            drawLine(tt.b(0.25f), Offset(x + 1.2f * u, dado), Offset(x + 1.2f * u, suelo), 0.7f * u)
            x += w * 0.09f
        }
        vetas(Rect(0f, dado, w, suelo), (if (o) NOCHE else NOGAL).copy(alpha = 0.3f), 4030, 0.6f, vertical = true, grosor = 0.6f)
        drawRect(tz.c(mezcla(ROBLE, NOGAL, 0.3f)), Offset(0f, dado - w * 0.012f), Size(w, w * 0.016f))
        drawRect(tz.b(0.45f), Offset(0f, dado - w * 0.012f), Size(w, 1f * u))
        drawRect(tz.s(0.3f), Offset(0f, dado + w * 0.004f), Size(w, 1.5f * u))
        drawRect(tz.s(0.2f), Offset(0f, dado + w * 0.004f + 1.5f * u), Size(w, 3f * u))
    }

    /** El piso de tablas de nogal que se abren hacia quien mira, con sus juntas, su veta y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(mezcla(NOGAL, ROBLE_OSC, 0.25f)), Offset(0f, suelo), Size(w, alto - suelo))
        for (i in -4..14) {
            val x0 = w * i / 10f
            val x1 = w / 2 + (x0 - w / 2) * 1.5f
            drawLine(t.s(0.35f), Offset(x0, suelo), Offset(x1, alto), 1.3f * u)
            drawLine(t.b(0.2f), Offset(x0 + 1.3f * u, suelo), Offset(x1 + 1.3f * u, alto), 0.7f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else ROBLE, 4040, 16, u, 0.14f)
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    /** El termómetro en su tabla de nogal: el tubo con la columna de mercurio, la escala y el bulbo; un clavo arriba. */
    private fun DrawScope.termometro(r: Rect) {
        val u = density
        val t = tn(r.center)
        val tabla = Path().apply { addRoundRect(RoundRect(r, CornerRadius(r.width / 2, r.width / 2))) }
        sombraEnPared(tabla, r.center, size.height, 0.2f)
        drawPath(tabla, t.c(mezcla(NOGAL, ROBLE, 0.35f)))
        clipPath(tabla) {
            vetas(r, (if (esNoche) NOCHE else NOGAL).copy(alpha = 0.4f), 4050, 0.8f, vertical = true, grosor = 0.5f)
            drawRect(t.b(0.25f), r.topLeft, Size(r.width * 0.25f, r.height))
            drawRect(t.s(0.25f), Offset(r.right - r.width * 0.25f, r.top), Size(r.width * 0.25f, r.height))
        }
        // La escala de marfil, el tubo y el bulbo.
        val esc = Rect(r.left + r.width * 0.2f, r.top + r.height * 0.14f, r.right - r.width * 0.2f, r.bottom - r.height * 0.16f)
        drawRect(t.c(PAPEL), esc.topLeft, esc.size)
        for (k in 0..12) {
            val y = esc.top + esc.height * (0.05f + 0.9f * k / 12f)
            drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(esc.left + esc.width * 0.08f, y), Offset(esc.left + esc.width * (if (k % 3 == 0) 0.32f else 0.2f), y), 0.5f * u)
        }
        val cx = esc.center.x + esc.width * 0.12f
        drawLine(t.c(VIDRIO), Offset(cx, esc.top + 2f * u), Offset(cx, r.bottom - r.height * 0.08f), 2.2f * u, StrokeCap.Round)
        drawLine(t.c(Color(0xFF9AA0A2)), Offset(cx, esc.top + esc.height * 0.45f), Offset(cx, r.bottom - r.height * 0.08f), 1f * u)
        drawCircle(t.c(Color(0xFF9AA0A2)), 2.4f * u, Offset(cx, r.bottom - r.height * 0.08f))
        drawCircle(t.b(0.6f), 0.8f * u, Offset(cx - 0.8f * u, r.bottom - r.height * 0.08f - 0.8f * u))
        clavo(Offset(r.center.x, r.top + r.width * 0.3f), 1.3f * u, t)
    }

    /**
     * La lámina botánica enmarcada, un poco torcida: el marco de nogal con su filete dorado, el paspartú, y en el papel la
     * planta entera a trazo fino (la raíz, el tallo, las hojas y la flor), con su leyenda de renglones; el hilo y el clavo.
     */
    private fun DrawScope.lamina(r: Rect) {
        val u = density
        val t = tn(r.center)
        val clavoP = Offset(r.center.x, r.top - r.height * 0.1f)
        drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.2f, r.top + 2f * u), clavoP, 0.6f * u)
        drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(r.right - r.width * 0.2f, r.top + 2f * u), clavoP, 0.6f * u)
        clavo(clavoP, 1.3f * u, t)
        rotate(1.5f, r.center) {
            val marco = Path().apply { addRect(r) }
            sombraEnPared(marco, r.center, size.height, 0.24f)
            drawRect(t.c(NOGAL), r.topLeft, r.size)
            val m = r.width * 0.07f
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(t.b(0.3f), r.topLeft, Size(1.2f * u, r.height))
            drawRect(t.s(0.3f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            val pas = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
            drawRect(t.c(LATON), Offset(pas.left - 1f * u, pas.top - 1f * u), Size(pas.width + 2f * u, pas.height + 2f * u))
            drawRect(t.c(mezcla(PAPEL, KRAFT, 0.12f)), pas.topLeft, pas.size)
            drawRect(t.s(0.25f), pas.topLeft, Size(pas.width, 1.6f * u))
            drawRect(t.s(0.18f), pas.topLeft, Size(1.4f * u, pas.height))
            val p = Rect(pas.left + pas.width * 0.08f, pas.top + pas.height * 0.06f, pas.right - pas.width * 0.08f, pas.bottom - pas.height * 0.06f)
            drawRect(t.c(PAPEL), p.topLeft, p.size)
            // La planta: la raíz abajo, el tallo, cuatro hojas y la flor.
            val tinta = t.c(mezcla(TINTA, CUERO, 0.3f))
            val verde = t.c(mezcla(HOJA, PAPEL, 0.25f))
            val cx = p.center.x
            val raiz = p.top + p.height * 0.74f
            drawLine(t.c(KRAFT).copy(alpha = 0.6f), Offset(p.left + p.width * 0.15f, raiz), Offset(p.right - p.width * 0.15f, raiz), 0.6f * u)
            for (j in 0 until 6) {
                val a = -0.6f + 1.2f * j / 5f
                val q = Path().apply {
                    moveTo(cx, raiz)
                    cubicTo(cx + p.width * 0.08f * a, raiz + p.height * 0.05f, cx + p.width * 0.22f * a, raiz + p.height * 0.08f, cx + p.width * 0.3f * a, raiz + p.height * (0.1f + 0.04f * azar(4060, j)))
                }
                drawPath(q, tinta, style = Stroke(0.6f * u))
            }
            val tallo = Path().apply { moveTo(cx, raiz); cubicTo(cx - p.width * 0.05f, raiz - p.height * 0.25f, cx + p.width * 0.06f, p.top + p.height * 0.35f, cx, p.top + p.height * 0.16f) }
            drawPath(tallo, verde, style = Stroke(1f * u))
            drawPath(tallo, tinta.copy(alpha = 0.5f), style = Stroke(0.4f * u))
            listOf(Triple(0.6f, -1f, -40f), Triple(0.5f, 1f, 45f), Triple(0.38f, -1f, -50f), Triple(0.3f, 1f, 50f)).forEach { (fy, lado, g) ->
                val c = Offset(cx + lado * p.width * 0.15f, p.top + p.height * fy)
                rotate(g, c) {
                    drawOval(verde, Offset(c.x - p.width * 0.05f, c.y - p.height * 0.075f), Size(p.width * 0.1f, p.height * 0.15f))
                    drawOval(tinta.copy(alpha = 0.7f), Offset(c.x - p.width * 0.05f, c.y - p.height * 0.075f), Size(p.width * 0.1f, p.height * 0.15f), style = Stroke(0.4f * u))
                    drawLine(tinta.copy(alpha = 0.6f), Offset(c.x, c.y - p.height * 0.07f), Offset(c.x, c.y + p.height * 0.07f), 0.4f * u)
                }
            }
            val fl = Offset(cx, p.top + p.height * 0.13f)
            for (j in 0 until 5) {
                val a = -1.5708f + j * 1.2566f
                drawCircle(t.c(mezcla(FLOR, PAPEL, 0.25f)), p.width * 0.045f, Offset(fl.x + p.width * 0.05f * cos(a), fl.y + p.width * 0.05f * sin(a)))
            }
            drawCircle(t.c(LATON), p.width * 0.03f, fl)
            // La leyenda, en renglones sin letra.
            listOf(0.6f to 0.9f, 0.4f to 0.95f).forEach { (fw, fy) ->
                drawLine(tinta.copy(alpha = 0.55f), Offset(cx - p.width * fw / 2, p.top + p.height * fy), Offset(cx + p.width * fw / 2, p.top + p.height * fy), 0.6f * u)
            }
            pinceladas(p, if (esNoche) NOCHE else KRAFT, 4061, 5, u, 0.1f)
            // El reflejo del vidrio.
            drawPath(poligono(pas.left, pas.top + pas.height * 0.3f, pas.left + pas.width * 0.3f, pas.top, pas.left + pas.width * 0.45f, pas.top, pas.left, pas.top + pas.height * 0.48f), t.b(0.14f))
        }
    }

    /**
     * El pliego de herbario clavado con cuatro alfileres: el ejemplar (una gramínea con sus espigas) sujeto con tiras de papel,
     * la etiqueta de la colecta en la esquina y la punta de abajo que se levanta.
     */
    private fun DrawScope.pliego(r: Rect) {
        val u = density
        val t = tn(r.center)
        val esq = r.width * 0.16f
        val hoja = poligono(r.left, r.top, r.right, r.top, r.right, r.bottom - esq, r.right - esq, r.bottom, r.left, r.bottom)
        sombraEnPared(hoja, r.center, size.height, 0.2f)
        drawPath(hoja, t.c(mezcla(PAPEL, SECANTE, 0.4f)))
        clipPath(hoja) {
            // La gramínea: tres tallos con hojas largas y espigas.
            val base = Offset(r.left + r.width * 0.4f, r.bottom - r.height * 0.1f)
            val tallo = t.c(mezcla(HOJA, KRAFT, 0.35f))
            listOf(-0.25f to 0.9f, 0.05f to 1f, 0.3f to 0.8f).forEachIndexed { i, (dx, al) ->
                val tope = Offset(base.x + r.width * dx, r.top + r.height * (0.12f + 0.1f * (1f - al)))
                val q = Path().apply { moveTo(base.x, base.y); cubicTo(base.x, base.y - r.height * 0.3f, tope.x, tope.y + r.height * 0.3f, tope.x, tope.y) }
                drawPath(q, tallo, style = Stroke(0.9f * u))
                for (j in 0 until 6) {
                    val e = Offset(tope.x + (if (j % 2 == 0) -1f else 1f) * r.width * 0.025f, tope.y + r.height * 0.025f * j)
                    drawOval(t.c(mezcla(KRAFT, HOJA, 0.3f)), Offset(e.x - r.width * 0.02f, e.y - r.height * 0.015f), Size(r.width * 0.04f, r.height * 0.03f))
                }
                val h0 = Offset(base.x + r.width * dx * 0.4f, base.y - r.height * (0.25f + 0.1f * i))
                drawPath(Path().apply { moveTo(h0.x, h0.y); cubicTo(h0.x + r.width * 0.1f, h0.y - r.height * 0.05f, h0.x + r.width * 0.2f, h0.y - r.height * 0.02f, h0.x + r.width * (0.25f - 0.5f * (i % 2)), h0.y + r.height * 0.04f) },
                    tallo, style = Stroke(1.1f * u, cap = StrokeCap.Round))
            }
            // Las tiras de papel que lo sujetan.
            listOf(0.72f, 0.5f).forEach { fy ->
                val c = Offset(base.x + r.width * 0.02f, r.top + r.height * fy)
                rotate(-12f, c) { drawRect(t.c(PAPEL), Offset(c.x - r.width * 0.1f, c.y - 1.5f * u), Size(r.width * 0.2f, 3f * u)); drawRect(t.s(0.15f), Offset(c.x - r.width * 0.1f, c.y + 1.5f * u), Size(r.width * 0.2f, 0.8f * u)) }
            }
            // La etiqueta de la colecta, abajo a la derecha, con renglones a pluma.
            val et = Rect(r.right - r.width * 0.46f, r.bottom - r.height * 0.24f, r.right - r.width * 0.06f, r.bottom - r.height * 0.1f)
            drawRect(t.c(PAPEL), et.topLeft, et.size)
            drawRect(t.c(TINTA).copy(alpha = 0.5f), et.topLeft, et.size, style = Stroke(0.5f * u))
            for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.55f), Offset(et.left + et.width * 0.1f, et.top + et.height * (0.3f + 0.22f * j)), Offset(et.left + et.width * (0.85f - 0.2f * j), et.top + et.height * (0.3f + 0.22f * j)), 0.5f * u)
            pinceladas(r, if (esNoche) NOCHE else KRAFT, 4070, 6, u, 0.12f)
        }
        // La punta levantada: el revés y su sombra.
        val doblez = poligono(r.right, r.bottom - esq, r.right - esq * 0.2f, r.bottom - esq * 0.15f, r.right - esq, r.bottom)
        translate(1.5f * u, 2f * u) { drawPath(doblez, t.s(0.25f)) }
        drawPath(doblez, t.c(mezcla(SECANTE, KRAFT, 0.4f)))
        // Los alfileres de cabeza de vidrio.
        listOf(Offset(r.left + 2.5f * u, r.top + 2.5f * u), Offset(r.right - 2.5f * u, r.top + 2.5f * u), Offset(r.left + 2.5f * u, r.bottom - 2.5f * u)).forEachIndexed { i, p ->
            drawLine(t.s(0.3f), p, p + Offset(2.5f * u, 2.5f * u), 0.6f * u)
            drawCircle(t.c(if (i == 1) LACRE else mezcla(LATON, PAPEL, 0.2f)), 1.5f * u, p)
            drawCircle(t.b(0.7f), 0.5f * u, p - Offset(0.5f * u, 0.5f * u))
        }
    }

    /**
     * El salacot de tela cruda colgado de una percha de latón por la copa, visto de lado: la copa baja, el ala larga que cae
     * por delante y por detrás, la banda de tela, el botón de ventilación y el barboquejo de cuero que cuelga suelto.
     */
    private fun DrawScope.salacot(gancho: Offset) {
        val u = density
        val w = anchoW
        val sw = w * 0.21f
        val c = Offset(gancho.x, gancho.y + sw * 0.3f)
        val dh = sw * 0.3f
        val t = tn(c)
        val der = luzDerecha(c.x)
        val copa = Path().apply {
            moveTo(c.x - sw * 0.33f, c.y)
            cubicTo(c.x - sw * 0.34f, c.y - dh * 1.2f, c.x + sw * 0.3f, c.y - dh * 1.25f, c.x + sw * 0.34f, c.y)
            close()
        }
        val ala = Path().apply {
            moveTo(c.x - sw * 0.52f, c.y + sw * 0.13f)
            cubicTo(c.x - sw * 0.4f, c.y - sw * 0.03f, c.x + sw * 0.35f, c.y - sw * 0.04f, c.x + sw * 0.5f, c.y + sw * 0.1f)
            lineTo(c.x + sw * 0.48f, c.y + sw * 0.13f)
            cubicTo(c.x + sw * 0.3f, c.y + sw * 0.03f, c.x - sw * 0.35f, c.y + sw * 0.04f, c.x - sw * 0.5f, c.y + sw * 0.16f)
            close()
        }
        val todo = Path().apply { addPath(copa); addPath(ala) }
        sombraEnPared(todo, c, size.height, 0.22f)
        // El barboquejo de cuero, que cuelga suelto por debajo.
        val barb = Path().apply { moveTo(c.x + sw * 0.22f, c.y + sw * 0.05f); cubicTo(c.x + sw * 0.24f, c.y + sw * 0.18f, c.x + sw * 0.2f, c.y + sw * 0.28f, c.x + sw * 0.25f, c.y + sw * 0.36f) }
        translate(w * 0.012f, w * 0.006f) { drawPath(barb, t.s(0.2f), style = Stroke(1.8f * u)) }
        drawPath(barb, t.c(CUERO_OSC), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        drawRect(t.c(LATON), Offset(c.x + sw * 0.215f, c.y + sw * 0.22f), Size(sw * 0.05f, sw * 0.04f), style = Stroke(0.9f * u))
        // El ala, con lo de debajo en sombra.
        drawPath(ala, t.c(mezcla(CRUDO, KRAFT, 0.25f)))
        clipPath(ala) {
            drawRect(t.s(0.28f), Offset(c.x - sw / 2, c.y + sw * 0.06f), Size(sw, sw * 0.12f))
            drawRect(t.b(0.3f), Offset(c.x - sw / 2, c.y - sw * 0.04f), Size(sw, sw * 0.035f))
        }
        drawPath(copa, t.c(CRUDO))
        clipPath(copa) {
            val r = Rect(c.x - sw * 0.34f, c.y - dh * 1.05f, c.x + sw * 0.34f, c.y)
            cilindro(r, t, 1.1f, der)
            // Los paneles cosidos de la copa.
            listOf(-0.12f, 0.14f).forEach { f -> drawPath(Path().apply { moveTo(c.x + sw * f * 0.3f, c.y - dh * 0.92f); cubicTo(c.x + sw * f, c.y - dh * 0.7f, c.x + sw * f * 1.6f, c.y - dh * 0.3f, c.x + sw * f * 1.9f, c.y) }, t.s(0.22f), style = Stroke(0.6f * u)) }
            // La banda de tela (un pañuelo enrollado), con sus pliegues.
            drawRect(t.c(mezcla(KRAFT, CUERO, 0.35f)), Offset(r.left, c.y - dh * 0.36f), Size(r.width, dh * 0.3f))
            for (j in 0 until 6) drawLine(t.s(0.28f), Offset(r.left + r.width * (0.08f + 0.16f * j), c.y - dh * 0.36f), Offset(r.left + r.width * (0.15f + 0.16f * j), c.y - dh * 0.06f), 0.6f * u)
            drawRect(t.b(0.3f), Offset(r.left, c.y - dh * 0.36f), Size(r.width, 0.8f * u))
            // Una mancha de sudor y polvo del viaje.
            drawOval(t.c(mezcla(CRUDO, KRAFT, 0.5f)).copy(alpha = 0.6f), Offset(c.x + sw * 0.08f, c.y - dh * 0.75f), Size(sw * 0.13f, dh * 0.25f))
        }
        // El botón de ventilación y la percha de latón en que descansa.
        drawCircle(t.c(mezcla(CRUDO, KRAFT, 0.3f)), sw * 0.035f, Offset(c.x - sw * 0.01f, c.y - dh * 0.92f))
        drawCircle(t.b(0.5f), sw * 0.015f, Offset(c.x - sw * 0.02f, c.y - dh * 0.95f))
        drawRect(t.s(0.3f), Offset(c.x - sw * 0.02f + 1.5f * u, c.y - dh * 1.05f + 1.5f * u), Size(sw * 0.04f, sw * 0.03f))
    }

    // ---------------------------------------------------------------- el gabinete

    /**
     * El cuerpo del gabinete de roble: su sombra en la pared y el piso, el frente con la veta (de noche más encendido arriba a
     * la derecha, cerca del quinqué), el marco de cada hueco, el filete entre filas, los cantos, los tres cajoncitos de
     * semillas, el zócalo con sus patas y los golpes.
     */
    private fun DrawScope.gabinete(d: Disposicion, ch: Float, canto: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, canto, xR, b.top + hb * ZOCALO)

        // Su sombra en la pared (a la derecha) y en el piso.
        val t0 = tn(Offset(xR, ch * 1.4f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.024f, ch * SUP + w * 0.03f, xR + w * 0.024f, suelo, xR, suelo), t0.s(0.26f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.32f), Offset(xL + w * 0.02f, pisan - 2f * u), Size(xR - xL + w * 0.01f, hb * 0.05f))
        drawRect(tp.s(0.16f), Offset(xL + w * 0.03f, pisan + hb * 0.05f - 2f * u), Size(xR - xL, hb * 0.05f))

        // El frente de roble.
        if (!o) drawRect(ROBLE, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.62f, cuerpo.top + cuerpo.height * i / 5f)).c(ROBLE) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.2f), Color.Transparent, sombraMueble(0.2f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.45f), Color.Transparent), xL, w * 0.7f), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (o) NOCHE else ROBLE_OSC).copy(alpha = 0.3f), 4101, 0.55f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        pinceladas(cuerpo, if (o) NOCHE else ROBLE_OSC, 4102, 24, u, 0.1f)

        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra.
        for (k in 0 until 10) {
            val y0 = canto + cuerpo.height * k / 10f
            val t = tn(Offset(xL, y0 + cuerpo.height / 20f))
            drawRect(t.b(0.45f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f))
            val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
            drawRect(tr.c(ROBLE_OSC), Offset(xR - w * 0.008f, y0), Size(w * 0.008f, cuerpo.height / 10f))
            if (o) drawRect(tr.b(0.5f), Offset(xR - w * 0.008f, y0), Size(1f * u, cuerpo.height / 10f))
        }

        // Los marcos de los huecos, con su grosor y su sombra.
        val m = w * MARCO
        d.huecos.forEachIndexed { i, r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.28f), Offset(marco.left + 2.2f * u, marco.top + 2.6f * u), marco.size)
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.35f)), marco.topLeft, marco.size)
            drawRect(t.b(0.55f), marco.topLeft, Size(marco.width, 1.2f * u))
            drawRect(t.b(0.3f), marco.topLeft, Size(1.2f * u, marco.height))
            drawRect(t.s(0.35f), Offset(marco.left, marco.bottom - 1.4f * u), Size(marco.width, 1.4f * u))
            drawRect(t.s(0.28f), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
        }
        // El filete de los cantos de los estantes: una media caña entre fila y fila, de lado a lado.
        d.filas.forEachIndexed { i, f ->
            val y = f.bottom + d.separacion / 2
            if (i < d.filas.size - 1) {
                val t = tn(Offset(w * 0.5f, y))
                drawRect(t.s(0.3f), Offset(xL, y - 1.6f * u), Size(xR - xL, 1f * u))
                drawRect(t.c(ROBLE_LUZ), Offset(xL, y - 0.6f * u), Size(xR - xL, 1.4f * u))
                drawRect(t.b(0.5f), Offset(xL, y - 0.6f * u), Size(xR - xL, 0.6f * u))
                drawRect(t.s(0.25f), Offset(xL, y + 0.8f * u), Size(xR - xL, 1f * u))
            }
        }
        // El friso bajo la cornisa y la tabla del pie de los huecos.
        val primero = d.huecos.first(); val ultima = d.filas.last()
        listOf(primero.top - m - d.separacion * 0.45f, ultima.bottom + m + d.separacion * 0.4f).forEach { y ->
            val t = tn(Offset(w * 0.5f, y))
            drawRect(t.s(0.25f), Offset(xL, y), Size(xR - xL, 1f * u))
            drawRect(t.b(0.4f), Offset(xL, y + 1f * u), Size(xR - xL, 0.7f * u))
        }

        // ---- los tres cajoncitos de semillas
        val cy0 = b.top + hb * 0.04f; val cy1 = b.top + hb * 0.38f
        listOf(0.035f to 0.33f, 0.34f to 0.66f, 0.67f to 0.965f).forEachIndexed { i, (a, z) ->
            val sale = if (i == 2) hb * 0.025f else 0f
            val r = Rect(w * a + 1.5f * u, cy0 + sale, w * z - 1.5f * u, cy1 + sale)
            cajoncito(r, i, sale)
        }

        // ---- el zócalo con su moldura y las patas
        val tz = tn(Offset(w * 0.5f, b.top + hb * ZOCALO))
        val zoc = Rect(xL - w * 0.008f, b.top + hb * ZOCALO, xR + w * 0.008f, b.top + hb * PATAS)
        drawRect(tz.c(mezcla(ROBLE, ROBLE_OSC, 0.3f)), zoc.topLeft, zoc.size)
        vetas(zoc, (if (o) NOCHE else ROBLE_OSC).copy(alpha = 0.35f), 4110, 0.6f, grosor = 0.5f)
        drawRect(tz.c(ROBLE_LUZ), zoc.topLeft, Size(zoc.width, zoc.height * 0.22f))
        drawRect(tz.b(0.5f), zoc.topLeft, Size(zoc.width, 1f * u))
        drawRect(tz.s(0.3f), Offset(zoc.left, zoc.top + zoc.height * 0.22f), Size(zoc.width, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(zoc.right - w * 0.012f, zoc.top), Size(w * 0.012f, zoc.height))
        // Las patas de escuadra, con el corte curvo por dentro.
        listOf(false, true).forEach { derecha ->
            val x0 = if (derecha) xR - w * 0.085f else xL
            val pw = w * 0.085f
            val y0 = zoc.bottom; val y1 = pisan
            val pata = if (!derecha) Path().apply {
                moveTo(x0, y0); lineTo(x0 + pw, y0); lineTo(x0 + pw, y0 + (y1 - y0) * 0.3f)
                cubicTo(x0 + pw * 0.55f, y0 + (y1 - y0) * 0.35f, x0 + pw * 0.4f, y0 + (y1 - y0) * 0.6f, x0 + pw * 0.38f, y1); lineTo(x0, y1); close()
            } else Path().apply {
                moveTo(x0 + pw, y0); lineTo(x0, y0); lineTo(x0, y0 + (y1 - y0) * 0.3f)
                cubicTo(x0 + pw * 0.45f, y0 + (y1 - y0) * 0.35f, x0 + pw * 0.6f, y0 + (y1 - y0) * 0.6f, x0 + pw * 0.62f, y1); lineTo(x0 + pw, y1); close()
            }
            val t = tn(Offset(x0 + pw / 2, y1))
            drawPath(pata, t.c(mezcla(ROBLE, ROBLE_OSC, 0.35f)))
            clipPath(pata) {
                if (derecha) drawRect(t.s(0.25f), Offset(x0 + pw * 0.6f, y0), Size(pw * 0.4f, y1 - y0))
                else drawRect(t.b(0.25f), Offset(x0, y0), Size(pw * 0.2f, y1 - y0))
            }
        }

        // ---- los golpes: la esquina de abajo a la derecha y unas rozaduras en el zócalo
        val tg = tn(Offset(zoc.right, zoc.top))
        drawPath(poligono(zoc.right - w * 0.03f, zoc.top, zoc.right, zoc.top, zoc.right, zoc.top + zoc.height * 0.45f), tg.c(mezcla(ROBLE_LUZ, PAPEL, 0.35f)))
        drawLine(tg.s(0.4f), Offset(zoc.right - w * 0.03f, zoc.top), Offset(zoc.right, zoc.top + zoc.height * 0.45f), 0.7f * u)
        for (k in 0 until 4) {
            val x = zoc.left + zoc.width * (0.15f + 0.7f * azar(4120, k))
            drawLine(tz.b(0.3f), Offset(x, zoc.top + zoc.height * 0.5f), Offset(x + w * 0.025f, zoc.top + zoc.height * (0.55f + 0.2f * azar(4121, k))), 0.6f * u)
        }
    }

    /** Un cajoncito de semillas: el frente con su veta y su bisel, el portaetiquetas de latón con su tarjeta y el tirador. */
    private fun DrawScope.cajoncito(r: Rect, i: Int, sale: Float) {
        val u = density
        val t = tn(r.center)
        if (sale > 0f) {
            // Un poco abierto: la boca oscura arriba y el costado del cajón.
            drawRect(t.s(0.6f), Offset(r.left, r.top - sale), Size(r.width, sale + 1f * u))
            drawRect(t.c(ROBLE_OSC), Offset(r.left + 2f * u, r.top - sale * 0.6f), Size(r.width - 4f * u, sale * 0.6f))
        }
        drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.2f)), r.topLeft, r.size)
        vetas(r, (if (esNoche) NOCHE else ROBLE_OSC).copy(alpha = 0.35f), 4130 + i, 0.7f, grosor = 0.5f, ondulacion = 0.6f)
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1.1f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(1.1f * u, r.height))
        drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.3f * u), Size(r.width, 1.3f * u))
        drawRect(t.s(0.25f), Offset(r.right - 1.3f * u, r.top), Size(1.3f * u, r.height))
        // El portaetiquetas (el del medio es la placa de «y N más»: su tarjeta la pinta mueble() si hace falta).
        val anchoP = if (i == 1) placa.width * anchoW else r.width * 0.36f
        val pr = if (i == 1) Rect(r.center.x - anchoP / 2, r.top + r.height * 0.14f, r.center.x + anchoP / 2, r.top + r.height * 0.7f)
        else Rect(r.center.x - anchoP / 2, r.top + r.height * 0.18f, r.center.x + anchoP / 2, r.top + r.height * 0.56f)
        drawRect(t.s(0.3f), Offset(pr.left + 1.2f * u, pr.top + 1.5f * u), pr.size)
        drawRect(t.c(LATON), pr.topLeft, pr.size)
        drawRect(t.b(0.55f), pr.topLeft, Size(pr.width, 0.9f * u))
        val tarjeta = Rect(pr.left + 2.5f * u, pr.top + 2f * u, pr.right - 2.5f * u, pr.bottom - 2f * u)
        drawRect(t.c(PAPEL), tarjeta.topLeft, tarjeta.size)
        drawRect(t.s(0.15f), tarjeta.topLeft, Size(tarjeta.width, 1.2f * u))
        // Lo escrito a pluma en la tarjeta: un renglón ondulado.
        val ry = tarjeta.center.y
        drawPath(Path().apply {
            moveTo(tarjeta.left + tarjeta.width * 0.15f, ry)
            cubicTo(tarjeta.left + tarjeta.width * 0.35f, ry - 2f * u, tarjeta.left + tarjeta.width * 0.5f, ry + 2f * u, tarjeta.left + tarjeta.width * (0.7f + 0.1f * i), ry - 0.5f * u)
        }, t.c(TINTA).copy(alpha = 0.6f), style = Stroke(0.7f * u))
        listOf(pr.left + 1.3f * u, pr.right - 1.3f * u).forEach { x -> drawCircle(t.c(mezcla(LATON, NOGAL, 0.4f)), 0.8f * u, Offset(x, pr.center.y)) }
        // El tirador de latón, debajo.
        val pc = Offset(r.center.x, r.top + r.height * (if (i == 1) 0.84f else 0.76f))
        val rp = r.height * 0.09f
        drawCircle(t.s(0.35f), rp, pc + Offset(1.2f * u, 1.6f * u))
        drawCircle(t.c(LATON), rp, pc)
        drawCircle(t.s(0.2f), rp * 0.6f, pc + Offset(rp * 0.25f, rp * 0.25f))
        drawCircle(t.b(0.7f), rp * 0.35f, pc - Offset(rp * 0.35f, rp * 0.35f))
    }

    /**
     * La tapa del gabinete: la tabla vista un poco desde arriba (con su veta, un cerco de vaso y rozaduras), su canto de frente
     * con la moldura y la esquina golpeada, y la sombra que deja la cornisa en el friso.
     */
    private fun DrawScope.tapa(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ - w * 0.014f; val xR = w * DER + w * 0.014f
        val t = tn(Offset(w * 0.55f, sup))
        val tabla = Rect(xL, sup, xR, frente)
        if (!o) drawRect(mezcla(ROBLE_LUZ, ROBLE, 0.25f), tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> tn(Offset(xL + (xR - xL) * i / 5f, sup)).c(mezcla(ROBLE_LUZ, ROBLE, 0.25f)) }, xL, xR), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent, sombraMueble(0.12f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else ROBLE_OSC).copy(alpha = 0.3f), 4140, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.3f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        // El cerco de un vaso y unas rozaduras.
        drawOval(tn(Offset(w * 0.3f, sup)).s(0.2f), Offset(w * 0.27f, sup + (frente - sup) * 0.35f), Size(w * 0.05f, (frente - sup) * 0.45f), style = Stroke(0.9f * u))
        for (k in 0 until 5) {
            val x = w * (0.06f + 0.85f * azar(4141, k))
            drawLine(tn(Offset(x, sup)).b(0.3f), Offset(x, sup + (frente - sup) * (0.3f + 0.4f * azar(4142, k))), Offset(x + w * 0.03f, sup + (frente - sup) * (0.4f + 0.3f * azar(4143, k))), 0.6f * u)
        }
        // El canto: la moldura en dos planos (el de arriba recibe la luz) y el filete.
        val m1 = frente + (canto - frente) * 0.45f
        for (k in 0 until 8) {
            val x0 = xL + (xR - xL) * k / 8f
            val tk = tn(Offset(x0 + (xR - xL) / 16f, frente))
            drawRect(tk.c(ROBLE_LUZ), Offset(x0, frente), Size((xR - xL) / 8f + 0.5f, m1 - frente))
            drawRect(tk.c(mezcla(ROBLE, ROBLE_OSC, 0.25f)), Offset(x0, m1), Size((xR - xL) / 8f + 0.5f, canto - m1))
            drawRect(tk.b(0.7f), Offset(x0, frente), Size((xR - xL) / 8f + 0.5f, 1.2f * u))
            drawRect(tk.b(0.35f), Offset(x0, m1 + 1.2f * u), Size((xR - xL) / 8f + 0.5f, 0.8f * u))
        }
        drawRect(t.s(0.3f), Offset(xL, m1), Size(xR - xL, 0.9f * u))
        drawRect(t.s(0.35f), Offset(xL, canto - 1f * u), Size(xR - xL, 1f * u))
        // La esquina golpeada, arriba a la izquierda: la madera viva que asoma.
        val tg = tn(Offset(xL, frente))
        drawPath(poligono(xL, frente, xL + w * 0.035f, frente, xL + w * 0.012f, m1 + (canto - m1) * 0.5f, xL, canto - 1f * u), tg.c(mezcla(ROBLE_LUZ, PAPEL, 0.3f)))
        drawPath(Path().apply { moveTo(xL + w * 0.035f, frente); lineTo(xL + w * 0.012f, m1 + (canto - m1) * 0.5f); lineTo(xL, canto - 1f * u) }, tg.s(0.4f), style = Stroke(0.7f * u))
        // La sombra de la cornisa en el friso.
        drawRect(t.s(0.32f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.4f * u))
        drawRect(t.s(0.14f), Offset(w * IZQ, canto + 2.4f * u), Size(w * (DER - IZQ), 3f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * La prensa de plantas, de pie contra la pared y un poco ladeada: la rejilla de atrás, la pila de papel secante y cartón
     * entre las dos (se ven sus capas en el canto de arriba y en el costado), el pliego de encima con un helecho prensado que se
     * ve entre los listones, la rejilla de delante con sus clavos, un pliego y una fronda que asoman, y las dos correas de cuero
     * con su hebilla.
     */
    private fun DrawScope.prensa(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val pw = w * 0.25f; val ph = w * 0.32f
        val r = Rect(pie.x - pw / 2, pie.y - ph, pie.x + pw / 2, pie.y)
        val dv = Offset(w * 0.026f, -w * 0.016f)
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        val silueta = poligono(r.left, r.bottom, r.left, r.top, r.left + dv.x, r.top + dv.y, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right, r.bottom)
        val giro = -2.5f
        rotate(giro, Offset(r.left, r.bottom)) {
            sombraEnPared(silueta, r.center, sup + 2f * u, 0.22f)
        }
        contacto(Offset(pie.x + pw * 0.08f, pie.y), pw * 1.15f, 4f * u, 0.36f)
        rotate(giro, Offset(r.left, r.bottom)) {
            val madera = mezcla(ROBLE_LUZ, PAPEL, 0.18f)
            // El costado y el canto de arriba de la pila: las capas de papel, secante y cartón.
            val lado = poligono(r.right, r.top, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right, r.bottom)
            val arriba = poligono(r.left, r.top, r.left + dv.x, r.top + dv.y, r.right + dv.x, r.top + dv.y, r.right, r.top)
            drawPath(lado, t.c(SECANTE))
            drawPath(arriba, t.c(PAPEL))
            val capas = listOf(KRAFT, PAPEL, SECANTE, KRAFT, PAPEL, SECANTE, PAPEL, KRAFT)
            capas.forEachIndexed { j, col ->
                val f = 0.18f + 0.64f * (j + 0.5f) / capas.size
                drawLine(t.c(mezcla(col, TINTA, 0.12f)), Offset(r.right + dv.x * f, r.top + dv.y * f), Offset(r.right + dv.x * f, r.bottom + dv.y * f), 1.4f * u)
                drawLine(t.c(mezcla(col, TINTA, 0.05f)), Offset(r.left + dv.x * f, r.top + dv.y * f), Offset(r.right + dv.x * f, r.top + dv.y * f), 1.1f * u)
            }
            // Las tablas de atrás y de delante, vistas de canto.
            listOf(0f to 0.16f, 0.84f to 1f).forEach { (f0, f1) ->
                drawPath(poligono(r.right + dv.x * f0, r.top + dv.y * f0, r.right + dv.x * f1, r.top + dv.y * f1, r.right + dv.x * f1, r.bottom + dv.y * f1, r.right + dv.x * f0, r.bottom + dv.y * f0), t.c(mezcla(madera, ROBLE_OSC, 0.35f)))
                drawPath(poligono(r.left + dv.x * f0, r.top + dv.y * f0, r.left + dv.x * f1, r.top + dv.y * f1, r.right + dv.x * f1, r.top + dv.y * f1, r.right + dv.x * f0, r.top + dv.y * f0), t.c(mezcla(madera, LUZ_MUEBLE, 0.2f)))
            }
            if (der) drawPath(lado, t.b(0.3f)) else drawPath(lado, t.s(0.22f))
            drawPath(arriba, t.b(0.25f))
            // El pliego y la fronda que asoman por arriba, detrás de la rejilla de delante.
            rotate(4f, Offset(r.left + pw * 0.45f, r.top)) {
                val pl = Rect(r.left + pw * 0.28f, r.top - w * 0.03f, r.left + pw * 0.6f, r.top + ph * 0.1f)
                drawRect(t.c(PAPEL), pl.topLeft, pl.size)
                drawRect(t.b(0.4f), pl.topLeft, Size(pl.width, 1f * u))
                drawRect(t.s(0.15f), Offset(pl.right - pl.width * 0.15f, pl.top), Size(pl.width * 0.15f, pl.height))
                hoja(Offset(pl.left + pl.width * 0.35f, pl.top - w * 0.006f), w * 0.03f, -20f, t.c(mezcla(HOJA, KRAFT, 0.35f)), t)
            }
            helecho(Offset(r.right - pw * 0.2f, r.top + ph * 0.08f), pw * 0.62f, -62f, t.c(HOJA), 1.4f * u)
            helecho(Offset(r.right - pw * 0.3f, r.top + ph * 0.06f), pw * 0.4f, -88f, t.c(mezcla(HOJA, KRAFT, 0.3f)), 1.1f * u)
            // El pliego de delante, entre los listones, con el ejemplar prensado.
            drawRect(t.c(SECANTE), r.topLeft, r.size)
            pinceladas(r, if (esNoche) NOCHE else KRAFT, 4150, 8, u, 0.1f)
            helecho(Offset(r.left + pw * 0.25f, r.bottom - ph * 0.12f), ph * 0.8f, -72f, t.c(mezcla(HOJA, SECANTE, 0.35f)), 1.3f * u)
            hoja(Offset(r.left + pw * 0.72f, r.top + ph * 0.55f), w * 0.05f, 30f, t.c(mezcla(HOJA, KRAFT, 0.45f)), t)
            // La rejilla de delante: el bastidor y los listones, con su sombra en el papel.
            val fb = pw * 0.08f
            val listones = mutableListOf<Rect>()
            listones += Rect(r.left, r.top, r.right, r.top + fb)
            listones += Rect(r.left, r.bottom - fb, r.right, r.bottom)
            listones += Rect(r.left, r.top, r.left + fb, r.bottom)
            listones += Rect(r.right - fb, r.top, r.right, r.bottom)
            listOf(0.3f, 0.52f, 0.74f).forEach { fx -> listones += Rect(r.left + pw * fx - pw * 0.03f, r.top, r.left + pw * fx + pw * 0.03f, r.bottom) }
            listOf(0.36f, 0.64f).forEach { fy -> listones += Rect(r.left, r.top + ph * fy - ph * 0.022f, r.right, r.top + ph * fy + ph * 0.022f) }
            listones.forEach { l -> drawRect(t.s(0.26f), Offset(l.left + 2f * u, l.top + 2.4f * u), l.size) }
            listones.forEachIndexed { j, l ->
                drawRect(t.c(if (j >= 4 && l.width < l.height) mezcla(madera, ROBLE, 0.15f) else madera), l.topLeft, l.size)
                drawRect(t.b(0.45f), l.topLeft, Size(l.width, 0.9f * u))
                drawRect(t.b(0.3f), l.topLeft, Size(0.9f * u, l.height))
                drawRect(t.s(0.25f), Offset(l.left, l.bottom - 1f * u), Size(l.width, 1f * u))
                drawRect(t.s(0.2f), Offset(l.right - 1f * u, l.top), Size(1f * u, l.height))
            }
            vetas(Rect(r.left, r.top, r.left + fb, r.bottom), (if (esNoche) NOCHE else ROBLE_OSC).copy(alpha = 0.3f), 4151, 0.8f, vertical = true, grosor = 0.5f)
            vetas(Rect(r.right - fb, r.top, r.right, r.bottom), (if (esNoche) NOCHE else ROBLE_OSC).copy(alpha = 0.3f), 4152, 0.8f, vertical = true, grosor = 0.5f)
            // Los clavos de los cruces.
            listOf(0.3f, 0.52f, 0.74f).forEach { fx -> listOf(0.36f, 0.64f).forEach { fy ->
                drawCircle(t.c(mezcla(TINTA, ROBLE_OSC, 0.4f)), 0.9f * u, Offset(r.left + pw * fx, r.top + ph * fy))
            } }
            if (der) drawRect(t.b(0.12f), Offset(r.right - pw * 0.3f, r.top), Size(pw * 0.3f, ph))
            else drawRect(t.s(0.12f), Offset(r.right - pw * 0.25f, r.top), Size(pw * 0.25f, ph))
            // Las dos correas de cuero, con su hebilla y la punta que sobra.
            listOf(0.2f, 0.8f).forEachIndexed { j, fy ->
                val y = r.top + ph * fy
                val sh = ph * 0.06f
                drawPath(poligono(r.right, y - sh / 2, r.right + dv.x, y - sh / 2 + dv.y, r.right + dv.x, y + sh / 2 + dv.y, r.right, y + sh / 2), t.c(CUERO_OSC))
                drawRect(t.s(0.3f), Offset(r.left, y + sh / 2), Size(pw, 1.5f * u))
                drawRect(t.c(CUERO), Offset(r.left - 1f * u, y - sh / 2), Size(pw + 1f * u, sh))
                drawRect(t.b(0.4f), Offset(r.left - 1f * u, y - sh / 2), Size(pw + 1f * u, 0.9f * u))
                drawRect(t.s(0.3f), Offset(r.left - 1f * u, y + sh / 2 - 1f * u), Size(pw + 1f * u, 1f * u))
                // Las puntadas.
                var x = r.left + 2f * u
                while (x < r.right - 2f * u) { drawLine(t.c(mezcla(CRUDO, CUERO, 0.3f)).copy(alpha = 0.7f), Offset(x, y - sh * 0.3f), Offset(x + 1.4f * u, y - sh * 0.3f), 0.5f * u); x += 3f * u }
                val hx = r.left + pw * (0.58f + 0.08f * j)
                val hb = Rect(hx, y - sh * 0.8f, hx + sh * 1.1f, y + sh * 0.8f)
                drawRect(t.s(0.3f), Offset(hb.left + 1f * u, hb.top + 1.2f * u), hb.size, style = Stroke(1.5f * u))
                drawRect(t.c(LATON), hb.topLeft, hb.size, style = Stroke(1.5f * u))
                drawLine(t.c(LATON), Offset(hb.left, y), Offset(hb.right + 1f * u, y), 1f * u)
                drawLine(t.b(0.7f), hb.topLeft, Offset(hb.right, hb.top), 0.6f * u)
                // La punta, despegada, que cae un poco.
                val cola = Path().apply { moveTo(hb.right, y - sh / 2); cubicTo(hb.right + pw * 0.08f, y - sh / 2, hb.right + pw * 0.14f, y, hb.right + pw * 0.16f, y + sh * 0.9f); lineTo(hb.right + pw * 0.12f, y + sh * 1.1f); cubicTo(hb.right + pw * 0.1f, y + sh * 0.4f, hb.right + pw * 0.06f, y + sh / 2, hb.right, y + sh / 2); close() }
                translate(1.2f * u, 1.6f * u) { drawPath(cola, t.s(0.3f)) }
                drawPath(cola, t.c(mezcla(CUERO, CUERO_OSC, 0.3f)))
                drawCircle(t.c(CUERO_OSC), 0.8f * u, Offset(hb.right + pw * 0.08f, y + sh * 0.1f))
            }
        }
    }

    /**
     * La caja de herborizar (el vasculum), tumbada: un cilindro chato de hojalata pintada de verde, con su puerta de bisagra,
     * el pestillo de latón, las tapas de los extremos, la pintura saltada y la correa de cuero por detrás.
     */
    private fun DrawScope.vasculo(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val lw = w * 0.24f; val lh = w * 0.082f
        val r = Rect(pie.x - lw / 2, pie.y - lh, pie.x + lw / 2, pie.y)
        val t = tn(r.center)
        val forma = Path().apply { addRoundRect(RoundRect(r, CornerRadius(lh * 0.42f, lh * 0.5f))) }
        sombraEnPared(forma, r.center, sup, 0.18f)
        contacto(Offset(pie.x + lw * 0.06f, pie.y), lw * 1.05f, 4f * u, 0.4f)
        // La correa, por detrás, con sus anillas.
        val atras = Path().apply { moveTo(r.right - lw * 0.07f, r.top + lh * 0.2f); cubicTo(r.right + lw * 0.02f, r.top - lh * 0.5f, r.right + lw * 0.15f, r.top - lh * 0.1f, r.right + lw * 0.12f, r.bottom - lh * 0.05f) }
        drawPath(atras, t.c(CUERO_OSC), style = Stroke(2.6f * u, cap = StrokeCap.Round))
        val pintura = mezcla(VERDE, SALVIA, 0.22f)
        drawPath(forma, t.c(pintura))
        clipPath(forma) {
            tumbado(r, t, 1.2f)
            // Las tapas de los extremos: el filo de la costura.
            listOf(r.left + lw * 0.1f, r.right - lw * 0.1f).forEach { x ->
                drawArc(t.s(0.35f), 90f, if (x < r.center.x) 180f else -180f, false, Offset(x - lh * 0.2f, r.top), Size(lh * 0.4f, lh), style = Stroke(0.9f * u))
            }
            drawRect(t.s(0.2f), Offset(r.right - lw * 0.1f, r.top), Size(lw * 0.1f, lh))
            // La pintura saltada: asoma la hojalata.
            listOf(Offset(0.18f, 0.75f), Offset(0.62f, 0.85f), Offset(0.86f, 0.3f), Offset(0.4f, 0.1f)).forEachIndexed { k, f ->
                drawOval(t.c(Color(0xFFA9ADA4)), Offset(r.left + lw * f.x, r.top + lh * f.y), Size(lw * (0.025f + 0.02f * azar(4160, k)), lh * 0.1f))
            }
        }
        // La puerta, con su bisagra arriba y el pestillo de latón abajo.
        val pu = Rect(r.left + lw * 0.24f, r.top + lh * 0.14f, r.right - lw * 0.24f, r.top + lh * 0.74f)
        drawRoundRect(t.s(0.35f), pu.topLeft, pu.size, CornerRadius(2f * u), style = Stroke(1f * u))
        drawRoundRect(t.b(0.35f), pu.topLeft + Offset(1f * u, 1f * u), pu.size, CornerRadius(2f * u), style = Stroke(0.6f * u))
        for (k in 0 until 5) drawRect(t.c(mezcla(pintura, TINTA, 0.25f)), Offset(pu.left + pu.width * (0.1f + 0.18f * k), pu.top - 1.2f * u), Size(pu.width * 0.09f, 2f * u))
        drawRect(t.b(0.4f), Offset(pu.left, pu.top - 1.2f * u), Size(pu.width, 0.6f * u))
        val pe = Rect(pu.center.x - lw * 0.03f, pu.bottom - 1.5f * u, pu.center.x + lw * 0.03f, pu.bottom + lh * 0.14f)
        drawRect(t.s(0.3f), Offset(pe.left + 1f * u, pe.top + 1f * u), pe.size)
        drawRect(t.c(LATON), pe.topLeft, pe.size)
        drawRect(t.b(0.6f), pe.topLeft, Size(pe.width, 0.8f * u))
        // La correa larga, que cae por delante del canto de la tapa y cuelga sobre el friso.
        val cae = Path().apply {
            moveTo(r.left + lw * 0.08f, r.top + lh * 0.2f)
            cubicTo(r.left - lw * 0.04f, r.top + lh * 0.6f, r.left - lw * 0.02f, r.bottom + lh * 0.2f, r.left + lw * 0.08f, r.bottom + lh * 0.75f)
            cubicTo(r.left + lw * 0.25f, r.bottom + lh * 1.3f, r.left + lw * 0.5f, r.bottom + lh * 1.25f, r.left + lw * 0.62f, r.bottom + lh * 0.3f)
        }
        translate(w * 0.012f, w * 0.008f) { drawPath(cae, t.s(0.28f), style = Stroke(2.8f * u, cap = StrokeCap.Round)) }
        drawPath(cae, t.c(CUERO), style = Stroke(2.8f * u, cap = StrokeCap.Round))
        drawPath(cae, t.b(0.35f), style = Stroke(0.8f * u))
        // Las anillas de la correa en los extremos.
        listOf(r.left + lw * 0.08f, r.right - lw * 0.07f).forEach { x ->
            drawCircle(t.c(LATON), 2.2f * u, Offset(x, r.top + lh * 0.2f), style = Stroke(1.1f * u))
        }
    }

    /**
     * La campana de vidrio sobre su peana de nogal: dentro, la tierra con musgo y tres frondas de helecho (una todavía
     * enrollada); el vidrio con su reflejo largo, el borde, el pomo y las gotas de condensación abajo.
     */
    private fun DrawScope.campana(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val bw = w * 0.16f; val bh = w * 0.026f
        val gw = w * 0.13f; val gh = w * 0.2f
        val t = tn(Offset(pie.x, pie.y - gh / 2))
        val der = luzDerecha(pie.x)
        val peana = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val vb = peana.top - bh * 0.15f
        val vidrio = Path().apply {
            moveTo(pie.x - gw / 2, vb)
            lineTo(pie.x - gw / 2, vb - gh * 0.55f)
            cubicTo(pie.x - gw / 2, vb - gh * 1.02f, pie.x + gw / 2, vb - gh * 1.02f, pie.x + gw / 2, vb - gh * 0.55f)
            lineTo(pie.x + gw / 2, vb)
            close()
        }
        sombraEnPared(vidrio, Offset(pie.x, vb - gh / 2), sup, 0.09f)
        sombraEnPared(Path().apply { addRect(peana) }, peana.center, sup, 0.18f)
        contacto(Offset(pie.x + bw * 0.06f, pie.y), bw * 1.1f, 3.5f * u, 0.4f)
        // La peana torneada.
        drawOval(t.c(mezcla(NOGAL, ROBLE, 0.35f)), Offset(peana.left, peana.top - bh * 0.45f), Size(bw, bh * 0.9f))
        drawRect(t.c(NOGAL), peana.topLeft, peana.size)
        clipRect(peana.left, peana.top, peana.right, peana.bottom) { cilindro(peana, t, 1.2f, der) }
        drawRect(t.b(0.5f), peana.topLeft, Size(bw, 0.9f * u))
        drawRect(t.s(0.3f), Offset(peana.left, peana.top + bh * 0.5f), Size(bw, 0.8f * u))
        // La tierra con musgo.
        val ti = Rect(pie.x - gw * 0.46f, vb - gh * 0.12f, pie.x + gw * 0.46f, vb)
        drawPath(Path().apply { moveTo(ti.left, ti.bottom); cubicTo(ti.left + gw * 0.1f, ti.top, ti.right - gw * 0.2f, ti.top - gh * 0.03f, ti.right, ti.bottom); close() }, t.c(mezcla(NOGAL, TINTA, 0.3f)))
        for (k in 0 until 7) drawCircle(t.c(mezcla(HOJA, SALVIA, 0.3f * azar(4170, k))), gw * (0.04f + 0.03f * azar(4171, k)), Offset(ti.left + ti.width * (0.08f + 0.84f * k / 6f), ti.top + ti.height * (0.35f + 0.3f * azar(4172, k))))
        // Los helechos.
        val raiz = Offset(pie.x - gw * 0.05f, ti.top + ti.height * 0.4f)
        helecho(raiz, gh * 0.75f, -105f, t.c(HOJA), 1.5f * u)
        helecho(raiz + Offset(gw * 0.08f, 0f), gh * 0.6f, -62f, t.c(mezcla(HOJA, VERDE, 0.4f)), 1.4f * u)
        helecho(raiz - Offset(gw * 0.06f, 0f), gh * 0.48f, -140f, t.c(mezcla(HOJA, SALVIA, 0.3f)), 1.2f * u)
        // El báculo: una fronda nueva, todavía enrollada.
        val bc = Offset(pie.x + gw * 0.25f, ti.top - gh * 0.18f)
        drawPath(Path().apply { moveTo(pie.x + gw * 0.18f, ti.top + 2f * u); cubicTo(pie.x + gw * 0.2f, ti.top - gh * 0.1f, bc.x + gw * 0.06f, bc.y + gh * 0.03f, bc.x + gw * 0.04f, bc.y) }, t.c(HOJA), style = Stroke(1.3f * u))
        drawCircle(t.c(HOJA), gw * 0.035f, bc + Offset(gw * 0.01f, 0f), style = Stroke(1.2f * u))
        // El vidrio: el velo, el borde, el reflejo largo, el pomo y la condensación.
        drawPath(vidrio, t.c(VIDRIO).copy(alpha = if (esNoche) 0.08f else 0.18f))
        drawPath(vidrio, (if (esNoche) t.b(0.55f) else luzMueble(0.55f)), style = Stroke(1.1f * u))
        clipPath(vidrio) {
            val bx = if (der) pie.x + gw * 0.3f else pie.x - gw * 0.3f
            drawPath(Path().apply { moveTo(bx, vb - gh * 0.1f); cubicTo(bx, vb - gh * 0.6f, bx + (if (der) -1f else 1f) * gw * 0.05f, vb - gh * 0.82f, bx + (if (der) -1f else 1f) * gw * 0.14f, vb - gh * 0.9f) },
                if (esNoche) t.b(0.8f) else luzMueble(0.7f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawRect(t.s(0.08f), Offset(if (der) pie.x - gw / 2 else pie.x + gw * 0.3f, vb - gh), Size(gw * 0.2f, gh))
            for (k in 0 until 16) {
                val p = Offset(pie.x + gw * (azar(4173, k) - 0.5f) * 0.95f, vb - gh * 0.42f * azar(4174, k))
                drawCircle((if (esNoche) t.b(0.6f) else luzMueble(0.55f)), (0.5f + 0.7f * azar(4175, k)) * u, p)
            }
        }
        drawLine(t.c(mezcla(VIDRIO, SALVIA, 0.4f)), Offset(pie.x - gw / 2, vb), Offset(pie.x + gw / 2, vb), 1.4f * u)
        val po = Offset(pie.x, vb - gh * 0.79f)
        drawCircle(t.c(VIDRIO).copy(alpha = 0.6f), gw * 0.07f, po - Offset(0f, gw * 0.05f))
        drawCircle(if (esNoche) t.b(0.8f) else luzMueble(0.8f), gw * 0.025f, po - Offset(gw * 0.02f, gw * 0.07f))
    }

    /**
     * El quinqué de latón: el pie y la columna, el depósito, el mechero, el tubo de vidrio y la pantalla de vidrio verde. De día
     * está apagado; de noche, la llama bajo la pantalla y el vidrio verde encendido.
     */
    private fun DrawScope.quinque(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.05f))
        val o = esNoche
        val pieB = Rect(pie.x - w * 0.034f, pie.y - w * 0.016f, pie.x + w * 0.034f, pie.y)
        val col = Rect(pie.x - w * 0.007f, pie.y - w * 0.075f, pie.x + w * 0.007f, pieB.top)
        val dep = Rect(pie.x - w * 0.03f, col.top - w * 0.042f, pie.x + w * 0.03f, col.top + w * 0.004f)
        val mech = Rect(pie.x - w * 0.012f, dep.top - w * 0.014f, pie.x + w * 0.012f, dep.top + w * 0.004f)
        val tubo = Rect(pie.x - w * 0.009f, mech.top - w * 0.14f, pie.x + w * 0.009f, mech.top)
        val pantB = mech.top - w * 0.008f
        val pant = Path().apply {
            moveTo(pie.x - w * 0.02f, pantB - w * 0.06f)
            lineTo(pie.x + w * 0.02f, pantB - w * 0.06f)
            cubicTo(pie.x + w * 0.035f, pantB - w * 0.045f, pie.x + w * 0.058f, pantB - w * 0.015f, pie.x + w * 0.062f, pantB)
            lineTo(pie.x - w * 0.062f, pantB)
            cubicTo(pie.x - w * 0.058f, pantB - w * 0.015f, pie.x - w * 0.035f, pantB - w * 0.045f, pie.x - w * 0.02f, pantB - w * 0.06f)
            close()
        }
        val todo = Path().apply { addRect(pieB); addRect(col); addOval(dep); addRect(tubo); addPath(pant) }
        if (!o) sombraEnPared(todo, Offset(pie.x, pie.y - w * 0.1f), sup, 0.16f)
        contacto(Offset(pie.x + w * 0.005f, pie.y), w * 0.08f, 3f * u, 0.4f)
        val laton = t.c(LATON)
        fun brillo(r: Rect) = clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.4f) }
        // El pie y la columna.
        drawPath(poligono(pieB.left, pieB.bottom, pieB.left + w * 0.012f, pieB.top, pieB.right - w * 0.012f, pieB.top, pieB.right, pieB.bottom), laton)
        brillo(pieB)
        drawRect(laton, col.topLeft, col.size); brillo(col)
        drawOval(laton, Offset(pie.x - w * 0.013f, col.top + col.height * 0.4f), Size(w * 0.026f, w * 0.012f))
        // El depósito.
        drawOval(laton, dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            cilindro(dep, t, 1.5f)
            drawRect(t.s(0.2f), Offset(dep.left, dep.center.y + dep.height * 0.15f), Size(dep.width, dep.height))
            drawLine(t.s(0.3f), Offset(dep.left, dep.center.y), Offset(dep.right, dep.center.y), 0.7f * u)
        }
        drawRect(laton, mech.topLeft, mech.size); brillo(mech)
        // La llave de la mecha.
        drawCircle(laton, w * 0.006f, Offset(mech.right + w * 0.006f, mech.center.y))
        // El tubo de vidrio.
        drawRect(t.c(VIDRIO).copy(alpha = 0.3f), tubo.topLeft, tubo.size)
        drawLine(if (o) LLAMA.copy(alpha = 0.6f) else luzMueble(0.7f), Offset(tubo.left + tubo.width * 0.25f, tubo.top + 2f * u), Offset(tubo.left + tubo.width * 0.25f, tubo.bottom - 2f * u), 0.9f * u)
        drawRect(t.s(0.2f), Offset(tubo.right - tubo.width * 0.25f, tubo.top), Size(tubo.width * 0.25f, tubo.height))
        if (o) {
            // La llama, dentro del tubo, bajo la pantalla.
            val lc = Offset(pie.x, mech.top - w * 0.012f)
            drawCircle(LLAMA.copy(alpha = 0.35f), w * 0.03f, lc)
            drawOval(LLAMA, Offset(lc.x - w * 0.006f, lc.y - w * 0.014f), Size(w * 0.012f, w * 0.022f))
            drawOval(Color(0xFFFFF4D8), Offset(lc.x - w * 0.003f, lc.y - w * 0.008f), Size(w * 0.006f, w * 0.012f))
        }
        // La pantalla de vidrio verde, con el vidrio blanco por dentro y su borde.
        drawPath(pant, if (o) mezcla(VERDE, VIDRIO_VERDE, 0.55f) else VERDE)
        clipPath(pant) {
            if (o) {
                drawRect(VIDRIO_VERDE.copy(alpha = 0.5f), Offset(pie.x - w * 0.04f, pantB - w * 0.06f), Size(w * 0.05f, w * 0.06f))
                drawRect(NOCHE.copy(alpha = 0.25f), Offset(pie.x + w * 0.025f, pantB - w * 0.06f), Size(w * 0.04f, w * 0.06f))
            } else {
                drawRect(luzMueble(0.3f), Offset(pie.x - w * 0.05f, pantB - w * 0.06f), Size(w * 0.025f, w * 0.06f))
                drawRect(luzMueble(0.5f), Offset(pie.x - w * 0.038f, pantB - w * 0.06f), Size(w * 0.007f, w * 0.06f))
                drawRect(sombraMueble(0.25f), Offset(pie.x + w * 0.02f, pantB - w * 0.06f), Size(w * 0.045f, w * 0.06f))
            }
        }
        drawOval(if (o) Color(0xFFFFF0CF) else mezcla(PAPEL, SALVIA, 0.25f), Offset(pie.x - w * 0.062f, pantB - w * 0.006f), Size(w * 0.124f, w * 0.012f))
        drawArc(laton, 0f, 180f, false, Offset(pie.x - w * 0.062f, pantB - w * 0.006f), Size(w * 0.124f, w * 0.012f), style = Stroke(1.2f * u))
        // El aro de latón de arriba de la pantalla.
        drawRect(laton, Offset(pie.x - w * 0.021f, pantB - w * 0.064f), Size(w * 0.042f, w * 0.006f))
        if (o) drawCircle(LLAMA.copy(alpha = 0.3f), w * 0.008f, Offset(pie.x - w * 0.01f, dep.top + dep.height * 0.3f))
    }

    /** La pila de cuadernos de campo, cada uno con su tela, atados con una cinta de lacre con su lazo encima. */
    private fun DrawScope.cuadernos(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val libros = listOf(Triple(0.175f, 0.03f, NOGAL), Triple(0.16f, 0.026f, TELA), Triple(0.165f, 0.028f, mezcla(CUERO, KRAFT, 0.3f)), Triple(0.145f, 0.024f, VERDE))
        val desplaza = listOf(0f, -0.008f, 0.006f, -0.004f)
        var y = pie.y
        val rects = mutableListOf<Rect>()
        libros.forEachIndexed { i, (a, g, _) ->
            val cx = pie.x + w * desplaza[i]
            rects += Rect(cx - w * a / 2, y - w * g, cx + w * a / 2, y)
            y -= w * g
        }
        val todo = Path().apply { rects.forEach { addRect(it) } }
        sombraEnPared(todo, Offset(pie.x, (y + pie.y) / 2), sup, 0.2f)
        contacto(Offset(pie.x + w * 0.01f, pie.y), w * 0.17f, 3.5f * u, 0.4f)
        rects.forEachIndexed { i, r ->
            val t = tn(r.center)
            val tela = libros[i].third
            val tapaG = r.height * 0.18f
            // Los dos cartones de las tapas y el canto de las hojas entre ellos (o el lomo, en el segundo).
            if (i == 1) {
                drawRect(t.c(tela), r.topLeft, r.size)
                listOf(0.3f, 0.7f).forEach { f -> drawRect(t.s(0.25f), Offset(r.left + r.width * f, r.top), Size(1f * u, r.height)) }
                drawRect(t.c(LATON).copy(alpha = 0.7f), Offset(r.left + r.width * 0.42f, r.top + r.height * 0.35f), Size(r.width * 0.16f, r.height * 0.3f))
            } else {
                drawRect(t.c(tela), r.topLeft, r.size)
                drawRect(t.c(mezcla(PAPEL, KRAFT, 0.2f)), Offset(r.left + 1.5f * u, r.top + tapaG), Size(r.width - 3f * u, r.height - 2 * tapaG))
                for (j in 1..3) drawLine(t.s(0.2f), Offset(r.left + 2f * u, r.top + tapaG + (r.height - 2 * tapaG) * j / 4f), Offset(r.right - 2f * u, r.top + tapaG + (r.height - 2 * tapaG) * j / 4f), 0.5f * u)
            }
            drawRect(t.b(0.35f), r.topLeft, Size(r.width, 0.9f * u))
            drawRect(t.s(0.25f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
        }
        // Una tarjeta que asoma entre dos cuadernos.
        val r2 = rects[2]
        val tt = tn(r2.center)
        drawRect(tt.c(PAPEL), Offset(r2.left - w * 0.022f, r2.bottom - 2.5f * u), Size(w * 0.03f, 2.5f * u))
        // La cinta y el lazo.
        val tc = tn(Offset(pie.x, y))
        val cx = pie.x + w * 0.02f
        drawRect(tc.c(LACRE), Offset(cx - 1.5f * u, y), Size(3f * u, pie.y - y))
        drawRect(tc.s(0.25f), Offset(cx + 0.5f * u, y), Size(1f * u, pie.y - y))
        val lazo = Offset(cx, y - 1f * u)
        listOf(-1f, 1f).forEach { s ->
            drawOval(tc.c(LACRE), Offset(lazo.x + (if (s < 0) -w * 0.025f else 0f), lazo.y - w * 0.009f), Size(w * 0.025f, w * 0.012f), style = Stroke(1.6f * u))
            drawLine(tc.c(mezcla(LACRE, TINTA, 0.2f)), lazo, lazo + Offset(s * w * 0.016f, w * 0.014f), 1.6f * u, StrokeCap.Round)
        }
        drawCircle(tc.c(LACRE), 2f * u, lazo)
        drawCircle(tc.b(0.5f), 0.8f * u, lazo - Offset(0.6f * u, 0.6f * u))
    }

    /** La lupa de mango, tumbada en la tapa: el aro de latón visto de canto, el vidrio con el brillo y el mango torneado. */
    private fun DrawScope.lupa(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val rx = w * 0.042f; val ry = w * 0.013f
        val centro = Offset(c.x, c.y - ry)
        contacto(Offset(c.x - w * 0.02f, c.y + 0.5f * u), w * 0.13f, 2.6f * u, 0.35f)
        // El mango, hacia la izquierda.
        val m0 = Offset(centro.x - rx, centro.y + ry * 0.2f)
        val m1 = Offset(centro.x - rx - w * 0.06f, centro.y + ry * 0.9f)
        drawLine(t.c(NOGAL), m0, m1, 3.4f * u, StrokeCap.Round)
        drawLine(t.b(0.45f), m0 - Offset(0f, 1f * u), m1 - Offset(0f, 1f * u), 0.9f * u)
        drawLine(t.c(LATON), m0, m0 + (m1 - m0) * 0.12f, 3.8f * u)
        // El vidrio y el aro.
        drawOval(t.c(mezcla(ROBLE_LUZ, VIDRIO, 0.35f)), Offset(centro.x - rx, centro.y - ry), Size(rx * 2, ry * 2))
        drawOval(if (esNoche) t.b(0.8f) else luzMueble(0.6f), Offset(centro.x - rx * 0.6f, centro.y - ry * 0.7f), Size(rx * 0.5f, ry * 0.5f))
        drawOval(t.c(LATON), Offset(centro.x - rx, centro.y - ry), Size(rx * 2, ry * 2), style = Stroke(1.6f * u))
        drawArc(t.b(0.7f), 190f, 120f, false, Offset(centro.x - rx, centro.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.8f * u))
    }

    /** El frasco de semillas: el vidrio, las semillas dentro, el tapón de corcho y la etiqueta escrita a pluma. */
    private fun DrawScope.frasco(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val a = w * 0.042f; val al = w * 0.055f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(pie.x)
        val cuerpo = Path().apply { addRoundRect(RoundRect(r, CornerRadius(a * 0.2f))) }
        sombraEnPared(cuerpo, r.center, sup, 0.12f)
        contacto(Offset(pie.x + a * 0.15f, pie.y), a * 1.3f, 2.5f * u, 0.38f)
        drawPath(cuerpo, t.c(VIDRIO).copy(alpha = 0.35f))
        clipPath(cuerpo) {
            val sem = Rect(r.left, r.top + al * 0.38f, r.right, r.bottom)
            drawRect(t.c(mezcla(KRAFT, NOGAL, 0.4f)), sem.topLeft, sem.size)
            for (k in 0 until 14) drawOval(t.c(if (k % 3 == 0) NOGAL else mezcla(KRAFT, CRUDO, 0.4f)), Offset(sem.left + sem.width * azar(4180, k), sem.top + sem.height * azar(4181, k)), Size(2.4f * u, 1.6f * u))
            cilindro(r, t, 1.3f, der)
        }
        drawPath(cuerpo, t.b(0.4f), style = Stroke(0.8f * u))
        // El corcho.
        val co = Rect(pie.x - a * 0.3f, r.top - al * 0.2f, pie.x + a * 0.3f, r.top + al * 0.05f)
        drawRect(t.c(mezcla(KRAFT, CRUDO, 0.2f)), co.topLeft, co.size)
        clipRect(co.left, co.top, co.right, co.bottom) { cilindro(co, t, 1f, der); for (k in 0 until 5) drawCircle(t.s(0.3f), 0.5f * u, Offset(co.left + co.width * azar(4182, k), co.top + co.height * azar(4183, k))) }
        // La etiqueta.
        val et = Rect(r.left + a * 0.12f, r.top + al * 0.42f, r.right - a * 0.12f, r.top + al * 0.72f)
        drawRect(t.c(PAPEL), et.topLeft, et.size)
        drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(et.left + et.width * 0.15f, et.center.y), Offset(et.right - et.width * 0.2f, et.center.y), 0.6f * u)
        drawRect(t.s(0.18f), Offset(et.right - et.width * 0.25f, et.top), Size(et.width * 0.25f, et.height))
    }

    // ---------------------------------------------------------------- al pie

    /** El baúl de viaje: el cuerpo de lona encerada con sus listones de madera, las cantoneras y la cerradura de latón, y las etiquetas de embarque gastadas. */
    private fun DrawScope.baul(pie: Offset, w: Float, hb: Float) {
        val u = density
        val bw = w * 0.3f; val bh = hb * 0.42f
        val r = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val t = tn(r.center)
        val dv = Offset(-w * 0.0f, -hb * 0.06f)
        contacto(Offset(pie.x, pie.y), bw * 1.15f, 6f * u, 0.45f)
        // La tapa vista un poco desde arriba.
        drawPath(poligono(r.left, r.top, r.left + w * 0.02f, r.top + dv.y, r.right + w * 0.02f, r.top + dv.y, r.right, r.top), t.c(mezcla(CUERO, KRAFT, 0.3f)))
        drawRect(t.c(CUERO), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, if (esNoche) NOCHE else CUERO_OSC, 4190, 8, u, 0.18f)
            drawRect(t.b(0.15f), r.topLeft, Size(bw * 0.12f, bh))
            // La junta de la tapa.
            drawRect(t.s(0.4f), Offset(r.left, r.top + bh * 0.3f), Size(bw, 1.4f * u))
            // Los listones de madera.
            listOf(0.12f, 0.5f, 0.88f).forEach { fx ->
                val l = Rect(r.left + bw * fx - bw * 0.035f, r.top, r.left + bw * fx + bw * 0.035f, r.bottom)
                drawRect(t.s(0.25f), Offset(l.left + 1.5f * u, l.top), l.size)
                drawRect(t.c(ROBLE), l.topLeft, l.size)
                drawRect(t.b(0.35f), l.topLeft, Size(1f * u, l.height))
                listOf(0.12f, 0.88f).forEach { fy -> drawCircle(t.c(LATON), 1f * u, Offset(l.center.x, l.top + l.height * fy)) }
            }
            // Las etiquetas de embarque: un óvalo y un rectángulo con su franja, sin letras, gastadas.
            val e1 = Rect(r.left + bw * 0.18f, r.top + bh * 0.4f, r.left + bw * 0.43f, r.top + bh * 0.8f)
            rotate(-8f, e1.center) {
                drawRect(t.c(mezcla(PAPEL, KRAFT, 0.25f)), e1.topLeft, e1.size)
                drawRect(t.c(LACRE), e1.topLeft, Size(e1.width, e1.height * 0.25f))
                listOf(0.5f, 0.7f).forEach { fy -> drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(e1.left + e1.width * 0.12f, e1.top + e1.height * fy), Offset(e1.right - e1.width * 0.2f, e1.top + e1.height * fy), 0.6f * u) }
                pinceladas(e1, if (esNoche) NOCHE else KRAFT, 4191, 4, u, 0.3f)
            }
            val e2 = Rect(r.left + bw * 0.58f, r.top + bh * 0.38f, r.left + bw * 0.82f, r.top + bh * 0.7f)
            drawOval(t.c(mezcla(CRUDO, SALVIA, 0.3f)), e2.topLeft, e2.size)
            drawOval(t.c(VERDE).copy(alpha = 0.7f), e2.topLeft, e2.size, style = Stroke(1.2f * u))
            drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(e2.left + e2.width * 0.2f, e2.center.y), Offset(e2.right - e2.width * 0.2f, e2.center.y), 0.6f * u)
            drawPath(poligono(e2.right - e2.width * 0.3f, e2.bottom - e2.height * 0.1f, e2.right, e2.center.y, e2.right, e2.bottom), t.c(CUERO))
        }
        // Las cantoneras y la cerradura.
        listOf(r.left to r.top, r.left to r.bottom - bh * 0.18f).forEach { (x, y) ->
            drawRect(t.c(LATON), Offset(x, y), Size(bw * 0.07f, bh * 0.18f))
            drawRect(t.b(0.5f), Offset(x, y), Size(bw * 0.07f, 0.8f * u))
        }
        val ce = Rect(r.left + bw * 0.44f, r.top + bh * 0.22f, r.left + bw * 0.56f, r.top + bh * 0.42f)
        drawRect(t.s(0.3f), Offset(ce.left + 1f * u, ce.top + 1.2f * u), ce.size)
        drawRect(t.c(LATON), ce.topLeft, ce.size)
        drawRect(t.b(0.55f), ce.topLeft, Size(ce.width, 0.8f * u))
        drawCircle(t.c(TINTA), 1f * u, Offset(ce.center.x, ce.center.y + ce.height * 0.1f))
    }

    /**
     * La red de mariposas apoyada en diagonal: el aro en el piso, con su bolsa de gasa, y el mango que sube hacia la izquierda y
     * sale por el borde; su sombra en el zócalo y en el piso.
     */
    private fun DrawScope.red(b: Rect, w: Float, hb: Float) {
        val u = density
        val aro = Offset(w * 0.2f, b.top + hb * 0.78f)
        val ra = w * 0.075f
        val t = tn(aro)
        val m0 = Offset(aro.x - ra * 0.95f, aro.y - ra * 0.15f)
        val m1 = Offset(-w * 0.02f, b.top + hb * 0.02f)
        // La sombra del mango y de la bolsa.
        translate(w * 0.02f, w * 0.008f) { drawLine(t.s(0.25f), m0, m1, 3f * u, StrokeCap.Round) }
        drawOval(t.s(0.3f), Offset(aro.x - ra * 0.9f, b.top + hb * 0.93f), Size(ra * 2.2f, hb * 0.07f))
        // La bolsa de gasa, que cuelga del aro y se apoya en el piso.
        val bolsa = Path().apply {
            moveTo(aro.x - ra, aro.y)
            cubicTo(aro.x - ra * 0.9f, aro.y + ra * 0.7f, aro.x - ra * 0.1f, b.top + hb * 0.98f, aro.x + ra * 0.5f, b.top + hb * 0.96f)
            cubicTo(aro.x + ra * 1.1f, b.top + hb * 0.95f, aro.x + ra * 1.05f, aro.y + ra * 0.4f, aro.x + ra, aro.y)
            close()
        }
        drawPath(bolsa, t.c(mezcla(PAPEL, SALVIA, 0.2f)).copy(alpha = 0.55f))
        clipPath(bolsa) {
            var x = aro.x - ra
            while (x < aro.x + ra * 1.2f) { drawLine(t.s(0.12f), Offset(x, aro.y), Offset(x + ra * 0.3f, b.bottom), 0.5f * u); x += 3f * u }
            drawRect(t.s(0.12f), Offset(aro.x + ra * 0.2f, aro.y), Size(ra, b.bottom - aro.y))
        }
        drawPath(Path().apply { moveTo(aro.x - ra * 0.4f, aro.y + ra * 0.3f); cubicTo(aro.x - ra * 0.2f, aro.y + ra * 0.8f, aro.x + ra * 0.2f, aro.y + ra * 0.9f, aro.x + ra * 0.4f, b.top + hb * 0.94f) }, t.b(0.3f), style = Stroke(0.7f * u))
        // El aro de mimbre, visto un poco de lado, y el mango.
        drawOval(t.c(mezcla(KRAFT, ROBLE, 0.4f)), Offset(aro.x - ra, aro.y - ra * 0.28f), Size(ra * 2, ra * 0.56f), style = Stroke(1.8f * u))
        drawArc(t.b(0.5f), 180f, 120f, false, Offset(aro.x - ra, aro.y - ra * 0.28f), Size(ra * 2, ra * 0.56f), style = Stroke(0.7f * u))
        drawLine(t.c(mezcla(ROBLE, NOGAL, 0.3f)), m0, m1, 3f * u, StrokeCap.Round)
        drawLine(t.b(0.4f), m0 - Offset(0f, 1f * u), m1 - Offset(0f, 1f * u), 0.9f * u)
        drawLine(t.c(LATON), m0, m0 + (m1 - m0) * 0.05f, 3.6f * u)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** El ancho del marco de cada hueco, en fracción del ancho del mueble. */
    private const val MARCO = 0.014f

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega del quinqué.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, false, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        fondoHueco(w, h, t)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> carpeta(w, h, c, t)
                else -> when (Math.floorMod(m, 3)) {
                    0 -> herbario(w, h, c, m, t, der)
                    1 -> hojalata(w, h, c, m, t, der)
                    else -> vitrina(w, h, c, m, t)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: las tablas del fondo del gabinete, la sombra del estante de arriba en escalones, el costado derecho
     * más hondo y la tabla del piso con su canto. De día, la fila de arriba un poco más clara; de noche, el quinqué según lo
     * cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono) {
        val u = density
        drawRect(t.c(mezcla(ROBLE_OSC, NOGAL, 0.3f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.1f), Offset.Zero, Size(w * 0.4f, h))
        // Las juntas de las tablas del fondo.
        listOf(0.27f, 0.55f, 0.81f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h))
            drawRect(t.b(0.18f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h))
        }
        // La sombra del estante de arriba, en escalones, y los costados.
        listOf(0.34f to 0.1f, 0.2f to 0.12f, 0.08f to 0.22f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.2f), Offset(w * 0.93f, 0f), Size(w * 0.07f, h))
        drawRect(t.s(0.14f), Offset.Zero, Size(w * 0.025f, h))
        // La tabla del piso.
        val piso = h * PISO
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.45f), Offset(0f, piso), Size(w, 1f * u))
    }

    /** La etiqueta de papel del tema, con su sombra y el filete a pluma; de noche, más hundida cuanto más lejos del quinqué. */
    private fun DrawScope.etiqueta(w: Float, h: Float, t: Tono, papel: Color = PAPEL, filete: Boolean = true) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.25f), Offset(r.left + 1.2f * u, r.top + 1.5f * u), r.size)
        val fondo = if (t.o) mezcla(mezcla(papel, PAPEL_LEJOS, 0.9f), mezcla(papel, LLAMA, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(papel)
        drawRect(fondo, r.topLeft, r.size)
        if (filete) drawRect(TINTA.copy(alpha = 0.45f), Offset(r.left + 2.2f * u, r.top + 2.2f * u), Size(r.width - 4.4f * u, r.height - 4.4f * u), style = Stroke(0.7f * u))
        drawRect(t.s(0.1f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
    }

    /** Los pliegos que asoman por arriba de [x0]..[x1], desde [arriba]: más y más altos cuanto más guarda, algunos con la punta de una hoja. */
    private fun DrawScope.pliegos(x0: Float, x1: Float, arriba: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantos = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.04f + 0.014f * n)
        for (i in 0 until cuantos) {
            val f = (i + 0.5f) / cuantos
            val pw = (x1 - x0) * (0.3f + 0.15f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - pw) * (f * 0.9f + 0.05f * azar(c.semilla, i + 30))
            val y = arriba - sube * (0.45f + 0.55f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 5f
            rotate(g, Offset(x + pw / 2, arriba)) {
                drawRect(t.s(0.2f), Offset(x + 1.5f * u, y + 1.5f * u), Size(pw, arriba - y))
                drawRect(t.c(if (i % 2 == 0) PAPEL else SECANTE), Offset(x, y), Size(pw, arriba - y + 2f * u))
                drawRect(t.b(0.45f), Offset(x, y), Size(pw, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + pw * 0.85f, y), Size(pw * 0.15f, arriba - y))
                if (i % 2 == 0) hoja(Offset(x + pw * (0.3f + 0.4f * azar(c.semilla, i + 50)), y - h * 0.02f), h * 0.07f, -25f + 50f * azar(c.semilla, i + 60), t.c(mezcla(HOJA, KRAFT, 0.3f * azar(c.semilla, i + 70))), t)
            }
        }
    }

    /**
     * La caja de herbario forrada de tela verde: el lomo de cuero con sus nervios a la izquierda, las puntas de cuero, la tela
     * con su trama, dos correas con hebilla de latón, el borde de la tapa y la etiqueta; asoman los pliegos.
     */
    private fun DrawScope.herbario(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val caja = Rect(w * 0.05f, h * 0.27f, w * 0.95f, piso)
        val tela = when (Math.floorMod(m ushr 3, 3)) { 0 -> TELA; 1 -> mezcla(TELA, VERDE, 0.5f); else -> mezcla(TELA, SALVIA, 0.3f) }
        drawRect(t.s(0.28f), Offset(caja.left + w * 0.035f, caja.top + h * 0.03f), caja.size)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.4f)
        pliegos(caja.left + w * 0.14f, caja.right - w * 0.05f, caja.top + h * 0.03f, h, c, t)
        drawRect(t.c(tela), caja.topLeft, caja.size)
        // La trama de la tela.
        var x = caja.left
        while (x < caja.right) { drawLine(t.s(0.1f), Offset(x, caja.top), Offset(x, caja.bottom), 0.6f * u); x += 3f * u }
        pinceladas(caja, if (t.o) NOCHE else mezcla(tela, LUZ_MUEBLE, 0.4f), c.semilla, 6, u, 0.12f)
        if (der) drawRect(t.b(0.18f), Offset(caja.right - caja.width * 0.15f, caja.top), Size(caja.width * 0.15f, caja.height))
        else { drawRect(t.b(0.15f), caja.topLeft, Size(caja.width * 0.18f, caja.height)); drawRect(t.s(0.2f), Offset(caja.right - caja.width * 0.1f, caja.top), Size(caja.width * 0.1f, caja.height)) }
        // El borde de la tapa.
        drawRect(t.c(mezcla(tela, TINTA, 0.25f)), Offset(caja.left, caja.top), Size(caja.width, h * 0.035f))
        drawRect(t.b(0.45f), caja.topLeft, Size(caja.width, 1f * u))
        drawRect(t.s(0.3f), Offset(caja.left, caja.top + h * 0.035f), Size(caja.width, 1.2f * u))
        // El lomo de cuero con sus nervios y las puntas.
        val lomo = Rect(caja.left, caja.top, caja.left + caja.width * 0.11f, caja.bottom)
        drawRect(t.c(CUERO), lomo.topLeft, lomo.size)
        drawRect(t.b(0.25f), lomo.topLeft, Size(lomo.width * 0.3f, lomo.height))
        drawRect(t.s(0.3f), Offset(lomo.right - 1.4f * u, lomo.top), Size(1.4f * u, lomo.height))
        listOf(0.3f, 0.55f, 0.8f).forEach { fy ->
            drawRect(t.s(0.3f), Offset(lomo.left, lomo.top + lomo.height * fy + 1.2f * u), Size(lomo.width, 1f * u))
            drawRect(t.b(0.4f), Offset(lomo.left, lomo.top + lomo.height * fy), Size(lomo.width, 1.2f * u))
        }
        listOf(true, false).forEach { arriba ->
            val y0 = if (arriba) caja.top else caja.bottom
            val s = if (arriba) 1f else -1f
            drawPath(poligono(caja.right, y0, caja.right - caja.width * 0.09f, y0, caja.right, y0 + s * caja.width * 0.09f), t.c(CUERO))
        }
        // El desgaste de la tela en una esquina.
        drawOval(t.c(mezcla(tela, CRUDO, 0.35f)).copy(alpha = 0.5f), Offset(caja.left + caja.width * 0.12f, caja.bottom - h * 0.05f), Size(w * 0.08f, h * 0.035f))
        // Las dos correas con su hebilla.
        listOf(caja.top + h * 0.1f, piso - h * 0.08f).forEachIndexed { i, y ->
            val sh = h * 0.055f
            drawRect(t.s(0.25f), Offset(caja.left, y + sh), Size(caja.width, 1.2f * u))
            drawRect(t.c(CUERO_OSC), Offset(caja.left, y), Size(caja.width, sh))
            drawRect(t.b(0.35f), Offset(caja.left, y), Size(caja.width, 0.8f * u))
            val hx = caja.left + caja.width * (0.72f + 0.06f * (Math.floorMod(m ushr (5 + i), 2)))
            val hb = Rect(hx, y - sh * 0.3f, hx + sh * 1.2f, y + sh * 1.3f)
            drawRect(t.s(0.3f), Offset(hb.left + 1f * u, hb.top + 1.2f * u), hb.size, style = Stroke(1.4f * u))
            drawRect(t.c(LATON), hb.topLeft, hb.size, style = Stroke(1.4f * u))
            drawLine(t.c(LATON), Offset(hb.left, hb.center.y), Offset(hb.right + 1.5f * u, hb.center.y), 1f * u)
            drawLine(t.b(0.7f), hb.topLeft, Offset(hb.right, hb.top), 0.6f * u)
            destello(Offset(if (der) hb.right else hb.left, hb.top), 1.1f * u, t)
        }
        filo(caja, t, der)
        filo(lomo, t, der)
        etiqueta(w, h, t)
    }

    /**
     * La caja de hojalata lacada (negra o verde según la semilla): el asa de alambre, la tapa entreabierta por la izquierda
     * con los pliegos asomando, el filete dorado, el brillo de la laca, los golpes donde asoma la hojalata y la etiqueta pegada.
     */
    private fun DrawScope.hojalata(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        // La laca, según la semilla: verde botella, burdeos u ocre mostaza; el negro, el menos frecuente.
        val clase = Math.floorMod(m ushr 3, 7)
        val laca = when (clase) { 0, 1 -> BURDEOS; 2, 3 -> MOSTAZA; 4, 5 -> VERDE; else -> Color(0xFF23271F) }
        val oro = if (clase == 2 || clase == 3) mezcla(LATON, LUZ_MUEBLE, 0.45f) else LATON
        val lacaO = mezcla(laca, NOCHE, 0.3f)
        val caja = Rect(w * 0.06f, h * 0.34f, w * 0.94f, piso)
        drawRect(t.s(0.3f), Offset(caja.left + w * 0.035f, caja.top + h * 0.02f), caja.size)
        contacto(Offset(caja.center.x + w * 0.04f, piso), caja.width * 1.05f, h * 0.05f, 0.42f)
        // Los pliegos que asoman por la rendija.
        pliegos(caja.left + w * 0.05f, caja.left + caja.width * 0.7f, caja.top + h * 0.02f, h, c, t)
        drawRect(t.c(laca), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            val lx = if (der) caja.right - caja.width * 0.2f else caja.left + caja.width * 0.1f
            drawRect(t.b(0.08f), Offset(lx - caja.width * 0.06f, caja.top), Size(caja.width * 0.22f, caja.height))
            drawRect(t.b(0.22f), Offset(lx, caja.top), Size(1.2f * u, caja.height))
            drawRect(t.s(0.2f), Offset(if (der) caja.left else caja.right - caja.width * 0.25f, caja.top), Size(caja.width * 0.25f, caja.height))
            // El filete dorado.
            val f = Rect(caja.left + w * 0.035f, caja.top + h * 0.06f, caja.right - w * 0.035f, caja.bottom - h * 0.05f)
            drawRect(t.c(oro).copy(alpha = 0.8f), f.topLeft, f.size, style = Stroke(0.8f * u))
            listOf(f.topLeft, Offset(f.right, f.top), Offset(f.left, f.bottom), f.bottomRight).forEach { p -> drawCircle(t.c(oro).copy(alpha = 0.8f), 1.4f * u, p, style = Stroke(0.6f * u)) }
            // Los golpes: la hojalata que asoma.
            for (k in 0 until 4) drawOval(t.c(Color(0xFF9EA39A)), Offset(caja.left + caja.width * (if (k % 2 == 0) 0.01f else 0.93f) + w * 0.01f * azar(m, k), caja.top + caja.height * (0.15f + 0.7f * azar(m + 1, k))), Size(w * 0.025f, h * 0.025f))
        }
        drawRect(t.b(0.5f), caja.topLeft, Size(caja.width, 1f * u))
        // La tapa, levantada por la izquierda.
        val tapa = Rect(w * 0.045f, h * 0.24f, w * 0.955f, h * 0.36f)
        rotate(-2.2f, Offset(tapa.right, tapa.bottom)) {
            drawRect(t.s(0.3f), Offset(tapa.left + 1.5f * u, tapa.bottom), Size(tapa.width, 2f * u))
            drawRect(t.c(lacaO), tapa.topLeft, tapa.size)
            drawRect(t.c(laca), Offset(tapa.left, tapa.top), Size(tapa.width, tapa.height * 0.55f))
            drawRect(t.b(0.55f), tapa.topLeft, Size(tapa.width, 1.1f * u))
            if (der) drawRect(t.b(0.25f), Offset(tapa.right - tapa.width * 0.2f, tapa.top), Size(tapa.width * 0.2f, tapa.height))
            else drawRect(t.b(0.25f), Offset(tapa.left + tapa.width * 0.05f, tapa.top), Size(tapa.width * 0.12f, tapa.height))
            drawLine(t.c(oro).copy(alpha = 0.8f), Offset(tapa.left + w * 0.03f, tapa.top + tapa.height * 0.75f), Offset(tapa.right - w * 0.03f, tapa.top + tapa.height * 0.75f), 0.7f * u)
            // El asa de alambre, abatida, con sus orejas.
            val a0 = Offset(tapa.left + tapa.width * 0.36f, tapa.top + 1f * u); val a1 = Offset(tapa.left + tapa.width * 0.64f, tapa.top + 1f * u)
            val asa = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x, tapa.top - h * 0.13f, a1.x, tapa.top - h * 0.13f, a1.x, a1.y) }
            translate(1.5f * u, 2f * u) { drawPath(asa, t.s(0.3f), style = Stroke(1.8f * u)) }
            drawPath(asa, t.c(Color(0xFF8F938A)), style = Stroke(1.8f * u, cap = StrokeCap.Round))
            drawPath(asa, t.b(0.5f), style = Stroke(0.6f * u))
            destello(Offset((a0.x + a1.x) / 2, tapa.top - h * 0.098f), 1.2f * u, t)
            filo(tapa, t, der)
            listOf(a0, a1).forEach { p -> drawRect(t.c(lacaO), Offset(p.x - 2.5f * u, p.y - 1f * u), Size(5f * u, 3f * u)); drawCircle(t.c(LATON), 0.9f * u, p + Offset(0f, 0.5f * u)) }
        }
        filo(Rect(caja.left, tapa.bottom, caja.right, caja.bottom), t, der)
        etiqueta(w, h, t, mezcla(PAPEL, KRAFT, 0.15f), filete = Math.floorMod(m ushr 4, 2) == 0)
        // La etiqueta pegada: la esquina que se despega.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val e = r.height * 0.22f
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.15f, r.bottom - e * 0.15f), t.c(mezcla(KRAFT, PAPEL, 0.4f)))
    }

    /**
     * El cajón de vitrina: el frente de roble con su bisel, el vidrio con las bandejitas de semillas, bellotas y cápsulas (más
     * llenas cuanto más guarda) y su reflejo, sobres de semillas que asoman detrás, el portaetiquetas de latón con la
     * etiqueta y el tirador de concha.
     */
    private fun DrawScope.vitrina(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val piso = h * PISO
        val fr = Rect(w * 0.04f, h * 0.15f, w * 0.96f, piso)
        drawRect(t.s(0.28f), Offset(fr.left + w * 0.03f, fr.top + h * 0.03f), fr.size)
        contacto(Offset(fr.center.x + w * 0.04f, piso), fr.width * 1.05f, h * 0.05f, 0.4f)
        // Sobres de semillas que asoman por detrás.
        val n = c.n.coerceIn(0, 10)
        val sobres = (1 + n / 3).coerceIn(1, 4)
        for (i in 0 until sobres) {
            val sx = fr.left + fr.width * (0.12f + 0.2f * i + 0.05f * azar(c.semilla, i))
            val sw = fr.width * 0.15f
            val sy = fr.top - h * (0.05f + 0.012f * n) * (0.6f + 0.4f * azar(c.semilla, i + 5))
            rotate((azar(c.semilla, i + 9) - 0.5f) * 8f, Offset(sx + sw / 2, fr.top)) {
                drawRect(t.c(mezcla(KRAFT, PAPEL, 0.35f + 0.3f * (i % 2))), Offset(sx, sy), Size(sw, fr.top - sy + 2f * u))
                drawPath(poligono(sx, sy, sx + sw, sy, sx + sw / 2, sy + sw * 0.35f), t.s(0.18f))
                drawRect(t.b(0.4f), Offset(sx, sy), Size(sw, 0.8f * u))
            }
        }
        val madera = mezcla(ROBLE, ROBLE_LUZ, 0.45f)
        drawRect(t.c(madera), fr.topLeft, fr.size)
        vetas(fr, (if (t.o) NOCHE else ROBLE_OSC).copy(alpha = 0.3f), c.semilla, 0.6f, grosor = 0.5f, ondulacion = 0.5f)
        drawRect(t.b(0.5f), fr.topLeft, Size(fr.width, 1.1f * u))
        drawRect(t.b(0.3f), fr.topLeft, Size(1.1f * u, fr.height))
        drawRect(t.s(0.3f), Offset(fr.left, fr.bottom - 1.4f * u), Size(fr.width, 1.4f * u))
        drawRect(t.s(0.2f), Offset(fr.right - fr.width * 0.06f, fr.top), Size(fr.width * 0.06f, fr.height))
        // La ventana: el fondo de papel, la rejilla de bandejitas y lo que guardan.
        val ven = Rect(w * 0.09f, h * 0.19f, w * 0.91f, h * 0.485f)
        drawRect(t.s(0.45f), Offset(ven.left - 1.5f * u, ven.top - 1.5f * u), Size(ven.width + 3f * u, ven.height + 3f * u))
        drawRect(t.c(mezcla(SECANTE, KRAFT, 0.3f)), ven.topLeft, ven.size)
        // Cuatro casillas, todas llenas (lo que cambia con las fichas son los sobres de atrás), cada una con lo suyo.
        val columnas = 4
        val cw = ven.width / columnas
        for (i in 0 until columnas) {
            val celda = Rect(ven.left + cw * i, ven.top, ven.left + cw * (i + 1), ven.bottom)
            drawRect(t.s(0.25f), celda.topLeft, Size(celda.width, 2f * u))
            drawRect(t.s(0.15f), celda.topLeft, Size(1.4f * u, celda.height))
            val p = Offset(celda.center.x, celda.center.y + ven.height * 0.06f)
            val s = cw * 1.75f
            // La sombra de lo que hay en la casilla: en su fondo, corrida a la derecha, y la de contacto.
            drawOval(t.s(0.22f), Offset(p.x - s * 0.1f, p.y - s * 0.2f), Size(s * 0.42f, s * 0.46f))
            drawOval(t.s(0.3f), Offset(p.x - s * 0.2f, p.y + s * 0.17f), Size(s * 0.46f, s * 0.08f))
            when ((Math.floorMod(m, 4) + i) % 4) {
                0 -> { // una bellota con su cúpula y su rabillo
                    drawOval(t.c(mezcla(KRAFT, CUERO, 0.4f)), Offset(p.x - s * 0.17f, p.y - s * 0.12f), Size(s * 0.34f, s * 0.36f))
                    drawOval(t.s(0.2f), Offset(p.x + s * 0.02f, p.y - s * 0.1f), Size(s * 0.14f, s * 0.32f))
                    drawOval(t.b(0.45f), Offset(p.x - s * 0.11f, p.y - s * 0.04f), Size(s * 0.07f, s * 0.14f))
                    drawCircle(t.c(NOGAL), s * 0.02f, Offset(p.x, p.y + s * 0.23f))
                    drawArc(t.c(mezcla(NOGAL, KRAFT, 0.3f)), 180f, 180f, true, Offset(p.x - s * 0.2f, p.y - s * 0.24f), Size(s * 0.4f, s * 0.26f))
                    for (q in 0 until 4) drawLine(t.s(0.3f), Offset(p.x - s * 0.15f + s * 0.1f * q, p.y - s * 0.17f), Offset(p.x - s * 0.12f + s * 0.1f * q, p.y - s * 0.12f), 0.6f * u)
                    drawLine(t.b(0.35f), Offset(p.x - s * 0.17f, p.y - s * 0.13f), Offset(p.x + s * 0.05f, p.y - s * 0.2f), 0.7f * u)
                    drawLine(t.c(NOGAL), Offset(p.x, p.y - s * 0.24f), Offset(p.x + s * 0.05f, p.y - s * 0.31f), 1.4f * u, StrokeCap.Round)
                }
                1 -> { // un puñado de semillas redondas, de dos clases
                    for (q in 0 until 6) {
                        val c0 = Offset(p.x + s * (azar(c.semilla, i * 10 + q) - 0.5f) * 0.36f, p.y + s * 0.02f + s * (azar(c.semilla + 3, i * 10 + q) - 0.5f) * 0.26f)
                        val r = s * (0.055f + 0.02f * azar(c.semilla + 5, q))
                        drawCircle(t.s(0.3f), r, c0 + Offset(r * 0.35f, r * 0.35f))
                        drawCircle(t.c(if (q % 2 == 0) NOGAL else mezcla(KRAFT, CRUDO, 0.3f)), r, c0)
                        drawCircle(t.b(0.5f), r * 0.35f, c0 - Offset(r * 0.35f, r * 0.35f))
                    }
                }
                2 -> { // una cápsula de adormidera con su corona
                    drawOval(t.c(mezcla(HOJA, KRAFT, 0.55f)), Offset(p.x - s * 0.15f, p.y - s * 0.16f), Size(s * 0.3f, s * 0.34f))
                    drawOval(t.s(0.2f), Offset(p.x + s * 0.02f, p.y - s * 0.14f), Size(s * 0.12f, s * 0.3f))
                    drawOval(t.b(0.4f), Offset(p.x - s * 0.1f, p.y - s * 0.08f), Size(s * 0.07f, s * 0.16f))
                    drawRect(t.c(mezcla(NOGAL, KRAFT, 0.4f)), Offset(p.x - s * 0.11f, p.y - s * 0.22f), Size(s * 0.22f, s * 0.07f))
                    for (q in 0 until 4) drawLine(t.s(0.35f), Offset(p.x - s * 0.08f + s * 0.055f * q, p.y - s * 0.22f), Offset(p.x - s * 0.08f + s * 0.055f * q, p.y - s * 0.15f), 0.6f * u)
                    drawLine(t.c(mezcla(HOJA, KRAFT, 0.4f)), Offset(p.x, p.y + s * 0.18f), Offset(p.x + s * 0.02f, p.y + s * 0.24f), 1.4f * u)
                }
                else -> { // una vaina abierta con sus semillas
                    rotate(-30f, p) {
                        drawOval(t.c(mezcla(KRAFT, CUERO, 0.2f)), Offset(p.x - s * 0.27f, p.y - s * 0.08f), Size(s * 0.54f, s * 0.16f))
                        drawOval(t.c(mezcla(KRAFT, NOGAL, 0.45f)), Offset(p.x - s * 0.23f, p.y - s * 0.04f), Size(s * 0.46f, s * 0.08f))
                        for (q in 0 until 4) drawCircle(t.c(NOGAL), s * 0.035f, Offset(p.x - s * 0.15f + s * 0.1f * q, p.y))
                        drawLine(t.b(0.45f), Offset(p.x - s * 0.22f, p.y - s * 0.06f), Offset(p.x + s * 0.2f, p.y - s * 0.06f), 0.7f * u)
                    }
                }
            }
            if (i < columnas - 1) {
                drawRect(t.c(madera), Offset(celda.right - 1f * u, celda.top), Size(2f * u, celda.height))
                drawRect(t.b(0.35f), Offset(celda.right - 1f * u, celda.top), Size(0.7f * u, celda.height))
            }
        }
        // El vidrio: el reflejo en diagonal y el canto de luz.
        drawPath(poligono(ven.left + ven.width * 0.06f, ven.bottom, ven.left + ven.width * 0.22f, ven.top, ven.left + ven.width * 0.34f, ven.top, ven.left + ven.width * 0.18f, ven.bottom), if (t.o) LLAMA.copy(alpha = 0.18f * t.k) else luzMueble(0.22f))
        drawPath(poligono(ven.left + ven.width * 0.4f, ven.bottom, ven.left + ven.width * 0.5f, ven.top, ven.left + ven.width * 0.54f, ven.top, ven.left + ven.width * 0.44f, ven.bottom), if (t.o) LLAMA.copy(alpha = 0.12f * t.k) else luzMueble(0.12f))
        drawRect(if (t.o) LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.8f)) else luzMueble(0.5f), Offset(ven.left, ven.top), Size(ven.width, 0.8f * u))
        destello(Offset(ven.right - ven.width * 0.06f, ven.top + ven.height * 0.15f), 1.3f * u, t)
        filo(fr, t, true)
        // El portaetiquetas de latón alrededor de la etiqueta y el tirador de concha debajo.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val pr = Rect(r.left - 2.5f * u, r.top - 2.5f * u, r.right + 2.5f * u, r.bottom + 2.5f * u)
        drawRect(t.s(0.3f), Offset(pr.left + 1.2f * u, pr.top + 1.5f * u), pr.size)
        drawRect(t.c(LATON), pr.topLeft, pr.size)
        drawRect(t.b(0.55f), pr.topLeft, Size(pr.width, 0.9f * u))
        drawRect(t.s(0.3f), Offset(pr.left, pr.bottom - 1f * u), Size(pr.width, 1f * u))
        etiqueta(w, h, t)
        listOf(pr.left + 1.3f * u, pr.right - 1.3f * u).forEach { x -> drawCircle(t.c(mezcla(LATON, NOGAL, 0.4f)), 0.8f * u, Offset(x, pr.center.y)) }
        val tc = Rect(w * 0.4f, pr.bottom - 0.5f * u, w * 0.6f, piso - h * 0.012f)
        if (tc.height > 2f * u) {
            drawArc(t.s(0.35f), 0f, 180f, true, Offset(tc.left + 1f * u, tc.top - tc.height + 1f * u), Size(tc.width, tc.height * 2))
            drawArc(t.c(LATON), 0f, 180f, true, Offset(tc.left, tc.top - tc.height), Size(tc.width, tc.height * 2))
            drawArc(t.b(0.6f), 20f, 140f, false, Offset(tc.left + 1f * u, tc.top - tc.height + 1f * u), Size(tc.width - 2f * u, tc.height * 2 - 2f * u), style = Stroke(0.7f * u))
        }
    }

    /** Lo que no tiene tema: una carpeta de cartón atada con dos cintas y su lazo, con hojas sueltas que asoman y la etiqueta en blanco. */
    private fun DrawScope.carpeta(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        val car = Rect(w * 0.05f, h * 0.3f, w * 0.95f, piso)
        drawRect(t.s(0.28f), Offset(car.left + w * 0.035f, car.top + h * 0.03f), car.size)
        contacto(Offset(car.center.x + w * 0.04f, piso), car.width * 1.05f, h * 0.05f, 0.4f)
        pliegos(car.left + w * 0.04f, car.right - w * 0.04f, car.top + h * 0.03f, h, c, t)
        // Las hojas sueltas que se salen por la derecha.
        val n = c.n.coerceIn(0, 10)
        for (i in 0 until (1 + n / 3).coerceAtMost(3)) {
            val y = car.top + car.height * (0.15f + 0.22f * i)
            drawRect(t.c(if (i % 2 == 0) PAPEL else SECANTE), Offset(car.right - w * 0.04f, y), Size(w * 0.04f + w * 0.015f * (1 + i % 2), car.height * 0.18f))
            drawRect(t.s(0.15f), Offset(car.right - w * 0.04f, y + car.height * 0.18f), Size(w * 0.06f, 0.9f * u))
        }
        val carton = mezcla(KRAFT, SALVIA_OSC, 0.2f)
        // La carpeta es delgada: se ve el canto de sus dos tapas abajo, y el lomo doblado a la izquierda.
        drawRect(t.c(mezcla(carton, NOGAL, 0.3f)), Offset(car.left + w * 0.012f, car.bottom - h * 0.03f), Size(car.width, h * 0.03f))
        drawRect(t.c(carton), car.topLeft, car.size)
        drawRect(t.s(0.25f), Offset(car.left + car.width * 0.07f, car.top), Size(1.2f * u, car.height))
        drawRect(t.b(0.35f), Offset(car.left + car.width * 0.07f + 1.2f * u, car.top), Size(0.8f * u, car.height))
        pinceladas(car, if (t.o) NOCHE else mezcla(KRAFT, TINTA, 0.4f), c.semilla, 8, u, 0.14f)
        drawRect(t.b(0.15f), car.topLeft, Size(car.width * 0.12f, car.height))
        drawRect(t.s(0.18f), Offset(car.right - car.width * 0.08f, car.top), Size(car.width * 0.08f, car.height))
        drawRect(t.b(0.4f), car.topLeft, Size(car.width, 1f * u))
        // Las esquinas gastadas y un cerco de humedad.
        clipRect(car.left, car.top, car.right, car.bottom) {
            listOf(Offset(car.right, car.top), car.bottomRight).forEach { p -> drawCircle(t.c(mezcla(carton, PAPEL, 0.3f)), w * 0.02f, p) }
        }
        drawOval(t.s(0.12f), Offset(car.left + car.width * 0.62f, car.top + car.height * 0.08f), Size(w * 0.16f, h * 0.1f), style = Stroke(1.2f * u))
        // Las cintas de algodón, de los costados al lazo del medio, arriba.
        val ly = car.top + h * 0.11f
        val lc = Offset(w * 0.5f, ly)
        val cinta = t.c(mezcla(CRUDO, PAPEL, 0.4f))
        listOf(car.left, car.right).forEach { x ->
            drawLine(t.s(0.25f), Offset(x, ly + 1.5f * u), lc + Offset(0f, 1.5f * u), 2.4f * u)
            drawLine(cinta, Offset(x, ly), lc, 2.4f * u)
        }
        listOf(-1f, 1f).forEach { s ->
            drawOval(cinta, Offset(lc.x + (if (s < 0) -w * 0.09f else 0f), lc.y - h * 0.05f), Size(w * 0.09f, h * 0.07f), style = Stroke(2f * u))
            drawLine(cinta, lc, lc + Offset(s * w * 0.06f, h * 0.1f), 2.2f * u, StrokeCap.Round)
        }
        drawCircle(cinta, 2.6f * u, lc)
        drawCircle(t.s(0.2f), 2.6f * u, lc + Offset(0.8f * u, 0.8f * u), style = Stroke(0.6f * u))
        filo(car, t, true)
        etiqueta(w, h, t, mezcla(PAPEL, KRAFT, 0.1f), filete = false)
    }

    /** Un hueco libre: una maceta de barro con un esqueje, o unas hojas secas sueltas con una bellota, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            val cx = w * 0.4f
            val pw = w * 0.24f; val ph = h * 0.34f
            val boca = piso - ph
            val maceta = poligono(cx - pw / 2, boca, cx + pw / 2, boca, cx + pw * 0.36f, piso, cx - pw * 0.36f, piso)
            translate(w * 0.035f, h * 0.02f) { drawPath(maceta, t.s(0.25f)) }
            contacto(Offset(cx + pw * 0.1f, piso), pw * 1.2f, h * 0.05f, 0.42f)
            drawPath(maceta, t.c(TERRACOTA))
            clipPath(maceta) {
                cilindro(Rect(cx - pw / 2, boca, cx + pw / 2, piso), t, 1.2f)
                drawOval(t.c(mezcla(TERRACOTA, CRUDO, 0.5f)).copy(alpha = 0.5f), Offset(cx - pw * 0.1f, piso - ph * 0.3f), Size(pw * 0.3f, ph * 0.2f))
            }
            val borde = Rect(cx - pw * 0.55f, boca - h * 0.05f, cx + pw * 0.55f, boca)
            drawRect(t.c(mezcla(TERRACOTA, CUERO, 0.2f)), borde.topLeft, borde.size)
            clipRect(borde.left, borde.top, borde.right, borde.bottom) { cilindro(borde, t, 1.2f) }
            drawOval(t.c(mezcla(NOGAL, TINTA, 0.3f)), Offset(borde.left + 2f * u, borde.top - h * 0.015f), Size(borde.width - 4f * u, h * 0.03f))
            // El esqueje: el tallo y cinco hojas.
            val tallo = Path().apply { moveTo(cx, borde.top); cubicTo(cx - w * 0.02f, borde.top - h * 0.15f, cx + w * 0.03f, borde.top - h * 0.3f, cx + w * 0.01f, h * 0.12f) }
            drawPath(tallo, t.c(mezcla(HOJA, NOGAL, 0.3f)), style = Stroke(1.4f * u))
            listOf(Triple(0.85f, -1f, -50f), Triple(0.65f, 1f, 50f), Triple(0.45f, -1f, -40f), Triple(0.28f, 1f, 35f), Triple(0.12f, 0f, 5f)).forEachIndexed { i, (f, s, g) ->
                val y = borde.top - (borde.top - h * 0.12f) * (1f - f)
                hoja(Offset(cx + s * w * 0.05f + w * 0.01f, y), h * 0.15f, g, t.c(mezcla(HOJA, SALVIA, 0.15f * i)), t)
            }
            // Una etiqueta de madera clavada en la tierra.
            drawRect(t.c(CRUDO), Offset(cx + pw * 0.18f, borde.top - h * 0.08f), Size(w * 0.02f, h * 0.1f))
        } else {
            // Unas hojas secas sueltas sobre la tabla, una bellota y una ramita.
            listOf(Triple(0.32f, -15f, 0.42f), Triple(0.57f, 70f, 0.34f), Triple(0.78f, -60f, 0.38f)).forEachIndexed { i, (fx, g, l) ->
                val p = Offset(w * fx, piso - h * 0.07f)
                translate(w * 0.015f, h * 0.02f) { rotate(g + 90f, p) { drawOval(t.s(0.25f), Offset(p.x - h * l * 0.22f, p.y - h * l * 0.5f), Size(h * l * 0.44f, h * l)) } }
                hoja(p, h * l, g + 90f, t.c(mezcla(KRAFT, if (i == 1) LACRE else CUERO, 0.3f)), t)
            }
            val b = Offset(w * 0.15f, piso - h * 0.05f)
            contacto(Offset(b.x + w * 0.01f, piso), w * 0.08f, h * 0.03f, 0.4f)
            drawOval(t.c(mezcla(KRAFT, CUERO, 0.4f)), Offset(b.x - w * 0.025f, b.y - h * 0.04f), Size(w * 0.05f, h * 0.08f))
            drawArc(t.c(NOGAL), 180f, 180f, true, Offset(b.x - w * 0.03f, b.y - h * 0.08f), Size(w * 0.06f, h * 0.06f))
            drawPath(Path().apply { moveTo(w * 0.45f, piso - h * 0.01f); cubicTo(w * 0.6f, piso - h * 0.03f, w * 0.7f, piso - h * 0.02f, w * 0.88f, piso - h * 0.06f) }, t.c(mezcla(NOGAL, KRAFT, 0.3f)), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
    }

    override val rotulo = Rect(0.07f, 0.52f, 0.93f, 0.82f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fraunces
    override val salida = -SALIDA
    override val placa = Rect(0.38f, 0.085f, 0.62f, 0.29f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Papel secante, con un helecho grande prensado y su etiqueta en la esquina.
        val s = if (oscuro) mezcla(SECANTE, Color.Black, 0.6f) else SECANTE
        drawRect(s)
        for (k in 0 until 40) drawCircle(Color.Black.copy(alpha = 0.03f), (6f + 20f * azar(95, k)) * u, Offset(w * azar(96, k), h * azar(97, k)))
        helecho(Offset(w * 0.25f, h * 0.95f), h * 0.85f, -75f, HOJA.copy(alpha = 0.16f), 3 * u)
        drawRect(PAPEL.copy(alpha = 0.6f), Offset(w * 0.62f, h * 0.86f), Size(w * 0.3f, h * 0.08f))
        drawRect(TINTA.copy(alpha = 0.3f), Offset(w * 0.62f, h * 0.86f), Size(w * 0.3f, h * 0.08f), style = Stroke(0.8f * u))
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(TELA, oscuro), r.topLeft, r.size) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.25f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) PAPEL else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PAPEL.copy(alpha = 0.4f))
        // El ejemplar prensado abajo a la derecha, sujeto con dos tiras de papel.
        val b = Offset(w * 0.9f, h * 0.98f)
        helecho(b, minOf(w, h) * 0.5f, -130f + 20f * azar(semilla, 1), HOJA.copy(alpha = 0.3f), 1.2f * u * g)
        listOf(0.25f, 0.6f).forEach { f ->
            val c = Offset(b.x - minOf(w, h) * 0.5f * f * 0.64f, b.y - minOf(w, h) * 0.5f * f * 0.77f)
            rotate(-40f, c) { drawRect(Color.White.copy(alpha = 0.55f), Offset(c.x - 7 * u * g, c.y - 2 * u * g), Size(14 * u * g, 4 * u * g)) }
        }
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 12.dp, 24.dp, 28.dp, 26.dp)
    override val proporcionFicha = 0.8f
    override val anchoGrande = 0.9f
    override val proporcionGrande = 0.7f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El ejemplar clavado con su alfiler: ya está identificado.
            "hecho" -> {
                drawLine(LATON, Offset(c.x - s * 0.3f, c.y + s * 0.42f), Offset(c.x + s * 0.18f, c.y - s * 0.26f), s * 0.07f, StrokeCap.Round)
                drawCircle(ROJO, s * 0.16f, Offset(c.x + s * 0.2f, c.y - s * 0.28f))
            }
            "convence" -> {
                drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + s * 0.36f); quadraticTo(c.x - s * 0.36f, c.y - s * 0.3f, c.x + s * 0.4f, c.y - s * 0.4f); quadraticTo(c.x + s * 0.3f, c.y + s * 0.34f, c.x - s * 0.36f, c.y + s * 0.36f); close() }, HOJA)
                drawLine(PAPEL, Offset(c.x - s * 0.3f, c.y + s * 0.3f), Offset(c.x + s * 0.3f, c.y - s * 0.3f), s * 0.05f)
            }
            "piensa" -> {
                drawCircle(LATON, s * 0.26f, Offset(c.x - s * 0.08f, c.y - s * 0.08f), style = Stroke(s * 0.09f))
                drawLine(CUERO_OSC, Offset(c.x + s * 0.12f, c.y + s * 0.12f), Offset(c.x + s * 0.42f, c.y + s * 0.42f), s * 0.12f, StrokeCap.Round)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Una flor prensada. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        for (k in 0 until 5) {
            val a = Math.toRadians(-90.0 + k * 72.0)
            drawCircle(FLOR, s * 0.18f, Offset(c.x + s * 0.24f * cos(a).toFloat(), c.y + s * 0.24f * sin(a).toFloat()))
        }
        drawCircle(Color(0xFFE0B84A), s * 0.12f, c)
    }

    override val desorden = 1.5f
}
