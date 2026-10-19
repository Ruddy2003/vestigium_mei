package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Lo que eliges para importar puede llegar de tres formas: un .zip (o varios), archivos sueltos (varios a la vez)
 * o una carpeta entera (la de Takeout ya descomprimida). Aquí se recorren igual: cada archivo con su ruta relativa.
 */
object Archivos {
    /** Un archivo elegido: su ruta relativa (carpeta/nombre) y de dónde se lee. */
    data class Fuente(val ruta: String, val uri: Uri, val tam: Long = -1)

    fun nombre(ctx: Context, uri: Uri): String = runCatching {
        ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
    }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/') ?: "archivo"

    /** Archivos sueltos o .zip elegidos con el selector (varios a la vez). */
    fun deDocumentos(ctx: Context, uris: List<Uri>): List<Fuente> = uris.map { Fuente(nombre(ctx, it), it) }

    /**
     * Todos los archivos de una carpeta y sus subcarpetas (rápido aunque sean miles: una consulta por carpeta).
     * [filtro] recibe el nombre para no listar lo que no sirve.
     */
    fun deCarpeta(ctx: Context, arbol: Uri, filtro: (String) -> Boolean = { true }): List<Fuente> {
        val out = ArrayList<Fuente>()
        val raiz = DocumentsContract.getTreeDocumentId(arbol)
        val pila = ArrayDeque<Pair<String, String>>().apply { add(raiz to "") }
        val cols = arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID, DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE, DocumentsContract.Document.COLUMN_SIZE)
        while (pila.isNotEmpty()) {
            val (id, ruta) = pila.removeLast()
            val hijos = DocumentsContract.buildChildDocumentsUriUsingTree(arbol, id)
            runCatching {
                ctx.contentResolver.query(hijos, cols, null, null, null)?.use { c ->
                    while (c.moveToNext()) {
                        val hid = c.getString(0); val n = c.getString(1) ?: continue; val mime = c.getString(2)
                        val r = if (ruta.isEmpty()) n else "$ruta/$n"
                        if (mime == DocumentsContract.Document.MIME_TYPE_DIR) pila.add(hid to r)
                        else if (filtro(n) || n.endsWith(".zip", true)) out.add(Fuente(r, DocumentsContract.buildDocumentUriUsingTree(arbol, hid), if (c.isNull(3)) -1 else c.getLong(3)))
                    }
                }
            }
        }
        return out
    }

    /**
     * Recorre cada archivo; los .zip se abren y se entrega cada archivo de adentro (con su ruta dentro del zip).
     * El flujo que recibe [uso] no se debe cerrar.
     */
    fun recorrer(ctx: Context, fuentes: List<Fuente>, filtro: (String) -> Boolean, avance: (Int) -> Unit = {}, uso: (ruta: String, tam: Long, ins: InputStream) -> Unit) {
        for ((k, f) in fuentes.withIndex()) {
            runCatching {
                ctx.contentResolver.openInputStream(f.uri)?.use { s ->
                    if (f.ruta.endsWith(".zip", true)) {
                        val z = ZipInputStream(s.buffered(1 shl 16))
                        while (true) {
                            val e = z.nextEntry ?: break
                            if (e.isDirectory || !filtro(e.name.substringAfterLast('/'))) continue
                            runCatching { uso(e.name, e.size, object : InputStream() {
                                override fun read(): Int = z.read()
                                override fun read(b: ByteArray, off: Int, len: Int): Int = z.read(b, off, len)
                                override fun close() {}
                            }) }
                        }
                    } else if (filtro(f.ruta.substringAfterLast('/'))) uso(f.ruta, f.tam, s)
                }
            }
            avance(k + 1)
        }
    }
}
