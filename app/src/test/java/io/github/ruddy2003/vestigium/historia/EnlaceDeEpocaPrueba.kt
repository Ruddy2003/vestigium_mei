package io.github.ruddy2003.vestigium.historia

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.Epocas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * «En la época de X ›» de punta a punta (tanda H3), con acontecimientos de verdad: una muestra recortada de lo que
 * Wikipedia dio para el 13 de noviembre, el 3 de febrero, el 20 de julio y el 16 de septiembre
 * (`eventos_h3.json`), y lo que Wikidata dijo de sus artículos (`wikidata_h3.json`). Dice qué acontecimiento va a quién
 * con Wikidata, y qué pasa sin él.
 */
class EnlaceDeEpocaPrueba {
    private val eventos = Efemerides.leer(javaClass.getResource("/historia/eventos_h3.json")!!.readText())
    private val paises = Wikidata.leer(javaClass.getResource("/historia/wikidata_h3.json")!!.readText())

    private fun conCuento(tema: String) = OtrasVidas.visibles(tema).filter { Calendarios.de(it.id) != null && TodasLasPiezas.de(it.id) != null }

    /** El evento como lo lee la regla, igual que `acontecimiento()` de la pantalla. */
    private fun acontecimiento(e: Efemerides.Evento, conWikidata: Boolean) = Epocas.Acontecimiento(e.anio, e.texto,
        e.enlaces.map { l -> Epocas.Articulo(l.titulo, l.wikidata, if (conWikidata) l.wikidata?.let { paises[it] } else null) })

    /** A quién va cada evento de la muestra en [tema]: «año: vida (motivo)», o «año: —». */
    private fun reparto(tema: String, conWikidata: Boolean): List<String> = eventos.map { e ->
        val r = Epocas.vidaDelAcontecimiento(acontecimiento(e, conWikidata), conCuento(tema), tema)
        "${e.anio}: " + (r?.let { "${it.first.id} (${it.second.name.lowercase()})" } ?: "—")
    }

    @Test fun laMuestraEsLaEsperada() {
        assertEquals(13, eventos.size)
        // Cada artículo trae su identificador, y Wikidata contestó por todos.
        eventos.flatMap { it.enlaces }.forEach { l -> assertEquals(l.titulo, true, l.wikidata in paises) }
    }

    @Test fun conWikidataEnHelenico() {
        assertEquals(listOf(
            "1304: medieval (pais)",      // Eduardo I toma Stirling: Inglaterra y el Reino Unido
            "1523: tenochtitlan (pais)",  // Alvarado sale de Tenochtitlán: México en minoría, pero el texto lo nombra
            "1810: —",                    // el Grito de Dolores es de México, pero nadie mexicano vive entonces
            "1825: —",                    // el Mar del Norte tiene demasiados países
            "1887: —",                    // Martí en Chicago: su doble nacionalidad no cuenta
            "1896: —",                    // Cuba: un río de la frontera no la hace de México
            "1903: —",                    // Ford, en Estados Unidos: en Helénico no hay nadie de allí
            "1923: hacienda (pais)",      // matan a Pancho Villa: tres artículos de México y uno de Estados Unidos
            "1940: cdmx1960 (pais)",      // la Universidad de Colima: Wikidata dice México
            "1942: cdmx1960 (mundial)",   // Guadalcanal, en la Segunda Guerra Mundial: Rosa tenía ocho años
            "1950: —",                    // Leopoldo III: nombra la guerra, pero ya había terminado
            "1957: cdmx1960 (pais)",      // un teatro en Monterrey
            "1969: —",                    // el Apolo 11: Rosa ya no está en su temporada 5
        ), reparto("heleno", conWikidata = true))
    }

    @Test fun conWikidataEnArtDecoYEnSovietico() {
        val deco = reparto("deco", conWikidata = true)
        assertEquals("1903: pianista (pais)", deco.first { it.startsWith("1903") })
        assertEquals("1923: hacienda (pais)", deco.first { it.startsWith("1923") })
        val urss = reparto("urss", conWikidata = true)
        assertEquals("1969: obrera_moscu (mundial)", urss.first { it.startsWith("1969") })
    }

    @Test fun sinWikidataSeUsanLasPalabras() {
        val r = reparto("heleno", conWikidata = false)
        assertEquals("1923: hacienda (palabras)", r.first { it.startsWith("1923") }) // el texto dice «(México)»
        assertEquals("1957: cdmx1960 (palabras)", r.first { it.startsWith("1957") }) // «Monterrey, México»
        assertEquals("1304: medieval (palabras)", r.first { it.startsWith("1304") }) // «Eduardo I de Inglaterra»
        assertEquals("1942: cdmx1960 (mundial)", r.first { it.startsWith("1942") })
        // Lo que se pierde sin Wikidata: el texto de Colima no nombra México.
        assertEquals("1940: —", r.first { it.startsWith("1940") })
        // Y nada que no fuera de nadie pasa a ser de alguien.
        listOf("1810", "1825", "1887", "1896", "1950", "1969").forEach { y -> assertEquals("$y: —", r.first { it.startsWith(y) }) }
    }
}
