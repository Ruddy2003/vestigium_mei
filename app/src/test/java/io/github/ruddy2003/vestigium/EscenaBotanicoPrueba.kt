package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Carga
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Costa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Mesa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Red
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Velas
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosBotanico
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * Los dos cuadros de Botánico (tanda S13) cuentan lo mismo que el cuento del naturalista: dónde está (el bergantín, la
 * casa de Botafogo o un lugar en tierra que todavía no tiene cuadro), qué costa se ve y si navega o está fondeado, las
 * velas, la tormenta, el frío, los días señalados, los cajones y lo que hay encendido o en uso a cada hora salen de la
 * ficha del día, y aquí se comparan con su calendario (`AnioG5.kt`, `NATURALISTA`) todos los días de 2026 a 2032.
 */
class EscenaBotanicoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "naturalista")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    /** El primer día de [anio] en que cae el día especial [id] (los «suceso» se mueven desde 2032), o null. */
    private fun diaDe(anio: Int, id: String): Momento? { var d = LocalDate.of(anio, 1, 1); while (d.year == anio) { val m = momento(d.toString()); if (hoyEs(m, id)) return m; d = d.plusDays(1) }; return null }

    @Test fun laEscenaEsLaDelNaturalista() {
        assertEquals(listOf("naturalista"), EscenaBotanico.vidas)
        assertEquals("naturalista", EscenaBotanico.vida("2027-05-10"))
        assertTrue(EscenaBotanico.tallaDelGato in 0.8f..1.8f)
        assertEquals("botanico", OtrasVidas.porId("naturalista")!!.tema)
    }

    @Test fun estaDondeDiceElCalendario() {
        val selva = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val t = m.ficha.temporada
            val esperado = when (m.ficha.estado) {
                "tierra" -> if (t == 1) Lugar.SELVA else if (t == 2) Lugar.PAMPA else Lugar.MONTANA
                "enfermo" -> Lugar.CUARTO
                "islas" -> Lugar.ISLAS
                "casa" -> Lugar.CASA
                else -> Lugar.BERGANTIN
            }
            assertEquals(d.toString(), esperado, EscenaBotanico.lugar(m))
            // Solo la selva tiene cuadro propio en tierra; los demás sitios se pintan con el bergantín fondeado.
            assertEquals(d.toString(), if (esperado == Lugar.SELVA) Cuadro.SELVA else Cuadro.BARCO, EscenaBotanico.cuadro(m))
            assertEquals(d.toString(), esperado != Lugar.SELVA && esperado != Lugar.BERGANTIN, EscenaBotanico.provisional(m))
            if (esperado == Lugar.SELVA) { selva[d.year] = (selva[d.year] ?: 0) + 1; assertTrue(d.toString(), md(d) >= "04-25" && md(d) <= "07-04") }
        }
        // La selva: 71 días, del 25 de abril al 4 de julio, en 2026 (el avance) y en 2027; nunca más.
        assertEquals(mapOf(2026 to 71, 2027 to 71), selva)
        assertEquals(Lugar.BERGANTIN, EscenaBotanico.lugar(momento("2027-04-24"))); assertEquals(Lugar.SELVA, EscenaBotanico.lugar(momento("2027-04-25")))
        assertEquals(Lugar.SELVA, EscenaBotanico.lugar(momento("2027-07-04"))); assertEquals(Lugar.BERGANTIN, EscenaBotanico.lugar(momento("2027-07-05")))
        assertEquals(Lugar.PAMPA, EscenaBotanico.lugar(momento("2028-09-01"))); assertEquals(Lugar.MONTANA, EscenaBotanico.lugar(momento("2029-08-20")))
        assertEquals(Lugar.CUARTO, EscenaBotanico.lugar(momento("2029-10-01"))); assertEquals(Lugar.MONTANA, EscenaBotanico.lugar(momento("2030-04-01")))
        assertEquals(Lugar.ISLAS, EscenaBotanico.lugar(momento("2030-10-01"))); assertEquals(Lugar.CASA, EscenaBotanico.lugar(momento("2031-11-01")))
    }

    @Test fun laCostaYElFondeoSiguenElViaje() {
        cadaDia { d, m ->
            val c = EscenaBotanico.costa(m)
            when (EscenaBotanico.lugar(m)) {
                Lugar.SELVA -> assertEquals(d.toString(), Costa.TROPICAL, c)
                Lugar.PAMPA -> assertEquals(d.toString(), Costa.PATAGONIA, c)
                Lugar.MONTANA, Lugar.CUARTO -> assertEquals(d.toString(), Costa.ANDES, c)
                Lugar.ISLAS -> assertEquals(d.toString(), Costa.VOLCANICA, c)
                Lugar.CASA -> assertEquals(d.toString(), Costa.INGLESA, c)
                Lugar.BERGANTIN -> assertEquals(d.toString(), EscenaBotanico.tramo(m).costa, c)
            }
            // En tierra el bergantín está siempre fondeado; si no hay puerto, no hay pueblo con luces.
            if (EscenaBotanico.provisional(m) || EscenaBotanico.lugar(m) == Lugar.SELVA) assertTrue(d.toString(), EscenaBotanico.fondeado(m))
            if (!EscenaBotanico.fondeado(m)) assertFalse(d.toString(), EscenaBotanico.puerto(m))
            if (c == Costa.MAR_ABIERTO) assertFalse(d.toString(), EscenaBotanico.fondeado(m))
            // Las velas: aferradas fondeado o con tormenta; caídas con la calma; largas si navega.
            val v = EscenaBotanico.velas(m)
            assertEquals(d.toString(), when { EscenaBotanico.fondeado(m) || EscenaBotanico.tormenta(m) -> Velas.AFERRADAS; EscenaBotanico.calma(m) -> Velas.CAIDAS; else -> Velas.LARGAS }, v)
            assertTrue(d.toString(), EscenaBotanico.marejada(m) in 0f..1f)
        }
        // Lo que dicen el calendario y el cuento de cada día especial: dónde está y si fondea o navega.
        for (a in listOf(2026, 2027)) {
            for ((id, costa) in listOf("praia" to Costa.VOLCANICA, "bahia" to Costa.TROPICAL, "rio" to Costa.TROPICAL, "envio" to Costa.PATAGONIA, "punta_alta" to Costa.PATAGONIA, "carta" to Costa.PATAGONIA, "fuego" to Costa.FUEGO)) {
                val m = diaDe(a, id) ?: continue
                assertEquals("$a $id", costa, EscenaBotanico.costa(m)); assertTrue("$a $id: fondeado", EscenaBotanico.fondeado(m))
            }
            for (id in listOf("vuelve_bordo", "cabo_hornos")) { val m = diaDe(a, id)!!; assertFalse("$a $id: navega", EscenaBotanico.fondeado(m)) }
            val vuelta = diaDe(a, "vuelve_bordo")!!; assertEquals(Costa.TROPICAL, EscenaBotanico.costa(vuelta)); assertEquals(Lugar.BERGANTIN, EscenaBotanico.lugar(vuelta))
        }
        assertEquals(Costa.ANDES, EscenaBotanico.costa(momento("2029-07-23"))); assertTrue(EscenaBotanico.fondeado(momento("2029-07-23")))
        assertEquals(Costa.FUEGO, EscenaBotanico.costa(momento("2029-05-25"))); assertFalse(EscenaBotanico.fondeado(momento("2029-05-25")))
        assertEquals(Costa.ANDES, EscenaBotanico.costa(momento("2029-12-01")))
        assertEquals(Costa.ANDES, EscenaBotanico.costa(momento("2030-02-20"))); assertEquals(Costa.ANDES, EscenaBotanico.costa(momento("2030-03-04")))
        assertEquals(Costa.VOLCANICA, EscenaBotanico.costa(momento("2030-09-15"))); assertEquals(Costa.MAR_ABIERTO, EscenaBotanico.costa(momento("2030-11-01")))
        assertEquals(Costa.VOLCANICA, EscenaBotanico.costa(momento("2030-11-15"))); assertEquals(Costa.COLINAS, EscenaBotanico.costa(momento("2030-12-19")))
        assertEquals(Costa.COLINAS, EscenaBotanico.costa(momento("2031-01-12"))); assertEquals(Costa.ATOLON, EscenaBotanico.costa(momento("2031-04-01")))
        assertEquals(Costa.COLINAS, EscenaBotanico.costa(momento("2031-05-31"))); assertEquals(Costa.VOLCANICA, EscenaBotanico.costa(momento("2031-07-08")))
        assertEquals(Costa.INGLESA, EscenaBotanico.costa(momento("2031-10-02"))); assertTrue(EscenaBotanico.fondeado(momento("2031-10-02")))
        assertEquals(Velas.CAIDAS, EscenaBotanico.velas(momento("2031-03-10"))); assertEquals(0f, EscenaBotanico.marejada(momento("2031-03-10")), 0f)
        // El itinerario está en orden y cada temporada empieza el 1 de enero.
        for (t in 1..5) { val tr = EscenaBotanico.ITINERARIO.filter { it.temporada == t }; assertEquals("01-01", tr.first().desde); assertEquals(tr.map { it.desde }.sorted(), tr.map { it.desde }) }
    }

    @Test fun losCajonesSiguenSuArco() {
        var antes: Momento? = null
        cadaDia { d, m ->
            val a = m.ficha.arcos["cajones"]!!.toFloat()
            assertEquals(d.toString(), a.coerceIn(0f, 1f), EscenaBotanico.cajones(m), 0.0001f)
            assertEquals(d.toString(), if (a <= 0.3f) Carga.POCOS else if (a <= 0.7f) Carga.LLENANDO else Carga.LLENOS, EscenaBotanico.carga(m))
            assertTrue(d.toString(), EscenaBotanico.cuantosCajones(m) in 1..9)
            assertTrue(d.toString(), BarcoBotanico.cajonesEnCubierta(m) in 1..9 && SelvaBotanico.cajonesEnLaGaleria(m) in 1..5)
            // Los días del envío se vacían de golpe.
            val b = antes
            if (EscenaBotanico.diaDeEnvio(m) && b != null) assertTrue(d.toString(), EscenaBotanico.cuantosCajones(m) < EscenaBotanico.cuantosCajones(b))
            antes = m
        }
        assertTrue(EscenaBotanico.diaDeEnvio(momento("2027-08-19"))); assertTrue(EscenaBotanico.diaDeEnvio(momento("2028-11-14")))
        assertTrue(EscenaBotanico.cuantosCajones(momento("2027-08-18")) - EscenaBotanico.cuantosCajones(momento("2027-08-19")) >= 2)
        assertEquals(Carga.LLENOS, EscenaBotanico.carga(momento("2031-09-10"))); assertEquals(9, EscenaBotanico.cuantosCajones(momento("2031-09-10")))
        // En la selva, pocos: el arco va de 0.25 a 0.40.
        assertTrue(SelvaBotanico.cajonesEnLaGaleria(momento("2027-04-25")) in 2..4); assertTrue(SelvaBotanico.cajonesEnLaGaleria(momento("2027-07-04")) in 3..5)
    }

    @Test fun laTormentaYElFrio() {
        cadaDia { d, m ->
            val g = EscenaBotanico.inclinacion(m)
            if (EscenaBotanico.tormenta(m)) { assertTrue(d.toString(), abs(g) in 4f..7f); assertEquals(d.toString(), 1f, EscenaBotanico.marejada(m), 0f) } else assertEquals(d.toString(), 0f, g, 0f)
            assertEquals(d.toString(), m.con("tormenta") || m.ficha.clima == Clima.TORMENTA, EscenaBotanico.tormenta(m))
            val n = EscenaBotanico.nieve(m)
            assertTrue(d.toString(), n in 0f..1f)
            // La ficha manda: si nieva, nieva (también en el trópico).
            if (m.ficha.clima == Clima.NIEVE) assertEquals(d.toString(), 1f, n, 0f)
            if (n > 0f && EscenaBotanico.cuadro(m) == Cuadro.BARCO) assertTrue(d.toString(), EscenaBotanico.escarcha(m))
            if (EscenaBotanico.cuadro(m) == Cuadro.SELVA) assertFalse(d.toString(), EscenaBotanico.escarcha(m))
        }
        for (dia in listOf("2027-12-28", "2029-05-25")) { val m = momento(dia); assertTrue(dia, EscenaBotanico.tormenta(m)); assertEquals(Velas.AFERRADAS, EscenaBotanico.velas(m)); assertEquals(Mesa.RECOGIDA, EscenaBotanico.mesaDeCubierta(m)) }
        assertTrue(EscenaBotanico.tormenta(momento("2027-12-25")))
        // Tierra del Fuego el día que llegan: escarcha todo el día.
        assertTrue(EscenaBotanico.escarcha(momento("2027-12-18", 15.0)))
    }

    @Test fun losDiasSenaladosSeVenSuDiaYNoLaVispera() {
        for (a in 2026..2032) {
            assertTrue("$a", EscenaBotanico.fiesta(momento("$a-01-01"))); assertTrue("$a", EscenaBotanico.fiesta(momento("$a-12-25")))
            assertFalse("$a", EscenaBotanico.fiesta(momento("$a-12-24")))
        }
        assertTrue(EscenaBotanico.terremoto(momento("2030-02-20"))); assertTrue(EscenaBotanico.terremoto(momento("2030-03-04"))); assertFalse(EscenaBotanico.terremoto(momento("2030-02-19")))
        assertTrue(EscenaBotanico.ruinas(momento("2030-03-04"))); assertFalse(EscenaBotanico.ruinas(momento("2030-02-20")))
        // El volcán: la costa de los Andes lo tiene; echa fuego solo el 1 de diciembre de 1834, y de noche más.
        assertTrue(EscenaBotanico.volcan(momento("2029-12-01")))
        assertEquals(1f, EscenaBotanico.erupcion(momento("2029-12-01", 22.0)), 0f); assertEquals(0.4f, EscenaBotanico.erupcion(momento("2029-12-01", 12.0)), 0f)
        assertEquals(0f, EscenaBotanico.erupcion(momento("2029-11-30", 22.0)), 0f)
        cadaDia { d, m -> if (EscenaBotanico.erupcion(m) > 0f) assertEquals(d.toString(), LocalDate.of(2029, 12, 1), d); if (EscenaBotanico.volcan(m)) assertEquals(d.toString(), Costa.ANDES, EscenaBotanico.costa(m)) }
        // La selva: la mudanza con los libros al sol, los escarabajos y el bergantín en la bahía «cuando está».
        val mudanza = momento("2027-04-25", 15.0)
        assertTrue(EscenaBotanico.botafogo(mudanza) && EscenaBotanico.librosAlSol(mudanza)); assertFalse(EscenaBotanico.librosAlSol(momento("2027-04-25", 8.0)))
        assertFalse(EscenaBotanico.botafogo(momento("2027-04-26", 15.0)))
        assertTrue(EscenaBotanico.escarabajos(momento("2027-05-20"))); assertFalse(EscenaBotanico.escarabajos(momento("2027-05-19")))
        for ((dia, esta) in listOf("2027-04-25" to true, "2027-04-26" to false, "2027-05-20" to false, "2027-06-02" to false, "2027-06-03" to true, "2027-07-04" to true, "2027-07-05" to false))
            assertEquals(dia, esta, EscenaBotanico.bergantinEnLaBahia(momento(dia)))
        cadaDia { d, m -> if (EscenaBotanico.bergantinEnLaBahia(m) || EscenaBotanico.botafogo(m) || EscenaBotanico.escarabajos(m)) assertEquals(d.toString(), Lugar.SELVA, EscenaBotanico.lugar(m)) }
    }

    @Test fun loEncendidoYEnUsoSigueElHorario() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 6.0, 7.0, 13.0, 15.0, 18.5, 20.0, 21.0, 23.0)) {
                val m = momento(d.toString(), h); val barco = EscenaBotanico.cuadro(m) == Cuadro.BARCO
                assertTrue("$d $h", EscenaBotanico.luzDeLaCabina(m) in 0f..1f && EscenaBotanico.lamparaDeLaGaleria(m) in 0f..1f)
                // La mesa de cubierta solo en el bergantín (en los lugares sin cuadro va sin mesa); en uso a las horas de trabajo.
                val mesa = EscenaBotanico.mesaDeCubierta(m)
                if (EscenaBotanico.lugar(m) != Lugar.BERGANTIN) assertNull("$d $h", mesa) else {
                    assertNotNull("$d $h", mesa)
                    val seco = m.ficha.clima != Clima.LLUVIA && m.ficha.clima != Clima.NIEVE && !EscenaBotanico.tormenta(m)
                    if (seco && m.hacer == Hacer.TRABAJO) assertEquals("$d $h", Mesa.TRABAJO, mesa)
                    if (seco && m.hacer != Hacer.TRABAJO) assertEquals("$d $h", Mesa.QUIETA, mesa)
                }
                // El diario de 19 a 22, a la luz de la vela (en la cabina) o de la lámpara (en la galería).
                if (h == 20.0 || h == 21.0) { assertEquals("$d $h", 1f, EscenaBotanico.luzDeLaCabina(m), 0f); assertEquals("$d $h", 1f, EscenaBotanico.lamparaDeLaGaleria(m), 0f) }
                if (h == 3.0) { assertEquals("$d $h", 0f, EscenaBotanico.luzDeLaCabina(m), 0f); assertEquals("$d $h", 0f, EscenaBotanico.lamparaDeLaGaleria(m), 0f) }
                // El farol de popa y la lámpara de la bitácora, de noche y solo a bordo.
                if (barco && (h == 23.0 || h == 3.0)) assertTrue("$d $h", EscenaBotanico.farolDePopa(m) && EscenaBotanico.lamparaDeLaBitacora(m))
                if (h == 13.0 || !barco) assertFalse("$d $h", EscenaBotanico.farolDePopa(m))
                // La red: arrastrando solo si navega, a las horas de trabajo y con tiempo seco.
                if (EscenaBotanico.red(m) == Red.ARRASTRANDO) assertTrue("$d $h", !EscenaBotanico.fondeado(m) && m.hacer == Hacer.TRABAJO && !EscenaBotanico.tormenta(m))
                if (!barco || EscenaBotanico.provisional(m)) assertEquals("$d $h", Red.GUARDADA, EscenaBotanico.red(m))
                // En la selva: la red de mariposas y la escopeta se van con él por la mañana; luciérnagas de noche, mariposas de día.
                if (!barco) {
                    assertEquals("$d $h", !(m.hacer == Hacer.TRABAJO && h < 13.0), EscenaBotanico.redYEscopetaEnCasa(m))
                    if (h == 13.0 || h == 15.0) assertFalse("$d $h", EscenaBotanico.luciernagas(m))
                    if (h == 3.0 || h == 23.0) assertFalse("$d $h", EscenaBotanico.mariposas(m))
                    assertEquals("$d $h", if (h < 13.0 && m.hacer == Hacer.TRABAJO) Mesa.QUIETA else when (m.hacer) { Hacer.TRABAJO -> Mesa.TRABAJO; Hacer.COMER -> Mesa.COMIDA; Hacer.LIBRE -> Mesa.DIARIO; else -> Mesa.RECOGIDA }, EscenaBotanico.mesaDeLaGaleria(m))
                } else { assertFalse("$d $h", EscenaBotanico.luciernagas(m)); assertFalse("$d $h", EscenaBotanico.mariposas(m)) }
            }
        }
    }

    /** Lo mismo que vigila `GatoCuadroPrueba` en todos los cuadros: suelo a todas horas y bastante largo, sus sitios y una luz válida. */
    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosDosCuadros() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaBotanico.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Cuadro>()
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 17.0, 20.5, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaBotanico.anclas(m); vistos += EscenaBotanico.cuadro(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val plantado = EscenaBotanico.estorbos(m)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaBotanico.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.SELVA), vistos)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosBotanico.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun elMarSeMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaBotanico.seMueve(m)) }
        val a = momento("2029-12-20", 13.0); val b = momento("2029-12-20", 13.0)
        assertEquals(EscenaBotanico.costa(a), EscenaBotanico.costa(b)); assertEquals(EscenaBotanico.inclinacion(a), EscenaBotanico.inclinacion(b), 0f)
        assertEquals(EscenaBotanico.anclas(a).map { it.x to it.y }, EscenaBotanico.anclas(b).map { it.x to it.y })
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * Los dos cuadros se dibujan enteros (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, de día
     * y de noche, navegando, fondeado, con tormenta, con nieve y en los lugares sin cuadro propio, sin lanzar nada. Las
     * sombras arrojadas llevan un recorte que tiene que caber en el lienzo.
     */
    @Test fun losDosCuadrosSeDibujanConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-03-21", "2027-12-28", "2027-05-10", "2027-06-12", "2029-12-01", "2030-09-18", "2031-10-20")
        val vistos = HashSet<Cuadro>()
        for (dia in dias) for (h in listOf(7.0, 13.0, 21.0)) {
            val base = momento(dia, h); vistos += EscenaBotanico.cuadro(base)
            for (o in ObjetosBotanico.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.NIEVE)) {
                if (clima == Clima.NIEVE && o != null && o != "red") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaBotanico) { quieta(m) } }
                        lienzo { with(EscenaBotanico) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.SELVA), vistos)
    }
}
