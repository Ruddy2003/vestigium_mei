package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * La paleta de Abismo: quince colores de materia y tres de luz, para los dos cuadros (el barco y el muelle).
 *
 * Manda el agua: el verde botella del golfo («el agua tiene el color de una botella»), que se hunde en el azul verdoso
 * de lo hondo y se aclara hasta la arena del fondo. La acompañan el cobre y el latón de los cascos (lo que la luz
 * enciende), el blanco y el azul de los barcos («pintada de blanco y azul como las barcas de la isla»), la madera de la
 * cubierta, la lona de los trajes y el dorado de las esponjas secas. Los acentos se repiten: el cobre del casco vuelve en
 * la bomba y en el farol; el azul de la regala, en la cúpula de San Nicolás; el rojo del manto del santo, en la marca de
 * la cuerda de vida, en los huevos de Pascua y en las rayas de la bandera; el verde de la hierba del fondo, en las
 * palmeras y los pinos del río; el dorado de las esponjas, en el marco del icono.
 *
 * Del tema de la app (un batiscafo de hoy, que no es el cuento: se anota en el informe) se quedan el latón
 * (`#C9A24A`), el verde agua (`#0F8A86`), el coral (`#C0563F`) y el verde vivo del agua de noche (`#5CF2C6`), que aquí es
 * la luz del mar que «se ilumina de verde cuando la tocas». Lo demás es **[Diseño propio]**.
 */
internal object ColorAbismo {
    // ---- Quince colores de materia.
    val MAR = Color(0xFF2A8680)                                                 // verde botella
    val MAR_HONDO = Color(0xFF15505E)
    val ARENA = Color(0xFFE4D3A4)                                               // el fondo de arena, la playa del río
    val HIERBA = Color(0xFF627F48)                                              // la hierba del fondo, las palmas, los pinos
    val COBRE = Color(0xFFB8673A)                                               // los cascos
    val LATON = Color(0xFFC9A24A)                                               // ventanillas, pernos, la bomba, el marco del icono
    val LONA = Color(0xFFC3B597)                                                // los trajes, los sacos, el toldo
    val PLOMO = Color(0xFF5C6271)                                               // las botas, el hierro, la cadena
    val MADERA = Color(0xFFA8845A)                                              // la cubierta, las tablas del muelle
    val BREA = Color(0xFF2D2725)                                                // juntas, goma, lo más oscuro
    val BLANCO = Color(0xFFEAE3D2)                                              // la pintura del casco, la iglesia (nunca blanco puro)
    val AZUL = Color(0xFF2E6086)                                                // la regala, la cúpula, las puertas
    val CANAMO = Color(0xFFBEA676)                                              // los cabos y la cuerda de las ristras
    val ESPONJA = Color(0xFFD7AB5E)                                             // la esponja seca, dorada
    val ROJO = Color(0xFFB2513C)                                                // el manto del santo, la marca de la cuerda, los huevos

    // ---- Las tres luces: la lumbre (la lamparilla, el farol, los cafés), el violeta de las sombras y el verde del agua de noche.
    val LUMBRE = Color(0xFFFFB25A)
    val SOMBRA = Color(0xFF6E70B2)
    val VERDE_VIVO = Color(0xFF5CF2C6)

    // ---- Mezclas con nombre.
    val MAR_CLARO = lerp(MAR, ARENA, 0.42f)                                     // el agua sobre la arena
    val ESPONJA_VIVA = lerp(BREA, HIERBA, 0.22f)                               // negra y viscosa
    val ESPONJA_OSC = lerp(ESPONJA, MADERA, 0.45f)
    val COBRE_OSC = lerp(COBRE, BREA, 0.45f)
    val VERDIN = lerp(COBRE, MAR, 0.55f)                                        // el cardenillo del cobre
    val MADERA_OSC = lerp(MADERA, BREA, 0.5f)
    val MADERA_GRIS = lerp(MADERA, LONA, 0.45f)                                // la madera lavada por el mar
    val LONA_MOJADA = lerp(LONA, BREA, 0.35f)
    val BLANCO_SUCIO = lerp(BLANCO, MADERA, 0.22f)
    val PALMA = lerp(HIERBA, MAR_HONDO, 0.25f)
    val ORO = lerp(LATON, Color(0xFFFFE6A0), 0.3f)
    val LUNA = lerp(SOMBRA, Color.White, 0.66f)
}

/**
 * Abismo: el mundo de Nikos, buzo esponjero de escafandra de Tarpon Springs, en el golfo de México, de 1910 a 1914.
 * **Manda el cuento y no el tema de la app** (el tema es un batiscafo de hoy con su arrecife; el cuento, un buzo griego
 * con casco de cobre en 1910): el cuadro pinta el cuento. Son **dos cuadros** en una misma escena, como Mongol,
 * Botánico y Napoleónico; cuál toca lo decide [lugar].
 *
 * 1. **El barco** ([BarcoAbismo], `EscenaAbismoBarco*.kt`), casi todo el año: la goleta fondeada sobre el banco. De pie
 *    en la popa, mirando hacia proa y hacia fuera por la borda de estribor, un poco desde arriba: **el agua es tan clara
 *    que se ve el fondo** («se ven los peces a diez brazas desde la borda»). Es lo que tiene este cuadro y no los otros dos
 *    a bordo (Pirata, de través; el bergantín de Botánico, a lo largo).
 * 2. **El muelle** ([MuelleAbismo], `EscenaAbismoMuelle*.kt`), del 18 de diciembre al 25 de enero (el estado `puerto`)
 *    y los días en que el cuento pone el barco en tierra aunque la ficha diga «barco» ([diaDeTierra]): el río de Tarpon
 *    Springs con la flota amarrada, «un bosque sin hojas» de palos, y el pueblo griego detrás.
 *
 * **El cuarto** de la casa de huéspedes (el estado `enfermo`, 36 días de 2030) todavía no tiene cuadro: se pinta, de
 * forma provisional, con el muelle de verano y sin barcos («Por la ventana se ve el río, y ningún barco: están todos en
 * el mar») ([provisional]).
 *
 * Sin personas: que Nikos está abajo se cuenta con la manguera y la cuerda de vida tensas, la bomba en marcha, el collar
 * de burbujas, el gancho vacío de su casco y la escala echada ([buzoAbajo]). La enfermedad y los muertos van fuera de
 * escena, como en el cuento.
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG6.kt`):
 * - **Dónde está** ([lugar], [cuadro], [provisional], [diaDeTierra]) y **la flota del muelle** ([flota]).
 * - **El horario** («Un día suyo»): [fase] (las inmersiones, la comida, limpiar y ensartar, el corro del farol, el sueño),
 *   [buzoAbajo], [manguera], [escala], [cascosColgados], [esponjasNegras], [mantas], [hornillo], [farol], la
 *   [lamparilla] del icono, que no se apaga nunca, los [farolesLejos] de los otros barcos y [lucesDelMuelle].
 * - **Las esponjas** ([esponjas], [carga], [ristras], [seVende]), con los cortes del cuento.
 * - **El mar** ([mar], [fondo], [costa]).
 * - **Los días señalados:** [fiesta] y [fiestaDeHoy] (las de la Iglesia, por el calendario juliano: [juliana]), la
 *   [cruz] de la Epifanía, la [vuelta] a puerto, el [luto], la [pascua], la vela de la [dormicion], la bandera del
 *   [cuatroDeJulio], los [cohetes] de fin de año, el barco de al lado parado ([paralisis]) y el [cometa].
 */
internal object EscenaAbismo : EscenaVida {
    override val vidas = listOf("buzo_esponjas")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está Nikos. [clave] es el estado del calendario que lo pone ahí; el barco es lo que queda cuando no se cumple ninguno. */
    internal enum class Lugar(val clave: String) { BARCO(""), MUELLE("puerto"), CUARTO("enfermo") }

    /** Qué cuadro se pinta. */
    internal enum class Cuadro { BARCO, MUELLE }

    /**
     * Los días en que el cuento pone el barco en el muelle aunque la ficha diga «barco» (no tienen estado):
     * - `lonja` (26 de junio): «Hoy el barco ha vuelto a Tarpon Springs, a mitad de año… Se descargan las esponjas»;
     * - `cuatro_julio` (4 de julio): «el barco todavía está en el muelle, antes de volver a salir»;
     * - `boda` (temporada 5): «Se casa con Eleni en la iglesia de San Nicolás».
     *
     * **[Diseño propio]**: la escena lee el día y esos días pinta el muelle; así cuenta lo mismo que el cuento sin tocar el
     * calendario (la congelación no deja cambiarlo). Los demás días de solo título (la foto, el giro, Eleni que llega o
     * aprende a ensartar, la casa, la cuna) cuentan el mar en sus párrafos, así que se quedan en el barco.
     */
    internal val DIAS_DE_TIERRA = listOf("lonja", "cuatro_julio", "boda")

    /** El día de zarpar (`zarpa`, 26 de enero) la mañana pasa en el muelle («Los barcos salen por el río uno detrás de otro»): hasta esta hora. */
    internal const val HORA_DE_ZARPAR = 11.0

    /** Si hoy el cuento pone el barco en el muelle aunque la ficha diga «barco»: [DIAS_DE_TIERRA], y la mañana de zarpar. */
    internal fun diaDeTierra(m: Momento): Boolean =
        m.estado == "" && (DIAS_DE_TIERRA.any { hoyEs(m, it) } || (hoyEs(m, "zarpa") && m.hora < HORA_DE_ZARPAR))

    /** Dónde está hoy, por el estado del calendario y los días de tierra: lo vigila `EscenaAbismoPrueba`. */
    internal fun lugar(m: Momento): Lugar = when (m.estado) {
        "puerto" -> Lugar.MUELLE
        "enfermo" -> Lugar.CUARTO
        else -> if (diaDeTierra(m)) Lugar.MUELLE else Lugar.BARCO
    }

    /** Qué cuadro pinta ese lugar: el cuarto, el del muelle (hasta que llegue el suyo). */
    internal fun cuadro(l: Lugar): Cuadro = when (l) {
        Lugar.BARCO -> Cuadro.BARCO
        Lugar.MUELLE, Lugar.CUARTO -> Cuadro.MUELLE
    }

    internal fun cuadro(m: Momento): Cuadro = cuadro(lugar(m))

    /** Si hoy está en un sitio que todavía no tiene cuadro (el cuarto): se pinta, de forma provisional, con el muelle. */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.BARCO, Lugar.MUELLE -> false
        Lugar.CUARTO -> true
    }

    /** Cuántos barcos hay en el muelle. */
    internal enum class Flota {
        /** Del 18 de diciembre al 25 de enero: «amarrados de dos en dos y de tres en tres… un bosque sin hojas». */
        LLENO,
        /** Los días de tierra de la temporada (la lonja, el 4 de julio, la boda): el suyo y unos pocos; los demás, en el mar. */
        POCOS,
        /** La mañana de zarpar: los barcos van saliendo por el río, uno detrás de otro. */
        SALIENDO,
        /** El cuarto: «ningún barco: están todos en el mar». */
        VACIO,
    }

    /** La flota del muelle de hoy; null en el barco. */
    internal fun flota(m: Momento): Flota? = when (lugar(m)) {
        Lugar.BARCO -> null
        Lugar.CUARTO -> Flota.VACIO
        Lugar.MUELLE -> when {
            hoyEs(m, "zarpa") -> Flota.SALIENDO
            m.estado == "puerto" -> Flota.LLENO
            else -> Flota.POCOS
        }
    }

    /** Si el muelle se pinta con la luz y el cielo del verano (las tormentas de la tarde): la estación de las lluvias. */
    internal fun veranoEnTierra(m: Momento): Boolean = m.ficha.estacion == Estacion.LLUVIAS

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /**
     * La fecha («MM-DD») del calendario juliano que corresponde a la fecha gregoriana [md]: trece días después, como dice
     * el calendario de Nikos («las fiestas de la Iglesia caen trece días después»). Así se comprueba que la Navidad (7 de
     * enero), San Basilio (14), la Epifanía (19), la Anunciación (7 de abril), la Dormición (28 de agosto) y San Nicolás
     * (19 de diciembre) son las de la Iglesia griega.
     */
    internal fun juliana(md: String): String = LocalDate.parse("2027-$md").plusDays(13).toString().substring(5)

    // ---------------------------------------------------------------- El horario de Nikos

    /**
     * La fase del día, por «Un día suyo» (igual en el barco, el muelle y el cuarto; cambia lo que se ve):
     * - [ALBA] (4:30 a 5:00): el bocado de antes de bajar, con el farol;
     * - [INMERSION] (5:00 a 11:00): «inmersiones con casco de cobre»;
     * - [COMIDA] (11:00 a 12:30 y 17:00 a 18:00): el hornillo de la proa;
     * - [ESPONJAS] (12:30 a 17:00): «limpiar y ensartar esponjas»;
     * - [CORRO] (18:00 a 20:00): «sentados en corro, con el farol en medio»;
     * - [SUENO] (20:00 a 4:30): «duerme en cubierta, envuelto en su manta… con el cielo entero encima».
     */
    internal enum class Fase { ALBA, INMERSION, COMIDA, ESPONJAS, CORRO, SUENO }

    internal fun fase(m: Momento): Fase = when (m.hacer) {
        Hacer.DORMIR -> Fase.SUENO
        Hacer.TRABAJO -> if (m.hora < 12.0) Fase.INMERSION else Fase.ESPONJAS
        Hacer.COMER -> if (m.hora < 5.0) Fase.ALBA else Fase.COMIDA
        Hacer.GENTE -> Fase.CORRO
        Hacer.CAMINO, Hacer.LIBRE, Hacer.CUIDAR, Hacer.REZAR, null -> Fase.COMIDA
    }

    /**
     * Los días que no se baja: las fiestas a bordo (Pascua, la Dormición: «Se limpian los trajes, se repara la red, se pesca
     * con sedal desde la borda»), la noche del cometa («Todos están en cubierta»), el día del huracán, el del buzo del barco
     * de al lado, y la tormenta **[Diseño propio]**: con la mar corta y blanca no se baja, todo va trincado.
     */
    internal fun diaSinBajar(m: Momento): Boolean =
        hoyEs(m, "pascua") || hoyEs(m, "dormicion") || hoyEs(m, "cometa") || hoyEs(m, "huracan") || hoyEs(m, "paralisis") ||
            mar(m) == Mar.TORMENTA || mar(m) == Mar.HURACAN

    /**
     * Si Nikos está abajo, en el fondo: en el barco, a la hora de las inmersiones, un día de bajar. Entonces la bomba
     * está en marcha, la manguera y la cuerda de vida bajan tensas al agua, sube el collar de burbujas y falta su casco en
     * el gancho. **[Diseño propio]**: «Baja una y otra vez durante la mañana»; el cuadro lo pinta abajo toda la mañana.
     */
    internal fun buzoAbajo(m: Momento): Boolean = lugar(m) == Lugar.BARCO && fase(m) == Fase.INMERSION && !diaSinBajar(m)

    /** Dónde está la manguera del aire: bajando al agua con el buzo, o adujada en cubierta junto a la bomba. */
    internal enum class Manguera { AL_AGUA, ADUJADA }

    internal fun manguera(m: Momento): Manguera = if (buzoAbajo(m)) Manguera.AL_AGUA else Manguera.ADUJADA

    /** La escala de cuerda: echada por la borda la mañana de inmersiones («baja por la escala de cuerda»); si no, recogida en la regala. */
    internal enum class Escala { ECHADA, RECOGIDA }

    internal fun escala(m: Momento): Escala = if (lugar(m) == Lugar.BARCO && fase(m) == Fase.INMERSION && !diaSinBajar(m)) Escala.ECHADA else Escala.RECOGIDA

    /** Los cascos de cobre colgados de sus ganchos (los tres buzos del barco): uno menos mientras Nikos está abajo. */
    internal fun cascosColgados(m: Momento): Int = if (buzoAbajo(m)) 2 else 3

    /**
     * Cuántas esponjas negras, recién sacadas, hay en cubierta por limpiar, de 0 a 1: suben con la red durante la
     * mañana («sube con la red llena de esponjas negras que chorrean»), se van limpiando por la tarde («se pisan para
     * quitarles la piel negra… las dejan como pan blanco») y al anochecer ya están todas ensartadas.
     */
    internal fun esponjasNegras(m: Momento): Float {
        if (lugar(m) != Lugar.BARCO) return 0f
        val h = m.hora.toFloat()
        return when (fase(m)) {
            Fase.INMERSION -> if (diaSinBajar(m)) 0f else (0.15f + 0.75f * (h - 5f) / 6f).coerceIn(0f, 0.9f)
            Fase.COMIDA -> if (h < 13f && !diaSinBajar(m)) 0.9f else 0f
            Fase.ESPONJAS -> if (diaSinBajar(m)) 0f else (1f - (h - 12.5f) / 4.5f).coerceIn(0.1f, 1f)
            Fase.ALBA, Fase.CORRO, Fase.SUENO -> 0f
        }
    }

    /** Las ristras a medio ensartar sobre la escotilla, con la aguja de madera: a la hora de limpiar y ensartar. */
    internal fun ensartando(m: Momento): Boolean = lugar(m) == Lugar.BARCO && fase(m) == Fase.ESPONJAS && esponjasNegras(m) > 0f

    /** Las mantas tendidas en la popa para dormir («envuelto en su manta, con la cabeza sobre un rollo de cuerda»). */
    internal fun mantas(m: Momento): Boolean = fase(m) == Fase.SUENO

    /** El hornillo de la proa, encendido a las horas de comer («una sopa de lentejas… de una olla común»). */
    internal fun hornillo(m: Momento): Boolean = lugar(m) == Lugar.BARCO && (fase(m) == Fase.COMIDA || fase(m) == Fase.ALBA)

    /**
     * El farol de cubierta: en el corro de la tarde («con el farol en medio»; «Michalis enciende el farol»), en el bocado de
     * antes del alba, y la noche de Pascua («el capitán encendió una vela del farol»).
     */
    internal fun farol(m: Momento): Boolean =
        lugar(m) == Lugar.BARCO && (fase(m) == Fase.CORRO || fase(m) == Fase.ALBA || (hoyEs(m, "pascua") && (m.hora >= 23.0 || m.hora < 1.0)))

    /** La lamparilla de aceite del icono de la popa, «que no se apaga nunca, ni con tormenta»: siempre (también en el muelle y en el cuarto). */
    @Suppress("UNUSED_PARAMETER")
    internal fun lamparilla(m: Momento): Boolean = true

    /** Los faroles de los otros barcos, lejos: «Se encienden los faroles de los otros barcos, lejos, uno aquí, otro allá». */
    internal fun farolesLejos(m: Momento): Boolean = lugar(m) == Lugar.BARCO && (m.hora >= 19.5 || m.hora < 5.0)

    /**
     * Lo encendido en el muelle, de 0 a 1 cada cosa: los faroles de los barcos amarrados («encienden sus faroles, uno tras
     * otro»), los cafés («hay ruido y humo hasta tarde»), las ventanas de San Nicolás (vísperas, las fiestas y las misas
     * del alba) y las lamparillas de los iconos de cada popa (siempre).
     */
    internal class LucesDelMuelle(val faroles: Float, val cafes: Float, val iglesia: Float, val lamparillas: Boolean)

    internal fun lucesDelMuelle(m: Momento): LucesDelMuelle {
        val h = m.hora
        val barcos = flota(m) != Flota.VACIO
        val faroles = if (!barcos) 0f else if (h >= 18.5 || h < 5.5) 1f else if (h >= 17.5 && h < 18.5) 0.5f else 0f
        val cafes = if (h >= 17.0 || h < 1.0) 1f else if (h >= 6.0 && h < 8.0) 0.6f else 0f
        val fiestaGrande = fiesta(m) || cruz(m)
        val iglesia = when {
            fiestaGrande && h >= 17.0 && h < 23.0 -> 1f
            (hoyEs(m, "navidad_vieja") || hoyEs(m, "epifania") || hoyEs(m, "pascua")) && h >= 5.0 && h < 7.5 -> 1f
            h >= 17.5 && h < 19.5 -> 0.7f                                            // vísperas
            else -> 0f
        }
        return LucesDelMuelle(faroles, cafes, iglesia, lamparilla(m))
    }

    // ---------------------------------------------------------------- Las esponjas

    /** Lo que lleva el barco, de 0 a 1 (el arco `esponjas`): «se vende en la lonja a mitad de año y al volver a puerto». En tierra no se lee. */
    internal fun esponjas(m: Momento): Float = if (lugar(m) == Lugar.BARCO) m.arco("esponjas").coerceIn(0f, 1f) else 0f

    /** Los tres tramos del cuento (`ENTORNOS` en `PiezasNikos.kt`): manda el primero que se cumple, con los rangos cerrados. */
    internal enum class Carga {
        /** 0 a 0.2: «las cuerdas del palo están casi vacías». */
        POCAS,
        /** 0.2 a 0.6: «Del palo cuelgan ya muchas cuerdas de esponjas secándose al sol, como racimos». */
        RACIMOS,
        /** 0.6 a 1: «esponjas en la bodega, en cubierta, colgadas de todas partes». */
        LLENO,
    }

    internal fun carga(m: Momento): Carga = esponjas(m).let { if (it <= 0.2f) Carga.POCAS else if (it <= 0.6f) Carga.RACIMOS else Carga.LLENO }

    /** Cuántas cuerdas de esponjas cuelgan a secar (de 1 a 14): crecen con el arco; con tormenta se recogen casi todas. */
    internal fun ristras(m: Momento): Int {
        if (lugar(m) != Lugar.BARCO) return 0
        val n = 1 + (esponjas(m) * 13f).roundToInt()
        return if (mar(m) == Mar.TORMENTA || mar(m) == Mar.HURACAN) minOf(n, 2) else n
    }

    /** El día en que se venden las esponjas y el arco cae: la lonja de junio y la vuelta a puerto en diciembre. */
    internal fun seVende(m: Momento): Boolean = hoyEs(m, "lonja") || hoyEs(m, "vuelta")

    /** Si el barco llega al muelle cargado («cargados de esponjas hasta los palos»): el día de la vuelta y el de la lonja, aunque el arco ya diga 0. */
    internal fun cargadoEnElMuelle(m: Momento): Boolean = lugar(m) == Lugar.MUELLE && seVende(m)

    // ---------------------------------------------------------------- El mar

    /**
     * Cómo está el mar sobre el banco:
     * - [CALMA]: «quieto como un plato»: un día despejado de cada dos **[Diseño propio]**, y el día de la `calma`;
     * - [CLARA]: el agua clara con su rizo («el agua tiene el color de una botella»), el resto de los días buenos;
     * - [NORTE]: «Sopla el viento del norte, frío y seco, que pica el agua»; los nortes «enturbian el agua»;
     * - [TORMENTA]: «el viento levanta de golpe olas cortas y blancas»;
     * - [HURACAN]: el día del huracán de 1910 (cielo amarillo, «olas largas que venían de muy lejos»).
     */
    internal enum class Mar { CALMA, CLARA, NORTE, TORMENTA, HURACAN }

    internal fun mar(m: Momento): Mar = when {
        hoyEs(m, "huracan") -> Mar.HURACAN
        m.ficha.clima == Clima.TORMENTA -> Mar.TORMENTA
        m.ficha.clima == Clima.VIENTO -> Mar.NORTE
        hoyEs(m, "calma") -> Mar.CALMA
        m.ficha.clima == Clima.DESPEJADO && m.azar("mar").real() < 0.5f -> Mar.CALMA
        else -> Mar.CLARA
    }

    /**
     * Cuánto fondo se ve desde la borda, de 0 a 1: «el agua tan clara en los días buenos que se ven los peces a diez brazas
     * desde la borda. En los días malos no se ve ni la mano». Con nubes y con lluvia, menos (el cielo devuelto tapa); el día
     * del banco hondo («a veinticinco brazas… la luz es azul oscura»), la mitad. De noche lo apaga la luz, no esto.
     */
    internal fun fondo(m: Momento): Float {
        val base = when (mar(m)) {
            Mar.CALMA -> 1f
            Mar.CLARA -> when (m.ficha.clima) { Clima.NUBES -> 0.7f; Clima.LLUVIA -> 0.42f; else -> 0.86f }
            Mar.NORTE -> 0.1f
            Mar.TORMENTA -> 0f
            Mar.HURACAN -> 0f
        }
        val hondo = if (hoyEs(m, "hondo") || hoyEs(m, "hondo_otra_vez")) 0.5f else 1f
        return base * hondo
    }

    /** Lo que se ve en el horizonte del banco. */
    internal enum class Costa {
        /** «un mar llano y verde donde no se ve tierra»: lo de casi todos los días. */
        NINGUNA,
        /** «la costa baja de Florida, que es solo una raya de árboles», al este; y la «costa vacía» del banco nuevo. */
        RAYA,
        /** El día del huracán: «una ensenada de la costa, entre manglares… Amarran el barco a los árboles». */
        ENSENADA,
    }

    /** La costa de hoy **[Diseño propio]**: la raya de árboles uno de cada cuatro días (el cuento la da al azar), siempre el del banco nuevo. */
    internal fun costa(m: Momento): Costa = when {
        hoyEs(m, "huracan") -> Costa.ENSENADA
        hoyEs(m, "banco_nuevo") -> Costa.RAYA
        m.azar("costa").real() < 0.25f -> Costa.RAYA
        else -> Costa.NINGUNA
    }

    // ---------------------------------------------------------------- Los días señalados

    /** Las fiestas (clave `fiesta`): el empavesado azul y blanco, sin letras. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** Las fiestas de Nikos: las de la Iglesia griega van por el calendario juliano ([juliana]); la Pascua, la ortodoxa. */
    internal enum class Fiesta(val id: String, val juliana: Boolean) {
        NAVIDAD("navidad_vieja", true), SAN_BASILIO("san_basilio", true), EPIFANIA("epifania", true), ANUNCIACION("veinticinco_marzo", true),
        PASCUA("pascua", false), DORMICION("dormicion", true), SAN_NICOLAS("san_nicolas", true),
        ANIO_NUEVO("anio_civil", false), FIN_DE_ANIO("fin_anio", false), CUATRO_DE_JULIO("cuatro_julio", false), BODA("boda", false),
    }

    /** La fecha gregoriana de cada fiesta de la Iglesia que va por el juliano: el calendario la pone trece días después. */
    internal val GREGORIANA = mapOf(Fiesta.NAVIDAD to "12-25", Fiesta.SAN_BASILIO to "01-01", Fiesta.EPIFANIA to "01-06", Fiesta.ANUNCIACION to "03-25",
        Fiesta.DORMICION to "08-15", Fiesta.SAN_NICOLAS to "12-06")

    /** La fiesta de hoy, si la hay. */
    internal fun fiestaDeHoy(m: Momento): Fiesta? = Fiesta.entries.firstOrNull { hoyEs(m, it.id) }

    /** La Epifanía (clave `cruz`): «el pope tira una cruz de madera al agua»; coronas en el río, ramos y cirios en el muelle. */
    internal fun cruz(m: Momento): Boolean = m.con("cruz")

    /** La vuelta a puerto (clave `puerto`): «los barcos vuelven… cargados de esponjas hasta los palos». */
    internal fun vuelta(m: Momento): Boolean = m.con("puerto")

    /** El luto (el Titanic, el barco de Key West perdido): el gallardete a media asta y un paño negro en el icono **[Diseño propio]**. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** Pascua: los huevos rojos y las velas junto al icono; no se baja. */
    internal fun pascua(m: Momento): Boolean = hoyEs(m, "pascua")

    /** La Dormición: «se enciende una vela delante del icono de la popa». */
    internal fun dormicion(m: Momento): Boolean = hoyEs(m, "dormicion")

    /** El 4 de julio y el 25 de marzo: la bandera griega «junto a la americana» (sin letras: solo sus rayas y su azul). */
    internal fun cuatroDeJulio(m: Momento): Boolean = hoyEs(m, "cuatro_julio") || hoyEs(m, "veinticinco_marzo")

    /** Fin de año, de noche: cohetes y los barcos adornados con farolillos («los barcos amarrados se adornan con farolillos»). */
    internal fun cohetes(m: Momento): Boolean = hoyEs(m, "fin_anio") && (m.hora >= 22.0 || m.hora < 1.0)

    internal fun farolillos(m: Momento): Boolean = hoyEs(m, "fin_anio") || hoyEs(m, "anio_civil")

    /** El día del buzo paralizado: «en el barco de al lado»; el otro barco, cerca, con su bomba parada y su escala echada. */
    internal fun paralisis(m: Momento): Boolean = hoyEs(m, "paralisis")

    /**
     * El cometa Halley, de 0 a 1: entero la noche del 19 de mayo de 1910 («la cola del cometa cruza las estrellas como un
     * velo blanco»), y débil del 23 de abril (`twain`: «Todavía no se ve bien») al 31 de mayo de la temporada 1
     * **[Diseño propio]**: «que se ve cada noche desde el barco».
     */
    internal fun cometa(m: Momento): Float = when {
        hoyEs(m, "cometa") -> 1f
        m.ficha.temporada == 1 && mesDia(m) in "04-23".."05-31" -> 0.35f
        else -> 0f
    }

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo, alto: Color = this.alto) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. En el golfo, el aire húmedo y tibio: la bruma del horizonte, salina y cálida, y la luz blanca que
     * «pega fuerte desde temprano»; de noche, el azul hondo del mar abierto. El día del huracán, el cielo del sur «de un
     * color amarillo raro». En el muelle de invierno, el aire seco del norte: el cielo más limpio y la luz más fría.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        return when (cuadro(m)) {
            Cuadro.BARCO -> {
                val amarillo = if (hoyEs(m, "huracan")) 0.55f * dia else 0f
                l.con(ambiente = lerp(lerp(l.ambiente, Color(0xFFFFF2DC), 0.1f * dia), Color(0xFF92A0D2), 0.48f * n),
                    bruma = lerp(lerp(lerp(l.bruma, Color(0xFFE8E2C8), 0.3f * dia), Color(0xFFD8C27A), amarillo), Color(0xFF4E5C92), 0.5f * n),
                    bajo = lerp(lerp(l.bajo, Color(0xFFF4E6C0), 0.2f * dia), Color(0xFFE6C468), amarillo),
                    medio = lerp(l.medio, Color(0xFFD9C98E), 0.6f * amarillo))
            }
            Cuadro.MUELLE -> {
                val frio = if (veranoEnTierra(m)) 0f else 1f
                l.con(ambiente = lerp(lerp(l.ambiente, Color(0xFFF2F0E6), 0.12f * dia * frio), Color(0xFF969CD0), 0.46f * n),
                    bruma = lerp(lerp(l.bruma, lerp(Color(0xFFE6DCC0), Color(0xFFDCE2E0), frio), 0.3f * dia), Color(0xFF56609A), 0.5f * n),
                    alto = lerp(l.alto, Color(0xFF6E9EC0), 0.12f * dia * frio))
            }
        }
    }

    /** La luz de la luna, para las manchas que deja y los filos que platea. */
    val LUZ_DE_LUNA = Color(0xFFBFD0F0)

    /**
     * De dónde viene la luz que tumba las sombras: de día, el sol (`Rayo`); de noche, **la luna**, que también da su
     * sombra, corta y azul, y su lado de luz fría (la noche a dos luces) **[Diseño propio]**, como en Botánico. Sin luna,
     * nada.
     */
    internal fun rayo(l: Luces): Rayo {
        val r = Rayo(l)
        if (r.fuerza > 0.05f || l.noche < 0.55f) return r
        val luna = ((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.nublado)
        if (luna <= 0.05f) return r
        val cruda = ((l.lunaX - 0.5f) * 2.6f).coerceIn(-1f, 1f); val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
        val cot = 1.25f
        return Rayo(dia = 0f, lat = lat, cot = cot, tan = 1f / cot, ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * abs(lat)) * cot,
            fuerza = 0.42f * luna, color = Color(0xFF6E72B4), contraste = 0.3f + 0.45f * luna)
    }

    /** Cuánto alumbra la luna, de 0 a 1: de noche y sin nubes. */
    internal fun luna(l: Luces): Float = (((l.estrellas - 0.3f) / 0.7f).coerceIn(0f, 1f) * (1f - l.nublado)) * l.noche

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.BARCO -> with(BarcoAbismo) { cuadro(m) }
            Lugar.MUELLE -> with(MuelleAbismo) { cuadro(m) }
            Lugar.CUARTO -> with(MuelleAbismo) { cuadro(m) }                    // hasta que llegue su cuadro propio
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.BARCO -> BarcoAbismo.seMueve(m)
        Lugar.MUELLE -> MuelleAbismo.seMueve(m)
        Lugar.CUARTO -> MuelleAbismo.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.BARCO -> with(BarcoAbismo) { vivo(m, reloj) }
            Lugar.MUELLE -> with(MuelleAbismo) { vivo(m, reloj) }
            Lugar.CUARTO -> with(MuelleAbismo) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (lugar(m)) {
        Lugar.BARCO -> BarcoAbismo.luzDelGato(m, en)
        Lugar.MUELLE -> MuelleAbismo.luzDelGato(m, en)
        Lugar.CUARTO -> MuelleAbismo.luzDelGato(m, en)
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.BARCO -> BarcoAbismo.estorbos(m)
        Lugar.MUELLE -> MuelleAbismo.estorbos(m)
        Lugar.CUARTO -> MuelleAbismo.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.BARCO -> BarcoAbismo.anclas(m)
        Lugar.MUELLE -> MuelleAbismo.anclas(m)
        Lugar.CUARTO -> MuelleAbismo.anclas(m)
    }
}
