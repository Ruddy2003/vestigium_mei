package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Fase
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Alumbrado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.SolQueEntra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Lo que el cuadro del club necesita saber de un momento, calculado una vez: dónde está Lou, la fase del día, qué está
 * encendido, la luz de dentro y la de fuera, el sol que entra por las ventanas altas y el neón de la calle.
 */
internal class VistaDeco(val m: Momento) {
    val lugar: EscenaDeco.Lugar = EscenaDeco.lugar(m)
    val prov: Boolean = EscenaDeco.provisional(m)
    val nuevo: Boolean = EscenaDeco.clubNuevo(m)
    val fase: Fase = EscenaDeco.fase(m)
    val enc: EscenaDeco.Encendido = EscenaDeco.encendido(m)
    val gente: EscenaDeco.Gente = EscenaDeco.gente(m)
    /** La luz de dentro (la penumbra del sótano) y la de la calle. */
    val l: Luces = EscenaDeco.luces(m)
    val ext: Luces = m.luces()
    /** Cuánto se nota lo que da luz: poco de día, del todo de noche. */
    val noche: Float = ext.noche
    val sol: SolQueEntra? = EscenaDeco.sol(m)
    val neon: Float = EscenaDeco.neon(m)
    val humo: Float = EscenaDeco.humo(m)
    val puerta: EscenaDeco.Puerta = EscenaDeco.puerta(m)

    /** Hacia dónde viaja la luz del sol, por cada metro que cruza la sala hacia la izquierda. */
    val dirSol: V3? = sol?.let { V3(-1f, -it.e, -it.a) }
    /** Hacia dónde viaja la luz del letrero de enfrente que entra por las ventanas (de arriba y un poco del fondo). */
    val dirNeon: V3 = V3(-1f, -0.68f, -0.1f)
    /**
     * La luz fría que entra por las ventanas altas y modela la sala (la de color opuesto a las lámparas y al sol): de día,
     * cuando el sol no entra (a primera hora, con el cielo cubierto), la claridad gris azulada que baja de la acera; de
     * noche, la de la farola y del cielo, como la luna por la ventana de Renacimiento. Deja su mancha con la reja en el
     * suelo y tumba sus sombras. Con nieve, la calle blanca la aviva. **[Diseño propio]**.
     */
    val frio: Float = run {
        val dia = (1f - ext.noche).coerceIn(0f, 1f); val nieve = if (EscenaDeco.nevada(m)) 1.15f else 1f
        val deDia = if (sol == null) 0.9f * dia * (1f - 0.3f * ext.nublado) else 0.25f * dia * (1f - sol.fuerza)
        (maxOf(deDia, ext.noche * 0.85f) * nieve).coerceIn(0f, 1f)
    }
    val dirFrio: V3 = if (ext.noche > 0.5f) V3(-1f, -1.35f, -0.85f) else V3(-1f, -1.2f, -0.7f)
    val colorFrio: Color = lerp(lerp(lerp(ext.medio, Color.White, 0.5f), Color(0xFFC2D2F4), 0.55f), ColorDeco.FAROLA, ext.noche.coerceIn(0f, 1f))

    val focos: List<Foco> = LuzDeco.focos(this)
    val a: Alumbrado = Alumbrado(l, focos)

    /** El color del sol en lo que alumbra: su luz cálida sobre el color propio. */
    private fun alSolColor(propio: Color): Color {
        val s = sol?.color ?: Color.White
        return Color((propio.red * s.red * 1.18f + 0.04f).coerceIn(0f, 1f), (propio.green * s.green * 1.12f + 0.03f).coerceIn(0f, 1f), (propio.blue * s.blue * 0.98f + 0.02f).coerceIn(0f, 1f), propio.alpha)
    }

    /**
     * El color [propio] de algo que está en [w] (en la sala) con su cara mirando a [n]: la penumbra de dentro, lo que le
     * llega de cada luz encendida (las de la izquierda en la cara que mira a la izquierda, y al revés), el sol si está
     * dentro del haz de una ventana y la cara lo mira, y de noche el neón que entra por las ventanas.
     */
    fun c(w: V3, propio: Color, n: V3, conSol: Boolean = true): Color {
        val p = CamDeco.p(w)
        var col = a.en(p, propio, (n.x * 1.2f).coerceIn(-1f, 1f))
        if (n.y < -0.4f) col = lerp(col, Color(col.red * 0.78f, col.green * 0.76f, col.blue * 0.9f, col.alpha), -n.y * 0.6f)
        val s = sol; val d = dirSol
        if (conSol && s != null && d != null) {
            val k = alSol(w)
            if (k > 0.01f) {
                val largo = sqrt(d.x * d.x + d.y * d.y + d.z * d.z); val nl = sqrt(n.x * n.x + n.y * n.y + n.z * n.z).coerceAtLeast(0.01f)
                val mira = (-(n.x * d.x + n.y * d.y + n.z * d.z) / (largo * nl)).coerceIn(0f, 1f)
                col = lerp(col, alSolColor(propio), (k * s.fuerza * (0.25f + 0.75f * mira)).coerceIn(0f, 0.92f))
            }
        }
        if (frio > 0.05f) {
            val k = enElHaz(w, dirFrio, 0.08f)
            if (k > 0.01f) { val fr = colorFrio
                col = lerp(col, Color((propio.red * 0.75f + 0.12f) * fr.red, (propio.green * 0.75f + 0.12f) * fr.green, (propio.blue * 0.75f + 0.12f) * fr.blue, propio.alpha), (0.5f * k * frio).coerceIn(0f, 1f)) }
        }
        if (neon > 0.05f && !prov) {
            val k = enElHaz(w, dirNeon, 0.18f)
            if (k > 0.01f) col = lerp(col, Color((propio.red * 0.6f + 0.3f) * ColorDeco.NEON.red, (propio.green * 0.6f + 0.3f) * ColorDeco.NEON.green * 0.9f, (propio.blue * 0.6f + 0.3f) * ColorDeco.NEON.blue, propio.alpha), (0.35f * k * neon).coerceIn(0f, 1f))
        }
        return col
    }

    /** Lo mismo sin el sol directo: para las superficies grandes (el suelo, las paredes, el techo), que reciben la mancha de sol aparte. */
    fun s(w: V3, propio: Color, n: V3): Color = c(w, propio, n, conSol = false)

    /** Lo mismo para algo pintado de frente, dado su punto del lienzo (lo lejano y lo plano). */
    fun c2(p: Offset, propio: Color, mira: Float = 0f): Color = a.en(p, propio, mira)

    /**
     * El pincel de un sitio del lienzo, para lo que se pinta con `Pincel` (los objetos del día): su luz es la que llega
     * ahí (la penumbra, las lámparas, el neón), y sus sombras las tumba la luz que más lo alumbra.
     */
    fun pincel(p: Offset): Pincel {
        val luz = a.en(p, Color.White)
        val local = Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = luz, bruma = l.bruma, brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f)
        val manda = focos.maxByOrNull { f -> val d = f.p - p; f.fuerza / (1f + (d.x * d.x + d.y * d.y) / (f.alcance * f.alcance)) }
        val lat = if (manda == null || manda.p.x >= p.x) 1f else -1f
        return Pincel(local, rayoFijo(0.7f * lat, 52f, 0.45f, ColorDeco.SOMBRA), 0f, ColorDeco.LAMPARA)
    }

    /** De 0 a 1: si el punto [w] está dentro del haz de sol de alguna ventana (con un borde blando). */
    fun alSol(w: V3): Float { val d = dirSol ?: return 0f; return enElHaz(w, d, 0.06f) }

    /**
     * De 0 a 1: si el rayo que llega a [w] viajando hacia [d] pasó por el hueco de una ventana alta de la pared derecha.
     * [blando]: el ancho del borde, en metros.
     */
    fun enElHaz(w: V3, d: V3, blando: Float): Float {
        if (w.x >= SalaDeco.DER) return 0f
        val t = (SalaDeco.DER - w.x) / -d.x
        val y = w.y - d.y * t; val z = w.z - d.z * t
        var mejor = 0f
        for ((z0, z1) in SalaDeco.VENTANAS) {
            val dy = minOf(y - SalaDeco.VY0, SalaDeco.VY1 - y); val dz = minOf(z - z0, z1 - z)
            val k = (minOf(dy, dz) / blando + 0.5f).coerceIn(0f, 1f)
            if (k > mejor) mejor = k
        }
        return mejor
    }
}

/**
 * El sótano del club de Sully: el cuadro de Art déco (`composicion_deco.md` de la tanda S15). De pie en la esquina del
 * estrado, junto al piano de Lou, mirando la sala hacia la escalera de la calle.
 *
 * Los planos, cada uno en su archivo, para que otro lo continúe:
 * - **La sala** ([SalaDeco], `EscenaDecoClubSala.kt`): el techo bajo de la antigua carbonera (chapa prensada, vigas y la
 *   tubería de la calefacción que cruza en diagonal), la pared del fondo y la de la izquierda (rojas, con su zócalo de
 *   madera y el friso déco pintado), el estrado, la barra con su espejo biselado, la puerta del camerino, el hueco de la
 *   escalera con su puerta arriba, las dos ventanas altas de la pared derecha con la calle a ras de acera, y el suelo
 *   encerado con la pista.
 * - **Lo que hay en ella** ([CosasDeco], `EscenaDecoClubCosas.kt`): el piano de Lou (el marco, a la izquierda), su
 *   taburete, los instrumentos del estrado, la fila de mesitas con sus sillas, el perchero, las cajas de gaseosa, el cubo;
 *   y delante, la mesa cercana con el objeto del día y la silla con el abrigo.
 * - **La luz** ([LuzDeco], `EscenaDecoClubLuz.kt`): de día, el sol que entra por las ventanas en haces con polvo y deja su
 *   mancha (con la reja) en el suelo, en las mesas y en la pared; de noche, las tres luces: las lámparas cálidas con el
 *   humo en conos, el neón de la calle que entra y late, y el azul frío de la noche en el hueco de la escalera; las sombras
 *   de cada una y los reflejos en el suelo encerado.
 */
internal object ClubDeco {
    /** Por dónde camina el gato: delante de todo, de la pata del piano a las cajas de gaseosa. */
    const val PISO_GATO = 276f

    fun seMueve(m: Momento): Boolean = true                                    // el polvo en el sol, el humo o el neón no paran

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaDeco(m)
        drawRect(v.c2(Offset(190f, 140f), ColorDeco.CAOBA), Offset.Zero, Size(ANCHO, ALTO))
        val cosas = CosasDeco.cosas(v)
        with(SalaDeco) { techo(v); paredes(v); suelo(v) }
        with(LuzDeco) { enLasSuperficies(v) }
        with(SalaDeco) { estrado(v); escalera(v); ventanas(v) }
        with(LuzDeco) { manchaEnElEstrado(v); sombras(v, cosas) }
        val lejos = cosas.filter { it.hondo > CosasDeco.CERCA }; val cerca = cosas.filter { it.hondo <= CosasDeco.CERCA }
        for (k in lejos) k.pintar(this)
        with(LuzDeco) { aire(v) }
        for (k in cerca) k.pintar(this)
        with(SalaDeco) { marco(v) }
        with(LuzDeco) { adornos(v); velo(v) }
        grano(Azar(Semilla.de("deco", "grano")), 0.55f + 0.35f * (1f - v.noche))
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) { with(LuzDeco) { vivo(VistaDeco(m), reloj) } }

    // ---------------------------------------------------------------- El gato

    /**
     * La luz del gato: la de la sala donde pisa (las lámparas, el neón, la penumbra) y, si está en el haz de sol, el sol.
     * Sus sombras las tumba la luz que más lo alumbra.
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaDeco(m)
        val centro = en + Offset(0f, -12f)
        val manda = v.focos.maxByOrNull { f -> val d = f.p - centro; f.fuerza / (1f + (d.x * d.x + d.y * d.y) / (f.alcance * f.alcance)) }
        val rayo = if (manda != null && manda.fuerza > 0.15f) {
            val dx = en.x - manda.p.x; val lat = if (dx >= 0f) -1f else 1f
            rayoFijo(lat * (0.4f + 0.5f * (abs(dx) / 200f).coerceIn(0f, 1f)), 50f, (0.3f + 0.3f * manda.fuerza).coerceIn(0f, 0.7f), ColorDeco.SOMBRA)
        } else rayoFijo(0.5f, 60f, 0.25f, ColorDeco.SOMBRA)
        val brillo = lerp(Color(0xFFFFE6C2), Color(0xFFE8C8F0), v.neon * 0.4f)
        return LuzGato(rayo, v.noche.coerceIn(0f, 1f), brillo) { c -> v.c2(centro, c, 0f) }
    }

    /** Lo plantado en el suelo del gato: la pata del taburete del piano y las de la silla del abrigo. */
    fun estorbos(m: Momento): List<Float> = listOf(CosasDeco.TABURETE_X, CosasDeco.SILLA_ABRIGO_X)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, 22f, PISO_GATO, hasta = 352f))
        add(Ancla(TipoAncla.MIRADOR, CosasDeco.MIRADOR.x, CosasDeco.MIRADOR.y))          // encima del piano, en el extremo cercano de la tapa
        add(Ancla(TipoAncla.OBJETO, CosasDeco.OBJETO_X, PISO_GATO))                       // al pie de la mesa cercana
        add(Ancla(TipoAncla.ABRIGO, CosasDeco.ABRIGO_X, PISO_GATO))                       // bajo el teclado del piano
        if (EscenaDeco.fiesta(m) && !EscenaDeco.provisional(m)) add(Ancla(TipoAncla.ADORNO, CosasDeco.GLOBO_X, PISO_GATO - 6f))
    }
}
