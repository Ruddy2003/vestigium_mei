package io.github.ruddy2003.vestigium.sensores

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.github.ruddy2003.vestigium.data.Escucha
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import java.time.LocalTime
import java.util.concurrent.Executors

/**
 * Escucha las notificaciones solo para contarlas (de qué app y a qué hora; jamás se lee ni se guarda su texto)
 * y las sesiones de música para anotar qué canción empezó a sonar.
 */
class Oyente : NotificationListenerService() {
    companion object {
        fun activo(ctx: Context): Boolean {
            val s = Settings.Secure.getString(ctx.contentResolver, "enabled_notification_listeners") ?: return false
            return s.split(":").any { ComponentName.unflattenFromString(it)?.packageName == ctx.packageName }
        }
    }

    private val hilo = Executors.newSingleThreadExecutor()
    private val vistos = HashMap<String, Long>()
    private var msm: MediaSessionManager? = null
    private val controles = HashMap<MediaSession.Token, Pair<MediaController, MediaController.Callback>>()
    private val sonadas = HashMap<String, Long>()
    private val alCambiar = MediaSessionManager.OnActiveSessionsChangedListener { enlazar(it) }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            if (sbn.packageName == packageName || sbn.isOngoing) return
            val n = sbn.notification ?: return
            if ((n.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return
            val ahora = System.currentTimeMillis()
            val previo = vistos.put(sbn.key, ahora)
            // Una actualización de la misma notificación no cuenta como otra.
            if (previo != null && (ahora - previo < 60_000 || (n.flags and Notification.FLAG_ONLY_ALERT_ONCE) != 0)) return
            if (vistos.size > 600) vistos.entries.removeIf { ahora - it.value > 6 * 3_600_000L }
            val pkg = sbn.packageName
            val hora = LocalTime.now().hour
            hilo.execute {
                runCatching {
                    Repo.init(applicationContext)
                    Repo.contarNotif(Fechas.diaLogicoDeMillis(ahora), Apps.nombre(applicationContext, pkg), hora)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        runCatching {
            val m = getSystemService(MediaSessionManager::class.java) ?: return
            msm = m
            val comp = ComponentName(this, Oyente::class.java)
            m.addOnActiveSessionsChangedListener(alCambiar, comp)
            enlazar(m.getActiveSessions(comp))
        }
    }

    override fun onListenerDisconnected() {
        runCatching { msm?.removeOnActiveSessionsChangedListener(alCambiar) }
        controles.values.forEach { (c, cb) -> runCatching { c.unregisterCallback(cb) } }
        controles.clear()
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        hilo.shutdown()
        super.onDestroy()
    }

    private fun enlazar(lista: List<MediaController>?) {
        val actuales = lista.orEmpty()
        val tokens = actuales.map { it.sessionToken }.toSet()
        controles.keys.filter { it !in tokens }.forEach { t -> controles.remove(t)?.let { (c, cb) -> runCatching { c.unregisterCallback(cb) } } }
        for (c in actuales) {
            if (controles.containsKey(c.sessionToken)) continue
            val cb = object : MediaController.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadata?) = revisar(c)
                override fun onPlaybackStateChanged(state: PlaybackState?) = revisar(c)
            }
            runCatching { c.registerCallback(cb, Handler(Looper.getMainLooper())) }
            controles[c.sessionToken] = c to cb
            revisar(c)
        }
    }

    private fun revisar(c: MediaController) {
        try {
            if (c.playbackState?.state != PlaybackState.STATE_PLAYING) return
            val md = c.metadata ?: return
            val titulo = md.getString(MediaMetadata.METADATA_KEY_TITLE)?.trim().orEmpty()
            if (titulo.isEmpty()) return
            val artista = (md.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: md.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: "").trim()
            val album = md.getString(MediaMetadata.METADATA_KEY_ALBUM)?.trim().orEmpty()
            val dur = md.getLong(MediaMetadata.METADATA_KEY_DURATION).takeIf { it > 0 }
            val clave = "${c.packageName}|$titulo|$artista"
            val ahora = System.currentTimeMillis()
            val antes = sonadas[clave]
            // La misma canción en pausa y reanudada no se cuenta dos veces.
            if (antes != null && ahora - antes < maxOf(dur ?: 0L, 180_000L) + 60_000L) return
            sonadas[clave] = ahora
            if (sonadas.size > 300) sonadas.entries.removeIf { ahora - it.value > 3 * 3_600_000L }
            val pkg = c.packageName
            hilo.execute {
                runCatching {
                    Repo.init(applicationContext)
                    Repo.guardarEscucha(Escucha(id = Repo.nuevoId(), ts = Fechas.isoDeMillis(ahora), fecha = Fechas.diaLogicoDeMillis(ahora),
                        titulo = titulo.take(200), artista = artista.take(200), album = album.take(200), app = Apps.nombre(applicationContext, pkg),
                        duracion_s = dur?.let { (it / 1000).toInt() }))
                }
            }
        } catch (_: Exception) {}
    }
}
