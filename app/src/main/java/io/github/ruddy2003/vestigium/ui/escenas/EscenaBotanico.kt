package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.roundToInt

/**
 * La paleta de Botánico: dieciséis colores de materia y dos de luz, para los dos cuadros (el bergantín y la selva).
 * Manda la madera: la teca lavada con arena de la cubierta y el roble de las mesas, los cajones y la galería. La
 * acompañan el verde hondo de la selva y el turquesa del mar. Los acentos se repiten de un cuadro a otro: el latón de la
 * bitácora vuelve en las hebillas de la prensa y en la lámpara de la galería; el rojo de la teja, en la cureña del
 * cañón y en las banderas; el blanco de la lona de las velas, en la cal de la casa y en el papel del herbario; el cuero
 * (el óxido del tema) en las correas de la prensa y en la escopeta; el verde escarabajo, en los frascos y en los
 * insectos; el naranja de las naranjas, en la mariposa del gato.
 *
 * El musgo, el óxido, el papel y el verde escarabajo salen de los colores del tema y del gato de pixel de Botánico
 * (collar musgo con una joya verde escarabajo, salacot crema); lo demás es **[Diseño propio]**.
 */
internal object ColorBotanico {
    // ---- Dieciséis colores de materia.
    val TECA = Color(0xFFC2A67E)                                                // la cubierta, lavada con arena
    val MADERA = Color(0xFF94663F)                                              // roble: mesas, cajones, la galería
    val BREA = Color(0xFF2E2824)                                                // la brea, el casco, las vigotas
    val LONA = Color(0xFFEDE4CD)                                                // las velas, el encerado, la cal, el papel
    val LATON = Color(0xFFC99E46)
    val TEJA = Color(0xFFB0502E)                                                // el tejado, la cureña, las banderas
    val CANAMO = Color(0xFFB79A66)                                              // la jarcia de labor, los cabos
    val HIERRO = Color(0xFF4C4A52)
    val CUERO = Color(0xFF8C4A22)                                               // el óxido del tema
    val SELVA = Color(0xFF2E5638)
    val HOJA = Color(0xFF7EA24E)
    val ESCARABAJO = Color(0xFF2E5E5A)
    val NARANJA = Color(0xFFDE842C)
    val MAR = Color(0xFF2C7E88)
    val ARENA = Color(0xFFE2D0A4)
    val ROCA = Color(0xFF7C7872)                                                // los morros, la lava, las peñas

    // ---- Las dos luces: la de la vela y las lámparas (y del farol), y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFB45E)
    val SOMBRA = Color(0xFF7C74B0)

    // ---- Lo demás son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val CAL = lerp(LONA, Color.White, 0.35f)
    val PAPEL = lerp(LONA, ARENA, 0.25f)
    val MAR_HONDO = lerp(MAR, BREA, 0.42f)
    val ORILLA = lerp(MAR, ARENA, 0.55f)
    val MADERA_OSC = lerp(MADERA, BREA, 0.5f)
    val CAOBA = lerp(MADERA, TEJA, 0.35f)                                       // la rueda y la bitácora, barnizadas
    val NIEVE = lerp(LONA, Color.White, 0.6f)
    val SELVA_OSC = lerp(SELVA, BREA, 0.45f)
    val HOJA_LUZ = lerp(HOJA, ARENA, 0.45f)
    val AMURADA = lerp(LONA, MADERA, 0.42f)                                     // la amurada por dentro, pintada de color piedra
    val LUNA = lerp(SOMBRA, Color.White, 0.62f)
}

/**
 * Botánico: el mundo del joven naturalista, a bordo de un bergantín de la marina que da la vuelta al mundo de 1832 a
 * 1836. Son **dos cuadros** en una misma escena, como Mongol y la Ruta de la Seda; cuál toca lo decide [lugar], que lee
 * el estado del calendario **y la temporada** (la misma clave `tierra` es la selva en la temporada 1, la pampa en la 2
 * y la montaña en la 3 y la 4).
 *
 * 1. **El bergantín** ([BarcoBotanico], `EscenaBotanicoBarco.kt`), unos 290 días al año: de pie en la popa, junto al
 *    timón, mirando hacia proa por la banda de estribor. La cubierta se aleja hacia el centro izquierda y el mar queda
 *    a la derecha, con la costa del tramo del viaje en el horizonte.
 * 2. **La selva** ([SelvaBotanico], `EscenaBotanicoSelva.kt`), la casa de Botafogo, del 25 de abril al 4 de julio de
 *    la temporada 1 (y de 2026, el avance): de pie en un extremo de la galería, mirando a lo largo de ella y en diagonal
 *    hacia la bahía.
 *
 * **Los demás lugares en tierra** (la pampa, la montaña, el cuarto de Valparaíso, las islas y la casa inglesa, de 2028
 * en adelante) tendrán su cuadro en las tandas chicas (`docs/plan_historia_y_escenas.md`, sección 8). Mientras llegan,
 * esos días se pinta **el bergantín fondeado frente a esa costa, sin la mesa de trabajo** (decisión 21, como Seda con
 * Samarcanda) **[Diseño propio]**. El cuadro propio entra en [quieta], [viva], [anclas], [estorbos] y [luzDelGato], en la
 * rama de su [Lugar].
 *
 * Las piezas que usan los dos cuadros (los cajones, la prensa, las cajas de frascos, la mesa, el bergantín lejano) y
 * las grandes del barco y de la selva están en [PiezasBotanico].
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG5.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar], [cuadro]) y, a bordo, **qué costa se ve** ([tramo], [costa]), si **navega o está
 *   fondeado** ([fondeado], [puerto]) y **cómo van las velas** ([velas]); el mar ([marejada]) y la tormenta ([tormenta],
 *   con el horizonte inclinado: [inclinacion]).
 * - **El frío:** la nieve en la jarcia y la cubierta ([nieve]) y la escarcha ([escarcha]).
 * - **Los días señalados:** la fiesta con el barco empavesado ([fiesta]), el terremoto ([terremoto], [ruinas]), el
 *   volcán de la costa ([volcan], [erupcion]), los días del envío ([diaDeEnvio]) y, en la selva, la mudanza a Botafogo
 *   con los libros al sol ([botafogo], [librosAlSol]), los escarabajos y el paraguas al revés ([escarabajos]) y el
 *   bergantín fondeado en la bahía ([bergantinEnLaBahia]).
 * - **Los cajones** ([cajones], [carga], [cuantosCajones]), con los cortes del cuento (0.3 y 0.7 del arco `cajones`).
 * - **El horario del naturalista** («Un día suyo»): la mesa de trabajo de cubierta ([mesaDeCubierta]) y la de la
 *   galería ([mesaDeLaGaleria]), la luz de la cabina que sale por la claraboya ([luzDeLaCabina]), el farol de popa
 *   ([farolDePopa]), la lámpara de la bitácora ([lamparaDeLaBitacora]), la red de arrastre ([red]), la cubierta mojada
 *   ([cubiertaMojada]); en la selva, la lámpara de la galería ([lamparaDeLaGaleria]), la red y la escopeta en casa
 *   ([redYEscopetaEnCasa]), las luciérnagas ([luciernagas]) y las mariposas ([mariposas]).
 */
internal object EscenaBotanico : EscenaVida {
    override val vidas = listOf("naturalista")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está el naturalista. Solo [BERGANTIN] y [SELVA] tienen cuadro propio; los demás, el bergantín fondeado frente a su costa. */
    internal enum class Lugar { BERGANTIN, SELVA, PAMPA, MONTANA, CUARTO, ISLAS, CASA }

    /** Qué cuadro se pinta. */
    internal enum class Cuadro { BARCO, SELVA }

    /**
     * Dónde está hoy, por el estado y la temporada: lo vigila `EscenaBotanicoPrueba`. «tierra» es la casa de Botafogo
     * en la temporada 1, la pampa en la 2 y la montaña en la 3 y la 4; «enfermo» es el cuarto de Valparaíso; «islas»,
     * las Galápagos; «casa», la casa inglesa.
     */
    internal fun lugar(m: Momento): Lugar = when (m.estado) {
        "tierra" -> when (m.ficha.temporada) { 1 -> Lugar.SELVA; 2 -> Lugar.PAMPA; else -> Lugar.MONTANA }
        "enfermo" -> Lugar.CUARTO
        "islas" -> Lugar.ISLAS
        "casa" -> Lugar.CASA
        else -> Lugar.BERGANTIN
    }

    /** Qué cuadro pinta ese lugar: la selva tiene el suyo; los demás, el del bergantín (hasta que llegue el suyo). */
    internal fun cuadro(l: Lugar): Cuadro = when (l) {
        Lugar.SELVA -> Cuadro.SELVA
        Lugar.BERGANTIN, Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> Cuadro.BARCO
    }

    internal fun cuadro(m: Momento): Cuadro = cuadro(lugar(m))

    /** Si hoy está en tierra en un sitio que todavía no tiene cuadro: se pinta el bergantín fondeado, sin la mesa. */
    internal fun provisional(m: Momento): Boolean = lugar(m).let { it != Lugar.BERGANTIN && it != Lugar.SELVA }

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    // ---------------------------------------------------------------- El viaje: la costa de cada tramo

    /**
     * Lo que se ve en el horizonte, a estribor. **[Aproximación]**: el calendario solo dice dónde está el bergantín los
     * días especiales; entre uno y otro se sigue el viaje real del Beagle según la memoria de esta tanda (sin comprobar
     * en ninguna fuente durante la sesión): las islas de Cabo Verde, la costa de Brasil, el Río de la Plata y la
     * Patagonia, Tierra del Fuego, la costa de Chile con los Andes, las Galápagos, Tahití, Nueva Zelanda, Australia, las
     * Keeling, el Cabo, Santa Elena y Falmouth.
     */
    internal enum class Costa {
        /** Solo mar, de horizonte a horizonte. */
        MAR_ABIERTO,
        /** Costa tropical con morros de piedra y selva (Bahía, Río, Pernambuco). */
        TROPICAL,
        /** Islas volcánicas: conos, coladas de lava, pocas palmeras (Cabo Verde, las Galápagos, Tahití, Mauricio, Santa Elena). */
        VOLCANICA,
        /** La Patagonia y el Plata: costa baja, barrancas, el agua parda. */
        PATAGONIA,
        /** Tierra del Fuego y el estrecho: montañas negras, bosques oscuros, nieve en las cumbres y glaciares. */
        FUEGO,
        /** La costa de Chile y del Perú, con la cordillera nevada detrás y su volcán. */
        ANDES,
        /** Un anillo de coral con palmeras, a flor de agua (las Keeling). */
        ATOLON,
        /** Colinas templadas, verdes o pardas (Nueva Zelanda, Australia, el Cabo). */
        COLINAS,
        /** La costa inglesa: acantilados verdes, campos y un pueblo con su iglesia (Falmouth). */
        INGLESA,
    }

    /** Un tramo del viaje: desde el día [desde] («MM-DD») de la temporada [temporada], la [costa] que se ve, y si se está [fondeado] frente a un [puerto]. */
    internal class Tramo(val temporada: Int, val desde: String, val costa: Costa, val fondeado: Boolean, val puerto: Boolean = false)

    /** El itinerario, por temporadas y en orden. Cada temporada empieza el 1 de enero. **[Aproximación]** (ver [Costa]). */
    internal val ITINERARIO: List<Tramo> = listOf(
        // Temporada 1, 1832: el Atlántico, Cabo Verde, el ecuador, Bahía, Río, el Plata, Punta Alta y Tierra del Fuego.
        Tramo(1, "01-01", Costa.MAR_ABIERTO, false), Tramo(1, "01-16", Costa.VOLCANICA, true, true), Tramo(1, "02-08", Costa.MAR_ABIERTO, false),
        Tramo(1, "02-28", Costa.TROPICAL, true, true), Tramo(1, "03-18", Costa.TROPICAL, false), Tramo(1, "04-04", Costa.TROPICAL, true, true),
        Tramo(1, "07-05", Costa.TROPICAL, false), Tramo(1, "07-11", Costa.MAR_ABIERTO, false), Tramo(1, "07-26", Costa.PATAGONIA, true, true),
        Tramo(1, "09-06", Costa.PATAGONIA, true), Tramo(1, "10-19", Costa.PATAGONIA, true, true), Tramo(1, "11-26", Costa.PATAGONIA, false),
        Tramo(1, "12-17", Costa.FUEGO, true), Tramo(1, "12-22", Costa.FUEGO, false),
        // Temporada 2, 1833: la misión de los fueguinos, las Malvinas, el Plata (la pampa), Puerto Deseado.
        Tramo(2, "01-01", Costa.FUEGO, false), Tramo(2, "01-15", Costa.FUEGO, true), Tramo(2, "02-26", Costa.PATAGONIA, true),
        Tramo(2, "04-28", Costa.PATAGONIA, true, true), Tramo(2, "12-06", Costa.PATAGONIA, false), Tramo(2, "12-23", Costa.PATAGONIA, true),
        // Temporada 3, 1834: San Julián, el estrecho, la misión vacía, el Pacífico, Valparaíso, las islas del sur.
        Tramo(3, "01-01", Costa.PATAGONIA, false), Tramo(3, "01-09", Costa.PATAGONIA, true), Tramo(3, "02-01", Costa.FUEGO, false),
        Tramo(3, "03-05", Costa.FUEGO, true), Tramo(3, "03-20", Costa.PATAGONIA, true), Tramo(3, "05-20", Costa.FUEGO, false),
        Tramo(3, "06-10", Costa.MAR_ABIERTO, false), Tramo(3, "06-26", Costa.ANDES, false), Tramo(3, "07-23", Costa.ANDES, true, true),
        Tramo(3, "11-11", Costa.ANDES, false),
        // Temporada 4, 1835: Chiloé, Valdivia y el terremoto, Concepción, Valparaíso, Copiapó y Lima, las Galápagos, Tahití, Nueva Zelanda.
        Tramo(4, "01-01", Costa.ANDES, false), Tramo(4, "02-08", Costa.ANDES, true, true), Tramo(4, "04-23", Costa.ANDES, false),
        Tramo(4, "07-05", Costa.ANDES, true, true), Tramo(4, "09-07", Costa.MAR_ABIERTO, false), Tramo(4, "09-15", Costa.VOLCANICA, true),
        Tramo(4, "10-20", Costa.MAR_ABIERTO, false), Tramo(4, "11-15", Costa.VOLCANICA, true, true), Tramo(4, "11-27", Costa.MAR_ABIERTO, false),
        Tramo(4, "12-19", Costa.COLINAS, true, true),
        // Temporada 5, 1836: Australia, el Índico, las Keeling, Mauricio, el Cabo, Santa Elena, Brasil otra vez y Falmouth.
        Tramo(5, "01-01", Costa.MAR_ABIERTO, false), Tramo(5, "01-12", Costa.COLINAS, true, true), Tramo(5, "03-06", Costa.MAR_ABIERTO, false),
        Tramo(5, "04-01", Costa.ATOLON, true), Tramo(5, "04-13", Costa.MAR_ABIERTO, false), Tramo(5, "04-29", Costa.VOLCANICA, true, true),
        Tramo(5, "05-10", Costa.MAR_ABIERTO, false), Tramo(5, "05-31", Costa.COLINAS, true, true), Tramo(5, "06-18", Costa.MAR_ABIERTO, false),
        Tramo(5, "07-08", Costa.VOLCANICA, true, true), Tramo(5, "07-14", Costa.MAR_ABIERTO, false), Tramo(5, "08-01", Costa.TROPICAL, true, true),
        Tramo(5, "08-17", Costa.MAR_ABIERTO, false), Tramo(5, "10-02", Costa.INGLESA, true, true),
    )

    /** El tramo del viaje de hoy, por la fecha del calendario (desde 2032 se repite el de la temporada 5). */
    internal fun tramo(m: Momento): Tramo {
        val t = m.ficha.temporada.coerceIn(1, 5); val md = mesDia(m)
        return ITINERARIO.last { it.temporada == t && it.desde <= md }
    }

    /**
     * La costa que se ve hoy. En los lugares sin cuadro propio, la de ese sitio, fondeado frente a él: la pampa, frente a
     * la costa baja del Plata; la montaña y el cuarto, en Valparaíso, con los Andes detrás; las islas, frente a las
     * Galápagos; la casa, en Falmouth. Desde 2032 la llegada a casa se mueve, y manda el estado sobre el itinerario.
     */
    internal fun costa(m: Momento): Costa = when (lugar(m)) {
        Lugar.BERGANTIN -> tramo(m).costa
        Lugar.SELVA -> Costa.TROPICAL
        Lugar.PAMPA -> Costa.PATAGONIA
        Lugar.MONTANA, Lugar.CUARTO -> Costa.ANDES
        Lugar.ISLAS -> Costa.VOLCANICA
        Lugar.CASA -> Costa.INGLESA
    }

    /** Si el bergantín está fondeado (en el itinerario, o porque él está en tierra). Navega los días de tormenta del mar abierto. */
    internal fun fondeado(m: Momento): Boolean = when (lugar(m)) {
        Lugar.BERGANTIN -> tramo(m).fondeado
        Lugar.SELVA, Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> true
    }

    /** Si frente al fondeadero hay un puerto con su pueblo (de noche, con sus luces). */
    internal fun puerto(m: Momento): Boolean = fondeado(m) && when (lugar(m)) {
        Lugar.BERGANTIN -> tramo(m).puerto
        Lugar.SELVA, Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.CASA -> true
        Lugar.ISLAS -> false
    }

    /** Lo propio de las aguas de un tramo (cómo cae la costa, dónde están los morros): igual todos los días del tramo. */
    internal fun aguas(m: Momento, que: String): Azar { val t = tramo(m); return Azar(Semilla.de("botanico", "tramo", t.temporada, t.desde, costa(m).name, que)) }

    // ---------------------------------------------------------------- El mar, las velas y la tormenta

    /** La tormenta: la clave del día (el cabo de Hornos, el estrecho) o el tiempo que hace. */
    internal fun tormenta(m: Momento): Boolean = m.con("tormenta") || m.ficha.clima == Clima.TORMENTA

    /** La calma chicha del Índico (temporada 5, 10 de marzo): el mar como un plato y las velas caídas. */
    internal fun calma(m: Momento): Boolean = hoyEs(m, "calor_indico")

    /** Cómo van las velas. */
    internal enum class Velas { LARGAS, CAIDAS, AFERRADAS }

    /** Largas si navega; caídas con la calma; aferradas fondeado o con tormenta («velas aferradas, todo trincado»). */
    internal fun velas(m: Momento): Velas = when {
        fondeado(m) || tormenta(m) -> Velas.AFERRADAS
        calma(m) -> Velas.CAIDAS
        else -> Velas.LARGAS
    }

    /** Cuánto se mueve el mar, de 0 (la calma) a 1 (la tormenta). Fondeado, poco. */
    internal fun marejada(m: Momento): Float = when {
        tormenta(m) -> 1f
        calma(m) -> 0f
        fondeado(m) -> (0.12f + 0.3f * m.viento).coerceIn(0f, 1f)
        else -> (0.32f + 0.55f * m.viento).coerceIn(0f, 1f)
    }

    /**
     * Cuántos grados se inclina el horizonte con la tormenta: «se mueve el mundo, no el barco» **[Diseño propio]**. Entre
     * 4 y 7 grados, a un lado o al otro según el día y la hora (el barco cabecea y da bandazos). Sin tormenta, 0.
     */
    internal fun inclinacion(m: Momento): Float {
        if (!tormenta(m)) return 0f
        val az = m.azar("bandazo", (m.hora * 3).toInt())
        val grados = az.entre(4f, 7f)
        return if (az.entero(2) == 0) grados else -grados
    }

    // ---------------------------------------------------------------- El frío

    /**
     * Cuánta nieve hay en la jarcia, la cubierta y los botes, de 0 a 1: toda el día que nieva (la ficha manda, también
     * en el trópico: ver el informe) y algo en los meses fríos del estrecho y de Tierra del Fuego (de mayo a agosto).
     */
    internal fun nieve(m: Momento): Float = when {
        m.ficha.clima == Clima.NIEVE -> 1f
        cuadro(m) == Cuadro.BARCO && costa(m) == Costa.FUEGO && mesDia(m) >= "05-15" && mesDia(m) <= "08-31" -> 0.45f
        else -> 0f
    }

    /** La escarcha en la regala, los cabos y la tapa de los cajones: con nieve, y las mañanas de Tierra del Fuego (todo el día que llegan). */
    internal fun escarcha(m: Momento): Boolean = cuadro(m) == Cuadro.BARCO && (nieve(m) > 0f || (costa(m) == Costa.FUEGO && (m.hora < 10.0 || m.con("nieve"))))

    // ---------------------------------------------------------------- Los días señalados

    /** El año nuevo, la Navidad y el fin de año: el barco empavesado, con banderas de señales de palo a palo (sin letras). */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** El terremoto de Valdivia y la llegada a Concepción (clave `terremoto`). */
    internal fun terremoto(m: Momento): Boolean = m.con("terremoto")

    /** «Llegan a Concepción: la ciudad, en ruinas»: el pueblo de la costa, caído, con su polvo. */
    internal fun ruinas(m: Momento): Boolean = hoyEs(m, "concepcion")

    /** Si la costa de hoy tiene su volcán: la de los Andes. */
    internal fun volcan(m: Momento): Boolean = cuadro(m) == Cuadro.BARCO && costa(m) == Costa.ANDES

    /**
     * Cuánto echa el volcán, de 0 a 1 (clave `fuego`, temporada 3, 1 de diciembre: «un volcán de la costa echando fuego
     * en la noche»): de día, una columna de humo (0.4); de noche, el fuego (1). Los demás días, nada.
     */
    internal fun erupcion(m: Momento): Float = if (!m.con("fuego")) 0f else if (m.hora >= 19.5 || m.hora < 5.5) 1f else 0.4f

    /** Los días en que se despachan los cajones a Inglaterra («se vacían de golpe»): el arco baja ese día. */
    internal fun diaDeEnvio(m: Momento): Boolean = hoyEs(m, "envio") || hoyEs(m, "envio_fosiles")

    /** El día que se muda a Botafogo (temporada 1, 25 de abril): el primer día de la selva. */
    internal fun botafogo(m: Momento): Boolean = lugar(m) == Lugar.SELVA && hoyEs(m, "botafogo")

    /**
     * Los libros, los papeles, las redes y los frascos mojados, secándose al sol en la galería el día de la mudanza: «el
     * bote se llenó de agua y casi pierde los libros»; los saca por la tarde **[Diseño propio]**: desde las 11.
     */
    internal fun librosAlSol(m: Momento): Boolean = botafogo(m) && m.hora >= 11.0 && m.hora < 19.0

    /** El día de los escarabajos (temporada 1, 20 de mayo): el paraguas abierto al revés y los escarabajos en la mesa. */
    internal fun escarabajos(m: Momento): Boolean = lugar(m) == Lugar.SELVA && hoyEs(m, "escarabajos")

    /**
     * Si desde la casa se ve el bergantín fondeado en la bahía («cuando está»). El día de la mudanza todavía está; luego
     * «vuelve al norte a medir» y regresa el 3 de junio **[Aproximación, sin verificar]**: son las fechas del Beagle en
     * 1832 según la memoria de la tanda.
     */
    internal fun bergantinEnLaBahia(m: Momento): Boolean = lugar(m) == Lugar.SELVA && (mesDia(m) == "04-25" || mesDia(m) >= "06-03")

    // ---------------------------------------------------------------- Los cajones

    /** Cuántos especímenes lleva, de 0 a 1 (el arco `cajones`): se vacía de golpe con cada envío. */
    internal fun cajones(m: Momento): Float = m.arco("cajones").coerceIn(0f, 1f)

    /** Los tres tramos del cuento (`ENTORNOS` en `PiezasNaturalista.kt`): manda el primero que se cumple, y los rangos son cerrados. */
    internal enum class Carga { POCOS, LLENANDO, LLENOS }

    internal fun carga(m: Momento): Carga = cajones(m).let { if (it <= 0.3f) Carga.POCOS else if (it <= 0.7f) Carga.LLENANDO else Carga.LLENOS }

    /** Cuántos cajones se pintan: uno o dos con el arco vacío, hasta nueve con todo lleno. */
    internal fun cuantosCajones(m: Momento): Int = 1 + (cajones(m) * 8f).roundToInt()

    // ---------------------------------------------------------------- El horario del naturalista

    /** Cómo está una mesa de trabajo. */
    internal enum class Mesa { TRABAJO, QUIETA, COMIDA, DIARIO, TAPADA, RECOGIDA }

    /**
     * La mesa de trabajo de cubierta **[Diseño propio]** (el cuento la pone en la cabina; el cuadro la saca a cubierta, a
     * la vista): en uso a las horas de trabajo; tapada con el encerado si llueve o nieva; recogida y trincada con
     * tormenta; quieta, con lo suyo ordenado, el resto del día. Null en los lugares sin cuadro propio (va sin mesa).
     */
    internal fun mesaDeCubierta(m: Momento): Mesa? = when {
        lugar(m) != Lugar.BERGANTIN -> null
        tormenta(m) -> Mesa.RECOGIDA
        m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.NIEVE -> Mesa.TAPADA
        m.hacer == Hacer.TRABAJO -> Mesa.TRABAJO
        else -> Mesa.QUIETA
    }

    /**
     * La mesa de la galería de Botafogo: casi vacía por la mañana, que él sube a colectar; con los pájaros y los insectos
     * a la hora de «disecar y etiquetar»; puesta a las horas de comer; con el diario y la lámpara de 19 a 22; recogida
     * de noche.
     */
    internal fun mesaDeLaGaleria(m: Momento): Mesa = when (m.hacer) {
        Hacer.TRABAJO -> if (m.hora < 13.0) Mesa.QUIETA else Mesa.TRABAJO
        Hacer.COMER -> Mesa.COMIDA
        Hacer.LIBRE -> Mesa.DIARIO
        else -> Mesa.RECOGIDA
    }

    /**
     * La luz de la cabina de popa, de 0 a 1, que sale de noche por la claraboya: la vela de escribir el diario (de 19 a
     * 22), la mesa del capitán al caer la tarde y la vela del alba; de madrugada, apagada.
     */
    internal fun luzDeLaCabina(m: Momento): Float = when (m.hacer) {
        Hacer.LIBRE -> 1f
        Hacer.COMER -> if (m.hora >= 17.5) 0.85f else if (m.hora < 7.0) 0.5f else 0.3f
        Hacer.TRABAJO -> 0.3f
        Hacer.DORMIR -> if (m.hora >= 22.0) 0.12f else 0f
        else -> 0.2f
    }

    /** El farol de popa: «el bergantín es una cáscara de luz en la oscuridad, con un farol en la popa». Encendido de noche. */
    internal fun farolDePopa(m: Momento): Boolean = cuadro(m) == Cuadro.BARCO && luces(m).noche > 0.3f

    /** La lámpara de la bitácora, para que el timonel vea la brújula: encendida de noche. */
    internal fun lamparaDeLaBitacora(m: Momento): Boolean = cuadro(m) == Cuadro.BARCO && luces(m).noche > 0.25f

    /** La red de arrastre. */
    internal enum class Red { ARRASTRANDO, SECANDO, GUARDADA }

    /**
     * La red de tela fina: «atada a un cabo y arrastrada detrás del barco» a las horas de trabajo, si navega y no hay
     * tormenta; tendida en la borda a secar el resto del día seco; guardada si llueve, con tormenta o en tierra.
     */
    internal fun red(m: Momento): Red {
        val seco = m.ficha.clima != Clima.LLUVIA && m.ficha.clima != Clima.NIEVE && !tormenta(m)
        return when {
            lugar(m) != Lugar.BERGANTIN -> Red.GUARDADA
            m.hacer == Hacer.TRABAJO && !fondeado(m) && seco -> Red.ARRASTRANDO
            seco && m.hacer != Hacer.DORMIR -> Red.SECANDO
            else -> Red.GUARDADA
        }
    }

    /** La cubierta mojada: con lluvia o tormenta, y mientras se baldea al alba (de 6:00 a 8:00). */
    internal fun cubiertaMojada(m: Momento): Boolean = m.ficha.clima == Clima.LLUVIA || tormenta(m) || (cuadro(m) == Cuadro.BARCO && m.hora >= 6.0 && m.hora < 8.0)

    /**
     * La lámpara de la mesa de la galería, de 0 a 1: «a la luz de una lámpara rodeada de insectos» de 19 a 22; a la hora
     * de comer del alba y de la tarde, cuando ya está oscuro; apagada el resto.
     */
    internal fun lamparaDeLaGaleria(m: Momento): Float = when (m.hacer) {
        Hacer.LIBRE -> 1f
        Hacer.COMER -> if (m.hora < 7.0 || m.hora >= 18.0) 0.8f else 0f
        else -> 0f
    }

    /** La red de mariposas y la escopeta, apoyadas en la pared: por la mañana se las lleva a la selva. */
    internal fun redYEscopetaEnCasa(m: Momento): Boolean = !(m.hacer == Hacer.TRABAJO && m.hora < 13.0)

    /** Las luciérnagas «entre los naranjos, cientos», de noche y sin lluvia. */
    internal fun luciernagas(m: Momento): Boolean = lugar(m) == Lugar.SELVA && (m.hora >= 19.5 || m.hora < 4.5) &&
        m.ficha.clima != Clima.LLUVIA && m.ficha.clima != Clima.TORMENTA && m.ficha.clima != Clima.NIEVE

    /** Las mariposas grandes y azules del jardín, de día y con buen tiempo. */
    internal fun mariposas(m: Momento): Boolean = lugar(m) == Lugar.SELVA && m.hora >= 8.0 && m.hora < 17.0 &&
        (m.ficha.clima == Clima.DESPEJADO || m.ficha.clima == Clima.NUBES || m.ficha.clima == Clima.VIENTO)

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. A bordo, el aire del mar: la bruma del horizonte, salina y azulada; más fría y gris frente a
     * Tierra del Fuego, los Andes y la costa inglesa, y más cálida y húmeda en el trópico. En la selva, el aire mojado
     * de Río: la bruma verdosa entre los planos y la luz dorada.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return when (cuadro(m)) {
            Cuadro.BARCO -> {
                val frio = when (costa(m)) { Costa.FUEGO -> 1f; Costa.ANDES, Costa.INGLESA, Costa.PATAGONIA -> 0.5f; else -> 0f }
                l.con(ambiente = lerp(lerp(l.ambiente, Color(0xFFE6EEF2), 0.16f * frio * dia), Color(0xFF969CD0), 0.45f * n),
                    bruma = lerp(lerp(l.bruma, lerp(Color(0xFFE8E4D0), Color(0xFFCAD6DC), frio), 0.3f * dia), Color(0xFF5A6496), 0.5f * n),
                    medio = lerp(l.medio, Color(0xFFD4E0E2), 0.1f * dia * frio))
            }
            Cuadro.SELVA -> l.con(ambiente = lerp(lerp(l.ambiente, Color(0xFFFFF0D4), 0.12f * dia), Color(0xFF9A98CE), 0.45f * n),
                bruma = lerp(lerp(l.bruma, Color(0xFFDCE6CC), 0.32f * dia), Color(0xFF5A6898), 0.5f * n),
                bajo = lerp(l.bajo, Color(0xFFF2E8C4), 0.2f * dia))
        }
    }

    /**
     * De dónde viene la luz que tumba las sombras: de día, el sol (`Rayo`); de noche, **la luna**, que también da su
     * sombra, corta y azul, y su lado de luz fría (la noche a dos luces, como la de Renacimiento: la lámpara o la
     * claraboya, cálidas por un lado; la luna, fría por el otro) **[Diseño propio]**. Sin luna (nublado, o antes de que
     * salga), nada.
     */
    internal fun rayo(l: Luces): Rayo {
        val r = Rayo(l)
        if (r.fuerza > 0.05f || l.noche < 0.55f) return r
        val luna = ((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.nublado)
        if (luna <= 0.05f) return r
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (kotlin.math.abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        val cot = 1.25f
        return Rayo(dia = 0f, lat = lat, cot = cot, tan = 1f / cot, ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * kotlin.math.abs(lat)) * cot,
            fuerza = 0.42f * luna, color = Color(0xFF6E72B4), contraste = 0.3f + 0.45f * luna)
    }

    /** El color de la luz de la luna, para las manchas que deja y los filos que platea. */
    val LUZ_DE_LUNA = Color(0xFFBFD0F0)

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.BERGANTIN -> with(BarcoBotanico) { cuadro(m) }
            Lugar.SELVA -> with(SelvaBotanico) { cuadro(m) }
            Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> with(BarcoBotanico) { cuadro(m) }   // hasta que llegue su cuadro propio
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.BERGANTIN -> BarcoBotanico.seMueve(m)
        Lugar.SELVA -> SelvaBotanico.seMueve(m)
        Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> BarcoBotanico.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.BERGANTIN -> with(BarcoBotanico) { vivo(m, reloj) }
            Lugar.SELVA -> with(SelvaBotanico) { vivo(m, reloj) }
            Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> with(BarcoBotanico) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (lugar(m)) {
        Lugar.BERGANTIN -> BarcoBotanico.luzDelGato(m, en)
        Lugar.SELVA -> SelvaBotanico.luzDelGato(m, en)
        Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> BarcoBotanico.luzDelGato(m, en)
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.BERGANTIN -> BarcoBotanico.estorbos(m)
        Lugar.SELVA -> SelvaBotanico.estorbos(m)
        Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> BarcoBotanico.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.BERGANTIN -> BarcoBotanico.anclas(m)
        Lugar.SELVA -> SelvaBotanico.anclas(m)
        Lugar.PAMPA, Lugar.MONTANA, Lugar.CUARTO, Lugar.ISLAS, Lugar.CASA -> BarcoBotanico.anclas(m)
    }
}
