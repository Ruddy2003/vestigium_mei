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

    /** Varias escrituras de una vez (mucho más rápido para miles de puntos). */
    fun enTransaccion(f: () -> Unit) {
        val w = writableDatabase
        w.beginTransaction()
        try { f(); w.setTransactionSuccessful() } finally { w.endTransaction() }
    }

    fun existeUbicacion(ms: Long): Boolean =
        readableDatabase.rawQuery("SELECT 1 FROM ubicaciones WHERE ms = ? LIMIT 1", arrayOf(ms.toString())).use { it.moveToFirst() }

    fun transaccion(bloque: () -> Unit) {
        val db = writableDatabase
        db.beginTransaction()
        try { bloque(); db.setTransactionSuccessful() } finally { db.endTransaction() }
    }
}
