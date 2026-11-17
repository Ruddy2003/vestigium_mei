package io.github.ruddy2003.vestigium.ui.ideario

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

// =====================================================================
//  Templario
// =====================================================================

/**
 * Templario: el rincón de la cámara de un castillo de frontera del Levante, hacia 1250, donde se guardan las arcas de la
 * encomienda y el sargento lleva las cuentas. Es un interior sin ventana: un muro de sillares de caliza dorada con sus juntas
 * gruesas, piedras más oscuras, marcas de cantero, un nicho con una cruz pintada a la almagra, el hollín de la vela y una
 * argolla de hierro empotrada. De día la luz viene de arriba a la izquierda, y una saetera que queda fuera del cuadro deja una
 * franja de sol en la piedra. El mueble son baldas gruesas de roble sobre ménsulas de piedra (a los lados y en medio) y, abajo,
 * un banco corrido de sillería; cada hueco tiene su piso de tabla y la sombra de la balda de arriba. Encima, el punto focal,
 * a la izquierda: el escudo de lágrima con la cruz, colgado de su guige en un gancho, y debajo, sobre la balda, el yelmo de
 * cuenco con nasal sobre la cofia de malla, que cae por el canto. Luego, el candelero de hierro con su vela de sebo, el libro
 * de la regla con su cierre, la matriz del sello y un sello de lacre, la balanza de cambista y la bolsa con unos dineros
 * sueltos; en el rincón, la espada envainada con el cinturón enrollado, apoyada en la pared. En el muro, el manto pardo del
 * sargento doblado sobre una percha (asoma su cruz roja), el paternóster colgado de la punta y un manojo de llaves en un
 * clavo. Al pie, sobre las losas, unas alforjas cortadas por el borde, un par de espuelas de pincho y unas botas, una caída.
 * Cada tema es, según la semilla, un arca de roble (natural o con la almagra desvaída) con flejes y dos candados, un
 * cofrecillo de cuero hervido de tapa abombada con sus herrajes, o unos fajos de cartas atados con cordel y sellos pendientes
 * junto a un estuche de pergaminos; en los tres, la tablilla de yeso con el tema y las cartas lacradas que asoman (más con más
 * fichas). Lo que no tiene tema es un saco de lona con cartas sueltas y la tablilla sin filete; un hueco libre lleva una
 * palmatoria con la vela apagada o un cuenco de madera. Al abrir uno, la tapa se levanta y suben las cartas. De noche todo se
 * hunde en azul negro: alumbra solo la vela de sebo (el halo en la piedra dorada, el brillo en el yelmo y la malla, la cruz
 * del escudo encendida); la primera fila de arcas queda cálida, la segunda a medias y las de abajo casi perdidas. Por dentro,
 * un paño de lana blanca con la cruz. Las fichas son cartas selladas con su sello de cera pendiente.
 */
object IdeasTemplario : DisenoIdeas {
    // ---------------------------------------------------------------- la paleta de la cámara
    /** La caliza dorada del Levante, su luz, su sombra y el mortero de las juntas. */
    private val CALIZA = Color(0xFFD5BA86)
    private val CALIZA_LUZ = Color(0xFFEBD6A8)
    private val CALIZA_OSC = Color(0xFFA88B5C)
    private val MORTERO = Color(0xFF8E7754)
    /** El roble de las baldas y las arcas. */
    private val TABLA = Color(0xFF6E4C2D)
    private val TABLA_LUZ = Color(0xFF9A754C)
    private val TABLA_OSC = Color(0xFF3C2817)
    /** El hierro (yelmo, malla, flejes, candelero) y su brillo. */
    private val FIERRO = Color(0xFF4C4C55)
    private val FIERRO_LUZ = Color(0xFFA3A1AA)
    /** El rojo de la cruz, el pardo del manto, el cuero, el lacre. */
    private val CRUZ = Color(0xFFA82B26)
    private val PARDO = Color(0xFF5C4734)
    private val CUERO_M = Color(0xFF7E4E2C)
    private val CUERO_OSC = Color(0xFF462A1A)
    private val LACRE = Color(0xFF8E2620)
    /** El sebo de la vela, la lona, el bronce, el pergamino de las cartas, la almagra pintada. */
    private val SEBO = Color(0xFFE4D3A0)
    private val LONA = Color(0xFFC2AE88)
    private val BRONCE = Color(0xFFB48C48)
    private val PERG = Color(0xFFEDE2C4)
    private val ALMAGRA = Color(0xFFA2492F)
    /** El yeso de las tablillas; de noche, lejos de la vela (con [TINTA], unos 5.5:1). */
    private val YESO = Color(0xFFF2ECDD)
    private val YESO_LEJOS = Color(0xFF9C9586)
    /** La noche de la cámara (azul negro) y la llama de sebo. */
    private val NOCHE_T = Color(0xFF0B0E24)
    private val LLAMA = Color(0xFFFFBA60)

    // ---------------------------------------------------------------- las del interior y las fichas (sin cambios)
    private val ROBLE = Color(0xFF4E3420)
    private val ROBLE_OSC = Color(0xFF2C1C10)
    private val HIERRO = Color(0xFF1F1E1D)
    private val ROJO = Color(0xFFB0191F)
    private val BLANCO = Color(0xFFF4EFE4)
    private val NEGRO = Color(0xFF1C1A18)
    private val LINO = Color(0xFFECE4D0)
    private val CERA = Color(0xFF8E1A16)
    private val TINTA = Color(0xFF201E1B)

    private fun t(c: Color, oscuro: Boolean) = if (oscuro) mezcla(c, Color.Black, 0.32f) else c

    // ---------------------------------------------------------------- las medidas

    override val cols = 2
    override val proporcion = 1.45f
    override val corona = 0.66f
    override val base = 0.28f
    override val lado = 0.07f
    override val hueco = 0.06f

    /** Mueble rehecho: el aura solo sale al tocar (ver [DisenoIdeas.auraEnReposo]). */
    override val auraEnReposo = false

    /** La cara de arriba de cada balda (y del banco) y el grosor de su canto, en fracciones del ancho. */
    private const val CARA = 0.028f
    private const val CANTO = 0.042f
    /** El piso de cada hueco (donde empieza la cara de la balda) y donde pisa lo guardado, en fracción de su alto. */
    private const val PISO = 0.9f
    private const val PISA = 0.955f
    /** En la base: dónde acaba el frente del banco, en fracción de su alto. */
    private const val BANCO = 0.42f

    // ---------------------------------------------------------------- la luz y la noche

    /** La llama de la vela de sebo, en fracciones del ancho. */
    private val LUZ = Offset(0.47f, 0.49f)

    /** Cómo cae la luz de la vela por las filas de arcas: (alto, cuánta luz). Con 9 temas, de la primera a la quinta fila. */
    private val CAIDA = listOf(0.7f to 0.92f, 0.86f to 0.8f, 1.19f to 0.52f, 1.53f to 0.24f, 1.87f to 0.08f, 2.2f to 0.04f, 2.6f to 0.03f)

    /** Cuánto le llega de la vela a [p] (en fracciones del ancho). */
    private fun luz(p: Offset): Float = maxOf(radial(p), luzCuerpo(p)).coerceIn(0.03f, 1f)

    /** La luz directa de la llama: cae deprisa con la distancia. */
    private fun radial(p: Offset): Float {
        val dx = p.x - LUZ.x
        val dy = (p.y - LUZ.y) * (if (p.y > LUZ.y) 0.85f else 1f)
        return 1.35f * exp(-4.1f * sqrt(dx * dx + dy * dy))
    }

    /** La luz en la pared y en el mueble: la directa, y solo parte de la que baja por las filas (lo guardado la recoge más). */
    private fun luzPared(p: Offset): Float = maxOf(radial(p), 0.6f * luzCuerpo(p)).coerceIn(0.025f, 1f)

    private fun luzCuerpo(p: Offset): Float {
        if (p.y < CAIDA.first().first) return 0f
        var k = CAIDA.last().second
        for (i in 0 until CAIDA.size - 1) {
            val (y0, k0) = CAIDA[i]; val (y1, k1) = CAIDA[i + 1]
            if (p.y in y0..y1) { k = k0 + (k1 - k0) * (p.y - y0) / (y1 - y0); break }
        }
        val lado = (1f - 0.45f * (abs(p.x - LUZ.x) - 0.12f).coerceAtLeast(0f)).coerceIn(0.75f, 1f)
        return k * lado
    }

    /** El color propio de algo, hundido en la noche: oscuro y de su tono, no gris. */
    private fun hondo(c: Color) = Color(c.red * 0.12f + NOCHE_T.red * 0.85f, c.green * 0.12f + NOCHE_T.green * 0.85f, c.blue * 0.12f + NOCHE_T.blue * 0.9f, c.alpha)

    /** Cómo se pinta algo según dónde está: de día tal cual (con [dia], un poco más claro); de noche, según [k]. */
    private class Tono(val o: Boolean, val k: Float, val dia: Float = 0f)

    private fun Tono.c(col: Color): Color {
        if (!o) return if (dia > 0f) mezcla(col, LUZ_MUEBLE, dia) else col
        val v = mezcla(hondo(col), col, (1.05f * k.pow(1.35f)).coerceAtMost(0.92f))
        return mezcla(v, LLAMA, 0.17f * k)
    }

    /** La sombra: de día violeta hondo; de noche, la noche. */
    private fun Tono.s(a: Float) = if (o) NOCHE_T.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else sombraMueble(a)

    /** El brillo de lo que mira a la luz: de día crema; de noche, el filo de la llama. */
    private fun Tono.b(a: Float): Color = if (!o) luzMueble(a) else LLAMA.copy(alpha = (a * 0.95f * k).coerceIn(0f, 1f))

    /** Lo que el mueble que se está pintando sabe: si es de noche y su ancho. */
    private var esNoche = false
    private var anchoW = 1f

    private fun tn(p: Offset): Tono = Tono(esNoche, if (esNoche) luzPared(p / anchoW) else 0f)

    /** De noche la luz viene de la vela: lo que está a su izquierda la recibe por la derecha. */
    private fun luzDerecha(x: Float) = esNoche && x / anchoW < LUZ.x - 0.02f

    // ---------------------------------------------------------------- ayudantes

    private fun poligono(vararg xy: Float): Path = Path().apply {
        moveTo(xy[0], xy[1])
        var i = 2
        while (i < xy.size) { lineTo(xy[i], xy[i + 1]); i += 2 }
        close()
    }

    /** Revuelve la semilla de un tema para repartir bien las tres clases. */
    private fun mezclar(s: Int): Int {
        var x = s * 0x9E3779B1L.toInt()
        x = x xor (x ushr 15)
        x *= 0x85EBCA6BL.toInt()
        return x xor (x ushr 13)
    }

    /**
     * La sombra de algo en la pared: de día corrida a la derecha y abajo; de noche, hacia fuera de la vela, más marcada cuanto
     * más cerca está de ella. No baja de [hasta].
     */
    private fun DrawScope.sombraEnPared(p: Path, desde: Offset, hasta: Float, a: Float = 0.2f) {
        val w = anchoW
        clipRect(0f, 0f, size.width, hasta) {
            if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(p, sombraMueble(a)) }
            else {
                val k = luzPared(desde / w)
                if (k > 0.15f) {
                    val dx = (desde.x / w - LUZ.x).coerceIn(-0.3f, 0.3f) / 0.3f
                    val dy = ((desde.y / w - LUZ.y) / 0.3f).coerceIn(-1f, 1f)
                    translate(w * 0.035f * dx, w * 0.02f * dy + w * 0.006f) { drawPath(p, NOCHE_T.copy(alpha = (0.75f * k).coerceAtMost(0.55f))) }
                }
            }
        }
    }

    /** La sombra de contacto de lo que pisa, corrida a la derecha. */
    private fun DrawScope.contacto(centro: Offset, ancho: Float, alto: Float, a: Float, t: Tono) =
        drawOval(t.s(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))

    /** La luz de un cuerpo redondo de pie dentro de [r] (dentro de su recorte); [der], la luz por la derecha. */
    private fun DrawScope.cilindro(r: Rect, t: Tono, fuerza: Float = 1f, der: Boolean = false) {
        fun x(f: Float, a: Float) = if (der) r.right - r.width * (f + a) else r.left + r.width * f
        drawRect(t.b(0.18f * fuerza), Offset(x(0.04f, 0.3f), r.top), Size(r.width * 0.3f, r.height))
        drawRect(t.b(0.32f * fuerza), Offset(x(0.16f, 0.08f), r.top), Size(r.width * 0.08f, r.height))
        drawRect(t.s(0.16f * fuerza), Offset(x(0.58f, 0.42f), r.top), Size(r.width * 0.42f, r.height))
        drawRect(t.s(0.2f * fuerza), Offset(x(0.82f, 0.18f), r.top), Size(r.width * 0.18f, r.height))
    }

    /** Un clavo o remache de hierro con su sombra y su brillo. */
    private fun DrawScope.clavo(p: Offset, r: Float, t: Tono, col: Color = FIERRO) {
        drawCircle(t.s(0.35f), r * 1.05f, p + Offset(r * 0.45f, r * 0.5f))
        drawCircle(t.c(col), r, p)
        drawCircle(t.b(0.55f), r * 0.4f, p - Offset(r * 0.3f, r * 0.3f))
    }

    /** El filo de la vela en un recipiente, de noche: el canto de arriba y el costado que mira a la luz. */
    private fun DrawScope.filo(r: Rect, t: Tono, der: Boolean) {
        if (!t.o || t.k < 0.08f) return
        val u = density
        drawRect(LLAMA.copy(alpha = (0.8f * t.k).coerceAtMost(0.7f)), r.topLeft, Size(r.width, 1.4f * u))
        val x = if (der) r.right - 1.4f * u else r.left
        drawRect(LLAMA.copy(alpha = (0.55f * t.k).coerceAtMost(0.5f)), Offset(x, r.top), Size(1.4f * u, r.height))
    }

    /** Un destello en el metal: de día crema; de noche, la llama. */
    private fun DrawScope.destello(c: Offset, r: Float, t: Tono) {
        if (t.o) { if (t.k > 0.08f) { drawCircle(LLAMA.copy(alpha = (0.3f * t.k).coerceAtMost(0.28f)), r * 2.4f, c); drawCircle(Color(0xFFFFEFC8).copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c) } }
        else drawCircle(luzMueble(0.8f), r, c)
    }

    /** Una carta doblada y lacrada, de pie, de [r]: el pergamino, su doblez y el lacre. */
    private fun DrawScope.carta(r: Rect, t: Tono, s: Int) {
        val u = density
        val col = when (Math.floorMod(s, 3)) { 0 -> PERG; 1 -> mezcla(PERG, LONA, 0.35f); else -> mezcla(PERG, CALIZA, 0.3f) }
        drawRect(t.c(col), r.topLeft, r.size)
        drawRect(t.b(0.4f), r.topLeft, Size(r.width, 0.8f * u))
        drawRect(t.s(0.18f), Offset(r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height))
        drawLine(t.s(0.2f), Offset(r.left, r.top + r.height * 0.35f), Offset(r.right, r.top + r.height * 0.38f), 0.6f * u)
        val sello = Offset(r.center.x, r.top + r.height * 0.22f)
        val rr = minOf(r.width * 0.2f, 3.2f * u)
        drawCircle(t.c(LACRE), rr, sello)
        drawCircle(t.b(0.35f), rr * 0.35f, sello - Offset(rr * 0.3f, rr * 0.3f))
    }

    // ---------------------------------------------------------------- el mueble entero

    override fun DrawScope.mueble(d: Disposicion, oscuro: Boolean) {
        val u = density
        esNoche = oscuro
        val w = size.width; val alto = size.height
        anchoW = w
        val b = d.base; val hb = b.height
        val banco = b.top + hb * BANCO
        val ultima = d.filas.last()
        val pie = d.corona.bottom - w * 0.013f

        // ---- el muro de sillares, su luz y lo que tiene grabado o empotrado
        muro(w, banco)
        if (!oscuro) luzDeDia(w, d.corona.bottom - w * CARA)
        d.huecos.forEach { r -> fondoHueco(r) }
        nicho(Rect(w * 0.375f, w * 0.055f, w * 0.455f, w * 0.205f))
        hollin(w)
        argolla(Offset(w * 0.036f, d.filas.first().top + (d.filas.first().height) * 0.45f))

        // ---- lo colgado: el escudo, la percha con el manto y el paternóster, las llaves
        escudo(w)
        manto(w)
        llaves(Offset(w * 0.528f, w * 0.205f))

        // ---- las baldas sobre sus ménsulas (la de arriba y una bajo cada fila menos la última) y el banco
        balda(d.corona.bottom - w * CARA, d.corona.bottom, w)
        d.filas.dropLast(1).forEach { f -> balda(f.bottom - w * CARA, f.bottom, w) }
        bancoCorrido(ultima.bottom, banco, w)

        // ---- lo de encima de la balda alta, de atrás adelante
        espada(Offset(w * 0.958f, pie), w)
        Offset(w * 0.78f, pie).let { p -> scale(1.35f, p) { balanza(p, w) } }
        Offset(w * 0.878f, pie + w * 0.003f).let { p -> scale(1.35f, p) { bolsa(p, w) } }
        Offset(w * 0.512f, pie + w * 0.004f).let { p -> scale(1.35f, p) { libro(p, w) } }
        Offset(w * 0.695f, pie + w * 0.002f).let { p -> scale(1.35f, p) { sello(p, w) } }
        candelero(Offset(w * LUZ.x, pie), w)
        yelmo(Offset(w * 0.34f, pie), d.corona.bottom, w)

        // ---- el suelo de losas y lo del pie
        suelo(w, banco, alto)
        alforjas(w, banco, alto)
        Offset(w * 0.47f, alto - w * 0.05f).let { p -> scale(1.5f, p) { espuelas(p, w) } }
        botas(w, alto)

        // ---- la placa de «y N más»: una lápida lisa en el frente del banco
        if (d.mas > 0) placa.let { pl ->
            val r = d.enBase(pl)
            val t = tn(r.center)
            drawRect(t.s(0.35f), Offset(r.left + 1.6f * u, r.top + 2f * u), r.size)
            drawRect(if (oscuro) YESO_LEJOS else mezcla(CALIZA_LUZ, YESO, 0.45f), r.topLeft, r.size)
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(t.s(0.25f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), style = Stroke(0.7f * u))
        }

        // ---- la luz de la vela, por encima de lo que tiene cerca
        if (oscuro) {
            val llama = LUZ * w
            mancha(llama + Offset(0f, w * 0.02f), w * 0.62f, LLAMA.copy(alpha = 0.13f))
            mancha(llama, w * 0.3f, LLAMA.copy(alpha = 0.22f))
            mancha(llama, w * 0.11f, LLAMA.copy(alpha = 0.38f))
            llamaDeSebo(llama + Offset(0f, w * 0.01f), w * 0.0105f)
        }
        granoMueble(Rect(0f, 0f, w, alto), oscuro)
    }

    // ---------------------------------------------------------------- el muro

    /** El muro de sillares hasta [hasta]: hiladas de piedras de largo distinto, juntas gruesas. */
    private fun DrawScope.muro(w: Float, hasta: Float) {
        val u = density
        var y = 0f; var fila = 0
        while (y < hasta) {
            val alto = w * (0.068f + 0.022f * azar(711, fila))
            var x = -w * 0.15f * azar(712, fila); var j = 0
            while (x < w) {
                val largo = w * (0.16f + 0.16f * azar(713 + fila, j))
                sillar(Rect(x, y, x + largo, minOf(y + alto, hasta)), fila * 41 + j)
                x += largo; j++
            }
            y += alto; fila++
        }
        pinceladas(Rect(0f, 0f, w, hasta), if (esNoche) NOCHE_T else CALIZA_OSC, 7101, 60, u, if (esNoche) 0.05f else 0.08f)
        // Las marcas de cantero en tres piedras.
        listOf(Triple(0.13f, 0.6f, 0), Triple(0.62f, 0.43f, 1), Triple(0.965f, 1.45f, 2)).forEach { (fx, fy, tipo) -> marcaCantero(Offset(w * fx, w * fy), w * 0.014f, tipo) }
    }

    /** Un sillar: el mortero de su junta, su tono (alguno más oscuro o más dorado), la labra, sus cantos y algún desconchón. */
    private fun DrawScope.sillar(r: Rect, s: Int) {
        val u = density
        val t = tn(r.center)
        val tt = Tono(t.o, t.k * 0.9f)
        drawRect(tt.c(MORTERO), r.topLeft, r.size)
        val i = Rect(r.left + 1.6f * u, r.top + 1.6f * u, r.right - 1.6f * u, r.bottom - 1.6f * u)
        if (i.width < 2f || i.height < 2f) return
        var tono = mezcla(CALIZA, CALIZA_OSC, 0.04f + 0.36f * azar(s, 1))
        val q = azar(s, 2)
        if (q > 0.86f) tono = mezcla(tono, MORTERO, 0.4f) else if (q > 0.7f) tono = mezcla(tono, Color(0xFFDDB46E), 0.4f)
        drawRect(tt.c(tono), i.topLeft, i.size)
        for (j in 0 until 2) {
            val ow = i.width * (0.3f + 0.4f * azar(s, 10 + j)); val oh = i.height * (0.35f + 0.3f * azar(s, 20 + j))
            drawOval(tt.c(mezcla(tono, if (j == 0) CALIZA_OSC else CALIZA_LUZ, 0.45f)).copy(alpha = 0.42f),
                Offset(i.left + (i.width - ow) * azar(s, 30 + j), i.top + (i.height - oh) * azar(s, 40 + j)), Size(ow, oh))
        }
        drawRect(tt.b(0.3f), i.topLeft, Size(i.width, 1.1f * u))
        drawRect(tt.b(0.16f), i.topLeft, Size(1f * u, i.height))
        drawRect(tt.s(0.26f), Offset(i.left, i.bottom - 1.5f * u), Size(i.width, 1.5f * u))
        drawRect(tt.s(0.18f), Offset(i.right - 1.2f * u, i.top), Size(1.2f * u, i.height))
        // Picaduras.
        for (j in 0 until 3) drawCircle(tt.s(0.28f), 0.8f * u, Offset(i.left + i.width * azar(s, 50 + j), i.top + i.height * azar(s, 60 + j)))
        if (azar(s, 3) > 0.78f) {
            val e = i.height * (0.25f + 0.2f * azar(s, 4))
            val der = azar(s, 5) > 0.5f
            val x = if (der) i.right else i.left
            val sx = if (der) -1f else 1f
            drawPath(poligono(x, i.bottom, x + sx * e * 1.5f, i.bottom, x + sx * e * 0.5f, i.bottom - e * 0.55f, x, i.bottom - e), tt.s(0.3f))
            drawLine(tt.b(0.32f), Offset(x, i.bottom - e), Offset(x + sx * e * 0.5f, i.bottom - e * 0.55f), 0.8f * u)
        }
    }

    /** Una marca de cantero grabada: una flecha, un triángulo con su raya o un reloj de arena. */
    private fun DrawScope.marcaCantero(c: Offset, s: Float, tipo: Int) {
        val u = density
        val t = tn(c)
        val l = when (tipo) {
            0 -> listOf(0f, 0.5f, 0f, -0.5f, -0.28f, -0.2f, 0f, -0.5f, 0f, -0.5f, 0.28f, -0.2f)
            1 -> listOf(-0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0.4f, 0f, -0.45f, 0f, -0.45f, -0.4f, 0.4f, 0f, -0.45f, 0f, 0.55f)
            else -> listOf(-0.35f, -0.45f, 0.35f, 0.45f, 0.35f, -0.45f, -0.35f, 0.45f, -0.35f, -0.45f, 0.35f, -0.45f, -0.35f, 0.45f, 0.35f, 0.45f)
        }
        var i = 0
        while (i < l.size) {
            val a = Offset(c.x + l[i] * s, c.y + l[i + 1] * s); val b = Offset(c.x + l[i + 2] * s, c.y + l[i + 3] * s)
            drawLine(t.b(0.35f), a + Offset(0.7f * u, 0.7f * u), b + Offset(0.7f * u, 0.7f * u), 0.7f * u, StrokeCap.Round)
            drawLine(t.s(0.5f), a, b, 0.9f * u, StrokeCap.Round)
            i += 4
        }
    }

    /**
     * La luz de día: la mancha clara arriba a la izquierda, la piedra que se apaga hacia la derecha y abajo, y la franja de sol
     * de una saetera que queda fuera del cuadro, que baja hasta la balda alta.
     */
    private fun DrawScope.luzDeDia(w: Float, hasta: Float) {
        mancha(Offset(w * 0.04f, -w * 0.06f), w * 0.9f, luzMueble(0.3f))
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, sombraMueble(0.17f)), w * 0.38f, w), Offset(w * 0.38f, 0f), Size(w * 0.62f, size.height))
        drawPath(saetera(w, hasta, 0.014f), luzMueble(0.12f))
        drawPath(saetera(w, hasta, 0f), luzMueble(0.26f))
    }

    /** La franja de sol de la saetera: casi vertical, un poco más ancha abajo; [ensanche] para su borde suave. */
    private fun saetera(w: Float, hasta: Float, ensanche: Float): Path =
        poligono(w * (0.045f - ensanche), 0f, w * (0.1f + ensanche), 0f, w * (0.135f + ensanche), hasta + w * 0.02f, w * (0.068f - ensanche), hasta + w * 0.02f)

    /** El fondo de un hueco: la sombra de la balda de arriba en escalones y el lado que no mira a la luz. */
    private fun DrawScope.fondoHueco(r: Rect) {
        val t = tn(r.center)
        // Escalones chicos, para que no se note el borde de ninguno.
        for (j in 1..8) drawRect(t.s(0.035f), r.topLeft, Size(r.width, r.height * 0.45f * j / 8f))
    }

    /** El nicho de la pared con la cruz patada pintada a la almagra, medio borrada. */
    private fun DrawScope.nicho(r: Rect) {
        val u = density
        val t = tn(r.center)
        val boca = arcoMedioPunto(r)
        // El marco de piedra labrada alrededor.
        val marco = arcoMedioPunto(Rect(r.left - r.width * 0.12f, r.top - r.width * 0.12f, r.right + r.width * 0.12f, r.bottom + r.width * 0.06f))
        drawPath(marco, t.c(mezcla(CALIZA, CALIZA_LUZ, 0.3f)))
        drawPath(marco, t.s(0.3f), style = Stroke(1f * u))
        drawPath(boca, t.c(mezcla(CALIZA_OSC, MORTERO, 0.4f)))
        clipPath(boca) {
            drawRect(t.s(0.3f), r.topLeft, Size(r.width, r.height * 0.3f))
            drawRect(t.s(0.25f), Offset(r.left, r.top), Size(r.width * 0.22f, r.height))
            drawRect(t.b(0.12f), Offset(r.right - r.width * 0.25f, r.top + r.height * 0.3f), Size(r.width * 0.25f, r.height))
            val c = Offset(r.center.x, r.top + r.height * 0.55f)
            drawPath(cruzPatada(c, r.width * 0.3f), t.c(ALMAGRA).copy(alpha = 0.8f))
            for (j in 0 until 6) drawCircle(t.c(mezcla(CALIZA_OSC, MORTERO, 0.3f)).copy(alpha = 0.75f), r.width * (0.03f + 0.03f * azar(731, j)), Offset(c.x + r.width * (azar(732, j) - 0.5f) * 0.6f, c.y + r.width * (azar(733, j) - 0.5f) * 0.6f))
        }
        // El alféizar del nicho, que coge la luz.
        drawRect(t.c(CALIZA_LUZ), Offset(r.left - r.width * 0.05f, r.bottom - 1f * u), Size(r.width * 1.1f, r.width * 0.07f))
        drawRect(t.s(0.35f), Offset(r.left - r.width * 0.05f, r.bottom - 1f * u + r.width * 0.07f), Size(r.width * 1.1f, 1.4f * u))
    }

    /** El hollín que la vela de sebo ha dejado en la piedra encima de ella. */
    private fun DrawScope.hollin(w: Float) {
        val c = Offset(w * LUZ.x, w * 0.39f)
        val col = if (esNoche) NOCHE_T else Color(0xFF3A3028)
        mancha(c + Offset(0f, w * 0.04f), w * 0.05f, col.copy(alpha = 0.16f))
        mancha(c - Offset(0f, w * 0.01f), w * 0.065f, col.copy(alpha = 0.12f))
        mancha(c - Offset(0f, w * 0.07f), w * 0.07f, col.copy(alpha = 0.07f))
    }

    /** Una argolla de hierro empotrada en la piedra, con su grapa. */
    private fun DrawScope.argolla(c: Offset) {
        val u = density
        val w = anchoW
        val t = tn(c)
        val r = w * 0.013f
        drawRect(t.s(0.35f), Offset(c.x - w * 0.006f + 1f * u, c.y - w * 0.006f + 1f * u), Size(w * 0.012f, w * 0.012f))
        drawRect(t.c(FIERRO), Offset(c.x - w * 0.006f, c.y - w * 0.006f), Size(w * 0.012f, w * 0.012f))
        val ring = Offset(c.x, c.y + r)
        drawCircle(t.s(0.3f), r, ring + Offset(1.5f * u, 2f * u), style = Stroke(2.2f * u))
        drawCircle(t.c(FIERRO), r, ring, style = Stroke(2.2f * u))
        drawArc(t.b(0.5f), 160f, 90f, false, Offset(ring.x - r, ring.y - r), Size(r * 2, r * 2), style = Stroke(0.8f * u))
        // El chorreón de óxido.
        drawRect(t.c(CUERO_M).copy(alpha = 0.18f), Offset(c.x - w * 0.003f, c.y + r * 2.2f), Size(w * 0.006f, w * 0.05f))
    }

    // ---------------------------------------------------------------- las baldas, las ménsulas y el banco

    /** Una balda gruesa de roble: su cara de arriba desde [cara], su canto desde [frente], las ménsulas de piedra y sus sombras. */
    private fun DrawScope.balda(cara: Float, frente: Float, w: Float) {
        val u = density
        val xL = w * 0.004f; val xR = w * 0.996f
        val bajo = frente + w * CANTO
        // La sombra del canto en la pared, debajo.
        val t0 = tn(Offset(w * 0.5f, bajo))
        drawRect(t0.s(0.4f), Offset(xL, bajo), Size(xR - xL, w * 0.008f))
        drawRect(t0.s(0.2f), Offset(xL, bajo + w * 0.008f), Size(xR - xL, w * 0.012f))
        // Las ménsulas: a los lados y en medio.
        listOf(0.036f, 0.5f, 0.964f).forEach { fx -> mensula(Offset(w * fx, bajo), w * 0.044f, w * 0.05f) }
        // La cara de arriba y el canto, por tramos (de noche, cada tramo con su luz).
        val n = 10
        for (k in 0 until n) {
            val x0 = xL + (xR - xL) * k / n; val ww = (xR - xL) / n + 0.6f
            val t = tn(Offset(x0 + ww / 2, frente))
            drawRect(t.c(mezcla(TABLA_LUZ, TABLA, 0.25f)), Offset(x0, cara), Size(ww, frente - cara))
            drawRect(t.c(TABLA), Offset(x0, frente), Size(ww, bajo - frente))
            drawRect(t.b(0.5f), Offset(x0, frente), Size(ww, 1.2f * u))
            drawRect(t.s(0.3f), Offset(x0, bajo - 2f * u), Size(ww, 2f * u))
        }
        if (!esNoche) {
            drawRect(Brush.horizontalGradient(listOf(luzMueble(0.16f), Color.Transparent, sombraMueble(0.14f)), xL, xR), Offset(xL, cara), Size(xR - xL, bajo - cara))
        }
        vetas(Rect(xL, cara, xR, frente), (if (esNoche) NOCHE_T else TABLA_OSC).copy(alpha = 0.28f), (cara / u).toInt(), 0.8f, grosor = 0.5f, ondulacion = 0.3f)
        vetas(Rect(xL, frente, xR, bajo), (if (esNoche) NOCHE_T else TABLA_OSC).copy(alpha = 0.38f), (frente / u).toInt() + 7, 0.9f, grosor = 0.6f, ondulacion = 0.5f)
        val tc = tn(Offset(w * 0.5f, cara))
        drawRect(tc.s(0.4f), Offset(xL, cara), Size(xR - xL, 1.4f * u))
        // Los extremos: la testa de la izquierda a la luz, la de la derecha en sombra.
        drawRect(tn(Offset(xL, frente)).c(mezcla(TABLA_LUZ, CALIZA, 0.25f)), Offset(xL, frente), Size(w * 0.006f, bajo - frente))
        drawRect(tn(Offset(xR, frente)).s(0.4f), Offset(xR - w * 0.008f, cara), Size(w * 0.008f, bajo - cara))
        // Los golpes del canto, una rozadura clara y los clavos sobre las ménsulas.
        for (k in 0 until 4) {
            val x = w * (0.1f + 0.8f * azar((frente / u).toInt(), k))
            val t = tn(Offset(x, frente))
            drawPath(poligono(x, frente, x + w * 0.018f, frente, x + w * 0.009f, frente + w * 0.008f), t.c(mezcla(TABLA_LUZ, CALIZA_LUZ, 0.3f)))
        }
        listOf(0.036f, 0.5f, 0.964f).forEach { fx -> listOf(-1f, 1f).forEach { s -> clavo(Offset(w * fx + s * w * 0.011f, frente + w * CANTO * 0.5f), 1.3f * u, tn(Offset(w * fx, frente))) } }
    }

    /** Una ménsula de piedra bajo la balda: el frente, el cuarto bocel que se mete, y su sombra en la pared. */
    private fun DrawScope.mensula(arriba: Offset, ancho: Float, alto: Float) {
        val u = density
        val t = tn(arriba + Offset(0f, alto / 2))
        val x0 = arriba.x - ancho / 2; val x1 = arriba.x + ancho / 2
        val y0 = arriba.y; val y1 = arriba.y + alto * 0.42f; val y2 = arriba.y + alto
        val forma = Path().apply {
            moveTo(x0, y0); lineTo(x1, y0); lineTo(x1, y1)
            cubicTo(x1, y1 + (y2 - y1) * 0.6f, x1 - ancho * 0.3f, y2, x1 - ancho * 0.55f, y2)
            lineTo(x0 + ancho * 0.12f, y2)
            cubicTo(x0 + ancho * 0.02f, y2 - (y2 - y1) * 0.3f, x0, y1 + (y2 - y1) * 0.4f, x0, y1); close()
        }
        // La sombra en la pared.
        if (!esNoche) translate(ancho * 0.35f, alto * 0.2f) { drawPath(forma, sombraMueble(0.24f)) }
        else translate(ancho * 0.2f * (if (arriba.x / anchoW < LUZ.x) -1f else 1f), alto * 0.25f) { drawPath(forma, NOCHE_T.copy(alpha = 0.4f)) }
        drawPath(forma, t.c(CALIZA))
        clipPath(forma) {
            drawRect(t.c(mezcla(CALIZA, CALIZA_OSC, 0.55f)), Offset(x0, y1), Size(ancho, y2 - y1))
            drawRect(t.s(0.2f), Offset(x0, y1 + (y2 - y1) * 0.5f), Size(ancho, y2 - y1))
            drawRect(t.b(0.3f), Offset(x0, y0), Size(ancho * 0.18f, y1 - y0))
            drawRect(t.s(0.2f), Offset(x1 - ancho * 0.2f, y0), Size(ancho * 0.2f, y2 - y0))
            drawRect(t.b(0.4f), Offset(x0, y1 - 1f * u), Size(ancho, 1f * u))
            drawRect(t.s(0.35f), Offset(x0, y1), Size(ancho, 1f * u))
        }
    }

    /** El banco corrido de sillería: la cara de arriba gastada (el piso de la última fila), el frente de piedras y su pie. */
    private fun DrawScope.bancoCorrido(frente: Float, fin: Float, w: Float) {
        val u = density
        val cara = frente - w * CARA
        for (k in 0 until 10) {
            val x0 = w * k / 10f; val ww = w / 10f + 0.6f
            val t = tn(Offset(x0 + ww / 2, frente))
            drawRect(t.c(mezcla(CALIZA_LUZ, CALIZA, 0.35f)), Offset(x0, cara), Size(ww, frente - cara))
            drawRect(t.b(0.5f), Offset(x0, frente - 1.6f * u), Size(ww, 1.6f * u))
        }
        val tc = tn(Offset(w * 0.5f, cara))
        drawRect(tc.s(0.35f), Offset(0f, cara), Size(w, 1.4f * u))
        pinceladas(Rect(0f, cara, w, frente), if (esNoche) NOCHE_T else CALIZA_OSC, 7201, 10, u, 0.12f)
        // El frente: una hilada de sillares grandes con su arista redondeada y gastada.
        var x = -w * 0.06f; var j = 0
        while (x < w) {
            val largo = w * (0.22f + 0.12f * azar(7202, j))
            val r = Rect(x, frente, x + largo, fin)
            val t = tn(r.center)
            drawRect(t.c(MORTERO), r.topLeft, r.size)
            val i = Rect(r.left + 1.8f * u, r.top, r.right - 1.8f * u, r.bottom - 1.6f * u)
            drawRect(t.c(mezcla(CALIZA, CALIZA_OSC, 0.15f + 0.3f * azar(7203, j))), i.topLeft, i.size)
            drawRect(t.c(mezcla(CALIZA_LUZ, CALIZA, 0.3f)), i.topLeft, Size(i.width, w * 0.01f))
            drawRect(t.s(0.2f), Offset(i.left, i.top + w * 0.01f), Size(i.width, 1.2f * u))
            drawRect(t.b(0.16f), i.topLeft, Size(1.2f * u, i.height))
            drawRect(t.s(0.2f), Offset(i.right - 1.4f * u, i.top), Size(1.4f * u, i.height))
            for (q in 0 until 2) drawOval(t.c(mezcla(CALIZA, CALIZA_OSC, 0.6f)).copy(alpha = 0.35f), Offset(i.left + i.width * azar(7204 + j, q) * 0.7f, i.top + i.height * (0.3f + 0.4f * azar(7205 + j, q))), Size(i.width * 0.3f, i.height * 0.25f))
            x += largo; j++
        }
        // Lo más bajo, más sucio y húmedo.
        val tb = tn(Offset(w * 0.5f, fin))
        drawRect(tb.s(0.22f), Offset(0f, fin - (fin - frente) * 0.3f), Size(w, (fin - frente) * 0.3f))
    }

    /** El suelo de losas que se abren hacia quien mira, con la sombra del banco. */
    private fun DrawScope.suelo(w: Float, desde: Float, alto: Float) {
        val u = density
        val t = tn(Offset(w * 0.5f, alto))
        val tt = tn(Offset(w * 0.5f, desde))
        drawRect(t.c(mezcla(CALIZA_OSC, MORTERO, 0.35f)), Offset(0f, desde), Size(w, alto - desde))
        val juntas = listOf(0.3f, 0.68f).map { desde + (alto - desde) * it }
        juntas.forEach { y -> drawLine(t.s(0.35f), Offset(0f, y), Offset(w, y), 1.2f * u); drawLine(t.b(0.2f), Offset(0f, y + 1.2f * u), Offset(w, y + 1.2f * u), 0.6f * u) }
        val fuga = Offset(w * 0.5f, desde - w * 1.1f)
        for (j in -5..5) {
            val xb = w * 0.5f + j * w * 0.17f
            fun xx(yy: Float) = fuga.x + (xb - fuga.x) * (yy - fuga.y) / (alto - fuga.y)
            drawLine(t.s(0.3f), Offset(xx(desde), desde), Offset(xx(alto), alto), 1f * u)
        }
        pinceladas(Rect(0f, desde, w, alto), if (esNoche) NOCHE_T else CALIZA_OSC, 7301, 16, u, 0.12f)
        drawRect(tt.s(0.45f), Offset(0f, desde), Size(w, 2f * u))
        drawRect(tt.s(0.2f), Offset(0f, desde + 2f * u), Size(w, w * 0.015f))
    }

    // ---------------------------------------------------------------- en la pared

    /** El escudo de lágrima: arriba redondeado y abajo en punta. [c] es el centro del borde de arriba. */
    private fun siluetaEscudo(c: Offset, a: Float, h: Float): Path = Path().apply {
        val hombro = c.y + h * 0.09f
        moveTo(c.x - a, hombro)
        cubicTo(c.x - a * 0.72f, c.y - h * 0.015f, c.x + a * 0.72f, c.y - h * 0.015f, c.x + a, hombro)
        cubicTo(c.x + a * 1.03f, c.y + h * 0.42f, c.x + a * 0.52f, c.y + h * 0.78f, c.x, c.y + h)
        cubicTo(c.x - a * 0.52f, c.y + h * 0.78f, c.x - a * 1.03f, c.y + h * 0.42f, c.x - a, hombro)
        close()
    }

    /**
     * El escudo de lágrima colgado de su guige: el gancho de hierro, las dos ramas de la correa, el campo blanco gastado con
     * la cruz roja, su bombeo (claro a la izquierda, sombra a la derecha), el ribete de cuero, rozaduras, un desconchón que
     * deja ver la madera y la sombra en la pared.
     */
    private fun DrawScope.escudo(w: Float) {
        val u = density
        val gancho = Offset(w * 0.228f, w * 0.048f)
        val c = Offset(w * 0.214f, w * 0.112f)
        val a = w * 0.12f; val h = w * 0.4f
        val sil = siluetaEscudo(c, a, h)
        val t = tn(Offset(c.x, c.y + h * 0.4f))
        val giro = -4f
        val correaA = Offset(c.x - a * 0.5f, c.y + h * 0.05f); val correaB = Offset(c.x + a * 0.55f, c.y + h * 0.05f)
        val correa = Path().apply { moveTo(correaA.x, correaA.y); lineTo(gancho.x, gancho.y + 2f * u); lineTo(correaB.x, correaB.y) }
        rotate(giro, gancho) {
            // La sombra en la pared (escudo y correa).
            sombraEnPared(sil, Offset(c.x, c.y + h * 0.4f), size.height, 0.24f)
            sombraEnPared(Path().apply { addPath(correa) }, gancho, size.height, 0.0f)
            if (!esNoche) translate(w * 0.02f, w * 0.012f) { drawPath(correa, sombraMueble(0.22f), style = Stroke(w * 0.008f)) }
            // La guige.
            drawPath(correa, t.c(CUERO_OSC), style = Stroke(w * 0.008f))
            drawPath(correa, t.b(0.3f), style = Stroke(w * 0.002f))
            // El campo.
            drawPath(sil, t.c(mezcla(YESO, CALIZA_LUZ, 0.35f)))
            clipPath(sil) {
                // La cruz roja, con la pintura cuarteada.
                val cr = Tono(t.o, (t.k * 1.15f).coerceAtMost(1f))
                val bw = a * 0.34f
                drawRect(cr.c(CRUZ), Offset(c.x - bw / 2, c.y - h * 0.05f), Size(bw, h * 1.1f))
                drawRect(cr.c(CRUZ), Offset(c.x - a * 1.1f, c.y + h * 0.27f), Size(a * 2.2f, bw))
                for (j in 0 until 14) {
                    val p = Offset(c.x + a * (azar(741, j) - 0.5f) * 1.6f, c.y + h * (0.05f + 0.85f * azar(742, j)))
                    drawOval(t.c(mezcla(YESO, CALIZA, 0.4f)).copy(alpha = 0.5f), p, Size(w * (0.004f + 0.008f * azar(743, j)), w * 0.003f))
                }
                // El bombeo: luz a la izquierda (de noche, a la derecha, hacia la vela) y sombra al otro lado.
                if (!t.o) {
                    drawRect(luzMueble(0.22f), Offset(c.x - a, c.y), Size(a * 0.45f, h))
                    drawRect(sombraMueble(0.14f), Offset(c.x + a * 0.2f, c.y), Size(a, h))
                    drawRect(sombraMueble(0.14f), Offset(c.x + a * 0.6f, c.y), Size(a, h))
                } else {
                    drawRect(t.s(0.3f), Offset(c.x - a, c.y), Size(a * 0.7f, h))
                    drawRect(t.b(0.22f), Offset(c.x + a * 0.25f, c.y), Size(a * 0.8f, h))
                }
                drawRect(t.s(0.18f), Offset(c.x - a, c.y + h * 0.72f), Size(a * 2f, h * 0.3f))
                // Rozaduras de golpes y un desconchón que deja ver la tabla.
                for (j in 0 until 6) {
                    val p = Offset(c.x + a * (azar(744, j) - 0.4f) * 1.4f, c.y + h * (0.15f + 0.6f * azar(745, j)))
                    drawLine(t.s(0.35f), p, p + Offset(w * 0.02f, w * (0.008f * azar(746, j) - 0.004f)), 0.7f * u)
                    drawLine(t.b(0.4f), p + Offset(0f, 0.8f * u), p + Offset(w * 0.02f, w * (0.008f * azar(746, j) - 0.004f) + 0.8f * u), 0.5f * u)
                }
                drawPath(poligono(c.x + a * 0.62f, c.y + h * 0.5f, c.x + a * 0.95f, c.y + h * 0.44f, c.x + a * 0.9f, c.y + h * 0.58f, c.x + a * 0.7f, c.y + h * 0.6f), t.c(TABLA_LUZ))
                // Un tajo de la paz: el sol de la saetera (de día) cruza la parte izquierda.
                if (!t.o) drawPath(saetera(w, size.height, 0f), luzMueble(0.12f))
            }
            // El ribete de cuero, con su brillo arriba a la izquierda (de noche, el filo de la vela a la derecha).
            drawPath(sil, t.c(CUERO_OSC), style = Stroke(w * 0.007f))
            if (!t.o) drawArc(luzMueble(0.45f), 180f, 70f, false, Offset(c.x - a * 0.95f, c.y + h * 0.01f), Size(a * 1.9f, h * 0.5f), style = Stroke(1.1f * u))
            else clipRect(c.x + a * 0.3f, c.y, c.x + a * 1.2f, c.y + h * 0.75f) { drawPath(sil, LLAMA.copy(alpha = (1.1f * t.k).coerceAtMost(0.75f)), style = Stroke(1.6f * u)) }
            // Los clavos del brazal que atraviesan el campo.
            listOf(Offset(c.x - a * 0.55f, c.y + h * 0.48f), Offset(c.x + a * 0.55f, c.y + h * 0.48f), Offset(c.x - a * 0.6f, c.y + h * 0.18f), Offset(c.x + a * 0.6f, c.y + h * 0.18f)).forEach { clavo(it, 1.4f * u, t) }
        }
        // El gancho de hierro.
        val tg = tn(gancho)
        drawCircle(tg.s(0.35f), w * 0.007f, gancho + Offset(1.5f * u, 1.5f * u))
        drawCircle(tg.c(FIERRO), w * 0.007f, gancho)
        drawArc(tg.c(FIERRO), 0f, 200f, false, Offset(gancho.x - w * 0.008f, gancho.y - w * 0.004f), Size(w * 0.016f, w * 0.016f), style = Stroke(2f * u))
        drawCircle(tg.b(0.6f), w * 0.0025f, gancho - Offset(w * 0.002f, w * 0.002f))
    }

    /**
     * La percha de madera sobre dos escarpias, con el manto pardo del sargento doblado encima: el pliegue de arriba que cubre
     * la vara, la caída con sus pliegues hondos (claro y sombra), el bajo ondulado y más largo a la derecha, el envés que asoma
     * detrás, y un trozo de la cruz roja. De la punta de la vara cuelga el paternóster de nudos.
     */
    private fun DrawScope.manto(w: Float) {
        val u = density
        val vy = w * 0.105f
        val x0 = w * 0.58f; val x1 = w * 0.868f
        val t = tn(Offset(w * 0.72f, w * 0.22f))
        // Las escarpias y la vara.
        listOf(0.6f, 0.845f).forEach { fx ->
            val p = Offset(w * fx, vy + w * 0.01f)
            drawRect(t.s(0.3f), Offset(p.x - w * 0.004f + 1.5f * u, p.y + 1.5f * u), Size(w * 0.008f, w * 0.022f))
            drawRect(t.c(FIERRO), Offset(p.x - w * 0.004f, p.y), Size(w * 0.008f, w * 0.022f))
        }
        val vara = Rect(x0, vy - w * 0.007f, x1, vy + w * 0.007f)
        if (!esNoche) translate(w * 0.015f, w * 0.012f) { drawRect(sombraMueble(0.22f), vara.topLeft, vara.size) }
        drawRect(t.c(TABLA), vara.topLeft, vara.size)
        drawRect(t.b(0.4f), vara.topLeft, Size(vara.width, vara.height * 0.35f))
        drawRect(t.s(0.3f), Offset(vara.left, vara.bottom - vara.height * 0.3f), Size(vara.width, vara.height * 0.3f))
        drawOval(t.c(TABLA_LUZ), Offset(x1 - w * 0.004f, vara.top), Size(w * 0.008f, vara.height))
        // El paño: el manto doblado sobre la vara. La mitad de delante cae recogida arriba y se abre abajo en pliegues; la de
        // detrás, más larga, asoma por debajo a la derecha.
        val l = w * 0.6f; val r = w * 0.835f
        val pano = mezcla(PARDO, LONA, 0.12f)
        val pliegues = listOf(0.1f, 0.3f, 0.47f, 0.66f, 0.86f)
        fun bajo(f: Float): Float {
            var y = w * (0.37f - 0.045f * f)
            pliegues.forEach { q -> val d = abs(f - q); if (d < 0.08f) y -= w * 0.014f * (1f - d / 0.08f) }
            return y
        }
        val bajoPts = (0..24).map { i -> val f = i / 24f; Offset(l - w * 0.012f + (r - l + w * 0.024f) * f, bajo(f)) }
        val enves = Path().apply {
            moveTo(l + w * 0.09f, w * 0.3f); lineTo(r - w * 0.004f, w * 0.2f)
            cubicTo(r + w * 0.012f, w * 0.3f, r + w * 0.02f, w * 0.36f, r + w * 0.012f, w * 0.405f)
            cubicTo(r - w * 0.02f, w * 0.4f, r - w * 0.035f, w * 0.415f, r - w * 0.06f, w * 0.402f)
            cubicTo(r - w * 0.08f, w * 0.392f, r - w * 0.1f, w * 0.405f, r - w * 0.13f, w * 0.385f)
            close()
        }
        val caida = Path().apply {
            moveTo(l + w * 0.012f, vy - w * 0.002f)
            cubicTo(l + w * 0.04f, vy - w * 0.024f, r - w * 0.04f, vy - w * 0.024f, r - w * 0.012f, vy - w * 0.002f)
            cubicTo(r + w * 0.002f, w * 0.18f, r + w * 0.014f, w * 0.27f, bajoPts.last().x, bajoPts.last().y)
            for (i in bajoPts.indices.reversed()) lineTo(bajoPts[i].x, bajoPts[i].y)
            cubicTo(l - w * 0.018f, w * 0.27f, l - w * 0.004f, w * 0.17f, l + w * 0.012f, vy - w * 0.002f)
            close()
        }
        sombraEnPared(Path().apply { addPath(enves); addPath(caida) }, Offset(w * 0.72f, w * 0.22f), size.height, 0.26f)
        drawPath(enves, t.c(mezcla(pano, NOCHE_T, 0.18f)))
        clipPath(enves) {
            drawRect(t.s(0.25f), Offset(r - w * 0.15f, w * 0.36f), Size(w * 0.2f, w * 0.06f))
            listOf(0.76f, 0.8f).forEach { fx -> drawLine(t.s(0.3f), Offset(w * fx, w * 0.33f), Offset(w * (fx + 0.004f), w * 0.41f), 1.4f * u) }
        }
        drawPath(caida, t.c(pano))
        clipPath(caida) {
            // Un trozo de la cruz roja, que un pliegue corta.
            drawPath(cruzPatada(Offset(l + (r - l) * 0.36f, w * 0.225f), w * 0.042f), Tono(t.o, (t.k * 1.1f).coerceAtMost(1f)).c(CRUZ))
            // La luz general: de día, clara a la izquierda y en sombra a la derecha.
            if (!t.o) { drawRect(luzMueble(0.14f), Offset(l - w * 0.02f, vy), Size((r - l) * 0.3f, w * 0.3f)); drawRect(sombraMueble(0.16f), Offset(l + (r - l) * 0.7f, vy), Size((r - l) * 0.4f, w * 0.3f)) }
            else drawRect(t.s(0.2f), Offset(l + (r - l) * 0.55f, vy), Size((r - l) * 0.5f, w * 0.3f))
            // Los pliegues: cada uno, una hondura en sombra que nace recogida arriba y se abre abajo, y su cresta de luz al lado.
            pliegues.forEachIndexed { i, q ->
                val qt = 0.5f + (q - 0.5f) * 0.5f
                val xt = l + w * 0.012f + (r - l - w * 0.024f) * qt
                val xb = l - w * 0.012f + (r - l + w * 0.024f) * q
                val yb = bajo(q) + w * 0.004f
                val ancho = w * (0.016f + 0.008f * azar(7411, i))
                val hondo = Path().apply {
                    moveTo(xt - w * 0.002f, vy + w * 0.012f)
                    cubicTo(xt + w * 0.006f, vy + w * 0.08f, xb - ancho * 0.9f, yb - w * 0.12f, xb - ancho / 2, yb)
                    lineTo(xb + ancho / 2, yb)
                    cubicTo(xb + ancho * 0.2f, yb - w * 0.12f, xt + w * 0.01f, vy + w * 0.08f, xt + w * 0.003f, vy + w * 0.012f)
                    close()
                }
                drawPath(hondo, t.s(0.3f))
                drawOval(t.s(0.35f), Offset(xb - ancho * 0.45f, yb - w * 0.012f), Size(ancho * 0.9f, w * 0.014f))
                val cresta = Path().apply { moveTo(xt + w * 0.012f, vy + w * 0.015f); cubicTo(xt + w * 0.02f, vy + w * 0.09f, xb + ancho * 1.1f, yb - w * 0.12f, xb + ancho * 1.2f, yb - w * 0.006f) }
                drawPath(cresta, t.b(if (i < 2) 0.3f else 0.18f), style = Stroke(w * (0.006f + 0.003f * azar(7412, i)), cap = StrokeCap.Round))
            }
            // El rollo de arriba, sobre la vara: luz encima y su sombra debajo.
            drawRect(t.b(0.34f), Offset(l, vy - w * 0.022f), Size(r - l, w * 0.012f))
            drawRect(t.s(0.3f), Offset(l, vy + w * 0.004f), Size(r - l, w * 0.008f))
            drawRect(t.s(0.14f), Offset(l, vy + w * 0.012f), Size(r - l, w * 0.012f))
            pinceladas(Rect(l - w * 0.02f, vy, r + w * 0.02f, w * 0.38f), if (t.o) NOCHE_T else mezcla(pano, LUZ_MUEBLE, 0.4f), 7401, 14, u, 0.1f)
        }
        // El bajo con su grosor: un ribete más oscuro.
        drawPath(Path().apply { bajoPts.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }, t.c(mezcla(pano, NOCHE_T, 0.35f)), style = Stroke(1.6f * u))
        if (t.o) drawPath(caida, LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.3f)), style = Stroke(1f * u))
        // El paternóster de nudos, colgado de la punta de la vara.
        val pt = Offset(w * 0.86f, vy + w * 0.006f)
        val tt = tn(pt + Offset(0f, w * 0.08f))
        val cuerda = listOf(
            Path().apply { moveTo(pt.x, pt.y); cubicTo(pt.x - w * 0.012f, pt.y + w * 0.06f, pt.x + w * 0.004f, pt.y + w * 0.12f, pt.x + w * 0.012f, pt.y + w * 0.13f) },
            Path().apply { moveTo(pt.x + w * 0.004f, pt.y); cubicTo(pt.x + w * 0.022f, pt.y + w * 0.05f, pt.x + w * 0.02f, pt.y + w * 0.1f, pt.x + w * 0.012f, pt.y + w * 0.13f) },
        )
        cuerda.forEach { c ->
            if (!esNoche) translate(w * 0.01f, w * 0.006f) { drawPath(c, sombraMueble(0.2f), style = Stroke(1f * u)) }
            drawPath(c, tt.c(LONA), style = Stroke(0.8f * u))
        }
        // Los nudos (cuentas), a lo largo de las dos ramas.
        for (j in 0 until 9) {
            val f = (j + 1) / 10f
            listOf(Offset(pt.x - w * 0.006f + w * 0.017f * f, pt.y + w * 0.13f * f - w * 0.003f), Offset(pt.x + w * 0.014f - w * 0.002f * f, pt.y + w * 0.13f * f)).forEachIndexed { q, p ->
                val pp = if (q == 0) Offset(pt.x - w * 0.012f * 4f * f * (1f - f) + w * 0.012f * f, pt.y + w * 0.13f * f) else Offset(pt.x + w * 0.004f + w * 0.024f * 4f * f * (1f - f) * 0.7f + w * 0.008f * f, pt.y + w * 0.13f * f)
                drawCircle(tt.c(mezcla(CUERO_OSC, LONA, 0.35f)), w * 0.0035f, pp)
                drawCircle(tt.b(0.4f), w * 0.0013f, pp - Offset(w * 0.001f, w * 0.001f))
            }
        }
        // La borla del final.
        val bo = Offset(pt.x + w * 0.012f, pt.y + w * 0.13f)
        drawPath(poligono(bo.x - w * 0.003f, bo.y, bo.x + w * 0.003f, bo.y, bo.x + w * 0.007f, bo.y + w * 0.03f, bo.x - w * 0.007f, bo.y + w * 0.03f), tt.c(CRUZ))
        drawLine(tt.s(0.3f), bo + Offset(w * 0.002f, w * 0.005f), bo + Offset(w * 0.004f, w * 0.03f), 0.7f * u)
    }

    /** El manojo de llaves grandes en su anilla, colgado de un clavo, con su sombra. */
    private fun DrawScope.llaves(clavoP: Offset) {
        val u = density
        val w = anchoW
        val t = tn(clavoP + Offset(0f, w * 0.05f))
        val anilla = Offset(clavoP.x, clavoP.y + w * 0.014f)
        val ra = w * 0.012f
        fun dibujar(col: Color, luz: Boolean) {
            drawCircle(col, ra, anilla, style = Stroke(1.8f * u))
            listOf(Triple(-22f, 0.075f, 0), Triple(4f, 0.09f, 1), Triple(26f, 0.065f, 2)).forEach { (g, l, i) ->
                rotate(g, Offset(anilla.x, anilla.y + ra)) {
                    val x = anilla.x; val y0 = anilla.y + ra
                    // El ojo de la llave, la caña y el paletón.
                    drawCircle(col, w * 0.009f, Offset(x, y0 + w * 0.01f), style = Stroke(2f * u))
                    drawLine(col, Offset(x, y0 + w * 0.019f), Offset(x, y0 + w * l), 2.2f * u)
                    drawRect(col, Offset(x, y0 + w * (l - 0.02f)), Size(w * 0.016f, w * 0.008f))
                    drawRect(col, Offset(x + w * 0.006f, y0 + w * (l - 0.012f)), Size(w * 0.01f, w * 0.01f))
                    if (luz) drawLine(t.b(0.5f), Offset(x - 0.8f * u, y0 + w * 0.02f), Offset(x - 0.8f * u, y0 + w * (l - 0.022f)), 0.6f * u)
                    if (i == 1) drawCircle(t.c(CUERO_M), w * 0.003f, Offset(x, y0 + w * (l - 0.03f)))
                }
            }
        }
        if (!esNoche) translate(w * 0.014f, w * 0.01f) { dibujar(sombraMueble(0.22f), false) }
        dibujar(t.c(FIERRO), true)
        clavo(clavoP, w * 0.0045f, t)
    }

    // ---------------------------------------------------------------- sobre la balda alta

    /**
     * El yelmo de cuenco con nasal, ladeado, sobre la cofia de malla amontonada, que cae por el canto de la balda con su
     * peso (el bajo combado y una punta más larga).
     */
    private fun DrawScope.yelmo(pie: Offset, canto: Float, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.06f))
        val der = luzDerecha(pie.x)
        // ---- la cofia de malla: el montón y la caída por el canto
        val monton = Path().apply {
            moveTo(pie.x - w * 0.092f, pie.y + w * 0.006f)
            cubicTo(pie.x - w * 0.085f, pie.y - w * 0.03f, pie.x - w * 0.04f, pie.y - w * 0.05f, pie.x, pie.y - w * 0.048f)
            cubicTo(pie.x + w * 0.05f, pie.y - w * 0.046f, pie.x + w * 0.09f, pie.y - w * 0.02f, pie.x + w * 0.094f, pie.y + w * 0.006f)
            close()
        }
        val xa = pie.x - w * 0.078f; val xb = pie.x + w * 0.06f
        val caida = Path().apply {
            moveTo(xa, pie.y - w * 0.004f); lineTo(xb, pie.y - w * 0.004f)
            cubicTo(xb + w * 0.004f, canto + w * 0.01f, xb - w * 0.002f, canto + w * 0.03f, xb - w * 0.01f, canto + w * 0.05f)
            cubicTo(xb - w * 0.03f, canto + w * 0.058f, xb - w * 0.06f, canto + w * 0.06f, xa + w * 0.05f, canto + w * 0.072f)
            cubicTo(xa + w * 0.035f, canto + w * 0.082f, xa + w * 0.02f, canto + w * 0.07f, xa + w * 0.012f, canto + w * 0.055f)
            cubicTo(xa + w * 0.002f, canto + w * 0.04f, xa - w * 0.004f, canto + w * 0.01f, xa, pie.y - w * 0.004f)
            close()
        }
        // Su sombra: en el canto y en la pared de debajo.
        translate(w * 0.012f, w * 0.01f) { drawPath(caida, t.s(0.35f)) }
        contacto(Offset(pie.x + w * 0.01f, pie.y + w * 0.004f), w * 0.2f, w * 0.012f, 0.4f, t)
        malla(caida, Rect(xa - w * 0.01f, pie.y - w * 0.01f, xb + w * 0.01f, canto + w * 0.09f), t, der)
        clipPath(caida) {
            // Los pliegues de la caída y el canto de la balda que se marca debajo.
            listOf(0.18f, 0.45f, 0.72f).forEach { f -> drawRect(t.s(0.25f), Offset(xa + (xb - xa) * f, pie.y), Size(w * 0.008f, w * 0.12f)) }
            drawRect(t.b(0.22f), Offset(xa, canto - w * 0.002f), Size(xb - xa, w * 0.006f))
            drawRect(t.s(0.3f), Offset(xa, canto + w * 0.035f), Size(xb - xa, w * 0.06f))
        }
        malla(monton, Rect(pie.x - w * 0.1f, pie.y - w * 0.055f, pie.x + w * 0.1f, pie.y + w * 0.01f), t, der)
        clipPath(monton) {
            drawRect(t.s(0.25f), Offset(pie.x - w * 0.1f, pie.y - w * 0.012f), Size(w * 0.2f, w * 0.02f))
            if (!der) drawRect(t.b(0.12f), Offset(pie.x - w * 0.1f, pie.y - w * 0.06f), Size(w * 0.06f, w * 0.06f))
        }
        // ---- el yelmo, ladeado sobre el montón
        val borde = pie.y - w * 0.034f
        val cx = pie.x + w * 0.004f
        val cuenco = Path().apply {
            moveTo(cx - w * 0.064f, borde)
            cubicTo(cx - w * 0.066f, borde - w * 0.06f, cx - w * 0.03f, borde - w * 0.112f, cx + w * 0.004f, borde - w * 0.114f)
            cubicTo(cx + w * 0.036f, borde - w * 0.11f, cx + w * 0.066f, borde - w * 0.058f, cx + w * 0.064f, borde)
            close()
        }
        rotate(-7f, Offset(cx, borde)) {
            sombraEnPared(cuenco, Offset(cx, borde - w * 0.05f), pie.y - w * 0.01f, 0.24f)
            drawPath(cuenco, t.c(FIERRO))
            clipPath(cuenco) {
                val r = Rect(cx - w * 0.066f, borde - w * 0.116f, cx + w * 0.066f, borde)
                cilindro(r, t, 1.6f, der)
                // El remate abombado: más luz arriba, más sombra abajo.
                drawRect(t.s(0.18f), Offset(r.left, borde - w * 0.03f), Size(r.width, w * 0.03f))
                // El brillo largo del hierro pulido.
                val bx = if (der) cx + w * 0.034f else cx - w * 0.032f
                drawPath(Path().apply { moveTo(bx, borde - w * 0.012f); cubicTo(bx - w * 0.002f, borde - w * 0.05f, bx + (if (der) -w * 0.01f else w * 0.012f), borde - w * 0.09f, cx + (if (der) w * 0.01f else -w * 0.006f), borde - w * 0.106f) },
                    if (t.o) LLAMA.copy(alpha = (1.1f * t.k).coerceAtMost(0.9f)) else luzMueble(0.75f), style = Stroke(w * 0.005f, cap = StrokeCap.Round))
                // Óxido y algún golpe.
                drawOval(t.c(CUERO_M).copy(alpha = 0.3f), Offset(cx + w * 0.02f, borde - w * 0.03f), Size(w * 0.018f, w * 0.01f))
                drawOval(t.c(CUERO_M).copy(alpha = 0.22f), Offset(cx - w * 0.045f, borde - w * 0.022f), Size(w * 0.012f, w * 0.008f))
                drawArc(t.s(0.35f), 200f, 120f, false, Offset(cx + w * 0.006f, borde - w * 0.075f), Size(w * 0.02f, w * 0.014f), style = Stroke(0.8f * u))
            }
            // El aro del borde con sus remaches.
            val aro = Rect(cx - w * 0.066f, borde - w * 0.013f, cx + w * 0.066f, borde + w * 0.002f)
            drawRoundRect(t.c(mezcla(FIERRO, NOCHE_T, 0.2f)), aro.topLeft, aro.size, CornerRadius(2f * u))
            drawRect(t.b(0.4f), aro.topLeft, Size(aro.width, 1f * u))
            listOf(-0.05f, -0.025f, 0.025f, 0.05f).forEach { fx -> clavo(Offset(cx + w * fx, aro.center.y), 1.2f * u, t, FIERRO_LUZ) }
            // El nasal, que baja hasta el montón.
            val nasal = Path().apply {
                moveTo(cx - w * 0.008f, borde - w * 0.045f); lineTo(cx + w * 0.008f, borde - w * 0.045f)
                lineTo(cx + w * 0.009f, borde + w * 0.026f)
                cubicTo(cx + w * 0.006f, borde + w * 0.032f, cx - w * 0.006f, borde + w * 0.032f, cx - w * 0.009f, borde + w * 0.026f); close()
            }
            translate(w * 0.004f, w * 0.003f) { drawPath(nasal, t.s(0.4f)) }
            drawPath(nasal, t.c(mezcla(FIERRO, FIERRO_LUZ, 0.15f)))
            clipPath(nasal) {
                drawRect(t.b(0.45f), Offset(if (der) cx + w * 0.003f else cx - w * 0.008f, borde - w * 0.05f), Size(w * 0.005f, w * 0.08f))
                drawRect(t.s(0.3f), Offset(if (der) cx - w * 0.009f else cx + w * 0.004f, borde - w * 0.05f), Size(w * 0.005f, w * 0.08f))
            }
            clavo(Offset(cx, borde - w * 0.035f), 1.2f * u, t, FIERRO_LUZ)
            destello(Offset(if (der) cx + w * 0.03f else cx - w * 0.028f, borde - w * 0.07f), 1.4f * u, t)
        }
    }

    /** La malla de anillas dentro de [p]: filas al tresbolillo de anillas chicas, cada una con su sombra y su brillo. */
    private fun DrawScope.malla(p: Path, r: Rect, t: Tono, der: Boolean) {
        val u = density
        drawPath(p, t.c(mezcla(FIERRO, NOCHE_T, 0.3f)))
        clipPath(p) {
            val paso = 2.5f * u
            var y = r.top; var fila = 0
            val luzC = t.c(mezcla(FIERRO_LUZ, FIERRO, 0.2f))
            while (y < r.bottom) {
                var x = r.left + (if (fila % 2 == 0) 0f else paso / 2)
                while (x < r.right) {
                    drawCircle(t.s(0.4f), paso * 0.42f, Offset(x + 0.5f * u, y + 0.6f * u), style = Stroke(0.7f * u))
                    drawCircle(luzC.copy(alpha = 0.6f), paso * 0.42f, Offset(x, y), style = Stroke(0.6f * u))
                    x += paso
                }
                y += paso * 0.78f; fila++
            }
            // La luz del montón: de día a la izquierda; de noche, el brillo de la vela en la malla, del lado de la llama.
            if (!t.o) { drawRect(luzMueble(0.14f), r.topLeft, Size(r.width * 0.35f, r.height)); drawRect(sombraMueble(0.18f), Offset(r.right - r.width * 0.35f, r.top), Size(r.width * 0.35f, r.height)) }
            else {
                drawRect(t.s(0.3f), Offset(if (der) r.left else r.right - r.width * 0.4f, r.top), Size(r.width * 0.4f, r.height))
                for (j in 0 until 26) {
                    val q = Offset(r.left + r.width * (if (der) 0.5f + 0.5f * azar(751, j) else 0.5f * azar(751, j)), r.top + r.height * azar(752, j))
                    drawCircle(LLAMA.copy(alpha = (0.9f * t.k * azar(753, j)).coerceAtMost(0.85f)), 0.8f * u, q)
                }
            }
        }
    }

    /** El candelero de hierro de tres patas con su pincho y la vela de sebo, gastada y chorreada; de noche, encendida. */
    private fun DrawScope.candelero(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.05f))
        val plato = pie.y - w * 0.07f
        val vela = Rect(pie.x - w * 0.011f, plato - w * 0.075f, pie.x + w * 0.011f, plato - w * 0.002f)
        val sil = Path().apply { addRect(Rect(pie.x - w * 0.004f, plato, pie.x + w * 0.004f, pie.y - w * 0.01f)); addRect(vela); addOval(Rect(pie.x - w * 0.026f, plato - w * 0.006f, pie.x + w * 0.026f, plato + w * 0.006f)) }
        if (!esNoche) sombraEnPared(sil, Offset(pie.x, plato), pie.y - w * 0.012f, 0.22f)
        contacto(Offset(pie.x + w * 0.006f, pie.y + w * 0.002f), w * 0.07f, w * 0.008f, 0.4f, t)
        val hierro = t.c(FIERRO)
        // Las tres patas en curva y el nudo.
        listOf(-1f, 0f, 1f).forEach { s ->
            val p = Path().apply { moveTo(pie.x, pie.y - w * 0.014f); cubicTo(pie.x + s * w * 0.012f, pie.y - w * 0.012f, pie.x + s * w * 0.024f, pie.y - w * 0.006f, pie.x + s * w * 0.03f + (if (s == 0f) w * 0.006f else 0f), pie.y + (if (s == 0f) w * 0.003f else 0f)) }
            drawPath(p, hierro, style = Stroke(1.8f * u, cap = StrokeCap.Round))
        }
        drawCircle(hierro, w * 0.006f, Offset(pie.x, pie.y - w * 0.014f))
        drawRect(hierro, Offset(pie.x - w * 0.0035f, plato), Size(w * 0.007f, pie.y - w * 0.014f - plato))
        drawRect(t.b(0.4f), Offset(pie.x - w * 0.0035f, plato), Size(w * 0.002f, pie.y - w * 0.014f - plato))
        // El plato para el sebo que gotea, con un charco cuajado.
        drawOval(hierro, Offset(pie.x - w * 0.026f, plato - w * 0.006f), Size(w * 0.052f, w * 0.012f))
        drawOval(t.c(mezcla(SEBO, CALIZA_OSC, 0.25f)), Offset(pie.x - w * 0.018f, plato - w * 0.005f), Size(w * 0.036f, w * 0.007f))
        drawArc(t.b(0.5f), 180f, 90f, false, Offset(pie.x - w * 0.026f, plato - w * 0.006f), Size(w * 0.052f, w * 0.012f), style = Stroke(0.8f * u))
        // La vela de sebo: amarillenta, con chorreras y el borde derretido.
        drawRect(t.c(SEBO), vela.topLeft, vela.size)
        clipRect(vela.left, vela.top, vela.right, vela.bottom) { cilindro(vela, t, 1.2f, luzDerecha(pie.x) || esNoche) }
        if (esNoche) drawRect(LLAMA.copy(alpha = 0.35f), vela.topLeft, Size(vela.width, vela.height * 0.4f))
        listOf(0.2f to 0.6f, 0.75f to 0.85f, 0.5f to 0.3f).forEach { (fx, fl) ->
            val x = vela.left + vela.width * fx
            drawLine(t.c(mezcla(SEBO, LUZ_MUEBLE, 0.35f)), Offset(x, vela.top + 1f * u), Offset(x, vela.top + vela.height * fl), w * 0.004f, StrokeCap.Round)
        }
        drawOval(t.c(mezcla(SEBO, CALIZA_OSC, 0.3f)), Offset(vela.left + w * 0.002f, vela.top - w * 0.002f), Size(vela.width - w * 0.004f, w * 0.005f))
        drawLine(Color(0xFF1C1612), Offset(pie.x, vela.top), Offset(pie.x + w * 0.002f, vela.top - w * 0.008f), 1f * u, StrokeCap.Round)
    }

    /** La llama de la vela de sebo: más roja y humosa que la de cera. */
    private fun DrawScope.llamaDeSebo(p: Offset, s: Float) {
        val f = Path().apply {
            moveTo(p.x + s * 0.2f, p.y - s * 2f)
            cubicTo(p.x + s * 0.7f, p.y - s * 1f, p.x + s * 0.6f, p.y, p.x, p.y + s * 0.2f)
            cubicTo(p.x - s * 0.6f, p.y, p.x - s * 0.6f, p.y - s * 0.9f, p.x + s * 0.2f, p.y - s * 2f)
            close()
        }
        drawCircle(LLAMA.copy(alpha = 0.35f), s * 2.6f, p - Offset(0f, s * 0.7f))
        drawPath(f, Color(0xFFFFA048))
        drawPath(Path().apply {
            moveTo(p.x + s * 0.1f, p.y - s * 1.2f)
            cubicTo(p.x + s * 0.4f, p.y - s * 0.6f, p.x + s * 0.32f, p.y - s * 0.05f, p.x, p.y + s * 0.05f)
            cubicTo(p.x - s * 0.32f, p.y - s * 0.05f, p.x - s * 0.36f, p.y - s * 0.6f, p.x + s * 0.1f, p.y - s * 1.2f)
            close()
        }, Color(0xFFFFF0C8))
    }

    /** El libro de la regla, pequeño y tumbado: la tapa de cuero vista un poco desde arriba, el canto de las hojas y el cierre. */
    private fun DrawScope.libro(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x + w * 0.05f, pie.y - w * 0.02f))
        val x0 = pie.x; val x1 = pie.x + w * 0.1f
        val grueso = w * 0.026f
        val arriba = pie.y - grueso
        val prof = w * 0.014f
        contacto(Offset((x0 + x1) / 2 + w * 0.01f, pie.y), w * 0.12f, w * 0.008f, 0.42f, t)
        sombraEnPared(poligono(x0 + prof, arriba - prof, x1 + prof, arriba - prof, x1 + prof, pie.y, x0 + prof, pie.y), Offset(x0, arriba), pie.y - w * 0.012f, 0.2f)
        // La tapa de arriba (paralelogramo) y el lado de las hojas.
        val cuero = mezcla(CUERO_M, LACRE, 0.25f)
        drawPath(poligono(x0, arriba, x1, arriba, x1 + prof, arriba - prof, x0 + prof, arriba - prof), t.c(mezcla(cuero, LUZ_MUEBLE, 0.12f)))
        drawPath(poligono(x1, arriba, x1 + prof, arriba - prof, x1 + prof, pie.y - prof, x1, pie.y), t.c(mezcla(cuero, NOCHE_T, 0.3f)))
        drawRect(t.c(cuero), Offset(x0, arriba), Size(x1 - x0, w * 0.006f))
        drawRect(t.c(cuero), Offset(x0, pie.y - w * 0.006f), Size(x1 - x0, w * 0.006f))
        drawRect(t.c(mezcla(PERG, CALIZA, 0.3f)), Offset(x0 + w * 0.002f, arriba + w * 0.006f), Size(x1 - x0 - w * 0.004f, grueso - w * 0.012f))
        for (j in 0 until 4) drawLine(t.s(0.18f), Offset(x0 + w * 0.003f, arriba + w * (0.009f + 0.003f * j)), Offset(x1 - w * 0.003f, arriba + w * (0.009f + 0.003f * j)), 0.5f * u)
        drawRect(t.b(0.5f), Offset(x0, arriba), Size(x1 - x0, 0.9f * u))
        drawRect(t.s(0.2f), Offset(x0 + (x1 - x0) * 0.65f, arriba - prof), Size((x1 - x0) * 0.35f, prof))
        // La cruz grabada en la tapa.
        drawPath(cruzPatada(Offset((x0 + x1) / 2 + prof * 0.5f, arriba - prof * 0.5f), prof * 0.4f), t.c(mezcla(cuero, TINTA, 0.35f)))
        // El cierre: la correa que baja por el canto y su broche de bronce.
        val cx = x0 + (x1 - x0) * 0.72f
        drawRect(t.c(CUERO_OSC), Offset(cx, arriba - prof * 0.6f), Size(w * 0.008f, grueso + prof * 0.6f - w * 0.004f))
        drawRect(t.c(BRONCE), Offset(cx - w * 0.002f, arriba + grueso * 0.35f), Size(w * 0.012f, w * 0.008f))
        destello(Offset(cx + w * 0.002f, arriba + grueso * 0.38f), 1f * u, t)
        // La cinta del registro que asoma.
        drawLine(t.c(CRUZ), Offset(x0 + (x1 - x0) * 0.3f, pie.y - w * 0.006f), Offset(x0 + (x1 - x0) * 0.27f, pie.y + w * 0.006f), 1.4f * u)
        filo(Rect(x0, arriba, x1, pie.y), t, false)
    }

    /** La matriz del sello, de bronce, de pie (la base, el mango cónico y su anilla), y un sello de lacre tumbado junto a ella. */
    private fun DrawScope.sello(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.02f))
        contacto(Offset(pie.x + w * 0.004f, pie.y), w * 0.03f, w * 0.006f, 0.42f, t)
        val mango = poligono(pie.x - w * 0.01f, pie.y - w * 0.006f, pie.x + w * 0.01f, pie.y - w * 0.006f, pie.x + w * 0.003f, pie.y - w * 0.036f, pie.x - w * 0.003f, pie.y - w * 0.036f)
        sombraEnPared(mango, Offset(pie.x, pie.y - w * 0.02f), pie.y - w * 0.012f, 0.2f)
        drawOval(t.c(BRONCE), Offset(pie.x - w * 0.013f, pie.y - w * 0.009f), Size(w * 0.026f, w * 0.01f))
        drawPath(mango, t.c(BRONCE))
        clipPath(mango) { cilindro(Rect(pie.x - w * 0.01f, pie.y - w * 0.04f, pie.x + w * 0.01f, pie.y), t, 1.4f, luzDerecha(pie.x)) }
        drawCircle(t.c(BRONCE), w * 0.005f, Offset(pie.x, pie.y - w * 0.041f), style = Stroke(1.4f * u))
        destello(Offset(pie.x - w * 0.003f, pie.y - w * 0.026f), 0.9f * u, t)
        // El sello de lacre, delante, con la cruz impresa.
        val s = Offset(pie.x - w * 0.026f, pie.y + w * 0.002f)
        drawOval(t.s(0.35f), Offset(s.x - w * 0.009f, s.y - w * 0.002f), Size(w * 0.022f, w * 0.007f))
        drawOval(t.c(LACRE), Offset(s.x - w * 0.011f, s.y - w * 0.006f), Size(w * 0.022f, w * 0.009f))
        drawOval(t.c(mezcla(LACRE, NOCHE_T, 0.3f)), Offset(s.x - w * 0.006f, s.y - w * 0.004f), Size(w * 0.012f, w * 0.005f))
        drawLine(t.b(0.5f), Offset(s.x - w * 0.008f, s.y - w * 0.005f), Offset(s.x, s.y - w * 0.006f), 0.6f * u)
    }

    /** La balanza de cambista de bronce: el pie de madera, el fiel, la cruz inclinada y los dos platillos colgados de sus cordones. */
    private fun DrawScope.balanza(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.07f))
        val top = pie.y - w * 0.13f
        val base = Rect(pie.x - w * 0.028f, pie.y - w * 0.014f, pie.x + w * 0.028f, pie.y)
        val giro = 4f
        val ia = Offset(pie.x - w * 0.045f, top + w * 0.004f); val da = Offset(pie.x + w * 0.045f, top - w * 0.003f)
        val pa = Offset(ia.x, pie.y - w * 0.05f); val pd = Offset(da.x, pie.y - w * 0.06f)
        val sil = Path().apply {
            addRect(Rect(pie.x - w * 0.003f, top, pie.x + w * 0.003f, base.top)); addRect(base)
            addRect(Rect(ia.x, top - w * 0.003f, da.x, top + w * 0.003f))
            addOval(Rect(pa.x - w * 0.018f, pa.y - w * 0.005f, pa.x + w * 0.018f, pa.y + w * 0.006f)); addOval(Rect(pd.x - w * 0.018f, pd.y - w * 0.005f, pd.x + w * 0.018f, pd.y + w * 0.006f))
        }
        sombraEnPared(sil, Offset(pie.x, top), pie.y - w * 0.012f, 0.18f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), w * 0.07f, w * 0.008f, 0.42f, t)
        // El pie de madera.
        drawRect(t.c(TABLA), base.topLeft, base.size)
        drawRect(t.b(0.4f), base.topLeft, Size(base.width, 1f * u))
        drawRect(t.s(0.3f), Offset(base.right - base.width * 0.25f, base.top), Size(base.width * 0.25f, base.height))
        // El fiel y la cruz.
        val bronce = t.c(BRONCE)
        drawRect(bronce, Offset(pie.x - w * 0.003f, top), Size(w * 0.006f, base.top - top))
        drawRect(t.b(0.45f), Offset(pie.x - w * 0.003f, top), Size(w * 0.0018f, base.top - top))
        drawLine(bronce, ia, da, 2f * u, StrokeCap.Round)
        drawLine(t.b(0.5f), ia - Offset(0f, 0.7f * u), da - Offset(0f, 0.7f * u), 0.6f * u)
        drawPath(poligono(pie.x - w * 0.003f, top - w * 0.002f, pie.x + w * 0.003f, top - w * 0.002f, pie.x, top - w * 0.018f), bronce)
        drawCircle(bronce, w * 0.004f, Offset(pie.x, top))
        // Los cordones y los platillos; el de la derecha, más bajo, con unas monedas.
        listOf(ia to pa, da to pd).forEachIndexed { i, (a, p) ->
            listOf(-0.016f, 0f, 0.016f).forEach { dx -> drawLine(t.c(LONA).copy(alpha = 0.85f), a, Offset(p.x + w * dx, p.y), 0.6f * u) }
            drawArc(bronce, 0f, 180f, true, Offset(p.x - w * 0.019f, p.y - w * 0.006f), Size(w * 0.038f, w * 0.014f))
            drawOval(t.c(mezcla(BRONCE, TABLA_OSC, 0.3f)), Offset(p.x - w * 0.019f, p.y - w * 0.003f), Size(w * 0.038f, w * 0.006f))
            drawArc(t.b(0.5f), 160f, 60f, false, Offset(p.x - w * 0.019f, p.y - w * 0.006f), Size(w * 0.038f, w * 0.014f), style = Stroke(0.7f * u))
            if (i == 0) listOf(-0.006f, 0.004f).forEach { dx -> drawOval(t.c(mezcla(FIERRO_LUZ, BRONCE, 0.3f)), Offset(p.x + w * dx - w * 0.005f, p.y - w * 0.004f), Size(w * 0.01f, w * 0.004f)) }
        }
        destello(Offset(pie.x - w * 0.02f, top + w * 0.001f), 1f * u, t)
    }

    /** La bolsa de las monedas, de cuero claro, sentada y fruncida con su cordón; unos dineros de vellón sueltos delante. */
    private fun DrawScope.bolsa(pie: Offset, w: Float) {
        val u = density
        val t = tn(Offset(pie.x, pie.y - w * 0.02f))
        val col = mezcla(CUERO_M, LONA, 0.35f)
        val cuerpo = Path().apply {
            moveTo(pie.x - w * 0.012f, pie.y - w * 0.042f)
            cubicTo(pie.x - w * 0.03f, pie.y - w * 0.03f, pie.x - w * 0.034f, pie.y - w * 0.002f, pie.x - w * 0.014f, pie.y)
            lineTo(pie.x + w * 0.018f, pie.y)
            cubicTo(pie.x + w * 0.034f, pie.y - w * 0.004f, pie.x + w * 0.03f, pie.y - w * 0.03f, pie.x + w * 0.012f, pie.y - w * 0.042f)
            close()
        }
        sombraEnPared(cuerpo, Offset(pie.x, pie.y - w * 0.02f), pie.y - w * 0.012f, 0.2f)
        contacto(Offset(pie.x + w * 0.006f, pie.y), w * 0.06f, w * 0.008f, 0.42f, t)
        drawPath(cuerpo, t.c(col))
        clipPath(cuerpo) {
            cilindro(Rect(pie.x - w * 0.034f, pie.y - w * 0.045f, pie.x + w * 0.034f, pie.y), t, 1.3f, luzDerecha(pie.x))
            listOf(-0.008f, 0f, 0.009f).forEach { dx -> drawLine(t.s(0.3f), Offset(pie.x + w * dx, pie.y - w * 0.04f), Offset(pie.x + w * dx * 2f, pie.y - w * 0.018f), 0.7f * u) }
        }
        // El frunce de arriba y el cordón.
        drawPath(poligono(pie.x - w * 0.012f, pie.y - w * 0.042f, pie.x - w * 0.016f, pie.y - w * 0.054f, pie.x - w * 0.004f, pie.y - w * 0.048f, pie.x + w * 0.004f, pie.y - w * 0.056f, pie.x + w * 0.014f, pie.y - w * 0.05f, pie.x + w * 0.012f, pie.y - w * 0.042f), t.c(mezcla(col, LUZ_MUEBLE, 0.1f)))
        drawLine(t.c(CUERO_OSC), Offset(pie.x - w * 0.013f, pie.y - w * 0.042f), Offset(pie.x + w * 0.013f, pie.y - w * 0.042f), 1.4f * u)
        drawPath(Path().apply { moveTo(pie.x + w * 0.01f, pie.y - w * 0.042f); cubicTo(pie.x + w * 0.02f, pie.y - w * 0.036f, pie.x + w * 0.026f, pie.y - w * 0.02f, pie.x + w * 0.03f, pie.y - w * 0.012f) }, t.c(CUERO_OSC), style = Stroke(1f * u))
        // Las monedas sueltas.
        listOf(Triple(-0.04f, 0.004f, 0), Triple(-0.028f, 0.007f, 1), Triple(0.036f, 0.006f, 2)).forEach { (dx, dy, i) ->
            val p = Offset(pie.x + w * dx, pie.y + w * dy)
            drawOval(t.s(0.35f), Offset(p.x - w * 0.004f, p.y - w * 0.001f), Size(w * 0.011f, w * 0.004f))
            drawOval(t.c(if (i == 1) BRONCE else mezcla(FIERRO_LUZ, BRONCE, 0.25f)), Offset(p.x - w * 0.005f, p.y - w * 0.003f), Size(w * 0.01f, w * 0.004f))
            destello(Offset(p.x - w * 0.002f, p.y - w * 0.002f), 0.6f * u, t)
        }
    }

    /**
     * La espada envainada, apoyada en la pared por el rincón derecho: el pomo de rueda, la empuñadura forrada, la cruz recta,
     * la vaina de cuero con su contera y su boquilla, y el cinturón blanco enrollado en la vaina, con la hebilla y la punta
     * que cuelga.
     */
    private fun DrawScope.espada(punta: Offset, w: Float) {
        val u = density
        val t = tn(Offset(punta.x, punta.y - w * 0.2f))
        val x = punta.x; val y = punta.y
        val vainaA = w * 0.262f
        val vaina = Path().apply {
            moveTo(x - w * 0.006f, y); lineTo(x + w * 0.006f, y)
            lineTo(x + w * 0.011f, y - vainaA); lineTo(x - w * 0.011f, y - vainaA); close()
        }
        val cruz = Rect(x - w * 0.044f, y - vainaA - w * 0.012f, x + w * 0.044f, y - vainaA - w * 0.003f)
        val puno = Rect(x - w * 0.0055f, y - vainaA - w * 0.082f, x + w * 0.0055f, cruz.top)
        val pomo = Offset(x, puno.top - w * 0.012f)
        val sil = Path().apply { addPath(vaina); addRect(cruz); addRect(puno); addOval(Rect(pomo, w * 0.016f)) }
        val giro = -6.5f
        // La sombra en la pared, y la de la punta en la balda.
        if (!esNoche) translate(w * 0.02f, w * 0.01f) { rotate(giro, punta) { clipRect(0f, 0f, size.width, punta.y - w * 0.01f) { drawPath(sil, sombraMueble(0.24f)) } } }
        else translate(w * 0.03f, w * 0.006f) { rotate(giro, punta) { drawPath(sil, NOCHE_T.copy(alpha = 0.35f)) } }
        contacto(Offset(x + w * 0.004f, y), w * 0.03f, w * 0.006f, 0.45f, t)
        rotate(giro, punta) {
            // La vaina.
            drawPath(vaina, t.c(CUERO_OSC))
            clipPath(vaina) { cilindro(Rect(x - w * 0.011f, y - vainaA, x + w * 0.011f, y), t, 1.3f, luzDerecha(x)) }
            drawPath(poligono(x - w * 0.006f, y, x + w * 0.006f, y, x + w * 0.008f, y - w * 0.03f, x - w * 0.008f, y - w * 0.03f), t.c(FIERRO))
            drawRect(t.c(FIERRO), Offset(x - w * 0.012f, y - vainaA), Size(w * 0.024f, w * 0.014f))
            drawRect(t.b(0.45f), Offset(x - w * 0.012f, y - vainaA), Size(w * 0.024f, 1f * u))
            // El cinturón enrollado: vueltas en diagonal, con su hebilla, y la punta que cuelga.
            val cin = mezcla(LONA, CUERO_M, 0.15f)
            for (j in 0 until 5) {
                val yy = y - vainaA + w * 0.03f + j * w * 0.024f
                drawPath(poligono(x - w * 0.012f, yy, x + w * 0.012f, yy - w * 0.01f, x + w * 0.012f, yy - w * 0.0015f, x - w * 0.012f, yy + w * 0.0085f), t.c(cin))
                drawLine(t.s(0.3f), Offset(x - w * 0.012f, yy + w * 0.0085f), Offset(x + w * 0.012f, yy - w * 0.0015f), 0.8f * u)
                drawLine(t.b(0.4f), Offset(x - w * 0.012f, yy), Offset(x + w * 0.012f, yy - w * 0.01f), 0.6f * u)
            }
            val hb = Offset(x - w * 0.002f, y - vainaA + w * 0.08f)
            drawRect(t.c(FIERRO), Offset(hb.x - w * 0.007f, hb.y - w * 0.007f), Size(w * 0.014f, w * 0.012f), style = Stroke(1.4f * u))
            val cola = Path().apply { moveTo(x + w * 0.012f, y - vainaA + w * 0.12f); cubicTo(x + w * 0.03f, y - vainaA + w * 0.15f, x + w * 0.022f, y - vainaA + w * 0.19f, x + w * 0.03f, y - vainaA + w * 0.22f) }
            drawPath(cola, t.s(0.3f), style = Stroke(w * 0.009f))
            drawPath(cola, t.c(cin), style = Stroke(w * 0.0075f, cap = StrokeCap.Round))
            // La cruz recta, la empuñadura forrada de cuero con su cordón y el pomo de rueda.
            drawRect(t.c(FIERRO), cruz.topLeft, cruz.size)
            drawRect(t.b(0.5f), cruz.topLeft, Size(cruz.width, 1f * u))
            drawRect(t.s(0.3f), Offset(cruz.left, cruz.bottom - 1f * u), Size(cruz.width, 1f * u))
            drawRect(t.c(CUERO_M), puno.topLeft, puno.size)
            var yy = puno.top + w * 0.004f
            while (yy < puno.bottom) { drawLine(t.s(0.35f), Offset(puno.left, yy), Offset(puno.right, yy + w * 0.003f), 0.7f * u); yy += w * 0.006f }
            drawRect(t.b(0.25f), puno.topLeft, Size(puno.width * 0.35f, puno.height))
            drawCircle(t.c(FIERRO), w * 0.016f, pomo)
            drawCircle(t.c(mezcla(FIERRO, NOCHE_T, 0.3f)), w * 0.009f, pomo)
            drawArc(t.b(0.55f), 180f, 90f, false, Offset(pomo.x - w * 0.016f, pomo.y - w * 0.016f), Size(w * 0.032f, w * 0.032f), style = Stroke(1.2f * u))
            destello(Offset(pomo.x - w * 0.008f, pomo.y - w * 0.008f), 1.2f * u, t)
            destello(Offset(cruz.left + w * 0.01f, cruz.top + 1f * u), 1f * u, t)
        }
    }

    // ---------------------------------------------------------------- al pie

    /** Las alforjas de cuero, recostadas contra el banco y cortadas por el borde izquierdo: la bolsa, su solapa y la correa. */
    private fun DrawScope.alforjas(w: Float, banco: Float, alto: Float) {
        val u = density
        val pie = alto - w * 0.03f
        val t = tn(Offset(w * 0.1f, pie - w * 0.06f))
        // La bolsa de atrás, colgando por encima del banco.
        val atras = poligono(-w * 0.02f, banco - w * 0.035f, w * 0.13f, banco - w * 0.04f, w * 0.15f, banco + w * 0.05f, -w * 0.02f, banco + w * 0.06f)
        drawPath(atras, t.c(mezcla(CUERO_M, NOCHE_T, 0.25f)))
        drawRect(t.s(0.3f), Offset(-w * 0.02f, banco + w * 0.02f), Size(w * 0.17f, w * 0.04f))
        val cuerpo = Path().apply {
            moveTo(-w * 0.03f, pie - w * 0.11f)
            cubicTo(w * 0.06f, pie - w * 0.125f, w * 0.17f, pie - w * 0.12f, w * 0.2f, pie - w * 0.09f)
            cubicTo(w * 0.215f, pie - w * 0.05f, w * 0.21f, pie - w * 0.01f, w * 0.19f, pie)
            lineTo(-w * 0.03f, pie); close()
        }
        contacto(Offset(w * 0.1f, pie), w * 0.26f, w * 0.014f, 0.45f, t)
        drawPath(cuerpo, t.c(CUERO_M))
        clipPath(cuerpo) {
            drawRect(t.b(0.16f), Offset(0f, pie - w * 0.13f), Size(w * 0.21f, w * 0.03f))
            drawRect(t.s(0.25f), Offset(w * 0.15f, pie - w * 0.13f), Size(w * 0.08f, w * 0.13f))
            drawRect(t.s(0.2f), Offset(-w * 0.03f, pie - w * 0.03f), Size(w * 0.25f, w * 0.03f))
            pinceladas(Rect(-w * 0.03f, pie - w * 0.13f, w * 0.21f, pie), if (t.o) NOCHE_T else CUERO_OSC, 7501, 8, u, 0.14f)
            // Las costuras.
            drawPath(Path().apply { moveTo(w * 0.005f, pie - w * 0.1f); cubicTo(w * 0.08f, pie - w * 0.11f, w * 0.16f, pie - w * 0.105f, w * 0.19f, pie - w * 0.08f) }, t.c(LONA).copy(alpha = 0.5f), style = Stroke(0.7f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(3f * u, 2f * u))))
        }
        // La solapa con su correa y hebilla.
        val solapa = Path().apply {
            moveTo(-w * 0.03f, pie - w * 0.112f)
            cubicTo(w * 0.06f, pie - w * 0.127f, w * 0.16f, pie - w * 0.122f, w * 0.19f, pie - w * 0.095f)
            lineTo(w * 0.175f, pie - w * 0.06f)
            cubicTo(w * 0.12f, pie - w * 0.05f, w * 0.05f, pie - w * 0.05f, -w * 0.03f, pie - w * 0.06f); close()
        }
        translate(w * 0.004f, w * 0.006f) { drawPath(solapa, t.s(0.35f)) }
        drawPath(solapa, t.c(mezcla(CUERO_M, CUERO_OSC, 0.3f)))
        drawPath(solapa, t.b(0.25f), style = Stroke(0.8f * u))
        val cx = w * 0.1f
        drawRect(t.c(CUERO_OSC), Offset(cx - w * 0.008f, pie - w * 0.07f), Size(w * 0.016f, w * 0.05f))
        drawRect(t.c(BRONCE), Offset(cx - w * 0.011f, pie - w * 0.062f), Size(w * 0.022f, w * 0.014f), style = Stroke(1.4f * u))
        destello(Offset(cx - w * 0.008f, pie - w * 0.062f), 0.9f * u, t)
    }

    /** Un par de espuelas de pincho tiradas en las losas: la gargantilla en U, el pincho y las correas. */
    private fun DrawScope.espuelas(c: Offset, w: Float) {
        val u = density
        val t = tn(c)
        listOf(Triple(Offset(-0.03f, 0f), -12f, 0), Triple(Offset(0.04f, 0.012f), 165f, 1)).forEach { (d, g, i) ->
            val p = c + d * w
            rotate(g, p) {
                val forma = Path().apply {
                    moveTo(p.x - w * 0.035f, p.y - w * 0.008f)
                    cubicTo(p.x - w * 0.03f, p.y + w * 0.02f, p.x + w * 0.02f, p.y + w * 0.02f, p.x + w * 0.028f, p.y - w * 0.008f)
                }
                translate(w * 0.004f, w * 0.006f) { drawPath(forma, t.s(0.4f), style = Stroke(2.2f * u)) }
                drawPath(forma, t.c(FIERRO), style = Stroke(2.2f * u, cap = StrokeCap.Round))
                drawPath(forma, t.b(0.4f), style = Stroke(0.6f * u))
                // El pincho.
                drawPath(poligono(p.x - w * 0.004f, p.y + w * 0.012f, p.x + w * 0.004f, p.y + w * 0.012f, p.x, p.y + w * 0.032f), t.c(FIERRO))
                // La correa.
                drawPath(Path().apply { moveTo(p.x + w * 0.028f, p.y - w * 0.008f); cubicTo(p.x + w * 0.05f, p.y - w * 0.02f, p.x + w * 0.06f, p.y + w * 0.0f, p.x + w * 0.075f, p.y - w * 0.01f) }, t.c(CUERO_OSC), style = Stroke(1.8f * u))
            }
        }
    }

    /** Las botas de montar de cuero blando: una de pie, con la caña vencida en pliegues, y la otra caída. */
    private fun DrawScope.botas(w: Float, alto: Float) {
        val u = density
        val pie = alto - w * 0.028f
        val t = tn(Offset(w * 0.82f, pie - w * 0.08f))
        val col = mezcla(CUERO_OSC, CUERO_M, 0.45f)
        // La caída, tumbada hacia la derecha y cortada por el borde.
        val caida = Path().apply {
            moveTo(w * 0.83f, pie - w * 0.002f)
            cubicTo(w * 0.83f, pie - w * 0.04f, w * 0.87f, pie - w * 0.06f, w * 0.92f, pie - w * 0.06f)
            lineTo(w * 1.02f, pie - w * 0.07f); lineTo(w * 1.02f, pie - w * 0.01f); lineTo(w * 0.9f, pie); close()
        }
        contacto(Offset(w * 0.9f, pie), w * 0.2f, w * 0.014f, 0.45f, t)
        drawPath(caida, t.c(mezcla(col, NOCHE_T, 0.1f)))
        clipPath(caida) {
            drawRect(t.b(0.2f), Offset(w * 0.83f, pie - w * 0.07f), Size(w * 0.2f, w * 0.015f))
            drawRect(t.s(0.25f), Offset(w * 0.83f, pie - w * 0.02f), Size(w * 0.2f, w * 0.02f))
            listOf(0.93f, 0.96f, 0.99f).forEach { fx -> drawLine(t.s(0.35f), Offset(w * fx, pie - w * 0.065f), Offset(w * fx - w * 0.006f, pie - w * 0.005f), 0.8f * u) }
        }
        // La de pie: la puntera redonda hacia la izquierda, el empeine, el talón y la caña alta vencida.
        val bota = Path().apply {
            moveTo(w * 0.8f, pie)
            lineTo(w * 0.665f, pie)
            cubicTo(w * 0.645f, pie, w * 0.64f, pie - w * 0.03f, w * 0.665f, pie - w * 0.036f)
            cubicTo(w * 0.69f, pie - w * 0.042f, w * 0.72f, pie - w * 0.05f, w * 0.735f, pie - w * 0.07f)
            cubicTo(w * 0.74f, pie - w * 0.1f, w * 0.728f, pie - w * 0.13f, w * 0.738f, pie - w * 0.165f)
            lineTo(w * 0.805f, pie - w * 0.16f)
            cubicTo(w * 0.8f, pie - w * 0.13f, w * 0.812f, pie - w * 0.1f, w * 0.806f, pie - w * 0.07f)
            cubicTo(w * 0.802f, pie - w * 0.04f, w * 0.815f, pie - w * 0.015f, w * 0.8f, pie)
            close()
        }
        sombraEnPared(bota, Offset(w * 0.77f, pie - w * 0.08f), size.height, 0.0f)
        contacto(Offset(w * 0.74f, pie), w * 0.19f, w * 0.014f, 0.45f, t)
        drawPath(bota, t.c(col))
        clipPath(bota) {
            cilindro(Rect(w * 0.725f, pie - w * 0.17f, w * 0.82f, pie), t, 1.2f, luzDerecha(w * 0.75f))
            // El empeine: luz arriba, sombra en la suela.
            drawRect(t.b(0.18f), Offset(w * 0.64f, pie - w * 0.04f), Size(w * 0.09f, w * 0.012f))
            // Los pliegues de la caña vencida.
            listOf(0.13f, 0.105f, 0.08f).forEach { fy ->
                drawPath(Path().apply { moveTo(w * 0.73f, pie - w * fy); cubicTo(w * 0.75f, pie - w * (fy + 0.01f), w * 0.78f, pie - w * (fy - 0.006f), w * 0.81f, pie - w * (fy + 0.004f)) }, t.s(0.38f), style = Stroke(1.3f * u))
                drawPath(Path().apply { moveTo(w * 0.73f, pie - w * (fy + 0.005f)); cubicTo(w * 0.75f, pie - w * (fy + 0.015f), w * 0.78f, pie - w * (fy - 0.001f), w * 0.81f, pie - w * (fy + 0.009f)) }, t.b(0.25f), style = Stroke(0.7f * u))
            }
            drawRect(t.c(mezcla(col, NOCHE_T, 0.4f)), Offset(w * 0.64f, pie - w * 0.007f), Size(w * 0.18f, w * 0.007f))
        }
        // La boca de la caña, por dentro.
        drawOval(t.c(mezcla(col, NOCHE_T, 0.55f)), Offset(w * 0.738f, pie - w * 0.17f), Size(w * 0.067f, w * 0.014f))
        drawArc(t.b(0.35f), 180f, 180f, false, Offset(w * 0.738f, pie - w * 0.17f), Size(w * 0.067f, w * 0.014f), style = Stroke(0.8f * u))
    }

    // ---------------------------------------------------------------- los compartimentos

    override fun DrawScope.compartimento(c: EstadoCajon, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val ab = c.abierto.coerceIn(0f, 1f)
        // Dónde está este hueco en el mueble (en fracciones del ancho), para saber cuánto le llega de la vela.
        val anchoH = (1f - 2f * lado - hueco * (cols - 1)) / cols
        val altoH = anchoH / proporcion
        val centro = Offset(lado + c.col * (anchoH + hueco) + anchoH / 2, corona + hueco + c.fila * (altoH + hueco) + altoH / 2)
        val dentro = c.cols == cols
        val k = if (!oscuro) 0f else if (dentro) luz(centro) else 0.5f
        val dia = if (oscuro) 0f else 0.07f * (1f - c.fila / maxOf(1f, c.filas - 1f)) + (if (c.col == 0) 0.02f else 0f)
        val t = Tono(oscuro, k, dia)
        val der = oscuro && (!dentro || centro.x < LUZ.x)
        if (c.vacio) { vacio(w, h, c, t, der); return }
        val m = mezclar(c.semilla)
        when {
            c.sinTema -> saco(w, h, c, t, der, ab)
            else -> when (Math.floorMod(m, 3)) {
                0 -> arca(w, h, c, m, t, der, ab)
                1 -> cofre(w, h, c, m, t, der, ab)
                else -> fajos(w, h, c, m, t, der, ab)
            }
        }
        // Al abrirlo, sube un poco de luz de dentro.
        if (ab > 0f) mancha(Offset(w / 2, h * 0.3f), w * 0.45f, if (oscuro) LLAMA.copy(alpha = 0.2f * ab) else luzMueble(0.3f * ab))
    }

    /** La tablilla de yeso con el tema, clavada ([clavada]) o atada; con filete rojo si está escrita. */
    private fun DrawScope.tablilla(w: Float, h: Float, t: Tono, clavada: Boolean, filete: Boolean, s: Int) {
        val u = density
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawRect(t.s(0.35f), Offset(r.left + 1.6f * u, r.top + 2.2f * u), r.size)
        val fondo = if (t.o) mezcla(YESO_LEJOS, mezcla(YESO, LLAMA, 0.12f), (1.3f * t.k.pow(0.6f)).coerceAtMost(1f)) else t.c(YESO)
        drawRect(fondo, r.topLeft, r.size)
        // El canto de la tabla, abajo y a la derecha.
        drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        drawRect(t.s(0.18f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.9f * u))
        // El yeso saltado en una esquina.
        val e = r.height * 0.22f
        if (Math.floorMod(s, 2) == 0) drawPath(poligono(r.right - e * 1.6f, r.bottom, r.right, r.bottom, r.right, r.bottom - e), mezcla(fondo, TABLA_LUZ, 0.55f))
        else drawPath(poligono(r.left, r.top, r.left + e * 1.4f, r.top, r.left, r.top + e), mezcla(fondo, TABLA_LUZ, 0.55f))
        if (filete) drawRect(CRUZ.copy(alpha = 0.55f), Offset(r.left + 2.6f * u, r.top + 2.4f * u), Size(r.width - 5.2f * u, r.height - 4.8f * u), style = Stroke(0.8f * u))
        if (clavada) listOf(r.left + 1.8f * u, r.right - 1.8f * u).forEach { x -> clavo(Offset(x, r.center.y), 1.4f * u, t) }
        else listOf(r.left + 3f * u, r.right - 3f * u).forEach { x -> drawCircle(t.s(0.5f), 1f * u, Offset(x, r.top + 2.4f * u)) }
    }

    /** Las cartas lacradas de pie en la abertura entre [x0] y [x1] sobre [boca]: más y más altas con más fichas. */
    private fun DrawScope.cartas(x0: Float, x1: Float, boca: Float, sube: Float, h: Float, c: EstadoCajon, t: Tono) {
        val n = c.n.coerceIn(0, 10)
        val cuantas = (2 + n / 2).coerceIn(2, 7)
        for (i in 0 until cuantas) {
            val f = (i + 0.5f) / cuantas
            val cw = (x1 - x0) * (0.14f + 0.06f * azar(c.semilla, i + 20))
            val x = x0 + (x1 - x0 - cw) * (f * 0.92f + 0.04f * azar(c.semilla, i + 30))
            val alto = sube * (0.55f + 0.5f * azar(c.semilla, i)) + h * 0.03f
            rotate((azar(c.semilla, i + 40) - 0.5f) * 10f, Offset(x + cw / 2, boca)) {
                carta(Rect(x, boca - alto, x + cw, boca + h * 0.02f), t, c.semilla + i)
            }
        }
    }

    /**
     * El arca de roble (natural, o con la almagra desvaída según la semilla): la tapa con su cara de arriba y su canto, el
     * cuerpo de tres tablas, el costado en perspectiva, los flejes de hierro con remaches, las escuadras, dos aldabas con sus
     * candados y la tablilla clavada; asoman las cartas bajo la tapa.
     */
    private fun DrawScope.arca(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val pisa = h * PISA
        val pintada = Math.floorMod(m ushr 3, 3) == 2
        val madera = if (pintada) mezcla(ALMAGRA, TABLA, 0.4f) else if (Math.floorMod(m ushr 3, 3) == 1) mezcla(TABLA, TABLA_LUZ, 0.35f) else TABLA
        val cuerpo = Rect(w * 0.06f, h * 0.43f, w * 0.92f, pisa)
        val lado = w * 0.035f
        val sube = h * (0.03f + 0.013f * c.n.coerceIn(0, 8)) + ab * h * 0.12f
        val tapa = Rect(w * 0.045f, h * 0.29f - sube, w * 0.935f, h * 0.43f - sube)
        // Sombra en la pared y de contacto.
        drawRect(t.s(0.22f), Offset(cuerpo.right, cuerpo.top + h * 0.04f), Size(w * 0.06f, cuerpo.height - h * 0.04f))
        contacto(Offset(cuerpo.center.x + w * 0.05f, pisa), cuerpo.width * 1.1f, h * 0.05f, 0.42f, t)
        // La boca abierta: lo oscuro de dentro y las cartas.
        drawRect(t.s(0.85f), Offset(cuerpo.left + w * 0.01f, tapa.bottom), Size(cuerpo.width - w * 0.02f, cuerpo.top - tapa.bottom + 2f * u))
        cartas(cuerpo.left + w * 0.04f, cuerpo.right - w * 0.04f, cuerpo.top + h * 0.02f, sube, h, c, t)
        // El costado en perspectiva y el cuerpo.
        drawPath(poligono(cuerpo.right, cuerpo.top, cuerpo.right + lado, cuerpo.top - lado * 0.6f, cuerpo.right + lado, cuerpo.bottom - lado * 0.6f, cuerpo.right, cuerpo.bottom), t.c(mezcla(madera, NOCHE_T, 0.35f)))
        drawRect(t.c(madera), cuerpo.topLeft, cuerpo.size)
        vetas(cuerpo, (if (t.o) NOCHE_T else TABLA_OSC).copy(alpha = 0.35f), c.semilla, 0.9f, grosor = 0.6f, ondulacion = 0.4f)
        listOf(1f / 3f, 2f / 3f).forEach { f ->
            val y = cuerpo.top + cuerpo.height * f
            drawLine(t.s(0.45f), Offset(cuerpo.left, y), Offset(cuerpo.right, y), 1.1f * u)
            drawLine(t.b(0.25f), Offset(cuerpo.left, y + 1.1f * u), Offset(cuerpo.right, y + 1.1f * u), 0.6f * u)
        }
        if (pintada) for (j in 0 until 7) drawOval(t.c(TABLA).copy(alpha = 0.6f), Offset(cuerpo.left + cuerpo.width * azar(m, j), cuerpo.top + cuerpo.height * azar(m + 1, j)), Size(w * (0.03f + 0.04f * azar(m + 2, j)), h * 0.025f))
        if (!t.o) { drawRect(luzMueble(0.12f), cuerpo.topLeft, Size(cuerpo.width * 0.2f, cuerpo.height)); drawRect(sombraMueble(0.14f), Offset(cuerpo.right - cuerpo.width * 0.25f, cuerpo.top), Size(cuerpo.width * 0.25f, cuerpo.height)) }
        else drawRect(t.s(0.25f), Offset(if (der) cuerpo.left else cuerpo.right - cuerpo.width * 0.35f, cuerpo.top), Size(cuerpo.width * 0.35f, cuerpo.height))
        // La tapa: su cara de arriba (vista un poco desde arriba), su canto y su costado.
        val caraT = tapa.top - h * 0.06f
        drawPath(poligono(tapa.left, tapa.top, tapa.right, tapa.top, tapa.right + lado, caraT, tapa.left + lado, caraT), t.c(mezcla(madera, LUZ_MUEBLE, 0.12f)))
        drawPath(poligono(tapa.right, tapa.top, tapa.right + lado, caraT, tapa.right + lado, tapa.bottom - lado * 0.6f, tapa.right, tapa.bottom), t.c(mezcla(madera, NOCHE_T, 0.35f)))
        drawRect(t.c(mezcla(madera, TABLA_OSC, 0.15f)), tapa.topLeft, tapa.size)
        vetas(tapa, (if (t.o) NOCHE_T else TABLA_OSC).copy(alpha = 0.3f), c.semilla + 3, 0.7f, grosor = 0.5f, ondulacion = 0.4f)
        drawRect(t.b(0.5f), tapa.topLeft, Size(tapa.width, 1.2f * u))
        drawRect(t.s(0.4f), Offset(tapa.left, tapa.bottom - 1.6f * u), Size(tapa.width, 1.6f * u))
        // Los flejes de hierro: dos verticales (tapa y cuerpo) y el del pie, con sus remaches; las escuadras.
        listOf(0.15f, 0.83f).forEach { fx ->
            val x = w * fx
            listOf(Rect(x - w * 0.018f, tapa.top, x + w * 0.018f, tapa.bottom), Rect(x - w * 0.018f, cuerpo.top, x + w * 0.018f, cuerpo.bottom)).forEach { r ->
                drawRect(t.c(FIERRO), r.topLeft, r.size)
                drawRect(t.b(0.35f), r.topLeft, Size(1f * u, r.height))
                drawRect(t.s(0.3f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
                var yy = r.top + h * 0.035f
                while (yy < r.bottom - h * 0.02f) { clavo(Offset(x, yy), 1.3f * u, t, FIERRO_LUZ); yy += h * 0.075f }
            }
            drawPath(poligono(x - w * 0.018f, caraT + (tapa.top - caraT) * (if (fx < 0.5f) 0.0f else 0f), x + w * 0.018f, caraT, x + w * 0.018f + lado * 0f, tapa.top, x - w * 0.018f, tapa.top), t.c(mezcla(FIERRO, FIERRO_LUZ, 0.2f)))
        }
        drawRect(t.c(FIERRO), Offset(cuerpo.left, cuerpo.bottom - h * 0.045f), Size(cuerpo.width, h * 0.045f))
        drawRect(t.b(0.35f), Offset(cuerpo.left, cuerpo.bottom - h * 0.045f), Size(cuerpo.width, 1f * u))
        // Las aldabas y los candados.
        listOf(0.33f, 0.61f).forEach { fx ->
            val x = w * fx
            val ald = Rect(x - w * 0.012f, tapa.bottom - h * 0.03f, x + w * 0.012f, cuerpo.top + h * 0.06f)
            drawRect(t.s(0.35f), Offset(ald.left + 1.5f * u, ald.top + 1.5f * u), ald.size)
            drawRect(t.c(FIERRO), ald.topLeft, ald.size)
            drawRect(t.b(0.35f), ald.topLeft, Size(1f * u, ald.height))
            val cand = Offset(x + w * 0.004f, ald.bottom + h * 0.035f)
            drawArc(t.c(FIERRO), 180f, 180f, false, Offset(cand.x - w * 0.016f, cand.y - h * 0.065f), Size(w * 0.032f, h * 0.06f), style = Stroke(2f * u))
            drawRect(t.s(0.4f), Offset(cand.x - w * 0.022f + 1.5f * u, cand.y - h * 0.035f + 1.5f * u), Size(w * 0.044f, h * 0.06f))
            drawRoundRect(t.c(mezcla(FIERRO, NOCHE_T, 0.15f)), Offset(cand.x - w * 0.022f, cand.y - h * 0.035f), Size(w * 0.044f, h * 0.06f), CornerRadius(1.5f * u))
            drawRect(t.b(0.4f), Offset(cand.x - w * 0.022f, cand.y - h * 0.035f), Size(w * 0.044f, 1f * u))
            drawCircle(t.s(0.6f), 1.1f * u, Offset(cand.x, cand.y - h * 0.005f))
            destello(Offset(cand.x - w * 0.015f, cand.y - h * 0.03f), 1f * u, t)
        }
        filo(tapa, t, der)
        filo(Rect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom), t, der)
        tablilla(w, h, t, clavada = true, filete = true, s = m)
    }

    /**
     * El cofrecillo de documentos de cuero hervido (negro, leonado o rojizo según la semilla): la tapa abombada con sus fajas
     * de hierro curvas y su labrado, el cuerpo con las esquinas de hierro y tachuelas, la cerradura de bocallave; las cartas
     * asoman bajo la tapa entreabierta.
     */
    private fun DrawScope.cofre(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val pisa = h * PISA
        val cuero = when (Math.floorMod(m ushr 3, 3)) { 0 -> Color(0xFF2E2622); 1 -> mezcla(CUERO_M, LONA, 0.38f); else -> mezcla(LACRE, CUERO_OSC, 0.45f) }
        val cuerpo = Rect(w * 0.09f, h * 0.48f, w * 0.89f, pisa)
        val sube = h * (0.025f + 0.012f * c.n.coerceIn(0, 8)) + ab * h * 0.12f
        val tb = h * 0.48f - sube
        val tapaR = Rect(w * 0.075f, h * 0.17f - sube, w * 0.905f, tb)
        val tapa = Path().apply {
            moveTo(tapaR.left, tapaR.bottom); lineTo(tapaR.left, tapaR.bottom - tapaR.height * 0.3f)
            cubicTo(tapaR.left, tapaR.top + tapaR.height * 0.05f, tapaR.right, tapaR.top + tapaR.height * 0.05f, tapaR.right, tapaR.bottom - tapaR.height * 0.3f)
            lineTo(tapaR.right, tapaR.bottom); close()
        }
        drawRect(t.s(0.22f), Offset(cuerpo.right, cuerpo.top + h * 0.04f), Size(w * 0.06f, cuerpo.height - h * 0.04f))
        translate(w * 0.05f, h * 0.03f) { drawPath(tapa, t.s(0.18f)) }
        contacto(Offset(cuerpo.center.x + w * 0.05f, pisa), cuerpo.width * 1.1f, h * 0.05f, 0.42f, t)
        drawRect(t.s(0.85f), Offset(cuerpo.left + w * 0.01f, tb), Size(cuerpo.width - w * 0.02f, cuerpo.top - tb + 2f * u))
        cartas(cuerpo.left + w * 0.05f, cuerpo.right - w * 0.05f, cuerpo.top + h * 0.02f, sube, h, c, t)
        // El cuerpo.
        drawRect(t.c(cuero), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            if (!t.o) { drawRect(luzMueble(0.12f), cuerpo.topLeft, Size(cuerpo.width * 0.18f, cuerpo.height)); drawRect(sombraMueble(0.16f), Offset(cuerpo.right - cuerpo.width * 0.22f, cuerpo.top), Size(cuerpo.width * 0.22f, cuerpo.height)) }
            else drawRect(t.s(0.25f), Offset(if (der) cuerpo.left else cuerpo.right - cuerpo.width * 0.35f, cuerpo.top), Size(cuerpo.width * 0.35f, cuerpo.height))
            drawRect(t.s(0.2f), Offset(cuerpo.left, cuerpo.bottom - cuerpo.height * 0.15f), Size(cuerpo.width, cuerpo.height * 0.15f))
            pinceladas(cuerpo, if (t.o) NOCHE_T else mezcla(cuero, LUZ_MUEBLE, 0.3f), c.semilla, 8, u, 0.14f)
        }
        // La tapa abombada, con su labrado de rombos y su luz.
        drawPath(tapa, t.c(mezcla(cuero, LUZ_MUEBLE, 0.06f)))
        clipPath(tapa) {
            drawRect(t.b(0.3f), Offset(tapaR.left, tapaR.top), Size(tapaR.width, tapaR.height * 0.2f))
            drawRect(t.s(0.3f), Offset(tapaR.left, tapaR.bottom - tapaR.height * 0.25f), Size(tapaR.width, tapaR.height * 0.25f))
            drawRect(t.b(0.16f), Offset(tapaR.left, tapaR.top + tapaR.height * 0.2f), Size(tapaR.width, tapaR.height * 0.18f))
            var x = tapaR.left - tapaR.height
            while (x < tapaR.right) {
                drawLine(t.s(0.25f), Offset(x, tapaR.bottom), Offset(x + tapaR.height, tapaR.top), 0.7f * u)
                drawLine(t.s(0.25f), Offset(x, tapaR.top), Offset(x + tapaR.height, tapaR.bottom), 0.7f * u)
                x += w * 0.08f
            }
        }
        drawPath(tapa, t.s(0.4f), style = Stroke(1f * u))
        // Las fajas de hierro curvas sobre la tapa y rectas en el cuerpo, con tachuelas.
        listOf(0.25f, 0.73f).forEach { fx ->
            val x = w * fx
            clipPath(tapa) {
                drawRect(t.c(FIERRO), Offset(x - w * 0.016f, tapaR.top), Size(w * 0.032f, tapaR.height))
                drawRect(t.b(0.35f), Offset(x - w * 0.016f, tapaR.top), Size(1f * u, tapaR.height))
            }
            drawRect(t.c(FIERRO), Offset(x - w * 0.016f, cuerpo.top), Size(w * 0.032f, cuerpo.height))
            drawRect(t.b(0.35f), Offset(x - w * 0.016f, cuerpo.top), Size(1f * u, cuerpo.height))
            clavo(Offset(x, tapaR.top + tapaR.height * 0.35f), 1.3f * u, t, FIERRO_LUZ)
            clavo(Offset(x, tapaR.bottom - tapaR.height * 0.15f), 1.3f * u, t, FIERRO_LUZ)
            clavo(Offset(x, cuerpo.top + h * 0.03f), 1.3f * u, t, FIERRO_LUZ)
            clavo(Offset(x, cuerpo.bottom - h * 0.03f), 1.3f * u, t, FIERRO_LUZ)
        }
        // Las esquinas de hierro.
        listOf(cuerpo.left, cuerpo.right).forEach { x ->
            val s = if (x == cuerpo.left) 1f else -1f
            drawPath(poligono(x, cuerpo.bottom, x + s * w * 0.06f, cuerpo.bottom, x, cuerpo.bottom - h * 0.1f), t.c(FIERRO))
            drawPath(poligono(x, tapaR.bottom, x + s * w * 0.05f, tapaR.bottom, x, tapaR.bottom - h * 0.08f), t.c(FIERRO))
        }
        // La cerradura con su bocallave, entre la tapa y la tablilla.
        val ce = Rect(w * 0.44f, tb - h * 0.04f, w * 0.54f, cuerpo.top + h * 0.06f)
        drawRect(t.s(0.35f), Offset(ce.left + 1.5f * u, ce.top + 1.5f * u), ce.size)
        drawRoundRect(t.c(FIERRO), ce.topLeft, ce.size, CornerRadius(2f * u))
        drawRect(t.b(0.4f), ce.topLeft, Size(ce.width, 1f * u))
        drawCircle(t.s(0.8f), w * 0.006f, Offset(ce.center.x, ce.center.y - h * 0.006f))
        drawRect(t.s(0.8f), Offset(ce.center.x - w * 0.002f, ce.center.y - h * 0.006f), Size(w * 0.004f, h * 0.025f))
        destello(Offset(ce.left + w * 0.012f, ce.top + h * 0.012f), 1f * u, t)
        if (t.o && t.k > 0.08f) drawPath(tapa, LLAMA.copy(alpha = (0.6f * t.k).coerceAtMost(0.5f)), style = Stroke(1.3f * u))
        filo(cuerpo, t, der)
        tablilla(w, h, t, clavada = true, filete = true, s = m)
    }

    /**
     * Los fajos de cartas apilados (más con más fichas), cada uno atado en cruz con su cordel y con sellos de cera pendientes
     * que cuelgan por delante, y el estuche de pergaminos de pie detrás, con su tapa y su correa (de cuero rojo, de madera o
     * de hojalata según la semilla); la tablilla, atada al fajo de delante.
     */
    private fun DrawScope.fajos(w: Float, h: Float, c: EstadoCajon, m: Int, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val pisa = h * PISA
        // El estuche de pie, detrás a la derecha.
        val estuche = Rect(w * 0.73f, h * 0.1f - ab * h * 0.08f, w * 0.87f, pisa - h * 0.02f)
        val colE = when (Math.floorMod(m ushr 3, 3)) { 0 -> mezcla(LACRE, CUERO_M, 0.3f); 1 -> TABLA_LUZ; else -> mezcla(FIERRO_LUZ, FIERRO, 0.35f) }
        drawRect(t.s(0.22f), Offset(estuche.right, estuche.top + h * 0.05f), Size(w * 0.05f, estuche.height - h * 0.05f))
        drawRect(t.c(colE), estuche.topLeft, estuche.size)
        clipRect(estuche.left, estuche.top, estuche.right, estuche.bottom) { cilindro(estuche, t, 1.4f, der) }
        val tapaE = Rect(estuche.left - w * 0.008f, estuche.top - h * 0.02f, estuche.right + w * 0.008f, estuche.top + h * 0.09f)
        drawRect(t.c(mezcla(colE, TABLA_OSC, 0.3f)), tapaE.topLeft, tapaE.size)
        clipRect(tapaE.left, tapaE.top, tapaE.right, tapaE.bottom) { cilindro(tapaE, t, 1.4f, der) }
        drawOval(t.c(mezcla(colE, LUZ_MUEBLE, 0.2f)), Offset(tapaE.left, tapaE.top - h * 0.012f), Size(tapaE.width, h * 0.024f))
        listOf(0.35f, 0.75f).forEach { fy -> drawRect(t.c(CUERO_OSC), Offset(estuche.left, estuche.top + estuche.height * fy), Size(estuche.width, h * 0.03f)) }
        // Una punta de pergamino enrollado que asoma de la tapa cuando se abre.
        if (ab > 0f) drawRect(t.c(PERG), Offset(estuche.left + w * 0.02f, tapaE.bottom), Size(w * 0.04f, h * 0.06f * ab))
        filo(estuche, t, der)
        // Los fajos: de abajo arriba; cada uno, un bloque de cartas con sus capas, atado.
        val n = c.n.coerceIn(0, 10)
        val cuantos = (3 + n / 5).coerceIn(3, 4)
        val fh = h * (if (cuantos == 3) 0.17f else 0.15f)
        var y = pisa
        contacto(Offset(w * 0.4f + w * 0.05f, pisa), w * 0.75f, h * 0.05f, 0.42f, t)
        val sellos = mutableListOf<Offset>()
        for (i in 0 until cuantos) {
            val dx = (azar(c.semilla, 70 + i) - 0.5f) * w * 0.06f
            val g = (azar(c.semilla, 80 + i) - 0.5f) * 3f
            val r = Rect(w * 0.06f + dx, y - fh, w * 0.72f + dx, y)
            val sube = if (i == cuantos - 1) ab * h * 0.06f else 0f
            rotate(g, r.center) {
                translate(0f, -sube) {
                    val col = when (Math.floorMod(c.semilla + i, 3)) { 0 -> PERG; 1 -> mezcla(PERG, LONA, 0.4f); else -> mezcla(PERG, CALIZA, 0.45f) }
                    drawRect(t.s(0.25f), Offset(r.left + w * 0.02f, r.top + h * 0.01f), r.size)
                    drawRect(t.c(col), r.topLeft, r.size)
                    // Las capas de las cartas en el canto, y alguna que sobresale.
                    for (j in 1 until 5) drawLine(t.s(0.22f), Offset(r.left, r.top + r.height * j / 5f), Offset(r.right - w * 0.01f * azar(c.semilla, j + i * 7), r.top + r.height * j / 5f), 0.6f * u)
                    drawRect(t.c(mezcla(col, LUZ_MUEBLE, 0.2f)), Offset(r.left - w * 0.015f, r.top + r.height * 0.4f), Size(w * 0.03f, r.height * 0.2f))
                    if (!t.o) { drawRect(luzMueble(0.12f), r.topLeft, Size(r.width * 0.2f, r.height)); drawRect(sombraMueble(0.12f), Offset(r.right - r.width * 0.22f, r.top), Size(r.width * 0.22f, r.height)) }
                    drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
                    drawRect(t.s(0.3f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
                    // El cordel en cruz.
                    val cx = r.left + r.width * (0.3f + 0.1f * azar(c.semilla, 90 + i))
                    drawLine(t.c(mezcla(LONA, CUERO_M, 0.3f)), Offset(cx, r.top), Offset(cx, r.bottom), 1.4f * u)
                    drawLine(t.c(mezcla(LONA, CUERO_M, 0.3f)), Offset(r.left, r.center.y), Offset(r.right, r.center.y), 1.2f * u)
                    drawCircle(t.c(mezcla(LONA, CUERO_M, 0.3f)), 2f * u, Offset(cx, r.center.y))
                    filo(r, t, der)
                }
            }
            y -= fh
        }
        // Las cartas sueltas que asoman del fajo de arriba.
        cartas(w * 0.12f, w * 0.62f, y + h * 0.02f, h * (0.02f + 0.012f * n) + ab * h * 0.08f, h, c, t)
        // Los sellos pendientes del fajo de arriba, por encima de la tablilla.
        listOf(0.16f, 0.36f, 0.55f).forEachIndexed { i, fx -> if (i < 2 + n / 4) sellos.add(Offset(w * (fx + 0.04f * azar(c.semilla, 100 + i)), y + h * 0.02f)) }
        // Los sellos pendientes: un cordelito y el disco de cera.
        sellos.forEachIndexed { i, p ->
            val l = h * (0.04f + 0.03f * azar(c.semilla, 110 + i))
            val q = Offset(p.x + w * 0.006f, p.y + l)
            drawLine(t.c(mezcla(LONA, CRUZ, 0.3f)), p, q, 0.9f * u)
            drawCircle(t.s(0.35f), h * 0.032f, q + Offset(1.2f * u, 1.6f * u))
            drawCircle(t.c(if (i % 2 == 0) LACRE else mezcla(LACRE, SEBO, 0.35f)), h * 0.032f, q)
            drawCircle(t.s(0.25f), h * 0.02f, q, style = Stroke(0.6f * u))
            drawCircle(t.b(0.45f), h * 0.01f, q - Offset(h * 0.012f, h * 0.012f))
        }
        // La tablilla atada con su cordel al fajo de delante.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawLine(t.c(LONA), Offset(r.left + 3f * u, r.top + 2.4f * u), Offset(w * 0.35f, r.top - h * 0.06f), 0.9f * u)
        drawLine(t.c(LONA), Offset(r.right - 3f * u, r.top + 2.4f * u), Offset(w * 0.35f, r.top - h * 0.06f), 0.9f * u)
        tablilla(w, h, t, clavada = false, filete = true, s = m)
    }

    /**
     * Lo que no tiene tema: un saco de lona panzudo con su remiendo, atado en la boca con un cordel; de la boca abierta salen
     * cartas sueltas (más con más fichas), alguna caída al lado, y la tablilla en blanco cuelga del cordel.
     */
    private fun DrawScope.saco(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean, ab: Float) {
        val u = density
        val pisa = h * PISA
        val cuello = h * 0.3f
        val saco = Path().apply {
            moveTo(w * 0.4f, cuello)
            cubicTo(w * 0.2f, cuello + h * 0.06f, w * 0.06f, h * 0.5f, w * 0.08f, h * 0.78f)
            cubicTo(w * 0.09f, h * 0.9f, w * 0.14f, pisa, w * 0.3f, pisa)
            lineTo(w * 0.78f, pisa)
            cubicTo(w * 0.9f, pisa, w * 0.94f, h * 0.88f, w * 0.93f, h * 0.74f)
            cubicTo(w * 0.92f, h * 0.5f, w * 0.78f, cuello + h * 0.06f, w * 0.58f, cuello)
            close()
        }
        translate(w * 0.04f, h * 0.025f) { drawPath(saco, t.s(0.2f)) }
        contacto(Offset(w * 0.55f, pisa), w * 0.9f, h * 0.05f, 0.42f, t)
        // Una carta caída a los pies.
        rotate(-8f, Offset(w * 0.1f, pisa)) { drawRect(t.s(0.3f), Offset(w * 0.02f, pisa - h * 0.03f), Size(w * 0.14f, h * 0.035f)); drawRect(t.c(PERG), Offset(w * 0.015f, pisa - h * 0.04f), Size(w * 0.14f, h * 0.035f)); drawCircle(t.c(LACRE), 2.6f * u, Offset(w * 0.085f, pisa - h * 0.022f)) }
        // La boca abierta con las cartas.
        val sube = h * (0.06f + 0.015f * c.n.coerceIn(0, 10)) + ab * h * 0.1f
        cartas(w * 0.3f, w * 0.68f, cuello + h * 0.04f, sube, h, c, t)
        drawPath(poligono(w * 0.34f, cuello - h * 0.04f, w * 0.64f, cuello - h * 0.05f, w * 0.6f, cuello + h * 0.02f, w * 0.38f, cuello + h * 0.02f), t.c(mezcla(LONA, LUZ_MUEBLE, 0.1f)))
        drawPath(saco, t.c(LONA))
        clipPath(saco) {
            cilindro(Rect(w * 0.06f, cuello, w * 0.94f, pisa), t, 0.6f, der)
            // Las arrugas que bajan del cuello.
            listOf(0.3f, 0.42f, 0.55f, 0.68f).forEachIndexed { i, fx ->
                drawPath(Path().apply { moveTo(w * 0.49f, cuello); cubicTo(w * (0.49f + (fx - 0.49f) * 0.4f), h * 0.42f, w * fx, h * 0.5f, w * (fx + 0.03f * (i - 1.5f)), h * 0.6f) }, t.s(0.25f), style = Stroke(1.4f * u))
            }
            drawRect(t.s(0.25f), Offset(w * 0.06f, pisa - h * 0.08f), Size(w * 0.9f, h * 0.08f))
            // La trama y el remiendo.
            var y = cuello
            while (y < pisa) { drawLine(t.s(0.06f), Offset(w * 0.06f, y), Offset(w * 0.94f, y), 0.5f * u); y += 2.4f * u }
            val rem = Rect(w * 0.7f, h * 0.42f, w * 0.84f, h * 0.53f)
            drawRect(t.c(mezcla(LONA, CUERO_M, 0.3f)), rem.topLeft, rem.size)
            drawRect(t.s(0.3f), rem.topLeft, rem.size, style = Stroke(0.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.5f * u))))
        }
        // El cordel del cuello, con su nudo y las puntas.
        val nudo = Offset(w * 0.47f, cuello + h * 0.01f)
        drawLine(t.c(mezcla(LONA, CUERO_M, 0.45f)), Offset(w * 0.37f, cuello + h * 0.02f), Offset(w * 0.6f, cuello), 2f * u)
        drawCircle(t.c(mezcla(LONA, CUERO_M, 0.45f)), 2.6f * u, nudo)
        // La tablilla en blanco colgando del cordel.
        val r = Rect(w * rotulo.left, h * rotulo.top, w * rotulo.right, h * rotulo.bottom)
        drawLine(t.c(mezcla(LONA, CUERO_M, 0.45f)), nudo, Offset(r.left + 3f * u, r.top + 2.4f * u), 0.9f * u)
        drawLine(t.c(mezcla(LONA, CUERO_M, 0.45f)), nudo, Offset(r.right - 3f * u, r.top + 2.4f * u), 0.9f * u)
        filo(Rect(w * 0.1f, cuello, w * 0.92f, pisa), t, true)
        tablilla(w, h, t, clavada = false, filete = false, s = c.semilla)
    }

    /** Un hueco libre: una palmatoria de hierro con la vela apagada, o un cuenco de madera torneado, según la semilla. */
    private fun DrawScope.vacio(w: Float, h: Float, c: EstadoCajon, t: Tono, der: Boolean) {
        val u = density
        val pisa = h * PISA
        if (Math.floorMod(c.semilla ushr 2, 2) == 0) {
            val cx = w * 0.42f
            contacto(Offset(cx + w * 0.03f, pisa), w * 0.3f, h * 0.04f, 0.42f, t)
            // El platillo con su asa.
            drawOval(t.c(FIERRO), Offset(cx - w * 0.13f, pisa - h * 0.04f), Size(w * 0.26f, h * 0.06f))
            drawOval(t.c(mezcla(FIERRO, NOCHE_T, 0.3f)), Offset(cx - w * 0.11f, pisa - h * 0.04f), Size(w * 0.22f, h * 0.035f))
            drawArc(t.b(0.45f), 180f, 90f, false, Offset(cx - w * 0.13f, pisa - h * 0.04f), Size(w * 0.26f, h * 0.06f), style = Stroke(1f * u))
            drawArc(t.c(FIERRO), 90f, 240f, false, Offset(cx + w * 0.11f, pisa - h * 0.05f), Size(w * 0.06f, h * 0.06f), style = Stroke(2f * u))
            // El cabo de vela apagada, con la mecha negra.
            val v = Rect(cx - w * 0.03f, pisa - h * 0.22f, cx + w * 0.03f, pisa - h * 0.03f)
            drawRect(t.c(SEBO), v.topLeft, v.size)
            clipRect(v.left, v.top, v.right, v.bottom) { cilindro(v, t, 1.2f, der) }
            drawOval(t.c(mezcla(SEBO, CALIZA_OSC, 0.35f)), Offset(v.left, v.top - h * 0.01f), Size(v.width, h * 0.02f))
            drawLine(t.c(mezcla(SEBO, LUZ_MUEBLE, 0.3f)), Offset(v.left + w * 0.012f, v.top), Offset(v.left + w * 0.012f, v.top + v.height * 0.6f), 2.2f * u, StrokeCap.Round)
            drawLine(Color(0xFF1C1612), Offset(cx, v.top), Offset(cx + w * 0.006f, v.top - h * 0.03f), 1.1f * u, StrokeCap.Round)
        } else {
            val cx = w * 0.5f
            val bw = w * 0.42f; val bh = h * 0.2f
            contacto(Offset(cx + w * 0.03f, pisa), bw * 1.1f, h * 0.045f, 0.42f, t)
            val cuenco = Path().apply {
                moveTo(cx - bw / 2, pisa - bh)
                cubicTo(cx - bw / 2, pisa - bh * 0.2f, cx - bw * 0.25f, pisa, cx, pisa)
                cubicTo(cx + bw * 0.25f, pisa, cx + bw / 2, pisa - bh * 0.2f, cx + bw / 2, pisa - bh)
                close()
            }
            drawPath(cuenco, t.c(TABLA_LUZ))
            clipPath(cuenco) {
                cilindro(Rect(cx - bw / 2, pisa - bh, cx + bw / 2, pisa), t, 1.3f, der)
                listOf(0.35f, 0.62f).forEach { f -> drawLine(t.s(0.3f), Offset(cx - bw / 2, pisa - bh * (1f - f)), Offset(cx + bw / 2, pisa - bh * (1f - f)), 0.7f * u) }
                vetas(Rect(cx - bw / 2, pisa - bh, cx + bw / 2, pisa), (if (t.o) NOCHE_T else TABLA_OSC).copy(alpha = 0.25f), 7601, 0.6f, grosor = 0.5f)
            }
            drawOval(t.c(TABLA_OSC), Offset(cx - bw / 2, pisa - bh - h * 0.03f), Size(bw, h * 0.06f))
            drawArc(t.b(0.45f), 180f, 180f, false, Offset(cx - bw / 2, pisa - bh - h * 0.03f), Size(bw, h * 0.06f), style = Stroke(1.2f * u))
            // Una cuchara de madera apoyada en el borde.
            drawLine(t.s(0.3f), Offset(cx + bw * 0.1f + 2f * u, pisa - bh + 2f * u), Offset(cx + bw * 0.7f + 2f * u, pisa - bh - h * 0.12f + 2f * u), 2.4f * u)
            drawLine(t.c(mezcla(TABLA_LUZ, LONA, 0.4f)), Offset(cx + bw * 0.1f, pisa - bh), Offset(cx + bw * 0.7f, pisa - bh - h * 0.12f), 2.4f * u, StrokeCap.Round)
        }
    }

    override val rotulo = Rect(0.08f, 0.56f, 0.9f, 0.83f)
    override fun tintaRotulo(oscuro: Boolean) = TINTA
    override val letraRotulo: FontFamily = LetraExtra.cruzada
    override val salida = 0f
    override val placa = Rect(0.36f, 0.1f, 0.64f, 0.34f)
    override fun tintaPlaca(oscuro: Boolean) = TINTA

    // ---------------------------------------------------------------- el interior y las fichas (sin cambios)

    /** Un escudo de punta: arriba recto, abajo en punta. */
    private fun escudo(c: Offset, a: Float, h: Float) = Path().apply {
        moveTo(c.x - a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y - h / 2); lineTo(c.x + a / 2, c.y)
        quadraticTo(c.x + a / 2, c.y + h * 0.35f, c.x, c.y + h / 2); quadraticTo(c.x - a / 2, c.y + h * 0.35f, c.x - a / 2, c.y); close()
    }

    override fun DrawScope.interior(oscuro: Boolean) {
        val u = density
        val w = size.width; val h = size.height
        // Un paño de lana blanca, con su trama, y la cruz patada grande y tenue.
        val pano = if (oscuro) mezcla(LINO, Color.Black, 0.6f) else LINO
        drawRect(pano)
        var y = 0f
        while (y < h) { drawLine(Color.Black.copy(alpha = 0.04f), Offset(0f, y), Offset(w, y), 0.8f * u); y += 3 * u }
        var x = 0f
        while (x < w) { drawLine(Color.Black.copy(alpha = 0.03f), Offset(x, 0f), Offset(x, h), 0.8f * u); x += 3 * u }
        drawPath(cruzPatada(Offset(w / 2, h * 0.42f), w * 0.36f), ROJO.copy(alpha = if (oscuro) 0.16f else 0.1f))
        // Las paredes del arca: tablas de roble con sus escuadras de hierro.
        val pl = 14 * u
        listOf(Rect(0f, 0f, w, pl), Rect(0f, h - pl, w, h), Rect(0f, 0f, pl, h), Rect(w - pl, 0f, w, h)).forEach { r ->
            drawRect(t(ROBLE, oscuro), r.topLeft, r.size)
            vetas(r, ROBLE_OSC.copy(alpha = 0.5f), (r.left + r.top).toInt(), 1f, vertical = r.height > r.width, grosor = 0.6f, ondulacion = 0.4f)
        }
        escuadrasPared(w, h, pl, 46 * u, t(HIERRO, oscuro))
        sombraInterior(Rect(pl, pl, w - pl, h - pl), 18 * u, 0.3f)
    }

    override fun tintaInterior(oscuro: Boolean) = if (oscuro) LINO else TINTA
    override val pared: Dp = 16.dp
    override val columnas = 2
    override val lineas = 5

    override fun forma(semilla: Int): Shape = RoundedCornerShape(1.dp)

    override fun DrawScope.adornoFicha(p: Pagina, grande: Boolean, semilla: Int) {
        val u = density
        val w = size.width; val h = size.height
        val g = if (grande) 1.7f else 1f
        drawRect(LINO.copy(alpha = 0.4f))
        // Los dobleces de la carta, en tercios.
        listOf(h / 3, h * 2 / 3).forEach { y ->
            drawLine(Color.Black.copy(alpha = 0.07f), Offset(0f, y), Offset(w, y), 1f * u)
            drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, y + 1 * u), Offset(w, y + 1 * u), 1f * u)
        }
        // El sello de cera pendiente, con su cordón y la cruz.
        val sello = Offset(w / 2, h - 10 * u * g)
        val ranura = h - 26 * u * g
        drawLine(Color.Black.copy(alpha = 0.4f), Offset(w / 2 - 4 * u * g, ranura), Offset(w / 2 + 4 * u * g, ranura), 1.2f * u)
        drawLine(ROJO.copy(alpha = 0.8f), Offset(w / 2 - 2 * u * g, ranura), Offset(sello.x - 2 * u * g, sello.y), 1.2f * u * g)
        drawLine(ROJO.copy(alpha = 0.8f), Offset(w / 2 + 2 * u * g, ranura), Offset(sello.x + 2 * u * g, sello.y), 1.2f * u * g)
        drawCircle(CERA, 7.5f * u * g, sello)
        drawCircle(mezcla(CERA, Color.Black, 0.35f), 6 * u * g, sello, style = Stroke(0.9f * u * g))
        drawPath(cruzPatada(sello, 3.8f * u * g), mezcla(CERA, Color.Black, 0.35f))
    }

    override val margenes = MargenesFicha(12.dp, 14.dp, 28.dp, 22.dp, 28.dp, 48.dp)
    override val proporcionFicha = 0.88f
    override val anchoGrande = 0.92f
    override val proporcionGrande = 0.72f
    override val barra = false
    override fun altoBarra(grande: Boolean): Dp = 0.dp
    override val apilado = false

    override fun DrawScope.marcaPostura(id: String, p: Pagina) {
        val s = size.minDimension; val c = center
        when (id) {
            // El escudo rojo con su palomita.
            "hecho" -> {
                drawPath(escudo(c, s * 0.7f, s * 0.86f), ROJO)
                drawPath(Path().apply { moveTo(c.x - s * 0.18f, c.y - s * 0.02f); lineTo(c.x - s * 0.04f, c.y + s * 0.13f); lineTo(c.x + s * 0.2f, c.y - s * 0.16f) }, BLANCO, style = Stroke(s * 0.09f, cap = StrokeCap.Round))
            }
            "convence" -> drawPath(cruzPatada(c, s * 0.44f), ROJO)
            // El baucent: el escudo partido, negro arriba y blanco abajo.
            "piensa" -> {
                val e = escudo(c, s * 0.7f, s * 0.86f)
                clipPath(e) { drawRect(NEGRO, Offset(0f, 0f), Size(size.width, c.y)); drawRect(BLANCO, Offset(0f, c.y), Size(size.width, size.height)) }
                drawPath(e, NEGRO, style = Stroke(s * 0.06f))
            }
            "duda" -> circuloDuda(p.tenue)
            "desacuerdo" -> aspa(ROJO)
            else -> {}
        }
    }

    /** El sello de cera roja, con la cruz. */
    override fun DrawScope.marcaFijada(p: Pagina) {
        val s = size.minDimension; val c = center
        drawCircle(CERA, s * 0.44f, c)
        drawCircle(mezcla(CERA, Color.Black, 0.35f), s * 0.36f, c, style = Stroke(s * 0.06f))
        drawPath(cruzPatada(c, s * 0.24f), mezcla(CERA, Color.Black, 0.35f))
    }

    override val desorden = 0f
}
