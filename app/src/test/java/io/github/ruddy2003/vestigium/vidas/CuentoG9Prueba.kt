package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.PiezasIniciado
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los cuentos del grupo 9, los de los temas del lote 10: el de Tadeo, el agente de la red de Frente exterior, que es
 * ficción, y el de Eleazar, el iniciado de Arcontes, una persona inventada en la Galilea real (al final). Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
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

    // ================================================================= Eleazar, el iniciado de Arcontes

    private val e = "iniciado"
    private val piezaE get() = TodasLasPiezas.de(e)!!
    private val calE get() = Calendarios.de(e)!!
    private val eleazar get() = OtrasVidas.porId(e)!!
    private fun cuentoE(dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, eleazar, calE), eleazar, calE, piezaE, hora)
    private val diaEntero = HashMap<String, String>()
    /** Todo lo que dice su cuento ese día, a una hora de cada tramo de «Un día suyo». */
    private fun todoElDiaE(dia: String) = diaEntero.getOrPut(dia) { listOf(2.0, 5.5, 6.5, 9.0, 12.5, 15.0, 17.5, 18.5, 20.0).flatMap { cuentoE(dia, it).parrafos }.joinToString(" ") }
    private fun normalesE(desde: String, hasta: String) = dias(desde, hasta).filter { !cuentoE(it).especial }
    private fun nuncaE(desde: String, hasta: String, texto: String) =
        normalesE(desde, hasta).forEach { assertFalse("$it: «$texto» fuera de tiempo", texto in todoElDiaE(it)) }
    private fun algunaE(desde: String, hasta: String, texto: String) =
        assertTrue("«$texto» no sale entre $desde y $hasta", normalesE(desde, hasta).any { texto in todoElDiaE(it) })
    private fun soloEntreE(desde: String, hasta: String, texto: String) {
        if (desde > "2027-01-01") nuncaE("2027-01-01", LocalDate.parse(desde).minusDays(1).toString(), texto)
        if (hasta < "2031-12-31") nuncaE(LocalDate.parse(hasta).plusDays(1).toString(), "2031-12-31", texto)
        algunaE(desde, hasta, texto)
    }
    private fun textosDeE(lugar: Parrafo, estado: String) =
        piezaE.variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to eleazar.nombre), piezaE.genero) }.toSet()

    @Test fun estanLasPiezasDeEleazar() {
        assertEquals(e, piezaE.vida)
        assertTrue("La temporada 1 está escrita", 1 in piezaE.temporadasEscritas)
        assertEquals(emptyList<String>(), piezaE.problemas(calE))
        assertEquals(Genero.MASCULINO, piezaE.genero)
        // No es ficción: es una persona inventada en un mundo real.
        assertEquals(Cuento.NOTA_HISTORICA, cuentoE("2027-04-04").nota)
        assertTrue(piezaE.textos().count { "{nombre}" in it } > 40)
        listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertTrue(d, "Eleazar" in todoElDiaE(d)) }
        // Los días especiales de la temporada 1, todos, y solo ellos.
        assertEquals(calE.temporadas[0].dias.map { it.id }.toSet(), PiezasIniciado.POR_TEMPORADA.getValue(1).keys)
    }

    @Test fun lasTemporadasDosACincoVanCadaUnaEnSuArchivo() {
        val porTemporada = PiezasIniciado.POR_TEMPORADA
        assertEquals((1..5).toSet(), porTemporada.keys)
        porTemporada.forEach { (t, textos) ->
            val suyos = calE.temporadas[t - 1].dias.map { it.id }.toSet()
            textos.keys.forEach { assertTrue("Temporada $t: «$it» no es un día de esa temporada", it in suyos) }
        }
        // Ninguna pisa a otra, y todas llegan a las piezas.
        assertEquals(porTemporada.values.sumOf { it.size }, piezaE.especiales.size)
        // Una temporada entra en las escritas cuando su archivo lo dice, y entonces tiene el texto de todos sus días.
        piezaE.temporadasEscritas.forEach { t -> assertEquals("Temporada $t", calE.temporadas[t - 1].dias.map { it.id }.toSet(), porTemporada.getValue(t).keys) }
    }

    @Test fun jesusNuncaDeFrenteYTodaviaSinCitasTextuales() {
        val deEl = Regex("""\b(Nazaret|Nazareno|Jesús|Salvador)\b""")
        val deFrente = listOf("rostro", "su cara", "sus ojos", "de frente", "le vio la cara a", "lo miró a los ojos")
        piezaE.textos().filter { deEl.containsMatchIn(it) }.forEach { s -> deFrente.forEach { assertFalse("«$it» en «${s.take(60)}…»", it in s) } }
        // Sus palabras van en estilo indirecto, con la cita al lado: todavía no hay ninguna cita textual entre comillas.
        piezaE.textos().forEach { assertFalse("Cita textual en «${it.take(60)}…»", '«' in it || '"' in it) }
        // Los días en que lo ve (la figura de luz del cuadro), lo ve de espaldas.
        piezaE.temporadasEscritas.forEach { t -> calE.temporadas[t - 1].dias.filter { it.escena == "figura_luz" }.forEach { d ->
            val texto = piezaE.especiales.getValue(d.id).dias.first().values.joinToString(" ")
            assertTrue("«${d.id}»: no dice que lo ve de espaldas", "espaldas" in texto)
        } }
    }

    @Test fun laVozDelTemaYNadaFueraDeSuTiempo() {
        // La voz del tema (`docs/diseno/temas/arcontes.md`) y lo que en el año 28 todavía no existía.
        val prohibidas = listOf("magia", "mágico", "mágica", "hechizo", "hechizos", "demoníaco", "demoníaca", "iluminado", "illuminati", "cristiano", "cristianos", "iglesia",
            "pentagrama", "calavera", "satánico", "Helena", "Jehová", "Yahvé")
        piezaE.textos().forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", Regex("""\b$it\b""", RegexOption.IGNORE_CASE).containsMatchIn(s)) } }
    }

    @Test fun nadieApareceAntesNiDespuesDeTiempoEnElCuentoDeEleazar() {
        // El de Nazaret, desde que lo ve en el Jordán; en la orilla, de que vuelve a Galilea a la cruz; después, el que vio al amanecer.
        nuncaE("2027-01-01", "2027-11-05", "Nazaret")
        soloEntreE("2027-12-16", "2029-04-06", "dicen que el de Nazaret anda cerca")
        soloEntreE("2029-04-07", "2031-12-31", "donde hablaba el de Nazaret")
        soloEntreE("2029-04-28", "2031-12-31", "el hombre que vio en la orilla al amanecer")
        // Las barcas de los que lo siguen: solo mientras anda por el lago.
        nuncaE("2027-01-01", "2027-12-20", "barcas de fuera"); algunaE("2028-01-01", "2028-08-31", "barcas de fuera"); nuncaE("2028-09-25", "2031-12-31", "barcas de fuera")
        // María, la vecina: las voces, la casa cerrada y el sepulcro vacío.
        soloEntreE("2027-01-01", "2028-02-07", "Dicen que tiene siete demonios")
        soloEntreE("2028-02-08", "2029-04-08", "está cerrada desde que se fue con el de Nazaret")
        soloEntreE("2029-04-09", "2031-12-31", "fue la primera que vio el sepulcro vacío")
        // Mara espera a Débora, y Débora nace en el verano del 29.
        soloEntreE("2027-11-20", "2028-07-26", "Mara espera un hijo")
        nuncaE("2027-01-01", "2028-07-26", "Débora"); algunaE("2028-07-28", "2028-12-31", "Débora")
        // Shifra, hasta el invierno del 31.
        soloEntreE("2027-01-01", "2030-01-17", "Shifra, su madre, es viuda")
        soloEntreE("2030-01-18", "2031-12-31", "Desde que murió Shifra")
        // El círculo: en casa de Ismael y, desde noviembre del 31, en la cueva del Arbel.
        soloEntreE("2027-01-01", "2030-11-03", "el cuarto de atrás de la casa de Ismael")
        soloEntreE("2030-11-04", "2031-12-31", "la cueva del Arbel")
        // Simón de Samaria, desde que oye hablar de él.
        soloEntreE("2031-07-06", "2031-12-31", "Simón de Samaria")
    }

    @Test fun enJerusalenYEnSamariaSeNotaElEstado() {
        fun viveElEstado(estado: String, desde: String, hasta: String) {
            val manana = textosDeE(Parrafo.MANANA, estado); val pueblo = textosDeE(Parrafo.PUEBLO, estado)
            val anochecer = textosDeE(Parrafo.ANOCHECER, estado); val trabajo = textosDeE(Parrafo.TRABAJO, estado)
            val vistos = normalesE(desde, hasta)
            assertTrue("$estado: no hay días normales entre $desde y $hasta", vistos.isNotEmpty())
            vistos.forEach { dia ->
                assertEquals(dia, estado, cuentoE(dia).ficha.estado)
                val c = cuentoE(dia, 9.0)
                assertTrue("$dia: la mañana es la de siempre", c.parrafos[Parrafo.MANANA.ordinal] in manana)
                assertTrue("$dia: el pueblo es el de siempre", c.parrafos[Parrafo.PUEBLO.ordinal] in pueblo)
                assertTrue("$dia: el anochecer es el de siempre", c.parrafos[Parrafo.ANOCHECER.ordinal] in anochecer)
                listOf(2.0, 5.5, 6.5, 9.0, 15.0, 17.5, 20.0).forEach { h -> assertTrue("$dia, $h h: el trabajo es el de siempre", cuentoE(dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
            }
        }
        // En Jerusalén, entre la llegada y Yom Kipur, y entre Yom Kipur y Sucot; en Samaria, todo el verano del 32.
        viveElEstado("jerusalen", "2027-09-16", "2027-09-22")
        viveElEstado("huida", "2031-06-12", "2031-09-25")
        // Y fuera de los estados no sale ninguna de sus variantes.
        val deEstado = Parrafo.entries.flatMap { l -> textosDeE(l, "jerusalen") + textosDeE(l, "huida") }.toSet()
        normalesE("2027-01-01", "2027-09-11").forEach { d -> cuentoE(d, 9.0).parrafos.forEach { assertFalse("$d: variante de un estado en casa", it in deEstado) } }
    }

    @Test fun losDiasDeCaminoCambianLaMananaElTrabajoYElPueblo() {
        // Las variantes de «jerusalen» de la mañana, el trabajo y el pueblo son de la ciudad: en los días de camino
        // (los especiales `sube_*` y `baja_*`) no puede salir ninguna. Vale para cada temporada que esté escrita.
        val ciudad = listOf(Parrafo.MANANA, Parrafo.TRABAJO, Parrafo.PUEBLO).associateWith { textosDeE(it, "jerusalen") }
        piezaE.temporadasEscritas.forEach { t ->
            FichaDelDia.inicios(calE, Temporadas.PRIMER_ANIO + t - 1).filter { (d, _) -> d.id.startsWith("sube_") || d.id.startsWith("baja_") }.forEach { (d, inicio) ->
                (0 until d.duracion).map { inicio.plusDays(it.toLong()).toString() }.forEach { dia -> listOf(5.5, 9.0, 15.0, 20.0).forEach { h ->
                    val c = cuentoE(dia, h)
                    ciudad.forEach { (lugar, textos) -> assertFalse("$dia, $h h (${d.id}): ${lugar.nombre} de la ciudad en el camino", c.parrafos[lugar.ordinal] in textos) }
                } }
            }
        }
    }

    @Test fun noTrabajaEnLosDiasDeFiesta() {
        val noTrabaja = Regex("""\bno (se )?trabaja\b""")
        listOf("2027-05-20", "2027-09-09", "2027-09-18", "2027-09-23").forEach { dia -> listOf(9.0, 15.0).forEach { h ->
            assertTrue("$dia, $h h: trabaja en un día de fiesta", noTrabaja.containsMatchIn(cuentoE(dia, h).parrafos[Parrafo.TRABAJO.ordinal]))
        } }
    }
}
