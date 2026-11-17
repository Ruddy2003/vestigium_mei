package io.github.ruddy2003.vestigium.data

import android.content.Context
import java.io.File

/**
 * Dónde viven las imágenes de los cuadros guardados (esquema 27): `files/cuadros/<id>.jpg`, dentro de la app. Viajan en el
 * respaldo completo (`cuadros/` del .zip) y se restauran con él, como las fotos del día; no van en la exportación para R ni
 * en la cápsula del año (son imágenes de la app, no datos tuyos). Los datos de cada cuadro están en `Config.cuadros_guardados`
 * ([CuadrosGuardados]).
 */
object CarpetaCuadros {
    fun de(ctx: Context): File = File(ctx.filesDir, CuadrosGuardados.CARPETA).apply { mkdirs() }

    /** La imagen de [g], si existe y no está vacía. */
    fun archivo(ctx: Context, g: CuadroGuardado): File? = File(de(ctx), g.archivo).takeIf { it.exists() && it.length() > 0 }

    /**
     * Escribe la imagen [id] con [escribir] (que recibe el archivo a llenar) sin dejar nunca una a medias: primero en
     * «<nombre>.nuevo» y al final la renombra. Devuelve el nombre, o null si no se pudo.
     */
    fun escribir(ctx: Context, id: String, escribir: (File) -> Unit): String? = runCatching {
        require(CuadrosGuardados.idValido(id))
        val n = CuadrosGuardados.nombre(id)
        val tmp = File(de(ctx), "$n.nuevo")
        try { escribir(tmp) } catch (e: Throwable) { tmp.delete(); throw e }
        check(tmp.length() > 0) { "imagen vacía" }
        val f = File(de(ctx), n)
        if (!tmp.renameTo(f)) { f.delete(); check(tmp.renameTo(f)) { "no se pudo renombrar" } }
        n
    }.getOrNull()

    /** Borra la imagen de [g]. */
    fun borrar(ctx: Context, g: CuadroGuardado) { File(de(ctx), g.archivo).delete() }

    /**
     * Borra las imágenes que ningún cuadro guardado usa (un guardado a medias, uno quitado). Devuelve cuántas. No toca las
     * de la última hora: una imagen recién escrita todavía puede estar esperando a que se guarden sus datos.
     */
    fun limpiar(ctx: Context, c: Config, ahora: Long = System.currentTimeMillis()): Int {
        val viejos = de(ctx).listFiles()?.filter { it.isFile && ahora - it.lastModified() > 3_600_000L }?.map { it.name } ?: return 0
        return CuadrosGuardados.sobrantes(c, viejos).count { File(de(ctx), it).delete() }
    }
}
