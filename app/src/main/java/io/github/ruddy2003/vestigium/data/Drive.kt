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
 * Cada día reemplaza los CSV (para que R los lea siempre frescos); guarda una copia fechada por semana (las últimas 8)
 * y una por mes (las últimas 12).
 */
object Drive {
    private const val ALCANCE = "https://www.googleapis.com/auth/drive.file"
    private const val API = "https://www.googleapis.com/drive/v3/files"
    private const val SUBIR = "https://www.googleapis.com/upload/drive/v3/files"
    private const val CARPETA = "Vestigium Mei"
    /** Copias semanales (los últimos 8 lunes) y mensuales (los últimos 12 meses) que se guardan en Drive. */
    private const val COPIAS = 8
    private const val COPIAS_MES = 12
    /** Nombre (sin extensión) del respaldo que se guarda aparte cuando viene mucho más pobre que el último. */
    private const val REVISAR = "vestigium_revisar"
    const val EMPOBRECIDO = "Este respaldo trae muchos menos días o momentos que el último que subiste, así que no reemplacé tu copia de Drive: lo guardé aparte como «vestigium_revisar». Si borraste a propósito, toca «conectar y subir ahora» en Más → Datos."

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

    private fun subirArchivo(token: String, dir: String, existentes: Map<String, String>, nombre: String, tipo: String, datos: ByteArray) {
        val id = existentes[nombre]
        if (id != null) Red.enviar("$SUBIR/$id?uploadType=media", "PATCH", tipo, datos, h(token), 180_000)
        else { val (t, cuerpo) = multipart(nombre, dir, tipo, datos); Red.enviar("$SUBIR?uploadType=multipart", "POST", t, cuerpo, h(token), 180_000) }
    }

    /**
     * Sube todo. Sin contraseña: los CSV para R, el respaldo completo del día (vestigium_respaldo.zip) y una copia semanal fechada.
     * Con contraseña: solo el respaldo cifrado (.vmc) y su copia semanal; los archivos sueltos de antes se borran de Drive.
     */
    fun subir(ctx: Context, token: String, forzar: Boolean = false): String {
        if (Respaldos.bloqueado(ctx)) return Respaldos.PAUSA
        val dir = carpeta(token)
        val existentes = buscar(token, "'$dir' in parents and trashed = false").associate { it.second to it.first }
        val cifra = Respaldos.cifradoActivo(ctx)
        val ext = if (cifra) ".vmc" else ".zip"
        val conteo = Respaldos.conteo()
        // Mucho más pobre que el último: se guarda aparte y no se tocan la copia del día, la semanal ni la mensual.
        if (!forzar && Respaldos.empobrecido(Repo.config.drive_conteo, conteo)) {
            val a = Respaldos.archivo(ctx)
            val b = a.readBytes(); a.delete()
            subirArchivo(token, dir, existentes, REVISAR + ext, Respaldos.mime(ext), b)
            Repo.cambiarConfig { it.copy(drive_ultimo = Fechas.ahoraIso()) }
            return EMPOBRECIDO
        }
        var n = 0
        if (!cifra) for (a in Exportar.archivos(ctx)) { subirArchivo(token, dir, existentes, a.nombre, mime(a.nombre), a.bytes()); n++ }
        val diario = Respaldos.archivo(ctx)
        val bytes = diario.readBytes(); diario.delete()
        subirArchivo(token, dir, existentes, Respaldos.BASE + ext, Respaldos.mime(ext), bytes); n++
        // Los archivos en claro de antes se borran solo cuando ya subió el cifrado (si falla la subida, quedan).
        if (cifra) existentes.filterKeys { Respaldos.esArchivoPlano(it) }.values.forEach { runCatching { Red.borrar("$API/$it", h(token)) } }
        existentes[REVISAR + ext]?.let { runCatching { Red.borrar("$API/$it", h(token)) } }
        // Copia semanal fechada (el lunes de la semana) y limpieza de las más viejas.
        val nombreSem = "vestigium_${Fechas.lunesDe(Fechas.hoyCalendario())}$ext"
        if (!existentes.containsKey(nombreSem)) {
            val (tipo, cuerpo) = multipart(nombreSem, dir, Respaldos.mime(ext), bytes)
            Red.enviar("$SUBIR?uploadType=multipart", "POST", tipo, cuerpo, h(token), 180_000); n++
            val patron = Regex("vestigium_\\d{4}-\\d{2}-\\d{2}\\$ext")
            val copias = (existentes.keys.filter { patron.matches(it) } + nombreSem).distinct().sortedDescending()
            copias.drop(COPIAS).forEach { viejo -> existentes[viejo]?.let { runCatching { Red.borrar("$API/$it", h(token)) } } }
        }
        // Copia mensual (una por mes) y limpieza de las de hace más de un año.
        val nombreMes = "vestigium_mes_${Fechas.hoyCalendario().take(7)}$ext"
        if (!existentes.containsKey(nombreMes)) {
            val (tipo, cuerpo) = multipart(nombreMes, dir, Respaldos.mime(ext), bytes)
            Red.enviar("$SUBIR?uploadType=multipart", "POST", tipo, cuerpo, h(token), 180_000); n++
            val patron = Regex("vestigium_mes_\\d{4}-\\d{2}\\$ext")
            (existentes.keys.filter { patron.matches(it) } + nombreMes).distinct().sortedDescending().drop(COPIAS_MES)
                .forEach { viejo -> existentes[viejo]?.let { runCatching { Red.borrar("$API/$it", h(token)) } } }
        }
        // El respaldo del otro modo (si cambiaste la contraseña) ya no sirve.
        existentes[Respaldos.BASE + if (cifra) ".zip" else ".vmc"]?.let { runCatching { Red.borrar("$API/$it", h(token)) } }
        Repo.cambiarConfig { it.copy(drive_ultimo = Fechas.ahoraIso(), drive_conteo = conteo) }
        return if (cifra) "Subí tu respaldo cifrado a Drive, carpeta «$CARPETA»." else "Subí $n archivos a tu Drive, carpeta «$CARPETA»."
    }

    /** La copia más reciente que esta app dejó en tu Drive (el respaldo del día o la semanal), con su fecha. */
    fun ultimaCopia(token: String): Triple<String, String, String>? {
        val q = "trashed = false and name contains 'vestigium_' and (name contains '.zip' or name contains '.vmc')"
        val url = "$API?q=${Red.codificar(q)}&fields=${Red.codificar("files(id,name,modifiedTime)")}&orderBy=modifiedTime%20desc&pageSize=50&spaces=drive"
        val o = JSON.parseToJsonElement(Red.get(url, h(token))).jsonObject
        return (o["files"] as? JsonArray)?.mapNotNull { f ->
            val j = f as? JsonObject ?: return@mapNotNull null
            val id = (j["id"] as? JsonPrimitive)?.content ?: return@mapNotNull null
            Triple(id, (j["name"] as? JsonPrimitive)?.content ?: "", (j["modifiedTime"] as? JsonPrimitive)?.content ?: "")
        }?.filter { it.second.startsWith("vestigium_") && it.second != "vestigium_resumido.zip" && !it.second.startsWith(REVISAR) }?.maxByOrNull { it.third }
    }

    fun bajar(token: String, id: String): ByteArray = Red.bajar("$API/$id?alt=media", h(token))

    /** Trae la copia más reciente de Drive y la restaura. [contrasena] para un respaldo cifrado de otro teléfono. */
    fun restaurar(ctx: Context, token: String, contrasena: CharArray?): Respaldos.Lectura {
        val (id, nombre, cuando) = ultimaCopia(token) ?: return Respaldos.Lectura.Error("No encontré respaldos de Vestigium Mei en tu Drive.")
        val datos = bajar(token, id)
        return when (val r = Respaldos.restaurar(ctx, datos, contrasena)) {
            is Respaldos.Lectura.Listo -> Respaldos.Lectura.Listo("Restauré «$nombre» (${cuando.take(10)}). " + r.mensaje)
            else -> r
        }
    }

    /** Baja la copia más reciente de Drive y dice qué trae, sin restaurar nada. */
    fun verificar(ctx: Context, token: String, contrasena: CharArray?): Respaldos.Lectura {
        val (id, nombre, cuando) = ultimaCopia(token) ?: return Respaldos.Lectura.Error("No encontré respaldos de Vestigium Mei en tu Drive.")
        return when (val r = Respaldos.verificar(ctx, bajar(token, id), contrasena)) {
            is Respaldos.Lectura.Listo -> Respaldos.Lectura.Listo("«$nombre» (subido el ${cuando.take(10)}). " + r.mensaje)
            else -> r
        }
    }

    /** Respaldo de fondo: solo funciona si ya diste permiso antes; si Google pide confirmar de nuevo, avisa. */
    suspend fun automatico(ctx: Context): Boolean {
        if (!Repo.config.drive) return false
        // En pausa por falta de contraseña no es un problema de permiso: eso lo avisa Trabajo con su propio aviso.
        if (Respaldos.bloqueado(ctx)) return true
        return when (val a = acceso(ctx)) {
            is Acceso.Listo -> runCatching {
                if (subir(ctx, a.token) == EMPOBRECIDO) io.github.ruddy2003.vestigium.avisos.Avisos.driveEmpobrecido(ctx)
                true
            }.getOrDefault(false)
            else -> false
        }
    }
}
