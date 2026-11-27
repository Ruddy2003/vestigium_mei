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
import io.github.ruddy2003.vestigium.ui.Letra
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
 * La paleta del estante de Distopía: la del Ideario del mismo tema (el sótano del archivo del ministerio), con lo que le falta a
 * esta pared. Lo frío: la pintura de oficina pública (crema sucio arriba, verde gris abajo, la raya oliva), la chapa gris verdosa
 * de la estantería, el hierro, el linóleo y la luz del tubo. Lo cálido: la manila y el cartón, el papel hueso, el óxido, el yute,
 * la madera de los mangos, el rojo de cartel y del balduque, la bombilla del flexo y las resistencias de la estufa.
 */
internal object PaletaDistopia {
    /** La pared: el crema sucio de arriba, el verde gris institucional de abajo y el oliva de la raya (los del Ideario). */
    val CREMA = Color(0xFFDCD5BA)
    val VERDE = Color(0xFF7E8B76)
    val OLIVA = Color(0xFF545E40)
    /** La chapa pintada de gris verdoso (la del archivero del Ideario), su luz y su sombra; el hierro donde salta la pintura. */
    val ACERO = Color(0xFF8C948A)
    val ACERO_LUZ = Color(0xFFBEC2B4)
    val ACERO_OSC = Color(0xFF4E5550)
    val HIERRO = Color(0xFF3E4246)
    /** El fondo de chapa de los huecos: el mismo gris verdoso, más hondo y algo más frío, para que lo guardado resalte. */
    val FONDO = Color(0xFF545F5D)
    /** La manila de las carpetas y la de las etiquetas (un valor medio), el cartón, el papel hueso y el papel de embalar. */
    val CARPETA = Color(0xFFD6BB80)
    val MANILA = Color(0xFFC7A96C)
    val CARTON = Color(0xFFA5845A)
    val HUESO = Color(0xFFEDE6D0)
    val KRAFT = Color(0xFF9C7A4E)
    /** El rojo de cartel (y del balduque), el burdeos, la baquelita (y la tinta) y el óxido. */
    val CARTEL = Color(0xFFB5372B)
    val BURDEOS = Color(0xFF7C2820)
    val BAQUELITA = Color(0xFF1E2024)
    val OXIDO = Color(0xFF8E5C3C)
    /** El esmalte oliva oscuro de la multicopista (el de la máquina de escribir del Ideario), la madera de los mangos y el yute. */
    val ESMALTE = Color(0xFF3D4434)
    val MADERA = Color(0xFF8A5A34)
    val YUTE = Color(0xFFA88A5C)
    /** El plomo de los precintos. */
    val PLOMO = Color(0xFF8A8E92)
    /** El linóleo del suelo, en losetas de dos verdes grises. */
    val LINO = Color(0xFF6D7869)
    val LINO_OSC = Color(0xFF58624F)
    /** La luz: el tubo fluorescente (crema fría), la bombilla del flexo y de la lámpara, las resistencias de la estufa y la luna. */
    val LUZ_TUBO = Color(0xFFF2F2DA)
    val FLEXO = Color(0xFFFFCF86)
    val BOMBILLA = Color(0xFFFFD98E)
    val RESISTENCIA = Color(0xFFFF7A2E)
    val BRASA = Color(0xFFE0401C)
    val PILOTO = Color(0xFFFF4A3A)
    val LUNA = Color(0xFFD4E0F0)
    /** El filo frío que de noche recorta lo que queda lejos de las bombillas (la luz floja del sótano, gris azulada). */
    val FRIO = Color(0xFF93A6B2)
    /** El aire de la noche del sótano (el gris azulado hondo del Ideario), el de los Sueños, el anochecer y su ocaso. */
    val NOCHE = Color(0xFF121A20)
    val NOCHE_SUENOS = Color(0xFF10161E)
    val ANOCHECER = Color(0xFF3A3352)
    val CREPUSCULO = Color(0xFFF2A070)
    val MELOCOTON = Color(0xFFF6C49C)

    /** El ánimo en tela de encuadernar (de lo malo a lo bueno): rojo de cartel, lino crudo, azul pizarra. */
    val TELAS = listOf(Color(0xFFB02A22), Color(0xFFB7A882), Color(0xFF2B5277))
    /** El ánimo en esmalte de cápsula: rojo, verde gris de oficina, azul pizarra. */
    val ESMALTES = listOf(Color(0xFFB8261E), Color(0xFF93A08A), Color(0xFF285276))
    /** El ánimo en cartón de camisa: cartón rojo, manila, cartón azul. */
    val CAMISAS = listOf(Color(0xFFA62A20), Color(0xFFD2BC8C), Color(0xFF345A80))

    /** La letra del día (la de los rótulos del Ideario) y la del mes, a máquina. */
    val LETRA: FontFamily = LetraExtra.oswald
    val LETRA_MES: FontFamily = Letra.codigo
}

/**
 * El aire del momento en el estante de Distopía: de día (el tubo), de noche (el modo oscuro: arden el flexo, la lámpara de
 * inspección y la estufa) y en los Sueños ([noche]: el último sol por el tragaluz, en claro; la luna, en oscuro).
 */
internal class AireDistopia(val oscuro: Boolean, val noche: Boolean) {
    private val p = PaletaDistopia
    /** Cuánto arde el flexo (de 0 a 1). */
    val flexo: Float get() = if (oscuro && !noche) 1f else if (noche && oscuro) 0.45f else if (noche) 0.4f else 0f
    /** Cuánto arde la lámpara de inspección del costado. */
    val lampara: Float get() = if (oscuro && !noche) 1f else 0f
    /** Cuánto arde la estufa: de noche, al rojo; en los Sueños en oscuro, baja. */
    val estufa: Float get() = if (oscuro && !noche) 1f else if (noche && oscuro) 0.5f else 0f
    /** Si alumbra el tubo (solo de día). */
    val tubo: Boolean get() = !oscuro && !noche
    /** El color en que se hunde lo que no se alumbra. */
    val hondo: Color get() = if (oscuro) (if (noche) p.NOCHE_SUENOS else p.NOCHE) else if (noche) p.ANOCHECER else SOMBRA_MUEBLE

    fun en(k: Float, tinte: Color, mancha: Float, amb: Float, lav: Float = 0f) =
        LuzDistopia(this, k.coerceIn(0f, 1f), tinte, mancha.coerceIn(0f, 1f), amb.coerceIn(0f, 1f), lav.coerceIn(0f, 1f))

    /** El color propio de algo, hundido: oscuro, de su mismo tono (no gris), con el aire del sótano. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.17f + n.red * 0.83f, c.green * 0.17f + n.green * 0.83f, c.blue * 0.17f + n.blue * 0.83f, c.alpha)
    }

    /** El color del ánimo hundido: guarda más de su tono que lo demás, para que las entradas lejanas se sigan distinguiendo. */
    fun hundidoAnimo(c: Color): Color {
        val n = hondo
        val f = if (noche) 0.24f else 0.27f
        return Color(c.red * f + n.red * (1f - f), c.green * f + n.green * (1f - f), c.blue * f + n.blue * (1f - f), c.alpha)
    }

    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> mezcla(SOMBRA_MUEBLE, p.ANOCHECER, 0.55f).copy(alpha = (a * 1.1f).coerceIn(0f, 1f))
        else -> mezcla(SOMBRA_MUEBLE, Color(0xFF243030), 0.3f).copy(alpha = a.coerceIn(0f, 1f))
    }
}

/**
 * La luz de una cosa en un sitio: [k], cuánto le llega de las bombillas (el flexo, la lámpara o la estufa) y de qué color
 * ([tinte]); [mancha], cuánto le da la reja de luz del tragaluz (el último sol en los Sueños en claro, la luna en oscuro); [amb],
 * cuánto le llega del tubo de día (1 arriba a la izquierda, menos hacia abajo a la derecha); [lav], de noche y solo en la pared, la
 * luz del flexo que rebota en el cuarto (ancha, floja y sin color: aclara la pared hacia su propio gris, como en el Ideario). [c]
 * da el color de algo con esa luz, [s] la sombra, [b] el plano de luz, [filo] el borde encendido por una bombilla y [frio] el de la
 * luna.
 */
internal class LuzDistopia(val a: AireDistopia, val k: Float, val tinte: Color, val mancha: Float, val amb: Float, val lav: Float = 0f) {
    private val p = PaletaDistopia
    val luna: Float get() = if (a.oscuro && a.noche) mancha else 0f
    val ocaso: Float get() = if (!a.oscuro && a.noche) mancha else 0f

    /** La misma luz con la mancha del tragaluz forzada a [v]. */
    fun conMancha(v: Float): LuzDistopia = if (!a.noche) this else LuzDistopia(a, k, tinte, v.coerceIn(0f, 1f) * (if (a.oscuro) 0.85f else 1f), amb, lav)

    /** Cuánto enciende la bombilla lo que toca: casi nada lejos y de golpe cerca, para que manden los charcos. */
    private val q: Float get() = (1.1f * k * k).coerceAtMost(0.94f)

    fun c(col: Color): Color = when {
        a.oscuro -> {
            // Lo hundido, aclarado por el rebote del flexo en la pared hacia su propio color apagado (gris verdoso, sin amarillo).
            val base = if (lav > 0.01f) mezcla(a.hundido(col), apagado(col), lav) else a.hundido(col)
            val fuego = mezcla(mezcla(base, alFuego(col), q), tinte, 0.1f * k)
            if (luna > 0.01f) mezcla(fuego, lunaFria(col), 0.7f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(col, (0.34f + 0.3f * (1f - amb) - 0.5f * ocaso).coerceIn(0.08f, 0.62f)), alOcaso(col), ocaso), tinte, 0.3f * k)
        else -> alTubo(col)
    }

    /**
     * Un color a la luz del tubo: cerca (arriba a la izquierda) se aclara hacia su crema fría; lejos se apaga hacia su propio
     * oscuro, un poco frío, sin agrisar. Como el archivero del Ideario, que va de claro a la izquierda a hondo a la derecha.
     */
    private fun alTubo(c: Color): Color =
        if (amb >= 0.55f) mezcla(c, p.LUZ_TUBO, (amb - 0.55f) * 0.62f)
        else {
            val f = (0.55f - amb) * 0.75f
            Color(c.red * (1f - f), c.green * (1f - f * 0.96f), c.blue * (1f - f * 0.9f), c.alpha)
        }

    /** Un color bajo el último sol: melocotón, más claro, sin lavar el color propio. */
    private fun alOcaso(c: Color): Color = mezcla(Color((c.red * 1.16f + 0.08f).coerceAtMost(1f), (c.green * 1.0f + 0.05f).coerceAtMost(1f), (c.blue * 0.86f + 0.02f).coerceAtMost(1f), c.alpha), p.MELOCOTON, 0.26f)

    private fun anochecer(c: Color, hondo: Float): Color =
        Color(c.red * (1f - hondo), c.green * (1f - hondo * 0.95f), c.blue * (1f - hondo * 0.74f), c.alpha)

    /** Un color bajo la bombilla: la luz de color se multiplica por el del objeto y aclara; lo cercano es lo más claro del cuadro. */
    private fun alFuego(c: Color): Color =
        Color((c.red * tinte.red * 1.5f + 0.05f).coerceAtMost(1f), (c.green * tinte.green * 1.26f + 0.02f).coerceAtMost(1f), (c.blue * tinte.blue * 1.3f).coerceAtMost(1f), c.alpha)

    /**
     * Un color bajo la luna: el mismo color, virado a frío y un paso más claro que la noche (no un azul pegado encima): la pared
     * sigue siendo pared, con sus desconchones y su humedad.
     */
    private fun lunaFria(c: Color): Color =
        Color((c.red * 0.29f + 0.02f).coerceAtMost(1f), (c.green * 0.35f + 0.035f).coerceAtMost(1f), (c.blue * 0.41f + 0.07f).coerceAtMost(1f), c.alpha)

    /** Un color apagado por la luz floja del cuarto: su propio color a media luz, con algo del gris azulado del sótano. */
    private fun apagado(c: Color): Color {
        val n = a.hondo
        return Color(c.red * 0.4f + n.red * 0.3f, c.green * 0.42f + n.green * 0.3f, c.blue * 0.42f + n.blue * 0.32f, c.alpha)
    }

    /** La sombra: más franca donde da la mancha (de borde más duro). */
    fun s(alfa: Float): Color = a.sombra(if (!a.oscuro) alfa * (1f + 0.45f * ocaso) else alfa * (1f + 0.4f * luna))

    fun b(alfa: Float): Color = when {
        a.oscuro -> mezcla(tinte, p.LUNA, (luna / (k + luna + 0.001f)).coerceIn(0f, 1f)).copy(alpha = (alfa * 0.95f * maxOf(k, luna * 0.85f)).coerceIn(0f, 1f))
        a.noche -> mezcla(luzMueble(1f), p.CREPUSCULO, 0.5f).copy(alpha = (alfa * (0.3f + 0.55f * maxOf(ocaso, k))).coerceIn(0f, 1f))
        else -> mezcla(luzMueble(1f), p.LUZ_TUBO, 0.6f).copy(alpha = (alfa * (0.55f + 0.5f * amb)).coerceIn(0f, 1f))
    }

    fun filo(alfa: Float): Color = if (k < 0.03f || (!a.oscuro && !a.noche)) Color.Transparent
        else tinte.copy(alpha = (alfa * k * (if (a.oscuro) 1.2f else 0.75f)).coerceIn(0f, 0.92f))

    fun frio(alfa: Float): Color = if (!a.oscuro || luna < 0.03f) Color.Transparent else p.LUNA.copy(alpha = (alfa * luna).coerceIn(0f, 0.85f))

    /** El color del ánimo con la luz que le llega; de noche se apaga algo menos que lo demás y conserva su color. */
    fun animo(base: Color): Color = when {
        a.oscuro -> {
            val qq = maxOf(q, luna * 0.6f)
            val fuego = mezcla(a.hundidoAnimo(base), mezcla(base, alFuego(base), 0.6f), (0.08f + 0.9f * qq).coerceAtMost(0.94f))
            if (luna > 0.01f) mezcla(fuego, lunaFria(base), 0.5f * luna) else fuego
        }
        a.noche -> mezcla(mezcla(anochecer(base, (0.12f + 0.3f * (1f - amb) - 0.25f * ocaso).coerceIn(0.05f, 0.4f)), p.CREPUSCULO, 0.2f * ocaso), tinte, 0.22f * k)
        else -> if (amb >= 0.55f) mezcla(base, p.LUZ_TUBO, (amb - 0.55f) * 0.3f)
            else { val f = (0.55f - amb) * 0.4f; Color(base.red * (1f - f), base.green * (1f - f), base.blue * (1f - f * 0.9f), base.alpha) }
    }

    /**
     * La manila de la etiqueta donde va el día: un valor medio, teñido de noche por la luz que le toca (cálido junto a la
     * bombilla, frío bajo la luna, hundido lejos); nunca tanto que el número deje de leerse.
     */
    fun etiqueta(): Color = when {
        a.oscuro -> mezcla(mezcla(mezcla(p.MANILA, a.hondo, 0.68f), alFuego(mezcla(p.MANILA, Color.White, 0.15f)), (q * 1.4f).coerceAtMost(1f)), lunaFria(p.MANILA), 0.5f * luna)
        a.noche -> mezcla(mezcla(p.MANILA, p.ANOCHECER, 0.22f - 0.12f * maxOf(ocaso, k)), p.CREPUSCULO, 0.14f * ocaso)
        else -> c(p.MANILA)
    }

    /** La tinta sobre un papel de color [fondo]: oscura donde el papel tiene luz; donde se hundió, un gris claro que se lee. */
    fun tinta(fondo: Color): Color {
        if (!a.oscuro) return p.BAQUELITA
        return if (luminanciaDistopia(fondo) < 0.085f) mezcla(Color(0xFF8E8C80), tinte, 0.12f * k) else Color(0xFF0C0D0F)
    }
}

internal fun luminanciaDistopia(c: Color): Float {
    fun ch(v: Float) = if (v <= 0.04045f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
    return 0.2126f * ch(c.red) + 0.7152f * ch(c.green) + 0.0722f * ch(c.blue)
}

/**
 * El dibujo del estante del diario de Distopía (lo usa `EstanteDistopia`, en `BibliotecaFuturo.kt`): la estantería de chapa del
 * archivo, en el sótano del ministerio, contra la misma pared del Ideario (crema sucio arriba, verde gris abajo, la raya oliva).
 * Cornisa prensada con su friso de rejillas y la placa de inventario en blanco; costados con grueso, con su columna de ranuras;
 * baldas de labio con el canal del portaetiquetas; fondo de chapa; zócalo con rejilla. Arriba, la multicopista de tambor, los
 * paquetes de papel, la lata de tinta, la taza con lápices y el flexo; al pie, las cajas de archivo, la estufa eléctrica y el saco
 * de papeles para quemar.
 *
 * De día alumbra el tubo fluorescente de arriba a la izquierda (su regleta asoma en la esquina). De noche arden el flexo (arriba a
 * la derecha, vuelto hacia la primera repisa), la lámpara de inspección colgada del costado izquierdo y la estufa del pie; entre
 * sus charcos, el gris azulado hondo del sótano. En los Sueños en claro, el último sol entra bajo por el tragaluz de la acera
 * (fuera del cuadro, arriba a la izquierda) y pinta su reja en la pared; en oscuro, la luna por el mismo tragaluz.
 *
 * La pared y el fondo de los huecos están en `MuebleEstanteDistopiaPared.kt`, la estantería en `MuebleEstanteDistopiaMueble.kt`,
 * lo de arriba en `MuebleEstanteDistopiaCorona.kt`, lo que llena los huecos en `MuebleEstanteDistopiaHuecos.kt`, el pie en
 * `MuebleEstanteDistopiaPie.kt`, las entradas en `MuebleEstanteDistopiaVolumenes.kt` y los trazos comunes en
 * `MuebleEstanteDistopiaTrazos.kt`.
 */
internal object MuebleEstanteDistopia {
    val LETRA: FontFamily = PaletaDistopia.LETRA

    // ---------------------------------------------------------------- las medidas (no son constantes de compilación a
    // propósito: así `EstanteDistopia` las lee al dibujar)

    val CORONA = 0.62f
    val BASE = 0.27f
    val LADO = 0.07f
    val ALTO_REPISA: Dp = 116.dp
    val TABLA: Dp = 20.dp
    val SEPARA: Dp = 3.dp

    /** Más ancho cuanto más escribiste: crece rápido en las entradas cortas y se llena hacia las 120 palabras, con algo de azar. */
    fun ancho(palabras: Int): Dp {
        val crece = (2.2f * sqrt(palabras.coerceAtLeast(0).toFloat())).coerceAtMost(24f)
        return (24f + crece + Math.floorMod(palabras * 37 + 5, 6)).dp
    }

    /** Cuál de los tres es una entrada: 0, el archivador de palanca; 1, la cápsula del correo neumático; 2, el legajo atado. */
    fun clase(semilla: Int): Int {
        val x = azar(semilla xor 0x3d17, 61)
        return if (x < 0.44f) 0 else if (x < 0.7f) 1 else 2
    }

    /** El archivador, el más alto; el legajo, el más bajo (le asoman los papeles); todos algo más altos cuanto más escribiste. */
    fun alto(semilla: Int, palabras: Int): Float {
        val lleno = (palabras / 800f).coerceIn(0f, 1f)
        return when (clase(semilla)) {
            0 -> 0.82f + 0.03f * lleno + 0.09f * azar(semilla, 1)
            // La cápsula: unas largas y otras cortas (las hay de dos medidas en los tubos).
            1 -> (if (azar(semilla, 3) < 0.35f) 0.7f else 0.8f) + 0.04f * lleno + 0.07f * azar(semilla, 1)
            else -> 0.74f + 0.05f * lleno + 0.1f * azar(semilla, 1)
        }
    }

    // ---------------------------------------------------------------- la luz (todo en anchos del mueble)

    /** El ancho con que un volumen pasa su repisa a anchos del mueble (no sabe cuánto mide el estante): el de un teléfono. */
    const val ANCHO_NOMINAL = 380f
    /** Una repisa con su tabla, en dp. */
    const val FILA = 136f
    /** Donde pisa lo de arriba (la cara de arriba de la cornisa), en anchos desde arriba. */
    const val APOYO = 0.5f
    /** La raya oliva entre las dos pinturas de la pared, en anchos desde arriba. */
    const val RAYA = 0.335f
    /** La boca del flexo, arriba a la derecha, vuelta hacia abajo. */
    const val FLEXO_X = 0.735f
    const val FLEXO_Y = 0.462f
    /** La lámpara de inspección colgada del costado izquierdo: su x y a qué fracción del cuerpo cuelga. */
    const val LAMPARA_X = 0.036f
    const val LAMPARA_T = 0.46f
    /** Las resistencias de la estufa: su x y su altura sobre el pie del lienzo (en anchos). */
    const val ESTUFA_X = 0.42f
    const val ESTUFA_Y = 0.1f

    /** El alto del cuerpo (de la corona al pie de la última repisa) con [repisas] repisas, en anchos. */
    fun cuerpo(repisas: Int): Float = repisas * FILA / ANCHO_NOMINAL

    /** Dónde arde la lámpara, en anchos desde arriba, en un estante de [repisas] repisas. */
    fun lamparaY(repisas: Int): Float = CORONA + cuerpo(repisas) * LAMPARA_T

    /**
     * La luz de un punto del lienzo ([x], [y] en anchos desde arriba a la izquierda) en un estante de [repisas] repisas y [alto]
     * anchos. [delante]: cuánto está por delante de la pared (0, la pared; 0.03, los cantos; 0.05, las entradas), para que la
     * reja del tragaluz se corra hacia la luz en lo que está delante.
     */
    fun luz(a: AireDistopia, x: Float, y: Float, alto: Float, repisas: Int, delante: Float = 0f): LuzDistopia {
        val kf = if (a.flexo > 0f) a.flexo * kFlexo(x, y) else 0f
        // En la pared (y en lo que cuelga de ella), el rebote ancho y sin color del flexo en el cuarto (no es un charco cálido).
        val lav = if (a.flexo > 0f && a.oscuro && delante < 0.015f) a.flexo * lavadoFlexo(x, y) else 0f
        val kl = if (a.lampara > 0f) a.lampara * (1.9f * exp(-6.6f * dist(x - LAMPARA_X, (y - lamparaY(repisas)) * 0.95f))).coerceAtMost(1f) else 0f
        val ke = if (a.estufa > 0f) a.estufa * kEstufa(x, y, alto) else 0f
        val k = (kf + kl + ke).coerceIn(0f, 1f)
        val suma = kf + kl + ke + 0.0001f
        val tinte = if (k < 0.001f) PaletaDistopia.FLEXO
            else mezcla(mezcla(PaletaDistopia.FLEXO, PaletaDistopia.BOMBILLA, kl / suma), PaletaDistopia.RESISTENCIA, ke / suma * 0.72f)
        val mancha = when {
            !a.noche -> 0f
            a.oscuro -> lunaDistopia(x + delante * 0.55f, y + delante * 0.85f)
            else -> ocasoDistopia(x + delante * 0.95f, y + delante * 0.25f)
        }
        // El tubo, fuera del cuadro arriba a la izquierda: más luz cerca, menos hacia abajo y a la derecha.
        val amb = (1.02f - 0.62f * x - 0.07f * y - 0.18f * maxOf(0f, y - 0.62f) / maxOf(0.5f, alto)).coerceIn(0.18f, 1f)
        return a.en(k, tinte, mancha, amb, lav)
    }

    /** Cuánto alumbra el flexo un punto: la pantalla manda la luz hacia abajo; lo de encima de la boca recibe mucho menos. */
    fun kFlexo(x: Float, y: Float): Float {
        val tapa = (1f - 3.2f * (FLEXO_Y - y).coerceAtLeast(0f)).coerceIn(0.32f, 1f)
        // Hacia abajo y hacia la izquierda (adonde mira la pantalla: la multicopista y la primera repisa) llega más lejos.
        val dy = (y - FLEXO_Y) * (if (y > FLEXO_Y) 0.6f else 0.85f)
        val dx = (x - FLEXO_X) * (if (x < FLEXO_X) 0.74f else 1.05f)
        return (1.95f * exp(-5.5f * dist(dx, dy))).coerceAtMost(1f) * tapa
    }

    /**
     * El rebote del flexo en la pared: ancho y flojo, sin color (como el halo de la pared del Ideario, que aclara la pintura hacia su
     * propio gris y no la vuelve amarilla). Se apaga hacia la izquierda y hacia arriba.
     */
    fun lavadoFlexo(x: Float, y: Float): Float = (0.72f * exp(-2.3f * dist((x - FLEXO_X) * 0.85f, (y - FLEXO_Y + 0.06f) * 0.95f))).coerceAtMost(0.72f)

    /** Cuánto alumbra la estufa un punto (sin contar si arde): un charco que sube por el zócalo y la última repisa. */
    fun kEstufa(x: Float, y: Float, alto: Float, oscuro: Boolean = true): Float {
        val dyE = (alto - y - ESTUFA_Y).coerceAtLeast(0f)
        return ((if (oscuro) 2.1f else 1.9f) * exp(-6.2f * dist((x - ESTUFA_X) * 1.05f, dyE * 0.66f))).coerceAtMost(1f)
    }

    private fun dist(dx: Float, dy: Float) = sqrt(dx * dx + dy * dy)

    /**
     * De qué lado le llega la luz a un punto: -1 si de la izquierda, 1 si de la derecha. De día y en los Sueños, del tubo o del
     * tragaluz (izquierda); de noche, de la bombilla que más lo alumbra.
     */
    fun hacia(a: AireDistopia, x: Float, y: Float, alto: Float, repisas: Int): Float {
        if (!a.oscuro || a.noche) return -1f
        val kf = kFlexo(x, y)
        val kl = 1.9f * exp(-5.4f * dist(x - LAMPARA_X, (y - lamparaY(repisas)) * 0.74f))
        val ke = kEstufa(x, y, alto) * 1.05f
        return when {
            kf >= kl && kf >= ke -> if (x < FLEXO_X) 1f else -1f
            kl >= ke -> -1f
            else -> if (x < ESTUFA_X) 1f else -1f
        }
    }

    // ---------------------------------------------------------------- el estante

    fun DrawScope.estante(d: DisposicionEstante, medidor: TextMeasurer, oscuro: Boolean, noche: Boolean) {
        val w = size.width; val h = size.height
        if (w < 4f || h < 4f) return
        val a = AireDistopia(oscuro, noche)
        val ch = d.corona.bottom
        val n = d.repisas.size
        val fondo = if (n > 0) d.tablas.last().bottom else ch
        val g = MarcoDistopia(w, h, ch, d.izquierda.right, d.derecha.left, fondo, n, a)

        // ---- debajo de la corona guardada, la fila de la junta (que puede caer a medio píxel): la chapa del pie de la cornisa
        // desde el píxel entero de arriba, así no asoma el fondo.
        drawRect(g.luz(w * 0.5f, ch, 0.03f).c(PaletaDistopia.ACERO_OSC), Offset(0f, floor(ch) - 1f), Size(w, 3f))
        val clave = "${w.toInt()}|${ch.toInt()}|$oscuro|$noche|$density"
        CapaDistopia.pegar(this, if (noche) 1 else 0, clave, ch) {
            with(ParedEstanteDistopia) { paredArriba(g) }
            with(CoronaEstanteDistopia) { detras(g) }
            with(MuebleDistopia) { cornisa(g) }
            with(CoronaEstanteDistopia) { delante(g) }
            granoMueble(Rect(0f, 0f, w, ch), oscuro)
        }
        if (n == 0) return
        with(ParedEstanteDistopia) { d.repisas.forEachIndexed { k, r -> fondoHueco(k, r, g) } }
        with(HuecosEstanteDistopia) {
            d.repisas.forEachIndexed { k, r ->
                val oc = d.ocupado.getOrNull(k) ?: return@forEachIndexed
                hueco(k, r, oc, g)
            }
        }
        with(MuebleDistopia) {
            juntas(g, d)
            d.tablas.forEachIndexed { k, r -> balda(k, r, g) }
            d.placas.forEach { p -> placa(p, medidor, g) }
            costados(g, d)
        }
        with(PieEstanteDistopia) { pie(g) }
        with(MuebleDistopia) { lampara(g) }
        granoMueble(Rect(0f, ch, w, h), oscuro)
    }

    fun DrawScope.volumen(v: Volumen, medidor: TextMeasurer, oscuro: Boolean) = volumenDistopia(v, medidor, oscuro)
}

// ---------------------------------------------------------------- la reja de luz del tragaluz

/**
 * Una reja de luz: el paralelogramo con esquina ([ox], [oy]) y lados ([ux], [uy]) (a lo largo, [cols] paños) y ([vx], [vy]) (a lo
 * alto), con los barrotes de [barra] (en fracción de un paño). De 0 a 1, con el borde neto.
 */
internal class RejaDistopia(val ox: Float, val oy: Float, val ux: Float, val uy: Float, val vx: Float, val vy: Float,
                            val cols: Int, val barra: Float) {
    private val det = ux * vy - uy * vx

    fun en(x: Float, y: Float): Float {
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val dx = x - ox; val dy = y - oy
        val s = (dx * vy - dy * vx) / det
        val t = (ux * dy - uy * dx) / det
        if (s <= 0f || s >= 1f || t <= 0f || t >= 1f) return 0f
        val cs = s * cols
        val fs = cs - floor(cs)
        val b = barra * 0.5f
        if (fs < b || fs > 1f - b) return 0f
        // El borde de arriba y el de abajo, algo más tenues (la luz entra por un hueco con grueso).
        return (0.7f + 0.3f * (minOf(t, 1f - t) * 8f).coerceAtMost(1f))
    }

    /** Los paños, como camino en píxeles ([w], el ancho), corridos [dx], [dy] anchos. */
    fun camino(w: Float, dx: Float = 0f, dy: Float = 0f): Path = Path().apply {
        val b = barra * 0.5f
        for (c in 0 until cols) {
            val s0 = (c + b) / cols; val s1 = (c + 1 - b) / cols
            fun p(s: Float, t: Float) = Offset((ox + ux * s + vx * t + dx) * w, (oy + uy * s + vy * t + dy) * w)
            val a = p(s0, 0f); val bb = p(s1, 0f); val c2 = p(s1, 1f); val d = p(s0, 1f)
            moveTo(a.x, a.y); lineTo(bb.x, bb.y); lineTo(c2.x, c2.y); lineTo(d.x, d.y); close()
        }
    }
}

/**
 * El último sol de los Sueños en claro: entra bajo y largo por el tragaluz, casi tendido, con sus cuatro barrotes; cae alto, en la
 * pared crema, por encima de lo que hay sobre la estantería (que no la toca): solo la cruza lo que cuelga de la pared (la regleta,
 * la tablilla, el correo neumático, los tubos), que se pinta a su modo y le echa su sombra.
 */
internal val OCASO_DISTOPIA = RejaDistopia(-0.06f, 0.072f, 0.97f, 0.1f, 0.045f, 0.118f, 5, 0.2f)

/** La luna de los Sueños en oscuro: más chica, en la pared alta entre la tablilla y el correo neumático. */
internal val LUNA_DISTOPIA = RejaDistopia(0.165f, 0.098f, 0.27f, 0.055f, 0.03f, 0.105f, 4, 0.22f)

internal fun ocasoDistopia(x: Float, y: Float): Float = OCASO_DISTOPIA.en(x, y)
internal fun lunaDistopia(x: Float, y: Float): Float = LUNA_DISTOPIA.en(x, y)

/**
 * Las medidas que comparten las piezas, en píxeles: el ancho [w] y el alto [h] del lienzo, el pie de la corona [ch], el borde de
 * dentro de los costados ([x0], [x1]), el pie de la última tabla ([fondo]) y cuántas repisas hay ([n]). Y la luz de cada punto.
 */
internal class MarcoDistopia(val w: Float, val h: Float, val ch: Float, val x0: Float, val x1: Float, val fondo: Float, val n: Int, val a: AireDistopia) {
    private val m = MuebleEstanteDistopia
    /** Donde pisa lo de arriba: la cara de arriba de la cornisa. */
    val apoyo: Float get() = w * m.APOYO
    /** La luz de un punto en píxeles, a [delante] anchos de la pared; con [mancha], cuánto le da la reja del tragaluz (forzado). */
    fun luz(p: Offset, delante: Float = 0f, mancha: Float? = null): LuzDistopia = luz(p.x, p.y, delante, mancha)
    fun luz(x: Float, y: Float, delante: Float = 0f, mancha: Float? = null): LuzDistopia {
        val l = m.luz(a, x / w, y / w, h / w, n, delante)
        val forzada = mancha ?: forzar
        return if (forzada == null) l else l.conMancha(forzada)
    }

    /**
     * La mancha del tragaluz forzada para todo lo que se pinte mientras no sea null (la usa [conRejaTodo]: 0 fuera de la reja, 1
     * dentro), así lo que cuelga de la pared se pinta dos veces sin tener que pasar la mancha pieza por pieza.
     */
    var forzar: Float? = null

    /**
     * Hacia dónde cae la sombra de lo que cuelga de la pared, en píxeles, por [largo]: de día, a la derecha y abajo (el tubo); con
     * el último sol, larga y casi tendida hacia la derecha; de noche, corta; con la luna, abajo y a la derecha.
     */
    fun arrojada(largo: Float): Offset = when {
        !a.noche && !a.oscuro -> Offset(0.014f, 0.011f)
        !a.oscuro -> Offset(0.032f, 0.008f)
        !a.noche -> Offset(0.005f, 0.008f)
        else -> Offset(0.012f, 0.018f)
    } * (w * largo)
    /** De qué lado le llega la luz a un punto en píxeles. */
    fun hacia(x: Float, y: Float): Float = m.hacia(a, x / w, y / w, h / w, n)
    /** Dónde arde cada luz, en píxeles. */
    val flexo: Offset get() = Offset(w * m.FLEXO_X, w * m.FLEXO_Y)
    val lampara: Offset get() = Offset(w * m.LAMPARA_X, w * m.lamparaY(n))
    val estufa: Offset get() = Offset(w * m.ESTUFA_X, h - w * m.ESTUFA_Y)
    /** La reja del momento (el sol o la luna de los Sueños), o null en el diario. */
    val reja: RejaDistopia? get() = when {
        !a.noche -> null
        a.oscuro -> LUNA_DISTOPIA
        else -> OCASO_DISTOPIA
    }
    /** El corrimiento de la reja para lo que está a [delante] anchos de la pared (hacia la luz). */
    fun corre(delante: Float): Offset = if (a.oscuro) Offset(-delante * 0.55f, -delante * 0.85f) else Offset(-delante * 0.95f, -delante * 0.25f)
}

/**
 * Dibuja [pieza] con la reja apagada y, recortada a la reja del tragaluz para lo que está a [delante] anchos de la pared, otra vez
 * con la reja encendida: así la luz pinta lo que toca, con borde neto. Sin reja (el diario), una sola vez.
 */
internal fun DrawScope.conRejaDistopia(g: MarcoDistopia, delante: Float, zona: Rect? = null, pieza: DrawScope.(Float?) -> Unit) {
    val e = g.reja
    if (e == null) { pieza(null); return }
    pieza(0f)
    val c = g.corre(delante)
    val camino = e.camino(g.w, c.x, c.y)
    val b = camino.getBounds()
    if (b.isEmpty) return
    if (zona != null && !b.overlaps(zona)) return
    clipRect(b.left, b.top, b.right, b.bottom) { clipPath(camino) { pieza(1f) } }
}

/**
 * Como [conRejaDistopia], pero sin pasar la mancha a [pieza]: la fija en el marco ([MarcoDistopia.forzar]) mientras pinta, para lo
 * que cuelga de la pared (la regleta, la tablilla, los tubos, el correo neumático), que así se pinta igual por dentro y por fuera
 * de la reja, con su luz y con la sombra que le echa a la pared.
 */
internal fun DrawScope.conRejaTodo(g: MarcoDistopia, delante: Float, zona: Rect, pieza: DrawScope.() -> Unit) {
    val e = g.reja
    if (e == null) { pieza(); return }
    g.forzar = 0f
    pieza()
    val c = g.corre(delante)
    val camino = e.camino(g.w, c.x, c.y)
    val b = camino.getBounds()
    if (!b.isEmpty && b.overlaps(zona)) {
        g.forzar = 1f
        clipRect(b.left, b.top, b.right, b.bottom) { clipPath(camino) { pieza() } }
    }
    g.forzar = null
}

/**
 * Lo de arriba del estante, pintado una sola vez por tamaño y modo en una imagen que luego se pega (en dos ranuras: el diario y
 * los Sueños). La imagen llega hasta el píxel entero de abajo y se pega en un píxel entero: su última fila, que puede ser medio
 * píxel, deja ver lo que se pintó debajo (el pie de la cornisa), no el fondo.
 */
internal object CapaDistopia {
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

/** Las escalas del ánimo de Distopía, la de día y la de noche (bajo, medio, alto). */
private val TONOS_DISTOPIA: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.DISTOPIA, false) to juegoTonos(TemaId.DISTOPIA, true) }

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaDistopia(t: Color, escala: List<Color>): Float? {
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
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa de claro a casi
 * negro); aquí la entrada es un objeto del cuarto: conserva su color de día y la luz lo apaga con la distancia. Se ubica el color
 * en la escala de noche y se toma el mismo punto en la de día; si no está, queda igual.
 */
internal fun tonoDeDiaDistopia(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_DISTOPIA
    val x = enEscalaDistopia(t, noche) ?: return t
    return if (x < 0.5f) mezcla(dia[0], dia[1], x * 2) else mezcla(dia[1], dia[2], (x - 0.5f) * 2)
}

/**
 * El material del ánimo: se ubica el color (de día) en la escala del tema y se toma el mismo punto en [materiales] (tela, esmalte
 * o cartón, de lo malo a lo bueno). Si el color no está en la escala, queda igual.
 */
internal fun materialDelAnimoDistopia(t: Color, materiales: List<Color>): Color {
    val x = enEscalaDistopia(t, TONOS_DISTOPIA.first) ?: return t
    val c = if (x < 0.5f) mezcla(materiales[0], materiales[1], x * 2) else mezcla(materiales[1], materiales[2], (x - 0.5f) * 2)
    // Más color hacia los extremos (el rojo y el azul); el de en medio (el lino, el gris, la manila) queda como es.
    return vivoDistopia(c, 1f + 0.25f * (kotlin.math.abs(x - 0.5f) * 2.5f).coerceAtMost(1f))
}

/** [c] con más color: se aleja de su gris (del mismo valor) en [f]; así los tonos de en medio no se lavan. */
internal fun vivoDistopia(c: Color, f: Float): Color {
    val g = 0.3f * c.red + 0.59f * c.green + 0.11f * c.blue
    return Color((g + (c.red - g) * f).coerceIn(0f, 1f), (g + (c.green - g) * f).coerceIn(0f, 1f), (g + (c.blue - g) * f).coerceIn(0f, 1f), c.alpha)
}
