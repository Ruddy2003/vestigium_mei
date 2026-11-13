package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaGotico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaGotico.Obra
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
 * El cuadro de Gótico (tanda P4b) cuenta lo mismo que el cuento de Pierre: la obra de la cabecera, el andamio que
 * cede, la crecida del Somme y la altura del coro salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaGoticoPrueba {
    private fun momento(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "cantero")!!, hora)
    private fun obra(dia: String) = EscenaGotico.obra(momento(dia, 13.0))

    @Test fun laEscenaEsLaDePierre() {
        assertEquals(listOf("cantero"), EscenaGotico.vidas)
        assertEquals("cantero", EscenaGotico.vida("2027-05-10"))
    }

    @Test fun laObraEstaComoDiceElCuento() {
        // Temporada 1: la obra en marcha de marzo al 2 de septiembre; el 3 se acaba el dinero y se tapa.
        assertEquals(Obra.TAPADA, obra("2027-01-20"))                       // invierno: los muros, tapados con paja
        assertEquals(Obra.EN_MARCHA, obra("2027-05-10"))
        assertEquals(5, EscenaGotico.andamios(momento("2027-05-10", 13.0)))
        assertEquals(Obra.EN_MARCHA, obra("2027-09-02"))
        assertEquals(Obra.TAPADA, obra("2027-09-03"))
        assertEquals(0, EscenaGotico.andamios(momento("2027-09-03", 13.0)))
        assertEquals(Obra.TAPADA, obra("2027-10-15")); assertEquals(Obra.TAPADA, obra("2028-03-03"))
        // Temporada 2: vuelve despacio el 4 de marzo.
        assertEquals(Obra.A_MEDIAS, obra("2028-03-04"))
        assertEquals(Obra.A_MEDIAS, obra("2028-06-20"))
        // Después, en marcha en temporada y tapada en invierno.
        assertEquals(Obra.TAPADA, obra("2029-01-10")); assertEquals(Obra.EN_MARCHA, obra("2029-06-01"))
        assertEquals(Obra.EN_MARCHA, obra("2030-06-09")); assertEquals(Obra.TAPADA, obra("2030-12-20"))
        assertEquals(Obra.EN_MARCHA, obra("2031-10-10"))
    }

    @Test fun laGruaSoloSubePiedraConLaObraEnMarchaYASusHorasDeTrabajo() {
        assertTrue(EscenaGotico.gruaCargada(momento("2027-05-10", 10.0)))
        assertFalse(EscenaGotico.gruaCargada(momento("2027-05-10", 12.0)))  // está comiendo
        assertFalse(EscenaGotico.gruaCargada(momento("2027-05-10", 21.5)))
        assertFalse(EscenaGotico.gruaCargada(momento("2027-09-03", 10.0)))  // la obra, parada
    }

    @Test fun elAndamioCedidoSeVeMientrasEstaHerido() {
        assertFalse(EscenaGotico.andamioCaido(momento("2028-05-26", 13.0)))
        for (dia in listOf("2028-05-27", "2028-06-20", "2028-08-24")) assertTrue(dia, EscenaGotico.andamioCaido(momento(dia, 13.0)))
        assertFalse(EscenaGotico.andamioCaido(momento("2028-08-25", 13.0)))
    }

    @Test fun laCrecidaDelSommeSoloEnSusDias() {
        assertEquals(0f, EscenaGotico.crecida(momento("2029-01-16", 13.0)), 0.001f)
        assertTrue(EscenaGotico.crecida(momento("2029-01-18", 13.0)) > 0.2f)
        assertTrue(EscenaGotico.crecida(momento("2029-01-22", 13.0)) > 0.9f)
        assertEquals(0f, EscenaGotico.crecida(momento("2029-02-09", 13.0)), 0.001f)
        assertEquals(0f, EscenaGotico.crecida(momento("2027-05-10", 13.0)), 0.001f)
    }

    @Test fun elCoroSubeYNuncaBaja() {
        assertEquals(0.10f, EscenaGotico.alturaDelCoro(momento("2027-01-05", 13.0)), 0.01f)
        assertTrue(EscenaGotico.alturaDelCoro(momento("2031-12-01", 13.0)) > 0.5f)
        var antes = 0f; var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) { val ahora = EscenaGotico.alturaDelCoro(momento(d.toString(), 13.0)); assertTrue("$d: el coro no puede bajar", ahora >= antes - 0.0001f); antes = ahora; d = d.plusDays(1) }
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "cantero")!!
            for (hora in listOf(3.0, 7.0, 10.0, 12.0, 13.0, 19.5, 21.0)) {
                val m = Momento(f, hora)
                assertEquals("$d $hora h: el andamio cedido se ve si y solo si él está herido", f.estado == "herido", EscenaGotico.andamioCaido(m))
                val arco = (f.arcos["andamios"] ?: 0.0).toFloat()
                assertEquals("$d: tapada si y solo si casi no hay andamios", arco < 0.3f, EscenaGotico.obra(m) == Obra.TAPADA)
                assertEquals("$d: sin andamios si y solo si está tapada", EscenaGotico.obra(m) == Obra.TAPADA, EscenaGotico.andamios(m) == 0)
                assertTrue("$d: de 0 a 5 cuerpos de andamio", EscenaGotico.andamios(m) in 0..5)
                if (EscenaGotico.gruaCargada(m)) { assertEquals(Obra.EN_MARCHA, EscenaGotico.obra(m)); assertEquals(Hacer.TRABAJO, m.hacer) }
                assertEquals("$d: la crecida sigue al arco del agua", (f.arcos["agua"] ?: 0.0) > 0.0 || m.con("inundacion"), EscenaGotico.crecida(m) > 0f)
                // Las anclas del gato caben en el cuadro, y nunca faltan el suelo, el mirador, el objeto y el abrigo.
                val anclas = EscenaGotico.anclas(m)
                for (a in anclas) assertTrue("$d $hora h: ancla ${a.tipo} fuera", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                assertEquals("$d $hora h: hay fuego para el gato si y solo si el hogar está encendido", m.hacer == Hacer.COMER || m.hacer == Hacer.GENTE, anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d: hay adorno si y solo si es fiesta", m.con("fiesta"), anclas.any { it.tipo == TipoAncla.ADORNO })
            }
            d = d.plusDays(1)
        }
    }
}
