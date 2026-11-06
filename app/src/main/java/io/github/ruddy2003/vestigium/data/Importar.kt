@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

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
import kotlinx.serialization.json.decodeFromStream
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

    /** Respaldo de esta app (JSON suelto, .zip completo o cifrado .vmc), o el JSON exportado por la versión web de Vestigium Mei. */
    fun respaldo(ctx: Context, uri: Uri): ResultadoImport = when (val r = Respaldos.restaurarUri(ctx, uri, null)) {
        is Respaldos.Lectura.Listo -> ResultadoImport(r.mensaje)
        is Respaldos.Lectura.Error -> ResultadoImport(r.mensaje)
        Respaldos.Lectura.PideContrasena -> ResultadoImport("Ese respaldo está cifrado: restáuralo desde «Respaldo completo» para escribir la contraseña.")
    }

    /**
     * Un respaldo hecho por una versión más nueva de la app no se restaura: lo que esta versión no conoce se descartaría
     * en silencio. Devuelve el aviso, o null si se puede restaurar.
     */
    fun esquemaFuturo(v: Int?): String? =
        if (v != null && v > SCHEMA_V) "Este respaldo es de una versión más nueva de la app (esquema $v; esta tiene el $SCHEMA_V). Actualiza la app antes de restaurarlo: así no se pierde nada." else null

    /** Lo que se leyó de un vm_respaldo.json. */
    sealed class Leido {
        /** Un respaldo de esta app y, por colección, cuántos elementos ilegibles se saltaron. */
        class Propio(val r: Respaldo, val saltados: Map<String, Int>) : Leido()
        /** El JSON de la versión web. */
        class Web(val raiz: JsonObject) : Leido()
        /** No se puede restaurar (de una versión más nueva, o no es JSON). */
        class Malo(val aviso: String) : Leido()
    }

    /**
     * Lee vm_respaldo.json directo del archivo, sin pasarlo a texto ni armar su árbol (con años de datos, eso no cabía en
     * memoria). Solo si así no se puede (un elemento que esta versión no entiende, o la versión web) se lee como texto.
     */
    fun leer(f: java.io.File): Leido {
        val r = runCatching { f.inputStream().buffered(64 * 1024).use { JSON.decodeFromStream(Respaldo.serializer(), it) } }.getOrNull()
        if (r != null && r.exportado.isNotEmpty()) return esquemaFuturo(r.schema_v)?.let { Leido.Malo(it) } ?: Leido.Propio(r, emptyMap())
        return try { leer(f.readText(Charsets.UTF_8)) } catch (e: OutOfMemoryError) { Leido.Malo("El respaldo no cupo en la memoria del teléfono.") }
    }

    fun leer(texto: String): Leido {
        val raiz = runCatching { JSON.parseToJsonElement(texto).jsonObject }.getOrNull() ?: return Leido.Malo("El archivo no es un JSON válido.")
        if (!raiz.containsKey("momentos") && !raiz.containsKey("respuestas")) return Leido.Web(raiz)
        esquemaFuturo(raiz["schema_v"].int())?.let { return Leido.Malo(it) }
        val (r, saltados) = tolerante(raiz)
        return Leido.Propio(r, saltados)
    }

    /**
     * El respaldo, aunque traiga elementos que no se pueden leer: antes, uno solo (un momento con un dato que esta versión no
     * entiende) tumbaba la restauración entera. Ahora cada colección que falla se revisa elemento por elemento y se quedan
     * los que sí se leen; los demás se cuentan para avisar.
     */
    fun tolerante(raiz: JsonObject): Pair<Respaldo, Map<String, Int>> {
        runCatching { return JSON.decodeFromJsonElement(Respaldo.serializer(), raiz) to emptyMap() }
        val base = mapOf(
            "exportado" to ((raiz["exportado"] as? JsonPrimitive)?.takeIf { it !is JsonNull } ?: JsonPrimitive("?")),
            "schema_v" to (raiz["schema_v"].int()?.let { JsonPrimitive(it) } ?: JsonPrimitive(0)))
        fun sirve(k: String, v: JsonElement) = runCatching { JSON.decodeFromJsonElement(Respaldo.serializer(), JsonObject(base + (k to v))) }.isSuccess
        val saltados = LinkedHashMap<String, Int>()
        val limpio = LinkedHashMap<String, JsonElement>(base)
        for ((k, v) in raiz) {
            if (k in base) continue
            when {
                sirve(k, v) -> limpio[k] = v
                v is JsonArray -> {
                    val buenos = v.filter { sirve(k, JsonArray(listOf(it))) }
                    saltados[k] = v.size - buenos.size
                    limpio[k] = JsonArray(buenos)
                }
                else -> saltados[k] = 1
            }
        }
        return JSON.decodeFromJsonElement(Respaldo.serializer(), JsonObject(limpio)) to saltados
    }

    fun aplicar(l: Leido): ResultadoImport = when (l) {
        is Leido.Malo -> ResultadoImport(l.aviso)
        is Leido.Web -> web(l.raiz)
        is Leido.Propio -> propio(l.r, l.saltados)
    }

    /** El texto de vm_respaldo.json (o el de la versión web). */
    fun respaldoTexto(texto: String): ResultadoImport = aplicar(leer(texto))

    private fun propio(r: Respaldo, saltados: Map<String, Int>): ResultadoImport {
            // En un teléfono recién instalado, el respaldo trae también todos tus ajustes; si ya hay datos, se combinan sin pisar lo tuyo.
            // (Si los ajustes del respaldo no se pudieron leer, no se toman como si fueran los de un teléfono nuevo.)
            val nuevo = Repo.dias.isEmpty() && Repo.momentos.isEmpty() && Repo.gastos.isEmpty() && "config" !in saltados
            val n = Repo.importarDias(r.dias, sobrescribir = false)
            if (r.visitas.isNotEmpty()) Repo.importarVisitas(r.visitas)
            // En lote: antes cada momento se buscaba en toda la lista, se guardaba suelto y la lista se reordenaba entera.
            Repo.importarMomentos(r.momentos)
            Repo.importarGastos(r.gastos)
            if (r.ingresos.isNotEmpty()) Repo.importarIngresos(r.ingresos)
            if (r.tramos.isNotEmpty()) Repo.importarTramos(r.tramos)
            if (r.consumos.isNotEmpty()) Repo.importarConsumos(r.consumos)
            if (r.notas.isNotEmpty()) Repo.importarNotas(r.notas)
            if (r.ideas.isNotEmpty()) Repo.importarIdeas(r.ideas)
            if (r.listas.isNotEmpty()) Repo.importarListas(r.listas)
            if (r.metas.isNotEmpty()) Repo.importarMetas(r.metas)
            if (r.tomas.isNotEmpty()) Repo.importarTomas(r.tomas)
            Repo.importarAvisos(r.avisos)
            if (r.laptop.isNotEmpty()) Repo.guardarLaptop(r.laptop.filter { it.fecha !in Repo.laptop })
            Repo.importarRespuestas(r.respuestas)
            Repo.importarReacciones(r.reacciones)
            // Sin pisar lo de aquí: un lugar o un día de sensores que ya está se queda como está (antes, un lugar que
            // renombraste volvía al nombre viejo, y la pantalla o el clima de aquí se cambiaban por los del respaldo).
            val lugaresAqui = Repo.lugares.map { it.id }.toSet()
            r.lugares.filter { it.id !in lugaresAqui }.forEach { Repo.guardarLugar(it) }
            Repo.importarSalud(r.salud.map { Fusion.salud(Repo.salud[it.fecha], it) })
            r.pantalla.filter { it.fecha !in Repo.pantalla }.forEach { Repo.guardarPantalla(it) }
            // Una carta que ya abriste aquí sigue abierta; una que solo está en el respaldo se agrega; si allá ya estaba
            // abierta y aquí no, queda abierta (antes el respaldo volvía a sellar las que abriste después).
            r.cartas.forEach { c ->
                val aqui = Repo.cartas.firstOrNull { it.id == c.id }
                when {
                    aqui == null -> Repo.guardarCarta(c)
                    c.abierta && !aqui.abierta -> Repo.guardarCarta(aqui.copy(abierta = true, abierta_el = aqui.abierta_el ?: c.abierta_el))
                }
            }
            r.experimentos.forEach { e -> Fusion.experimento(Repo.experimentos.firstOrNull { it.id == e.id }, e)?.let { Repo.guardarExperimento(it) } }
            r.entorno.filter { it.fecha !in Repo.entorno }.forEach { Repo.guardarEntorno(it) }
            r.fotos.filter { it.fecha !in Repo.fotos }.forEach { Repo.guardarFotos(it) }
            r.agenda.filter { it.fecha !in Repo.agenda }.forEach { Repo.guardarAgenda(it) }
            val sesionesAqui = Repo.sesiones.map { it.id }.toSet()
            r.sesiones.filter { it.id !in sesionesAqui }.forEach { Repo.guardarSesion(it) }
            val ascensosAqui = Repo.ascensos.map { it.id }.toSet()
            r.ascensos.filter { it.id !in ascensosAqui }.forEach { Repo.guardarAscenso(it) }
            val obrasAqui = Repo.obras.map { it.id }.toSet()
            r.obras.filter { it.id !in obrasAqui }.forEach { Repo.guardarObra(it) }
            Repo.importarEscuchas(r.escuchas)
            Repo.importarSensores(r.notifs, r.actividad)
            r.comunicacion.forEach { if (it.fecha !in Repo.comunicacion) Repo.guardarComunicacion(it) }
            r.conexiones.forEach { c -> if (c.fecha !in Repo.conexiones) Repo.cambiarConexion(c.fecha) { c } }
            if (r.subrayados.isNotEmpty()) Repo.importarSubrayados(r.subrayados)
            if (r.huellas.isNotEmpty()) Repo.importarHuellas(r.huellas)
            if (r.huellas_dia.isNotEmpty()) Repo.guardarHuellasDia(r.huellas_dia.filter { it.fecha !in Repo.huellas })
            if (r.chats.isNotEmpty()) { val aqui = Repo.chats.map { it.id }.toSet(); Repo.importarChats(r.chats.filter { it.id !in aqui }) }
            if (r.youtube.isNotEmpty()) { val aqui = Repo.youtube.map { it.id }.toSet(); Repo.importarYoutube(r.youtube.filter { it.id !in aqui }, reemplazarSuscripciones = false) }
            if (r.nutricion.isNotEmpty() || r.series.isNotEmpty()) {
                val fs = r.nutricion.map { it.fecha } + r.comidas.map { it.fecha } + r.series.map { it.fecha } + r.entrenos.map { it.fecha }
                if (fs.isNotEmpty() && Repo.nutricion.isEmpty() && Repo.entrenos.isEmpty()) {
                    Repo.importarMacrofactor(fs.min(), fs.max(), r.nutricion, r.comidas, r.series, r.entrenos)
                    // Un respaldo de antes del esquema 15 no trae la fuerza de referencia: se calcula con sus series (H-7-14).
                    // Con los entrenos del respaldo: la lista en memoria se actualiza un instante después y todavía estaría vacía.
                    runCatching { Repo.completarFuerzaReferencia(r.entrenos) }
                }
            }
            Repo.cambiarConfig { c -> combinarConfig(c, r.config, nuevo) }
            val avisoSaltados = saltados.filterValues { it > 0 }.takeIf { it.isNotEmpty() }?.let { m ->
                " No se pudieron leer y se saltaron: " + m.entries.joinToString(", ") { (k, v) -> if (k == "config") "los ajustes" else "%,d de «%s»".format(v, k) } + "."
            }.orEmpty()
            return ResultadoImport("Respaldo importado: ${porNumero(n, "1 día nuevo", "$n días nuevos")}, ${porNumero(r.momentos.size, "1 momento", "${r.momentos.size} momentos")}, ${porNumero(r.gastos.size, "1 gasto", "${r.gastos.size} gastos")} y ${porNumero(r.visitas.size, "1 visita", "${r.visitas.size} visitas")} revisados" +
                (if (nuevo) "; también tus ajustes, Conocerte, la brújula y el mapa." else if ("config" in saltados) "." else "; tus ajustes se combinaron con los de aquí.") + avisoSaltados)
    }

    /** El JSON de la versión web de Vestigium Mei. */
    private fun web(raiz: JsonObject): ResultadoImport {
        val dias = raiz["dias"]?.jsonArray?.mapNotNull { e -> runCatching { diaDesdeWeb(e.jsonObject) }.getOrNull() } ?: emptyList()
        val n = Repo.importarDias(dias, sobrescribir = false)
        val gastos = raiz["gastos_mes"]?.jsonArray?.flatMap { mes ->
            mes.jsonObject["items"]?.jsonArray?.mapNotNull { it as? JsonObject }?.map { g ->
                Gasto(id = g["id"].str() ?: Repo.nuevoId(), fecha = g["fecha"].str() ?: "", hora = g["hora"].str() ?: "",
                    monto = (g["monto"] as? JsonPrimitive)?.doubleOrNull ?: 0.0, categoria = g["categoria"].str() ?: "Otro",
                    imprevisto = g["imprevisto"].bool(), nota = g["nota"].str() ?: "", se_repite = g["se_repite"].bool())
            } ?: emptyList()
        }?.filter { it.fecha.isNotEmpty() } ?: emptyList()
        Repo.importarGastos(gastos)
        val habitos = raiz["habitos"]?.jsonArray?.mapNotNull { it as? JsonObject }?.map {
            Habito(id = it["id"].str() ?: Texto.slug(it["nombre"].str() ?: "h"), nombre = it["nombre"].str() ?: "Hábito", activo = (it["activo"] as? JsonPrimitive)?.booleanOrNull ?: true)
        } ?: emptyList()
        val eq = (raiz["equivalencias"] as? JsonObject)?.mapNotNull { (k, v) -> v.str()?.let { k to it } }?.toMap() ?: emptyMap()
        Repo.cambiarConfig { c -> c.copy(habitos = (c.habitos + habitos).distinctBy { it.id }, equivalencias = eq + c.equivalencias) }
        return ResultadoImport("Importé ${porNumero(n, "1 día nuevo", "$n días nuevos")} y ${porNumero(gastos.size, "1 gasto", "${gastos.size} gastos")} de la versión web. Los días que ya existían no se tocaron.")
    }

    /**
     * Ajustes del respaldo. Con la app vacía se toman completos, menos lo que solo vale en el otro teléfono (la carpeta elegida,
     * los avisos ya programados). Con datos, se suman listas y mapas sin borrar nada de lo de aquí.
     */
    fun combinarConfig(c: Config, r: Config, nuevo: Boolean): Config {
        if (nuevo) return r.copy(carpeta_respaldo = null, disparos = emptyMap(), plan_muestreo = emptyMap(), ultimo_sync = c.ultimo_sync, enfoque_tipo = null, enfoque_inicio = null, enfoque_plan = null,
            bienvenida_vista = true, drive = c.drive, drive_carpeta = c.drive_carpeta ?: r.drive_carpeta, drive_ultimo = c.drive_ultimo, respaldo_ultimo = c.respaldo_ultimo)
        val perfilVacio = c.perfil == Perfil()
        return c.copy(
            habitos = (c.habitos + r.habitos).distinctBy { it.id }, equivalencias = r.equivalencias + c.equivalencias,
            categorias = (c.categorias + r.categorias).distinct(), categorias_ingreso = (c.categorias_ingreso + r.categorias_ingreso).distinct(),
            frases_guardadas = (c.frases_guardadas + r.frases_guardadas).distinctBy { it.texto + it.autor },
            gimnasios = (c.gimnasios + r.gimnasios).distinct(), whatsapp_yo = c.whatsapp_yo.ifBlank { r.whatsapp_yo },
            ciudades_visitadas = (c.ciudades_visitadas + r.ciudades_visitadas).distinctBy { it.id },
            paises_rascados = (c.paises_rascados + r.paises_rascados).distinct(), paises_tapados = (c.paises_tapados + r.paises_tapados).distinct(),
            unesco_marcados = (c.unesco_marcados + r.unesco_marcados).distinctBy { it.id }, unesco_descartados = (c.unesco_descartados + r.unesco_descartados).distinct(),
            perfil = if (perfilVacio) r.perfil else c.perfil, perfil_historial = (r.perfil_historial + c.perfil_historial).distinct().sortedBy { it.fecha },
            conocer_visto = c.conocer_visto || r.conocer_visto, politica = (c.politica + r.politica).distinctBy { it.fecha }.sortedBy { it.fecha },
            fotos_aparatos = (c.fotos_aparatos + r.fotos_aparatos).distinct(), fotos_sin_fecha = (c.fotos_sin_fecha + r.fotos_sin_fecha).distinct(),
            fotos_revisadas = r.fotos_revisadas + c.fotos_revisadas, salud_sexo = c.salud_sexo.ifBlank { r.salud_sexo },
            logros_propios = (c.logros_propios + r.logros_propios).distinctBy { it.id }, logros_vistos = r.logros_vistos + c.logros_vistos,
            temas_rotacion = c.temas_rotacion.ifEmpty { r.temas_rotacion },
            // Si se abandonó en el otro teléfono, aquí también queda abandonada.
            campanas = (c.campanas.map { a -> r.campanas.firstOrNull { it.id == a.id }?.abandonada?.let { ab -> a.copy(abandonada = a.abandonada ?: ab) } ?: a } + r.campanas).distinctBy { it.id }, hall_logros = c.hall_logros.ifEmpty { r.hall_logros },
            favoritos_mas = (c.favoritos_mas + r.favoritos_mas).distinct(), habito_recaida = c.habito_recaida ?: r.habito_recaida,
            // Tu gente: lo de aquí manda; del respaldo se suma a quien no esté.
            // Las fechas de nacimiento y fallecimiento que solo tiene el respaldo también se suman.
            personas = c.personas.map { p -> r.personas.firstOrNull { it.nombre == p.nombre }?.let { q -> p.copy(nacimiento = p.nacimiento.ifBlank { q.nacimiento }, fallecimiento = p.fallecimiento.ifBlank { q.fallecimiento }) } ?: p } +
                r.personas.filter { p -> c.personas.none { it.nombre == p.nombre } },
            fechas_propias = (c.fechas_propias + r.fechas_propias).distinctBy { it.id },
            // El historial de zonas: lo de aquí y lo del respaldo, sin repetir instantes (lo de aquí manda; H-1-7).
            zonas = (c.zonas + r.zonas).distinctBy { Fechas.millis(it.desde) }.sortedBy { Fechas.millis(it.desde) },
            personas_omitidas = (c.personas_omitidas + r.personas_omitidas).distinct(), personas_distintas = (c.personas_distintas + r.personas_distintas).distinct(),
            temas_distintos = (c.temas_distintos + r.temas_distintos).distinct(),
            lugares_ignorados = (c.lugares_ignorados + r.lugares_ignorados).distinctBy { "%.4f,%.4f".format(it.lat, it.lon) },
            importado = r.importado + c.importado, barometro_ajuste = c.barometro_ajuste ?: r.barometro_ajuste,
            sustancias_ocultas = (c.sustancias_ocultas + r.sustancias_ocultas).distinct(),
            cafeina_vida_media_h = if (c.cafeina_vida_media_h == Config().cafeina_vida_media_h) r.cafeina_vida_media_h else c.cafeina_vida_media_h,
            suplementos = (c.suplementos + r.suplementos).distinctBy { it.id },
            vinculos = (c.vinculos + r.vinculos).distinctBy { it.a + "|" + it.b },
            laptop_categorias = r.laptop_categorias + c.laptop_categorias,
        )
    }

    private fun diaDesdeWeb(o: JsonObject): Dia {
        val fecha = o["fecha"].str()!!.take(10)
        val habitos = (o["habitos"] as? JsonObject)?.mapValues { it.value.bool() }?.filterValues { it } ?: emptyMap()
        return Dia(
            // Sin versión propia, 0: no la versión de hoy (P-33). Los sellos, los del archivo o vacíos.
            fecha = fecha, schema_v = o["schema_v"].int() ?: 0, origen = o["origen"].str() ?: "web",
            valencia = o["valencia"].int(), activacion = o["activacion"].int(), dolor = o["dolor"].int(),
            ctx = Catalogo.CONTEXTOS.associate { it.id to o["ctx_${it.id}"].bool() }.filterValues { it },
            hitos = o["hitos"].lista(), flow = o["flow"].str(), sdt_necesidad = o["sdt_necesidad"].str(), apreciacion = o["apreciacion"].str(),
            sueno_recordado = o["sueno_recordado"].bool(), sueno_valencia = o["sueno_valencia"].int(),
            sueno_lucidez = o["sueno_lucidez"].str(), sueno_vividez = o["sueno_vividez"].str(), sueno_tipo = o["sueno_tipo"].str(),
            sueno_rareza = o["sueno_rareza"].str(), sueno_pesadilla = o["sueno_pesadilla"].str()?.let { it == "true" || it == "1" },
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
        return ResultadoImport("Leí ${dias.size} nota(s); importé ${porNumero(n, "1 día nuevo", "$n días nuevos")}." + if (fallas.isNotEmpty()) " No pude leer: ${fallas.joinToString(", ")}." else "")
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
            sueno_vividez = s("sueno_vividez"), sueno_tipo = s("sueno_tipo"), sueno_rareza = s("sueno_rareza"), sueno_pesadilla = s("sueno_pesadilla")?.let { it == "true" || it == "1" }, sueno_temas = l("sueno_temas"), sueno_texto = s("sueno_texto") ?: "",
            // Los sellos del encabezado si los trae; si no, vacíos: no la hora de importar (P-33).
            nota = nota, ts_creado = s("ts_creado"), ts_editado = s("ts_editado"),
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
                    var latE7: Long? = null; var lonE7: Long? = null; var tsViejo: Long? = null; var precision: Double? = null
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
                                    // El formato viejo trae la precisión de cada punto (metros): se conserva (P-33).
                                    "accuracy" -> precision = r.nextDouble()
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
                        db.ponerUbicacion(tv, la / 1e7, lo / 1e7, precision?.toFloat(), "takeout"); puntos++
                    }
                }
                when (r.peek()) {
                    JsonToken.BEGIN_OBJECT -> objeto(null)
                    JsonToken.BEGIN_ARRAY -> { r.beginArray(); while (r.hasNext()) { if (r.peek() == JsonToken.BEGIN_OBJECT) objeto(null) else r.skipValue() }; r.endArray() }
                    else -> {}
                }
            }
        }
        Mundo.invalidar()
        return ResultadoImport("Importé $puntos puntos de ubicación de tu Cronología (${porNumero(visitas, "1 visita", "$visitas visitas")} a lugares).")
    }
}
