package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Compania
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Moscu
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Lo que el vivac necesita saber de un momento, calculado una vez: la luz (el sol o la luna, el fuego del rancho, las
 * sombras de las nubes), la nieve, la fase del día, el paisaje del tramo y lo que el cuento pone ese día.
 */
internal class VistaVivac(val m: Momento) {
    val lugar: Lugar = EscenaNapoleon.lugar(m)
    val fondo: VivacNapoleon.Fondo = VivacNapoleon.fondo(m)
    val campo: VivacNapoleon.Campo = VivacNapoleon.campo(m)
    val l: Luces = VivacNapoleon.luces(m)
    /** El sol, con la sombra no más larga que tres veces lo alto (como en la retirada). */
    val r: Rayo = RetiradaLuz.rayo(l)
    val sol: Sol3 = Sol3(r)
    val p: Pincel = Pincel(l, r, 0f, ColorNapoleon.LUMBRE)
    val nieve: Float = EscenaNapoleon.nieve(m)
    val fase: Fase = EscenaNapoleon.fase(m)
    val fuego: Float = VivacNapoleon.fuego(m)
    val noche: Float = l.noche
    /** El día cubierto: sin sombras arrojadas y con la bruma por capas. */
    val cubierto: Float = ((l.nublado - 0.4f) / 0.45f).coerceIn(0f, 1f)
    /** Lo que alumbra el fuego del rancho a lo que tiene cerca. */
    val lumbre: Float = RetiradaLuz.lumbre(fuego, l, cubierto)
    val luna: Float = RetiradaLuz.luna(l)
    val rLuna: Rayo? = RetiradaLuz.rayoDeLuna(l)
    /** Moscú, o Smolensk, que arde en el horizonte (la tercera luz, roja), de 0 a 1. */
    val incendio: Float = VivacNapoleon.incendio(m)
    /** Las sombras de las nubes del día sobre el campo, a esta hora. */
    val nubes: List<VivacLuz.SombraDeNube> = VivacLuz.sombrasDeNube(m, l, r)
    val a: Alumbre = Alumbre(p, sol, VivacCerca.LUZ_DEL_FUEGO, lumbre, 150f, luna * RetiradaLuz.LUNA_EN_LAS_COSAS, rLuna?.let { Sol3(it) })

    /** El alumbre de algo que pisa en [pie]: a la sombra si cae bajo una nube. */
    fun en(pie: Offset): Alumbre = a.aLaSombra(VivacLuz.enSombraDeNube(nubes, pie) * 0.85f)

    /** El color propio del suelo hoy, de lejos ([lejos] = 1) a cerca: la hierba, el trigo, el rastrojo, la arena o la nieve. */
    fun suelo(lejos: Float): Color = p.arriba(VivacCampo.colorDelCampo(this, lejos), lejos)
}

/**
 * **El vivac**: el cuadro de la campaña de Napoleónico (`composicion.md` de la tanda S14, cuadro B), todos los días que
 * Jean-Baptiste no está en la retirada. De pie entre los pabellones de la compañía, mirando el campamento en diagonal.
 * El horizonte va a y = 120.
 *
 * **Como la Ruta de la Seda: una base que no se mueve y un paisaje que cambia** (indicación del autor): el ejército se
 * mueve y cada noche el campamento es otro campo, pero la compañía acampa siempre igual. La **base** es el campamento de
 * la compañía, siempre en su sitio: la tienda de oficial que enmarca por la izquierda, el asta del águila, la fila de
 * fusiles en pabellón que se aleja en diagonal con las mochilas al pie, el fuego del rancho con la marmita para ocho, el
 * tambor, las ramas de dormir, el tocón de partir leña con el objeto del día y el montón de leña. Lo que **cambia** es el
 * campo y el horizonte del tramo de la campaña ([fondo]): el camino de Francia, Alemania, Polonia, el Niemen, el llano de
 * Rusia, Smolensk ardiendo, el campo de Borodino, Moscú, Prusia en invierno, el Rin, los Cien Días; y lo que el cuento
 * pone ese día.
 *
 * Los planos, de atrás adelante, cada uno en su archivo:
 * - **Lejos** ([VivacLejos], `EscenaNapoleonVivacLejos.kt`): el cielo, y el horizonte de cada tramo con sus lomas, sus
 *   bosques, sus pueblos, sus ríos y sus ciudades (Moscú entera, en llamas o quemada; Smolensk; los reductos de Borodino).
 * - **El campo** ([VivacCampo], `EscenaNapoleonVivacCampo.kt`): el llano del tramo (hierba, trigo, rastrojo, arena o
 *   nieve) que cambia de lejos a cerca; el camino real con el polvo de la columna; los campamentos de las otras compañías
 *   y de los otros batallones en filas que se van al mismo punto de fuga que la nuestra, con sus pabellones, sus tiendas
 *   y sus fuegos (de noche, «hogueras hasta el horizonte»); el parque de cañones y el de los carros.
 * - **La base** ([VivacCerca], `EscenaNapoleonVivacCerca.kt`): el campamento de la compañía.
 * - **La luz** ([VivacLuz], `EscenaNapoleonVivacLuz.kt`): las sombras de las nubes sobre el campo, las sombras del sol que
 *   caminan, el polvo dorado, la noche a dos luces (el fuego del rancho como sol, con las sombras en abanico, contra la
 *   luna fría y los cientos de fuegos lejanos) y la tercera luz, roja, de la ciudad que arde.
 *
 * **El horario** (lo dice [EscenaNapoleon.fase], igual que en la retirada), sobre una base que no se mueve: de 4:00 a
 * 12:00 y de 13:00 a 18:00 la compañía está de marcha y el cuadro es **el alto** (los fusiles en pabellón, las mochilas
 * en el suelo, la marmita junto al fuego de anoche, que es ceniza y humea por la mañana) mientras por el camino real pasa
 * el polvo de la columna; de 12:00 a 13:00, el alto de la comida (la marmita al fuego). Desde las 18:00, el vivac
 * encendido, con los fuegos hasta el horizonte. De madrugada, el rescoldo y las camas con sus mantas.
 *
 * **Los lugares que todavía no tienen cuadro** caen aquí ([EscenaNapoleon.provisional]): Moscú se pinta de verdad (el
 * vivac fuera de la ciudad, con sus cúpulas al fondo, el incendio y la ciudad quemada); el cuartel, el hospital de Prusia
 * y el pueblo de Borgoña son **provisionales** ([provisional]): la base sin lo que no está (sin la compañía en el
 * hospital y en el pueblo) y, al fondo, su ciudad o su pueblo.
 */
internal object VivacNapoleon {
    private val C = ColorNapoleon

    /** El horizonte. */
    const val H = 120f
    val MIRADA = Mirada(H, 560f)

    /** Un punto del suelo (x, z en metros, en el espacio de los ojos) en el lienzo. */
    fun w(x: Float, z: Float): Offset = Offset(MIRADA.centro + MIRADA.focal * x / z, H + MIRADA.focal * PiezasNapoleon.OJO / z)

    /** Cuántas unidades del lienzo mide un metro a la profundidad [z]. */
    fun k(z: Float): Float = MIRADA.focal / z

    /** La profundidad del suelo a la altura [y] del lienzo. */
    fun zEn(y: Float): Float = MIRADA.focal * PiezasNapoleon.OJO / (y - H).coerceAtLeast(0.5f)

    /** Cuánto se desvanece algo a la profundidad [z] (para `Luces.tono`). */
    fun lejos(z: Float): Float = (z / 260f).coerceIn(0f, 0.8f)

    /** El suelo del gato: delante de todo. */
    const val PISO_GATO = 278f

    fun seMueve(m: Momento): Boolean = true                                    // el humo del fuego, del rescoldo o de los fuegos lejanos no para

    // ---------------------------------------------------------------- El tramo de la campaña

    /**
     * El paisaje del fondo: el tramo de la campaña ([tramo]) o el lugar donde está. Cada uno con su horizonte y su
     * vegetación. Los tramos salen de los días del calendario y de lo que dice el cuento de cada uno (`AnioG5.kt`,
     * `PiezasJeanBaptiste.kt`); donde la fecha la pone el cuadro, es **[Aproximación]**.
     */
    internal enum class Fondo {
        /** Francia: el camino del Rin en marzo de 1812 («desde la primera colina… la ciudad se queda a la espalda»), y la campaña de Francia de 1814 («la nieve de la Champaña»). */
        FRANCIA,
        /** Alemania: «casas con vigas de madera pintadas, tejados empinados, iglesias con torres en punta» (abril de 1812, y la campaña de 1813). */
        ALEMANIA,
        /** Polonia: «un camino de arena, entre bosques de pinos y pueblos de casas de madera con techo de paja». */
        POLONIA,
        /** El Niemen: el 24 de junio, «uno de los puentes que tendieron de noche los pontoneros», «Solo bosque»; el 14 de diciembre, helado. */
        NIEMEN,
        /** El llano de Rusia en verano: «camino de arena… calor… polvo», el trigo, los bosques de pinos y las casas de madera. */
        RUSIA,
        /** Smolensk, el 17 y el 18 de agosto: «murallas viejas sobre un río», «un campo de trigo», «casas de madera que arden». */
        SMOLENSK,
        /** Borodino, el 7 de septiembre: «unos reductos de tierra llenos de cañones rusos… en el humo». */
        BORODINO,
        /** Moscú (el estado `moscu`): «cientos de cúpulas doradas y de colores»; luego en llamas; luego «un campo de chimeneas negras». */
        MOSCU,
        /** Prusia en invierno, después de cruzar el Niemen helado: «un pueblo con casas de verdad, con humo en las chimeneas». */
        PRUSIA,
        /** El Rin y Maguncia, de noviembre de 1813 al final del año (el campamento del tifus). */
        RIN,
        /** Los Cien Días (1815): el campo de Bélgica, de trigo alto, con sus granjas tapiadas. */
        CIEN_DIAS,
        /** El cuartel (provisional): la ciudad del cuartel, con el convento viejo. */
        CUARTEL,
        /** El hospital de Prusia (provisional): la ciudad con la iglesia llena de catres, nevada. */
        HOSPITAL,
        /** El pueblo de Borgoña (provisional): las viñas de la ladera, la iglesia, el río y los álamos. */
        PUEBLO,
    }

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /**
     * El tramo de la campaña en el vivac, por los días del calendario y del cuento:
     * - temporada 1 (1812; también 2026, el avance): hasta el 1 de abril, Francia (los 19 días de enero, antes del
     *   cuartel, y la marcha de marzo hasta el Rin: [Aproximación]); desde `rin_ida` (2 de abril), Alemania; desde `polonia`
     *   (10 de mayo), Polonia; `niemen` (24 de junio), el Niemen; luego Rusia, salvo `smolensk` y `borodino`; desde
     *   `niemen_vuelta` (14 de diciembre), el Niemen helado ese día y Prusia después;
     * - temporada 2 (1813): Prusia hasta el hospital; desde `regimiento` (20 de marzo), Alemania; desde `rin` (8 de
     *   noviembre), el Rin;
     * - temporada 3 (1814): Francia; temporada 4 (1815): los Cien Días.
     */
    internal fun tramo(m: Momento): Fondo {
        val md = mesDia(m)
        return when (m.ficha.temporada) {
            1 -> when {
                EscenaNapoleon.hoyEs(m, "smolensk") -> Fondo.SMOLENSK
                EscenaNapoleon.hoyEs(m, "borodino") -> Fondo.BORODINO
                EscenaNapoleon.hoyEs(m, "niemen") || EscenaNapoleon.hoyEs(m, "niemen_vuelta") -> Fondo.NIEMEN
                md >= "12-14" -> Fondo.PRUSIA
                md >= "06-24" -> Fondo.RUSIA
                md >= "05-10" -> Fondo.POLONIA
                md >= "04-02" -> Fondo.ALEMANIA
                else -> Fondo.FRANCIA
            }
            2 -> when {
                md >= "11-08" -> Fondo.RIN
                md >= "03-20" -> Fondo.ALEMANIA
                else -> Fondo.PRUSIA
            }
            3 -> Fondo.FRANCIA
            else -> Fondo.CIEN_DIAS
        }
    }

    /** El fondo de hoy: el del lugar si no está en campaña; si no, el del tramo. */
    internal fun fondo(m: Momento): Fondo = when (EscenaNapoleon.lugar(m)) {
        Lugar.MOSCU -> Fondo.MOSCU
        Lugar.CUARTEL -> Fondo.CUARTEL
        Lugar.HOSPITAL -> Fondo.HOSPITAL
        Lugar.PUEBLO -> Fondo.PUEBLO
        Lugar.VIVAC, Lugar.RETIRADA -> tramo(m)
    }

    /** Si hoy el cuadro es provisional (el cuartel, el hospital, el pueblo): se dibuja con decoro, pero no es su cuadro. */
    internal fun provisional(m: Momento): Boolean = when (EscenaNapoleon.lugar(m)) {
        Lugar.CUARTEL, Lugar.HOSPITAL, Lugar.PUEBLO -> true
        Lugar.VIVAC, Lugar.RETIRADA, Lugar.MOSCU -> false
    }

    // ---------------------------------------------------------------- El campo

    /** Lo que cubre el llano: la hierba, el trigo verde o maduro, el rastrojo, la arena del camino de Polonia o la nieve. */
    internal enum class Campo { HIERBA, TRIGO_VERDE, TRIGO, RASTROJO, ARENA, VINA, NIEVE }

    /**
     * El campo de hoy **[Aproximación]**: con nieve, la nieve; en Polonia y el día del Niemen, la arena («camino de
     * arena»); en el pueblo, las viñas; si no, por el mes: la hierba del invierno y la primavera, el trigo verde de mayo y
     * junio, el trigo maduro de julio y agosto («La compañía espera en un campo de trigo», el 17 de agosto), el rastrojo
     * de septiembre a noviembre.
     */
    internal fun campo(m: Momento): Campo {
        if (EscenaNapoleon.nieve(m) >= 0.5f) return Campo.NIEVE
        val f = fondo(m); val mes = mesDia(m).substring(0, 2).toInt()
        return when {
            f == Fondo.PUEBLO -> Campo.VINA
            f == Fondo.POLONIA || (f == Fondo.NIEMEN && mes in 5..9) -> Campo.ARENA
            mes in 5..6 -> Campo.TRIGO_VERDE
            mes in 7..8 -> Campo.TRIGO
            mes in 9..11 -> Campo.RASTROJO
            else -> Campo.HIERBA
        }
    }

    // ---------------------------------------------------------------- La compañía y el ejército

    /** Cuántos fuegos hace la compañía (los ranchos): [EscenaNapoleon.fuegos]; en el hospital y en el pueblo, ninguno de campaña: solo el suyo. */
    fun fuegosDeLaCompania(m: Momento): Int = if (sinCompania(m)) 1 else EscenaNapoleon.fuegos(m)

    /**
     * Si la compañía no está: en el hospital (está él solo, herido) y en el pueblo (ya no es soldado). Entonces no hay
     * fusiles en pabellón, ni águila, ni campamento de otras compañías: la base se queda con lo suyo.
     */
    fun sinCompania(m: Momento): Boolean = when (EscenaNapoleon.lugar(m)) {
        Lugar.HOSPITAL, Lugar.PUEBLO -> true
        Lugar.VIVAC, Lugar.RETIRADA, Lugar.MOSCU, Lugar.CUARTEL -> false
    }

    /**
     * Cuántos pabellones de la compañía se ven en la fila: dos por rancho (ocho hombres, dos pabellones de cuatro fusiles).
     * Con la compañía entera, la fila llega lejos; con pocos, «caben todos alrededor de un solo fuego» y quedan dos.
     *
     * **A todas horas** **[Diseño propio]**: el cuadro es «un alto al borde del camino» (`composicion.md`) y la base no se
     * mueve (la indicación del autor: «como la Ruta de la Seda»). De día es el alto de la marcha («hace alto con la
     * compañía en la cuneta… se sienta en la mochila»; «la instrucción en un prado», «de guardia… delante del
     * campamento», dice el cuento a esas horas), con el fuego de anoche en ceniza y el polvo de la columna en el camino;
     * de noche, el vivac. En el hospital y en el pueblo no está la compañía.
     */
    fun pabellones(m: Momento): Int = if (sinCompania(m)) 0 else 2 * fuegosDeLaCompania(m)

    /** El asta del águila, en la puerta de la tienda: mientras está la compañía (de marcha va con ella). */
    fun aguila(m: Momento): Boolean = pabellones(m) > 0

    /**
     * Cuántos fuegos de otras compañías y de otros cuerpos se ven encendidos en el campo, hasta el horizonte
     * **[Diseño propio]** («las hogueras de todo el ejército… hasta donde se ve, miles»): con la compañía entera, el ejército
     * entero acampa alrededor; con la compañía mermada, menos; con pocos, pocos. Solo de noche ([EscenaNapoleon.fuegosLejos]).
     */
    fun fuegosDelEjercito(m: Momento): Int {
        if (sinCompania(m) || !EscenaNapoleon.fuegosLejos(m)) return 0
        return when (EscenaNapoleon.cuantos(m)) { Compania.ENTERA -> 300; Compania.MERMADA -> 190; Compania.POCOS -> 70 }
    }

    /**
     * El fuego del rancho, de 0 a 1 ([EscenaNapoleon.fuego]); los días de fiesta, más grande de noche («se bebe por el
     * emperador alrededor de los fuegos»). En el pueblo es el fuego de los sarmientos, de día.
     */
    fun fuego(m: Momento): Float {
        val f = EscenaNapoleon.fuego(m)
        return if (EscenaNapoleon.fiesta(m) && EscenaNapoleon.fase(m) == Fase.VIVAC) 1f else f
    }

    /**
     * El polvo de la columna por el camino real, de 0 a 1: de marcha, de día («polvo, sed y moscas»), más en verano y con
     * viento; nada con lluvia, nieve o niebla, ni con la tierra nevada. Los días de marcha señalados (cruzar el Niemen),
     * más.
     */
    fun polvo(m: Momento): Float {
        if (EscenaNapoleon.fase(m) != Fase.MARCHA || sinCompania(m)) return 0f
        when (m.ficha.clima) { Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE, Clima.NIEBLA -> return 0f; else -> Unit }
        if (EscenaNapoleon.nieve(m) > 0.3f) return 0f
        val mes = mesDia(m).substring(0, 2).toInt()
        val base = when (mes) { 6, 7, 8 -> 1f; 5, 9 -> 0.75f; 4, 10 -> 0.5f; else -> 0.3f }
        return (base * (if (EscenaNapoleon.marcha(m)) 1.2f else 1f) + 0.2f * m.viento).coerceIn(0f, 1f)
    }

    /** El farol de la tienda de oficial, de 0 a 1: encendido al caer la tarde y hasta medianoche; luego, bajo. */
    fun farol(m: Momento): Float {
        val n = m.luces().noche
        if (n < 0.25f) return 0f
        val h = m.hora.toFloat()
        val k = if (h >= 17f || h < 0.5f) 1f else 0.35f
        return (k * ((n - 0.25f) / 0.35f).coerceIn(0f, 1f))
    }

    /**
     * Cuánto arde en el horizonte, de 0 a 1 (la tercera luz, roja): Moscú del 15 al 18 de septiembre
     * ([EscenaNapoleon.incendio]), Smolensk sus dos días; después, Moscú quemada humea sin luz.
     */
    fun incendio(m: Momento): Float = when {
        EscenaNapoleon.moscu(m) == Moscu.ARDE -> EscenaNapoleon.incendio(m)
        EscenaNapoleon.smolensk(m) -> if (EscenaNapoleon.diaNumero(m, "smolensk") == 1) 0.75f else 0.55f
        else -> 0f
    }

    /** El humo de la batalla en el horizonte (Borodino, las de Alemania y de Bélgica), de día. */
    fun batalla(m: Momento): Boolean = EscenaNapoleon.guerra(m) && m.hora >= 5.0 && m.hora < 20.0

    // ---------------------------------------------------------------- La luz

    /**
     * La luz de fuera: la de [EscenaNapoleon.luces] (el sol bajo del invierno, el aire dorado del verano) y, cuando arde
     * Moscú o Smolensk, el cielo y la bruma rojos («el cielo está rojo como de día»).
     */
    fun luces(m: Momento): Luces {
        val l = EscenaNapoleon.luces(m)
        val k = incendio(m); if (k <= 0.01f) return l
        val n = l.noche * k
        return Luces(
            alto = lerp(l.alto, Color(0xFF5A2C3A), 0.35f * n), medio = lerp(l.medio, Color(0xFFB0503A), 0.4f * n), bajo = lerp(l.bajo, Color(0xFFE07A3E), 0.55f * n),
            ambiente = lerp(l.ambiente, Color(0xFFC08A86), 0.3f * n), bruma = lerp(l.bruma, Color(0xFF9A4C40), 0.45f * n), brumaFuerza = l.brumaFuerza,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas * (1f - 0.7f * k), noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    // ---------------------------------------------------------------- El cuadro

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaVivac(m)
        with(VivacLejos) { cielo(v); lejos(v) }
        with(VivacLuz) { velo(v, 0.5f, 70f, 132f) }
        with(VivacCampo) { campo(v) }
        with(VivacLuz) { sombrasDelSuelo(v) }
        with(VivacCampo) { campamentos(v) }
        with(VivacLuz) { velo(v, 0.3f, 118f, 176f) }
        with(VivacLuz) { lucesDelSuelo(v) }
        with(VivacCerca) { base(v) }
        with(VivacLuz) { noche(v) }
        when (m.ficha.clima) {
            Clima.LLUVIA, Clima.TORMENTA, Clima.POLVO -> tiempoQuieto(v.l, m.ficha.clima, H, m.semilla("tiempo"))
            Clima.NIEVE, Clima.NIEBLA, Clima.DESPEJADO, Clima.NUBES, Clima.VIENTO, Clima.SIN_CLIMA -> Unit
        }
        grano(Azar(Semilla.de("napoleon", "grano", "vivac")), 0.6f + 0.4f * v.r.dia)
    }

    /**
     * Lo que el vivac provisional ponía en el horizonte de la retirada cuando la retirada pintaba el vivac
     * (`RetiradaNapoleon.pintar` con `vivac = true`). El vivac ya tiene su cuadro y nadie pinta así; se queda vacío para
     * que la retirada compile sin tocarla (se puede quitar cuando la retirada deje de llamarlo).
     */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.horizonte(v: VistaNapoleon) = Unit

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaVivac(m)
        with(VivacLejos) { vivo(v, reloj) }
        with(VivacCampo) { vivo(v, reloj) }
        with(VivacCerca) { vivo(v, reloj) }
        if (m.ficha.clima == Clima.NIEVE) with(VivacLuz) { copos(v, reloj) }
        tiempoVivo(v.l, m.ficha.clima, reloj, H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    /**
     * La luz del gato: la del momento, a la sombra si pisa bajo una nube y, de noche, cálida del lado del fuego del rancho
     * si está cerca de él.
     */
    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaVivac(m)
        val a = v.en(en); val k = a.calor(en).coerceIn(0f, 1f)
        val r = if (a.sombra > 0.3f) Rayo(v.r.dia, v.r.lat, v.r.cot, v.r.tan, v.r.ax, v.r.ay, v.r.fuerza * (1f - a.sombra), v.r.color, v.r.contraste * (1f - 0.5f * a.sombra)) else v.r
        return LuzGato(r, v.l.noche) { c -> val t = v.l.tono(c); val s = if (a.sombra > 0.01f) lerp(t, mult(t, v.r.color, 0.9f), 0.5f * a.sombra * v.r.dia) else t; if (k > 0.01f) lerp(s, a.deFuego(c), 0.55f * k) else s }
    }

    /** Lo plantado en el suelo del gato: el fuego del rancho (también apagado) y el tambor. */
    fun estorbos(m: Momento): List<Float> = listOf(VivacCerca.FUEGO.x)

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, VivacCerca.SUELO_DESDE, PISO_GATO, hasta = VivacCerca.SUELO_HASTA))
        add(Ancla(TipoAncla.MIRADOR, VivacCerca.MIRADOR.x, VivacCerca.MIRADOR.y))
        add(Ancla(TipoAncla.OBJETO, VivacCerca.GATO_MIRA_EL_OBJETO, PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, VivacCerca.ABRIGO, PISO_GATO))
        if (fuego(m) >= 0.45f) add(Ancla(TipoAncla.FUEGO, VivacCerca.FUEGO.x - 40f, PISO_GATO, hasta = VivacCerca.FUEGO.x))
    }

    /** Un azar fijo del vivac (lo que no cambia de un día a otro). */
    fun fijo(vararg partes: Any): Azar = Azar(Semilla.de("napoleon", "vivac", *partes))
}
