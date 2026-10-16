package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Ruido del ambiente en el instante en que registras algo: se escucha 1.5 segundos, se calcula el nivel y se descarta el audio.
 * El valor es aproximado (dB relativos sumando 90 a la escala digital), útil para comparar tus lugares entre sí.
 */
object Ambiente {
    fun tienePermiso(ctx: Context) = ctx.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    suspend fun ruido(ctx: Context, ms: Int = 1500): Double? = withContext(Dispatchers.IO) {
        if (!tienePermiso(ctx)) return@withContext null
        val tasa = 16_000
        val min = AudioRecord.getMinBufferSize(tasa, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        if (min <= 0) return@withContext null
        val rec = runCatching { AudioRecord(MediaRecorder.AudioSource.MIC, tasa, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, min * 2) }.getOrNull()
            ?: return@withContext null
        try {
            if (rec.state != AudioRecord.STATE_INITIALIZED) return@withContext null
            rec.startRecording()
            val buf = ShortArray(min)
            var suma = 0.0; var n = 0L
            val total = tasa * ms / 1000
            // Se descarta el primer cuarto de segundo: el micrófono tarda en estabilizarse.
            var leidas = 0
            while (leidas < total) {
                val r = rec.read(buf, 0, buf.size)
                if (r <= 0) break
                if (leidas > tasa / 4) for (i in 0 until r) { val x = buf[i].toDouble(); suma += x * x; n++ }
                leidas += r
            }
            if (n == 0L || suma == 0.0) return@withContext null
            val rms = sqrt(suma / n)
            Math.round((20 * log10(rms / 32768.0) + 90) * 10) / 10.0
        } catch (e: Exception) { null } finally { runCatching { rec.stop() }; rec.release() }
    }
}
