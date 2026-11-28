package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * La paleta del estante de Soviético: la del Ideario del mismo tema (el rincón del fichero en la biblioteca del club de la
 * fábrica), con lo que le falta a esta pared. Lo frío: el óleo azul apagado de abajo de la pared, la chapa gris verdosa, el vidrio
 * verde de la lámpara y la noche azul verdosa del cuarto. Lo cálido: la cal amarillenta, el roble barnizado de la estantería y del
 * parquet, el latón, la cartulina, el papel de estraza, el rojo de cartel y la bombilla.
 */
internal object PaletaUrss {
    /** La pared: la cal amarillenta de arriba y su sombra; el óleo azul apagado de abajo y su raya (los del Ideario). */
    val CAL = Color(0xFFE6DEC6)
    val CAL_OSC = Color(0xFFC6BB9A)
    val OLEO = Color(0xFF6C8A92)
    val OLEO_OSC = Color(0xFF3D5864)
    /** El roble barnizado de la estantería: su tono, su luz, su sombra y la veta. */
    val ROBLE = Color(0xFFA56E3A)
    val ROBLE_LUZ = Color(0xFFCB955A)
    val ROBLE_OSC = Color(0xFF6B4324)
    val VETA = Color(0xFF4A2D17)
    /** La chapa esmaltada gris verdosa del fichero del Ideario, su luz, su sombra y el metal que asoma. */
    val ACERO = Color(0xFF7A8478)
    val ACERO_LUZ = Color(0xFF9AA498)
    val ACERO_OSC = Color(0xFF4E5650)
    val METAL = Color(0xFFBFC2BA)
    /** El aluminio de las bobinas y de las latas. */
    val ALUMINIO = Color(0xFFB4B8B2)
    /** El latón de los portaetiquetas y de los anillos. */
    val LATON = Color(0xFFB89A50)
    val LATON_OSC = Color(0xFF7A6430)
    /** El rojo de cartel, el granate, el negro de la baquelita y de la tinta. */
    val ROJO = Color(0xFFC1121F)
    val GRANATE = Color(0xFF7C1A1E)
    val NEGRO = Color(0xFF161414)
    /** La cartulina de las fichas (clara y amarillenta), el cartón, el papel de estraza y el bramante. */
    val CARTULINA = Color(0xFFEFE6D2)
    val CARTULINA_OSC = Color(0xFFD8CCB0)
    val CARTON = Color(0xFFB09670)
    val ESTRAZA = Color(0xFFA68559)
    val BRAMANTE = Color(0xFFC9B48A)
    /** El parquet de roble en espiga (el del Ideario). */
    val PARQUET = Color(0xFF8A6438)
    val PARQUET_OSC = Color(0xFF5E4024)
    /** El vidrio verde de la lámpara, y encendido por dentro. */
    val VERDE = Color(0xFF2E5E3A)
    val VERDE_LUZ = Color(0xFF86DCA4)
    /** La tinta violeta del sello y el té. */
    val VIOLETA = Color(0xFF5A4690)
    val TE = Color(0xFFB5652A)
    /** El papel de periódico. */
    val PERIODICO = Color(0xFFCFC6AE)
    /** La luz: la bombilla (la del Ideario), el dial del receptor, la luz de día que entra de arriba y la luna. */
    val LLAMA = Color(0xFFFFD08A)
    val DIAL = Color(0xFFFFB45C)
    val DIA = Color(0xFFF6F1E2)
    val LUNA = Color(0xFFD2E0EC)
    /** El aire de la noche del cuarto (el azul verdoso hondo del Ideario), el de los Sueños, el anochecer y su ocaso. */
    val NOCHE = Color(0xFF0A191D)
    val NOCHE_SUENOS = Color(0xFF0B1722)
    /**
     * El oscuro en que se hunde lo que no alumbra ninguna bombilla: el mismo azul verdoso del Ideario, un punto más claro que el de
     * las sombras, para que entre los charcos se vea el paso del cálido al frío en el roble y en la pared.
     */
    val NOCHE_CUERPO = Color(0xFF10262F)
    val NOCHE_CUERPO_SUENOS = Color(0xFF0F2131)
    val ANOCHECER = Color(0xFF3B3A55)
    val CREPUSCULO = Color(0xFFF2A26E)
    val MELOCOTON = Color(0xFFF6C8A0)

    /**
     * El ánimo en tela de encuadernar, de lo malo a lo bueno, en cinco puntos de la misma escala del tema (rojo, crudo, negro): el
     * rojo, el coral, el lino crudo, el gris topo oscuro y el carbón cálido. Así cada tramo tiene cuerpo (el de en medio del crudo
     * al negro no se queda en gris ceniza).
     */
    val TELAS = listOf(Color(0xFFAB1E1B), Color(0xFFD9573C), Color(0xFFDCCCA2), Color(0xFF6E5C4D), Color(0xFF2D2824))
    /** El ánimo en papel de cartel: rojo de cartel, coral, papel crudo, topo, tinta negra. */
    val PAPELES = listOf(Color(0xFFC41A1A), Color(0xFFE2603E), Color(0xFFE6D6AC), Color(0xFF766455), Color(0xFF282320))
    /** El ánimo en esmalte de pintura de carteles: rojo, coral, crudo, topo, negro. */
    val PINTURAS = listOf(Color(0xFFCC2219), Color(0xFFE8623C), Color(0xFFEDDDB6), Color(0xFF7A6A5C), Color(0xFF221D1A))

    /** La letra del tema (la de sus rótulos). */
    val LETRA: FontFamily = LetraExtra.russo
}

/**
 * El aire del momento en el estante de Soviético: de día, la luz fría de arriba a la izquierda; de noche (el modo oscuro) arden la
 * lámpara colgada de vidrio verde, la lámpara de pinza del costado y el dial del receptor; en los Sueños ([noche]), el último sol
 * por una ventana fuera del cuadro en claro, y la luna por la misma ventana en oscuro, con la lámpara baja y el dial encendido.
 */
internal class AireUrss(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaUrss
    /** Cuánto arde la lámpara colgada (de 0 a 1). */
    val lampara: Float get() = if (oscuro && !noche) 1f else if (oscuro) 0.32f else 0f
    /** Cuánto arde la lámpara de pinza del costado. */
    val pinza: Float get() = if (oscuro && !noche) 1f else 0f
    /** Cuánto alumbra el dial del receptor. */
    val dial: Float get() = if (oscuro && !noche) 1f else if (oscuro) 0.6f else 0f
    /** Si es de día. */
    val dia: Boolean get() = !oscuro && !noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    fun en(k: Float, tinte: Color, mancha: Float, amb: Float, lav: Float = 0f, verde: Float = 0f) =
        LuzUrss(this, k.coerceIn(0f, 1f), tinte, mancha.coerceIn(0f, 1f), amb.coerceIn(0f, 1f), lav.coerceIn(0f, 1f), verde.coerceIn(0f, 1f))

    /** El oscuro del cuerpo: el azul verdoso del Ideario (de noche) o el de las sombras (de día y al anochecer). */
    private val cuerpo: Color get() = if (oscuro) (if (noche) p.NOCHE_CUERPO_SUENOS else p.NOCHE_CUERPO) else hondo

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire azul verdoso del cuarto. */
    fun hundido(c: Color): Color {
        val n = cuerpo
        return Color(c.red * 0.16f + n.red * 0.84f, c.green * 0.16f + n.green * 0.84f, c.blue * 0.16f + n.blue * 0.84f, c.alpha)
    }

    /**
     * El color del ánimo hundido: baja de valor pero conserva su matiz, tostándose (el rojo queda rojo oscuro; el crudo, tostado
     * oscuro; el carbón, casi negro), con algo del aire del cuarto; así las entradas lejanas se siguen distinguiendo por su color.
     */
    fun hundidoAnimo(c: Color): Color = mezcla(Color(c.red * 0.42f, c.green * 0.34f, c.blue * 0.26f, c.alpha), cuerpo, 0.32f)

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.55f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> SOMBRA_MUEBLE.copy(alpha = a.coerceIn(0f, 1f))
    }
}

/**
 * La luz de una cosa en un sitio: [k], cuánto le llega de las bombillas (la lámpara colgada, la de pinza o el dial) y de qué color
 * ([tinte]); [mancha], cuánto le da la luz de la ventana de fuera del cuadro en los Sueños (el último sol en claro, la luna en
 * oscuro); [amb], cuánto le llega de la luz de día (1 arriba a la izquierda, menos hacia abajo a la derecha); [lav], de noche y solo
 * en la pared, el rebote ancho y sin color de la lámpara en el cuarto; [verde], de noche, el resplandor del vidrio verde por encima
 * de la pantalla. [c] da el color de algo con esa luz, [s] la sombra, [b] el plano de luz, [filo] el borde encendido por una
 * bombilla y [frio] el de la luna.
 */
internal class LuzUrss(val a: AireUrss, val k: Float, val tinte: Color, val mancha: Float, val amb: Float, val lav: Float = 0f, val verde: Float = 0f) {
    private val p = PaletaUrss
    val luna: Float get() = if (a.oscuro && a.noche) mancha else 0f
    val ocaso: Float get() = if (!a.oscuro && a.noche) mancha else 0f

    /** La misma luz con la mancha de la ventana forzada a [v]. */
    fun conMancha(v: Float): LuzUrss = if (!a.noche) this else LuzUrss(a, k, tinte, v.coerceIn(0f, 1f) * (if (a.oscuro) 0.85f else 1f), amb, lav, verde)

    /** Cuánto enciende la bombilla lo que toca: casi nada lejos y de golpe cerca, para que manden los charcos. */
    private val q: Float get() = (1.1f * k * k).coerceAtMost(0.94f)

    fun c(col: Color): Color = when {
        a.oscuro -> {
            // Lo hundido, aclarado por el rebote de la lámpara en la pared hacia su propio color apagado.
            val base = if (lav > 0.01f) mezcla(a.hundido(col), apagado(col), lav) else a.hundido(col)
            var r = mezcla(mezcla(base, alFuego(col), q), tinte, 0.07f * k)
            if (verde > 0.01f) r = mezcla(r, mezcla(alVerde(col), p.VERDE_LUZ, 0.15f), 0.5f * verde)
            if (luna > 0.01f) r = mezcla(r, lunaFria(col), 0.7f * luna)
            r
        }
        a.noche -> mezcla(mezcla(anochecer(col, (0.26f + 0.24f * (1f - amb) - 0.45f * ocaso).coerceIn(0.08f, 0.5f)), alOcaso(col), ocaso), tinte, 0.3f * k)
        else -> alDia(col)
    }

    /**
     * Un color a la luz de día (fría, alta, de arriba a la izquierda): cerca se aclara un poco hacia el blanco de la luz; lejos se
     * apaga hacia su propio oscuro, algo frío, sin agrisar. Como el fichero del Ideario, claro a la izquierda y hondo a la derecha.
     */
    private fun alDia(c: Color): Color =
        if (amb >= 0.58f) mezcla(c, p.DIA, (amb - 0.58f) * 0.5f)
        else {
            val f = (0.58f - amb) * 0.7f
            Color(c.red * (1f - f), c.green * (1f - f * 0.96f), c.blue * (1f - f * 0.88f), c.alpha)
        }

    /** Un color bajo el último sol: melocotón, más claro, sin lavar el color propio. */
    private fun alOcaso(c: Color): Color = mezcla(Color((c.red * 1.08f + 0.04f).coerceAtMost(1f), (c.green * 0.96f + 0.03f).coerceAtMost(1f), (c.blue * 0.8f + 0.01f).coerceAtMost(1f), c.alpha), p.MELOCOTON, 0.3f)

    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.96f), c.blue * (1f - hondo * 0.86f), c.alpha)

    /** Un color bajo la bombilla: la luz de color se multiplica por el del objeto y aclara; lo cercano es lo más claro del cuadro. */
    private fun alFuego(c: Color): Color = mezcla(Color((c.red * 1.06f + 0.03f).coerceAtMost(1f), (c.green * 1.02f + 0.02f).coerceAtMost(1f), (c.blue * 0.98f).coerceAtMost(1f), c.alpha),
        Color((c.red * tinte.red * 1.32f + 0.04f).coerceAtMost(1f), (c.green * tinte.green * 1.2f + 0.02f).coerceAtMost(1f), (c.blue * tinte.blue * 1.25f).coerceAtMost(1f), c.alpha), 0.48f)

    /** Un color bajo el vidrio verde: su valor a media luz, virado al verde. */
    private fun alVerde(c: Color): Color =
        Color((c.red * 0.32f + 0.02f).coerceAtMost(1f), (c.green * 0.5f + 0.08f).coerceAtMost(1f), (c.blue * 0.38f + 0.04f).coerceAtMost(1f), c.alpha)

    /** Un color bajo la luna: el mismo, virado a frío y un paso más claro que la noche. */
    private fun lunaFria(c: Color): Color =
        Color((c.red * 0.38f + 0.04f).coerceAtMost(1f), (c.green * 0.48f + 0.06f).coerceAtMost(1f), (c.blue * 0.56f + 0.1f).coerceAtMost(1f), c.alpha)

    /** Un color apagado por la luz floja del cuarto: su propio color a media luz, con algo del azul verdoso de la noche. */
    private fun apagado(c: Color): Color {
        val n = a.hondo
        return Color(c.red * 0.4f + n.red * 0.3f, c.green * 0.42f + n.green * 0.32f, c.blue * 0.42f + n.blue * 0.32f, c.alpha)
    }

    /** La sombra: más franca donde da la mancha (de borde más duro). */
    fun s(alfa: Float): Color = a.sombra(if (!a.oscuro) alfa * (1f + 0.45f * ocaso) else alfa * (1f + 0.4f * luna))

    fun b(alfa: Float): Color = when {
        a.oscuro -> {
            val col = if (verde > k) mezcla(p.VERDE_LUZ, luzMueble(1f), 0.3f) else mezcla(tinte, p.LUNA, (luna / (k + luna + 0.001f)).coerceIn(0f, 1f))
            col.copy(alpha = (alfa * 0.95f * maxOf(k, luna * 0.85f, verde * 0.6f)).coerceIn(0f, 1f))
        }
        a.noche -> mezcla(luzMueble(1f), p.CREPUSCULO, 0.5f).copy(alpha = (alfa * (0.3f + 0.55f * maxOf(ocaso, k))).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.DIA, 0.6f).copy(alpha = (alfa * (0.55f + 0.5f * amb)).coerceIn(0f, 1f))
    }

    fun filo(alfa: Float): Color = if (k < 0.03f || a.dia) Color.Transparent
        else tinte.copy(alpha = (alfa * k * (if (a.oscuro) 1.2f else 0.75f)).coerceIn(0f, 0.92f))

    fun frio(alfa: Float): Color = if (!a.oscuro || luna < 0.03f) Color.Transparent else p.LUNA.copy(alpha = (alfa * luna).coerceIn(0f, 0.85f))

    /** El color del ánimo con la luz que le llega; de noche se apaga algo menos que lo demás y conserva su color. */
    fun animo(base: Color): Color = when {
        a.oscuro -> {
            val qq = maxOf(q, luna * 0.6f)
            val fuego = mezcla(a.hundidoAnimo(base), mezcla(base, alFuego(base), 0.55f), (0.08f + 0.9f * qq).coerceAtMost(0.94f))
            if (luna > 0.01f) mezcla(fuego, lunaFria(base), 0.5f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(base, (0.12f + 0.3f * (1f - amb) - 0.25f * ocaso).coerceIn(0.05f, 0.4f)), p.CREPUSCULO, 0.2f * ocaso), tinte, 0.22f * k)
        else -> if (amb >= 0.58f) mezcla(base, p.DIA, (amb - 0.58f) * 0.28f)
            else { val f = (0.58f - amb) * 0.4f; Color(base.red * (1f - f), base.green * (1f - f), base.blue * (1f - f * 0.9f), base.alpha) }
    }

    /**
     * La cartulina amarillenta de las etiquetas (la de las fichas del Ideario, ya vieja): un valor medio, teñido de noche por la luz
     * que le toca (cálido junto a la bombilla, frío bajo la luna, hundido lejos); nunca tanto que el número deje de leerse.
     */
    fun etiqueta(base: Color = ETIQUETA_URSS): Color = when {
        a.oscuro -> mezcla(mezcla(mezcla(base, a.hondo, 0.7f), alFuego(mezcla(base, Color.White, 0.12f)), (q * 1.4f).coerceAtMost(1f)), lunaFria(base), 0.5f * luna)
        a.noche -> mezcla(mezcla(base, p.ANOCHECER, 0.22f - 0.12f * maxOf(ocaso, k)), p.CREPUSCULO, 0.14f * ocaso)
        else -> c(base)
    }

    /** La tinta sobre un papel de color [fondo]: oscura donde el papel tiene luz; donde se hundió, un gris claro que se lee. */
    fun tinta(fondo: Color): Color {
        if (!a.oscuro) return p.NEGRO
        return if (luminanciaUrss(fondo) < 0.085f) mezcla(Color(0xFF8C9490), tinte, 0.12f * k) else Color(0xFF0D0E0E)
    }
}

/** La cartulina de las etiquetas: tostada, de valor medio (no blanca), como la tarjeta vieja de los portaetiquetas del Ideario. */
internal val ETIQUETA_URSS = Color(0xFFBBA273)

internal fun luminanciaUrss(c: Color): Float {
    fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
    return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
}

/**
 * El dibujo del estante del diario de Soviético (lo usa `EstanteUrss`, en `BibliotecaContemporanea.kt`): la estantería abierta de
 * roble barnizado de la biblioteca del club de la fábrica, en la otra pared del mismo cuarto que el fichero del Ideario, contra la
 * misma pintura a dos tonos (la cal arriba, el óleo azul apagado abajo, la raya a pulso). Sin fondo: en cada hueco se ve el óleo.
 * Cornisa escalonada, friso con su filete negro y su chapita, costados gruesos, tablas con canto y zócalo negro. Arriba, el
 * proyector de cine con sus bobinas, las latas de película, el vaso de té, los libros con el sello y la regla en T; en la pared, el
 * altavoz de la radio por cable y un pliego de prueba; colgada, la lámpara de vidrio verde. Al pie, sobre el parquet en espiga, la
 * pila de periódicos y el receptor de válvulas en su taburete.
 *
 * De día la luz entra de arriba a la izquierda, de fuera del cuadro. De noche arden la lámpara colgada (arriba a la derecha), la
 * lámpara de pinza sujeta al costado izquierdo (a media altura) y el dial del receptor (al pie); entre sus charcos, el azul
 * verdoso hondo del Ideario. En los Sueños en claro, el último sol entra bajo por una ventana de fuera del cuadro y pinta sus
 * paños en la cal; en oscuro, la luna por la misma ventana.
 *
 * La pared y el fondo de los huecos están en `MuebleEstanteUrssPared.kt`, la estantería en `MuebleEstanteUrssObra.kt`, lo de arriba
 * en `MuebleEstanteUrssCorona.kt` y `MuebleEstanteUrssMuro.kt`, lo que llena los huecos en `MuebleEstanteUrssHuecos.kt`, el pie en
 * `MuebleEstanteUrssPie.kt`, las entradas en `MuebleEstanteUrssVolumenes.kt`, los trazos comunes en `MuebleEstanteUrssTrazos.kt` y
 * los paños de la ventana de los Sueños, la imagen guardada y el color del ánimo en `MuebleEstanteUrssLuz.kt`.
 */
internal object MuebleEstanteUrss {
    val LETRA: FontFamily = PaletaUrss.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteUrss` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.3f
    val LADO = 0.075f
    val ALTO_REPISA: Dp = 116.dp
    val TABLA: Dp = 22.dp
    val SEPARA: Dp = 3.dp

    /** Más ancho cuanto más escribiste: crece rápido en las entradas cortas y se llena hacia las 100 palabras, con algo de azar. */
    fun ancho(palabras: Int): Dp {
        val crece = (2.2f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(22f)
        return (27f + crece + Math.floorMod(palabras * 37 + 5, 6)).dp
    }

    /** Cuál de los tres es una entrada: 0, el libro de tela; 1, el rollo de carteles; 2, el bote de pintura. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x5a17, 47)
        return if (x < 0.44f) 0 else if (x < 0.73f) 1 else 2
    }

    /** El libro, el más alto; el rollo, casi tanto; el bote, más bajo (su brocha sube por encima). */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.84f + 0.03f * lleno + 0.07f * azar(semilla, 1)
            1 -> 0.8f + 0.04f * lleno + 0.08f * azar(semilla, 1)
            else -> 0.8f + 0.03f * lleno + 0.06f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz (todo en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 138f
    /** Donde pisa lo de arriba (el borde de delante de la tapa de la estantería), en anchos desde arriba. */
    const val APOYO = 0.5f
    /** La raya de remate de la pared, en anchos desde arriba (0.14 por encima de lo que pisa, como en el Ideario). */
    const val RAYA = 0.36f
    /** La bombilla de la lámpara colgada, bajo su pantalla de vidrio verde; y el borde de la pantalla. */
    const val LAMPARA_X = 0.565f
    const val LAMPARA_Y = 0.262f
    const val BORDE_Y = 0.25f
    /** La boca de la lámpara de pinza del costado izquierdo, a lo ancho (sobre el costado: no tapa lo guardado). */
    const val PINZA_X = 0.058f
    /** El dial del receptor: a lo ancho, y su altura sobre el pie del lienzo (en anchos). */
    const val DIAL_X = 0.919f
    const val DIAL_Y = 0.268f

    /** El alto del cuerpo (de la corona al pie de la última repisa) con [repisas] repisas, en anchos. */
    fun cuerpo(repisas: Int): Float = repisas * FILA / ANCHO_NOMINAL

    /**
     * La tabla a la que va sujeta la lámpara de pinza: la de encima de la repisa de en medio (alumbra esa repisa); con una sola
     * repisa, -1: va sujeta bajo la cornisa.
     */
    fun tablaPinza(repisas: Int): Int = if (repisas <= 1) -1 else (repisas - 2) / 2

    /** Dónde arde la lámpara de pinza, en anchos desde arriba: la boca, justo bajo el canto de su tabla. */
    fun pinzaY(repisas: Int): Float = CORONA + (tablaPinza(repisas) + 1) * FILA / ANCHO_NOMINAL + 0.004f

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [repisas] repisas y [alto]
     * anchos. [delante]: cuánto está por delante de la pared (0, la pared; 0.03, los cantos; 0.05, las entradas), para que la
     * mancha de la ventana se corra hacia la luz en lo que está delante.
     */
    fun luz(a: AireUrss, x: Float, y: Float, alto: Float, repisas: Int, delante: Float = 0f): LuzUrss {
        val kl = if (a.lampara > 0f) a.lampara * kLampara(x, y) else 0f
        val enPared = a.oscuro && delante < 0.015f
        val lav = if (a.lampara > 0f && enPared) a.lampara * lavadoLampara(x, y) else 0f
        val verde = if (a.lampara > 0f && a.oscuro) a.lampara * verdeLampara(x, y) else 0f
        val kp = if (a.pinza > 0f) a.pinza * kPinza(x, y, repisas) else 0f
        val kd = if (a.dial > 0f) a.dial * kDial(x, y, alto) else 0f
        val k = (kl + kp + kd).coerceIn(0f, 1f)
        val suma = kl + kp + kd + 0.0001f
        val tinte = if (k < 0.001f) PaletaUrss.LLAMA else mezcla(PaletaUrss.LLAMA, PaletaUrss.DIAL, kd / suma * 0.8f)
        val mancha = when {
            !a.noche -> 0f
            a.oscuro -> LUNA_URSS.en(x + delante * 0.6f, y + delante * 0.8f)
            else -> OCASO_URSS.en(x + delante * 0.95f, y + delante * 0.25f)
        }
        // La luz de día, de arriba a la izquierda: más cerca, más; hacia abajo a la derecha, menos.
        val amb = (1.02f - 0.6f * x - 0.08f * y - 0.16f * maxOf(0f, y - 0.62f) / maxOf(0.5f, alto)).coerceIn(0.2f, 1f)
        return a.en(k, tinte, mancha, amb, lav, verde)
    }

    /**
     * Cuánto alumbra la lámpara colgada un punto: por debajo de la pantalla, el cono cálido de la bombilla (con su borde, que en la
     * pared se ve); por encima, muy poco (la pantalla tapa: ahí manda el resplandor verde del vidrio).
     */
    fun kLampara(x: Float, y: Float): Float {
        val dx = x - LAMPARA_X; val dy = y - LAMPARA_Y
        // Por debajo del borde de la sombra de la pantalla (una hipérbola, como la de la lámpara del Ideario), la luz cálida.
        val s = ((y - arcoLampara(x) + 0.03f) / 0.06f).coerceIn(0f, 1f)
        return (1.9f * exp(-3.7f * dist(dx * 0.95f, dy * 0.62f))).coerceAtMost(1f) * (0.14f + 0.86f * s)
    }

    /** El borde de la sombra de la pantalla en la pared: con el vértice bajo la pantalla, baja hacia los lados. */
    fun arcoLampara(x: Float): Float {
        val dx = (x - LAMPARA_X) / 0.15f
        return BORDE_Y + 0.004f + (sqrt(1f + dx * dx) - 1f) * 0.068f
    }

    /** El rebote de la lámpara en la pared: ancho y flojo, sin color (aclara la pintura hacia su propio tono, como en el Ideario). */
    fun lavadoLampara(x: Float, y: Float): Float = (0.62f * exp(-2.4f * dist((x - LAMPARA_X) * 0.85f, (y - LAMPARA_Y - 0.08f) * 0.8f))).coerceAtMost(0.62f)

    /** El resplandor verde del vidrio, por encima del borde de la pantalla. */
    fun verdeLampara(x: Float, y: Float): Float {
        val arriba = ((arcoLampara(x) + 0.01f - y) / 0.03f).coerceIn(0f, 1f)
        return (0.75f * exp(-4.4f * dist((x - LAMPARA_X) * 0.9f, (y - LAMPARA_Y + 0.05f) * 1.1f))).coerceAtMost(0.7f) * arriba
    }

    /**
     * Cuánto alumbra la lámpara de pinza un punto: su pantalla de chapa mira a la derecha y abajo, a la repisa de debajo de su
     * tabla; lo de fuera de su cono recibe bastante menos.
     */
    fun kPinza(x: Float, y: Float, repisas: Int): Float {
        val dx = x - PINZA_X; val dy = y - pinzaY(repisas)
        val largo = dist(dx, dy)
        // El eje del cono, a unos 32 grados por debajo de la horizontal.
        val ax = 0.848f; val ay = 0.53f
        val a = dx * ax + dy * ay
        val perp = abs(dx * ay - dy * ax)
        val cono = if (a <= 0f) 0.22f else (1f - (atan2(perp, a) * 57.2958f - 46f) / 14f).coerceIn(0.22f, 1f)
        return (1.8f * exp(-3.9f * largo)).coerceAtMost(1f) * cono
    }

    /** Cuánto alumbra el dial del receptor un punto: un charco bajo y corto, que sube por el zócalo y la última repisa. */
    fun kDial(x: Float, y: Float, alto: Float): Float {
        val dyD = (alto - y - DIAL_Y)
        val dy = if (dyD > 0f) dyD * 0.75f else dyD * 1.3f
        return (1.7f * exp(-6f * dist((x - DIAL_X) * 1.05f, dy))).coerceAtMost(1f)
    }

    private fun dist(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)

    /**
     * De qué lado le llega la luz a un punto: -1 si de la izquierda, 1 si de la derecha. De día y en los Sueños, de la izquierda (la
     * luz de arriba, la ventana); de noche, de la bombilla que más lo alumbra.
     */
    fun hacia(a: AireUrss, x: Float, y: Float, alto: Float, repisas: Int): Float {
        if (!a.oscuro || a.noche) return -1f
        val kl = kLampara(x, y)
        val kp = kPinza(x, y, repisas)
        val kd = kDial(x, y, alto)
        return when {
            kl >= kp && kl >= kd -> if (x < LAMPARA_X) 1f else -1f
            kp >= kd -> -1f
            else -> if (x < DIAL_X) 1f else -1f
        }
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireUrss(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoUrss(w, h, ch, d.izquierda.right, d.derecha.left, fondo, n, a)

        // ---- debajo de la corona guardada, la fila de la junta (que puede caer a medio píxel): el roble del pie de la cornisa desde
        // el píxel entero de arriba, así no asoma el fondo.
        drawRect(g.luz(w * 0.5f, ch, 0.03f).c(PaletaUrss.ROBLE_OSC), Offset(0f, floor(ch) - 1f), Size(w, 3f))
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaUrss.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(ParedEstanteUrss) { paredArriba(g) }
            with(CoronaEstanteUrss) { sombrasEnPanos(g) }
            with(MuroEstanteUrss) { colgado(g) }
            with(CoronaEstanteUrss) { detras(g) }
            with(ObraEstanteUrss) { cornisa(g) }
            with(CoronaEstanteUrss) { delante(g) }
            conPanosTodoUrss(g, 0.14f, Rect(0f, 0f, w, g.apoyo)) { with(MuroEstanteUrss) { lampara(g) } }
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        // En los Sueños, lo que toca la luz de la ventana (la pared de los huecos, las tablas y los costados) se pinta dos veces: fuera y
        // dentro de sus paños, con el borde neto.
        with(ParedEstanteUrss) { d.repisas.forEachIndexed { k, r -> conPanosTodoUrss(g, 0f, r) { fondoHueco(k, r, d.ocupado.getOrNull(k) ?: r.right, g) } } }
        with(HuecosEstanteUrss) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(ObraEstanteUrss) {
            juntas(g, d)
            d.tablas.forEachIndexed { k, r -> conPanosTodoUrss(g, 0.04f, r) { tabla(k, r, g) } }
            d.placas.forEach { p -> conPanosTodoUrss(g, 0.045f, p.r) { placa(p, medidor, g) } }
            conPanosTodoUrss(g, 0.04f, Rect(0f, g.ch, w, g.fondo)) { costados(g, d) }
        }
        with(PieEstanteUrss) { pie(g) }
        with(ObraEstanteUrss) { pinza(g, d) }
        granoMueble(Rect(0f, ch, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenUrss(v, medidor, oscuro)
}

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]) y cuántas repisas hay ([n]). Y la luz de cada punto.
 */
internal class MarcoUrss(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val n: Int, val a: AireUrss) {
    private val m = MuebleEstanteUrss
    /** Donde pisa lo de arriba: el borde de delante de la tapa de la estantería. */
    val apoyo: Float get() = w * m.APOYO
    /** La raya de la pared. */
    val raya: Float get() = w * m.RAYA

    /** La luz de un punto en píxeles, a [delante] anchos de la pared; con [mancha], cuánto le da la ventana (forzado). */
    fun luz(p: Offset, delante: Float = 0f, mancha: Float? = null): LuzUrss = luz(p.x, p.y, delante, mancha)
    fun luz(x: Float, y: Float, delante: Float = 0f, mancha: Float? = null): LuzUrss {
        val l = m.luz(a, x / w, y / w, h / w, n, delante)
        val forzada = mancha ?: forzar
        return if (forzada == null) l else l.conMancha(forzada)
    }

    /** La mancha de la ventana forzada para todo lo que se pinte mientras no sea null (la usa [conPanosTodoUrss]). */
    var forzar: Float? = null

    /**
     * Hacia dónde cae la sombra de lo que está contra la pared, en píxeles, por [largo]: de día, a la derecha y abajo; con el último
     * sol, larga y casi tendida hacia la derecha; de noche, según la lámpara colgada (que está arriba: hacia abajo y hacia fuera);
     * con la luna, abajo y a la derecha.
     */
    fun arrojada(largo: Float, x: Float = w * 0.3f): Offset = when {
        a.dia -> Offset(0.016f, 0.012f)
        !a.oscuro -> Offset(0.034f, 0.008f)
        !a.noche -> Offset(if (x < w * m.LAMPARA_X) -0.012f else 0.012f, 0.012f)
        else -> Offset(0.012f, 0.018f)
    } * (w * largo)

    /** De qué lado le llega la luz a un punto en píxeles. */
    fun hacia(x: Float, y: Float): Float = m.hacia(a, x / w, y / w, h / w, n)
    /** Dónde arde cada luz, en píxeles. */
    val lampara: Offset get() = Offset(w * m.LAMPARA_X, w * m.LAMPARA_Y)
    val pinza: Offset get() = Offset(w * m.PINZA_X, w * m.pinzaY(n))
    val dial: Offset get() = Offset(w * m.DIAL_X, h - w * m.DIAL_Y)
    /** Los paños del momento (el sol o la luna de los Sueños), o null en el diario. */
    val panos: PanosUrss? get() = when {
        !a.noche -> null
        a.oscuro -> LUNA_URSS
        else -> OCASO_URSS
    }
    /** El corrimiento de los paños para lo que está a [delante] anchos de la pared (hacia la luz). */
    fun corre(delante: Float): Offset = if (a.oscuro) Offset(-delante * 0.6f, -delante * 0.8f) else Offset(-delante * 0.95f, -delante * 0.25f)
}
