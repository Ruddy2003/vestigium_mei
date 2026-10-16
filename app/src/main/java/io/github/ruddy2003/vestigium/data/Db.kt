package io.github.ruddy2003.vestigium.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Almacén local. Cada colección guarda documentos JSON (flexible para que el esquema crezca con los años)
 * y las ubicaciones, que son muchas, van en su propia tabla con columnas numéricas.
 */
class Db(ctx: Context) : SQLiteOpenHelper(ctx, "vestigium.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE docs (col TEXT NOT NULL, id TEXT NOT NULL, fecha TEXT, json TEXT NOT NULL, editado INTEGER NOT NULL, PRIMARY KEY(col, id))")
        db.execSQL("CREATE INDEX docs_fecha ON docs(col, fecha)")
        db.execSQL("CREATE TABLE ubicaciones (ms INTEGER NOT NULL, lat REAL NOT NULL, lon REAL NOT NULL, precision REAL, fuente TEXT)")
        db.execSQL("CREATE INDEX ubic_ms ON ubicaciones(ms)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

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

    fun contar(col: String): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM docs WHERE col = ?", arrayOf(col)).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

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

    fun contarUbicaciones(): Long =
        readableDatabase.rawQuery("SELECT COUNT(*) FROM ubicaciones", null).use { c -> if (c.moveToFirst()) c.getLong(0) else 0 }

    fun existeUbicacion(ms: Long): Boolean =
        readableDatabase.rawQuery("SELECT 1 FROM ubicaciones WHERE ms = ? LIMIT 1", arrayOf(ms.toString())).use { it.moveToFirst() }

    fun transaccion(bloque: () -> Unit) {
        val db = writableDatabase
        db.beginTransaction()
        try { bloque(); db.setTransactionSuccessful() } finally { db.endTransaction() }
    }
}
