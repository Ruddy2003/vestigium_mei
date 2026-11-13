package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG2
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Los calendarios del grupo 2 (tanda A2): Kichizō, Mary, José, Rosa, Alguien del mundo y Alguien de México. Las
 * pruebas generales (forma, certeza, fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo
 * registrado en [Calendarios]; aquí va lo propio de cada historia.
 */
class AnioG2Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }
    private fun dia(c: Calendario, temporada: Int, id: String) = c.temporadas[temporada - 1].dias.first { it.id == id }

    /** Los días entre dos fechas «MM-DD» del mismo año. */
    private fun entre(a: String, b: String) = MesDia.indice(b) - MesDia.indice(a)

    /** La Pascua gregoriana del año [y] (algoritmo de Meeus, Jones y Butcher), calculada aquí por separado. */
    private fun pascua(y: Int): LocalDate {
        val a = y % 19; val b = y / 100; val c = y % 100; val d = b / 4; val e = b % 4; val f = (b + 8) / 25; val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30; val i = c / 4; val k = c % 4; val l = (32 + 2 * e + 2 * i - h - k) % 7; val m = (a + 11 * h + 22 * l) / 451
        val mes = (h + l - 7 * m + 114) / 31; val dia = (h + l - 7 * m + 114) % 31 + 1
        return LocalDate.of(y, mes, dia)
    }

    private fun mmdd(d: LocalDate) = "%02d-%02d".format(d.monthValue, d.dayOfMonth)

    /** Que el Viernes Santo de cada temporada sea el de la Pascua de su año histórico. */
    private fun viernesSantoCuadra(c: Calendario, primerAnio: Int) {
        fechas(c, "viernes_santo").forEachIndexed { i, f -> assertEquals("${c.vida}, temporada ${i + 1}", mmdd(pascua(primerAnio + i).minusDays(2)), f) }
    }

    @Test fun estanLosSeisRegistrados() {
        assertEquals(listOf("edo", "manchester", "hacienda", "cdmx1960", "mundo2025", "mexico2025"), AnioG2.TODOS.map { it.vida })
        AnioG2.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida} es un personaje común, sin tema", null, OtrasVidas.porId(c.vida)!!.tema)
        }
    }

    @Test fun lasFiestasDeKichizoSiguenLaLuna() {
        val c = AnioG2.KICHIZO
        // El año nuevo lunar cae cada año en otra fecha, entre el 20 de enero y el 20 de febrero, y la última noche del
        // año es la víspera.
        val anioNuevo = fechas(c, "shogatsu").map { it!! }
        assertEquals(5, anioNuevo.toSet().size)
        anioNuevo.forEach { assertTrue(it, MesDia.indice(it) in MesDia.indice("01-20")..MesDia.indice("02-20")) }
        anioNuevo.zip(fechas(c, "omisoka")).forEach { (a, o) -> assertEquals(MesDia.indice(a) - 1, MesDia.indice(o!!)) }
        // Las fiestas de un mismo año lunar guardan su distancia: de 5/5 a 7/7 van 61 o 62 días (dos meses lunares y
        // dos días), salvo cuando en medio cae un mes intercalado (el 5 bis de la temporada 2).
        c.temporadas.indices.forEach { i ->
            val dias = entre(fechas(c, "tango")[i]!!, fechas(c, "tanabata")[i]!!)
            if (i == 1) assertTrue("Temporada 2: $dias", dias in 90..92) else assertTrue("Temporada ${i + 1}: $dias", dias in 60..62)
        }
        // Sannō y Kanda se turnan: Sannō los años pares (1750, 1752, 1754) y Kanda los nones.
        assertEquals(listOf(true, false, true, false, true), c.temporadas.map { t -> t.dias.any { it.id == "sanno" } })
        assertEquals(listOf(false, true, false, true, false), c.temporadas.map { t -> t.dias.any { it.id == "kanda" } })
        // Yoshimune muere el 12 de julio de 1751 (el día 20 del quinto mes bis), en la temporada 2.
        assertEquals("07-12", dia(c, 2, "yoshimune").fecha); assertEquals(CertezaDia.HISTORICO, dia(c, 2, "yoshimune").certeza)
    }

    @Test fun laCasaDeKichizoSeQuemaYSeLevanta() {
        val c = AnioG2.KICHIZO
        assertEquals(1.0, ficha(c, "2030-03-13").arcos.getValue("casa"), 0.0)
        assertEquals(0.0, ficha(c, "2030-03-14").arcos.getValue("casa"), 0.0)
        assertEquals(1.0, ficha(c, "2030-06-01").arcos.getValue("casa"), 0.0)
        assertEquals(1.0, ficha(c, "2031-12-31").arcos.getValue("casa"), 0.0)
        // Mientras la levantan duermen en el templo.
        assertEquals(null, ficha(c, "2030-03-13").estado); assertEquals("sin_casa", ficha(c, "2030-04-02").estado)
        assertEquals("sin_casa", ficha(c, "2030-05-24").estado); assertEquals(null, ficha(c, "2030-05-25").estado)
        // La enfermedad de Edo se cura en la aldea, entre el Bon y la luna de otoño de la temporada 3.
        assertEquals("aldea", ficha(c, "2029-09-01").estado); assertEquals(null, ficha(c, "2029-09-19").estado)
        assertTrue(MesDia.indice("08-27") > MesDia.indice(fechas(c, "bon")[2]!!) + 3)
        assertTrue(MesDia.indice("09-19") < MesDia.indice(fechas(c, "tsukimi")[2]!!))
    }

    @Test fun maryPasaLaHuelgaSinFabrica() {
        val c = AnioG2.MARY
        // La huelga llega a Manchester el 13 de agosto de 1842 y se acaba el 26 de septiembre.
        assertEquals(null, ficha(c, "2029-08-12").estado); assertEquals("paro", ficha(c, "2029-08-13").estado)
        assertEquals("paro", ficha(c, "2029-09-25").estado); assertEquals(null, ficha(c, "2029-09-26").estado)
        // Las fiestas que cuelgan de la Pascua de 1840 a 1844.
        viernesSantoCuadra(c, 1840)
        c.temporadas.indices.forEach { i ->
            val p = pascua(1840 + i)
            assertEquals(mmdd(p.minusDays(47)), fechas(c, "carnaval")[i])
            assertEquals(mmdd(p.plusDays(50)), fechas(c, "whit")[i])
            assertEquals(mmdd(p.plusDays(52)), fechas(c, "kersal")[i])
        }
        // Las cuatro semanas después de que nace Tom.
        assertEquals("en_casa", ficha(c, "2030-11-05").estado); assertEquals(null, ficha(c, "2030-11-17").estado)
    }

    @Test fun laMilpaDeJoseYSuMano() {
        val c = AnioG2.JOSE
        viernesSantoCuadra(c, 1905)
        c.temporadas.indices.forEach { i ->
            val p = pascua(1905 + i)
            assertEquals(mmdd(p.minusDays(46)), fechas(c, "ceniza")[i])
            assertEquals(mmdd(p.minusDays(1)), fechas(c, "judas")[i])
        }
        // El maíz del pegujal: nada en abril, crece con las lluvias y se pizca en noviembre.
        assertEquals(0.0, ficha(c, "2028-04-15").arcos.getValue("milpa"), 0.0)
        assertTrue(ficha(c, "2028-08-20").arcos.getValue("milpa") > 0.5)
        assertEquals(1.0, ficha(c, "2028-11-05").arcos.getValue("milpa"), 0.0)
        assertEquals(0.0, ficha(c, "2028-12-15").arcos.getValue("milpa"), 0.0)
        // La púa de maguey: dos semanas sin raspar en la temporada 3.
        assertEquals("herido", ficha(c, "2029-07-02").estado); assertEquals(null, ficha(c, "2029-07-16").estado)
        assertEquals(Estacion.SECAS, ficha(c, "2027-05-24").estacion); assertEquals(Estacion.LLUVIAS, ficha(c, "2027-05-25").estacion)
    }

    @Test fun rosaSeCasaYTieneASuHija() {
        val c = AnioG2.ROSA
        viernesSantoCuadra(c, 1960)
        assertEquals("acapulco", ficha(c, "2028-06-20").estado); assertEquals(null, ficha(c, "2028-06-24").estado)
        assertEquals("incapacidad", ficha(c, "2029-06-07").estado); assertEquals(null, ficha(c, "2029-07-28").estado)
        // La boda, la luna de miel y la mudanza van en una sola cadena, en orden.
        val t2 = c.temporadas[1].dias.filter { it.cadena == "boda" }.map { it.id }
        assertEquals(listOf("boda", "va_acapulco", "vuelve_acapulco"), t2)
    }

    @Test fun lasFiestasDeLaIndiaSiguenLaLuna() {
        val c = AnioG2.MUNDO
        c.temporadas.indices.forEach { i ->
            val n = "Temporada ${i + 1}"
            // Diwali cae en la luna nueva de octubre o noviembre, unos veinte días después de Dussehra, y Chhath empieza
            // en el cuarto día de la luna que sigue: de cuatro a seis días después de Diwali.
            val diwali = fechas(c, "diwali")[i]!!
            assertTrue("$n: Diwali el $diwali", MesDia.indice(diwali) in MesDia.indice("10-15")..MesDia.indice("11-15"))
            assertTrue("$n: de Dussehra a Diwali", entre(fechas(c, "dussehra")[i]!!, diwali) in 18..21)
            assertTrue("$n: de Diwali a Chhath", entre(diwali, fechas(c, "chhath")[i]!!) in 4..6)
            // Holi, en la luna llena de fines del invierno.
            assertTrue("$n: Holi", MesDia.indice(fechas(c, "holi")[i]!!) in MesDia.indice("02-25")..MesDia.indice("03-25"))
        }
        assertEquals("aldea", ficha(c, "2028-07-25").estado); assertEquals(null, ficha(c, "2028-08-04").estado)
    }

    @Test fun lasVidasDeDosMilVeinticincoNoInventanElFuturo() {
        // De 2027 en adelante (temporadas 3 a 5) nada de su mundo es «histórico»: solo fiestas que vuelven y cosas
        // programadas, como «aproximado». Y cada una tiene al menos una cosa programada con su fuente.
        listOf(AnioG2.MUNDO, AnioG2.MEXICO).forEach { c ->
            (3..5).forEach { t ->
                val delMundo = c.temporadas[t - 1].dias.filter { it.tipo == TipoDia.MUNDO || it.tipo == TipoDia.GUERRA }
                delMundo.forEach { assertTrue("${c.vida}, temporada $t, «${it.id}»", it.certeza != CertezaDia.HISTORICO) }
                assertTrue("${c.vida}, temporada $t: nada programado", delMundo.any { it.fuente != null && it.id !in setOf("monzon", "simulacro") })
            }
        }
    }

    @Test fun alguienDeMexicoVaAlBuenFinYAlMundial() {
        val c = AnioG2.MEXICO
        viernesSantoCuadra(c, 2025)
        // El Buen Fin va del jueves al lunes del puente de la Revolución, que es el tercer lunes de noviembre.
        fechas(c, "buen_fin").forEachIndexed { i, f ->
            val lunes = LocalDate.of(2025 + i, 11, 1).with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY))
            assertEquals("Temporada ${i + 1}", mmdd(lunes.minusDays(4)), f)
        }
        assertEquals("06-11", dia(c, 2, "inauguracion").fecha)
        assertEquals("playa", ficha(c, "2028-07-22").estado); assertEquals(null, ficha(c, "2028-07-27").estado)
    }
}
