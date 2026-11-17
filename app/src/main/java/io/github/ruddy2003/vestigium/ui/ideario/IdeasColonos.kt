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
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// =====================================================================
//  Colonos
// =====================================================================

/**
 * Colonos: la trastienda del almacén general de un pueblo que se reconstruye (2288). Es un interior sin ventana: una pared
 * de tablas de pino encaladas, remendada a la derecha con una chapa ondulada rescatada, con un listón de madera vieja del que
 * cuelgan las cosas y, debajo de la cal, lo que queda de un rótulo de antes; la luz de día entra de arriba a la izquierda,
 * fuera del cuadro. El mueble es la cajonera del almacén, hecha de tablas rescatadas de distintos tonos (el larguero de la
 * derecha, ladrillo; los tramos del medio, azul marino, pino, ladrillo y verde; un travesaño verde), con la pintura gastada,
 * sus clavos, sus parches de lata, la cuenta de lo fiado a lápiz en el friso y el sobre de trabajo de tablones, del que cuelga
 * una gaceta clavada con dos tachuelas; abajo, el faldón azul con la tarjeta de «y N más» y los tacos en que descansa.
 * Encima, el punto focal: la balanza de platillos de latón sobre su caja de madera, con el fiel ladeado, maíz en un platillo
 * y las pesas (tuercas y piezas de máquina) en el otro; a su lado las demás pesas sobre la gaceta, la campanilla de
 * mostrador, el quinqué de hojalata esmaltada con su tubo de vidrio, dos frascos de conserva con tapa de tela, los sacos de
 * semillas con su pala (uno cortado por el borde), el libro de cuentas apoyado en la pared con su lápiz atado y unas pastillas
 * de jabón apiladas. En la pared, el sombrero de paja de ala ancha colgado de un clavo, la pizarra de precios (dibujos y
 * números a tiza) y una ristra de mazorcas. Al pie, sobre el piso de tablones, el saco de harina abierto (cortado por el borde
 * izquierdo), la caja de fruta y el barril con su tapa (cortado por el derecho). Cada tema es, según la semilla, un cajetín
 * de tabla rescatada (pino, azul, ladrillo o verde) con su tirador de cordel, de cuero o de cuchara doblada y los recortes
 * asomando; un frasco de conserva grande con los recortes enrollados dentro y su tapa de tela atada; o una lata de galletas
 * con la marca borrada y la tapa levantada. En los tres, la etiqueta de papel de gaceta con el tema. Lo que no tiene tema es
 * una cesta de mimbre con recortes sueltos y una tarjeta atada; un hueco libre guarda un ovillo de cordel o unas manzanas. Al
 * tocar uno, sale hacia quien mira. De noche todo se hunde en azul marino: alumbra solo el quinqué (el halo cálido en las
 * tablas encaladas, el filo en el latón de la balanza y en el vidrio de los frascos), la primera fila encendida, la segunda a
 * medias y las de abajo casi perdidas. Por dentro, el cajón forrado de gaceta, con tachuelas y un cerco de café. Las fichas
 * son recortes cortados a tijera con su cinta.
 */
object IdeasColonos : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta del almacén
    /** La cal de la pared y su sombra. */
    private val CAL = Color(0xFFE6DDC6)
    private val CAL_OSC = Color(0xFFBDB096)
    /** El pino de las tablas rescatadas, su luz y su sombra; la madera vieja del listón, de la caja de la balanza y del barril. */
    private val PINO = Color(0xFFB08A5A)
    private val PINO_LUZ = Color(0xFFCBA676)
    private val PINO_OSC = Color(0xFF7A5A36)
    private val TABLON = Color(0xFF574230)
    /** Las pinturas que quedan en las tablas, las telas y las latas: ladrillo, azul marino, verde salvia y amarillo maíz. */
    private val LADRILLO = Color(0xFF9E2B25)
    private val MARINO = Color(0xFF3E4B60)
    private val SALVIA = Color(0xFF7F9068)
    private val MAIZ = Color(0xFFD9A93E)
    /** El papel de gaceta y su tinta; la cinta amarillenta. */
    private val GACETA = Color(0xFFEDE4CC)
    private val TINTA = Color(0xFF1E1A16)
    private val CINTA = Color(0xFFE8D48A)
    /** La lata (la chapa, los parches, la pala) y su óxido; el latón de la balanza; el cordel y la arpillera; el vidrio; la pizarra. */
    private val LATA = Color(0xFFA0A4A0)
    private val OXIDO = Color(0xFF99562F)
    private val LATON = Color(0xFFB89A50)
    private val CUERDA = Color(0xFFC9A86A)
    private val VIDRIO = Color(0xFFD8E6DF)
    private val PIZARRA = Color(0xFF39413F)
    /** La llama del quinqué. */
    private val LUZ = Color(0xFFF2C46B)
    /** La noche del almacén: azul marino hondo. */
    private val NOCHE = Color(0xFF0C121E)
    /**
     * La etiqueta de noche lejos del quinqué: con [TINTA] da unos 6:1 en el título y 4.6:1 en la cuenta (al 78 %); cerca del
     * quinqué sube hacia [GACETA] con un poco de llama.
     */
    private val GACETA_LEJOS = Color(0xFF9F9884)

    /** La arpillera de los sacos, el hierro de las pesas y los clavos, y la paja del sombrero y la cesta: salen de la paleta. */
    private val ARPILLERA = mezcla(CUERDA, PINO_OSC, 0.3f)
    private val HIERRO = mezcla(LATA, TINTA, 0.55f)
    private val PAJA = mezcla(MAIZ, GACETA, 0.5f)

    /** El oscurecido de siempre: solo el interior lo usa. */
    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.3f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.3f
    override val corona = 0.64f
    override val base = 0.3f
    override val lado = 0.06f
    override val hueco = 0.045f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** En la corona: el fondo del sobre de trabajo, su canto de frente y lo bajo del canto, en fracción de su alto. */
    private const val SUP = 0.8f
    private const val FRENTE = 0.87f
    private const val CANTO = 0.935f
    /** En la base: lo bajo del faldón, el rincón de la pared con el piso, donde pisan los tacos y donde pisan las cosas del pie. */
    private const val FALDON = 0.3f
    private const val SUELO = 0.4f
    private const val PISAN = 0.47f
    private const val PIE = 0.96f
    /** Los costados de la cajonera, en fracción del ancho. */
    private const val IZQ = 0.02f
    private const val DER = 0.98f
    /** El piso de cada hueco (la tabla) y cuánto crece su contenido al tocarlo. */
    private const val PISO = 0.9f
    private const val CRECE = 0.06f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama del quinqué, en fracciones del ancho. */
    private val FOCO = Offset(0.6f, 0.42f)

    /** Cuánto le llega del quinqué a un punto [p] (en fracciones del ancho): cae con la distancia; abajo, casi nada. */
    private fun luz(p: Offset): Float {
        val dx = p.x - FOCO.x
        val dy = (p.y - FOCO.y) * (if (p.y > FOCO.y) 0.8f else 1f)
        val radial = 1.45f * exp(-3.9f * sqrt(dx * dx + dy * dy))
        return maxOf(radial, luzCuerpo(p)).coerceIn(0.03f, 1f)
    }

    /**
     * La luz que el quinqué echa hacia abajo sobre el frente de la cajonera: cae despacio de fila en fila (la primera, encendida;
     * la segunda, a medias; la tercera, tenue; la cuarta y la quinta, casi perdidas), un poco menos en la columna de la
     * izquierda, más lejos de la llama. Los puntos son los centros de las filas con 9 temas, en fracciones del ancho.
     */
    private val CAIDA = listOf(0.6f to 0.74f, 0.85f to 0.7f, 1.21f to 0.47f, 1.58f to 0.26f, 1.94f to 0.11f, 2.31f to 0.05f, 2.8f to 0.03f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.5f * (abs(p.x - FOCO.x) - 0.05f).coerceAtLeast(0f)).coerceIn(0.7f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hondo(c: Color) = Color(c.red * 0.13f + NOCHE.red * 0.8f, c.green * 0.13f + NOCHE.green * 0.8f, c.blue * 0.13f + NOCHE.blue * 0.8f, c.alpha)

    /**
     * Cómo se pinta algo según dónde está. De día, tal cual (con [dia], un poco más claro). De noche, de su color hondo hacia
     * el suyo según [k] (lo que le llega del quinqué), teñido de llama.
     */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k).coerceAtMost(0.9f))
        return mezcla(v, LUZ, 0.14f * k)
    }

    /** La sombra: de día, violeta hondo; de noche, la noche del almacén. */
    private fun Tono.s(a: Float) = if (o) NOCHE.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día, crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LUZ.copy(alpha = (a * 0.9f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho (para pasar píxeles a fracciones). */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset, dia: Float = 0f): Tono = Tono(esNoche, if (esNoche) luz(p / anchoW) else 0f, dia)

    /** El latón de la balanza devuelve más luz que la madera: de noche se enciende un poco más que lo que tiene al lado. */
    private fun tnLaton(p: Offset): Tono = Tono(esNoche, if (esNoche) (luz(p / anchoW) * 1.3f).coerceAtMost(1f) else 0f)

    /** De noche la luz viene del quinqué: lo que está a su izquierda recibe la luz por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < FOCO.x - 0.03f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** [p] girado [grados] alrededor de [c]. */
    private fun girado(p: Offset, c: Offset, grados: Float): Offset {
        val a = Math.toRadians(grados.toDouble())
        val dx = p.x - c.x; val dy = p.y - c.y
        return Offset(c.x + dx * cos(a).toFloat() - dy * sin(a).toFloat(), c.y + dx * sin(a).toFloat() + dy * cos(a).toFloat())
    }

    /** Lo que queda de un óvalo al quitarle él mismo corrido: la media luna de sombra (o de luz) de un cuerpo redondo. */
    private fun creciente(r: Rect, dx: Float, dy: Float): Path =
        Path.combine(PathOperation.Difference, Path().apply { addOval(r) }, Path().apply { addOval(r.translate(dx, dy)) })

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera del quinqué, tanto más marcada
     * cuanto más cerca está de él. No baja de [hasta] (el sobre o el piso que la corta).
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.16f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.022f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luz(desde / w)
                if (k > 0.12f) {
                    val dx = (desde.x / w - FOCO.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    val dy = (desde.y / w - FOCO.y).coerceIn(-0.3f, 0.3f) / 0.3f
                    translate(w * 0.04f * dx, w * 0.03f * dy) { drawPath(p, NOCHE.copy(alpha = (0.6f * k).coerceAtMost(0.5f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa el sobre, una tabla o el piso, corrida a la derecha. */
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

    /** El destello del quinqué en el latón o el vidrio, de noche (de día, la luz crema). */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.06f) { drawCircle(LUZ.copy(alpha = (0.35f * t.k).coerceAtMost(0.3f)), r * 2.2f, c); drawCircle(Color(0xFFFFF0CF).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Un clavo de hierro con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, col: Color = HIERRO) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.5f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.55f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /**
     * El filo cálido del quinqué en un recipiente, de noche: en su canto de arriba y en el costado que mira a la luz ([der]: el
     * derecho), tanto más vivo cuanto más le llega. De día no pinta nada.
     */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.06f) return
        val u = density
        drawRect(LUZ.copy(alpha = (0.85f * t.k).coerceAtMost(0.75f)), r.topLeft, Size(r.width, 1.5f * u))
        drawRect(LUZ.copy(alpha = (0.3f * t.k).coerceAtMost(0.3f)), Offset(r.left, r.top + 1.5f * u), Size(r.width, 3f * u))
        val x = if (der) r.right - 1.3f * u else r.left
        drawRect(LUZ.copy(alpha = (0.6f * t.k).coerceAtMost(0.55f)), Offset(x, r.top), Size(1.3f * u, r.height))
    }

    /** Una raya a lo ancho, de [x0] a [x1]: de noche por tramos, cada uno con la luz que le llega. [clara]: luz o sombra. */
    private fun DrawScope.raya(x0: Float, x1: Float, y: Float, grosor: Float, a: Float, clara: Boolean) {
        val n = if (esNoche) maxOf(1, ((x1 - x0) / (anchoW * 0.17f)).toInt()) else 1
        for (i in 0 until n) {
            val a0 = x0 + (x1 - x0) * i / n; val a1 = x0 + (x1 - x0) * (i + 1) / n
            val t = tn(Offset((a0 + a1) / 2, y))
            drawRect(if (clara) t.b(a) else t.s(a), Offset(a0, y), Size(a1 - a0 + 0.5f, grosor))
        }
    }

    /**
     * Una tabla rescatada: su color por tramos (de noche, cada uno con la luz que le llega), la veta, lo que queda de pintura
     * si es [pintada] (la madera que asoma donde se gastó), el canto de luz arriba y a la izquierda y el de sombra abajo y a la
     * derecha.
     */
    private fun DrawScope.tabla(r: Rect, col: Color, semilla: Int, vertical: Boolean, pintada: Boolean = false) {
        val u = density
        val largo = if (vertical) r.height else r.width
        val n = maxOf(1, (largo / (anchoW * 0.2f)).toInt())
        for (i in 0 until n) {
            val s = if (vertical) Rect(r.left, r.top + r.height * i / n, r.right, r.top + r.height * (i + 1) / n + 0.5f)
            else Rect(r.left + r.width * i / n, r.top, r.left + r.width * (i + 1) / n + 0.5f, r.bottom)
            val t = tn(s.center)
            drawRect(t.c(col), s.topLeft, s.size)
            if (pintada) desgaste(s, t.c(mezcla(PINO, PINO_LUZ, 0.3f)), semilla + 31 * i, (s.width * s.height / (u * u * 230f)).toInt().coerceIn(5, 24), vertical)
            if (vertical) { drawRect(t.b(0.3f), s.topLeft, Size(1.1f * u, s.height)); drawRect(t.s(0.3f), Offset(s.right - 1.3f * u, s.top), Size(1.3f * u, s.height)) }
            else { drawRect(t.b(0.42f), s.topLeft, Size(s.width, 1.1f * u)); drawRect(t.s(0.32f), Offset(s.left, s.bottom - 1.3f * u), Size(s.width, 1.3f * u)) }
        }
        vetas(r, (if (esNoche) NOCHE else PINO_OSC).copy(alpha = if (pintada) 0.2f else 0.32f), semilla, 0.6f, vertical = vertical, grosor = 0.5f, ondulacion = 0.5f)
    }

    /**
     * La pintura gastada de una tabla: rozaduras finas a lo largo de la veta, por donde asoma la madera ([col]); la mitad, pegadas
     * a los cantos, que es donde más roza.
     */
    private fun DrawScope.desgaste(r: Rect, col: Color, semilla: Int, cuantas: Int, vertical: Boolean, alfa: Float = 0.6f) {
        val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            for (k in 0 until cuantas) {
                val largo = (7f + 30f * azar(semilla, 3 * k)) * u
                val grosor = (0.8f + 2.4f * azar(semilla, 3 * k + 1)) * u
                val a = azar(semilla, 3 * k + 2)
                val b = azar(semilla + 7, k)
                val orilla = if (k % 2 == 0) (if (b < 0.5f) b * 0.24f else 1f - (1f - b) * 0.24f) else b
                val c = col.copy(alpha = alfa * (0.45f + 0.55f * azar(semilla + 3, k)))
                if (vertical) drawRoundRect(c, Offset(r.left + r.width * orilla - grosor / 2, r.top + r.height * a - largo / 2), Size(grosor, largo), CornerRadius(grosor / 2))
                else drawRoundRect(c, Offset(r.left + r.width * a - largo / 2, r.top + r.height * orilla - grosor / 2), Size(largo, grosor), CornerRadius(grosor / 2))
            }
        }
    }

    /** Un parche de lata clavado: la chapa con su canto de luz, una abolladura, el óxido de los clavos y los cuatro clavos. */
    private fun DrawScope.parche(r: Rect, semilla: Int) {
        val u = density
        val t = tn(r.center)
        drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
        drawRect(t.c(LATA), r.topLeft, r.size)
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.b(0.25f), r.topLeft, Size(1f * u, r.height))
        drawRect(t.s(0.25f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawOval(t.s(0.14f), Offset(r.left + r.width * 0.35f, r.top + r.height * 0.3f), Size(r.width * 0.4f, r.height * 0.45f))
        drawOval(t.b(0.2f), Offset(r.left + r.width * 0.3f, r.top + r.height * 0.24f), Size(r.width * 0.3f, r.height * 0.3f))
        val m = 2.6f * u
        listOf(Offset(r.left + m, r.top + m), Offset(r.right - m, r.top + m), Offset(r.left + m, r.bottom - m), Offset(r.right - m, r.bottom - m)).forEachIndexed { i, p ->
            if (azar(semilla, i) > 0.35f) drawOval(t.c(OXIDO).copy(alpha = 0.55f), Offset(p.x - 1.6f * u, p.y - 1f * u), Size(3.2f * u, (4f + 5f * azar(semilla, i + 9)) * u))
            clavo(p, 1.1f * u, t)
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
        val pieF = sup + (frente - sup) * 0.88f
        val suelo = b.top + hb * SUELO
        val liston = Rect(0f, ch * 0.05f, w, ch * 0.118f)

        // ---- la pared de tablas encaladas, la chapa, el listón y el piso de tablones
        pared(w, sup, suelo, liston)
        piso(w, suelo, alto)

        // ---- en la pared: el sombrero, la pizarra de precios y la ristra de mazorcas
        sombrero(Offset(w * 0.125f, liston.bottom + w * 0.075f), liston)
        pizarra(Rect(w * 0.64f, liston.bottom + w * 0.032f, w * 0.86f, liston.bottom + w * 0.2f), Offset(w * 0.75f, liston.center.y))
        mazorcas(Offset(w * 0.93f, liston.center.y))

        // ---- la cajonera y su sobre de trabajo
        cajonera(d, ch, canto, b)
        sobre(w, sup, frente, canto)

        // ---- lo de encima, de atrás adelante
        libro(Offset(w * 0.035f, pieA), sup)
        sacos(pieA, pieF, sup)
        balanza(Offset(w * 0.325f, pieA), sup)
        quinque(Offset(w * FOCO.x, pieA), sup)
        gaceta(Rect(w * 0.415f, pieA + w * 0.006f, w * 0.565f, canto + w * 0.064f), frente)
        jabones(Offset(w * 0.165f, pieF))
        pesas(Offset(w * 0.455f, pieF))
        campanilla(Offset(w * 0.545f, pieF + w * 0.002f))
        frasco(Offset(w * 0.7f, pieA + w * 0.012f), w * 0.078f, w * 0.118f, LADRILLO, MARINO, sup, 0)
        frasco(Offset(w * 0.77f, pieF), w * 0.066f, w * 0.085f, MAIZ, LADRILLO, sup, 1)

        // ---- al pie: el saco de harina, la caja de fruta y el barril
        sacoHarina(Offset(w * 0.085f, b.top + hb * PIE), w, hb)
        cajaFruta(Offset(w * 0.32f, b.top + hb * (PIE - 0.03f)), w, hb)
        barril(Offset(w * 0.915f, b.top + hb * PIE), w, hb)

        // ---- la tarjeta de «y N más», clavada en el faldón
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.3f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size)
            drawRect(if (o) GACETA_LEJOS else GACETA, r.topLeft, r.size)
            drawRect(t.s(0.12f), Offset(r.right - r.width * 0.06f, r.top), Size(r.width * 0.06f, r.height))
            listOf(r.left + 4f * u, r.right - 4f * u).forEach { x -> clavo(Offset(x, r.top + 4f * u), 1.2f * u, t) }
        }

        // ---- la luz del quinqué, por encima de lo que tiene cerca
        val llama = FOCO * w
        if (o) {
            mancha(llama + Offset(0f, w * 0.03f), w * 0.46f, LUZ.copy(alpha = 0.1f))
            mancha(llama, w * 0.19f, LUZ.copy(alpha = 0.22f))
        }
        granoMueble(Rect(0f, 0f, w, alto), o)
    }

    /**
     * La pared: tablas de pino encaladas, tendidas (sus juntas, la cal gastada donde roza lo de encima y la madera que asoma),
     * con la mancha de luz arriba a la izquierda (de noche, el halo del quinqué); debajo de la cal, lo que queda de un rótulo
     * de antes (las tablas se volvieron a clavar en otro orden y ya no casa); a la derecha, la chapa ondulada con que se
     * remendó, con su solape, sus clavos y su óxido; y el listón de madera vieja del que cuelgan las cosas.
     */
    private fun DrawScope.pared(w: Float, sup: Float, suelo: Float, liston: Rect) {
        val u = density
        val o = esNoche
        if (!o) {
            drawRect(Brush.horizontalGradient(listOf(mezcla(CAL, LUZ_MUEBLE, 0.4f), CAL, mezcla(CAL, CAL_OSC, 0.75f)), 0f, w), Offset.Zero, Size(w, suelo))
            mancha(Offset(w * 0.1f, 0f), w * 0.7f, luzMueble(0.5f))
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, sombraMueble(0.15f)), sup * 0.5f, sup), Offset(0f, sup * 0.5f), Size(w, suelo - sup * 0.5f))
        } else {
            // La cal es muy clara: lejos de la llama se hunde más deprisa que lo demás, para que el halo se vea.
            fun k(d: Float) = Tono(true, luz(FOCO + Offset(d, 0f)).pow(1.3f)).c(CAL)
            drawRect(Brush.radialGradient(0f to k(0.04f), 0.1f to k(0.12f), 0.22f to k(0.26f), 0.4f to k(0.48f), 0.65f to k(0.78f), 1f to k(1.2f),
                center = FOCO * w, radius = w * 1.2f), Offset.Zero, Size(w, suelo))
        }
        pinceladas(Rect(0f, 0f, w, sup), if (o) NOCHE else CAL_OSC, 5001, 34, u, if (o) 0.03f else 0.1f)
        pinceladas(Rect(0f, 0f, w, sup), if (o) NOCHE else mezcla(CAL, LUZ_MUEBLE, 0.6f), 5002, 16, u, if (o) 0.03f else 0.12f)

        // Lo que queda del rótulo de antes, debajo de la cal: una franja, un aro y unas puntas de flecha, tabla por tabla.
        val at = w * 0.064f
        val y0 = at * 0.42f
        listOf(2 to 0f, 3 to -0.034f, 4 to 0.022f).forEach { (i, corre) ->
            val yy = y0 + at * i
            clipRect(w * 0.19f, yy, w * 0.63f, yy + at) {
                translate(w * corre, 0f) {
                    // De noche apenas se adivina (y no se oscurece la pared: el halo del quinqué no debe quedar a cuadros).
                    val f = if (o) 0.5f else 1f
                    drawRect(mezcla(LADRILLO, PINO, if (o) 0.3f else 0f).copy(alpha = 0.1f * f), Offset(w * 0.2f, y0 + at * 2.25f), Size(w * 0.42f, at * 2.3f))
                    drawCircle(MARINO.copy(alpha = 0.13f * f), w * 0.062f, Offset(w * 0.3f, y0 + at * 3.4f), style = Stroke(w * 0.022f))
                    if (!o) {
                        for (j in 0 until 3) {
                            val x = w * (0.42f + 0.055f * j); val yc = y0 + at * 3.4f
                            drawPath(poligono(x, yc - at * 0.75f, x + w * 0.035f, yc, x, yc + at * 0.75f, x + w * 0.016f, yc + at * 0.75f, x + w * 0.051f, yc, x + w * 0.016f, yc - at * 0.75f),
                                GACETA.copy(alpha = 0.3f))
                        }
                        // La cal de encima, a brochazos: tapa el rótulo a trozos.
                        pinceladas(Rect(w * 0.16f, yy, w * 0.66f, yy + at), CAL, 5005 + i, 7, u, 0.5f)
                    }
                }
            }
        }
        // Las juntas de las tablas: arriba, de lado a lado; detrás de la cajonera solo asoman a los costados.
        var y = y0
        var i = 0
        while (y < suelo) {
            val x1 = if (y < sup) w else w * IZQ
            raya(0f, x1, y, 1.2f * u, 0.3f, false)
            raya(0f, x1, y + 1.2f * u, 0.9f * u, 0.5f, true)
            if (y >= sup) { raya(w * DER, w, y, 1.2f * u, 0.3f, false) }
            // Cada tabla, de su tono: unas un poco más grises, otras más crema.
            if (y < sup && i % 3 != 1) drawRect((if (o) NOCHE else if (i % 3 == 0) CAL_OSC else LUZ_MUEBLE).copy(alpha = (if (i % 3 == 0) 0.1f else 0.16f) * (if (o) 0.6f else 1f)), Offset(0f, y + 2f * u), Size(w, at - 2f * u))
            y += at; i++
        }
        // Los empalmes de las tablas (a tope, sobre los montantes) y sus clavos, con el hilo de óxido que deja cada uno.
        listOf(0.235f to 1, 0.545f to 0, 0.235f to 3, 0.545f to 4, 0.545f to 6, 0.235f to 6).forEach { (fx, fila) ->
            val yy = y0 + at * fila
            val t = tn(Offset(w * fx, yy + at / 2))
            drawRect(t.s(0.32f), Offset(w * fx, yy + 1.2f * u), Size(1.1f * u, at - 1.2f * u))
            drawRect(t.b(0.4f), Offset(w * fx + 1.1f * u, yy + 1.2f * u), Size(0.8f * u, at - 1.2f * u))
        }
        for (f in 0 until 8) listOf(0.225f, 0.555f).forEachIndexed { j, fx ->
            val p = Offset(w * fx + w * 0.004f * (azar(5010 + j, f) - 0.5f), y0 + at * (f + 0.3f + 0.4f * azar(5011 + j, f)))
            if (p.y < sup - w * 0.02f && (p.y < liston.top || p.y > liston.bottom + w * 0.01f)) {
                val t = tn(p)
                if (azar(5012 + j, f) > 0.4f) drawOval(t.c(OXIDO).copy(alpha = 0.35f), Offset(p.x - 1.2f * u, p.y), Size(2.4f * u, (5f + 9f * azar(5013 + j, f)) * u))
                clavo(p, 1.2f * u, t)
            }
        }
        // La cal gastada: la madera que asoma donde rozan las cosas, abajo, y en algún nudo.
        listOf(Triple(0.2f, 0.97f, 0.05f), Triple(0.29f, 0.985f, 0.035f), Triple(0.5f, 0.975f, 0.06f), Triple(0.66f, 0.99f, 0.04f), Triple(0.06f, 0.93f, 0.04f),
            Triple(0.43f, 0.31f, 0.02f), Triple(0.33f, 0.62f, 0.016f), Triple(0.58f, 0.2f, 0.018f)).forEachIndexed { j, (fx, fy, r) ->
            val c = Offset(w * fx, sup * fy)
            val t = tn(c)
            val madera = t.c(mezcla(PINO, CAL_OSC, if (o) 0.6f else 0.25f))
            for (q in 0 until 4) drawOval(madera.copy(alpha = if (o) 0.12f else 0.6f), Offset(c.x + w * r * (azar(5020 + j, q) - 0.9f), c.y + w * r * 0.4f * (azar(5021 + j, q) - 0.8f)), Size(w * r * (0.8f + 0.9f * azar(5022 + j, q)), w * r * (0.22f + 0.2f * azar(5023 + j, q))))
            drawLine(t.s(0.2f), Offset(c.x - w * r * 0.6f, c.y - w * r * 0.1f), Offset(c.x + w * r * 0.5f, c.y - w * r * 0.12f), 0.6f * u)
        }
        // La mugre de abajo, donde la pared toca el sobre.
        raya(0f, w, sup - w * 0.012f, w * 0.012f, 0.1f, false)

        chapa(w * 0.775f, w, sup, suelo)

        // ---- el listón: madera vieja, con su veta, lo que le queda de pintura, dos tirafondos y su sombra en la pared
        raya(0f, w, liston.bottom, w * 0.011f, 0.3f, false)
        raya(0f, w, liston.bottom + w * 0.011f, w * 0.012f, 0.13f, false)
        tabla(liston, TABLON, 5030, vertical = false)
        val tl = tn(Offset(w * 0.36f, liston.center.y))
        drawRect(tl.c(MARINO).copy(alpha = 0.5f), Offset(w * 0.27f, liston.top + 1.2f * u), Size(w * 0.17f, liston.height * 0.62f))
        pinceladas(Rect(w * 0.27f, liston.top, w * 0.44f, liston.bottom), tl.c(TABLON), 5031, 9, u, 0.6f)
        listOf(0.045f, 0.5f, 0.965f).forEach { fx ->
            val p = Offset(w * fx, liston.center.y)
            val t = tn(p)
            drawRect(t.s(0.3f), Offset(p.x - 3.2f * u, p.y - 3.2f * u + 1.5f * u), Size(8f * u, 8f * u))
            drawRect(t.c(HIERRO), Offset(p.x - 4f * u, p.y - 4f * u), Size(8f * u, 8f * u))
            drawRect(t.b(0.4f), Offset(p.x - 4f * u, p.y - 4f * u), Size(8f * u, 1f * u))
            drawCircle(t.c(mezcla(HIERRO, OXIDO, 0.4f)), 2.2f * u, p)
            drawCircle(t.b(0.5f), 0.8f * u, p - Offset(0.7f * u, 0.7f * u))
        }
        // Un clavo libre, con una lazada de cordel que alguien dejó colgada.
        val pc = Offset(w * 0.47f, liston.center.y + liston.height * 0.1f)
        val tc = tn(pc)
        val lazada = Path().apply {
            moveTo(pc.x, pc.y)
            cubicTo(pc.x - w * 0.02f, pc.y + w * 0.03f, pc.x - w * 0.016f, pc.y + w * 0.062f, pc.x, pc.y + w * 0.064f)
            cubicTo(pc.x + w * 0.016f, pc.y + w * 0.062f, pc.x + w * 0.018f, pc.y + w * 0.03f, pc.x, pc.y)
        }
        translate(w * 0.012f, w * 0.008f) { drawPath(lazada, tc.s(0.2f), style = Stroke(1.4f * u)) }
        drawPath(lazada, tc.c(CUERDA), style = Stroke(1.5f * u))
        clavo(pc, 1.6f * u, tc)
    }

    /**
     * La chapa ondulada rescatada, clavada sobre las tablas de [x0] al borde: las ondas a lo alto (cada una con su lomo de luz y
     * su valle de sombra), el solape de las dos hojas, el borde cortado, los clavos con su arandela y el óxido que chorrea.
     */
    private fun DrawScope.chapa(x0: Float, w: Float, sup: Float, suelo: Float) {
        val u = density
        val onda = w * 0.025f
        val solape = sup * 0.56f
        var y = 0f
        while (y < suelo) {
            val y1 = minOf(suelo, y + w * 0.13f)
            // Detrás de la cajonera solo asoma la franja del costado.
            val xa = if (y >= sup) w * DER else x0
            val t = tn(Offset((xa + w) / 2, (y + y1) / 2))
            drawRect(t.c(mezcla(LATA, MARINO, 0.12f)), Offset(xa, y), Size(w - xa, y1 - y + 0.5f))
            var x = x0
            while (x < w) {
                if (x + onda > xa) {
                    drawRect(t.b(0.3f), Offset(x + onda * 0.08f, y), Size(onda * 0.3f, y1 - y + 0.5f))
                    drawRect(t.b(0.3f), Offset(x + onda * 0.17f, y), Size(onda * 0.1f, y1 - y + 0.5f))
                    drawRect(t.s(0.2f), Offset(x + onda * 0.56f, y), Size(onda * 0.4f, y1 - y + 0.5f))
                    drawRect(t.s(0.18f), Offset(x + onda * 0.74f, y), Size(onda * 0.16f, y1 - y + 0.5f))
                }
                x += onda
            }
            y = y1
        }
        // El óxido: abajo, donde se junta el polvo, y en chorreras desde el solape.
        for (k in 0 until 9) {
            val x = x0 + (w - x0) * azar(5040, k)
            val alto = w * (0.03f + 0.07f * azar(5041, k))
            val arriba = if (k % 3 == 0) solape else sup - alto
            val t = tn(Offset(x, arriba))
            drawOval(t.c(OXIDO).copy(alpha = if (k % 3 == 0) 0.3f else 0.4f), Offset(x - onda * 0.3f, arriba), Size(onda * (if (k % 3 == 0) 0.3f + 0.3f * azar(5042, k) else 0.7f + 0.9f * azar(5042, k)), alto))
        }
        pinceladas(Rect(x0, 0f, w, sup), tn(Offset(w * 0.9f, sup * 0.4f)).c(OXIDO), 5043, 10, u, 0.2f)
        // El solape de las dos hojas, con su sombra, y el borde cortado de la izquierda.
        raya(x0, w, solape, 1.6f * u, 0.4f, false)
        raya(x0, w, solape + 1.6f * u, 3f * u, 0.16f, false)
        raya(x0, w, solape - 1f * u, 1f * u, 0.5f, true)
        var yy = 0f
        while (yy < sup) {
            val t = tn(Offset(x0, yy + w * 0.06f))
            drawRect(t.b(0.6f), Offset(x0, yy), Size(1.2f * u, w * 0.13f))
            drawRect(t.s(0.22f), Offset(x0 - 2.4f * u, yy), Size(2.4f * u, w * 0.13f))
            yy += w * 0.13f
        }
        // Los clavos de cabeza ancha, con su arandela.
        listOf(Offset(0.02f, 0.28f), Offset(0.02f, 0.7f), Offset(0.1f, 0.585f), Offset(0.19f, 0.585f), Offset(0.02f, 0.93f), Offset(0.145f, 0.3f)).forEach { f ->
            val p = Offset(x0 + w * f.x, sup * f.y)
            val t = tn(p)
            drawOval(t.c(OXIDO).copy(alpha = 0.5f), Offset(p.x - 2f * u, p.y), Size(4f * u, 10f * u))
            drawCircle(t.c(mezcla(LATA, GACETA, 0.3f)), 2.6f * u, p)
            clavo(p, 1.4f * u, t)
        }
    }

    /** El piso de tablones que se abren hacia quien mira, con sus juntas, su veta, el polvo y la sombra del rincón. */
    private fun DrawScope.piso(w: Float, suelo: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, suelo))
        drawRect(t.c(mezcla(TABLON, PINO_OSC, 0.45f)), Offset(0f, suelo), Size(w, alto - suelo))
        for (i in -3..11) {
            val x0 = w * i / 8f
            val x1 = w / 2 + (x0 - w / 2) * 1.5f
            drawLine(t.s(0.4f), Offset(x0, suelo), Offset(x1, alto), 1.4f * u)
            drawLine(t.b(0.2f), Offset(x0 + 1.4f * u, suelo), Offset(x1 + 1.4f * u, alto), 0.7f * u)
        }
        pinceladas(Rect(0f, suelo, w, alto), if (esNoche) NOCHE else PINO, 5050, 18, u, 0.16f)
        drawRect(tt.s(0.35f), Offset(0f, suelo), Size(w, 2.5f * u))
        drawRect(tt.s(0.15f), Offset(0f, suelo + 2.5f * u), Size(w, 5f * u))
    }

    // ---------------------------------------------------------------- en la pared

    /**
     * El sombrero de paja de ala ancha de Ruth, colgado del listón por el barboquejo: visto de frente a la copa, el ala con sus
     * vueltas de trenza y el borde deshilachado, la copa con su hendidura, la cinta azul con las dos puntas sueltas y un
     * remiendo cosido.
     */
    private fun DrawScope.sombrero(c: Offset, liston: Rect) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val rx = w * 0.094f; val ry = w * 0.082f
        val ala = Rect(c.x - rx, c.y - ry, c.x + rx, c.y + ry)
        val forma = Path().apply { addOval(ala) }
        val clavoP = Offset(c.x + w * 0.006f, liston.center.y)
        // El barboquejo, del clavo a los dos lados del ala.
        listOf(-0.62f, 0.66f).forEach { f ->
            val p = Offset(c.x + rx * f, c.y - ry * 0.7f)
            translate(w * 0.01f, w * 0.006f) { drawLine(t.s(0.2f), clavoP, p, 1.2f * u) }
            drawLine(t.c(mezcla(CUERDA, PINO_OSC, 0.3f)), clavoP, p, 1.3f * u)
        }
        clavo(clavoP, 1.8f * u, t)
        sombraEnPared(forma, c, size.height, 0.26f)
        // El borde deshilachado: unas pajas que se salen.
        for (k in 0 until 9) {
            val a = 0.5f + 2.3f * azar(5100, k)
            val p0 = Offset(c.x + rx * 0.97f * cos(a), c.y + ry * 0.97f * sin(a))
            drawLine(t.c(PAJA), p0, p0 + Offset(rx * 0.1f * cos(a + 0.5f), ry * 0.12f * sin(a + 0.5f)), 0.8f * u)
        }
        drawPath(forma, t.c(PAJA))
        val der = luzDerecha(c.x)
        clipPath(forma) {
            // Las vueltas de la trenza.
            for (j in 1..6) {
                val f = 0.42f + 0.58f * j / 6.5f
                drawOval(t.s(0.13f), Offset(c.x - rx * f, c.y - ry * f - ry * 0.05f * (1f - f)), Size(rx * 2 * f, ry * 2 * f), style = Stroke(0.8f * u))
            }
            // La luz de arriba a la izquierda y la sombra del otro lado (de noche, al revés: la luz viene del quinqué).
            drawPath(creciente(ala, if (der) -rx * 0.22f else rx * 0.22f, ry * 0.2f), t.b(0.3f))
            drawPath(creciente(ala, if (der) rx * 0.2f else -rx * 0.2f, -ry * 0.22f), t.s(0.2f))
            pinceladas(ala, t.c(mezcla(PAJA, PINO_OSC, 0.5f)), 5101, 10, u, 0.22f)
            // El remiendo: un trozo de tela cosido en el ala.
            val rm = Rect(c.x - rx * 0.86f, c.y + ry * 0.08f, c.x - rx * 0.52f, c.y + ry * 0.42f)
            rotate(-18f, rm.center) {
                drawRect(t.c(mezcla(PAJA, ARPILLERA, 0.7f)), rm.topLeft, rm.size)
                for (k in 0 until 4) {
                    val x = rm.left + rm.width * (0.12f + 0.25f * k)
                    drawLine(t.c(TINTA).copy(alpha = 0.55f), Offset(x, rm.top - 1.2f * u), Offset(x, rm.top + 1.6f * u), 0.7f * u)
                    drawLine(t.c(TINTA).copy(alpha = 0.55f), Offset(x, rm.bottom - 1.6f * u), Offset(x, rm.bottom + 1.2f * u), 0.7f * u)
                }
            }
        }
        // La copa: su sombra en el ala, la cinta alrededor y lo de arriba, con la hendidura.
        val cc = Offset(c.x - rx * 0.03f, c.y - ry * 0.1f)
        val rc = rx * 0.47f
        val copa = Rect(cc.x - rc, cc.y - rc * 0.92f, cc.x + rc, cc.y + rc * 0.92f)
        clipPath(forma) { translate(if (der) -rx * 0.14f else rx * 0.14f, ry * 0.14f) { drawOval(t.s(0.3f), copa.topLeft, copa.size) } }
        drawOval(t.c(MARINO), Offset(copa.left - rc * 0.1f, copa.top - rc * 0.02f), Size(copa.width + rc * 0.2f, copa.height + rc * 0.16f))
        // Las dos puntas de la cinta, que caen sobre el ala.
        listOf(0.22f to 12f, 0.42f to -6f).forEach { (f, g) ->
            val p = Offset(cc.x + rc * f, copa.bottom + rc * 0.02f)
            rotate(g, p) {
                drawPath(poligono(p.x - rc * 0.13f, p.y, p.x + rc * 0.13f, p.y, p.x + rc * 0.15f, p.y + rc * 0.75f, p.x, p.y + rc * 0.62f, p.x - rc * 0.15f, p.y + rc * 0.75f), t.c(mezcla(MARINO, TINTA, 0.15f)))
                drawLine(t.b(0.3f), Offset(p.x - rc * 0.1f, p.y + rc * 0.05f), Offset(p.x - rc * 0.12f, p.y + rc * 0.65f), 0.7f * u)
            }
        }
        drawOval(t.c(mezcla(PAJA, GACETA, 0.25f)), copa.topLeft, copa.size)
        clipPath(Path().apply { addOval(copa) }) {
            drawPath(creciente(copa, if (der) rc * 0.3f else -rc * 0.3f, -rc * 0.32f), t.s(0.24f))
            for (j in 1..3) drawOval(t.s(0.1f), Offset(cc.x - rc * j / 4f, cc.y - rc * 0.92f * j / 4f), Size(rc * 2 * j / 4f, rc * 1.84f * j / 4f), style = Stroke(0.7f * u))
            // La hendidura de la copa.
            drawOval(t.s(0.2f), Offset(cc.x - rc * 0.5f, cc.y - rc * 0.2f), Size(rc * 1.05f, rc * 0.42f))
            drawOval(t.b(0.35f), Offset(cc.x - rc * 0.5f, cc.y - rc * 0.08f), Size(rc * 1.05f, rc * 0.4f), style = Stroke(0.8f * u))
        }
        drawOval(t.b(0.45f), Offset(if (der) cc.x + rc * 0.25f else cc.x - rc * 0.66f, cc.y - rc * 0.7f), Size(rc * 0.42f, rc * 0.3f))
    }

    /** Una cifra a tiza, de trazo de palo, en una caja de [alto] de alto (y 0.6 de ancho). */
    private fun DrawScope.cifra(n: Int, o: Offset, alto: Float, col: Color, grosor: Float) {
        val pts: List<Pair<Float, Float>> = when (n) {
            1 -> listOf(0.15f to 0.3f, 0.5f to 0f, 0.5f to 1f)
            2 -> listOf(0.05f to 0.25f, 0.2f to 0.03f, 0.48f to 0.03f, 0.6f to 0.26f, 0.05f to 1f, 0.62f to 1f)
            3 -> listOf(0.05f to 0.05f, 0.58f to 0.05f, 0.28f to 0.42f, 0.56f to 0.56f, 0.56f to 0.86f, 0.32f to 1f, 0.05f to 0.88f)
            4 -> listOf(0.48f to 1f, 0.48f to 0f, 0.04f to 0.66f, 0.64f to 0.66f)
            5 -> listOf(0.58f to 0.03f, 0.14f to 0.03f, 0.08f to 0.44f, 0.38f to 0.38f, 0.6f to 0.58f, 0.56f to 0.86f, 0.3f to 1f, 0.05f to 0.88f)
            7 -> listOf(0.05f to 0.03f, 0.6f to 0.03f, 0.26f to 1f)
            else -> listOf(0.32f to 0f, 0.58f to 0.2f, 0.58f to 0.8f, 0.32f to 1f, 0.06f to 0.8f, 0.06f to 0.2f, 0.32f to 0f)
        }
        val p = Path().apply { pts.forEachIndexed { i, (x, y) -> val q = Offset(o.x + x * alto, o.y + y * alto); if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) } }
        drawPath(p, col, style = Stroke(grosor, cap = StrokeCap.Round))
    }

    /**
     * La pizarra de precios, colgada del listón con un cordel y un poco ladeada: el marco de pino, la piedra con sus borrones,
     * y a tiza tres renglones (una manzana, un saco y un frasco, cada uno con su precio; uno tachado y vuelto a escribir); la
     * tiza cuelga de su hilo.
     */
    private fun DrawScope.pizarra(r: Rect, clavoP: Offset) {
        val u = density
        val w = anchoW
        val t = tn(r.center)
        val giro = -2.2f
        val e0 = girado(Offset(r.left + r.width * 0.12f, r.top), r.center, giro)
        val e1 = girado(Offset(r.right - r.width * 0.12f, r.top), r.center, giro)
        listOf(e0, e1).forEach { e ->
            translate(w * 0.012f, w * 0.007f) { drawLine(t.s(0.2f), clavoP, e, 1.2f * u) }
            drawLine(t.c(CUERDA), clavoP, e, 1.4f * u)
        }
        clavo(clavoP, 1.8f * u, t)
        rotate(giro, r.center) {
            sombraEnPared(Path().apply { addRect(r) }, r.center, size.height, 0.26f)
            val m = r.width * 0.055f
            drawRect(t.c(mezcla(PINO, PINO_LUZ, 0.4f)), r.topLeft, r.size)
            vetas(Rect(r.left, r.top, r.right, r.top + m), (if (esNoche) NOCHE else PINO_OSC).copy(alpha = 0.35f), 5110, 0.8f, grosor = 0.5f)
            vetas(Rect(r.left, r.bottom - m, r.right, r.bottom), (if (esNoche) NOCHE else PINO_OSC).copy(alpha = 0.35f), 5111, 0.8f, grosor = 0.5f)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1.1f * u))
            drawRect(t.b(0.3f), r.topLeft, Size(1.1f * u, r.height))
            drawRect(t.s(0.3f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
            // Los ingletes del marco.
            listOf(r.topLeft to Offset(m, m), Offset(r.right, r.top) to Offset(-m, m), Offset(r.left, r.bottom) to Offset(m, -m), r.bottomRight to Offset(-m, -m)).forEach { (p, dd) -> drawLine(t.s(0.3f), p, p + dd, 0.7f * u) }
            val p = Rect(r.left + m, r.top + m, r.right - m, r.bottom - m)
            drawRect(t.c(PIZARRA), p.topLeft, p.size)
            drawRect(t.s(0.35f), p.topLeft, Size(p.width, 2f * u))
            drawRect(t.s(0.25f), p.topLeft, Size(1.6f * u, p.height))
            // Los borrones del trapo.
            val tiza = t.c(GACETA)
            pinceladas(p, tiza, 5112, 9, u, 0.09f)
            drawOval(tiza.copy(alpha = 0.1f), Offset(p.left + p.width * 0.52f, p.top + p.height * 0.5f), Size(p.width * 0.4f, p.height * 0.3f))
            val g = 1.5f * u
            val trazo = tiza.copy(alpha = 0.88f)
            val alto = p.height * 0.17f
            listOf(0.1f, 0.4f, 0.7f).forEachIndexed { i, fy ->
                val y = p.top + p.height * fy
                val ix = p.left + p.width * 0.14f
                when (i) {
                    0 -> { // una manzana con su rabo
                        drawCircle(trazo, alto * 0.42f, Offset(ix, y + alto * 0.58f), style = Stroke(g))
                        drawLine(trazo, Offset(ix, y + alto * 0.2f), Offset(ix + alto * 0.18f, y - alto * 0.05f), g, StrokeCap.Round)
                    }
                    1 -> { // un saco atado
                        drawPath(Path().apply {
                            moveTo(ix - alto * 0.28f, y + alto * 0.22f); lineTo(ix - alto * 0.45f, y + alto); lineTo(ix + alto * 0.45f, y + alto); lineTo(ix + alto * 0.28f, y + alto * 0.22f); close()
                            moveTo(ix - alto * 0.3f, y); lineTo(ix + alto * 0.3f, y + alto * 0.22f); moveTo(ix + alto * 0.3f, y); lineTo(ix - alto * 0.3f, y + alto * 0.22f)
                        }, trazo, style = Stroke(g, cap = StrokeCap.Round))
                    }
                    else -> { // un frasco con su tapa
                        drawRoundRect(trazo, Offset(ix - alto * 0.36f, y + alto * 0.26f), Size(alto * 0.72f, alto * 0.76f), CornerRadius(alto * 0.14f), style = Stroke(g))
                        drawLine(trazo, Offset(ix - alto * 0.42f, y + alto * 0.1f), Offset(ix + alto * 0.42f, y + alto * 0.1f), g * 1.5f, StrokeCap.Round)
                    }
                }
                // La raya de puntos hasta el precio.
                for (q in 0 until 4) drawCircle(trazo.copy(alpha = 0.6f), 0.8f * u, Offset(p.left + p.width * (0.3f + 0.06f * q), y + alto * 0.9f))
                val nx = p.left + p.width * 0.58f
                when (i) {
                    0 -> cifra(3, Offset(nx, y), alto, trazo, g)
                    1 -> { cifra(1, Offset(nx, y), alto, trazo, g); cifra(2, Offset(nx + alto * 0.72f, y), alto, trazo, g) }
                    else -> {
                        // El precio viejo, tachado, y el nuevo al lado.
                        cifra(7, Offset(nx - alto * 0.25f, y), alto, trazo.copy(alpha = 0.45f), g)
                        drawLine(trazo, Offset(nx - alto * 0.4f, y + alto * 0.72f), Offset(nx + alto * 0.5f, y + alto * 0.22f), g, StrokeCap.Round)
                        cifra(5, Offset(nx + alto * 1.15f, y), alto, trazo, g)
                    }
                }
            }
            // La tiza, que cuelga de su hilo desde la esquina.
            val h0 = Offset(r.right - m * 0.5f, r.bottom)
            val h1 = Offset(r.right + w * 0.004f, r.bottom + w * 0.036f)
            drawLine(t.c(CUERDA), h0, h1, 0.8f * u)
            translate(w * 0.008f, w * 0.005f) { drawLine(t.s(0.25f), h1, h1 + Offset(w * 0.003f, w * 0.022f), 3.4f * u, StrokeCap.Round) }
            drawLine(t.c(GACETA), h1, h1 + Offset(w * 0.003f, w * 0.022f), 3.4f * u, StrokeCap.Round)
        }
    }

    /**
     * La ristra de mazorcas colgada del listón: cuatro mazorcas de distinto grano (amarillo, rojo oscuro, pálido), cada una
     * con sus hileras de granos y su lado en sombra, atadas por las farfollas, que se abren hacia arriba.
     */
    private fun DrawScope.mazorcas(clavoP: Offset) {
        val u = density
        val w = anchoW
        val nudo = Offset(clavoP.x, clavoP.y + w * 0.062f)
        val t = tn(Offset(nudo.x, nudo.y + w * 0.09f))
        val der = luzDerecha(nudo.x)
        val espigas = listOf(Triple(30f, 0.118f, mezcla(MAIZ, OXIDO, 0.35f)), Triple(-27f, 0.13f, MAIZ), Triple(12f, 0.15f, mezcla(LADRILLO, TABLON, 0.35f)), Triple(-8f, 0.142f, mezcla(MAIZ, GACETA, 0.3f)))
        val cuello = w * 0.03f
        val ancho = w * 0.04f
        fun forma(largo: Float): Path = Path().apply {
            val y0 = nudo.y + cuello; val y1 = y0 + w * largo
            moveTo(nudo.x - ancho * 0.36f, y0)
            cubicTo(nudo.x - ancho * 0.62f, y0 + (y1 - y0) * 0.3f, nudo.x - ancho * 0.5f, y1 - (y1 - y0) * 0.2f, nudo.x, y1)
            cubicTo(nudo.x + ancho * 0.5f, y1 - (y1 - y0) * 0.2f, nudo.x + ancho * 0.62f, y0 + (y1 - y0) * 0.3f, nudo.x + ancho * 0.36f, y0)
            close()
        }
        // Su sombra en la pared: la de cada mazorca, en una sola mancha.
        val sombra = Path()
        espigas.forEach { (g, largo, _) ->
            val y0 = nudo.y + cuello; val y1 = y0 + w * largo
            val pts = listOf(Offset(nudo.x - ancho * 0.4f, y0), Offset(nudo.x - ancho * 0.55f, (y0 + y1) / 2), Offset(nudo.x - ancho * 0.3f, y1 - ancho * 0.3f), Offset(nudo.x, y1),
                Offset(nudo.x + ancho * 0.3f, y1 - ancho * 0.3f), Offset(nudo.x + ancho * 0.55f, (y0 + y1) / 2), Offset(nudo.x + ancho * 0.4f, y0), nudo).map { girado(it, nudo, g) }
            val una = Path().apply { pts.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }; close() }
            sombra.op(sombra, una, PathOperation.Union)
        }
        sombraEnPared(sombra, Offset(nudo.x, nudo.y + w * 0.1f), size.height, 0.24f)
        // El cordel del clavo al nudo.
        drawLine(t.c(CUERDA), clavoP, nudo, 1.5f * u)
        clavo(clavoP, 1.8f * u, t)
        // Las farfollas secas, abiertas hacia arriba por detrás del nudo.
        listOf(-150f, -118f, -80f, -48f, -22f).forEachIndexed { i, g ->
            val a = Math.toRadians(g.toDouble())
            val p = Offset(nudo.x + w * 0.05f * cos(a).toFloat(), nudo.y + w * 0.042f * sin(a).toFloat())
            val q = Offset(nudo.x + w * 0.02f * cos(a + 0.5).toFloat(), nudo.y + w * 0.02f * sin(a + 0.5).toFloat())
            drawPath(Path().apply { moveTo(nudo.x, nudo.y); cubicTo(q.x, q.y, p.x, p.y + w * 0.012f, p.x, p.y); cubicTo(p.x, p.y, q.x + w * 0.012f, q.y, nudo.x, nudo.y); close() },
                t.c(mezcla(GACETA, CUERDA, 0.25f + 0.12f * (i % 3))))
        }
        espigas.forEachIndexed { i, (g, largo, grano) ->
            rotate(g, nudo) {
                val f = forma(largo)
                val y0 = nudo.y + cuello; val y1 = y0 + w * largo
                // Las farfollas que la sujetan al nudo.
                drawPath(poligono(nudo.x, nudo.y, nudo.x - ancho * 0.4f, y0 + w * 0.012f, nudo.x + ancho * 0.4f, y0 + w * 0.012f), t.c(mezcla(GACETA, CUERDA, 0.45f)))
                drawLine(t.s(0.2f), nudo, Offset(nudo.x, y0 + w * 0.01f), 0.6f * u)
                drawPath(f, t.c(grano))
                clipPath(f) {
                    // Las hileras de granos.
                    var y = y0 + w * 0.006f
                    while (y < y1) { drawLine(t.s(0.2f), Offset(nudo.x - ancho, y), Offset(nudo.x + ancho, y), 0.7f * u); y += w * 0.0075f }
                    listOf(-0.22f, 0.02f, 0.26f).forEach { fx -> drawLine(t.s(0.16f), Offset(nudo.x + ancho * fx, y0), Offset(nudo.x + ancho * fx * 0.6f, y1), 0.7f * u) }
                    val luzX = if (der) nudo.x + ancho * 0.1f else nudo.x - ancho * 0.42f
                    drawRect(t.b(0.3f), Offset(luzX, y0), Size(ancho * 0.3f, y1 - y0))
                    drawRect(t.s(0.26f), Offset(if (der) nudo.x - ancho * 0.6f else nudo.x + ancho * 0.18f, y0), Size(ancho * 0.45f, y1 - y0))
                    // Unos granos que faltan.
                    if (i % 2 == 0) drawOval(t.c(mezcla(GACETA, CUERDA, 0.5f)), Offset(nudo.x - ancho * 0.1f, y0 + (y1 - y0) * 0.55f), Size(ancho * 0.3f, w * 0.014f))
                }
            }
        }
        // El nudo de cordel, con sus vueltas.
        drawOval(t.s(0.3f), Offset(nudo.x - w * 0.012f, nudo.y - w * 0.006f + 1.5f * u), Size(w * 0.03f, w * 0.03f))
        drawOval(t.c(CUERDA), Offset(nudo.x - w * 0.015f, nudo.y - w * 0.01f), Size(w * 0.03f, w * 0.03f))
        for (k in 0 until 3) drawLine(t.s(0.3f), Offset(nudo.x - w * 0.014f, nudo.y - w * 0.002f + w * 0.006f * k), Offset(nudo.x + w * 0.014f, nudo.y + w * 0.002f + w * 0.006f * k), 0.7f * u)
        drawLine(t.b(0.4f), Offset(nudo.x - w * 0.012f, nudo.y - w * 0.006f), Offset(nudo.x + w * 0.006f, nudo.y - w * 0.009f), 0.8f * u)
    }

    // ---------------------------------------------------------------- la cajonera

    /** Los tonos de los travesaños y de los tramos del larguero del medio, fila por fila: (color, si es pintura). */
    private val TRAVESANOS = listOf(PINO_LUZ to false, mezcla(PINO, PINO_OSC, 0.35f) to false, mezcla(SALVIA, PINO, 0.2f) to true, mezcla(PINO, PINO_LUZ, 0.5f) to false,
        mezcla(PINO_OSC, PINO, 0.5f) to false, mezcla(MARINO, PINO, 0.25f) to true)
    private val MEDIOS = listOf(mezcla(MARINO, PINO, 0.2f) to true, mezcla(PINO, PINO_LUZ, 0.6f) to false, mezcla(LADRILLO, PINO, 0.3f) to true, mezcla(PINO_OSC, PINO, 0.3f) to false,
        mezcla(SALVIA, PINO, 0.25f) to true)

    /**
     * El cuerpo de la cajonera, de tablas rescatadas: su sombra en la pared y en el piso, los dos largueros de los costados (el
     * de la derecha, pintado de ladrillo), los travesaños entre fila y fila y los tramos del larguero del medio, cada uno de su
     * tono, con sus clavos; los parches de lata; el faldón de abajo y los tacos en que descansa.
     */
    private fun DrawScope.cajonera(d: Disposicion, ch: Float, canto: Float, b: Rect) {
        val u = density
        val w = anchoW
        val hb = b.height
        val xL = w * IZQ; val xR = w * DER
        val suelo = b.top + hb * SUELO
        val pisan = b.top + hb * PISAN
        val abajo = b.top + hb * FALDON
        val h0 = d.huecos.first()
        val h1 = d.huecos[cols - 1]
        val izq = h0.left; val der = h1.right
        val sep = d.separacion

        // Su sombra en la pared (a la derecha) y debajo, entre los tacos.
        val t0 = tn(Offset(xR, ch * 1.4f))
        drawPath(poligono(xR, ch * SUP, xR + w * 0.02f, ch * SUP + w * 0.03f, xR + w * 0.02f, suelo, xR, suelo), t0.s(0.3f))
        val tp = tn(Offset(w * 0.5f, pisan))
        drawRect(tp.c(mezcla(TABLON, TINTA, 0.55f)), Offset(xL, abajo - 1f), Size(xR - xL, pisan - abajo + 1f))
        drawRect(tp.s(0.35f), Offset(xL, abajo - 1f), Size(xR - xL, (pisan - abajo) * 0.5f))
        drawRect(tp.s(0.34f), Offset(xL + w * 0.02f, pisan), Size(xR - xL + w * 0.005f, hb * 0.05f))
        drawRect(tp.s(0.16f), Offset(xL + w * 0.03f, pisan + hb * 0.05f), Size(xR - xL, hb * 0.05f))
        // Los tacos de madera vieja.
        listOf(xL + w * 0.03f, xR - w * 0.16f).forEachIndexed { i, x ->
            val r = Rect(x, abajo, x + w * 0.13f, pisan)
            tabla(r, mezcla(TABLON, PINO_OSC, 0.3f), 5200 + i, vertical = false)
            val t = tn(r.center)
            drawRect(t.s(0.35f), r.topLeft, Size(r.width, r.height * 0.3f))
            drawLine(t.s(0.25f), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.3f, r.bottom), 0.8f * u)
        }

        // El fondo de todo el cuerpo (lo que queda entre tabla y tabla).
        val cuerpo = Rect(xL, canto, xR, abajo)
        drawRect(tn(cuerpo.center).c(PINO_OSC), cuerpo.topLeft, cuerpo.size)

        // Los travesaños: el friso de arriba, uno entre fila y fila, y el faldón.
        val filas = d.filas
        for (i in 0..filas.size) {
            val y0 = if (i == 0) canto else filas[i - 1].bottom
            val y1 = if (i == filas.size) abajo else filas[i].top
            val par = if (i == 0) (mezcla(PINO, PINO_LUZ, 0.3f) to false) else if (i == filas.size) (mezcla(MARINO, PINO, 0.18f) to true) else TRAVESANOS[(i - 1) % TRAVESANOS.size]
            tabla(Rect(izq, y0, der, y1), par.first, 5210 + i, vertical = false, pintada = par.second)
            // La sombra que el canto del sobre deja en el friso.
            if (i == 0) {
                raya(izq, der, y0, 2.6f * u, 0.34f, false); raya(izq, der, y0 + 2.6f * u, 3.4f * u, 0.15f, false)
                // De noche el quinqué está encima del sobre: el vuelo del sobre deja el friso a media luz.
                if (esNoche) { drawRect(NOCHE.copy(alpha = 0.3f), Offset(izq, y0), Size(der - izq, (y1 - y0) * 0.55f)); drawRect(NOCHE.copy(alpha = 0.14f), Offset(izq, y0 + (y1 - y0) * 0.55f), Size(der - izq, (y1 - y0) * 0.45f)) }
            }
        }
        // Los tramos del larguero del medio, uno por fila, cada uno de una tabla distinta.
        filas.forEachIndexed { i, f ->
            val par = MEDIOS[i % MEDIOS.size]
            tabla(Rect(h0.right, f.top, h1.left, f.bottom), par.first, 5230 + i, vertical = true, pintada = par.second)
        }
        // Los dos largueros de los costados, de arriba abajo: el de la izquierda recibe la luz; el de la derecha, pintado de ladrillo.
        tabla(Rect(xL, canto, izq, abajo), mezcla(PINO_LUZ, PINO, 0.3f), 5240, vertical = true)
        tabla(Rect(der, canto, xR, abajo), mezcla(LADRILLO, PINO_OSC, 0.35f), 5241, vertical = true, pintada = true)
        var y = canto
        while (y < abajo) {
            val alto = minOf(w * 0.2f, abajo - y)
            val t = tn(Offset(xR, y + alto / 2))
            drawRect(t.s(0.3f), Offset(xR - w * 0.012f, y), Size(w * 0.012f, alto))
            if (esNoche) drawRect(t.b(0.3f), Offset(der, y), Size(1.2f * u, alto))
            y += w * 0.2f
        }

        // El rebaje de cada hueco: la sombra del canto por fuera y el filo de la tabla de abajo.
        d.huecos.forEach { r ->
            val t = tn(r.center)
            drawRect(t.s(0.55f), Offset(r.left - 1.6f * u, r.top - 1.6f * u), Size(r.width + 3.2f * u, r.height + 3.2f * u))
            drawRect(t.b(0.4f), Offset(r.left - 1.6f * u, r.bottom + 1.6f * u), Size(r.width + 3.2f * u, 1f * u))
        }
        // Los clavos de las uniones: dos en cada cruce de travesaño y larguero.
        for (i in 0..filas.size) {
            val yy = if (i == 0) filas[0].top - sep * 0.5f else if (i == filas.size) filas[i - 1].bottom + sep * 0.45f else filas[i - 1].bottom + sep * 0.5f
            listOf(xL + (izq - xL) * 0.5f, (h0.right + h1.left) / 2, der + (xR - der) * 0.5f).forEachIndexed { j, x ->
                val p = Offset(x + w * 0.004f * (azar(5250 + i, j) - 0.5f), yy)
                val t = tn(p)
                listOf(-0.3f, 0.3f).forEach { f -> clavo(p + Offset(sep * f * (if (j == 1) 1f else 0.5f), sep * 0.08f * (azar(5251 + i, j) - 0.5f)), 1.1f * u, t) }
            }
        }

        // Los parches de lata: uno en el larguero de la izquierda, otro que une dos tablas en un travesaño y otro abajo a la derecha.
        if (filas.size >= 2) {
            parche(Rect(xL + w * 0.004f, filas[1].top + filas[1].height * 0.25f, izq - w * 0.004f, filas[1].top + filas[1].height * 0.62f), 5260)
            parche(Rect(w * 0.62f, filas[0].bottom + sep * 0.1f, w * 0.75f, filas[1].top - sep * 0.1f), 5261)
        }
        if (filas.size >= 4) parche(Rect(der + w * 0.003f, filas[3].top + filas[3].height * 0.5f, xR - w * 0.004f, filas[3].top + filas[3].height * 0.82f), 5262)
        // Una junta a tope en el friso y otra en el faldón (las tablas no daban el largo), y un golpe en la esquina.
        listOf(Offset(w * 0.3f, canto) to (filas[0].top - canto), Offset(w * 0.68f, filas.last().bottom) to (abajo - filas.last().bottom)).forEach { (p, alto) ->
            val t = tn(p)
            drawRect(t.s(0.4f), p, Size(1.2f * u, alto))
            drawRect(t.b(0.35f), p + Offset(1.2f * u, 0f), Size(0.8f * u, alto))
        }
        // La cuenta de lo fiado, a lápiz en el friso: palotes de cinco en cinco.
        val py = canto + (filas[0].top - canto) * 0.3f
        val alto = (filas[0].top - canto) * 0.42f
        val tl = tn(Offset(w * 0.78f, py))
        val lapiz = tl.c(mezcla(TINTA, PINO_OSC, 0.3f)).copy(alpha = 0.6f)
        for (k in 0 until 13) {
            if (k % 5 == 4) drawLine(lapiz, Offset(w * (0.705f + 0.011f * (k - 4)) - w * 0.006f, py + alto * 0.8f), Offset(w * (0.705f + 0.011f * (k - 1)) + w * 0.006f, py + alto * 0.15f), 0.9f * u, StrokeCap.Round)
            else drawLine(lapiz, Offset(w * (0.705f + 0.011f * k) + w * 0.002f * (azar(5270, k) - 0.5f), py), Offset(w * (0.705f + 0.011f * k) + w * 0.003f * (azar(5271, k) - 0.5f), py + alto * (0.9f + 0.2f * azar(5272, k))), 0.9f * u, StrokeCap.Round)
        }
        val tg = tn(Offset(xL, abajo))
        drawPath(poligono(xL, abajo - w * 0.03f, xL + w * 0.016f, abajo, xL, abajo), tg.c(mezcla(PINO_LUZ, GACETA, 0.4f)))
        // El canto de abajo del faldón.
        raya(xL, xR, abajo - 1.6f * u, 1.6f * u, 0.4f, false)
    }

    /**
     * El sobre de trabajo: tres tablones vistos un poco desde arriba (sus juntas, un cerco de frasco, cortes de cuchillo y
     * rozaduras), el canto de frente aserrado, con lo que le queda de pintura azul, sus clavos y la esquina comida.
     */
    private fun DrawScope.sobre(w: Float, sup: Float, frente: Float, canto: Float) {
        val u = density
        val o = esNoche
        val xL = w * IZQ - w * 0.014f; val xR = w * DER + w * 0.014f
        val arriba = Rect(xL, sup, xR, frente)
        val madera = mezcla(PINO_LUZ, PINO, 0.35f)
        if (!o) drawRect(madera, arriba.topLeft, arriba.size)
        else drawRect(Brush.horizontalGradient(List(7) { i -> tn(Offset(xL + (xR - xL) * i / 6f, sup)).c(madera) }, xL, xR), arriba.topLeft, arriba.size)
        if (!o) drawRect(Brush.horizontalGradient(listOf(luzMueble(0.3f), Color.Transparent, sombraMueble(0.14f)), xL, xR), arriba.topLeft, arriba.size)
        vetas(arriba, (if (o) NOCHE else PINO_OSC).copy(alpha = 0.3f), 5300, 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        // La sombra de la pared al fondo y las juntas de los tres tablones.
        raya(xL, xR, sup, 1.6f * u, 0.3f, false)
        listOf(0.36f, 0.7f).forEach { f ->
            val y = sup + (frente - sup) * f
            raya(xL, xR, y, 0.9f * u, 0.32f, false); raya(xL, xR, y + 0.9f * u, 0.7f * u, 0.3f, true)
        }
        // El cerco de un frasco, unos cortes de cuchillo y una mancha.
        drawOval(tn(Offset(w * 0.64f, sup)).s(0.2f), Offset(w * 0.625f, sup + (frente - sup) * 0.42f), Size(w * 0.055f, (frente - sup) * 0.45f), style = Stroke(0.9f * u))
        for (k in 0 until 6) {
            val x = w * (0.05f + 0.88f * azar(5301, k))
            val y = sup + (frente - sup) * (0.3f + 0.5f * azar(5302, k))
            drawLine(tn(Offset(x, y)).s(0.28f), Offset(x, y), Offset(x + w * (0.012f + 0.02f * azar(5303, k)), y - (frente - sup) * 0.12f), 0.6f * u)
        }
        drawOval(tn(Offset(w * 0.25f, sup)).s(0.12f), Offset(w * 0.2f, sup + (frente - sup) * 0.5f), Size(w * 0.1f, (frente - sup) * 0.4f))
        // El canto de frente: el corte de la sierra, la pintura que queda y los clavos.
        val n = 8
        for (k in 0 until n) {
            val x0 = xL + (xR - xL) * k / n; val aw = (xR - xL) / n + 0.5f
            val tk = tn(Offset(x0 + aw / 2, frente))
            drawRect(tk.c(mezcla(PINO, PINO_OSC, 0.3f)), Offset(x0, frente), Size(aw, canto - frente))
            drawRect(tk.b(0.65f), Offset(x0, frente), Size(aw, 1.3f * u))
            drawRect(tk.b(0.2f), Offset(x0, frente + 1.3f * u), Size(aw, 2f * u))
        }
        clipRect(xL, frente, xR, canto) {
            // Las marcas de la sierra, al sesgo.
            var x = xL
            var k = 0
            while (x < xR) { drawLine(tn(Offset(x, frente)).s(0.14f), Offset(x, frente), Offset(x - (canto - frente) * 0.35f, canto), 0.6f * u); x += w * (0.008f + 0.012f * azar(5304, k)); k++ }
            // Lo que queda de pintura azul, a trozos.
            listOf(0.03f to 0.1f, 0.15f to 0.05f, 0.22f to 0.12f, 0.37f to 0.03f, 0.8f to 0.07f, 0.9f to 0.04f).forEachIndexed { j, (fx, fw) ->
                val azul = tn(Offset(w * (fx + fw / 2), frente)).c(mezcla(MARINO, PINO, 0.15f)).copy(alpha = 0.62f)
                val tiras = 6
                val alto = (canto - frente - 2.6f * u) / tiras
                for (s in 0 until tiras) {
                    if (azar(5305 + j, s) < 0.2f) continue
                    val x0 = w * (fx + 0.022f * (azar(5306 + j, s) - 0.5f))
                    val x1 = w * (fx + fw + 0.03f * (azar(5307 + j, s) - 0.5f))
                    drawRect(azul, Offset(x0, frente + 1.3f * u + alto * s), Size(x1 - x0, alto + 0.5f))
                }
            }
        }
        raya(xL, xR, canto - 1.2f * u, 1.2f * u, 0.38f, false)
        listOf(0.06f, 0.31f, 0.69f, 0.94f).forEach { fx -> listOf(-0.006f, 0.006f).forEach { dx -> val p = Offset(w * (fx + dx), (frente + canto) / 2 + 1f * u); clavo(p, 1.2f * u, tn(p)) } }
        // La esquina comida, a la derecha: la madera viva que asoma.
        val tg = tn(Offset(xR, frente))
        drawPath(poligono(xR, frente, xR - w * 0.03f, frente, xR - w * 0.01f, frente + (canto - frente) * 0.6f, xR, canto), tg.c(mezcla(PINO_LUZ, GACETA, 0.35f)))
        drawPath(Path().apply { moveTo(xR - w * 0.03f, frente); lineTo(xR - w * 0.01f, frente + (canto - frente) * 0.6f); lineTo(xR, canto) }, tg.s(0.4f), style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- lo de encima

    /**
     * El libro de cuentas, de pie contra la pared y un poco ladeado: la tapa de tela azul con el lomo y las puntas de cuero, la
     * etiqueta de papel, el canto de las hojas y el lápiz atado con un hilo, que cuelga por delante.
     */
    private fun DrawScope.libro(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val lw = w * 0.104f; val lh = w * 0.138f
        val r = Rect(pie.x, pie.y - lh, pie.x + lw, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(r.center.x)
        val dv = Offset(w * 0.013f, -w * 0.006f)
        contacto(Offset(pie.x + lw * 0.55f, pie.y), lw * 1.2f, 3.5f * u, 0.36f)
        rotate(4.5f, pie) {
            sombraEnPared(poligono(r.left, r.bottom, r.left, r.top, r.left + dv.x, r.top + dv.y, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right, r.bottom), r.center, sup + 2f * u, 0.22f)
            // El canto de las hojas: arriba y al costado, entre las dos tapas.
            val lado = poligono(r.right, r.top, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right, r.bottom)
            val arriba = poligono(r.left, r.top, r.left + dv.x, r.top + dv.y, r.right + dv.x, r.top + dv.y, r.right, r.top)
            drawPath(lado, t.c(mezcla(GACETA, CUERDA, 0.25f)))
            drawPath(arriba, t.c(GACETA))
            for (j in 1..4) {
                val f = j / 5f
                drawLine(t.s(0.18f), Offset(r.right + dv.x * f, r.top + dv.y * f), Offset(r.right + dv.x * f, r.bottom + dv.y * f), 0.5f * u)
            }
            drawPath(poligono(r.right + dv.x * 0.82f, r.top + dv.y * 0.82f, r.right + dv.x, r.top + dv.y, r.right + dv.x, r.bottom + dv.y, r.right + dv.x * 0.82f, r.bottom + dv.y * 0.82f), t.c(mezcla(MARINO, TINTA, 0.3f)))
            if (der) drawPath(lado, t.b(0.25f)) else drawPath(lado, t.s(0.2f))
            // La tapa de tela, con su desgaste.
            drawRect(t.c(MARINO), r.topLeft, r.size)
            clipRect(r.left, r.top, r.right, r.bottom) {
                var x = r.left
                while (x < r.right) { drawLine(t.s(0.1f), Offset(x, r.top), Offset(x, r.bottom), 0.6f * u); x += 3f * u }
                pinceladas(r, t.c(mezcla(MARINO, GACETA, 0.4f)), 5400, 7, u, 0.2f)
                if (der) drawRect(t.b(0.16f), Offset(r.right - lw * 0.2f, r.top), Size(lw * 0.2f, lh)) else { drawRect(t.b(0.14f), Offset(r.left + lw * 0.2f, r.top), Size(lw * 0.16f, lh)); drawRect(t.s(0.18f), Offset(r.right - lw * 0.14f, r.top), Size(lw * 0.14f, lh)) }
            }
            // El lomo de cuero con sus nervios y las dos puntas.
            val cuero = mezcla(LADRILLO, TABLON, 0.45f)
            val lomo = Rect(r.left, r.top, r.left + lw * 0.2f, r.bottom)
            drawRect(t.c(cuero), lomo.topLeft, lomo.size)
            drawRect(t.b(0.25f), lomo.topLeft, Size(lomo.width * 0.3f, lh))
            drawRect(t.s(0.3f), Offset(lomo.right - 1.2f * u, lomo.top), Size(1.2f * u, lh))
            listOf(0.14f, 0.38f, 0.62f, 0.86f).forEach { fy ->
                drawRect(t.b(0.35f), Offset(lomo.left, lomo.top + lh * fy), Size(lomo.width, 1.1f * u))
                drawRect(t.s(0.3f), Offset(lomo.left, lomo.top + lh * fy + 1.1f * u), Size(lomo.width, 1f * u))
            }
            val pc = lw * 0.2f
            drawPath(poligono(r.right, r.top, r.right - pc, r.top, r.right, r.top + pc), t.c(cuero))
            drawPath(poligono(r.right, r.bottom, r.right - pc, r.bottom, r.right, r.bottom - pc), t.c(cuero))
            // La etiqueta de papel, con su doble filete y dos renglones a pluma.
            val et = Rect(r.left + lw * 0.32f, r.top + lh * 0.16f, r.right - lw * 0.12f, r.top + lh * 0.42f)
            drawRect(t.s(0.25f), Offset(et.left + 1f * u, et.top + 1.2f * u), et.size)
            drawRect(t.c(GACETA), et.topLeft, et.size)
            drawRect(t.c(TINTA).copy(alpha = 0.55f), Offset(et.left + 1.6f * u, et.top + 1.6f * u), Size(et.width - 3.2f * u, et.height - 3.2f * u), style = Stroke(0.6f * u))
            listOf(0.42f, 0.66f).forEachIndexed { j, fy -> drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(et.left + et.width * 0.16f, et.top + et.height * fy), Offset(et.right - et.width * (0.16f + 0.2f * j), et.top + et.height * fy), 0.7f * u) }
            drawRect(t.b(0.4f), r.topLeft, Size(lw, 1f * u))
            // El lápiz atado: el hilo sale del lomo y el lápiz cuelga por delante de la tapa.
            val h0 = Offset(r.left + lw * 0.1f, r.top)
            val l0 = Offset(r.left + lw * 0.42f, r.top + lh * 0.6f)
            val l1 = Offset(r.left + lw * 0.74f, r.top + lh * 0.95f)
            val hilo = Path().apply { moveTo(h0.x, h0.y); cubicTo(h0.x - lw * 0.05f, h0.y + lh * 0.3f, l0.x - lw * 0.2f, l0.y - lh * 0.12f, l0.x, l0.y) }
            drawPath(hilo, t.c(CUERDA), style = Stroke(0.9f * u))
            translate(2f * u, 2.4f * u) { drawLine(t.s(0.3f), l0, l1, 3f * u, StrokeCap.Round) }
            drawLine(t.c(MAIZ), l0, l1, 3f * u)
            drawLine(t.b(0.45f), l0 + Offset(0.8f * u, -0.8f * u), l1 + Offset(0.8f * u, -0.8f * u), 0.8f * u)
            val punta = l1 + (l1 - l0) * 0.16f
            drawLine(t.c(mezcla(PINO_LUZ, GACETA, 0.3f)), l1, punta, 2.6f * u)
            drawLine(t.c(TINTA), l1 + (l1 - l0) * 0.11f, punta + (l1 - l0) * 0.03f, 1.4f * u, StrokeCap.Round)
            drawCircle(t.c(CUERDA), 1.4f * u, l0)
        }
    }

    /** El contorno de un saco de pie, panzudo: [boca] es el ancho de arriba, en fracción del ancho [a]. */
    private fun saco(cx: Float, pie: Float, a: Float, h: Float, boca: Float): Path = Path().apply {
        val top = pie - h
        moveTo(cx - a * boca / 2, top)
        cubicTo(cx - a * (boca / 2 + 0.08f), top + h * 0.06f, cx - a * 0.6f, top + h * 0.3f, cx - a * 0.55f, pie - h * 0.25f)
        cubicTo(cx - a * 0.53f, pie - h * 0.06f, cx - a * 0.5f, pie, cx - a * 0.4f, pie)
        lineTo(cx + a * 0.4f, pie)
        cubicTo(cx + a * 0.5f, pie, cx + a * 0.53f, pie - h * 0.06f, cx + a * 0.55f, pie - h * 0.25f)
        cubicTo(cx + a * 0.6f, top + h * 0.3f, cx + a * (boca / 2 + 0.08f), top + h * 0.06f, cx + a * boca / 2, top)
        close()
    }

    /** La tela de un saco dentro de su contorno [f]: la trama, el volumen, los pliegues que caen de la boca y los de la panza. */
    private fun DrawScope.telaSaco(f: Path, r: Rect, t: Tono, col: Color, der: Boolean, semilla: Int, trama: Float = 0.09f) {
        val u = density
        drawPath(f, t.c(col))
        clipPath(f) {
            var x = r.left - r.height * 0.2f
            while (x < r.right) { drawLine(t.s(trama), Offset(x, r.top), Offset(x + r.height * 0.2f, r.bottom), 0.6f * u); x += 3.4f * u }
            var y = r.top
            while (y < r.bottom) { drawLine(t.s(trama), Offset(r.left, y), Offset(r.right, y + r.width * 0.04f), 0.6f * u); y += 3.4f * u }
            cilindro(r, t, 1.15f, der)
            // Los pliegues: unos caen de la boca, otros suben del asiento.
            for (k in 0 until 5) {
                val x0 = r.left + r.width * (0.2f + 0.6f * (k + azar(semilla, k) * 0.6f) / 5f)
                val p = Path().apply { moveTo(x0, r.top); cubicTo(x0 + r.width * 0.03f, r.top + r.height * 0.2f, x0 - r.width * 0.05f * (azar(semilla, k + 5) - 0.3f), r.top + r.height * 0.35f, x0 + r.width * 0.04f * (azar(semilla, k + 9) - 0.5f), r.top + r.height * (0.4f + 0.25f * azar(semilla, k + 13))) }
                drawPath(p, t.s(0.2f), style = Stroke(1f * u))
                translate(1.2f * u, 0f) { drawPath(p, t.b(0.22f), style = Stroke(0.8f * u)) }
            }
            drawPath(Path().apply { moveTo(r.left + r.width * 0.08f, r.bottom - r.height * 0.2f); cubicTo(r.left + r.width * 0.3f, r.bottom - r.height * 0.08f, r.left + r.width * 0.55f, r.bottom - r.height * 0.14f, r.right - r.width * 0.1f, r.bottom - r.height * 0.26f) }, t.s(0.18f), style = Stroke(1f * u))
            drawRect(t.s(0.2f), Offset(r.left, r.bottom - r.height * 0.08f), Size(r.width, r.height * 0.08f))
        }
    }

    /**
     * Los sacos de semillas: detrás, uno cerrado, atado por el cuello con su moño, cortado por el borde; delante, otro abierto,
     * con la boca enrollada, el maíz a la vista, la pala de lata clavada en el grano y una espiga estarcida en azul.
     */
    private fun DrawScope.sacos(pieA: Float, pieF: Float, sup: Float) {
        val u = density
        val w = anchoW
        // ---- el de atrás, cerrado
        val cx = w * 0.955f; val a = w * 0.17f; val h = w * 0.19f
        val r = Rect(cx - a * 0.6f, pieA - h, cx + a * 0.6f, pieA)
        val t = tn(r.center)
        val der = luzDerecha(cx)
        val f = saco(cx, pieA, a, h, 0.2f)
        val mono = poligono(cx - a * 0.1f, r.top + h * 0.02f, cx - a * 0.24f, r.top - h * 0.16f, cx - a * 0.05f, r.top - h * 0.12f, cx + a * 0.04f, r.top - h * 0.2f, cx + a * 0.2f, r.top - h * 0.13f, cx + a * 0.1f, r.top + h * 0.02f)
        sombraEnPared(Path().apply { addPath(f); addPath(mono) }, r.center, sup, 0.24f)
        contacto(Offset(cx, pieA), a * 1.2f, 4f * u, 0.4f)
        drawPath(mono, t.c(mezcla(ARPILLERA, PINO_OSC, 0.2f)))
        listOf(-0.12f, 0.02f, 0.12f).forEach { fx -> drawLine(t.s(0.25f), Offset(cx + a * fx * 0.5f, r.top), Offset(cx + a * fx * 1.6f, r.top - h * 0.14f), 0.8f * u) }
        telaSaco(f, r, t, ARPILLERA, der, 5410)
        // El cordel del cuello, con sus vueltas y un cabo suelto.
        val cu = Rect(cx - a * 0.13f, r.top - h * 0.01f, cx + a * 0.13f, r.top + h * 0.05f)
        drawRoundRect(t.c(CUERDA), cu.topLeft, cu.size, CornerRadius(cu.height / 2))
        drawLine(t.s(0.3f), Offset(cu.left, cu.center.y), Offset(cu.right, cu.center.y + 1f * u), 0.7f * u)
        drawLine(t.b(0.4f), Offset(cu.left + 1f * u, cu.top + 1f * u), Offset(cu.right - 1f * u, cu.top + 1f * u), 0.7f * u)
        drawPath(Path().apply { moveTo(cu.left, cu.center.y); cubicTo(cu.left - a * 0.1f, cu.center.y + h * 0.04f, cu.left - a * 0.1f, cu.center.y + h * 0.12f, cu.left - a * 0.04f, cu.center.y + h * 0.17f) }, t.c(CUERDA), style = Stroke(1.2f * u, cap = StrokeCap.Round))

        // ---- el de delante, abierto
        val cx2 = w * 0.878f; val a2 = w * 0.135f; val h2 = w * 0.118f
        val r2 = Rect(cx2 - a2 * 0.6f, pieF - h2, cx2 + a2 * 0.6f, pieF)
        val t2 = tn(r2.center)
        val der2 = luzDerecha(cx2)
        val f2 = saco(cx2, pieF, a2, h2, 0.86f)
        translate(w * 0.02f, w * 0.006f) { drawPath(f2, t2.s(0.22f)) }
        contacto(Offset(cx2 + a2 * 0.05f, pieF), a2 * 1.25f, 4.5f * u, 0.42f)
        // La boca: por detrás, el borde enrollado del fondo; dentro, el maíz.
        val boca = Rect(cx2 - a2 * 0.46f, r2.top - h2 * 0.17f, cx2 + a2 * 0.46f, r2.top + h2 * 0.11f)
        drawOval(t2.c(mezcla(ARPILLERA, PINO_OSC, 0.35f)), Offset(boca.left - a2 * 0.03f, boca.top - h2 * 0.05f), Size(boca.width + a2 * 0.06f, boca.height + h2 * 0.05f))
        drawOval(t2.c(mezcla(MAIZ, OXIDO, 0.12f)), boca.topLeft, boca.size)
        clipPath(Path().apply { addOval(boca) }) {
            for (k in 0 until 26) {
                val p = Offset(boca.left + boca.width * azar(5420, k), boca.top + boca.height * azar(5421, k))
                drawOval(t2.c(if (k % 4 == 0) mezcla(MAIZ, OXIDO, 0.45f) else mezcla(MAIZ, GACETA, 0.25f * (k % 3))), Offset(p.x, p.y), Size(3.2f * u, 2.4f * u))
            }
            drawRect(t2.s(0.22f), Offset(boca.left, boca.top), Size(boca.width, boca.height * 0.28f))
        }
        // La pala de lata, clavada en el grano: el cucharón y el mango de madera.
        val pc = Offset(cx2 + a2 * 0.1f, boca.center.y)
        rotate(-28f, pc) {
            val cuch = Path().apply {
                moveTo(pc.x - a2 * 0.13f, pc.y); lineTo(pc.x - a2 * 0.15f, pc.y - h2 * 0.36f)
                cubicTo(pc.x - a2 * 0.15f, pc.y - h2 * 0.5f, pc.x + a2 * 0.15f, pc.y - h2 * 0.5f, pc.x + a2 * 0.15f, pc.y - h2 * 0.36f)
                lineTo(pc.x + a2 * 0.13f, pc.y); close()
            }
            translate(2f * u, 2f * u) { drawPath(cuch, t2.s(0.25f)) }
            drawPath(cuch, t2.c(LATA))
            clipPath(cuch) { cilindro(Rect(pc.x - a2 * 0.15f, pc.y - h2 * 0.5f, pc.x + a2 * 0.15f, pc.y), t2, 1.5f, der2) }
            val mango = Rect(pc.x - a2 * 0.04f, pc.y - h2 * 0.92f, pc.x + a2 * 0.04f, pc.y - h2 * 0.44f)
            drawRoundRect(t2.c(mezcla(PINO, TABLON, 0.35f)), mango.topLeft, mango.size, CornerRadius(a2 * 0.04f))
            clipRect(mango.left, mango.top, mango.right, mango.bottom) { cilindro(mango, t2, 1.3f, der2) }
            drawRect(t2.c(LATA), Offset(mango.left - 1f * u, mango.bottom - h2 * 0.06f), Size(mango.width + 2f * u, h2 * 0.06f))
            destello(Offset(pc.x - a2 * 0.07f, pc.y - h2 * 0.36f), 1.2f * u, t2)
        }
        telaSaco(f2, r2, t2, mezcla(ARPILLERA, CUERDA, 0.35f), der2, 5430)
        // La espiga estarcida en azul, gastada.
        val ec = Offset(cx2 - a2 * 0.04f, r2.top + h2 * 0.6f)
        val azul = t2.c(MARINO).copy(alpha = 0.6f)
        drawLine(azul, Offset(ec.x, ec.y - h2 * 0.26f), Offset(ec.x, ec.y + h2 * 0.28f), 1.6f * u)
        for (k in 0 until 4) listOf(-1f, 1f).forEach { s ->
            val y = ec.y - h2 * 0.2f + h2 * 0.1f * k
            drawLine(azul, Offset(ec.x, y + h2 * 0.08f), Offset(ec.x + s * a2 * 0.11f, y - h2 * 0.02f), 2.4f * u, StrokeCap.Round)
        }
        // El borde enrollado de delante, con sus vueltas.
        val rollo = Rect(cx2 - a2 * 0.5f, r2.top - h2 * 0.03f, cx2 + a2 * 0.5f, r2.top + h2 * 0.17f)
        drawRoundRect(t2.s(0.3f), Offset(rollo.left, rollo.top + 2f * u), rollo.size, CornerRadius(rollo.height / 2))
        drawRoundRect(t2.c(mezcla(ARPILLERA, CUERDA, 0.6f)), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        clipPath(Path().apply { addRoundRect(RoundRect(rollo, CornerRadius(rollo.height / 2))) }) {
            var x = rollo.left
            while (x < rollo.right) { drawLine(t2.s(0.18f), Offset(x, rollo.bottom), Offset(x + rollo.height * 0.6f, rollo.top), 0.7f * u); x += 4f * u }
            drawRect(t2.b(0.3f), rollo.topLeft, Size(rollo.width, rollo.height * 0.3f))
            drawRect(t2.s(0.2f), Offset(rollo.left, rollo.bottom - rollo.height * 0.3f), Size(rollo.width, rollo.height * 0.3f))
        }
        // Unos granos caídos en el sobre.
        listOf(-0.62f to 0.02f, -0.74f to -0.05f, -0.55f to -0.1f).forEach { (fx, fy) ->
            val p = Offset(cx2 + a2 * fx, pieF + w * fy * 0.2f)
            drawOval(t2.s(0.3f), Offset(p.x + 0.6f * u, p.y + 0.8f * u), Size(3.4f * u, 2.4f * u))
            drawOval(t2.c(MAIZ), p, Size(3.4f * u, 2.4f * u))
        }
    }

    /** Una tuerca vista de lado: sus tres caras (la del medio, de frente), la boca de arriba con su agujero y una mota de óxido. */
    private fun DrawScope.tuerca(cx: Float, base: Float, ancho: Float, alto: Float, t: Tono, der: Boolean, semilla: Int) {
        val u = density
        val r = Rect(cx - ancho / 2, base - alto, cx + ancho / 2, base)
        drawRect(t.c(HIERRO), r.topLeft, r.size)
        drawRect(if (der) t.s(0.28f) else t.b(0.3f), r.topLeft, Size(ancho * 0.27f, alto))
        drawRect(if (der) t.b(0.3f) else t.s(0.3f), Offset(r.right - ancho * 0.27f, r.top), Size(ancho * 0.27f, alto))
        drawRect(t.b(0.16f), Offset(r.left + ancho * 0.27f, r.top), Size(ancho * 0.46f, alto * 0.4f))
        drawOval(t.c(OXIDO).copy(alpha = 0.6f), Offset(r.left + ancho * (0.3f + 0.3f * azar(semilla, 1)), r.top + alto * 0.45f), Size(ancho * 0.22f, alto * 0.4f))
        drawRect(t.s(0.35f), Offset(r.left, r.bottom - 0.9f * u), Size(ancho, 0.9f * u))
        val boca = Rect(r.left, r.top - alto * 0.22f, r.right, r.top + alto * 0.22f)
        drawOval(t.c(mezcla(HIERRO, LATA, 0.45f)), boca.topLeft, boca.size)
        drawOval(t.c(mezcla(HIERRO, TINTA, 0.5f)), Offset(cx - ancho * 0.22f, boca.top + boca.height * 0.25f), Size(ancho * 0.44f, boca.height * 0.5f))
        drawArc(t.b(0.5f), 180f, 150f, false, boca.topLeft, boca.size, style = Stroke(0.7f * u))
    }

    /**
     * La balanza de platillos, el objeto principal: la caja de madera con su cajoncito, la columna de latón torneada, el astil
     * ladeado con su fiel, las cadenas y los dos platillos; en el de la izquierda, más alto, un puñado de maíz; en el de la
     * derecha, las pesas: tuercas apiladas y una pieza de máquina.
     */
    private fun DrawScope.balanza(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val der = luzDerecha(pie.x)
        val bw = w * 0.2f; val bh = w * 0.036f; val prof = w * 0.013f
        val caja = Rect(pie.x - bw / 2, pie.y - bh, pie.x + bw / 2, pie.y)
        val tapaY = caja.top - prof
        val pivote = Offset(pie.x, tapaY + prof * 0.5f - w * 0.228f)
        val giro = 7f
        val brazo = w * 0.168f
        val eI = girado(Offset(pivote.x - brazo, pivote.y), pivote, giro)
        val eD = girado(Offset(pivote.x + brazo, pivote.y), pivote, giro)
        val cuelga = w * 0.098f
        val rx = w * 0.062f; val ry = w * 0.011f; val hondoP = w * 0.027f
        val pI = Offset(eI.x, eI.y + cuelga); val pD = Offset(eD.x, eD.y + cuelga)
        val t = tnLaton(Offset(pie.x, pivote.y + w * 0.1f))
        val tI = tnLaton(pI); val tD = tnLaton(pD)
        val laton = t.c(LATON)
        fun cuenco(p: Offset): Path = Path().apply {
            moveTo(p.x - rx, p.y)
            cubicTo(p.x - rx * 0.9f, p.y + hondoP * 1.25f, p.x + rx * 0.9f, p.y + hondoP * 1.25f, p.x + rx, p.y)
            cubicTo(p.x + rx, p.y - ry * 1.3f, p.x - rx, p.y - ry * 1.3f, p.x - rx, p.y)
            close()
        }
        val col = Rect(pie.x - w * 0.0075f, pivote.y, pie.x + w * 0.0075f, tapaY + prof * 0.5f)
        val astil = poligono(pivote.x - brazo, pivote.y - w * 0.004f, pivote.x - w * 0.03f, pivote.y - w * 0.0105f, pivote.x, pivote.y - w * 0.013f, pivote.x + w * 0.03f, pivote.y - w * 0.0105f,
            pivote.x + brazo, pivote.y - w * 0.004f, pivote.x + brazo, pivote.y + w * 0.004f, pivote.x + w * 0.03f, pivote.y + w * 0.0085f, pivote.x - w * 0.03f, pivote.y + w * 0.0085f, pivote.x - brazo, pivote.y + w * 0.004f)
        val astilGirado = Path().apply {
            listOf(Offset(pivote.x - brazo, pivote.y - w * 0.004f), Offset(pivote.x, pivote.y - w * 0.013f), Offset(pivote.x + brazo, pivote.y - w * 0.004f), Offset(pivote.x + brazo, pivote.y + w * 0.004f),
                Offset(pivote.x, pivote.y + w * 0.0085f), Offset(pivote.x - brazo, pivote.y + w * 0.004f)).map { girado(it, pivote, giro) }.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
            close()
        }
        // ---- su sombra en la pared: la columna, el astil, los platillos y las cadenas
        val silueta = Path().apply { addRect(col); addPath(astilGirado); addPath(cuenco(pI)); addPath(cuenco(pD)); addRect(Rect(caja.left, tapaY, caja.right, caja.bottom)) }
        sombraEnPared(silueta, Offset(pie.x, pivote.y + w * 0.12f), sup, 0.24f)
        listOf(eI to pI, eD to pD).forEach { (e, p) ->
            val k = luz(e / w)
            val corre = if (!esNoche) Offset(w * 0.022f, w * 0.012f) else Offset(w * 0.04f * ((e.x / w - FOCO.x).coerceIn(-0.3f, 0.3f) / 0.3f), w * 0.03f * ((e.y / w - FOCO.y).coerceIn(-0.3f, 0.3f) / 0.3f))
            if (!esNoche || k > 0.12f) translate(corre.x, corre.y) {
                listOf(-0.92f, 0.92f).forEach { s -> drawLine(if (esNoche) NOCHE.copy(alpha = (0.5f * k).coerceAtMost(0.4f)) else sombraMueble(0.18f), e, Offset(p.x + rx * s, p.y), 1f * u) }
            }
        }
        contacto(Offset(pie.x + bw * 0.04f, pie.y), bw * 1.15f, 4.5f * u, 0.42f)

        // ---- la caja de madera: la tapa vista desde arriba, el frente con su cajoncito y el tirador
        val madera = mezcla(TABLON, PINO_OSC, 0.35f)
        drawPath(poligono(caja.left, caja.top, caja.left + prof * 0.8f, tapaY, caja.right - prof * 0.8f, tapaY, caja.right, caja.top), t.c(mezcla(madera, PINO, 0.35f)))
        drawLine(t.b(0.45f), Offset(caja.left + prof * 0.8f, tapaY), Offset(caja.right - prof * 0.8f, tapaY), 0.8f * u)
        drawRect(t.c(madera), caja.topLeft, caja.size)
        vetas(caja, (if (esNoche) NOCHE else TINTA).copy(alpha = 0.22f), 5440, 0.7f, grosor = 0.5f, ondulacion = 0.5f)
        drawRect(t.b(0.5f), caja.topLeft, Size(bw, 1.1f * u))
        if (der) drawRect(t.b(0.2f), Offset(caja.right - bw * 0.14f, caja.top), Size(bw * 0.14f, bh)) else { drawRect(t.b(0.16f), caja.topLeft, Size(bw * 0.12f, bh)); drawRect(t.s(0.22f), Offset(caja.right - bw * 0.12f, caja.top), Size(bw * 0.12f, bh)) }
        drawRect(t.s(0.35f), Offset(caja.left, caja.bottom - 1.2f * u), Size(bw, 1.2f * u))
        val cj = Rect(caja.left + bw * 0.2f, caja.top + bh * 0.2f, caja.right - bw * 0.2f, caja.bottom - bh * 0.16f)
        drawRect(t.s(0.4f), cj.topLeft, cj.size, style = Stroke(1f * u))
        drawRect(t.b(0.25f), Offset(cj.left + 1f * u, cj.top + 1f * u), Size(cj.width - 2f * u, 0.8f * u))
        drawCircle(t.s(0.35f), bh * 0.13f, cj.center + Offset(1f * u, 1.3f * u))
        drawCircle(laton, bh * 0.13f, cj.center)
        destello(cj.center - Offset(bh * 0.04f, bh * 0.04f), 0.8f * u, t)
        // Las cantoneras de latón.
        listOf(caja.left, caja.right - bw * 0.05f).forEach { x ->
            drawRect(laton, Offset(x, caja.top), Size(bw * 0.05f, bh * 0.3f)); drawRect(laton, Offset(x, caja.bottom - bh * 0.3f), Size(bw * 0.05f, bh * 0.3f))
            drawRect(t.b(0.5f), Offset(x, caja.top), Size(bw * 0.05f, 0.8f * u))
        }

        // ---- la columna torneada: el pie acampanado, el fuste con sus anillos, la horquilla y el remate
        val pieCol = Path().apply {
            val y0 = tapaY + prof * 0.55f
            moveTo(pie.x - w * 0.03f, y0)
            cubicTo(pie.x - w * 0.026f, y0 - w * 0.018f, pie.x - w * 0.009f, y0 - w * 0.016f, pie.x - w * 0.0075f, y0 - w * 0.04f)
            lineTo(pie.x + w * 0.0075f, y0 - w * 0.04f)
            cubicTo(pie.x + w * 0.009f, y0 - w * 0.016f, pie.x + w * 0.026f, y0 - w * 0.018f, pie.x + w * 0.03f, y0)
            close()
        }
        drawOval(t.s(0.35f), Offset(pie.x - w * 0.03f, tapaY + prof * 0.4f), Size(w * 0.072f, prof * 0.5f))
        drawPath(pieCol, laton)
        clipPath(pieCol) { cilindro(Rect(pie.x - w * 0.03f, tapaY - w * 0.04f, pie.x + w * 0.03f, tapaY + prof), t, 1.5f, der) }
        drawRect(laton, col.topLeft, col.size)
        clipRect(col.left, col.top, col.right, col.bottom) { cilindro(col, t, 1.6f, der) }
        listOf(0.3f, 0.62f, 0.66f).forEachIndexed { i, f ->
            val y = col.top + col.height * f
            val an = Rect(pie.x - w * (if (i == 0) 0.013f else 0.011f), y - w * 0.005f, pie.x + w * (if (i == 0) 0.013f else 0.011f), y + w * 0.005f)
            drawOval(laton, an.topLeft, an.size)
            clipPath(Path().apply { addOval(an) }) { cilindro(an, t, 1.6f, der) }
            drawRect(t.s(0.3f), Offset(col.left, an.bottom - 0.4f * u), Size(col.width, 1.2f * u))
        }
        // La placa del fiel, fija en la columna.
        val placaF = Path().apply { moveTo(pie.x, pivote.y + w * 0.02f); lineTo(pie.x - w * 0.02f, pivote.y + w * 0.062f); cubicTo(pie.x - w * 0.01f, pivote.y + w * 0.07f, pie.x + w * 0.01f, pivote.y + w * 0.07f, pie.x + w * 0.02f, pivote.y + w * 0.062f); close() }
        drawPath(placaF, t.c(mezcla(LATON, GACETA, 0.35f)))
        for (k in -2..2) drawLine(t.c(TINTA).copy(alpha = 0.6f), Offset(pie.x + w * 0.0075f * k, pivote.y + w * 0.056f), Offset(pie.x + w * 0.0085f * k, pivote.y + w * 0.064f), 0.6f * u)

        // ---- los platillos, de atrás adelante: lo de dentro, lo que llevan, y el cuenco con su brillo
        listOf(Triple(eI, pI, tI), Triple(eD, pD, tD)).forEachIndexed { i, (e, p, tp) ->
            val lat = tp.c(LATON)
            // Las cadenas de atrás y el gancho.
            drawLine(tp.c(mezcla(LATON, TINTA, 0.35f)), e, Offset(p.x, p.y - ry * 0.8f), 0.8f * u)
            // El interior del platillo.
            drawOval(tp.c(mezcla(LATON, TABLON, 0.5f)), Offset(p.x - rx, p.y - ry), Size(rx * 2, ry * 2))
            drawOval(tp.s(0.25f), Offset(p.x - rx, p.y - ry), Size(rx * 2, ry * 1.1f))
            if (i == 0) {
                // El maíz: un montón con sus granos.
                val monton = Path().apply { moveTo(p.x - rx * 0.62f, p.y + ry * 0.2f); cubicTo(p.x - rx * 0.4f, p.y - hondoP * 0.75f, p.x + rx * 0.35f, p.y - hondoP * 0.85f, p.x + rx * 0.6f, p.y + ry * 0.2f); close() }
                drawPath(monton, tp.c(mezcla(MAIZ, OXIDO, 0.1f)))
                clipPath(monton) {
                    for (k in 0 until 18) drawOval(tp.c(if (k % 4 == 0) mezcla(MAIZ, OXIDO, 0.4f) else mezcla(MAIZ, GACETA, 0.3f)), Offset(p.x + rx * 1.2f * (azar(5450, k) - 0.5f), p.y - hondoP * 0.8f * azar(5451, k)), Size(3f * u, 2.3f * u))
                    drawRect(tp.s(0.2f), Offset(if (der) p.x - rx else p.x + rx * 0.15f, p.y - hondoP), Size(rx * 0.85f, hondoP * 1.3f))
                }
            } else {
                // Las pesas: dos tuercas apiladas, una grande suelta y un piñón apoyado.
                tuerca(p.x - rx * 0.3f, p.y + ry * 0.3f, rx * 0.56f, hondoP * 0.5f, tp, der, 5460)
                tuerca(p.x - rx * 0.3f, p.y + ry * 0.3f - hondoP * 0.5f, rx * 0.44f, hondoP * 0.42f, tp, der, 5461)
                tuerca(p.x + rx * 0.36f, p.y + ry * 0.4f, rx * 0.5f, hondoP * 0.56f, tp, der, 5462)
                val pc = Offset(p.x + rx * 0.08f, p.y - hondoP * 0.42f)
                for (k in 0 until 8) { val a = k * 0.785f; drawLine(tp.c(HIERRO), pc, pc + Offset(rx * 0.24f * cos(a), rx * 0.24f * sin(a)), 2.2f * u) }
                drawCircle(tp.c(mezcla(HIERRO, LATA, 0.3f)), rx * 0.17f, pc)
                drawCircle(tp.c(mezcla(HIERRO, TINTA, 0.5f)), rx * 0.06f, pc)
                drawArc(tp.b(0.5f), 170f, 110f, false, Offset(pc.x - rx * 0.17f, pc.y - rx * 0.17f), Size(rx * 0.34f, rx * 0.34f), style = Stroke(0.7f * u))
            }
            // El cuenco por delante.
            val cu = Path().apply {
                moveTo(p.x - rx, p.y)
                cubicTo(p.x - rx * 0.9f, p.y + hondoP * 1.25f, p.x + rx * 0.9f, p.y + hondoP * 1.25f, p.x + rx, p.y)
                cubicTo(p.x + rx, p.y + ry * 1.3f, p.x - rx, p.y + ry * 1.3f, p.x - rx, p.y)
                close()
            }
            drawPath(cu, lat)
            clipPath(cu) {
                cilindro(Rect(p.x - rx, p.y, p.x + rx, p.y + hondoP), tp, 1.5f, der)
                drawRect(tp.s(0.25f), Offset(p.x - rx, p.y + hondoP * 0.62f), Size(rx * 2, hondoP * 0.5f))
                // Las abolladuras y el verdín del uso.
                drawOval(tp.c(mezcla(LATON, SALVIA, 0.5f)).copy(alpha = 0.5f), Offset(p.x + rx * 0.2f, p.y + hondoP * 0.3f), Size(rx * 0.3f, hondoP * 0.3f))
            }
            drawArc(tp.b(0.75f), 20f, 140f, false, Offset(p.x - rx, p.y - ry), Size(rx * 2, ry * 2), style = Stroke(1f * u))
            drawArc(tp.c(mezcla(LATON, GACETA, 0.3f)), 180f, 180f, false, Offset(p.x - rx, p.y - ry), Size(rx * 2, ry * 2), style = Stroke(0.9f * u))
            destello(Offset(if (der) p.x + rx * 0.55f else p.x - rx * 0.6f, p.y + hondoP * 0.3f), 1.3f * u, tp)
            // Las cadenas de delante, eslabón por eslabón, y la argolla.
            listOf(-0.94f, 0.94f).forEach { s ->
                val q = Offset(p.x + rx * s, p.y)
                drawLine(tp.c(mezcla(LATON, TINTA, 0.2f)), e, q, 1f * u)
                for (k in 1..9) { val c = e + (q - e) * (k / 10f); drawCircle(tp.c(mezcla(LATON, GACETA, 0.2f)), 0.9f * u, c) }
            }
            drawCircle(lat, w * 0.006f, e + Offset(0f, w * 0.004f), style = Stroke(1.3f * u))
        }

        // ---- el astil ladeado, con su fiel, y la horquilla con el remate
        rotate(giro, pivote) {
            // El fiel: la aguja que baja del pivote.
            drawPath(poligono(pivote.x - w * 0.004f, pivote.y, pivote.x + w * 0.004f, pivote.y, pivote.x, pivote.y + w * 0.058f), t.c(mezcla(HIERRO, TINTA, 0.3f)))
            translate(1.2f * u, 1.6f * u) { drawPath(astil, t.s(0.25f)) }
            drawPath(astil, laton)
            clipPath(astil) {
                drawRect(t.b(0.4f), Offset(pivote.x - brazo, pivote.y - w * 0.013f), Size(brazo * 2, w * 0.006f))
                drawRect(t.s(0.26f), Offset(pivote.x - brazo, pivote.y + w * 0.002f), Size(brazo * 2, w * 0.01f))
                if (esNoche) drawRect(t.b(0.5f), Offset(pivote.x, pivote.y - w * 0.013f), Size(brazo, w * 0.004f))
            }
            // Las muescas de los extremos.
            listOf(-1f, 1f).forEach { s -> drawCircle(t.c(mezcla(LATON, TINTA, 0.3f)), w * 0.0045f, Offset(pivote.x + s * brazo, pivote.y)) }
        }
        val hor = Rect(pie.x - w * 0.012f, pivote.y - w * 0.016f, pie.x + w * 0.012f, pivote.y + w * 0.014f)
        drawRoundRect(laton, hor.topLeft, hor.size, CornerRadius(w * 0.004f))
        clipRect(hor.left, hor.top, hor.right, hor.bottom) { cilindro(hor, t, 1.5f, der) }
        drawCircle(t.c(mezcla(HIERRO, TINTA, 0.3f)), w * 0.004f, pivote)
        drawRect(laton, Offset(pie.x - w * 0.004f, hor.top - w * 0.012f), Size(w * 0.008f, w * 0.012f))
        drawCircle(laton, w * 0.009f, Offset(pie.x, hor.top - w * 0.018f))
        drawCircle(t.s(0.22f), w * 0.009f * 0.6f, Offset(pie.x + (if (der) -1f else 1f) * w * 0.003f, hor.top - w * 0.015f))
        destello(Offset(pie.x + (if (der) 1f else -1f) * w * 0.003f, hor.top - w * 0.021f), 1.2f * u, t)
        destello(Offset(pie.x + (if (der) 1f else -1f) * w * 0.004f, col.top + col.height * 0.3f), 1.1f * u, t)
    }

    /**
     * El quinqué: el depósito de hojalata esmaltada en azul, desportillado, con su asa; el quemador de latón con la rueda de la
     * mecha y el tubo de vidrio panzudo, ahumado arriba. De día está apagado; de noche, la llama dentro del tubo.
     */
    private fun DrawScope.quinque(pie: Offset, sup: Float) {
        val u = density
        val w = anchoW
        val o = esNoche
        val t = tn(Offset(pie.x, pie.y - w * 0.04f))
        val dep = Rect(pie.x - w * 0.043f, pie.y - w * 0.052f, pie.x + w * 0.043f, pie.y - w * 0.006f)
        val base = Rect(pie.x - w * 0.03f, pie.y - w * 0.012f, pie.x + w * 0.03f, pie.y)
        val quem = Rect(pie.x - w * 0.017f, dep.top - w * 0.022f, pie.x + w * 0.017f, dep.top + w * 0.006f)
        val yb = quem.top
        val alto = w * 0.14f
        val tubo = Path().apply {
            moveTo(pie.x - w * 0.014f, yb)
            cubicTo(pie.x - w * 0.016f, yb - alto * 0.12f, pie.x - w * 0.031f, yb - alto * 0.16f, pie.x - w * 0.03f, yb - alto * 0.34f)
            cubicTo(pie.x - w * 0.029f, yb - alto * 0.55f, pie.x - w * 0.013f, yb - alto * 0.62f, pie.x - w * 0.012f, yb - alto)
            lineTo(pie.x + w * 0.012f, yb - alto)
            cubicTo(pie.x + w * 0.013f, yb - alto * 0.62f, pie.x + w * 0.029f, yb - alto * 0.55f, pie.x + w * 0.03f, yb - alto * 0.34f)
            cubicTo(pie.x + w * 0.031f, yb - alto * 0.16f, pie.x + w * 0.016f, yb - alto * 0.12f, pie.x + w * 0.014f, yb)
            close()
        }
        val cuerpo = Path().apply { addOval(dep); addRect(base); addRect(quem) }
        if (!o) { sombraEnPared(cuerpo, dep.center, sup, 0.22f); sombraEnPared(tubo, Offset(pie.x, yb - alto / 2), sup, 0.1f) }
        contacto(Offset(pie.x + w * 0.006f, pie.y), w * 0.085f, 3.5f * u, 0.42f)
        // El asa de alambre, a la derecha.
        drawOval(t.c(HIERRO), Offset(dep.right - w * 0.008f, dep.top + dep.height * 0.18f), Size(w * 0.03f, dep.height * 0.6f), style = Stroke(1.6f * u))
        // El pie y el depósito esmaltado.
        val esmalte = mezcla(MARINO, LATA, 0.12f)
        drawPath(poligono(base.left, base.bottom, base.left + w * 0.008f, base.top, base.right - w * 0.008f, base.top, base.right, base.bottom), t.c(esmalte))
        drawOval(t.c(esmalte), dep.topLeft, dep.size)
        clipPath(Path().apply { addOval(dep) }) {
            cilindro(dep, t, 1.5f)
            drawRect(t.s(0.22f), Offset(dep.left, dep.center.y + dep.height * 0.2f), Size(dep.width, dep.height))
            // Los desportillados del esmalte: asoma la hojalata, con su óxido.
            listOf(Offset(0.2f, 0.62f), Offset(0.7f, 0.3f), Offset(0.52f, 0.8f), Offset(0.84f, 0.6f)).forEachIndexed { k, f ->
                val p = Offset(dep.left + dep.width * f.x, dep.top + dep.height * f.y)
                drawOval(t.c(if (k % 2 == 0) LATA else mezcla(LATA, OXIDO, 0.6f)), p, Size(dep.width * (0.07f + 0.05f * azar(5470, k)), dep.height * 0.12f))
            }
            // El filete blanco del borde.
            drawLine(t.c(GACETA).copy(alpha = 0.7f), Offset(dep.left, dep.top + dep.height * 0.24f), Offset(dep.right, dep.top + dep.height * 0.24f), 0.9f * u)
        }
        // El quemador de latón, con su corona calada y la rueda de la mecha.
        drawRect(t.c(LATON), quem.topLeft, quem.size)
        clipRect(quem.left, quem.top, quem.right, quem.bottom) {
            cilindro(quem, t, 1.5f)
            for (k in 0 until 5) drawRect(t.c(mezcla(LATON, TINTA, 0.6f)), Offset(quem.left + quem.width * (0.1f + 0.18f * k), quem.top + quem.height * 0.2f), Size(quem.width * 0.08f, quem.height * 0.3f))
        }
        drawRect(t.c(LATON), Offset(quem.left - w * 0.004f, quem.bottom - w * 0.007f), Size(quem.width + w * 0.008f, w * 0.007f))
        drawLine(t.c(LATON), Offset(quem.right, quem.center.y), Offset(quem.right + w * 0.012f, quem.center.y), 1.2f * u)
        drawCircle(t.c(LATON), w * 0.006f, Offset(quem.right + w * 0.014f, quem.center.y))
        drawCircle(t.s(0.3f), w * 0.0025f, Offset(quem.right + w * 0.014f, quem.center.y))
        // El tubo de vidrio.
        drawPath(tubo, if (o) LUZ.copy(alpha = 0.3f) else t.c(VIDRIO).copy(alpha = 0.36f))
        clipPath(tubo) {
            if (o) {
                // La llama, dentro de la panza del tubo.
                val lc = Offset(pie.x, yb - alto * 0.2f)
                drawCircle(LUZ.copy(alpha = 0.5f), w * 0.03f, lc)
                drawOval(LUZ, Offset(lc.x - w * 0.008f, lc.y - w * 0.022f), Size(w * 0.016f, w * 0.036f))
                drawOval(Color(0xFFFFF4D8), Offset(lc.x - w * 0.0045f, lc.y - w * 0.012f), Size(w * 0.009f, w * 0.022f))
            } else {
                // La mecha y el brillo largo del vidrio.
                drawRect(t.c(GACETA), Offset(pie.x - w * 0.005f, yb - alto * 0.12f), Size(w * 0.01f, alto * 0.12f))
                drawRect(t.c(TINTA).copy(alpha = 0.7f), Offset(pie.x - w * 0.005f, yb - alto * 0.12f), Size(w * 0.01f, alto * 0.03f))
                drawRect(sombraMueble(0.12f), Offset(pie.x + w * 0.012f, yb - alto), Size(w * 0.02f, alto))
            }
            // El hollín de arriba.
            drawRect(t.c(TINTA).copy(alpha = if (o) 0.25f else 0.2f), Offset(pie.x - w * 0.02f, yb - alto), Size(w * 0.04f, alto * 0.2f))
            drawRect(t.c(TINTA).copy(alpha = 0.1f), Offset(pie.x - w * 0.02f, yb - alto * 0.8f), Size(w * 0.04f, alto * 0.12f))
        }
        drawPath(tubo, if (o) LUZ.copy(alpha = 0.55f) else luzMueble(0.75f), style = Stroke(1f * u))
        drawPath(Path().apply { moveTo(pie.x - w * 0.021f, yb - alto * 0.2f); cubicTo(pie.x - w * 0.025f, yb - alto * 0.35f, pie.x - w * 0.02f, yb - alto * 0.5f, pie.x - w * 0.009f, yb - alto * 0.7f) },
            if (o) Color(0xFFFFF4D8).copy(alpha = 0.7f) else luzMueble(0.85f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        drawOval(if (o) LUZ.copy(alpha = 0.6f) else luzMueble(0.6f), Offset(pie.x - w * 0.012f, yb - alto - w * 0.003f), Size(w * 0.024f, w * 0.006f), style = Stroke(0.9f * u))
        if (o) drawCircle(LUZ.copy(alpha = 0.3f), w * 0.01f, Offset(pie.x - w * 0.012f, dep.top + dep.height * 0.3f))
    }

    /**
     * La gaceta clavada al sobre: lo de arriba descansa en los tablones y el resto cuelga por delante del canto y del friso,
     * sujeta con dos tachuelas, con su cabecera, sus columnas de renglones, un grabado y la esquina de abajo levantada.
     */
    private fun DrawScope.gaceta(r: Rect, frente: Float) {
        val u = density
        val w = anchoW
        val t = tn(Offset(r.center.x, frente))
        val cae = Rect(r.left, frente, r.right, r.bottom)
        val esq = r.width * 0.16f
        val hoja = poligono(cae.left, cae.top, cae.right, cae.top, cae.right + w * 0.004f, cae.bottom - esq, cae.right - esq * 0.9f, cae.bottom + w * 0.003f, cae.left + w * 0.005f, cae.bottom)
        // Lo que descansa en el sobre, visto de sesgo.
        val echada = poligono(r.left + w * 0.014f, r.top, r.right + w * 0.01f, r.top, r.right, frente, r.left, frente)
        translate(w * 0.006f, w * 0.003f) { drawPath(echada, t.s(0.25f)) }
        drawPath(echada, t.c(mezcla(GACETA, CUERDA, 0.12f)))
        for (j in 1..3) { val y = r.top + (frente - r.top) * j / 4f; drawLine(t.c(TINTA).copy(alpha = 0.3f), Offset(r.left + w * 0.02f, y), Offset(r.right - w * 0.006f, y), 0.6f * u) }
        // La sombra de lo que cuelga, en el canto y en el friso.
        translate(w * 0.012f, w * 0.008f) { drawPath(hoja, t.s(0.28f)) }
        drawPath(hoja, t.c(GACETA))
        clipPath(hoja) {
            val tinta = t.c(TINTA)
            // La cabecera: una pastilla de tinta partida en bloques (letras de lejos) entre dos filetes.
            val cy = cae.top + cae.height * 0.1f
            drawLine(tinta.copy(alpha = 0.6f), Offset(cae.left + cae.width * 0.06f, cy), Offset(cae.right - cae.width * 0.06f, cy), 0.7f * u)
            var x = cae.left + cae.width * 0.12f
            var k = 0
            while (x < cae.right - cae.width * 0.16f) {
                val aw = cae.width * (0.04f + 0.035f * azar(5480, k))
                if (k % 5 != 3) drawRect(tinta.copy(alpha = 0.82f), Offset(x, cy + cae.height * 0.035f), Size(aw, cae.height * 0.085f))
                x += aw + cae.width * 0.012f; k++
            }
            val cy2 = cy + cae.height * 0.155f
            drawLine(tinta.copy(alpha = 0.75f), Offset(cae.left + cae.width * 0.06f, cy2), Offset(cae.right - cae.width * 0.06f, cy2), 1.1f * u)
            drawLine(tinta.copy(alpha = 0.5f), Offset(cae.left + cae.width * 0.06f, cy2 + 2f * u), Offset(cae.right - cae.width * 0.06f, cy2 + 2f * u), 0.5f * u)
            // Tres columnas de renglones, con un titular y un grabado.
            val c0 = cy2 + cae.height * 0.06f
            for (col in 0 until 3) {
                val xa = cae.left + cae.width * (0.07f + 0.3f * col); val xb = xa + cae.width * 0.26f
                var y = c0
                var j = 0
                while (y < cae.bottom - cae.height * 0.05f) {
                    val grabado = col == 1 && j in 2..6
                    val titular = (col == 0 && j == 0) || (col == 2 && j == 5)
                    if (titular) drawRect(tinta.copy(alpha = 0.7f), Offset(xa, y - 0.8f * u), Size((xb - xa) * 0.8f, 2.2f * u))
                    else if (!grabado) drawLine(tinta.copy(alpha = 0.42f), Offset(xa, y), Offset(xb - (xb - xa) * 0.3f * azar(5481 + col, j) * (if (j % 4 == 3) 1f else 0.15f), y), 0.7f * u)
                    y += 2.3f * u; j++
                }
                if (col == 1) {
                    val g = Rect(xa, c0 + 2.3f * u * 1.6f, xb, c0 + 2.3f * u * 6.6f)
                    drawRect(t.c(MARINO).copy(alpha = 0.5f), g.topLeft, g.size)
                    drawCircle(t.c(GACETA).copy(alpha = 0.7f), g.height * 0.24f, Offset(g.left + g.width * 0.68f, g.top + g.height * 0.38f))
                    drawPath(poligono(g.left, g.bottom, g.left + g.width * 0.3f, g.top + g.height * 0.45f, g.left + g.width * 0.55f, g.bottom), t.c(TINTA).copy(alpha = 0.45f))
                }
                if (col > 0) drawLine(tinta.copy(alpha = 0.3f), Offset(xa - cae.width * 0.02f, c0 - 2f * u), Offset(xa - cae.width * 0.02f, cae.bottom), 0.5f * u)
            }
            // El papel, amarillento por abajo, y la sombra del doblez de arriba.
            drawRect(t.c(CINTA).copy(alpha = 0.22f), Offset(cae.left, cae.bottom - cae.height * 0.3f), Size(cae.width + w * 0.01f, cae.height * 0.32f))
            drawRect(t.s(0.12f), cae.topLeft, Size(cae.width, 3f * u))
            drawRect(t.s(0.1f), Offset(cae.right - cae.width * 0.06f, cae.top), Size(cae.width * 0.08f, cae.height))
        }
        drawLine(t.b(0.6f), Offset(cae.left, cae.top), Offset(cae.right, cae.top), 1f * u)
        // La esquina de abajo, levantada: el revés y su sombra.
        val doblez = poligono(cae.right + w * 0.004f, cae.bottom - esq, cae.right - esq * 0.75f, cae.bottom - esq * 0.7f, cae.right - esq * 0.9f, cae.bottom + w * 0.003f)
        translate(-1.5f * u, 2f * u) { drawPath(doblez, t.s(0.25f)) }
        drawPath(doblez, t.c(mezcla(GACETA, CUERDA, 0.3f)))
        // Las dos tachuelas, en el canto.
        listOf(cae.left + cae.width * 0.1f, cae.right - cae.width * 0.1f).forEach { x -> clavo(Offset(x, cae.top + w * 0.012f), 1.7f * u, t, LATON) }
    }

    /** Las pastillas de jabón apiladas: tres, de distinta receta (sebo, hierbas y ceniza), la de arriba atravesada y con su sello. */
    private fun DrawScope.jabones(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.02f))
        val der = luzDerecha(pie.x)
        val a = w * 0.064f; val al = w * 0.0175f
        contacto(Offset(pie.x + a * 0.06f, pie.y), a * 1.25f, 3.4f * u, 0.4f)
        val colores = listOf(mezcla(GACETA, MAIZ, 0.4f), mezcla(SALVIA, GACETA, 0.5f), mezcla(GACETA, LADRILLO, 0.2f))
        colores.forEachIndexed { i, col ->
            val cx = pie.x + a * (if (i == 1) 0.06f else if (i == 2) -0.05f else 0f)
            val ancho = if (i == 2) a * 0.78f else a
            val r = Rect(cx - ancho / 2, pie.y - al * (i + 1), cx + ancho / 2, pie.y - al * i)
            val barra = Path().apply { addRoundRect(RoundRect(r, CornerRadius(al * 0.28f))) }
            drawPath(barra, t.c(col))
            clipPath(barra) {
                drawRect(t.b(0.32f), r.topLeft, Size(r.width, al * 0.3f))
                drawRect(t.s(0.2f), Offset(r.left, r.bottom - al * 0.28f), Size(r.width, al * 0.28f))
                drawRect(if (der) t.b(0.2f) else t.s(0.2f), Offset(r.right - r.width * 0.14f, r.top), Size(r.width * 0.14f, al))
                if (der) drawRect(t.s(0.16f), r.topLeft, Size(r.width * 0.14f, al))
                // Las motas de la receta.
                for (k in 0 until 5) drawCircle(t.s(0.16f), 0.7f * u, Offset(r.left + r.width * azar(5490 + i, k), r.top + al * azar(5493 + i, k)))
            }
            if (i == 2) {
                // La de arriba, atravesada: se le ve la cara de arriba con el sello hundido.
                val cara = poligono(r.left + a * 0.04f, r.top, r.left + a * 0.12f, r.top - al * 0.5f, r.right + a * 0.02f, r.top - al * 0.5f, r.right - a * 0.04f, r.top)
                drawPath(cara, t.c(mezcla(col, GACETA, 0.35f)))
                drawOval(t.s(0.2f), Offset(r.center.x - a * 0.14f, r.top - al * 0.4f), Size(a * 0.3f, al * 0.3f), style = Stroke(0.7f * u))
            }
        }
    }

    /**
     * Las pesas que no están en el platillo, sobre la gaceta: tres tuercas apiladas y atadas con alambre, con su asa, y un
     * perno con su tuerca.
     */
    private fun DrawScope.pesas(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.02f))
        val der = luzDerecha(pie.x)
        contacto(Offset(pie.x + w * 0.012f, pie.y), w * 0.1f, 3.4f * u, 0.42f)
        // La pila de tres, de mayor a menor.
        val anchos = listOf(0.044f, 0.037f, 0.03f)
        val al = w * 0.0135f
        anchos.forEachIndexed { i, a -> tuerca(pie.x - w * 0.016f, pie.y - al * i, w * a, al, t, der, 5500 + i) }
        val topePila = pie.y - al * 3
        val asa = Path().apply { moveTo(pie.x - w * 0.026f, topePila); cubicTo(pie.x - w * 0.026f, topePila - w * 0.026f, pie.x - w * 0.006f, topePila - w * 0.026f, pie.x - w * 0.006f, topePila) }
        drawPath(asa, t.c(mezcla(HIERRO, OXIDO, 0.35f)), style = Stroke(1.5f * u))
        drawPath(asa, t.b(0.4f), style = Stroke(0.5f * u))
        listOf(-0.026f, -0.006f).forEach { fx -> drawLine(t.c(mezcla(HIERRO, OXIDO, 0.35f)), Offset(pie.x + w * fx, topePila), Offset(pie.x + w * fx, pie.y - 1f * u), 0.9f * u) }
        // El perno, de pie, con su tuerca enroscada.
        val bx = pie.x + w * 0.032f
        tuerca(bx, pie.y + w * 0.002f, w * 0.03f, al * 0.9f, t, der, 5510)
        val rosca = Rect(bx - w * 0.006f, pie.y - w * 0.034f, bx + w * 0.006f, pie.y - al * 0.9f)
        drawRect(t.c(HIERRO), rosca.topLeft, rosca.size)
        clipRect(rosca.left, rosca.top, rosca.right, rosca.bottom) {
            cilindro(rosca, t, 1.4f, der)
            var y = rosca.top
            while (y < rosca.bottom) { drawLine(t.s(0.35f), Offset(rosca.left, y), Offset(rosca.right, y + 1.2f * u), 0.6f * u); y += 2f * u }
        }
        tuerca(bx, rosca.top + w * 0.004f, w * 0.022f, w * 0.009f, t, der, 5511)
    }

    /** La campanilla de mostrador: la peana, la cúpula de latón con su brillo y el botón de arriba. */
    private fun DrawScope.campanilla(pie: Offset) {
        val u = density
        val w = anchoW
        val t = tn(Offset(pie.x, pie.y - w * 0.015f))
        val der = luzDerecha(pie.x)
        val rb = w * 0.031f
        contacto(Offset(pie.x + rb * 0.1f, pie.y), rb * 2.4f, 3.2f * u, 0.42f)
        val peana = Rect(pie.x - rb, pie.y - w * 0.008f, pie.x + rb, pie.y)
        drawRoundRect(t.c(mezcla(HIERRO, TINTA, 0.3f)), peana.topLeft, peana.size, CornerRadius(w * 0.003f))
        drawRect(t.b(0.4f), peana.topLeft, Size(peana.width, 0.9f * u))
        val rc = rb * 0.84f; val alto = w * 0.03f
        val cupula = Path().apply {
            moveTo(pie.x - rc, peana.top)
            cubicTo(pie.x - rc, peana.top - alto * 1.3f, pie.x + rc, peana.top - alto * 1.3f, pie.x + rc, peana.top)
            close()
        }
        drawPath(cupula, t.c(LATON))
        clipPath(cupula) {
            cilindro(Rect(pie.x - rc, peana.top - alto, pie.x + rc, peana.top), t, 1.6f, der)
            drawRect(t.s(0.22f), Offset(pie.x - rc, peana.top - alto * 0.2f), Size(rc * 2, alto * 0.2f))
            drawLine(t.s(0.25f), Offset(pie.x - rc, peana.top - alto * 0.32f), Offset(pie.x + rc, peana.top - alto * 0.32f), 0.6f * u)
        }
        drawRect(t.c(HIERRO), Offset(pie.x - w * 0.003f, peana.top - alto - w * 0.006f), Size(w * 0.006f, w * 0.009f))
        drawCircle(t.c(mezcla(HIERRO, TINTA, 0.4f)), w * 0.0065f, Offset(pie.x, peana.top - alto - w * 0.009f))
        drawCircle(t.b(0.5f), w * 0.002f, Offset(pie.x - w * 0.002f, peana.top - alto - w * 0.011f))
        destello(Offset(pie.x + (if (der) 1f else -1f) * rc * 0.45f, peana.top - alto * 0.62f), 1.4f * u, t)
    }

    /**
     * Un frasco de conserva en el sobre: el vidrio con lo que guarda (a trozos, con su caldo), el brillo, y la tapa de tela de
     * cuadros atada con cordel, con su vuelo.
     */
    private fun DrawScope.frasco(pie: Offset, a: Float, al: Float, contenido: Color, tela: Color, sup: Float, i: Int) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - al / 2))
        val der = luzDerecha(pie.x)
        val cuerpoR = Rect(pie.x - a / 2, pie.y - al * 0.78f, pie.x + a / 2, pie.y)
        val cuerpo = Path().apply { addRoundRect(RoundRect(cuerpoR, CornerRadius(a * 0.2f, a * 0.2f))) }
        val cuello = Rect(pie.x - a * 0.36f, pie.y - al * 0.86f, pie.x + a * 0.36f, cuerpoR.top + al * 0.06f)
        val gorro = Path().apply {
            moveTo(pie.x - a * 0.42f, cuello.top + al * 0.05f)
            cubicTo(pie.x - a * 0.42f, pie.y - al * 1.03f, pie.x + a * 0.42f, pie.y - al * 1.03f, pie.x + a * 0.42f, cuello.top + al * 0.05f)
            // El vuelo de la tela por debajo del cordel, con sus picos.
            lineTo(pie.x + a * 0.56f, cuello.top + al * 0.17f); lineTo(pie.x + a * 0.36f, cuello.top + al * 0.13f); lineTo(pie.x + a * 0.2f, cuello.top + al * 0.2f)
            lineTo(pie.x, cuello.top + al * 0.14f); lineTo(pie.x - a * 0.22f, cuello.top + al * 0.2f); lineTo(pie.x - a * 0.38f, cuello.top + al * 0.13f); lineTo(pie.x - a * 0.56f, cuello.top + al * 0.18f)
            close()
        }
        sombraEnPared(Path().apply { addPath(cuerpo); addPath(gorro) }, cuerpoR.center, sup, 0.2f)
        contacto(Offset(pie.x + a * 0.1f, pie.y), a * 1.3f, 3.4f * u, 0.42f)
        drawRect(t.c(VIDRIO).copy(alpha = 0.4f), cuello.topLeft, cuello.size)
        drawPath(cuerpo, t.c(VIDRIO).copy(alpha = 0.4f))
        clipPath(cuerpo) {
            // Lo que guarda: hasta el hombro, a trozos, con su caldo más oscuro abajo.
            val lleno = Rect(cuerpoR.left, cuerpoR.top + cuerpoR.height * 0.14f, cuerpoR.right, cuerpoR.bottom)
            drawRect(t.c(mezcla(contenido, TABLON, 0.18f)), lleno.topLeft, lleno.size)
            for (k in 0 until 9) {
                val p = Offset(lleno.left + lleno.width * (0.02f + 0.8f * azar(5520 + i, k)), lleno.top + lleno.height * (0.02f + 0.82f * azar(5523 + i, k)))
                drawOval(t.c(mezcla(contenido, if (k % 3 == 0) GACETA else MAIZ, 0.22f + 0.2f * azar(5526 + i, k))), p, Size(a * (0.2f + 0.14f * azar(5529 + i, k)), a * 0.17f))
                drawArc(t.s(0.2f), 0f, 180f, false, p, Size(a * 0.26f, a * 0.17f), style = Stroke(0.7f * u))
            }
            drawRect(t.s(0.16f), Offset(lleno.left, lleno.bottom - lleno.height * 0.2f), Size(lleno.width, lleno.height * 0.2f))
            drawLine(t.b(0.4f), Offset(lleno.left, lleno.top), Offset(lleno.right, lleno.top), 0.8f * u)
            cilindro(cuerpoR, t, 1.3f, der)
        }
        drawPath(cuerpo, t.b(0.45f), style = Stroke(0.9f * u))
        // El brillo largo del vidrio y el del hombro.
        val bx = if (der) cuerpoR.right - a * 0.18f else cuerpoR.left + a * 0.18f
        drawLine(if (esNoche) t.b(0.9f) else luzMueble(0.75f), Offset(bx, cuerpoR.top + cuerpoR.height * 0.2f), Offset(bx, cuerpoR.bottom - cuerpoR.height * 0.22f), 1.8f * u, StrokeCap.Round)
        destello(Offset(bx + (if (der) -1f else 1f) * a * 0.06f, cuerpoR.top + cuerpoR.height * 0.1f), 1.2f * u, t)
        // La tapa de tela de cuadros.
        val pano = mezcla(tela, GACETA, 0.12f)
        drawPath(gorro, t.c(pano))
        clipPath(gorro) {
            val gr = Rect(pie.x - a * 0.6f, pie.y - al * 1.03f, pie.x + a * 0.6f, cuello.top + al * 0.2f)
            var x = gr.left
            while (x < gr.right) { drawRect(t.c(GACETA).copy(alpha = 0.4f), Offset(x, gr.top), Size(a * 0.07f, gr.height)); x += a * 0.17f }
            var y = gr.top
            while (y < gr.bottom) { drawRect(t.c(GACETA).copy(alpha = 0.4f), Offset(gr.left, y), Size(gr.width, a * 0.07f)); y += a * 0.17f }
            if (der) { drawRect(t.b(0.2f), Offset(pie.x + a * 0.1f, gr.top), Size(a * 0.3f, gr.height)); drawRect(t.s(0.2f), gr.topLeft, Size(a * 0.3f, gr.height)) }
            else { drawRect(t.b(0.22f), Offset(pie.x - a * 0.4f, gr.top), Size(a * 0.3f, gr.height)); drawRect(t.s(0.24f), Offset(pie.x + a * 0.18f, gr.top), Size(a * 0.5f, gr.height)) }
            // Los pliegues del vuelo.
            for (k in 0 until 5) drawLine(t.s(0.25f), Offset(pie.x + a * (-0.4f + 0.2f * k), cuello.top + al * 0.06f), Offset(pie.x + a * (-0.46f + 0.23f * k), cuello.top + al * 0.2f), 0.7f * u)
        }
        // El cordel, con su lazada.
        val cy = cuello.top + al * 0.055f
        drawLine(t.s(0.3f), Offset(pie.x - a * 0.42f, cy + 1.2f * u), Offset(pie.x + a * 0.42f, cy + 1.2f * u), 1.4f * u)
        drawLine(t.c(CUERDA), Offset(pie.x - a * 0.43f, cy), Offset(pie.x + a * 0.43f, cy), 1.6f * u)
        val lz = Offset(pie.x + a * 0.12f, cy)
        listOf(-1f, 1f).forEach { s ->
            drawOval(t.c(CUERDA), Offset(lz.x + (if (s < 0) -a * 0.2f else 0f), lz.y - al * 0.04f), Size(a * 0.2f, al * 0.07f), style = Stroke(1.1f * u))
            drawLine(t.c(CUERDA), lz, lz + Offset(s * a * 0.12f, al * 0.12f), 1.1f * u, StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- al pie

    /**
     * El saco de harina, abierto y cortado por el borde izquierdo: la tela de algodón con sus dos franjas estarcidas, la boca
     * enrollada, la harina a la vista y la que se ha caído al piso.
     */
    private fun DrawScope.sacoHarina(pie: Offset, w: Float, hb: Float) {
        val u = density
        val a = w * 0.23f; val h = hb * 0.62f
        val r = Rect(pie.x - a * 0.6f, pie.y - h, pie.x + a * 0.6f, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(pie.x)
        val f = saco(pie.x, pie.y, a, h, 0.84f)
        val algodon = mezcla(GACETA, CAL_OSC, 0.35f)
        translate(w * 0.02f, w * 0.004f) { drawPath(f, t.s(0.26f)) }
        contacto(Offset(pie.x + a * 0.08f, pie.y), a * 1.3f, hb * 0.07f, 0.45f)
        // La harina caída alrededor.
        listOf(Offset(0.62f, 0.02f), Offset(0.78f, -0.03f), Offset(0.5f, 0.05f), Offset(0.9f, 0.03f)).forEachIndexed { k, d ->
            drawOval(t.c(GACETA).copy(alpha = 0.5f - 0.08f * k), Offset(pie.x + a * d.x - a * 0.12f, pie.y + hb * d.y - hb * 0.02f), Size(a * (0.3f - 0.04f * k), hb * 0.045f))
        }
        // La boca: el borde enrollado de atrás y la harina, que hace montón por encima del borde.
        val boca = Rect(pie.x - a * 0.45f, r.top - h * 0.17f, pie.x + a * 0.45f, r.top + h * 0.07f)
        val cy = boca.center.y; val ry = boca.height / 2
        drawOval(t.c(mezcla(algodon, PINO_OSC, 0.3f)), Offset(boca.left - a * 0.04f, boca.top - h * 0.05f), Size(boca.width + a * 0.08f, boca.height + h * 0.05f))
        drawOval(t.b(0.3f), Offset(boca.left - a * 0.04f, boca.top - h * 0.05f), Size(boca.width + a * 0.08f, boca.height + h * 0.05f), style = Stroke(0.9f * u))
        val harina = mezcla(GACETA, Color.White, 0.5f)
        val monton = Path().apply {
            moveTo(boca.left + a * 0.03f, cy)
            cubicTo(boca.left + a * 0.16f, boca.top - h * 0.2f, pie.x + a * 0.05f, boca.top - h * 0.3f, pie.x + a * 0.16f, boca.top - h * 0.04f)
            cubicTo(pie.x + a * 0.26f, boca.top + h * 0.02f, boca.right - a * 0.08f, boca.top + h * 0.02f, boca.right - a * 0.03f, cy)
            cubicTo(boca.right - a * 0.03f, cy + ry * 1.33f, boca.left + a * 0.03f, cy + ry * 1.33f, boca.left + a * 0.03f, cy)
            close()
        }
        drawPath(monton, t.c(harina))
        clipPath(monton) {
            // La cara del montón que no mira a la luz, el hoyo que dejó la pala y unos grumos.
            drawPath(Path().apply { moveTo(pie.x - a * 0.02f, boca.top - h * 0.3f); cubicTo(pie.x + a * 0.02f, boca.top - h * 0.05f, pie.x + a * 0.1f, boca.top + h * 0.05f, pie.x + a * 0.02f, boca.bottom); lineTo(boca.right, boca.bottom); lineTo(boca.right, boca.top - h * 0.3f); close() },
                if (der) t.b(0.18f) else t.s(0.14f))
            drawOval(t.s(0.14f), Offset(pie.x + a * 0.12f, boca.top + h * 0.03f), Size(a * 0.22f, h * 0.07f))
            for (k in 0 until 6) drawOval(t.s(0.1f), Offset(boca.left + boca.width * azar(5602, k), boca.top - h * 0.1f + h * 0.2f * azar(5603, k)), Size(a * 0.05f, h * 0.02f))
        }
        telaSaco(f, r, t, algodon, der, 5600, 0.05f)
        clipPath(f) {
            // Las dos franjas estarcidas, que siguen la panza del saco.
            listOf(0.42f to LADRILLO, 0.56f to MARINO).forEach { (fy, col) ->
                val y = r.top + h * fy
                drawPath(Path().apply { moveTo(r.left, y); cubicTo(r.left + r.width * 0.3f, y + h * 0.06f, r.left + r.width * 0.7f, y + h * 0.06f, r.right, y) }, t.c(col).copy(alpha = 0.55f), style = Stroke(h * 0.07f))
            }
            // El polvo de harina que mancha la tela.
            pinceladas(Rect(r.left, r.top, r.right, r.top + h * 0.4f), t.c(GACETA), 5601, 8, u, 0.4f)
        }
        // El borde enrollado de delante: una morcilla de tela que sigue la boca, con sus vueltas.
        val grueso = h * 0.15f
        val e = a * 0.05f
        val rollo = Path().apply {
            moveTo(boca.left - e, cy)
            cubicTo(boca.left - e, cy + (ry + grueso) * 1.33f, boca.right + e, cy + (ry + grueso) * 1.33f, boca.right + e, cy)
            cubicTo(boca.right + e, cy - grueso * 0.5f, boca.right - e * 1.2f, cy - grueso * 0.5f, boca.right - e * 1.2f, cy)
            cubicTo(boca.right - e * 1.2f, cy + ry * 1.33f, boca.left + e * 1.2f, cy + ry * 1.33f, boca.left + e * 1.2f, cy)
            cubicTo(boca.left + e * 1.2f, cy - grueso * 0.5f, boca.left - e, cy - grueso * 0.5f, boca.left - e, cy)
            close()
        }
        translate(0f, 2.6f * u) { drawPath(rollo, t.s(0.3f)) }
        drawPath(rollo, t.c(mezcla(algodon, GACETA, 0.45f)))
        clipPath(rollo) {
            var x = boca.left - e - grueso
            while (x < boca.right + e) { drawLine(t.s(0.16f), Offset(x, cy + ry + grueso), Offset(x + grueso * 0.9f, cy - grueso), 0.7f * u); x += 4.6f * u }
            // La luz en el lomo de la morcilla y la sombra en su panza.
            drawPath(Path().apply { moveTo(boca.left, cy + ry * 0.3f); cubicTo(boca.left + a * 0.05f, cy + (ry + grueso * 0.3f) * 1.33f, boca.right - a * 0.05f, cy + (ry + grueso * 0.3f) * 1.33f, boca.right, cy + ry * 0.3f) }, t.b(0.3f), style = Stroke(grueso * 0.3f))
            drawPath(Path().apply { moveTo(boca.left - e, cy + ry * 0.8f); cubicTo(boca.left, cy + (ry + grueso * 1.05f) * 1.33f, boca.right, cy + (ry + grueso * 1.05f) * 1.33f, boca.right + e, cy + ry * 0.8f) }, t.s(0.2f), style = Stroke(grueso * 0.35f))
            if (der) drawRect(t.s(0.14f), Offset(boca.left - e, cy - grueso), Size(a * 0.2f, ry + grueso * 3f)) else drawRect(t.s(0.16f), Offset(boca.right - a * 0.16f, cy - grueso), Size(a * 0.3f, ry + grueso * 3f))
        }
    }

    /** Una manzana: el cuerpo con su media luna de sombra, el hoyo del rabo, el rabo y el brillo. */
    private fun DrawScope.manzana(c: Offset, r: Float, col: Color, t: Tono, der: Boolean) {
        val u = density
        val caja = Rect(c.x - r, c.y - r * 0.92f, c.x + r, c.y + r * 0.92f)
        drawOval(t.c(col), caja.topLeft, caja.size)
        drawPath(creciente(caja, if (der) r * 0.34f else -r * 0.34f, -r * 0.3f), t.s(0.26f))
        // El lado que el sol no maduró.
        clipPath(Path().apply { addOval(caja) }) {
            drawOval(t.c(mezcla(col, MAIZ, 0.5f)).copy(alpha = 0.3f), Offset(c.x - r * (if (der) 1.3f else -0.1f), c.y - r * 0.1f), Size(r * 1.4f, r * 1.3f))
            // Las pintas de la piel.
            for (k in 0 until 5) drawCircle(t.b(0.3f), r * 0.03f, Offset(c.x + r * 0.7f * (azar((c.x + c.y).toInt(), k) - 0.5f), c.y + r * 0.8f * (azar((c.x - c.y).toInt(), k + 5) - 0.4f)))
        }
        drawOval(t.s(0.3f), Offset(c.x - r * 0.3f, c.y - r * 0.9f), Size(r * 0.6f, r * 0.26f))
        drawLine(t.c(TABLON), Offset(c.x, c.y - r * 0.78f), Offset(c.x + r * 0.16f, c.y - r * 1.2f), 1.2f * u, StrokeCap.Round)
        drawOval(t.b(0.55f), Offset(if (der) c.x + r * 0.2f else c.x - r * 0.62f, c.y - r * 0.55f), Size(r * 0.36f, r * 0.26f))
    }

    /**
     * La caja de fruta: los dos listones de las esquinas, las tablillas con sus rendijas (por las que se ve la fruta), sus
     * clavos y las manzanas que colman la caja; una rodó al piso.
     */
    private fun DrawScope.cajaFruta(pie: Offset, w: Float, hb: Float) {
        val u = density
        val cw = w * 0.235f; val chh = hb * 0.36f
        val r = Rect(pie.x - cw / 2, pie.y - chh, pie.x + cw / 2, pie.y)
        val t = tn(r.center)
        val der = luzDerecha(pie.x)
        val rojo = mezcla(LADRILLO, OXIDO, 0.25f)
        translate(w * 0.02f, w * 0.004f) { drawRect(t.s(0.26f), r.topLeft, r.size) }
        contacto(Offset(pie.x + cw * 0.06f, pie.y), cw * 1.15f, hb * 0.06f, 0.45f)
        // Las manzanas que colman la caja, de atrás adelante.
        val frutas = listOf(Triple(0.16f, -0.36f, rojo), Triple(0.4f, -0.46f, mezcla(SALVIA, MAIZ, 0.35f)), Triple(0.66f, -0.4f, rojo), Triple(0.86f, -0.3f, mezcla(MAIZ, LADRILLO, 0.35f)),
            Triple(0.28f, -0.16f, mezcla(MAIZ, LADRILLO, 0.45f)), Triple(0.54f, -0.2f, rojo), Triple(0.76f, -0.12f, mezcla(SALVIA, MAIZ, 0.2f)))
        frutas.forEach { (fx, fy, col) -> manzana(Offset(r.left + cw * fx, r.top + chh * fy), cw * 0.115f, col, t, der) }
        // El fondo oscuro de la caja y la fruta que se ve por las rendijas.
        drawRect(t.c(mezcla(TABLON, TINTA, 0.4f)), r.topLeft, r.size)
        for (k in 0 until 7) drawCircle(t.c(if (k % 3 == 1) mezcla(SALVIA, MAIZ, 0.3f) else rojo).copy(alpha = 0.75f), cw * 0.09f, Offset(r.left + cw * (0.1f + 0.135f * k), r.top + chh * (if (k % 2 == 0) 0.36f else 0.7f)))
        // Las tres tablillas.
        listOf(0f to 0.27f, 0.36f to 0.63f, 0.72f to 1f).forEachIndexed { i, (a, z) ->
            val tb = Rect(r.left, r.top + chh * a, r.right, r.top + chh * z)
            tabla(tb, if (i == 1) mezcla(PINO_LUZ, GACETA, 0.2f) else mezcla(PINO_LUZ, PINO, 0.4f), 5610 + i, vertical = false)
            drawRect(t.s(0.3f), Offset(tb.left, tb.bottom), Size(tb.width, 1.6f * u))
        }
        if (der) drawRect(t.b(0.12f), Offset(r.right - cw * 0.3f, r.top), Size(cw * 0.3f, chh)) else drawRect(t.s(0.14f), Offset(r.right - cw * 0.3f, r.top), Size(cw * 0.3f, chh))
        // Los listones de las esquinas y sus clavos.
        listOf(r.left, r.right - cw * 0.085f).forEachIndexed { i, x ->
            val ls = Rect(x, r.top - chh * 0.04f, x + cw * 0.085f, r.bottom)
            drawRect(t.s(0.25f), Offset(ls.left + 1.6f * u, ls.top + 1f * u), ls.size)
            tabla(ls, mezcla(PINO, PINO_OSC, 0.25f), 5620 + i, vertical = true)
            listOf(0.16f, 0.52f, 0.86f).forEach { fy -> clavo(Offset(ls.center.x, r.top + chh * fy), 1f * u, t) }
        }
        // Una hoja de manzano que quedó enganchada y la manzana que rodó al piso.
        val mz = Offset(r.right + cw * 0.2f, pie.y - cw * 0.085f + hb * 0.03f)
        contacto(Offset(mz.x + cw * 0.02f, mz.y + cw * 0.1f), cw * 0.3f, hb * 0.04f, 0.45f)
        manzana(mz, cw * 0.105f, mezcla(MAIZ, LADRILLO, 0.5f), tn(mz), luzDerecha(mz.x))
        val hj = Offset(r.left + cw * 0.5f, r.top + chh * 0.02f)
        rotate(-24f, hj) {
            drawOval(t.c(mezcla(SALVIA, TABLON, 0.2f)), Offset(hj.x - cw * 0.06f, hj.y - cw * 0.022f), Size(cw * 0.12f, cw * 0.044f))
            drawLine(t.b(0.35f), Offset(hj.x - cw * 0.06f, hj.y), Offset(hj.x + cw * 0.06f, hj.y), 0.6f * u)
        }
    }

    /**
     * El barril, cortado por el borde derecho: las duelas panzudas con sus juntas, los cuatro flejes de hierro con su óxido, y
     * la tapa de tablas, corrida, con su travesaño y el hueco oscuro que deja.
     */
    private fun DrawScope.barril(pie: Offset, w: Float, hb: Float) {
        val u = density
        val bw = w * 0.26f; val bh = hb * 0.86f
        val top = pie.y - bh
        val t = tn(Offset(pie.x, pie.y - bh / 2))
        val tt = tn(Offset(pie.x, top))
        val der = luzDerecha(pie.x)
        val boca = 0.4f
        val f = Path().apply {
            moveTo(pie.x - bw * boca, top)
            cubicTo(pie.x - bw * 0.52f, top + bh * 0.3f, pie.x - bw * 0.52f, pie.y - bh * 0.3f, pie.x - bw * boca, pie.y)
            cubicTo(pie.x - bw * 0.2f, pie.y + bh * 0.04f, pie.x + bw * 0.2f, pie.y + bh * 0.04f, pie.x + bw * boca, pie.y)
            cubicTo(pie.x + bw * 0.52f, pie.y - bh * 0.3f, pie.x + bw * 0.52f, top + bh * 0.3f, pie.x + bw * boca, top)
            close()
        }
        translate(-w * 0.012f, w * 0.004f) { drawPath(f, t.s(0.2f)) }
        contacto(Offset(pie.x, pie.y + bh * 0.02f), bw * 1.2f, hb * 0.08f, 0.5f)
        val r = Rect(pie.x - bw * 0.52f, top, pie.x + bw * 0.52f, pie.y + bh * 0.04f)
        drawPath(f, t.c(mezcla(PINO_OSC, TABLON, 0.35f)))
        clipPath(f) {
            // Las duelas: sus juntas, que siguen la panza, y cada una de su tono.
            for (k in -3..3) {
                val fx = k / 3.6f
                val x0 = pie.x + bw * boca * fx; val xm = pie.x + bw * 0.53f * fx
                val junta = Path().apply { moveTo(x0, top); cubicTo(xm, top + bh * 0.3f, xm, pie.y - bh * 0.3f, x0, pie.y + bh * 0.04f) }
                if (k % 2 == 0) drawPath(Path().apply { addPath(junta); lineTo(x0 + bw * 0.12f, pie.y + bh * 0.04f); cubicTo(xm + bw * 0.15f, pie.y - bh * 0.3f, xm + bw * 0.15f, top + bh * 0.3f, x0 + bw * 0.12f, top); close() }, t.c(PINO).copy(alpha = 0.22f))
                drawPath(junta, t.s(0.35f), style = Stroke(1.2f * u))
                translate(1.2f * u, 0f) { drawPath(junta, t.b(0.2f), style = Stroke(0.7f * u)) }
            }
            cilindro(r, t, 1.3f, der)
            pinceladas(r, if (esNoche) NOCHE else TABLON, 5630, 12, u, 0.16f)
            // Los flejes de hierro, que se comban hacia abajo, con su remache y su óxido.
            listOf(0.07f, 0.27f, 0.72f, 0.92f).forEachIndexed { i, fy ->
                val y = top + bh * fy
                val comba = bh * 0.035f
                val fleje = Path().apply { moveTo(r.left, y); cubicTo(r.left + r.width * 0.3f, y + comba, r.right - r.width * 0.3f, y + comba, r.right, y) }
                translate(0f, bh * 0.03f) { drawPath(fleje, t.s(0.3f), style = Stroke(bh * 0.03f)) }
                drawPath(fleje, t.c(HIERRO), style = Stroke(bh * 0.052f))
                translate(0f, -bh * 0.02f) { drawPath(fleje, t.b(0.35f), style = Stroke(0.8f * u)) }
                drawOval(t.c(OXIDO).copy(alpha = 0.6f), Offset(r.left + r.width * (0.15f + 0.2f * i), y - bh * 0.01f), Size(r.width * 0.16f, bh * 0.045f))
                clavo(Offset(pie.x - bw * 0.12f, y + comba * 0.72f), 1.2f * u, t, mezcla(HIERRO, LATA, 0.4f))
            }
            if (der) drawRect(t.b(0.1f), Offset(pie.x, top), Size(bw * 0.3f, bh)) else drawRect(t.s(0.12f), Offset(pie.x + bw * 0.2f, top), Size(bw * 0.4f, bh * 1.1f))
        }
        // La boca del barril: el canto de las duelas, el hueco oscuro y la tapa corrida.
        val aro = Rect(pie.x - bw * boca, top - bh * 0.075f, pie.x + bw * boca, top + bh * 0.075f)
        drawOval(tt.c(mezcla(PINO_OSC, TABLON, 0.2f)), aro.topLeft, aro.size)
        val dentro = Rect(aro.left + bw * 0.03f, aro.top + bh * 0.012f, aro.right - bw * 0.03f, aro.bottom - bh * 0.012f)
        drawOval(tt.c(mezcla(TABLON, TINTA, 0.6f)), dentro.topLeft, dentro.size)
        drawArc(tt.b(0.5f), 180f, 180f, false, aro.topLeft, aro.size, style = Stroke(0.9f * u))
        val tapa = Rect(dentro.left - bw * 0.07f, dentro.top - bh * 0.03f, dentro.right - bw * 0.09f, dentro.bottom - bh * 0.022f)
        val tapaP = Path().apply { addOval(tapa) }
        translate(w * 0.006f, bh * 0.02f) { drawPath(tapaP, tt.s(0.35f)) }
        drawPath(tapaP, tt.c(mezcla(PINO, PINO_LUZ, 0.5f)))
        clipPath(tapaP) {
            listOf(0.3f, 0.52f, 0.76f).forEach { fx -> drawLine(tt.s(0.3f), Offset(tapa.left + tapa.width * fx, tapa.top), Offset(tapa.left + tapa.width * (fx + 0.03f), tapa.bottom), 0.9f * u) }
            drawRect(tt.b(0.25f), tapa.topLeft, Size(tapa.width * 0.4f, tapa.height))
            drawRect(tt.s(0.18f), Offset(tapa.left + tapa.width * 0.7f, tapa.top), Size(tapa.width * 0.3f, tapa.height))
        }
        drawArc(tt.s(0.4f), 0f, 180f, false, tapa.topLeft, tapa.size, style = Stroke(1.6f * u))
        // El travesaño de la tapa, con sus dos clavos.
        val tv = Rect(tapa.left + tapa.width * 0.16f, tapa.center.y - bh * 0.016f, tapa.right - tapa.width * 0.16f, tapa.center.y + bh * 0.012f)
        drawRect(tt.s(0.3f), Offset(tv.left + 1.4f * u, tv.top + 2f * u), tv.size)
        drawRect(tt.c(mezcla(PINO, PINO_OSC, 0.3f)), tv.topLeft, tv.size)
        drawRect(tt.b(0.45f), tv.topLeft, Size(tv.width, 0.9f * u))
        listOf(tv.left + tv.width * 0.12f, tv.right - tv.width * 0.12f).forEach { x -> clavo(Offset(x, tv.center.y), 1f * u, tt) }
    }

    // ---------------------------------------------------------------- los compartimentos

    /** Revuelve la semilla de un tema (su `hashCode`, que en temas parecidos se parece) para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 16)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 15)
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
        val dia = if (oscuro) 0f else 0.08f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.025f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < FOCO.x)
        fondoHueco(w, h, t, der)
        if (c.vacio) { vacio(w, h, c, t, der); return }
        val m = mezclar(c.semilla)
        // Al tocarlo, sale hacia quien mira: crece un poco desde arriba (el rótulo baja con él).
        scale(1f + CRECE * ab, Offset(w / 2, 0f)) {
            when {
                c.sinTema -> cesta(w, h, c, t, der)
                else -> when (Math.floorMod(m, 3)) {
                    1 -> cajetin(w, h, c, m, t, der)
                    0 -> tarro(w, h, c, m, t, der)
                    else -> lata(w, h, c, m, t, der)
                }
            }
        }
    }

    /**
     * El fondo de un hueco: las tablas del fondo de la cajonera, tendidas, la sombra de la tabla de arriba en escalones, el
     * costado que no mira a la luz más hondo y la tabla del piso con su canto. De día, la fila de arriba un poco más clara; de
     * noche, el quinqué según lo cerca que esté.
     */
    private fun DrawScope.fondoHueco(w: Float, h: Float, t: Tono, der: Boolean) {
        val u = density
        drawRect(t.c(mezcla(PINO_OSC, TABLON, 0.45f)), Offset.Zero, Size(w, h))
        drawRect(t.b(0.1f), Offset(if (der) w * 0.55f else 0f, 0f), Size(w * 0.45f, h))
        // Las juntas de las tablas del fondo.
        listOf(0.3f, 0.6f).forEach { fy ->
            drawRect(t.s(0.3f), Offset(0f, h * fy), Size(w, 1f * u))
            drawRect(t.b(0.16f), Offset(0f, h * fy + 1f * u), Size(w, 0.7f * u))
        }
        // La sombra de la tabla de arriba, en escalones, y los costados.
        listOf(0.36f to 0.1f, 0.22f to 0.12f, 0.09f to 0.24f).forEach { (a, al) -> drawRect(t.s(al), Offset.Zero, Size(w, h * a)) }
        drawRect(t.s(0.22f), Offset(if (der) 0f else w * 0.93f, 0f), Size(w * 0.07f, h))
        drawRect(t.s(0.14f), Offset(if (der) w * 0.975f else 0f, 0f), Size(w * 0.025f, h))
        // La tabla del piso.
        val piso = h * PISO
        drawRect(t.c(mezcla(PINO, PINO_LUZ, 0.4f)), Offset(0f, piso), Size(w, h - piso))
        drawRect(t.b(0.5f), Offset(0f, piso), Size(w, 1f * u))
        drawRect(t.s(0.2f), Offset(0f, h - 1.4f * u), Size(w, 1.4f * u))
    }

    /** La caja de la etiqueta, en píxeles del compartimento. */
    private fun DrawScope.cajaRotulo(w: Float, h: Float) = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)

    /** El papel de un rótulo: de día, el suyo; de noche, hundido hacia [GACETA_LEJOS] (nunca más, para que la tinta se lea) y encendido cerca del quinqué. */
    private fun papelRotulo(t: Tono, papel: Color): Color =
        if (t.o) mezcla(mezcla(papel, GACETA_LEJOS, 0.9f), mezcla(papel, LUZ, 0.1f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(papel)

    /**
     * La etiqueta de papel de gaceta del tema, con su sombra y su filete; de noche, más hundida cuanto más lejos del quinqué.
     * [cinta]: pegada con dos tiras de cinta amarillenta en las esquinas de arriba.
     */
    private fun DrawScope.etiqueta(w: Float, h: Float, t: Tono, papel: Color = GACETA, cinta: Boolean = false, filete: Boolean = true) {
        val u = density
        val r = cajaRotulo(w, h)
        drawRect(t.s(0.26f), Offset(r.left + 1.2f * u, r.top + 1.6f * u), r.size)
        drawRect(papelRotulo(t, papel), r.topLeft, r.size)
        if (filete) {
            drawLine(TINTA.copy(alpha = 0.5f), Offset(r.left + 3f * u, r.top + 2.4f * u), Offset(r.right - 3f * u, r.top + 2.4f * u), 0.9f * u)
            drawLine(TINTA.copy(alpha = 0.5f), Offset(r.left + 3f * u, r.bottom - 2.4f * u), Offset(r.right - 3f * u, r.bottom - 2.4f * u), 0.9f * u)
        }
        drawRect(t.s(0.1f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
        if (cinta) listOf(r.left to -24f, r.right to 24f).forEach { (x, g) ->
            val p = Offset(x, r.top)
            rotate(g, p) {
                val tira = Rect(p.x - 9f * u, p.y - 3.4f * u, p.x + 9f * u, p.y + 3.4f * u)
                drawRect(t.c(CINTA).copy(alpha = 0.85f), tira.topLeft, tira.size)
                drawRect(t.b(0.3f), tira.topLeft, Size(tira.width, 0.8f * u))
            }
        }
    }

    /**
     * Los recortes que asoman de pie entre [x0] y [x1], desde [abajo] hacia arriba: más y más altos cuanto más guarda, cada uno
     * con su cabecera o sus renglones de imprenta, alguno amarillento.
     */
    private fun DrawScope.recortes(x0: Float, x1: Float, abajo: Float, h: Float, c: EstadoCajon, t: Tono, alto: Float = 1f) {
        val u = density
        val n = c.n.coerceIn(0, 10)
        val cuantos = (1 + n / 2).coerceIn(1, 5)
        val sube = h * (0.05f + 0.013f * n) * alto
        for (i in 0 until cuantos) {
            val f = (i + 0.5f) / cuantos
            val pw = (x1 - x0) * (0.26f + 0.14f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - pw) * (f * 0.92f + 0.05f * azar(c.semilla, i + 30))
            val y = abajo - sube * (0.5f + 0.5f * azar(c.semilla, i))
            val g = (azar(c.semilla, i + 40) - 0.5f) * 7f
            rotate(g, Offset(x + pw / 2, abajo)) {
                drawRect(t.s(0.22f), Offset(x + 1.6f * u, y + 1.6f * u), Size(pw, abajo - y))
                drawRect(t.c(if (i % 3 == 1) mezcla(GACETA, CINTA, 0.45f) else GACETA), Offset(x, y), Size(pw, abajo - y + 2f * u))
                drawRect(t.b(0.45f), Offset(x, y), Size(pw, 0.9f * u))
                drawRect(t.s(0.12f), Offset(x + pw * 0.86f, y), Size(pw * 0.14f, abajo - y))
                // Lo impreso: una cabecera gruesa o renglones.
                val tinta = t.c(TINTA)
                if (i % 2 == 0) drawRect(tinta.copy(alpha = 0.6f), Offset(x + pw * 0.1f, y + 2.6f * u), Size(pw * 0.6f, 2f * u))
                var yy = y + (if (i % 2 == 0) 6.6f else 3f) * u
                while (yy < abajo - 1f * u) { drawLine(tinta.copy(alpha = 0.36f), Offset(x + pw * 0.1f, yy), Offset(x + pw * 0.86f, yy), 0.6f * u); yy += 2.2f * u }
            }
        }
    }

    /**
     * El cajetín: el cajón de tabla rescatada (pino, azul marino, ladrillo o verde, con la pintura gastada), entreabierto, con
     * los recortes de pie dentro; sus clavos, la etiqueta de gaceta pegada con cinta y el tirador: de cordel, de cuero o una
     * cuchara doblada, según el tema.
     */
    private fun DrawScope.cajetin(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val clase = Math.floorMod(m ushr 13, 4)
        val fr = Rect(w * 0.03f, h * 0.2f, w * 0.97f, h * 0.975f)
        // Lo de dentro: el hueco oscuro del cajón, sus dos costados y la trasera.
        val boca = Rect(fr.left + w * 0.012f, h * 0.085f, fr.right - w * 0.012f, fr.top + 1f)
        drawRect(t.c(mezcla(TABLON, TINTA, 0.55f)), boca.topLeft, boca.size)
        drawRect(t.c(mezcla(PINO, PINO_OSC, 0.4f)), boca.topLeft, Size(boca.width, h * 0.022f))
        drawRect(t.b(0.4f), boca.topLeft, Size(boca.width, 0.9f * u))
        listOf(boca.left, boca.right - w * 0.022f).forEach { x ->
            drawRect(t.c(mezcla(PINO_LUZ, PINO, 0.4f)), Offset(x, boca.top), Size(w * 0.022f, boca.height))
            drawRect(t.s(0.25f), Offset(if (x == boca.left) x + w * 0.022f else x - 1.2f * u, boca.top), Size(1.2f * u, boca.height))
        }
        recortes(boca.left + w * 0.04f, boca.right - w * 0.04f, fr.top + 1f, h, c, t)
        // El frente.
        val pintura = when (clase) { 1 -> mezcla(MARINO, PINO, 0.12f); 2 -> mezcla(LADRILLO, PINO, 0.22f); 3 -> mezcla(SALVIA, PINO, 0.12f); else -> mezcla(PINO_LUZ, PINO, 0.25f) }
        drawRect(t.s(0.3f), Offset(fr.left + 1.5f * u, fr.top + 2f * u), fr.size)
        drawRect(t.c(pintura), fr.topLeft, fr.size)
        vetas(fr, (if (t.o) NOCHE else PINO_OSC).copy(alpha = if (clase == 0) 0.36f else 0.2f), c.semilla, 0.7f, grosor = 0.6f, ondulacion = 0.6f)
        clipRect(fr.left, fr.top, fr.right, fr.bottom) {
            if (clase != 0) {
                // La pintura gastada: la madera que asoma en los cantos y donde se agarra.
                desgaste(fr, t.c(mezcla(PINO, PINO_LUZ, 0.4f)), c.semilla + 3, 26, false)
                desgaste(Rect(fr.left, fr.top, fr.right, fr.top + fr.height * 0.16f), t.c(PINO_LUZ), c.semilla + 5, 12, false, 0.7f)
                desgaste(Rect(w * 0.33f, h * 0.72f, w * 0.67f, h * 0.95f), t.c(mezcla(PINO, PINO_LUZ, 0.4f)), c.semilla + 7, 16, false, 0.7f)
            } else {
                // Un nudo de la madera y la mugre de las manos.
                val nudo = Offset(fr.left + fr.width * (0.12f + 0.1f * azar(c.semilla, 91)), fr.top + fr.height * 0.82f)
                drawOval(t.c(PINO_OSC), Offset(nudo.x - w * 0.02f, nudo.y - h * 0.018f), Size(w * 0.04f, h * 0.036f))
                drawOval(t.s(0.3f), Offset(nudo.x - w * 0.009f, nudo.y - h * 0.008f), Size(w * 0.018f, h * 0.016f))
                drawOval(t.s(0.1f), Offset(w * 0.36f, h * 0.72f), Size(w * 0.28f, h * 0.22f))
            }
            if (der) { drawRect(t.b(0.14f), Offset(fr.right - fr.width * 0.2f, fr.top), Size(fr.width * 0.2f, fr.height)); drawRect(t.s(0.16f), fr.topLeft, Size(fr.width * 0.1f, fr.height)) }
            else { drawRect(t.b(0.12f), fr.topLeft, Size(fr.width * 0.2f, fr.height)); drawRect(t.s(0.16f), Offset(fr.right - fr.width * 0.1f, fr.top), Size(fr.width * 0.1f, fr.height)) }
        }
        // El canto de arriba de la tabla (el grueso, con su luz), los cantos y los clavos.
        drawRect(t.c(mezcla(PINO_LUZ, GACETA, 0.2f)), fr.topLeft, Size(fr.width, h * 0.022f))
        drawRect(t.b(0.5f), fr.topLeft, Size(fr.width, 1f * u))
        drawRect(t.s(0.3f), Offset(fr.left, fr.top + h * 0.022f), Size(fr.width, 1f * u))
        drawRect(t.b(0.28f), fr.topLeft, Size(1.1f * u, fr.height))
        drawRect(t.s(0.3f), Offset(fr.right - 1.3f * u, fr.top), Size(1.3f * u, fr.height))
        drawRect(t.s(0.38f), Offset(fr.left, fr.bottom - 1.5f * u), Size(fr.width, 1.5f * u))
        listOf(fr.left + w * 0.028f, fr.right - w * 0.028f).forEach { x -> listOf(fr.top + h * 0.075f, fr.bottom - h * 0.05f).forEach { y -> clavo(Offset(x, y), 1.2f * u, t) } }
        filo(fr, t, der)
        etiqueta(w, h, t, cinta = true)
        // El tirador.
        val tc = Offset(w * 0.5f, h * 0.795f)
        when (clase) {
            1 -> { // una tira de cuero doblada, clavada con dos tachuelas de latón
                val cuero = mezcla(LADRILLO, TABLON, 0.55f)
                val a = w * 0.064f
                val tira = Path().apply {
                    moveTo(tc.x - a, tc.y - h * 0.05f); lineTo(tc.x + a, tc.y - h * 0.05f)
                    lineTo(tc.x + a * 0.86f, tc.y + h * 0.08f)
                    cubicTo(tc.x + a * 0.86f, tc.y + h * 0.15f, tc.x - a * 0.86f, tc.y + h * 0.15f, tc.x - a * 0.86f, tc.y + h * 0.08f)
                    close()
                }
                translate((if (der) -2.4f else 2.4f) * u, 3f * u) { drawPath(tira, t.s(0.38f)) }
                drawPath(tira, t.c(cuero))
                clipPath(tira) {
                    drawRect(if (der) t.s(0.2f) else t.b(0.22f), Offset(tc.x - a, tc.y - h * 0.05f), Size(a * 0.6f, h * 0.2f))
                    drawRect(if (der) t.b(0.22f) else t.s(0.22f), Offset(tc.x + a * 0.4f, tc.y - h * 0.05f), Size(a * 0.6f, h * 0.2f))
                    // El doblez de la tira (va clavada arriba y cuelga en lazo) y el brillo del cuero sobado, abajo.
                    drawRect(t.s(0.3f), Offset(tc.x - a, tc.y + h * 0.02f), Size(a * 2, 1.4f * u))
                    drawRect(t.b(0.25f), Offset(tc.x - a, tc.y + h * 0.02f + 1.4f * u), Size(a * 2, 1f * u))
                    drawOval(t.b(0.22f), Offset(tc.x - a * 0.5f, tc.y + h * 0.075f), Size(a, h * 0.045f))
                }
                // El pespunte.
                drawPath(Path().apply {
                    moveTo(tc.x - a * 0.8f, tc.y - h * 0.035f); lineTo(tc.x - a * 0.68f, tc.y + h * 0.08f)
                    cubicTo(tc.x - a * 0.66f, tc.y + h * 0.13f, tc.x + a * 0.66f, tc.y + h * 0.13f, tc.x + a * 0.68f, tc.y + h * 0.08f); lineTo(tc.x + a * 0.8f, tc.y - h * 0.035f)
                }, t.c(mezcla(CUERDA, GACETA, 0.3f)).copy(alpha = 0.7f), style = Stroke(0.8f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 2f * u))))
                listOf(-0.5f, 0.5f).forEach { fx -> clavo(Offset(tc.x + a * fx, tc.y - h * 0.018f), 1.9f * u, t, LATON) }
            }
            2 -> { // una cuchara doblada, clavada por los dos cabos
                val a0 = Offset(tc.x - w * 0.1f, tc.y); val a1 = Offset(tc.x + w * 0.125f, tc.y)
                val arco = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + w * 0.03f, a0.y + h * 0.15f, a1.x - w * 0.04f, a1.y + h * 0.15f, a1.x, a1.y) }
                translate((if (der) -2.4f else 2.4f) * u, 3.4f * u) { drawPath(arco, t.s(0.38f), style = Stroke(4.6f * u, cap = StrokeCap.Round)) }
                drawPath(arco, t.c(LATA), style = Stroke(4.8f * u, cap = StrokeCap.Round))
                drawPath(arco, t.s(0.25f), style = Stroke(1.6f * u))
                translate(0f, -1.2f * u) { drawPath(arco, t.b(0.6f), style = Stroke(1f * u)) }
                // La pala de la cuchara, aplastada contra la tabla, y el cabo del mango.
                val pala = Path().apply {
                    moveTo(a0.x + w * 0.02f, a0.y)
                    cubicTo(a0.x - w * 0.005f, a0.y - h * 0.075f, a0.x - w * 0.1f, a0.y - h * 0.07f, a0.x - w * 0.11f, a0.y)
                    cubicTo(a0.x - w * 0.1f, a0.y + h * 0.07f, a0.x - w * 0.005f, a0.y + h * 0.075f, a0.x + w * 0.02f, a0.y)
                    close()
                }
                translate((if (der) -2f else 2f) * u, 2.6f * u) { drawPath(pala, t.s(0.34f)) }
                drawPath(pala, t.c(LATA))
                // El hueco de la pala, con su sombra por dentro y el filo de luz en el borde.
                drawOval(t.s(0.2f), Offset(a0.x - w * 0.095f, a0.y - h * 0.045f), Size(w * 0.085f, h * 0.09f))
                drawArc(t.b(0.45f), 20f, 130f, false, Offset(a0.x - w * 0.09f, a0.y - h * 0.04f), Size(w * 0.075f, h * 0.08f), style = Stroke(1f * u))
                drawPath(pala, t.b(0.6f), style = Stroke(0.9f * u))
                // El cabo del mango, con su remate, aplastado también.
                val cabo = Rect(a1.x - w * 0.012f, a1.y - h * 0.04f, a1.x + w * 0.03f, a1.y + h * 0.04f)
                translate((if (der) -2f else 2f) * u, 2.6f * u) { drawOval(t.s(0.34f), cabo.topLeft, cabo.size) }
                drawOval(t.c(LATA), cabo.topLeft, cabo.size)
                drawOval(t.b(0.5f), cabo.topLeft, cabo.size, style = Stroke(0.8f * u))
                clavo(Offset(a0.x - w * 0.085f, a0.y - h * 0.012f), 1.3f * u, t)
                clavo(Offset(cabo.center.x, cabo.center.y), 1.3f * u, t)
                destello(Offset(tc.x + (if (der) 1f else -1f) * w * 0.03f, tc.y + h * 0.072f), 1.1f * u, t)
            }
            else -> { // un lazo de cordel, con sus dos nudos
                val a0 = Offset(tc.x - w * 0.115f, tc.y - h * 0.01f); val a1 = Offset(tc.x + w * 0.115f, tc.y - h * 0.01f)
                val lazo = Path().apply { moveTo(a0.x, a0.y); cubicTo(a0.x + w * 0.025f, a0.y + h * 0.18f, a1.x - w * 0.025f, a1.y + h * 0.18f, a1.x, a1.y) }
                translate((if (der) -2.4f else 2.4f) * u, 3.4f * u) { drawPath(lazo, t.s(0.38f), style = Stroke(4.4f * u, cap = StrokeCap.Round)) }
                drawPath(lazo, t.c(mezcla(CUERDA, PINO_OSC, 0.15f)), style = Stroke(4.8f * u, cap = StrokeCap.Round))
                drawPath(lazo, t.c(mezcla(CUERDA, GACETA, 0.35f)), style = Stroke(1.6f * u))
                // Las vueltas del torcido.
                for (k in 1..11) {
                    val f = k / 12f
                    val p = Offset(a0.x + (a1.x - a0.x) * f + w * 0.05f * (1f - 2f * f) * f * (1f - f), a0.y + h * 0.135f * 4f * f * (1f - f))
                    drawLine(t.s(0.34f), p - Offset(2f * u, 2f * u), p + Offset(2f * u, 2f * u), 0.8f * u)
                }
                // Los dos agujeros de la tabla y el nudo de cada cabo.
                listOf(a0, a1).forEach { p ->
                    drawCircle(t.s(0.6f), 4.4f * u, p)
                    drawCircle(t.c(mezcla(CUERDA, PINO_OSC, 0.15f)), 3.6f * u, p + Offset(0f, 1.2f * u))
                    drawCircle(t.s(0.25f), 3.6f * u, p + Offset(0.8f * u, 1.8f * u), style = Stroke(0.8f * u))
                    drawCircle(t.b(0.45f), 1.3f * u, p + Offset(-1f * u, 0.3f * u))
                }
            }
        }
    }

    /**
     * El frasco de conserva grande: el vidrio panzudo (verdoso, azulado o ámbar), los recortes enrollados dentro (más cuanto
     * más guarda), el brillo largo, el culo grueso, la tapa de tela de cuadros atada con cordel y la etiqueta pegada en la panza.
     */
    private fun DrawScope.tarro(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 13, 4)
        val tela = when (clase) { 0 -> mezcla(LADRILLO, OXIDO, 0.2f); 1 -> MARINO; 2 -> mezcla(MAIZ, OXIDO, 0.2f); else -> mezcla(SALVIA, MARINO, 0.25f) }
        val tinte = when (clase) { 1 -> mezcla(VIDRIO, MARINO, 0.35f); 3 -> mezcla(VIDRIO, MAIZ, 0.5f); else -> VIDRIO }
        val x0 = w * 0.07f; val x1 = w * 0.93f
        val hombro = h * 0.31f
        val cuerpo = Path().apply {
            moveTo(w * 0.24f, hombro)
            cubicTo(w * 0.12f, hombro + h * 0.01f, x0, hombro + h * 0.06f, x0, hombro + h * 0.17f)
            lineTo(x0, piso - h * 0.07f)
            cubicTo(x0, piso - h * 0.01f, x0 + w * 0.02f, piso, x0 + w * 0.06f, piso)
            lineTo(x1 - w * 0.06f, piso)
            cubicTo(x1 - w * 0.02f, piso, x1, piso - h * 0.01f, x1, piso - h * 0.07f)
            lineTo(x1, hombro + h * 0.17f)
            cubicTo(x1, hombro + h * 0.06f, w * 0.88f, hombro + h * 0.01f, w * 0.76f, hombro)
            close()
        }
        val cuello = Rect(w * 0.235f, h * 0.22f, w * 0.765f, hombro + h * 0.012f)
        // Su sombra en el fondo del hueco y en la tabla.
        translate(if (der) -w * 0.03f else w * 0.03f, h * 0.02f) { drawPath(cuerpo, t.s(0.26f)) }
        drawOval(t.s(0.42f), Offset(x0 + (if (der) -w * 0.03f else w * 0.03f), piso - h * 0.025f), Size(x1 - x0, h * 0.055f))
        // El vidrio.
        drawRect(t.c(tinte).copy(alpha = 0.34f), cuello.topLeft, cuello.size)
        drawPath(cuerpo, t.c(mezcla(tinte, TABLON, 0.3f)).copy(alpha = 0.5f))
        clipPath(cuerpo) {
            // Los recortes de la gaceta, de pie dentro y ladeados unos contra otros: papel de periódico envejecido, enrollado
            // fino y atado con cordel o con una goma, con los renglones impresos a lo largo; alguno, doblado en cuatro.
            // Primero los de detrás y luego los de delante; más cuanto más guarda.
            val n = c.n.coerceIn(0, 10)
            val cuantos = (3 + n).coerceIn(3, 10)
            val tinta = t.c(TINTA)
            val renglones = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(7f * u, 2.2f * u, 3.4f * u, 2.2f * u))
            for (pasada in 0..1) for (i in 0 until cuantos) {
                if (i % 2 != pasada) continue
                val doblado = cuantos >= 4 && i % 5 == 2
                val rw = if (doblado) w * 0.105f else w * (0.046f + 0.03f * azar(c.semilla, i + 3))
                val cx = x0 + w * 0.085f + (x1 - x0 - w * 0.17f) * ((i + 0.5f) / cuantos + 0.04f * (azar(c.semilla, i + 7) - 0.5f))
                val top = hombro + h * (if (doblado) 0.07f else 0.02f + 0.14f * azar(c.semilla, i))
                val base = when (i % 3) { 0 -> mezcla(GACETA, CUERDA, 0.42f); 1 -> mezcla(GACETA, CINTA, 0.55f); else -> mezcla(GACETA, PINO, 0.3f) }
                val papel = t.c(if (pasada == 0) mezcla(base, PINO_OSC, 0.14f) else base)
                val rr = Rect(cx - rw / 2, top, cx + rw / 2, piso + 2f * u)
                rotate((azar(c.semilla, i + 11) - 0.5f) * (if (doblado) 10f else 20f), Offset(cx, piso)) {
                    // La sombra que deja en el de al lado.
                    drawRect(t.s(0.24f), Offset(rr.left + (if (der) -2f else 2f) * u, rr.top + 1.5f * u), rr.size)
                    drawRect(papel, rr.topLeft, rr.size)
                    if (doblado) {
                        // Doblado en cuatro: los cantos de las hojas, el doblez del medio, una cabecera y unos renglones.
                        listOf(0.9f, 0.8f).forEach { f -> drawLine(t.s(0.26f), Offset(rr.left + rw * f, rr.top), Offset(rr.left + rw * f, rr.bottom), 0.6f * u) }
                        drawLine(t.s(0.16f), Offset(cx - rw * 0.08f, rr.top), Offset(cx - rw * 0.08f, rr.bottom), 0.8f * u)
                        drawRect(t.b(0.4f), rr.topLeft, Size(rw, 0.9f * u))
                        drawRect(tinta.copy(alpha = 0.6f), Offset(rr.left + rw * 0.1f, rr.top + 3f * u), Size(rw * 0.5f, 2.2f * u))
                        // Los renglones, donde la etiqueta los deja ver: arriba y abajo.
                        listOf(rr.top + 7.6f * u to h * rotulo.top + 2f * u, h * rotulo.bottom + 3f * u to piso - h * 0.06f).forEach { (ya, yb) ->
                            var y = ya
                            while (y < yb) { drawLine(tinta.copy(alpha = 0.38f), Offset(rr.left + rw * 0.1f, y), Offset(rr.left + rw * 0.72f, y), 0.6f * u); y += 2.5f * u }
                        }
                    } else {
                        // El rollo: el lado de luz y el de sombra, el borde del papel a lo largo y la punta de arriba, apretada.
                        drawRect(t.b(0.2f), Offset(if (der) rr.right - rw * 0.36f else rr.left + rw * 0.1f, rr.top), Size(rw * 0.26f, rr.height))
                        drawRect(t.s(0.24f), Offset(if (der) rr.left else rr.right - rw * 0.32f, rr.top), Size(rw * 0.32f, rr.height))
                        // Lo impreso, a lo largo del rollo: renglones a trozos y el negro de un titular.
                        listOf(0.2f, 0.4f, 0.78f).forEachIndexed { q, f ->
                            val x = rr.left + rw * f
                            drawLine(tinta.copy(alpha = 0.4f), Offset(x, rr.top + rw * (0.5f + 0.3f * q)), Offset(x, rr.bottom), 0.7f * u, pathEffect = renglones)
                        }
                        val ty = if (i % 2 == 0) h * rotulo.bottom + h * 0.03f else rr.top + rw * 0.7f
                        drawRect(tinta.copy(alpha = 0.55f), Offset(rr.left + rw * 0.52f, ty), Size(rw * 0.16f, h * 0.075f))
                        drawLine(t.s(0.32f), Offset(cx + rw * 0.2f, rr.top), Offset(cx + rw * 0.2f, rr.bottom), 0.6f * u)
                        if (i % 4 == 1) {
                            // Uno se ve de punta: la espiral del papel, apretada, sin hueco.
                            val punta = Rect(rr.left, top - rw * 0.15f, rr.right, top + rw * 0.15f)
                            drawOval(t.c(mezcla(base, PINO_OSC, 0.34f)), punta.topLeft, punta.size)
                            drawOval(t.s(0.34f), punta.topLeft, punta.size, style = Stroke(0.6f * u))
                            drawOval(t.s(0.3f), Offset(punta.left + rw * 0.18f, punta.top + rw * 0.06f), Size(rw * 0.64f, rw * 0.19f), style = Stroke(0.6f * u))
                            drawOval(t.s(0.3f), Offset(punta.left + rw * 0.34f, punta.top + rw * 0.11f), Size(rw * 0.32f, rw * 0.09f), style = Stroke(0.6f * u))
                        } else {
                            // Los demás, de canto: el corte de arriba, un poco al sesgo, con su filo.
                            drawLine(t.s(0.34f), Offset(rr.left, top + rw * 0.06f), Offset(rr.right, top - rw * 0.02f), 1.2f * u)
                            drawLine(t.b(0.35f), Offset(rr.left, top + rw * 0.06f + 1.2f * u), Offset(rr.right, top - rw * 0.02f + 1.2f * u), 0.7f * u)
                        }
                        // La atadura: una goma o dos vueltas de cordel con su nudo; arriba, donde se ve, y otra abajo.
                        listOf(top + h * 0.045f, piso - h * 0.105f).forEachIndexed { q, y ->
                            if (i % 3 == 0) {
                                drawRect(t.c(mezcla(LADRILLO, OXIDO, 0.35f)), Offset(rr.left - 0.5f * u, y), Size(rw + 1f * u, 1.5f * u))
                            } else {
                                drawRect(t.s(0.28f), Offset(rr.left, y + 2.2f * u), Size(rw, 0.8f * u))
                                drawRect(t.c(mezcla(CUERDA, PINO_OSC, 0.2f)), Offset(rr.left - 0.6f * u, y), Size(rw + 1.2f * u, 2.2f * u))
                                drawRect(t.b(0.35f), Offset(rr.left - 0.6f * u, y), Size(rw + 1.2f * u, 0.6f * u))
                                if (q == 0) {
                                    val nudo = Offset(cx - rw * 0.12f, y + 1.1f * u)
                                    drawLine(t.c(CUERDA), nudo, nudo + Offset(-2.6f * u, 4f * u), 0.9f * u, StrokeCap.Round)
                                    drawLine(t.c(CUERDA), nudo, nudo + Offset(2.2f * u, 4.4f * u), 0.9f * u, StrokeCap.Round)
                                    drawCircle(t.c(CUERDA), 1.5f * u, nudo)
                                }
                            }
                        }
                    }
                }
            }
            // El culo grueso del frasco y la luz que entra por el vidrio.
            drawRect(t.c(tinte).copy(alpha = 0.26f), Offset(x0, piso - h * 0.055f), Size(x1 - x0, h * 0.055f))
            drawRect(t.b(0.4f), Offset(x0, piso - h * 0.055f), Size(x1 - x0, 0.9f * u))
            drawRect(t.c(tinte).copy(alpha = 0.16f), Offset(x0, hombro), Size(x1 - x0, piso - hombro))
            val lx = if (der) x1 - w * 0.11f else x0 + w * 0.04f
            drawRect(t.b(0.16f), Offset(lx, hombro), Size(w * 0.07f, piso - hombro))
            drawRect(t.s(0.2f), Offset(if (der) x0 else x1 - w * 0.08f, hombro), Size(w * 0.08f, piso - hombro))
            drawRect(t.s(0.14f), Offset(x0, hombro), Size(x1 - x0, h * 0.04f))
        }
        drawPath(cuerpo, t.b(0.5f), style = Stroke(1f * u))
        // El brillo largo de la panza, el del hombro y la rosca del cuello.
        val bx = if (der) x1 - w * 0.045f else x0 + w * 0.045f
        drawPath(Path().apply { moveTo(bx, hombro + h * 0.2f); cubicTo(bx - (if (der) -1f else 1f) * w * 0.004f, hombro + h * 0.1f, bx + (if (der) -1f else 1f) * w * 0.02f, hombro + h * 0.06f, bx + (if (der) -1f else 1f) * w * 0.05f, hombro + h * 0.04f) },
            if (t.o) t.b(0.95f) else luzMueble(0.8f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawLine(if (t.o) t.b(0.7f) else luzMueble(0.6f), Offset(bx, piso - h * 0.16f), Offset(bx, piso - h * 0.08f), 2f * u, StrokeCap.Round)
        destello(Offset(if (der) w * 0.72f else w * 0.28f, hombro + h * 0.035f), 1.4f * u, t)
        listOf(0.25f, 0.6f).forEach { f -> drawLine(t.b(0.4f), Offset(cuello.left, cuello.top + cuello.height * f), Offset(cuello.right, cuello.top + cuello.height * f + 1.2f * u), 0.8f * u) }
        // La tapa de tela: el gorro, el vuelo con sus picos y los cuadros.
        val atado = h * 0.235f
        val gorro = Path().apply {
            moveTo(w * 0.2f, atado)
            cubicTo(w * 0.2f, h * 0.04f, w * 0.8f, h * 0.04f, w * 0.8f, atado)
            lineTo(w * 0.87f, h * 0.33f); lineTo(w * 0.78f, h * 0.3f); lineTo(w * 0.7f, h * 0.345f); lineTo(w * 0.6f, h * 0.305f); lineTo(w * 0.5f, h * 0.35f)
            lineTo(w * 0.4f, h * 0.305f); lineTo(w * 0.3f, h * 0.345f); lineTo(w * 0.21f, h * 0.3f); lineTo(w * 0.13f, h * 0.335f)
            close()
        }
        translate(if (der) -w * 0.02f else w * 0.02f, h * 0.02f) { drawPath(gorro, t.s(0.25f)) }
        drawPath(gorro, t.c(tela))
        clipPath(gorro) {
            val paso = w * 0.058f
            var x = w * 0.1f
            while (x < w * 0.9f) { drawRect(t.c(GACETA).copy(alpha = 0.42f), Offset(x, 0f), Size(paso * 0.42f, h * 0.36f)); x += paso }
            var y = h * 0.05f
            while (y < h * 0.36f) { drawRect(t.c(GACETA).copy(alpha = 0.42f), Offset(w * 0.1f, y), Size(w * 0.8f, paso * 0.42f)); y += paso }
            if (der) { drawRect(t.b(0.2f), Offset(w * 0.55f, 0f), Size(w * 0.2f, h * 0.36f)); drawRect(t.s(0.22f), Offset(w * 0.1f, 0f), Size(w * 0.2f, h * 0.36f)) }
            else { drawRect(t.b(0.22f), Offset(w * 0.24f, 0f), Size(w * 0.2f, h * 0.36f)); drawRect(t.s(0.24f), Offset(w * 0.64f, 0f), Size(w * 0.3f, h * 0.36f)) }
            // Los pliegues del vuelo, que salen del cordel.
            for (k in 0 until 8) drawLine(t.s(0.26f), Offset(w * (0.24f + 0.075f * k), atado), Offset(w * (0.18f + 0.092f * k), h * 0.34f), 0.8f * u)
            drawRect(t.s(0.18f), Offset(w * 0.1f, atado), Size(w * 0.8f, h * 0.022f))
        }
        // El cordel, con sus dos vueltas, su lazada y los cabos que cuelgan.
        drawLine(t.s(0.3f), Offset(w * 0.2f, atado + 2f * u), Offset(w * 0.8f, atado + 2f * u), 2f * u)
        drawLine(t.c(CUERDA), Offset(w * 0.195f, atado), Offset(w * 0.805f, atado), 2.4f * u)
        drawLine(t.b(0.4f), Offset(w * 0.2f, atado - 0.8f * u), Offset(w * 0.8f, atado - 0.8f * u), 0.7f * u)
        val lz = Offset(w * 0.6f, atado)
        listOf(-1f, 1f).forEach { s ->
            drawOval(t.c(CUERDA), Offset(lz.x + (if (s < 0) -w * 0.06f else 0f), lz.y - h * 0.035f), Size(w * 0.06f, h * 0.05f), style = Stroke(1.6f * u))
            drawPath(Path().apply { moveTo(lz.x, lz.y); cubicTo(lz.x + s * w * 0.02f, lz.y + h * 0.04f, lz.x + s * w * 0.035f, lz.y + h * 0.06f, lz.x + s * w * 0.03f, lz.y + h * 0.1f) }, t.c(CUERDA), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
        drawCircle(t.c(mezcla(CUERDA, PINO_OSC, 0.2f)), 2.4f * u, lz)
        if (t.o && t.k > 0.06f) {
            // El filo de la llama en el vidrio que mira al quinqué.
            val fx = if (der) x1 - 1.4f * u else x0
            drawRect(LUZ.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), Offset(fx, hombro + h * 0.17f), Size(1.4f * u, piso - hombro - h * 0.24f))
        }
        etiqueta(w, h, t, mezcla(GACETA, CINTA, 0.12f))
    }

    /**
     * La lata de galletas con la marca borrada: la tapa de bisagra levantada (se le ve la hojalata por dentro), los recortes de
     * pie, y el frente pintado (ladrillo, azul, mostaza o verde) con su cenefa, el medallón raspado donde iba la marca, los
     * golpes y el óxido de los cantos; la etiqueta, pegada encima.
     */
    private fun DrawScope.lata(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val clase = Math.floorMod(m ushr 13, 4)
        val laca = when (clase) { 0 -> mezcla(LADRILLO, OXIDO, 0.25f); 1 -> mezcla(MARINO, LATA, 0.1f); 2 -> mezcla(MAIZ, OXIDO, 0.3f); else -> mezcla(SALVIA, MARINO, 0.12f) }
        val oro = if (clase == 2) mezcla(GACETA, MAIZ, 0.2f) else mezcla(MAIZ, GACETA, 0.25f)
        val caja = Rect(w * 0.05f, h * 0.29f, w * 0.95f, piso)
        val cajaP = Path().apply { addRoundRect(RoundRect(caja, CornerRadius(w * 0.018f))) }
        // Su sombra en el fondo y en la tabla.
        translate(if (der) -w * 0.03f else w * 0.03f, h * 0.02f) { drawPath(cajaP, t.s(0.28f)) }
        drawOval(t.s(0.42f), Offset(caja.left + (if (der) -w * 0.03f else w * 0.03f), piso - h * 0.025f), Size(caja.width, h * 0.055f))
        // La tapa levantada, vista por dentro: la hojalata con su reflejo, el borde doblado y la bisagra de alambre.
        val ty = h * 0.045f
        val tapa = poligono(caja.left + w * 0.01f, caja.top + h * 0.02f, caja.right - w * 0.01f, caja.top + h * 0.02f, caja.right - w * 0.045f, ty, caja.left + w * 0.045f, ty)
        translate(if (der) -w * 0.02f else w * 0.02f, h * 0.012f) { drawPath(tapa, t.s(0.26f)) }
        drawPath(tapa, t.c(mezcla(LATA, GACETA, 0.12f)))
        clipPath(tapa) {
            val bx = if (der) w * 0.56f else w * 0.2f
            drawPath(poligono(bx, caja.top + h * 0.02f, bx + w * 0.1f, ty, bx + w * 0.24f, ty, bx + w * 0.14f, caja.top + h * 0.02f), t.b(0.34f))
            drawPath(poligono(bx + w * 0.2f, caja.top + h * 0.02f, bx + w * 0.3f, ty, bx + w * 0.33f, ty, bx + w * 0.23f, caja.top + h * 0.02f), t.b(0.22f))
            drawRect(t.s(0.2f), Offset(if (der) 0f else w * 0.72f, 0f), Size(w * 0.28f, h * 0.4f))
            // El óxido del borde y la sombra de lo que guarda.
            drawRect(t.c(OXIDO).copy(alpha = 0.4f), Offset(0f, ty), Size(w, h * 0.016f))
            drawRect(t.s(0.16f), Offset(0f, caja.top - h * 0.07f), Size(w, h * 0.1f))
        }
        drawPath(tapa, t.c(laca), style = Stroke(1.8f * u))
        drawLine(t.b(0.6f), Offset(caja.left + w * 0.045f, ty), Offset(caja.right - w * 0.045f, ty), 1f * u)
        recortes(caja.left + w * 0.06f, caja.right - w * 0.06f, caja.top + h * 0.02f, h, c, t, 0.9f)
        // El cuerpo de la lata.
        drawPath(cajaP, t.c(laca))
        clipPath(cajaP) {
            val lx = if (der) caja.right - caja.width * 0.2f else caja.left + caja.width * 0.08f
            drawRect(t.b(0.1f), Offset(lx - caja.width * 0.05f, caja.top), Size(caja.width * 0.2f, caja.height))
            drawRect(t.b(0.24f), Offset(lx, caja.top), Size(1.4f * u, caja.height))
            drawRect(t.s(0.22f), Offset(if (der) caja.left else caja.right - caja.width * 0.2f, caja.top), Size(caja.width * 0.2f, caja.height))
            // La cenefa: dos filetes y las cantoneras de adorno.
            val f = Rect(caja.left + w * 0.03f, caja.top + h * 0.075f, caja.right - w * 0.03f, caja.bottom - h * 0.045f)
            drawRoundRect(t.c(oro).copy(alpha = 0.85f), f.topLeft, f.size, CornerRadius(w * 0.012f), style = Stroke(1.1f * u))
            drawRoundRect(t.c(oro).copy(alpha = 0.6f), Offset(f.left + 2.6f * u, f.top + 2.6f * u), Size(f.width - 5.2f * u, f.height - 5.2f * u), CornerRadius(w * 0.008f), style = Stroke(0.6f * u))
            listOf(f.topLeft, Offset(f.right, f.top), Offset(f.left, f.bottom), f.bottomRight).forEach { p ->
                val sx = if (p.x < w / 2) 1f else -1f; val sy = if (p.y < (f.top + f.bottom) / 2) 1f else -1f
                drawArc(t.c(oro).copy(alpha = 0.85f), if (sx > 0) (if (sy > 0) 0f else 270f) else (if (sy > 0) 90f else 180f), 90f, false,
                    Offset(p.x - w * 0.03f, p.y - w * 0.03f), Size(w * 0.06f, w * 0.06f), style = Stroke(1f * u))
                drawCircle(t.c(oro).copy(alpha = 0.85f), 1.5f * u, p + Offset(sx * w * 0.035f, sy * w * 0.035f))
            }
            // El medallón donde iba la marca, raspado: quedan el óvalo y unos restos; lo demás es hojalata a la vista.
            val md = Rect(w * 0.3f, caja.top + h * 0.088f, w * 0.7f, caja.top + h * 0.2f)
            drawOval(t.c(oro).copy(alpha = 0.5f), md.topLeft, md.size)
            drawOval(t.c(mezcla(laca, TINTA, 0.25f)), Offset(md.left + 2f * u, md.top + 2f * u), Size(md.width - 4f * u, md.height - 4f * u))
            pinceladas(Rect(md.left - w * 0.04f, md.top - h * 0.01f, md.right + w * 0.04f, md.bottom + h * 0.02f), t.c(LATA), c.semilla + 11, 13, u, 0.75f)
            // Los golpes y el óxido de abajo y de los cantos.
            for (k in 0 until 5) drawOval(t.c(if (k % 2 == 0) LATA else mezcla(LATA, OXIDO, 0.6f)), Offset(caja.left + caja.width * (if (k % 2 == 0) 0.005f else 0.93f) + w * 0.012f * azar(m, k), caja.top + caja.height * (0.1f + 0.75f * azar(m + 1, k))), Size(w * 0.03f, h * 0.03f))
            drawRect(t.c(OXIDO).copy(alpha = 0.45f), Offset(caja.left, caja.bottom - h * 0.03f), Size(caja.width, h * 0.03f))
            pinceladas(Rect(caja.left, caja.bottom - h * 0.12f, caja.right, caja.bottom), t.c(OXIDO), c.semilla + 13, 7, u, 0.4f)
        }
        // El borde de arriba de la lata (el reborde doblado) y la bisagra.
        drawRoundRect(t.c(mezcla(laca, TINTA, 0.2f)), Offset(caja.left - w * 0.004f, caja.top), Size(caja.width + w * 0.008f, h * 0.036f), CornerRadius(h * 0.012f))
        drawRect(t.b(0.5f), Offset(caja.left, caja.top), Size(caja.width, 1f * u))
        drawRect(t.s(0.3f), Offset(caja.left, caja.top + h * 0.036f), Size(caja.width, 1.2f * u))
        filo(caja, t, der)
        etiqueta(w, h, t, mezcla(GACETA, CUERDA, 0.12f), filete = Math.floorMod(m ushr 9, 2) == 0)
        // La etiqueta, pegada con engrudo: una esquina que se despega.
        val r = cajaRotulo(w, h)
        val e = r.height * 0.24f
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right - e * 0.15f, r.bottom - e * 0.15f), t.c(mezcla(CUERDA, GACETA, 0.45f)))
        drawPath(poligono(r.right - e, r.bottom, r.right, r.bottom - e, r.right, r.bottom), t.c(laca))
    }

    /**
     * Lo que no tiene tema: una cesta de mimbre con su asa, el tejido en hiladas, el borde trenzado y los recortes sueltos,
     * revueltos, que se salen; la etiqueta es una tarjeta atada al asa con cordel.
     */
    private fun DrawScope.cesta(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val borde = h * 0.4f
        val mimbre = mezcla(CUERDA, PINO, 0.45f)
        val cuerpo = Path().apply {
            moveTo(w * 0.045f, borde)
            cubicTo(w * 0.05f, borde + (piso - borde) * 0.6f, w * 0.08f, piso - h * 0.02f, w * 0.13f, piso)
            lineTo(w * 0.87f, piso)
            cubicTo(w * 0.92f, piso - h * 0.02f, w * 0.95f, borde + (piso - borde) * 0.6f, w * 0.955f, borde)
            close()
        }
        translate(if (der) -w * 0.03f else w * 0.03f, h * 0.02f) { drawPath(cuerpo, t.s(0.26f)) }
        drawOval(t.s(0.42f), Offset(w * 0.1f + (if (der) -w * 0.03f else w * 0.03f), piso - h * 0.025f), Size(w * 0.8f, h * 0.055f))
        // El asa, por detrás: un aro de mimbre de lado a lado.
        val asa = Path().apply { moveTo(w * 0.1f, borde); cubicTo(w * 0.12f, -h * 0.02f, w * 0.88f, -h * 0.02f, w * 0.9f, borde) }
        translate(if (der) -w * 0.02f else w * 0.02f, h * 0.015f) { drawPath(asa, t.s(0.22f), style = Stroke(4f * u)) }
        drawPath(asa, t.c(mezcla(mimbre, PINO_OSC, 0.25f)), style = Stroke(4.6f * u, cap = StrokeCap.Round))
        drawPath(asa, t.b(0.35f), style = Stroke(1f * u))
        for (k in 1..15) {
            val f = k / 16f
            val p = Offset(w * (0.1f + 0.8f * f) + w * 0.02f * (0.5f - f) * 4f * f * (1f - f), borde - (borde + h * 0.015f) * 3f * f * (1f - f))
            drawLine(t.s(0.26f), p - Offset(1.6f * u, 1.6f * u), p + Offset(1.6f * u, 1.6f * u), 0.7f * u)
        }
        // Los recortes sueltos, revueltos: unos de pie, otros que cuelgan por fuera del borde.
        val n = c.n.coerceIn(0, 10)
        val cuantos = (3 + n / 2).coerceIn(3, 7)
        for (i in 0 until cuantos) {
            val f = (i + 0.5f) / cuantos
            val pw = w * (0.17f + 0.08f * azar(c.semilla, i + 2)); val ph = h * (0.2f + 0.012f * n + 0.08f * azar(c.semilla, i + 4))
            val p = Offset(w * (0.12f + 0.76f * f) + w * 0.03f * (azar(c.semilla, i + 6) - 0.5f), borde + h * 0.04f)
            rotate((azar(c.semilla, i + 8) - 0.5f) * 44f, p) {
                val r = Rect(p.x - pw / 2, p.y - ph, p.x + pw / 2, p.y)
                drawRect(t.s(0.22f), Offset(r.left + 1.8f * u, r.top + 1.8f * u), r.size)
                drawRect(t.c(if (i % 3 == 2) mezcla(GACETA, CINTA, 0.45f) else GACETA), r.topLeft, r.size)
                drawRect(t.b(0.4f), r.topLeft, Size(pw, 0.9f * u))
                drawRect(t.s(0.1f), Offset(r.right - pw * 0.14f, r.top), Size(pw * 0.14f, ph))
                val tinta = t.c(TINTA)
                if (i % 2 == 1) drawRect(tinta.copy(alpha = 0.6f), Offset(r.left + pw * 0.1f, r.top + 3f * u), Size(pw * 0.55f, 2.2f * u))
                var y = r.top + (if (i % 2 == 1) 7.4f else 3.4f) * u
                while (y < r.bottom - 1f * u) { drawLine(tinta.copy(alpha = 0.36f), Offset(r.left + pw * 0.1f, y), Offset(r.right - pw * 0.12f, y), 0.6f * u); y += 2.3f * u }
            }
        }
        // El cuerpo: las hiladas del tejido, que alternan, y las varas de pie.
        drawPath(cuerpo, t.c(mimbre))
        clipPath(cuerpo) {
            val hiladas = 6
            val ah = (piso - borde) / hiladas
            val paso = w * 0.072f
            drawRect(t.s(0.3f), Offset(0f, borde), Size(w, piso - borde))
            for (j in 0 until hiladas) {
                val y = borde + ah * j
                // El mimbre pasa por delante de una vara sí y otra no: cada tramo, un lomo redondo que se hunde en los cabos.
                var x = w * 0.03f + (if (j % 2 == 0) 0f else paso / 2)
                var q = 0
                while (x < w) {
                    drawOval(t.c(if ((q + j) % 3 == 0) mezcla(mimbre, PINO_LUZ, 0.35f) else mimbre), Offset(x - paso * 0.56f, y + ah * 0.06f), Size(paso * 1.12f, ah * 0.92f))
                    drawOval(t.b(0.3f), Offset(x - paso * 0.36f, y + ah * 0.16f), Size(paso * 0.66f, ah * 0.24f))
                    x += paso; q++
                }
            }
            if (der) { drawRect(t.b(0.16f), Offset(w * 0.7f, borde), Size(w * 0.2f, piso - borde)); drawRect(t.s(0.2f), Offset(0f, borde), Size(w * 0.16f, piso - borde)) }
            else { drawRect(t.b(0.16f), Offset(w * 0.08f, borde), Size(w * 0.2f, piso - borde)); drawRect(t.s(0.22f), Offset(w * 0.78f, borde), Size(w * 0.22f, piso - borde)) }
            // Un mimbre roto que se sale.
            drawLine(t.c(mezcla(mimbre, GACETA, 0.3f)), Offset(w * 0.16f, borde + ah * 2.5f), Offset(w * 0.11f, borde + ah * 1.7f), 1.4f * u, StrokeCap.Round)
        }
        // El borde trenzado.
        val rollo = Rect(w * 0.03f, borde - h * 0.03f, w * 0.97f, borde + h * 0.045f)
        drawRoundRect(t.s(0.3f), Offset(rollo.left, rollo.top + 2.4f * u), rollo.size, CornerRadius(rollo.height / 2))
        drawRoundRect(t.c(mezcla(mimbre, PINO_LUZ, 0.3f)), rollo.topLeft, rollo.size, CornerRadius(rollo.height / 2))
        clipPath(Path().apply { addRoundRect(RoundRect(rollo, CornerRadius(rollo.height / 2))) }) {
            var x = rollo.left - rollo.height
            while (x < rollo.right) { drawLine(t.s(0.3f), Offset(x, rollo.bottom), Offset(x + rollo.height * 0.9f, rollo.top), 1f * u); x += 4.2f * u }
            drawRect(t.b(0.3f), rollo.topLeft, Size(rollo.width, rollo.height * 0.3f))
            drawRect(t.s(0.2f), Offset(rollo.left, rollo.bottom - rollo.height * 0.3f), Size(rollo.width, rollo.height * 0.3f))
        }
        filo(rollo, t, der)
        // La tarjeta atada con cordel: el cordel baja del borde al ojete.
        val r = cajaRotulo(w, h)
        val ojo = Offset(r.left + r.height * 0.3f, r.center.y)
        drawPath(Path().apply { moveTo(w * 0.13f, borde); cubicTo(w * 0.1f, borde + h * 0.06f, ojo.x - w * 0.03f, ojo.y - h * 0.08f, ojo.x, ojo.y) }, t.c(CUERDA), style = Stroke(1.5f * u))
        // La tarjeta de envío, con sus dos puntas cortadas a la izquierda y el ojete reforzado.
        val e = r.height * 0.28f
        val tarjeta = poligono(r.left + e, r.top, r.right, r.top, r.right, r.bottom, r.left + e, r.bottom, r.left, r.bottom - e, r.left, r.top + e)
        translate(1.4f * u, 1.8f * u) { drawPath(tarjeta, t.s(0.3f)) }
        drawPath(tarjeta, papelRotulo(t, mezcla(GACETA, CUERDA, 0.3f)))
        clipPath(tarjeta) {
            drawRect(t.s(0.1f), Offset(r.right - r.width * 0.05f, r.top), Size(r.width * 0.05f, r.height))
            drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
        }
        drawCircle(t.c(mezcla(LADRILLO, CUERDA, 0.45f)), r.height * 0.13f, ojo)
        drawCircle(t.s(0.6f), r.height * 0.055f, ojo)
        drawPath(Path().apply { moveTo(ojo.x, ojo.y); cubicTo(ojo.x - w * 0.02f, ojo.y + h * 0.03f, ojo.x - w * 0.05f, ojo.y + h * 0.02f, w * 0.085f, borde + h * 0.03f) }, t.c(CUERDA), style = Stroke(1.5f * u))
    }

    /** Un hueco libre: un ovillo de cordel con su cabo suelto y unas tijeras, o dos manzanas y una hoja, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val piso = h * PISO
        val lado = if (der) -1f else 1f
        if (Math.floorMod(c.semilla ushr 2, 2) == 1) {
            // El ovillo de cordel: la bola con sus vueltas cruzadas, el cabo que se desenrolla por la tabla y unas tijeras.
            val r = h * 0.3f
            val oc = Offset(w * 0.36f, piso - r * 0.96f)
            val bola = Rect(oc.x - r, oc.y - r, oc.x + r, oc.y + r)
            translate(lado * w * 0.035f, h * 0.02f) { drawOval(t.s(0.26f), bola.topLeft, bola.size) }
            drawOval(t.s(0.45f), Offset(oc.x - r * 0.9f + lado * w * 0.03f, piso - h * 0.03f), Size(r * 2f, h * 0.06f))
            // El cabo suelto.
            val cabo = Path().apply { moveTo(oc.x + r * 0.6f, piso - h * 0.03f); cubicTo(w * 0.6f, piso + h * 0.01f, w * 0.66f, piso - h * 0.09f, w * 0.75f, piso - h * 0.035f); cubicTo(w * 0.8f, piso - h * 0.005f, w * 0.86f, piso - h * 0.03f, w * 0.92f, piso - h * 0.02f) }
            translate(1.6f * u, 2f * u) { drawPath(cabo, t.s(0.35f), style = Stroke(2.2f * u)) }
            drawPath(cabo, t.c(CUERDA), style = Stroke(2.4f * u, cap = StrokeCap.Round))
            drawOval(t.c(CUERDA), bola.topLeft, bola.size)
            clipPath(Path().apply { addOval(bola) }) {
                // Las vueltas del cordel: dos haces anchos que se cruzan en el frente, cada vuelta con su hilo de sombra.
                listOf(-34f to mezcla(CUERDA, PINO_OSC, 0.18f), 40f to mezcla(CUERDA, GACETA, 0.2f)).forEachIndexed { j, (g, col) ->
                    rotate(g, oc) {
                        val banda = Rect(oc.x - r * 1.1f, oc.y - r * (if (j == 0) 1.1f else 0.5f), oc.x + r * 1.1f, oc.y + r * (if (j == 0) 1.1f else 0.5f))
                        drawRect(t.c(col), banda.topLeft, banda.size)
                        var y = banda.top
                        while (y < banda.bottom) {
                            // Cada vuelta se comba con la bola.
                            val p = Path().apply { moveTo(banda.left, y + r * 0.1f); cubicTo(oc.x - r * 0.4f, y - r * 0.05f, oc.x + r * 0.4f, y - r * 0.05f, banda.right, y + r * 0.1f) }
                            drawPath(p, t.s(0.3f), style = Stroke(0.9f * u))
                            translate(0f, 1.2f * u) { drawPath(p, t.b(0.25f), style = Stroke(0.7f * u)) }
                            y += r * 0.105f
                        }
                    }
                }
                // El volumen: la sombra en dos pasos por el lado que no mira a la luz, y el claro del otro.
                drawPath(creciente(bola, -lado * r * 0.2f, -r * 0.2f), t.s(0.16f))
                drawPath(creciente(bola, -lado * r * 0.42f, -r * 0.42f), t.s(0.18f))
                drawPath(creciente(bola, lado * r * 0.26f, r * 0.3f), t.b(0.2f))
            }
            drawOval(t.s(0.3f), bola.topLeft, bola.size, style = Stroke(0.8f * u))
            // Las tijeras, apoyadas contra el fondo: las dos hojas, el tornillo y los ojos.
            val tj = Offset(w * 0.76f, piso - h * 0.34f)
            listOf(-9f, 9f).forEach { g ->
                rotate(g, tj) {
                    val hoja = poligono(tj.x - w * 0.012f, tj.y, tj.x + w * 0.012f, tj.y, tj.x + w * 0.003f, tj.y - h * 0.34f)
                    translate(lado * 2f * u, 2f * u) { drawPath(hoja, t.s(0.3f)) }
                    drawPath(hoja, t.c(LATA))
                    drawLine(t.b(0.6f), Offset(tj.x - w * 0.008f, tj.y - h * 0.02f), Offset(tj.x + w * 0.001f, tj.y - h * 0.32f), 0.8f * u)
                    val ojo = Rect(tj.x - w * 0.04f, tj.y + h * 0.03f, tj.x + w * 0.02f, tj.y + h * 0.25f)
                    drawLine(t.c(HIERRO), tj, Offset(ojo.center.x + w * 0.012f, ojo.top + h * 0.01f), 2.6f * u)
                    translate(lado * 2f * u, 2f * u) { drawOval(t.s(0.3f), ojo.topLeft, ojo.size, style = Stroke(2.4f * u)) }
                    drawOval(t.c(HIERRO), ojo.topLeft, ojo.size, style = Stroke(2.8f * u))
                    drawArc(t.b(0.5f), 180f, 100f, false, ojo.topLeft, ojo.size, style = Stroke(0.8f * u))
                }
            }
            clavo(tj, 1.6f * u, t, mezcla(LATA, GACETA, 0.3f))
        } else {
            // Dos manzanas (una roja y otra verde, a medio madurar), con su sombra, y una hoja suelta.
            val rm = h * 0.25f
            val a = Offset(w * 0.56f, piso - rm * 0.82f); val b = Offset(w * 0.33f, piso - rm * 0.98f)
            listOf(a to rm * 0.86f, b to rm).forEach { (p, r) ->
                translate(lado * w * 0.035f, h * 0.02f) { drawOval(t.s(0.24f), Offset(p.x - r, p.y - r * 0.92f), Size(r * 2, r * 1.84f)) }
                drawOval(t.s(0.45f), Offset(p.x - r * 0.9f + lado * w * 0.03f, piso - h * 0.03f), Size(r * 2f, h * 0.06f))
            }
            manzana(a, rm * 0.86f, mezcla(SALVIA, MAIZ, 0.35f), t, der)
            manzana(b, rm, mezcla(LADRILLO, OXIDO, 0.2f), t, der)
            val hj = Offset(w * 0.78f, piso - h * 0.035f)
            rotate(-14f, hj) {
                drawOval(t.s(0.3f), Offset(hj.x - w * 0.07f + 1.6f * u, hj.y - h * 0.03f + 2f * u), Size(w * 0.14f, h * 0.06f))
                drawOval(t.c(mezcla(SALVIA, TABLON, 0.25f)), Offset(hj.x - w * 0.07f, hj.y - h * 0.03f), Size(w * 0.14f, h * 0.06f))
                drawLine(t.b(0.4f), Offset(hj.x - w * 0.07f, hj.y), Offset(hj.x + w * 0.075f, hj.y), 0.7f * u)
            }
        }
    }

    override val rotulo = Rect(0.08f, 0.43f, 0.92f, 0.7f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.fell
    override val salida = CRECE * 0.565f
    override val placa = Rect(0.36f, 0.055f, 0.64f, 0.265f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // El fondo forrado de gaceta: columnas de renglones, tachuelas en las esquinas y un cerco de café.
        val g = if (oscuro) mezcla(GACETA, Color.Black, 0.58f) else GACETA
        drawRect(g)
        val col = 3
        val cw = w / col
        for (j in 0 until col) {
            drawRect(TINTA.copy(alpha = 0.35f), Offset(cw * j + 14 * u, 24 * u), Size(cw - 28 * u, 7 * u))
            var y = 40 * u
            while (y < h - 16 * u) { drawLine(TINTA.copy(alpha = 0.12f), Offset(cw * j + 14 * u, y), Offset(cw * (j + 1) - 14 * u, y), 1.2f * u); y += 6 * u }
            if (j > 0) drawLine(TINTA.copy(alpha = 0.2f), Offset(cw * j, 20 * u), Offset(cw * j, h - 14 * u), 0.8f * u)
        }
        drawCircle(Color(0xFF6A4020).copy(alpha = 0.25f), 32 * u, Offset(w * 0.7f, h * 0.65f), style = Stroke(4 * u))
        val pl = 12 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r -> drawRect(t(PINO, oscuro), r.topLeft, r.size) }
        listOf(Offset(pl + 8 * u, pl + 8 * u), Offset(w - pl - 8 * u, pl + 8 * u), Offset(pl + 8 * u, h - pl - 8 * u), Offset(w - pl - 8 * u, h - pl - 8 * u)).forEach { drawCircle(t(LATON, oscuro), 3 * u, it) }
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 16 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) GACETA else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = GenericShape { s, _ -> addPath(contornoIrregular(Rect(0f, 0f, s.width, s.height), semilla, 12, 0.018f)) }

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.6f else 1f
        drawRect(GACETA.copy(alpha = 0.45f))
        // Los filetes de columna arriba y abajo, y una tira de cinta amarillenta.
        drawLine(TINTA.copy(alpha = 0.4f), Offset(8 * u, 6 * u * g), Offset(w - 8 * u, 6 * u * g), 0.8f * u)
        drawLine(TINTA.copy(alpha = 0.3f), Offset(8 * u, h - 6 * u * g), Offset(w - 8 * u, h - 6 * u * g), 0.6f * u)
        val c = Offset(w * (0.4f + 0.2f * azar(semilla, 1)), 2 * u * g)
        rotate(-4f + 8f * azar(semilla, 2), c) { drawRect(CINTA.copy(alpha = 0.6f), Offset(c.x - 14 * u * g, c.y - 2 * u * g), Size(28 * u * g, 7 * u * g)) }
    }

    override val margenes = MargenesFicha(14.dp, 14.dp, 13.dp, 26.dp, 26.dp, 26.dp)
    override val proporcionFicha = 0.9f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            "hecho" -> selloHecho(MARINO, p.color)
            // Un martillo.
            "convence" -> {
                drawLine(PINO_OSC, Offset(c.x - s * 0.3f, c.y + s * 0.4f), Offset(c.x + s * 0.12f, c.y - s * 0.12f), s * 0.12f, StrokeCap.Round)
                rotate(45f, Offset(c.x + s * 0.16f, c.y - s * 0.16f)) { drawRect(MARINO, Offset(c.x - s * 0.04f, c.y - s * 0.26f), Size(s * 0.4f, s * 0.2f)) }
            }
            // Un quinqué.
            "piensa" -> {
                drawOval(LUZ, Offset(c.x - s * 0.18f, c.y - s * 0.4f), Size(s * 0.36f, s * 0.5f))
                drawRect(LATON, Offset(c.x - s * 0.24f, c.y + s * 0.1f), Size(s * 0.48f, s * 0.24f))
                drawCircle(Color(0xFFE07A2A), s * 0.07f, Offset(c.x, c.y - s * 0.1f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(LADRILLO)
            else -> {}
        }
    }

    /** Una tachuela. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(Color.Black.copy(alpha = 0.25f), s * 0.36f, Offset(c.x + s * 0.06f, c.y + s * 0.06f))
        drawCircle(LATON, s * 0.36f, c)
        drawCircle(Color.White.copy(alpha = 0.45f), s * 0.12f, Offset(c.x - s * 0.1f, c.y - s * 0.1f))
    }

    override val desorden = 2.5f
}
