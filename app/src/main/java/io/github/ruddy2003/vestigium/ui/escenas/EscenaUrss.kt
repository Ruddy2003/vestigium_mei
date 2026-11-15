package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla
import java.time.LocalDate
import kotlin.math.tan

/**
 * La paleta de Soviético: quince colores de materia y tres de luz, para la cocina de Liudmila y lo que se ve por su
 * ventana. Manda el papel de cartel del tema ([PAPEL]: el hule, los visillos, las tiras de las rendijas, la nieve al sol
 * se le acerca) y lo acompañan el verde agua del zócalo pintado al óleo ([OLEO]) y el gris del hormigón de los bloques
 * ([HORMIGON]). Los acentos se repiten de una cosa a otra: el rojo de cartel ([ROJO]) va en los cuadros del hule, en las
 * banderas, en la raya del plan, en la estrella del Kremlin, en los geranios y en la luz de la torre; el naranja de la
 * pantalla ([NARANJA]) vuelve en las mandarinas, en el chal y en las ventanas encendidas de enfrente; el latón del samovar
 * ([LATON]), en el portavasos, en el grifo y en la luz de las farolas; el azul de la olla esmaltada ([AZUL]), en la llama
 * del gas, en el tapete y en la luz del televisor.
 *
 * El papel, el negro y el rojo salen de los colores del tema y de su gato de pixel (papel `#F7F0DE`, tinta `#161414`,
 * rojo `#C1121F`); el oro, de su terciario. Lo demás es **[Diseño propio]**.
 */
internal object ColorUrss {
    // ---- Quince colores de materia.
    val PARED = Color(0xFFE0D6BC)                                              // la cal de arriba de las paredes
    val OLEO = Color(0xFF7D998B)                                               // el zócalo pintado al óleo, verde agua
    val LINOLEO = Color(0xFF9A5C43)                                            // el suelo de linóleo, rojo ladrillo
    val PAPEL = Color(0xFFF1E7CF)                                              // el papel de cartel: hule, visillo, tiras
    val ROJO = Color(0xFFB4262B)                                               // el rojo de cartel
    val TINTA = Color(0xFF2B2624)                                              // el negro de cartel: hierro, hollín
    val ESMALTE = Color(0xFFEDEAE1)                                            // la nevera, el fogón, la olla, el radiador
    val LATON = Color(0xFFC79A48)                                              // el samovar, el portavasos, el grifo
    val MADERA = Color(0xFF86603F)                                             // la mesa, el taburete, el marco, la alacena
    val HORMIGON = Color(0xFFB0ACA2)                                           // los bloques de paneles
    val LADRILLO = Color(0xFF995843)                                           // las chimeneas, los muros viejos
    val NIEVE = Color(0xFFEEEFEC)                                              // nunca blanco puro
    val NARANJA = Color(0xFFD3762F)                                            // la pantalla de la lámpara, las mandarinas
    val AZUL = Color(0xFF5A7497)                                               // la olla esmaltada, la llama, el tapete
    val VERDE = Color(0xFF5B7847)                                              // las plantas, los árboles, el pino

    // ---- Las tres luces: la lámpara, la pantalla del televisor y el violeta de las sombras.
    val LAMPARA = Color(0xFFFFB25E)
    val TELE = Color(0xFF9DBDFF)
    val SOMBRA = Color(0xFF7470B0)

    // ---- Lo demás son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val LUNA = lerp(SOMBRA, Color.White, 0.66f)
    val ESTUCO = lerp(PAPEL, LATON, 0.42f)                                     // la casa vieja de enfrente
    val ORO = lerp(LATON, NARANJA, 0.28f)                                      // los flecos, las mimosas
    val CAL = lerp(PARED, Color.White, 0.3f)                                   // el techo y los derrames de la ventana
    val PANEL = lerp(HORMIGON, NIEVE, 0.55f)                                   // los bloques blancos nuevos
    val HIERRO = lerp(TINTA, AZUL, 0.25f)                                      // el hierro pintado de negro
    val CROMO = lerp(ESMALTE, AZUL, 0.32f)                                     // el tirador de la nevera, los bordes
    val TIERRA = lerp(MADERA, TINTA, 0.35f)                                    // la tierra del patio, el barro
    val FAROLA = lerp(LAMPARA, LATON, 0.35f)                                   // la luz naranja de las farolas
    val VENTANA_LUZ = lerp(LAMPARA, PAPEL, 0.32f)                              // una ventana encendida de enfrente
}

/**
 * Soviético: el mundo de Liudmila, tejedora en una fábrica de Moscú, de 1975 a 1979. Es **un interior con ventana**
 * (`docs/diseno/ESCENAS.md`, sección 5): **su cocina**, mirada de pie desde la puerta, con la ventana al patio entre
 * bloques ([CocinaUrss], `EscenaUrssCocina*.kt`; lo de fuera, [PatioUrss], `EscenaUrssPatio.kt`).
 *
 * **[Diseño propio]**: el calendario pedía la nave de telares o la cola de una tienda; el cuento pasa sobre todo en casa
 * («una cocina de seis metros», la lámpara, el vaso de té, la tele) y casi todos los objetos del día son de casa. La
 * fábrica se ve lejos, por la ventana, con el cartel del plan ([plan]).
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG6.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar], [cuadro], [provisional]): en Moscú, la cocina; en el sanatorio del mar Negro (21 días de
 *   2028 y de 2030), la misma cocina sin ella, todavía provisional ([enElSanatorio]).
 * - **La casa** ([casa], [cajas]): la vieja de cinco pisos hasta la mudanza del 25 de junio de la temporada 3; desde
 *   entonces, el bloque blanco de nueve pisos. La cocina es la base que no cambia; lo de fuera, sí.
 * - **La nevera** ([nevera]), desde el 20 de marzo de la temporada 1; antes, la alacena y la leche entre los cristales.
 * - **El tiempo y el año:** la nieve con su regla propia ([nieve], [nieveMojada], [deshielo]), el frío ([frio]), el
 *   termómetro ([termometro]), la escarcha del cristal ([escarcha]), las tiras de papel ([tiras]), la calefacción
 *   ([calefaccion]), la pista de hielo ([pista]), las hojas ([hojas], [amarillas]), la pelusa de los álamos ([pelusa])
 *   y el barro ([barro]).
 * - **Los días señalados:** la fiesta, el desfile y sus banderas ([banderas]), el luto, el año nuevo, las mimosas, los
 *   claveles, la Pascua de la suegra, los melocotones del sanatorio y el cartel del plan ([plan]).
 * - **El horario** («Un día suyo»): la fase del día ([fase]) y lo que está encendido a cada hora ([lampara], [fogon],
 *   [samovar], [televisor], [ventanaEncendida], [farolas]).
 * - **La luz** ([luces], [lucesDentro], [solQueEntra]): el sol de invierno, que va bajo, y la tarde que oscurece antes.
 */
internal object EscenaUrss : EscenaVida {
    override val vidas = listOf("obrera_moscu")

    // ---------------------------------------------------------------- Dónde está

    /** Dónde está Liudmila: [clave] es el estado del calendario que la pone ahí. Moscú es lo que queda sin estado. */
    internal enum class Lugar(val clave: String) { MOSCU(""), SANATORIO("sanatorio") }

    /** Qué cuadro se pinta: por ahora uno solo, la cocina. */
    internal enum class Cuadro { COCINA }

    /** Dónde está hoy, por el estado del calendario: lo vigila `EscenaUrssPrueba`. Un estado que no se conoce, Moscú. */
    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.MOSCU

    internal fun cuadro(l: Lugar): Cuadro = when (l) {
        Lugar.MOSCU -> Cuadro.COCINA
        Lugar.SANATORIO -> Cuadro.COCINA
    }

    internal fun cuadro(m: Momento): Cuadro = cuadro(lugar(m))

    /**
     * Si hoy se pinta una variante provisional: los 21 días del sanatorio se pintan como la cocina sin ella (decisión 21
     * del plan: una estancia corta no lleva cuadro propio) hasta que se haga la playa de piedras con el edificio blanco.
     */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.MOSCU -> false
        Lugar.SANATORIO -> true
    }

    internal fun enElSanatorio(m: Momento): Boolean = lugar(m) == Lugar.SANATORIO

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)
    private fun mes(m: Momento): Int = m.ficha.dia.substring(5, 7).toInt()

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Qué día es hoy del día especial [id] (1 el primero), o 0 si hoy no lo es. */
    internal fun diaNumero(m: Momento, id: String): Int = m.ficha.especiales.firstOrNull { it.dia.id == id }?.numero ?: 0

    private fun diaDelAnio(md: String): Int = LocalDate.parse("2027-${if (md == "02-29") "02-28" else md}").dayOfYear

    /** El valor en el día [md] de una curva dada por puntos («MM-DD» → valor), en línea recta entre uno y otro, que da la vuelta al año. */
    internal fun curvaDelAnio(md: String, puntos: List<Pair<String, Float>>): Float {
        val d = diaDelAnio(md)
        val p = puntos.map { diaDelAnio(it.first) to it.second }
        val i = p.indexOfLast { it.first <= d }
        val a = if (i < 0) p.last().let { (it.first - 365) to it.second } else p[i]
        val b = if (i < 0) p.first() else p.getOrNull(i + 1) ?: p.first().let { (it.first + 365) to it.second }
        if (b.first == a.first) return a.second
        return a.second + (b.second - a.second) * ((d - a.first).toFloat() / (b.first - a.first))
    }

    // ---------------------------------------------------------------- La casa

    /** La casa vieja (hasta la mudanza) y el piso nuevo (desde la mudanza). */
    internal enum class Casa { VIEJA, NUEVA }

    /**
     * La casa de hoy: hasta la víspera de `mudanza` (25 de junio de la temporada 3), «un edificio viejo de cinco pisos sin
     * ascensor, con un patio de tierra, unos árboles, un banco»; desde ese día, «bloques blancos de nueve pisos… con
     * barro entre medias y árboles recién plantados que todavía son palos». Es la cuenta `hasta`/`desde` del cuento.
     */
    internal fun casa(m: Momento): Casa = if (paso(m, 3, "06-25")) Casa.NUEVA else Casa.VIEJA

    internal fun casaNueva(m: Momento): Boolean = casa(m) == Casa.NUEVA

    /**
     * Cuántas cajas de la mudanza hay en la cocina **[Diseño propio]**: desde que les dan el piso (`piso`, 15 de junio de
     * la temporada 3) se empieza a empaquetar en la casa vieja (de dos a cinco); el día de la mudanza llegan todas al
     * piso nuevo (siete) y se van vaciando hasta el 10 de julio. Solo en la temporada 3: después ya no hay cajas.
     */
    internal fun cajas(m: Momento): Int {
        if (m.ficha.temporada != 3) return 0
        val d = diaDelAnio(mesDia(m)); val piso = diaDelAnio("06-15"); val mudanza = diaDelAnio("06-25"); val fin = diaDelAnio("07-10")
        return when {
            d < piso -> 0
            d < mudanza -> 2 + (d - piso) * 4 / (mudanza - piso)
            d <= fin -> 7 - (d - mudanza) * 6 / (fin - mudanza)
            else -> 0
        }.coerceIn(0, 7)
    }

    /** El día de la mudanza y los dos siguientes, la lámpara cuelga sin su pantalla (todavía en una caja) **[Diseño propio]**. */
    internal fun bombillaDesnuda(m: Momento): Boolean = m.ficha.temporada == 3 && mesDia(m) in "06-25".."06-27"

    // ---------------------------------------------------------------- La nevera

    /** La nevera ZIL está en la cocina desde el día `nevera` (20 de marzo de la temporada 1); antes, la alacena. */
    internal fun nevera(m: Momento): Boolean = paso(m, 1, "03-20")

    /** Antes de la nevera, en invierno, «la leche entre las dos ventanas». */
    internal fun lecheEntreLosCristales(m: Momento): Boolean = !nevera(m) && (mes(m) >= 11 || mes(m) <= 3)

    // ---------------------------------------------------------------- La nieve y el frío

    /**
     * La nieve del suelo a lo largo del año **[Aproximación]**: el cuento ata «la primera nieve que no se derrite» al
     * segundo día de la Revolución (8 de noviembre) y el deshielo a la primavera («la nieve se derrite y las calles son
     * ríos de agua sucia», desde el 20 de marzo). Toda desde finales de noviembre hasta marzo; a mediados de abril, nada.
     */
    private val NIEVE_DEL_ANIO = listOf("03-01" to 1f, "03-20" to 0.86f, "04-02" to 0.42f, "04-14" to 0f, "11-07" to 0f, "11-08" to 0.88f, "11-26" to 1f)

    /**
     * Cuánta nieve cubre el patio, los tejados y los alféizares, de 0 a 1. **La regla** **[Aproximación]**, que no
     * contradice el cuento (lectura de la tanda, puntos 3.5 y 6):
     * - la curva [NIEVE_DEL_ANIO]: nada hasta el 7 de noviembre; desde el 8, la nieve que ya no se va; toda desde el 26;
     *   el deshielo desde el 20 de marzo, y nada desde el 14 de abril;
     * - si nieva: al menos 0.9 donde ya hay nieve; donde no (octubre, principios de noviembre, abril), una nevada fina
     *   que no cuaja: 0.35;
     * - si llueve sobre la nieve, se aplasta y se moja ([nieveMojada]): un 80 %;
     * - el día del frío que nadie recuerda (clave `nieve`, 3 de enero de la temporada 5), toda.
     */
    internal fun nieve(m: Momento): Float {
        if (m.con("nieve")) return 1f
        val base = curvaDelAnio(mesDia(m), NIEVE_DEL_ANIO)
        return when (m.ficha.clima) {
            Clima.NIEVE -> if (base > 0.05f) maxOf(base, 0.9f) else 0.35f
            Clima.LLUVIA, Clima.TORMENTA -> base * 0.8f
            else -> base
        }.coerceIn(0f, 1f)
    }

    /** Si la nieve está mojada y aplastada: llueve sobre ella (marzo, abril y noviembre). */
    internal fun nieveMojada(m: Momento): Boolean = nieve(m) > 0.2f && (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA)

    /** El deshielo: del 20 de marzo al 20 de abril, la nieve sucia, los charcos y los carámbanos que gotean. */
    internal fun deshielo(m: Momento): Boolean = mesDia(m) in "03-20".."04-20"

    /**
     * El frío de fuera, de 0 (verano) a 1 (el frío de enero) **[Aproximación]**: por meses, más en las noches
     * despejadas; el día del frío que nadie recuerda («casi cuarenta bajo cero»), 1 todo el día.
     */
    internal fun frio(m: Momento): Float {
        if (m.con("nieve")) return 1f
        val base = curvaDelAnio(mesDia(m), listOf("01-15" to 0.95f, "02-15" to 0.85f, "03-15" to 0.55f, "04-10" to 0.25f, "05-10" to 0f, "09-25" to 0f, "10-20" to 0.25f, "11-15" to 0.5f, "12-15" to 0.8f))
        val noche = m.hora >= 20.0 || m.hora < 8.0
        return (base + (if (noche && m.ficha.clima == Clima.DESPEJADO) 0.12f else 0f)).coerceIn(0f, 1f)
    }

    /** Lo que marca el termómetro del marco, de 0 (abajo del todo) a 1 (calor de julio). En invierno, «menos doce». */
    internal fun termometro(m: Momento): Float = if (m.con("nieve")) 0.04f else (0.86f - 0.62f * frio(m)).coerceIn(0.04f, 1f)

    /**
     * Las flores de escarcha en el cristal, de 0 a 1: con frío de verdad (de diciembre a febrero) por la mañana y en
     * las noches despejadas; el día del frío que nadie recuerda, todo el día y hasta media ventana.
     */
    internal fun escarcha(m: Momento): Float {
        if (m.con("nieve")) return 1f
        val f = frio(m); if (f < 0.72f) return 0f
        val h = m.hora
        val cuando = h < 11.0 || ((h >= 20.0 || h < 8.0) && m.ficha.clima == Clima.DESPEJADO) || h >= 22.0
        return if (cuando) ((f - 0.72f) / 0.26f).coerceIn(0.3f, 0.9f) else 0f
    }

    /** Las tiras de papel pegadas en las rendijas del marco: se ponen al llegar el frío y «se quitan» en primavera. */
    internal fun tiras(m: Momento): Boolean = mesDia(m) >= "10-20" || mesDia(m) < "04-10"

    /** La calefacción encendida: el radiador caliente, con su paño secándose («las calefacciones que todavía no se encienden» en otoño). */
    internal fun calefaccion(m: Momento): Boolean = mesDia(m) >= "10-12" || mesDia(m) < "04-25"

    /** La pista de hielo del patio: «los conserjes riegan los patios» en lo más frío, con la nieve ya asentada. */
    internal fun pista(m: Momento): Boolean = (mesDia(m) >= "12-10" || mesDia(m) < "03-06") && nieve(m) >= 0.8f && !nieveMojada(m)

    // ---------------------------------------------------------------- Las estaciones

    /** Cuántas hojas tienen los árboles, de 0 (pelados) a 1. */
    internal fun hojas(m: Momento): Float = curvaDelAnio(mesDia(m), listOf("04-24" to 0f, "05-06" to 0.45f, "05-20" to 1f, "09-24" to 1f, "10-10" to 0.62f, "10-24" to 0.12f, "11-02" to 0f))

    /** Cuánto amarillean, de 0 a 1: «los árboles se ponen amarillos y el barro vuelve». */
    internal fun amarillas(m: Momento): Float = curvaDelAnio(mesDia(m), listOf("08-28" to 0f, "09-22" to 0.55f, "10-06" to 1f, "11-10" to 1f, "11-11" to 0f))

    /** La pelusa de los álamos, en junio, que flota y se junta en los rincones; en el barrio nuevo, menos. */
    internal fun pelusa(m: Momento): Float {
        val k = curvaDelAnio(mesDia(m), listOf("06-01" to 0f, "06-06" to 1f, "06-20" to 1f, "06-28" to 0f))
        return k * (if (casaNueva(m)) 0.45f else 1f)
    }

    /**
     * El barro del patio, de 0 a 1: en el barrio nuevo siempre algo («barro entre medias»), más con lluvia; en el
     * deshielo y en las lluvias de otoño, en los dos.
     */
    internal fun barro(m: Momento): Float {
        val md = mesDia(m); val lluvia = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
        val estacion = if (deshielo(m) || md in "04-21".."05-05") 0.8f else if (md in "10-01".."11-07") (if (lluvia) 0.6f else 0.3f) else if (lluvia) 0.3f else 0f
        val nuevo = if (casaNueva(m)) (if (nieve(m) > 0.6f) 0.1f else 0.55f) else 0f
        return maxOf(estacion * (1f - nieve(m)), nuevo).coerceIn(0f, 1f)
    }

    /** La ventana abierta de par en par: en los días de calor de verano, de día, si no llueve. */
    internal fun ventanaAbierta(m: Momento): Boolean {
        val md = mesDia(m); val lluvia = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
        return md in "05-25".."08-25" && !lluvia && m.hora >= 9.0 && m.hora < 22.0 && !enElSanatorio(m)
    }

    /** El ventanillo abierto: en verano casi siempre; en invierno, mientras se cocina (sale el vaho). */
    internal fun ventanilloAbierto(m: Momento): Boolean = !calefaccion(m) || fogon(m) || ventilando(m)

    /** Por la mañana, después del desayuno, se deja el ventanillo abierto para ventilar, hasta las once **[Diseño propio]**. */
    internal fun ventilando(m: Momento): Boolean = m.hora >= 6.0 && m.hora < 11.0 && !enElSanatorio(m) && !malTiempo(m)

    // ---------------------------------------------------------------- Los días señalados

    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")
    internal fun desfile(m: Momento): Boolean = m.con("desfile")
    internal fun luto(m: Momento): Boolean = m.con("luto")
    internal fun anioNuevo(m: Momento): Boolean = hoyEs(m, "anio_nuevo")

    /** Dónde hay banderas rojas en el patio y en los bloques. */
    internal enum class Banderas { NINGUNA, FAROLAS, BALCONES, TODAS }

    /**
     * Las banderas, como el cuento: el 1 de mayo y el 7 de noviembre, en las farolas y en los balcones; el día después del
     * 1 de mayo, «los balcones llenos de banderas»; el 8 de noviembre, «las banderas mojadas colgando de las farolas»; el
     * día de la Constitución, «en las farolas y en los balcones»; y los sesenta años de la Revolución (5 de noviembre de
     * la temporada 3), «Moscú se llena de banderas».
     */
    internal fun banderas(m: Momento): Banderas = when {
        hoyEs(m, "sesenta") -> Banderas.TODAS
        diaNumero(m, "primero_mayo") == 1 || diaNumero(m, "revolucion") == 1 -> Banderas.TODAS
        diaNumero(m, "primero_mayo") == 2 -> Banderas.BALCONES
        diaNumero(m, "revolucion") == 2 -> Banderas.FAROLAS
        hoyEs(m, "constitucion") -> Banderas.TODAS
        else -> Banderas.NINGUNA
    }

    /** La mesa de fiesta: el año nuevo (las mandarinas, la botella y la rama de abeto) y el segundo día de los desfiles («la mesa grande»). */
    internal fun mesaDeFiesta(m: Momento): Boolean = anioNuevo(m) || diaNumero(m, "primero_mayo") == 2 || diaNumero(m, "revolucion") == 2

    /** La rama de abeto con sus bolas, en un tarro del alféizar, de la víspera del año nuevo a la Navidad vieja **[Diseño propio]**. */
    internal fun ramaDeAbeto(m: Momento): Boolean = mesDia(m) >= "12-29" || mesDia(m) <= "01-13"

    /** El 8 de marzo: «las mimosas en un vaso». */
    internal fun mimosas(m: Momento): Boolean = hoyEs(m, "ocho_marzo")

    /** El día de la Victoria: «claveles rojos». */
    internal fun claveles(m: Momento): Boolean = hoyEs(m, "victoria")

    /** La Pascua de la suegra: los huevos teñidos con piel de cebolla y el pan alto en su lata. */
    internal fun pascua(m: Momento): Boolean = hoyEs(m, "pascua_suegra")

    /** El día que vuelve del mar Negro, «con una caja de melocotones». */
    internal fun melocotones(m: Momento): Boolean = hoyEs(m, "vuelve_sanatorio")

    /**
     * Lo cumplido del plan del año en la fábrica, de 0 a 1 (el arco `plan`): la raya roja del cartel que se ve lejos, en
     * la fachada de la fábrica **[Diseño propio]** (el cuento pone el cartel a la entrada de la nave). Cae a 0 el 29 de
     * diciembre.
     */
    internal fun plan(m: Momento): Float = m.arco("plan").coerceIn(0f, 1f)

    // ---------------------------------------------------------------- El horario de Liudmila

    /**
     * La fase del día, por «Un día suyo» **[Diseño propio]**: el cuadro es su cocina.
     * - [MANANA] (de 6:00 a 7:00): «hierve el agua, hace la kasha»; la lámpara y el gas encendidos.
     * - [SOLA] (de 7:00 a 18:00: el metro, la nave, el comedor y las colas): la cocina sola, el desayuno recogido.
     * - [VUELTA] (de 18:00 a 20:00): vuelve con la bolsa de red; la sopa para mañana.
     * - [NOCHE] (de 20:00 a 23:00): «lee en la cocina, con la lámpara encendida y un vaso de té»; la tele en el cuarto.
     * - [SUENO] (de 23:00 a 6:00).
     */
    internal enum class Fase { MANANA, SOLA, VUELTA, NOCHE, SUENO }

    internal fun fase(m: Momento): Fase = when (m.hacer) {
        Hacer.DORMIR -> Fase.SUENO
        Hacer.CUIDAR -> if (m.hora < 12.0) Fase.MANANA else Fase.VUELTA
        Hacer.LIBRE -> Fase.NOCHE
        Hacer.CAMINO, Hacer.TRABAJO, Hacer.COMER, Hacer.GENTE, Hacer.REZAR, null -> Fase.SOLA
    }

    /** Si ella está en casa (no en el sanatorio y no en el metro, en la nave o en las colas). */
    internal fun enCasa(m: Momento): Boolean = !enElSanatorio(m) && fase(m) != Fase.SOLA && fase(m) != Fase.SUENO

    /** Si fuera ya está oscuro, o casi: entonces las luces se notan. */
    internal fun oscuro(m: Momento): Boolean = luces(m).noche > 0.22f

    /** Un día oscuro de verdad: nieve que cae, lluvia, tormenta o niebla. */
    internal fun diaOscuro(m: Momento): Boolean = malTiempo(m)

    /** Si nieva, llueve o hay niebla: la lámpara se enciende antes. */
    internal fun malTiempo(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA || m.ficha.clima == Clima.NIEBLA

    /**
     * La lámpara de pantalla: de 6:00 a 7:00 («se levanta a oscuras»); a la vuelta, si ya oscurece o hace mal tiempo; toda
     * la noche, hasta las 23:00. Por la tarde, si ya está oscuro, la enciende la suegra o Lena, que vuelven antes; y en los
     * días oscuros (nieve, lluvia, niebla) desde el mediodía, la suegra (después, Lena al volver de la escuela)
     * **[Diseño propio]**. En el sanatorio, solo de 20:00 a 23:00 (los de casa, para el té).
     */
    internal fun lampara(m: Momento): Boolean {
        if (enElSanatorio(m)) return fase(m) == Fase.NOCHE
        return when (fase(m)) {
            Fase.MANANA -> true
            Fase.VUELTA -> oscuro(m) || malTiempo(m) || calefaccion(m)
            Fase.NOCHE -> true
            Fase.SOLA -> (m.hora >= 15.5 && oscuro(m)) || (m.hora >= 12.0 && diaOscuro(m))
            Fase.SUENO -> false
        }
    }

    /** El gas: «la llama azul es lo primero del día» (la kasha) y, a la vuelta, la sopa para mañana. */
    internal fun fogon(m: Momento): Boolean = !enElSanatorio(m) && (fase(m) == Fase.MANANA || fase(m) == Fase.VUELTA)

    /** El samovar eléctrico, enchufado y echando vapor: por la mañana y a la hora del té de la noche. */
    internal fun samovar(m: Momento): Boolean = fase(m) == Fase.NOCHE || (fase(m) == Fase.MANANA && !enElSanatorio(m))

    /**
     * El hervidor todavía caliente, con su hilo de vapor, la media hora después de que ella se va al metro (de 7:00 a
     * 7:40): el desayuno recién recogido **[Diseño propio]**.
     */
    internal fun hervidorCaliente(m: Momento): Boolean = !enElSanatorio(m) && m.hacer == Hacer.CAMINO && m.hora < 8.0

    /** El vaso de té en su portavasos, sobre la mesa: con el samovar y a la vuelta; de día, boca abajo en el escurridor. */
    internal fun te(m: Momento): Boolean = samovar(m) || fase(m) == Fase.VUELTA

    /** El televisor del cuarto, encendido: «la televisión de la tarde», de 20:00 a 23:00 (en el sanatorio, los de casa). */
    internal fun televisor(m: Momento): Boolean = fase(m) == Fase.NOCHE

    /** La bolsa de red con la compra, colgada del picaporte desde que vuelve de las colas hasta la mañana. */
    internal fun bolsaColgada(m: Momento): Boolean = !enElSanatorio(m) && (fase(m) == Fase.VUELTA || fase(m) == Fase.NOCHE || fase(m) == Fase.SUENO)

    /** Las botas de fieltro secándose junto al radiador, cuando hace frío y están en casa (de día se las lleva). */
    internal fun botas(m: Momento): Boolean = !enElSanatorio(m) && frio(m) >= 0.4f && fase(m) != Fase.SOLA

    /** El abrigo y el chal en su gancho cuando ella está en casa; de día se los lleva (en el sanatorio, el gancho vacío). */
    internal fun abrigoColgado(m: Momento): Boolean = !enElSanatorio(m) && fase(m) != Fase.SOLA

    /** El visillo corrido a medias y las plantas con su botella de riego: la cocina mientras ella está en el mar Negro. */
    internal fun visilloCorrido(m: Momento): Boolean = enElSanatorio(m)

    /**
     * Si la ventana [i] del bloque de enfrente está encendida **[Diseño propio]**: cada una tiene su hora de encender al
     * caer la tarde (de 16:30 a 20:30, «las ventanas de los bloques se encienden una a una») y de apagar (de 22:00 a la
     * 1:00), y algunas se encienden también de madrugada (de 5:30 a 7:30); de un día a otro, la hora se corre un poco.
     * En invierno, la tarde oscurece antes ([crepusculo]) y se encienden antes.
     */
    internal fun ventanaEncendida(m: Momento, i: Int): Boolean {
        val fijo = Azar(Semilla.de("urss", "ventana", i)); val dia = m.azar("ventana", i)
        val enciende = fijo.entre(16.5f, 20.5f) + dia.entre(-0.4f, 0.4f) - 1.6f * crepusculo(m)
        val apaga = fijo.entre(22f, 25f) + dia.entre(-0.3f, 0.3f)
        val madruga = fijo.real() < 0.3f; val desvela = fijo.real() < 0.05f
        val h = m.hora.toFloat(); val hh = if (h < 12f) h + 24f else h
        return (hh >= enciende && hh < apaga) || (madruga && h >= 5.5f + 2f * fijo.real() * 0.5f && h < 7.6f) || desvela
    }

    /** Las farolas del patio: desde que oscurece (o antes, con mal tiempo y en las tardes de invierno) hasta que amanece. */
    internal fun farolas(m: Momento): Boolean = luces(m).noche > 0.25f || (malTiempo(m) && (m.hora >= 15.5 || m.hora < 8.0)) || crepusculo(m) > 0.3f

    // ---------------------------------------------------------------- Los objetos del día que ya están en la cocina

    /**
     * Los objetos del día que son también piezas de la cocina: el samovar de la mesa, el televisor del cuarto, la radio
     * de la pared, el abrigo del gancho, las botas del radiador y la bolsa del picaporte. El día que uno es el objeto, la
     * cocina no pinta el suyo si `ObjetosUrss` ya lo dibuja en su sitio ([ObjetosUrss.DIBUJADOS]); mientras el contrato
     * esté vacío, la cocina pinta el suyo y ese es el objeto del día.
     */
    internal val DE_LA_COCINA = setOf("samovar", "televisor", "radio", "abrigo", "botas", "bolsa")

    /** Si la cocina pinta hoy su [pieza] (no la pinta si es el objeto del día y el contrato de objetos ya la dibuja). */
    internal fun laCocinaPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in ObjetosUrss.DIBUJADOS)

    // ---------------------------------------------------------------- La luz

    /**
     * Cuánto baja el sol en invierno, de 0 a 1 (1: lo que dan las luces de siempre) **[Aproximación]**: en Moscú, en
     * diciembre, el sol no pasa de la altura de los tejados. Las luces de todas las escenas lo suben igual todo el año;
     * aquí se baja su altura, y con ella las sombras de fuera se alargan (también a mediodía).
     */
    internal fun alturaDelSol(m: Momento): Float = when (mes(m)) { 11, 12, 1 -> 0.45f; 10, 2 -> 0.6f; 3, 9 -> 0.8f; else -> 1f }

    /**
     * Cuánto se adelanta el anochecer en las tardes de invierno, de 0 a 1 **[Aproximación]**: el cuento dice que en
     * invierno «oscurece antes de salir de la fábrica» (a las 16:00), pero las luces de todas las escenas ponen el sol a
     * la misma hora todo el año. Aquí, de noviembre a febrero, la luz de las 16:00 a las 19:00 se acerca a la del
     * anochecer (no se cambia la hora).
     */
    internal fun crepusculo(m: Momento): Float {
        val k = when (mes(m)) { 12, 1 -> 1f; 11, 2 -> 0.7f; 10, 3 -> 0.3f; else -> 0f }
        val h = m.hora.toFloat()
        val tarde = if (h < 15.5f) 0f else if (h < 18f) (h - 15.5f) / 2.5f else if (h < 20.5f) 1f - (h - 18f) / 2.5f else 0f
        return (k * tarde).coerceIn(0f, 1f)
    }

    private fun Luces.mezcla(o: Luces, k: Float): Luces = Luces(
        alto = lerp(alto, o.alto, k), medio = lerp(medio, o.medio, k), bajo = lerp(bajo, o.bajo, k), ambiente = lerp(ambiente, o.ambiente, k),
        bruma = lerp(bruma, o.bruma, k), brumaFuerza = brumaFuerza + (o.brumaFuerza - brumaFuerza) * k, solX = solX, solAltura = solAltura,
        solColor = lerp(solColor, o.solColor, k), solFuerza = solFuerza * (1f - k), lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas + (o.estrellas - estrellas) * k, noche = noche + (o.noche - noche) * k, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera, la que se ve por la ventana. El sol de invierno va bajo ([alturaDelSol]) y la tarde de invierno
     * oscurece antes ([crepusculo]); con nieve, el aire es frío y limpio, el horizonte pálido, y de noche la nieve deja
     * ver el patio con su claridad azul.
     */
    internal fun luces(m: Momento): Luces {
        val l0 = m.luces(); val c = crepusculo(m)
        val l = if (c > 0.01f) l0.mezcla(lucesDe(m.ficha.luz(20.4), m.ficha.clima), 0.85f * c) else l0
        val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado); val frio = nieve(m)
        val sol = if (l.solAltura > 0f) l.solAltura * alturaDelSol(m) else l.solAltura
        return Luces(
            alto = l.alto, medio = lerp(l.medio, Color(0xFFD9E2E6), 0.18f * dia * frio), bajo = lerp(l.bajo, lerp(Color(0xFFF2E2BE), Color(0xFFF0E8DC), frio), 0.22f * dia),
            ambiente = lerp(l.ambiente, Color(0xFF9EA6DA), (0.16f + 0.24f * frio) * n), bruma = lerp(lerp(l.bruma, lerp(Color(0xFFE2D8BC), Color(0xFFDDE2E8), frio), 0.3f * dia), Color(0xFF56628F), 0.45f * n),
            brumaFuerza = l.brumaFuerza, solX = l.solX, solAltura = sol, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /**
     * La luz de fondo de la cocina: la del día de fuera, rebajada (una sola ventana, aunque grande). Con nieve fuera, la
     * cocina se llena de una claridad fría que sube del patio. De noche queda más oscura y más azul que fuera: así lo que
     * alumbran la lámpara, el gas y la tele destaca en cálido o en frío sobre la penumbra, sin quedar gris.
     */
    internal fun lucesDentro(m: Momento): Luces {
        val l = luces(m); val nv = nieve(m)
        val k = 0.6f - 0.16f * l.noche + 0.05f * nv * (1f - l.noche)
        val frio = lerp(Color.White, Color(0xFFDCE2F2), 0.25f * nv + 0.3f * l.nublado)
        val amb = Color((l.ambiente.red * k * frio.red).coerceIn(0f, 1f), (l.ambiente.green * k * frio.green).coerceIn(0f, 1f), (l.ambiente.blue * (k + 0.04f + 0.1f * l.noche) * frio.blue).coerceIn(0f, 1f))
        return Luces(
            alto = l.alto, medio = l.medio, bajo = l.bajo, ambiente = amb, bruma = lerp(Color(0xFF3A3448), amb, 0.6f), brumaFuerza = 0.4f,
            solX = l.solX, solAltura = l.solAltura, solColor = l.solColor, solFuerza = l.solFuerza, lunaX = l.lunaX, lunaAltura = l.lunaAltura,
            estrellas = l.estrellas, noche = l.noche, nublado = l.nublado, eclipse = 0f,
        )
    }

    /**
     * El sol (o la luna) que entra por la ventana: por cada unidad que avanza hacia quien mira, el rayo baja [baja] y se
     * corre [corre] de lado (negativo, hacia la izquierda). [fuerza] de 0 a 1 y su [color]; [deLuna], la luz fría.
     */
    internal class SolDeVentana(val baja: Float, val corre: Float, val fuerza: Float, val color: Color, val deLuna: Boolean = false)

    /**
     * El sol o la luna que entra a esta hora por la ventana, o null si no entra nada (cubierto, o de noche sin luna).
     * **[Aproximación]**, la misma que aprobó el autor en la capilla: la ventana da al mismo cielo que pinta `Luces`, y la
     * mancha cae como si la ventana mirara hacia el sol. Por la mañana el sol está a la derecha y bajo: la mancha se
     * tiende larga sobre la mesa y se corre a la izquierda; a mediodía cae corta, con toda la cruz del marco, junto a la
     * ventana; por la tarde, a la derecha, hacia la puerta. La altura es la de las luces de siempre, rebajada en
     * invierno solo a la mitad de lo que baja fuera, para que la mancha caiga en la cocina y no detrás de quien mira.
     */
    internal fun solQueEntra(m: Momento): SolDeVentana? {
        val l = m.luces(); val r = Rayo(l)
        if (r.fuerza > 0.05f && crepusculo(m) < 0.7f) {
            // A la cocina, el sol de invierno se le baja poco: así a mediodía la mancha llega al linóleo (como en la capilla).
            val invierno = 1f - 0.12f * (1f - alturaDelSol(m))
            val alto = (8.0 + 50.0 * (l.solAltura * invierno / 1.35f).coerceIn(0f, 1f))
            val baja = tan(Math.toRadians(alto)).toFloat().coerceIn(0.22f, 1.6f)
            val corre = (-(l.solX - 0.56f) * 1.6f).coerceIn(-0.85f, 0.85f)
            val fuerza = ((r.fuerza / 0.72f) * (1f - crepusculo(m))).coerceIn(0f, 1f)
            return SolDeVentana(baja, corre, fuerza, lerp(l.solColor, Color.White, 0.2f))
        }
        if (l.noche > 0.7f && l.nublado < 0.5f && l.lunaAltura > 0.05f) {
            val corre = (-(l.lunaX - 0.56f) * 1.4f).coerceIn(-0.7f, 0.7f)
            return SolDeVentana(0.62f, corre, 0.62f * (1f - l.nublado), ColorUrss.LUNA, deLuna = true)
        }
        return null
    }

    // ---------------------------------------------------------------- El cuadro

    override fun DrawScope.quieta(m: Momento) {
        when (cuadro(m)) {
            Cuadro.COCINA -> with(CocinaUrss) { cuadro(m) }
        }
    }

    override fun seMueve(m: Momento): Boolean = when (cuadro(m)) {
        Cuadro.COCINA -> CocinaUrss.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (cuadro(m)) {
            Cuadro.COCINA -> with(CocinaUrss) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (cuadro(m)) {
        Cuadro.COCINA -> CocinaUrss.luzDelGato(m, en)
    }

    override val tallaDelGato: Float get() = 1.2f

    override fun estorbos(m: Momento): List<Float> = when (cuadro(m)) {
        Cuadro.COCINA -> CocinaUrss.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (cuadro(m)) {
        Cuadro.COCINA -> CocinaUrss.anclas(m)
    }
}
