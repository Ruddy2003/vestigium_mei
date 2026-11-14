package io.github.ruddy2003.vestigium.historia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/** Lo que se le pregunta a Wikidata y cómo se lee (tanda H3), con una muestra recortada de su respuesta. */
class WikidataPrueba {
    private val muestra = javaClass.getResource("/historia/wikidata_h3.json")!!.readText()

    @Test fun soloVanIdentificadoresDeArticulos() {
        val u = Wikidata.url(listOf("Q96", "Q192278"))
        assertEquals("https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=claims&ids=Q96%7CQ192278", u)
        assertThrows(IllegalArgumentException::class.java) { Wikidata.url(listOf("México")) }
        assertThrows(IllegalArgumentException::class.java) { Wikidata.url(emptyList()) }
        assertThrows(IllegalArgumentException::class.java) { Wikidata.url((1..51).map { "Q$it" }) }
    }

    @Test fun leeElPaisDeCadaArticulo() {
        val p = Wikidata.leer(muestra)
        assertEquals(listOf("Q96"), p["Q655"]) // Chihuahua (P17)
        assertEquals(listOf("Q96"), p["Q192278"]) // Pancho Villa (P27)
        assertEquals(listOf("Q30"), p["Q294931"]) // William Randolph Hearst (P27)
        assertTrue("el Mar del Norte tiene muchos países", p["Q1693"]!!.size > 3)
        assertEquals("la marejada no dice país", emptyList<String>(), p["Q121742"])
    }

    @Test fun laNacionalidadSoloCuentaSiEsUna() {
        val p = Wikidata.leer(muestra)
        // José Martí sale como español y mexicano: no cuenta ninguna.
        assertEquals(emptyList<String>(), p["Q103285"])
        // Hernán Cortés, con tres, tampoco.
        assertEquals(emptyList<String>(), p["Q7326"])
    }

    @Test fun pideEnLotesDeCincuentaYUnaSolaVuelta() {
        val pedidas = mutableListOf<String>()
        val ids = (1..120).map { "Q$it" } + "no es un id"
        Wikidata.paisesDe(ids) { u -> pedidas += u; """{"entities":{}}""" }
        assertEquals(3, pedidas.size)
        assertTrue(pedidas.all { it.startsWith("https://www.wikidata.org/w/api.php?") && !it.contains("no es") })
    }

    @Test fun unaRespuestaRaraNoRompe() {
        assertTrue(Wikidata.leer("{}").isEmpty())
        assertTrue(Wikidata.leer("""{"entities":{"Q1":{"missing":""},"x":{}}}""").isEmpty())
        assertEquals(emptyList<String>(), Wikidata.leer("""{"entities":{"Q2":{"id":"Q2"}}}""")["Q2"])
        assertFalse(Wikidata.esId("Q0")); assertTrue(Wikidata.esId("Q96"))
    }
}
