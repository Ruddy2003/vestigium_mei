package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File

/**
 * Buscador de obras y sus portadas. Libros: Google Books y Open Library. Series: TVmaze (o TMDB si hay clave).
 * Películas: TMDB. Juegos: RAWG. Artículos: Crossref. Solo se manda el título que escribes; nada tuyo.
 * Las claves de TMDB y RAWG se guardan aparte, solo en este teléfono: no entran en respaldos ni exportaciones.
 * La portada elegida se baja una vez y se guarda en la carpeta privada de la app.
 */
object Portadas {
    class Resultado(
        val fuente: String, val titulo: String, val autor: String = "", val anio: Int? = null, val portada: String? = null,
        val total: Int? = null, val revista: String = "", val doi: String = "", val detalle: String = "",
    )

    // ------------------------------------------------------------ claves

    private fun prefs(ctx: Context) = ctx.getSharedPreferences("claves_busqueda", Context.MODE_PRIVATE)
    fun claveTmdb(ctx: Context): String = runCatching { prefs(ctx).getString("tmdb", "") }.getOrNull() ?: ""
    fun claveRawg(ctx: Context): String = runCatching { prefs(ctx).getString("rawg", "") }.getOrNull() ?: ""
    fun guardarClaves(ctx: Context, tmdb: String, rawg: String) {
        prefs(ctx).edit().putString("tmdb", tmdb.trim()).putString("rawg", rawg.trim()).apply()
    }

    /** TMDB acepta la «API Key» corta (v3) o el token largo de lectura (empieza con eyJ). */
    private fun tmdb(ctx: Context, ruta: String, params: String = ""): JsonObject {
        val k = claveTmdb(ctx)
        val largo = k.startsWith("eyJ")
        val url = "https://api.themoviedb.org/3/$ruta?language=es-MX" + (if (largo) "" else "&api_key=${Red.codificar(k)}") + params
        return JSON.parseToJsonElement(Red.get(url, if (largo) mapOf("Authorization" to "Bearer $k") else emptyMap())).jsonObject
    }

    /** Prueba una clave: null si funciona, o el problema en palabras. */
    fun probar(ctx: Context, servicio: String): String? {
        if ((if (servicio == "tmdb") claveTmdb(ctx) else claveRawg(ctx)).isBlank()) return "Falta la clave."
        return runCatching {
            if (servicio == "tmdb") tmdb(ctx, "configuration") else Red.get("https://api.rawg.io/api/games?key=${Red.codificar(claveRawg(ctx))}&page_size=1")
            null
        }.getOrElse { e -> if (e.message?.contains("401") == true || e.message?.contains("403") == true) "La clave no es válida." else "No pude conectar: ${e.message?.take(80)}" }
    }

    // ------------------------------------------------------------ búsqueda

    private fun JsonElement?.o(k: String): JsonElement? = (this as? JsonObject)?.get(k)
    private fun JsonElement?.s(k: String): String? = ((this as? JsonObject)?.get(k) as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() && it != "null" }
    private fun JsonElement?.i(k: String): Int? = ((this as? JsonObject)?.get(k) as? JsonPrimitive)?.let { it.intOrNull ?: it.contentOrNull?.toDoubleOrNull()?.toInt() }
    private fun JsonElement?.a(k: String): JsonArray? = (this as? JsonObject)?.get(k) as? JsonArray
    private fun anio(s: String?) = s?.take(4)?.toIntOrNull()
    private fun https(u: String?) = u?.replace("http://", "https://")

    /** Qué buscadores tiene cada tipo (para avisar si falta una clave). */
    fun disponible(ctx: Context, tipo: String): Boolean = when (tipo) {
        "pelicula" -> claveTmdb(ctx).isNotBlank()
        "juego" -> claveRawg(ctx).isNotBlank()
        else -> true
    }

    fun buscar(ctx: Context, tipo: String, q: String): List<Resultado> {
        val t = q.trim(); if (t.length < 2) return emptyList()
        val e = Red.codificar(t)
        return when (tipo) {
            "libro" -> {
                val google = runCatching {
                    JSON.parseToJsonElement(Red.get("https://www.googleapis.com/books/v1/volumes?q=$e&maxResults=15&printType=books")).a("items").orEmpty().mapNotNull { it.o("volumeInfo") }.map { v ->
                        Resultado("google:" + (v.s("title") ?: ""), v.s("title") ?: "", v.a("authors")?.joinToString(", ") { (it as? JsonPrimitive)?.contentOrNull ?: "" } ?: "",
                            anio(v.s("publishedDate")), https(v.o("imageLinks").s("thumbnail"))?.replace("&edge=curl", ""), v.i("pageCount"), detalle = v.s("publisher") ?: "")
                    }
                }.getOrDefault(emptyList())
                val ol = runCatching {
                    JSON.parseToJsonElement(Red.get("https://openlibrary.org/search.json?q=$e&limit=15&fields=key,title,author_name,first_publish_year,cover_i,number_of_pages_median"))
                        .a("docs").orEmpty().map { d ->
                            Resultado("openlibrary:" + (d.s("key") ?: ""), d.s("title") ?: "", d.a("author_name")?.take(2)?.joinToString(", ") { (it as? JsonPrimitive)?.contentOrNull ?: "" } ?: "",
                                d.i("first_publish_year"), d.i("cover_i")?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }, d.i("number_of_pages_median"))
                        }
                }.getOrDefault(emptyList())
                // Primero los que tienen portada; sin repetir el mismo título y autor.
                (google + ol).filter { it.titulo.isNotBlank() }.distinctBy { (it.titulo + "|" + it.autor).lowercase() }.sortedBy { if (it.portada == null) 1 else 0 }.take(20)
            }
            "serie" -> if (claveTmdb(ctx).isNotBlank()) tmdb(ctx, "search/tv", "&query=$e").a("results").orEmpty().take(15).map { r ->
                Resultado("tmdb:tv:" + r.s("id"), r.s("name") ?: "", "", anio(r.s("first_air_date")), r.s("poster_path")?.let { "https://image.tmdb.org/t/p/w500$it" },
                    detalle = r.s("original_name")?.takeIf { it != r.s("name") } ?: "")
            } else JSON.parseToJsonElement(Red.get("https://api.tvmaze.com/search/shows?q=$e")).jsonArray.take(15).mapNotNull { it.o("show") }.map { sh ->
                Resultado("tvmaze:" + sh.s("id"), sh.s("name") ?: "", sh.o("network").s("name") ?: sh.o("webChannel").s("name") ?: "", anio(sh.s("premiered")),
                    https(sh.o("image").s("original") ?: sh.o("image").s("medium")), detalle = sh.s("language") ?: "")
            }
            "pelicula" -> if (claveTmdb(ctx).isBlank()) emptyList() else tmdb(ctx, "search/movie", "&query=$e").a("results").orEmpty().take(15).map { r ->
                Resultado("tmdb:movie:" + r.s("id"), r.s("title") ?: "", "", anio(r.s("release_date")), r.s("poster_path")?.let { "https://image.tmdb.org/t/p/w500$it" },
                    detalle = r.s("original_title")?.takeIf { it != r.s("title") } ?: "")
            }
            "juego" -> if (claveRawg(ctx).isBlank()) emptyList() else JSON.parseToJsonElement(Red.get("https://api.rawg.io/api/games?key=${Red.codificar(claveRawg(ctx))}&search=$e&page_size=15"))
                .a("results").orEmpty().map { g ->
                    Resultado("rawg:" + g.s("id"), g.s("name") ?: "", "", anio(g.s("released")), g.s("background_image"),
                        detalle = g.a("platforms")?.take(3)?.mapNotNull { it.o("platform").s("name") }?.joinToString(", ") ?: "")
                }
            "articulo" -> {
                val doi = Regex("10\\.\\d{4,9}/\\S+").find(t)?.value
                val items = if (doi != null) listOf(JSON.parseToJsonElement(Red.get("https://api.crossref.org/works/${Red.codificar(doi)}")).jsonObject["message"]!!)
                    else JSON.parseToJsonElement(Red.get("https://api.crossref.org/works?query.bibliographic=$e&rows=15&select=DOI,title,author,container-title,issued,page")).o("message").a("items").orEmpty()
                items.map { w ->
                    val autores = w.a("author").orEmpty().map { a -> listOfNotNull(a.s("given"), a.s("family")).joinToString(" ") }.filter { it.isNotBlank() }
                    val fecha = w.o("issued").a("date-parts")?.firstOrNull()?.jsonArray?.firstOrNull()?.let { (it as? JsonPrimitive)?.intOrNull }
                    Resultado("crossref:" + (w.s("DOI") ?: ""), w.a("title")?.firstOrNull()?.let { (it as? JsonPrimitive)?.contentOrNull } ?: "",
                        if (autores.size > 3) autores.take(3).joinToString(", ") + " et al." else autores.joinToString(", "), fecha, null,
                        revista = w.a("container-title")?.firstOrNull()?.let { (it as? JsonPrimitive)?.contentOrNull } ?: "", doi = w.s("DOI") ?: "", detalle = w.s("page")?.let { "pp. $it" } ?: "")
                }.filter { it.titulo.isNotBlank() }
            }
            else -> emptyList()
        }
    }

    /** Completa lo que la búsqueda no trae (director, estudio, duración, capítulos). Si falla, devuelve el mismo resultado. */
    fun detalles(ctx: Context, r: Resultado): Resultado = runCatching {
        val partes = r.fuente.split(":")
        when (partes[0]) {
            "tmdb" -> if (partes[1] == "movie") {
                val d = tmdb(ctx, "movie/${partes[2]}", "&append_to_response=credits")
                val dir = d.o("credits").a("crew").orEmpty().filter { it.s("job") == "Director" }.mapNotNull { it.s("name") }.joinToString(", ")
                Resultado(r.fuente, r.titulo, dir, r.anio, r.portada, d.i("runtime"), detalle = r.detalle)
            } else {
                val d = tmdb(ctx, "tv/${partes[2]}")
                val creadores = d.a("created_by").orEmpty().mapNotNull { it.s("name") }.joinToString(", ").ifBlank { d.a("networks")?.firstOrNull().s("name") ?: "" }
                Resultado(r.fuente, r.titulo, creadores, r.anio, r.portada, d.i("number_of_episodes"), detalle = r.detalle)
            }
            "tvmaze" -> {
                val eps = JSON.parseToJsonElement(Red.get("https://api.tvmaze.com/shows/${partes[1]}/episodes")).jsonArray.size
                Resultado(r.fuente, r.titulo, r.autor, r.anio, r.portada, eps.takeIf { it > 0 }, detalle = r.detalle)
            }
            "rawg" -> {
                val d = JSON.parseToJsonElement(Red.get("https://api.rawg.io/api/games/${partes[1]}?key=${Red.codificar(claveRawg(ctx))}")).jsonObject
                val estudio = d.a("developers").orEmpty().mapNotNull { it.s("name") }.take(2).joinToString(", ")
                Resultado(r.fuente, r.titulo, estudio, r.anio, r.portada, d.i("playtime")?.takeIf { it > 0 }, detalle = r.detalle)
            }
            else -> r
        }
    }.getOrDefault(r)

    // ------------------------------------------------------------ portadas en el teléfono

    fun carpeta(ctx: Context) = File(ctx.filesDir, "portadas").apply { mkdirs() }
    fun archivo(ctx: Context, nombre: String?): File? = nombre?.let { n -> runCatching { File(carpeta(ctx), n) }.getOrNull() }?.takeIf { it.exists() }

    /** Reduce la imagen a un tamaño razonable (lado mayor ~700 px) y la guarda como JPG. */
    private fun guardar(ctx: Context, bytes: ByteArray, obraId: String): String? {
        val op = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, op)
        if (op.outWidth <= 0) return null
        var muestra = 1
        while (maxOf(op.outWidth, op.outHeight) / (muestra * 2) >= 700) muestra *= 2
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = muestra }) ?: return null
        val nombre = "$obraId-${System.currentTimeMillis() % 100000}.jpg"
        carpeta(ctx).listFiles()?.filter { it.name.startsWith("$obraId-") }?.forEach { it.delete() }
        File(carpeta(ctx), nombre).outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 86, it) }
        bmp.recycle()
        return nombre
    }

    fun bajar(ctx: Context, url: String, obraId: String): String? = runCatching {
        val c = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        c.connectTimeout = 20_000; c.readTimeout = 20_000; c.setRequestProperty("User-Agent", Red.AGENTE)
        val bytes = try { if (c.responseCode in 200..299) c.inputStream.use { it.readBytes() } else null } finally { c.disconnect() }
        bytes?.let { guardar(ctx, it, obraId) }
    }.getOrNull()

    fun desdeFoto(ctx: Context, uri: Uri, obraId: String): String? = runCatching {
        ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() }?.let { guardar(ctx, it, obraId) }
    }.getOrNull()

    fun borrar(ctx: Context, nombre: String?) { nombre?.let { File(carpeta(ctx), it).delete() } }

    /** Una imagen chica (para la lista de resultados) bajada a memoria, sin guardarla. */
    fun miniatura(url: String): Bitmap? = runCatching {
        val c = java.net.URL(url).openConnection() as java.net.HttpURLConnection
        c.connectTimeout = 15_000; c.readTimeout = 15_000; c.setRequestProperty("User-Agent", Red.AGENTE)
        val bytes = try { c.inputStream.use { it.readBytes() } } finally { c.disconnect() }
        val op = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, op)
        var m = 1; while (maxOf(op.outWidth, op.outHeight) / (m * 2) >= 240) m *= 2
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = m })
    }.getOrNull()

    /** Lee una portada guardada, reducida al tamaño en que se va a mostrar. */
    fun leer(f: File, lado: Int): Bitmap? = runCatching {
        val op = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(f.absolutePath, op)
        var m = 1; while (maxOf(op.outWidth, op.outHeight) / (m * 2) >= lado) m *= 2
        BitmapFactory.decodeFile(f.absolutePath, BitmapFactory.Options().apply { inSampleSize = m })
    }.getOrNull()

    const val CREDITOS = "Búsqueda con Google Books, Open Library, TVmaze, TMDB, RAWG y Crossref. Este producto usa la API de TMDB, pero TMDB no lo avala ni lo certifica. Datos de juegos de RAWG (rawg.io)."
}
