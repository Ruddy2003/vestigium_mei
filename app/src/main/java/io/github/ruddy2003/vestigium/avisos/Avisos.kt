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

    fun crearCanales(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        listOf(
            NotificationChannel(CANAL_ANIMO, "Ánimo durante el día", NotificationManager.IMPORTANCE_HIGH).apply { description = "Avisos breves para registrar cómo te sientes" },
            NotificationChannel(CANAL_DIARIO, "Diario y sueños", NotificationManager.IMPORTANCE_DEFAULT).apply { description = "Recordatorio de la mañana y de la noche" },
            NotificationChannel(CANAL_ESCALAS, "Escalas periódicas", NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(CANAL_REPORTES, "Reportes", NotificationManager.IMPORTANCE_LOW),
        ).forEach { nm.createNotificationChannel(it) }
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
        val b = NotificationCompat.Builder(ctx, CANAL_ANIMO)
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
        val b = NotificationCompat.Builder(ctx, CANAL_DIARIO)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Recuerdas lo que soñaste?")
            .setContentText("Anótalo antes de levantarte; el recuerdo se borra en minutos.")
            .setContentIntent(quick(ctx, 2001, QuickActivity.M_SUENO))
            .setAutoCancel(true)
            .setTimeoutAfter(3 * 60 * 60 * 1000L)
        mostrar(ctx, 2001, b)
    }

    fun diario(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_DIARIO)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("¿Cómo estuvo tu día?")
            .setContentText("Llena tu diario. Si fue un día difícil, basta con un número.")
            .setContentIntent(abrirApp(ctx, 2002, "dia"))
            .addAction(0, "Día difícil: solo un número", quick(ctx, 2003, QuickActivity.M_MINIMO))
            .setAutoCancel(true)
        mostrar(ctx, 2002, b)
    }

    fun escalas(ctx: Context, nombres: String) {
        val b = NotificationCompat.Builder(ctx, CANAL_ESCALAS)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle("Cuestionarios pendientes")
            .setContentText("$nombres · toma unos 2 minutos")
            .setContentIntent(abrirApp(ctx, 2004, "escalas"))
            .setAutoCancel(true)
        mostrar(ctx, 2004, b)
    }

    fun carta(ctx: Context) {
        val b = NotificationCompat.Builder(ctx, CANAL_DIARIO)
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
