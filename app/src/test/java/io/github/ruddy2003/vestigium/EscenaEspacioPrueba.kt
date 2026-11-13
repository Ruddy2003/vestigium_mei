package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio
import io.github.ruddy2003.vestigium.ui.escenas.EscenaEspacio.Manda
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import io.github.ruddy2003.vestigium.vidas.Franja
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Espacio (tanda S3) cuenta lo mismo que el cuento de la astronauta: la parada del reactor, las semanas
 * sin peso, cómo va el cultivo, si el traje de salida está en su armario y qué luz hay a bordo salen de la ficha del
 * día, y aquí se comparan con su calendario. Es el primer interior con dos luces: no hay sol, y la hora se nota en
 * qué lámpara manda.
 */
class EscenaEspacioPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "astronauta")!!, hora)

    @Test fun laEscenaEsLaDeLaAstronauta() {
        assertEquals(listOf("astronauta"), EscenaEspacio.vidas)
        assertEquals("astronauta", EscenaEspacio.vida("2027-05-10"))
    }

    @Test fun laLuzDeABordoHaceDeSol() {
        // Por la mañana manda la lámpara de la izquierda; a mediodía, la del centro; por la tarde, la de la derecha; de noche, las de guardia.
        val dia = "2027-05-06"
        assertEquals(Manda.IZQUIERDA, EscenaEspacio.manda(momento(dia, 7.0)))
        assertEquals(Manda.CENTRO, EscenaEspacio.manda(momento(dia, 13.0)))
        assertEquals(Manda.DERECHA, EscenaEspacio.manda(momento(dia, 18.0)))
        assertEquals(Manda.DERECHA, EscenaEspacio.manda(momento(dia, 21.0)))
        for (h in listOf(22.0, 23.0, 1.0, 4.0)) assertEquals("$h", Manda.GUARDIA, EscenaEspacio.manda(momento(dia, h)))
        // Las tres horas de la hoja dan tres luces distintas: cálida de la izquierda, blanca de arriba y el último naranja con las de guardia.
        val siete = EscenaEspacio.programa(momento(dia, 7.0)); val una = EscenaEspacio.programa(momento(dia, 13.0)); val nueve = EscenaEspacio.programa(momento(dia, 21.0))
        assertTrue(siete.izq > siete.centro && siete.centro > siete.der)
        assertTrue(una.centro > 0.9f && una.nivel > 0.95f)
        assertTrue(nueve.naranja > 0.95f && nueve.guardia > 0.5f && nueve.der > 0.4f && nueve.nivel < 0.5f)
        // La mañana viene de la izquierda; el mediodía, de arriba; la tarde y el último naranja, de la derecha; la noche, de ningún lado.
        assertTrue(EscenaEspacio.deLado(momento(dia, 7.0)) < -0.6f); assertTrue(EscenaEspacio.deLado(momento(dia, 10.0)) < -0.5f)
        assertEquals(0f, EscenaEspacio.deLado(momento(dia, 13.0)), 0.05f)
        assertTrue(EscenaEspacio.deLado(momento(dia, 18.0)) > 0.6f); assertTrue(EscenaEspacio.deLado(momento(dia, 21.0)) > 0.9f)
        assertEquals(0f, EscenaEspacio.deLado(momento(dia, 23.0)), 0.001f); assertEquals(0f, EscenaEspacio.deLado(momento("2027-10-08", 10.0)), 0.001f)
        // De 22:00 a 5:00, solo las de guardia; y la luz sube y baja poco a poco, sin saltos.
        for (h in listOf(22.0, 0.0, 3.0, 4.67)) { val g = EscenaEspacio.programa(momento(dia, h)); assertEquals(0f, g.nivel, 0.001f); assertEquals(1f, g.guardia, 0.001f) }
        for (i in 0 until 72) {
            val h = i / 3.0 + 0.01; val despues = ((i + 1) % 72) / 3.0 + 0.01
            val a = EscenaEspacio.programa(momento(dia, h)); val b = EscenaEspacio.programa(momento(dia, despues))
            assertTrue("de $h a los veinte minutos siguientes, la luz salta", abs(a.nivel - b.nivel) <= 0.3f && abs(a.guardia - b.guardia) <= 0.36f)
        }
    }

    @Test fun enLaParadaHayMediaLuzYCalefactor() {
        for (anio in 2027..2031) {
            assertFalse(EscenaEspacio.parada(momento("$anio-10-04")))
            for (dia in listOf("10-05", "10-09", "10-14")) {
                val m = momento("$anio-$dia", 10.0)
                assertTrue("$anio-$dia", EscenaEspacio.parada(m))
                val g = EscenaEspacio.programa(m)
                assertEquals(0f, g.izq + g.centro + g.der, 0.001f); assertTrue(EscenaEspacio.emergencia(m) > 0.3f); assertTrue(EscenaEspacio.pantallas(m) <= 0.1f)
                assertEquals(Manda.GUARDIA, EscenaEspacio.manda(m)); assertTrue(EscenaEspacio.luzDeCultivo(m) <= 0.2f)
                assertTrue(EscenaEspacio.calefactor(m)); assertTrue(EscenaEspacio.anclas(m).any { it.tipo == TipoAncla.FUEGO })
                // Mientras duerme, el calefactor está apagado.
                assertFalse(EscenaEspacio.calefactor(momento("$anio-$dia", 2.0))); assertFalse(EscenaEspacio.anclas(momento("$anio-$dia", 2.0)).any { it.tipo == TipoAncla.FUEGO })
            }
            assertFalse(EscenaEspacio.parada(momento("$anio-10-16")))
            assertFalse(EscenaEspacio.anclas(momento("$anio-10-16")).any { it.tipo == TipoAncla.FUEGO })
        }
        // El día que se para el reactor suena la alarma.
        assertTrue(EscenaEspacio.alarma(momento("2028-10-05")))
    }

    @Test fun sinPesoElAnilloNoGira() {
        // Temporada 3: del 10 de mayo al 31, con el anillo parado. El 1 de junio vuelve a girar.
        assertTrue(EscenaEspacio.giraElAnillo(momento("2029-05-09")))
        for (dia in listOf("2029-05-10", "2029-05-20", "2029-05-31")) { assertTrue(dia, EscenaEspacio.sinPeso(momento(dia))); assertFalse(dia, EscenaEspacio.giraElAnillo(momento(dia))) }
        assertTrue(EscenaEspacio.giraElAnillo(momento("2029-06-01")))
        for (anio in listOf(2027, 2028, 2030, 2031)) assertFalse(EscenaEspacio.sinPeso(momento("$anio-05-20")))
        // Sin peso no hay lámpara de mesa encendida ni vaho de comida: nada se queda en la mesa.
        assertFalse(EscenaEspacio.flexo(momento("2029-05-20", 21.4)))
    }

    @Test fun elCultivoCreceYSeCosecha() {
        // Crece todo el año hasta el 19 de agosto; el 20 se cosecha y vuelve a empezar.
        assertTrue(EscenaEspacio.cultivo(momento("2027-02-01")) < EscenaEspacio.cultivo(momento("2027-06-01")))
        assertTrue(EscenaEspacio.cultivo(momento("2027-06-01")) < EscenaEspacio.cultivo(momento("2027-08-15")))
        assertTrue(EscenaEspacio.trigoMaduro(momento("2027-08-15"))); assertFalse(EscenaEspacio.trigoMaduro(momento("2027-03-01")))
        for (anio in 2027..2031) {
            assertTrue(EscenaEspacio.cultivo(momento("$anio-08-19")) > 0.95f)
            assertTrue(EscenaEspacio.cultivo(momento("$anio-08-20")) < 0.2f); assertFalse(EscenaEspacio.trigoMaduro(momento("$anio-08-21")))
            for (dia in listOf("08-20", "08-21", "08-22", "08-23")) assertTrue("$anio-$dia", EscenaEspacio.cosecha(momento("$anio-$dia")))
            assertFalse(EscenaEspacio.cosecha(momento("$anio-08-24")))
        }
        // La nave avanza: cada año, un poco más del camino.
        assertTrue(EscenaEspacio.viaje(momento("2027-01-02")) < EscenaEspacio.viaje(momento("2031-12-30")))
        assertTrue(EscenaEspacio.viaje(momento("2027-01-02")) in 0.3f..0.36f); assertTrue(EscenaEspacio.viaje(momento("2031-12-30")) in 0.43f..0.46f)
    }

    @Test fun losDiasQueSeSaleAlCascoElTrajeNoEsta() {
        for (dia in listOf("2027-05-20", "2029-08-12", "2030-09-25")) { assertTrue(dia, EscenaEspacio.fuera(momento(dia))); assertFalse(dia, EscenaEspacio.trajeEnSuSitio(momento(dia))) }
        for (dia in listOf("2027-05-19", "2027-05-21", "2029-08-13")) assertTrue(dia, EscenaEspacio.trajeEnSuSitio(momento(dia)))
    }

    @Test fun lasFiestasLaAlarmaYElLuto() {
        // La fiesta de las luces: las lamparitas. El aniversario de la partida: al bajar la luz, se apaga todo.
        for (anio in 2027..2031) {
            assertTrue(EscenaEspacio.lamparitas(momento("$anio-12-21"))); assertFalse(EscenaEspacio.lamparitas(momento("$anio-12-22")))
            assertFalse(EscenaEspacio.aOscuras(momento("$anio-04-22", 13.0)))
            val m = momento("$anio-04-22", 22.0)
            assertTrue(EscenaEspacio.aOscuras(m)); assertEquals(0f, EscenaEspacio.pantallas(m), 0.001f); assertEquals(0f, EscenaEspacio.luzDeCultivo(m), 0.001f); assertTrue(EscenaEspacio.programa(m).guardia <= 0.25f)
            assertFalse(EscenaEspacio.aOscuras(momento("$anio-04-23", 22.0)))
        }
        for (dia in listOf("2027-02-25", "2027-07-20", "2028-06-18", "2028-07-28", "2028-07-30", "2029-05-10")) assertTrue(dia, EscenaEspacio.alarma(momento(dia)))
        assertFalse(EscenaEspacio.alarma(momento("2027-02-26")))
        assertTrue(EscenaEspacio.luto(momento("2028-03-16"))); assertFalse(EscenaEspacio.luto(momento("2028-03-17")))
        for (dia in listOf("2027-01-01", "2027-07-01", "2027-11-28", "2029-02-20", "2030-07-15", "2031-04-10")) {
            assertTrue(dia, EscenaEspacio.fiesta(momento(dia))); assertTrue(dia, EscenaEspacio.anclas(momento(dia)).any { it.tipo == TipoAncla.ADORNO })
        }
    }

    @Test fun elTramoDelDiaSeNotaEnElModulo() {
        val dia = "2027-05-06"
        // Trabaja de 7 a 12 y de 14:30 a 18: las pantallas, del todo. Duerme de 22:30 a 6: casi apagadas.
        for (h in listOf(7.0, 10.0, 15.0, 17.6)) { assertTrue("$h", EscenaEspacio.trabajando(momento(dia, h))); assertEquals(1f, EscenaEspacio.pantallas(momento(dia, h)), 0.001f) }
        for (h in listOf(23.0, 2.0, 5.4)) { assertTrue("$h", EscenaEspacio.durmiendo(momento(dia, h))); assertEquals(0.3f, EscenaEspacio.pantallas(momento(dia, h)), 0.001f) }
        assertTrue(EscenaEspacio.comiendo(momento(dia, 12.4))); assertTrue(EscenaEspacio.comiendo(momento(dia, 18.4))); assertFalse(EscenaEspacio.comiendo(momento(dia, 13.0)))
        assertTrue(EscenaEspacio.ejercicio(momento(dia, 13.4))); assertFalse(EscenaEspacio.enLaVentana(momento(dia, 13.4)))
        assertTrue(EscenaEspacio.conGente(momento(dia, 19.4)))
        // Su rato de la ventana, de 21:00 a 22:30: la lámpara de la mesa, encendida.
        assertTrue(EscenaEspacio.enLaVentana(momento(dia, 21.0))); assertTrue(EscenaEspacio.flexo(momento(dia, 21.0))); assertFalse(EscenaEspacio.flexo(momento(dia, 13.0))); assertFalse(EscenaEspacio.flexo(momento(dia, 23.0)))
        // Las lámparas del cultivo descansan de noche.
        assertTrue(EscenaEspacio.luzDeCultivo(momento(dia, 13.0)) > 0.9f); assertTrue(EscenaEspacio.luzDeCultivo(momento(dia, 1.0)) < 0.35f)
    }

    @Test fun ningunDiaElCuadroContradiceALaFicha() {
        var d = LocalDate.of(2026, 1, 1)
        var enParada = 0; var sinPeso = 0
        while (d.year <= 2032) {
            val f = FichaDelDia.de(d.toString(), "astronauta")!!
            for (hora in listOf(2.0, 5.0, 5.34, 7.0, 10.0, 12.4, 13.0, 17.0, 19.4, 20.67, 21.0, 21.67, 23.0)) {
                val m = Momento(f, hora)
                assertEquals("$d: parada si y solo si el estado es parada", f.estado == "parada", EscenaEspacio.parada(m))
                assertEquals("$d: sin peso si y solo si el estado es ingravidez", f.estado == "ingravidez", EscenaEspacio.sinPeso(m))
                assertEquals("$d: el anillo gira salvo sin peso", f.estado != "ingravidez", EscenaEspacio.giraElAnillo(m))
                assertEquals("$d: el cultivo sigue al arco", (f.arcos["granja"] ?: 0.0).toFloat().coerceIn(0f, 1f), EscenaEspacio.cultivo(m), 0.0001f)
                assertEquals("$d: el traje no está si y solo si se sale al casco", m.con("fuera"), !EscenaEspacio.trajeEnSuSitio(m))
                // La luz: todo entre 0 y 1; en la parada no hay lámparas de techo; de noche (luz tenue) nunca hay más que el último naranja.
                val g = EscenaEspacio.programa(m)
                for (x in listOf(g.izq, g.centro, g.der, g.naranja, g.guardia, g.nivel, EscenaEspacio.pantallas(m), EscenaEspacio.luzDeCultivo(m), EscenaEspacio.emergencia(m))) assertTrue("$d $hora h: luz fuera de 0 a 1", x in 0f..1f)
                if (f.estado == "parada") assertEquals("$d: en la parada no lucen las lámparas del techo", 0f, g.nivel, 0.001f)
                if (m.luz.franja == Franja.TENUE) assertTrue("$d $hora h: con la luz tenue no puede haber día", g.nivel <= 0.45f)
                if (m.luz.franja == Franja.ENCENDIDO && hora >= 6.0 && hora <= 20.0 && f.estado != "parada") assertTrue("$d $hora h: de día hay luz", g.nivel >= 0.7f)
                val focos = EscenaEspacio.focos(m)
                assertTrue("$d: las tres lámparas van primero", focos.size >= 3)
                for (q in focos) assertTrue("$d $hora h: foco raro", q.fuerza >= 0f && q.fuerza <= 1.5f && q.alcance > 0f && q.p.x in -10f..ANCHO + 10f && q.p.y in -10f..ALTO)
                val l = EscenaEspacio.luces(m)
                assertTrue("$d $hora h: noche de 0 a 1", l.noche in 0f..1f)
                // El calefactor, solo en la parada y con ella despierta; la lámpara de la mesa, nunca sin peso.
                assertEquals("$d $hora h: calefactor", f.estado == "parada" && m.hacer != Hacer.DORMIR, EscenaEspacio.calefactor(m))
                if (f.estado == "ingravidez") assertFalse("$d: sin peso no hay lámpara de mesa", EscenaEspacio.flexo(m))
                // Las anclas del gato caben en el cuadro y nunca faltan el suelo, el mirador, el objeto y el abrigo.
                val anclas = EscenaEspacio.anclas(m)
                for (a in anclas) assertTrue("$d $hora h: ancla ${a.tipo} fuera", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO)
                for (t in listOf(TipoAncla.SUELO, TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: falta $t", anclas.any { it.tipo == t })
                val suelo = anclas.first { it.tipo == TipoAncla.SUELO }
                assertTrue("$d: el suelo del gato mide al menos 110", suelo.hasta - suelo.x >= 110f)
                for (t in listOf(TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertTrue("$d: $t dentro del suelo", anclas.first { it.tipo == t }.x in suelo.x..suelo.hasta)
                assertEquals("$d $hora h: hay fuego para el gato si y solo si el calefactor está encendido", EscenaEspacio.calefactor(m), anclas.any { it.tipo == TipoAncla.FUEGO })
                assertEquals("$d: hay adorno si y solo si es fiesta", m.con("fiesta"), anclas.any { it.tipo == TipoAncla.ADORNO })
                assertTrue("$d: siempre hay algo que se mueve", EscenaEspacio.seMueve(m))
                val luz = EscenaEspacio.luzDelGato(m, androidx.compose.ui.geometry.Offset(180f, 281f))
                assertTrue("$d $hora h: la luz del gato", luz.noche in 0f..1f)
            }
            if (f.estado == "parada") enParada++; if (f.estado == "ingravidez") sinPeso++
            d = d.plusDays(1)
        }
        println("dias en parada: $enParada; dias sin peso: $sinPeso")
        assertEquals("diez días de parada cada año, de 2026 a 2032", 10 * 7, enParada)
        assertEquals("veintidós días sin peso, solo en la temporada 3", 22, sinPeso)
    }
}
