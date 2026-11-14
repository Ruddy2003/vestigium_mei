package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.CapillaUrbe
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrbe
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrbe.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.FrescoUrbe
import io.github.ruddy2003.vestigium.ui.escenas.FrescoUrbe.Sitio
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosUrbe
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los dos cuadros de Roma (tanda S5) cuentan lo mismo que el cuento de Giulia: en cuál está (la capilla o, en agosto,
 * la azotea), cómo va el andamio, cuánto del fresco está limpio y lo que la obra ha ido encontrando salen de la ficha
 * del día, y aquí se comparan con su calendario.
 */
class EscenaUrbePrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "restauradora")!!, hora)
    private fun despejado(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "restauradora")!!.copy(clima = Clima.DESPEJADO), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeGiulia() {
        assertEquals(listOf("restauradora"), EscenaUrbe.vidas)
        assertEquals("restauradora", EscenaUrbe.vida("2027-05-10"))
    }

    @Test fun enAgostoEstaEnLaAzoteaYElRestoDelAnioEnLaCapilla() {
        cadaDia { d, m ->
            assertEquals(d.toString(), if (m.ficha.estado == "agosto") Lugar.AGOSTO else Lugar.CAPILLA, EscenaUrbe.lugar(m))
            // La obra cierra el 1 de agosto y ella vuelve al andamio el 31.
            val agosto = d.monthValue == 8 && d.dayOfMonth <= 30
            assertEquals(d.toString(), agosto, EscenaUrbe.lugar(m) == Lugar.AGOSTO)
        }
    }

    @Test fun elAndamioSeMontaSeBajaYSeQuitaCuandoDiceElCuento() {
        // Temporada 1 (2026 y 2027): sube por primera vez el 20 de enero.
        assertTrue(EscenaUrbe.porMontar(momento("2027-01-19"))); assertFalse(EscenaUrbe.hayAndamio(momento("2027-01-19")))
        assertFalse(EscenaUrbe.porMontar(momento("2027-01-20"))); assertTrue(EscenaUrbe.hayAndamio(momento("2027-01-20"))); assertTrue(EscenaUrbe.andamioDelAltar(momento("2027-01-20")))
        // Temporada 4 (2030): el 30 de noviembre bajan el de la pared del altar; queda el otro.
        assertTrue(EscenaUrbe.andamioDelAltar(momento("2030-11-29"))); assertFalse(EscenaUrbe.andamioDelAltar(momento("2030-11-30"))); assertTrue(EscenaUrbe.hayAndamio(momento("2030-11-30")))
        // Temporada 5 (2031): el 12 de octubre se quita el último, y el 8 de noviembre se abre la capilla.
        assertTrue(EscenaUrbe.hayAndamio(momento("2031-10-11"))); assertFalse(EscenaUrbe.hayAndamio(momento("2031-10-12")))
        assertFalse(EscenaUrbe.abierta(momento("2031-11-07"))); assertTrue(EscenaUrbe.abierta(momento("2031-11-08")))
        // Cuando la temporada 5 se repite (2032), el cuento vuelve a contarla: el cuadro también.
        assertTrue(EscenaUrbe.hayAndamio(momento("2032-05-10"))); assertFalse(EscenaUrbe.andamioDelAltar(momento("2032-05-10"))); assertFalse(EscenaUrbe.hayAndamio(momento("2032-11-20")))
        // Nunca hay andamio del altar sin el otro.
        cadaDia { d, m -> if (EscenaUrbe.andamioDelAltar(m)) assertTrue(d.toString(), EscenaUrbe.hayAndamio(m)) }
    }

    @Test fun elFrescoSeLimpiaPorZonasYNuncaSeEnsucia() {
        var antes = -1; var temporada = 0
        cadaDia { d, m ->
            val limpias = FrescoUrbe.ZONAS.count { EscenaUrbe.limpia(m, it.umbral) }
            // Dentro de las cinco temporadas, cada día hay las mismas zonas limpias que ayer o más.
            if (m.ficha.vuelta == 0 && !(m.ficha.temporada == 1 && temporada == 1 && d.dayOfYear == 1)) assertTrue("$d: $limpias < $antes", limpias >= antes)
            antes = if (m.ficha.vuelta == 0) limpias else -1; temporada = m.ficha.temporada
            // La bóveda entera está limpia justo desde el día que dice el calendario.
            assertEquals(d.toString(), EscenaUrbe.bovedaLimpia(m), FrescoUrbe.limpias(m, Sitio.BOVEDA) == 5)
            // La pared del altar, cuando bajan su andamio; la derecha, cuando se quita el último.
            if (!EscenaUrbe.andamioDelAltar(m) && !EscenaUrbe.porMontar(m)) assertEquals(d.toString(), 7, FrescoUrbe.limpias(m, Sitio.ALTAR))
            if (!EscenaUrbe.hayAndamio(m) && !EscenaUrbe.porMontar(m)) { assertEquals(d.toString(), 6, FrescoUrbe.limpias(m, Sitio.DERECHA)); assertNull(d.toString(), FrescoUrbe.enObra(m)) }
            // Mientras hay andamio, hay una zona en obra.
            if (EscenaUrbe.hayAndamio(m)) assertNotNull(d.toString(), FrescoUrbe.enObra(m))
        }
        // Antes de empezar, todo negro de hollín; en mayo de la temporada 1 ya hay un tramo de bóveda limpio.
        assertEquals(0, FrescoUrbe.ZONAS.count { EscenaUrbe.limpia(momento("2027-01-10"), it.umbral) })
        assertEquals(1, FrescoUrbe.limpias(momento("2027-05-06"), Sitio.BOVEDA))
        assertTrue(FrescoUrbe.limpias(momento("2028-11-19"), Sitio.BOVEDA) < 5); assertEquals(5, FrescoUrbe.limpias(momento("2028-11-20"), Sitio.BOVEDA))
        assertTrue(FrescoUrbe.limpias(momento("2030-11-29"), Sitio.ALTAR) < 7); assertEquals(7, FrescoUrbe.limpias(momento("2030-11-30"), Sitio.ALTAR))
        assertTrue(FrescoUrbe.limpias(momento("2031-10-11"), Sitio.DERECHA) < 6); assertEquals(6, FrescoUrbe.limpias(momento("2031-10-12"), Sitio.DERECHA))
    }

    @Test fun loQueLaObraEncuentraSeVeDesdeSuDia() {
        assertFalse(EscenaUrbe.angel(momento("2027-03-17"))); assertTrue(EscenaUrbe.angel(momento("2027-03-18"))); assertTrue(EscenaUrbe.angel(momento("2026-03-18")))
        assertFalse(EscenaUrbe.grieta(momento("2027-11-11"))); assertTrue(EscenaUrbe.grieta(momento("2027-11-12"))); assertTrue(EscenaUrbe.grieta(momento("2029-02-01")))
        assertFalse(EscenaUrbe.firma(momento("2029-03-03"))); assertTrue(EscenaUrbe.firma(momento("2029-03-04"))); assertTrue(EscenaUrbe.firma(momento("2032-01-05")))
        assertFalse(EscenaUrbe.conSamir(momento("2027-06-15"))); assertTrue(EscenaUrbe.conSamir(momento("2027-06-16"))); assertFalse(EscenaUrbe.conSamir(momento("2031-07-10")))
        // Los dos días que la grieta para la obra nadie sube al andamio; el día del aguacero no hay luz.
        for (dia in listOf("2027-11-12", "2027-11-13")) { assertTrue(EscenaUrbe.obraParada(momento(dia))); assertFalse(EscenaUrbe.trabajando(momento(dia, 10.0))) }
        assertFalse(EscenaUrbe.obraParada(momento("2027-11-14")))
        assertTrue(EscenaUrbe.sinLuz(momento("2027-10-15"))); assertFalse(EscenaUrbe.focosDeObra(momento("2027-10-15", 10.0))); assertTrue(EscenaUrbe.velas(momento("2027-10-15", 10.0)))
        assertTrue(EscenaUrbe.papeles(momento("2030-10-15"))); assertFalse(EscenaUrbe.papeles(momento("2030-10-16")))
    }

    @Test fun losFocosSoloLucenMientrasTrabajaYLasVelasAlCaerLaTarde() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 10.0, 13.4, 16.0, 19.0, 21.0, 23.8)) {
                val m = momento(d.toString(), h)
                if (EscenaUrbe.focosDeObra(m)) { assertEquals("$d $h", Hacer.TRABAJO, m.hacer); assertTrue("$d $h", EscenaUrbe.hayAndamio(m)); assertEquals("$d $h", Lugar.CAPILLA, EscenaUrbe.lugar(m)) }
            }
            assertFalse(d.toString(), EscenaUrbe.focosDeObra(momento(d.toString(), 3.0)))
            assertTrue(d.toString(), EscenaUrbe.velas(momento(d.toString(), 21.0))); assertFalse(d.toString(), EscenaUrbe.velas(momento(d.toString(), 3.0)))
        }
        assertTrue(EscenaUrbe.focosDeObra(momento("2027-05-06", 10.0))); assertFalse(EscenaUrbe.focosDeObra(momento("2027-05-06", 13.4)))
        // Con la capilla terminada ya no hay focos de obra.
        assertFalse(EscenaUrbe.focosDeObra(momento("2031-11-20", 10.0)))
    }

    @Test fun laManchaDeSolSeMueveConLaHora() {
        val manana = EscenaUrbe.solQueEntra(despejado("2027-05-06", 7.0))!!; val mediodia = EscenaUrbe.solQueEntra(despejado("2027-05-06", 13.0))!!; val tarde = EscenaUrbe.solQueEntra(despejado("2027-05-06", 18.4))!!
        // A mediodía el sol cae más a plomo que por la mañana y por la tarde.
        assertTrue(mediodia.e > manana.e * 2f); assertTrue(mediodia.e > tarde.e * 1.5f)
        // Por la mañana cruza la capilla; por la tarde se va hacia quien mira.
        assertTrue(tarde.a > manana.a + 0.2f)
        // A mediodía la mancha está en el suelo, cerca de la ventana; por la mañana llega a la pared derecha.
        val enElSuelo = CapillaUrbe.centroDeLaMancha(mediodia); val enLaPared = CapillaUrbe.centroDeLaMancha(manana)
        assertTrue(enElSuelo.y >= CapillaUrbe.SUELO && enElSuelo.x < 200f)
        assertTrue(enLaPared.x >= CapillaUrbe.X1 - 0.5f)
        // De noche, con luna y sin nubes, entra la luna, más floja que el sol; con el cielo cubierto no entra nada de día.
        val luna = EscenaUrbe.solQueEntra(despejado("2027-05-06", 23.0)); if (luna != null) assertTrue(luna.fuerza < mediodia.fuerza)
        assertNull(EscenaUrbe.solQueEntra(Momento(FichaDelDia.de("2027-05-06", "restauradora")!!.copy(clima = Clima.LLUVIA), 13.0)))
    }

    @Test fun elGatoTieneSuSitioEnLosDosCuadros() {
        cadaDia { d, _ ->
            for (h in listOf(7.0, 13.0, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaUrbe.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h", suelo.hasta - suelo.x >= 110f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo != TipoAncla.SUELO && a.tipo != TipoAncla.MIRADOR && a.tipo != TipoAncla.ADORNO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                // Solo se echa junto a las velas del velero, cuando están encendidas; en la azotea no hay fuego.
                assertEquals("$d $h", EscenaUrbe.lugar(m) == Lugar.CAPILLA && EscenaUrbe.velas(m), anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d $h", EscenaUrbe.fiesta(m), anclas.any { it.tipo == TipoAncla.ADORNO })
                for (x in EscenaUrbe.estorbos(m)) assertTrue("$d $h", x in 0f..ANCHO)
            }
        }
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosUrbe.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun losDiasEspecialesSeVen() {
        // El luto por el papa, el humo blanco y el desfile son de la temporada 1; Ferragosto cae en la azotea.
        assertTrue(EscenaUrbe.luto(momento("2027-04-21"))); assertTrue(EscenaUrbe.luto(momento("2027-04-26"))); assertFalse(EscenaUrbe.luto(momento("2028-04-21")))
        assertTrue(EscenaUrbe.humoBlanco(momento("2027-05-08"))); assertFalse(EscenaUrbe.humoBlanco(momento("2028-05-08")))
        for (anio in 2027..2031) { assertTrue(EscenaUrbe.desfile(momento("$anio-06-02"))); assertTrue(EscenaUrbe.navidad(momento("$anio-12-25")))
            val ferragosto = momento("$anio-08-15"); assertEquals(Lugar.AGOSTO, EscenaUrbe.lugar(ferragosto)); assertTrue(EscenaUrbe.fiesta(ferragosto)) }
        // Algo se mueve cuando hay velas, y en la azotea de noche.
        assertTrue(EscenaUrbe.seMueve(momento("2027-05-06", 21.0))); assertTrue(EscenaUrbe.seMueve(momento("2027-08-10", 22.0)))
    }
}
