package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

/**
 * El azar de las fichas. Es propio (SplitMix64) y no el de la biblioteca, para que la misma semilla dé siempre lo mismo
 * en cualquier versión de la app: `kotlin.random.Random(semilla)` no promete eso entre versiones de Kotlin.
 */
class Azar(semilla: Long) {
    private var x = semilla

    fun siguiente(): Long {
        x += -0x61C8864680B583EBL                       // 0x9E3779B97F4A7C15
        var z = x
        z = (z xor (z ushr 30)) * -0x40A7B892E31B1A47L  // 0xBF58476D1CE4E5B9
        z = (z xor (z ushr 27)) * -0x6B2FB644ECCEEE15L  // 0x94D049BB133111EB
        return z xor (z ushr 31)
    }

    /** Un entero de 0 a [n] − 1. */
    fun entero(n: Int): Int = Math.floorMod(siguiente(), n.toLong()).toInt()
}

/** Semillas que no dependen de la máquina ni de la versión: FNV-1a de 64 bits sobre el texto de las partes. */
object Semilla {
    fun de(vararg partes: Any): Long {
        var h = -0x340D631B7BDDDCDBL                    // 0xCBF29CE484222325
        for (b in partes.joinToString("|").toByteArray(Charsets.UTF_8)) {
            h = h xor (b.toLong() and 0xFF)
            h *= 0x100000001B3L
        }
        return Azar(h).siguiente()
    }
}

/**
 * La luz a la [hora] (0 a 24, con decimales) según el [regimen].
 *
 * Seis franjas: amanecer de 5 a 8, mañana de 8 a 12, mediodía de 12 a 16, tarde de 16 a 19, atardecer de 19 a 21 y
 * noche de 21 a 5. Son horas fijas, iguales todo el año y en todos los lugares **[Aproximación]**: el amanecer empieza
 * a las 5:00, con el día lógico. Dos estados: encendido de 5 a 21 y tenue de 21 a 5. Siempre de noche: noche, y el
 * avance cuenta el día entero desde las 5:00.
 */
fun luzDe(hora: Double, regimen: RegimenLuz): Luz {
    val h = ((hora % 24) + 24) % 24
    fun tramo(f: Franja, desde: Double, hasta: Double) = Luz(f, ((h - desde + 24) % 24) / ((hasta - desde + 24) % 24))
    return when (regimen) {
        RegimenLuz.SEIS_FRANJAS -> when {
            h < 5 -> tramo(Franja.NOCHE, 21.0, 5.0)
            h < 8 -> tramo(Franja.AMANECER, 5.0, 8.0)
            h < 12 -> tramo(Franja.MANANA, 8.0, 12.0)
            h < 16 -> tramo(Franja.MEDIODIA, 12.0, 16.0)
            h < 19 -> tramo(Franja.TARDE, 16.0, 19.0)
            h < 21 -> tramo(Franja.ATARDECER, 19.0, 21.0)
            else -> tramo(Franja.NOCHE, 21.0, 5.0)
        }
        RegimenLuz.DOS_ESTADOS -> if (h >= 5 && h < 21) tramo(Franja.ENCENDIDO, 5.0, 21.0) else tramo(Franja.TENUE, 21.0, 5.0)
        RegimenLuz.SIEMPRE_NOCHE -> Luz(Franja.NOCHE, ((h - 5 + 24) % 24) / 24)
    }
}

/** Un día especial que toca hoy: cuál es, qué día real empezó y en qué día suyo va (1 = el primero). */
data class EspecialDelDia(val dia: DiaEspecial, val inicio: LocalDate, val numero: Int) {
    val primerDia: Boolean get() = numero == 1
    val ultimoDia: Boolean get() = numero == dia.duracion
}

/**
 * La ficha del día de un personaje: todo lo que el cuento, la escena y el cuadro necesitan saber de «hoy, en su vida».
 *
 * Es **determinista** (el mismo día y el mismo personaje dan siempre la misma ficha, en cualquier teléfono) y es **una
 * por día lógico**: no depende de la hora. Lo que sí cambia con la hora se le pregunta aparte ([luz], [tramo]).
 * No lee ningún dato tuyo.
 *
 * - [temporada], [vuelta], [avance]: ver [Temporadas].
 * - [edad] y [anioHistorico]: avanzan uno con cada temporada. [cuando] y [mundo]: el año y el estado del mundo, como
 *   los escribe el calendario.
 * - [semilla]: el azar de este día, por si la escena o el gato quieren sortear algo propio.
 * - [estacion] y [clima]: los del lugar del personaje. El clima se sortea con la tabla del mes, salvo que un día
 *   especial pida uno.
 * - [especiales]: los días especiales que tocan hoy; manda el primero ([principal]).
 * - [arcos]: el valor de hoy (de 0 a 1) de cada arco de la escena.
 * - [objeto]: el objeto del día, o null si el calendario no tiene objetos.
 */
data class FichaDelDia(
    val vida: String, val dia: String, val temporada: Int, val vuelta: Int, val avance: Boolean,
    val edad: Int, val anioHistorico: Int, val cuando: String, val mundo: String, val semilla: Long,
    val estacion: Estacion, val clima: Clima, val especiales: List<EspecialDelDia>, val arcos: Map<String, Double>, val objeto: String?,
    val regimen: RegimenLuz, private val tramos: List<OtrasVidas.Tramo>,
) {
    /** Hoy es un día especial: enciende la marca del cuadro. */
    val especial: Boolean get() = especiales.isNotEmpty()

    /** El día especial que manda hoy: el más corto de los que tocan; si empatan, el que empezó después. */
    val principal: EspecialDelDia? get() = especiales.firstOrNull()

    /** El estado que pide la escena hoy; vacío si ningún día especial la cambia. */
    val escena: String get() = especiales.firstOrNull { it.dia.escena.isNotEmpty() }?.dia?.escena ?: ""

    /** La luz de la escena a la [hora] (0 a 24, con decimales). */
    fun luz(hora: Double): Luz = luzDe(hora, regimen)

    /** Qué hace el personaje a la [hora], según «Un día suyo»; null si a esa hora no tiene tramo. */
    fun tramo(hora: Double): OtrasVidas.Tramo? {
        val h = ((hora % 24) + 24) % 24
        return tramos.firstOrNull { if (it.desde <= it.hasta) h >= it.desde && h < it.hasta else h >= it.desde || h < it.hasta }
    }

    /**
     * Qué variante de cada párrafo toca hoy. [variantes] dice cuántas tiene cada lugar del cuento (normalmente ocho
     * números, uno por [Parrafo]); devuelve, para cada lugar, un número de 0 a su cantidad − 1.
     *
     * Si hay combinaciones suficientes (el producto de [variantes] es al menos 1,098, es decir, tres años), la
     * combinación de un día no se repite en todo su año ni en el año anterior o el siguiente: ninguna se repite en
     * 365 días. Además, un lugar con 3 variantes o más no repite la suya dos días seguidos del mismo año, salvo que
     * casi no queden combinaciones libres.
     */
    fun piezas(variantes: List<Int>): List<Int> {
        val d = LocalDate.parse(dia)
        return Barajas.piezas(vida, d.year, variantes)[d.dayOfYear - 1].toList()
    }

    companion object {
        /** Cuánto se puede mover un suceso personal, en días, cuando la temporada 5 se repite. **[Diseño propio]** */
        const val MARGEN_SORTEO = 45

        private val memoria = ConcurrentHashMap<String, FichaDelDia>()

        /**
         * La ficha del personaje [vidaId] en el día lógico [diaLogico] («2027-03-21»; el día lógico empieza a las
         * 5:00). Null si el personaje no existe o todavía no tiene calendario. Se calcula una vez y se recuerda.
         */
        fun de(diaLogico: String, vidaId: String): FichaDelDia? {
            val clave = "$vidaId|$diaLogico"
            memoria[clave]?.let { return it }
            val quien = OtrasVidas.porId(vidaId) ?: return null
            val cal = Calendarios.de(vidaId) ?: return null
            if (memoria.size > 200) memoria.clear()
            return memoria.getOrPut(clave) { de(diaLogico, quien, cal) }
        }

        /** Lo mismo, con el personaje y su calendario en la mano (para las pruebas). No recuerda nada. */
        fun de(diaLogico: String, quien: OtrasVidas.Vida, cal: Calendario): FichaDelDia {
            val d = LocalDate.parse(diaLogico)
            val temporada = Temporadas.de(d.year)
            val datos = cal.temporadas[temporada - 1]
            val especiales = especialesDe(cal, d)
            val pesos = cal.clima[d.monthValue - 1]
            return FichaDelDia(
                vida = cal.vida, dia = diaLogico, temporada = temporada, vuelta = Temporadas.vuelta(d.year), avance = Temporadas.esAvance(d.year),
                edad = quien.edad + temporada - 1, anioHistorico = Temporadas.anioHistorico(quien.anio, temporada), cuando = datos.cuando, mundo = datos.mundo,
                semilla = Semilla.de(cal.vida, diaLogico),
                estacion = cal.estacion(d),
                clima = especiales.firstNotNullOfOrNull { it.dia.clima } ?: sortearClima(pesos, Azar(Semilla.de(cal.vida, diaLogico, "clima"))),
                especiales = especiales,
                arcos = cal.arcos.associate { it.id to it.valor(temporada, d) },
                objeto = if (cal.objetos.isEmpty()) null else cal.objetos[Barajas.objetos(cal.vida, d.year, cal.objetos.size)[d.dayOfYear - 1]],
                regimen = cal.luz, tramos = quien.dia,
            )
        }

        /**
         * Cuándo empieza cada día especial del año real [anio]. De 2027 a 2031 (y en el avance de 2026), en su fecha.
         * Desde 2032, cuando la temporada 5 se repite, los sucesos personales se sortean de nuevo: cada uno (o cada
         * cadena, entera) se mueve hasta [MARGEN_SORTEO] días antes o después de su fecha, sin salirse del año.
         * Las fiestas, las cosechas, los ritos, los hechos de guerra y los sucesos del mundo no se mueven.
         */
        fun inicios(cal: Calendario, anio: Int): List<Pair<DiaEspecial, LocalDate>> {
            val dias = cal.temporadas[Temporadas.de(anio) - 1].dias
            val mover = HashMap<String, Int>()
            if (Temporadas.vuelta(anio) > 0) dias.filter { it.tipo == TipoDia.SUCESO }.groupBy { it.cadena ?: it.id }.forEach { (grupo, l) ->
                val desde = maxOf(-MARGEN_SORTEO, -l.minOf { MesDia.indice(it.fecha) })
                val hasta = minOf(MARGEN_SORTEO, 364 - l.maxOf { MesDia.indice(it.fecha) })
                mover[grupo] = desde + Azar(Semilla.de(cal.vida, "sorteo", anio, grupo)).entero(hasta - desde + 1)
            }
            return dias.map { d -> d to MesDia.enAnio(d.fecha, anio).plusDays((if (d.tipo == TipoDia.SUCESO) mover[d.cadena ?: d.id] ?: 0 else 0).toLong()) }
        }

        /**
         * Los días especiales que tocan el día [d], el que manda primero. Cuentan los de su año y los del año anterior
         * que todavía no terminan (una fiesta que empieza el 28 de diciembre y dura diez días sigue en enero).
         */
        private fun especialesDe(cal: Calendario, d: LocalDate): List<EspecialDelDia> =
            listOf(d.year, d.year - 1).flatMap { anio ->
                inicios(cal, anio).mapNotNull { (dia, inicio) ->
                    val n = ChronoUnit.DAYS.between(inicio, d)
                    if (n >= 0 && n < dia.duracion) EspecialDelDia(dia, inicio, n.toInt() + 1) else null
                }
            }.sortedWith(compareBy<EspecialDelDia> { it.dia.duracion }.thenByDescending { it.inicio })

        private fun sortearClima(pesos: Map<Clima, Int>, azar: Azar): Clima {
            // En el orden del enum, no en el del mapa: así el sorteo no depende de cómo se escribió la tabla.
            val lista = Clima.entries.mapNotNull { c -> pesos[c]?.takeIf { it > 0 }?.let { c to it } }
            if (lista.isEmpty()) return Clima.SIN_CLIMA
            var resto = azar.entero(lista.sumOf { it.second })
            for ((c, peso) in lista) { if (resto < peso) return c; resto -= peso }
            return lista.last().first
        }
    }
}

/**
 * El reparto del año: qué variante de cada párrafo y qué objeto toca cada día. Se calcula el año entero de una vez
 * (y se recuerda), porque lo de un día depende de los demás: nada se repite de un día al siguiente y ninguna
 * combinación sale dos veces.
 */
internal object Barajas {
    private const val DIAS = 366
    private val memoria = ConcurrentHashMap<String, Array<IntArray>>()

    /**
     * [largo] números de 0 a [n] − 1 como quien reparte una baraja: se barajan las [n] cartas, se reparten todas, se
     * vuelven a barajar… Así cada una sale casi las mismas veces y, si la primera de una ronda es la última de la
     * anterior, se cambia por otra para que no salga dos veces seguidas.
     */
    fun baraja(n: Int, largo: Int, azar: Azar): IntArray {
        val s = IntArray(largo)
        if (n <= 1) return s
        var i = 0; var ultima = -1
        val ronda = IntArray(n) { it }
        while (i < largo) {
            for (j in n - 1 downTo 1) { val k = azar.entero(j + 1); val v = ronda[j]; ronda[j] = ronda[k]; ronda[k] = v }
            if (ronda[0] == ultima) { val k = 1 + azar.entero(n - 1); ronda[0] = ronda[k]; ronda[k] = ultima }
            for (v in ronda) { if (i < largo) s[i++] = v }
            ultima = ronda[n - 1]
        }
        return s
    }

    /** El objeto de cada día del año [anio] (366 lugares), entre [n] objetos. */
    fun objetos(vida: String, anio: Int, n: Int): IntArray {
        if (memoria.size > 60) memoria.clear()
        return memoria.getOrPut("$vida|$anio|objetos|$n") { arrayOf(baraja(n, DIAS, Azar(Semilla.de(vida, "objeto", anio)))) }[0]
    }

    /**
     * La variante de cada párrafo para cada día del año [anio]: 366 filas, una por día, con un número por lugar.
     *
     * Cada lugar se reparte con su [baraja]. Después se quitan las combinaciones repetidas: un año par solo se
     * compara consigo mismo; un año impar, además, con el anterior y el siguiente (que son pares y no dependen de
     * él). Así dos años seguidos nunca comparten una combinación y no hay que calcular todos los años desde el primero.
     */
    fun piezas(vida: String, anio: Int, variantes: List<Int>): Array<IntArray> {
        require(variantes.isNotEmpty() && variantes.all { it >= 1 }) { "Cada párrafo necesita al menos una variante" }
        if (memoria.size > 60) memoria.clear()
        return memoria.getOrPut("$vida|$anio|piezas|$variantes") {
            val vecinos = if (Math.floorMod(anio, 2) == 1) listOf(piezas(vida, anio - 1, variantes), piezas(vida, anio + 1, variantes)) else emptyList()
            repartir(vida, anio, variantes, vecinos)
        }
    }

    private fun repartir(vida: String, anio: Int, variantes: List<Int>, vecinos: List<Array<IntArray>>): Array<IntArray> {
        val azar = Azar(Semilla.de(vida, "piezas", anio))
        val columnas = variantes.map { baraja(it, DIAS, azar) }
        val usadas = HashSet<List<Int>>()
        vecinos.forEach { v -> v.forEach { usadas += it.toList() } }
        fun fila(dia: Int) = columnas.map { it[dia] }
        // Cuántas combinaciones hay en total (sin pasarse de lo que cabe en un Long).
        val total = variantes.fold(1L) { a, n -> if (a > Long.MAX_VALUE / n) Long.MAX_VALUE else a * n }
        for (dia in 0 until DIAS) {
            if (fila(dia) in usadas && !cambiarUna(columnas, variantes, dia, usadas, azar)) cambiarTodas(columnas, variantes, dia, usadas, total, azar)
            usadas += fila(dia)
        }
        return Array(DIAS) { dia -> IntArray(columnas.size) { columnas[it][dia] } }
    }

    /**
     * Arregla una combinación repetida cambiando la variante de un solo lugar por otra que no sea la de ayer ni la
     * de mañana. Devuelve si lo logró.
     */
    private fun cambiarUna(columnas: List<IntArray>, variantes: List<Int>, dia: Int, usadas: Set<List<Int>>, azar: Azar): Boolean {
        val primero = azar.entero(columnas.size)
        for (paso in columnas.indices) {
            val col = columnas[(primero + paso) % columnas.size]; val n = variantes[(primero + paso) % columnas.size]
            val original = col[dia]
            for (salto in 1 until n) {
                val v = (original + salto) % n
                if ((dia > 0 && col[dia - 1] == v) || (dia < DIAS - 1 && col[dia + 1] == v)) continue
                col[dia] = v
                if (columnas.map { it[dia] } !in usadas) return true
            }
            col[dia] = original
        }
        return false
    }

    /**
     * Último recurso, cuando quedan muy pocas combinaciones libres: recorre las combinaciones una por una, desde un
     * lugar sorteado, y toma la primera libre, aunque repita la variante de ayer en algún lugar (importa más que el
     * cuento no se repita). Como nunca hay más de 1,098 usadas (tres años), basta mirar 1,099.
     */
    private fun cambiarTodas(columnas: List<IntArray>, variantes: List<Int>, dia: Int, usadas: Set<List<Int>>, total: Long, azar: Azar) {
        val desde = if (total > 2200) Math.floorMod(azar.siguiente(), total - 1100) else 0L
        for (c in 0 until minOf(total, 1099L)) {
            var resto = (desde + c) % total
            val fila = variantes.map { n -> (resto % n).toInt().also { resto /= n } }
            if (fila !in usadas) { fila.forEachIndexed { i, v -> columnas[i][dia] = v }; return }
        }
    }
}
