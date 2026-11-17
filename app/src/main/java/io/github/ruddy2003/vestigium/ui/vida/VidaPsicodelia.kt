package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrazosCiudad
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.grano
import io.github.ruddy2003.vestigium.ui.TrazosCiudad.vineta
import io.github.ruddy2003.vestigium.ui.TrazosTinta
import io.github.ruddy2003.vestigium.ui.TrazosTinta.bandas
import io.github.ruddy2003.vestigium.ui.TrazosTinta.trama
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Psicodelia, versión 2 (`docs/diseno/temas/psicodelia.md`, «Los muebles»; la base en `psicodelia_base.md`): Vida en el
 * rincón de trabajo de David, el dibujante de carteles, en su cuarto de una victoriana del Haight hacia 1967. Es el mismo
 * cuarto que la repisa de tablas de la biblioteca y la cajonera de planos del Ideario: **la mesa de dibujo contra la pared,
 * con su lámpara de brazo**, y nada más que el mueble y la pared (sin ventana, cielo ni calle; `MUEBLES.md`, versión 2).
 * Objetos con uso: la plumilla y los frascos de tinta, la lata de café con los pinceles, la regla T colgada de su clavo, la
 * cinta de pintor, los bocetos, el cartel de la semana clavado, el pase de autobús, la radio con el dial en el 106.9, la
 * entrada de un baile y el recorte del periódico del barrio. Sin flores, arcoíris, lámpara de cera ni símbolo de la paz;
 * ninguna persona dibujada; ningún cartel real (el cartel es el disco del día hecho sol, propio).
 *
 * **Un solo dibujo, dos pintores** ([Pincel]): la geometría (qué hay y dónde) es la misma en las dos caras; cambia cómo se
 * pinta cada material ([Mat]).
 * - Ciudad · claro: la luz de la tarde entra de arriba a la izquierda desde fuera del cuadro (una mancha de sol en la
 *   pared), cada volumen con su lado de luz y su lado de sombra, sombras de color café, pinceladas, grano de película y
 *   viñeta ([TrazosCiudad]).
 * - Ciudad · oscuro: la lámpara de brazo encendida es la luz: halo en la pared, cono sobre lo que alumbra, filo cálido en lo
 *   que tiene cerca; lo lejano se hunde en el café negro del cuarto.
 * - Psicodelia · claro: el mueble impreso en tintas planas sobre papel crema ([TrazosTinta]): las superficies grandes en
 *   trama de líneas de su tinta, las chicas en tinta llena, la tinta azul negra que graba los contornos y la trama de las
 *   sombras (siempre a la derecha de cada volumen: la luz sigue viniendo de la izquierda).
 * - Psicodelia · oscuro: sobre negro de terciopelo; la luz es la proyección de las luces líquidas en la pared (el
 *   retroproyector queda fuera del cuadro): manchas planas de magenta y ultramar (y de bermellón desde i = 0.5) que tiñen los
 *   contornos de lo que tienen delante. La lámpara está apagada.
 * - La intensidad ([EstadoTema.intensidad], solo en la psicodelia): las tintas suben de croma, el registro se corre (el
 *   magenta a la derecha, el bermellón a la izquierda), entra la tercera tinta, las bandas y los anillos del cartel crecen
 *   y, desde i = 0.9, se salen de su papel; de noche, las manchas de luz crecen, se deforman y echan anillos. Los nichos no
 *   cambian nunca: el texto y los datos se leen igual con i = 1.
 *
 * Los muebles:
 * - Social: la pared sobre la mesa de dibujo, con el pliego de papel de estraza clavado donde David traza a la gente del
 *   barrio (el nicho). La lámpara de brazo entra por la izquierda; en el borde de la mesa, la radio con el dial en el 106.9,
 *   la taza, los frascos y la lata de los pinceles; a la derecha, la regla T y la escuadra colgadas, y el pase y la entrada
 *   metidos en la esquina del pliego.
 * - Mapas: la mesa de dibujo de frente: el tablero de pino con el pliego sujeto con cinta (el nicho), la canaleta de los
 *   lápices con la plumilla y el frasco destapado, las patas de caballete sobre la duela. A la izquierda, los bocetos y la
 *   tira de pruebas de tinta; a la derecha, la lámpara de brazo prensada al tablero, que cruza por delante del cartel de la
 *   semana.
 * - Otras vidas: la caja de tipos de imprenta, de pie sobre la mesa contra la pared, con sus cinco cajetines grandes (el
 *   nicho). Encima, los carteles enrollados, el frasco, el sencillo en su funda y el pase; la lámpara entra por la izquierda;
 *   a la derecha, la entrada del baile clavada y la regla T apoyada.
 * - Íconos: la torre del puente con la niebla que entra; la hoja entintada de negro que la plumilla raspa hasta descubrir
 *   el cartel; y el plano del barrio, con el Panhandle, y tres chinchetas unidas por el camino.
 * - Notas (Vida y Diario): el corcho sobre la mesa de dibujo, en su marco de pino, con un boceto, el recorte del periódico
 *   del barrio y la entrada del baile prendidos en el marco; abajo, el borde de la mesa con sus cosas, y la lámpara que se
 *   asoma por la derecha. Cada nota es un papel del oficio (la ficha rayada, la hoja de libreta, la etiqueta de cartón, el
 *   boleto de transbordo) con su chincheta; la fijada lleva la etiqueta del disco del día.
 */
internal object VidaPsicodelia : DisenoVida {
    // ---------------------------------------------------------------- La paleta (la ciudad; la psicodelia sale de TrazosTinta)

    /** La pared del cuarto: el yeso cálido de la tarde. */
    private val PARED = Color(0xFFD3C3A6)
    /** El pino barnizado de la mesa y del marco, y la madera oscura de las patas y de la radio. */
    private val PINO = Color(0xFFA67C4E)
    private val MADERA = TrazosCiudad.MADERA
    /** El metal pavonado de la lámpara y de la plumilla. */
    private val METAL = TrazosCiudad.POSTE
    /** El verde botella del esmalte de la lámpara (el del estudio, secundario de la ciudad clara). */
    private val VERDE = Color(0xFF3D5838)
    /** El papel de dibujo, el de periódico, el de estraza y el corcho. */
    private val PAPEL = Color(0xFFEDE3CC)
    private val PERIODICO = Color(0xFFD8CBAE)
    private val ESTRAZA = Color(0xFFB48F63)
    private val CORCHO = Color(0xFFA27A4E)
    /** Las tintas de la ciudad: el naranja quemado, el ocre mostaza, el ladrillo, la etiqueta del disco y el café casi negro. */
    private val NARANJA = Color(0xFF9C4219)
    private val OCRE = Color(0xFF7A5713)
    private val LADRILLO = TrazosCiudad.COCHE
    private val ETIQUETA = TrazosCiudad.ETIQUETA
    private val TINTA = Color(0xFF2A2119)
    /** La duela del piso. */
    private val DUELA = Color(0xFF5C3D29)
    /** El papel de la psicodelia clara un poco más blanco que el fondo, para las hojas. */
    private val HOJA_TINTA = Color(0xFFFBF5E8)

    override fun area(m: MuebleVida): Rect = when (m) {
        MuebleVida.SOCIAL -> Rect(0.20f, 0.08f, 0.84f, 0.65f)
        MuebleVida.MAPAS -> Rect(0.25f, 0.10f, 0.75f, 0.64f)
        MuebleVida.OTRAS_VIDAS -> Rect(0.13f, 0.31f, 0.87f, 0.81f)
        MuebleVida.NOTAS -> Rect(0.08f, 0.12f, 0.92f, 0.78f)
    }

    // ---------------------------------------------------------------- El pintor: cómo se pinta cada material en cada cara

    /** Las tintas de la psicodelia: el papel (o el crema sobre negro), la que graba y las tres de color, más su sobreimpresión. */
    private enum class Ti { PAPEL, GRABADO, MAGENTA, ULTRAMAR, BERMELLON, VIOLETA }

    /**
     * Un material: su color en la ciudad, la tinta con la que se imprime en la psicodelia y si allí va en tinta llena
     * ([lleno]) o en trama de líneas sobre el papel (las superficies grandes, para que el cartel respire).
     */
    private enum class Mat(val ciudad: Color, val ti: Ti, val lleno: Boolean) {
        PARED(Color(0xFFD3C3A6), Ti.PAPEL, false),
        PINO(Color(0xFFA67C4E), Ti.ULTRAMAR, false),
        MADERA(Color(0xFF6E4630), Ti.ULTRAMAR, true),
        METAL(Color(0xFF3E3128), Ti.GRABADO, true),
        ESMALTE(Color(0xFF3D5838), Ti.MAGENTA, true),
        PAPEL(Color(0xFFEDE3CC), Ti.PAPEL, true),
        PERIODICO(Color(0xFFD8CBAE), Ti.PAPEL, true),
        ESTRAZA(Color(0xFFB48F63), Ti.BERMELLON, false),
        CORCHO(Color(0xFFA27A4E), Ti.MAGENTA, false),
        NARANJA(Color(0xFF9C4219), Ti.BERMELLON, true),
        OCRE(Color(0xFF7A5713), Ti.VIOLETA, true),
        LADRILLO(Color(0xFF7A3424), Ti.MAGENTA, true),
        ETIQUETA(Color(0xFFC8662A), Ti.BERMELLON, true),
        VERDE(Color(0xFF3D5838), Ti.ULTRAMAR, true),
        TINTA(Color(0xFF2A2119), Ti.GRABADO, true),
        VIDRIO(Color(0xFF66705F), Ti.ULTRAMAR, false),
        DUELA(Color(0xFF5C3D29), Ti.VIOLETA, false),
        /** El faldón de la mesa: madera oscura, pero grande, así que en la psicodelia va en trama. */
        FALDON(Color(0xFF6E4630), Ti.ULTRAMAR, false),
    }

    /** Una mancha de luz líquida en la pared (psicodelia · oscuro): su centro, su radio, su tinta y su forma. */
    private class Mancha(val c: Offset, val r: Float, val t: Ti, val semilla: Int)

    /**
     * Cómo se pinta este dibujo: la cara ([tinta] = psicodelia), el modo ([o] = oscuro), la intensidad [i] (0 en la
     * ciudad), un dp en píxeles ([u]), de dónde viene la luz ([foco], con su [alcance]: la tarde fuera del cuadro arriba a la
     * izquierda, o la lámpara de noche) y las manchas de luz líquida de la noche psicodélica.
     */
    private class Pincel(val tinta: Boolean, val o: Boolean, val i: Float, val u: Float, val foco: Offset, val alcance: Float, val manchas: List<Mancha>) {
        val m = TrazosTinta.medidas(i)
        /** La lámpara de brazo alumbra (ciudad · oscuro). */
        val lampara: Boolean get() = o && !tinta
    }

    /** La cara de Psicodelia y su intensidad (la intensidad solo cuenta en la cara psicodelia). */
    private fun DrawScope.pincel(oscuro: Boolean, lamparaEn: Offset, alcanceLampara: Float, manchas: List<Mancha>): Pincel {
        val tinta = EstadoTema.caraDe(TemaId.PSICODELIA) == Cara.INTENSA
        val i = if (tinta) EstadoTema.intensidad.let { if (it.isNaN()) 0f else it.coerceIn(0f, 1f) } else 0f
        val w = size.width; val h = size.height
        val (foco, alcance) = if (oscuro && !tinta) lamparaEn to alcanceLampara else Offset(-0.12f * w, -0.25f * h) to max(w, h) * 0.95f
        return Pincel(tinta, oscuro, i, max(w / 360f, 0.01f), foco, alcance, manchas)
    }

    private fun Pincel.ink(t: Ti): Color = when (t) {
        Ti.PAPEL -> if (o) TrazosTinta.CREMA else HOJA_TINTA
        Ti.GRABADO -> TrazosTinta.grabado(o)
        Ti.MAGENTA -> TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, o, i)
        Ti.ULTRAMAR -> TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, o, i)
        // Con i = 0 el cartel va a dos tintas: lo del bermellón se imprime en magenta hasta que entra la tercera.
        Ti.BERMELLON -> lerp(TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, o, i), TrazosTinta.tinta(TrazosTinta.Tinta.BERMELLON, o, i), m.bermellon)
        Ti.VIOLETA -> TrazosTinta.sobreimpresion(TrazosTinta.tinta(TrazosTinta.Tinta.MAGENTA, o, i), TrazosTinta.tinta(TrazosTinta.Tinta.ULTRAMAR, o, i), o)
    }

    /** El fondo de la impresión: el papel o el negro de terciopelo. */
    private fun Pincel.fondoTinta(): Color = TrazosTinta.papel(o)

    /** Cuánto se corre la tinta [t] con el registro de la intensidad, en píxeles. */
    private fun DrawScope.corrido(p: Pincel, t: Ti): Offset = when (t) {
        Ti.MAGENTA -> TrazosTinta.corrimiento(TrazosTinta.Tinta.MAGENTA, p.m.registro) * density
        Ti.BERMELLON -> TrazosTinta.corrimiento(if (p.m.bermellon > 0f) TrazosTinta.Tinta.BERMELLON else TrazosTinta.Tinta.MAGENTA, p.m.registro) * density
        else -> Offset.Zero
    }

    /** Cuánto le llega una luz (en [foco], con alcance [radio]) a un punto [q]: 1 al lado, 0 lejos, con caída suave. */
    private fun cerca(q: Offset, foco: Offset, radio: Float): Float {
        if (radio <= 0f) return 0f
        val dx = q.x - foco.x; val dy = q.y - foco.y
        val k = 1f - sqrt(dx * dx + dy * dy) / radio
        return if (k <= 0f) 0f else (k * k * (3f - 2f * k)).coerceIn(0f, 1f)
    }

    /**
     * El color de la ciudad para el material [c] en una cara de volumen con luz [k] (de −1, sombra plena, a 1, luz plena) en
     * el punto [ref]. De día: hacia el crema de lo que mira al sol o hacia el café de la sombra, y un poco más claro cerca de
     * la mancha de sol. De noche: hundido en el café negro del cuarto, salvo lo que alumbra la lámpara.
     */
    private fun Pincel.ciudad(c: Color, k: Float, ref: Offset): Color {
        val f = cerca(ref, foco, alcance)
        return if (!o) {
            val b = if (k >= 0f) TrazosCiudad.alSol(c, 0.30f * k) else TrazosCiudad.enSombra(c, 0.40f * -k)
            TrazosCiudad.alSol(b, 0.18f * f)
        } else {
            val hondo = lerp(c, TrazosCiudad.PARED_NOCHE, 0.74f)
            val alumbrado = lerp(c, TrazosCiudad.LAMPARA_LUZ, 0.22f)
            val g = f * (if (k >= 0f) 0.8f + 0.2f * k else 0.5f * (1f + k))
            lerp(hondo, alumbrado, g.coerceIn(0f, 1f))
        }
    }

    /** El color con que se pinta [mat] en la cara que toca (en la psicodelia, su tinta). */
    private fun Pincel.color(mat: Mat, k: Float, ref: Offset): Color = if (tinta) ink(mat.ti) else ciudad(mat.ciudad, k, ref)

    /** La tinta de la mancha de luz más cercana a [q] y cuánto le llega (psicodelia · oscuro). */
    private fun Pincel.luzLiquida(q: Offset): Pair<Color, Float> {
        var mejor: Mancha? = null
        var fuerza = 0f
        for (mc in manchas) {
            if (mc.t == Ti.BERMELLON && m.bermellon <= 0f) continue
            val f = cerca(q, mc.c, mc.r * (2.2f + 0.8f * i))
            if (f > fuerza) { fuerza = f; mejor = mc }
        }
        return (mejor?.let { ink(it.t) } ?: TrazosTinta.CREMA) to fuerza
    }

    /** El contorno que graba: la tinta azul negra sobre papel; sobre negro, el crema tenue teñido por la luz líquida cercana. */
    private fun Pincel.contorno(ref: Offset): Color {
        if (!o) return TrazosTinta.GRABAR
        val (c, f) = luzLiquida(ref)
        return lerp(TrazosTinta.CREMA.copy(alpha = 0.55f), c, f)
    }

    /**
     * Pinta [forma] con el material [mat], como una cara de volumen con luz [k] (ver [ciudad]) alrededor de [ref]. En la
     * psicodelia: tinta llena o trama de su tinta sobre el papel, con la sombra en trama (a la derecha), el registro corrido
     * por la intensidad y el contorno grabado.
     */
    private fun DrawScope.pinta(p: Pincel, forma: Path, mat: Mat, k: Float, ref: Offset, contorno: Boolean = true, grosor: Float = 0.75f) {
        if (!p.tinta) {
            drawPath(forma, p.ciudad(mat.ciudad, k, ref))
            return
        }
        val tinta = p.ink(mat.ti)
        val d = corrido(p, mat.ti)
        val u = p.u
        translate(d.x, d.y) {
            when {
                // Lo del color del papel: el papel (o el crema) lleno, con la sombra en trama de la tinta que graba.
                mat.ti == Ti.PAPEL -> {
                    drawPath(forma, if (p.o) TrazosTinta.NEGRO else tinta)
                    if (p.o) trama(forma, 2.6f * u, 0.55f * u, TrazosTinta.CREMA.copy(alpha = if (k < -0.2f) 0.35f else 0.6f), 0f)
                    else if (k < -0.2f) trama(forma, 2.4f * u, 0.5f * u, TrazosTinta.GRABAR.copy(alpha = 0.7f), 45f)
                }
                mat.lleno -> {
                    if (p.o && k < -0.2f) {
                        drawPath(forma, TrazosTinta.NEGRO)
                        trama(forma, 2.0f * u, 0.8f * u, tinta, 45f)
                    } else {
                        drawPath(forma, tinta)
                        if (!p.o && k < -0.2f) trama(forma, 2.3f * u, 0.55f * u, TrazosTinta.GRABAR.copy(alpha = 0.75f), 45f)
                    }
                }
                else -> {
                    drawPath(forma, p.fondoTinta())
                    val paso = if (k < -0.2f) 1.9f else if (k > 0.4f) 3.6f else 2.7f
                    trama(forma, paso * u, (if (p.o) 0.7f else 0.8f) * u, tinta, if (k < -0.2f) 45f else 0f)
                }
            }
        }
        if (contorno) drawPath(forma, p.contorno(ref), style = Stroke(grosor * u, join = StrokeJoin.Round))
    }

    /**
     * La sombra que una pieza ([forma]) deja en lo que tiene detrás o debajo, a [dist] dp: de día, abajo a la derecha, en
     * café; de noche en la ciudad, lejos de la lámpara; en la psicodelia clara, en trama de la tinta que graba. En la
     * psicodelia oscura no hay sombra: la luz es la de la pared.
     */
    private fun DrawScope.sombra(p: Pincel, forma: Path, ref: Offset, dist: Float, fuerza: Float = 1f) {
        if (p.tinta && p.o) return
        val u = p.u
        val dir = if (p.lampara) {
            val dx = ref.x - p.foco.x; val dy = ref.y - p.foco.y
            val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
            Offset(dx / l, dy / l * 0.8f + 0.2f)
        } else Offset(0.6f, 0.8f)
        val d = dir * (dist * u)
        if (!p.tinta) {
            val a = (if (p.o) 0.42f else 0.24f) * fuerza
            translate(d.x * 0.5f, d.y * 0.5f) { drawPath(forma, TrazosCiudad.SOMBRA.copy(alpha = a * 0.5f)) }
            translate(d.x, d.y) { drawPath(forma, TrazosCiudad.SOMBRA.copy(alpha = a)) }
        } else translate(d.x, d.y) { trama(forma, 2.0f * u, 0.5f * u, TrazosTinta.GRABAR.copy(alpha = 0.8f * fuerza), 45f) }
    }

    // ---------------------------------------------------------------- Ayudas de forma

    private fun caja(r: Rect): Path = Path().apply { addRect(r) }
    private fun redondo(r: Rect, radio: Float): Path = Path().apply { addRoundRect(RoundRect(r, CornerRadius(max(radio, 0f)))) }
    private fun ovalo(r: Rect): Path = Path().apply { addOval(r) }
    private fun circulo(c: Offset, r: Float): Path = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    private fun poligono(a: Offset, b: Offset, c: Offset, d: Offset? = null): Path = Path().apply {
        moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y)
        if (d != null) lineTo(d.x, d.y)
        close()
    }
    private fun DrawScope.fr(x: Float, y: Float) = Offset(size.width * x, size.height * y)
    private fun DrawScope.fr(l: Float, t: Float, r: Float, b: Float) = Rect(size.width * l, size.height * t, size.width * r, size.height * b)

    /** Un número estable de 0 a 1 (la misma irregularidad cada vez). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    /** Todo lo que se dibuja dentro de [bloque], menos lo que caería sobre [nicho]. */
    private fun DrawScope.fueraDe(nicho: Rect, bloque: DrawScope.() -> Unit) {
        val q = Path().apply { addRect(Rect(-size.width, -size.height, size.width * 2f, size.height * 2f)); addRect(nicho); fillType = PathFillType.EvenOdd }
        clipPath(q) { bloque() }
    }

    /**
     * Una caja de frente con volumen: la cara de delante, el canto de arriba que mira a la luz, el costado derecho en sombra
     * y el filo de abajo. [bisel] es el ancho del canto y del costado, en px.
     */
    private fun DrawScope.volumen(p: Pincel, r: Rect, mat: Mat, bisel: Float, k: Float = 0.1f) {
        if (r.width <= 0f || r.height <= 0f) return
        val b = min(bisel, min(r.width, r.height) * 0.3f)
        pinta(p, caja(r), mat, k, r.center)
        if (b <= 0.5f) return
        pinta(p, caja(Rect(r.left, r.top, r.right, r.top + b)), mat, 0.85f, Offset(r.center.x, r.top), contorno = false)
        pinta(p, caja(Rect(r.right - b, r.top + b, r.right, r.bottom)), mat, -0.8f, Offset(r.right, r.center.y), contorno = false)
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.5f else 0.25f), Offset(r.left, r.bottom - b * 0.5f), Size(r.width, b * 0.5f))
        else drawPath(caja(r), p.contorno(r.center), style = Stroke(0.75f * p.u))
    }

    // ---------------------------------------------------------------- La pared y la luz

    /**
     * La pared del cuarto en [r]. Ciudad · claro: el yeso con sus pinceladas, la mancha de sol de la tarde que entra de
     * arriba a la izquierda (sin ventana a la vista) y el lado derecho más hondo. Ciudad · oscuro: el café negro, con el halo
     * de la lámpara. Psicodelia · claro: el papel, con la trama apenas visible en la parte que no alumbra la tarde.
     * Psicodelia · oscuro: el negro de terciopelo con las manchas de luz líquida.
     */
    private fun DrawScope.pared(p: Pincel, r: Rect, semilla: Int, bandasEn: List<Offset> = emptyList()) {
        val w = size.width; val h = size.height; val u = p.u
        // La mancha de sol de la tarde: un trapecio inclinado, más ancho abajo, que cae de arriba a la izquierda.
        val sol = poligono(Offset(r.left - 0.04f * w, r.top), Offset(r.left + 0.40f * w, r.top), Offset(r.left + 0.26f * w, r.top + 0.92f * h), Offset(r.left - 0.18f * w, r.top + 0.92f * h))
        when {
            !p.tinta && !p.o -> {
                drawRect(PARED, r.topLeft, r.size)
                pinceladas(r, TrazosCiudad.AL_SOL, semilla, 34, u, 0.10f)
                pinceladas(r, TrazosCiudad.SOMBRA, semilla + 1, 22, u, 0.05f)
                // La luz, en tres pasadas que se ensanchan: borde blando sin degradado.
                for (k in 0..2) {
                    val e = (2 - k) * 6f * u
                    translate(-e * 0.5f, 0f) { drawPath(sol, TrazosCiudad.SOL_VELADO.copy(alpha = 0.22f)) }
                    if (e > 0f) translate(e * 0.5f, 0f) { drawPath(sol, TrazosCiudad.SOL_VELADO.copy(alpha = 0.06f)) }
                }
                // El lado de la pared que no alcanza la tarde, más hondo hacia la derecha (escalones planos).
                for (k in 0..3) drawRect(TrazosCiudad.SOMBRA.copy(alpha = 0.035f), Offset(r.left + r.width * (0.55f + 0.11f * k), r.top), Size(r.width, r.height))
            }
            !p.tinta && p.o -> {
                drawRect(TrazosCiudad.PARED_NOCHE, r.topLeft, r.size)
                pinceladas(r, TrazosCiudad.LAMPARA_LUZ, semilla, 26, u, 0.025f)
                // El halo de la lámpara en la pared: discos que se suman hacia la bombilla.
                val pasos = 18
                for (k in 0 until pasos) {
                    val s = k / pasos.toFloat()
                    drawCircle(TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.022f * (0.4f + 2.2f * s * s)), p.alcance * (1f - 0.94f * s), p.foco)
                }
                for (k in 0..2) drawCircle(TrazosCiudad.LAMPARA.copy(alpha = 0.05f), p.alcance * (0.05f + 0.04f * k), p.foco)
            }
            p.tinta && !p.o -> {
                drawRect(TrazosTinta.PAPEL, r.topLeft, r.size)
                val fuera = Path().apply { addRect(r); addPath(sol); fillType = PathFillType.EvenOdd }
                trama(fuera, 3.2f * u, 0.45f * u, TrazosTinta.GRABAR.copy(alpha = 0.16f), 0f)
            }
            else -> {
                drawRect(TrazosTinta.NEGRO, r.topLeft, r.size)
                manchasDeLuz(p)
            }
        }
        if (p.tinta) for (c in bandasEn) bandasDePared(p, c, r)
    }

    /** El contorno ondulado de una mancha de luz líquida: un círculo que se deforma con tres ondas, más con la intensidad. */
    private fun manchaForma(c: Offset, r: Float, semilla: Int, deforma: Float): Path = Path().apply {
        val f1 = azar(semilla, 1) * 6.28f; val f2 = azar(semilla, 2) * 6.28f; val f3 = azar(semilla, 3) * 6.28f
        val pasos = 56
        for (s in 0..pasos) {
            val a = s * 2f * PI.toFloat() / pasos
            val rr = r * (1f + deforma * (0.16f * sin(2f * a + f1) + 0.10f * sin(3f * a + f2) + 0.06f * sin(5f * a + f3)))
            val x = c.x + rr * cos(a) * 1.18f; val y = c.y + rr * sin(a) * 0.9f
            if (s == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Las luces líquidas proyectadas en la pared (psicodelia · oscuro): cada mancha en capas planas de su tinta (el borde
     * tenue, el cuerpo, el corazón), que se suman donde se pisan como una sobreimpresión. Con la intensidad, crecen, se
     * deforman y echan anillos alternos; la de bermellón entra desde i = 0.5.
     */
    private fun DrawScope.manchasDeLuz(p: Pincel) {
        val i = p.i
        val crece = 1f + 0.45f * i
        val deforma = 1f + 1.4f * i
        for (mc in p.manchas) {
            val a = if (mc.t == Ti.BERMELLON) p.m.bermellon else 1f
            if (a <= 0f) continue
            val tinta = p.ink(mc.t)
            val r = mc.r * crece
            drawPath(manchaForma(mc.c, r * 1.55f, mc.semilla + 7, deforma * 1.2f), tinta.copy(alpha = 0.10f * a))
            drawPath(manchaForma(mc.c, r * 1.18f, mc.semilla + 3, deforma), tinta.copy(alpha = 0.20f * a))
            drawPath(manchaForma(mc.c, r, mc.semilla, deforma), tinta.copy(alpha = 0.40f * a))
            val nucleo = mc.c + Offset(r * 0.18f * (azar(mc.semilla, 9) - 0.5f), r * 0.2f * (azar(mc.semilla, 8) - 0.5f))
            drawPath(manchaForma(nucleo, r * 0.42f, mc.semilla + 11, deforma * 1.3f), tinta.copy(alpha = 0.5f * a))
            // Los anillos de la intensidad, alternando la tinta de la mancha con el crema.
            val anillos = (p.m.anillos / 3).coerceAtMost(3)
            for (k in 1..anillos) {
                val c = if (k % 2 == 0) TrazosTinta.CREMA.copy(alpha = 0.18f * a) else tinta.copy(alpha = 0.45f * a)
                drawPath(manchaForma(mc.c, r * (1.3f + 0.28f * k), mc.semilla + 20, deforma), c, style = Stroke(p.m.grosor * 0.7f * p.u))
            }
        }
    }

    /**
     * Las bandas que fluyen por la pared con la intensidad (solo la psicodelia): nacen detrás de los objetos alrededor de
     * [c], como si el cartel se saliera de su papel. Con i = 0 no hay; crecen en número, onda y opacidad hasta i = 1.
     */
    private fun DrawScope.bandasDePared(p: Pincel, c: Offset, r: Rect) {
        val i = p.i
        if (i <= 0.05f) return
        val u = p.u
        val m = p.m
        val n = (m.bandasPorLado * 0.45f).toInt().coerceIn(2, 5)
        val a = (i * 1.2f).coerceIn(0f, 1f)
        val ult = p.ink(Ti.ULTRAMAR).copy(alpha = (if (p.o) 0.55f else 0.45f) * a)
        val mag = p.ink(Ti.MAGENTA).copy(alpha = (if (p.o) 0.6f else 0.5f) * a)
        val dm = corrido(p, Ti.MAGENTA)
        clipPath(caja(r)) {
            bandas(c, r.height * 1.25f, n, 5f * u, m.amplitud * 0.35f * u, m.grosor * 0.55f * u, ult, 1.2f, 0.1f, 1f)
            translate(dm.x, dm.y) { bandas(c, r.height * 1.25f, n, 5f * u, m.amplitud * 0.4f * u, m.grosor * 0.4f * u, mag, 1.2f, 0.35f, 1f) }
        }
    }

    /**
     * El grano y la viñeta de la película (solo la ciudad): el grano sobre todo el lienzo; la viñeta, fuera del [nicho],
     * para que el contenido no pierda contraste.
     */
    private fun DrawScope.acabado(p: Pincel, nicho: Rect?) {
        if (p.tinta) return
        // El grano de la lámina se pensó para la interfaz; en un mueble a toda la escena se baja (de noche, sobre el café negro, se veía como lluvia).
        grano(if (p.o) TrazosCiudad.GRANO_NOCHE * 0.35f else TrazosCiudad.GRANO_DIA * 0.8f, semilla = 5)
        if (nicho != null) fueraDe(nicho) { vineta(if (p.o) TrazosCiudad.VINETA_NOCHE else TrazosCiudad.VINETA_DIA) }
        else vineta(if (p.o) TrazosCiudad.VINETA_NOCHE else TrazosCiudad.VINETA_DIA)
    }

    /** El color sólido de un nicho: el [mat] llevado al tono de la tarjeta (claro en claro, oscuro en oscuro). */
    private fun Pincel.nicho(e: EstadoMueble, mat: Mat): Color = when {
        !tinta && !o -> lerp(e.fondo, mat.ciudad, 0.30f)
        !tinta -> lerp(e.fondo, lerp(mat.ciudad, TrazosCiudad.LAMPARA_LUZ, 0.15f), 0.16f)
        else -> lerp(e.fondo, ink(mat.ti), if (o) 0.07f else 0.05f)
    }

    // ---------------------------------------------------------------- Las piezas de la pared

    /** Una chincheta de cabeza redonda en [c], con su sombra. */
    private fun DrawScope.chincheta(p: Pincel, c: Offset, r: Float, mat: Mat) {
        if (!p.tinta) drawCircle(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.5f else 0.3f), r, c + Offset(r * 0.45f, r * 0.6f))
        pinta(p, circulo(c, r), mat, 0.3f, c, grosor = 0.5f)
        if (!p.tinta) drawCircle(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.25f else 0.55f), r * 0.32f, c - Offset(r * 0.3f, r * 0.3f))
        else drawCircle(p.fondoTinta(), r * 0.25f, c - Offset(r * 0.25f, r * 0.25f))
    }

    /** Una tira de cinta de pintor, centrada en [c], de [largo] × [ancho] px, girada [grados]. */
    private fun DrawScope.cinta(p: Pincel, c: Offset, largo: Float, ancho: Float, grados: Float) {
        rotate(grados, c) {
            val r = Rect(c.x - largo / 2, c.y - ancho / 2, c.x + largo / 2, c.y + ancho / 2)
            val q = Path().apply {
                moveTo(r.left, r.top); lineTo(r.right, r.top)
                lineTo(r.right - ancho * 0.12f, r.top + ancho * 0.3f); lineTo(r.right, r.top + ancho * 0.6f); lineTo(r.right - ancho * 0.08f, r.bottom)
                lineTo(r.left, r.bottom); lineTo(r.left + ancho * 0.1f, r.top + ancho * 0.55f); close()
            }
            if (!p.tinta) drawPath(q, p.ciudad(Color(0xFFE3D3A8), 0.2f, c).copy(alpha = 0.92f))
            else { drawPath(q, p.fondoTinta()); trama(q, 1.6f * p.u, 0.45f * p.u, p.ink(Ti.GRABADO).copy(alpha = 0.6f), 90f) }
        }
    }

    /** Los tipos de hoja que se clavan en la pared y en el corcho. */
    private enum class Hoja { BOCETO, PRUEBAS, ENTRADA, RECORTE, PASE }

    /**
     * Una hoja clavada en [r], girada [grados]: un boceto del disco con sus rayos a lápiz, la tira de pruebas de tinta, la
     * entrada de un baile, el recorte del periódico del barrio o el pase de autobús. Con su sombra y su chincheta.
     */
    private fun DrawScope.hoja(p: Pincel, r: Rect, grados: Float, que: Hoja, semilla: Int, chinche: Boolean = true) {
        val u = p.u
        rotate(grados, r.center) {
            val mat = when (que) { Hoja.RECORTE -> Mat.PERIODICO; Hoja.ENTRADA -> Mat.PAPEL; Hoja.PASE -> Mat.PAPEL; else -> Mat.PAPEL }
            val forma = if (que == Hoja.RECORTE) Path().apply {
                // El recorte, con los bordes cortados a mano.
                moveTo(r.left, r.top + r.height * 0.04f)
                val n = 9
                for (k in 1..n) lineTo(r.left + r.width * k / n, r.top + r.height * (0.02f + 0.05f * azar(semilla, k)))
                lineTo(r.right - r.width * 0.02f, r.bottom)
                for (k in n - 1 downTo 0) lineTo(r.left + r.width * k / n, r.bottom - r.height * 0.04f * azar(semilla, k + 20))
                close()
            } else caja(r)
            sombra(p, forma, r.center, 2.4f)
            pinta(p, forma, mat, 0.2f, r.center, grosor = 0.6f)
            val linea = if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, r.center).copy(alpha = 0.75f)
            when (que) {
                Hoja.BOCETO -> {
                    // El disco del día a lápiz, con sus rayos tanteados.
                    val c = Offset(r.center.x, r.top + r.height * 0.45f)
                    val rr = min(r.width, r.height) * 0.26f
                    drawCircle(linea.copy(alpha = 0.7f), rr, c, style = Stroke(0.6f * u))
                    drawCircle(linea.copy(alpha = 0.5f), rr * 0.3f, c, style = Stroke(0.5f * u))
                    for (k in 0 until 10) {
                        val a = k * 0.628f + azar(semilla, k) * 0.2f
                        val q = Path().apply {
                            moveTo(c.x + rr * 1.15f * cos(a), c.y + rr * 1.15f * sin(a))
                            quadraticTo(c.x + rr * 1.6f * cos(a + 0.25f), c.y + rr * 1.6f * sin(a + 0.25f), c.x + rr * 2.0f * cos(a), c.y + rr * 2.0f * sin(a))
                        }
                        clipPath(caja(r)) { drawPath(q, linea.copy(alpha = 0.55f), style = Stroke(0.5f * u)) }
                    }
                    // La forma de las letras, tanteada abajo.
                    for (k in 0 until 4) drawRect(linea.copy(alpha = 0.35f), Offset(r.left + r.width * (0.16f + 0.18f * k), r.bottom - r.height * 0.2f), Size(r.width * 0.13f, r.height * 0.09f), style = Stroke(0.45f * u))
                }
                Hoja.PRUEBAS -> {
                    // La tira de pruebas: cuadros de tinta uno bajo otro, con su apunte al lado.
                    val tintas = if (p.tinta) listOf(Mat.LADRILLO, Mat.VERDE, Mat.NARANJA, Mat.OCRE, Mat.TINTA) else listOf(Mat.NARANJA, Mat.VERDE, Mat.OCRE, Mat.LADRILLO, Mat.TINTA)
                    val alto = r.height / (tintas.size + 0.6f)
                    tintas.forEachIndexed { k, t ->
                        val q = Rect(r.left + r.width * 0.14f, r.top + alto * (0.35f + k), r.left + r.width * 0.56f, r.top + alto * (1.1f + k))
                        pinta(p, caja(q), t, 0.2f, q.center, contorno = false)
                        drawLine(linea.copy(alpha = 0.5f), Offset(r.left + r.width * 0.64f, q.center.y), Offset(r.left + r.width * (0.78f + 0.12f * azar(semilla, k)), q.center.y), 0.6f * u)
                    }
                }
                Hoja.ENTRADA -> {
                    // La entrada del baile: la franja de color, el número y el talón picado.
                    val franja = Rect(r.left, r.top, r.right, r.top + r.height * 0.32f)
                    pinta(p, caja(franja), Mat.NARANJA, 0.2f, franja.center, contorno = false)
                    drawCircle(if (p.tinta) p.fondoTinta() else p.ciudad(PAPEL, 0.3f, franja.center), franja.height * 0.3f, Offset(franja.left + franja.width * 0.22f, franja.center.y))
                    for (k in 0 until 3) drawLine(linea.copy(alpha = 0.55f), Offset(r.left + r.width * 0.1f, r.top + r.height * (0.48f + 0.13f * k)), Offset(r.left + r.width * (0.62f - 0.12f * k), r.top + r.height * (0.48f + 0.13f * k)), 0.7f * u)
                    val x = r.left + r.width * 0.76f
                    drawLine(linea.copy(alpha = 0.6f), Offset(x, r.top + r.height * 0.36f), Offset(x, r.bottom), 0.5f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(1.2f * u, 1.2f * u)))
                    drawRect(linea.copy(alpha = 0.6f), Offset(x + r.width * 0.06f, r.top + r.height * 0.5f), Size(r.width * 0.12f, r.height * 0.3f), style = Stroke(0.5f * u))
                }
                Hoja.RECORTE -> {
                    // El periódico del barrio: el titular de bloque, dos columnas de texto y el grabado del tintero partido.
                    drawRect(linea.copy(alpha = 0.85f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.12f), Size(r.width * 0.84f, r.height * 0.13f))
                    val g = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.32f, r.left + r.width * 0.46f, r.top + r.height * 0.78f)
                    if (p.tinta) {
                        drawRect(p.ink(Ti.MAGENTA), g.topLeft, Size(g.width, g.height * 0.5f))
                        drawRect(p.ink(Ti.ULTRAMAR), Offset(g.left, g.top + g.height * 0.5f), Size(g.width, g.height * 0.5f))
                    } else drawRect(p.ciudad(OCRE, 0f, g.center).copy(alpha = 0.55f), g.topLeft, g.size)
                    for (k in 0 until 7) {
                        val y = r.top + r.height * (0.34f + 0.075f * k)
                        drawLine(linea.copy(alpha = 0.4f), Offset(r.left + r.width * 0.52f, y), Offset(r.left + r.width * (0.86f + 0.06f * azar(semilla, k)), y), 0.55f * u)
                    }
                    drawLine(linea.copy(alpha = 0.4f), Offset(r.left + r.width * 0.08f, r.top + r.height * 0.86f), Offset(r.left + r.width * 0.8f, r.top + r.height * 0.86f), 0.55f * u)
                }
                Hoja.PASE -> {
                    // El pase de autobús: la banda del mes, el número y las perforaciones del día.
                    val banda = Rect(r.left, r.top + r.height * 0.18f, r.right, r.top + r.height * 0.42f)
                    pinta(p, caja(banda), Mat.OCRE, 0.2f, banda.center, contorno = false)
                    drawLine(linea.copy(alpha = 0.6f), Offset(r.left + r.width * 0.12f, r.top + r.height * 0.62f), Offset(r.left + r.width * 0.6f, r.top + r.height * 0.62f), 0.9f * u)
                    for (k in 0 until 4) drawCircle(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, r.center).copy(alpha = 0.5f), 0.9f * u, Offset(r.left + r.width * (0.2f + 0.18f * k), r.top + r.height * 0.82f))
                }
            }
            if (chinche) chincheta(p, Offset(r.center.x + r.width * 0.05f, r.top + r.height * 0.07f), 1.7f * u, if (semilla % 2 == 0) Mat.LADRILLO else Mat.VERDE)
        }
    }

    /**
     * El cartel de la semana clavado en la pared, en [r]: el disco del día hecho sol, con sus rayos que ondulan, las bandas
     * que se abren a su alrededor y las letras de bloque abajo, en su marco de arco. En la ciudad, una prueba ya vieja,
     * impresa a dos tintas apagadas; en la psicodelia, el cartel de verdad, que crece con la intensidad (más bandas, más
     * onda, anillos, el registro corrido y, desde i = 0.9, las bandas que se salen del papel).
     */
    private fun DrawScope.cartel(p: Pincel, r: Rect, semilla: Int) {
        val u = p.u
        val papel = caja(r)
        sombra(p, papel, r.center, 3.2f)
        pinta(p, papel, Mat.PAPEL, 0.2f, r.center, grosor = 0.6f)
        val marco = TrazosTinta.arco(Rect(r.left + r.width * 0.08f, r.top + r.height * 0.06f, r.right - r.width * 0.08f, r.top + r.height * 0.72f), r.width * 0.38f, u)
        val c = Offset(r.center.x, r.top + r.height * 0.40f)
        val rd = r.width * 0.17f
        // La escala del cartel: sus medidas son para un cartel de ~180 dp de ancho.
        val esc = (r.width / (180f * u)).coerceIn(0.1f, 1f)
        if (!p.tinta) {
            val naranja = p.ciudad(NARANJA, 0.1f, c).copy(alpha = 0.85f)
            val verde = p.ciudad(VERDE, 0.1f, c).copy(alpha = 0.85f)
            clipPath(marco) {
                drawPath(TrazosTinta.rayos(c, rd * 1.25f, r.width, 16, 2.4f * u, 1.5f, semilla.toFloat()), naranja)
            }
            drawPath(marco, verde, style = Stroke(1.1f * u))
            drawCircle(p.ciudad(TINTA, 0.1f, c), rd, c)
            for (k in 1..3) drawCircle(p.ciudad(METAL, 0.4f, c).copy(alpha = 0.6f), rd * (0.5f + 0.15f * k), c, style = Stroke(0.35f * u))
            drawCircle(p.ciudad(ETIQUETA, 0.2f, c), rd * 0.38f, c)
            letrasBloque(r, verde, 0f)
        } else {
            val m = p.m
            val mag = p.ink(Ti.MAGENTA); val ult = p.ink(Ti.ULTRAMAR); val ber = p.ink(Ti.BERMELLON)
            val dm = corrido(p, Ti.MAGENTA); val db = corrido(p, Ti.BERMELLON)
            val dibujarBandas: DrawScope.() -> Unit = {
                val n = (m.bandasPorLado * 0.55f).toInt().coerceIn(3, 7)
                bandas(c, r.height * 0.86f, n, r.width * 0.035f, m.amplitud * esc * u, m.grosor * esc * u, ult, 1.4f, semilla * 0.13f, 0.95f)
                translate(dm.x, dm.y) { bandas(c, r.height * 0.86f, n - 1, r.width * 0.035f, m.amplitud * esc * u * 1.1f, m.grosor * esc * u * 0.7f, mag, 1.4f, semilla * 0.13f + 0.25f, 0.95f) }
            }
            if (m.fueraDelMarco) dibujarBandas() else clipPath(marco) { dibujarBandas() }
            translate(db.x, db.y) {
                if (m.bermellon > 0f) clipPath(marco) { drawPath(TrazosTinta.rayos(c, rd * 1.2f, rd * 2.2f, 14, 2f * u * (1f + p.i), 1.5f, semilla.toFloat()), ber.copy(alpha = m.bermellon)) }
            }
            for (k in 1..m.anillos) {
                val col = when (k % 3) { 0 -> ult; 1 -> mag; else -> if (m.bermellon > 0f) ber else ult }
                drawCircle(col, rd * (1.05f + 0.12f * k), c, style = Stroke(rd * 0.06f))
            }
            drawPath(marco, p.ink(Ti.GRABADO), style = Stroke(0.9f * u))
            drawCircle(p.ink(Ti.PAPEL), rd, c)
            drawCircle(p.ink(Ti.GRABADO), rd, c, style = Stroke(0.7f * u))
            for (k in 1..3) drawCircle(p.ink(Ti.GRABADO).copy(alpha = 0.6f), rd * (0.5f + 0.15f * k), c, style = Stroke(0.35f * u))
            translate(dm.x, dm.y) { drawCircle(mag, rd * 0.36f, c) }
            drawCircle(p.ink(Ti.GRABADO), rd * 0.06f, c)
            translate(dm.x, dm.y) { letrasBloque(r, mag, m.ondaTitulo * esc * u) }
            letrasBloque(r, ult.copy(alpha = 0.85f), m.ondaTitulo * esc * u * 0.6f)
        }
        // Las chinchetas de las esquinas de arriba.
        chincheta(p, Offset(r.left + r.width * 0.07f, r.top + r.height * 0.035f), 1.5f * u, Mat.LADRILLO)
        chincheta(p, Offset(r.right - r.width * 0.07f, r.top + r.height * 0.035f), 1.5f * u, Mat.LADRILLO)
    }

    /** Las letras de bloque del cartel, abstractas: cinco bloques que llenan la forma de barril abajo del papel, con [onda]. */
    private fun DrawScope.letrasBloque(r: Rect, color: Color, onda: Float) {
        val n = 5
        val caja = Rect(r.left + r.width * 0.1f, r.top + r.height * 0.76f, r.right - r.width * 0.1f, r.top + r.height * 0.93f)
        val paso = caja.width / n
        for (k in 0 until n) {
            val t = (k + 0.5f) / n
            val alto = caja.height * (0.62f + 0.38f * sin(PI.toFloat() * t))
            val dy = onda * sin(2f * PI.toFloat() * t)
            val q = Rect(caja.left + paso * k + paso * 0.08f, caja.center.y - alto / 2 + dy, caja.left + paso * (k + 1) - paso * 0.08f, caja.center.y + alto / 2 + dy)
            drawRoundRect(color, q.topLeft, q.size, CornerRadius(paso * 0.18f))
        }
    }

    /**
     * La regla T colgada de su clavo: la cabeza en [cabeza] y la hoja que baja [largo] px, girada [grados]. Y la escuadra,
     * si [escuadra], colgada del mismo clavo.
     */
    private fun DrawScope.reglaT(p: Pincel, cabeza: Offset, largo: Float, grados: Float, escuadra: Boolean) {
        val u = p.u
        if (escuadra) {
            val e = poligono(cabeza + Offset(-2f * u, 3f * u), cabeza + Offset(-2f * u + largo * 0.42f, 3f * u + largo * 0.42f), cabeza + Offset(-2f * u, 3f * u + largo * 0.42f))
            val hueco = poligono(cabeza + Offset(2f * u, 12f * u), cabeza + Offset(2f * u + largo * 0.2f, 12f * u + largo * 0.2f), cabeza + Offset(2f * u, 12f * u + largo * 0.2f))
            val forma = Path().apply { addPath(e); addPath(hueco); fillType = PathFillType.EvenOdd }
            sombra(p, forma, cabeza, 2.2f)
            pinta(p, forma, Mat.VIDRIO, 0.3f, cabeza, grosor = 0.5f)
        }
        rotate(grados, cabeza) {
            val hojaR = Rect(cabeza.x - 2.2f * u, cabeza.y + 3f * u, cabeza.x + 2.2f * u, cabeza.y + largo)
            val cab = Rect(cabeza.x - 9f * u, cabeza.y - 1.5f * u, cabeza.x + 9f * u, cabeza.y + 4f * u)
            sombra(p, caja(hojaR), cabeza, 2.5f)
            sombra(p, caja(cab), cabeza, 2.5f)
            volumen(p, hojaR, Mat.PINO, 0.9f * u, 0.3f)
            for (k in 1 until 12) drawLine(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, hojaR.center).copy(alpha = 0.5f),
                Offset(hojaR.left, hojaR.top + hojaR.height * k / 12f), Offset(hojaR.left + (if (k % 2 == 0) 2.2f else 1.2f) * u, hojaR.top + hojaR.height * k / 12f), 0.4f * u)
            volumen(p, cab, Mat.MADERA, 1.1f * u, 0.2f)
            drawCircle(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(METAL, 0f, cabeza), 1f * u, cabeza + Offset(0f, 1f * u))
        }
    }

    // ---------------------------------------------------------------- Lo que hay sobre la mesa

    /**
     * Un frasco de tinta de pie en [base] (su pie), de [alto] px, con la tinta de [mat] dentro, destapado (con la plumilla
     * metida) o con su tapa de baquelita.
     */
    private fun DrawScope.frasco(p: Pincel, base: Offset, alto: Float, mat: Mat, destapado: Boolean) {
        val u = p.u
        val an = alto * 0.82f
        val cuerpo = Rect(base.x - an / 2, base.y - alto * 0.78f, base.x + an / 2, base.y)
        val cuello = Rect(base.x - an * 0.26f, base.y - alto, base.x + an * 0.26f, base.y - alto * 0.76f)
        sombra(p, redondo(cuerpo, an * 0.2f), base, 2f)
        pinta(p, redondo(cuerpo, an * 0.2f), Mat.VIDRIO, 0.1f, cuerpo.center, grosor = 0.6f)
        // La tinta dentro, hasta tres cuartos.
        val dentro = Rect(cuerpo.left + an * 0.08f, cuerpo.top + cuerpo.height * 0.3f, cuerpo.right - an * 0.08f, cuerpo.bottom - an * 0.06f)
        pinta(p, redondo(dentro, an * 0.15f), mat, 0f, dentro.center, contorno = false)
        // La etiqueta.
        val et = Rect(cuerpo.left + an * 0.12f, cuerpo.top + cuerpo.height * 0.42f, cuerpo.right - an * 0.18f, cuerpo.top + cuerpo.height * 0.8f)
        pinta(p, caja(et), Mat.PAPEL, 0.3f, et.center, grosor = 0.4f)
        drawLine(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, et.center).copy(alpha = 0.6f), Offset(et.left + et.width * 0.15f, et.center.y), Offset(et.right - et.width * 0.2f, et.center.y), 0.6f * u)
        // El brillo del vidrio, del lado de la luz.
        if (!p.tinta) drawLine(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.25f * cerca(base, p.foco, p.alcance) + 0.05f else 0.55f), Offset(cuerpo.left + an * 0.14f, cuerpo.top + cuerpo.height * 0.15f), Offset(cuerpo.left + an * 0.14f, cuerpo.bottom - cuerpo.height * 0.25f), 0.9f * u, cap = StrokeCap.Round)
        pinta(p, caja(cuello), Mat.VIDRIO, 0.2f, cuello.center, grosor = 0.5f)
        if (destapado) {
            // La plumilla metida, inclinada.
            val a = base + Offset(-an * 0.05f, -alto * 0.9f)
            val b = a + Offset(alto * 0.55f, -alto * 0.95f)
            drawLine(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(MADERA, 0.2f, b), a, b, 1.6f * u, cap = StrokeCap.Round)
            drawLine(if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(OCRE, 0.3f, b), a + (b - a) * 0.55f, b, 1.9f * u, cap = StrokeCap.Round)
        } else {
            val tapa = Rect(cuello.left - an * 0.05f, cuello.top - alto * 0.18f, cuello.right + an * 0.05f, cuello.top + alto * 0.04f)
            volumen(p, tapa, Mat.TINTA, 0.8f * u, 0.2f)
        }
    }

    /** La lata de café vacía con los pinceles y las plumillas, de pie en [base], de [alto] px. */
    private fun DrawScope.lata(p: Pincel, base: Offset, alto: Float, semilla: Int) {
        val u = p.u
        val an = alto * 0.7f
        val r = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        // Los mangos, que asoman por arriba: lápices y pinceles de largos distintos.
        for (k in 0 until 6) {
            val x = r.left + an * (0.18f + 0.13f * k)
            val tope = r.top - alto * (0.35f + 0.45f * azar(semilla, k))
            val inclina = (azar(semilla, k + 10) - 0.5f) * an * 0.7f
            val mat = when (k % 3) { 0 -> Mat.LADRILLO; 1 -> Mat.PINO; else -> Mat.VERDE }
            drawLine(if (p.tinta) p.ink(mat.ti) else p.ciudad(mat.ciudad, 0.2f, Offset(x, tope)), Offset(x, r.top + alto * 0.2f), Offset(x + inclina, tope), 1.5f * u, cap = StrokeCap.Round)
            if (k % 2 == 0) drawLine(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0.2f, Offset(x, tope)), Offset(x + inclina * 0.92f, tope + alto * 0.06f), Offset(x + inclina, tope), 2.1f * u, cap = StrokeCap.Round)
        }
        sombra(p, caja(r), base, 2.4f)
        volumen(p, r, Mat.METAL, an * 0.12f, 0.35f)
        // La etiqueta de papel de la lata, sin marca: una banda con un filete.
        val et = Rect(r.left, r.top + alto * 0.28f, r.right, r.top + alto * 0.7f)
        pinta(p, caja(et), Mat.NARANJA, 0.1f, et.center, contorno = false)
        drawRect(if (p.tinta) p.ink(Ti.PAPEL) else p.ciudad(PAPEL, 0.2f, et.center).copy(alpha = 0.8f), Offset(et.left, et.top + et.height * 0.4f), Size(et.width, et.height * 0.18f))
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.4f else 0.22f), Offset(et.right - an * 0.22f, et.top), Size(an * 0.22f, et.height))
        for (k in 0 until 3) drawLine(if (p.tinta) p.ink(Ti.GRABADO).copy(alpha = 0.5f) else p.ciudad(TINTA, 0f, r.center).copy(alpha = 0.3f),
            Offset(r.left, r.top + alto * (0.12f + 0.05f * k)), Offset(r.right, r.top + alto * (0.12f + 0.05f * k)), 0.4f * u)
    }

    /** Un lápiz tumbado de [a] a [b] (la punta en [b]), de [mat]. */
    private fun DrawScope.lapiz(p: Pincel, a: Offset, b: Offset, mat: Mat) {
        val u = p.u
        val punta = a + (b - a) * 0.86f
        if (!p.tinta) drawLine(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.45f else 0.25f), a + Offset(0.8f * u, 1.6f * u), b + Offset(0.8f * u, 1.6f * u), 2.0f * u, cap = StrokeCap.Round)
        drawLine(p.color(mat, 0.2f, a), a, punta, 2.2f * u)
        drawLine(p.color(Mat.PINO, 0.6f, b), punta, b, 1.6f * u)
        drawLine(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, b), punta + (b - punta) * 0.7f, b, 0.9f * u, cap = StrokeCap.Round)
        if (!p.tinta) drawLine(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.15f else 0.4f), a + Offset(0f, -0.6f * u), punta + Offset(0f, -0.6f * u), 0.5f * u)
    }

    /** La radio de válvulas de mesa, en [r], con el dial en el 106.9 (la FM de la noche). */
    private fun DrawScope.radio(p: Pincel, r: Rect) {
        val u = p.u
        sombra(p, redondo(r, r.height * 0.18f), r.center, 3f)
        val cuerpo = redondo(r, r.height * 0.18f)
        pinta(p, cuerpo, Mat.MADERA, 0.1f, r.center)
        // El lado en sombra y el filo de arriba.
        if (!p.tinta) {
            clipPath(cuerpo) {
                drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.35f else 0.22f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
                drawRect(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.06f + 0.25f * cerca(r.topLeft, p.foco, p.alcance) else 0.22f), r.topLeft, Size(r.width, r.height * 0.07f))
            }
        }
        // La rejilla del parlante.
        val rej = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.18f, r.left + r.width * 0.46f, r.bottom - r.height * 0.16f)
        pinta(p, redondo(rej, rej.height * 0.12f), Mat.ESTRAZA, -0.3f, rej.center, grosor = 0.5f)
        for (k in 1 until 7) drawLine(if (p.tinta) p.ink(Ti.GRABADO).copy(alpha = 0.7f) else p.ciudad(MADERA, -0.6f, rej.center),
            Offset(rej.left + rej.width * k / 7f, rej.top), Offset(rej.left + rej.width * k / 7f, rej.bottom), 0.9f * u)
        // El dial: la ventanilla con su escala y la aguja en el 106.9.
        val dial = Rect(r.left + r.width * 0.53f, r.top + r.height * 0.2f, r.right - r.width * 0.08f, r.top + r.height * 0.5f)
        pinta(p, redondo(dial, 1.5f * u), Mat.PAPEL, 0.4f, dial.center, grosor = 0.5f)
        if (p.lampara) drawRect(TrazosCiudad.LAMPARA.copy(alpha = 0.35f), dial.topLeft, dial.size)
        val tinta = if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, dial.center)
        for (k in 0..10) {
            val x = dial.left + dial.width * (0.08f + 0.84f * k / 10f)
            drawLine(tinta.copy(alpha = 0.6f), Offset(x, dial.bottom - dial.height * (if (k % 5 == 0) 0.45f else 0.25f)), Offset(x, dial.bottom - dial.height * 0.08f), 0.4f * u)
        }
        // 88 a 108: el 106.9 cae casi al final de la escala.
        val x = dial.left + dial.width * (0.08f + 0.84f * (106.9f - 88f) / 20f)
        drawLine(if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(LADRILLO, 0.3f, dial.center), Offset(x, dial.top + dial.height * 0.1f), Offset(x, dial.bottom - dial.height * 0.05f), 0.8f * u)
        // Las dos perillas.
        for (k in 0..1) {
            val c = Offset(dial.left + dial.width * (0.22f + 0.56f * k), r.top + r.height * 0.73f)
            pinta(p, circulo(c, r.height * 0.11f), Mat.TINTA, 0.2f, c, grosor = 0.5f)
            if (!p.tinta) drawCircle(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.12f else 0.35f), r.height * 0.04f, c - Offset(r.height * 0.04f, r.height * 0.04f))
        }
    }

    /** La taza de loza, de pie en [base], de [alto] px. */
    private fun DrawScope.taza(p: Pincel, base: Offset, alto: Float) {
        val u = p.u
        val an = alto * 0.9f
        val r = Rect(base.x - an / 2, base.y - alto, base.x + an / 2, base.y)
        val asa = Path().apply { addOval(Rect(r.right - an * 0.12f, r.top + alto * 0.22f, r.right + an * 0.3f, r.top + alto * 0.68f)) }
        drawPath(asa, p.color(Mat.PAPEL, -0.2f, r.center), style = Stroke(1.6f * u))
        sombra(p, redondo(r, an * 0.12f), base, 2f)
        pinta(p, redondo(r, an * 0.12f), Mat.PAPEL, 0.2f, r.center, grosor = 0.6f)
        // La franja de color de la loza de cafetería.
        pinta(p, caja(Rect(r.left, r.top + alto * 0.18f, r.right, r.top + alto * 0.3f)), Mat.VERDE, 0.2f, r.center, contorno = false)
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.35f else 0.18f), Offset(r.right - an * 0.28f, r.top), Size(an * 0.28f, alto))
    }

    /** El rollo de cinta de pintor tumbado de canto, en [c], de radio [r]. */
    private fun DrawScope.rollo(p: Pincel, c: Offset, r: Float) {
        sombra(p, circulo(c, r), c, 1.8f)
        val forma = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)); addOval(Rect(c.x - r * 0.55f, c.y - r * 0.55f, c.x + r * 0.55f, c.y + r * 0.55f)); fillType = PathFillType.EvenOdd }
        pinta(p, forma, Mat.PERIODICO, 0.1f, c, grosor = 0.5f)
        if (!p.tinta) drawCircle(TrazosCiudad.SOMBRA.copy(alpha = 0.25f), r * 0.8f, c, style = Stroke(r * 0.12f))
    }

    /** Los carteles enrollados, tumbados, desde [a] hacia la derecha, [largo] px y de grueso [g]. */
    private fun DrawScope.enrollados(p: Pincel, a: Offset, largo: Float, g: Float) {
        for (k in 0..1) {
            val r = Rect(a.x + k * g * 0.5f, a.y - g * (1.0f + k * 0.85f), a.x + largo - k * g * 1.5f, a.y - k * g * 0.85f)
            sombra(p, redondo(r, g * 0.5f), r.center, 1.8f)
            pinta(p, redondo(r, g * 0.5f), if (k == 0) Mat.PAPEL else Mat.ESTRAZA, 0.2f, r.center, grosor = 0.6f)
            val boca = Rect(r.right - g * 0.5f, r.top, r.right + g * 0.15f, r.bottom)
            pinta(p, ovalo(boca), Mat.PAPEL, -0.5f, boca.center, grosor = 0.5f)
            drawCircle(p.color(Mat.TINTA, 0f, boca.center).copy(alpha = 0.6f), g * 0.12f, boca.center)
            // La goma que lo ata.
            drawLine(p.color(Mat.LADRILLO, 0.2f, r.center), Offset(r.left + r.width * 0.35f, r.top), Offset(r.left + r.width * 0.33f, r.bottom), 1.2f * p.u)
        }
    }

    /** El sencillo en su funda de papel, de canto contra la pared, en [r]: el hueco redondo deja ver la etiqueta. */
    private fun DrawScope.sencillo(p: Pincel, r: Rect, grados: Float) {
        rotate(grados, Offset(r.center.x, r.bottom)) {
            sombra(p, caja(r), r.center, 2.2f)
            pinta(p, caja(r), Mat.ESTRAZA, 0.15f, r.center, grosor = 0.6f)
            val c = r.center
            pinta(p, circulo(c, r.width * 0.26f), Mat.TINTA, 0f, c, grosor = 0.4f)
            pinta(p, circulo(c, r.width * 0.13f), Mat.ETIQUETA, 0.2f, c, contorno = false)
            drawCircle(p.color(Mat.PAPEL, 0f, c), r.width * 0.025f, c)
        }
    }

    /**
     * La lámpara de brazo: prensada en [base], el primer brazo hasta [codo], el segundo hasta [cabeza], y la pantalla de
     * esmalte que mira hacia [mira] (en grados, 0 = a la derecha, 90 = abajo), de [tam] px. Con sus resortes. En la ciudad
     * de noche está encendida: la boca de la pantalla y el cono de luz sobre lo que alumbra. En la psicodelia clara, la luz
     * son rayos grabados; en la oscura está apagada.
     */
    private fun DrawScope.lampara(p: Pincel, base: Offset, codo: Offset, cabeza: Offset, mira: Float, tam: Float, nicho: Rect?) {
        val u = p.u
        val metal = { q: Offset -> if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(METAL, 0.2f, q) }
        val brillo = { q: Offset -> if (p.tinta) p.fondoTinta() else p.ciudad(TrazosCiudad.PASAMANOS_BRILLO, 0.5f, q) }
        // El cono de luz, debajo de todo (y nunca sobre el nicho).
        val rad = mira * PI.toFloat() / 180f
        val dir = Offset(cos(rad), sin(rad)); val nor = Offset(-dir.y, dir.x)
        val boca = cabeza + dir * (tam * 0.62f)
        val dibujarLuz: DrawScope.() -> Unit = {
            if (p.lampara) {
                for (k in 0..2) {
                    val l = tam * (3.2f + 0.8f * k); val a = tam * (0.42f + 0.55f * (k + 1))
                    drawPath(poligono(boca + nor * (tam * 0.42f), boca - nor * (tam * 0.42f), boca + dir * l - nor * a, boca + dir * l + nor * a), TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.07f))
                }
            } else if (p.tinta && !p.o) {
                for (k in -3..3) {
                    val d2 = dir + nor * (k * 0.16f)
                    drawLine(p.ink(Ti.GRABADO).copy(alpha = 0.55f), boca + d2 * (tam * 0.25f), boca + d2 * (tam * (1.4f + 0.3f * (k % 2))), 0.5f * u,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.4f * u, 1.6f * u)))
                }
            }
        }
        if (nicho != null) fueraDe(nicho) { dibujarLuz() } else dibujarLuz()
        // La prensa.
        val prensa = Rect(base.x - 3.5f * u, base.y - 3f * u, base.x + 3.5f * u, base.y + 5f * u)
        sombra(p, caja(prensa), base, 2f)
        volumen(p, prensa, Mat.METAL, 1f * u, 0.3f)
        // Los dos brazos: dos varillas paralelas cada uno, con sus resortes.
        fun brazo(a: Offset, b: Offset, resorte: Boolean) {
            val d = b - a; val l = sqrt(d.x * d.x + d.y * d.y).coerceAtLeast(1f)
            val n = Offset(-d.y / l, d.x / l) * (1.5f * u)
            if (!p.tinta) {
                drawLine(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.4f else 0.2f), a + Offset(2.5f * u, 3f * u), b + Offset(2.5f * u, 3f * u), 1.4f * u)
            }
            drawLine(metal(a), a + n, b + n, 1.25f * u, cap = StrokeCap.Round)
            drawLine(metal(a), a - n, b - n, 1.25f * u, cap = StrokeCap.Round)
            drawLine(brillo(a).copy(alpha = if (p.tinta) 1f else 0.5f), a + n * 1.3f, b + n * 1.3f, 0.4f * u)
            if (resorte) {
                val r0 = a + d * 0.18f; val r1 = a + d * 0.62f
                val pasos = 14
                val zz = Path().apply {
                    moveTo(r0.x, r0.y)
                    for (s in 1..pasos) {
                        val q = r0 + (r1 - r0) * (s / pasos.toFloat()) + n * (if (s % 2 == 0) 1.6f else 3.4f)
                        lineTo(q.x, q.y)
                    }
                }
                drawPath(zz, if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(TrazosCiudad.PASAMANOS, 0.3f, r0), style = Stroke(0.55f * u))
            }
        }
        brazo(base, codo, true)
        brazo(codo, cabeza, true)
        for (q in listOf(codo, cabeza)) {
            drawCircle(metal(q), 2.4f * u, q)
            drawCircle(brillo(q).copy(alpha = 0.6f), 0.8f * u, q - Offset(0.6f * u, 0.6f * u))
        }
        // La pantalla de esmalte, en coordenadas propias (mirando a la derecha) y girada.
        rotate(mira, cabeza) {
            val c = cabeza
            val cuello = Rect(c.x - tam * 0.05f, c.y - tam * 0.12f, c.x + tam * 0.16f, c.y + tam * 0.12f)
            volumen(p, cuello, Mat.METAL, 0.8f * u, 0.2f)
            val cono = Path().apply {
                moveTo(c.x + tam * 0.12f, c.y - tam * 0.16f)
                cubicTo(c.x + tam * 0.3f, c.y - tam * 0.2f, c.x + tam * 0.48f, c.y - tam * 0.42f, c.x + tam * 0.62f, c.y - tam * 0.48f)
                lineTo(c.x + tam * 0.62f, c.y + tam * 0.48f)
                cubicTo(c.x + tam * 0.48f, c.y + tam * 0.42f, c.x + tam * 0.3f, c.y + tam * 0.2f, c.x + tam * 0.12f, c.y + tam * 0.16f)
                close()
            }
            // La luz de la tarde (o la de la pared) da en la mitad de arriba; la de abajo, en sombra.
            pinta(p, cono, Mat.ESMALTE, 0.25f, c)
            val bajo = Path().apply {
                moveTo(c.x + tam * 0.12f, c.y + tam * 0.02f); lineTo(c.x + tam * 0.62f, c.y + tam * 0.05f)
                lineTo(c.x + tam * 0.62f, c.y + tam * 0.48f)
                cubicTo(c.x + tam * 0.48f, c.y + tam * 0.42f, c.x + tam * 0.3f, c.y + tam * 0.2f, c.x + tam * 0.12f, c.y + tam * 0.16f); close()
            }
            if (!p.tinta) drawPath(bajo, TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.35f else 0.28f))
            else if (!p.o) trama(bajo, 1.8f * u, 0.55f * u, TrazosTinta.GRABAR, 45f)
            if (!p.tinta) drawLine(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.18f else 0.45f), Offset(c.x + tam * 0.2f, c.y - tam * 0.15f), Offset(c.x + tam * 0.55f, c.y - tam * 0.4f), 0.9f * u, cap = StrokeCap.Round)
            // La boca: el borde enrollado y, dentro, la bombilla (encendida en la ciudad de noche).
            val bocaR = Rect(c.x + tam * 0.56f, c.y - tam * 0.48f, c.x + tam * 0.70f, c.y + tam * 0.48f)
            val dentro = when {
                p.lampara -> TrazosCiudad.LAMPARA
                p.tinta && p.o -> TrazosTinta.NEGRO
                p.tinta -> p.fondoTinta()
                else -> p.ciudad(TINTA, -0.2f, c)
            }
            drawOval(dentro, bocaR.topLeft, bocaR.size)
            if (p.lampara) drawOval(Color.White.copy(alpha = 0.6f), Offset(bocaR.left + bocaR.width * 0.2f, bocaR.center.y - tam * 0.14f), Size(bocaR.width * 0.6f, tam * 0.28f))
            drawOval(if (p.tinta) p.contorno(c) else p.ciudad(VERDE, 0.5f, c), bocaR.topLeft, bocaR.size, style = Stroke(1.1f * u))
        }
    }

    // ---------------------------------------------------------------- La mesa de dibujo

    /** El borde de la mesa contra la pared: la tabla en [r] (su canto de frente) y, debajo, el faldón con su cajón. */
    private fun DrawScope.bordeMesa(p: Pincel, r: Rect, faldon: Rect?, semilla: Int) {
        val u = p.u
        // La tapa, vista apenas desde arriba: una franja clara y el canto de frente.
        val tapa = Rect(r.left, r.top, r.right, r.top + r.height * 0.45f)
        val canto = Rect(r.left, tapa.bottom, r.right, r.bottom)
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.5f else 0.25f), Offset(r.left, r.top - 2.5f * u), Size(r.width, 2.5f * u))
        pinta(p, caja(tapa), Mat.PINO, 0.7f, tapa.center)
        pinta(p, caja(canto), Mat.PINO, -0.1f, canto.center)
        if (!p.tinta) {
            pinceladas(tapa, TrazosCiudad.AL_SOL, semilla, 12, u, if (p.o) 0.04f else 0.12f)
            // Las vetas del pino y una mancha de tinta vieja.
            for (k in 0 until 5) drawLine(p.ciudad(MADERA, -0.3f, tapa.center).copy(alpha = 0.35f), Offset(tapa.left, tapa.top + tapa.height * (0.2f + 0.15f * k)),
                Offset(tapa.right, tapa.top + tapa.height * (0.25f + 0.13f * k)), 0.4f * u)
            drawOval(p.ciudad(TINTA, 0f, tapa.center).copy(alpha = 0.3f), Offset(tapa.left + tapa.width * 0.62f, tapa.top + tapa.height * 0.3f), Size(6f * u, 2.2f * u))
            drawLine(TrazosCiudad.AL_SOL.copy(alpha = if (p.o) 0.2f else 0.4f), Offset(canto.left, canto.top), Offset(canto.right, canto.top), 0.7f * u)
        }
        if (faldon != null && faldon.height > 0f) {
            pinta(p, caja(faldon), Mat.FALDON, -0.3f, faldon.center)
            if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.45f else 0.3f), faldon.topLeft, Size(faldon.width, max(2.5f * u, faldon.height * 0.18f)))
            // El cajón con su tirador de latón.
            val cajon = Rect(faldon.left + faldon.width * 0.38f, faldon.top + faldon.height * 0.22f, faldon.left + faldon.width * 0.62f, min(faldon.bottom - 2f * u, faldon.top + faldon.height * 0.8f))
            if (cajon.height > 2f * u) {
                drawRect(p.contorno(cajon.center).let { if (p.tinta) it else p.ciudad(TINTA, -0.2f, cajon.center).copy(alpha = 0.6f) }, cajon.topLeft, cajon.size, style = Stroke(0.7f * u))
                val t = Offset(cajon.center.x, cajon.top + cajon.height * 0.45f)
                pinta(p, redondo(Rect(t.x - 4f * u, t.y - 1.2f * u, t.x + 4f * u, t.y + 1.2f * u), 1f * u), Mat.OCRE, 0.5f, t, grosor = 0.4f)
            }
        }
    }

    // ---------------------------------------------------------------- Social: la pared sobre la mesa, con el pliego

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val ni = fr(0.20f, 0.08f, 0.84f, 0.65f)
        val foco = fr(0.14f, 0.27f)
        val p = pincel(oscuro, foco, w * 0.62f, listOf(
            Mancha(fr(0.16f, 0.30f), w * 0.13f, Ti.MAGENTA, 3), Mancha(fr(0.86f, 0.18f), w * 0.12f, Ti.ULTRAMAR, 5), Mancha(fr(0.62f, 0.86f), w * 0.11f, Ti.BERMELLON, 9)))
        val u = p.u
        pared(p, Rect(0f, 0f, w, h), e.semilla, listOf(fr(0.10f, 0.45f), fr(0.93f, 0.40f)))
        // La moldura de los cuadros, arriba, con su sombra.
        val moldura = fr(0f, 0.02f, 1f, 0.045f)
        sombra(p, caja(moldura), moldura.center, 1.5f)
        volumen(p, moldura, Mat.PINO, 1.2f * u, 0.4f)
        // A la derecha: la regla T y la escuadra colgadas de su clavo, y un boceto.
        reglaT(p, fr(0.925f, 0.12f), h * 0.62f, -3f, escuadra = true)
        hoja(p, fr(0.865f, 0.50f, 0.995f, 0.72f), 4f, Hoja.BOCETO, 11)
        // El pliego de estraza, más grande que el nicho, con su sombra, sus chinchetas y la esquina que se levanta.
        val pliego = Rect(ni.left - 4f * u, ni.top - 4f * u, ni.right + 4f * u, ni.bottom + 5f * u)
        sombra(p, caja(pliego), pliego.center, 3.6f)
        pinta(p, caja(pliego), Mat.ESTRAZA, 0.3f, pliego.center, grosor = 0.6f)
        drawRect(p.nicho(e, Mat.ESTRAZA), ni.topLeft, ni.size)
        if (p.tinta) drawRect(p.ink(Ti.GRABADO).copy(alpha = 0.5f), ni.topLeft, ni.size, style = Stroke(0.5f * u))
        val esquina = poligono(Offset(pliego.right, pliego.bottom - 9f * u), Offset(pliego.right, pliego.bottom), Offset(pliego.right - 10f * u, pliego.bottom), Offset(pliego.right - 3f * u, pliego.bottom - 7f * u))
        pinta(p, esquina, Mat.ESTRAZA, -0.6f, pliego.bottomRight, grosor = 0.5f)
        for (q in listOf(Offset(pliego.left + 2.2f * u, pliego.top + 2.2f * u), Offset(pliego.right - 2.2f * u, pliego.top + 2.2f * u), Offset(pliego.left + 2.2f * u, pliego.bottom - 2.2f * u)))
            chincheta(p, q, 1.6f * u, Mat.LADRILLO)
        // En la esquina de arriba a la derecha del pliego: el pase y la entrada del baile metidos bajo la chincheta.
        fueraDe(ni) {
            hoja(p, Rect(pliego.right - 6f * u, pliego.top + 10f * u, pliego.right + 20f * u, pliego.top + 26f * u), 8f, Hoja.PASE, 3, chinche = false)
            hoja(p, Rect(pliego.right - 4f * u, pliego.top + 28f * u, pliego.right + 26f * u, pliego.top + 42f * u), -6f, Hoja.ENTRADA, 4, chinche = false)
        }
        // La mesa: su borde y el faldón.
        val mesa = fr(0f, 0.80f, 1f, 0.87f)
        bordeMesa(p, mesa, fr(0.04f, 0.87f, 0.96f, 1.0f), e.semilla)
        // Lo que hay sobre la mesa, de izquierda a derecha: la radio, la taza, los frascos, la lata de los pinceles, el rollo, la plumilla.
        val pie = mesa.top + mesa.height * 0.35f
        radio(p, Rect(w * 0.235f, pie - h * 0.15f, w * 0.40f, pie))
        taza(p, Offset(w * 0.45f, pie), h * 0.075f)
        frasco(p, Offset(w * 0.52f, pie), h * 0.085f, Mat.TINTA, destapado = false)
        frasco(p, Offset(w * 0.575f, pie), h * 0.075f, Mat.LADRILLO, destapado = true)
        lapiz(p, Offset(w * 0.62f, pie - 1f * u), Offset(w * 0.74f, pie - 2.5f * u), Mat.VERDE)
        rollo(p, Offset(w * 0.80f, pie - h * 0.035f), h * 0.035f)
        lata(p, Offset(w * 0.90f, pie), h * 0.13f, e.semilla + 2)
        // La lámpara de brazo, prensada al borde de la mesa a la izquierda, que se asoma sobre el pliego.
        lampara(p, Offset(w * 0.075f, mesa.top + 2f * u), fr(0.035f, 0.36f), fr(0.10f, 0.13f), 58f, h * 0.14f, ni)
        acabado(p, ni)
    }

    // ---------------------------------------------------------------- Mapas: la mesa de dibujo de frente

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val ni = fr(0.25f, 0.10f, 0.75f, 0.64f)
        val foco = fr(0.80f, 0.22f)
        val p = pincel(oscuro, foco, w * 0.55f, listOf(
            Mancha(fr(0.10f, 0.22f), w * 0.11f, Ti.ULTRAMAR, 2), Mancha(fr(0.90f, 0.38f), w * 0.12f, Ti.MAGENTA, 6), Mancha(fr(0.50f, 0.04f), w * 0.10f, Ti.BERMELLON, 8)))
        val u = p.u
        pared(p, Rect(0f, 0f, w, h * 0.86f), e.semilla, listOf(fr(0.10f, 0.42f), fr(0.80f, 0.42f)))
        // El zócalo y la duela.
        val zocalo = fr(0f, 0.83f, 1f, 0.88f)
        volumen(p, zocalo, Mat.PINO, 1.2f * u, 0.2f)
        val piso = fr(0f, 0.88f, 1f, 1f)
        pinta(p, caja(piso), Mat.DUELA, -0.2f, piso.center, contorno = false)
        if (!p.tinta) for (k in 0 until 6) drawLine(TrazosCiudad.SOMBRA.copy(alpha = 0.35f), Offset(w * (0.05f + 0.19f * k + 0.04f * azar(e.semilla, k)), piso.top), Offset(w * (0.03f + 0.19f * k), piso.bottom), 0.6f * u)
        // A la izquierda, en la pared: los dos bocetos y la tira de pruebas de tinta.
        hoja(p, fr(0.025f, 0.08f, 0.165f, 0.36f), -4f, Hoja.BOCETO, 4)
        hoja(p, fr(0.06f, 0.40f, 0.135f, 0.70f), 2f, Hoja.PRUEBAS, 5)
        // A la derecha, el cartel de la semana clavado (la lámpara le cruza por delante).
        cartel(p, fr(0.835f, 0.07f, 0.985f, 0.62f), e.semilla)
        // Las patas de caballete, detrás del tablero, hasta el piso; con su travesaño.
        val pataI = poligono(fr(0.28f, 0.70f), fr(0.31f, 0.70f), fr(0.27f, 0.95f), fr(0.24f, 0.95f))
        val pataD = poligono(fr(0.69f, 0.70f), fr(0.72f, 0.70f), fr(0.76f, 0.95f), fr(0.73f, 0.95f))
        for (pt in listOf(pataI, pataD)) { sombra(p, pt, Offset(w * 0.5f, h * 0.85f), 3f); pinta(p, pt, Mat.MADERA, -0.2f, Offset(w * 0.5f, h * 0.85f)) }
        val trav = fr(0.27f, 0.84f, 0.74f, 0.865f)
        volumen(p, trav, Mat.MADERA, 1f * u, -0.1f)
        // El tablero de pino: su marco y su sombra en la pared.
        val tablero = fr(0.215f, 0.055f, 0.785f, 0.70f)
        sombra(p, caja(tablero), tablero.center, 4.5f)
        volumen(p, tablero, Mat.PINO, 2.4f * u, 0.15f)
        if (!p.tinta) pinceladas(tablero, TrazosCiudad.AL_SOL, e.semilla + 3, 14, u, if (p.o) 0.03f else 0.08f)
        // El pliego de dibujo, un poco mayor que el nicho, y la cinta en sus esquinas.
        val pliego = Rect(ni.left - 3f * u, ni.top - 3f * u, ni.right + 3f * u, ni.bottom + 3f * u)
        sombra(p, caja(pliego), pliego.center, 1.2f)
        pinta(p, caja(pliego), Mat.PAPEL, 0.3f, pliego.center, grosor = 0.5f)
        drawRect(p.nicho(e, Mat.PAPEL), ni.topLeft, ni.size)
        fueraDe(ni) {
            cinta(p, pliego.topLeft + Offset(1f * u, 1f * u), 13f * u, 4.5f * u, -40f)
            cinta(p, Offset(pliego.right - 1f * u, pliego.top + 1f * u), 13f * u, 4.5f * u, 40f)
            cinta(p, Offset(pliego.left + 1f * u, pliego.bottom - 1f * u), 13f * u, 4.5f * u, 40f)
            cinta(p, Offset(pliego.right - 1f * u, pliego.bottom - 1f * u), 13f * u, 4.5f * u, -40f)
        }
        // La canaleta de los lápices, al pie del tablero, con lo que hay en ella.
        val canal = fr(0.195f, 0.70f, 0.805f, 0.745f)
        sombra(p, caja(canal), canal.center, 2.5f)
        volumen(p, canal, Mat.MADERA, 1.4f * u, 0.4f)
        lapiz(p, Offset(w * 0.36f, canal.top - 1.2f * u), Offset(w * 0.47f, canal.top - 1.6f * u), Mat.LADRILLO)
        lapiz(p, Offset(w * 0.50f, canal.top - 1.4f * u), Offset(w * 0.59f, canal.top - 1.0f * u), Mat.VERDE)
        frasco(p, Offset(w * 0.235f, canal.top), h * 0.10f, Mat.LADRILLO, destapado = true)
        frasco(p, Offset(w * 0.285f, canal.top), h * 0.075f, Mat.TINTA, destapado = false)
        rollo(p, Offset(w * 0.66f, canal.top - h * 0.03f), h * 0.03f)
        lata(p, Offset(w * 0.79f, canal.top + 1f * u), h * 0.13f, e.semilla)
        // En el piso: la carpeta de cartón apoyada en la pata y los carteles enrollados.
        val carpeta = poligono(fr(0.07f, 0.70f), fr(0.20f, 0.69f), fr(0.215f, 0.965f), fr(0.085f, 0.965f))
        sombra(p, carpeta, fr(0.14f, 0.8f), 3f)
        pinta(p, carpeta, Mat.ESTRAZA, 0.1f, fr(0.14f, 0.8f))
        drawLine(p.color(Mat.LADRILLO, 0.2f, fr(0.14f, 0.8f)), fr(0.135f, 0.70f), fr(0.15f, 0.965f), 1.2f * u)
        enrollados(p, fr(0.80f, 0.965f), w * 0.17f, h * 0.045f)
        // La lámpara de brazo, prensada al canto derecho del tablero, con la cabeza sobre la esquina del pliego.
        lampara(p, fr(0.785f, 0.52f), fr(0.905f, 0.30f), fr(0.83f, 0.09f), 130f, h * 0.15f, ni)
        acabado(p, ni)
    }

    // ---------------------------------------------------------------- Otras vidas: la caja de tipos sobre la mesa

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val ni = fr(0.13f, 0.31f, 0.87f, 0.81f)
        val foco = fr(0.12f, 0.20f)
        val p = pincel(oscuro, foco, w * 0.6f, listOf(
            Mancha(fr(0.30f, 0.12f), w * 0.12f, Ti.MAGENTA, 4), Mancha(fr(0.78f, 0.10f), w * 0.13f, Ti.ULTRAMAR, 7), Mancha(fr(0.95f, 0.70f), w * 0.09f, Ti.BERMELLON, 12)))
        val u = p.u
        pared(p, Rect(0f, 0f, w, h), e.semilla, listOf(fr(0.05f, 0.5f), fr(0.95f, 0.5f)))
        // A la derecha, en la pared: la entrada del baile clavada; la regla T apoyada en la mesa.
        hoja(p, fr(0.905f, 0.14f, 0.99f, 0.36f), 6f, Hoja.ENTRADA, 6)
        // La caja de tipos: el marco de pino, con su sombra en la pared.
        val marco = fr(0.10f, 0.24f, 0.90f, 0.87f)
        sombra(p, caja(marco), marco.center, 4f)
        volumen(p, marco, Mat.PINO, 2.4f * u, 0.15f)
        if (!p.tinta) pinceladas(marco, TrazosCiudad.AL_SOL, e.semilla, 10, u, if (p.o) 0.03f else 0.08f)
        // El fondo de los cajetines (el nicho) y los cuatro tabiques.
        drawRect(p.nicho(e, Mat.PINO), ni.topLeft, ni.size)
        for (k in 1..4) {
            val x = ni.left + ni.width * k / 5f
            val t = Rect(x - 1.1f * u, ni.top, x + 1.1f * u, ni.bottom)
            pinta(p, caja(t), Mat.PINO, -0.2f, t.center, grosor = 0.4f)
            if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.25f else 0.12f), Offset(t.right, ni.top), Size(2.2f * u, ni.height))
        }
        // La sombra del travesaño de arriba dentro de los cajetines.
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.3f else 0.14f), ni.topLeft, Size(ni.width, 3f * u))
        else if (!p.o) trama(caja(Rect(ni.left, ni.top, ni.right, ni.top + 2.5f * u)), 1.6f * u, 0.4f * u, TrazosTinta.GRABAR, 45f)
        // La tira de abajo con la cantonera de latón y el tirador de la caja.
        val tira = Rect(marco.left, ni.bottom, marco.right, marco.bottom)
        val t = Offset(tira.center.x, tira.center.y)
        pinta(p, redondo(Rect(t.x - 9f * u, t.y - 2.2f * u, t.x + 9f * u, t.y + 2.2f * u), 2f * u), Mat.OCRE, 0.5f, t, grosor = 0.5f)
        val rotulo = Rect(t.x - 22f * u, t.y - 2.6f * u, t.x - 12f * u, t.y + 2.6f * u)
        pinta(p, caja(rotulo), Mat.PAPEL, 0.4f, rotulo.center, grosor = 0.4f)
        // La mesa.
        val mesa = fr(0f, 0.87f, 1f, 0.95f)
        bordeMesa(p, mesa, fr(0.03f, 0.95f, 0.97f, 1.0f), e.semilla + 1)
        reglaT(p, fr(0.955f, 0.40f), h * 0.47f, 8f, escuadra = false)
        // Encima de la caja: los carteles enrollados, el frasco, el sencillo de canto y el pase.
        val encima = marco.top
        enrollados(p, Offset(w * 0.20f, encima), w * 0.20f, h * 0.05f)
        frasco(p, Offset(w * 0.46f, encima), h * 0.11f, Mat.VERDE, destapado = false)
        sencillo(p, Rect(w * 0.55f, encima - h * 0.17f, w * 0.64f, encima), -5f)
        hoja(p, Rect(w * 0.66f, encima - h * 0.12f, w * 0.75f, encima), 10f, Hoja.PASE, 2, chinche = false)
        taza(p, Offset(w * 0.82f, encima), h * 0.09f)
        // La lámpara, prensada al borde de la mesa a la izquierda, con la cabeza sobre la caja.
        lampara(p, Offset(w * 0.045f, mesa.top + 2f * u), fr(0.025f, 0.30f), fr(0.09f, 0.06f), 40f, h * 0.15f, ni)
        acabado(p, ni)
    }

    // ---------------------------------------------------------------- Los íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val s = min(h * 0.86f, w * 0.5f)
        val c = Offset(w / 2f, h / 2f)
        val p = pincel(oscuro, c, s, listOf(Mancha(c + Offset(-s * 0.3f, -s * 0.1f), s * 0.3f, Ti.MAGENTA, 1), Mancha(c + Offset(s * 0.35f, s * 0.15f), s * 0.28f, Ti.ULTRAMAR, 2)))
        // En los íconos, u es relativo al ícono (≈ 60 dp), no al ancho del lienzo.
        val q = Pincel(p.tinta, p.o, p.i, max(s / 60f, 0.01f), if (oscuro && !p.tinta) c + Offset(-s * 0.4f, -s * 0.5f) else p.foco, if (oscuro && !p.tinta) s * 1.2f else p.alcance, p.manchas)
        when (id) {
            "unesco" -> torrePuente(q, c, s)
            "rascar" -> rascar(q, c, s)
            else -> plano(q, c, s, e)
        }
    }

    /** La torre del puente sobre el estrecho, con los cables y la niebla que entra a ras del agua. */
    private fun DrawScope.torrePuente(p: Pincel, c: Offset, s: Float) {
        val u = p.u
        val fondo = Rect(c.x - s * 0.95f, c.y - s * 0.5f, c.x + s * 0.95f, c.y + s * 0.5f)
        clipPath(redondo(fondo, s * 0.08f)) {
            // El agua y la otra orilla.
            val agua = Rect(fondo.left, c.y + s * 0.18f, fondo.right, fondo.bottom)
            pinta(p, caja(agua), Mat.VERDE, -0.1f, agua.center, contorno = false)
            val loma = Path().apply {
                moveTo(fondo.left, agua.top); quadraticTo(c.x - s * 0.6f, c.y - s * 0.05f, c.x - s * 0.2f, agua.top); close()
            }
            pinta(p, loma, Mat.DUELA, -0.2f, c, contorno = false)
            // Los cables: el principal en curva y las péndolas.
            val torre = c.x + s * 0.25f
            val cab = Path().apply {
                moveTo(fondo.left, c.y - s * 0.05f); quadraticTo(c.x - s * 0.35f, c.y + s * 0.18f, torre, c.y - s * 0.4f)
                quadraticTo(torre + s * 0.35f, c.y + s * 0.0f, fondo.right, c.y - s * 0.12f)
            }
            val colorTorre = if (p.tinta) p.ink(Ti.BERMELLON) else p.ciudad(NARANJA, 0.2f, c)
            drawPath(cab, colorTorre, style = Stroke(1.4f * u))
            for (k in 0 until 9) {
                val x = fondo.left + (torre - fondo.left) * (k + 0.5f) / 9f
                val tt = (x - fondo.left) / (torre - fondo.left)
                val y0 = c.y - s * 0.05f + (1 - tt) * (1 - tt) * 0f + 2 * (1 - tt) * tt * (s * 0.23f) + tt * tt * (-s * 0.35f)
                drawLine(colorTorre.copy(alpha = 0.7f), Offset(x, y0), Offset(x, c.y + s * 0.12f), 0.5f * u)
            }
            // El tablero del puente.
            val tab = Rect(fondo.left, c.y + s * 0.10f, fondo.right, c.y + s * 0.15f)
            pinta(p, caja(tab), Mat.NARANJA, -0.3f, tab.center)
            // La torre: dos patas con sus travesaños escalonados.
            for (lado in listOf(-1f, 1f)) {
                val pata = Rect(torre + lado * s * 0.07f - s * 0.035f, c.y - s * 0.44f, torre + lado * s * 0.07f + s * 0.035f, c.y + s * 0.3f)
                pinta(p, caja(pata), Mat.NARANJA, if (lado < 0) 0.5f else -0.5f, pata.center, grosor = 0.5f)
            }
            for (k in 0 until 4) {
                val y = c.y - s * (0.36f - 0.12f * k)
                pinta(p, caja(Rect(torre - s * 0.07f, y, torre + s * 0.07f, y + s * 0.035f)), Mat.NARANJA, 0.2f, Offset(torre, y), grosor = 0.4f)
            }
            // La niebla que entra a ras: bandas planas que se superponen.
            for (k in 0 until 3) {
                val y = c.y + s * (0.12f + 0.1f * k)
                val banda = Path().apply {
                    moveTo(fondo.left, y)
                    val pasos = 16
                    for (t in 0..pasos) { val x = fondo.left + fondo.width * t / pasos; lineTo(x, y - s * 0.05f * sin(t * 0.9f + k)) }
                    lineTo(fondo.right, y + s * 0.09f); lineTo(fondo.left, y + s * 0.09f); close()
                }
                if (p.tinta) { drawPath(banda, (if (p.o) TrazosTinta.CREMA else HOJA_TINTA).copy(alpha = 0.55f)); drawPath(banda, p.ink(Ti.GRABADO).copy(alpha = 0.6f), style = Stroke(0.5f * u)) }
                else drawPath(banda, p.ciudad(TrazosCiudad.BRUMA, 0.5f, c).copy(alpha = 0.55f))
            }
        }
        drawPath(redondo(fondo, s * 0.08f), if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, c).copy(alpha = 0.6f), style = Stroke(0.8f * u))
    }

    /** La hoja entintada de negro que la plumilla raspa hasta descubrir el cartel debajo. */
    private fun DrawScope.rascar(p: Pincel, c: Offset, s: Float) {
        val u = p.u
        val hojaR = Rect(c.x - s * 0.7f, c.y - s * 0.45f, c.x + s * 0.55f, c.y + s * 0.45f)
        sombra(p, caja(hojaR), hojaR.center, 2f)
        // Lo de debajo: el disco y los rayos del cartel.
        pinta(p, caja(hojaR), Mat.PAPEL, 0.2f, hojaR.center, grosor = 0.6f)
        val cd = Offset(hojaR.left + hojaR.width * 0.62f, hojaR.top + hojaR.height * 0.55f)
        clipPath(caja(hojaR)) {
            drawPath(TrazosTinta.rayos(cd, s * 0.12f, s * 0.9f, 14, 2f * u, 1.5f), p.color(Mat.NARANJA, 0.2f, cd))
            drawCircle(p.color(Mat.VERDE, 0.2f, cd), s * 0.12f, cd)
        }
        // La tinta negra que aún cubre: todo menos la parte raspada, de borde mordido.
        val raspado = Path().apply {
            moveTo(hojaR.right, hojaR.bottom)
            lineTo(hojaR.left + hojaR.width * 0.32f, hojaR.bottom)
            val pasos = 10
            for (k in 0..pasos) {
                val t = k / pasos.toFloat()
                val x = hojaR.left + hojaR.width * (0.32f + 0.62f * t)
                val y = hojaR.bottom - hojaR.height * (0.15f + 0.7f * t) + (if (k % 2 == 0) 1 else -1) * 1.6f * u
                lineTo(x, y)
            }
            lineTo(hojaR.right, hojaR.top + hojaR.height * 0.1f)
            close()
        }
        val cubierta = Path().apply { addRect(hojaR); addPath(raspado); fillType = PathFillType.EvenOdd }
        drawPath(cubierta, if (p.tinta) (if (p.o) Color(0xFF241D31) else TrazosTinta.GRABAR) else p.ciudad(TINTA, 0f, c))
        // Las virutas de tinta raspada.
        for (k in 0 until 4) drawArc(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, c), 0f, 220f, false,
            Offset(hojaR.left + hojaR.width * (0.45f + 0.1f * k), hojaR.bottom + 1f * u), Size(3f * u, 2.4f * u), style = Stroke(0.6f * u))
        // La plumilla, en diagonal sobre el borde raspado.
        val a = Offset(hojaR.right + s * 0.25f, hojaR.top - s * 0.15f)
        val b = Offset(hojaR.left + hojaR.width * 0.62f, hojaR.top + hojaR.height * 0.45f)
        if (!p.tinta) drawLine(TrazosCiudad.SOMBRA.copy(alpha = 0.3f), a + Offset(2f * u, 2.5f * u), b + Offset(2f * u, 2.5f * u), 3f * u, cap = StrokeCap.Round)
        drawLine(p.color(Mat.MADERA, 0.3f, a), a, a + (b - a) * 0.7f, 3.2f * u, cap = StrokeCap.Round)
        drawLine(p.color(Mat.METAL, 0.4f, b), a + (b - a) * 0.7f, b, 2.2f * u, cap = StrokeCap.Round)
        drawLine(p.color(Mat.TINTA, 0f, b), a + (b - a) * 0.93f, b, 1f * u, cap = StrokeCap.Round)
    }

    /** El plano del barrio, con el Panhandle y el parque, y tres chinchetas unidas por el camino. */
    private fun DrawScope.plano(p: Pincel, c: Offset, s: Float, e: EstadoIcono) {
        val u = p.u
        val hojaR = Rect(c.x - s * 0.85f, c.y - s * 0.46f, c.x + s * 0.85f, c.y + s * 0.46f)
        sombra(p, caja(hojaR), hojaR.center, 2f)
        pinta(p, caja(hojaR), Mat.PERIODICO, 0.2f, hojaR.center, grosor = 0.6f)
        clipPath(caja(hojaR)) {
            // El parque, a la izquierda, y la franja larga del Panhandle que entra en la cuadrícula.
            pinta(p, caja(Rect(hojaR.left, hojaR.top + hojaR.height * 0.35f, hojaR.left + hojaR.width * 0.22f, hojaR.bottom)), Mat.VERDE, 0.1f, c, contorno = false)
            pinta(p, caja(Rect(hojaR.left + hojaR.width * 0.22f, hojaR.top + hojaR.height * 0.35f, hojaR.left + hojaR.width * 0.62f, hojaR.top + hojaR.height * 0.47f)), Mat.VERDE, 0.1f, c, contorno = false)
            // La cuadrícula de las calles.
            val linea = if (p.tinta) p.ink(Ti.GRABADO).copy(alpha = 0.6f) else p.ciudad(TINTA, 0f, c).copy(alpha = 0.35f)
            for (k in 1 until 9) drawLine(linea, Offset(hojaR.left + hojaR.width * k / 9f, hojaR.top), Offset(hojaR.left + hojaR.width * k / 9f, hojaR.bottom), 0.5f * u)
            for (k in 1 until 5) drawLine(linea, Offset(hojaR.left, hojaR.top + hojaR.height * k / 5f), Offset(hojaR.right, hojaR.top + hojaR.height * k / 5f), 0.5f * u)
        }
        // El camino entre los tres lugares, en línea ondulada, y las chinchetas.
        val a = Offset(hojaR.left + hojaR.width * 0.3f, hojaR.top + hojaR.height * 0.25f)
        val b = Offset(hojaR.left + hojaR.width * 0.62f, hojaR.top + hojaR.height * 0.72f)
        val d = Offset(hojaR.left + hojaR.width * 0.84f, hojaR.top + hojaR.height * 0.3f)
        val camino = Path().apply { moveTo(a.x, a.y); quadraticTo(a.x - s * 0.05f, b.y, b.x, b.y); quadraticTo(b.x + s * 0.25f, b.y, d.x, d.y) }
        drawPath(camino, if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(LADRILLO, 0.2f, c), style = Stroke(1.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2.5f * u, 1.8f * u))))
        chincheta(p, a, 3.2f * u, Mat.LADRILLO)
        chincheta(p, b, 3.2f * u, Mat.ETIQUETA)
        chincheta(p, d, 3.2f * u, Mat.LADRILLO)
        drawPath(caja(hojaR), if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, c).copy(alpha = 0.5f), style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- Notas: el corcho sobre la mesa de dibujo

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height
        if (w <= 0f || h <= 0f) return
        val ni = fr(0.08f, 0.12f, 0.92f, 0.78f)
        val foco = fr(0.90f, 0.10f)
        val p = pincel(oscuro, foco, w * 0.75f, listOf(
            Mancha(fr(0.06f, 0.10f), w * 0.12f, Ti.MAGENTA, 13), Mancha(fr(0.95f, 0.55f), w * 0.11f, Ti.ULTRAMAR, 14), Mancha(fr(0.45f, 0.02f), w * 0.10f, Ti.BERMELLON, 15)))
        val u = p.u
        pared(p, Rect(0f, 0f, w, h), e.semilla, listOf(fr(0.03f, 0.45f), fr(0.97f, 0.45f)))
        // El marco de pino del corcho, con su sombra en la pared, y el corcho (el nicho).
        val marco = fr(0.05f, 0.085f, 0.95f, 0.815f)
        sombra(p, caja(marco), marco.center, 4f)
        volumen(p, marco, Mat.PINO, 2.4f * u, 0.15f)
        if (!p.tinta) pinceladas(marco, TrazosCiudad.AL_SOL, e.semilla, 10, u, if (p.o) 0.03f else 0.08f)
        val corcho = p.nicho(e, Mat.CORCHO)
        drawRect(corcho, ni.topLeft, ni.size)
        // El grano del corcho, apenas: puntitos que no estorban al texto de las notas.
        val punto = if (p.tinta) p.ink(Ti.MAGENTA).copy(alpha = if (p.o) 0.10f else 0.12f) else lerp(corcho, if (p.o) TrazosCiudad.LAMPARA_LUZ else TrazosCiudad.SOMBRA, 0.25f).copy(alpha = 0.5f)
        for (k in 0 until 260) drawCircle(punto, (0.35f + 0.5f * azar(e.semilla, k)) * u, Offset(ni.left + ni.width * azar(e.semilla + 1, k), ni.top + ni.height * azar(e.semilla + 2, k)))
        // La sombra del marco dentro del corcho, arriba y a la izquierda (la luz viene de ahí: la sombra del canto cae en el corcho).
        if (!p.tinta) drawRect(TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.3f else 0.15f), ni.topLeft, Size(ni.width, 2.5f * u))
        else if (!p.o) trama(caja(Rect(ni.left, ni.top, ni.right, ni.top + 2.5f * u)), 1.6f * u, 0.4f * u, TrazosTinta.GRABAR, 45f)
        // En el marco: el boceto en la esquina de arriba, el recorte del periódico y la entrada del baile al costado.
        fueraDe(ni) {
            hoja(p, fr(0.015f, 0.005f, 0.17f, 0.115f), -5f, Hoja.BOCETO, 21)
            hoja(p, fr(0.53f, 0.0f, 0.70f, 0.11f), 3f, Hoja.RECORTE, 22)
            hoja(p, fr(0.0f, 0.40f, 0.075f, 0.62f), -8f, Hoja.ENTRADA, 23, chinche = false)
        }
        // La mesa de dibujo debajo del corcho, con sus cosas.
        val mesa = fr(0f, 0.86f, 1f, 0.92f)
        bordeMesa(p, mesa, fr(0.03f, 0.92f, 0.97f, 1.0f), e.semilla + 4)
        val pie = mesa.top + mesa.height * 0.35f
        frasco(p, Offset(w * 0.12f, pie), h * 0.06f, Mat.TINTA, destapado = false)
        frasco(p, Offset(w * 0.17f, pie), h * 0.055f, Mat.LADRILLO, destapado = true)
        lapiz(p, Offset(w * 0.24f, pie - 1f * u), Offset(w * 0.38f, pie - 0.5f * u), Mat.LADRILLO)
        rollo(p, Offset(w * 0.47f, pie - h * 0.025f), h * 0.025f)
        taza(p, Offset(w * 0.60f, pie), h * 0.055f)
        lata(p, Offset(w * 0.74f, pie), h * 0.09f, e.semilla + 1)
        // La lámpara de brazo, prensada a la mesa a la derecha, con la cabeza asomada sobre la esquina del marco.
        lampara(p, Offset(w * 0.935f, mesa.top + 1.5f * u), fr(0.985f, 0.45f), fr(0.905f, 0.05f), 145f, h * 0.10f, ni)
        acabado(p, ni)
    }

    /**
     * Una nota: un papel del oficio clavado en el corcho. Según el tipo: la ficha rayada (idea), la hoja de libreta de borde
     * cortado (pensamiento), la etiqueta de cartón con su ojal (propósito) o el boleto de transbordo con su banda (recordatorio).
     * La fijada lleva la etiqueta del disco del día en la esquina y una chincheta más. El texto va encima, en [tintaNota].
     */
    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width <= 0f || r.height <= 0f) return
        val p0 = pincel(oscuro, Offset(r.right * 1.2f, -r.height * 0.4f), r.width * 2.4f, listOf(Mancha(Offset(r.left, r.top), r.width, Ti.MAGENTA, 1), Mancha(Offset(r.right, r.bottom), r.width, Ti.ULTRAMAR, 2)))
        val p = Pincel(p0.tinta, p0.o, p0.i, max(r.width / 100f, 0.01f), p0.foco, p0.alcance, p0.manchas)
        val u = p.u
        val giro = listOf(-1.2f, 0.8f, -0.5f, 1.3f, -0.9f, 0.6f)[e.indice.mod(6)]
        val papel = Rect(r.left + r.width * 0.04f, r.top + r.height * 0.05f, r.right - r.width * 0.04f, r.bottom - r.height * 0.04f)
        // El color del papel, legible con el texto de tintaNota.
        val base: Color = when (e.tipo) {
            "pensamiento" -> PERIODICO
            "proposito" -> lerp(ESTRAZA, PAPEL, 0.5f)
            "recordatorio" -> Color(0xFFE6DCC4)
            else -> PAPEL
        }
        val fondoPapel = when {
            !p.tinta && !p.o -> lerp(base, Color.White, 0.18f)
            !p.tinta -> lerp(lerp(base, TrazosCiudad.PARED_NOCHE, 0.80f), TrazosCiudad.LAMPARA_LUZ, 0.06f)
            !p.o -> HOJA_TINTA
            else -> Color(0xFF211B2B)
        }
        rotate(giro, papel.center) {
            val forma = when (e.tipo) {
                "pensamiento" -> Path().apply {
                    // La hoja de libreta: el borde de arriba cortado de la espiral.
                    moveTo(papel.left, papel.top + 2f * u)
                    val n = 12
                    for (k in 0..n) lineTo(papel.left + papel.width * k / n, papel.top + (if (k % 2 == 0) 0.5f else 2.5f) * u)
                    lineTo(papel.right, papel.bottom); lineTo(papel.left, papel.bottom); close()
                }
                "proposito" -> Path().apply {
                    // La etiqueta de cartón: las esquinas de arriba cortadas.
                    val c = papel.width * 0.14f
                    moveTo(papel.left + c, papel.top); lineTo(papel.right - c, papel.top); lineTo(papel.right, papel.top + c)
                    lineTo(papel.right, papel.bottom); lineTo(papel.left, papel.bottom); lineTo(papel.left, papel.top + c); close()
                }
                else -> caja(papel)
            }
            // La sombra en el corcho.
            if (!(p.tinta && p.o)) {
                if (!p.tinta) translate(2.2f * u, 3f * u) { drawPath(forma, TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.55f else 0.3f)) }
                else translate(2.2f * u, 3f * u) { trama(forma, 1.8f * u, 0.5f * u, TrazosTinta.GRABAR.copy(alpha = 0.8f), 45f) }
            }
            drawPath(forma, fondoPapel)
            val linea = when {
                !p.tinta && !p.o -> TrazosCiudad.SOMBRA.copy(alpha = 0.16f)
                !p.tinta -> TrazosCiudad.LAMPARA_LUZ.copy(alpha = 0.12f)
                else -> p.ink(Ti.ULTRAMAR).copy(alpha = if (p.o) 0.25f else 0.2f)
            }
            when (e.tipo) {
                "idea" -> {
                    // La ficha rayada: el filete de color arriba y los renglones finos.
                    drawLine(if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(LADRILLO, 0.2f, papel.center).copy(alpha = 0.7f), Offset(papel.left, papel.top + papel.height * 0.14f), Offset(papel.right, papel.top + papel.height * 0.14f), 0.9f * u)
                    for (k in 1..6) drawLine(linea, Offset(papel.left, papel.top + papel.height * (0.14f + 0.12f * k)), Offset(papel.right, papel.top + papel.height * (0.14f + 0.12f * k)), 0.5f * u)
                }
                "pensamiento" -> {
                    // La libreta: renglones y el margen.
                    for (k in 1..7) drawLine(linea, Offset(papel.left, papel.top + papel.height * (0.06f + 0.12f * k)), Offset(papel.right, papel.top + papel.height * (0.06f + 0.12f * k)), 0.5f * u)
                    drawLine(if (p.tinta) p.ink(Ti.BERMELLON).copy(alpha = 0.5f) else p.ciudad(LADRILLO, 0f, papel.center).copy(alpha = 0.35f), Offset(papel.left + papel.width * 0.08f, papel.top + 3f * u), Offset(papel.left + papel.width * 0.08f, papel.bottom), 0.6f * u)
                }
                "proposito" -> {
                    // El ojal reforzado y el hilo que sube a la chincheta.
                    val o = Offset(papel.center.x, papel.top + papel.height * 0.09f)
                    drawCircle(if (p.tinta) p.ink(Ti.MAGENTA) else p.ciudad(OCRE, 0.3f, o), 3.6f * u, o)
                    drawCircle(lerp(fondoPapel, Color.Black, 0.3f), 1.6f * u, o)
                }
                else -> {
                    // El boleto de transbordo: la banda de color arriba y la fila de horas perforable abajo.
                    drawRect(if (p.tinta) p.ink(Ti.ULTRAMAR) else p.ciudad(OCRE, 0.2f, papel.center).copy(alpha = 0.75f), Offset(papel.left, papel.top + papel.height * 0.03f), Size(papel.width, papel.height * 0.07f))
                    for (k in 0 until 8) drawRect(linea.copy(alpha = linea.alpha * 1.6f), Offset(papel.left + papel.width * (0.07f + 0.11f * k), papel.bottom - papel.height * 0.1f), Size(papel.width * 0.08f, papel.height * 0.06f), style = Stroke(0.5f * u))
                }
            }
            // El borde del papel.
            drawPath(forma, if (p.tinta) p.contorno(papel.center) else TrazosCiudad.SOMBRA.copy(alpha = if (p.o) 0.4f else 0.3f), style = Stroke(0.6f * u))
            if (p.tinta && e.fijada) {
                // El filete doble del cartel.
                val m = papel.width * 0.035f
                drawRect(p.ink(Ti.MAGENTA), Offset(papel.left + m, papel.top + m), Size(papel.width - 2 * m, papel.height - 2 * m), style = Stroke(0.9f * u))
                drawRect(p.ink(Ti.ULTRAMAR), Offset(papel.left + 2.2f * m, papel.top + 2.2f * m), Size(papel.width - 4.4f * m, papel.height - 4.4f * m), style = Stroke(0.5f * u))
            }
            // El tipo, en la esquina de arriba a la derecha (dentro del margen): el sol, la onda, la flecha o el reloj.
            val q = Offset(papel.right - papel.width * 0.1f, papel.top + papel.height * 0.08f)
            val marca = if (p.tinta) p.ink(Ti.ULTRAMAR) else p.ciudad(VERDE, 0.2f, q)
            val rm = papel.width * 0.035f
            when (e.tipo) {
                "idea" -> { drawCircle(marca, rm, q); for (k in 0 until 8) { val a = k * 0.785f; drawLine(marca, q + Offset(cos(a), sin(a)) * (rm * 1.4f), q + Offset(cos(a), sin(a)) * (rm * 2f), 0.6f * u) } }
                "pensamiento" -> drawPath(Path().apply { moveTo(q.x - rm * 2f, q.y); quadraticTo(q.x - rm, q.y - rm * 1.6f, q.x, q.y); quadraticTo(q.x + rm, q.y + rm * 1.6f, q.x + rm * 2f, q.y) }, marca, style = Stroke(0.8f * u))
                "proposito" -> { drawLine(marca, q - Offset(rm * 2f, 0f), q + Offset(rm * 1.6f, 0f), 0.8f * u); drawPath(poligono(q + Offset(rm * 2.2f, 0f), q + Offset(rm * 0.9f, -rm), q + Offset(rm * 0.9f, rm)), marca) }
                else -> { drawCircle(marca, rm * 1.6f, q, style = Stroke(0.7f * u)); drawLine(marca, q, q - Offset(0f, rm * 1.1f), 0.6f * u); drawLine(marca, q, q + Offset(rm * 0.8f, 0f), 0.6f * u) }
            }
            // La fijada: la etiqueta del disco del día, pegada en la esquina de arriba a la izquierda.
            if (e.fijada) {
                val d = Offset(papel.left + papel.width * 0.1f, papel.top + papel.height * 0.09f)
                val rd = papel.width * 0.06f
                if (!p.tinta) drawCircle(TrazosCiudad.SOMBRA.copy(alpha = 0.3f), rd, d + Offset(0.8f * u, 1f * u))
                pinta(p, circulo(d, rd), Mat.ETIQUETA, 0.3f, d, grosor = 0.4f)
                drawCircle(if (p.tinta) p.ink(Ti.PAPEL) else p.ciudad(PAPEL, 0.3f, d), rd * 0.62f, d, style = Stroke(0.4f * u))
                drawCircle(if (p.tinta) p.ink(Ti.GRABADO) else p.ciudad(TINTA, 0f, d), rd * 0.14f, d)
            }
        }
        // La chincheta, arriba al centro (sin girar: está clavada en el corcho).
        val pin = Offset(r.center.x, r.top + r.height * 0.075f)
        val matPin = when (e.tipo) { "idea" -> Mat.LADRILLO; "pensamiento" -> Mat.VERDE; "proposito" -> Mat.OCRE; else -> Mat.ETIQUETA }
        chincheta(p, pin, 3.2f * u, if (e.fijada) Mat.LADRILLO else matPin)
        if (e.fijada) chincheta(p, Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.09f), 2.6f * u, Mat.VERDE)
    }

    /** El texto sobre las notas: el café casi negro (o la tinta azul negra) en claro; el crema en oscuro. */
    override fun tintaNota(oscuro: Boolean): Color {
        val tinta = EstadoTema.caraDe(TemaId.PSICODELIA) == Cara.INTENSA
        return when {
            oscuro && tinta -> TrazosTinta.CREMA
            oscuro -> Color(0xFFEEE3D0)
            tinta -> TrazosTinta.GRABAR
            else -> TINTA
        }
    }
}
