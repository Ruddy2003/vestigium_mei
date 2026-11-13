package io.github.ruddy2003.vestigium.avisos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

/**
 * Los avisos de ánimo a su hora (esquema 20; decisión del autor del 5 nov 2026: lo mejor metodológicamente).
 *
 * El plan del día elige horas al azar ([Programador.planDelDia]). Antes, cada aviso salía cuando le tocaba correr a la tarea
 * de cada 15 minutos: entre 0 y ~15 min tarde, y más con el teléfono en reposo (más de 50 min y ya no salía). Los que más se
 * atrasaban eran justo los de los ratos en que no usabas el teléfono, y eso sesga el muestreo. Ahora cada aviso tiene su
 * alarma exacta; la tarea de 15 minutos queda como respaldo (si la alarma se perdió, por ejemplo tras reiniciar). Además se
 * guarda la hora en que el aviso salió de verdad ([io.github.ruddy2003.vestigium.data.AvisosDia.reales]).
 */
object Muestreo {
    private const val ACCION = "io.github.ruddy2003.vestigium.MUESTREO"
    private const val EXTRA_DIA = "dia"
    private const val EXTRA_INDICE = "indice"
    private const val EXTRA_HORA = "hora"
    /** Los planes tienen como mucho esta cantidad de avisos (el código de cada alarma es 4500 + su índice). */
    private const val MAXIMO = 16
    /** Más tarde que esto, un aviso ya no se muestra: la pregunta es «cómo estás ahora». */
    const val RETRASO_MAXIMO_MS = 50 * 60_000L

    private fun intento(ctx: Context, dia: String?, i: Int, t: Long?, crear: Boolean): PendingIntent? {
        val it = Intent(ctx, MuestreoReceiver::class.java).setAction(ACCION)
        if (dia != null && t != null) it.putExtra(EXTRA_DIA, dia).putExtra(EXTRA_INDICE, i).putExtra(EXTRA_HORA, t)
        val banderas = PendingIntent.FLAG_IMMUTABLE or if (crear) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_NO_CREATE
        return PendingIntent.getBroadcast(ctx, 4500 + i, it, banderas)
    }

    /** Programa una alarma exacta por cada aviso del plan de [dia] que todavía no sonó (si el sistema no deja, una aproximada). */
    fun programar(ctx: Context, dia: String, plan: List<Long>, ahora: Long = System.currentTimeMillis()) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        plan.take(MAXIMO).forEachIndexed { i, t ->
            if (t <= ahora || Repo.config.disparos.containsKey("muestreo:$dia:$i")) return@forEachIndexed
            val pi = intento(ctx, dia, i, t, crear = true) ?: return@forEachIndexed
            try {
                if (Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
                else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
            } catch (e: SecurityException) { am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi) }
        }
    }

    /** Quita las alarmas pendientes (al cambiar de zona horaria, el plan se rehace). */
    fun cancelar(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        for (i in 0 until MAXIMO) intento(ctx, null, i, null, crear = false)?.let { am.cancel(it); it.cancel() }
    }

    /**
     * El aviso [i] del plan de [dia], planeado para [t]: se anota una sola vez (lo dispare la alarma o la tarea de 15 min) y
     * se muestra si van menos de [RETRASO_MAXIMO_MS] de retraso y las notificaciones de ánimo están activas.
     */
    @Synchronized
    fun disparar(ctx: Context, dia: String, i: Int, t: Long, ahora: Long = System.currentTimeMillis()) {
        val clave = "muestreo:$dia:$i"
        // Unos segundos de adelanto de la alarma no cuentan como «antes de su hora».
        if (Repo.config.disparos.containsKey(clave) || ahora < t - 60_000L) return
        val limite = ahora - 40L * 86_400_000
        Repo.cambiarConfig { c -> c.copy(disparos = c.disparos.filterValues { it > limite } + (clave to ahora)) }
        val iso = Fechas.isoDeMillis(t)
        val muestra = ahora - t < RETRASO_MAXIMO_MS && Avisos.animoVisible(ctx)
        val real = Fechas.isoDeMillis(maxOf(ahora, t))
        Repo.anotarAvisos(dia) { a ->
            a.copy(vencidos = (a.vencidos + iso).distinct(), mostrados = if (muestra) (a.mostrados + iso).distinct() else a.mostrados,
                reales = if (muestra) a.reales + (iso to real) else a.reales)
        }
        if (muestra) Avisos.muestreo(ctx, i, t)
    }
}

/** Suena la alarma de un aviso de ánimo. */
class MuestreoReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        io.github.ruddy2003.vestigium.ui.RotacionTema.revisarDesde(context)
        val dia = intent.getStringExtra("dia") ?: return
        val i = intent.getIntExtra("indice", -1).takeIf { it >= 0 } ?: return
        val t = intent.getLongExtra("hora", 0L).takeIf { it > 0 } ?: return
        if (!Repo.config.avisos) return
        // Si el plan cambió (rehiciste los ajustes), una alarma vieja no vale: solo la del plan vigente.
        if (Repo.config.plan_muestreo[dia]?.getOrNull(i) != t) return
        runCatching { Muestreo.disparar(context, dia, i, t) }
    }
}
