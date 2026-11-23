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
 * La paleta del estante de Templario: la cámara alta de la torre de un castillo de frontera del Levante hacia 1250. La cal
 * del enlucido con su despiece fingido a la almagra, la caliza dorada que asoma donde se cae, el cedro del Líbano del
 * armario con el fondo de los casilleros pintado de verde tierra, el hierro de los flejes, el latón del aguamanil y de la
 * lámpara, el vidrio de Siria, el barro y lo vidriado de la costa, el lino de la sobreveste y la palma de la estera. Dos
 * temperaturas: lo cálido es el cedro, la almagra, el latón y la lumbre; lo frío, el verde tierra, el cielo y la noche.
 */
internal object PaletaTemplario {
    /** La cal del enlucido: su cara, su luz (al sol), su sombra y la almagra del despiece fingido. */
    val CAL = Color(0xFFEDE1C8)
    val CAL_LUZ = Color(0xFFFFF6E2)
    val CAL_SOMBRA = Color(0xFFC9B79B)
    val ALMAGRA = Color(0xFFA5432B)
    val ALMAGRA_OSC = Color(0xFF6A2417)
    /** La caliza dorada de la torre (la misma del Ideario), donde se ha caído el enlucido. */
    val CALIZA = Color(0xFFD2B07A)
    val CALIZA_OSC = Color(0xFF9C7C4E)
    /** El cedro del armario: su cara, su luz, su sombra y lo hondo. */
    val CEDRO = Color(0xFF8A4529)
    val CEDRO_LUZ = Color(0xFFBC7448)
    val CEDRO_OSC = Color(0xFF552716)
    val CEDRO_HONDO = Color(0xFF2C120A)
    /** El verde tierra del fondo de los casilleros, su luz y su sombra. */
    val VERDE = Color(0xFF465638)
    val VERDE_LUZ = Color(0xFF66774E)
    val VERDE_OSC = Color(0xFF2A3522)
    val VERDE_HONDO = Color(0xFF161D12)
    /** El hierro de los flejes, los clavos y el brazo de la lámpara. */
    val HIERRO = Color(0xFF34343A)
    val HIERRO_LUZ = Color(0xFF8E9098)
    /** El latón del aguamanil, la jofaina, la lámpara y los bullones. */
    val LATON = Color(0xFFC6963F)
    val LATON_LUZ = Color(0xFFF4D68C)
    val LATON_OSC = Color(0xFF7A5220)
    /** El cobre de la jofaina. */
    val COBRE = Color(0xFFB8643A)
    val COBRE_LUZ = Color(0xFFEDA675)
    val COBRE_OSC = Color(0xFF6A3018)
    /** El bronce del brasero. */
    val BRONCE = Color(0xFF9A6A34)
    /** El pergamino de las etiquetas y las cartas, la tinta de agallas y el lacre. */
    val PERGAMINO = Color(0xFFE2D2AE)
    val PERG_OSC = Color(0xFFB09A6E)
    val TINTA = Color(0xFF26180E)
    val LACRE = Color(0xFFA2291C)
    /** El lino de la sobreveste y de la toalla, y la cruz. */
    val LINO = Color(0xFFF0E9DA)
    val LINO_OSC = Color(0xFFC6B9A0)
    val CRUZ = Color(0xFFAE2620)
    /** El barro, lo vidriado de la costa (amarillo y verde), el azul de Siria, el vidrio de la lámpara. */
    val BARRO = Color(0xFFC28A58)
    val BARRO_OSC = Color(0xFF85532F)
    val VIDRIADO = Color(0xFFD5A63E)
    val VIDRIADO_VERDE = Color(0xFF6F8B3A)
    val AZUL = Color(0xFF2E5E9E)
    val VIDRIO = Color(0xFFD9E4D2)
    /** La palma de la estera y la espuerta, los dátiles, las granadas, el cuerno. */
    val PALMA = Color(0xFFC9A465)
    val PALMA_OSC = Color(0xFF8C6C3C)
    val DATIL = Color(0xFF6E3016)
    val GRANADA = Color(0xFFB2352A)
    val CUERNO = Color(0xFFE0CB98)
    val CUERNO_OSC = Color(0xFF8E7446)
    val CUERO = Color(0xFF6A3F22)
    val CUERO_OSC = Color(0xFF3C2213)
    /** El cielo del ajimez: de día blanco de calor abajo y azul arriba; de noche, el de la vigilia. */
    val CIELO = Color(0xFFEFEEDF)
    val CIELO_ALTO = Color(0xFF8FBEDB)
    val CIELO_NOCHE = Color(0xFF14204A)
    val CIELO_NOCHE_ALTO = Color(0xFF0A102C)
    /** La noche de arriba (añil) y la de los Sueños; lo hondo del cuerpo del armario de noche (sangre de toro) y en los Sueños. */
    val NOCHE = Color(0xFF0E1434)
    val NOCHE_SUENOS = Color(0xFF16103A)
    val SANGRE = Color(0xFF26100B)
    val HONDO_SUENOS = Color(0xFF22102E)
    /** Lo hondo del fondo de los casilleros de noche: el verde tierra se hunde en verde botella (en los Sueños, en un azul de pizarra). */
    val FONDO_NOCHE = Color(0xFF0A1A14)
    val FONDO_SUENOS = Color(0xFF101634)
    /** La sombra del ocaso de los Sueños claros (violeta, con dirección), su luz naranja y el sol del mediodía. */
    val SOMBRA_OCASO = Color(0xFF402A62)
    val OCASO = Color(0xFFF4A25C)
    val SOL = Color(0xFFFFF0C8)
    /** La llama del aceite, su corazón, la ascua, la luna y su roce. */
    val LLAMA = Color(0xFFFFB55A)
    val FUEGO = Color(0xFFFFE9B4)
    val BRASA = Color(0xFFE2622E)
    val LUNA = Color(0xFFE6EEF8)
    val LUNA_RAS = Color(0xFFA2BCE0)
    /** La sombra fría del día (la del verde, el vidrio y el hierro). */
    val FRIO = Color(0xFF2E3A4E)

    val LETRA: FontFamily = LetraExtra.cruzada
}

/**
 * El aire del momento en el estante de Templario: de día (el sol que entra por el ajimez), de noche (el modo oscuro: arden la
 * lámpara de vidrio, el candil de los huecos y el brasero del pie) y en los Sueños ([noche]: el ocaso en claro, la luna en
 * oscuro). De noche cada color va de su hondo (oscuro, de su tono, con el aire de la noche) a su color según cuánta luz le
 * llega ([cerca], de 0 a 1), con el calor de la llama encima. Arriba la noche es añil; en el cuerpo del armario y en el suelo
 * ([cuerpo]), lo que no se alumbra se hunde en un pardo de sangre de toro (en los Sueños, en un ciruela hondo).
 */
internal class AireTemplario(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false) {
    private val p = PaletaTemplario
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && fondo -> if (noche) p.FONDO_SUENOS else p.FONDO_NOCHE
        oscuro && cuerpo -> if (noche) p.HONDO_SUENOS else p.SANGRE
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, algo de su tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        val f = if (cuerpo) 0.13f else 0.09f
        return Color(c.red * f + n.red * 0.86f, c.green * f + n.green * 0.86f, c.blue * f + n.blue * 0.86f, c.alpha)
    }

    /** El color de algo a la luz del aceite: alumbra en naranja, así que el azul y el verde bajan más que el rojo. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.86f, c.blue * 0.6f, c.alpha)

    /** El color al ocaso de los Sueños claros: algo más bajo y más tibio. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.96f, c.green * 0.86f, c.blue * 0.8f, c.alpha)

    /** Cerca de la llama, lo claro se vuelve ámbar (la cal pasa a miel, no a amarillo sucio). */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaLlama(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.5f).coerceAtMost(1f), (l * 1.08f).coerceAtMost(1f), l * 0.55f, c.alpha)
        return mezcla(mezcla(c, v, 0.8f), tono, (if (cuerpo) 0.3f else 0.55f) * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(hundido(c), ambar(c, k), (1.08f * Math.pow(k.toDouble(), 1.3).toFloat()).coerceAtMost(0.94f)), if (cuerpo) p.BRASA else p.LLAMA, 0.12f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la mancha de luz del ajimez ([f], de 0 a 1): [c] es el material y [base], su color sin esa luz. De día, el sol
     * blanco y cálido; al ocaso, el naranja; con la luna, el material en un plata frío. Es el color de la superficie bajo esa
     * luz, opaco: no un velo encima.
     */
    fun sol(c: Color, base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        return when {
            oscuro -> {
                // La luna se suma a lo que ya hay (la lumbre sigue alumbrando debajo): el material en un plata frío.
                val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
                val g = mezcla(c, Color(l, l, l), 0.55f)
                val q = 0.36f * k
                Color((base.red + g.red * 0.42f * q).coerceAtMost(1f), (base.green + g.green * 0.58f * q).coerceAtMost(1f), (base.blue + (g.blue * 0.95f + 0.1f) * q).coerceAtMost(1f), c.alpha)
            }
            noche -> mezcla(base, Color((base.red * 1.3f + 0.12f).coerceAtMost(1f), (base.green * 1.0f + 0.04f).coerceAtMost(1f), base.blue * 0.72f, c.alpha), k)
            // El sol del mediodía: el mismo color, más claro y entibiado (sin irse al blanco, que lo lavaría).
            else -> {
                val l = Color((base.red * 1.36f + 0.04f).coerceAtMost(1f), (base.green * 1.28f + 0.02f).coerceAtMost(1f), (base.blue * 1.08f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF0D29C), 0.32f), k)
            }
        }
    }

    /** El color de [c] con la luz del momento ([f] como en [dia]) y, si [lit] pasa de 0, con la mancha [m] del ajimez encima. */
    fun en(c: Color, f: Float, cerca: Float, lit: Float, m: ManchaTemplario?): Color {
        val base = dia(c, f, cerca)
        return if (lit > 0f && m != null) sol(c, base, m.fuerza * lit) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del verde, el vidrio y el hierro); de noche, la misma que las demás. */
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

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (lo rojo sigue rojo, pardo
     * lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.22f, t.green * 0.19f, t.blue * 0.2f, t.alpha), hondo, 0.25f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.2).toFloat()).coerceAtMost(1f))
    }

    /**
     * El pergamino donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca más claro
     * que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        // Lejos, pergamino tostado en sombra (más pardo y más hondo), no gris.
        val lejos = Color(c.red * 0.39f, c.green * 0.27f, c.blue * 0.16f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.7f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.6).toFloat().coerceAtMost(1f))
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoTemplario(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoTemplario(centro: Offset, ancho: Float, alto: Float, a: AireTemplario, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de cabeza redonda: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.clavoTemplario(p: Offset, r: Float, a: AireTemplario, k: Float, metal: Color = PaletaTemplario.HIERRO) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.5f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón, el vidrio o lo vidriado (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloTemplario(c: Offset, r: Float, a: AireTemplario, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaTemplario.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaTemplario.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroTemplario(r: Rect, a: AireTemplario, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Templario, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_TEMPLARIO: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.TEMPLARIO, false) to juegoTonos(TemaId.TEMPLARIO, true) }

private fun cuadradoTemplario(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaTemplario(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoTemplario(t.red - a0.red - dr * f) + cuadradoTemplario(t.green - a0.green - dg * f) + cuadradoTemplario(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los cueros del ánimo, de bajo a alto: el ánimo es la piel de la entrada, en todo su cuerpo. Lo bajo, piel teñida de añil;
 * lo de en medio, piel de alumbre cruda; lo alto, leonado, almagra y grana. Siguen la escala del tema (`juegoTonos`).
 */
private val CUEROS_TEMPLARIO = listOf(
    0f to Color(0xFF223258), 0.26f to Color(0xFF34508A), 0.4f to Color(0xFF6B7EA2), 0.5f to Color(0xFFE0D0A8),
    0.6f to Color(0xFFD5A458), 0.72f to Color(0xFFBC6630), 0.86f to Color(0xFFA8302A), 1f to Color(0xFF8C1A26),
)

/**
 * La piel de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [CUEROS_TEMPLARIO]; si
 * no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores: aquí la entrada es un objeto de la
 * cámara, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la de día) y después la luz
 * dibujada lo apaga con la distancia.
 */
internal fun cueroTemplario(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_TEMPLARIO
    val x = (if (oscuro) enEscalaTemplario(t, noche) else null) ?: enEscalaTemplario(t, dia) ?: return t
    for (i in 0 until CUEROS_TEMPLARIO.size - 1) {
        val (f0, c0) = CUEROS_TEMPLARIO[i]; val (f1, c1) = CUEROS_TEMPLARIO[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return CUEROS_TEMPLARIO.last().second
}
