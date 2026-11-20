package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.LUZ_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import kotlin.math.ln
import kotlin.math.pow

/*
 * La luz, el aire y las medidas del libro del día de Espacio ([MuebleTapaEspacio]).
 */

/**
 * El aire del libro del día: el del estante ([AireEstanteEspacio]) salvo en dos cosas. En el sueño en claro, en lugar del
 * anochecer violeta parejo manda la tira de guardia, con dirección: cerca, casi el color de día con un toque azul; lejos, una
 * penumbra azul pizarra. Y la tarjeta del extracto ([papel]): en oscuro se calienta y se aclara cerca de la lámpara y se
 * queda gris azulada lejos, siempre legible.
 */
internal class AireTapaEspacio(val base: AireEstanteEspacio) {
    private val P = PaletaEstanteEspacio
    val oscuro = base.oscuro
    val noche = base.noche
    val lumbre = base.lumbre
    private val suenoClaro = !oscuro && noche

    fun luz(c: Color, l: LuzEstanteEspacio): Color {
        if (!suenoClaro) return base.luz(c, l)
        val k = l.k.coerceIn(0f, 1f).pow(0.8f)
        val m = mezcla(PENUMBRA, LUZ_GUARDIA, k)
        return mezcla(Color(c.red * m.red, c.green * m.green, c.blue * m.blue, c.alpha), P.GUARDIA, 0.08f * k)
    }

    fun sombra(a: Float): Color = if (suenoClaro) SOMBRA_SUENO.copy(alpha = (a * 1.1f).coerceIn(0f, 1f)) else base.sombra(a)
    fun claro(a: Float): Color = base.claro(a)
    fun filo(a: Float): Color = base.filo(a)

    /** El papel de la tarjeta y de las etiquetas con la luz que le llega. */
    fun papel(l: LuzEstanteEspacio): Color = when {
        !lumbre -> P.PAPEL
        oscuro && !noche -> mezcla(mezcla(P.PAPEL, base.hondo, 0.5f), mezcla(P.PAPEL, P.AMBAR, 0.16f), l.k.coerceIn(0f, 1f).pow(0.55f))
        suenoClaro -> luz(P.PAPEL, LuzEstanteEspacio(maxOf(l.k, 0.45f), 0f))
        else -> base.etiqueta(l)
    }

    companion object {
        /** Lo que multiplica cada color en el sueño en claro: lejos, azul pizarra; junto a la tira de guardia, casi blanco. */
        val PENUMBRA = Color(0xFF8A93B2)
        val LUZ_GUARDIA = Color(0xFFF2F5FF)
        val SOMBRA_SUENO = Color(0xFF283052)
    }
}

/**
 * La luz del rincón del libro del día. De día no hay luz dibujada (las luminarias del techo quedan fuera, arriba a la
 * izquierda); en oscuro manda la lámpara de lectura, cálida, arriba a la derecha, y el piloto de guardia, azul, abajo a la
 * izquierda, apenas da un filo frío; en los sueños la lámpara se apaga y manda la tira de guardia.
 */
internal class LuzTapaEspacio(private val w: Float, private val h: Float, u: Float, private val a: AireTapaEspacio) {
    private val P = PaletaEstanteEspacio
    /** La boca de la lámpara de lectura y el piloto de guardia, en píxeles del lienzo. */
    val lampara = Offset(w - 21f * u, 40f * u)
    val guardia = Offset(minOf(9f * u, w * 0.025f), h * 0.6f)
    private val alcanceLampara = 255f * u
    private val alcanceGuardia = 235f * u

    /**
     * Con [plano], lo que se pinta lleva la luz entera: la caída con la distancia la pone después, encima y de un solo
     * dibujo, el mapa de luz ([colorEscena]).
     */
    var plano = false

    /** Cuánto llega de la lámpara a [p]: se apaga deprisa con la distancia (lo lejano casi se pierde). */
    fun deLampara(p: Offset): Float = (1f - (p - lampara).getDistance() / alcanceLampara).coerceIn(0f, 1f).pow(2.2f)

    /** Cuánto llega del piloto de guardia a [p]. */
    fun deGuardia(p: Offset): Float = (1f - (p - guardia).getDistance() / alcanceGuardia).coerceIn(0f, 1f).pow(1.6f)

    /** La luz que de verdad llega a [p]. */
    fun real(p: Offset): LuzEstanteEspacio = when {
        !a.lumbre -> LuzEstanteEspacio(1f, 0f)
        a.noche -> LuzEstanteEspacio(deGuardia(p), 0.05f)
        else -> LuzEstanteEspacio(deLampara(p), 0.5f * deGuardia(p))
    }

    fun en(p: Offset): LuzEstanteEspacio = if (plano && a.lumbre) LuzEstanteEspacio(1f, if (a.noche) 0.05f else 0f) else real(p)

    /** El modo del mapa: 0 de día, 1 en oscuro, 2 el sueño en oscuro, 3 el sueño en claro. */
    val modo: Int get() = if (!a.lumbre) 0 else if (a.oscuro && !a.noche) 1 else if (a.oscuro) 2 else 3

    /** Cómo se pone el mapa de la escena: encima (apaga hacia el hondo) o, en el sueño en claro, multiplicando. */
    val mezclaMapa: BlendMode get() = BlendMode.SrcOver

    /**
     * El color del mapa de luz de la escena en la fracción ([fx], [fy]) del lienzo. De día: crema arriba a la izquierda y la
     * sombra violeta abajo a la derecha. En oscuro: el hondo de la noche, más tapado cuanto menos luz llega (la misma cuenta
     * que [AireEstanteEspacio.luz], sobre el color ya alumbrado del todo). En el sueño en claro: lo que multiplica, de la
     * penumbra azul pizarra a casi blanco junto a la tira.
     */
    fun colorEscena(fx: Float, fy: Float): Color {
        val p = Offset(fx * w, fy * h)
        val l = real(p)
        return when (modo) {
            0 -> {
                val t = fx * 0.65f + fy * 0.35f
                if (t < 0.5f) LUZ_MUEBLE.copy(alpha = 0.2f * (1f - t / 0.5f)) else SOMBRA_MUEBLE.copy(alpha = 0.15f * ((t - 0.5f) / 0.5f))
            }
            1 -> {
                val m = (0.9f - 0.84f * l.k.pow(0.75f) - 0.28f * l.f).coerceIn(0.07f, 0.9f)
                a.base.hondo.copy(alpha = ((m - 0.06f) / 0.94f).coerceIn(0f, 1f))
            }
            2 -> {
                val m = (0.92f - 0.86f * l.k.pow(0.7f) - 0.25f * l.f).coerceIn(0.08f, 0.92f)
                a.base.hondo.copy(alpha = ((m - 0.0475f) / 0.9525f).coerceIn(0f, 1f))
            }
            else -> PENUMBRA_SUENO.copy(alpha = 0.44f * (1f - l.k.coerceIn(0f, 1f).pow(0.8f)))
        }
    }

    /** El papel de la tarjeta lejos de la luz (con él se pinta, antes de su mapa). */
    fun papelLejos(): Color = when (modo) {
        0 -> P.PAPEL
        1 -> mezcla(P.PAPEL, a.base.hondo, 0.5f)
        2 -> a.base.etiqueta(LuzEstanteEspacio(0f, 0.05f))
        else -> a.papel(LuzEstanteEspacio(0.45f, 0f))
    }

    /** El mapa de la tarjeta, en la fracción ([fx], [fy]) de [t]: el papel alumbrado, tanto más cuanto más luz le llega. */
    fun colorPapel(fx: Float, fy: Float, t: Rect): Color {
        val k = real(Offset(t.left + fx * t.width, t.top + fy * t.height)).k.coerceIn(0f, 1f)
        return when (modo) {
            0 -> Color.Transparent
            1 -> mezcla(P.PAPEL, P.AMBAR, 0.16f).copy(alpha = k.pow(0.55f))
            2 -> a.base.etiqueta(LuzEstanteEspacio(1f, 0.05f)).copy(alpha = k)
            else -> a.papel(LuzEstanteEspacio(1f, 0f)).copy(alpha = ((maxOf(k, 0.45f).pow(0.8f) - 0.528f) / 0.472f).coerceIn(0f, 1f))
        }
    }

    companion object {
        /** La penumbra azul pizarra del sueño en claro, que tapa lo que queda lejos de la tira (multiplicar pesaba al pintar). */
        val PENUMBRA_SUENO = Color(0xFF2E3858)
    }

    /** Hacia dónde cae la sombra de algo que está en [p], de [largo]: de día, abajo a la derecha; de noche, lejos de la luz. */
    fun caida(p: Offset, largo: Float): Offset {
        if (!a.lumbre) return Offset(largo, largo * 1.15f)
        val v = p - (if (a.noche) guardia else lampara)
        val d = v.getDistance().coerceAtLeast(1f)
        return v / d * (largo * 1.3f)
    }

    /**
     * El color [c] de una pieza grande en [r], con la luz de su centro (con [plano], la entera: la caída la pone el mapa).
     * Sin degradados: pesaban al pintar. Con [papel], el de la tarjeta.
     */
    fun pincel(c: Color, r: Rect, papel: Boolean = false): Brush = SolidColor(if (papel) a.papel(en(r.center)) else a.luz(c, en(r.center)))
}

/**
 * Dónde va cada parte del libro, en píxeles: la tapa de [bl] a [br] y de [bt] a [bb]; el lomo, de [bl] a [lomo]; el grueso
 * de las hojas que se ve abajo y a la derecha ([g]: 3 dp en blanco, más con más palabras, hasta 12) hacia [fondo]; la
 * tarjeta del extracto ([tarjeta], la caja de [MuebleTapaEspacio.CAJA] con un margen) y la goma ([goma], su x).
 */
internal class GeoTapaEspacio(w: Float, h: Float, u: Float, palabras: Int, vacio: Boolean) {
    val bl = w * 0.05f
    val br = w * 0.875f
    val bt = h * 0.07f
    val bb = h * 0.872f
    val lomo = bl + maxOf(10f * u, w * 0.034f)
    val g = (if (vacio || palabras <= 0) 3f else minOf(12f, 3f + 2.1f * ln(1f + palabras / 25f))) * u
    val fondo = Offset(g * 0.8f, g * 0.7f)
    val libro = Rect(bl, bt, br, bb)
    val tarjeta = Rect(w * 0.105f, h * 0.27f, w * 0.83f, h * 0.67f)
    val goma = w * 0.853f
}
