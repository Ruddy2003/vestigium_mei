package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Mar
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoDeFoco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El barco esponjero: el cuadro de casi todo el año (`composicion.md` de la tanda S16, cuadro A). Un barco chico y quieto
 * sobre un agua tan clara que se ve el fondo; trabajo de buzos: cobre, lona, cuerda y esponjas.
 *
 * **Desde dónde.** De pie en la popa, sobre la tapa del tambucho (los ojos a [OJO] metros de la cubierta), mirando hacia
 * proa y hacia fuera por la borda de estribor, un poco hacia abajo: la vista gira [GIRO] del eje del barco hacia
 * estribor. Así la borda, baja, es la diagonal grande: sale del borde de abajo, a la derecha, y sube hasta la proa, arriba a
 * la izquierda; el agua queda a la derecha y en el centro (más de un tercio del cuadro) y la cubierta abajo a la
 * izquierda. Es lo que no tienen los otros dos cuadros a bordo: Pirata mira de través, el bergantín de Botánico a lo
 * largo; este mira **hacia abajo, al agua**. **[Diseño propio]**: la tapa del tambucho de popa como sitio de quien mira.
 *
 * **La perspectiva.** La cámara de agujero de Mongol ([Mirada], [Sitio]) con el horizonte en [H] y la focal [F]. Las
 * medidas del barco van en metros desde los ojos: u a estribor, h sobre la cubierta, v hacia proa ([pb]); una goleta de
 * 14 metros y 4.4 de manga **[Aproximación]**: la crujía a 0.75 m a babor de los ojos, la borda a 1.45 m a estribor; el
 * palo mayor a 3 m, el trinquete a 8.5 m, la roda a 11.5 m. La cubierta está a [FRANCO] metros sobre el agua.
 *
 * **La luz** ([Vista]). De día, el sol de todas las escenas; las sombras de la cubierta, acortadas para que caigan dentro
 * del barco **[Aproximación]** (como en Botánico). La «mancha de sol que camina» es aquí **la sombra calada de las
 * ristras y de la botavara sobre la cubierta** (por la mañana entra de estribor y cruza las tablas; a mediodía se
 * encoge bajo lo que cuelga; por la tarde cae al agua) y, en el agua, **la sombra del barco en el fondo**, que se tiende
 * hacia fuera por la tarde, con la red de luz del sol en la arena y los haces que bajan. El cobre de los cascos es lo que
 * la luz enciende. De noche, a tres luces: la lamparilla del icono (y el farol de 18:00 a 20:00), cálidas; la luna,
 * fría, con su calle en el agua y su plata en la cubierta; y el verde vivo del agua.
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **El mar** ([MarAbismo], `EscenaAbismoBarcoMar.kt`): el cielo, la costa, los otros barcos, el agua transparente con su
 *   fondo, el bote, los pelícanos.
 * - **El aparejo** ([AparejoAbismo], `EscenaAbismoBarcoAparejo.kt`): el trinquete, la botavara izada con las ristras, la
 *   jarcia y, delante, el palo mayor con los cascos y el traje colgados (el marco de la izquierda y de arriba).
 * - **La cubierta** ([CubiertaAbismo], `EscenaAbismoBarcoCubierta.kt`): las tablas, la borda, la capa de sombras y lo que
 *   hay encima: la bomba, la escotilla con el objeto del día, la tina, las botas, la manguera, la escala, las esponjas,
 *   las mantas, la hornacina del icono.
 */
internal object BarcoAbismo {
    private val C = ColorAbismo

    // ---------------------------------------------------------------- La cámara

    const val H = 96f
    const val F = 230f
    const val CX = 190f
    /** La altura de los ojos sobre la cubierta, en metros (de pie sobre la tapa del tambucho de popa). */
    const val OJO = 2.2f
    /** La cubierta sobre el agua, en el costado. */
    const val FRANCO = 0.6f
    /** Cuánto gira la vista del eje del barco hacia estribor (24°). */
    const val GIRO = 0.4189f
    private val CG = cos(GIRO); private val SG = sin(GIRO)
    val MIRADA = Mirada(H, F, CX)

    /** Un punto en metros desde los ojos (x a la derecha, h sobre la cubierta, z de fondo) en el lienzo. */
    fun pc(x: Float, h: Float, z: Float): Offset { val zz = z.coerceAtLeast(0.15f); return Offset(CX + F * x / zz, H + F * (OJO - h) / zz) }

    /** De los ejes del barco (u a estribor, v hacia proa, en metros desde los ojos) a los de la cámara (x, z). */
    fun cam(u: Float, v: Float): Offset = Offset(u * CG - v * SG, u * SG + v * CG)

    /** Un punto del barco (u, h, v) en el lienzo. */
    fun pb(u: Float, h: Float, v: Float): Offset { val c = cam(u, v); return pc(c.x, h, c.y) }

    /** Cuántas unidades del lienzo mide un metro a [z] metros de fondo. */
    fun k(z: Float): Float = F / z.coerceAtLeast(0.15f)

    /** El sitio de una cosa que pisa la cubierta en (u, v), con los ejes del barco (x a estribor, z hacia proa) más [giro]. */
    fun sitio(u: Float, v: Float, giro: Float = 0f): Sitio { val c = cam(u, v); return Sitio(MIRADA, pc(c.x, 0f, c.y), k(c.y), GIRO + giro) }

    // ---------------------------------------------------------------- El barco

    /** La crujía, a babor de los ojos. */
    const val EJE = -0.75f
    const val MEDIA_MANGA = 2.2f
    /** La altura de la regala sobre la cubierta, en el combés; sube hacia la proa. */
    const val REGALA = 0.72f
    const val PALO_MAYOR = 3.0f
    const val TRINQUETE = 8.5f
    const val RODA = 11.5f
    const val POPA = -2.5f

    /** La media manga a [v] metros: igual hasta los 6 m y, hacia la proa, se cierra en la roda. */
    fun media(v: Float): Float = if (v <= 6f) MEDIA_MANGA else MEDIA_MANGA * sqrt((1f - ((v - 6f) / (RODA - 6f)).let { it * it }).coerceAtLeast(0f))
    fun estribor(v: Float) = EJE + media(v)
    fun babor(v: Float) = EJE - media(v)
    /** El arrufo: la regala sube hacia la proa. */
    fun regala(v: Float) = REGALA + 0.38f * ((v - 5f) / (RODA - 5f)).coerceIn(0f, 1f).let { it * it }

    // ---------------------------------------------------------------- Dónde está cada cosa

    /**
     * La bomba: en medio de la cubierta, un poco a estribor, con el frente (el manómetro) hacia popa, girada lo justo para
     * que se lean a la vez la caja con su manómetro y las dos ruedas en escorzo.
     */
    val SITIO_BOMBA = sitio(0.32f, 3.85f, 0.2f)
    /** La tapa de la escotilla de proa, detrás de la bomba: ahí se ensartan las ristras y se juega a las cartas en el corro. */
    const val ESCOTILLA_U = -0.05f
    const val ESCOTILLA_V = 6.6f
    val SITIO_ESCOTILLA = sitio(ESCOTILLA_U, ESCOTILLA_V, -0.05f)
    const val ESCOTILLA_ANCHO = 0.95f
    const val ESCOTILLA_FONDO = 0.7f
    const val ESCOTILLA_ALTO = 0.34f
    /** La tina de lavar las esponjas, cortada por el borde de abajo, a la izquierda. */
    val SITIO_TINA = sitio(-0.3f, 2.95f, 0f)
    /** Las botas de plomo, a la derecha de la bomba: una de pie y la otra caída de lado. */
    val SITIO_BOTA = sitio(0.66f, 3.5f, -0.5f)
    val SITIO_BOTA2 = sitio(0.92f, 3.22f, 0.7f)
    /** El montón de las esponjas limpias, abajo, junto a la borda. */
    const val LIMPIAS_U = 1.0f
    const val LIMPIAS_V = 2.7f
    /**
     * La hornacina del icono, en la aleta de estribor, abajo a la derecha: clavada en lo alto de un candelero que sube de
     * la borda, para que se vea entera sobre el agua y su lamparilla alumbre la regala, las tablas y el agua.
     */
    const val ICONO_V = 1.62f
    const val ICONO_U = 0.14f
    const val ICONO_H = 1.12f

    /** Dónde se pone en la borda la manguera (y la cuerda de vida) para bajar al agua. */
    const val BAJADA_V = 3.25f

    /** El suelo del gato: la cubierta de delante, entre la tina y la borda. */
    const val PISO_GATO = 263f
    const val GATO_DESDE = 66f
    const val GATO_HASTA = 192f

    /** El centro de la tabla del icono en el lienzo y su escala. */
    val ICONO: Offset get() = pb(estribor(ICONO_V) - ICONO_U, ICONO_H, ICONO_V)
    val K_ICONO: Float get() = k(cam(estribor(ICONO_V) - ICONO_U, ICONO_V).y)
    /** Dónde arde la lamparilla, en el lienzo (la luz cálida que no se apaga nunca). */
    val LAMPARILLA: Offset get() = PiezasAbismo.llamaDeLaLamparilla(ICONO, K_ICONO)

    /** La escala de los objetos del día sobre la escotilla: la de Mongol, 60 unidades por metro a escala 1. */
    fun escalaEn(z: Float): Float = k(z) / 60f

    /** Dónde va el objeto chico del día: sobre la tapa de la bomba, en medio del cuadro (la escotilla queda detrás de los cascos). */
    fun soporte(): Offset = SITIO_BOMBA.p(0f, PiezasAbismo.BOMBA_ALTO + 0.01f, 0f)

    /** El objeto grande pisa delante de la bomba, a su izquierda, y se recuesta en ella. */
    val PIE_GRANDE: Offset get() = pb(0.1f, 0f, 3.05f)
    val APOYO_GRANDE: Offset get() = SITIO_BOMBA.p(-0.25f, 0.62f, -0.15f)

    // ---------------------------------------------------------------- La luz

    /** Lo más larga que es una sombra en cubierta: por cada metro de alto, a lo sumo este tanto de largo. */
    const val COT_MAX = 1.8f
    const val LAT_MIN = 0.6f

    /**
     * El rayo del barco: el de [EscenaAbismo.rayo] (el sol, o de noche la luna), con las sombras acortadas y un poco más
     * de lado, para que la sombra de las ristras, de la botavara y de la jarcia caiga en la cubierta **[Aproximación]**.
     */
    internal fun rayoDelBarco(l: Luces): Rayo {
        val r = EscenaAbismo.rayo(l)
        val lat = if (abs(r.lat) < LAT_MIN) (if (r.lat < 0f) -LAT_MIN else LAT_MIN) else r.lat
        val cot = r.cot.coerceIn(0.45f, COT_MAX)
        val fuerza = if (r.dia < 0.05f) (r.fuerza * 2.2f).coerceAtMost(0.6f) else r.fuerza
        return Rayo(r.dia, lat, cot, 1f / cot, -lat * cot, -0.33f * (1.3f - 0.6f * abs(lat)) * cot, fuerza, r.color, r.contraste)
    }

    /** Dónde cae en la cubierta (en metros de la cámara: x, z) la sombra del punto (x, h, z). */
    fun alSuelo(sol: Sol3, x: Float, h: Float, z: Float): Offset = Offset(x + sol.sx * h, z + sol.sz * h)

    /** Las luces cálidas encendidas a esta hora, de 0 a 1: la lamparilla del icono y el farol. */
    internal class Lumbres(val lamparilla: Float, val farol: Float)

    /** Cuánto alumbra el farol «ya bajo» las primeras horas del sueño. */
    const val FAROL_BAJO = 0.45f
    /** Hasta qué hora queda el farol bajo encendido. */
    const val FAROL_HASTA = 22.5

    /**
     * El farol de cubierta, de 0 a 1: entero en el corro de la tarde y en el bocado de antes del alba, y la noche de Pascua
     * ([EscenaAbismo.farol]); y **ya bajo** ([FAROL_BAJO]) las primeras horas del sueño, de las 20:00 a las 22:30, hasta
     * que el último se duerme **[Diseño propio]** («el farol, de las 18 a las 20, ya bajo o apagado»). Así la noche de las
     * 21:00 tiene tres luces: el farol, la lamparilla y la luna; más tarde quedan la lamparilla y la luna.
     */
    internal fun farolDeCubierta(m: Momento): Float = when {
        EscenaAbismo.lugar(m) != EscenaAbismo.Lugar.BARCO -> 0f
        EscenaAbismo.farol(m) -> 1f
        EscenaAbismo.fase(m) == EscenaAbismo.Fase.SUENO && m.hora >= 20.0 && m.hora < FAROL_HASTA -> FAROL_BAJO
        else -> 0f
    }

    /**
     * Cuántos trajes de lona hay puestos a secar sobre la regala: uno por la mañana (los otros los llevan puestos el buzo
     * que está abajo y el que espera su turno), dos desde que se acaba de bajar hasta el anochecer (los de la mañana,
     * mojados), ninguno de noche (doblados, en la bodega); los días que no se baja, uno, seco **[Diseño propio]**. El día
     * que el traje es el objeto, ninguno: lo pinta el objeto.
     */
    internal fun trajesASecar(m: Momento): Int {
        if (EscenaAbismo.lugar(m) != EscenaAbismo.Lugar.BARCO || !ObjetosAbismo.elCuadroPinta(m, "escafandra")) return 0
        return when (EscenaAbismo.fase(m)) {
            EscenaAbismo.Fase.SUENO -> 0
            EscenaAbismo.Fase.ALBA, EscenaAbismo.Fase.INMERSION -> 1
            EscenaAbismo.Fase.COMIDA, EscenaAbismo.Fase.ESPONJAS, EscenaAbismo.Fase.CORRO -> if (EscenaAbismo.diaSinBajar(m) || m.hora < 11.0) 1 else 2
        }
    }

    /**
     * El montón de esponjas ya limpias («las dejan como pan blanco»), de 0 a 1: crece mientras se limpian, por la tarde, y
     * al anochecer ya están todas ensartadas en las cuerdas.
     */
    internal fun esponjasLimpias(m: Momento): Float {
        if (EscenaAbismo.lugar(m) != EscenaAbismo.Lugar.BARCO || EscenaAbismo.diaSinBajar(m)) return 0f
        val h = m.hora.toFloat()
        return when (EscenaAbismo.fase(m)) {
            EscenaAbismo.Fase.ESPONJAS -> ((h - 12.5f) / 4.5f).coerceIn(0.15f, 1f)
            EscenaAbismo.Fase.COMIDA -> if (h >= 17f) 1f else 0f
            EscenaAbismo.Fase.ALBA, EscenaAbismo.Fase.INMERSION, EscenaAbismo.Fase.CORRO, EscenaAbismo.Fase.SUENO -> 0f
        }
    }

    /**
     * Cuánto se corre en el fondo (en metros de la cámara: x, z) la sombra del casco: lo que dice el agua ([AguaClara.corre])
     * por los cuatro metros y medio que hay hasta la arena, sin pasar de unos metros, y medio metro hacia fuera **[Aproximación]**
     * (el casco tiene pantoque: su sombra asoma por estribor aun con el sol alto). Por la mañana, con el sol por estribor,
     * cae bajo el barco y casi no se ve; a mediodía asoma como una franja junto al costado; por la tarde se tiende hacia
     * fuera, ancha, con la red de luz alrededor: es la mancha que camina de este cuadro.
     */
    internal fun corrimientoDeLaSombra(a: AguaClara): Offset {
        val c = a.corre * (a.hondo + 0.5f)
        return Offset((c.x + 0.5f).coerceIn(-3f, 4.5f), c.y.coerceIn(0.3f, 3.5f))
    }

    /** Dónde está el farol: colgado de una driza larga del cabo de las ristras, sobre la bomba y el corro, a la altura del pecho. */
    private const val FAROL_U = 0.35f
    private const val FAROL_V = 2.85f
    const val FAROL_ALTO = 1.55f
    val FAROL: Offset get() = pb(FAROL_U, FAROL_ALTO, FAROL_V)
    val K_FAROL: Float get() = k(cam(FAROL_U, FAROL_V).y)
    /** El farol en metros de la cámara (x, alto, z), para las sombras que tumba. */
    val LUMBRE_FAROL: V3 get() { val c = cam(FAROL_U, FAROL_V); return V3(c.x, FAROL_ALTO + 0.05f, c.y) }

    /**
     * Todo lo que el cuadro de un momento comparte entre sus planos: la luz, el pincel, las lumbres, el mar y lo que
     * dice el horario.
     */
    internal class Vista(val m: Momento) {
        val l: Luces = deNoche(EscenaAbismo.luces(m))
        val r: Rayo = rayoDelBarco(l)
        val sol = Sol3(r)
        val p = Pincel(l, r, 0f, C.LUMBRE)
        val mar: Mar = EscenaAbismo.mar(m)
        val luna: Float = EscenaAbismo.luna(l)
        val lu = Lumbres(lamparilla = 0.25f + 0.75f * l.noche, farol = farolDeCubierta(m))
        /** Cuánto se inclina el horizonte (con tormenta, el barco escora), en radianes. */
        val escora: Float = when (mar) { Mar.TORMENTA -> 0.045f; Mar.HURACAN -> 0.03f; Mar.NORTE -> 0.012f; Mar.CALMA, Mar.CLARA -> 0f }

        /**
         * El pincel de lo que está en [en] (en el lienzo): de día, el del sol; de noche, con el calor de la lamparilla o
         * del farol si están cerca, y la sombra tumbada lejos de ellos.
         */
        fun pincel(en: Offset): Pincel {
            if (l.noche < 0.3f) return p
            val fuentes = listOf(Triple(LAMPARILLA, lu.lamparilla * 0.75f, 70f), Triple(FAROL, lu.farol, 105f))
            var mejor: Pair<Offset, Float>? = null; var calor = 0f
            for ((q, f, alcance) in fuentes) { if (f <= 0.02f) continue; val d = (q - en).getDistance(); val kk = f / (1f + (d / alcance) * (d / alcance)); calor += kk; if (mejor == null || kk > mejor.second) mejor = q to kk }
            if (mejor == null || calor < 0.06f) return p
            val rr = rayoDeFoco(Foco(mejor.first, C.LUMBRE, 1f, 60f, suelo = en.y), en, 0.4f, C.SOMBRA)
            return Pincel(l, Rayo(rr.dia, rr.lat, rr.cot, rr.tan, rr.ax, rr.ay, rr.fuerza, rr.color, maxOf(rr.contraste, r.contraste)), (calor * 1.2f * l.noche).coerceIn(0f, 0.9f), C.LUMBRE)
        }
    }

    /** La luz de fuera con la noche más honda a bordo: así lo que alumbran la lamparilla, el farol y la luna destaca. */
    private fun deNoche(l: Luces): Luces {
        if (l.noche < 0.05f) return l
        val k = 1f - 0.3f * l.noche
        val amb = Color((l.ambiente.red * k * 0.94f).coerceIn(0f, 1f), (l.ambiente.green * k * 0.97f).coerceIn(0f, 1f), (l.ambiente.blue * k).coerceIn(0f, 1f))
        return Luces(alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = l.bruma, brumaFuerza = l.brumaFuerza, solX = l.solX, solAltura = l.solAltura,
            solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura, estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = l.eclipse)
    }

    /** El color de la luna sobre lo que platea. */
    internal fun plata(v: Vista): Color = lerp(EscenaAbismo.LUZ_DE_LUNA, Color.White, 0.2f)

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = Vista(m)
        with(MarAbismo) { mar(v) }
        with(CubiertaAbismo) { suelo(v) }
        with(AparejoAbismo) { lejos(v) }
        with(CubiertaAbismo) { cosas(v) }
        with(AparejoAbismo) { marco(v) }
        tiempoQuieto(v.l, m.ficha.clima, H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("abismo", "grano", "barco")), 0.6f + 0.4f * v.r.dia)
    }

    /** El agua siempre se mueve (los destellos, la red de luz, el verde de noche). */
    fun seMueve(m: Momento): Boolean = true

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = Vista(m)
        with(MarAbismo) { vivo(v, reloj) }
        with(CubiertaAbismo) { vivo(v, reloj) }
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = Vista(m); val p = v.pincel(en)
        return LuzGato(p.r, v.l.noche) { if (p.calida > 0.05f) lerp(v.l.tono(it), mult(it, C.LUMBRE, 0.5f), p.calida * 0.4f) else v.l.tono(it) }
    }

    /** En el paso del gato no hay nada plantado: la tina pisa más acá y la bomba más allá. */
    @Suppress("UNUSED_PARAMETER")
    fun estorbos(m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        val mirador = pb(estribor(2.85f) - 0.05f, regala(2.85f), 2.85f)
        add(Ancla(TipoAncla.MIRADOR, mirador.x, mirador.y))                       // sentado en la regala, mirando el agua
        add(Ancla(TipoAncla.OBJETO, soporte().x + 14f, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_BOMBA.pie.x - 26f, PISO_GATO))            // a sotavento de la bomba
        if (EscenaAbismo.fiesta(m)) add(Ancla(TipoAncla.ADORNO, 150f, 80f))
    }
}
