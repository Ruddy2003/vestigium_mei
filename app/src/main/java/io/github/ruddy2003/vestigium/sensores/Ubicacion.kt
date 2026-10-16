package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

object Ubicacion {
    fun tienePermiso(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun tieneSegundoPlano(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Ubicación rápida: intenta una lectura actual de bajo consumo y, si tarda, usa la última conocida. */
    @SuppressLint("MissingPermission")
    suspend fun actual(ctx: Context, esperaMs: Long = 7000): Location? {
        if (!tienePermiso(ctx)) return null
        val cliente = LocationServices.getFusedLocationProviderClient(ctx)
        return try {
            withTimeoutOrNull(esperaMs) {
                cliente.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token).await()
            } ?: cliente.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun ultima(ctx: Context): Location? {
        if (!tienePermiso(ctx)) return null
        return try { LocationServices.getFusedLocationProviderClient(ctx).lastLocation.await() } catch (e: Exception) { null }
    }

    /** Muestra de fondo: se guarda solo si cambió algo respecto a la anterior o pasó tiempo suficiente. */
    suspend fun muestrear(ctx: Context) {
        if (!Repo.config.muestreo_ubicacion || !tieneSegundoPlano(ctx)) return
        val loc = actual(ctx, 10000) ?: return
        Repo.db().ponerUbicacion(loc.time.takeIf { it > 0 } ?: System.currentTimeMillis(), loc.latitude, loc.longitude, loc.accuracy, "app")
    }
}

object Geocercas {
    private fun intent(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 4401, Intent(ctx, GeocercaReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    @SuppressLint("MissingPermission")
    fun registrar(ctx: Context) {
        if (!Ubicacion.tieneSegundoPlano(ctx)) return
        val cliente = LocationServices.getGeofencingClient(ctx)
        val pi = intent(ctx)
        cliente.removeGeofences(pi)
        val lugares = Repo.lugares.take(95)
        if (lugares.isEmpty()) return
        val cercas = lugares.map {
            Geofence.Builder()
                .setRequestId(it.id)
                .setCircularRegion(it.lat, it.lon, it.radio_m.toFloat().coerceAtLeast(80f))
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(120000)
                .build()
        }
        val req = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(cercas)
            .build()
        try { cliente.addGeofences(req, pi) } catch (e: SecurityException) { }
    }
}

class GeocercaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        val ev = GeofencingEvent.fromIntent(intent) ?: return
        if (ev.hasError()) return
        val ts = ev.triggeringLocation?.time?.takeIf { it > 0 }?.let { Fechas.isoDeMillis(it) } ?: Fechas.ahoraIso()
        val ids = ev.triggeringGeofences?.map { it.requestId } ?: return
        when (ev.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> ids.forEach { Repo.entrada(it, ts) }
            Geofence.GEOFENCE_TRANSITION_EXIT -> ids.forEach { Repo.salida(it, ts) }
        }
        ev.triggeringLocation?.let { Repo.db().ponerUbicacion(it.time, it.latitude, it.longitude, it.accuracy, "geocerca") }
    }
}
