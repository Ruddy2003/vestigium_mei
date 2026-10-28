package io.github.ruddy2003.vestigium.sensores

import io.github.ruddy2003.vestigium.data.privado
import android.content.Context
import io.github.ruddy2003.vestigium.data.EntornoDia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Luna
import io.github.ruddy2003.vestigium.data.Red
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/**
 * Clima, luz del día y calidad del aire de Open-Meteo (gratuito, sin cuenta).
 * Solo se envían coordenadas redondeadas a ~1 km; nunca tu ruta ni datos personales.
 */
object Entorno {
    private const val PRONOSTICO = "https://api.open-meteo.com/v1/forecast"
    private const val ARCHIVO = "https://archive-api.open-meteo.com/v1/archive"
    private const val AIRE = "https://air-quality-api.open-meteo.com/v1/air-quality"

    data class Punto(val lat: Double, val lon: Double)

    /** Dónde estuviste ese día: la mediana de tus ubicaciones; si no hay, la de tus momentos; si no, la última conocida. */
    fun lugarDe(fecha: String, respaldo: Punto?): Punto? {
        val pts = Repo.db().ubicaciones(Fechas.inicioDia(fecha), Fechas.finDia(fecha))
        // Las coordenadas de un momento privado no salen hacia el servicio del clima.
        val lats = pts.map { it.lat }.ifEmpty { Repo.momentos.filter { it.fecha == fecha && !it.privado }.mapNotNull { it.lat } }
        val lons = pts.map { it.lon }.ifEmpty { Repo.momentos.filter { it.fecha == fecha && !it.privado }.mapNotNull { it.lon } }
        if (lats.isEmpty() || lons.isEmpty()) return respaldo
        return Punto(mediana(lats), mediana(lons))
    }

    /** Pasados estos días, el clima se pide al archivo histórico (reanálisis); antes, a los modelos de pronóstico. */
    const val DIAS_ARCHIVO = 80

    /**
     * De dónde salió el clima del día (H-3-4): «archivo» (reanálisis) o «pronostico». Lo guardado antes del esquema 15 no lo
     * dice: se deduce de cuántos días habían pasado cuando se pidió ([EntornoDia.ts]), con la misma regla de [DIAS_ARCHIVO].
     */
    fun fuente(e: EntornoDia): String? = e.fuente ?: e.ts?.let { t ->
        runCatching { if (ChronoUnit.DAYS.between(Fechas.parse(e.fecha), Fechas.parse(t)) > DIAS_ARCHIVO) "archivo" else "pronostico" }.getOrNull()
    }

    private fun mediana(x: List<Double>): Double = x.sorted().let { it[it.size / 2] }
    private fun r2(x: Double) = (x * 100).roundToInt() / 100.0

    /** Rellena los días que faltan (hasta un año atrás) y actualiza los recientes. Devuelve cuántos días quedaron. */
    suspend fun sincronizar(ctx: Context, maxPeticiones: Int = 8): Int {
        if (!Repo.config.clima) return 0
        val hoy = Fechas.hoyCalendario()
        val ultimo = Ubicacion.ultima(ctx)?.let { Punto(it.latitude, it.longitude) }
            ?: Repo.db().ubicaciones(System.currentTimeMillis() - 30L * 86_400_000).lastOrNull()?.let { Punto(it.lat, it.lon) }
            ?: Repo.momentos.lastOrNull { it.lat != null && !it.privado }?.let { Punto(it.lat!!, it.lon!!) }
            ?: return 0
        // Días que interesan: los últimos 7, más cualquier día con registro que no tenga entorno, hasta 365 días atrás.
        val limite = Fechas.sumar(hoy, -365)
        val conRegistro = (Repo.dias.keys + Repo.salud.keys + Repo.momentos.map { it.fecha }).filter { it >= limite && it <= hoy }
        val recientes = (0..7).map { Fechas.sumar(hoy, -it.toLong()) }
        // También los días que vinieron del pronóstico y ya están en el archivo histórico: se cambian por el reanálisis, para que
        // la serie no mezcle las dos fuentes (H-3-4).
        fun viejo(f: String) = ChronoUnit.DAYS.between(Fechas.parse(f), Fechas.parse(hoy)) > DIAS_ARCHIVO
        val pendientes = (conRegistro + recientes).toSortedSet().filter { f -> Repo.entorno[f].let { e -> e == null || !e.completo || (viejo(f) && fuente(e) == "pronostico") } }
        if (pendientes.isEmpty()) return 0

        // Agrupa días seguidos que estén en la misma zona (~10 km) y usen la misma fuente.
        data class Tramo(val desde: String, var hasta: String, val p: Punto, val archivo: Boolean)
        val tramos = ArrayList<Tramo>()
        for (f in pendientes) {
            val p = lugarDe(f, ultimo) ?: continue
            val pr = Punto(r2(p.lat), r2(p.lon))
            val archivo = viejo(f)
            val t = tramos.lastOrNull()
            if (t != null && t.archivo == archivo && Fechas.sumar(t.hasta, 1) == f && ChronoUnit.DAYS.between(Fechas.parse(t.desde), Fechas.parse(f)) < 60 &&
                kotlin.math.abs(t.p.lat - pr.lat) < 0.1 && kotlin.math.abs(t.p.lon - pr.lon) < 0.1) t.hasta = f
            else tramos.add(Tramo(f, f, pr, archivo))
        }
        var n = 0
        // Primero lo más reciente.
        for (t in tramos.sortedByDescending { it.hasta }.take(maxPeticiones)) {
            val dias = runCatching { pedir(t.desde, t.hasta, t.p, t.archivo) }.getOrNull() ?: continue
            val aire = runCatching { aire(t.desde, t.hasta, t.p) }.getOrDefault(emptyMap())
            for (e in dias) {
                val a = aire[e.fecha]
                val f = Luna.fase(e.fecha)
                // El archivo no trae el índice UV y el aire puede no responder: lo que el día ya tenía se conserva.
                val antes = Repo.entorno[e.fecha]
                Repo.guardarEntorno(e.copy(pm25 = a?.first ?: antes?.pm25, pm10 = a?.second ?: antes?.pm10, aqi_us = a?.third ?: antes?.aqi_us,
                    luna_fase = f, luna_ilum = Luna.iluminacion(f), uv_max = e.uv_max ?: antes?.uv_max, fuente = if (t.archivo) "archivo" else "pronostico",
                    completo = e.fecha < Fechas.sumar(hoy, -1) && (t.archivo || ChronoUnit.DAYS.between(Fechas.parse(e.fecha), Fechas.parse(hoy)) >= 2),
                    ts = Fechas.ahoraIso()))
                n++
            }
        }
        return n
    }

    private fun lista(o: JsonObject?, k: String): List<Double?> =
        (o?.get(k) as? JsonArray)?.map { (it as? JsonPrimitive)?.doubleOrNull } ?: emptyList()
    private fun textos(o: JsonObject?, k: String): List<String?> =
        (o?.get(k) as? JsonArray)?.map { (it as? JsonPrimitive)?.takeIf { p -> p.isString }?.content } ?: emptyList()

    private fun pedir(desde: String, hasta: String, p: Punto, archivo: Boolean): List<EntornoDia> {
        val diarios = mutableListOf("weather_code", "temperature_2m_max", "temperature_2m_min", "apparent_temperature_max", "precipitation_sum",
            "precipitation_hours", "sunrise", "sunset", "daylight_duration", "sunshine_duration", "wind_speed_10m_max")
        if (!archivo) diarios += "uv_index_max"
        val url = (if (archivo) ARCHIVO else PRONOSTICO) + "?latitude=${p.lat}&longitude=${p.lon}&timezone=auto" +
            "&start_date=$desde&end_date=$hasta&daily=${diarios.joinToString(",")}" +
            "&hourly=temperature_2m,relative_humidity_2m,cloud_cover,surface_pressure"
        val raiz = JSON.parseToJsonElement(Red.get(url)).jsonObject
        val d = raiz["daily"] as? JsonObject ?: return emptyList()
        val h = raiz["hourly"] as? JsonObject
        val fechas = textos(d, "time")
        val horas = textos(h, "time")
        fun mediaDe(clave: String, fecha: String): Double? {
            val v = lista(h, clave)
            val xs = horas.indices.filter { horas[it]?.startsWith(fecha) == true }.mapNotNull { v.getOrNull(it) }
            return if (xs.isEmpty()) null else (xs.average() * 10).roundToInt() / 10.0
        }
        val tmax = lista(d, "temperature_2m_max"); val tmin = lista(d, "temperature_2m_min"); val sens = lista(d, "apparent_temperature_max")
        val prec = lista(d, "precipitation_sum"); val hprec = lista(d, "precipitation_hours"); val cod = lista(d, "weather_code")
        val luz = lista(d, "daylight_duration"); val sol = lista(d, "sunshine_duration"); val viento = lista(d, "wind_speed_10m_max")
        val uv = lista(d, "uv_index_max"); val ama = textos(d, "sunrise"); val ata = textos(d, "sunset")
        return fechas.mapIndexedNotNull { i, f ->
            f ?: return@mapIndexedNotNull null
            if (tmax.getOrNull(i) == null && cod.getOrNull(i) == null) return@mapIndexedNotNull null
            EntornoDia(
                fecha = f, lat = p.lat, lon = p.lon,
                t_max = tmax.getOrNull(i), t_min = tmin.getOrNull(i), t_media = mediaDe("temperature_2m", f), sensacion_max = sens.getOrNull(i),
                precip_mm = prec.getOrNull(i), horas_lluvia = hprec.getOrNull(i)?.roundToInt(), nubes_pct = mediaDe("cloud_cover", f),
                humedad_pct = mediaDe("relative_humidity_2m", f), presion_hpa = mediaDe("surface_pressure", f), viento_max = viento.getOrNull(i),
                uv_max = uv.getOrNull(i), codigo = cod.getOrNull(i)?.roundToInt(),
                amanecer = ama.getOrNull(i)?.substringAfter('T'), atardecer = ata.getOrNull(i)?.substringAfter('T'),
                luz_min = luz.getOrNull(i)?.let { (it / 60).roundToInt() }, sol_min = sol.getOrNull(i)?.let { (it / 60).roundToInt() },
            )
        }
    }

    /** PM2.5 y PM10 promedio, e índice de calidad del aire (EE. UU.) máximo del día. */
    private fun aire(desde: String, hasta: String, p: Punto): Map<String, Triple<Double?, Double?, Int?>> {
        val url = "$AIRE?latitude=${p.lat}&longitude=${p.lon}&timezone=auto&start_date=$desde&end_date=$hasta&hourly=pm2_5,pm10,us_aqi"
        val h = JSON.parseToJsonElement(Red.get(url)).jsonObject["hourly"] as? JsonObject ?: return emptyMap()
        val t = textos(h, "time"); val p25 = lista(h, "pm2_5"); val p10 = lista(h, "pm10"); val aqi = lista(h, "us_aqi")
        return t.indices.groupBy { t[it]?.take(10) ?: "" }.filterKeys { it.isNotEmpty() }.mapValues { (_, idx) ->
            val a = idx.mapNotNull { p25.getOrNull(it) }; val b = idx.mapNotNull { p10.getOrNull(it) }; val c = idx.mapNotNull { aqi.getOrNull(it) }
            Triple(if (a.isEmpty()) null else (a.average() * 10).roundToInt() / 10.0, if (b.isEmpty()) null else (b.average() * 10).roundToInt() / 10.0, c.maxOrNull()?.roundToInt())
        }
    }
}
