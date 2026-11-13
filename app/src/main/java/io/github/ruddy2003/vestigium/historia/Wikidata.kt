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
// en el trabajo de la mañana y la respuesta se guarda en el mismo archivo de la fecha. Si Wikidata no contesta, no pasa
// nada: el enlace usa las palabras de cada personaje.
//
// Qué propiedades sirven. H3 las midió con las respuestas enteras de cuatro fechas (436 acontecimientos, 847 artículos):
// - P17 (país) la traen 488 artículos; P27 (nacionalidad), 195; P495 (país de origen), 34. Son las que se usan.
// - P131 (entidad territorial, 232) y P276 (lugar, 100) no cambiaron ningún enlace: los lugares ya traen P17. Pedirlas
//   obligaba a una segunda vuelta para saber el país de cada lugar, así que se quitaron.
// - P361 («parte de») mete continentes; P19 y P20 (dónde nació o murió alguien) no dicen dónde pasó el suceso; P710
//   (participantes) mete todos los países de una guerra. No se usan.
// - P27 solo cuenta si es una sola: José Martí, cubano, sale como español y mexicano, y hacía de un suceso de Chicago
//   algo de México.
//
// Kotlin puro (sin Android), como `Efemerides`: la descarga se le pasa.

object Wikidata {
    /** Cuántos identificadores van en cada petición (`wbgetentities` acepta hasta 50). */
    const val LOTE = 50

    /** País (de un lugar, un suceso, un edificio) y país de origen (de una obra o una costumbre). */
    val PAIS = listOf("P17", "P495")

    /** País de nacionalidad (de una persona): cuenta solo si es uno. */
    const val NACIONALIDAD = "P27"

    private val json = Json { ignoreUnknownKeys = true }
    private val ID = Regex("Q[1-9][0-9]*")

    fun esId(s: String): Boolean = ID.matches(s)

    /** La dirección de una petición: solo lleva identificadores de artículos. */
    fun url(ids: List<String>): String {
        require(ids.isNotEmpty() && ids.size <= LOTE && ids.all { esId(it) }) { "identificadores no válidos" }
        return "https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=claims&ids=" + ids.joinToString("%7C")
    }

    private fun JsonElement?.o(k: String): JsonElement? = (this as? JsonObject)?.get(k)

    private fun valores(claims: JsonObject, p: String): List<String> = (claims[p] as? JsonArray).orEmpty().mapNotNull { c ->
        ((c.o("mainsnak").o("datavalue").o("value").o("id")) as? JsonPrimitive)?.contentOrNull?.takeIf { esId(it) }
    }.distinct()

    /**
     * La respuesta de `wbgetentities`: para cada identificador, sus países ([PAIS], y [NACIONALIDAD] si es uno solo),
     * sin repetir. Un artículo que Wikidata conoce pero que no dice país sale con la lista vacía; uno que no conoce, no
     * sale.
     */
    fun leer(respuesta: String): Map<String, List<String>> {
        val entidades = json.parseToJsonElement(respuesta).o("entities") as? JsonObject ?: return emptyMap()
        return entidades.mapNotNull { (q, e) ->
            if (!esId(q) || e.o("missing") != null) return@mapNotNull null
            val claims = e.o("claims") as? JsonObject ?: return@mapNotNull q to emptyList()
            val nacion = valores(claims, NACIONALIDAD).takeIf { it.size == 1 }.orEmpty()
            q to (PAIS.flatMap { valores(claims, it) } + nacion).distinct()
        }.toMap()
    }

    /**
     * Los países de cada uno de [articulos], pidiéndolos con [pedir] en lotes de [LOTE]: una sola vuelta. Si una
     * petición falla, lanza el error (y quien llama sigue sin Wikidata).
     */
    fun paisesDe(articulos: Collection<String>, pedir: (String) -> String): Map<String, List<String>> =
        articulos.filter { esId(it) }.distinct().sorted().chunked(LOTE).fold(emptyMap()) { acc, lote -> acc + leer(pedir(url(lote))) }
}
