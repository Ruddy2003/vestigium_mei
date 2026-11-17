package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
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
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Pirata
// =====================================================================

/**
 * Pirata: el rincón de Tomás, el grumete, en la bodega de un balandro del Caribe hacia 1718. Es el costado del casco por
 * dentro, sin portas ni mar a la vista: el forro de tracas de pino embreado con su comba, las costuras de brea con su hilo de
 * estopa, los topes de las tablas y los clavos de hierro con su chorrera de óxido, manchas de sal abajo; arriba, la cubierta
 * vista por debajo, los baos que vienen hacia quien mira y el durmiente con sus pernos. Tres cuadernas de roble (las
 * costillas, con su codo arriba) bajan hasta el plan y sostienen la estiba: baldas de tablón con su barrote de mar en
 * candeleros, trincadas a cada cuaderna con vueltas de cabo, su nudo y su chicote; la segunda balda lleva un remiendo de
 * pino nuevo. De día la luz baja por la escotilla, fuera del cuadro arriba a la izquierda: un haz con polvo y, sobre el
 * forro y el coy, la cuadrícula de luz del enjaretado. El punto focal es el coy recogido, la hamaca de lona enrollada y
 * trincada, colgada de dos ganchos con sus arraigados en abanico y el borde de una manta roja; a su lado, colgados, el farol
 * de sebo con sus cristales de asta (ladeado por el balanceo) y una adujada en su cabilla. Sobre la balda alta: la jarra de
 * peltre, una carta enrollada con el compás de puntas encima, la botella de vidrio verde con su corcho, el octante de madera
 * apoyado en el forro, el cuchillo clavado en la tabla y el catalejo tumbado. Al pie, sobre el plan: el rollo de lona
 * (cortado por la izquierda), las balas en su chillera, un cubo y dos barriles, uno con su grifo y un charco y otro cortado
 * por el borde derecho. Cada tema es, según la semilla, un cofre de marino pintado (verde, rojo o azul gastado), más ancho
 * abajo, con cantoneras de latón, gazas de cabo y candado; un barrilete abierto con sus aros y la tapa apoyada detrás, o un
 * fardo de lona encerada cosido y atado con cabo; el tema va con tiza en una pizarrita clavada o colgada, o pintado en una
 * tablilla embreada, y asoman cartas enrolladas (más con más fichas) y alguna moneda. Lo que no tiene tema es un cajón de
 * fruta con los pliegos sueltos y la pizarrita sin escribir; un hueco libre lleva un cabo suelto y una caracola o una
 * botella volcada. Al abrir uno, lo guardado sube por detrás del barrote. De noche todo se hunde en azul negro de brea:
 * alumbra solo el farol, con su luz ladeada, las sombras de las cuadernas alargadas sobre el forro, brillos en el latón y en
 * el vidrio; la primera fila encendida, la segunda a medias y lo de abajo casi perdido, con un reflejo en el charco. Por
 * dentro, pino embreado con un barco pintado en la tapa. Las fichas son cartas de marear con sus rumbos.
 */
object IdeasPirata : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la bodega (sale de la del cuadro)
    /** El pino embreado del forro, en su tono medio y en sombra. */
    private val CASCO = Color(0xFF7A5A3A)
    private val CASCO_OSC = Color(0xFF4E3826)
    /** El roble de las cuadernas, los baos y las baldas, gastado. */
    private val ROBLE = Color(0xFF8E6A44)
    private val ROBLE_LUZ = Color(0xFFB08A58)
    /** La brea de las costuras y la estopa del calafateo. */
    private val ALQUITRAN = Color(0xFF2E2622)
    private val ESTOPA = Color(0xFFB59C70)
    /** La lona del coy, del fardo y del rollo, y el cáñamo de los cabos. */
    private val LONA = Color(0xFFE9DEC2)
    private val LONA_OSC = Color(0xFFB9A886)
    private val CABO = Color(0xFFCDB47C)
    private val CABO_OSC = Color(0xFF8E7444)
    /** El latón de las cantoneras y el catalejo, el peltre, el hierro y su óxido. */
    private val BRONCE = Color(0xFFDAB556)
    private val PELTRE = Color(0xFF9EA09A)
    private val HIERRO = Color(0xFF4C4E58)
    private val OXIDO = Color(0xFF9A5A2E)
    /** El vidrio verde botella y las pinturas gastadas de los cofres: rojo, azul y verde. */
    private val BOTELLA = Color(0xFF3F6E48)
    private val BERMELLON = Color(0xFFA6402E)
    private val AZUL = Color(0xFF3E5A6A)
    private val VERDE = Color(0xFF4A6E52)
    /** La pizarra, el oro de las monedas y la llama de sebo del farol. */
    private val PIZARRA = Color(0xFF2C3033)
    private val ORO = Color(0xFFE0B848)
    private val LLAMA = Color(0xFFFFB860)
    /** La noche de la bodega: azul negro con brea. */
    private val NOCHE = Color(0xFF0B1426)

    /** Los del interior y las fichas (sin cambios). */
    private val PINO = Color(0xFF7A5634)
    private val PINO_OSC = Color(0xFF4A321C)
    private val BREA = Color(0xFF1E1610)
    private val LATON = Color(0xFFC09A48)
    private val VERDE_OSC = Color(0xFF1C3026)
    private val TIZA = Color(0xFFEDEAE0)
    private val PERGAMINO = Color(0xFFE6D6AE)
    private val ROJO = Color(0xFF9A2A1C)
    private val RUMBO = Color(0xFF2E6A4A)
    private val TINTA = Color(0xFF2A1C0F)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.32f
    override val corona = 0.6f
    override val base = 0.3f
    override val lado = 0.06f
    override val hueco = 0.05f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    private fun anchoH() = (1f - 2f * lado - hueco * (cols - 1)) / cols
    private fun altoH() = anchoH() / proporcion

    /** En fracciones del ancho: lo bajo del techo, del durmiente, el fondo y el frente de la balda alta. */
    private const val TECHO = 0.04f
    private const val DURMIENTE = 0.085f
    private const val FONDO_T = 0.568f
    private const val TABLA = 0.586f
    private const val CANTO_T = 0.612f
    /** El grueso de las baldas, en fracciones del ancho. */
    private const val BALDA = 0.026f
    /** En la base, desde su borde de arriba: dónde empieza el plan (el piso), en fracciones del ancho. */
    private const val PLAN = 0.06f
    /** Las tracas del forro: dónde empieza la primera, su alto y cuánto suben en los extremos (la comba). */
    private const val TRACA0 = 0.03f
    private const val TRACA = 0.052f
    private const val COMBA = 0.016f
    /** Dentro de un hueco, en fracciones de su alto: la balda, el barrote de mar y cuánto sube lo guardado al abrirlo. */
    private const val PISO = 0.93f
    private const val BARR = 0.855f
    private const val BARR_G = 0.036f
    private const val SALIDA = 0.07f

    /** Las cuadernas: su centro y su medio ancho, en fracciones del ancho. */
    private val CUADERNAS = listOf(0.0375f to 0.0225f, 0.5f to 0.025f, 0.9625f to 0.0225f)
    /** Los baos de la cubierta, en fracciones del ancho. */
    private val BAOS = listOf(0.0375f, 0.27f, 0.5f, 0.73f, 0.9625f)

    // ---------------------------------------------------------------- la luz y la noche

    /** El gancho del farol y cuánto lo ladea el balanceo (grados, hacia la izquierda). */
    private val GANCHO = Offset(0.73f, DURMIENTE)
    private const val GIRO = 9f
    /** La llama del farol, ya ladeado, en fracciones del ancho. */
    private val LUZ = Offset(0.73f - 0.155f * sin(Math.toRadians(GIRO.toDouble())).toFloat(), DURMIENTE + 0.155f * cos(Math.toRadians(GIRO.toDouble())).toFloat())

    /** Cuánto le llega del farol a un punto [p] (en fracciones del ancho): cae con la distancia; abajo, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.6f * exp(-3.8f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que el farol echa hacia abajo sobre la estiba: cae de fila en fila (la primera, encendida; la segunda, a medias;
     * la tercera, tenue; la cuarta y la quinta, casi perdidas), algo menos en la columna de la izquierda. Los puntos son los
     * centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.62f to 0.9f, 0.81f to 0.8f, 1.17f to 0.46f, 1.54f to 0.2f, 1.9f to 0.07f, 2.27f to 0.04f, 2.7f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.6f * (abs(p.x - LUZ.x) - 0.06f).coerceAtLeast(0f)).coerceIn(0.65f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.11f + NOCHE.red * 0.85f, c.green * 0.11f + NOCHE.green * 0.85f, c.blue * 0.11f + NOCHE.blue * 0.85f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia] más claro, o más hondo si es negativo). De noche, de su
     * color hondo hacia el suyo según [k] (lo que le llega del farol), teñido de llama.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else if (dia < 0f) mezcla(col, SOMBRA_MUEBLE, -dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche de la bodega. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

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

    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    /** La sombra de contacto de lo que pisa una tabla o el plan, corrida a la derecha. */
    private fun DrawScope.contacto(t: Tono, centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(t.s(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /**
     * La sombra de algo del mueble en el forro: de día corrida a la derecha y abajo; de noche, lejos del farol y tanto más
     * marcada cuanto más cerca está de él. No baja de [hasta].
     */
    private fun DrawScope.sombraEnForro(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.016f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.1f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    val dy = ((desde.y / w - LUZ.y) / 0.3f).coerceIn(-0.4f, 1f)
                    translate(w * 0.04f * dx, w * (0.01f + 0.02f * dy)) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

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
        drawRect(t.s(0.22f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El brillo del farol en el latón o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un clavo de hierro, con su chorrera de óxido si [oxido]. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, oxido: Boolean = false, largo: Float = 0f) {
        if (oxido && largo > 0f) {
            drawLine(t.c(OXIDO).copy(alpha = 0.3f), p, p + Offset(r * 0.3f, largo), r * 1.5f, StrokeCap.Round)
            drawLine(t.c(OXIDO).copy(alpha = 0.4f), p, p + Offset(r * 0.2f, largo * 0.55f), r * 0.8f, StrokeCap.Round)
        }
        drawCircle(t.s(0.3f), r * 1.05f, p + Offset(r * 0.45f, r * 0.45f))
        drawCircle(t.c(mezcla(HIERRO, OXIDO, 0.35f)), r, p)
        drawCircle(t.b(0.45f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Un cabo por [p]: el cuerpo, el lado de luz y la colcha (el torcido) en rayitas. */
    private fun DrawScope.cabo(p: Path, g: Float, t: Tono, color: Color = CABO) {
        drawPath(p, t.c(mezcla(color, CABO_OSC, 0.75f)), style = Stroke(g, cap = StrokeCap.Round))
        drawPath(p, t.c(color), style = Stroke(g * 0.6f, cap = StrokeCap.Round))
        drawPath(p, t.c(CABO_OSC).copy(alpha = 0.7f), style = Stroke(g * 0.75f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 0.35f, g * 0.75f))))
    }

    /** Una moneda de plata u oro tumbada o de canto: el disco, su canto y el brillo. */
    private fun DrawScope.moneda(c: Offset, r: Float, t: Tono, plata: Boolean = false) {
        val col = if (plata) mezcla(PELTRE, LONA, 0.35f) else ORO
        drawOval(t.s(0.3f), Offset(c.x - r + r * 0.3f, c.y - r * 0.45f + r * 0.35f), Size(r * 2, r * 0.9f))
        drawOval(t.c(mezcla(col, OXIDO, 0.35f)), Offset(c.x - r, c.y - r * 0.4f), Size(r * 2, r * 0.9f))
        drawOval(t.c(col), Offset(c.x - r, c.y - r * 0.5f), Size(r * 2, r * 0.85f))
        drawOval(t.c(mezcla(col, OXIDO, 0.25f)), Offset(c.x - r * 0.6f, c.y - r * 0.3f), Size(r * 1.2f, r * 0.45f), style = Stroke(r * 0.15f))
        destello(Offset(c.x - r * 0.45f, c.y - r * 0.25f), r * 0.22f, t)
    }

    /**
     * Una carta de marear enrollada, de [a] (el pie, escondido) a [b] (la punta): el rollo con su luz, el canto con la espiral
     * y una cinta. [g] es el grueso.
     */
    private fun DrawScope.rolloCarta(a: Offset, b: Offset, g: Float, t: Tono, cinta: Color = BERMELLON, papel: Color = PERGAMINO) {
        val u = density
        val d = b - a
        val l = d.getDistance().coerceAtLeast(1f)
        val ang = Math.toDegrees(kotlin.math.atan2(d.y.toDouble(), d.x.toDouble())).toFloat()
        rotate(ang, a) {
            val r = Rect(a.x, a.y - g / 2, a.x + l, a.y + g / 2)
            drawRect(t.s(0.25f), Offset(r.left + 1.5f * u, r.top + 1.8f * u), r.size)
            drawRect(t.c(papel), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { tumbado(r, t, 1.1f) }
            // La espiral del canto.
            val e = Rect(r.right - g * 0.28f, r.top, r.right + g * 0.28f, r.bottom)
            drawOval(t.c(mezcla(papel, LONA_OSC, 0.45f)), e.topLeft, e.size)
            drawArc(t.s(0.45f), 200f, 260f, false, Offset(e.left + e.width * 0.2f, e.top + e.height * 0.2f), Size(e.width * 0.6f, e.height * 0.6f), style = Stroke(0.6f * u))
            drawOval(t.s(0.35f), e.topLeft, e.size, style = Stroke(0.6f * u))
            // La cinta.
            val cx = r.left + l * 0.55f
            drawRect(t.c(cinta), Offset(cx, r.top - 0.4f * u), Size(g * 0.22f, g + 0.8f * u))
            drawRect(t.s(0.25f), Offset(cx + g * 0.14f, r.top), Size(g * 0.08f, g))
            // Un rumbo que asoma, en tinta corrida.
            drawLine(t.c(mezcla(papel, TINTA, 0.45f)).copy(alpha = 0.45f), Offset(r.left + l * 0.7f, r.top + g * 0.2f), Offset(r.left + l * 0.9f, r.top + g * 0.75f), 0.5f * u)
        }
    }

    // ---------------------------------------------------------------- el forro del casco (lo usan el mueble y los huecos)

    /** El canto de arriba de la traca [k] en [gx] (fracciones del ancho del mueble): sube un poco hacia los extremos. */
    private fun yTraca(k: Int, gx: Float): Float { val e = 2f * gx - 1f; return TRACA0 + k * TRACA - COMBA * e * e }

    /**
     * El forro: tracas de pino embreado, cada una de su tono, con la costura de brea y su hilo de estopa, el canto de luz, los
     * topes de las tablas y los clavos con su chorrera de óxido. Se pinta en [r] (en píxeles del lienzo de ahora); [W] es el
     * ancho del mueble y ([ox], [oy]) dónde cae este lienzo dentro de él, para que las tracas casen de un hueco a otro.
     * [trozos] es en cuántos tramos se parte cada traca para que la luz cambie a lo ancho.
     */
    private fun DrawScope.forro(W: Float, ox: Float, oy: Float, r: Rect, trozos: Int, tono: (Offset) -> Tono) {
        val u = density
        val gTop = (oy + r.top) / W; val gBot = (oy + r.bottom) / W
        val k0 = floor((gTop - TRACA0) / TRACA).toInt() - 1
        val k1 = ceil((gBot - TRACA0 + COMBA) / TRACA).toInt()
        val gx0 = (ox + r.left) / W; val gx1 = (ox + r.right) / W
        fun px(gx: Float) = gx * W - ox
        fun py(gy: Float) = gy * W - oy
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in k0..k1) {
                val tabla = mezcla(CASCO_OSC, CASCO, 0.2f + 0.7f * azar(5101, k + 400))
                for (j in 0 until trozos) {
                    val a = gx0 + (gx1 - gx0) * j / trozos
                    val b = gx0 + (gx1 - gx0) * (j + 1) / trozos + 1f / W
                    val m = (a + b) / 2
                    val t = tono(Offset(m, yTraca(k, m) + TRACA / 2))
                    drawPath(Path().apply {
                        moveTo(px(a), py(yTraca(k, a))); lineTo(px(m), py(yTraca(k, m))); lineTo(px(b), py(yTraca(k, b)))
                        lineTo(px(b), py(yTraca(k + 1, b))); lineTo(px(m), py(yTraca(k + 1, m))); lineTo(px(a), py(yTraca(k + 1, a))); close()
                    }, t.c(tabla))
                    // La costura de arriba: la sombra de la traca de encima, la brea, la estopa y el canto con luz.
                    val costura = Path().apply { moveTo(px(a), py(yTraca(k, a))); lineTo(px(m), py(yTraca(k, m))); lineTo(px(b), py(yTraca(k, b))) }
                    translate(0f, -1.6f * u) { drawPath(costura, t.s(0.14f), style = Stroke(1.6f * u)) }
                    drawPath(costura, t.c(ALQUITRAN).copy(alpha = 0.85f), style = Stroke(1.7f * u))
                    translate(0f, 1.1f * u) { drawPath(costura, t.c(ESTOPA).copy(alpha = 0.3f), style = Stroke(0.6f * u)) }
                    translate(0f, 2.2f * u) { drawPath(costura, t.b(0.16f), style = Stroke(0.9f * u)) }
                    // La panza de la tabla, un poco más honda abajo.
                    val panza = Path().apply { moveTo(px(a), py(yTraca(k + 1, a)) - 3f * u); lineTo(px(m), py(yTraca(k + 1, m)) - 3f * u); lineTo(px(b), py(yTraca(k + 1, b)) - 3f * u) }
                    drawPath(panza, t.s(0.08f), style = Stroke(4f * u))
                }
                // Los topes de las tablas, con sus clavos; y algún clavo suelto con su chorrera.
                for (j in 0 until 2) {
                    val gx = (azar(5102, k * 3 + j) + 0.5f * j) % 1f
                    if (gx < gx0 - 0.01f || gx > gx1 + 0.01f) continue
                    val t = tono(Offset(gx, yTraca(k, gx) + TRACA / 2))
                    val x = px(gx); val y0 = py(yTraca(k, gx)) + 1f * u; val y1 = py(yTraca(k + 1, gx))
                    drawLine(t.c(ALQUITRAN).copy(alpha = 0.8f), Offset(x, y0), Offset(x, y1), 1.4f * u)
                    drawLine(t.b(0.18f), Offset(x + 1.3f * u, y0), Offset(x + 1.3f * u, y1), 0.7f * u)
                    listOf(-1f, 1f).forEach { s ->
                        val ox2 = x + s * 3.2f * u
                        clavo(Offset(ox2, y0 + (y1 - y0) * 0.32f), 1.05f * u, t, azar(5103, k * 5 + j) > 0.45f, W * 0.022f)
                        clavo(Offset(ox2, y0 + (y1 - y0) * 0.72f), 1.05f * u, t)
                    }
                }
                val gs = azar(5104, k)
                if (gs in gx0..gx1) {
                    val t = tono(Offset(gs, yTraca(k, gs) + TRACA / 2))
                    clavo(Offset(px(gs), py(yTraca(k, gs) + TRACA * 0.45f)), 1.1f * u, t, true, W * (0.02f + 0.02f * azar(5105, k)))
                }
            }
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        val o = oscuro
        esNoche = o
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base
        val plan = b.top + w * PLAN

        // ---- el forro del casco, de lado a lado y de arriba abajo
        forro(w, 0f, 0f, Rect(0f, 0f, w, plan), 12) { p -> Tono(o, if (o) luz(p) else 0f, if (o) 0f else (0.05f - 0.06f * p.y).coerceAtLeast(-0.1f)) }
        if (!o) { mancha(Offset(-w * 0.05f, -w * 0.05f), w * 1.05f, luzMueble(0.3f)); mancha(Offset(w * 0.27f, w * 0.3f), w * 0.33f, luzMueble(0.2f)) }
        pinceladas(Rect(0f, 0f, w, plan), if (o) NOCHE else CASCO_OSC, 6001, 70, u, 0.1f)
        pinceladas(Rect(0f, 0f, w, plan), if (o) NOCHE else mezcla(CASCO, LUZ_MUEBLE, 0.3f), 6002, 30, u, 0.07f)
        // La sal de abajo: manchas blanquecinas donde rezuma la humedad.
        for (k in 0 until 9) {
            val p = Offset(w * azar(6003, k), b.top - w * (0.05f + 0.25f * azar(6004, k)))
            val t = tn(p)
            drawOval(t.c(mezcla(LONA, CASCO, 0.35f)).copy(alpha = 0.1f + 0.06f * azar(6005, k)), Offset(p.x - w * 0.04f, p.y), Size(w * (0.05f + 0.06f * azar(6006, k)), w * 0.012f))
        }

        // ---- el techo, los baos y el durmiente
        techo(w)
        // ---- las cuadernas, con su sombra en el forro
        CUADERNAS.forEach { (cx, hw) -> sombraCuaderna(cx, hw, plan) }
        CUADERNAS.forEachIndexed { i, (cx, hw) -> cuaderna(i, cx, hw, plan) }
        // ---- las baldas de la estiba con sus barrotes y sus trincas
        baldaAlta(w)
        d.filas.forEachIndexed { i, f -> balda(i, f, d) }

        // ---- lo de arriba: el coy, colgado; el farol y la adujada; lo de la balda alta
        talega(Offset(w * 0.305f, w * 0.325f))
        coy()
        adujada(Offset(w * 0.945f, w * 0.215f))
        octante(Offset(w * 0.578f, w * FONDO_T + w * 0.004f))
        jarra(Offset(w * 0.105f, w * 0.578f))
        carta(Offset(w * 0.17f, w * 0.581f))
        botella(Offset(w * 0.425f, w * 0.575f))
        cuchillo(Offset(w * 0.68f, w * 0.584f))
        catalejo(Offset(w * 0.735f, w * 0.582f))
        barroteAlto(w)
        farol(Offset(w * GANCHO.x, w * GANCHO.y))

        // ---- al pie: el zócalo de la estiba, el plan y lo que hay en él
        pie(d, plan, alto)

        // ---- la placa de «y N más»: una pizarrita clavada en el zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            pizarra(r, t, 0)
        }

        // ---- la luz: de día, el haz de la escotilla y la cuadrícula del enjaretado; de noche, el halo del farol
        if (!o) escotilla(w)
        else {
            val llama = LUZ * w
            mancha(llama + Offset(-w * 0.05f, w * 0.08f), w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(llama + Offset(-w * 0.02f, w * 0.03f), w * 0.2f, LLAMA.copy(alpha = 0.22f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * El techo: la cubierta vista desde abajo con sus costuras; los baos que vienen hacia quien mira (se ve su cara de abajo,
     * más ancha cuanto más cerca); y el durmiente, el madero grueso que corre por el costado y los sostiene, con sus pernos y
     * su sombra en el forro.
     */
    private fun DrawScope.techo(w: Float) {
        val u = density
        val o = esNoche
        val yT = w * TECHO; val yD = w * DURMIENTE
        for (j in 0 until 10) {
            val x0 = w * j / 10f; val t = tn(Offset(x0 + w * 0.05f, yT * 0.5f))
            drawRect(t.c(mezcla(CASCO_OSC, ALQUITRAN, 0.35f)), Offset(x0, 0f), Size(w / 10f + 1f, yT))
            listOf(0.33f, 0.68f).forEach { f -> drawRect(t.c(ALQUITRAN), Offset(x0, yT * f), Size(w / 10f + 1f, 1.2f * u)); drawRect(t.b(0.12f), Offset(x0, yT * f + 1.2f * u), Size(w / 10f + 1f, 0.7f * u)) }
        }
        // Los baos.
        BAOS.forEachIndexed { i, x ->
            val cx = w * x
            val t = tn(Offset(cx, yT * 0.5f))
            val p = poligono(cx - w * 0.026f, yT, cx + w * 0.026f, yT, cx + w * 0.036f, 0f, cx - w * 0.036f, 0f)
            drawPath(p, t.c(ROBLE))
            clipPath(p) {
                vetas(Rect(cx - w * 0.04f, 0f, cx + w * 0.04f, yT), t.s(0.25f), 6010 + i, 0.6f, vertical = true, grosor = 0.5f)
                drawRect(t.b(0.25f), Offset(cx - w * 0.04f, 0f), Size(w * 0.018f, yT))
                drawRect(t.s(0.3f), Offset(cx + w * 0.012f, 0f), Size(w * 0.03f, yT))
            }
            drawLine(t.s(0.5f), Offset(cx - w * 0.026f, yT), Offset(cx + w * 0.026f, yT), 1.4f * u)
        }
        // El durmiente, con sus pernos.
        for (j in 0 until 10) {
            val x0 = w * j / 10f; val t = tn(Offset(x0 + w * 0.05f, (yT + yD) / 2))
            drawRect(t.c(mezcla(ROBLE, CASCO_OSC, 0.35f)), Offset(x0, yT), Size(w / 10f + 1f, yD - yT))
            drawRect(t.b(0.4f), Offset(x0, yT), Size(w / 10f + 1f, 1.2f * u))
            drawRect(t.s(0.25f), Offset(x0, yD - (yD - yT) * 0.3f), Size(w / 10f + 1f, (yD - yT) * 0.3f))
            drawRect(t.s(0.45f), Offset(x0, yD - 1.4f * u), Size(w / 10f + 1f, 1.4f * u))
            // La sombra del durmiente en el forro.
            drawRect(t.s(0.3f), Offset(x0, yD), Size(w / 10f + 1f, w * 0.008f))
            drawRect(t.s(0.12f), Offset(x0, yD + w * 0.008f), Size(w / 10f + 1f, w * 0.014f))
        }
        vetas(Rect(0f, yT, w, yD), (if (o) NOCHE else CASCO_OSC).copy(alpha = 0.35f), 6020, 0.7f, grosor = 0.5f)
        for (j in 0 until 7) {
            val p = Offset(w * (0.09f + 0.145f * j), (yT + yD) / 2)
            clavo(p, 1.9f * u, tn(p), true, w * 0.03f)
        }
    }

    /**
     * La sombra de una cuaderna en el forro: de día, una franja a su derecha; de noche, larga y apartándose del farol, tanto
     * más ancha cuanto más lejos está de él a lo ancho (es la sombra de un canto que sobresale, vista desde una luz cercana).
     */
    private fun DrawScope.sombraCuaderna(cx: Float, hw: Float, plan: Float) {
        val w = anchoW
        val y0 = w * DURMIENTE
        if (!esNoche) {
            drawRect(sombraMueble(0.3f), Offset(w * (cx + hw), y0), Size(w * 0.018f, plan - y0))
            drawRect(sombraMueble(0.12f), Offset(w * (cx + hw + 0.018f), y0), Size(w * 0.014f, plan - y0))
        } else {
            val izq = cx < LUZ.x
            val borde = if (izq) cx - hw else cx + hw
            val ancho = (abs(borde - LUZ.x) * 0.22f).coerceIn(0.01f, 0.13f)
            val x = w * borde; val s = if (izq) -1f else 1f
            // Por tramos: más negra donde el farol la marca más.
            for (j in 0 until 10) {
                val ya = y0 + (plan - y0) * j / 10f; val yb = y0 + (plan - y0) * (j + 1) / 10f
                val k = luz(Offset(borde, (ya + yb) / 2 / w))
                val crece = 1f + 0.6f * ((ya / w - LUZ.y).coerceAtLeast(0f))
                drawPath(poligono(x, ya, x + s * w * ancho * crece, ya + w * 0.02f, x + s * w * ancho * crece, yb + w * 0.02f, x, yb), NOCHE.copy(alpha = (0.75f * k).coerceAtMost(0.55f)))
            }
        }
    }

    /**
     * Una cuaderna: el madero de roble que sobresale del forro, con su veta, su codo (la curva que la une al bao) arriba, el
     * costado que se ve (el de dentro), los golpes y los pernos.
     */
    private fun DrawScope.cuaderna(i: Int, cx: Float, hw: Float, plan: Float) {
        val u = density
        val w = anchoW
        val y0 = w * DURMIENTE; val yC = w * 0.2f
        val x0 = w * (cx - hw); val x1 = w * (cx + hw)
        val n = 14
        for (j in 0 until n) {
            val ya = y0 + (plan - y0) * j / n; val yb = y0 + (plan - y0) * (j + 1) / n + 1f
            val t = tn(Offset(w * cx, (ya + yb) / 2))
            drawRect(t.c(ROBLE), Offset(x0, ya), Size(x1 - x0, yb - ya))
            // La cara de luz y la de sombra; de noche, el filo hacia el farol.
            drawRect(t.b(0.22f), Offset(x0, ya), Size((x1 - x0) * 0.22f, yb - ya))
            drawRect(t.s(0.22f), Offset(x1 - (x1 - x0) * 0.25f, ya), Size((x1 - x0) * 0.25f, yb - ya))
            if (esNoche) {
                val hacia = if (cx < LUZ.x) x1 - 1.3f * u else x0
                drawRect(LLAMA.copy(alpha = (0.7f * t.k).coerceAtMost(0.6f)), Offset(hacia, ya), Size(1.3f * u, yb - ya))
            }
        }
        val todo = Rect(x0, y0, x1, plan)
        vetas(todo, (if (esNoche) NOCHE else CASCO_OSC).copy(alpha = 0.35f), 6030 + i, 0.8f, vertical = true, grosor = 0.6f, ondulacion = 0.6f)
        // El costado de dentro: la cuaderna de la izquierda enseña su lado derecho; la de la derecha, el izquierdo.
        if (i != 1) {
            val izq = i == 0
            for (j in 0 until 7) {
                val ya = y0 + (plan - y0) * j / 7; val yb = y0 + (plan - y0) * (j + 1) / 7 + 1f
                val t = tn(Offset(w * cx, (ya + yb) / 2))
                val xs = if (izq) x1 else x0 - w * 0.008f
                drawRect(t.c(mezcla(ROBLE, CASCO_OSC, if (izq) 0.55f else 0.1f)), Offset(xs, ya), Size(w * 0.008f, yb - ya))
                if (!izq && !esNoche) drawRect(luzMueble(0.2f), Offset(xs, ya), Size(w * 0.003f, yb - ya))
            }
        }
        // El codo: la cuaderna se abre hacia el bao, con su curva y un perno.
        val t = tn(Offset(w * cx, (y0 + yC) / 2))
        val codo = Path().apply {
            moveTo(x0, yC); cubicTo(x0, yC - (yC - y0) * 0.45f, x0 - w * hw * 0.6f, y0 + (yC - y0) * 0.2f, x0 - w * hw * 0.9f, y0)
            lineTo(x1 + w * hw * 0.9f, y0); cubicTo(x1 + w * hw * 0.6f, y0 + (yC - y0) * 0.2f, x1, yC - (yC - y0) * 0.45f, x1, yC); close()
        }
        drawPath(codo, t.c(mezcla(ROBLE, ROBLE_LUZ, 0.15f)))
        clipPath(codo) {
            drawRect(t.b(0.2f), Offset(x0 - w * hw, y0), Size(w * hw * 0.9f, yC - y0))
            drawRect(t.s(0.25f), Offset(x1 - w * hw * 0.2f, y0), Size(w * hw * 1.2f, yC - y0))
        }
        drawLine(t.s(0.35f), Offset(x0 - w * hw * 0.6f, y0 + w * 0.012f), Offset(x0, yC), 0.8f * u)
        clavo(Offset(w * cx, y0 + (yC - y0) * 0.4f), 2.2f * u, t, true, w * 0.035f)
        // Golpes y rozaduras del roble.
        for (k in 0 until 5) {
            val y = y0 + (plan - y0) * (0.2f + 0.75f * azar(6040 + i, k))
            val tt = tn(Offset(w * cx, y))
            drawLine(tt.b(0.3f), Offset(x0 + (x1 - x0) * 0.2f, y), Offset(x0 + (x1 - x0) * (0.5f + 0.4f * azar(6041 + i, k)), y + w * 0.006f), 0.7f * u)
            drawLine(tt.s(0.3f), Offset(x0 + (x1 - x0) * 0.2f, y + 0.8f * u), Offset(x0 + (x1 - x0) * 0.6f, y + w * 0.006f + 0.8f * u), 0.6f * u)
        }
        // Pernos de la cuaderna, con su óxido.
        for (k in 0 until 5) {
            val p = Offset(w * cx + (x1 - x0) * (azar(6050 + i, k) - 0.5f) * 0.4f, y0 + (plan - y0) * (0.12f + 0.2f * k))
            clavo(p, 1.6f * u, tn(p), azar(6051 + i, k) > 0.4f, w * 0.03f)
        }
    }

    /** Un tablón de la estiba, de [x0] a [x1] y de [y0] (arriba) a [y1]: la cara con su veta, su canto de luz y su sombra en el forro. */
    private fun DrawScope.tablon(x0: Float, x1: Float, y0: Float, y1: Float, semilla: Int, color: Color = mezcla(ROBLE, ROBLE_LUZ, 0.45f)) {
        val u = density
        val w = anchoW
        for (j in 0 until 8) {
            val xa = x0 + (x1 - x0) * j / 8; val xb = x0 + (x1 - x0) * (j + 1) / 8 + 1f
            val t = tn(Offset((xa + xb) / 2, (y0 + y1) / 2))
            // La sombra que deja en el forro de abajo.
            drawRect(t.s(0.32f), Offset(xa, y1), Size(xb - xa, w * 0.007f))
            drawRect(t.s(0.14f), Offset(xa, y1 + w * 0.007f), Size(xb - xa, w * 0.012f))
            drawRect(t.c(color), Offset(xa, y0), Size(xb - xa, y1 - y0))
            drawRect(t.b(0.5f), Offset(xa, y0), Size(xb - xa, 1.1f * u))
            if (esNoche) drawRect(LLAMA.copy(alpha = (0.55f * t.k).coerceAtMost(0.5f)), Offset(xa, y0), Size(xb - xa, 1.1f * u))
            drawRect(t.s(0.3f), Offset(xa, y1 - 1.4f * u), Size(xb - xa, 1.4f * u))
        }
        vetas(Rect(x0, y0, x1, y1), (if (esNoche) NOCHE else CASCO_OSC).copy(alpha = 0.32f), semilla, 0.8f, grosor = 0.5f, ondulacion = 0.5f)
        // La sal en el canto.
        for (k in 0 until 3) {
            val x = x0 + (x1 - x0) * azar(semilla + 1, k)
            val t = tn(Offset(x, y1))
            drawOval(t.c(LONA).copy(alpha = 0.16f), Offset(x, y1 - (y1 - y0) * 0.55f), Size(w * 0.05f, (y1 - y0) * 0.45f))
        }
    }

    /** Los candeleros y el barrote de mar a la altura [y], de lado a lado, con su sombra debajo. */
    private fun DrawScope.barroteLargo(y: Float, g: Float, hasta: Float, semilla: Int) {
        val u = density
        val w = anchoW
        CUADERNAS.forEach { (cx, _) ->
            val x = w * cx
            val t = tn(Offset(x, (y + hasta) / 2))
            val pr = Rect(x - w * 0.007f, y, x + w * 0.007f, hasta)
            drawRect(t.s(0.25f), Offset(pr.left + 1.5f * u, pr.top), pr.size)
            drawRect(t.c(ROBLE_LUZ), pr.topLeft, pr.size)
            drawRect(t.b(0.3f), pr.topLeft, Size(pr.width * 0.35f, pr.height))
            drawRect(t.s(0.25f), Offset(pr.right - pr.width * 0.3f, pr.top), Size(pr.width * 0.3f, pr.height))
        }
        for (j in 0 until 8) {
            val xa = w * j / 8; val xb = w * (j + 1) / 8 + 1f
            val t = tn(Offset((xa + xb) / 2, y))
            drawRect(t.s(0.22f), Offset(xa, y + g), Size(xb - xa, g * 0.7f))
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)), Offset(xa, y), Size(xb - xa, g))
            drawRect(t.b(0.45f), Offset(xa, y), Size(xb - xa, g * 0.3f))
            drawRect(t.s(0.25f), Offset(xa, y + g * 0.72f), Size(xb - xa, g * 0.28f))
        }
        // Los clavos del barrote en cada candelero.
        CUADERNAS.forEach { (cx, _) -> val p = Offset(w * cx, y + g * 0.5f); clavo(p, 1.1f * u, tn(p)) }
        vetas(Rect(0f, y, w, y + g), (if (esNoche) NOCHE else CASCO_OSC).copy(alpha = 0.25f), semilla, 0.6f, grosor = 0.4f)
    }

    /**
     * Una trinca: vueltas de cabo que atan la balda a la cuaderna en [x], encima de la balda que empieza en [y], con su nudo y
     * su chicote colgando. [s] cambia las vueltas.
     */
    private fun DrawScope.trinca(x: Float, hw: Float, y: Float, s: Int) {
        val u = density
        val w = anchoW
        val t = tn(Offset(x, y))
        val g = 2.4f * u
        val alto = w * 0.045f
        val vueltas = 3 + Math.floorMod(s, 2)
        for (j in 0 until vueltas) {
            val ya = y - alto + alto * j / vueltas
            val p = Path().apply { moveTo(x - hw * 1.1f, ya + alto * 0.28f); cubicTo(x - hw * 0.3f, ya + alto * 0.16f, x + hw * 0.4f, ya + alto * 0.02f, x + hw * 1.1f, ya - alto * 0.04f) }
            translate(1.2f * u, 1.6f * u) { drawPath(p, t.s(0.25f), style = Stroke(g)) }
            cabo(p, g, t)
        }
        // El nudo y el chicote.
        val nudo = Offset(x + hw * (if (s % 3 == 0) -0.9f else 0.9f), y - alto * 0.15f)
        drawCircle(t.s(0.3f), g * 1.2f, nudo + Offset(1f * u, 1.3f * u))
        drawCircle(t.c(CABO_OSC), g * 1.15f, nudo)
        drawCircle(t.c(CABO), g * 0.7f, nudo - Offset(g * 0.25f, g * 0.25f))
        val l = w * (0.035f + 0.025f * azar(6060, s))
        val chicote = Path().apply { moveTo(nudo.x, nudo.y); cubicTo(nudo.x + g * 2f, nudo.y + l * 0.4f, nudo.x - g * 1.5f, nudo.y + l * 0.7f, nudo.x + g * 0.6f, nudo.y + l) }
        translate(1.2f * u, 1.5f * u) { drawPath(chicote, t.s(0.2f), style = Stroke(g * 0.9f)) }
        cabo(chicote, g * 0.9f, t)
        // El extremo deshilachado.
        val fin = Offset(nudo.x + g * 0.6f, nudo.y + l)
        for (k in -1..1) drawLine(t.c(CABO), fin, fin + Offset(k * g * 0.5f, g * 1.2f), 0.5f * u)
    }

    /** La balda alta, donde están las cosas de Tomás: su cara de arriba, su canto, la sombra en el forro. */
    private fun DrawScope.baldaAlta(w: Float) {
        val u = density
        // La cara de arriba, vista apenas.
        for (j in 0 until 8) {
            val xa = w * j / 8; val xb = w * (j + 1) / 8 + 1f
            val t = tn(Offset((xa + xb) / 2, w * FONDO_T), if (esNoche) 0f else 0.06f)
            drawRect(t.c(mezcla(ROBLE_LUZ, LONA, 0.15f)), Offset(xa, w * FONDO_T), Size(xb - xa, w * (TABLA - FONDO_T)))
            drawRect(t.s(0.35f), Offset(xa, w * FONDO_T), Size(xb - xa, 1.5f * u))
        }
        vetas(Rect(0f, w * FONDO_T, w, w * TABLA), (if (esNoche) NOCHE else ROBLE).copy(alpha = 0.3f), 6070, 0.6f, grosor = 0.5f)
        // Cercos y manchas de uso.
        listOf(0.31f, 0.86f).forEach { f ->
            val t = tn(Offset(w * f, w * 0.577f))
            drawOval(t.s(0.18f), Offset(w * f, w * 0.573f), Size(w * 0.04f, w * 0.009f), style = Stroke(0.8f * u))
        }
        tablon(0f, w, w * TABLA, w * CANTO_T, 6071)
        CUADERNAS.forEachIndexed { i, (cx, hw) -> clavo(Offset(w * cx, w * (TABLA + CANTO_T) / 2), 1.6f * u, tn(Offset(w * cx, w * TABLA)), i == 1, w * 0.02f) }
    }

    /** El barrote de mar de la balda alta, por delante de lo que hay encima. */
    private fun DrawScope.barroteAlto(w: Float) = barroteLargo(w * 0.553f, w * 0.009f, w * TABLA, 6072)

    /** Una balda entre filas, con su barrote, sus trincas y, en la segunda, el remiendo. */
    private fun DrawScope.balda(i: Int, f: Rect, d: Disposicion) {
        val u = density
        val w = anchoW
        val ah = f.height
        val y0 = f.bottom; val y1 = y0 + w * BALDA
        tablon(0f, w, y0, y1, 6080 + i)
        if (i == 1) {
            // El remiendo: un trozo de pino nuevo clavado encima del tablón rajado.
            val x0 = w * 0.6f; val x1 = w * 0.79f
            val t = tn(Offset((x0 + x1) / 2, y0))
            drawLine(t.s(0.5f), Offset(x0 - w * 0.03f, y0 + (y1 - y0) * 0.55f), Offset(x1 + w * 0.04f, y0 + (y1 - y0) * 0.45f), 0.8f * u)
            drawRect(t.s(0.3f), Offset(x0 + 1.5f * u, y0 - 1f * u), Size(x1 - x0, y1 - y0 + 3f * u))
            drawRect(t.c(mezcla(ROBLE_LUZ, LONA, 0.3f)), Offset(x0, y0 - 2f * u), Size(x1 - x0, y1 - y0 + 3f * u))
            vetas(Rect(x0, y0 - 2f * u, x1, y1 + 1f * u), t.c(ROBLE).copy(alpha = 0.4f), 6090, 0.9f, grosor = 0.5f)
            drawRect(t.b(0.5f), Offset(x0, y0 - 2f * u), Size(x1 - x0, 1f * u))
            listOf(0.08f, 0.92f).forEach { fx -> listOf(0.3f, 0.75f).forEach { fy -> clavo(Offset(x0 + (x1 - x0) * fx, y0 - 2f * u + (y1 - y0 + 3f * u) * fy), 1.2f * u, t, fy > 0.5f, w * 0.012f) } }
        }
        // El barrote de mar de la fila (lo vuelve a pintar cada hueco por delante de lo que guarda).
        barroteLargo(f.top + ah * BARR, ah * BARR_G, y0, 6100 + i)
        // Las trincas en cada cuaderna.
        CUADERNAS.forEachIndexed { j, (cx, hw) -> trinca(w * cx, w * hw, y0, i * 3 + j) }
    }

    // ---------------------------------------------------------------- lo de arriba

    /**
     * El coy recogido: la hamaca de lona enrollada y trincada con vueltas de meollar, combada entre dos ganchos del durmiente,
     * con sus arraigados en abanico hasta las argollas y el borde de una manta roja que asoma. La cuadrícula de la escotilla
     * le cae encima (se pinta después).
     */
    private fun DrawScope.coy() {
        val u = density
        val w = anchoW
        val g1 = Offset(w * 0.075f, w * DURMIENTE); val g2 = Offset(w * 0.455f, w * DURMIENTE)
        val e1 = Offset(w * 0.115f, w * 0.2f); val e2 = Offset(w * 0.425f, w * 0.19f)
        val c1 = Offset(w * 0.17f, w * 0.33f); val c2 = Offset(w * 0.37f, w * 0.325f)
        fun pt(s: Float): Offset {
            val m = 1f - s
            return e1 * (m * m * m) + c1 * (3 * m * m * s) + c2 * (3 * m * s * s) + e2 * (s * s * s)
        }
        fun tg(s: Float): Offset {
            val m = 1f - s
            val d = (c1 - e1) * (3 * m * m) + (c2 - c1) * (6 * m * s) + (e2 - c2) * (3 * s * s)
            return d / d.getDistance().coerceAtLeast(0.001f)
        }
        val n = 22
        val arriba = ArrayList<Offset>(); val abajo = ArrayList<Offset>()
        fun grueso(s: Float) = w * (0.03f + 0.013f * sin(Math.PI * s).toFloat().let { kotlin.math.sqrt(it) })
        for (i in 0..n) {
            val s = i / n.toFloat()
            val p = pt(s); val d = tg(s); val nr = Offset(d.y, -d.x)
            arriba.add(p + nr * grueso(s)); abajo.add(p - nr * grueso(s))
        }
        val forma = Path().apply {
            moveTo(arriba[0].x, arriba[0].y); for (i in 1..n) lineTo(arriba[i].x, arriba[i].y)
            for (i in n downTo 0) lineTo(abajo[i].x, abajo[i].y); close()
        }
        val t = tn(pt(0.5f))
        // Los arraigados: de cada argolla, un abanico de cabitos al extremo del coy.
        listOf(Triple(g1, 0, 1f), Triple(g2, n, -1f)).forEach { (g, idx, _) ->
            val argolla = g + Offset(0f, w * 0.03f)
            val tt = tn(argolla)
            // El gancho.
            drawRect(tt.c(HIERRO), Offset(g.x - w * 0.006f, g.y - 1f * u), Size(w * 0.012f, 2.5f * u))
            drawArc(tt.c(HIERRO), 0f, 200f, false, Offset(g.x - w * 0.009f, g.y + w * 0.006f), Size(w * 0.018f, w * 0.022f), style = Stroke(1.8f * u))
            drawLine(tt.c(HIERRO), g, g + Offset(w * 0.009f, w * 0.017f), 1.8f * u)
            for (k in 0 until 7) {
                val q = arriba[idx] + (abajo[idx] - arriba[idx]) * (k / 6f)
                val p = Path().apply { moveTo(argolla.x, argolla.y); lineTo(q.x, q.y) }
                translate(w * 0.01f, w * 0.01f) { if (!esNoche) drawPath(p, sombraMueble(0.12f), style = Stroke(0.9f * u)) }
                drawPath(p, tt.c(CABO_OSC), style = Stroke(1.1f * u))
                drawPath(p, tt.c(CABO).copy(alpha = 0.7f), style = Stroke(0.5f * u))
            }
            drawCircle(tt.c(HIERRO), w * 0.008f, argolla, style = Stroke(1.6f * u))
            drawCircle(tt.b(0.4f), w * 0.008f, argolla, style = Stroke(0.5f * u))
        }
        // Su sombra en el forro.
        sombraEnForro(forma, pt(0.5f), w * FONDO_T, 0.2f)
        // La lona.
        drawPath(forma, t.c(LONA))
        clipPath(forma) {
            fun curva(off: Float, desde: Float = 0f, hasta: Float = 1f) = Path().apply {
                for (i in 0..n) {
                    val s = desde + (hasta - desde) * i / n
                    val p = pt(s); val d = tg(s); val q = p + Offset(d.y, -d.x) * (grueso(s) * off)
                    if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
                }
            }
            drawPath(curva(0.65f), t.b(0.35f), style = Stroke(w * 0.012f))
            drawPath(curva(-0.55f), t.s(0.2f), style = Stroke(w * 0.022f))
            drawPath(curva(-0.95f), t.s(0.28f), style = Stroke(w * 0.01f))
            // Los pliegues a lo largo.
            listOf(0.25f to 0.18f, -0.1f to 0.1f, 0.45f to 0.12f).forEachIndexed { j, (off, a) ->
                drawPath(curva(off, 0.08f + 0.06f * j, 0.92f - 0.05f * j), t.s(a), style = Stroke(0.8f * u))
                translate(0f, 1.1f * u) { drawPath(curva(off, 0.1f + 0.06f * j, 0.9f - 0.05f * j), t.b(0.2f), style = Stroke(0.6f * u)) }
            }
            // Manchas de uso.
            pinceladas(Rect(w * 0.1f, w * 0.18f, w * 0.43f, w * 0.42f), if (esNoche) NOCHE else LONA_OSC, 6110, 10, u, 0.18f)
        }
        // El borde de la manta roja que asoma por abajo.
        val manta = Path().apply {
            val a = 0.33f; val bb = 0.6f
            for (i in 0..10) { val s = a + (bb - a) * i / 10; val p = pt(s); val d = tg(s); val q = p - Offset(d.y, -d.x) * (grueso(s) * 0.92f); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
            for (i in 10 downTo 0) { val s = a + (bb - a) * i / 10; val p = pt(s); val d = tg(s); val q = p - Offset(d.y, -d.x) * (grueso(s) * 1.05f + w * 0.006f * sin(Math.PI * i / 10).toFloat()); lineTo(q.x, q.y) }
            close()
        }
        drawPath(manta, t.c(mezcla(BERMELLON, LONA_OSC, 0.25f)))
        drawPath(manta, t.s(0.25f), style = Stroke(0.6f * u))
        // Las vueltas de meollar que lo trincan.
        listOf(0.1f, 0.26f, 0.43f, 0.6f, 0.76f, 0.9f).forEachIndexed { j, s ->
            val a = arriba[(s * n).toInt().coerceIn(0, n)] ; val bb = abajo[((s - 0.03f) * n).toInt().coerceIn(0, n)]
            val p = Path().apply { moveTo(a.x, a.y - 0.5f * u); cubicTo(a.x + w * 0.006f, (a.y + bb.y) / 2, bb.x + w * 0.006f, (a.y + bb.y) / 2, bb.x, bb.y + 0.5f * u) }
            drawPath(p, t.s(0.3f), style = Stroke(3.6f * u))
            cabo(p, 1.9f * u, t)
            if (j % 2 == 0) drawCircle(t.c(CABO_OSC), 1.6f * u, Offset(a.x + w * 0.003f, a.y + w * 0.004f))
        }
        drawPath(forma, t.s(0.3f), style = Stroke(0.7f * u))
    }

    /**
     * La talega de Tomás: una bolsa de lona blanda colgada de un clavo del forro por su cordón, fruncida en la boca, con su
     * remiendo y el peso abajo (lo que lleva dentro la deforma).
     */
    private fun DrawScope.talega(clavo0: Offset) {
        val u = density
        val w = anchoW
        val t = tn(clavo0 + Offset(0f, w * 0.08f), if (esNoche) 0f else 0.02f)
        val cuello = clavo0 + Offset(-w * 0.004f, w * 0.045f)
        val forma = Path().apply {
            moveTo(cuello.x - w * 0.012f, cuello.y)
            cubicTo(cuello.x - w * 0.03f, cuello.y + w * 0.03f, cuello.x - w * 0.06f, cuello.y + w * 0.08f, cuello.x - w * 0.045f, cuello.y + w * 0.13f)
            cubicTo(cuello.x - w * 0.02f, cuello.y + w * 0.155f, cuello.x + w * 0.035f, cuello.y + w * 0.155f, cuello.x + w * 0.05f, cuello.y + w * 0.12f)
            cubicTo(cuello.x + w * 0.06f, cuello.y + w * 0.08f, cuello.x + w * 0.028f, cuello.y + w * 0.03f, cuello.x + w * 0.012f, cuello.y)
            close()
        }
        sombraEnForro(forma, cuello + Offset(0f, w * 0.08f), w * FONDO_T, 0.2f)
        // El cordón al clavo.
        val cordon = Path().apply { moveTo(cuello.x - w * 0.008f, cuello.y); lineTo(clavo0.x, clavo0.y); lineTo(cuello.x + w * 0.008f, cuello.y) }
        drawPath(cordon, t.c(CABO_OSC), style = Stroke(1.3f * u))
        clavo(clavo0, 1.5f * u, t)
        drawPath(forma, t.c(mezcla(LONA_OSC, CABO, 0.25f)))
        clipPath(forma) {
            if (!esNoche) drawRect(t.b(0.2f), Offset(cuello.x - w * 0.06f, cuello.y), Size(w * 0.035f, w * 0.16f))
            drawRect(t.s(0.25f), Offset(cuello.x + w * 0.02f, cuello.y), Size(w * 0.05f, w * 0.16f))
            drawRect(t.s(0.2f), Offset(cuello.x - w * 0.06f, cuello.y + w * 0.12f), Size(w * 0.12f, w * 0.04f))
            // El remiendo, de otra tela, con sus puntadas.
            val rr = Rect(cuello.x - w * 0.03f, cuello.y + w * 0.07f, cuello.x + w * 0.0f, cuello.y + w * 0.1f)
            drawRect(t.c(mezcla(AZUL, LONA, 0.35f)), rr.topLeft, rr.size)
            drawRect(t.c(LONA).copy(alpha = 0.7f), rr.topLeft, rr.size, style = Stroke(0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.5f * u, 1.2f * u))))
        }
        // Los pliegues que bajan del frunce.
        listOf(-1f, 0f, 1f).forEach { f ->
            val p = Path().apply { moveTo(cuello.x + f * w * 0.006f, cuello.y + w * 0.006f); cubicTo(cuello.x + f * w * 0.02f, cuello.y + w * 0.04f, cuello.x + f * w * 0.03f, cuello.y + w * 0.07f, cuello.x + f * w * 0.028f, cuello.y + w * 0.1f) }
            drawPath(p, t.s(0.25f), style = Stroke(1f * u))
        }
        // El frunce y su lazada.
        drawOval(t.c(CABO_OSC), Offset(cuello.x - w * 0.014f, cuello.y - w * 0.004f), Size(w * 0.028f, w * 0.01f))
        val lazo = Path().apply { moveTo(cuello.x + w * 0.01f, cuello.y + w * 0.002f); cubicTo(cuello.x + w * 0.025f, cuello.y + w * 0.01f, cuello.x + w * 0.02f, cuello.y + w * 0.03f, cuello.x + w * 0.03f, cuello.y + w * 0.045f) }
        cabo(lazo, 1.4f * u, t)
    }

    /**
     * El farol de sebo, ladeado por el balanceo: el cono con sus respiraderos, el aro para colgarlo, el cuerpo de hojalata con
     * cuatro varillas y los cristales de asta (de cuerno, lechosos y moteados), la vela de sebo dentro y el plato de abajo.
     * De noche la vela arde y el asta brilla; de día es un farol apagado con su vela a medias.
     */
    private fun DrawScope.farol(gancho: Offset) {
        val u = density
        val w = anchoW
        val o = esNoche
        val cx = gancho.x
        val yA = gancho.y + w * 0.035f
        val cono0 = yA + w * 0.008f; val cono1 = yA + w * 0.05f
        val cuerpo0 = cono1; val cuerpo1 = cuerpo0 + w * 0.125f
        val hw = w * 0.042f
        // La sombra en el forro (de día).
        if (!o) {
            val silueta = Path().apply {
                addRect(Rect(cx - hw, cuerpo0, cx + hw, cuerpo1 + w * 0.012f))
                addPath(poligono(cx - w * 0.01f, cono0, cx + w * 0.01f, cono0, cx + hw * 1.12f, cono1, cx - hw * 1.12f, cono1))
            }
            rotate(GIRO, gancho) { translate(w * 0.024f, w * 0.02f) { drawPath(silueta, sombraMueble(0.2f)) } }
        }
        val t = Tono(o, if (o) 1f else 0f, 0.02f)
        rotate(GIRO, gancho) {
            // El gancho, la gaza de cabo y el aro.
            drawLine(tn(gancho).c(HIERRO), gancho, gancho + Offset(0f, w * 0.012f), 2f * u)
            val gaza = Path().apply { moveTo(cx, gancho.y + w * 0.01f); cubicTo(cx - w * 0.006f, gancho.y + w * 0.02f, cx + w * 0.006f, yA - w * 0.006f, cx, yA) }
            cabo(gaza, 1.8f * u, t)
            drawArc(t.c(HIERRO), 180f, 180f, false, Offset(cx - w * 0.01f, yA - w * 0.004f), Size(w * 0.02f, w * 0.018f), style = Stroke(1.6f * u))
            // El cono, con sus respiraderos.
            val cono = poligono(cx - w * 0.01f, cono0, cx + w * 0.01f, cono0, cx + hw * 1.12f, cono1, cx - hw * 1.12f, cono1)
            drawPath(cono, t.c(PELTRE))
            clipPath(cono) {
                drawRect(if (o) LLAMA.copy(alpha = 0.15f) else luzMueble(0.25f), Offset(cx - hw * 1.2f, cono0), Size(hw * 0.7f, cono1 - cono0))
                drawRect(t.s(0.3f), Offset(cx + hw * 0.35f, cono0), Size(hw, cono1 - cono0))
            }
            for (k in 0 until 5) {
                val x = cx + hw * (-0.7f + 0.35f * k)
                drawCircle(if (o) LLAMA else t.c(ALQUITRAN), 1.1f * u, Offset(x, cono0 + (cono1 - cono0) * 0.6f))
            }
            drawRect(t.c(mezcla(PELTRE, HIERRO, 0.4f)), Offset(cx - hw * 1.15f, cono1 - 1.6f * u), Size(hw * 2.3f, 2.6f * u))
            // Los cristales de asta.
            val vid = Rect(cx - hw, cuerpo0, cx + hw, cuerpo1)
            drawRect(if (o) mezcla(LLAMA, LONA, 0.35f) else t.c(mezcla(LONA_OSC, CABO, 0.35f)), vid.topLeft, vid.size)
            clipRect(vid.left, vid.top, vid.right, vid.bottom) {
                if (o) {
                    drawOval(mezcla(LLAMA, Color(0xFFFFF0CF), 0.5f), Offset(cx - hw * 0.85f, cuerpo0 + (cuerpo1 - cuerpo0) * 0.12f), Size(hw * 1.6f, (cuerpo1 - cuerpo0) * 0.8f))
                    drawOval(Color(0xFFFFF4DC), Offset(cx - hw * 0.45f, cuerpo0 + (cuerpo1 - cuerpo0) * 0.25f), Size(hw * 0.8f, (cuerpo1 - cuerpo0) * 0.45f))
                    drawRect(OXIDO.copy(alpha = 0.25f), Offset(cx + hw * 0.4f, cuerpo0), Size(hw * 0.6f, cuerpo1 - cuerpo0))
                } else {
                    drawRect(luzMueble(0.25f), vid.topLeft, Size(hw * 0.5f, vid.height))
                    drawRect(sombraMueble(0.22f), Offset(cx + hw * 0.35f, vid.top), Size(hw * 0.7f, vid.height))
                    // La vela de sebo, a medias, que se adivina por el asta.
                    drawRect(LONA.copy(alpha = 0.5f), Offset(cx - hw * 0.18f, vid.top + vid.height * 0.4f), Size(hw * 0.36f, vid.height * 0.6f))
                    drawLine(ALQUITRAN.copy(alpha = 0.4f), Offset(cx, vid.top + vid.height * 0.4f), Offset(cx, vid.top + vid.height * 0.32f), 0.8f * u)
                }
                // El moteado del cuerno.
                for (k in 0 until 9) {
                    val p = Offset(vid.left + vid.width * azar(6120, k), vid.top + vid.height * azar(6121, k))
                    drawOval((if (o) OXIDO else CABO_OSC).copy(alpha = 0.25f), Offset(p.x, p.y), Size(hw * (0.2f + 0.3f * azar(6122, k)), hw * 0.16f))
                }
            }
            if (o) {
                // La llama, viva.
                val lc = Offset(cx - hw * 0.05f, cuerpo0 + (cuerpo1 - cuerpo0) * 0.42f)
                drawOval(LLAMA, Offset(lc.x - w * 0.005f, lc.y - w * 0.012f), Size(w * 0.01f, w * 0.02f))
                drawOval(Color(0xFFFFF7E0), Offset(lc.x - w * 0.0025f, lc.y - w * 0.006f), Size(w * 0.005f, w * 0.011f))
            }
            // Las varillas.
            listOf(-1f, -0.35f, 0.35f, 1f).forEach { f ->
                val x = cx + hw * f
                drawRect(t.c(mezcla(PELTRE, HIERRO, 0.3f)), Offset(x - 1.1f * u, cuerpo0), Size(2.2f * u, cuerpo1 - cuerpo0))
                if (f < 0f) drawRect(if (o) LLAMA.copy(alpha = 0.6f) else luzMueble(0.4f), Offset(x - 1.1f * u, cuerpo0), Size(0.8f * u, cuerpo1 - cuerpo0))
            }
            drawRect(t.c(mezcla(PELTRE, HIERRO, 0.3f)), Offset(cx - hw * 1.05f, cuerpo0 + (cuerpo1 - cuerpo0) * 0.5f - 0.8f * u), Size(hw * 2.1f, 1.6f * u))
            // El plato de abajo, con su pie.
            val plato = Rect(cx - hw * 1.15f, cuerpo1, cx + hw * 1.15f, cuerpo1 + w * 0.012f)
            drawRoundRect(t.c(PELTRE), plato.topLeft, plato.size, CornerRadius(2f * u))
            clipRect(plato.left, plato.top, plato.right, plato.bottom) { cilindro(plato, t, 1.2f) }
            drawRect(t.c(mezcla(PELTRE, HIERRO, 0.5f)), Offset(cx - hw * 0.5f, plato.bottom), Size(hw, w * 0.005f))
            destello(Offset(cx - hw * 0.8f, plato.top + 1.5f * u), 1.2f * u, if (o) Tono(true, 0.8f) else t)
        }
    }

    /**
     * La adujada: un cabo adujado en vueltas colgado de una cabilla (el pasador de madera) en la cuaderna de la derecha, con
     * las vueltas que lo ahorcan arriba y el chicote que cuelga.
     */
    private fun DrawScope.adujada(pin: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pin + Offset(0f, w * 0.06f))
        // La cabilla y su taco.
        drawRect(t.s(0.3f), Offset(pin.x - w * 0.016f + 1.5f * u, pin.y - w * 0.008f + 2f * u), Size(w * 0.032f, w * 0.016f))
        drawRect(t.c(ROBLE_LUZ), Offset(pin.x - w * 0.016f, pin.y - w * 0.008f), Size(w * 0.032f, w * 0.016f))
        drawRect(t.b(0.4f), Offset(pin.x - w * 0.016f, pin.y - w * 0.008f), Size(w * 0.032f, 1f * u))
        drawRoundRect(t.c(mezcla(ROBLE_LUZ, LONA, 0.2f)), Offset(pin.x - w * 0.004f, pin.y - w * 0.03f), Size(w * 0.008f, w * 0.04f), CornerRadius(w * 0.004f))
        drawCircle(t.c(mezcla(ROBLE_LUZ, LONA, 0.2f)), w * 0.006f, Offset(pin.x, pin.y - w * 0.03f))
        // Las vueltas.
        val gr = 2.6f * u
        val ov = mutableListOf<Rect>()
        for (k in 0 until 6) {
            val rx = w * (0.028f + 0.008f * azar(6130, k)); val ry = w * (0.07f + 0.016f * azar(6131, k))
            val cx = pin.x + w * (-0.004f + 0.008f * azar(6132, k))
            ov.add(Rect(cx - rx, pin.y, cx + rx, pin.y + 2 * ry))
        }
        val silueta = Path().apply { ov.forEach { addOval(it) } }
        sombraEnForro(silueta, pin + Offset(0f, w * 0.07f), w * FONDO_T, 0.16f)
        ov.forEachIndexed { k, r ->
            val p = Path().apply { addOval(r) }
            val tt = Tono(t.o, t.k * (if (k % 2 == 0) 0.85f else 1f), t.dia)
            cabo(p, gr, tt)
        }
        // Las vueltas que la ahorcan.
        for (j in 0 until 3) {
            val y = pin.y + w * (0.015f + 0.008f * j)
            val p = Path().apply { moveTo(pin.x - w * 0.03f, y); cubicTo(pin.x - w * 0.01f, y + w * 0.006f, pin.x + w * 0.01f, y + w * 0.006f, pin.x + w * 0.03f, y) }
            cabo(p, gr * 1.05f, t)
        }
        // El chicote.
        val y0 = pin.y + w * 0.04f
        val chicote = Path().apply { moveTo(pin.x - w * 0.02f, y0); cubicTo(pin.x - w * 0.04f, y0 + w * 0.06f, pin.x - w * 0.015f, y0 + w * 0.1f, pin.x - w * 0.03f, y0 + w * 0.165f) }
        cabo(chicote, gr, t)
    }

    /** La jarra de peltre con su tapa de bisagra, el asa y una abolladura. [pie] es el centro de su base. */
    private fun DrawScope.jarra(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie - Offset(0f, w * 0.04f), if (esNoche) 0f else 0.03f)
        val r = Rect(pie.x - w * 0.028f, pie.y - w * 0.075f, pie.x + w * 0.03f, pie.y)
        val cuerpo = poligono(r.left + w * 0.004f, r.top, r.right - w * 0.004f, r.top, r.right, r.bottom, r.left, r.bottom)
        // El asa (detrás, a la derecha).
        val asa = Path().apply { moveTo(r.right - 1f * u, r.top + w * 0.012f); cubicTo(r.right + w * 0.028f, r.top + w * 0.01f, r.right + w * 0.026f, r.bottom - w * 0.03f, r.right - 1f * u, r.bottom - w * 0.016f) }
        sombraEnForro(Path().apply { addPath(cuerpo); addPath(asa) }, r.center, w * FONDO_T + w * 0.004f)
        contacto(t, Offset(pie.x + w * 0.006f, pie.y), w * 0.075f, 3f * u, 0.4f)
        drawPath(asa, t.c(mezcla(PELTRE, HIERRO, 0.35f)), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.3f), style = Stroke(w * 0.003f))
        drawPath(cuerpo, t.c(PELTRE))
        clipPath(cuerpo) {
            cilindro(r, t, 1.3f)
            // Los filetes del peltre.
            listOf(0.18f, 0.86f).forEach { f -> drawRect(t.s(0.25f), Offset(r.left, r.top + r.height * f), Size(r.width, 1.2f * u)); drawRect(t.b(0.3f), Offset(r.left, r.top + r.height * f + 1.2f * u), Size(r.width, 0.7f * u)) }
            // La abolladura.
            drawArc(t.s(0.35f), 200f, 120f, false, Offset(r.left + r.width * 0.55f, r.top + r.height * 0.45f), Size(r.width * 0.3f, r.height * 0.2f), style = Stroke(1f * u))
        }
        destello(Offset(r.left + r.width * 0.24f, r.top + r.height * 0.4f), 1.3f * u, t)
        // La tapa de bisagra, un poco levantada, con su pulgar.
        val tapa = Path().apply { moveTo(r.left - w * 0.002f, r.top + 1f * u); cubicTo(r.left + w * 0.01f, r.top - w * 0.014f, r.right - w * 0.01f, r.top - w * 0.016f, r.right + w * 0.002f, r.top - w * 0.004f); close() }
        drawPath(tapa, t.c(mezcla(PELTRE, LONA, 0.15f)))
        drawPath(tapa, t.s(0.3f), style = Stroke(0.8f * u))
        drawCircle(t.c(PELTRE), w * 0.005f, Offset(r.right + w * 0.004f, r.top - w * 0.008f))
    }

    /** La carta enrollada tumbada en la balda, con el compás de puntas abierto encima. [pie] es su extremo izquierdo, abajo. */
    private fun DrawScope.carta(pie: Offset) {
        val u = density
        val w = anchoW
        val g = w * 0.03f
        val a = Offset(pie.x, pie.y - g / 2); val b = Offset(pie.x + w * 0.2f, pie.y - g / 2 - w * 0.006f)
        val t = tn((a + b) / 2f, if (esNoche) 0f else 0.03f)
        contacto(t, Offset((a.x + b.x) / 2 + w * 0.01f, pie.y), w * 0.22f, 3.5f * u, 0.4f)
        sombraEnForro(Path().apply { addRect(Rect(a.x, a.y - g / 2, b.x, a.y + g / 2)) }, (a + b) / 2f, w * FONDO_T + w * 0.004f, 0.14f)
        rolloCarta(a, b, g, t, AZUL)
        // El compás de puntas: dos patas de hierro con su cabeza de latón.
        val cab = Offset(pie.x + w * 0.11f, pie.y - g - w * 0.012f)
        val p1 = Offset(pie.x + w * 0.05f, pie.y - w * 0.002f); val p2 = Offset(pie.x + w * 0.165f, pie.y - g * 0.4f)
        drawLine(t.s(0.3f), cab + Offset(2f * u, 2f * u), p1 + Offset(2f * u, 1f * u), 1.6f * u)
        drawLine(t.s(0.3f), cab + Offset(2f * u, 2f * u), p2 + Offset(2f * u, 1f * u), 1.6f * u)
        drawLine(t.c(HIERRO), cab, p1, 1.7f * u, StrokeCap.Round)
        drawLine(t.c(HIERRO), cab, p2, 1.7f * u, StrokeCap.Round)
        drawLine(t.b(0.5f), cab + Offset(-0.5f * u, 0f), p1 + Offset(-0.5f * u, 0f), 0.5f * u)
        drawCircle(t.c(BRONCE), w * 0.007f, cab)
        destello(cab - Offset(w * 0.002f, w * 0.002f), 0.9f * u, t)
    }

    /** La botella de vidrio verde con su tapón de corcho y su cordel, a medio beber. [pie] es el centro de su base. */
    private fun DrawScope.botella(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie - Offset(0f, w * 0.06f), if (esNoche) 0f else 0.03f)
        val hb = w * 0.075f; val rb = w * 0.024f
        val forma = Path().apply {
            moveTo(pie.x - rb, pie.y); lineTo(pie.x - rb, pie.y - hb)
            cubicTo(pie.x - rb, pie.y - hb - w * 0.02f, pie.x - w * 0.008f, pie.y - hb - w * 0.02f, pie.x - w * 0.008f, pie.y - hb - w * 0.035f)
            lineTo(pie.x - w * 0.008f, pie.y - hb - w * 0.06f); lineTo(pie.x + w * 0.008f, pie.y - hb - w * 0.06f)
            lineTo(pie.x + w * 0.008f, pie.y - hb - w * 0.035f)
            cubicTo(pie.x + w * 0.008f, pie.y - hb - w * 0.02f, pie.x + rb, pie.y - hb - w * 0.02f, pie.x + rb, pie.y - hb)
            lineTo(pie.x + rb, pie.y); close()
        }
        sombraEnForro(forma, pie - Offset(0f, w * 0.06f), w * FONDO_T + w * 0.004f, 0.16f)
        contacto(t, Offset(pie.x + w * 0.006f, pie.y), w * 0.06f, 3f * u, 0.4f)
        drawPath(forma, t.c(mezcla(BOTELLA, NOCHE, 0.25f)))
        clipPath(forma) {
            // Lo que queda dentro, más hondo, y el vidrio de arriba, más claro.
            drawRect(t.c(mezcla(BOTELLA, ALQUITRAN, 0.45f)), Offset(pie.x - rb, pie.y - hb * 0.55f), Size(rb * 2, hb * 0.55f))
            drawRect(t.c(mezcla(BOTELLA, LONA, 0.18f)).copy(alpha = 0.7f), Offset(pie.x - rb, pie.y - hb - w * 0.06f), Size(rb * 2, hb * 0.45f + w * 0.06f))
            drawLine(t.c(mezcla(BOTELLA, LONA, 0.4f)), Offset(pie.x - rb, pie.y - hb * 0.55f), Offset(pie.x + rb, pie.y - hb * 0.55f), 0.8f * u)
            drawRect(t.s(0.25f), Offset(pie.x + rb * 0.4f, pie.y - hb - w * 0.06f), Size(rb, hb + w * 0.06f))
            // Los reflejos del vidrio.
            drawRect(if (esNoche) LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.8f)) else luzMueble(0.75f), Offset(pie.x - rb * 0.62f, pie.y - hb + w * 0.004f), Size(1.6f * u, hb * 0.75f))
            drawRect(if (esNoche) LLAMA.copy(alpha = (0.5f * t.k).coerceAtMost(0.4f)) else luzMueble(0.35f), Offset(pie.x + rb * 0.55f, pie.y - hb * 0.8f), Size(1f * u, hb * 0.5f))
        }
        destello(Offset(pie.x - rb * 0.4f, pie.y - hb - w * 0.006f), 1.2f * u, t)
        // El corcho y su cordel.
        val corcho = Rect(pie.x - w * 0.0085f, pie.y - hb - w * 0.074f, pie.x + w * 0.0085f, pie.y - hb - w * 0.056f)
        drawRect(t.c(mezcla(CABO, OXIDO, 0.35f)), corcho.topLeft, corcho.size)
        drawRect(t.b(0.3f), corcho.topLeft, Size(corcho.width * 0.35f, corcho.height))
        for (k in 0 until 3) drawCircle(t.s(0.35f), 0.5f * u, Offset(corcho.left + corcho.width * (0.3f + 0.25f * k), corcho.top + corcho.height * (0.3f + 0.2f * k)))
        drawLine(t.c(CABO_OSC), Offset(pie.x - w * 0.009f, pie.y - hb - w * 0.05f), Offset(pie.x + w * 0.009f, pie.y - hb - w * 0.05f), 1.2f * u)
    }

    /**
     * El octante de madera apoyado en el forro: el sector de un octavo de círculo con sus dos radios y su travesaño, el limbo
     * con la escala de marfil, la alidada de latón y los dos espejitos. [pie] es el centro del limbo, donde pisa la balda.
     */
    private fun DrawScope.octante(pie: Offset) {
        val u = density
        val w = anchoW
        val R = w * 0.165f
        val apice = Offset(pie.x, pie.y - R * 1.02f)
        val t = tn(apice + Offset(0f, R * 0.5f), if (esNoche) 0f else 0.03f)
        fun en(ang: Float, r: Float) = Offset(apice.x + r * cos(Math.toRadians(ang.toDouble())).toFloat(), apice.y + r * sin(Math.toRadians(ang.toDouble())).toFloat())
        val a0 = 67.5f; val a1 = 112.5f
        val g = w * 0.0075f
        fun caja(cx: Float, r: Float) = Rect(cx - r, apice.y - r, cx + r, apice.y + r)
        /** Un listón de [p] a [q] de ancho [a], como polígono. */
        fun liston(p: Offset, q: Offset, a: Float): Path {
            val d = q - p; val l = d.getDistance().coerceAtLeast(1f); val n = Offset(-d.y / l, d.x / l) * (a / 2)
            return poligono(p.x + n.x, p.y + n.y, q.x + n.x, q.y + n.y, q.x - n.x, q.y - n.y, p.x - n.x, p.y - n.y)
        }
        rotate(-5f, pie) {
            // El limbo: un sector de corona, ancho.
            val limbo = Path().apply {
                arcTo(caja(apice.x, R * 1.04f), a0, a1 - a0, true)
                arcTo(caja(apice.x, R * 0.84f), a1, -(a1 - a0), false)
                close()
            }
            val radios = listOf(liston(apice, en(a0, R * 0.9f), g * 1.6f), liston(apice, en(a1, R * 0.9f), g * 1.6f), liston(en(a0, R * 0.5f), en(a1, R * 0.5f), g * 1.2f))
            val todo = Path().apply { addPath(limbo); radios.forEach { addPath(it) } }
            // La sombra en el forro.
            if (!esNoche) translate(w * 0.02f, w * 0.008f) { drawPath(todo, sombraMueble(0.22f)) }
            val madera = t.c(mezcla(ROBLE, ALQUITRAN, 0.2f))
            radios.forEach { r ->
                drawPath(r, madera)
                clipPath(r) { drawRect(t.b(0.3f), Offset(apice.x - R, apice.y), Size(R * 0.95f, R * 1.1f)) }
                drawPath(r, t.s(0.3f), style = Stroke(0.6f * u))
            }
            drawPath(limbo, madera)
            // La escala de marfil sobre el limbo, con sus rayas.
            val esc = Path().apply {
                arcTo(caja(apice.x, R * 1.0f), a0 + 2f, a1 - a0 - 4f, true)
                arcTo(caja(apice.x, R * 0.93f), a1 - 2f, -(a1 - a0 - 4f), false)
                close()
            }
            drawPath(esc, t.c(mezcla(LONA, CABO, 0.15f)))
            for (k in 0..20) {
                val a = a0 + 3f + (a1 - a0 - 6f) * k / 20
                drawLine(t.c(TINTA).copy(alpha = 0.75f), en(a, R * 0.93f), en(a, R * (if (k % 5 == 0) 0.995f else 0.965f)), 0.6f * u)
            }
            drawPath(limbo, t.s(0.35f), style = Stroke(0.7f * u))
            drawPath(Path().apply { arcTo(caja(apice.x, R * 1.04f), a0, a1 - a0, true) }, t.b(0.4f), style = Stroke(0.9f * u))
            // La alidada de latón, del ápice al limbo, con su nonio.
            val fin = en(83f, R * 1.0f)
            val alidada = liston(apice, fin, g * 1.1f)
            drawPath(alidada, t.c(BRONCE))
            drawPath(alidada, t.c(mezcla(BRONCE, OXIDO, 0.4f)), style = Stroke(0.5f * u))
            val nonio = liston(en(80f, R * 0.88f), en(86f, R * 0.88f), w * 0.014f)
            drawPath(nonio, t.c(BRONCE))
            destello(apice + (fin - apice) * 0.35f, 1.1f * u, t)
            // Los espejos: el grande en el ápice, en su marco, y el chico en el radio de la izquierda.
            val e1 = Rect(apice.x - w * 0.011f, apice.y - w * 0.02f, apice.x + w * 0.011f, apice.y + w * 0.002f)
            drawRect(t.c(BRONCE), Offset(e1.left - 1.2f * u, e1.top - 1.2f * u), Size(e1.width + 2.4f * u, e1.height + 2.4f * u))
            drawRect(t.c(mezcla(PELTRE, AZUL, 0.3f)), e1.topLeft, e1.size)
            drawRect(t.b(0.7f), e1.topLeft, Size(e1.width * 0.3f, e1.height))
            val esp = en(a1, R * 0.42f)
            drawRect(t.c(BRONCE), Offset(esp.x - w * 0.016f, esp.y - w * 0.011f), Size(w * 0.016f, w * 0.02f))
            drawRect(t.c(mezcla(PELTRE, AZUL, 0.3f)), Offset(esp.x - w * 0.014f, esp.y - w * 0.009f), Size(w * 0.012f, w * 0.016f))
            drawRect(t.b(0.6f), Offset(esp.x - w * 0.014f, esp.y - w * 0.009f), Size(w * 0.004f, w * 0.016f))
            // La pínula (la mira) en el otro radio.
            val pin = en(a0, R * 0.7f)
            drawRect(t.c(BRONCE), Offset(pin.x, pin.y - w * 0.012f), Size(w * 0.006f, w * 0.016f))
            drawCircle(t.c(BRONCE), w * 0.0045f, apice)
        }
    }

    /** El cuchillo de marinero clavado en la balda: la hoja hundida en la madera, la virola de latón y el mango de madera. */
    private fun DrawScope.cuchillo(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie - Offset(0f, w * 0.04f), if (esNoche) 0f else 0.03f)
        rotate(16f, pie) {
            val hoja = Rect(pie.x - w * 0.007f, pie.y - w * 0.03f, pie.x + w * 0.007f, pie.y)
            val mango = Rect(pie.x - w * 0.009f, hoja.top - w * 0.065f, pie.x + w * 0.009f, hoja.top - w * 0.007f)
            if (!esNoche) translate(w * 0.016f, w * 0.004f) { drawRect(sombraMueble(0.2f), mango.topLeft, Size(mango.width, mango.height + w * 0.03f)) }
            drawRect(t.c(mezcla(PELTRE, LONA, 0.2f)), hoja.topLeft, hoja.size)
            drawRect(t.s(0.25f), Offset(hoja.center.x, hoja.top), Size(hoja.width / 2, hoja.height))
            drawRect(t.b(0.6f), hoja.topLeft, Size(1f * u, hoja.height))
            drawRect(t.c(BRONCE), Offset(mango.left - 1f * u, mango.bottom), Size(mango.width + 2f * u, w * 0.006f))
            drawRoundRect(t.c(mezcla(ROBLE, ALQUITRAN, 0.3f)), mango.topLeft, mango.size, CornerRadius(w * 0.006f))
            clipRect(mango.left, mango.top, mango.right, mango.bottom) { cilindro(mango, t, 1.2f) }
            drawCircle(t.c(BRONCE), 1f * u, Offset(mango.center.x, mango.top + mango.height * 0.3f))
            drawCircle(t.c(BRONCE), 1f * u, Offset(mango.center.x, mango.top + mango.height * 0.7f))
        }
        // La raja en la madera donde entra la hoja.
        drawLine(t.s(0.55f), Offset(pie.x - w * 0.008f, pie.y + 0.5f * u), Offset(pie.x + w * 0.006f, pie.y + 0.5f * u), 1f * u)
    }

    /** El catalejo tumbado: el tubo forrado de cuero con su costura, los aros de latón, los tubos que se sacan y la lente. */
    private fun DrawScope.catalejo(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie - Offset(-w * 0.1f, w * 0.02f), if (esNoche) 0f else 0.03f)
        contacto(t, Offset(pie.x + w * 0.12f, pie.y), w * 0.24f, 3.5f * u, 0.42f)
        rotate(-4f, pie) {
            val d0 = w * 0.032f
            val tramos = listOf(Triple(0f, 0.11f, d0), Triple(0.11f, 0.175f, d0 * 0.78f), Triple(0.175f, 0.225f, d0 * 0.6f))
            tramos.forEachIndexed { i, (a, b, d) ->
                val r = Rect(pie.x + w * a, pie.y - d0 / 2 - d / 2, pie.x + w * b, pie.y - d0 / 2 + d / 2)
                if (!esNoche && i == 0) translate(w * 0.014f, w * 0.012f) { drawRect(sombraMueble(0.15f), r.topLeft, r.size) }
                drawRect(t.c(if (i == 0) mezcla(OXIDO, ALQUITRAN, 0.45f) else BRONCE), r.topLeft, r.size)
                clipRect(r.left, r.top, r.right, r.bottom) { tumbado(r, t, if (i == 0) 1f else 1.5f) }
                if (i == 0) {
                    // La costura del cuero.
                    drawLine(t.c(CABO).copy(alpha = 0.5f), Offset(r.left + w * 0.01f, r.center.y + d * 0.1f), Offset(r.right - w * 0.01f, r.center.y + d * 0.1f), 0.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.5f * u)))
                } else destello(Offset(r.left + r.width * 0.4f, r.top + d * 0.22f), 1f * u, t)
                // El aro de latón.
                val aro = Rect(r.right - w * 0.006f, r.top - 1f * u, r.right + w * 0.002f, r.bottom + 1f * u)
                drawRect(t.c(BRONCE), aro.topLeft, aro.size)
                drawRect(t.b(0.5f), aro.topLeft, Size(aro.width, 1f * u))
            }
            // El aro grande y la lente, a la izquierda.
            val ab = Rect(pie.x - w * 0.006f, pie.y - d0 - 1.5f * u, pie.x + w * 0.012f, pie.y + 1.5f * u)
            drawRect(t.c(BRONCE), ab.topLeft, ab.size)
            clipRect(ab.left, ab.top, ab.right, ab.bottom) { tumbado(ab, t, 1.5f) }
            drawOval(t.c(mezcla(AZUL, NOCHE, 0.35f)), Offset(pie.x - w * 0.01f, pie.y - d0), Size(w * 0.008f, d0 - 0.5f * u))
            destello(Offset(pie.x - w * 0.006f, pie.y - d0 * 0.7f), 1f * u, t)
        }
    }

    // ---------------------------------------------------------------- al pie

    /** El zócalo de la estiba, el plan con sus tablas, y lo de encima: lona, balas, cubo y los dos barriles con su charco. */
    private fun DrawScope.pie(d: Disposicion, plan: Float, alto: Float) {
        val u = density
        val w = anchoW
        val b = d.base
        val ult = d.filas.last()
        val z0 = ult.bottom + w * BALDA
        // El zócalo: tablas de pie que cierran la estiba.
        for (j in 0 until 14) {
            val xa = w * j / 14f; val xb = w * (j + 1) / 14f
            val t = tn(Offset((xa + xb) / 2, (z0 + plan) / 2), -0.04f)
            drawRect(t.c(mezcla(CASCO, ROBLE, 0.3f + 0.4f * azar(6200, j))), Offset(xa, z0), Size(xb - xa + 1f, plan - z0))
            drawRect(t.s(0.35f), Offset(xa, z0), Size(1.3f * u, plan - z0))
            drawRect(t.b(0.2f), Offset(xa + 1.3f * u, z0), Size(0.7f * u, plan - z0))
            clavo(Offset((xa + xb) / 2, z0 + (plan - z0) * 0.3f), 1f * u, t, azar(6201, j) > 0.5f, w * 0.015f)
        }
        val tz = tn(Offset(w * 0.5f, z0))
        drawRect(tz.s(0.35f), Offset(0f, z0), Size(w, w * 0.006f))
        // El plan: las tablas del piso, que se abren hacia quien mira, mojadas en el rincón.
        for (j in 0 until 6) {
            val ya = plan + (alto - plan) * (j / 6f).let { it * (0.6f + 0.4f * it) }
            val yb = plan + (alto - plan) * ((j + 1) / 6f).let { it * (0.6f + 0.4f * it) }
            for (i in 0 until 6) {
                val xa = w * i / 6f; val xb = w * (i + 1) / 6f + 1f
                val t = tn(Offset((xa + xb) / 2, (ya + yb) / 2), -0.06f)
                drawRect(t.c(mezcla(CASCO_OSC, CASCO, 0.25f + 0.4f * azar(6210, j * 7 + i))), Offset(xa, ya), Size(xb - xa, yb - ya))
                drawRect(t.s(0.4f), Offset(xa, ya), Size(xb - xa, 1.4f * u))
                drawRect(t.b(0.14f), Offset(xa, ya + 1.4f * u), Size(xb - xa, 0.8f * u))
            }
        }
        pinceladas(Rect(0f, plan, w, alto), if (esNoche) NOCHE else ALQUITRAN, 6220, 18, u, 0.12f)
        val tp = tn(Offset(w * 0.5f, plan))
        drawRect(tp.s(0.4f), Offset(0f, plan), Size(w, w * 0.01f))
        drawRect(tp.s(0.18f), Offset(0f, plan + w * 0.01f), Size(w, w * 0.02f))

        // ---- de atrás adelante
        val y0 = b.top
        barril(Rect(w * 0.84f, y0 + w * 0.045f, w * 1.04f, y0 + w * 0.24f), false, 6230)
        rolloLona(Rect(-w * 0.05f, y0 + w * 0.15f, w * 0.15f, y0 + w * 0.222f))
        balas(Offset(w * 0.22f, y0 + w * 0.275f))
        cubo(Offset(w * 0.47f, y0 + w * 0.255f))
        charco(Offset(w * 0.7f, y0 + w * 0.285f))
        barril(Rect(w * 0.66f, y0 + w * 0.06f, w * 0.86f, y0 + w * 0.262f), true, 6240)
    }

    /**
     * Un barril de roble con sus duelas y sus aros de hierro, la tapa con su chanfle vista desde arriba y, si [grifo], la
     * espita de madera con su llave y la gota que cae. [r] va del borde de la tapa al pie.
     */
    private fun DrawScope.barril(r: Rect, grifo: Boolean, s: Int) {
        val u = density
        val w = anchoW
        val t = tn(r.center, -0.03f)
        val panza = r.width * 0.06f
        val elip = r.width * 0.12f
        val cuerpo = Path().apply {
            moveTo(r.left + panza, r.top + elip / 2)
            cubicTo(r.left - panza * 0.6f, r.top + r.height * 0.3f, r.left - panza * 0.6f, r.bottom - r.height * 0.3f, r.left + panza, r.bottom - elip / 2)
            cubicTo(r.left + r.width * 0.3f, r.bottom + elip * 0.25f, r.right - r.width * 0.3f, r.bottom + elip * 0.25f, r.right - panza, r.bottom - elip / 2)
            cubicTo(r.right + panza * 0.6f, r.bottom - r.height * 0.3f, r.right + panza * 0.6f, r.top + r.height * 0.3f, r.right - panza, r.top + elip / 2)
            close()
        }
        contacto(t, Offset(r.center.x + w * 0.02f, r.bottom), r.width * 1.15f, w * 0.022f, 0.5f)
        // Su sombra en el zócalo.
        if (!esNoche) translate(w * 0.03f, -w * 0.004f) { clipRect(0f, 0f, size.width, r.bottom - elip) { drawPath(cuerpo, sombraMueble(0.22f)) } }
        drawPath(cuerpo, t.c(mezcla(ROBLE, CASCO, 0.3f)))
        clipPath(cuerpo) {
            // Las duelas.
            for (k in 1 until 8) {
                val f = k / 8f
                val x = r.left + r.width * f
                val bomba = (f - 0.5f) * panza * 1.6f
                val p = Path().apply { moveTo(x, r.top); cubicTo(x + bomba, r.top + r.height * 0.3f, x + bomba, r.bottom - r.height * 0.3f, x, r.bottom) }
                drawPath(p, t.s(0.3f), style = Stroke(1f * u))
                translate(1.1f * u, 0f) { drawPath(p, t.b(0.12f), style = Stroke(0.6f * u)) }
                if (k % 3 == 1) drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)).copy(alpha = 0.35f), Offset(x + 1.5f * u, r.top), Size(r.width / 8f - 3f * u, r.height))
            }
            cilindro(Rect(r.left - panza, r.top, r.right + panza, r.bottom), t, 1.4f, der = esNoche && r.center.x / w < LUZ.x)
            // Los aros.
            listOf(0.12f, 0.24f, 0.76f, 0.88f).forEach { f ->
                val y = r.top + r.height * f
                val p = Path().apply { moveTo(r.left - panza, y); cubicTo(r.left + r.width * 0.3f, y + elip * 0.5f, r.right - r.width * 0.3f, y + elip * 0.5f, r.right + panza, y) }
                drawPath(p, t.c(mezcla(HIERRO, OXIDO, 0.3f)), style = Stroke(w * 0.009f))
                translate(0f, -w * 0.003f) { drawPath(p, t.b(0.3f), style = Stroke(0.8f * u)) }
                translate(0f, w * 0.004f) { drawPath(p, t.s(0.3f), style = Stroke(0.8f * u)) }
            }
            // Chorreras de óxido de los aros.
            for (k in 0 until 4) {
                val x = r.left + r.width * (0.15f + 0.7f * azar(s, k))
                drawLine(t.c(OXIDO).copy(alpha = 0.3f), Offset(x, r.top + r.height * 0.26f), Offset(x + 1f * u, r.top + r.height * (0.36f + 0.12f * azar(s + 1, k))), 1.6f * u)
            }
        }
        // La tapa, vista desde arriba.
        val tapa = Rect(r.left + panza, r.top, r.right - panza, r.top + elip)
        drawOval(t.c(mezcla(ROBLE_LUZ, CASCO, 0.25f)), tapa.topLeft, tapa.size)
        clipPath(Path().apply { addOval(tapa) }) {
            for (k in 1 until 4) drawLine(t.s(0.3f), Offset(tapa.left + tapa.width * k / 4, tapa.top), Offset(tapa.left + tapa.width * k / 4, tapa.bottom), 0.8f * u)
            drawRect(t.b(0.25f), tapa.topLeft, Size(tapa.width * 0.35f, tapa.height))
            drawCircle(t.c(ALQUITRAN), 1.8f * u, Offset(tapa.center.x + tapa.width * 0.15f, tapa.center.y))
        }
        drawOval(t.c(mezcla(ROBLE, CASCO_OSC, 0.4f)), tapa.topLeft, tapa.size, style = Stroke(1.6f * u))
        drawArc(t.b(0.4f), 180f, 180f, false, tapa.topLeft, tapa.size, style = Stroke(0.8f * u))
        if (grifo) {
            // La espita de madera, abajo al frente, con su llave; y la gota.
            val e = Offset(r.center.x - r.width * 0.06f, r.bottom - r.height * 0.18f)
            val esp = Rect(e.x - w * 0.009f, e.y - w * 0.008f, e.x + w * 0.009f, e.y + w * 0.02f)
            drawRect(t.s(0.35f), Offset(esp.left + 1.5f * u, esp.top + 2f * u), esp.size)
            drawRoundRect(t.c(ROBLE_LUZ), esp.topLeft, esp.size, CornerRadius(2f * u))
            clipRect(esp.left, esp.top, esp.right, esp.bottom) { cilindro(esp, t, 1.3f) }
            drawRect(t.c(BRONCE), Offset(e.x - w * 0.016f, e.y - w * 0.014f), Size(w * 0.032f, w * 0.006f))
            destello(Offset(e.x - w * 0.012f, e.y - w * 0.012f), 0.9f * u, t)
            drawOval(t.c(ROBLE_LUZ), Offset(e.x - w * 0.006f, esp.bottom - 1f * u), Size(w * 0.012f, w * 0.006f))
            drawOval(t.c(mezcla(AZUL, LONA, 0.45f)).copy(alpha = 0.8f), Offset(e.x - 1.2f * u, esp.bottom + w * 0.012f), Size(2.4f * u, 3.4f * u))
        }
    }

    /** El charco bajo el grifo: agua oscura en las tablas, con el reflejo de la luz (de noche, el del farol). */
    private fun DrawScope.charco(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c, -0.03f)
        val forma = Path().apply {
            moveTo(c.x - w * 0.09f, c.y)
            cubicTo(c.x - w * 0.08f, c.y - w * 0.014f, c.x - w * 0.02f, c.y - w * 0.016f, c.x + w * 0.02f, c.y - w * 0.012f)
            cubicTo(c.x + w * 0.06f, c.y - w * 0.014f, c.x + w * 0.1f, c.y - w * 0.006f, c.x + w * 0.085f, c.y + w * 0.004f)
            cubicTo(c.x + w * 0.05f, c.y + w * 0.012f, c.x - w * 0.06f, c.y + w * 0.012f, c.x - w * 0.09f, c.y)
            close()
        }
        drawPath(forma, t.c(mezcla(ALQUITRAN, AZUL, 0.35f)).copy(alpha = 0.75f))
        clipPath(forma) {
            if (esNoche) {
                drawLine(LLAMA.copy(alpha = 0.3f), Offset(c.x - w * 0.02f, c.y - w * 0.004f), Offset(c.x + w * 0.035f, c.y - w * 0.004f), 1.6f * u)
                drawLine(Color(0xFFFFF0CF).copy(alpha = 0.6f), Offset(c.x, c.y - w * 0.004f), Offset(c.x + w * 0.015f, c.y - w * 0.004f), 0.9f * u)
            } else {
                drawLine(luzMueble(0.5f), Offset(c.x - w * 0.06f, c.y - w * 0.006f), Offset(c.x + w * 0.01f, c.y - w * 0.008f), 1.4f * u)
                drawLine(luzMueble(0.3f), Offset(c.x - w * 0.02f, c.y + w * 0.002f), Offset(c.x + w * 0.05f, c.y + w * 0.001f), 0.9f * u)
            }
        }
        drawPath(forma, if (esNoche) LLAMA.copy(alpha = 0.14f) else t.b(0.2f), style = Stroke(0.8f * u))
    }

    /** El rollo de lona tumbado, cortado por el borde: su luz, la espiral del canto y dos ligadas de cabo. */
    private fun DrawScope.rolloLona(r: Rect) {
        val u = density
        val w = anchoW
        val t = tn(r.center, -0.03f)
        contacto(t, Offset(r.center.x + w * 0.02f, r.bottom), r.width * 1.1f, w * 0.016f, 0.45f)
        drawRect(t.c(mezcla(LONA, LONA_OSC, 0.4f)), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            tumbado(r, t, 1.3f)
            listOf(0.3f, 0.65f).forEach { f -> drawLine(t.s(0.15f), Offset(r.left, r.top + r.height * f), Offset(r.right, r.top + r.height * (f + 0.05f)), 0.8f * u) }
        }
        val e = Rect(r.right - r.height * 0.22f, r.top, r.right + r.height * 0.22f, r.bottom)
        drawOval(t.c(mezcla(LONA, LUZ_MUEBLE, 0.1f)), e.topLeft, e.size)
        for (k in 1..3) {
            val f = k / 4f
            drawOval(t.s(0.3f), Offset(e.center.x - e.width * f / 2, e.center.y - e.height * f / 2), Size(e.width * f, e.height * f), style = Stroke(0.6f * u))
        }
        listOf(0.35f, 0.75f).forEach { f ->
            val x = r.left + r.width * f
            val p = Path().apply { moveTo(x, r.top - 1f * u); cubicTo(x + w * 0.008f, r.top + r.height * 0.4f, x + w * 0.008f, r.bottom - r.height * 0.3f, x + w * 0.002f, r.bottom + 1f * u) }
            cabo(p, 2.2f * u, t)
        }
    }

    /** Las balas de cañón apiladas en su chillera de madera: la pirámide de cuatro, tres, dos y una, cada una con su luz. */
    private fun DrawScope.balas(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie, -0.03f)
        val rb = w * 0.022f
        // La chillera: una tabla con su canto y sus tacos.
        val ch = Rect(pie.x - rb * 5f, pie.y - w * 0.022f, pie.x + rb * 5f, pie.y)
        contacto(t, Offset(pie.x + w * 0.02f, pie.y), ch.width * 1.1f, w * 0.018f, 0.5f)
        drawRect(t.c(mezcla(ROBLE, CASCO, 0.2f)), ch.topLeft, ch.size)
        drawRect(t.b(0.4f), ch.topLeft, Size(ch.width, 1.1f * u))
        drawRect(t.s(0.3f), Offset(ch.left, ch.bottom - ch.height * 0.3f), Size(ch.width, ch.height * 0.3f))
        vetas(ch, t.s(0.3f), 6250, 0.6f, grosor = 0.5f)
        listOf(0.08f, 0.92f).forEach { f -> clavo(Offset(ch.left + ch.width * f, ch.center.y), 1.1f * u, t) }
        val filas = listOf(4, 3, 2, 1)
        var y = ch.top - rb * 0.6f
        filas.forEachIndexed { j, n ->
            for (i in 0 until n) {
                val c = Offset(pie.x + (i - (n - 1) / 2f) * rb * 2.05f, y)
                drawCircle(t.s(0.35f), rb, c + Offset(rb * 0.25f, rb * 0.2f))
                drawCircle(t.c(mezcla(HIERRO, ALQUITRAN, 0.2f)), rb, c)
                drawCircle(t.c(mezcla(HIERRO, LONA, 0.15f)), rb * 0.7f, c - Offset(rb * 0.22f, rb * 0.22f))
                drawCircle(t.c(mezcla(HIERRO, ALQUITRAN, 0.2f)), rb * 0.55f, c - Offset(rb * 0.05f, rb * 0.05f))
                drawCircle(t.c(OXIDO).copy(alpha = 0.35f), rb * 0.25f, c + Offset(rb * (0.3f * azar(6260, j * 5 + i) - 0.1f), rb * 0.35f))
                destello(c - Offset(rb * 0.45f, rb * 0.45f), rb * 0.14f, t)
            }
            y -= rb * 1.72f
        }
    }

    /** El cubo de duelas con dos aros, el asa de cabo y agua dentro. [pie] es el centro de su base. */
    private fun DrawScope.cubo(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(pie, -0.03f)
        val hb = w * 0.07f; val ra = w * 0.045f; val rp = w * 0.036f
        val boca = Rect(pie.x - ra, pie.y - hb - w * 0.008f, pie.x + ra, pie.y - hb + w * 0.012f)
        val cuerpo = poligono(pie.x - ra, pie.y - hb + w * 0.002f, pie.x + ra, pie.y - hb + w * 0.002f, pie.x + rp, pie.y, pie.x - rp, pie.y)
        contacto(t, Offset(pie.x + w * 0.012f, pie.y), w * 0.1f, w * 0.014f, 0.45f)
        // El asa, detrás.
        val asa = Path().apply { moveTo(pie.x - ra, pie.y - hb + w * 0.004f); cubicTo(pie.x - ra * 0.8f, pie.y - hb - w * 0.06f, pie.x + ra * 0.8f, pie.y - hb - w * 0.06f, pie.x + ra, pie.y - hb + w * 0.004f) }
        cabo(asa, 2f * u, t)
        drawPath(cuerpo, t.c(mezcla(ROBLE, ROBLE_LUZ, 0.25f)))
        clipPath(cuerpo) {
            for (k in 1 until 6) drawLine(t.s(0.3f), Offset(pie.x - ra + 2 * ra * k / 6, pie.y - hb), Offset(pie.x - rp + 2 * rp * k / 6, pie.y), 0.9f * u)
            cilindro(Rect(pie.x - ra, pie.y - hb, pie.x + ra, pie.y), t, 1.3f)
            listOf(0.25f, 0.8f).forEach { f -> drawRect(t.c(mezcla(HIERRO, OXIDO, 0.3f)), Offset(pie.x - ra, pie.y - hb + hb * f), Size(ra * 2, w * 0.006f)); drawRect(t.b(0.3f), Offset(pie.x - ra, pie.y - hb + hb * f), Size(ra * 2, 0.7f * u)) }
        }
        drawOval(t.c(mezcla(ROBLE, CASCO_OSC, 0.3f)), boca.topLeft, boca.size)
        drawOval(t.c(mezcla(ALQUITRAN, AZUL, 0.4f)), Offset(boca.left + 2f * u, boca.top + 2f * u), Size(boca.width - 4f * u, boca.height - 3f * u))
        drawLine(if (esNoche) LLAMA.copy(alpha = 0.25f) else luzMueble(0.45f), Offset(boca.left + boca.width * 0.25f, boca.center.y), Offset(boca.left + boca.width * 0.55f, boca.center.y - 0.5f * u), 1f * u)
    }

    /**
     * La luz de la escotilla, de día: el haz que baja de arriba a la izquierda, con su polvo, y la cuadrícula del enjaretado
     * (la reja de la escotilla) dibujada en luz sobre el forro y el coy.
     */
    private fun DrawScope.escotilla(w: Float) {
        val u = density
        val haz = poligono(0f, 0f, w * 0.2f, 0f, w * 0.48f, w * 0.5f, w * 0.13f, w * 0.47f)
        drawPath(haz, luzMueble(0.07f))
        // El polvo en el haz.
        for (k in 0 until 26) {
            val f = azar(6300, k); val g = azar(6301, k)
            val p = Offset(w * (0.03f + 0.2f * g + 0.3f * f), w * 0.48f * f)
            drawCircle(luzMueble(0.25f + 0.35f * azar(6302, k)), (0.5f + 0.8f * azar(6303, k)) * u, p)
        }
        // La cuadrícula: cuatro por tres claros, sesgados, con los barrotes en medio.
        val A = Offset(w * 0.14f, w * 0.135f); val B = Offset(w * 0.45f, w * 0.18f)
        val D = Offset(w * 0.1f, w * 0.43f)
        val nx = 3; val ny = 3
        val bar = 0.09f
        for (i in 0 until nx) for (j in 0 until ny) {
            fun q(fx: Float, fy: Float) = A + (B - A) * fx + (D - A) * fy
            val x0 = (i + bar) / nx; val x1 = (i + 1 - bar) / nx
            val y0 = (j + bar) / ny; val y1 = (j + 1 - bar) / ny
            val p0 = q(x0, y0); val p1 = q(x1, y0); val p2 = q(x1, y1); val p3 = q(x0, y1)
            val a = 0.1f + 0.04f * azar(6310, i * 3 + j)
            drawPath(poligono(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y), luzMueble(a))
        }
    }

    /** Una pizarrita o tablilla en [r]: 0, pizarra con marco de pino y dos clavos; 1, tablilla embreada; 2, pizarra colgada de un cordel. */
    private fun DrawScope.pizarra(r: Rect, t: Tono, tipo: Int, cordel: Offset? = null, escrita: Boolean = true) {
        val u = density
        val m = 3.2f * u
        if (tipo == 2 && cordel != null) {
            val a = Offset(r.left + r.width * 0.12f, r.top - m * 0.5f); val b = Offset(r.right - r.width * 0.12f, r.top - m * 0.5f)
            listOf(a, b).forEach { p -> val c = Path().apply { moveTo(p.x, p.y); lineTo(cordel.x, cordel.y) }; drawPath(c, t.c(CABO_OSC), style = Stroke(1.2f * u)); drawPath(c, t.c(CABO).copy(alpha = 0.6f), style = Stroke(0.5f * u)) }
        }
        drawRect(t.s(0.32f), Offset(r.left - m + 2f * u, r.top - m + 2.5f * u), Size(r.width + 2 * m, r.height + 2 * m))
        if (tipo == 1) {
            // La tablilla embreada, de canto gastado.
            val tab = poligono(r.left - m, r.top - m * 0.6f, r.right + m * 0.8f, r.top - m, r.right + m, r.bottom + m * 0.8f, r.left - m * 0.7f, r.bottom + m)
            drawPath(tab, t.c(mezcla(ALQUITRAN, CASCO_OSC, 0.25f)))
            clipPath(tab) {
                vetas(Rect(r.left - m, r.top - m, r.right + m, r.bottom + m), t.c(CASCO).copy(alpha = 0.35f), 6400, 0.6f, grosor = 0.5f)
                drawRect(t.b(0.12f), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, m))
            }
            drawPath(tab, t.c(mezcla(CASCO, ROBLE_LUZ, 0.4f)), style = Stroke(0.9f * u))
        } else {
            drawRect(t.c(mezcla(ROBLE_LUZ, LONA, 0.15f)), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, r.height + 2 * m))
            drawRect(t.b(0.45f), Offset(r.left - m, r.top - m), Size(r.width + 2 * m, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left - m, r.bottom + m - 1.2f * u), Size(r.width + 2 * m, 1.2f * u))
            drawRect(t.c(PIZARRA), r.topLeft, r.size)
            drawRect(t.s(0.3f), r.topLeft, Size(r.width, 1.2f * u))
            // Lo borrado de la tiza.
            clipRect(r.left, r.top, r.right, r.bottom) {
                for (k in 0 until (if (escrita) 2 else 4)) {
                    val x = r.left + r.width * azar(6410, k + tipo * 7)
                    drawOval(TIZA.copy(alpha = if (escrita) 0.045f else 0.09f), Offset(x - r.width * 0.12f, r.top + r.height * (0.1f + 0.5f * azar(6411, k))), Size(r.width * 0.3f, r.height * 0.35f))
                }
            }
        }
        if (tipo != 2) listOf(Offset(r.left - m * 0.5f, r.top - m * 0.5f), Offset(r.right + m * 0.5f, r.top - m * 0.5f)).forEach { clavo(it, 1.2f * u, t) }
    }

    // ---------------------------------------------------------------- cada compartimento

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        val aw = anchoH(); val ah = altoH()
        val dentro = c.cols == cols
        val W = w / aw
        val ox = if (dentro) (lado + c.col * (aw + hueco)) * W else 0f
        val oy = if (dentro) (corona + hueco + c.fila * (ah + hueco)) * W else 0f
        val centro = Offset(ox / W + aw / 2, oy / W + ah / 2)
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.06f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f) - 0.02f
        val t = Tono(oscuro, k, dia)
        // De noche, hacia dónde cae la sombra de lo guardado (lejos del farol); de día, a la derecha.
        val dx = if (!oscuro) 1f else if (dentro) ((centro.x - LUZ.x) / 0.3f).coerceIn(-1f, 1f) else 1f
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        fondoHueco(w, h, W, ox, oy, t, c, oscuro, dentro)
        if (c.vacio) { vacio(w, h, c, t); barrote(w, h, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cajon(w, h, c, t, dx)
                else -> when (Math.floorMod(m ushr 3, 3)) {
                    0 -> cofre(w, h, c, m, t, dx, der)
                    1 -> barrilete(w, h, c, m, t, dx, der)
                    else -> fardo(w, h, c, m, t, dx, der)
                }
            }
        }
        barrote(w, h, t)
    }

    /**
     * El fondo de un hueco: el forro (sus tracas casan con las del mueble), la sombra de la balda de arriba en escalones, la
     * sombra de la cuaderna (de día a la izquierda; de noche, la larga de la cuaderna del medio en la columna izquierda) y la
     * cara de arriba de la balda, donde pisa lo guardado.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, W: Float, ox: Float, oy: Float, t: Tono, c: EstadoCajon, oscuro: Boolean, dentro: Boolean) {
        val u = density
        val piso = h * PISO
        forro(W, ox, oy, Rect(0f, 0f, w, piso + 1f), 2) { t }
        listOf(0.3f to 0.07f, 0.17f to 0.1f, 0.07f to 0.2f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        if (!oscuro) {
            drawRect(t.s(0.24f), Offset.Zero, Size(W * 0.018f, piso))
            drawRect(t.s(0.1f), Offset(W * 0.018f, 0f), Size(W * 0.014f, piso))
        } else if (dentro && c.col == 0) {
            val a = (0.75f * t.k).coerceAtMost(0.55f)
            drawPath(poligono(w - W * 0.05f, 0f, w, 0f, w, piso, w - W * 0.058f, piso), NOCHE.copy(alpha = a))
        }
        // La cara de arriba de la balda, con su veta y la junta con el forro.
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.55f)), Offset(0f, piso), Size(w, h - piso))
        vetas(Rect(0f, piso, w, h), t.c(CASCO_OSC).copy(alpha = 0.3f), 6500 + c.fila, 0.5f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.4f), Offset(0f, piso), Size(w, 1.4f * u))
        drawRect(t.s(0.15f), Offset(0f, piso + 1.4f * u), Size(w, 2f * u))
    }

    /** El barrote de mar del hueco, por delante de lo guardado, con su sombra debajo. */
    private fun DrawScope.barrote(w: Float, h: Float, t: Tono) {
        val u = density
        val y = h * BARR; val g = h * BARR_G
        drawRect(t.s(0.25f), Offset(0f, y + g), Size(w, g * 0.8f))
        drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)), Offset(0f, y), Size(w, g))
        drawRect(t.b(0.45f), Offset(0f, y), Size(w, g * 0.3f))
        drawRect(t.s(0.25f), Offset(0f, y + g * 0.72f), Size(w, g * 0.28f))
        if (t.o) drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), Offset(0f, y), Size(w, 1f * u))
    }

    /** La sombra de lo guardado en el forro, hacia [dx] (de noche, apartándose del farol), sin bajar de la balda. */
    private fun DrawScope.sombraGuardado(p: Path, w: Float, h: Float, t: Tono, dx: Float) {
        val a = if (!t.o) 0.2f else (0.65f * t.k).coerceAtMost(0.5f)
        if (a < 0.03f) return
        clipRect(0f, 0f, w, h * PISO) {
            translate(w * 0.07f * dx, h * 0.03f) { drawPath(p, if (t.o) NOCHE.copy(alpha = a) else sombraMueble(a)) }
        }
    }

    /** El filo cálido del farol en lo que mira hacia él, de noche: el canto de arriba y el costado de [der]. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** Las cartas enrolladas (y alguna moneda) que asoman de [x0]..[x1] desde [boca]: más y más altas cuanto más guarda. */
    private fun DrawScope.cartas(x0: Float, x1: Float, boca: Float, h: Float, c: EstadoCajon, t: Tono, monedas: Boolean = true) {
        val u = density
        val n = c.n.coerceIn(0, 12)
        val cuantas = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.08f + 0.016f * n)
        val g = h * 0.075f
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val x = x0 + (x1 - x0) * (0.1f + 0.8f * f) + (x1 - x0) * 0.06f * (azar(c.semilla, i) - 0.5f)
            val incl = (azar(c.semilla, i + 10) - 0.5f) * 0.5f + (f - 0.5f) * 0.5f
            val alto = sube * (0.6f + 0.4f * azar(c.semilla, i + 20))
            val a = Offset(x, boca + h * 0.06f)
            val b = Offset(x + alto * incl, boca - alto)
            val papel = if (i % 3 == 1) mezcla(PERGAMINO, LONA_OSC, 0.4f) else PERGAMINO
            val cinta = when (i % 3) { 0 -> BERMELLON; 1 -> AZUL; else -> CABO_OSC }
            rolloCarta(a, b, g * (0.85f + 0.3f * azar(c.semilla, i + 30)), t, cinta, papel)
        }
        if (monedas && n >= 3) {
            val cm = (n / 3).coerceIn(1, 3)
            for (i in 0 until cm) {
                val x = x0 + (x1 - x0) * (0.15f + 0.7f * azar(c.semilla, i + 40))
                moneda(Offset(x, boca - h * 0.01f - h * 0.02f * i), h * 0.034f, t, i == 1)
            }
        }
        if (n == 0) drawLine(t.s(0.3f), Offset(x0, boca), Offset(x1, boca), 1f * u)
    }

    /**
     * El cofre de marino: el cuerpo pintado (verde, rojo o azul gastado), más ancho abajo, con sus tablas, desconchones donde
     * asoma la madera y rozaduras; la tapa, que se levanta más cuanto más guarda, con las cartas y las monedas en la rendija;
     * las cantoneras de latón, las gazas de cabo con sus tojinos a los lados, la aldaba con el candado y la pizarrita clavada.
     */
    private fun DrawScope.cofre(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, dx: Float, der: Boolean) {
        val u = density
        val pintura = when (Math.floorMod(m ushr 5, 3)) { 0 -> VERDE; 1 -> BERMELLON; else -> AZUL }
        val yT = h * 0.4f; val yB = h * 0.965f
        val cuerpo = poligono(w * 0.11f, yT, w * 0.89f, yT, w * 0.94f, yB, w * 0.06f, yB)
        val abre = if (c.n > 0) h * (0.02f + 0.011f * c.n.coerceAtMost(8)) else 0f
        val tapa = Rect(w * 0.085f, yT - abre - h * 0.1f, w * 0.915f, yT - abre)
        sombraGuardado(Path().apply { addPath(cuerpo); addRect(tapa) }, w, h, t, dx)
        contacto(t, Offset(w * 0.53f, yB), w * 0.98f, h * 0.06f, 0.45f)
        // La rendija: lo oscuro de dentro, las cartas y las monedas.
        if (abre > 0f) {
            drawRect(t.c(mezcla(ALQUITRAN, NOCHE, 0.3f)), Offset(w * 0.12f, tapa.bottom - 1f * u), Size(w * 0.76f, abre + 2f * u))
            clipRect(0f, -h, w, yT) { cartas(w * 0.14f, w * 0.86f, yT, h, c, t) }
        }
        // El cuerpo pintado.
        drawPath(cuerpo, t.c(pintura))
        clipPath(cuerpo) {
            // Las tablas.
            listOf(0.36f, 0.68f).forEach { f -> val y = yT + (yB - yT) * f; drawRect(t.s(0.3f), Offset(0f, y), Size(w, 1.2f * u)); drawRect(t.b(0.18f), Offset(0f, y + 1.2f * u), Size(w, 0.7f * u)) }
            vetas(Rect(0f, yT, w, yB), t.s(0.18f), c.semilla, 0.5f, grosor = 0.5f, ondulacion = 0.4f)
            // La luz: de día, el costado izquierdo claro y el derecho hondo; de noche, hacia el farol.
            if (!t.o) { drawRect(t.b(0.16f), Offset(0f, yT), Size(w * 0.22f, yB - yT)); drawRect(t.s(0.22f), Offset(w * 0.8f, yT), Size(w * 0.2f, yB - yT)) }
            else if (der) drawRect(t.b(0.22f), Offset(w * 0.78f, yT), Size(w * 0.22f, yB - yT))
            else drawRect(t.b(0.18f), Offset(0f, yT), Size(w * 0.2f, yB - yT))
            // Los desconchones: la madera que asoma bajo la pintura.
            for (k in 0 until 5) {
                val cx = w * (if (k % 2 == 0) 0.1f + 0.15f * azar(m, k) else 0.7f + 0.2f * azar(m, k))
                val cy = yT + (yB - yT) * (0.08f + 0.85f * azar(m + 1, k))
                val rx = w * (0.025f + 0.035f * azar(m + 2, k))
                drawOval(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.4f)), Offset(cx - rx, cy - rx * 0.35f), Size(rx * 2, rx * 0.7f))
                drawOval(t.c(mezcla(pintura, LONA, 0.3f)), Offset(cx - rx, cy - rx * 0.35f), Size(rx * 2, rx * 0.7f), style = Stroke(0.6f * u))
            }
            // Rozaduras.
            for (k in 0 until 4) {
                val y = yT + (yB - yT) * (0.15f + 0.8f * azar(m + 3, k)); val x = w * (0.15f + 0.6f * azar(m + 4, k))
                drawLine(t.c(mezcla(pintura, LONA, 0.45f)), Offset(x, y), Offset(x + w * 0.08f, y + h * 0.01f), 0.6f * u)
            }
        }
        drawRect(t.s(0.3f), Offset(w * 0.86f, yT), Size(w * 0.02f, yB - yT))
        filo(Rect(w * 0.11f, yT, w * 0.89f, yB), t, der)
        // La tapa: su cara de arriba (vista apenas), su frente y su sombra sobre el cuerpo.
        drawRect(t.s(0.35f), Offset(w * 0.11f, tapa.bottom), Size(w * 0.78f, h * 0.025f))
        drawRect(t.c(mezcla(pintura, ALQUITRAN, 0.18f)), tapa.topLeft, tapa.size)
        drawRect(t.c(mezcla(pintura, LUZ_MUEBLE, if (t.o) 0.05f else 0.18f)), tapa.topLeft, Size(tapa.width, tapa.height * 0.32f))
        drawRect(t.b(0.4f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(t.s(0.3f), Offset(tapa.left, tapa.bottom - 1.4f * u), Size(tapa.width, 1.4f * u))
        filo(tapa, t, der)
        // Las cantoneras de latón: en las esquinas del cuerpo y de la tapa.
        fun cantonera(p: Offset, sx: Float, sy: Float) {
            val l = w * 0.065f
            val pl = poligono(p.x, p.y, p.x + sx * l, p.y, p.x + sx * l * 0.55f, p.y + sy * l * 0.32f, p.x + sx * l * 0.2f, p.y + sy * l * 0.32f, p.x + sx * l * 0.2f, p.y + sy * l * 0.8f, p.x, p.y + sy * l)
            drawPath(pl, t.s(0.3f), style = Stroke(1.6f * u))
            drawPath(pl, t.c(BRONCE))
            drawPath(pl, t.c(mezcla(BRONCE, OXIDO, 0.4f)), style = Stroke(0.6f * u))
            clavo(Offset(p.x + sx * l * 0.12f, p.y + sy * l * 0.15f), 0.9f * u, t)
            destello(Offset(p.x + sx * l * 0.4f, p.y + sy * l * 0.1f), 0.9f * u, t)
        }
        cantonera(Offset(w * 0.11f, yT + 1f * u), 1f, 1f); cantonera(Offset(w * 0.89f, yT + 1f * u), -1f, 1f)
        cantonera(Offset(w * 0.06f, yB), 1f, -1f); cantonera(Offset(w * 0.94f, yB), -1f, -1f)
        cantonera(Offset(tapa.left, tapa.top), 1f, 1f); cantonera(Offset(tapa.right, tapa.top), -1f, 1f)
        // Las gazas de cabo con sus tojinos.
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) w * 0.085f else w * 0.915f
            val y = h * 0.52f
            drawRect(t.s(0.3f), Offset(x - w * 0.018f + 1.2f * u, y - h * 0.035f + 1.5f * u), Size(w * 0.036f, h * 0.07f))
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.3f)), Offset(x - w * 0.018f, y - h * 0.035f), Size(w * 0.036f, h * 0.07f))
            drawRect(t.b(0.35f), Offset(x - w * 0.018f, y - h * 0.035f), Size(w * 0.036f, 1f * u))
            val gaza = Path().apply { moveTo(x, y - h * 0.03f); cubicTo(x + s * w * 0.08f, y - h * 0.02f, x + s * w * 0.085f, y + h * 0.12f, x + s * w * 0.005f, y + h * 0.05f) }
            translate(1.5f * u, 2f * u) { drawPath(gaza, t.s(0.25f), style = Stroke(3f * u)) }
            cabo(gaza, 3f * u, t)
        }
        // La aldaba y el candado de hierro.
        val ca = Offset(w * 0.5f, tapa.bottom + h * 0.02f)
        drawRect(t.c(mezcla(HIERRO, OXIDO, 0.2f)), Offset(ca.x - w * 0.016f, tapa.bottom - h * 0.05f), Size(w * 0.032f, h * 0.06f))
        drawArc(t.c(HIERRO), 180f, 180f, false, Offset(ca.x - w * 0.022f, ca.y - h * 0.04f), Size(w * 0.044f, h * 0.06f), style = Stroke(1.8f * u))
        val cand = Rect(ca.x - w * 0.034f, ca.y - h * 0.012f, ca.x + w * 0.034f, ca.y + h * 0.075f)
        drawRoundRect(t.s(0.35f), Offset(cand.left + 1.2f * u, cand.top + 1.5f * u), cand.size, CornerRadius(w * 0.012f))
        drawRoundRect(t.c(mezcla(HIERRO, OXIDO, 0.25f)), cand.topLeft, cand.size, CornerRadius(w * 0.012f))
        clipRect(cand.left, cand.top, cand.right, cand.bottom) { cilindro(cand, t, 1.2f) }
        drawCircle(t.c(ALQUITRAN), 1.2f * u, Offset(ca.x, ca.y + h * 0.035f))
        drawLine(t.c(ALQUITRAN), Offset(ca.x, ca.y + h * 0.035f), Offset(ca.x, ca.y + h * 0.055f), 1f * u)
        destello(Offset(cand.left + cand.width * 0.25f, cand.top + 2f * u), 0.9f * u, t)
        pizarra(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, 0)
    }

    /**
     * El barrilete abierto: la tapa quitada, apoyada detrás contra el forro; las duelas con su panza, los aros (de hierro o de
     * avellano según la semilla), la boca con las cartas y las monedas, y la tablilla embreada colgada de un cordel.
     */
    private fun DrawScope.barrilete(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, dx: Float, der: Boolean) {
        val u = density
        val yT = h * 0.33f; val yB = h * 0.965f
        val xT = w * 0.15f; val panza = w * 0.07f
        val ry = h * 0.05f
        val cuerpo = Path().apply {
            moveTo(xT, yT)
            cubicTo(xT - panza * 1.3f, yT + (yB - yT) * 0.3f, xT - panza * 1.3f, yB - (yB - yT) * 0.3f, xT, yB)
            lineTo(w - xT, yB)
            cubicTo(w - xT + panza * 1.3f, yB - (yB - yT) * 0.3f, w - xT + panza * 1.3f, yT + (yB - yT) * 0.3f, w - xT, yT)
            cubicTo(w - xT, yT + ry * 1.3f, xT, yT + ry * 1.3f, xT, yT)
            close()
        }
        // La tapa quitada, apoyada detrás contra el forro.
        val tapa = Rect(w * 0.01f, h * 0.16f, w * 0.34f, h * 0.62f)
        rotate(-10f, Offset(tapa.center.x, tapa.bottom)) {
            sombraGuardado(Path().apply { addOval(tapa) }, w, h, t, dx * 0.5f)
            drawOval(t.c(mezcla(ROBLE, CASCO, 0.25f)), tapa.topLeft, tapa.size)
            clipPath(Path().apply { addOval(tapa) }) {
                listOf(0.3f, 0.55f, 0.78f).forEach { f -> drawLine(t.s(0.35f), Offset(tapa.left + tapa.width * f, tapa.top), Offset(tapa.left + tapa.width * f, tapa.bottom), 1f * u) }
                drawRect(t.b(0.2f), tapa.topLeft, Size(tapa.width * 0.25f, tapa.height))
                drawRect(t.s(0.25f), Offset(tapa.left + tapa.width * 0.7f, tapa.top), Size(tapa.width * 0.3f, tapa.height))
            }
            drawOval(t.c(mezcla(ROBLE, CASCO_OSC, 0.4f)), tapa.topLeft, tapa.size, style = Stroke(1.6f * u))
        }
        sombraGuardado(cuerpo, w, h, t, dx)
        contacto(t, Offset(w * 0.53f, yB), w * 0.9f, h * 0.06f, 0.45f)
        // La boca: el borde de atrás, lo oscuro, lo que asoma.
        val boca = Rect(xT, yT - ry, w - xT, yT + ry)
        drawOval(t.c(mezcla(ROBLE_LUZ, CASCO, 0.2f)), boca.topLeft, boca.size)
        drawOval(t.c(mezcla(ALQUITRAN, NOCHE, 0.3f)), Offset(boca.left + 3f * u, boca.top + 2.5f * u), Size(boca.width - 6f * u, boca.height - 3f * u))
        cartas(xT + w * 0.04f, w - xT - w * 0.04f, yT + ry * 0.3f, h, c, t)
        // El cuerpo.
        val madera = mezcla(ROBLE, if (Math.floorMod(m ushr 4, 2) == 0) ROBLE_LUZ else CASCO, 0.3f)
        drawPath(cuerpo, t.c(madera))
        val aroHierro = Math.floorMod(m ushr 6, 4) != 0
        clipPath(cuerpo) {
            for (k in 0 until 7) {
                val f0 = k / 7f; val f1 = (k + 1) / 7f
                val xa = xT + (w - 2 * xT) * f0; val xb = xT + (w - 2 * xT) * f1
                val ba = (f0 - 0.5f) * panza * 2.2f; val bb = (f1 - 0.5f) * panza * 2.2f
                val duela = Path().apply {
                    moveTo(xa, yT); cubicTo(xa + ba, yT + (yB - yT) * 0.3f, xa + ba, yB - (yB - yT) * 0.3f, xa, yB)
                    lineTo(xb, yB); cubicTo(xb + bb, yB - (yB - yT) * 0.3f, xb + bb, yT + (yB - yT) * 0.3f, xb, yT); close()
                }
                drawPath(duela, t.c(mezcla(madera, if (k % 2 == 0) ROBLE_LUZ else CASCO_OSC, 0.12f + 0.12f * azar(m, k))))
            }
            for (k in 1 until 7) {
                val f = k / 7f
                val x = xT + (w - 2 * xT) * f
                val bomba = (f - 0.5f) * panza * 2.2f
                val p = Path().apply { moveTo(x, yT); cubicTo(x + bomba, yT + (yB - yT) * 0.3f, x + bomba, yB - (yB - yT) * 0.3f, x, yB) }
                drawPath(p, t.s(0.35f), style = Stroke(1.1f * u))
            }
            if (!t.o) { drawRect(t.b(0.18f), Offset(0f, yT), Size(w * 0.28f, yB - yT)); drawRect(t.s(0.24f), Offset(w * 0.74f, yT), Size(w * 0.26f, yB - yT)) }
            else if (der) drawRect(t.b(0.22f), Offset(w * 0.72f, yT), Size(w * 0.28f, yB - yT))
            else drawRect(t.b(0.18f), Offset(0f, yT), Size(w * 0.26f, yB - yT))
            // Los aros.
            listOf(0.06f, 0.15f, 0.9f).forEach { f ->
                val y = yT + (yB - yT) * f
                val p = Path().apply { moveTo(0f, y); cubicTo(w * 0.3f, y + ry * 1.1f, w * 0.7f, y + ry * 1.1f, w, y) }
                if (aroHierro) {
                    drawPath(p, t.c(mezcla(HIERRO, OXIDO, 0.3f)), style = Stroke(h * 0.035f))
                    translate(0f, -h * 0.012f) { drawPath(p, t.b(0.3f), style = Stroke(0.8f * u)) }
                } else {
                    drawPath(p, t.c(mezcla(CABO_OSC, ROBLE, 0.4f)), style = Stroke(h * 0.04f))
                    translate(0f, h * 0.012f) { drawPath(p, t.s(0.3f), style = Stroke(1f * u)) }
                    translate(0f, -h * 0.013f) { drawPath(p, t.b(0.25f), style = Stroke(0.7f * u)) }
                }
            }
        }
        // El borde de delante de la boca, con su luz.
        drawArc(t.c(mezcla(ROBLE_LUZ, LONA, 0.15f)), 0f, 180f, false, boca.topLeft, boca.size, style = Stroke(2.2f * u))
        drawArc(t.b(0.5f), 20f, 140f, false, boca.topLeft, boca.size, style = Stroke(0.8f * u))
        filo(Rect(xT, yT, w - xT, yB), t, der)
        // La tablilla embreada, colgada de un clavo del aro.
        val clv = Offset(w * 0.5f, yT + (yB - yT) * 0.1f)
        pizarra(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, 1)
        drawPath(Path().apply { moveTo(w * rotulo.left + w * 0.1f, h * rotulo.top - 2f * u); lineTo(clv.x, clv.y); lineTo(w * rotulo.right - w * 0.1f, h * rotulo.top - 2f * u) }, t.c(CABO_OSC), style = Stroke(1.2f * u))
        clavo(clv, 1.4f * u, t)
    }

    /**
     * El fardo de lona encerada: blando, con su brillo de cera y sus pliegues, la costura con sus puntadas, atado en cruz con
     * cabo (los cabos lo ciñen), y la boca fruncida por donde asoman las cartas; la pizarrita, colgada del cabo.
     */
    private fun DrawScope.fardo(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, dx: Float, der: Boolean) {
        val u = density
        val lona = when (Math.floorMod(m ushr 5, 3)) { 0 -> mezcla(LONA_OSC, CABO, 0.3f); 1 -> mezcla(LONA_OSC, OXIDO, 0.2f); else -> mezcla(LONA_OSC, AZUL, 0.18f) }
        val yT = h * 0.27f; val yB = h * 0.965f
        val forma = Path().apply {
            moveTo(w * 0.1f, yB)
            cubicTo(w * 0.02f, yB - h * 0.05f, w * 0.03f, yT + h * 0.12f, w * 0.1f, yT + h * 0.04f)
            cubicTo(w * 0.2f, yT - h * 0.03f, w * 0.34f, yT + h * 0.02f, w * 0.42f, yT + h * 0.03f)
            cubicTo(w * 0.47f, yT + h * 0.06f, w * 0.53f, yT + h * 0.06f, w * 0.58f, yT + h * 0.03f)
            cubicTo(w * 0.68f, yT + h * 0.01f, w * 0.82f, yT - h * 0.04f, w * 0.91f, yT + h * 0.05f)
            cubicTo(w * 0.98f, yT + h * 0.13f, w * 0.98f, yB - h * 0.05f, w * 0.9f, yB)
            close()
        }
        sombraGuardado(forma, w, h, t, dx)
        contacto(t, Offset(w * 0.53f, yB), w * 0.95f, h * 0.06f, 0.45f)
        // Lo que asoma por la boca fruncida.
        clipRect(0f, -h, w, yT + h * 0.06f) { cartas(w * 0.36f, w * 0.64f, yT + h * 0.07f, h, c, t, monedas = false) }
        drawPath(forma, t.c(lona))
        clipPath(forma) {
            // La luz blanda del bulto.
            if (!t.o) { drawOval(t.b(0.2f), Offset(w * 0.02f, yT), Size(w * 0.5f, (yB - yT) * 0.7f)); drawOval(t.s(0.2f), Offset(w * 0.55f, yT + h * 0.2f), Size(w * 0.55f, yB - yT)) }
            else if (der) drawOval(t.b(0.25f), Offset(w * 0.5f, yT), Size(w * 0.55f, (yB - yT) * 0.8f))
            else drawOval(t.b(0.2f), Offset(w * 0.0f, yT), Size(w * 0.5f, (yB - yT) * 0.7f))
            drawRect(t.s(0.22f), Offset(0f, yB - h * 0.08f), Size(w, h * 0.08f))
            // Los pliegues: desde donde aprietan los cabos.
            listOf(Triple(0.3f, 0.41f, 1f), Triple(0.7f, 0.41f, -1f)).forEach { (fx, fy, s) ->
                for (k in 0 until 3) {
                    val p0 = Offset(w * fx, h * fy)
                    val p = Path().apply { moveTo(p0.x, p0.y); cubicTo(p0.x + s * w * (0.06f + 0.03f * k), p0.y + h * 0.06f, p0.x + s * w * (0.1f + 0.05f * k), p0.y + h * (0.12f + 0.08f * k), p0.x + s * w * (0.12f + 0.06f * k), p0.y + h * (0.2f + 0.1f * k)) }
                    drawPath(p, t.s(0.22f), style = Stroke(1.6f * u))
                    translate(-s * 1.4f * u, 0f) { drawPath(p, t.b(0.22f), style = Stroke(0.8f * u)) }
                }
            }
            // El brillo de la cera.
            for (k in 0 until 4) drawOval(t.b(0.18f), Offset(w * (0.12f + 0.2f * k), yT + h * (0.1f + 0.25f * azar(m, k))), Size(w * 0.08f, h * 0.025f))
            // La costura con sus puntadas.
            val cx = w * (0.17f + 0.04f * azar(m, 9))
            drawLine(t.s(0.35f), Offset(cx, yT), Offset(cx + w * 0.02f, yB), 1f * u)
            for (k in 0 until 9) {
                val y = yT + (yB - yT) * (k + 0.5f) / 9; val x = cx + w * 0.02f * (y - yT) / (yB - yT)
                drawLine(t.c(mezcla(CABO, LONA, 0.4f)), Offset(x - w * 0.012f, y - h * 0.008f), Offset(x + w * 0.012f, y + h * 0.008f), 0.9f * u)
            }
        }
        filo(Rect(w * 0.06f, yT, w * 0.94f, yB), t, der)
        // Los cabos que lo atan: dos de arriba abajo y uno de lado a lado, con sus nudos.
        listOf(0.3f, 0.7f).forEach { f ->
            val x = w * f
            val p = Path().apply { moveTo(x - w * 0.02f, yT + h * 0.02f); cubicTo(x, yT + h * 0.2f, x + w * 0.01f, yB - h * 0.2f, x - w * 0.005f, yB + 1f * u) }
            cabo(p, 2.6f * u, t)
        }
        val yc = h * 0.41f
        val cenido = Path().apply { moveTo(w * 0.03f, yc - h * 0.01f); cubicTo(w * 0.3f, yc + h * 0.025f, w * 0.7f, yc + h * 0.025f, w * 0.97f, yc - h * 0.01f) }
        translate(0f, 1.8f * u) { drawPath(cenido, t.s(0.3f), style = Stroke(3.6f * u)) }
        cabo(cenido, 2.8f * u, t)
        listOf(0.3f, 0.7f).forEach { f ->
            val p = Offset(w * f, yc + h * 0.016f)
            drawCircle(t.s(0.3f), 2.8f * u, p + Offset(1f * u, 1.2f * u))
            drawCircle(t.c(CABO_OSC), 2.6f * u, p)
            drawCircle(t.c(CABO), 1.5f * u, p - Offset(0.8f * u, 0.8f * u))
        }
        // La pizarrita, colgada del cabo.
        pizarra(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, 2, Offset(w * 0.5f, yc + h * 0.02f))
    }

    /**
     * Lo que no tiene tema: un cajón de fruta de tablillas, con los pliegos sueltos de cualquier modo (uno caído por delante),
     * una carta enrollada encima y la pizarrita sin escribir.
     */
    private fun DrawScope.cajon(w: Float, h: Float, c: EstadoCajon, t: Tono, dx: Float) {
        val u = density
        val yT = h * 0.44f; val yB = h * 0.965f
        val caja = Rect(w * 0.07f, yT, w * 0.93f, yB)
        sombraGuardado(Path().apply { addRect(caja) }, w, h, t, dx)
        contacto(t, Offset(w * 0.53f, yB), w * 0.95f, h * 0.06f, 0.45f)
        // Lo de dentro: oscuro, y los pliegos de cualquier modo.
        drawRect(t.c(mezcla(ALQUITRAN, NOCHE, 0.2f)), caja.topLeft, caja.size)
        val n = c.n.coerceIn(0, 12)
        val sueltos = (2 + n / 2).coerceIn(2, 6)
        for (i in 0 until sueltos) {
            val x = caja.left + caja.width * (0.05f + 0.75f * azar(c.semilla, i + 60))
            val y = yT - h * (0.02f + 0.06f * azar(c.semilla, i + 61))
            val g = (azar(c.semilla, i + 62) - 0.5f) * 24f
            rotate(g, Offset(x + w * 0.1f, y + h * 0.05f)) {
                drawRect(t.s(0.25f), Offset(x + 1.5f * u, y + 2f * u), Size(w * 0.22f, h * 0.14f))
                drawRect(t.c(if (i % 2 == 0) PERGAMINO else mezcla(PERGAMINO, LONA_OSC, 0.4f)), Offset(x, y), Size(w * 0.22f, h * 0.14f))
                drawRect(t.b(0.4f), Offset(x, y), Size(w * 0.22f, 0.9f * u))
                drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(x + w * 0.03f, y + h * 0.05f), Offset(x + w * 0.17f, y + h * 0.04f), 0.6f * u)
            }
        }
        if (n >= 2) rolloCarta(Offset(caja.left + caja.width * 0.45f, yT - h * 0.02f), Offset(caja.left + caja.width * 0.95f, yT - h * 0.1f), h * 0.07f, t, CABO_OSC)
        // Las tablillas del cajón, con huecos entre ellas.
        listOf(0f to 0.3f, 0.42f to 0.7f, 0.8f to 1f).forEach { (a, b) ->
            val r = Rect(caja.left, yT + (yB - yT) * a, caja.right, yT + (yB - yT) * b)
            drawRect(t.s(0.3f), Offset(r.left, r.bottom), Size(r.width, 1.5f * u))
            drawRect(t.c(mezcla(ROBLE_LUZ, LONA, 0.25f)), r.topLeft, r.size)
            vetas(r, t.c(ROBLE).copy(alpha = 0.4f), c.semilla + (a * 10).toInt(), 0.7f, grosor = 0.5f)
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.2f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            listOf(0.05f, 0.95f).forEach { fx -> clavo(Offset(r.left + r.width * fx, r.center.y), 0.9f * u, t) }
        }
        // Los postes de las esquinas.
        listOf(caja.left, caja.right - w * 0.05f).forEach { x ->
            drawRect(t.c(mezcla(ROBLE, ROBLE_LUZ, 0.4f)), Offset(x, yT - h * 0.02f), Size(w * 0.05f, yB - yT + h * 0.02f))
            drawRect(t.b(0.3f), Offset(x, yT - h * 0.02f), Size(w * 0.015f, yB - yT))
            drawRect(t.s(0.3f), Offset(x + w * 0.035f, yT - h * 0.02f), Size(w * 0.015f, yB - yT))
        }
        // Un pliego caído por delante, apoyado en la tabla.
        rotate(-12f, Offset(w * 0.86f, yB)) {
            drawRect(t.s(0.25f), Offset(w * 0.8f + 1.5f * u, yB - h * 0.2f + 1.5f * u), Size(w * 0.14f, h * 0.2f))
            drawRect(t.c(PERGAMINO), Offset(w * 0.8f, yB - h * 0.2f), Size(w * 0.14f, h * 0.2f))
            drawRect(t.s(0.15f), Offset(w * 0.89f, yB - h * 0.2f), Size(w * 0.05f, h * 0.2f))
        }
        pizarra(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, 0, escrita = false)
    }

    /** Un hueco libre: un cabo suelto en la balda y, según la semilla, una caracola o una botella volcada con su corcho al lado. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val m = mezclar(c.semilla)
        val y = h * 0.965f
        // El cabo suelto, en eses sobre la tabla.
        val cabo1 = Path().apply {
            moveTo(-w * 0.02f, y - h * 0.02f)
            cubicTo(w * 0.15f, y - h * 0.06f, w * 0.2f, y + h * 0.01f, w * 0.32f, y - h * 0.02f)
            cubicTo(w * 0.42f, y - h * 0.05f, w * 0.34f, y - h * 0.11f, w * 0.26f, y - h * 0.07f)
            cubicTo(w * 0.2f, y - h * 0.04f, w * 0.3f, y + h * 0.005f, w * 0.45f, y - h * 0.01f)
        }
        translate(1.5f * u, 2f * u) { drawPath(cabo1, t.s(0.3f), style = Stroke(3.2f * u)) }
        cabo(cabo1, 3f * u, t)
        if (Math.floorMod(m, 2) == 0) {
            // La caracola tumbada: la espira con sus nudos hacia la izquierda, la vuelta grande y el labio rosado abierto.
            val cc = Offset(w * 0.64f, y - h * 0.17f)
            val r = h * 0.13f
            contacto(t, Offset(cc.x + w * 0.02f, y), w * 0.36f, h * 0.05f, 0.45f)
            val concha = Path().apply {
                moveTo(cc.x - r * 2.4f, cc.y - r * 0.15f)
                cubicTo(cc.x - r * 1.6f, cc.y - r * 0.7f, cc.x - r * 0.6f, cc.y - r * 1.15f, cc.x + r * 0.5f, cc.y - r * 1.05f)
                cubicTo(cc.x + r * 1.3f, cc.y - r * 0.95f, cc.x + r * 1.9f, cc.y - r * 0.2f, cc.x + r * 2.3f, cc.y + r * 0.3f)
                cubicTo(cc.x + r * 1.6f, cc.y + r * 0.85f, cc.x + r * 0.2f, cc.y + r * 1.0f, cc.x - r * 0.6f, cc.y + r * 0.7f)
                cubicTo(cc.x - r * 1.3f, cc.y + r * 0.45f, cc.x - r * 2.0f, cc.y + r * 0.2f, cc.x - r * 2.4f, cc.y - r * 0.15f)
                close()
            }
            drawPath(concha, t.c(mezcla(LONA, CABO, 0.35f)))
            clipPath(concha) {
                drawRect(t.s(0.25f), Offset(cc.x - r * 2.5f, cc.y + r * 0.2f), Size(r * 5f, r))
                drawRect(t.b(0.3f), Offset(cc.x - r * 2.5f, cc.y - r * 1.2f), Size(r * 5f, r * 0.5f))
                // Las vueltas de la espira y las bandas pardas.
                for (k in 0 until 4) {
                    val x = cc.x - r * (2.1f - 0.5f * k)
                    drawLine(t.c(mezcla(OXIDO, LONA, 0.3f)), Offset(x, cc.y - r * 1.2f), Offset(x + r * 0.45f, cc.y + r * 1f), 1.3f * u)
                    drawLine(t.b(0.3f), Offset(x + 1.4f * u, cc.y - r * 1.2f), Offset(x + r * 0.45f + 1.4f * u, cc.y + r * 1f), 0.6f * u)
                }
                listOf(-0.2f, 0.3f).forEach { f -> drawLine(t.c(mezcla(OXIDO, LONA, 0.5f)).copy(alpha = 0.5f), Offset(cc.x - r * 0.5f, cc.y + r * f), Offset(cc.x + r * 2f, cc.y + r * (f + 0.3f)), r * 0.12f) }
            }
            // Los nudos de arriba.
            for (k in 0 until 5) {
                val p = Offset(cc.x - r * (1.6f - 0.55f * k), cc.y - r * (0.62f + 0.1f * k.coerceAtMost(3)))
                drawCircle(t.s(0.3f), r * 0.17f, p + Offset(r * 0.06f, r * 0.08f))
                drawCircle(t.c(mezcla(LONA, CABO, 0.25f)), r * 0.16f, p)
                drawCircle(t.b(0.45f), r * 0.07f, p - Offset(r * 0.05f, r * 0.05f))
            }
            // El labio rosado, abierto hacia quien mira, y la boca honda.
            val labio = Path().apply {
                moveTo(cc.x - r * 0.3f, cc.y + r * 0.75f)
                cubicTo(cc.x - r * 0.1f, cc.y - r * 0.2f, cc.x + r * 1.2f, cc.y - r * 0.55f, cc.x + r * 2.2f, cc.y + r * 0.25f)
                cubicTo(cc.x + r * 1.6f, cc.y + r * 0.95f, cc.x + r * 0.4f, cc.y + r * 1.05f, cc.x - r * 0.3f, cc.y + r * 0.75f)
                close()
            }
            drawPath(labio, t.c(mezcla(BERMELLON, LONA, 0.55f)))
            drawOval(t.c(mezcla(BERMELLON, ALQUITRAN, 0.45f)), Offset(cc.x + r * 0.3f, cc.y + r * 0.15f), Size(r * 1.3f, r * 0.5f))
            drawPath(labio, t.b(0.4f), style = Stroke(0.9f * u))
            destello(Offset(cc.x + r * 1.1f, cc.y - r * 0.05f), 1.1f * u, t)
        } else {
            // La botella volcada, con su corcho al lado.
            val a = Offset(w * 0.52f, y - h * 0.08f)
            val l = w * 0.32f; val g = h * 0.15f
            contacto(t, Offset(a.x + l * 0.45f, y), l * 1.1f, h * 0.05f, 0.45f)
            val cuerpo = Path().apply {
                addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(a.x, a.y - g / 2, a.x + l * 0.62f, a.y + g / 2), CornerRadius(g * 0.2f)))
                addPath(poligono(a.x + l * 0.6f, a.y - g / 2, a.x + l * 0.75f, a.y - g * 0.18f, a.x + l * 0.75f, a.y + g * 0.18f, a.x + l * 0.6f, a.y + g / 2))
                addRect(Rect(a.x + l * 0.74f, a.y - g * 0.17f, a.x + l * 0.9f, a.y + g * 0.17f))
            }
            drawPath(cuerpo, t.c(mezcla(BOTELLA, NOCHE, 0.2f)))
            clipPath(cuerpo) {
                drawRect(t.c(mezcla(BOTELLA, LONA, 0.2f)).copy(alpha = 0.6f), Offset(a.x, a.y - g / 2), Size(l, g * 0.4f))
                drawRect(t.s(0.25f), Offset(a.x, a.y + g * 0.15f), Size(l, g * 0.35f))
                drawRect(if (t.o) LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.8f)) else luzMueble(0.7f), Offset(a.x + l * 0.06f, a.y - g * 0.32f), Size(l * 0.45f, 1.5f * u))
            }
            destello(Offset(a.x + l * 0.15f, a.y - g * 0.3f), 1.1f * u, t)
            val co = Offset(w * 0.93f, y - h * 0.035f)
            drawRect(t.c(mezcla(CABO, OXIDO, 0.35f)), Offset(co.x - w * 0.02f, co.y - h * 0.03f), Size(w * 0.04f, h * 0.06f))
            drawRect(t.b(0.3f), Offset(co.x - w * 0.02f, co.y - h * 0.03f), Size(w * 0.04f, 1f * u))
        }
    }

    override val rotulo = Rect(0.08f, 0.5f, 0.92f, 0.76f)
    override fun tintaRotulo(oscuro: Boolean) = TIZA
    override val letraRotulo: FontFamily = LetraExtra.caslon
    override val salida = -SALIDA
    override val placa = Rect(0.37f, 0.02f, 0.63f, 0.18f)
    override fun tintaPlaca(oscuro: Boolean) = TIZA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Pino embreado, de tablas verticales.
        drawRect(t(PINO_OSC, oscuro))
        val n = 6
        for (k in 0 until n) {
            val r = Rect(w * k / n, 0f, w * (k + 1) / n, h)
            drawRect(t(mezcla(PINO_OSC, PINO, 0.2f + 0.4f * azar(17, k)), oscuro), r.topLeft, r.size)
            vetas(r, Color.Black.copy(alpha = 0.3f), 30 + k, 0.8f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
            drawLine(t(BREA, oscuro), Offset(r.left, 0f), Offset(r.left, h), 1.6f * u)
        }
        // Un barco pintado en la tapa, desvaído.
        val b = Offset(w / 2, h * 0.2f)
        val pintura = TIZA.copy(alpha = 0.16f)
        drawPath(Path().apply { moveTo(b.x - 80 * u, b.y); lineTo(b.x + 90 * u, b.y); lineTo(b.x + 70 * u, b.y + 22 * u); lineTo(b.x - 66 * u, b.y + 22 * u); close() }, pintura)
        listOf(-40f, 10f, 55f).forEachIndexed { i, dx ->
            val alto = (if (i == 1) 110f else 86f) * u
            drawLine(pintura, Offset(b.x + dx * u, b.y), Offset(b.x + dx * u, b.y - alto), 2 * u)
            for (j in 0 until 3) drawRect(pintura, Offset(b.x + dx * u - 22 * u + j * 3 * u, b.y - alto + 10 * u + j * 30 * u), Size(44 * u - j * 6 * u, 24 * u))
        }
        var ox = 0f
        while (ox < w) { drawArc(pintura, 0f, 180f, false, Offset(ox, b.y + 26 * u), Size(24 * u, 10 * u), style = Stroke(1.6f * u)); ox += 24 * u }
        // Las paredes del cofre, con sus cantoneras.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(VERDE_OSC, oscuro), r.topLeft, r.size) }
        escuadrasPared(w, h, pl, 40 * u, t(LATON, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.35f)
    }

    override fun tintaInterior(oscuro: Boolean) = PERGAMINO
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 30, 0.008f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(PERGAMINO.copy(alpha = 0.45f))
        // Los rumbos: dieciséis líneas desde un centro, en negro, verde y rojo, como en un portulano.
        val c = Offset(w * (0.5f + 0.25f * azar(semilla, 1)), h * (0.45f + 0.25f * azar(semilla, 2)))
        val l = maxOf(w, h) * 1.5f
        for (k in 0 until 16) {
            val a = k * Math.PI / 8
            val col = when (k % 4) { 0 -> TINTA; 2 -> RUMBO; else -> ROJO }
            drawLine(col.copy(alpha = 0.13f), c, Offset(c.x + l * cos(a).toFloat(), c.y + l * sin(a).toFloat()), 0.8f * u)
        }
        drawPath(estrella(c, 9 * u * g, 8, 0.35f), TINTA.copy(alpha = 0.3f))
        drawCircle(TINTA.copy(alpha = 0.25f), 6 * u * g, c, style = Stroke(0.6f * u))
        // Un trozo de costa por la izquierda, con sus nombres en rayitas.
        val costa = Path().apply {
            moveTo(0f, h * 0.15f)
            var y = h * 0.15f; var k = 0
            while (y < h * 0.9f) { y += 10 * u * g; lineTo(w * (0.04f + 0.1f * azar(semilla, k + 10)), y); k++ }
            lineTo(0f, h * 0.9f)
        }
        drawPath(costa, Color(0xFF8A6A3A).copy(alpha = 0.3f), style = Stroke(1.2f * u))
        for (k in 0 until 5) {
            val y = h * (0.22f + 0.14f * k)
            drawLine(ROJO.copy(alpha = 0.25f), Offset(w * 0.12f, y), Offset(w * (0.18f + 0.06f * azar(semilla, k + 30)), y - 3 * u), 0.8f * u)
        }
    }

    override val margenes = MargenesFicha(13.dp, 14.dp, 13.dp, 26.dp, 28.dp, 28.dp)
    override val proporcionFicha = 1.05f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.75f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // Una moneda de a ocho.
            "hecho" -> selloHecho(TINTA, p.color)
            // Un ancla.
            "convence" -> {
                drawCircle(TINTA, s * 0.1f, Offset(c.x, c.y - s * 0.36f), style = Stroke(s * 0.07f))
                drawLine(TINTA, Offset(c.x, c.y - s * 0.26f), Offset(c.x, c.y + s * 0.4f), s * 0.09f)
                drawLine(TINTA, Offset(c.x - s * 0.22f, c.y - s * 0.16f), Offset(c.x + s * 0.22f, c.y - s * 0.16f), s * 0.08f)
                drawArc(TINTA, 20f, 140f, false, Offset(c.x - s * 0.36f, c.y - s * 0.1f), Size(s * 0.72f, s * 0.52f), style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
            // Una brújula con la aguja al norte.
            "piensa" -> {
                drawCircle(TINTA, s * 0.38f, c, style = Stroke(s * 0.07f))
                drawPath(Path().apply { moveTo(c.x, c.y - s * 0.3f); lineTo(c.x + s * 0.09f, c.y); lineTo(c.x - s * 0.09f, c.y); close() }, ROJO)
                drawPath(Path().apply { moveTo(c.x, c.y + s * 0.3f); lineTo(c.x + s * 0.09f, c.y); lineTo(c.x - s * 0.09f, c.y); close() }, TINTA)
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** Un puñal clavado. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawPath(Path().apply { moveTo(c.x - s * 0.07f, c.y - s * 0.05f); lineTo(c.x + s * 0.07f, c.y - s * 0.05f); lineTo(c.x, c.y + s * 0.5f); close() }, Color(0xFFB8BCC0))
        drawLine(LATON, Offset(c.x - s * 0.24f, c.y - s * 0.08f), Offset(c.x + s * 0.24f, c.y - s * 0.08f), s * 0.09f, StrokeCap.Round)
        drawLine(PINO_OSC, Offset(c.x, c.y - s * 0.12f), Offset(c.x, c.y - s * 0.42f), s * 0.12f, StrokeCap.Round)
        drawCircle(LATON, s * 0.07f, Offset(c.x, c.y - s * 0.46f))
    }

    override val desorden = 2f
}
