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
 * Los cuentos del grupo 5. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T5a: Tomás, Ochiyo y Marie-Jeanne.
 */
class CuentoG5Prueba {
    private fun pieza(vida: String) = TodasLasPiezas.de(vida)!!
    private fun cal(vida: String) = Calendarios.de(vida)!!
    private fun quien(vida: String) = OtrasVidas.porId(vida)!!
    private fun cuento(vida: String, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(vida), cal(vida)), quien(vida), cal(vida), pieza(vida), hora)!!
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a siete horas distintas (cada una con su párrafo del trabajo). */
    private fun todoElDia(vida: String, dia: String) = listOf(1.0, 5.25, 9.0, 12.5, 16.5, 19.5, 21.0).flatMap { cuento(vida, dia, it).parrafos }.joinToString(" ")
    /** Los días que no son especiales: en ellos solo salen las variantes de todos los días. */
    private fun normales(vida: String, desde: String, hasta: String) = dias(desde, hasta).filter { !cuento(vida, it).especial }
    private fun textosDe(vida: String, lugar: Parrafo, estado: String) =
        pieza(vida).variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to quien(vida).nombre), pieza(vida).genero) }.toSet()
    private fun palabra(p: String) = Regex("""\b$p\b""")
    private fun nombra(vida: String, dia: String, quien: String) = palabra(quien).containsMatchIn(todoElDia(vida, dia))

    /** Los ids que vuelven en más de una temporada: sus textos valen los cinco años. */
    private fun deCadaAnio(v: String) = cal(v).temporadas.flatMap { t -> t.dias.map { it.id } }.groupingBy { it }.eachCount().filterValues { it >= 2 }.keys
    private fun noNombran(v: String, nombres: List<String>) = deCadaAnio(v).forEach { id -> pieza(v).especiales[id]?.let { t ->
        (t.dias.flatMap { it.values } + t.sigue.values.flatten()).forEach { s -> nombres.forEach { assertFalse("$v, «$id» nombra a $it: «${s.take(50)}…»", palabra(it).containsMatchIn(s)) } }
    } }

    /** En los días del estado [estado], la mañana y el trabajo a las [horas] salen de sus variantes, no de las de casa. */
    private fun vivenElEstado(v: String, estado: String, desde: String, hasta: String, horas: List<Double>) {
        val manana = textosDe(v, Parrafo.MANANA, estado); val trabajo = textosDe(v, Parrafo.TRABAJO, estado)
        dias(desde, hasta).filter { !cuento(v, it).especial }.forEach { dia ->
            assertEquals("$v, $dia", estado, cuento(v, dia).ficha.estado)
            assertTrue("$v, $dia: amanece en casa", cuento(v, dia, 9.0).parrafos[Parrafo.MANANA.ordinal] in manana)
            horas.forEach { h -> assertTrue("$v, $dia, $h h: el trabajo es el de siempre", cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal] in trabajo) }
        }
    }

    /** Un estado largo no repite el mismo cuento: en sus días salen al menos [minimo] mañanas, trabajos y anocheceres distintos. */
    private fun varianEnElEstado(v: String, desde: String, hasta: String, hora: Double, minimo: Int) {
        val normales = normales(v, desde, hasta)
        assertTrue("$v: pocas mañanas distintas", normales.map { cuento(v, it, hora).parrafos[Parrafo.MANANA.ordinal] }.toSet().size >= minimo)
        assertTrue("$v: pocos trabajos distintos a las $hora", normales.map { cuento(v, it, hora).parrafos[Parrafo.TRABAJO.ordinal] }.toSet().size >= minimo)
        assertTrue("$v: pocos anocheceres distintos", normales.map { cuento(v, it, hora).parrafos[Parrafo.ANOCHECER.ordinal] }.toSet().size >= minimo)
    }

    /** Los días de [desde] a [hasta] en los que no sale [texto] en todo el día. */
    private fun nunca(v: String, desde: String, hasta: String, texto: String) =
        normales(v, desde, hasta).forEach { assertFalse("$v, $it: «$texto» fuera de tiempo", texto in todoElDia(v, it)) }
    private fun alguna(v: String, desde: String, hasta: String, texto: String) =
        assertTrue("$v: «$texto» no sale entre $desde y $hasta", normales(v, desde, hasta).any { texto in todoElDia(v, it) })
    /** El párrafo del trabajo de ese día dice que no trabaja, a las 9 y a las 15. */
    private fun descansa(v: String, dia: String) = listOf(9.0, 15.0).forEach { h ->
        assertTrue("$v, $dia, $h h: trabaja en su día de fiesta", "no trabaja" in cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal])
    }

    @Test fun estanLasPiezasDeLaTandaT5a() {
        listOf("grumete", "posadera", "lavandera").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.MASCULINO, pieza("grumete").genero)
        assertEquals(Genero.FEMENINO, pieza("posadera").genero); assertEquals(Genero.FEMENINO, pieza("lavandera").genero)
    }

    // ---------- Tomás ----------

    @Test fun enElCuentoDeTomasNadieApareceAntesNiDespuesDeTiempo() {
        val v = "grumete"
        // Kofi cae con la fiebre el 2 de julio de la 3 y muere el 9.
        nunca(v, "2029-07-02", "2031-12-31", "Kofi es un marinero de la Costa de Oro")
        nunca(v, "2027-01-01", "2029-07-08", "Kofi ya no está")
        alguna(v, "2029-07-10", "2029-12-31", "Kofi ya no está")
        // La tripulación cambia de capitán el 25 de noviembre de la 2.
        nunca(v, "2028-11-25", "2031-12-31", "El capitán Ned Grady")
        nunca(v, "2027-01-01", "2028-11-24", "El capitán Hollis")
        alguna(v, "2028-11-26", "2029-06-30", "El capitán Hollis")
        noNombran(v, listOf("Kofi", "Grady", "Hollis"))
    }

    @Test fun tomasCarenaCadaAnioYPasaUnMesEnNassau() {
        val v = "grumete"
        val horas = listOf(1.0, 5.0, 8.5, 10.0, 12.5, 14.0, 17.0, 21.0)
        (2027..2031).forEach { y -> vivenElEstado(v, "cayo", "$y-03-11", "$y-03-23", horas); varianEnElEstado(v, "$y-03-11", "$y-03-23", 10.0, 3) }
        vivenElEstado(v, "nassau", "2027-06-29", "2027-07-23", horas); varianEnElEstado(v, "2027-06-29", "2027-07-23", 10.0, 3)
    }

    // ---------- Ochiyo ----------

    @Test fun enElCuentoDeOchiyoNadieApareceAntesNiDespuesDeTiempo() {
        val v = "posadera"
        // Osen llega el 5 de marzo de la 1 y se casa el 8 de septiembre de la 4.
        normales(v, "2027-01-01", "2027-03-04").forEach { assertFalse("$it: Osen antes de tiempo", nombra(v, it, "Osen")) }
        alguna(v, "2027-03-10", "2027-12-31", "Osen")
        nunca(v, "2027-01-01", "2030-09-07", "Osen se casó con el hijo del herrero")
        // Conoce a Sakichi el 12 de octubre de la 1; se prometen el 22 de mayo de la 4 y se casan el 8 de noviembre de la 5.
        normales(v, "2027-01-01", "2027-10-11").forEach { assertFalse("$it: Sakichi antes de tiempo", nombra(v, it, "Sakichi")) }
        alguna(v, "2027-10-13", "2027-12-31", "Sakichi")
        nunca(v, "2027-01-01", "2030-05-21", "están prometidos"); nunca(v, "2031-11-08", "2031-12-31", "están prometidos")
        nunca(v, "2027-01-01", "2031-11-07", "Sakichi, su marido")
        noNombran(v, listOf("Osen", "Sakichi"))
    }

    @Test fun ochiyoVeLosPeregrinosDeIseYDescansaSusDosDiasLibres() {
        val v = "posadera"
        vivenElEstado(v, "okage", "2027-05-02", "2027-08-11", listOf(1.0, 6.0, 7.5, 10.0, 13.5, 16.0, 21.5))
        varianEnElEstado(v, "2027-05-02", "2027-08-11", 10.0, 3)
        // Sus dos días libres de cada año, en el campo con su madre.
        cal(v).temporadas.forEachIndexed { i, t -> listOf("yabuiri", "yabuiri7").forEach { id -> descansa(v, "${2027 + i}-${t.dias.first { it.id == id }.fecha}") } }
    }

    // ---------- Marie-Jeanne ----------

    @Test fun enElCuentoDeMarieJeanneNadieApareceAntesNiDespuesDeTiempo() {
        val v = "lavandera"
        // Jacques entra en la guardia el 20 de julio de la 1, se va a la frontera el 25 de julio de la 4 y vuelve el 15 de junio de la 5.
        nunca(v, "2027-01-01", "2027-07-19", "Jacques está en la guardia"); nunca(v, "2030-07-25", "2031-12-31", "Jacques está en la guardia")
        nunca(v, "2027-01-01", "2030-07-24", "Jacques está en la frontera"); nunca(v, "2031-06-15", "2031-12-31", "Jacques está en la frontera")
        alguna(v, "2030-08-01", "2031-06-14", "Jacques está en la frontera")
        nunca(v, "2027-01-01", "2031-06-14", "Jacques volvió de la frontera")
        // Antoine llega el 10 de mayo de la 2; Paul trabaja en el barco desde el 16 de noviembre de la 1 y lee desde el 10 de septiembre de la 5.
        normales(v, "2027-01-01", "2028-05-09").forEach { assertFalse("$it: Antoine antes de tiempo", nombra(v, it, "Antoine")) }
        alguna(v, "2028-05-15", "2028-12-31", "Antoine")
        nunca(v, "2027-01-01", "2027-11-15", "Paul la ayuda en el barco"); nunca(v, "2031-09-10", "2031-12-31", "Paul la ayuda en el barco")
        nunca(v, "2027-01-01", "2031-09-09", "Paul sabe leer")
        noNombran(v, listOf("Jacques", "Antoine"))
    }

    @Test fun marieJeannePasaElSenaHeladoYNoLavaEnLasFiestasDeGuardar() {
        val v = "lavandera"
        vivenElEstado(v, "helada", "2027-01-02", "2027-01-19", listOf(1.0, 5.0, 9.0, 12.2, 15.0, 19.0, 20.5))
        varianEnElEstado(v, "2027-01-02", "2027-01-19", 9.0, 3)
        // Pascua de los cinco años; la Asunción, Todos los Santos y Navidad mientras se celebran (hasta 1792).
        (listOf("2027-04-12", "2028-04-04", "2029-04-24", "2030-04-08", "2031-03-31") + (2027..2030).flatMap { listOf("$it-08-15", "$it-11-01", "$it-12-25") })
            .forEach { descansa(v, it) }
    }
}
