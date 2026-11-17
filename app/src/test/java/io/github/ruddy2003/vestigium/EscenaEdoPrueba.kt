package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.CasaDeTeEdo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEdo
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosEdo
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro de Japón Edo (tanda S11) cuenta lo mismo que el cuento de Ochiyo: los peregrinos de Ise, las procesiones de
 * los daimios, el precio del arroz, las fiestas y las estampas salen de la ficha del día, y aquí se comparan con su
 * calendario.
 */
class EscenaEdoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "posadera")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeOchiyo() {
        assertEquals(listOf("posadera"), EscenaEdo.vidas)
        assertEquals("posadera", EscenaEdo.vida("2027-05-10"))
        assertTrue(EscenaEdo.tallaDelGato in 0.8f..1.8f)
    }

    @Test fun losPeregrinosDeIseSoloEnSuTemporada() {
        val dias = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.estado == "okage", EscenaEdo.okage(m))
            if (EscenaEdo.okage(m)) { dias[d.year] = (dias[d.year] ?: 0) + 1; assertEquals("$d", 1, m.ficha.temporada) }
        }
        // De mayo a mediados de agosto de la temporada 1 (en 2026 se ve la temporada 1 de avance).
        assertTrue("${dias[2027]}", (dias[2027] ?: 0) in 95..110); assertEquals(null, dias[2028])
        assertTrue(EscenaEdo.okage(momento("2027-05-01"))); assertFalse(EscenaEdo.okage(momento("2027-08-12")))
    }

    @Test fun lasProcesionesPreparanElCamino() {
        for (anio in 2027..2031) { assertTrue("$anio", EscenaEdo.procesion(momento("$anio-05-10"))); assertTrue("$anio", EscenaEdo.procesion(momento("$anio-10-28"))); assertFalse("$anio", EscenaEdo.procesion(momento("$anio-05-11"))) }
        assertTrue(EscenaEdo.procesion(momento("2029-09-16")))                              // la comitiva del shogun con el pintor
    }

    @Test fun elArrozSubeConElHambre() {
        cadaDia { d, m -> assertTrue("$d", EscenaEdo.sacos(m) in listOf(1, 2, 4)); assertEquals("$d", m.arco("arroz") >= 0.7f, EscenaEdo.mijo(m)) }
        assertEquals(4, EscenaEdo.sacos(momento("2027-06-01"))); assertFalse(EscenaEdo.mijo(momento("2029-06-01")))
        assertEquals(1, EscenaEdo.sacos(momento("2030-12-20"))); assertTrue(EscenaEdo.mijo(momento("2030-12-20"))); assertTrue(EscenaEdo.mijo(momento("2031-06-01")))
        // Nunca baja de un año para otro dentro de las temporadas.
        assertTrue(EscenaEdo.sacos(momento("2031-12-01")) <= EscenaEdo.sacos(momento("2027-12-01")))
    }

    @Test fun lasEstampasSeQuedanDesdeSuDia() {
        assertFalse(EscenaEdo.estampaDeLaOla(momento("2028-04-27"))); assertTrue(EscenaEdo.estampaDeLaOla(momento("2028-04-28"))); assertTrue(EscenaEdo.estampaDeLaOla(momento("2031-01-01")))
        assertFalse(EscenaEdo.estampaDeLaPosta(momento("2030-12-04"))); assertTrue(EscenaEdo.estampaDeLaPosta(momento("2030-12-05")))
        assertTrue(EscenaEdo.boda(momento("2031-11-08"))); assertFalse(EscenaEdo.boda(momento("2031-11-09")))
    }

    @Test fun lasFiestasYElTiempo() {
        assertTrue(EscenaEdo.bon(momento("2027-08-30"))); assertTrue(EscenaEdo.tanabata(momento("2027-08-24"))); assertTrue(EscenaEdo.tango(momento("2027-06-25")))
        assertTrue(EscenaEdo.tsukimi(momento("2027-10-01"))); assertTrue(EscenaEdo.anioNuevo(momento("2027-01-25")))
        assertTrue(EscenaEdo.rioCerrado(momento("2028-09-08"))); assertTrue(EscenaEdo.llueve(momento("2028-09-08")))
        assertTrue(EscenaEdo.nieveDelFuji(momento("2029-10-10"))); assertTrue(EscenaEdo.nieveEnElFuji(momento("2029-10-10")) > EscenaEdo.nieveEnElFuji(momento("2028-10-10")))
        cadaDia { d, m -> assertTrue("$d", EscenaEdo.nieveEnElFuji(m) in 0f..1f) }
        for (anio in 2027..2031) {
            val estados = (1..365).map { EscenaEdo.arrozal(momento(LocalDate.ofYearDay(anio, it).toString())) }.toSet()
            assertEquals("$anio", EscenaEdo.Arrozal.entries.toSet(), estados)
            assertEquals(EscenaEdo.Arrozal.DORADO, EscenaEdo.arrozal(momento("$anio-09-20"))); assertEquals(EscenaEdo.Arrozal.SECO, EscenaEdo.arrozal(momento("$anio-01-20")))
        }
    }

    @Test fun elFarolYElFogonSiguenSuDia() {
        cadaDia { d, _ ->
            assertTrue("$d", EscenaEdo.farol(momento(d.toString(), 21.4))); assertFalse("$d", EscenaEdo.farol(momento(d.toString(), 23.0))); assertFalse("$d", EscenaEdo.farol(momento(d.toString(), 13.0)))
            assertTrue("$d", EscenaEdo.fogon(momento(d.toString(), 13.0)) >= 0.9f)
            assertTrue("$d", EscenaEdo.regado(momento(d.toString(), 6.0)) || EscenaEdo.nevado(momento(d.toString(), 6.0)))
            assertTrue("$d", CasaDeTeEdo.focos(momento(d.toString(), 21.4)).isNotEmpty())
        }
    }

    @Test fun losObjetosYLasAnclasDelGato() {
        assertEquals(Calendarios.de("posadera")!!.objetos.toSet(), ObjetosEdo.TODOS.toSet())
        for (dia in listOf("2027-05-05", "2027-09-05", "2028-01-05", "2031-11-08")) for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento(dia, h); val anclas = EscenaEdo.anclas(m)
            for (a in anclas) assertTrue("$dia $h ${a.tipo}", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO && a.y in 0f..ALTO)
            val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
            assertTrue(suelo.hasta - suelo.x >= 110f)
            assertEquals(1, anclas.count { it.tipo == TipoAncla.OBJETO })
        }
    }
}
