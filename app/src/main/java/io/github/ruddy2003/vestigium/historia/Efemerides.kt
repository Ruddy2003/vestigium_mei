package io.github.ruddy2003.vestigium.historia

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import java.io.File
import java.time.LocalDate

// =====================================================================
//  «Hoy en la historia» (plan de historia y escenas, sección 3.6; tanda H1, 13 nov 2026)
// =====================================================================
//
// Lo que pasó un día como hoy, de Wikipedia en español. **Lo único que sale del teléfono es el mes y el día** (en la
// dirección que se pide); nada tuyo se cruza con lo que llega. Se guarda un archivo pequeño por fecha en
// `filesDir/historia/`, fuera de la base, del respaldo y de la exportación: no es un dato tuyo.
//
// De dónde salen los eventos. H1 probó las tres maneras el 13 nov 2026:
// - `api.wikimedia.org/feed/v1/wikipedia/es/onthisday/events/MM/DD` no se pudo probar: la red de la sesión lo bloquea;
// - la API de MediaWiki («13 de noviembre», sección «Acontecimientos») contesta «demasiadas peticiones» a las
//   direcciones compartidas, y daría el texto en wikitexto, que hay que limpiar a mano;
// - **`es.wikipedia.org/api/rest_v1/feed/onthisday/events/MM/DD`** contesta en JSON, con el año, el texto y los
//   artículos enlazados de cada evento: 98 eventos el 13 de noviembre (del 7 a. C. a 2024), unos 100 KB comprimidos.
//   Es la que se usa. Sale de la misma página del día, así que la licencia es la de Wikipedia: CC BY-SA 4.0.
//
// Todo lo de este archivo es Kotlin puro (sin Android): la carpeta y la descarga se le pasan, para poder probarlo.

object Efemerides {
    /** Días que vale lo guardado de una fecha; después se vuelve a pedir (el plan dice 30). */
    const val DIAS_VIGENTE = 30L

    /** Cuántos artículos enlazados se guardan por evento (se muestran debajo de su texto). */
    const val ENLACES_POR_EVENTO = 3

    const val LICENCIA = "CC BY-SA 4.0"
    const val URL_LICENCIA = "https://creativecommons.org/licenses/by-sa/4.0/deed.es"

    /** Un artículo de Wikipedia que el evento enlaza. */
    @Serializable
    data class Enlace(val titulo: String, val url: String)

    /** Un evento: su año (negativo, antes de Cristo), su texto y los artículos que enlaza. */
    @Serializable
    data class Evento(val anio: Int, val texto: String, val enlaces: List<Enlace> = emptyList())

    /** Lo guardado de una fecha: el día en que se bajó y sus eventos. */
    @Serializable
    data class Guardado(val mes: Int, val dia: Int, val bajado: String, val eventos: List<Evento>)

    /** Lo que ve la pantalla: los eventos, si salen de lo guardado sin poder renovarlo, y cuándo se bajaron. */
    data class Resultado(val eventos: List<Evento>, val bajado: LocalDate?, val sinConexion: Boolean, val error: String? = null)

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    /** La dirección que se pide: solo lleva el mes y el día. */
    fun url(mes: Int, dia: Int): String = "https://es.wikipedia.org/api/rest_v1/feed/onthisday/events/%02d/%02d".format(mes, dia)

    /** La página del día en Wikipedia («13 de noviembre»), para el pie de la pantalla. */
    fun paginaDelDia(mes: Int, dia: Int): String = "https://es.wikipedia.org/wiki/${dia}_de_${MESES[mes - 1]}"

    /** «13 de noviembre». */
    fun nombreDelDia(mes: Int, dia: Int): String = "$dia de ${MESES[mes - 1]}"

    /** «1492», «7 a. C.». Wikipedia no tiene año 0: el año antes del 1 es el 1 a. C. (−1). */
    fun nombreDelAnio(anio: Int): String = if (anio < 0) "${-anio} a. C." else anio.toString()

    private val MESES = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")

    // ------------------------------------------------------------ leer la respuesta

    private fun JsonElement?.o(k: String): JsonElement? = (this as? JsonObject)?.get(k)
    private fun JsonElement?.s(k: String): String? = ((this as? JsonObject)?.get(k) as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

    /**
     * Los eventos de la respuesta, del más antiguo al más reciente (a igual año, en el orden de Wikipedia).
     * - El texto se limpia: sin espacios de más y con mayúscula al empezar (Wikipedia los escribe como «en Argentina, …»,
     *   porque en su página van detrás del año).
     * - Se descartan los que no tienen año o texto.
     * - De cada uno se guardan hasta [ENLACES_POR_EVENTO] artículos, sin repetir, con su dirección de escritorio.
     */
    fun leer(respuesta: String): List<Evento> {
        val raiz = json.parseToJsonElement(respuesta)
        val eventos = raiz.o("events") as? JsonArray ?: return emptyList()
        return eventos.mapNotNull { e ->
            val anio = (e.o("year") as? JsonPrimitive)?.intOrNull ?: return@mapNotNull null
            val texto = limpiar(e.s("text") ?: return@mapNotNull null).takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            val enlaces = (e.o("pages") as? JsonArray).orEmpty().mapNotNull { p ->
                val titulo = p.o("titles").s("normalized") ?: p.s("normalizedtitle") ?: p.s("title")?.replace('_', ' ') ?: return@mapNotNull null
                val url = p.o("content_urls").o("desktop").s("page") ?: return@mapNotNull null
                Enlace(titulo, url)
            }.distinctBy { it.url }.take(ENLACES_POR_EVENTO)
            Evento(anio, texto, enlaces)
        }.sortedBy { it.anio }
    }

    /** Sin espacios repetidos ni en las orillas, y con la primera letra en mayúscula. */
    fun limpiar(s: String): String = s.replace(Regex("\\s+"), " ").trim().replaceFirstChar { it.uppercase() }

    // ------------------------------------------------------------ lo guardado

    private fun archivo(carpeta: File, mes: Int, dia: Int) = File(carpeta, "%02d-%02d.json".format(mes, dia))

    /** Lo guardado de esa fecha, o null si no hay o no se puede leer. */
    fun guardado(carpeta: File, mes: Int, dia: Int): Guardado? =
        runCatching { json.decodeFromString(Guardado.serializer(), archivo(carpeta, mes, dia).readText()) }.getOrNull()

    /** Si lo guardado todavía vale [hoy]: se bajó hace menos de [DIAS_VIGENTE] días (y no «en el futuro»). */
    fun vigente(g: Guardado, hoy: LocalDate): Boolean {
        val b = runCatching { LocalDate.parse(g.bajado) }.getOrNull() ?: return false
        return !b.isAfter(hoy) && b.plusDays(DIAS_VIGENTE).isAfter(hoy)
    }

    private fun guardar(carpeta: File, g: Guardado) {
        carpeta.mkdirs()
        val f = archivo(carpeta, g.mes, g.dia)
        val tmp = File(carpeta, f.name + ".tmp")
        tmp.writeText(json.encodeToString(Guardado.serializer(), g))
        if (!tmp.renameTo(f)) { f.delete(); tmp.renameTo(f) }
    }

    /**
     * Baja la fecha [mes]/[dia] con [pedir] (que recibe la dirección y devuelve el cuerpo) y la guarda. Si Wikipedia
     * contesta sin eventos no se guarda nada: así no se pisa una copia buena con una vacía.
     */
    fun bajarYGuardar(carpeta: File, mes: Int, dia: Int, hoy: LocalDate, pedir: (String) -> String): Guardado? {
        val eventos = leer(pedir(url(mes, dia)))
        if (eventos.isEmpty()) return null
        return Guardado(mes, dia, hoy.toString(), eventos).also { guardar(carpeta, it) }
    }

    /**
     * Los eventos de [fecha] para la pantalla ([pedir] baja una dirección, como `Red.get`).
     * - Si lo guardado está vigente, eso, sin conectarse.
     * - Si no, se baja; si falla, lo guardado aunque sea viejo, avisando que no hubo conexión.
     * - Sin nada guardado y sin conexión: una lista vacía con el error.
     */
    fun delDia(carpeta: File, fecha: LocalDate, hoy: LocalDate, pedir: (String) -> String): Resultado {
        val g = guardado(carpeta, fecha.monthValue, fecha.dayOfMonth)
        if (g != null && vigente(g, hoy)) return Resultado(g.eventos, LocalDate.parse(g.bajado), sinConexion = false)
        return try {
            val nuevo = bajarYGuardar(carpeta, fecha.monthValue, fecha.dayOfMonth, hoy, pedir)
            when {
                nuevo != null -> Resultado(nuevo.eventos, hoy, sinConexion = false)
                g != null -> Resultado(g.eventos, LocalDate.parse(g.bajado), sinConexion = true, error = "Wikipedia no trajo eventos")
                else -> Resultado(emptyList(), null, sinConexion = true, error = "Wikipedia no trajo eventos")
            }
        } catch (e: java.util.concurrent.CancellationException) {
            throw e
        } catch (e: Throwable) {
            Resultado(g?.eventos.orEmpty(), g?.let { runCatching { LocalDate.parse(it.bajado) }.getOrNull() }, sinConexion = true,
                error = e.message?.take(120) ?: e::class.simpleName)
        }
    }

    /**
     * El trabajo de 15 minutos, una vez al día: deja lista la fecha de mañana (si lo guardado no está vigente), para que
     * mañana se vea aunque no haya conexión. Luego borra lo que ya no vale y no es de hoy ni de mañana, para que la
     * carpeta no crezca (unas treinta fechas de unos 36 KB cada una: alrededor de 1 MB). Devuelve si bajó algo.
     */
    fun prepararManana(carpeta: File, hoy: LocalDate, pedir: (String) -> String): Boolean {
        val manana = hoy.plusDays(1)
        val g = guardado(carpeta, manana.monthValue, manana.dayOfMonth)
        val bajo = if (g == null || !vigente(g, hoy)) bajarYGuardar(carpeta, manana.monthValue, manana.dayOfMonth, hoy, pedir) != null else false
        podar(carpeta, hoy)
        return bajo
    }

    /** Borra las fechas guardadas que ya no están vigentes, salvo hoy y mañana (que sirven sin conexión aunque sean viejas). */
    fun podar(carpeta: File, hoy: LocalDate) {
        val quedan = setOf(hoy, hoy.plusDays(1)).map { "%02d-%02d.json".format(it.monthValue, it.dayOfMonth) }.toSet()
        carpeta.listFiles().orEmpty().forEach { f ->
            if (f.name in quedan) return@forEach
            if (f.name.endsWith(".tmp")) { f.delete(); return@forEach }
            val g = runCatching { json.decodeFromString(Guardado.serializer(), f.readText()) }.getOrNull()
            if (g == null || !vigente(g, hoy)) f.delete()
        }
    }
}
