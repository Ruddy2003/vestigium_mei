package io.github.ruddy2003.vestigium.historia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.time.LocalDate

/**
 * «Hoy en la historia» (tanda H1): el lector de Wikipedia con una muestra guardada de su respuesta
 * (`test/resources/historia/eventos_11_13.json`, recortada de la del 13 de noviembre), y la caché por fecha.
 */
class EfemeridesPrueba {
    private val muestra = javaClass.getResource("/historia/eventos_11_13.json")!!.readText()
    private fun carpeta(): File = Files.createTempDirectory("historia").toFile()
    private val hoy = LocalDate.of(2026, 11, 13)

    @Test fun leeLosEventosDelMasAntiguoAlMasReciente() {
        val l = Efemerides.leer(muestra)
        assertEquals(6, l.size)
        assertEquals(listOf(-7, 619, 1918, 2015, 2024, 2024), l.map { it.anio })
        // A igual año, en el orden de Wikipedia.
        assertTrue(l[4].texto.startsWith("Se produce una explosión"))
        assertTrue(l[5].texto.startsWith("En Argentina"))
    }

    @Test fun elTextoEmpiezaConMayusculaYSinEspaciosDeMas() {
        val l = Efemerides.leer(muestra)
        l.forEach { e -> assertTrue(e.texto, e.texto.first().isUpperCase() || !e.texto.first().isLetter()); assertEquals(e.texto, e.texto.trim()); assertFalse(e.texto.contains("  ")) }
        assertEquals("En todo el norte", Efemerides.limpiar("  en   todo el norte ").take(16))
    }

    @Test fun cadaEventoTraeSusArticulosConDireccionDeEscritorio() {
        val l = Efemerides.leer(muestra)
        assertTrue(l.first { it.anio == 2024 }.enlaces.isEmpty() || l.last().enlaces.isNotEmpty())
        assertEquals(0, l.first { it.texto.startsWith("Se produce una explosión") }.enlaces.size)
        // El de 619 enlaza cuatro artículos en la muestra: se guardan tres.
        val sevilla = l.first { it.anio == 619 }
        assertEquals(Efemerides.ENLACES_POR_EVENTO, sevilla.enlaces.size)
        l.flatMap { it.enlaces }.forEach { assertTrue(it.url, it.url.startsWith("https://es.wikipedia.org/wiki/")); assertTrue(it.titulo.isNotBlank()) }
        assertEquals("Atentados de París de noviembre de 2015", l.first { it.anio == 2015 }.enlaces.first().titulo)
    }

    @Test fun soloSeMandaElMesYElDia() {
        assertEquals("https://es.wikipedia.org/api/rest_v1/feed/onthisday/events/02/29", Efemerides.url(2, 29))
        assertEquals("https://es.wikipedia.org/api/rest_v1/feed/onthisday/events/11/13", Efemerides.url(11, 13))
        assertFalse(Efemerides.url(11, 13).contains('?'))
        assertEquals("https://es.wikipedia.org/wiki/13_de_noviembre", Efemerides.paginaDelDia(11, 13))
        assertEquals("1 de enero", Efemerides.nombreDelDia(1, 1))
    }

    @Test fun losAniosAntesDeCristo() {
        assertEquals("7 a. C.", Efemerides.nombreDelAnio(-7))
        assertEquals("1 a. C.", Efemerides.nombreDelAnio(-1))
        assertEquals("619", Efemerides.nombreDelAnio(619))
    }

    @Test fun unaRespuestaRaraNoRompe() {
        assertTrue(Efemerides.leer("{}").isEmpty())
        assertTrue(Efemerides.leer("""{"events":[{"year":2000},{"text":"sin año"},{"year":1990,"text":"   "}]}""").isEmpty())
        assertEquals(1990, Efemerides.leer("""{"events":[{"year":1990,"text":"algo","pages":[{"titles":{}}]}]}""").single().anio)
    }

    @Test fun loGuardadoSirveTreintaDiasSinVolverAPedir() {
        val dir = carpeta(); var pedidas = 0
        val pedir: (String) -> String = { u -> pedidas++; assertEquals(Efemerides.url(11, 13), u); muestra }
        val r1 = Efemerides.delDia(dir, hoy, hoy, pedir)
        assertEquals(1, pedidas); assertEquals(6, r1.eventos.size); assertFalse(r1.sinConexion)
        val r2 = Efemerides.delDia(dir, hoy, hoy.plusDays(29), pedir)
        assertEquals(1, pedidas); assertEquals(hoy, r2.bajado)
        Efemerides.delDia(dir, hoy, hoy.plusDays(30), pedir)
        assertEquals(2, pedidas)
    }

    @Test fun sinConexionMuestraLoGuardadoAunqueSeaViejo() {
        val dir = carpeta()
        Efemerides.delDia(dir, hoy, hoy) { muestra }
        val r = Efemerides.delDia(dir, hoy, hoy.plusYears(1)) { throw IOException("sin red") }
        assertTrue(r.sinConexion); assertEquals(6, r.eventos.size); assertEquals(hoy, r.bajado); assertNotNull(r.error)
        val vacio = Efemerides.delDia(carpeta(), hoy, hoy) { throw IOException("sin red") }
        assertTrue(vacio.sinConexion); assertTrue(vacio.eventos.isEmpty()); assertNull(vacio.bajado)
    }

    @Test fun unaRespuestaVaciaNoPisaLoBueno() {
        val dir = carpeta()
        Efemerides.delDia(dir, hoy, hoy) { muestra }
        val r = Efemerides.delDia(dir, hoy, hoy.plusDays(40)) { """{"events":[]}""" }
        assertEquals(6, r.eventos.size); assertTrue(r.sinConexion)
        assertEquals(hoy.toString(), Efemerides.guardado(dir, 11, 13)!!.bajado)
    }

    @Test fun elTrabajoDejaListaLaFechaDeMananaYPoda() {
        val dir = carpeta(); val pedidas = mutableListOf<String>()
        // Una fecha vieja y otra vigente, ajenas a hoy y a mañana.
        Efemerides.delDia(dir, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 1)) { muestra }
        Efemerides.delDia(dir, LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 1)) { muestra }
        assertTrue(Efemerides.prepararManana(dir, hoy) { u -> pedidas += u; muestra })
        assertEquals(listOf(Efemerides.url(11, 14)), pedidas)
        assertNotNull(Efemerides.guardado(dir, 11, 14))
        assertNull("la vieja se borra", Efemerides.guardado(dir, 3, 1))
        assertNotNull("la vigente se queda", Efemerides.guardado(dir, 11, 1))
        // Otra vez el mismo día: ya está, no se pide.
        assertFalse(Efemerides.prepararManana(dir, hoy) { u -> pedidas += u; muestra })
        assertEquals(1, pedidas.size)
    }

    @Test fun elVeintinueveDeFebrero() {
        val dir = carpeta()
        val r = Efemerides.delDia(dir, LocalDate.of(2028, 2, 29), LocalDate.of(2028, 2, 29)) { u -> assertTrue(u.endsWith("/02/29")); muestra }
        assertEquals(6, r.eventos.size)
        assertNotNull(Efemerides.guardado(dir, 2, 29))
    }
}
