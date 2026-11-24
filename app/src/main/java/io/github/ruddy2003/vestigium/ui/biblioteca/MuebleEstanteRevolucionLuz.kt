package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Revolución: la trastienda de una imprenta de un faubourg de París en el otoño de 1790. El yeso de
 * cal teñido de ocre rosado con su cenefa de plantilla, el roble de la librería pintado de rojo sangre de toro con el interior
 * de verde cardenillo, el papel de trapo y la tinta de imprenta, el plomo de los tipos, el pino de la caja, el latón del
 * candelero y del componedor, la loza blanca con su azul, el gres, el sebo de las velas, el hierro de la estufa y la piedra de
 * liais con sus tacos de pizarra. Dos temperaturas: lo cálido es el yeso, la sangre de toro, el pino, el latón y la lumbre; lo
 * frío, el cardenillo, el plomo, el cielo de la ventana y la noche.
 */
internal object PaletaRevolucion {
    /** El yeso de la pared: su cara, su luz (al sol), su sombra y lo hondo; el listón que asoma en el desconchón. */
    val YESO = Color(0xFFDDA88A)
    val YESO_LUZ = Color(0xFFF6D2B4)
    val YESO_SOMBRA = Color(0xFFB57F64)
    val YESO_HONDO = Color(0xFF7A4A3A)
    val LISTON = Color(0xFF8C6440)
    /** La pintura de la librería, sangre de toro: su cara, su luz, su sombra y lo hondo; el roble que asoma donde se gastó. */
    val SANGRE = Color(0xFF7E2A1F)
    val SANGRE_LUZ = Color(0xFFAE4A33)
    val SANGRE_OSC = Color(0xFF511710)
    val SANGRE_HONDA = Color(0xFF2C0B08)
    val ROBLE = Color(0xFFA67C4C)
    val ROBLE_OSC = Color(0xFF6A4A2A)
    /** El cardenillo del interior de los casilleros, su luz, su sombra y lo hondo. */
    val VERDE = Color(0xFF4C7E6C)
    val VERDE_LUZ = Color(0xFF6E9E88)
    val VERDE_OSC = Color(0xFF2D5246)
    val VERDE_HONDO = Color(0xFF16302A)
    /** El papel de trapo: crudo, su luz, tostado (las etiquetas) y su sombra; la tinta de imprenta. */
    val PAPEL = Color(0xFFEDE2C8)
    val PAPEL_LUZ = Color(0xFFFBF4E2)
    val TOSTADO = Color(0xFFD2BC90)
    val PAPEL_OSC = Color(0xFFA48D64)
    val TINTA = Color(0xFF1D1A1F)
    /** El papel gris de estraza de las resmas y de lo que no tiene ánimo. */
    val ESTRAZA = Color(0xFF9C8E76)
    val ESTRAZA_OSC = Color(0xFF6C604E)
    /** El bramante. */
    val BRAMANTE = Color(0xFFC8AC76)
    val BRAMANTE_OSC = Color(0xFF8A7148)
    /** El plomo de los tipos y su brillo. */
    val PLOMO = Color(0xFF5E6268)
    val PLOMO_LUZ = Color(0xFFB8BEC4)
    /** El pino de la caja de tipos y de la tina. */
    val PINO = Color(0xFFC08F58)
    val PINO_LUZ = Color(0xFFE2B47A)
    val PINO_OSC = Color(0xFF7A5230)
    /** El latón del candelero, del componedor y de los tiradores. */
    val LATON = Color(0xFFC79C4C)
    val LATON_LUZ = Color(0xFFF4D892)
    val LATON_OSC = Color(0xFF75561F)
    /** La loza blanca, su sombra y el azul de su cenefa; el gres de los botes. */
    val LOZA = Color(0xFFEEEADF)
    val LOZA_OSC = Color(0xFFB8B2A4)
    val AZUL_LOZA = Color(0xFF2F4F8E)
    val GRES = Color(0xFF9C8466)
    val GRES_OSC = Color(0xFF5E4A36)
    /** El sebo de las velas. */
    val SEBO = Color(0xFFEDE0B8)
    val SEBO_OSC = Color(0xFFB8A676)
    /** El hierro de la estufa, los aros y los clavos. */
    val HIERRO = Color(0xFF2E2C2E)
    val HIERRO_LUZ = Color(0xFF7C7A7C)
    /** El cuero de las balas de entintar. */
    val CUERO = Color(0xFF4C3020)
    val CUERO_LUZ = Color(0xFF7E5636)
    /** La piedra de liais del suelo, su junta y la pizarra de los tacos. */
    val PIEDRA = Color(0xFFCFC2A6)
    val PIEDRA_OSC = Color(0xFFA69A82)
    val PIZARRA = Color(0xFF2C2D33)
    /** Los tres colores sueltos (en cintas y papeles, nunca juntos): el rojo, el azul y el crudo. */
    val ROJO = Color(0xFFA8242E)
    val AZUL = Color(0xFF2A4478)
    /** El cielo de la ventana: de día, blanco de noviembre abajo y azul arriba; la fachada de enfrente; de noche, la calle. */
    val CIELO = Color(0xFFE8ECE8)
    val CIELO_ALTO = Color(0xFF9EB8CC)
    val ENFRENTE = Color(0xFFCDB89A)
    val CIELO_NOCHE = Color(0xFF16204A)
    val CIELO_NOCHE_ALTO = Color(0xFF0A1030)
    /** La noche de arriba (añil de tinta) y la de los Sueños; lo hondo del cuerpo de noche y en los Sueños. */
    val NOCHE = Color(0xFF10142E)
    val NOCHE_SUENOS = Color(0xFF181036)
    val HONDO = Color(0xFF280B09)
    val HONDO_SUENOS = Color(0xFF22102C)
    /** Lo hondo del fondo de los casilleros: el cardenillo se hunde en verde botella (en los Sueños, en pizarra azul). */
    val FONDO_NOCHE = Color(0xFF08201B)
    val FONDO_SUENOS = Color(0xFF0E1834)
    /** La sombra del ocaso de los Sueños claros (violeta, con dirección), su luz naranja y el sol del día. */
    val SOMBRA_OCASO = Color(0xFF3E2A5E)
    val OCASO = Color(0xFFF4A25C)
    val SOL = Color(0xFFFFF0C8)
    /** La llama del sebo, su corazón, la brasa, la luna y su roce. */
    val LLAMA = Color(0xFFFFB45A)
    val FUEGO = Color(0xFFFFEAB6)
    val BRASA = Color(0xFFE45C2A)
    val LUNA = Color(0xFFE6EEF8)
    val LUNA_RAS = Color(0xFFA2BCE0)
    /** La sombra fría del día (la del cardenillo, el plomo y el vidrio). */
    val FRIO = Color(0xFF263A44)

    val LETRA: FontFamily = LetraExtra.convencion
}

/**
 * El aire del momento en el estante de Revolución: de día (la luz de la ventana del cajista), de noche (el modo oscuro: arden la
 * vela de arriba, la de un hueco y la estufa del pie) y en los Sueños ([noche]: el ocaso en claro, la luna en oscuro). De noche
 * cada color va de su hondo (oscuro, de su tono, con el aire de la noche) a su color según cuánta luz le llega ([cerca], de 0
 * a 1), con el calor de la llama encima. Arriba la noche es añil de tinta; en el cuerpo de la librería y en el suelo
 * ([cuerpo]), lo que no se alumbra se hunde en un sangre de toro hondo (en los Sueños, en un ciruela); el fondo de los
 * casilleros ([fondo]), en verde botella.
 */
internal class AireRevolucion(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false) {
    private val p = PaletaRevolucion
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && fondo -> if (noche) p.FONDO_SUENOS else p.FONDO_NOCHE
        oscuro && cuerpo -> if (noche) p.HONDO_SUENOS else p.HONDO
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, algo de su tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        val f = if (cuerpo) 0.14f else 0.1f
        return Color(c.red * f + n.red * 0.86f, c.green * f + n.green * 0.86f, c.blue * f + n.blue * 0.86f, c.alpha)
    }

    /** El color de algo a la luz del sebo: alumbra en naranja, así que el azul y el verde bajan más que el rojo. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.86f, c.blue * 0.6f, c.alpha)

    /** El color al ocaso de los Sueños claros: algo más bajo y más tibio. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.96f, c.green * 0.86f, c.blue * 0.8f, c.alpha)

    /** Cerca de la llama, lo claro se vuelve ámbar (el yeso y el papel pasan a miel, no a amarillo sucio). */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaLlama(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.5f).coerceAtMost(1f), (l * 1.08f).coerceAtMost(1f), l * 0.55f, c.alpha)
        return mezcla(mezcla(c, v, 0.8f), tono, (if (cuerpo) 0.3f else 0.55f) * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(hundido(c), ambar(c, k), (1.1f * Math.pow(k.toDouble(), 1.5).toFloat()).coerceAtMost(0.94f)), if (cuerpo) p.BRASA else p.LLAMA, 0.12f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la luz de la ventana ([f], de 0 a 1): [c] es el material y [base], su color sin esa luz. De día, el sol blanco
     * y tibio de noviembre; al ocaso, el naranja; con la luna, el material en un plata frío. Es el color de la superficie bajo
     * esa luz, opaco: no un velo encima.
     */
    fun sol(c: Color, base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        return when {
            oscuro -> {
                val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
                val g = mezcla(c, Color(l, l, l), 0.55f)
                val q = 0.36f * k
                Color((base.red + g.red * 0.42f * q).coerceAtMost(1f), (base.green + g.green * 0.58f * q).coerceAtMost(1f), (base.blue + (g.blue * 0.95f + 0.1f) * q).coerceAtMost(1f), c.alpha)
            }
            noche -> mezcla(base, Color((base.red * 1.24f + 0.1f).coerceAtMost(1f), (base.green * 1.02f + 0.05f).coerceAtMost(1f), base.blue * 0.8f + 0.02f, c.alpha), k)
            else -> {
                val l = Color((base.red * 1.42f + 0.06f).coerceAtMost(1f), (base.green * 1.36f + 0.05f).coerceAtMost(1f), (base.blue * 1.18f + 0.03f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF6E2BC), 0.3f), k)
            }
        }
    }

    /** El color de [c] con la luz del momento ([f] como en [dia]) y, si [lit] pasa de 0, con la mancha [m] de la ventana. */
    fun en(c: Color, f: Float, cerca: Float, lit: Float, m: ManchaRevolucion?): Color {
        val base = dia(c, f, cerca)
        return if (lit > 0f && m != null) sol(c, base, m.fuerza * lit) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del cardenillo, el plomo y el vidrio); de noche, la misma que las demás. */
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
     * El color del ánimo en el papel de una entrada: de noche conserva su tono de día y solo baja con la distancia a la luz (el
     * rojo sigue rojo, pardo lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.22f, t.green * 0.19f, t.blue * 0.2f, t.alpha), hondo, 0.25f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        return mezcla(apagado, encendido, (1.15f * Math.pow(k.toDouble(), 1.5).toFloat()).coerceAtMost(1f))
    }

    /**
     * El papel tostado donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca más
     * claro que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = Color(c.red * 0.4f, c.green * 0.28f, c.blue * 0.18f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.7f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.6).toFloat().coerceAtMost(1f))
    }
}

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoRevolucion(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoRevolucion(centro: Offset, ancho: Float, alto: Float, a: AireRevolucion, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de cabeza redonda: su sombra, la cabeza ([metal]) y el punto de luz. */
internal fun DrawScope.clavoRevolucion(p: Offset, r: Float, a: AireRevolucion, k: Float, metal: Color = PaletaRevolucion.HIERRO) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.5f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón, la loza o el plomo (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloRevolucion(c: Offset, r: Float, a: AireRevolucion, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaRevolucion.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaRevolucion.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroRevolucion(r: Rect, a: AireRevolucion, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Una onda seguida de [x0] a [x1] en [y] (alto [alto], paso [paso]): la cenefa de la loza, sin trazos sueltos que se lean. */
internal fun DrawScope.ondaRevolucion(x0: Float, x1: Float, y: Float, alto: Float, paso: Float, color: Color, grueso: Float) {
    if (paso <= 0.5f || x1 <= x0) return
    val p = Path()
    val d = paso / 12f
    var x = x0
    p.moveTo(x, y)
    while (x < x1) {
        x += d
        p.lineTo(x, y - alto * kotlin.math.sin((x - x0) / paso * 6.2832f))
    }
    drawPath(p, color, style = androidx.compose.ui.graphics.drawscope.Stroke(grueso, cap = androidx.compose.ui.graphics.StrokeCap.Round))
}

/** La llama de una vela de sebo en [p] (la base de la llama), de tamaño [s]: la lengua, su corazón y su aura. */
internal fun DrawScope.llamaRevolucion(p: Offset, s: Float, f: Float = 1f) {
    val pal = PaletaRevolucion
    val llama = Path().apply {
        moveTo(p.x + s * 0.2f, p.y - s * 2.5f)
        cubicTo(p.x + s * 0.95f, p.y - s * 1.2f, p.x + s * 0.75f, p.y, p.x, p.y + s * 0.2f)
        cubicTo(p.x - s * 0.75f, p.y, p.x - s * 0.7f, p.y - s * 1.1f, p.x + s * 0.2f, p.y - s * 2.5f); close()
    }
    drawCircle(pal.LLAMA.copy(alpha = 0.3f * f), s * 2.6f, p - Offset(0f, s * 0.9f))
    drawPath(llama, Color(0xFFFF9E46).copy(alpha = (0.6f + 0.4f * f).coerceAtMost(1f)))
    drawOval(pal.FUEGO, Offset(p.x - s * 0.32f, p.y - s * 1.15f), Size(s * 0.64f, s * 1.2f))
    // El pabilo, negro, que se dobla en la llama.
    drawLine(pal.TINTA, Offset(p.x, p.y + s * 0.35f), Offset(p.x + s * 0.12f, p.y - s * 0.45f), s * 0.16f)
}

/** Las escalas del ánimo de Revolución, la de día y la de noche, en 51 puntos cada una (como las arma `Tonos.divergente`). */
private val MUESTRAS_REVOLUCION: Pair<List<Color>, List<Color>> by lazy {
    fun muestras(e: List<Color>) = List(51) { i ->
        val x = i / 50f
        if (x < 0.5f) lerp(e[0], e[1], x * 2) else lerp(e[1], e[2], (x - 0.5f) * 2)
    }
    muestras(juegoTonos(TemaId.REVOLUCION, false)) to muestras(juegoTonos(TemaId.REVOLUCION, true))
}

/** Dónde cae [t] en una escala muestreada (de 0 a 1), o null si no está en ella. */
private fun enEscalaRevolucion(t: Color, muestras: List<Color>): Float? {
    var mejor = -1; var error = 0.0025f
    muestras.forEachIndexed { i, c ->
        val dr = t.red - c.red; val dg = t.green - c.green; val db = t.blue - c.blue
        val e = dr * dr + dg * dg + db * db
        if (e < error) { error = e; mejor = i }
    }
    return if (mejor < 0) null else mejor / 50f
}

/**
 * Los papeles del ánimo, de bajo a alto: el ánimo es el papel de la entrada, en todo su cuerpo. Lo bajo, papel rojo de
 * cubierta; lo de en medio, papel crudo de trapo; lo alto, el papel azul de las cubiertas de la época. Siguen la escala del
 * tema (`juegoTonos`).
 */
private val PAPELES_REVOLUCION = listOf(
    0f to Color(0xFF9A1E2C), 0.2f to Color(0xFFB0343A), 0.38f to Color(0xFFC9877A), 0.5f to Color(0xFFE6D9BC),
    0.62f to Color(0xFFA9B4C2), 0.8f to Color(0xFF4F6A9C), 1f to Color(0xFF243E72),
)

/**
 * El papel de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en
 * [PAPELES_REVOLUCION]; si no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores (el de en
 * medio pasa de crudo a casi negro): aquí la entrada es papel de la trastienda, así que primero se vuelve al color de día (el
 * mismo punto de la escala de noche en la de día) y después la luz dibujada lo apaga con la distancia.
 */
internal fun papelRevolucion(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = MUESTRAS_REVOLUCION
    val x = (if (oscuro) enEscalaRevolucion(t, noche) else null) ?: enEscalaRevolucion(t, dia) ?: return t
    for (i in 0 until PAPELES_REVOLUCION.size - 1) {
        val (f0, c0) = PAPELES_REVOLUCION[i]; val (f1, c1) = PAPELES_REVOLUCION[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return PAPELES_REVOLUCION.last().second
}
