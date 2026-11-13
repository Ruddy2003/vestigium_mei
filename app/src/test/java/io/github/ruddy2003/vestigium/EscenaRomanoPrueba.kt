package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.Embarcadero
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRomano.Puesto
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro de Romano (tanda S1) cuenta lo mismo que el cuento de Cayo: la muralla que se rehace en piedra, la
 * guarnición que mengua, su puesto vacío mientras está en el camino del desfiladero, el río helado y el embarcadero
 * que se lleva la crecida salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaRomanoPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "legionario")!!, hora)

    @Test fun laEscenaEsLaDeCayo() {
        assertEquals(listOf("legionario"), EscenaRomano.vidas)
        assertEquals("legionario", EscenaRomano.vida("2027-05-10"))
    }

    @Test fun laMurallaSubeComoDiceElCuento() {
        // Temporada 1: empalizada hasta el 3 de abril, el día de la primera piedra; a fin de octubre va por 0.3.
        assertEquals(0, EscenaRomano.tramosDePiedra(momento("2027-01-20"))); assertFalse(EscenaRomano.enObra(momento("2027-01-20")))
        assertEquals(0, EscenaRomano.tramosDePiedra(momento("2027-04-03"))); assertTrue(EscenaRomano.enObra(momento("2027-04-03")))
        assertEquals(1, EscenaRomano.tramosDePiedra(momento("2027-10-31"))); assertTrue(EscenaRomano.enObra(momento("2027-10-31")))
        assertTrue("en invierno el andamio se queda puesto", EscenaRomano.enObra(momento("2028-01-15")))
        // Temporada 2: llega a 0.6. Temporada 3: se termina el 31 de octubre, y con ella la torre.
        assertEquals(3, EscenaRomano.tramosDePiedra(momento("2028-10-31")))
        assertFalse(EscenaRomano.torreDePiedra(momento("2029-10-30")))
        assertEquals(EscenaRomano.TRAMOS, EscenaRomano.tramosDePiedra(momento("2029-10-31")))
        assertTrue(EscenaRomano.torreDePiedra(momento("2029-10-31"))); assertFalse(EscenaRomano.enObra(momento("2029-10-31")))
        assertEquals(EscenaRomano.TRAMOS, EscenaRomano.tramosDePiedra(momento("2031-06-01"))); assertFalse(EscenaRomano.enObra(momento("2031-06-01")))
    }

    @Test fun laMurallaNuncaBaja() {
        var antes = 0; var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) { val ahora = EscenaRomano.tramosDePiedra(momento(d.toString())); assertTrue("$d: la piedra no se quita", ahora >= antes); antes = ahora; d = d.plusDays(1) }
    }

    @Test fun suPuestoEstaVacioMientrasEstaEnElCamino() {
        assertEquals(Puesto.DE_GUARDIA, EscenaRomano.puesto(momento("2028-05-31")))
        for (dia in listOf("2028-06-01", "2028-06-20", "2028-07-14")) assertEquals(dia, Puesto.VACIO, EscenaRomano.puesto(momento(dia)))
        assertEquals(Puesto.DE_GUARDIA, EscenaRomano.puesto(momento("2028-07-15")))
        // Con el puesto vacío no hay fuego para el gato, ni a la hora de comer.
        assertFalse(EscenaRomano.anclas(momento("2028-06-20", 12.4)).any { it.tipo == TipoAncla.FUEGO })
        assertTrue(EscenaRomano.anclas(momento("2028-05-31", 12.4)).any { it.tipo == TipoAncla.FUEGO })
    }

    @Test fun lasTiendasMenguanCuandoParteLaVejilacion() {
        assertEquals(18, EscenaRomano.tiendas(momento("2027-05-10"))); assertEquals(5, EscenaRomano.fuegos(momento("2027-05-10")))
        assertEquals(18, EscenaRomano.tiendas(momento("2030-09-24")))
        assertEquals(11, EscenaRomano.tiendas(momento("2030-09-26"))); assertEquals(3, EscenaRomano.fuegos(momento("2030-09-26")))
        assertEquals(11, EscenaRomano.tiendas(momento("2031-02-28")))
        assertEquals(12, EscenaRomano.tiendas(momento("2031-03-02")))            // llegan los reclutas
    }

    @Test fun elDanubioSeHielaCuandoDiceElCuento() {
        // Temporada 2: el 10 de enero se hiela de orilla a orilla y el 18 lo cruzan los sármatas.
        for (dia in listOf("2028-01-10", "2028-01-18", "2028-01-31")) assertTrue(dia, EscenaRomano.helado(momento(dia)))
        assertFalse(EscenaRomano.helado(momento("2028-02-01"))); assertFalse(EscenaRomano.helado(momento("2027-07-10")))
        assertTrue(momento("2028-01-18").con("humo"))
    }

    @Test fun laCrecidaSeLlevaElEmbarcadero() {
        assertEquals(Embarcadero.ENTERO, EscenaRomano.embarcadero(momento("2029-05-19")))
        assertEquals(Embarcadero.ANEGADO, EscenaRomano.embarcadero(momento("2029-05-20")))
        assertEquals(Embarcadero.ROTO, EscenaRomano.embarcadero(momento("2029-05-21"))); assertEquals(Embarcadero.ROTO, EscenaRomano.embarcadero(momento("2029-07-31")))
        assertEquals(Embarcadero.ENTERO, EscenaRomano.embarcadero(momento("2029-08-01")))
        assertEquals(Embarcadero.ENTERO, EscenaRomano.embarcadero(momento("2028-06-10"))); assertEquals(Embarcadero.ENTERO, EscenaRomano.embarcadero(momento("2030-06-10")))
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "legionario")!!
            for (hora in listOf(3.0, 5.0, 7.0, 10.0, 12.4, 13.0, 18.4, 20.0, 21.0, 22.0)) {
                val m = Momento(f, hora)
                assertEquals("$d: el puesto está vacío si y solo si él está en el camino", f.estado == "camino", EscenaRomano.puesto(m) == Puesto.VACIO)
                val muralla = (f.arcos["muralla"] ?: 0.0).toFloat()
                assertEquals("$d: los tramos de piedra siguen al arco", (muralla * EscenaRomano.TRAMOS + 0.001f).toInt(), EscenaRomano.tramosDePiedra(m))
                assertTrue("$d: de 0 a ${EscenaRomano.TRAMOS} tramos", EscenaRomano.tramosDePiedra(m) in 0..EscenaRomano.TRAMOS)
                if (EscenaRomano.enObra(m)) assertTrue("$d: si hay obra, queda algún tramo por hacer", EscenaRomano.tramosDePiedra(m) < EscenaRomano.TRAMOS)
                assertEquals("$d: la torre es de piedra si y solo si la muralla está terminada", muralla >= 0.999f, EscenaRomano.torreDePiedra(m))
                assertEquals("$d: sin andamio si y solo si no ha empezado o ya terminó", muralla >= 0.999f || (muralla <= 0f && !m.con("obra")), !EscenaRomano.enObra(m))
                val guarnicion = (f.arcos["guarnicion"] ?: 0.0).toFloat()
                assertEquals("$d: las tiendas siguen a la guarnición", Math.round(guarnicion * 18f), EscenaRomano.tiendas(m))
                assertTrue("$d: de 0 a 5 fuegos", EscenaRomano.fuegos(m) in 0..5)
                val enero = d.monthValue == 1
                assertEquals("$d: helado solo en enero, si nieva o es el enero del hielo", enero && (f.clima == Clima.NIEVE || m.con("nieve") || (f.temporada == 2 && d.dayOfMonth >= 10)), EscenaRomano.helado(m))
                if (EscenaRomano.embarcadero(m) != Embarcadero.ENTERO) assertEquals("$d: el embarcadero solo falta en la temporada de la crecida", 3, f.temporada)
                // Las anclas del gato caben en el cuadro, y nunca faltan el suelo, el mirador, el objeto y el abrigo.
                val anclas = EscenaRomano.anclas(m)
                for (a in anclas) assertTrue("$d $hora h: ancla ${a.tipo} fuera", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }
                assertTrue("$d: el suelo del gato mide al menos 110", suelo.hasta - suelo.x >= 110f)
                assertEquals("$d $hora h: hay fuego para el gato si y solo si el brasero está encendido", (m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE) && f.estado != "camino", anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d: hay adorno si y solo si es fiesta", m.con("fiesta") || m.con("rosas"), anclas.any { it.tipo == TipoAncla.ADORNO })
                // El gato nunca se sienta a mirar en el tramo que está en obra.
                val mirador = anclas.first { it.tipo == TipoAncla.MIRADOR }
                val tramo = (mirador.x / (EscenaRomano.TORRE / EscenaRomano.TRAMOS)).toInt()
                if (EscenaRomano.enObra(m)) assertTrue("$d: el mirador no está en el tramo en obra", tramo != EscenaRomano.tramosDePiedra(m))
                for (x in EscenaRomano.estorbos(m)) assertTrue("$d: estorbo fuera del suelo", x in suelo.x..suelo.hasta)
            }
            d = d.plusDays(1)
        }
    }
}
