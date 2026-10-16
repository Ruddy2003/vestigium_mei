package io.github.ruddy2003.vestigium.data

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Respaldo en tu Google Drive, en una carpeta «Vestigium Mei». Usa el permiso drive.file:
 * la app solo puede ver los archivos que ella misma creó, nunca el resto de tu Drive.
 * Cada día reemplaza los CSV (para que R los lea siempre frescos) y cada semana guarda una copia .zip fechada.
 */
object Drive {
    private const val ALCANCE = "https://www.googleapis.com/auth/drive.file"
    private const val API = "https://www.googleapis.com/drive/v3/files"
    private const val SUBIR = "https://www.googleapis.com/upload/drive/v3/files"
    private const val CARPETA = "Vestigium Mei"
    private const val COPIAS = 12

    sealed class Acceso {
        data class Listo(val token: String) : Acceso()
        data class Permiso(val intent: PendingIntent) : Acceso()
        data class Error(val mensaje: String) : Acceso()
    }

    private fun solicitud() = AuthorizationRequest.builder().setRequestedScopes(listOf(Scope(ALCANCE))).build()

    suspend fun acceso(ctx: Context): Acceso = try {
        val r = Identity.getAuthorizationClient(ctx).authorize(solicitud()).await()
        val pi = r.pendingIntent
        if (r.hasResolution() && pi != null) Acceso.Permiso(pi)
        else r.accessToken?.let { Acceso.Listo(it) } ?: Acceso.Error("Google no devolvió acceso.")
    } catch (e: ApiException) {
        Acceso.Error(if (e.statusCode == 10) "Falta configurar el cliente OAuth en Google Cloud (paquete y huella SHA-1). Revisa la guía en esta pantalla." else "Google respondió con el código ${e.statusCode}.")
    } catch (e: Exception) { Acceso.Error(e.message ?: "No pude conectar con Google.") }

    /** Lee el resultado de la pantalla de permiso de Google. */
    fun tokenDe(ctx: Context, data: Intent?): String? =
        runCatching { Identity.getAuthorizationClient(ctx).getAuthorizationResultFromIntent(data).accessToken }.getOrNull()

    private fun h(token: String) = mapOf("Authorization" to "Bearer $token")

    private fun buscar(token: String, q: String): List<Pair<String, String>> {
        val url = "$API?q=${Red.codificar(q)}&fields=${Red.codificar("files(id,name)")}&pageSize=200&spaces=drive"
        val o = JSON.parseToJsonElement(Red.get(url, h(token))).jsonObject
        return (o["files"] as? JsonArray)?.mapNotNull { f ->
            val j = f as? JsonObject ?: return@mapNotNull null
            val id = (j["id"] as? JsonPrimitive)?.content ?: return@mapNotNull null
            id to ((j["name"] as? JsonPrimitive)?.content ?: "")
        } ?: emptyList()
    }

    private fun carpeta(token: String): String {
        Repo.config.drive_carpeta?.let { id ->
            val ok = runCatching {
                (JSON.parseToJsonElement(Red.get("$API/$id?fields=id,trashed", h(token))).jsonObject["trashed"] as? JsonPrimitive)?.content == "false"
            }.getOrDefault(false)
            if (ok) return id
        }
        val existente = buscar(token, "name = '$CARPETA' and mimeType = 'application/vnd.google-apps.folder' and trashed = false").firstOrNull()?.first
        val id = existente ?: run {
            val meta = """{"name":"$CARPETA","mimeType":"application/vnd.google-apps.folder"}"""
            val r = Red.enviar("$API?fields=id", "POST", "application/json; charset=UTF-8", meta.toByteArray(), h(token))
            (JSON.parseToJsonElement(r).jsonObject["id"] as JsonPrimitive).content
        }
        Repo.cambiarConfig { it.copy(drive_carpeta = id) }
        return id
    }

    private fun multipart(nombre: String, padre: String?, mime: String, datos: ByteArray): Pair<String, ByteArray> {
        val limite = "vestigium" + System.nanoTime()
        val n = JsonPrimitive(nombre).toString()
        val meta = if (padre != null) """{"name":$n,"parents":["$padre"]}""" else """{"name":$n}"""
        val b = ByteArrayOutputStream()
        b.write("--$limite\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n$meta\r\n--$limite\r\nContent-Type: $mime\r\n\r\n".toByteArray())
        b.write(datos)
        b.write("\r\n--$limite--\r\n".toByteArray())
        return "multipart/related; boundary=$limite" to b.toByteArray()
    }

    private fun mime(n: String) = when { n.endsWith(".csv") -> "text/csv"; n.endsWith(".json") -> "application/json"; n.endsWith(".zip") -> "application/zip"; else -> "text/plain" }

    /** Sube todo. Devuelve un mensaje para mostrar. */
    fun subir(ctx: Context, token: String): String {
        val dir = carpeta(token)
        val existentes = buscar(token, "'$dir' in parents and trashed = false").associate { it.second to it.first }
        val archivos = Exportar.archivos()
        var n = 0
        for ((nombre, cont) in archivos) {
            val datos = cont.toByteArray()
            val id = existentes[nombre]
            if (id != null) Red.enviar("$SUBIR/$id?uploadType=media", "PATCH", mime(nombre), datos, h(token))
            else { val (tipo, cuerpo) = multipart(nombre, dir, mime(nombre), datos); Red.enviar("$SUBIR?uploadType=multipart", "POST", tipo, cuerpo, h(token)) }
            n++
        }
        // Copia semanal fechada (el lunes de la semana) y limpieza de las más viejas.
        val nombreZip = "vestigium_${Fechas.lunesDe(Fechas.hoyCalendario())}.zip"
        if (!existentes.containsKey(nombreZip)) {
            val zip = ByteArrayOutputStream().also { bo -> ZipOutputStream(bo).use { z -> archivos.forEach { (nm, c) -> z.putNextEntry(ZipEntry(nm)); z.write(c.toByteArray()); z.closeEntry() } } }.toByteArray()
            val (tipo, cuerpo) = multipart(nombreZip, dir, "application/zip", zip)
            Red.enviar("$SUBIR?uploadType=multipart", "POST", tipo, cuerpo, h(token))
            val copias = (existentes.keys.filter { it.startsWith("vestigium_") && it.endsWith(".zip") } + nombreZip).sortedDescending()
            copias.drop(COPIAS).forEach { viejo -> existentes[viejo]?.let { runCatching { Red.borrar("$API/$it", h(token)) } } }
        }
        Repo.cambiarConfig { it.copy(drive_ultimo = Fechas.ahoraIso()) }
        return "Subí $n archivos a tu Drive, carpeta «$CARPETA»."
    }

    /** Respaldo de fondo: solo funciona si ya diste permiso antes; si Google pide confirmar de nuevo, avisa. */
    suspend fun automatico(ctx: Context): Boolean {
        if (!Repo.config.drive) return false
        return when (val a = acceso(ctx)) {
            is Acceso.Listo -> runCatching { subir(ctx, a.token); true }.getOrDefault(false)
            else -> false
        }
    }
}
