package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG7
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs
import kotlin.math.log10

/**
 * Los calendarios del grupo 7 (tanda A7): la astronauta, la recolectora de semillas, el funcionario, la residente y el
 * archivista, todos de ficción. Las pruebas generales (forma, certeza, continuidad) están en [AnioPilotoPrueba] y corren
 * solas sobre todo lo registrado en [Calendarios]; aquí va lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG7Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun estadoEntre(c: Calendario, clave: String, desde: String, hasta: String, despues: Boolean = true) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("${c.vida}: el día antes de $desde", ficha(c, a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("${c.vida}, $d", clave, ficha(c, d.toString()).estado); d = d.plusDays(1) }
        if (despues) assertTrue("${c.vida}: el día después de $hasta", ficha(c, b.plusDays(1).toString()).estado != clave)
    }
    /** Cuántos días del año [anio] está el personaje en el estado [clave]. */
    private fun diasEn(c: Calendario, clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { ficha(c, it.toString()).estado == clave }

    @Test fun estanLosCincoTodosDeFiccionYCadaUnoConSuTema() {
        assertEquals(listOf("astronauta", "semillera", "funcionario", "residente", "archivista"), AnioG7.TODOS.map { it.vida })
        val temas = mapOf("astronauta" to "espacio", "semillera" to "ruinas", "funcionario" to "distopia", "residente" to "refugio", "archivista" to "arcano")
        AnioG7.TODOS.forEach { c ->
            val q = OtrasVidas.porId(c.vida)!!
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], q.tema)
            assertTrue("${c.vida}: es ficción", q.ficticia)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun laAstronautaVaSoloDeIdaAAlfaCentauri() {
        val c = AnioG7.ASTRONAUTA; val q = OtrasVidas.porId(c.vida)!!
        // Su vida nueva: de ficción, con números inventados y sin la estación espacial.
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.INVENTADO })
        assertTrue("Alfa Centauri" in q.lugar); assertTrue(q.historia.none { "Estación Espacial" in it })
        assertEquals("año 15 del viaje", ficha(c, "2027-03-03").cuando); assertEquals("año 19 del viaje", ficha(c, "2031-03-03").cuando)
        // En la nave no hay cielo ni estaciones; la luz se enciende y se baja.
        val f = ficha(c, "2028-06-06")
        assertEquals(Estacion.NINGUNA, f.estacion); assertEquals(Clima.SIN_CLIMA, f.clima)
        assertEquals(Franja.ENCENDIDO, f.luz(12.0).franja); assertEquals(Franja.TENUE, f.luz(23.0).franja)
        (2027..2031).forEach { y -> estadoEntre(c, "parada", "$y-10-05", "$y-10-14") }
        estadoEntre(c, "ingravidez", "2029-05-10", "2029-05-31")
        (2027..2031).forEach { y -> assertEquals(1.0, ficha(c, "$y-08-19").arcos.getValue("granja"), 0.0); assertTrue(ficha(c, "$y-08-20").arcos.getValue("granja") < 0.2) }
        // El viaje: el año 15 empieza a 1.5 años luz (una décima de la velocidad de la luz) y la parte hecha solo crece.
        val ly = 4.37
        assertEquals(1.5, ficha(c, "2027-01-01").arcos.getValue("viaje") * ly, 0.05)
        assertEquals(2.0, ficha(c, "2031-12-31").arcos.getValue("viaje") * ly, 0.05)
        // La estrella de delante supera al Sol cuando su luz llega con la misma fuerza: con las magnitudes absolutas del Sol
        // (+4.83) y de Alfa Centauri A y B (+4.38 y +5.71), eso pasa a unos 1.82 años luz de la Tierra.
        val alfa = -2.5 * log10(Math.pow(10.0, -0.4 * 4.38) + Math.pow(10.0, -0.4 * 5.71))
        val cruce = ly / (1 + Math.pow(10.0, (4.83 - alfa) / 5))
        assertEquals(1.82, cruce, 0.01)
        val dia = "2030-${fechas(c, "brillante")[3]}"
        assertTrue("El día de la estrella de delante cae a ${ficha(c, dia).arcos.getValue("viaje") * ly} años luz", abs(ficha(c, dia).arcos.getValue("viaje") * ly - cruce) < 0.015)
        assertEquals(listOf(null, null, null, "07-15", null), fechas(c, "brillante"))
    }

    @Test fun laRecolectoraGuardaSemillasYViajaDosVeces() {
        val c = AnioG7.SEMILLERA
        assertEquals("año 40 tras el silencio", ficha(c, "2027-03-03").cuando); assertEquals("año 44 tras el silencio", ficha(c, "2031-03-03").cuando)
        (2027..2031).forEach { y -> assertEquals(1.0, ficha(c, "$y-09-09").arcos.getValue("huerto"), 0.0); assertTrue(ficha(c, "$y-09-10").arcos.getValue("huerto") < 0.2) }
        estadoEntre(c, "expedicion", "2028-07-01", "2028-07-21")
        estadoEntre(c, "expedicion", "2031-05-25", "2031-06-14")
        // Cuando la temporada 5 se repite, el viaje a la costa se mueve entero: siempre son tres semanas.
        (2032..2036).forEach { y -> assertEquals("$y", 21, diasEn(c, "expedicion", y)) }
        assertEquals(0.0, ficha(c, "2029-04-01").arcos.getValue("puente"), 0.0); assertEquals(1.0, ficha(c, "2030-06-20").arcos.getValue("puente"), 0.0)
        assertTrue(ficha(c, "2031-12-31").arcos.getValue("bosque") > ficha(c, "2027-01-01").arcos.getValue("bosque"))
    }

    @Test fun elFuncionarioPasaCampanasYVisitaASuHermana() {
        val c = AnioG7.FUNCIONARIO
        estadoEntre(c, "campana", "2028-04-01", "2028-04-21")
        estadoEntre(c, "campana", "2030-03-20", "2030-04-09")
        estadoEntre(c, "provincia", "2030-06-28", "2030-06-29")
        assertEquals(0, diasEn(c, "campana", 2027)); assertEquals(0, diasEn(c, "campana", 2031))
        // La ración baja y las cámaras suben.
        assertTrue(ficha(c, "2031-12-31").arcos.getValue("racion") < ficha(c, "2027-01-01").arcos.getValue("racion"))
        assertTrue(ficha(c, "2031-12-31").arcos.getValue("camaras") > ficha(c, "2027-01-01").arcos.getValue("camaras"))
    }

    @Test fun laResidenteVeElCieloAlFinal() {
        val c = AnioG7.RESIDENTE
        assertEquals(Clima.SIN_CLIMA, ficha(c, "2027-07-07").clima)
        estadoEntre(c, "racionamiento", "2028-03-15", "2028-04-20")
        estadoEntre(c, "cuarentena", "2029-02-01", "2029-02-25")
        estadoEntre(c, "abierto", "2031-09-20", "2031-12-31", despues = false)
        // La cisterna se vacía de golpe al agrietarse y se recupera despacio.
        assertTrue(ficha(c, "2028-03-14").arcos.getValue("reserva") < 0.3); assertTrue(ficha(c, "2028-03-13").arcos.getValue("reserva") > 0.7)
        // El día que llueve, llueve: es el primer clima de verdad que ve.
        assertEquals(Clima.LLUVIA, ficha(c, "2031-10-15").clima)
        assertEquals("cielo", ficha(c, "2031-09-20").escena)
    }

    @Test fun elArchivistaTerminaElCatalogo() {
        val c = AnioG7.ARCHIVISTA
        estadoEntre(c, "fiebre", "2029-03-01", "2029-03-24")
        estadoEntre(c, "faro", "2030-01-20", "2030-02-09")
        assertEquals(0.0, ficha(c, "2027-09-13").arcos.getValue("signo"), 0.0); assertTrue(ficha(c, "2031-12-31").arcos.getValue("signo") > 0.8)
        // El archivo no avanza mientras tiene fiebre y se termina con el catálogo.
        assertEquals(ficha(c, "2029-03-01").arcos.getValue("archivo"), ficha(c, "2029-03-24").arcos.getValue("archivo"), 1e-9)
        assertEquals(1.0, ficha(c, "2031-10-20").arcos.getValue("archivo"), 0.0)
        assertEquals("10-20", fechas(c, "catalogo")[4])
    }
}
