package io.github.ruddy2003.vestigium.data

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Peticiones HTTP mínimas: clima y aire (Open-Meteo), Google Drive, Spotify, las fichas de obras (TMDB, TVmaze, RAWG, Google Books, Open Library, Crossref) y «Hoy en la historia» (Wikipedia). */
object Red {
    const val AGENTE = "VestigiumMei/1.0 (Android; uso personal)"

    /**
     * Todo lo que sale del teléfono hacia un servicio ajeno y qué recibe cada uno (H-8-17). Lo muestran la política de privacidad
     * y Ajustes › Privacidad; si se agrega un servicio, va aquí.
     */
    val SERVICIOS: List<Pair<String, String>> = listOf(
        "Open-Meteo (clima y aire)" to "tu zona aproximada (coordenadas redondeadas a ~1 km) y las fechas",
        "OpenStreetMap (mapa)" to "los mosaicos de calles de la zona que miras en el mapa",
        "TMDB, TVmaze, RAWG, Google Books, Open Library y Crossref" to "solo el título o el DOI que escribes al buscar una obra, según el tipo",
        "Spotify (si lo conectas)" to "tu permiso para leer lo que escuchaste; la app le pide tu historial reciente",
        "Google Drive (si lo activas)" to "tus respaldos completos, cifrados si pones contraseña",
        "Wikipedia en español (Hoy en la historia)" to "solo el mes y el día de hoy o de mañana, para traer lo que pasó un día como ese",
    )

    fun get(url: String, encabezados: Map<String, String> = emptyMap(), tiempo: Int = 20_000): String {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = tiempo; c.readTimeout = tiempo
        c.setRequestProperty("User-Agent", AGENTE)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        try {
            val code = c.responseCode
            val cuerpo = (if (code in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.use { it.readText() } ?: ""
            if (code !in 200..299) throw IOException("HTTP $code ${cuerpo.take(300)}")
            return cuerpo
        } finally { c.disconnect() }
    }

    /** Envía un cuerpo (POST/PATCH) y devuelve la respuesta. */
    fun enviar(url: String, metodo: String, tipo: String, cuerpo: ByteArray, encabezados: Map<String, String> = emptyMap(), tiempo: Int = 60_000): String {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = tiempo; c.readTimeout = tiempo
        if (metodo == "PATCH") { c.requestMethod = "POST"; c.setRequestProperty("X-HTTP-Method-Override", "PATCH") } else c.requestMethod = metodo
        c.doOutput = true
        c.setRequestProperty("User-Agent", AGENTE)
        c.setRequestProperty("Content-Type", tipo)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        c.setFixedLengthStreamingMode(cuerpo.size)
        try {
            c.outputStream.use { it.write(cuerpo) }
            val code = c.responseCode
            val r = (if (code in 200..299) c.inputStream else c.errorStream)?.bufferedReader()?.use { it.readText() } ?: ""
            if (code !in 200..299) throw IOException("HTTP $code ${r.take(300)}")
            return r
        } finally { c.disconnect() }
    }

    /**
     * Sube [archivo] por partes con una sesión reanudable de Google (9 nov 2026): primero se abre la sesión con los datos del
     * archivo ([inicio], con `uploadType=resumable`) y luego el contenido sale directo del archivo, sin cargarlo en memoria.
     * Antes el respaldo entero (y una copia para el envío) se leía en memoria, y con años de datos la subida podía fallar.
     */
    fun subirPorPartes(inicio: String, metodo: String, meta: String, tipo: String, archivo: java.io.File, encabezados: Map<String, String>, tiempo: Int = 300_000): String {
        val c = URL(inicio).openConnection() as HttpURLConnection
        c.connectTimeout = 30_000; c.readTimeout = 60_000
        if (metodo == "PATCH") { c.requestMethod = "POST"; c.setRequestProperty("X-HTTP-Method-Override", "PATCH") } else c.requestMethod = metodo
        c.doOutput = true
        c.setRequestProperty("User-Agent", AGENTE)
        c.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        c.setRequestProperty("X-Upload-Content-Type", tipo)
        c.setRequestProperty("X-Upload-Content-Length", archivo.length().toString())
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        val m = meta.toByteArray()
        c.setFixedLengthStreamingMode(m.size)
        val sesion = try {
            c.outputStream.use { it.write(m) }
            val code = c.responseCode
            if (code !in 200..299) throw IOException("HTTP $code ${c.errorStream?.bufferedReader()?.use { it.readText() }?.take(300) ?: ""}")
            c.getHeaderField("Location") ?: throw IOException("Drive no abrió la sesión de subida")
        } finally { c.disconnect() }
        val p = URL(sesion).openConnection() as HttpURLConnection
        p.connectTimeout = 30_000; p.readTimeout = tiempo
        p.requestMethod = "PUT"
        p.doOutput = true
        p.setRequestProperty("User-Agent", AGENTE)
        p.setRequestProperty("Content-Type", tipo)
        encabezados.forEach { (k, v) -> p.setRequestProperty(k, v) }
        p.setFixedLengthStreamingMode(archivo.length())
        try {
            p.outputStream.use { o -> archivo.inputStream().use { it.copyTo(o, 64 * 1024) } }
            val code = p.responseCode
            val r = (if (code in 200..299) p.inputStream else p.errorStream)?.bufferedReader()?.use { it.readText() } ?: ""
            if (code !in 200..299) throw IOException("HTTP $code ${r.take(300)}")
            return r
        } finally { p.disconnect() }
    }

    /** Descarga directo a un archivo, por partes (un respaldo grande de Drive no se carga en memoria). */
    fun bajarA(url: String, destino: java.io.File, encabezados: Map<String, String> = emptyMap(), tiempo: Int = 300_000) {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 20_000; c.readTimeout = tiempo
        c.setRequestProperty("User-Agent", AGENTE)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        try {
            val code = c.responseCode
            if (code !in 200..299) throw IOException("HTTP $code ${c.errorStream?.bufferedReader()?.use { it.readText() }?.take(300) ?: ""}")
            c.inputStream.use { i -> destino.outputStream().buffered(64 * 1024).use { i.copyTo(it, 64 * 1024) } }
        } finally { c.disconnect() }
    }

    fun borrar(url: String, encabezados: Map<String, String>) {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = "DELETE"
        c.setRequestProperty("User-Agent", AGENTE)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        try { c.responseCode } finally { c.disconnect() }
    }

    fun codificar(s: String): String = java.net.URLEncoder.encode(s, "UTF-8")
}
