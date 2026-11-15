package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG9
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Los calendarios del grupo 9, los de los temas del lote 10: el de Tadeo, el agente de la red de Frente exterior, que es
 * ficción, y el de Eleazar, el iniciado de Arcontes, una persona inventada en la Galilea real del 28 al 32. Las pruebas
 * generales (forma, certeza, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en
 * [Calendarios]; aquí va lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG9Prueba {
    private val c = AnioG9.AGENTE
    private fun ficha(dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun arco(dia: String, id: String) = ficha(dia).arcos.getValue(id)
    private fun estadoEntre(clave: String, desde: String, hasta: String) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("el día antes de $desde", ficha(a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("$d", clave, ficha(d.toString()).estado); d = d.plusDays(1) }
        assertTrue("el día después de $hasta", ficha(b.plusDays(1).toString()).estado != clave)
    }
    /** Cuántos días del año [anio] está en el estado [clave]. */
    private fun diasEn(clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { ficha(it.toString()).estado == clave }

    @Test fun estaTadeoConSuTemaYEsFiccion() {
        assertEquals(listOf("agente_red", "iniciado"), AnioG9.TODOS.map { it.vida })
        assertTrue("No está en el registro", Calendarios.de(c.vida) === c)
        val q = OtrasVidas.porId(c.vida)!!
        assertEquals("frente", q.tema); assertEquals("tema", q.dibujo); assertEquals("Tadeo", q.nombre); assertEquals(32, q.edad)
        assertTrue(q.ficticia)
        assertTrue(q.inspiracion.startsWith("Inspirado en Andor, de Star Wars"))
        // Sus números son de ficción: todos inventados, y están los 24.
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.INVENTADO })
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isEmpty())
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10c): duerme de 2 a 9 y la ventana es de 23 a 2.
        val f = ficha("2027-04-04")
        assertEquals(OtrasVidas.Hacer.DORMIR, f.tramo(5.0)!!.que); assertEquals(OtrasVidas.Hacer.TRABAJO, f.tramo(0.5)!!.que)
        assertEquals("la ventana de transmisión y la misión de la noche", f.tramo(23.5)!!.texto)
        assertEquals(OtrasVidas.Hacer.GENTE, f.tramo(21.0)!!.que)
        // Sin época: es de otro mundo, y sus años son los del Directorio.
        assertNull(Epocas.de(q))
        assertEquals("año 212 del Directorio", ficha("2027-03-03").cuando); assertEquals("año 216 del Directorio", ficha("2031-03-03").cuando)
        assertEquals(36, ficha("2031-03-03").edad)
    }

    @Test fun nadaSaleDeLaSaga() {
        // Ni nombres de la saga ni las palabras que la voz del tema no dice nunca (docs/diseno/temas/frente.md).
        val prohibidas = listOf("imperio", "imperial", "rebelde", "rebelión", "alianza", "la fuerza", "jedi", "sith", "galaxia muy lejana", "andor", "ferrix", "coruscant",
            "aldhani", "kenari", "narkina", "ghorman", "cassian", "luthen", "mon mothma", "bix", "syril", "dedra", "maarva", "soldado de asalto", "bláster", "droide", "hiperespacio")
        val q = OtrasVidas.porId(c.vida)!!
        val textos = c.temporadas.flatMap { t -> listOf(t.cuando, t.resumen) + t.dias.map { it.titulo } } + q.historia + listOf(q.nombre, q.rol, q.lugar, q.cuando) + q.dia.map { it.texto } + q.datos.values.map { it.nota }
        textos.forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", Regex("""\b$it\b""").containsMatchIn(s.lowercase())) } }
    }

    @Test fun laRedAvanzaDeTemporadaEnTemporada() {
        assertEquals(listOf("cuota", "patrullas", "caida", "ocupacion", "golpe"), c.temporadas.map { it.mundo })
        // El reclutamiento, las primeras misiones, la caída, la fuga y el golpe, cada uno en su año.
        assertEquals(listOf("02-24", null, null, null, null), fechas("recluta"))
        assertEquals(listOf("12-06", null, null, null, null), fechas("juramento"))
        assertEquals(listOf(null, "07-14", null, null, null), fechas("sube_cresta"))
        assertEquals(listOf(null, null, "10-02", null, null), fechas("redada"))
        assertEquals(listOf(null, null, null, "07-09", null), fechas("fuga"))
        assertEquals(listOf(null, null, null, null, "06-29"), fechas("golpe"))
        // Lo que vuelve cada año está en las cinco; en la temporada 3 el censo lo pilla escondido y lleva otro id.
        listOf("cambio_anio", "cuota", "casco_nuevo", "lamparas", "concordia", "convoy", "feria", "caidos", "carbon", "noche_larga").forEach { id ->
            assertTrue(id, fechas(id).all { it != null })
        }
        assertEquals(listOf("10-15", "10-15", null, "10-15", "10-15"), fechas("censo"))
        assertEquals(listOf(null, null, "10-15", null, null), fechas("censo_escondido"))
        // En la temporada 5 los sucesos de su vida no cuelgan de los días del golpe, que no se mueven: no hay cadenas.
        assertTrue(c.temporadas[4].dias.none { it.cadena != null })
        assertTrue(c.temporadas[4].dias.filter { it.id in setOf("sube_golpe", "golpe") }.all { it.tipo == TipoDia.GUERRA })
    }

    @Test fun subeALaCrestaDosVecesYSeEscondeEnLaMina() {
        estadoEntre("cresta", "2028-07-14", "2028-07-17")
        estadoEntre("escondido", "2029-10-02", "2029-10-29")
        estadoEntre("cresta", "2031-06-26", "2031-06-28")
        assertEquals(0, diasEn("cresta", 2027)); assertEquals(0, diasEn("escondido", 2027)); assertEquals(0, diasEn("escondido", 2030))
        // El censo de la temporada 3 cae mientras está escondido.
        assertEquals("escondido", ficha("2029-10-15").estado); assertEquals("censo_escondido", ficha("2029-10-15").principal!!.dia.id)
        // Cuando la temporada 5 se repite, los días del golpe no se mueven: siempre son tres días en la Cresta.
        (2032..2036).forEach { y -> assertEquals("$y", 3, diasEn("cresta", y)); assertEquals("cresta", ficha("$y-06-27").estado) }
    }

    @Test fun elCuadroPuedeContarLoMismoQueElCuento() {
        // El casco nuevo llega entero cada 10 de marzo y el desguace se lo come a lo largo del año.
        (2027..2031).forEach { y ->
            assertTrue(arco("$y-03-09", "casco") < 0.05); assertEquals(1.0, arco("$y-03-10", "casco"), 0.0)
            assertTrue(arco("$y-09-01", "casco") < arco("$y-04-01", "casco"))
            assertEquals("casco", ficha("$y-03-10").escena)
        }
        // La estación de radar se enciende en la temporada 2 y se queda ciega con el golpe.
        assertEquals(0.0, arco("2028-04-29", "radar"), 0.0); assertEquals(1.0, arco("2028-04-30", "radar"), 0.0)
        assertEquals(1.0, arco("2031-06-28", "radar"), 0.0); assertEquals(0.5, arco("2031-06-29", "radar"), 0.0)
        // La relojería, entera hasta la redada, quemada hasta que Lis la abre, y reabierta.
        assertEquals(0.0, arco("2029-10-01", "relojeria"), 0.0); assertEquals(1.0, arco("2029-10-02", "relojeria"), 0.0)
        assertEquals(1.0, arco("2031-09-09", "relojeria"), 0.0); assertEquals(0.3, arco("2031-09-10", "relojeria"), 1e-9)
        assertEquals("humo", ficha("2029-10-02").escena)
        // El crucero de anillo, sobre el puerto de la fuga al golpe.
        assertEquals(0.0, arco("2030-07-11", "crucero"), 0.0); assertEquals(1.0, arco("2030-07-12", "crucero"), 0.0)
        assertEquals(1.0, arco("2031-07-02", "crucero"), 0.0); assertEquals(0.0, arco("2031-07-03", "crucero"), 0.0)
        assertEquals(0.0, arco("2027-12-31", "crucero"), 0.0); assertEquals(0.0, arco("2031-12-31", "crucero"), 0.0)
        // La ocupación sube con Morell, con los controles, con la redada y con el crucero, y baja después del golpe.
        val ocupa = listOf("2027-06-01", "2029-03-15", "2029-06-01", "2029-12-01", "2030-08-01").map { arco(it, "ocupacion") }
        assertEquals("La ocupación no sube", ocupa.sorted(), ocupa)
        assertTrue(arco("2031-08-01", "ocupacion") < arco("2027-06-01", "ocupacion"))
        // La chispa aparece pintada en el muelle en la temporada 1, la Oficina la borra con la redada y vuelve más que nunca con el golpe.
        assertEquals(0.0, arco("2027-10-27", "chispa"), 0.0); assertTrue(arco("2027-10-28", "chispa") > 0.0); assertEquals("pintadas", ficha("2027-10-28").escena)
        assertTrue(arco("2029-10-02", "chispa") < arco("2029-10-01", "chispa"))
        assertEquals(1.0, arco("2031-07-03", "chispa"), 0.0)
        // Los días que cambian lo que se ve.
        assertEquals("desfile", ficha("2027-06-12").escena); assertEquals("golpe", ficha("2031-06-29").escena)
        assertEquals("toque", ficha("2028-09-10").escena); assertEquals("redada", ficha("2030-07-15").escena)
    }

    // ================================================================= Eleazar, el iniciado de Arcontes

    private val ini = AnioG9.INICIADO
    private val eleazar get() = OtrasVidas.porId(ini.vida)!!
    private fun fichaE(dia: String) = FichaDelDia.de(dia, eleazar, ini)
    private fun fechasE(id: String) = ini.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun diaE(id: String) = ini.temporadas.flatMap { it.dias }.first { it.id == id }
    private fun arcoE(dia: String, id: String) = fichaE(dia).arcos.getValue(id)
    private fun estadoEntreE(clave: String, desde: String, hasta: String) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("el día antes de $desde", fichaE(a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("$d", clave, fichaE(d.toString()).estado); d = d.plusDays(1) }
        assertTrue("el día después de $hasta", fichaE(b.plusDays(1).toString()).estado != clave)
    }

    /** El día de la semana de una fecha del calendario juliano, por su número de día juliano: 0 es lunes y 6, domingo. */
    private fun semanaJuliana(anio: Int, mesDia: String): Int {
        val m = mesDia.take(2).toInt(); val d = mesDia.takeLast(2).toInt()
        val a = (14 - m) / 12; val y = anio + 4800 - a; val mm = m + 12 * a - 3
        return Math.floorMod(d + (153 * mm + 2) / 5 + 365 * y + y / 4 - 32083, 7)
    }

    @Test fun estaEleazarConSuTemaYEsUnaPersonaInventada() {
        assertTrue("No está en el registro", Calendarios.de(ini.vida) === ini)
        val q = eleazar
        assertEquals("arcontes", q.tema); assertEquals("tema", q.dibujo); assertEquals("Eleazar", q.nombre); assertEquals(28, q.edad); assertEquals(28, q.anio)
        // No es ficción: es una persona inventada en un mundo real (PLAN_TEMAS, 10a), con sus números reconstruidos.
        assertFalse(q.ficticia)
        assertEquals("Persona inventada. Los hechos siguen los evangelios y lo que la historia da por probable. Sus creencias son las de los textos gnósticos de los siglos II a IV, puestas a propósito en el siglo I.", q.inspiracion)
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.RECONSTRUIDO })
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isNotEmpty() && q.fuentes.all { it.second.startsWith("https://en.wikipedia.org/wiki/") })
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10d).
        val f = fichaE("2027-04-04")
        listOf(23.0 to OtrasVidas.Hacer.DORMIR, 5.5 to OtrasVidas.Hacer.REZAR, 6.5 to OtrasVidas.Hacer.COMER, 9.0 to OtrasVidas.Hacer.TRABAJO, 12.5 to OtrasVidas.Hacer.COMER,
            15.0 to OtrasVidas.Hacer.TRABAJO, 17.5 to OtrasVidas.Hacer.CAMINO, 18.5 to OtrasVidas.Hacer.COMER, 20.0 to OtrasVidas.Hacer.GENTE).forEach { (h, que) -> assertEquals("$h h", que, f.tramo(h)!!.que) }
        assertEquals("la oración del alba", f.tramo(5.5)!!.texto); assertEquals("la reunión escondida de los que saben", f.tramo(20.0)!!.texto)
        assertEquals("a la orilla, a escuchar al maestro", f.tramo(17.5)!!.texto)
        // Tiene época (el año 28, más o menos treinta) y avanza un año con cada temporada: del 28 al 32.
        assertEquals(-2..58, Epocas.de(q))
        assertEquals((28..32).map { "$it d. C." }, ini.temporadas.map { it.cuando })
        assertEquals(28, fichaE("2027-03-03").anioHistorico); assertEquals(32, fichaE("2031-03-03").anioHistorico); assertEquals(32, fichaE("2031-03-03").edad)
    }

    @Test fun tieneElDobleDeDiasYLaHistoriaAvanzaSinRepetirse() {
        assertEquals(listOf("antipas", "maestro", "pilato", "comunidad", "sanedrin"), ini.temporadas.map { it.mundo })
        ini.temporadas.forEachIndexed { i, t ->
            assertTrue("Temporada ${i + 1}: ${t.dias.size} días", t.dias.size in 35..40)
            // Cada día lleva su año en el id, también las fiestas: ninguno vuelve, y sus textos no se pisan.
            t.dias.forEach { d -> assertTrue("Temporada ${i + 1}: «${d.id}» no lleva su año", d.id.endsWith("_${28 + i}")) }
            assertTrue("Temporada ${i + 1}: pocos días con fuente", t.dias.count { it.fuente != null } >= 15)
            assertTrue("Temporada ${i + 1}: pocos sucesos de su vida", t.dias.count { it.tipo == TipoDia.SUCESO } >= 5)
        }
        // La vida de Jesús (temporadas 1 a 3) y lo que vino después (4 y 5), cada hecho en su año.
        assertEquals(listOf("09-06", null, null, null, null), fechasE("rumor_juan_28"))
        assertEquals(listOf("11-06", null, null, null, null), fechasE("bautismo_28"))
        assertEquals(listOf("12-21", null, null, null, null), fechasE("pescadores_28"))
        assertEquals(listOf(null, "02-22", null, null, null), fechasE("tempestad_29"))
        assertEquals(listOf(null, "04-02", null, null, null), fechasE("muerte_juan_29"))
        assertEquals(listOf(null, "04-11", null, null, null), fechasE("panes_29"))
        assertEquals(listOf(null, null, "04-03", null, null), fechasE("mercaderes_30"))
        assertEquals(listOf(null, null, "04-07", null, null), fechasE("cruz_30"))
        assertEquals(listOf(null, null, null, "03-30", null), fechasE("presos_31"))
        assertEquals(listOf(null, null, null, null, "06-05"), fechasE("esteban_32"))
        assertEquals(listOf(null, null, null, null, "07-06"), fechasE("simon_32"))
        // Juan está preso antes de que el de Nazaret vuelva a Galilea (Marcos 1:14), y muere antes de los panes (Marcos 6).
        assertTrue(MesDia.indice(diaE("carcel_juan_28").fecha) < MesDia.indice(diaE("galilea_28").fecha))
        assertTrue(MesDia.indice(diaE("muerte_juan_29").fecha) < MesDia.indice(diaE("panes_29").fecha))
    }

    @Test fun lasFiestasSonLasDeLaTablaDeLasFuentes() {
        fun f(nombre: String) = ini.temporadas.mapIndexed { i, t -> t.dias.first { it.id == "${nombre}_${28 + i}" } }
        // `docs/diseno/temas/fuentes_arcontes.md`, sección 6. El Purim del 28 cayó el 29 de febrero, que no se puede
        // escribir: va el 1 de marzo, el 15 de adar, el de Jerusalén. En el 29 y en el 32 es el 14 de adar II.
        assertEquals(listOf("03-01", "03-19", "03-08", "02-25", "03-15"), f("purim").map { it.fecha })
        // Pésaj, el 14 de nisán; en el 30 ese día es el de la cruz, y la fiesta va el 15, que cayó en sábado.
        assertEquals(listOf("03-30", "04-18", "04-08", "03-27", "04-13"), f("pesaj").map { it.fecha })
        assertEquals(listOf("05-20", "06-08", "05-28", "05-17", "06-03"), f("shavuot").map { it.fecha })
        assertEquals(listOf("09-09", "09-28", "09-18", "09-07", "09-26"), f("rosh").map { it.fecha })
        assertEquals(listOf("09-18", "10-07", "09-27", "09-16", "10-05"), f("kipur").map { it.fecha })
        assertEquals(listOf("09-23", "10-12", "10-02", "09-21", "10-10"), f("sucot").map { it.fecha })
        assertEquals(listOf("12-01", "12-20", "12-10", "11-30", "12-18"), f("januca").map { it.fecha })
        assertTrue(f("sucot").all { it.duracion == 7 }); assertTrue(f("januca").all { it.duracion == 8 })
        // La primera gavilla, el 16 de nisán.
        assertEquals("04-01", diaE("cebada_28").fecha); assertEquals("04-20", diaE("cebada_29").fecha)
        listOf("purim", "pesaj", "shavuot", "rosh", "kipur", "sucot", "januca").flatMap { f(it) }.forEach { d ->
            assertEquals(d.id, CertezaDia.APROXIMADO, d.certeza); assertNotNull(d.id, d.fuente)
        }
    }

    @Test fun laSemanaDeLaPascuaDelTreintaSigueLaCronologiaDeJuan() {
        val t3 = ini.temporadas[2].dias.associateBy { it.id }
        fun semana(id: String) = semanaJuliana(30, t3.getValue(id).fecha)
        // La crucifixión, el viernes 14 de nisán, 7 de abril (juliano); la cena, el jueves; Pésaj, el sábado.
        assertEquals("04-07", t3.getValue("cruz_30").fecha); assertEquals(4, semana("cruz_30"))
        assertEquals(3, semana("cena_30")); assertEquals(5, semana("pesaj_30"))
        // La entrada y el sepulcro vacío, en domingo; Pentecostés, el domingo 28 de mayo.
        assertEquals(6, semana("entrada_30")); assertEquals(6, semana("sepulcro_30"))
        assertEquals("05-28", t3.getValue("shavuot_30").fecha); assertEquals(6, semana("shavuot_30"))
        // La ascensión, el día cuarenta desde el domingo del sepulcro (Hechos 1:3).
        assertEquals(MesDia.indice("04-09") + 39, MesDia.indice(t3.getValue("cuarenta_30").fecha))
        // Las noticias de Cafarnaúm llegan el día después del sábado (Marcos 1:21–34).
        assertEquals(6, semanaJuliana(28, diaE("cafarnaum_28").fecha))
        // Los mercaderes del Templo, en la última semana, en el 30, y no antes (corrección 1).
        assertEquals(listOf(null, null, "04-03", null, null), fechasE("mercaderes_30"))
        assertTrue(ini.temporadas.take(2).flatMap { it.dias }.none { "mercader" in it.titulo || "cambistas" in it.titulo })
    }

    @Test fun respetaLasCorreccionesDeLasFuentes() {
        // Sejano cae el 18 de octubre del 31 (histórico); la noticia llega entre finales de noviembre y diciembre.
        val sejano = diaE("sejano_31"); assertEquals("10-18", sejano.fecha); assertEquals(CertezaDia.HISTORICO, sejano.certeza); assertNotNull(sejano.fuente)
        val noticia = diaE("noticia_sejano_31"); assertTrue(noticia.fecha, noticia.fecha in "11-20".."12-31"); assertEquals(CertezaDia.APROXIMADO, noticia.certeza)
        // Esteban, aproximado (y adelantado al 32).
        assertEquals(CertezaDia.APROXIMADO, diaE("esteban_32").certeza)
        // Helena no está en Samaria en el 32: no sale en ningún título ni resumen.
        ini.temporadas.forEach { t -> assertFalse("Helena" in t.resumen); t.dias.forEach { assertFalse(it.titulo, "Helena" in it.titulo) } }
        // Lo de su vida es inventado; lo de los evangelios y de Hechos, aproximado y con su cita.
        listOf("bautismo_28", "panes_29", "agua_29", "entrada_30", "sepulcro_30", "orilla_30", "apostoles_31", "dinero_32").forEach { id ->
            assertEquals(id, CertezaDia.APROXIMADO, diaE(id).certeza); assertNotNull(id, diaE(id).fuente)
        }
    }

    @Test fun subeAJerusalenEnLasFiestasYHuyeASamaria() {
        estadoEntreE("jerusalen", "2027-09-12", "2027-10-04")
        estadoEntreE("jerusalen", "2028-10-02", "2028-10-23")
        estadoEntreE("jerusalen", "2029-03-26", "2029-04-18"); estadoEntreE("jerusalen", "2029-05-22", "2029-06-04")
        estadoEntreE("jerusalen", "2030-03-19", "2030-04-07"); estadoEntreE("jerusalen", "2030-09-12", "2030-10-02")
        estadoEntreE("jerusalen", "2031-04-05", "2031-04-23"); estadoEntreE("jerusalen", "2031-05-28", "2031-06-06")
        estadoEntreE("huida", "2031-06-07", "2031-09-25")
        // Las Pascuas del 28 y del 29 las pasa en casa.
        assertEquals(null, fichaE("2027-03-30").estado); assertEquals(null, fichaE("2028-04-18").estado)
        // Desde 2032, cuando la temporada 5 se repite, los viajes y la huida no se mueven: los abren y los cierran ritos,
        // fiestas y sucesos del mundo. Sus sucesos personales van lejos de ellos, en invierno, y no hay cadenas.
        (2032..2036).forEach { y -> assertEquals("$y", "huida", fichaE("$y-07-15").estado); assertEquals("$y", "jerusalen", fichaE("$y-04-13").estado); assertEquals("$y", null, fichaE("$y-02-01").estado) }
        listOf("sube_pesaj_32", "vuelve_pesaj_32", "sube_shavuot_32", "dispersion_32", "rosh_32").forEach { assertNotEquals(it, TipoDia.SUCESO, diaE(it).tipo) }
        ini.temporadas[4].dias.filter { it.tipo == TipoDia.SUCESO }.forEach { d ->
            assertTrue(d.id, MesDia.indice(d.fecha) <= MesDia.indice("02-15") || MesDia.indice(d.fecha) >= MesDia.indice("11-12"))
        }
        assertTrue(ini.temporadas[4].dias.none { it.cadena != null })
    }

    @Test fun elCuadroDeArcontesPuedeContarLoMismoQueElCuento() {
        // La figura de luz, solo los días en que él ve a Jesús, y solo en las temporadas 1 a 3 (decisión 18).
        val conFigura = ini.temporadas.map { t -> t.dias.filter { it.escena == "figura_luz" }.map { it.id } }
        assertEquals(listOf("bautismo_28", "pescadores_28", "lo_ve_28"), conFigura[0])
        assertTrue(conFigura[1].size >= 5); assertTrue("orilla_30" in conFigura[2]); assertTrue(conFigura[3].isEmpty() && conFigura[4].isEmpty())
        listOf("2027-11-06", "2027-12-21", "2028-04-12", "2028-10-15", "2029-04-02", "2029-04-28").forEach { assertEquals(it, "figura_luz", fichaE(it).escena) }
        // Las barcas de la gente que lo sigue: ninguna hasta que vuelve a Galilea, lo más en los panes, ninguna desde la cruz.
        assertEquals(0.0, arcoE("2027-12-15", "barcas"), 0.0); assertTrue(arcoE("2028-04-11", "barcas") > 0.95)
        assertEquals(0.0, arcoE("2029-04-07", "barcas"), 0.0); assertEquals(0.0, arcoE("2031-06-01", "barcas"), 0.0)
        // El círculo se va a la cueva del Arbel en noviembre del 31, y sella sus hojas en la jarra en octubre del 32.
        assertEquals(0.0, arcoE("2030-11-03", "cueva"), 0.0); assertEquals(1.0, arcoE("2030-11-04", "cueva"), 0.0); assertEquals(1.0, arcoE("2031-12-31", "cueva"), 0.0)
        assertEquals(0.0, arcoE("2031-10-27", "jarra"), 0.0); assertEquals(1.0, arcoE("2031-10-28", "jarra"), 0.0)
        val codice = listOf("2027-01-01", "2028-06-01", "2029-06-01", "2030-06-01", "2031-10-01").map { arcoE(it, "codice") }
        assertEquals("Las hojas del códice no crecen", codice.sorted(), codice); assertTrue(codice.last() - codice.first() > 0.6)
        // Más pescado tendido en invierno que en verano, todos los años.
        (2027..2031).forEach { y -> assertTrue("$y", arcoE("$y-02-20", "secadero") > arcoE("$y-07-15", "secadero") + 0.5) }
        // Los días que cambian lo que se ve.
        assertEquals("camino", fichaE("2027-09-13").escena); assertEquals("sucot", fichaE("2027-09-25").escena); assertEquals("lamparas", fichaE("2027-12-03").escena)
        assertEquals("oscuridad", fichaE("2029-04-07").escena); assertEquals("luto", fichaE("2031-06-05").escena); assertEquals("grieta", fichaE("2027-04-14").escena)
    }
}
