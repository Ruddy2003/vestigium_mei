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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * El muelle de Tarpon Springs: el cuadro del invierno (`composicion.md` de la tanda S16, cuadro B), del 18 de diciembre al
 * 25 de enero, y de los días en que el cuento pone el barco en tierra ([EscenaAbismo.diaDeTierra]). El río con la flota
 * amarrada «de dos en dos», «un bosque sin hojas» de palos, y el pueblo griego al otro lado del agua.
 *
 * **Desde dónde.** De pie en el muelle de tablas de la orilla de acá, junto a la popa del barco de Nikos, mirando río
 * arriba en diagonal, hacia la otra orilla (la cámara gira [GIRO] a la izquierda del río; los ojos a [OJO] m sobre las
 * tablas y a [DESDE_EL_CANTO] m del canto). Las medidas van en metros, en los ejes del río: X a lo ancho (0 es el canto de
 * este muelle; el agua, X negativas), Z río arriba ([pm] las lleva al lienzo).
 * - **Una sola superficie en perspectiva:** las tablas de este muelle corren a lo largo del río y fugan al mismo punto que
 *   la fila de barcos ([FUGA]); su canto sale del borde de abajo, hacia el centro, y se aleja en diagonal hasta la calle
 *   del muelle, a la derecha.
 * - **La diagonal grande:** a la izquierda, la fila de barcos amarrados de dos en dos a la otra orilla, con la popa hacia
 *   quien mira, que se aleja río arriba; detrás, el pueblo en tres cuartos (la lonja, los cafés, las casas de madera) y,
 *   por encima de los tejados, San Nicolás con su cúpula azul.
 * - **El marco:** la popa del barco de Nikos, amarrado detrás de quien mira con la popa hacia el río, cortada por el borde
 *   de la izquierda y el de abajo (la regala azul que da la vuelta, la tablazón, la caña del timón, la hornacina del icono
 *   y su lamparilla); su botavara, abierta sobre el muelle con la vela aferrada y las ristras, entra por arriba. A la
 *   derecha, el toldo del café y una de sus mesas, cortados por el borde.
 * - **El río**, en medio, la superficie grande: un espejo roto con los reflejos de todo, que llega hasta el horizonte por
 *   donde salen el sol y la luna (su calle cae en el agua).
 * - **La zona de los astros** (x 270–330, y 45–110) queda libre: por ahí solo se ve la lejanía baja del río.
 * **[Diseño propio]**: el sitio exacto (que el pueblo esté enfrente y no a este lado) es para que el río quede en medio,
 * el pueblo se vea en tres cuartos y la calle de la luna caiga en el agua.
 *
 * **La luz.** De día, el sol bajo de invierno ([rayoDelMuelle]): las sombras largas del toldo, de los postes del tendedero
 * con sus ristras, del farol y, por la tarde, de la botavara y la jarcia de Nikos cruzan las tablas del muelle, como una
 * reja que camina con la hora ([sombraEnLasTablas]). De noche, a dos luces: lo cálido (las lamparillas de cada popa, los
 * faroles del muelle, las ventanas de los cafés y de la iglesia, la luz del café de este lado), con su halo, su charco en
 * las tablas y su reflejo largo en el río, contra la luna, que platea los cascos, las tablas y la cúpula y deja su calle.
 *
 * Los planos, cada uno en su archivo: [RioMuelle] (`EscenaAbismoMuelleRio.kt`: el cielo, la lejanía, el agua y sus
 * reflejos), [OrillaMuelle] (`EscenaAbismoMuelleOrilla.kt`: el pueblo de enfrente y la fila de barcos) y [CercaMuelle]
 * (`EscenaAbismoMuelleCerca.kt`: este muelle con sus cosas, el café, la popa de Nikos y el objeto del día).
 */
internal object MuelleAbismo {
    private val C = ColorAbismo

    // ---------------------------------------------------------------- La cámara

    const val H = 128f
    const val F = 300f
    const val CX = 190f
    /** Los ojos sobre las tablas del muelle. */
    const val OJO = 1.6f
    /** El agua del río, bajo las tablas. */
    const val AGUA = -1.0f
    /** Cuánto gira la mirada a la izquierda del río, en radianes (unos 20°). */
    const val GIRO = 0.35f
    /** A qué distancia del canto está quien mira, sobre las tablas. */
    const val DESDE_EL_CANTO = 1.4f
    private val CG = cos(GIRO)
    private val SG = sin(GIRO)
    val MIRADA = Mirada(H, F, CX)

    /** El punto de fuga del río (y de las tablas y de la fila de barcos), en el horizonte. */
    val FUGA: Offset = Offset(CX + F * tan(GIRO), H)

    /** Un punto del río (X a lo ancho, Z río arriba), en metros desde los ojos, de lado y de fondo. */
    fun lado(x: Float, z: Float): Float = (x - DESDE_EL_CANTO) * CG + z * SG
    fun fondo(x: Float, z: Float): Float = -(x - DESDE_EL_CANTO) * SG + z * CG

    /** Un punto del río a [h] metros sobre las tablas, en el lienzo. */
    fun pm(x: Float, h: Float, z: Float): Offset { val zz = fondo(x, z).coerceAtLeast(0.15f); return Offset(CX + F * lado(x, z) / zz, H + F * (OJO - h) / zz) }

    /** Cuántas unidades mide un metro en (x, z). */
    fun k(x: Float, z: Float): Float = F / fondo(x, z).coerceAtLeast(0.15f)

    /** Cuánto se pierde en la bruma algo que está en (x, z), de 0 a 1 (para `luces.tono`). */
    fun lejos(x: Float, z: Float): Float = ((fondo(x, z) - 12f) / 160f).coerceIn(0f, 0.85f)

    /** La z más cercana de una línea del río (a lo ancho [x]) que todavía cae delante de los ojos (para cortar las formas que pasan por detrás). */
    fun zCerca(x: Float, fondoMinimo: Float = 0.45f): Float = (fondoMinimo + (x - DESDE_EL_CANTO) * SG) / CG

    /**
     * El sitio de algo que pisa en (x, z) a [h] metros (las tablas, 0; la cubierta de un barco, más abajo), con su frente
     * girado [giro] radianes desde río abajo (0: el frente mira río abajo, hacia quien mira; positivo, hacia la otra
     * orilla… vista desde aquí, a la derecha del cuadro).
     */
    fun sitio(x: Float, z: Float, giro: Float = 0f, h: Float = 0f): Sitio = Sitio(MIRADA, pm(x, h, z), k(x, z), giro - GIRO)

    // ---------------------------------------------------------------- El sitio

    /** El ancho del río, del canto de este muelle al de la otra orilla. */
    const val RIO = 17.5f
    /** El muelle de la otra orilla queda un poco más alto que el agua que este. */
    const val OTRA_ALTURA = 0.15f
    /** La fila de barcos amarrados a la otra orilla: el de dentro, contra el muelle, y el de fuera, abarloado. */
    const val FILA_DENTRO = -RIO + 2.35f
    const val FILA_FUERA = -RIO + 6.75f
    /** Dónde tienen la popa las parejas de la fila, río arriba. */
    val PAREJAS = listOf(9.5f, 25f, 43f, 61f, 81f, 104f, 130f)

    /** El barco de Nikos, amarrado a este muelle detrás de quien mira: su eje, su popa (hacia río arriba) y su manga. */
    const val NIKOS_EJE = -2.3f
    const val NIKOS_POPA = 3.6f
    const val NIKOS_MANGA = 4.4f

    /** El suelo del gato: las tablas de este muelle, delante. */
    const val PISO_GATO = 276f
    const val GATO_DESDE = 172f
    const val GATO_HASTA = 300f

    /** El cajón del primer plano: el soporte del objeto del día. */
    val SITIO_CAJON: Sitio get() = sitio(0.75f, 4.6f, -0.3f)
    const val CAJON_ALTO = 0.46f
    fun soporte(): Offset = SITIO_CAJON.p(0f, CAJON_ALTO + 0.01f, 0f)
    val PIE_GRANDE: Offset get() = pm(0.42f, 0f, 4.25f)
    val APOYO_GRANDE: Offset get() = SITIO_CAJON.p(-0.3f, CAJON_ALTO, 0f)
    fun escalaEn(z: Float): Float = k(1f, z) / 60f

    /** Los sacos del primer plano, donde se sube el gato a mirar el río. */
    val SITIO_SACOS: Sitio get() = sitio(1.75f, 6.2f, 0.2f)

    // ---------------------------------------------------------------- La luz

    const val COT_MAX = 2.4f

    /** El rayo del muelle: el sol bajo de invierno o la luna, con las sombras tendidas un poco más de lado **[Aproximación]**. */
    internal fun rayoDelMuelle(l: Luces): Rayo {
        val r = EscenaAbismo.rayo(l)
        val lat = if (abs(r.lat) < 0.55f) (if (r.lat < 0f) -0.55f else 0.55f) else r.lat
        val cot = r.cot.coerceIn(0.5f, COT_MAX)
        val fuerza = if (r.dia < 0.05f) (r.fuerza * 1.8f).coerceAtMost(0.5f) else r.fuerza
        return Rayo(r.dia, lat, cot, 1f / cot, -lat * cot, -0.33f * (1.3f - 0.6f * abs(lat)) * cot, fuerza, r.color, r.contraste)
    }

    /**
     * Hacia dónde caen las sombras en las tablas de este muelle, en metros por metro de alto (x: a lo ancho del río,
     * positivo hacia la calle; z: río arriba), o null si no hay sol ni luna que las tumbe. Es la «reja» de las sombras
     * del toldo, del tendedero, del farol y de la jarcia de Nikos: por la mañana el sol está a la derecha del cuadro y
     * las sombras van hacia el agua; por la tarde, hacia la calle; a mediodía, cortas. **[Aproximación]**: el sol de
     * `Luces` cruza el cuadro de derecha a izquierda; no es el recorrido real del sol sobre el río.
     */
    internal fun sombraEnLasTablas(l: Luces): Offset? {
        val r = rayoDelMuelle(l)
        if (r.fuerza < 0.04f) return null
        val s = Sol3(r)
        // Sol3 da la dirección en los ejes de quien mira: se lleva a los del río.
        return Offset(s.sx * CG - s.sz * SG, s.sx * SG + s.sz * CG)
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
        /** El farol de este muelle y la luz del café de este lado, de 0 a 1. */
        val farol: Float = luces.faroles * l.noche
        val cafe: Float = luces.cafes * l.noche
        /** Las sombras en las tablas (la reja), o null. */
        val reja: Offset? = sombraEnLasTablas(l)

        /** De noche, el pincel con el calor de la lamparilla de Nikos, del farol o del café, si están cerca de [en]. */
        fun pincel(en: Offset): Pincel {
            if (l.noche < 0.3f) return p
            val fuentes = listOf(CercaMuelle.ICONO to lamparilla * 0.6f, CercaMuelle.FAROL to farol * 0.9f, CercaMuelle.LUZ_DEL_CAFE to cafe * 0.85f)
            var mejor: Pair<Offset, Float>? = null; var calor = 0f
            for ((q, f) in fuentes) { if (f <= 0.02f) continue; val d = (q - en).getDistance(); val kk = f / (1f + (d / 80f) * (d / 80f)); calor += kk; if (mejor == null || kk > mejor.second) mejor = q to kk }
            if (mejor == null || calor < 0.06f) return p
            val rr = rayoDeFoco(Foco(mejor.first, C.LUMBRE, 1f, 80f, suelo = en.y), en, 0.4f, C.SOMBRA)
            return Pincel(l, Rayo(rr.dia, rr.lat, rr.cot, rr.tan, rr.ax, rr.ay, rr.fuerza, rr.color, maxOf(rr.contraste, r.contraste)), (calor * l.noche).coerceIn(0f, 0.85f), C.LUMBRE)
        }
    }

    // ---------------------------------------------------------------- Lo que el cuento mueve en el muelle

    /**
     * Los barcos amarrados a la otra orilla hoy: (x, z de la popa, semilla). Con la flota llena (el invierno), parejas en
     * toda la fila y alguna de tres; los días de tierra del verano (la lonja, el 4 de julio, la boda), unos pocos; la
     * mañana de zarpar faltan los de delante, que ya van saliendo; en el cuarto, ninguno.
     */
    internal fun barcos(m: Momento): List<Triple<Float, Float, Int>> {
        val todos = ArrayList<Triple<Float, Float, Int>>()
        PAREJAS.forEachIndexed { i, z ->
            todos += Triple(FILA_DENTRO, z, i * 3)
            todos += Triple(FILA_FUERA, z + 1.4f + (i % 3) * 0.7f, i * 3 + 1)
            if (i == 1 || i == 4) todos += Triple(FILA_FUERA + 4.4f, z + 2.6f, i * 3 + 2)       // las de tres en tres
        }
        return when (EscenaAbismo.flota(m) ?: Flota.LLENO) {
            Flota.LLENO -> todos
            Flota.POCOS -> todos.filter { it.third in setOf(3, 4, 9) }
            Flota.SALIENDO -> todos.filter { it.third >= 9 && it.third % 4 != 0 }
            Flota.VACIO -> emptyList()
        }
    }

    /** Si el barco de Nikos está amarrado a este muelle: siempre en el muelle, menos en el cuarto («ningún barco»). */
    internal fun nikosAmarrado(m: Momento): Boolean = EscenaAbismo.flota(m) != Flota.VACIO

    /**
     * Cuántas ristras cuelgan de la botavara de Nikos: en invierno, pocas (las esponjas están en la lonja); el día que el
     * barco llega cargado, muchas («cargados de esponjas hasta los palos»); los días de tierra del verano, algunas.
     */
    internal fun ristrasDeLaBotavara(m: Momento): Int = when {
        !nikosAmarrado(m) -> 0
        EscenaAbismo.cargadoEnElMuelle(m) -> 9
        EscenaAbismo.flota(m) == Flota.POCOS -> 5
        else -> 3
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = Vista(m)
        with(RioMuelle) { lejos(v); rio(v) }
        with(OrillaMuelle) { orilla(v) }
        with(RioMuelle) { sobreElRio(v) }
        with(CercaMuelle) { cerca(v) }
        with(RioMuelle) { tiempo(v) }
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

    /** La bita del muelle, en el paso del gato. */
    @Suppress("UNUSED_PARAMETER")
    fun estorbos(m: Momento): List<Float> = listOf(CercaMuelle.BITA.pie.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, GATO_DESDE, PISO_GATO, hasta = GATO_HASTA))
        val sacos = SITIO_SACOS.p(0f, 0.62f, 0f)
        add(Ancla(TipoAncla.MIRADOR, sacos.x, sacos.y))                              // sobre los sacos, mirando el río
        add(Ancla(TipoAncla.OBJETO, soporte().x, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, SITIO_CAJON.pie.x - 34f, PISO_GATO))           // al abrigo del cajón
        if (EscenaAbismo.fiesta(m) || EscenaAbismo.cruz(m)) add(Ancla(TipoAncla.ADORNO, 206f, 262f))
    }

    /** Si hoy llueve o hay tormenta: las tablas mojadas devuelven las luces. */
    internal fun mojado(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
}
