package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.JsonReader
import android.util.JsonToken
import java.io.InputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.zip.ZipInputStream

/**
 * Importaciones de otros servicios: Spotify (historial), Kindle (My Clippings), Google Takeout (YouTube y búsquedas)
 * y WhatsApp (solo metadatos de los chats exportados: nunca se guarda el texto de nadie).
 */
object Importaciones {
    private fun hash(s: String): String = MessageDigest.getInstance("SHA-1").digest(s.toByteArray()).take(8).joinToString("") { "%02x".format(it) }

    fun nombre(ctx: Context, uri: Uri): String = runCatching {
        ctx.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c -> if (c.moveToFirst()) c.getString(0) else null }
    }.getOrNull() ?: uri.lastPathSegment ?: "archivo"

    /** Abre un archivo; si es .zip, entrega cada archivo de adentro que cumpla el filtro. */
    private fun abrir(ctx: Context, uri: Uri, filtro: (String) -> Boolean, uso: (String, InputStream) -> Unit) {
        val n = nombre(ctx, uri)
        val ins = ctx.contentResolver.openInputStream(uri) ?: return
        ins.use { s ->
            if (n.endsWith(".zip", true)) {
                val z = ZipInputStream(s.buffered())
                while (true) {
                    val e = z.nextEntry ?: break
                    if (!e.isDirectory && filtro(e.name)) uso(e.name.substringAfterLast('/'), object : InputStream() {
                        override fun read(): Int = z.read()
                        override fun read(b: ByteArray, off: Int, len: Int): Int = z.read(b, off, len)
                        override fun close() {}
                    })
                }
            } else uso(n, s)
        }
    }

    private fun JsonReader.texto(): String? = if (peek() == JsonToken.NULL) { nextNull(); null } else nextString()
    private fun JsonReader.numero(): Long? = if (peek() == JsonToken.NULL) { nextNull(); null } else nextLong()
    private fun JsonReader.logico(): Boolean? = when (peek()) { JsonToken.NULL -> { nextNull(); null }; JsonToken.BOOLEAN -> nextBoolean(); else -> { skipValue(); null } }

    // ---------------- Spotify ----------------
    /**
     * Acepta el «historial de reproducción ampliado» (Streaming_History_Audio_*.json) y el básico (StreamingHistory_music_*.json),
     * sueltos o dentro del .zip que manda Spotify. Las horas de Spotify vienen en UTC y se pasan a tu hora local.
     */
    fun spotify(ctx: Context, uris: List<Uri>): String {
        val lista = ArrayList<Escucha>()
        var archivos = 0
        for (u in uris) runCatching {
            abrir(ctx, u, { it.endsWith(".json", true) && (it.contains("Streaming_History", true) || it.contains("StreamingHistory", true)) || !it.contains('/') && it.endsWith(".json", true) }) { _, s ->
                archivos++
                val r = JsonReader(InputStreamReader(s, Charsets.UTF_8))
                if (r.peek() != JsonToken.BEGIN_ARRAY) { r.close(); return@abrir }
                r.beginArray()
                while (r.hasNext()) {
                    var ts: String? = null; var fin: String? = null; var ms: Long? = null
                    var cancion: String? = null; var artista: String? = null; var album: String? = null
                    var episodio: String? = null; var programa: String? = null; var saltada: Boolean? = null
                    r.beginObject()
                    while (r.hasNext()) when (r.nextName()) {
                        "ts" -> ts = r.texto()
                        "endTime" -> fin = r.texto()
                        "ms_played", "msPlayed" -> ms = r.numero()
                        "master_metadata_track_name", "trackName" -> cancion = r.texto()
                        "master_metadata_album_artist_name", "artistName" -> artista = r.texto()
                        "master_metadata_album_album_name" -> album = r.texto()
                        "episode_name" -> episodio = r.texto()
                        "episode_show_name" -> programa = r.texto()
                        "skipped" -> saltada = r.logico()
                        else -> r.skipValue()
                    }
                    r.endObject()
                    val titulo = cancion ?: episodio ?: continue
                    // El ampliado marca cuándo terminó; el básico, «endTime» sin zona (UTC). Se calcula el inicio restando lo escuchado.
                    val finMs = ts?.let { runCatching { Instant.parse(it).toEpochMilli() }.getOrNull() }
                        ?: fin?.let { runCatching { LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).toInstant(ZoneOffset.UTC).toEpochMilli() }.getOrNull() }
                        ?: continue
                    val ini = finMs - (ms ?: 0L)
                    lista.add(Escucha(
                        id = "sp_" + hash("$finMs|$titulo|${artista ?: programa}"), ts = Fechas.isoDeMillis(ini), fecha = Fechas.diaLogicoDeMillis(ini),
                        titulo = titulo.take(200), artista = (artista ?: programa ?: "").take(200), album = if (cancion == null) "podcast" else (album ?: "").take(200),
                        app = "Spotify", ms = ms?.toInt(), saltada = saltada, fuente = "spotify",
                    ))
                }
                r.endArray()
            }
        }
        if (lista.isEmpty()) return "No encontré reproducciones. Elige los archivos Streaming_History…json (o el .zip completo que te mandó Spotify)."
        // Lo que se anotó desde las notificaciones de Spotify en esos días queda reemplazado por el historial, que es más preciso.
        val desde = lista.minOf { it.fecha }; val hasta = lista.maxOf { it.fecha }
        // También lo leído por la conexión directa: el historial trae además cuánto escuchaste y si la saltaste.
        val viejas = Repo.escuchas(desde, hasta, todas = true).filter { (it.fuente == "notificacion" && it.app.contains("Spotify", true)) || it.fuente == "spotify_api" }.map { it.id }
        Repo.borrarEscuchas(viejas)
        Repo.importarEscuchas(lista)
        val reales = lista.count { (it.ms ?: 0) >= 30_000 }
        return "Importé ${lista.size} reproducciones de $archivos archivo(s), del $desde al $hasta ($reales de más de 30 segundos)."
    }

    // ---------------- Kindle ----------------
    private val SEPARADOR = Regex("^=+\\s*$")
    private val RX_POS = Regex("(?:location|loc\\.|posición|posicion|ubicación|ubicacion|Location|Posición)\\s+([\\d]+(?:-[\\d]+)?)", RegexOption.IGNORE_CASE)
    private val RX_PAG = Regex("(?:page|página|pagina)\\s+(\\d+)", RegexOption.IGNORE_CASE)
    private val FECHAS_KINDLE = listOf(
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy h:mm:ss a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy HH:mm:ss", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy H:mm:ss", Locale.forLanguageTag("es")),
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy H:mm:ss", Locale.forLanguageTag("es")),
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy, H:mm:ss", Locale.forLanguageTag("es")),
    )

    private fun fechaKindle(meta: String): Long? {
        val t = meta.substringAfter("Added on ", "").ifEmpty { meta.substringAfter("Añadido el ", "") }.ifEmpty { meta.substringAfter("Agregado el ", "") }
            .ifEmpty { meta.substringAfterLast("|").trim() }.trim()
        for (f in FECHAS_KINDLE) {
            val r = runCatching { LocalDateTime.parse(t, f) }.getOrNull()
                ?: runCatching { LocalDateTime.parse(t.replaceFirstChar { it.uppercase() }, f) }.getOrNull()
            if (r != null) return r.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
        return null
    }

    fun kindle(ctx: Context, uri: Uri): String {
        val texto = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: return "No pude leer el archivo."
        val lista = ArrayList<Subrayado>()
        val bloque = ArrayList<String>()
        fun cerrar() {
            val l = bloque.map { it.trimEnd('\r') }.dropWhile { it.isBlank() }
            bloque.clear()
            if (l.size < 2) return
            val cabeza = l[0].removePrefix("﻿").trim()
            val autor = Regex("\\(([^()]*)\\)\\s*$").find(cabeza)?.groupValues?.get(1)?.trim() ?: ""
            val libro = if (autor.isNotEmpty()) cabeza.substring(0, cabeza.lastIndexOf('(')).trim() else cabeza
            val meta = l[1]
            val tipo = when {
                meta.contains("Highlight", true) || meta.contains("subrayado", true) || meta.contains("resaltado", true) -> "subrayado"
                meta.contains("Note", true) || meta.contains("nota", true) -> "nota"
                meta.contains("Bookmark", true) || meta.contains("marcador", true) -> "marcador"
                else -> "otro"
            }
            val cuerpo = l.drop(2).joinToString("\n").trim()
            if (tipo == "marcador" && cuerpo.isEmpty()) return
            val ms = fechaKindle(meta)
            lista.add(Subrayado(
                id = "kd_" + hash("$libro|$meta|$cuerpo"), libro = libro, autor = autor, tipo = tipo,
                ubicacion = RX_POS.find(meta)?.groupValues?.get(1) ?: "", pagina = RX_PAG.find(meta)?.groupValues?.get(1)?.toIntOrNull(),
                ts = ms?.let { Fechas.isoDeMillis(it) }, fecha = ms?.let { Fechas.diaLogicoDeMillis(it) }, texto = cuerpo,
            ))
        }
        texto.lines().forEach { if (SEPARADOR.matches(it)) cerrar() else bloque.add(it) }
        cerrar()
        if (lista.isEmpty()) return "No encontré subrayados. El archivo se llama «My Clippings.txt» y está en la carpeta documents del Kindle."
        Repo.importarSubrayados(lista)
        val libros = lista.map { it.libro }.distinct()
        return "Importé ${lista.size} subrayados y notas de ${libros.size} libros" + (lista.count { it.fecha == null }.takeIf { it > 0 }?.let { " ($it sin fecha legible)" } ?: "") + "."
    }

    // ---------------- Google Takeout ----------------
    private val PREFIJOS = listOf(
        "Watched " to "visto", "Viste " to "visto", "Has visto " to "visto", "Visto " to "visto",
        "Searched for " to "buscado", "Buscaste " to "buscado", "Has buscado " to "buscado",
        "Visited " to "visitado", "Visitaste " to "visitado", "Has visitado " to "visitado",
    )

    /**
     * «Mi actividad» de Google Takeout en formato JSON: el historial de YouTube (watch-history.json, search-history.json)
     * y el de la Búsqueda de Google (MyActivity.json). Se puede elegir el .zip completo.
     */
    fun google(ctx: Context, uris: List<Uri>): String = googleFuentes(ctx, Archivos.deDocumentos(ctx, uris))

    /**
     * Google Takeout: el historial de YouTube y Mi actividad (JSON), y los CSV de YouTube (suscripciones, «me gusta», listas, comentarios).
     * Acepta el .zip, archivos sueltos (varios a la vez) o la carpeta ya descomprimida.
     */
    fun googleFuentes(ctx: Context, fuentes: List<Archivos.Fuente>): String {
        val lista = ArrayList<Huella>()
        val yt = ArrayList<YtEvento>(); var conSubs = false
        // Dentro de un .zip solo se abren los historiales; lo que elegiste suelto se revisa siempre (se reconoce por su contenido).
        val filtro = { n: String -> (n.endsWith(".json", true) && (n.contains("history", true) || n.contains("activity", true) || n.contains("actividad", true) || n.contains("historial", true))) ||
            n.endsWith(".csv", true) }
        val vistos = ArrayList<String>(); val fallas = ArrayList<String>()
        Archivos.recorrer(ctx, fuentes, filtro, sueltos = { true }) { ruta, _, s ->
            val nombreArch = ruta.substringAfterLast('/')
            if (nombreArch.endsWith(".csv", true)) {
                val r = youtubeCsv(ruta, s)
                if (r.first) conSubs = true
                yt.addAll(r.second)
                vistos += nombreArch
                return@recorrer
            }
            val antes = lista.size
            try {
                // Algunos archivos traen una marca BOM al inicio que el lector de JSON no acepta.
                val br = java.io.BufferedReader(InputStreamReader(s, Charsets.UTF_8))
                br.mark(4); if (br.read() != 0xFEFF) br.reset()
                val r = JsonReader(br)
                r.isLenient = true
                if (r.peek() != JsonToken.BEGIN_ARRAY) { fallas += "$nombreArch (no es un historial)"; return@recorrer }
                r.beginArray()
                while (r.hasNext()) {
                    if (r.peek() != JsonToken.BEGIN_OBJECT) { r.skipValue(); continue }
                    var header = ""; var titulo = ""; var tiempo: String? = null; var canal = ""; val productos = ArrayList<String>(); var anuncio = false
                    r.beginObject()
                    while (r.hasNext()) {
                        val k = r.nextName()
                        val t = r.peek()
                        when {
                            k == "header" && t == JsonToken.STRING -> header = r.nextString()
                            k == "title" && t == JsonToken.STRING -> titulo = r.nextString()
                            k == "time" && t == JsonToken.STRING -> tiempo = r.nextString()
                            k == "subtitles" && t == JsonToken.BEGIN_ARRAY -> {
                                r.beginArray()
                                while (r.hasNext()) {
                                    if (r.peek() != JsonToken.BEGIN_OBJECT) { r.skipValue(); continue }
                                    r.beginObject()
                                    while (r.hasNext()) { if (r.nextName() == "name" && canal.isEmpty() && r.peek() == JsonToken.STRING) canal = r.nextString() else r.skipValue() }
                                    r.endObject()
                                }
                                r.endArray()
                            }
                            k == "products" && t == JsonToken.BEGIN_ARRAY -> { r.beginArray(); while (r.hasNext()) { if (r.peek() == JsonToken.STRING) productos.add(r.nextString()) else r.skipValue() }; r.endArray() }
                            // En el historial de YouTube, «details» marca los anuncios («From Google Ads»).
                            k == "details" -> { anuncio = true; r.skipValue() }
                            else -> r.skipValue()
                        }
                    }
                    r.endObject()
                    val ms = tiempo?.let { x -> runCatching { Instant.parse(x).toEpochMilli() }.getOrNull() ?: runCatching { java.time.OffsetDateTime.parse(x).toInstant().toEpochMilli() }.getOrNull() } ?: continue
                    if (anuncio) continue
                    val (pref, accion) = PREFIJOS.firstOrNull { titulo.startsWith(it.first) } ?: ("" to "otro")
                    val esYoutube = header.contains("YouTube", true) || productos.any { it.contains("YouTube", true) } || nombreArch.contains("watch-history", true) ||
                        nombreArch.contains("search-history", true) || nombreArch.contains("reproducci", true)
                    val producto = when {
                        esYoutube -> "youtube"
                        header.contains("Search", true) || header.contains("Búsqueda", true) || productos.any { it.contains("Search", true) || it.contains("Búsqueda", true) } -> "busqueda"
                        else -> header.lowercase().ifBlank { "otro" }
                    }
                    lista.add(Huella(id = "gg_" + hash("$ms|$titulo"), ts = Fechas.isoDeMillis(ms), fecha = Fechas.diaLogicoDeMillis(ms),
                        producto = producto, accion = accion, titulo = titulo.removePrefix(pref).take(300), canal = canal.take(120)))
                }
                r.endArray()
            } catch (e: Exception) {
                fallas += "$nombreArch (se leyó hasta el registro ${lista.size - antes + 1}: ${e.message?.take(80)})"
            }
            if (lista.size > antes) vistos += nombreArch else if (fallas.none { it.startsWith(nombreArch) }) fallas += "$nombreArch (sin registros con fecha)"
        }
        val msgYt = if (yt.isNotEmpty()) {
            Repo.importarYoutube(yt.distinctBy { it.id }, conSubs)
            val c = yt.groupingBy { it.tipo }.eachCount()
            listOfNotNull(c["suscripcion"]?.let { "$it suscripciones" }, c["megusta"]?.let { "$it «me gusta»" }, c["lista"]?.let { "$it videos en listas" },
                c["comentario"]?.let { "$it comentarios (solo fecha)" }, c["chat"]?.let { "$it mensajes en chats en vivo (solo fecha)" }).joinToString(", ")
        } else null
        val detalle = if (fallas.isNotEmpty()) " No pude usar: " + fallas.take(5).joinToString("; ") + "." else ""
        if (lista.isEmpty()) return (msgYt?.let { "YouTube: $it." } ?: ("No encontré actividad" + if (fuentes.isEmpty()) " (no llegó ningún archivo)." else
            " en ${fuentes.size} archivo(s). Revisa que el historial esté en formato JSON (no HTML).")) + detalle
        Repo.importarHuellas(lista)
        // Resumen por día, recalculado con todo lo guardado en esas fechas para no contar dos veces.
        val desde = lista.minOf { it.fecha }; val hasta = lista.maxOf { it.fecha }
        val todas = Repo.huellasDe(desde, hasta).distinctBy { it.id }
        val dias = todas.groupBy { it.fecha }.map { (f, l) ->
            HuellasDia(fecha = f,
                yt_vistos = l.count { it.producto == "youtube" && it.accion == "visto" },
                yt_busquedas = l.count { it.producto == "youtube" && it.accion == "buscado" },
                busquedas = l.count { it.producto == "busqueda" && it.accion == "buscado" },
                visitas = l.count { it.accion == "visitado" },
                otras = l.count { it.accion == "otro" },
                primera = l.minOf { it.ts }, ultima = l.maxOf { it.ts },
                canales = l.filter { it.canal.isNotBlank() }.groupingBy { it.canal }.eachCount().entries.sortedByDescending { it.value }.take(10).associate { it.key to it.value })
        }
        Repo.guardarHuellasDia(dias)
        return "Importé ${lista.size} registros (${lista.count { it.producto == "youtube" && it.accion == "visto" }} videos vistos, " +
            "${lista.count { it.accion == "buscado" }} búsquedas) del $desde al $hasta." + (msgYt?.let { " YouTube: $it." } ?: "") + detalle
    }

    // ---------------- CSV de YouTube (Takeout) ----------------

    /** Lee un CSV completo respetando comillas, comas y saltos de línea dentro de las celdas (los comentarios los traen). */
    private fun filasCsv(s: InputStream): List<List<String>> {
        val r = s.bufferedReader(Charsets.UTF_8)
        val filas = ArrayList<List<String>>(); var fila = ArrayList<String>(); val sb = StringBuilder(); var q = false
        while (true) {
            val c = r.read(); if (c < 0) break
            val ch = c.toChar()
            if (q) {
                if (ch == '"') { r.mark(1); val n = r.read(); if (n == '"'.code) sb.append('"') else { q = false; if (n >= 0) r.reset() } } else sb.append(ch)
            } else when (ch) {
                '"' -> q = true
                ',' -> { fila.add(sb.toString()); sb.clear() }
                '\r' -> {}
                '\n' -> { fila.add(sb.toString()); sb.clear(); if (fila.any { it.isNotBlank() }) filas.add(fila); fila = ArrayList() }
                else -> sb.append(ch)
            }
        }
        if (sb.isNotEmpty() || fila.isNotEmpty()) { fila.add(sb.toString()); if (fila.any { it.isNotBlank() }) filas.add(fila) }
        return filas
    }

    private val F_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.US)
    private fun msTakeout(t: String): Long? {
        val x = t.trim().ifEmpty { return null }
        return runCatching { java.time.OffsetDateTime.parse(x).toInstant().toEpochMilli() }.getOrNull()
            ?: runCatching { Instant.parse(x).toEpochMilli() }.getOrNull()
            ?: runCatching { java.time.ZonedDateTime.parse(x, F_UTC).toInstant().toEpochMilli() }.getOrNull()
            ?: runCatching { LocalDateTime.parse(x.take(19).replace(' ', 'T')).toInstant(ZoneOffset.UTC).toEpochMilli() }.getOrNull()
    }

    private fun col(cab: List<String>, vararg claves: String): Int =
        cab.indexOfFirst { h -> val b = h.trim().removePrefix("\uFEFF").lowercase(); claves.any { b == it || b.contains(it) } }

    /**
     * Un CSV de YouTube según su nombre: suscripciones, la lista de «me gusta», cualquier otra lista, comentarios o chats en vivo.
     * Devuelve si traía suscripciones y los eventos. Del texto de comentarios y chats no se guarda nada.
     */
    private fun youtubeCsv(ruta: String, s: InputStream): Pair<Boolean, List<YtEvento>> {
        val n = ruta.substringAfterLast('/').removeSuffix(".csv").removeSuffix(".CSV")
        val bajo = n.lowercase()
        val carpeta = ruta.substringBeforeLast('/', "").lowercase()
        val filas = filasCsv(s)
        if (filas.size < 2) return false to emptyList()
        val out = ArrayList<YtEvento>()
        fun evento(tipo: String, ms: Long?, video: String = "", canal: String = "", lista: String = "", clave: String) {
            out += YtEvento("yt_" + hash("$tipo|$clave"), tipo, ms?.let { Fechas.isoDeMillis(it) } ?: "", ms?.let { Fechas.diaLogicoDeMillis(it) } ?: "", video, canal.take(120), lista.take(120))
        }
        when {
            bajo.contains("subscription") || bajo.contains("suscripcion") -> {
                val cab = filas[0]
                val iId = col(cab, "channel id", "id del canal", "id. del canal"); val iT = col(cab, "channel title", "título del canal", "titulo del canal", "nombre del canal")
                filas.drop(1).forEach { f ->
                    val t = f.getOrNull(iT)?.trim().orEmpty(); val id = f.getOrNull(iId)?.trim().orEmpty()
                    if (t.isNotEmpty() || id.isNotEmpty()) evento("suscripcion", null, canal = t.ifEmpty { id }, clave = id.ifEmpty { t })
                }
                return true to out
            }
            bajo.contains("comment") || bajo.contains("comentario") || bajo.contains("live chat") || bajo.contains("chats en vivo") || bajo.contains("chat en vivo") -> {
                val tipo = if (bajo.contains("chat")) "chat" else "comentario"
                val cab = filas[0]
                val iTs = col(cab, "create timestamp", "creation timestamp", "marca de tiempo", "timestamp", "fecha")
                val iV = col(cab, "video id", "id del video", "id. del video"); val iId = col(cab, "comment id", "id del comentario", "chat id", "id del chat")
                if (iTs < 0) return false to emptyList()
                filas.drop(1).forEachIndexed { k, f ->
                    val ms = msTakeout(f.getOrNull(iTs).orEmpty()) ?: return@forEachIndexed
                    evento(tipo, ms, video = f.getOrNull(iV)?.trim().orEmpty(), clave = f.getOrNull(iId)?.trim()?.ifEmpty { null } ?: "$ms|$k")
                }
            }
            bajo == "playlists" || bajo == "listas de reproducción" || bajo == "listas de reproduccion" || bajo == "channel" || bajo == "canal" -> return false to emptyList()
            // Cada lista: «Liked videos-videos.csv», «Videos que me gustan-videos.csv» o «<nombre>-videos.csv»; también el formato viejo con cabecera de la lista.
            carpeta.contains("playlist") || carpeta.contains("lista") || bajo.endsWith("-videos") || bajo.endsWith("-vídeos") || bajo.contains("liked") || bajo.contains("me gusta") -> {
                val nombre = n.removeSuffix("-videos").removeSuffix("-vídeos").trim()
                val megusta = Regex("liked|me gusta|gustan", RegexOption.IGNORE_CASE).containsMatchIn(nombre)
                var cab = filas[0]; var desde = 1
                // Formato viejo: primero los datos de la lista y luego, en otra cabecera, «Video Id,Time Added».
                val otra = filas.indexOfFirst { f -> f.any { it.trim().equals("Video Id", true) || it.trim().equals("Video ID", true) || it.trim().equals("ID del video", true) } }
                if (otra >= 0) { cab = filas[otra]; desde = otra + 1 }
                val iV = col(cab, "video id", "id del video", "id. del video"); val iTs = col(cab, "timestamp", "time added", "marca de tiempo", "fecha")
                if (iV < 0) return false to emptyList()
                filas.drop(desde).forEach { f ->
                    val v = f.getOrNull(iV)?.trim().orEmpty(); if (v.isEmpty()) return@forEach
                    evento(if (megusta) "megusta" else "lista", msTakeout(f.getOrNull(iTs).orEmpty()), video = v, lista = if (megusta) "" else nombre, clave = "$nombre|$v")
                }
            }
        }
        return false to out
    }

    // ---------------- WhatsApp ----------------
    data class Mensaje(val ms: Long, val autor: String, val palabras: Int, val multimedia: Boolean)
    data class Chat(val nombre: String, val mensajes: List<Mensaje>) { val autores: List<String> get() = mensajes.groupingBy { it.autor }.eachCount().entries.sortedByDescending { it.value }.map { it.key } }

    // Android: «16/10/26, 13:49 - Ana: hola»; iPhone: «[16/10/26, 1:49:02 p.m.] Ana: hola». También acepta a. m./p. m. y AM/PM.
    private val RX_ANDROID = Regex("^\\u200E?(\\d{1,2})/(\\d{1,2})/(\\d{2,4}),?\\s+(\\d{1,2}):(\\d{2})(?::(\\d{2}))?\\s*([aApP]\\.?\\s?[mM]\\.?)?\\s*[-–]\\s+(.+?):\\s(.*)$")
    private val RX_IOS = Regex("^\\u200E?\\[(\\d{1,2})/(\\d{1,2})/(\\d{2,4}),?\\s+(\\d{1,2}):(\\d{2})(?::(\\d{2}))?\\s*([aApP]\\.?\\s?[mM]\\.?)?\\]\\s+(.+?):\\s(.*)$")
    private val RX_FECHA = Regex("^\\u200E?\\[?\\d{1,2}/\\d{1,2}/\\d{2,4}")
    private val MULTIMEDIA = listOf("omitido", "omitida", "omitted", "<adjunto:", "<attached:")

    private fun nombreChat(archivo: String): String = archivo.removeSuffix(".txt").removeSuffix(".zip")
        .replace(Regex("^(Chat de WhatsApp con|WhatsApp Chat with|WhatsApp Chat -|Chat de WhatsApp -)\\s*", RegexOption.IGNORE_CASE), "").trim().ifEmpty { "chat" }

    /** Lee los chats exportados («Exportar chat → Sin archivos»). No guarda nada todavía: primero hay que decir cuál autor eres tú. */
    fun whatsappLeer(ctx: Context, uris: List<Uri>): List<Chat> {
        val chats = ArrayList<Chat>()
        for (u in uris) runCatching {
            abrir(ctx, u, { it.endsWith(".txt", true) }) { nombreArch, s ->
                val lineas = s.bufferedReader(Charsets.UTF_8).readLines()
                data class Crudo(val a: Int, val b: Int, val y: Int, val h: Int, val mi: Int, val se: Int, val ampm: String?, val autor: String, val txt: String)
                val crudos = ArrayList<Crudo>()
                for (bruta in lineas) {
                    // Los exportes recientes usan espacios especiales antes de «p. m.»: se normalizan.
                    val l = bruta.replace('\u202F', ' ').replace('\u00A0', ' ')
                    val m = RX_IOS.find(l) ?: RX_ANDROID.find(l)
                    if (m == null) {
                        // Avisos del sistema («X cambió el asunto»): tienen fecha pero no autor. Se ignoran.
                        if (RX_FECHA.containsMatchIn(l)) continue
                        // Línea que continúa el mensaje anterior: suma sus palabras.
                        crudos.lastOrNull()?.let { c -> crudos[crudos.size - 1] = c.copy(txt = c.txt + " " + l) }
                        continue
                    }
                    val g = m.groupValues
                    crudos.add(Crudo(g[1].toInt(), g[2].toInt(), g[3].toInt().let { if (it < 100) 2000 + it else it }, g[4].toInt(), g[5].toInt(), g[6].toIntOrNull() ?: 0, g[7].ifBlank { null }, g[8].trim(), g[9]))
                }
                if (crudos.isEmpty()) return@abrir
                // ¿Día/mes o mes/día? Si algún primer número pasa de 12, es día/mes; si el segundo pasa de 12, mes/día.
                val mesDia = crudos.any { it.b > 12 } && crudos.none { it.a > 12 }
                val msgs = crudos.mapNotNull { c ->
                    val dia = if (mesDia) c.b else c.a; val mes = if (mesDia) c.a else c.b
                    var h = c.h
                    c.ampm?.lowercase()?.let { ap -> val pm = ap.startsWith("p"); if (pm && h < 12) h += 12; if (!pm && h == 12) h = 0 }
                    val ms = runCatching { LocalDateTime.of(c.y, mes, dia, h, c.mi, c.se).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() }.getOrNull() ?: return@mapNotNull null
                    val multi = MULTIMEDIA.any { c.txt.contains(it, true) }
                    Mensaje(ms, c.autor.removePrefix("‎").trim(), if (multi) 0 else Texto.palabras(c.txt), multi)
                }
                chats.add(Chat(nombreChat(nombreArch), msgs))
            }
        }
        return chats
    }

    /** Convierte los chats en conteos por día. Los textos se descartan aquí y nunca se guardan. */
    fun whatsappGuardar(chats: List<Chat>, yo: String): String {
        val out = ArrayList<ChatDia>()
        for (c in chats) {
            val grupo = c.autores.size > 2
            val ordenados = c.mensajes.sortedBy { it.ms }
            for ((fecha, l) in ordenados.groupBy { Fechas.diaLogicoDeMillis(it.ms) }) {
                val mios = l.filter { it.autor == yo }; val otros = l.filter { it.autor != yo }
                // Tiempo de respuesta: minutos entre el último mensaje del otro y la primera respuesta (si fue en menos de 12 horas).
                val respMia = ArrayList<Double>(); val respOtros = ArrayList<Double>()
                var conversaciones = 0; var inicie = 0
                var prev: Mensaje? = ordenados.lastOrNull { it.ms < l.first().ms }
                for (m in l) {
                    val p = prev
                    if (p == null || m.ms - p.ms > 4 * 3_600_000L) { conversaciones++; if (m.autor == yo) inicie++ }
                    if (p != null && p.autor != m.autor && m.ms - p.ms < 12 * 3_600_000L) {
                        val min = (m.ms - p.ms) / 60_000.0
                        if (m.autor == yo) respMia.add(min) else if (p.autor == yo) respOtros.add(min)
                    }
                    prev = m
                }
                fun mediana(x: List<Double>) = if (x.isEmpty()) null else x.sorted()[x.size / 2].let { Math.round(it * 10) / 10.0 }
                out.add(ChatDia(id = "wa_" + hash("${c.nombre}|$fecha"), fecha = fecha, chat = c.nombre, grupo = grupo,
                    mios = mios.size, otros = otros.size, palabras_mias = mios.sumOf { it.palabras }, palabras_otros = otros.sumOf { it.palabras },
                    multimedia = l.count { it.multimedia }, primero = Fechas.isoDeMillis(l.first().ms), ultimo = Fechas.isoDeMillis(l.last().ms),
                    resp_mia_min = mediana(respMia), resp_otros_min = mediana(respOtros), inicie = inicie, conversaciones = conversaciones))
            }
        }
        Repo.importarChats(out)
        Repo.cambiarConfig { it.copy(whatsapp_yo = yo) }
        return "Guardé la actividad de ${chats.size} chat(s): ${out.size} días con mensajes. Solo conteos y tiempos; ningún texto."
    }
}
