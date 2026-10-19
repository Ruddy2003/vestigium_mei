package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.util.Base64
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import kotlin.math.abs

/**
 * Conexión directa con tu cuenta de Spotify (Web API, «recently-played»).
 *
 * - Autorización con PKCE: no hace falta secreto de cliente, solo el Client ID de una app que creas en developer.spotify.com.
 * - Spotify ya no acepta redirecciones a esquemas propios, así que la app abre por un momento un receptor
 *   en 127.0.0.1 (solo accesible desde el mismo teléfono) para recibir el código.
 * - Los tokens viven en preferencias privadas de la app; nunca entran al respaldo ni a los CSV.
 * - Spotify solo da las últimas 50 canciones, así que se consulta cada ~30 min desde el Pulso.
 */
object Spotify {
    const val PUERTO = 43117
    const val REDIRECCION = "http://127.0.0.1:$PUERTO/callback"
    private const val ALCANCES = "user-read-recently-played"
    private val json = Json { ignoreUnknownKeys = true }

    /** Mensaje para la tarjeta de Datos (se actualiza aunque la pantalla esté cerrada). */
    val mensaje = mutableStateOf<String?>(null)
    val esperando = mutableStateOf(false)
    /** Cambia cada vez que se conecta, sincroniza o desconecta, para refrescar la tarjeta. */
    val version = mutableStateOf(0)

    private fun prefs(ctx: Context) = ctx.getSharedPreferences("spotify", Context.MODE_PRIVATE)

    fun clientId(ctx: Context): String = prefs(ctx).getString("client_id", "") ?: ""
    fun conectado(ctx: Context): Boolean = prefs(ctx).getString("refresh", null) != null
    fun ultimo(ctx: Context): Long = prefs(ctx).getLong("ultimo", 0L)
    fun error(ctx: Context): String? = prefs(ctx).getString("error", null)

    private fun aleatorio(n: Int): String {
        val abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        val r = SecureRandom()
        return String(CharArray(n) { abc[r.nextInt(abc.length)] })
    }

    /** Prepara la autorización (PKCE) y devuelve la dirección que hay que abrir en el navegador. */
    fun urlAutorizacion(ctx: Context, clientId: String): String {
        val verificador = aleatorio(64)
        val reto = Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(verificador.toByteArray(Charsets.US_ASCII)), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val estado = aleatorio(16)
        prefs(ctx).edit().putString("client_id", clientId.trim()).putString("verificador", verificador).putString("estado", estado).apply()
        return "https://accounts.spotify.com/authorize?client_id=${Red.codificar(clientId.trim())}&response_type=code" +
            "&redirect_uri=${Red.codificar(REDIRECCION)}&code_challenge_method=S256&code_challenge=$reto" +
            "&scope=${Red.codificar(ALCANCES)}&state=$estado"
    }

    /**
     * Espera hasta 5 minutos a que el navegador vuelva a 127.0.0.1 con el código.
     * Devuelve la ruta pedida (/callback?code=…) o null si no llegó.
     */
    suspend fun esperarRedireccion(): String? = withContext(Dispatchers.IO) {
        runCatching { servidor?.close() }
        val srv = ServerSocket()
        servidor = srv
        try {
            srv.reuseAddress = true
            srv.bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), PUERTO))
            srv.soTimeout = 300_000
            var ruta: String? = null
            while (ruta == null) {
                val s = srv.accept()
                try { ruta = atender(s) } finally { runCatching { s.close() } }
            }
            ruta
        } catch (e: Exception) {
            null
        } finally {
            runCatching { srv.close() }
        }
    }

    @Volatile private var servidor: ServerSocket? = null
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Abre el receptor y devuelve la dirección para el navegador. La espera vive fuera de la pantalla,
     * así sigue funcionando mientras estás en el navegador.
     */
    fun iniciar(ctx: Context, clientId: String): String {
        val app = ctx.applicationContext
        val url = urlAutorizacion(app, clientId)
        esperando.value = true
        mensaje.value = "Acepta en el navegador y luego vuelve aquí."
        alcance.launch {
            val ruta = esperarRedireccion()
            if (ruta != null) mensaje.value = canjear(app, ruta)
            else if (esperando.value) mensaje.value = "No llegó la respuesta del navegador. Si te mostró una página de error, copia su dirección y pégala abajo."
            esperando.value = false
        }
        return url
    }

    /** Para la dirección pegada a mano: cierra el receptor y canjea. */
    fun canjearPegada(ctx: Context, direccion: String): String {
        esperando.value = false
        runCatching { servidor?.close() }
        return canjear(ctx.applicationContext, direccion)
    }

    /** Lee una petición del navegador. Si es la de regreso, contesta con una página breve y devuelve la ruta. */
    private fun atender(s: Socket): String? {
        s.soTimeout = 10_000
        val lector = s.getInputStream().bufferedReader()
        val primera = lector.readLine() ?: return null
        while (true) { val l = lector.readLine() ?: break; if (l.isEmpty()) break }
        val ruta = primera.split(" ").getOrNull(1) ?: ""
        val esRegreso = ruta.startsWith("/callback")
        val html = if (esRegreso) """
            <!doctype html><html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
            <title>Vestigium Mei</title></head>
            <body style="background:#17191c;color:#f2efe8;font-family:serif;padding:32px;text-align:center">
            <h2 style="letter-spacing:.2em">VESTIGIVM MEI</h2>
            <p>${if ("error=" in ruta) "Spotify no dio el permiso." else "Listo. Ya puedes volver a la app."}</p>
            <p><a style="color:#d2ae5a" href="intent:#Intent;action=android.intent.action.MAIN;category=android.intent.category.LAUNCHER;package=io.github.ruddy2003.vestigium;end">Volver a Vestigium Mei</a></p>
            </body></html>
        """.trimIndent() else ""
        val cuerpo = html.toByteArray(Charsets.UTF_8)
        val cabecera = (if (esRegreso) "HTTP/1.1 200 OK\r\n" else "HTTP/1.1 404 Not Found\r\n") +
            "Content-Type: text/html; charset=utf-8\r\nContent-Length: ${cuerpo.size}\r\nConnection: close\r\nCache-Control: no-store\r\n\r\n"
        s.getOutputStream().apply { write(cabecera.toByteArray(Charsets.US_ASCII)); write(cuerpo); flush() }
        return if (esRegreso) ruta else null
    }

    private fun parametros(ruta: String): Map<String, String> =
        ruta.substringAfter('?', "").split('&').filter { '=' in it }.associate {
            URLDecoder.decode(it.substringBefore('='), "UTF-8") to URLDecoder.decode(it.substringAfter('='), "UTF-8")
        }

    /** Recibe la ruta del regreso (o la dirección completa pegada a mano), canjea el código y hace la primera lectura. */
    fun canjear(ctx: Context, rutaODireccion: String): String {
        val p = parametros(rutaODireccion.trim())
        p["error"]?.let { return "Spotify respondió «$it». Vuelve a intentarlo." }
        val codigo = p["code"] ?: return "No encontré el código en esa dirección. Copia la dirección completa que empieza con http://127.0.0.1…"
        val pr = prefs(ctx)
        if (p["state"] != null && p["state"] != pr.getString("estado", null)) return "Esa dirección es de un intento anterior. Toca «conectar» otra vez."
        val cuerpo = "grant_type=authorization_code&code=${Red.codificar(codigo)}&redirect_uri=${Red.codificar(REDIRECCION)}" +
            "&client_id=${Red.codificar(clientId(ctx))}&code_verifier=${Red.codificar(pr.getString("verificador", "") ?: "")}"
        val r = try {
            json.parseToJsonElement(Red.enviar("https://accounts.spotify.com/api/token", "POST", "application/x-www-form-urlencoded", cuerpo.toByteArray())).jsonObject
        } catch (e: IOException) {
            return "Spotify no aceptó el código: ${e.message?.take(160)}"
        }
        guardarTokens(ctx, r)
        pr.edit().remove("verificador").remove("estado").remove("error").apply()
        version.value++
        val n = runCatching { sincronizar(ctx) }.getOrElse { return "Conectado, pero la primera lectura falló: ${it.message?.take(160)}" }
        return "Conectado con Spotify. Guardé $n canciones recientes; de aquí en adelante se leen solas cada media hora."
    }

    private fun guardarTokens(ctx: Context, r: JsonObject) {
        val e = prefs(ctx).edit()
        r["access_token"]?.jsonPrimitive?.contentOrNull?.let { e.putString("acceso", it) }
        r["refresh_token"]?.jsonPrimitive?.contentOrNull?.let { e.putString("refresh", it) }
        val seg = r["expires_in"]?.jsonPrimitive?.longOrNull ?: 3600
        e.putLong("expira", System.currentTimeMillis() + (seg - 60) * 1000)
        e.apply()
    }

    /** Token vigente; lo renueva si caducó. Null si ya no hay permiso. */
    private fun token(ctx: Context): String? {
        val pr = prefs(ctx)
        val acceso = pr.getString("acceso", null)
        if (acceso != null && System.currentTimeMillis() < pr.getLong("expira", 0)) return acceso
        val refresh = pr.getString("refresh", null) ?: return null
        val cuerpo = "grant_type=refresh_token&refresh_token=${Red.codificar(refresh)}&client_id=${Red.codificar(clientId(ctx))}"
        return try {
            val r = json.parseToJsonElement(Red.enviar("https://accounts.spotify.com/api/token", "POST", "application/x-www-form-urlencoded", cuerpo.toByteArray())).jsonObject
            guardarTokens(ctx, r)
            pr.getString("acceso", null)
        } catch (e: IOException) {
            // invalid_grant: el permiso se revocó o la app de Spotify se borró. Hay que volver a conectar.
            if (e.message?.contains("invalid_grant") == true || e.message?.contains("invalid_client") == true) {
                pr.edit().remove("acceso").remove("refresh").putString("error", "Spotify retiró el permiso; vuelve a conectar.").apply()
                version.value++
            }
            null
        }
    }

    /**
     * Lee las últimas 50 canciones y guarda las nuevas. Spotify marca la hora en que terminó cada una,
     * así que el inicio se calcula restando la duración.
     * Lo que ya se había anotado desde la notificación de Spotify para esa misma canción se reemplaza (aquí viene completo);
     * lo que ya venía en el historial importado no se duplica.
     */
    fun sincronizar(ctx: Context): Int {
        val t = token(ctx) ?: throw IOException("Sin permiso de Spotify.")
        val r = try {
            json.parseToJsonElement(Red.get("https://api.spotify.com/v1/me/player/recently-played?limit=50", mapOf("Authorization" to "Bearer $t"))).jsonObject
        } catch (e: IOException) {
            val m = e.message ?: ""
            prefs(ctx).edit().putString("error", when {
                "403" in m -> "Spotify negó el acceso (403). Revisa que tu cuenta esté en «User Management» de la app y que el dueño tenga Premium."
                "401" in m -> "El permiso caducó; vuelve a conectar."
                else -> "No pude leer Spotify: ${m.take(120)}"
            }).apply()
            version.value++
            throw e
        }
        val nuevas = ArrayList<Escucha>()
        for (el in r["items"]?.jsonArray ?: emptyList()) {
            val item = el.jsonObject
            val tr = item["track"]?.jsonObject ?: continue
            val fin = runCatching { Instant.parse(item["played_at"]!!.jsonPrimitive.content).toEpochMilli() }.getOrNull() ?: continue
            val dur = tr["duration_ms"]?.jsonPrimitive?.intOrNull
            val ini = fin - (dur ?: 0)
            val artistas = tr["artists"]?.jsonArray?.mapNotNull { a -> a.jsonObject["name"]?.jsonPrimitive?.contentOrNull }?.joinToString(", ") ?: ""
            nuevas.add(Escucha(
                id = "spa_$fin", ts = Fechas.isoDeMillis(ini), fecha = Fechas.diaLogicoDeMillis(ini),
                titulo = (tr["name"]?.jsonPrimitive?.contentOrNull ?: continue).take(200), artista = artistas.take(200),
                album = (tr["album"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull ?: "").take(200),
                app = "Spotify", duracion_s = dur?.let { d -> d / 1000 }, fuente = "spotify_api",
                contexto = (item["context"] as? JsonObject)?.get("type")?.jsonPrimitive?.contentOrNull ?: "",
            ))
        }
        var guardadas = 0
        if (nuevas.isNotEmpty()) {
            val desde = Fechas.sumar(nuevas.minOf { it.fecha }, -1); val hasta = Fechas.sumar(nuevas.maxOf { it.fecha }, 1)
            val existentes = Repo.escuchas(desde, hasta, todas = true)
            val ids = existentes.map { it.id }.toHashSet()
            fun ms(e: Escucha) = runCatching { java.time.OffsetDateTime.parse(e.ts).toInstant().toEpochMilli() }.getOrDefault(0L)
            fun misma(a: Escucha, b: Escucha, margen: Long) = a.titulo.equals(b.titulo, true) && abs(ms(a) - ms(b)) <= margen
            val importadas = existentes.filter { it.fuente == "spotify" }
            val avisos = existentes.filter { it.fuente == "notificacion" && it.app.contains("Spotify", true) }
            val aGuardar = nuevas.filter { n -> n.id !in ids && importadas.none { misma(it, n, 180_000) } }
            val reemplazadas = avisos.filter { a -> aGuardar.any { n -> misma(a, n, 600_000 + (n.duracion_s ?: 0) * 1000L) } }.map { it.id }
            if (reemplazadas.isNotEmpty()) Repo.borrarEscuchas(reemplazadas)
            if (aGuardar.isNotEmpty()) Repo.importarEscuchas(aGuardar)
            guardadas = aGuardar.size
        }
        prefs(ctx).edit().putLong("ultimo", System.currentTimeMillis()).remove("error").apply()
        version.value++
        return guardadas
    }

    /** Desde el Pulso: como mucho cada 25 minutos. */
    fun quizaSincronizar(ctx: Context) {
        if (!conectado(ctx)) return
        if (System.currentTimeMillis() - ultimo(ctx) < 25 * 60_000L) return
        runCatching { sincronizar(ctx) }
    }

    fun desconectar(ctx: Context) {
        prefs(ctx).edit().remove("acceso").remove("refresh").remove("expira").remove("error").apply()
        version.value++
    }

    fun contar(): Long = runCatching { Repo.db().contarPrefijo("escuchas", "spa_") }.getOrDefault(0L)
}
