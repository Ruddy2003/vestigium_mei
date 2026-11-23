package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * La paleta del estante de Pirata: la cámara de popa de un balandro del Caribe hacia 1718. El mamparo de cuarterones pintado
 * de verde cardenillo; la librería de caoba de las Antillas con el fondo de los casilleros de minio; el latón de las
 * barandillas de mar y el hierro de los cáncamos; el roble del bao y el pino fregado de la cubierta; y lo del bodegón. Dos
 * temperaturas: lo cálido es la caoba, el minio y la llama; lo frío, el verde, el mar de la ventana y la luna.
 */
internal object PaletaPirata {
    /** El verde cardenillo del mamparo: su cara, su luz, su sombra y lo hondo de las juntas. */
    val VERDE = Color(0xFF3F6B58)
    val VERDE_CLARO = Color(0xFF6F9A82)
    val VERDE_OSC = Color(0xFF2A4A3C)
    val VERDE_HONDO = Color(0xFF162A21)
    /** La caoba del mueble: su cara, su luz, su sombra y lo hondo. */
    val CAOBA = Color(0xFF6C2C18)
    val CAOBA_LUZ = Color(0xFFA9562E)
    val CAOBA_OSC = Color(0xFF3C160C)
    val CAOBA_HONDA = Color(0xFF220A05)
    /** El minio del fondo de los casilleros y su sombra. */
    val MINIO = Color(0xFF9E3A26)
    val MINIO_OSC = Color(0xFF5E1D13)
    /** El roble del bao, el pino fregado de la cubierta, la brea de las costuras. */
    val ROBLE = Color(0xFF8C6C4A)
    val ROBLE_LUZ = Color(0xFFBC9E74)
    val ROBLE_OSC = Color(0xFF4E3A26)
    val PINO = Color(0xFFC09A6A)
    val PINO_OSC = Color(0xFF8A6844)
    val BREA = Color(0xFF29211B)
    /** El latón y el hierro. */
    val LATON = Color(0xFFC9A04A)
    val LATON_OSC = Color(0xFF7A5A22)
    val HIERRO = Color(0xFF383A40)
    val HIERRO_LUZ = Color(0xFF8C909A)
    /** El papel, la vitela cruda, la tinta de agallas y el lacre. */
    val PAPEL = Color(0xFFEEE3C6)
    val PAPEL_OSC = Color(0xFFBBA77C)
    val TINTA = Color(0xFF22170E)
    val LACRE = Color(0xFFA02C1C)
    /** El cabo de cáñamo y el bramante. */
    val CABO = Color(0xFFCDB47C)
    val CABO_OSC = Color(0xFF86683C)
    /** La lona embreada de lo que no tiene ánimo. */
    val EMBREADO = Color(0xFF2E2B27)
    /** Los del bodegón: el vidrio, la arena, las limas, la piña, la jícara, el fieltro, el cuerno, el barro, el mimbre y el coco. */
    val VIDRIO = Color(0xFFD6E6DE)
    val ARENA = Color(0xFFE2BF72)
    val LIMA = Color(0xFF8DB43A)
    val LIMA_OSC = Color(0xFF4A6E22)
    val PINA = Color(0xFFD9A23A)
    val PINA_OSC = Color(0xFF8E5A1C)
    val HOJA = Color(0xFF4E7A3C)
    val JICARA = Color(0xFF9C6A30)
    val FIELTRO = Color(0xFF2A2522)
    val GALON = Color(0xFFD7B358)
    val CUERNO = Color(0xFFDCC48E)
    val BARRO = Color(0xFFB96B3C)
    val VIDRIADO = Color(0xFF6E8A52)
    val MIMBRE = Color(0xFFC69C58)
    val COCO = Color(0xFF6C4626)
    /** El cielo y el mar de la ventana, y la espuma de la estela. */
    val CIELO = Color(0xFFD6E8EC)
    val CIELO_ALTO = Color(0xFF8DBBD2)
    val MAR = Color(0xFF2C6F88)
    val ESPUMA = Color(0xFFEFF5F2)
    /** La noche de arriba (azul de mar, honda) y la de los Sueños (añil). */
    val NOCHE = Color(0xFF0A1830)
    val NOCHE_SUENOS = Color(0xFF120F38)
    /** Lo hondo del cuerpo del mueble de noche: sangre de toro (el minio y la caoba siguen rojos); en los Sueños, ciruela. */
    val SANGRE = Color(0xFF1C0806)
    val HONDO_SUENOS = Color(0xFF221030)
    /** La sombra del ocaso de los Sueños claros (violeta, con dirección) y su luz naranja. */
    val SOMBRA_OCASO = Color(0xFF3E2A62)
    val OCASO = Color(0xFFF2A060)
    /** La llama, su corazón, la luna y el roce de la luna en los cantos. */
    val LLAMA = Color(0xFFFFB45A)
    val FUEGO = Color(0xFFFFE6AE)
    val LUNA = Color(0xFFE9EFF7)
    val LUNA_RAS = Color(0xFF9CB8DA)
    /** La sombra fría del día (la del verde, el vidrio y el hierro). */
    val FRIO = Color(0xFF2B3E4C)

    val LETRA: FontFamily = LetraExtra.caslon
}

/**
 * El aire del momento en el estante de Pirata: de día (la luz de la ventana de la aleta), de noche (el modo oscuro: arde el
 * candelero de cardán, las velas de los huecos y, abajo, el fanal) y en los Sueños ([noche]: el ocaso en claro, la luna
 * llena en oscuro). De noche cada color va de su hondo (oscuro, de su mismo tono, con el aire de la noche) a su color según
 * cuánta luz le llega ([cerca], de 0 a 1), con el calor de la llama encima. Arriba ([cuerpo] falso) el aire es el azul de
 * mar de la ventana; en el cuerpo del mueble y en la cubierta, lo que no se alumbra se hunde en un rojo de sangre de toro (en
 * los Sueños, en un ciruela hondo): el minio y la caoba siguen siendo rojos de noche.
 */
internal class AirePirata(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false) {
    private val p = PaletaPirata
    /** Si arde la vela. */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && cuerpo -> if (noche) p.HONDO_SUENOS else p.SANGRE
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.12f + n.red * 0.84f, c.green * 0.12f + n.green * 0.84f, c.blue * 0.12f + n.blue * 0.84f, c.alpha)
    }

    /**
     * El color de algo a la luz de la vela: el sebo alumbra en naranja, así que el azul y el verde bajan más que el rojo (no
     * es el color con un velo naranja encima, que lo lavaría).
     */
    private fun aLaVela(c: Color): Color = Color((c.red * 1.12f).coerceAtMost(1f), c.green * 0.9f, c.blue * 0.62f, c.alpha)

    /** El color al ocaso de los Sueños claros: algo más bajo y más tibio (la luz es naranja y rasante; no hay velo). */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.95f, c.green * 0.86f, c.blue * 0.8f, c.alpha)

    /**
     * Arriba, cerca del candelero, la luz de la vela vuelve ámbar lo que alumbra (el verde del mamparo pasa a ocre, no a
     * oliva): se toma la luminosidad del color y se le da el tono de la llama, en la medida [k] de la luz que le llega.
     */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaVela(c)
        if (cuerpo) return mezcla(c, v, 0.75f)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.6f).coerceAtMost(1f), (l * 1.12f).coerceAtMost(1f), l * 0.52f, c.alpha)
        return mezcla(mezcla(c, v, 0.75f), tono, 0.62f * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.12f * k) else c
        return mezcla(mezcla(hundido(c), ambar(c, k), (1.05f * k).coerceAtMost(0.93f)), p.LLAMA, 0.14f * k)
    }
    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (lumbre) return luz(c, cerca)
        return if (f > 0f) mezcla(c, luzMueble(1f), f) else if (f < 0f) mezcla(c, sombraMueble(1f), -f) else c
    }
    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.2f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del verde, el vidrio y el hierro); de noche, la misma que las demás. */
    fun frio(a: Float): Color = if (lumbre) sombra(a) else p.FRIO.copy(alpha = a.coerceIn(0f, 1f))
    /** La luz crema del día; al ocaso, la naranja; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.08f) else if (noche) p.OCASO.copy(alpha = (a * 0.5f).coerceIn(0f, 1f)) else luzMueble(a)
    /** El brillo de lo que mira a la luz: de día crema; al ocaso, naranja; de noche, el filo de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.OCASO, 0.55f).copy(alpha = (a * 0.75f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la vela en lo que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent
    /** La segunda luz de la noche: la luna de la ventana, fría, que roza los cantos que la miran (la izquierda). */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = (if (noche) a * 1.4f else a).coerceIn(0f, 1f)) else Color.Transparent
    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (lo dorado sigue dorado,
     * pardo lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.24f, t.green * 0.21f, t.blue * 0.2f, t.alpha), hondo, 0.22f)
        val encendido = mezcla(t, aLaVela(t), 0.55f)
        return mezcla(apagado, encendido, (1.08f * k).coerceAtMost(1f))
    }
    /**
     * El papel viejo o la madera clara donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo
     * lejos (nunca más claro que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = Color(c.red * 0.56f, c.green * 0.47f, c.blue * 0.36f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.93f, c.blue * 0.72f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 0.8).toFloat())
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoPirata(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoPirata(centro: Offset, ancho: Float, alto: Float, a: AirePirata, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo o tornillo de cabeza redonda: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.remachePirata(p: Offset, r: Float, a: AirePirata, k: Float, metal: Color = PaletaPirata.HIERRO) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón, el vidrio o lo vidriado (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloPirata(c: Offset, r: Float, a: AirePirata, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaPirata.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaPirata.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroPirata(r: Rect, a: AirePirata, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Pirata, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_PIRATA: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.PIRATA, false) to juegoTonos(TemaId.PIRATA, true) }

private fun cuadradoPirata(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaPirata(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoPirata(t.red - a0.red - dr * f) + cuadradoPirata(t.green - a0.green - dg * f) + cuadradoPirata(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los materiales del ánimo, de bajo a alto: el ánimo es el material de la entrada, en todo su cuerpo. Lo bajo, cuero o lona
 * teñidos del azul verde del mar hondo; lo de en medio, vitela y lona crudas; lo alto, cordobán dorado. Siguen la escala del
 * tema (`juegoTonos`), con los puntos de en medio algo más cerca de su extremo para que no salgan lavados.
 */
private val MATERIALES_PIRATA = listOf(
    0f to Color(0xFF17505C), 0.3f to Color(0xFF2A7E7C), 0.42f to Color(0xFF5E9C90), 0.5f to Color(0xFFE2D5B0),
    0.58f to Color(0xFFDDB66C), 0.7f to Color(0xFFCC9236), 1f to Color(0xFFA6681A),
)

/**
 * El material de una entrada según su ánimo [t] (ya de día): se ubica en la escala de día del tema y se toma el mismo punto en
 * [MATERIALES_PIRATA]; si no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores (el ánimo
 * de en medio pasa de la lona a un casi negro): aquí la entrada es un objeto de la cámara, así que primero se vuelve al color
 * de día (el mismo punto de la escala de noche en la de día) y después la luz dibujada lo apaga con la distancia.
 */
internal fun materialPirata(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_PIRATA
    val x = (if (oscuro) enEscalaPirata(t, noche) else null) ?: enEscalaPirata(t, dia) ?: return t
    for (i in 0 until MATERIALES_PIRATA.size - 1) {
        val (f0, c0) = MATERIALES_PIRATA[i]; val (f1, c1) = MATERIALES_PIRATA[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return MATERIALES_PIRATA.last().second
}

/**
 * Lo fijo del estante (la corona con su bodegón y el pie), pintado una vez por tamaño y modo en dos imágenes guardadas que
 * luego se pegan. Cada imagen va de fila entera a fila entera: la de arriba llega hasta el píxel entero de debajo de la
 * corona y la de abajo empieza en el píxel entero de encima del pie, con lo pintado hasta ahí, así la junta con el cuerpo no
 * deja asomar el fondo. Se guarda solo la última de cada una (cambian de tamaño o de modo muy pocas veces).
 */
internal object CapaPirata {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    /** Pega la imagen de las filas [arriba] a [abajo] (enteras) de todo el ancho; la pinta antes con [pintar] si cambió. */
    fun DrawScope.pegar(ranura: Int, clave: String, arriba: Int, abajo: Int, pintar: DrawScope.() -> Unit) {
        val ancho = ceil(size.width).toInt(); val alto = abajo - arriba
        if (ancho < 1 || alto < 1 || ranura !in 0..1) return
        var img = imagenes[ranura]
        if (img == null || claves[ranura] != clave) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                translate(0f, -arriba.toFloat()) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave; img = nueva
        }
        drawImage(img, Offset(0f, arriba.toFloat()))
    }
}

/**
 * El dibujo del estante del diario de Pirata (lo usa `EstantePirata`, en `BibliotecaModerna.kt`): la librería empotrada de
 * caoba de la cámara de popa de un balandro del Caribe hacia 1718. La cornisa volada con su cordón tallado y su galería de
 * latón, el friso con sus tableros, los montantes con un cabo tallado y sus cáncamos (va trincada al barco), una barandilla de
 * mar de latón delante de cada balda, el fondo de los casilleros de minio y el cuerpo de dos cajones con tiradores de gota.
 * Contra el mamparo de cuarterones pintado de verde, con la ventana de la aleta en escorzo a la izquierda (de día, su paño de
 * luz y el reflejo del agua; de noche, la luna) y el candelero de cardán a la derecha, que de noche arde; en los huecos,
 * una palmatoria o un candelero que de noche también arden; abajo, el fanal. La pared, la ventana y la cubierta están en
 * [MuroEstantePirata]; el mueble, en [ObraEstantePirata]; el bodegón, en [CoronaEstantePirata] y [MesaEstantePirata]; lo de
 * los huecos, en [HuecosEstantePirata]; el pie, en [PiezasEstantePirata]; las entradas, en [VolumenEstantePirata].
 */
internal object MuebleEstantePirata {
    private val P = PaletaPirata

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstantePirata` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.32f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (32 + (palabras / 40).coerceAtMost(16)).dp

    /** Cuál de los tres es una entrada: 0, el cuaderno de bitácora; 1, el canuto de cartas; 2, el paquete de hule. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 9)) * 29 + (semilla ushr 4), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el cuaderno, el más alto; el canuto; el paquete, más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.87f + 0.06f * azar(semilla, 1)
        1 -> 0.84f + 0.06f * azar(semilla, 1)
        else -> 0.79f + 0.05f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde el candelero de cardán, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.885f
    const val LAMP_Y = 0.262f
    /** Dónde está el fanal: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val FANAL_X = 0.3f
    const val FANAL_Y = 0.62f
    /** Dónde está la luna (la ventana), para el roce frío de la noche. */
    const val LUNA_X = 0.06f
    const val LUNA_Y = 0.2f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** A cuántos dp del piso de la repisa corre la barandilla de mar (la misma en el mueble y en cada entrada). */
    const val RIEL = 21f
    /** El grueso de la barandilla, en dp. */
    const val RIEL_G = 3.2f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.112f
    /** En la base: dónde acaba el cuerpo de los cajones y empieza la cubierta, en fracción de su alto. */
    const val SUELO = 0.36f
    /** La vela de un hueco: a qué alto de su repisa arde (en fracción del hueco, desde abajo). */
    const val VELA_Y = 0.46f

    /** La luz del candelero: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la llama, en anchos del mueble. */
    fun lampara(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.3f else 1.6f) * exp(-2.3f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.85f else 1f)))).coerceIn(0f, 1f)

    /** La luz del candelero en la pared y en lo de arriba: más corta que en el cuerpo, para que lo lejano se hunda. */
    fun lamparaArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.25f else 1.5f) * exp(-2.8f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.03f, 1f)

    /** La luz del fanal de la cubierta: más corta, solo para lo de abajo. */
    fun fanal(dx: Float, dy: Float): Float = (0.95f * exp(-4f * sqrt(dx * dx * 1.2f + dy * dy))).coerceIn(0f, 1f)

    /** La luz de la vela de un hueco: corta, para sus vecinos, el fondo y el canto de la tabla de arriba. */
    fun vela(dx: Float, dy: Float): Float = (0.95f * exp(-5.6f * sqrt(dx * dx + dy * dy * 1.4f))).coerceIn(0f, 1f)

    /** La vela que más alumbra ([x], [y]) entre [velas] (x, y, x, y… en anchos). */
    private fun velaMayor(x: Float, y: Float, velas: FloatArray): Float {
        var m = 0f
        var i = 0
        while (i + 1 < velas.size) { m = maxOf(m, vela(x - velas[i], y - velas[i + 1])); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): el candelero, el fanal a la altura [yFanal] y las [velas] de los huecos. */
    fun calor(x: Float, y: Float, yFanal: Float, suenos: Boolean, velas: FloatArray): Float {
        val kl = lampara(x - LAMP_X, y - LAMP_Y, suenos)
        val kf = fanal(x - FANAL_X, y - yFanal)
        val kv = velaMayor(x, y, velas)
        return (1f - (1f - kl) * (1f - kf) * (1f - kv)).coerceIn(0.03f, 1f)
    }

    /** El roce frío de la luna en ([x], [y]), en anchos: solo arriba a la izquierda. */
    fun luna(x: Float, y: Float): Float {
        val dx = x - LUNA_X; val dy = y - LUNA_Y
        return (0.9f * exp(-2.6f * sqrt(dx * dx + dy * dy * 0.7f))).coerceIn(0f, 1f)
    }

    /**
     * El plano de luna de los Sueños oscuros: la franja fría que entra por la ventana y cruza en diagonal el mamparo y lo alto
     * del mueble. Cuánto le toca a ([x], [y]) (anchos): 1 en su eje, 0 fuera; se apaga hacia abajo.
     */
    fun franjaLuna(x: Float, y: Float): Float {
        val ex = 0.62f; val ey = 0.785f
        val px = x - 0.1f; val py = y - 0.2f
        val dist = kotlin.math.abs(px * ey - py * ex)
        val largo = px * ex + py * ey
        if (largo < 0f) return 0f
        val ancho = (1f - dist / 0.12f).coerceIn(0f, 1f)
        val caida = (1.25f - largo * 0.75f).coerceIn(0f, 1f)
        return ancho * caida
    }

    /** Las cuatro esquinas de la franja de luna, en anchos, de [desde] a [hasta] a lo largo de su eje. */
    fun esquinasFranja(desde: Float, hasta: Float): FloatArray {
        val ex = 0.62f; val ey = 0.785f; val nx = -ey; val ny = ex; val m = 0.12f
        val ax = 0.1f + ex * desde; val ay = 0.2f + ey * desde; val bx = 0.1f + ex * hasta; val by = 0.2f + ey * hasta
        return floatArrayOf(ax + nx * m, ay + ny * m, bx + nx * m, by + ny * m, bx - nx * m, by - ny * m, ax - nx * m, ay - ny * m)
    }

    /** Dónde arde el fanal, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yFanalNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * FANAL_Y

    /**
     * Dónde va la luz del hueco de una repisa (la palmatoria o el candelero, en x del lienzo), o null si no hay sitio. La usan
     * el hueco (para dibujarla) y el estante (para alumbrar con ella).
     */
    fun xLuzHueco(oc: Float, xDer: Float, w: Float): Float? {
        val x0 = oc + w * 0.02f; val x1 = xDer - w * 0.035f
        return if (x1 - x0 >= w * 0.1f) x0 + w * 0.05f else null
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AirePirata(oscuro, noche)
        val ac = AirePirata(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yF = (b.top + b.height * FANAL_Y) / w
        // Las velas de los huecos: una en cada hueco con sitio. Se apuntan para que cada volumen sepa cuánta luz le dan.
        val centros = FloatArray(d.repisas.size) { -1f }
        val velasL = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        d.repisas.forEachIndexed { k, r ->
            val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
            val x = xLuzHueco(oc, d.derecha.left, w) ?: return@forEachIndexed
            centros[k] = ((x - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            velasL.add(x / w); velasL.add((r.bottom - r.height * VELA_Y) / w)
        }
        val velas = if (a.lumbre) velasL.toFloatArray() else FloatArray(0)
        LucesHuecoPirata.poner(LucesHuecoPirata.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) lamparaArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yF, noche, velas)
        }
        val frio: (Offset) -> Float = { p -> if (a.oscuro) luna(p.x / w, p.y / w) else 0f }
        val clave = "${w.toInt()}:${ch}:${b.top}:${h}:$oscuro:$noche:$density"

        // Lo de arriba: la pared, la ventana, el bodegón y la cornisa, en una imagen que llega al píxel entero de debajo.
        with(CapaPirata) {
            pegar(0, "c:$clave", 0, ceil(ch).toInt()) {
                // Debajo de todo, un fondo opaco: ninguna junta de lo que se pinta encima deja la imagen transparente.
                drawRect(a.luz(P.CAOBA_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstantePirata) { mamparo(a, apoyo, ch, cerca, frio) }
                with(CoronaEstantePirata) { pared(a, apoyo, cerca) }
                with(ObraEstantePirata) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cerca) }
                with(CoronaEstantePirata) { encima(a, apoyo, cerca) }
                with(MuroEstantePirata) { luzDeNoche(a, ch); franjaDeLuna(a, 0f, ch + 2f) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, los montantes y las tablas.
        with(ObraEstantePirata) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, ac, cerca) }
            d.tablas.forEachIndexed { k, r -> tabla(k, r, ac, cerca) }
            montante(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, ac, cerca)
            montante(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, ac, cerca)
        }
        with(HuecosEstantePirata) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, ac, cerca)
            }
        }
        with(ObraEstantePirata) {
            d.repisas.forEach { r -> barandilla(r, d, ac, cerca) }
            luzCuerpo(ch, b.top, ac, yF, velas)
        }
        with(MuroEstantePirata) { solDelOcaso(a, d.izquierda, ch); franjaDeLuna(a, ch, b.top) }
        with(HuecosEstantePirata) { d.repisas.forEachIndexed { k, r -> llamaHueco(k, r, d, ac) } }
        with(ObraEstantePirata) { d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center)) } }
        // El cuerpo de los cajones. La última tabla acaba en un borde exacto y la base empieza en el alto redondeado del lienzo
        // menos su alto: entre las dos puede quedar medio píxel, así que los cajones empiezan en el píxel entero de encima de la
        // que acabe antes.
        val suelo = b.top + b.height * SUELO
        val top = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        with(ObraEstantePirata) { cajonera(Rect(0f, top, w, suelo), ac, cerca) }
        if (d.repisas.isNotEmpty()) granoMueble(Rect(0f, ch, w, suelo), oscuro, 0.8f)
        // Lo de abajo: la cubierta y lo que hay en ella, en una imagen desde el píxel entero de encima del suelo.
        with(CapaPirata) {
            val arriba = floor(suelo).toInt()
            pegar(1, "p:$clave", arriba, ceil(h).toInt()) {
                // Debajo, opaco y del color del canto bajo del zócalo, que es lo que asoma en la fila de la junta.
                drawRect(ac.luz(P.CAOBA_HONDA, cerca(Offset(w / 2, (top + suelo) / 2))), Offset(0f, arriba.toFloat()), Size(w, ceil(h) - arriba + 1f))
                val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yF, noche, FloatArray(0)) }
                with(MuroEstantePirata) { cubierta(ac, suelo, h, cercaPie) }
                with(PiezasEstantePirata) { alPie(ac, b, suelo, h, cercaPie) }
                granoMueble(Rect(0f, arriba.toFloat(), w, h), oscuro)
            }
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AirePirata(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        // Las velas de los huecos, tal como las apuntó el estante (si no las hay, solo el candelero y el fanal).
        val velas = if (a.lumbre) LucesHuecoPirata.velas(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yFn = yFanalNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yFn, v.noche, velas) else 1f
        val f = if (a.oscuro) maxOf(luna(fx, fy), if (v.noche) franjaLuna(fx, fy) else 0f) else 0f
        // De qué lado le llega la luz: de día, de la ventana (izquierda); de noche, de la luz que más alumbra.
        val kl = lampara(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kf = fanal(fx - FANAL_X, fy - yFn)
        var kv = 0f; var xv = fx
        var i = 0
        while (i + 1 < velas.size) { val e = vela(fx - velas[i], fy - velas[i + 1]); if (e > kv) { kv = e; xv = velas[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kv >= kl && kv >= kf -> if (xv >= fx) 1f else -1f
            kl >= kf -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < FANAL_X) 1f else -1f
        }
        // Lejos de la luz, lo guardado se hunde con color pero no desaparece.
        val kd = if (a.oscuro) maxOf(k, 0.06f) else k
        val tinte = materialPirata(v.tono, oscuro)
        with(VolumenEstantePirata) {
            when (clase(v.semilla)) {
                0 -> cuaderno(v, medidor, a, kd, f, hacia, tinte)
                1 -> canuto(v, medidor, a, kd, f, hacia, tinte)
                else -> paquete(v, medidor, a, kd, f, hacia, tinte)
            }
            if (a.oscuro && v.noche && f > 0.05f) lunaEncima(f)
        }
    }
}

/**
 * Dónde arden las velas de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en
 * su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan sus vecinas. Por repisa, el centro de la vela en
 * la misma medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoPirata {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Las velas de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguna si no se apuntaron. */
    fun velas(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val lado = MuebleEstantePirata.LADO
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(lado + c[k] * (1f - 2 * lado))
            l.add(MuebleEstantePirata.CORONA + k * MuebleEstantePirata.FILA / MuebleEstantePirata.ANCHO_NOMINAL +
                (MuebleEstantePirata.ALTO_REPISA.value * (1f - MuebleEstantePirata.VELA_Y)) / MuebleEstantePirata.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
