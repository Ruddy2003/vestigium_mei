package io.github.ruddy2003.vestigium

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.Ancla
import io.github.ruddy2003.vestigium.ui.escenas.EscenaHeleno
import io.github.ruddy2003.vestigium.ui.escenas.EscenaHeleno.Nave
import io.github.ruddy2003.vestigium.ui.escenas.EscenaVida
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria
import io.github.ruddy2003.vestigium.ui.escenas.escenaDe
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lucesDe
import io.github.ruddy2003.vestigium.ui.escenas.sinCuadro
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.luzDe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El motor de escena del cuadro de Hoy (plan de historia y escenas, tanda P4a): lo que se puede comprobar sin dibujar.
 * El dibujo se revisa en las capturas (`CAPTURAS=cuadro`).
 */
class EscenasPrueba {
    private fun ficha(dia: String) = FichaDelDia.de(dia, "remero")!!
    private fun momento(dia: String, hora: Double) = Momento(ficha(dia), hora)
    private fun nave(dia: String, hora: Double) = EscenaHeleno.nave(momento(dia, hora))
    private fun claridad(c: Color) = 0.299f * c.red + 0.587f * c.green + 0.114f * c.blue
    private fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha in 0f..1f
    private fun cielo(hora: Double, clima: Clima = Clima.DESPEJADO, eclipse: Float = 0f): Luces = lucesDe(luzDe(hora, RegimenLuz.SEIS_FRANJAS), clima, eclipse)

    // ---------------------------------------------------------------- Qué tema tiene cuadro

    @Test fun cadaEscenaTieneSuBanderitaSuPersonajeYSuCalendario() {
        TemaId.entries.forEach { t ->
            val escena = escenaDe(t)
            assertEquals("${t.id}: la escena y la banderita van juntas", escena == null, banderitaHistoria(t) == null)
            if (sinCuadro(t)) assertNull("${t.id} no lleva cuadro", escena)
            if (escena != null) {
                assertTrue("${t.id}: la escena no dice de quién es", escena.vidas.isNotEmpty())
                escena.vidas.forEach { id ->
                    val vida = OtrasVidas.porId(id)
                    assertNotNull("${t.id}: no existe la vida «$id»", vida)
                    assertEquals("La vida «$id» no es la de ${t.id}", t.id, vida!!.tema)
                    assertNotNull("«$id» necesita su calendario para tener cuadro", FichaDelDia.de("2027-03-21", id))
                }
            }
        }
        assertEquals("Solo Terminal, Navidad y Básico se quedan sin cuadro", setOf(TemaId.CIBER, TemaId.NAVIDAD, TemaId.BASICO), TemaId.entries.filter { sinCuadro(it) }.toSet())
        assertEquals(listOf("remero"), escenaDe(TemaId.HELENO)!!.vidas)
        assertEquals(listOf("cantero"), escenaDe(TemaId.GOTICO)!!.vidas)
        assertEquals(listOf("repartidora", "fideos"), escenaDe(TemaId.NEON)!!.vidas)
    }

    @Test fun conDosPersonajesSeAlternanUnDiaCadaUno() {
        val dos = object : EscenaVida {
            override val vidas = listOf("a", "b")
            override fun DrawScope.quieta(m: Momento) = Unit
            override fun DrawScope.viva(m: Momento, reloj: Float) = Unit
            override fun seMueve(m: Momento) = false
            override fun anclas(m: Momento) = emptyList<Ancla>()
        }
        var d = LocalDate.of(2026, 12, 20)
        repeat(40) {
            assertNotEquals("Dos días seguidos con el mismo personaje ($d)", dos.vida(d.toString()), dos.vida(d.plusDays(1).toString()))
            assertEquals(dos.vida(d.toString()), dos.vida(d.plusDays(2).toString()))
            d = d.plusDays(1)
        }
        assertEquals("remero", EscenaHeleno.vida("2027-01-01"))
    }

    // ---------------------------------------------------------------- La luz

    @Test fun laLuzEsValidaATodasHorasConTodoTiempo() {
        val muestras = listOf(Color.White, Color.Black, Color(0xFF2E5E8E), Color(0xFFEDE6D6), Color(0xFFC2652F))
        for (regimen in RegimenLuz.entries) for (clima in Clima.entries) for (eclipse in listOf(0f, 0.4f, 1f)) for (paso in 0 until 24 * 6) {
            // Si un color se saliera de 0 a 1, `Color(...)` lanzaría aquí mismo.
            val l = lucesDe(luzDe(paso / 6.0, regimen), clima, eclipse)
            val donde = "$regimen, $clima, eclipse $eclipse, ${paso / 6.0} h"
            (listOf(l.alto, l.medio, l.bajo, l.ambiente, l.bruma, l.solColor) + muestras.flatMap { c -> listOf(l.tono(c), l.tono(c, 0.5f), l.tono(c, 1f), l.cara(c, 1f), l.cara(c, -1f, 0.7f)) })
                .forEach { assertTrue("Color fuera de rango ($donde)", valido(it)) }
            assertTrue(donde, l.noche in 0f..1f && l.estrellas in 0f..1f && l.brumaFuerza in 0f..1f && l.nublado in 0f..1f && l.solFuerza in 0f..1f)
            assertTrue(donde, l.solX in -0.1f..1.1f && l.lunaX in 0f..1f && l.lunaAltura in 0f..1f)
        }
    }

    @Test fun deNocheSeEnciendenLasLucesYDeDiaNo() {
        assertEquals(0f, cielo(13.0).noche, 0.001f)
        assertEquals(0f, cielo(13.0).estrellas, 0.001f)
        assertEquals(1f, cielo(1.0).noche, 0.001f)
        assertEquals(1f, cielo(1.0).estrellas, 0.001f)
        assertTrue("Con lluvia no se ven estrellas", cielo(1.0, Clima.LLUVIA).estrellas < 0.2f)
        assertEquals("A las 21 ya hay que encender", true, cielo(21.0).noche > 0.8f)
        // Las tres horas de las capturas dan tres luces distintas: mediodía, amanecer y noche, de más a menos clara.
        val c7 = claridad(cielo(7.0).ambiente); val c13 = claridad(cielo(13.0).ambiente); val c21 = claridad(cielo(21.0).ambiente)
        assertTrue("7 h: $c7, 13 h: $c13, 21 h: $c21", c13 > c7 + 0.03f && c7 > c21 + 0.3f)
        // Un día cubierto es más oscuro que uno despejado, y una tormenta, más que la lluvia.
        assertTrue(claridad(cielo(13.0, Clima.LLUVIA).ambiente) < claridad(cielo(13.0).ambiente) - 0.1f)
        assertTrue(claridad(cielo(13.0, Clima.TORMENTA).ambiente) < claridad(cielo(13.0, Clima.LLUVIA).ambiente))
        // Los interiores: encendido y tenue.
        assertEquals(0f, lucesDe(luzDe(13.0, RegimenLuz.DOS_ESTADOS), Clima.SIN_CLIMA).noche, 0.001f)
        assertEquals(1f, lucesDe(luzDe(23.0, RegimenLuz.DOS_ESTADOS), Clima.SIN_CLIMA).noche, 0.001f)
        assertEquals(1f, lucesDe(luzDe(13.0, RegimenLuz.SIEMPRE_NOCHE), Clima.DESPEJADO).noche, 0.001f)
    }

    @Test fun laLuzCambiaPocoAPoco() {
        // De un paso de veinte minutos al siguiente (que es cada cuánto se vuelve a trazar el cuadro) no hay brincos.
        for (paso in 0 until 72) {
            val a = cielo(paso / 3.0); val b = cielo(((paso + 1) % 72) / 3.0)
            val hora = "${paso / 3.0} h"
            assertTrue("La luz brinca a las $hora", abs(claridad(a.ambiente) - claridad(b.ambiente)) < 0.14f)
            assertTrue("El cielo brinca a las $hora", abs(claridad(a.alto) - claridad(b.alto)) < 0.12f && abs(claridad(a.bajo) - claridad(b.bajo)) < 0.2f)
            assertTrue("Las luces se encienden de golpe a las $hora", abs(a.noche - b.noche) < 0.3f)
        }
    }

    @Test fun elSolCruzaElCuadroYEnElEclipseSeVe() {
        assertTrue("De noche no hay sol", cielo(2.0).solAltura < 0f && cielo(22.0).solAltura < 0f)
        assertTrue("Sale por la derecha y se pone por la izquierda", cielo(7.0).solX > cielo(13.0).solX && cielo(13.0).solX > cielo(19.5).solX)
        assertTrue("A las 7 y a las 19:30 está dentro del cuadro", cielo(7.0).solAltura in 0f..1f && cielo(19.5).solAltura in 0f..1f)
        assertTrue("A mediodía queda por encima del cuadro", cielo(13.0).solAltura > 1f)
        assertTrue("En el eclipse se baja para que se vea", cielo(14.7, eclipse = 1f).solAltura in 0f..1f)
        assertTrue("El eclipse apaga el día", claridad(cielo(14.7, eclipse = 1f).ambiente) < claridad(cielo(14.7).ambiente) - 0.2f)
        assertTrue("Con el cielo cubierto no se ve el disco", cielo(7.0, Clima.LLUVIA).solFuerza < 0.05f)
    }

    // ---------------------------------------------------------------- El momento

    @Test fun elCuadroQuietoSoloCambiaCadaVeinteMinutosOAlCambiarDeTramo() {
        val f = ficha("2027-05-05")
        assertEquals(Momento(f, 9.0).sello, Momento(f, 9.3).sello)
        assertNotEquals(Momento(f, 9.0).sello, Momento(f, 9.34).sello)
        // A las 13:30 Demeas pasa de comer a trabajar, dentro del mismo tercio de hora: el sello cambia igual.
        assertNotEquals(Momento(f, 13.4).sello, Momento(f, 13.5).sello)
        assertNotEquals(Momento(f, 9.0).sello, Momento(ficha("2027-05-06"), 9.0).sello)
        assertEquals(1.0, Momento(f, 25.0).hora, 0.0001)
        assertEquals(23.0, Momento(f, -1.0).hora, 0.0001)
        assertEquals(0.0, Momento(f, 24.0).hora, 0.0001)
        assertEquals(OtrasVidas.Hacer.COMER, Momento(f, 13.0).hacer)
        assertEquals(OtrasVidas.Hacer.DORMIR, Momento(f, 23.0).hacer)
        assertEquals(0f, Momento(f, 9.0).arco("no_existe"), 0f)
        assertEquals(Momento(f, 9.0).semilla("x"), Momento(f, 20.0).semilla("x"))
        assertNotEquals(Momento(f, 9.0).semilla("x"), Momento(f, 9.0).semilla("y"))
    }

    // ---------------------------------------------------------------- Helénico cuenta lo mismo que el cuento

    @Test fun laNaveDeDemeasEstaDondeDiceElCuento() {
        // En su cobertizo hasta la botadura del 12 de abril; después, en el agua.
        assertEquals(Nave.COBERTIZO, nave("2027-02-03", 9.0))
        assertEquals(Nave.COBERTIZO, nave("2027-04-11", 13.0))
        assertEquals(Nave.AMARRADA, nave("2027-04-12", 13.0))
        // En temporada: remando a sus horas de trabajo y amarrada cuando come, cuando está con gente y cuando duerme.
        assertEquals(Nave.REMANDO, nave("2027-05-05", 10.0))
        assertEquals(Nave.AMARRADA, nave("2027-05-05", 13.0))
        assertEquals(Nave.AMARRADA, nave("2027-05-05", 19.0))
        assertEquals(Nave.AMARRADA, nave("2027-05-05", 23.0))
        // El día en que zarpa la flota ya no está, y no vuelve hasta que él vuelve (decisión 18).
        assertNotEquals(Nave.AUSENTE, nave("2027-07-04", 13.0))
        for (hora in listOf(1.0, 7.0, 13.0, 21.0)) {
            assertEquals(Nave.AUSENTE, nave("2027-07-05", hora))
            assertEquals(Nave.AUSENTE, nave("2027-08-03", hora))
            assertEquals(Nave.AUSENTE, nave("2027-09-17", hora))
            assertNotEquals(Nave.AUSENTE, nave("2027-09-18", hora))
        }
        // Desde el 1 de noviembre, otra vez bajo techo.
        assertEquals(Nave.AMARRADA, nave("2027-10-31", 13.0))
        assertEquals(Nave.COBERTIZO, nave("2027-11-01", 13.0))
        // Los otros viajes: Epidauro, las cien naves y el segundo trirreme.
        assertEquals(Nave.AUSENTE, nave("2028-06-06", 13.0)); assertEquals(Nave.AUSENTE, nave("2028-07-09", 13.0)); assertNotEquals(Nave.AUSENTE, nave("2028-07-10", 13.0))
        assertEquals(Nave.AUSENTE, nave("2030-08-22", 13.0)); assertEquals(Nave.AUSENTE, nave("2030-08-31", 13.0)); assertNotEquals(Nave.AUSENTE, nave("2030-09-01", 13.0))
        assertEquals(Nave.AUSENTE, nave("2031-07-15", 13.0)); assertEquals(Nave.AUSENTE, nave("2031-07-16", 13.0)); assertNotEquals(Nave.AUSENTE, nave("2031-07-17", 13.0))
    }

    @Test fun enNingunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            val f = ficha(d.toString())
            for (hora in listOf(3.0, 7.0, 10.0, 13.0, 15.0, 19.0, 21.0)) {
                val m = Momento(f, hora)
                val nave = EscenaHeleno.nave(m)
                assertEquals("$d $hora h: la nave falta si y solo si él está en el mar", f.estado == "mar", nave == Nave.AUSENTE)
                if (f.estado != "mar") assertEquals("$d $hora h: guardada si y solo si lo dice el arco", f.arcos.getValue("nave") < 0.5, nave == Nave.COBERTIZO)
                if (nave == Nave.REMANDO) assertEquals("$d $hora h: solo reman a sus horas de trabajo", OtrasVidas.Hacer.TRABAJO, m.hacer)
                val aFlote = EscenaHeleno.navesAFlote(m)
                assertTrue("$d: $aFlote naves a flote", aFlote in 0..7)
                if (f.arcos.getValue("nave") < 0.5) assertEquals("$d: en invierno las naves están bajo techo", 0, aFlote)
            }
            d = d.plusDays(1)
        }
        // El día en que zarpan cien naves, la dársena se queda con menos que la víspera.
        assertTrue(EscenaHeleno.navesAFlote(momento("2027-07-05", 13.0)) < EscenaHeleno.navesAFlote(momento("2027-07-04", 13.0)))
        assertTrue("El día de la partida la escena lo sabe", momento("2027-07-05", 13.0).con("partida"))
        assertTrue("Y el de la invasión, también, aunque mande otro día especial", momento("2027-07-05", 13.0).con("humo"))
    }

    @Test fun lasAnclasDelGatoCabenEnElCuadro() {
        for (dia in listOf("2027-02-03", "2027-04-23", "2027-05-05", "2027-07-05", "2027-11-22", "2031-09-01")) for (hora in listOf(5.5, 9.0, 13.0, 18.0, 23.0)) {
            val m = momento(dia, hora)
            val anclas = EscenaHeleno.anclas(m)
            assertTrue("$dia $hora h: falta el suelo", anclas.any { it.tipo == TipoAncla.SUELO && it.hasta > it.x })
            anclas.forEach { a -> assertTrue("$dia $hora h: ancla ${a.tipo} fuera del cuadro", a.x in 0f..ANCHO && a.hasta in 0f..ANCHO && a.y in 0f..ALTO) }
            assertEquals("$dia $hora h: el gato solo se arrima al fuego si está encendido", m.hacer == OtrasVidas.Hacer.COMER && m.estado != "mar", anclas.any { it.tipo == TipoAncla.FUEGO })
        }
    }
}
