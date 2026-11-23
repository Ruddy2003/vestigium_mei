package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Tierras Antiguas: la sala de arriba de la posada del paso, donde el cronista guarda los días de la
 * compañía. Lo frío: el enlucido pintado de azul de lapislázuli, el hierro, el peltre, la luna y el cuarzo. Lo cálido: el roble
 * ahumado del armario, el oro viejo de los filetes y del estarcido, el latón, el cobre del brasero, la cera, las llamas y el sol
 * de la ventana. Las gemas de la mina (rubí, esmeralda, zafiro, amatista) son los acentos.
 */
internal object PaletaAntiguas {
    /** El enlucido de lapislázuli: su cara, su luz, su sombra y lo hondo. */
    val LAPIS = Color(0xFF2A4790)
    val LAPIS_LUZ = Color(0xFF5577C0)
    val LAPIS_OSC = Color(0xFF1A2D66)
    val LAPIS_HONDO = Color(0xFF101C46)
    /** El oro viejo del estarcido, de los filetes y de la cenefa; y el ocre de la cenefa. */
    val ORO = Color(0xFFC9A24C)
    val ORO_LUZ = Color(0xFFF2D68C)
    val ORO_OSC = Color(0xFF7E5E22)
    val OCRE = Color(0xFFB27A34)
    val OCRE_OSC = Color(0xFF6E4318)
    /** El roble ahumado del armario y de la viga: su cara, su luz, su sombra y lo hondo. */
    val ROBLE = Color(0xFF5C3B22)
    val ROBLE_LUZ = Color(0xFF936539)
    val ROBLE_OSC = Color(0xFF341F10)
    val ROBLE_HONDO = Color(0xFF1C1008)
    /** Las tablas del suelo (roble más claro, gastado). */
    val SUELO = Color(0xFF7A5434)
    val SUELO_LUZ = Color(0xFFA67A4C)
    /** La piedra de la casa (bajo el enlucido roto, en el derrame de la ventana) y la cal. */
    val PIEDRA = Color(0xFF8E8676)
    val PIEDRA_OSC = Color(0xFF575043)
    val CAL = Color(0xFFDCD2BC)
    /** El latón, el hierro, el cobre y el peltre. */
    val LATON = Color(0xFFC9A04A)
    val LATON_LUZ = Color(0xFFF2D68A)
    val LATON_OSC = Color(0xFF7A5A22)
    val HIERRO = Color(0xFF35333B)
    val HIERRO_LUZ = Color(0xFF8E8C98)
    val COBRE = Color(0xFFB8673A)
    val COBRE_LUZ = Color(0xFFF2B47E)
    val COBRE_OSC = Color(0xFF5E2914)
    val PELTRE = Color(0xFF8C939A)
    val PELTRE_LUZ = Color(0xFFDDE2E6)
    val PELTRE_OSC = Color(0xFF4A5058)
    /** Las gemas de la mina. */
    val RUBI = Color(0xFFB01E36)
    val ESMERALDA = Color(0xFF1F8C5C)
    val ZAFIRO = Color(0xFF2B50C4)
    val AMATISTA = Color(0xFF7B3FA8)
    val AMATISTA_LUZ = Color(0xFFCDA6F0)
    val AMATISTA_OSC = Color(0xFF3C1A5C)
    val CUARZO = Color(0xFFE8E6EE)
    /** El pergamino de las etiquetas (de valor medio), la vitela cruda, la tinta y la cera. */
    val PERGAMINO = Color(0xFFC8B084)
    val VITELA = Color(0xFFE6D8B4)
    val VITELA_OSC = Color(0xFFB09A70)
    val TINTA = Color(0xFF22160E)
    val CERA = Color(0xFFEADFC4)
    val CERA_OSC = Color(0xFFB5A386)
    /** El cuero sin teñir (lo que no tiene ánimo) y el de las correas. */
    val CUERO = Color(0xFF7C4F2C)
    val CUERO_OSC = Color(0xFF452A16)
    /** La lana de la alfombra: rubia, añil, ocre y crudo. */
    val RUBIA = Color(0xFF9A2E26)
    val ANIL = Color(0xFF263E78)
    val CRUDO = Color(0xFFE0D2AE)
    /** La arpillera del saco de la mina y el cordel. */
    val ARPILLERA = Color(0xFFA88A5E)
    val CUERDA = Color(0xFFB79E6C)
    /** El sol de la ventana, el cielo, la llama y su corazón, la brasa, el ocaso y la luna. */
    val SOL = Color(0xFFFFE6B0)
    val CIELO = Color(0xFFE6EEF0)
    val CIELO_NOCHE = Color(0xFF16214E)
    val LLAMA = Color(0xFFFFB55C)
    val FUEGO = Color(0xFFFFE8B4)
    val BRASA = Color(0xFFE8582A)
    val OCASO = Color(0xFFF59A5C)
    val SOMBRA_OCASO = Color(0xFF402C66)
    val LUNA = Color(0xFFC6D4F4)
    /** La noche: el añil hondo en que se hunde la pared, el pardo rojizo del roble y lo hondo de los Sueños. */
    val NOCHE_PARED = Color(0xFF0A1031)
    val NOCHE_MUEBLE = Color(0xFF26110A)
    val NOCHE_SUENOS = Color(0xFF131036)
    val HONDO_SUENOS = Color(0xFF1E0F2A)
    /** La sombra fría del día, la que deja la luz de la ventana. */
    val FRIO = Color(0xFF1E2A4E)

    val LETRA: FontFamily = LetraExtra.camino
}

/**
 * El aire del momento en el estante de Tierras Antiguas: de día (el sol de la ventana de la izquierda), de noche (el modo oscuro:
 * arden la vela de las lunas, la arandela, los candiles de los huecos y el brasero) y en los Sueños ([noche]: el ocaso por la ventana en
 * claro; la luna en oscuro, con la vela baja y el brasero en brasas). Lo que no se alumbra se hunde en un oscuro con color:
 * la pared en un añil hondo y el roble ([mueble]) en un pardo rojizo; en los Sueños, en un ciruela.
 */
internal class AireAntiguas(val oscuro: Boolean, val noche: Boolean, val mueble: Boolean = false) {
    private val p = PaletaAntiguas
    /** Si arden las luces del tema. */
    val lumbre: Boolean get() = oscuro || noche
    /** El oscuro en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && mueble -> if (noche) p.HONDO_SUENOS else p.NOCHE_MUEBLE
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE_PARED
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, de su mismo tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        return Color(c.red * 0.14f + n.red * 0.82f, c.green * 0.14f + n.green * 0.82f, c.blue * 0.14f + n.blue * 0.82f, c.alpha)
    }

    /** A la luz de la llama, el azul y el verde bajan más que el rojo: no es el color con un velo naranja encima. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.88f, c.blue * 0.64f, c.alpha)

    /** Al ocaso de los Sueños claros: algo más bajo y más tibio, sin velo. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.95f, c.green * 0.85f, c.blue * 0.82f, c.alpha)

    /** El color de algo según la luz cálida que le llega ([cerca], de 0 a 1). De día, tal cual. */
    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(hundido(c), aLaLlama(c), (1.05f * k).coerceAtMost(0.94f)), p.LLAMA, 0.12f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); con lumbre, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val base = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(base, luzMueble(1f), f) else if (f < 0f) mezcla(base, sombraMueble(1f), -f) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el oscuro con color. */
    fun sombra(a: Float): Color = when {
        oscuro -> hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f))
        noche -> p.SOMBRA_OCASO.copy(alpha = (a * 1.15f).coerceIn(0f, 1f))
        else -> sombraMueble(a)
    }

    /** La luz crema del día; al ocaso, la naranja; de noche, casi nada. */
    fun claro(a: Float): Color = when {
        oscuro -> luzMueble(a * 0.06f)
        noche -> mezcla(luzMueble(1f), p.OCASO, 0.55f).copy(alpha = (a * 0.7f).coerceIn(0f, 1f))
        else -> luzMueble(a)
    }

    /** El brillo de lo que mira a la luz: de día, crema; al ocaso, naranja; de noche, el de la llama según [cerca]. */
    fun brillo(a: Float, cerca: Float): Color = when {
        oscuro -> p.LLAMA.copy(alpha = (a * 0.95f * cerca).coerceIn(0f, 1f))
        noche -> mezcla(luzMueble(1f), p.OCASO, 0.5f).copy(alpha = (a * 0.75f).coerceIn(0f, 1f))
        else -> luzMueble(a)
    }

    /** El filo de la llama en el canto que la mira; al ocaso, el del sol rasante; de día no hay. */
    fun filo(a: Float): Color = when {
        oscuro -> p.LLAMA.copy(alpha = a.coerceIn(0f, 1f))
        noche -> p.OCASO.copy(alpha = (a * 0.6f).coerceIn(0f, 1f))
        else -> Color.Transparent
    }

    /** El roce frío de la luna en los cantos que miran a la ventana (de noche, sobre todo en los Sueños). */
    fun lunar(a: Float): Color = if (oscuro) p.LUNA.copy(alpha = (if (noche) a * 1.3f else a * 0.5f).coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (lejos, hondo pero con su color;
     * cerca, encendido); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.19f, t.green * 0.155f, t.blue * 0.14f, t.alpha), hondo, 0.3f)
        val encendido = mezcla(t, aLaLlama(t), 0.85f)
        return mezcla(apagado, encendido, (1.08f * k).coerceAtMost(1f))
    }

    /**
     * El pergamino donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca más claro que
     * lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = Color(c.red * 0.5f, c.green * 0.42f, c.blue * 0.34f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.93f, c.blue * 0.74f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 0.8).toFloat())
    }
}
