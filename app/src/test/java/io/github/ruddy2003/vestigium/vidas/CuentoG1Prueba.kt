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
 * Los cuentos del grupo 1. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T1a: Naa, Ata e Ipy. Tanda T1b: Lucio, Alice y Citlali.
 */
class CuentoG1Prueba {
    private fun pieza(vida: String) = TodasLasPiezas.de(vida)!!
    private fun cal(vida: String) = Calendarios.de(vida)!!
    private fun quien(vida: String) = OtrasVidas.porId(vida)!!
    private fun cuento(vida: String, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(vida), cal(vida)), quien(vida), cal(vida), pieza(vida), hora)
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a siete horas distintas (cada una con su párrafo del trabajo). */
    private fun todoElDia(vida: String, dia: String) = listOf(1.0, 5.75, 9.0, 12.5, 16.0, 18.5, 20.0).flatMap { cuento(vida, dia, it).parrafos }.joinToString(" ")
    /** Los días que no son especiales: en ellos solo salen las variantes de todos los días. */
    private fun normales(vida: String, desde: String, hasta: String) = dias(desde, hasta).filter { !cuento(vida, it).especial }
    private fun textosDe(vida: String, lugar: Parrafo, estado: String) =
        pieza(vida).variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to quien(vida).nombre), pieza(vida).genero) }.toSet()

    /** Los ids que vuelven en más de una temporada: sus textos valen los cinco años. */
    private fun deCadaAnio(v: String) = cal(v).temporadas.flatMap { t -> t.dias.map { it.id } }.groupingBy { it }.eachCount().filterValues { it >= 2 }.keys
    private fun noNombran(v: String, nombres: List<String>) = deCadaAnio(v).forEach { id -> pieza(v).especiales[id]?.let { t ->
        (t.dias.flatMap { it.values } + t.sigue.values.flatten()).forEach { s -> nombres.forEach { assertFalse("$v, «$id» nombra a $it: «${s.take(50)}…»", it in s) } }
    } }

    @Test fun estanLasPiezasDeLasTandasT1aYT1b() {
        listOf("paleolitico", "neolitico", "egipto", "roma", "medieval", "tenochtitlan").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
        }
        listOf("paleolitico", "medieval", "tenochtitlan").forEach { assertEquals(Genero.FEMENINO, pieza(it).genero) }
        // Naa y Ata viven antes de cualquier año con número: el cuento no lo dice.
        listOf("paleolitico", "neolitico").forEach { v -> dias("2027-01-01", "2027-12-31").forEach { d -> assertFalse("$v, $d", "a. C." in cuento(v, d).parrafos.joinToString(" ")) } }
    }

    // ---------- Naa ----------

    @Test fun enElCuentoDeNaaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "paleolitico"
        // Sese muere el 18 de febrero de la temporada 3.
        normales(v, "2029-02-18", "2031-12-31").forEach { assertFalse("$it: Sese después de morir", "Sese" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-01", "2027-12-31").any { "Sese" in todoElDia(v, it) })
        // Mai nace el 2 de mayo de la temporada 2; antes, Naa la espera desde el 2 de septiembre de la 1.
        normales(v, "2027-01-01", "2028-05-01").forEach { assertFalse("$it: Mai antes de nacer", "Mai" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-05-21", "2028-12-31").any { "Mai" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2027-09-01").forEach { assertFalse("$it: espera un hijo antes de tiempo", "lleva otro hijo dentro" in todoElDia(v, it)) }
        normales(v, "2028-05-02", "2031-12-31").forEach { assertFalse("$it: sigue esperando", "lleva otro hijo dentro" in todoElDia(v, it)) }
        // Iru se va el 30 de julio de la temporada 2 y vuelve el 5 de octubre de la 3.
        normales(v, "2028-07-30", "2029-10-04").forEach { assertFalse("$it: Iru mientras está en otro campamento", "Iru" in todoElDia(v, it)) }
        assertTrue(normales(v, "2029-10-06", "2029-12-31").any { "Iru volvió" in todoElDia(v, it) })
    }

    @Test fun lasFiestasDeNaaNoNombranANadieQueLlegueOSeVaya() {
        val c = cal("paleolitico")
        val deCadaAnio = c.temporadas.flatMap { t -> t.dias.map { it.id } }.groupingBy { it }.eachCount().filterValues { it >= 2 }.keys
        deCadaAnio.forEach { id -> pieza("paleolitico").especiales[id]?.let { t ->
            (t.dias.flatMap { it.values } + t.sigue.values.flatten()).forEach { s -> listOf("Sese", "Mai", "Iru", "Ekwa").forEach { assertFalse("«$id» nombra a $it: «${s.take(50)}…»", it in s) } }
        } }
    }

    // ---------- Ata ----------

    @Test fun ataEnLaMontanaNoAmaneceEnCasa() {
        val v = "neolitico"
        val manana = textosDe(v, Parrafo.MANANA, "montana"); val trabajo = textosDe(v, Parrafo.TRABAJO, "montana")
        (dias("2027-08-26", "2027-09-05")).forEach { dia ->
            assertEquals("montana", cuento(v, dia).ficha.estado)
            assertTrue("$dia: amanece en casa", cuento(v, dia, 9.0).parrafos[Parrafo.MANANA.ordinal] in manana)
            listOf(5.75, 7.0, 10.0, 13.5, 19.0, 23.0).forEach { h -> assertTrue("$dia, $h h: trabaja en casa", cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
        }
        // Fuera de la montaña no sale nada de la montaña.
        val deMontana = pieza(v).variantes.values.flatten().filter { it.estado == "montana" }.map { Cuento.llenar(it.texto, mapOf("nombre" to "Ata"), Genero.MASCULINO) }
        normales(v, "2027-01-01", "2028-12-31").filter { cuento(v, it).ficha.estado == null }.forEach { dia -> deMontana.forEach { assertFalse("$dia: la montaña sin estar en ella", it in todoElDia(v, dia)) } }
    }

    @Test fun enElCuentoDeAtaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "neolitico"
        // Ari muere el 8 de marzo de la temporada 2; Seli nace el 5 de agosto de la 4.
        normales(v, "2028-03-08", "2031-12-31").forEach { assertFalse("$it: Ari después de morir", "Ari" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-01", "2027-12-31").any { "Ari" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2030-08-04").forEach { assertFalse("$it: Seli antes de nacer", "Seli" in todoElDia(v, it)) }
        assertTrue(normales(v, "2030-08-06", "2030-12-31").any { "Seli" in todoElDia(v, it) })
        // Siembra en otoño, escarda en primavera y corta juncos en invierno.
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() !in 10..11 }.forEach { assertFalse("$it: siembra fuera de tiempo", "A esta hora siembra" in cuento(v, it, 10.0).parrafos[Parrafo.TRABAJO.ordinal]) }
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() !in 3..6 }.forEach { assertFalse("$it: escarda fuera de tiempo", "escarda el trigo" in cuento(v, it, 10.0).parrafos[Parrafo.TRABAJO.ordinal]) }
    }

    // ---------- Ipy ----------

    @Test fun ipyPasaLaCrecidaEnLaObraDelRey() {
        val v = "egipto"
        val trabajo = textosDe(v, Parrafo.TRABAJO, "obra")
        (2027..2031).forEach { anio ->
            assertEquals(null, cuento(v, "$anio-08-27").ficha.estado); assertEquals("obra", cuento(v, "$anio-08-28").ficha.estado)
            assertEquals("obra", cuento(v, "$anio-10-27").ficha.estado); assertEquals(null, cuento(v, "$anio-10-28").ficha.estado)
            dias("$anio-08-29", "$anio-10-27").forEach { dia ->
                listOf(9.0, 12.0, 15.0, 18.5, 20.0, 23.0).forEach { h -> assertTrue("$dia, $h h: trabaja en su campo, que está bajo el agua", cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
            }
        }
        // En su casa no sale nada de la obra.
        val deObra = pieza(v).variantes.values.flatten().filter { it.estado == "obra" }.map { Cuento.llenar(it.texto, mapOf("nombre" to "Ipy"), Genero.MASCULINO) }
        normales(v, "2027-01-01", "2027-12-31").filter { cuento(v, it).ficha.estado == null }.forEach { dia -> deObra.forEach { assertFalse("$dia: la obra estando en casa", it in todoElDia(v, dia)) } }
    }

    @Test fun enElCuentoDeIpyNadieApareceAntesNiDespuesDeTiempo() {
        val v = "egipto"
        // Baket muere el 28 de abril de la temporada 3; Amenemope nace el 10 de junio de la 4.
        normales(v, "2029-04-28", "2031-12-31").forEach { assertFalse("$it: Baket después de morir", "Baket, vive" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-01", "2027-12-31").any { "Baket" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2030-06-09").forEach { assertFalse("$it: Amenemope antes de nacer", "Amenemope" in todoElDia(v, it)) }
        assertTrue(normales(v, "2030-06-11", "2030-12-31").any { "Amenemope" in todoElDia(v, it) })
        // En la estación fresca no se duerme en el techo, y cada faena va en su tiempo.
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() in listOf(11, 12, 1, 2, 3) }.forEach { dia ->
            assertFalse("$dia: duerme en el techo con frío", "duerme en el techo" in cuento(v, dia, 23.0).parrafos[Parrafo.TRABAJO.ordinal])
        }
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() !in 3..5 }.forEach { assertFalse("$it: siega fuera de tiempo", "A esta hora siega" in cuento(v, it, 9.0).parrafos[Parrafo.TRABAJO.ordinal]) }
    }

    // ---------- Lucio ----------

    @Test fun enElCuentoDeLucioNadieApareceAntesNiDespuesDeTiempo() {
        val v = "roma"
        // Félix es aprendiz del 12 de enero de la temporada 1 al 12 de abril de la 5, cuando abre su puesto.
        normales(v, "2027-01-01", "2027-01-11").forEach { assertFalse("$it: Félix antes de llegar", "Félix" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-13", "2027-12-31").any { "Félix, el aprendiz" in todoElDia(v, it) })
        normales(v, "2031-04-12", "2031-12-31").forEach { assertFalse("$it: Félix sigue de aprendiz", "Félix, el aprendiz" in todoElDia(v, it) || "le enseña a Félix" in todoElDia(v, it)) }
        // Sexto es marinero hasta que deja el mar, el 2 de diciembre de la temporada 4; Tito nace el 30 de junio de la 4.
        normales(v, "2030-12-02", "2031-12-31").forEach { assertFalse("$it: Sexto sigue en el mar", "pasa media vida en el mar" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2030-12-01").forEach { assertFalse("$it: Sexto en el taller antes de tiempo", "dejó el mar" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2030-06-29").forEach { assertFalse("$it: Tito antes de nacer", "Tito" in todoElDia(v, it)) }
        assertTrue(normales(v, "2030-07-01", "2030-12-31").any { "Tito" in todoElDia(v, it) })
        // Marco: en la calle hasta la escuela (24 de marzo de la temporada 1), en la escuela hasta el taller (25 de mayo de la 5).
        normales(v, "2027-03-24", "2031-12-31").forEach { assertFalse("$it: Marco sigue en la calle", "Marco anda por la calle" in todoElDia(v, it)) }
        noNombran(v, listOf("Félix", "Sexto", "Tito"))
    }

    // ---------- Alice ----------

    @Test fun enElCuentoDeAliceNadieApareceAntesNiDespuesDeTiempo() {
        val v = "medieval"
        // Edith muere el 28 de noviembre de la temporada 2; Agnes entra a servir el 5 de octubre de la 2.
        normales(v, "2028-11-28", "2031-12-31").forEach { assertFalse("$it: Edith después de morir", "Edith" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-01", "2027-12-31").any { "Edith" in todoElDia(v, it) })
        normales(v, "2028-10-05", "2031-12-31").forEach { assertFalse("$it: Agnes sigue en casa", "Agnes, la mayor" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-10-06", "2028-12-31").any { "Agnes sirve ahora" in todoElDia(v, it) })
        // Matilda nace el 3 de abril de la temporada 3; Joan se casa el 20 de enero de la 1.
        normales(v, "2027-01-01", "2029-04-02").forEach { assertFalse("$it: Matilda antes de nacer", "Matilda" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2027-01-19").forEach { assertFalse("$it: Joan casada antes de la boda", "Joan" in todoElDia(v, it)) }
        // Cada faena en su estación: la trilla en invierno, la escarda en mayo y junio.
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() !in listOf(12, 1, 2) }.forEach { assertFalse("$it: trilla fuera de tiempo", "trilla en el granero" in cuento(v, it, 9.0).parrafos[Parrafo.TRABAJO.ordinal]) }
        normales(v, "2027-01-01", "2031-12-31").filter { it.substring(5, 7).toInt() !in 5..6 }.forEach { assertFalse("$it: escarda fuera de tiempo", "escarda el trigo" in cuento(v, it, 9.0).parrafos[Parrafo.TRABAJO.ordinal]) }
        noNombran(v, listOf("Edith", "Agnes", "Joan", "Matilda"))
    }

    // ---------- Citlali ----------

    @Test fun citlaliViveFueraMientrasLaChinampaEstaBajoElAgua() {
        val v = "tenochtitlan"
        assertEquals(null, cuento(v, "2027-08-27").ficha.estado); assertEquals("fuera", cuento(v, "2027-08-28").ficha.estado)
        assertEquals("fuera", cuento(v, "2027-10-19").ficha.estado); assertEquals(null, cuento(v, "2027-10-20").ficha.estado)
        val manana = textosDe(v, Parrafo.MANANA, "fuera"); val trabajo = textosDe(v, Parrafo.TRABAJO, "fuera")
        normales(v, "2027-08-29", "2027-10-19").forEach { dia -> assertTrue("$dia: amanece en su casa", cuento(v, dia, 9.0).parrafos[Parrafo.MANANA.ordinal] in manana) }
        dias("2027-08-29", "2027-10-19").forEach { dia ->
            listOf(5.0, 9.0, 12.5, 14.0, 17.0, 19.0, 23.0).forEach { h -> assertTrue("$dia, $h h: trabaja en su casa", cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
        }
        // De la inundación al primer almácigo no hay nada que cosechar en la chinampa; las flores empiezan en la temporada 4.
        val cultivo = listOf("recoge chiles y tomates", "escarda el maíz", "pasa las plantitas", "cosecha lo que da la chinampa", "corta flores")
        dias("2027-08-28", "2027-12-01").forEach { dia -> cultivo.forEach { assertFalse("$dia: «$it» con la chinampa ahogada", it in todoElDia(v, dia)) } }
        assertTrue(normales(v, "2027-01-01", "2027-08-27").any { dia -> cultivo.any { it in todoElDia(v, dia) } })
        assertTrue(normales(v, "2027-12-03", "2028-12-31").any { dia -> cultivo.any { it in todoElDia(v, dia) } })
        normales(v, "2027-01-01", "2030-03-27").forEach { assertFalse("$it: flores antes de sembrarlas", "corta flores" in todoElDia(v, it)) }
    }

    @Test fun enElCuentoDeCitlaliNadieApareceAntesNiDespuesDeTiempo() {
        val v = "tenochtitlan"
        // Xóchitl nace el 20 de junio de la temporada 2; Papan deja de bordar el 28 de octubre de la 3.
        normales(v, "2027-01-01", "2028-06-19").forEach { assertFalse("$it: Xóchitl antes de nacer", "Xóchitl" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-06-25", "2028-12-31").any { "Xóchitl" in todoElDia(v, it) })
        normales(v, "2029-10-28", "2031-12-31").forEach { assertFalse("$it: Papan borda sin ver", "Papan borda" in todoElDia(v, it)) }
        // Mazatl recibe su manta el 16 de junio de la temporada 4 y se casa el 20 de febrero de la 5.
        normales(v, "2027-01-01", "2030-06-15").forEach { assertFalse("$it: la manta antes del cautivo", "manta de guerrero" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2031-02-19").forEach { assertFalse("$it: Mazatl casado antes de tiempo", "Mazatl se casó" in todoElDia(v, it)) }
        noNombran(v, listOf("Xóchitl"))
    }
}
