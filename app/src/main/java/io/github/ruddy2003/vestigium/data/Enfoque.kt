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
 *   (2 h con 3 descansos de 10 min = 4 bloques de 30 min). La duración de cada bloque y de cada descanso es su **meta**,
 *   no un corte (esquema 21): al llegar a ella suena un aviso ([PlanEnfoque.avisado]) y el reloj **sigue contando** lo
 *   de más. Tú decides cuándo cambiar: en un bloque, «Pasar a descanso» ([pasarADescanso]); en un descanso, «Volver al
 *   enfoque» ([volverAlEnfoque]); en el último bloque, «Terminar» ([terminar]). Se puede pasar al descanso antes de la
 *   meta, pausar y terminar en cualquier momento.
 * - Siesta: con alarma opcional para despertar; al sonar sigue contando hasta que la termines.
 * Cada bloque de enfoque se guarda como un [Tramo] con su largo real ([Tramo.min], sin pausas y con lo de más) y los
 * minutos planeados ([Tramo.plan_min]). Una alarma exacta da el aviso de la meta aunque la app esté cerrada, y la
 * notificación fija lleva la cuenta regresiva (o, pasada la meta, la cuenta hacia arriba) y los botones.
 * Un plan guardado por una versión anterior con [PlanEnfoque.esperando] se respeta: pregunta «¿Empezamos el bloque N?»
 * hasta que toques «Empezar» ([reanudar]); ya no se crea ese estado.
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
    /** Lo que falta para la meta del segmento (nunca baja de 0; pasada la meta, ver [extra]). Null sin meta. */
    fun restante(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()): Long? = segmentos(p).getOrNull(p.segmento)?.ms?.let { (it - transcurrido(p, ahora)).coerceAtLeast(0) }
    /** Lo de más: los ms que el segmento lleva sobre su meta (0 si no ha llegado o no tiene meta). */
    fun extra(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()): Long =
        segmentos(p).getOrNull(p.segmento)?.ms?.takeIf { !p.esperando }?.let { (transcurrido(p, ahora) - it).coerceAtLeast(0) } ?: 0L
    /** ¿El segmento ya llegó a su meta? (Ya avisado, o el tiempo ya la alcanzó.) Falso sin meta. */
    fun pasadoDeMeta(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()): Boolean {
        val ms = segmentos(p).getOrNull(p.segmento)?.ms ?: return false
        return !p.esperando && (p.avisado || transcurrido(p, ahora) >= ms)
    }
    /** ¿Después del segmento actual viene un descanso? (Solo en un bloque que no es el último.) */
    fun hayDescansoDespues(p: PlanEnfoque) = !enDescanso(p) && segmentos(p).getOrNull(p.segmento + 1)?.enfoque == false
    fun enDescanso(p: PlanEnfoque) = segmentos(p).getOrNull(p.segmento)?.enfoque == false
    fun enPausa(p: PlanEnfoque) = p.seg_inicio == null && !p.esperando
    /** Enfoque de toda la sesión: lo guardado más lo que va del bloque actual. */
    fun enfoqueTotal(p: PlanEnfoque, ahora: Long = System.currentTimeMillis()) = p.hecho_ms + if (enDescanso(p) || p.esperando) 0 else transcurrido(p, ahora)
    /**
     * A qué hora termina todo si no pausas y cambias justo en cada meta (null en libre). Si el segmento actual ya pasó su
     * meta, lo que sigue empieza ahora ([restante] es 0).
     */
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
        val p = PlanEnfoque(tipo, metaMin?.takeIf { it > 0 }, descansos.coerceIn(0, 12), descansoMin.coerceIn(1, 180), inicio = ahora, seg_inicio = ahora, seg_primer_inicio = ahora)
        Repo.cambiarConfig { it.copy(enfoque_ultimo = p.copy(inicio = 0L, seg_inicio = null, seg_primer_inicio = null)) }
        guardar(ctx, p)
    }

    fun pausar(ctx: Context) {
        val p = plan() ?: return
        if (p.seg_inicio == null) return
        guardar(ctx, p.copy(seg_acumulado_ms = transcurrido(p), seg_inicio = null, pausas = p.pausas + 1))
    }

    /** Reanuda una pausa o empieza el bloque que estaba esperando tu confirmación (planes de antes del esquema 21). */
    fun reanudar(ctx: Context) {
        val p = plan() ?: return
        if (p.seg_inicio != null) return
        val ahora = System.currentTimeMillis()
        guardar(ctx, p.copy(seg_inicio = ahora, esperando = false, seg_primer_inicio = p.seg_primer_inicio ?: ahora))
        NotificationManagerCompat.from(ctx).cancel(NOTIF_AVISO)
    }

    /**
     * «Pasar a descanso»: en un bloque de enfoque que no es el último, guarda el bloque con su largo real (con lo de más,
     * sin pausas) y empieza el descanso que sigue. Se puede antes de la meta. En el último bloque no hace nada: ahí solo
     * se termina.
     */
    fun pasarADescanso(ctx: Context) {
        val p = plan() ?: return
        if (p.esperando || !hayDescansoDespues(p)) return
        val ahora = System.currentTimeMillis()
        val hecho = if (guardarBloque(p, ahora) != null) transcurrido(p, ahora) else 0L
        guardar(ctx, p.copy(segmento = p.segmento + 1, seg_inicio = ahora, seg_acumulado_ms = 0, seg_primer_inicio = ahora, esperando = false,
            avisado = false, hecho_ms = p.hecho_ms + hecho))
        NotificationManagerCompat.from(ctx).cancel(NOTIF_AVISO)
    }

    /** «Volver al enfoque»: termina el descanso (antes o después de su meta) y empieza el siguiente bloque. */
    fun volverAlEnfoque(ctx: Context) {
        val p = plan() ?: return
        if (!enDescanso(p)) return
        val sig = p.segmento + 1
        val ahora = System.currentTimeMillis()
        if (sig >= segmentos(p).size) { completar(ctx, p); return }
        guardar(ctx, p.copy(segmento = sig, seg_inicio = ahora, seg_acumulado_ms = 0, seg_primer_inicio = ahora, esperando = false, avisado = false))
        NotificationManagerCompat.from(ctx).cancel(NOTIF_AVISO)
    }

    /** El nombre de antes de [volverAlEnfoque]. */
    fun saltarDescanso(ctx: Context) = volverAlEnfoque(ctx)

    /**
     * Termina la sesión: guarda lo que llevaba el bloque actual con su largo real (si es de un minuto o más, con lo de más
     * sobre la meta) y le pone la nota a todos los bloques de esta sesión. Un descanso en curso no se guarda. Devuelve los
     * minutos de enfoque (o de siesta) guardados en total.
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

    /**
     * Revisa si el segmento actual llegó a su meta (lo llaman la alarma, la tarea de 15 minutos y la pantalla del reloj).
     * No corta ni cambia de segmento: la primera vez avisa con sonido y guarda [PlanEnfoque.avisado]; el tiempo sigue
     * corriendo. Con un margen de 1,5 s por si la alarma llega un poco antes. Ya avisado, solo refresca la notificación
     * fija (el «+N min»).
     */
    fun avanzar(ctx: Context, ahora: Long = System.currentTimeMillis()) {
        val p = plan() ?: return
        val dur = segmentos(p).getOrNull(p.segmento)?.ms
        if (dur == null || p.esperando || p.seg_inicio == null) { programar(ctx, p); return }
        if (p.avisado) { notificar(ctx, p); return }
        if (transcurrido(p, ahora) + 1500 < dur) { programar(ctx, p); return }
        val q = p.copy(avisado = true)
        guardar(ctx, q)
        val n = bloques(q)
        val k = numeroBloque(q)
        when {
            q.tipo == "siesta" -> avisar(ctx, voz().despertar, "Siesta de ${dur / MIN} min. Sigue contando: toca «Terminar» cuando te levantes.")
            enDescanso(q) -> avisar(ctx, voz().finDescanso, "Se acabó el descanso · vuelve al enfoque cuando quieras: sigue el bloque ${k + 1} de $n.")
            hayDescansoDespues(q) -> avisar(ctx, voz().meta, "Llegaste a la meta del bloque $k de $n · sigue contando; pasa al descanso cuando quieras. ${voz().consejo}")
            n > 1 -> avisar(ctx, voz().meta, "Llegaste a la meta del bloque $k de $n · sigue contando; termina cuando quieras.")
            else -> avisar(ctx, voz().meta, "Llegaste a la meta de ${horasMin((dur / MIN).toInt())} · sigue contando; termina cuando quieras.")
        }
    }

    private fun completar(ctx: Context, q: PlanEnfoque) {
        val min = Math.round(q.hecho_ms / 60_000.0).toInt()
        limpiar(ctx)
        if (q.tipo == "siesta") avisar(ctx, voz().despertar, "Siesta de $min min.")
        else avisar(ctx, voz().terminaste, "${horasMin(min)} de ${Catalogo.nombreTramo(q.tipo).lowercase()}" + if (bloques(q) > 1) " en ${bloques(q)} bloques." else ".")
    }

    private fun horasMin(m: Int) = if (m < 60) "$m min" else "${m / 60} h" + if (m % 60 > 0) " ${m % 60} min" else ""

    private fun prefijo(p: PlanEnfoque) = "enf${p.inicio}_"

    /** Le pone la nota («qué hiciste») a todos los bloques de una sesión. */
    fun anotarSesion(inicio: Long, nota: String) {
        Repo.tramos.filter { it.id.startsWith("enf${inicio}_") }.forEach { Repo.guardarTramo(it.copy(nota = nota.trim())) }
    }

    /** Guarda el bloque actual como tramo: [Tramo.min] es lo real (sin las pausas, con lo de más) y [Tramo.plan_min], su meta. */
    private fun guardarBloque(p: PlanEnfoque, fin: Long, nota: String = ""): Tramo? {
        val ms = transcurrido(p, fin)
        if (ms < MIN) return null
        val ini = p.seg_primer_inicio ?: (fin - ms)
        val planMin = segmentos(p).getOrNull(p.segmento)?.ms?.let { (it / MIN).toInt() }
        val t = Tramo(prefijo(p) + p.segmento, Fechas.diaLogicoDeMillis(ini), p.tipo, Fechas.isoDeMillis(ini), Fechas.isoDeMillis(fin), Math.round(ms / 60_000.0).toInt(),
            nota = nota.trim(), origen = if (p.meta_min != null) "plan" else "cronometro", ts_creado = Fechas.ahoraIso(), plan_min = planMin)
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
        runCatching { NotificationManagerCompat.from(ctx).cancel(NOTIF_AVISO) }
        refrescar(ctx)
    }

    // ---------------------------------------------------------------- alarma y notificaciones

    private fun intentAlarma(ctx: Context) = PendingIntent.getBroadcast(ctx, 4103, Intent(ctx, EnfoqueReceiver::class.java).setAction(EnfoqueReceiver.AVANZAR),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    /** Una alarma exacta para el aviso de la meta del segmento, si aún no se dio (si el sistema no deja, una aproximada). */
    private fun programar(ctx: Context, p: PlanEnfoque?) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        val pi = intentAlarma(ctx)
        am.cancel(pi)
        p ?: return
        if (p.avisado || p.esperando) return
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
    }

    /** Textos del cronómetro en la voz del tema. */
    private fun voz() = io.github.ruddy2003.vestigium.avisos.Avisos.vozEnfoque()

    /** El primario del tema, para el color de las notificaciones. */
    private fun color(ctx: Context) = runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.colorTema(ctx) }.getOrNull()

    private fun abrirReloj(ctx: Context, req: Int = 4101) = PendingIntent.getActivity(ctx, req,
        Intent(ctx, io.github.ruddy2003.vestigium.ui.EnfoqueActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    private fun accion(ctx: Context, accion: String, req: Int) = PendingIntent.getBroadcast(ctx, req, Intent(ctx, EnfoqueReceiver::class.java).setAction(accion),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    /**
     * La notificación fija: cuenta regresiva hasta la meta; pasada la meta (y en libre), hacia arriba con lo que lleva el
     * segmento y «+N min» en el título. Los botones que tocan según el estado (Android muestra hasta tres).
     */
    private fun notificar(ctx: Context, p: PlanEnfoque) {
        if (!io.github.ruddy2003.vestigium.avisos.Avisos.permitido(ctx)) return
        canales(ctx)
        val ahora = System.currentTimeMillis()
        val nombre = if (p.tipo == "siesta") "Siesta" else Catalogo.nombreTramo(p.tipo)
        val n = bloques(p)
        val bloque = if (n > 1) " · bloque ${numeroBloque(p)} de $n" else ""
        val b = NotificationCompat.Builder(ctx, CANAL).setSmallIcon(R.drawable.ic_stat).setOngoing(true).setOnlyAlertOnce(true).setSilent(true)
            .setContentIntent(abrirReloj(ctx)).setCategory(NotificationCompat.CATEGORY_STOPWATCH)
        color(ctx)?.let { b.setColor(it) }
        val rest = restante(p, ahora)
        val pasado = pasadoDeMeta(p, ahora)
        val mas = extra(p, ahora) / MIN
        val descanso = enDescanso(p)
        val conDescanso = hayDescansoDespues(p)
        val luego = "Luego sigue $nombre".lowercase().replaceFirstChar { it.uppercase() }
        when {
            p.esperando -> b.setContentTitle("¿Empezamos el bloque ${numeroBloque(p)} de $n?").setContentText("$nombre · toca «Empezar» cuando estés listo")
                .addAction(0, "Empezar", accion(ctx, EnfoqueReceiver.REANUDAR, 4105))
            enPausa(p) -> {
                b.setContentTitle("En pausa · $nombre$bloque").setContentText(when {
                    pasado -> "+$mas min sobre la meta de este " + if (descanso) "descanso" else "bloque"
                    rest != null -> "Quedan ${(rest + MIN - 1) / MIN} min de este " + if (descanso) "descanso" else "bloque"
                    else -> "Llevas ${transcurrido(p, ahora) / MIN} min"
                }).addAction(0, "Reanudar", accion(ctx, EnfoqueReceiver.REANUDAR, 4105))
                if (descanso) b.addAction(0, "Volver al enfoque", accion(ctx, EnfoqueReceiver.VOLVER, 4106))
                else if (conDescanso) b.addAction(0, "Pasar a descanso", accion(ctx, EnfoqueReceiver.DESCANSO, 4110))
            }
            descanso && pasado -> b.setContentTitle("${voz().descanso} · +$mas min").setContentText("$luego · vuelve cuando quieras")
                .setWhen(ahora - transcurrido(p, ahora)).setShowWhen(true).setUsesChronometer(true)
                .addAction(0, "Volver al enfoque", accion(ctx, EnfoqueReceiver.VOLVER, 4106))
            descanso -> b.setContentTitle("${voz().descanso}$bloque").setContentText(luego)
                .setWhen(ahora + (rest ?: 0)).setShowWhen(true).setUsesChronometer(true).setChronometerCountDown(true)
                .addAction(0, "Volver al enfoque", accion(ctx, EnfoqueReceiver.VOLVER, 4106))
            pasado -> {
                b.setContentTitle(when {
                    p.tipo == "siesta" -> "Siesta · +$mas min desde la alarma"
                    mas < 1 -> "Llegaste a la meta$bloque"
                    else -> "+$mas min sobre la meta$bloque"
                }).setContentText(when {
                    p.tipo == "siesta" -> "Toca «Terminar» cuando te levantes"
                    conDescanso -> "$nombre · pasa al descanso cuando quieras"
                    else -> "$nombre · termina cuando quieras"
                }).setWhen(ahora - transcurrido(p, ahora)).setShowWhen(true).setUsesChronometer(true)
                if (conDescanso) b.addAction(0, "Pasar a descanso", accion(ctx, EnfoqueReceiver.DESCANSO, 4110))
                b.addAction(0, "Pausar", accion(ctx, EnfoqueReceiver.PAUSAR, 4107))
            }
            rest != null -> {
                b.setContentTitle("$nombre$bloque").setContentText("Meta a las " + Fechas.horaDe(Fechas.isoDeMillis(ahora + rest)))
                    .setWhen(ahora + rest).setShowWhen(true).setUsesChronometer(true).setChronometerCountDown(true)
                if (conDescanso) b.addAction(0, "Pasar a descanso", accion(ctx, EnfoqueReceiver.DESCANSO, 4110))
                b.addAction(0, "Pausar", accion(ctx, EnfoqueReceiver.PAUSAR, 4107))
            }
            else -> b.setContentTitle(nombre).setContentText("Toca para ver el reloj")
                .setWhen(ahora - transcurrido(p, ahora)).setShowWhen(true).setUsesChronometer(true)
                .addAction(0, "Pausar", accion(ctx, EnfoqueReceiver.PAUSAR, 4107))
        }
        b.addAction(0, "Terminar", accion(ctx, EnfoqueReceiver.TERMINAR, 4102))
        try { NotificationManagerCompat.from(ctx).notify(NOTIF, b.build()) } catch (e: SecurityException) { }
    }

    /** Un aviso con sonido (llegaste a la meta, se acabó el descanso, hora de despertar), con el botón que toca después. */
    private fun avisar(ctx: Context, titulo: String, texto: String) {
        if (!io.github.ruddy2003.vestigium.avisos.Avisos.permitido(ctx)) return
        canales(ctx)
        // El canal del fin de bloque lleva el sonido del tema: uno por tema («enfoque_aviso_<tema>»), como los de Avisos.
        val canal = runCatching { io.github.ruddy2003.vestigium.avisos.Avisos.canal(ctx, CANAL_AVISO) }.getOrDefault(CANAL_AVISO)
        val b = NotificationCompat.Builder(ctx, canal).setSmallIcon(R.drawable.ic_stat).setContentTitle(titulo).setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto)).setAutoCancel(true).setTimeoutAfter(45 * MIN)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_ALARM).setContentIntent(abrirReloj(ctx, 4108))
        color(ctx)?.let { b.setColor(it) }
        plan()?.let { p ->
            when {
                p.esperando -> b.addAction(0, "Empezar", accion(ctx, EnfoqueReceiver.REANUDAR, 4109))
                enDescanso(p) -> b.addAction(0, "Volver al enfoque", accion(ctx, EnfoqueReceiver.VOLVER, 4111))
                hayDescansoDespues(p) -> b.addAction(0, "Pasar a descanso", accion(ctx, EnfoqueReceiver.DESCANSO, 4112))
                else -> b.addAction(0, "Terminar", accion(ctx, EnfoqueReceiver.TERMINAR, 4113))
            }
        }
        try { NotificationManagerCompat.from(ctx).notify(NOTIF_AVISO, b.build()) } catch (e: SecurityException) { }
    }

    // ---------------------------------------------------------------- tramos que ya pasaron

    /** Un tramo que ya pasó y anotas después (la siesta de la tarde, una hora de lectura). */
    fun agregar(tipo: String, fecha: String, hora: String, min: Int, nota: String = ""): Tramo? {
        if (min <= 0) return null
        val t = runCatching { java.time.LocalTime.parse(hora.trim().padStart(5, '0').take(5)) }.getOrNull() ?: return null
        // Antes de las 5:00 es la madrugada del día lógico: cae en la fecha de calendario siguiente.
        val dia = if (t.hour < 5) Fechas.parse(fecha).plusDays(1) else Fechas.parse(fecha)
        val ini = Fechas.millisDe(dia.atTime(t))
        val tr = Tramo(Repo.nuevoId(), fecha, tipo, Fechas.isoDeMillis(ini), Fechas.isoDeMillis(ini + min * 60_000L), min, nota.trim(), origen = "manual", ts_creado = Fechas.ahoraIso())
        Repo.guardarTramo(tr)
        return tr
    }

    /** Un tramo que ya pasó, con su inicio exacto. */
    fun agregarMs(tipo: String, inicioMs: Long, min: Int, nota: String = ""): Tramo? {
        if (min <= 0) return null
        val tr = Tramo(Repo.nuevoId(), Fechas.diaLogicoDeMillis(inicioMs), tipo, Fechas.isoDeMillis(inicioMs), Fechas.isoDeMillis(inicioMs + min * 60_000L), min, nota.trim(), origen = "manual", ts_creado = Fechas.ahoraIso())
        Repo.guardarTramo(tr)
        return tr
    }

    /** Minutos por tipo de un día. */
    fun delDia(fecha: String): Map<String, Int> = Repo.tramos.filter { it.fecha == fecha }.groupBy { it.tipo }.mapValues { (_, l) -> l.sumOf { it.min } }
}

/** Los botones de la notificación del cronómetro y la alarma del aviso de la meta. */
class EnfoqueReceiver : BroadcastReceiver() {
    companion object {
        const val TERMINAR = "io.github.ruddy2003.vestigium.ENFOQUE_TERMINAR"
        const val AVANZAR = "io.github.ruddy2003.vestigium.ENFOQUE_AVANZAR"
        const val PAUSAR = "io.github.ruddy2003.vestigium.ENFOQUE_PAUSAR"
        const val REANUDAR = "io.github.ruddy2003.vestigium.ENFOQUE_REANUDAR"
        /** Pasar a descanso (esquema 21). */
        const val DESCANSO = "io.github.ruddy2003.vestigium.ENFOQUE_DESCANSO"
        /** Volver al enfoque desde el descanso (esquema 21). */
        const val VOLVER = "io.github.ruddy2003.vestigium.ENFOQUE_VOLVER"
        /** El nombre de antes de [VOLVER] (por si queda alguna notificación vieja con ese botón). */
        const val SALTAR = "io.github.ruddy2003.vestigium.ENFOQUE_SALTAR"
    }
    override fun onReceive(context: Context, intent: Intent) {
        Repo.init(context)
        when (intent.action) {
            AVANZAR -> Enfoque.avanzar(context)
            PAUSAR -> Enfoque.pausar(context)
            REANUDAR -> Enfoque.reanudar(context)
            DESCANSO -> Enfoque.pasarADescanso(context)
            VOLVER, SALTAR -> Enfoque.volverAlEnfoque(context)
            TERMINAR -> {
                val min = Enfoque.terminar(context)
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    android.widget.Toast.makeText(context, if (min > 0) "Guardado: $min min" else "Duró menos de un minuto; no se guardó.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
