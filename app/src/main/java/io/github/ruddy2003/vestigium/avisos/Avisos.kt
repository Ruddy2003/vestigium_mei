package io.github.ruddy2003.vestigium.avisos

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.ruddy2003.vestigium.R
import io.github.ruddy2003.vestigium.ui.MainActivity
import io.github.ruddy2003.vestigium.ui.QuickActivity

object Avisos {
    const val CANAL_ANIMO = "animo"
    const val CANAL_DIARIO = "diario"
    const val CANAL_ESCALAS = "escalas"
    const val CANAL_REPORTES = "reportes"

    /** Sonido de cada tema: el de los avisos de ánimo y el solemne (diario, sueños, cartas, cuestionarios). */
    private val SONIDOS = mapOf(
        "romano" to (R.raw.n_romano_aviso to R.raw.n_romano_solemne), "ciber" to (R.raw.n_ciber_aviso to R.raw.n_ciber_solemne),
        "heleno" to (R.raw.n_heleno_aviso to R.raw.n_heleno_solemne), "medieval" to (R.raw.n_medieval_aviso to R.raw.n_medieval_solemne),
        "distopia" to (R.raw.n_distopia_aviso to R.raw.n_distopia_solemne), "refugio" to (R.raw.n_refugio_aviso to R.raw.n_refugio_solemne),
        "orden" to (R.raw.n_orden_aviso to R.raw.n_orden_solemne), "laboratorio" to (R.raw.n_laboratorio_aviso to R.raw.n_laboratorio_solemne),
        "colonos" to (R.raw.n_colonos_aviso to R.raw.n_colonos_solemne),
        "napoleon" to (R.raw.n_napoleon_aviso to R.raw.n_napoleon_solemne), "botanico" to (R.raw.n_botanico_aviso to R.raw.n_botanico_solemne),
        "navidad" to (R.raw.n_navidad_aviso to R.raw.n_navidad_solemne), "seda" to (R.raw.n_seda_aviso to R.raw.n_seda_solemne),
        "urss" to (R.raw.n_urss_aviso to R.raw.n_urss_solemne),
        "gotico" to (R.raw.n_gotico_aviso to R.raw.n_gotico_solemne), "espacio" to (R.raw.n_espacio_aviso to R.raw.n_espacio_solemne),
        "andalus" to (R.raw.n_andalus_aviso to R.raw.n_andalus_solemne), "mongol" to (R.raw.n_mongol_aviso to R.raw.n_mongol_solemne),
        "renacimiento" to (R.raw.n_renacimiento_aviso to R.raw.n_renacimiento_solemne),
    )

    private fun nombreCanal(base: String) = when (base) {
        CANAL_ANIMO -> "Ánimo durante el día"; CANAL_DIARIO -> "Diario y sueños"; CANAL_ESCALAS -> "Escalas periódicas"; else -> "Reportes"
    }
    private fun importancia(base: String) = when (base) {
        CANAL_ANIMO -> NotificationManager.IMPORTANCE_HIGH; CANAL_REPORTES -> NotificationManager.IMPORTANCE_LOW; else -> NotificationManager.IMPORTANCE_DEFAULT
    }

    /** Recurso de sonido para un canal según el tema elegido (null = sonido normal del teléfono). */
    fun sonido(base: String, tema: String = runCatching { io.github.ruddy2003.vestigium.data.Repo.config.tema }.getOrDefault("romano")): Int? {
        val par = SONIDOS[tema] ?: SONIDOS.getValue("romano")
        return when (base) { CANAL_ANIMO -> par.first; CANAL_DIARIO, CANAL_ESCALAS -> par.second; else -> null }
    }

    fun uriSonido(ctx: Context, res: Int): Uri = Uri.parse("android.resource://${ctx.packageName}/$res")

    /**
     * Android no deja cambiar el sonido de un canal ya creado, así que hay un canal por tema («animo_medieval»…).
     * Al cambiar de tema se crea el nuevo copiando lo que hayas decidido en el anterior (silencio, importancia, vibración)
     * y se borra el viejo.
     */
    fun canal(ctx: Context, base: String): String {
        if (base == CANAL_REPORTES) return base
        val nm = ctx.getSystemService(NotificationManager::class.java)
        val c = runCatching { io.github.ruddy2003.vestigium.data.Repo.config }.getOrNull()
        val propio = c?.sonido_avisos ?: true
        val tema = c?.tema ?: "romano"
        val id = base + "_" + if (propio) tema else "sistema"
        if (nm.getNotificationChannel(id) != null) return id
        val viejos = nm.notificationChannels.filter { it.id == base || it.id.startsWith(base + "_") }
        val previo = viejos.firstOrNull()
        val ch = NotificationChannel(id, nombreCanal(base), previo?.importance ?: importancia(base)).apply {
            description = when (base) { CANAL_ANIMO -> "Avisos breves para registrar cómo te sientes"; CANAL_DIARIO -> "Recordatorio de la mañana y de la noche"; else -> "Cuestionarios que tocan" }
            val atributos = android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            when {
                previo != null && previo.sound == null -> setSound(null, null)          // lo habías silenciado: sigue en silencio
                propio -> sonido(base, tema)?.let { setSound(uriSonido(ctx, it), atributos) }
            }
            previo?.let { enableVibration(it.shouldVibrate()); setShowBadge(it.canShowBadge()) }
        }
        nm.createNotificationChannel(ch)
        viejos.forEach { nm.deleteNotificationChannel(it.id) }
        return id
    }

    fun crearCanales(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(CANAL_REPORTES, nombreCanal(CANAL_REPORTES), importancia(CANAL_REPORTES)))
        listOf(CANAL_ANIMO, CANAL_DIARIO, CANAL_ESCALAS).forEach { runCatching { canal(ctx, it) } }
    }

    fun permitido(ctx: Context): Boolean =
        android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    private fun mostrar(ctx: Context, id: Int, b: NotificationCompat.Builder) {
        if (!permitido(ctx)) return
        try { NotificationManagerCompat.from(ctx).notify(id, b.build()) } catch (e: SecurityException) { }
    }

    private fun quick(ctx: Context, req: Int, modo: String, extras: Intent.() -> Unit = {}): PendingIntent {
        val i = Intent(ctx, QuickActivity::class.java).putExtra(QuickActivity.MODO, modo)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK).apply(extras)
        return PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun abrirApp(ctx: Context, req: Int, pantalla: String): PendingIntent {
        val i = Intent(ctx, MainActivity::class.java).putExtra(MainActivity.PANTALLA, pantalla).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(ctx, req, i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    fun muestreo(ctx: Context, indice: Int, programadoMs: Long) {
        val id = 1000 + indice
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_ANIMO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Cómo te sientes ahora?")
            .setContentText("Dos toques: qué tan agradable y cuánta energía.")
            .setContentIntent(quick(ctx, id, QuickActivity.M_ANIMO) { putExtra(QuickActivity.AVISO_MS, programadoMs); putExtra(QuickActivity.NOTIF, id) })
            .setAutoCancel(true)
            .setTimeoutAfter(60 * 60 * 1000L)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
        mostrar(ctx, id, b)
    }

    fun sueno(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Recuerdas lo que soñaste?")
            .setContentText("Anótalo antes de levantarte; el recuerdo se borra en minutos.")
            .setContentIntent(quick(ctx, 2001, QuickActivity.M_SUENO))
            .setAutoCancel(true)
            .setTimeoutAfter(3 * 60 * 60 * 1000L)
        mostrar(ctx, 2001, b)
    }

    /** «¿Cómo terminó lo de anoche?»: abre la hoja para completar duración, máximo y cruda. Sin nombrar la sustancia si está oculta o en modo discreto. */
    fun loDeAnoche(ctx: Context, c: io.github.ruddy2003.vestigium.data.Consumo, n: Int) {
        val cfg = io.github.ruddy2003.vestigium.data.Repo.config
        val nombrar = !cfg.discreto && io.github.ruddy2003.vestigium.data.Sustancias.visible(c)
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Cómo terminó lo de anoche?")
            .setContentText((if (nombrar) io.github.ruddy2003.vestigium.data.Catalogo.nombreSustancia(c) + ": " else "") + "hasta qué hora duró, el máximo y cómo amaneciste." +
                if (n > 1) " Hay $n por completar." else "")
            .setContentIntent(quick(ctx, 2010, QuickActivity.M_CONSUMO) { putExtra(QuickActivity.CONSUMO, c.id); putExtra(QuickActivity.ORIGEN, "notificacion") })
            .setAutoCancel(true)
            .setTimeoutAfter(6 * 60 * 60 * 1000L)
        mostrar(ctx, 2010, b)
    }

    fun diario(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Cómo estuvo tu día?")
            .setContentText("Llena tu diario. Si fue un día difícil, basta con un número.")
            .setContentIntent(abrirApp(ctx, 2002, "dia"))
            .addAction(0, "Día difícil: solo un número", quick(ctx, 2003, QuickActivity.M_MINIMO))
            .setAutoCancel(true)
        mostrar(ctx, 2002, b)
    }

    fun escalas(ctx: Context, pendientes: List<Pair<io.github.ruddy2003.vestigium.ciencia.Instrumento, Long>>) {
        val peor = pendientes.maxOf { it.second }
        val nombres = pendientes.joinToString(", ") { it.first.nombre.substringBefore(" (") }
        val titulo = when {
            peor <= 0 -> "Hoy toca: $nombres"
            peor <= 3 -> "Pendiente desde hace $peor día${if (peor > 1) "s" else ""}: $nombres"
            else -> "Llevas $peor días de atraso: $nombres"
        }
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_ESCALAS))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText("Unos ${pendientes.size * 2} minutos. Mientras más regular, más comparables tus resultados.")
            .setContentIntent(abrirApp(ctx, 2004, "escalas"))
            .setPriority(if (peor > 3) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
        mostrar(ctx, 2004, b)
    }

    fun carta(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Tienes una carta de tu yo del pasado")
            .setContentText("Llegó la fecha que elegiste para abrirla.")
            .setContentIntent(abrirApp(ctx, 2005, "cartas"))
            .setAutoCancel(true)
        mostrar(ctx, 2005, b)
    }

    fun reporte(ctx: Context, titulo: String, uri: Uri, mime: String) {
        val ver = Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pi = PendingIntent.getActivity(ctx, 2006, ver, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(titulo)
            .setContentText("Toca para verlo. Está guardado en tu galería.")
            .setContentIntent(pi)
            .setAutoCancel(true)
        mostrar(ctx, 2006, b)
    }

    fun logros(ctx: Context, nombres: List<String>) {
        if (nombres.isEmpty()) return
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(if (nombres.size == 1) "Logro conseguido: ${nombres[0]}" else "${nombres.size} logros nuevos")
            .setContentText(nombres.take(4).joinToString(" · "))
            .setContentIntent(abrirApp(ctx, 2008, "logros"))
            .setAutoCancel(true)
        mostrar(ctx, 2008, b)
    }

    /** El 31 de diciembre por la mañana: «Tu año» ya está listo, con el sonido del tema. */
    fun tuAnio(ctx: Context, anio: Int) {
        val b = NotificationCompat.Builder(ctx, canal(ctx, CANAL_DIARIO))
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Tu $anio está listo")
            .setContentText("Un año entero en historias: lo que más hiciste, lo único, lo curioso. Toca para verlo.")
            .setContentIntent(abrirApp(ctx, 2009, "tu_anio"))
            .setAutoCancel(true)
        mostrar(ctx, 2009, b)
    }

    fun drive(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("El respaldo en Drive necesita tu permiso")
            .setContentText("Google pidió confirmar el acceso. Toca para volver a conectar.")
            .setContentIntent(abrirApp(ctx, 2007, "datos"))
            .setAutoCancel(true)
        mostrar(ctx, 2007, b)
    }
}
