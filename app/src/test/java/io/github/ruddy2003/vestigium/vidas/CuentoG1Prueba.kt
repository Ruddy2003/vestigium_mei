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
 * tiempo y que los estados se noten. Tanda T1a: Naa, Ata e Ipy.
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

    @Test fun estanLasPiezasDeLaTandaT1a() {
        listOf("paleolitico", "neolitico", "egipto").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
        }
        assertEquals(Genero.FEMENINO, pieza("paleolitico").genero)
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
}
