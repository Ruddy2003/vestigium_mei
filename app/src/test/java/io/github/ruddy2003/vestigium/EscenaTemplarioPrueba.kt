package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.CastilloTemplario
import io.github.ruddy2003.vestigium.ui.escenas.EscenaTemplario
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosTemplario
import io.github.ruddy2003.vestigium.ui.escenas.PatioTemplario
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
 * El cuadro de Templario (tanda S11) cuenta lo mismo que el cuento de Bernat: la torre del sur que se levanta, el campo
 * que se siega, la nieve, el ataque, la enfermería y el mes en Acre salen de la ficha del día, y aquí se comparan con su
 * calendario.
 */
class EscenaTemplarioPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "sargento")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeBernat() {
        assertEquals(listOf("sargento"), EscenaTemplario.vidas)
        assertEquals("sargento", EscenaTemplario.vida("2027-05-10"))
        assertTrue(EscenaTemplario.tallaDelGato in 0.8f..1.8f)
    }

    @Test fun laTorreDelSurSubeComoDiceElCuento() {
        var antes = Float.MAX_VALUE; var anterior = ""
        cadaDia { d, m ->
            val alto = EscenaTemplario.altoDeLaTorre(m); val arco = EscenaTemplario.muralla(m)
            assertEquals("$d", m.ficha.arcos["muralla"]!!.toFloat().coerceIn(0f, 1f), arco, 0.0001f)
            // La torre nunca baja (dentro de una misma temporada que avanza; el año siguiente sigue donde iba).
            if (d.year in 2027..2031 && d != LocalDate.of(2027, 1, 1) && anterior.isNotEmpty()) assertTrue("$d: la torre bajó", alto <= antes + 0.001f)
            antes = alto; anterior = d.toString()
            // Lo que dice el cuento: un muñón por debajo del muro mientras el arco no llega a 0.6; por encima desde ahí.
            if (arco < 0.6f) assertTrue("$d: debería ser un muñón", alto > CastilloTemplario.ALMENAS) else assertTrue("$d: debería pasar el muro", alto <= CastilloTemplario.ALMENAS)
            assertEquals("$d", arco >= 0.8f - 1e-4f, EscenaTemplario.torreTerminada(m))
            assertEquals("$d", EscenaTemplario.torreTerminada(m), EscenaTemplario.obraEnElEste(m))
            assertTrue("$d", alto in CastilloTemplario.CIMA..CastilloTemplario.MUNON)
        }
        // Se termina el día en que se pone la cruz (suceso `fin_torre`, 10 de julio de la temporada 4).
        assertFalse(EscenaTemplario.torreTerminada(momento("2030-07-09"))); assertTrue(EscenaTemplario.torreTerminada(momento("2030-07-10")))
        assertTrue(EscenaTemplario.finDeLaTorre(momento("2030-07-10"))); assertFalse(EscenaTemplario.finDeLaTorre(momento("2030-07-11")))
        assertEquals(CastilloTemplario.MUNON, EscenaTemplario.altoDeLaTorre(momento("2027-01-01")), 0.01f)
    }

    @Test fun losEstadosSeVenSoloSusDias() {
        var herido = 0; var acre = 0
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.estado == "herido", EscenaTemplario.herido(m))
            assertEquals("$d", m.ficha.estado == "acre", EscenaTemplario.enAcre(m))
            if (EscenaTemplario.herido(m)) { herido++; assertEquals("$d", 2, m.ficha.temporada); assertTrue("$d", EscenaTemplario.muleta(momento(d.toString(), 10.0))); assertFalse("$d", EscenaTemplario.muleta(momento(d.toString(), 22.0))) }
            else assertFalse("$d", EscenaTemplario.muleta(m))
            if (EscenaTemplario.enAcre(m)) { acre++; assertEquals("$d", 3, m.ficha.temporada); assertFalse("$d", EscenaTemplario.suEscudo(m)); assertEquals("$d", 3, EscenaTemplario.lanzasEnElArmero(m)) }
        }
        // Seis o siete semanas en la enfermería y un mes en Acre: los dos son variantes del patio (decisión 21).
        assertTrue("$herido días herido", herido in 45..56); assertTrue("$acre días en Acre", acre in 30..36)
    }

    @Test fun lasClavesDelCalendarioCambianElCuadro() {
        assertTrue(EscenaTemplario.luto(momento("2027-02-26"))); assertTrue(EscenaTemplario.campanas(momento("2027-03-27")))
        assertTrue(EscenaTemplario.nevado(momento("2028-01-20")))
        val ataque = momento("2028-06-12")
        assertTrue(EscenaTemplario.guerra(ataque)); assertEquals(0, EscenaTemplario.lanzasEnElArmero(ataque)); assertFalse(EscenaTemplario.suEscudo(ataque)); assertTrue(EscenaTemplario.fuegoDeLaTorre(ataque))
        assertEquals(4, EscenaTemplario.lanzasEnElArmero(momento("2028-06-13")))
        assertTrue(EscenaTemplario.polvo(momento("2030-05-28")))
        assertTrue(EscenaTemplario.temblor(momento("2029-01-28"))); assertFalse(EscenaTemplario.grietaRemendada(momento("2029-01-27"))); assertTrue(EscenaTemplario.grietaRemendada(momento("2029-02-10")))
        // Ni en fiesta, ni en luto, ni de noche trabajan los albañiles.
        assertFalse(EscenaTemplario.trabajanEnLaObra(momento("2027-02-26", 10.0))); assertFalse(EscenaTemplario.trabajanEnLaObra(momento("2027-05-05", 21.0)))
    }

    @Test fun elTrigoBrotaDoraYSeSiegaEnMayo() {
        for (anio in 2027..2031) {
            var siega = 0
            var d = LocalDate.of(anio, 1, 1)
            while (d.year == anio) { val m = momento(d.toString()); assertTrue("$d", EscenaTemplario.trigo(m) in 0f..1f); if (EscenaTemplario.siega(m)) siega++; assertFalse("$d", EscenaTemplario.siega(m) && EscenaTemplario.rastrojo(m)); d = d.plusDays(1) }
            assertEquals("$anio", 10, siega)
            assertTrue(EscenaTemplario.siega(momento("$anio-05-10"))); assertTrue(EscenaTemplario.rastrojo(momento("$anio-07-01"))); assertFalse(EscenaTemplario.rastrojo(momento("$anio-02-01")))
            assertTrue(EscenaTemplario.trigo(momento("$anio-02-01")) < EscenaTemplario.trigo(momento("$anio-04-20")))
            assertTrue(EscenaTemplario.nieveDelHermon(momento("$anio-01-15")) > EscenaTemplario.nieveDelHermon(momento("$anio-08-15")))
        }
    }

    @Test fun deNocheSeEnciendenSusLuces() {
        cadaDia { d, _ ->
            val noche = momento(d.toString(), 21.6); val dia = momento(d.toString(), 13.0)
            assertTrue("$d", EscenaTemplario.velas(noche)); assertTrue("$d", EscenaTemplario.lamparaDeLaEnfermeria(noche)); assertTrue("$d", EscenaTemplario.farol(noche))
            assertTrue("$d", PatioTemplario.focos(noche).isNotEmpty())
            assertFalse("$d", EscenaTemplario.farol(dia)); assertFalse("$d", EscenaTemplario.velas(dia))
            assertTrue("$d", EscenaTemplario.brasero(noche) in 0f..1f)
        }
        // El brasero arde las noches frías y no las de verano.
        assertTrue(EscenaTemplario.brasero(momento("2027-12-20", 20.0)) > 0.3f); assertEquals(0f, EscenaTemplario.brasero(momento("2027-07-20", 20.0)), 0f)
    }

    @Test fun losObjetosYLasAnclasDelGato() {
        assertEquals(Calendarios.de("sargento")!!.objetos.toSet(), ObjetosTemplario.TODOS.toSet())
        for (dia in listOf("2027-05-05", "2027-12-20", "2028-01-20", "2030-07-10")) for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento(dia, h); val anclas = EscenaTemplario.anclas(m)
            for (a in anclas) { assertTrue("$dia $h ${a.tipo}", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO && a.y in 0f..ALTO) }
            val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
            assertTrue(suelo.hasta - suelo.x >= 110f)
            assertEquals(1, anclas.count { it.tipo == TipoAncla.OBJETO })
            if (EscenaTemplario.brasero(m) > 0.5f) assertTrue(anclas.any { it.tipo == TipoAncla.FUEGO })
        }
    }
}
