package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.vidas.Clima
import java.time.LocalDate
import kotlin.math.roundToInt

/**
 * La paleta de Napoleónico: dieciséis colores de materia y dos de luz, para los dos cuadros (la retirada y el vivac).
 * Manda el blanco de la nieve, que nunca es blanco puro: cálido al sol y azul violeta a la sombra. Lo acompañan el negro
 * verdoso de los pinos y el azul del uniforme (los capotes de los oficiales, la caja del tambor, la carroza). Los acentos
 * se repiten de una cosa a otra: el rojo del pompón y de las charreteras vuelve en la tapicería de la carroza, en el
 * aro del tambor y en el fuego; el latón de la placa del chacó, en el tambor, en el candelabro y en el bronce del cañón;
 * el oro del marco del cuadro, en el águila; el blanco de las correas, en la lona de la tienda y del carro.
 *
 * El azul imperial, el carmesí y el oro salen de los colores del tema y del gato de pixel de Napoleónico (bicornio azul
 * tinta, collar carmesí con su dije de oro); el capote «gris o pardo» lo dice el cuento; lo demás es **[Diseño propio]**.
 */
internal object ColorNapoleon {
    // ---- Dieciséis colores de materia.
    val NIEVE = Color(0xFFF0EEE6)                                               // nunca blanco puro
    val PINO = Color(0xFF253A30)                                                // el negro verdoso del bosque
    val AZUL = Color(0xFF2F4577)                                                // el uniforme, la caja del tambor, la carroza
    val CAPOTE = Color(0xFF878073)                                              // «paño gris o pardo»
    val ROJO = Color(0xFFA32D2C)                                                // charreteras, pompón, tapicería, aros del tambor
    val LATON = Color(0xFFC39A48)                                               // placa del chacó, tambor, bronce del cañón
    val PLATA = Color(0xFFB9BCC2)                                               // bayonetas, candelabro, espejo
    val HIERRO = Color(0xFF3F3E46)                                              // llantas, cañones de los fusiles, la marmita
    val MADERA = Color(0xFF8C6642)                                              // ruedas, culatas, carros, tablas
    val CUERO = Color(0xFF3A2C26)                                               // cartucheras y botas, de cuero negro
    val CORREA = Color(0xFFE4DCC8)                                              // el ante blanco de los correajes
    val PIEL = Color(0xFF9C6C45)                                                // la piel de vaca de la mochila, con su pelo
    val BARRO = Color(0xFF6C5A4A)                                               // el camino antes de la nieve
    val HOJA = Color(0xFFAE8A42)                                                // la hierba seca y las hojas del otoño
    val CENIZA = Color(0xFF34302F)                                              // el hollín, lo quemado
    val LONA = Color(0xFFE0D6BE)                                                // la tienda de oficial, el toldo del carro, la manta

    // ---- Las dos luces: la del fuego (y de los faroles), y el violeta de las sombras.
    val LUMBRE = Color(0xFFFFA24A)
    val SOMBRA = Color(0xFF7470B0)

    // ---- Lo demás son mezclas de los de arriba, con nombre para no repetir la cuenta.
    val ORO = lerp(LATON, Color(0xFFFFE6A0), 0.32f)                           // el marco dorado, el águila
    val NIEVE_SOMBRA = lerp(NIEVE, SOMBRA, 0.42f)
    val NIEVE_SUCIA = lerp(NIEVE, BARRO, 0.38f)                                 // la nieve pisada del camino
    val PINO_OSC = lerp(PINO, CENIZA, 0.45f)
    val PINO_LUZ = lerp(PINO, HOJA, 0.38f)
    val MADERA_OSC = lerp(MADERA, CENIZA, 0.5f)
    val MADERA_GRIS = lerp(MADERA, CAPOTE, 0.55f)                              // la madera vieja, lavada por la lluvia
    val BRONCE = lerp(LATON, MADERA, 0.4f)
    val TERCIOPELO = lerp(ROJO, CENIZA, 0.35f)                                  // la tapicería de la carroza
    val GRANA = lerp(ROJO, LUMBRE, 0.25f)
    val LUNA = lerp(SOMBRA, Color.White, 0.64f)
}

/**
 * Napoleónico: el mundo de Jean-Baptiste, soldado de infantería de la campaña de Rusia, de 1812 a 1816. Son **dos
 * cuadros** en una misma escena, como Mongol y Botánico; cuál toca lo decide [lugar], que lee el estado del calendario.
 *
 * 1. **La retirada** ([RetiradaNapoleon], `EscenaNapoleonRetirada*.kt`), del 19 de octubre al 13 de diciembre de 1812
 *    (2026, el avance, y 2027): de pie en la cuneta, mirando camino adelante, hacia el oeste. El camino, largo, entre
 *    bosques negros; lo que el ejército va dejando; y un solo fuego. Es el primer cuadro con la nieve de protagonista.
 *    Se mueve con la marcha, en seis tramos ([tramo]), con una base que no cambia ([TramosRetirada]).
 * 2. **El vivac** ([VivacNapoleon], `EscenaNapoleonVivac*.kt`), el resto de la campaña: el alto de la compañía junto
 *    al camino, con su fuego y sus fusiles en pabellón. También se mueve con la marcha, en trece tramos de la campaña
 *    (Francia, Alemania, Polonia, el Niemen, el llano de Rusia, Smolensk, Borodino…), con una base que no cambia.
 *
 * **Los demás lugares:** Moscú se pinta de verdad como variante del vivac, con sus cúpulas en el horizonte y su
 * incendio ([moscu]). El cuartel, el hospital de Prusia y el pueblo de Borgoña todavía no tienen cuadro: se pintan
 * con el vivac, con su fondo y sin la compañía ([provisional]), como hizo Botánico con sus lugares en tierra; el
 * hospital y el pueblo tendrán el suyo en las tandas chicas.
 *
 * Las piezas que usan los dos (los fusiles en pabellón, la mochila, el chacó, el capote, el tambor, la marmita, la
 * hoguera, los carros, la carroza, el cañón, el cofre con el cuadro, el águila, el pino, la casa quemada y la tienda)
 * están en [PiezasNapoleon].
 *
 * Lo que cuenta, igual que el cuento (todo sale de la ficha; ver el comentario de su calendario en `AnioG5.kt`). Los
 * sucesos se cuentan «desde su día» con la misma cuenta que usa el cuento ([paso]):
 * - **Dónde está** ([lugar], [cuadro], [provisional]) y, en la retirada, **el tramo** ([tramo]: la salida de Moscú,
 *   Maloyaroslavets y el camino viejo, la nieve, Krasnoi, el Berezina y el final, hacia el Niemen).
 * - **La nieve** ([nieve], [helada], [nieveMojada]), con su regla propia, porque el calendario no tiene arco de nieve.
 * - **La compañía y sus fuegos** ([compania], [fuegos]), con los cortes del cuento (0.8 y 0.5 del arco `compania`).
 * - **El horario** («Un día suyo»): la fase del día ([fase]: la marcha, el alto de la comida, el vivac, el sueño), el
 *   fuego del alto ([fuego], [estadoDelFuego]), los fusiles en pabellón ([pabellon]), la marmita ([marmita]), la carne
 *   en la bayoneta ([carneEnLaBayoneta]), las ramas y mantas de dormir ([camas]), las huellas frescas ([huellas]) y lo
 *   que está encendido lejos ([fuegosLejos]).
 * - **Los días señalados:** la guerra ([guerra]), el luto ([luto]), la fiesta ([fiesta]), la marcha ([marcha]); en la
 *   retirada, lo abandonado ([abandono]), el pueblo que arde en Maloyaroslavets ([puebloArde]), los cañones de Krasnoi
 *   ([canonesEnLasColinas]), el humo del puente ([humoDelPuente]), el águila envuelta ([aguilaEnvuelta]) y las huellas
 *   del trineo ([trineo]); en el vivac, el fuego de Smolensk ([smolensk]) y el incendio de Moscú ([moscu]).
 */
internal object EscenaNapoleon : EscenaVida {
    override val vidas = listOf("granadero")

    // ---------------------------------------------------------------- Dónde está

    /**
     * Dónde está Jean-Baptiste. [clave] es el estado del calendario que lo pone ahí; el vivac es lo que queda cuando no
     * se cumple ninguno (la campaña). Solo [VIVAC] y [RETIRADA] tendrán cuadro propio.
     */
    internal enum class Lugar(val clave: String) { VIVAC(""), RETIRADA("retirada"), CUARTEL("cuartel"), MOSCU("moscu"), HOSPITAL("hospital"), PUEBLO("pueblo") }

    /** Qué cuadro se pinta. */
    internal enum class Cuadro { VIVAC, RETIRADA }

    /** Dónde está hoy, por el estado del calendario: lo vigila `EscenaNapoleonPrueba`. Un estado que no se conoce, el vivac. */
    internal fun lugar(m: Momento): Lugar = Lugar.entries.firstOrNull { it.clave == m.estado } ?: Lugar.VIVAC

    /** Qué cuadro pinta ese lugar: la retirada tiene el suyo; los demás, el del vivac (hasta que llegue el suyo). */
    internal fun cuadro(l: Lugar): Cuadro = when (l) {
        Lugar.RETIRADA -> Cuadro.RETIRADA
        Lugar.VIVAC, Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> Cuadro.VIVAC
    }

    internal fun cuadro(m: Momento): Cuadro = cuadro(lugar(m))

    /**
     * Si hoy está en un sitio que todavía no tiene cuadro (el cuartel, el hospital, el pueblo): se pinta, de forma
     * provisional, con el vivac. Moscú no cuenta: el vivac lo pinta de verdad, como variante suya.
     */
    internal fun provisional(m: Momento): Boolean = when (lugar(m)) {
        Lugar.VIVAC, Lugar.RETIRADA, Lugar.MOSCU -> false
        Lugar.CUARTEL, Lugar.HOSPITAL, Lugar.PUEBLO -> true
    }

    // ---------------------------------------------------------------- Cuentas de fechas

    private fun mesDia(m: Momento): String = m.ficha.dia.substring(5)

    /** Si ya pasó el día [fecha] («MM-DD») de la temporada [t]: la misma cuenta que hace el cuento con `desde` y `hasta`. */
    internal fun paso(m: Momento, t: Int, fecha: String): Boolean = m.ficha.temporada > t || (m.ficha.temporada == t && mesDia(m) >= fecha)

    internal fun hoyEs(m: Momento, id: String): Boolean = m.ficha.especiales.any { it.dia.id == id }

    /** Qué día es hoy del día especial [id] (1 el primero), o 0 si hoy no lo es. */
    internal fun diaNumero(m: Momento, id: String): Int = m.ficha.especiales.firstOrNull { it.dia.id == id }?.numero ?: 0

    /** El día del año de [md] («MM-DD») en un año sin bisiesto, de 1 a 365. */
    private fun diaDelAnio(md: String): Int = LocalDate.parse("2027-$md").dayOfYear

    /** El valor en el día [md] de una curva dada por puntos («MM-DD» → valor), en línea recta entre uno y otro, que da la vuelta al año. */
    private fun curvaDelAnio(md: String, puntos: List<Pair<String, Float>>): Float {
        val d = diaDelAnio(if (md == "02-29") "02-28" else md)
        val p = puntos.map { diaDelAnio(it.first) to it.second }
        val i = p.indexOfLast { it.first <= d }
        val a = if (i < 0) p.last().let { (it.first - 365) to it.second } else p[i]
        val b = if (i < 0) p.first() else p.getOrNull(i + 1) ?: p.first().let { (it.first + 365) to it.second }
        if (b.first == a.first) return a.second
        return a.second + (b.second - a.second) * ((d - a.first).toFloat() / (b.first - a.first))
    }

    // ---------------------------------------------------------------- La retirada: el tramo

    /**
     * Los seis tramos de la retirada, de Moscú hacia el Niemen, del 19 de octubre al 13 de diciembre. La retirada **se
     * mueve**: como en la Ruta de la Seda, hay una base que viaja con la compañía y no cambia (el alto alrededor del
     * fuego, el sitio del objeto del día, el suelo del gato, el camino en fuga y desde dónde se mira) y lo de alrededor
     * cambia de un tramo a otro: el marco de la izquierda, lo abandonado, la lejanía y la nieve ([TramosRetirada]).
     *
     * Cada tramo empieza el día de una frase del cuento (`PiezasJeanBaptiste`):
     * - **SALIDA**, desde `sale_moscu` (19 oct): «Salió cargado como una feria: carros llenos de cosas robadas, carrozas»;
     *   «un camino lleno de cosas tiradas: cofres abiertos, cuadros, candelabros, carrozas rotas, todo lo que se robó en
     *   Moscú»; «mirando hacia atrás las ruinas de Moscú». Sin nieve: «Por la mañana ya hace frío».
     * - **MALOYAROSLAVETS**, desde `maloyaroslavets` (24 oct): «un pueblo junto a un río… hasta que no quedó una casa en
     *   pie»; «pasan junto al pueblo, que arde»; y se vuelve «por el camino de Smolensk, el que ya está comido y quemado»:
     *   «Los pueblos del camino de vuelta están quemados, los que quemaron ellos mismos al venir».
     * - **NIEVE**, desde `nieve` (6 nov), y otra vez del 19 al 25 de noviembre: «empezó a nevar… y ya no paró. Al mediodía
     *   el camino estaba blanco»; «sembrado de muertos y de caballos, de carros volcados, de cañones abandonados, de fuegos
     *   apagados».
     * - **KRASNOI**, los cuatro días de `krasnoi` (15–18 nov): «los rusos esperaban en las colinas junto al camino, con
     *   cañones y cosacos… en la nieve, bajo la metralla».
     * - **BEREZINA**, los tres días de `berezina` (26–28 nov) y el de `lucien` (29 nov): el río «lleno de hielo suelto»,
     *   «dos puentes de madera», las «fogatas»; el 29, «quemar los puentes… mirando el humo del puente, del otro lado»
     *   (el cuento sigue ese día junto al río, por eso es de este tramo y no del siguiente).
     * - **FINAL**, del 30 de noviembre al 13 de diciembre, hasta la víspera de `niemen_vuelta`: «el frío de verdad, que no
     *   se parece a nada»; «la nieve, que le llega a las rodillas»; «Del ejército… quedan pedazos que andan».
     *
     * Las fechas de cada tramo son las de los días señalados del calendario (`AnioG5.kt`); la única que no lo es, el
     * final del Berezina y el principio del último tramo (el 30, el día después de `lucien`), sale de que el cuento siga
     * en el río el 29. Lo vigila `EscenaNapoleonPrueba`.
     */
    internal enum class Tramo { SALIDA, MALOYAROSLAVETS, NIEVE, KRASNOI, BEREZINA, FINAL }

    /** El tramo de un día de la retirada, por su fecha («MM-DD»): la misma cuenta que [tramo], para las pruebas. */
    internal fun tramoDe(md: String): Tramo = when {
        md < "10-24" -> Tramo.SALIDA
        md < "11-06" -> Tramo.MALOYAROSLAVETS
        md in "11-15".."11-18" -> Tramo.KRASNOI
        md in "11-26".."11-29" -> Tramo.BEREZINA
        md >= "11-30" -> Tramo.FINAL
        else -> Tramo.NIEVE
    }

    /** El tramo de hoy; null fuera de la retirada. Los días de Krasnoi y del Berezina mandan los días señalados mismos. */
    internal fun tramo(m: Momento): Tramo? = when {
        lugar(m) != Lugar.RETIRADA -> null
        hoyEs(m, "krasnoi") -> Tramo.KRASNOI
        hoyEs(m, "berezina") || hoyEs(m, "lucien") -> Tramo.BEREZINA
        else -> tramoDe(mesDia(m))
    }

    // ---------------------------------------------------------------- La nieve

    /**
     * La nieve de la retirada a lo largo de los días, desde el día después de que empieza a nevar **[Aproximación]**: el
     * calendario no tiene arco de nieve. Va a más («y ya no paró»; «la nieve, que le llega a las rodillas»): casi toda el
     * 7 de noviembre y toda desde el 20.
     */
    private val NIEVE_DE_LA_RETIRADA = listOf("11-07" to 0.84f, "11-20" to 1f, "12-31" to 1f)

    /**
     * La nieve del invierno en los demás lugares (el vivac de enero y de diciembre, el cuartel, el hospital de Prusia y
     * el pueblo) **[Aproximación]**: el cuento dice «la nieve sucia barrida contra las paredes» del cuartel, «Fuera nieva»
     * en el hospital, el Niemen «sobre el hielo» el 14 de diciembre y «la nieve de la Champaña» en febrero de 1814.
     */
    private val NIEVE_DEL_INVIERNO = listOf(
        "01-10" to 0.9f, "02-20" to 0.78f, "03-12" to 0.35f, "03-25" to 0f, "11-20" to 0f, "12-08" to 0.55f, "12-20" to 0.82f,
    )

    /**
     * Cuánta nieve cubre el suelo, de 0 a 1. **La regla de la retirada** **[Aproximación]**, que no contradice el cuento
     * (lectura de la tanda, puntos 3 y 6):
     * - antes del 6 de noviembre, nada (barro helado y hojas); si ese día nieva (el sorteo lo da en 2026 el 4 y el 5),
     *   una primera nevada fina que no cuaja en el camino: 0.35;
     * - el 6 de noviembre (clave `nieve`) cuaja a lo largo de la mañana: «Al mediodía el camino estaba blanco»;
     * - desde el 7, la curva [NIEVE_DE_LA_RETIRADA];
     * - si nieva, al menos 0.9; si llueve sobre la nieve, se aplasta y se moja ([nieveMojada]): un 85 %.
     *
     * En los demás lugares, la curva del invierno [NIEVE_DEL_INVIERNO] (la mitad en el pueblo de Borgoña); si nieva, al
     * menos 0.85 de noviembre a marzo y 0.4 el resto del año; la clave `nieve` fuera de la retirada (Montmirail), toda.
     */
    internal fun nieve(m: Momento): Float {
        val md = mesDia(m); val clima = m.ficha.clima
        return when (lugar(m)) {
            Lugar.RETIRADA -> {
                val base = when {
                    md < "11-06" -> 0f
                    md == "11-06" -> 0.86f * ((m.hora.toFloat() - 6f) / 6f).coerceIn(0.08f, 1f)
                    else -> curvaDelAnio(md, NIEVE_DE_LA_RETIRADA)
                }
                when (clima) {
                    Clima.NIEVE -> if (md < "11-06") 0.35f else if (md == "11-06") base else maxOf(base, 0.9f)
                    Clima.LLUVIA, Clima.TORMENTA -> base * 0.85f
                    else -> base
                }
            }
            Lugar.VIVAC, Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> {
                if (m.con("nieve")) return 1f
                val invierno = md >= "11-01" || md < "04-01"
                val base = curvaDelAnio(md, NIEVE_DEL_INVIERNO) * (if (lugar(m) == Lugar.PUEBLO) 0.5f else 1f)
                when (clima) {
                    Clima.NIEVE -> maxOf(base, if (invierno) 0.85f else 0.4f)
                    Clima.LLUVIA, Clima.TORMENTA -> base * 0.6f
                    else -> base
                }
            }
        }.coerceIn(0f, 1f)
    }

    /** Si la nieve está mojada y aplastada: llueve sobre ella (lo da el sorteo del clima en noviembre y diciembre). */
    internal fun nieveMojada(m: Momento): Boolean = nieve(m) > 0.2f && (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA)

    /**
     * La escarcha del suelo sin nieve, por la mañana: «Por la mañana ya hace frío» desde que salen de Moscú, y el «camino
     * de barro helado que se rompe bajo las botas» desde noviembre (entonces, todo el día). No con lluvia.
     */
    internal fun helada(m: Momento): Boolean {
        if (nieve(m) >= 0.5f) return false
        if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) return false
        val md = mesDia(m)
        val frio = md >= "10-15" || md < "04-01"
        return frio && (m.hora < 10.0 || md >= "11-01" || md < "03-01")
    }

    /**
     * Cuánto baja el sol en invierno, de 0 a 1 (1: lo que dan las luces de siempre) **[Aproximación]**: en Rusia, en
     * noviembre, el sol va bajo todo el día. Las luces de todas las escenas lo suben igual en todas las estaciones; aquí
     * se baja su altura, y con ella las sombras se alargan (también a mediodía).
     */
    internal fun alturaDelSol(m: Momento): Float {
        val mes = mesDia(m).substring(0, 2).toInt()
        return when (mes) { 11, 12, 1 -> 0.45f; 10, 2 -> 0.62f; 3, 9 -> 0.82f; else -> 1f }
    }

    // ---------------------------------------------------------------- La compañía y sus fuegos

    /** Cuántos quedan de la compañía, de 0 a 1 (el arco `compania`): baja en la retirada de Rusia y en Waterloo. */
    internal fun compania(m: Momento): Float = m.arco("compania").coerceIn(0f, 1f)

    /** Los tres tramos del cuento (`ENTORNOS` en `PiezasJeanBaptiste.kt`): manda el primero que se cumple, con los rangos cerrados. */
    internal enum class Compania { ENTERA, MERMADA, POCOS }

    internal fun cuantos(m: Momento): Compania = compania(m).let { if (it >= 0.8f) Compania.ENTERA else if (it >= 0.5f) Compania.MERMADA else Compania.POCOS }

    /**
     * Cuántos fuegos hace la compañía: «tantos fuegos como ranchos» con la compañía casi entera (unos ciento cuarenta
     * hombres en ranchos de ocho: diecisiete con todos, catorce como poco); con la compañía mermada, uno por rancho que
     * queda; con pocos, «caben todos alrededor de un solo fuego» **[Diseño propio]**: cuántos, entre esos extremos.
     */
    internal fun fuegos(m: Momento): Int {
        val c = compania(m)
        return when (cuantos(m)) {
            Compania.ENTERA -> maxOf(14, (c * 17f).roundToInt())
            Compania.MERMADA -> (c * 17f).roundToInt().coerceIn(8, 13)
            Compania.POCOS -> 1
        }
    }

    // ---------------------------------------------------------------- El horario de Jean-Baptiste

    /**
     * La fase del día, por «Un día suyo» **[Diseño propio]**: el cuadro es un alto al borde del camino.
     * - [MARCHA] (de 4:00 a 12:00 y de 13:00 a 18:00): la compañía ya se fue; queda el fuego de anoche y las huellas.
     * - [ALTO] (de 12:00 a 13:00, y el bocado de antes de salir, de 3:30 a 4:00): los fusiles en pabellón, la marmita.
     * - [VIVAC] (de 18:00 a 22:00): el fuego encendido, la marmita, se limpia el fusil y se cose.
     * - [SUENO] (de 22:00 a 3:30): el rescoldo, las ramas y las mantas de dormir.
     */
    internal enum class Fase { MARCHA, ALTO, VIVAC, SUENO }

    internal fun fase(m: Momento): Fase = when (m.hacer) {
        Hacer.CAMINO -> Fase.MARCHA
        Hacer.COMER -> Fase.ALTO
        Hacer.CUIDAR -> Fase.VIVAC
        Hacer.DORMIR -> Fase.SUENO
        Hacer.TRABAJO, Hacer.LIBRE, Hacer.GENTE, Hacer.REZAR, null -> Fase.MARCHA
    }

    /**
     * El fuego del alto, de 0 (ceniza fría) a 1: encendido del todo en el vivac («Se oye la leña»); a la hora de comer,
     * para la marmita; de noche se deja bajar («cerca del fuego que se apaga»); por la mañana queda el rescoldo del de
     * anoche, que humea y se apaga hacia las diez; por la tarde, la ceniza fría («junto a un fuego que se apagó»).
     */
    internal fun fuego(m: Momento): Float {
        val h = m.hora.toFloat()
        return when (fase(m)) {
            Fase.VIVAC -> 1f
            Fase.ALTO -> if (h < 6f) 0.62f else 0.72f
            Fase.SUENO -> (if (h >= 22f) 0.48f - 0.04f * (h - 22f) else 0.4f - 0.04f * h).coerceIn(0.2f, 0.48f)
            Fase.MARCHA -> if (h >= 4f && h < 10f) 0.3f * (1f - (h - 4f) / 6f) else 0f
        }
    }

    /** Cómo está el fuego del alto. */
    internal enum class Fuego { ENCENDIDO, RESCOLDO, APAGADO }

    internal fun estadoDelFuego(m: Momento): Fuego = fuego(m).let { if (it >= 0.5f) Fuego.ENCENDIDO else if (it > 0.02f) Fuego.RESCOLDO else Fuego.APAGADO }

    /** Los fusiles en pabellón: mientras la compañía está en el alto («los fusiles puestos en pabellón»). De marcha, se los llevan. */
    internal fun pabellon(m: Momento): Boolean = fase(m) != Fase.MARCHA

    /** Dónde está la marmita «para ocho»: colgada al fuego en el alto y en el vivac; junto a él mientras duermen; de marcha, se la llevan. */
    internal enum class Marmita { AL_FUEGO, AL_LADO, NINGUNA }

    internal fun marmita(m: Momento): Marmita = when (fase(m)) {
        Fase.ALTO, Fase.VIVAC -> Marmita.AL_FUEGO
        Fase.SUENO -> Marmita.AL_LADO
        Fase.MARCHA -> Marmita.NINGUNA
    }

    /** En la retirada, a las horas del fuego, la carne de caballo «asada en el fuego en la punta de la bayoneta». */
    internal fun carneEnLaBayoneta(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && (fase(m) == Fase.ALTO || fase(m) == Fase.VIVAC)

    /** Las ramas cortadas «para dormir encima» y las mantas extendidas: mientras duermen. */
    internal fun camas(m: Momento): Boolean = fase(m) == Fase.SUENO

    /**
     * Cuánto se notan las huellas frescas de la columna en el camino, de 0 a 1: de marcha, muchas (acaba de pasar); a la
     * hora del alto y de noche, las de la tarde; si nieva, la nieve nueva las va tapando.
     */
    internal fun huellas(m: Momento): Float {
        val base = when (fase(m)) { Fase.MARCHA -> 1f; Fase.ALTO -> 0.85f; Fase.VIVAC -> 0.7f; Fase.SUENO -> 0.55f }
        return if (m.ficha.clima == Clima.NIEVE) base * 0.55f else base
    }

    /**
     * Si los fuegos de lejos están encendidos (los de los otros grupos, y los del ejército en el vivac): al caer la tarde
     * y de noche, de 17:00 a 6:00 («cada grupo busca un sitio para el fuego»), y desde las 15:30 si hace mal tiempo. De
     * día, solo su humo.
     */
    internal fun fuegosLejos(m: Momento): Boolean = m.hora >= 17.0 || m.hora < 6.0 || (malTiempo(m) && m.hora >= 15.5)

    /** Si nieva, hay niebla o llueve: entonces los fuegos se encienden antes («el fuego se enciende antes y manda»). */
    internal fun malTiempo(m: Momento): Boolean = m.ficha.clima == Clima.NIEVE || m.ficha.clima == Clima.NIEBLA || m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA

    // ---------------------------------------------------------------- Los días señalados

    /** Las batallas (Maloyaroslavets en la retirada; Borodino y las de Alemania y Bélgica en el vivac): el humo de los cañones. */
    internal fun guerra(m: Momento): Boolean = m.con("guerra")

    /** La muerte de Lucien, el tifus de Maguncia y la noticia de Ney: un crespón negro en el asta del águila **[Diseño propio]**. */
    internal fun luto(m: Momento): Boolean = m.con("luto")

    /** El año nuevo, San Napoleón, la Navidad y la última noche del año: el fuego más grande y el águila sin envolver. */
    internal fun fiesta(m: Momento): Boolean = m.con("fiesta")

    /** Los días de marcha (cruzar el Niemen, salir de Moscú): la columna pasa cargada. */
    internal fun marcha(m: Momento): Boolean = m.con("marcha")

    /**
     * Cuánto ha dejado ya el ejército en el camino, de 0 a 1 **[Aproximación]**: «Salió cargado como una feria» el 19 de
     * octubre (ya hay una carroza rota y un carro); con la nieve se tira casi todo («un camino lleno de cosas tiradas»).
     */
    internal fun abandono(m: Momento): Float =
        if (lugar(m) != Lugar.RETIRADA) 0f else curvaDelAnio(mesDia(m), listOf("10-18" to 0.3f, "10-19" to 0.3f, "11-06" to 0.7f, "11-20" to 1f, "12-31" to 1f))

    /** El primer día de la retirada: los carros pasan cargados de lo de Moscú («Salió cargado como una feria»). */
    internal fun cargados(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && hoyEs(m, "sale_moscu")

    /** Maloyaroslavets: «pasan junto al pueblo, que arde». Las casas de madera del llano, en llamas, ese día. */
    internal fun puebloArde(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && hoyEs(m, "maloyaroslavets")

    /** Krasnoi: «los rusos esperaban en las colinas junto al camino, con cañones»: el humo de los cañones en las lomas, de día. */
    internal fun canonesEnLasColinas(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && hoyEs(m, "krasnoi") && m.hora >= 6.0 && m.hora < 18.0

    /** El día que queman los puentes del Berezina: «mirando el humo del puente, del otro lado». */
    internal fun humoDelPuente(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && hoyEs(m, "lucien")

    /** «Las águilas van envueltas en trapos» en la retirada; en la campaña, el águila se lleva descubierta. */
    internal fun aguilaEnvuelta(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA

    /**
     * «Se dice que el emperador se fue a París en un trineo» (6 de diciembre): en el camino, las huellas recién hechas de
     * los patines de un trineo **[Diseño propio]**: el cuento lo cuenta de oídas; el cuadro deja su rastro, no el trineo.
     */
    internal fun trineo(m: Momento): Boolean = lugar(m) == Lugar.RETIRADA && hoyEs(m, "trineo")

    /** Smolensk arde (17 y 18 de agosto de 1812): «casas de madera que arden», y de noche «la ciudad que arde». */
    internal fun smolensk(m: Momento): Boolean = hoyEs(m, "smolensk")

    /** Cómo está Moscú, vista desde el campo de fuera. */
    internal enum class Moscu { ENTERA, ARDE, QUEMADA }

    /**
     * Moscú, desde el 14 de septiembre: entera el primer día («cientos de cúpulas doradas… brillando al sol»), en llamas
     * del 15 al 18 (clave `fuego`; «el cielo está rojo como de día»), y luego «un campo de chimeneas negras». Null fuera
     * de Moscú. El 19 de octubre, ya en la retirada, «mirando hacia atrás las ruinas de Moscú».
     */
    internal fun moscu(m: Momento): Moscu? = when {
        lugar(m) != Lugar.MOSCU -> null
        hoyEs(m, "incendio") -> Moscu.ARDE
        hoyEs(m, "moscu") -> Moscu.ENTERA
        else -> Moscu.QUEMADA
    }

    /** Cuánto arde Moscú, de 0 a 1: empieza el primer día, todo el segundo y el tercero, y «el fuego empieza a apagarse» el cuarto. */
    internal fun incendio(m: Momento): Float = when (diaNumero(m, "incendio")) { 0 -> 0f; 1 -> 0.7f; 2, 3 -> 1f; else -> 0.45f }

    // ---------------------------------------------------------------- La luz

    private fun Luces.con(ambiente: Color, bruma: Color, medio: Color = this.medio, bajo: Color = this.bajo, solAltura: Float = this.solAltura) = Luces(
        alto = alto, medio = medio, bajo = bajo, ambiente = ambiente, bruma = bruma, brumaFuerza = brumaFuerza,
        solX = solX, solAltura = solAltura, solColor = solColor, solFuerza = solFuerza, lunaX = lunaX, lunaAltura = lunaAltura,
        estrellas = estrellas, noche = noche, nublado = nublado, eclipse = 0f,
    )

    /**
     * La luz de fuera. El sol del invierno va bajo ([alturaDelSol]); el aire frío deja el horizonte pálido y limpio, y
     * de noche la claridad de la nieve deja ver el llano, el camino y el bosque. En la campaña de verano, el aire dorado.
     */
    internal fun luces(m: Momento): Luces {
        val l = m.luces(); val dia = (1f - l.noche).coerceIn(0f, 1f); val n = l.noche * (1f - 0.3f * l.nublado)
        val frio = maxOf(nieve(m), if (lugar(m) == Lugar.RETIRADA) 0.55f else 0f)
        val sol = if (l.solAltura > 0f) l.solAltura * alturaDelSol(m) else l.solAltura
        return l.con(
            ambiente = lerp(l.ambiente, Color(0xFF9EA4DA), (0.18f + 0.22f * frio) * n),
            bruma = lerp(lerp(l.bruma, lerp(Color(0xFFE6DCBA), Color(0xFFE0E5EC), frio), 0.32f * dia), Color(0xFF5A6498), 0.5f * n),
            medio = lerp(l.medio, Color(0xFFD8E2EA), 0.16f * dia * frio),
            bajo = lerp(l.bajo, lerp(Color(0xFFF4E4B8), Color(0xFFF2E6D8), frio), 0.24f * dia),
            solAltura = sol,
        )
    }

    // ---------------------------------------------------------------- Los cuadros

    override fun DrawScope.quieta(m: Momento) {
        when (lugar(m)) {
            Lugar.RETIRADA -> with(RetiradaNapoleon) { cuadro(m) }
            Lugar.VIVAC -> with(VivacNapoleon) { cuadro(m) }
            Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> with(VivacNapoleon) { cuadro(m) }   // hasta que llegue su cuadro propio
        }
    }

    override fun seMueve(m: Momento): Boolean = when (lugar(m)) {
        Lugar.RETIRADA -> RetiradaNapoleon.seMueve(m)
        Lugar.VIVAC -> VivacNapoleon.seMueve(m)
        Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> VivacNapoleon.seMueve(m)
    }

    override fun DrawScope.viva(m: Momento, reloj: Float) {
        when (lugar(m)) {
            Lugar.RETIRADA -> with(RetiradaNapoleon) { vivo(m, reloj) }
            Lugar.VIVAC -> with(VivacNapoleon) { vivo(m, reloj) }
            Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> with(VivacNapoleon) { vivo(m, reloj) }
        }
    }

    // ---------------------------------------------------------------- El gato

    override fun luzDelGato(m: Momento, en: Offset): LuzGato = when (lugar(m)) {
        Lugar.RETIRADA -> RetiradaNapoleon.luzDelGato(m, en)
        Lugar.VIVAC -> VivacNapoleon.luzDelGato(m, en)
        Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> VivacNapoleon.luzDelGato(m, en)
    }

    override fun estorbos(m: Momento): List<Float> = when (lugar(m)) {
        Lugar.RETIRADA -> RetiradaNapoleon.estorbos(m)
        Lugar.VIVAC -> VivacNapoleon.estorbos(m)
        Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> VivacNapoleon.estorbos(m)
    }

    override fun anclas(m: Momento): List<Ancla> = when (lugar(m)) {
        Lugar.RETIRADA -> RetiradaNapoleon.anclas(m)
        Lugar.VIVAC -> VivacNapoleon.anclas(m)
        Lugar.CUARTEL, Lugar.MOSCU, Lugar.HOSPITAL, Lugar.PUEBLO -> VivacNapoleon.anclas(m)
    }
}
