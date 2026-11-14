package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRenacimiento
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRenacimiento.Motivo
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosRenacimiento
import io.github.ruddy2003.vestigium.ui.escenas.ObraRenacimiento
import io.github.ruddy2003.vestigium.ui.escenas.PinturasRenacimiento
import io.github.ruddy2003.vestigium.ui.escenas.TallerRenacimiento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Lado
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cae
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Renacimiento (tanda S6) cuenta lo mismo que el cuento de Iacopo: qué tabla hay en cada caballete y
 * cuánto lleva pintado, si él está en el taller o en el campo, los cartones de la capilla y lo que el día cambia salen
 * de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaRenacimientoPrueba {
    private val E = EscenaRenacimiento
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "aprendiz")!!, hora)
    private fun despejado(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "aprendiz")!!.copy(clima = Clima.DESPEJADO), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeIacopo() {
        assertEquals(listOf("aprendiz"), E.vidas)
        assertEquals("aprendiz", E.vida("2027-05-10"))
        // 2027 es su temporada 1 (1490): las fechas de las demás pruebas cuentan con eso.
        assertEquals(1, momento("2027-05-05").ficha.temporada); assertEquals(5, momento("2031-05-05").ficha.temporada)
    }

    @Test fun enElCampoElTallerEstaSinElYSoloEsasSeisSemanas() {
        var dias = 0
        cadaDia { d, m ->
            assertEquals("$d", m.ficha.estado == "campo", E.enElCampo(m))
            if (E.enElCampo(m)) {
                dias++
                assertFalse("$d: la losa se fue en la mula", E.losa(m)); assertEquals("$d: se llevaron los cartones", 0, E.cartones(m))
                assertFalse("$d: su jergón no se extiende", E.jergon(Momento(m.ficha, 2.0)))
                // Su banqueta no está en el suelo: no estorba al gato.
                assertEquals("$d", listOf(270f), E.estorbos(m))
            } else assertTrue("$d", E.losa(m))
        }
        assertTrue("entre 40 y 44 días en el campo, y son $dias", dias in 40..44)
        assertTrue(E.enElCampo(momento("2029-07-10"))); assertFalse(E.enElCampo(momento("2029-06-27"))); assertFalse(E.enElCampo(momento("2029-08-09")))
    }

    @Test fun laTablaDeIacopoSigueElArcoYSeVaCuandoLaFirma() {
        var antes = -1f
        cadaDia { d, m ->
            val t = m.ficha.temporada; val md = d.toString().substring(5)
            val debe = (t == 4 && md >= "03-10") || (t == 5 && md <= "05-25")
            assertEquals("$d: su tabla en el caballete", debe, E.conTabla(m))
            assertEquals("$d: el caballete está desde que la empieza", t > 4 || (t == 4 && md >= "03-10"), E.conCaballete(m))
            if (E.conTabla(m)) {
                assertEquals("$d", m.arco("tabla"), E.tablaDeIacopo(m), 0.0001f)
                if (!m.ficha.avance && antes >= 0f && d.year in 2030..2031) assertTrue("$d: la tabla no va a menos", E.tablaDeIacopo(m) >= antes - 0.0001f)
                antes = E.tablaDeIacopo(m)
            } else assertEquals("$d", 0f, E.tablaDeIacopo(m), 0f)
        }
        // El día que la empieza es madera enyesada; el día que la firma está acabada, y al siguiente ya no está.
        assertTrue(E.tablaDeIacopo(momento("2030-03-10")) < PinturasRenacimiento.DIBUJO)
        assertTrue(E.tablaDeIacopo(momento("2031-05-25")) >= 0.999f); assertTrue(E.firmada(momento("2031-05-25")))
        assertFalse(E.conTabla(momento("2031-05-26"))); assertTrue(E.conCaballete(momento("2031-05-26")))
        assertFalse(E.conCaballete(momento("2030-03-09")))
    }

    @Test fun lasTresTablasDelMaestroVanDelYesoAlCuadro() {
        cadaDia { d, m ->
            val t = E.tablaDelMaestro(m)
            assertTrue("$d: avance ${t.avance}", t.avance in 0f..1f)
            // El pastor no se pinta hasta que Iacopo posa.
            if (t.motivo == Motivo.PASTORES && !E.paso(m, 3, "01-22")) assertTrue("$d", t.avance <= 0.4f)
            if (t.motivo == Motivo.PASTORES && E.paso(m, 3, "01-22")) assertTrue("$d", t.avance >= 0.42f)
            // Mientras el taller está de luto por el maestro, su tabla no avanza y está tapada.
            if (E.maestroDeLuto(m)) { assertEquals("$d", 0.62f, t.avance, 0.0001f); assertTrue("$d", E.tapadas(m)); assertTrue("$d", E.luto(m)) }
        }
        assertEquals(Motivo.ENCUENTRO, E.tablaDelMaestro(momento("2027-05-05")).motivo)
        // El día `visitacion` el taller termina la tabla.
        assertTrue(E.hoyEs(momento("2028-05-28"), "visitacion")); assertEquals(1f, E.tablaDelMaestro(momento("2028-05-28")).avance, 0.0001f)
        assertTrue(E.tablaDelMaestro(momento("2028-05-20")).avance < 1f)
        assertEquals(Motivo.PASTORES, E.tablaDelMaestro(momento("2028-06-12")).motivo); assertTrue(E.tablaDelMaestro(momento("2028-06-12")).avance < 0.01f)
        assertTrue(E.posa(momento("2029-01-22")))
        assertEquals(Motivo.SANTOS, E.tablaDelMaestro(momento("2030-03-01")).motivo)
        // Muere el maestro el 11 de enero de la temporada 5: la tabla queda a medias y no se termina ese año.
        assertTrue(E.maestroDeLuto(momento("2031-01-11"))); assertTrue(E.maestroDeLuto(momento("2031-02-23"))); assertFalse(E.maestroDeLuto(momento("2031-02-24"))); assertFalse(E.maestroDeLuto(momento("2031-01-10")))
        assertTrue(E.tablaDelMaestro(momento("2031-12-31")).avance < 0.9f)
    }

    @Test fun losCartonesDeLaCapillaSeEnrollanCuandoSeDescubrenLosFrescos() {
        cadaDia { d, m ->
            val n = E.cartones(m)
            if (E.enElCampo(m)) assertEquals("$d", 0, n)
            else if (E.paso(m, 1, "12-22")) { assertEquals("$d", 1, n); assertTrue("$d", E.cartonesEnrollados(m)) }
            else { assertTrue("$d", n == 2 || n == 3); assertFalse("$d", E.cartonesEnrollados(m)); assertEquals("$d", m.arco("capilla") < 0.93f, n == 3) }
        }
        assertEquals(3, E.cartones(momento("2027-02-01"))); assertEquals(2, E.cartones(momento("2027-12-01"))); assertEquals(1, E.cartones(momento("2027-12-22")))
        assertFalse(E.ultramar(momento("2027-01-17"))); assertTrue(E.ultramar(momento("2027-01-18")))
        assertFalse(E.cinta(momento("2028-09-28"))); assertTrue(E.cinta(momento("2028-09-29")))
        assertFalse(E.andamioEnLaLinterna(momento("2029-04-04"))); assertTrue(E.andamioEnLaLinterna(momento("2029-04-05"))); assertFalse(E.andamioEnLaLinterna(momento("2029-07-01")))
    }

    @Test fun lasClavesDelDiaSeVen() {
        cadaDia { d, m ->
            if (m.con("fiesta")) assertTrue("$d", E.fiesta(m))
            if (m.con("luto")) assertTrue("$d", E.luto(m))
            if (m.con("nieve")) assertTrue("$d", E.nevado(m))
            assertEquals("$d", m.con("fuego"), E.carroDeFuego(m)); assertEquals("$d", m.con("campanas"), E.campanas(m)); assertEquals("$d", m.con("guerra"), E.guerra(m))
            if (m.ficha.clima == Clima.NIEVE) assertTrue("$d", E.nevado(m))
            // Con el rey de Francia dentro, las contraventanas casi cerradas.
            assertEquals("$d", if (m.con("guerra")) 0.3f else 1f, E.abierta(m), 0f)
        }
        assertTrue(E.guerra(momento("2031-11-17"))); assertTrue(E.carroDeFuego(momento("2027-04-11"))); assertTrue(E.mayo(momento("2027-05-01"))); assertTrue(E.sanJuan(momento("2027-06-24")))
        assertTrue(E.sinLena(momento("2028-12-10"))); assertFalse(E.lumbre(momento("2028-12-10", 10.0))); assertFalse(E.lumbre(momento("2028-12-10", 19.0)))
        // Los días de guardar el taller no trabaja: ni lumbre de cola, y las tablas, tapadas.
        val pascua = momento("2027-04-11", 10.0)
        assertTrue(E.cerrado(pascua)); assertFalse(E.trabajando(pascua)); assertTrue(E.tapadas(pascua))
        assertFalse(E.cerrado(momento("2027-02-23", 10.0))); assertTrue(E.cerrado(momento("2027-02-23", 13.0)))
    }

    @Test fun elTramoDelDiaCambiaElTaller() {
        val dia = "2027-05-05"
        assertEquals(Hacer.TRABAJO, momento(dia, 10.0).hacer)
        assertTrue(E.trabajando(momento(dia, 10.0))); assertTrue(E.lumbre(momento(dia, 10.0))); assertFalse(E.tapadas(momento(dia, 10.0))); assertFalse(E.lampara(momento(dia, 10.0)))
        assertTrue(E.comiendo(momento(dia, 12.4))); assertFalse(E.comiendo(momento(dia, 10.0)))
        assertTrue(E.lampara(momento(dia, 19.0))); assertTrue(E.tapadas(momento(dia, 19.0))); assertFalse(E.lumbre(momento(dia, 19.0))); assertTrue(E.rescoldo(momento(dia, 19.0)))
        assertTrue(E.jergon(momento(dia, 23.0))); assertTrue(E.entornada(momento(dia, 23.0))); assertFalse(E.lampara(momento(dia, 23.0))); assertFalse(E.jergon(momento(dia, 10.0)))
        // En invierno, el brasero por la tarde.
        assertTrue(E.lumbre(momento("2027-01-20", 19.0)))
        // Lo que se mueve: de día y sin lumbre ni lluvia, nada.
        assertFalse(E.seMueve(despejado("2027-08-15", 14.6))); assertTrue(E.seMueve(momento(dia, 19.0)))
    }

    @Test fun elSolEntraDistintoALasTresHoras() {
        val dia = "2027-05-05"
        val manana = E.solQueEntra(despejado(dia, 7.0)); val mediodia = E.solQueEntra(despejado(dia, 13.0)); val noche = E.solQueEntra(despejado(dia, 23.0))
        assertNotNull(manana); assertNotNull(mediodia)
        // Con el sol bajo el rayo cruza más sala antes de tocar el suelo, y las sombras son más largas.
        assertTrue(mediodia!!.e > manana!!.e * 1.5f)
        val pie = Offset(144f, 246f)
        val r7 = E.rayoEn(despejado(dia, 7.0), E.focos(despejado(dia, 7.0)), pie, 1f)!!; val r13 = E.rayoEn(despejado(dia, 13.0), E.focos(despejado(dia, 13.0)), pie, 1f)!!
        assertTrue("a las 7:00 la sombra es más larga que a las 13:00", abs(r7.ax) > abs(r13.ax) * 1.4f)
        assertTrue("las dos caen hacia la derecha, lejos de la ventana de la calle", r7.ax > 0f && r13.ax > 0f)
        // La mancha cae dentro del cuadro y en otro sitio a cada hora.
        val c7 = E.centroDeLaMancha(manana); val c13 = E.centroDeLaMancha(mediodia)
        assertTrue(abs(c7.x - c13.x) > 20f)
        for (c in listOf(c7, c13)) assertTrue(c.x in 0f..ANCHO && c.y in 0f..ALTO)
        if (noche != null) assertTrue(noche.deLuna)
        // De noche manda otra luz: la lámpara encendida tumba la sombra de lo que tiene cerca.
        val m19 = despejado(dia, 20.6); assertTrue(E.focos(m19).any { it.p == ObraRenacimiento.LUZ_LAMPARA })
        // Los claros de la ventana de la calle caen en el suelo de la sala.
        val claro = TallerRenacimiento.clarosDeLaCalle().first().first()
        assertTrue(TallerRenacimiento.E.cae(claro.first, claro.second, mediodia, Lado.IZQUIERDA).y >= TallerRenacimiento.SUELO - 0.01f)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val vistos = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { vistos += it } }
        assertEquals(ObjetosRenacimiento.TODOS.sorted(), vistos.toList())
    }

    /** Lo mismo que `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` pide a todas las escenas registradas, para esta. */
    @Test fun elGatoTieneSueloLuzYSitioDondeNoEstorba() {
        assertTrue("la talla del gato", E.tallaDelGato in 0.8f..1.8f)
        val medio = Gatuno.MEDIO_LARGO * E.tallaDelGato
        for (dia in listOf("2027-01-20", "2027-05-05", "2028-08-21", "2029-03-18", "2029-07-10", "2030-11-10", "2027-05-01", "2028-12-10", "2031-02-01")) for (hora in listOf(3.0, 7.0, 12.4, 13.0, 14.7, 17.0, 19.0, 20.5, 23.0)) {
            val m = momento(dia, hora); val anclas = E.anclas(m)
            val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
            assertTrue("$dia $hora: no hay suelo para el gato", suelo != null)
            assertTrue("el suelo es muy corto para caminar", suelo!!.hasta - suelo.x >= 2 * medio + 60f)
            assertTrue("el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * E.tallaDelGato > 150f && suelo.y <= 285f)
            val plantado = E.estorbos(m)
            plantado.forEach { x -> assertTrue("$dia $hora: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta) }
            val quieto = gatoQuieto(suelo, anclas, E.tallaDelGato, plantado)
            assertEquals(AnimGato.SENTADO, quieto.anim)
            plantado.forEach { x -> assertTrue("$dia $hora: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio) }
            assertTrue("$dia $hora: quieto se sale de su tramo", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
            anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$dia $hora: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
            // Todas las anclas caen dentro del cuadro, y el fuego solo está si hay lumbre.
            anclas.forEach { a -> assertTrue("$dia $hora: ${a.tipo} fuera del cuadro", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO) }
            assertEquals("$dia $hora", E.lumbre(m), anclas.any { it.tipo == TipoAncla.FUEGO }); assertEquals("$dia $hora", E.fiesta(m), anclas.any { it.tipo == TipoAncla.ADORNO })
            assertTrue(anclas.any { it.tipo == TipoAncla.MIRADOR } && anclas.any { it.tipo == TipoAncla.OBJETO } && anclas.any { it.tipo == TipoAncla.ABRIGO })
            // La luz que le llega es un color válido, en cualquier sitio de su suelo.
            for (x in listOf(suelo.x, quieto.x, suelo.hasta)) { val luz = E.luzDelGato(m, Offset(x, suelo.y)); val c = luz.tono(androidx.compose.ui.graphics.Color(0xFF808080))
                assertTrue(c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f); assertTrue(luz.noche in 0f..1f); assertTrue(luz.rayo.fuerza in 0f..1f) }
        }
    }
}
