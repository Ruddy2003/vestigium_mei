package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG6
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Los calendarios del grupo 6 (tanda A6): Albert, Nikos, Lou, Liudmila y Giulia. Las pruebas generales (forma, certeza,
 * fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en [Calendarios]; aquí va
 * lo propio de cada historia y lo que su cuadro va a leer.
 */
class AnioG6Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun dia(anio: Int, mmdd: String) = LocalDate.parse("%04d-%s".format(anio, mmdd))
    private fun entreAnios(anio: Int, a: String, b: String) = ChronoUnit.DAYS.between(dia(anio, a), dia(anio, b)).toInt()
    private fun estadoEntre(c: Calendario, clave: String, desde: String, hasta: String, despues: Boolean = true) {
        val a = LocalDate.parse(desde); val b = LocalDate.parse(hasta)
        assertTrue("${c.vida}: el día antes de $desde", ficha(c, a.minusDays(1).toString()).estado != clave)
        var d = a
        while (!d.isAfter(b)) { assertEquals("${c.vida}, $d", clave, ficha(c, d.toString()).estado); d = d.plusDays(1) }
        if (despues) assertTrue("${c.vida}: el día después de $hasta", ficha(c, b.plusDays(1).toString()).estado != clave)
    }
    /** La Pascua gregoriana (algoritmo anónimo de Meeus, Jones y Butcher). */
    private fun pascuaGregoriana(y: Int): LocalDate {
        val a = y % 19; val b = y / 100; val c = y % 100; val d = b / 4; val e = b % 4; val f = (b + 8) / 25; val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30; val i = c / 4; val k = c % 4; val l = (32 + 2 * e + 2 * i - h - k) % 7; val m = (a + 11 * h + 22 * l) / 451
        return LocalDate.of(y, (h + l - 7 * m + 114) / 31, (h + l - 7 * m + 114) % 31 + 1)
    }
    /** La Pascua ortodoxa: la juliana (algoritmo de Meeus), pasada al gregoriano con los trece días de los años 1900 a 2099. */
    private fun pascuaOrtodoxa(y: Int): LocalDate {
        val a = y % 4; val b = y % 7; val c = y % 19; val d = (19 * c + 15) % 30; val e = (2 * a + 4 * b - d + 34) % 7
        return LocalDate.of(y, (d + e + 114) / 31, (d + e + 114) % 31 + 1).plusDays(13)
    }

    @Test fun estanLosCincoYCadaUnoConSuTema() {
        assertEquals(listOf("fogonero", "buzo_esponjas", "pianista", "obrera_moscu", "restauradora"), AnioG6.TODOS.map { it.vida })
        val temas = mapOf("fogonero" to "vapor", "buzo_esponjas" to "abismo", "pianista" to "deco", "obrera_moscu" to "urss", "restauradora" to "urbe")
        AnioG6.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida}: su tema", temas[c.vida], OtrasVidas.porId(c.vida)!!.tema)
            assertTrue("${c.vida}: el cuadro necesita al menos un arco", c.arcos.isNotEmpty())
            assertTrue("${c.vida}: el cuadro necesita al menos un estado", c.estados.isNotEmpty())
        }
    }

    @Test fun albertTrabajaEnLasFiestasDeLosBancosYLlegaAMaquinista() {
        val c = AnioG6.ALBERT
        c.temporadas.indices.forEach { i ->
            val y = 1880 + i; val pascua = pascuaGregoriana(y)
            assertEquals("$y: lunes de Pascua", pascua.plusDays(1), dia(y, fechas(c, "lunes_pascua")[i]!!))
            assertEquals("$y: martes de carnaval", pascua.minusDays(47), dia(y, fechas(c, "martes_tortitas")[i]!!))
            assertEquals("$y: lunes de Pentecostés", pascua.plusDays(50), dia(y, fechas(c, "lunes_pentecostes")[i]!!))
            val gala = dia(y, fechas(c, "gala")[i]!!); assertTrue("$y: la gala", gala.dayOfWeek == DayOfWeek.SATURDAY && gala.dayOfMonth in 8..14)
            val agosto = dia(y, fechas(c, "lunes_agosto")[i]!!); assertTrue("$y: agosto", agosto.dayOfWeek == DayOfWeek.MONDAY && agosto.dayOfMonth in 1..7)
        }
        estadoEntre(c, "baja", "2029-06-20", "2029-07-19")
        estadoEntre(c, "maquinista", "2031-09-10", "2031-12-31", despues = false)
        assertEquals(0.0, ficha(c, "2027-05-03").arcos.getValue("viaducto"), 0.0)
        assertEquals(1.0, ficha(c, "2030-06-01").arcos.getValue("viaducto"), 0.0)
    }

    @Test fun nikosVaConElCalendarioViejoYPasaElInviernoEnPuerto() {
        val c = AnioG6.NIKOS
        // El juliano va trece días detrás: las fiestas fijas de la Iglesia caen trece días después.
        mapOf("navidad_vieja" to "12-25", "san_basilio" to "01-01", "epifania" to "01-06", "dormicion" to "08-15", "san_nicolas" to "12-06").forEach { (id, juliano) ->
            val f = fechas(c, id).first()!!
            assertEquals(id, 13, entreAnios(1910, juliano, f).let { if (it < 0) it + 365 else it })
        }
        c.temporadas.indices.forEach { i -> assertEquals("${1910 + i}: la Pascua ortodoxa", pascuaOrtodoxa(1910 + i), dia(1910 + i, fechas(c, "pascua")[i]!!)) }
        estadoEntre(c, "puerto", "2027-12-18", "2028-01-25")
        estadoEntre(c, "enfermo", "2030-06-12", "2030-07-17")
        (2027..2031).forEach { y ->
            assertTrue(ficha(c, "$y-06-25").arcos.getValue("esponjas") > 0.9); assertTrue(ficha(c, "$y-06-26").arcos.getValue("esponjas") < 0.2)
        }
    }

    @Test fun louTocaHastaElCracYVuelveAEmpezar() {
        val c = AnioG6.LOU
        c.temporadas.indices.forEach { i ->
            val y = 1926 + i
            assertEquals("$y: Pascua", pascuaGregoriana(y), dia(y, fechas(c, "pascua")[i]!!))
            val gracias = dia(y, fechas(c, "accion_gracias")[i]!!); assertTrue("$y: Acción de Gracias, el último jueves", gracias.dayOfWeek == DayOfWeek.THURSDAY && gracias.dayOfMonth + 7 > 30)
            val trabajo = dia(y, fechas(c, "dia_trabajo")[i]!!); assertTrue("$y: día del Trabajo", trabajo.dayOfWeek == DayOfWeek.MONDAY && trabajo.dayOfMonth in 1..7)
        }
        estadoEntre(c, "gira", "2029-06-18", "2029-08-29")
        estadoEntre(c, "sin_club", "2031-02-17", "2031-05-04")
        assertEquals("10-24", fechas(c, "jueves_negro")[3]); assertEquals("10-29", fechas(c, "martes_negro")[3])
        assertTrue(ficha(c, "2030-10-23").arcos.getValue("publico") - ficha(c, "2030-11-20").arcos.getValue("publico") >= 0.5)
    }

    @Test fun liudmilaSigueLasFiestasSovieticasYElPlan() {
        val c = AnioG6.LIUDMILA
        c.temporadas.indices.forEach { i -> assertEquals("${1975 + i}: el sábado de trabajo", DayOfWeek.SATURDAY, dia(1975 + i, fechas(c, "subbotnik")[i]!!).dayOfWeek) }
        // El día de la Constitución cambia de fecha con la Constitución de 1977.
        assertEquals(listOf("12-05", "12-05", "10-07", "10-07", "10-07"), fechas(c, "constitucion"))
        c.temporadas.indices.forEach { i -> fechas(c, "pascua_suegra")[i]?.let { assertEquals("${1975 + i}: Pascua", pascuaOrtodoxa(1975 + i), dia(1975 + i, it)) } }
        estadoEntre(c, "sanatorio", "2028-07-05", "2028-07-25")
        estadoEntre(c, "sanatorio", "2030-07-10", "2030-07-30")
        (2027..2031).forEach { y -> assertEquals(1.0, ficha(c, "$y-12-28").arcos.getValue("plan"), 0.0); assertEquals(0.0, ficha(c, "$y-12-29").arcos.getValue("plan"), 0.0) }
    }

    @Test fun giuliaVeElAnioDeLosDosPapasYSoloLoProgramadoDelFuturo() {
        val c = AnioG6.GIULIA
        c.temporadas.indices.forEach { i -> assertEquals("${2025 + i}: Pascua", pascuaGregoriana(2025 + i), dia(2025 + i, fechas(c, "pascua")[i]!!)) }
        // 2025: el papa muere el lunes de Pascua, que es también el cumpleaños de Roma; el cónclave, el 7 de mayo, y el papa nuevo, el 8.
        assertEquals(pascuaGregoriana(2025).plusDays(1), dia(2025, fechas(c, "francisco")[0]!!))
        assertEquals(null, fechas(c, "natale_roma")[0])
        assertEquals("05-07", fechas(c, "conclave")[0]); assertEquals("05-08", fechas(c, "leon")[0])
        // Desde 2027 es futuro: lo único «histórico» son las fiestas que vuelven cada año.
        val deCadaAnio = c.temporadas.flatMap { t -> t.dias.map { it.id } }.groupingBy { it }.eachCount().filterValues { it >= 2 }.keys
        (2..4).forEach { i -> c.temporadas[i].dias.filter { it.id !in deCadaAnio }.forEach { d -> assertTrue("${2025 + i}, «${d.id}»", d.certeza != CertezaDia.HISTORICO) } }
        (2027..2031).forEach { y -> estadoEntre(c, "agosto", "$y-08-01", "$y-08-30") }
        assertEquals(1.0, ficha(c, "2031-10-12").arcos.getValue("fresco"), 0.0)
    }
}
