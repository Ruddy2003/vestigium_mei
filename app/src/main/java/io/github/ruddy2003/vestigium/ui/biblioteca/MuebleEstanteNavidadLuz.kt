package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Navidad: la cocina de la casa en diciembre, la misma del Ideario (`IdeasNavidad`). La pared pintada
 * de verdoso apagado con su cenefa de azulejo verde y el azulejo crema; la alacena de pino pintada de rojo de almagre, con los
 * filetes verdes de la misma mano que el aparador y el pino que asoma donde se gastó; el machihembrado salvia del fondo; el
 * latón viejo, el cobre, el hierro de la cocina, el barro del suelo; el abeto, las esferas de vidrio, el papel, la lana. Dos
 * temperaturas: lo cálido es el almagre, el pino, el latón, el cobre y la lumbre; lo frío, el salvia, el verde, la nieve de la
 * ventana y la noche verde azulada de la cocina.
 */
internal object PaletaNavidad {
    /** La pintura de la pared (la del Ideario), su luz, su sombra y el vaho. */
    val PINTURA = Color(0xFFD5D0B0)
    val PINTURA_LUZ = Color(0xFFEAE6CB)
    val PINTURA_SOMBRA = Color(0xFFA8A587)
    /** El azulejo crema, su junta y el verde de la cenefa. */
    val AZULEJO = Color(0xFFEFE6CF)
    val JUNTA = Color(0xFFB8AE92)
    val FILETE = Color(0xFF2F5B40)
    val FILETE_LUZ = Color(0xFF4F7E5C)
    /** El rojo de almagre de la alacena: su cara, su luz, su sombra y lo hondo. */
    val ALMAGRE = Color(0xFF983229)
    val ALMAGRE_LUZ = Color(0xFFC25A42)
    val ALMAGRE_OSC = Color(0xFF63201B)
    val ALMAGRE_HONDO = Color(0xFF34100E)
    /** El pino que asoma, el de la cuchara y el del taburete. */
    val PINO = Color(0xFFB9874F)
    val PINO_LUZ = Color(0xFFDDB57C)
    val PINO_OSC = Color(0xFF6A4528)
    /** El salvia del machihembrado del fondo: su cara, su luz, su sombra y lo hondo. */
    val SALVIA = Color(0xFFA2B08E)
    val SALVIA_LUZ = Color(0xFFBDC8A6)
    val SALVIA_OSC = Color(0xFF6F8166)
    val SALVIA_HONDO = Color(0xFF44543F)
    /** La pintura crema del marco de la ventana y las tarjetas; el papel; la cartulina de las etiquetas (de valor medio). */
    val CREMA = Color(0xFFEDE0BF)
    val PAPEL = Color(0xFFF5ECD8)
    val CARTULINA = Color(0xFFD5BD8C)
    val CARTULINA_OSC = Color(0xFFA68B5A)
    val TINTA = Color(0xFF2A1A12)
    /** El rojo del vichy, el lazo y las cintas. */
    val CARMIN = Color(0xFFAC2E27)
    /** El latón viejo, su luz y su sombra. */
    val LATON = Color(0xFFC39A4A)
    val LATON_LUZ = Color(0xFFF1D68E)
    val LATON_OSC = Color(0xFF7C5C22)
    /** El cobre del molde y del hervidor. */
    val COBRE = Color(0xFFAE5530)
    val COBRE_LUZ = Color(0xFFEAA070)
    val COBRE_OSC = Color(0xFF5E2812)
    /** El abeto: sus agujas en tres verdes y lo hondo entre las ramas. */
    val ABETO = Color(0xFF2E5C3A)
    val ABETO_LUZ = Color(0xFF5F8C55)
    val ABETO_OSC = Color(0xFF183424)
    val ABETO_HONDO = Color(0xFF0E2016)
    /** Las esferas de vidrio: roja, dorada, azul y plateada; el azul de lata de las bombillas apagadas. */
    val VIDRIO_ROJO = Color(0xFFB3242C)
    val VIDRIO_ORO = Color(0xFFD2A548)
    val VIDRIO_AZUL = Color(0xFF2E4D76)
    val VIDRIO_PLATA = Color(0xFFC3CAD0)
    /** La loza de la jarra, el hierro de la cocina y el barro del suelo. */
    val LOZA = Color(0xFFF2EEE2)
    val HIERRO = Color(0xFF2B2A2E)
    val HIERRO_LUZ = Color(0xFF5C5B62)
    val BARRO = Color(0xFFB4664A)
    /** La leña: la corteza, la madera cortada y el abedul. */
    val CORTEZA = Color(0xFF5A4030)
    val LENA = Color(0xFFD9B27A)
    val ABEDUL = Color(0xFFE6E0CF)
    /** La lana roja, la crema y la verde de los ovillos y la alfombra. */
    val LANA_ROJA = Color(0xFFB23A30)
    val LANA_CREMA = Color(0xFFEDE2C8)
    val LANA_VERDE = Color(0xFF3C6A48)
    /** La naranja y los clavos. */
    val NARANJA = Color(0xFFDD8730)
    /** El cielo y la nieve de la ventana: de día, de tarde de invierno; de noche, el de afuera. */
    val CIELO = Color(0xFFE6ECE8)
    val CIELO_ALTO = Color(0xFFAFC4D0)
    val NIEVE = Color(0xFFF6F7F8)
    val NIEVE_SOMBRA = Color(0xFFBFCAD6)
    val CIELO_NOCHE = Color(0xFF152A40)
    val CIELO_NOCHE_ALTO = Color(0xFF0A1528)
    val NIEVE_NOCHE = Color(0xFF51677F)
    /** La noche de la cocina (la del Ideario) y la de los Sueños; lo hondo del almagre de noche. */
    val NOCHE = Color(0xFF0D1C20)
    val NOCHE_SUENOS = Color(0xFF0A1A26)
    val VINO = Color(0xFF1A0709)
    /** El filo frío que deja la nieve de la ventana de noche. */
    val NIEVE_FILO = Color(0xFF8DB2BC)
    /** La sombra del ocaso de los Sueños claros (violeta), su luz y el sol de la tarde. */
    val SOMBRA_OCASO = Color(0xFF402A62)
    val OCASO = Color(0xFFF2B286)
    val SOL = Color(0xFFFFF0C8)
    /** La llama de la vela (la del Ideario), su corazón, la lumbre de la cocina, la luna y su roce. */
    val LLAMA = Color(0xFFFFC66E)
    val FUEGO = Color(0xFFFFF4D6)
    val LUMBRE = Color(0xFFFF8E3C)
    val LUNA = Color(0xFFE6EEF8)
    val LUNA_RAS = Color(0xFFA2BCE0)
    /** La sombra fría del día (la del vidrio, el salvia y la nieve). */
    val FRIO = Color(0xFF2A3E44)
    /** Las bombillas encendidas (las del Ideario): roja, verde, azul y ámbar; y su vidrio de día. */
    val BOMBILLAS = listOf(Color(0xFFFF6A5A), Color(0xFF7FE08A), Color(0xFF8DB8FF), Color(0xFFFFCF5A))
    val BOMBILLAS_DIA = listOf(Color(0xFFAC2E27), Color(0xFF2F5B40), Color(0xFF2E4D76), Color(0xFFC39A4A))

    val LETRA: FontFamily = LetraExtra.calida
}

/**
 * El aire del momento en el estante de Navidad: de día (el sol de la tarde que entra por la ventana), de noche (el modo oscuro:
 * arden la vela de la cornisa, la de un hueco y la lumbre de la cocina de hierro) y en los Sueños ([noche]: el ocaso en claro, la
 * luna en oscuro). De noche es la misma cocina que el Ideario a la misma hora: lo que no alumbra una llama se hunde en su verde
 * azulado hondo (la receta del Ideario: el color propio mezclado con esa noche, menos cuanto más luz le llega, y el calor de la
 * llama encima); la madera roja de la alacena ([madera]) se hunde en un vino muy oscuro, pero sus sombras y sus cantos fríos son de
 * ese verde azulado. En los Sueños oscuros, lo mismo hacia el azul verdoso de la casa con luna.
 */
internal class AireNavidad(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false, val madera: Boolean = false) {
    private val p = PaletaNavidad
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche
    /** La noche de la casa: la verde azulada del Ideario (en los Sueños, la azul verdosa de la luna). */
    val nocheCasa: Color get() = if (noche) p.NOCHE_SUENOS else p.NOCHE
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && madera -> if (noche) mezcla(p.VINO, p.NOCHE_SUENOS, 0.75f) else p.VINO
        oscuro -> nocheCasa
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color de algo a la luz de la vela: alumbra en naranja, así que el azul y el verde bajan más que el rojo. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.86f, c.blue * 0.6f, c.alpha)

    /** El color al anochecer de los Sueños claros: el mismo cuarto ya en penumbra, apenas entibiado (el violeta va en la sombra). */
    private fun alOcaso(c: Color): Color =
        if (cuerpo) Color(c.red * 0.8f, c.green * 0.74f, c.blue * 0.76f, c.alpha)
        else Color(c.red * 0.85f, c.green * 0.78f, c.blue * 0.74f, c.alpha)

    /**
     * De noche: el color propio hundido en [hondo] (casi del todo lejos de la luz) y, cuanto más cerca de la llama, más su color,
     * virado a la llama, con el calor encima. En el cuerpo de la alacena la luz cae algo más deprisa, para que entre una llama y otra
     * se hunda de verdad.
     */
    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        val f = Math.pow(k.toDouble(), if (madera) 1.75 else if (cuerpo) 1.35 else 1.0).toFloat()
        val base = mezcla(c, hondo, (0.9f - 0.74f * f).coerceIn(0.12f, 0.92f))
        val caliente = mezcla(base, aLaLlama(base), 0.55f * f)
        return mezcla(caliente, p.LLAMA, 0.15f * f)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la mancha de la ventana ([f], de 0 a 1): [c] es el material y [base], su color sin esa luz. De día, el sol de la
     * tarde, cálido; al ocaso, el melocotón; con la luna, el mismo material virado al frío y un paso más claro. Es el color de la
     * superficie bajo esa luz, opaco: no un velo encima.
     */
    fun sol(c: Color, base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        return when {
            oscuro -> {
                // La luna: el color de lo que toca, virado al azul verdoso y un paso más claro (sin irse al blanco).
                val frio = Color((base.red * 1.15f + 0.03f).coerceAtMost(1f), (base.green * 1.3f + 0.06f).coerceAtMost(1f), (base.blue * 1.45f + 0.09f).coerceAtMost(1f), c.alpha)
                mezcla(base, frio, k)
            }
            noche -> {
                val l = Color((base.red * 1.2f + 0.06f).coerceAtMost(1f), (base.green * 1.1f + 0.05f).coerceAtMost(1f), (base.blue * 1.02f + 0.04f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF6C29C), 0.3f), k)
            }
            else -> {
                val l = Color((base.red * 1.28f + 0.05f).coerceAtMost(1f), (base.green * 1.2f + 0.04f).coerceAtMost(1f), (base.blue * 1.06f + 0.02f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFFFE3A2), 0.18f), k)
            }
        }
    }

    /** El color de [c] con la luz del momento ([f] como en [dia]) y, si [lit] pasa de 0, con la mancha [m] de la ventana encima. */
    fun en(c: Color, f: Float, cerca: Float, lit: Float, m: ManchaNavidad?): Color {
        val base = dia(c, f, cerca)
        return if (lit > 0f && m != null) sol(c, base, m.fuerza * lit) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el verde azulado de la casa (también en la madera roja). */
    fun sombra(a: Float): Color =
        if (oscuro) nocheCasa.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del vidrio, el salvia y la nieve); de noche, la misma que las demás. */
    fun frio(a: Float): Color = if (lumbre) sombra(a) else p.FRIO.copy(alpha = a.coerceIn(0f, 1f))
    /** La luz crema del día; al ocaso, la naranja; de noche, casi nada. */
    fun claro(a: Float): Color = if (oscuro) luzMueble(a * 0.07f) else if (noche) p.OCASO.copy(alpha = (a * 0.5f).coerceIn(0f, 1f)) else luzMueble(a)
    /** El brillo de lo que mira a la luz: de día crema; al ocaso, naranja; de noche, el filo de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color =
        if (!oscuro) (if (noche) mezcla(luzMueble(1f), p.OCASO, 0.55f).copy(alpha = (a * 0.75f).coerceIn(0f, 1f)) else luzMueble(a))
        else p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
    /** El filo de la llama en lo que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = if (oscuro) p.LLAMA.copy(alpha = a.coerceIn(0f, 1f)) else if (noche) p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f)) else Color.Transparent
    /** El roce frío de la luna (en los Sueños oscuros, más). */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA_RAS.copy(alpha = (if (noche) a * 1.4f else a).coerceIn(0f, 1f)) else Color.Transparent
    /** El filo frío de la nieve de la ventana (de noche) o de la luna (en los Sueños oscuros) en los cantos que miran a la izquierda. */
    fun filoFrio(a: Float): Color = if (!oscuro) Color.Transparent else (if (noche) p.LUNA_RAS else p.NIEVE_FILO).copy(alpha = a.coerceIn(0f, 1f))

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (lo rojo sigue rojo, hundido en el
     * verde azulado lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(t, hondo, 0.8f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.3).toFloat()).coerceAtMost(1f))
    }

    /**
     * La cartulina donde va el día: ámbar cerca de la llama y, lejos, el papel en la sombra de la cocina (más frío y apagado), sin
     * bajar del 4.5 a 1 con la tinta casi negra (una luminancia de 0.2).
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = if (noche) Color(0xFF84796A) else Color(0xFF8A795E)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.7f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.5).toFloat().coerceAtMost(1f))
    }
}

/**
 * Cuánta luz fría de la ventana llega de noche a ([fx], [fy]) (anchos): la nieve de afuera (o la luna, en los Sueños) entra por
 * arriba a la izquierda y filetea lo que mira hacia allá; se apaga hacia abajo y hacia la derecha.
 */
internal fun frioVentanaNavidad(fx: Float, fy: Float): Float {
    val dx = fx - 0.02f; val dy = (fy - 0.3f) * 0.45f
    return (1f - kotlin.math.sqrt(dx * dx + dy * dy) / 1.05f).coerceIn(0f, 1f)
}

/**
 * La mancha de luz que entra por la ventana de la cocina (arriba a la izquierda) y cae en la pared y en lo que tiene delante: los
 * cuatro vidrios de las dos hojas, tendidos por el sol bajo de la tarde, con la sombra del parteluz y del travesaño (y, dentro,
 * la de la rama nevada de afuera y la de la naranja colgada). [q] es el cuadrilátero de la mancha en el plano de la pared, en anchos
 * (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda); lo que está más adelante (la cornisa, el abeto) la recibe
 * corrida [FRENTE]. [fuerza] es lo que aclara.
 */
internal class ManchaNavidad(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** Un punto de la mancha: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en anchos, corrido [corrido]. */
    fun en(fx: Float, fy: Float, corrido: Float = 0f): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + corrido, ty + (by - ty) * fy)
    }

    /** Un vidrio ([fx0]..[fx1], [fy0]..[fy1]) proyectado, en un lienzo de ancho [w]. */
    private fun Path.vidrio(w: Float, fx0: Float, fx1: Float, fy0: Float, fy1: Float, corrido: Float) {
        val a = en(fx0, fy0, corrido); val b = en(fx1, fy0, corrido); val c = en(fx1, fy1, corrido); val d = en(fx0, fy1, corrido)
        moveTo(a.x * w, a.y * w); lineTo(b.x * w, b.y * w); lineTo(c.x * w, c.y * w); lineTo(d.x * w, d.y * w); close()
    }

    /** Los cuatro vidrios proyectados en un lienzo de ancho [w], con la sombra del parteluz y del travesaño; [crece] los agranda (la penumbra). */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f): Path {
        val p = Path()
        val parteluz = 0.075f; val travesano = 0.06f
        for (col in 0 until 2) {
            val x0 = if (col == 0) 0f else 0.5f + parteluz / 2
            val x1 = if (col == 0) 0.5f - parteluz / 2 else 1f
            for (fila in 0 until 2) {
                val y0 = if (fila == 0) 0f else 0.5f + travesano / 2
                val y1 = if (fila == 0) 0.5f - travesano / 2 else 1f
                p.vidrio(w, x0 - crece, x1 + crece, y0 - crece, y1 + crece, corrido)
            }
        }
        return p
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 dentro de un vidrio, 0 fuera. */
    fun toca(x: Float, y: Float, corrido: Float = 0f): Float {
        val ax = q[0] + corrido; val ay = q[1]
        val ux = q[2] - q[0]; val uy = q[3] - q[1]
        val vx = q[6] - q[0]; val vy = q[7] - q[1]
        val det = ux * vy - uy * vx
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val px = x - ax; val py = y - ay
        val fx = (px * vy - py * vx) / det
        val fy = (ux * py - uy * px) / det
        if (fx < 0f || fx > 1f || fy < 0f || fy > 1f) return 0f
        if (fx > 0.4625f && fx < 0.5375f) return 0f
        if (fy > 0.47f && fy < 0.53f) return 0f
        return 1f
    }

    companion object {
        /** Cuánto se corre el plano del frente del mueble respecto de la pared, en anchos (la luz entra desde la izquierda). */
        const val FRENTE = -0.03f
    }
}

/**
 * La mancha de la ventana en este momento, en la pared, o null (de noche manda la lumbre y la ventana solo enseña la nieve de
 * afuera). La de la tarde cae a la derecha de la ventana, entre ella y el abeto; al ocaso, más tendida y más baja; la luna,
 * más angosta y más tenue, en el azulejo entre la ventana y el abeto, quebrándose en la cenefa y en la cornisa.
 */
internal fun manchaNavidad(a: AireNavidad): ManchaNavidad? = when {
    a.oscuro && a.noche -> ManchaNavidad(floatArrayOf(0.265f, 0.2f, 0.4f, 0.22f, 0.465f, 0.53f, 0.33f, 0.51f), PaletaNavidad.LUNA, 0.5f)
    a.oscuro -> null
    a.noche -> ManchaNavidad(floatArrayOf(0.28f, 0.2f, 0.66f, 0.25f, 0.74f, 0.5f, 0.36f, 0.45f), PaletaNavidad.OCASO, 0.75f)
    else -> ManchaNavidad(floatArrayOf(0.28f, 0.12f, 0.56f, 0.16f, 0.66f, 0.53f, 0.38f, 0.49f), PaletaNavidad.SOL, 0.9f)
}

/**
 * La luna de los Sueños oscuros en el cuerpo de la alacena (de 0 a 1): entra por la ventana de arriba a la izquierda y roza lo de
 * arriba de ese lado; lejos, nada. [fx] y [fy], en anchos del mueble.
 */
internal fun lunaCuerpoNavidad(a: AireNavidad, fx: Float, fy: Float): Float {
    if (!(a.oscuro && a.noche)) return 0f
    val dx = fx - 0.12f; val dy = (fy - 0.55f) * 0.75f
    return (1.12f - kotlin.math.sqrt(dx * dx + dy * dy) / 0.9f).coerceIn(0f, 1f)
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoNavidad(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** El mismo camino corrido ([dx], [dy]). */
internal fun corridoNavidad(p: Path, dx: Float, dy: Float) = Path().apply { addPath(p, Offset(dx, dy)) }

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoNavidad(centro: Offset, ancho: Float, alto: Float, a: AireNavidad, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo o tornillo de latón: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoNavidad(p: Offset, r: Float, a: AireNavidad, k: Float, metal: Color = PaletaNavidad.LATON) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloNavidad(c: Offset, r: Float, a: AireNavidad, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaNavidad.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaNavidad.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (va dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroNavidad(r: Rect, a: AireNavidad, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Navidad, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_NAVIDAD: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.NAVIDAD, false) to juegoTonos(TemaId.NAVIDAD, true) }

private fun cuadradoNavidad(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaNavidad(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoNavidad(t.red - a0.red - dr * f) + cuadradoNavidad(t.green - a0.green - dg * f) + cuadradoNavidad(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto (la tela del álbum, el papel del regalo y la lana del calcetín): lo bajo, vino y carmesí
 * de acebo; lo de en medio, avena tostada (nunca blanco); lo alto, verde de abeto. Siguen la escala del tema (`juegoTonos`:
 * rojo, crema y verde).
 */
private val TINTES_NAVIDAD = listOf(
    0f to Color(0xFF5E1016), 0.2f to Color(0xFF8E1C24), 0.34f to Color(0xFFB0392F), 0.44f to Color(0xFFC88A5E), 0.5f to Color(0xFFD7C39A),
    0.56f to Color(0xFFB9BC8C), 0.65f to Color(0xFF86A56E), 0.77f to Color(0xFF4F8452), 0.89f to Color(0xFF2C6339), 1f to Color(0xFF1B4628),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_NAVIDAD]; si no
 * está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores: aquí la entrada es un objeto del cuarto,
 * así que primero se vuelve al color de día (el mismo punto de la escala de noche en la de día) y después la luz dibujada lo
 * apaga con la distancia.
 */
internal fun tonoDeDiaNavidad(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_NAVIDAD
    val x = (if (oscuro) enEscalaNavidad(t, noche) else null) ?: enEscalaNavidad(t, dia) ?: return t
    for (i in 0 until TINTES_NAVIDAD.size - 1) {
        val (f0, c0) = TINTES_NAVIDAD[i]; val (f1, c1) = TINTES_NAVIDAD[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_NAVIDAD.last().second
}
