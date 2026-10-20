package io.github.ruddy2003.vestigium.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.ruddy2003.vestigium.R

/**
 * El cronómetro: un bloque de enfoque (estudio, lectura, idiomas, tesis, trabajo, algo creativo) o una siesta.
 * Mientras corre hay una notificación fija con el tiempo y un botón para terminar; al terminar queda un [Tramo].
 */
object Enfoque {
    const val CANAL = "enfoque"
    private const val NOTIF = 4100

    /** Lo que está corriendo ahora: tipo y milisegundos de inicio. */
    fun enCurso(c: Config = Repo.config): Pair<String, Long>? {
        val t = c.enfoque_tipo ?: return null
        val i = c.enfoque_inicio ?: return null
        return t to i
    }

    fun minutosCorriendo(): Int = enCurso()?.let { ((System.currentTimeMillis() - it.second) / 60_000).toInt() } ?: 0

    fun iniciar(ctx: Context, tipo: String) {
        if (enCurso() != null) terminar(ctx)
        val ahora = System.currentTimeMillis()
        Repo.cambiarConfig { it.copy(enfoque_tipo = tipo, enfoque_inicio = ahora) }
        notificar(ctx, tipo, ahora)
        refrescar(ctx)
    }

    /** Termina el bloque y lo guarda (si duró al menos un minuto). */
    fun terminar(ctx: Context, nota: String = ""): Tramo? {
        val (tipo, ini) = enCurso() ?: return null
        val fin = System.currentTimeMillis()
        val min = ((fin - ini) / 60_000).toInt()
        Repo.cambiarConfig { it.copy(enfoque_tipo = null, enfoque_inicio = null) }
        runCatching { NotificationManagerCompat.from(ctx).cancel(NOTIF) }
        val t = if (min >= 1) Tramo(Repo.nuevoId(), Fechas.diaLogicoDeMillis(ini), tipo, Fechas.isoDeMillis(ini), Fechas.isoDeMillis(fin), min, nota.trim())
            .also { Repo.guardarTramo(it) } else null
        refrescar(ctx)
        return t
    }

    /** Lo detiene sin guardar nada. */
    fun descartar(ctx: Context) {
        Repo.cambiarConfig { it.copy(enfoque_tipo = null, enfoque_inicio = null) }
        runCatching { NotificationManagerCompat.from(ctx).cancel(NOTIF) }
        refrescar(ctx)
    }

    /** Un tramo que ya pasó y anotas después (la siesta de la tarde, una hora de lectura). */
    fun agregar(tipo: String, fecha: String, hora: String, min: Int, nota: String = ""): Tramo? {
        if (min <= 0) return null
        val t = runCatching { java.time.LocalTime.parse(hora.trim().padStart(5, '0').take(5)) }.getOrNull() ?: return null
        // Antes de las 5:00 es la madrugada del día lógico: cae en la fecha de calendario siguiente.
        val dia = if (t.hour < 5) Fechas.parse(fecha).plusDays(1) else Fechas.parse(fecha)
        val ini = dia.atTime(t).atZone(Fechas.zona()).toInstant().toEpochMilli()
        val tr = Tramo(Repo.nuevoId(), fecha, tipo, Fechas.isoDeMillis(ini), Fechas.isoDeMillis(ini + min * 60_000L), min, nota.trim(), origen = "manual")
        Repo.guardarTramo(tr)
        return tr
    }

    /** Minutos por tipo de un día. */
    fun delDia(fecha: String): Map<String, Int> = Repo.tramos.filter { it.fecha == fecha }.groupBy { it.tipo }.mapValues { (_, l) -> l.sumOf { it.min } }

    private fun refrescar(ctx: Context) {
        runCatching { io.github.ruddy2003.vestigium.widget.Widgets.actualizar(ctx) }
        runCatching { android.service.quicksettings.TileService.requestListeningState(ctx, ComponentName(ctx, io.github.ruddy2003.vestigium.widget.MosaicoEnfoque::class.java)) }
    }

    private fun notificar(ctx: Context, tipo: String, desde: Long) {
        if (!io.github.ruddy2003.vestigium.avisos.Avisos.permitido(ctx)) return
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CANAL) == null) nm.createNotificationChannel(
            NotificationChannel(CANAL, "Cronómetro de enfoque", NotificationManager.IMPORTANCE_LOW).apply { description = "El bloque de enfoque o la siesta que está corriendo"; setShowBadge(false) })
        val abrir = PendingIntent.getActivity(ctx, 4101,
            Intent(ctx, io.github.ruddy2003.vestigium.ui.QuickActivity::class.java).putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.MODO, io.github.ruddy2003.vestigium.ui.QuickActivity.M_ENFOQUE)
                .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.ORIGEN, "notificacion").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val fin = PendingIntent.getBroadcast(ctx, 4102, Intent(ctx, EnfoqueReceiver::class.java).setAction(EnfoqueReceiver.TERMINAR),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(if (tipo == "siesta") "Siesta" else "Enfoque: ${Catalogo.nombreTramo(tipo).lowercase()}")
            .setContentText("Toca «terminar» cuando acabes.")
            .setWhen(desde).setShowWhen(true).setUsesChronometer(true)
            .setOngoing(true).setOnlyAlertOnce(true).setSilent(true)
            .setContentIntent(abrir)
            .addAction(0, "Terminar", fin)
        try { NotificationManagerCompat.from(ctx).notify(NOTIF, b.build()) } catch (e: SecurityException) { }
    }
}

/** El botón «Terminar» de la notificación del cronómetro. */
class EnfoqueReceiver : BroadcastReceiver() {
    companion object { const val TERMINAR = "io.github.ruddy2003.vestigium.ENFOQUE_TERMINAR" }
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        if (intent.action == TERMINAR) {
            val t = Enfoque.terminar(context)
            android.os.Handler(android.os.Looper.getMainLooper()).post {
                android.widget.Toast.makeText(context, if (t != null) "Guardado: ${t.min} min de ${Catalogo.nombreTramo(t.tipo).lowercase()}" else "Duró menos de un minuto; no se guardó.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
