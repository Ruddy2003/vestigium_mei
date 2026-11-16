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
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.CamRefugio
import io.github.ruddy2003.vestigium.ui.escenas.ColorRefugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Compuerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Estado
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Obra
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.TramoCultivo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRefugio.TramoReserva
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosRefugio
import io.github.ruddy2003.vestigium.ui.escenas.PasilloRefugio
import io.github.ruddy2003.vestigium.ui.escenas.SalaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.VistaRefugio
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Refugio (tanda S17: la sala de bombas de la residente, con el pasillo grande y la Puerta al fondo) cuenta
 * lo mismo que el cuento: el estado del refugio, la cisterna y los tanques, dónde cuelga la lámpara de trabajo a cada
 * hora, qué está encendido, la compuerta, la Puerta y lo de fuera, y los días del cuento salen de la ficha del día, y
 * aquí se comparan con su calendario (`AnioG7.kt`, `RESIDENTE`) todos los días de 2026 a 2032.
 */
class EscenaRefugioPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "residente")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }
    private fun calido(c: Color) = c.red > c.blue + 0.15f
    private fun frio(c: Color) = c.blue > c.red + 0.15f

    @Test fun laEscenaEsLaDeLaResidente() {
        assertEquals(listOf("residente"), EscenaRefugio.vidas)
        assertEquals("residente", EscenaRefugio.vida("2027-05-05"))
        assertTrue(EscenaRefugio.tallaDelGato in 0.8f..1.8f)
        assertEquals("refugio", OtrasVidas.porId("residente")!!.tema)
    }

    // ---------------------------------------------------------------- Los estados

    /** Los cuatro estados, en las fechas del calendario: el racionamiento de 2028, la cuarentena de 2029 y el «abierto» desde 2031. */
    @Test fun losCuatroEstadosEnSusFechas() {
        val dias = mutableMapOf<Pair<Estado, Int>, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "racionamiento" -> Estado.RACIONAMIENTO; "cuarentena" -> Estado.CUARENTENA; "abierto" -> Estado.ABIERTO; null, "" -> Estado.CERRADO; else -> null }
            assertEquals(d.toString(), esperado, EscenaRefugio.estado(m))
            val e = EscenaRefugio.estado(m)
            dias[e to d.year] = (dias[e to d.year] ?: 0) + 1
            // Lo que deja cada estado, solo en él.
            assertEquals(d.toString(), e == Estado.RACIONAMIENTO, EscenaRefugio.botellasEnFila(m))
            assertEquals(d.toString(), e == Estado.CUARENTENA, EscenaRefugio.mascarillas(m))
            assertEquals(d.toString(), e == Estado.CUARENTENA, EscenaRefugio.compuerta(m) == Compuerta.BAJADA && !EscenaRefugio.simulacro(m))
            assertEquals(d.toString(), e == Estado.ABIERTO, EscenaRefugio.puerta(m) == Puerta.ABIERTA)
        }
        assertEquals(37, dias[Estado.RACIONAMIENTO to 2028]); assertEquals(25, dias[Estado.CUARENTENA to 2029])
        assertEquals(103, dias[Estado.ABIERTO to 2031]); assertEquals(103, dias[Estado.ABIERTO to 2032])
        assertEquals(365, dias[Estado.CERRADO to 2027])
        // Los bordes.
        assertEquals(Estado.CERRADO, EscenaRefugio.estado(momento("2028-03-14"))); assertEquals(Estado.RACIONAMIENTO, EscenaRefugio.estado(momento("2028-03-15")))
        assertEquals(Estado.RACIONAMIENTO, EscenaRefugio.estado(momento("2028-04-20"))); assertEquals(Estado.CERRADO, EscenaRefugio.estado(momento("2028-04-21")))
        assertEquals(Estado.CUARENTENA, EscenaRefugio.estado(momento("2029-02-01"))); assertEquals(Estado.CERRADO, EscenaRefugio.estado(momento("2029-02-26")))
        assertEquals(Estado.CERRADO, EscenaRefugio.estado(momento("2031-09-19"))); assertEquals(Estado.ABIERTO, EscenaRefugio.estado(momento("2031-09-20")))
        assertEquals(Estado.ABIERTO, EscenaRefugio.estado(momento("2031-12-31")))
        // Con la Puerta cerrada no se ve fuera; abierta, sí.
        assertNull(VistaRefugio(momento("2027-05-05")).fuera); assertNotNull(VistaRefugio(momento("2031-10-01")).fuera)
    }

    // ---------------------------------------------------------------- Los arcos

    /** La columna de nivel lee la reserva de la cisterna: baja de golpe el día de la grieta y tarda en volver. */
    @Test fun laCisternaYSusTramos() {
        cadaDia { d, m ->
            val r = m.arco("reserva")
            assertEquals(d.toString(), r, EscenaRefugio.reserva(m), 1e-6f)
            val esperado = when { r < EscenaRefugio.MARCA_ROJA -> TramoReserva.BAJA; r < 0.6f -> TramoReserva.MEDIA; else -> TramoReserva.LLENA }
            assertEquals(d.toString(), esperado, EscenaRefugio.tramoReserva(m))
            if (EscenaRefugio.racionamiento(m)) assertTrue(d.toString(), EscenaRefugio.tramoReserva(m) != TramoReserva.LLENA)
        }
        assertEquals(TramoReserva.LLENA, EscenaRefugio.tramoReserva(momento("2028-03-13")))
        assertEquals(TramoReserva.BAJA, EscenaRefugio.tramoReserva(momento("2028-03-14")))
        assertEquals(TramoReserva.BAJA, EscenaRefugio.tramoReserva(momento("2028-04-01")))
        assertEquals(TramoReserva.MEDIA, EscenaRefugio.tramoReserva(momento("2028-05-01")))
        assertEquals(TramoReserva.LLENA, EscenaRefugio.tramoReserva(momento("2028-12-31")))
        assertEquals(TramoReserva.LLENA, EscenaRefugio.tramoReserva(momento("2032-06-01")))
    }

    /** Los tanques leen el cultivo: llenos antes de la cosecha, recién sembrados después, verdes el resto; cada año los tres. */
    @Test fun losTanquesYSusTramos() {
        val tramosDelAnio = mutableMapOf<Int, MutableSet<TramoCultivo>>()
        cadaDia { d, m ->
            val c = m.arco("cultivo")
            val esperado = when { c < 0.35f -> TramoCultivo.SEMBRADO; c < 0.75f -> TramoCultivo.VERDE; else -> TramoCultivo.LLENO }
            assertEquals(d.toString(), esperado, EscenaRefugio.tramoCultivo(m))
            tramosDelAnio.getOrPut(d.year) { mutableSetOf() } += EscenaRefugio.tramoCultivo(m)
            if (hoyEs(m, "cosecha")) assertEquals(d.toString(), TramoCultivo.SEMBRADO, EscenaRefugio.tramoCultivo(m))
        }
        for (anio in 2026..2032) assertEquals("$anio", TramoCultivo.entries.toSet(), tramosDelAnio[anio])
        assertEquals(TramoCultivo.VERDE, EscenaRefugio.tramoCultivo(momento("2027-01-20")))
        assertEquals(TramoCultivo.LLENO, EscenaRefugio.tramoCultivo(momento("2027-05-19")))
        assertEquals(TramoCultivo.SEMBRADO, EscenaRefugio.tramoCultivo(momento("2027-05-20")))
        assertEquals(TramoCultivo.VERDE, EscenaRefugio.tramoCultivo(momento("2027-11-01")))
    }

    // ---------------------------------------------------------------- La luz

    /** La lámpara de trabajo camina con la hora y con lo que ella hace: los manómetros, la bomba, la bancada, el filtro; fuera de su turno, apagada. */
    @Test fun laLamparaCaminaConLaHora() {
        val dia = "2027-05-05"
        assertEquals(Obra.MANOMETROS, EscenaRefugio.lampara(momento(dia, 7.0)))
        assertEquals(Obra.BOMBA, EscenaRefugio.lampara(momento(dia, 10.0)))
        assertEquals(Obra.BANCADA, EscenaRefugio.lampara(momento(dia, 12.34)))
        assertEquals(Obra.FILTRO, EscenaRefugio.lampara(momento(dia, 13.0)))
        for (h in listOf(3.0, 6.5, 17.0, 19.0, 21.0, 23.0)) assertNull("$h", EscenaRefugio.lampara(momento(dia, h)))
        // Las cuatro obras cuelgan en sitios distintos del cuadro, y la de las 7:00 y la de las 13:00 lejos una de otra.
        val sitios = Obra.entries.map { CamRefugio.p(SalaRefugio.bombilla(it)) }
        for (i in sitios.indices) for (j in i + 1 until sitios.size) assertTrue("$i $j", (sitios[i] - sitios[j]).getDistance() > 25f)
        assertTrue(abs(CamRefugio.p(SalaRefugio.bombilla(Obra.MANOMETROS)).x - CamRefugio.p(SalaRefugio.bombilla(Obra.FILTRO)).x) > 40f)
        // Cada lámpara está dentro del cuadro y bajo el techo.
        for (o in Obra.entries) { val p = CamRefugio.p(SalaRefugio.bombilla(o)); assertTrue("$o", p.x in 0f..ANCHO && p.y in 0f..ALTO); assertTrue(SalaRefugio.bombilla(o).y < SalaRefugio.TECHO) }
        // Con el cuaderno: abierto mientras trabaja, cerrado cuando se va; el mono cuelga cuando no lo lleva.
        assertTrue(EscenaRefugio.cuadernoAbierto(momento(dia, 10.0))); assertFalse(EscenaRefugio.cuadernoAbierto(momento(dia, 21.0)))
        assertFalse(EscenaRefugio.monoColgado(momento(dia, 10.0))); assertTrue(EscenaRefugio.monoColgado(momento(dia, 21.0)))
        assertTrue(EscenaRefugio.fiambreraAbierta(momento(dia, 13.0))); assertFalse(EscenaRefugio.fiambreraAbierta(momento(dia, 7.0)))
        // Los días de obra y los estados la mueven: la bomba tres, los filtros, el turno de más y las dos lámparas de la cuarentena.
        assertEquals(Obra.BOMBA, EscenaRefugio.lampara(momento("2027-02-12", 15.0)))
        assertEquals(Obra.FILTRO, EscenaRefugio.lampara(momento("2027-04-11", 8.0)))
        assertEquals(Obra.FILTRO, EscenaRefugio.lampara(momento("2027-04-11", 20.0)))
        assertEquals(Obra.BANCADA, EscenaRefugio.lampara(momento("2028-03-25", 21.0)))
        assertEquals(2, EscenaRefugio.lamparas(momento("2029-02-10", 10.0)).toSet().size)
        assertNull(EscenaRefugio.lampara(momento("2029-02-10", 23.0)))
    }

    /** Tres horas, tres cuadros por la luz: a las 7:00 manda la lámpara de los manómetros; a las 13:00, la del filtro; a las 21:00, el azul de la noche. */
    @Test fun tresHorasSonTresLuces() {
        val a = VistaRefugio(momento("2027-05-05", 7.0)); val b = VistaRefugio(momento("2027-05-05", 13.0)); val c = VistaRefugio(momento("2027-05-05", 21.0))
        assertEquals(listOf(Obra.MANOMETROS), a.obras); assertEquals(listOf(Obra.FILTRO), b.obras); assertTrue(c.obras.isEmpty())
        assertTrue(a.dia && b.dia && !c.dia)
        // La pared de manómetros, encendida a las 7:00 y no a las 13:00; la bancada, al revés.
        val tablero = io.github.ruddy2003.vestigium.ui.escenas.V3((SalaRefugio.TABLERO_X0 + SalaRefugio.TABLERO_X1) / 2f, 1.9f, SalaRefugio.FONDO - 0.1f)
        val bancada = io.github.ruddy2003.vestigium.ui.escenas.V3(-1.2f, SalaRefugio.BANCADA_Y, 3.0f)
        assertTrue(a.deLampara(tablero) > b.deLampara(tablero) + 0.1f)
        assertTrue(b.deLampara(bancada) > a.deLampara(bancada) + 0.1f)
        // De noche, la penumbra es azul, no gris.
        assertTrue(frio(c.ambienteSala) && frio(c.ambientePasillo))
    }

    /** De noche hay dos luces de color opuesto: el azul del pasillo y el ámbar de la guardia (o la lámpara del turno de más, o el fuego de fuera). */
    @Test fun deNocheHayDosLuces() {
        for (dia in listOf("2027-05-05", "2028-03-25", "2029-02-10", "2031-10-01", "2032-12-24")) {
            val v = VistaRefugio(momento(dia, 21.0))
            assertTrue("$dia: una luz fría", v.focos.any { frio(it.color) && it.fuerza > 0.25f })
            assertTrue("$dia: una luz cálida", v.focos.any { calido(it.color) && it.fuerza > 0.5f })
            // Y las dos tumban sombras o alumbran la sala.
            assertTrue("$dia", v.tumban.isNotEmpty())
        }
        // En el racionamiento la lámpara sigue encendida de noche; con la Puerta abierta, arde el fuego de fuera.
        assertTrue(VistaRefugio(momento("2028-03-25", 21.0)).focos.any { it.color == ColorRefugio.LAMPARA })
        assertTrue(EscenaRefugio.fuego(momento("2031-10-01", 21.0)) > 0.5f)
        assertEquals(0f, EscenaRefugio.fuego(momento("2031-10-01", 13.0)), 0f)
        assertEquals(0f, EscenaRefugio.fuego(momento("2027-05-05", 21.0)), 0f)
        // Las luces del refugio suben a las 5:00 y bajan a las 21:00, como la ficha.
        assertTrue(EscenaRefugio.deDia(momento("2027-05-05", 5.0))); assertFalse(EscenaRefugio.deDia(momento("2027-05-05", 21.0)))
        assertTrue(EscenaRefugio.tanques(momento("2027-05-05", 21.0)) > EscenaRefugio.tanques(momento("2027-05-05", 13.0)))
    }

    /** Con la Puerta abierta, el cielo de fuera cambia con la hora y el sol de la tarde camina por el pasillo. */
    @Test fun enAbiertoElCieloCambiaConLaHora() {
        var conSol = 0
        var d = LocalDate.of(2031, 9, 20)
        while (d <= LocalDate.of(2031, 12, 31)) {
            val dia = d.toString()
            val manana = EscenaRefugio.cieloDeFuera(momento(dia, 7.0)); val tarde = EscenaRefugio.cieloDeFuera(momento(dia, 13.0)); val noche = EscenaRefugio.cieloDeFuera(momento(dia, 21.0))
            assertNotEquals(dia, manana.medio, tarde.medio); assertNotEquals(dia, tarde.medio, noche.medio)
            assertTrue(dia, noche.noche > tarde.noche + 0.5f)
            // Por la mañana el sol está detrás del monte; desde mediodía entra, y cuanto más tarde, más lejos llega.
            assertNull(dia, EscenaRefugio.solPorLaPuerta(momento(dia, 9.0)))
            assertNull(dia, EscenaRefugio.solPorLaPuerta(momento(dia, 22.0)))
            val a = EscenaRefugio.alcanceDelSol(momento(dia, 16.0)); val b = EscenaRefugio.alcanceDelSol(momento(dia, 19.67))
            if (a > 0f && b > 0f) { assertTrue(dia, b > a + 3f); conSol++ }
            d = d.plusDays(1)
        }
        assertTrue("casi todos los días entra el sol de la tarde", conSol > 80)
        // A la caída del sol llega hasta el umbral de la sala.
        assertTrue((20..31).any { EscenaRefugio.alcanceDelSol(momento("2031-10-${"%02d".format(it)}", 20.0)) > PasilloRefugio.LARGO * 0.8f })
        // El día que llueve, llueve fuera.
        assertEquals(Clima.LLUVIA, EscenaRefugio.climaDeFuera(momento("2031-10-15")))
        assertNotEquals(Clima.LLUVIA, EscenaRefugio.climaDeFuera(momento("2031-10-14")))
        // Con la Puerta cerrada no entra nada.
        assertNull(EscenaRefugio.solPorLaPuerta(momento("2027-05-05", 19.0)))
    }

    // ---------------------------------------------------------------- Los días del cuento

    /** Lo que se ve de un momento: si dos días dan la misma huella a las 10, 13 y 21, el cuadro no cambia entre ellos. */
    private fun huella(dia: String): List<Any?> = listOf(10.0, 13.0, 21.0).flatMap { h ->
        val m = momento(dia, h); val R = EscenaRefugio
        listOf(R.estado(m), R.lamparas(m), R.compuerta(m), R.puerta(m), R.alarma(m), R.solFalso(m), R.guirnalda(m), R.luto(m), R.sellado(m), R.cosecha(m),
            R.filtros(m), R.bombaTres(m), R.senal(m), R.tocadiscosRoto(m), R.arbolDeCable(m), R.lucesDelArbol(m), R.barroDeBotas(m), R.florDeVerdad(m),
            R.gafasDeCarton(m), R.tramoReserva(m), R.tramoCultivo(m), if (R.abierto(m)) R.climaDeFuera(m) else null)
    }

    /** Cada día del cuento que tiene algo que verse cambia el cuadro respecto del último día corriente antes de él. */
    @Test fun cadaDiaDelCuentoCambiaAlgo() {
        val conAlgo = setOf("sellado", "filtros", "nochebuena", "sol_falso", "salen", "vuelven", "senal", "tocadiscos", "grieta", "abre", "llueve", "fin_anio", "dorotea", "fundadores", "anio_nuevo", "hijo_lina", "simulacro", "bomba_tres", "cosecha", "fiebre", "racion", "fin_racion", "abre_sector")
        val vistos = mutableSetOf<String>()
        cadaDia { d, m ->
            val ids = m.ficha.especiales.map { it.dia.id }.filter { it in conAlgo }
            if (ids.isEmpty()) return@cadaDia
            var antes = d.minusDays(1)
            while (momento(antes.toString()).ficha.especiales.isNotEmpty()) antes = antes.minusDays(1)
            assertNotEquals("$d ${ids.joinToString()}: el cuadro no cambia respecto de $antes", huella(antes.toString()), huella(d.toString()))
            vistos += ids
        }
        assertEquals(conAlgo, vistos)
        // Lo de cada uno, a su hora.
        assertEquals(Compuerta.BAJADA, EscenaRefugio.compuerta(momento("2027-11-11", 10.0))); assertTrue(EscenaRefugio.alarma(momento("2027-11-11", 10.0)))
        assertFalse(EscenaRefugio.alarma(momento("2027-11-11", 21.0)))
        assertTrue(EscenaRefugio.alarma(momento("2027-02-12", 13.0)) && EscenaRefugio.bombaTres(momento("2027-02-12")))
        assertEquals(Puerta.ENTORNADA, EscenaRefugio.puerta(momento("2030-05-10"))); assertEquals(Puerta.CERRADA, EscenaRefugio.puerta(momento("2030-05-11")))
        assertTrue(EscenaRefugio.lucesDelArbol(momento("2027-12-24", 21.0))); assertFalse(EscenaRefugio.lucesDelArbol(momento("2027-12-24", 13.0)))
        assertTrue(EscenaRefugio.solFalso(momento("2027-07-20", 13.0))); assertFalse(EscenaRefugio.solFalso(momento("2027-07-20", 22.0)))
        assertFalse(EscenaRefugio.florDeVerdad(momento("2030-07-27"))); assertTrue(EscenaRefugio.florDeVerdad(momento("2030-07-28"))); assertTrue(EscenaRefugio.florSeca(momento("2031-01-10")))
        assertTrue(EscenaRefugio.tramoReserva(momento("2028-03-14")) == TramoReserva.BAJA && EscenaRefugio.alarma(momento("2028-03-14")) && !EscenaRefugio.botellasEnFila(momento("2028-03-14")))
    }

    // ---------------------------------------------------------------- El gato

    /** El gato tiene su suelo con un tramo libre de al menos 110, sus sitios dentro del cuadro y una luz válida. */
    @Test fun elGatoTieneSuTramoLibre() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaRefugio.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 17.5, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaRefugio.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                val plantado = EscenaRefugio.estorbos(m)
                plantado.forEach { x -> assertTrue("$d $h: lo plantado está fuera del suelo del gato", x in suelo.x..suelo.hasta) }
                // El tramo libre más largo, entre lo plantado y los extremos.
                val cortes = (listOf(suelo.x) + plantado.sorted() + listOf(suelo.hasta))
                assertTrue("$d $h: no hay un tramo libre de 110", cortes.zipWithNext { a, b -> b - a }.max() >= 110f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO)
                assertTrue(anclas.single { it.tipo == TipoAncla.MIRADOR }.y < suelo.y - 60f)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = EscenaRefugio.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f)
                }
            }
            d = d.plusDays(9)
        }
    }

    // ---------------------------------------------------------------- Los objetos

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosRefugio.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosRefugio.TODOS.containsAll(EscenaRefugio.DE_LA_SALA))
        assertTrue(ObjetosRefugio.TODOS.containsAll(ObjetosRefugio.GRANDES))
        // Mientras el contrato esté vacío, la sala pinta sus piezas (son el objeto del día de esas fechas).
        cadaDia { d, m -> for (p in EscenaRefugio.DE_LA_SALA) assertTrue("$d $p", EscenaRefugio.laSalaPinta(m, p) || p in ObjetosRefugio.DIBUJADOS) }
        // El soporte del objeto chico está sobre la bancada, a la vista, y no lo tapa el gato quieto.
        val s = CamRefugio.p(SalaRefugio.SOPORTE); assertTrue(s.x in 60f..ANCHO - 40f && s.y in 120f..ALTO - 40f)
    }

    // ---------------------------------------------------------------- El dibujo

    /** El cuadro se dibuja sin fallas en sus estados y días, a las tres horas, con la parte quieta y la viva. */
    @Test fun seDibujaSinFallas() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", runCatching { ImageBitmap(1, 1) }.isSuccess)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt(); val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        fun dibuja(m: Momento) = CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
            lienzo { with(EscenaRefugio) { quieta(m) } }
            lienzo { with(EscenaRefugio) { viva(m, 2.5f) } }
        }
        val dias = listOf("2026-11-16", "2027-02-12", "2027-04-11", "2027-05-20", "2027-07-20", "2027-11-11", "2027-12-24", "2028-03-14", "2028-03-25", "2029-02-10", "2030-05-10", "2031-09-20", "2031-10-15", "2032-03-01")
        for (dia in dias) for (h in listOf(7.0, 13.0, 21.0)) dibuja(momento(dia, h))
    }
}
