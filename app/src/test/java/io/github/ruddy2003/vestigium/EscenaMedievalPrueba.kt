package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMedieval
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosMedieval
import io.github.ruddy2003.vestigium.ui.escenas.PELAJE_MEDIEVAL
import io.github.ruddy2003.vestigium.ui.escenas.PupitresMedieval
import io.github.ruddy2003.vestigium.ui.escenas.SalaMedieval
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
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
import kotlin.math.abs

/**
 * El cuadro del scriptorium (tanda S6) cuenta lo mismo que el cuento del hermano Odo: cómo va la Biblia, qué hay en
 * su pupitre, quién falta y qué se ve por las ventanas salen de la ficha del día, y aquí se comparan con su
 * calendario. También repite, para esta escena, lo que las pruebas del gato piden a todas las escenas.
 */
class EscenaMedievalPrueba {
    private val E = EscenaMedieval
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "copista")!!, hora)
    private fun despejado(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "copista")!!.copy(clima = Clima.DESPEJADO), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeOdo() {
        assertEquals(listOf("copista"), E.vidas)
        assertEquals("copista", E.vida("2027-05-10"))
        val vida = OtrasVidas.porId("copista")
        assertNotNull(vida); assertEquals("medieval", vida!!.tema)
    }

    @Test fun suSitioQuedaVacioSoloMientrasEstaEnLorsch() {
        var dias = 0
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.estado == "lorsch", E.enLorsch(m))
            if (E.enLorsch(m)) {
                dias++
                assertEquals("$d: en Lorsch es la temporada 2", 2, m.ficha.temporada)
                assertFalse("$d: en Lorsch no hay libro en su pupitre", E.copiando(m))
                for (h in listOf(8.0, 14.0, 21.0)) { val q = momento(d.toString(), h); assertFalse("$d $h", E.trabajando(q)); assertFalse("$d $h: no hay página que cubrir", E.cubierto(q)); assertEquals(0, E.renglones(q)) }
            }
        }
        // Es una estancia corta (decisión 21): menos de ocho semanas en los siete años, así que es variante y no cuadro.
        assertTrue("días en Lorsch: $dias", dias in 20..55)
        assertTrue(E.enLorsch(momento("2028-06-20"))); assertFalse(E.enLorsch(momento("2028-06-04"))); assertFalse(E.enLorsch(momento("2028-07-09")))
        assertFalse(E.enLorsch(momento("2027-06-20"))); assertFalse(E.enLorsch(momento("2029-06-20")))
    }

    @Test fun elLibroSigueElArcoYLosSucesosDelCuento() {
        cadaDia { d, m ->
            assertEquals("$d", m.arco("libro").coerceIn(0f, 1f), E.libro(m), 0.0001f)
            assertTrue("$d: cuadernos", E.cuadernos(m) in 0..8); assertTrue("$d: tomos", E.tomos(m) in 0..2)
            if (!E.encargada(m)) { assertEquals("$d", 0, E.cuadernos(m)); assertFalse("$d", E.copiando(m)) }
            if (E.bibliaEnElCoro(m)) assertEquals("$d: la Biblia ya está en el coro", 0, E.tomos(m))
            if (E.bibliaTerminada(m) && !E.libroNuevo(m)) { assertFalse("$d: entre un libro y otro no copia", E.copiando(m)); assertEquals(0, E.cuadernos(m)) }
        }
        // Las fechas del calendario, una por una (el año 2027 es la temporada 1).
        assertFalse(E.encargada(momento("2027-01-11"))); assertTrue(E.encargada(momento("2027-01-12")))
        assertFalse(E.pieles(momento("2027-03-09"))); assertTrue(E.pieles(momento("2027-03-10")))
        assertFalse(E.inicial(momento("2027-10-14"))); assertTrue(E.inicial(momento("2027-10-15")))
        assertFalse(E.heinrich(momento("2028-01-19"))); assertTrue(E.heinrich(momento("2028-01-20")))
        assertFalse(E.llave(momento("2028-11-24"))); assertTrue(E.llave(momento("2028-11-25")))
        assertEquals(0, E.tomos(momento("2029-09-07"))); assertEquals(1, E.tomos(momento("2029-09-08")))
        assertEquals(1, E.tomos(momento("2030-06-28"))); assertEquals(2, E.tomos(momento("2030-06-29"))); assertEquals(0, E.tomos(momento("2030-06-30")))
        assertFalse(E.piedraDeLeer(momento("2030-10-19"))); assertTrue(E.piedraDeLeer(momento("2030-10-20")))
        assertFalse(E.heinrichCopia(momento("2031-06-23"))); assertTrue(E.heinrichCopia(momento("2031-06-24")))
        assertFalse(E.libroNuevo(momento("2031-08-27"))); assertTrue(E.libroNuevo(momento("2031-08-28"))); assertTrue(E.copiando(momento("2031-09-10")))
        // Los cuadernos se apilan con el avance y vuelven a empezar con el segundo tomo.
        assertTrue(E.cuadernos(momento("2029-08-30")) >= 7); assertTrue(E.cuadernos(momento("2029-09-20")) <= 1)
        assertTrue(E.cuadernos(momento("2027-03-01")) < E.cuadernos(momento("2028-12-01")))
    }

    @Test fun elTejadoDelCampanarioSeQuemaConElRayo() {
        assertFalse(E.tejadoQuemado(momento("2029-07-27"))); assertTrue(E.tejadoQuemado(momento("2029-07-28"))); assertTrue(E.tejadoQuemado(momento("2029-12-01")))
        assertFalse(E.tejadoQuemado(momento("2030-04-01"))); assertFalse(E.tejadoQuemado(momento("2027-07-28")))
    }

    @Test fun laPaginaSeLlenaConLasHorasDeTrabajoYSeCubreAlIrse() {
        val dia = "2027-05-12"
        assertFalse(E.cerrado(momento(dia)))
        val r = listOf(7.0, 9.0, 10.9, 13.5, 16.0).map { E.renglones(momento(dia, it)) }
        assertEquals("los renglones no van a menos: $r", r.sorted(), r); assertTrue(r.first() < r.last()); assertTrue(r.all { it in 0..22 })
        for (h in listOf(8.0, 14.0)) { val m = momento(dia, h); assertEquals(Hacer.TRABAJO, m.hacer); assertTrue(E.trabajando(m)); assertFalse(E.cubierto(m)) }
        for (h in listOf(3.0, 6.0, 11.5, 12.5, 17.0, 21.0)) { val m = momento(dia, h); assertFalse("$h", E.trabajando(m)); assertTrue("$h: la página, cubierta", E.cubierto(m)) }
        // En las fiestas grandes y en la sangría el scriptorium está cerrado: nadie copia a ninguna hora.
        for (fiesta in listOf("2027-12-25", "2027-02-02", "2027-05-05", "2027-11-11")) { val m = momento(fiesta, 9.0); assertTrue(fiesta, E.cerrado(m)); assertFalse(fiesta, E.trabajando(m)); assertFalse(fiesta, E.salaEnMarcha(m)); assertEquals(0, E.renglones(m)) }
    }

    @Test fun lasClavesDeEscenaYLoDeFueraSalenDeLaFicha() {
        var vendimias = 0; var campanas = 0; var lutos = 0
        cadaDia { d, m ->
            assertEquals("$d", m.con("vendimia"), E.vendimia(m)); assertEquals("$d", m.con("campanas"), E.campanas(m)); assertEquals("$d", m.con("luto"), E.luto(m)); assertEquals("$d", m.con("fiesta"), E.fiesta(m))
            if (m.ficha.clima == Clima.NIEVE || m.con("nieve")) assertTrue("$d", E.nevado(m)) else assertFalse("$d", E.nevado(m))
            if (E.vendimia(m)) vendimias++; if (E.campanas(m)) campanas++; if (E.luto(m)) lutos++
        }
        assertTrue("vendimia: $vendimias", vendimias >= 60); assertTrue("campanas: $campanas", campanas >= 30); assertTrue("luto: $lutos", lutos >= 5)
        assertTrue(E.candelaria(momento("2027-02-02"))); assertTrue(E.procesion(momento("2027-02-02", 7.0))); assertFalse(E.procesion(momento("2027-02-02", 13.0)))
        assertTrue(E.helada(momento("2030-01-15"))); assertTrue(E.nevado(momento("2030-01-15")))
        // Las ventanas de la iglesia se encienden en los oficios de noche; de día, no.
        assertTrue(SalaMedieval.iglesiaEncendida(despejado("2027-05-12", 2.5)) > 0.7f)
        assertTrue(SalaMedieval.iglesiaEncendida(despejado("2027-05-12", 23.0)) in 0.1f..0.4f)
        assertEquals(0f, SalaMedieval.iglesiaEncendida(despejado("2027-05-12", 13.0)), 0.001f)
    }

    @Test fun tresHorasDanTresLuces() {
        val dia = "2027-05-12"
        val manana = E.sol(despejado(dia, 7.0)); val mediodia = E.sol(despejado(dia, 13.0)); val noche = E.sol(despejado(dia, 21.0))
        assertNotNull(manana); assertNotNull(mediodia)
        assertFalse(manana!!.deLuna); assertFalse(mediodia!!.deLuna)
        // Por la mañana el sol entra tendido y cruza la sala; a mediodía cae a plomo junto a la ventana.
        assertTrue(mediodia.e > manana.e * 1.5f)
        // De noche no hay sol: si entra algo, es la luna; y la lámpara del dormitorio se cuela por la puerta.
        if (noche != null) assertTrue(noche.deLuna)
        assertEquals(0f, E.lampara(despejado(dia, 13.0)), 0.001f); assertTrue(E.lampara(despejado(dia, 23.0)) > 0.9f)
        assertTrue(E.focos(despejado(dia, 23.0)).any { it.p == SalaMedieval.LUZ_PUERTA }); assertFalse(E.focos(despejado(dia, 13.0)).any { it.p == SalaMedieval.LUZ_PUERTA })
        // Con el cielo cubierto no entra mancha de sol.
        assertNull(E.sol(Momento(FichaDelDia.de(dia, "copista")!!.copy(clima = Clima.LLUVIA), 13.0)))
        // Los focos son siempre válidos y las dos ventanas van las primeras.
        for (h in listOf(0.0, 5.5, 7.0, 13.0, 19.0, 21.0)) { val f = E.focos(despejado(dia, h)); assertEquals(SalaMedieval.LUZ_SUR, f[0].p); assertEquals(SalaMedieval.LUZ_CLAUSTRO, f[1].p); assertTrue(f.all { it.fuerza in 0f..1.3f && it.alcance > 0f }) }
    }

    @Test fun losDoceObjetosSonLosDelCalendario() {
        val vistos = HashSet<String>()
        cadaDia { d, m -> m.ficha.objeto?.let { vistos += it; assertTrue("$d: el objeto «$it» no tiene dibujo", it in ObjetosMedieval.TODOS) } }
        assertEquals(ObjetosMedieval.TODOS.toSet(), vistos)
        assertEquals(12, ObjetosMedieval.TODOS.size)
    }

    @Test fun elGatoTieneSueloLuzYSusDosAcciones() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        assertTrue(E.tallaDelGato in 0.8f..1.8f)
        assertEquals(2, PELAJE_MEDIEVAL.especiales.size)
        for (dia in listOf("2027-01-20", "2027-05-05", "2028-06-20", "2028-08-21", "2029-03-18", "2030-11-10", "2027-04-16", "2027-12-25")) for (hora in listOf(3.0, 7.0, 13.0, 14.7, 17.0, 20.5, 23.0)) {
            val m = momento(dia, hora)
            val anclas = E.anclas(m); val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
            assertNotNull("$dia $hora: no hay suelo para el gato", suelo); suelo!!
            assertTrue("el suelo es muy corto para caminar", suelo.hasta - suelo.x >= 2 * Gatuno.MEDIO_LARGO * E.tallaDelGato + 60f)
            assertTrue("el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * E.tallaDelGato > 150f && suelo.y <= ALTO)
            assertTrue(anclas.all { it.x in 0f..ANCHO && it.hasta in 0f..ANCHO && it.y in 0f..ALTO })
            assertEquals(1, anclas.count { it.tipo == TipoAncla.MIRADOR }); assertEquals(1, anclas.count { it.tipo == TipoAncla.OBJETO }); assertEquals(1, anclas.count { it.tipo == TipoAncla.ABRIGO })
            assertEquals("$dia: el adorno, solo en fiesta o con campanas", E.fiesta(m) || E.campanas(m), anclas.any { it.tipo == TipoAncla.ADORNO })
            // Lo plantado cae dentro del tramo del gato, y cuando no se anima no queda encima de nada.
            val plantado = E.estorbos(m)
            plantado.forEach { x -> assertTrue("lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta) }
            val quieto = gatoQuieto(suelo, anclas, E.tallaDelGato, plantado)
            assertEquals(AnimGato.SENTADO, quieto.anim)
            plantado.forEach { x -> assertTrue("$dia $hora: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= Gatuno.MEDIO_LARGO * E.tallaDelGato) }
            assertTrue("quieto se sale de su tramo", quieto.x - Gatuno.MEDIO_LARGO * E.tallaDelGato >= suelo.x - 0.01f && quieto.x + Gatuno.MEDIO_LARGO * E.tallaDelGato <= suelo.hasta + 0.01f)
            anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$dia $hora: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= Gatuno.MEDIO_LARGO * E.tallaDelGato) }
            for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                val luz = E.luzDelGato(m, Offset(x, suelo.y))
                for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$dia $hora: un color del gato no es válido", valido(luz.tono(c)))
                assertTrue("$dia $hora: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
            }
        }
        // El objeto del día está en su banco, fuera del pupitre.
        assertEquals(PupitresMedieval.BANCO.x, E.anclas(momento("2027-05-12")).first { it.tipo == TipoAncla.OBJETO }.x, 0.01f)
    }

    @Test fun seMueveSoloCuandoHayAlgoQueSeMueva() {
        // Un mediodía despejado de invierno, sin campanas ni abejas: nada se mueve y el cuadro no pide animación.
        val quieto = (1..28).map { despejado("2027-01-%02d".format(it), 13.0) }.filter { !E.volteo(it) && !E.procesion(it) }
        assertTrue(quieto.isNotEmpty()); assertTrue(quieto.all { !E.seMueve(it) })
        assertTrue(E.seMueve(despejado("2027-05-12", 23.0)))                    // la lámpara del dormitorio
        assertTrue(E.seMueve(Momento(FichaDelDia.de("2027-05-12", "copista")!!.copy(clima = Clima.LLUVIA), 13.0)))
        assertTrue(E.seMueve(momento("2027-02-02", 7.0)))                        // la procesión de las velas
    }
}
