package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import java.time.LocalDate

/*
 * El año de cada personaje de «Otras vidas»: con qué datos se escribe su calendario (las cinco temporadas, los días
 * especiales, los arcos de la escena, las estaciones y el clima de su lugar). Es Kotlin puro, sin Android, para que se
 * pueda probar fuera del teléfono. Nada de esto se guarda ni se exporta, y nada lee un dato tuyo: todo sale de la fecha
 * y del personaje (plan de historia y escenas, secciones 3.1 y 6).
 */

/**
 * Las temporadas. Cada año real es un año más en la vida del personaje: de 2027 a 2031 corren las temporadas 1 a 5.
 * En 2026 (y antes) se muestra la 1 como avance. Desde 2032 se repite la 5, con sus sucesos personales sorteados de
 * nuevo; cada repetición es una «vuelta» más.
 */
object Temporadas {
    const val PRIMER_ANIO = 2027
    const val CUANTAS = 5

    /** La temporada del año real [anioReal]: `clamp(anioReal − 2026, 1, 5)`. */
    fun de(anioReal: Int): Int = (anioReal - (PRIMER_ANIO - 1)).coerceIn(1, CUANTAS)

    /** Cuántas veces se ha repetido ya la última temporada: 0 hasta 2031, 1 en 2032, 2 en 2033… */
    fun vuelta(anioReal: Int): Int = (anioReal - (PRIMER_ANIO + CUANTAS - 1)).coerceAtLeast(0)

    /** Antes de 2027 la temporada 1 se ve como avance. */
    fun esAvance(anioReal: Int): Boolean = anioReal < PRIMER_ANIO

    /**
     * El año histórico de la temporada: [base] (el `anio` del personaje; negativo = antes de Cristo) más uno por cada
     * temporada. No existe el año 0: de −1 (1 a. C.) se pasa a 1.
     */
    fun anioHistorico(base: Int, temporada: Int): Int {
        val a = base + temporada - 1
        return if (base < 0 && a >= 0) a + 1 else a
    }
}

/**
 * Fechas «MM-DD» de los calendarios, sin año: «03-21» es el 21 de marzo. El 29 de febrero no se puede escribir: en un
 * año bisiesto ese día cuenta como una prolongación del 28 (misma estación, mismos arcos).
 */
object MesDia {
    private val FORMA = Regex("""(\d\d)-(\d\d)""")
    private val LARGO = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private val ANTES = IntArray(12).also { a -> for (m in 1 until 12) a[m] = a[m - 1] + LARGO[m - 1] }

    private fun partes(s: String): Pair<Int, Int>? {
        val g = FORMA.matchEntire(s)?.groupValues ?: return null
        val mes = g[1].toInt(); val dia = g[2].toInt()
        return if (mes in 1..12 && dia in 1..LARGO[mes - 1]) mes to dia else null
    }

    fun valido(s: String): Boolean = partes(s) != null

    /** El lugar del día en un año de 365: 0 es el 1 de enero y 364, el 31 de diciembre. */
    fun indice(s: String): Int {
        val (mes, dia) = requireNotNull(partes(s)) { "Fecha «$s»: se esperaba MM-DD" }
        return ANTES[mes - 1] + dia - 1
    }

    /** Lo mismo para una fecha real. El 29 de febrero comparte lugar con el 28. */
    fun indice(d: LocalDate): Int = ANTES[d.monthValue - 1] + minOf(d.dayOfMonth, LARGO[d.monthValue - 1]) - 1

    fun mes(s: String): Int = requireNotNull(partes(s)) { "Fecha «$s»: se esperaba MM-DD" }.first

    /** La fecha real de [s] en el año [anio]. */
    fun enAnio(s: String, anio: Int): LocalDate {
        val (mes, dia) = requireNotNull(partes(s)) { "Fecha «$s»: se esperaba MM-DD" }
        return LocalDate.of(anio, mes, dia)
    }
}

/**
 * Qué clase de día especial es. Solo los sucesos personales ([SUCESO]) se sortean de nuevo cuando la temporada 5 se
 * repite. [MUNDO] es lo que pasa fuera de su casa y no es fiesta ni guerra (un eclipse, una peste, una inundación, un
 * papa nuevo): tiene su fecha y no se mueve.
 */
enum class TipoDia(val nombre: String) {
    FIESTA("fiesta"), COSECHA("cosecha"), GUERRA("guerra"), RITO("rito"), MUNDO("suceso del mundo"), SUCESO("suceso personal"),
}

/** Qué tan cierto es un día especial. Es otra escala que la de los números de «Otras vidas» ([OtrasVidas.Certeza]). */
enum class CertezaDia(val nombre: String, val explica: String) {
    HISTORICO("histórico", "Pasó o se celebraba en esa fecha, según las fuentes."),
    APROXIMADO("aproximado", "Pasó o se celebraba, pero las fuentes no dan el día exacto: la fecha se eligió dentro de lo que dicen."),
    INVENTADO("inventado", "Verosímil para su época y su lugar, pero inventado."),
}

/** La estación del lugar del personaje. No todos los lugares tienen las cuatro de la zona templada. */
enum class Estacion(val nombre: String) {
    PRIMAVERA("primavera"), VERANO("verano"), OTONO("otoño"), INVIERNO("invierno"),
    SECAS("secas"), LLUVIAS("lluvias"),
    /** Las tres del Nilo. */
    INUNDACION("inundación"), SIEMBRA("siembra"), COSECHA("cosecha"),
    /** Bajo tierra, en una nave o donde el año no se nota. */
    NINGUNA("sin estaciones"),
}

/** El tiempo que hace. [SIN_CLIMA] es para los interiores (el refugio, el laboratorio, la nave). */
enum class Clima(val nombre: String) {
    DESPEJADO("despejado"), NUBES("nublado"), LLUVIA("lluvia"), TORMENTA("tormenta"), NIEVE("nieve"), NIEBLA("niebla"),
    VIENTO("viento"), POLVO("polvo"), SIN_CLIMA("sin clima"),
}

/** Cómo cambia la luz de la escena con la hora. */
enum class RegimenLuz {
    /** Casi todos: amanecer, mañana, mediodía, tarde, atardecer y noche. */
    SEIS_FRANJAS,
    /** Ciudad neón: los niveles bajos, donde siempre es de noche. */
    SIEMPRE_NOCHE,
    /** Refugio, Laboratorio y Espacio: encendido de día y tenue de noche (bajo tierra o con el ciclo de luz de a bordo). */
    DOS_ESTADOS,
}

/** Una franja de luz. Las seis primeras son de [RegimenLuz.SEIS_FRANJAS]; las dos últimas, de [RegimenLuz.DOS_ESTADOS]. */
enum class Franja(val nombre: String, val oscura: Boolean) {
    AMANECER("amanecer", false), MANANA("mañana", false), MEDIODIA("mediodía", false), TARDE("tarde", false),
    ATARDECER("atardecer", false), NOCHE("noche", true), ENCENDIDO("luz encendida", false), TENUE("luz tenue", true),
}

/** La luz de una hora: la franja y cuánto de ella ha pasado (de 0, cuando empieza, a 1, cuando termina). */
data class Luz(val franja: Franja, val avance: Double)

/** Los ocho lugares del cuento, en orden (plan, sección 3.2). */
enum class Parrafo(val nombre: String) {
    MANANA("la mañana y la luz"), TIEMPO("el tiempo y la estación"), TRABAJO("el trabajo del tramo"), PUEBLO("el pueblo y el entorno"),
    GENTE("la gente cercana"), OBJETO("el objeto del día"), PENSAMIENTO("lo que piensa"), ANOCHECER("el anochecer"),
}

/**
 * Un día especial de una temporada: una fiesta, una cosecha, un hecho de guerra, un rito o un suceso de su vida.
 *
 * - [id]: su clave, única dentro de la temporada. El mismo id en otra temporada es el mismo día que vuelve cada año
 *   (una fiesta), y así comparte su texto.
 * - [fecha]: «MM-DD», exacta, sin margen de días. Dura [duracion] días seguidos (una cosecha puede durar 30).
 * - [escena]: el estado que toma la escena esos días («luto», «antorchas»…); vacío si la escena no cambia.
 * - [fuente]: título y enlace, solo si la fuente se abrió y se comprobó. Lo inventado nunca lleva enlace.
 * - [cadena]: une sucesos que dependen uno de otro («enferma» y después «se recupera»). Cuando la temporada 5 se
 *   repite, los de una misma cadena se mueven juntos y conservan su distancia.
 * - [clima]: si ese día tiene que hacer un tiempo concreto (una nevada), manda sobre el sorteo.
 */
class DiaEspecial(
    val id: String, val fecha: String, val duracion: Int = 1, val tipo: TipoDia, val titulo: String, val escena: String = "",
    val certeza: CertezaDia, val fuente: Pair<String, String>? = null, val cadena: String? = null, val clima: Clima? = null,
)

/** Qué mide un arco de la escena. */
enum class TipoArco(val nombre: String) {
    CULTIVO("etapa del cultivo"), OBRA("avance de una obra"), DANOS("daños y reconstrucción"), ADORNOS("adornos"), OTRO("otro"),
}

/**
 * Un punto de un arco: el [valor] (de 0 a 1) que tiene el [fecha] («MM-DD») de la [temporada]. Con [salto], el valor
 * anterior se mantiene hasta la víspera y cambia de golpe ese día (la casa que se quema); sin él, se llega poco a poco.
 * La temporada puede ser 0 o 6: son puntos de apoyo fuera de las cinco, para que la línea llegue bien a los bordes.
 */
class PuntoArco(val temporada: Int, val fecha: String, val valor: Double, val salto: Boolean = false) {
    /** Su lugar en la línea de los cinco años: 0 es el 1 de enero de la temporada 1. */
    val lugar: Int get() = (temporada - 1) * 365 + MesDia.indice(fecha)
}

/**
 * Un arco de la escena: algo que cambia despacio a lo largo de las temporadas (el trigo que crece, la catedral que
 * sube, la casa que se reconstruye). Es **una sola línea para los cinco años**: entre dos puntos seguidos el valor
 * avanza en línea recta, así que de una temporada a la siguiente empalma solo, sin brincos, salvo donde un punto pide
 * un [PuntoArco.salto]. Antes del primer punto vale lo que el primero; después del último, lo que el último.
 */
class Arco(val id: String, val tipo: TipoArco, val puntos: List<PuntoArco>) {
    /** El valor en el lugar [lugar] de la línea (ver [PuntoArco.lugar]). */
    fun valor(lugar: Int): Double {
        if (puntos.isEmpty()) return 0.0
        val i = puntos.indexOfLast { it.lugar <= lugar }
        if (i < 0) return puntos.first().valor
        val a = puntos[i]
        val b = puntos.getOrNull(i + 1) ?: return a.valor
        if (b.salto) return a.valor
        return a.valor + (b.valor - a.valor) * (lugar - a.lugar) / (b.lugar - a.lugar).toDouble()
    }

    /** El valor el día [fecha] de la temporada [temporada]. */
    fun valor(temporada: Int, fecha: LocalDate): Double = valor((temporada - 1) * 365 + MesDia.indice(fecha))

    companion object {
        /**
         * Un arco que se repite igual cada año (un cultivo): los mismos puntos «MM-DD» en las temporadas 0 a 6, para
         * que también empalme en enero de la primera y en diciembre de la última. Los [saltos] son las fechas en que
         * el valor cambia de golpe (la siega).
         */
        fun cadaAnio(id: String, tipo: TipoArco, vararg puntos: Pair<String, Double>, saltos: Set<String> = emptySet()): Arco =
            Arco(id, tipo, (0..Temporadas.CUANTAS + 1).flatMap { n -> puntos.sortedBy { MesDia.indice(it.first) }.map { (f, v) -> PuntoArco(n, f, v, f in saltos) } })
    }
}

/**
 * Una temporada del personaje. [cuando]: el año como se dice en su mundo («430 a. C.», «año 41 tras el silencio»).
 * [mundo]: el estado del mundo ese año, en una clave («paz», «guerra», «peste»). [resumen]: qué pasa ese año, en una
 * línea, para quien escribe la temporada siguiente. [dias]: sus días especiales.
 */
class TemporadaVida(val cuando: String, val mundo: String, val resumen: String, val dias: List<DiaEspecial>)

/**
 * El calendario de un personaje: sus cinco temporadas, escritas de una vez para que la historia sea continua.
 *
 * - [vida]: el id del personaje en [OtrasVidas].
 * - [estaciones]: desde qué fecha «MM-DD» rige cada estación en su lugar.
 * - [clima]: doce tablas, una por mes (de enero a diciembre): cada tiempo con su peso. El de cada día se sortea con
 *   esos pesos. **[Aproximación]**: es un clima verosímil para el lugar, no un registro.
 * - [luz]: cómo cambia la luz de su escena con la hora.
 * - [arcos]: lo que cambia despacio en la escena.
 * - [objetos]: las claves de los objetos que pueden salir como «objeto del día».
 */
class Calendario(
    val vida: String, val estaciones: List<Pair<String, Estacion>>, val clima: List<Map<Clima, Int>>, val luz: RegimenLuz = RegimenLuz.SEIS_FRANJAS,
    val temporadas: List<TemporadaVida>, val arcos: List<Arco> = emptyList(), val objetos: List<String> = emptyList(),
) {
    /** La estación del día [d]: la del último cambio que ya pasó; antes del primero del año, la que venía del año anterior. */
    fun estacion(d: LocalDate): Estacion {
        val hoy = MesDia.indice(d)
        val orden = estaciones.sortedBy { MesDia.indice(it.first) }
        return (orden.lastOrNull { MesDia.indice(it.first) <= hoy } ?: orden.lastOrNull())?.second ?: Estacion.NINGUNA
    }

    /**
     * Todo lo que está mal escrito en este calendario; vacío si está bien. Lo usan las pruebas de cada tanda de
     * calendarios. Revisa la forma (fechas, duraciones, claves, pesos, puntos de los arcos) y las reglas de certeza:
     * lo inventado no lleva enlace y el calendario de un personaje de ficción es todo inventado.
     */
    fun problemas(): List<String> {
        val p = mutableListOf<String>()
        val quien = OtrasVidas.porId(vida)
        if (quien == null) p += "No existe la vida «$vida» en OtrasVidas"

        if (estaciones.isEmpty()) p += "Faltan las estaciones"
        estaciones.forEach { (f, _) -> if (!MesDia.valido(f)) p += "Estación con fecha «$f»: se esperaba MM-DD" }
        estaciones.map { it.first }.let { if (it.size != it.toSet().size) p += "Dos estaciones empiezan el mismo día" }

        if (clima.size != 12) p += "El clima necesita 12 tablas, una por mes (hay ${clima.size})"
        clima.forEachIndexed { i, mes ->
            if (mes.values.any { it < 0 }) p += "Clima del mes ${i + 1}: hay un peso negativo"
            if (mes.values.sum() <= 0) p += "Clima del mes ${i + 1}: los pesos suman 0"
        }

        if (temporadas.size != Temporadas.CUANTAS) p += "Se esperaban ${Temporadas.CUANTAS} temporadas (hay ${temporadas.size})"
        temporadas.forEachIndexed { i, temp ->
            val n = "Temporada ${i + 1}"
            if (temp.cuando.isBlank()) p += "$n: falta «cuando»"
            if (!CLAVE.matches(temp.mundo)) p += "$n: «mundo» debe ser una clave en minúsculas («${temp.mundo}»)"
            temp.dias.groupBy { it.id }.forEach { (id, l) -> if (l.size > 1) p += "$n: el día «$id» está ${l.size} veces" }
            temp.dias.forEach { d ->
                val donde = "$n, día «${d.id}»"
                if (!CLAVE.matches(d.id)) p += "$donde: el id debe ser una clave en minúsculas"
                if (!MesDia.valido(d.fecha)) p += "$donde: fecha «${d.fecha}», se esperaba MM-DD (y no el 29 de febrero)"
                if (d.duracion !in 1..365) p += "$donde: duración ${d.duracion}, debe ir de 1 a 365"
                if (d.titulo.isBlank()) p += "$donde: falta el título"
                if (d.escena.isNotEmpty() && !CLAVE.matches(d.escena)) p += "$donde: «escena» debe ser una clave en minúsculas («${d.escena}»)"
                if (d.cadena != null && !CLAVE.matches(d.cadena)) p += "$donde: «cadena» debe ser una clave en minúsculas («${d.cadena}»)"
                if (d.certeza == CertezaDia.INVENTADO && d.fuente != null) p += "$donde: lo inventado no lleva fuente"
                if (d.fuente != null && (d.fuente.first.isBlank() || !d.fuente.second.startsWith("https://"))) p += "$donde: la fuente necesita título y un enlace https"
                if (quien?.ficticia == true && d.certeza != CertezaDia.INVENTADO) p += "$donde: el calendario de un personaje de ficción es todo inventado"
            }
            // Una cadena se mueve entera cuando la temporada se repite: no puede mezclar sucesos (que se mueven) con días fijos.
            temp.dias.filter { it.cadena != null }.groupBy { it.cadena }.forEach { (c, l) ->
                if (l.any { it.tipo == TipoDia.SUCESO } && l.any { it.tipo != TipoDia.SUCESO }) p += "$n: la cadena «$c» mezcla sucesos personales con días que no se mueven"
            }
        }

        arcos.groupBy { it.id }.forEach { (id, l) -> if (l.size > 1) p += "El arco «$id» está ${l.size} veces" }
        arcos.forEach { a ->
            val donde = "Arco «${a.id}»"
            if (!CLAVE.matches(a.id)) p += "$donde: el id debe ser una clave en minúsculas"
            if (a.puntos.isEmpty()) p += "$donde: no tiene puntos"
            val malos = a.puntos.filter { !MesDia.valido(it.fecha) }
            malos.forEach { p += "$donde: fecha «${it.fecha}», se esperaba MM-DD" }
            a.puntos.forEach {
                if (it.temporada !in 0..Temporadas.CUANTAS + 1) p += "$donde: temporada ${it.temporada}, debe ir de 0 a ${Temporadas.CUANTAS + 1}"
                if (it.valor !in 0.0..1.0) p += "$donde: valor ${it.valor}, debe ir de 0 a 1"
            }
            if (malos.isEmpty()) {
                if (a.puntos.zipWithNext().any { (x, y) -> y.lugar <= x.lugar }) p += "$donde: los puntos deben ir en orden de fecha, sin repetir día"
                if (a.puntos.firstOrNull()?.salto == true) p += "$donde: el primer punto no puede ser un salto"
            }
        }

        if (objetos.size != objetos.toSet().size) p += "Hay objetos repetidos"
        objetos.forEach { if (!CLAVE.matches(it)) p += "Objeto «$it»: debe ser una clave en minúsculas" }
        return p
    }

    companion object {
        /** Las claves (ids, escenas, cadenas, objetos, mundo): minúsculas sin acentos, cifras y guion bajo. */
        val CLAVE = Regex("""[a-z][a-z0-9_]*""")

        /** Las cuatro estaciones de la zona templada del norte, por sus fechas astronómicas redondeadas. **[Aproximación]** */
        val TEMPLADO_NORTE = listOf("03-20" to Estacion.PRIMAVERA, "06-21" to Estacion.VERANO, "09-22" to Estacion.OTONO, "12-21" to Estacion.INVIERNO)

        /** Las mismas fechas en el hemisferio sur, con las estaciones al revés. **[Aproximación]** */
        val TEMPLADO_SUR = listOf("03-20" to Estacion.OTONO, "06-21" to Estacion.INVIERNO, "09-22" to Estacion.PRIMAVERA, "12-21" to Estacion.VERANO)

        /** Donde el año no se nota. */
        val SIN_ESTACIONES = listOf("01-01" to Estacion.NINGUNA)

        /** El mismo tiempo todo el año (los interiores: `climaFijo(Clima.SIN_CLIMA)`). */
        fun climaFijo(c: Clima): List<Map<Clima, Int>> = List(12) { mapOf(c to 1) }
    }
}
