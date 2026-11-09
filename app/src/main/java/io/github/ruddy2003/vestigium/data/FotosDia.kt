package io.github.ruddy2003.vestigium.data

import android.content.Context
import java.io.File

/**
 * La foto del día, guardada dentro de la app (esquema 22). Antes solo se guardaba el enlace a la galería: en un teléfono
 * nuevo, o si la foto se borraba de la galería, se perdía. Ahora se copia una versión reducida aquí y viaja en el respaldo.
 */
object FotosDia {
    fun carpeta(ctx: Context): File = File(ctx.filesDir, "fotos_dia").apply { mkdirs() }
}
