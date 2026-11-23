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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La paleta del estante de Prehistoria: la boca de una cueva habitada del suroeste de Europa, hace unos 17 000 años. Lo
 * cálido: el ocre rojo y el amarillo, el pino descortezado del andamio y su corteza rojiza, el cuero crudo de las ataduras, la
 * piel curtida, el hueso, el sol que entra por la boca y el fuego. Lo frío: la caliza de la pared a la luz del cielo (gris
 * azulada), el negro de manganeso, el sílex, la corteza de abedul, el enebro y la luna.
 */
internal object PaletaCueva {
    /** La caliza de la pared: su cara, al sol, a la sombra del cielo y honda (el fondo de los huecos). */
    val CALIZA = Color(0xFFC9B192)
    val CALIZA_SOL = Color(0xFFF8DDAE)
    val CALIZA_FRIA = Color(0xFF8E96A4)
    val CALIZA_HONDA = Color(0xFF5E5C66)
    /** El techo de la boca, ennegrecido por los fuegos, y el hollín. */
    val TECHO = Color(0xFF5A4C4A)
    val HOLLIN = Color(0xFF241B1C)
    /** Los pigmentos: ocre rojo, ocre amarillo, ocre anaranjado, negro de manganeso y carbón; el caolín (blanco). */
    val OCRE_ROJO = Color(0xFFAC3420)
    val OCRE_AMAR = Color(0xFFD49B34)
    val OCRE_NAR = Color(0xFFC96A2A)
    val MANGANESO = Color(0xFF2F2536)
    val CARBON = Color(0xFF211916)
    val CAOLIN = Color(0xFFF1EBDD)
    /** El pino descortezado de los postes y las varas: su cara, su luz y lo hondo; y su corteza rojiza. */
    val PINO = Color(0xFFBE9F76)
    val PINO_LUZ = Color(0xFFEAD2A8)
    val PINO_OSC = Color(0xFF6C4D33)
    val CORTEZA = Color(0xFF7E4630)
    val CORTEZA_OSC = Color(0xFF3F2218)
    /** El cuero crudo de las correas y el tendón: su cara y su hondo. */
    val CUERO = Color(0xFFDCBE88)
    val CUERO_OSC = Color(0xFF8E6B3C)
    /** La piel curtida (el lado de la carne), su hondo y el pelo. */
    val PIEL = Color(0xFFD2A874)
    val PIEL_OSC = Color(0xFF7C5434)
    val PELO = Color(0xFF5C4130)
    /** El hueso y el asta: su cara y su hondo. */
    val HUESO = Color(0xFFEFDDB8)
    val HUESO_OSC = Color(0xFFB09B76)
    /** La corteza de abedul y sus lenticelas. */
    val ABEDUL = Color(0xFFEEE8DB)
    val LENTICELA = Color(0xFF3B302D)
    /** El sílex, el enebro y la carne que se seca. */
    val SILEX = Color(0xFF5A6070)
    val ENEBRO = Color(0xFF4D6440)
    val CARNE = Color(0xFF7A2E22)
    /** La tierra pisada del suelo y la ceniza. */
    val TIERRA = Color(0xFF6E4D38)
    val CENIZA = Color(0xFFA8A39C)
    /** El sol de la boca, la luz del cielo en la sombra, la llama, el fuego, la brasa y la luna. */
    val SOL = Color(0xFFFFE0A0)
    val CIELO = Color(0xFF7C8CB0)
    val LLAMA = Color(0xFFFFC768)
    val FUEGO = Color(0xFFF2852F)
    val BRASA = Color(0xFFE8491C)
    val LUNA = Color(0xFFDCE6F6)
    /** El aire de la noche (pardo violáceo de manganeso), el de los Sueños, el anochecer y su crepúsculo. */
    val NOCHE = Color(0xFF1E1016)
    val NOCHE_SUENOS = Color(0xFF17142E)
    val ANOCHECER = Color(0xFF3E2C55)
    val CREPUSCULO = Color(0xFFF2946A)

    /**
     * Los pigmentos del ánimo, de lo malo a lo bueno, en el orden de la escala del tema (del carbón al ocre rojo): el negro de
     * manganeso, el pardo de hematites, el ocre amarillo, el anaranjado y el rojo. Los de la cueva; nada queda en pastel.
     */
    val PIGMENTOS = listOf(0f to Color(0xFF3A2C42), 0.25f to Color(0xFF7A3B2E), 0.5f to Color(0xFFD69C30), 0.75f to Color(0xFFCB6526), 1f to Color(0xFFB0301E))

    val LETRA: FontFamily = LetraExtra.abrigo
}

/**
 * El aire del momento en el estante de Prehistoria: de día (el sol de la boca), de noche (el modo oscuro: arden la lámpara de
 * grasa, la tea y el hogar) y en los Sueños ([noche]: el ocaso que entra bajo por la boca, en claro; la luna, en oscuro).
 */
internal class AireCueva(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaCueva
    /** Cuánto arde la lámpara de grasa de arriba (de 0 a 1). */
    val lampara: Float get() = if (oscuro && !noche) 1f else if (noche && !oscuro) 0.5f else if (noche) 0.3f else 0f
    /** Cuánto arde la tea del poste. */
    val tea: Float get() = if (oscuro && !noche) 1f else 0f
    /** Cuánto arde el hogar: de noche en llamas; en los Sueños, en brasas. */
    val hogar: Float get() = if (oscuro && !noche) 1f else if (noche) (if (oscuro) 0.62f else 0.3f) else 0f
    /** Si el hogar está en llamas (o solo en brasas). */
    val llamas: Boolean get() = !noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    fun en(k: Float, tinte: Color, sol: Float, luna: Float, amb: Float) =
        LuzCueva(this, k.coerceIn(0f, 1f), tinte, sol.coerceIn(0f, 1f), luna.coerceIn(0f, 1f), amb.coerceIn(0f, 1f))

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.15f + n.red * 0.85f, c.green * 0.15f + n.green * 0.85f, c.blue * 0.15f + n.blue * 0.85f, c.alpha)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.5f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> mezcla(SOMBRA_MUEBLE, Color(0xFF3B3550), 0.4f).copy(alpha = a.coerceIn(0f, 1f))
    }
}

/**
 * La luz de una cosa en un sitio: [k], cuánto le llega del fuego (la lámpara, la tea o el hogar) y de qué color ([tinte]); [sol],
 * del sol de la boca (de día) o del ocaso (en los Sueños en claro); [luna], de la luna de los Sueños; [amb], de la luz del cielo
 * que entra por la boca y se apaga hacia dentro. [c] da el color de algo con esa luz, [s] la sombra, [b] el plano de luz,
 * [filo] el borde encendido por el fuego y [frio] el de la luna.
 */
internal class LuzCueva(val a: AireCueva, val k: Float, val tinte: Color, val sol: Float, val luna: Float, val amb: Float) {
    private val p = PaletaCueva
    /** La misma luz con la mancha de la boca forzada a [v]: el sol o el ocaso de día y en los Sueños en claro; la luna en oscuro. */
    /** La misma luz con el fuego atenuado a [f] (para lo grande y lejano, como la pared: lo de cerca lo ponen los halos). */
    fun atenuada(f: Float): LuzCueva = LuzCueva(a, k * f, tinte, sol, luna, amb)
    fun conMancha(v: Float): LuzCueva = when {
        a.oscuro && a.noche -> LuzCueva(a, k, tinte, sol, v.coerceIn(0f, 1f) * 0.7f, amb)
        !a.oscuro -> LuzCueva(a, k, tinte, v.coerceIn(0f, 1f), luna, amb)
        else -> this
    }
    /** Cuánto enciende el fuego lo que toca: poco lejos y de golpe cerca (la mezcla de color va en Oklab, que ya aclara). */
    private val q: Float get() = (1.1f * k * sqrt(k)).coerceAtMost(0.94f)

    fun c(col: Color): Color = when {
        a.oscuro -> {
            val fuego = mezcla(mezcla(a.hundido(col), col, q), tinte, 0.26f * k)
            if (luna > 0.01f) mezcla(fuego, lunaFria(col), 0.62f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(col, (0.2f + 0.5f * (1f - amb) - 0.45f * sol).coerceIn(0.04f, 0.56f)), alOcaso(col), sol), tinte, 0.3f * k)
        else -> {
            // A la sombra, la luz del cielo: más oscuro y apenas más frío, sin agrisar; al sol, más claro y más cálido.
            val f = 0.86f - 0.26f * (1f - amb)
            val sombra = Color(col.red * f, col.green * f, (col.blue * (f + 0.03f)).coerceAtMost(1f), col.alpha)
            if (sol <= 0f) sombra else mezcla(sombra, alSol(col), sol)
        }
    }

    /** Un color bajo el sol bajo del ocaso: dorado y rosado, más saturado, sin aclarar de más. */
    private fun alOcaso(c: Color): Color = mezcla(Color((c.red * 1.06f + 0.06f).coerceAtMost(1f), (c.green * 0.92f + 0.02f).coerceAtMost(1f), c.blue * 0.78f, c.alpha), p.CREPUSCULO, 0.3f)

    /** Un color bajo el sol de la boca: más claro, más cálido y más saturado. */
    private fun alSol(c: Color): Color = mezcla(Color((c.red * 1.1f + 0.05f).coerceAtMost(1f), (c.green * 1.05f + 0.03f).coerceAtMost(1f), c.blue * 0.9f, c.alpha), p.SOL, 0.12f)

    /** La sombra: más franca donde da el sol (de borde más duro), más floja en la penumbra. */
    fun s(alfa: Float): Color = a.sombra(if (!a.oscuro) alfa * (1f + 0.5f * sol) else alfa * (1f + 0.4f * luna))
    fun b(alfa: Float): Color = when {
        a.oscuro -> mezcla(tinte, p.LUNA, (luna / (k + luna + 0.001f)).coerceIn(0f, 1f)).copy(alpha = (alfa * 0.95f * maxOf(k, luna * 0.85f)).coerceIn(0f, 1f))
        a.noche -> mezcla(luzMueble(1f), p.CREPUSCULO, 0.5f).copy(alpha = (alfa * (0.32f + 0.55f * maxOf(sol, k))).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.SOL, 0.5f * sol).copy(alpha = (alfa * (0.6f + 0.45f * sol) * (0.75f + 0.25f * amb)).coerceIn(0f, 1f))
    }
    fun filo(alfa: Float): Color = if (k < 0.03f || (!a.oscuro && !a.noche)) Color.Transparent
        else tinte.copy(alpha = (alfa * k * (if (a.oscuro) 1.2f else 0.75f)).coerceIn(0f, 0.92f))
    fun frio(alfa: Float): Color = if (!a.oscuro || luna < 0.03f) Color.Transparent else p.LUNA.copy(alpha = (alfa * luna).coerceIn(0f, 0.85f))

    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.95f), c.blue * (1f - hondo * 0.72f), c.alpha)

    private fun lunaFria(c: Color): Color =
        Color((c.red * 0.5f + 0.03f).coerceAtMost(1f), (c.green * 0.58f + 0.05f).coerceAtMost(1f), (c.blue * 0.7f + 0.12f).coerceAtMost(1f), c.alpha)

    /**
     * El pigmento del ánimo con la luz que le llega. De noche se apaga algo menos que lo demás y conserva su color, para que en la
     * repisa más lejana las entradas se sigan distinguiendo.
     */
    fun animo(base: Color): Color = when {
        a.oscuro -> {
            val qq = maxOf(q, luna * 0.6f)
            val fuego = mezcla(mezcla(a.hundido(base), base, (0.14f + 0.86f * qq).coerceAtMost(0.96f)), tinte, 0.14f * k)
            if (luna > 0.01f) mezcla(fuego, lunaFria(base), 0.5f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(base, (0.08f + 0.4f * (1f - amb) - 0.25f * sol).coerceIn(0.03f, 0.36f)), p.CREPUSCULO, 0.2f * sol), tinte, 0.22f * k)
        else -> {
            val f = 0.95f - 0.16f * (1f - amb)
            val sombra = Color(base.red * f, base.green * f, (base.blue * (f + 0.04f)).coerceAtMost(1f), base.alpha)
            if (sol <= 0f) sombra else mezcla(sombra, alSol(base), sol)
        }
    }

    /**
     * El cuero crudo de la etiqueta donde va el día: un valor medio (no brilla más que el objeto), teñido de noche por la luz que
     * le toca; nunca tanto que el número deje de leerse (4.5:1 o más con el carbón).
     */
    fun etiqueta(): Color = when {
        a.oscuro -> mezcla(mezcla(mezcla(ETIQUETA_CUEVA, a.hondo, (0.66f - 0.5f * maxOf(q, luna * 0.7f)).coerceIn(0.2f, 0.66f)), tinte, 0.34f * k), p.LUNA, 0.14f * luna)
        a.noche -> mezcla(mezcla(ETIQUETA_CUEVA, p.ANOCHECER, 0.2f - 0.12f * maxOf(sol, k)), p.CREPUSCULO, 0.14f * sol)
        else -> c(ETIQUETA_CUEVA)
    }
}

/** El cuero crudo raspado de las etiquetas: un tostado de valor medio. */
internal val ETIQUETA_CUEVA = Color(0xFFB98955)

/**
 * El dibujo del estante del diario de Prehistoria (lo usa `EstanteCueva`, en `BibliotecaAntiguedad.kt`): el andamio de varas que
 * el grupo levantó contra la pared de la boca de la cueva para guardar en seco lo que no debe mojarse. Dos postes de pino
 * descortezado con su horqueta, clavados en el suelo y acuñados con piedras; la viga de arriba descansa en las horquetas; cada
 * repisa es un zarzo de varas puestas de delante atrás sobre un travesaño, todo atado con correas de cuero crudo. Contra
 * la caliza de la boca, decorada con lavados y puntos de ocre, trazos de dedos y dos manos sopladas con manganeso. Arriba, el
 * bodegón de la casa: la piel de reno tensada en su bastidor, la lámpara de grasa, el cesto de corteza colmado de ocres, el rollo
 * de cuerda, el haz de azagayas y lo que se seca colgado de la viga. Al pie, el hogar de lajas de canto, el enlosado, el tocón del
 * tallador, los cantos de calentar y los haces de astillas y de enebro.
 *
 * De día manda el sol que entra por la boca (arriba a la izquierda, fuera del cuadro): una mancha de borde neto con la forma de
 * la boca, que en lo que está delante se corre hacia ella. De noche arden la lámpara de grasa (arriba a la izquierda), la tea
 * del poste derecho (a media altura) y el hogar (al pie), y entre ellas todo se hunde en un pardo violáceo. En los Sueños en
 * claro entra el ocaso, bajo y largo; en oscuro manda la luna que entra por la boca y el fuego arde bajo.
 *
 * La pared y el techo están en `MuebleEstanteCuevaPared.kt`, el andamio en `MuebleEstanteCuevaAndamio.kt`, lo de arriba en
 * `MuebleEstanteCuevaCorona.kt`, lo que llena los huecos en `MuebleEstanteCuevaHuecos.kt`, el pie en `MuebleEstanteCuevaPie.kt`
 * y las entradas en `MuebleEstanteCuevaVolumenes.kt`.
 */
internal object MuebleEstanteCueva {
    val LETRA: FontFamily = PaletaCueva.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteCueva` las lee al dibujar)

    val CORONA = 0.6f
    val BASE = 0.32f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    /**
     * Más ancho cuanto más escribiste: crece rápido en las entradas cortas (con la raíz de la cuenta) y se llena hacia las 140
     * palabras, con unos dp de más o de menos según la cuenta, para que la repisa no sea una empalizada. Cada clase se acomoda
     * en el ancho que le toca: el hueso usa una parte, la bolsa y la piel enrollada lo llenan.
     */
    fun ancho(palabras: Int): Dp {
        val crece = (2.3f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(26f)
        return (23f + crece + Math.floorMod(palabras * 31 + 7, 6)).dp
    }

    /** Cuál de los tres es una entrada: 0, el hueso de cuenta; 1, la bolsa de piel; 2, la piel enrollada. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x3c71, 53)
        return if (x < 0.4f) 0 else if (x < 0.72f) 1 else 2
    }

    /** El hueso, el más alto; la bolsa, la más baja; todas más altas cuanto más escribiste, y con algo de azar. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.8f + 0.06f * lleno + 0.1f * azar(semilla, 1)
            1 -> 0.72f + 0.1f * lleno + 0.08f * azar(semilla, 1)
            else -> 0.76f + 0.08f * lleno + 0.08f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz (todo en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** Donde pisan las cosas de arriba (la cara de la plataforma), en anchos desde arriba. */
    const val APOYO = 0.515f
    /** La viga de arriba, en las horquetas: su eje, en anchos desde arriba. */
    const val VIGA = 0.135f
    /** La llama de la lámpara de grasa de arriba, en anchos. */
    const val LAMP_X = 0.2f
    const val LAMP_Y = 0.452f
    /** La tea atada al poste derecho: su x, y su llama a qué fracción del cuerpo (de la corona al pie de la última repisa). */
    const val TEA_X = 0.957f
    const val TEA_T = 0.42f
    /** El fuego del hogar: su x y su altura sobre el pie del lienzo (en anchos). */
    const val HOGAR_X = 0.3f
    const val HOGAR_Y = 0.1f

    /** El alto del cuerpo (de la corona al pie de la última repisa) con [repisas] repisas, en anchos. */
    fun cuerpo(repisas: Int): Float = repisas * FILA / ANCHO_NOMINAL

    /** Dónde arde la tea, en anchos desde arriba, en un estante de [repisas] repisas. */
    fun teaY(repisas: Int): Float = CORONA + cuerpo(repisas) * TEA_T - 0.06f

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [repisas] repisas y [alto]
     * anchos. [delante]: cuánto está por delante de la pared (0, la pared; 0.03, los postes y los cantos; 0.05, las entradas),
     * para que la mancha del sol o de la luna se corra hacia la boca en lo que está delante.
     */
    fun luz(a: AireCueva, x: Float, y: Float, alto: Float, repisas: Int, delante: Float = 0f): LuzCueva {
        val sobrePie = alto - y
        // La lámpara de grasa: alumbra lo de arriba; poco a poco hacia la segunda repisa.
        val kl = if (a.lampara > 0f) a.lampara * (1.5f * exp(-4.1f * dist(x - LAMP_X, y - LAMP_Y))).coerceAtMost(1f) else 0f
        // La tea: un charco a media altura, más ancho a lo alto que a lo ancho.
        val kt = if (a.tea > 0f) a.tea * (1.45f * exp(-4.4f * dist((x - TEA_X) * 1.05f, (y - teaY(repisas)) * 0.85f))).coerceAtMost(1f) else 0f
        // El hogar: encendido abajo, se apaga hacia arriba.
        val dyH = (sobrePie - HOGAR_Y).coerceAtLeast(0f)
        val kh = if (a.hogar > 0f) a.hogar * (2f * exp(-3.3f * dist((x - HOGAR_X) * 1.1f, dyH))).coerceAtMost(1f) else 0f
        val k = (kl + kt + kh).coerceIn(0f, 1f)
        val tinte = if (k < 0.001f) PaletaCueva.LLAMA else mezcla(PaletaCueva.LLAMA, mezcla(PaletaCueva.FUEGO, PaletaCueva.BRASA, if (a.llamas) 0.2f else 0.6f), kh / (kl + kt + kh + 0.0001f))
        val sol = when {
            a.oscuro -> 0f
            a.noche -> ocasoCueva(x + delante * 0.95f, y + delante * 0.3f)
            else -> solCueva(x + delante * 0.8f, y + delante * 0.7f)
        }
        val luna = if (a.oscuro && a.noche) lunaCueva(x + delante * 0.6f, y + delante * 0.8f) else 0f
        val amb = (1f - 0.1f * y - 0.18f * x).coerceIn(0.55f, 1f)
        return a.en(k, tinte, sol, luna, amb)
    }

    private fun dist(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireCueva(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoCueva(w, h, ch, d.izquierda.right, d.derecha.left, fondo, d.base.top, n, a)

        // ---- debajo de la corona guardada, la fila de la junta (que puede caer a medio píxel): la pared y los postes desde el
        // píxel entero de arriba, así no asoma el fondo.
        with(AndamioEstanteCueva) { arranque(g) }
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaCueva.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(ParedEstanteCueva) { paredArriba(g) }
            with(CoronaEstanteCueva) { detras(g) }
            with(AndamioEstanteCueva) { arriba(g) }
            with(CoronaEstanteCueva) { delante(g) }
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        with(ParedEstanteCueva) { d.repisas.forEachIndexed { k, r -> fondoHueco(k, r, g) } }
        with(AndamioEstanteCueva) { zarzos(g, d) }
        with(HuecosEstanteCueva) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(PieEstanteCueva) { suelo(g) }
        with(AndamioEstanteCueva) {
            postes(g, d)
            d.tablas.forEachIndexed { k, r -> travesano(k, r, g) }
            d.placas.forEach { p -> placa(p, medidor, g) }
            tea(g)
        }
        with(PieEstanteCueva) { alPie(g) }
        // El grano, en el suelo (en el fondo hondo de los huecos no se vería; los postes y las varas ya tienen su veta).
        granoMueble(Rect(0f, g.suelo, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenCueva(v, medidor, oscuro)
}

/**
 * Cuánto sol de la boca (de 0 a 1) cae en un punto de la pared ([x], [y] en anchos): una mancha grande arriba a la izquierda con
 * el contorno quebrado de la boca, de borde neto.
 */
internal fun solCueva(x: Float, y: Float): Float = manchaCueva(x, y, 0.02f, 0.16f, 0.64f, 0.3f, 0.66f, 11)

/** El ocaso de los Sueños en claro: el sol bajo entra casi tendido por la boca y deja una mancha larga y estrecha, de borde neto. */
internal fun ocasoCueva(x: Float, y: Float): Float = manchaCueva(x, y, 0.26f, 0.43f, 0.58f, 0.12f, 0.28f, 31)

/** La luna de los Sueños en oscuro: una mancha fría, más chica que la del sol, que entra por la boca. */
internal fun lunaCueva(x: Float, y: Float): Float = 0.7f * manchaCueva(x, y, 0.16f, 0.34f, 0.5f, 0.15f, 0.78f, 23)

/**
 * Una mancha de luz con el contorno de la boca: una elipse de centro ([cx], [cy]), semiejes [ra] y [rb], girada [giro] radianes,
 * con el borde quebrado según [semilla]. Devuelve de 0 a 1, con el borde neto.
 */
internal fun manchaCueva(x: Float, y: Float, cx: Float, cy: Float, ra: Float, rb: Float, giro: Float, semilla: Int): Float {
    val dx = x - cx; val dy = y - cy
    val c = cos(giro); val s = sin(giro)
    val u = (dx * c + dy * s) / ra; val v = (-dx * s + dy * c) / rb
    val r = sqrt(u * u + v * v)
    val ang = kotlin.math.atan2(v, u)
    val quiebre = 1f + 0.06f * sin(5f * ang + semilla) + 0.035f * sin(11f * ang + semilla * 0.7f) + 0.02f * sin(23f * ang)
    return (((quiebre - r) * minOf(ra, rb)) / 0.008f).coerceIn(0f, 1f)
}

/** El contorno de una mancha de [manchaCueva] como camino, en píxeles (con [w], el ancho), corrido [dx], [dy] anchos. */
internal fun caminoManchaCueva(w: Float, cx: Float, cy: Float, ra: Float, rb: Float, giro: Float, semilla: Int, dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
    val c = cos(giro); val s = sin(giro)
    val pasos = 72
    for (i in 0 until pasos) {
        val ang = 6.2832f * i / pasos
        val quiebre = 1f + 0.06f * sin(5f * ang + semilla) + 0.035f * sin(11f * ang + semilla * 0.7f) + 0.02f * sin(23f * ang)
        val u = cos(ang) * quiebre * ra; val v = sin(ang) * quiebre * rb
        val x = cx + u * c - v * s + dx; val y = cy + u * s + v * c + dy
        if (i == 0) moveTo(x * w, y * w) else lineTo(x * w, y * w)
    }
    close()
}

/** El sol de la boca como camino (para pintar lo que toca), corrido hacia la boca [delante] anchos. */
internal fun caminoSolCueva(w: Float, delante: Float): Path = caminoManchaCueva(w, 0.02f, 0.16f, 0.64f, 0.3f, 0.66f, 11, -delante * 0.8f, -delante * 0.7f)

/** La luna de los Sueños como camino. */
internal fun caminoLunaCueva(w: Float, delante: Float): Path = caminoManchaCueva(w, 0.16f, 0.34f, 0.5f, 0.15f, 0.78f, 23, -delante * 0.6f, -delante * 0.8f)

/** El ocaso como camino. */
internal fun caminoOcasoCueva(w: Float, delante: Float): Path = caminoManchaCueva(w, 0.26f, 0.43f, 0.58f, 0.12f, 0.28f, 31, -delante * 0.95f, -delante * 0.3f)

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]), el arranque de la base ([base]) y cuántas repisas
 * hay ([n]). Y la luz de cada punto ([luz]).
 */
internal class MarcoCueva(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val base: Float,
                          val n: Int, val a: AireCueva) {
    private val m = MuebleEstanteCueva
    /** Donde pisa lo de arriba: la cara de la plataforma. */
    val apoyo: Float get() = w * m.APOYO
    /** El eje de la viga de arriba. */
    val viga: Float get() = w * m.VIGA
    /** Donde empieza el suelo: un poco por debajo de la última repisa. */
    val suelo: Float get() = fondo + w * 0.05f
    /** El eje de cada poste. */
    val posteI: Float get() = x0 * 0.52f
    val posteD: Float get() = w - (w - x1) * 0.52f
    /** El medio grueso de un poste. */
    val radioPoste: Float get() = x0 * 0.4f
    /**
     * La luz de un punto en píxeles, a [delante] anchos de la pared. Con [mancha] se fuerza cuánto le da la mancha de la boca (el
     * sol, el ocaso o la luna): 0 para pintar lo de fuera y 1 para lo de dentro, recortado a su borde neto.
     */
    fun luz(p: Offset, delante: Float = 0f, mancha: Float? = null): LuzCueva = luz(p.x, p.y, delante, mancha)
    fun luz(x: Float, y: Float, delante: Float = 0f, mancha: Float? = null): LuzCueva {
        val l = m.luz(a, x / w, y / w, h / w, n, delante)
        return if (mancha == null) l else l.conMancha(mancha)
    }
    /** Dónde arde la lámpara de arriba, la tea y el hogar, en píxeles. */
    val lampara: Offset get() = Offset(w * m.LAMP_X, w * m.LAMP_Y)
    val tea: Offset get() = Offset(w * m.TEA_X, w * m.teaY(n))
    val hogar: Offset get() = Offset(w * m.HOGAR_X, h - w * m.HOGAR_Y)
    /** La mancha de luz de la boca (sol, ocaso o luna, según el momento) para lo que está a [delante] anchos de la pared, o null. */
    fun mancha(delante: Float): Path? = when {
        !a.oscuro && !a.noche -> caminoSolCueva(w, delante)
        !a.oscuro && a.noche -> caminoOcasoCueva(w, delante)
        a.oscuro && a.noche -> caminoLunaCueva(w, delante)
        else -> null
    }
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser medio
 * píxel, deja ver lo que se pintó debajo (el arranque de la pared y los postes), no el fondo.
 */
internal object CapaCueva {
    private val claves = arrayOfNulls<String>(2)
    private val imagenes = arrayOfNulls<ImageBitmap>(2)

    fun pegar(ds: DrawScope, ranura: Int, clave: String, alto: Float, pintar: DrawScope.() -> Unit) {
        val w = ceil(ds.size.width).toInt()
        val h = ceil(alto).toInt().coerceAtMost(ceil(ds.size.height).toInt())
        if (w < 1 || h < 1 || ranura !in 0..1) return
        val img = imagenes[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(w, h).also { nueva ->
            CanvasDrawScope().draw(Density(ds.density, ds.fontScale), ds.layoutDirection, Canvas(nueva), Size(w.toFloat(), h.toFloat())) {
                clipRect(0f, 0f, ds.size.width, alto) { pintar() }
            }
            imagenes[ranura] = nueva; claves[ranura] = clave
        }
        ds.drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), dstOffset = IntOffset.Zero, dstSize = IntSize(img.width, img.height))
    }
}

/** Las escalas del ánimo de Prehistoria, la de día y la de noche (bajo, medio, alto). */
private val TONOS_CUEVA: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.CUEVA, false) to juegoTonos(TemaId.CUEVA, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaCueva(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val ex = t.red - a0.red - dr * f; val ey = t.green - a0.green - dg * f; val ez = t.blue - a0.blue - db * f
        val er = ex * ex + ey * ey + ez * ez
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (pensada para la interfaz) y una
 * entrada clara se volvía oscura de noche. Aquí la entrada es un objeto de la cueva: conserva su color de día y el fuego lo apaga
 * con la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaCueva(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_CUEVA
    val x = enEscalaCueva(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El pigmento de la cueva que corresponde a un color del ánimo: se ubica el color en la escala de día del tema y se toma el mismo
 * punto en [PaletaCueva.PIGMENTOS]. Si el color no está en la escala, queda igual.
 */
internal fun pigmentoDelAnimoCueva(t: Color): Color {
    val x = enEscalaCueva(t, TONOS_CUEVA.first) ?: return t
    val pig = PaletaCueva.PIGMENTOS
    for (i in 0 until pig.size - 1) {
        val (f0, c0) = pig[i]; val (f1, c1) = pig[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return pig.last().second
}
