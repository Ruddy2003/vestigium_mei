package io.github.ruddy2003.vestigium.data

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Consumo de sustancias: episodios con nivel subjetivo (0 a 10), y dos estimaciones farmacológicas:
 * - Cafeína en el cuerpo (mg): cada dosis se absorbe en ~45 min y se elimina con la vida media que elijas (5 h por omisión).
 * - Alcohol en sangre (g/L, Widmark): absorción en ~30 min y eliminación constante de 0.15 g/L por hora.
 * Las dosis salen de MacroFactor (cada alimento con su hora) y de lo que anotes aquí; lo que aparece en los dos se cuenta una
 * vez, toma por toma ([dosis]).
 */
object Sustancias {
    /** Una bebida estándar: 14 g de alcohol puro. */
    const val GRAMOS_BEBIDA = 14.0
    private const val H = 3_600_000L
    private const val MIN = 60_000L

    fun visible(id: String, c: Config = Repo.config) = id !in c.sustancias_ocultas
    fun visible(c: Consumo) = visible(c.sustancia)

    /** Los que siguen: con nivel, sin fin y empezados hace menos de 18 horas. */
    fun abiertos(ahora: Long = System.currentTimeMillis()): List<Consumo> =
        Repo.consumos.filter { c -> c.fin == null && Catalogo.sustancia(c.sustancia)?.conNivel == true && ahora - Fechas.millis(c.inicio) in 0..18 * H }

    /** Lo de anoche (o de hace unos días) que falta completar: duración, máximo y cruda. */
    fun porCompletar(hoy: String = Fechas.diaLogico()): List<Consumo> =
        Repo.consumos.filter { c -> !c.completo && c.fecha < hoy && c.fecha >= Fechas.sumar(hoy, -3) && Catalogo.sustancia(c.sustancia)?.conNivel == true }

    fun nuevo(sustancia: String, inicioMs: Long, nivel: Int?, cantidad: Double? = null, via: String? = null, otra: String = "", conQuien: List<String> = emptyList(), origen: String = "app"): Consumo {
        val ini = Fechas.isoDeMillis(inicioMs)
        val conNivel = Catalogo.sustancia(sustancia)?.conNivel == true
        val c = Consumo(Repo.nuevoId(), Fechas.diaLogicoDeMillis(inicioMs), sustancia, otra.trim(), ini,
            // Tabaco y cafeína son instantáneos: quedan cerrados y completos al registrarlos.
            fin = if (conNivel) null else ini,
            niveles = if (nivel != null) listOf(Lectura(Fechas.isoDeMillis(System.currentTimeMillis().coerceAtLeast(inicioMs)), nivel)) else emptyList(),
            nivel_max = nivel, cantidad = cantidad, via = via, con_quien = conQuien, completo = !conNivel, origen = origen,
            ts_creado = Fechas.ahoraIso())
        Repo.guardarConsumo(c)
        return c
    }

    fun lectura(c: Consumo, nivel: Int, ms: Long = System.currentTimeMillis()): Consumo {
        val n = c.copy(niveles = (c.niveles + Lectura(Fechas.isoDeMillis(ms), nivel)).sortedBy { it.ts }, nivel_max = maxOf(c.nivel_max ?: 0, nivel))
        Repo.guardarConsumo(n); return n
    }

    fun cerrar(c: Consumo, finMs: Long = System.currentTimeMillis()): Consumo {
        val n = c.copy(fin = Fechas.isoDeMillis(finMs.coerceAtLeast(Fechas.millis(c.inicio))))
        Repo.guardarConsumo(n); return n
    }

    /** Fin efectivo para dibujar y calcular: el que dijiste; si sigue, ahora; si quedó abierto de otro día, 4 horas después del inicio. */
    fun finEfectivo(c: Consumo, ahora: Long = System.currentTimeMillis()): Long {
        val ini = Fechas.millis(c.inicio)
        c.fin?.let { return Fechas.millis(it).coerceAtLeast(ini) }
        return if (ahora - ini in 0..18 * H) ahora else ini + 4 * H
    }

    /** Nivel subjetivo (0 a 10) en un instante, interpolando tus lecturas. Null = no hay con qué saberlo. */
    fun nivelEn(c: Consumo, ms: Long, ahora: Long = System.currentTimeMillis()): Double? {
        if (Catalogo.sustancia(c.sustancia)?.conNivel != true) return null
        val ini = Fechas.millis(c.inicio); val fin = finEfectivo(c, ahora)
        if (ms < ini || ms > fin) return 0.0
        val abierto = c.fin == null && fin == ahora
        val pts = ArrayList<Pair<Long, Double>>()
        val lect = c.niveles.map { Fechas.millis(it.ts).coerceIn(ini, fin) to it.nivel.toDouble() }.sortedBy { it.first }
        when {
            lect.isNotEmpty() -> {
                pts += ini to lect.first().second
                pts += lect
                // Si el máximo que dijiste en la mañana es mayor que lo leído, el pico se pone a la mitad del episodio.
                val max = c.nivel_max?.toDouble() ?: 0.0
                if (max > lect.maxOf { it.second } && fin > ini) pts += ((ini + fin) / 2) to max
                if (!abierto) pts += fin to 0.0
            }
            c.nivel_max != null -> {
                val d = fin - ini; val m = c.nivel_max.toDouble()
                pts += ini to 0.0; pts += (ini + d * 35 / 100) to m; pts += (ini + d * 65 / 100) to m
                if (!abierto) pts += fin to 0.0
            }
            else -> return null
        }
        val o = pts.sortedBy { it.first }
        if (ms <= o.first().first) return o.first().second
        for (i in 1 until o.size) {
            val (a, va) = o[i - 1]; val (b, vb) = o[i]
            if (ms <= b) return if (b == a) vb else va + (vb - va) * (ms - a).toDouble() / (b - a)
        }
        return o.last().second
    }

    /** Nivel máximo de un episodio: el que dijiste o el mayor leído. */
    fun maximo(c: Consumo): Int? = listOfNotNull(c.nivel_max, c.niveles.maxOfOrNull { it.nivel }).maxOrNull()

    fun horas(c: Consumo): Double? = c.fin?.let { (Fechas.millis(it) - Fechas.millis(c.inicio)) / 3_600_000.0 }?.takeIf { it > 0 }

    // ---------------------------------------------------------------- dosis con hora

    class Dosis(val ms: Long, val cantidad: Double)

    private fun msDe(fecha: String, hora: String): Long? = runCatching {
        Fechas.millisDe(LocalDateTime.of(Fechas.parse(fecha), LocalTime.parse(hora.take(5))))
    }.getOrNull()

    /** La hora de un alimento de MacroFactor; si no se puede leer, el mediodía de su día, para no perder la dosis (P-20). */
    private fun msComida(fecha: String, hora: String): Long? = msDe(fecha, hora) ?: msDe(fecha, "12:00")

    /** Hasta dónde un registro de aquí y uno de MacroFactor se toman por la misma toma (H-4-2). */
    private const val MISMA_TOMA = 90 * MIN

    /**
     * Cafeína (mg) y alcohol (g) con su hora entre dos días (de calendario), de MacroFactor y de tus registros, sin contar dos
     * veces lo que anotaste en los dos lados (H-4-2; antes, si MacroFactor tenía algo ese día, se ignoraba todo lo de aquí):
     * - Cafeína: cada toma de aquí se empareja con una de MacroFactor a menos de 90 minutos y de cantidad parecida (de la mitad
     *   al doble). La pareja cuenta una vez, con lo de aquí: su hora es la de la toma; la de MacroFactor, la del registro.
     * - Alcohol: un episodio de aquí y lo de MacroFactor entre su inicio y su fin (±90 minutos) son lo mismo; cuenta la fuente
     *   con más gramos.
     */
    fun dosis(desde: String, hasta: String, comidas: List<Comida>? = null): Pair<List<Dosis>, List<Dosis>> {
        val cs = comidas ?: runCatching { Repo.comidas(desde, hasta) }.getOrDefault(emptyList())
        val cafMf = cs.mapNotNull { c -> c.valores["cafeina_mg"]?.takeIf { it > 0 }?.let { v -> msComida(c.fecha, c.hora)?.let { Dosis(it, v) } } }
        val alcMf = cs.mapNotNull { c -> c.valores["alcohol_g"]?.takeIf { it > 0 }?.let { v -> msComida(c.fecha, c.hora)?.let { Dosis(it, v) } } }
        val ini = Fechas.inicioDia(desde) - 12 * H; val fin = Fechas.finDia(hasta) + 6 * H
        val propios = Repo.consumos.filter { Fechas.millis(it.inicio) in ini..fin }.sortedBy { Fechas.millis(it.inicio) }
        val cafLibres = cafMf.toMutableList()
        val cafPropia = propios.filter { it.sustancia == "cafeina" && (it.cantidad ?: 0.0) > 0 }.map { c ->
            val ms = Fechas.millis(c.inicio); val q = c.cantidad!!
            cafLibres.filter { kotlin.math.abs(it.ms - ms) <= MISMA_TOMA && it.cantidad in (q * 0.5)..(q * 2.0) }.minByOrNull { kotlin.math.abs(it.ms - ms) }
                ?.let { cafLibres.remove(it) }
            Dosis(ms, q)
        }
        // Las bebidas de un episodio se reparten a lo largo de la primera parte (o una cada 40 minutos si no hay fin).
        val alcLibres = alcMf.toMutableList()
        val alcPropio = propios.filter { it.sustancia == "alcohol" && (it.cantidad ?: 0.0) > 0 }.flatMap { c ->
            val n = c.cantidad!!; val a = Fechas.millis(c.inicio)
            val tramo = c.fin?.let { ((Fechas.millis(it) - a) * 0.7).toLong() }?.takeIf { it > 0 } ?: (40 * MIN * (n - 1).coerceAtLeast(0.0)).toLong()
            val k = kotlin.math.ceil(n).toInt().coerceAtLeast(1)
            val propias = (0 until k).map { i -> Dosis(a + if (k == 1) 0 else tramo * i / (k - 1), (if (i == k - 1) n - (k - 1) else 1.0).coerceAtLeast(0.0) * GRAMOS_BEBIDA) }
            val z = c.fin?.let { Fechas.millis(it) }?.coerceAtLeast(a) ?: (a + tramo)
            val mf = alcLibres.filter { it.ms in (a - MISMA_TOMA)..(z + MISMA_TOMA) }
            if (mf.sumOf { it.cantidad } > propias.sumOf { it.cantidad }) emptyList<Dosis>() else { alcLibres.removeAll(mf); propias }
        }
        return (cafLibres + cafPropia).sortedBy { it.ms } to (alcLibres + alcPropio).sortedBy { it.ms }
    }

    /** Cafeína en el cuerpo (mg) en cada instante pedido. */
    fun cafeinaEn(dosis: List<Dosis>, instantes: List<Long>, vidaMediaH: Double = Repo.config.cafeina_vida_media_h): List<Double> = instantes.map { t ->
        dosis.sumOf { d ->
            val dt = (t - d.ms).toDouble()
            if (dt <= 0) 0.0 else d.cantidad * (dt / (45.0 * MIN)).coerceAtMost(1.0) * Math.pow(0.5, dt / (vidaMediaH * H))
        }
    }

    /** Peso que se usa si no hay ninguno registrado (kg). */
    const val PESO_OMISION = 70.0

    /** Todos los pesos con fecha, de la báscula (Health Connect) y de la tendencia de MacroFactor, ordenados (el mismo día, la báscula después). */
    fun pesos(): List<Pair<String, Double>> =
        (Repo.nutricion.values.mapNotNull { n -> n.valores["peso_tendencia_kg"]?.takeIf { it > 0 }?.let { n.fecha to it } } +
            Repo.salud.values.mapNotNull { s -> s.peso_kg?.takeIf { it > 0 }?.let { s.fecha to it } }).sortedBy { it.first }

    /**
     * El peso de [fecha] (P-20): el más reciente hasta ese día entre las dos fuentes (una pesada vieja de la báscula ya no le gana
     * a la tendencia de ayer); si no hay ninguno anterior, el primero posterior. Null si no hay ningún peso.
     */
    fun pesoDe(fecha: String, pesos: List<Pair<String, Double>>): Double? = pesos.lastOrNull { it.first <= fecha }?.second ?: pesos.firstOrNull()?.second

    /** Peso (kg) de ese día ([pesoDe]); [PESO_OMISION] si no hay ninguno. */
    fun pesoEn(fecha: String): Double = pesoDe(fecha, pesos()) ?: PESO_OMISION

    private fun sexo(c: Config = Repo.config) = c.perfil.sexo.ifBlank { c.salud_sexo }.lowercase()

    /** Coeficiente de Widmark según el sexo que dijiste (0.68 hombre, 0.55 mujer, 0.62 si no). */
    fun widmarkR(sexo: String = sexo()): Double = when (sexo) { "hombre" -> 0.68; "mujer" -> 0.55; else -> 0.62 }

    /** Proporción de agua en la sangre que usa la fórmula de Watson para pasar del agua corporal a r (Watson et al., 1981). */
    const val AGUA_SANGRE = 0.80

    /**
     * La r de Widmark a partir del agua corporal total de Watson et al. (1980) (I-2): r = agua / (0.80 · peso), acotada entre 0.45
     * y 0.95. Hombres: agua = 2.447 − 0.09516·edad + 0.1074·estatura + 0.3362·peso; mujeres: −2.097 + 0.1069·estatura + 0.2466·peso.
     * Null si falta algo (en los hombres, la edad) o la estatura no está entre 100 y 250 cm: entonces se usa [widmarkR].
     */
    fun watsonR(sexo: String, edad: Int?, estaturaCm: Int?, pesoKg: Double): Double? {
        val h = estaturaCm?.takeIf { it in 100..250 }?.toDouble() ?: return null
        if (pesoKg <= 0) return null
        val agua = when (sexo) {
            "hombre" -> 2.447 - 0.09516 * (edad ?: return null) + 0.1074 * h + 0.3362 * pesoKg
            "mujer" -> -2.097 + 0.1069 * h + 0.2466 * pesoKg
            else -> return null
        }
        return (agua / (AGUA_SANGRE * pesoKg)).coerceIn(0.45, 0.95)
    }

    /**
     * La r y el peso de un día: con un peso real ese día ([pesoDe]), la r de Watson si se puede; con el peso por omisión, siempre
     * la r fija por sexo (Watson con un peso inventado no corrige nada).
     */
    fun rYPeso(fecha: String, pesos: List<Pair<String, Double>>, c: Config = Repo.config): Pair<Double, Double> {
        val w = pesoDe(fecha, pesos) ?: return widmarkR(sexo(c)) to PESO_OMISION
        val edad = Perfiles.edad(fecha, c.perfil)
        return (watsonR(sexo(c), edad, c.perfil.estatura_cm, w) ?: widmarkR(sexo(c))) to w
    }

    /**
     * Alcohol en sangre estimado (g/L ≈ ‰) en cada instante (ordenados). Simulación en pasos de 5 minutos:
     * cada dosis entra en 30 minutos y el cuerpo elimina 0.15 g/L por hora. El peso y la r son los de cada día ([rYPeso]).
     */
    fun alcoholEn(dosis: List<Dosis>, instantes: List<Long>): List<Double> {
        if (dosis.isEmpty() || instantes.isEmpty()) return instantes.map { 0.0 }
        val paso = 5 * MIN
        val todosPesos = pesos()
        val porDia = HashMap<String, Pair<Double, Double>>()
        val salida = HashMap<Long, Double>()
        val orden = instantes.sorted()
        var t = dosis.first().ms
        var g = 0.0
        var i = 0
        val fin = orden.last()
        while (i < orden.size && orden[i] < t) { salida[orden[i]] = 0.0; i++ }
        while (t <= fin) {
            val (r, w) = porDia.getOrPut(Fechas.diaLogicoDeMillis(t)) { rYPeso(Fechas.diaLogicoDeMillis(t), todosPesos) }
            for (d in dosis) {
                val a = t - d.ms
                if (a in 0 until 30 * MIN) g += d.cantidad * paso / (30.0 * MIN)
            }
            g = (g - 0.15 * r * w * paso / H).coerceAtLeast(0.0)
            val c = g / (r * w)
            while (i < orden.size && orden[i] < t + paso) { salida[orden[i]] = c; i++ }
            // Sin alcohol en el cuerpo ni dosis por venir: salta a la siguiente dosis.
            if (g == 0.0) {
                val sig = dosis.firstOrNull { it.ms > t }?.ms ?: break
                while (i < orden.size && orden[i] < sig) { salida[orden[i]] = 0.0; i++ }
                t = sig; continue
            }
            t += paso
        }
        return instantes.map { salida[it] ?: 0.0 }
    }

    /** Alcohol en sangre máximo estimado (g/L) de un episodio, con Widmark; null si no hay bebidas con que calcularlo. */
    fun alcoholMaximo(c: Consumo, alc: List<Dosis>? = null): Double? {
        if (c.sustancia != "alcohol") return null
        val todas = alc ?: dosis(c.fecha, Fechas.sumar(c.fecha, 1)).second
        val ini = Fechas.millis(c.inicio)
        val fin = finEfectivo(c) + 3 * H
        val propias = todas.filter { it.ms in (ini - H)..fin }
        if (propias.isEmpty()) return null
        val instantes = (ini..fin step 5 * MIN).toList()
        return alcoholEn(propias, instantes).maxOrNull()?.takeIf { it > 0 }
    }

    // ---------------------------------------------------------------- por día (para el panel)

    /** Los consumos de un día lógico (por la hora en que empezaron). */
    fun delDia(fecha: String) = Repo.consumos.filter { it.fecha == fecha }
}
