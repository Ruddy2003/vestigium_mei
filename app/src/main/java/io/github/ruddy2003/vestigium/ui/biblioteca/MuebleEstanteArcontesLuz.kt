package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import io.github.ruddy2003.vestigium.ui.LetraExtra
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.TrazosArcontes
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.sombraMueble
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La paleta del estante de Arcontes: la cueva de la reunión escondida, la misma del Ideario (`IdeasArcontes`). La caliza de la
 * pared y de los sillares, el olivo de las arquetas (aquí, del armario), el bronce con su cardenillo, el hueso, el papiro y el
 * cordel de lino, el barro, el cuero, el pergamino, la lana del manto, los juncos, la tierra y la sal. Dos temperaturas: lo
 * cálido es el olivo, el bronce, el barro, el oro del sol y la llama; lo frío, la caliza en sombra, el añil del lago en los tintes,
 * el blanco violeta de la grieta y la noche azul negra del cielo antiguo.
 */
internal object PaletaArcontes {
    /** La caliza de la cueva (la del Ideario): su cara, su luz, su sombra, el fondo de los huecos y lo más hondo. */
    val CALIZA = TrazosArcontes.CALIZA
    val CALIZA_LUZ = Color(0xFFEBDFBF)
    val CALIZA_SOMBRA = TrazosArcontes.CALIZA_SOMBRA
    val HUECO = Color(0xFF8E7C5E)
    val HUECO_HONDO = Color(0xFF5A4C3C)
    /** La roca de los bordes y del techo de la cueva, que se tuerce hacia dentro y queda en sombra. */
    val ROCA = Color(0xFF6F5F55)
    /** El olivo del armario (el de las arquetas): su cara, su luz, su sombra, lo hondo y la veta que se arremolina. */
    val OLIVO = Color(0xFFB39567)
    val OLIVO_LUZ = Color(0xFFD6BE8E)
    val OLIVO_OSC = Color(0xFF7C623E)
    val OLIVO_HONDO = Color(0xFF4A3824)
    val OLIVO_VETA = Color(0xFF4F3B25)
    /** El bronce de las chapas y los aros, su luz, su sombra y el cardenillo que le sale en las juntas. */
    val BRONCE = Color(0xFF8C6A34)
    val BRONCE_LUZ = Color(0xFFC9A45E)
    val BRONCE_OSC = Color(0xFF574019)
    val CARDENILLO = Color(0xFF5F8F7A)
    /** El hueso de los pasadores. */
    val HUESO = Color(0xFFE8DEC6)
    /** El papiro de las hojas, el de las etiquetas (de valor medio), su fibra; el cordel de lino. */
    val PAPIRO = Color(0xFFEDE3C8)
    val PAPIRO_ETIQUETA = Color(0xFFC4AD7B)
    val PAPIRO_OSC = Color(0xFFB9A57A)
    val FIBRA = Color(0xFF8E7650)
    val CORDEL = Color(0xFFCDBB8E)
    /** La tinta de carbón, violácea de tan negra. */
    val TINTA = Color(0xFF1E1A2B)
    /** El barro cocido de las lámparas y las jarras, el claro y el requemado. */
    val BARRO = TrazosArcontes.BARRO
    val BARRO_CLARO = TrazosArcontes.BARRO_CLARO
    val BARRO_OSC = Color(0xFF5C3119)
    /** El cuero sin teñir, su correa oscura y el pergamino sin teñir. */
    val CUERO = TrazosArcontes.CUERO
    val CUERO_CRUDO = Color(0xFF9A7650)
    val CORREA = Color(0xFF4A321E)
    val PERGAMINO = Color(0xFFD8C69C)
    /** La lana del manto del Ideario, la lana cruda y sus dos franjas oscuras. */
    val LANA = Color(0xFF8B7357)
    val LANA_CRUDA = Color(0xFFBFA982)
    val FRANJA = Color(0xFF3C2C24)
    /** Los juncos de los cestos y la estera. */
    val JUNCO = Color(0xFFB59A5E)
    val JUNCO_OSC = Color(0xFF7A6438)
    /** La tierra apisonada del suelo (la del Ideario) y su sombra. */
    val TIERRA = Color(0xFF7F6A4C)
    val TIERRA_OSC = Color(0xFF574631)
    /** La sal, la cera oscura de las tablillas, el plomo de las pesas y el verde del hisopo. */
    val SAL = Color(0xFFF2EEE4)
    val CERA = Color(0xFF3E3226)
    val PLOMO = Color(0xFF6C6A72)
    val HISOPO = Color(0xFF6E7048)
    val HISOPO_CLARO = Color(0xFF8E9160)
    /** El oro de más del sol (el del Demiurgo) y la luz del sol en la caliza. */
    val ORO = TrazosArcontes.ORO_DEMIURGO
    val SOL = Color(0xFFFFE6A6)
    /** La llama de la lámpara (la del Ideario), su halo, su corazón y la brasa del brasero. */
    val LLAMA = TrazosArcontes.LLAMA
    val LLAMA_HALO = TrazosArcontes.LLAMA_HALO
    val FUEGO = Color(0xFFFFF0CF)
    val BRASA = Color(0xFFE2582C)
    /** La grieta: el blanco y su halo violeta, la única luz que no es de este mundo. */
    val GRIETA = TrazosArcontes.GRIETA
    val GRIETA_HALO = TrazosArcontes.GRIETA_HALO
    /** La noche de la cueva: el azul negro del cielo antiguo (la del Ideario), la del mueble y la de los Sueños. */
    val NOCHE = Color(0xFF090B1D)
    val NOCHE_CUERPO = Color(0xFF0D0E1E)
    val NOCHE_SUENOS = Color(0xFF080B20)
    /** La luna que entra por la boca y su roce frío. */
    val LUNA = Color(0xFFD9E1F2)
    val LUNA_RAS = Color(0xFFA8B6DA)
    /** El ocaso de los Sueños claros: la luz melocotón y la sombra violeta. */
    val OCASO = Color(0xFFF4BE92)
    val SOMBRA_OCASO = Color(0xFF3E2C58)

    val LETRA: FontFamily = LetraExtra.caudex
}

/**
 * El aire del momento en el estante de Arcontes: de día (el sol que entra por la boca de la cueva, fuera del cuadro arriba a la
 * izquierda, como en el Ideario), de noche (el modo oscuro: arden la lámpara de encima del armario, la de un hueco y el brasero
 * del pie) y en los Sueños ([noche]: el último sol en claro, la luna en oscuro).
 */
internal class AireArcontes(val oscuro: Boolean, val noche: Boolean) {
    /** Si arden las luces (de noche siempre; en los Sueños claros, recién prendidas). */
    val lumbre: Boolean get() = oscuro || noche

    /** La mancha que la boca deja en la pared: la del sol de día, la del último sol en los Sueños claros y la de la luna. */
    val parche: FloatArray? get() = when {
        oscuro && noche -> LUNA_Q
        oscuro -> null
        noche -> OCASO_Q
        else -> SOL_Q
    }

    /** Para pintar algo dos veces, fuera y dentro de la mancha: 0, todo fuera; 1, todo dentro; negativo, según dónde esté. */
    var forzar = -1f

    /**
     * Cuánto le llega de la mancha de la boca a un punto (anchos del mueble): 1 dentro, 0 fuera, con un borde decidido y
     * despejado. De día, la parte de la izquierda pasa por el velo de lino y queda más baja, con las rayas de sus pliegues.
     */
    fun mancha(x: Float, y: Float): Float {
        val q = parche ?: return 0f
        var v = if (forzar >= 0f) forzar else {
            var m = distancia(q, x, y)
            m += 0.007f * sin(x * 37f + y * 23f) + 0.004f * sin(x * 91f - y * 53f)
            val f = (m / BORDE).coerceIn(0f, 1f)
            f * f * (3f - 2f * f)
        }
        if (v > 0f && q === SOL_Q) {
            // El velo: por la izquierda la luz pasa por el lino (más baja) y deja los pliegues en rayas.
            val borde = 0.17f + 0.12f * y
            val t = ((borde - x) / 0.03f).coerceIn(0f, 1f)
            if (t > 0f) v *= 1f - t * (0.42f + 0.16f * sin(x * 120f + y * 8f))
        }
        return v
    }

    /** La luz de día de fondo: más clara arriba a la izquierda, donde está la boca; más honda abajo a la derecha. */
    fun ambiente(x: Float, y: Float): Float {
        if (oscuro) return 0f
        val dx = x + 0.04f; val dy = y * 0.6f
        return exp(-1.3f * sqrt(dx * dx + dy * dy))
    }

    /** El tono de algo que está en ([x], [y]) (anchos), con la lumbre [k] que le llega y la parte de ella que es brasa ([rojo]). */
    fun en(x: Float, y: Float, k: Float, rojo: Float = 0f, cuerpo: Boolean = false): TonoArcontes =
        TonoArcontes(this, k.coerceIn(0f, 1f), mancha(x, y), ambiente(x, y), rojo.coerceIn(0f, 1f), cuerpo)

    companion object {
        /** El sol de la boca: una mancha ancha en la pared de la corona, más alta a la izquierda, que baja al costado izquierdo. */
        val SOL_Q = floatArrayOf(-0.06f, 0.1f, 0.3f, 0.055f, 0.6f, 0.12f, 0.68f, 0.3f, 0.52f, 0.6f, 0.14f, 0.76f, -0.06f, 0.72f)
        /** El último sol: entra bajo y largo, más tendido, y llega a la cornisa y a lo alto de las repisas. */
        val OCASO_Q = floatArrayOf(-0.06f, 0.3f, 0.55f, 0.27f, 0.92f, 0.36f, 0.74f, 0.82f, -0.06f, 1.08f)
        /** La luna: una mancha alta y corta en la pared de arriba a la izquierda. */
        val LUNA_Q = floatArrayOf(0f, 0.09f, 0.2f, 0.06f, 0.36f, 0.1f, 0.42f, 0.28f, 0.3f, 0.42f, 0.06f, 0.46f, -0.02f, 0.3f)
        /** El ancho del borde de la mancha (su penumbra), en anchos: un borde decidido, no un velo. */
        const val BORDE = 0.016f

        /** Lo que se mete ([x], [y]) en el polígono convexo [q] (en el orden de las agujas del reloj): positivo dentro. */
        fun distancia(q: FloatArray, x: Float, y: Float): Float {
            var m = Float.MAX_VALUE
            val n = q.size / 2
            for (i in 0 until n) {
                val ax = q[2 * i]; val ay = q[2 * i + 1]
                val bx = q[(2 * i + 2) % q.size]; val by = q[(2 * i + 3) % q.size]
                val dx = bx - ax; val dy = by - ay
                val l = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-5f)
                m = minOf(m, ((x - ax) * -dy + (y - ay) * dx) / l)
            }
            return m
        }
    }
}

/**
 * El tono de un sitio: cómo se pinta lo que está ahí. De día, su color con la luz de fondo ([amb]: más claro y tibio cerca de la
 * boca, más hondo lejos) y, donde cae la mancha del sol ([sol]), dorado de más. De noche, de su hondo (oscuro, de su mismo tono,
 * con el azul negro del cielo antiguo) a su color según [k] (la lumbre que le llega), con la llama encima (o la brasa, [rojo]).
 * Al ocaso, más bajo y con el melocotón donde toca [sol]; con la luna, la plata fría donde toca.
 */
internal class TonoArcontes(val a: AireArcontes, val k: Float, val sol: Float, val amb: Float, val rojo: Float, val cuerpo: Boolean) {
    private val p = PaletaArcontes

    /** El aire en que se hunde lo que no se alumbra. */
    private val hondo: Color get() = when {
        a.oscuro && cuerpo -> p.NOCHE_CUERPO
        a.oscuro && a.noche -> p.NOCHE_SUENOS
        a.oscuro -> p.NOCHE
        a.noche -> p.SOMBRA_OCASO
        else -> SOMBRA_MUEBLE
    }

    /** La lumbre del sitio: la llama, o la brasa si le llega más del brasero. */
    private val lumbre: Color get() = if (rojo > 0f) mezcla(p.LLAMA, p.BRASA, rojo * 0.7f) else p.LLAMA

    /** El color propio hundido: oscuro, de su mismo tono (no gris), con el aire de la noche. */
    private fun hundido(c: Color): Color {
        val n = hondo
        val f = if (cuerpo) 0.17f else 0.13f
        return Color(c.red * f + n.red * 0.82f, c.green * f + n.green * 0.82f, c.blue * f + n.blue * 0.82f, c.alpha)
    }

    /** El color a la luz de la llama: el rojo sube y el azul baja (más aún junto a la brasa). */
    private fun aLaLlama(c: Color): Color =
        Color((c.red * (1.1f + 0.05f * rojo)).coerceAtMost(1f), c.green * (0.9f - 0.1f * rojo), c.blue * (0.64f - 0.12f * rojo), c.alpha)

    /** El color de [c] con la luz del momento; [f] más claro (positivo) o más hondo (negativo), como lo pinta la luz. */
    fun c(c: Color, f: Float = 0f): Color {
        if (!a.oscuro) {
            val b = if (a.noche) Color(c.red * 0.9f, c.green * 0.89f, c.blue * 0.92f, c.alpha) else c
            val d = if (f > 0f) mezcla(b, LUZ_MUEBLE, f)
                else if (f < 0f) (if (a.noche) mezcla(b, Color(b.red * 0.5f, b.green * 0.5f, b.blue * 0.6f, b.alpha), -f * 1.3f) else mezcla(b, hondo, -f))
                else b
            if (a.noche) {
                // El último sol pinta solo donde cae: el mismo color virado a melocotón y un paso más claro.
                val l = Color((d.red * 1.13f + 0.07f).coerceAtMost(1f), (d.green * 1.04f + 0.035f).coerceAtMost(1f), (d.blue * 0.95f + 0.02f).coerceAtMost(1f), c.alpha)
                return mezcla(mezcla(d, mezcla(l, p.OCASO, 0.26f), sol * 0.9f), p.LLAMA, 0.12f * k)
            }
            // De día: lo cercano a la boca más claro y tibio; lo lejano, más hondo; y donde cae el sol, el oro de más.
            val luz = amb - 0.45f
            var v = if (luz > 0f) mezcla(d, LUZ_MUEBLE, 0.3f * luz) else mezcla(d, SOMBRA_MUEBLE, -0.22f * luz)
            if (sol > 0f) {
                val dorado = Color((v.red * 1.07f + 0.07f).coerceAtMost(1f), (v.green * 1.04f + 0.055f).coerceAtMost(1f), (v.blue * 0.92f + 0.03f).coerceAtMost(1f), c.alpha)
                v = mezcla(v, mezcla(dorado, p.SOL, 0.06f), sol * 0.88f)
            }
            return v
        }
        val base = if (f > 0f) mezcla(c, LUZ_MUEBLE, f * 0.4f) else if (f < 0f) mezcla(c, hondo, -f * 0.6f) else c
        val e = if (cuerpo) 1.9 else 1.6
        val kk = (1.12f * Math.pow(k.toDouble(), e).toFloat()).coerceAtMost(0.93f)
        var v = mezcla(hundido(base), mezcla(base, aLaLlama(base), 0.6f), kk)
        v = mezcla(v, lumbre, 0.13f * k)
        if (sol > 0f) {
            // La luna: el mismo color, un poco más claro y frío donde toca (la caliza sigue caliza; no vela lo demás).
            val q = 0.24f * sol * sol
            v = Color((v.red + (c.red * 0.78f + 0.02f) * q).coerceAtMost(1f), (v.green + (c.green * 0.86f + 0.04f) * q).coerceAtMost(1f),
                (v.blue + (c.blue * 0.98f + 0.12f) * q).coerceAtMost(1f), c.alpha)
        }
        return v
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun s(al: Float): Color =
        if (a.oscuro) hondo.copy(alpha = (al * 1.3f).coerceIn(0f, 1f)) else if (a.noche) hondo.copy(alpha = (al * 1.1f).coerceIn(0f, 1f)) else sombraMueble(al)

    /** El brillo de lo que mira a la luz: de día, crema (dorado en la mancha); al ocaso, melocotón; de noche, el filo de la llama. */
    fun b(al: Float): Color = when {
        !a.oscuro && a.noche -> mezcla(LUZ_MUEBLE, p.OCASO, 0.55f).copy(alpha = (al * (0.25f + 0.7f * sol)).coerceIn(0f, 1f))
        !a.oscuro -> mezcla(LUZ_MUEBLE, p.SOL, 0.5f * sol).copy(alpha = (al * (0.7f + 0.3f * amb + 0.3f * sol)).coerceIn(0f, 1f))
        sol > 0.05f && sol > k -> p.LUNA_RAS.copy(alpha = (al * 0.8f * sol).coerceIn(0f, 1f))
        else -> lumbre.copy(alpha = (al * 0.95f * k).coerceIn(0f, 1f))
    }

    /** El filo de la llama en lo que la mira, de noche; al ocaso, el del sol rasante; de día, el del sol en la mancha. */
    fun filo(al: Float): Color = when {
        a.oscuro -> lumbre.copy(alpha = (al * k).coerceIn(0f, 1f))
        a.noche -> p.OCASO.copy(alpha = (al * 0.6f * sol).coerceIn(0f, 1f))
        else -> p.SOL.copy(alpha = (al * 0.7f * sol).coerceIn(0f, 1f))
    }

    /** El roce frío de la luna (solo en los Sueños oscuros). */
    fun lunar(al: Float): Color = if (a.oscuro && a.noche) p.LUNA_RAS.copy(alpha = (al * sol).coerceIn(0f, 1f)) else Color.Transparent

    /**
     * El color del ánimo: de noche conserva su tono de día y solo baja con la distancia a la luz (el añil sigue añil, hondo lejos
     * y encendido cerca); al ocaso, tibio; con la luna, plateado donde toca.
     */
    fun animo(t: Color): Color {
        if (!a.oscuro) return c(t)
        val apagado = mezcla(Color(t.red * 0.26f, t.green * 0.26f, t.blue * 0.27f, t.alpha), hondo, 0.28f)
        val encendido = mezcla(t, aLaLlama(t), 0.45f)
        var v = mezcla(apagado, encendido, (1.12f * Math.pow(k.toDouble(), 1.35).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezcla(v, mezcla(t, p.LUNA, 0.35f), 0.4f * sol)
        return v
    }

    /**
     * El papiro o el pergamino donde va el día, teñido por la luz que le llega: ámbar cerca de la llama, pardo hondo lejos (nunca
     * más claro que lo alumbrado) y siempre con contraste para la tinta.
     */
    fun etiqueta(col: Color): Color {
        if (!a.oscuro) return c(col)
        val lejos = Color(col.red * 0.42f, col.green * 0.38f, col.blue * 0.32f, col.alpha)
        val alumbrado = Color((col.red * 1.04f).coerceAtMost(1f), col.green * 0.92f, col.blue * 0.7f, col.alpha)
        var v = mezcla(lejos, alumbrado, (1.1f * Math.pow(k.toDouble(), 1.7).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezcla(v, mezcla(col, p.LUNA, 0.3f).let { Color(it.red * 0.7f, it.green * 0.74f, it.blue * 0.82f, it.alpha) }, 0.45f * sol)
        return v
    }

    /** La tinta del día sobre [etiqueta]: siempre oscura y legible. */
    fun tinta(): Color = if (a.oscuro) Color(0xFF110C08) else c(p.TINTA)
}

/**
 * Dónde está cada cosa para la luz: el aire del momento, el ancho del mueble, cuánta lumbre llega a cada punto ([cerca]) y qué
 * parte de ella es del brasero ([brasa]). Con [t] se saca el tono de un sitio.
 */
internal class SitioArcontes(val a: AireArcontes, val w: Float, private val cerca: (Offset) -> Float, private val brasa: (Offset) -> Float) {
    fun k(p: Offset): Float = cerca(p)
    fun t(p: Offset, cuerpo: Boolean = false): TonoArcontes = a.en(p.x / w, p.y / w, cerca(p), brasa(p), cuerpo)

    /** Un degradado a lo ancho con el color de cada punto (de noche); de día, el color del medio, liso. */
    fun aLoAncho(x0: Float, x1: Float, y: Float, cuerpo: Boolean = false, col: (TonoArcontes) -> Color): Brush {
        if (x1 - x0 < 2f || !a.lumbre) return SolidColor(col(t(Offset((x0 + x1) / 2, y), cuerpo)))
        val n = ((x1 - x0) / (w * 0.07f)).toInt().coerceIn(3, 16)
        return Brush.horizontalGradient(List(n) { i -> col(t(Offset(x0 + (x1 - x0) * i / (n - 1), y), cuerpo)) }, x0, x1)
    }

    /** Lo mismo a lo alto, de [y0] a [y1] en la [x] dada (de día también, por la mancha del sol). */
    fun aLoAlto(y0: Float, y1: Float, x: Float, cuerpo: Boolean = false, col: (TonoArcontes) -> Color): Brush {
        if (y1 - y0 < 2f) return SolidColor(col(t(Offset(x, (y0 + y1) / 2), cuerpo)))
        val n = ((y1 - y0) / (w * 0.05f)).toInt().coerceIn(3, 40)
        return Brush.verticalGradient(List(n) { i -> col(t(Offset(x, y0 + (y1 - y0) * i / (n - 1)), cuerpo)) }, y0, y1)
    }
}

// ---------------------------------------------------------------- ayudantes de dibujo

/** Un polígono cerrado por sus puntos (x, y, x, y…). */
internal fun poligonoArcontes(vararg xy: Float) = Path().apply {
    moveTo(xy[0], xy[1])
    for (i in 1 until xy.size / 2) lineTo(xy[2 * i], xy[2 * i + 1])
    close()
}

/** La sombra de lo que pisa: un óvalo corrido hacia donde no está la luz ([hacia]: 1, la luz a la derecha; -1, a la izquierda). */
internal fun DrawScope.contactoArcontes(centro: Offset, ancho: Float, alto: Float, t: TonoArcontes, fuerza: Float = 0.38f, hacia: Float = -1f) =
    drawOval(t.s(fuerza), Offset(centro.x - ancho * (0.5f + 0.12f * hacia), centro.y - alto / 2), Size(ancho, alto))

/** Un clavo de bronce: su sombra, la cabeza y el punto de luz. */
internal fun DrawScope.clavoArcontes(p: Offset, r: Float, t: TonoArcontes, metal: Color = PaletaArcontes.BRONCE) {
    drawCircle(t.s(0.45f), r * 1.05f, p + Offset(r * 0.4f, r * 0.5f))
    drawCircle(t.c(metal), r, p)
    drawCircle(t.b(0.6f), r * 0.38f, p - Offset(r * 0.3f, r * 0.3f))
}

/** El destello de la luz en el bronce o el barro bruñido (de día, crema; de noche, el de la llama si le llega). */
internal fun DrawScope.destelloArcontes(c: Offset, r: Float, t: TonoArcontes) {
    if (!t.a.oscuro) { drawCircle(luzMueble(0.8f), r, c); return }
    if (t.k < 0.05f) return
    drawCircle(PaletaArcontes.LLAMA.copy(alpha = (0.4f * t.k).coerceAtMost(0.35f)), r * 2.2f, c)
    drawCircle(PaletaArcontes.FUEGO.copy(alpha = (1.1f * t.k).coerceAtMost(0.95f)), r, c)
}

/** Las franjas de luz y sombra de un cuerpo redondo de pie dentro de [r] (ir dentro de su recorte), con el claro hacia [hacia]. */
internal fun DrawScope.cilindroArcontes(r: Rect, t: TonoArcontes, hacia: Float, fuerza: Float = 1f) {
    val der = hacia > 0f
    fun x(f: Float, an: Float) = if (der) r.right - r.width * (f + an) else r.left + r.width * f
    drawRect(t.b(0.16f * fuerza), Offset(x(0f, 0.34f), r.top), Size(r.width * 0.34f, r.height))
    drawRect(t.b(0.3f * fuerza), Offset(x(0.13f, 0.09f), r.top), Size(r.width * 0.09f, r.height))
    drawRect(t.s(0.16f * fuerza), Offset(x(0.56f, 0.44f), r.top), Size(r.width * 0.44f, r.height))
    drawRect(t.s(0.26f * fuerza), Offset(x(0.8f, 0.2f), r.top), Size(r.width * 0.2f, r.height))
}

/** Un charco de luz cálida (o fría) que se apaga hacia fuera, en tres círculos opacos y uno suave. */
internal fun DrawScope.charcoArcontes(c: Offset, r: Float, color: Color, fuerza: Float) {
    if (r <= 1f || fuerza <= 0f) return
    drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.34f * fuerza), color.copy(alpha = 0.12f * fuerza), color.copy(alpha = 0f)), c, r), r, c)
}


// ---------------------------------------------------------------- el ánimo

/** Las escalas del ánimo de Arcontes, la de día y la de noche (bajo, medio, alto), para volver a la de día. */
private val TONOS_ARCONTES: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.ARCONTES, false) to juegoTonos(TemaId.ARCONTES, true) }

private fun cuadradoArcontes(x: Float) = x * x

/** Dónde cae [t] en la escala [escala] (bajo, medio, alto): de 0 a 1 a lo largo de ella, o null si no está en ella. */
private fun enEscalaArcontes(t: Color, escala: List<Color>): Float? {
    if (escala.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = escala[seg]; val a1 = escala[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoArcontes(t.red - a0.red - dr * f) + cuadradoArcontes(t.green - a0.green - dg * f) + cuadradoArcontes(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

/**
 * Los tintes del ánimo, de bajo a alto (el cuero de los códices, el pergamino de los rollos y la lana de los envoltorios): lo
 * bajo, el añil del lago (el tinte de la hierba pastel); lo de en medio, sin teñir, tostado (nunca blanco); lo alto, la gualda y
 * el ocre dorado. Siguen la escala del tema (`juegoTonos`: el lago, la caliza y el ocre).
 */
private val TINTES_ARCONTES = listOf(
    0f to Color(0xFF173A4A), 0.18f to Color(0xFF25566A), 0.34f to Color(0xFF4F8590), 0.45f to Color(0xFF9AA08A), 0.5f to Color(0xFFC2AE84),
    0.56f to Color(0xFFCDAE62), 0.68f to Color(0xFFC79534), 0.82f to Color(0xFFB0781E), 1f to Color(0xFF8C5512),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_ARCONTES]; si no
 * está en la escala, queda igual. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa de caliza a casi negro):
 * aquí la entrada es un objeto de la cueva, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la
 * de día) y después la luz dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaArcontes(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    val (dia, noche) = TONOS_ARCONTES
    val x = (if (oscuro) enEscalaArcontes(t, noche) else null) ?: enEscalaArcontes(t, dia) ?: return t
    for (i in 0 until TINTES_ARCONTES.size - 1) {
        val (f0, c0) = TINTES_ARCONTES[i]; val (f1, c1) = TINTES_ARCONTES[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_ARCONTES.last().second
}
