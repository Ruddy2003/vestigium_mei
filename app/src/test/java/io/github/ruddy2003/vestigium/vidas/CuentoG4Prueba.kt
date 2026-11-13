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
 * Los cuentos del grupo 4. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T4a: Odo, Bernat y Zaynab. Tanda T4b: Bat e Iacopo.
 */
class CuentoG4Prueba {
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

    /** Un estado largo no repite el mismo cuento: en sus días salen al menos [minimo] mañanas y trabajos distintos. */
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

    @Test fun estanLasPiezasDeLaTandaT4a() {
        listOf("copista", "sargento", "cordobesa").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.MASCULINO, pieza("copista").genero); assertEquals(Genero.MASCULINO, pieza("sargento").genero)
        assertEquals(Genero.FEMENINO, pieza("cordobesa").genero)
        // Odo y Bernat se llaman «Hermano» en su ficha: el cuento no lo pone con mayúscula en medio de una frase.
        listOf("copista", "sargento").forEach { v -> listOf("2027-03-03", "2027-09-09").forEach { d -> assertFalse("$v, $d", "Hermano" in todoElDia(v, d)) } }
    }

    // ---------- Odo ----------

    @Test fun enElCuentoDeOdoNadieApareceAntesNiDespuesDeTiempo() {
        val v = "copista"
        // Heinrich llega el 20 de enero de la temporada 2.
        normales(v, "2027-01-01", "2028-01-19").forEach { assertFalse("$it: Heinrich antes de tiempo", nombra(v, it, "Heinrich")) }
        assertTrue(normales(v, "2028-01-25", "2028-12-31").any { nombra(v, it, "Heinrich") })
        // Gerhard es el bibliotecario hasta que enferma (25 de noviembre de la 2) y está en la enfermería hasta que muere (18 de enero de la 3).
        nunca(v, "2028-11-25", "2031-12-31", "Gerhard es el bibliotecario")
        nunca(v, "2027-01-01", "2028-11-24", "Gerhard está en la enfermería"); nunca(v, "2029-01-18", "2031-12-31", "Gerhard está en la enfermería")
        // Gertrudis se va con Hildegarda el 20 de mayo de la temporada 1; la piedra de leer llega el 20 de octubre de la 4.
        nunca(v, "2027-01-01", "2027-05-19", "Gertrudis")
        alguna(v, "2027-06-01", "2027-12-31", "Gertrudis")
        nunca(v, "2027-01-01", "2030-10-19", "piedra de leer")
        noNombran(v, listOf("Heinrich", "Gerhard", "Gertrudis"))
    }

    @Test fun odoPasaSeisSemanasEnLorsch() {
        val v = "copista"
        vivenElEstado(v, "lorsch", "2028-06-06", "2028-07-07", listOf(1.0, 2.5, 4.5, 6.0, 9.0, 11.5, 12.5, 14.0, 17.0, 19.0))
        varianEnElEstado(v, "2028-06-06", "2028-07-07", 9.0, 3)
    }

    // ---------- Bernat ----------

    @Test fun enElCuentoDeBernatNadieApareceAntesNiDespuesDeTiempo() {
        val v = "sargento"
        // Ramon está en Egipto hasta que llega la noticia de Mansura (26 de febrero de la 1); después se le recuerda muerto.
        nunca(v, "2027-02-26", "2031-12-31", "está en Egipto con el ejército")
        nunca(v, "2027-01-01", "2027-03-02", "murió en Mansura")
        alguna(v, "2027-03-03", "2027-12-31", "murió en Mansura")
        // Pere llega el 12 de marzo de la 2 y muere el 28 de agosto de la 4.
        normales(v, "2027-01-01", "2028-03-11").forEach { assertFalse("$it: Pere antes de tiempo", nombra(v, it, "Pere")) }
        nunca(v, "2030-08-28", "2031-12-31", "Pere es el hermano joven")
        nunca(v, "2027-01-01", "2030-08-27", "Pere está enterrado")
        // El Moro cojea desde el 5 de octubre de la 2; el comendador nuevo llega el 20 de enero de la 5.
        nunca(v, "2027-01-01", "2028-10-04", "El Moro cojea")
        nunca(v, "2027-01-01", "2031-01-19", "comendador nuevo"); nunca(v, "2031-01-20", "2031-12-31", "caballero de Borgoña")
        noNombran(v, listOf("Ramon", "Pere"))
    }

    @Test fun bernatPasaSeisSemanasHeridoYUnMesEnAcre() {
        val v = "sargento"
        val horas = listOf(0.5, 3.0, 5.75, 9.0, 11.5, 14.0, 17.5, 18.5, 19.5, 22.0)
        vivenElEstado(v, "herido", "2028-06-13", "2028-08-01", horas); varianEnElEstado(v, "2028-06-13", "2028-08-01", 9.0, 3)
        vivenElEstado(v, "acre", "2029-09-13", "2029-10-14", horas); varianEnElEstado(v, "2029-09-13", "2029-10-14", 9.0, 3)
    }

    // ---------- Zaynab ----------

    @Test fun enElCuentoDeZaynabNadieApareceAntesNiDespuesDeTiempo() {
        val v = "cordobesa"
        // Espera desde el 5 de septiembre de la 2; Maryam nace el 2 de mayo de la 3; Rabia llega el 15 de febrero de la 4.
        nunca(v, "2027-01-01", "2028-09-04", "espera un hijo")
        normales(v, "2027-01-01", "2029-05-01").forEach { assertFalse("$it: Maryam antes de nacer", nombra(v, it, "Maryam")) }
        alguna(v, "2029-05-10", "2029-12-31", "Maryam")
        normales(v, "2027-01-01", "2030-02-14").forEach { assertFalse("$it: Rabia antes de tiempo", nombra(v, it, "Rabia")) }
        // Alí va a la escuela desde el 15 de marzo de la 1 hasta que entra de aprendiz (22 de abril de la 5); Juliana se casa el 12 de mayo de la 1.
        nunca(v, "2027-01-01", "2027-03-14", "va a la escuela de la mezquita"); nunca(v, "2031-04-22", "2031-12-31", "va a la escuela de la mezquita")
        nunca(v, "2027-01-01", "2031-04-21", "ya trabaja en la tienda de su padre")
        nunca(v, "2027-01-01", "2027-05-11", "ya está casada")
        noNombran(v, listOf("Maryam", "Rabia", "escuela"))
    }

    @Test fun zaynabAyunaCadaRamadan() {
        val v = "cordobesa"
        // Del día siguiente al primero del ayuno (que es especial) a la víspera de la fiesta.
        listOf("2027-07-09" to "2027-08-06", "2028-06-29" to "2028-07-27", "2029-06-17" to "2029-07-15", "2030-06-06" to "2030-07-04", "2031-05-27" to "2031-06-24").forEach { (a, b) ->
            vivenElEstado(v, "ramadan", a, b, listOf(1.0, 5.25, 6.0, 9.0, 13.0, 15.0, 18.0, 20.0))
            varianEnElEstado(v, a, b, 9.0, 3)
        }
    }

    // ---------- Tanda T4b ----------

    @Test fun estanLasPiezasDeLaTandaT4b() {
        listOf("pastor_mongol", "aprendiz").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(Genero.MASCULINO, pieza(v).genero)
        }
    }

    // ---------- Bat ----------

    @Test fun enElCuentoDeBatNadieApareceAntesNiDespuesDeTiempo() {
        val v = "pastor_mongol"
        // Khulan espera desde el 12 de agosto de la 1 y Saran nace el 12 de marzo de la 2; el potro blanco nace el 2 de mayo de la 1.
        nunca(v, "2027-01-01", "2027-08-11", "Khulan espera")
        normales(v, "2027-01-01", "2028-03-11").forEach { assertFalse("$it: Saran antes de nacer", nombra(v, it, "Saran")) }
        alguna(v, "2028-03-20", "2028-12-31", "Saran")
        nunca(v, "2027-01-01", "2027-05-01", "potro blanco")
        // Su madre enferma el 30 de enero de la 3 y muere el 8 de marzo de la 4.
        nunca(v, "2029-01-30", "2031-12-31", "manda en todo el campamento")
        nunca(v, "2027-01-01", "2029-01-29", "está enferma desde el invierno"); nunca(v, "2030-03-08", "2031-12-31", "está enferma desde el invierno")
        nunca(v, "2027-01-01", "2030-03-07", "ya no está")
        // Temür vuelve de la guerra el 28 de julio de la 5; el invierno del hielo empieza el 15 de febrero de la 4.
        nunca(v, "2031-07-28", "2031-12-31", "sigue en la guerra del oeste")
        nunca(v, "2027-01-01", "2031-07-27", "volvió de la guerra después de cinco años")
        nunca(v, "2027-01-01", "2030-02-14", "invierno del hielo")
        noNombran(v, listOf("Saran", "Temür", "potro blanco"))
    }

    @Test fun batPasaCadaVeranoEnElLlano() {
        val v = "pastor_mongol"
        (2027..2031).forEach { y ->
            vivenElEstado(v, "verano", "$y-05-26", "$y-09-24", listOf(1.0, 5.5, 9.0, 12.5, 15.0, 20.0))
            varianEnElEstado(v, "$y-05-26", "$y-09-24", 9.0, 3)
        }
    }

    // ---------- Iacopo ----------

    @Test fun enElCuentoDeIacopoNadieApareceAntesNiDespuesDeTiempo() {
        val v = "aprendiz"
        // Conoce a Caterina el 10 de julio de la 1 y se promete el 14 de julio de la 4.
        normales(v, "2027-01-01", "2027-07-09").forEach { assertFalse("$it: Caterina antes de tiempo", nombra(v, it, "Caterina")) }
        alguna(v, "2027-07-15", "2027-12-31", "Caterina")
        nunca(v, "2027-01-01", "2030-07-13", "está prometida")
        // El maestro Domenico muere el 11 de enero de la 5; Nanna se casa el 8 de octubre de la 3.
        nunca(v, "2031-01-11", "2031-12-31", "El maestro Domenico es un hombre")
        nunca(v, "2027-01-01", "2031-01-10", "Desde que murió el maestro Domenico")
        nunca(v, "2027-01-01", "2029-10-07", "está casada con un tejedor"); nunca(v, "2029-10-08", "2031-12-31", "Su padre le busca marido")
        // Su primera tabla, del 10 de marzo de la 4 al 25 de mayo de la 5; Lorenzo muere el 9 de abril de la 3.
        nunca(v, "2027-01-01", "2030-03-09", "Desde que empezó su primera tabla"); nunca(v, "2031-05-25", "2031-12-31", "Desde que empezó su primera tabla")
        nunca(v, "2029-04-09", "2031-12-31", "jardín de los Médici")
        noNombran(v, listOf("Caterina", "Domenico"))
    }

    @Test fun iacopoPintaEnElMugelloYNoTrabajaEnLasFiestasDeGuardar() {
        val v = "aprendiz"
        vivenElEstado(v, "campo", "2029-06-29", "2029-08-07", listOf(1.0, 5.5, 9.0, 12.5, 15.0, 20.0))
        varianEnElEstado(v, "2029-06-29", "2029-08-07", 9.0, 3)
        // Pascua y San Juan de los cinco años: el taller está cerrado.
        (listOf("2027-04-11", "2028-04-03", "2029-04-22", "2030-04-07", "2031-03-30") + (2027..2031).map { "$it-06-24" }).forEach { d ->
            assertTrue("$d: trabaja en una fiesta de guardar", "no trabaja" in cuento(v, d, 9.0).parrafos[Parrafo.TRABAJO.ordinal])
        }
    }
}
