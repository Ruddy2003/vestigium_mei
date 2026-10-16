package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import io.github.ruddy2003.vestigium.ciencia.Escalas
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Serializable
data class Respaldo(
    val exportado: String,
    val schema_v: Int,
    val dias: List<Dia> = emptyList(),
    val momentos: List<Momento> = emptyList(),
    val gastos: List<Gasto> = emptyList(),
    val respuestas: List<Respuesta> = emptyList(),
    val reacciones: List<Reaccion> = emptyList(),
    val lugares: List<Lugar> = emptyList(),
    val visitas: List<Visita> = emptyList(),
    val salud: List<SaludDia> = emptyList(),
    val pantalla: List<PantallaDia> = emptyList(),
    val cartas: List<Carta> = emptyList(),
    val experimentos: List<Experimento> = emptyList(),
    val config: Config = Config(),
)

/** CSV listos para R: fechas AAAA-MM-DD, booleanos como 0/1, listas separadas por «|», vacío = dato faltante. */
object Exportar {
    private fun celda(v: Any?): String {
        if (v == null) return ""
        val s = when (v) {
            is Boolean -> if (v) "1" else "0"
            is Double -> if (v.isNaN()) "" else v.toString()
            is List<*> -> v.joinToString("|")
            else -> v.toString()
        }
        return if (s.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) "\"" + s.replace("\"", "\"\"") + "\"" else s
    }

    fun csv(filas: List<Map<String, Any?>>): String {
        val cols = LinkedHashSet<String>()
        filas.forEach { cols.addAll(it.keys) }
        val sb = StringBuilder("﻿")
        sb.append(cols.joinToString(",")).append('\n')
        filas.forEach { f -> sb.append(cols.joinToString(",") { celda(f[it]) }).append('\n') }
        return sb.toString()
    }

    fun filasDias(): List<Map<String, Any?>> {
        val eq = Repo.equivalencias()
        return Repo.dias.values.sortedBy { it.fecha }.map { d ->
            val r = linkedMapOf<String, Any?>(
                "fecha" to d.fecha, "schema_v" to d.schema_v, "origen" to d.origen, "retroactivo" to d.retroactivo,
                "dia_dificil" to d.dia_dificil, "ts_creado" to d.ts_creado, "ts_editado" to d.ts_editado,
                "valencia" to d.valencia, "activacion" to d.activacion, "dolor" to d.dolor,
            )
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = d.ctx[it.id] == true }
            r["hitos"] = d.hitos
            r["flow"] = d.flow; r["sdt_necesidad"] = d.sdt_necesidad; r["apreciacion"] = d.apreciacion
            r["sueno_recordado"] = d.sueno_recordado; r["sueno_valencia"] = d.sueno_valencia; r["sueno_lucidez"] = d.sueno_lucidez
            r["sueno_vividez"] = d.sueno_vividez; r["sueno_tipo"] = d.sueno_tipo; r["sueno_temas"] = d.sueno_temas
            r["sueno_calidad"] = d.sueno_calidad; r["sueno_texto"] = d.sueno_texto
            r["mejor_momento"] = d.mejor_momento; r["peor_momento"] = d.peor_momento
            r["impulso"] = d.impulso; r["recaida"] = d.recaida; r["detonantes"] = d.detonantes
            r["medicamentos"] = d.medicamentos; r["prediccion_manana"] = d.prediccion_manana
            Repo.config.habitos.forEach { r["hab_${it.id}"] = d.habitos[it.id] == true }
            r["personas"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.distinct()
            r["temas"] = Texto.temas(d.nota).map { Texto.canon(it, eq) }.distinct()
            r["palabras"] = Texto.palabras(d.nota)
            r["nota"] = d.nota
            r
        }
    }

    fun filasMomentos() = Repo.momentos.map {
        linkedMapOf<String, Any?>("id" to it.id, "ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "tipo" to it.tipo,
            "intensidad" to it.intensidad, "valencia" to it.valencia, "activacion" to it.activacion, "texto" to it.texto,
            "personas" to Texto.personas(it.texto), "temas" to Texto.temas(it.texto),
            "lat" to it.lat, "lon" to it.lon, "precision_m" to it.precision_m, "lugar" to Repo.lugares.firstOrNull { l -> l.id == it.lugar }?.nombre,
            "origen" to it.origen, "aviso_ts" to it.aviso_ts,
            "latencia_min" to it.aviso_ts?.let { a -> (Fechas.millis(it.ts) - Fechas.millis(a)) / 60000.0 })
    }

    fun filasGastos() = Repo.gastos.map {
        linkedMapOf<String, Any?>("id" to it.id, "fecha" to it.fecha, "hora" to it.hora, "monto" to it.monto, "categoria" to it.categoria,
            "imprevisto" to it.imprevisto, "nota" to it.nota, "lat" to it.lat, "lon" to it.lon)
    }

    fun filasEscalas() = Repo.respuestas.map { r ->
        val m = linkedMapOf<String, Any?>("id" to r.id, "fecha" to r.fecha, "ts" to r.ts, "instrumento" to r.instrumento)
        r.items.toSortedMap().forEach { (k, v) -> m["item_$k"] = v }
        r.puntajes.forEach { (k, v) -> m["puntaje_$k"] = v }
        m
    }

    fun filasReaccion() = Repo.reacciones.map {
        linkedMapOf<String, Any?>("id" to it.id, "ts" to it.ts, "fecha" to it.fecha, "hora" to Fechas.horaDe(it.ts), "media_ms" to it.media_ms,
            "mediana_ms" to it.mediana_ms, "lapsos" to it.lapsos, "anticipaciones" to it.anticipaciones, "ensayos_ms" to it.ensayos_ms)
    }

    fun filasSalud() = Repo.salud.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "sueno_min" to it.sueno_min, "sueno_profundo_min" to it.sueno_profundo_min,
            "sueno_rem_min" to it.sueno_rem_min, "sueno_ligero_min" to it.sueno_ligero_min, "sueno_despierto_min" to it.sueno_despierto_min,
            "sueno_inicio" to it.sueno_inicio, "sueno_fin" to it.sueno_fin, "fc_media" to it.fc_media, "fc_min" to it.fc_min, "fc_max" to it.fc_max,
            "fc_reposo" to it.fc_reposo, "hrv_rmssd" to it.hrv_rmssd, "pasos" to it.pasos, "spo2" to it.spo2)
    }

    fun filasPantalla() = Repo.pantalla.values.sortedBy { it.fecha }.map {
        linkedMapOf<String, Any?>("fecha" to it.fecha, "total_min" to it.total_min, "desbloqueos" to it.desbloqueos,
            "primer_uso" to it.primer_uso, "ultimo_uso" to it.ultimo_uso,
            "top_apps" to it.por_app_min.entries.take(10).map { e -> "${e.key}:${e.value}" })
    }

    fun filasVisitas() = Repo.visitas.map { v ->
        val l = Repo.lugares.firstOrNull { it.id == v.lugar }
        linkedMapOf<String, Any?>("lugar" to l?.nombre, "tipo" to l?.tipo, "entrada" to v.entrada, "salida" to v.salida,
            "minutos" to v.salida?.let { (Fechas.millis(it) - Fechas.millis(v.entrada)) / 60000 })
    }

    fun csvUbicaciones(): String {
        val sb = StringBuilder("﻿ts,lat,lon,precision_m,fuente\n")
        Repo.db().ubicaciones().forEach { sb.append(Fechas.isoDeMillis(it.ms)).append(',').append(it.lat).append(',').append(it.lon).append(',')
            .append(it.precision ?: "").append(',').append(it.fuente ?: "").append('\n') }
        return sb.toString()
    }

    fun respaldoJson(): String = JSON.encodeToString(Respaldo.serializer(), Respaldo(
        exportado = Fechas.ahoraIso(), schema_v = SCHEMA_V, dias = Repo.dias.values.sortedBy { it.fecha }, momentos = Repo.momentos,
        gastos = Repo.gastos, respuestas = Repo.respuestas, reacciones = Repo.reacciones, lugares = Repo.lugares, visitas = Repo.visitas,
        salud = Repo.salud.values.toList(), pantalla = Repo.pantalla.values.toList(), cartas = Repo.cartas, experimentos = Repo.experimentos,
        config = Repo.config.copy(disparos = emptyMap(), plan_muestreo = emptyMap())))

    fun archivos(): List<Pair<String, String>> = listOf(
        "vm_dias.csv" to csv(filasDias()),
        "vm_momentos.csv" to csv(filasMomentos()),
        "vm_gastos.csv" to csv(filasGastos()),
        "vm_escalas.csv" to csv(filasEscalas()),
        "vm_reaccion.csv" to csv(filasReaccion()),
        "vm_salud.csv" to csv(filasSalud()),
        "vm_pantalla.csv" to csv(filasPantalla()),
        "vm_visitas.csv" to csv(filasVisitas()),
        "vm_ubicaciones.csv" to csvUbicaciones(),
        "vm_respaldo.json" to respaldoJson(),
        "LEEME.txt" to leeme(),
    )

    private fun leeme() = """
        Vestigium Mei · exportación ${Fechas.ahoraIso()}
        Esquema de datos versión $SCHEMA_V.

        En R:
          library(tidyverse)
          dias <- read_csv("vm_dias.csv")
          momentos <- read_csv("vm_momentos.csv")
          salud <- read_csv("vm_salud.csv")
          panel <- dias |> left_join(salud, by = "fecha")

        Convenciones: fechas AAAA-MM-DD en hora local; los días empiezan a las 5:00;
        booleanos 0/1; listas separadas por «|»; celda vacía = dato que no existe (nunca se rellenó con promedios).
        Escalas: ${Escalas.TODAS.joinToString("; ") { it.nombre + " — " + it.adaptacion }}
    """.trimIndent()

    fun zip(ctx: Context): File {
        val dir = File(ctx.cacheDir, "export").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val f = File(dir, "vestigium_${Fechas.hoyCalendario()}.zip")
        ZipOutputStream(f.outputStream()).use { z ->
            archivos().forEach { (nombre, cont) -> z.putNextEntry(ZipEntry(nombre)); z.write(cont.toByteArray()); z.closeEntry() }
        }
        return f
    }

    fun compartir(ctx: Context) {
        val f = java.io.File(java.io.File(ctx.cacheDir, "export"), "vestigium_${Fechas.hoyCalendario()}.zip").takeIf { it.exists() } ?: zip(ctx)
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".archivos", f)
        val i = Intent(Intent.ACTION_SEND).setType("application/zip").putExtra(Intent.EXTRA_STREAM, uri)
            .putExtra(Intent.EXTRA_SUBJECT, f.name).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(i, "Guardar o enviar los datos").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Escribe todos los archivos en la carpeta elegida por el usuario (se reemplazan cada día). */
    fun respaldoAutomatico(ctx: Context): Boolean {
        val uriTxt = Repo.config.carpeta_respaldo ?: return false
        val dir = DocumentFile.fromTreeUri(ctx, Uri.parse(uriTxt)) ?: return false
        if (!dir.canWrite()) return false
        archivos().forEach { (nombre, cont) ->
            val mime = when { nombre.endsWith(".csv") -> "text/csv"; nombre.endsWith(".json") -> "application/json"; else -> "text/plain" }
            val f = dir.findFile(nombre) ?: dir.createFile(mime, nombre) ?: return@forEach
            ctx.contentResolver.openOutputStream(f.uri, "wt")?.use { it.write(cont.toByteArray()) }
        }
        return true
    }

}
