package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG8
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los calendarios del grupo 8 (tanda A8): la escriba, la doctora, Ruth y Brenna, todos de ficción. Las pruebas generales
 * (forma, certeza, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en [Calendarios];
 * aquí va lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG8Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun estadoEntre(c: Calendario, clave: String, desde: String, hasta: String, despues: Boolean = true) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("${c.vida}: el día antes de $desde", ficha(c, a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("${c.vida}, $d", clave, ficha(c, d.toString()).estado); d = d.plusDays(1) }
        if (despues) assertTrue("${c.vida}: el día después de $hasta", ficha(c, b.plusDays(1).toString()).estado != clave)
    }
    private fun diasEn(c: Calendario, clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { ficha(c, it.toString()).estado == clave }

    @Test fun estanLosCuatroTodosDeFiccionYCadaUnoConSuTema() {
        assertEquals(listOf("escriba", "cientifica", "colona", "guia_caminos"), AnioG8.TODOS.map { it.vida })
        val temas = mapOf("escriba" to "orden", "cientifica" to "laboratorio", "colona" to "colonos", "guia_caminos" to "antiguas")
        AnioG8.TODOS.forEach { c ->
            val q = OtrasVidas.porId(c.vida)!!
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], q.tema)
            assertTrue("${c.vida}: es ficción", q.ficticia)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun laEscribaVaALaBovedaSeEncierraConElNucleoYCaeHerida() {
        val c = AnioG8.ESCRIBA
        estadoEntre(c, "expedicion", "2028-04-26", "2028-06-13")
        estadoEntre(c, "encierro", "2029-02-15", "2029-03-13")
        estadoEntre(c, "herida", "2030-04-28", "2030-06-01")
        // El año del viaje, el cierre de los postigos la pilla lejos: ese día lleva otro id.
        assertEquals(listOf("05-20", null, "05-20", "05-20", "05-20"), fechas(c, "postigos")); assertEquals("05-20", fechas(c, "postigos_lejos")[1])
        // La Garza cae en la tormenta de la temporada 3 y vuelve a volar en la 4; el núcleo se lee entero en la 5.
        assertEquals(1.0, ficha(c, "2029-07-29").arcos.getValue("garza"), 0.0); assertTrue(ficha(c, "2029-07-30").arcos.getValue("garza") < 0.2)
        assertEquals(1.0, ficha(c, "2030-09-10").arcos.getValue("garza"), 0.0)
        assertEquals(0.0, ficha(c, "2028-11-05").arcos.getValue("codice"), 0.0); assertEquals(1.0, ficha(c, "2031-08-28").arcos.getValue("codice"), 0.0)
        // La sequía: la cisterna casi toca fondo en la temporada 5.
        assertTrue(ficha(c, "2031-07-11").arcos.getValue("cisterna") < ficha(c, "2030-07-11").arcos.getValue("cisterna"))
    }

    @Test fun laDoctoraViveBajoTierraYSubeAlFinal() {
        val c = AnioG8.CIENTIFICA
        val f = ficha(c, "2028-06-06")
        assertEquals(Estacion.NINGUNA, f.estacion); assertEquals(Clima.SIN_CLIMA, f.clima)
        assertEquals(Franja.ENCENDIDO, f.luz(12.0).franja); assertEquals(Franja.TENUE, f.luz(23.0).franja)
        estadoEntre(c, "cuarentena", "2028-03-08", "2028-04-04")
        estadoEntre(c, "experimento", "2029-05-03", "2029-05-23")
        estadoEntre(c, "cierre", "2030-09-28", "2030-10-11")
        estadoEntre(c, "arriba", "2031-04-26", "2031-05-23")
        // Arriba, por fin, el sol y la lluvia de verdad.
        assertEquals(Clima.DESPEJADO, ficha(c, "2031-04-27").clima); assertEquals(Clima.LLUVIA, ficha(c, "2031-05-13").clima)
        // Cuando la temporada 5 se repite, la subida se mueve entera: siempre son cuatro semanas.
        (2032..2036).forEach { y -> assertEquals("$y", 28, diasEn(c, "arriba", y)) }
        // La fiebre: los jardines se vacían de golpe.
        assertTrue(ficha(c, "2028-03-27").arcos.getValue("jardines") > 0.6); assertTrue(ficha(c, "2028-03-28").arcos.getValue("jardines") < 0.1)
        assertEquals(1.0, ficha(c, "2031-05-24").arcos.getValue("investigacion"), 0.0)
    }

    @Test fun ruthAyudaAOtrosPueblosAguantaElAsedioYLaVentisca() {
        val c = AnioG8.RUTH
        estadoEntre(c, "auxilio", "2027-06-01", "2027-06-15")
        estadoEntre(c, "asedio", "2029-07-28", "2029-08-05")
        estadoEntre(c, "encierro", "2030-01-14", "2030-02-05")
        estadoEntre(c, "auxilio", "2030-06-25", "2030-07-11")
        estadoEntre(c, "auxilio", "2031-05-30", "2031-06-11")
        (2032..2036).forEach { y -> assertEquals("$y", 13, diasEn(c, "auxilio", y)) }
        // El muro arde en el asedio y se termina de piedra en la temporada 5.
        assertTrue(ficha(c, "2029-08-02").arcos.getValue("muro") < ficha(c, "2029-08-01").arcos.getValue("muro") - 0.2)
        assertEquals(1.0, ficha(c, "2031-08-25").arcos.getValue("muro"), 0.0)
        // El granizo de la temporada 2 tumba la mitad del maíz.
        assertTrue(ficha(c, "2028-07-08").arcos.getValue("maiz") < 0.4); assertEquals(1.0, ficha(c, "2029-09-14").arcos.getValue("maiz"), 0.0)
    }

    @Test fun brennaPasaCadaInviernoConElPasoCerradoYLlegaATorremuda() {
        val c = AnioG8.BRENNA
        // La nieve cierra y abre el paso en un día distinto cada año.
        assertEquals(5, fechas(c, "abre_paso").toSet().size); assertEquals(5, fechas(c, "cierra_paso").toSet().size)
        // El invierno de antes del primer año también tiene el paso cerrado: se mira solo hasta que se abre.
        (1..80).forEach { n -> assertEquals("${LocalDate.of(2027, 1, 1).plusDays(n - 1L)}", "paso_cerrado", ficha(c, LocalDate.of(2027, 1, 1).plusDays(n - 1L).toString()).estado) }
        assertTrue(ficha(c, "2027-03-22").estado != "paso_cerrado")
        estadoEntre(c, "paso_cerrado", "2027-12-06", "2028-03-15")
        estadoEntre(c, "paso_cerrado", "2030-12-03", "2031-03-24")
        estadoEntre(c, "paso_cerrado", "2031-12-08", "2031-12-31", despues = false)
        estadoEntre(c, "travesia", "2029-07-20", "2029-08-18")
        estadoEntre(c, "crianza", "2031-04-12", "2031-05-19")
        // El año de la travesía, la Compañía de Verano es ella: no hay otra salida ese día.
        assertEquals(null, fechas(c, "compania_verano")[2])
        // La veta nueva se abre en la temporada 1 y se derrumba en la 4.
        assertEquals(0.0, ficha(c, "2027-04-07").arcos.getValue("veta"), 0.0); assertTrue(ficha(c, "2027-04-08").arcos.getValue("veta") > 0.2)
        assertEquals(0.0, ficha(c, "2030-06-14").arcos.getValue("veta"), 0.0)
        assertEquals(1.0, ficha(c, "2030-09-28").arcos.getValue("refugio"), 0.0)
    }
}
