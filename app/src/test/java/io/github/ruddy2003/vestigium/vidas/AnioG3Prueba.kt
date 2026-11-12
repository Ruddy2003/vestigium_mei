package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG3
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Los calendarios del grupo 3 (tanda A3): Cayo, Pay, Ama, K'an y Nanai-vandak. Las pruebas generales (forma, certeza,
 * fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en [Calendarios]; aquí va
 * lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG3Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun entre(a: String, b: String) = MesDia.indice(b) - MesDia.indice(a)
    /** Los días entre dos fechas «MM-DD» de dos temporadas seguidas (cuenta el bisiesto de en medio, como el juliano). */
    private fun entreAnios(anioA: Int, a: String, anioB: Int, b: String) =
        ChronoUnit.DAYS.between(LocalDate.parse("%04d-%s".format(anioA, a)), LocalDate.parse("%04d-%s".format(anioB, b))).toInt()

    @Test fun estanLosCincoYCadaUnoConSuTema() {
        assertEquals(listOf("legionario", "escriba_tumba", "pintora_cueva", "escriba_maya", "mercader_sogdiano"), AnioG3.TODOS.map { it.vida })
        val temas = mapOf("legionario" to "romano", "escriba_tumba" to "egipto", "pintora_cueva" to "cueva", "escriba_maya" to "meso", "mercader_sogdiano" to "seda")
        AnioG3.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], OtrasVidas.porId(c.vida)!!.tema)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun laMurallaDeCayoSubeYLaGuarnicionBajaCuandoParteLaVejilacion() {
        val c = AnioG3.CAYO
        val muralla = (2027..2031).map { ficha(c, "$it-12-31").arcos.getValue("muralla") }
        assertEquals(listOf(0.3, 0.6, 1.0, 1.0, 1.0), muralla.map { Math.round(it * 10) / 10.0 })
        assertEquals(1.0, ficha(c, "2030-09-24").arcos.getValue("guarnicion"), 0.0)
        assertEquals(0.6, ficha(c, "2030-09-25").arcos.getValue("guarnicion"), 0.0)
        // Seis semanas fuera del campamento, en el camino del desfiladero (temporada 2).
        assertEquals(null, ficha(c, "2028-05-31").estado); assertEquals("camino", ficha(c, "2028-06-01").estado)
        assertEquals("camino", ficha(c, "2028-07-14").estado); assertEquals(null, ficha(c, "2028-07-15").estado)
        // El calendario religioso del ejército es el mismo los cinco años.
        listOf("votos", "quinquatrias", "rosalias", "neptunalias", "armilustrium", "saturnales").forEach { id -> assertEquals(id, 1, fechas(c, id).toSet().size) }
    }

    @Test fun lasFiestasDePaySiguenElCalendarioCivil() {
        val c = AnioG3.PAY
        c.temporadas.indices.forEach { i ->
            val n = "Temporada ${i + 1}"
            val nuevo = fechas(c, "anio_nuevo")[i]!!
            assertEquals(n, 44, entre(nuevo, fechas(c, "opet")[i]!!))
            assertEquals(n, 107, entre(nuevo, fechas(c, "joiak")[i]!!))
            assertEquals(n, -5, entre(nuevo, fechas(c, "epagomenos")[i]!!))
        }
        // Cincuenta años después que Ipy, el año nuevo cae doce o trece días antes.
        val ipy = Calendarios.de("egipto")!!
        c.temporadas.indices.forEach { i -> assertTrue(entre(fechas(c, "anio_nuevo")[i]!!, fechas(ipy, "anio_nuevo")[i]!!) in 12..13) }
        // La Fiesta del Valle cae en el mes II Shemu, entre mediados de marzo y mediados de abril.
        (fechas(c, "valle") + fechas(c, "valle_obra")).filterNotNull().forEach { assertTrue(it, MesDia.indice(it) in MesDia.indice("03-15")..MesDia.indice("04-14")) }
        // La tumba de Seti II se termina para su entierro y la de Siptah empieza de cero.
        assertEquals(1.0, ficha(c, "2030-05-24").arcos.getValue("tumba"), 0.0)
        assertEquals(0.0, ficha(c, "2030-06-25").arcos.getValue("tumba"), 0.0)
        assertEquals("valle", ficha(c, "2030-04-01").estado); assertEquals(null, ficha(c, "2030-05-11").estado)
    }

    @Test fun amaSubeCadaVeranoYElFrisoCrece() {
        val c = AnioG3.AMA
        (2027..2031).forEach { y ->
            assertEquals(null, ficha(c, "$y-05-31").estado); assertEquals("verano", ficha(c, "$y-06-01").estado)
            assertEquals("verano", ficha(c, "$y-09-19").estado); assertEquals(null, ficha(c, "$y-09-20").estado)
        }
        val friso = (2027..2031).map { ficha(c, "$it-12-31").arcos.getValue("friso") }
        assertTrue("El friso no crece: $friso", friso.zipWithNext().all { (a, b) -> b > a })
        assertEquals(1.0, friso.last(), 0.0)
        // Sus años no tienen número.
        c.temporadas.forEach { assertTrue(it.cuando, it.cuando.none { ch -> ch.isDigit() }) }
        assertTrue(ficha(c, "2027-01-20").arcos.getValue("nieve") > 0.9); assertEquals(0.0, ficha(c, "2027-07-01").arcos.getValue("nieve"), 0.0)
    }

    @Test fun elTiempoDeKanCuadraConLaCuentaLarga() {
        val c = AnioG3.KAN
        // Los cinco días sin nombre terminan la víspera del año nuevo del haab.
        c.temporadas.indices.forEach { i -> assertEquals(5, entre(fechas(c, "wayeb")[i]!!, fechas(c, "pop")[i]!!)) }
        // Un tun son 360 días: del fin de tun de 750 al fin del k'atun de 751, a la subida de Pájaro Jaguar IV en 752 y a los
        // fines de tun de 753 y 754.
        val finales = listOf(750 to fechas(c, "fin_tun")[0]!!, 751 to fechas(c, "fin_katun")[1]!!, 752 to fechas(c, "entronizacion")[2]!!,
            753 to fechas(c, "fin_tun")[3]!!, 754 to fechas(c, "fin_tun")[4]!!)
        finales.zipWithNext().forEach { (a, b) -> assertEquals("${a.first} a ${b.first}", 360, entreAnios(a.first, a.second, b.first, b.second)) }
        // La estela se levanta el día del fin del k'atun (temporada 2).
        assertTrue(ficha(c, "2028-05-02").arcos.getValue("estela") < 1.0); assertEquals(1.0, ficha(c, "2028-05-03").arcos.getValue("estela"), 0.0)
        assertEquals("embajada", ficha(c, "2029-04-27").estado); assertEquals(null, ficha(c, "2029-05-15").estado)
    }

    @Test fun nanaiPasaElVeranoEnUnaCiudadYElInviernoEnElCamino() {
        val c = AnioG3.NANAI
        listOf(2027, 2029, 2031).forEach { assertEquals("changan", ficha(c, "$it-07-01").estado); assertEquals(1.0, ficha(c, "$it-07-01").arcos.getValue("ruta"), 0.0) }
        listOf(2028, 2030).forEach { assertEquals("samarcanda", ficha(c, "$it-07-01").estado); assertEquals(0.0, ficha(c, "$it-07-01").arcos.getValue("ruta"), 0.0) }
        (2027..2031).forEach { y ->
            assertEquals("$y: en enero está en el camino", null, ficha(c, "$y-01-15").estado)
            assertEquals("$y: las linternas, a los catorce días del año nuevo", 14, entre(fechas(c, "ano_chino")[y - 2027]!!, fechas(c, "linternas")[y - 2027]!!))
        }
        assertEquals(listOf(true, false, true, false, true), c.temporadas.map { t -> t.dias.any { it.id == "duanwu" } })
        assertEquals(listOf(false, true, false, true, false), c.temporadas.map { t -> t.dias.any { it.id == "vendimia" } })
    }
}
