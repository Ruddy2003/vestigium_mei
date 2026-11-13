package io.github.ruddy2003.vestigium.historia

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

// =====================================================================
//  De qué país es cada artículo, según Wikidata (tanda H3, 13 nov 2026)
// =====================================================================
//
// «Hoy en la historia» enlaza un acontecimiento con un personaje solo si pudo afectarle: si es de su país o región
// (`vidas/Epocas.kt`). Para saber de dónde es, se le pregunta a Wikidata por los artículos que el acontecimiento enlaza:
// la respuesta de Wikipedia ya trae su identificador (`wikibase_item`, como `Q96`).
//
// **A Wikidata solo van esos identificadores**, de artículos de Wikipedia, en lotes de [LOTE]: nada tuyo. Se pregunta
// en el trabajo de la mañana, junto con la fecha de mañana, y la respuesta se guarda en el mismo archivo de la fecha.
// Si Wikidata no contesta, no pasa nada: el enlace usa las palabras de cada personaje.
//
// Kotlin puro (sin Android), como `Efemerides`: la descarga se le pasa.

object Wikidata {
    /** Cuántos identificadores van en cada petición (`wbgetentities` acepta hasta 50). */
    const val LOTE = 50

    /**
     * Las propiedades que dicen directamente el país de un artículo:
     * - P17, país (de un lugar, un suceso, una batalla, un edificio);
     * - P27, país de nacionalidad (de una persona);
     * - P495, país de origen (de una obra o una costumbre).
     */
    val PAIS = listOf("P17", "P27", "P495")

    /**
     * Las que dicen dónde está o pasó, con un lugar que puede no ser un país (una ciudad, un estado): de esos lugares
     * se pregunta también su país, en una segunda vuelta.
     * - P276, lugar (de un suceso);
     * - P131, entidad territorial en la que está (de un lugar: la ciudad está en un estado, el estado en un país).
     */
    val LUGAR = listOf("P276", "P131")

    private val json = Json { ignoreUnknownKeys = true }
    private val ID = Regex("Q[1-9][0-9]*")

    fun esId(s: String): Boolean = ID.matches(s)

    /** La dirección de una petición: solo lleva identificadores de artículos y las propiedades que se piden. */
    fun url(ids: List<String>): String {
        require(ids.isNotEmpty() && ids.size <= LOTE && ids.all { esId(it) }) { "identificadores no válidos" }
        return "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=claims&ids=" + ids.joinToString("%7C")
    }

    private fun JsonElement?.o(k: String): JsonElement? = (this as? JsonObject)?.get(k)

    /**
     * La respuesta de `wbgetentities`: para cada identificador, los valores (otros identificadores) de las propiedades
     * de [PAIS] y [LUGAR]. Lo que no es un identificador (una fecha, un texto) se ignora; un artículo que Wikidata no
     * conoce no sale.
     */
    fun leer(respuesta: String): Map<String, Map<String, List<String>>> {
        val entidades = json.parseToJsonElement(respuesta).o("entities") as? JsonObject ?: return emptyMap()
        return entidades.mapNotNull { (q, e) ->
            if (!esId(q) || e.o("missing") != null) return@mapNotNull null
            val claims = e.o("claims") as? JsonObject ?: return@mapNotNull q to emptyMap()
            q to (PAIS + LUGAR).mapNotNull { p ->
                val vals = (claims[p] as? JsonArray).orEmpty().mapNotNull { c ->
                    ((c.o("mainsnak").o("datavalue").o("value").o("id")) as? JsonPrimitive)?.contentOrNull?.takeIf { esId(it) }
                }.distinct()
                if (vals.isEmpty()) null else p to vals
            }.toMap()
        }.toMap()
    }

    /** Pide [ids] en lotes de [LOTE] con [pedir] y junta las respuestas. Si una petición falla, falla todo. */
    fun pedirTodos(ids: Collection<String>, pedir: (String) -> String): Map<String, Map<String, List<String>>> =
        ids.filter { esId(it) }.distinct().sorted().chunked(LOTE).fold(emptyMap()) { acc, lote -> acc + leer(pedir(url(lote))) }

    /**
     * Para cada uno de [articulos], los identificadores de los sitios a los que pertenece: él mismo (un artículo puede
     * ser un país o una ciudad), los de [PAIS], los lugares de [LUGAR] y, en una segunda vuelta, el país y la entidad de
     * esos lugares. Son dos rondas de peticiones como mucho. Si Wikidata no contesta, lanza el error.
     */
    fun lugaresDe(articulos: Collection<String>, pedir: (String) -> String): Map<String, List<String>> {
        val primera = pedirTodos(articulos, pedir)
        val lugares = primera.values.flatMap { m -> LUGAR.flatMap { m[it].orEmpty() } }.toSet() - primera.keys
        val segunda = if (lugares.isEmpty()) emptyMap() else pedirTodos(lugares, pedir)
        val todo = primera + segunda
        return primera.mapValues { (q, m) ->
            val directos = PAIS.flatMap { m[it].orEmpty() }
            val sitios = LUGAR.flatMap { m[it].orEmpty() }
            val deLosSitios = sitios.flatMap { s -> todo[s].orEmpty().let { sm -> PAIS.flatMap { sm[it].orEmpty() } + LUGAR.flatMap { sm[it].orEmpty() } } }
            (listOf(q) + directos + sitios + deLosSitios).distinct()
        }
    }
}
