package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.File

/**
 * La foto del día, guardada dentro de la app (esquema 22, 9 nov 2026). Antes solo se guardaba el enlace a la galería
 * (`Dia.foto`): en un teléfono nuevo, o si la foto se borraba de la galería, se perdía. Ahora se copia una versión reducida
 * (1600 px de lado, JPEG al 85 %) en `fotos_dia/<fecha>.jpg` y viaja en el respaldo; el enlace se conserva.
 */
object FotoDelDia {
    const val LADO = 1600
    const val CALIDAD = 85

    fun carpeta(ctx: Context): File = File(ctx.filesDir, "fotos_dia").apply { mkdirs() }

    fun nombre(fecha: String) = "$fecha.jpg"

    /** La copia guardada de [d], si existe. */
    fun archivo(ctx: Context, d: Dia?): File? = d?.foto_archivo?.let { File(carpeta(ctx), it) }?.takeIf { it.exists() && it.length() > 0 }

    /** Tamaño de destino para que el lado mayor no pase de [lado] (sin agrandar). */
    fun destino(ancho: Int, alto: Int, lado: Int = LADO): Pair<Int, Int> {
        val mayor = maxOf(ancho, alto)
        if (mayor <= lado || mayor <= 0) return ancho to alto
        val k = lado.toDouble() / mayor
        return maxOf(1, Math.round(ancho * k).toInt()) to maxOf(1, Math.round(alto * k).toInt())
    }

    /** Copia la foto de [uri] para [fecha]. El nombre del archivo, o null si no se pudo leer (el enlace ya no sirve). */
    fun copiar(ctx: Context, uri: Uri, fecha: String): String? = runCatching {
        val fuente = ImageDecoder.createSource(ctx.contentResolver, uri)
        val bmp = ImageDecoder.decodeBitmap(fuente) { dec, info, _ ->
            val (w, h) = destino(info.size.width, info.size.height)
            dec.setTargetSize(w, h)
            dec.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
        val n = nombre(fecha)
        val tmp = File(carpeta(ctx), "$n.nuevo")
        try {
            tmp.outputStream().buffered().use { if (!bmp.compress(Bitmap.CompressFormat.JPEG, CALIDAD, it)) error("no se pudo comprimir") }
        } finally { bmp.recycle() }
        val f = File(carpeta(ctx), n)
        if (!tmp.renameTo(f)) { f.delete(); tmp.renameTo(f) }
        n
    }.getOrNull()

    /** Si el día tiene foto elegida y todavía no su copia, la hace. true si quedó. */
    fun completar(ctx: Context, fecha: String): Boolean {
        val d = Repo.dias[fecha] ?: return false
        val u = d.foto ?: return false
        if (archivo(ctx, d) != null) return true
        val n = copiar(ctx, Uri.parse(u), fecha) ?: return false
        Repo.importarDias(listOf(Repo.dia(fecha).copy(foto_archivo = n)), sobrescribir = true)
        return true
    }

    /** Las copias que faltan de los días anteriores, mientras su enlace siga vivo (unas cuantas por pulso). */
    fun completarPendientes(ctx: Context, maximo: Int = 30): Int =
        Repo.dias.values.filter { it.foto != null && archivo(ctx, it) == null }.sortedByDescending { it.fecha }.take(maximo).count { completar(ctx, it.fecha) }

    /** Quitar la foto del día también quita su copia. */
    fun borrar(ctx: Context, fecha: String) { File(carpeta(ctx), nombre(fecha)).delete() }

    /** Una miniatura de la foto del día: primero la copia guardada; si no hay, la de la galería. */
    fun miniatura(ctx: Context, d: Dia?, px: Int): Bitmap? {
        archivo(ctx, d)?.let { f ->
            runCatching {
                val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(f.path, o)
                var s = 1; while (maxOf(o.outWidth, o.outHeight) / (s * 2) >= px) s *= 2
                BitmapFactory.decodeFile(f.path, BitmapFactory.Options().apply { inSampleSize = s })
            }.getOrNull()?.let { return it }
        }
        val u = d?.foto ?: return null
        return io.github.ruddy2003.vestigium.sensores.Galeria.miniatura(ctx, Uri.parse(u), px)
    }

    /** Si el día tiene foto (la copia o, al menos, el enlace). */
    fun tiene(d: Dia?): Boolean = d?.foto != null || d?.foto_archivo != null
}
