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
import androidx.compose.ui.graphics.drawscope.clipPath
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
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * La paleta del estante de la Ciudad verde: el cuarto de lectura del último piso del edificio donde vive el campamento, bajo el
 * lucernario roto, cuarenta años después del silencio. Lo cálido: el roble de la librería de la biblioteca municipal, su latón,
 * el cartón de las etiquetas, el barro, el ocre de la capa vieja de pintura, el ladrillo, la vela y las brasas. Lo frío: el verde
 * agua del yeso, el esmalte azul, el vidrio, la luz verdosa que cuela el verdín de los vidrios y la luna. Y los verdes de lo que
 * crece: la hiedra, el musgo y el helecho.
 */
internal object PaletaRuinas {
    /** El yeso pintado de verde agua (pintura de oficina pública): su cara, al sol y en sombra; la capa ocre rosada de debajo y el ladrillo. */
    val YESO = Color(0xFF8FBBA8)
    val YESO_SOL = Color(0xFFE2EBC4)
    val YESO_SOMBRA = Color(0xFF557F74)
    val YESO_BLANCO = Color(0xFFE6DCC4)
    val OCRE_ROSA = Color(0xFFC99A6E)
    val LADRILLO = Color(0xFFA3533A)
    val LADRILLO_OSC = Color(0xFF6A3022)
    /** El roble de la librería: su cara, su luz, su sombra, lo hondo del fondo y el barniz comido por el agua. */
    val ROBLE = Color(0xFF8C5A30)
    val ROBLE_LUZ = Color(0xFFC48C52)
    val ROBLE_OSC = Color(0xFF4E2E18)
    val ROBLE_HONDO = Color(0xFF2E1B10)
    val GASTADO = Color(0xFFA8916E)
    /** El latón de los portaetiquetas y las bisagras, su sombra y el cardenillo. */
    val LATON = Color(0xFFCDA24C)
    val LATON_OSC = Color(0xFF7A5822)
    val CARDENILLO = Color(0xFF5FA08A)
    /** Los verdes de lo que crece. */
    val HIEDRA = Color(0xFF3B6A2A)
    val HIEDRA_LUZ = Color(0xFF74A443)
    val HIEDRA_OSC = Color(0xFF213F1A)
    val MUSGO = Color(0xFF6C8A2C)
    val MUSGO_LUZ = Color(0xFFA9BE4C)
    val HELECHO = Color(0xFF7DA84A)
    /** El cartón kraft de las etiquetas (un valor medio), el de la tarjeta del mes y el papel del herbario. */
    val KRAFT = Color(0xFFB58E58)
    val KRAFT_OSC = Color(0xFF7C5A32)
    /** El tejuelo sobre una tela clara: un kraft más hondo, para que contraste. */
    val KRAFT_TEJUELO_OSC = Color(0xFF9A6E3C)
    val CARTON = Color(0xFFCDB485)
    val PAPEL = Color(0xFFE8DFC8)
    /** El bramante, el corcho y el lápiz. */
    val BRAMANTE = Color(0xFFCDB07A)
    val CORCHO = Color(0xFFB98A56)
    val LAPIZ = Color(0xFF26231F)
    /** El hierro esmaltado de la máquina de escribir, sus teclas de hueso y el niquelado. */
    val HIERRO = Color(0xFF2B2D2A)
    val HIERRO_LUZ = Color(0xFF5E625C)
    val TECLA = Color(0xFFDCD2B6)
    val NIQUEL = Color(0xFFB9BDB6)
    /** El esmalte del cuenco y de la tetera (azul cobalto con su borde y el hierro donde saltó) y el blanco del esmalte. */
    val ESMALTE = Color(0xFF2F5E8E)
    val ESMALTE_LUZ = Color(0xFF6C98C4)
    val ESMALTE_BLANCO = Color(0xFFEAE4D2)
    /** El vidrio: verde de botella, el de los vidrios del lucernario y el claro. */
    val VIDRIO_VERDE = Color(0xFF3E7A4A)
    val VIDRIO_CLARO = Color(0xFFCFE0D4)
    /** El barro de las macetas y de la baldosa, el ocre de la baldosa, su gris verdoso y la tierra. */
    val BARRO = Color(0xFFB4603A)
    val BARRO_OSC = Color(0xFF6E3220)
    val OCRE = Color(0xFFCB9446)
    val BALDOSA_GRIS = Color(0xFF8E968A)
    val TIERRA = Color(0xFF5A4030)
    /** La luz: el sol, el sol verdoso que cuela el verdín, la llama, el fuego, la brasa y la luna. */
    val SOL = Color(0xFFFFE6A6)
    val SOL_VERDIN = Color(0xFFD6E8A0)
    val LLAMA = Color(0xFFFFC66C)
    val FUEGO = Color(0xFFF28C34)
    val BRASA = Color(0xFFE6531E)
    val LUNA = Color(0xFFD4E4F0)
    /** El aire de la noche (verde botella hondo), el de los Sueños, el anochecer y su crepúsculo. */
    val NOCHE = Color(0xFF0C1912)
    val NOCHE_SUENOS = Color(0xFF09161A)
    val ANOCHECER = Color(0xFF3B2E52)
    val CREPUSCULO = Color(0xFFF2A070)
    /** El melocotón del ocaso donde pinta. */
    val MELOCOTON = Color(0xFFF6C49C)

    /** El ánimo en tela de encuadernar (de lo malo a lo bueno): óxido, lino crudo, hiedra. */
    val TELAS = listOf(Color(0xFFB4381E), Color(0xFFDCC088), Color(0xFF2E7A2C))
    /** El ánimo en vidrio: ámbar oscuro, vidrio claro, vidrio verde. */
    val VIDRIOS = listOf(Color(0xFFA0500E), Color(0xFFA6DCCB), Color(0xFF237A44))
    /** El ánimo en lona encerada: herrumbre, lona cruda, verde de toldo. */
    val LONAS = listOf(Color(0xFFAC3E26), Color(0xFFC8B07A), Color(0xFF3E7236))

    val LETRA: FontFamily = LetraExtra.letrero
    val LETRA_MES: FontFamily = LetraExtra.overpass
}

/**
 * El aire del momento en el estante de la Ciudad verde: de día (el sol del lucernario), de noche (el modo oscuro: arden el farol
 * de tarro, el farolillo de lata y el anafre) y en los Sueños ([noche]: el ocaso que entra bajo por el lucernario, en claro; la
 * luna, en oscuro).
 */
internal class AireRuinas(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaRuinas
    /** Cuánto arde la vela del farol de tarro (de 0 a 1). */
    val tarro: Float get() = if (oscuro && !noche) 1f else if (noche && oscuro) 0.45f else if (noche) 0.35f else 0f
    /** Cuánto arde el farolillo de lata del costado. */
    val farolillo: Float get() = if (oscuro && !noche) 1f else 0f
    /** Cuánto arde el anafre: de noche, vivo; en los Sueños, en brasas. */
    val anafre: Float get() = if (oscuro && !noche) 1f else if (noche) (if (oscuro) 0.5f else 0.3f) else 0f
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    fun en(k: Float, tinte: Color, sol: Float, luna: Float, amb: Float) =
        LuzRuinas(this, k.coerceIn(0f, 1f), tinte, sol.coerceIn(0f, 1f), luna.coerceIn(0f, 1f), amb.coerceIn(0f, 1f))

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.19f + n.red * 0.81f, c.green * 0.19f + n.green * 0.81f, c.blue * 0.19f + n.blue * 0.81f, c.alpha)
    }

    /** El color del ánimo hundido: guarda más de su tono que lo demás, para que de lejos las entradas se sigan distinguiendo. */
    fun hundidoAnimo(c: Color): Color {
        val n = hondo
        val f = if (noche) 0.24f else 0.32f
        return Color(c.red * f + n.red * (1f - f), c.green * f + n.green * (1f - f), c.blue * f + n.blue * (1f - f), c.alpha)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.5f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> mezcla(SOMBRA_MUEBLE, Color(0xFF233A3A), 0.35f).copy(alpha = a.coerceIn(0f, 1f))
    }
}

/**
 * La luz de una cosa en un sitio: [k], cuánto le llega de las luces de la casa (la vela, el farolillo o las brasas) y de qué color
 * ([tinte]); [sol], del lucernario (de día, el sol; en los Sueños en claro, el ocaso): 1 donde falta el vidrio, algo menos y más
 * verde donde el vidrio sigue con su verdín; [luna], de la luna de los Sueños; [amb], de la luz del cuarto, que se apaga hacia
 * abajo. [c] da el color de algo con esa luz, [s] la sombra, [b] el plano de luz, [filo] el borde encendido por la llama y [frio]
 * el de la luna.
 */
internal class LuzRuinas(val a: AireRuinas, val k: Float, val tinte: Color, val sol: Float, val luna: Float, val amb: Float) {
    private val p = PaletaRuinas

    /** La misma luz con la mancha del lucernario forzada a [v]: el sol o el ocaso en claro; la luna en los Sueños en oscuro. */
    fun conMancha(v: Float): LuzRuinas = when {
        a.oscuro && a.noche -> LuzRuinas(a, k, tinte, sol, v.coerceIn(0f, 1f) * 0.85f, amb)
        !a.oscuro -> LuzRuinas(a, k, tinte, v.coerceIn(0f, 1f), luna, amb)
        else -> this
    }

    /** Cuánto enciende la llama lo que toca: casi nada lejos y de golpe cerca, para que manden los charcos. */
    private val q: Float get() = (1.1f * k * k).coerceAtMost(0.94f)

    fun c(col: Color): Color = when {
        a.oscuro -> {
            val fuego = mezcla(mezcla(a.hundido(col), alFuego(col), q), tinte, 0.1f * k)
            if (luna > 0.01f) mezcla(fuego, lunaFria(col), 0.62f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(col, (0.34f + 0.4f * (1f - amb) - 0.5f * sol).coerceIn(0.06f, 0.66f)), alOcaso(col), sol), tinte, 0.3f * k)
        else -> {
            // A la sombra, la luz del cuarto: más oscuro y apenas más frío, sin agrisar; al sol, más claro y más cálido (verdoso
            // donde el sol pasó por un vidrio con verdín).
            val f = 0.88f - 0.24f * (1f - amb)
            val sombra = Color(col.red * f, col.green * f, (col.blue * (f + 0.03f)).coerceAtMost(1f), col.alpha)
            if (sol <= 0f) sombra else mezcla(sombra, alSol(col), sol)
        }
    }

    /** Un color bajo el sol del lucernario: más claro y más cálido; donde el sol es parcial (el vidrio con verdín), más verde. */
    private fun alSol(c: Color): Color {
        val luz = mezcla(p.SOL_VERDIN, p.SOL, ((sol - 0.5f) * 2f).coerceIn(0f, 1f))
        return mezcla(Color((c.red * 1.26f + 0.08f).coerceAtMost(1f), (c.green * 1.14f + 0.05f).coerceAtMost(1f), (c.blue * 0.92f).coerceAtMost(1f), c.alpha), luz, 0.2f)
    }

    /** Un color bajo el sol bajo del ocaso: melocotón, más claro, sin lavar el color propio. */
    private fun alOcaso(c: Color): Color = mezcla(Color((c.red * 1.16f + 0.08f).coerceAtMost(1f), (c.green * 1.0f + 0.05f).coerceAtMost(1f), (c.blue * 0.86f + 0.02f).coerceAtMost(1f), c.alpha), p.MELOCOTON, 0.26f)

    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.95f), c.blue * (1f - hondo * 0.72f), c.alpha)

    /**
     * Un color bajo la llama: la luz de color se multiplica por el del objeto (el verde agua se vuelve oliva dorado, el roble
     * naranja, el papel amarillo), y aclara: lo cercano a la llama es lo más claro y lo más cálido del cuadro.
     */
    private fun alFuego(c: Color): Color =
        Color((c.red * tinte.red * 1.5f + 0.05f).coerceAtMost(1f), (c.green * tinte.green * 1.24f + 0.02f).coerceAtMost(1f), (c.blue * tinte.blue * 1.3f).coerceAtMost(1f), c.alpha)

    /** Un color bajo la luna: virado a azul hondo, solo un paso más claro que la noche (no un azul pálido). */
    private fun lunaFria(c: Color): Color =
        Color((c.red * 0.22f + 0.03f).coerceAtMost(1f), (c.green * 0.42f + 0.06f).coerceAtMost(1f), (c.blue * 0.58f + 0.11f).coerceAtMost(1f), c.alpha)

    /** La sombra: más franca donde da el sol (de borde más duro), más floja en la penumbra. */
    fun s(alfa: Float): Color = a.sombra(if (!a.oscuro) alfa * (1f + 0.45f * sol) else alfa * (1f + 0.4f * luna))

    fun b(alfa: Float): Color = when {
        a.oscuro -> mezcla(tinte, p.LUNA, (luna / (k + luna + 0.001f)).coerceIn(0f, 1f)).copy(alpha = (alfa * 0.95f * maxOf(k, luna * 0.85f)).coerceIn(0f, 1f))
        a.noche -> mezcla(luzMueble(1f), p.CREPUSCULO, 0.5f).copy(alpha = (alfa * (0.32f + 0.55f * maxOf(sol, k))).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.SOL, 0.5f * sol).copy(alpha = (alfa * (0.62f + 0.45f * sol) * (0.75f + 0.25f * amb)).coerceIn(0f, 1f))
    }

    fun filo(alfa: Float): Color = if (k < 0.03f || (!a.oscuro && !a.noche)) Color.Transparent
        else tinte.copy(alpha = (alfa * k * (if (a.oscuro) 1.2f else 0.75f)).coerceIn(0f, 0.92f))

    fun frio(alfa: Float): Color = if (!a.oscuro || luna < 0.03f) Color.Transparent else p.LUNA.copy(alpha = (alfa * luna).coerceIn(0f, 0.85f))

    /**
     * El color del ánimo con la luz que le llega. De noche se apaga algo menos que lo demás y conserva su color, para que en la
     * repisa más lejana las entradas se sigan distinguiendo.
     */
    fun animo(base: Color): Color = when {
        a.oscuro -> {
            val qq = maxOf(q, luna * 0.6f)
            val fuego = mezcla(a.hundidoAnimo(base), mezcla(base, alFuego(base), 0.6f), (0.08f + 0.9f * qq).coerceAtMost(0.94f))
            if (luna > 0.01f) mezcla(fuego, lunaFria(base), 0.5f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(base, (0.1f + 0.38f * (1f - amb) - 0.25f * sol).coerceIn(0.04f, 0.38f)), p.CREPUSCULO, 0.2f * sol), tinte, 0.22f * k)
        else -> {
            val f = 0.96f - 0.14f * (1f - amb)
            val sombra = Color(base.red * f, base.green * f, (base.blue * (f + 0.03f)).coerceAtMost(1f), base.alpha)
            if (sol <= 0f) sombra else mezcla(sombra, alSol(base), sol)
        }
    }

    /**
     * El cartón kraft de la etiqueta donde va el día: un valor medio, teñido de noche por la luz que le toca (cálido junto a la
     * llama, frío bajo la luna, hundido lejos de las dos); nunca tanto que el número deje de leerse.
     */
    fun etiqueta(oscura: Boolean = false): Color {
        val kraft = if (oscura) p.KRAFT_TEJUELO_OSC else p.KRAFT
        return when {
            a.oscuro -> mezcla(mezcla(mezcla(kraft, a.hondo, 0.7f), alFuego(mezcla(kraft, Color.White, 0.15f)), (q * 1.4f).coerceAtMost(1f)), lunaFria(kraft), 0.5f * luna)
            a.noche -> mezcla(mezcla(kraft, p.ANOCHECER, 0.2f - 0.12f * maxOf(sol, k)), p.CREPUSCULO, 0.14f * sol)
            else -> c(kraft)
        }
    }

    /**
     * La tinta del número sobre un cartón de color [fondo]: lápiz oscuro donde el cartón tiene luz; donde se hundió en la noche, el
     * grafito brilla un poco más claro que el cartón (así se sigue leyendo sin que el cartón tenga que quedar claro).
     */
    fun tinta(fondo: Color): Color {
        if (!a.oscuro) return p.LAPIZ
        return if (luminancia(fondo) < 0.085f) mezcla(Color(0xFF8E8670), tinte, 0.12f * k) else Color(0xFF0C0A08)
    }

    private fun luminancia(c: Color): Float {
        fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
        return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
    }
}

/**
 * El dibujo del estante del diario de la Ciudad verde (lo usa `EstanteRuinas`, en `BibliotecaFuturo.kt`): la librería de roble de
 * la biblioteca municipal, la de los libros que se salvaron, que el campamento subió al cuarto de lectura del último piso, bajo
 * el lucernario roto. Cornisa con su cornisón, dentellones y friso de tableros, costados en pilastra con las bisagras de las
 * puertas de vidrio que ya no están, baldas de canto moldurado con su portaetiquetas de latón (el rótulo del mes, en una tarjeta de
 * cartón) y zócalo; el barniz comido por el agua, musgo, un helecho en la cornisa rota y la hiedra que trepa. Contra el yeso verde
 * agua que se descascara, con el herbario colgado de un cordel.
 *
 * De día manda el sol del lucernario (arriba a la izquierda, fuera del cuadro): una mancha enrejada por los perfiles de hierro,
 * con paños donde falta el vidrio (encendidos) y paños con su vidrio verdoso, que se corre hacia la luz en lo que está delante.
 * De noche arden el farol de tarro de la cornisa (arriba a la izquierda), el farolillo de lata agujereada del costado derecho (a
 * media altura) y el anafre del pie; entre sus charcos, todo se hunde en un verde botella. En los Sueños en claro entra el ocaso,
 * bajo y largo; en oscuro, la luna por el lucernario, y la vela y las brasas arden bajas.
 *
 * La pared y el fondo de los huecos están en `MuebleEstanteRuinasPared.kt`, la librería en `MuebleEstanteRuinasMueble.kt`, lo de
 * arriba en `MuebleEstanteRuinasCorona.kt` y `MuebleEstanteRuinasMaquina.kt`, lo que llena los huecos en
 * `MuebleEstanteRuinasHuecos.kt`, el pie en `MuebleEstanteRuinasPie.kt` y las entradas en `MuebleEstanteRuinasVolumenes.kt`.
 */
internal object MuebleEstanteRuinas {
    val LETRA: FontFamily = PaletaRuinas.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteRuinas` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.3f
    val LADO = 0.085f
    val ALTO_REPISA: Dp = 112.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    /**
     * Más ancho cuanto más escribiste: crece rápido en las entradas cortas (con la raíz de la cuenta) y se llena hacia las 140
     * palabras, con unos dp de más o de menos según la cuenta, para que la repisa no sea una empalizada.
     */
    fun ancho(palabras: Int): Dp {
        val crece = (2.2f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(25f)
        return (24f + crece + Math.floorMod(palabras * 37 + 5, 6)).dp
    }

    /** Cuál de los tres es una entrada: 0, el libro salvado; 1, la botella de las hojas enrolladas; 2, el fardo de lona encerada. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x5a17, 61)
        return if (x < 0.4f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El libro, el más alto; el fardo, el más bajo; todos un poco más altos cuanto más escribiste, y con algo de azar. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.76f + 0.04f * lleno + 0.16f * azar(semilla, 1)
            // La botella según su hechura (la misma que dibuja el volumen): de vino, la más alta; de borgoña; de botica, la baja.
            1 -> when ((azar(semilla, 21) * 3).toInt()) {
                0 -> 0.86f + 0.03f * lleno + 0.06f * azar(semilla, 1)
                1 -> 0.8f + 0.04f * lleno + 0.08f * azar(semilla, 1)
                else -> 0.72f + 0.04f * lleno + 0.06f * azar(semilla, 1)
            }
            else -> 0.7f + 0.06f * lleno + 0.14f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz (todo en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 132f
    /** Donde pisa lo de arriba (la cara del cornisón), en anchos desde arriba. */
    const val APOYO = 0.47f
    /** La llama del farol de tarro, sobre la cornisa a la izquierda, en anchos. */
    const val TARRO_X = 0.205f
    const val TARRO_Y = 0.41f
    /** El farolillo de lata colgado del costado derecho: su x, y a qué fracción del cuerpo cuelga. */
    const val FAROLILLO_X = 0.958f
    const val FAROLILLO_T = 0.44f
    /** Las brasas del anafre: su x y su altura sobre el pie del lienzo (en anchos). */
    const val ANAFRE_X = 0.27f
    const val ANAFRE_Y = 0.13f

    /** El alto del cuerpo (de la corona al pie de la última repisa) con [repisas] repisas, en anchos. */
    fun cuerpo(repisas: Int): Float = repisas * FILA / ANCHO_NOMINAL

    /** Dónde arde el farolillo, en anchos desde arriba, en un estante de [repisas] repisas. */
    fun farolilloY(repisas: Int): Float = CORONA + cuerpo(repisas) * FAROLILLO_T

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [repisas] repisas y [alto]
     * anchos. [delante]: cuánto está por delante de la pared (0, la pared; 0.03, los cantos; 0.05, las entradas), para que la
     * mancha del lucernario se corra hacia la luz en lo que está delante.
     */
    fun luz(a: AireRuinas, x: Float, y: Float, alto: Float, repisas: Int, delante: Float = 0f): LuzRuinas {
        val o = a.oscuro
        // La vela del tarro: alumbra lo de arriba y se apaga hacia la segunda repisa.
        val kt = if (a.tarro > 0f) a.tarro * ((if (o) 1.95f else 1.5f) * exp((if (o) -6.2f else -4.6f) * dist(x - TARRO_X, (y - TARRO_Y) * 0.9f))).coerceAtMost(1f) else 0f
        // El farolillo: un charco a media altura, más alto que ancho; enciende su costado y las entradas vecinas.
        val kf = if (a.farolillo > 0f) a.farolillo * (1.95f * exp(-5.8f * dist((x - FAROLILLO_X) * 1.0f, (y - farolilloY(repisas)) * 0.72f))).coerceAtMost(1f) else 0f
        // El anafre: encendido abajo, se apaga hacia arriba.
        val ka = if (a.anafre > 0f) a.anafre * kAnafre(x, y, alto, o) else 0f
        val k = (kt + kf + ka).coerceIn(0f, 1f)
        // El color de la luz: la vela, amarilla; el farolillo, algo más naranja; las brasas, ámbar anaranjado (el rojo, solo en la boca).
        val suma = kt + kf + ka + 0.0001f
        val tinte = if (k < 0.001f) PaletaRuinas.LLAMA else mezcla(PaletaRuinas.LLAMA, PaletaRuinas.FUEGO, (kf * 0.4f + ka * 0.62f) / suma)
        val sol = when {
            a.oscuro -> 0f
            a.noche -> ocasoRuinas(x + delante * 0.95f, y + delante * 0.3f)
            else -> solRuinas(x + delante * 0.75f, y + delante * 0.85f)
        }
        val luna = if (a.oscuro && a.noche) lunaRuinas(x + delante * 0.5f, y + delante * 0.9f) else 0f
        val amb = (1f - 0.09f * y - 0.12f * x).coerceIn(0.55f, 1f)
        return a.en(k, tinte, sol, luna, amb)
    }

    private fun dist(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)

    /** Cuánto alumbran las brasas del anafre un punto (sin contar si arden): un charco que sube por el zócalo y la última repisa. */
    fun kAnafre(x: Float, y: Float, alto: Float, oscuro: Boolean = true): Float {
        val dyA = (alto - y - ANAFRE_Y).coerceAtLeast(0f)
        return ((if (oscuro) 2.1f else 1.9f) * exp((if (oscuro) -5.6f else -3.8f) * dist((x - ANAFRE_X) * 1.1f, dyA * 0.62f))).coerceAtMost(1f)
    }

    /**
     * De qué lado le llega la luz a un punto: -1 si de la izquierda, 1 si de la derecha. De día y en los Sueños, del lucernario
     * (izquierda); de noche, de la luz de la casa que más lo alumbra (el farolillo está a la derecha; la vela y el anafre, a la
     * izquierda).
     */
    fun hacia(a: AireRuinas, x: Float, y: Float, alto: Float, repisas: Int): Float {
        if (!a.oscuro || a.noche) return -1f
        val kt = 1.95f * exp(-6.2f * dist(x - TARRO_X, (y - TARRO_Y) * 0.9f))
        val kf = 1.95f * exp(-5.8f * dist(x - FAROLILLO_X, (y - farolilloY(repisas)) * 0.72f))
        val ka = kAnafre(x, y, alto) * 1.1f
        return when {
            kf > kt && kf > ka -> 1f
            kt >= ka -> if (x < TARRO_X) 1f else -1f
            else -> if (x < ANAFRE_X) 1f else -1f
        }
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireRuinas(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoRuinas(w, h, ch, d.izquierda.right, d.derecha.left, fondo, n, a)

        // ---- debajo de la corona guardada, la fila de la junta (que puede caer a medio píxel): el roble del pie de la cornisa
        // desde el píxel entero de arriba, así no asoma el fondo.
        drawRect(g.luz(w * 0.5f, ch, 0.03f).c(PaletaRuinas.ROBLE_OSC), Offset(0f, floor(ch) - 1f), Size(w, 3f))
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaRuinas.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(ParedEstanteRuinas) { paredArriba(g) }
            with(CoronaEstanteRuinas) { detras(g) }
            with(MuebleRuinas) { cornisa(g) }
            with(CoronaEstanteRuinas) { delante(g) }
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        with(ParedEstanteRuinas) { d.repisas.forEachIndexed { k, r -> fondoHueco(k, r, g) } }
        with(HuecosEstanteRuinas) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(MuebleRuinas) {
            juntas(g, d)
            d.tablas.forEachIndexed { k, r -> balda(k, r, g) }
            d.placas.forEach { p -> placa(p, medidor, g) }
            costados(g)
        }
        with(PieEstanteRuinas) { pie(g) }
        with(MuebleRuinas) { farolillo(g) }
        granoMueble(Rect(0f, fondo, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenRuinas(v, medidor, oscuro)
}

// ---------------------------------------------------------------- la mancha del lucernario

/**
 * Un enrejado de luz: el paralelogramo con esquina ([ox], [oy]) y lados ([ux], [uy]) (a lo ancho, [cols] paños) y ([vx], [vy])
 * (a lo alto, [filas] paños), con los perfiles de hierro de [barra] (en fracción de un paño). [paños] dice, por paño (fila a fila),
 * cuánto sol pasa: 1 donde falta el vidrio, 0.6 donde sigue con su verdín, 0 donde lo tapa algo. De 0 a 1, con el borde neto.
 */
internal class EnrejadoRuinas(val ox: Float, val oy: Float, val ux: Float, val uy: Float, val vx: Float, val vy: Float,
                              val cols: Int, val filas: Int, val barra: Float, val panos: FloatArray) {
    private val det = ux * vy - uy * vx

    fun en(x: Float, y: Float): Float {
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val dx = x - ox; val dy = y - oy
        val s = (dx * vy - dy * vx) / det
        val t = (ux * dy - uy * dx) / det
        if (s <= 0f || s >= 1f || t <= 0f || t >= 1f) return 0f
        val cs = s * cols; val ct = t * filas
        val fs = cs - floor(cs); val ft = ct - floor(ct)
        val b = barra * 0.5f
        if (fs < b || fs > 1f - b || ft < b || ft > 1f - b) return 0f
        val i = (floor(ct).toInt().coerceIn(0, filas - 1)) * cols + floor(cs).toInt().coerceIn(0, cols - 1)
        return panos.getOrElse(i) { 0f }
    }

    /** Los paños con al menos [minimo] de sol, como camino en píxeles ([w], el ancho), corridos [dx], [dy] anchos. */
    fun camino(w: Float, minimo: Float, maximo: Float = 2f, dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        val b = barra * 0.5f
        for (f in 0 until filas) for (c in 0 until cols) {
            val v = panos.getOrElse(f * cols + c) { 0f }
            if (v < minimo || v > maximo) continue
            val s0 = (c + b) / cols; val s1 = (c + 1 - b) / cols
            val t0 = (f + b) / filas; val t1 = (f + 1 - b) / filas
            fun p(s: Float, t: Float) = Offset((ox + ux * s + vx * t + dx) * w, (oy + uy * s + vy * t + dy) * w)
            val a = p(s0, t0); val bb = p(s1, t0); val c2 = p(s1, t1); val d = p(s0, t1)
            moveTo(a.x, a.y); lineTo(bb.x, bb.y); lineTo(c2.x, c2.y); lineTo(d.x, d.y); close()
        }
    }
}

/** El sol del lucernario: alto, de arriba a la izquierda; dos paños sin vidrio, tres con su verdín y uno roto a medias. */
internal val SOL_RUINAS = EnrejadoRuinas(-0.07f, -0.05f, 0.56f, 0.13f, 0.27f, 0.62f, 3, 1, 0.13f,
    floatArrayOf(1f, 0.62f, 1f))

/** El ocaso de los Sueños en claro: entra bajo y largo, casi tendido. */
internal val OCASO_RUINAS = EnrejadoRuinas(-0.08f, 0.33f, 0.96f, 0.2f, 0.05f, 0.17f, 3, 1, 0.1f,
    floatArrayOf(1f, 0.62f, 1f))

/** La luna de los Sueños en oscuro: más chica y empinada. */
internal val LUNA_RUINAS = EnrejadoRuinas(0.14f, -0.04f, 0.3f, 0.07f, 0.13f, 0.44f, 3, 1, 0.14f,
    floatArrayOf(1f, 0.62f, 1f))

internal fun solRuinas(x: Float, y: Float): Float = SOL_RUINAS.en(x, y)
internal fun ocasoRuinas(x: Float, y: Float): Float = OCASO_RUINAS.en(x, y)
internal fun lunaRuinas(x: Float, y: Float): Float = LUNA_RUINAS.en(x, y)

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]) y cuántas repisas hay ([n]). Y la luz de cada punto.
 */
internal class MarcoRuinas(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val n: Int, val a: AireRuinas) {
    private val m = MuebleEstanteRuinas
    /** Donde pisa lo de arriba: la cara del cornisón. */
    val apoyo: Float get() = w * m.APOYO
    /** La luz de un punto en píxeles, a [delante] anchos de la pared; con [mancha], cuánto le da la mancha del lucernario (forzado). */
    fun luz(p: Offset, delante: Float = 0f, mancha: Float? = null): LuzRuinas = luz(p.x, p.y, delante, mancha)
    fun luz(x: Float, y: Float, delante: Float = 0f, mancha: Float? = null): LuzRuinas {
        val l = m.luz(a, x / w, y / w, h / w, n, delante)
        return if (mancha == null) l else l.conMancha(mancha)
    }
    /** Dónde arde cada luz, en píxeles. */
    val tarro: Offset get() = Offset(w * m.TARRO_X, w * m.TARRO_Y)
    val farolillo: Offset get() = Offset(w * m.FAROLILLO_X, w * m.farolilloY(n))
    val anafre: Offset get() = Offset(w * m.ANAFRE_X, h - w * m.ANAFRE_Y)
    /** El enrejado del momento (sol, ocaso o luna), o null de noche en el diario. */
    val enrejado: EnrejadoRuinas? get() = when {
        !a.oscuro && !a.noche -> SOL_RUINAS
        !a.oscuro && a.noche -> OCASO_RUINAS
        a.oscuro && a.noche -> LUNA_RUINAS
        else -> null
    }
    /** El corrimiento de la mancha para lo que está a [delante] anchos de la pared (hacia la luz). */
    fun corre(delante: Float): Offset = when {
        !a.oscuro && a.noche -> Offset(-delante * 0.95f, -delante * 0.3f)
        a.oscuro && a.noche -> Offset(-delante * 0.5f, -delante * 0.9f)
        else -> Offset(-delante * 0.75f, -delante * 0.85f)
    }
}

/**
 * Dibuja [pieza] a la luz del cuarto (con la mancha apagada) y, recortada a la mancha del lucernario para lo que está a [delante]
 * anchos de la pared, otra vez con la mancha encendida: una vez en los paños con verdín y otra en los paños sin vidrio. Así la luz
 * pinta lo que toca, con borde neto. Sin mancha (la noche del diario), una sola vez.
 */
internal fun DrawScope.conManchaRuinas(g: MarcoRuinas, delante: Float, zona: Rect? = null, pieza: DrawScope.(Float?) -> Unit) {
    val e = g.enrejado
    if (e == null) { pieza(null); return }
    pieza(0f)
    val c = g.corre(delante)
    val verdes = e.camino(g.w, 0.3f, 0.9f, c.x, c.y)
    val claros = e.camino(g.w, 0.9f, 2f, c.x, c.y)
    for ((camino, v) in listOf(verdes to 0.6f, claros to 1f)) {
        val b = camino.getBounds()
        if (b.isEmpty) continue
        if (zona != null && !b.overlaps(zona)) continue
        clipRect(b.left, b.top, b.right, b.bottom) { clipPath(camino) { pieza(v) } }
    }
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser medio
 * píxel, deja ver lo que se pintó debajo (el pie de la cornisa), no el fondo.
 */
internal object CapaRuinas {
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

// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de la Ciudad verde, la de día y la de noche (bajo, medio, alto). */
private val TONOS_RUINAS: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.RUINAS, false) to juegoTonos(TemaId.RUINAS, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaRuinas(t: Color, escala: List<Color>): Float? {
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
 * entrada clara se volvía oscura de noche. Aquí la entrada es un objeto del cuarto: conserva su color de día y la luz lo apaga con
 * la distancia. Se ubica el color en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaRuinas(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_RUINAS
    val x = enEscalaRuinas(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El material del ánimo: se ubica el color (de día) en la escala del tema y se toma el mismo punto en [materiales] (tela, vidrio o
 * lona, de lo malo a lo bueno). Si el color no está en la escala, queda igual.
 */
internal fun materialDelAnimoRuinas(t: Color, materiales: List<Color>, viveza: Float = 1.3f): Color {
    val x = enEscalaRuinas(t, TONOS_RUINAS.first) ?: return t
    val c = if (x < 0.5f) mezcla(materiales[0], materiales[1], x * 2) else mezcla(materiales[1], materiales[2], (x - 0.5f) * 2)
    return vivoRuinas(c, viveza)
}

/** [c] con más color: se aleja de su gris (del mismo valor) en [f]; así los tonos de en medio no se lavan. */
internal fun vivoRuinas(c: Color, f: Float): Color {
    val g = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    return Color((g + (c.red - g) * f).coerceIn(0f, 1f), (g + (c.green - g) * f).coerceIn(0f, 1f), (g + (c.blue - g) * f).coerceIn(0f, 1f), c.alpha)
}
