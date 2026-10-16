package io.github.ruddy2003.vestigium.sensores

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
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
        "android.permission.health.READ_HEALTH_DATA_IN_BACKGROUND",
        "android.permission.health.READ_HEALTH_DATA_HISTORY",
    )

    fun disponible(ctx: Context): Boolean =
        HealthConnectClient.getSdkStatus(ctx) == HealthConnectClient.SDK_AVAILABLE

    suspend fun concedidos(ctx: Context): Set<String> =
        if (!disponible(ctx)) emptySet() else try {
            HealthConnectClient.getOrCreate(ctx).permissionController.getGrantedPermissions()
        } catch (e: Exception) { emptySet() }

    /** Sincroniza los últimos `dias` días. El sueño se asigna al día en que despertaste. */
    suspend fun sincronizar(ctx: Context, dias: Int = 3): Int {
        if (!disponible(ctx)) return 0
        val cliente = HealthConnectClient.getOrCreate(ctx)
        val ok = try { cliente.permissionController.getGrantedPermissions() } catch (e: Exception) { return 0 }
        if (ok.isEmpty()) return 0
        val zona = ZoneId.systemDefault()
        var n = 0
        for (i in 0 until dias) {
            val fecha = Fechas.sumar(Fechas.hoyCalendario(), -i.toLong())
            val d = Fechas.parse(fecha)
            val ini = d.atStartOfDay(zona).toInstant()
            val fin = d.plusDays(1).atStartOfDay(zona).toInstant()
            val rango = TimeRangeFilter.between(ini, fin)
            var s = Repo.salud[fecha] ?: SaludDia(fecha = fecha)

            if (HealthPermission.getReadPermission(StepsRecord::class) in ok) runCatching {
                val r = cliente.aggregate(AggregateRequest(setOf(StepsRecord.COUNT_TOTAL), rango))
                s = s.copy(pasos = r[StepsRecord.COUNT_TOTAL])
            }
            if (HealthPermission.getReadPermission(HeartRateRecord::class) in ok) runCatching {
                val r = cliente.aggregate(AggregateRequest(setOf(HeartRateRecord.BPM_AVG, HeartRateRecord.BPM_MIN, HeartRateRecord.BPM_MAX), rango))
                s = s.copy(fc_media = r[HeartRateRecord.BPM_AVG]?.toDouble(), fc_min = r[HeartRateRecord.BPM_MIN]?.toDouble(), fc_max = r[HeartRateRecord.BPM_MAX]?.toDouble())
            }
            if (HealthPermission.getReadPermission(RestingHeartRateRecord::class) in ok) runCatching {
                val r = cliente.readRecords(ReadRecordsRequest(recordType = RestingHeartRateRecord::class, timeRangeFilter = rango))
                if (r.records.isNotEmpty()) s = s.copy(fc_reposo = r.records.map { it.beatsPerMinute.toDouble() }.average())
            }
            if (HealthPermission.getReadPermission(OxygenSaturationRecord::class) in ok) runCatching {
                val r = cliente.readRecords(ReadRecordsRequest(recordType = OxygenSaturationRecord::class, timeRangeFilter = rango))
                if (r.records.isNotEmpty()) s = s.copy(spo2 = r.records.map { it.percentage.value }.average())
            }
            // Noche que termina este día: de las 18:00 del día anterior a las 14:00 de este.
            val noche = TimeRangeFilter.between(d.minusDays(1).atTime(18, 0).atZone(zona).toInstant(), d.atTime(14, 0).atZone(zona).toInstant())
            if (HealthPermission.getReadPermission(SleepSessionRecord::class) in ok) runCatching {
                val r = cliente.readRecords(ReadRecordsRequest(recordType = SleepSessionRecord::class, timeRangeFilter = noche))
                val sesiones = r.records.filter { it.endTime.atZone(zona).toLocalDate() == d }
                if (sesiones.isNotEmpty()) {
                    var total = 0L; var prof = 0L; var rem = 0L; var lig = 0L; var desp = 0L
                    var hayEtapas = false
                    for (ses in sesiones) {
                        if (ses.stages.isEmpty()) {
                            total += java.time.Duration.between(ses.startTime, ses.endTime).toMinutes()
                        } else for (st in ses.stages) {
                            hayEtapas = true
                            val m = java.time.Duration.between(st.startTime, st.endTime).toMinutes()
                            when (st.stage) {
                                SleepSessionRecord.STAGE_TYPE_DEEP -> { prof += m; total += m }
                                SleepSessionRecord.STAGE_TYPE_REM -> { rem += m; total += m }
                                SleepSessionRecord.STAGE_TYPE_LIGHT -> { lig += m; total += m }
                                SleepSessionRecord.STAGE_TYPE_SLEEPING -> total += m
                                SleepSessionRecord.STAGE_TYPE_AWAKE, SleepSessionRecord.STAGE_TYPE_AWAKE_IN_BED, SleepSessionRecord.STAGE_TYPE_OUT_OF_BED -> desp += m
                                else -> {}
                            }
                        }
                    }
                    val inicio = sesiones.minOf { it.startTime }; val finS = sesiones.maxOf { it.endTime }
                    s = s.copy(
                        sueno_min = total.toInt(),
                        sueno_profundo_min = if (hayEtapas) prof.toInt() else null,
                        sueno_rem_min = if (hayEtapas) rem.toInt() else null,
                        sueno_ligero_min = if (hayEtapas) lig.toInt() else null,
                        sueno_despierto_min = if (hayEtapas) desp.toInt() else null,
                        sueno_inicio = Fechas.isoDeMillis(inicio.toEpochMilli()),
                        sueno_fin = Fechas.isoDeMillis(finS.toEpochMilli()),
                    )
                }
            }
            if (HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class) in ok) runCatching {
                val r = cliente.readRecords(ReadRecordsRequest(recordType = HeartRateVariabilityRmssdRecord::class, timeRangeFilter = noche))
                if (r.records.isNotEmpty()) s = s.copy(hrv_rmssd = r.records.map { it.heartRateVariabilityMillis }.average())
            }
            s = s.copy(ts = Fechas.ahoraIso())
            Repo.guardarSalud(s); n++
        }
        if (HealthPermission.getReadPermission(ExerciseSessionRecord::class) in ok) runCatching { ejercicio(cliente, dias + 1) }
        return n
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
