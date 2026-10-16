package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.net.Uri
import android.util.JsonReader
import android.util.JsonToken
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.InputStreamReader
import java.time.OffsetDateTime

data class ResultadoImport(val mensaje: String)

object Importar {

    private fun JsonElement?.str(): String? = (this as? JsonPrimitive)?.takeIf { it !is JsonNull }?.content?.takeIf { it.isNotEmpty() && it != "null" }
    private fun JsonElement?.int(): Int? = (this as? JsonPrimitive)?.let { it.intOrNull ?: it.doubleOrNull?.toInt() ?: it.content.toIntOrNull() }
    private fun JsonElement?.bool(): Boolean = (this as? JsonPrimitive)?.let { it.booleanOrNull ?: (it.content == "1") } ?: false
    private fun JsonElement?.lista(): List<String> = when (this) {
        is JsonArray -> this.mapNotNull { it.str() }
        is JsonPrimitive -> this.str()?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        else -> emptyList()
    }

    /** Respaldo de esta app, o el JSON exportado por la versión web de Vestigium Mei. */
    fun respaldo(ctx: Context, uri: Uri): ResultadoImport {
        val texto = ctx.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) } ?: return ResultadoImport("No pude leer el archivo.")
        val raiz = runCatching { JSON.parseToJsonElement(texto).jsonObject }.getOrNull() ?: return ResultadoImport("El archivo no es un JSON válido.")
        // Respaldo propio
        if (raiz.containsKey("momentos") || raiz.containsKey("respuestas")) {
            val r = JSON.decodeFromString(Respaldo.serializer(), texto)
            val n = Repo.importarDias(r.dias, sobrescribir = false)
            r.momentos.filter { m -> Repo.momentos.none { it.id == m.id } }.forEach { Repo.guardarMomento(it) }
            Repo.importarGastos(r.gastos)
            r.respuestas.forEach { Repo.guardarRespuesta(it) }
            r.reacciones.forEach { rr -> if (Repo.reacciones.none { it.id == rr.id }) Repo.guardarReaccion(rr) }
            r.lugares.forEach { Repo.guardarLugar(it) }
            r.salud.forEach { Repo.guardarSalud(it) }
            r.pantalla.forEach { Repo.guardarPantalla(it) }
            r.cartas.forEach { Repo.guardarCarta(it) }
            r.experimentos.forEach { Repo.guardarExperimento(it) }
            r.entorno.forEach { Repo.guardarEntorno(it) }
            r.fotos.forEach { Repo.guardarFotos(it) }
            r.agenda.forEach { Repo.guardarAgenda(it) }
            r.sesiones.forEach { Repo.guardarSesion(it) }
            r.ascensos.forEach { a -> if (Repo.ascensos.none { it.id == a.id }) Repo.guardarAscenso(a) }
            r.obras.forEach { o -> if (Repo.obras.none { it.id == o.id }) Repo.guardarObra(o) }
            r.escuchas.forEach { Repo.guardarEscucha(it) }
            Repo.importarSensores(r.notifs, r.actividad)
            Repo.cambiarConfig { c -> c.copy(habitos = (c.habitos + r.config.habitos).distinctBy { it.id }, equivalencias = r.config.equivalencias + c.equivalencias) }
            return ResultadoImport("Respaldo importado: $n días nuevos, ${r.momentos.size} momentos y ${r.gastos.size} gastos revisados.")
        }
        // Versión web
        val dias = raiz["dias"]?.jsonArray?.mapNotNull { e -> runCatching { diaDesdeWeb(e.jsonObject) }.getOrNull() } ?: emptyList()
        val n = Repo.importarDias(dias, sobrescribir = false)
        val gastos = raiz["gastos_mes"]?.jsonArray?.flatMap { mes ->
            mes.jsonObject["items"]?.jsonArray?.mapNotNull { it as? JsonObject }?.map { g ->
                Gasto(id = g["id"].str() ?: Repo.nuevoId(), fecha = g["fecha"].str() ?: "", hora = g["hora"].str() ?: "",
                    monto = (g["monto"] as? JsonPrimitive)?.doubleOrNull ?: 0.0, categoria = g["categoria"].str() ?: "Otro",
                    imprevisto = g["imprevisto"].bool(), nota = g["nota"].str() ?: "")
            } ?: emptyList()
        }?.filter { it.fecha.isNotEmpty() } ?: emptyList()
        Repo.importarGastos(gastos)
        val habitos = raiz["habitos"]?.jsonArray?.mapNotNull { it as? JsonObject }?.map {
            Habito(id = it["id"].str() ?: Texto.slug(it["nombre"].str() ?: "h"), nombre = it["nombre"].str() ?: "Hábito", activo = (it["activo"] as? JsonPrimitive)?.booleanOrNull ?: true)
        } ?: emptyList()
        val eq = (raiz["equivalencias"] as? JsonObject)?.mapNotNull { (k, v) -> v.str()?.let { k to it } }?.toMap() ?: emptyMap()
        Repo.cambiarConfig { c -> c.copy(habitos = (c.habitos + habitos).distinctBy { it.id }, equivalencias = eq + c.equivalencias) }
        return ResultadoImport("Importé $n días nuevos y ${gastos.size} gastos de la versión web. Los días que ya existían no se tocaron.")
    }

    private fun diaDesdeWeb(o: JsonObject): Dia {
        val fecha = o["fecha"].str()!!.take(10)
        val habitos = (o["habitos"] as? JsonObject)?.mapValues { it.value.bool() }?.filterValues { it } ?: emptyMap()
        return Dia(
            fecha = fecha, origen = o["origen"].str() ?: "web",
            valencia = o["valencia"].int(), activacion = o["activacion"].int(), dolor = o["dolor"].int(),
            ctx = Catalogo.CONTEXTOS.associate { it.id to o["ctx_${it.id}"].bool() }.filterValues { it },
            hitos = o["hitos"].lista(), flow = o["flow"].str(), sdt_necesidad = o["sdt_necesidad"].str(), apreciacion = o["apreciacion"].str(),
            sueno_recordado = o["sueno_recordado"].bool(), sueno_valencia = o["sueno_valencia"].int(),
            sueno_lucidez = o["sueno_lucidez"].str(), sueno_vividez = o["sueno_vividez"].str(), sueno_tipo = o["sueno_tipo"].str(),
            sueno_temas = o["sueno_temas"].lista(), sueno_texto = o["sueno_texto"].str() ?: "",
            habitos = habitos, nota = o["nota"].str() ?: "", ts_creado = o["ts_creado"].str(), ts_editado = o["ts_editado"].str(),
        )
    }

    // ---------- Obsidian ----------
    fun obsidian(ctx: Context, uris: List<Uri>): ResultadoImport {
        val dias = ArrayList<Dia>(); val fallas = ArrayList<String>()
        for (u in uris) {
            val nombre = u.lastPathSegment ?: ""
            val txt = runCatching { ctx.contentResolver.openInputStream(u)?.use { it.readBytes().toString(Charsets.UTF_8) } }.getOrNull()
            val d = txt?.let { runCatching { diaDesdeMd(it, nombre) }.getOrNull() }
            if (d != null) dias.add(d) else fallas.add(nombre.substringAfterLast('/'))
        }
        val n = Repo.importarDias(dias, sobrescribir = false)
        return ResultadoImport("Leí ${dias.size} nota(s); importé $n días nuevos." + if (fallas.isNotEmpty()) " No pude leer: ${fallas.joinToString(", ")}." else "")
    }

    /** YAML mínimo del encabezado de Obsidian: clave: valor, listas con guiones, bloques | y comillas. */
    fun yaml(txt: String): Map<String, Any?> {
        val out = LinkedHashMap<String, Any?>()
        val lineas = txt.lines()
        var i = 0
        fun limpiar(v: String): String {
            val t = v.trim()
            return when {
                t.length >= 2 && t.startsWith("\"") && t.endsWith("\"") -> t.substring(1, t.length - 1).replace("\\n", "\n").replace("\\\"", "\"")
                t.length >= 2 && t.startsWith("'") && t.endsWith("'") -> t.substring(1, t.length - 1).replace("''", "'")
                else -> t
            }
        }
        while (i < lineas.size) {
            val l = lineas[i]
            val m = Regex("^([A-Za-z0-9_]+):\\s*(.*)$").find(l)
            if (m == null) { i++; continue }
            val k = m.groupValues[1]; val v = m.groupValues[2]
            when {
                v.startsWith("|") || v.startsWith(">") -> {
                    val sb = ArrayList<String>(); i++
                    while (i < lineas.size && (lineas[i].startsWith(" ") || lineas[i].isBlank())) { sb.add(lineas[i].trim()); i++ }
                    out[k] = sb.joinToString(if (v.startsWith(">")) " " else "\n").trim(); continue
                }
                v.startsWith("[") && v.endsWith("]") -> out[k] = v.substring(1, v.length - 1).split(",").map { limpiar(it) }.filter { it.isNotEmpty() }
                v.isEmpty() -> {
                    val items = ArrayList<String>(); var j = i + 1
                    while (j < lineas.size && Regex("^\\s*-\\s+").containsMatchIn(lineas[j])) { items.add(limpiar(lineas[j].replace(Regex("^\\s*-\\s+"), ""))); j++ }
                    if (items.isNotEmpty()) { out[k] = items; i = j; continue } else out[k] = null
                }
                else -> out[k] = limpiar(v)
            }
            i++
        }
        return out
    }

    private fun diaDesdeMd(txt: String, nombre: String): Dia? {
        val m = Regex("^\\uFEFF?---\\r?\\n([\\s\\S]*?)\\r?\\n---\\r?\\n?([\\s\\S]*)$").find(txt)
        val fm = m?.let { yaml(it.groupValues[1]) } ?: emptyMap()
        val cuerpo = m?.groupValues?.get(2) ?: txt
        val fecha = (fm["fecha"] as? String)?.takeIf { Regex("^\\d{4}-\\d{2}-\\d{2}").containsMatchIn(it) }?.take(10)
            ?: Regex("\\d{4}-\\d{2}-\\d{2}").find(nombre)?.value ?: return null
        fun s(k: String) = (fm[k] as? String)?.takeIf { it.isNotBlank() && it != "null" }
        fun n(k: String) = s(k)?.toDoubleOrNull()?.toInt()
        fun b(k: String) = s(k)?.let { it == "true" || it == "1" } ?: false
        fun l(k: String): List<String> = when (val v = fm[k]) { is List<*> -> v.mapNotNull { (it as? String)?.trim()?.lowercase()?.replace(Regex("\\s+"), "_") }.filter { it.isNotEmpty() }; is String -> v.split(",").map { it.trim() }.filter { it.isNotEmpty() }; else -> emptyList() }
        var nota = s("nota") ?: ""
        if (nota.isBlank()) nota = cuerpo.lines().filter { ln -> !Regex("^\\s*(>|#{1,6}\\s|```)").containsMatchIn(ln) && !ln.contains("INPUT[") }.joinToString("\n").trim()
        return Dia(
            fecha = fecha, origen = "obsidian", schema_v = n("schema_v") ?: 0,
            valencia = n("valencia"), activacion = n("activacion"), dolor = n("dolor"),
            ctx = Catalogo.CONTEXTOS.associate { it.id to b("ctx_${it.id}") }.filterValues { it },
            hitos = l("hitos"), flow = s("flow"), sdt_necesidad = s("sdt_necesidad"), apreciacion = s("apreciacion"),
            sueno_recordado = b("sueno_recordado"), sueno_valencia = n("sueno_valencia"), sueno_lucidez = s("sueno_lucidez"),
            sueno_vividez = s("sueno_vividez"), sueno_tipo = s("sueno_tipo"), sueno_temas = l("sueno_temas"), sueno_texto = s("sueno_texto") ?: "",
            nota = nota, ts_creado = Fechas.ahoraIso(), ts_editado = Fechas.ahoraIso(),
        )
    }

    // ---------- Cronología de Google Maps ----------
    /**
     * Lee el JSON de la Cronología exportada desde el teléfono (semanticSegments) o el formato viejo de Takeout (locations).
     * Se procesa en flujo para soportar archivos grandes.
     */
    fun cronologia(ctx: Context, uri: Uri): ResultadoImport {
        var puntos = 0; var visitas = 0
        val db = Repo.db()
        val input = ctx.contentResolver.openInputStream(uri) ?: return ResultadoImport("No pude abrir el archivo.")
        val rx = Regex("(-?\\d+(?:\\.\\d+)?)°?,\\s*(-?\\d+(?:\\.\\d+)?)°?")
        fun latLng(s: String?): Pair<Double, Double>? = s?.let { rx.find(it.removePrefix("geo:")) }?.let { it.groupValues[1].toDouble() to it.groupValues[2].toDouble() }
        fun ms(s: String?): Long? = s?.let { runCatching { OffsetDateTime.parse(it).toInstant().toEpochMilli() }.getOrNull() }

        db.transaccion {
            JsonReader(InputStreamReader(input, Charsets.UTF_8)).use { r ->
                // Recorrido recursivo: cada objeto recuerda las horas de inicio que ya vio para dárselas a sus hijos.
                fun objeto(inicioPadre: Long?) {
                    r.beginObject()
                    var inicio: Long? = inicioPadre
                    var tiempo: Long? = null
                    var punto: Pair<Double, Double>? = null
                    var latE7: Long? = null; var lonE7: Long? = null; var tsViejo: Long? = null
                    while (r.hasNext()) {
                        val k = r.nextName()
                        when (r.peek()) {
                            JsonToken.BEGIN_OBJECT -> objeto(inicio)
                            JsonToken.BEGIN_ARRAY -> {
                                r.beginArray()
                                while (r.hasNext()) { if (r.peek() == JsonToken.BEGIN_OBJECT) objeto(inicio) else r.skipValue() }
                                r.endArray()
                            }
                            JsonToken.STRING -> {
                                val v = r.nextString()
                                when (k) {
                                    "startTime" -> { inicio = ms(v) ?: inicio }
                                    "time", "timestamp" -> { tiempo = ms(v); tsViejo = tiempo }
                                    "point", "latLng", "LatLng" -> punto = latLng(v)
                                    "timestampMs" -> tsViejo = v.toLongOrNull()
                                }
                            }
                            JsonToken.NUMBER -> {
                                when (k) {
                                    "latitudeE7" -> latE7 = r.nextLong()
                                    "longitudeE7" -> lonE7 = r.nextLong()
                                    "timestampMs" -> tsViejo = r.nextString().toLongOrNull()
                                    else -> r.skipValue()
                                }
                            }
                            else -> r.skipValue()
                        }
                    }
                    r.endObject()
                    val p = punto
                    val t = tiempo ?: inicio
                    if (p != null && t != null && !db.existeUbicacion(t)) {
                        db.ponerUbicacion(t, p.first, p.second, null, if (tiempo == null) "maps_visita" else "maps"); puntos++
                        if (tiempo == null) visitas++
                    }
                    val la = latE7; val lo = lonE7; val tv = tsViejo
                    if (la != null && lo != null && tv != null && !db.existeUbicacion(tv)) {
                        db.ponerUbicacion(tv, la / 1e7, lo / 1e7, null, "takeout"); puntos++
                    }
                }
                when (r.peek()) {
                    JsonToken.BEGIN_OBJECT -> objeto(null)
                    JsonToken.BEGIN_ARRAY -> { r.beginArray(); while (r.hasNext()) { if (r.peek() == JsonToken.BEGIN_OBJECT) objeto(null) else r.skipValue() }; r.endArray() }
                    else -> {}
                }
            }
        }
        return ResultadoImport("Importé $puntos puntos de ubicación de tu Cronología ($visitas visitas a lugares).")
    }
}
