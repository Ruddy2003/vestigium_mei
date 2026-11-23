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
 * La paleta del estante de Art nouveau: el despacho de la tienda del taller de litografía de París hacia 1900. La caoba roja
 * del mueble y su marquetería de limoncillo, sicomoro y madera teñida de verde; el papel-cuero dorado del fondo de los
 * casilleros; el papel pintado verde gris de la pared con su motivo en verde hondo y oro pálido; el latón y el bronce; el
 * papel crema, la cartulina y la tinta; las flores, el vidrio camafeo y el grès flameado; las baldosas hidráulicas. Dos
 * temperaturas: lo cálido es la caoba, el dorado y las lámparas; lo frío, el verde de la pared, el violeta y la luz del café.
 */
internal object PaletaNouveau {
    /** La caoba del mueble: su cara, su luz, su sombra y lo hondo. */
    val CAOBA = Color(0xFF6E2A1C)
    val CAOBA_LUZ = Color(0xFFB45C3A)
    val CAOBA_OSC = Color(0xFF441710)
    val CAOBA_HONDA = Color(0xFF250A07)
    /** La marquetería: el limoncillo, el sicomoro y la madera teñida de verde. */
    val LIMONCILLO = Color(0xFFE2BE70)
    val SICOMORO = Color(0xFFEDE0BE)
    val TENIDA = Color(0xFF6F8C58)
    /** El papel-cuero dorado del fondo de los casilleros: su cara, su luz y lo hundido del estampado. */
    val CUERO = Color(0xFFA9802F)
    val CUERO_LUZ = Color(0xFFD9B35C)
    val CUERO_OSC = Color(0xFF6E5018)
    /** El papel pintado de la pared: verde gris, su luz, su sombra, el motivo y el oro pálido; la cenefa de ocre. */
    val PARED = Color(0xFF4E6C60)
    val PARED_LUZ = Color(0xFF7C9A8A)
    val PARED_OSC = Color(0xFF2F4A3E)
    val MOTIVO = Color(0xFF2C4639)
    val ORO = Color(0xFFD9BE7A)
    val ORO_OSC = Color(0xFF8E7234)
    val CENEFA = Color(0xFFB38A3A)
    /** El latón y el bronce dorado. */
    val LATON = Color(0xFFC9A04C)
    val LATON_LUZ = Color(0xFFF4E0A0)
    val LATON_OSC = Color(0xFF6E5220)
    /** El papel, la cartulina de las etiquetas (de valor medio), el tafilete de los tejuelos y la tinta. */
    val PAPEL = Color(0xFFF2EBDA)
    val PAPEL_OSC = Color(0xFFCDBF9C)
    val CARTULINA = Color(0xFFC8AC78)
    val TARJETA = Color(0xFFB9B89A)
    val TAFILETE = Color(0xFF7C3A24)
    val TINTA = Color(0xFF2A2530)
    /** Las flores inventadas: el violeta del iris, la malva, el blanco del lirio, el amarillo, el rosa viejo, la hoja. */
    val IRIS = Color(0xFF7E5FA6)
    val IRIS_OSC = Color(0xFF4A3270)
    val MALVA = Color(0xFFB48AB0)
    val LIRIO = Color(0xFFF3EBD6)
    val AMARILLO = Color(0xFFE2B84A)
    val ROSA = Color(0xFFD69A84)
    val HOJA = Color(0xFF5F7E40)
    val HOJA_OSC = Color(0xFF2F4A26)
    val TALLO = Color(0xFF8FA67E)
    /** El vidrio camafeo de Nancy: del ámbar al violeta, con el verde. */
    val AMBAR = Color(0xFFD9A14A)
    val VIOLETA = Color(0xFF6B4E8C)
    val VIDRIO_VERDE = Color(0xFF7FA88A)
    /** El grès flameado: el esmalte verde azul, su chorreado claro y el barro. */
    val GRES = Color(0xFF2F5E66)
    val GRES_CHORRO = Color(0xFF8FB49A)
    val GRES_BARRO = Color(0xFF7A5434)
    /** Las baldosas hidráulicas: crema, salvia, ocre y violeta. */
    val BALDOSA = Color(0xFFE4D8BA)
    val BALDOSA_SALVIA = Color(0xFF7A9670)
    val BALDOSA_OCRE = Color(0xFFC29646)
    val BALDOSA_VIOLETA = Color(0xFF6A5282)
    /** La percalina sin ánimo (habano), el papel de estraza y la seda de las cintas. */
    val HABANO = Color(0xFF6E4E34)
    val ESTRAZA = Color(0xFFB08A5A)
    val SEDA = Color(0xFFE9D9A8)
    /** La noche de arriba (verde botella, el de la calle con el café) y la del cuerpo (vino oscuro); y las de los Sueños. */
    val AIRE_NOCHE = Color(0xFF0C1E1A)
    val CUERPO_NOCHE = Color(0xFF260E12)
    val AIRE_SUENOS = Color(0xFF111833)
    val CUERPO_SUENOS = Color(0xFF1C1230)
    /** La sombra del ocaso de los Sueños claros (violeta) y su luz naranja. */
    val SOMBRA_OCASO = Color(0xFF3E2A62)
    val OCASO = Color(0xFFF2A060)
    /** El sol del escaparate. */
    val SOL = Color(0xFFFFE3A6)
    /** La luz eléctrica del aplique, la del quinqué y la vela, su corazón; el verde del café y la luna. */
    val LAMPARA = Color(0xFFF5D488)
    val LLAMA = Color(0xFFFFB85E)
    val FUEGO = Color(0xFFFFEDC0)
    val CAFE = Color(0xFF6FB58C)
    val LUNA = Color(0xFFDCE4F2)
    val LUNA_RAS = Color(0xFFA8BCDA)
    /** La sombra fría del día. */
    val FRIO = Color(0xFF2B3E4C)

    val LETRA: FontFamily = LetraExtra.federant
    val TEXTO: FontFamily = LetraExtra.brygada
}

/**
 * El aire del momento en el estante de Art nouveau: de día (el sol del escaparate), de noche (el modo oscuro: el aplique, el
 * quinqué y la palmatoria encendidos) y en los Sueños ([noche]: el ocaso en claro, la luna en oscuro). De noche cada color va
 * de su hondo (oscuro, de su mismo tono, con el aire de la noche) a su color según cuánta luz le llega ([cerca], de 0 a 1),
 * dorado por la lámpara. Arriba ([cuerpo] falso) el aire hondo es el verde botella de la calle; en el cuerpo del mueble y al
 * pie, un vino oscuro: la caoba y el dorado siguen siendo cálidos de noche.
 */
internal class AireNouveau(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false) {
    private val p = PaletaNouveau
    /** Si arden las lámparas. */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && cuerpo -> if (noche) p.CUERPO_SUENOS else p.CUERPO_NOCHE
        oscuro -> if (noche) p.AIRE_SUENOS else p.AIRE_NOCHE
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.14f + n.red * 0.82f, c.green * 0.14f + n.green * 0.82f, c.blue * 0.14f + n.blue * 0.82f, c.alpha)
    }

    /** El color de algo a la luz eléctrica y de petróleo: dorada, el azul baja más que el rojo (no es un velo encima). */
    private fun aLaLampara(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.64f, c.alpha)

    /** El color al ocaso de los Sueños claros: algo más bajo y más tibio (la luz es naranja y rasante; no hay velo). */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.96f, c.green * 0.87f, c.blue * 0.82f, c.alpha)

    /**
     * Cerca de la lámpara, su luz vuelve dorado lo que alumbra (el verde de la pared pasa a oliva dorado, no a gris): se toma la
     * luminosidad del color y se le da el tono de la luz, en la medida [k] de la que le llega.
     */
    private fun dorado(c: Color, k: Float): Color {
        val v = aLaLampara(c)
        if (cuerpo) return mezcla(c, v, 0.8f)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.55f).coerceAtMost(1f), (l * 1.2f).coerceAtMost(1f), l * 0.6f, c.alpha)
        return mezcla(mezcla(c, v, 0.7f), tono, 0.45f * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(hundido(c), dorado(c, k), (1.05f * k).coerceAtMost(0.94f)), p.LAMPARA, 0.12f * k)
    }
    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (lumbre) return luz(c, cerca)
        return if (f > 0f) mezcla(c, luzMueble(1f), f) else if (f < 0f) mezcla(c, sombraMueble(1f), -f) else c
    }
    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color = if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La luz crema del día; al ocaso, la naranja; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.06f) else if (noche) p.OCASO.copy(alpha = (a * 0.5f).coerceIn(0f, 1f)) else luzMueble(a)
    /** El brillo de lo que mira a la luz: de día crema; al ocaso, naranja; de noche, el filo de la lámpara según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.OCASO, 0.5f).copy(alpha = (a * 0.8f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LAMPARA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la lámpara en lo que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent
    /** La luz fría que entra por el escaparate de noche: el verde del café de enfrente; en los Sueños, la luna. */
    fun fria(a: Float): Color = if (oscuro) (if (noche) p.LUNA_RAS else p.CAFE).copy(alpha = a.coerceIn(0f, 1f)) else Color.Transparent
    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (el violeta sigue violeta,
     * hondo lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.24f, t.green * 0.22f, t.blue * 0.24f, t.alpha), hondo, 0.25f)
        val encendido = mezcla(t, aLaLampara(t), 0.5f)
        return mezcla(apagado, encendido, (1.08f * k).coerceAtMost(1f))
    }
    /**
     * La cartulina o el tafilete donde va el día, teñido por la luz que le llega: dorado cerca de la lámpara, pardo hondo lejos
     * (nunca más claro que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = mezcla(Color(c.red * 0.5f, c.green * 0.41f, c.blue * 0.32f, c.alpha), hondo, 0.1f)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.94f, c.blue * 0.72f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 0.85).toFloat())
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoNouveau(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde no da la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoNouveau(centro: Offset, ancho: Float, alto: Float, a: AireNouveau, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** El destello de la luz en el latón, el vidrio o el esmalte (de día, crema; de noche, el de la lámpara si le llega). */
internal fun DrawScope.destelloNouveau(c: Offset, r: Float, a: AireNouveau, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaNouveau.LAMPARA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaNouveau.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Las escalas del ánimo de Art nouveau, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_NOUVEAU: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.NOUVEAU, false) to juegoTonos(TemaId.NOUVEAU, true) }

private fun cuadradoNouveau(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaNouveau(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoNouveau(t.red - a0.red - dr * f) + cuadradoNouveau(t.green - a0.green - dg * f) + cuadradoNouveau(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto: el ánimo es la percalina, el papel de seda o el papel de cartas de la entrada, en todo
 * su cuerpo. Lo bajo, verde salvia hondo; lo de en medio, crema; lo alto, violeta de iris. Siguen la escala del tema
 * (`juegoTonos`), con los puntos de en medio algo más cerca de su extremo para que no salgan lavados.
 */
private val TINTES_NOUVEAU = listOf(
    0f to Color(0xFF2E5A40), 0.3f to Color(0xFF557A58), 0.42f to Color(0xFF94A784), 0.5f to Color(0xFFE6D8B6),
    0.58f to Color(0xFFBBA2C8), 0.7f to Color(0xFF8A68A8), 1f to Color(0xFF55357A),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_NOUVEAU]; si
 * no está en la escala, queda igual. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa del crema a un casi
 * negro): aquí la entrada es un objeto del despacho, así que primero se vuelve al color de día (el mismo punto de la escala de
 * noche en la de día, como `tonoDeDiaDeco`) y después la luz dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaNouveau(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_NOUVEAU
    val x = (if (oscuro) enEscalaNouveau(t, noche) else null) ?: enEscalaNouveau(t, dia) ?: return t
    for (i in 0 until TINTES_NOUVEAU.size - 1) {
        val (f0, c0) = TINTES_NOUVEAU[i]; val (f1, c1) = TINTES_NOUVEAU[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_NOUVEAU.last().second
}

/**
 * Lo fijo del estante (la corona con su bodegón y el cuerpo bajo con el suelo), pintado una vez por tamaño y modo en dos
 * imágenes guardadas que luego se pegan. Cada imagen va de fila entera a fila entera; se guarda solo la última de cada una.
 */
internal object CapaNouveau {
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
 * El dibujo del estante del diario de Art nouveau (lo usa `EstanteNouveau`, en `BibliotecaNouveau.kt`): la biblioteca abierta
 * de caoba del despacho de la tienda del taller de litografía, en la planta de la calle, hecha en un taller del faubourg
 * Saint-Antoine a la moda de 1900. Los montantes suben como tallos y pasan la cornisa hasta un capullo tallado; la cornisa con
 * su gola y el friso de marquetería de flores; cada casillero con sus hombros curvos tallados y el fondo de papel-cuero
 * dorado; las tablas con su bocel y la cartela de marquetería del mes; abajo, el cuerpo de dos puertas talladas, el faldón y
 * los pies de raíz sobre las baldosas hidráulicas. Contra la pared de papel pintado verde gris. De día, el sol del escaparate
 * (fuera del cuadro, a la izquierda) deja su paño con el dibujo de los montantes curvos; de noche arden el aplique eléctrico,
 * el quinqué de un hueco y la palmatoria del pie, y por el escaparate entra el verde del café de enfrente (en los Sueños, la
 * luna). La pared y el suelo están en [MuroEstanteNouveau]; el mueble, en [ObraEstanteNouveau]; el bodegón, en
 * [CoronaEstanteNouveau]; lo de los huecos, en [HuecosEstanteNouveau]; el pie, en [PieEstanteNouveau]; las entradas, en
 * [VolumenEstanteNouveau]; las flores, en [FloresNouveau].
 */
internal object MuebleEstanteNouveau {
    private val P = PaletaNouveau

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteNouveau` las lee al dibujar)

    val CORONA = 0.64f
    val BASE = 0.36f
    val LADO = 0.078f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    fun ancho(palabras: Int): Dp = (30 + (palabras / 40).coerceAtMost(18)).dp

    /** Cuál de los tres es una entrada: 0, el tomo de la revista; 1, el rollo de carteles; 2, el legajo de cartas. */
    fun clase(semilla: Int): Int {
        val k = Math.floorMod((semilla xor (semilla ushr 9)) * 31 + (semilla ushr 5), 7)
        return if (k < 3) 0 else if (k < 5) 1 else 2
    }

    /** Llenan de cuatro quintos a nueve décimos del casillero: el tomo, el más alto; los rollos; el legajo, más bajo. */
    fun alto(semilla: Int, palabras: Int): Float = when (clase(semilla)) {
        0 -> 0.86f + 0.07f * azar(semilla, 1)
        1 -> 0.84f + 0.08f * azar(semilla, 1)
        else -> 0.8f + 0.06f * azar(semilla, 1)
    }

    // ---------------------------------------------------------------- la luz

    /** Dónde arde la tulipa del aplique, en anchos del mueble desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.885f
    const val LAMP_Y = 0.285f
    /** Dónde arde la palmatoria del pie: a lo ancho, y a lo alto en fracción del pie (desde su borde de arriba). */
    const val VELA_X = 0.42f
    const val VELA_Y = 0.4f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 134f
    /** La cornisa, en anchos: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas. */
    const val CORNISA = 0.132f
    /** En la base: dónde pisan los pies del mueble en el suelo, en fracción de su alto. */
    const val PISA = 0.32f
    /** El quinqué de un hueco: a qué alto de su repisa arde su tulipa (en fracción del hueco, desde abajo). */
    const val QUINQUE_Y = 0.62f

    /** La luz del aplique: cuánta llega (de 1 a 0) a un punto a [dx] y [dy] de la tulipa, en anchos del mueble. */
    fun aplique(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.05f else 1.6f) * exp(-3.1f * sqrt(dx * dx * 1.1f + dy * dy * (if (dy > 0f) 0.8f else 1.1f)))).coerceIn(0f, 1f)

    /** La luz del aplique en la pared y en lo de arriba: más corta, para que lo lejano se hunda. */
    fun apliqueArriba(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 1.0f else 1.5f) * exp(-3.6f * sqrt(dx * dx + dy * dy * 1.1f))).coerceIn(0.03f, 1f)

    /** La luz de la palmatoria del pie: corta, para lo de abajo. */
    fun palmatoria(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.7f else 1f) * exp(-4.2f * sqrt(dx * dx * 1.15f + dy * dy))).coerceIn(0f, 1f)

    /** La luz del quinqué de un hueco: para sus vecinos, el fondo y el canto de las tablas de arriba y de abajo. */
    fun quinque(dx: Float, dy: Float, suenos: Boolean): Float =
        ((if (suenos) 0.72f else 1.05f) * exp(-5.2f * sqrt(dx * dx + dy * dy * 1.3f))).coerceIn(0f, 1f)

    /** El quinqué que más alumbra ([x], [y]) entre [luces] (x, y, x, y… en anchos). */
    private fun quinqueMayor(x: Float, y: Float, luces: FloatArray, suenos: Boolean): Float {
        var m = 0f
        var i = 0
        while (i + 1 < luces.size) { m = maxOf(m, quinque(x - luces[i], y - luces[i + 1], suenos)); i += 2 }
        return m
    }

    /** Cuánta luz cálida llega a ([x], [y]) (anchos): el aplique, la palmatoria a la altura [yVela] y los [quinques]. */
    fun calor(x: Float, y: Float, yVela: Float, suenos: Boolean, quinques: FloatArray): Float {
        val ka = aplique(x - LAMP_X, y - LAMP_Y, suenos)
        val kv = palmatoria(x - VELA_X, y - yVela, suenos)
        val kq = quinqueMayor(x, y, quinques, suenos)
        return (1f - (1f - ka) * (1f - kv) * (1f - kq)).coerceIn(0.03f, 1f)
    }

    /** La luz fría del escaparate en ([x], [y]), en anchos: el café o la luna, arriba a la izquierda. */
    fun fria(x: Float, y: Float, suenos: Boolean): Float {
        val dx = x + 0.06f; val dy = y - (if (suenos) 0.3f else 0.22f)
        return ((if (suenos) 1.0f else 0.75f) * exp(-2.3f * sqrt(dx * dx * 0.8f + dy * dy * 0.9f))).coerceIn(0f, 1f)
    }

    /** Dónde arde la palmatoria, a lo alto y en anchos, en un estante de [repisas] repisas (lo usa cada volumen). */
    fun yVelaNominal(repisas: Int): Float = CORONA + repisas * FILA / ANCHO_NOMINAL + BASE * VELA_Y

    /**
     * En qué repisas va el quinqué y en qué x del lienzo; -1 donde no va. El primero, en la repisa de más sitio libre; si el
     * estante tiene cuatro repisas o más, otro en la de más sitio de las que quedan a dos repisas o más de la primera (así
     * alumbran tramos distintos). La usan el hueco (para dibujarlo) y el estante (para alumbrar con él).
     */
    fun quinques(d: DisposicionEstante, w: Float): FloatArray {
        val n = d.repisas.size
        val res = FloatArray(n) { -1f }
        if (n == 0) return res
        val xDer = d.derecha.left
        fun libre(k: Int) = xDer - (d.ocupado.getOrNull(k) ?: xDer)
        val minimo = w * 0.17f
        var primera = -1; var m = 0f
        for (k in 0 until n) if (libre(k) > m) { m = libre(k); primera = k }
        if (primera < 0 || m < minimo) return res
        res[primera] = (d.ocupado.getOrNull(primera) ?: return res) + minOf(m * 0.3f, w * 0.085f)
        if (n >= 4) {
            var segunda = -1; var m2 = 0f
            for (k in 0 until n) if (kotlin.math.abs(k - primera) >= 2 && libre(k) > m2) { m2 = libre(k); segunda = k }
            if (segunda >= 0 && m2 >= minimo) res[segunda] = (d.ocupado.getOrNull(segunda) ?: return res) + minOf(m2 * 0.3f, w * 0.085f)
        }
        return res
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 2f || h < 2f) return
        val a = AireNouveau(oscuro, noche)
        val ac = AireNouveau(oscuro, noche, cuerpo = true)
        val ch = d.corona.bottom
        val b = d.base
        val apoyo = ch - w * CORNISA
        val yV = (b.top + b.height * VELA_Y) / w
        // Los quinqués de los huecos: se apuntan para que cada volumen sepa cuánta luz le dan.
        val xq = quinques(d, w)
        val centros = FloatArray(d.repisas.size) { -1f }
        val luces = ArrayList<Float>()
        val inter = d.derecha.left - d.izquierda.right
        d.repisas.forEachIndexed { k, r ->
            if (xq[k] < 0f) return@forEachIndexed
            centros[k] = ((xq[k] - d.izquierda.right) / maxOf(1f, inter)).coerceIn(0f, 1f)
            luces.add(xq[k] / w); luces.add((r.bottom - r.height * QUINQUE_Y) / w)
        }
        val quinq = if (a.lumbre) luces.toFloatArray() else FloatArray(0)
        LucesHuecoNouveau.poner(LucesHuecoNouveau.clave(d.repisas.size, oscuro, noche), centros)
        val cerca: (Offset) -> Float = { p ->
            if (!a.lumbre) 1f
            else if (p.y < ch) apliqueArriba(p.x / w - LAMP_X, p.y / w - LAMP_Y, noche)
            else calor(p.x / w, p.y / w, yV, noche, quinq)
        }
        val clave = "${w.toInt()}:${ch}:${b.top}:${h}:$oscuro:$noche:$density"

        // Lo de arriba: la cenefa y la moldura en cada dibujo (son bandas baratas) y, desde la fila entera de la moldura, la pared,
        // el bodegón y la cornisa, en una imagen que llega al píxel entero de debajo de la corona.
        val arribaCor = floor(w * MuroEstanteNouveau.MOLDURA).toInt()
        with(MuroEstanteNouveau) { cenefaArriba(a, apoyo, arribaCor) }
        with(CapaNouveau) {
            pegar(0, "c:$clave", arribaCor, ceil(ch).toInt()) {
                drawRect(a.luz(P.CAOBA_OSC, 0.5f), Offset.Zero, Size(w, ceil(ch) + 1f))
                with(MuroEstanteNouveau) { pared(a, apoyo, cerca) }
                with(CoronaEstanteNouveau) { colgado(a, apoyo, cerca) }
                with(ObraEstanteNouveau) { cornisa(Rect(0f, apoyo, w, ch + 2f), a, cerca) }
                with(CoronaEstanteNouveau) { encima(a, apoyo, cerca) }
                with(MuroEstanteNouveau) { luzDeNoche(a, ch) }
                granoMueble(Rect(0f, 0f, w, ch + 2f), oscuro)
            }
        }
        // El cuerpo: los casilleros, los montantes y las tablas.
        with(ObraEstanteNouveau) {
            d.repisas.forEachIndexed { k, r -> casillero(k, r, d, ac) }
        }
        with(HuecosEstanteNouveau) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, d.derecha.left, xq[k], ac, cerca)
            }
        }
        with(ObraEstanteNouveau) {
            d.repisas.forEachIndexed { k, r -> enjutas(r, d, ac) }
            d.tablas.forEachIndexed { k, r -> tabla(k, r, ac) }
            montante(Rect(d.izquierda.left, ch, d.izquierda.right, b.top), true, ac)
            montante(Rect(d.derecha.left, ch, d.derecha.right, b.top), false, ac)
        }
        // La moldura y la cara del zócalo en cada dibujo, desde el píxel entero de encima de la última tabla o de la base (la que
        // acabe antes) hasta la fila entera de debajo de la cara, donde empieza la imagen guardada del pie.
        val junta = floor(minOf(b.top, d.tablas.lastOrNull()?.bottom ?: b.top))
        val arriba = floor(b.top + w * PieEstanteNouveau.CARA).toInt().coerceAtLeast(junta.toInt() + 1)
        with(PieEstanteNouveau) { zocaloArriba(ac, b, junta, arriba + 1f) }
        // La luz del cuerpo; de noche también en el zócalo, con la misma luz que la imagen del pie.
        with(ObraEstanteNouveau) { luzCuerpo(ch, if (a.lumbre) arriba + 1f else b.top, ac, yV, quinq) }
        with(MuroEstanteNouveau) { panoEnElCuerpo(a, d, ch, b.top) }
        with(HuecosEstanteNouveau) { d.repisas.forEachIndexed { k, r -> llamaHueco(r, d, xq[k], ac) } }
        with(ObraEstanteNouveau) { d.placas.forEach { p -> placa(p, medidor, ac, cerca(p.r.center), (d.derecha.left - d.izquierda.right) * 0.86f) } }
        granoMueble(Rect(0f, ch, w, arriba + 1f), oscuro, 0.8f)
        // Lo de abajo: desde la fila entera de debajo de la cara del zócalo, el faldón, el suelo y lo que hay en él, en una imagen
        // con debajo un fondo opaco del color de la caoba.
        with(CapaNouveau) {
            pegar(1, "p:$clave:$arriba:${quinq.joinToString(",")}", arriba, ceil(h).toInt()) {
                drawRect(ac.luz(P.CAOBA_OSC, cerca(Offset(w / 2, b.top))), Offset(0f, arriba.toFloat()), Size(w, ceil(h) - arriba + 1f))
                val cercaPie: (Offset) -> Float = { p -> if (!a.lumbre) 1f else calor(p.x / w, p.y / w, yV, noche, quinq) }
                with(PieEstanteNouveau) { pie(ac, Rect(0f, arriba.toFloat(), w, h), b, cercaPie, quinq) }
                granoMueble(Rect(0f, arriba.toFloat(), w, h), oscuro)
            }
        }
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) {
        if (size.width < 2f || size.height < 2f) return
        val a = AireNouveau(oscuro, v.noche, cuerpo = true)
        val fx = LADO + v.centro * (1f - 2 * LADO)
        val fy = CORONA + (v.repisa + 0.5f) * FILA / ANCHO_NOMINAL
        val quinq = if (a.lumbre) LucesHuecoNouveau.luces(v.repisas, oscuro, v.noche) else FloatArray(0)
        val yVn = yVelaNominal(v.repisas)
        val k = if (a.lumbre) calor(fx, fy, yVn, v.noche, quinq) else 1f
        val f = if (a.oscuro) fria(fx, fy, v.noche) else 0f
        // De qué lado le llega la luz: de día, del escaparate (izquierda); de noche, de la luz que más alumbra.
        val ka = aplique(fx - LAMP_X, fy - LAMP_Y, v.noche)
        val kv = palmatoria(fx - VELA_X, fy - yVn, v.noche)
        var kq = 0f; var xqq = fx
        var i = 0
        while (i + 1 < quinq.size) { val e = quinque(fx - quinq[i], fy - quinq[i + 1], v.noche); if (e > kq) { kq = e; xqq = quinq[i] }; i += 2 }
        val hacia = when {
            !a.oscuro -> -1f
            kq >= ka && kq >= kv -> if (xqq >= fx) 1f else -1f
            ka >= kv -> if (fx < LAMP_X) 1f else -1f
            else -> if (fx < VELA_X) 1f else -1f
        }
        val kd = if (a.oscuro) maxOf(k, 0.07f) else k
        val tinte = tonoDeDiaNouveau(v.tono, oscuro)
        val c = clase(v.semilla)
        with(VolumenEstanteNouveau) {
            when (c) {
                0 -> tomo(v, medidor, a, kd, f, hacia, tinte)
                1 -> rollo(v, medidor, a, kd, f, hacia, tinte)
                else -> legajo(v, medidor, a, kd, f, hacia, tinte)
            }
        }
        // En los Sueños, el paño del escaparate (el ocaso, la luna) que entra en las repisas de arriba pinta también su frente.
        val q = MuroEstanteNouveau.panoCuerpo(a) ?: return
        val wN = ANCHO_NOMINAL * density
        val x0 = fx - size.width / wN / 2
        val y0 = CORONA + (v.repisa * FILA + ALTO_REPISA.value) / ANCHO_NOMINAL - size.height / wN
        if (x0 > maxOf(q[2], q[4]) || y0 > maxOf(q[5], q[7]) || y0 + size.height / wN < minOf(q[1], q[3])) return
        val arriba = size.height * (if (c == 0) 0.02f else if (c == 1) 0.06f else 0.08f)
        clipRect(0f, arriba, size.width, size.height) {
            translate(-x0 * wN, -y0 * wN) { with(MuroEstanteNouveau) { panoSobre(a, q, wN, null, -0.03f, -0.02f, if (a.oscuro) 0.72f else 0.42f, 3) } }
        }
    }
}

/**
 * Dónde arden los quinqués de los huecos, apuntado por el estante al dibujarse para que cada volumen (que se dibuja después, en
 * su propio lienzo, y no sabe dónde acaba su repisa) sepa cuánta luz le dan. Por repisa, el centro del quinqué en la misma
 * medida que [Volumen.centro], o -1 si esa repisa no tiene. La clave es el número de repisas y el momento.
 */
internal object LucesHuecoNouveau {
    @Volatile private var clave = ""
    @Volatile private var centros = FloatArray(0)

    fun clave(repisas: Int, oscuro: Boolean, noche: Boolean) = "$repisas:$oscuro:$noche"

    fun poner(clave: String, c: FloatArray) { centros = c; this.clave = clave }

    /** Los quinqués de un estante de [repisas] repisas, en anchos (x, y, x, y…), o ninguno si no se apuntaron. */
    fun luces(repisas: Int, oscuro: Boolean, noche: Boolean): FloatArray {
        val c = centros
        if (clave != clave(repisas, oscuro, noche)) return FloatArray(0)
        val l = ArrayList<Float>()
        val m = MuebleEstanteNouveau
        for (k in c.indices) {
            if (c[k] < 0f) continue
            l.add(m.LADO + c[k] * (1f - 2 * m.LADO))
            l.add(m.CORONA + k * m.FILA / m.ANCHO_NOMINAL + (m.ALTO_REPISA.value * (1f - m.QUINQUE_Y)) / m.ANCHO_NOMINAL)
        }
        return l.toFloatArray()
    }
}
