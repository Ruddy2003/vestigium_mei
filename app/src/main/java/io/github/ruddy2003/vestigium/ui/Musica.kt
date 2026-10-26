package io.github.ruddy2003.vestigium.ui

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.coroutines.delay

/**
 * La música de cada tema: la presentación al elegirlo (una pantalla con su emblema, su nombre y una frase, como al completar el día),
 * y los tonos de llamada y de notificación para todo el teléfono. Los avisos de la app suenan siempre con su propio sonido,
 * distinto de estos, para que sepas que es Vestigium Mei.
 */
object Musica {
    fun presentacion(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.s_romano_presentacion
        TemaId.CIBER -> R.raw.s_ciber_presentacion
        TemaId.HELENO -> R.raw.s_heleno_presentacion
        TemaId.MEDIEVAL -> R.raw.s_medieval_presentacion
        TemaId.DISTOPIA -> R.raw.s_distopia_presentacion
        TemaId.REFUGIO -> R.raw.s_refugio_presentacion
        TemaId.ORDEN -> R.raw.s_orden_presentacion
        TemaId.LABORATORIO -> R.raw.s_laboratorio_presentacion
        TemaId.COLONOS -> R.raw.s_colonos_presentacion
        TemaId.NAPOLEON -> R.raw.s_napoleon_presentacion
        TemaId.BOTANICO -> R.raw.s_botanico_presentacion
        TemaId.NAVIDAD -> R.raw.s_navidad_presentacion
        TemaId.SEDA -> R.raw.s_seda_presentacion
        TemaId.URSS -> R.raw.s_urss_presentacion
        TemaId.GOTICO -> R.raw.s_gotico_presentacion
        TemaId.ESPACIO -> R.raw.s_espacio_presentacion
        TemaId.ANDALUS -> R.raw.s_andalus_presentacion
        TemaId.MONGOL -> R.raw.s_mongol_presentacion
        TemaId.RENACIMIENTO -> R.raw.s_renacimiento_presentacion
        TemaId.REVOLUCION -> R.raw.s_revolucion_presentacion
        TemaId.ANTIGUAS -> R.raw.s_antiguas_presentacion
    }
    /** La música larga de «Tu año»: la frase del tema que va creciendo (~1 min y medio). */
    fun anual(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.s_romano_anual
        TemaId.CIBER -> R.raw.s_ciber_anual
        TemaId.HELENO -> R.raw.s_heleno_anual
        TemaId.MEDIEVAL -> R.raw.s_medieval_anual
        TemaId.DISTOPIA -> R.raw.s_distopia_anual
        TemaId.REFUGIO -> R.raw.s_refugio_anual
        TemaId.ORDEN -> R.raw.s_orden_anual
        TemaId.LABORATORIO -> R.raw.s_laboratorio_anual
        TemaId.COLONOS -> R.raw.s_colonos_anual
        TemaId.NAPOLEON -> R.raw.s_napoleon_anual
        TemaId.BOTANICO -> R.raw.s_botanico_anual
        TemaId.NAVIDAD -> R.raw.s_navidad_anual
        TemaId.SEDA -> R.raw.s_seda_anual
        TemaId.URSS -> R.raw.s_urss_anual
        TemaId.GOTICO -> R.raw.s_gotico_anual
        TemaId.ESPACIO -> R.raw.s_espacio_anual
        TemaId.ANDALUS -> R.raw.s_andalus_anual
        TemaId.MONGOL -> R.raw.s_mongol_anual
        TemaId.RENACIMIENTO -> R.raw.s_renacimiento_anual
        TemaId.REVOLUCION -> R.raw.s_revolucion_anual
        TemaId.ANTIGUAS -> R.raw.s_antiguas_anual
    }
    fun llamada(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.t_romano_llamada
        TemaId.CIBER -> R.raw.t_ciber_llamada
        TemaId.HELENO -> R.raw.t_heleno_llamada
        TemaId.MEDIEVAL -> R.raw.t_medieval_llamada
        TemaId.DISTOPIA -> R.raw.t_distopia_llamada
        TemaId.REFUGIO -> R.raw.t_refugio_llamada
        TemaId.ORDEN -> R.raw.t_orden_llamada
        TemaId.LABORATORIO -> R.raw.t_laboratorio_llamada
        TemaId.COLONOS -> R.raw.t_colonos_llamada
        TemaId.NAPOLEON -> R.raw.t_napoleon_llamada
        TemaId.BOTANICO -> R.raw.t_botanico_llamada
        TemaId.NAVIDAD -> R.raw.t_navidad_llamada
        TemaId.SEDA -> R.raw.t_seda_llamada
        TemaId.URSS -> R.raw.t_urss_llamada
        TemaId.GOTICO -> R.raw.t_gotico_llamada
        TemaId.ESPACIO -> R.raw.t_espacio_llamada
        TemaId.ANDALUS -> R.raw.t_andalus_llamada
        TemaId.MONGOL -> R.raw.t_mongol_llamada
        TemaId.RENACIMIENTO -> R.raw.t_renacimiento_llamada
        TemaId.REVOLUCION -> R.raw.t_revolucion_llamada
        TemaId.ANTIGUAS -> R.raw.t_antiguas_llamada
    }
    fun notificacion(t: TemaId): Int = when (t) {
        TemaId.ROMANO -> R.raw.t_romano_notificacion
        TemaId.CIBER -> R.raw.t_ciber_notificacion
        TemaId.HELENO -> R.raw.t_heleno_notificacion
        TemaId.MEDIEVAL -> R.raw.t_medieval_notificacion
        TemaId.DISTOPIA -> R.raw.t_distopia_notificacion
        TemaId.REFUGIO -> R.raw.t_refugio_notificacion
        TemaId.ORDEN -> R.raw.t_orden_notificacion
        TemaId.LABORATORIO -> R.raw.t_laboratorio_notificacion
        TemaId.COLONOS -> R.raw.t_colonos_notificacion
        TemaId.NAPOLEON -> R.raw.t_napoleon_notificacion
        TemaId.BOTANICO -> R.raw.t_botanico_notificacion
        TemaId.NAVIDAD -> R.raw.t_navidad_notificacion
        TemaId.SEDA -> R.raw.t_seda_notificacion
        TemaId.URSS -> R.raw.t_urss_notificacion
        TemaId.GOTICO -> R.raw.t_gotico_notificacion
        TemaId.ESPACIO -> R.raw.t_espacio_notificacion
        TemaId.ANDALUS -> R.raw.t_andalus_notificacion
        TemaId.MONGOL -> R.raw.t_mongol_notificacion
        TemaId.RENACIMIENTO -> R.raw.t_renacimiento_notificacion
        TemaId.REVOLUCION -> R.raw.t_revolucion_notificacion
        TemaId.ANTIGUAS -> R.raw.t_antiguas_notificacion
    }

    private var reproductor: MediaPlayer? = null

    /** Toca un recurso de principio a fin (respeta el modo silencio). Si ya sonaba otro, lo corta. */
    fun tocar(ctx: Context, res: Int, forzar: Boolean = false) {
        detener()
        if (!forzar) {
            if (!Repo.config.sonidos) return
            val audio = ctx.getSystemService(AudioManager::class.java)
            if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return
        }
        runCatching {
            val mp = MediaPlayer()
            mp.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            ctx.resources.openRawResourceFd(res).use { fd -> mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            mp.setOnCompletionListener { it.release(); if (reproductor === it) reproductor = null }
            mp.prepare(); mp.start()
            reproductor = mp
        }
    }

    /** Para «Tu año»: la música larga, que se puede bajar poco a poco y se repite si las historias duran más. */
    fun anualTocar(ctx: Context, t: TemaId): MediaPlayer? {
        detener()
        if (!Repo.config.sonidos) return null
        val audio = ctx.getSystemService(AudioManager::class.java)
        if (audio != null && audio.ringerMode != AudioManager.RINGER_MODE_NORMAL) return null
        return runCatching {
            val mp = MediaPlayer()
            mp.setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            ctx.resources.openRawResourceFd(anual(t)).use { fd -> mp.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length) }
            mp.isLooping = true
            mp.prepare(); mp.start()
            reproductor = mp
            mp
        }.getOrNull()
    }

    fun detener() {
        reproductor?.let { runCatching { it.stop() }; runCatching { it.release() } }
        reproductor = null
    }
}

/** La presentación del tema recién elegido. */
object PresentacionTema {
    var tema by mutableStateOf<TemaId?>(null)
    var frase by mutableStateOf<Frases.Frase?>(null)
    var vuelta by mutableIntStateOf(0)

    fun mostrar(ctx: Context, t: TemaId) {
        Sentidos.vibrarEvento(ctx, Sentidos.Evento.TEMA)
        Musica.tocar(ctx, Musica.presentacion(t))
        if (!Repo.config.animaciones) return
        frase = Frases.de(t).random()
        tema = t
        vuelta++
        Fiesta.lanzar()
    }

    fun cerrar() { tema = null }
}

@Composable
fun CapaPresentacionTema() {
    val t = PresentacionTema.tema ?: return
    val vuelta = PresentacionTema.vuelta
    val f = PresentacionTema.frase
    val p = remember(vuelta) { Animatable(if (Repo.config.animaciones) 0f else 1f) }
    val e = MaterialTheme.colorScheme
    LaunchedEffect(vuelta) {
        if (!Repo.config.animaciones) return@LaunchedEffect
        delay(250)
        p.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        delay(7000)
        p.animateTo(0f, tween(600))
        if (PresentacionTema.vuelta == vuelta) PresentacionTema.cerrar()
    }
    Box(Modifier.fillMaxSize().graphicsLayer { alpha = p.value }.background(e.background.copy(alpha = 0.95f))
        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { PresentacionTema.cerrar() }) {
        CapaFiesta()
        Column(Modifier.align(Alignment.Center).padding(horizontal = 34.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.graphicsLayer { val s = 0.6f + 0.4f * p.value; scaleX = s; scaleY = s }) { Emblema(t, 132.dp, e, animado = Repo.config.animaciones) }
            Text(t.nombre, style = MaterialTheme.typography.headlineMedium, color = e.primary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 22.dp))
            Meandro(Modifier.width(200.dp).padding(top = 8.dp, bottom = 18.dp))
            f?.let { fr ->
                fr.original?.let { Text(it, style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic), color = e.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 8.dp)) }
                Text("«" + fr.texto + "»", style = MaterialTheme.typography.titleMedium, color = e.onBackground, textAlign = TextAlign.Center)
                Text("— " + fr.autor, style = MaterialTheme.typography.labelMedium, color = e.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp))
            }
            Text(t.descripcion, style = MaterialTheme.typography.bodySmall, color = e.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 26.dp))
            Text("toca para seguir", style = MaterialTheme.typography.labelSmall, color = e.outline, modifier = Modifier.padding(top = 14.dp))
        }
    }
}

/** Tonos de llamada y de notificación del tema para todo el teléfono. */
object TonosTelefono {
    /** Copia el tono a la carpeta de tonos del teléfono (Ringtones/Vestigium o Notifications/Vestigium) y devuelve su dirección. */
    fun instalar(ctx: Context, t: TemaId, llamada: Boolean): Uri? = runCatching {
        val archivo = "vestigium_${t.id}_${if (llamada) "llamada" else "notificacion"}.ogg"
        val col = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        ctx.contentResolver.query(col, arrayOf(MediaStore.Audio.Media._ID), "${MediaStore.Audio.Media.DISPLAY_NAME} = ?", arrayOf(archivo), null)?.use { c ->
            if (c.moveToFirst()) return@runCatching ContentUris.withAppendedId(col, c.getLong(0))
        }
        val v = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, archivo)
            put(MediaStore.Audio.Media.TITLE, "Vestigium · ${t.nombre}")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/ogg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, (if (llamada) Environment.DIRECTORY_RINGTONES else Environment.DIRECTORY_NOTIFICATIONS) + "/Vestigium")
            put(MediaStore.Audio.Media.IS_RINGTONE, llamada); put(MediaStore.Audio.Media.IS_NOTIFICATION, !llamada)
            put(MediaStore.Audio.Media.IS_ALARM, false); put(MediaStore.Audio.Media.IS_MUSIC, false)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = ctx.contentResolver.insert(col, v) ?: return@runCatching null
        ctx.contentResolver.openOutputStream(uri)?.use { out -> ctx.resources.openRawResource(if (llamada) Musica.llamada(t) else Musica.notificacion(t)).use { it.copyTo(out) } }
        ctx.contentResolver.update(uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        uri
    }.getOrNull()

    fun puedeCambiar(ctx: Context): Boolean = Settings.System.canWrite(ctx)

    /** Android pide un permiso aparte («modificar ajustes del sistema») para cambiar los tonos predeterminados. */
    fun pedirPermiso(ctx: Context) {
        runCatching { ctx.startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + ctx.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    /** Pone el tono del tema como predeterminado del teléfono. Los avisos de la app siguen con su propio sonido. */
    fun poner(ctx: Context, t: TemaId, llamada: Boolean): String {
        if (!puedeCambiar(ctx)) { pedirPermiso(ctx); return "Activa «Modificar ajustes del sistema» para Vestigium Mei y vuelve a tocar el botón." }
        val uri = instalar(ctx, t, llamada) ?: return "No pude guardar el tono en el teléfono."
        return runCatching {
            RingtoneManager.setActualDefaultRingtoneUri(ctx, if (llamada) RingtoneManager.TYPE_RINGTONE else RingtoneManager.TYPE_NOTIFICATION, uri)
            // Para que los avisos de la app nunca suenen igual que el resto del teléfono.
            if (!llamada && !Repo.config.sonido_avisos) Repo.cambiarConfig { it.copy(sonido_avisos = true) }
            if (llamada) "Listo: tu tono de llamada ahora es el de ${t.nombre}." else "Listo: las notificaciones del teléfono suenan con ${t.nombre}; las de Vestigium Mei, con su propio sonido."
        }.getOrElse { "El teléfono no dejó cambiar el tono." }
    }
}
