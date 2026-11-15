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
 * ficción; el de Eleazar, el iniciado de Arcontes, una persona inventada en la Galilea real; el de Segomaros, el aprendiz
 * de los druidas de Galia, una persona inventada en la Galia real; el de Marguerite, la aprendiz de litografía de Art
 * nouveau, una persona inventada en el París real, y el de David, el dibujante de carteles de Psicodelia, una persona
 * inventada en el San Francisco real (los cuatro, al final). Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
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

    /**
     * Los versículos de la Reina-Valera 1909 que el cuento cita tal cual, copiados letra por letra de Wikisource
     * (es.wikisource.org, «Biblia Reina-Valera 1909», un capítulo por página) el 15 nov 2026, con su ortografía de entonces.
     * La tabla completa, con los enlaces, está en la sección 14 de `docs/diseno/temas/fuentes_arcontes.md`.
     */
    private val RV1909 = mapOf(
        "Marcos 1:24" to "Diciendo: ¡Ah! ¿qué tienes con nosotros, Jesús Nazareno? ¿Has venido á destruirnos? Sé quién eres, el Santo de Dios.",
        "Marcos 1:41" to "Y Jesús, teniendo misericordia de él, extendió su mano, y le tocó, y le dice: Quiero, sé limpio.",
        "Marcos 6:18" to "Porque Juan decía á Herodes: No te es lícito tener la mujer de tu hermano.",
        "Marcos 4:39" to "Y levantándose, increpó al viento, y dijo á la mar: Calla, enmudece. Y cesó el viento, y fué hecha grande bonanza.",
        "Juan 6:63" to "El espíritu es el que da vida; la carne nada aprovecha: las palabras que yo os he hablado, son espíritu y son vida.",
        "Juan 7:34" to "Me buscaréis, y no me hallaréis; y donde yo estaré, vosotros no podréis venir.",
        "Juan 10:34" to "Respondióles Jesús: ¿No está escrito en vuestra ley: Yo dije, Dioses sois?",
        "Juan 19:30" to "Y como Jesús tomó el vinagre, dijo: Consumado es. Y habiendo inclinado la cabeza, dió el espíritu.",
        "Juan 20:17" to "Dícele Jesús: No me toques: porque aun no he subido á mi Padre: mas ve á mis hermanos, y diles: Subo á mi Padre y á vuestro Padre, á mi Dios y á vuestro Dios.",
        "Juan 21:12" to "Díceles Jesús: Venid, comed. Y ninguno de los discípulos osaba preguntarle: ¿Tú, quién eres? sabiendo que era el Señor.",
        "Hechos 1:11" to "Los cuales también les dijeron: Varones Galileos, ¿qué estáis mirando al cielo? este mismo Jesús que ha sido tomado desde vosotros arriba en el cielo, así vendrá como le habéis visto ir al cielo.",
        "Hechos 3:6" to "Y Pedro dijo: Ni tengo plata ni oro; mas lo que tengo te doy: en el nombre de Jesucristo de Nazaret, levántate y anda.",
        "Hechos 4:20" to "Porque no podemos dejar de decir lo que hemos visto y oído.",
        "Hechos 7:60" to "Y puesto de rodillas, clamó á gran voz: Señor, no les imputes este pecado. Y habiendo dicho esto, durmió.",
        "Hechos 8:10" to "Al cual oían todos atentamente desde el más pequeño hasta el más grande, diciendo: Este es la gran virtud de Dios.",
    )

    @Test fun jesusNuncaDeFrenteYSusPalabrasConSuCita() {
        val deEl = Regex("""\b(Nazaret|Nazareno|Jesús|Salvador)\b""")
        val deFrente = listOf("rostro", "su cara", "sus ojos", "de frente", "le vio la cara a", "lo miró a los ojos")
        piezaE.textos().filter { deEl.containsMatchIn(it) }.forEach { s -> deFrente.forEach { assertFalse("«$it» en «${s.take(60)}…»", it in s) } }
        // Las palabras van en estilo indirecto, con la cita al lado. Solo unas pocas frases cortas van tal cual, de la
        // Reina-Valera 1909: entre comillas españolas, seguidas de su cita entre paréntesis («…» (Juan 19:30)), y cada una
        // está letra por letra en el versículo que cita (el primero, si la cita nombra varios). Nunca comillas inglesas.
        val conCita = Regex("""«([^«»]+)» \(([^()]+)\)""")
        var textuales = 0
        piezaE.textos().forEach { s ->
            assertFalse("Comillas inglesas en «${s.take(60)}…»", '"' in s || '“' in s || '”' in s)
            val citas = conCita.findAll(s).toList()
            assertEquals("Comillas sin su cita al lado en «${s.take(60)}…»", s.count { it == '«' }, citas.size)
            assertEquals("Comillas sin cerrar en «${s.take(60)}…»", s.count { it == '«' }, s.count { it == '»' })
            citas.forEach { m ->
                val (frase, cita) = m.destructured
                val ref = cita.split(';', ',').first().trim()
                val versiculo = RV1909[ref]
                assertTrue("«$frase» ($cita): no está entre los versículos copiados de la Reina-Valera 1909", versiculo != null)
                assertTrue("«$frase» no está tal cual en $ref de la Reina-Valera 1909: «$versiculo»", frase in versiculo!!)
            }
            textuales += citas.size
        }
        assertTrue("Hay $textuales citas textuales: no más de 15 en todo el cuento", textuales in 1..15)
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

    // ================================================================= Segomaros, el aprendiz de los druidas de Galia

    private val s = "aprendiz_druida"
    private val piezaS get() = TodasLasPiezas.de(s)!!
    private val calS get() = Calendarios.de(s)!!
    private val segomaros get() = OtrasVidas.porId(s)!!
    private fun cuentoS(dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, segomaros, calS), segomaros, calS, piezaS, hora)
    private val diaEnteroS = HashMap<String, String>()
    /** Todo lo que dice su cuento ese día, a una hora de cada tramo de «Un día suyo». */
    private fun todoElDiaS(dia: String) = diaEnteroS.getOrPut(dia) { listOf(2.0, 5.5, 6.5, 9.0, 11.5, 14.0, 17.0, 18.5, 20.0).flatMap { cuentoS(dia, it).parrafos }.joinToString(" ") }
    private fun normalesS(desde: String, hasta: String) = dias(desde, hasta).filter { !cuentoS(it).especial }
    private fun nuncaS(desde: String, hasta: String, texto: String) =
        normalesS(desde, hasta).forEach { assertFalse("$it: «$texto» fuera de tiempo", texto in todoElDiaS(it)) }
    private fun algunaS(desde: String, hasta: String, texto: String) =
        assertTrue("«$texto» no sale entre $desde y $hasta", normalesS(desde, hasta).any { texto in todoElDiaS(it) })
    private fun soloEntreS(desde: String, hasta: String, texto: String) {
        if (desde > "2027-01-01") nuncaS("2027-01-01", LocalDate.parse(desde).minusDays(1).toString(), texto)
        if (hasta < "2031-12-31") nuncaS(LocalDate.parse(hasta).plusDays(1).toString(), "2031-12-31", texto)
        algunaS(desde, hasta, texto)
    }
    private fun textosDeS(lugar: Parrafo, estado: String) =
        piezaS.variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to segomaros.nombre), piezaS.genero) }.toSet()

    @Test fun estanLasPiezasDeSegomaros() {
        assertEquals(s, piezaS.vida)
        assertEquals("Solo la temporada 1 está escrita", setOf(1), piezaS.temporadasEscritas)
        assertEquals(emptyList<String>(), piezaS.problemas(calS))
        assertEquals(Genero.MASCULINO, piezaS.genero)
        // No es ficción: es una persona inventada en un mundo real.
        assertEquals(Cuento.NOTA_HISTORICA, cuentoS("2027-04-04").nota)
        assertTrue(piezaS.textos().count { "{nombre}" in it } > 40)
        listOf("2027-03-03", "2029-05-10", "2030-09-09", "2031-11-11").forEach { d -> assertTrue(d, "Segomaros" in todoElDiaS(d)) }
        // Los días especiales de la temporada 1, todos, y solo ellos.
        assertEquals(calS.temporadas[0].dias.map { it.id }.toSet(), piezaS.especiales.keys)
    }

    /**
     * Los pasajes de la traducción de César de José Goya y Muniain (Madrid, 1919; dominio público) de los que el cuento
     * copia frases tal cual, copiados de Wikisource (es.wikisource.org, «Comentarios de la guerra de las Galias (1919)»,
     * libros sexto y séptimo) el 15 nov 2026, con su ortografía.
     */
    private val GOYA = mapOf(
        "César 6.14" to "Esméranse, sobre todo, en persuadir la inmortalidad de las almas y su transmigración de unos cuerpos a otros, cuya creencia juzgan ser grandísimo incentivo para el valor, poniendo aparte el temor de la muerte.",
        "César 7.89" to "Al otro día Vercingetórix, convocada su gente, protesta \"no haber emprendido él esta guerra por sus propios intereses, sino por la defensa de la común libertad; mas ya que es forzoso ceder a la fortuna, él está pronto a que lo sacrifiquen, o dándole, si quieren, la muerte, o entregándolo vivo a los Romanos para satisfacerlos\".",
    )

    @Test fun loQueVaTalCualEsDeGoyaYLoDemasEnEstiloIndirecto() {
        // Las frases tal cual van entre comillas españolas, seguidas de su pasaje («…» (César 7.89)), y están letra por letra
        // en el pasaje de Goya que citan. Nunca comillas inglesas.
        val conCita = Regex("""«([^«»]+)» \((César [0-9.]+)\)""")
        var textuales = 0
        piezaS.textos().forEach { t ->
            assertFalse("Comillas inglesas en «${t.take(60)}…»", '"' in t || '“' in t || '”' in t)
            val citas = conCita.findAll(t).toList()
            assertEquals("Comillas sin su pasaje al lado en «${t.take(60)}…»", t.count { it == '«' }, citas.size)
            citas.forEach { m ->
                val (frase, pasaje) = m.destructured
                val texto = GOYA[pasaje]
                assertTrue("«$frase» ($pasaje): no está entre los pasajes copiados de Goya", texto != null)
                assertTrue("«$frase» no está tal cual en $pasaje de Goya", frase in texto!!)
            }
            textuales += citas.size
        }
        assertTrue("Hay $textuales citas textuales", textuales in 1..6)
        // Lo de César se cuenta como lo que dice César: el cuento lo nombra cada vez que lo usa.
        assertTrue(piezaS.textos().count { "César 6." in it || "César 7." in it || "César 5." in it || "(6.1" in it || "(7." in it } >= 15)
    }

    @Test fun laVozDeGaliaYNadaFueraDeSuTiempo() {
        // Lo que el tema no dice nunca (`docs/diseno/temas/galia.md`): mitos irlandeses y galeses, dioses de época romana,
        // druidismo moderno, el druida como mago de cuento y «bárbaro» como palabra propia. Y lo que no había en la Galia
        // del 52 a. C.: castañas, cristianos, iglesias, nombres de tebeo.
        val prohibidas = listOf("Cernunnos", "Epona", "Sucellus", "Belenus", "Lugus", "Lug", "Samhain", "Beltane", "Beltaine", "ogham", "Stonehenge", "trisquel",
            "bárbaro", "bárbaros", "bárbara", "magia", "mago", "mágico", "mágica", "hechizo", "poción", "druidesa", "Tutatis", "castañas", "castaña", "cristiano", "iglesia")
        piezaS.textos().forEach { t -> prohibidas.forEach { assertFalse("«$it» en «${t.take(60)}…»", Regex("""(?U)\b$it\b""", RegexOption.IGNORE_CASE).containsMatchIn(t)) } }
        // Los dioses que nombra son los de Lucano y los que César nombra a la romana.
        val dioses = Regex("""\b(Teutates|Esus|Taranis|Mercurio|Apolo|Marte|Júpiter|Minerva|Dis Pater)\b""")
        assertTrue(piezaS.textos().any { dioses.containsMatchIn(it) })
    }

    @Test fun nadieApareceAntesNiDespuesDeTiempoEnElCuentoDeSegomaros() {
        // Vercingétorix, desde que lo echan de Gergovia; rey hasta la entrega; desde la entrega, el de Alesia.
        nuncaS("2027-01-01", "2029-02-04", "Vercingétorix")
        soloEntreS("2029-02-12", "2029-09-26", "el rey, al que ha visto de lejos")
        soloEntreS("2029-09-27", "2031-12-31", "por la defensa de la común libertad")
        // La escuela: en el robledal hasta que la deja; solo, con los versos, hasta que vuelve; y otra vez en el robledal.
        soloEntreS("2027-01-01", "2029-02-15", "sentado en la hojarasca con los otros aprendices")
        soloEntreS("2029-02-16", "2031-01-09", "Desde que dejó la escuela")
        soloEntreS("2031-01-10", "2031-12-31", "Repite con ellos versos que ya sabía")
        // Esunertos: le enseña armas hasta Alesia; después no se sabe de él; desde que se sabe, cayó.
        soloEntreS("2027-01-01", "2029-08-16", "Le enseña armas y caballos sin piedad")
        soloEntreS("2029-10-13", "2029-10-23", "De Esunertos no se sabe nada")
        soloEntreS("2029-10-24", "2031-12-31", "Esunertos cayó en la última batalla")
        // La herida del brazo, desde la batalla de caballería; el potro, desde que se lo dan.
        soloEntreS("2029-08-14", "2031-12-31", "La marca es una raya blanca")
        soloEntreS("2030-02-20", "2031-12-31", "monta su potro")
        // Su hermana: en casa, prometida, casada en la montaña y con un hijo.
        soloEntreS("2027-01-01", "2028-09-05", "de lengua rápida")
        soloEntreS("2028-09-06", "2030-04-14", "A su hermana la han prometido")
        soloEntreS("2030-04-15", "2031-08-13", "Su hermana vive en la montaña desde la boda")
        soloEntreS("2031-08-14", "2031-12-31", "Su hermana tiene un hijo")
        // Los mercaderes de Italia en el mercado: hasta Cenabum, y otra vez desde que vuelven.
        soloEntreS("2027-01-01", "2029-01-22", "Algunos vienen de Italia")
        soloEntreS("2030-06-03", "2031-12-31", "El mercado de Gergovia está otra vez lleno")
        // Los campamentos de César al pie de la meseta, solo mientras están (lo dice el arco `romanos`).
        soloEntreS("2029-05-05", "2029-05-22", "están los campamentos de César")
    }

    @Test fun conLosJinetesEnAlesiaYCautivoSeNotaElEstado() {
        fun viveElEstado(estado: String, desde: String, hasta: String) {
            val manana = textosDeS(Parrafo.MANANA, estado); val pueblo = textosDeS(Parrafo.PUEBLO, estado)
            val anochecer = textosDeS(Parrafo.ANOCHECER, estado); val trabajo = textosDeS(Parrafo.TRABAJO, estado); val gente = textosDeS(Parrafo.GENTE, estado)
            val vistos = normalesS(desde, hasta)
            assertTrue("$estado: no hay días normales entre $desde y $hasta", vistos.isNotEmpty())
            vistos.forEach { dia ->
                assertEquals(dia, estado, cuentoS(dia).ficha.estado)
                val c = cuentoS(dia, 9.0)
                assertTrue("$dia: la mañana es la de siempre", c.parrafos[Parrafo.MANANA.ordinal] in manana)
                assertTrue("$dia: el pueblo es el de siempre", c.parrafos[Parrafo.PUEBLO.ordinal] in pueblo)
                assertTrue("$dia: la gente es la de siempre", c.parrafos[Parrafo.GENTE.ordinal] in gente)
                assertTrue("$dia: el anochecer es el de siempre", c.parrafos[Parrafo.ANOCHECER.ordinal] in anochecer)
                listOf(2.0, 5.5, 6.5, 9.0, 11.5, 14.0, 17.0, 18.5, 20.0).forEach { h -> assertTrue("$dia, $h h: el trabajo es el de siempre", cuentoS(dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
            }
        }
        viveElEstado("campana", "2029-03-02", "2029-04-29"); viveElEstado("campana", "2029-06-14", "2029-08-16")
        viveElEstado("alesia", "2029-08-17", "2029-09-26"); viveElEstado("cautivo", "2029-09-27", "2029-10-07")
        // Con los jinetes y en Alesia, la mañana, el trabajo y el anochecer cambian de un día a otro.
        listOf("2029-03-02" to "2029-04-29", "2029-08-17" to "2029-09-26").forEach { (a, b) ->
            val n = normalesS(a, b)
            listOf(Parrafo.MANANA, Parrafo.TRABAJO, Parrafo.ANOCHECER).forEach { lugar ->
                assertTrue("$a: pocos párrafos distintos en «${lugar.nombre}»", n.map { cuentoS(it, 14.0).parrafos[lugar.ordinal] }.toSet().size >= 2)
            }
        }
        // Y fuera de los estados no sale ninguna de sus variantes.
        val deEstado = Parrafo.entries.flatMap { l -> listOf("campana", "alesia", "cautivo").flatMap { textosDeS(l, it) } }.toSet()
        (normalesS("2027-01-01", "2028-12-31") + normalesS("2029-10-13", "2031-12-31")).forEach { d -> cuentoS(d, 9.0).parrafos.forEach { assertFalse("$d: variante de un estado en casa", it in deEstado) } }
    }

    // ================================================================= Marguerite, la aprendiz de litografía de Art nouveau

    private val m = "litografa"
    private val piezaM get() = TodasLasPiezas.de(m)!!
    private val calM get() = Calendarios.de(m)!!
    private val marguerite get() = OtrasVidas.porId(m)!!
    private fun cuentoM(dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, marguerite, calM), marguerite, calM, piezaM, hora)
    private val diaEnteroM = HashMap<String, String>()
    /** Todo lo que dice su cuento ese día, a una hora de cada tramo de «Un día suyo». */
    private fun todoElDiaM(dia: String) = diaEnteroM.getOrPut(dia) { listOf(2.0, 6.5, 7.2, 9.0, 12.5, 15.0, 19.2, 20.0, 22.0).flatMap { cuentoM(dia, it).parrafos }.joinToString(" ") }
    private val normalesM = HashMap<String, Boolean>()
    private fun normalesM(desde: String, hasta: String) = dias(desde, hasta).filter { d -> normalesM.getOrPut(d) { !cuentoM(d).especial } }
    private fun nuncaM(desde: String, hasta: String, texto: String) =
        normalesM(desde, hasta).forEach { assertFalse("$it: «$texto» fuera de tiempo", texto in todoElDiaM(it)) }
    private fun algunaM(desde: String, hasta: String, texto: String) =
        assertTrue("«$texto» no sale entre $desde y $hasta", normalesM(desde, hasta).any { texto in todoElDiaM(it) })
    private fun soloEntreM(desde: String, hasta: String, texto: String) {
        if (desde > "2027-01-01") nuncaM("2027-01-01", LocalDate.parse(desde).minusDays(1).toString(), texto)
        if (hasta < "2031-12-31") nuncaM(LocalDate.parse(hasta).plusDays(1).toString(), "2031-12-31", texto)
        algunaM(desde, hasta, texto)
    }

    @Test fun estanLasPiezasDeMarguerite() {
        assertEquals(m, piezaM.vida)
        assertEquals("Solo la temporada 1 está escrita", setOf(1), piezaM.temporadasEscritas)
        assertEquals(emptyList<String>(), piezaM.problemas(calM))
        assertEquals(Genero.FEMENINO, piezaM.genero)
        // No es ficción: es una persona inventada en un mundo real.
        assertEquals(Cuento.NOTA_HISTORICA, cuentoM("2027-04-04").nota)
        assertTrue(piezaM.textos().count { "{nombre}" in it } > 40)
        listOf("2027-03-03", "2029-05-10", "2030-09-09", "2031-11-11").forEach { d -> assertTrue(d, "Marguerite" in todoElDiaM(d)) }
        // Los días especiales de la temporada 1, todos, y solo ellos.
        assertEquals(calM.temporadas[0].dias.map { it.id }.toSet(), piezaM.especiales.keys)
    }

    /**
     * Las frases que el cuento copia tal cual, cada una con el autor y la obra que la llevan al lado. Son de las que
     * `docs/diseno/temas/fuentes_nouveau.md` (sección 11) comprobó letra por letra en el texto de la obra, en Wikisource:
     * la 8 (Zola), la 10 (Mallarmé) y la 33 (Baudelaire).
     */
    private val FRASES = mapOf(
        "la vérité est en marche, et rien ne l'arrêtera" to "Zola, J'accuse…!",
        "l'absente de tous bouquets" to "Mallarmé, Avant-dire",
        "Les parfums, les couleurs et les sons se répondent." to "Baudelaire, Correspondances",
    )

    /** Lo que va entre comillas españolas sin ser una cita: el nombre de una cosa, como se escribía. */
    private val NOMBRES = setOf("Métropolitain")

    @Test fun loQueVaTalCualEstaComprobadoYLoDemasEnEstiloIndirecto() {
        val comillas = Regex("""«([^«»]+)»(?: \(([^()]+)\))?""")
        var textuales = 0
        piezaM.textos().forEach { t ->
            assertFalse("Comillas inglesas en «${t.take(60)}…»", '"' in t || '“' in t || '”' in t)
            comillas.findAll(t).forEach { x ->
                val (dentro, fuente) = x.destructured
                if (dentro in FRASES) { assertEquals("«$dentro» sin su autor y su obra al lado", FRASES.getValue(dentro), fuente); textuales++ }
                else assertTrue("«$dentro»: ni es una frase comprobada ni un nombre", dentro in NOMBRES && fuente.isEmpty())
            }
        }
        assertTrue("Hay $textuales citas textuales", textuales in 3..5)
        // Las flores se dicen como las dice Le Langage des fleurs (sección 3), en estilo indirecto: el lila, la primera
        // emoción de amor (p. 205); la margarita de los prados, que lo pensará (p. 205); la violeta, la modestia (p. 210).
        val todo = piezaM.textos().joinToString(" ")
        listOf("el lila, la primera emoción de amor", "la margarita de los prados, que lo pensará", "la violeta, la modestia", "la amapola, el consuelo", "el iris, un mensaje").forEach { assertTrue(it, it in todo) }
    }

    @Test fun laVozDelTemaYNadaFueraDeSuTiempoEnElCuentoDeMarguerite() {
        // Lo que el tema no dice nunca (`docs/diseno/temas/nouveau.md`): el Art déco, la «Belle Époque» de postal, el cabaré de
        // película, Tiffany; y lo que no es de su París: los frascos de Lalique para Coty, el Trianon-Concert que ardió.
        val prohibidas = listOf("Belle Époque", "art déco", "déco", "cancán", "Tiffany", "Moulin-Rouge", "Moulin Rouge", "Lautrec", "Coty", "Trianon", "Lalique")
        piezaM.textos().forEach { t -> prohibidas.forEach { assertFalse("«$it» en «${t.take(60)}…»", Regex("""(?U)\b${Regex.escape(it)}\b""", RegexOption.IGNORE_CASE).containsMatchIn(t)) } }
        // Le Chat Noir solo como lo que ya cerró; la torre del Campo de Marte, de lejos y una vez.
        piezaM.textos().filter { "Chat Noir" in it }.forEach { assertTrue(it, "cerró" in it) }
        assertTrue(piezaM.textos().count { "torre de hierro" in it } <= 1)
        // Los cafés-conciertos son los que estaban abiertos (corrección 10).
        assertTrue(piezaM.textos().any { "La Cigale" in it }); assertTrue(piezaM.textos().any { "Divan japonais" in it })
    }

    @Test fun elMetroSoloDesdeQueAbreYNadieApareceAntesDeTiempo() {
        // El camino: a pie hasta el 19 de julio de 1900, por las obras desde octubre de 1898, y en el metro desde que abre.
        soloEntreM("2027-01-01", "2029-07-18", "media hora larga de adoquines")
        soloEntreM("2027-10-04", "2029-07-18", "pasa junto a las vallas de las obras del metro")
        soloEntreM("2029-07-19", "2031-12-31", "quince céntimos de segunda")
        soloEntreM("2029-07-19", "2031-12-31", "sale del metro con los ojos acostumbrados")
        soloEntreM("2029-07-19", "2031-12-31", "París tiene ahora dos pisos")
        // La entrada delante del taller (el arco `metro`): nada, las obras al fondo, en la esquina, la barandilla y los faroles.
        soloEntreM("2027-01-01", "2027-10-03", "Del metro todavía no se ve nada")
        soloEntreM("2027-10-04", "2028-10-09", "Al fondo de la calle se ven las vallas")
        soloEntreM("2028-10-10", "2029-07-18", "En la esquina están las vallas del metro")
        soloEntreM("2029-07-19", "2030-03-17", "barandilla verde que se curva como un tallo")
        soloEntreM("2030-03-18", "2031-12-31", "dos candelabros sostienen sus faroles")
        // El oficio, que avanza.
        soloEntreM("2027-02-21", "2031-12-31", "grania una piedra vieja")
        soloEntreM("2027-12-10", "2031-12-31", "moja la piedra con la esponja")
        soloEntreM("2028-02-27", "2031-12-31", "mete los pliegos en la prensa, uno por uno")
        soloEntreM("2028-08-21", "2031-12-31", "atiende la prensa de vapor")
        soloEntreM("2030-01-21", "2031-11-17", "dibuja letras en una piedra pequeña")
        soloEntreM("2031-02-03", "2031-12-31", "prepara ella misma una piedra")
        soloEntreM("2028-12-05", "2031-12-31", "Desde que tiene el cuaderno de tapas de tela")
        // Berthe: soltera, con novio, casada y con una niña.
        soloEntreM("2027-01-01", "2028-03-18", "el pelo rojo y la lengua rápida")
        soloEntreM("2028-03-19", "2029-02-16", "Berthe tiene novio, Lucien")
        soloEntreM("2029-02-17", "2031-06-01", "Berthe se casó con Lucien")
        soloEntreM("2031-06-02", "2031-12-31", "Berthe tiene una niña, Louise")
        // Paul, en la escuela y luego en el taller de su padre.
        soloEntreM("2027-01-01", "2029-03-04", "va todavía a la escuela del barrio")
        soloEntreM("2029-03-05", "2031-12-31", "Paul ha dejado la escuela")
        // Henri: nadie lo nombra antes de las lilas; luego, el permiso; luego, el sí.
        nuncaM("2027-01-01", "2030-05-11", "Henri")
        soloEntreM("2030-05-12", "2030-12-07", "compone libros letra por letra")
        soloEntreM("2030-12-08", "2031-12-15", "Henri ha pedido permiso a su padre")
        soloEntreM("2031-12-16", "2031-12-31", "Henri y ella se casarán")
        // Anselme, en el taller hasta que se retira; el Divan japonais, mientras es café-concierto.
        soloEntreM("2027-01-01", "2031-11-17", "dibuja las letras de los carteles desde antes")
        soloEntreM("2031-11-18", "2031-12-31", "Anselme ya no viene al taller")
        soloEntreM("2028-06-24", "2030-08-09", "Divan japonais de la rue des Martyrs")
    }

    @Test fun duranteLaExposicionSeNotaYFueraNo() {
        val pueblo = listOf("París está lleno de gente de fuera", "carteles de la Exposición y de sus pabellones", "en el centro no se cabe", "pabellones de cuarenta países")
        val anochecer = listOf("amanecer equivocado", "a ver encenderse la Exposición", "París no se acaba de dormir", "plano de la Exposición")
        val noche = listOf("ver encendida la Exposición", "llenas de extranjeros", "del lado de la Exposición")
        val vistos = normalesM("2029-04-14", "2029-11-11")
        assertTrue(vistos.size > 150)
        vistos.forEach { d ->
            assertEquals(d, "exposicion", cuentoM(d).ficha.estado)
            val c = cuentoM(d, 22.0)
            assertTrue("$d: el pueblo es el de siempre", pueblo.any { it in c.parrafos[Parrafo.PUEBLO.ordinal] })
            assertTrue("$d: el anochecer es el de siempre", anochecer.any { it in c.parrafos[Parrafo.ANOCHECER.ordinal] })
            assertTrue("$d: la noche es la de siempre", noche.any { it in c.parrafos[Parrafo.TRABAJO.ordinal] })
        }
        // Cambian de un día a otro, y fuera de la Exposición no sale ninguna.
        assertTrue(vistos.map { cuentoM(it, 22.0).parrafos[Parrafo.PUEBLO.ordinal] }.toSet().size >= 4)
        (normalesM("2027-01-01", "2029-04-13") + normalesM("2029-11-13", "2031-12-31")).forEach { d ->
            (pueblo + anochecer + noche).forEach { assertFalse("$d: «$it» fuera de la Exposición", it in todoElDiaM(d)) }
        }
    }

    @Test fun noTrabajaElDiaDeAnioNuevoNiTiraElDiaDelInventario() {
        (2027..2031).forEach { y -> listOf(9.0, 15.0).forEach { h ->
            assertTrue("$y-01-01, $h h", "Hoy no se trabaja" in cuentoM("$y-01-01", h).parrafos[Parrafo.TRABAJO.ordinal])
            assertTrue("$y-12-29, $h h", "Hoy no se tira nada" in cuentoM("$y-12-29", h).parrafos[Parrafo.TRABAJO.ordinal])
        } }
        // El día de J'accuse…!, la frase de Zola, con su obra.
        val zola = cuentoM("2027-01-13")
        assertEquals("Zola publica «J'accuse…!» en L'Aurore, y en el taller no se habla de otra cosa", zola.titulo)
        assertTrue("«la vérité est en marche, et rien ne l'arrêtera» (Zola, J'accuse…!)" in zola.parrafos[Parrafo.GENTE.ordinal])
        assertEquals(CertezaDia.HISTORICO, zola.notas.first().certeza)
    }

    // ================================================================= David, el dibujante de carteles de Psicodelia

    private val dv = "cartelista"
    private val piezaD get() = TodasLasPiezas.de(dv)!!
    private val calD get() = Calendarios.de(dv)!!
    private val david get() = OtrasVidas.porId(dv)!!
    private fun cuentoD(dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, david, calD), david, calD, piezaD, hora)
    private val diaEnteroD = HashMap<String, String>()
    /** Todo lo que dice su cuento ese día, a una hora de cada tramo de «Un día suyo». */
    private fun todoElDiaD(dia: String) = diaEnteroD.getOrPut(dia) { listOf(6.0, 12.5, 15.0, 18.5, 19.5, 22.0, 3.0).flatMap { cuentoD(dia, it).parrafos }.joinToString(" ") }
    private val normalesD = HashMap<String, Boolean>()
    private fun normalesD(desde: String, hasta: String) = dias(desde, hasta).filter { d -> normalesD.getOrPut(d) { !cuentoD(d).especial } }
    private fun nuncaD(desde: String, hasta: String, texto: String) =
        normalesD(desde, hasta).forEach { assertFalse("$it: «$texto» fuera de tiempo", texto in todoElDiaD(it)) }
    private fun algunaD(desde: String, hasta: String, texto: String) =
        assertTrue("«$texto» no sale entre $desde y $hasta", normalesD(desde, hasta).any { texto in todoElDiaD(it) })
    private fun soloEntreD(desde: String, hasta: String, texto: String) {
        if (desde > "2027-01-01") nuncaD("2027-01-01", LocalDate.parse(desde).minusDays(1).toString(), texto)
        if (hasta < "2031-12-31") nuncaD(LocalDate.parse(hasta).plusDays(1).toString(), "2031-12-31", texto)
        algunaD(desde, hasta, texto)
    }
    private fun textosDeD(lugar: Parrafo, estado: String) =
        piezaD.variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to david.nombre), piezaD.genero) }.toSet()

    @Test fun estanLasPiezasDeDavid() {
        assertEquals(dv, piezaD.vida)
        assertEquals("Solo la temporada 1 está escrita", setOf(1), piezaD.temporadasEscritas)
        assertEquals(emptyList<String>(), piezaD.problemas(calD))
        assertEquals(Genero.MASCULINO, piezaD.genero)
        // No es ficción: es una persona inventada en un mundo real.
        assertEquals(Cuento.NOTA_HISTORICA, cuentoD("2027-04-04").nota)
        assertTrue(piezaD.textos().count { "{nombre}" in it } > 40)
        listOf("2027-03-03", "2029-05-10", "2030-09-09", "2031-11-11").forEach { d -> assertTrue(d, "David" in todoElDiaD(d)) }
        // Los días especiales de la temporada 1, todos, y solo ellos.
        assertEquals(calD.temporadas[0].dias.map { it.id }.toSet(), piezaD.especiales.keys)
        // Unas 60 a 80 variantes de todos los días, más las de los tres días de Monterey.
        val variantes = piezaD.variantes.values.flatten()
        assertTrue("${variantes.count { it.estado == null }} variantes", variantes.count { it.estado == null } in 60..110)
        assertTrue(variantes.count { it.estado == "monterey" } >= 10)
    }

    @Test fun nadaDeDrogasEnElCuento() {
        // La regla del tema, la más importante: ninguna palabra de la lista, en ningún texto, ni en los cuentos ya armados.
        piezaD.textos().forEach { t -> JERGA_DE_DROGAS.forEach { r -> assertFalse("«${r.find(t)?.value}» en «${t.take(70)}…»", r.containsMatchIn(t)) } }
        (dias("2027-01-01", "2027-12-31") + dias("2029-06-01", "2029-06-30")).forEach { d ->
            val c = cuentoD(d, 22.0)
            (c.parrafos + c.titulo).forEach { t -> JERGA_DE_DROGAS.forEach { r -> assertFalse("$d: «${r.find(t)?.value}» en «${t.take(70)}…»", r.containsMatchIn(t)) } }
        }
        // Ni las marcas de las salas, la radio, el festival o el periódico (corrección 6), ni el nombre de 1978 de las casas.
        piezaD.textos().forEach { t -> MARCAS_DE_PSICODELIA.forEach { r -> assertFalse("«${r.find(t)?.value}» en «${t.take(70)}…»", r.containsMatchIn(t)) } }
        // Las casas, casi todas grises (corrección 1): nada de calles enteras de colores.
        piezaD.textos().forEach { t -> assertFalse(t, Regex("""(?i)calles? enteras?|todas las casas (son|están) (pintadas|de colores)""").containsMatchIn(t)) }
        assertTrue(piezaD.textos().any { "Casi todas son grises" in it })
    }

    /**
     * Las frases que el cuento copia tal cual, en inglés, cada una con su autor y su obra al lado y su español después. Son
     * de las 33 que `docs/diseno/temas/fuentes_psicodelia.md` (sección 10) comprobó letra por letra en Wikisource: la 18
     * (Thoreau), la 14 (Whitman) y la 26 (Gibran).
     */
    private val FRASES_D = mapOf(
        "If a man does not keep pace with his companions, perhaps it is because he hears a different drummer." to "Thoreau, Walden",
        "I sound my barbaric yawp over the roofs of the world." to "Whitman, Leaves of Grass",
        "Work is love made visible." to "Gibran, The Prophet",
    )

    @Test fun loQueVaTalCualEstaComprobadoYLoDemasEnEstiloIndirectoEnElCuentoDeDavid() {
        val comillas = Regex("""«([^«»]+)»(?: \(([^()]+)\))?""")
        var textuales = 0
        piezaD.textos().forEach { t ->
            assertFalse("Comillas inglesas en «${t.take(60)}…»", '"' in t || '“' in t || '”' in t)
            comillas.findAll(t).forEach { x ->
                val (dentro, fuente) = x.destructured
                assertTrue("«$dentro»: no es una frase comprobada", dentro in FRASES_D)
                assertEquals("«$dentro» sin su autor y su obra al lado", FRASES_D.getValue(dentro), fuente)
                textuales++
            }
        }
        assertEquals(3, textuales)
        // Cada una lleva su español al lado (principio 7), en estilo indirecto.
        val todo = piezaD.textos().joinToString(" ")
        listOf("quizá oye otro tambor", "lanza su grito bárbaro sobre los tejados del mundo", "el trabajo es el amor hecho visible").forEach { assertTrue(it, it in todo) }
        // Sin nombres de bandas ni de canciones: las dos canciones de la radio se describen.
        assertTrue(piezaD.especiales.getValue("distorsion").dias[0].values.any { "una canción nueva con una guitarra" in it })
        assertTrue(piezaD.especiales.getValue("sitar").dias[0].values.any { "un sitar, un instrumento de la India" in it })
    }

    @Test fun nadieApareceAntesNiDespuesDeTiempoEnElCuentoDeDavid() {
        // El trabajo: la imprenta hasta su primer cartel; el cartel de la semana desde entonces; las tintas fluorescentes y los pinceles de Sal.
        soloEntreD("2027-01-11", "2028-05-06", "trabaja en la imprenta del señor Okada")
        soloEntreD("2028-05-07", "2031-12-31", "dibuja el cartel de la semana")
        soloEntreD("2029-05-27", "2031-12-31", "pensando en las tintas fluorescentes")
        soloEntreD("2031-02-22", "2031-12-31", "pinta con los pinceles del viejo Sal")
        // Las noches: el sótano de la calle Page, la sala de la calle Sutter, el auditorio de Geary, la FM del 106.9, el tocadiscos.
        soloEntreD("2027-04-24", "2028-02-18", "baja al sótano de la calle Page")
        soloEntreD("2028-02-19", "2031-01-24", "está en la sala de la calle Sutter")
        soloEntreD("2028-05-07", "2030-07-05", "está en el auditorio de Geary")
        soloEntreD("2031-01-25", "2031-12-31", "va a las salas que quedan")
        soloEntreD("2029-02-12", "2031-12-31", "A esta hora oye el 106.9")
        soloEntreD("2027-10-02", "2031-12-31", "pone un disco en el tocadiscos de maleta")
        // El pasaje: quince centavos hasta el 15 de marzo de 1969; veinte desde entonces.
        soloEntreD("2027-01-01", "2031-03-14", "echa sus quince centavos")
        soloEntreD("2031-03-15", "2031-12-31", "los veinte centavos que cuesta ahora el pasaje")
        // El periódico del barrio y el guiso de los Diggers, mientras existen.
        soloEntreD("2028-09-20", "2030-02-09", "venden en la calle el periódico del barrio")
        soloEntreD("2029-05-13", "2030-10-11", "lo que sobró del guiso de los Diggers")
        // Su gente: Nora, Gil y su órgano, Sal y su retiro, el señor Okada, Ellen.
        soloEntreD("2027-01-01", "2030-01-19", "Nora, la otra del departamento")
        soloEntreD("2027-01-01", "2031-04-04", "Gil Ortega comparte el departamento")
        soloEntreD("2028-01-15", "2031-04-04", "Gil ensaya en la sala")
        soloEntreD("2027-02-15", "2031-02-21", "El viejo Sal es rotulista")
        soloEntreD("2031-02-22", "2031-12-31", "El viejo Sal ya no tiene taller")
        soloEntreD("2027-01-11", "2031-12-31", "El señor Okada, el impresor")
        nuncaD("2027-01-01", "2028-06-17", "Ellen")
        soloEntreD("2028-06-18", "2029-02-18", "Ellen trabaja en las luces de las salas")
        soloEntreD("2029-02-19", "2030-03-01", "Ellen y él salen juntos")
        soloEntreD("2030-03-02", "2031-12-31", "Ellen vive ahora en el departamento")
        soloEntreD("2030-03-02", "2031-12-31", "duerme junto a Ellen")
        // La luz negra de su ventana y la casa turquesa.
        soloEntreD("2030-04-13", "2031-12-31", "la ventana de su cuarto brilla de un violeta raro")
        soloEntreD("2031-05-10", "2031-12-31", "La casa de la señora Petrakis es ahora turquesa")
        // Los carteles de los postes (el arco `carteles`): ninguno, alguno, llenos y cubiertos.
        soloEntreD("2027-01-01", "2028-02-18", "solo hay avisos de cuartos en alquiler")
        // Llenos del todo solo en el verano del amor: del 2 de mayo, cuando el arco pasa de 0.9, al entierro del 6 de octubre.
        nuncaD("2027-01-01", "2029-05-01", "como una enredadera de colores"); nuncaD("2029-10-06", "2031-12-31", "como una enredadera de colores")
        algunaD("2029-06-01", "2029-10-05", "como una enredadera de colores")
    }

    @Test fun enMontereySeNotaElEstadoYEnLaCalleNo() {
        val manana = textosDeD(Parrafo.MANANA, "monterey"); val trabajo = textosDeD(Parrafo.TRABAJO, "monterey")
        val pueblo = textosDeD(Parrafo.PUEBLO, "monterey"); val anochecer = textosDeD(Parrafo.ANOCHECER, "monterey")
        listOf("2029-06-16", "2029-06-17", "2029-06-18").forEach { dia ->
            val c = cuentoD(dia, 15.0)
            assertEquals(dia, "monterey", c.ficha.estado)
            // Mientras la temporada 3 no tenga sus especiales, todo lo que no sea del día sale de las variantes del festival.
            assertTrue("$dia: la mañana es la de siempre", c.parrafos[Parrafo.MANANA.ordinal] in manana)
            assertTrue("$dia: el pueblo es el de siempre", c.parrafos[Parrafo.PUEBLO.ordinal] in pueblo)
            assertTrue("$dia: el anochecer es el de siempre", c.parrafos[Parrafo.ANOCHECER.ordinal] in anochecer)
            listOf(5.0, 12.5, 15.0, 19.5, 22.0, 3.0).forEach { h -> assertTrue("$dia, $h h: el trabajo es el de siempre", cuentoD(dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
        }
        // Y fuera del festival no sale ninguna.
        val deEstado = piezaD.variantes.values.flatten().filter { it.estado == "monterey" }.map { Cuento.llenar(it.texto, mapOf("nombre" to david.nombre), piezaD.genero) }.toSet()
        (normalesD("2029-05-01", "2029-06-15") + normalesD("2029-06-19", "2029-08-31")).forEach { d -> cuentoD(d, 15.0).parrafos.forEach { assertFalse("$d: Monterey en casa", it in deEstado) } }
    }

    @Test fun noDibujaEnNavidadNiEnAccionDeGraciasYLlegaElPrimerDia() {
        (2027..2031).forEach { y -> listOf(5.0, 15.0, 22.0).forEach { h ->
            assertTrue("$y-12-25, $h h", "Hoy no dibuja: es Navidad" in cuentoD("$y-12-25", h).parrafos[Parrafo.TRABAJO.ordinal])
        } }
        listOf("2027-11-25", "2028-11-24", "2029-11-23", "2030-11-28", "2031-11-27").forEach { d ->
            assertTrue(d, "Hoy no dibuja: es el día de Acción de Gracias" in cuentoD(d, 15.0).parrafos[Parrafo.TRABAJO.ordinal])
        }
        // El primer día llega a la ciudad; el día del baile de Graham, la gente real sale de lejos y con su fuente.
        val llega = cuentoD("2027-01-01", 15.0)
        assertEquals("Llega a San Francisco en el autobús de Stockton, con una maleta y una carpeta de dibujos", llega.titulo)
        assertTrue("Hoy no trabaja: busca dónde vivir" in llega.parrafos[Parrafo.TRABAJO.ordinal])
        val graham = cuentoD("2027-12-10")
        assertEquals(CertezaDia.HISTORICO, graham.notas.first().certeza)
        assertEquals("https://en.wikipedia.org/wiki/Bill_Graham_(promoter)", graham.notas.first().fuente!!.second)
        assertTrue("un tal Bill Graham" in graham.parrafos[Parrafo.GENTE.ordinal])
    }
}
