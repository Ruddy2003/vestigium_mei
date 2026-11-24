package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Napoleónico: el cuarto de una casa señorial de Lituania requisada para el estado mayor en el verano
 * de 1812. El enlucido pintado de amarillo Imperio con su friso verde, la caoba y el latón de la librería de campaña del
 * oficial, el paño verde con que van forradas sus cajas, el papel, la tinta y el lacre, el globo, la chapa de la lámpara, el
 * vidrio de la botella, el roble del parqué, el cuero de la silla y el abedul de la leña. Dos temperaturas: lo cálido es el
 * amarillo, la caoba, el latón y la lumbre; lo frío, el paño, el friso, el mar del globo, el cielo y la noche.
 */
internal object PaletaNapoleon {
    /** El amarillo Imperio de la pared: su cara, su luz (al sol), su sombra y lo hondo; el enlucido pardo que asoma. */
    val AMARILLO = Color(0xFFDDA845)
    val AMARILLO_LUZ = Color(0xFFF7D888)
    val AMARILLO_SOMBRA = Color(0xFFB27F2E)
    val AMARILLO_HONDO = Color(0xFF7A5420)
    val ENLUCIDO = Color(0xFFA48A66)
    val ENLUCIDO_OSC = Color(0xFF6E5A40)
    /** El verde Imperio del friso pintado y de los filetes, y el ocre de sus palmetas. */
    val VERDE = Color(0xFF2E5A40)
    val VERDE_OSC = Color(0xFF1B3628)
    val OCRE = Color(0xFFC6913A)
    /** La caoba: su cara, su luz, su sombra, lo hondo y la llama de la chapa flameada. */
    val CAOBA = Color(0xFF7C3420)
    val CAOBA_LUZ = Color(0xFFB4603E)
    val CAOBA_OSC = Color(0xFF4C1B10)
    val CAOBA_HONDO = Color(0xFF26100A)
    val CAOBA_LLAMA = Color(0xFF9A4428)
    /** El latón de cantoneras, filetes, portaetiquetas y la lámpara. */
    val LATON = Color(0xFFCFA548)
    val LATON_LUZ = Color(0xFFF7E09A)
    val LATON_OSC = Color(0xFF835F1E)
    /** El paño verde del forro de las cajas: su cara, su luz, su sombra y lo hondo. */
    val PANO = Color(0xFF2F6444)
    val PANO_LUZ = Color(0xFF4F8A5C)
    val PANO_OSC = Color(0xFF1C3E2A)
    val PANO_HONDO = Color(0xFF0F2418)
    /** El papel, la cartulina de las etiquetas (de valor medio), la tinta y el lacre. */
    val PAPEL = Color(0xFFF1E6CC)
    val CARTULINA = Color(0xFFD8C495)
    val CARTULINA_OSC = Color(0xFFA98F60)
    val TINTA = Color(0xFF24170E)
    val LACRE = Color(0xFFA3242A)
    /** El becerro de cantos y esquinas, el cuero de la silla y su sombra. */
    val BECERRO = Color(0xFF7A4A28)
    val CUERO = Color(0xFF7A4322)
    val CUERO_OSC = Color(0xFF41210F)
    /** El globo: el mar, la tierra en dos tintas, el papel del horizonte. */
    val MAR = Color(0xFF8DB7AE)
    val MAR_OSC = Color(0xFF4F7F7A)
    val TIERRA_GLOBO = Color(0xFFDDB86A)
    val TIERRA_GLOBO2 = Color(0xFFC98E5E)
    /** La chapa verde de la pantalla, el vidrio de la botella, el estaño y la hojalata. */
    val CHAPA = Color(0xFF24402F)
    val VIDRIO = Color(0xFF2E4D26)
    val ESTANO = Color(0xFF9EA2A0)
    val HOJALATA = Color(0xFFA8ADAD)
    /** El roble del parqué, su veta y su junta. */
    val ROBLE = Color(0xFFB98A52)
    val ROBLE_OSC = Color(0xFF7C5730)
    /** El abedul de la leña, su corteza oscura y la madera cortada. */
    val ABEDUL = Color(0xFFE6E0CF)
    val ABEDUL_MARCA = Color(0xFF2E2A26)
    val LENA = Color(0xFFD9B27A)
    /** El paño azul de la mantilla y el galón. */
    val AZUL = Color(0xFF26407E)
    val GALON = Color(0xFFD7B25A)
    /** La porcelana de la taza y la plata. */
    val PORCELANA = Color(0xFFF4F0E6)
    val PLATA = Color(0xFFC9CDD0)
    /** El cielo de la ventana: de día, blanco de luz abajo y azul arriba; de noche, el de afuera. */
    val CIELO = Color(0xFFEAF0E8)
    val CIELO_ALTO = Color(0xFF9CC3DE)
    val CIELO_NOCHE = Color(0xFF132250)
    val CIELO_NOCHE_ALTO = Color(0xFF080F2C)
    /** La noche de arriba (azul) y la de los Sueños; lo hondo del cuerpo de noche (caoba sangre de toro) y en los Sueños. */
    val NOCHE = Color(0xFF0C1432)
    val NOCHE_SUENOS = Color(0xFF15103A)
    val SANGRE = Color(0xFF1D0C08)
    val HONDO_SUENOS = Color(0xFF22102E)
    /** Lo hondo del paño de los casilleros de noche: verde botella (en los Sueños, azul pizarra). */
    val FONDO_NOCHE = Color(0xFF071A10)
    val FONDO_SUENOS = Color(0xFF0E1534)
    /** La sombra del ocaso de los Sueños claros (violeta), su luz naranja y el sol del mediodía. */
    val SOMBRA_OCASO = Color(0xFF402A62)
    val OCASO = Color(0xFFF2B286)
    val SOL = Color(0xFFFFF0C8)
    /** La llama de la vela, su corazón, la luna y su roce. */
    val LLAMA = Color(0xFFFFB65C)
    val FUEGO = Color(0xFFFFEAB8)
    val LUNA = Color(0xFFE6EEF8)
    val LUNA_RAS = Color(0xFFA2BCE0)
    /** La sombra fría del día (la del paño, el vidrio y el mar). */
    val FRIO = Color(0xFF233A48)

    val LETRA: FontFamily = LetraExtra.bodoni
    val LETRA_TITULO: FontFamily = LetraExtra.bodoniTitulo
}

/**
 * El aire del momento en el estante de Napoleónico: de día (el sol que entra por la ventana), de noche (el modo oscuro: arden
 * la bouillotte, el candelero de un hueco y el farol del pie) y en los Sueños ([noche]: el ocaso en claro, la luna en
 * oscuro). De noche cada color va de su hondo (oscuro, de su tono, con el aire de la noche) a su color según cuánta luz le
 * llega ([luz], de 0 a 1), con el calor de la llama encima. Arriba la noche es azul; en el cuerpo de la librería y en el suelo
 * ([cuerpo]), lo que no se alumbra se hunde en un pardo de caoba (en los Sueños, en un ciruela hondo); en el paño de los
 * casilleros ([fondo]), en verde botella.
 */
internal class AireNapoleon(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false) {
    private val p = PaletaNapoleon
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
        val f = if (cuerpo) 0.09f else 0.1f
        return Color(c.red * f + n.red * 0.85f, c.green * f + n.green * 0.85f, c.blue * f + n.blue * 0.85f, c.alpha)
    }

    /** El color de algo a la luz de la vela: alumbra en naranja, así que el azul y el verde bajan más que el rojo. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.87f, c.blue * 0.62f, c.alpha)

    /**
     * El color al anochecer de los Sueños claros: el mismo cuarto ya en penumbra, cada cosa con su color (el amarillo más hondo,
     * la caoba y el paño con el suyo), solo más bajo y apenas entibiado; el violeta va en las sombras ([sombra]), no aquí.
     */
    private fun alOcaso(c: Color): Color =
        if (cuerpo) Color(c.red * 0.78f, c.green * 0.74f, c.blue * 0.74f, c.alpha)
        else Color(c.red * 0.84f, c.green * 0.76f, c.blue * 0.7f, c.alpha)

    /** Cerca de la llama, lo claro se vuelve ámbar (el amarillo pasa a miel, no a pardo). */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaLlama(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.5f).coerceAtMost(1f), (l * 1.08f).coerceAtMost(1f), l * 0.55f, c.alpha)
        return mezcla(mezcla(c, v, 0.8f), tono, (if (cuerpo) 0.28f else 0.5f) * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        // En el cuerpo la luz cae más deprisa: lo de en medio, entre una llama y otra, se hunde de verdad.
        val e = if (cuerpo) 2.1 else 1.3
        return mezcla(mezcla(hundido(c), ambar(c, k), (1.1f * Math.pow(k.toDouble(), e).toFloat()).coerceAtMost(0.94f)), p.LLAMA, 0.12f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val b = if (noche) alOcaso(c) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /**
     * Lo que toca la mancha de luz de la ventana ([f], de 0 a 1): [c] es el material y [base], su color sin esa luz. De día, el
     * sol blanco y cálido; al ocaso, el naranja; con la luna, el material en un plata frío. Es el color de la superficie bajo esa
     * luz, opaco: no un velo encima.
     */
    fun sol(c: Color, base: Color, f: Float): Color {
        val k = f.coerceIn(0f, 1f)
        return when {
            oscuro -> {
                val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
                val g = mezcla(c, Color(l, l, l), 0.55f)
                val q = 0.38f * k
                Color((base.red + g.red * 0.42f * q).coerceAtMost(1f), (base.green + g.green * 0.58f * q).coerceAtMost(1f), (base.blue + (g.blue * 0.95f + 0.1f) * q).coerceAtMost(1f), c.alpha)
            }
            // El último sol, bajo: un melocotón suave sobre el color de cada cosa, no un naranja que se la coma.
            noche -> {
                val l = Color((base.red * 1.2f + 0.06f).coerceAtMost(1f), (base.green * 1.1f + 0.05f).coerceAtMost(1f), (base.blue * 1.02f + 0.04f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF6C29C), 0.3f), k)
            }
            else -> {
                // El mismo color, más claro y entibiado, sin irse al blanco (que lo lavaría).
                val l = Color((base.red * 1.32f + 0.04f).coerceAtMost(1f), (base.green * 1.22f + 0.03f).coerceAtMost(1f), (base.blue * 1.05f + 0.01f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFFFE6A8), 0.16f), k)
            }
        }
    }

    /** El color de [c] con la luz del momento ([f] como en [dia]) y, si [lit] pasa de 0, con la mancha [m] de la ventana encima. */
    fun en(c: Color, f: Float, cerca: Float, lit: Float, m: ManchaNapoleon?): Color {
        val base = dia(c, f, cerca)
        return if (lit > 0f && m != null) sol(c, base, m.fuerza * lit) else base
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del paño, el vidrio y el mar); de noche, la misma que las demás. */
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
        val apagado = mezcla(Color(t.red * 0.22f, t.green * 0.19f, t.blue * 0.21f, t.alpha), hondo, 0.25f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.45).toFloat()).coerceAtMost(1f))
    }

    /**
     * La cartulina donde va el día, teñida por la luz que le llega: ámbar cerca de la llama, parda honda lejos (nunca más clara
     * que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        // Lejos, cartulina tostada en sombra (parda, no gris), que aún deja leer la tinta (unos 3 a 1, letra grande).
        val lejos = Color(c.red * 0.54f, c.green * 0.41f, c.blue * 0.26f, c.alpha)
        val alumbrado = Color((c.red * 1.04f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.7f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 2.0).toFloat().coerceAtMost(1f))
    }
}

/**
 * La mancha de luz que entra por la ventana del costado y cae en la pared del fondo y en lo que tiene delante: la trama de los
 * cuarterones de las dos hojas, tendida y alargada por el ángulo de la luz, con la sombra del montante de en medio y de los
 * peinazos. [q] es el cuadrilátero de la mancha en el plano de la pared, en anchos (arriba izquierda, arriba derecha, abajo
 * derecha, abajo izquierda); lo que está más adelante (la cornisa, el globo) la recibe corrida [FRENTE] a la izquierda.
 * [fuerza] es lo que aclara.
 */
internal class ManchaNapoleon(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** Un punto de la mancha: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en anchos, corrido [corrido]. */
    fun en(fx: Float, fy: Float, corrido: Float = 0f): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset(tx + (bx - tx) * fy + corrido, ty + (by - ty) * fy)
    }

    /** Un cuarterón ([fx0]..[fx1], [fy0]..[fy1]) proyectado, en un lienzo de ancho [w]. */
    private fun Path.cuarteron(w: Float, fx0: Float, fx1: Float, fy0: Float, fy1: Float, corrido: Float) {
        val a = en(fx0, fy0, corrido); val b = en(fx1, fy0, corrido); val c = en(fx1, fy1, corrido); val d = en(fx0, fy1, corrido)
        moveTo(a.x * w, a.y * w); lineTo(b.x * w, b.y * w); lineTo(c.x * w, c.y * w); lineTo(d.x * w, d.y * w); close()
    }

    /**
     * Los cuarterones de la ventana proyectados (dos hojas de cuatro cuarterones de alto), en un lienzo de ancho [w]: entre
     * ellos, la sombra del montante y de los peinazos; [crece] los agranda (la penumbra).
     */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f): Path {
        val p = Path()
        val montante = 0.07f; val peinazo = 0.05f
        for (hoja in 0 until 2) {
            val x0 = if (hoja == 0) 0f else 0.5f + montante / 2
            val x1 = if (hoja == 0) 0.5f - montante / 2 else 1f
            for (fila in 0 until 4) {
                val y0 = fila * 0.25f + (if (fila > 0) peinazo / 2 else 0f)
                val y1 = (fila + 1) * 0.25f - (if (fila < 3) peinazo / 2 else 0f)
                p.cuarteron(w, x0 - crece, x1 + crece, y0 - crece, y1 + crece, corrido)
            }
        }
        return p
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 dentro de un cuarterón, 0 fuera. */
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
        if (fx > 0.465f && fx < 0.535f) return 0f
        return 1f
    }

    companion object {
        /** Cuánto se corre el plano del frente del mueble respecto de la pared, en anchos (la luz entra desde la izquierda). */
        const val FRENTE = -0.03f
    }
}

/**
 * La mancha de la ventana en este momento, en la pared del fondo, o null (de noche manda la lumbre y la ventana solo enseña el
 * cielo). Cae en la pared por encima de la cornisa; lo que tiene delante (el globo) la corta con su sombra.
 */
internal fun manchaNapoleon(a: AireNapoleon): ManchaNapoleon? = when {
    // La luna, más alta y más tenue: los cuarterones caen más empinados.
    a.oscuro && a.noche -> ManchaNapoleon(floatArrayOf(0.35f, 0.12f, 0.49f, 0.145f, 0.545f, 0.37f, 0.405f, 0.35f), PaletaNapoleon.LUNA, 0.62f)
    a.oscuro -> null
    // Al ocaso, el sol bajo: los cuarterones se tienden y se alargan a lo ancho.
    a.noche -> ManchaNapoleon(floatArrayOf(0.34f, 0.13f, 0.74f, 0.18f, 0.79f, 0.37f, 0.4f, 0.32f), PaletaNapoleon.OCASO, 0.75f)
    else -> ManchaNapoleon(floatArrayOf(0.3f, 0.08f, 0.56f, 0.11f, 0.69f, 0.51f, 0.44f, 0.48f), PaletaNapoleon.SOL, 0.9f)
}

/**
 * La luna de los Sueños oscuros en el cuerpo de la librería (de 0 a 1): entra por la ventana de arriba a la izquierda y roza
 * las cajas de arriba de ese lado; lejos, nada. [fx] y [fy], en anchos del mueble.
 */
internal fun lunaCuerpoNapoleon(a: AireNapoleon, fx: Float, fy: Float): Float {
    if (!(a.oscuro && a.noche)) return 0f
    val dx = fx - 0.12f; val dy = (fy - 0.5f) * 0.75f
    return (1.15f - kotlin.math.sqrt(dx * dx + dy * dy) / 0.95f).coerceIn(0f, 1f)
}

/** Un óvalo inscrito en [r] (para no repetir esquina y tamaño). */
internal fun DrawScope.ovaloNapoleon(color: Color, r: Rect, style: DrawStyle = Fill) = drawOval(color, r.topLeft, r.size, style = style)

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoNapoleon(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoNapoleon(centro: Offset, ancho: Float, alto: Float, a: AireNapoleon, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un tornillo o clavo de latón: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoNapoleon(p: Offset, r: Float, a: AireNapoleon, k: Float, metal: Color = PaletaNapoleon.LATON) {
    drawCircle(a.sombra(0.4f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.brillo(0.55f, k), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el latón o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloNapoleon(c: Offset, r: Float, a: AireNapoleon, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaNapoleon.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaNapoleon.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroNapoleon(r: Rect, a: AireNapoleon, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.24f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Napoleónico, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_NAPOLEON: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.NAPOLEON, false) to juegoTonos(TemaId.NAPOLEON, true) }

private fun cuadradoNapoleon(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaNapoleon(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoNapoleon(t.red - a0.red - dr * f) + cuadradoNapoleon(t.green - a0.green - dg * f) + cuadradoNapoleon(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto (el tafilete, el papel jaspeado y la seda de las entradas): lo bajo, granate y carmesí;
 * lo de en medio, becerro leonado y vitela tostada (nunca blanco); lo alto, azul de Prusia. Siguen la escala del tema
 * (`juegoTonos`: carmesí, crema y azul).
 */
private val TINTES_NAPOLEON = listOf(
    0f to Color(0xFF5E1220), 0.22f to Color(0xFF8E1B2C), 0.37f to Color(0xFFAE4A36), 0.45f to Color(0xFFC4945F), 0.5f to Color(0xFFD3BC8C),
    0.56f to Color(0xFF8FB2A6), 0.63f to Color(0xFF7C99BE), 0.75f to Color(0xFF4A6CA6), 0.87f to Color(0xFF2B4889), 1f to Color(0xFF1C2D66),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_NAPOLEON]; si
 * no está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores: aquí la entrada es un objeto del
 * cuarto, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la de día) y después la luz
 * dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaNapoleon(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_NAPOLEON
    val x = (if (oscuro) enEscalaNapoleon(t, noche) else null) ?: enEscalaNapoleon(t, dia) ?: return t
    for (i in 0 until TINTES_NAPOLEON.size - 1) {
        val (f0, c0) = TINTES_NAPOLEON[i]; val (f1, c1) = TINTES_NAPOLEON[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_NAPOLEON.last().second
}
