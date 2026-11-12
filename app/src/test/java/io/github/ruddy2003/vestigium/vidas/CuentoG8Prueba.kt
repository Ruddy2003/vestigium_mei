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
 * Los cuentos del grupo 8, todos de ficción. Las pruebas generales (forma, largo, repeticiones, certeza) están en
 * [CuentoPrueba] y corren solas sobre todas las piezas registradas; aquí va lo propio de cada historia: que nadie
 * aparezca antes ni después de tiempo y que los estados se noten. Tanda T8a: la escriba y la doctora. Tanda T8b: Ruth y
 * Brenna.
 */
class CuentoG8Prueba {
    private fun pieza(vida: String) = TodasLasPiezas.de(vida)!!
    private fun cal(vida: String) = Calendarios.de(vida)!!
    private fun quien(vida: String) = OtrasVidas.porId(vida)!!
    private fun cuento(vida: String, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(vida), cal(vida)), quien(vida), cal(vida), pieza(vida), hora)!!
    private fun dias(desde: String, hasta: String) =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { it <= LocalDate.parse(hasta) }.map { it.toString() }.toList()
    /** Todo lo que dice el cuento ese día, a nueve horas distintas (cada una con su párrafo del trabajo). */
    private fun todoElDia(vida: String, dia: String) = listOf(1.0, 5.75, 7.25, 9.0, 12.5, 15.0, 18.5, 20.0, 21.75).flatMap { cuento(vida, dia, it).parrafos }.joinToString(" ")
    private fun normales(vida: String, desde: String, hasta: String) = dias(desde, hasta).filter { !cuento(vida, it).especial }
    private fun textosDe(vida: String, lugar: Parrafo, estado: String) =
        pieza(vida).variantes.getValue(lugar).filter { it.estado == estado }.map { Cuento.llenar(it.texto, mapOf("nombre" to quien(vida).nombre), pieza(vida).genero) }.toSet()
    private fun palabra(p: String) = Regex("""\b$p\b""")
    private fun nombra(vida: String, dia: String, quien: String) = palabra(quien).containsMatchIn(todoElDia(vida, dia))
    private fun nadieAntes(v: String, quien: String, hasta: String) =
        normales(v, "2027-01-01", hasta).forEach { assertFalse("$v, $it: $quien antes de tiempo", nombra(v, it, quien)) }

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

    @Test fun estanLasPiezasDeLaTandaT8aSinElHuecoDelNombre() {
        listOf("escriba", "cientifica").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(v, Genero.FEMENINO, pieza(v).genero)
            // Sus fichas se llaman «Una escriba» y «La doctora»: el cuento no usa el hueco del nombre.
            assertTrue("$v usa el hueco del nombre", pieza(v).textos().none { "{nombre}" in it })
        }
        // «Una escriba» no sale nunca; «La doctora» sí, porque así la llama el cuento.
        listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertFalse("escriba, $d", "Una escriba" in todoElDia("escriba", d)) }
    }

    // ---------- La escriba ----------

    @Test fun enElCuentoDeLaEscribaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "escriba"
        // Jimena: la encuentran el 24 de febrero de la 1; letras el 2 de octubre; lee el 28 de septiembre de la 2;
        // candilera el 20 de abril de la 4; patrulla el 20 de febrero de la 5.
        nadieAntes(v, "Jimena", "2027-02-23")
        nunca(v, "2027-10-02", "2031-12-31", "la niña que encontraron sola")
        nunca(v, "2027-01-01", "2027-10-01", "Jimena aprende las letras"); nunca(v, "2028-09-28", "2031-12-31", "Jimena aprende las letras")
        nunca(v, "2027-01-01", "2028-09-27", "Jimena ya lee sola"); nunca(v, "2027-01-01", "2030-04-19", "Jimena es candilera")
        nunca(v, "2027-01-01", "2031-02-19", "Jimena patrulla ya")
        // Fermín: pierde la vista el 28 de julio de la 1, le da las llaves el 25 de enero de la 3 y muere el 30 de noviembre de la 4.
        nunca(v, "2027-07-28", "2031-12-31", "con las llaves colgadas del cinturón")
        nunca(v, "2027-01-01", "2029-01-24", "Fermín, ciego ya, le dio las llaves"); nunca(v, "2030-11-30", "2031-12-31", "Fermín, ciego ya")
        nunca(v, "2027-01-01", "2030-11-29", "Fermín murió"); alguna(v, "2030-12-01", "2031-03-31", "Fermín murió")
        // Ulises cuelga el caparazón el 10 de junio de la 3.
        nunca(v, "2029-06-10", "2031-12-31", "Ulises es el caballero más viejo"); nunca(v, "2027-01-01", "2029-06-09", "Ulises colgó el caparazón")
        // Damián: juntos desde el 6 de junio de la 2, la pierna del 30 de julio de la 3, el cuarto del 25 de noviembre de la 3,
        // el hijo que espera desde el 20 de octubre de la 4, y Ciro desde el 25 de mayo de la 5.
        nunca(v, "2028-06-06", "2031-12-31", "Hasta ahí llega todo")
        nunca(v, "2027-01-01", "2029-07-29", "tiene la pierna rota"); nunca(v, "2029-11-25", "2031-12-31", "tiene la pierna rota")
        nunca(v, "2029-11-25", "2031-12-31", "celda del ala de las escribas"); nunca(v, "2027-01-01", "2029-11-24", "viven juntos en un cuarto")
        alguna(v, "2029-12-01", "2030-03-31", "viven juntos en un cuarto")
        nunca(v, "2027-01-01", "2030-10-19", "espera un hijo"); nadieAntes(v, "Ciro", "2031-05-24")
        alguna(v, "2031-06-01", "2031-12-31", "Ciro")
        // Desde que espera un hijo, nada de patrullas largas por el páramo.
        nunca(v, "2030-10-20", "2031-12-31", "va de patrulla por el páramo"); nunca(v, "2030-10-20", "2031-12-31", "baja con la patrulla a una casa hundida")
        // Nieves manda la patrulla desde la emboscada, el 28 de abril de la 4; el núcleo sale de la Bóveda el 16 de mayo de la 2.
        nunca(v, "2027-01-01", "2030-04-27", "Nieves manda la patrulla"); nadieAntes(v, "núcleo", "2028-05-15")
        nunca(v, "2027-01-01", "2031-08-27", "la máquina de los planos")
        noNombran(v, listOf("Jimena", "Fermín", "Ulises", "Damián", "Nieves", "Ciro"))
    }

    @Test fun laEscribaViajaAlSurSeEncierraConElNucleoYCuraLaHerida() {
        val v = "escriba"
        val horas = listOf(1.0, 5.5, 9.0, 12.5, 15.0, 19.5, 21.0)
        vivenElEstado(v, "expedicion", "2028-04-27", "2028-06-13", horas); varianEnElEstado(v, "2028-04-27", "2028-06-13", 9.0, 2)
        vivenElEstado(v, "encierro", "2029-02-16", "2029-03-13", horas); varianEnElEstado(v, "2029-02-16", "2029-03-13", 9.0, 2)
        vivenElEstado(v, "herida", "2030-04-29", "2030-06-01", horas); varianEnElEstado(v, "2030-04-29", "2030-06-01", 9.0, 2)
        listOf("anio_nuevo", "fundacion", "vigilia", "noche_larga").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- La doctora ----------

    @Test fun enElCuentoDeLaDoctoraNadieApareceAntesNiDespuesDeTiempo() {
        val v = "cientifica"
        // Lauro: llega el 3 de febrero de la 1, pregunta el 28 de agosto, se va el 27 de septiembre de la 4, y ella lo ve
        // arriba el 17 de mayo de la 5.
        nadieAntes(v, "Lauro", "2027-02-02")
        nunca(v, "2027-01-01", "2027-08-27", "Lauro, su asistente, pregunta")
        listOf("trabaja con Lauro en la mesa larga", "Lauro, que no come", "Lauro apaga una a una").forEach { nunca(v, "2030-09-27", "2031-12-31", it) }
        nunca(v, "2027-01-01", "2031-05-16", "A Lauro lo vio arriba")
        // Hilda deja el laboratorio el 4 de diciembre de la 1 y muere el 20 de enero de la 3.
        nunca(v, "2027-12-04", "2031-12-31", "tiene ochenta años")
        nunca(v, "2027-01-01", "2027-12-03", "Hilda ya no baja"); nunca(v, "2029-01-20", "2031-12-31", "Hilda ya no baja")
        nunca(v, "2027-01-01", "2029-01-19", "Hilda murió")
        // Tomé: Mentes desde el 30 de octubre de la 1; entra el 10 de julio de la 2; quiere irse arriba desde el 10 de octubre de la 5.
        nunca(v, "2027-01-01", "2027-10-29", "Tomé estudia para la prueba de Mentes"); nunca(v, "2028-07-10", "2031-12-31", "Tomé estudia para la prueba de Mentes")
        nunca(v, "2027-01-01", "2028-07-09", "Tomé trabaja en Mentes"); nunca(v, "2027-01-01", "2031-10-09", "Tomé quiere volver arriba")
        // Coral: el permiso, el 26 de julio de la 2; Alba nace el 30 de agosto de la 3.
        nunca(v, "2028-07-26", "2031-12-31", "Coral quiere un hijo"); nadieAntes(v, "Alba", "2029-08-29")
        // Neva: baja el 24 de febrero de la 2, la devuelven el 12 de mayo; la vuelve a encontrar el 8 de mayo de la 5.
        nadieAntes(v, "Neva", "2028-02-23")
        nunca(v, "2028-05-12", "2031-12-31", "vive encerrada en un cuarto de Medicina"); nunca(v, "2027-01-01", "2031-05-07", "Neva vive arriba, en un patio")
        // El rector Mora hasta el 3 de marzo de la 3; la rectora Ibarra desde entonces.
        normales(v, "2029-03-03", "2031-12-31").forEach { assertFalse("$it: Mora fuera de tiempo", nombra(v, it, "Mora")) }
        nadieAntes(v, "Ibarra", "2029-03-02")
        // La mancha verde desde el 10 de diciembre de la 4; el sol, desde el 27 de abril de la 5.
        nunca(v, "2027-01-01", "2030-12-09", "una mancha verde")
        nunca(v, "2031-04-27", "2031-12-31", "Piensa en el sol, que vio una vez"); nunca(v, "2027-01-01", "2031-04-26", "que vio por fin")
        noNombran(v, listOf("Lauro", "Tomé", "Hilda", "Coral", "Alba", "Neva", "Mora", "Ibarra"))
    }

    @Test fun laDoctoraPasaLaFiebreLaCampanaElCierreYSubeArriba() {
        val v = "cientifica"
        val horas = listOf(1.0, 7.0, 9.0, 13.0, 15.0, 20.0, 21.75)
        vivenElEstado(v, "cuarentena", "2028-03-09", "2028-04-04", horas); varianEnElEstado(v, "2028-03-09", "2028-04-04", 9.0, 2)
        vivenElEstado(v, "experimento", "2029-05-04", "2029-05-23", horas); varianEnElEstado(v, "2029-05-04", "2029-05-23", 9.0, 2)
        vivenElEstado(v, "cierre", "2030-09-29", "2030-10-11", horas); varianEnElEstado(v, "2030-09-29", "2030-10-11", 9.0, 2)
        vivenElEstado(v, "arriba", "2031-04-28", "2031-05-23", horas); varianEnElEstado(v, "2031-04-28", "2031-05-23", 9.0, 2)
        // Arriba, el tiempo no sale de las lámparas: el calendario dice «sin clima», y el párrafo es el del estado.
        val arriba = textosDe(v, Parrafo.TIEMPO, "arriba")
        normales(v, "2031-04-28", "2031-05-23").forEach { assertTrue("$it: arriba con el tiempo de abajo", cuento(v, it).parrafos[Parrafo.TIEMPO.ordinal] in arriba) }
        listOf("anio_nuevo", "fundacion", "luz_larga", "noche_larga").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- Tanda T8b ----------

    @Test fun estanLasPiezasDeLaTandaT8bConSuNombre() {
        listOf("colona", "guia_caminos").forEach { v ->
            assertEquals(v, pieza(v).vida)
            assertEquals("$v: solo la temporada 1 está escrita", setOf(1), pieza(v).temporadasEscritas)
            assertEquals(emptyList<String>(), pieza(v).problemas(cal(v)))
            assertEquals(v, Genero.FEMENINO, pieza(v).genero)
            // Ruth y Brenna tienen nombre propio: el cuento usa el hueco, y el nombre sale cada día.
            assertTrue("$v no usa el hueco del nombre", pieza(v).textos().any { "{nombre}" in it })
            listOf("2027-03-03", "2029-09-09", "2031-11-11").forEach { d -> assertTrue("$v, $d", quien(v).nombre in todoElDia(v, d)) }
        }
    }

    // ---------- Ruth ----------

    @Test fun enElCuentoDeRuthNadieApareceAntesNiDespuesDeTiempo() {
        val v = "colona"
        // Nora: lee sola el 20 de octubre de la 1, hace guardia desde el 30 de octubre de la 4 y habla por la radio desde el 28 de septiembre de la 5.
        nunca(v, "2027-10-20", "2031-12-31", "Nora está aprendiendo a leer"); nunca(v, "2027-01-01", "2027-10-19", "que lee en voz alta para los niños")
        nunca(v, "2027-01-01", "2030-10-29", "Nora ya hace guardia en el muro"); nunca(v, "2027-01-01", "2031-09-27", "Nora habla por la radio del Remanso")
        // Abel: lo espera desde el 30 de julio de la 2, nace el 12 de marzo de la 3 y camina desde el 28 de marzo de la 4.
        nadieAntes(v, "Abel", "2029-03-11")
        nunca(v, "2027-01-01", "2028-07-29", "Ruth espera un hijo"); nunca(v, "2029-03-12", "2031-12-31", "Ruth espera un hijo")
        nunca(v, "2030-03-28", "2031-12-31", "Abel, su hijo, nació"); nunca(v, "2027-01-01", "2030-03-27", "Abel ya camina")
        // Agustina muere el 20 de febrero de la 4; Bruno se queda en Cuatro Molinos el 12 de julio de la 4 y vuelve de visita el 25 de octubre de la 5.
        nunca(v, "2030-02-20", "2031-12-31", "Agustina, su madre, vive con ellos"); nunca(v, "2027-01-01", "2030-02-19", "La silla de Agustina")
        nunca(v, "2030-07-12", "2031-12-31", "vive en la casa de al lado"); nunca(v, "2027-01-01", "2030-07-11", "Bruno se quedó en Cuatro Molinos")
        nunca(v, "2027-01-01", "2031-10-24", "Bruno vive en Cuatro Molinos con su mujer")
        // Damián muere en el portón y Tomás sale herido el 3 de agosto de la 3.
        nunca(v, "2029-08-03", "2031-12-31", "Damián guarda el portón desde antes"); nunca(v, "2027-01-01", "2029-08-02", "un sombrero de paja colgado de un clavo")
        nunca(v, "2027-01-01", "2029-08-02", "Tomás no ha recuperado del todo el brazo")
        // Joaquín y Lidia llegan el 28 de abril de la 2; el jabón, el 10 de diciembre. Celia, en persona desde el 5 de junio de la 5.
        nadieAntes(v, "Joaquín", "2028-04-27"); nadieAntes(v, "Lidia", "2028-04-27")
        nunca(v, "2028-12-10", "2031-12-31", "Joaquín y Lidia llegaron del sur"); nunca(v, "2027-01-01", "2028-12-09", "Lidia hace jabón")
        nunca(v, "2031-06-05", "2031-12-31", "Nadie del Remanso la ha visto nunca"); nunca(v, "2027-01-01", "2031-06-04", "ya tiene cara para Ruth")
        // Jefa de las guardias de noche desde el 20 de mayo de la 2; la torre del agua, desde el 25 de octubre de la 2.
        nunca(v, "2027-01-01", "2028-05-19", "la lista de las guardias de la noche"); nunca(v, "2027-01-01", "2028-05-19", "Es la jefa de las guardias de noche")
        nunca(v, "2027-01-01", "2028-10-24", "La torre del agua se ve desde el llano")
        // El muro: el hueco del este se cierra el 15 de noviembre de la 1; arde el 2 de agosto de la 3; piedra desde el 15 de
        // octubre de la 3; terminado el 25 de agosto de la 5.
        nunca(v, "2027-11-15", "2031-12-31", "donde ahora solo hay un carro volcado")
        listOf("Por el lado este el muro tiene todavía un hueco", "Del lado del este el muro no está cerrado").forEach { nunca(v, "2027-11-15", "2029-08-01", it) }
        nunca(v, "2029-08-02", "2031-12-31", "El muro es una colcha de todo")
        nunca(v, "2027-01-01", "2029-08-01", "torcidas por el fuego"); nunca(v, "2029-10-15", "2031-12-31", "torcidas por el fuego")
        nunca(v, "2029-10-15", "2031-12-31", "cambia tablas en el muro")
        listOf("sube piedra del arroyo", "El muro nuevo se levanta con piedra", "El muro de piedra sube cada semana").forEach { nunca(v, "2027-01-01", "2029-10-14", it) }
        listOf("El muro de piedra rodea ya", "El muro de piedra está terminado").forEach { nunca(v, "2027-01-01", "2031-08-24", it) }
        alguna(v, "2031-09-01", "2031-12-31", "El muro de piedra")
        noNombran(v, listOf("Nora", "Abel", "Agustina", "Bruno", "Damián", "Tomás", "Joaquín", "Lidia", "Celia"))
    }

    @Test fun ruthAyudaFueraAguantaElAsedioYLaVentiscaYNoTrabajaEnLasFiestas() {
        val v = "colona"
        val horas = listOf(1.0, 5.5, 9.0, 12.5, 15.0, 19.5)
        vivenElEstado(v, "auxilio", "2027-06-02", "2027-06-15", horas); varianEnElEstado(v, "2027-06-02", "2027-06-15", 9.0, 2)
        vivenElEstado(v, "auxilio", "2030-06-26", "2030-07-11", horas); vivenElEstado(v, "auxilio", "2031-05-31", "2031-06-11", horas)
        vivenElEstado(v, "asedio", "2029-07-29", "2029-08-05", horas); varianEnElEstado(v, "2029-07-29", "2029-08-05", 9.0, 2)
        vivenElEstado(v, "encierro", "2030-01-15", "2030-02-05", horas); varianEnElEstado(v, "2030-01-15", "2030-02-05", 9.0, 2)
        listOf("anio_nuevo", "fundacion", "canciones", "elotes", "noche_larga").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
    }

    // ---------- Brenna ----------

    @Test fun enElCuentoDeBrennaNadieApareceAntesNiDespuesDeTiempo() {
        val v = "guia_caminos"
        // Tobin: llega el 2 de junio de la 1; la canción, el 3 de mayo de la 2; se lo pide el 2 de noviembre de la 3; el cuarto
        // alto, el 12 de enero de la 4; ella espera desde el 12 de septiembre de la 4; la niña nace el 12 de abril de la 5.
        nadieAntes(v, "Tobin", "2027-06-01")
        nunca(v, "2028-05-03", "2031-12-31", "Tobin es un arpista del valle que subió")
        nunca(v, "2027-01-01", "2028-05-02", "Desde la canción, toda la aldea"); nunca(v, "2029-11-02", "2031-12-31", "Desde la canción, toda la aldea")
        nunca(v, "2027-01-01", "2029-11-01", "Tobin le ha pedido que vivan juntos"); nunca(v, "2030-01-12", "2031-12-31", "Tobin le ha pedido que vivan juntos")
        listOf("viven en el cuarto alto", "con el brazo de Tobin encima", "pegada a Tobin").forEach { nunca(v, "2027-01-01", "2030-01-11", it) }
        nunca(v, "2027-01-01", "2030-09-11", "Brenna espera un hijo"); nunca(v, "2031-04-12", "2031-12-31", "Brenna espera un hijo")
        nadieAntes(v, "niña", "2031-04-11")
        // Arn: la runa del 2 de octubre de la 1; se pierde el 13 de octubre de la 2; la linterna, el 2 de abril de la 3; su runa
        // en Torremuda, el 3 de agosto de la 3; el refugio con su nombre, el 28 de septiembre de la 4.
        nunca(v, "2027-01-01", "2027-10-01", "camino que sigue")
        nunca(v, "2028-10-13", "2031-12-31", "Todavía guía, con una linterna de mina")
        nunca(v, "2027-01-01", "2028-10-12", "El viejo Arn no volvió de la niebla"); nunca(v, "2029-04-02", "2031-12-31", "El viejo Arn no volvió de la niebla")
        nunca(v, "2027-01-01", "2029-04-01", "el deshielo devolvió la linterna"); nunca(v, "2027-01-01", "2029-08-02", "Piensa en la runa de Arn grabada")
        nunca(v, "2027-01-01", "2030-09-27", "refugio de Arn")
        // Neli: desde el 5 de agosto de la 2; aprendiz el 22 de abril de la 3; su tramo el 20 de mayo de la 4; sola el 21 de agosto de la 5.
        nadieAntes(v, "Neli", "2028-08-04")
        nunca(v, "2029-04-22", "2031-12-31", "tiene trece años"); nunca(v, "2027-01-01", "2029-04-21", "Neli es aprendiz del Gremio")
        nunca(v, "2027-01-01", "2030-05-19", "Neli ya guía sola su tramo"); nunca(v, "2027-01-01", "2031-08-20", "Neli ha llevado sola una compañía")
        // Corvin, desde la veta nueva (8 de abril de la 1) hasta que se va (5 de junio de la 5); Bastián, del 15 de abril de la 1
        // al 17 de junio de la 4.
        nadieAntes(v, "Corvin", "2027-04-07"); nadieAntes(v, "Bastián", "2027-04-14")
        nunca(v, "2030-06-17", "2031-12-31", "Corvin, el capataz de los mineros, es un hombre ancho")
        nunca(v, "2027-01-01", "2030-06-16", "Corvin ya no baja a la posada"); nunca(v, "2031-06-05", "2031-12-31", "Corvin ya no baja a la posada")
        nunca(v, "2027-01-01", "2031-06-04", "desde que Corvin se llevó")
        normales(v, "2031-06-05", "2031-12-31").forEach { assertFalse("$it: Bastián fuera de tiempo", nombra(v, it, "Bastián")) }
        // Orla le da el libro el 28 de octubre de la 5; el rayo parte el pino el 10 de julio de la 5; el alud se lleva el refugio
        // de la Cabra el 25 de febrero de la 2; el mapa del paso, desde el 20 de febrero de la 1.
        nunca(v, "2031-10-28", "2031-12-31", "Orla guarda el libro de crónicas"); nunca(v, "2027-01-01", "2031-10-27", "Brenna guarda ahora el libro")
        nunca(v, "2031-07-10", "2031-12-31", "El pino viejo de la posada es más alto"); nunca(v, "2027-01-01", "2031-07-09", "partido por el rayo")
        nunca(v, "2028-02-25", "2031-12-31", "el de la Cabra, colgado"); nunca(v, "2027-01-01", "2027-02-19", "el que empezó un invierno")
        noNombran(v, listOf("Tobin", "Arn", "Neli", "Corvin", "Bastián", "Orla", "niña"))
    }

    @Test fun brennaPasaLosInviernosEnLaPosadaCruzaHastaTorremudaYCriaASuHija() {
        val v = "guia_caminos"
        val horas = listOf(1.0, 5.5, 9.0, 12.5, 15.0, 18.5, 20.0)
        vivenElEstado(v, "paso_cerrado", "2027-01-02", "2027-03-21", horas); varianEnElEstado(v, "2027-01-02", "2027-03-21", 9.0, 3)
        vivenElEstado(v, "paso_cerrado", "2027-12-07", "2027-12-30", horas); vivenElEstado(v, "paso_cerrado", "2031-01-02", "2031-03-24", horas)
        vivenElEstado(v, "travesia", "2029-07-21", "2029-08-18", horas); varianEnElEstado(v, "2029-07-21", "2029-08-18", 9.0, 2)
        vivenElEstado(v, "crianza", "2031-04-13", "2031-05-19", horas); varianEnElEstado(v, "2031-04-13", "2031-05-19", 9.0, 2)
        // Sus fiestas de guardar, la feria con sus dos días.
        listOf("anio_nuevo", "primera_runa", "hierro", "noche_arpa", "fin_anio").flatMap { fechas(v, it) }.forEach { descansa(v, it) }
        fechas(v, "feria").forEach { descansa(v, it); descansa(v, LocalDate.parse(it).plusDays(1).toString()) }
    }
}
