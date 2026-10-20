package io.github.ruddy2003.vestigium.data

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Consumo de sustancias: episodios con nivel subjetivo (0 a 10), y dos estimaciones farmacológicas:
 * - Cafeína en el cuerpo (mg): cada dosis se absorbe en ~45 min y se elimina con la vida media que elijas (5 h por omisión).
 * - Alcohol en sangre (g/L, Widmark): absorción en ~30 min y eliminación constante de 0.15 g/L por hora.
 * Las dosis salen de MacroFactor (cada alimento con su hora) y, los días en que MacroFactor no tiene nada de esa sustancia,
 * de lo que anotes aquí. Así no se cuenta dos veces.
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
            nivel_max = nivel, cantidad = cantidad, via = via, con_quien = conQuien, completo = !conNivel, origen = origen)
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
        LocalDateTime.of(Fechas.parse(fecha), LocalTime.parse(hora.take(5))).atZone(Fechas.zona()).toInstant().toEpochMilli()
    }.getOrNull()

    /**
     * Cafeína (mg) y alcohol (g) con su hora entre dos días (de calendario), de MacroFactor y de tus registros.
     * Por día y sustancia se usa una sola fuente: si MacroFactor tiene algo ese día, manda MacroFactor.
     */
    fun dosis(desde: String, hasta: String, comidas: List<Comida>? = null): Pair<List<Dosis>, List<Dosis>> {
        val cs = comidas ?: runCatching { Repo.comidas(desde, hasta) }.getOrDefault(emptyList())
        val cafMf = cs.mapNotNull { c -> c.valores["cafeina_mg"]?.takeIf { it > 0 }?.let { v -> msDe(c.fecha, c.hora)?.let { Dosis(it, v) } } }
        val alcMf = cs.mapNotNull { c -> c.valores["alcohol_g"]?.takeIf { it > 0 }?.let { v -> msDe(c.fecha, c.hora)?.let { Dosis(it, v) } } }
        val diasCaf = cafMf.map { Fechas.diaLogicoDeMillis(it.ms) }.toSet()
        val diasAlc = alcMf.map { Fechas.diaLogicoDeMillis(it.ms) }.toSet()
        val ini = Fechas.inicioDia(desde) - 12 * H; val fin = Fechas.finDia(hasta) + 6 * H
        val propios = Repo.consumos.filter { Fechas.millis(it.inicio) in ini..fin }
        val cafPropia = propios.filter { it.sustancia == "cafeina" && it.fecha !in diasCaf && (it.cantidad ?: 0.0) > 0 }.map { Dosis(Fechas.millis(it.inicio), it.cantidad!!) }
        // Las bebidas de un episodio se reparten a lo largo de la primera parte (o una cada 40 minutos si no hay fin).
        val alcPropio = propios.filter { it.sustancia == "alcohol" && it.fecha !in diasAlc && (it.cantidad ?: 0.0) > 0 }.flatMap { c ->
            val n = c.cantidad!!; val a = Fechas.millis(c.inicio)
            val tramo = c.fin?.let { ((Fechas.millis(it) - a) * 0.7).toLong() }?.takeIf { it > 0 } ?: (40 * MIN * (n - 1).coerceAtLeast(0.0)).toLong()
            val k = kotlin.math.ceil(n).toInt().coerceAtLeast(1)
            (0 until k).map { i -> Dosis(a + if (k == 1) 0 else tramo * i / (k - 1), (if (i == k - 1) n - (k - 1) else 1.0).coerceAtLeast(0.0) * GRAMOS_BEBIDA) }
        }
        return (cafMf + cafPropia).sortedBy { it.ms } to (alcMf + alcPropio).sortedBy { it.ms }
    }

    /** Cafeína en el cuerpo (mg) en cada instante pedido. */
    fun cafeinaEn(dosis: List<Dosis>, instantes: List<Long>, vidaMediaH: Double = Repo.config.cafeina_vida_media_h): List<Double> = instantes.map { t ->
        dosis.sumOf { d ->
            val dt = (t - d.ms).toDouble()
            if (dt <= 0) 0.0 else d.cantidad * (dt / (45.0 * MIN)).coerceAtMost(1.0) * Math.pow(0.5, dt / (vidaMediaH * H))
        }
    }

    /** Peso (kg) más reciente hasta ese día, de la báscula o de MacroFactor; 70 si no hay. */
    fun pesoEn(fecha: String): Double =
        (Repo.salud.values.filter { it.fecha <= fecha && (it.peso_kg ?: 0.0) > 0 }.maxByOrNull { it.fecha }?.peso_kg
            ?: Repo.nutricion.values.filter { it.fecha <= fecha && (it.valores["peso_tendencia_kg"] ?: 0.0) > 0 }.maxByOrNull { it.fecha }?.valores?.get("peso_tendencia_kg"))
            ?: 70.0

    /** Coeficiente de Widmark según el sexo que dijiste (0.68 hombre, 0.55 mujer, 0.62 si no). */
    fun widmarkR(): Double = when ((Repo.config.perfil.sexo.ifBlank { Repo.config.salud_sexo }).lowercase()) { "hombre" -> 0.68; "mujer" -> 0.55; else -> 0.62 }

    /**
     * Alcohol en sangre estimado (g/L ≈ ‰) en cada instante (ordenados). Simulación en pasos de 5 minutos:
     * cada dosis entra en 30 minutos y el cuerpo elimina 0.15 g/L por hora.
     */
    fun alcoholEn(dosis: List<Dosis>, instantes: List<Long>): List<Double> {
        if (dosis.isEmpty() || instantes.isEmpty()) return instantes.map { 0.0 }
        val paso = 5 * MIN
        val r = widmarkR()
        val salida = HashMap<Long, Double>()
        val orden = instantes.sorted()
        var t = dosis.first().ms
        var g = 0.0
        var i = 0
        val fin = orden.last()
        while (i < orden.size && orden[i] < t) { salida[orden[i]] = 0.0; i++ }
        while (t <= fin) {
            val w = pesoEn(Fechas.diaLogicoDeMillis(t))
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
