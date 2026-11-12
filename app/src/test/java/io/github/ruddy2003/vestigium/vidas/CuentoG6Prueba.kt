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
 * Los cuentos del grupo 6. Las pruebas generales (forma, largo, repeticiones, certeza) están en [CuentoPrueba] y corren
 * solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie aparezca antes ni después de
 * tiempo y que los estados se noten. Tanda T6a: Albert, Nikos y Lou.
 */
class CuentoG6Prueba {
    private fun pieza(vida: String) = TodasLasPiezas.de(vida)!!
    private fun cal(vida: String) = Calendarios.de(vida)!!
    private fun quien(vida: String) = OtrasVidas.porId(vida)!!
    private fun cuento(vida: String, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(vida), cal(vida)), quien(vida), cal(vida), pieza(vida), hora)!!
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a ocho horas distintas (cada una con su párrafo del trabajo). */
    private fun todoElDia(vida: String, dia: String) = listOf(1.0, 4.75, 5.25, 9.0, 12.5, 15.0, 18.5, 21.0).flatMap { cuento(vida, dia, it).parrafos }.joinToString(" ")
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

    /** En los días del estado [estado], la mañana y el trabajo a las [horas] salen de sus variantes, no de las de siempre. */
    private fun vivenElEstado(v: String, estado: String, desde: String, hasta: String, horas: List<Double>) {
        val manana = textosDe(v, Parrafo.MANANA, estado); val trabajo = textosDe(v, Parrafo.TRABAJO, estado)
        dias(desde, hasta).filter { !cuento(v, it).especial }.forEach { dia ->
            assertEquals("$v, $dia", estado, cuento(v, dia).ficha.estado)
            assertTrue("$v, $dia: la mañana es la de siempre", cuento(v, dia, 9.0).parrafos[Parrafo.MANANA.ordinal] in manana)
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
    /** El párrafo del trabajo de ese día dice que no trabaja, a las 8 y a las 14. */
    private fun descansa(v: String, dia: String) = listOf(8.0, 14.0).forEach { h ->
        assertTrue("$v, $dia, $h h: trabaja en su día de fiesta", "no trabaja" in cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal])
    }
    /** La fecha del día [id] en cada temporada en que está. */
    private fun fechas(v: String, id: String) = cal(v).temporadas.mapIndexedNotNull { i, t -> t.dias.firstOrNull { it.id == id }?.let { "${2027 + i}-${it.fecha}" } }

    @Test fun estanLasPiezasDeLaTandaT6a() {
        listOf("fogonero", "buzo_esponjas", "pianista").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
        }
        assertEquals(Genero.MASCULINO, pieza("fogonero").genero); assertEquals(Genero.MASCULINO, pieza("buzo_esponjas").genero)
        assertEquals(Genero.FEMENINO, pieza("pianista").genero)
    }

    // ---------- Albert ----------

    @Test fun enElCuentoDeAlbertNadieApareceAntesNiDespuesDeTiempo() {
        val v = "fogonero"
        // Conoce a Ellen el 22 de mayo de la 1; se prometen el 14 de febrero de la 2 y se casan el 28 de enero de la 3.
        normales(v, "2027-01-01", "2027-05-21").forEach { assertFalse("$it: Ellen antes de tiempo", nombra(v, it, "Ellen")) }
        alguna(v, "2027-05-25", "2027-12-31", "Ellen trabaja en una mercería")
        nunca(v, "2027-01-01", "2028-02-13", "están prometidos"); nunca(v, "2029-01-28", "2031-12-31", "están prometidos")
        nunca(v, "2027-01-01", "2029-01-27", "Ellen, su mujer")
        nunca(v, "2029-01-28", "2031-12-31", "vive todavía con su madre")
        // Tom baja a la mina el 20 de junio de la 2; Annie nace el 2 de mayo de la 4; se mudan el 14 de abril de la 4.
        nunca(v, "2028-06-20", "2031-12-31", "va todavía a la escuela"); nunca(v, "2027-01-01", "2028-06-19", "baja a la mina desde los doce")
        normales(v, "2027-01-01", "2030-05-01").forEach { assertFalse("$it: Annie antes de tiempo", nombra(v, it, "Annie")) }
        nunca(v, "2027-01-01", "2030-04-13", "y ahora Albert")
        // Hebburn conduce hasta que Albert aprueba, el 10 de septiembre de la 5.
        nunca(v, "2031-09-10", "2031-12-31", "Hebburn, el maquinista de su máquina")
        nunca(v, "2027-01-01", "2031-09-09", "Hebburn se jubiló")
        noNombran(v, listOf("Ellen", "Annie", "Hebburn"))
    }

    @Test fun albertPasaUnMesDeBajaYAcabaEnElRegulador() {
        val v = "fogonero"
        vivenElEstado(v, "baja", "2029-06-21", "2029-07-19", listOf(1.0, 4.75, 5.25, 10.0, 17.75, 18.5, 20.0))
        varianEnElEstado(v, "2029-06-21", "2029-07-19", 10.0, 2)
        vivenElEstado(v, "maquinista", "2031-09-11", "2031-12-30", listOf(8.0, 14.0))
        varianEnElEstado(v, "2031-09-11", "2031-12-30", 10.0, 2)
        // El último día del año sigue siendo maquinista.
        assertEquals("maquinista", cuento(v, "2031-12-31").ficha.estado)
    }

    // ---------- Nikos ----------

    @Test fun enElCuentoDeNikosNadieApareceAntesNiDespuesDeTiempo() {
        val v = "buzo_esponjas"
        // Yannis lleva su cuerda desde el 10 de marzo de la 2; antes, Stelios.
        normales(v, "2027-01-01", "2028-03-09").forEach { assertFalse("$it: Yannis antes de tiempo", nombra(v, it, "Yannis")) }
        nunca(v, "2028-03-10", "2031-12-31", "Stelios, su ayudante")
        // Eleni: su madre se la busca el 15 de septiembre de la 3; llega el 28 de noviembre de la 4; se casan el 17 de febrero de la 5.
        normales(v, "2027-01-01", "2029-09-14").forEach { assertFalse("$it: Eleni antes de tiempo", nombra(v, it, "Eleni")) }
        alguna(v, "2029-09-20", "2029-12-31", "hay una muchacha, Eleni")
        nunca(v, "2027-01-01", "2030-11-27", "Eleni ya está en Tarpon Springs"); nunca(v, "2031-02-17", "2031-12-31", "Eleni ya está en Tarpon Springs")
        nunca(v, "2027-01-01", "2031-02-16", "Eleni, su mujer")
        nunca(v, "2027-01-01", "2031-07-19", "Eleni espera un hijo")
        // La casa de madera, desde el 12 de mayo de la 5.
        nunca(v, "2027-01-01", "2031-05-11", "la casa de madera"); alguna(v, "2031-12-20", "2031-12-30", "la casa de madera")
        noNombran(v, listOf("Eleni", "Yannis", "Stelios"))
    }

    @Test fun nikosPasaElInviernoEnPuertoYCincoSemanasEnfermo() {
        val v = "buzo_esponjas"
        val horas = listOf(1.0, 4.75, 8.0, 11.5, 14.0, 17.5, 19.0)
        vivenElEstado(v, "puerto", "2027-01-02", "2027-01-25", horas)
        vivenElEstado(v, "puerto", "2027-12-18", "2028-01-25", horas); varianEnElEstado(v, "2027-12-18", "2028-01-25", 8.0, 2)
        vivenElEstado(v, "enfermo", "2030-06-13", "2030-07-17", horas); varianEnElEstado(v, "2030-06-13", "2030-07-17", 8.0, 2)
        assertEquals(null, cuento(v, "2027-01-26").ficha.estado)
        // Las fiestas de guardar de la Iglesia: ese día no trabaja.
        listOf("navidad_vieja", "san_basilio", "epifania", "pascua", "dormicion", "san_nicolas").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- Lou ----------

    @Test fun enElCuentoDeLouNadieApareceAntesNiDespuesDeTiempo() {
        val v = "pianista"
        // Conoce a Rafe el 12 de febrero de la 2; viven juntos desde el 1 de agosto; se va a Chicago el 12 de agosto de la 4,
        // vuelve el 2 de abril de la 5 y se casan el 14 de septiembre.
        normales(v, "2027-01-01", "2028-02-11").forEach { assertFalse("$it: Rafe antes de tiempo", nombra(v, it, "Rafe")) }
        alguna(v, "2028-02-15", "2028-07-31", "Rafe es un trompetista")
        nunca(v, "2027-01-01", "2028-07-31", "viven juntos en un cuarto más grande"); nunca(v, "2030-08-12", "2031-12-31", "viven juntos en un cuarto más grande")
        nunca(v, "2027-01-01", "2030-08-11", "Rafe está en Chicago"); nunca(v, "2031-04-02", "2031-12-31", "Rafe está en Chicago")
        nunca(v, "2027-01-01", "2031-09-13", "Rafe, su marido")
        // El club de Sully, del 15 de enero de la 1 al 17 de febrero de la 5; el club nuevo, desde el 5 de mayo de la 5.
        nunca(v, "2027-01-01", "2027-01-14", "Sully"); nunca(v, "2031-02-17", "2031-12-31", "Sully es el dueño")
        nunca(v, "2027-01-01", "2031-05-04", "El dueño del club nuevo")
        noNombran(v, listOf("Rafe", "Sully"))
    }

    @Test fun louSaleDeGiraYPasaMesesSinClub() {
        val v = "pianista"
        vivenElEstado(v, "gira", "2029-06-19", "2029-08-29", listOf(1.0, 5.0, 9.0, 13.5, 15.0, 18.0, 19.5, 20.5, 23.0))
        varianEnElEstado(v, "2029-06-19", "2029-08-29", 23.0, 2)
        vivenElEstado(v, "sin_club", "2031-02-18", "2031-05-04", listOf(2.0, 15.0, 20.5, 23.0))
        varianEnElEstado(v, "2031-02-18", "2031-05-04", 23.0, 2)
        assertEquals(null, cuento(v, "2031-05-06").ficha.estado)
    }
}
