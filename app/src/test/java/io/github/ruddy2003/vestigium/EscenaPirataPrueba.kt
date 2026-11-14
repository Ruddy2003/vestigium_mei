package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.CayoPirata
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Bomba
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Casco
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaPirata.Vela
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
 * El cuadro de Pirata (tanda S2) cuenta lo mismo que el cuento de Tomás: dónde está el balandro (en el mar, tumbado en
 * el cayo de la carena o fondeado en Nassau), cómo va el casco, cuánto lleva carenado y cómo va la vela salen de la
 * ficha del día, y aquí se comparan con su calendario. Son dos cuadros (a bordo y el cayo) y una variante (Nassau).
 */
class EscenaPirataPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "grumete")!!, hora)

    @Test fun laEscenaEsLaDeTomas() {
        assertEquals(listOf("grumete"), EscenaPirata.vidas)
        assertEquals("grumete", EscenaPirata.vida("2027-05-10"))
    }

    @Test fun elBalandroEstaDondeDiceElCuento() {
        // Cada año, del 10 al 23 de marzo, la carena: el balandro, tumbado en el cayo. El 24 vuelve al agua.
        for (anio in 2027..2031) {
            assertEquals(Lugar.MAR, EscenaPirata.lugar(momento("$anio-03-09")))
            for (dia in listOf("03-10", "03-16", "03-23")) assertEquals("$anio-$dia", Lugar.CAYO, EscenaPirata.lugar(momento("$anio-$dia")))
            assertEquals(Lugar.MAR, EscenaPirata.lugar(momento("$anio-03-24")))
        }
        // Temporada 1: entra en Nassau el 28 de junio y sale de noche el 24 de julio, después de la votación.
        assertEquals(Lugar.MAR, EscenaPirata.lugar(momento("2027-06-27")))
        for (dia in listOf("2027-06-28", "2027-07-10", "2027-07-22", "2027-07-23")) assertEquals(dia, Lugar.NASSAU, EscenaPirata.lugar(momento(dia)))
        assertEquals(Lugar.MAR, EscenaPirata.lugar(momento("2027-07-24")))
        // Los otros años no pasa por Nassau.
        for (anio in 2028..2031) assertEquals(Lugar.MAR, EscenaPirata.lugar(momento("$anio-07-10")))
    }

    @Test fun elCascoSeEnsuciaConLosMesesYLaBombaLoSigue() {
        // Recién carenado, limpio; se va ensuciando; a fin de año y hasta la carena siguiente, sucio.
        assertEquals(Casco.LIMPIO, EscenaPirata.casco(momento("2027-03-25"))); assertEquals(Bomba.DESCANSA, EscenaPirata.bomba(momento("2027-03-25")))
        assertEquals(Casco.LIMPIO, EscenaPirata.casco(momento("2027-05-20")))
        assertEquals(Casco.ENSUCIANDOSE, EscenaPirata.casco(momento("2027-09-01"))); assertEquals(Bomba.TRABAJA, EscenaPirata.bomba(momento("2027-09-01")))
        assertEquals(Casco.SUCIO, EscenaPirata.casco(momento("2027-12-20"))); assertEquals(Bomba.NO_PARA, EscenaPirata.bomba(momento("2027-12-20")))
        assertEquals(Casco.SUCIO, EscenaPirata.casco(momento("2028-02-15")))
        // Con el casco limpio no sale agua por la bomba; ensuciándose, solo a las horas de trabajo; sucio, a todas.
        assertFalse(EscenaPirata.achicando(momento("2027-04-10", 10.0)))
        assertTrue(EscenaPirata.achicando(momento("2027-09-01", 10.0))); assertFalse(EscenaPirata.achicando(momento("2027-09-01", 13.0)))
        assertTrue(EscenaPirata.achicando(momento("2027-12-20", 13.0))); assertTrue(EscenaPirata.achicando(momento("2027-12-20", 2.0)))
    }

    @Test fun laCarenaAvanzaDiaADia() {
        var antes = -1f
        var d = LocalDate.of(2028, 3, 10)
        while (d <= LocalDate.of(2028, 3, 23)) {
            val ahora = EscenaPirata.carenado(momento(d.toString()))
            assertTrue("$d: lo rascado no se vuelve a ensuciar", ahora > antes); antes = ahora; d = d.plusDays(1)
        }
        assertTrue("el primer día apenas se ha empezado", EscenaPirata.carenado(momento("2028-03-10")) < 0.15f)
        assertTrue("el último día está casi listo", EscenaPirata.carenado(momento("2028-03-23")) > 0.85f)
    }

    @Test fun laVelaVaComoPideElDia() {
        // La calma de la temporada 2 (cuatro días desde el 28 de abril): la vela, caída.
        for (dia in listOf("2028-04-28", "2028-05-01")) assertEquals(dia, Vela.CAIDA, EscenaPirata.vela(momento(dia)))
        // El huracán (tres días desde el 18 de septiembre) y la tormenta de la gavia: con rizos.
        for (dia in listOf("2028-09-18", "2028-09-20", "2029-10-05")) assertEquals(dia, Vela.RIZADA, EscenaPirata.vela(momento(dia)))
        // Fondeado en Nassau: aferrada.
        assertEquals(Vela.AFERRADA, EscenaPirata.vela(momento("2027-07-05")))
        // Y el día del abordaje hay presa a la vista, pero no en el cayo ni en puerto.
        assertTrue(EscenaPirata.abordaje(momento("2027-01-18"))); assertTrue(EscenaPirata.abordaje(momento("2027-10-10")))
        assertFalse(EscenaPirata.abordaje(momento("2027-07-05"))); assertFalse(EscenaPirata.abordaje(momento("2027-03-15")))
    }

    @Test fun elHorizonteCambiaConElViajeYElMismoDiaSaleSiempreIgual() {
        // El mismo día da siempre la misma vista, a cualquier hora y con cualquier tiempo.
        for (dia in listOf("2027-05-06", "2028-11-02", "2030-08-15", "2031-01-01")) {
            val f = FichaDelDia.de(dia, "grumete")!!
            val vista = EscenaPirata.horizonte(Momento(f, 13.0)); val velas = EscenaPirata.velasALaVista(Momento(f, 13.0))
            for (hora in listOf(2.0, 7.0, 21.0)) for (clima in listOf(Clima.DESPEJADO, Clima.LLUVIA, Clima.TORMENTA)) {
                assertEquals("$dia: la vista no cambia con la hora ni con el tiempo", vista, EscenaPirata.horizonte(Momento(f.copy(clima = clima), hora)))
                assertEquals("$dia: las velas tampoco", velas, EscenaPirata.velasALaVista(Momento(f.copy(clima = clima), hora)))
            }
            assertEquals(vista, EscenaPirata.horizonte(momento(dia)))
        }
        // Los tres días de un tramo comparten aguas.
        var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val hoy = momento(d.toString()); val ayer = momento(d.minusDays(1).toString())
            if (EscenaPirata.tramo(hoy) == EscenaPirata.tramo(ayer)) assertEquals("$d: mismo tramo, misma vista", EscenaPirata.horizonte(ayer), EscenaPirata.horizonte(hoy))
            d = d.plusDays(1)
        }
        // En cada año, navegando, salen todas las vistas y hay días sin velas, con una y con dos.
        for (anio in 2026..2032) {
            val vistas = HashSet<EscenaPirata.Horizonte>(); val velas = HashSet<Int>()
            d = LocalDate.of(anio, 1, 1)
            while (d.year == anio) { val m = momento(d.toString()); if (EscenaPirata.lugar(m) == Lugar.MAR) { vistas += EscenaPirata.horizonte(m); velas += EscenaPirata.velasALaVista(m) }; d = d.plusDays(1) }
            assertEquals("$anio: salen todas las vistas", EscenaPirata.Horizonte.entries.toSet(), vistas)
            assertEquals("$anio: ninguna vela, una y dos", setOf(0, 1, 2), velas)
        }
        // Y el viaje se nota: en cualquier mes se ven al menos tres horizontes distintos.
        d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val vistas = HashSet<EscenaPirata.Horizonte>(); val mes = d.monthValue; var enElMar = 0
            while (d.monthValue == mes) { val m = momento(d.toString()); if (EscenaPirata.lugar(m) == Lugar.MAR) { vistas += EscenaPirata.horizonte(m); enElMar++ }; d = d.plusDays(1) }
            if (enElMar >= 15) assertTrue("${d.minusDays(1)}: en el mes solo se vieron ${vistas.size} horizontes", vistas.size >= 3)
        }
    }

    @Test fun elTramoDelDiaSeNotaEnLaCubierta() {
        // Su día va por guardias: baldea al alba, come a las 8 y a las 12, y de noche hay farol y dados.
        assertTrue(EscenaPirata.baldeando(momento("2027-05-06", 7.0))); assertFalse(EscenaPirata.baldeando(momento("2027-05-06", 10.0)))
        assertTrue(EscenaPirata.comiendo(momento("2027-05-06", 12.4))); assertFalse(EscenaPirata.comiendo(momento("2027-05-06", 13.0)))
        assertTrue(EscenaPirata.farol(momento("2027-05-06", 21.0))); assertFalse(EscenaPirata.farol(momento("2027-05-06", 13.0))); assertFalse(EscenaPirata.farol(momento("2027-05-06", 2.0)))
        // Hay fuego para el gato a la hora de comer, pero no con tormenta, que no se enciende el fogón.
        assertTrue(EscenaPirata.anclas(momento("2027-05-06", 12.4)).any { it.tipo == TipoAncla.FUEGO })
        assertFalse(EscenaPirata.anclas(momento("2028-09-19", 12.4)).any { it.tipo == TipoAncla.FUEGO })
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        var enElCayo = 0; var enNassau = 0
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "grumete")!!
            for (hora in listOf(2.0, 5.0, 7.0, 8.4, 10.0, 12.4, 13.0, 17.0, 20.0, 21.0, 23.0)) {
                val m = Momento(f, hora)
                val lugar = EscenaPirata.lugar(m)
                assertEquals("$d: en el cayo si y solo si el estado es cayo", f.estado == "cayo", lugar == Lugar.CAYO)
                assertEquals("$d: en Nassau si y solo si el estado es nassau", f.estado == "nassau", lugar == Lugar.NASSAU)
                val arco = (f.arcos["casco"] ?: 0.0).toFloat()
                assertEquals("$d: el casco sigue al arco", if (arco < 0.3f) Casco.LIMPIO else if (arco < 0.7f) Casco.ENSUCIANDOSE else Casco.SUCIO, EscenaPirata.casco(m))
                assertTrue("$d: lo carenado, de 0 a 1", EscenaPirata.carenado(m) in 0f..1f)
                if (lugar == Lugar.CAYO) assertFalse("$d: en el cayo no se achica", EscenaPirata.achicando(m))
                val tormenta = m.con("tormenta") || f.clima == Clima.TORMENTA
                val vela = EscenaPirata.vela(m)
                assertEquals("$d: aferrada si y solo si está en Nassau", lugar == Lugar.NASSAU, vela == Vela.AFERRADA)
                if (lugar == Lugar.MAR) assertEquals("$d: con rizos si y solo si hay tormenta", tormenta, vela == Vela.RIZADA)
                if (lugar == Lugar.MAR && !tormenta) assertEquals("$d: caída si y solo si hay calma", m.con("calma"), vela == Vela.CAIDA)
                if (EscenaPirata.abordaje(m)) assertEquals("$d: solo hay presa en el mar", Lugar.MAR, lugar)
                // Las anclas del gato caben en el cuadro, y nunca faltan el suelo, el mirador, el objeto y el abrigo, en ninguno de los dos cuadros.
                val anclas = EscenaPirata.anclas(m)
                for (a in anclas) assertTrue("$d $hora h: ancla ${a.tipo} fuera", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }
                assertTrue("$d: el suelo del gato mide al menos 110", suelo.hasta - suelo.x >= 110f)
                for (t in listOf(TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: $t dentro del suelo", anclas.first { it.tipo == t }.x in suelo.x..suelo.hasta)
                for (x in EscenaPirata.estorbos(m)) assertTrue("$d: estorbo fuera del suelo", x in suelo.x..suelo.hasta)
                val fuego = if (lugar == Lugar.CAYO) CayoPirata.fuegoEncendido(m) && f.clima != Clima.TORMENTA else m.hacer == Hacer.COMER && !tormenta
                assertEquals("$d $hora h: hay fuego para el gato si y solo si está encendido", fuego, anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d: hay adorno si y solo si es fiesta a bordo", m.con("fiesta") && lugar != Lugar.CAYO, anclas.any { it.tipo == TipoAncla.ADORNO })
                assertTrue("$d: siempre hay algo que se mueve", EscenaPirata.seMueve(m))
            }
            if (f.estado == "cayo") enElCayo++; if (f.estado == "nassau") enNassau++
            d = d.plusDays(1)
        }
        assertEquals("catorce días de carena cada año, de 2026 a 2032", 14 * 7, enElCayo)
        assertTrue("Nassau solo en la temporada 1 (y en su avance de 2026)", enNassau == 26 || enNassau == 52)
    }
}
