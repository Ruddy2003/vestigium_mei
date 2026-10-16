package io.github.ruddy2003.vestigium.data

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/** Peticiones HTTP mínimas. Solo se usan para clima/aire (Open-Meteo), mosaicos del mapa y Google Drive. */
object Red {
    const val AGENTE = "VestigiumMei/1.0 (Android; uso personal)"

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

    fun borrar(url: String, encabezados: Map<String, String>) {
        val c = URL(url).openConnection() as HttpURLConnection
        c.requestMethod = "DELETE"
        c.setRequestProperty("User-Agent", AGENTE)
        encabezados.forEach { (k, v) -> c.setRequestProperty(k, v) }
        try { c.responseCode } finally { c.disconnect() }
    }

    fun codificar(s: String): String = java.net.URLEncoder.encode(s, "UTF-8")
}
