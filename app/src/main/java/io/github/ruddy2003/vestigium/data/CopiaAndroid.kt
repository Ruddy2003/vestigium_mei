package io.github.ruddy2003.vestigium.data

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.app.backup.FullBackupDataOutput
import android.content.Context
import android.os.ParcelFileDescriptor
import java.io.File

/**
 * La copia de seguridad de Android como tercera capa (esquema 22, 9 nov 2026), **apagada por omisión**. Si la enciendes en
 * Datos, cuando Android hace su copia de la app (de noche, cargando y con wifi) se lleva una foto coherente de la base
 * de datos, y al instalar la app en un teléfono nuevo con la misma cuenta, esa base vuelve sola.
 *
 * Solo sale si el transporte va cifrado de punta a punta (con el bloqueo de pantalla del teléfono) o de teléfono a
 * teléfono: lo exigen las reglas (`res/xml/copia_android*.xml`) y este agente. Android admite hasta 25 MB por app; si la
 * base pasa de 24 MB, no se manda y se anota. No lleva las fotos del día ni la contraseña del respaldo.
 */
class CopiaAndroid : BackupAgent() {
    companion object {
        const val CARPETA = "copia_android"
        const val ARCHIVO = "vestigium.sqlite"
        private const val LIMITE = 24L * 1024 * 1024

        private fun prefs(ctx: Context) = ctx.getSharedPreferences("copia_android", Context.MODE_PRIVATE)
        fun activa(ctx: Context) = prefs(ctx).getBoolean("activa", false)
        fun activar(ctx: Context, v: Boolean) { prefs(ctx).edit().putBoolean("activa", v).apply(); if (!v) File(ctx.filesDir, CARPETA).deleteRecursively() }
        /** Lo que pasó la última vez que Android pidió la copia: fecha y resultado. */
        fun ultima(ctx: Context): Pair<String?, String> = prefs(ctx).getString("ultima", null) to prefs(ctx).getString("resultado", "").orEmpty()
        private fun anotar(ctx: Context, r: String) { prefs(ctx).edit().putString("ultima", Fechas.ahoraIso()).putString("resultado", r).apply() }

        /**
         * En una instalación nueva, antes de abrir la base: si Android trajo la copia y todavía no hay base, la copia pasa a
         * ser la base. Si ya hay base, no se toca nada.
         */
        fun restaurarSiHace(ctx: Context) {
            val copia = File(File(ctx.filesDir, CARPETA), ARCHIVO)
            val base = ctx.getDatabasePath("vestigium.db")
            if (!copia.exists() || base.exists()) return
            runCatching { base.parentFile?.mkdirs(); copia.copyTo(base); anotar(ctx, "Restauré la base que trajo la copia de Android.") }
        }
    }

    override fun onBackup(oldState: ParcelFileDescriptor?, data: BackupDataOutput?, newState: ParcelFileDescriptor?) {}
    override fun onRestore(data: BackupDataInput?, appVersionCode: Int, newState: ParcelFileDescriptor?) {}

    override fun onFullBackup(data: FullBackupDataOutput) {
        val dir = File(filesDir, CARPETA)
        dir.deleteRecursively()
        val cifrada = data.transportFlags and FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED != 0
        val directa = data.transportFlags and FLAG_DEVICE_TO_DEVICE_TRANSFER != 0
        if (activa(this) && (cifrada || directa)) {
            runCatching {
                dir.mkdirs()
                val f = File(dir, ARCHIVO)
                Db(applicationContext).use { it.copiaCruda(f) }
                if (f.length() > LIMITE) { f.delete(); anotar(this, "La base pesa más de 24 MB: Android no la acepta. Usa la carpeta o Drive.") }
                else anotar(this, "Copia de la base enviada a Android (" + "%.1f MB".format(f.length() / 1_048_576.0) + ").")
            }.onFailure { anotar(this, "No pude preparar la copia: ${it.message}") }
        } else if (activa(this)) anotar(this, "Android pidió la copia sin cifrado de punta a punta: no mandé nada. Pon un bloqueo de pantalla.")
        super.onFullBackup(data)
    }
}
