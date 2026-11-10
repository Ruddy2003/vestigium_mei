package io.github.ruddy2003.vestigium.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Almacén local. Cada colección guarda documentos JSON (flexible para que el esquema crezca con los años)
 * y las ubicaciones, que son muchas, van en su propia tabla con columnas numéricas.
 */
class Db(ctx: Context) : SQLiteOpenHelper(ctx, "vestigium.db", null, 2) {
    companion object {
        /** Precisión prudente (metros) para los puntos que no la traen, como los importados de Google Maps (P-33). */
        const val PRECISION_SIN_DATO = 100.0
    }


    /**
     * Escritura con registro aparte (WAL, 9 nov 2026): un corte de luz o un cierre a mitad de una escritura ya no puede dejar
     * la base a medias, y leer mientras la tarea de fondo escribe no se bloquea.
     */
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.enableWriteAheadLogging()
    }

    /** Revisión rápida de la base (PRAGMA quick_check): «ok» si está sana; si no, lo que SQLite encontró. */
    fun revisar(): String = readableDatabase.rawQuery("PRAGMA quick_check", null).use { c ->
        val l = ArrayList<String>(); while (c.moveToNext() && l.size < 5) l += c.getString(0); l.joinToString("; ").ifBlank { "ok" }
    }

    /** Una copia cruda y coherente de la base (VACUUM INTO): la red por si algún día el JSON del respaldo no se pudiera leer. */
    fun copiaCruda(destino: java.io.File) {
        destino.delete()
        writableDatabase.execSQL("VACUUM INTO ?", arrayOf<Any>(destino.absolutePath))
    }

    /** Las colecciones que hay guardadas. */
    fun colecciones(): List<String> = readableDatabase.rawQuery("SELECT DISTINCT col FROM docs", null).use { c ->
        val l = ArrayList<String>(); while (c.moveToNext()) l += c.getString(0); l
    }

    /** Cada documento de [col] con su id y su fecha. */
    fun filas(col: String, f: (id: String, fecha: String?, json: String) -> Unit) {
        readableDatabase.rawQuery("SELECT id, fecha, json FROM docs WHERE col = ?", arrayOf(col)).use { c ->
            while (c.moveToNext()) f(c.getString(0), if (c.isNull(1)) null else c.getString(1), c.getString(2))
        }
    }

    /** La fecha con que está guardado un documento. */
    fun fechaDe(col: String, id: String): String? =
        readableDatabase.rawQuery("SELECT fecha FROM docs WHERE col = ? AND id = ?", arrayOf(col, id)).use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getString(0) else null }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE docs (col TEXT NOT NULL, id TEXT NOT NULL, fecha TEXT, json TEXT NOT NULL, editado INTEGER NOT NULL, PRIMARY KEY(col, id))")
        db.execSQL("CREATE INDEX docs_fecha ON docs(col, fecha)")
        db.execSQL("CREATE TABLE ubicaciones (ms INTEGER NOT NULL, lat REAL NOT NULL, lon REAL NOT NULL, precision REAL, fuente TEXT)")
        db.execSQL("CREATE INDEX ubic_ms ON ubicaciones(ms)")
        crearFotos(db)
    }

    /**
     * Catálogo de fotos con lugar: de dónde viene cada una (aparato, carpeta u origen en Google Fotos), su fecha y la del archivo.
     * Las ubicaciones «foto» se rehacen desde aquí cada vez que cambias qué aparatos son tuyos o qué fotos raras cuentan.
     */
    private fun crearFotos(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS fotos (clave TEXT PRIMARY KEY, ms INTEGER NOT NULL, lat REAL NOT NULL, lon REAL NOT NULL, aparato TEXT NOT NULL, origen TEXT NOT NULL, ms_archivo INTEGER NOT NULL, fuente TEXT NOT NULL)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            crearFotos(db)
            // Las fotos que ya estaban en el mapa pasan al catálogo sin saber de qué aparato son (se completa al volver a revisarlas).
            db.execSQL("INSERT OR IGNORE INTO fotos (clave, ms, lat, lon, aparato, origen, ms_archivo, fuente) " +
                "SELECT (ms / 1000) || '_' || CAST(ROUND(lat * 10000) AS INTEGER) || '_' || CAST(ROUND(lon * 10000) AS INTEGER), ms, lat, lon, '', 'antes', 0, fuente FROM ubicaciones WHERE fuente IN ('foto', 'foto_takeout')")
        }
    }

    class FotoCat(val clave: String, val ms: Long, val lat: Double, val lon: Double, val aparato: String, val origen: String, val msArchivo: Long, val fuente: String)

    /** Guarda una foto en el catálogo; si ya estaba, se queda con el dato más completo (aparato y origen conocidos). */
    fun ponerFoto(f: FotoCat) {
        writableDatabase.execSQL("INSERT INTO fotos (clave, ms, lat, lon, aparato, origen, ms_archivo, fuente) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON CONFLICT(clave) DO UPDATE SET aparato = CASE WHEN excluded.aparato != '' THEN excluded.aparato ELSE aparato END, " +
            "origen = CASE WHEN excluded.origen != 'antes' THEN excluded.origen ELSE origen END, ms_archivo = MAX(ms_archivo, excluded.ms_archivo)",
            arrayOf<Any>(f.clave, f.ms, f.lat, f.lon, f.aparato, f.origen, f.msArchivo, f.fuente))
    }

    fun recorrerFotos(f: (FotoCat) -> Unit) {
        readableDatabase.rawQuery("SELECT clave, ms, lat, lon, aparato, origen, ms_archivo, fuente FROM fotos ORDER BY ms", null).use { c ->
            while (c.moveToNext()) f(FotoCat(c.getString(0), c.getLong(1), c.getDouble(2), c.getDouble(3), c.getString(4), c.getString(5), c.getLong(6), c.getString(7)))
        }
    }

    fun contarFotos(): Long = readableDatabase.rawQuery("SELECT COUNT(*) FROM fotos", null).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    fun borrarUbicacionesDeFotos() { writableDatabase.delete("ubicaciones", "fuente IN ('foto', 'foto_takeout')", null) }

    fun contarUbicacionesDeFotos(): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM ubicaciones WHERE fuente IN ('foto', 'foto_takeout')", null).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    /** Puntos que no vienen de fotos (el teléfono, Google), entre dos instantes. */
    fun ubicacionesSinFotos(desde: Long, hasta: Long): List<Punto> =
        ubicaciones(desde, hasta).filter { it.fuente == null || !it.fuente.startsWith("foto") }

    fun poner(col: String, id: String, fecha: String?, json: String) {
        val v = ContentValues().apply {
            put("col", col); put("id", id); put("fecha", fecha); put("json", json); put("editado", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict("docs", null, v, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun borrar(col: String, id: String) {
        writableDatabase.delete("docs", "col = ? AND id = ?", arrayOf(col, id))
    }

    fun todos(col: String): List<String> {
        val out = ArrayList<String>()
        readableDatabase.rawQuery("SELECT json FROM docs WHERE col = ?", arrayOf(col)).use { c ->
            while (c.moveToNext()) out.add(c.getString(0))
        }
        return out
    }

    fun porFecha(col: String, desde: String, hasta: String): List<String> {
        val out = ArrayList<String>()
        readableDatabase.rawQuery("SELECT json FROM docs WHERE col = ? AND fecha >= ? AND fecha <= ? ORDER BY fecha", arrayOf(col, desde, hasta)).use { c ->
            while (c.moveToNext()) out.add(c.getString(0))
        }
        return out
    }

    fun borrarRango(col: String, desde: String, hasta: String) {
        writableDatabase.delete("docs", "col = ? AND fecha >= ? AND fecha <= ?", arrayOf(col, desde, hasta))
    }

    /** Borra de [col] los documentos de exactamente esas fechas (no lo que quede entre ellas). */
    fun borrarFechas(col: String, fechas: Collection<String>) {
        val w = writableDatabase
        fechas.forEach { w.delete("docs", "col = ? AND fecha = ?", arrayOf(col, it)) }
    }

    /** Las fechas en que [col] tiene algún documento. */
    fun fechas(col: String): Set<String> = readableDatabase.rawQuery("SELECT DISTINCT fecha FROM docs WHERE col = ? AND fecha IS NOT NULL", arrayOf(col)).use { c ->
        val l = HashSet<String>(); while (c.moveToNext()) l += c.getString(0); l
    }

    fun contar(col: String): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM docs WHERE col = ?", arrayOf(col)).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    fun contarPrefijo(col: String, prefijo: String): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM docs WHERE col = ? AND id LIKE ?", arrayOf(col, "$prefijo%")).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    fun uno(col: String, id: String): String? =
        readableDatabase.rawQuery("SELECT json FROM docs WHERE col = ? AND id = ?", arrayOf(col, id)).use { c ->
            if (c.moveToFirst()) c.getString(0) else null
        }

    fun ponerUbicacion(ms: Long, lat: Double, lon: Double, precision: Float?, fuente: String) {
        val v = ContentValues().apply {
            put("ms", ms); put("lat", lat); put("lon", lon); put("precision", precision?.toDouble()); put("fuente", fuente)
        }
        writableDatabase.insert("ubicaciones", null, v)
    }

    /** Las fuentes que toma el propio teléfono (muestra de fondo, geocerca, momento): las que no deben repetir un punto. */
    private val FUENTES_PROPIAS = "('app', 'geocerca', 'momento')"

    /**
     * Una lectura del propio teléfono: se guarda solo si ese mismo punto (misma hora, latitud y longitud) no está ya. Cuando
     * la lectura tarda, Android devuelve la última conocida, la misma una y otra vez: antes quedaba repetida en cada pulso
     * (H-3-8), y una geocerca repetía el punto de la muestra. Devuelve si lo guardó.
     */
    fun ponerMuestra(ms: Long, lat: Double, lon: Double, precision: Float?, fuente: String): Boolean {
        val ya = readableDatabase.rawQuery(
            "SELECT 1 FROM ubicaciones WHERE ms = ? AND ABS(lat - ?) < 1e-7 AND ABS(lon - ?) < 1e-7 LIMIT 1",
            arrayOf(ms.toString(), lat.toString(), lon.toString())
        ).use { it.moveToFirst() }
        if (ya) return false
        ponerUbicacion(ms, lat, lon, precision, fuente)
        return true
    }

    /**
     * Una sola vez (esquema 19): quita los puntos repetidos del propio teléfono que dejó la muestra de fondo (mismo punto,
     * misma hora); se queda el primero de cada uno. No toca los importados ni los de fotos. Devuelve cuántos quitó.
     */
    fun quitarMuestrasRepetidas(): Int = writableDatabase.delete(
        "ubicaciones",
        "fuente IN $FUENTES_PROPIAS AND rowid NOT IN (SELECT MIN(rowid) FROM ubicaciones WHERE fuente IN $FUENTES_PROPIAS GROUP BY ms, lat, lon)",
        null
    )

    data class Punto(val ms: Long, val lat: Double, val lon: Double, val precision: Double?, val fuente: String?)

    fun ubicaciones(desde: Long = 0, hasta: Long = Long.MAX_VALUE): List<Punto> {
        val out = ArrayList<Punto>()
        readableDatabase.rawQuery(
            "SELECT ms, lat, lon, precision, fuente FROM ubicaciones WHERE ms >= ? AND ms < ? ORDER BY ms",
            arrayOf(desde.toString(), hasta.toString())
        ).use { c ->
            while (c.moveToNext()) out.add(Punto(c.getLong(0), c.getDouble(1), c.getDouble(2), if (c.isNull(3)) null else c.getDouble(3), c.getString(4)))
        }
        return out
    }

    /** Todas las ubicaciones en orden, una por una, sin juntarlas en una lista (para el respaldo y la exportación). */
    fun recorrerPuntos(f: (Punto) -> Unit) {
        readableDatabase.rawQuery("SELECT ms, lat, lon, precision, fuente FROM ubicaciones ORDER BY ms", null).use { c ->
            while (c.moveToNext()) f(Punto(c.getLong(0), c.getDouble(1), c.getDouble(2), if (c.isNull(3)) null else c.getDouble(3), c.getString(4)))
        }
    }

    /**
     * Recorre las ubicaciones en orden sin crear un objeto por punto (para años de historial). Un punto sin precisión (importado
     * de Maps) cuenta como de [PRECISION_SIN_DATO] metros, no de 0 (P-33).
     */
    fun recorrerUbicaciones(desde: Long, hasta: Long = Long.MAX_VALUE, f: (ms: Long, lat: Double, lon: Double, precision: Double) -> Unit) {
        readableDatabase.rawQuery(
            "SELECT ms, lat, lon, COALESCE(precision, $PRECISION_SIN_DATO) FROM ubicaciones WHERE ms >= ? AND ms < ? ORDER BY ms",
            arrayOf(desde.toString(), hasta.toString())
        ).use { c -> while (c.moveToNext()) f(c.getLong(0), c.getDouble(1), c.getDouble(2), c.getDouble(3)) }
    }

    fun ultimaUbicacion(): Long? =
        readableDatabase.rawQuery("SELECT MAX(ms) FROM ubicaciones", null).use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else null }

    fun contarUbicaciones(): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM ubicaciones", null).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    /**
     * Una seña barata de las ubicaciones guardadas (cuántas son, la última fila y la suma de horas y coordenadas): si no
     * cambia, lo calculado con ellas sigue valiendo. Se saca en la propia base, sin traer los puntos a memoria.
     */
    fun huellaUbicaciones(): String =
        readableDatabase.rawQuery("SELECT COUNT(*), MAX(rowid), TOTAL(ms), TOTAL(lat), TOTAL(lon) FROM ubicaciones", null).use { c ->
            if (c.moveToFirst()) (0..4).joinToString("/") { if (c.isNull(it)) "" else c.getString(it) } else ""
        }

    /** Otro nombre de [transaccion] (lo usan las fotos con lugar): hay una sola manera de agrupar escrituras. */
    fun enTransaccion(f: () -> Unit) = transaccion(f)

    fun existeUbicacion(ms: Long): Boolean =
        readableDatabase.rawQuery("SELECT 1 FROM ubicaciones WHERE ms = ? LIMIT 1", arrayOf(ms.toString())).use { it.moveToFirst() }

    /** Varias escrituras de una vez: o entran todas o ninguna (y es mucho más rápido para miles de documentos o puntos). */
    fun transaccion(bloque: () -> Unit) {
        val db = writableDatabase
        db.beginTransaction()
        try { bloque(); db.setTransactionSuccessful() } finally { db.endTransaction() }
    }
}
