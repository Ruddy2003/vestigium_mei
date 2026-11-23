package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble

/**
 * La paleta del estante de Galia: la casa grande de un caballero arverno en Gergovia, en el 52 a. C. El barro de la pared
 * alisado con una lechada de arcilla blanca y, detrás del estante, bañado de almagra; el zarzo que asoma donde se cae; el
 * roble labrado a azuela del estante; la paja y los cabrios ahumados del alero; el hierro de la fragua, el bronce con su
 * verdín, el barro negro bruñido y el rojo del ánfora; la lana a cuadros, el mimbre, el cuero, el cuerno y la haya de las
 * tablillas. Dos temperaturas: lo cálido es el roble, la almagra, el bronce y la lumbre; lo frío, el verde del roble vivo,
 * el hierro, el humo y la noche del robledal.
 */
internal object PaletaGalia {
    /** El barro de la pared con su lechada de arcilla blanca: su cara, su luz (al sol), su sombra y el barro desnudo. */
    val BLANCO = Color(0xFFE6D9BC)
    val BLANCO_LUZ = Color(0xFFF8EFD8)
    val BLANCO_SOMBRA = Color(0xFFBFAC88)
    val BARRO = Color(0xFFB0905E)
    /** La almagra del barro de detrás del estante: su cara, su luz, su sombra y lo hondo. */
    val ALMAGRA = Color(0xFF833623)
    val ALMAGRA_LUZ = Color(0xFFB45A38)
    val ALMAGRA_OSC = Color(0xFF5E2216)
    val ALMAGRA_HONDA = Color(0xFF34120B)
    /** El hollín de la cenefa y de los cabrios. */
    val HOLLIN = Color(0xFF29231E)
    /** El zarzo de avellano que asoma donde se cayó el barro. */
    val ZARZO = Color(0xFF6A4E30)
    val ZARZO_LUZ = Color(0xFF9E7E52)
    /** El roble del estante: su cara, su luz, su sombra y lo hondo. */
    val ROBLE = Color(0xFF7E5836)
    val ROBLE_LUZ = Color(0xFFAE8456)
    val ROBLE_OSC = Color(0xFF4C321D)
    val ROBLE_HONDO = Color(0xFF26180D)
    /** La paja del alero, ennegrecida de humo, y los cabrios. */
    val PAJA = Color(0xFFB89C62)
    val PAJA_HUMO = Color(0xFF54442C)
    val CABRIO = Color(0xFF4A3421)
    /** El hierro de la fragua: las escuadras, los clavos, el soporte de la tea. */
    val HIERRO = Color(0xFF36363A)
    val HIERRO_LUZ = Color(0xFF8E9098)
    /** El bronce, su luz, su sombra y su verdín. */
    val BRONCE = Color(0xFFB07A3A)
    val BRONCE_LUZ = Color(0xFFEDC27A)
    val BRONCE_OSC = Color(0xFF6A4018)
    val VERDIN = Color(0xFF5F8C76)
    /** El barro negro bruñido de la tinaja y de las escudillas. */
    val NEGRO = Color(0xFF2F2A27)
    val NEGRO_LUZ = Color(0xFF746A60)
    /** El barro rojo del ánfora y el pardo de las ollas. */
    val ROJO = Color(0xFFB9663E)
    val ROJO_LUZ = Color(0xFFE29A6C)
    val ROJO_OSC = Color(0xFF773920)
    val PARDO = Color(0xFF8C6A48)
    /** La lana: roja de rubia, verde, gualda y cruda. */
    val LANA_ROJA = Color(0xFF9A3A28)
    val LANA_VERDE = Color(0xFF3F5E2A)
    val LANA_OCRE = Color(0xFFCDA44A)
    val LANA_CRUDA = Color(0xFFDCD0AE)
    /** El azul del glasto, en la lana y en la cenefa. */
    val GLASTO = Color(0xFF3E5A7C)
    /** La haya de las tablillas, su luz, su sombra y sus motas; la cera. */
    val HAYA = Color(0xFFCFA97C)
    val HAYA_LUZ = Color(0xFFE9D0A6)
    val HAYA_OSC = Color(0xFF9E7850)
    val HAYA_MOTA = Color(0xFF86583A)
    val CERA = Color(0xFFC09A52)
    val CERA_OSC = Color(0xFF8E6E34)
    /** El cuero de las correas, el mimbre y el cuerno. */
    val CUERO = Color(0xFF6B4226)
    val CUERO_OSC = Color(0xFF3C2414)
    val MIMBRE = Color(0xFFC8A364)
    val MIMBRE_LUZ = Color(0xFFE6CD94)
    val MIMBRE_OSC = Color(0xFF8A6836)
    val CUERNO = Color(0xFFE2CC98)
    val CUERNO_OSC = Color(0xFF8A6A3E)
    /** La carne curada, su grasa, las cebollas y su piel; el vidrio azul de los brazaletes. */
    val CARNE = Color(0xFF9A4632)
    val GRASA = Color(0xFFE6D4AE)
    val CEBOLLA = Color(0xFFC48446)
    val CEBOLLA_LUZ = Color(0xFFE8B678)
    val VIDRIO = Color(0xFF2F62A8)
    /** El suelo de tierra apisonada, los juncos y la piedra del hogar. */
    val TIERRA = Color(0xFF6C573C)
    val TIERRA_LUZ = Color(0xFF94805E)
    val JUNCO = Color(0xFFB9A05C)
    val JUNCO_OSC = Color(0xFF7A6634)
    val PIEDRA = Color(0xFF8C857A)
    val PIEDRA_OSC = Color(0xFF55504A)
    val CENIZA = Color(0xFFA9A398)
    /** La tinta del estilo en la cera. */
    val TINTA = Color(0xFF221A10)
    /** El sol de la puerta, el ocaso de los Sueños claros y su sombra violeta. */
    val SOL = Color(0xFFFFF0C8)
    val OCASO = Color(0xFFF4A05A)
    val SOMBRA_OCASO = Color(0xFF3E2A5E)
    /** La noche: arriba, el verde negro del robledal; en el cuerpo, el roble ahumado; en el fondo, la almagra hecha vino. */
    val NOCHE = Color(0xFF09170F)
    val CUERPO = Color(0xFF1D150B)
    val FONDO_NOCHE = Color(0xFF2A0D0A)
    /** La noche de los Sueños: un azul verdoso de luna, más hondo en el cuerpo y en el fondo. */
    val NOCHE_SUENOS = Color(0xFF0C1824)
    val CUERPO_SUENOS = Color(0xFF151724)
    val FONDO_SUENOS = Color(0xFF1A1222)
    /** La llama de la tea y del sebo, su corazón, la brasa, la luna y su roce. */
    val LLAMA = Color(0xFFFFB04A)
    val FUEGO = Color(0xFFFFE8B0)
    val BRASA = Color(0xFFE0602A)
    val LUNA = Color(0xFFE4ECF2)
    val LUNA_RAS = Color(0xFFA6C0DC)
    /** La sombra fría del día (la del hierro y la del barro negro). */
    val FRIO = Color(0xFF2C3442)

    val LETRA: FontFamily = LetraExtra.ysabeau
    val TITULO: FontFamily = LetraExtra.piazzolla
}

/**
 * El aire del momento en el estante de Galia: de día (el sol de la puerta), de noche (el modo oscuro: arden la tea, la
 * lamparilla de sebo de los huecos y el hogar del pie) y en los Sueños ([noche]: el ocaso en claro, la luna en oscuro). De
 * noche cada color va de su hondo a su color según cuánta luz le llega ([cerca], de 0 a 1), con el calor de la llama encima.
 * Arriba la noche es el verde negro del robledal; en el cuerpo del estante y en el suelo ([cuerpo]), el roble ahumado; en el
 * fondo de los casilleros ([fondo]), la almagra se hunde en vino.
 */
internal class AireGalia(val oscuro: Boolean, val noche: Boolean, val cuerpo: Boolean = false, val fondo: Boolean = false) {
    private val p = PaletaGalia
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche
    /** El aire en que se hunde lo que no se alumbra. */
    val hondo: Color get() = when {
        oscuro && fondo -> if (noche) p.FONDO_SUENOS else p.FONDO_NOCHE
        oscuro && cuerpo -> if (noche) p.CUERPO_SUENOS else p.CUERPO
        oscuro -> if (noche) p.NOCHE_SUENOS else p.NOCHE
        noche -> p.SOMBRA_OCASO
        else -> sombraMueble(1f)
    }

    /** El color propio de algo, hundido: oscuro, algo de su tono, con el aire de la noche. */
    fun hundido(c: Color): Color {
        val n = hondo
        val f = if (cuerpo) 0.14f else 0.1f
        return Color((c.red * f + n.red * 0.86f).coerceAtMost(1f), (c.green * f + n.green * 0.86f).coerceAtMost(1f), (c.blue * f + n.blue * 0.86f).coerceAtMost(1f), c.alpha)
    }

    /** El color de algo a la luz del fuego: alumbra en naranja, así que el azul y el verde bajan más que el rojo. */
    private fun aLaLlama(c: Color): Color = Color((c.red * 1.1f).coerceAtMost(1f), c.green * 0.85f, c.blue * 0.58f, c.alpha)

    /** El color al ocaso de los Sueños claros: algo más bajo y más tibio. */
    private fun alOcaso(c: Color): Color = Color(c.red * 0.96f, c.green * 0.85f, c.blue * 0.8f, c.alpha)

    /** Cerca de la llama, lo claro se vuelve ámbar (el barro blanco pasa a miel, no a amarillo sucio). */
    private fun ambar(c: Color, k: Float): Color {
        val v = aLaLlama(c)
        val l = c.red * 0.3f + c.green * 0.59f + c.blue * 0.11f
        val tono = Color((l * 1.5f).coerceAtMost(1f), (l * 1.06f).coerceAtMost(1f), l * 0.52f, c.alpha)
        return mezcla(mezcla(c, v, 0.8f), tono, (if (cuerpo) 0.3f else 0.5f) * k)
    }

    fun luz(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) mezcla(alOcaso(c), p.LLAMA, 0.1f * k) else c
        return mezcla(mezcla(hundido(c), ambar(c, k), (1.08f * Math.pow(k.toDouble(), 1.3).toFloat()).coerceAtMost(0.94f)), if (cuerpo) p.BRASA else p.LLAMA, 0.12f * k)
    }

    /** De día, más claro (positivo) o más hondo (negativo); de noche, como [luz]. */
    fun dia(c: Color, f: Float, cerca: Float = 1f): Color {
        if (oscuro) return luz(c, cerca)
        val b = if (noche) mezcla(alOcaso(c), p.LLAMA, 0.08f * cerca) else c
        return if (f > 0f) mezcla(b, luzMueble(1f), f) else if (f < 0f) mezcla(b, hondo, -f) else b
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun sombra(a: Float): Color =
        if (oscuro) hondo.copy(alpha = (a * 1.3f).coerceIn(0f, 1f)) else if (noche) hondo.copy(alpha = (a * 1.15f).coerceIn(0f, 1f)) else sombraMueble(a)
    /** La sombra fría del día (la del hierro y del barro negro); de noche, la misma que las demás. */
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
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (el verde sigue verde,
     * hondo lejos y encendido cerca); al ocaso, tibio.
     */
    fun animo(t: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(t) else t
        val apagado = mezcla(Color(t.red * 0.24f, t.green * 0.22f, t.blue * 0.2f, t.alpha), hondo, 0.25f)
        val encendido = mezcla(t, aLaLlama(t), 0.5f)
        return mezcla(apagado, encendido, (1.1f * Math.pow(k.toDouble(), 1.2).toFloat()).coerceAtMost(1f))
    }

    /**
     * La haya o la cera donde va el día, teñida por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca
     * más clara que lo alumbrado), y siempre con contraste para la tinta.
     */
    fun etiqueta(c: Color, cerca: Float): Color {
        val k = cerca.coerceIn(0f, 1f)
        if (!oscuro) return if (noche) alOcaso(c) else c
        val lejos = Color(c.red * 0.4f, c.green * 0.29f, c.blue * 0.17f, c.alpha)
        val alumbrado = Color((c.red * 1.05f).coerceAtMost(1f), c.green * 0.92f, c.blue * 0.68f, c.alpha)
        return mezcla(lejos, alumbrado, Math.pow(k.toDouble(), 1.5).toFloat().coerceAtMost(1f))
    }

    /**
     * Lo que toca la mancha de la puerta ([f], de 0 a 1): [base] es el color sin esa luz. De día, el sol cálido; al ocaso, el
     * naranja; con la luna, un plata frío. Es el color de la superficie bajo esa luz, opaco: no un velo encima.
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
            noche -> mezcla(base, Color((base.red * 1.3f + 0.12f).coerceAtMost(1f), (base.green * 1.0f + 0.04f).coerceAtMost(1f), base.blue * 0.72f, c.alpha), k)
            else -> {
                val l = Color((base.red * 1.32f + 0.04f).coerceAtMost(1f), (base.green * 1.25f + 0.02f).coerceAtMost(1f), (base.blue * 1.06f).coerceAtMost(1f), c.alpha)
                mezcla(base, mezcla(l, Color(0xFFF0D29C), 0.3f), k)
            }
        }
    }
}

/**
 * La mancha de luz que entra por la puerta (a la izquierda, fuera del cuadro) y cae en la pared y en el estante: un
 * cuadrilátero en anchos del mueble (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda) en el plano de la pared;
 * lo que está más adelante (el dintel, el poste, las tablas) la recibe corrida [FRENTE] hacia la puerta. [fuerza] es lo que
 * aclara. La mancha se tiende con el ángulo del sol: el borde de arriba es la sombra del dintel de la puerta y el de la
 * derecha, la de la jamba.
 */
internal class ManchaGalia(val q: FloatArray, val color: Color, val fuerza: Float) {
    /** El camino de la mancha en un lienzo de ancho [w], corrido [corrido] anchos; [crece], su penumbra. */
    fun forma(w: Float, corrido: Float = 0f, crece: Float = 0f): Path = Path().apply {
        moveTo((q[0] + corrido - crece) * w, (q[1] - crece) * w)
        lineTo((q[2] + corrido + crece) * w, (q[3] - crece) * w)
        lineTo((q[4] + corrido + crece) * w, (q[5] + crece) * w)
        lineTo((q[6] + corrido - crece) * w, (q[7] + crece) * w)
        close()
    }

    /** Cuánto le toca a ([x], [y]) (anchos) del plano corrido [corrido]: 1 dentro, 0 fuera, con un borde corto. */
    fun toca(x: Float, y: Float, corrido: Float = 0f): Float {
        val ax = q[0] + corrido; val ay = q[1]
        val ux = q[2] - q[0]; val uy = q[3] - q[1]
        val vx = q[6] - q[0]; val vy = q[7] - q[1]
        val det = ux * vy - uy * vx
        if (kotlin.math.abs(det) < 1e-6f) return 0f
        val px = x - ax; val py = y - ay
        val fx = (px * vy - py * vx) / det
        val fy = (ux * py - uy * px) / det
        if (fx < -0.04f || fx > 1.04f || fy < -0.04f || fy > 1.04f) return 0f
        val borde = minOf(fx + 0.04f, 1.04f - fx, fy + 0.04f, 1.04f - fy) / 0.08f
        return borde.coerceIn(0f, 1f)
    }

    companion object {
        /** Cuánto se corre el plano del frente del estante respecto de la pared, en anchos (la luz entra desde la izquierda). */
        const val FRENTE = -0.035f
    }
}

/** La mancha de la puerta en este momento, o null (de noche manda la lumbre; la puerta no se ve). */
internal fun manchaGalia(a: AireGalia): ManchaGalia? = when {
    // La luna, alta y fría, entra empinada y cae más abajo, sobre la primera repisa.
    a.oscuro && a.noche -> ManchaGalia(floatArrayOf(-0.02f, 0.3f, 0.3f, 0.4f, 0.5f, 0.98f, 0.12f, 0.86f), PaletaGalia.LUNA, 0.72f)
    a.oscuro -> null
    // Al ocaso, el sol bajo: la mancha se tiende casi horizontal y muy larga.
    a.noche -> ManchaGalia(floatArrayOf(-0.02f, 0.12f, 0.5f, 0.22f, 0.92f, 0.46f, 0.1f, 0.34f), PaletaGalia.OCASO, 1f)
    else -> ManchaGalia(floatArrayOf(-0.02f, 0.1f, 0.34f, 0.16f, 0.62f, 0.66f, 0.06f, 0.6f), PaletaGalia.SOL, 0.9f)
}

/**
 * La luz de la puerta sobre un objeto de silueta [sil] (lo de encima del estante, lo colgado): solo donde la mancha lo
 * alcanza, con el borde de la mancha cortándolo, en el plano corrido [corrido]. Se suma a lo que ya está pintado.
 */
internal fun DrawScope.luzDeLaPuertaGalia(sil: Path, a: AireGalia, m: ManchaGalia?, corrido: Float = ManchaGalia.FRENTE * 0.6f) {
    if (m == null) return
    val (col, alfa) = when {
        a.oscuro -> Color(0xFF6A86B8) to 0.32f
        a.noche -> PaletaGalia.OCASO to 0.42f
        else -> Color(0xFFFFC47A) to 0.34f
    }
    clipPath(sil) { clipPath(m.forma(size.width, corrido)) { drawRect(col.copy(alpha = alfa * m.fuerza), Offset.Zero, size, blendMode = BlendMode.Screen) } }
}

/** Un óvalo inscrito en [r], del color [c] (lleno o con el trazo [style]). */
internal fun DrawScope.ovaloGalia(c: Color, r: Rect, style: DrawStyle = Fill) = drawOval(c, r.topLeft, r.size, style = style)

/** Un óvalo inscrito en [r], con el pincel [b]. */
internal fun DrawScope.ovaloGalia(b: Brush, r: Rect) = drawOval(b, r.topLeft, r.size)

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoGalia(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde cae la luz ([hacia]: 1, a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoGalia(centro: Offset, ancho: Float, alto: Float, a: AireGalia, fuerza: Float = 0.34f, hacia: Float = 1f) =
    drawOval(a.sombra(if (a.oscuro) fuerza * 1.25f else fuerza), Offset(centro.x - ancho * (0.5f - 0.1f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de hierro de cabeza gruesa, forjada: su sombra, la cabeza con sus facetas y el punto de luz. */
internal fun DrawScope.clavoGalia(p: Offset, r: Float, a: AireGalia, k: Float, metal: Color = PaletaGalia.HIERRO) {
    drawCircle(a.sombra(0.42f), r * 1.08f, p + Offset(r * 0.42f, r * 0.55f))
    drawCircle(a.luz(metal, k), r, p)
    drawCircle(a.dia(PaletaGalia.HIERRO_LUZ, 0f, k).copy(alpha = 0.55f), r * 0.55f, p - Offset(r * 0.25f, r * 0.25f))
    drawCircle(a.brillo(0.55f, k), r * 0.26f, p - Offset(r * 0.38f, r * 0.38f))
}

/** El destello de la luz en el bronce, el barro bruñido o el vidrio (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloGalia(c: Offset, r: Float, a: AireGalia, k: Float) {
    if (!a.oscuro) { drawCircle(a.claro(0.85f), r, c); return }
    if (k < 0.05f) return
    drawCircle(PaletaGalia.LLAMA.copy(alpha = (0.4f * k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaGalia.FUEGO.copy(alpha = (1.1f * k).coerceAtMost(0.95f)), r, c)
}

/** Un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte): el claro hacia la luz y la sombra al otro lado. */
internal fun DrawScope.cilindroGalia(r: Rect, a: AireGalia, k: Float, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(a.brillo(0.16f * fuerza, k), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(a.brillo(0.3f * fuerza, k), Offset(x(0.14f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(a.sombra(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(a.sombra(0.26f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Las escalas del ánimo de Galia, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_GALIA: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.GALIA, false) to juegoTonos(TemaId.GALIA, true) }

private fun cuadradoGalia(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaGalia(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoGalia(t.red - a0.red - dr * f) + cuadradoGalia(t.green - a0.green - dg * f) + cuadradoGalia(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto, para la lana, el mimbre y el cuero: lo bajo, el verde del roble (la lana teñida, el
 * mimbre con su corteza); lo de en medio, lo crudo (la lana sin teñir, el mimbre pelado, el cuero de alumbre); lo alto, la
 * gualda y el dorado (la lana de gualda, el mimbre cocido, el cuero curtido). Siguen la escala del tema (`juegoTonos`).
 */
private val TINTES_GALIA = listOf(
    0f to Color(0xFF2C4A1E), 0.22f to Color(0xFF41662A), 0.38f to Color(0xFF7E8C4E), 0.5f to Color(0xFFDDD0AA),
    0.62f to Color(0xFFDCB656), 0.78f to Color(0xFFC48E28), 1f to Color(0xFF94621A),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_GALIA]; si no
 * está en la escala, queda igual. En el modo oscuro la escala del tema cambia de colores: aquí la entrada es un objeto de la
 * casa, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la de día) y después la luz
 * dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaGalia(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_GALIA
    val x = (if (oscuro) enEscalaGalia(t, noche) else null) ?: enEscalaGalia(t, dia) ?: return t
    for (i in 0 until TINTES_GALIA.size - 1) {
        val (f0, c0) = TINTES_GALIA[i]; val (f1, c1) = TINTES_GALIA[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_GALIA.last().second
}
