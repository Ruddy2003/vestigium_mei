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
    /**
     * El olivo del armario: el de las arquetas, pero más viejo y aceitado, más miel y más oscuro, para que el mueble se recorte
     * contra la caliza clara (el olivo claro se lavaría en ella); su cara, su luz, su sombra, lo hondo y la veta parda.
     */
    val OLIVO = Color(0xFFA27A45)
    val OLIVO_LUZ = Color(0xFFC99D60)
    val OLIVO_OSC = Color(0xFF6E4E2A)
    val OLIVO_HONDO = Color(0xFF3F2C18)
    val OLIVO_VETA = Color(0xFF4A3018)
    /** El bronce de las chapas y los aros, su luz, su sombra y el cardenillo que le sale en las juntas. */
    val BRONCE = Color(0xFF8C6A34)
    val BRONCE_LUZ = Color(0xFFC9A45E)
    val BRONCE_OSC = Color(0xFF574019)
    val CARDENILLO = Color(0xFF5F8F7A)
    /** El hueso de los pasadores. */
    val HUESO = Color(0xFFE8DEC6)
    /** El papiro de las hojas, el de las etiquetas (de valor medio), su fibra; el cordel de lino. */
    val PAPIRO = Color(0xFFEDE3C8)
    val PAPIRO_ETIQUETA = Color(0xFFB89C68)
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
    /** La noche de la cueva: el azul violáceo negro del cielo antiguo (la del Ideario) y la de los Sueños. */
    val NOCHE = Color(0xFF090B1D)
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
     * Dónde arden, en el lienzo y en anchos, la lámpara de encima del armario y el brasero del pie (en un estante ancho, lo de la
     * corona y del pie se queda del tamaño de un teléfono grande y se corren hacia el medio; ver [MuebleEstanteArcontes.escala]).
     */
    var lampX = MuebleEstanteArcontes.LAMP_X
    var lampY = MuebleEstanteArcontes.LAMP_Y
    var braseroX = MuebleEstanteArcontes.BRASERO_X
    var braseroY = 0f
    /** La escala de lo de la corona y del pie: la luz alcanza lo mismo (en dp) en un estante ancho que en un teléfono. */
    var escala = 1f

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
            val borde = VELO_X + VELO_Y * y
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

    /**
     * El tono de algo que está en ([x], [y]) (anchos), con la lumbre [k] que le llega, la parte de ella que es brasa ([rojo]) y la
     * luz fría de la grieta ([frio], solo de noche).
     */
    fun en(x: Float, y: Float, k: Float, rojo: Float = 0f, cuerpo: Boolean = false, frio: Float = 0f): TonoArcontes =
        TonoArcontes(this, k.coerceIn(0f, 1f), mancha(x, y), ambiente(x, y), rojo.coerceIn(0f, 1f), cuerpo, if (oscuro) frio.coerceIn(0f, 1f) else 0f)

    companion object {
        /**
         * El sol de la boca: una mancha ancha en la pared de la corona, más alta a la izquierda, que baja al costado izquierdo.
         * Arriba sube hasta el techo de roca, que la corta con su canto quebrado.
         */
        val SOL_Q = floatArrayOf(-0.06f, -0.02f, 0.3f, -0.02f, 0.62f, 0.0f, 0.68f, 0.3f, 0.52f, 0.6f, 0.14f, 0.76f, -0.06f, 0.72f)
        /** Dónde empieza el velo de lino, a la izquierda de la mancha: x = [VELO_X] + [VELO_Y] · y (anchos). */
        const val VELO_X = 0.05f
        const val VELO_Y = 0.1f
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
 * boca, más hondo lejos) y, donde cae la mancha del sol ([sol]), la misma piedra un paso más clara y más cálida. De noche, como en
 * el Ideario: todo hundido en el azul violáceo de la noche de la cueva, y según [k] (la lumbre que le llega) va saliendo su color,
 * primero más claro y, ya junto a la llama, dorado (o rojo de brasa, [rojo]); y junto a la grieta, su luz fría ([frio]). Al ocaso,
 * más bajo y con el melocotón donde toca [sol]; con la luna, la plata fría donde toca.
 */
internal class TonoArcontes(val a: AireArcontes, val k: Float, val sol: Float, val amb: Float, val rojo: Float, val cuerpo: Boolean, val frio: Float = 0f) {
    private val p = PaletaArcontes

    /** El aire en que se hunde lo que no se alumbra. */
    private val hondo: Color get() = when {
        a.oscuro && a.noche -> p.NOCHE_SUENOS
        a.oscuro -> p.NOCHE
        a.noche -> p.SOMBRA_OCASO
        else -> SOMBRA_MUEBLE
    }

    /** La lumbre del sitio: la llama, o la brasa si le llega más del brasero. */
    private val lumbre: Color get() = if (rojo > 0f) mezclaRapida(p.LLAMA, p.BRASA, rojo * 0.7f) else p.LLAMA

    /** El color propio hundido en la noche, como el `n` del Ideario: nueve partes del azul violáceo y una de lo suyo. */
    private fun hundido(c: Color): Color = mezclaRapida(c, hondo, 0.9f)

    /** La luz fría de la grieta sobre [v] (de noche, junto a ella): el blanco violeta que tiñe y aclara un poco. */
    private fun conFrio(v: Color): Color =
        if (frio <= 0.01f) v else mezclaRapida(v, mezclaRapida(p.GRIETA_HALO, p.GRIETA, 0.25f), 0.32f * frio)

    /** El color a la luz de la llama: el rojo sube y el azul baja (más aún junto a la brasa). */
    private fun aLaLlama(c: Color): Color =
        Color((c.red * (1.1f + 0.05f * rojo)).coerceAtMost(1f), c.green * (0.9f - 0.1f * rojo), c.blue * (0.64f - 0.12f * rojo), c.alpha)

    /** El color de [c] con la luz del momento; [f] más claro (positivo) o más hondo (negativo), como lo pinta la luz. */
    fun c(c: Color, f: Float = 0f): Color {
        if (!a.oscuro) {
            val b = if (a.noche) Color(c.red * 0.92f, c.green * 0.89f, c.blue * 0.88f, c.alpha) else c
            val d = if (f > 0f) mezclaRapida(b, LUZ_MUEBLE, f)
                else if (f < 0f) (if (a.noche) mezclaRapida(b, Color(b.red * 0.5f, b.green * 0.5f, b.blue * 0.6f, b.alpha), -f * 1.3f) else mezclaRapida(b, hondo, -f))
                else b
            if (a.noche) {
                // El último sol pinta solo donde cae: el mismo color virado a melocotón y un paso más claro.
                val l = Color((d.red * 1.13f + 0.07f).coerceAtMost(1f), (d.green * 1.04f + 0.035f).coerceAtMost(1f), (d.blue * 0.95f + 0.02f).coerceAtMost(1f), c.alpha)
                return mezclaRapida(mezclaRapida(d, mezclaRapida(l, p.OCASO, 0.26f), sol * 0.9f), p.LLAMA, 0.12f * k)
            }
            // De día: lo cercano a la boca más claro y tibio; lo lejano, más hondo; y donde cae el sol, la misma piedra un paso
            // más clara y más cálida (no un amarillo).
            val luz = amb - 0.45f
            var v = if (luz > 0f) mezclaRapida(d, LUZ_MUEBLE, 0.3f * luz) else mezclaRapida(d, SOMBRA_MUEBLE, -0.22f * luz)
            if (sol > 0f) {
                val claro = Color((v.red * 1.05f + 0.045f).coerceAtMost(1f), (v.green * 1.04f + 0.035f).coerceAtMost(1f), (v.blue * 1.0f + 0.02f).coerceAtMost(1f), c.alpha)
                v = mezclaRapida(v, claro, sol * 0.9f)
            }
            return v
        }
        // De noche: hundido en el azul violáceo lejos de la llama; con la lumbre sale su color, y solo cerca se calienta.
        val base = if (f > 0f) mezclaRapida(c, LUZ_MUEBLE, f * 0.4f) else if (f < 0f) mezclaRapida(c, hondo, -f * 0.6f) else c
        val kk = k.coerceIn(0f, 1f)
        // El olivo del armario se hunde más y se calienta solo muy cerca de la llama (lejos, el azul violáceo del Ideario).
        val calido = if (cuerpo) Math.pow(kk.toDouble(), 2.5).toFloat() else kk * kk
        val propio = mezclaRapida(base, aLaLlama(base), 0.75f * calido)
        val hundirse = (if (cuerpo) 0.92f - 0.62f * Math.pow(kk.toDouble(), 1.5).toFloat()
            else 0.9f - 0.62f * Math.pow(kk.toDouble(), 1.25).toFloat()).coerceIn(0.22f, 0.92f)
        var v = mezclaRapida(propio, hondo, hundirse)
        v = conFrio(mezclaRapida(v, lumbre, 0.16f * calido))
        if (sol > 0f) {
            // La luna: el mismo color, un poco más claro y frío donde toca (la caliza sigue caliza; no vela lo demás).
            val q = 0.15f * sol * sol
            v = Color((v.red + (c.red * 0.62f + 0.02f) * q).coerceAtMost(1f), (v.green + (c.green * 0.78f + 0.05f) * q).coerceAtMost(1f),
                (v.blue + (c.blue * 0.95f + 0.16f) * q).coerceAtMost(1f), c.alpha)
        }
        return v
    }

    /** La sombra: de día, violeta hondo; al ocaso, violeta; de noche, el aire hondo. */
    fun s(al: Float): Color =
        if (a.oscuro) hondo.copy(alpha = (al * 1.3f).coerceIn(0f, 1f)) else if (a.noche) hondo.copy(alpha = (al * 1.1f).coerceIn(0f, 1f)) else sombraMueble(al)

    /** El brillo de lo que mira a la luz: de día, crema (dorado en la mancha); al ocaso, melocotón; de noche, el filo de la llama. */
    fun b(al: Float): Color = when {
        !a.oscuro && a.noche -> mezclaRapida(LUZ_MUEBLE, p.OCASO, 0.55f).copy(alpha = (al * (0.25f + 0.7f * sol)).coerceIn(0f, 1f))
        !a.oscuro -> mezclaRapida(LUZ_MUEBLE, p.SOL, 0.5f * sol).copy(alpha = (al * (0.7f + 0.3f * amb + 0.3f * sol)).coerceIn(0f, 1f))
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
        // Lejos de la llama, hundido pero con su matiz vivo (el añil sigue añil; el ocre, ocre oscuro), un poco violáceo.
        val n = hondo
        val apagado = Color(t.red * 0.36f + n.red * 0.4f, t.green * 0.34f + n.green * 0.4f, t.blue * 0.4f + n.blue * 0.4f, t.alpha)
        val encendido = mezclaRapida(t, aLaLlama(t), 0.45f)
        var v = mezclaRapida(apagado, encendido, (1.12f * Math.pow(k.toDouble(), 1.35).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezclaRapida(v, mezclaRapida(t, p.LUNA, 0.35f), 0.4f * sol)
        return conFrio(v)
    }

    /**
     * El papiro o el pergamino donde va el día, teñido por la luz que le llega, de noche con paso gradual: ámbar junto a la llama;
     * lejos, hundido en el azul violáceo, oscuro (lo más claro de la noche queda junto a las llamas y en la grieta). Nunca de un
     * valor intermedio en el que ninguna tinta llegue a 4.5 a 1 (ver [tintaSobre]).
     */
    fun etiqueta(col: Color): Color {
        if (!a.oscuro) return c(col)
        val lejos = Color(col.red * 0.2f + 0.035f, col.green * 0.2f + 0.04f, col.blue * 0.2f + 0.1f, col.alpha)
        val alumbrado = Color((col.red * 1.04f).coerceAtMost(1f), col.green * 0.92f, col.blue * 0.7f, col.alpha)
        var v = mezclaRapida(lejos, alumbrado, (1.1f * Math.pow(k.toDouble(), 1.5).toFloat()).coerceAtMost(1f))
        if (sol > 0f) v = mezclaRapida(v, mezclaRapida(col, p.LUNA, 0.3f).let { Color(it.red * 0.5f, it.green * 0.54f, it.blue * 0.64f, it.alpha) }, 0.45f * sol)
        v = conFrio(v)
        // Fuera de la franja de valores en que ni la tinta oscura ni la clara se leen bien: al borde más cercano.
        val l = luminanciaArcontes(v)
        if (l > OSCURA_HASTA && l < CLARA_DESDE) {
            val meta = if (l < (OSCURA_HASTA + CLARA_DESDE) / 2) OSCURA_HASTA * 0.94f else CLARA_DESDE * 1.06f
            val f = Math.pow((meta / l).toDouble(), 1 / 2.2).toFloat()
            v = Color((v.red * f).coerceAtMost(1f), (v.green * f).coerceAtMost(1f), (v.blue * f).coerceAtMost(1f), v.alpha)
        }
        return v
    }

    /** La tinta del día: siempre oscura y legible (de día y al ocaso). */
    fun tinta(): Color = if (a.oscuro) Color(0xFF110C08) else if (a.noche) Color(0xFF231B27) else p.TINTA

    /**
     * La tinta sobre un papel [fondo]: de noche, oscura sobre el papiro alumbrado y clara (un gris lila pálido) sobre el hundido,
     * para que el número tenga siempre al menos 4.5 a 1.
     */
    fun tintaSobre(fondo: Color): Color = if (!a.oscuro || luminanciaArcontes(fondo) >= CLARA_DESDE) tinta() else Color(0xFFE6E2F0)

    private companion object {
        /** Hasta qué luminancia el papel lleva tinta clara, y desde cuál oscura (entre las dos, ninguna llega a 4.5 a 1). */
        const val OSCURA_HASTA = 0.125f
        const val CLARA_DESDE = 0.2f
    }
}

/**
 * Dónde está cada cosa para la luz: el aire del momento, el ancho del mueble y, por [luz], cuánta lumbre llega a cada punto, qué
 * parte de ella es del brasero y cuánta luz fría le da la grieta. Con [t] se saca el tono de un sitio. Lo que se pinta achicado
 * (con [escalado]: lo de la corona y del pie en un estante ancho) pregunta en sus propias medidas y aquí se pasan al lienzo.
 */
internal class SitioArcontes(val a: AireArcontes, val w: Float, val e: Float = 1f, val pivote: Offset = Offset.Zero,
                             private val luz: (Float, Float, FloatArray) -> Unit) {
    private val buf = FloatArray(3)

    /** El punto [p] (en las medidas de lo que se pinta) en el lienzo. */
    fun enLienzo(p: Offset): Offset = if (e == 1f) p else Offset(pivote.x + (p.x - pivote.x) * e, pivote.y + (p.y - pivote.y) * e)

    /** El mismo sitio para pintar achicado a [escala] alrededor de [centro] (del lienzo). */
    fun escalado(escala: Float, centro: Offset) = SitioArcontes(a, w, escala, centro, luz)

    fun k(p: Offset): Float { val q = enLienzo(p); luz(q.x, q.y, buf); return buf[0] }
    fun t(p: Offset, cuerpo: Boolean = false): TonoArcontes {
        val q = enLienzo(p)
        buf[2] = 0f
        luz(q.x, q.y, buf)
        return a.en(q.x / w, q.y / w, buf[0], buf[1], cuerpo, buf[2])
    }

    // Los tonos de la última fila y de la última columna, para no volver a calcularlos en las piezas que pintan varias capas
    // (la clave en números: armarla en texto pesaba en cada dibujo).
    private val claveFila = FloatArray(4) { Float.NaN }
    private var fila: List<TonoArcontes> = emptyList()
    private val claveColumna = FloatArray(4) { Float.NaN }
    private var columna: List<TonoArcontes> = emptyList()

    private fun igual(c: FloatArray, a0: Float, a1: Float, a2: Float, cuerpo: Boolean): Boolean {
        val q = if (cuerpo) 1f else 0f
        if (c[0] == a0 && c[1] == a1 && c[2] == a2 && c[3] == q) return true
        c[0] = a0; c[1] = a1; c[2] = a2; c[3] = q
        return false
    }

    private fun tonosFila(x0: Float, x1: Float, y: Float, cuerpo: Boolean): List<TonoArcontes> {
        if (!igual(claveFila, x0, x1, y, cuerpo)) {
            val n = ((x1 - x0) / (w * 0.12f)).toInt().coerceIn(3, 9)
            fila = List(n) { i -> t(Offset(x0 + (x1 - x0) * i / (n - 1), y), cuerpo) }
        }
        return fila
    }

    private fun tonosColumna(y0: Float, y1: Float, x: Float, cuerpo: Boolean): List<TonoArcontes> {
        if (!igual(claveColumna, y0, y1, x, cuerpo)) {
            val n = ((y1 - y0) / (w * 0.1f)).toInt().coerceIn(3, 16)
            columna = List(n) { i -> t(Offset(x, y0 + (y1 - y0) * i / (n - 1)), cuerpo) }
        }
        return columna
    }

    /** Un degradado a lo ancho con el color de cada punto (de noche); de día, el color del medio, liso. */
    fun aLoAncho(x0: Float, x1: Float, y: Float, cuerpo: Boolean = false, col: (TonoArcontes) -> Color): Brush {
        if (x1 - x0 < 2f || !a.lumbre) return SolidColor(col(t(Offset((x0 + x1) / 2, y), cuerpo)))
        return Brush.horizontalGradient(tonosFila(x0, x1, y, cuerpo).map(col), x0, x1)
    }

    /** Lo mismo a lo alto, de [y0] a [y1] en la [x] dada (de día también en lo alto, por la luz de fondo). */
    fun aLoAlto(y0: Float, y1: Float, x: Float, cuerpo: Boolean = false, col: (TonoArcontes) -> Color): Brush {
        if (y1 - y0 < 2f || (!a.lumbre && y1 - y0 < w * 0.5f)) return SolidColor(col(t(Offset(x, (y0 + y1) / 2), cuerpo)))
        return Brush.verticalGradient(tonosColumna(y0, y1, x, cuerpo).map(col), y0, y1)
    }
}

// ---------------------------------------------------------------- ayudantes de dibujo

/** La luminancia relativa de [c] (la de las pautas de contraste: de 0, negro, a 1, blanco). */
internal fun luminanciaArcontes(c: Color): Float {
    fun lin(v: Float) = if (v <= 0.04045f) v / 12.92f else Math.pow(((v + 0.055f) / 1.055f).toDouble(), 2.4).toFloat()
    return 0.2126f * lin(c.red) + 0.7152f * lin(c.green) + 0.0722f * lin(c.blue)
}

/**
 * La mezcla de dos colores en RGB, sin pasar por Oklab como `mezcla` (la de Compose): para lo que se calcula miles de veces por
 * dibujo (el tono de cada punto); en mezclas cortas apenas se distingue.
 */
internal fun mezclaRapida(a: Color, b: Color, f: Float): Color {
    val k = f.coerceIn(0f, 1f)
    return Color(a.red + (b.red - a.red) * k, a.green + (b.green - a.green) * k, a.blue + (b.blue - a.blue) * k, a.alpha + (b.alpha - a.alpha) * k)
}

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
 * Los tintes del ánimo, de bajo a alto (el cuero de los códices, la tela de las camisas de los rollos y la lana de los
 * envoltorios), tintes de entonces y no colores planos: lo bajo, el añil de la hierba pastel, hondo y un poco violáceo; lo de en
 * medio, sin teñir, tostado (nunca blanco); lo alto, la gualda apagada y el ocre de tierra. Siguen la escala del tema
 * (`juegoTonos`: el lago, la caliza y el ocre).
 */
private val TINTES_ARCONTES = listOf(
    0f to Color(0xFF1C2747), 0.18f to Color(0xFF2B3B63), 0.34f to Color(0xFF56638A), 0.45f to Color(0xFF8F8B84), 0.5f to Color(0xFFB7A47F),
    0.56f to Color(0xFFBC9F68), 0.68f to Color(0xFFAC8446), 0.82f to Color(0xFF966835), 1f to Color(0xFF774C26),
)

/**
 * El tinte de una entrada según su ánimo [t]: se ubica en la escala del tema y se toma el mismo punto en [TINTES_ARCONTES]; si no
 * está en la escala, queda igual. En el modo oscuro la escala del tema cambia (el ánimo de en medio pasa de caliza a casi negro):
 * aquí la entrada es un objeto de la cueva, así que primero se vuelve al color de día (el mismo punto de la escala de noche en la
 * de día) y después la luz dibujada lo apaga con la distancia.
 */
internal fun tonoDeDiaArcontes(t: Color?, oscuro: Boolean): Color? {
    if (t == null) return null
    // Hay pocos ánimos distintos y cada volumen pregunta en cada dibujo: se guarda lo ya buscado (la mezcla es en Oklab y pesa).
    val clave = t.value.toLong() xor (if (oscuro) 1L else 0L)
    synchronized(TINTES_HECHOS) { TINTES_HECHOS[clave]?.let { return it } }
    val v = tonoDeDiaArcontesSinGuardar(t, oscuro)
    synchronized(TINTES_HECHOS) { if (TINTES_HECHOS.size > 256) TINTES_HECHOS.clear(); TINTES_HECHOS[clave] = v }
    return v
}

private val TINTES_HECHOS = HashMap<Long, Color>()

private fun tonoDeDiaArcontesSinGuardar(t: Color, oscuro: Boolean): Color {
    val (dia, noche) = TONOS_ARCONTES
    val x = (if (oscuro) enEscalaArcontes(t, noche) else null) ?: enEscalaArcontes(t, dia) ?: return t
    for (i in 0 until TINTES_ARCONTES.size - 1) {
        val (f0, c0) = TINTES_ARCONTES[i]; val (f1, c1) = TINTES_ARCONTES[i + 1]
        if (x <= f1) return mezcla(c0, c1, ((x - f0) / maxOf(1e-4f, f1 - f0)).coerceIn(0f, 1f))
    }
    return TINTES_ARCONTES.last().second
}
