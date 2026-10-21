package io.github.ruddy2003.vestigium.data

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.ruddy2003.vestigium.R

/**
 * El cronómetro de enfoque y siestas.
 * - Libre: cuenta hacia arriba hasta que lo terminas.
 * - Con meta: el tiempo de enfoque se reparte solo en bloques iguales con los descansos en medio
 *   (2 h con 3 descansos de 10 min = 4 bloques de 30 min). Al acabar un bloque empieza el descanso; al acabar el
 *   descanso pregunta antes de empezar el siguiente bloque. Se puede pausar, saltar el descanso o terminar antes.
 * - Siesta: con alarma opcional para despertar.
 * Cada bloque de enfoque se guarda como un [Tramo] (sin contar pausas). Una alarma exacta avanza de segmento aunque la app
 * esté cerrada, y la notificación fija lleva la cuenta regresiva y los botones.
 */
object Enfoque {
    const val CANAL = "enfoque"
    const val CANAL_AVISO = "enfoque_aviso"
    private const val NOTIF = 4100
    private const val NOTIF_AVISO = 4104
    private const val MIN = 60_000L

    class Seg(val enfoque: Boolean, val ms: Long?)

    /** Los segmentos del plan: bloques de enfoque y descansos intercalados. */
    fun segmentos(p: PlanEnfoque): List<Seg> {
        val meta = p.meta_min ?: return listOf(Seg(true, null))
        if (p.tipo == "siesta" || p.descansos <= 0) return listOf(Seg(true, meta * MIN))
        val k = p.descansos + 1
        val base = meta / k; val resto = meta - base * k
        return (0 until k).flatMap { i ->
            val b = Seg(true, (base + if (i < resto) 1 else 0) * MIN)
            if (i < k - 1) listOf(b, Seg(false, p.descanso_min * MIN)) else listOf(b)
        }
    }
    fun bloques(p: PlanEnfoque) = segmentos(p).count { it.enfoque }
    /** Número de bloque (1, 2, 3…) del segmento actual. */
    fun numeroBloque(p: PlanEnfoque) = segmentos(p).take(p.segmento + 1).count { it.enfoque }.coerceAtLeast(1)

    /** El plan en curso (también lo que dejó corriendo una versión anterior, como cronómetro libre). */
    fun plan(c: Config = Repo.config): PlanEnfoque? = c.enfoque_plan
        ?: c.enfoque_tipo?.let { t -> c.enfoque_inicio?.let { PlanEnfoque(t, inicio = it, seg_inicio = it, seg_primer_inicio = it) } }

    /** Lo que está corriendo ahora: tipo y milisegundos de inicio de la sesión. */
    fun enCurso(c: Config = Repo.config): Pair<String, Long>? = plan(c)?.let { it.tipo to it.inicio }

    fun transcurrido(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()) = p.seg_acumulado_ms + (p.seg_inicio?.let { (ahora - it).coerceAtLeast(0) } ?: 0L)
    fun restante(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()): Long? = segmentos(p).getOrNull(p.segmento)?.ms?.let { (it - transcurrido(p, ahora)).coerceAtLeast(0) }
    fun enDescanso(p: PlanEnfoque) = segmentos(p).getOrNull(p.segmento)?.enfoque == false
    fun enPausa(p: PlanEnfoque) = p.seg_inicio == null && !p.esperando
    /** Enfoque de toda la sesión: lo guardado más lo que va del bloque actual. */
    fun enfoqueTotal(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()) = p.hecho_ms + if (enDescanso(p) || p.esperando) 0 else transcurrido(p, ahora)
    /** A qué hora termina todo si no pausas (null en libre). */
    fun finEstimado(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()): Long? {
        val segs = segmentos(p)
        if (segs.any { it.ms == null }) return null
        return ahora + (restante(p, ahora) ?: 0) + segs.drop(p.segmento + 1).sumOf { it.ms ?: 0 }
    }

    fun minutosCorriendo(): Int = plan()?.let { (enfoqueTotal(it) / MIN).toInt() } ?: 0

    // ---------------------------------------------------------------- acciones

    fun iniciar(ctx: Context, tipo: String) = iniciarPlan(ctx, tipo, null, 0, 10)

    fun iniciarPlan(ctx: Context, tipo: String, metaMin: Int?, descansos: Int, descansoMin: Int) {
        if (plan() != null) terminar(ctx)
        val ahora = System.currentTimeMillis()
        val p = PlanEnfoque(tipo, metaMin?.takeIf { it > 0 }, descansos.coerceIn(0, 12), descansoMin.coerceIn(1, 60), inicio = ahora, seg_inicio = ahora, seg_primer_inicio = ahora)
        Repo.cambiarConfig { it.copy(enfoque_ultimo = p.copy(inicio = 0L, seg_inicio = null, seg_primer_inicio = null)) }
        guardar(ctx, p)
    }

    fun pausar(ctx: Context) {
        val p = plan() ?: return
        if (p.seg_inicio == null) return
        guardar(ctx, p.copy(seg_acumulado_ms = transcurrido(p), seg_inicio = null, pausas = p.pausas + 1))
    }

    /** Reanuda una pausa o empieza el bloque que estaba esperando tu confirmación. */
    fun reanudar(ctx: Context) {
        val p = plan() ?: return
        if (p.seg_inicio != null) return
        val ahora = System.currentTimeMillis()
        guardar(ctx, p.copy(seg_inicio = ahora, esperando = false, seg_primer_inicio = p.seg_primer_inicio ?: ahora))
        NotificationManagerCompat.from(ctx).cancel(NOTIF_AVISO)
    }

    /** Termina el descanso ya y empieza el siguiente bloque. */
    fun saltarDescanso(ctx: Context) {
        val p = plan() ?: return
        if (!enDescanso(p)) return
        val sig = p.segmento + 1
        val ahora = System.currentTimeMillis()
        if (sig >= segmentos(p).size) { completar(ctx, p); return }
        guardar(ctx, p.copy(segmento = sig, seg_inicio = ahora, seg_acumulado_ms = 0, seg_primer_inicio = ahora, esperando = false))
    }

    /**
     * Termina la sesión: guarda lo que llevaba el bloque actual (si es de un minuto o más) y le pone la nota a todos los
     * bloques de esta sesión. Devuelve los minutos de enfoque (o de siesta) guardados en total.
     */
    fun terminar(ctx: Context, nota: String = ""): Int {
        val p = plan() ?: return 0
        val ahora = System.currentTimeMillis()
        var total = p.hecho_ms
        if (!enDescanso(p) && !p.esperando) { val t = guardarBloque(p, ahora, nota); if (t != null) total += transcurrido(p, ahora) }
        if (nota.isNotBlank()) anotarSesion(p.inicio, nota)
        limpiar(ctx)
        return Math.round(total / 60_000.0).toInt()
    }

    /** Lo detiene sin guardar el bloque en curso (los bloques ya terminados se quedan). */
    fun descartar(ctx: Context) { limpiar(ctx) }

    /** Revisa si el segmento actual ya terminó y avanza (lo llama la alarma y la pantalla del reloj). */
    fun avanzar(ctx: Context, ahora: Long = System.currentTimeMillis()) {
        var q = plan() ?: return
        val segs = segmentos(q)
        var cambio: String? = null
        while (true) {
            val cur = segs.getOrNull(q.segmento) ?: break
            val dur = cur.ms ?: break
            val ini = q.seg_inicio ?: break
            val fin = ini + dur - q.seg_acumulado_ms
            if (fin > ahora + 1500) break
            if (cur.enfoque) { guardarBloque(q, fin); q = q.copy(hecho_ms = q.hecho_ms + dur) }
            val sig = q.segmento + 1
            if (sig >= segs.size) { completar(ctx, q); return }
            q = if (segs[sig].enfoque) { cambio = "esperando"; q.copy(segmento = sig, seg_inicio = null, seg_acumulado_ms = 0, seg_primer_inicio = null, esperando = true) }
            else { cambio = "descanso"; q.copy(segmento = sig, seg_inicio = fin, seg_acumulado_ms = 0, seg_primer_inicio = fin, esperando = false) }
        }
        if (cambio == null) { programar(ctx, q); return }
        guardar(ctx, q)
        when (cambio) {
            "descanso" -> avisar(ctx, "Descanso de ${segs[q.segmento].ms!! / MIN} min", "Bloque ${numeroBloque(q)} de ${bloques(q)} listo. Levántate, toma agua.")
            "esperando" -> avisar(ctx, "Se acabó el descanso", "¿Empezamos el bloque ${numeroBloque(q)} de ${bloques(q)}? Toca «Empezar» cuando estés listo.")
        }
    }

    private fun completar(ctx: Context, q: PlanEnfoque) {
        val min = Math.round(q.hecho_ms / 60_000.0).toInt()
        limpiar(ctx)
        if (q.tipo == "siesta") avisar(ctx, "Hora de despertar", "Siesta de $min min.")
        else avisar(ctx, "¡Terminaste!", "${horasMin(min)} de ${Catalogo.nombreTramo(q.tipo).lowercase()}" + if (bloques(q) > 1) " en ${bloques(q)} bloques." else ".")
    }

    private fun horasMin(m: Int) = if (m < 60) "$m min" else "${m / 60} h" + if (m % 60 > 0) " ${m % 60} min" else ""

    private fun prefijo(p: PlanEnfoque) = "enf${p.inicio}_"

    /** Le pone la nota («qué hiciste») a todos los bloques de una sesión. */
    fun anotarSesion(inicio: Long, nota: String) {
        Repo.tramos.filter { it.id.startsWith("enf${inicio}_") }.forEach { Repo.guardarTramo(it.copy(nota = nota.trim())) }
    }

    /** Guarda el bloque actual como tramo (sin las pausas). */
    private fun guardarBloque(p: PlanEnfoque, fin: Long, nota: String = ""): Tramo? {
        val ms = transcurrido(p, fin)
        if (ms < MIN) return null
        val ini = p.seg_primer_inicio ?: (fin - ms)
        val t = Tramo(prefijo(p) + p.segmento, Fechas.diaLogicoDeMillis(ini), p.tipo, Fechas.isoDeMillis(ini), Fechas.isoDeMillis(fin), Math.round(ms / 60_000.0).toInt(),
            nota = nota.trim(), origen = if (p.meta_min != null) "plan" else "cronometro")
        Repo.guardarTramo(t)
        return t
    }

    private fun guardar(ctx: Context, p: PlanEnfoque) {
        Repo.cambiarConfig { it.copy(enfoque_plan = p, enfoque_tipo = p.tipo, enfoque_inicio = p.inicio) }
        programar(ctx, p)
        notificar(ctx, p)
        refrescar(ctx)
    }

    private fun limpiar(ctx: Context) {
        Repo.cambiarConfig { it.copy(enfoque_plan = null, enfoque_tipo = null, enfoque_inicio = null) }
        programar(ctx, null)
        runCatching { NotificationManagerCompat.from(ctx).cancel(NOTIF) }
        refrescar(ctx)
    }

    // ---------------------------------------------------------------- alarma y notificaciones

    private fun intentAlarma(ctx: Context) = PendingIntent.getBroadcast(ctx, 4103, Intent(ctx, EnfoqueReceiver::class.java).setAction(EnfoqueReceiver.AVANZAR),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    /** Una alarma exacta para el fin del segmento (si el sistema no deja, una aproximada). */
    private fun programar(ctx: Context, p: PlanEnfoque?) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        val pi = intentAlarma(ctx)
        am.cancel(pi)
        p ?: return
        val dur = segmentos(p).getOrNull(p.segmento)?.ms ?: return
        val ini = p.seg_inicio ?: return
        val at = ini + dur - p.seg_acumulado_ms
        try {
            if (Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
            else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } catch (e: SecurityException) { am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi) }
    }

    private fun refrescar(ctx: Context) {
        runCatching { io.github.ruddy2003.vestigium.widget.Widgets.actualizar(ctx) }
        runCatching { android.service.quicksettings.TileService.requestListeningState(ctx, ComponentName(ctx, io.github.ruddy2003.vestigium.widget.MosaicoEnfoque::class.java)) }
    }

    private fun canales(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CANAL) == null) nm.createNotificationChannel(
            NotificationChannel(CANAL, "Cronómetro de enfoque", NotificationManager.IMPORTANCE_LOW).apply { description = "El bloque de enfoque o la siesta que está corriendo"; setShowBadge(false) })
        if (nm.getNotificationChannel(CANAL_AVISO) == null) nm.createNotificationChannel(
            NotificationChannel(CANAL_AVISO, "Fin de bloque o descanso", NotificationManager.IMPORTANCE_HIGH).apply { description = "Avisa cuando empieza un descanso, cuando termina y cuando acabas"; enableVibration(true) })
    }

    private fun abrirReloj(ctx: Context, req: Int = 4101) = PendingIntent.getActivity(ctx, req,
        Intent(ctx, io.github.ruddy2003.vestigium.ui.EnfoqueActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun accion(ctx: Context, accion: String, req: Int) = PendingIntent.getBroadcast(ctx, req, Intent(ctx, EnfoqueReceiver::class.java).setAction(accion),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    /** La notificación fija: cuenta regresiva (o hacia arriba en libre) y los botones que tocan según el estado. */
    private fun notificar(ctx: Context, p: PlanEnfoque) {
        if (!io.github.ruddy2003.vestigium.avisos.Avisos.permitido(ctx)) return
        canales(ctx)
        val ahora = System.currentTimeMillis()
        val nombre = if (p.tipo == "siesta") "Siesta" else Catalogo.nombreTramo(p.tipo)
        val n = bloques(p)
        val bloque = if (n > 1) " · bloque ${numeroBloque(p)} de $n" else ""
        val b = NotificationCompat.Builder(ctx, CANAL).setSmallIcon(R.drawable.ic_stat).setOngoing(true).setOnlyAlertOnce(true).setSilent(true)
            .setContentIntent(abrirReloj(ctx)).setCategory(NotificationCompat.CATEGORY_STOPWATCH)
        val rest = restante(p, ahora)
        when {
            p.esperando -> b.setContentTitle("¿Empezamos el bloque ${numeroBloque(p)} de $n?").setContentText("$nombre · toca «Empezar» cuando estés listo")
                .addAction(0, "Empezar", accion(ctx, EnfoqueReceiver.REANUDAR, 4105))
            enPausa(p) -> b.setContentTitle("En pausa · $nombre$bloque").setContentText(rest?.let { "Quedan ${(it + MIN - 1) / MIN} min de este " + if (enDescanso(p)) "descanso" else "bloque" } ?: "Llevas ${transcurrido(p, ahora) / MIN} min")
                .addAction(0, "Reanudar", accion(ctx, EnfoqueReceiver.REANUDAR, 4105))
            enDescanso(p) -> b.setContentTitle("Descanso$bloque").setContentText("Luego sigue $nombre".lowercase().replaceFirstChar { it.uppercase() })
                .setWhen(ahora + (rest ?: 0)).setShowWhen(true).setUsesChronometer(true).setChronometerCountDown(true)
                .addAction(0, "Saltar descanso", accion(ctx, EnfoqueReceiver.SALTAR, 4106))
            rest != null -> b.setContentTitle("$nombre$bloque").setContentText("Termina a las " + Fechas.horaDe(Fechas.isoDeMillis(ahora + rest)))
                .setWhen(ahora + rest).setShowWhen(true).setUsesChronometer(true).setChronometerCountDown(true)
                .addAction(0, "Pausar", accion(ctx, EnfoqueReceiver.PAUSAR, 4107))
            else -> b.setContentTitle(nombre).setContentText("Toca para ver el reloj")
                .setWhen(ahora - transcurrido(p, ahora)).setShowWhen(true).setUsesChronometer(true)
                .addAction(0, "Pausar", accion(ctx, EnfoqueReceiver.PAUSAR, 4107))
        }
        b.addAction(0, "Terminar", accion(ctx, EnfoqueReceiver.TERMINAR, 4102))
        try { NotificationManagerCompat.from(ctx).notify(NOTIF, b.build()) } catch (e: SecurityException) { }
    }

    /** Un aviso con sonido en cada cambio (empieza el descanso, se acaba, terminaste). */
    private fun avisar(ctx: Context, titulo: String, texto: String) {
        if (!io.github.ruddy2003.vestigium.avisos.Avisos.permitido(ctx)) return
        canales(ctx)
        val b = NotificationCompat.Builder(ctx, CANAL_AVISO).setSmallIcon(R.drawable.ic_stat).setContentTitle(titulo).setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto)).setAutoCancel(true).setTimeoutAfter(45 * MIN)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_ALARM).setContentIntent(abrirReloj(ctx, 4108))
        if (plan()?.esperando == true) b.addAction(0, "Empezar", accion(ctx, EnfoqueReceiver.REANUDAR, 4109))
        try { NotificationManagerCompat.from(ctx).notify(NOTIF_AVISO, b.build()) } catch (e: SecurityException) { }
    }

    // ---------------------------------------------------------------- tramos que ya pasaron

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

    /** Un tramo que ya pasó, con su inicio exacto. */
    fun agregarMs(tipo: String, inicioMs: Long, min: Int, nota: String = ""): Tramo? {
        if (min <= 0) return null
        val tr = Tramo(Repo.nuevoId(), Fechas.diaLogicoDeMillis(inicioMs), tipo, Fechas.isoDeMillis(inicioMs), Fechas.isoDeMillis(inicioMs + min * 60_000L), min, nota.trim(), origen = "manual")
        Repo.guardarTramo(tr)
        return tr
    }

    /** Minutos por tipo de un día. */
    fun delDia(fecha: String): Map<String, Int> = Repo.tramos.filter { it.fecha == fecha }.groupBy { it.tipo }.mapValues { (_, l) -> l.sumOf { it.min } }
}

/** Los botones de la notificación del cronómetro y la alarma de fin de segmento. */
class EnfoqueReceiver : BroadcastReceiver() {
    companion object {
        const val TERMINAR = "io.github.ruddy2003.vestigium.ENFOQUE_TERMINAR"
        const val AVANZAR = "io.github.ruddy2003.vestigium.ENFOQUE_AVANZAR"
        const val PAUSAR = "io.github.ruddy2003.vestigium.ENFOQUE_PAUSAR"
        const val REANUDAR = "io.github.ruddy2003.vestigium.ENFOQUE_REANUDAR"
        const val SALTAR = "io.github.ruddy2003.vestigium.ENFOQUE_SALTAR"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        when (intent.action) {
            AVANZAR -> Enfoque.avanzar(context)
            PAUSAR -> Enfoque.pausar(context)
            REANUDAR -> Enfoque.reanudar(context)
            SALTAR -> Enfoque.saltarDescanso(context)
            TERMINAR -> {
                val min = Enfoque.terminar(context)
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    android.widget.Toast.makeText(context, if (min > 0) "Guardado: $min min" else "Duró menos de un minuto; no se guardó.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
