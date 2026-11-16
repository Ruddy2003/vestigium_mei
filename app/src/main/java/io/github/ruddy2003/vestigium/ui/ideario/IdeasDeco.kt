package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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
//  Art déco
// =====================================================================

/**
 * Art déco: el casillero de llaves de la recepción de un hotel de Manhattan, hacia 1926. Es un interior sin ventana: una pared
 * de paneles de madera de raíz con filetes de latón y una cornisa de laca, y la luz de día que entra de arriba a la izquierda,
 * fuera del cuadro. El mueble es un casillero de laca negra colgado detrás del mostrador: la laca refleja (brillos largos y
 * suaves), cada hueco lleva su filete de oro, las pilastras de los costados van estriadas, bajo la cornisa corre un friso en
 * zigzag y el remate es un zigurat de tres escalones con un abanico de rayos en marquetería de dos oros. Encima, el punto
 * focal, a la izquierda: la lámpara de bronce con su pantalla de vidrio esmerilado en tres pisos; a su pie, una llave dejada
 * en la repisa, con la chapa granate colgando por delante del canto; a la derecha, delante del remate, el reloj de sobremesa
 * escalonado, de alabastro y ónix verde con la esfera negra, y el jarrón de vidrio verde tallado con dos calas y una hoja
 * que corta el borde. En la pared, una lámina enmarcada (la fuente de los años veinte, en
 * oro sobre verde) que cuelga de dos cordones. Abajo, el mostrador: la cubierta de mármol verde con su veta y su brillo, el
 * canto de latón, el frente de laca con chevrones dorados y la placa de «y N más», el zócalo escalonado y el borde de una
 * alfombra granate; sobre el mármol, el teléfono de candelero con el auricular colgado, el libro de registro abierto con la
 * estilográfica y el secante de balancín, el timbre y un fedora gris olvidado; al pie, cortada por el borde derecho, una
 * maleta de cuero con sus etiquetas de viaje. Cada tema es un casillero con fondo de terciopelo verde y, según la semilla,
 * guarda la llave colgada de su chapa ovalada y los telegramas que van llegando, una sombrerera o un paquete atado con sus
 * cartas, o el periódico doblado y un sobre de correo aéreo (más papeles cuanto más guarda, y de otro color cada vez); el
 * tema va en la placa de latón grabada del labio. Lo que no tiene tema es un fajo de correo sin clasificar atado con cordel,
 * y su placa es una tarjeta de marfil; un hueco libre tiene el gancho sin llave, la placa esmaltada del número y una tarjeta
 * olvidada. Al abrir uno, lo guardado sube y la llave se mece. De noche todo se hunde en negro azulado: alumbra solo la
 * lámpara (el halo en la raíz y en el remate, el reflejo dorado en la laca, el oro de los filetes encendido cerca y apagado
 * lejos), las dos primeras filas se leen y las últimas casi se pierden. Por dentro, laca negra con un sol de rayos de oro.
 * Las fichas son telegramas con esquinas escalonadas.
 */
object IdeasDeco : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del hotel
    /** La laca negra (un negro cálido) y su plano con luz. */
    private val LACA = Color(0xFF161310)
    private val LACA_LUZ = Color(0xFF2E2820)
    /** El oro de los filetes y del latón, su luz y su sombra; el bronce de la lámpara. */
    private val ORO = Color(0xFFC9A24C)
    private val ORO_LUZ = Color(0xFFE8CF8A)
    private val ORO_OSC = Color(0xFF8A6A12)
    private val BRONCE = Color(0xFF7C5A2C)
    /** La madera de raíz de la pared, su luz y su sombra; el cuero de la maleta sale de ella. */
    private val RAIZ = Color(0xFFA06E3C)
    private val RAIZ_LUZ = Color(0xFFC79657)
    private val RAIZ_OSC = Color(0xFF6B4222)
    /** El verde: el mármol del mostrador y su veta, el terciopelo de los casilleros y el vidrio del jarrón. */
    private val VERDE = Color(0xFF1F6B57)
    private val MARMOL = Color(0xFF2E5E50)
    private val MARMOL_VETA = Color(0xFF9CC8B8)
    private val TERCIOPELO = Color(0xFF25503F)
    /** El marfil (vidrio esmerilado, calas, papeles) y los papeles de color: telegrama, sobre azul, papel de estraza, periódico. */
    private val MARFIL = Color(0xFFEFE6CF)
    private val CREMA = Color(0xFFF6F0E1)
    private val TELEGRAMA = Color(0xFFF2E6B8)
    private val AMARILLO = Color(0xFFE9CE74)
    private val AZUL = Color(0xFF55779C)
    private val ESTRAZA = Color(0xFFB58E5A)
    private val DIARIO = Color(0xFFD6D0BF)
    /** El granate de las chapas, las cintas y la alfombra; el fieltro gris del sombrero; la tinta. */
    private val GRANATE = Color(0xFF7A1E2A)
    private val ROJO = Color(0xFFA8322A)
    private val FIELTRO = Color(0xFFA29C92)
    private val TINTA = Color(0xFF16130E)
    /** La luz de la lámpara, su pantalla encendida y la noche del vestíbulo: negro azulado hondo. */
    private val LLAMA = Color(0xFFFFC56E)
    private val PANTALLA = Color(0xFFFFE6AE)
    private val NOCHE = Color(0xFF0A0D16)
    /**
     * La placa de latón de noche, lejos de la lámpara: con [TINTA] da unos 6:1 en el título; cerca de la lámpara sube hacia su
     * color con un poco de llama. [TARJETA_LEJOS], lo mismo para la tarjeta de marfil de «sin tema».
     */
    private val PLACA_LEJOS = Color(0xFFAC965F)
    private val TARJETA_LEJOS = Color(0xFFA7A08B)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.25f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.64f
    override val base = 0.46f
    override val lado = 0.075f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo de la repisa, su canto de frente y lo bajo de la cornisa, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.86f
    private const val CANTO = 0.935f
    /**
     * En la base: donde acaba el casillero, el fondo y el frente de la cubierta de mármol, lo bajo de su canto de latón, el
     * zócalo y el suelo, en fracción de su alto.
     */
    private const val RIEL = 0.075f
    private const val MESA = 0.4f
    private const val BORDE = 0.6f
    private const val LATON = 0.632f
    private const val ZOCALO = 0.845f
    private const val SUELO = 0.89f
    /** Los costados del casillero y el ancho de sus pilastras, en fracción del ancho. */
    private const val IZQ = 0.022f
    private const val DER = 0.978f
    private const val PILASTRA = 0.04f
    /** El filete de oro de cada hueco, en fracción del ancho. */
    private const val MARCO = 0.006f
    /** Lo alto del labio de cada casillero (donde va la placa) y cuánto sube lo guardado al abrirlo, en fracción de su alto. */
    private const val LABIO = 0.66f
    private const val SALIDA = 0.08f

    // ---------------------------------------------------------------- la luz y la noche

    /** La pantalla de la lámpara, en fracciones del ancho. */
    private val LUZ = Offset(0.2f, 0.27f)

    /** Cuánto le llega de la lámpara a un punto [p] (en fracciones del ancho): cae con la distancia; abajo a la derecha, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.75f else 1f)
        val radial = 1.38f * kotlin.math.exp(-3.0f * kotlin.math.sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.04f, 1f)
    }

    /**
     * La luz que la pantalla echa hacia abajo sobre el frente del casillero: cae despacio de fila en fila (la primera,
     * encendida; la segunda, a medias; la tercera, tenue; la cuarta y la quinta, casi perdidas), un poco menos en la columna
     * de la derecha, más lejos de la lámpara. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.9f, 0.84f to 0.8f, 1.19f to 0.52f, 1.54f to 0.27f, 1.89f to 0.1f, 2.24f to 0.055f, 2.9f to 0.04f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.68f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire azulado de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega de la lámpara), con un poco del color de su luz.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.12f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del vestíbulo. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el reflejo dorado de la lámpara. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la lámpara, tanto más
     * marcada cuanto más cerca está de ella. No baja de [hasta] (la repisa o el mármol que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.012f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la repisa o el mármol, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro a la izquierda y la sombra a la derecha. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.2f * fuerza), Offset(r.left, r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.34f * fuerza), Offset(r.left + r.width * 0.15f, r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(r.left + r.width * 0.58f, r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo de caras planas dentro de [r] (ir dentro de su recorte): el canto de luz a la izquierda y arriba, la sombra a la derecha. */
    private fun DrawScope.caras(r: Rect, t: Tono, fuerza: Float = 1f) {
        val u = density
        drawRect(t.b(0.16f * fuerza), r.topLeft, Size(r.width * 0.22f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(r.left + r.width * 0.72f, r.top), Size(r.width * 0.28f, r.height))
        drawRect(t.b(0.5f * fuerza), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.b(0.3f * fuerza), r.topLeft, Size(1f * u, r.height))
    }

    /**
     * Un plano de laca negra en [r]: el negro cálido y sus reflejos largos y suaves (dos franjas a lo alto, o una a lo ancho si
     * está [tumbada]); a la derecha o abajo se cierra en sombra.
     */
    private fun DrawScope.laca(r: Rect, t: Tono, semilla: Int, tumbada: Boolean = false, color: Color = LACA) {
        drawRect(t.c(color), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (!tumbada) {
                val x1 = r.left + r.width * (0.1f + 0.2f * azar(semilla, 1))
                val x2 = r.left + r.width * (0.52f + 0.22f * azar(semilla, 2))
                drawRect(t.b(0.07f), Offset(x1, r.top), Size(r.width * 0.17f, r.height))
                drawRect(t.b(0.11f), Offset(x1 + r.width * 0.035f, r.top), Size(r.width * 0.04f, r.height))
                drawRect(t.b(0.05f), Offset(x2, r.top), Size(r.width * 0.07f, r.height))
                drawRect(t.s(0.3f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
            } else {
                drawRect(t.b(0.07f), Offset(r.left, r.top + r.height * 0.1f), Size(r.width, r.height * 0.34f))
                drawRect(t.b(0.1f), Offset(r.left, r.top + r.height * 0.2f), Size(r.width, r.height * 0.07f))
                drawRect(t.s(0.3f), Offset(r.left, r.bottom - r.height * 0.22f), Size(r.width, r.height * 0.22f))
            }
        }
    }

    /**
     * Un filete de oro a lo ancho, con su luz arriba; va por tramos para que de noche se encienda cerca de la lámpara y se
     * apague lejos.
     */
    private fun DrawScope.fileteH(x0: Float, x1: Float, y: Float, g: Float, tramos: Int = 6, color: Color = ORO) {
        val paso = (x1 - x0) / tramos
        for (k in 0 until tramos) {
            val t = tn(Offset(x0 + paso * (k + 0.5f), y))
            drawRect(t.c(color), Offset(x0 + paso * k, y), Size(paso + 0.5f, g))
            drawRect(t.b(0.55f), Offset(x0 + paso * k, y), Size(paso + 0.5f, g * 0.35f))
        }
    }

    /** Lo mismo, a lo alto: la luz a la izquierda. */
    private fun DrawScope.fileteV(x: Float, y0: Float, y1: Float, g: Float, tramos: Int = 6, color: Color = ORO) {
        val paso = (y1 - y0) / tramos
        for (k in 0 until tramos) {
            val t = tn(Offset(x, y0 + paso * (k + 0.5f)))
            drawRect(t.c(color), Offset(x, y0 + paso * k), Size(g, paso + 0.5f))
            drawRect(t.b(0.5f), Offset(x, y0 + paso * k), Size(g * 0.35f, paso + 0.5f))
        }
    }

    /**
     * El filo cálido de la lámpara en algo, de noche: en su canto de arriba y en el costado izquierdo, que la miran, tanto más
     * vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, t: Tono) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.8f * t.k).coerceAtMost(0.7f)), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(LLAMA.copy(alpha = (0.28f * t.k).coerceAtMost(0.28f)), Offset(r.left, r.top + 1.4f * u), Size(r.width, 3f * u))
        drawRect(LLAMA.copy(alpha = (0.5f * t.k).coerceAtMost(0.45f)), r.topLeft, Size(1.2f * u, r.height))
    }

    /** El destello de la lámpara en el latón o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un abanico de rayos: medio sol con sus rayos alternados. Lo usan las marcas de las fichas. */
    private fun DrawScope.abanico(c: Offset, r: Float, a: Color, b: Color) {
        val n = 14
        for (k in 0 until n) drawArc(if (k % 2 == 0) a else b, 180f + k * 180f / n, 180f / n, true, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r))
        drawArc(LACA, 180f, 180f, true, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f))
        drawArc(ORO, 180f, 180f, false, Offset(c.x - r * 0.28f, c.y - r * 0.28f), Size(r * 0.56f, r * 0.56f), style = Stroke(1.2f * density))
    }

    /** Una llave de hotel a trazo, colgando de [c]. La usa la marca de las fichas fijadas. */
    private fun DrawScope.llaveChica(c: Offset, l: Float, color: Color) {
        val u = density
        drawCircle(color, l * 0.16f, Offset(c.x, c.y + l * 0.16f), style = Stroke(1.6f * u))
        drawLine(color, Offset(c.x, c.y + l * 0.32f), Offset(c.x, c.y + l), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.86f), Offset(c.x + l * 0.16f, c.y + l * 0.86f), 1.8f * u)
        drawLine(color, Offset(c.x, c.y + l * 0.96f), Offset(c.x + l * 0.12f, c.y + l * 0.96f), 1.8f * u)
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
        val pieF = sup + (frente - sup) * 0.8f
        val mesa = b.top + hb * MESA
        val borde = b.top + hb * BORDE

        // ---- la pared de raíz, y lo que cuelga de ella
        pared(w, canto, b.top, mesa)
        cuadro(Rect(w * 0.555f, w * 0.068f, w * 0.72f, w * 0.272f))

        // ---- el casillero: el remate, el cuerpo y la repisa de arriba
        remate(w, sup)
        casillero(d, canto, b)
        cornisa(w, sup, frente, canto)

        // ---- lo de encima, de atrás adelante
        jarron(Offset(w * 0.912f, pieA), sup)
        reloj(Offset(w * 0.752f, pieA), sup)
        lampara(Offset(w * LUZ.x, pieF), sup)
        llaveDejada(Offset(w * 0.372f, frente - (frente - sup) * 0.2f))

        // ---- el mostrador y lo que hay sobre el mármol
        mostrador(d, w, alto)
        val fondoM = mesa + (borde - mesa) * 0.3f
        val medioM = mesa + (borde - mesa) * 0.62f
        telefono(Offset(w * 0.118f, medioM), mesa)
        libro(w, mesa, borde)
        secante(Offset(w * 0.606f, medioM))
        timbre(Offset(w * 0.685f, mesa + (borde - mesa) * 0.78f))
        sombrero(Offset(w * 0.795f, fondoM + (borde - mesa) * 0.06f), mesa)

        // ---- al pie: la maleta, cortada por el borde derecho
        maleta(Rect(w * 0.83f, b.top + hb * 0.57f, w * 1.06f, b.top + hb * 0.972f))

        // ---- la luz de la lámpara, por encima de lo que tiene cerca
        if (o) {
            val foco = LUZ * w
            mancha(foco + Offset(0f, w * 0.04f), w * 0.46f, LLAMA.copy(alpha = 0.1f))
            mancha(foco + Offset(0f, w * 0.02f), w * 0.2f, LLAMA.copy(alpha = 0.2f))
            // El reflejo dorado de la lámpara, que se alarga en la laca del canto y del friso.
            clipRect(0f, frente, w, d.huecos.first().top - w * MARCO) {
                mancha(Offset(foco.x, frente + w * 0.02f), w * 0.13f, LLAMA.copy(alpha = 0.2f))
            }
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La madera de raíz en [r]: [n] nudos repartidos con la [semilla], cada uno un racimo de ojos chicos con las aguas de la
     * veta que lo rodean, sobre nubes más oscuras y más claras.
     */
    private fun DrawScope.raiz(r: Rect, semilla: Int, n: Int) {
        val u = density
        val w = anchoW
        pinceladas(r, if (esNoche) NOCHE else RAIZ_OSC, semilla, n * 2, u, 0.07f)
        pinceladas(r, if (esNoche) NOCHE else RAIZ_LUZ, semilla + 1, n, u, 0.1f)
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until n) {
                val c = Offset(r.left + r.width * azar(semilla + 2, k), r.top + r.height * azar(semilla + 3, k))
                val t = tn(c)
                val veta = t.c(RAIZ_OSC)
                val rr = w * (0.018f + 0.03f * azar(semilla + 4, k))
                // Las aguas: arcos abiertos alrededor del nudo, cada vez más anchos y más tenues.
                for (j in 0 until 4) {
                    val f = 1f + 0.5f * j
                    drawArc(veta.copy(alpha = 0.22f - 0.04f * j), 360f * azar(semilla + 6, k + 7 * j), 150f + 130f * azar(semilla + 7, k + 7 * j), false,
                        Offset(c.x - rr * f * 1.45f, c.y - rr * f * 0.8f), Size(rr * f * 2.9f, rr * f * 1.6f), style = Stroke((0.7f + 0.3f * j) * u))
                }
                // Los ojos: puntos oscuros con su cerco, apretados hacia el centro.
                val ojos = 4 + (5 * azar(semilla + 8, k)).toInt()
                for (j in 0 until ojos) {
                    val a = 6.28f * azar(semilla + 9 + j, k)
                    val d = rr * (0.1f + 0.9f * azar(semilla + 20 + j, k))
                    val p = Offset(c.x + d * 1.3f * cos(a), c.y + d * 0.75f * sin(a))
                    val ro = u * (0.9f + 1.7f * azar(semilla + 40 + j, k))
                    drawCircle(veta.copy(alpha = 0.4f), ro, p)
                    drawCircle(veta.copy(alpha = 0.2f), ro * 2.1f, p, style = Stroke(0.6f * u))
                    drawCircle(t.b(0.2f), ro * 0.45f, p + Offset(ro * 1.3f, ro * 1.3f))
                }
            }
        }
    }

    /**
     * La pared: paneles de madera de raíz con sus juntas fileteadas de latón, la mancha de luz arriba a la izquierda (de
     * noche, el halo de la lámpara), la cornisa de laca con su doble filete y su sombra, y el desgaste de abajo, detrás del
     * mostrador. Solo lleva veta donde se ve: arriba y entre el casillero y el mármol.
     */
    private fun DrawScope.pared(w: Float, canto: Float, baseTop: Float, mesa: Float) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(RAIZ, RAIZ_LUZ, 0.75f), RAIZ, mezcla(RAIZ, RAIZ_OSC, 0.45f)), 0f, w), Offset.Zero, Size(w, mesa))
            mancha(Offset(w * 0.04f, w * 0.03f), w * 0.72f, luzMueble(0.38f))
        } else {
            fun k(d: Float) = Tono(true, luz(LUZ + Offset(d, 0f))).c(RAIZ)
            drawRect(Brush.radialGradient(0f to k(0.05f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = LUZ * w, radius = w * 1.2f), Offset.Zero, Size(w, mesa))
        }
        val arriba = Rect(0f, 0f, w, canto)
        val abajo = Rect(0f, baseTop, w, mesa)
        raiz(arriba, 5001, 15)
        raiz(abajo, 5011, 6)
        // Las juntas de los paneles: la ranura y su filete de latón.
        listOf(0.165f, 0.5f, 0.835f).forEach { fx ->
            listOf(arriba, abajo).forEach { r ->
                val x = w * fx
                drawRect(tn(Offset(x, r.center.y)).s(0.4f), Offset(x - 1.6f * u, r.top), Size(1.6f * u, r.height))
                fileteV(x, r.top, r.bottom, 1.5f * u, 4)
            }
        }
        // La cornisa de laca, su doble filete de oro y la sombra que deja.
        val cor = w * 0.03f
        drawRect(Brush.horizontalGradient(List(9) { i -> tn(Offset(w * i / 8f, cor)).c(mezcla(LACA, LACA_LUZ, 0.35f)) }, 0f, w), Offset.Zero, Size(w, cor))
        drawRect(Brush.horizontalGradient(List(9) { i -> tn(Offset(w * i / 8f, cor)).b(0.09f) }, 0f, w), Offset(0f, cor * 0.3f), Size(w, cor * 0.28f))
        fileteH(0f, w, cor, 2f * u)
        fileteH(0f, w, cor + w * 0.009f, 1f * u)
        val tc = tn(Offset(w * 0.5f, cor))
        drawRect(tc.s(0.3f), Offset(0f, cor + 2f * u), Size(w, w * 0.007f - 2f * u))
        drawRect(tc.s(0.16f), Offset(0f, cor + w * 0.009f + 1f * u), Size(w, w * 0.014f))
        // El desgaste de abajo: la pared más sucia hacia el mostrador y unas rozaduras.
        val ta = tn(Offset(w * 0.5f, mesa))
        drawRect(ta.s(0.12f), Offset(0f, mesa - (mesa - baseTop) * 0.4f), Size(w, (mesa - baseTop) * 0.4f))
        drawRect(ta.s(0.12f), Offset(0f, mesa - (mesa - baseTop) * 0.16f), Size(w, (mesa - baseTop) * 0.16f))
        for (k in 0 until 5) {
            val x = w * (0.2f + 0.7f * azar(5030, k))
            val y = mesa - w * (0.012f + 0.03f * azar(5031, k))
            drawLine(ta.b(0.22f), Offset(x, y), Offset(x + w * (0.02f + 0.02f * azar(5032, k)), y - w * 0.004f), 0.7f * u)
        }
    }

    /**
     * La lámina de la pared, un poco torcida: el marco de laca con su filete de oro, el paspartú de marfil y la estampa (la
     * fuente de los años veinte, con sus chorros en arco y su pila escalonada, en oro sobre verde); cuelga de dos cordones
     * que suben al riel de la cornisa.
     */
    private fun DrawScope.cuadro(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        val cordon = t.c(mezcla(ORO_OSC, LACA, 0.3f))
        drawLine(cordon, Offset(r.left + r.width * 0.16f, r.top + 2f * u), Offset(r.left + r.width * 0.3f, w * 0.04f), 0.8f * u)
        drawLine(cordon, Offset(r.right - r.width * 0.16f, r.top + 2f * u), Offset(r.right - r.width * 0.3f, w * 0.04f), 0.8f * u)
        rotate(-1.3f, r.center) {
            sombraEnPared(Path().apply { addRect(r) }, r.center, size.height, 0.26f)
            laca(r, t, 5101)
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(t.b(0.3f), r.topLeft, Size(1.2f * u, r.height))
            val m = r.width * 0.075f
            val pas = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
            drawRect(t.c(ORO), Offset(pas.left - 1.4f * u, pas.top - 1.4f * u), Size(pas.width + 2.8f * u, pas.height + 2.8f * u))
            drawRect(t.c(MARFIL), pas.topLeft, pas.size)
            drawRect(t.s(0.26f), pas.topLeft, Size(pas.width, 1.8f * u))
            drawRect(t.s(0.18f), pas.topLeft, Size(1.5f * u, pas.height))
            val p = Rect(pas.left + pas.width * 0.13f, pas.top + pas.height * 0.09f, pas.right - pas.width * 0.13f, pas.bottom - pas.height * 0.09f)
            drawRect(t.c(mezcla(MARMOL, LACA, 0.35f)), p.topLeft, p.size)
            drawRect(t.c(ORO).copy(alpha = 0.8f), Offset(p.left + 2f * u, p.top + 2f * u), Size(p.width - 4f * u, p.height - 4f * u), style = Stroke(0.7f * u))
            // La fuente: la pila escalonada, el surtidor y tres chorros en arco a cada lado.
            val oro = t.c(ORO_LUZ)
            val cx = p.center.x
            val pie = p.bottom - p.height * 0.12f
            listOf(0.3f to 0f, 0.2f to 0.045f, 0.1f to 0.09f).forEach { (a, dy) ->
                drawRect(t.c(ORO), Offset(cx - p.width * a, pie - p.height * (dy + 0.045f)), Size(p.width * a * 2, p.height * 0.04f))
            }
            val boca = Offset(cx, pie - p.height * 0.62f)
            drawLine(oro, Offset(cx, pie - p.height * 0.13f), boca, 1.3f * u)
            listOf(0.36f to 0.56f, 0.26f to 0.44f, 0.15f to 0.3f).forEach { (ancho, cae) ->
                listOf(-1f, 1f).forEach { s ->
                    val fin = Offset(cx + s * p.width * ancho, boca.y + p.height * cae)
                    drawPath(Path().apply {
                        moveTo(boca.x, boca.y + p.height * 0.02f)
                        cubicTo(boca.x + s * p.width * ancho * 0.5f, boca.y - p.height * 0.12f, fin.x, boca.y, fin.x, fin.y)
                    }, oro, style = Stroke(1.1f * u, cap = StrokeCap.Round))
                    drawCircle(oro, 1.2f * u, fin)
                }
            }
            drawCircle(t.c(MARFIL), 1.8f * u, boca - Offset(0f, p.height * 0.03f))
            // El vidrio: un reflejo en diagonal.
            clipRect(pas.left, pas.top, pas.right, pas.bottom) {
                drawPath(poligono(pas.left + pas.width * 0.08f, pas.bottom, pas.left + pas.width * 0.5f, pas.top, pas.left + pas.width * 0.66f, pas.top, pas.left + pas.width * 0.24f, pas.bottom), t.b(0.12f))
            }
            drawRect(t.s(0.3f), Offset(r.right - 1.5f * u, r.top), Size(1.5f * u, r.height))
            drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
        }
    }

    // ---------------------------------------------------------------- el casillero

    /** Los tres escalones del remate, de abajo arriba: de dónde a dónde (fracciones del ancho) y cuánto suben sobre la repisa. */
    private val ESCALONES = listOf(Triple(0.07f, 0.93f, 0.07f), Triple(0.2f, 0.8f, 0.132f), Triple(0.325f, 0.675f, 0.195f))

    /**
     * El remate: un zigurat de laca de tres escalones, de pie al fondo de la repisa, con el canto de arriba de cada escalón, su
     * filete de oro, su lado en sombra y su sombra en la pared; en el frente, el abanico de rayos en marquetería de dos oros
     * que sale de un medio sol.
     */
    private fun DrawScope.remate(w: Float, sup: Float) {
        val u = density
        val rects = ESCALONES.map { (a, z, h) -> Rect(w * a, sup - w * h, w * z, sup) }
        val silueta = Path().apply { rects.forEach { addRect(it) } }
        sombraEnPared(silueta, Offset(w * 0.5f, sup - w * 0.1f), sup, 0.26f)
        rects.forEachIndexed { i, r ->
            // Por tramos, para que de noche el lado de la lámpara quede más encendido.
            val n = 6 - i
            for (k in 0 until n) {
                val tr = Rect(r.left + r.width * k / n, r.top, r.left + r.width * (k + 1) / n + 0.5f, r.bottom)
                drawRect(tn(tr.center).c(mezcla(LACA, LACA_LUZ, 0.25f)), tr.topLeft, tr.size)
            }
        }
        // Los reflejos de la laca en cada escalón que asoma.
        rects.forEachIndexed { i, r ->
            val t = tn(Offset(r.left + r.width * 0.2f, r.top + w * 0.03f))
            val visto = if (i < 2) rects[i + 1].left - r.left else r.width
            drawRect(t.b(0.08f), Offset(r.left + visto * 0.2f, r.top), Size(visto * 0.3f, r.height))
            drawRect(t.b(0.12f), Offset(r.left + visto * 0.3f, r.top), Size(visto * 0.08f, r.height))
            drawRect(tn(Offset(r.right, r.center.y)).s(0.32f), Offset(r.right - w * 0.014f, r.top), Size(w * 0.014f, r.height))
        }
        // El abanico: los rayos, alternando los dos oros, cada uno con su media cara en sombra.
        val c = Offset(w * 0.5f, sup)
        val radio = w * 0.178f
        clipPath(silueta) {
            val n = 13
            for (k in 0 until n) {
                val a = Math.toRadians(180.0 + 180.0 * (k + 0.5) / n)
                val largo = radio * (if (k % 2 == 0) 1f else 0.86f)
                val ancho = Math.toRadians(if (k % 2 == 0) 3.6 else 2.6)
                fun p(r: Float, da: Double) = Offset(c.x + r * cos(a + da).toFloat(), c.y + r * sin(a + da).toFloat())
                val t = tn(p(radio * 0.6f, 0.0))
                val oro = if (k % 2 == 0) ORO else mezcla(ORO_OSC, ORO, 0.35f)
                val luzDia = if (esNoche) 0f else 0.3f * (1f - k / (n - 1f))
                val i0 = p(w * 0.058f, 0.0); val e0 = p(largo, -ancho); val e1 = p(largo, ancho); val em = p(largo, 0.0)
                drawPath(poligono(i0.x, i0.y, e0.x, e0.y, em.x, em.y), t.c(mezcla(oro, ORO_LUZ, 0.25f + luzDia)))
                drawPath(poligono(i0.x, i0.y, em.x, em.y, e1.x, e1.y), t.c(mezcla(oro, ORO_OSC, 0.3f - luzDia * 0.5f)))
            }
            // El medio sol, con sus aros.
            val ts = tn(c - Offset(0f, w * 0.03f))
            val rs = w * 0.05f
            drawArc(ts.c(mezcla(ORO, ORO_LUZ, 0.5f)), 180f, 180f, true, Offset(c.x - rs, c.y - rs), Size(rs * 2, rs * 2))
            drawArc(ts.b(0.4f), 180f, 80f, true, Offset(c.x - rs, c.y - rs), Size(rs * 2, rs * 2))
            listOf(0.68f, 0.36f).forEach { f -> drawArc(ts.c(ORO_OSC), 180f, 180f, false, Offset(c.x - rs * f, c.y - rs * f), Size(rs * 2 * f, rs * 2 * f), style = Stroke(1.2f * u)) }
        }
        // El canto de arriba de cada escalón (se ve un poco desde arriba) y su filete de oro.
        rects.forEachIndexed { i, r ->
            val tramos = if (i < 2) listOf(r.left to rects[i + 1].left, rects[i + 1].right to r.right) else listOf(r.left to r.right)
            tramos.forEach { (x0, x1) ->
                val t = tn(Offset((x0 + x1) / 2, r.top))
                drawRect(t.c(mezcla(LACA_LUZ, ORO_OSC, 0.18f)), Offset(x0, r.top - w * 0.007f), Size(x1 - x0, w * 0.007f))
                drawRect(t.b(0.3f), Offset(x0, r.top - w * 0.007f), Size(x1 - x0, 1f * u))
                fileteH(x0, x1, r.top, 1.8f * u, 2)
                drawRect(t.s(0.3f), Offset(x0, r.top + 1.8f * u), Size(x1 - x0, 2f * u))
            }
            drawRect(tn(Offset(r.left, r.center.y)).b(0.32f), Offset(r.left, r.top - w * 0.007f), Size(1.2f * u, (if (i == 0) r.height else rects[i - 1].top - r.top) + w * 0.007f))
        }
    }

    /**
     * El cuerpo del casillero: su sombra en la pared (a la derecha y debajo), la laca con sus reflejos largos (de noche más
     * encendida arriba a la izquierda, cerca de la lámpara), el filete de oro de cada hueco, las pilastras estriadas con su
     * capitel y su basa escalonados, el friso en zigzag bajo la cornisa, un rombo de oro en cada cruce, el riel de abajo y las
     * dos ménsulas escalonadas que apoyan en la pared.
     */
    private fun DrawScope.casillero(d: Disposicion, canto: Float, b: Rect) {
        val u = density
        val o = esNoche
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val fondo = b.top + hb * RIEL
        val mesa = b.top + hb * MESA
        val cuerpo = Rect(xL, canto, xR, fondo)

        // Su sombra en la pared: a la derecha y debajo (la corta el mármol).
        val t0 = tn(Offset(xR, canto + cuerpo.height * 0.3f))
        drawPath(poligono(xR, canto - w * 0.03f, xR + w * 0.024f, canto, xR + w * 0.024f, fondo + w * 0.03f, xR, fondo), t0.s(0.3f))
        val tb = tn(Offset(w * 0.5f, fondo))
        clipRect(0f, fondo, size.width, mesa) {
            drawRect(tb.s(0.34f), Offset(xL + w * 0.016f, fondo), Size(xR - xL + w * 0.008f, w * 0.03f))
            drawRect(tb.s(0.16f), Offset(xL + w * 0.024f, fondo + w * 0.03f), Size(xR - xL, w * 0.022f))
        }

        // La laca, por tramos de arriba abajo.
        val tramos = 12
        for (k in 0 until tramos) {
            val y0 = cuerpo.top + cuerpo.height * k / tramos
            val t = tn(Offset(w * 0.4f, y0 + cuerpo.height / tramos / 2))
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.3f)), Offset(xL, y0), Size(xR - xL, cuerpo.height / tramos + 0.5f))
        }
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.1f), Color.Transparent, sombraMueble(0.3f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(Color.Transparent, NOCHE.copy(alpha = 0.4f)), w * 0.45f, xR), cuerpo.topLeft, cuerpo.size)

        // Los reflejos largos: en el montante del medio y en los travesaños entre filas.
        val pw = w * PILASTRA
        val medio = Rect(d.huecos[0].right + w * MARCO, cuerpo.top, d.huecos[1].left - w * MARCO, cuerpo.bottom)
        for (k in 0 until tramos) {
            val y0 = cuerpo.top + cuerpo.height * k / tramos
            val t = tn(Offset(medio.center.x, y0 + cuerpo.height / tramos / 2))
            drawRect(t.b(0.09f), Offset(medio.left + medio.width * 0.18f, y0), Size(medio.width * 0.4f, cuerpo.height / tramos + 0.5f))
            drawRect(t.b(0.16f), Offset(medio.left + medio.width * 0.3f, y0), Size(medio.width * 0.1f, cuerpo.height / tramos + 0.5f))
        }
        d.filas.forEachIndexed { i, f ->
            val y0 = f.bottom + w * MARCO
            val y1 = if (i < d.filas.size - 1) d.filas[i + 1].top - w * MARCO else fondo
            for (k in 0 until 4) {
                val x0 = xL + pw + (xR - xL - 2 * pw) * k / 4f
                val t = tn(Offset(x0 + (xR - xL) / 8f, (y0 + y1) / 2))
                drawRect(t.b(0.08f), Offset(x0, y0 + (y1 - y0) * 0.12f), Size((xR - xL - 2 * pw) / 4f + 0.5f, (y1 - y0) * 0.3f))
                drawRect(t.s(0.25f), Offset(x0, y1 - (y1 - y0) * 0.22f), Size((xR - xL - 2 * pw) / 4f + 0.5f, (y1 - y0) * 0.22f))
            }
        }

        // El filete de oro de cada hueco, con su luz arriba y a la izquierda y su sombra.
        val m = w * MARCO
        d.huecos.forEach { r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.4f), Offset(marco.left + 1.6f * u, marco.top + 2f * u), marco.size)
            fileteH(marco.left, marco.right, marco.top, m, 3)
            fileteH(marco.left, marco.right, r.bottom, m, 3, mezcla(ORO, ORO_OSC, 0.35f))
            fileteV(marco.left, marco.top, marco.bottom, m, 3)
            fileteV(r.right, marco.top, marco.bottom, m, 3, mezcla(ORO, ORO_OSC, 0.45f))
            drawRect(t.s(0.3f), Offset(marco.left, marco.bottom - 1f * u), Size(marco.width, 1f * u))
        }
        // Un rombo de oro en cada cruce del montante con los travesaños.
        d.filas.forEachIndexed { i, f ->
            if (i < d.filas.size - 1) {
                val c = Offset(medio.center.x, f.bottom + d.separacion / 2)
                val t = tn(c)
                val rr = d.separacion * 0.24f
                drawPath(poligono(c.x, c.y - rr, c.x + rr, c.y, c.x, c.y + rr, c.x - rr, c.y), t.c(ORO))
                drawPath(poligono(c.x, c.y - rr, c.x, c.y + rr, c.x - rr, c.y), t.b(0.4f))
            }
        }

        // Las pilastras estriadas, con su capitel y su basa escalonados.
        val arriba = d.huecos.first().top - m
        listOf(xL, xR - pw).forEachIndexed { lado, x ->
            val n = 14
            for (k in 0 until n) {
                val y0 = arriba + (fondo - arriba) * k / n
                val alto = (fondo - arriba) / n + 0.5f
                val t = tn(Offset(x + pw / 2, y0 + alto / 2))
                drawRect(t.c(mezcla(LACA, LACA_LUZ, if (lado == 0) 0.75f else 0.35f)), Offset(x, y0), Size(pw, alto))
                for (f in 0 until 4) {
                    val fx = x + pw * (0.12f + 0.21f * f)
                    drawRect(t.s(0.5f), Offset(fx, y0), Size(pw * 0.09f, alto))
                    drawRect(t.c(ORO).copy(alpha = 0.9f), Offset(fx + pw * 0.09f, y0), Size(1.1f * u, alto))
                    drawRect(t.b(0.22f), Offset(fx + pw * 0.09f + 1.1f * u, y0), Size(pw * 0.06f, alto))
                }
                if (lado == 0) drawRect(t.b(0.4f), Offset(x, y0), Size(1.3f * u, alto))
                else drawRect(t.s(0.4f), Offset(x + pw - w * 0.006f, y0), Size(w * 0.006f, alto))
            }
            listOf(true, false).forEach { capitel ->
                for (e in 0 until 3) {
                    val alto = w * 0.007f
                    val y = if (capitel) arriba + alto * e else fondo - w * 0.012f - alto * (e + 1)
                    val mete = pw * 0.1f * e
                    val t = tn(Offset(x + pw / 2, y))
                    drawRect(t.c(if (e % 2 == 0) ORO else LACA), Offset(x + mete, y), Size(pw - 2 * mete, alto))
                    drawRect(t.b(0.5f), Offset(x + mete, y), Size(pw - 2 * mete, 0.8f * u))
                    drawRect(t.s(0.35f), Offset(x + mete + (pw - 2 * mete) * 0.7f, y), Size((pw - 2 * mete) * 0.3f, alto))
                }
            }
        }

        // El friso bajo la cornisa: un zigzag de oro entre dos filetes.
        val f0 = canto + w * 0.012f; val f1 = arriba - w * 0.012f
        fileteH(xL, xR, f0, 1.1f * u, 8)
        fileteH(xL, xR, f1, 1.1f * u, 8)
        val dientes = 24
        val paso = (xR - xL - w * 0.02f) / dientes
        for (k in 0 until dientes) {
            val x0 = xL + w * 0.01f + paso * k
            val t = tn(Offset(x0 + paso / 2, (f0 + f1) / 2))
            val ya = f0 + (f1 - f0) * 0.26f; val yb = f1 - (f1 - f0) * 0.22f
            drawLine(t.c(ORO), Offset(x0, yb), Offset(x0 + paso / 2, ya), 1.5f * u)
            drawLine(t.c(mezcla(ORO, ORO_OSC, 0.5f)), Offset(x0 + paso / 2, ya), Offset(x0 + paso, yb), 1.5f * u)
        }

        // El riel de abajo con su filete y las dos ménsulas escalonadas que apoyan en la pared.
        fileteH(xL, xR, fondo - w * 0.012f, 1.6f * u, 8)
        drawRect(tb.s(0.4f), Offset(xL, fondo - 1.2f * u), Size(xR - xL, 1.2f * u))
        listOf(0.2f to 0.32f, 0.62f to 0.74f).forEach { (a, z) ->
            for (e in 0 until 3) {
                val r = Rect(w * (a + 0.016f * e), fondo + w * 0.011f * e, w * (z - 0.016f * e), fondo + w * 0.011f * (e + 1))
                val t = tn(r.center)
                drawRect(t.s(0.3f), Offset(r.left + w * 0.012f, r.top + w * 0.008f), r.size)
                laca(r, t, 5200 + e, tumbada = true, color = mezcla(LACA, LACA_LUZ, 0.4f))
                drawRect(t.c(ORO), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
                drawRect(t.b(0.3f), r.topLeft, Size(1.2f * u, r.height))
            }
        }
        // Los cantos de los costados: el de la izquierda recibe la luz; el de la derecha, en sombra.
        for (k in 0 until tramos) {
            val y0 = cuerpo.top + cuerpo.height * k / tramos
            val t = tn(Offset(xL, y0))
            drawRect(t.b(0.35f), Offset(xL, y0), Size(1.3f * u, cuerpo.height / tramos + 0.5f))
        }
    }

    /**
     * La repisa de arriba del casillero: la tabla de laca vista un poco desde arriba (con su reflejo y la sombra del remate) y
     * su canto en dos escalones con el filete de oro, y la sombra que deja en el friso.
     */
    private fun DrawScope.cornisa(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val xL = w * IZQ - w * 0.016f; val xR = w * DER + w * 0.016f
        val n = 20
        val m1 = frente + (canto - frente) * 0.5f
        for (k in 0 until n) {
            val x0 = xL + (xR - xL) * k / n; val an = (xR - xL) / n + 0.5f
            val t = tn(Offset(x0 + an / 2, sup))
            drawRect(t.c(mezcla(LACA_LUZ, RAIZ_OSC, 0.22f)), Offset(x0, sup), Size(an, frente - sup))
            drawRect(t.b(0.1f), Offset(x0, sup + (frente - sup) * 0.45f), Size(an, (frente - sup) * 0.3f))
            // El canto: el escalón de arriba, más salido, y el de abajo.
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.55f)), Offset(x0, frente), Size(an, m1 - frente))
            drawRect(t.b(0.1f), Offset(x0, frente + (m1 - frente) * 0.25f), Size(an, (m1 - frente) * 0.3f))
        }
        val t = tn(Offset(w * 0.5f, sup))
        drawRect(t.s(0.4f), Offset(xL, sup), Size(xR - xL, 1.6f * u))
        drawRect(t.s(0.18f), Offset(w * 0.1f, sup + 1.6f * u), Size(w * 0.8f, (frente - sup) * 0.22f))
        fileteH(xL, xR, frente, 2f * u, 8)
        drawRect(t.s(0.4f), Offset(xL, m1 - 1f * u), Size(xR - xL, 1f * u))
        val yL = w * IZQ - w * 0.004f; val yR = w * DER + w * 0.004f
        for (k in 0 until n) {
            val x0 = yL + (yR - yL) * k / n; val an = (yR - yL) / n + 0.5f
            val tk = tn(Offset(x0 + an / 2, m1))
            drawRect(tk.c(mezcla(LACA, LACA_LUZ, 0.3f)), Offset(x0, m1), Size(an, canto - m1))
        }
        fileteH(yL, yR, canto - 1.6f * u, 1.6f * u, 8)
        drawRect(tn(Offset(xR, frente)).s(0.4f), Offset(xR - w * 0.008f, sup), Size(w * 0.008f, m1 - sup))
        drawRect(tn(Offset(xL, frente)).b(0.4f), Offset(xL, sup), Size(1.3f * u, m1 - sup))
        // La sombra de la cornisa en el friso.
        drawRect(t.s(0.4f), Offset(w * IZQ, canto), Size(w * (DER - IZQ), 2.6f * u))
        drawRect(t.s(0.18f), Offset(w * IZQ, canto + 2.6f * u), Size(w * (DER - IZQ), 3.4f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * La lámpara: el pie de bronce en tres escalones, el fuste estriado con su collar y la pantalla de vidrio esmerilado en
     * tres pisos, cada uno con su aro de bronce, y el remate. De día está apagada (el vidrio, marfil, con su lado en sombra);
     * de noche, encendida: el vidrio dorado, más blanco al centro, y la luz que baja por el fuste hasta la repisa.
     */
    private fun DrawScope.lampara(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW * 1.15f
        val o = esNoche
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.06f))
        val pies = listOf(Rect(x - w * 0.058f, y - w * 0.014f, x + w * 0.058f, y), Rect(x - w * 0.042f, y - w * 0.029f, x + w * 0.042f, y - w * 0.014f),
            Rect(x - w * 0.026f, y - w * 0.043f, x + w * 0.026f, y - w * 0.029f))
        val fuste = Rect(x - w * 0.011f, y - w * 0.16f, x + w * 0.011f, y - w * 0.043f)
        val collar = Rect(x - w * 0.026f, y - w * 0.171f, x + w * 0.026f, y - w * 0.16f)
        val yb = collar.top
        fun piso(abajo: Float, arriba: Float, y0: Float, y1: Float) = poligono(x - w * abajo, y1, x - w * arriba, y0, x + w * arriba, y0, x + w * abajo, y1)
        val pisos = listOf(Triple(0.106f, 0.095f, yb - w * 0.054f to yb), Triple(0.08f, 0.069f, yb - w * 0.1f to yb - w * 0.054f), Triple(0.054f, 0.042f, yb - w * 0.14f to yb - w * 0.1f))
        val formas = pisos.map { (a, z, ys) -> piso(a, z, ys.first, ys.second) }
        val remate = Rect(x - w * 0.018f, yb - w * 0.15f, x + w * 0.018f, yb - w * 0.14f)
        val todo = Path().apply { pies.forEach { addRect(it) }; addRect(fuste); formas.forEach { addPath(it) }; addRect(remate) }
        if (!o) sombraEnPared(todo, Offset(x, y - w * 0.2f), sup, 0.2f)
        contacto(Offset(x + w * 0.008f, y), w * 0.135f, 4f * u, 0.45f)
        // El reflejo del pie en la laca de la repisa.
        drawRect(t.c(BRONCE).copy(alpha = 0.3f), Offset(pies[0].left + w * 0.006f, y), Size(pies[0].width - w * 0.012f, w * 0.008f))
        // El pie escalonado.
        pies.forEachIndexed { i, r ->
            drawRect(t.c(if (i == 1) mezcla(BRONCE, LACA, 0.35f) else BRONCE), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.3f) }
            drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.35f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        }
        // El fuste estriado y su collar.
        drawRect(t.c(BRONCE), fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) { cilindro(fuste, t, 1.5f) }
        listOf(0.36f, 0.62f).forEach { f -> drawRect(t.s(0.3f), Offset(fuste.left + fuste.width * f, fuste.top), Size(0.8f * u, fuste.height)) }
        if (o) drawRect(Brush.verticalGradient(listOf(PANTALLA.copy(alpha = 0.6f), Color.Transparent), fuste.top, fuste.top + fuste.height * 0.7f), fuste.topLeft, Size(fuste.width, fuste.height * 0.7f))
        listOf(0.3f, 0.34f).forEach { f -> drawRect(t.c(ORO), Offset(fuste.left - 1.5f * u, fuste.top + fuste.height * f), Size(fuste.width + 3f * u, 1.3f * u)) }
        drawRect(t.c(mezcla(BRONCE, ORO, 0.4f)), collar.topLeft, collar.size)
        clipRect(collar.left, collar.top, collar.right, collar.bottom) { cilindro(collar, t, 1.3f) }
        // La pantalla, de abajo arriba.
        val vidrio = mezcla(MARFIL, MARMOL_VETA, 0.18f)
        formas.forEachIndexed { i, f ->
            val (a, _, ys) = pisos[i]
            val r = Rect(x - w * a, ys.first, x + w * a, ys.second)
            if (o) {
                drawPath(f, mezcla(PANTALLA, LLAMA, 0.3f))
                clipPath(f) {
                    drawOval(PANTALLA, Offset(r.left + r.width * 0.12f, r.top - r.height * 0.3f), Size(r.width * 0.76f, r.height * 1.9f))
                    drawOval(Color(0xFFFFF7DE), Offset(r.left + r.width * 0.3f, r.top + r.height * (0.2f - 0.2f * i)), Size(r.width * 0.4f, r.height * 1.4f))
                    drawRect(mezcla(LLAMA, BRONCE, 0.35f).copy(alpha = 0.35f), Offset(r.right - r.width * 0.1f, r.top), Size(r.width * 0.1f, r.height))
                }
            } else {
                drawPath(f, vidrio)
                clipPath(f) {
                    drawRect(luzMueble(0.4f), Offset(r.left + r.width * 0.08f, r.top), Size(r.width * 0.2f, r.height))
                    drawRect(luzMueble(0.5f), Offset(r.left + r.width * 0.14f, r.top), Size(r.width * 0.05f, r.height))
                    drawRect(sombraMueble(0.14f), Offset(r.left + r.width * 0.56f, r.top), Size(r.width * 0.44f, r.height))
                    drawRect(sombraMueble(0.16f), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
                    drawRect(sombraMueble(0.12f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
                }
            }
            // Las estrías del vidrio y el aro de bronce de cada piso.
            clipPath(f) {
                val nervios = 9 - 2 * i
                for (k in 1 until nervios) {
                    val nx = r.left + r.width * k / nervios
                    drawLine(if (o) mezcla(LLAMA, BRONCE, 0.5f).copy(alpha = 0.22f) else sombraMueble(0.1f), Offset(nx, r.top), Offset(nx + (nx - x) * 0.1f, r.bottom), 0.8f * u)
                }
            }
            val aro = Rect(r.left - w * 0.004f, r.bottom - w * 0.007f, r.right + w * 0.004f, r.bottom)
            drawRect(if (o) mezcla(BRONCE, LACA, 0.3f) else BRONCE, aro.topLeft, aro.size)
            if (!o) clipRect(aro.left, aro.top, aro.right, aro.bottom) { cilindro(aro, t, 1.3f) }
            else drawRect(LLAMA.copy(alpha = 0.55f), aro.topLeft, Size(aro.width, 1f * u))
        }
        drawRect(t.c(BRONCE), remate.topLeft, remate.size)
        drawCircle(t.c(mezcla(BRONCE, ORO, 0.4f)), w * 0.008f, Offset(x, remate.top - w * 0.006f))
        destello(Offset(x - w * 0.003f, remate.top - w * 0.009f), 1.1f * u, t)
        // De noche, la luz que cae por debajo de la pantalla sobre el pie y la repisa.
        if (o) {
            drawOval(PANTALLA.copy(alpha = 0.22f), Offset(x - w * 0.12f, y - w * 0.012f), Size(w * 0.24f, w * 0.03f))
            pies.forEach { r -> drawRect(PANTALLA.copy(alpha = 0.5f), r.topLeft, Size(r.width, 1.2f * u)) }
        }
    }

    /**
     * Una llave dejada en la repisa, tumbada, con su chapa granate colgando por delante del canto y la sombra de las dos.
     */
    private fun DrawScope.llaveDejada(p: Offset) {
        val w = anchoW
        val t = tn(p)
        rotate(84f, p) { llave(p - Offset(0f, w * 0.012f), w * 0.088f, t) }
        rotate(-7f, p) { chapa(p + Offset(w * 0.004f, -w * 0.004f), w * 0.034f, w * 0.05f, GRANATE, t) }
    }

    /**
     * El reloj de sobremesa escalonado: la peana de laca, las alas de ónix verde con su veta, el cuerpo de alabastro en dos
     * escalones, y la esfera negra con su aro de latón, sus doce marcas de oro y las agujas.
     */
    private fun DrawScope.reloj(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW * 1.2f
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.06f))
        val peana = Rect(x - w * 0.086f, y - w * 0.012f, x + w * 0.086f, y)
        val alas = Rect(x - w * 0.076f, y - w * 0.056f, x + w * 0.076f, y - w * 0.012f)
        val cuerpo = Rect(x - w * 0.052f, y - w * 0.112f, x + w * 0.052f, y - w * 0.012f)
        val tope = Rect(x - w * 0.03f, y - w * 0.132f, x + w * 0.03f, y - w * 0.112f)
        val todo = Path().apply { addRect(peana); addRect(alas); addRect(cuerpo); addRect(tope) }
        if (!esNoche) sombraEnPared(todo, Offset(x, y - w * 0.06f), sup, 0.3f)
        contacto(Offset(x + w * 0.01f, y), w * 0.19f, 4f * u, 0.45f)
        drawRect(t.c(MARFIL).copy(alpha = 0.14f), Offset(cuerpo.left, y), Size(cuerpo.width, w * 0.007f))
        laca(peana, t, 5301, tumbada = true, color = mezcla(LACA, LACA_LUZ, 0.6f))
        drawRect(t.c(ORO), peana.topLeft, Size(peana.width, 1.4f * u))
        // Las alas de ónix verde.
        val onix = mezcla(MARMOL, MARMOL_VETA, 0.22f)
        drawRect(t.c(onix), alas.topLeft, alas.size)
        clipRect(alas.left, alas.top, alas.right, alas.bottom) {
            for (k in 0 until 6) {
                val x0 = alas.left + alas.width * azar(5302, k)
                drawLine(t.c(MARMOL_VETA).copy(alpha = 0.5f), Offset(x0, alas.top), Offset(x0 + alas.width * (0.1f - 0.2f * azar(5303, k)), alas.bottom), 0.8f * u)
            }
            caras(alas, t)
        }
        // El cuerpo de alabastro, en dos escalones.
        val alabastro = mezcla(MARFIL, ORO_LUZ, 0.22f)
        listOf(cuerpo, tope).forEachIndexed { i, r ->
            drawRect(t.s(0.25f), Offset(r.left + w * 0.006f, r.top + w * 0.004f), r.size)
            drawRect(t.c(alabastro), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                for (k in 0 until 3) {
                    val y0 = r.top + r.height * azar(5304 + i, k)
                    drawLine(t.c(mezcla(alabastro, RAIZ, 0.5f)).copy(alpha = 0.4f), Offset(r.left, y0), Offset(r.right, y0 + r.height * (0.2f - 0.4f * azar(5306 + i, k))), 0.7f * u)
                }
                caras(r, t, 1.1f)
            }
            drawRect(t.c(ORO), Offset(r.left, r.bottom - 1.3f * u), Size(r.width, 1.3f * u))
            filo(r, t)
        }
        // La esfera.
        val c = Offset(x, y - w * 0.062f)
        val rad = w * 0.036f
        drawCircle(t.s(0.3f), rad * 1.08f, c + Offset(1.5f * u, 2f * u))
        drawCircle(t.c(ORO), rad, c)
        drawArc(t.b(0.6f), 170f, 110f, false, Offset(c.x - rad, c.y - rad), Size(rad * 2, rad * 2), style = Stroke(1.2f * u))
        drawCircle(t.c(mezcla(LACA, LACA_LUZ, 0.4f)), rad * 0.82f, c)
        for (k in 0 until 12) {
            val a = Math.toRadians(k * 30.0)
            val r0 = rad * (if (k % 3 == 0) 0.5f else 0.62f)
            drawLine(t.c(ORO_LUZ), Offset(c.x + r0 * cos(a).toFloat(), c.y + r0 * sin(a).toFloat()), Offset(c.x + rad * 0.74f * cos(a).toFloat(), c.y + rad * 0.74f * sin(a).toFloat()), (if (k % 3 == 0) 1.6f else 0.9f) * u)
        }
        // Las agujas: las doce menos veinte.
        val hora = Math.toRadians(-90.0 + 350.0); val minuto = Math.toRadians(-90.0 + 240.0)
        drawLine(t.c(MARFIL), c, Offset(c.x + rad * 0.4f * cos(hora).toFloat(), c.y + rad * 0.4f * sin(hora).toFloat()), 1.8f * u, StrokeCap.Round)
        drawLine(t.c(MARFIL), c, Offset(c.x + rad * 0.62f * cos(minuto).toFloat(), c.y + rad * 0.62f * sin(minuto).toFloat()), 1.2f * u, StrokeCap.Round)
        drawCircle(t.c(ORO), 1.6f * u, c)
        drawArc(t.b(0.3f), 200f, 60f, false, Offset(c.x - rad * 0.7f, c.y - rad * 0.7f), Size(rad * 1.4f, rad * 1.4f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    /** Una cala: la espata blanca que se enrolla en embudo y acaba en punta, su garganta en sombra y el espádice amarillo. */
    private fun DrawScope.cala(base: Offset, alto: Float, giro: Float, t: Tono) {
        val u = density
        val h = alto
        rotate(giro, base) {
            val x = base.x; val y = base.y
            val espata = Path().apply {
                moveTo(x, y)
                cubicTo(x - 0.3f * h, y - 0.22f * h, x - 0.44f * h, y - 0.6f * h, x - 0.3f * h, y - 0.84f * h)
                cubicTo(x - 0.16f * h, y - 0.98f * h, x + 0.14f * h, y - 1.02f * h, x + 0.4f * h, y - 1.2f * h)
                cubicTo(x + 0.36f * h, y - 0.9f * h, x + 0.4f * h, y - 0.5f * h, x + 0.1f * h, y - 0.16f * h)
                close()
            }
            drawPath(espata, t.c(MARFIL))
            clipPath(espata) {
                drawRect(t.s(0.16f), Offset(x + 0.08f * h, y - 1.2f * h), Size(0.4f * h, 1.2f * h))
                drawRect(t.b(0.3f), Offset(x - 0.44f * h, y - 0.9f * h), Size(0.2f * h, 0.8f * h))
                drawRect(t.c(mezcla(VERDE, MARFIL, 0.55f)).copy(alpha = 0.5f), Offset(x - 0.2f * h, y - 0.16f * h), Size(0.4f * h, 0.16f * h))
            }
            // La garganta: la boca del embudo, en sombra.
            val boca = Path().apply {
                moveTo(x - 0.29f * h, y - 0.83f * h)
                cubicTo(x - 0.1f * h, y - 0.7f * h, x + 0.2f * h, y - 0.8f * h, x + 0.38f * h, y - 1.16f * h)
                cubicTo(x + 0.12f * h, y - 1.0f * h, x - 0.14f * h, y - 0.97f * h, x - 0.29f * h, y - 0.83f * h)
                close()
            }
            drawPath(boca, t.c(mezcla(MARFIL, RAIZ, 0.3f)))
            // El espádice.
            rotate(14f, Offset(x, y - 0.8f * h)) {
                drawOval(t.c(AMARILLO), Offset(x - 0.045f * h, y - 1.0f * h), Size(0.09f * h, 0.3f * h))
                drawOval(t.b(0.4f), Offset(x - 0.04f * h, y - 0.98f * h), Size(0.03f * h, 0.2f * h))
            }
            // El borde que monta: la espata se cierra sobre sí misma.
            drawPath(Path().apply { moveTo(x + 0.02f * h, y - 0.08f * h); cubicTo(x - 0.12f * h, y - 0.3f * h, x - 0.2f * h, y - 0.56f * h, x - 0.1f * h, y - 0.78f * h) }, t.s(0.22f), style = Stroke(0.9f * u))
        }
    }

    /**
     * El jarrón de vidrio verde tallado: el pie grueso, el cuerpo que se abre hacia la boca en caras (una con luz, otra en
     * sombra, con el filo claro de cada arista), el agua y los tallos que se ven dentro. Lleva dos calas, una que se vence
     * hacia la izquierda, y una hoja grande que corta el borde derecho.
     */
    private fun DrawScope.jarron(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.1f))
        val alto = w * 0.2f
        val boca = y - alto
        val talle = y - w * 0.016f
        // El ancho del cuerpo a cada altura: de 0.019 en el talle a 0.05 en la boca (fracciones del ancho).
        fun ancho(yy: Float) = w * (0.019f + 0.031f * (talle - yy) / (talle - boca))
        fun cara(f0: Float, f1: Float) = poligono(x + ancho(boca) * f0, boca, x + ancho(boca) * f1, boca, x + ancho(talle) * f1, talle, x + ancho(talle) * f0, talle)
        val vaso = cara(-1f, 1f)
        val peana = Rect(x - w * 0.034f, talle, x + w * 0.034f, y)
        val hoja = t.c(mezcla(VERDE, LACA, 0.25f))
        // Las flores y la hoja, detrás del vaso: primero sus sombras en la pared.
        val f1 = Offset(x - w * 0.092f, boca - w * 0.1f); val f2 = Offset(x + w * 0.002f, boca - w * 0.15f)
        val tallo1 = Path().apply { moveTo(x - w * 0.012f, boca + w * 0.01f); cubicTo(x - w * 0.02f, boca - w * 0.05f, x - w * 0.055f, boca - w * 0.085f, f1.x, f1.y) }
        val tallo2 = Path().apply { moveTo(x + w * 0.004f, boca + w * 0.01f); cubicTo(x + w * 0.014f, boca - w * 0.05f, x - w * 0.008f, boca - w * 0.11f, f2.x, f2.y) }
        val hc = Offset(x + w * 0.052f, boca - w * 0.07f)
        val limbo = Path().apply {
            moveTo(hc.x - w * 0.05f, hc.y + w * 0.05f)
            cubicTo(hc.x - w * 0.07f, hc.y - w * 0.01f, hc.x - w * 0.02f, hc.y - w * 0.075f, hc.x + w * 0.055f, hc.y - w * 0.105f)
            cubicTo(hc.x + w * 0.065f, hc.y - w * 0.03f, hc.x + w * 0.03f, hc.y + w * 0.04f, hc.x - w * 0.05f, hc.y + w * 0.05f)
            close()
        }
        val sombras = Path().apply { addPath(vaso); addPath(limbo); addOval(Rect(f1.x - w * 0.03f, f1.y - w * 0.075f, f1.x + w * 0.03f, f1.y)); addOval(Rect(f2.x - w * 0.03f, f2.y - w * 0.08f, f2.x + w * 0.03f, f2.y)) }
        if (!esNoche) sombraEnPared(sombras, Offset(x, boca), sup, 0.18f)
        contacto(Offset(x + w * 0.008f, y), w * 0.085f, 3.6f * u, 0.42f)
        // La hoja, con su nervio y su mitad en sombra.
        drawPath(Path().apply { moveTo(x + w * 0.016f, boca + w * 0.01f); cubicTo(x + w * 0.02f, boca - w * 0.01f, x + w * 0.012f, boca - w * 0.016f, hc.x - w * 0.05f, hc.y + w * 0.05f) }, hoja, style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(limbo, hoja)
        clipPath(limbo) {
            drawPath(poligono(hc.x - w * 0.05f, hc.y + w * 0.05f, hc.x + w * 0.055f, hc.y - w * 0.105f, hc.x + w * 0.08f, hc.y + w * 0.06f), t.s(0.24f))
            drawPath(poligono(hc.x - w * 0.05f, hc.y + w * 0.05f, hc.x + w * 0.055f, hc.y - w * 0.105f, hc.x - w * 0.07f, hc.y - w * 0.08f), t.b(0.14f))
        }
        drawLine(t.b(0.4f), Offset(hc.x - w * 0.05f, hc.y + w * 0.05f), Offset(hc.x + w * 0.05f, hc.y - w * 0.098f), 0.8f * u)
        for (k in 1..3) {
            val p = Offset(hc.x - w * 0.05f + w * 0.1f * k / 4f, hc.y + w * 0.05f - w * 0.148f * k / 4f)
            drawLine(t.b(0.2f), p, p + Offset(-w * 0.026f, -w * 0.012f), 0.5f * u)
            drawLine(t.s(0.25f), p, p + Offset(w * 0.022f, w * 0.016f), 0.5f * u)
        }
        // Los tallos y las calas.
        drawPath(tallo1, hoja, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(tallo2, hoja, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(tallo2, t.b(0.3f), style = Stroke(0.7f * u))
        cala(f1 + Offset(w * 0.004f, w * 0.004f), w * 0.072f, -36f, t)
        cala(f2 + Offset(0f, w * 0.004f), w * 0.078f, 6f, t)
        // El pie de vidrio macizo.
        drawRoundRect(t.c(mezcla(VERDE, LACA, 0.3f)), peana.topLeft, peana.size, CornerRadius(w * 0.004f))
        drawRect(t.b(0.5f), Offset(peana.left + w * 0.004f, peana.top), Size(peana.width - w * 0.008f, 1f * u))
        drawRect(t.b(0.25f), Offset(peana.left + w * 0.006f, peana.top), Size(w * 0.012f, peana.height))
        drawRect(t.s(0.3f), Offset(peana.right - w * 0.016f, peana.top), Size(w * 0.016f, peana.height))
        // El cuerpo: el vidrio verde, los tallos dentro, el agua y las cinco caras.
        drawPath(vaso, t.c(mezcla(VERDE, MARMOL_VETA, 0.16f)).copy(alpha = 0.92f))
        clipPath(vaso) {
            drawLine(hoja.copy(alpha = 0.8f), Offset(x - w * 0.012f, boca), Offset(x + w * 0.004f, talle), 2f * u)
            drawLine(hoja.copy(alpha = 0.8f), Offset(x + w * 0.006f, boca), Offset(x - w * 0.006f, talle), 2f * u)
            drawLine(hoja.copy(alpha = 0.8f), Offset(x + w * 0.018f, boca), Offset(x + w * 0.002f, talle), 1.8f * u)
            drawRect(t.c(MARMOL).copy(alpha = 0.35f), Offset(x - w * 0.05f, boca + alto * 0.26f), Size(w * 0.1f, alto))
            drawPath(cara(-1f, -0.56f), t.b(0.34f))
            drawPath(cara(-0.56f, -0.1f), t.b(0.1f))
            drawPath(cara(0.36f, 0.72f), t.s(0.2f))
            drawPath(cara(0.72f, 1f), t.s(0.36f))
            listOf(-0.56f to 0.6f, -0.1f to 0.3f, 0.36f to 0.3f, 0.72f to 0.35f).forEach { (f, a) ->
                drawLine(t.b(a), Offset(x + ancho(boca) * f, boca), Offset(x + ancho(talle) * f, talle), 0.9f * u)
            }
            // La talla: una cenefa de rombos bajo la boca y el nivel del agua.
            val cy = boca + alto * 0.13f
            for (k in -3..3) {
                val cx = x + ancho(cy) * k / 3.6f
                val rr = ancho(cy) * 0.12f
                drawPath(poligono(cx, cy - rr * 1.5f, cx + rr, cy, cx, cy + rr * 1.5f, cx - rr, cy), t.b(0.3f), style = Stroke(0.7f * u))
            }
            drawRect(t.b(0.45f), Offset(x - w * 0.05f, boca + alto * 0.26f), Size(w * 0.1f, 0.9f * u))
            drawRect(t.s(0.25f), Offset(x - w * 0.05f, talle - alto * 0.05f), Size(w * 0.1f, alto * 0.05f))
        }
        drawOval(t.c(mezcla(MARMOL_VETA, MARFIL, 0.4f)).copy(alpha = 0.8f), Offset(x - ancho(boca), boca - w * 0.004f), Size(ancho(boca) * 2, w * 0.008f), style = Stroke(1f * u))
        destello(Offset(x - ancho(boca) * 0.78f, boca + alto * 0.2f), 1.3f * u, t)
        filo(Rect(x - ancho(boca) * 0.9f, boca, x - ancho(boca) * 0.2f, talle), t)
    }

    // ---------------------------------------------------------------- el mostrador

    /**
     * El mostrador: la cubierta de mármol verde vista un poco desde arriba (su veta, su brillo largo y la sombra del rincón),
     * el canto de latón, el frente de laca con sus reflejos, sus filetes y la cenefa de chevrones dorados, la placa de latón
     * (lisa para el «y N más», o grabada con un abanico), el zócalo en dos escalones, el suelo y el borde de la alfombra
     * granate con su greca y sus flecos.
     */
    private fun DrawScope.mostrador(d: Disposicion, w: Float, alto: Float) {
        val u = density
        val o = esNoche
        val b = d.base; val hb = b.height
        val mesa = b.top + hb * MESA; val borde = b.top + hb * BORDE; val laton = b.top + hb * LATON
        val zoc = b.top + hb * ZOCALO; val suelo = b.top + hb * SUELO
        // ---- la cubierta de mármol
        val n = 8
        for (k in 0 until n) {
            val x0 = w * k / n
            val t = tn(Offset(x0 + w / n / 2, mesa), if (o) 0f else 0.1f * (1f - k / (n - 1f)))
            drawRect(t.c(MARMOL), Offset(x0, mesa), Size(w / n + 0.5f, borde - mesa))
        }
        val tm = tn(Offset(w * 0.4f, mesa))
        clipRect(0f, mesa, w, borde) {
            pinceladas(Rect(0f, mesa, w, borde), if (o) NOCHE else mezcla(MARMOL, LACA, 0.5f), 5401, 14, u, 0.2f)
            for (k in 0 until 11) {
                val x0 = w * (-0.05f + 1.1f * azar(5402, k))
                val veta = Path().apply {
                    moveTo(x0, mesa)
                    cubicTo(x0 + w * 0.05f * (azar(5403, k) - 0.3f), mesa + (borde - mesa) * 0.4f, x0 + w * 0.12f * (azar(5404, k) - 0.5f), mesa + (borde - mesa) * 0.6f, x0 + w * 0.2f * (azar(5405, k) - 0.4f), borde)
                }
                val t = tn(Offset(x0, mesa))
                drawPath(veta, t.c(MARMOL_VETA).copy(alpha = 0.3f + 0.3f * azar(5406, k)), style = Stroke((0.6f + 0.9f * azar(5407, k)) * u))
            }
            // El brillo largo del mármol pulido.
            drawRect(tm.b(0.12f), Offset(0f, mesa + (borde - mesa) * 0.5f), Size(w, (borde - mesa) * 0.22f))
            drawRect(tm.b(0.14f), Offset(w * 0.1f, mesa + (borde - mesa) * 0.56f), Size(w * 0.6f, (borde - mesa) * 0.07f))
        }
        drawRect(tm.s(0.42f), Offset(0f, mesa), Size(w, 2.2f * u))
        drawRect(tm.s(0.2f), Offset(0f, mesa + 2.2f * u), Size(w, (borde - mesa) * 0.14f))
        // ---- el canto de latón, con sus dos cañas
        fileteH(0f, w, borde, laton - borde, 8, mezcla(ORO, ORO_OSC, 0.25f))
        val tl = tn(Offset(w * 0.5f, borde))
        drawRect(tl.b(0.5f), Offset(0f, borde + (laton - borde) * 0.38f), Size(w, 1f * u))
        drawRect(tl.s(0.3f), Offset(0f, borde + (laton - borde) * 0.52f), Size(w, 1.2f * u))
        drawRect(tl.s(0.35f), Offset(0f, laton - 1.3f * u), Size(w, 1.3f * u))
        // ---- el frente de laca
        for (k in 0 until n) {
            val r = Rect(w * k / n, laton, w * (k + 1) / n + 0.5f, zoc)
            val t = tn(r.center)
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.3f)), r.topLeft, r.size)
            if (k % 2 == 0) {
                val x1 = r.left + r.width * (0.15f + 0.5f * azar(5410, k))
                drawRect(t.b(0.07f), Offset(x1, r.top), Size(r.width * 0.34f, r.height))
                drawRect(t.b(0.1f), Offset(x1 + r.width * 0.1f, r.top), Size(r.width * 0.08f, r.height))
            }
        }
        val tf = tn(Offset(w * 0.5f, laton))
        drawRect(tf.s(0.45f), Offset(0f, laton), Size(w, w * 0.007f))
        drawRect(tf.s(0.22f), Offset(0f, laton + w * 0.007f), Size(w, w * 0.01f))
        val f0 = laton + (zoc - laton) * 0.2f; val f1 = zoc - (zoc - laton) * 0.12f
        fileteH(0f, w, f0, 1.2f * u, 8)
        fileteH(0f, w, f1, 1.2f * u, 8)
        // La cenefa de chevrones: tres uves anidadas por tramo, salvo donde va la placa.
        val pl = d.enBase(placa)
        val paso = w * 0.068f
        var x = w * 0.012f
        while (x < w) {
            if (x + paso < pl.left - w * 0.02f || x > pl.right + w * 0.02f) {
                val t = tn(Offset(x + paso / 2, (f0 + f1) / 2))
                for (j in 0 until 3) {
                    val mete = paso * 0.13f * j
                    val ya = f0 + (f1 - f0) * (0.16f + 0.06f * j); val yb = f1 - (f1 - f0) * (0.14f + 0.22f * j)
                    if (yb > ya) {
                        drawLine(t.c(if (j == 1) mezcla(ORO, ORO_OSC, 0.5f) else ORO), Offset(x + mete, ya), Offset(x + paso / 2, yb), 1.3f * u)
                        drawLine(t.c(if (j == 1) ORO_OSC else mezcla(ORO, ORO_OSC, 0.4f)), Offset(x + paso / 2, yb), Offset(x + paso - mete, ya), 1.3f * u)
                    }
                }
            }
            x += paso
        }
        // ---- la placa de latón, con las esquinas en chaflán y sus tornillos
        val tp = tn(pl.center)
        val e = pl.height * 0.2f
        val forma = poligono(pl.left + e, pl.top, pl.right - e, pl.top, pl.right, pl.top + e, pl.right, pl.bottom - e, pl.right - e, pl.bottom, pl.left + e, pl.bottom, pl.left, pl.bottom - e, pl.left, pl.top + e)
        translate(1.6f * u, 2f * u) { drawPath(forma, tp.s(0.4f)) }
        drawPath(forma, if (!o) mezcla(ORO, ORO_LUZ, 0.75f) else if (d.mas > 0) mezcla(PLACA_LEJOS, mezcla(ORO_LUZ, LLAMA, 0.15f), (1.3f * tp.k.pow(0.6f)).coerceAtMost(1f)) else tp.c(mezcla(ORO, ORO_LUZ, 0.5f)))
        clipPath(forma) {
            drawRect(tp.b(0.3f), pl.topLeft, Size(pl.width, pl.height * 0.16f))
            drawRect(tp.s(0.14f), Offset(pl.left, pl.bottom - pl.height * 0.14f), Size(pl.width, pl.height * 0.14f))
        }
        val grabado = if (o && d.mas <= 0) tp.c(ORO_OSC) else ORO_OSC
        drawPath(forma, grabado.copy(alpha = 0.8f), style = Stroke(0.9f * u))
        listOf(pl.left + e * 0.8f, pl.right - e * 0.8f).forEach { sx ->
            drawCircle(grabado, 1.4f * u, Offset(sx, pl.center.y))
            drawCircle(luzMueble(if (o) 0.25f else 0.6f), 0.6f * u, Offset(sx - 0.4f * u, pl.center.y - 0.4f * u))
        }
        if (d.mas <= 0) {
            // Sin «y N más»: el abanico del hotel, grabado.
            val c = Offset(pl.center.x, pl.bottom - pl.height * 0.2f)
            val rr = pl.height * 0.56f
            for (k in 0..8) {
                val a = Math.toRadians(180.0 + 22.5 * k)
                drawLine(grabado.copy(alpha = 0.85f), Offset(c.x + rr * 0.32f * cos(a).toFloat(), c.y + rr * 0.32f * sin(a).toFloat()), Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat()), (if (k % 2 == 0) 1.3f else 0.8f) * u)
            }
            drawArc(grabado.copy(alpha = 0.85f), 180f, 180f, false, Offset(c.x - rr * 0.3f, c.y - rr * 0.3f), Size(rr * 0.6f, rr * 0.6f), style = Stroke(1f * u))
            drawLine(grabado.copy(alpha = 0.85f), Offset(c.x - rr * 1.5f, c.y), Offset(c.x + rr * 1.5f, c.y), 1f * u)
        }
        // ---- el zócalo, en dos escalones
        val z1 = zoc + (suelo - zoc) * 0.45f
        for (k in 0 until n) {
            val x0 = w * k / n
            val t = tn(Offset(x0 + w / n / 2, zoc))
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.55f)), Offset(x0, zoc), Size(w / n + 0.5f, z1 - zoc))
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.2f)), Offset(x0, z1), Size(w / n + 0.5f, suelo - z1))
        }
        fileteH(0f, w, zoc, 1.4f * u, 8)
        fileteH(0f, w, z1, 1f * u, 8, mezcla(ORO, ORO_OSC, 0.4f))
        // ---- el suelo de madera oscura y el borde de la alfombra
        val ts = tn(Offset(w * 0.4f, suelo))
        drawRect(ts.c(mezcla(RAIZ_OSC, LACA, 0.45f)), Offset(0f, suelo), Size(w, alto - suelo))
        drawRect(ts.s(0.45f), Offset(0f, suelo), Size(w, 2.4f * u))
        drawRect(ts.b(0.1f), Offset(0f, suelo + (alto - suelo) * 0.3f), Size(w, (alto - suelo) * 0.18f))
        val a0 = suelo + (alto - suelo) * 0.3f
        val fin = w * 0.74f
        drawRect(ts.s(0.35f), Offset(0f, a0 - 1.5f * u), Size(fin + 2f * u, 1.5f * u))
        drawRect(ts.c(GRANATE), Offset(0f, a0), Size(fin, alto - a0))
        drawRect(ts.c(MARFIL), Offset(0f, a0), Size(fin, (alto - a0) * 0.16f))
        drawRect(ts.c(LACA), Offset(0f, a0 + (alto - a0) * 0.16f), Size(fin, (alto - a0) * 0.1f))
        val g0 = a0 + (alto - a0) * 0.4f; val g1 = alto - (alto - a0) * 0.12f
        var gx = w * 0.01f
        var sube = true
        while (gx < fin - w * 0.03f) {
            // La greca: un zigzag escalonado de oro.
            val gy = if (sube) g0 else g1
            drawRect(ts.c(ORO), Offset(gx, minOf(gy, (g0 + g1) / 2)), Size(1.4f * u, abs(gy - (g0 + g1) / 2)))
            drawRect(ts.c(ORO), Offset(gx, gy), Size(w * 0.022f, 1.4f * u))
            drawRect(ts.c(ORO), Offset(gx + w * 0.022f, minOf(gy, (g0 + g1) / 2)), Size(1.4f * u, abs(gy - (g0 + g1) / 2)))
            drawRect(ts.c(ORO), Offset(gx + w * 0.022f, (g0 + g1) / 2), Size(w * 0.012f, 1.4f * u))
            gx += w * 0.034f
            sube = !sube
        }
        for (k in 0 until 9) {
            val fy = a0 + (alto - a0) * (k + 0.5f) / 9f
            drawLine(ts.c(MARFIL), Offset(fin, fy), Offset(fin + w * (0.012f + 0.006f * azar(5420, k)), fy + (azar(5421, k) - 0.5f) * 2f * u), 0.9f * u)
        }
    }

    /**
     * El teléfono de candelero, de esmalte negro con sus aros de latón: la base acampanada, el fuste, el micrófono en lo alto
     * con su bocina hacia la derecha, la horquilla y el auricular colgado a la izquierda, y el cordón que cae sobre el mármol.
     */
    private fun DrawScope.telefono(pie: Offset, mesa: Float) {
        val u = density
        val w = anchoW * 1.12f
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.07f))
        val negro = t.c(mezcla(LACA, LACA_LUZ, 0.2f))
        val base = Path().apply {
            moveTo(x - w * 0.048f, y); lineTo(x - w * 0.048f, y - w * 0.008f)
            cubicTo(x - w * 0.042f, y - w * 0.024f, x - w * 0.016f, y - w * 0.026f, x - w * 0.011f, y - w * 0.046f)
            lineTo(x + w * 0.011f, y - w * 0.046f)
            cubicTo(x + w * 0.016f, y - w * 0.026f, x + w * 0.042f, y - w * 0.024f, x + w * 0.048f, y - w * 0.008f)
            lineTo(x + w * 0.048f, y); close()
        }
        val fuste = Rect(x - w * 0.008f, y - w * 0.128f, x + w * 0.008f, y - w * 0.046f)
        val cabeza = Offset(x, y - w * 0.136f)
        val ax = x - w * 0.04f
        val auricular = Path().apply {
            moveTo(ax - w * 0.007f, y - w * 0.108f); lineTo(ax + w * 0.007f, y - w * 0.108f)
            lineTo(ax + w * 0.01f, y - w * 0.05f)
            cubicTo(ax + w * 0.012f, y - w * 0.04f, ax + w * 0.019f, y - w * 0.036f, ax + w * 0.02f, y - w * 0.024f)
            lineTo(ax - w * 0.02f, y - w * 0.024f)
            cubicTo(ax - w * 0.019f, y - w * 0.036f, ax - w * 0.012f, y - w * 0.04f, ax - w * 0.01f, y - w * 0.05f)
            close()
        }
        val todo = Path().apply { addPath(base); addRect(fuste); addPath(auricular); addOval(Rect(cabeza.x - w * 0.02f, cabeza.y - w * 0.026f, cabeza.x + w * 0.04f, cabeza.y + w * 0.02f)) }
        sombraEnPared(todo, Offset(x, y - w * 0.08f), mesa, 0.22f)
        contacto(Offset(x + w * 0.008f, y), w * 0.115f, 4f * u, 0.45f)
        // El cordón: del auricular a la base, y de la base hacia el borde.
        drawPath(Path().apply { moveTo(ax, y - w * 0.024f); cubicTo(ax - w * 0.012f, y + w * 0.004f, ax - w * 0.03f, y + w * 0.014f, x - w * 0.04f, y - w * 0.002f) }, t.c(mezcla(RAIZ_OSC, LACA, 0.5f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        drawPath(Path().apply { moveTo(x + w * 0.04f, y - w * 0.004f); cubicTo(x + w * 0.07f, y + w * 0.012f, x + w * 0.02f, y + w * 0.022f, -w * 0.01f, y + w * 0.016f) }, t.c(mezcla(RAIZ_OSC, LACA, 0.5f)), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        // La base y el fuste.
        drawPath(base, negro)
        clipPath(base) {
            drawRect(t.b(0.16f), Offset(x - w * 0.04f, y - w * 0.05f), Size(w * 0.02f, w * 0.05f))
            drawPath(Path().apply { moveTo(x - w * 0.036f, y - w * 0.012f); cubicTo(x - w * 0.03f, y - w * 0.022f, x - w * 0.014f, y - w * 0.026f, x - w * 0.008f, y - w * 0.044f) }, t.b(0.45f), style = Stroke(1.2f * u, cap = StrokeCap.Round))
            drawRect(t.s(0.3f), Offset(x + w * 0.02f, y - w * 0.05f), Size(w * 0.03f, w * 0.05f))
        }
        drawRect(t.c(ORO), Offset(x - w * 0.048f, y - w * 0.008f), Size(w * 0.096f, 1.2f * u))
        drawRect(negro, fuste.topLeft, fuste.size)
        clipRect(fuste.left, fuste.top, fuste.right, fuste.bottom) { cilindro(fuste, t, 1.4f) }
        listOf(0.04f, 0.9f).forEach { f -> drawRect(t.c(ORO), Offset(fuste.left - 1.2f * u, fuste.top + fuste.height * f), Size(fuste.width + 2.4f * u, 1.6f * u)) }
        // La horquilla de latón y el auricular colgado.
        drawRect(t.c(ORO), Offset(ax - w * 0.012f, y - w * 0.1f), Size(x - ax + w * 0.004f, w * 0.006f))
        drawRect(t.b(0.5f), Offset(ax - w * 0.012f, y - w * 0.1f), Size(x - ax + w * 0.004f, 0.8f * u))
        listOf(ax - w * 0.012f, ax + w * 0.009f).forEach { hx -> drawRect(t.c(ORO), Offset(hx, y - w * 0.112f), Size(w * 0.004f, w * 0.014f)) }
        drawPath(auricular, negro)
        clipPath(auricular) {
            drawRect(t.b(0.4f), Offset(ax - w * 0.006f, y - w * 0.11f), Size(1.4f * u, w * 0.07f))
            drawRect(t.s(0.3f), Offset(ax + w * 0.006f, y - w * 0.11f), Size(w * 0.02f, w * 0.09f))
        }
        drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.7f)), Offset(ax - w * 0.02f, y - w * 0.026f), Size(w * 0.04f, w * 0.006f))
        drawRect(t.c(ORO), Offset(ax - w * 0.008f, y - w * 0.112f), Size(w * 0.016f, w * 0.005f))
        // El micrófono: la caja redonda y la bocina, mirando un poco hacia arriba.
        rotate(-16f, cabeza) {
            val caja = Rect(cabeza.x - w * 0.012f, cabeza.y - w * 0.024f, cabeza.x + w * 0.012f, cabeza.y + w * 0.024f)
            drawRoundRect(negro, caja.topLeft, caja.size, CornerRadius(w * 0.008f))
            drawRect(t.b(0.4f), Offset(caja.left + 1.2f * u, caja.top + w * 0.006f), Size(1.3f * u, caja.height - w * 0.012f))
            drawRect(t.c(ORO), Offset(caja.right - 1f * u, caja.top + w * 0.004f), Size(1.6f * u, caja.height - w * 0.008f))
            val bocina = poligono(caja.right, cabeza.y - w * 0.009f, caja.right + w * 0.026f, cabeza.y - w * 0.02f, caja.right + w * 0.026f, cabeza.y + w * 0.02f, caja.right, cabeza.y + w * 0.009f)
            drawPath(bocina, negro)
            clipPath(bocina) {
                drawRect(t.b(0.3f), Offset(caja.right, cabeza.y - w * 0.02f), Size(w * 0.026f, w * 0.008f))
                drawRect(t.s(0.3f), Offset(caja.right, cabeza.y + w * 0.006f), Size(w * 0.026f, w * 0.014f))
            }
            drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.8f)), Offset(caja.right + w * 0.024f, cabeza.y - w * 0.021f), Size(w * 0.004f, w * 0.042f))
        }
        destello(Offset(x - w * 0.004f, y - w * 0.118f), 1f * u, t)
    }

    /**
     * El libro de registro, abierto sobre el mármol y visto un poco desde arriba: las tapas de cuero granate, las dos hojas
     * con sus renglones, sus columnas y lo ya escrito, el canto de las hojas, la cinta que cae por el canto del mostrador y la
     * estilográfica cruzada encima.
     */
    private fun DrawScope.libro(w: Float, mesa: Float, borde: Float) {
        val u = density
        val prof = borde - mesa
        val y0 = mesa + prof * 0.1f; val y1 = mesa + prof * 0.8f
        val xm = w * 0.4f
        val a0 = w * 0.262f; val z0 = w * 0.538f      // el fondo, más angosto
        val a1 = w * 0.244f; val z1 = w * 0.556f      // el frente
        val t = tn(Offset(xm, y0))
        val m = w * 0.006f
        val tapas = poligono(a0 - m, y0 - m * 0.6f, z0 + m, y0 - m * 0.6f, z1 + m * 1.4f, y1 + m, a1 - m * 1.4f, y1 + m)
        translate(w * 0.008f, w * 0.005f) { drawPath(tapas, t.s(0.42f)) }
        drawPath(tapas, t.c(mezcla(GRANATE, LACA, 0.25f)))
        drawLine(t.c(ORO), Offset(a1 - m * 1.4f, y1 + m), Offset(z1 + m * 1.4f, y1 + m), 1f * u)
        // El canto de las hojas, al frente.
        val gordo = prof * 0.09f
        drawPath(poligono(a1, y1, z1, y1, z1 + m * 0.4f, y1 + gordo, a1 - m * 0.4f, y1 + gordo), t.c(mezcla(MARFIL, ESTRAZA, 0.3f)))
        drawLine(t.s(0.25f), Offset(a1, y1 + gordo * 0.5f), Offset(z1, y1 + gordo * 0.5f), 0.6f * u)
        // Las dos hojas: la de la izquierda cae hacia el lomo (en sombra); la de la derecha sube hacia la luz.
        val izq = poligono(a0, y0, xm, y0 + prof * 0.03f, xm, y1 + prof * 0.02f, a1, y1)
        val der = poligono(xm, y0 + prof * 0.03f, z0, y0, z1, y1, xm, y1 + prof * 0.02f)
        drawPath(izq, t.c(MARFIL))
        drawPath(der, t.c(mezcla(MARFIL, CREMA, 0.6f)))
        clipPath(izq) { drawRect(t.s(0.2f), Offset(xm - w * 0.022f, y0), Size(w * 0.022f, y1 - y0 + prof * 0.03f)); drawRect(t.s(0.1f), Offset(xm - w * 0.05f, y0), Size(w * 0.028f, y1 - y0 + prof * 0.03f)) }
        clipPath(der) { drawRect(t.b(0.3f), Offset(xm, y0), Size(w * 0.02f, y1 - y0 + prof * 0.03f)) }
        // Los renglones, las columnas y lo escrito.
        val renglones = 6
        for (k in 1 until renglones) {
            val f = k / renglones.toFloat()
            val y = y0 + (y1 - y0) * f
            val xa = a0 + (a1 - a0) * f; val xz = z0 + (z1 - z0) * f
            drawLine(t.c(AZUL).copy(alpha = 0.4f), Offset(xa + w * 0.006f, y), Offset(xm - w * 0.006f, y + prof * 0.025f), 0.6f * u)
            drawLine(t.c(AZUL).copy(alpha = 0.4f), Offset(xm + w * 0.006f, y + prof * 0.025f), Offset(xz - w * 0.006f, y), 0.6f * u)
            // La letra, a pluma: toda la hoja izquierda y los primeros renglones de la derecha.
            val tinta = t.c(TINTA).copy(alpha = 0.65f)
            var px = xa + w * 0.012f
            var j = 0
            while (px < xm - w * 0.03f) {
                val largo = w * (0.012f + 0.02f * azar(5501 + k, j))
                drawLine(tinta, Offset(px, y - prof * 0.045f), Offset(px + largo, y - prof * 0.04f), 0.9f * u)
                px += largo + w * 0.008f; j++
            }
            if (k <= 2) {
                px = xm + w * 0.014f
                while (px < xm + w * (if (k == 1) 0.12f else 0.07f)) {
                    val largo = w * (0.012f + 0.02f * azar(5511 + k, j))
                    drawLine(tinta, Offset(px, y - prof * 0.02f), Offset(px + largo, y - prof * 0.025f), 0.9f * u)
                    px += largo + w * 0.008f; j++
                }
            }
        }
        listOf(0.72f, 0.86f).forEach { f ->
            drawLine(t.c(GRANATE).copy(alpha = 0.45f), Offset(a0 + (xm - a0) * f, y0 + prof * 0.03f * f), Offset(a1 + (xm - a1) * f, y1 + prof * 0.02f * f), 0.6f * u)
            drawLine(t.c(GRANATE).copy(alpha = 0.45f), Offset(xm + (z0 - xm) * f, y0 + prof * 0.03f * (1 - f)), Offset(xm + (z1 - xm) * f, y1 + prof * 0.02f * (1 - f)), 0.6f * u)
        }
        drawLine(t.s(0.4f), Offset(xm, y0 + prof * 0.03f), Offset(xm, y1 + prof * 0.02f), 1f * u)
        // La cinta de registro, que cae por el canto del mostrador.
        val cx = xm + w * 0.012f
        drawPath(poligono(cx, y1, cx + w * 0.011f, y1, cx + w * 0.014f, borde + prof * 0.34f, cx + w * 0.0085f, borde + prof * 0.26f, cx + w * 0.003f, borde + prof * 0.34f), t.c(GRANATE))
        drawLine(t.b(0.35f), Offset(cx + 0.8f * u, y1), Offset(cx + w * 0.004f, borde + prof * 0.3f), 0.7f * u)
        // La estilográfica, cruzada sobre la hoja derecha, con su sombra.
        val p0 = Offset(w * 0.438f, y1 - prof * 0.14f); val p1 = Offset(w * 0.535f, y0 + prof * 0.22f)
        translate(w * 0.004f, w * 0.004f) { drawLine(t.s(0.35f), p0, p1, 3.4f * u, StrokeCap.Round) }
        drawLine(t.c(mezcla(LACA, LACA_LUZ, 0.3f)), p0, p1, 3.4f * u, StrokeCap.Round)
        drawLine(t.b(0.5f), p0 + Offset(0f, -0.9f * u), p1 + Offset(0f, -0.9f * u), 0.8f * u)
        val banda = p0 + (p1 - p0) * 0.62f
        drawLine(t.c(ORO), banda, p0 + (p1 - p0) * 0.66f, 3.6f * u)
        drawLine(t.c(ORO), p0 + (p1 - p0) * 0.7f + Offset(0f, -1.6f * u), p0 + (p1 - p0) * 0.94f + Offset(0f, -1.6f * u), 0.9f * u)
        val punta = p0 - (p1 - p0) * 0.14f
        drawPath(poligono(p0.x, p0.y - 1.5f * u, p0.x, p0.y + 1.5f * u, punta.x, punta.y), t.c(ORO_LUZ))
    }

    /**
     * El secante de balancín, de perfil: la tabla de laca con su pomo de latón y, debajo, la suela en media luna forrada de
     * papel secante rosado, con sus manchas de tinta.
     */
    private fun DrawScope.secante(pie: Offset) {
        val u = density
        val w = anchoW * 1.25f
        val x = pie.x; val y = pie.y
        val t = tn(pie)
        contacto(Offset(x + w * 0.008f, y), w * 0.07f, 3.2f * u, 0.42f)
        val suela = Path().apply {
            moveTo(x - w * 0.034f, y - w * 0.02f); lineTo(x + w * 0.034f, y - w * 0.02f)
            cubicTo(x + w * 0.03f, y + w * 0.004f, x - w * 0.03f, y + w * 0.004f, x - w * 0.034f, y - w * 0.02f)
            close()
        }
        drawPath(suela, t.c(mezcla(LACA, LACA_LUZ, 0.7f)))
        clipPath(suela) {
            drawRect(t.b(0.14f), Offset(x - w * 0.034f, y - w * 0.02f), Size(w * 0.02f, w * 0.03f))
            drawRect(t.s(0.25f), Offset(x + w * 0.012f, y - w * 0.02f), Size(w * 0.03f, w * 0.03f))
        }
        // El papel secante, que sube por las dos puntas.
        val papel = Path().apply { moveTo(x - w * 0.034f, y - w * 0.02f); cubicTo(x - w * 0.03f, y + w * 0.004f, x + w * 0.03f, y + w * 0.004f, x + w * 0.034f, y - w * 0.02f) }
        drawPath(papel, t.c(mezcla(GRANATE, MARFIL, 0.66f)), style = Stroke(2.6f * u))
        drawCircle(t.c(TINTA).copy(alpha = 0.5f), 1f * u, Offset(x - w * 0.01f, y - w * 0.003f))
        drawCircle(t.c(TINTA).copy(alpha = 0.4f), 0.8f * u, Offset(x + w * 0.012f, y - w * 0.004f))
        val tabla = Rect(x - w * 0.037f, y - w * 0.028f, x + w * 0.037f, y - w * 0.019f)
        laca(tabla, t, 5601, tumbada = true, color = mezcla(LACA, LACA_LUZ, 0.45f))
        drawRect(t.c(ORO), tabla.topLeft, Size(tabla.width, 1.2f * u))
        drawRect(t.c(mezcla(ORO, ORO_OSC, 0.4f)), Offset(x - w * 0.003f, y - w * 0.036f), Size(w * 0.006f, w * 0.008f))
        drawCircle(t.c(ORO), w * 0.009f, Offset(x, y - w * 0.042f))
        drawCircle(t.s(0.25f), w * 0.005f, Offset(x + w * 0.003f, y - w * 0.04f))
        destello(Offset(x - w * 0.003f, y - w * 0.045f), 0.9f * u, t)
    }

    /** El timbre de recepción: la peana negra, la campana de latón con su brillo y su lado en sombra, y el botón. */
    private fun DrawScope.timbre(pie: Offset) {
        val u = density
        val w = anchoW * 1.25f
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.02f))
        contacto(Offset(x + w * 0.008f, y), w * 0.085f, 3.4f * u, 0.45f)
        // El reflejo en el mármol.
        drawOval(t.c(ORO).copy(alpha = 0.22f), Offset(x - w * 0.026f, y + 1f * u), Size(w * 0.052f, w * 0.012f))
        drawRoundRect(t.c(mezcla(LACA, LACA_LUZ, 0.4f)), Offset(x - w * 0.038f, y - w * 0.008f), Size(w * 0.076f, w * 0.008f), CornerRadius(w * 0.003f))
        drawRect(t.b(0.3f), Offset(x - w * 0.036f, y - w * 0.008f), Size(w * 0.072f, 0.8f * u))
        val campana = Path().apply {
            moveTo(x - w * 0.031f, y - w * 0.008f)
            cubicTo(x - w * 0.031f, y - w * 0.03f, x - w * 0.018f, y - w * 0.04f, x, y - w * 0.04f)
            cubicTo(x + w * 0.018f, y - w * 0.04f, x + w * 0.031f, y - w * 0.03f, x + w * 0.031f, y - w * 0.008f)
            close()
        }
        drawPath(campana, t.c(ORO))
        clipPath(campana) {
            drawOval(t.b(0.45f), Offset(x - w * 0.026f, y - w * 0.038f), Size(w * 0.022f, w * 0.03f))
            drawRect(t.s(0.2f), Offset(x + w * 0.006f, y - w * 0.04f), Size(w * 0.03f, w * 0.04f))
            drawRect(t.s(0.22f), Offset(x + w * 0.018f, y - w * 0.04f), Size(w * 0.02f, w * 0.04f))
            drawRect(t.s(0.25f), Offset(x - w * 0.04f, y - w * 0.012f), Size(w * 0.08f, w * 0.004f))
        }
        drawRect(t.c(mezcla(ORO, ORO_OSC, 0.5f)), Offset(x - w * 0.003f, y - w * 0.048f), Size(w * 0.006f, w * 0.009f))
        drawOval(t.c(ORO_LUZ), Offset(x - w * 0.008f, y - w * 0.054f), Size(w * 0.016f, w * 0.008f))
        destello(Offset(x - w * 0.016f, y - w * 0.03f), 1.2f * u, t)
    }

    /**
     * El fedora olvidado: el ala de fieltro gris vista un poco desde arriba, con el frente caído, la copa con su hendidura y su
     * pellizco, la cinta negra de gro con su lazo plano y su brillo; su sombra en la pared y en el mármol.
     */
    private fun DrawScope.sombrero(pie: Offset, mesa: Float) {
        val u = density
        val w = anchoW
        val x = pie.x; val y = pie.y
        val t = tn(Offset(x, y - w * 0.05f))
        val fieltro = t.c(FIELTRO)
        val ala = Path().apply {
            moveTo(x - w * 0.104f, y - w * 0.008f)
            cubicTo(x - w * 0.1f, y - w * 0.03f, x - w * 0.05f, y - w * 0.034f, x, y - w * 0.034f)
            cubicTo(x + w * 0.05f, y - w * 0.034f, x + w * 0.1f, y - w * 0.03f, x + w * 0.104f, y - w * 0.012f)
            cubicTo(x + w * 0.1f, y + w * 0.004f, x + w * 0.04f, y + w * 0.014f, x - w * 0.01f, y + w * 0.014f)
            cubicTo(x - w * 0.06f, y + w * 0.014f, x - w * 0.104f, y + w * 0.006f, x - w * 0.104f, y - w * 0.008f)
            close()
        }
        val copa = Path().apply {
            moveTo(x - w * 0.058f, y - w * 0.012f)
            cubicTo(x - w * 0.064f, y - w * 0.05f, x - w * 0.054f, y - w * 0.086f, x - w * 0.03f, y - w * 0.092f)
            cubicTo(x - w * 0.014f, y - w * 0.096f, x - w * 0.008f, y - w * 0.082f, x, y - w * 0.08f)
            cubicTo(x + w * 0.008f, y - w * 0.082f, x + w * 0.016f, y - w * 0.096f, x + w * 0.032f, y - w * 0.092f)
            cubicTo(x + w * 0.056f, y - w * 0.086f, x + w * 0.064f, y - w * 0.05f, x + w * 0.058f, y - w * 0.012f)
            cubicTo(x + w * 0.03f, y - w * 0.002f, x - w * 0.03f, y - w * 0.002f, x - w * 0.058f, y - w * 0.012f)
            close()
        }
        sombraEnPared(Path().apply { addPath(copa); addPath(ala) }, Offset(x, y - w * 0.04f), mesa, 0.24f)
        drawOval(t.s(0.45f), Offset(x - w * 0.09f, y - w * 0.002f), Size(w * 0.215f, w * 0.022f))
        // El ala: por debajo, en sombra; por encima, la luz a la izquierda.
        translate(0f, w * 0.004f) { drawPath(ala, t.c(mezcla(FIELTRO, LACA, 0.55f))) }
        drawPath(ala, fieltro)
        clipPath(ala) {
            drawOval(t.b(0.22f), Offset(x - w * 0.11f, y - w * 0.036f), Size(w * 0.1f, w * 0.03f))
            drawOval(t.s(0.2f), Offset(x + w * 0.02f, y - w * 0.03f), Size(w * 0.1f, w * 0.05f))
            drawOval(t.s(0.3f), Offset(x - w * 0.05f, y - w * 0.022f), Size(w * 0.13f, w * 0.022f))
        }
        drawPath(Path().apply { moveTo(x - w * 0.1f, y - w * 0.004f); cubicTo(x - w * 0.06f, y + w * 0.01f, x + w * 0.04f, y + w * 0.012f, x + w * 0.1f, y - w * 0.008f) }, t.b(0.3f), style = Stroke(0.9f * u))
        // La copa.
        drawPath(copa, fieltro)
        clipPath(copa) {
            drawRect(t.b(0.2f), Offset(x - w * 0.064f, y - w * 0.1f), Size(w * 0.03f, w * 0.1f))
            drawRect(t.s(0.16f), Offset(x + w * 0.014f, y - w * 0.1f), Size(w * 0.06f, w * 0.1f))
            drawRect(t.s(0.2f), Offset(x + w * 0.038f, y - w * 0.1f), Size(w * 0.03f, w * 0.1f))
            // El pellizco del frente: dos hoyos a los lados de la hendidura.
            drawPath(poligono(x - w * 0.006f, y - w * 0.082f, x + w * 0.008f, y - w * 0.082f, x + w * 0.02f, y - w * 0.046f, x + w * 0.004f, y - w * 0.04f), t.s(0.24f))
            drawPath(poligono(x - w * 0.006f, y - w * 0.082f, x - w * 0.02f, y - w * 0.05f, x + w * 0.004f, y - w * 0.04f), t.b(0.18f))
            // La cinta de gro.
            val cinta = Path().apply {
                moveTo(x - w * 0.07f, y - w * 0.04f)
                cubicTo(x - w * 0.03f, y - w * 0.03f, x + w * 0.03f, y - w * 0.03f, x + w * 0.07f, y - w * 0.04f)
                lineTo(x + w * 0.07f, y); lineTo(x - w * 0.07f, y); close()
            }
            drawPath(cinta, t.c(mezcla(LACA, LACA_LUZ, 0.35f)))
            drawPath(Path().apply { moveTo(x - w * 0.07f, y - w * 0.034f); cubicTo(x - w * 0.03f, y - w * 0.024f, x + w * 0.03f, y - w * 0.024f, x + w * 0.07f, y - w * 0.034f) }, t.b(0.35f), style = Stroke(0.9f * u))
            drawRect(t.b(0.14f), Offset(x - w * 0.05f, y - w * 0.04f), Size(w * 0.016f, w * 0.04f))
        }
        // El lazo plano, a la izquierda.
        val lz = Rect(x - w * 0.056f, y - w * 0.036f, x - w * 0.034f, y - w * 0.014f)
        drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.6f)), lz.topLeft, lz.size)
        drawRect(t.b(0.3f), lz.topLeft, Size(lz.width, 0.8f * u))
        drawRect(t.c(LACA), Offset(lz.center.x - w * 0.003f, lz.top - 1f * u), Size(w * 0.006f, lz.height + 2f * u))
        filo(Rect(x - w * 0.03f, y - w * 0.093f, x + w * 0.03f, y - w * 0.04f), t)
    }

    /**
     * La maleta de cuero, de pie delante del mostrador y cortada por el borde derecho: el cuero con su desgaste, los ribetes y
     * las cantoneras, la correa con su hebilla, el asa, el cierre de latón y dos etiquetas de viaje (una redonda con un sol y
     * otra de vapor, con su casco y sus dos chimeneas).
     */
    private fun DrawScope.maleta(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        val cuero = mezcla(RAIZ_OSC, GRANATE, 0.3f)
        drawRect(t.s(0.4f), Offset(r.left - w * 0.012f, r.bottom - w * 0.006f), Size(r.width, w * 0.012f))
        // El asa, arriba.
        val asa = Path().apply { moveTo(r.left + r.width * 0.34f, r.top); cubicTo(r.left + r.width * 0.36f, r.top - w * 0.03f, r.left + r.width * 0.6f, r.top - w * 0.03f, r.left + r.width * 0.62f, r.top) }
        drawPath(asa, t.c(mezcla(cuero, LACA, 0.4f)), style = Stroke(3.4f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.3f), style = Stroke(0.8f * u))
        drawRoundRect(t.c(cuero), r.topLeft, r.size, CornerRadius(w * 0.012f))
        clipRect(r.left, r.top, r.right, r.bottom) {
            pinceladas(r, if (esNoche) NOCHE else mezcla(cuero, LACA, 0.5f), 5701, 10, u, 0.18f)
            pinceladas(r, if (esNoche) NOCHE else mezcla(cuero, RAIZ_LUZ, 0.5f), 5702, 6, u, 0.16f)
            drawRect(t.b(0.16f), r.topLeft, Size(r.width * 0.14f, r.height))
            drawRect(t.s(0.2f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
            // Los ribetes de arriba y de abajo, con su pespunte.
            listOf(r.top to r.top + r.height * 0.09f, r.bottom - r.height * 0.09f to r.bottom).forEach { (ya, yb) ->
                drawRect(t.c(mezcla(cuero, LACA, 0.35f)), Offset(r.left, ya), Size(r.width, yb - ya))
                drawLine(t.c(ESTRAZA).copy(alpha = 0.6f), Offset(r.left, (ya + yb) / 2), Offset(r.right, (ya + yb) / 2), 0.7f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * u, 2.4f * u)))
            }
            // La correa, con su hebilla.
            val cx = r.left + r.width * 0.22f
            drawRect(t.s(0.3f), Offset(cx + w * 0.022f, r.top), Size(1.6f * u, r.height))
            drawRect(t.c(mezcla(cuero, LACA, 0.45f)), Offset(cx, r.top), Size(w * 0.022f, r.height))
            drawRect(t.b(0.25f), Offset(cx, r.top), Size(1f * u, r.height))
            val hb = Rect(cx - w * 0.004f, r.top + r.height * 0.3f, cx + w * 0.026f, r.top + r.height * 0.4f)
            drawRect(t.c(ORO), hb.topLeft, hb.size, style = Stroke(1.6f * u))
            drawLine(t.c(ORO), Offset(hb.center.x, hb.top), Offset(hb.center.x, hb.bottom + 1.5f * u), 1.1f * u)
        }
        // Las cantoneras de la izquierda, con sus clavos.
        listOf(r.top, r.bottom).forEach { y ->
            val s = if (y == r.top) 1f else -1f
            drawPath(poligono(r.left, y, r.left + w * 0.04f, y, r.left + w * 0.03f, y + s * w * 0.012f, r.left + w * 0.012f, y + s * w * 0.03f, r.left, y + s * w * 0.04f), t.c(mezcla(cuero, LACA, 0.6f)))
            drawCircle(t.c(ORO), 1.3f * u, Offset(r.left + w * 0.012f, y + s * w * 0.012f))
        }
        // El cierre de latón, bajo el asa.
        val cz = Rect(r.left + r.width * 0.42f, r.top + r.height * 0.09f, r.left + r.width * 0.54f, r.top + r.height * 0.2f)
        drawRect(t.s(0.3f), Offset(cz.left + 1.2f * u, cz.top + 1.4f * u), cz.size)
        drawRect(t.c(ORO), cz.topLeft, cz.size)
        drawRect(t.b(0.5f), cz.topLeft, Size(cz.width, 1f * u))
        drawCircle(t.c(ORO_OSC), 1.4f * u, cz.center)
        // La etiqueta redonda: un sol sobre granate, con su aro de marfil.
        val e1 = Offset(r.left + r.width * 0.5f, r.top + r.height * 0.42f)
        val re = w * 0.032f
        drawCircle(t.s(0.3f), re, e1 + Offset(1.2f * u, 1.6f * u))
        drawCircle(t.c(MARFIL), re, e1)
        drawCircle(t.c(GRANATE), re * 0.84f, e1)
        for (k in 0 until 7) {
            val a = Math.toRadians(180.0 + 30.0 * k)
            drawLine(t.c(AMARILLO), Offset(e1.x + re * 0.3f * cos(a).toFloat(), e1.y + re * 0.3f + re * 0.3f * sin(a).toFloat()), Offset(e1.x + re * 0.7f * cos(a).toFloat(), e1.y + re * 0.3f + re * 0.7f * sin(a).toFloat()), 1.1f * u)
        }
        drawArc(t.c(AMARILLO), 180f, 180f, true, Offset(e1.x - re * 0.24f, e1.y + re * 0.06f), Size(re * 0.48f, re * 0.48f))
        drawRect(t.c(MARFIL), Offset(e1.x - re * 0.7f, e1.y + re * 0.3f), Size(re * 1.4f, re * 0.12f))
        // La etiqueta de vapor, torcida y con una esquina rota: el mar azul, el casco negro y las dos chimeneas granates.
        val e2 = Rect(r.left + r.width * 0.3f, r.top + r.height * 0.62f, r.left + r.width * 0.68f, r.top + r.height * 0.86f)
        rotate(-7f, e2.center) {
            val papel = poligono(e2.left, e2.top, e2.right - e2.width * 0.14f, e2.top, e2.right, e2.top + e2.height * 0.22f, e2.right, e2.bottom, e2.left, e2.bottom)
            translate(1.2f * u, 1.6f * u) { drawPath(papel, t.s(0.3f)) }
            drawPath(papel, t.c(MARFIL))
            clipPath(papel) {
                drawRect(t.c(AZUL), Offset(e2.left, e2.top + e2.height * 0.62f), Size(e2.width, e2.height * 0.38f))
                val casco = poligono(e2.left + e2.width * 0.12f, e2.top + e2.height * 0.46f, e2.right - e2.width * 0.1f, e2.top + e2.height * 0.46f, e2.right - e2.width * 0.2f, e2.top + e2.height * 0.7f, e2.left + e2.width * 0.2f, e2.top + e2.height * 0.7f)
                drawPath(casco, t.c(LACA))
                drawRect(t.c(MARFIL), Offset(e2.left + e2.width * 0.26f, e2.top + e2.height * 0.36f), Size(e2.width * 0.44f, e2.height * 0.1f))
                listOf(0.36f, 0.54f).forEach { f -> drawRect(t.c(GRANATE), Offset(e2.left + e2.width * f, e2.top + e2.height * 0.14f), Size(e2.width * 0.09f, e2.height * 0.22f)); drawRect(t.c(LACA), Offset(e2.left + e2.width * f, e2.top + e2.height * 0.14f), Size(e2.width * 0.09f, e2.height * 0.06f)) }
                drawLine(t.c(MARFIL), Offset(e2.left, e2.top + e2.height * 0.8f), Offset(e2.right, e2.top + e2.height * 0.8f), 0.8f * u)
                drawRect(t.c(GRANATE), Offset(e2.left, e2.top), Size(e2.width, e2.height * 0.07f))
            }
        }
        // Rozaduras del cuero.
        for (k in 0 until 4) {
            val p = Offset(r.left + r.width * (0.05f + 0.6f * azar(5710, k)), r.top + r.height * (0.15f + 0.7f * azar(5711, k)))
            drawLine(t.c(RAIZ_LUZ).copy(alpha = 0.4f), p, p + Offset(w * 0.02f, w * (0.01f - 0.02f * azar(5712, k))), 0.7f * u)
        }
        filo(Rect(r.left, r.top, minOf(r.right, size.width), r.bottom), t)
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = (s xor 2091) * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la lámpara.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.03f else 0f)
        val t = Tono(oscuro, k, dia)
        fondoHueco(w, h, t)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        // Lo guardado no se sale del hueco al subir.
        clipRect(0f, 0f, w, h * LABIO + 1f) {
            translate(0f, -SALIDA * h * ab) {
                when {
                    c.sinTema -> fajo(w, h, c, t)
                    else -> when (Math.floorMod(m, 3)) {
                        0 -> conLlave(w, h, c, m, t, ab)
                        1 -> conPaquete(w, h, c, m, t)
                        else -> conPeriodico(w, h, c, m, t)
                    }
                }
            }
        }
        labio(w, h, t, c.semilla)
        placaTema(w, h, t, c.sinTema)
    }

    /**
     * El fondo de un casillero: el terciopelo verde con su pelo, la sombra de la balda de arriba en escalones, el costado
     * derecho más hondo y el gancho de latón de la llave. De día, la fila de arriba un poco más clara; de noche, la lámpara
     * según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono) {
        val u = density
        drawRect(t.c(TERCIOPELO), Offset.Zero, Size(w, h))
        drawRect(t.b(0.1f), Offset.Zero, Size(w * 0.42f, h))
        drawRect(t.b(0.07f), Offset(w * 0.06f, 0f), Size(w * 0.16f, h))
        pinceladas(Rect(0f, 0f, w, h * LABIO), if (t.o) NOCHE else mezcla(TERCIOPELO, LACA, 0.5f), 61, 7, u, 0.16f)
        // La sombra de la balda de arriba, en escalones, y los costados.
        listOf(0.34f to 0.1f, 0.2f to 0.14f, 0.1f to 0.2f, 0.035f to 0.3f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        // El techo y el costado izquierdo del casillero, que se ven un poco en escorzo.
        drawPath(poligono(0f, 0f, w, 0f, w - w * 0.02f, h * 0.03f, w * 0.02f, h * 0.03f), t.c(mezcla(LACA, TERCIOPELO, 0.3f)))
        drawPath(poligono(0f, 0f, w * 0.02f, h * 0.03f, w * 0.02f, h * LABIO, 0f, h * LABIO), t.c(mezcla(LACA, TERCIOPELO, 0.45f)))
        drawPath(poligono(w, 0f, w - w * 0.02f, h * 0.03f, w - w * 0.02f, h * LABIO, w, h * LABIO), t.c(mezcla(TERCIOPELO, MARMOL_VETA, 0.12f)))
        drawRect(t.s(0.3f), Offset(w * 0.02f, h * 0.03f), Size(w * 0.96f, 1.4f * u))
        drawRect(t.s(0.1f), Offset(w * 0.88f, 0f), Size(w * 0.12f, h))
        drawRect(t.s(0.22f), Offset(w * 0.02f, h * 0.03f), Size(w * 0.03f, h))
        drawRect(t.s(0.12f), Offset(w * 0.05f, h * 0.03f), Size(w * 0.03f, h))
        gancho(Offset(w * GANCHO, h * 0.075f), t)
    }

    /** Dónde va el gancho de la llave, en fracción del ancho del casillero. */
    private const val GANCHO = 0.15f

    /** El gancho de latón atornillado al fondo: la roseta y el garfio. */
    private fun DrawScope.gancho(p: Offset, t: Tono) {
        val u = density
        drawCircle(t.s(0.4f), 3.4f * u, p + Offset(1.2f * u, 1.6f * u))
        drawCircle(t.c(ORO), 3.2f * u, p)
        drawCircle(t.c(ORO_OSC), 1.2f * u, p)
        drawPath(Path().apply { moveTo(p.x, p.y + 1f * u); cubicTo(p.x, p.y + 7f * u, p.x + 5f * u, p.y + 7f * u, p.x + 5f * u, p.y + 3.5f * u) }, t.c(ORO), style = Stroke(1.8f * u, cap = StrokeCap.Round))
        destello(p - Offset(1f * u, 1f * u), 0.9f * u, t)
    }

    /** El labio del casillero: el frente de laca de la balda, con su filete de oro arriba y su reflejo. */
    private fun DrawScope.labio(w: Float, h: Float, t: Tono, semilla: Int) {
        val u = density
        val y0 = h * LABIO
        drawRect(t.s(0.35f), Offset(0f, y0 - 1.4f * u), Size(w, 1.4f * u))
        laca(Rect(0f, y0, w, h), t, semilla, tumbada = true, color = mezcla(LACA, LACA_LUZ, 0.35f))
        drawRect(t.b(0.07f), Offset(w * 0.08f, y0), Size(w * 0.2f, h - y0))
        drawRect(t.c(ORO), Offset(0f, y0), Size(w, 1.8f * u))
        drawRect(t.b(0.6f), Offset(0f, y0), Size(w, 0.7f * u))
        drawRect(t.s(0.35f), Offset(0f, y0 + 1.8f * u), Size(w, 1.6f * u))
    }

    /**
     * La placa del tema, en el labio: de latón grabado, con las esquinas en chaflán, su filete y sus dos tornillos; la de «sin
     * tema», una tarjeta de marfil en su marco de latón. De noche, más apagada cuanto más lejos de la lámpara, pero siempre
     * legible.
     */
    private fun DrawScope.placaTema(w: Float, h: Float, t: Tono, sinTema: Boolean) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        val e = r.height * 0.17f
        val forma = poligono(r.left + e, r.top, r.right - e, r.top, r.right, r.top + e, r.right, r.bottom - e, r.right - e, r.bottom, r.left + e, r.bottom, r.left, r.bottom - e, r.left, r.top + e)
        translate(1.4f * u, 1.8f * u) { drawPath(forma, t.s(0.45f)) }
        val cerca = (1.25f * t.k.pow(0.8f)).coerceAtMost(1f)
        if (sinTema) {
            drawPath(forma, if (t.o) mezcla(mezcla(ORO_OSC, PLACA_LEJOS, 0.45f), ORO, cerca) else ORO)
            val dentro = Rect(r.left + 3.2f * u, r.top + 3.2f * u, r.right - 3.2f * u, r.bottom - 3.2f * u)
            drawRect(if (t.o) mezcla(TARJETA_LEJOS, mezcla(MARFIL, LLAMA, 0.12f), cerca) else t.c(MARFIL), dentro.topLeft, dentro.size)
            drawRect(t.s(0.2f), dentro.topLeft, Size(dentro.width, 1.6f * u))
            drawRect(t.s(0.14f), dentro.topLeft, Size(1.4f * u, dentro.height))
            drawRect(t.b(0.5f), r.topLeft + Offset(e, 0f), Size(r.width - 2 * e, 0.8f * u))
        } else {
            drawPath(forma, if (t.o) mezcla(PLACA_LEJOS, mezcla(ORO_LUZ, LLAMA, 0.16f), cerca) else t.c(mezcla(ORO, ORO_LUZ, 0.78f)))
            clipPath(forma) {
                drawRect(t.b(0.3f), r.topLeft, Size(r.width, r.height * 0.13f))
                drawRect(t.s(0.12f), Offset(r.left, r.bottom - r.height * 0.12f), Size(r.width, r.height * 0.12f))
            }
            drawPath(forma, ORO_OSC.copy(alpha = 0.75f), style = Stroke(0.9f * u))
            listOf(r.left + e * 0.62f, r.right - e * 0.62f).forEach { x ->
                drawCircle(ORO_OSC.copy(alpha = 0.9f), 1.3f * u, Offset(x, r.center.y))
                drawLine(mezcla(ORO_LUZ, MARFIL, 0.3f).copy(alpha = if (t.o) 0.35f else 0.8f), Offset(x - 0.9f * u, r.center.y - 0.5f * u), Offset(x + 0.9f * u, r.center.y + 0.5f * u), 0.5f * u)
            }
        }
    }

    /** Una hoja o un sobre de pie dentro de [r], con su sombra en el terciopelo, su canto de luz y su lado en sombra. */
    private fun DrawScope.papel(r: Rect, color: Color, t: Tono) {
        val u = density
        drawRect(t.s(0.3f), Offset(r.left + r.width * 0.035f + 1.5f * u, r.top + r.height * 0.02f + 1.5f * u), r.size)
        drawRect(t.c(color), r.topLeft, r.size)
        drawRect(t.s(0.1f), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
        drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.9f * u))
        filo(r, t)
    }

    /**
     * Una llave de hotel colgada de [c], con su sombra: el ojo en aro con su cuello, la tija y el paletón con sus dos dientes.
     * [l] es su largo.
     */
    private fun DrawScope.llave(c: Offset, l: Float, t: Tono) {
        val u = density
        val g = l * 0.055f
        fun trazo(col: Color, brillo: Boolean) {
            drawCircle(col, l * 0.13f, Offset(c.x, c.y + l * 0.14f), style = Stroke(g * 1.5f))
            drawCircle(col, l * 0.045f, Offset(c.x, c.y + l * 0.14f), style = Stroke(g * 0.6f))
            drawRect(col, Offset(c.x - g * 1.1f, c.y + l * 0.27f), Size(g * 2.2f, l * 0.05f))
            drawRect(col, Offset(c.x - g * 0.6f, c.y + l * 0.3f), Size(g * 1.2f, l * 0.7f))
            drawRect(col, Offset(c.x, c.y + l * 0.78f), Size(l * 0.15f, l * 0.07f))
            drawRect(col, Offset(c.x, c.y + l * 0.89f), Size(l * 0.11f, l * 0.08f))
            drawRect(col, Offset(c.x + l * 0.11f, c.y + l * 0.78f), Size(l * 0.04f, l * 0.15f))
            if (brillo) {
                drawRect(t.b(0.55f), Offset(c.x - g * 0.6f, c.y + l * 0.3f), Size(g * 0.4f, l * 0.7f))
                drawArc(t.b(0.6f), 150f, 110f, false, Offset(c.x - l * 0.13f, c.y + l * 0.01f), Size(l * 0.26f, l * 0.26f), style = Stroke(g * 0.5f))
                drawRect(t.s(0.25f), Offset(c.x + g * 0.2f, c.y + l * 0.3f), Size(g * 0.4f, l * 0.7f))
            }
        }
        translate(l * 0.05f, l * 0.035f) { trazo(t.s(0.38f), false) }
        trazo(t.c(ORO), true)
    }

    /**
     * La chapa ovalada de la llave, colgada de su anilla desde [c]: de fibra de [color] (o de latón), con su ojal, su reborde,
     * su brillo y el número grabado a trazos.
     */
    private fun DrawScope.chapa(c: Offset, an: Float, al: Float, color: Color, t: Tono) {
        val u = density
        val r = Rect(c.x - an / 2, c.y + al * 0.16f, c.x + an / 2, c.y + al * 1.16f)
        drawOval(t.s(0.38f), Offset(r.left + an * 0.14f, r.top + al * 0.04f), r.size)
        drawCircle(t.c(ORO), al * 0.1f, Offset(c.x, c.y + al * 0.1f), style = Stroke(1.2f * u))
        drawOval(t.c(color), r.topLeft, r.size)
        clipPath(Path().apply { addOval(r) }) {
            drawRect(t.b(0.2f), r.topLeft, Size(an * 0.34f, al))
            drawRect(t.s(0.2f), Offset(r.left + an * 0.66f, r.top), Size(an * 0.34f, al))
        }
        drawOval(t.c(mezcla(color, MARFIL, 0.45f)).copy(alpha = 0.8f), Offset(r.left + an * 0.1f, r.top + al * 0.06f), Size(an * 0.8f, al * 0.88f), style = Stroke(0.8f * u))
        drawCircle(t.c(LACA), al * 0.045f, Offset(c.x, r.top + al * 0.13f))
        // El número, a trazos.
        val oro = t.c(if (color == MARFIL) TINTA else ORO_LUZ)
        val ny = r.top + al * 0.42f
        drawLine(oro, Offset(c.x - an * 0.18f, ny), Offset(c.x - an * 0.18f, ny + al * 0.26f), 1.1f * u)
        drawArc(oro, -90f, 250f, false, Offset(c.x - an * 0.02f, ny), Size(an * 0.24f, al * 0.15f), style = Stroke(1f * u))
        drawLine(oro, Offset(c.x - an * 0.02f, ny + al * 0.26f), Offset(c.x + an * 0.22f, ny + al * 0.26f), 1f * u)
        filo(Rect(r.left + an * 0.2f, r.top, r.right - an * 0.2f, r.bottom), t)
    }

    /** Un telegrama: la hoja de [color], su membrete de [banda], la raya del título y las tiras pegadas con lo tecleado. */
    private fun DrawScope.telegrama(r: Rect, color: Color, banda: Color, semilla: Int, t: Tono) {
        val u = density
        papel(r, color, t)
        drawRect(t.c(banda), Offset(r.left, r.top + r.height * 0.05f), Size(r.width, r.height * 0.1f))
        drawRect(t.c(MARFIL).copy(alpha = 0.85f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.085f), Size(r.width * 0.4f, r.height * 0.03f))
        for (j in 0 until 4) {
            val y = r.top + r.height * (0.24f + 0.13f * j)
            val largo = r.width * (0.5f + 0.34f * azar(semilla, j + 80))
            drawRect(t.c(mezcla(color, MARFIL, 0.6f)), Offset(r.left + r.width * 0.08f, y), Size(largo, r.height * 0.075f))
            drawRect(t.s(0.12f), Offset(r.left + r.width * 0.08f, y + r.height * 0.075f), Size(largo, 0.8f * u))
            var px = r.left + r.width * 0.11f
            var q = 0
            while (px < r.left + r.width * 0.06f + largo - r.width * 0.1f) {
                val pl = r.width * (0.05f + 0.1f * azar(semilla + j, q + 90))
                drawRect(t.c(TINTA).copy(alpha = 0.6f), Offset(px, y + r.height * 0.028f), Size(pl, r.height * 0.022f))
                px += pl + r.width * 0.03f; q++
            }
        }
    }

    /**
     * El casillero con su llave: la llave colgada del gancho con su chapa ovalada (granate, verde o marfil, según la semilla;
     * se mece al abrirlo) y, apoyados a la derecha, los telegramas que van llegando: más, y más abiertos en abanico, cuanto
     * más guarda.
     */
    private fun DrawScope.conLlave(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, ab: Float) {
        val clase = Math.floorMod(m ushr 3, 3)
        val hoja = when (clase) { 0 -> mezcla(GRANATE, MARFIL, 0.8f); 1 -> mezcla(AZUL, MARFIL, 0.7f); else -> AMARILLO }
        val banda = when (clase) { 0 -> GRANATE; 1 -> mezcla(AZUL, LACA, 0.3f); else -> VERDE }
        val chapaC = when (clase) { 0 -> MARFIL; 1 -> VERDE; else -> GRANATE }
        val n = c.n.coerceIn(1, 9)
        val cuantos = (1 + (n * 2) / 3).coerceIn(1, 5)
        val pie = h * (LABIO + 0.1f)
        for (i in 0 until cuantos) {
            val f = if (cuantos == 1) 0.5f else i / (cuantos - 1f)
            val cx = w * (0.56f + (if (cuantos == 1) 0.1f else 0.24f * f))
            val an = w * 0.3f
            val arriba = h * (0.1f + 0.05f * azar(c.semilla, i) + 0.04f * (1f - f))
            val giro = (if (cuantos == 1) -4f else -9f + 15f * f) + 3f * (azar(c.semilla, i + 10) - 0.5f)
            rotate(giro, Offset(cx, pie)) {
                telegrama(Rect(cx - an / 2, arriba, cx + an / 2, pie), if (i % 2 == 0) hoja else mezcla(hoja, MARFIL, 0.45f), banda, c.semilla + i, t)
            }
        }
        // La llave y su chapa, colgadas del gancho.
        val g = Offset(w * GANCHO + 3f * density, h * 0.1f)
        rotate(-5f * ab + 2f, g) { llave(g + Offset(-w * 0.03f, 0f), h * 0.47f, t) }
        rotate(9f + 6f * ab, g) { chapa(g + Offset(w * 0.045f, h * 0.01f), w * 0.105f, h * 0.3f, chapaC, t) }
    }

    /** Un sobre de pie dentro de [r]: por delante (con su sello, su matasellos y las señas) o por detrás (con su solapa). */
    private fun DrawScope.sobre(r: Rect, color: Color, sello: Color, deFrente: Boolean, semilla: Int, t: Tono) {
        val u = density
        papel(r, color, t)
        if (deFrente) {
            val s = Rect(r.right - r.width * 0.22f, r.top + r.height * 0.1f, r.right - r.width * 0.07f, r.top + r.height * 0.36f)
            drawRect(t.c(MARFIL), Offset(s.left - 1f * u, s.top - 1f * u), Size(s.width + 2f * u, s.height + 2f * u))
            drawRect(t.c(sello), s.topLeft, s.size)
            drawOval(t.c(MARFIL).copy(alpha = 0.7f), Offset(s.left + s.width * 0.25f, s.top + s.height * 0.2f), Size(s.width * 0.5f, s.height * 0.6f))
            drawCircle(t.c(TINTA).copy(alpha = 0.45f), r.height * 0.13f, Offset(s.left - r.width * 0.06f, s.center.y + r.height * 0.03f), style = Stroke(0.8f * u))
            for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(s.left - r.width * 0.2f, s.top + s.height * (0.3f + 0.25f * j)), Offset(s.left - r.width * 0.08f, s.top + s.height * (0.3f + 0.25f * j)), 0.7f * u)
            for (j in 0 until 3) {
                val y = r.top + r.height * (0.5f + 0.1f * j)
                drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.24f, y), Offset(r.left + r.width * (0.6f + 0.14f * azar(semilla, j + 70)), y - r.height * 0.008f), 0.9f * u)
            }
        } else {
            val solapa = Path().apply { moveTo(r.left, r.top); lineTo(r.center.x, r.top + r.height * 0.42f); lineTo(r.right, r.top) }
            translate(0f, 1.2f * u) { drawPath(solapa, t.s(0.2f), style = Stroke(1f * u)) }
            drawPath(solapa, t.b(0.4f), style = Stroke(0.7f * u))
        }
    }

    /**
     * El casillero con un bulto: una sombrerera redonda (rosa palo o a rayas de marfil y negro, con su tapa, su galón y su
     * cordón) o un paquete de papel de estraza atado con bramante y su etiqueta, según la semilla; a la izquierda, bajo el
     * gancho vacío, las cartas que van llegando.
     */
    private fun DrawScope.conPaquete(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val clase = Math.floorMod(m ushr 3, 3)
        val pie = h * (LABIO + 0.1f)
        // Las cartas, a la izquierda.
        val n = c.n.coerceIn(1, 9)
        val cuantas = (1 + n / 3).coerceIn(1, 3)
        val colores = listOf(MARFIL, mezcla(AZUL, MARFIL, 0.55f), mezcla(GRANATE, MARFIL, 0.78f))
        for (i in 0 until cuantas) {
            val cx = w * (0.2f + 0.085f * i)
            val giro = -13f + 7f * i + 3f * (azar(c.semilla, i + 20) - 0.5f)
            rotate(giro, Offset(cx, pie)) {
                sobre(Rect(cx - w * 0.15f, h * (0.26f - 0.03f * i), cx + w * 0.15f, pie), colores[(i + clase) % 3], if (i % 2 == 0) GRANATE else VERDE, i == cuantas - 1, c.semilla + i, t)
            }
        }
        if (clase == 1) {
            // El paquete de estraza, atado en cruz.
            val r = Rect(w * 0.43f, h * 0.17f, w * 0.93f, pie)
            drawRect(t.s(0.32f), Offset(r.left + w * 0.03f, r.top + h * 0.03f), r.size)
            drawRect(t.c(ESTRAZA), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                pinceladas(r, if (t.o) NOCHE else mezcla(ESTRAZA, RAIZ_OSC, 0.5f), c.semilla, 5, u, 0.14f)
                caras(r, t)
                // Los dobleces del papel en las puntas.
                drawPath(poligono(r.left, r.top, r.left + r.width * 0.16f, r.top + r.height * 0.24f, r.left, r.top + r.height * 0.5f), t.s(0.12f))
                drawLine(t.s(0.25f), r.topLeft, Offset(r.left + r.width * 0.16f, r.top + r.height * 0.24f), 0.8f * u)
                drawLine(t.b(0.3f), Offset(r.left + r.width * 0.16f, r.top + r.height * 0.24f), Offset(r.left, r.top + r.height * 0.5f), 0.8f * u)
                drawLine(t.s(0.2f), Offset(r.right, r.top), Offset(r.right - r.width * 0.14f, r.top + r.height * 0.22f), 0.8f * u)
                // La etiqueta con las señas y el sello.
                val e = Rect(r.left + r.width * 0.12f, r.top + r.height * 0.4f, r.left + r.width * 0.52f, r.top + r.height * 0.7f)
                drawRect(t.s(0.2f), Offset(e.left + 1f * u, e.top + 1.4f * u), e.size)
                drawRect(t.c(MARFIL), e.topLeft, e.size)
                drawRect(t.c(GRANATE), e.topLeft, e.size, style = Stroke(0.8f * u))
                for (j in 0 until 3) drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(e.left + e.width * 0.12f, e.top + e.height * (0.3f + 0.22f * j)), Offset(e.left + e.width * (0.8f - 0.15f * j), e.top + e.height * (0.3f + 0.22f * j)), 0.9f * u)
                val s = Rect(r.right - r.width * 0.2f, r.top + r.height * 0.08f, r.right - r.width * 0.07f, r.top + r.height * 0.3f)
                drawRect(t.c(MARFIL), Offset(s.left - 1f * u, s.top - 1f * u), Size(s.width + 2f * u, s.height + 2f * u))
                drawRect(t.c(VERDE), s.topLeft, s.size)
            }
            // El bramante, con su lazada.
            val bx = r.left + r.width * 0.66f; val by = r.top + r.height * 0.3f
            val hilo = t.c(mezcla(MARFIL, ESTRAZA, 0.4f))
            drawLine(t.s(0.3f), Offset(bx + 1.4f * u, r.top), Offset(bx + 1.4f * u, r.bottom), 1.2f * u)
            drawLine(hilo, Offset(bx, r.top), Offset(bx, r.bottom), 1.5f * u)
            drawLine(t.s(0.3f), Offset(r.left, by + 1.4f * u), Offset(r.right, by + 1.4f * u), 1.2f * u)
            drawLine(hilo, Offset(r.left, by), Offset(r.right, by), 1.5f * u)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(bx, by); cubicTo(bx + s * w * 0.05f, by - h * 0.12f, bx + s * w * 0.085f, by - h * 0.03f, bx, by) }, hilo, style = Stroke(1.3f * u))
                drawLine(hilo, Offset(bx, by), Offset(bx + s * w * 0.04f, by + h * 0.1f), 1.3f * u)
            }
            drawCircle(hilo, 2f * u, Offset(bx, by))
            filo(r, t)
        } else {
            // La sombrerera: el cuerpo, la tapa vista un poco desde arriba, el galón y el cordón del asa.
            val rayada = clase == 2
            val color = if (rayada) MARFIL else mezcla(GRANATE, MARFIL, 0.6f)
            val cx = w * 0.68f; val an = w * 0.235f
            val tapaY = h * 0.2f; val tapaAl = h * 0.12f; val elip = h * 0.05f
            val cuerpo = Rect(cx - an, tapaY + tapaAl * 0.6f, cx + an, pie)
            drawRect(t.s(0.32f), Offset(cuerpo.left + w * 0.035f, tapaY + h * 0.03f), Size(cuerpo.width, pie - tapaY))
            drawRect(t.c(color), cuerpo.topLeft, cuerpo.size)
            clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
                if (rayada) for (j in 0 until 7) {
                    // Las rayas se aprietan hacia los lados: la caja es redonda.
                    val f = (j + 0.5f) / 7f
                    val sx = cx + an * sin((f - 0.5f) * 2.7f) / sin(1.35f)
                    drawRect(t.c(mezcla(LACA, LACA_LUZ, 0.4f)), Offset(sx - an * 0.05f * cos((f - 0.5f) * 2.7f), cuerpo.top), Size(an * 0.11f * cos((f - 0.5f) * 2.7f), cuerpo.height))
                }
                cilindro(cuerpo, t, 0.9f)
                drawRect(t.s(0.22f), cuerpo.topLeft, Size(cuerpo.width, tapaAl * 0.7f))
            }
            val tapa = Rect(cx - an * 1.05f, tapaY, cx + an * 1.05f, tapaY + tapaAl)
            drawOval(t.c(mezcla(color, MARFIL, 0.35f)), Offset(tapa.left, tapaY - elip), Size(tapa.width, elip * 2))
            drawOval(t.b(0.25f), Offset(tapa.left + tapa.width * 0.08f, tapaY - elip * 0.8f), Size(tapa.width * 0.5f, elip * 1.2f))
            val faldon = Path().apply {
                moveTo(tapa.left, tapaY); lineTo(tapa.left, tapa.bottom)
                cubicTo(tapa.left + tapa.width * 0.25f, tapa.bottom + elip * 0.9f, tapa.right - tapa.width * 0.25f, tapa.bottom + elip * 0.9f, tapa.right, tapa.bottom)
                lineTo(tapa.right, tapaY)
                cubicTo(tapa.right - tapa.width * 0.25f, tapaY + elip * 1.3f, tapa.left + tapa.width * 0.25f, tapaY + elip * 1.3f, tapa.left, tapaY)
                close()
            }
            drawPath(faldon, t.c(if (rayada) mezcla(LACA, LACA_LUZ, 0.4f) else mezcla(color, GRANATE, 0.3f)))
            clipPath(faldon) { cilindro(Rect(tapa.left, tapaY, tapa.right, tapa.bottom + elip), t, 1f) }
            // El galón dorado y el cordón que cruza la tapa y cae por los lados.
            drawPath(Path().apply { moveTo(tapa.left, tapa.bottom - tapaAl * 0.3f); cubicTo(tapa.left + tapa.width * 0.25f, tapa.bottom + elip * 0.5f, tapa.right - tapa.width * 0.25f, tapa.bottom + elip * 0.5f, tapa.right, tapa.bottom - tapaAl * 0.3f) }, t.c(ORO), style = Stroke(1.3f * u))
            val cordon = t.c(if (rayada) GRANATE else mezcla(LACA, LACA_LUZ, 0.5f))
            drawPath(Path().apply { moveTo(cx - an * 0.72f, tapaY + elip * 0.5f); cubicTo(cx - an * 0.4f, tapaY - h * 0.2f, cx + an * 0.4f, tapaY - h * 0.2f, cx + an * 0.72f, tapaY + elip * 0.5f) }, cordon, style = Stroke(2f * u, cap = StrokeCap.Round))
            listOf(-0.72f, 0.72f).forEach { f ->
                drawLine(cordon, Offset(cx + an * f, tapaY + elip * 0.5f), Offset(cx + an * f, pie), 1.8f * u)
                drawCircle(t.c(ORO), 2.2f * u, Offset(cx + an * f, tapaY + elip * 0.7f))
            }
            filo(Rect(tapa.left + tapa.width * 0.12f, tapaY - elip, tapa.right - tapa.width * 0.12f, tapa.bottom), t)
        }
    }

    /**
     * El casillero con el periódico doblado (el lomo del doblez, la cabecera, los titulares, las columnas y un grabado) y,
     * apoyado a la derecha, un sobre de correo aéreo con su borde de listas granates y azules; detrás, más sobres cuanto más
     * guarda.
     */
    private fun DrawScope.conPeriodico(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono) {
        val u = density
        val clase = Math.floorMod(m ushr 3, 3)
        val pie = h * (LABIO + 0.1f)
        val n = c.n.coerceIn(1, 9)
        // Los sobres de detrás.
        val detras = (n / 2).coerceIn(0, 3)
        val colores = listOf(AMARILLO, mezcla(GRANATE, MARFIL, 0.75f), mezcla(VERDE, MARFIL, 0.7f))
        for (i in 0 until detras) {
            val cx = w * (0.9f - 0.1f * i)
            rotate(12f - 5f * i, Offset(cx, pie)) { sobre(Rect(cx - w * 0.15f, h * (0.12f + 0.03f * i), cx + w * 0.15f, pie), colores[(i + clase) % 3], VERDE, false, c.semilla + i, t) }
        }
        // El periódico, doblado y apoyado a la izquierda.
        val papelD = when (clase) { 0 -> mezcla(DIARIO, AMARILLO, 0.2f); 1 -> mezcla(DIARIO, GRANATE, 0.12f); else -> DIARIO }
        val r = Rect(w * 0.07f, h * 0.13f, w * 0.52f, pie)
        rotate(-5f, Offset(r.left, pie)) {
            // Las hojas de dentro, que asoman por arriba y por la derecha.
            drawRect(t.s(0.3f), Offset(r.left + w * 0.03f, r.top + h * 0.03f), r.size)
            drawRect(t.c(mezcla(papelD, LACA, 0.12f)), Offset(r.left + w * 0.012f, r.top - h * 0.012f), r.size)
            drawRect(t.c(papelD), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                // La cabecera.
                drawRect(t.c(TINTA).copy(alpha = 0.85f), Offset(r.left + r.width * 0.14f, r.top + r.height * 0.06f), Size(r.width * 0.72f, r.height * 0.075f))
                drawLine(t.c(TINTA).copy(alpha = 0.7f), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.17f), Offset(r.right - r.width * 0.06f, r.top + r.height * 0.17f), 0.8f * u)
                drawLine(t.c(TINTA).copy(alpha = 0.7f), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.19f), Offset(r.right - r.width * 0.06f, r.top + r.height * 0.19f), 0.5f * u)
                // El titular y el grabado.
                drawRect(t.c(TINTA).copy(alpha = 0.7f), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.235f), Size(r.width * 0.6f, r.height * 0.04f))
                drawRect(t.c(TINTA).copy(alpha = 0.55f), Offset(r.left + r.width * 0.06f, r.top + r.height * 0.3f), Size(r.width * 0.42f, r.height * 0.03f))
                val g = Rect(r.left + r.width * 0.58f, r.top + r.height * 0.3f, r.right - r.width * 0.06f, r.top + r.height * 0.52f)
                drawRect(t.c(mezcla(papelD, TINTA, 0.55f)), g.topLeft, g.size)
                drawOval(t.c(mezcla(papelD, TINTA, 0.2f)), Offset(g.left + g.width * 0.3f, g.top + g.height * 0.15f), Size(g.width * 0.4f, g.height * 0.5f))
                // Las columnas.
                for (col in 0 until 3) {
                    val x0 = r.left + r.width * (0.06f + 0.31f * col)
                    var y = r.top + r.height * (if (col == 2) 0.57f else 0.37f)
                    var j = 0
                    while (y < r.bottom - h * 0.02f) {
                        drawLine(t.c(TINTA).copy(alpha = 0.38f), Offset(x0, y), Offset(x0 + r.width * (0.2f + 0.07f * azar(c.semilla + col, j)), y), 0.7f * u)
                        y += r.height * 0.034f; j++
                    }
                }
                drawRect(t.s(0.1f), Offset(r.left + r.width * 0.8f, r.top), Size(r.width * 0.2f, r.height))
                // El lomo del doblez, a la izquierda.
                drawRect(t.b(0.3f), r.topLeft, Size(r.width * 0.035f, r.height))
                drawRect(t.s(0.14f), Offset(r.left + r.width * 0.035f, r.top), Size(r.width * 0.02f, r.height))
            }
            drawRect(t.b(0.45f), r.topLeft, Size(r.width, 0.9f * u))
            filo(r, t)
        }
        // El sobre de correo aéreo, delante.
        val s = Rect(w * 0.5f, h * 0.26f, w * 0.9f, pie)
        rotate(8f, Offset(s.center.x, pie)) {
            val color = when (clase) { 0 -> MARFIL; 1 -> mezcla(AZUL, MARFIL, 0.72f); else -> mezcla(MARFIL, CREMA, 0.5f) }
            sobre(s, color, if (clase == 1) GRANATE else AZUL, true, c.semilla, t)
            // El borde de listas inclinadas, alternando granate y azul.
            clipRect(s.left, s.top, s.right, s.bottom) {
                val paso = s.width / 9f
                val gr = s.height * 0.07f
                for (j in -1 until 10) {
                    val x0 = s.left + paso * j
                    drawPath(poligono(x0, s.top + gr, x0 + paso * 0.5f, s.top, x0 + paso, s.top, x0 + paso * 0.5f, s.top + gr), t.c(if (Math.floorMod(j, 2) == 0) GRANATE else AZUL))
                }
                val pasoV = s.height / 7f
                for (j in 0 until 8) {
                    val y0 = s.top + gr + pasoV * j
                    drawPath(poligono(s.left, y0, s.left + gr, y0 - pasoV * 0.5f, s.left + gr, y0, s.left, y0 + pasoV * 0.5f), t.c(if (j % 2 == 0) AZUL else GRANATE))
                    drawPath(poligono(s.right - gr, y0, s.right, y0 - pasoV * 0.5f, s.right, y0, s.right - gr, y0 + pasoV * 0.5f), t.c(if (j % 2 == 0) GRANATE else AZUL))
                }
            }
            filo(s, t)
        }
    }

    /**
     * Lo que no tiene tema: un fajo de correo sin clasificar (sobres de varios tamaños y colores, que asoman unos tras otros),
     * atado en cruz con cordel y con su lazada; más sobres cuanto más guarda.
     */
    private fun DrawScope.fajo(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val pie = h * (LABIO + 0.1f)
        val n = c.n.coerceIn(1, 9)
        val cuantos = (2 + n / 2).coerceIn(2, 5)
        val colores = listOf(mezcla(AZUL, MARFIL, 0.6f), AMARILLO, mezcla(GRANATE, MARFIL, 0.75f), ESTRAZA, MARFIL)
        val cx = w * 0.52f
        rotate(-4f, Offset(cx, pie)) {
            for (i in cuantos - 1 downTo 1) {
                val an = w * (0.27f + 0.04f * azar(c.semilla, i))
                val dx = w * (0.035f * i) * (if (i % 2 == 0) 1f else -0.8f)
                sobre(Rect(cx - an + dx, h * (0.2f - 0.035f * i), cx + an + dx, pie), colores[i % colores.size], VERDE, false, c.semilla + i, t)
            }
            val r = Rect(cx - w * 0.3f, h * 0.2f, cx + w * 0.3f, pie)
            sobre(r, MARFIL, GRANATE, true, c.semilla, t)
            // El cordel en cruz, con su lazada.
            val hilo = t.c(mezcla(ESTRAZA, RAIZ_OSC, 0.3f))
            val bx = cx - w * 0.06f; val by = r.top + (pie - r.top) * 0.38f
            drawLine(t.s(0.3f), Offset(bx + 1.4f * u, h * 0.06f), Offset(bx + 1.4f * u, pie), 1.2f * u)
            drawLine(hilo, Offset(bx, h * 0.06f), Offset(bx, pie), 1.6f * u)
            drawLine(t.s(0.3f), Offset(cx - w * 0.36f, by + 1.4f * u), Offset(cx + w * 0.36f, by + 1.4f * u), 1.2f * u)
            drawLine(hilo, Offset(cx - w * 0.36f, by), Offset(cx + w * 0.36f, by), 1.6f * u)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply { moveTo(bx, by); cubicTo(bx + s * w * 0.06f, by - h * 0.13f, bx + s * w * 0.1f, by - h * 0.02f, bx, by) }, hilo, style = Stroke(1.4f * u))
                drawLine(hilo, Offset(bx, by), Offset(bx + s * w * 0.045f, by + h * 0.11f), 1.4f * u)
            }
            drawCircle(hilo, 2.2f * u, Offset(bx, by))
        }
    }

    /**
     * Un hueco libre: el gancho sin llave, una tarjeta de visita olvidada contra el fondo y, en el labio, la placa esmaltada
     * del número.
     */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val pie = h * (LABIO + 0.08f)
        val cx = w * (0.56f + 0.2f * azar(c.semilla, 1))
        rotate(-14f + 30f * azar(c.semilla, 2), Offset(cx, pie)) {
            val r = Rect(cx - w * 0.1f, h * 0.36f, cx + w * 0.1f, pie)
            papel(r, MARFIL, t)
            drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.3f), Offset(r.right - r.width * 0.2f, r.top + r.height * 0.3f), 1f * u)
            drawLine(t.c(TINTA).copy(alpha = 0.4f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.48f), Offset(r.right - r.width * 0.3f, r.top + r.height * 0.48f), 0.7f * u)
        }
        labio(w, h, t, c.semilla)
        // La placa del número: un óvalo de esmalte con su aro de latón.
        val p = Rect(w * 0.39f, h * 0.71f, w * 0.61f, h * 0.91f)
        drawOval(t.s(0.4f), Offset(p.left + 1.4f * u, p.top + 1.8f * u), p.size)
        drawOval(t.c(ORO), p.topLeft, p.size)
        drawOval(t.c(MARFIL), Offset(p.left + 2.2f * u, p.top + 2.2f * u), Size(p.width - 4.4f * u, p.height - 4.4f * u))
        drawArc(t.b(0.5f), 180f, 90f, false, p.topLeft, p.size, style = Stroke(0.9f * u))
        val tinta = t.c(TINTA).copy(alpha = 0.8f)
        val ny = p.top + p.height * 0.3f; val nh = p.height * 0.4f
        drawLine(tinta, Offset(p.center.x - p.width * 0.14f, ny), Offset(p.center.x - p.width * 0.14f, ny + nh), 1.3f * u)
        drawOval(tinta, Offset(p.center.x - p.width * 0.03f, ny), Size(p.width * 0.17f, nh), style = Stroke(1.2f * u))
        listOf(p.left - w * 0.03f, p.right + w * 0.03f).forEach { x -> drawPath(poligono(x, p.center.y - 2.4f * u, x + 2.4f * u, p.center.y, x, p.center.y + 2.4f * u, x - 2.4f * u, p.center.y), t.c(ORO)) }
    }

    override val rotulo = Rect(0.05f, 0.695f, 0.95f, 0.958f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.pasaje
    override val salida = 0f
    override val placa = Rect(0.3f, 0.668f, 0.64f, 0.812f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Laca negra con un sol de rayos de oro que sale de abajo, y un doble filete.
        drawRect(t(LACA, oscuro))
        val o = Offset(w / 2, h * 1.05f)
        for (k in 0 until 36) {
            val a = Math.toRadians(180.0 + k * 5.0)
            drawLine(ORO.copy(alpha = if (k % 2 == 0) 0.1f else 0.05f), o, Offset(o.x + h * 1.4f * cos(a).toFloat(), o.y + h * 1.4f * sin(a).toFloat()), (if (k % 2 == 0) 3f else 1f) * u)
        }
        val pl = 14 * u
        drawRect(t(ORO, oscuro), Offset(pl * 0.5f, pl * 0.5f), Size(w - pl, h - pl), style = Stroke(1.6f * u))
        drawRect(t(ORO, oscuro).copy(alpha = 0.5f), Offset(pl, pl), Size(w - 2 * pl, h - 2 * pl), style = Stroke(0.8f * u))
        escuadrasPared(w, h, pl * 0.5f, 34 * u, t(ORO, oscuro))
    }

    override fun tintaInterior(oscuro: Boolean) = CREMA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(TELEGRAMA.copy(alpha = 0.5f))
        // La cabecera verde y oro, y las esquinas escalonadas.
        drawRect(VERDE.copy(alpha = 0.8f), Offset.Zero, Size(w, 3 * u * g))
        drawRect(ORO, Offset(0f, 3 * u * g), Size(w, 1.2f * u * g))
        val e = 9 * u * g; val m = 4 * u * g
        listOf(Offset(m, m + 4 * u * g) to Offset(1f, 1f), Offset(w - m, m + 4 * u * g) to Offset(-1f, 1f), Offset(m, h - m) to Offset(1f, -1f), Offset(w - m, h - m) to Offset(-1f, -1f)).forEach { (o, s) ->
            val p = Path().apply {
                moveTo(o.x + s.x * e, o.y); lineTo(o.x, o.y); lineTo(o.x, o.y + s.y * e)
                moveTo(o.x + s.x * e * 0.6f, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * 2.5f * u * g); lineTo(o.x + s.x * 2.5f * u * g, o.y + s.y * e * 0.6f)
            }
            drawPath(p, ORO_OSC.copy(alpha = 0.7f), style = Stroke(0.9f * u * g))
        }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 14.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.92f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Un rombo de oro macizo.
            "hecho" -> {
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.46f); lineTo(c.x + s * 0.34f, c.y); lineTo(c.x, c.y + s * 0.46f); lineTo(c.x - s * 0.34f, c.y); close() }, ORO)
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.24f); lineTo(c.x + s * 0.18f, c.y); lineTo(c.x, c.y + s * 0.24f); lineTo(c.x - s * 0.18f, c.y); close() }, ORO_OSC, style = Stroke(s * 0.05f))
            }
            "convence" -> abanico(Offset(c.x, c.y + s * 0.26f), s * 0.48f, ORO, ORO_OSC)
            "piensa" -> listOf(-0.16f, 0.12f).forEach { dy -> drawPath(Path().apply { moveTo(c.x - s * 0.36f, c.y + dy * s - s * 0.12f); lineTo(c.x, c.y + dy * s + s * 0.14f); lineTo(c.x + s * 0.36f, c.y + dy * s - s * 0.12f) }, VERDE, style = Stroke(s * 0.1f)) }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** La llave de la habitación, con su placa granate. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        llaveChica(Offset(c.x - s * 0.1f, c.y - s * 0.5f), s * 0.9f, ORO_OSC)
        drawRoundRect(GRANATE, Offset(c.x + s * 0.04f, c.y - s * 0.12f), Size(s * 0.3f, s * 0.4f), CornerRadius(s * 0.12f))
    }

    override val desorden = 1.5f
}
