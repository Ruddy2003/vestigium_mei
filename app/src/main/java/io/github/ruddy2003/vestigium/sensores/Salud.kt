package io.github.ruddy2003.vestigium.sensores

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyFatRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.Vo2MaxRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.HeartRateVariabilityRmssdRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.records.RestingHeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.SaludDia
import io.github.ruddy2003.vestigium.data.Sesion
import java.time.Instant
import java.time.ZoneId

/** Lectura de Health Connect: sueño, corazón, pasos y oxígeno que escribe el reloj Samsung. */
object Salud {
    val PERMISOS: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(BodyFatRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class),
        HealthPermission.getReadPermission(SkinTemperatureRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(FloorsClimbedRecord::class),
        HealthPermission.getReadPermission(Vo2MaxRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND",
        "android.permission.health.READ_HEALTH_DATA_HISTORY",
    )

    fun disponible(ctx: Context): Boolean =
        HealthConnectClient.getSdkStatus(ctx) == HealthConnectClient.SDK_AVAILABLE

    suspend fun concedidos(ctx: Context): Set<String> =
        if (!disponible(ctx)) emptySet() else try {
            HealthConnectClient.getOrCreate(ctx).permissionController.getGrantedPermissions()
        } catch (e: Exception) { emptySet() }

    /** Lee todas las páginas (Health Connect devuelve de a 1000). */
    private suspend fun <T : androidx.health.connect.client.records.Record> leer(cliente: HealthConnectClient, tipo: kotlin.reflect.KClass<T>, rango: TimeRangeFilter, max: Int = 6000): List<T> {
        val out = ArrayList<T>()
        var token: String? = null
        do {
            val r = cliente.readRecords(ReadRecordsRequest(recordType = tipo, timeRangeFilter = rango, pageSize = 1000, pageToken = token))
            out += r.records; token = r.pageToken
        } while (token != null && out.size < max)
        return out
    }

    /** Nombre corto de la app que escribió el dato. */
    fun fuente(pkg: String): String = when {
        pkg.contains("shealth") || pkg.contains("samsung") -> "Samsung Health"
        pkg.contains("apps.fitness") -> "Google Fit"
        pkg.contains("urbandroid") -> "Sleep as Android"
        pkg.contains("fitbit") -> "Fitbit"
        pkg.contains("garmin") -> "Garmin"
        pkg.contains("healthdata") -> "Health Connect"
        else -> pkg.substringAfterLast('.')
    }

    private class Bloque(var ini: Instant, var fin: Instant, val sesiones: MutableList<SleepSessionRecord> = ArrayList())

    private class Noche(val asleep: Long, val prof: Long, val rem: Long, val lig: Long, val desp: Long, val etapas: Boolean)

    /**
     * Cuánto dormiste en un bloque, en segundos (no en minutos por etapa: Samsung escribe etapas de pocos minutos y
     * truncar cada una hacía perder hasta una hora). Si las etapas cubren al menos 60 % de la sesión se suman las de sueño;
     * si no, se toma la sesión entera menos lo despierto. Las sesiones que se enciman dentro del bloque cuentan una sola vez.
     */
    private fun noche(b: Bloque): Noche {
        var asleep = 0L; var prof = 0L; var rem = 0L; var lig = 0L; var desp = 0L; var etapas = false
        // La más larga primero; una que se encime más de 10 minutos con otra ya contada se descarta.
        val usadas = ArrayList<SleepSessionRecord>()
        b.sesiones.sortedByDescending { java.time.Duration.between(it.startTime, it.endTime).seconds }.forEach { ses ->
            val choca = usadas.any { u -> minOf(u.endTime, ses.endTime).epochSecond - maxOf(u.startTime, ses.startTime).epochSecond > 600 }
            if (!choca) usadas += ses
        }
        for (ses in usadas) {
            val dur = java.time.Duration.between(ses.startTime, ses.endTime).seconds
            var cub = 0L; var dormido = 0L; var p = 0L; var r = 0L; var l = 0L; var d = 0L
            for (st in ses.stages) {
                val m = java.time.Duration.between(st.startTime, st.endTime).seconds.coerceAtLeast(0)
                cub += m
                when (st.stage) {
                    SleepSessionRecord.STAGE_TYPE_DEEP -> { p += m; dormido += m }
                    SleepSessionRecord.STAGE_TYPE_REM -> { r += m; dormido += m }
                    SleepSessionRecord.STAGE_TYPE_LIGHT -> { l += m; dormido += m }
                    SleepSessionRecord.STAGE_TYPE_AWAKE, SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED, SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> d += m
                    else -> dormido += m
                }
            }
            if (ses.stages.isNotEmpty()) etapas = true
            asleep += if (ses.stages.isNotEmpty() && cub >= dur * 0.6) dormido else (dur - d).coerceAtLeast(0)
            prof += p; rem += r; lig += l; desp += d
        }
        return Noche(asleep, prof, rem, lig, desp, etapas)
    }

    private fun hm(t: Instant, zona: ZoneId) = t.atZone(zona).toLocalTime().let { "%02d:%02d".format(it.hour, it.minute) }

    /** Sincroniza los últimos `dias` días. El sueño se asigna al día en que despertaste. */
    suspend fun sincronizar(ctx: Context, dias: Int = 3): Int {
        if (!disponible(ctx)) return 0
        val cliente = HealthConnectClient.getOrCreate(ctx)
        val ok = try { cliente.permissionController.getGrantedPermissions() } catch (e: Exception) { return 0 }
        if (ok.isEmpty()) return 0
        val zona = ZoneId.systemDefault()
        var n = 0
        // Lo que devolvió cada tipo de dato, para «Estado de los datos».
        val diag = LinkedHashMap<String, String>()
        val conDato = HashMap<String, Int>()
        fun bien(k: String) { conDato[k] = (conDato[k] ?: 0) + 1 }
        fun error(k: String, e: Throwable) { diag[k] = "error: " + (e::class.simpleName ?: "?") + (e.message?.take(80)?.let { " · $it" } ?: "") }
        suspend fun intentar(k: String, perm: String, bloque: suspend () -> Unit) {
            if (perm !in ok) { diag[k] = "sin permiso"; return }
            try { bloque() } catch (e: Exception) { error(k, e) }
        }
        for (i in 0 until dias) {
            val fecha = Fechas.sumar(Fechas.hoyCalendario(), -i.toLong())
            val d = Fechas.parse(fecha)
            val ini = d.atStartOfDay(zona).toInstant()
            val fin = d.plusDays(1).atStartOfDay(zona).toInstant()
            val rango = TimeRangeFilter.between(ini, fin)
            var s = Repo.salud[fecha] ?: SaludDia(fecha = fecha)

            // Los pasos, del día lógico (v13): de las 05:00 a las 05:00 del día siguiente; lo de la madrugada es del día anterior.
            val iniPasos = Instant.ofEpochMilli(Fechas.inicioDiaLogico(fecha))
            val finPasos = Instant.ofEpochMilli(minOf(Fechas.finDiaLogico(fecha), System.currentTimeMillis()))
            if (finPasos > iniPasos) intentar("pasos", HealthPermission.getReadPermission(StepsRecord::class)) {
                val ag = cliente.aggregate(AggregateRequest(setOf(StepsRecord.COUNT_TOTAL), TimeRangeFilter.between(iniPasos, finPasos)))
                s = s.copy(pasos = ag[StepsRecord.COUNT_TOTAL], ventana_pasos = "logica"); if (s.pasos != null) bien("pasos")
            }
            intentar("ritmo cardiaco", HealthPermission.getReadPermission(HeartRateRecord::class)) {
                val ag = cliente.aggregate(AggregateRequest(setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_MAX), rango))
                var media = ag[HeartRateRecord.BPM_AVG]?.toDouble(); var min = ag[HeartRateRecord.BPM_MIN]?.toDouble(); var max = ag[HeartRateRecord.BPM_MAX]?.toDouble()
                // Si la agregación no trae nada, se calcula con las muestras (algunas apps solo escriben series sueltas).
                if (media == null) {
                    val m = leer(cliente, HeartRateRecord::class, rango).flatMap { it.samples }.map { it.beatsPerMinute.toDouble() }
                    if (m.isNotEmpty()) { media = m.average(); min = m.min(); max = m.max() }
                }
                s = s.copy(fc_media = r(media, 1), fc_min = min, fc_max = max); if (media != null) bien("ritmo cardiaco")
            }
            intentar("frecuencia en reposo", HealthPermission.getReadPermission(RestingHeartRateRecord::class)) {
                val l = leer(cliente, RestingHeartRateRecord::class, rango)
                if (l.isNotEmpty()) { s = s.copy(fc_reposo = l.map { it.beatsPerMinute.toDouble() }.average()); bien("frecuencia en reposo") }
            }
            intentar("oxígeno", HealthPermission.getReadPermission(OxygenSaturationRecord::class)) {
                val l = leer(cliente, OxygenSaturationRecord::class, rango)
                if (l.isNotEmpty()) { s = s.copy(spo2 = r(l.map { it.percentage.value }.average(), 1)); bien("oxígeno") }
            }
            // El sueño: de las 12:00 del día anterior al final de este; se queda con lo que termina este día.
            val ventana = TimeRangeFilter.between(d.minusDays(1).atTime(12, 0).atZone(zona).toInstant(), fin)
            var bloqueNoche: Bloque? = null
            intentar("sueño", HealthPermission.getReadPermission(SleepSessionRecord::class)) {
                val leidas = leer(cliente, SleepSessionRecord::class, ventana).filter { it.endTime.atZone(zona).toLocalDate() == d && it.endTime > it.startTime }
                fun clave(ses: SleepSessionRecord) = "${fuente(ses.metadata.dataOrigin.packageName)} ${hm(ses.startTime, zona)}–${hm(ses.endTime, zona)}"
                // Lo que marcaste «no cuenta» (la siesta del sofá, una noche que no fue tuya) no entra en la cuenta (N-04).
                val noCuentan = leidas.filter { clave(it) in s.sueno_descartadas }
                val todas = leidas - noCuentan.toSet()
                val fuentesNoCuentan = noCuentan.map { "${clave(it)} · ${it.stages.size} etapas · no cuenta" }
                // Una sola fuente por noche: la que trae etapas (Samsung Health primero). Así no se suma la misma noche dos veces.
                val porFuente = todas.groupBy { it.metadata.dataOrigin.packageName }
                val elegida = porFuente.maxByOrNull { (pkg, l) ->
                    (if (l.any { it.stages.isNotEmpty() }) 4_000_000_000L else 0L) + (if (fuente(pkg) == "Samsung Health") 2_000_000_000L else 0L) +
                        l.sumOf { java.time.Duration.between(it.startTime, it.endTime).seconds }
                }?.key
                val propias = porFuente[elegida].orEmpty().sortedBy { it.startTime }
                // Bloques: sesiones separadas por menos de una hora son la misma noche.
                val bloques = ArrayList<Bloque>()
                for (ses in propias) {
                    val b = bloques.lastOrNull()
                    if (b != null && ses.startTime.epochSecond - b.fin.epochSecond < 3600) { b.sesiones += ses; if (ses.endTime > b.fin) b.fin = ses.endTime }
                    else bloques += Bloque(ses.startTime, ses.endTime, mutableListOf(ses))
                }
                // La noche: el bloque más largo que empezó antes del mediodía; lo demás son siestas.
                val mediodia = d.atTime(12, 0).atZone(zona).toInstant()
                val principal = bloques.filter { it.ini < mediodia }.maxByOrNull { it.fin.epochSecond - it.ini.epochSecond }
                bloqueNoche = principal
                val siestas = bloques.filter { it !== principal }
                val fuentes = todas.sortedBy { it.startTime }.map { ses ->
                    val estado = when {
                        ses.metadata.dataOrigin.packageName != elegida -> "otra fuente, no se suma"
                        principal?.sesiones?.contains(ses) == true -> "noche"
                        else -> "siesta"
                    }
                    "${clave(ses)} · ${ses.stages.size} etapas · $estado"
                } + fuentesNoCuentan
                // Con la noche corregida a mano, la noche no se toca, pero las siestas sí se vuelven a contar (antes se congelaban, H-NAV-3).
                if (s.sueno_manual) {
                    // La variabilidad y el oxígeno de la noche se leen en tus horas corregidas, no en las del reloj.
                    bloqueNoche = null
                    s = s.copy(sueno_fuentes = fuentes, siesta_reloj_min = siestas.sumOf { noche(it).asleep }.takeIf { it > 0 }?.let { Math.round(it / 60.0).toInt() })
                    return@intentar
                }
                if (principal != null) {
                    val nn = noche(principal)
                    fun min(x: Long) = Math.round(x / 60.0).toInt()
                    s = s.copy(
                        sueno_min = min(nn.asleep),
                        sueno_profundo_min = if (nn.etapas) min(nn.prof) else null, sueno_rem_min = if (nn.etapas) min(nn.rem) else null,
                        sueno_ligero_min = if (nn.etapas) min(nn.lig) else null, sueno_despierto_min = if (nn.etapas) min(nn.desp) else null,
                        sueno_inicio = Fechas.isoDeMillis(principal.ini.toEpochMilli()), sueno_fin = Fechas.isoDeMillis(principal.fin.toEpochMilli()),
                        sueno_fuentes = fuentes,
                        siesta_reloj_min = siestas.sumOf { noche(it).asleep }.takeIf { it > 0 }?.let { min(it) },
                    )
                    bien("sueño")
                } else {
                    // La lectura funcionó y no hay noche: se borra lo que hubiera quedado mal de antes.
                    s = s.copy(sueno_min = null, sueno_profundo_min = null, sueno_rem_min = null, sueno_ligero_min = null, sueno_despierto_min = null,
                        sueno_inicio = null, sueno_fin = null, sueno_fuentes = fuentes,
                        siesta_reloj_min = siestas.sumOf { noche(it).asleep }.takeIf { it > 0 }?.let { Math.round(it / 60.0).toInt() })
                }
            }
            // Durante la noche: variabilidad, frecuencia y oxígeno mínimo.
            val rangoNoche = bloqueNoche?.let { TimeRangeFilter.between(it.ini, it.fin) }
                ?: s.sueno_inicio?.let { a -> s.sueno_fin?.let { b -> TimeRangeFilter.between(Instant.ofEpochMilli(Fechas.millis(a)), Instant.ofEpochMilli(Fechas.millis(b))) } }
            intentar("variabilidad (HRV)", HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class)) {
                val l = leer(cliente, HeartRateVariabilityRmssdRecord::class, rangoNoche ?: TimeRangeFilter.between(d.minusDays(1).atTime(18, 0).atZone(zona).toInstant(), d.atTime(14, 0).atZone(zona).toInstant()))
                if (l.isNotEmpty()) { s = s.copy(hrv_rmssd = r(l.map { it.heartRateVariabilityMillis }.average(), 1)); bien("variabilidad (HRV)") }
            }
            if (rangoNoche != null) {
                if (HealthPermission.getReadPermission(HeartRateRecord::class) in ok) runCatching {
                    s = s.copy(fc_sueno = r(cliente.aggregate(AggregateRequest(setOf(HeartRateRecord.BPM_AVG), rangoNoche))[HeartRateRecord.BPM_AVG]?.toDouble(), 1))
                }
                if (HealthPermission.getReadPermission(OxygenSaturationRecord::class) in ok) runCatching {
                    val l = leer(cliente, OxygenSaturationRecord::class, rangoNoche)
                    if (l.isNotEmpty()) s = s.copy(spo2_min = r(l.minOf { it.percentage.value }, 1))
                }
            }
            s = extras(cliente, ok, rango, s)
            s = s.copy(ts = Fechas.ahoraIso())
            Repo.guardarSalud(s); n++
        }
        if (HealthPermission.getReadPermission(ExerciseSessionRecord::class) in ok) runCatching { ejercicio(cliente, dias + 1) }
        // Resumen por tipo: en cuántos de los días revisados hubo dato, o el error/permiso que lo impidió.
        listOf("sueño", "pasos", "ritmo cardiaco", "oxígeno", "frecuencia en reposo", "variabilidad (HRV)").forEach { k ->
            if (diag[k] == null) diag[k] = conDato[k]?.let { "datos en $it de $dias días" } ?: "Health Connect no tiene datos de esto en $dias días"
            else if (conDato[k] != null && diag[k]?.startsWith("error") == true) diag[k] = "datos en ${conDato[k]} de $dias días; a veces: ${diag[k]}"
        }
        diag["segundo plano"] = if ("android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND" in ok) "permitido" else "sin permiso: solo se actualiza con la app abierta"
        Repo.cambiarConfig { it.copy(hc_diagnostico = diag, hc_diagnostico_ts = Fechas.ahoraIso()) }
        return n
    }

    /** Vuelve a leer del reloj un solo día (tras quitar la corrección a mano). */
    suspend fun resincronizarDia(ctx: Context, fecha: String) {
        val dias = (Fechas.diasEntre(fecha, Fechas.hoyCalendario()) + 1).toInt().coerceIn(1, 60)
        sincronizar(ctx, dias)
    }

    private fun r(x: Double?, d: Int = 1): Double? = x?.let { val f = Math.pow(10.0, d.toDouble()); Math.round(it * f) / f }

    /** Peso, grasa, respiración, temperatura, presión, distancia, calorías, pisos, VO2 máx. y agua: lo que el reloj o la báscula escriban. */
    private suspend fun extras(cliente: HealthConnectClient, ok: Set<String>, rango: TimeRangeFilter, base: SaludDia): SaludDia {
        var s = base
        fun tiene(k: kotlin.reflect.KClass<out androidx.health.connect.client.records.Record>) = HealthPermission.getReadPermission(k) in ok
        if (tiene(WeightRecord::class)) runCatching {
            cliente.readRecords(ReadRecordsRequest(WeightRecord::class, rango)).records.lastOrNull()?.let { s = s.copy(peso_kg = r(it.weight.inKilograms, 2)) }
        }
        if (tiene(BodyFatRecord::class)) runCatching {
            cliente.readRecords(ReadRecordsRequest(BodyFatRecord::class, rango)).records.lastOrNull()?.let { s = s.copy(grasa_pct = r(it.percentage.value)) }
        }
        if (tiene(RespiratoryRateRecord::class)) runCatching {
            val l = cliente.readRecords(ReadRecordsRequest(RespiratoryRateRecord::class, rango)).records
            if (l.isNotEmpty()) s = s.copy(resp_rpm = r(l.map { it.rate }.average()))
        }
        if (tiene(BodyTemperatureRecord::class)) runCatching {
            val l = cliente.readRecords(ReadRecordsRequest(BodyTemperatureRecord::class, rango)).records
            if (l.isNotEmpty()) s = s.copy(temp_c = r(l.map { it.temperature.inCelsius }.average(), 2))
        }
        if (tiene(SkinTemperatureRecord::class)) runCatching {
            val d = cliente.readRecords(ReadRecordsRequest(SkinTemperatureRecord::class, rango)).records.flatMap { it.deltas }.map { it.delta.inCelsius }
            if (d.isNotEmpty()) s = s.copy(temp_piel_delta = r(d.average(), 2))
        }
        if (tiene(BloodPressureRecord::class)) runCatching {
            val l = cliente.readRecords(ReadRecordsRequest(BloodPressureRecord::class, rango)).records
            if (l.isNotEmpty()) s = s.copy(pa_sistolica = r(l.map { it.systolic.inMillimetersOfMercury }.average(), 0), pa_diastolica = r(l.map { it.diastolic.inMillimetersOfMercury }.average(), 0))
        }
        if (tiene(DistanceRecord::class)) runCatching {
            cliente.aggregate(AggregateRequest(setOf(DistanceRecord.DISTANCE_TOTAL), rango))[DistanceRecord.DISTANCE_TOTAL]?.let { s = s.copy(distancia_km = r(it.inKilometers, 2)) }
        }
        if (tiene(ActiveCaloriesBurnedRecord::class)) runCatching {
            cliente.aggregate(AggregateRequest(setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL), rango))[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.let { s = s.copy(cal_activas = r(it.inKilocalories, 0)) }
        }
        if (tiene(TotalCaloriesBurnedRecord::class)) runCatching {
            cliente.aggregate(AggregateRequest(setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL), rango))[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.let { s = s.copy(cal_totales = r(it.inKilocalories, 0)) }
        }
        if (tiene(FloorsClimbedRecord::class)) runCatching {
            cliente.aggregate(AggregateRequest(setOf(FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL), rango))[FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL]?.let { s = s.copy(pisos = r(it, 0)) }
        }
        if (tiene(Vo2MaxRecord::class)) runCatching {
            cliente.readRecords(ReadRecordsRequest(Vo2MaxRecord::class, rango)).records.lastOrNull()?.let { s = s.copy(vo2max = r(it.vo2MillilitersPerMinuteKilogram)) }
        }
        if (tiene(HydrationRecord::class)) runCatching {
            cliente.aggregate(AggregateRequest(setOf(HydrationRecord.VOLUME_TOTAL), rango))[HydrationRecord.VOLUME_TOTAL]?.let { s = s.copy(agua_ml = r(it.inMilliliters, 0)) }
        }
        return s
    }

    val TIPOS_EJERCICIO: Map<Int, String> = mapOf(
        ExerciseSessionRecord.EXERCISE_TYPE_ROCK_CLIMBING to "escalada",
        ExerciseSessionRecord.EXERCISE_TYPE_WALKING to "caminata",
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING to "carrera",
        ExerciseSessionRecord.EXERCISE_TYPE_RUNNING_TREADMILL to "caminadora",
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING to "bici",
        ExerciseSessionRecord.EXERCISE_TYPE_BIKING_STATIONARY to "bici fija",
        ExerciseSessionRecord.EXERCISE_TYPE_HIKING to "senderismo",
        ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING to "fuerza",
        ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING to "pesas",
        ExerciseSessionRecord.EXERCISE_TYPE_YOGA to "yoga",
        ExerciseSessionRecord.EXERCISE_TYPE_PILATES to "pilates",
        ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL to "natación",
        ExerciseSessionRecord.EXERCISE_TYPE_STRETCHING to "estiramiento",
        ExerciseSessionRecord.EXERCISE_TYPE_HIGH_INTENSITY_INTERVAL_TRAINING to "HIIT",
        ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT to "otro",
    )

    /** Sesiones de ejercicio del reloj, con la frecuencia cardiaca de cada una. */
    private suspend fun ejercicio(cliente: HealthConnectClient, dias: Int) {
        val zona = ZoneId.systemDefault()
        val ini = Fechas.parse(Fechas.sumar(Fechas.hoyCalendario(), -dias.toLong())).atStartOfDay(zona).toInstant()
        val r = cliente.readRecords(ReadRecordsRequest(recordType = ExerciseSessionRecord::class, timeRangeFilter = TimeRangeFilter.after(ini)))
        for (e in r.records) {
            val rango = TimeRangeFilter.between(e.startTime, e.endTime)
            val fc = runCatching { cliente.aggregate(AggregateRequest(setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MAX), rango)) }.getOrNull()
            val ms = e.startTime.toEpochMilli()
            Repo.guardarSesion(Sesion(
                id = e.metadata.id.ifEmpty { "hc_$ms" }, fecha = Fechas.diaLogicoDeMillis(ms),
                inicio = Fechas.isoDeMillis(ms), fin = Fechas.isoDeMillis(e.endTime.toEpochMilli()),
                tipo = e.exerciseType, tipo_nombre = TIPOS_EJERCICIO[e.exerciseType] ?: "tipo ${e.exerciseType}", titulo = e.title ?: "",
                min = java.time.Duration.between(e.startTime, e.endTime).toMinutes().toInt(),
                fc_media = fc?.get(HeartRateRecord.BPM_AVG)?.toDouble(), fc_max = fc?.get(HeartRateRecord.BPM_MAX)?.toDouble(),
            ))
        }
    }

    @Suppress("unused")
    private fun Instant.local(): String = Fechas.isoDeMillis(toEpochMilli())
}
