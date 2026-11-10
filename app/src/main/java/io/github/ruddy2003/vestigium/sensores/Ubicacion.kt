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
        // Si se acaba la espera, la petición se cancela: antes seguía buscando señal (y gastando batería) aunque ya no se usara.
        val cancelar = CancellationTokenSource()
        return try {
            withTimeoutOrNull(esperaMs) {
                cliente.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancelar.token).await()
            } ?: cliente.lastLocation.await()
        } catch (e: Exception) {
            null
        } finally {
            cancelar.cancel()
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun ultima(ctx: Context): Location? {
        if (!tienePermiso(ctx)) return null
        return try { LocationServices.getFusedLocationProviderClient(ctx).lastLocation.await() } catch (e: Exception) { null }
    }

    /**
     * Muestra de fondo. Si la lectura tarda, Android da la última ubicación conocida, que puede ser la misma de los pulsos
     * anteriores: un punto que ya está no se vuelve a guardar ([Db.ponerMuestra]).
     */
    suspend fun muestrear(ctx: Context) {
        if (!Repo.config.muestreo_ubicacion || !tieneSegundoPlano(ctx)) return
        val loc = actual(ctx, 10000) ?: return
        Repo.db().ponerMuestra(loc.time.takeIf { it > 0 } ?: System.currentTimeMillis(), loc.latitude, loc.longitude, loc.accuracy, "app")
    }
}

object Geocercas {
    private fun intent(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, 4401, Intent(ctx, GeocercaReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    /**
     * Registra una geocerca por lugar. [soloSiCambio]: al arrancar el proceso (lo arranca también cada pulso, cada widget
     * y cada geocerca) no se vuelven a registrar si los lugares son los mismos y no ha pasado un día; antes se quitaban y
     * se ponían de nuevo cada vez, y una salida justo en ese momento se perdía.
     */
    @SuppressLint("MissingPermission")
    fun registrar(ctx: Context, soloSiCambio: Boolean = false) {
        if (!Ubicacion.tieneSegundoPlano(ctx)) return
        val cliente = LocationServices.getGeofencingClient(ctx)
        val pi = intent(ctx)
        // Android admite 100 geocercas por app; se usan 95. Con más lugares, antes se tomaban los 95 primeros por orden de
        // creación y los últimos nunca registraban visitas. Ahora, los 95 más cercanos a tu última ubicación conocida (sin
        // ubicación, como antes). Se dejan en su orden de siempre para que la firma solo cambie si cambia el conjunto.
        val todos = Repo.lugares
        val lugares = if (todos.size <= 95) todos else {
            val p = runCatching { Repo.db().ultimoPunto() }.getOrNull()
            if (p == null) todos.take(95) else {
                val cercanos = todos.sortedBy { io.github.ruddy2003.vestigium.data.Geo.distanciaM(p.lat, p.lon, it.lat, it.lon) }.take(95).mapTo(HashSet()) { it.id }
                todos.filter { it.id in cercanos }
            }
        }
        val firma = lugares.joinToString("|") { "${it.id},${it.lat},${it.lon},${it.radio_m}" }.hashCode()
        val prefs = ctx.getSharedPreferences("geocercas", Context.MODE_PRIVATE)
        val ahora = System.currentTimeMillis()
        if (soloSiCambio && prefs.getInt("firma", 0) == firma && ahora - prefs.getLong("ms", 0L) < 86_400_000L) return
        if (lugares.isEmpty()) { cliente.removeGeofences(pi); prefs.edit().putInt("firma", firma).putLong("ms", ahora).apply(); return }
        val cercas = lugares.map {
            Geofence.Builder()
                .setRequestId(it.id)
                .setCircularRegion(it.lat, it.lon, it.radio_m.toFloat().coerceAtLeast(80f))
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(120000)
                .build()
        }
        // Al registrar avisa también de los lugares donde ya no estás (INITIAL_TRIGGER_EXIT): cierra una visita que quedó
        // abierta porque Android no avisó la salida. A los lugares sin visita abierta no les pasa nada (Repo.salida).
        val req = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofences(cercas)
            .build()
        // Primero se quitan las anteriores y, al terminar, se agregan las nuevas.
        cliente.removeGeofences(pi).addOnCompleteListener {
            try {
                cliente.addGeofences(req, pi).addOnSuccessListener { prefs.edit().putInt("firma", firma).putLong("ms", ahora).apply() }
            } catch (e: SecurityException) { }
        }
    }
}

class GeocercaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        val ev = GeofencingEvent.fromIntent(intent) ?: return
        if (ev.hasError()) return
        val ts = ev.triggeringLocation?.time?.takeIf { it > 0 }?.let { Fechas.isoDeMillis(it) } ?: Fechas.ahoraIso()
        val ids = ev.triggeringGeofences?.map { it.requestId } ?: return
        // El punto primero: la entrada y la salida miran las ubicaciones para saber si saliste antes de este aviso.
        ev.triggeringLocation?.let { Repo.db().ponerMuestra(it.time, it.latitude, it.longitude, it.accuracy, "geocerca") }
        when (ev.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                // Si cerró la visita de un lugar del que Android no avisó la salida, se registran de nuevo para que avise al volver.
                val cerro = ids.map { Repo.entrada(it, ts) }.any { it }
                if (cerro) runCatching { Geocercas.registrar(context) }
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> ids.forEach { Repo.salida(it, ts) }
        }
    }
}
