package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La paleta del estante de Renacimiento: la tienda del cartolaio (el papelero, librero y encuadernador) de Florencia hacia
 * 1490, donde el aprendiz compra su papel y donde acaban de llegar los libros impresos. Lo cálido: el muro pintado de rojo de
 * tierra, el nogal tallado con sus dorados, el papel, el latón y la luz tamizada de la impannata. Lo frío: la pietra serena del
 * marco de la ventana, de la cornisa del cuarto y del suelo, el verde de cobre del fondo de las repisas y el añil de la alfombra.
 */
internal object PaletaRenacimiento {
    /** El muro: el rojo de tierra del enlucido pintado, su luz, lo hondo y la cal que asoma donde se gastó. */
    val ROJO_MURO = Color(0xFF9C4632)
    val ROJO_LUZ = Color(0xFFC0664A)
    val ROJO_OSC = Color(0xFF6E2C20)
    val CAL = Color(0xFFE6D8BC)
    /** El friso pintado de arriba: el verde de tierra y el ocre. */
    val VERDE_TIERRA = Color(0xFF5C6B4E)
    val OCRE = Color(0xFFC99A48)
    /** La pietra serena: el gris verdoso de la piedra labrada, su cara clara y lo hondo de sus molduras. */
    val SERENA = Color(0xFF8C948F)
    val SERENA_CLARA = Color(0xFFB9BEB6)
    val SERENA_OSC = Color(0xFF545C5E)
    /** El nogal tallado: su cara, su luz, lo hondo de la talla y la veta. */
    val NOGAL = Color(0xFF55331E)
    val NOGAL_LUZ = Color(0xFF8A5B36)
    val NOGAL_OSC = Color(0xFF2A170C)
    val VETA = Color(0xFF1C0E06)
    /** El oro de la talla (el dorado al agua, gastado en los cantos) y su brillo. */
    val ORO = Color(0xFFC39A45)
    val ORO_LUZ = Color(0xFFF2DC96)
    val ORO_OSC = Color(0xFF7A5A22)
    /** El fondo de las repisas, pintado de verde de cobre: su cara y su luz. */
    val VERDE_FONDO = Color(0xFF2D4A3C)
    val VERDE_LUZ = Color(0xFF4A6E58)
    /** El papel nuevo, el papel tostado, la tinta de imprenta, la tinta de agallas (sepia) y el rojo de las rúbricas. */
    val PAPEL = Color(0xFFF1E6CB)
    val PAPEL_OSC = Color(0xFFD5C097)
    val IMPRENTA = Color(0xFF221A16)
    val SEPIA = Color(0xFF3E2817)
    val RUBRICA = Color(0xFFB0402A)
    /** El latón (de la salvadera, los herrajes y las cantoneras) y su brillo; el hierro de herrajes, tijeras y clavos. */
    val LATON = Color(0xFFB68B38)
    val LATON_LUZ = Color(0xFFF4D98C)
    val HIERRO = Color(0xFF2E2F35)
    val HIERRO_LUZ = Color(0xFF7C808A)
    /** El cáñamo del bramante, la tela encerada de la impannata, la arpillera y el cuero pardo. */
    val CANAMO = Color(0xFFB9A072)
    val TELA = Color(0xFFE2C78C)
    val ARPILLERA = Color(0xFFA88E62)
    val CUERO = Color(0xFF6E3A20)
    /** La alfombra de Anatolia: el añil del campo, el rojo de rubia, el marfil y el azafrán. */
    val ANIL = Color(0xFF243A63)
    val RUBIA = Color(0xFFA2302A)
    val MARFIL = Color(0xFFEDE0C2)
    val AZAFRAN = Color(0xFFD7A13E)
    /** El azul de la mayólica y del papel de las resmas; la lacre de los sellos. */
    val AZUL = Color(0xFF3A5D8C)
    val LACRE = Color(0xFF9A2A20)
    /** El aire de la noche, el de los Sueños y el anochecer de los Sueños en claro; la llama, su centro, el sol y la luna. */
    val NOCHE = Color(0xFF160E12)
    val NOCHE_SUENOS = Color(0xFF0E1128)
    val ANOCHECER = Color(0xFF3A2E5E)
    val CREPUSCULO = Color(0xFFF0A066)
    val LLAMA = Color(0xFFFFB45A)
    val FUEGO = Color(0xFFFFE6AE)
    val SOL = Color(0xFFFFF0CC)
    val LUNA = Color(0xFFE4E8F4)
    /** Lo hondo del tinte: el ánimo se tiñe un poco hacia aquí para que sea cuero o papel teñido y no pastel. */
    val TINTE_HONDO = Color(0xFF3A2216)
    /** Lo que no tiene ánimo: el papel de estraza de las envolturas, pardo y sin teñir. */
    val SIN_ANIMO = Color(0xFFA88F6C)
    /**
     * Las pieles y papeles teñidos del ánimo, de lo malo a lo bueno, en el orden de la escala del tema (sienna, crema, azul):
     * el marroquí rojo hondo, el pardo rojizo, la vitela cálida, el verde y el azul pizarra. Así ningún lomo queda gris.
     */
    val PIELES = listOf(0f to Color(0xFF7A2618), 0.25f to Color(0xFFA2532C), 0.5f to Color(0xFFD9BA80), 0.74f to Color(0xFF4F7458), 1f to Color(0xFF2A4772))

    val LETRA: FontFamily = LetraExtra.cormorant
}

/**
 * El aire del momento en el estante de Renacimiento: de día (la luz de la impannata), de noche (el modo oscuro: solo el
 * farol) y en los Sueños ([noche]: al anochecer en claro, la noche más azul en oscuro, con la luna tras la tela). Con [en]
 * se pide el tono de algo según cuánta luz del farol le llega.
 */
internal class AireRenacimiento(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaRenacimiento
    /** Si arde el farol. */
    val lumbre: Boolean get() = oscuro || noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    /** El tono de algo al que le llega [k] del farol (de día, la luz de la ventana: [k] no cuenta). */
    fun en(k: Float) = TonoRenacimiento(this, if (lumbre) k.coerceIn(0f, 1f) else 1f)

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.16f + n.red * 0.8f, c.green * 0.16f + n.green * 0.8f, c.blue * 0.16f + n.blue * 0.8f, c.alpha)
    }

    fun luz(c: Color, cerca: Float): Color = en(cerca).c(c)
    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.25f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.4f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
    fun claro(a: Float): Color = luzMueble(if (oscuro) a * 0.1f else if (noche) a * 0.45f else a)

    /**
     * El color del ánimo hecho piel o papel teñido ([pielDelAnimo]), un poco hacia lo hondo con [tinte], con la luz que le
     * llega. De noche se apaga algo menos que lo demás y conserva su color, para que en la repisa más lejana los volúmenes se
     * sigan distinguiendo entre sí.
     */
    fun animo(t: Color?, k: Float, tinte: Float = 0f): Color {
        val base = if (t == null) p.SIN_ANIMO else pielDelAnimo(t)
        val viva = if (tinte > 0f) mezcla(base, p.TINTE_HONDO, tinte * 0.35f) else base
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(mezcla(viva, p.ANOCHECER, 0.24f - 0.14f * kk), p.LLAMA, 0.1f * kk) else viva
        return mezcla(mezcla(hundido(viva), viva, (0.36f + 0.7f * kk).coerceAtMost(0.95f)), p.LLAMA, 0.14f * kk)
    }
    /** El papel donde va el día: casi no se apaga, para que el número se lea siempre. */
    fun etiqueta(k: Float): Color {
        val kk = k.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(p.PAPEL, p.ANOCHECER, 0.16f - 0.1f * kk) else p.PAPEL
        return mezcla(mezcla(p.PAPEL, hondo, 0.5f - 0.44f * kk), p.LLAMA, 0.18f * kk)
    }
    val tinta: Color get() = p.SEPIA
}

/**
 * El tono de una cosa en un sitio: su color ([c]) con la luz que le llega, la sombra ([s]), el plano de luz ([b]: crema de
 * día, la llama de noche) y el filo del farol en lo que lo mira ([f], solo con el farol encendido).
 */
internal class TonoRenacimiento(val a: AireRenacimiento, val k: Float) {
    private val p = PaletaRenacimiento
    fun c(col: Color): Color = when {
        !a.lumbre -> col
        !a.oscuro -> mezcla(mezcla(col, p.ANOCHECER, 0.5f - 0.4f * k), p.LLAMA, 0.16f * k)
        else -> mezcla(mezcla(a.hundido(col), col, (1.05f * k).coerceAtMost(0.92f)), p.LLAMA, 0.2f * k)
    }
    fun s(alfa: Float): Color = a.sombra(alfa)
    fun b(alfa: Float): Color = when {
        !a.lumbre -> luzMueble(alfa)
        !a.oscuro -> mezcla(luzMueble(1f), p.LLAMA, k).copy(alpha = (alfa * (0.35f + 0.55f * k)).coerceIn(0f, 1f))
        else -> p.LLAMA.copy(alpha = (alfa * 0.95f * k).coerceIn(0f, 1f))
    }
    fun f(alfa: Float): Color = if (!a.lumbre || k < 0.04f) Color.Transparent else p.LLAMA.copy(alpha = (alfa * k * (if (a.oscuro) 1.1f else 0.6f)).coerceIn(0f, 0.85f))
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoRenacimiento(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoRenacimiento(centro: Offset, ancho: Float, alto: Float, a: AireRenacimiento, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.08f * hacia), centro.y - alto / 2), Size(ancho, alto))

/**
 * La sombra de una forma en el muro: de día corrida a la derecha y abajo (la ventana está arriba a la izquierda); con el
 * farol, hacia fuera de la llama, tanto más marcada cuanto más cerca está. No baja de [hasta] (la cornisa que la corta).
 */
internal fun DrawScope.sombraEnMuroRenacimiento(forma: Path, desde: Offset, hasta: Float, a: AireRenacimiento, fuerza: Float = 0.2f, lejos: Float = 1f) {
    val w = size.width
    clipRect(0f, 0f, w, hasta) {
        if (!a.oscuro) translate(w * 0.02f * lejos, w * 0.014f * lejos) { drawPath(forma, a.sombra(fuerza)) }
        else {
            val llama = Offset(w * MuebleEstanteRenacimiento.LAMP_X, w * MuebleEstanteRenacimiento.LAMP_Y)
            val k = MuebleEstanteRenacimiento.alcanceArriba((desde.x - llama.x) / w, (desde.y - llama.y) / w)
            if (k > 0.06f) {
                val dx = ((desde.x - llama.x) / w).coerceIn(-0.3f, 0.3f) / 0.3f
                translate(w * 0.05f * dx * lejos, w * 0.012f * lejos) { drawPath(forma, a.hondo.copy(alpha = (0.75f * k).coerceAtMost(0.55f))) }
            }
        }
    }
}

/** El destello de la luz en el latón, el oro o el vidriado (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloRenacimiento(c: Offset, r: Float, t: TonoRenacimiento) {
    if (!t.a.lumbre) { drawCircle(luzMueble(0.85f), r, c); return }
    if (t.k < 0.05f) return
    drawCircle(PaletaRenacimiento.LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaRenacimiento.FUEGO.copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c)
}

/** Las escalas del ánimo de Renacimiento, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_RENACIMIENTO: Pair<List<Color>, List<Color>> by lazy {
    juegoTonos(TemaId.RENACIMIENTO, false) to juegoTonos(TemaId.RENACIMIENTO, true)
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el ánimo neutro pasa del
 * papel a un casi negro, pensado para la interfaz), y un volumen claro se volvía negro de noche. Aquí el volumen es un objeto
 * de la tienda: conserva su color de día y el farol lo apaga con la distancia. Se ubica el color en la escala de noche (el
 * tramo y la fracción que más se le acercan) y se toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaRenacimiento(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_RENACIMIENTO
    if (dia.size < 3 || noche.size < 3) return t
    var mejor: Color = t
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = noche[seg]; val a1 = noche[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoRen(t.red - a0.red - dr * f) + cuadradoRen(t.green - a0.green - dg * f) + cuadradoRen(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = mezcla(dia[seg], dia[seg + 1], f) }
    }
    return mejor
}

private fun cuadradoRen(x: Float) = x * x

/**
 * La piel (o el papel teñido) que corresponde a un color del ánimo: se ubica el color en la escala de día del tema (el tramo y
 * la fracción que más se le acercan) y se toma el mismo punto en [PaletaRenacimiento.PIELES]. Si el color no está en la
 * escala, queda igual.
 */
internal fun pielDelAnimo(t: Color): Color {
    val dia = TONOS_RENACIMIENTO.first
    if (dia.size < 3) return t
    var mejor = -1f
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = dia[seg]; val a1 = dia[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoRen(t.red - a0.red - dr * f) + cuadradoRen(t.green - a0.green - dg * f) + cuadradoRen(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    if (mejor < 0f) return t
    val pieles = PaletaRenacimiento.PIELES
    for (i in 0 until pieles.size - 1) {
        val (f0, c0) = pieles[i]; val (f1, c1) = pieles[i + 1]
        if (mejor <= f1) return mezcla(c0, c1, ((mejor - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return pieles.last().second
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega: el muro con su estarcido y
 * su friso, la impannata, lo colgado, la cornisa tallada y el bodegón de encima. Es lo que más pesa del dibujo y no cambia al
 * moverse la pantalla ni al tocar un volumen. Se guarda solo la última (cambia de tamaño o de modo muy pocas veces).
 */
internal object CoronaRenacimiento {
    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun pegar(ds: DrawScope, alto: Float, a: AireRenacimiento, pintar: DrawScope.() -> Unit) {
        val w = ceil(ds.size.width).toInt()
        val h = ceil(alto).toInt().coerceAtMost(ceil(ds.size.height).toInt())
        if (w < 1 || h < 1) return
        val k = "$w:$h:${a.oscuro}:${a.noche}:${ds.density}"
        val img = imagen.takeIf { clave == k } ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(ds.density, ds.fontScale), ds.layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) { pintar() }
            imagen = nueva; clave = k
        }
        ds.drawImage(img)
    }
}

/**
 * El dibujo del estante del diario de Renacimiento (lo usa `EstanteRenacimiento`, en `BibliotecaModerna.kt`): el estante de
 * nogal tallado y dorado de la tienda de un cartolaio en Florencia hacia 1490, contra un muro pintado de rojo de tierra con su
 * estarcido, su friso y su cornisa de pietra serena, y la impannata (la ventana de tela encerada, sin vidrio) arriba a la
 * izquierda, que de día derrama una luz tibia y sin rayo. Arriba, el libro impreso abierto en su atril con los anteojos encima,
 * la resma envuelta con las tijeras, el tintero, el reloj de arena y el farol de hojas de asta sobre su pila de libros, que de
 * noche es la única luz; del muro cuelgan el cordel con los pliegos recién impresos, la piel de pergamino y la filza.
 * Al pie, sobre la alfombra de Anatolia, la bala de papel atada y la pila de cuadernillos. Todo el dibujo sale de [estante] y
 * de [volumen]; la luz de la noche de los dos sale de la misma curva, [alcance]. El muro, la ventana y el suelo están en
 * [MuroEstanteRenacimiento], el mueble en [ObraEstanteRenacimiento], lo de arriba en [PiezasEstanteRenacimiento], lo del pie y
 * de los huecos en [PieEstanteRenacimiento] y las entradas en [VolumenEstanteRenacimiento].
 */
internal object MuebleEstanteRenacimiento {
    private val P = PaletaRenacimiento

    // ---------------------------------------------------------------- las medidas

    const val CORONA = 0.6f
    const val BASE = 0.36f
    const val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 18.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (24 + (palabras / 40).coerceAtMost(20)).dp

    /** Cuál de los tres es una entrada: 0, el libro en piel teñida; 1, los pliegos atados en su envoltura; 2, el librito en vitela floja. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod(semilla xor (semilla ushr 7), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** El libro en piel, el más alto; los pliegos, más bajos y más altos cuanto más escribiste; el librito, mediano. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.08f * azar(semilla, 1)
        1 -> 0.66f + 0.12f * (palabras / 800f).coerceIn(0f, 1f) + 0.04f * azar(semilla, 1)
        else -> 0.78f + 0.08f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el farol (el medio de sus hojas de asta), en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.835f
    const val LAMP_Y = 0.322f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    private const val ANCHO_NOMINAL = 411f
    /** Una repisa con su tabla, en dp. */
    private const val FILA = 130f
    /** En la base: el rincón del muro con el suelo, lo de abajo del zócalo, donde pisan las garras y donde pisa lo del pie. */
    const val SUELO = 0.33f
    const val ZOCALO = 0.42f
    const val PISAN = 0.6f
    const val PIE = 0.95f
    /** Lo que vuela la cornisa sobre las repisas, en anchos: su alto. */
    const val CORNISA = 0.125f

    /**
     * La curva de la luz del farol, la misma para el mueble y para los volúmenes: cuánta llega (de 1 a 0) a un punto que
     * está a [dx] y [dy] de la llama, en anchos del mueble. Cae deprisa: la primera repisa encendida cerca del farol, la
     * segunda a medias, la tercera tenue y las de abajo casi perdidas; en un estante corto, la última siempre en penumbra.
     */
    fun alcance(dx: Float, dy: Float, repisas: Int): Float {
        val d = sqrt(dx * dx * 1.1f + dy * dy)
        val corto = if (repisas <= 2) 1.25f else 1f
        return (1.85f * exp(-2.5f * d * corto)).coerceIn(0f, 1f)
    }

    /** La luz del farol en lo de arriba (el muro, la cornisa y lo que hay en ella). */
    fun alcanceArriba(dx: Float, dy: Float): Float = (1.8f * exp(-3.8f * sqrt(dx * dx + dy * dy))).coerceIn(0f, 1f)

    /**
     * En los Sueños, cuánta luz de luna entra por la ventana (de 0 a 1) en un punto del cuerpo del mueble: llega por la
     * izquierda a la cornisa y a las dos primeras repisas y se pierde hacia la derecha y hacia abajo. [pos] cuenta repisas.
     */
    fun lunaEn(fx: Float, pos: Float, oscuro: Boolean): Float =
        ((0.85f - fx) / 0.85f).coerceIn(0f, 1f) * (1f - 0.42f * pos).coerceIn(0f, 1f) * (if (oscuro) 1f else 0.7f)

    /** La luz que llega al cuerpo del mueble: [pos] cuenta repisas desde la de arriba (1.5 es media altura de la segunda). */
    fun cercaCuerpo(pos: Float, repisas: Int, fx: Float): Float =
        alcance(fx - LAMP_X, CORONA - LAMP_Y + pos * FILA / ANCHO_NOMINAL, repisas)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireRenacimiento(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val b = d.base
        val fila = if (n > 0) d.repisas[0].height + d.tablas[0].height else FILA * density
        val llama = Offset(w * LAMP_X, w * LAMP_Y)
        val cerca: (Offset) -> Float = { p ->
            if (p.y <= ch) alcanceArriba((p.x - llama.x) / w, (p.y - llama.y) / w) else cercaCuerpo((p.y - ch) / maxOf(1f, fila), n, p.x / w)
        }
        val apoyo = ch - w * CORNISA        // donde pisa lo de arriba: la cara de la cimasa

        // Lo de arriba (el muro, la ventana, lo colgado, la cornisa y lo que hay encima) no cambia entre dibujos: se pinta
        // una vez por tamaño y modo y se pega.
        CoronaRenacimiento.pegar(this, ch, a) {
            with(MuroEstanteRenacimiento) {
                muro(a, apoyo, ch, cerca)
                ventana(a)
            }
            with(PiezasEstanteRenacimiento) { colgado(a, apoyo, cerca) }
            with(ObraEstanteRenacimiento) { cornisa(Rect(0f, apoyo, w, ch), a, cerca) }
            with(PiezasEstanteRenacimiento) { arriba(a, apoyo, cerca) }
            if (a.lumbre) with(FarolEstanteRenacimiento) { haloFarol(a) }
            if (noche) with(ObraEstanteRenacimiento) { lunaEnCornisa(Rect(0f, apoyo, w, ch), a) }
        }
        with(ObraEstanteRenacimiento) {
            juntasAbajo(a, b, d.tablas.lastOrNull()?.bottom, cerca)
            juntasRepisas(a, d.repisas, cerca)
        }
        with(MuroEstanteRenacimiento) { losas(a, b.top + b.height * PISAN, h, cerca) }
        with(ObraEstanteRenacimiento) {
            bajoMueble(a, b)
            d.repisas.forEachIndexed { k, r -> interior(k, r, d.ocupado.getOrNull(k) ?: r.left, a, cerca) }
        }
        with(PieEstanteRenacimiento) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                huecoLibre(k, r, oc, a, cerca)
            }
        }
        with(ObraEstanteRenacimiento) {
            d.tablas.forEachIndexed { k, r -> tabla(r, 610 + 7 * k, a, cerca, k) }
            d.placas.forEach { p -> placa(p, medidor, a, cerca(p.r.center)) }
            pilastra(d.izquierda, false, a, n, fila)
            pilastra(d.derecha, true, a, n, fila)
            zocalo(b, a, cerca)
        }
        if (a.lumbre) {
            // La luz del farol sobre lo que tiene cerca: la cornisa y la repisa de arriba.
            val f = if (oscuro) 1f else 0.55f
            mancha(llama + Offset(-w * 0.02f, w * 0.12f), w * 0.3f, P.LLAMA.copy(alpha = 0.16f * f))
        }
        with(PieEstanteRenacimiento) { alPie(a, b, cerca) }
        granoMueble(Rect(0f, 0f, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireRenacimiento(oscuro, v.noche)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val k = if (a.lumbre) cercaCuerpo(v.repisa + 0.55f, v.repisas, fx) else 1f
        // De qué lado le llega la luz: de día, de la impannata (izquierda); de noche, del farol (casi siempre a la derecha).
        val luzDe = if (a.lumbre && fx < LAMP_X) 1f else -1f
        // De día, lo de la derecha queda algo más lejos de la ventana.
        val dia = if (a.lumbre) 0f else 0.1f * fx + 0.03f * v.repisa
        val tono = tonoDeDiaRenacimiento(v.tono, oscuro)
        // En los Sueños, la luna de la ventana da en frío por la izquierda en las primeras repisas.
        val luna = if (v.noche) lunaEn(fx, v.repisa.toFloat(), oscuro) else 0f
        with(VolumenEstanteRenacimiento) {
            when (clase(v.semilla)) {
                0 -> libroPiel(v, tono, medidor, a, k, luzDe, dia, luna)
                1 -> pliegos(v, tono, medidor, a, k, luzDe, dia, luna)
                else -> librito(v, tono, medidor, a, k, luzDe, dia, luna)
            }
        }
    }
}
