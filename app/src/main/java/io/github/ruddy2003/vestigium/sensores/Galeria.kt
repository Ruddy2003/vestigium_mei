package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.FotosDia
import io.github.ruddy2003.vestigium.data.Repo

/** Fotos del día leídas de la galería. Solo se guarda cuántas hubo; las imágenes nunca se copian ni salen del teléfono. */
object Galeria {
    fun permisos(): Array<String> =
        if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    fun tienePermiso(ctx: Context): Boolean =
        ctx.checkSelfPermission(permisos()[0]) == PackageManager.PERMISSION_GRANTED

    /** Android 14 permite dar acceso solo a algunas fotos: en ese caso los conteos no son confiables. */
    fun parcial(ctx: Context): Boolean = Build.VERSION.SDK_INT >= 34 && !tienePermiso(ctx) &&
        ctx.checkSelfPermission("android.permission.READ_MEDIA_VISUAL_USER_SELECTED") == PackageManager.PERMISSION_GRANTED

    data class Foto(val id: Long, val uri: Uri, val ms: Long, val tipo: String, val favorita: Boolean)

    // El día de la app empieza a las 5:00: una foto a la 1:00 pertenece a la noche anterior.
    private fun ini(f: String) = Fechas.inicioDia(f) + 5 * 3_600_000L
    private fun fin(f: String) = Fechas.finDia(f) + 5 * 3_600_000L

    fun delDia(ctx: Context, fecha: String): List<Foto> {
        if (!tienePermiso(ctx) && !parcial(ctx)) return emptyList()
        val out = ArrayList<Foto>()
        val proy = arrayOf(MediaStore.Images.Media._ID, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.RELATIVE_PATH, MediaStore.MediaColumns.IS_FAVORITE)
        val a = ini(fecha); val b = fin(fecha)
        val sel = "(${MediaStore.MediaColumns.DATE_TAKEN} >= ? AND ${MediaStore.MediaColumns.DATE_TAKEN} < ?) OR " +
            "(${MediaStore.MediaColumns.DATE_TAKEN} IS NULL AND ${MediaStore.MediaColumns.DATE_ADDED} >= ? AND ${MediaStore.MediaColumns.DATE_ADDED} < ?)"
        val args = arrayOf(a.toString(), b.toString(), (a / 1000).toString(), (b / 1000).toString())
        runCatching {
            ctx.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proy, sel, args, "${MediaStore.MediaColumns.DATE_TAKEN} ASC")?.use { c ->
                while (c.moveToNext()) {
                    val id = c.getLong(0)
                    val ms = if (c.isNull(1)) c.getLong(2) * 1000 else c.getLong(1)
                    val ruta = (c.getString(3) ?: "").lowercase()
                    val tipo = when { "screenshot" in ruta -> "captura"; ruta.startsWith("dcim/") -> "camara"; else -> "otra" }
                    out.add(Foto(id, ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id), ms, tipo, c.getInt(4) == 1))
                }
            }
        }
        return out
    }

    private fun videos(ctx: Context, fecha: String): Int {
        val a = ini(fecha); val b = fin(fecha)
        return runCatching {
            ctx.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Video.Media._ID),
                "${MediaStore.MediaColumns.DATE_TAKEN} >= ? AND ${MediaStore.MediaColumns.DATE_TAKEN} < ?", arrayOf(a.toString(), b.toString()), null)?.use { it.count } ?: 0
        }.getOrDefault(0)
    }

    fun contar(ctx: Context, fecha: String): FotosDia? {
        if (!tienePermiso(ctx)) return null
        val l = delDia(ctx, fecha)
        val camara = l.filter { it.tipo == "camara" }
        return FotosDia(fecha = fecha, fotos = camara.size, capturas = l.count { it.tipo == "captura" }, otras = l.count { it.tipo == "otra" },
            videos = videos(ctx, fecha), primera = camara.firstOrNull()?.let { Fechas.isoDeMillis(it.ms) }, ultima = camara.lastOrNull()?.let { Fechas.isoDeMillis(it.ms) })
    }

    fun sincronizar(ctx: Context, dias: Int = 3) {
        if (!tienePermiso(ctx)) return
        for (i in 0 until dias) contar(ctx, Fechas.sumar(Fechas.diaLogico(), -i.toLong()))?.let { Repo.guardarFotos(it) }
    }

    /** Llena los días con registro que aún no tienen conteo de fotos (la galería conserva años de historia). */
    fun historia(ctx: Context, maxDias: Int = 400) {
        if (!tienePermiso(ctx)) return
        val fechas = (Repo.dias.keys + Repo.salud.keys).filter { it !in Repo.fotos }.sortedDescending().take(maxDias)
        fechas.forEach { f -> contar(ctx, f)?.let { Repo.guardarFotos(it) } }
    }

    fun miniatura(ctx: Context, uri: Uri, px: Int): Bitmap? =
        runCatching { ctx.contentResolver.loadThumbnail(uri, Size(px, px), null) }.getOrNull()
}
