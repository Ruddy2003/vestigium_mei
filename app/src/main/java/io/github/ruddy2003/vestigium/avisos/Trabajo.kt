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
import io.github.ruddy2003.vestigium.data.Drive
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.sensores.Agenda
import io.github.ruddy2003.vestigium.sensores.Entorno
import io.github.ruddy2003.vestigium.sensores.Galeria
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
import kotlinx.coroutines.CancellationException
import kotlin.random.Random

object Programador {
    fun asegurar(ctx: Context) {
        val req = PeriodicWorkRequestBuilder<Pulso>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("pulso", ExistingPeriodicWorkPolicy.KEEP, req)
    }

    fun ahora(ctx: Context) {
        WorkManager.getInstance(ctx).enqueueUniqueWork("pulso_ya", ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<Pulso>().build())
    }

    /**
     * Horas aleatorias dentro de la ventana: una en cada tramo igual, lejos de sus bordes. Entre dos avisos seguidos quedan al menos
     * 2 × min(45 min, tramo/4): 90 minutos si cada tramo dura 3 h o más, menos si hay muchos avisos en una ventana corta
     * (4 avisos de 10 a 21 h: 82.5 min; con tramos de 40 min, 20 min). Lo aleatorio evita registrar solo momentos predecibles.
     */
    fun planDelDia(fecha: String): List<Long> {
        val c = Repo.config
        val n = c.muestreos_por_dia.coerceIn(0, 6)
        if (n == 0 || c.ventana_fin <= c.ventana_inicio) return emptyList()
        // La ventana en la hora local de ese día (un día con cambio de horario no la corre una hora; H-1-7).
        val ini = Fechas.millisDe(Fechas.parse(fecha).atTime(c.ventana_inicio, 0))
        val fin = Fechas.millisDe(Fechas.parse(fecha).atTime(c.ventana_fin.coerceAtMost(23), 0)) + if (c.ventana_fin >= 24) 3_600_000L else 0L
        val tramo = (fin - ini) / n
        val rnd = Random(fecha.hashCode())
        return (0 until n).map { i ->
            val margen = minOf(45 * 60_000L, tramo / 4)
            ini + i * tramo + margen + (rnd.nextDouble() * (tramo - 2 * margen)).toLong()
        }
    }
}

/** Lo que vale [Pulso.paso] cuando el paso terminó. */
private const val PASO_HECHO = -1

/**
 * Como runCatching, pero deja pasar la cancelación: si WorkManager detiene la tarea (pasó su límite de 10 min), se detiene
 * de verdad. runCatching la atrapaba y lo que seguía (respaldo incluido) corría sin la protección de WorkManager.
 */
internal inline fun intentar(f: () -> Unit) {
    try { f() } catch (e: CancellationException) { throw e } catch (e: Throwable) { }
}

/** Tarea cada 15 minutos: ubicación, avisos que tocan, sincronización diaria y reportes. */
class Pulso(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val ctx = applicationContext
        Repo.init(ctx)
        intentar { Repo.registrarZona() }
        // Una sola vez: la fuerza de referencia de los entrenos que ya estaban (H-7-14).
        intentar {
            val mig = ctx.getSharedPreferences("migraciones", Context.MODE_PRIVATE)
            if (!mig.getBoolean("fuerza_ref", false)) { Repo.completarFuerzaReferencia(); mig.edit().putBoolean("fuerza_ref", true).apply() }
        }
        // Una sola vez (esquema 19): quita los puntos repetidos de la muestra de fondo y corrige las visitas que quedaron
        // abiertas o encimadas (decisión del autor, 5 nov 2026: corregir también lo pasado).
        var visitasCorregidas = false
        intentar {
            val mig = ctx.getSharedPreferences("migraciones", Context.MODE_PRIVATE)
            if (!mig.getBoolean("comidas_medianoche", false)) { Repo.rehacerHorarioComidas(); mig.edit().putBoolean("comidas_medianoche", true).apply() }
            // Con una repetición, el 1RM es el peso (ya no Epley): se recalcula la fuerza de referencia guardada.
            if (!mig.getBoolean("epley_una", false)) { Repo.completarFuerzaReferencia(); mig.edit().putBoolean("epley_una", true).apply() }
            if (!mig.getBoolean("esquema19_ubicaciones", false)) { Repo.db().quitarMuestrasRepetidas(); mig.edit().putBoolean("esquema19_ubicaciones", true).apply() }
            if (!mig.getBoolean("esquema19_visitas", false)) {
                io.github.ruddy2003.vestigium.data.Lugares.limpiarVisitasPasadas(); visitasCorregidas = true
                mig.edit().putBoolean("esquema19_visitas", true).apply()
                intentar { Geocercas.registrar(ctx) }
            }
        }
        intentar { Ubicacion.muestrear(ctx) }
        // Las visitas abiertas de las que tus ubicaciones ya dicen que saliste (si Android no avisó la salida). No en el
        // pulso de la corrección: la lista en memoria se actualiza un instante después y partiría de la de antes.
        if (!visitasCorregidas) intentar { if (io.github.ruddy2003.vestigium.data.Lugares.cerrarAbiertas()) Geocercas.registrar(ctx) }
        intentar { io.github.ruddy2003.vestigium.sensores.Percepcion.muestrear(ctx) }
        // ¿Llevas un rato en un sitio nuevo? Pregunta si es un lugar especial (máximo dos veces al día, nunca de noche).
        intentar {
            if (Repo.config.avisos) io.github.ruddy2003.vestigium.data.Lugares.revisar(ctx)?.let { c ->
                Avisos.lugar(ctx, c.sugerencia, ((c.hasta_ms - c.desde_ms) / 60_000L).toInt())
            }
        }
        intentar { io.github.ruddy2003.vestigium.data.Spotify.quizaSincronizar(ctx) }
        intentar { avisos(ctx) }
        intentar { diario(ctx) }
        // Por si la alarma del enfoque se perdió (reinicio del teléfono): avanza y la vuelve a programar.
        intentar { io.github.ruddy2003.vestigium.data.Enfoque.avanzar(ctx) }
        // El tema del día (si pediste que cambie solo): también el widget y el ícono.
        intentar { if (io.github.ruddy2003.vestigium.ui.RotacionTema.revisar()) { io.github.ruddy2003.vestigium.widget.Widgets.actualizar(ctx); io.github.ruddy2003.vestigium.ui.IconoApp.aplicar(ctx); io.github.ruddy2003.vestigium.ui.AccesosDirectos.publicar(ctx) } }
        return Result.success()
    }

    private fun yaFue(clave: String) = Repo.config.disparos.containsKey(clave)

    /** Lo que se sabe de cada paso de las tandas en curso: [PASO_HECHO], o cuántas veces se empezó. */
    private val pasos get() = applicationContext.getSharedPreferences("pulso_pasos", Context.MODE_PRIVATE)

    /**
     * Un paso de una tanda (la sincronización del día, la de la noche, la historia del mes). Se salta si ya se hizo o si
     * ya se empezó dos veces (si siempre se corta, no se insiste todo el día). Se anota como hecho al terminar, también si
     * falló con un error (como antes: no se reintenta en cada pulso); si la tarea se detuvo a la mitad, no.
     */
    private suspend fun paso(tanda: String, nombre: String, f: suspend () -> Unit) {
        if (isStopped) return
        val clave = "$tanda|$nombre"
        val antes = pasos.getInt(clave, 0)
        if (antes == PASO_HECHO || antes >= 2) return
        pasos.edit().putInt(clave, antes + 1).commit()
        try { f() } catch (e: CancellationException) { throw e } catch (e: Throwable) { }
        if (!isStopped) pasos.edit().putInt(clave, PASO_HECHO).apply()
    }

    /** Quita lo anotado de tandas que ya no son las de hoy ni la del mes. */
    private fun olvidarPasos(vigentes: Set<String>) {
        val viejas = pasos.all.keys.filter { it.substringBefore('|') !in vigentes }
        if (viejas.isNotEmpty()) pasos.edit().apply { viejas.forEach { remove(it) } }.apply()
    }
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

        // Muestreo de ánimo. Cada aviso queda anotado (planeado, vencido, mostrado) para saber cuántos se contestaron.
        val plan = c.plan_muestreo[hoy] ?: Programador.planDelDia(hoy).also { p ->
            Repo.cambiarConfig { cc -> cc.copy(plan_muestreo = cc.plan_muestreo.filterKeys { it >= Fechas.sumar(hoy, -3) } + (hoy to p)) }
            // Si el plan se rehace a mitad del día (cambiaste los ajustes), los que ya vencieron siguen contando y del plan nuevo
            // solo cuentan los que aún pueden sonar (los de un turno ya usado no vuelven a sonar).
            if (p.isNotEmpty() || Repo.avisos[hoy] != null)
                Repo.anotarAvisos(hoy) { a -> a.copy(planeados = a.vencidos.size + p.indices.count { !yaFue("muestreo:$hoy:$it") }) }
        }
        // Cada aviso tiene su alarma exacta (Muestreo); este pulso es el respaldo si alguna se perdió.
        plan.forEachIndexed { i, t -> if (!yaFue("muestreo:$hoy:$i") && ms >= t) Muestreo.disparar(ctx, hoy, i, t, ms) }
        intentar { Muestreo.programar(ctx, hoy, plan, ms) }

        // Sueños por la mañana
        if (ahora.hour >= c.hora_sueno && ahora.hour < c.hora_sueno + 3 && !yaFue("sueno:$hoy")) {
            marcar("sueno:$hoy")
            val d = Repo.dias[diaLog]
            if (d?.sueno_texto.isNullOrBlank() && d?.sueno_recordado != true) Avisos.sueno(ctx)
        }

        // Días especiales (cumpleaños, aniversarios, fechas propias): una vez, desde tu hora de despertar y hasta las 21.
        if (c.avisos_especiales && ahora.hour >= c.hora_sueno && ahora.hour < 21 && !yaFue("especial:$diaLog")) {
            marcar("especial:$diaLog")
            val l = io.github.ruddy2003.vestigium.data.Especiales.de(diaLog, c)
            if (l.isNotEmpty()) Avisos.especial(ctx, l.map { io.github.ruddy2003.vestigium.data.Especiales.frase(it) })
        }

        // Lo de anoche: una hora después de tu hora de despertar, si quedó un consumo sin completar.
        if (ahora.hour >= (c.hora_sueno + 1).coerceAtMost(13) && ahora.hour < 15 && !yaFue("anoche:$hoy")) {
            val l = io.github.ruddy2003.vestigium.data.Sustancias.porCompletar(diaLog)
            if (l.isNotEmpty()) { marcar("anoche:$hoy"); Avisos.loDeAnoche(ctx, l.first(), l.size) }
        }

        // Diario por la noche
        if (ahora.hour >= c.hora_diario && !yaFue("diario:$hoy")) {
            marcar("diario:$hoy")
            if (Repo.dias[diaLog]?.valencia == null) Avisos.diario(ctx)
        }

        // Cuestionarios: aviso el día que toca y cada vez más seguido si se atrasan.
        val pendientes = Escalas.pendientes()
        if (pendientes.isNotEmpty()) {
            val peor = pendientes.maxOf { it.second }
            val horas = if (c.escalas_insistir) Escalas.horasAviso(peor, c.escalas_hora) else listOf(c.escalas_hora)
            // Se toma el turno más reciente que ya pasó; si el teléfono estuvo apagado no se amontonan avisos.
            val ultimo = horas.indices.lastOrNull { ahora.hour >= horas[it] }
            if (ultimo != null && !yaFue("escalas:$hoy:$ultimo")) {
                (0..ultimo).forEach { if (!yaFue("escalas:$hoy:$it")) marcar("escalas:$hoy:$it") }
                Avisos.escalas(ctx, pendientes)
            }
        }

        // Importaciones (una vez por semana), metas que toca revisar y notas de recordatorio.
        intentar { Recordatorios.revisar(ctx, ahora, ::yaFue, ::marcar) }

        // Cartas al futuro
        val vencidas = Repo.cartas.filter { !it.abierta && it.abrir_en <= hoy }
        if (vencidas.isNotEmpty() && !yaFue("carta:$hoy")) { marcar("carta:$hoy"); Avisos.carta(ctx) }
    }

    private suspend fun diario(ctx: Context) {
        val hoy = Fechas.hoyCalendario()
        val ahora = LocalDateTime.now()
        if (ahora.hour < 6) return
        val mes = Fechas.mesDe(hoy)
        olvidarPasos(setOf("sync:$hoy", "sync2:$hoy", "historia:$mes"))
        // Cada paso se anota al terminar, no antes: si WorkManager corta la tarea (su límite son 10 min), el siguiente pulso
        // sigue donde se quedó. Antes la tanda entera se daba por hecha al empezar y lo que faltaba no se hacía ese día.
        if (!yaFue("sync:$hoy")) {
            val s = "sync:$hoy"
            paso(s, "salud") { Salud.sincronizar(ctx, 3) }
            paso(s, "pantalla") { Pantalla.sincronizar(ctx, 2) }
            paso(s, "geocercas") { Geocercas.registrar(ctx) }
            paso(s, "actividad") { Actividad.registrar(ctx) }
            paso(s, "galeria") { Galeria.sincronizar(ctx, 3) }
            paso(s, "agenda") { Agenda.sincronizar(ctx) }
            paso(s, "comunicacion") { io.github.ruddy2003.vestigium.sensores.Comunicacion.sincronizar(ctx, 3) }
            paso(s, "entorno") { Entorno.sincronizar(ctx) }
            paso(s, "respaldo") { Exportar.respaldoAutomatico(ctx) }
            if (isStopped) return
            // Lo que lleva más de 7 días en la papelera se va (v14).
            intentar { Repo.limpiarPapelera() }
            intentar {
                if ((Repo.config.carpeta_respaldo != null || Repo.config.drive) && io.github.ruddy2003.vestigium.data.Respaldos.bloqueado(ctx) &&
                    !yaFue("respaldo_pausa:${Fechas.lunesDe(hoy)}")) { marcar("respaldo_pausa:${Fechas.lunesDe(hoy)}"); Avisos.respaldoPausado(ctx) }
            }
            intentar {
                if (Repo.config.drive && !Drive.automatico(ctx) && !yaFue("drive_aviso:${Fechas.lunesDe(hoy)}")) {
                    marcar("drive_aviso:${Fechas.lunesDe(hoy)}"); Avisos.drive(ctx)
                }
            }
            marcar(s)
            Repo.cambiarConfig { it.copy(ultimo_sync = Fechas.ahoraIso()) }
        } else if (ahora.hour >= 21 && !yaFue("sync2:$hoy")) {
            val s = "sync2:$hoy"
            paso(s, "pantalla") { Pantalla.sincronizar(ctx, 1) }
            paso(s, "salud") { Salud.sincronizar(ctx, 1) }
            paso(s, "galeria") { Galeria.sincronizar(ctx, 1) }
            paso(s, "agenda") { Agenda.sincronizar(ctx) }
            paso(s, "comunicacion") { io.github.ruddy2003.vestigium.sensores.Comunicacion.sincronizar(ctx, 1) }
            if (isStopped) return
            marcar(s)
        }
        // Una vez al mes se rellena la historia que guardan la galería y el calendario.
        if (ahora.hour >= 13 && !yaFue("historia:$mes")) {
            val s = "historia:$mes"
            paso(s, "galeria") { Galeria.historia(ctx) }
            paso(s, "agenda") { Agenda.historia(ctx) }
            paso(s, "comunicacion") { io.github.ruddy2003.vestigium.sensores.Comunicacion.historia(ctx) }
            paso(s, "entorno") { Entorno.sincronizar(ctx, maxPeticiones = 20) }
            if (isStopped) return
            marcar(s)
        }
        // Logros nuevos: un aviso por la tarde con los que se consiguieron (se festejan al abrir Logros).
        if (ahora.hour >= 18 && !yaFue("logros:$hoy")) {
            marcar("logros:$hoy")
            intentar {
                val vistos = Repo.config.logros_vistos
                val avisados = Repo.config.disparos.keys.filter { it.startsWith("logro_aviso:") }.map { it.removePrefix("logro_aviso:") }.toSet()
                val nuevos = io.github.ruddy2003.vestigium.ciencia.Logros.todos(ctx).filter { it.hecho && it.id !in vistos && it.id !in avisados && it.conseguido?.let { f -> f >= Fechas.sumar(hoy, -3) } == true }
                if (nuevos.isNotEmpty()) { nuevos.forEach { marcar("logro_aviso:${it.id}") }; Avisos.logros(ctx, nuevos.map { it.nombre }) }
            }
        }
        // «Tu año»: el 31 de diciembre por la mañana.
        if (ahora.monthValue == 12 && ahora.dayOfMonth == 31 && ahora.hour >= 9 && !yaFue("tu_anio:${ahora.year}")) {
            marcar("tu_anio:${ahora.year}")
            intentar { Avisos.tuAnio(ctx, ahora.year) }
        }
        // Reporte semanal: lunes, de la semana anterior
        if (ahora.dayOfWeek == DayOfWeek.MONDAY && ahora.hour >= 8 && !yaFue("reporte_sem:$hoy")) {
            marcar("reporte_sem:$hoy")
            intentar { Reportes.semanal(ctx, Fechas.sumar(Fechas.lunesDe(hoy), -7), avisar = true) }
        }
        // Reporte mensual: día 1, del mes anterior
        if (ahora.dayOfMonth == 1 && ahora.hour >= 9 && !yaFue("reporte_mes:$hoy")) {
            marcar("reporte_mes:$hoy")
            intentar { Reportes.mensual(ctx, Fechas.mesDe(Fechas.sumar(hoy, -1)), avisar = true) }
        }
        intentar { Widgets.actualizar(ctx) }
    }
}

/** Al reiniciar el teléfono, Android borra las geocercas: se vuelven a registrar. */
class ArranqueReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        // Al cambiar de zona horaria (un viaje o el horario de verano del sistema) se anota desde cuándo rige la nueva.
        runCatching { Repo.registrarZona() }
        if (intent.action == Intent.ACTION_TIMEZONE_CHANGED) {
            // El plan de hoy son instantes fijos calculados con la zona de antes: tras un viaje caerían de madrugada. Se
            // quita y el siguiente pulso lo rehace con la zona nueva (los turnos ya usados no vuelven a sonar).
            runCatching {
                Muestreo.cancelar(context)
                val hoy = Fechas.hoyCalendario()
                Repo.cambiarConfig { it.copy(plan_muestreo = it.plan_muestreo - hoy) }
            }
            return
        }
        runCatching { Geocercas.registrar(context) }
        runCatching { Actividad.registrar(context) }
        Programador.asegurar(context)
    }
}
