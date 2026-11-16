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
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Camino
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Refugio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAntiguas.Veta
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.LuzAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.SitioAntiguas
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
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
 * El cuadro de Tierras Antiguas (tanda S18: la posada del Cuerno al pie del paso) cuenta lo mismo que el cuento de
 * Brenna: dónde está (la posada con el paso abierto, el paso cerrado de cada invierno, la travesía y la crianza), la nieve
 * y el camino, los tres arcos con los cortes del cuento (la veta en cero antes de abrirse, hundida y tapada), lo que queda
 * desde su día, la sombra que sube por el paso, lo encendido de noche, la compañía en el camino y las señales de cada día
 * del cuento salen de la ficha del día, y aquí se comparan con su calendario (`AnioG8.kt`) todos los días de 2026 a 2032.
 */
class EscenaAntiguasPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "guia_caminos")!!, hora)
    private fun conClima(dia: String, hora: Double, clima: Clima) = momento(dia, hora).let { Momento(it.ficha.copy(clima = clima), hora) }
    private fun cadaDia(hora: Double = 10.0, hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString(), hora)); d = d.plusDays(1) } }
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeBrenna() {
        assertEquals(listOf("guia_caminos"), EscenaAntiguas.vidas)
        assertEquals("antiguas", OtrasVidas.porId("guia_caminos")!!.tema)
        assertTrue(escenaDe(TemaId.ANTIGUAS) === EscenaAntiguas)
        assertTrue(EscenaAntiguas.tallaDelGato in 0.8f..1.8f)
    }

    /** Los estados en sus fechas, que cambian cada año: el paso se cierra y se abre en días distintos; la travesía es de 2029; la crianza, de 2031 (y se repite desde 2032). */
    @Test fun estaDondeDiceElCalendario() {
        val cerrado = mutableMapOf<Int, Int>(); val travesia = mutableMapOf<Int, Int>(); val crianza = mutableMapOf<Int, Int>()
        val cierres = HashSet<String>(); val aperturas = HashSet<String>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "paso_cerrado" -> Lugar.CERRADO; "travesia" -> Lugar.TRAVESIA; "crianza" -> Lugar.CRIANZA; else -> Lugar.POSADA }
            assertEquals(d.toString(), esperado, EscenaAntiguas.lugar(m))
            assertEquals(d.toString(), esperado == Lugar.TRAVESIA, EscenaAntiguas.provisional(m))
            when (esperado) {
                Lugar.CERRADO -> cerrado[d.year] = (cerrado[d.year] ?: 0) + 1
                Lugar.TRAVESIA -> travesia[d.year] = (travesia[d.year] ?: 0) + 1
                Lugar.CRIANZA -> crianza[d.year] = (crianza[d.year] ?: 0) + 1
                Lugar.POSADA -> Unit
            }
            if (hoyEs(m, "cierra_paso")) { cierres += d.toString().substring(5); assertEquals(d.toString(), Lugar.CERRADO, EscenaAntiguas.lugar(m)) }
            if (hoyEs(m, "abre_paso")) { aperturas += d.toString().substring(5); assertEquals(d.toString(), Lugar.POSADA, EscenaAntiguas.lugar(m)) }
        }
        assertEquals(mapOf(2026 to 106, 2027 to 106, 2028 to 108, 2029 to 107, 2030 to 106, 2031 to 107, 2032 to 108), cerrado)
        assertEquals(mapOf(2029 to 30), travesia)
        assertEquals(38, crianza[2031]); assertEquals(38, crianza[2032])
        assertTrue("el día en que se cierra cambia de un año a otro", cierres.size >= 4)
        assertTrue("el día en que se abre cambia de un año a otro", aperturas.size >= 4)
        assertEquals(Lugar.TRAVESIA, EscenaAntiguas.lugar(momento("2029-07-20"))); assertEquals(Lugar.POSADA, EscenaAntiguas.lugar(momento("2029-08-19")))
        assertEquals(Lugar.CRIANZA, EscenaAntiguas.lugar(momento("2031-04-12"))); assertEquals(Lugar.POSADA, EscenaAntiguas.lugar(momento("2031-05-20")))
    }

    /** Con el paso cerrado no hay camino, ni piedras, ni puente («solo una ladera blanca y lisa»); el día que se cierra, abajo todavía se adivina. */
    @Test fun conElPasoCerradoNoHayCaminoNiPuente() {
        cadaDia { d, m ->
            val cam = EscenaAntiguas.camino(m)
            if (EscenaAntiguas.lugar(m) == Lugar.CERRADO) {
                assertTrue(d.toString(), EscenaAntiguas.caminoBorrado(m))
                assertEquals(1f, EscenaAntiguas.nieveLadera(m), 0f); assertEquals(1f, EscenaAntiguas.nieveAbajo(m), 0f)
                if (hoyEs(m, "cierra_paso")) assertEquals(d.toString(), Camino.TAPANDOSE, cam)
                else { assertEquals(d.toString(), Camino.BORRADO, cam); assertFalse(d.toString(), EscenaAntiguas.hayPuente(m)); assertEquals(d.toString(), 0, EscenaAntiguas.piedrasALaVista(m)) }
            } else if (EscenaAntiguas.lugar(m) == Lugar.POSADA) {
                assertTrue(d.toString(), EscenaAntiguas.hayPuente(m))
                assertEquals(d.toString(), SitioAntiguas.PIEDRAS.size, EscenaAntiguas.piedrasALaVista(m))
            }
            if (hoyEs(m, "abre_paso")) assertEquals(d.toString(), Camino.ZANJA, cam)
        }
        // La crianza que desde 2033 empieza con el paso todavía cerrado: manda el estado, pero la nieve sigue a la fecha.
        val m = momento("2033-03-20")
        if (EscenaAntiguas.lugar(m) == Lugar.CRIANZA) assertTrue(EscenaAntiguas.caminoBorrado(m))
    }

    /** Los tres arcos con los cortes del cuento; la veta en cero quiere decir tres cosas según la fecha. */
    @Test fun losTresArcosYSusTramos() {
        val vistos = HashSet<Veta>(); val refugios = HashSet<Refugio>()
        cadaDia { d, m ->
            assertEquals(d.toString(), m.arco("nieve"), EscenaAntiguas.nieveDelCollado(m), 1e-6f)
            val r = m.arco("refugio"); val ref = EscenaAntiguas.refugio(m); refugios += ref
            assertEquals(d.toString(), if (r >= 0.999f) Refugio.TERMINADO else if (r >= 0.001f) Refugio.A_MEDIO else Refugio.NADA, ref)
            val v = m.arco("veta"); val veta = EscenaAntiguas.veta(m); vistos += veta
            when (veta) {
                Veta.ENTERA, Veta.HUNDIDA, Veta.TAPADA -> assertEquals(d.toString(), 0f, v, 0f)
                Veta.PEQUENA -> assertTrue(d.toString(), v > 0f && v <= 0.6f)
                Veta.MEJOR -> assertTrue(d.toString(), v > 0.6f)
            }
            if (d.year >= 2031) assertEquals(d.toString(), Veta.TAPADA, veta)
        }
        assertEquals(Veta.entries.toSet(), vistos); assertEquals(Refugio.entries.toSet(), refugios)
        // Los tres ceros de la veta.
        assertEquals(Veta.ENTERA, EscenaAntiguas.veta(momento("2027-04-07"))); assertEquals(Veta.PEQUENA, EscenaAntiguas.veta(momento("2027-04-08")))
        assertEquals(Veta.MEJOR, EscenaAntiguas.veta(momento("2027-10-09"))); assertEquals(Veta.MEJOR, EscenaAntiguas.veta(momento("2030-06-13")))
        assertEquals(Veta.HUNDIDA, EscenaAntiguas.veta(momento("2030-06-14"))); assertEquals(Veta.HUNDIDA, EscenaAntiguas.veta(momento("2030-06-29")))
        assertEquals(Veta.TAPADA, EscenaAntiguas.veta(momento("2030-06-30")))
        for (dia in listOf("2027-04-07", "2030-06-20", "2030-07-01")) assertEquals(dia, 0f, momento(dia).arco("veta"), 0f)
        // El refugio de Arn: nada, a medio hacer (quieto en 0.4 todo el invierno) y terminado.
        assertEquals(Refugio.NADA, EscenaAntiguas.refugio(momento("2029-05-26"))); assertEquals(Refugio.A_MEDIO, EscenaAntiguas.refugio(momento("2029-06-15")))
        assertEquals(Refugio.A_MEDIO, EscenaAntiguas.refugio(momento("2030-01-20"))); assertEquals(Refugio.TERMINADO, EscenaAntiguas.refugio(momento("2030-09-28")))
    }

    /** Lo que queda desde su día: el refugio de la Cabra hasta el alud, la senda de la guía, el cuarto alto, las casas vacías, el pino partido. */
    @Test fun loQueQuedaDesdeSuDia() {
        assertTrue(EscenaAntiguas.refugioDeLaCabra(momento("2028-02-24"))); assertFalse(EscenaAntiguas.refugioDeLaCabra(momento("2028-02-25")))
        assertFalse(EscenaAntiguas.sendaDeLaGuia(momento("2027-04-14"))); assertTrue(EscenaAntiguas.sendaDeLaGuia(momento("2027-04-15")))
        assertFalse(EscenaAntiguas.cuartoAlto(momento("2030-01-11"))); assertTrue(EscenaAntiguas.cuartoAlto(momento("2030-01-12")))
        assertFalse(EscenaAntiguas.casasVacias(momento("2031-06-04"))); assertTrue(EscenaAntiguas.casasVacias(momento("2031-06-05")))
        assertFalse(EscenaAntiguas.pinoPartido(momento("2031-07-09"))); assertTrue(EscenaAntiguas.pinoPartido(momento("2031-07-10"))); assertTrue(EscenaAntiguas.pinoPartido(momento("2032-01-10")))
        cadaDia { d, m -> assertEquals(d.toString(), EscenaAntiguas.lugar(m) == Lugar.CRIANZA, EscenaAntiguas.cuna(m)) }
    }

    /**
     * La sombra grande: por la tarde sube por el paso (primero el valle y el puente, luego el collado, al final las
     * cumbres); por la mañana baja (a las 7:00 solo lo alto tiene sol; a las 10:00, ya todo); a mediodía no hay; y con el
     * cielo cubierto no hay sombra arrojada.
     */
    @Test fun laSombraSubePorLaTardeYBajaPorLaManana() {
        val dia = "2027-08-03"
        fun sombra(h: Double) = LuzAntiguas.sombra(EscenaAntiguas.luces(conClima(dia, h, Clima.DESPEJADO)))
        // Por la tarde, la línea sube (su altura en el lienzo baja) hora a hora; por la mañana, al revés.
        var antes = Float.MAX_VALUE
        for (h in listOf(16.0, 17.0, 18.0, 19.0, 20.0)) { val s = sombra(h); assertFalse(s.manana); assertTrue("$h", s.cuanta > 0.1f); val y = s.y(SitioAntiguas.COLLADO.x); assertTrue("$h", y < antes); antes = y }
        antes = -Float.MAX_VALUE
        for (h in listOf(6.67, 7.0, 8.0, 9.0)) { val s = sombra(h); assertTrue(s.manana); val y = s.y(SitioAntiguas.COLLADO.x); assertTrue("$h", y > antes); antes = y }
        val puente = SitioAntiguas.PUENTE_ACA; val collado = SitioAntiguas.COLLADO; val cumbre = SitioAntiguas.CUMBRE
        assertTrue(LuzAntiguas.sombraEn(sombra(18.67), puente) > 0.7f); assertTrue(LuzAntiguas.sombraEn(sombra(18.67), collado) < 0.1f)
        assertTrue(LuzAntiguas.sombraEn(sombra(20.0), cumbre) < 0.1f)
        assertTrue(LuzAntiguas.sombraEn(sombra(20.0), collado) > 0.2f); assertTrue(LuzAntiguas.sombraEn(sombra(20.0), collado) > LuzAntiguas.sombraEn(sombra(19.0), collado))
        assertTrue(LuzAntiguas.sombraEn(sombra(7.0), cumbre) < 0.1f); assertTrue(LuzAntiguas.sombraEn(sombra(7.0), puente) > 0.5f)
        assertTrue(LuzAntiguas.sombraEn(sombra(10.33), puente) < 0.05f)
        for (h in listOf(11.0, 13.0, 14.67)) assertEquals("$h", 0f, LuzAntiguas.sombraEn(sombra(h), puente), 0f)
        // La franja de sol por la muesca: por la mañana temprano, y no a mediodía.
        assertNotNull(LuzAntiguas.franja(EscenaAntiguas.luces(conClima(dia, 7.0, Clima.DESPEJADO))))
        assertNull(LuzAntiguas.franja(EscenaAntiguas.luces(conClima(dia, 13.0, Clima.DESPEJADO))))
        // Con el cielo cubierto, ni línea de sombra, ni franja, ni sombras arrojadas.
        for (clima in listOf(Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE, Clima.NIEBLA)) for (h in listOf(7.0, 13.0, 17.0)) {
            val l = EscenaAntiguas.luces(conClima(dia, h, clima))
            assertEquals("$clima $h", 0f, LuzAntiguas.sombra(l).cuanta, 0f)
            assertNull("$clima $h", LuzAntiguas.franja(l))
            if (clima != Clima.NIEBLA) assertTrue("$clima $h", LuzAntiguas.rayo(l).fuerza < 0.02f)
        }
    }

    /** De noche, dos luces de color opuesto: la de la sala (la puerta, el ventanuco) y la de la luna, que tumba sus sombras. */
    @Test fun deNocheHayDosLuces() {
        var d = LocalDate.of(2026, 1, 3)
        while (d.year <= 2032) {
            val m = conClima(d.toString(), 21.0, Clima.DESPEJADO); val l = EscenaAntiguas.luces(m)
            assertTrue("$d", EscenaAntiguas.encendido(m).sala * l.noche > 0.5f)
            assertTrue("$d", LuzAntiguas.luna(l) > 0.5f)
            assertTrue("$d", LuzAntiguas.rayo(l).fuerza > 0.1f)
            d = d.plusDays(11)
        }
        // La noche de las linternas: todas encendidas en el pino y una en cada ventana; la Noche del Arpa, todas las de la posada.
        assertEquals(5, EscenaAntiguas.encendido(momento("2027-02-03", 21.0)).linternasDelPino)
        assertTrue(EscenaAntiguas.encendido(momento("2027-12-21", 23.0)).todasLasVentanas)
        // La crianza: la ventana del cuarto alto encendida de noche.
        assertTrue(EscenaAntiguas.encendido(momento("2031-04-20", 21.0)).cuartoAlto > 0.5f)
        assertEquals(0f, EscenaAntiguas.encendido(momento("2028-07-20", 21.0)).cuartoAlto, 0f)
        // La fila de los mineros que bajan: mientras hay mineros, con el paso abierto.
        assertTrue(EscenaAntiguas.encendido(momento("2028-07-20", 20.0)).filaDeMineros > 0)
        assertEquals(0, EscenaAntiguas.encendido(momento("2031-08-20", 20.0)).filaDeMineros)
    }

    /** Con la compañía en el camino no están las mulas ni el bastón, y la fila de mulas sube por las zetas; en la travesía no están nunca. */
    @Test fun laCompaniaSeVeEnElCamino() {
        cadaDia(8.0) { d, m ->
            val c = EscenaAntiguas.companiaEnElCamino(m)
            assertEquals(d.toString(), EscenaAntiguas.lugar(m) == Lugar.POSADA, c)
            if (c) { if (!hoyEs(m, "refugios")) assertEquals(d.toString(), 0, EscenaAntiguas.mulas(m)); assertFalse(EscenaAntiguas.bastonEnLaPuerta(m)) }
            if (hoyEs(m, "refugios")) assertEquals(d.toString(), 2, EscenaAntiguas.mulas(m))
            if (c && m.ficha.clima != Clima.NIEBLA) assertNotNull(d.toString(), EscenaAntiguas.filaEnElCamino(m))
            if (EscenaAntiguas.lugar(m) == Lugar.TRAVESIA) { assertEquals(0, EscenaAntiguas.mulas(m)); assertFalse(EscenaAntiguas.bastonEnLaPuerta(m)) }
        }
        val noche = momento("2027-05-05", 21.0)
        assertEquals(2, EscenaAntiguas.mulas(noche)); assertTrue(EscenaAntiguas.bastonEnLaPuerta(noche)); assertNull(EscenaAntiguas.filaEnElCamino(noche))
        // La fila sube por la mañana y baja por la tarde.
        val sube = EscenaAntiguas.filaEnElCamino(conClima("2027-05-05", 7.0, Clima.DESPEJADO))!!; val arriba = EscenaAntiguas.filaEnElCamino(conClima("2027-05-05", 10.0, Clima.DESPEJADO))!!
        val baja = EscenaAntiguas.filaEnElCamino(conClima("2027-05-05", 16.0, Clima.DESPEJADO))!!
        assertTrue(sube < arriba); assertTrue(baja < arriba)
        assertTrue(EscenaAntiguas.mulasEnLaFila(momento("2027-07-20")) > EscenaAntiguas.mulasEnLaFila(momento("2027-07-21")))
    }

    /** Cada día del cuento que deja algo a la vista lo deja ese día (su señal), y todos aparecen entre 2026 y 2032. */
    @Test fun cadaDiaDelCuentoCambiaAlgo() {
        val vistos = HashSet<String>()
        cadaDia { d, m ->
            for ((id, senal) in EscenaAntiguas.SENAL_DE) if (hoyEs(m, id)) {
                vistos += id
                assertTrue("$d $id", senal in EscenaAntiguas.senales(m))
                // Y es algo que el día de antes no estaba (salvo que ayer fuera el mismo día especial, o uno que deja lo mismo).
                val ayer = momento(d.minusDays(1).toString())
                if (EscenaAntiguas.SENAL_DE.none { (otro, s) -> s == senal && hoyEs(ayer, otro) } && !(senal == EscenaAntiguas.Senal.CUNA && EscenaAntiguas.cuna(ayer)) && !(senal == EscenaAntiguas.Senal.LAZO_NEGRO && ayer.con("luto")))
                    assertFalse("$d $id", senal in EscenaAntiguas.senales(ayer))
            }
        }
        assertEquals(EscenaAntiguas.SENAL_DE.keys, vistos)
        // El segundo día de la tormenta, las piedras caídas en la pedrera.
        assertTrue(EscenaAntiguas.Senal.PIEDRAS_CAIDAS in EscenaAntiguas.senales(momento("2027-07-09"))); assertFalse(EscenaAntiguas.Senal.PIEDRAS_CAIDAS in EscenaAntiguas.senales(momento("2027-07-08")))
    }

    @Test fun elGatoTieneSueloLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaAntiguas.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 18.5, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaAntiguas.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 30f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO)
                val estorbos = EscenaAntiguas.estorbos(m)
                for (x in estorbos) assertTrue("$d $h: lo plantado está fuera del suelo del gato", x in suelo.x..suelo.hasta)
                val quieto = gatoQuieto(suelo, anclas, talla, estorbos)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in estorbos) assertTrue("$d $h: quieto encima de lo plantado", abs(quieto.x - x) >= medio)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = EscenaAntiguas.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFFA09A8C), Color.White, Color.Black, Color(0xFF8E1B24))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f)
                }
            }
            d = d.plusDays(5)
        }
    }

    @Test fun losObjetosSonLosDelCalendarioYNoSalenDosVeces() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosAntiguas.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosAntiguas.TODOS.containsAll(ObjetosAntiguas.GRANDES + ObjetosAntiguas.YA_EN_EL_CUADRO))
        val base = momento("2027-05-05").ficha
        for (pieza in ObjetosAntiguas.YA_EN_EL_CUADRO) assertEquals(pieza, pieza !in ObjetosAntiguas.DIBUJADOS, ObjetosAntiguas.elCuadroPinta(Momento(base.copy(objeto = pieza), 13.0), pieza))
        for (o in ObjetosAntiguas.TODOS) assertEquals(o, o in ObjetosAntiguas.GRANDES, ObjetosAntiguas.esGrande(Momento(base.copy(objeto = o), 13.0)))
    }

    @Test fun elMismoMomentoDaLoMismo() {
        val a = momento("2027-05-05", 13.0); val b = momento("2027-05-05", 13.0)
        assertEquals(EscenaAntiguas.senales(a), EscenaAntiguas.senales(b))
        assertEquals(EscenaAntiguas.anclas(a).map { it.x to it.y }, EscenaAntiguas.anclas(b).map { it.x to it.y })
        cadaDia { d, m -> assertTrue("$d", EscenaAntiguas.seMueve(m)) }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** El cuadro se dibuja entero (la parte quieta y la viva) en cada estado, a cada hora, con cada tiempo y con cada objeto, sin lanzar nada. */
    @Test fun seDibujaEnCadaEstadoACadaHoraYConCadaTiempo() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-05-05", "2027-01-15", "2027-01-01", "2027-02-03", "2027-03-22", "2027-05-14", "2027-07-08", "2027-07-09", "2027-08-24", "2027-09-06",
            "2027-12-06", "2027-12-21", "2028-02-25", "2028-10-14", "2029-07-30", "2029-09-01", "2030-06-15", "2030-08-24", "2030-09-28", "2031-04-12", "2031-07-10", "2031-08-20")
        val lugares = HashSet<Lugar>()
        for (dia in dias) for (h in listOf(4.0, 7.0, 13.0, 17.0, 21.0, 23.5)) {
            val base = momento(dia, h); lugares += EscenaAntiguas.lugar(base)
            for (o in ObjetosAntiguas.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima) + Clima.entries) {
                if (clima != base.ficha.clima && o != null) continue
                if (o != null && h != 13.0) continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                    lienzo { with(EscenaAntiguas) { quieta(m) } }
                    lienzo { with(EscenaAntiguas) { viva(m, 3.7f) } }
                }
            }
        }
        assertEquals(Lugar.entries.toSet(), lugares)
    }
}
