package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG4
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Los calendarios del grupo 4 (tanda A4): Odo, Bernat, Zaynab, Bat e Iacopo. Las pruebas generales (forma, certeza,
 * fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en [Calendarios]; aquí va
 * lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG4Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun entre(a: String, b: String) = MesDia.indice(b) - MesDia.indice(a)
    /** Los días entre dos fechas «MM-DD» de dos años (en estos siglos no hay año secular de por medio, así que cuenta igual que el juliano). */
    private fun entreAnios(anioA: Int, a: String, anioB: Int, b: String) =
        ChronoUnit.DAYS.between(LocalDate.parse("%04d-%s".format(anioA, a)), LocalDate.parse("%04d-%s".format(anioB, b))).toInt()
    /** En cada temporada, el miércoles de ceniza cae 46 días antes de la Pascua (contados con el 29 de febrero de los bisiestos). */
    private fun pascuaCuadra(c: Calendario, primerAnio: Int) = c.temporadas.indices.forEach { i ->
        val anio = primerAnio + i
        assertEquals("${c.vida}, $anio: la ceniza", -46, entreAnios(anio, fechas(c, "pascua")[i]!!, anio, fechas(c, "cuaresma")[i]!!))
    }

    @Test fun estanLosCincoYCadaUnoConSuTema() {
        assertEquals(listOf("copista", "sargento", "cordobesa", "pastor_mongol", "aprendiz"), AnioG4.TODOS.map { it.vida })
        val temas = mapOf("copista" to "medieval", "sargento" to "templario", "cordobesa" to "andalus", "pastor_mongol" to "mongol", "aprendiz" to "renacimiento")
        AnioG4.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], OtrasVidas.porId(c.vida)!!.tema)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun laBibliaDeOdoSeTerminaYEmpiezaOtroLibro() {
        val c = AnioG4.ODO
        pascuaCuadra(c, 1150)
        c.temporadas.indices.forEach { i -> assertEquals("Pentecostés, temporada ${i + 1}", 49, entre(fechas(c, "pascua")[i]!!, fechas(c, "pentecostes")[i]!!)) }
        val libro = (2027..2030).map { ficha(c, "$it-12-31").arcos.getValue("libro") }
        assertTrue("La Biblia no crece: $libro", libro.zipWithNext().all { (a, b) -> b >= a })
        assertEquals(1.0, ficha(c, "2030-06-29").arcos.getValue("libro"), 0.0)
        assertEquals(1.0, ficha(c, "2031-08-27").arcos.getValue("libro"), 0.0); assertEquals(0.0, ficha(c, "2031-08-28").arcos.getValue("libro"), 0.0)
        // Seis semanas en Lorsch (temporada 2), sin ninguna fiesta de la abadía en medio.
        assertEquals(null, ficha(c, "2028-06-04").estado); assertEquals("lorsch", ficha(c, "2028-06-05").estado)
        assertEquals("lorsch", ficha(c, "2028-07-07").estado); assertEquals(null, ficha(c, "2028-07-08").estado)
    }

    @Test fun bernatSeQuedaEnElCastilloSalvoHeridoOEnAcre() {
        val c = AnioG4.BERNAT
        pascuaCuadra(c, 1250)
        // Las fiestas de la Virgen, patrona de la orden, no se mueven.
        listOf("candelaria", "anunciacion", "asuncion", "natividad_virgen").forEach { id -> assertEquals(id, 1, fechas(c, id).toSet().size) }
        assertEquals("herido", ficha(c, "2028-06-12").estado); assertEquals("herido", ficha(c, "2028-08-01").estado); assertEquals(null, ficha(c, "2028-08-02").estado)
        assertEquals("acre", ficha(c, "2029-09-12").estado); assertEquals("acre", ficha(c, "2029-10-14").estado); assertEquals(null, ficha(c, "2029-10-15").estado)
        // El castillo nunca se encoge.
        val muralla = (2027..2031).map { ficha(c, "$it-12-31").arcos.getValue("muralla") }
        assertTrue("La muralla no crece: $muralla", muralla.zipWithNext().all { (a, b) -> b >= a })
        // Mansura (8 de febrero) llega antes que la noticia del maestre (6 de abril), y la del rey libre, después.
        assertTrue(MesDia.indice(fechas(c, "mansura")[0]!!) < MesDia.indice(fechas(c, "maestre")[0]!!))
        assertTrue(MesDia.indice(fechas(c, "maestre")[0]!!) < MesDia.indice(fechas(c, "rey_acre")[0]!!))
    }

    @Test fun elAnioDeZaynabEsLunar() {
        val c = AnioG4.ZAYNAB
        val anios = (970..974).toList()
        // Un año lunar son 354 o 355 días: cada fiesta cae unos once días antes que el año anterior.
        listOf("ramadan", "eid_fitr", "eid_adha", "hegira", "ashura").forEach { id ->
            val f = fechas(c, id)
            anios.indices.toList().zipWithNext().forEach { (a, b) -> assertTrue("$id, ${anios[a]} a ${anios[b]}", entreAnios(anios[a], f[a]!!, anios[b], f[b]!!) in 354..355) }
        }
        c.temporadas.indices.forEach { i ->
            val n = "Temporada ${i + 1}"
            assertTrue(n, entre(fechas(c, "ramadan")[i]!!, fechas(c, "eid_fitr")[i]!!) in 29..30)
            assertEquals(n, 26, entre(fechas(c, "ramadan")[i]!!, fechas(c, "qadr")[i]!!))
            assertTrue(n, entre(fechas(c, "eid_fitr")[i]!!, fechas(c, "eid_adha")[i]!!) in 68..70)
            assertEquals(n, 9, entre(fechas(c, "hegira")[i]!!, fechas(c, "ashura")[i]!!))
            // El Ramadán es un estado: del primer día del ayuno a la víspera de la fiesta.
            val r = LocalDate.parse("${2027 + i}-${fechas(c, "ramadan")[i]}"); val e = LocalDate.parse("${2027 + i}-${fechas(c, "eid_fitr")[i]}")
            assertEquals(n, "ramadan", ficha(c, r.toString()).estado); assertEquals(n, "ramadan", ficha(c, e.minusDays(1).toString()).estado)
            assertEquals(n, null, ficha(c, e.toString()).estado); assertEquals(n, null, ficha(c, r.minusDays(1).toString()).estado)
        }
        assertEquals(1.0, ficha(c, "2028-06-03").arcos.getValue("tela"), 0.0); assertEquals(0.0, ficha(c, "2029-08-28").arcos.getValue("tela"), 0.0)
        assertTrue(ficha(c, "2027-03-15").arcos.getValue("rio") > ficha(c, "2027-09-15").arcos.getValue("rio"))
    }

    @Test fun batSigueElAnioDeLaLunaYElDeLosPastos() {
        val c = AnioG4.BAT
        // El mes blanco: doce lunas (354 o 355 días) o trece (383 a 385) después del anterior.
        val f = fechas(c, "mes_blanco")
        (0..3).forEach { i -> val d = entreAnios(1220 + i, f[i]!!, 1221 + i, f[i + 1]!!); assertTrue("${1220 + i}: $d días", d in 354..355 || d in 383..385) }
        listOf("dragón", "serpiente", "caballo", "oveja", "mono").forEachIndexed { i, a -> assertTrue(c.temporadas[i].cuando, a in c.temporadas[i].cuando) }
        (2027..2031).forEach { y ->
            assertEquals(null, ficha(c, "$y-05-24").estado); assertEquals("verano", ficha(c, "$y-05-25").estado)
            assertEquals("verano", ficha(c, "$y-09-24").estado); assertEquals(null, ficha(c, "$y-09-25").estado)
            assertTrue(ficha(c, "$y-08-01").arcos.getValue("pasto") > ficha(c, "$y-01-15").arcos.getValue("pasto"))
        }
        // El invierno malo de la temporada 4 se lleva un tercio del rebaño, que luego se recupera un poco.
        assertTrue(ficha(c, "2030-02-14").arcos.getValue("rebano") - ficha(c, "2030-03-21").arcos.getValue("rebano") >= 0.3)
        assertTrue(ficha(c, "2031-12-31").arcos.getValue("rebano") > ficha(c, "2030-03-21").arcos.getValue("rebano"))
    }

    @Test fun iacopoPierdeAlMaestroYVeEntrarALosFranceses() {
        val c = AnioG4.IACOPO
        pascuaCuadra(c, 1490)
        c.temporadas.indices.forEach { i -> assertEquals("El carnaval, temporada ${i + 1}", -1, entre(fechas(c, "cuaresma")[i]!!, fechas(c, "carnaval")[i]!!)) }
        // La temporada 5: el maestro muere el 11 de enero, la estatua de nieve es del 20, Piero se va el 9 de noviembre y el rey entra el 17.
        val t5 = c.temporadas[4].dias.associate { it.id to it.fecha }
        assertEquals("01-11", t5["muere_maestro"]); assertEquals("01-20", t5["estatua_nieve"]); assertEquals("11-09", t5["piero"]); assertEquals("11-17", t5["rey_francia"])
        assertEquals("campo", ficha(c, "2029-06-28").estado); assertEquals("campo", ficha(c, "2029-08-07").estado); assertEquals(null, ficha(c, "2029-08-08").estado)
        assertEquals(1.0, ficha(c, "2027-12-22").arcos.getValue("capilla"), 0.0)
        assertEquals(0.0, ficha(c, "2030-03-10").arcos.getValue("tabla"), 0.0); assertEquals(1.0, ficha(c, "2031-05-25").arcos.getValue("tabla"), 0.0)
    }
}
