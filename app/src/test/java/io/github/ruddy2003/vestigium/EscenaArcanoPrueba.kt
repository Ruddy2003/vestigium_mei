package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.BahiaArcano
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Niebla
import io.github.ruddy2003.vestigium.ui.escenas.EscenaArcano.Torrero
import io.github.ruddy2003.vestigium.ui.escenas.GaleriaArcano
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
 * El cuadro de Horror cósmico (tanda S4) cuenta lo mismo que el cuento del archivista: cuántas cajas le quedan por
 * abrir, en cuántos sitios aparece el signo, quién cuida el faro y si da luz, cómo está el muelle, dónde están las
 * barcas, cuánta niebla hay y dónde pasa él el día salen de la ficha, y aquí se comparan con su calendario.
 */
class EscenaArcanoPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "archivista")!!, hora)

    @Test fun laEscenaEsLaDelArchivista() {
        assertEquals(listOf("archivista"), EscenaArcano.vidas)
        assertEquals("archivista", EscenaArcano.vida("2027-05-10"))
    }

    @Test fun estaDondeDiceElCuento() {
        // Temporada 3 (2029): cae enfermo el 1 de marzo y se levanta el 25. Temporada 4 (2030): sube al faro el 20 de enero y baja el 10 de febrero.
        assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("2029-02-28")))
        for (dia in listOf("2029-03-01", "2029-03-12", "2029-03-24")) assertEquals(dia, Lugar.POSADA, EscenaArcano.lugar(momento(dia)))
        assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("2029-03-25")))
        assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("2030-01-19")))
        for (dia in listOf("2030-01-20", "2030-02-01", "2030-02-09")) assertEquals(dia, Lugar.FARO, EscenaArcano.lugar(momento(dia)))
        assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("2030-02-10")))
        // Los otros años, esos días está en el archivo.
        for (anio in listOf(2027, 2028, 2031)) { assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("$anio-03-12"))); assertEquals(Lugar.ARCHIVO, EscenaArcano.lugar(momento("$anio-02-01"))) }
        // Mientras no está, el archivo no se abre a ninguna hora, y su ventana de la posada o la casa del torrero tienen luz.
        for (h in listOf(2.0, 9.5, 13.0, 18.5, 22.0)) { assertFalse(EscenaArcano.archivoAbierto(momento("2029-03-12", h))); assertFalse(EscenaArcano.archivoAbierto(momento("2030-02-01", h))); assertFalse(EscenaArcano.lamparaDeLaPuerta(momento("2029-03-12", h))) }
        assertTrue(EscenaArcano.ventanaDeLaPosada(momento("2029-03-12", 13.0))); assertFalse(EscenaArcano.ventanaDeLaPosada(momento("2029-03-26", 13.0)))
        assertTrue(EscenaArcano.casaDelTorrero(momento("2030-02-01", 22.0)))
    }

    @Test fun cadaAnioQuedanMenosCajasYHayMasCajonesLlenos() {
        // El arco «archivo» va de 0.05 a 1: de catorce cajas a ninguna, y de un cajón del fichero a los doce.
        assertEquals(14, EscenaArcano.cajasPorAbrir(momento("2027-01-02"))); assertEquals(0, EscenaArcano.cajasPorAbrir(momento("2031-12-30")))
        assertEquals(1, EscenaArcano.cajonesLlenos(momento("2027-01-02"))); assertEquals(12, EscenaArcano.cajonesLlenos(momento("2031-12-30")))
        var cajas = 15; var cajones = 0
        var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val m = momento(d.toString())
            assertTrue("$d: las cajas no vuelven", EscenaArcano.cajasPorAbrir(m) <= cajas); cajas = EscenaArcano.cajasPorAbrir(m)
            assertTrue("$d: los cajones no se vacían", EscenaArcano.cajonesLlenos(m) >= cajones); cajones = EscenaArcano.cajonesLlenos(m)
            assertEquals("$d", cajas, GaleriaArcano.cajasPresentes(m).size)
            d = d.plusDays(7)
        }
        // Mientras está en cama (temporada 3) el archivo no avanza.
        assertEquals(EscenaArcano.cajasPorAbrir(momento("2029-03-01")), EscenaArcano.cajasPorAbrir(momento("2029-03-25")))
        // El día en que abre la última caja (10 de septiembre de la temporada 5) queda una; al terminar el catálogo (20 de octubre), ninguna.
        assertEquals(1, EscenaArcano.cajasPorAbrir(momento("2031-09-10"))); assertEquals(0, EscenaArcano.cajasPorAbrir(momento("2031-10-20")))
        // Una caja de arriba nunca queda sin la que la sostiene: las presentes son siempre las primeras del orden.
        for (n in listOf("2027-06-01", "2028-06-01", "2029-06-01", "2030-06-01")) { val p = GaleriaArcano.cajasPresentes(momento(n)); assertEquals(p.size, p.toSet().size) }
    }

    @Test fun elSignoApareceElDiaQueDiceElCalendarioYYaNoSeVa() {
        // Lo ve por primera vez el 14 de septiembre de la temporada 1.
        assertEquals(0, EscenaArcano.signos(momento("2027-01-02"))); assertEquals(0, EscenaArcano.signos(momento("2027-09-13")))
        assertEquals(1, EscenaArcano.signos(momento("2027-09-14")))
        assertEquals(7, EscenaArcano.signos(momento("2031-12-30")))
        var antes = 0
        for (anio in 2027..2031) for (mes in listOf("01-15", "05-15", "09-20", "12-15")) { val s = EscenaArcano.signos(momento("$anio-$mes")); assertTrue("$anio-$mes", s >= antes); antes = s }
    }

    @Test fun elFaroLoCuidaQuienDiceElCuento() {
        // Baltasar muere el 22 de octubre de la temporada 4; el torrero nuevo llega el 5 de marzo de la 5.
        assertEquals(Torrero.BALTASAR, EscenaArcano.torrero(momento("2027-06-01"))); assertEquals(Torrero.BALTASAR, EscenaArcano.torrero(momento("2030-10-21")))
        assertEquals(Torrero.NADIE, EscenaArcano.torrero(momento("2030-10-22"))); assertEquals(Torrero.NADIE, EscenaArcano.torrero(momento("2031-03-04")))
        assertEquals(Torrero.NUEVO, EscenaArcano.torrero(momento("2031-03-05"))); assertEquals(Torrero.NUEVO, EscenaArcano.torrero(momento("2031-12-01")))
        // Con torrero, el faro da luz de noche y está apagado a mediodía de un día claro.
        val claro = FichaDelDia.de("2027-06-01", "archivista")!!.copy(clima = Clima.DESPEJADO)
        assertTrue(EscenaArcano.faroEncendido(Momento(claro, 23.0))); assertFalse(EscenaArcano.faroEncendido(Momento(claro, 13.0)))
        // Con niebla se enciende también de día.
        assertTrue(EscenaArcano.faroEncendido(Momento(claro.copy(clima = Clima.NIEBLA), 13.0)))
        // Sin torrero lo encienden tarde: al anochecer sigue apagado, y a medianoche ya no.
        val sinTorrero = FichaDelDia.de("2030-12-01", "archivista")!!.copy(clima = Clima.DESPEJADO)
        assertFalse(EscenaArcano.faroEncendido(Momento(sinTorrero, 21.0))); assertTrue(EscenaArcano.faroEncendido(Momento(sinTorrero, 23.6)))
        // La noche del 12 de diciembre de la temporada 2 no se enciende a ninguna hora.
        for (h in listOf(13.0, 19.0, 21.0, 23.6, 2.0)) assertFalse("$h", EscenaArcano.faroEncendido(momento("2028-12-12", h)))
        assertTrue(EscenaArcano.faroEncendido(momento("2028-12-11", 23.0)))
        // Con el torrero nuevo la luz gira más despacio.
        assertTrue(EscenaArcano.vueltaDelFaro(momento("2031-06-01")) > EscenaArcano.vueltaDelFaro(momento("2027-06-01")))
    }

    @Test fun elMuelleSeRompeConElTemporalDeLaTemporadaTres() {
        assertFalse(EscenaArcano.muelleRoto(momento("2027-06-01"))); assertFalse(EscenaArcano.muelleRoto(momento("2029-09-29")))
        for (dia in listOf("2029-09-30", "2029-10-02", "2029-12-01", "2030-06-01", "2031-06-01")) assertTrue(dia, EscenaArcano.muelleRoto(momento(dia)))
        // Sin la mitad de fuera no queda farol en la punta.
        assertFalse(BahiaArcano.farolEncendido(momento("2030-06-01", 23.0))); assertTrue(BahiaArcano.farolEncendido(momento("2027-06-01", 23.0)))
        assertTrue(EscenaArcano.temporal(momento("2029-09-30"))); assertTrue(EscenaArcano.temporal(momento("2027-03-12")))
    }

    @Test fun lasBarcasEstanDondeTocaEnCadaEpoca() {
        // Varadas del 15 de octubre a la bendición del 1 de mayo; a flote el resto del año.
        for (dia in listOf("2027-10-15", "2027-12-25", "2028-02-02", "2028-04-30")) { assertTrue(dia, EscenaArcano.barcasVaradas(momento(dia))); assertTrue(dia, BahiaArcano.barcasAFlote(momento(dia)).isEmpty()) }
        for (dia in listOf("2027-05-01", "2027-07-16", "2027-10-14")) assertFalse(dia, EscenaArcano.barcasVaradas(momento(dia)))
        // En la temporada salen a faenar por la mañana (si no hay temporal ni fiesta) y por la tarde están en el puerto.
        val verano = FichaDelDia.de("2027-07-08", "archivista")!!.copy(clima = Clima.DESPEJADO)
        assertTrue(EscenaArcano.barcasFuera(Momento(verano, 7.0))); assertFalse(EscenaArcano.barcasFuera(Momento(verano, 13.0)))
        assertTrue(BahiaArcano.barcasAFlote(Momento(verano, 7.0)).size < BahiaArcano.barcasAFlote(Momento(verano, 13.0)).size)
        assertFalse(EscenaArcano.barcasFuera(Momento(verano.copy(clima = Clima.TORMENTA), 7.0)))
        // El día de la Virgen del Carmen dan la vuelta a la bahía.
        assertTrue(EscenaArcano.virgenEnBarca(momento("2027-07-16", 13.0))); assertFalse(EscenaArcano.virgenEnBarca(momento("2027-07-16", 22.0))); assertFalse(EscenaArcano.virgenEnBarca(momento("2027-07-17", 13.0)))
    }

    @Test fun losDiasSenaladosSeVen() {
        assertTrue(EscenaArcano.candelaria(momento("2027-02-02"))); assertTrue(EscenaArcano.velasEnLaBaranda(momento("2027-02-02", 22.0))); assertFalse(EscenaArcano.velasEnLaBaranda(momento("2027-02-03", 22.0)))
        assertTrue(EscenaArcano.bendicion(momento("2028-05-01"))); assertTrue(EscenaArcano.procesion(momento("2028-06-29"))); assertTrue(EscenaArcano.fiesta(momento("2028-06-29")))
        assertTrue(EscenaArcano.difuntos(momento("2029-11-02"))); assertTrue(EscenaArcano.luto(momento("2029-11-02"))); assertTrue(EscenaArcano.luto(momento("2030-10-22")))
        // La noche de la marea más baja el agua se va del todo; de día, no.
        assertTrue(EscenaArcano.mareaBajisima(momento("2027-08-23", 23.0))); assertEquals(0f, EscenaArcano.marea(momento("2027-08-23", 23.0)), 0f); assertFalse(EscenaArcano.mareaBajisima(momento("2027-08-23", 13.0)))
        // Los cinco días de niebla de la temporada 1 y los otros dos días de clave «niebla»: no se ve ni el muelle.
        for (dia in listOf("2027-11-20", "2027-11-22", "2027-11-24", "2027-08-23", "2028-12-12")) assertEquals(dia, Niebla.CERRADA, EscenaArcano.niebla(momento(dia)))
        // Lo que el mar trae y se queda unos días.
        assertFalse(EscenaArcano.naufragio(momento("2027-03-11"))); assertTrue(EscenaArcano.naufragio(momento("2027-03-12"))); assertTrue(EscenaArcano.naufragio(momento("2027-03-30"))); assertFalse(EscenaArcano.naufragio(momento("2027-04-03"))); assertFalse(EscenaArcano.naufragio(momento("2028-03-12")))
        assertTrue(EscenaArcano.barcaSeca(momento("2028-04-05"))); assertFalse(EscenaArcano.barcaSeca(momento("2028-04-15"))); assertFalse(EscenaArcano.barcaSeca(momento("2027-04-05")))
        assertTrue(EscenaArcano.ballena(momento("2029-05-20"))); assertTrue(EscenaArcano.ballena(momento("2029-05-26"))); assertFalse(EscenaArcano.ballena(momento("2029-05-27"))); assertFalse(EscenaArcano.ballena(momento("2030-05-20")))
        // El mar rojo: al alba del 20 de junio de la temporada 5, y se va con el día.
        assertTrue(EscenaArcano.marRojo(momento("2031-06-20", 7.0)) > 0.9f); assertEquals(0f, EscenaArcano.marRojo(momento("2031-06-20", 15.0)), 0f); assertEquals(0f, EscenaArcano.marRojo(momento("2031-06-21", 7.0)), 0f)
        assertTrue(EscenaArcano.sinPajaros(momento("2030-08-08"))); assertFalse(EscenaArcano.sinPajaros(momento("2030-08-09")))
        // La isla que no existe no asoma antes de que encuentre el mapa (28 de octubre de la temporada 2).
        var d = LocalDate.of(2027, 1, 1)
        while (d < LocalDate.of(2028, 10, 28)) { assertFalse("$d", EscenaArcano.islaQueNoExiste(momento(d.toString()))); d = d.plusDays(1) }
    }

    @Test fun elTramoDelDiaSeNotaEnLaGaleria() {
        val dia = "2027-05-10"
        // Duerme de 3 a 9, come de 9 a 10 y de 18 a 19, trabaja de 10 a 17, pasea de 17 a 18 y lee de 19 a 3.
        assertEquals(Hacer.DORMIR, momento(dia, 6.0).hacer); assertFalse(EscenaArcano.archivoAbierto(momento(dia, 6.0)))
        assertTrue(EscenaArcano.comiendo(momento(dia, 9.5))); assertTrue(EscenaArcano.trabajando(momento(dia, 13.0))); assertTrue(EscenaArcano.archivoAbierto(momento(dia, 13.0)))
        assertEquals(Hacer.CAMINO, momento(dia, 17.4).hacer); assertFalse(EscenaArcano.archivoAbierto(momento(dia, 17.4)))
        assertTrue(EscenaArcano.leyendo(momento(dia, 22.0))); assertTrue(EscenaArcano.lamparaDeLaMesa(momento(dia, 22.0))); assertFalse(EscenaArcano.lamparaDeLaMesa(momento(dia, 13.0)))
        // De noche, mientras lee, hay al menos dos luces en la galería: la de la puerta y la de la mesa.
        assertTrue(EscenaArcano.focos(momento(dia, 22.0)).size >= 2); assertTrue(EscenaArcano.focos(FichaDelDia.de(dia, "archivista")!!.copy(clima = Clima.DESPEJADO).let { Momento(it, 13.0) }).isEmpty())
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        var conNiebla = 0; var cerrada = 0; var enCama = 0; var enElFaro = 0
        while (d.year <= 2032) {
            val ficha = FichaDelDia.de(d.toString(), "archivista")!!
            for (h in listOf(1.0, 4.0, 6.0, 7.0, 9.4, 11.0, 13.0, 15.0, 17.4, 18.6, 20.4, 21.0, 23.4)) {
                val m = Momento(ficha, h)
                assertEquals("$d", when (ficha.estado) { "fiebre" -> Lugar.POSADA; "faro" -> Lugar.FARO; else -> Lugar.ARCHIVO }, EscenaArcano.lugar(m))
                // Con la clave «niebla» o con niebla en la ficha, el cuadro la pinta (salvo temporal); con viento no hay ninguna.
                if (ficha.clima == Clima.NIEBLA) assertTrue("$d $h", EscenaArcano.niebla(m).ordinal >= Niebla.NIEBLA.ordinal)
                if (ficha.clima == Clima.VIENTO || ficha.clima == Clima.TORMENTA) assertEquals("$d $h", Niebla.NINGUNA, EscenaArcano.niebla(m))
                if (EscenaArcano.niebla(m) == Niebla.CERRADA) assertTrue("$d $h", ficha.especiales.any { it.dia.escena == "niebla" })
                // Cerrado el archivo no hay nada encendido en la mesa; y nunca se abre mientras está fuera.
                if (EscenaArcano.lugar(m) != Lugar.ARCHIVO) { assertFalse("$d $h", EscenaArcano.archivoAbierto(m)); assertFalse("$d $h", EscenaArcano.lamparaDeLaMesa(m)); assertFalse("$d $h", EscenaArcano.legajosTendidos(m)) }
                if (EscenaArcano.muelleRoto(m)) assertFalse("$d $h", BahiaArcano.farolEncendido(m))
                if (EscenaArcano.barcasVaradas(m)) { assertFalse("$d $h", EscenaArcano.barcasFuera(m)); assertTrue("$d $h", BahiaArcano.barcasAFlote(m).isEmpty()) }
                assertTrue("$d $h", EscenaArcano.marea(m) in 0f..1f)
                assertTrue("$d $h", EscenaArcano.cajasPorAbrir(m) in 0..14); assertTrue("$d $h", EscenaArcano.signos(m) in 0..7)
                // Las anclas del gato: siempre hay suelo, mirador, objeto y abrigo, y caen dentro del cuadro.
                val anclas = EscenaArcano.anclas(m)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d $h $t", anclas.any { it.tipo == t })
                assertEquals("$d $h", EscenaArcano.fiesta(m) || EscenaArcano.bendicion(m), anclas.any { it.tipo == TipoAncla.ADORNO })
                assertFalse("$d $h", anclas.any { it.tipo == TipoAncla.FUEGO })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO); assertTrue("$d $h ${a.tipo}", a.y in 0f..ALTO) }
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }; assertTrue(suelo.hasta - suelo.x >= 110f)
                for (x in EscenaArcano.estorbos(m)) assertTrue("$d $h", x in suelo.x..suelo.hasta)
            }
            val medio = Momento(ficha, 13.0)
            if (EscenaArcano.niebla(medio).ordinal >= Niebla.NIEBLA.ordinal) conNiebla++
            if (EscenaArcano.niebla(medio) == Niebla.CERRADA) cerrada++
            if (d.year in 2027..2031) { if (EscenaArcano.lugar(medio) == Lugar.POSADA) enCama++; if (EscenaArcano.lugar(medio) == Lugar.FARO) enElFaro++ }
            d = d.plusDays(1)
        }
        // Tres semanas largas en cama (24 días) y tres en el faro (21); niebla, entre cinco y once días al mes.
        assertEquals(24, enCama); assertEquals(21, enElFaro)
        assertTrue("días con niebla: $conNiebla", conNiebla in 420..1000); assertTrue("días de niebla cerrada: $cerrada", cerrada >= 7)
    }
}
