package io.github.ruddy2003.vestigium.sensores

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.PantallaDia
import io.github.ruddy2003.vestigium.data.Repo

/** Tiempo en pantalla por app y desbloqueos, calculado en el teléfono a partir de los eventos de uso. */
object Pantalla {
    fun tienePermiso(ctx: Context): Boolean {
        val aom = ctx.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val modo = aom.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName)
        return modo == AppOpsManager.MODE_ALLOWED
    }

    private fun lanzadores(ctx: Context): Set<String> {
        val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return ctx.packageManager.queryIntentActivities(i, 0).map { it.activityInfo.packageName }.toSet()
    }

    @Suppress("DEPRECATION")
    private fun nombreApp(ctx: Context, pkg: String): String = try {
        val pm: PackageManager = ctx.packageManager
        pm.getApplicationLabel(pm.getApplicationInfo(pkg, 0)).toString()
    } catch (e: Exception) { pkg }

    /**
     * El uso del día lógico [fecha] (esquema 13): de las 05:00 a las 05:00 del día siguiente, así lo de la madrugada cuenta para
     * el día que estabas viviendo. Los días calculados antes del esquema 13 quedaron con el día de calendario (ver [PantallaDia.ventana]).
     */
    fun calcular(ctx: Context, fecha: String): PantallaDia? {
        if (!tienePermiso(ctx)) return null
        val usm = ctx.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val ini = Fechas.inicioDiaLogico(fecha)
        val fin = minOf(Fechas.finDiaLogico(fecha), System.currentTimeMillis())
        if (fin <= ini) return null
        val eventos = usm.queryEvents(ini, fin)
        val e = UsageEvents.Event()
        val excluir = lanzadores(ctx) + ctx.packageName
        val porApp = HashMap<String, Long>()
        var actual: String? = null
        var desde = 0L
        var desbloqueos = 0
        var primero: Long? = null
        var ultimo: Long? = null
        val porHora = LongArray(24)
        val zona = Fechas.zona()
        /** Reparte un tramo de uso entre las horas del día que toca. */
        fun repartir(a: Long, b: Long) {
            var x = a
            while (x < b) {
                val h = java.time.Instant.ofEpochMilli(x).atZone(zona)
                val finHora = h.withMinute(0).withSecond(0).withNano(0).plusHours(1).toInstant().toEpochMilli()
                val y = minOf(b, finHora)
                porHora[h.hour] += y - x
                x = y
            }
        }
        fun cerrar(t: Long) {
            val a = actual ?: return
            if (t > desde) { porApp[a] = (porApp[a] ?: 0L) + (t - desde); repartir(desde, t) }
            actual = null
        }
        while (eventos.hasNextEvent()) {
            eventos.getNextEvent(e)
            when (e.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    cerrar(e.timeStamp)
                    if (e.packageName !in excluir) {
                        actual = e.packageName; desde = e.timeStamp
                        if (primero == null) primero = e.timeStamp
                    }
                }
                UsageEvents.Event.ACTIVITY_PAUSED -> if (e.packageName == actual) { cerrar(e.timeStamp); ultimo = e.timeStamp }
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> cerrar(e.timeStamp)
                UsageEvents.Event.KEYGUARD_HIDDEN -> desbloqueos++
            }
        }
        cerrar(fin)
        val mins = porApp.filterValues { it >= 60_000 }.mapKeys { nombreApp(ctx, it.key) }
            .entries.groupBy({ it.key }, { it.value }).mapValues { (it.value.sum() / 60_000).toInt() }
            .entries.sortedByDescending { it.value }.take(40).associate { it.key to it.value }
        val sueno = runCatching { alrededorDelSueno(ctx, usm, fecha) }.getOrNull()
        return PantallaDia(
            fecha = fecha,
            total_min = (porApp.values.sum() / 60_000).toInt(),
            desbloqueos = desbloqueos,
            primer_uso = primero?.let { Fechas.isoDeMillis(it) },
            ultimo_uso = ultimo?.let { Fechas.isoDeMillis(it) },
            por_app_min = mins,
            por_hora_min = porHora.map { (it / 60_000).toInt() },
            cama_min = sueno?.first,
            dormir_tras_tel_min = sueno?.second,
            tel_tras_despertar_min = sueno?.third,
            ventana = "logica",
        )
    }

    /** Ratos de uso (inicio, fin) entre dos instantes, de cualquier app que no sea el lanzador. */
    private fun usos(ctx: Context, usm: UsageStatsManager, a: Long, b: Long): List<Pair<Long, Long>> {
        val eventos = usm.queryEvents(a, b)
        val e = UsageEvents.Event()
        val excluir = lanzadores(ctx)
        val out = ArrayList<Pair<Long, Long>>()
        var actual: String? = null; var desde = 0L
        fun cerrar(t: Long) { if (actual != null && t > desde) out += desde to t; actual = null }
        while (eventos.hasNextEvent()) {
            eventos.getNextEvent(e)
            when (e.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> { cerrar(e.timeStamp); if (e.packageName !in excluir) { actual = e.packageName; desde = e.timeStamp } }
                UsageEvents.Event.ACTIVITY_PAUSED -> if (e.packageName == actual) cerrar(e.timeStamp)
                UsageEvents.Event.SCREEN_NON_INTERACTIVE -> cerrar(e.timeStamp)
            }
        }
        cerrar(b)
        return out
    }

    /**
     * La noche que terminó en [fecha] (según el reloj): minutos de pantalla en la última hora antes de dormirte, minutos entre
     * soltar el teléfono y dormirte, y minutos entre despertar y tomarlo. Null si no hay sueño del reloj.
     */
    private fun alrededorDelSueno(ctx: Context, usm: UsageStatsManager, fecha: String): Triple<Int?, Int?, Int?>? {
        val s = Repo.salud[fecha] ?: return null
        val ini = s.sueno_inicio?.let { Fechas.millis(it) }?.takeIf { it > 0 } ?: return null
        val fin = s.sueno_fin?.let { Fechas.millis(it) }?.takeIf { it > ini } ?: return null
        val antes = usos(ctx, usm, ini - 3 * 3_600_000L, ini)
        val cama = antes.sumOf { (a, b) -> (minOf(b, ini) - maxOf(a, ini - 3_600_000L)).coerceAtLeast(0L) } / 60_000
        val soltar = antes.maxOfOrNull { it.second }?.let { ((ini - it) / 60_000).toInt().coerceAtLeast(0) }
        val ahora = System.currentTimeMillis()
        val despues = if (ahora > fin) usos(ctx, usm, fin, minOf(ahora, fin + 4 * 3_600_000L)) else emptyList()
        val tomar = despues.minOfOrNull { it.first }?.let { ((it - fin) / 60_000).toInt().coerceAtLeast(0) }
        return Triple(cama.toInt(), soltar, tomar)
    }

    fun sincronizar(ctx: Context, dias: Int = 2) {
        for (i in 0 until dias) {
            val f = Fechas.sumar(Fechas.diaLogico(), -i.toLong())
            calcular(ctx, f)?.let { Repo.guardarPantalla(it) }
        }
    }
}
