package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAndalus.Telar
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro de Al-Ándalus (tanda S1) cuenta lo mismo que el cuento de Zaynab: la tela del telar, el río, el Ramadán
 * con su anafe apagado de día y sus lámparas de noche, la hueste en el puente y la nieve salen de la ficha del día, y
 * aquí se comparan con su calendario (`AnioG4.kt`).
 */
class EscenaAndalusPrueba {
    private fun momento(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "cordobesa")!!, hora)
    private fun telar(dia: String) = EscenaAndalus.telar(momento(dia, 13.0))

    @Test fun laEscenaEsLaDeZaynab() {
        assertEquals(listOf("cordobesa"), EscenaAndalus.vidas)
        assertEquals("cordobesa", EscenaAndalus.vida("2027-05-05"))
    }

    @Test fun elTelarEstaComoDiceElCuento() {
        // Temporada 1: el 10 de febrero le encargan la tela del califa. Antes, la urdimbre sola.
        assertEquals(Telar.URDIMBRE, telar("2027-01-20")); assertEquals(Telar.URDIMBRE, telar("2027-02-10"))
        assertEquals(Telar.TEJIENDO, telar("2027-05-05")); assertEquals(Telar.TEJIENDO, telar("2028-06-02"))
        // Temporada 2: el 3 de junio la termina y se la llevan. El telar queda desnudo hasta la tela de la boda.
        assertEquals(Telar.DESNUDO, telar("2028-06-03")); assertEquals(Telar.DESNUDO, telar("2029-03-01")); assertEquals(Telar.DESNUDO, telar("2029-08-27"))
        // Temporada 3: el 28 de agosto le dan el telar grande y la tela de la boda.
        assertEquals(Telar.URDIMBRE, telar("2029-08-28"))
        assertEquals(Telar.TEJIENDO, telar("2030-06-01")); assertEquals(Telar.TEJIENDO, telar("2031-08-09"))
        // Temporada 5: el 10 de agosto la termina.
        assertEquals(Telar.DESNUDO, telar("2031-08-10")); assertEquals(Telar.DESNUDO, telar("2031-12-01"))
        // La del califa es carmesí; la de la boda, azul.
        assertFalse(EscenaAndalus.telaDeLaBoda(momento("2028-05-01", 13.0))); assertTrue(EscenaAndalus.telaDeLaBoda(momento("2030-05-01", 13.0)))
    }

    @Test fun laTelaAvanzaYNoRetrocedeMientrasLaTeje() {
        var antes = 0f; var d = LocalDate.of(2027, 2, 10)
        while (d < LocalDate.of(2028, 6, 4)) { val ahora = EscenaAndalus.tela(momento(d.toString(), 13.0)); assertTrue("$d: la tela no puede destejerse", ahora >= antes - 0.0001f); antes = ahora; d = d.plusDays(1) }
        assertEquals(1f, antes, 0.001f)
    }

    @Test fun enRamadanNoSeGuisaDeDiaYDeNocheArdenLasLamparas() {
        // Temporada 1: el Ramadán va del 8 de julio a la víspera de la fiesta, el 7 de agosto.
        val dia = "2027-07-20"
        assertTrue(EscenaAndalus.ramadan(momento(dia, 13.0)))
        assertFalse("en ayunas no se enciende el anafe a mediodía", EscenaAndalus.guisando(momento(dia, 13.0)))
        assertTrue("se guisa al caer la tarde", EscenaAndalus.guisando(momento(dia, 20.0)))
        assertTrue("la mesa, puesta en el patio", EscenaAndalus.mesaPuesta(momento(dia, 20.0)))
        assertTrue(EscenaAndalus.lamparas(momento(dia, 13.0))); assertFalse(EscenaAndalus.lamparasEncendidas(momento(dia, 13.0)))
        assertTrue(EscenaAndalus.lamparasEncendidas(momento(dia, 22.0))); assertTrue(EscenaAndalus.lamparasEncendidas(momento(dia, 2.0)))
        // Fuera del Ramadán: se guisa a mediodía y no hay lámparas.
        assertFalse(EscenaAndalus.ramadan(momento("2027-07-07", 13.0))); assertFalse(EscenaAndalus.ramadan(momento("2027-08-07", 13.0)))
        assertTrue(EscenaAndalus.guisando(momento("2027-05-05", 13.0))); assertFalse(EscenaAndalus.guisando(momento("2027-05-05", 20.0)))
        assertFalse(EscenaAndalus.lamparas(momento("2027-05-05", 22.0)))
    }

    @Test fun elRioSubeEnInviernoYBajaEnVerano() {
        assertEquals(0, EscenaAndalus.islas(momento("2027-03-15", 13.0))); assertEquals(1f, EscenaAndalus.crecida(momento("2027-03-15", 13.0)), 0.001f)
        assertEquals(5, EscenaAndalus.islas(momento("2027-09-15", 13.0))); assertEquals(0f, EscenaAndalus.crecida(momento("2027-09-15", 13.0)), 0.001f)
        assertTrue(EscenaAndalus.islas(momento("2027-07-20", 13.0)) in 1..4)
    }

    @Test fun losDiasSenaladosSeVen() {
        assertTrue("la boda de Juliana", EscenaAndalus.fiesta(momento("2027-05-12", 13.0)))
        assertTrue("la muerte del hijo del califa", EscenaAndalus.luto(momento("2027-06-02", 13.0)))
        assertTrue("la seda nueva", EscenaAndalus.sedaNueva(momento("2027-06-12", 13.0)))
        assertTrue("los vikingos en la costa", EscenaAndalus.hueste(momento("2028-07-10", 13.0)))
        assertTrue("Galib sale hacia África", EscenaAndalus.hueste(momento("2030-04-25", 13.0)))
        assertTrue("nieva en Córdoba", EscenaAndalus.nevado(momento("2029-01-28", 13.0)))
        assertFalse(EscenaAndalus.hueste(momento("2027-05-05", 13.0))); assertFalse(EscenaAndalus.luto(momento("2027-05-05", 13.0)))
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "cordobesa")!!
            val tela = (f.arcos["tela"] ?: 0.0).toFloat(); val rio = (f.arcos["rio"] ?: 0.0).toFloat()
            for (hora in listOf(3.0, 5.2, 7.0, 10.0, 13.0, 18.0, 20.0, 22.0)) {
                val m = Momento(f, hora)
                assertEquals("$d: el telar, desnudo si y solo si la pieza está terminada", tela >= 0.999f, EscenaAndalus.telar(m) == Telar.DESNUDO)
                assertEquals("$d: la tela del cuadro es la del arco", tela.coerceIn(0f, 1f), EscenaAndalus.tela(m), 0.0001f)
                assertEquals("$d: el río del cuadro es el del arco", rio.coerceIn(0f, 1f), EscenaAndalus.rio(m), 0.0001f)
                assertTrue("$d: de 0 a 5 islas", EscenaAndalus.islas(m) in 0..5)
                assertFalse("$d: con el río crecido no asoman islas", EscenaAndalus.crecida(m) > 0.2f && EscenaAndalus.islas(m) > 0)
                assertEquals("$d: es Ramadán en el cuadro si y solo si lo es en la ficha", f.estado == "ramadan", EscenaAndalus.ramadan(m))
                if (f.estado == "ramadan") {
                    assertFalse("$d $hora h: en Ramadán no se guisa mientras hay sol", EscenaAndalus.guisando(m) && m.hacer != Hacer.CUIDAR)
                    assertTrue("$d: en Ramadán las lámparas están colgadas", EscenaAndalus.lamparas(m))
                } else assertEquals("$d $hora h: se guisa a las horas de comer", m.hacer == Hacer.COMER, EscenaAndalus.guisando(m))
                assertEquals("$d: las lámparas, si y solo si es Ramadán o su noche", f.estado == "ramadan" || m.con("lamparas"), EscenaAndalus.lamparas(m))
                if (EscenaAndalus.lamparasEncendidas(m)) assertTrue("$d $hora h: las lámparas solo arden de noche", m.luces().noche > 0.3f)
                assertEquals("$d: la hueste cruza el puente si y solo si es día de guerra", m.con("guerra"), EscenaAndalus.hueste(m))
                // Las anclas del gato caben en el cuadro, y nunca faltan el suelo, el mirador, el objeto y el abrigo.
                val anclas = EscenaAndalus.anclas(m)
                for (a in anclas) assertTrue("$d $hora h: ancla ${a.tipo} fuera", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }
                assertTrue("$d: el gato tiene al menos 110 unidades de suelo", suelo.hasta - suelo.x >= 110f)
                assertEquals("$d $hora h: hay fuego para el gato si y solo si el anafe está encendido", EscenaAndalus.guisando(m), anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d: hay adorno si y solo si es fiesta", m.con("fiesta"), anclas.any { it.tipo == TipoAncla.ADORNO })
                assertTrue("$d: el anafe estorba en el suelo del gato", EscenaAndalus.estorbos(m).isNotEmpty())
            }
            d = d.plusDays(1)
        }
    }
}
