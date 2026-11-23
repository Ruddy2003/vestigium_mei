package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
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
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.sqrt

/**
 * La paleta del estante de Mongol: el lado de la dueña de la ger de un pastor hacia 1220. Lo cálido: el alerce ahumado de la
 * alacena, el bermellón y el oro de sus cantos pintados, el cobre del caldero que vino del oeste, el fieltro crudo, el sol de
 * la puerta y el fuego del hogar. Lo frío: el tapiz de fieltro teñido de añil, el hierro, la corteza de abedul y la luna del
 * toono.
 */
internal object PaletaMongol {
    /** El tapiz de fieltro de añil de la pared: su cara, su luz y lo hondo. */
    val ANIL = Color(0xFF2C4374)
    val ANIL_LUZ = Color(0xFF4D6A9E)
    val ANIL_OSC = Color(0xFF172443)
    /** El bermellón de la orla y de los cantos pintados, y su hondo. */
    val BERMELLON = Color(0xFFB43A26)
    val BERMELLON_OSC = Color(0xFF6C2015)
    /** El oro de los filetes pintados. */
    val ORO = Color(0xFFD9A443)
    val ORO_LUZ = Color(0xFFF4D488)
    /** El fieltro crudo (crema), el del suelo (gris pardo) y el de la cubierta del techo. */
    val CREMA = Color(0xFFE9DCBD)
    val FIELTRO_GRIS = Color(0xFFAE9F84)
    val TECHO = Color(0xFFC9BC9F)
    /** Los listones del khana y las varas del techo (los mismos que los del Ideario). */
    val KHANA = Color(0xFFB45A2E)
    /** El alerce de la alacena: su cara, su luz, lo hondo y el fondo ahumado de los casilleros. */
    val ALERCE = Color(0xFF94603A)
    val ALERCE_LUZ = Color(0xFFC8925E)
    val ALERCE_OSC = Color(0xFF4E2D19)
    val HOLLIN = Color(0xFF2A1A13)
    val HOLLIN_LUZ = Color(0xFF543722)
    /** El hierro forjado de las escuadras y el hogar, y su brillo. */
    val HIERRO = Color(0xFF38323A)
    val HIERRO_LUZ = Color(0xFF9A949C)
    /** El cobre martillado del caldero: su cara, su brillo y lo hondo. */
    val COBRE = Color(0xFFC0703C)
    val COBRE_LUZ = Color(0xFFF7C28C)
    val COBRE_OSC = Color(0xFF6A2D16)
    /** El bronce de la lámpara colgada (que también vino del oeste): su cara, su brillo y lo hondo. */
    val BRONCE = Color(0xFFAE8240)
    val BRONCE_LUZ = Color(0xFFF0CB7E)
    val BRONCE_OSC = Color(0xFF5C3D1B)
    /** La corteza de abedul vieja y tostada de las tablillas, su canto quemado y sus lenticelas. */
    val ABEDUL = Color(0xFFC9B28A)
    val ABEDUL_OSC = Color(0xFF8C7150)
    val LENTICELA = Color(0xFF3B2C24)
    /** El cuero curtido, el cuero crudo, la crin torcida y el mimbre. */
    val CUERO = Color(0xFF79472A)
    val CRUDO = Color(0xFFC7A174)
    val CRIN = Color(0xFF2E2521)
    val MIMBRE = Color(0xFFB88C53)
    /** La cuajada seca, el hueso, la carne seca y la boñiga seca del fuego. */
    val CUAJADA = Color(0xFFF2ECDA)
    val HUESO = Color(0xFFE6DCC3)
    val CARNE = Color(0xFF6E2A1E)
    val BONIGA = Color(0xFF6B5641)
    /** La tinta de hollín de las tablillas. */
    val TINTA = Color(0xFF22160F)
    /** El sol de la tarde que entra por la puerta, la llama, el fuego, la brasa y la luna. */
    val SOL = Color(0xFFFFD27C)
    val LLAMA = Color(0xFFFFB24C)
    val FUEGO = Color(0xFFEA6A2E)
    val BRASA = Color(0xFFFF5B22)
    val LUNA = Color(0xFFDDE6F6)
    /** El aire de la noche (pardo rojizo hondo, como en el Ideario), el de los Sueños, el anochecer y su crepúsculo. */
    val NOCHE = Color(0xFF1E0F0C)
    val NOCHE_SUENOS = Color(0xFF111226)
    val ANOCHECER = Color(0xFF3F2E5E)
    val CREPUSCULO = Color(0xFFEE9468)

    /**
     * Los fieltros teñidos del ánimo, de lo malo a lo bueno, en el orden de la escala del tema (rojo, crudo, azul): la rubia
     * honda, el rojo de óxido, el fieltro crudo, el azul de glasto y el añil. El fieltro se teñía justo así; nada queda en pastel.
     */
    val TINTES = listOf(0f to Color(0xFF9C2A1C), 0.25f to Color(0xFFBF5B30), 0.5f to Color(0xFFE3D3AE), 0.75f to Color(0xFF4F79A8), 1f to Color(0xFF1F4C8C))
    /** Lo que no tiene ánimo: fieltro pardo de lana sin teñir. */
    val SIN_ANIMO = Color(0xFF8C7457)

    val LETRA: FontFamily = LetraExtra.filosofo
}

/**
 * El aire del momento en el estante de Mongol: de día (el sol de la puerta y la luz del toono), de noche (el modo oscuro: arden
 * la lámpara y el hogar, y la luna roza lo alto) y en los Sueños ([noche]: el anochecer por la puerta en claro, con la lámpara
 * baja; en oscuro, el hogar en brasas y la luna). Con [en] se pide la luz de un sitio.
 */
internal class AireMongol(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaMongol
    /** Si arde el hogar (de noche llamas; en los Sueños, brasas). */
    val lumbre: Boolean get() = oscuro || noche
    /** Si el hogar está en llamas (el modo oscuro del diario) o solo en brasas. */
    val llamas: Boolean get() = oscuro && !noche
    /**
     * Cuánto arde la lámpara de bronce colgada (de 0 a 1): entera de noche en el diario, baja al anochecer de los Sueños en
     * claro y apagada de día y en los Sueños en oscuro (la casa duerme: solo quedan las brasas y la luna).
     */
    val lampara: Float get() = if (oscuro && !noche) 1f else if (noche && !oscuro) 0.45f else 0f
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE
    /** El color de la luz del hogar: amarillo en llamas, rojo de brasa en los Sueños. */
    val fuego: Color get() = if (llamas) p.LLAMA else mezcla(p.LLAMA, p.BRASA, 0.45f)

    /** La luz de un sitio: [k] del hogar, [f] de la luna; de día, [s] del sol de la puerta y [t] del toono. */
    fun en(k: Float, f: Float = 0f, s: Float = 0f, t: Float = 1f) = LuzMongol(this, k.coerceIn(0f, 1f), f.coerceIn(0f, 1f), s.coerceIn(0f, 1f), t.coerceIn(0f, 1f))

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.17f + n.red * 0.8f, c.green * 0.17f + n.green * 0.8f, c.blue * 0.17f + n.blue * 0.8f, c.alpha)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.45f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }
}

/**
 * La luz de una cosa en un sitio: su color con la luz que le llega ([c]), la sombra ([s]), el plano de luz ([b]: crema de día,
 * el color del fuego de noche), el filo del hogar en lo que lo mira ([filo]) y el roce frío de la luna ([frio]).
 */
internal class LuzMongol(val a: AireMongol, val k: Float, val f: Float, val sol: Float, val toono: Float) {
    private val p = PaletaMongol
    fun c(col: Color): Color = when {
        a.oscuro -> {
            val fuego = mezcla(mezcla(a.hundido(col), col, (1.05f * k).coerceAtMost(0.93f)), a.fuego, 0.2f * k)
            // La luna: en el diario, un roce que platea; en los Sueños deja ver el color propio, enfriado (no agrisado).
            if (f > 0.01f) { if (a.noche) mezcla(fuego, lunaFria(col), 0.62f * f) else mezcla(fuego, mezcla(col, p.LUNA, 0.3f), 0.5f * f) } else fuego
        }
        a.noche -> {
            // El anochecer: cada cosa conserva su color, más hondo hacia abajo y hacia la derecha, lejos del toono y de la puerta
            // (el violeta queda solo en las sombras); lo que alcanza la luz baja de la puerta queda rosado, y lo que alcanza la
            // lámpara, cálido.
            mezcla(mezcla(anochecer(col, (0.12f + 0.55f * (1f - toono) - 0.25f * sol).coerceIn(0.04f, 0.4f)), p.CREPUSCULO, 0.3f * sol), a.fuego, 0.34f * k)
        }
        else -> mezcla(mezcla(col, SOMBRA_MUEBLE, 0.2f * (1f - toono)), p.SOL, 0.16f * sol)
    }
    fun s(alfa: Float): Color = a.sombra(alfa)
    fun b(alfa: Float): Color = when {
        a.oscuro -> mezcla(a.fuego, p.LUNA, (f / (k + f + 0.001f)).coerceIn(0f, 1f)).copy(alpha = (alfa * 0.95f * maxOf(k, f * 0.8f)).coerceIn(0f, 1f))
        a.noche -> mezcla(luzMueble(1f), p.CREPUSCULO, 0.5f).copy(alpha = (alfa * (0.35f + 0.5f * maxOf(sol, k))).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.SOL, 0.5f * sol).copy(alpha = (alfa * (0.75f + 0.35f * sol)).coerceIn(0f, 1f))
    }
    fun filo(alfa: Float): Color = if (!a.lumbre || k < 0.03f) Color.Transparent
        else a.fuego.copy(alpha = (alfa * k * (if (a.oscuro) 1.15f else 0.7f)).coerceIn(0f, 0.9f))
    fun frio(alfa: Float): Color = if (!a.oscuro || f < 0.03f) Color.Transparent else p.LUNA.copy(alpha = (alfa * f).coerceIn(0f, 0.8f))

    /** Un color con la luz que se va: más hondo en [hondo], y algo más frío, sin echarle violeta. */
    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.95f), c.blue * (1f - hondo * 0.75f), c.alpha)

    /** Un color bajo la luna de los Sueños: el suyo, enfriado y un poco levantado, sin perder su saturación. */
    private fun lunaFria(c: Color): Color =
        Color((c.red * 0.86f + 0.03f).coerceAtMost(1f), (c.green * 0.93f + 0.05f).coerceAtMost(1f), (c.blue * 1.0f + 0.06f).coerceAtMost(1f), c.alpha)

    /**
     * El color del ánimo hecho fieltro teñido ([fieltroDelAnimo]), con la luz que le llega. De noche se apaga algo menos que lo
     * demás y conserva su color, para que en la repisa más lejana las entradas se sigan distinguiendo entre sí.
     */
    fun animo(t: Color?): Color {
        val base = if (t == null) p.SIN_ANIMO else fieltroDelAnimo(t)
        return when {
            a.oscuro -> {
                val q = maxOf(k, f * 0.55f)
                val fuego = mezcla(mezcla(a.hundido(base), base, (0.18f + 0.84f * q).coerceAtMost(0.96f)), a.fuego, 0.14f * k)
                if (f > 0.01f) { if (a.noche) mezcla(fuego, lunaFria(base), 0.45f * f) else mezcla(fuego, mezcla(base, p.LUNA, 0.25f), 0.35f * f) } else fuego
            }
            a.noche -> mezcla(mezcla(anochecer(base, (0.08f + 0.45f * (1f - toono) - 0.22f * sol).coerceIn(0.03f, 0.34f)), p.CREPUSCULO, 0.18f * sol), a.fuego, 0.22f * k)
            else -> mezcla(mezcla(base, SOMBRA_MUEBLE, 0.12f * (1f - toono)), p.SOL, 0.12f * sol)
        }
    }

    /**
     * La corteza donde va el día: un valor medio (no brilla más que el fieltro), teñida de noche por la luz que le toca, el fuego
     * o la luna; nunca tanto que el número deje de leerse (4.5:1 o más con la tinta).
     */
    fun etiqueta(): Color = when {
        a.oscuro -> mezcla(mezcla(mezcla(p.ABEDUL, a.hondo, (0.52f - 0.32f * maxOf(k, f)).coerceIn(0.2f, 0.52f)), a.fuego, 0.3f * k), p.LUNA, 0.16f * f)
        a.noche -> mezcla(p.ABEDUL, p.ANOCHECER, 0.16f - 0.08f * maxOf(sol, k))
        else -> mezcla(p.ABEDUL, p.SOL, 0.1f * sol)
    }
}

/**
 * El dibujo del estante del diario de Mongol (lo usa `EstanteMongol`, en `BibliotecaMedievo.kt`): la alacena de alerce del
 * lado de la dueña de la ger, el oriente, donde se guardan la leche y las cosas de la casa, hacia 1220. Tablas gruesas
 * ahumadas, costados con su chaflán y sus escuadras de hierro, cada canto pintado de bermellón con filete de oro, la cornisa
 * volada sobre un friso de añil y, abajo, un cuerpo cerrado de dos portezuelas pintadas. Contra el tapiz de fieltro de añil
 * colgado sobre el khana, bajo las varas pintadas del techo. Arriba, el bodegón de la cocina: el caldero de cobre martillado
 * que el pastor trajo del oeste, el balde de ordeñar, la tabla de cuajadas, el vellón con el huso; colgados, la carne seca, el
 * colador y la lámpara de aceite de bronce, que también vino del oeste. Al pie, la estera de fieltro acolchado, el cesto de la
 * boñiga y el hogar. De día manda el sol bajo de la puerta abierta (a la izquierda, fuera del cuadro), que deja su mancha de
 * borde neto abajo, y arriba la luz del toono; de noche arden la lámpara (arriba a la derecha) y el hogar (abajo a la
 * derecha), las repisas de en medio se hunden y la luna roza lo alto. En los Sueños en claro es el anochecer: el violeta
 * se hace hondo hacia abajo y a la derecha, la luz baja de la puerta queda rosada y la lámpara recién encendida es el acento;
 * en oscuro la lámpara está apagada, el hogar en brasas tiñe lo de abajo y manda el haz de luna, que baja del toono en
 * diagonal y de borde neto. La pared y el suelo están en [MuroEstanteMongol], la alacena en [AlacenaEstanteMongol], lo de arriba en
 * [CoronaEstanteMongol], lo que llena los huecos de las repisas en [HuecosEstanteMongol], el pie en [PieEstanteMongol] y las entradas en
 * `MuebleEstanteMongolVolumenes.kt` y `MuebleEstanteMongolCofres.kt`.
 */
internal object MuebleEstanteMongol {
    val LETRA: FontFamily = PaletaMongol.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteMongol` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.42f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 110.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    /**
     * Más ancho cuanto más escribiste: crece rápido en las entradas cortas (con la raíz de la cuenta) y se llena hacia las 130
     * palabras; unos dp de más o de menos según la cuenta misma, para que la repisa no sea una empalizada. El contrato no pasa la
     * semilla al ancho, así que cada clase se acomoda en el ancho que le toca: el rollo usa solo una parte, la bolsa se desborda
     * un poco sobre las vecinas y la pila de cofres lo llena.
     */
    fun ancho(palabras: Int): Dp {
        val crece = (2.6f * kotlin.math.sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(30f)
        return (22f + crece + Math.floorMod(palabras * 37 + 11, 7)).dp
    }

    /** Cuál de los tres es una entrada: 0, el rollo de fieltro; 1, la pila de cofres de mimbre forrados de fieltro; 2, la bolsa. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x5bd1, 77)
        return if (x < 0.45f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El rollo, el más alto; la pila de cofres, la más baja; todas más altas cuanto más escribiste, y con algo de azar. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.72f + 0.12f * lleno + 0.13f * azar(semilla, 1)
            1 -> 0.6f + 0.16f * lleno + 0.1f * azar(semilla, 1)
            else -> 0.62f + 0.12f * lleno + 0.12f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz

    /** El fuego del hogar: su x (en anchos) y su altura sobre el pie del lienzo (en anchos). */
    const val HOGAR_X = 0.8f
    const val HOGAR_Y = 0.09f
    /** La llama de la lámpara de bronce colgada (en el pico), en anchos desde la esquina de arriba a la izquierda. */
    const val LAMP_X = 0.735f
    const val LAMP_Y = 0.222f
    /** El toono (por donde entra la luna), en anchos: centrado y por encima del lienzo. */
    const val TOONO_X = 0.5f
    const val TOONO_Y = -0.35f
    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 130f
    /** La cornisa: lo que mide de alto, desde donde pisan las cosas hasta el arranque de las repisas, en anchos. */
    const val CORNISA = 0.115f
    /** En la base: el cuerpo cerrado de la alacena, en anchos desde su arranque. */
    const val CUERPO_BAJO = 0.19f

    /**
     * Cuánta luz del hogar (de 0 a 1) llega a un punto que está a [dx] de su x y a [alto] por encima de su pie (en anchos).
     * Llega encendida a lo bajo y se apaga hacia arriba; lo de la izquierda, un poco antes. Con [brasas] (los Sueños), menos.
     */
    fun hogar(dx: Float, alto: Float, brasas: Boolean): Float {
        val dy = (alto - HOGAR_Y).coerceAtLeast(0f)
        val d = sqrt(dx * dx * 1.3f + dy * dy * 0.9f)
        val f = if (brasas) 0.6f else 1f
        return (1.75f * f * exp(-2.45f * d)).coerceIn(0f, 1f)
    }

    /**
     * Cuánta luz de la lámpara (de 0 a 1) llega a un punto que está a [dx] y [dy] de su llama (en anchos): encendido cerca, a
     * medias en la repisa de arriba, tenue en la segunda y nada más abajo (ahí manda el hogar).
     */
    fun lampara(dx: Float, dy: Float): Float = (1.3f * exp(-2.7f * sqrt(dx * dx * 1.1f + dy * dy))).coerceIn(0f, 1f)

    /**
     * Cuánta luna (de 0 a 1) llega desde el toono a un punto a [x] (en anchos) y [y] por debajo del lienzo de arriba. En el
     * diario es un roce frío en lo alto (manda el hogar). En los Sueños hay un poco de luna en todo lo alto y, con [conHaz] (las
     * entradas, que el haz pintado no alcanza), la del haz si el punto cae dentro.
     */
    fun luna(x: Float, y: Float, suenos: Boolean, conHaz: Boolean = false): Float {
        val dx = kotlin.math.abs(x - TOONO_X)
        if (!suenos) return (0.2f * exp(-3f * (y - 0.2f).coerceAtLeast(0f)) * (1f - 2f * dx).coerceIn(0.1f, 1f)).coerceIn(0f, 1f)
        val fondo = 0.2f * exp(-1.4f * (y - 0.2f).coerceAtLeast(0f)) * (1f - 1.4f * dx).coerceIn(0.2f, 1f)
        // Las entradas están al canto, delante: el haz les cae donde cae en los frentes.
        val enHaz = if (conHaz) 0.6f * haz(x - HAZ_DELANTE, y) * exp(-0.45f * y.coerceAtLeast(0f)) else 0f
        return (fondo + enHaz).coerceIn(0f, 1f)
    }

    /** El haz de luna de los Sueños en oscuro: baja del toono en diagonal hacia la izquierda. Su centro a la altura [y] (en anchos). */
    fun centroHaz(y: Float): Float = HAZ_X - HAZ_CAE * y
    const val HAZ_X = 0.63f
    const val HAZ_CAE = 0.36f
    const val HAZ_MEDIO = 0.14f
    /** Lo que se corre el paño del haz en lo que está delante (los frentes) respecto de lo que está al fondo: ahí se quiebra. */
    const val HAZ_DELANTE = 0.035f

    /** Cuánto del haz (de 0 a 1) cae en un punto ([x], [y] en anchos): de borde neto. */
    fun haz(x: Float, y: Float): Float = ((HAZ_MEDIO - kotlin.math.abs(x - centroHaz(y))) / 0.012f).coerceIn(0f, 1f)

    /** La mancha del sol de la puerta, en anchos: dónde la cortan los dos lados del marco junto al pie del mueble. */
    const val SOL_X0 = 0.06f
    const val SOL_X1 = 0.5f

    /** Hasta qué altura sobre el suelo sube el sol por el frente del mueble a la x [x]: la sombra del dintel, que baja a la derecha. */
    fun dintel(x: Float): Float = 0.42f - 0.26f * (x - SOL_X0)

    /** Lo que se inclinan los lados del marco al subir por el mueble: [sobre] anchos por encima del suelo. */
    fun inclinaSol(sobre: Float): Float = 0.06f * (sobre / 0.42f)

    /**
     * Cuánto sol de la puerta (de 0 a 1) da en un punto a [x] y [alto] por encima del pie del lienzo (en anchos). Es la forma de la
     * puerta: en el suelo, una franja que se corre hacia delante; al llegar al mueble se quiebra y sube por su frente entre los
     * dos lados del marco, hasta la sombra del dintel. Borde neto.
     */
    fun sol(x: Float, alto: Float): Float {
        val junta = BASE - CUERPO_BAJO
        val sobre = alto - junta
        fun dentro(a0: Float, a1: Float) = ((x - a0) / 0.012f).coerceIn(0f, 1f) * ((a1 - x) / 0.012f).coerceIn(0f, 1f)
        if (sobre < 0f) {
            val t = (-sobre / junta).coerceIn(0f, 1f)
            return dentro(SOL_X0 + 0.16f * t, SOL_X1 + 0.28f * t)
        }
        val i = inclinaSol(sobre)
        return dentro(SOL_X0 + i, SOL_X1 + i) * ((dintel(x - i) - sobre) / 0.012f).coerceIn(0f, 1f)
    }

    /** La luz difusa del toono de día: 1 arriba, menos hacia abajo y hacia la derecha. */
    fun toono(x: Float, y: Float): Float = (1f - 0.16f * y - 0.12f * x).coerceIn(0.55f, 1f)

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [alto] anchos: la misma
     * para el mueble y para las entradas. Con [entrada] cuentan también el sol de la puerta y el haz de luna, que en el mueble
     * se pintan aparte con su forma de borde neto.
     */
    fun luz(a: AireMongol, x: Float, y: Float, alto: Float, entrada: Boolean = true): LuzMongol {
        val sobrePie = alto - y
        val kh = if (a.lumbre) hogar(x - HOGAR_X, sobrePie, !a.llamas) else 0f
        val kl = if (a.lampara > 0f) lampara(x - LAMP_X, y - LAMP_Y) * a.lampara else 0f
        val k = (kh + kl).coerceIn(0f, 1f)
        val f = if (a.oscuro) luna(x, y, a.noche, entrada) else 0f
        val s = if (!a.oscuro && entrada) sol(x, sobrePie) else 0f
        return a.en(k, f, s, toono(x, y))
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireMongol(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoMongol(w, h, ch, d.izquierda.right, d.derecha.left, fondo, d.base.top, n, a)

        // ---- lo de arriba (la pared, lo colgado, la cornisa y el bodegón) se pinta una vez por tamaño y modo y se pega. Debajo
        // va el arranque del cuerpo desde el píxel entero de arriba, así la fila de la junta (que puede caer a medio píxel) no
        // deja ver el fondo.
        with(AlacenaEstanteMongol) { cuerpo(g, d) }
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaMongol.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(MuroEstanteMongol) { pared(g) }
            with(CoronaEstanteMongol) { colgado(g) }
            with(AlacenaEstanteMongol) { cornisa(g) }
            with(CoronaEstanteMongol) { encima(g) }
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        with(HuecosEstanteMongol) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(AlacenaEstanteMongol) {
            d.tablas.forEachIndexed { k, r -> tabla(k, r, g) }
            d.placas.forEach { p -> placa(p, medidor, g) }
            costados(g, d)
        }
        with(MuroEstanteMongol) { suelo(g) }
        with(AlacenaEstanteMongol) {
            cuerpoBajo(g)
            // El sol de la puerta, de borde neto y con la forma de la puerta: en el suelo y, quebrado, en el frente del mueble (la
            // tabla de abajo, el cuerpo cerrado y el costado). El fondo de la última repisa queda en la sombra del costado: el sol
            // bajo de la izquierda no entra tan hondo; sí alumbra las entradas, que están al canto.
            val tope = d.tablas.last().top
            solPuerta(g, Rect(0f, tope, w, h))
            solPuerta(g, Rect(0f, ch, g.x0, tope))
        }
        with(PieEstanteMongol) { alPie(g) }
        // En los Sueños en oscuro, el haz de luna del toono: pinta lo que toca, de borde neto (por superposición, no un velo).
        if (oscuro && noche) hazLunaMongol(g, d)
        // El grano, en la madera y en lo de abajo (en el fondo oscuro de los casilleros no se vería: ahí lo pone el fieltro).
        granoMueble(Rect(0f, ch, g.x0, fondo), oscuro)
        granoMueble(Rect(g.x1, ch, w, fondo), oscuro)
        d.tablas.forEach { t -> granoMueble(Rect(g.x0, t.top, g.x1, t.bottom), oscuro) }
        granoMueble(Rect(0f, fondo, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenMongol(v, medidor, oscuro)
}

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]), el arranque de la base ([base]) y cuántas repisas
 * hay ([n]). Y la luz de cada punto ([luz]).
 */
internal class MarcoMongol(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val base: Float,
                           val n: Int, val a: AireMongol) {
    /** Donde pisa lo de arriba: la cara de la cornisa. */
    val apoyo: Float get() = ch - w * MuebleEstanteMongol.CORNISA
    /** Donde acaba el cuerpo cerrado de abajo y empieza el suelo. */
    val suelo: Float get() = base + w * MuebleEstanteMongol.CUERPO_BAJO
    /** La luz de un punto en píxeles. */
    fun luz(p: Offset): LuzMongol = MuebleEstanteMongol.luz(a, p.x / w, p.y / w, h / w, entrada = false)
    fun luz(x: Float, y: Float): LuzMongol = MuebleEstanteMongol.luz(a, x / w, y / w, h / w, entrada = false)
    /**
     * La luz de algo grande y lejano a la lámpara y al hogar (el tapiz, el techo, el khana): les llega [atenua] de la luz de un
     * punto; lo de cerca lo ponen los halos. Así de noche se quedan hondos y con su color.
     */
    fun luz(p: Offset, atenua: Float): LuzMongol { val l = luz(p); return a.en(l.k * atenua, l.f * (0.4f + 0.6f * atenua), l.sol, l.toono) }
    /** Dónde arde la lámpara colgada, en píxeles. */
    val lampara: Offset get() = Offset(w * MuebleEstanteMongol.LAMP_X, w * MuebleEstanteMongol.LAMP_Y)
    /** Dónde está el fuego, en píxeles. */
    val hogar: Offset get() = Offset(w * MuebleEstanteMongol.HOGAR_X, h - w * MuebleEstanteMongol.HOGAR_Y)
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser
 * medio píxel, deja ver lo que se pintó debajo (el arranque del cuerpo), no el fondo.
 */
internal object CapaMongol {
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

/**
 * La mancha del sol que entra por la puerta abierta (a la izquierda, fuera del cuadro), en el frente del mueble y en el suelo:
 * arriba la corta la sombra del dintel, que baja hacia la derecha, y a la derecha la del marco; abajo se abre por el suelo.
 */
internal fun poligonoSolMongol(g: MarcoMongol): Path {
    val m = MuebleEstanteMongol
    val w = g.w; val h = g.h
    val j = g.suelo
    val x0 = m.SOL_X0; val x1 = m.SOL_X1
    val i0 = m.inclinaSol(m.dintel(x0)); val i1 = m.inclinaSol(m.dintel(x1))
    return Path().apply {
        // En el suelo: la franja que se corre hacia delante.
        addPath(poligonoMongol(w * x0, j, w * x1, j, w * (x1 + 0.28f), h + 2f, w * (x0 + 0.16f), h + 2f))
        // En el frente del mueble: sube entre los dos lados del marco hasta la sombra del dintel (se solapa un píxel con el suelo).
        addPath(poligonoMongol(w * x0, j + 1f, w * x1, j + 1f, w * (x1 + i1), j - w * m.dintel(x1), w * (x0 + i0), j - w * m.dintel(x0)))
    }
}

/**
 * El haz de luna de los Sueños en oscuro, que baja del toono en diagonal. En el aire, apenas un rastro tenue de borde blando, solo
 * en lo alto. Donde cae, un paño frío de borde neto que sigue cada superficie y se quiebra en cada cambio de plano: el fondo (la
 * pared, el fondo de los casilleros) y los frentes (la cornisa, los cantos de las tablas, el cuerpo bajo), corridos entre sí;
 * en el suelo, que se tiende hacia delante, se abre más inclinado. Aclara y enfría lo que toca sin cubrirlo: un sobreexpuesto
 * frío (cada cosa conserva su color, más clara) y un levantado leve de lo oscuro hacia el azul de la luna.
 */
internal fun DrawScope.hazLunaMongol(g: MarcoMongol, d: DisposicionEstante) {
    val m = MuebleEstanteMongol
    val w = g.w; val h = g.h
    val medio = w * m.HAZ_MEDIO
    val delante = w * m.HAZ_DELANTE
    fun centro(y: Float) = w * m.centroHaz(y / w)
    // El rastro en el aire: tres capas cada vez más angostas que se apagan antes de la cornisa (el borde queda blando).
    val alto = g.apoyo * 0.8f
    listOf(1.3f, 1f, 0.7f).forEach { k ->
        val p = poligonoMongol(centro(0f) - medio * k, 0f, centro(0f) + medio * k, 0f, centro(alto) + medio * k, alto, centro(alto) - medio * k, alto)
        drawPath(p, Brush.verticalGradient(listOf(PaletaMongol.LUNA.copy(alpha = 0.035f), Color.Transparent), 0f, alto))
    }
    // El paño, por planos: (arriba, abajo, corrimiento, inclinación extra).
    val planos = ArrayList<FloatArray>()
    planos.add(floatArrayOf(0f, g.apoyo, 0f, 0f))
    planos.add(floatArrayOf(g.apoyo, g.ch, delante, 0f))
    d.repisas.forEachIndexed { k, r ->
        planos.add(floatArrayOf(r.top, r.bottom, 0f, 0f))
        d.tablas.getOrNull(k)?.let { t -> planos.add(floatArrayOf(t.top, t.bottom, delante, 0f)) }
    }
    planos.add(floatArrayOf(g.fondo, g.suelo, delante, 0f))
    planos.add(floatArrayOf(g.suelo, h, delante, 0.8f))
    val pano = Path()
    planos.forEach { (y0, y1, dx, extra) ->
        if (y1 <= y0) return@forEach
        val c0 = centro(y0) + dx
        val c1 = centro(y1) + dx - (y1 - y0) * m.HAZ_CAE * extra
        pano.addPath(poligonoMongol(c0 - medio, y0, c0 + medio, y0, c1 + medio, y1 + 0.5f, c1 - medio, y1 + 0.5f))
    }
    drawPath(pano, Color(0xFF38404D), blendMode = BlendMode.ColorDodge)
    drawPath(pano, Color(0xFF080A12), blendMode = BlendMode.Screen)
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoMongol(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** [p] corrido. */
internal fun corridoMongol(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** Lo que queda de [p] al quitarle la misma forma corrida: una media luna en el borde contrario al corrimiento. */
internal fun bordeMongol(p: Path, dx: Float, dy: Float) = Path().apply { op(p, corridoMongol(p, dx, dy), PathOperation.Difference) }

/** El brillo duro de lo que reluce (el cobre, el hierro, el sebo): casi blanco y cálido. */
internal fun luzDuraMongol(a: Float) = Color(0xFFFFF6E2).copy(alpha = a.coerceIn(0f, 1f))

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoMongol(centro: Offset, ancho: Float, alto: Float, l: LuzMongol, fuerza: Float = 0.36f, hacia: Float = 1f) =
    drawOval(l.s(if (l.a.oscuro) fuerza * 1.2f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de cabeza redonda visto de frente: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.clavoMongol(p: Offset, r: Float, l: LuzMongol, metal: Color = PaletaMongol.HIERRO) {
    drawCircle(l.s(0.4f), r * 1.05f, p + Offset(r * 0.45f, r * 0.5f))
    drawCircle(l.c(metal), r, p)
    drawCircle(l.b(0.6f), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** Las escalas del ánimo de Mongol, la de día y la de noche (bajo, medio, alto). */
private val TONOS_MONGOL: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.MONGOL, false) to juegoTonos(TemaId.MONGOL, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaMongol(t: Color, escala: List<Color>): Float? {
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
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el ánimo de en medio pasa del
 * fieltro crudo a un casi negro, pensado para la interfaz) y una entrada clara se volvía negra de noche. Aquí la entrada es un
 * objeto de la ger: conserva su color de día y el hogar lo apaga con la distancia. Se ubica el color en la escala de noche y se
 * toma el mismo punto en la de día; si no está en la escala, queda igual.
 */
internal fun tonoDeDiaMongol(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_MONGOL
    val x = enEscalaMongol(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El fieltro teñido que corresponde a un color del ánimo: se ubica el color en la escala de día del tema y se toma el mismo
 * punto en [PaletaMongol.TINTES]. Si el color no está en la escala, queda igual.
 */
internal fun fieltroDelAnimo(t: Color): Color {
    val x = enEscalaMongol(t, TONOS_MONGOL.first) ?: return t
    val tintes = PaletaMongol.TINTES
    for (i in 0 until tintes.size - 1) {
        val (f0, c0) = tintes[i]; val (f1, c1) = tintes[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return tintes.last().second
}

/**
 * Un rombo escalonado de radio [r] alrededor de [c]: el contorno de un rombo hecho en [n] escalones iguales por lado, con una
 * punta plana arriba, abajo y a los lados. Se lee como rombo (los escalones son chicos e iguales), no como cruz.
 */
internal fun romboEscalonadoMongol(c: Offset, r: Float, n: Int = 3): Path {
    val pasos = n.coerceAtLeast(1)
    val d = r / (pasos + 0.5f)
    return Path().apply {
        // El cuarto de arriba a la derecha y, por simetría, los otros tres.
        val cuarto = ArrayList<Offset>()
        cuarto.add(Offset(d / 2, -r))
        for (i in 1..pasos) {
            cuarto.add(Offset(d / 2 + (i - 1) * d, -r + i * d))
            cuarto.add(Offset(d / 2 + i * d, -r + i * d))
        }
        val todo = ArrayList<Offset>()
        todo.addAll(cuarto)
        cuarto.asReversed().forEach { o -> todo.add(Offset(o.x, -o.y)) }
        cuarto.forEach { o -> todo.add(Offset(-o.x, -o.y)) }
        cuarto.asReversed().forEach { o -> todo.add(Offset(-o.x, o.y)) }
        todo.forEachIndexed { i, o -> if (i == 0) moveTo(c.x + o.x, c.y + o.y) else lineTo(c.x + o.x, c.y + o.y) }
        close()
    }
}
