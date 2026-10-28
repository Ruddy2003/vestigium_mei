package io.github.ruddy2003.vestigium.data

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Peticiones HTTP mínimas: clima y aire (Open-Meteo), Google Drive, Spotify y las fichas de obras (TMDB, TVmaze, RAWG, Google Books, Open Library, Crossref). */
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

    /** Descarga binaria (un respaldo de Drive). */
    fun bajar(url: String, encabezados: Map<String, String> = emptyMap(), tiempo: Int = 120_000): ByteArray {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 20_000; c.readTimeout = tiempo
        c.setRequestProperty("User-Agent", AGENTE)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        try {
            val code = c.responseCode
            if (code !in 200..299) throw IOException("HTTP $code ${c.errorStream?.bufferedReader()?.use { it.readText() }?.take(300) ?: ""}")
            return c.inputStream.use { it.readBytes() }
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
