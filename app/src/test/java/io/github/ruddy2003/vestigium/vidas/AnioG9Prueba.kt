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
 * ficción; el de Eleazar, el iniciado de Arcontes, una persona inventada en la Galilea real del 28 al 32, y el de
 * Segomaros, el aprendiz de los druidas de Galia, una persona inventada en la Galia real del 54 al 50 a. C.; el de
 * Marguerite, la aprendiz de litografía de Art nouveau, una persona inventada en el París real de 1898 a 1902, y el de
 * David, el dibujante de carteles de Psicodelia, una persona inventada en el San Francisco real de 1965 a 1969. Las pruebas
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
        assertEquals(listOf("agente_red", "iniciado", "aprendiz_druida", "litografa", "cartelista"), AnioG9.TODOS.map { it.vida })
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

    // ================================================================= Segomaros, el aprendiz de los druidas de Galia

    private val apr = AnioG9.APRENDIZ
    private val segomaros get() = OtrasVidas.porId(apr.vida)!!
    private fun fichaS(dia: String) = FichaDelDia.de(dia, segomaros, apr)
    private fun fechasS(id: String) = apr.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun diaS(id: String) = apr.temporadas.flatMap { it.dias }.first { it.id == id }
    private fun arcoS(dia: String, id: String) = fichaS(dia).arcos.getValue(id)
    private fun estadoEntreS(clave: String, desde: String, hasta: String) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("el día antes de $desde", fichaS(a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("$d", clave, fichaS(d.toString()).estado); d = d.plusDays(1) }
        assertTrue("el día después de $hasta", fichaS(b.plusDays(1).toString()).estado != clave)
    }
    private fun diasEnS(clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { fichaS(it.toString()).estado == clave }

    @Test fun estaSegomarosConSuTemaYEsUnaPersonaInventada() {
        assertEquals(listOf("agente_red", "iniciado", "aprendiz_druida", "litografa", "cartelista"), AnioG9.TODOS.map { it.vida })
        assertTrue("No está en el registro", Calendarios.de(apr.vida) === apr)
        val q = segomaros
        assertEquals("galia", q.tema); assertEquals("tema", q.dibujo); assertEquals("Segomaros", q.nombre); assertEquals(19, q.edad); assertEquals(-54, q.anio)
        assertEquals("Aprendiz de los druidas", q.rol)
        // No es ficción: es una persona inventada en un mundo real (PLAN_TEMAS, 10a), con sus números reconstruidos.
        assertFalse(q.ficticia)
        assertEquals("Persona inventada. Su formación sigue lo que César cuenta de los druidas, y su guerra, el levantamiento del 52 a. C.; las cifras son reconstrucciones.", q.inspiracion)
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.RECONSTRUIDO })
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isNotEmpty() && q.fuentes.all { it.second.startsWith("https://en.wikipedia.org/wiki/") })
        // La historia dice que deja la escuela porque los druidas no van a la guerra (fuentes_galia.md, corrección 9).
        assertTrue(q.historia[1].contains("Los druidas no van a la guerra (6.14)"))
        assertTrue(q.historia[1].contains("deja la escuela"))
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10e).
        val f = fichaS("2027-04-04")
        listOf(23.0 to OtrasVidas.Hacer.DORMIR, 5.5 to OtrasVidas.Hacer.CUIDAR, 6.5 to OtrasVidas.Hacer.COMER, 9.0 to OtrasVidas.Hacer.REZAR, 11.5 to OtrasVidas.Hacer.COMER,
            14.0 to OtrasVidas.Hacer.TRABAJO, 17.0 to OtrasVidas.Hacer.CAMINO, 18.5 to OtrasVidas.Hacer.COMER, 20.0 to OtrasVidas.Hacer.GENTE).forEach { (h, que) -> assertEquals("$h h", que, f.tramo(h)!!.que) }
        assertEquals("junto al fuego de la casa grande", f.tramo(2.0)!!.texto); assertEquals("el ganado", f.tramo(5.5)!!.texto)
        assertEquals("aprender de memoria los versos de los druidas", f.tramo(9.0)!!.texto); assertEquals("armas y caballos con los guerreros", f.tramo(14.0)!!.texto)
        assertEquals("llevar mensajes de oppidum en oppidum", f.tramo(17.0)!!.texto); assertEquals("el banquete y los bardos", f.tramo(20.0)!!.texto)
        // Tiene época (el 54 a. C., más o menos treinta) y avanza un año con cada temporada: del 54 al 50 a. C.
        assertEquals(-84..-24, Epocas.de(q))
        assertEquals((54 downTo 50).map { "$it a. C." }, apr.temporadas.map { it.cuando })
        assertEquals(-54, fichaS("2027-03-03").anioHistorico); assertEquals(-52, fichaS("2029-03-03").anioHistorico); assertEquals(-50, fichaS("2031-03-03").anioHistorico)
        assertEquals(21, fichaS("2029-03-03").edad); assertEquals(23, fichaS("2031-03-03").edad)
        assertEquals("52 a. C.", fichaS("2029-09-27").cuando)
    }

    @Test fun unosVeinteDiasPorTemporadaYLaHistoriaAvanza() {
        assertEquals(listOf("escuela", "inquietud", "levantamiento", "resistencia", "conquista"), apr.temporadas.map { it.mundo })
        apr.temporadas.forEachIndexed { i, t ->
            assertTrue("Temporada ${i + 1}: ${t.dias.size} días", t.dias.size in 18..26)
            assertTrue("Temporada ${i + 1}: pocos días con fuente", t.dias.count { it.fuente != null } >= 5)
            assertTrue("Temporada ${i + 1}: pocos sucesos de su vida", t.dias.count { it.tipo == TipoDia.SUCESO } >= 4)
        }
        // La escuela (1), la Galia inquieta (2), el levantamiento (3), las últimas resistencias (4) y la Galia conquistada (5).
        assertEquals(listOf("05-24", "05-20", null, null, "05-22"), fechasS("asamblea"))
        assertEquals(listOf("10-20", null, null, null, null), fechasS("tasgecio"))
        assertEquals(listOf("11-28", null, null, null, null), fechasS("atuatuca"))
        assertEquals(listOf(null, "03-18", null, null, null), fechasS("cortes"))
        assertEquals(listOf(null, "10-18", null, null, null), fechasS("acon"))
        assertEquals(listOf(null, null, "01-23", null, null), fechasS("grito"))
        assertEquals(listOf(null, null, "02-16", null, null), fechasS("deja_escuela"))
        assertEquals(listOf(null, null, "05-20", null, null), fechasS("victoria"))
        assertEquals(listOf(null, null, "06-24", null, null), fechasS("bibracte"))
        assertEquals(listOf(null, null, "09-27", null, null), fechasS("entrega"))
        assertEquals(listOf(null, null, null, "08-26", null), fechasS("uxellodunum_cae"))
        assertEquals(listOf(null, null, null, "09-12", null), fechasS("lucterio_entregado"))
        assertEquals(listOf(null, null, null, null, "01-10"), fechasS("vuelve_escuela"))
        assertEquals(listOf(null, null, null, null, "12-02"), fechasS("legiones"))
        // Lo que vuelve casi cada año comparte el id; el año de la guerra lleva otro.
        assertEquals(listOf("11-17", "11-17", null, "11-17", "11-17"), fechasS("samonios"))
        assertEquals(listOf(null, null, "11-17", null, null), fechasS("samonios_52"))
        assertEquals(listOf(null, "07-10", null, "07-10", "07-10"), fechasS("siega"))
        assertEquals(listOf("07-12", null, null, null, null), fechasS("siega_seca"))
        assertEquals(listOf("03-28", "03-26", null, "03-26", "03-26"), fechasS("siembra"))
        // El orden del levantamiento (PLAN_TEMAS, 10e): Cenabum, Gergovia, Avárico, la victoria de Gergovia, Bibracte, Alesia.
        val orden = listOf("grito", "rey", "avarico_cae", "victoria", "bibracte", "alesia", "entrega").map { MesDia.indice(diaS(it).fecha) }
        assertEquals(orden.sorted(), orden)
    }

    @Test fun lasFechasSiguenLaCronologiaYLasCorrecciones() {
        // Casi todo es aproximado: nada va como histórico, porque César da estaciones y no fechas.
        assertTrue(apr.temporadas.flatMap { it.dias }.none { it.certeza == CertezaDia.HISTORICO })
        apr.temporadas.flatMap { it.dias }.forEach { d ->
            if (d.fuente != null) assertEquals(d.id, CertezaDia.APROXIMADO, d.certeza) else assertEquals(d.id, CertezaDia.INVENTADO, d.certeza)
        }
        // El muérdago, el sexto día de la luna (Plinio), con la luna nueva calculada: 11 de enero del 54, 30 de enero del 53,
        // 7 de enero del 51 y 26 de enero del 50. En el 52 cae el mismo día de Cenabum, y no va aparte.
        assertEquals(listOf("01-16", "02-04", null, null, "01-31"), fechasS("muerdago"))
        assertEquals(listOf(null, null, null, "01-12", null), fechasS("muerdago_lejos"))
        // Cenabum: el 23 de enero de Carcopino (Wikipedia en francés), con la duda de la corrección 17; la entrega, el 27 de
        // septiembre (Brunaux).
        assertEquals("https://fr.wikipedia.org/wiki/Guerre_des_Gaules", diaS("grito").fuente!!.second)
        assertEquals("https://en.wikipedia.org/wiki/Battle_of_Alesia", diaS("entrega").fuente!!.second)
        // Bibracte, sin decir que fue toda la Galia (corrección 4).
        assertFalse(diaS("bibracte").titulo.contains("toda la Galia"))
        // Uxellodunum y Lucterio, del libro 8 de Aulo Hircio, comprobados en Wikipedia.
        assertEquals("https://en.wikipedia.org/wiki/Siege_of_Uxellodunum", diaS("uxellodunum").fuente!!.second)
        assertEquals("https://en.wikipedia.org/wiki/Lucterius", diaS("lucterio_entregado").fuente!!.second)
        // Los dioses y lo que el tema no usa nunca (`docs/diseno/temas/galia.md`): ni mitos irlandeses ni galeses, ni dioses
        // de época romana, ni druidismo moderno, ni «bárbaro».
        val prohibidas = listOf("Cernunnos", "Epona", "Sucellus", "Belenus", "Lugus", "Samhain", "Beltane", "Beltaine", "ogham", "Stonehenge", "bárbaro", "bárbaros", "magia", "mago", "hechizo", "druidesa")
        val textos = apr.temporadas.flatMap { t -> listOf(t.cuando, t.resumen) + t.dias.map { it.titulo } } + segomaros.historia + segomaros.dia.map { it.texto } + segomaros.datos.values.map { it.nota }
        textos.forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", Regex("""(?U)\b$it\b""", RegexOption.IGNORE_CASE).containsMatchIn(s)) } }
    }

    @Test fun vaConLosJinetesSeEncierraEnAlesiaYVuelveCautivo() {
        estadoEntreS("campana", "2029-03-02", "2029-04-29")
        estadoEntreS("campana", "2029-06-14", "2029-08-16")
        estadoEntreS("alesia", "2029-08-17", "2029-09-26")
        estadoEntreS("cautivo", "2029-09-27", "2029-10-07")
        // En Gergovia durante el cerco de César, y de vuelta en casa después de los días de camino.
        assertEquals(null, fichaS("2029-05-10").estado); assertEquals(null, fichaS("2029-10-20").estado)
        assertEquals("camino", fichaS("2029-10-10").escena)
        // Los otros años, en casa.
        listOf(2027, 2028, 2030, 2031, 2032).forEach { y -> listOf("campana", "alesia", "cautivo").forEach { assertEquals("$y, $it", 0, diasEnS(it, y)) } }
        // En la temporada 5, que desde 2032 se repite, lo que no debe moverse no es un suceso, y no hay cadenas.
        listOf("vuelve_escuela", "aniversario_gergovia", "aniversario_alesia", "parte_maestro", "vuelve_maestro").forEach { assertNotEquals(it, TipoDia.SUCESO, diaS(it).tipo) }
        assertTrue(apr.temporadas[4].dias.none { it.cadena != null })
    }

    @Test fun elCuadroDeGaliaPuedeContarLoMismoQueElCuento() {
        // Los campamentos de César al pie de Gergovia: del 5 al 22 de mayo del 52.
        assertEquals(0.0, arcoS("2029-05-04", "romanos"), 0.0); assertEquals(1.0, arcoS("2029-05-05", "romanos"), 0.0)
        assertEquals(1.0, arcoS("2029-05-22", "romanos"), 0.0); assertEquals(0.0, arcoS("2029-05-23", "romanos"), 0.0)
        assertEquals(0.0, arcoS("2028-05-10", "romanos"), 0.0); assertEquals(0.0, arcoS("2030-05-10", "romanos"), 0.0)
        // Las granjas quemadas: ninguna hasta las Cevenas, más cuando César llega a Gergovia, y se van rehaciendo.
        assertEquals(0.0, arcoS("2029-02-23", "danos"), 0.0); assertEquals(0.5, arcoS("2029-02-24", "danos"), 0.0); assertEquals(0.6, arcoS("2029-05-05", "danos"), 1e-9)
        assertTrue(arcoS("2031-12-31", "danos") < arcoS("2030-01-01", "danos"))
        // La muralla se refuerza en la primavera del 52 y no vuelve a bajar.
        val muralla = listOf("2027-06-01", "2029-03-01", "2029-04-01", "2029-04-30", "2031-06-01").map { arcoS(it, "muralla") }
        assertEquals(muralla.sorted(), muralla); assertEquals(1.0, muralla.last(), 0.0)
        // Los mercaderes de Italia: hasta Cenabum, ninguno en el 52, y vuelven en el 51.
        assertEquals(0.4, arcoS("2029-01-22", "comercio"), 0.0); assertEquals(0.0, arcoS("2029-01-23", "comercio"), 0.0)
        assertEquals(0.0, arcoS("2030-06-02", "comercio"), 0.0); assertEquals(0.35, arcoS("2030-06-03", "comercio"), 1e-9)
        // El trigo, cada año: alto antes de la siega y segado después.
        (2027..2031).forEach { y -> assertTrue("$y", arcoS("$y-07-09", "trigo") > 0.95); assertEquals(0.0, arcoS("$y-07-10", "trigo"), 0.0) }
        // Los días que cambian lo que se ve.
        assertEquals("muerdago", fichaS("2027-01-16").escena); assertEquals("samonios", fichaS("2027-11-18").escena)
        assertEquals("grito", fichaS("2029-01-23").escena); assertEquals("romanos", fichaS("2029-05-05").escena)
        assertEquals("victoria", fichaS("2029-05-20").escena); assertEquals("socorro", fichaS("2029-09-22").escena)
        assertEquals("entrega", fichaS("2029-09-27").escena); assertEquals("luto", fichaS("2030-08-26").escena)
        assertEquals(Clima.NIEVE, fichaS("2029-02-24").clima); assertEquals(Clima.TORMENTA, fichaS("2027-07-01").clima)
    }

    // ================================================================= Marguerite, la aprendiz de litografía de Art nouveau

    private val lit = AnioG9.LITOGRAFA
    private val marguerite get() = OtrasVidas.porId(lit.vida)!!
    private fun fichaM(dia: String) = FichaDelDia.de(dia, marguerite, lit)
    private fun fechasM(id: String) = lit.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun diaM(id: String) = lit.temporadas.flatMap { it.dias }.first { it.id == id }
    private fun arcoM(dia: String, id: String) = fichaM(dia).arcos.getValue(id)
    private fun diasEnM(clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { fichaM(it.toString()).estado == clave }

    @Test fun estaMargueriteConSuTemaYEsUnaPersonaInventada() {
        assertEquals(listOf("agente_red", "iniciado", "aprendiz_druida", "litografa", "cartelista"), AnioG9.TODOS.map { it.vida })
        assertTrue("No está en el registro", Calendarios.de(lit.vida) === lit)
        val q = marguerite
        assertEquals("nouveau", q.tema); assertEquals("tema", q.dibujo); assertEquals("Marguerite", q.nombre); assertEquals("Aprendiz de un taller de litografía", q.rol)
        // La ficha dice «19 años en 1900»: la temporada 1 es el 1898, y tiene 17.
        assertEquals(17, q.edad); assertEquals(1898, q.anio)
        // Su nombre es uno de la muestra de `fuentes_nouveau.md` (sección 10), y su apellido sale en la historia.
        assertTrue(q.nombre in setOf("Marguerite", "Jeanne", "Louise", "Suzanne", "Germaine", "Marie"))
        assertTrue(q.historia[0].startsWith("Marguerite Fournier"))
        // No es ficción: es una persona inventada en un mundo real (PLAN_TEMAS, 10a), con sus números reconstruidos.
        assertFalse(q.ficticia)
        assertEquals("Persona inventada. Inspirada en los talleres de litografía que imprimían los carteles de París hacia 1900; las cifras son reconstrucciones.", q.inspiracion)
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.RECONSTRUIDO })
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isNotEmpty() && q.fuentes.all { it.second.startsWith("https://fr.wikipedia.org/wiki/") })
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10f), con las correcciones 8 y 9 de las fuentes.
        val f = fichaM("2027-04-04")
        listOf(2.0 to OtrasVidas.Hacer.DORMIR, 6.5 to OtrasVidas.Hacer.COMER, 7.2 to OtrasVidas.Hacer.CAMINO, 9.0 to OtrasVidas.Hacer.TRABAJO, 12.5 to OtrasVidas.Hacer.COMER,
            15.0 to OtrasVidas.Hacer.TRABAJO, 19.2 to OtrasVidas.Hacer.CAMINO, 20.0 to OtrasVidas.Hacer.CUIDAR, 22.0 to OtrasVidas.Hacer.GENTE).forEach { (h, que) -> assertEquals("$h h", que, f.tramo(h)!!.que) }
        assertEquals("pasar las piedras de cada color por la prensa", f.tramo(9.0)!!.texto)
        // El metro solo desde el 19 de julio de 1900 (corrección 8), y a Montmartre, a pie.
        assertEquals("a casa: a pie y, desde el 19 de julio de 1900, en el metro nuevo", f.tramo(19.2)!!.texto)
        assertTrue(f.tramo(22.0)!!.texto.contains("Montmartre") && f.tramo(22.0)!!.texto.contains("a pie"))
        assertTrue(f.tramo(22.0)!!.texto.contains("La Cigale") && !f.tramo(22.0)!!.texto.contains("Chat Noir"))
        // Diez horas y media de trabajo: por debajo de las 11 de la ley del 30 de marzo de 1900, y lo mismo que pide desde 1902 (corrección 9).
        val horas = q.dia.filter { it.que == OtrasVidas.Hacer.TRABAJO }.sumOf { it.hasta - it.desde }
        assertEquals(10.5, horas, 1e-9)
        // Tiene época (1898, más o menos treinta) y avanza un año con cada temporada: de 1898 a 1902.
        assertEquals(1868..1928, Epocas.de(q))
        assertEquals((1898..1902).map { "$it" }, lit.temporadas.map { it.cuando })
        assertEquals(1898, fichaM("2027-03-03").anioHistorico); assertEquals(1900, fichaM("2029-03-03").anioHistorico); assertEquals(1902, fichaM("2031-03-03").anioHistorico)
        assertEquals(19, fichaM("2029-03-03").edad); assertEquals(21, fichaM("2031-03-03").edad)
        assertEquals(LUGARES.getValue("litografa").wikidata, setOf("Q90", "Q142", "Q70802", "Q50716", "Q753646", "Q186115"))
    }

    @Test fun unosVeinteDiasPorTemporadaYLaHistoriaAvanzaHastaElMetro() {
        assertEquals(listOf("obras", "vispera", "exposicion", "faroles", "jornada"), lit.temporadas.map { it.mundo })
        lit.temporadas.forEachIndexed { i, t ->
            assertTrue("Temporada ${i + 1}: ${t.dias.size} días", t.dias.size in 18..22)
            assertTrue("Temporada ${i + 1}: pocos días con fuente", t.dias.count { it.fuente != null } >= 6)
            assertTrue("Temporada ${i + 1}: pocos sucesos de su vida", t.dias.count { it.tipo == TipoDia.SUCESO } >= 6)
        }
        // Entra de aprendiz (1), la víspera (2), la Exposición y el metro (3), los faroles (4) y la jornada de diez y media (5).
        assertEquals(listOf("01-02", null, null, null, null), fechasM("entra_taller"))
        assertEquals(listOf("01-13", null, null, null, null), fechasM("jaccuse"))
        assertEquals(listOf("03-30", null, null, null, null), fechasM("seis_lineas"))
        assertEquals(listOf("10-04", null, null, null, null), fechasM("obras_metro"))
        assertEquals(listOf(null, "06-10", null, null, null), fechasM("concurso"))
        assertEquals(listOf(null, null, "03-30", null, null), fechasM("ley_millerand"))
        assertEquals(listOf(null, null, "04-14", null, null), fechasM("abre_exposicion"))
        assertEquals(listOf(null, null, "05-01", null, null), fechasM("grand_palais"))
        assertEquals(listOf(null, null, "06-15", null, null), fechasM("pruebas_metro"))
        assertEquals(listOf(null, null, "07-19", null, null), fechasM("abre_metro"))
        assertEquals(listOf(null, null, "08-06", null, null), fechasM("chatelet"))
        assertEquals(listOf(null, null, "08-13", null, null), fechasM("louvre"))
        assertEquals(listOf(null, null, "09-01", null, null), fechasM("linea_entera"))
        assertEquals(listOf(null, null, "10-02", null, null), fechasM("trocadero"))
        assertEquals(listOf(null, null, "11-12", null, null), fechasM("cierra_exposicion"))
        assertEquals(listOf(null, null, "12-13", null, null), fechasM("dauphine"))
        assertEquals(listOf(null, null, null, "02-13", null), fechasM("nancy"))
        assertEquals(listOf(null, null, null, "03-18", null), fechasM("faroles"))
        assertEquals(listOf(null, null, null, null, "04-01"), fechasM("jornada"))
        assertEquals(listOf(null, null, null, null, "10-07"), fechasM("anvers"))
        assertEquals(listOf(null, null, null, null, "12-11"), fechasM("petit_palais"))
        // Lo que vuelve cada año comparte el id.
        listOf("anio_nuevo", "auteuil", "inventario").forEach { id -> assertTrue(id, fechasM(id).all { it != null }) }
        assertEquals(listOf("01-01", "01-01", "01-01", "01-01", "01-01"), fechasM("anio_nuevo"))
        assertEquals(listOf("12-29", "12-29", "12-29", "12-29", "12-29"), fechasM("inventario"))
        // Lo que dice que lo pensará va antes de que diga que sí (la margarita de los prados: «j'y songerai»).
        assertTrue(MesDia.indice(diaM("lo_pensara").fecha) < MesDia.indice(diaM("dice_si").fecha))
        // Las estaciones de su camino abren en orden: Palais-Royal y Bastille el primer día; Châtelet y Saint-Paul, y luego Louvre.
        val orden = listOf("pruebas_metro", "abre_metro", "chatelet", "louvre", "linea_entera", "trocadero", "cierra_exposicion", "dauphine").map { MesDia.indice(diaM(it).fecha) }
        assertEquals(orden.sorted(), orden)
    }

    @Test fun lasFechasSonLasDeLasFuentesYSusCorrecciones() {
        // «Histórico», solo lo que `fuentes_nouveau.md` da con día; lo que solo tiene año o mes, «aproximado» con su fuente.
        val historicos = lit.temporadas.flatMap { it.dias }.filter { it.certeza == CertezaDia.HISTORICO }.map { it.id }.toSet()
        assertEquals(setOf("jaccuse", "seis_lineas", "obras_metro", "concurso", "ley_millerand", "abre_exposicion", "grand_palais", "pruebas_metro", "abre_metro",
            "chatelet", "louvre", "linea_entera", "trocadero", "cierra_exposicion", "nancy", "jornada", "anvers", "petit_palais"), historicos)
        lit.temporadas.flatMap { it.dias }.forEach { d ->
            if (d.certeza == CertezaDia.INVENTADO) assertNull(d.id, d.fuente) else assertNotNull(d.id, d.fuente)
        }
        // El ramal de Porte Dauphine: el 13 o el 3 de diciembre, según dónde se mire en la página (sección 5).
        assertEquals(CertezaDia.APROXIMADO, diaM("dauphine").certeza)
        // La ley de 1900 y su escalón de 1902, con sus páginas.
        assertEquals("https://fr.wikipedia.org/wiki/Mars_1900", diaM("ley_millerand").fuente!!.second)
        assertEquals("https://fr.wikipedia.org/wiki/1902_en_France", diaM("jornada").fuente!!.second)
        assertEquals("https://fr.wikipedia.org/wiki/Ligne_1_du_m%C3%A9tro_de_Paris", diaM("abre_metro").fuente!!.second)
        // Antes del 19 de julio de 1900 ningún día la lleva en metro (corrección 8); el metro llega a Anvers en 1902.
        val antes = lit.temporadas.take(2).flatMap { it.dias } + lit.temporadas[2].dias.filter { MesDia.indice(it.fecha) < MesDia.indice("07-19") }
        antes.forEach { d -> assertFalse(d.titulo, Regex("""(?i)\ben (el )?metro\b|\btren\b""").containsMatchIn(d.titulo)) }
        // Lo que el tema no usa nunca (`docs/diseno/temas/nouveau.md`) y lo que no había en su París.
        val prohibidas = listOf("Belle Époque", "art déco", "déco", "cancán", "Tiffany", "Moulin-Rouge", "Moulin Rouge", "Lautrec", "Coty", "Trianon", "14 de julio")
        val q = marguerite
        val textos = lit.temporadas.flatMap { t -> listOf(t.cuando, t.resumen) + t.dias.map { it.titulo } } + q.historia + q.dia.map { it.texto } + q.datos.values.map { it.nota }
        textos.forEach { s -> prohibidas.forEach { assertFalse("«$it» en «${s.take(60)}…»", Regex("""(?U)\b${Regex.escape(it)}\b""", RegexOption.IGNORE_CASE).containsMatchIn(s)) } }
        // Le Chat Noir solo sale como lo que ya cerró (corrección 10).
        textos.filter { "Chat Noir" in it }.forEach { assertTrue(it, "cerró" in it) }
    }

    @Test fun laExposicionDuraLoQueDuroYEnLaTemporadaCincoLoFijoNoSeMueve() {
        // Del 14 de abril a la víspera del cierre, el 12 de noviembre (el último día ya es especial).
        assertEquals(null, fichaM("2029-04-13").estado); assertEquals("exposicion", fichaM("2029-04-14").estado)
        assertEquals("exposicion", fichaM("2029-11-11").estado); assertEquals(null, fichaM("2029-11-12").estado)
        assertEquals(212, diasEnM("exposicion", 2029))
        listOf(2027, 2028, 2030, 2031, 2032).forEach { y -> assertEquals("$y", 0, diasEnM("exposicion", y)) }
        // En la temporada 5, que desde 2032 se repite, lo que no debe moverse no es un suceso, y no hay cadenas.
        listOf("anio_nuevo", "inventario", "jornada", "anvers", "petit_palais", "pabellon", "letreros").forEach { assertNotEquals(it, TipoDia.SUCESO, diaM(it).tipo) }
        assertTrue(lit.temporadas[4].dias.none { it.cadena != null })
        // Lo que dice que sí sigue después de lo que lo pensará, aunque se muevan.
        (2032..2041).forEach { y ->
            val inicios = FichaDelDia.inicios(lit, y).associate { it.first.id to it.second }
            assertTrue("$y", inicios.getValue("lo_pensara") < inicios.getValue("dice_si"))
        }
    }

    @Test fun elCuadroDeArtNouveauPuedeContarLoMismoQueElCuento() {
        // La entrada de metro delante del taller: sin obras, las obras al fondo, en la esquina, la barandilla y los faroles.
        assertEquals(0.0, arcoM("2027-10-03", "metro"), 0.0); assertEquals(0.1, arcoM("2027-10-04", "metro"), 1e-9)
        assertTrue(arcoM("2028-10-09", "metro") < 0.25); assertEquals(0.3, arcoM("2028-10-10", "metro"), 1e-9)
        assertEquals(0.5, arcoM("2029-07-18", "metro"), 1e-9); assertEquals(0.75, arcoM("2029-07-19", "metro"), 0.0)
        assertEquals(0.75, arcoM("2030-03-17", "metro"), 0.0); assertEquals(1.0, arcoM("2030-03-18", "metro"), 0.0); assertEquals(1.0, arcoM("2031-12-31", "metro"), 0.0)
        // La valla de enfrente, llena durante la Exposición.
        assertEquals(1.0, arcoM("2029-06-01", "carteles"), 0.0); assertTrue(arcoM("2029-11-13", "carteles") < 0.6); assertTrue(arcoM("2028-01-01", "carteles") < 0.5)
        // Los árboles de la calle, cada año: desnudos en enero, llenos en junio.
        (2027..2031).forEach { y -> assertEquals(0.0, arcoM("$y-01-15", "hojas"), 0.0); assertTrue("$y", arcoM("$y-06-20", "hojas") > 0.95) }
        // Los días que cambian lo que se ve.
        assertEquals("obras", fichaM("2027-10-04").escena); assertEquals("carteles", fichaM("2027-11-05").escena)
        assertEquals("exposicion", fichaM("2029-04-14").escena); assertEquals("luces", fichaM("2029-06-09").escena)
        assertEquals("metro", fichaM("2029-07-19").escena); assertEquals("faroles", fichaM("2030-03-18").escena); assertEquals("metro", fichaM("2031-10-07").escena)
        assertEquals(Clima.TORMENTA, fichaM("2028-04-15").clima); assertEquals(Clima.NIEVE, fichaM("2030-01-30").clima); assertEquals(Clima.LLUVIA, fichaM("2030-07-08").clima)
        // La visita a Auteuil (el invernadero del cuadro) vuelve cada año.
        assertEquals(5, lit.temporadas.count { t -> t.dias.any { it.id == "auteuil" } })
    }

    // ================================================================= David, el dibujante de carteles de Psicodelia

    private val car = AnioG9.CARTELISTA
    private val david get() = OtrasVidas.porId(car.vida)!!
    private fun fichaD(dia: String) = FichaDelDia.de(dia, david, car)
    private fun fechasD(id: String) = car.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun diaD(id: String) = car.temporadas.flatMap { it.dias }.first { it.id == id }
    private fun arcoD(dia: String, id: String) = fichaD(dia).arcos.getValue(id)
    private fun diasEnD(clave: String, anio: Int) =
        generateSequence(LocalDate.of(anio, 1, 1)) { it.plusDays(1) }.takeWhile { it.year == anio }.count { fichaD(it.toString()).estado == clave }
    /** Todo lo que el calendario y «Su vida» dicen con palabras (sin los enlaces). */
    private val textosD get() = car.temporadas.flatMap { t -> listOf(t.cuando, t.resumen) + t.dias.flatMap { listOfNotNull(it.titulo, it.fuente?.first) } } +
        david.historia + listOf(david.nombre, david.rol, david.lugar, david.cuando, david.inspiracion) + david.dia.map { it.texto } + david.datos.values.map { it.nota }

    @Test fun estaDavidConSuTemaYEsUnaPersonaInventada() {
        assertEquals(listOf("agente_red", "iniciado", "aprendiz_druida", "litografa", "cartelista"), AnioG9.TODOS.map { it.vida })
        assertTrue("No está en el registro", Calendarios.de(car.vida) === car)
        val q = david
        assertEquals("psicodelia", q.tema); assertEquals("tema", q.dibujo); assertEquals("Dibujante de carteles de conciertos", q.rol)
        // Su nombre es uno de los de la ficha (PLAN_TEMAS, 10g; corrección 19) y no es el de nadie de la escena.
        assertTrue(q.nombre in setOf("David", "Richard", "Thomas", "Ronald", "Michael"))
        assertTrue(q.nombre !in setOf("Wes", "Rick", "Stanley", "Alton", "Victor", "Bonnie", "Bill", "Chet", "Larry", "Tom", "Allen"))
        assertTrue(q.historia[0].startsWith("David Hale"))
        // La ficha dice «24 años en 1967»: la temporada 1 es el 1965, y tiene 22.
        assertEquals(22, q.edad); assertEquals(1965, q.anio)
        // No es ficción: es una persona inventada en un mundo real (PLAN_TEMAS, 10a), con sus números reconstruidos.
        assertFalse(q.ficticia)
        assertEquals("Persona inventada. Inspirado en los dibujantes de carteles de las salas de baile de San Francisco en 1966 y 1967; las cifras son reconstrucciones.", q.inspiracion)
        assertEquals(OtrasVidas.MEDIDAS.map { it.id }.toSet(), q.datos.keys)
        assertTrue(q.datos.values.all { it.c == OtrasVidas.Certeza.RECONSTRUIDO })
        assertEquals("No bebe: la regla del tema", 0.0, q.datos.getValue("alcohol").v, 0.0)
        assertEquals(3, q.historia.size); assertTrue(q.fuentes.isNotEmpty() && q.fuentes.all { it.second.startsWith("https://en.wikipedia.org/wiki/") })
        // Su agenda es la de la ficha del tema (PLAN_TEMAS, 10g), y cubre las 24 horas.
        val f = fichaD("2027-04-04")
        listOf(5.0 to OtrasVidas.Hacer.DORMIR, 12.5 to OtrasVidas.Hacer.COMER, 15.0 to OtrasVidas.Hacer.TRABAJO, 18.5 to OtrasVidas.Hacer.COMER, 19.5 to OtrasVidas.Hacer.CAMINO,
            22.0 to OtrasVidas.Hacer.GENTE, 1.0 to OtrasVidas.Hacer.GENTE, 3.0 to OtrasVidas.Hacer.LIBRE).forEach { (h, que) -> assertEquals("$h h", que, f.tramo(h)!!.que) }
        assertEquals(24.0, q.dia.sumOf { (if (it.hasta > it.desde) it.hasta else it.hasta + 24) - it.desde }, 1e-9)
        assertEquals("dibujar el cartel de la semana", f.tramo(15.0)!!.texto)
        assertEquals("en la cocina del departamento compartido", f.tramo(18.5)!!.texto)
        assertEquals("música en el departamento", f.tramo(3.0)!!.texto)
        // Los bailes son de jueves a sábado, y el día de la agenda es uno de esos (corrección 17): lo dicen la agenda y la historia.
        assertTrue(f.tramo(22.0)!!.texto.contains("de jueves a sábado")); assertTrue(q.historia[1].contains("de jueves a sábado"))
        // Las salas y la radio, por su calle o su frecuencia (corrección 6).
        assertTrue(f.tramo(22.0)!!.texto.contains("la sala de la calle Sutter") && f.tramo(22.0)!!.texto.contains("el auditorio de Geary"))
        // Tiene época (1965, más o menos treinta) y avanza un año con cada temporada: de 1965 a 1969.
        assertEquals(1935..1995, Epocas.de(q))
        assertEquals((1965..1969).map { "$it" }, car.temporadas.map { it.cuando })
        assertEquals(1965, fichaD("2027-03-03").anioHistorico); assertEquals(1967, fichaD("2029-03-03").anioHistorico); assertEquals(1969, fichaD("2031-03-03").anioHistorico)
        assertEquals(24, fichaD("2029-03-03").edad); assertEquals(26, fichaD("2031-03-03").edad)
        // Su lugar, para «Hoy en la historia»: su ciudad y su estado, no todo el país.
        assertEquals(setOf("Q62", "Q99", "Q1027700", "Q635559", "Q932701"), LUGARES.getValue("cartelista").wikidata)
        assertFalse("Q30" in LUGARES.getValue("cartelista").wikidata)
    }

    @Test fun nadaDeDrogasEnSuVidaNiEnSuCalendario() {
        // La regla del tema, la más importante (PLAN_TEMAS, 10g; `fuentes_psicodelia.md`, sección 9): ni de lejos ni en jerga.
        textosD.forEach { s -> JERGA_DE_DROGAS.forEach { r -> assertFalse("«${r.find(s)?.value}» en «${s.take(70)}…»", r.containsMatchIn(s)) } }
        // Lo de la sección 9 no está en el calendario: ni la primera tienda de Haight, ni la clínica gratuita (7 de junio de 1967).
        assertTrue(car.temporadas[2].dias.none { it.fecha == "06-07" }); assertTrue(car.temporadas[1].dias.none { it.fecha == "01-03" })
        // Ni marcas de las salas, la radio o el festival (corrección 6), ni el nombre de las casas, que es de 1978 (corrección 1).
        textosD.forEach { s -> MARCAS_DE_PSICODELIA.forEach { r -> assertFalse("«${r.find(s)?.value}» en «${s.take(70)}…»", r.containsMatchIn(s)) } }
        // Del 6 de octubre de 1967, solo el entierro de mentira.
        assertEquals("En Buena Vista Park, el entierro de mentira del hippie, con un ataúd en hombros", diaD("entierro_hippie").titulo)
        // Del 14 de enero, solo los poetas, las bandas y la gente en el pasto.
        assertEquals("En los Polo Fields del parque, miles de personas en el pasto oyen a poetas y bandas", diaD("encuentro_parque").titulo)
    }

    @Test fun unosVeinteDiasPorTemporadaYLaHistoriaAvanzaHastaElVeranoDelAmor() {
        assertEquals(listOf("barrio", "salas", "verano", "despues", "cambio"), car.temporadas.map { it.mundo })
        car.temporadas.forEachIndexed { i, t ->
            assertTrue("Temporada ${i + 1}: ${t.dias.size} días", t.dias.size in 18..23)
            assertTrue("Temporada ${i + 1}: pocos días con fuente", t.dias.count { it.fuente != null } >= 5)
            assertTrue("Temporada ${i + 1}: pocos sucesos de su vida", t.dias.count { it.tipo == TipoDia.SUCESO } >= 6)
        }
        // Llega (1), las salas y su primer cartel (2), el verano del amor (3), el año de después (4) y el barrio que cambia (5).
        assertEquals(listOf("01-01", null, null, null, null), fechasD("llega"))
        assertEquals(listOf("12-10", null, null, null, null), fechasD("baile_geary"))
        assertEquals(listOf(null, "04-22", null, null, null), fechasD("sutter"))
        assertEquals(listOf(null, "05-07", null, null, null), fechasD("primer_cartel"))
        assertEquals(listOf(null, "09-02", null, null, null), fechasD("un_numero"))
        assertEquals(listOf(null, "09-20", null, null, null), fechasD("periodico"))
        assertEquals(listOf(null, "09-23", null, null, null), fechasD("sala_grande"))
        assertEquals(listOf(null, "12-17", null, null, null), fechasD("entierro_dinero"))
        assertEquals(listOf(null, null, "01-14", null, null), fechasD("encuentro_parque"))
        assertEquals(listOf(null, null, "01-29", null, null), fechasD("baile_krishna"))
        assertEquals(listOf(null, null, "02-12", null, null), fechasD("fm_noche"))
        assertEquals(listOf(null, null, "04-15", null, null), fechasD("marcha"))
        assertEquals(listOf(null, null, "06-10", null, null), fechasD("tamalpais"))
        assertEquals(listOf(null, null, "06-16", null, null), fechasD("monterey"))
        assertEquals(listOf(null, null, "07-07", null, null), fechasD("portada_time"))
        assertEquals(listOf(null, null, "08-06", null, null), fechasD("fm_todo_el_dia"))
        assertEquals(listOf(null, null, "09-01", null, null), fechasD("life"))
        assertEquals(listOf(null, null, "10-06", null, null), fechasD("entierro_hippie"))
        assertEquals(listOf(null, null, null, "03-18", null), fechasD("huelga_fm"))
        assertEquals(listOf(null, null, null, null, "03-15"), fechasD("pasaje"))
        // La comida gratis del Panhandle, en el verano del amor; los Diggers se acaban en el 68.
        assertEquals(listOf(null, null, "05-13", null, null), fechasD("comida_panhandle"))
        assertTrue(MesDia.indice(diaD("comida_panhandle").fecha) < MesDia.indice(diaD("monterey").fecha))
        assertEquals(listOf(null, null, null, "10-12", null), fechasD("diggers_fin"))
        assertEquals(2, diaD("tamalpais").duracion); assertEquals(3, diaD("monterey").duracion)
        // Lo que vuelve cada año comparte el id, el texto y la fecha; Acción de Gracias es el cuarto jueves de noviembre.
        listOf("niebla_verano", "calor_septiembre", "accion_gracias", "navidad").forEach { id -> assertTrue(id, fechasD(id).all { it != null }) }
        assertEquals(List(5) { "06-02" }, fechasD("niebla_verano")); assertEquals(List(5) { "09-18" }, fechasD("calor_septiembre")); assertEquals(List(5) { "12-25" }, fechasD("navidad"))
        (1965..1969).forEachIndexed { i, y ->
            val cuarto = generateSequence(LocalDate.of(y, 11, 1)) { it.plusDays(1) }.filter { it.dayOfWeek == java.time.DayOfWeek.THURSDAY }.drop(3).first()
            assertEquals("$y", "%02d-%02d".format(cuarto.monthValue, cuarto.dayOfMonth), fechasD("accion_gracias")[i])
        }
        // Su gente llega y se va en orden: Ellen (2, 3 y 4), Nora (4), Gil y Sal (5).
        val orden = listOf("conoce_ellen", "ellen_luces", "ellen_novia", "nora_se_va", "ellen_se_muda", "sal_retira", "gil_se_va", "estudio")
        val lugares = orden.map { id -> car.temporadas.indexOfFirst { t -> t.dias.any { it.id == id } } * 365 + MesDia.indice(diaD(id).fecha) }
        assertEquals(lugares.sorted(), lugares)
    }

    @Test fun lasFechasDeDavidSonLasDeLasFuentesYSusCorrecciones() {
        // «Histórico», solo lo que `fuentes_psicodelia.md` da con día; lo que solo tiene año, mes o estación, «aproximado» con su fuente.
        val historicos = car.temporadas.flatMap { it.dias }.filter { it.certeza == CertezaDia.HISTORICO }.map { it.id }.toSet()
        assertEquals(setOf("baile_geary", "un_numero", "periodico", "sala_grande", "entierro_dinero", "encuentro_parque", "baile_krishna", "fm_noche", "marcha",
            "tamalpais", "monterey", "portada_time", "fm_todo_el_dia", "life", "entierro_hippie", "huelga_fm"), historicos)
        car.temporadas.flatMap { it.dias }.forEach { d ->
            if (d.certeza == CertezaDia.INVENTADO) assertNull(d.id, d.fuente) else assertNotNull(d.id, d.fuente)
            d.fuente?.let { assertTrue(d.id, it.second.startsWith("https://en.wikipedia.org/wiki/")) }
        }
        // La niebla y el calor son los del clima de hoy (corrección 21): aproximados, y con su tiempo.
        assertEquals(CertezaDia.APROXIMADO, diaD("niebla_verano").certeza); assertEquals(Clima.NIEBLA, diaD("niebla_verano").clima)
        assertEquals(CertezaDia.APROXIMADO, diaD("calor_septiembre").certeza); assertEquals(Clima.DESPEJADO, diaD("calor_septiembre").clima)
        assertEquals("https://en.wikipedia.org/wiki/San_Francisco#Climate", diaD("niebla_verano").fuente!!.second)
        // La FM, en el 106.9: de medianoche a las seis el 12 de febrero, a toda hora el 6 de agosto.
        assertEquals("https://en.wikipedia.org/wiki/KFRC-FM", diaD("fm_noche").fuente!!.second)
        assertTrue("106.9" in diaD("fm_noche").titulo && "medianoche a las seis" in diaD("fm_noche").titulo)
        // Monterey, con su precio (corrección 8), y la sala de la calle Sutter con los dos dólares del 29 de enero.
        assertTrue("de tres a seis dólares y medio" in diaD("monterey").titulo); assertTrue("dos dólares" in diaD("baile_krishna").titulo)
        // Los cuatro años sin cronología de 1967 llevan solo lo que el archivo trae: lo que no está, no se inventa como hecho.
        listOf(0, 1, 3, 4).forEach { i -> car.temporadas[i].dias.filter { it.tipo != TipoDia.SUCESO && it.certeza == CertezaDia.INVENTADO }.forEach { d ->
            assertTrue("${d.id}: inventado que no es de su vida", d.id in setOf("accion_gracias", "navidad", "lluvia_enero", "pintan_casa", "tienda_cerrada"))
        } }
        // Las casas: casi todas grises. En su calle, una pintada desde marzo de 1965, dos desde abril de 1969, y la suya en mayo de 1969.
        assertEquals(0.0, arcoD("2027-03-28", "calle"), 0.0); assertEquals(0.1, arcoD("2027-03-29", "calle"), 0.0)
        assertEquals(0.1, arcoD("2031-04-18", "calle"), 0.0); assertEquals(0.25, arcoD("2031-04-19", "calle"), 0.0)
    }

    @Test fun enMontereyTresDiasYEnLaTemporadaCincoLoFijoNoSeMueve() {
        assertEquals(null, fichaD("2029-06-15").estado)
        listOf("2029-06-16", "2029-06-17", "2029-06-18").forEach { assertEquals(it, "monterey", fichaD(it).estado) }
        assertEquals(null, fichaD("2029-06-19").estado)
        assertEquals(3, diasEnD("monterey", 2029))
        listOf(2027, 2028, 2030, 2031, 2032).forEach { y -> assertEquals("$y", 0, diasEnD("monterey", y)) }
        // En la temporada 5, que desde 2032 se repite, lo que no debe moverse no es un suceso, y no hay cadenas.
        listOf("niebla_verano", "calor_septiembre", "accion_gracias", "navidad", "cierra_sutter", "pasaje", "colores_calle", "pintan_casa", "tienda_cerrada", "lluvia_enero").forEach {
            assertNotEquals(it, TipoDia.SUCESO, diaD(it).tipo)
        }
        assertTrue(car.temporadas[4].dias.none { it.cadena != null })
        // La casa se pinta en tres días, el mismo día en los dos: la escena y el arco no se separan aunque se repita.
        (2031..2036).forEach { y ->
            assertEquals("$y", "pintura", fichaD("$y-05-10").escena); assertEquals("$y", 0.3, arcoD("$y-05-10", "casa"), 0.0)
            assertEquals("$y", 1.0, arcoD("$y-05-12", "casa"), 0.0)
        }
    }

    @Test fun elCuadroDePsicodeliaPuedeContarLoMismoQueElCuento() {
        // La casa de la señora Petrakis: gris hasta el 9 de mayo de 1969; turquesa desde el 12.
        assertEquals(0.0, arcoD("2027-01-01", "casa"), 0.0); assertEquals(0.0, arcoD("2031-05-09", "casa"), 0.0)
        assertEquals(0.6, arcoD("2031-05-11", "casa"), 0.0); assertEquals(1.0, arcoD("2031-12-31", "casa"), 0.0)
        // Los carteles de los postes: ninguno hasta las letras que se derriten (marzo de 1966); llenos en el verano del amor; menos después.
        assertEquals(0.0, arcoD("2028-03-11", "carteles"), 0.0); assertEquals(0.2, arcoD("2028-03-12", "carteles"), 0.0)
        (0..120).forEach { assertEquals(1.0, arcoD(LocalDate.of(2029, 6, 1).plusDays(it.toLong()).toString(), "carteles"), 0.0) }
        assertEquals(0.75, arcoD("2029-10-06", "carteles"), 0.0); assertTrue(arcoD("2031-06-01", "carteles") < 0.5)
        // Los días que cambian lo que se ve.
        assertEquals("rotulo", fichaD("2027-06-26").escena); assertEquals("carteles", fichaD("2028-03-12").escena); assertEquals("carteles", fichaD("2028-04-22").escena); assertEquals("carteles", fichaD("2028-05-07").escena)
        assertEquals("parque", fichaD("2029-01-14").escena); assertEquals("radio", fichaD("2029-02-12").escena); assertEquals("radio", fichaD("2029-08-06").escena)
        assertEquals("carteles", fichaD("2029-09-01").escena); assertEquals("mudanza", fichaD("2030-01-20").escena); assertEquals("mudanza", fichaD("2030-03-02").escena)
        assertEquals("luz_negra", fichaD("2030-04-13").escena); assertEquals("cerrado", fichaD("2031-08-30").escena)
        assertEquals("fiesta", fichaD("2027-12-25").escena); assertEquals("fiesta", fichaD("2029-11-23").escena)
        // El tiempo de San Francisco: niebla en verano, nunca nieve; la niebla del 2 de junio, cada año.
        (2027..2031).forEach { y -> assertEquals("$y", Clima.NIEBLA, fichaD("$y-06-02").clima); assertEquals("$y", Clima.DESPEJADO, fichaD("$y-09-18").clima) }
        assertTrue(car.clima.none { Clima.NIEVE in it || Clima.TORMENTA in it || Clima.POLVO in it })
        assertTrue(car.clima.subList(5, 8).all { m -> m.getValue(Clima.NIEBLA) == m.values.max() })
        assertEquals(Clima.LLUVIA, fichaD("2031-01-10").clima)
    }
}

/**
 * Lo que el tema Psicodelia no dice nunca (PLAN_TEMAS, 10g; `docs/diseno/temas/psicodelia.md`; `fuentes_psicodelia.md`,
 * sección 9): las drogas, ni de lejos ni en jerga, ni el alcohol, ni el tabaco, ni lo que toca el seguimiento privado. Cada
 * entrada es una palabra entera o el principio de una («viaj» quita «viaje», «viajar» y «viajeros»), sin contar
 * mayúsculas. Algunas son palabras corrientes con otro sentido en la jerga («hierba», «hielo», «raya», «coca», «aguja»,
 * «grifo», «volado», «colocar»): el cuento no las usa nunca, aunque fuera con su sentido de siempre. La usan
 * [AnioG9Prueba] y [CuentoG9Prueba].
 */
internal val JERGA_DE_DROGAS: List<Regex> = listOf(
    "drog\\w*", "viaj\\w*", "[áa]cid\\w*", "coloc\\w*", "porro\\w*", "marihuana", "mariguana", "cannabis", "hach[íi]s", "mota", "hierba\\w*", "yerba", "lsd",
    "alucin\\w*", "psicod\\w*", "psiqued\\w*", "trip\\w*", "flip\\w*", "subid[óo]n", "volad[oa]s?", "pachec\\w*", "grif[oa]s?", "fum\\w*", "cigarr\\w*",
    "tabaco", "pipas?", "hongos?", "peyote", "mescalin\\w*", "hero[íi]na", "coca\\w*", "anfetamin\\w*", "speed", "dealer", "camell\\w*", "pastill\\w*",
    "dosis", "sobredosis", "sustancia\\w*", "jering\\w*", "aguja\\w*", "inyec\\w*", "liar", "mona", "polvo\\w*", "cristal\\w*", "hielo", "nieve", "perico",
    "raya\\w*", "alcohol\\w*", "cervez\\w*", "vinos?", "whisky", "licor\\w*", "borrach\\w*", "trago\\w*", "copas?", "bebid\\w*", "embriag\\w*", "resaca",
    "expand\\w*", "percepci\\w*", "conciencia", "cl[íi]nica\\w*", "reca[íi]d\\w*", "racha\\w*", "impulso\\w*", "high", "stoned", "kesey", "leary", "huxley",
    "head shop",
).map { Regex("""(?iU)(?<![\p{L}\p{N}])(?:$it)(?![\p{L}\p{N}])""") }

/**
 * Los nombres que el tema no usa (corrección 6 y 1 de `fuentes_psicodelia.md`): las salas, la radio, el festival y el
 * periódico van por su calle, su frecuencia o su lugar, y las casas pintadas no se llaman por el nombre que les dio un libro
 * de 1978. Tampoco la lámpara de cera con su marca.
 */
internal val MARCAS_DE_PSICODELIA: List<Regex> = listOf(
    "fillmore", "avalon", "family dog", "monterey pop", "kmpx", "winterland", "oracle", "red dog", "painted lad\\w*", "l[áa]mparas? de lava", "day-glo", "acid rock",
    "bill graham presents", "trip without a ticket",
).map { Regex("""(?iU)(?<![\p{L}\p{N}])(?:$it)(?![\p{L}\p{N}])""") }
