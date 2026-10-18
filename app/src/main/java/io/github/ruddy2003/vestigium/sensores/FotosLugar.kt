package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Mundo
import io.github.ruddy2003.vestigium.data.Repo
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.ZipInputStream

/**
 * Dónde tomaste tus fotos: el lugar y la hora que la cámara guarda dentro de cada foto, en el teléfono o en el Takeout de Google Fotos.
 * De cada foto se guarda solo eso (como una ubicación más, fuente «foto»); la imagen nunca se copia.
 */
object FotosLugar {
    class Resultado(val revisadas: Int, val conLugar: Int, val nuevas: Int) {
        override fun toString() = when {
            revisadas == 0 -> "No había fotos nuevas que revisar."
            conLugar == 0 -> "Revisé $revisadas fotos; ninguna trae el lugar guardado."
            else -> "Revisé $revisadas fotos: $conLugar traen lugar ($nuevas nuevas en tu mapa)."
        }
    }

    const val PERMISO = Manifest.permission.ACCESS_MEDIA_LOCATION

    fun tienePermiso(ctx: Context): Boolean = ctx.checkSelfPermission(PERMISO) == PackageManager.PERMISSION_GRANTED

    private fun prefs(ctx: Context) = ctx.getSharedPreferences("fotos_lugar", Context.MODE_PRIVATE)
    fun conLugar(ctx: Context): Int = prefs(ctx).getInt("con_lugar", 0) + prefs(ctx).getInt("takeout", 0)
    fun ultimaRevision(ctx: Context): Long = prefs(ctx).getLong("revision", 0L)

    private fun valido(lat: Double, lon: Double) = !(lat == 0.0 && lon == 0.0) && lat in -90.0..90.0 && lon in -180.0..180.0

    /** Guarda los puntos que falten (una foto que ya estaba no se repite). */
    private fun guardar(puntos: List<Triple<Long, Double, Double>>, fuente: String): Int {
        if (puntos.isEmpty()) return 0
        val db = Repo.db()
        var n = 0
        db.enTransaccion {
            for ((ms, la, lo) in puntos) if (!db.existeUbicacion(ms)) { db.ponerUbicacion(ms, la, lo, null, fuente); n++ }
        }
        return n
    }

    /**
     * Revisa las fotos del teléfono que no se han revisado (desde la última vez). La primera vez recorre toda la galería.
     * [avance] recibe cuántas van y cuántas son.
     */
    fun revisar(ctx: Context, avance: (Int, Int) -> Unit = { _, _ -> }): Resultado {
        if (!Galeria.tienePermiso(ctx) && !Galeria.parcial(ctx)) return Resultado(0, 0, 0)
        val p = prefs(ctx)
        // Si antes se revisó sin el permiso de ubicación de fotos (Android borraba el lugar), se empieza de nuevo.
        if (tienePermiso(ctx) && !p.getBoolean("con_permiso", false)) p.edit().putLong("ultimo_agregado", 0L).putInt("con_lugar", 0).putBoolean("con_permiso", true).apply()
        val desde = p.getLong("ultimo_agregado", 0L)
        val proy = arrayOf(MediaStore.Images.Media._ID, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.MediaColumns.DATE_ADDED)
        val filas = ArrayList<Triple<Long, Long, Long>>()
        runCatching {
            ctx.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proy, "${MediaStore.MediaColumns.DATE_ADDED} > ?", arrayOf(desde.toString()),
                "${MediaStore.MediaColumns.DATE_ADDED} ASC")?.use { c ->
                while (c.moveToNext()) filas += Triple(c.getLong(0), if (c.isNull(1)) 0L else c.getLong(1), c.getLong(2))
            }
        }
        var con = 0; var nuevas = 0; var ultimo = desde
        val lote = ArrayList<Triple<Long, Double, Double>>()
        val permiso = tienePermiso(ctx)
        filas.forEachIndexed { i, (id, tomada, agregada) ->
            var uri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            // Sin el permiso de ubicación de fotos, Android borra el lugar al leerla; con él se pide el original.
            if (permiso) uri = runCatching { MediaStore.setRequireOriginal(uri) }.getOrDefault(uri)
            runCatching {
                ctx.contentResolver.openInputStream(uri)?.use { s ->
                    val exif = ExifInterface(s)
                    val ll = exif.latLong
                    if (ll != null && valido(ll[0], ll[1])) {
                        val ms = if (tomada > 0) tomada else fechaExif(exif) ?: agregada * 1000
                        lote += Triple(ms, ll[0], ll[1]); con++
                    }
                }
            }
            ultimo = maxOf(ultimo, agregada)
            if (lote.size >= 300 || i == filas.lastIndex) { nuevas += guardar(lote, "foto"); lote.clear(); p.edit().putLong("ultimo_agregado", ultimo).apply() }
            if (i % 25 == 0) avance(i, filas.size)
        }
        avance(filas.size, filas.size)
        p.edit().putInt("con_lugar", p.getInt("con_lugar", 0) + con).putLong("revision", System.currentTimeMillis()).apply()
        if (nuevas > 0) Mundo.invalidar()
        return Resultado(filas.size, con, nuevas)
    }

    /** Vuelve a revisar todas desde el principio (por ejemplo, después de dar el permiso de ubicación de fotos). */
    fun reiniciar(ctx: Context) { prefs(ctx).edit().putLong("ultimo_agregado", 0L).putInt("con_lugar", 0).apply() }

    private fun fechaExif(e: ExifInterface): Long? {
        val t = e.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) ?: e.getAttribute(ExifInterface.TAG_DATETIME) ?: return null
        return runCatching { SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).parse(t)?.time }.getOrNull()
    }

    /**
     * Takeout de Google Fotos: junto a cada foto viene un .json con su fecha y su lugar («geoData»). Se leen solo esos archivos
     * pequeños; las fotos dentro del .zip se saltan sin guardarse.
     */
    fun importarTakeout(ctx: Context, uris: List<Uri>, avance: (String) -> Unit = {}): Resultado {
        var vistos = 0; var con = 0; var nuevas = 0
        val lote = ArrayList<Triple<Long, Double, Double>>()
        for ((k, u) in uris.withIndex()) {
            runCatching {
                ctx.contentResolver.openInputStream(u)?.use { ins ->
                    val z = ZipInputStream(ins.buffered(1 shl 16))
                    while (true) {
                        val e = z.nextEntry ?: break
                        val nombre = e.name.substringAfterLast('/')
                        if (!e.isDirectory && nombre.endsWith(".json", true) && nombre != "metadata.json" && (e.size in 0..200_000 || e.size == -1L)) {
                            val txt = z.readBytes()
                            if (txt.size < 200_000) leerSidecar(String(txt, Charsets.UTF_8))?.let { vistos++; lote += it; con++ }
                        }
                        if (lote.size >= 500) { nuevas += guardar(lote, "foto_takeout"); lote.clear(); avance("Archivo ${k + 1} de ${uris.size}: $con fotos con lugar…") }
                    }
                }
            }
            nuevas += guardar(lote, "foto_takeout"); lote.clear()
            avance("Archivo ${k + 1} de ${uris.size}: $con fotos con lugar…")
        }
        val p = prefs(ctx)
        p.edit().putInt("takeout", p.getInt("takeout", 0) + nuevas).apply()
        if (nuevas > 0) Mundo.invalidar()
        return Resultado(vistos, con, nuevas)
    }

    /** {"photoTakenTime":{"timestamp":"1590000000"},"geoData":{"latitude":19.4,"longitude":-99.1},"geoDataExif":{…}} */
    private fun leerSidecar(t: String): Triple<Long, Double, Double>? = runCatching {
        if (!t.contains("photoTakenTime") || !t.contains("geoData")) return null
        val o = JSON.parseToJsonElement(t).jsonObject
        val ts = o["photoTakenTime"]?.jsonObject?.get("timestamp")?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: return null
        fun geo(k: String): Pair<Double, Double>? = (o[k] as? JsonObject)?.let { g ->
            val la = g["latitude"]?.jsonPrimitive?.doubleOrNull; val lo = g["longitude"]?.jsonPrimitive?.doubleOrNull
            if (la != null && lo != null && valido(la, lo)) la to lo else null
        }
        val (la, lo) = geo("geoData") ?: geo("geoDataExif") ?: return null
        Triple(ts * 1000, la, lo)
    }.getOrNull()
}
