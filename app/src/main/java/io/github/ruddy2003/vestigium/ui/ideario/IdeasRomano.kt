package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.foundation.shape.GenericShape
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
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.Pagina
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
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

/**
 * Roma: el armarium del tablinum, hacia el 110 d. C., en la casa de un veterano o en el cuarto del escribiente del campamento
 * del Danubio. Es un interior sin ventana: un muro pintado al fresco por zonas (el friso negro con una guirnalda pintada, el
 * paño rojo pompeyano con su doble filete ocre y el zócalo negro jaspeado), con desconchones donde asoma el revoque y el
 * ladrillo, y un piso de cocciopesto con teselas blancas; la luz de día entra de arriba a la izquierda, fuera del cuadro. El
 * mueble es el armario de nogal: la cornisa con su tabla, las ovas y dardos, los dentículos y el astrágalo; el friso con
 * rosetas talladas en metopas rehundidas; un frontón bajo con un clípeo en el tímpano; los marcos moldurados de cada anaquel;
 * las dos hojas abiertas en escorzo, con sus bisagras de hueso, la cerradura de bronce y la argolla; el zócalo con su tabula
 * ansata (la placa de «y N más») y los pies torneados. Encima, el punto focal a la izquierda: el casco de legionario, de
 * hierro con ribetes de bronce, su guardanuca y sus carrilleras, junto a un rollo a medio leer cuyo pliego cae por el canto de
 * la cornisa; a la derecha del frontón, el tintero doble de bronce con sus cálamos, la bolsa de monedas con dos monedas
 * sueltas y un dado de hueso, y el candelabro de trípode con la lucerna encima. En la pared, una corona de laurel seca colgada
 * de un clavo con su cinta. Al pie, el escabel con unas tablillas de cera abiertas y el estilo, y el scrinium cilíndrico con
 * su tapa y su correa, cortado por el borde derecho. Cada tema es, según la semilla, una capsa de cuero (natural, teñida de
 * rojo o de nogal) con sus aros de bronce y los rollos de pie, y su tapa apoyada al lado; un haz de rollos tumbados atados,
 * vistos por sus cabos, con sus etiquetas (sillyboi) de colores colgando; o un fajo de tablillas de cera atado con cordel y
 * sellado, con un díptico abierto de pie detrás. Más rollos o más tablillas cuanto más guarda. El tema va en una tabula ansata
 * de bronce claro. Lo que no tiene tema es un cesto de mimbre con rollos sueltos; un hueco libre lleva un tintero volcado con
 * su mancha o una lucerna apagada. Al abrir uno, lo que guarda sube. De noche todo se hunde en un violeta de vino: alumbra
 * solo la lucerna (halo cálido en el fresco, el rojo encendido cerca, la corona, el brillo en el bronce del casco y del
 * candelabro); los anaqueles de arriba a la derecha, encendidos, y los de abajo a la izquierda, casi perdidos. Por dentro,
 * una pared al fresco. Las fichas son rollos: la hoja de papiro entre sus dos varillas con perillas.
 */
object IdeasRomano : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta (sale del cuadro de Cayo)
    /** El fresco: el rojo pompeyano del paño, el negro del friso y del zócalo, el ocre de los filetes, el revoque. */
    private val ROJO_FRESCO = Color(0xFFA03B2A)
    private val POMPEYANO = Color(0xFF8E2B1F)
    private val NEGRO = Color(0xFF1E1714)
    private val FILETE = Color(0xFFC9A54A)
    private val REVOQUE = Color(0xFFDDD2B8)
    /** El nogal del armario: su luz y su sombra. */
    private val NOGAL = Color(0xFF5E3C22)
    private val NOGAL_LUZ = Color(0xFF8C6340)
    private val NOGAL_OSC = Color(0xFF331F10)
    /** El boj claro de las tablillas y del escabel. */
    private val BOJ = Color(0xFFC6A46C)
    /** El bronce, su luz y el latón claro de las placas. */
    private val BRONCE = Color(0xFFB07A3A)
    private val BRONCE_LUZ = Color(0xFFE2B866)
    private val LATON = Color(0xFFD9B878)
    /** El hierro del casco. */
    private val HIERRO = Color(0xFF74726F)
    /** El cuero de las capsas, del scrinium y de la bolsa. */
    private val CUERO = Color(0xFFA0703F)
    private val CUERO_OSC = Color(0xFF5E3A20)
    /** El papiro, el hueso de las bisagras y de las perillas, la cera negra. */
    private val PAPIRO = Color(0xFFE9D9A9)
    private val PAPIRO_OSC = Color(0xFFB59B62)
    private val HUESO = Color(0xFFE6DCC4)
    private val CERA = Color(0xFF2E2622)
    /** El rojo del ejército (cintas, etiquetas, lacre), el laurel y el barro del piso. */
    private val ROJO = Color(0xFFA8382A)
    private val LAUREL = Color(0xFF5E7A3A)
    private val OCRE = Color(0xFFC98B3A)
    private val BARRO = Color(0xFF9E6248)
    private val TINTA = Color(0xFF2A1A0C)
    /** La llama de la lucerna y la noche del tablinum: violeta de vino. */
    private val LLAMA = Color(0xFFFFC56E)
    private val NOCHE = Color(0xFF150F18)
    /**
     * La placa de noche lejos de la lucerna: con [TINTA] da unos 6:1 en el título y 4.5:1 en la cuenta; cerca de la llama sube hacia [LATON].
     */
    private val PLACA_LEJOS = Color(0xFFB09A72)
    /** Las del interior y las fichas (sin cambios). */
    private val LACRE = Color(0xFFA3172E)
    private val PURPURA = Color(0xFF6E3458)
    private val AZAFRAN = Color(0xFFD6A23E)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.4f
    override val corona = 0.66f
    override val base = 0.3f
    override val lado = 0.105f
    override val hueco = 0.04f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona, en fracciones del ancho: la tabla de la cornisa (atrás y adelante), su frente y lo bajo del friso. */
    private const val TABLA = 0.462f
    private const val FRENTE = 0.495f
    private const val CANTO = 0.585f
    private const val FRISO = 0.668f
    /** Donde pisan las cosas de encima: atrás y adelante de la tabla. */
    private const val PIE_A = 0.474f
    private const val PIE_F = 0.489f
    /** En la base, en fracción de su alto: el fin del cuerpo, el fin del zócalo, el rincón con el piso, donde pisan los pies. */
    private const val CUERPO_FIN = 0.07f
    private const val ZOCALO_FIN = 0.3f
    private const val SUELO = 0.4f
    private const val PISAN = 0.56f
    /** Los costados del cuerpo, en fracción del ancho. */
    private const val IZQ = 0.075f
    private const val DER = 0.925f
    /** El marco de cada hueco, en fracción del ancho. */
    private const val MARCO = 0.011f
    /** El piso de cada hueco y cuánto sube lo que guarda al abrirlo, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val SALIDA = 0.07f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la lucerna, en fracciones del ancho. */
    private val LUZ = Offset(0.856f, 0.16f)

    /** Cuánto le llega de la lucerna a un punto [p] (en fracciones del ancho). */
    private fun luz(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.8f else 1f)
        val radial = 1.35f * exp(-3.1f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que cae sobre el frente del armario, de fila en fila: la primera, encendida; la segunda, a medias; la tercera,
     * tenue; la cuarta y la quinta, casi perdidas; menos en la columna de la izquierda, más lejos de la lucerna. Los puntos son
     * los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.92f, 0.85f to 0.84f, 1.16f to 0.56f, 1.47f to 0.3f, 1.78f to 0.13f, 2.09f to 0.055f, 2.4f to 0.035f, 2.8f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.55f * (abs(p.x - LUZ.x) - 0.08f).coerceAtLeast(0f)).coerceIn(0.68f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.14f + NOCHE.red * 0.8f, c.green * 0.14f + NOCHE.green * 0.8f, c.blue * 0.14f + NOCHE.blue * 0.8f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día tal cual (con [dia], más claro); de noche, de su hondo a su color según [k]. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.16f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del cuarto. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))

    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** De noche la luz viene de la lucerna: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la lucerna. No baja de [hasta]. */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.18f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.024f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.014f) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa la cornisa, una tabla o el piso, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float) =
        drawOval(if (esNoche) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    private fun DrawScope.contactoT(centro: Offset, ancho: Float, alto: Float, a: Float, t: Tono) =
        drawOval(t.s(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.2f * fuerza), Offset(x(0f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.36f * fuerza), Offset(x(0.15f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.22f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
    }

    /** Un cuerpo tumbado dentro de [r]: la luz arriba, la sombra abajo (ir dentro de su recorte). */
    private fun DrawScope.tumbado(r: Rect, t: Tono, fuerza: Float = 1f) {
        drawRect(t.b(0.18f * fuerza), r.topLeft, Size(r.width, r.height * 0.32f))
        drawRect(t.b(0.3f * fuerza), Offset(r.left, r.top + r.height * 0.14f), Size(r.width, r.height * 0.08f))
        drawRect(t.s(0.16f * fuerza), Offset(r.left, r.top + r.height * 0.6f), Size(r.width, r.height * 0.4f))
        drawRect(t.s(0.22f * fuerza), Offset(r.left, r.top + r.height * 0.82f), Size(r.width, r.height * 0.18f))
    }

    /** El filo cálido de la lucerna, de noche, en el canto de arriba y en el costado que la mira. De día no pinta nada. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.4f * u))
        drawRect(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.4f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** El destello en el bronce: de noche, de la llama; de día, crema. */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LLAMA.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.85f), r, c)
    }

    /** Un clavo de bronce con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(BRONCE), r, p)
        drawCircle(t.b(0.6f), r * 0.42f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** Una hoja de laurel girada [giro] grados: el óvalo en punta, su lado en sombra y su nervio. */
    private fun DrawScope.hojaLaurel(c: Offset, largo: Float, giro: Float, col: Color, t: Tono) {
        val u = density
        val a = largo * 0.2f
        val l = largo / 2
        rotate(giro, c) {
            val forma = Path().apply {
                moveTo(c.x, c.y + l)
                cubicTo(c.x + a * 1.4f, c.y + l * 0.4f, c.x + a * 1.1f, c.y - l * 0.5f, c.x, c.y - l)
                cubicTo(c.x - a * 1.1f, c.y - l * 0.5f, c.x - a * 1.4f, c.y + l * 0.4f, c.x, c.y + l)
                close()
            }
            drawPath(forma, col)
            clipPath(forma) { drawRect(t.s(0.22f), Offset(c.x, c.y - l), Size(a * 1.5f, largo)) }
            drawLine(t.b(0.35f), Offset(c.x, c.y - l * 0.85f), Offset(c.x, c.y + l), 0.5f * u)
        }
    }

    /** La tabula ansata en [r]: la placa de bronce claro con sus dos asas en cola de milano, su marco hundido y sus clavos. */
    private fun DrawScope.tabula(r: Rect, t: Tono, asa: Float) {
        val u = density
        val forma = Path().apply {
            moveTo(r.left, r.top); lineTo(r.right, r.top)
            lineTo(r.right + asa, r.top - r.height * 0.06f); lineTo(r.right + asa * 0.5f, r.center.y); lineTo(r.right + asa, r.bottom + r.height * 0.06f)
            lineTo(r.right, r.bottom); lineTo(r.left, r.bottom)
            lineTo(r.left - asa, r.bottom + r.height * 0.06f); lineTo(r.left - asa * 0.5f, r.center.y); lineTo(r.left - asa, r.top - r.height * 0.06f)
            close()
        }
        translate(1.6f * u, 2.2f * u) { drawPath(forma, t.s(0.38f)) }
        val fondo = if (t.o) mezcla(PLACA_LEJOS, mezcla(LATON, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else LATON
        drawPath(forma, fondo)
        clipPath(forma) {
            // El bronce batido: más claro arriba a la izquierda, más hondo abajo a la derecha.
            drawRect(if (t.o) LLAMA.copy(alpha = 0.12f * t.k) else luzMueble(0.25f), Offset(r.left - asa, r.top - asa), Size(r.width * 0.4f, r.height * 0.45f + asa))
            drawRect(t.s(0.14f), Offset(r.left - asa, r.bottom - r.height * 0.18f), Size(r.width + 2 * asa, r.height * 0.3f))
            drawRect(t.s(0.1f), Offset(r.right - r.width * 0.1f, r.top - asa), Size(r.width * 0.1f + asa * 2, r.height + asa * 2))
        }
        drawPath(forma, mezcla(BRONCE, TINTA, 0.35f).copy(alpha = 0.7f), style = Stroke(0.9f * u))
        // El marco hundido: sombra arriba e izquierda, luz abajo y derecha.
        val m = Rect(r.left + 2.2f * u, r.top + 2.2f * u, r.right - 2.2f * u, r.bottom - 2.2f * u)
        drawLine(mezcla(BRONCE, TINTA, 0.3f).copy(alpha = 0.6f), m.topLeft, Offset(m.right, m.top), 0.9f * u)
        drawLine(mezcla(BRONCE, TINTA, 0.3f).copy(alpha = 0.5f), m.topLeft, Offset(m.left, m.bottom), 0.9f * u)
        drawLine(if (t.o) LLAMA.copy(alpha = 0.4f * t.k) else luzMueble(0.6f), Offset(m.left, m.bottom), m.bottomRight, 0.8f * u)
        drawLine(if (t.o) LLAMA.copy(alpha = 0.3f * t.k) else luzMueble(0.45f), Offset(m.right, m.top), m.bottomRight, 0.8f * u)
        listOf(r.left - asa * 0.62f, r.right + asa * 0.62f).forEach { x ->
            drawCircle(mezcla(BRONCE, TINTA, 0.45f), 1.2f * u, Offset(x, r.center.y))
            drawCircle(if (t.o) LLAMA.copy(alpha = 0.5f * t.k) else luzMueble(0.6f), 0.5f * u, Offset(x - 0.4f * u, r.center.y - 0.4f * u))
        }
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        esNoche = oscuro
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base; val hb = b.height
        val suelo = b.top + hb * SUELO

        // ---- el muro al fresco y el piso
        pared(w, suelo)
        piso(w, suelo, alto)

        // ---- en la pared: la corona de laurel seca
        coronaLaurel(Offset(w * 0.215f, w * 0.17f), w * 0.062f)

        // ---- el armario: su sombra, el cuerpo, los marcos, las hojas, el zócalo y los pies
        armario(d, suelo)
        cornisa(w)
        fronton(w)

        // ---- lo de encima, de atrás adelante
        candelabro(Offset(w * 0.9f, w * PIE_A))
        tintero(Offset(w * 0.1f, w * PIE_A + 0.003f * w))
        rollo(w)
        bolsa(Offset(w * 0.385f, w * PIE_F))
        casco(Offset(w * 0.695f, w * PIE_F))

        // ---- al pie: el escabel con las tablillas y el scrinium cortado por el borde
        escabel(b, w, hb)
        scrinium(b, w, hb)

        // ---- la placa de «y N más», en el zócalo
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            tabula(r, tn(r.center), r.height * 0.55f)
        }

        // ---- la luz de la lucerna sobre lo que tiene cerca
        if (oscuro) {
            val llama = LUZ * w
            mancha(llama + Offset(-w * 0.04f, w * 0.06f), w * 0.5f, LLAMA.copy(alpha = 0.1f))
            mancha(llama + Offset(0f, w * 0.02f), w * 0.16f, LLAMA.copy(alpha = 0.24f))
        }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    /**
     * El muro al fresco: el friso negro con la guirnalda pintada, el paño rojo con su doble filete, el zócalo negro jaspeado;
     * la mancha de luz de día arriba a la izquierda (de noche, el halo de la lucerna), pinceladas, desconchones y grietas.
     */
    private fun DrawScope.pared(w: Float, suelo: Float) {
        val u = density
        val o = esNoche
        val zocalo = suelo - w * 0.16f
        val friso = w * 0.06f
        // El paño rojo.
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(ROJO_FRESCO, LUZ_MUEBLE, 0.16f), ROJO_FRESCO, mezcla(ROJO_FRESCO, POMPEYANO, 0.7f)), 0f, w), Offset.Zero, Size(w, zocalo))
            mancha(Offset(w * 0.04f, 0f), w * 0.7f, luzMueble(0.3f))
        } else {
            fun k(dd: Float) = Tono(true, luz(LUZ + Offset(dd, 0f))).c(ROJO_FRESCO)
            drawRect(Brush.radialGradient(0f to k(0.04f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.8f), 1f to k(1.3f),
                center = LUZ * w, radius = w * 1.3f), Offset.Zero, Size(w, zocalo))
        }
        pinceladas(Rect(0f, 0f, w, zocalo), if (o) NOCHE else POMPEYANO, 5001, 44, u, 0.12f)
        pinceladas(Rect(0f, 0f, w, zocalo), if (o) NOCHE else mezcla(ROJO_FRESCO, LUZ_MUEBLE, 0.35f), 5002, 22, u, 0.09f)
        // El friso negro con la guirnalda pintada: festones verdes con sus cintas rojas.
        val tf = tn(Offset(w * 0.6f, friso / 2))
        drawRect(tf.c(NEGRO), Offset.Zero, Size(w, friso))
        pinceladas(Rect(0f, 0f, w, friso), tf.c(mezcla(NEGRO, FILETE, 0.2f)), 5003, 10, u, 0.12f)
        val festones = 5
        for (i in 0 until festones) {
            val x0 = w * (i - 0.25f) / (festones - 1.5f)
            val x1 = w * (i + 0.75f) / (festones - 1.5f)
            val tt = tn(Offset((x0 + x1) / 2, friso / 2))
            val g = Path().apply { moveTo(x0, friso * 0.22f); cubicTo(x0 + (x1 - x0) * 0.25f, friso * 0.85f, x0 + (x1 - x0) * 0.75f, friso * 0.85f, x1, friso * 0.22f) }
            drawPath(g, tt.c(mezcla(LAUREL, NEGRO, 0.25f)), style = Stroke(friso * 0.17f, cap = StrokeCap.Round))
            drawPath(g, tt.c(mezcla(LAUREL, FILETE, 0.3f)).copy(alpha = 0.7f), style = Stroke(friso * 0.05f, cap = StrokeCap.Round))
            for (j in 1..5) {
                val f = j / 6f
                val px = x0 + (x1 - x0) * f
                val py = friso * (0.22f + 0.48f * (1f - (2f * f - 1f).pow(2)))
                drawCircle(tt.c(mezcla(LAUREL, FILETE, 0.45f)), friso * 0.05f, Offset(px, py - friso * 0.04f))
            }
            // La cinta en el nudo.
            drawLine(tt.c(ROJO), Offset(x0, friso * 0.22f), Offset(x0 - friso * 0.12f, friso * 0.62f), friso * 0.05f, StrokeCap.Round)
            drawLine(tt.c(ROJO), Offset(x0, friso * 0.22f), Offset(x0 + friso * 0.1f, friso * 0.66f), friso * 0.05f, StrokeCap.Round)
            drawCircle(tt.c(FILETE), friso * 0.07f, Offset(x0, friso * 0.2f))
        }
        // El doble filete del paño: el ocre y, por dentro, uno fino claro.
        val tfi = tn(Offset(w * 0.5f, friso))
        drawRect(tfi.c(FILETE), Offset(0f, friso), Size(w, w * 0.006f))
        drawRect(tfi.s(0.3f), Offset(0f, friso + w * 0.006f), Size(w, 1f * u))
        val m = w * 0.028f
        val panel = Rect(m, friso + m, w - m, zocalo - m)
        listOf(panel.left, panel.right).forEach { x ->
            val tx = tn(Offset(x, w * 0.3f))
            drawRect(tx.c(FILETE).copy(alpha = 0.85f), Offset(x - w * 0.002f, panel.top), Size(w * 0.004f, panel.height))
            drawRect(tx.c(REVOQUE).copy(alpha = 0.4f), Offset(x + (if (x < w / 2) w * 0.008f else -w * 0.009f), panel.top + w * 0.01f), Size(w * 0.0015f, panel.height - w * 0.02f))
        }
        drawRect(tfi.c(FILETE).copy(alpha = 0.85f), Offset(panel.left, panel.top), Size(panel.width, w * 0.004f))
        drawRect(tfi.c(REVOQUE).copy(alpha = 0.4f), Offset(panel.left + w * 0.008f, panel.top + w * 0.008f), Size(panel.width - w * 0.017f, w * 0.0015f))
        // El zócalo negro con el jaspeado y su filete.
        val tz = tn(Offset(w * 0.5f, zocalo))
        drawRect(tz.c(mezcla(NEGRO, POMPEYANO, 0.12f)), Offset(0f, zocalo), Size(w, suelo - zocalo))
        for (k in 0 until 26) {
            val p = Offset(w * azar(5010, k), zocalo + (suelo - zocalo) * (0.15f + 0.8f * azar(5011, k)))
            val tt = tn(p)
            val col = when (k % 3) { 0 -> FILETE; 1 -> ROJO_FRESCO; else -> REVOQUE }
            rotate(-30f + 60f * azar(5012, k), p) { drawOval(tt.c(col).copy(alpha = 0.22f), Offset(p.x - w * 0.012f, p.y - w * 0.003f), Size(w * 0.024f, w * 0.006f)) }
        }
        drawRect(tz.c(FILETE), Offset(0f, zocalo - w * 0.005f), Size(w, w * 0.005f))
        drawRect(tz.c(ROJO_FRESCO), Offset(0f, zocalo), Size(w, w * 0.003f))
        drawRect(tz.s(0.25f), Offset(0f, zocalo + w * 0.003f), Size(w, 1.2f * u))
        // Los desconchones: el revoque blanco y, en el más hondo, el ladrillo.
        desconchon(Offset(w * 0.03f, w * 0.34f), w * 0.05f, 5020, ladrillo = true)
        desconchon(Offset(w * 0.47f, w * 0.03f), w * 0.028f, 5021, ladrillo = false)
        desconchon(Offset(w * 0.975f, zocalo + (suelo - zocalo) * 0.55f), w * 0.04f, 5022, ladrillo = true)
        desconchon(Offset(w * 0.33f, w * 0.2f), w * 0.016f, 5023, ladrillo = false)
        // Grietas finas.
        listOf(Offset(0.35f, 0.2f), Offset(0.99f, 0.3f)).forEachIndexed { i, f ->
            val p0 = Offset(w * f.x, w * f.y)
            val t = tn(p0)
            val g = Path().apply {
                moveTo(p0.x, p0.y)
                var q = p0
                for (j in 0 until 5) { q += Offset(w * (0.006f + 0.01f * azar(5030 + i, j)) * (if (j % 2 == 0) -1f else 0.5f), w * (0.012f + 0.01f * azar(5040 + i, j))); lineTo(q.x, q.y) }
            }
            drawPath(g, t.s(0.3f), style = Stroke(0.7f * u))
            translate(0.8f * u, 0.8f * u) { drawPath(g, t.b(0.2f), style = Stroke(0.5f * u)) }
        }
    }

    /** Un desconchón del fresco: el borde irregular, el revoque claro, la sombra del canto de arriba y, si es hondo, el ladrillo. */
    private fun DrawScope.desconchon(c: Offset, r: Float, s: Int, ladrillo: Boolean) {
        val u = density
        val t = tn(c)
        val p = Path().apply {
            val n = 11
            for (k in 0 until n) {
                val a = 6.283f * k / n
                val rr = r * (0.55f + 0.5f * azar(s, k)) * (if (k % 3 == 0) 0.7f else 1f)
                val q = Offset(c.x + rr * cos(a) * 1.25f, c.y + rr * sin(a) * 0.8f)
                if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        drawPath(p, t.c(REVOQUE))
        clipPath(p) {
            drawRect(t.s(0.25f), Offset(c.x - r * 2, c.y - r * 2), Size(r * 4, r * 1.55f))
            if (ladrillo) {
                // Lo más hondo: el ladrillo en hiladas, con sus juntas de mortero.
                val bp = poligono(c.x - r * 0.75f, c.y - r * 0.05f, c.x - r * 0.2f, c.y - r * 0.3f, c.x + r * 0.55f, c.y - r * 0.2f, c.x + r * 0.8f, c.y + r * 0.2f, c.x + r * 0.2f, c.y + r * 0.42f, c.x - r * 0.5f, c.y + r * 0.35f)
                drawPath(bp, t.c(BARRO))
                clipPath(bp) {
                    listOf(-0.12f, 0.15f).forEach { fy -> drawLine(t.c(REVOQUE).copy(alpha = 0.6f), Offset(c.x - r, c.y + r * fy), Offset(c.x + r, c.y + r * fy), 1f * u) }
                    listOf(-0.35f to -0.4f, 0.25f to -0.4f, -0.05f to -0.12f, 0.55f to -0.12f, -0.4f to 0.15f, 0.2f to 0.15f).forEach { (fx, fy) ->
                        drawLine(t.c(REVOQUE).copy(alpha = 0.6f), Offset(c.x + r * fx, c.y + r * fy), Offset(c.x + r * fx, c.y + r * (fy + 0.27f)), 1f * u)
                    }
                    drawRect(t.s(0.3f), Offset(c.x - r, c.y - r), Size(r * 2, r * 0.82f))
                }
            }
        }
        drawPath(p, t.s(0.35f), style = Stroke(0.8f * u))
        translate(0.9f * u, 0.9f * u) { drawPath(p, t.b(0.3f), style = Stroke(0.6f * u)) }
    }

    /** El piso de cocciopesto: el barro rojizo con su rejilla de teselas blancas, pinceladas y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(BARRO), Offset(0f, suelo), Size(w, alto - suelo))
        drawRect(Brush.verticalGradient(listOf(tt.s(0.3f), Color.Transparent), suelo, alto), Offset(0f, suelo), Size(w, alto - suelo))
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else mezcla(BARRO, NOGAL, 0.5f), 5050, 18, u, 0.16f)
        // Las teselas: hileras cada vez más separadas hacia quien mira.
        var y = suelo + (alto - suelo) * 0.08f
        var fila = 0
        while (y < alto) {
            val f = (y - suelo) / (alto - suelo)
            val paso = w * (0.03f + 0.03f * f)
            var x = (if (fila % 2 == 0) 0f else paso / 2) - w * 0.02f
            val tf = tn(Offset(w * 0.5f, y))
            while (x < w) {
                val r = (0.8f + 1.2f * f) * u
                drawRect(tf.c(REVOQUE).copy(alpha = 0.75f), Offset(x - r, y - r * 0.6f), Size(r * 2, r * 1.2f))
                x += paso
            }
            y += (alto - suelo) * (0.12f + 0.1f * f)
            fila++
        }
        drawRect(tt.s(0.4f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.18f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    /** La corona de laurel seca colgada de un clavo: el aro de hojas, más verdes unas y más pardas otras, y su cinta roja descolorida. */
    private fun DrawScope.coronaLaurel(c: Offset, r: Float) {
        val u = density
        val t = tn(c)
        val clavo = Offset(c.x + r * 0.05f, c.y - r * 1.25f)
        // La sombra del aro en el fresco.
        val aro = Path().apply { addOval(Rect(c.x - r * 1.1f, c.y - r * 1.05f, c.x + r * 1.1f, c.y + r * 1.05f)); addOval(Rect(c.x - r * 0.62f, c.y - r * 0.58f, c.x + r * 0.62f, c.y + r * 0.58f)) }
        aro.fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
        sombraEnPared(aro, c, w(1.2f), 0.2f)
        // El hilo del clavo a la corona.
        drawLine(t.c(mezcla(PAPIRO_OSC, NOGAL, 0.3f)), clavo, Offset(c.x - r * 0.3f, c.y - r * 0.85f), 0.8f * u)
        drawLine(t.c(mezcla(PAPIRO_OSC, NOGAL, 0.3f)), clavo, Offset(c.x + r * 0.35f, c.y - r * 0.82f), 0.8f * u)
        clavo(clavo, 1.5f * u, t)
        // El aro: dos ramas que se cruzan abajo; cada hoja apunta hacia arriba por su lado.
        val n = 13
        for (lado in listOf(-1f, 1f)) for (k in 0 until n) {
            val ang = 90f + lado * (8f + 160f * k / (n - 1))
            val rad = Math.toRadians(ang.toDouble())
            val rr = r * (0.82f + 0.08f * azar(5100, k + (if (lado > 0) 20 else 0)))
            val p = Offset(c.x + rr * cos(rad).toFloat(), c.y + rr * sin(rad).toFloat())
            val tono = azar(5101, k + (if (lado > 0) 30 else 0))
            val col = mezcla(mezcla(LAUREL, OCRE, 0.25f + 0.4f * tono), PAPIRO_OSC, 0.15f * tono)
            val tp = tn(p)
            listOf(-1f, 1f).forEach { sh ->
                hojaLaurel(p + Offset(sh * r * 0.08f, 0f), r * (0.36f + 0.06f * tono), ang + 90f * lado * -1f + sh * 28f + 180f, tp.c(col), tp)
            }
        }
        // La cinta, atada abajo, con sus dos colas.
        val nudo = Offset(c.x, c.y + r * 0.88f)
        val cinta = t.c(mezcla(ROJO, PAPIRO, 0.22f))
        listOf(-1f, 1f).forEach { s ->
            val cola = Path().apply {
                moveTo(nudo.x, nudo.y)
                cubicTo(nudo.x + s * r * 0.15f, nudo.y + r * 0.35f, nudo.x + s * r * 0.05f, nudo.y + r * 0.6f, nudo.x + s * r * 0.28f, nudo.y + r * 0.95f)
            }
            translate(r * 0.08f, r * 0.05f) { drawPath(cola, t.s(0.2f), style = Stroke(r * 0.12f, cap = StrokeCap.Round)) }
            drawPath(cola, cinta, style = Stroke(r * 0.12f, cap = StrokeCap.Round))
            drawPath(cola, t.b(0.3f), style = Stroke(r * 0.03f))
        }
        drawOval(cinta, Offset(nudo.x - r * 0.12f, nudo.y - r * 0.08f), Size(r * 0.24f, r * 0.16f))
    }

    /** Un ancho [f] del mueble en píxeles (para las alturas en fracciones del ancho). */
    private fun w(f: Float) = anchoW * f

    // ---------------------------------------------------------------- el armario

    private fun DrawScope.armario(d: Disposicion, suelo: Float) {
        val u = density
        val o = esNoche
        val w = anchoW
        val b = d.base; val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val fin = b.top + hb * CUERPO_FIN
        val zfin = b.top + hb * ZOCALO_FIN
        val pisan = b.top + hb * PISAN
        val cuerpo = Rect(xL, w * CANTO, xR, fin)

        // Su sombra en la pared (a la derecha) y en el piso.
        val t0 = tn(Offset(xR, w * 1.2f))
        drawPath(poligono(w * 0.945f, w * FRENTE, w * 0.975f, w * FRENTE + w * 0.03f, w * 0.975f, suelo, xR, suelo), t0.s(0.28f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.s(0.34f), Offset(xL + w * 0.02f, pisan - 2f * u), Size(xR - xL + w * 0.01f, hb * 0.05f))
        drawRect(tp.s(0.16f), Offset(xL + w * 0.03f, pisan + hb * 0.05f - 2f * u), Size(xR - xL, hb * 0.06f))

        // Las hojas abiertas, detrás del cuerpo (la bisagra queda en su canto).
        puerta(cuerpo, izquierda = true)
        puerta(cuerpo, izquierda = false)

        // El frente de nogal.
        if (!o) drawRect(NOGAL, cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.verticalGradient(List(6) { i -> tn(Offset(w * 0.7f, cuerpo.top + cuerpo.height * i / 5f)).c(NOGAL) }, cuerpo.top, cuerpo.bottom), cuerpo.topLeft, cuerpo.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.22f)), xL, xR), cuerpo.topLeft, cuerpo.size)
        else drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.45f), Color.Transparent), xL, w * 0.75f), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.35f), 5201, 0.5f, vertical = true, grosor = 0.6f, ondulacion = 0.5f)
        pinceladas(cuerpo, if (o) NOCHE else NOGAL_LUZ, 5202, 26, u, 0.08f)

        // Los cantos de los costados: el izquierdo con luz; el derecho en sombra.
        for (k in 0 until 10) {
            val y0 = cuerpo.top + cuerpo.height * k / 10f
            val t = tn(Offset(xL, y0 + cuerpo.height / 20f))
            drawRect(t.b(0.4f), Offset(xL, y0), Size(1.4f * u, cuerpo.height / 10f))
            val tr = tn(Offset(xR, y0 + cuerpo.height / 20f))
            drawRect(tr.c(NOGAL_OSC), Offset(xR - w * 0.007f, y0), Size(w * 0.007f, cuerpo.height / 10f))
            if (o) drawRect(tr.b(0.5f), Offset(xR - w * 0.007f, y0), Size(1f * u, cuerpo.height / 10f))
        }

        // El friso con rosetas en metopas rehundidas, entre la cornisa y los anaqueles.
        val fr = Rect(xL + w * 0.012f, w * CANTO + w * 0.012f, xR - w * 0.012f, w * FRISO)
        val tfr = tn(fr.center)
        drawRect(tfr.s(0.35f), Offset(xL, w * CANTO), Size(xR - xL, 2.6f * u))
        drawRect(tfr.s(0.15f), Offset(xL, w * CANTO + 2.6f * u), Size(xR - xL, 4f * u))
        val metopas = 6
        for (i in 0 until metopas) {
            val mw = fr.width / metopas
            val r = Rect(fr.left + mw * i + w * 0.008f, fr.top + w * 0.008f, fr.left + mw * (i + 1) - w * 0.008f, fr.bottom - w * 0.008f)
            val t = tn(r.center)
            drawRect(t.c(mezcla(NOGAL, NOGAL_OSC, 0.35f)), r.topLeft, r.size)
            // Lo rehundido: el borde de arriba y el izquierdo en sombra; el de abajo y el derecho, con luz.
            drawRect(t.s(0.35f), r.topLeft, Size(r.width, 2f * u))
            drawRect(t.s(0.25f), r.topLeft, Size(2f * u, r.height))
            drawRect(t.b(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(t.b(0.22f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
            if (i % 2 == 0) roseta(r.center, minOf(r.width, r.height) * 0.36f, t)
            else {
                // Un triglifo: tres bandas en relieve.
                for (j in 0 until 3) {
                    val x = r.left + r.width * (0.22f + 0.24f * j)
                    drawRect(t.c(NOGAL), Offset(x, r.top + r.height * 0.15f), Size(r.width * 0.13f, r.height * 0.7f))
                    drawRect(t.b(0.35f), Offset(x, r.top + r.height * 0.15f), Size(1f * u, r.height * 0.7f))
                    drawRect(t.s(0.3f), Offset(x + r.width * 0.13f - 1f * u, r.top + r.height * 0.15f), Size(1f * u, r.height * 0.7f))
                }
            }
        }

        // Los marcos de los huecos, moldurados, con su grosor y su sombra.
        val m = w * MARCO
        d.huecos.forEach { r ->
            val t = tn(r.center)
            val marco = Rect(r.left - m, r.top - m, r.right + m, r.bottom + m)
            drawRect(t.s(0.3f), Offset(marco.left + 2f * u, marco.top + 2.4f * u), marco.size)
            drawRect(t.c(mezcla(NOGAL, NOGAL_LUZ, 0.4f)), marco.topLeft, marco.size)
            drawRect(t.b(0.5f), marco.topLeft, Size(marco.width, 1.2f * u))
            drawRect(t.b(0.3f), marco.topLeft, Size(1.2f * u, marco.height))
            // La media caña: una raya de sombra y otra de luz a media moldura.
            val mm = Rect(marco.left + m * 0.45f, marco.top + m * 0.45f, marco.right - m * 0.45f, marco.bottom - m * 0.45f)
            drawRect(t.s(0.25f), mm.topLeft, mm.size, style = Stroke(1f * u))
            drawRect(t.b(0.2f), mm.topLeft + Offset(1f * u, 1f * u), mm.size, style = Stroke(0.7f * u))
            drawRect(t.s(0.35f), Offset(marco.left, marco.bottom - 1.4f * u), Size(marco.width, 1.4f * u))
            drawRect(t.s(0.28f), Offset(marco.right - 1.4f * u, marco.top), Size(1.4f * u, marco.height))
        }
        // Los cantos de los anaqueles: un astrágalo de cuentas entre fila y fila.
        d.filas.forEachIndexed { i, f ->
            if (i < d.filas.size - 1) {
                val y = f.bottom + d.separacion / 2
                astragalo(xL + w * 0.01f, xR - w * 0.01f, y, w * 0.007f)
            }
        }

        // ---- el zócalo con sus molduras
        val tz = tn(Offset(w * 0.5f, (fin + zfin) / 2))
        val zoc = Rect(xL - w * 0.012f, fin, xR + w * 0.012f, zfin)
        drawRect(tz.c(mezcla(NOGAL, NOGAL_OSC, 0.2f)), zoc.topLeft, zoc.size)
        vetas(zoc, (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.35f), 5210, 0.6f, grosor = 0.5f)
        drawRect(tz.c(NOGAL_LUZ), zoc.topLeft, Size(zoc.width, zoc.height * 0.16f))
        drawRect(tz.b(0.5f), zoc.topLeft, Size(zoc.width, 1f * u))
        drawRect(tz.s(0.3f), Offset(zoc.left, zoc.top + zoc.height * 0.16f), Size(zoc.width, 1.2f * u))
        drawRect(tz.c(mezcla(NOGAL, NOGAL_LUZ, 0.3f)), Offset(zoc.left - w * 0.006f, zoc.bottom - zoc.height * 0.2f), Size(zoc.width + w * 0.012f, zoc.height * 0.2f))
        drawRect(tz.b(0.4f), Offset(zoc.left - w * 0.006f, zoc.bottom - zoc.height * 0.2f), Size(zoc.width + w * 0.012f, 1f * u))
        drawRect(tz.s(0.35f), Offset(zoc.left - w * 0.006f, zoc.bottom - 1.2f * u), Size(zoc.width + w * 0.012f, 1.2f * u))
        drawRect(tz.s(0.25f), Offset(zoc.right - w * 0.012f, zoc.top), Size(w * 0.012f, zoc.height))
        // Los paneles rehundidos a los lados de la placa.
        listOf(0.05f to 0.32f, 0.68f to 0.95f).forEach { (a, z) ->
            val r = Rect(zoc.left + zoc.width * a, zoc.top + zoc.height * 0.3f, zoc.left + zoc.width * z, zoc.bottom - zoc.height * 0.3f)
            drawRect(tz.s(0.3f), r.topLeft, Size(r.width, 1.5f * u))
            drawRect(tz.s(0.2f), r.topLeft, Size(1.5f * u, r.height))
            drawRect(tz.b(0.3f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
            drawRect(tz.b(0.2f), Offset(r.right - 1f * u, r.top), Size(1f * u, r.height))
        }
        // Una rozadura en el canto, a la derecha.
        drawPath(poligono(zoc.right - w * 0.05f, zoc.top, zoc.right, zoc.top, zoc.right, zoc.top + zoc.height * 0.35f), tz.c(mezcla(NOGAL_LUZ, PAPIRO, 0.3f)))

        // ---- los pies torneados
        listOf(xL + w * 0.035f, xR - w * 0.035f).forEach { x -> pieTorneado(Offset(x, pisan), zfin, w * 0.06f) }
    }

    /** Una roseta tallada de ocho pétalos, con el lado de luz arriba a la izquierda y el botón en medio. */
    private fun DrawScope.roseta(c: Offset, r: Float, t: Tono) {
        drawCircle(t.s(0.3f), r * 1.05f, c + Offset(r * 0.12f, r * 0.14f))
        for (k in 0 until 8) {
            val a = 45f * k
            rotate(a, c) {
                val pr = Rect(c.x - r * 0.2f, c.y - r, c.x + r * 0.2f, c.y - r * 0.15f)
                drawOval(t.c(mezcla(NOGAL, NOGAL_LUZ, 0.55f)), pr.topLeft, pr.size)
                drawOval(t.s(0.28f), Offset(pr.center.x, pr.top), Size(pr.width / 2, pr.height))
            }
        }
        drawCircle(t.c(NOGAL_LUZ), r * 0.24f, c)
        drawCircle(t.b(0.5f), r * 0.1f, c - Offset(r * 0.08f, r * 0.08f))
    }

    /** Un astrágalo: la moldura de cuentas y carretes, de [x0] a [x1] en [y], con su luz arriba y su sombra abajo. */
    private fun DrawScope.astragalo(x0: Float, x1: Float, y: Float, r: Float) {
        val u = density
        val t = tn(Offset((x0 + x1) / 2, y))
        drawRect(t.s(0.3f), Offset(x0, y + r * 0.8f), Size(x1 - x0, r))
        drawRect(t.c(NOGAL_OSC), Offset(x0, y - r * 0.6f), Size(x1 - x0, r * 1.2f))
        var x = x0 + r
        var k = 0
        while (x < x1 - r) {
            val tt = tn(Offset(x, y))
            if (k % 2 == 0) {
                drawOval(tt.c(NOGAL_LUZ), Offset(x - r * 1.1f, y - r * 0.75f), Size(r * 2.2f, r * 1.5f))
                drawOval(tt.b(0.45f), Offset(x - r * 0.7f, y - r * 0.6f), Size(r * 0.9f, r * 0.5f))
                drawOval(tt.s(0.25f), Offset(x - r * 0.4f, y + r * 0.05f), Size(r * 1.4f, r * 0.6f))
                x += r * 2.4f
            } else {
                drawOval(tt.c(mezcla(NOGAL, NOGAL_LUZ, 0.4f)), Offset(x - r * 0.35f, y - r * 0.5f), Size(r * 0.7f, r))
                x += r * 1.0f
            }
            k++
        }
        drawLine(t.b(0.3f), Offset(x0, y - r * 0.75f), Offset(x1, y - r * 0.75f), 0.6f * u)
    }

    /** Un pie torneado: el anillo, la bola, el cuello y el pie que pisa, con luz a la izquierda y sombra a la derecha. */
    private fun DrawScope.pieTorneado(pie: Offset, arriba: Float, ancho: Float) {
        val u = density
        val h = pie.y - arriba
        val t = tn(Offset(pie.x, pie.y))
        val c = pie.x
        contacto(Offset(c + ancho * 0.15f, pie.y), ancho * 1.4f, h * 0.12f, 0.45f)
        val perfil = Path().apply {
            moveTo(c - ancho * 0.45f, arriba)
            lineTo(c + ancho * 0.45f, arriba)
            lineTo(c + ancho * 0.45f, arriba + h * 0.12f)
            lineTo(c + ancho * 0.3f, arriba + h * 0.16f)
            cubicTo(c + ancho * 0.62f, arriba + h * 0.25f, c + ancho * 0.62f, arriba + h * 0.55f, c + ancho * 0.22f, arriba + h * 0.62f)
            lineTo(c + ancho * 0.16f, arriba + h * 0.7f)
            lineTo(c + ancho * 0.3f, arriba + h * 0.74f)
            lineTo(c + ancho * 0.3f, arriba + h * 0.8f)
            cubicTo(c + ancho * 0.16f, arriba + h * 0.86f, c + ancho * 0.2f, arriba + h * 0.95f, c + ancho * 0.28f, pie.y)
            lineTo(c - ancho * 0.28f, pie.y)
            cubicTo(c - ancho * 0.2f, arriba + h * 0.95f, c - ancho * 0.16f, arriba + h * 0.86f, c - ancho * 0.3f, arriba + h * 0.8f)
            lineTo(c - ancho * 0.3f, arriba + h * 0.74f)
            lineTo(c - ancho * 0.16f, arriba + h * 0.7f)
            lineTo(c - ancho * 0.22f, arriba + h * 0.62f)
            cubicTo(c - ancho * 0.62f, arriba + h * 0.55f, c - ancho * 0.62f, arriba + h * 0.25f, c - ancho * 0.3f, arriba + h * 0.16f)
            lineTo(c - ancho * 0.45f, arriba + h * 0.12f)
            close()
        }
        drawPath(perfil, t.c(mezcla(NOGAL, NOGAL_LUZ, 0.25f)))
        clipPath(perfil) {
            cilindro(Rect(c - ancho * 0.62f, arriba, c + ancho * 0.62f, pie.y), t, 1.5f, luzDerecha(c))
            listOf(0.12f, 0.16f, 0.62f, 0.7f, 0.74f, 0.8f).forEach { f -> drawLine(t.s(0.35f), Offset(c - ancho, arriba + h * f), Offset(c + ancho, arriba + h * f), 0.8f * u) }
        }
        drawRect(t.b(0.4f), Offset(c - ancho * 0.45f, arriba), Size(ancho * 0.9f, 1f * u))
    }

    /**
     * Una hoja del armario, abierta en escorzo: la cara de dentro con su tablero rehundido, el canto de la hoja, las bisagras de
     * hueso en el canto del cuerpo y, en la izquierda, la cerradura de bronce; en la derecha, la argolla.
     */
    private fun DrawScope.puerta(cuerpo: Rect, izquierda: Boolean) {
        val u = density
        val w = anchoW
        val s = if (izquierda) -1f else 1f
        val xb = if (izquierda) cuerpo.left else cuerpo.right
        val xf = xb + s * w * 0.058f
        val yb0 = cuerpo.top + w * 0.012f; val yb1 = cuerpo.bottom - w * 0.006f
        val yf0 = cuerpo.top - w * 0.014f; val yf1 = cuerpo.bottom + w * 0.02f
        val hoja = poligono(xb, yb0, xf, yf0, xf, yf1, xb, yb1)
        val tc = tn(Offset((xb + xf) / 2, w * 1.0f))
        // La sombra de la hoja en la pared.
        if (!esNoche) translate(w * (if (izquierda) 0.008f else 0.026f), w * 0.012f) { drawPath(hoja, sombraMueble(0.24f)) }
        // La cara de dentro: la izquierda mira lejos de la luz (más honda); la derecha la recibe.
        val cara = if (izquierda) mezcla(NOGAL, NOGAL_OSC, 0.3f) else mezcla(NOGAL, NOGAL_LUZ, 0.25f)
        if (!esNoche) drawPath(hoja, cara)
        else drawPath(hoja, Brush.verticalGradient(List(6) { i -> tn(Offset(xb + s * w * 0.03f, yb0 + (yb1 - yb0) * i / 5f)).c(cara) }, yb0, yb1))
        clipPath(hoja) {
            vetas(Rect(minOf(xb, xf), yf0, maxOf(xb, xf), yf1), (if (esNoche) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), if (izquierda) 5221 else 5222, 0.5f, vertical = true, grosor = 0.5f, ondulacion = 0.4f)
        }
        // El tablero rehundido: un cuadrilátero en escorzo, con la sombra del lado de arriba.
        val ins = 0.22f
        fun yAt(f: Float, top: Boolean) = if (top) yb0 + (yf0 - yb0) * f else yb1 + (yf1 - yb1) * f
        val f0 = ins; val f1 = 1f - ins * 0.8f
        val xa = xb + (xf - xb) * f0; val xz = xb + (xf - xb) * f1
        val tab = poligono(xa, yAt(f0, true) + w * 0.04f, xz, yAt(f1, true) + w * 0.04f, xz, yAt(f1, false) - w * 0.04f, xa, yAt(f0, false) - w * 0.04f)
        drawPath(tab, tc.s(0.18f))
        drawPath(tab, tc.s(0.35f), style = Stroke(1.2f * u))
        translate(s * 1f * u, 1f * u) { drawPath(tab, tc.b(0.2f), style = Stroke(0.7f * u)) }
        // El canto de la hoja (su grosor), del lado de fuera.
        val canto = poligono(xf, yf0, xf + s * w * 0.008f, yf0 + w * 0.004f, xf + s * w * 0.008f, yf1 - w * 0.004f, xf, yf1)
        drawPath(canto, tc.c(if (izquierda) NOGAL_LUZ else NOGAL_OSC))
        if (esNoche && !izquierda) drawPath(canto, tc.b(0.4f))
        // Las bisagras de hueso: tres canutos en el canto del cuerpo.
        listOf(0.08f, 0.5f, 0.92f).forEach { f ->
            val y = yb0 + (yb1 - yb0) * f
            val r = Rect(xb - w * 0.007f, y - w * 0.022f, xb + w * 0.007f, y + w * 0.022f)
            val tb = tn(r.center)
            drawRect(tb.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRoundRect(tb.c(HUESO), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(w * 0.004f))
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, tb, 1.3f) }
            listOf(0.33f, 0.66f).forEach { g -> drawLine(tb.s(0.4f), Offset(r.left, r.top + r.height * g), Offset(r.right, r.top + r.height * g), 0.8f * u) }
        }
        val ym = (yb0 + yb1) / 2
        if (izquierda) {
            // La cerradura: la caja de bronce con sus clavos y el ojo de la llave, cerca del canto libre.
            val xc = xb + (xf - xb) * 0.62f
            val r = Rect(xc - w * 0.012f, ym - w * 0.035f, xc + w * 0.012f, ym + w * 0.035f)
            val tb = tn(r.center)
            drawRect(tb.s(0.35f), Offset(r.left - 1.5f * u, r.top + 2f * u), r.size)
            drawRect(tb.c(BRONCE), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, tb, 1f) }
            drawRect(tb.c(mezcla(BRONCE, TINTA, 0.3f)), r.topLeft, r.size, style = Stroke(0.8f * u))
            listOf(r.top + r.height * 0.12f, r.bottom - r.height * 0.12f).forEach { y -> drawCircle(tb.c(mezcla(BRONCE, TINTA, 0.4f)), 1f * u, Offset(r.center.x, y)) }
            drawCircle(tb.c(TINTA), w * 0.004f, Offset(r.center.x, r.center.y - w * 0.004f))
            drawPath(poligono(r.center.x - w * 0.002f, r.center.y - w * 0.002f, r.center.x + w * 0.002f, r.center.y - w * 0.002f, r.center.x + w * 0.003f, r.center.y + w * 0.01f, r.center.x - w * 0.003f, r.center.y + w * 0.01f), tb.c(TINTA))
            // La llave, puesta, con su argolla colgando.
            drawCircle(tb.c(BRONCE), w * 0.008f, Offset(r.center.x - w * 0.002f, r.bottom + w * 0.016f), style = Stroke(1.4f * u))
        } else {
            val tb = tn(Offset(xf, ym))
            val c = Offset(xb + (xf - xb) * 0.6f, ym)
            drawCircle(tb.s(0.3f), w * 0.011f, c + Offset(1.5f * u, 2.5f * u), style = Stroke(1.6f * u))
            drawCircle(tb.c(BRONCE), w * 0.011f, c + Offset(0f, w * 0.008f), style = Stroke(1.6f * u))
            drawCircle(tb.c(BRONCE_LUZ), w * 0.004f, c)
            destello(c + Offset(-w * 0.008f, w * 0.006f), 0.9f * u, tb)
        }
    }

    /**
     * La cornisa: la tabla de arriba vista un poco desde arriba (con su veta, un cerco y rozaduras), y su frente moldurado: el
     * filete, las ovas y dardos, los dentículos y el listel, con la sombra de cada vuelo.
     */
    private fun DrawScope.cornisa(w: Float) {
        val u = density
        val o = esNoche
        val xL = w * 0.045f; val xR = w * 0.955f
        val t = tn(Offset(w * 0.6f, w * TABLA))
        val tabla = Rect(xL + w * 0.012f, w * TABLA, xR - w * 0.012f, w * FRENTE)
        if (!o) drawRect(mezcla(NOGAL_LUZ, NOGAL, 0.3f), tabla.topLeft, tabla.size)
        else drawRect(Brush.horizontalGradient(List(6) { i -> tn(Offset(tabla.left + tabla.width * i / 5f, w * TABLA)).c(mezcla(NOGAL_LUZ, NOGAL, 0.3f)) }, tabla.left, tabla.right), tabla.topLeft, tabla.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.28f), Color.Transparent, sombraMueble(0.14f)), xL, xR), tabla.topLeft, tabla.size)
        vetas(tabla, (if (o) NOCHE else NOGAL_OSC).copy(alpha = 0.3f), 5240, 0.9f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.s(0.3f), tabla.topLeft, Size(tabla.width, 1.4f * u))
        for (k in 0 until 5) {
            val x = w * (0.08f + 0.84f * azar(5241, k))
            drawLine(tn(Offset(x, w * TABLA)).b(0.3f), Offset(x, w * (TABLA + 0.01f + 0.015f * azar(5242, k))), Offset(x + w * 0.03f, w * (TABLA + 0.014f + 0.012f * azar(5243, k))), 0.6f * u)
        }
        // El frente, en franjas que se reparten la luz de la lucerna de izquierda a derecha.
        val tramos = 10
        val yF = w * FRENTE; val yC = w * CANTO
        val y1 = yF + (yC - yF) * 0.12f   // filete
        val y2 = yF + (yC - yF) * 0.42f   // ovas
        val y3 = yF + (yC - yF) * 0.72f   // dentículos
        for (k in 0 until tramos) {
            val x0 = xL + (xR - xL) * k / tramos
            val x1 = xL + (xR - xL) * (k + 1) / tramos
            val tk = tn(Offset((x0 + x1) / 2, (yF + yC) / 2))
            drawRect(tk.c(NOGAL_LUZ), Offset(x0, yF), Size(x1 - x0 + 0.5f, y1 - yF))
            drawRect(tk.c(mezcla(NOGAL, NOGAL_OSC, 0.2f)), Offset(x0, y1), Size(x1 - x0 + 0.5f, y2 - y1))
            drawRect(tk.c(NOGAL_OSC), Offset(x0, y2), Size(x1 - x0 + 0.5f, y3 - y2))
            drawRect(tk.c(mezcla(NOGAL, NOGAL_LUZ, 0.2f)), Offset(x0, y3), Size(x1 - x0 + 0.5f, yC - y3))
            drawRect(tk.b(0.65f), Offset(x0, yF), Size(x1 - x0 + 0.5f, 1.2f * u))
            drawRect(tk.b(0.3f), Offset(x0, y3), Size(x1 - x0 + 0.5f, 0.9f * u))
        }
        drawRect(t.s(0.35f), Offset(xL, y1), Size(xR - xL, 1f * u))
        // Las ovas y los dardos.
        val ov = (y2 - y1)
        var x = xL + ov * 0.6f
        while (x < xR - ov * 0.4f) {
            val tt = tn(Offset(x, (y1 + y2) / 2))
            drawOval(tt.s(0.4f), Offset(x - ov * 0.42f, y1 + ov * 0.1f), Size(ov * 0.84f, ov * 0.86f))
            drawOval(tt.c(mezcla(NOGAL, NOGAL_LUZ, 0.6f)), Offset(x - ov * 0.34f, y1 + ov * 0.12f), Size(ov * 0.62f, ov * 0.72f))
            drawOval(tt.b(0.45f), Offset(x - ov * 0.28f, y1 + ov * 0.16f), Size(ov * 0.26f, ov * 0.36f))
            drawOval(tt.s(0.25f), Offset(x - ov * 0.02f, y1 + ov * 0.4f), Size(ov * 0.28f, ov * 0.42f))
            drawLine(tt.c(NOGAL_LUZ), Offset(x + ov * 0.5f, y1 + ov * 0.1f), Offset(x + ov * 0.5f, y2 - ov * 0.1f), 1.1f * u)
            x += ov
        }
        // Los dentículos, con su sombra a la derecha y abajo.
        val dh = y3 - y2
        x = xL + dh * 0.3f
        while (x < xR - dh * 0.7f) {
            val tt = tn(Offset(x, (y2 + y3) / 2))
            drawRect(tt.s(0.35f), Offset(x + 1.5f * u, y2 + 1.5f * u), Size(dh * 0.62f, dh * 0.82f))
            drawRect(tt.c(mezcla(NOGAL, NOGAL_LUZ, 0.45f)), Offset(x, y2), Size(dh * 0.62f, dh * 0.8f))
            drawRect(tt.b(0.4f), Offset(x, y2), Size(dh * 0.62f, 1f * u))
            drawRect(tt.b(0.2f), Offset(x, y2), Size(1f * u, dh * 0.8f))
            x += dh
        }
        drawRect(t.s(0.3f), Offset(xL, yC - 1f * u), Size(xR - xL, 1f * u))
        // La cabeza de la cornisa: su costado derecho, en sombra.
        val tr = tn(Offset(xR, (yF + yC) / 2))
        drawPath(poligono(xR, yF, xR + w * 0.006f, yF + w * 0.004f, xR + w * 0.006f, yC - w * 0.002f, xR, yC), tr.c(NOGAL_OSC))
    }

    /** El frontón bajo: el tímpano rehundido con el clípeo tallado, las cornisas inclinadas y las acróteras. */
    private fun DrawScope.fronton(w: Float) {
        val u = density
        val cx = w * 0.5f
        val base = w * (TABLA + 0.014f)
        val apice = w * 0.392f
        val a = w * 0.145f
        val t = tn(Offset(cx, (base + apice) / 2))
        val todo = poligono(cx - a, base, cx, apice, cx + a, base)
        sombraEnPared(todo, Offset(cx, apice), w * TABLA + w * 0.02f, 0.2f)
        drawPath(todo, t.c(NOGAL))
        // El tímpano rehundido.
        val ins = w * 0.012f
        val tim = poligono(cx - a + ins * 2.6f, base - ins * 0.5f, cx, apice + ins * 1.4f, cx + a - ins * 2.6f, base - ins * 0.5f)
        drawPath(tim, t.c(mezcla(NOGAL, NOGAL_OSC, 0.45f)))
        clipPath(tim) {
            drawPath(poligono(cx - a, base - ins * 0.5f, cx, apice + ins * 1.4f, cx, apice + ins * 3f, cx - a, base + ins), t.s(0.25f))
        }
        // Las cornisas inclinadas: la de la izquierda mira a la luz; la de la derecha, en sombra.
        val izq = poligono(cx - a, base, cx, apice, cx, apice + ins * 1.3f, cx - a + ins * 2.2f, base)
        val der = poligono(cx + a, base, cx, apice, cx, apice + ins * 1.3f, cx + a - ins * 2.2f, base)
        drawPath(izq, t.c(mezcla(NOGAL_LUZ, NOGAL, 0.2f)))
        drawPath(der, tn(Offset(cx + a * 0.5f, (base + apice) / 2)).c(mezcla(NOGAL, NOGAL_LUZ, 0.25f)))
        drawPath(der, t.s(if (esNoche) 0.05f else 0.18f))
        drawLine(t.b(0.6f), Offset(cx - a, base), Offset(cx, apice), 1.2f * u)
        drawLine(tn(Offset(cx + a * 0.5f, apice)).b(0.5f), Offset(cx, apice), Offset(cx + a, base), 1f * u)
        // El clípeo: el escudo redondo con su umbo y el borde.
        val cc = Offset(cx, base - (base - apice) * 0.36f)
        val r = (base - apice) * 0.24f
        drawCircle(t.s(0.35f), r, cc + Offset(r * 0.14f, r * 0.16f))
        drawCircle(t.c(mezcla(NOGAL, NOGAL_LUZ, 0.5f)), r, cc)
        drawCircle(t.s(0.25f), r * 0.75f, cc, style = Stroke(1f * u))
        drawCircle(t.c(NOGAL_LUZ), r * 0.3f, cc)
        drawCircle(t.b(0.55f), r * 0.12f, cc - Offset(r * 0.1f, r * 0.1f))
        drawArc(t.b(0.45f), 180f, 90f, false, Offset(cc.x - r, cc.y - r), Size(r * 2, r * 2), style = Stroke(1f * u))
        // Las acróteras: palmetas talladas en el ápice y en los extremos.
        listOf(Offset(cx, apice), Offset(cx - a, base), Offset(cx + a, base)).forEachIndexed { i, p ->
            val s = w * (if (i == 0) 0.022f else 0.016f)
            val tt = tn(p)
            for (k in -2..2) {
                val ang = Math.toRadians(-90.0 + k * 26.0)
                val q = Offset(p.x + s * cos(ang).toFloat(), p.y - s * 0.2f + s * sin(ang).toFloat())
                drawLine(tt.s(0.3f), p + Offset(1f * u, 0f), q + Offset(1f * u, 1f * u), s * 0.26f, StrokeCap.Round)
                drawLine(tt.c(mezcla(NOGAL, NOGAL_LUZ, 0.6f)), p, q, s * 0.24f, StrokeCap.Round)
            }
            drawCircle(tt.c(NOGAL_LUZ), s * 0.24f, p)
        }
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El casco de legionario (galo imperial), de pie sobre la cornisa y de perfil hacia la izquierda: la calota de hierro con sus
     * cejas repujadas y el botón de la cimera, la visera y el ribete de bronce, el guardanuca con sus nervios, la oreja abierta,
     * la carrillera colgando con sus rosetas y el asa del guardanuca.
     */
    private fun DrawScope.casco(pie: Offset) {
        val u = density
        val w = anchoW
        val s = w * 0.25f
        val x0 = pie.x - s * 0.5f
        // Apoyado en la carrillera y en el canto del guardanuca: todo el casco va girado unos grados hacia atrás.
        val giro = Math.toRadians(7.0)
        val cg = cos(giro).toFloat(); val sg = sin(giro).toFloat()
        fun p(fx: Float, fy: Float): Offset {
            val dx = fx - 0.38f
            return Offset(x0 + s * (0.38f + dx * cg - fy * sg), pie.y + s * (dx * sg + fy * cg))
        }
        val t = tn(p(0.5f, -0.4f))
        val der = luzDerecha(pie.x)
        val calota = Path().apply {
            moveTo(p(0.13f, -0.27f).x, p(0.13f, -0.27f).y)
            val a = p(0.09f, -0.52f); val b = p(0.27f, -0.7f); val c = p(0.48f, -0.7f)
            cubicTo(a.x, a.y, b.x, b.y, c.x, c.y)
            val d = p(0.69f, -0.7f); val e = p(0.83f, -0.55f); val f = p(0.82f, -0.32f)
            cubicTo(d.x, d.y, e.x, e.y, f.x, f.y)
            val g = p(0.6f, -0.27f); val h = p(0.35f, -0.25f); val i = p(0.13f, -0.27f)
            cubicTo(g.x, g.y, h.x, h.y, i.x, i.y)
            close()
        }
        // El guardanuca: una lámina ancha que nace de toda la parte de atrás del borde y se abre hacia abajo hasta la tabla.
        val nuca = Path().apply {
            val a = p(0.6f, -0.3f); moveTo(a.x, a.y)
            val b = p(0.78f, -0.36f); val c = p(0.88f, -0.3f); val d = p(0.95f, -0.22f)
            cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            val e = p(1.0f, -0.17f); val f = p(1.06f, -0.14f); val g = p(1.09f, -0.11f)
            cubicTo(e.x, e.y, f.x, f.y, g.x, g.y)
            val h = p(1.07f, -0.085f); lineTo(h.x, h.y)
            val i = p(0.95f, -0.09f); val j = p(0.78f, -0.13f); val k = p(0.62f, -0.17f)
            cubicTo(i.x, i.y, j.x, j.y, k.x, k.y)
            close()
        }
        // La carrillera: el borde de delante en curva (la ceja del ojo y la boca), el de atrás casi recto.
        val carrillera = Path().apply {
            val a = p(0.22f, -0.28f); moveTo(a.x, a.y)
            val b = p(0.5f, -0.28f); lineTo(b.x, b.y)
            val c = p(0.52f, -0.17f); val d = p(0.5f, -0.06f); val e = p(0.44f, 0f)
            cubicTo(c.x, c.y, d.x, d.y, e.x, e.y)
            val f = p(0.32f, 0f); lineTo(f.x, f.y)
            val g = p(0.26f, -0.05f); val h = p(0.32f, -0.12f); val i = p(0.28f, -0.17f)
            cubicTo(g.x, g.y, h.x, h.y, i.x, i.y)
            val j = p(0.22f, -0.2f); val k = p(0.2f, -0.24f); val l = p(0.22f, -0.28f)
            cubicTo(j.x, j.y, k.x, k.y, l.x, l.y)
            close()
        }
        val visera = poligono(p(0.04f, -0.31f).x, p(0.04f, -0.31f).y, p(0.2f, -0.29f).x, p(0.2f, -0.29f).y, p(0.2f, -0.24f).x, p(0.2f, -0.24f).y, p(0.02f, -0.27f).x, p(0.02f, -0.27f).y)
        val todo = Path().apply { addPath(calota); addPath(nuca); addPath(carrillera); addPath(visera) }
        sombraEnPared(todo, p(0.5f, -0.4f), w * TABLA + w * 0.005f, 0.24f)
        contacto(p(0.66f, 0f), s * 0.95f, s * 0.08f, 0.45f)
        val hierro = t.c(HIERRO)
        val bronce = t.c(BRONCE)
        // El hueco de dentro, que se ve por la oreja y bajo el guardanuca.
        drawPath(poligono(p(0.48f, -0.28f).x, p(0.48f, -0.28f).y, p(0.64f, -0.29f).x, p(0.64f, -0.29f).y, p(0.63f, -0.17f).x, p(0.63f, -0.17f).y, p(0.5f, -0.1f).x, p(0.5f, -0.1f).y), t.c(mezcla(CUERO_OSC, NOCHE, 0.5f)))
        // El guardanuca: el hierro con dos nervios repujados, la cara de abajo en sombra y el ribete de bronce.
        drawPath(nuca, hierro)
        clipPath(nuca) {
            val bajo = Path().apply { val a = p(0.62f, -0.15f); moveTo(a.x, a.y); val b = p(0.8f, -0.11f); val c = p(0.95f, -0.08f); val e = p(1.09f, -0.08f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
            drawPath(bajo, t.s(0.3f), style = Stroke(s * 0.07f))
            val arriba = Path().apply { val a = p(0.66f, -0.33f); moveTo(a.x, a.y); val b = p(0.8f, -0.36f); val c = p(0.9f, -0.28f); val e = p(1.06f, -0.15f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
            drawPath(arriba, if (esNoche) LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)) else t.b(0.2f), style = Stroke(s * 0.06f))
            listOf(0f, 0.04f).forEach { dd ->
                val n = Path().apply { val a = p(0.64f, -0.26f + dd); moveTo(a.x, a.y); val b = p(0.8f, -0.3f + dd); val c = p(0.92f, -0.21f + dd); val e = p(1.06f, -0.13f + dd * 0.4f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
                drawPath(n, t.b(0.45f), style = Stroke(1.4f * u))
                translate(0f, 1.5f * u) { drawPath(n, t.s(0.45f), style = Stroke(1.1f * u)) }
            }
        }
        val ribete = Path().apply { val a = p(0.64f, -0.17f); moveTo(a.x, a.y); val b = p(0.8f, -0.13f); val c = p(0.95f, -0.09f); val e = p(1.08f, -0.087f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
        drawPath(ribete, bronce, style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(ribete, t.c(BRONCE_LUZ).copy(alpha = 0.8f), style = Stroke(0.8f * u))
        // El asa del guardanuca, colgando debajo.
        drawArc(bronce, 0f, 180f, false, p(0.84f, -0.14f), Size(s * 0.09f, s * 0.06f), style = Stroke(1.5f * u))
        // La calota.
        drawPath(calota, hierro)
        clipPath(calota) {
            val r = Rect(p(0.08f, -0.72f), p(0.84f, -0.24f))
            if (!der) {
                drawOval(t.b(0.22f), Offset(r.left, r.top), Size(r.width * 0.55f, r.height * 0.8f))
                drawOval(t.b(0.3f), Offset(r.left + r.width * 0.1f, r.top + r.height * 0.06f), Size(r.width * 0.28f, r.height * 0.4f))
                drawRect(t.s(0.25f), Offset(r.left + r.width * 0.62f, r.top), Size(r.width * 0.4f, r.height))
                drawRect(t.s(0.16f), Offset(r.left, r.top + r.height * 0.75f), Size(r.width, r.height * 0.3f))
            } else {
                drawOval(t.b(0.22f), Offset(r.left + r.width * 0.4f, r.top - r.height * 0.05f), Size(r.width * 0.7f, r.height * 0.9f))
                drawOval(t.b(0.25f), Offset(r.left + r.width * 0.62f, r.top + r.height * 0.06f), Size(r.width * 0.3f, r.height * 0.45f))
                drawOval(t.s(0.18f), Offset(r.left - r.width * 0.3f, r.top), Size(r.width * 0.75f, r.height * 1.2f))
                drawOval(t.s(0.18f), Offset(r.left - r.width * 0.3f, r.top + r.height * 0.1f), Size(r.width * 0.55f, r.height * 1.1f))
            }
            // Las cejas repujadas sobre la frente.
            listOf(0f, 0.06f).forEach { dd ->
                val c = Path().apply { val a = p(0.12f, -0.4f - dd); moveTo(a.x, a.y); val b = p(0.18f, -0.5f - dd); val c2 = p(0.3f, -0.52f - dd); val e = p(0.38f, -0.46f - dd); cubicTo(b.x, b.y, c2.x, c2.y, e.x, e.y) }
                drawPath(c, t.b(0.5f), style = Stroke(1.5f * u))
                translate(0.8f * u, 1.6f * u) { drawPath(c, t.s(0.45f), style = Stroke(1.2f * u)) }
            }
            // El óxido y las rozaduras del hierro.
            for (k in 0 until 6) drawCircle(t.c(mezcla(HIERRO, OCRE, 0.5f)).copy(alpha = 0.25f), s * (0.012f + 0.015f * azar(5300, k)), p(0.2f + 0.55f * azar(5301, k), -0.3f - 0.3f * azar(5302, k)))
            // El ribete de bronce del borde.
            val rib = Path().apply { val a = p(0.13f, -0.3f); moveTo(a.x, a.y); val b = p(0.35f, -0.28f); val c = p(0.6f, -0.3f); val e = p(0.82f, -0.35f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
            drawPath(rib, bronce, style = Stroke(3f * u))
            drawPath(rib, t.c(BRONCE_LUZ).copy(alpha = 0.8f), style = Stroke(1f * u))
        }
        // El brillo largo de la calota.
        val brillo = Path().apply { val a = p(0.16f, -0.42f); moveTo(a.x, a.y); val b = p(0.18f, -0.58f); val c = p(0.3f, -0.66f); val e = p(0.42f, -0.67f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
        if (!esNoche) drawPath(brillo, luzMueble(0.75f), style = Stroke(2f * u, cap = StrokeCap.Round))
        else {
            val b2 = Path().apply { val a = p(0.6f, -0.67f); moveTo(a.x, a.y); val b = p(0.72f, -0.64f); val c = p(0.8f, -0.55f); val e = p(0.81f, -0.42f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
            drawPath(b2, LLAMA.copy(alpha = (0.9f * t.k).coerceAtMost(0.85f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            drawPath(calota, LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.4f)), style = Stroke(1f * u))
        }
        // El botón de la cimera.
        val bot = p(0.47f, -0.705f)
        drawRect(bronce, Offset(bot.x - s * 0.025f, bot.y - s * 0.03f), Size(s * 0.05f, s * 0.035f))
        drawOval(t.c(BRONCE_LUZ), Offset(bot.x - s * 0.03f, bot.y - s * 0.045f), Size(s * 0.06f, s * 0.025f))
        destello(Offset(bot.x - s * 0.01f, bot.y - s * 0.035f), 1f * u, t)
        // La visera de bronce.
        drawPath(visera, bronce)
        drawLine(t.c(BRONCE_LUZ), p(0.04f, -0.31f), p(0.2f, -0.29f), 1.2f * u)
        drawLine(t.s(0.4f), p(0.02f, -0.27f), p(0.2f, -0.24f), 1f * u)
        // La carrillera: el hierro con su ribete, dos rosetas de bronce y la bisagra arriba.
        drawPath(carrillera, mezcla(hierro, t.c(NOGAL_OSC), 0.15f))
        clipPath(carrillera) {
            cilindro(Rect(p(0.2f, -0.28f), p(0.57f, 0f)), t, 1.3f, der)
            drawRect(t.s(0.3f), Offset(x0, pie.y - s * 0.06f), Size(s, s * 0.1f))
        }
        drawPath(carrillera, bronce, style = Stroke(1.6f * u))
        listOf(p(0.36f, -0.18f), p(0.42f, -0.08f)).forEach { c ->
            drawCircle(t.s(0.35f), s * 0.026f, c + Offset(1f * u, 1.2f * u))
            drawCircle(bronce, s * 0.024f, c)
            drawCircle(t.c(BRONCE_LUZ), s * 0.011f, c - Offset(s * 0.006f, s * 0.006f))
        }
        drawRect(bronce, Offset(p(0.33f, -0.29f).x, p(0.33f, -0.29f).y), Size(s * 0.14f, s * 0.035f))
        destello(p(0.35f, -0.28f), 1f * u, t)
        // La guarda de la oreja.
        val oreja = Path().apply { val a = p(0.5f, -0.28f); moveTo(a.x, a.y); val b = p(0.52f, -0.19f); val c = p(0.6f, -0.18f); val e = p(0.63f, -0.29f); cubicTo(b.x, b.y, c.x, c.y, e.x, e.y) }
        drawPath(oreja, bronce, style = Stroke(2.2f * u))
        drawPath(oreja, t.c(BRONCE_LUZ).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        // Remaches del ribete.
        listOf(0.2f, 0.62f, 0.77f).forEach { f -> drawCircle(t.c(BRONCE_LUZ), 1f * u, p(f, -0.3f - 0.02f * f)) }
    }

    /** El rollo a medio leer: el cilindro tumbado en la tabla y el pliego escrito que cae por el canto de la cornisa y se riza. */
    private fun DrawScope.rollo(w: Float) {
        val u = density
        val x0 = w * 0.175f; val x1 = w * 0.285f
        val yR = w * (TABLA + 0.012f)
        val d = w * 0.024f
        val t = tn(Offset((x0 + x1) / 2, w * 0.5f))
        val caida = w * 0.66f
        // El pliego: de la base del rollo hacia adelante, sobre el canto, y cuelga con una ola y la punta rizada.
        val pliego = Path().apply {
            moveTo(x0 + w * 0.006f, yR + d * 0.5f)
            lineTo(x1 - w * 0.004f, yR + d * 0.5f)
            lineTo(x1 + w * 0.002f, w * FRENTE)
            cubicTo(x1 + w * 0.008f, w * 0.54f, x1 - w * 0.002f, w * 0.6f, x1 + w * 0.006f, caida)
            lineTo(x0 + w * 0.012f, caida + w * 0.008f)
            cubicTo(x0 + w * 0.004f, w * 0.6f, x0 + w * 0.012f, w * 0.54f, x0 + w * 0.002f, w * FRENTE)
            close()
        }
        // Su sombra en el frente de la cornisa y en el friso.
        if (!esNoche) translate(w * 0.012f, w * 0.006f) { drawPath(pliego, sombraMueble(0.28f)) }
        drawPath(pliego, t.c(PAPIRO))
        clipPath(pliego) {
            // La parte de arriba, en la tabla, más clara; lo que cuelga, a la sombra de su propio pliegue.
            drawRect(t.b(0.25f), Offset(x0, yR), Size(x1 - x0, w * FRENTE - yR))
            drawRect(t.s(0.12f), Offset(x0, w * FRENTE), Size(x1 - x0, caida - w * FRENTE + w * 0.02f))
            drawRect(t.s(0.15f), Offset(x1 - w * 0.02f, w * FRENTE), Size(w * 0.03f, caida))
            drawRect(t.s(0.25f), Offset(x0, w * FRENTE), Size(x1 - x0, 1.6f * u))
            // Las fibras y dos columnas de escritura.
            var y = yR + d
            while (y < caida) { drawLine(t.c(PAPIRO_OSC).copy(alpha = 0.18f), Offset(x0, y), Offset(x1 + w * 0.01f, y), 0.5f * u); y += 2.6f * u }
            listOf(x0 + w * 0.016f, x0 + w * 0.062f).forEach { cx ->
                var yy = w * FRENTE + w * 0.008f
                var k = 0
                while (yy < caida - w * 0.008f) {
                    val l = w * (0.03f + 0.012f * azar(5310, k + (cx * 10).toInt()))
                    drawLine(t.c(TINTA).copy(alpha = 0.45f), Offset(cx, yy), Offset(cx + l, yy), 0.9f * u)
                    yy += 3.4f * u; k++
                }
            }
        }
        // La punta rizada.
        drawOval(t.c(mezcla(PAPIRO, PAPIRO_OSC, 0.4f)), Offset(x0 + w * 0.008f, caida - w * 0.004f), Size(x1 - x0, w * 0.014f))
        drawOval(t.s(0.3f), Offset(x0 + w * 0.012f, caida + w * 0.003f), Size(x1 - x0 - w * 0.006f, w * 0.006f))
        // El rollo tumbado, con su luz arriba y las perillas de hueso.
        val r = Rect(x0, yR - d * 0.5f, x1, yR + d * 0.5f)
        contacto(Offset(r.center.x, r.bottom), r.width * 1.05f, d * 0.4f, 0.35f)
        drawRoundRect(t.c(mezcla(PAPIRO, PAPIRO_OSC, 0.2f)), r.topLeft, r.size, androidx.compose.ui.geometry.CornerRadius(d * 0.3f))
        clipRect(r.left, r.top, r.right, r.bottom) { tumbado(r, t, 1.2f) }
        listOf(r.left, r.right).forEach { x ->
            drawOval(t.c(PAPIRO), Offset(x - d * 0.18f, r.top), Size(d * 0.36f, d))
            drawCircle(t.c(HUESO), d * 0.22f, Offset(x + (if (x < r.center.x) -d * 0.2f else d * 0.2f), r.center.y))
            drawCircle(t.b(0.6f), d * 0.09f, Offset(x + (if (x < r.center.x) -d * 0.25f else d * 0.15f), r.center.y - d * 0.08f))
        }
        // El sillybos rojo que cuelga del cabo.
        val sx = r.right + d * 0.2f
        drawLine(t.c(PAPIRO_OSC), Offset(sx, r.center.y), Offset(sx + d * 0.3f, r.bottom + d * 0.5f), 0.7f * u)
        val sb = Rect(sx - d * 0.05f, r.bottom + d * 0.5f, sx + d * 0.75f, r.bottom + d * 0.95f)
        rotate(12f, sb.topLeft) { drawRect(t.c(ROJO), sb.topLeft, sb.size); drawRect(t.s(0.2f), Offset(sb.left, sb.bottom - 1f * u), Size(sb.width, 1f * u)) }
    }

    /** El tintero doble de bronce: dos cuerpos unidos por un puente, con sus tapas, y los cálamos (uno metido, otro tumbado). */
    private fun DrawScope.tintero(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.03f))
        val der = luzDerecha(pie.x)
        val rr = w * 0.0135f; val h = w * 0.036f
        val cs = listOf(pie.x - rr * 1.05f, pie.x + rr * 1.05f)
        val todo = Path().apply { cs.forEach { addRect(Rect(it - rr, pie.y - h, it + rr, pie.y)) } }
        sombraEnPared(todo, Offset(pie.x, pie.y - h), w * TABLA + w * 0.02f, 0.2f)
        contacto(Offset(pie.x + rr * 0.3f, pie.y), rr * 5f, w * 0.008f, 0.4f)
        // Un cálamo metido en el de la derecha, inclinado hacia atrás.
        drawLine(t.s(0.3f), Offset(cs[1] + 1.5f * u, pie.y - h), Offset(cs[1] + w * 0.03f, pie.y - h - w * 0.06f), 2f * u)
        drawLine(t.c(mezcla(PAPIRO_OSC, OCRE, 0.4f)), Offset(cs[1], pie.y - h), Offset(cs[1] + w * 0.028f, pie.y - h - w * 0.062f), 1.8f * u, StrokeCap.Round)
        drawLine(t.c(mezcla(PAPIRO_OSC, NOGAL, 0.3f)), Offset(cs[1] + w * 0.008f, pie.y - h - w * 0.017f), Offset(cs[1] + w * 0.009f, pie.y - h - w * 0.02f), 2.4f * u)
        cs.forEachIndexed { i, cx ->
            val r = Rect(cx - rr, pie.y - h, cx + rr, pie.y)
            drawRect(t.c(BRONCE), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.4f, der) }
            drawRect(t.c(BRONCE_LUZ), Offset(r.left, r.top + h * 0.2f), Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.top + h * 0.82f), Size(r.width, 1f * u))
            drawOval(t.c(mezcla(BRONCE, BRONCE_LUZ, 0.4f)), Offset(r.left - 1f * u, r.top - rr * 0.35f), Size(r.width + 2f * u, rr * 0.7f))
            if (i == 1) drawOval(t.c(TINTA), Offset(r.left + rr * 0.4f, r.top - rr * 0.18f), Size(rr * 1.2f, rr * 0.36f))
            else drawCircle(t.c(BRONCE_LUZ), rr * 0.25f, Offset(cx, r.top - rr * 0.1f))
        }
        destello(Offset(cs[if (der) 1 else 0] + (if (der) rr * 0.5f else -rr * 0.5f), pie.y - h * 0.6f), 1f * u, t)
        // El otro cálamo, tumbado delante, con la punta manchada de tinta.
        val a = Offset(pie.x - w * 0.04f, pie.y + w * 0.012f); val b = Offset(pie.x + w * 0.02f, pie.y + w * 0.006f)
        drawLine(t.s(0.3f), a + Offset(1f * u, 1.5f * u), b + Offset(1f * u, 1.5f * u), 2f * u)
        drawLine(t.c(mezcla(PAPIRO_OSC, OCRE, 0.4f)), a, b, 1.8f * u, StrokeCap.Round)
        drawLine(t.c(TINTA), a, a + (b - a) * 0.12f, 1.8f * u, StrokeCap.Round)
    }

    /** La bolsa de cuero con su cordón, desplomada; dos monedas sueltas y un dado de hueso. */
    private fun DrawScope.bolsa(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.02f))
        val der = luzDerecha(pie.x)
        val bw = w * 0.05f; val bh = w * 0.036f
        val cuerpo = Path().apply {
            moveTo(pie.x - bw * 0.15f, pie.y - bh * 0.75f)
            cubicTo(pie.x - bw * 0.55f, pie.y - bh * 0.6f, pie.x - bw * 0.62f, pie.y, pie.x - bw * 0.2f, pie.y)
            lineTo(pie.x + bw * 0.3f, pie.y)
            cubicTo(pie.x + bw * 0.62f, pie.y, pie.x + bw * 0.5f, pie.y - bh * 0.6f, pie.x + bw * 0.12f, pie.y - bh * 0.75f)
            close()
        }
        sombraEnPared(cuerpo, Offset(pie.x, pie.y - bh), w * TABLA + w * 0.02f, 0.18f)
        contacto(Offset(pie.x + bw * 0.1f, pie.y), bw * 1.3f, w * 0.008f, 0.45f)
        drawPath(cuerpo, t.c(CUERO))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - bw * 0.6f, pie.y - bh, pie.x + bw * 0.6f, pie.y), t, 1.3f, der)
            drawRect(t.s(0.25f), Offset(pie.x - bw, pie.y - bh * 0.18f), Size(bw * 2, bh * 0.2f))
            // Los pliegues que deja el cordón.
            listOf(-0.25f, 0.05f, 0.3f).forEach { f -> drawLine(t.s(0.3f), Offset(pie.x + bw * f * 0.4f, pie.y - bh * 0.72f), Offset(pie.x + bw * f, pie.y - bh * 0.25f), 0.8f * u) }
        }
        // El cuello fruncido y la boca, con el cordón y sus cabos.
        drawOval(t.c(mezcla(CUERO, CUERO_OSC, 0.3f)), Offset(pie.x - bw * 0.2f, pie.y - bh * 1.02f), Size(bw * 0.4f, bh * 0.32f))
        drawLine(t.c(ROJO), Offset(pie.x - bw * 0.18f, pie.y - bh * 0.75f), Offset(pie.x + bw * 0.16f, pie.y - bh * 0.78f), 1.4f * u)
        drawLine(t.c(ROJO), Offset(pie.x + bw * 0.14f, pie.y - bh * 0.78f), Offset(pie.x + bw * 0.42f, pie.y - bh * 0.3f), 1f * u, StrokeCap.Round)
        drawLine(t.c(ROJO), Offset(pie.x + bw * 0.14f, pie.y - bh * 0.78f), Offset(pie.x + bw * 0.55f, pie.y - bh * 0.55f), 1f * u, StrokeCap.Round)
        // Las monedas: un denario de plata y un as de bronce.
        listOf(Triple(-0.85f, 0.25f, HUESO), Triple(-0.6f, 0.5f, BRONCE)).forEach { (fx, fy, col) ->
            val c = Offset(pie.x + bw * fx, pie.y + w * 0.004f * fy)
            drawOval(t.s(0.35f), Offset(c.x - w * 0.009f + 1f * u, c.y - w * 0.003f + 1f * u), Size(w * 0.018f, w * 0.006f))
            drawOval(t.c(mezcla(col, HIERRO, if (col == HUESO) 0.35f else 0f)), Offset(c.x - w * 0.009f, c.y - w * 0.003f), Size(w * 0.018f, w * 0.006f))
            drawOval(t.b(0.4f), Offset(c.x - w * 0.006f, c.y - w * 0.002f), Size(w * 0.008f, w * 0.002f))
        }
        // El dado de hueso, con sus puntos.
        val dd = w * 0.012f
        val dc = Offset(pie.x + bw * 0.85f, pie.y + w * 0.004f)
        drawRect(t.s(0.35f), Offset(dc.x - dd / 2 + 1.5f * u, dc.y - dd + 1f * u), Size(dd, dd))
        drawRect(t.c(HUESO), Offset(dc.x - dd / 2, dc.y - dd), Size(dd, dd))
        drawPath(poligono(dc.x - dd / 2, dc.y - dd, dc.x - dd * 0.2f, dc.y - dd * 1.3f, dc.x + dd * 0.8f, dc.y - dd * 1.3f, dc.x + dd / 2, dc.y - dd), t.c(mezcla(HUESO, LUZ_MUEBLE, 0.5f)))
        drawPath(poligono(dc.x + dd / 2, dc.y - dd, dc.x + dd * 0.8f, dc.y - dd * 1.3f, dc.x + dd * 0.8f, dc.y - dd * 0.3f, dc.x + dd / 2, dc.y), t.c(mezcla(HUESO, PAPIRO_OSC, 0.5f)))
        listOf(Offset(-0.25f, -0.75f), Offset(0f, -0.5f), Offset(0.25f, -0.25f)).forEach { f -> drawCircle(t.c(TINTA), dd * 0.09f, Offset(dc.x + dd * f.x, dc.y + dd * f.y)) }
    }

    /**
     * El candelabro de bronce de trípode, con sus patas de león, el fuste con sus nudos y el platillo; encima, la lucerna con su
     * disco, su asa y su pico. De noche, la llama en el pico.
     */
    private fun DrawScope.candelabro(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, w * 0.3f))
        val o = esNoche
        val bronce = t.c(BRONCE)
        val plato = w * 0.205f
        // Las patas: tres de león, dos a los lados y una de frente.
        val nudoP = Offset(pie.x, pie.y - w * 0.04f)
        val patas = listOf(Offset(pie.x - w * 0.034f, pie.y + w * 0.004f), Offset(pie.x + w * 0.036f, pie.y + w * 0.002f), Offset(pie.x + w * 0.004f, pie.y + w * 0.012f))
        val sil = Path().apply {
            addRect(Rect(pie.x - w * 0.004f, plato, pie.x + w * 0.004f, nudoP.y))
            patas.forEach { q -> moveTo(nudoP.x, nudoP.y); lineTo(q.x, q.y); lineTo(q.x + w * 0.006f, q.y); close() }
            addOval(Rect(pie.x - w * 0.045f, plato - w * 0.03f, pie.x + w * 0.035f, plato))
        }
        if (!o) sombraEnPared(sil, Offset(pie.x, w * 0.3f), w * TABLA + w * 0.01f, 0.18f)
        patas.forEach { q -> contacto(q, w * 0.016f, w * 0.005f, 0.45f) }
        patas.forEachIndexed { i, q ->
            val tq = tn(q)
            val pata = Path().apply {
                moveTo(nudoP.x - w * 0.004f, nudoP.y)
                cubicTo(nudoP.x + (q.x - nudoP.x) * 0.3f, nudoP.y + w * 0.004f, q.x - (q.x - nudoP.x) * 0.1f, q.y - w * 0.03f, q.x, q.y - w * 0.006f)
                lineTo(q.x + w * 0.004f, q.y - w * 0.006f)
                cubicTo(q.x + (q.x - nudoP.x) * 0.05f, q.y - w * 0.026f, nudoP.x + (q.x - nudoP.x) * 0.4f, nudoP.y + w * 0.01f, nudoP.x + w * 0.004f, nudoP.y + w * 0.006f)
                close()
            }
            drawPath(pata, tq.c(if (i == 2) BRONCE else mezcla(BRONCE, NOGAL_OSC, 0.15f)))
            drawPath(pata, tq.b(0.3f), style = Stroke(0.6f * u))
            // La garra.
            drawOval(tq.c(BRONCE), Offset(q.x - w * 0.006f, q.y - w * 0.008f), Size(w * 0.014f, w * 0.008f))
            listOf(-0.004f, 0f, 0.004f).forEach { dx -> drawCircle(tq.c(BRONCE_LUZ), w * 0.0016f, Offset(q.x + w * (0.001f + dx), q.y - w * 0.002f)) }
        }
        // El fuste, con su luz y tres nudos.
        val fuste = Rect(pie.x - w * 0.0045f, plato, pie.x + w * 0.0045f, nudoP.y)
        drawRect(bronce, fuste.topLeft, fuste.size)
        for (k in 0 until 6) {
            val y0 = fuste.top + fuste.height * k / 6f
            val tk = tn(Offset(pie.x, y0))
            val r = Rect(fuste.left, y0, fuste.right, y0 + fuste.height / 6f + 0.5f)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, tk, 1.5f, luzDerecha(pie.x)) }
        }
        listOf(0.25f, 0.6f, 0.98f).forEach { f ->
            val y = fuste.top + fuste.height * f
            val tk = tn(Offset(pie.x, y))
            drawOval(tk.c(BRONCE), Offset(pie.x - w * 0.01f, y - w * 0.006f), Size(w * 0.02f, w * 0.012f))
            drawOval(tk.c(BRONCE_LUZ), Offset(pie.x - w * 0.008f, y - w * 0.005f), Size(w * 0.008f, w * 0.004f))
        }
        // El platillo.
        drawOval(bronce, Offset(pie.x - w * 0.026f, plato - w * 0.004f), Size(w * 0.052f, w * 0.01f))
        drawArc(t.c(BRONCE_LUZ), 180f, 180f, false, Offset(pie.x - w * 0.026f, plato - w * 0.004f), Size(w * 0.052f, w * 0.01f), style = Stroke(1f * u))
        // La lucerna: el cuerpo redondo con el disco, el asa a la derecha y el pico a la izquierda.
        val lb = Rect(pie.x - w * 0.028f, plato - w * 0.032f, pie.x + w * 0.028f, plato - w * 0.002f)
        val pico = Path().apply {
            moveTo(lb.left + w * 0.01f, lb.top + w * 0.006f)
            lineTo(LUZ.x * w - w * 0.002f, lb.top + w * 0.005f)
            cubicTo(LUZ.x * w - w * 0.012f, lb.top + w * 0.006f, LUZ.x * w - w * 0.012f, lb.bottom - w * 0.004f, LUZ.x * w - w * 0.002f, lb.bottom - w * 0.006f)
            lineTo(lb.left + w * 0.01f, lb.bottom - w * 0.004f)
            close()
        }
        drawPath(pico, bronce)
        drawPath(pico, t.b(0.25f))
        drawOval(bronce, lb.topLeft, lb.size)
        clipPath(Path().apply { addOval(lb) }) {
            tumbado(lb, t, 1.3f)
        }
        // El disco con su agujero de llenar, el borde y las volutas del pico.
        drawOval(t.c(mezcla(BRONCE, NOGAL_OSC, 0.25f)), Offset(lb.left + w * 0.008f, lb.top - w * 0.001f), Size(lb.width - w * 0.016f, w * 0.008f))
        drawCircle(t.c(TINTA), w * 0.0022f, Offset(lb.center.x + w * 0.003f, lb.top + w * 0.003f))
        drawCircle(t.c(BRONCE_LUZ), w * 0.003f, Offset(lb.left + w * 0.009f, lb.top + w * 0.012f))
        drawArc(t.c(BRONCE), -70f, 220f, false, Offset(lb.right - w * 0.004f, lb.top - w * 0.006f), Size(w * 0.014f, w * 0.016f), style = Stroke(1.6f * u))
        val boca = Offset(LUZ.x * w + w * 0.002f, lb.top + w * 0.004f)
        drawCircle(t.c(TINTA), w * 0.0025f, boca + Offset(w * 0.001f, w * 0.001f))
        if (o) {
            // La llama: el halo, el cuerpo y el corazón blanco.
            val lc = LUZ * w
            drawCircle(LLAMA.copy(alpha = 0.3f), w * 0.03f, lc)
            drawCircle(LLAMA.copy(alpha = 0.4f), w * 0.014f, lc)
            val llama = Path().apply {
                moveTo(lc.x, lc.y - w * 0.022f)
                cubicTo(lc.x + w * 0.009f, lc.y - w * 0.008f, lc.x + w * 0.007f, lc.y + w * 0.008f, lc.x, lc.y + w * 0.009f)
                cubicTo(lc.x - w * 0.007f, lc.y + w * 0.008f, lc.x - w * 0.009f, lc.y - w * 0.008f, lc.x, lc.y - w * 0.022f)
                close()
            }
            drawPath(llama, LLAMA)
            drawOval(Color(0xFFFFF4D8), Offset(lc.x - w * 0.003f, lc.y - w * 0.004f), Size(w * 0.006f, w * 0.011f))
            // El reflejo de la llama en el cuerpo de la lucerna y en el platillo.
            drawOval(LLAMA.copy(alpha = 0.6f), Offset(lb.left + w * 0.004f, lb.top + w * 0.004f), Size(w * 0.014f, w * 0.006f))
        } else {
            // Apagada: la mecha y un tizne en el pico.
            drawLine(t.c(TINTA), boca, boca + Offset(-w * 0.002f, -w * 0.006f), 1.4f * u, StrokeCap.Round)
            drawOval(t.c(TINTA).copy(alpha = 0.3f), Offset(boca.x - w * 0.006f, boca.y - w * 0.004f), Size(w * 0.01f, w * 0.006f))
            destello(Offset(lb.left + w * 0.012f, lb.top + w * 0.008f), 1.2f * u, t)
        }
    }

    // ---------------------------------------------------------------- al pie

    /** El escabel de patas torneadas, con unas tablillas de cera abiertas encima y el estilo atravesado. */
    private fun DrawScope.escabel(b: Rect, w: Float, hb: Float) {
        val u = density
        val x0 = w * 0.025f; val x1 = w * 0.31f
        val top = b.top + hb * 0.52f
        val fr = top + hb * 0.06f
        val pisa = b.top + hb * 0.96f
        val t = tn(Offset((x0 + x1) / 2, fr))
        contacto(Offset((x0 + x1) / 2 + w * 0.02f, pisa), (x1 - x0) * 1.05f, hb * 0.08f, 0.38f)
        // Las patas: dos de frente, torneadas, y una de atrás que asoma.
        listOf(x0 + w * 0.022f, x1 - w * 0.022f, x1 - w * 0.05f).forEachIndexed { i, x ->
            val y1 = if (i == 2) pisa - hb * 0.06f else pisa
            val tp = tn(Offset(x, y1))
            val r = Rect(x - w * 0.009f, fr, x + w * 0.009f, y1)
            drawRect(tp.c(if (i == 2) NOGAL_OSC else mezcla(BOJ, NOGAL, 0.45f)), r.topLeft, r.size)
            if (i < 2) {
                clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, tp, 1.4f) }
                listOf(0.25f, 0.55f).forEach { f -> drawOval(tp.c(mezcla(BOJ, NOGAL, 0.35f)), Offset(x - w * 0.012f, r.top + r.height * f - w * 0.004f), Size(w * 0.024f, w * 0.008f)) }
            }
        }
        // La tabla, vista un poco desde arriba, y su canto.
        val tabla = Rect(x0, top, x1, fr)
        drawRect(t.c(mezcla(BOJ, NOGAL_LUZ, 0.3f)), tabla.topLeft, tabla.size)
        drawRect(t.b(0.25f), tabla.topLeft, Size(tabla.width * 0.4f, tabla.height))
        drawRect(t.c(mezcla(BOJ, NOGAL, 0.4f)), Offset(x0, fr), Size(x1 - x0, hb * 0.07f))
        drawRect(t.b(0.4f), Offset(x0, fr), Size(x1 - x0, 1f * u))
        drawRect(t.s(0.3f), Offset(x0, fr + hb * 0.07f - 1.2f * u), Size(x1 - x0, 1.2f * u))
        vetas(Rect(x0, fr, x1, fr + hb * 0.07f), (if (esNoche) NOCHE else NOGAL).copy(alpha = 0.35f), 5400, 0.6f, grosor = 0.5f)
        // Las tablillas abiertas: dos marcos de boj con la cera negra, escrita, unidos por el cordel.
        val ty = top + hb * 0.03f
        listOf(0f, 1f).forEach { k ->
            val l = x0 + w * (0.025f + 0.13f * k)
            val r = l + w * 0.12f
            val tab = poligono(l + w * 0.006f, ty - hb * 0.06f, r + w * 0.004f, ty - hb * 0.06f, r, ty, l, ty)
            contactoT(Offset((l + r) / 2, ty + hb * 0.005f), w * 0.11f, hb * 0.03f, 0.3f, t)
            drawPath(tab, t.c(BOJ))
            drawPath(tab, t.b(0.3f), style = Stroke(0.8f * u))
            val cera = poligono(l + w * 0.013f, ty - hb * 0.05f, r - w * 0.003f, ty - hb * 0.05f, r - w * 0.006f, ty - hb * 0.01f, l + w * 0.008f, ty - hb * 0.01f)
            drawPath(cera, t.c(CERA))
            for (j in 0 until 3) {
                val yy = ty - hb * (0.04f - 0.011f * j)
                drawLine(t.c(mezcla(BOJ, PAPIRO, 0.5f)).copy(alpha = 0.6f), Offset(l + w * 0.016f, yy), Offset(r - w * (0.012f + 0.02f * azar(5401, j + 3 * k.toInt())), yy), 0.6f * u)
            }
            drawRect(t.c(mezcla(BOJ, NOGAL, 0.35f)), Offset(l, ty), Size(r - l, hb * 0.014f))
        }
        // El cordel entre las dos.
        drawLine(t.c(ROJO), Offset(x0 + w * 0.142f, ty - hb * 0.03f), Offset(x0 + w * 0.158f, ty - hb * 0.03f), 1.6f * u)
        // El estilo de hierro, atravesado: la punta y la espátula de borrar.
        val a = Offset(x0 + w * 0.07f, ty + hb * 0.015f); val c = Offset(x0 + w * 0.23f, ty - hb * 0.075f)
        drawLine(t.s(0.35f), a + Offset(1.5f * u, 2f * u), c + Offset(1.5f * u, 2f * u), 1.6f * u)
        drawLine(t.c(HIERRO), a, c, 1.6f * u, StrokeCap.Round)
        drawLine(t.c(HIERRO), c, c + (c - a) * 0.08f, 3.4f * u, StrokeCap.Square)
        drawLine(t.b(0.5f), a + Offset(0f, -0.6f * u), c + Offset(0f, -0.6f * u), 0.5f * u)
    }

    /** El scrinium: la caja cilíndrica de rollos, de madera forrada de cuero, con sus aros, su tapa y su correa; cortado por el borde. */
    private fun DrawScope.scrinium(b: Rect, w: Float, hb: Float) {
        val u = density
        val x0 = w * 0.795f; val x1 = w * 1.06f
        val arriba = b.top + hb * 0.3f
        val pisa = b.top + hb * 0.95f
        val eh = (x1 - x0) * 0.22f
        val t = tn(Offset((x0 + x1) / 2, (arriba + pisa) / 2))
        contacto(Offset((x0 + x1) / 2 - w * 0.02f, pisa), (x1 - x0) * 1.1f, eh * 0.6f, 0.4f)
        // Su sombra en el zócalo del armario, a la izquierda (la luz de día viene de la izquierda, así que cae casi detrás).
        val cuerpo = Rect(x0, arriba, x1, pisa)
        drawOval(t.c(CUERO_OSC), Offset(x0, pisa - eh / 2), Size(x1 - x0, eh))
        drawRect(t.c(mezcla(CUERO, ROJO, 0.25f)), cuerpo.topLeft, cuerpo.size)
        clipRect(x0, arriba, x1, pisa + eh / 2) {
            cilindro(Rect(x0, arriba, x1 + (x1 - x0) * 0.3f, pisa), t, 1.3f)
            pinceladas(cuerpo, if (esNoche) NOCHE else CUERO_OSC, 5410, 8, u, 0.15f)
            // Las costuras.
            var y = arriba + eh
            while (y < pisa - eh * 0.2f) { drawLine(t.c(PAPIRO_OSC).copy(alpha = 0.5f), Offset(x0 + (x1 - x0) * 0.12f, y), Offset(x0 + (x1 - x0) * 0.12f, y + 2f * u), 0.8f * u); y += 4.5f * u }
        }
        // Los aros de bronce.
        listOf(arriba + (pisa - arriba) * 0.35f, pisa - (pisa - arriba) * 0.08f).forEach { y ->
            drawArc(t.c(mezcla(BRONCE, NOGAL_OSC, 0.3f)), 0f, 180f, false, Offset(x0, y - eh / 2), Size(x1 - x0, eh), style = Stroke(4f * u))
            drawArc(t.c(BRONCE_LUZ), 30f, 150f, false, Offset(x0, y - eh / 2 - 1f * u), Size(x1 - x0, eh), style = Stroke(1.4f * u))
        }
        // La tapa: un casquete un poco más ancho, con su borde de bronce.
        val tapa = Rect(x0 - w * 0.008f, arriba - eh * 0.9f, x1 + w * 0.008f, arriba + eh * 0.25f)
        drawRect(t.s(0.3f), Offset(tapa.left, arriba + eh * 0.25f), Size(tapa.width, 2f * u))
        drawRect(t.c(CUERO_OSC), Offset(tapa.left, arriba - eh * 0.4f), Size(tapa.width, eh * 0.65f))
        clipRect(tapa.left, arriba - eh * 0.4f, tapa.right, arriba + eh * 0.25f) { cilindro(Rect(tapa.left, tapa.top, tapa.right + tapa.width * 0.3f, tapa.bottom), t, 1.2f) }
        drawOval(t.c(mezcla(CUERO, CUERO_OSC, 0.3f)), Offset(tapa.left, arriba - eh * 0.9f), Size(tapa.width, eh))
        drawOval(t.b(0.25f), Offset(tapa.left + tapa.width * 0.06f, arriba - eh * 0.85f), Size(tapa.width * 0.4f, eh * 0.5f))
        drawArc(t.c(BRONCE), 0f, 180f, false, Offset(tapa.left, arriba - eh * 0.4f - eh / 2 + eh * 0.65f), Size(tapa.width, eh), style = Stroke(2f * u))
        // El asa de bronce en la tapa.
        drawArc(t.c(BRONCE), 180f, 180f, false, Offset(tapa.left + tapa.width * 0.3f, arriba - eh * 0.9f), Size(tapa.width * 0.25f, eh * 0.7f), style = Stroke(2f * u))
        // La correa: pasa por encima de la tapa y baja por el frente, con su hebilla.
        val cx = x0 + (x1 - x0) * 0.28f
        drawRect(t.s(0.3f), Offset(cx + 1.5f * u, arriba - eh * 0.5f), Size(w * 0.022f, pisa - arriba))
        drawRect(t.c(CUERO_OSC), Offset(cx, arriba - eh * 0.55f), Size(w * 0.02f, pisa - arriba + eh * 0.4f))
        drawRect(t.b(0.3f), Offset(cx, arriba - eh * 0.55f), Size(1f * u, pisa - arriba))
        val hb2 = Rect(cx - w * 0.004f, arriba + (pisa - arriba) * 0.5f, cx + w * 0.024f, arriba + (pisa - arriba) * 0.5f + w * 0.022f)
        drawRect(t.c(BRONCE), hb2.topLeft, hb2.size, style = Stroke(1.6f * u))
        drawLine(t.c(BRONCE), Offset(hb2.left, hb2.center.y), Offset(hb2.right, hb2.center.y), 1.2f * u)
        destello(Offset(hb2.left + 1f * u, hb2.top + 1f * u), 1f * u, t)
        // Un rollo que asoma por debajo de la tapa mal puesta, con su sillybos.
        val ra = Offset(x0 + (x1 - x0) * 0.6f, arriba - eh * 0.7f)
        drawLine(t.c(PAPIRO_OSC), ra, ra + Offset(-w * 0.01f, -w * 0.035f), w * 0.016f, StrokeCap.Round)
        drawLine(t.c(PAPIRO), ra + Offset(-w * 0.003f, 0f), ra + Offset(-w * 0.013f, -w * 0.035f), w * 0.008f, StrokeCap.Round)
        drawCircle(t.c(HUESO), w * 0.005f, ra + Offset(-w * 0.011f, -w * 0.04f))
        drawRect(t.c(LAUREL), Offset(ra.x - w * 0.002f, ra.y - w * 0.026f), Size(w * 0.014f, w * 0.008f))
    }

    // ---------------------------------------------------------------- los compartimentos

    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la lucerna.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        fondoHueco(w, h, t)
        if (c.vacio) { vacio(w, h, c, t); return }
        val m = mezclar(c.semilla)
        translate(0f, -SALIDA * h * ab) {
            when {
                c.sinTema -> cesto(w, h, c, t, der)
                else -> when (Math.floorMod(m ushr 18, 3)) {
                    0 -> capsa(w, h, c, m, t, der)
                    1 -> haz(w, h, c, m, t, der)
                    else -> tablillas(w, h, c, m, t, der)
                }
            }
            tabula(Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom), t, w * 0.04f)
        }
    }

    /**
     * El fondo de un hueco: las tablas de nogal del fondo con sus juntas, la sombra de la tabla de arriba en escalones, el
     * costado derecho más hondo, la luz que entra por la izquierda y la tabla del piso con su canto.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono) {
        val u = density
        drawRect(t.c(mezcla(NOGAL_OSC, NOGAL, 0.45f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.1f), Offset.Zero, Size(w * 0.35f, h))
        listOf(0.3f, 0.62f, 0.86f).forEach { fx ->
            drawRect(t.s(0.3f), Offset(w * fx, 0f), Size(1f * u, h))
            drawRect(t.b(0.16f), Offset(w * fx + 1f * u, 0f), Size(0.7f * u, h))
        }
        listOf(0.36f to 0.1f, 0.22f to 0.12f, 0.09f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset(w * 0.92f, 0f), Size(w * 0.08f, h))
        drawRect(t.s(0.12f), Offset.Zero, Size(w * 0.03f, h))
        val piso = h * PISO
        drawRect(t.c(mezcla(NOGAL, NOGAL_LUZ, 0.35f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.45f), Offset(0f, piso), Size(w, 1f * u))
        drawRect(t.s(0.2f), Offset(w * 0.9f, piso), Size(w * 0.1f, h - piso))
    }

    /** Un rollo de pie (para la capsa y el cesto): el cuerpo de papiro, su cabo con la espiral y la perilla del umbilicus. */
    private fun DrawScope.rolloDePie(base: Offset, rr: Float, alto: Float, giro: Float, t: Tono, perilla: Color, sillybos: Color?) {
        val u = density
        rotate(giro, base) {
            val r = Rect(base.x - rr, base.y - alto, base.x + rr, base.y)
            drawRect(t.c(mezcla(PAPIRO, PAPIRO_OSC, 0.25f)), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { cilindro(r, t, 1.3f) }
            drawOval(t.c(PAPIRO), Offset(r.left, r.top - rr * 0.4f), Size(rr * 2, rr * 0.8f))
            drawOval(t.c(PAPIRO_OSC), Offset(r.left + rr * 0.3f, r.top - rr * 0.24f), Size(rr * 1.4f, rr * 0.48f), style = Stroke(0.6f * u))
            drawCircle(t.c(perilla), rr * 0.32f, Offset(base.x, r.top - rr * 0.05f))
            drawCircle(t.b(0.5f), rr * 0.12f, Offset(base.x - rr * 0.1f, r.top - rr * 0.12f))
            if (sillybos != null) {
                drawLine(t.c(PAPIRO_OSC), Offset(base.x + rr * 0.6f, r.top), Offset(base.x + rr * 1.3f, r.top + alto * 0.28f), 0.6f * u)
                val sb = Rect(base.x + rr * 0.9f, r.top + alto * 0.28f, base.x + rr * 2.3f, r.top + alto * 0.28f + rr * 0.8f)
                drawRect(t.s(0.25f), Offset(sb.left + 1f * u, sb.top + 1.2f * u), sb.size)
                drawRect(t.c(sillybos), sb.topLeft, sb.size)
                drawRect(t.b(0.3f), sb.topLeft, Size(sb.width, 0.8f * u))
            }
        }
    }

    /** El color del sillybos según la semilla: rojo, ocre, verde laurel o papiro. */
    private fun colorSillybos(s: Int, i: Int) = when (Math.floorMod(s + i * 7, 4)) { 0 -> ROJO; 1 -> OCRE; 2 -> mezcla(LAUREL, PAPIRO, 0.2f); else -> mezcla(PAPIRO, PAPIRO_OSC, 0.3f) }

    /**
     * La capsa: el cilindro de cuero (natural, teñido de rojo o de nogal, según la semilla) con sus aros de bronce, su correa y
     * su costura; los rollos de pie que asoman (más con más fichas) y, al lado, la tapa apoyada contra el fondo.
     */
    private fun DrawScope.capsa(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val cuero = when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(CUERO, OCRE, 0.45f); 1 -> mezcla(ROJO, CUERO_OSC, 0.3f); else -> mezcla(NOGAL, CUERO, 0.35f) }
        val x0 = w * 0.08f; val x1 = w * 0.54f
        val eh = (x1 - x0) * 0.26f
        val arriba = h * 0.27f
        // La tapa, apoyada contra el fondo a la derecha: un disco con su borde de bronce.
        val tc = Offset(w * 0.73f, h * 0.53f)
        val tr = w * 0.19f
        drawOval(t.s(0.3f), Offset(tc.x - tr * 0.7f + w * 0.03f, tc.y - tr + h * 0.03f), Size(tr * 1.4f, tr * 2f))
        drawOval(t.c(mezcla(cuero, CUERO_OSC, 0.25f)), Offset(tc.x - tr * 0.7f, tc.y - tr), Size(tr * 1.4f, tr * 2f))
        drawOval(t.b(0.2f), Offset(tc.x - tr * 0.6f, tc.y - tr * 0.9f), Size(tr * 0.7f, tr * 1.1f))
        drawOval(t.c(BRONCE), Offset(tc.x - tr * 0.7f, tc.y - tr), Size(tr * 1.4f, tr * 2f), style = Stroke(2.2f * u))
        drawOval(t.c(mezcla(cuero, TINTA, 0.25f)), Offset(tc.x - tr * 0.45f, tc.y - tr * 0.65f), Size(tr * 0.9f, tr * 1.3f), style = Stroke(1f * u))
        drawCircle(t.c(BRONCE), tr * 0.12f, tc)
        destello(tc - Offset(tr * 0.04f, tr * 0.05f), 1f * u, t)
        filo(Rect(tc.x - tr * 0.7f, tc.y - tr, tc.x + tr * 0.7f, tc.y + tr), t, der)
        // Con muchas fichas, un rollo de más apoyado en la tapa.
        val n = c.n.coerceIn(0, 12)
        if (n >= 6) rolloDePie(Offset(w * 0.62f, piso), w * 0.035f, h * 0.5f, 14f, t, HUESO, colorSillybos(m, 9))
        // La sombra de la capsa en el fondo y en la tabla.
        drawRoundRect(t.s(0.2f), Offset(x0 + w * 0.045f, arriba + h * 0.03f), Size(x1 - x0, piso - arriba - h * 0.03f), androidx.compose.ui.geometry.CornerRadius(w * 0.05f, eh * 0.5f))
        contactoT(Offset((x0 + x1) / 2 + w * 0.04f, piso), (x1 - x0) * 1.15f, eh * 0.7f, 0.4f, t)
        // La boca oscura y los rollos de pie, más y más altos con más fichas.
        drawOval(t.c(mezcla(CUERO_OSC, NOCHE, 0.5f)), Offset(x0, arriba - eh / 2), Size(x1 - x0, eh))
        val cuantos = (1 + n / 2).coerceIn(1, 6)
        for (i in 0 until cuantos) {
            val fila = i % 2
            val f = (i / 2 + 0.5f * fila + 0.3f) / 3.2f
            val cx = x0 + (x1 - x0) * (0.14f + 0.72f * f)
            val rr = (x1 - x0) * 0.085f
            val alto = h * (0.12f + 0.012f * n + 0.06f * azar(c.semilla, i))
            val y = arriba - eh * 0.15f + fila * eh * 0.3f
            val giro = (azar(c.semilla, i + 10) - 0.5f) * 14f
            val perilla = if (Math.floorMod(m ushr (6 + i), 3) == 0) TINTA else HUESO
            rolloDePie(Offset(cx, y + h * 0.04f), rr, alto + h * 0.04f, giro, t, perilla, if (i == cuantos - 1 && n > 1) colorSillybos(m, i) else null)
        }
        // El cilindro de cuero.
        val cuerpo = Rect(x0, arriba, x1, piso - eh * 0.25f)
        drawOval(t.c(mezcla(cuero, CUERO_OSC, 0.5f)), Offset(x0, cuerpo.bottom - eh / 2), Size(x1 - x0, eh))
        drawRect(t.c(cuero), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom + eh / 2) {
            cilindro(Rect(x0, arriba, x1, piso), t, 1.3f, der)
            pinceladas(cuerpo, if (t.o) NOCHE else mezcla(cuero, LUZ_MUEBLE, 0.3f), c.semilla, 6, u, 0.12f)
            var y = arriba + eh * 0.6f
            while (y < cuerpo.bottom) { drawLine(t.c(mezcla(cuero, PAPIRO, 0.4f)).copy(alpha = 0.55f), Offset(x0 + (x1 - x0) * 0.1f, y), Offset(x0 + (x1 - x0) * 0.1f, y + 2f * u), 0.8f * u); y += 4f * u }
        }
        drawArc(t.c(mezcla(cuero, PAPIRO, 0.35f)), 0f, 180f, false, Offset(x0, arriba - eh / 2), Size(x1 - x0, eh), style = Stroke(2f * u))
        listOf(arriba + (cuerpo.bottom - arriba) * 0.12f, cuerpo.bottom - (cuerpo.bottom - arriba) * 0.05f).forEach { y ->
            drawArc(t.c(mezcla(BRONCE, NOGAL_OSC, 0.3f)), 0f, 180f, false, Offset(x0, y - eh / 2), Size(x1 - x0, eh), style = Stroke(4f * u))
            drawArc(t.c(BRONCE_LUZ), if (der) 10f else 60f, 110f, false, Offset(x0, y - eh / 2 - 1f * u), Size(x1 - x0, eh), style = Stroke(1.4f * u))
        }
        // La correa, de un costado al otro, colgando floja por delante.
        val correa = Path().apply {
            moveTo(x0 + 1f * u, arriba + h * 0.06f)
            cubicTo(x0 + (x1 - x0) * 0.25f, arriba + h * 0.2f, x0 + (x1 - x0) * 0.75f, arriba + h * 0.2f, x1 - 1f * u, arriba + h * 0.06f)
        }
        translate(1.5f * u, 2f * u) { drawPath(correa, t.s(0.3f), style = Stroke(2.6f * u)) }
        drawPath(correa, t.c(CUERO_OSC), style = Stroke(2.6f * u))
        drawPath(correa, t.b(0.3f), style = Stroke(0.7f * u))
        listOf(x0, x1).forEach { x -> drawCircle(t.c(BRONCE), 1.6f * u, Offset(x + (if (x < w / 2) 1.5f * u else -1.5f * u), arriba + h * 0.06f)) }
        filo(cuerpo, t, der)
    }

    /**
     * El haz de rollos tumbados, vistos por sus cabos: la pirámide de discos de papiro con su espiral y su perilla (hueso o
     * negra), atada con un cordel; las etiquetas (sillyboi) de colores cuelgan de algunos. Más rollos con más fichas; con muchas,
     * uno de pie apoyado en el costado.
     */
    private fun DrawScope.haz(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(0, 12)
        val rr = w * 0.11f
        // Las filas, de abajo arriba. La de abajo queda casi toda tras la placa: para que el haz llene el hueco, desde 2
        // fichas ya es la pirámide entera (3, 2, 1); con una, 3 y 2.
        val filas = if (n >= 2) listOf(3, 2, 1) else listOf(3, 2)
        val cx = w * 0.38f
        // La funda del haz (la membrana teñida que envolvía los rollos buenos): púrpura, azafrán, roja o sin funda.
        val funda: Color? = when (Math.floorMod(m ushr 9, 4)) { 0 -> PURPURA; 1 -> AZAFRAN; 2 -> ROJO; else -> null }
        // El rollo de pie, apoyado en el costado derecho; con muchas fichas, otro más corto a su lado.
        if (n >= 6) rolloDePie(Offset(w * 0.75f, piso), w * 0.04f, h * 0.5f, 6f, t, TINTA, colorSillybos(m, 3))
        rolloDePie(Offset(w * 0.86f, piso), w * 0.045f, h * 0.66f, -9f, t, HUESO, colorSillybos(m, 5))
        // La sombra del haz en el fondo: la de cada cabo, corrida.
        val anchoHaz = rr * 2f * filas[0]
        filas.forEachIndexed { fi, cuantos ->
            val y = piso - rr - fi * rr * 1.73f
            for (j in 0 until cuantos) drawCircle(t.s(0.16f), rr, Offset(cx + (j - (cuantos - 1) / 2f) * rr * 2f + w * 0.045f, y + h * 0.03f))
        }
        contactoT(Offset(cx + w * 0.04f, piso), anchoHaz * 1.1f, h * 0.05f, 0.4f, t)
        val cabos = mutableListOf<Offset>()
        filas.forEachIndexed { fi, cuantos ->
            val y = piso - rr - fi * rr * 1.73f
            for (j in 0 until cuantos) {
                val x = cx + (j - (cuantos - 1) / 2f) * rr * 2f
                val i = cabos.size
                val p = Offset(x + rr * 0.06f * (azar(c.semilla, i + 40) - 0.5f), y)
                cabos.add(p)
                val rrr = rr * (0.92f + 0.08f * azar(c.semilla, i + 50))
                val papiro = mezcla(PAPIRO, PAPIRO_OSC, 0.45f * azar(c.semilla, i + 60))
                drawCircle(t.s(0.3f), rrr, p + Offset(rrr * 0.08f, rrr * 0.1f))
                drawCircle(t.c(papiro), rrr, p)
                // La funda teñida, que se ve como un aro alrededor del cabo (no todos los rollos del haz la llevan).
                if (funda != null && azar(c.semilla, i + 80) < 0.85f) drawCircle(t.c(funda), rrr * 0.89f, p, style = Stroke(rrr * 0.22f))
                // La espiral de las vueltas.
                listOf(0.82f, 0.64f, 0.46f).forEach { f -> drawCircle(t.c(PAPIRO_OSC).copy(alpha = 0.55f), rrr * f, p + Offset(rrr * (1f - f) * 0.15f, 0f), style = Stroke(0.6f * u)) }
                drawArc(t.b(0.45f), 190f, 80f, false, Offset(p.x - rrr * 0.88f, p.y - rrr * 0.88f), Size(rrr * 1.76f, rrr * 1.76f), style = Stroke(1.2f * u))
                drawArc(t.s(0.3f), 10f, 90f, false, Offset(p.x - rrr * 0.95f, p.y - rrr * 0.95f), Size(rrr * 1.9f, rrr * 1.9f), style = Stroke(1.5f * u))
                // La perilla del umbilicus.
                val per = if (Math.floorMod(m ushr (4 + i), 3) == 0) TINTA else HUESO
                drawCircle(t.s(0.35f), rrr * 0.26f, p + Offset(rrr * 0.06f, rrr * 0.07f))
                drawCircle(t.c(per), rrr * 0.24f, p)
                drawCircle(t.b(0.55f), rrr * 0.09f, p - Offset(rrr * 0.08f, rrr * 0.08f))
            }
        }
        // El cordel que ata el haz: una vuelta horizontal por la fila de en medio y el nudo.
        val yc = piso - rr - rr * 1.73f * 0.5f
        val cordel = t.c(mezcla(PAPIRO_OSC, CUERO, 0.4f))
        drawLine(t.s(0.3f), Offset(cx - anchoHaz / 2, yc + 1.5f * u), Offset(cx + anchoHaz / 2, yc + 1.5f * u), 1.6f * u)
        drawLine(cordel, Offset(cx - anchoHaz / 2 + rr * 0.1f, yc), Offset(cx + anchoHaz / 2 - rr * 0.1f, yc), 1.5f * u)
        val nudo = Offset(cx + rr * 0.9f, yc)
        drawCircle(cordel, 2.4f * u, nudo)
        drawLine(cordel, nudo, nudo + Offset(rr * 0.25f, rr * 0.7f), 1.2f * u, StrokeCap.Round)
        drawLine(cordel, nudo, nudo + Offset(-rr * 0.1f, rr * 0.8f), 1.2f * u, StrokeCap.Round)
        // Los sillyboi que cuelgan de los cabos de arriba (los de abajo quedan tras la placa).
        val desde = filas[0]
        for (i in desde until cabos.size) {
            if (azar(c.semilla, i + 70) < 0.25f) continue
            val p = cabos[i]
            val col = colorSillybos(m, i)
            val q = p + Offset(rr * 0.5f, rr * 0.95f)
            drawLine(t.c(PAPIRO_OSC), p + Offset(rr * 0.2f, rr * 0.1f), q, 0.6f * u)
            val sb = Rect(q.x - rr * 0.2f, q.y, q.x + rr * 0.55f, q.y + rr * 0.42f)
            rotate((azar(c.semilla, i + 80) - 0.5f) * 20f, sb.topLeft) {
                drawRect(t.s(0.3f), Offset(sb.left + 1f * u, sb.top + 1.2f * u), sb.size)
                drawRect(t.c(col), sb.topLeft, sb.size)
                drawRect(t.b(0.3f), sb.topLeft, Size(sb.width, 0.8f * u))
                drawLine(t.c(TINTA).copy(alpha = 0.5f), Offset(sb.left + sb.width * 0.2f, sb.center.y), Offset(sb.right - sb.width * 0.2f, sb.center.y), 0.6f * u)
            }
        }
        if (t.o && t.k > 0.06f) cabos.forEach { p -> drawArc(LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), if (der) -60f else 200f, 70f, false, Offset(p.x - rr * 0.95f, p.y - rr * 0.95f), Size(rr * 1.9f, rr * 1.9f), style = Stroke(1.2f * u)) }
    }

    /**
     * El fajo de tablillas de cera: las tablas de boj o de nogal, una sobre otra (más con más fichas), atadas con un cordel en
     * cruz y selladas con lacre; detrás, de pie contra el fondo, un díptico abierto con la cera escrita y el estilo apoyado.
     */
    private fun DrawScope.tablillas(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val n = c.n.coerceIn(0, 12)
        val madera = if (Math.floorMod(m ushr 3, 2) == 0) BOJ else mezcla(NOGAL_LUZ, BOJ, 0.35f)
        val cera = if (Math.floorMod(m ushr 5, 3) == 0) mezcla(OCRE, CUERO_OSC, 0.35f) else CERA
        // El díptico abierto de pie contra el fondo, a la derecha.
        val dx0 = w * 0.5f; val dx1 = w * 0.94f
        val dTop = h * 0.14f
        val mitad = (dx0 + dx1) / 2
        drawRect(t.s(0.18f), Offset(dx0 + w * 0.035f, dTop + h * 0.03f), Size(dx1 - dx0, piso - dTop - h * 0.03f))
        listOf(0, 1).forEach { k ->
            val l = if (k == 0) dx0 else mitad
            val r = if (k == 0) mitad else dx1
            val sesgo = if (k == 0) h * 0.03f else 0f
            val tabla = poligono(l, dTop + sesgo, r, dTop, r, piso, l, piso)
            drawPath(tabla, t.c(if (k == 0) mezcla(madera, NOGAL, 0.2f) else madera))
            val mm = w * 0.022f
            val campo = poligono(l + mm, dTop + sesgo + mm, r - mm, dTop + mm, r - mm, piso - mm, l + mm, piso - mm)
            drawPath(campo, t.c(cera))
            clipPath(campo) {
                drawRect(t.s(0.3f), Offset(l, dTop), Size(r - l, h * 0.04f))
                for (j in 0 until 9) {
                    val yy = dTop + mm + h * (0.07f + 0.075f * j)
                    val largo = (r - l - mm * 3) * (0.5f + 0.45f * azar(c.semilla, j + 10 * k))
                    drawLine(t.c(mezcla(madera, PAPIRO, 0.5f)).copy(alpha = 0.55f), Offset(l + mm * 1.5f, yy), Offset(l + mm * 1.5f + largo, yy), 0.7f * u)
                }
            }
            drawPath(tabla, t.b(0.25f), style = Stroke(0.8f * u))
            if (k == 0) drawRect(t.s(0.2f), Offset(l, dTop), Size((r - l) * 0.25f, piso - dTop))
        }
        drawLine(t.s(0.4f), Offset(mitad, dTop), Offset(mitad, piso), 1.4f * u)
        listOf(0.3f, 0.7f).forEach { f -> drawLine(t.c(ROJO), Offset(mitad - w * 0.012f, dTop + (piso - dTop) * f), Offset(mitad + w * 0.012f, dTop + (piso - dTop) * f), 1.3f * u) }
        // El estilo, apoyado en el díptico.
        val ea = Offset(dx1 - w * 0.06f, piso); val eb = Offset(dx1 + w * 0.01f, dTop + h * 0.08f)
        drawLine(t.s(0.35f), ea + Offset(1.5f * u, 0f), eb + Offset(1.5f * u, 1.5f * u), 1.6f * u)
        drawLine(t.c(HIERRO), ea, eb, 1.5f * u, StrokeCap.Round)
        drawLine(t.c(HIERRO), eb, eb + (eb - ea) * 0.07f, 3.2f * u)
        drawLine(t.b(0.5f), ea + Offset(-0.6f * u, 0f), eb + Offset(-0.6f * u, 0f), 0.5f * u)
        filo(Rect(dx0, dTop, dx1, piso), t, der)
        // El fajo de tablas, de abajo arriba.
        val cuantas = (6 + n / 2).coerceIn(6, 10)
        val x0 = w * 0.06f; val x1 = w * 0.54f
        val grueso = h * 0.072f
        val tapa = grueso * 0.45f
        contactoT(Offset((x0 + x1) / 2 + w * 0.04f, piso), (x1 - x0) * 1.12f, h * 0.05f, 0.42f, t)
        drawRect(t.s(0.18f), Offset(x0 + w * 0.04f, piso - cuantas * grueso + h * 0.02f), Size(x1 - x0, cuantas * grueso - h * 0.02f))
        var arriba = piso
        for (i in 0 until cuantas) {
            val desliz = w * 0.025f * (azar(c.semilla, i + 20) - 0.5f)
            val y1 = piso - i * grueso
            val y0 = y1 - grueso
            val tono = mezcla(madera, if (i % 3 == 1) NOGAL else PAPIRO, 0.12f + 0.12f * azar(c.semilla, i + 30))
            val r = Rect(x0 + desliz, y0, x1 + desliz, y1)
            drawRect(t.c(tono), r.topLeft, r.size)
            // El canto: el borde rebajado de la tablilla (la cera queda dentro) y su luz.
            drawRect(t.c(mezcla(tono, CERA, 0.55f)), Offset(r.left + w * 0.02f, r.top + grueso * 0.38f), Size(r.width - w * 0.04f, grueso * 0.2f))
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(t.s(0.2f), Offset(r.right - r.width * 0.08f, r.top), Size(r.width * 0.08f, r.height))
            if (!der) drawRect(t.b(0.12f), r.topLeft, Size(r.width * 0.12f, r.height))
            // Los agujeros del cordel en el canto izquierdo.
            drawCircle(t.c(TINTA).copy(alpha = 0.5f), 1.2f * u, Offset(r.left + w * 0.012f, r.center.y))
            arriba = y0
        }
        // La tabla de encima, vista desde arriba: su cara con la veta.
        val cara = poligono(x0 + w * 0.012f, arriba - tapa, x1 - w * 0.008f, arriba - tapa, x1, arriba, x0, arriba)
        drawPath(cara, t.c(mezcla(madera, LUZ_MUEBLE, if (t.o) 0f else 0.15f)))
        drawPath(cara, t.b(0.2f))
        // El cordel en cruz y el sello de lacre encima.
        val cx = (x0 + x1) / 2
        val cordel = t.c(mezcla(PAPIRO, PAPIRO_OSC, 0.5f))
        drawLine(t.s(0.3f), Offset(cx + 1.5f * u, arriba - tapa * 0.5f), Offset(cx + 1.5f * u, piso), 1.6f * u)
        drawLine(cordel, Offset(cx, arriba - tapa * 0.5f), Offset(cx, piso - 1f * u), 1.5f * u)
        drawLine(cordel, Offset(x0 + w * 0.01f, arriba - tapa * 0.5f), Offset(x1 - w * 0.01f, arriba - tapa * 0.5f), 1.5f * u)
        val sello = Offset(cx, arriba - tapa * 0.5f)
        val lacre = if (Math.floorMod(m ushr 7, 2) == 0) ROJO else mezcla(ROJO, CUERO_OSC, 0.45f)
        for (k in 0 until 7) drawCircle(t.c(lacre), w * 0.03f, sello + Offset(w * 0.005f * cos(k * 0.9f), w * 0.004f * sin(k * 0.9f)))
        drawCircle(t.c(mezcla(lacre, TINTA, 0.3f)), w * 0.019f, sello, style = Stroke(1f * u))
        drawCircle(t.b(0.45f), w * 0.008f, sello - Offset(w * 0.012f, w * 0.01f))
        filo(Rect(x0, arriba - tapa, x1, piso), t, der)
    }

    /** Lo que no tiene tema: un cesto de mimbre con rollos sueltos, ladeados, que asoman (más con más fichas). */
    private fun DrawScope.cesto(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val x0 = w * 0.12f; val x1 = w * 0.82f
        val arriba = h * 0.36f
        val eh = (x1 - x0) * 0.18f
        val paja = mezcla(OCRE, PAPIRO, 0.35f)
        drawPath(poligono(x0 + w * 0.045f, arriba + h * 0.04f, x1 + w * 0.045f, arriba + h * 0.04f, x1, piso, x0 + w * 0.085f, piso), t.s(0.18f))
        contactoT(Offset((x0 + x1) / 2 + w * 0.04f, piso), (x1 - x0) * 1.1f, h * 0.05f, 0.4f, t)
        drawOval(t.c(mezcla(paja, CUERO_OSC, 0.6f)), Offset(x0, arriba - eh / 2), Size(x1 - x0, eh))
        val n = c.n.coerceIn(0, 12)
        val cuantos = (2 + n / 2).coerceIn(2, 7)
        for (i in 0 until cuantos) {
            val f = (i + 0.5f) / cuantos
            val giro = -32f + 64f * f + (azar(c.semilla, i) - 0.5f) * 14f
            val alto = h * (0.3f + 0.12f * azar(c.semilla, i + 5) + 0.01f * n)
            rolloDePie(Offset(x0 + (x1 - x0) * (0.18f + 0.64f * f), arriba + h * 0.18f), w * 0.04f, alto, giro, t,
                if (i % 3 == 1) TINTA else HUESO, if (i % 2 == 0) colorSillybos(c.semilla, i) else null)
        }
        // El cesto: el cuerpo que se abre un poco hacia arriba, el trenzado y el borde.
        val cuerpo = poligono(x0, arriba, x1, arriba, x1 - w * 0.04f, piso - eh * 0.2f, x0 + w * 0.04f, piso - eh * 0.2f)
        drawPath(cuerpo, t.c(paja))
        clipPath(cuerpo) {
            cilindro(Rect(x0, arriba, x1, piso), t, 1.2f, der)
            var y = arriba + h * 0.05f
            var fila = 0
            while (y < piso) {
                var x = x0 + (if (fila % 2 == 0) 0f else w * 0.03f)
                while (x < x1) {
                    drawOval(t.c(mezcla(paja, CUERO_OSC, 0.25f)), Offset(x, y - h * 0.018f), Size(w * 0.05f, h * 0.036f), style = Stroke(0.8f * u))
                    x += w * 0.06f
                }
                drawLine(t.s(0.25f), Offset(x0, y + h * 0.02f), Offset(x1, y + h * 0.02f), 0.7f * u)
                y += h * 0.06f; fila++
            }
            listOf(0.25f, 0.5f, 0.75f).forEach { fx -> drawLine(t.c(mezcla(paja, CUERO_OSC, 0.45f)), Offset(x0 + (x1 - x0) * fx, arriba), Offset(x0 + (x1 - x0) * fx, piso), 1.2f * u) }
        }
        // El borde trenzado.
        val br = Rect(x0 - w * 0.01f, arriba - h * 0.03f, x1 + w * 0.01f, arriba + h * 0.04f)
        drawRoundRect(t.c(mezcla(paja, CUERO, 0.2f)), br.topLeft, br.size, androidx.compose.ui.geometry.CornerRadius(h * 0.03f))
        var x = br.left + w * 0.01f
        while (x < br.right - w * 0.01f) { drawLine(t.s(0.3f), Offset(x, br.top + 1f * u), Offset(x + w * 0.02f, br.bottom - 1f * u), 0.8f * u); x += w * 0.025f }
        drawRect(t.b(0.35f), br.topLeft, Size(br.width, 1f * u))
        filo(br, t, der)
    }

    /** Un hueco libre: un tintero volcado con su mancha de tinta y un cálamo, o una lucerna apagada con una moneda, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono) {
        val u = density
        val piso = h * PISO
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            // La mancha de tinta en la tabla.
            drawOval(t.c(TINTA).copy(alpha = 0.85f), Offset(w * 0.4f, piso - h * 0.02f), Size(w * 0.32f, h * 0.05f))
            drawOval(t.c(TINTA).copy(alpha = 0.85f), Offset(w * 0.66f, piso - h * 0.005f), Size(w * 0.09f, h * 0.025f))
            drawOval(t.b(0.25f), Offset(w * 0.46f, piso - h * 0.012f), Size(w * 0.1f, h * 0.012f))
            // El tintero tumbado: el cilindro de bronce de lado, la boca hacia la mancha.
            val r = Rect(w * 0.2f, piso - h * 0.16f, w * 0.44f, piso)
            contactoT(Offset(r.center.x + w * 0.03f, piso), r.width * 1.1f, h * 0.04f, 0.4f, t)
            drawRect(t.c(BRONCE), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) { tumbado(r, t, 1.3f) }
            drawOval(t.c(mezcla(BRONCE, BRONCE_LUZ, 0.3f)), Offset(r.right - w * 0.03f, r.top), Size(w * 0.06f, r.height))
            drawOval(t.c(TINTA), Offset(r.right - w * 0.015f, r.top + r.height * 0.2f), Size(w * 0.035f, r.height * 0.6f))
            drawOval(t.c(mezcla(BRONCE, NOGAL_OSC, 0.3f)), Offset(r.left - w * 0.03f, r.top), Size(w * 0.06f, r.height))
            drawLine(t.c(BRONCE_LUZ), Offset(r.left, r.top + r.height * 0.3f), Offset(r.right, r.top + r.height * 0.3f), 1f * u)
            destello(Offset(r.left + r.width * 0.3f, r.top + r.height * 0.25f), 1.2f * u, t)
            // El cálamo rodado.
            drawLine(t.s(0.3f), Offset(w * 0.12f, piso - h * 0.01f), Offset(w * 0.5f, piso - h * 0.06f), 2.2f * u)
            drawLine(t.c(mezcla(PAPIRO_OSC, OCRE, 0.4f)), Offset(w * 0.1f, piso - h * 0.02f), Offset(w * 0.48f, piso - h * 0.07f), 2f * u, StrokeCap.Round)
        } else {
            // La lucerna apagada: el cuerpo, el disco, el asa, el pico tiznado y la mecha.
            val c0 = Offset(w * 0.45f, piso - h * 0.07f)
            val lw = w * 0.38f; val lh = h * 0.17f
            contactoT(Offset(c0.x + w * 0.03f, piso), lw * 1.4f, h * 0.04f, 0.42f, t)
            val pico = poligono(c0.x - lw * 0.3f, c0.y - lh * 0.3f, c0.x - lw * 0.85f, c0.y - lh * 0.22f, c0.x - lw * 0.88f, c0.y + lh * 0.12f, c0.x - lw * 0.3f, c0.y + lh * 0.3f)
            drawPath(pico, t.c(mezcla(BARRO, BRONCE, 0.4f)))
            drawPath(pico, t.b(0.2f))
            val cuerpo = Rect(c0.x - lw / 2, c0.y - lh / 2, c0.x + lw / 2, c0.y + lh / 2)
            drawOval(t.c(mezcla(BARRO, BRONCE, 0.4f)), cuerpo.topLeft, cuerpo.size)
            clipPath(Path().apply { addOval(cuerpo) }) { tumbado(cuerpo, t, 1.4f) }
            drawOval(t.c(mezcla(BARRO, NOGAL_OSC, 0.35f)), Offset(c0.x - lw * 0.32f, c0.y - lh * 0.48f), Size(lw * 0.64f, lh * 0.32f))
            drawCircle(t.c(TINTA), lh * 0.06f, Offset(c0.x, c0.y - lh * 0.33f))
            drawArc(t.c(mezcla(BARRO, BRONCE, 0.4f)), -80f, 220f, false, Offset(c0.x + lw * 0.42f, c0.y - lh * 0.5f), Size(lw * 0.22f, lh * 0.7f), style = Stroke(2.4f * u))
            drawOval(t.c(TINTA).copy(alpha = 0.6f), Offset(c0.x - lw * 0.92f, c0.y - lh * 0.35f), Size(lw * 0.22f, lh * 0.5f))
            drawLine(t.c(TINTA), Offset(c0.x - lw * 0.82f, c0.y - lh * 0.15f), Offset(c0.x - lw * 0.86f, c0.y - lh * 0.45f), 1.4f * u, StrokeCap.Round)
            // Una moneda olvidada.
            drawOval(t.s(0.35f), Offset(w * 0.73f + 1f * u, piso - h * 0.025f + 1f * u), Size(w * 0.07f, h * 0.03f))
            drawOval(t.c(BRONCE), Offset(w * 0.73f, piso - h * 0.025f), Size(w * 0.07f, h * 0.03f))
            drawOval(t.b(0.4f), Offset(w * 0.745f, piso - h * 0.02f), Size(w * 0.025f, h * 0.008f))
        }
    }

    override val rotulo = Rect(0.055f, 0.645f, 0.945f, 0.885f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = Letra.inscripcion
    override val salida = -SALIDA
    override val placa = Rect(0.36f, 0.08f, 0.64f, 0.27f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    /** Una corona de laurel: dos ramas de hojas en círculo, abiertas arriba, y la cinta abajo. */
    private fun DrawScope.laurel(c: Offset, r: Float, color: Color) {
        for (lado in listOf(-1f, 1f)) for (k in 0 until 7) {
            val ang = (if (lado > 0) 100f + 22f * k else 80f - 22f * k)
            val rad = Math.toRadians(ang.toDouble())
            val p = Offset(c.x + r * kotlin.math.cos(rad).toFloat(), c.y + r * kotlin.math.sin(rad).toFloat())
            rotate(ang + 90f, p) { drawOval(color, Offset(p.x - r * 0.28f, p.y - r * 0.11f), Size(r * 0.56f, r * 0.22f)) }
        }
        drawLine(LACRE, Offset(c.x - r * 0.2f, c.y + r * 1.05f), Offset(c.x - r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
        drawLine(LACRE, Offset(c.x + r * 0.2f, c.y + r * 1.05f), Offset(c.x + r * 0.45f, c.y + r * 1.5f), r * 0.12f, StrokeCap.Round)
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Una pared al fresco: el campo rojo, el friso negro arriba, el zócalo negro abajo, filetes amarillos y candelabros finos.
        drawRect(Brush.verticalGradient(listOf(t(POMPEYANO, oscuro), t(mezcla(POMPEYANO, Color.Black, 0.18f), oscuro))))
        for (k in 0 until 90) mancha(Offset(w * azar(31, k), h * azar(32, k)), (10f + 30f * azar(33, k)) * u, (if (k % 3 == 0) Color.White else Color.Black).copy(alpha = 0.05f))
        val friso = h * 0.07f; val zocalo = h * 0.09f
        drawRect(t(NEGRO, oscuro), Offset.Zero, Size(w, friso))
        drawRect(t(NEGRO, oscuro), Offset(0f, h - zocalo), Size(w, zocalo))
        // Una greca de ovas en el friso.
        var x = 6 * u
        while (x < w) { drawOval(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(x, friso * 0.35f), Size(5 * u, friso * 0.4f), style = Stroke(0.8f * u)); x += 9 * u }
        drawLine(t(FILETE, oscuro), Offset(0f, friso), Offset(w, friso), 1.2f * u)
        drawLine(t(FILETE, oscuro), Offset(0f, h - zocalo), Offset(w, h - zocalo), 1.2f * u)
        val m = 8 * u
        drawRect(t(FILETE, oscuro).copy(alpha = 0.8f), Offset(m, friso + m), Size(w - 2 * m, h - friso - zocalo - 2 * m), style = Stroke(1f * u))
        listOf(m + 3 * u, w - m - 3 * u).forEach { cx ->
            drawLine(t(FILETE, oscuro).copy(alpha = 0.6f), Offset(cx, friso + m), Offset(cx, h - zocalo - m), 1f * u)
            var y = friso + m + 30 * u
            while (y < h - zocalo - m - 10 * u) { drawOval(t(FILETE, oscuro).copy(alpha = 0.7f), Offset(cx - 3 * u, y), Size(6 * u, 3 * u)); y += 70 * u }
        }
        // En el zócalo, pinceladas de mármol fingido.
        for (k in 0 until 12) {
            val y = h - zocalo + zocalo * azar(41, k)
            drawLine(Color.White.copy(alpha = 0.08f), Offset(w * azar(42, k), y), Offset(w * azar(42, k) + 30 * u, y + 6 * u), 1f * u)
        }
    }

    override fun tintaInterior(oscuro: Boolean) = Color(0xFFF3E6C8)
    override val pared: Dp = 14.dp
    override val columnas = 2
    override val lineas = 5

    /** El grueso de las varillas del rollo, en px, según el tamaño de la ficha. */
    private fun varilla(s: Size) = (minOf(s.width, s.height) * 0.065f).coerceIn(22f, 56f)

    override fun forma(semilla: Int): Shape = GenericShape { s, _ ->
        val v = varilla(s)
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, 0f, s.width, v), androidx.compose.ui.geometry.CornerRadius(v / 2)))
        addRect(Rect(v * 0.55f, v * 0.5f, s.width - v * 0.55f, s.height - v * 0.5f))
        addRoundRect(androidx.compose.ui.geometry.RoundRect(Rect(0f, s.height - v, s.width, s.height), androidx.compose.ui.geometry.CornerRadius(v / 2)))
    }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val v = varilla(size)
        // Las fibras del papiro: tiras horizontales y, debajo, las verticales; y una junta entre hojas.
        var y = v; while (y < h - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.12f + 0.08f * azar(semilla, y.toInt())), Offset(v * 0.55f, y), Offset(w - v * 0.55f, y), 0.6f * u); y += 2.6f * u }
        var x = v; while (x < w - v) { drawLine(PAPIRO_OSC.copy(alpha = 0.06f), Offset(x, v), Offset(x, h - v), 0.5f * u); x += 4.5f * u }
        val junta = w * (0.3f + 0.4f * azar(semilla, 7))
        drawLine(PAPIRO_OSC.copy(alpha = 0.3f), Offset(junta, v), Offset(junta, h - v), 1.2f * u)
        // La sombra donde la hoja se enrolla en cada varilla.
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.22f), Color.Transparent), v * 0.5f, v * 0.5f + 10 * u), Offset(0f, v * 0.5f), Size(w, 10 * u))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.22f)), h - v * 0.5f - 10 * u, h - v * 0.5f), Offset(0f, h - v * 0.5f - 10 * u), Size(w, 10 * u))
        // Las varillas (umbilici) de madera, con sus perillas de marfil.
        listOf(0f, h - v).forEach { y0 ->
            drawRoundRect(Brush.verticalGradient(listOf(NOGAL_LUZ, NOGAL, NOGAL_OSC), y0, y0 + v), Offset(v * 0.35f, y0 + v * 0.12f), Size(w - v * 0.7f, v * 0.76f), androidx.compose.ui.geometry.CornerRadius(v * 0.38f))
            listOf(v * 0.5f, w - v * 0.5f).forEach { cx ->
                drawCircle(Color(0xFFEDE3CC), v * 0.5f, Offset(cx, y0 + v / 2))
                drawCircle(Color(0xFFB8A784), v * 0.5f, Offset(cx, y0 + v / 2), style = Stroke(1f * u))
                drawCircle(Color.White.copy(alpha = 0.6f), v * 0.14f, Offset(cx - v * 0.15f, y0 + v * 0.35f))
            }
        }
    }

    override val margenes = MargenesFicha(18.dp, 16.dp, 18.dp, 32.dp, 32.dp, 36.dp)
    override val proporcionFicha = 0.95f
    override val anchoGrande = 0.94f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El lacre de una tablilla sellada.
            "hecho" -> selloHecho(LACRE, p.color)
            // Una corona de laurel chica.
            "convence" -> laurel(Offset(c.x, c.y - s * 0.05f), s * 0.36f, LAUREL)
            // Tres interpuntos triangulares, como en las inscripciones.
            "piensa" -> listOf(-0.32f, 0f, 0.32f).forEach { dx ->
                val o = Offset(c.x + dx * s, c.y)
                drawPath(Path().apply { moveTo(o.x - s * 0.1f, o.y - s * 0.09f); lineTo(o.x + s * 0.1f, o.y - s * 0.09f); lineTo(o.x, o.y + s * 0.12f); close() }, TINTA)
            }
            "duda" -> drawArc(p.tenue, 30f, 300f, false, Offset(c.x - s * 0.34f, c.y - s * 0.34f), Size(s * 0.68f, s * 0.68f), style = Stroke(s * 0.12f))
            // La theta nigrum: la marca de los caídos en las listas.
            "desacuerdo" -> { drawCircle(LACRE, s * 0.36f, c, style = Stroke(s * 0.12f)); drawLine(LACRE, Offset(c.x - s * 0.36f, c.y), Offset(c.x + s * 0.36f, c.y), s * 0.12f) }
            else -> {}
        }
    }

    /** Un sello de lacre rojo, con su borde corrido y una L. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        for (k in 0 until 8) drawCircle(LACRE, s * 0.36f, Offset(c.x + s * 0.06f * kotlin.math.cos(k * 0.785f), c.y + s * 0.06f * kotlin.math.sin(k * 0.785f)))
        drawCircle(mezcla(LACRE, Color.Black, 0.25f), s * 0.24f, c, style = Stroke(s * 0.06f))
    }

    override val desorden = 0f
}
