package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG5
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Los calendarios del grupo 5 (tanda A5): Tomás, Ochiyo, Marie-Jeanne, Jean-Baptiste y el naturalista. Las pruebas
 * generales (forma, certeza, fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado
 * en [Calendarios]; aquí va lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG5Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun entre(a: String, b: String) = MesDia.indice(b) - MesDia.indice(a)
    /** Los días entre dos fechas «MM-DD» de dos años, contados con los bisiestos de verdad. */
    private fun entreAnios(anioA: Int, a: String, anioB: Int, b: String) =
        ChronoUnit.DAYS.between(LocalDate.parse("%04d-%s".format(anioA, a)), LocalDate.parse("%04d-%s".format(anioB, b))).toInt()
    /** El estado de [c] es [clave] del día [desde] al [hasta] (incluidos), y no lo es el día antes ni (si [despues]) el día después. */
    private fun estadoEntre(c: Calendario, clave: String, desde: String, hasta: String, despues: Boolean = true) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("${c.vida}: el día antes de $desde", ficha(c, a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("${c.vida}, $d", clave, ficha(c, d.toString()).estado); d = d.plusDays(1) }
        if (despues) assertTrue("${c.vida}: el día después de $hasta", ficha(c, b.plusDays(1).toString()).estado != clave)
    }
    /** La Pascua gregoriana (algoritmo anónimo de Meeus, Jones y Butcher), como «MM-DD». */
    private fun pascuaGregoriana(y: Int): String {
        val a = y % 19; val b = y / 100; val c = y % 100; val d = b / 4; val e = b % 4; val f = (b + 8) / 25; val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30; val i = c / 4; val k = c % 4; val l = (32 + 2 * e + 2 * i - h - k) % 7; val m = (a + 11 * h + 22 * l) / 451
        return "%02d-%02d".format((h + l - 7 * m + 114) / 31, (h + l - 7 * m + 114) % 31 + 1)
    }

    @Test fun estanLosCincoYCadaUnoConSuTema() {
        assertEquals(listOf("grumete", "posadera", "lavandera", "granadero", "naturalista"), AnioG5.TODOS.map { it.vida })
        val temas = mapOf("grumete" to "pirata", "posadera" to "edo", "lavandera" to "revolucion", "granadero" to "napoleon", "naturalista" to "botanico")
        AnioG5.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], OtrasVidas.porId(c.vida)!!.tema)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun tomasCarenaCadaAnioYLasNoticiasLleganDespues() {
        val c = AnioG5.TOMAS
        (2027..2031).forEach { y ->
            estadoEntre(c, "cayo", "$y-03-10", "$y-03-23")
            assertTrue("$y: el casco, sucio antes de la carena", ficha(c, "$y-03-09").arcos.getValue("casco") >= 0.9)
            assertEquals(0.0, ficha(c, "$y-03-24").arcos.getValue("casco"), 0.0)
        }
        estadoEntre(c, "nassau", "2027-06-28", "2027-07-23")
        assertEquals("07-22", fechas(c, "rogers")[0])
        // Cada noticia llega después de lo que cuenta (fechas julianas, como las de las páginas).
        mapOf("barbanegra" to "11-22", "rackham_preso" to "10-22", "rackham_ahorcado" to "11-18", "vane" to "03-29", "roberts_muerto" to "02-10", "william" to "08-22")
            .forEach { (id, real) -> val f = fechas(c, id).filterNotNull().single(); assertTrue("$id: $f", entre(real, f) > 0) }
    }

    @Test fun elAnioDeOchiyoEsLunar() {
        val c = AnioG5.OCHIYO
        // El año nuevo: doce lunas (354 o 355 días) o trece (383 a 385) después del anterior.
        val f = fechas(c, "shogatsu")
        (0..3).forEach { i -> val d = entreAnios(1830 + i, f[i]!!, 1831 + i, f[i + 1]!!); assertTrue("${1830 + i}: $d días", d in 354..355 || d in 383..385) }
        c.temporadas.indices.forEach { i ->
            val n = "Temporada ${i + 1}"
            // Su día libre de invierno es el 16 del primer mes; Tanabata, el 7 del séptimo; Bon empieza el 13; el día libre de verano, el 16.
            assertEquals(n, 15, entreAnios(1830 + i, fechas(c, "shogatsu")[i]!!, 1830 + i, fechas(c, "yabuiri")[i]!!))
            assertEquals(n, 6, entre(fechas(c, "tanabata")[i]!!, fechas(c, "bon")[i]!!))
            assertEquals(n, 3, entre(fechas(c, "bon")[i]!!, fechas(c, "yabuiri7")[i]!!))
        }
        estadoEntre(c, "okage", "2027-05-01", "2027-08-11")
        assertTrue("El hambre de Tenpō sube el arroz", ficha(c, "2030-12-31").arcos.getValue("arroz") - ficha(c, "2030-06-01").arcos.getValue("arroz") >= 0.4)
    }

    @Test fun aMarieJeanneSeLeApaganLasFiestas() {
        val c = AnioG5.MARIE_JEANNE
        c.temporadas.indices.forEach { i ->
            val n = "${1789 + i}"
            assertEquals(n, pascuaGregoriana(1789 + i), fechas(c, "pascua")[i])
            assertEquals("$n: el carnaval", -47, entreAnios(1789 + i, fechas(c, "pascua")[i]!!, 1789 + i, fechas(c, "carnaval")[i]!!))
            fechas(c, "fete_dieu")[i]?.let { assertEquals("$n: la Fête-Dieu", 60, entreAnios(1789 + i, fechas(c, "pascua")[i]!!, 1789 + i, it)) }
        }
        // La procesión deja de salir en 1792; Navidad y Todos los Santos, en 1793; el aniversario de la Bastilla empieza en 1791.
        assertEquals(listOf(true, true, true, false, false), fechas(c, "fete_dieu").map { it != null })
        assertEquals(listOf(true, true, true, true, false), fechas(c, "navidad").map { it != null })
        assertEquals(listOf(false, false, true, true, true), fechas(c, "aniversario").map { it != null })
        estadoEntre(c, "helada", "2027-01-01", "2027-01-19")
        estadoEntre(c, "marcha", "2027-10-05", "2027-10-06")
        assertEquals(1.0, ficha(c, "2027-07-14").arcos.getValue("pan"), 0.0)
        assertTrue(ficha(c, "2031-09-29").arcos.getValue("pan") > ficha(c, "2029-06-01").arcos.getValue("pan"))
    }

    @Test fun jeanBaptisteVaDeRusiaASuPueblo() {
        val c = AnioG5.JEAN_BAPTISTE
        listOf(2, 3, 4).forEach { i -> assertEquals("${1812 + i}", pascuaGregoriana(1812 + i), fechas(c, "pascua")[i]) }
        estadoEntre(c, "moscu", "2027-09-14", "2027-10-18")
        estadoEntre(c, "retirada", "2027-10-19", "2027-12-13")
        estadoEntre(c, "pueblo", "2029-06-20", "2030-04-04")
        estadoEntre(c, "pueblo", "2030-07-20", "2031-12-31", despues = false)
        // La compañía se deshace en la retirada.
        assertTrue(ficha(c, "2027-12-14").arcos.getValue("compania") <= 0.2)
        assertEquals("06-18", fechas(c, "waterloo")[3]); assertEquals("06-24", fechas(c, "niemen")[0])
    }

    @Test fun elNaturalistaSigueElViaje() {
        val c = AnioG5.NATURALISTA
        // Las fechas del viaje, como las da la página.
        listOf(Triple(0, "bahia", "02-28"), Triple(0, "punta_alta", "09-22"), Triple(0, "fuego", "12-18"), Triple(1, "mision", "01-23"), Triple(1, "rosas", "08-12"),
            Triple(2, "valparaiso", "07-23"), Triple(3, "terremoto", "02-20"), Triple(3, "concepcion", "03-04"), Triple(3, "galapagos", "09-15"),
            Triple(4, "keeling", "04-01"), Triple(4, "falmouth", "10-02")).forEach { (i, id, f) -> assertEquals(id, f, fechas(c, id)[i]) }
        estadoEntre(c, "tierra", "2027-04-25", "2027-07-04")
        estadoEntre(c, "islas", "2030-09-15", "2030-10-19")
        estadoEntre(c, "casa", "2031-10-05", "2031-12-31", despues = false)
        // Los cajones se vacían con cada envío a Inglaterra.
        assertTrue(ficha(c, "2027-08-18").arcos.getValue("cajones") - ficha(c, "2027-08-19").arcos.getValue("cajones") >= 0.3)
        assertTrue(ficha(c, "2028-11-13").arcos.getValue("cajones") - ficha(c, "2028-11-14").arcos.getValue("cajones") >= 0.3)
    }
}
