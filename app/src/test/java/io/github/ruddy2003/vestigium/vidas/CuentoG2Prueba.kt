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
 * Los cuentos del grupo 2. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T2a: Kichizō, Mary y José. Tanda T2b: Rosa, Alguien del mundo y Alguien de
 * México.
 */
class CuentoG2Prueba {
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

    @Test fun estanLasPiezasDeLaTandaT2a() {
        listOf("edo", "manchester", "hacienda").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.FEMENINO, pieza("manchester").genero)
        assertEquals(Genero.MASCULINO, pieza("edo").genero); assertEquals(Genero.MASCULINO, pieza("hacienda").genero)
    }

    // ---------- Kichizō ----------

    @Test fun enElCuentoDeKichizoNadieApareceAntesDeTiempo() {
        val v = "edo"
        // Okiku nace el 8 de mayo de la temporada 2; Shinsuke entra de aprendiz el 2 de julio de la 4.
        normales(v, "2027-01-01", "2028-05-07").forEach { assertFalse("$it: Okiku antes de nacer", "Okiku" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-06-01", "2028-12-31").any { "Okiku" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2030-07-01").forEach { assertFalse("$it: Shinsuke antes de tiempo", "Shinsuke" in todoElDia(v, it)) }
        // Ume espera desde el 8 de octubre de la temporada 1.
        normales(v, "2027-01-01", "2027-10-07").forEach { assertFalse("$it: Ume espera antes de tiempo", "Ume espera" in todoElDia(v, it)) }
        noNombran(v, listOf("Okiku", "Shinsuke"))
    }

    @Test fun kichizoViveSusTresEstados() {
        val v = "edo"
        // La aldea (temporada 3): amanece y trabaja allá a cualquier hora.
        vivenElEstado(v, "aldea", "2029-08-28", "2029-09-18", listOf(1.0, 5.75, 7.0, 10.0, 12.5, 16.5, 19.0))
        // La casa quemada (temporada 4): amanece y duerme en el templo.
        vivenElEstado(v, "sin_casa", "2030-03-15", "2030-05-24", listOf(1.0, 12.5, 19.0))
        // Ōyama (temporada 5): la peregrinación entera.
        vivenElEstado(v, "oyama", "2031-08-16", "2031-08-18", listOf(1.0, 5.75, 7.0, 10.0, 12.5, 16.5, 19.0))
        // Fuera de los estados no sale nada de ellos.
        val deFuera = listOf("aldea", "sin_casa", "oyama").flatMap { e -> Parrafo.entries.flatMap { textosDe(v, it, e) } }.toSet()
        normales(v, "2027-01-01", "2027-12-31").forEach { dia -> assertTrue("$dia", cuento(v, dia, 9.0).parrafos.none { it in deFuera }) }
    }

    // ---------- Mary ----------

    @Test fun enElCuentoDeMaryNadieApareceAntesNiDespuesDeTiempo() {
        val v = "manchester"
        // Jem aparece el 25 de abril de la temporada 1.
        normales(v, "2027-01-01", "2027-04-24").forEach { assertFalse("$it: Jem antes de conocerlo", palabra("Jem").containsMatchIn(todoElDia(v, it))) }
        assertTrue(normales(v, "2027-05-01", "2027-12-31").any { palabra("Jem").containsMatchIn(todoElDia(v, it)) })
        // Vive con sus padres hasta el 12 de noviembre de la temporada 3.
        normales(v, "2029-11-12", "2031-12-31").forEach { assertFalse("$it: sigue en casa de sus padres", "comparte con Jane" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2029-11-11").forEach { assertFalse("$it: en el cuarto nuevo antes de tiempo", "cuarto nuevo" in todoElDia(v, it)) }
        // Tom nace el 20 de octubre de la temporada 4; Bridget se va el 30 de julio de la 5.
        normales(v, "2027-01-01", "2030-10-19").forEach { assertFalse("$it: Tom antes de nacer", palabra("Tom").containsMatchIn(todoElDia(v, it))) }
        assertTrue(normales(v, "2030-11-20", "2030-12-31").any { palabra("Tom").containsMatchIn(todoElDia(v, it)) })
        normales(v, "2031-07-30", "2031-12-31").forEach { assertFalse("$it: Bridget después de irse", "Bridget" in todoElDia(v, it)) }
        // Su padre teje hasta el 15 de octubre de la temporada 1.
        normales(v, "2027-10-15", "2031-12-31").forEach { assertFalse("$it: su padre sigue tejiendo", "teje en casa" in todoElDia(v, it)) }
        noNombran(v, listOf("Jem", "Tom", "Bridget"))
    }

    @Test fun maryPasaLaHuelgaSinFabricaYLasSemanasDeTomEnCasa() {
        val v = "manchester"
        vivenElEstado(v, "paro", "2029-08-14", "2029-09-25", listOf(5.25, 7.0, 8.25, 10.0, 15.0))
        vivenElEstado(v, "en_casa", "2030-10-21", "2030-11-16", listOf(5.25, 7.0, 8.25, 10.0, 15.0))
    }

    // ---------- José ----------

    @Test fun enElCuentoDeJoseNadieApareceAntesNiDespuesDeTiempo() {
        val v = "hacienda"
        // Rafael nace el 25 de mayo de la temporada 2; Cuca, el 1 de diciembre de la 4.
        normales(v, "2027-01-01", "2028-05-24").forEach { assertFalse("$it: Rafael antes de nacer", "Rafael" in todoElDia(v, it)) }
        assertTrue(normales(v, "2028-06-25", "2028-12-31").any { "Rafael" in todoElDia(v, it) })
        normales(v, "2027-01-01", "2030-11-30").forEach { assertFalse("$it: Cuca antes de nacer", "Cuca" in todoElDia(v, it)) }
        // Doña Refugio muere el 20 de enero de la temporada 4; Nicolás huye el 25 de marzo de la 5.
        normales(v, "2030-01-20", "2031-12-31").forEach { assertFalse("$it: doña Refugio después de morir", "oña Refugio" in todoElDia(v, it)) }
        assertTrue(normales(v, "2027-01-01", "2027-12-31").any { "oña Refugio" in todoElDia(v, it) })
        normales(v, "2031-03-25", "2031-12-31").forEach { assertFalse("$it: Nicolás después de irse", "Nicolás" in todoElDia(v, it)) }
        noNombran(v, listOf("Rafael", "Cuca", "Refugio", "Nicolás"))
    }

    @Test fun joseNoRaspaConLaManoHerida() {
        vivenElEstado("hacienda", "herido", "2029-07-03", "2029-07-15", listOf(7.0, 10.0, 12.5, 15.0))
        // Y fuera de esas dos semanas el trabajo es el de siempre.
        val herido = textosDe("hacienda", Parrafo.TRABAJO, "herido")
        normales("hacienda", "2029-07-16", "2029-08-31").forEach { assertFalse(it, cuento("hacienda", it, 10.0).parrafos[Parrafo.TRABAJO.ordinal] in herido) }
    }

    // ---------- T2b ----------

    @Test fun estanLasPiezasDeLaTandaT2bYLosAlguienNoTienenNombre() {
        listOf("cdmx1960", "mundo2025", "mexico2025").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.FEMENINO, pieza("cdmx1960").genero)
        // Una mujer y un hombre, y ninguno se llama «Alguien»: el cuento no usa su nombre.
        assertEquals(Genero.FEMENINO, pieza("mundo2025").genero); assertEquals(Genero.MASCULINO, pieza("mexico2025").genero)
        listOf("mundo2025", "mexico2025").forEach { v ->
            assertTrue("$v usa {nombre}", pieza(v).textos().none { "{nombre}" in it })
            dias("2027-01-01", "2027-12-31").forEach { d -> assertFalse("$v, $d", quien(v).nombre in cuento(v, d).parrafos.joinToString(" ")) }
        }
    }

    @Test fun enElCuentoDeRosaNadieApareceAntesDeTiempo() {
        val v = "cdmx1960"
        // Se casa el 17 de junio de la temporada 2 y se muda el 24; Claudia nace el 2 de junio de la 3.
        normales(v, "2027-01-01", "2028-06-16").forEach { assertFalse("$it: casada antes de la boda", "Arturo, su marido" in todoElDia(v, it)) }
        normales(v, "2028-06-17", "2031-12-31").forEach { assertFalse("$it: novios después de la boda", "Arturo, su novio" in todoElDia(v, it)) }
        normales(v, "2028-06-24", "2031-12-31").forEach { assertFalse("$it: sigue en la vecindad", "Doña Carmen le da a Rosa un taco" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2029-06-01").forEach { assertFalse("$it: Claudia antes de nacer", "Claudia" in todoElDia(v, it)) }
        noNombran(v, listOf("Arturo", "Claudia"))
        vivenElEstado(v, "acapulco", "2028-06-19", "2028-06-23", listOf(1.0, 6.5, 7.5, 10.0, 15.0, 20.0))
        vivenElEstado(v, "incapacidad", "2029-06-03", "2029-07-27", listOf(7.5, 10.0, 15.0))
    }

    @Test fun enElCuentoDeAlguienDelMundoNadieApareceAntesDeTiempo() {
        val v = "mundo2025"
        // Kabir nace el 28 de mayo de la temporada 3; se mudan el 25 de noviembre de la 3.
        normales(v, "2027-01-01", "2029-05-27").forEach { assertFalse("$it: Kabir antes de nacer", "Kabir" in todoElDia(v, it)) }
        normales(v, "2029-11-25", "2031-12-31").forEach { assertFalse("$it: sigue en la azotea", "Sube a la azotea a tender" in todoElDia(v, it)) }
        noNombran(v, listOf("Kabir"))
        vivenElEstado(v, "aldea", "2028-07-21", "2028-08-03", listOf(1.0, 7.0, 8.5, 10.0, 13.5, 21.0))
    }

    @Test fun enElCuentoDeAlguienDeMexicoNadieApareceAntesDeTiempo() {
        val v = "mexico2025"
        // Valeria nace el 15 de julio de la temporada 3; se mudan el 1 de marzo de la 3 y el 24 de febrero de la 5.
        normales(v, "2027-01-01", "2029-07-14").forEach { assertFalse("$it: Valeria antes de nacer", "Valeria" in todoElDia(v, it)) }
        normales(v, "2029-03-01", "2031-12-31").forEach { assertFalse("$it: en el departamento viejo", "El departamento es chico" in todoElDia(v, it)) }
        normales(v, "2027-01-01", "2031-02-23").forEach { assertFalse("$it: en el fraccionamiento antes de tiempo", "casa del fraccionamiento todavía" in todoElDia(v, it)) }
        noNombran(v, listOf("Valeria"))
        vivenElEstado(v, "playa", "2028-07-21", "2028-07-26", listOf(1.0, 6.5, 7.5, 10.0, 14.5, 21.0))
    }
}
