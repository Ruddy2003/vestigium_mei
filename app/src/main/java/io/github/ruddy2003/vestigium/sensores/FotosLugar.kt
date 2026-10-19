package io.github.ruddy2003.vestigium.sensores

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import io.github.ruddy2003.vestigium.data.Db
import io.github.ruddy2003.vestigium.data.Fechas
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
import kotlin.math.roundToLong

/**
 * Dónde tomaste tus fotos: el lugar y la hora que la cámara guarda dentro de cada foto, en el teléfono o en el Takeout de Google Fotos.
 * De cada foto se guarda solo eso, más de qué aparato o carpeta viene (para que cuenten solo las tuyas); la imagen nunca se copia.
 *
 * Todo pasa por un catálogo: las ubicaciones «foto» del mapa se rehacen desde él según lo que elijas (qué aparatos son tuyos,
 * cuáles tenían el reloj mal, si cuentan las fotos compartidas, qué fotos raras sí cuentan).
 */
object FotosLugar {
    class Resultado(val revisadas: Int, val conLugar: Int, val nuevas: Int) {
        override fun toString() = when {
            revisadas == 0 -> "No había fotos nuevas que revisar."
            conLugar == 0 -> "Revisé $revisadas fotos; ninguna trae el lugar guardado."
            else -> "Revisé $revisadas fotos: $conLugar traen lugar."
        }
    }

    const val PERMISO = Manifest.permission.ACCESS_MEDIA_LOCATION
    /** Antes de esto no había fotos con GPS (las primeras cámaras y teléfonos con GPS son de mediados de los 2000). */
    private val MINIMO: Long = java.time.LocalDate.of(2004, 1, 1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
    private const val VERSION_CATALOGO = 2

    fun tienePermiso(ctx: Context): Boolean = ctx.checkSelfPermission(PERMISO) == PackageManager.PERMISSION_GRANTED

    private fun prefs(ctx: Context) = ctx.getSharedPreferences("fotos_lugar", Context.MODE_PRIVATE)
    /** Cuántas fotos con lugar cuentan en tu mapa ahora mismo. */
    fun conLugar(ctx: Context): Int = runCatching { Repo.db().contarUbicacionesDeFotos().toInt() }.getOrDefault(0)
    /** Cuántas hay en el catálogo, cuenten o no. */
    fun enCatalogo(): Int = runCatching { Repo.db().contarFotos().toInt() }.getOrDefault(0)
    fun ultimaRevision(ctx: Context): Long = prefs(ctx).getLong("revision", 0L)

    private fun valido(lat: Double, lon: Double) = !(lat == 0.0 && lon == 0.0) && lat in -90.0..90.0 && lon in -180.0..180.0
    fun fechaPosible(ms: Long) = ms >= MINIMO && ms <= System.currentTimeMillis() + 86_400_000L

    /** La misma foto en el teléfono y en Takeout tiene la misma clave: segundo en que se tomó y lugar (a unos 10 m). */
    private fun clave(ms: Long, lat: Double, lon: Double) = "${ms / 1000}_${(lat * 10000).roundToLong()}_${(lon * 10000).roundToLong()}"

    // ------------------------------------------------------------------ de dónde viene cada foto

    private val CARPETAS_AJENAS = listOf("whatsapp", "telegram", "download", "messenger", "instagram", "facebook", "signal", "screenshot", "snapchat", "twitter", "reddit", "pinterest")

    /** «Google Pixel 10», «samsung SM-G973F»… sin repetir la marca cuando el modelo ya la trae. */
    fun nombreAparato(marca: String?, modelo: String?): String {
        val ma = marca?.trim().orEmpty(); val mo = modelo?.trim().orEmpty()
        return when {
            mo.isEmpty() -> ma
            ma.isEmpty() || mo.lowercase().startsWith(ma.lowercase()) -> mo
            else -> "$ma $mo"
        }
    }

    /** El nombre que ves en la lista: el aparato y, si la foto está en una carpeta de otra app (Telegram, Descargas…), también la carpeta. */
    private fun etiqueta(aparato: String, carpeta: String?): String {
        val c = carpeta?.trim('/')?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
        val ajena = c != null && CARPETAS_AJENAS.any { c.lowercase().contains(it) }
        return when {
            aparato.isNotEmpty() && ajena -> "$aparato · $c"
            aparato.isNotEmpty() -> aparato
            c != null -> "sin cámara · $c"
            else -> "sin datos de cámara"
        }
    }

    // ------------------------------------------------------------------ revisar la galería

    /**
     * Revisa las fotos del teléfono que no se han revisado (desde la última vez). La primera vez recorre toda la galería.
     * [avance] recibe cuántas van y cuántas son.
     */
    fun revisar(ctx: Context, avance: (Int, Int) -> Unit = { _, _ -> }): Resultado {
        if (!Galeria.tienePermiso(ctx) && !Galeria.parcial(ctx)) return Resultado(0, 0, 0)
        val p = prefs(ctx)
        // Sin el permiso de ubicación de fotos Android borraba el lugar; y el catálogo nuevo necesita el aparato de cada foto: se empieza de nuevo.
        if ((tienePermiso(ctx) && !p.getBoolean("con_permiso", false)) || p.getInt("catalogo", 0) < VERSION_CATALOGO)
            p.edit().putLong("ultimo_agregado", 0L).putBoolean("con_permiso", tienePermiso(ctx)).putInt("catalogo", VERSION_CATALOGO).apply()
        val desde = p.getLong("ultimo_agregado", 0L)
        val proy = arrayOf(MediaStore.Images.Media._ID, MediaStore.MediaColumns.DATE_TAKEN, MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.RELATIVE_PATH)
        class Fila(val id: Long, val tomada: Long, val agregada: Long, val ruta: String?)
        val filas = ArrayList<Fila>()
        runCatching {
            ctx.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proy, "${MediaStore.MediaColumns.DATE_ADDED} > ?", arrayOf(desde.toString()),
                "${MediaStore.MediaColumns.DATE_ADDED} ASC")?.use { c ->
                while (c.moveToNext()) filas += Fila(c.getLong(0), if (c.isNull(1)) 0L else c.getLong(1), c.getLong(2), c.getString(3))
            }
        }
        var con = 0; var ultimo = desde
        val lote = ArrayList<Db.FotoCat>()
        val permiso = tienePermiso(ctx)
        val db = Repo.db()
        filas.forEachIndexed { i, f ->
            var uri: Uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, f.id)
            // Sin el permiso de ubicación de fotos, Android borra el lugar al leerla; con él se pide el original.
            if (permiso) uri = runCatching { MediaStore.setRequireOriginal(uri) }.getOrDefault(uri)
            runCatching {
                ctx.contentResolver.openInputStream(uri)?.use { s ->
                    val exif = ExifInterface(s)
                    val ll = FloatArray(2)
                    if (exif.getLatLong(ll) && valido(ll[0].toDouble(), ll[1].toDouble())) {
                        val ms = if (f.tomada > 0) f.tomada else fechaExif(exif) ?: f.agregada * 1000
                        val ap = etiqueta(nombreAparato(exif.getAttribute(ExifInterface.TAG_MAKE), exif.getAttribute(ExifInterface.TAG_MODEL)), f.ruta)
                        lote += Db.FotoCat(clave(ms, ll[0].toDouble(), ll[1].toDouble()), ms, ll[0].toDouble(), ll[1].toDouble(), ap, "telefono", f.agregada * 1000, "foto"); con++
                    }
                }
            }
            ultimo = maxOf(ultimo, f.agregada)
            if (lote.size >= 300 || i == filas.lastIndex) { db.enTransaccion { lote.forEach { db.ponerFoto(it) } }; lote.clear(); p.edit().putLong("ultimo_agregado", ultimo).apply() }
            if (i % 25 == 0) avance(i, filas.size)
        }
        avance(filas.size, filas.size)
        p.edit().putLong("revision", System.currentTimeMillis()).apply()
        val antes = conLugar(ctx)
        if (con > 0) aplicar(ctx)
        return Resultado(filas.size, con, (conLugar(ctx) - antes).coerceAtLeast(0))
    }

    /** Vuelve a revisar todas desde el principio (por ejemplo, después de dar el permiso de ubicación de fotos). */
    fun reiniciar(ctx: Context) { prefs(ctx).edit().putLong("ultimo_agregado", 0L).apply() }

    private fun fechaExif(e: ExifInterface): Long? {
        val t = e.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL) ?: e.getAttribute(ExifInterface.TAG_DATETIME) ?: return null
        return runCatching { SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).parse(t)?.time }.getOrNull()
    }

    // ------------------------------------------------------------------ Takeout

    /**
     * Takeout de Google Fotos: junto a cada foto viene un .json con su fecha, su lugar («geoData») y de dónde llegó a Google Fotos
     * (subida desde tu teléfono y de qué carpeta, álbum compartido, fotos de tu pareja…). Si en el .zip también vienen las fotos,
     * de cada una se leen solo los primeros kilobytes, donde la cámara guarda su marca y modelo; nada más se guarda.
     */
    /** El Takeout de Google Fotos: uno o varios .zip, los .json sueltos (varios a la vez) o la carpeta ya descomprimida. */
    fun importarTakeout(ctx: Context, fuentes: List<io.github.ruddy2003.vestigium.data.Archivos.Fuente>, avance: (String) -> Unit = {}): Resultado {
        val lados = HashMap<String, Sidecar>()       // carpeta/título → datos del .json
        val camaras = HashMap<String, String>()      // carpeta/archivo → aparato (de la foto misma)
        var vistos = 0
        val buf = ByteArray(128 * 1024)
        val filtro = { n: String -> val b = n.lowercase(); (b.endsWith(".json") && b != "metadata.json") || b.endsWith(".jpg") || b.endsWith(".jpeg") }
        io.github.ruddy2003.vestigium.data.Archivos.recorrer(ctx, fuentes, filtro, { k -> avance("Archivo $k de ${fuentes.size}: ${"%,d".format(lados.size)} fotos con lugar…") }) { ruta, tam, ins ->
            val carpeta = ruta.substringBeforeLast('/', ""); val bajo = ruta.substringAfterLast('/').lowercase()
            if (bajo.endsWith(".json") && (tam in 0..200_000 || tam == -1L)) {
                val txt = ins.readBytes()
                if (txt.size < 200_000) leerSidecar(String(txt, Charsets.UTF_8))?.let { (titulo, l) ->
                    vistos++
                    lados["$carpeta/${titulo.lowercase()}"] = l
                    if (vistos % 500 == 0) avance("${"%,d".format(lados.size)} fotos con lugar…")
                }
            } else if (bajo.endsWith(".jpg") || bajo.endsWith(".jpeg")) {
                // Solo el principio del archivo: ahí está la marca y el modelo de la cámara.
                var n = 0
                while (n < buf.size) { val r = ins.read(buf, n, buf.size - n); if (r <= 0) break; n += r }
                runCatching {
                    val ex = ExifInterface(java.io.ByteArrayInputStream(buf, 0, n))
                    nombreAparato(ex.getAttribute(ExifInterface.TAG_MAKE), ex.getAttribute(ExifInterface.TAG_MODEL)).takeIf { it.isNotEmpty() }?.let { camaras["$carpeta/$bajo"] = it }
                }
            }
        }
        val db = Repo.db()
        val lista = lados.entries.toList()
        lista.chunked(500).forEach { trozo ->
            db.enTransaccion {
                for ((llave, l) in trozo) {
                    val ap = camaras[llave]?.let { cam -> if (l.origen == "compartida") "$cam · ${l.etiqueta}" else cam } ?: l.etiqueta
                    db.ponerFoto(Db.FotoCat(clave(l.ms, l.lat, l.lon), l.ms, l.lat, l.lon, ap, l.origen, l.subida, "foto_takeout"))
                }
            }
        }
        val antes = conLugar(ctx)
        aplicar(ctx)
        return Resultado(vistos, lados.size, (conLugar(ctx) - antes).coerceAtLeast(0))
    }

    private class Sidecar(val ms: Long, val lat: Double, val lon: Double, val origen: String, val etiqueta: String, val subida: Long)

    /**
     * {"title":"IMG_1.jpg","photoTakenTime":{"timestamp":"1590000000"},"creationTime":{…},"geoData":{"latitude":19.4,"longitude":-99.1},
     *  "googlePhotosOrigin":{"mobileUpload":{"deviceFolder":{"localFolderName":"Camera"},"deviceType":"ANDROID_PHONE"}}}
     */
    private fun leerSidecar(t: String): Pair<String, Sidecar>? = runCatching {
        if (!t.contains("photoTakenTime") || !t.contains("geoData")) return null
        val o = JSON.parseToJsonElement(t).jsonObject
        val ts = o["photoTakenTime"]?.jsonObject?.get("timestamp")?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: return null
        val sube = o["creationTime"]?.jsonObject?.get("timestamp")?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L
        fun geo(k: String): Pair<Double, Double>? = (o[k] as? JsonObject)?.let { g ->
            val la = g["latitude"]?.jsonPrimitive?.doubleOrNull; val lo = g["longitude"]?.jsonPrimitive?.doubleOrNull
            if (la != null && lo != null && valido(la, lo)) la to lo else null
        }
        val (la, lo) = geo("geoData") ?: geo("geoDataExif") ?: return null
        val titulo = o["title"]?.jsonPrimitive?.contentOrNull ?: return null
        val og = o["googlePhotosOrigin"] as? JsonObject
        val (origen, etiqueta) = when {
            og == null -> "propia" to "Google Fotos (origen desconocido)"
            og.containsKey("fromSharedAlbum") -> "compartida" to "álbum compartido"
            og.containsKey("fromPartnerSharing") -> "compartida" to "compartidas por tu pareja"
            og.containsKey("mobileUpload") -> {
                val m = og["mobileUpload"] as? JsonObject
                val tipo = m?.get("deviceType")?.jsonPrimitive?.contentOrNull.orEmpty()
                val carpeta = (m?.get("deviceFolder") as? JsonObject)?.get("localFolderName")?.jsonPrimitive?.contentOrNull.orEmpty()
                val t2 = when { tipo.startsWith("IOS") -> "iPhone"; tipo.startsWith("ANDROID") -> "Android"; tipo.isNotEmpty() -> tipo.lowercase(); else -> "teléfono" }
                "propia" to etiqueta(if (carpeta.isEmpty() || carpeta.equals("Camera", true) || carpeta.equals("Cámara", true)) "$t2 · cámara" else t2, carpeta.takeIf { it.isNotEmpty() && !it.equals("Camera", true) })
            }
            og.containsKey("webUpload") -> "propia" to "subidas desde la web"
            og.containsKey("driveDesktopUploader") || og.containsKey("desktopUploader") -> "propia" to "subidas desde la computadora"
            og.containsKey("composition") -> "propia" to "creaciones de Google Fotos"
            else -> "propia" to "Google Fotos"
        }
        titulo to Sidecar(ts * 1000, la, lo, origen, etiqueta, sube * 1000)
    }.getOrNull()

    // ------------------------------------------------------------------ aparatos, fotos raras y aplicar

    class Aparato(val nombre: String, val fotos: Int, val desde: Long, val hasta: Long, val imposibles: Int, val compartidas: Int)

    /** De dónde vienen tus fotos con lugar: aparato o carpeta, cuántas, entre qué fechas y cuántas con fecha imposible. */
    fun aparatos(): List<Aparato> {
        class Acum { var n = 0; var desde = Long.MAX_VALUE; var hasta = 0L; var malas = 0; var comp = 0 }
        val m = HashMap<String, Acum>()
        runCatching {
            Repo.db().recorrerFotos { f ->
                val a = m.getOrPut(f.aparato) { Acum() }
                a.n++; if (f.origen == "compartida") a.comp++
                if (fechaPosible(f.ms)) { a.desde = minOf(a.desde, f.ms); a.hasta = maxOf(a.hasta, f.ms) } else a.malas++
            }
        }
        return m.map { (k, a) -> Aparato(k, a.n, if (a.desde == Long.MAX_VALUE) 0 else a.desde, a.hasta, a.malas, a.comp) }.sortedByDescending { it.fotos }
    }

    /** Un grupo de fotos que te ponen en un lugar raro: un país lejano un día en que tu teléfono estaba en otra parte, o uno que casi no aparece. */
    class Rara(val clave: String, val pais: String, val fecha: String, val fotos: Int, val aparatos: List<String>, val motivo: String, val contradicha: Boolean)

    @Volatile private var rarasCache: Pair<String, List<Rara>>? = null

    /** El país donde vives: el de «Conocerte» o, si no lo llenaste, el que más aparece en tus fotos. */
    private fun casa(ctx: Context, porPais: Map<String, Set<String>>): String? =
        Repo.config.perfil.pais.takeIf { it.length == 2 } ?: porPais.maxByOrNull { it.value.size }?.key

    /** Fecha (día lógico) y país de cada foto que pasa los filtros de aparato y origen. */
    private fun elegibles(): List<Pair<Db.FotoCat, Long>> {
        val c = Repo.config
        val mios = c.fotos_aparatos.toSet(); val sinFecha = c.fotos_sin_fecha.toSet()
        val out = ArrayList<Pair<Db.FotoCat, Long>>()
        Repo.db().recorrerFotos { f ->
            if (mios.isNotEmpty() && f.aparato !in mios) return@recorrerFotos
            if (!c.fotos_compartidas && f.origen == "compartida") return@recorrerFotos
            val ms = if (f.aparato in sinFecha) f.msArchivo else f.ms
            if (!fechaPosible(ms)) return@recorrerFotos
            out += f to ms
        }
        return out
    }

    /**
     * Grupos de fotos raras. Se fija en los países distintos al tuyo, por día:
     * si ese día el teléfono registró dónde estabas y no era ahí → contradicha (casi seguro una foto que te mandaron);
     * si no hay registro y ese país aparece en muy pocos días de tus fotos → aislada (puede ser un viaje corto o una foto ajena).
     */
    fun raras(ctx: Context): List<Rara> {
        val c = Repo.config
        val firma = "${enCatalogo()}|${c.fotos_aparatos}|${c.fotos_sin_fecha}|${c.fotos_compartidas}|${c.perfil.pais}"
        rarasCache?.let { if (it.first == firma) return it.second }
        val fotos = elegibles()
        // País de cada foto (con una caché por celda de ~1 km para no repetir el cálculo).
        val celdas = HashMap<Long, String?>()
        val conPais = fotos.map { (f, ms) ->
            val k = ((f.lat * 100).roundToLong() shl 20) xor (f.lon * 100).roundToLong()
            Triple(f, ms, celdas.getOrPut(k) { Mundo.paisEn(ctx, f.lat, f.lon)?.codigo })
        }
        val porPais = HashMap<String, MutableSet<String>>()
        conPais.forEach { (_, ms, p) -> if (p != null) porPais.getOrPut(p) { HashSet() } += Fechas.diaDeMillis(ms) }
        val hogar = casa(ctx, porPais)
        val grupos = conPais.filter { it.third != null && it.third != hogar }.groupBy { it.third!! + "|" + Fechas.diaDeMillis(it.second) }
        val db = Repo.db()
        val out = ArrayList<Rara>()
        for ((k, g) in grupos) {
            val pais = k.substringBefore('|'); val fecha = k.substringAfter('|')
            val ini = Fechas.inicioDia(fecha); val fin = Fechas.finDia(fecha)
            val gps = runCatching { db.ubicacionesSinFotos(ini, fin) }.getOrDefault(emptyList())
            val paisesGps = gps.filterIndexed { i, _ -> i % maxOf(1, gps.size / 12) == 0 }.mapNotNull { Mundo.paisEn(ctx, it.lat, it.lon)?.codigo }.toSet()
            val aps = g.groupingBy { it.first.aparato }.eachCount().entries.sortedByDescending { it.value }.map { it.key }
            when {
                paisesGps.isNotEmpty() && pais !in paisesGps -> {
                    val donde = paisesGps.joinToString(", ") { Mundo.pais(ctx, it)?.nombre ?: it }
                    out += Rara(k, pais, fecha, g.size, aps, "ese día tu teléfono estaba en $donde", true)
                }
                paisesGps.isEmpty() && (porPais[pais]?.size ?: 0) <= 2 && g.size <= 25 ->
                    out += Rara(k, pais, fecha, g.size, aps, "es de los pocos días con fotos en ese país y no hay otro registro de dónde estabas", false)
            }
        }
        val lista = out.sortedWith(compareByDescending<Rara> { it.contradicha }.thenByDescending { it.fecha })
        rarasCache = firma to lista
        return lista
    }

    /**
     * Rehace las ubicaciones «foto» del mapa desde el catálogo, con lo que elegiste. Las fotos raras no cuentan hasta que
     * digas que sí estuviste; las que marcaste que no, nunca.
     */
    fun aplicar(ctx: Context): Int {
        val c = Repo.config
        val raras = runCatching { raras(ctx) }.getOrDefault(emptyList())
        val excluir = raras.filter { c.fotos_revisadas[it.clave] != true }.map { it.clave }.toSet() +
            c.fotos_revisadas.filterValues { !it }.keys
        val db = Repo.db()
        var n = 0
        val fotos = elegibles()
        db.enTransaccion {
            db.borrarUbicacionesDeFotos()
            for ((f, ms) in fotos) {
                if (excluir.isNotEmpty()) {
                    // Solo las de fuera de casa pueden ser raras; el país se calcula solo si hace falta.
                    val dia = Fechas.diaDeMillis(ms)
                    if (excluir.any { it.endsWith("|$dia") } && Mundo.paisEn(ctx, f.lat, f.lon)?.codigo?.let { "$it|$dia" in excluir } == true) continue
                }
                db.ponerUbicacion(ms, f.lat, f.lon, null, f.fuente); n++
            }
        }
        Mundo.invalidar()
        return n
    }
}
