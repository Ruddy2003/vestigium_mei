package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.VeranoMongol
import io.github.ruddy2003.vestigium.ui.escenas.VeranoMongol.Edad
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Postura
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El campamento de verano de Mongol (tanda S12, cuadro B) cuenta lo mismo que el cuento de Bat: lo que cambia en él un
 * día señalado (la llegada, la primera leche, la esquila, el fieltro, los caballos de fuera, el cubo volcado), el potro
 * blanco que crece, las yeguas de la cuerda según la hora y el tiempo, y lo que hay en uso a cada hora. Se mira el día
 * de antes y el día, y todos los días del verano de 2026 a 2032.
 */
class EscenaMongolVeranoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "pastor_mongol")!!, hora)
    private fun cadaVerano(hacer: (LocalDate, Momento) -> Unit) {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) { val m = momento(d.toString()); if (EscenaMongol.lugar(m) == Lugar.VERANO) hacer(d, m); d = d.plusDays(1) }
    }

    @Test fun elPotroBlancoCreceDesdeQueNace() {
        assertEquals(Edad.POTRO, VeranoMongol.potroBlanco(momento("2027-07-01")))
        assertEquals(Edad.POTRO, VeranoMongol.potroBlanco(momento("2028-05-01")))
        assertEquals(Edad.ANOJO, VeranoMongol.potroBlanco(momento("2028-05-02")))
        assertEquals(Edad.ANOJO, VeranoMongol.potroBlanco(momento("2028-08-08")))              // el día de su carrera
        assertEquals(Edad.JOVEN, VeranoMongol.potroBlanco(momento("2029-05-02")))
        assertNull(VeranoMongol.potroBlanco(momento("2027-05-01")))
        cadaVerano { d, m ->
            val esperado = when (m.ficha.temporada) { 1 -> Edad.POTRO; 2 -> Edad.ANOJO; else -> Edad.JOVEN }
            assertEquals("$d", esperado, VeranoMongol.potroBlanco(m))
        }
    }

    @Test fun lasYeguasEstanEnLaCuerdaDeDiaYDeNocheSoloLosPotros() {
        cadaVerano { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 20.5, 23.0)) {
                val m = momento(d.toString(), h); val y = VeranoMongol.yeguada(m)
                if (VeranoMongol.llegada(m) && EscenaMongol.armado(m) < 1f) { assertTrue("$d $h: el día que llegan no se tiende la cuerda hasta armar", y.isEmpty()); continue }
                val adultas = y.count { !it.potro }
                if (EscenaMongol.yeguasEnLaCuerda(m)) assertTrue("$d $h: de día, las yeguas", adultas >= 3) else assertEquals("$d $h: de noche, solo los potros", 0, adultas)
                assertTrue("$d $h: los potros, siempre", y.any { it.potro || it.cria != null })
                assertEquals("$d $h: el potro mama de día, sin tormenta", EscenaMongol.yeguasEnLaCuerda(m) && m.ficha.clima != Clima.TORMENTA, y.any { it.cria != null })
                assertTrue("$d $h: la que tiene potro vuelve la cabeza hacia él", y.filter { it.cria != null }.all { it.postura == Postura.MIRANDO_ATRAS })
                if (m.ficha.clima == Clima.TORMENTA) assertTrue("$d $h: con tormenta, juntas y de pie", y.all { it.postura == Postura.DE_PIE })
                else assertTrue("$d $h: un potro echado", y.any { it.potro && it.postura == Postura.ECHADO })
                assertEquals("$d $h: el potro blanco", VeranoMongol.potroBlanco(m) != null && (VeranoMongol.potroBlanco(m) != Edad.JOVEN || EscenaMongol.yeguasEnLaCuerda(m)), y.any { it.blanco || it.criaBlanca })
                assertTrue("$d $h: la cuerda va de la estaca al fondo", y.all { it.t in 0f..1f })
            }
        }
        // La primera leche: todas las yeguas atadas.
        assertEquals(4, VeranoMongol.yeguada(momento("2027-06-16", 7.0)).count { !it.potro })
        assertEquals(3, VeranoMongol.yeguada(momento("2027-06-15", 7.0)).count { !it.potro })
    }

    @Test fun lasFaenasSeVenSuDiaYNoLaVispera() {
        for (a in 2027..2031) {
            assertFalse(VeranoMongol.lanaEnElSuelo(momento("$a-06-24"))); assertTrue(VeranoMongol.lanaEnElSuelo(momento("$a-06-25")))
            assertTrue(VeranoMongol.lanaEnElSuelo(momento("$a-07-04"))); assertFalse(VeranoMongol.lanaEnElSuelo(momento("$a-07-05")))
            assertFalse(VeranoMongol.fieltroTendido(momento("$a-09-04"))); assertTrue(VeranoMongol.fieltroTendido(momento("$a-09-05")))
            assertTrue(VeranoMongol.fieltroTendido(momento("$a-09-11"))); assertFalse(VeranoMongol.fieltroTendido(momento("$a-09-12")))
            assertFalse(VeranoMongol.llegada(momento("$a-05-24"))); assertTrue(VeranoMongol.llegada(momento("$a-05-25"))); assertFalse(VeranoMongol.llegada(momento("$a-05-26")))
            assertFalse(VeranoMongol.primeraLeche(momento("$a-06-15"))); assertTrue(VeranoMongol.primeraLeche(momento("$a-06-16"))); assertFalse(VeranoMongol.primeraLeche(momento("$a-06-17")))
        }
        assertTrue(VeranoMongol.cuboVolcado(momento("2027-08-12"))); assertFalse(VeranoMongol.cuboVolcado(momento("2027-08-11")))
        // Solo el día de la temporada 1 (y el de 2026, que es su avance).
        cadaVerano { d, m -> if (VeranoMongol.cuboVolcado(m)) assertTrue("$d", d.toString() in setOf("2026-08-12", "2027-08-12")) }
    }

    @Test fun losCaballosDeFueraLleganConLasNoticiasYLasFiestas() {
        assertEquals(3, VeranoMongol.caballosDeFuera(momento("2027-07-20"))); assertEquals(0, VeranoMongol.caballosDeFuera(momento("2027-07-19")))   // el correo del kan
        assertEquals(1, VeranoMongol.caballosDeFuera(momento("2028-07-01"))); assertEquals(0, VeranoMongol.caballosDeFuera(momento("2028-06-30")))   // Urgench
        assertEquals(1, VeranoMongol.caballosDeFuera(momento("2030-09-15")))                                                                            // el Kalka
        assertEquals(2, VeranoMongol.caballosDeFuera(momento("2028-08-08")))                                                                            // la carrera del potro blanco
        assertEquals(3, VeranoMongol.caballosDeFuera(momento("2031-07-28"))); assertEquals(0, VeranoMongol.caballosDeFuera(momento("2031-07-27")))   // vuelve Temür
        cadaVerano { d, m ->
            val n = VeranoMongol.caballosDeFuera(m)
            assertEquals("$d", n > 0, EscenaMongol.caballoAjeno(m) || EscenaMongol.fiesta(m) || EscenaMongol.carrera(m))
            assertEquals("$d: la meta de la carrera", EscenaMongol.carrera(m), VeranoMongol.carrera(m))
        }
    }

    @Test fun loQueHayEnUsoSigueLaHoraYElTiempo() {
        cadaVerano { d, _ ->
            for (h in listOf(3.0, 5.5, 7.0, 12.5, 13.0, 20.5, 23.0)) {
                val m = momento(d.toString(), h)
                val llueve = m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA
                assertEquals("$d $h", llueve, VeranoMongol.rioTurbio(m))
                assertEquals("$d $h: duerme fuera, sobre la piel", m.hacer == OtrasVidas.Hacer.DORMIR && !llueve, VeranoMongol.pielParaDormir(m))
                assertEquals("$d $h: el ordeño del alba", m.hacer == OtrasVidas.Hacer.CUIDAR && EscenaMongol.yeguasEnLaCuerda(m), VeranoMongol.ordenio(m))
                if (m.hacer == OtrasVidas.Hacer.GENTE) assertTrue("$d $h: el corro de la hora de la gente", VeranoMongol.corro(m))
                if (m.hacer == OtrasVidas.Hacer.DORMIR || m.hacer == OtrasVidas.Hacer.TRABAJO) assertFalse("$d $h", VeranoMongol.corro(m))
                assertTrue("$d $h", VeranoMongol.batido(m) in 0f..1f)
                assertEquals("$d $h: la silla, en su caballete salvo el día que es el objeto", m.ficha.objeto != "silla", VeranoMongol.sillaEnElCaballete(m))
                val c = VeranoMongol.hierba(m); assertTrue("$d $h", c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f)
            }
        }
    }

    @Test fun elMismoMomentoDaLoMismo() {
        val a = momento("2029-07-02", 13.0); val b = momento("2029-07-02", 13.0)
        assertEquals(VeranoMongol.yeguada(a).map { Triple(it.t, it.postura, it.blanco) }, VeranoMongol.yeguada(b).map { Triple(it.t, it.postura, it.blanco) })
        assertEquals(VeranoMongol.caballosDeFuera(a), VeranoMongol.caballosDeFuera(b))
        assertEquals(VeranoMongol.hierba(a), VeranoMongol.hierba(b))
    }
}
