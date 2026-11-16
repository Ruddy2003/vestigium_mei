package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Cielo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Grama
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Jardines
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaLaboratorio.Tarea
import io.github.ruddy2003.vestigium.ui.escenas.Fuente
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosLaboratorio
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.VistaLab
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Laboratorio (tanda S17: el laboratorio de la grama) cuenta lo mismo que el cuento de la doctora: dónde está
 * (el laboratorio, la cuarentena, la campana, el cierre y arriba, también con la subida movida desde 2032), los tres arcos
 * (los jardines, la grama, lo verde de arriba) y sus tramos, la luz que camina con el horario del riego, el cielo de la
 * pantalla a la hora de arriba, las tres luces de la noche, los tres días que cambian la luz y lo que cambia cada día del
 * cuento; todo sale de la ficha del día y aquí se compara con su calendario (`AnioG8.kt`) de 2026 a 2032.
 */
class EscenaLaboratorioPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "cientifica")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeLaDoctora() {
        assertEquals(listOf("cientifica"), EscenaLaboratorio.vidas)
        assertEquals("cientifica", EscenaLaboratorio.vida("2027-11-10"))
        assertEquals("laboratorio", OtrasVidas.porId("cientifica")!!.tema)
        assertTrue(EscenaLaboratorio.tallaDelGato in 0.8f..1.8f)
    }

    // ---------------------------------------------------------------- Dónde está

    @Test fun estaDondeDiceElCalendario() {
        val cuenta = HashMap<Pair<Int, Lugar>, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "cuarentena" -> Lugar.CUARENTENA; "experimento" -> Lugar.CAMPANA; "cierre" -> Lugar.CIERRE; "arriba" -> Lugar.ARRIBA; else -> Lugar.LABORATORIO }
            assertEquals(d.toString(), esperado, EscenaLaboratorio.lugar(m))
            assertEquals(d.toString(), Cuadro.LABORATORIO, EscenaLaboratorio.cuadro(m))
            assertEquals(d.toString(), esperado == Lugar.ARRIBA, EscenaLaboratorio.provisional(m))
            assertEquals(d.toString(), esperado == Lugar.CUARENTENA || esperado == Lugar.CIERRE, EscenaLaboratorio.luzRoja(m))
            cuenta[d.year to esperado] = (cuenta[d.year to esperado] ?: 0) + 1
        }
        // La cuarentena: 28 días de 2028; la campana: 21 de 2029; el cierre: 14 de 2030; arriba: 28 en 2031 y cada año desde 2032.
        assertEquals(28, cuenta[2028 to Lugar.CUARENTENA]); assertEquals(21, cuenta[2029 to Lugar.CAMPANA]); assertEquals(14, cuenta[2030 to Lugar.CIERRE])
        assertEquals(28, cuenta[2031 to Lugar.ARRIBA]); assertEquals(28, cuenta[2032 to Lugar.ARRIBA])
        for (a in listOf(2026, 2027)) assertEquals(if (a == 2026) 365 else 365, cuenta[a to Lugar.LABORATORIO])
        // Los días de cambio: el primer día ya es el estado, el día de vuelta ya es el laboratorio.
        for ((dia, lugar) in listOf("2028-03-07" to Lugar.LABORATORIO, "2028-03-08" to Lugar.CUARENTENA, "2028-04-04" to Lugar.CUARENTENA, "2028-04-05" to Lugar.LABORATORIO,
            "2029-05-02" to Lugar.LABORATORIO, "2029-05-03" to Lugar.CAMPANA, "2029-05-23" to Lugar.CAMPANA, "2029-05-24" to Lugar.LABORATORIO,
            "2030-09-27" to Lugar.LABORATORIO, "2030-09-28" to Lugar.CIERRE, "2030-10-11" to Lugar.CIERRE, "2030-10-12" to Lugar.LABORATORIO,
            "2031-04-25" to Lugar.LABORATORIO, "2031-04-26" to Lugar.ARRIBA, "2031-05-23" to Lugar.ARRIBA, "2031-05-24" to Lugar.LABORATORIO,
            // La subida movida de 2032 (la cadena `subida` se sortea entera desde 2032): del 20 de marzo al 16 de abril.
            "2032-03-19" to Lugar.LABORATORIO, "2032-03-20" to Lugar.ARRIBA, "2032-04-16" to Lugar.ARRIBA, "2032-04-17" to Lugar.LABORATORIO, "2032-04-26" to Lugar.LABORATORIO))
            assertEquals(dia, lugar, EscenaLaboratorio.lugar(momento(dia)))
        // Arriba: el disco del sol en la pantalla solo desde el día del sol (el segundo de la subida); el primero, no.
        assertFalse(EscenaLaboratorio.solEnLaPantalla(momento("2031-04-26", 13.0))); assertTrue(EscenaLaboratorio.solEnLaPantalla(momento("2031-04-27", 13.0)))
        assertFalse(EscenaLaboratorio.solEnLaPantalla(momento("2032-03-20", 13.0))); assertTrue(EscenaLaboratorio.solEnLaPantalla(momento("2032-03-21", 13.0)))
        assertFalse(EscenaLaboratorio.solEnLaPantalla(momento("2027-05-05", 13.0)))
        assertEquals(Clima.DESPEJADO, EscenaLaboratorio.climaDeArriba(momento("2031-04-27"))); assertEquals(Clima.LLUVIA, EscenaLaboratorio.climaDeArriba(momento("2031-05-13")))
        assertTrue(EscenaLaboratorio.deslumbra(momento("2031-04-27", 13.0))); assertFalse(EscenaLaboratorio.deslumbra(momento("2031-04-28", 13.0)))
    }

    // ---------------------------------------------------------------- Los tres arcos

    @Test fun losJardinesSiguenAlArcoYSusCuatroTramos() {
        val vistos = HashSet<Jardines>()
        cadaDia { d, m ->
            val v = m.ficha.arcos["jardines"]!!.toFloat()
            assertEquals(d.toString(), v.coerceIn(0f, 1f), EscenaLaboratorio.jardines(m), 0.0001f)
            // Los tramos del cuento (`ENTORNOS` en `PiezasCientifica.kt`): rangos cerrados, manda el primero.
            val esperado = if (v <= 0.08f) Jardines.VACIOS else if (v <= 0.35f) Jardines.BROTES else if (v <= 0.75f) Jardines.MEDIO else Jardines.LLENOS
            assertEquals(d.toString(), esperado, EscenaLaboratorio.tramoJardines(m)); vistos += esperado
            assertEquals(d.toString(), if (md(d) in "09-10".."09-12") md(d).takeLast(2).toInt() - 9 else 0, EscenaLaboratorio.siega(m))
        }
        assertEquals(Jardines.entries.toSet(), vistos)
        assertEquals(Jardines.LLENOS, EscenaLaboratorio.tramoJardines(momento("2027-05-05"))); assertEquals(Jardines.BROTES, EscenaLaboratorio.tramoJardines(momento("2027-09-10")))
        assertEquals(Jardines.MEDIO, EscenaLaboratorio.tramoJardines(momento("2026-11-16"))); assertEquals(Jardines.VACIOS, EscenaLaboratorio.tramoJardines(momento("2028-03-28")))
        assertEquals(Jardines.MEDIO, EscenaLaboratorio.tramoJardines(momento("2028-03-27")))
    }

    @Test fun laGramaSigueLosHitosDeLaInvestigacion() {
        val vistos = HashSet<Grama>()
        cadaDia { d, m -> vistos += EscenaLaboratorio.grama(m); assertTrue(d.toString(), EscenaLaboratorio.brotes(m) in 0..20); if (EscenaLaboratorio.grama(m) == Grama.TIERRA) assertEquals(0, EscenaLaboratorio.brotes(m)) }
        assertEquals(Grama.entries.toSet(), vistos)
        for ((dia, g) in listOf("2027-05-13" to Grama.TIERRA, "2027-05-14" to Grama.HILO, "2026-05-14" to Grama.HILO, "2029-05-02" to Grama.HILO, "2029-05-03" to Grama.MATAS,
            "2030-03-01" to Grama.MATAS, "2030-03-02" to Grama.TANQUE, "2031-04-25" to Grama.TANQUE, "2031-04-26" to Grama.CAJAS, "2031-12-31" to Grama.CAJAS))
            assertEquals(dia, g, EscenaLaboratorio.grama(momento(dia)))
        // Dentro de una temporada, las bandejas con brote no bajan.
        var antes = 0; var d = LocalDate.of(2027, 5, 14)
        while (d < LocalDate.of(2027, 12, 31)) { val b = EscenaLaboratorio.brotes(momento(d.toString())); assertTrue("$d", b >= antes); antes = b; d = d.plusDays(1) }
    }

    @Test fun loVerdeDeArribaCreceDesdeElDiaDelVerde() {
        cadaDia { d, m -> assertEquals(d.toString(), m.ficha.arcos["superficie"]!!.toFloat().coerceIn(0f, 1f), EscenaLaboratorio.superficie(m), 0.0001f) }
        assertEquals(0f, EscenaLaboratorio.superficie(momento("2030-12-09")), 0f)
        assertTrue(EscenaLaboratorio.superficie(momento("2030-12-20")) > 0f)
        assertEquals(0.2f, EscenaLaboratorio.superficie(momento("2031-04-26")), 0.002f)
        assertEquals(0.5f, EscenaLaboratorio.superficie(momento("2031-12-31")), 0.002f)
    }

    // ---------------------------------------------------------------- La luz

    @Test fun lasLamparasDeCultivoCaminanConLaHora() {
        // Un día normal: por la mañana las del fondo, a mediodía todas, por la tarde las de este extremo; de noche, violetas y bajas.
        val d = "2027-05-05"
        assertEquals(setOf(0, 1, 2), EscenaLaboratorio.encendidas(momento(d, 7.0)))
        assertEquals(setOf(0, 1, 2, 3, 4), EscenaLaboratorio.encendidas(momento(d, 13.0)))
        assertEquals(setOf(3, 4), EscenaLaboratorio.encendidas(momento(d, 17.5)))
        assertEquals(emptySet<Int>(), EscenaLaboratorio.encendidas(momento(d, 21.0)))
        assertTrue(EscenaLaboratorio.cultivoDeNoche(momento(d, 21.0)) > 0f); assertEquals(0f, EscenaLaboratorio.cultivoDeNoche(momento(d, 13.0)), 0f)
        // El centro de la luz de la mesa se mueve de la puerta hacia quien mira a lo largo del día, todos los días del laboratorio.
        cadaDia { dia, m0 ->
            if (EscenaLaboratorio.lugar(m0) != Lugar.LABORATORIO || EscenaLaboratorio.apagon(m0) || EscenaLaboratorio.anioNuevo(m0)) return@cadaDia
            fun centro(h: Double): Float { val m = momento(dia.toString(), h); val w = (0 until EscenaLaboratorio.LAMPARAS).map { EscenaLaboratorio.cultivo(m, it) }; return w.withIndex().sumOf { (i, k) -> (i * k).toDouble() }.toFloat() / w.sum() }
            assertTrue("$dia", centro(7.0) < centro(13.0) && centro(13.0) < centro(17.5))
        }
        // La campana deja las de la sala a medias; el año nuevo, todas a la vez.
        assertTrue((0 until 5).all { EscenaLaboratorio.cultivo(momento("2029-05-10", 13.0), it) in 0.4f..0.6f })
        assertEquals(setOf(0, 1, 2, 3, 4), EscenaLaboratorio.encendidas(momento("2027-01-01", 7.0)))
    }

    @Test fun elCieloDeLaPantallaCambiaConLaHora() {
        val d = "2027-05-05"
        assertEquals(listOf(Cielo.ALBA, Cielo.DIA, Cielo.TARDE, Cielo.NOCHE, Cielo.NOCHE), listOf(7.0, 13.0, 18.0, 21.0, 1.0).map { EscenaLaboratorio.cielo(momento(d, it)) })
        val colores = listOf(7.0, 13.0, 18.0, 21.0).map { EscenaLaboratorio.lucesDeArriba(momento(d, it)).alto }
        assertEquals(colores.size, colores.toSet().size)
        // La sala se tiñe con el color de la pantalla, distinto a cada hora; de noche, más que de día.
        val tintes = listOf(7.0, 13.0, 21.0).map { EscenaLaboratorio.colorDePantalla(momento(d, it)) }
        assertEquals(3, tintes.toSet().size)
        assertTrue(EscenaLaboratorio.colorDePantalla(momento(d, 7.0)).let { it.red > it.blue })              // el alba, dorada
        assertTrue(EscenaLaboratorio.colorDePantalla(momento(d, 21.0)).let { it.blue > it.red })             // la noche, azul
    }

    @Test fun deNocheHayTresLucesDeColorOpuesto() {
        // La noche de un día normal, con la música: la lámpara de su mesa (cálida), el violeta de los jardines por la puerta y la pantalla (fría).
        val v = VistaLab(momento("2027-05-05", 21.0))
        val mesa = v.luces.single { it.de == Fuente.MESA }; val puerta = v.luces.single { it.de == Fuente.PUERTA }; val pantalla = v.luces.single { it.de == Fuente.PANTALLA }
        assertTrue(mesa.color.red > mesa.color.blue); assertTrue(puerta.color.blue > puerta.color.red); assertTrue(pantalla.color.blue > pantalla.color.red)
        for (f in listOf(mesa, puerta, pantalla)) assertTrue(f.fuerza > 0.3f)
        // Las tres tumban sombra; de día, la lámpara de su mesa está apagada a la hora de comer y la puerta no tumba.
        assertTrue(v.tumban.containsAll(listOf(mesa, puerta, pantalla)))
        val dia = VistaLab(momento("2027-05-05", 13.0))
        assertTrue(dia.luces.none { it.de == Fuente.MESA }); assertTrue(dia.tumban.none { it.de == Fuente.PUERTA })
        // De madrugada, sin la lámpara de la mesa, sigue habiendo una cálida (la campana) y dos frías.
        val madrugada = VistaLab(momento("2027-05-05", 2.0))
        assertTrue(madrugada.luces.any { it.de == Fuente.CAMPANA && it.color.red > it.color.blue }); assertTrue(madrugada.luces.any { it.de == Fuente.PUERTA }); assertTrue(madrugada.luces.any { it.de == Fuente.PANTALLA })
    }

    @Test fun losTresDiasDeLuzEspecial() {
        // La luz larga: de noche los jardines siguen en blanco y su luz entra por la puerta.
        val larga = momento("2027-06-21", 23.0)
        assertTrue(EscenaLaboratorio.luzLarga(larga)); assertTrue(EscenaLaboratorio.jardinEnBlanco(larga)); assertEquals(0f, EscenaLaboratorio.violeta(larga), 0f)
        assertTrue(VistaLab(larga).luces.single { it.de == Fuente.PUERTA }.color.let { it.red > 0.8f && it.green > 0.8f })
        assertFalse(EscenaLaboratorio.jardinEnBlanco(momento("2027-06-20", 23.0)))
        // La noche larga: se apagan todas las lámparas; quedan las velas (y la pantalla, que no se apaga nunca).
        val noche = momento("2027-12-21", 21.5)
        assertTrue(EscenaLaboratorio.nocheLarga(noche)); assertTrue(EscenaLaboratorio.velas(noche))
        assertEquals(0f, EscenaLaboratorio.cultivoDeNoche(noche), 0f); assertEquals(0f, EscenaLaboratorio.lamparaDeMesa(noche), 0f); assertEquals(0f, EscenaLaboratorio.violeta(noche), 0f)
        assertTrue(VistaLab(noche).luces.count { it.de == Fuente.VELA } >= 3); assertTrue(VistaLab(noche).luces.any { it.de == Fuente.PANTALLA })
        assertFalse(EscenaLaboratorio.velas(momento("2027-12-20", 21.5)))
        // El apagón: de día, media luz; de noche, todo apagado salvo la linterna de manivela (también la pantalla).
        val apagonDia = momento("2027-07-08", 13.0); val apagonNoche = momento("2027-07-08", 22.0)
        assertTrue(EscenaLaboratorio.apagon(apagonDia)); assertEquals(0.45f, EscenaLaboratorio.general(apagonDia), 0.001f)
        assertTrue((0 until 5).all { EscenaLaboratorio.cultivo(apagonDia, it) <= 0.46f })
        assertTrue(EscenaLaboratorio.linterna(apagonNoche)); assertTrue(EscenaLaboratorio.pantallaApagada(apagonNoche))
        assertEquals(setOf(Fuente.LINTERNA), VistaLab(apagonNoche).luces.map { it.de }.toSet())
        assertFalse(EscenaLaboratorio.apagon(momento("2027-07-07", 13.0)))
    }

    @Test fun cadaDiaDelCuentoCambiaAlgo() {
        // Lo que el cuadro cuenta cada día del cuento: el día, y no la víspera.
        fun par(dia: String, f: (Momento) -> Boolean) { val d = LocalDate.parse(dia); assertTrue(dia, f(momento(dia, 13.0))); assertFalse("víspera de $dia", f(momento(d.minusDays(1).toString(), 13.0))) }
        par("2027-05-14") { EscenaLaboratorio.germina(it) }
        par("2027-09-10") { EscenaLaboratorio.siega(it) == 1 }
        par("2027-03-18") { EscenaLaboratorio.fundacion(it) }
        par("2027-01-01") { EscenaLaboratorio.anioNuevo(it) }
        par("2027-04-08") { EscenaLaboratorio.humo(it) }
        par("2030-03-02") { EscenaLaboratorio.funciona(it) }
        par("2027-12-04") { EscenaLaboratorio.cajaDeHilda(it) }
        par("2027-10-15") { EscenaLaboratorio.luto(it) && EscenaLaboratorio.memoria(it) }
        par("2027-02-03") { EscenaLaboratorio.lauro(it) }
        par("2029-01-20") { EscenaLaboratorio.cuadernosDeHilda(it) }
        par("2030-09-27") { EscenaLaboratorio.bataDeLauro(it) }
        par("2030-12-10") { EscenaLaboratorio.superficie(it) > 0f }
        assertTrue(EscenaLaboratorio.bataDeLauro(momento("2031-05-16"))); assertFalse(EscenaLaboratorio.bataDeLauro(momento("2031-05-17")))
        assertFalse(EscenaLaboratorio.lauro(momento("2030-09-27")))
        // Y el cuadro se ve distinto ese día que la víspera (con el mismo objeto y a la misma hora).
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        for (dia in listOf("2027-05-14", "2027-09-10", "2027-03-18", "2027-01-01", "2027-04-08", "2030-03-02", "2027-12-04", "2027-06-21", "2027-12-21", "2027-07-08", "2030-12-10")) {
            val h = if (dia == "2027-06-21" || dia == "2027-12-21") 22.0 else 13.0
            val antes = LocalDate.parse(dia).minusDays(1).toString()
            assertNotEquals(dia, pixeles(sinObjeto(momento(antes, h))).toList(), pixeles(sinObjeto(momento(dia, h))).toList())
        }
    }

    // ---------------------------------------------------------------- Lo que hace ella

    @Test fun laMesaSigueSuHorario() {
        val d = "2027-05-05"
        assertEquals(listOf(Tarea.DESAYUNO, Tarea.TRABAJO, Tarea.COMIDA, Tarea.TRABAJO, Tarea.RECOGIDO, Tarea.MUSICA, Tarea.RECOGIDO),
            listOf(7.0, 10.0, 13.0, 15.0, 20.0, 21.5, 2.0).map { EscenaLaboratorio.tarea(momento(d, it)) })
        // Arriba y en la campana, la mesa recogida y su lámpara apagada.
        for (dia in listOf("2031-05-01", "2029-05-10")) for (h in listOf(7.0, 13.0, 21.5)) { assertEquals(Tarea.RECOGIDO, EscenaLaboratorio.tarea(momento(dia, h))); assertEquals(0f, EscenaLaboratorio.lamparaDeMesa(momento(dia, h)), 0f) }
        assertTrue(EscenaLaboratorio.lamparaDeMesa(momento(d, 21.5)) > 0.9f); assertEquals(0f, EscenaLaboratorio.lamparaDeMesa(momento(d, 13.0)), 0f)
    }

    // ---------------------------------------------------------------- El gato

    /** Lo que vigila `GatoCuadroPrueba` en todos los cuadros, más el tramo libre de al menos 110 entre lo plantado y el objeto. */
    @Test fun elGatoTieneSueloLibreLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaLaboratorio.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Lugar>()
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(2.0, 7.0, 13.0, 17.5, 21.0)) {
                val m = momento(d.toString(), h); vistos += EscenaLaboratorio.lugar(m)
                for (o in listOf(m.ficha.objeto, "bata", "taza")) {
                    val mo = Momento(m.ficha.copy(objeto = o), h)
                    val anclas = EscenaLaboratorio.anclas(mo); val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                    assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                    assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                    for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                    for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                    // El tramo libre: entre lo plantado, el objeto y los extremos, al menos 110 seguidos.
                    val estorbos = EscenaLaboratorio.estorbos(mo)
                    for (x in estorbos) assertTrue("$d $h: lo plantado fuera del suelo del gato", x in suelo.x..suelo.hasta)
                    val cortes = (listOf(suelo.x, suelo.hasta) + estorbos + anclas.filter { it.tipo == TipoAncla.OBJETO }.map { it.x }).sorted()
                    assertTrue("$d $h: no hay un tramo libre de 110 (${cortes})", cortes.zipWithNext().maxOf { (a, b) -> b - a } >= 110f)
                    val quieto = gatoQuieto(suelo, anclas, talla, estorbos)
                    assertEquals(AnimGato.SENTADO, quieto.anim)
                    for (x in estorbos) assertTrue("$d $h: quieto encima de lo plantado", abs(quieto.x - x) >= medio)
                    for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                        val luz = EscenaLaboratorio.luzDelGato(mo, Offset(x, suelo.y))
                        for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                        assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                    }
                }
            }
            d = d.plusDays(5)
        }
        assertEquals(Lugar.entries.toSet(), vistos)
    }

    // ---------------------------------------------------------------- Los objetos

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosLaboratorio.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosLaboratorio.TODOS.containsAll(ObjetosLaboratorio.GRANDES)); assertTrue(ObjetosLaboratorio.TODOS.containsAll(ObjetosLaboratorio.DE_LA_SALA))
        assertTrue(ObjetosLaboratorio.DE_LA_SALA.containsAll(ObjetosLaboratorio.DIBUJADOS))
        cadaDia { d, m -> assertEquals(d.toString(), m.ficha.objeto in ObjetosLaboratorio.GRANDES, ObjetosLaboratorio.esGrande(m)) }
    }

    @Test fun seMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaLaboratorio.seMueve(m)) }
        val a = momento("2027-05-05", 13.0); val b = momento("2027-05-05", 13.0)
        assertEquals(EscenaLaboratorio.anclas(a).map { it.x to it.y }, EscenaLaboratorio.anclas(b).map { it.x to it.y })
        assertEquals(VistaLab(a).luces.map { it.w to it.fuerza }, VistaLab(b).luces.map { it.w to it.fuerza })
    }

    // ---------------------------------------------------------------- El dibujo

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }
    private fun sinObjeto(m: Momento) = Momento(m.ficha.copy(objeto = null), m.hora)

    /** El cuadro quieto dibujado a 190 de ancho, como lista de píxeles. */
    private fun pixeles(m: Momento): IntArray {
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val img = ImageBitmap(ancho, alto)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(ancho.toFloat(), alto.toFloat())) { lienzo { with(EscenaLaboratorio) { quieta(m) } } }
        return img.toPixelMap().buffer
    }

    /**
     * El cuadro se dibuja entero (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, a las horas de cada
     * franja, con lluvia y despejado (arriba) y en los estados y días señalados, sin lanzar nada.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-05-05", "2027-05-14", "2027-07-08", "2027-06-21", "2027-12-21", "2027-03-18", "2027-09-10", "2027-12-04", "2028-03-20", "2028-03-30", "2029-05-12", "2030-03-02", "2030-10-05", "2031-04-26", "2031-04-27", "2031-05-13", "2032-04-01")
        val vistos = HashSet<Lugar>()
        for (dia in dias) for (h in listOf(2.0, 7.0, 13.0, 17.5, 21.0)) {
            val base = momento(dia, h); vistos += EscenaLaboratorio.lugar(base)
            for (o in ObjetosLaboratorio.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.LLUVIA)) {
                if (clima != base.ficha.clima && o != null) continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaLaboratorio) { quieta(m) } }
                        lienzo { with(EscenaLaboratorio) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(Lugar.entries.toSet(), vistos)
    }
}
