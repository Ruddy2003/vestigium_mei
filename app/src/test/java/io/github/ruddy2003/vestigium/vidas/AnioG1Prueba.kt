package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioG1
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los calendarios del grupo 1 (tanda A1): Naa, Ata, Ipy, Lucio, Alice y Citlali. Las pruebas generales (forma, certeza,
 * fuentes, continuidad) están en [AnioPilotoPrueba] y corren solas sobre todo lo registrado en [Calendarios]; aquí va
 * lo propio de cada historia.
 */
class AnioG1Prueba {
    private fun ficha(c: Calendario, dia: String) = FichaDelDia.de(dia, OtrasVidas.porId(c.vida)!!, c)
    private fun fechas(c: Calendario, id: String) = c.temporadas.map { t -> t.dias.firstOrNull { it.id == id }?.fecha }

    @Test fun estanLosSeisRegistrados() {
        assertEquals(listOf("paleolitico", "neolitico", "egipto", "roma", "medieval", "tenochtitlan"), AnioG1.TODOS.map { it.vida })
        AnioG1.TODOS.forEach { c ->
            assertTrue("${c.vida} no está en el registro", Calendarios.de(c.vida) === c)
            assertEquals("${c.vida} es un personaje común, sin tema", null, OtrasVidas.porId(c.vida)!!.tema)
        }
    }

    @Test fun naaVeQueElLagoYaNoSeSeca() {
        val c = AnioG1.NAA
        // En las dos primeras temporadas el lago se cruza a pie en la seca; desde la tercera ya no.
        assertEquals(listOf(true, true, false, false, false), c.temporadas.map { t -> t.dias.any { it.id == "lago_seco" } })
        assertEquals(listOf(false, false, true, true, true), c.temporadas.map { t -> t.dias.any { it.id == "lago_alto" } })
        // El agua del lago en la seca sube cada año.
        val seca = (2027..2031).map { ficha(c, "$it-09-25").arcos.getValue("lago") }
        assertTrue("El lago no sube: $seca", seca.zipWithNext().all { (a, b) -> b > a })
        // Las lluvias largas llegan el 12 de marzo y con ellas la estación.
        assertEquals(Estacion.SECAS, ficha(c, "2027-03-11").estacion); assertEquals(Estacion.LLUVIAS, ficha(c, "2027-03-12").estacion)
        assertEquals("lluvias_largas", ficha(c, "2027-03-12").principal!!.dia.id)
        // Sese muere en la temporada 3 y ese mismo año dejan el campamento.
        val t3 = c.temporadas[2].dias.associateBy { it.id }
        assertEquals(MesDia.indice(t3.getValue("muere_sese").fecha) + 2, MesDia.indice(t3.getValue("dejan_campamento").fecha))
    }

    @Test fun ataSubeALaMontanaYElMonteHumea() {
        val c = AnioG1.ATA
        assertEquals(null, ficha(c, "2027-08-24").estado); assertEquals("montana", ficha(c, "2027-08-25").estado)
        assertEquals("montana", ficha(c, "2027-09-05").estado); assertEquals(null, ficha(c, "2027-09-06").estado)
        // Solo sube en las temporadas 1 y 5.
        assertEquals(null, ficha(c, "2030-08-30").estado)
        // El humo del monte aparece en la temporada 2 y la ceniza cae en la 3.
        assertEquals(0.0, ficha(c, "2028-05-17").arcos.getValue("humo"), 0.0)
        assertTrue(ficha(c, "2028-05-18").arcos.getValue("humo") > 0.5)
        assertEquals(1.0, ficha(c, "2029-04-03").arcos.getValue("humo"), 0.0)
        assertEquals(Clima.POLVO, ficha(c, "2029-04-02").clima)
        // La casa nueva se levanta en la temporada 4 y ya no baja.
        assertEquals(0.0, ficha(c, "2030-03-01").arcos.getValue("casa"), 0.0)
        assertEquals(1.0, ficha(c, "2030-07-01").arcos.getValue("casa"), 0.0)
        assertEquals(1.0, ficha(c, "2031-12-31").arcos.getValue("casa"), 0.0)
    }

    @Test fun lasFiestasDeIpySiguenElCalendarioCivil() {
        val c = AnioG1.IPY
        // El calendario civil de 365 días se corre un día cada cuatro años: entre 1250 y 1249 a. C. sus fiestas se
        // adelantan un día y luego se quedan quietas.
        assertEquals(listOf("07-02", "07-01", "07-01", "07-01", "07-01"), fechas(c, "anio_nuevo"))
        c.temporadas.forEach { t ->
            val d = t.dias.associateBy { it.id }
            val anio = MesDia.indice(d.getValue("anio_nuevo").fecha)
            assertEquals("Los cinco días fuera del año van justo antes del año nuevo", anio - 5, MesDia.indice(d.getValue("epagomenos").fecha))
            assertEquals("Opet empieza el 15 del segundo mes", anio + 44, MesDia.indice(d.getValue("opet").fecha))
            assertEquals("Joiak empieza el 18 del cuarto mes", anio + 107, MesDia.indice(d.getValue("joiak").fecha))
        }
        // Mientras el campo está bajo el agua, Ipy está en la obra del rey.
        assertEquals(null, ficha(c, "2027-08-27").estado); assertEquals("obra", ficha(c, "2027-08-28").estado)
        assertEquals("obra", ficha(c, "2027-10-27").estado); assertEquals(null, ficha(c, "2027-10-28").estado)
        // La crecida de la temporada 3 es baja.
        assertTrue(ficha(c, "2029-09-15").arcos.getValue("rio") < ficha(c, "2028-09-15").arcos.getValue("rio") - 0.3)
        // El jubileo del rey en la temporada 2 y la princesa hitita en la 5.
        assertEquals("jubileo", ficha(c, "2028-02-03").principal!!.dia.id)
        assertTrue(c.temporadas[4].dias.any { it.id == "princesa" && it.fuente != null })
    }

    @Test fun lucioVeElForoYLaColumna() {
        val c = AnioG1.LUCIO
        // Las fiestas de Roma no cambian de fecha: el calendario juliano no se corre.
        assertEquals(List(5) { "03-19" }, fechas(c, "quinquatrias"))
        assertEquals(5, ficha(c, "2027-12-21").principal!!.numero)
        // La columna se dedica el 12 de mayo de la temporada 4, y con ella el foro está terminado.
        val columna = ficha(c, "2030-05-12")
        assertEquals("columna", columna.principal!!.dia.id); assertEquals(CertezaDia.HISTORICO, columna.principal!!.dia.certeza)
        assertEquals(1.0, columna.arcos.getValue("foro"), 0.0)
        assertTrue(ficha(c, "2027-06-01").arcos.getValue("foro") < 0.9)
        val foro = (2027..2031).map { ficha(c, "$it-06-01").arcos.getValue("foro") }
        assertTrue("El foro baja: $foro", foro.zipWithNext().all { (a, b) -> b >= a })
    }

    @Test fun laPascuaDeAliceEstaCalculada() {
        val c = AnioG1.ALICE
        assertEquals(listOf("04-10", "04-02", "04-22", "04-07", "03-29"), fechas(c, "pascua"))
        c.temporadas.forEachIndexed { i, t ->
            val d = t.dias.associateBy { it.id }
            val pascua = MesDia.indice(d.getValue("pascua").fecha)
            // 1300 y 1304 son bisiestos en el calendario juliano: entre el carnaval y la Pascua hay un 29 de febrero, que
            // el calendario no cuenta (comparte lugar con el 28).
            val bisiesto = i == 0 || i == 4
            assertEquals("El carnaval va 47 días antes", pascua - if (bisiesto) 46 else 47, MesDia.indice(d.getValue("carnaval").fecha))
            assertEquals("Hocktide va 15 días después", pascua + 15, MesDia.indice(d.getValue("hock").fecha))
            assertEquals("Las rogativas van 36 días después", pascua + 36, MesDia.indice(d.getValue("rogativas").fecha))
            assertEquals("Pentecostés va 49 días después", pascua + 49, MesDia.indice(d.getValue("cerveza_pentecostes").fecha))
        }
        assertTrue(AnioG1.CALCULADOS.all { it in Calendarios.CALCULADOS })
        // La Navidad cruza el año y el 7 de enero vuelve el arado.
        assertEquals("navidad", ficha(c, "2028-01-05").principal!!.dia.id)
        assertEquals("arado", ficha(c, "2028-01-07").principal!!.dia.id)
    }

    @Test fun laChinampaDeCitlaliSeAhogaYSeRehace() {
        val c = AnioG1.CITLALI
        assertEquals(0.0, ficha(c, "2027-08-27").arcos.getValue("chinampa"), 0.0)
        assertEquals(1.0, ficha(c, "2027-08-28").arcos.getValue("chinampa"), 0.0)
        assertEquals("inundacion", ficha(c, "2027-08-28").escena)
        assertEquals(0.0, ficha(c, "2028-03-01").arcos.getValue("chinampa"), 0.0)
        // Lluvias de mediados de mayo a octubre.
        assertEquals(Estacion.SECAS, ficha(c, "2027-02-01").estacion); assertEquals(Estacion.LLUVIAS, ficha(c, "2027-07-01").estacion)
        // Los cinco días baldíos van antes del año nuevo de Sahagún, el 2 de febrero.
        val nemontemi = c.temporadas[0].dias.first { it.id == "nemontemi" }
        assertEquals(MesDia.indice("02-02"), MesDia.indice(nemontemi.fecha) + nemontemi.duracion)
        // Muere Ahuízotl y eligen a Moctezuma en la temporada 3; lo coronan en la 4.
        val t3 = c.temporadas[2].dias.map { it.id }; val t4 = c.temporadas[3].dias.map { it.id }
        assertTrue("muere_ahuizotl" in t3 && "eligen_moctezuma" in t3 && "coronacion" in t4)
    }
}
