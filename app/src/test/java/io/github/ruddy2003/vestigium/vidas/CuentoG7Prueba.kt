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
 * Los cuentos del grupo 7, todos de ficción. Las pruebas generales (forma, largo, repeticiones, certeza) están en
 * [CuentoPrueba] y corren solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie
 * aparezca antes ni después de tiempo y que los estados se noten. Tanda T7a: la astronauta, la recolectora y el
 * funcionario. Tanda T7b: la residente y el archivista.
 */
class CuentoG7Prueba {
    private fun pieza(vida: String) = TodasLasPiezas.de(vida)!!
    private fun cal(vida: String) = Calendarios.de(vida)!!
    private fun quien(vida: String) = OtrasVidas.porId(vida)!!
    private fun cuento(vida: String, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(vida), cal(vida)), quien(vida), cal(vida), pieza(vida), hora)!!
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a ocho horas distintas (cada una con su párrafo del trabajo). */
    private fun todoElDia(vida: String, dia: String) = listOf(1.0, 5.75, 7.25, 9.0, 12.5, 15.0, 18.5, 21.0).flatMap { cuento(vida, dia, it).parrafos }.joinToString(" ")
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
    private fun varianEnElEstado(v: String, desde: String, hasta: String, hora: Double, minimo: Int) {
        val normales = normales(v, desde, hasta)
        assertTrue("$v: pocas mañanas distintas", normales.map { cuento(v, it, hora).parrafos[Parrafo.MANANA.ordinal] }.toSet().size >= minimo)
        assertTrue("$v: pocos trabajos distintos a las $hora", normales.map { cuento(v, it, hora).parrafos[Parrafo.TRABAJO.ordinal] }.toSet().size >= minimo)
        assertTrue("$v: pocos anocheceres distintos", normales.map { cuento(v, it, hora).parrafos[Parrafo.ANOCHECER.ordinal] }.toSet().size >= minimo)
    }

    private fun nunca(v: String, desde: String, hasta: String, texto: String) =
        normales(v, desde, hasta).forEach { assertFalse("$v, $it: «$texto» fuera de tiempo", texto in todoElDia(v, it)) }
    private fun alguna(v: String, desde: String, hasta: String, texto: String) =
        assertTrue("$v: «$texto» no sale entre $desde y $hasta", normales(v, desde, hasta).any { texto in todoElDia(v, it) })
    private fun descansa(v: String, dia: String) = listOf(8.0, 14.0).forEach { h ->
        assertTrue("$v, $dia, $h h: trabaja en su día de fiesta", "no trabaja" in cuento(v, dia, h).parrafos[Parrafo.TRABAJO.ordinal])
    }
    private fun fechas(v: String, id: String) = cal(v).temporadas.mapIndexedNotNull { i, t -> t.dias.firstOrNull { it.id == id }?.let { "${2027 + i}-${it.fecha}" } }

    @Test fun estanLasPiezasDeLaTandaT7aSinElHuecoDelNombre() {
        mapOf("astronauta" to Genero.FEMENINO, "semillera" to Genero.FEMENINO, "funcionario" to Genero.MASCULINO).forEach { (v, g) ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(v, g, pieza(v).genero)
            // Sus fichas se llaman «Una astronauta», «Una recolectora», «Un funcionario»: el cuento no usa el hueco del nombre.
            assertTrue("$v usa el hueco del nombre", pieza(v).textos().none { "{nombre}" in it })
            listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertFalse("$v, $d", quien(v).nombre in todoElDia(v, d)) }
        }
    }

    // ---------- La astronauta ----------

    @Test fun enElCuentoDeLaAstronautaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "astronauta"
        // Su madre muere antes de la temporada 2: la noticia llega el 16 de marzo.
        nunca(v, "2028-03-16", "2031-12-31", "Su madre vive en la Tierra"); nunca(v, "2027-01-01", "2028-03-15", "Su madre murió")
        alguna(v, "2028-03-20", "2028-12-31", "Su madre murió")
        // Sana: aprendiz desde el 10 de junio de la 1, ingeniera desde el 28 de septiembre de la 2, madre desde el 10 de abril de la 5.
        normales(v, "2027-01-01", "2027-06-09").forEach { assertFalse("$it: Sana antes de tiempo", nombra(v, it, "Sana")) }
        nunca(v, "2027-01-01", "2028-09-27", "Sana ya es ingeniera"); nunca(v, "2027-01-01", "2031-04-09", "Sana tiene una hija")
        // Ade: se lo pide el 5 de diciembre de la 3; viven juntos desde el 14 de febrero de la 4.
        nunca(v, "2027-01-01", "2029-12-04", "Ade le ha pedido"); nunca(v, "2027-01-01", "2030-02-13", "viven juntos en un camarote")
        nunca(v, "2030-02-14", "2031-12-31", "Ade es el botánico de la granja")
        noNombran(v, listOf("Sana", "Ade", "Varga", "Nilo", "madre"))
    }

    @Test fun laAstronautaViveLaParadaYElAnilloSinPeso() {
        val v = "astronauta"
        val horas = listOf(1.0, 6.5, 9.0, 12.5, 13.5, 16.0, 18.5, 20.0, 21.5)
        vivenElEstado(v, "parada", "2027-10-06", "2027-10-14", horas); varianEnElEstado(v, "2027-10-06", "2027-10-14", 9.0, 2)
        vivenElEstado(v, "parada", "2031-10-06", "2031-10-14", horas)
        vivenElEstado(v, "ingravidez", "2029-05-11", "2029-05-31", horas); varianEnElEstado(v, "2029-05-11", "2029-05-31", 9.0, 2)
    }

    // ---------- La recolectora ----------

    @Test fun enElCuentoDeLaRecolectoraNadieApareceAntesNiDespuesDeTiempo() {
        val v = "semillera"
        // Lía, desde el 25 de mayo de la 1.
        normales(v, "2027-01-01", "2027-05-24").forEach { assertFalse("$it: Lía antes de tiempo", nombra(v, it, "Lía")) }
        alguna(v, "2027-06-01", "2027-12-31", "Lía es la niña")
        // Amalia: se cae el 15 de agosto de la 3 y muere el 8 de febrero de la 4.
        nunca(v, "2029-08-15", "2031-12-31", "Sube cada mañana a la azotea con un bastón")
        nunca(v, "2027-01-01", "2029-08-14", "ya no sube a la azotea"); nunca(v, "2030-02-08", "2031-12-31", "ya no sube a la azotea")
        nunca(v, "2027-01-01", "2030-02-07", "La vieja Amalia murió")
        // Teo: juntos desde el 2 de diciembre de la 2; ella espera desde el 18 de abril de la 4; la hija nace el 1 de diciembre.
        nunca(v, "2027-01-01", "2028-12-01", "Teo y la recolectora viven juntos")
        nunca(v, "2027-01-01", "2030-04-17", "espera un hijo"); nunca(v, "2030-12-01", "2031-12-31", "espera un hijo")
        nunca(v, "2027-01-01", "2030-11-30", "Su hija nació")
        // El puente, arreglado desde el 20 de junio de la 4.
        nunca(v, "2027-01-01", "2030-06-19", "El puente viejo ya está arreglado"); nunca(v, "2030-06-20", "2031-12-31", "El puente viejo está roto")
        noNombran(v, listOf("Lía", "Amalia", "Teo"))
    }

    @Test fun laRecolectoraSaleDeViajeDosVeces() {
        val v = "semillera"
        val horas = listOf(1.0, 5.75, 8.0, 12.0, 13.5, 15.0, 18.0, 20.0)
        vivenElEstado(v, "expedicion", "2028-07-02", "2028-07-21", horas); varianEnElEstado(v, "2028-07-02", "2028-07-21", 8.0, 2)
        vivenElEstado(v, "expedicion", "2031-05-26", "2031-06-14", horas)
    }

    // ---------- El funcionario ----------

    @Test fun enElCuentoDelFuncionarioNadieApareceAntesNiDespuesDeTiempo() {
        val v = "funcionario"
        // Anselm desaparece el 20 de febrero de la 1.
        nunca(v, "2027-02-20", "2031-12-31", "Anselm, su compañero de la mesa"); nunca(v, "2027-01-01", "2027-02-19", "la que era de Anselm")
        // Mira, del 5 de marzo de la 2 al 20 de agosto de la 3.
        nunca(v, "2027-01-01", "2028-03-04", "Mira trabaja en el archivo"); nunca(v, "2029-08-20", "2031-12-31", "Mira trabaja en el archivo")
        nunca(v, "2027-01-01", "2029-08-19", "A Mira la trasladaron")
        // El vecino del violín, hasta el 25 de junio de la 2.
        nunca(v, "2028-06-25", "2031-12-31", "toca el violín"); nunca(v, "2027-01-01", "2028-06-24", "la del viejo del violín")
        noNombran(v, listOf("Anselm", "Mira"))
    }

    @Test fun elFuncionarioPasaCampanasVisitaASuHermanaYNoTrabajaEnLasFiestas() {
        val v = "funcionario"
        val horas = listOf(1.0, 7.25, 8.0, 10.0, 12.75, 15.0, 19.0, 21.0)
        vivenElEstado(v, "campana", "2028-04-02", "2028-04-21", horas); varianEnElEstado(v, "2028-04-02", "2028-04-21", 10.0, 2)
        vivenElEstado(v, "campana", "2030-03-21", "2030-04-09", horas)
        vivenElEstado(v, "provincia", "2030-06-29", "2030-06-29", horas)
        listOf("anio_nuevo", "fundador", "desfile", "victoria").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- Tanda T7b ----------

    @Test fun estanLasPiezasDeLaTandaT7bSinElHuecoDelNombre() {
        mapOf("residente" to Genero.FEMENINO, "archivista" to Genero.MASCULINO).forEach { (v, g) ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(v, g, pieza(v).genero)
            assertTrue("$v usa el hueco del nombre", pieza(v).textos().none { "{nombre}" in it })
            // «Una residente» no sale; «El archivista» sí, porque así lo escribe el cuento.
            if (v == "residente") listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertFalse("$v, $d", quien(v).nombre in todoElDia(v, d)) }
        }
    }

    // ---------- La residente ----------

    @Test fun enElCuentoDeLaResidenteNadieApareceAntesNiDespuesDeTiempo() {
        val v = "residente"
        // Lina: permiso el 12 de octubre de la 1, hijo el 8 de julio de la 2.
        nunca(v, "2027-01-01", "2027-10-11", "A Lina le han dado por fin el permiso"); nunca(v, "2028-07-08", "2031-12-31", "A Lina le han dado por fin el permiso")
        nunca(v, "2027-01-01", "2028-07-07", "Lina tiene un hijo")
        // Noa: piden cuarto el 28 de noviembre de la 2; juntos desde el 2 de mayo de la 3; fuera del 10 de mayo al 28 de julio de la 4.
        nunca(v, "2027-01-01", "2028-11-27", "han pedido juntas un cuarto doble")
        nunca(v, "2027-01-01", "2029-05-01", "viven en un cuarto doble")
        nunca(v, "2027-01-01", "2030-05-09", "Noa está fuera"); nunca(v, "2030-07-28", "2031-12-31", "Noa está fuera")
        alguna(v, "2030-05-15", "2030-07-20", "Noa está fuera")
        // Noa es una mujer: ningún texto la trata de hombre ni queda el nombre de antes.
        nunca(v, "2027-01-01", "2031-12-31", "Ezra"); nunca(v, "2027-01-01", "2031-12-31", "muchacho alto")
        nunca(v, "2027-01-01", "2031-03-07", "La residente espera un hijo")
        // Dorotea muere el 10 de diciembre de la 3; el supervisor cambia el 25 de noviembre de la 3.
        nunca(v, "2029-12-10", "2031-12-31", "vive en el cuarto del fondo"); nunca(v, "2027-01-01", "2029-12-09", "La vieja Dorotea murió")
        nunca(v, "2027-01-01", "2029-11-24", "El supervisor nuevo")
        // La Puerta, abierta desde el 20 de septiembre de la 5.
        nunca(v, "2027-01-01", "2031-09-19", "abierta al fondo del pasillo, y en que ya sabe")
        noNombran(v, listOf("Lina", "Noa", "Dorotea"))
    }

    @Test fun laResidenteRacionaElAguaPasaLaCuarentenaYVeLaPuertaAbierta() {
        val v = "residente"
        val horas = listOf(1.0, 6.5, 9.0, 12.5, 15.0, 18.0, 20.0)
        vivenElEstado(v, "racionamiento", "2028-03-16", "2028-04-20", horas); varianEnElEstado(v, "2028-03-16", "2028-04-20", 9.0, 2)
        vivenElEstado(v, "cuarentena", "2029-02-02", "2029-02-25", horas); varianEnElEstado(v, "2029-02-02", "2029-02-25", 9.0, 2)
        vivenElEstado(v, "abierto", "2031-09-21", "2031-12-30", horas); varianEnElEstado(v, "2031-09-21", "2031-12-30", 9.0, 2)
        listOf("anio_nuevo", "fundadores", "sol_falso", "nochebuena").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- El archivista ----------

    @Test fun enElCuentoDelArchivistaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "archivista"
        // El signo, desde el 14 de septiembre de la 1.
        nunca(v, "2027-01-01", "2027-09-13", "encuentra otra vez el signo")
        // El profesor: le escribe el 10 de febrero de la 2 y contesta el 15 de abril de la 5.
        nunca(v, "2028-02-10", "2031-12-31", "contestó con una postal")
        nunca(v, "2027-01-01", "2028-02-09", "El profesor no contesta"); nunca(v, "2031-04-15", "2031-12-31", "El profesor no contesta")
        nunca(v, "2027-01-01", "2031-04-14", "contestó por fin a sus cartas")
        // Baltasar muere el 22 de octubre de la 4; un torrero nuevo desde el 5 de marzo de la 5.
        nunca(v, "2030-10-22", "2031-12-31", "vive solo en el faro desde hace cuarenta años")
        nunca(v, "2027-01-01", "2030-10-21", "Baltasar, el torrero, murió"); nunca(v, "2031-03-05", "2031-12-31", "Baltasar, el torrero, murió")
        nunca(v, "2027-01-01", "2031-03-04", "llegó un torrero nuevo")
        noNombran(v, listOf("Baltasar"))
    }

    @Test fun elArchivistaPasaLaFiebreYElFaroYNoTrabajaEnLasFiestas() {
        val v = "archivista"
        val horas = listOf(1.0, 9.5, 12.0, 15.0, 17.5, 18.5, 22.0)
        vivenElEstado(v, "fiebre", "2029-03-02", "2029-03-24", horas); varianEnElEstado(v, "2029-03-02", "2029-03-24", 12.0, 2)
        vivenElEstado(v, "faro", "2030-01-21", "2030-02-09", horas); varianEnElEstado(v, "2030-01-21", "2030-02-09", 12.0, 2)
        listOf("anio_nuevo", "san_pedro", "carmen", "navidad").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }
}
