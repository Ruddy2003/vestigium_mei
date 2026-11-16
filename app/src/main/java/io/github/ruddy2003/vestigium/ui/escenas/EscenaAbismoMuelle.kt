package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs

/**
 * El muelle de Tarpon Springs: el cuadro del invierno (`composicion.md` de la tanda S16, cuadro B), del 18 de diciembre al
 * 25 de enero, y de los días en que el cuento pone el barco en tierra ([EscenaAbismo.diaDeTierra]). El río con la flota
 * amarrada, «un bosque sin hojas» de palos, y el pueblo griego detrás.
 *
 * **Desde dónde.** De pie en el muelle de la cabeza de la dársena, junto a la popa del barco de Nikos, mirando río arriba
 * en diagonal ([H], [F], los ojos a [OJO] m sobre las tablas; las medidas en metros desde los ojos: x a la derecha, h
 * sobre el muelle, z de fondo, [pm]):
 * - **la diagonal grande**: a la izquierda, la fila de barcos amarrados «de dos en dos» a la orilla de enfrente, con la
 *   popa hacia quien mira, que se aleja río arriba, con su bosque de palos y su reflejo;
 * - **la contradiagonal**: a la derecha, el borde del muelle largo y su calle, que se alejan hacia el centro, con los
 *   cafés, la palmera, la lonja y, detrás, la iglesia de San Nicolás;
 * - **el marco**: la popa del barco de Nikos, amarrado de costado a este muelle, cerca, cortada por el borde de la
 *   izquierda, con la hornacina del icono y su lamparilla, y la botavara con las ristras que entran por arriba;
 * - **el río**, en medio, la superficie grande: un espejo roto por alguna onda, con los reflejos de todo.
 * **[Diseño propio]**: la dársena en escuadra (el muelle de la cabeza, donde se pisa, y el largo de la derecha) para que el
 * río quede en medio y el suelo del gato delante.
 *
 * **La luz.** De día, el sol bajo de invierno ([EscenaAbismo.luces] lo baja): las sombras largas de la botavara, las
 * ristras y la jarcia del barco de Nikos cruzan las tablas del muelle (una reja que camina con la hora), y los reflejos del
 * agua bailan en el espejo de popa (parte viva). De noche, a dos luces: las lamparillas de los iconos en cada popa, los
 * faroles de los barcos, los cafés y las ventanas de la iglesia, cálidos y reflejados en el río, contra la luna.
 *
 * Los planos, cada uno en su archivo: [RioMuelle] (`EscenaAbismoMuelleRio.kt`: el cielo, la lejanía, el río y la fila de
 * barcos), [OrillaMuelle] (`EscenaAbismoMuelleOrilla.kt`: el muelle largo con su pueblo) y [CercaMuelle]
 * (`EscenaAbismoMuelleCerca.kt`: el barco de Nikos y el muelle de la cabeza, con el objeto del día).
 */
internal object MuelleAbismo {
    private val C = ColorAbismo

    // ---------------------------------------------------------------- La cámara

    const val H = 120f
    const val F = 300f
    const val CX = 190f
    /** Los ojos sobre las tablas del muelle. */
    const val OJO = 1.6f
    /** El agua del río, bajo las tablas. */
    const val AGUA = -1.0f
    val MIRADA = Mirada(H, F, CX)

    /** Un punto en metros desde los ojos (x a la derecha, h sobre el muelle, z de fondo) en el lienzo. */
    fun pm(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.15f); return Offset(CX + F * x / zz, H + F * (OJO - h) / zz) }

    fun k(z: Float): Float = F / z.coerceAtLeast(0.15f)

    /** El sitio de algo que pisa en (x, z) a [h] metros (las tablas, 0; la cubierta de un barco, más abajo), girado [giro]. */
    fun sitio(x: Float, z: Float, giro: Float = 0f, h: Float = 0f): Sitio = Sitio(MIRADA, pm(x, h, z), k(z), giro)

    // ---------------------------------------------------------------- El sitio

    /** El borde del muelle de la cabeza (donde se pisa), a esta distancia. */
    const val BORDE = 3.64f
    /** El borde del muelle largo de la derecha, a esta distancia a la derecha; empieza río arriba de [DESDE_DERECHA]. */
    const val BORDE_DERECHA = 9f
    const val DESDE_DERECHA = 13.5f
    /** La fila de barcos de enfrente: la de dentro, contra la orilla, y la de fuera, abarloada. */
    const val FILA_DENTRO = -8.6f
    const val FILA_FUERA = -4.1f
    /** Las popas de la fila, río arriba. */
    val POPAS = listOf(13f, 28.5f, 44f, 60f, 77f, 96f, 118f)

    /** El barco de Nikos: amarrado de costado al muelle de la cabeza, con la popa aquí y la proa hacia la izquierda. */
    const val NIKOS_POPA = -1.2f
    const val NIKOS_EJE = 5.9f
    const val NIKOS_CUBIERTA = -0.4f

    /** El suelo del gato: las tablas del muelle de la cabeza, delante. */
    const val PISO_GATO = 274f
    const val GATO_DESDE = 150f
    const val GATO_HASTA = 290f

    /** El cajón del primer plano, a la derecha: el soporte del objeto del día. */
    val SITIO_CAJON = Sitio(MIRADA, pm(1.05f, 0f, 2.75f), k(2.75f), -0.35f)
    const val CAJON_ALTO = 0.42f
    fun soporte(): Offset = SITIO_CAJON.p(0f, CAJON_ALTO + 0.01f, 0f)
    val PIE_GRANDE: Offset get() = pm(0.55f, 0f, 2.6f)
    val APOYO_GRANDE: Offset get() = SITIO_CAJON.p(-0.25f, CAJON_ALTO, 0f)
    fun escalaEn(z: Float): Float = k(z) / 60f

    // ---------------------------------------------------------------- La luz

    const val COT_MAX = 2.4f

    /** El rayo del muelle: el sol bajo de invierno o la luna, con las sombras tendidas un poco más de lado **[Aproximación]**. */
    internal fun rayoDelMuelle(l: Luces): Rayo {
        val r = EscenaAbismo.rayo(l)
        val lat = if (abs(r.lat) < 0.55f) (if (r.lat < 0f) -0.55f else 0.55f) else r.lat
        val cot = r.cot.coerceIn(0.5f, COT_MAX)
        val fuerza = if (r.dia < 0.05f) (r.fuerza * 2.0f).coerceAtMost(0.55f) else r.fuerza
        return Rayo(r.dia, lat, cot, 1f / cot, -lat * cot, -0.33f * (1.3f - 0.6f * abs(lat)) * cot, fuerza, r.color, r.contraste)
    }

    /** Lo que el cuadro de un momento comparte entre sus planos. */
    internal class Vista(val m: Momento) {
        val l: Luces = EscenaAbismo.luces(m)
        val r: Rayo = rayoDelMuelle(l)
        val sol = Sol3(r)
        val p = Pincel(l, r, 0f, C.LUMBRE)
        val flota: Flota = EscenaAbismo.flota(m) ?: Flota.LLENO
        val luces = EscenaAbismo.lucesDelMuelle(m)
        val luna: Float = EscenaAbismo.luna(l)
        /** La lamparilla de la popa de Nikos (siempre encendida; de día casi no se nota). */
        val lamparilla: Float = 0.25f + 0.75f * l.noche

        /** De noche, el pincel con el calor de la lamparilla de Nikos o de los cafés, si están cerca de [en]. */
        fun pincel(en: Offset): Pincel {
            if (l.noche < 0.3f) return p
            val fuentes = listOf(CercaMuelle.ICONO to lamparilla * 0.7f, OrillaMuelle.CAFE to luces.cafes * 0.8f)
            var mejor: Pair<Offset, Float>? = null; var calor = 0f
            for ((q, f) in fuentes) { if (f <= 0.02f) continue; val d = (q - en).getDistance(); val kk = f / (1f + (d / 70f) * (d / 70f)); calor += kk; if (mejor == null || kk > mejor.second) mejor = q to kk }
            if (mejor == null || calor < 0.06f) return p
            val rr = rayoDeFoco(Foco(mejor.first, C.LUMBRE, 1f, 70f, suelo = en.y), en, 0.4f, C.SOMBRA)
            return Pincel(l, Rayo(rr.dia, rr.lat, rr.cot, rr.tan, rr.ax, rr.ay, rr.fuerza, rr.color, maxOf(rr.contraste, r.contraste)), (calor * l.noche).coerceIn(0f, 0.85f), C.LUMBRE)
        }
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = Vista(m)
        with(RioMuelle) { lejos(v); rio(v); fila(v) }
        with(OrillaMuelle) { orilla(v) }
        with(CercaMuelle) { cerca(v) }
        tiempoQuieto(v.l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("abismo", "grano", "muelle")), 0.6f + 0.4f * v.r.dia)
    }

    fun seMueve(m: Momento): Boolean = true                                  // el río y las luces siempre se mueven

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = Vista(m)
        with(RioMuelle) { vivo(v, reloj) }
        with(CercaMuelle) { vivo(v, reloj) }
        tiempoVivo(v.l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = Vista(m); val p = v.pincel(en)
        return LuzGato(p.r, v.l.noche) { if (p.calida > 0.05f) lerp(v.l.tono(it), mult(it, C.LUMBRE, 0.5f), p.calida * 0.4f) else v.l.tono(it) }
    }

    /** La bita del muelle de la cabeza está en el paso del gato. */
    @Suppress("UNUSED_PARAMETER")
    fun estorbos(m: Momento): List<Float> = listOf(CercaMuelle.BITA.pie.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        val tapa = SITIO_CAJON.p(0.15f, CAJON_ALTO, 0.1f)
        add(Ancla(TipoAncla.MIRADOR, tapa.x + 8f, tapa.y))                          // sobre el cajón, mirando el río
        add(Ancla(TipoAncla.OBJETO, soporte().x - 24f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_CAJON.pie.x - 30f, PISO_GATO))           // al abrigo del cajón
        if (EscenaAbismo.fiesta(m) || EscenaAbismo.cruz(m)) add(Ancla(TipoAncla.ADORNO, 120f, 250f))
    }
}
