package io.github.ruddy2003.vestigium.avisos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.github.ruddy2003.vestigium.ciencia.Escalas
import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.reportes.Reportes
import io.github.ruddy2003.vestigium.sensores.Geocercas
import io.github.ruddy2003.vestigium.sensores.Pantalla
import io.github.ruddy2003.vestigium.sensores.Salud
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.widget.Widgets
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Programador {
    fun asegurar(ctx: Context) {
        val req = PeriodicWorkRequestBuilder<Pulso>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("pulso", ExistingPeriodicWorkPolicy.KEEP, req)
    }

    fun ahora(ctx: Context) {
        WorkManager.getInstance(ctx).enqueueUniqueWork("pulso_ya", ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<Pulso>().build())
    }

    /** Horas aleatorias dentro de la ventana, separadas al menos 90 minutos. Lo aleatorio evita registrar solo momentos predecibles. */
    fun planDelDia(fecha: String): List<Long> {
        val c = Repo.config
        val n = c.muestreos_por_dia.coerceIn(0, 6)
        if (n == 0 || c.ventana_fin <= c.ventana_inicio) return emptyList()
        val zona = ZoneId.systemDefault()
        val base = Fechas.parse(fecha).atStartOfDay(zona).toInstant().toEpochMilli()
        val ini = base + c.ventana_inicio * 3_600_000L
        val fin = base + c.ventana_fin * 3_600_000L
        val tramo = (fin - ini) / n
        val rnd = Random(fecha.hashCode())
        return (0 until n).map { i ->
            val margen = minOf(45 * 60_000L, tramo / 4)
            ini + i * tramo + margen + (rnd.nextDouble() * (tramo - 2 * margen)).toLong()
        }
    }
}

/** Tarea cada 15 minutos: ubicación, avisos que tocan, sincronización diaria y reportes. */
class Pulso(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val ctx = applicationContext
        Repo.init(ctx)
        runCatching { Ubicacion.muestrear(ctx) }
        runCatching { avisos(ctx) }
        runCatching { diario(ctx) }
        return Result.success()
    }

    private fun yaFue(clave: String) = Repo.config.disparos.containsKey(clave)
    private fun marcar(clave: String) {
        val limite = System.currentTimeMillis() - 40L * 86_400_000
        Repo.cambiarConfig { c -> c.copy(disparos = c.disparos.filterValues { it > limite } + (clave to System.currentTimeMillis())) }
    }

    private fun avisos(ctx: Context) {
        val c = Repo.config
        if (!c.avisos) return
        val ahora = LocalDateTime.now()
        val ms = System.currentTimeMillis()
        val hoy = Fechas.hoyCalendario()
        val diaLog = Fechas.diaLogico()

        // Muestreo de ánimo
        val plan = c.plan_muestreo[hoy] ?: Programador.planDelDia(hoy).also { p ->
            Repo.cambiarConfig { cc -> cc.copy(plan_muestreo = cc.plan_muestreo.filterKeys { it >= Fechas.sumar(hoy, -3) } + (hoy to p)) }
        }
        plan.forEachIndexed { i, t ->
            val clave = "muestreo:$hoy:$i"
            if (!yaFue(clave) && ms >= t) {
                marcar(clave)
                if (ms - t < 50 * 60_000L) Avisos.muestreo(ctx, i, t)
            }
        }

        // Sueños por la mañana
        if (ahora.hour >= c.hora_sueno && ahora.hour < c.hora_sueno + 3 && !yaFue("sueno:$hoy")) {
            marcar("sueno:$hoy")
            val d = Repo.dias[diaLog]
            if (d?.sueno_texto.isNullOrBlank() && d?.sueno_recordado != true) Avisos.sueno(ctx)
        }

        // Diario por la noche
        if (ahora.hour >= c.hora_diario && !yaFue("diario:$hoy")) {
            marcar("diario:$hoy")
            if (Repo.dias[diaLog]?.valencia == null) Avisos.diario(ctx)
        }

        // Escalas: domingo en la noche y el día 1 de cada mes
        if (ahora.dayOfWeek == DayOfWeek.SUNDAY && ahora.hour >= 19 && !yaFue("escalas:$hoy")) {
            marcar("escalas:$hoy")
            val pendientes = Escalas.TODAS.filter { inst ->
                val u = Repo.ultimaRespuesta(inst.id)
                u == null || java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(u.fecha), LocalDate.now()) >= inst.frecuenciaDias - 1
            }
            if (pendientes.isNotEmpty()) Avisos.escalas(ctx, pendientes.joinToString(", ") { it.nombre })
        }

        // Cartas al futuro
        val vencidas = Repo.cartas.filter { !it.abierta && it.abrir_en <= hoy }
        if (vencidas.isNotEmpty() && !yaFue("carta:$hoy")) { marcar("carta:$hoy"); Avisos.carta(ctx) }
    }

    private suspend fun diario(ctx: Context) {
        val hoy = Fechas.hoyCalendario()
        val ahora = LocalDateTime.now()
        if (ahora.hour < 6) return
        if (!yaFue("sync:$hoy")) {
            marcar("sync:$hoy")
            runCatching { Salud.sincronizar(ctx, 3) }
            runCatching { Pantalla.sincronizar(ctx, 2) }
            runCatching { Geocercas.registrar(ctx) }
            runCatching { Exportar.respaldoAutomatico(ctx) }
            Repo.cambiarConfig { it.copy(ultimo_sync = Fechas.ahoraIso()) }
        } else if (ahora.hour >= 21 && !yaFue("sync2:$hoy")) {
            marcar("sync2:$hoy")
            runCatching { Pantalla.sincronizar(ctx, 1) }
            runCatching { Salud.sincronizar(ctx, 1) }
        }
        // Reporte semanal: lunes, de la semana anterior
        if (ahora.dayOfWeek == DayOfWeek.MONDAY && ahora.hour >= 8 && !yaFue("reporte_sem:$hoy")) {
            marcar("reporte_sem:$hoy")
            runCatching { Reportes.semanal(ctx, Fechas.sumar(Fechas.lunesDe(hoy), -7), avisar = true) }
        }
        // Reporte mensual: día 1, del mes anterior
        if (ahora.dayOfMonth == 1 && ahora.hour >= 9 && !yaFue("reporte_mes:$hoy")) {
            marcar("reporte_mes:$hoy")
            runCatching { Reportes.mensual(ctx, Fechas.mesDe(Fechas.sumar(hoy, -1)), avisar = true) }
        }
        runCatching { Widgets.actualizar(ctx) }
    }
}

/** Al reiniciar el teléfono, Android borra las geocercas: se vuelven a registrar. */
class ArranqueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        runCatching { Geocercas.registrar(context) }
        Programador.asegurar(context)
    }
}
