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
 * Los cuentos del grupo 3. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T3a: Cayo, Pay y Ama. Tanda T3b: K'an y Nanai-vandak.
 */
class CuentoG3Prueba {
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

    @Test fun estanLasPiezasDeLaTandaT3a() {
        listOf("legionario", "escriba_tumba", "pintora_cueva").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.MASCULINO, pieza("legionario").genero); assertEquals(Genero.MASCULINO, pieza("escriba_tumba").genero)
        assertEquals(Genero.FEMENINO, pieza("pintora_cueva").genero)
    }

    // ---------- Cayo ----------

    @Test fun enElCuentoDeCayoNadieApareceAntesDeTiempo() {
        val v = "legionario"
        // Conoce a Sabina el 20 de enero de la temporada 1; Durio llega de recluta el 30 de marzo de la 2.
        normales(v, "2027-01-01", "2027-01-19").forEach { assertFalse("$it: Sabina antes de tiempo", nombra(v, it, "Sabina")) }
        assertTrue(normales(v, "2027-02-01", "2027-12-31").any { nombra(v, it, "Sabina") })
        normales(v, "2027-01-01", "2028-03-29").forEach { assertFalse("$it: Durio antes de tiempo", nombra(v, it, "Durio")) }
        // Sabina espera desde el 5 de marzo de la temporada 3 y Gaia nace el 2 de noviembre.
        normales(v, "2027-01-01", "2029-03-04").forEach { assertFalse("$it: Sabina espera antes de tiempo", "Sabina espera" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2029-11-01").forEach { assertFalse("$it: Gaia antes de nacer", nombra(v, it, "Gaia")) }
        assertTrue(normales(v, "2029-11-10", "2029-12-31").any { nombra(v, it, "Gaia") })
        // Atilio se va con la vejilación el 24 de septiembre de la temporada 4.
        assertTrue(normales(v, "2027-01-01", "2030-09-23").any { nombra(v, it, "Atilio") })
        noNombran(v, listOf("Sabina", "Gaia", "Durio"))
    }

    @Test fun cayoViveSusSeisSemanasEnElCamino() {
        val v = "legionario"
        vivenElEstado(v, "camino", "2028-06-02", "2028-07-14", listOf(1.0, 4.75, 9.0, 12.5, 16.5, 20.0))
        varianEnElEstado(v, "2028-06-02", "2028-07-14", 9.0, 3)
        // Fuera del camino no sale nada de él.
        val camino = textosDe(v, Parrafo.MANANA, "camino")
        normales(v, "2028-05-01", "2028-05-31").forEach { assertFalse(it, cuento(v, it, 9.0).parrafos[Parrafo.MANANA.ordinal] in camino) }
    }

    // ---------- Pay ----------

    @Test fun enElCuentoDePayNadieApareceAntesNiDespuesDeTiempo() {
        val v = "escriba_tumba"
        // Meryt nace el 5 de mayo de la temporada 2; Tamit la espera desde el 10 de septiembre de la 1.
        normales(v, "2027-01-01", "2028-05-04").forEach { assertFalse("$it: Meryt antes de nacer", nombra(v, it, "Meryt")) }
        assertTrue(normales(v, "2028-05-10", "2028-12-31").any { nombra(v, it, "Meryt") })
        // Tiy, su madre, muere el 5 de febrero de la temporada 3; Paneb deja de ser capataz el 25 de junio.
        assertTrue(normales(v, "2027-01-01", "2029-02-04").any { nombra(v, it, "Tiy") })
        normales(v, "2029-02-05", "2031-12-31").forEach { assertFalse("$it: Tiy después de morir", nombra(v, it, "Tiy")) }
        // De la denuncia se habla todo ese año (su «mundo»), pero ya no sale como su capataz, y desde la temporada 4 no se le nombra.
        normales(v, "2029-06-25", "2029-12-31").forEach { assertFalse("$it: Paneb sigue de capataz", palabra("Paneb").containsMatchIn(cuento(v, it).parrafos[Parrafo.GENTE.ordinal])) }
        normales(v, "2030-01-01", "2031-12-31").forEach { assertFalse("$it: Paneb después de irse", nombra(v, it, "Paneb")) }
        noNombran(v, listOf("Meryt", "Tiy", "Paneb"))
    }

    @Test fun payDuermeEnElPasoMientrasAcabanLaTumbaDelRey() {
        val v = "escriba_tumba"
        vivenElEstado(v, "valle", "2030-03-03", "2030-05-10", listOf(1.0, 5.5, 6.25, 9.0, 11.5, 14.0, 16.5, 17.5, 19.0))
        varianEnElEstado(v, "2030-03-03", "2030-05-10", 9.0, 3)
    }

    // ---------- Ama ----------

    @Test fun enElCuentoDeAmaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "pintora_cueva"
        // Lin nace el 20 de abril de la temporada 2; Ama la espera desde el 15 de agosto de la 1.
        normales(v, "2027-01-01", "2028-04-19").forEach { assertFalse("$it: Lin antes de nacer", nombra(v, it, "Lin")) }
        assertTrue(normales(v, "2028-04-25", "2028-12-31").any { nombra(v, it, "Lin") })
        normales(v, "2027-01-01", "2027-08-14").forEach { assertFalse("$it: espera antes de tiempo", "espera un hijo" in todoElDia(v, it)) }
        // Yemi, su madre, muere el 10 de febrero de la temporada 3; a Sel la conoce el 3 de julio.
        assertTrue(normales(v, "2027-01-01", "2029-02-09").any { nombra(v, it, "Yemi") })
        normales(v, "2029-02-10", "2031-12-31").forEach { assertFalse("$it: Yemi después de morir", nombra(v, it, "Yemi")) }
        normales(v, "2027-01-01", "2029-07-02").forEach { assertFalse("$it: Sel antes de tiempo", nombra(v, it, "Sel")) }
        noNombran(v, listOf("Lin", "Yemi", "Sel"))
    }

    @Test fun amaPasaCadaVeranoEnLaMesetaYNoCuentaLosAnios() {
        val v = "pintora_cueva"
        (2027..2031).forEach { y ->
            vivenElEstado(v, "verano", "$y-06-02", "$y-09-19", listOf(1.0, 6.5, 9.0, 13.0, 15.0, 17.0, 19.0))
            varianEnElEstado(v, "$y-06-02", "$y-09-19", 9.0, 3)
        }
        // Como Naa, vive antes de la escritura: ni números ni palabras de calendario.
        val todo = pieza(v).let { p -> p.variantes.values.flatten().map { it.texto } + p.especiales.values.flatMap { t -> t.dias.flatMap { it.values } + t.sigue.values.flatten() } +
            p.cielos.values.flatten() + p.estaciones.values.flatten() + p.mundos.values.flatten() + p.objetos.map { it.dato } + p.entornos.flatMap { it.frases } }
        todo.forEach { s ->
            assertTrue("Un número: «${s.take(60)}…»", s.none { it.isDigit() })
            assertFalse("«a. C.» en «${s.take(60)}…»", "a. C." in s)
            listOf("semana", "semanas", "mes", "meses", "enero", "lunes").forEach { w -> assertFalse("«$w» en «${s.take(60)}…»", palabra(w).containsMatchIn(s)) }
        }
    }

    // ---------- Tanda T3b ----------

    @Test fun estanLasPiezasDeLaTandaT3b() {
        listOf("escriba_maya", "mercader_sogdiano").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(Genero.MASCULINO, pieza(v).genero)
        }
    }

    // ---------- K'an ----------

    @Test fun enElCuentoDeKanNadieApareceAntesNiDespuesDeTiempo() {
        val v = "escriba_maya"
        // Ix Sak espera desde el 1 de agosto de la temporada 2 y su hija nace el 20 de marzo de la 3.
        normales(v, "2027-01-01", "2028-07-31").forEach { assertFalse("$it: Ix Sak espera antes de tiempo", "Ix Sak espera" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2029-03-19").forEach { assertFalse("$it: la hija antes de nacer", "Su hija" in todoElDia(v, it)) }
        assertTrue(normales(v, "2029-03-25", "2029-12-31").any { "Su hija" in todoElDia(v, it) })
        // Su maestro, Ah K'in, muere el 15 de julio de la temporada 3.
        assertTrue(normales(v, "2027-01-01", "2029-07-14").any { "Ah K'in" in todoElDia(v, it) })
        normales(v, "2029-07-15", "2031-12-31").forEach { assertFalse("$it: Ah K'in después de morir", "Ah K'in" in todoElDia(v, it)) }
        // La estela se ve en obra mientras se talla, y después se lee en la plaza.
        assertTrue(normales(v, "2027-03-01", "2027-12-31").any { "estela nueva" in todoElDia(v, it) || "estela del k'atun" in todoElDia(v, it) })
        normales(v, "2028-05-04", "2031-12-31").forEach { assertFalse("$it: la estela sigue en obra", "estela nueva" in todoElDia(v, it)) }
        noNombran(v, listOf("Ah K'in", "hija", "maestro"))
    }

    @Test fun kanPasaUnMesEnYaxchilan() {
        val v = "escriba_maya"
        vivenElEstado(v, "embajada", "2029-04-13", "2029-05-14", listOf(1.0, 5.5, 9.0, 12.5, 14.0, 17.0, 18.5, 20.0))
        varianEnElEstado(v, "2029-04-13", "2029-05-14", 9.0, 3)
        val fuera = textosDe(v, Parrafo.MANANA, "embajada")
        normales(v, "2029-05-15", "2029-06-30").forEach { assertFalse(it, cuento(v, it, 9.0).parrafos[Parrafo.MANANA.ordinal] in fuera) }
    }

    // ---------- Nanai-vandak ----------

    @Test fun enElCuentoDeNanaiCadaUnoEstaDondeLeToca() {
        val v = "mercader_sogdiano"
        // Farn se queda en Samarcanda hasta el 28 de septiembre de la temporada 2; luego viaja con su padre; se casa el 10 de
        // julio de la 5.
        normales(v, "2027-01-01", "2028-09-27").forEach { assertFalse("$it: Farn viaja antes de tiempo", "Farn, su hijo, viaja con él" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-10-01", "2029-07-14").any { "Farn, su hijo, viaja con él" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2031-07-09").forEach { assertFalse("$it: Farn casado antes de tiempo", "está casado con la hija de Shi Ping" in todoElDia(v, it)) }
        // Su hija se casa el 10 de julio de la temporada 2; el nieto nace el 5 de agosto de la 4.
        normales(v, "2027-01-01", "2028-07-09").forEach { assertFalse("$it: la hija casada antes de tiempo", "está casada" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2030-08-04").forEach { assertFalse("$it: el nieto antes de nacer", palabra("nieto").containsMatchIn(todoElDia(v, it))) }
        assertTrue(normales(v, "2030-08-10", "2031-12-31").any { palabra("nieto").containsMatchIn(todoElDia(v, it)) })
        noNombran(v, listOf("Farn", "Nanai-dhat", "nieto"))
    }

    @Test fun nanaiPasaElVeranoEnUnaCiudadYElInviernoEnElCamino() {
        val v = "mercader_sogdiano"
        val horas = listOf(1.0, 4.5, 9.0, 13.5, 15.0, 19.0)
        listOf("2027-04-16" to "2027-09-21", "2029-04-26" to "2029-09-27", "2031-04-21" to "2031-09-14").forEach { (a, b) ->
            vivenElEstado(v, "changan", a, b, horas); varianEnElEstado(v, a, b, 9.0, 3)
        }
        listOf("2028-04-26" to "2028-09-27", "2030-04-29" to "2030-09-24").forEach { (a, b) ->
            vivenElEstado(v, "samarcanda", a, b, horas); varianEnElEstado(v, a, b, 9.0, 3)
        }
        // En el camino no amanece en ninguna de las dos ciudades.
        val ciudad = textosDe(v, Parrafo.MANANA, "changan") + textosDe(v, Parrafo.MANANA, "samarcanda")
        (2027..2031).forEach { y -> normales(v, "$y-01-01", "$y-02-28").forEach { assertFalse(it, cuento(v, it, 9.0).parrafos[Parrafo.MANANA.ordinal] in ciudad) } }
    }
}
