package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.SystemClock
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo

/** Detección de movimiento de Android: cuánto tiempo pasas a pie, en bici, en vehículo o quieto. Casi no gasta batería. */
object Actividad {
    val TIPOS = mapOf(
        DetectedActivity.WALKING to "a_pie", DetectedActivity.RUNNING to "corriendo", DetectedActivity.ON_BICYCLE to "bici",
        DetectedActivity.IN_VEHICLE to "vehiculo", DetectedActivity.STILL to "quieto",
    )
    val NOMBRES = mapOf("a_pie" to "a pie", "corriendo" to "corriendo", "bici" to "en bici", "vehiculo" to "en vehículo", "quieto" to "quieto")

    fun tienePermiso(ctx: Context) = ctx.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED

    private fun pi(ctx: Context): PendingIntent = PendingIntent.getBroadcast(ctx, 7070, Intent(ctx, ActividadReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

    @SuppressLint("MissingPermission")
    fun registrar(ctx: Context) {
        if (!tienePermiso(ctx)) return
        val lista = TIPOS.keys.flatMap { t ->
            listOf(ActivityTransition.ACTIVITY_TRANSITION_ENTER, ActivityTransition.ACTIVITY_TRANSITION_EXIT).map {
                ActivityTransition.Builder().setActivityType(t).setActivityTransition(it).build()
            }
        }
        runCatching { ActivityRecognition.getClient(ctx).requestActivityTransitionUpdates(ActivityTransitionRequest(lista), pi(ctx)) }
    }

    /** Reparte un tramo entre los días (que empiezan a las 5:00) que toca. */
    fun acumular(tipo: String, desde: Long, hasta: Long) {
        if (hasta <= desde || hasta - desde > 20 * 3_600_000L) return
        var a = desde
        while (a < hasta) {
            val dia = Fechas.diaLogicoDeMillis(a)
            val corte = Fechas.inicioDia(Fechas.sumar(dia, 1)) + 5 * 3_600_000L
            val b = minOf(hasta, corte)
            Repo.sumarActividad(dia, tipo, ((b - a) / 60_000).toInt())
            a = b
        }
    }
}

class ActividadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return
        val r = ActivityTransitionResult.extractResult(intent) ?: return
        Repo.init(context)
        val prefs = context.getSharedPreferences("actividad", Context.MODE_PRIVATE)
        for (e in r.transitionEvents.sortedBy { it.elapsedRealTimeNanos }) {
            val tipo = Actividad.TIPOS[e.activityType] ?: continue
            val ms = System.currentTimeMillis() - (SystemClock.elapsedRealtimeNanos() - e.elapsedRealTimeNanos) / 1_000_000
            val actual = prefs.getString("tipo", null)
            val desde = prefs.getLong("desde", 0)
            if (e.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                if (actual != null && desde > 0) Actividad.acumular(actual, desde, ms)
                prefs.edit().putString("tipo", tipo).putLong("desde", ms).apply()
            } else if (actual == tipo && desde > 0) {
                Actividad.acumular(tipo, desde, ms)
                prefs.edit().remove("tipo").remove("desde").apply()
            }
        }
    }
}
