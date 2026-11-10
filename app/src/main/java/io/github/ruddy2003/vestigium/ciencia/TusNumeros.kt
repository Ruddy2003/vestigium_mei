package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.privado
import android.content.Context
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Tus números de ahora: el promedio de los últimos 30 días con datos de cada cosa (dentro del último año), para compararte
 * con las recomendaciones de salud y con personas de otras épocas. Si algo se registró hace semanas (una importación
 * de MacroFactor, por ejemplo), se usan esos 30 días y se dice de cuándo son.
 */
object TusNumeros {
    class Valor(val v: Double, val dias: Int, val desde: String, val hasta: String) {
        val reciente: Boolean get() = Fechas.diasEntre(hasta, Fechas.diaLogico()) <= 45
    }

    @Volatile private var cache: Pair<String, Map<String, Valor>>? = null
    /** En las capturas de diseño todo se calcula de una vez, sin esperar a otro hilo. */
    @Volatile var sincrono = false

    private fun firma() = "${Fechas.diaLogico()}|${Repo.dias.size}|${Repo.salud.size}|${Repo.nutricion.size}|${Repo.entrenos.size}|${Repo.respuestas.size}|${Repo.obras.size}|${Repo.config.perfil}|${Mundo.version}"

    fun calcular(ctx: Context, ventana: Int = 30): Map<String, Valor> {
        val f = firma()
        cache?.let { if (it.first == f) return it.second }
        val out = construir(ctx, ventana)
        cache = f to out
        return out
    }

    /** Promedio de los últimos [n] días que tengan dato (en el último año). */
    private fun ultimos(serie: Map<String, Double?>, n: Int, hoy: String): Valor? {
        val desde = Fechas.sumar(hoy, -365)
        val l = serie.filter { (k, v) -> v != null && !v.isNaN() && k in desde..hoy }.toSortedMap().entries.toList().takeLast(n)
        if (l.isEmpty()) return null
        return Valor(l.sumOf { it.value!! } / l.size, l.size, l.first().key, l.last().key)
    }

    private fun construir(ctx: Context, n: Int): Map<String, Valor> {
        val hoy = Fechas.diaLogico()
        val panel = runCatching { Estadistica.panel(dinamicas = false) }.getOrDefault(emptyMap())
        fun col(id: String, f: (Double) -> Double = { it }): Map<String, Double?> = panel.mapValues { (_, r) -> r[id]?.let(f) }
        val out = LinkedHashMap<String, Valor>()
        fun pon(k: String, v: Valor?) { if (v != null) out[k] = v }

        pon("pasos", ultimos(col("pasos"), n, hoy))
        pon("km_dia", ultimos(col("distancia_km"), n, hoy))
        out["km_dia"]?.let { out["km_anio"] = Valor(it.v * 365, it.dias, it.desde, it.hasta) }
        pon("kcal", ultimos(col("kcal"), n, hoy))
        pon("proteina", ultimos(col("proteina_g"), n, hoy))
        pon("fibra", ultimos(col("fibra_g"), n, hoy))
        pon("alcohol", ultimos(col("alcohol_g"), n, hoy))
        pon("cafeina", ultimos(col("cafeina_mg"), n, hoy))
        pon("carbohidratos", ultimos(col("carbohidratos_g"), n, hoy))
        pon("grasa", ultimos(col("grasa_g"), n, hoy))
        // Nutrientes que vienen tal cual de MacroFactor.
        fun nut(clave: String): Map<String, Double?> = Repo.nutricion.mapValues { (_, d) -> d.valores[clave] }
        val anadido = ultimos(nut("azucares_anadidos_g"), n, hoy)
        if (anadido != null) { out["azucar"] = anadido; out["azucar_anadida"] = Valor(1.0, 0, "", "") }
        else pon("azucar", ultimos(col("azucares_g"), n, hoy))
        pon("sodio", ultimos(nut("sodio_mg"), n, hoy))
        pon("potasio", ultimos(nut("potasio_mg"), n, hoy))
        pon("agua", ultimos(nut("agua_g"), n, hoy)?.let { Valor(it.v / 1000, it.dias, it.desde, it.hasta) }
            ?: ultimos(Repo.salud.mapValues { it.value.agua_ml?.div(1000) }, n, hoy))
        // Porcentajes de la energía (solo los días con calorías registradas).
        fun pct(clave: String, kcalPorG: Double): Valor? = ultimos(Repo.nutricion.mapValues { (_, d) ->
            val k = d.valores["kcal"]; val g = d.valores[clave]; if (k != null && k > 300 && g != null) g * kcalPorG * 100 / k else null }, n, hoy)
        pon("grasa_sat_pct", pct("grasa_saturada_g", 9.0))
        pon("grasa_pct", pct("grasa_g", 9.0))
        Repo.nutricion.values.flatMap { it.valores.keys }.firstOrNull { it.contains("trans") }?.let { k -> pon("trans_pct", pct(k, 9.0)) }
        pon("azucar_pct", if (anadido != null) pct("azucares_anadidos_g", 4.0) else null)
        // Sueño.
        pon("sueno", ultimos(col("sueno_horas"), n, hoy))
        pon("dormir", ultimos(col("sueno_inicio_h"), n, hoy))
        pon("despertar", ultimos(col("sueno_fin_h"), n, hoy))
        // Movimiento.
        // Minutos de ejercicio por semana: la suma de las últimas 4 semanas con sesiones, entre 4 (P-23). Promediar solo los días
        // con sesión y multiplicar por 7 daba 315 min con tres sesiones de 45.
        val sesiones = Repo.sesiones.filter { it.min > 0 }
        if (sesiones.isNotEmpty()) {
            val fin = sesiones.maxOf { it.fecha }; val ini = Fechas.sumar(fin, -27)
            val en = sesiones.filter { it.fecha in ini..fin }
            out["ejercicio_sem"] = Valor(en.sumOf { it.min } / 4.0, en.map { it.fecha }.distinct().size, ini, fin)
        }
        // Días de fuerza por semana: en las últimas 4 semanas con entrenos.
        val fuerza = Repo.entrenos.values.filter { it.series > 0 }.map { it.fecha }.sorted()
        if (fuerza.isNotEmpty()) {
            val fin = fuerza.last(); val ini = Fechas.sumar(fin, -27)
            out["fuerza_sem"] = Valor(fuerza.count { it in ini..fin } / 4.0, fuerza.count { it in ini..fin }, ini, fin)
        }
        pon("fc_reposo", ultimos(col("fc_reposo"), n, hoy))
        pon("peso", ultimos(col("peso_kg"), n, hoy) ?: ultimos(col("peso_tendencia_kg"), n, hoy))
        pon("pa_sis", ultimos(Repo.salud.mapValues { it.value.pa_sistolica }, n, hoy))
        pon("pa_dia", ultimos(Repo.salud.mapValues { it.value.pa_diastolica }, n, hoy))
        pon("pm25", ultimos(col("pm25"), n, hoy))
        // El teléfono y el día.
        pon("pantalla", ultimos(col("pantalla_min") { it / 60 }, n, hoy))
        pon("notificaciones", ultimos(col("notificaciones"), n, hoy))
        pon("musica", ultimos(col("musica_min") { it / 60 }, n, hoy))
        pon("aire_libre", ultimos(col("luz_alta_min") { it / 60 }, n, hoy))
        pon("personas_dia", ultimos(panel.mapValues { (_, r) -> listOfNotNull(r["personas"], r["contactos"], r["wa_chats"]).maxOrNull() }, n, hoy))
        // Cuestionarios: el último de cada uno.
        listOf("who5" to "porcentaje", "ucla3" to "total", "pss4" to "total").forEach { (ins, p) ->
            Repo.ultimaRespuesta(ins)?.let { r -> r.puntajes[p]?.let { out[ins] = Valor(it, 1, r.fecha, r.fecha) } }
        }
        // Lo que no es por día.
        // Las personas distintas que aparecen, con la cuenta única (Personas.diasCon: aplica las uniones de nombres).
        val gente = io.github.ruddy2003.vestigium.data.Personas.diasCon().keys
        if (gente.isNotEmpty()) out["conocidas"] = Valor(gente.size.toDouble(), 0, "", hoy)
        val hace = Fechas.sumar(hoy, -365)
        Repo.obras.count { it.tipo == "libro" && it.estado == "terminado" && (it.fin ?: "") >= hace }.takeIf { Repo.obras.any { o -> o.tipo == "libro" } }
            ?.let { out["lectura"] = Valor(it.toDouble(), 0, hace, hoy) }
        val p = Repo.config.perfil
        if (p.nacimiento.length >= 4) runCatching {
            val e = java.time.Period.between(Fechas.parse(p.nacimiento.take(10).let { if (it.length == 4) "$it-07-01" else it }), Fechas.parse(hoy)).years
            out["edad"] = Valor(e.toDouble(), 0, "", hoy)
        }
        // Tu ciudad: cuánta gente vive ahí, y lo más lejos que has estado de ella.
        runCatching {
            if (p.ciudad.isNotBlank()) {
                val c = Mundo.ciudades(ctx)
                Mundo.buscarCiudad(ctx, p.ciudad, 6).firstOrNull { c.pais(it) == p.pais || p.pais.isBlank() }?.let { i ->
                    val raiz = c.raiz(i); val pob = maxOf(c.pob[i], c.pob[raiz])
                    if (pob > 0) out["poblacion"] = Valor(pob.toDouble(), 0, "", hoy)
                }
            }
            val r = Mundo.resumen(ctx)
            val casaLat = p.ciudad_lat ?: r.ciudades.firstOrNull()?.lat; val casaLon = p.ciudad_lon ?: r.ciudades.firstOrNull()?.lon
            if (casaLat != null && casaLon != null && r.ciudades.isNotEmpty())
                out["lejos"] = Valor(r.ciudades.maxOf { km(casaLat, casaLon, it.lat, it.lon) }, 0, "", hoy)
        }
        return out
    }

    fun km(la1: Double, lo1: Double, la2: Double, lo2: Double): Double {
        val r = PI / 180
        val a = sin((la2 - la1) * r / 2).let { it * it } + cos(la1 * r) * cos(la2 * r) * sin((lo2 - lo1) * r / 2).let { it * it }
        return 2 * 6371 * asin(sqrt(a.coerceIn(0.0, 1.0)))
    }
}
