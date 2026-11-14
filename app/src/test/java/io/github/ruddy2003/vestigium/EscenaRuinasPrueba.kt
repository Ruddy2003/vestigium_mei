package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Cerezos
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Enfrente
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Fogata
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Parque
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Puente
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Rio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRuinas.Torre
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
 * El cuadro de la Ciudad verde (tanda S4) cuenta lo mismo que el cuento de la recolectora de semillas: cómo va el
 * huerto, si el puente está roto o entero, si la torre de cristal sigue en pie, si el edificio de enfrente ya ardió, si
 * el río está helado y si ella está de viaje salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaRuinasPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "semillera")!!, hora)
    private fun hoyEs(dia: String, id: String) = momento(dia).ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeLaRecolectora() {
        assertEquals(listOf("semillera"), EscenaRuinas.vidas)
        assertEquals("semillera", EscenaRuinas.vida("2027-05-10"))
    }

    @Test fun laTorreDeCristalSeCaeElDiaQueDiceElCalendario() {
        // Temporada 1 (2027): se cae el 14 de abril, «a media mañana».
        assertEquals(Torre.INCLINADA, EscenaRuinas.torre(momento("2027-01-02"))); assertEquals(Torre.INCLINADA, EscenaRuinas.torre(momento("2027-04-13", 21.0)))
        assertTrue(hoyEs("2027-04-14", "torre"))
        assertEquals(Torre.INCLINADA, EscenaRuinas.torre(momento("2027-04-14", 7.0))); assertEquals(Torre.RECIEN_CAIDA, EscenaRuinas.torre(momento("2027-04-14", 10.0))); assertEquals(Torre.RECIEN_CAIDA, EscenaRuinas.torre(momento("2027-04-14", 21.0)))
        assertEquals(Torre.CAIDA, EscenaRuinas.torre(momento("2027-04-15", 7.0)))
        for (dia in listOf("2027-12-31", "2028-01-01", "2029-04-10", "2031-06-01", "2032-02-01")) assertEquals(dia, Torre.CAIDA, EscenaRuinas.torre(momento(dia)))
        // El año de avance (2026) repite la temporada 1: la torre vuelve a estar en pie hasta su día.
        assertEquals(Torre.INCLINADA, EscenaRuinas.torre(momento("2026-03-01"))); assertEquals(Torre.CAIDA, EscenaRuinas.torre(momento("2026-06-01")))
    }

    @Test fun elEdificioDeEnfrenteArdeElDiaDelRayo() {
        assertEquals(Enfrente.VERDE, EscenaRuinas.enfrente(momento("2027-07-09")))
        assertTrue(hoyEs("2027-07-10", "rayo")); assertEquals(Clima.TORMENTA, momento("2027-07-10").ficha.clima)
        assertEquals(Enfrente.ARDIENDO, EscenaRuinas.enfrente(momento("2027-07-10", 7.0))); assertEquals(Enfrente.ARDIENDO, EscenaRuinas.enfrente(momento("2027-07-10", 20.0)))
        for (dia in listOf("2027-07-11", "2028-07-10", "2031-01-01", "2032-06-01")) assertEquals(dia, Enfrente.QUEMADO, EscenaRuinas.enfrente(momento(dia)))
        // La hiedra le vuelve a subir poco a poco: nada el primer año, y cada año más.
        assertEquals(0f, EscenaRuinas.rebroteDeEnfrente(momento("2027-06-01")), 0f); assertEquals(0f, EscenaRuinas.rebroteDeEnfrente(momento("2027-12-01")), 0f)
        var antes = 0f
        for (anio in 2028..2032) { val r = EscenaRuinas.rebroteDeEnfrente(momento("$anio-06-01")); assertTrue("$anio: rebrota", r > antes); antes = r }
    }

    @Test fun elParqueDelNorteArdeTresDias() {
        // Temporada 3 (2029): del 28 al 30 de agosto. Esos días el humo tapa el sol.
        assertEquals(Parque.VERDE, EscenaRuinas.parque(momento("2027-08-28"))); assertEquals(Parque.VERDE, EscenaRuinas.parque(momento("2029-08-27")))
        for (dia in listOf("2029-08-28", "2029-08-29", "2029-08-30")) {
            assertTrue(dia, hoyEs(dia, "incendio")); assertEquals(dia, Parque.ARDIENDO, EscenaRuinas.parque(momento(dia)))
            assertTrue("$dia: el humo tapa el sol", EscenaRuinas.luces(momento(dia)).solFuerza < momento(dia).luces().solFuerza * 0.7f)
        }
        assertEquals(Parque.QUEMADO, EscenaRuinas.parque(momento("2029-08-31"))); assertEquals(Parque.QUEMADO, EscenaRuinas.parque(momento("2029-12-31")))
        assertEquals(Parque.REBROTA, EscenaRuinas.parque(momento("2030-05-01"))); assertEquals(Parque.REBROTA, EscenaRuinas.parque(momento("2032-05-01")))
    }

    @Test fun elPuenteSeArreglaCuandoDiceElCalendario() {
        // Roto hasta el 1 de abril de la temporada 3 (2029); en obra hasta el 20 de junio de la 4 (2030); después, entero.
        for (dia in listOf("2027-06-01", "2028-12-31", "2029-04-01")) assertEquals(dia, Puente.ROTO, EscenaRuinas.puente(momento(dia)))
        for (dia in listOf("2029-05-01", "2029-12-31", "2030-06-19")) assertEquals(dia, Puente.EN_OBRA, EscenaRuinas.puente(momento(dia)))
        assertTrue(hoyEs("2030-06-20", "puente_listo"))
        for (dia in listOf("2030-06-20", "2031-01-01", "2032-06-01")) assertEquals(dia, Puente.ENTERO, EscenaRuinas.puente(momento(dia)))
        // Nunca va para atrás.
        var antes = 0f; var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2032) { val o = EscenaRuinas.obraDelPuente(momento(d.toString())); assertTrue("$d: el puente no se deshace", o >= antes - 0.0001f); antes = o; d = d.plusDays(7) }
        // La feria del trueque se hace encima desde que está entero.
        assertTrue(EscenaRuinas.feriaEnElPuente(momento("2030-06-20"))); assertTrue(EscenaRuinas.feriaEnElPuente(momento("2030-06-21"))); assertTrue(EscenaRuinas.feriaEnElPuente(momento("2031-06-21")))
        assertFalse(EscenaRuinas.feriaEnElPuente(momento("2029-06-21"))); assertFalse(EscenaRuinas.feriaEnElPuente(momento("2031-06-25")))
    }

    @Test fun elHuertoSigueSuArco() {
        for (anio in 2027..2031) {
            // Tapado con paja del 1 de noviembre al 19 de marzo.
            for (dia in listOf("$anio-01-15", "$anio-03-19", "$anio-11-01", "$anio-12-31")) assertTrue(dia, EscenaRuinas.tapado(momento(dia)))
            for (dia in listOf("$anio-03-20", "$anio-07-01", "$anio-10-31")) assertFalse(dia, EscenaRuinas.tapado(momento(dia)))
            // Crece de marzo a septiembre, y tras la cosecha queda casi vacío.
            assertEquals(0f, EscenaRuinas.etapaDelHuerto(momento("$anio-02-01")), 0.001f)
            var antes = 0f; var d = LocalDate.of(anio, 3, 20)
            while (d <= LocalDate.of(anio, 9, 9)) { val e = EscenaRuinas.etapaDelHuerto(momento(d.toString())); assertTrue("$d: el huerto crece", e >= antes - 0.0001f); antes = e; d = d.plusDays(3) }
            assertTrue("$anio: a punto en septiembre", EscenaRuinas.etapaDelHuerto(momento("$anio-09-09")) > 0.97f)
            assertTrue("$anio: casi vacío tras la cosecha", EscenaRuinas.etapaDelHuerto(momento("$anio-09-10")) < 0.2f)
        }
        val m = momento("2029-06-15"); assertEquals(m.arco("huerto"), EscenaRuinas.etapaDelHuerto(m), 0f)
    }

    @Test fun elBosqueCreceCadaAnio() {
        assertEquals(momento("2029-06-15").arco("bosque"), EscenaRuinas.bosque(momento("2029-06-15")), 0f)
        assertTrue(EscenaRuinas.verdor(momento("2027-01-02")) < 0.05f); assertTrue(EscenaRuinas.verdor(momento("2031-12-30")) > 0.95f)
        var antes = -1f
        for (anio in 2027..2031) { val v = EscenaRuinas.verdor(momento("$anio-07-01")); assertTrue("$anio: más bosque", v > antes); antes = v }
    }

    @Test fun elRioSeHielaYSeDeshiela() {
        for (dia in listOf("2027-01-10", "2028-02-28", "2029-12-25")) assertEquals(dia, Rio.HELADO, EscenaRuinas.rio(momento(dia)))
        assertTrue(hoyEs("2027-03-01", "deshielo")); assertEquals(Rio.DESHIELO, EscenaRuinas.rio(momento("2027-03-01"))); assertEquals(Rio.DESHIELO, EscenaRuinas.rio(momento("2030-03-03")))
        for (dia in listOf("2027-03-04", "2027-07-01", "2031-11-30")) assertEquals(dia, Rio.CORRE, EscenaRuinas.rio(momento(dia)))
    }

    @Test fun losCerezosDeLaAvenida() {
        assertEquals(Cerezos.PELADOS, EscenaRuinas.cerezos(momento("2027-01-15"))); assertEquals(Cerezos.EN_FLOR, EscenaRuinas.cerezos(momento("2027-04-10")))
        assertEquals(Cerezos.VERDES, EscenaRuinas.cerezos(momento("2027-06-01"))); assertEquals(Cerezos.CON_CEREZAS, EscenaRuinas.cerezos(momento("2027-07-10")))
        assertEquals(Cerezos.DE_OTONO, EscenaRuinas.cerezos(momento("2027-10-15"))); assertEquals(Cerezos.PELADOS, EscenaRuinas.cerezos(momento("2027-12-10")))
        // Las cerezas se recogen tres días desde el 25 de julio: esos días hay escaleras, y los árboles tienen cerezas.
        for (anio in 2027..2031) for (dia in listOf("$anio-07-25", "$anio-07-26", "$anio-07-27")) {
            assertTrue(dia, hoyEs(dia, "cerezas")); assertTrue(dia, EscenaRuinas.recogiendoCerezas(momento(dia))); assertEquals(dia, Cerezos.CON_CEREZAS, EscenaRuinas.cerezos(momento(dia)))
        }
        assertFalse(EscenaRuinas.recogiendoCerezas(momento("2027-07-24"))); assertFalse(EscenaRuinas.recogiendoCerezas(momento("2027-07-28")))
    }

    @Test fun deExpedicionSuRinconQuedaRecogido() {
        // Temporada 2 (2028): del 1 al 21 de julio, al jardín botánico viejo. Temporada 5 (2031): del 25 de mayo al 14 de junio, a la costa.
        assertFalse(EscenaRuinas.deExpedicion(momento("2028-06-30"))); assertFalse(EscenaRuinas.deExpedicion(momento("2028-07-22")))
        for (dia in listOf("2028-07-01", "2028-07-10", "2028-07-21", "2031-05-25", "2031-06-02", "2031-06-14")) {
            val m = momento(dia, 20.0)
            assertTrue(dia, EscenaRuinas.deExpedicion(m)); assertFalse("$dia: la mochila no está", EscenaRuinas.mochilaEnSuClavo(m)); assertFalse("$dia: nadie enciende su vela", EscenaRuinas.velaEncendida(m))
        }
        assertFalse(EscenaRuinas.deExpedicion(momento("2031-06-15")))
        for (anio in listOf(2027, 2029, 2030)) { var d = LocalDate.of(anio, 1, 1); while (d.year == anio) { assertFalse("$d", EscenaRuinas.deExpedicion(momento(d.toString()))); d = d.plusDays(1) } }
        // Cuando la temporada 5 se repite, el viaje se sortea de nuevo, pero dura lo mismo.
        var dias = 0; var d = LocalDate.of(2032, 1, 1); while (d.year == 2032) { if (EscenaRuinas.deExpedicion(momento(d.toString()))) dias++; d = d.plusDays(1) }
        assertEquals(21, dias)
        // En casa, la mochila cuelga de su clavo salvo mientras recorre la ciudad (de 11:00 a 13:00) o si es el objeto del día.
        val enCasa = FichaDelDia.de("2027-05-06", "semillera")!!.copy(objeto = "lata")
        assertTrue(EscenaRuinas.mochilaEnSuClavo(Momento(enCasa, 9.0))); assertFalse(EscenaRuinas.mochilaEnSuClavo(Momento(enCasa, 12.0)))
        assertFalse(EscenaRuinas.mochilaEnSuClavo(Momento(enCasa.copy(objeto = "mochila"), 9.0)))
    }

    @Test fun laFogataSigueElDia() {
        val base = FichaDelDia.de("2027-05-06", "semillera")!!.copy(clima = Clima.DESPEJADO)
        assertEquals(Fogata.APAGADA, EscenaRuinas.fogata(Momento(base, 9.0))); assertEquals(Fogata.OLLA, EscenaRuinas.fogata(Momento(base, 13.4)))
        assertEquals(Fogata.ENCENDIDA, EscenaRuinas.fogata(Momento(base, 20.0))); assertEquals(Fogata.RESCOLDO, EscenaRuinas.fogata(Momento(base, 21.4))); assertEquals(Fogata.APAGADA, EscenaRuinas.fogata(Momento(base, 3.0)))
        // Con tormenta no se enciende: el día del rayo.
        assertEquals(Fogata.APAGADA, EscenaRuinas.fogata(momento("2027-07-10", 20.0)))
        // La noche más larga arde grande hasta el amanecer.
        for (anio in 2027..2031) { assertTrue(hoyEs("$anio-12-21", "noche_larga")); for (h in listOf(18.0, 22.0, 3.0)) if (momento("$anio-12-21").ficha.clima != Clima.TORMENTA) assertEquals("$anio $h", Fogata.GRANDE, EscenaRuinas.fogata(momento("$anio-12-21", h))) }
        assertTrue(EscenaRuinas.lumbre(Momento(base, 20.0)) > EscenaRuinas.lumbre(Momento(base, 21.4))); assertEquals(0f, EscenaRuinas.lumbre(Momento(base, 9.0)), 0f)
    }

    @Test fun losDiasSenaladosSeVen() {
        for (dia in listOf("2027-03-20", "2027-06-21", "2028-12-21", "2030-06-20", "2030-12-01", "2031-01-01")) assertTrue(dia, EscenaRuinas.fiesta(momento(dia)))
        for (dia in listOf("2027-09-05", "2029-09-09")) assertTrue(dia, EscenaRuinas.cosecha(momento(dia)))
        assertFalse(EscenaRuinas.cosecha(momento("2027-09-10")))
        assertTrue(EscenaRuinas.luto(momento("2030-02-08"))); assertFalse(EscenaRuinas.luto(momento("2030-02-09")))
        assertTrue(EscenaRuinas.nevado(momento("2027-01-25"))); assertTrue(EscenaRuinas.nevado(momento("2028-01-28")))
        assertTrue(EscenaRuinas.granizo(momento("2030-05-22"))); assertFalse(EscenaRuinas.granizo(momento("2029-05-22")))
        assertTrue(EscenaRuinas.ciervos(momento("2027-10-18", 8.0))); assertFalse(EscenaRuinas.ciervos(momento("2027-10-18", 15.0))); assertFalse(EscenaRuinas.ciervos(momento("2027-10-19", 8.0)))
        assertTrue(EscenaRuinas.sellandoLatas(momento("2029-10-01"))); assertTrue(EscenaRuinas.sellandoLatas(momento("2029-03-20"))); assertFalse(EscenaRuinas.sellandoLatas(momento("2029-10-02")))
    }

    @Test fun elGatoTieneDondeEstar() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 12.0, 13.4, 16.0, 18.0, 20.0, 21.4)) {
                val m = momento(d.toString(), h); val anclas = EscenaRuinas.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: suelo libre", suelo.hasta - suelo.x >= 110f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h: $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h: ${a.tipo} dentro del cuadro", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO && a.y in 0f..ALTO) }
                assertEquals("$d $h: el gato va al fuego si hay lumbre", EscenaRuinas.lumbre(m) > 0f, anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d $h: el adorno, en fiesta", EscenaRuinas.fiesta(m), anclas.any { it.tipo == TipoAncla.ADORNO })
                for (x in EscenaRuinas.estorbos(m)) assertTrue("$d $h: estorbo en el suelo del gato", x in suelo.x..suelo.hasta)
                assertTrue(EscenaRuinas.seMueve(m))
            }
            d = d.plusDays(1)
        }
    }
}
