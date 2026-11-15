package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los cuentos del grupo 9, los de los temas del lote 10. Por ahora, el de Tadeo, el agente de la red de Frente
 * exterior, que es ficción. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de su historia: que nadie aparezca antes ni después de
 * tiempo, que la red no salga antes de que lo recluten y que los estados se noten.
 */
class CuentoG9Prueba {
    private val v = "agente_red"
    private val pieza get() = TodasLasPiezas.de(v)!!
    private val cal get() = Calendarios.de(v)!!
    private val quien get() = OtrasVidas.porId(v)!!
    private fun cuento(dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien, cal), quien, cal, pieza, hora)
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a una hora de cada tramo de «Un día suyo» (cada una con su párrafo del trabajo). */
    private fun todoElDia(dia: String) = listOf(1.0, 5.0, 9.5, 12.0, 15.5, 17.0, 19.5, 21.0, 22.5).flatMap { cuento(dia, it).parrafos }.joinToString(" ")
    private fun normales(desde: String, hasta: String) = dias(desde, hasta).filter { !cuento(it).especial }
    private fun textosDe(lugar: Parrafo, estado: String) =
        pieza.variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to quien.nombre), pieza.genero) }.toSet()
    private fun palabra(p: String) = Regex("""\b$p\b""")
    private fun nadieAntes(nombre: String, hasta: String) =
        normales("2027-01-01", hasta).forEach { assertFalse("$it: $nombre antes de tiempo", palabra(nombre).containsMatchIn(todoElDia(it))) }
    private fun nunca(desde: String, hasta: String, texto: String) =
        normales(desde, hasta).forEach { assertFalse("$it: «$texto» fuera de tiempo", texto in todoElDia(it)) }
    private fun alguna(desde: String, hasta: String, texto: String) =
        assertTrue("«$texto» no sale entre $desde y $hasta", normales(desde, hasta).any { texto in todoElDia(it) })
    private fun soloEntre(desde: String, hasta: String, texto: String) {
        nunca("2027-01-01", LocalDate.parse(desde).minusDays(1).toString(), texto)
        if (hasta < "2031-12-31") nunca(LocalDate.parse(hasta).plusDays(1).toString(), "2031-12-31", texto)
        alguna(desde, hasta, texto)
    }
    private fun fechas(id: String) = cal.temporadas.mapIndexedNotNull { i, t -> t.dias.firstOrNull { it.id == id }?.let { "${2027 + i}-${it.fecha}" } }
    private fun descansa(dia: String) = listOf(8.0, 14.0).forEach { h ->
        assertTrue("$dia, $h h: trabaja en su día de fiesta", "no trabaja" in cuento(dia, h).parrafos[Parrafo.TRABAJO.ordinal])
    }

    /** En los días del estado [estado], la mañana y el trabajo a las [horas] salen de sus variantes, no de las de siempre. */
    private fun viveElEstado(estado: String, desde: String, hasta: String, horas: List<Double>) {
        val manana = textosDe(Parrafo.MANANA, estado); val trabajo = textosDe(Parrafo.TRABAJO, estado)
        val pueblo = textosDe(Parrafo.PUEBLO, estado); val anochecer = textosDe(Parrafo.ANOCHECER, estado)
        dias(desde, hasta).filter { !cuento(it).especial }.forEach { dia ->
            assertEquals(dia, estado, cuento(dia).ficha.estado)
            val c = cuento(dia, 9.0)
            assertTrue("$dia: la mañana es la de siempre", c.parrafos[Parrafo.MANANA.ordinal] in manana)
            assertTrue("$dia: el puerto es el de siempre", c.parrafos[Parrafo.PUEBLO.ordinal] in pueblo)
            assertTrue("$dia: el anochecer es el de siempre", c.parrafos[Parrafo.ANOCHECER.ordinal] in anochecer)
            horas.forEach { h -> assertTrue("$dia, $h h: el trabajo es el de siempre", cuento(dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
        }
    }
    private fun varianEnElEstado(desde: String, hasta: String, hora: Double) {
        val n = normales(desde, hasta)
        listOf(Parrafo.MANANA, Parrafo.TRABAJO, Parrafo.ANOCHECER).forEach { lugar ->
            assertTrue("pocos párrafos distintos en «${lugar.nombre}»", n.map { cuento(it, hora).parrafos[lugar.ordinal] }.toSet().size >= 2)
        }
    }

    @Test fun estanLasPiezasDeTadeoConSuNombre() {
        assertEquals(v, pieza.vida)
        assertEquals("Solo la temporada 1 está escrita", setOf(1), pieza.temporadasEscritas)
        assertEquals(emptyList<String>(), pieza.problemas(cal))
        assertEquals(Genero.MASCULINO, pieza.genero)
        assertEquals(Cuento.NOTA_FICCION, cuento("2027-04-04").nota)
        // Su ficha se llama «Tadeo», y el cuento usa su nombre.
        assertTrue(pieza.textos().count { "{nombre}" in it } > 40)
        listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertTrue(d, "Tadeo" in todoElDia(d)) }
    }

    @Test fun nadaSaleDeLaSaga() {
        val prohibidas = listOf("imperio", "imperial", "rebelde", "rebeldes", "rebelión", "alianza", "la fuerza", "jedi", "sith", "galaxia muy lejana", "andor", "ferrix", "coruscant",
            "aldhani", "kenari", "narkina", "ghorman", "cassian", "luthen", "mothma", "bix", "syril", "dedra", "maarva", "soldado de asalto", "bláster", "droide", "hiperespacio", "créditos")
        pieza.textos().forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", palabra(it).containsMatchIn(s.lowercase())) } }
    }

    @Test fun laRedNoSaleAntesDeQueLoRecluten() {
        // Del 1 de enero al 23 de febrero de la temporada 1 es solo un ladrón de piezas.
        // («la red» como palabra: «la pantalla redonda» de la radio no cuenta.)
        normales("2027-01-01", "2027-02-23").forEach { d -> listOf("la red", "célula", "Cobre", "la hora de la ventana", "ventana de transmisión", "la voz que lee números").forEach {
            assertFalse("$d: «$it» antes de que lo recluten", palabra(it).containsMatchIn(todoElDia(d)))
        } }
        alguna("2027-01-01", "2027-02-23", "vuelve a saltar la valla")
        nunca("2027-02-24", "2031-12-31", "vuelve a saltar la valla")
        // La ventana de transmisión, desde que oye la primera.
        nunca("2027-01-01", "2027-05-25", "la voz que lee números")
        alguna("2027-05-27", "2027-12-31", "la voz que lee números")
        // Lo que vuelve cada año no dice que es de la red: los dos primeros del año caen antes del reclutamiento.
        listOf("cambio_anio", "cuota").forEach { id -> pieza.especiales.getValue(id).dias.flatMap { it.values }.forEach { s ->
            listOf("la red", "célula", "Cobre", "ventana").forEach { assertFalse("«$id» dice «$it»", palabra(it).containsMatchIn(s)) }
        } }
    }

    @Test fun nadieApareceAntesNiDespuesDeTiempo() {
        // Odila: la relojera que le compra piezas, su jefa, desaparecida, presa, escondida, lejos y viva en otro mundo.
        soloEntre("2027-01-01", "2027-02-23", "Lleva años comprándole")
        soloEntre("2027-02-24", "2029-10-01", "es la que manda en la célula")
        soloEntre("2029-10-02", "2029-12-11", "De Odila no se sabe nada")
        soloEntre("2029-12-12", "2030-07-08", "Odila está viva, en el Hondo")
        soloEntre("2030-07-09", "2030-08-24", "Odila está escondida")
        soloEntre("2030-08-25", "2031-11-23", "Odila se fue de Halde")
        nunca("2027-01-01", "2031-11-23", "Odila vive en otro mundo")
        // Gaspar, desde el primer recado; preso desde que lo detienen; muerto desde que se sabe.
        nadieAntes("Gaspar", "2027-03-27")
        soloEntre("2027-03-28", "2029-06-23", "Gaspar es el más viejo de la célula")
        soloEntre("2029-06-24", "2030-03-03", "A Gaspar se lo llevaron")
        nunca("2027-01-01", "2030-03-03", "Gaspar murió")
        // Lis, Tula y Morell.
        nadieAntes("Lis", "2028-03-21"); nadieAntes("Tula", "2028-02-13"); nadieAntes("Morell", "2029-02-26")
        nunca("2031-08-05", "2031-12-31", "Morell")
        soloEntre("2027-09-08", "2028-02-13", "Sela espera un hijo")
        soloEntre("2028-02-14", "2029-03-29", "duerme en un cajón")
        nunca("2027-01-01", "2029-03-29", "ya corre por el patio")
        // La relojería quemada y reabierta, y la estación de radar encendida y ciega.
        soloEntre("2029-10-02", "2031-09-09", "es un hueco negro")
        nunca("2027-01-01", "2031-09-09", "«Radios»")
        soloEntre("2028-04-30", "2031-06-28", "la estación de radar del Directorio")
        nunca("2027-01-01", "2031-06-28", "a oscuras desde la noche del golpe")
        // El crucero de anillo, sobre el puerto de la fuga al golpe.
        nunca("2027-01-01", "2030-07-11", "crucero"); nunca("2031-07-03", "2031-12-31", "crucero")
        alguna("2030-07-23", "2031-06-25", "cuelga el crucero de anillo")
        // Lo que vuelve cada año no nombra a nadie que llega o se va.
        val deCadaAnio = cal.temporadas.flatMap { t -> t.dias.map { it.id } }.groupingBy { it }.eachCount().filterValues { it >= 2 }.keys
        assertTrue(deCadaAnio.size >= 10)
        deCadaAnio.forEach { id -> pieza.especiales[id]?.let { t -> (t.dias.flatMap { it.values } + t.sigue.values.flatten()).forEach { s ->
            listOf("Odila", "Gaspar", "Lis", "Tula", "Morell", "relojería", "crucero").forEach { assertFalse("«$id» nombra «$it»: «${s.take(50)}…»", palabra(it).containsMatchIn(s)) }
        } } }
    }

    @Test fun seEscondeEnLaMinaYSubeALaCresta() {
        val horas = listOf(1.0, 5.0, 9.5, 12.0, 15.5, 17.0, 19.5, 21.0, 22.5)
        viveElEstado("escondido", "2029-10-03", "2029-10-29", horas); varianEnElEstado("2029-10-03", "2029-10-29", 12.0)
        viveElEstado("cresta", "2028-07-15", "2028-07-17", horas); varianEnElEstado("2028-07-15", "2028-07-17", 12.0)
        viveElEstado("cresta", "2031-06-27", "2031-06-28", horas)
        // Escondido no ve el puerto ni a sus vecinos de siempre: solo a Yusef, por la cesta.
        normales("2029-10-03", "2029-10-29").forEach { d -> listOf("Brun", "Abel", "el Pozo, el bar").forEach { assertFalse("$d: «$it» mientras está escondido", it in cuento(d, 9.0).parrafos.joinToString(" ")) } }
    }

    @Test fun noTrabajaEnLasFiestasDeSiempre() {
        listOf("cambio_anio", "concordia", "noche_larga").flatMap { fechas(it) }.also { assertEquals(15, it.size) }.forEach { descansa(it) }
    }
}
