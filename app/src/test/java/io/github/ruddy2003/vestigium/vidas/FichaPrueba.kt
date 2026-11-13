package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El motor de «su año» (tanda P1 del plan de historia y escenas): temporadas, ficha del día, días especiales, arcos,
 * luz y reparto de los párrafos. Todo con un calendario **de prueba**, inventado aquí: no es el de ningún personaje.
 */
class FichaPrueba {
    // ---------------------------------------------------------------- El calendario de prueba

    private fun dia(id: String, fecha: String, tipo: TipoDia, duracion: Int = 1, escena: String = "", cadena: String? = null, clima: Clima? = null) =
        DiaEspecial(id, fecha, duracion, tipo, "Título de $id", escena, CertezaDia.INVENTADO, cadena = cadena, clima = clima)

    private val invierno = mapOf(Clima.NUBES to 50, Clima.LLUVIA to 30, Clima.NIEVE to 20)
    private val verano = mapOf(Clima.DESPEJADO to 70, Clima.NUBES to 20, Clima.TORMENTA to 10)

    private fun temporada(n: Int) = TemporadaVida(
        cuando = "año $n de la prueba", mundo = if (n == 2) "guerra" else "paz", resumen = "Temporada $n del calendario de prueba.",
        dias = listOf(
            dia("fiebre", "02-10", TipoDia.SUCESO, duracion = 3, cadena = "enfermedad"),
            dia("alivio", "02-20", TipoDia.SUCESO, cadena = "enfermedad"),
            dia("pascua", "04-05", TipoDia.FIESTA, duracion = 2, escena = "ramos"),
            dia("siega", "07-10", TipoDia.COSECHA, duracion = 30, escena = "gavillas"),
            dia("feria", "07-20", TipoDia.SUCESO, escena = "puestos"),
            dia("saqueo", "09-01", TipoDia.GUERRA, escena = "humo", clima = Clima.TORMENTA),
            dia("navidad", "12-25", TipoDia.FIESTA, duracion = 12, escena = "velas"),
        ),
    )

    private val CAL = Calendario(
        vida = "cantero",
        estaciones = Calendario.TEMPLADO_NORTE,
        clima = listOf(invierno, invierno, invierno, verano, verano, verano, verano, verano, verano, invierno, invierno, invierno),
        temporadas = (1..5).map { temporada(it) },
        arcos = listOf(
            Arco("obra", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 0.10), PuntoArco(3, "06-30", 0.40), PuntoArco(5, "12-31", 0.60))),
            // Se siembra el 15 de octubre, crece hasta el 30 de junio y se siega de golpe el 10 de julio.
            Arco.cadaAnio("trigo", TipoArco.CULTIVO, "10-15" to 0.0, "06-30" to 1.0, "07-10" to 0.0, saltos = setOf("07-10")),
            // La casa se quema el 1 de septiembre de la temporada 2 y queda reconstruida el 1 de junio de la 3.
            Arco("casa", TipoArco.DANOS, listOf(PuntoArco(2, "08-31", 0.0), PuntoArco(2, "09-01", 1.0, salto = true), PuntoArco(3, "06-01", 0.0))),
        ),
        objetos = listOf("mazo", "cincel", "escuadra", "plomada", "cantaro"),
    )

    private val PIERRE = OtrasVidas.porId("cantero")!!
    private fun ficha(dia: String, cal: Calendario = CAL) = FichaDelDia.de(dia, PIERRE, cal)
    private fun dias(desde: String, hasta: String): List<LocalDate> =
        generateSequence(LocalDate.parse(desde)) { it.plusDays(1) }.takeWhile { !it.isAfter(LocalDate.parse(hasta)) }.toList()

    // ---------------------------------------------------------------- Temporadas y bordes del año

    @Test fun temporadaDeCadaAnio() {
        assertEquals(listOf(1, 1, 1, 2, 3, 4, 5, 5, 5, 5), listOf(2020, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2099).map { Temporadas.de(it) })
        assertEquals(listOf(0, 0, 0, 1, 2, 68), listOf(2026, 2027, 2031, 2032, 2033, 2099).map { Temporadas.vuelta(it) })
        assertTrue(Temporadas.esAvance(2026)); assertFalse(Temporadas.esAvance(2027))
    }

    @Test fun elAnioCambiaConElDiaLogico() {
        // El 31 de diciembre es la última ficha de la temporada; la del 1 de enero ya es de la siguiente. La madrugada
        // del 1 de enero, antes de las 5:00, todavía es el día lógico 31 de diciembre: quien llama pasa ese día.
        val fin = ficha("2027-12-31"); val inicio = ficha("2028-01-01")
        assertEquals(1, fin.temporada); assertEquals(2, inicio.temporada)
        assertEquals(PIERRE.edad, fin.edad); assertEquals(PIERRE.edad + 1, inicio.edad)
        assertEquals(1240, fin.anioHistorico); assertEquals(1241, inicio.anioHistorico)
        assertEquals("año 1 de la prueba", fin.cuando); assertEquals("año 2 de la prueba", inicio.cuando)
        assertEquals("paz", fin.mundo); assertEquals("guerra", inicio.mundo)
    }

    @Test fun avanceDe2026() {
        val f = ficha("2026-11-11")
        assertEquals(1, f.temporada); assertTrue(f.avance); assertEquals(0, f.vuelta)
        assertEquals(PIERRE.edad, f.edad); assertEquals(1240, f.anioHistorico)
        assertFalse(ficha("2027-01-01").avance)
    }

    @Test fun temporadaSeisOMasRepiteLaQuinta() {
        val quinta = ficha("2031-06-15")
        for (anio in listOf(2032, 2033, 2040, 2099)) {
            val f = ficha("$anio-06-15")
            assertEquals(5, f.temporada); assertEquals(anio - 2031, f.vuelta)
            // Nadie envejece ni cambia de año histórico después de la quinta.
            assertEquals(quinta.edad, f.edad); assertEquals(quinta.anioHistorico, f.anioHistorico)
            assertEquals(quinta.cuando, f.cuando); assertEquals(quinta.arcos, f.arcos); assertEquals(quinta.estacion, f.estacion)
        }
        assertEquals(PIERRE.edad + 4, quinta.edad); assertEquals(1244, quinta.anioHistorico)
    }

    @Test fun anioHistoricoAntesDeCristo() {
        // Demeas: 431 a. C. En la temporada 2, el 430 a. C. (la peste).
        assertEquals(listOf(-431, -430, -429, -428, -427), (1..5).map { Temporadas.anioHistorico(-431, it) })
        // No hay año 0: del 1 a. C. se pasa al 1 d. C.
        assertEquals(listOf(-2, -1, 1, 2, 3), (1..5).map { Temporadas.anioHistorico(-2, it) })
        assertEquals(listOf(2077, 2081), listOf(1, 5).map { Temporadas.anioHistorico(2077, it) })
    }

    @Test fun fechasMesDia() {
        assertEquals(0, MesDia.indice("01-01")); assertEquals(58, MesDia.indice("02-28")); assertEquals(59, MesDia.indice("03-01")); assertEquals(364, MesDia.indice("12-31"))
        listOf("02-29", "02-30", "13-01", "00-10", "04-31", "4-5", "0405", "").forEach { assertFalse(it, MesDia.valido(it)) }
        // Bisiesto: el 29 de febrero comparte lugar con el 28, y marzo no se recorre.
        assertEquals(58, MesDia.indice(LocalDate.of(2028, 2, 29))); assertEquals(59, MesDia.indice(LocalDate.of(2028, 3, 1)))
        assertEquals(364, MesDia.indice(LocalDate.of(2028, 12, 31)))
        (0..364).forEach { i -> val d = LocalDate.of(2027, 1, 1).plusDays(i.toLong()); assertEquals(i, MesDia.indice(d)) }
    }

    // ---------------------------------------------------------------- Determinista y una por día

    @Test fun laFichaEsDeterminista() {
        for (d in listOf("2026-12-31", "2027-01-01", "2028-02-29", "2031-12-31", "2032-07-20", "2045-03-03")) {
            val a = ficha(d); val b = ficha(d)
            assertEquals(a, b); assertEquals(a.hashCode(), b.hashCode())
            assertEquals(a.piezas(TAMANOS), b.piezas(TAMANOS))
        }
        // Otro día u otro personaje: otra semilla.
        assertNotEquals(ficha("2027-03-01").semilla, ficha("2027-03-02").semilla)
        val otro = Calendario("remero", CAL.estaciones, CAL.clima, CAL.luz, CAL.temporadas, CAL.arcos, CAL.objetos)
        assertNotEquals(ficha("2027-03-01").semilla, FichaDelDia.de("2027-03-01", OtrasVidas.porId("remero")!!, otro).semilla)
    }

    /** Valores fijados: si cambian, el azar cambió y las fichas de días ya vistos dejarían de coincidir. */
    @Test fun elAzarNoCambiaEntreVersiones() {
        // SplitMix64 con semilla 0: los tres primeros valores de la implementación de referencia.
        val a = Azar(0)
        assertEquals(listOf(-0x1DDF57C684E23251L, 0x6E789E6AA1B965F4L, 0x06C45D188009454FL), List(3) { a.siguiente() })
        assertEquals(SEMILLA_FIJA, Semilla.de("cantero", "2027-01-01"))
        assertEquals(PIEZAS_FIJAS, ficha("2027-01-01").piezas(TAMANOS))
    }

    @Test fun laFichaNoDependeDeLaHora() {
        // Una sola ficha sirve de las 5:00 a las 5:00: la hora solo se le pregunta para la luz y el tramo.
        val f = ficha("2027-05-10")
        assertEquals(Franja.AMANECER, f.luz(5.0).franja); assertEquals(Franja.NOCHE, f.luz(4.99).franja)
        assertEquals(OtrasVidas.Hacer.DORMIR, f.tramo(2.0)!!.que); assertEquals(OtrasVidas.Hacer.DORMIR, f.tramo(26.0)!!.que)
        assertEquals(OtrasVidas.Hacer.TRABAJO, f.tramo(9.5)!!.que)
    }

    @Test fun todasLasVidasTienenTramoATodaHora() {
        (OtrasVidas.BASE + OtrasVidas.DE_TEMAS).forEach { v ->
            val f = FichaDelDia.de("2027-05-10", v, Calendario(v.id, CAL.estaciones, CAL.clima, CAL.luz, CAL.temporadas))
            (0 until 96).forEach { q -> assertTrue("${v.id} a las ${q / 4.0}", f.tramo(q / 4.0) != null) }
        }
    }

    @Test fun sinCalendarioNoHayFicha() {
        assertNull(FichaDelDia.de("2027-05-10", "no_existe"))
        (OtrasVidas.BASE + OtrasVidas.DE_TEMAS).filter { Calendarios.de(it.id) == null }.forEach { assertNull(FichaDelDia.de("2027-05-10", it.id)) }
    }

    /** Los calendarios ya registrados (ninguno en P1; los agregan P2 y de A1 a A8) están bien escritos y dan ficha todo el año. */
    @Test fun losCalendariosRegistradosEstanBien() {
        assertEquals(Calendarios.TODOS.size, Calendarios.TODOS.map { it.vida }.toSet().size)
        Calendarios.TODOS.forEach { cal ->
            assertEquals(cal.vida, emptyList<String>(), cal.problemas())
            dias("2026-12-25", "2032-01-10").forEach { d -> assertEquals(cal.vida, FichaDelDia.de(d.toString(), cal.vida)!!.vida) }
            assertTrue(FichaDelDia.de("2027-05-10", cal.vida) === FichaDelDia.de("2027-05-10", cal.vida))
        }
    }

    // ---------------------------------------------------------------- Días especiales

    @Test fun diasEspecialesConSuDuracion() {
        assertFalse(ficha("2027-04-04").especial)
        val a = ficha("2027-04-05"); val b = ficha("2027-04-06")
        assertTrue(a.especial); assertEquals("pascua", a.principal!!.dia.id); assertEquals(1, a.principal!!.numero); assertTrue(a.principal!!.primerDia)
        assertEquals(2, b.principal!!.numero); assertTrue(b.principal!!.ultimoDia); assertEquals("ramos", b.escena)
        assertFalse(ficha("2027-04-07").especial); assertEquals("", ficha("2027-04-07").escena)
    }

    @Test fun cuandoCoincidenDosMandaElMasCorto() {
        // La feria (1 día) cae dentro de la siega (30 días).
        val f = ficha("2027-07-20")
        assertEquals(listOf("feria", "siega"), f.especiales.map { it.dia.id })
        assertEquals("feria", f.principal!!.dia.id); assertEquals("puestos", f.escena)
        assertEquals(11, f.especiales[1].numero)
        assertEquals(listOf("siega"), ficha("2027-07-21").especiales.map { it.dia.id })
        assertEquals(30, ficha("2027-08-08").principal!!.numero); assertFalse(ficha("2027-08-09").especial)
    }

    @Test fun unaFiestaCruzaElFinDeAnio() {
        // Navidad: del 25 de diciembre al 5 de enero (12 días). En enero sigue, aunque ya sea otra temporada.
        assertEquals(7, ficha("2027-12-31").principal!!.numero)
        val enero = ficha("2028-01-01")
        assertEquals(2, enero.temporada); assertEquals("navidad", enero.principal!!.dia.id); assertEquals(8, enero.principal!!.numero)
        assertEquals(LocalDate.of(2027, 12, 25), enero.principal!!.inicio)
        assertEquals(12, ficha("2028-01-05").principal!!.numero); assertFalse(ficha("2028-01-06").especial)
        // También del avance de 2026 a 2027 y cuando la quinta se repite.
        assertEquals(8, ficha("2027-01-01").principal!!.numero); assertEquals(8, ficha("2033-01-01").principal!!.numero)
    }

    @Test fun bisiesto() {
        // 2028: la fiebre (10 a 12 de febrero) no cambia; un día que empieza el 28 de febrero no se alarga al 29.
        val cal = Calendario("cantero", CAL.estaciones, CAL.clima, CAL.luz, (1..5).map { TemporadaVida("a", "paz", "", listOf(dia("visita", "02-28", TipoDia.SUCESO), dia("quincena", "02-20", TipoDia.RITO, duracion = 15))) })
        assertTrue(ficha("2028-02-28", cal).especiales.any { it.dia.id == "visita" })
        assertFalse(ficha("2028-02-29", cal).especiales.any { it.dia.id == "visita" })
        // Las duraciones cuentan días reales: 15 días desde el 20 de febrero acaban el 5 de marzo en bisiesto y el 6 si no.
        assertTrue(ficha("2028-03-05", cal).especial); assertFalse(ficha("2028-03-06", cal).especial)
        assertTrue(ficha("2027-03-06", cal).especial); assertFalse(ficha("2027-03-07", cal).especial)
        // Y el 29 de febrero tiene ficha como cualquier día.
        val f = ficha("2028-02-29")
        assertEquals(Estacion.INVIERNO, f.estacion); assertEquals(ficha("2028-02-28").arcos, f.arcos)
    }

    @Test fun elClimaDeUnDiaEspecialManda() {
        (2026..2035).forEach { assertEquals(Clima.TORMENTA, ficha("$it-09-01").clima) }
    }

    // ---------------------------------------------------------------- La quinta se repite: los sucesos se sortean de nuevo

    @Test fun hasta2031NadaSeMueve() {
        for (anio in 2026..2031) FichaDelDia.inicios(CAL, anio).forEach { (d, inicio) -> assertEquals(MesDia.enAnio(d.fecha, anio), inicio) }
    }

    @Test fun desde2032LosSucesosSeSorteanDeNuevo() {
        val movidos = HashSet<Long>()
        for (anio in 2032..2060) {
            val inicios = FichaDelDia.inicios(CAL, anio).associate { it.first.id to it.second }
            assertEquals(inicios, FichaDelDia.inicios(CAL, anio).associate { it.first.id to it.second })   // determinista
            // Fiestas, cosechas y guerra: en su fecha.
            listOf("pascua" to "04-05", "siega" to "07-10", "saqueo" to "09-01", "navidad" to "12-25").forEach { (id, f) -> assertEquals(MesDia.enAnio(f, anio), inicios[id]) }
            // Los sucesos: hasta 45 días antes o después, dentro del año.
            listOf("fiebre" to "02-10", "alivio" to "02-20", "feria" to "07-20").forEach { (id, f) ->
                val n = java.time.temporal.ChronoUnit.DAYS.between(MesDia.enAnio(f, anio), inicios[id])
                assertTrue("$id en $anio: $n", abs(n) <= FichaDelDia.MARGEN_SORTEO); assertEquals(anio, inicios[id]!!.year)
                if (id == "feria") movidos += n
            }
            // La cadena se mueve entera: el alivio sigue 10 días después de la fiebre.
            assertEquals(10, java.time.temporal.ChronoUnit.DAYS.between(inicios["fiebre"], inicios["alivio"]))
            // Y la ficha lo ve en su fecha nueva.
            assertEquals("feria", ficha(inicios["feria"].toString()).principal!!.dia.id)
        }
        assertTrue("la feria casi no se mueve: $movidos", movidos.size >= 15)
    }

    @Test fun elSorteoNoSeSaleDelAnio() {
        val cal = Calendario("cantero", CAL.estaciones, CAL.clima, CAL.luz, (1..5).map {
            TemporadaVida("a", "paz", "", listOf(dia("primero", "01-03", TipoDia.SUCESO, cadena = "larga"), dia("ultimo", "12-20", TipoDia.SUCESO, cadena = "larga"), dia("fin", "12-31", TipoDia.SUCESO)))
        })
        for (anio in 2032..2080) FichaDelDia.inicios(cal, anio).forEach { (d, inicio) -> assertEquals("${d.id} en $anio", anio, inicio.year) }
    }

    // ---------------------------------------------------------------- Arcos

    @Test fun losArcosAvanzanEnLineaRecta() {
        val obra = CAL.arcos.first { it.id == "obra" }
        assertEquals(0.10, obra.valor(1, LocalDate.of(2027, 1, 1)), 1e-12)
        assertEquals(0.40, obra.valor(3, LocalDate.of(2029, 6, 30)), 1e-12)
        assertEquals(0.60, obra.valor(5, LocalDate.of(2031, 12, 31)), 1e-12)
        // A la mitad del primer tramo (910 días entre los dos puntos): 0.10 + 0.30 × 455 / 910 = 0.25.
        assertEquals(0.25, obra.valor(455), 1e-12)
        // Antes del primer punto y después del último, el valor del extremo.
        assertEquals(0.10, obra.valor(-50), 1e-12); assertEquals(0.60, obra.valor(5 * 365 + 50), 1e-12)
    }

    @Test fun losSaltosCambianDeGolpe() {
        assertEquals(0.0, ficha("2028-08-31").arcos["casa"]!!, 1e-12)
        assertEquals(1.0, ficha("2028-09-01").arcos["casa"]!!, 1e-12)        // se quema
        assertEquals(0.0, ficha("2027-09-01").arcos["casa"]!!, 1e-12)        // un año antes, intacta
        assertTrue(ficha("2029-01-15").arcos["casa"]!! in 0.4..0.6)           // a medio reconstruir
        assertEquals(0.0, ficha("2029-06-01").arcos["casa"]!!, 1e-12); assertEquals(0.0, ficha("2031-06-01").arcos["casa"]!!, 1e-12)
        // El trigo: lleno hasta la víspera de la siega y a cero ese día.
        assertEquals(1.0, ficha("2027-07-09").arcos["trigo"]!!, 1e-12); assertEquals(0.0, ficha("2027-07-10").arcos["trigo"]!!, 1e-12)
        assertEquals(0.0, ficha("2027-10-15").arcos["trigo"]!!, 1e-12); assertTrue(ficha("2027-10-16").arcos["trigo"]!! > 0.0)
    }

    /** De una temporada a la siguiente los arcos empalman: de un día al otro nunca hay un brinco, salvo los saltos declarados. */
    @Test fun losArcosEmpalmanEntreTemporadas() {
        val saltos = setOf("07-10", "09-01")
        val todos = dias("2027-01-01", "2031-12-31")
        todos.zipWithNext().forEach { (a, b) ->
            val antes = ficha(a.toString()).arcos; val despues = ficha(b.toString()).arcos
            assertEquals(setOf("obra", "trigo", "casa"), despues.keys)
            despues.forEach { (id, v) ->
                assertTrue("$id el $b: $v", v in 0.0..1.0)
                val brinco = abs(v - antes[id]!!)
                val declarado = b.toString().substring(5) in saltos && id != "obra"
                if (!declarado) assertTrue("$id brinca $brinco del $a al $b", brinco <= 0.005)
            }
        }
        // En cada cambio de temporada, uno por uno.
        for (anio in 2027..2030) {
            val fin = ficha("$anio-12-31").arcos; val inicio = ficha("${anio + 1}-01-01").arcos
            fin.forEach { (id, v) -> assertEquals("$id de $anio a ${anio + 1}", v, inicio[id]!!, 0.005) }
            // El trigo sembrado en octubre sigue creciendo en enero: no vuelve a cero.
            assertTrue(inicio["trigo"]!! > fin["trigo"]!! && fin["trigo"]!! > 0.25)
        }
        // También en los bordes de las cinco: el trigo de enero de la primera y de diciembre de la quinta.
        assertEquals(ficha("2028-01-01").arcos["trigo"]!!, ficha("2027-01-01").arcos["trigo"]!!, 1e-12)
        assertEquals(ficha("2027-12-31").arcos["trigo"]!!, ficha("2031-12-31").arcos["trigo"]!!, 1e-12)
    }

    @Test fun elAvanceYLaRepeticionUsanSuTemporada() {
        // 2026 muestra la temporada 1 y 2032 en adelante repite la 5, con los mismos valores en la misma fecha.
        listOf("03-15", "07-10", "11-30").forEach { f ->
            assertEquals(ficha("2027-$f").arcos, ficha("2026-$f").arcos)
            assertEquals(ficha("2031-$f").arcos, ficha("2032-$f").arcos); assertEquals(ficha("2031-$f").arcos, ficha("2050-$f").arcos)
        }
    }

    // ---------------------------------------------------------------- Estación y clima

    @Test fun estacionDelLugar() {
        assertEquals(Estacion.INVIERNO, ficha("2027-01-01").estacion); assertEquals(Estacion.INVIERNO, ficha("2027-03-19").estacion)
        assertEquals(Estacion.PRIMAVERA, ficha("2027-03-20").estacion); assertEquals(Estacion.VERANO, ficha("2027-06-21").estacion)
        assertEquals(Estacion.OTONO, ficha("2027-09-22").estacion); assertEquals(Estacion.OTONO, ficha("2027-12-20").estacion)
        assertEquals(Estacion.INVIERNO, ficha("2027-12-21").estacion)
        val sur = Calendario("cantero", Calendario.TEMPLADO_SUR, CAL.clima, CAL.luz, CAL.temporadas)
        assertEquals(Estacion.VERANO, ficha("2027-01-01", sur).estacion); assertEquals(Estacion.INVIERNO, ficha("2027-07-01", sur).estacion)
        val sin = Calendario("cantero", Calendario.SIN_ESTACIONES, Calendario.climaFijo(Clima.SIN_CLIMA), RegimenLuz.DOS_ESTADOS, CAL.temporadas)
        dias("2027-01-01", "2027-12-31").forEach { d -> val f = ficha(d.toString(), sin); assertEquals(Estacion.NINGUNA, f.estacion); if (d.toString() != "2027-09-01") assertEquals(Clima.SIN_CLIMA, f.clima) }
    }

    @Test fun elClimaSigueLaTablaDelMes() {
        // Muchos eneros (pesos 50, 30 y 20) y muchos julios (70, 20 y 10): las frecuencias se parecen a los pesos.
        val enero = (2000..2399).flatMap { a -> (1..31).map { ficha(LocalDate.of(a, 1, it).toString()).clima } }
        val julio = (2000..2399).flatMap { a -> (1..9).map { ficha(LocalDate.of(a, 7, it).toString()).clima } }
        fun parte(l: List<Clima>, c: Clima) = l.count { it == c } / l.size.toDouble()
        assertEquals(setOf(Clima.NUBES, Clima.LLUVIA, Clima.NIEVE), enero.toSet()); assertEquals(setOf(Clima.DESPEJADO, Clima.NUBES, Clima.TORMENTA), julio.toSet())
        assertEquals(0.50, parte(enero, Clima.NUBES), 0.02); assertEquals(0.30, parte(enero, Clima.LLUVIA), 0.02); assertEquals(0.20, parte(enero, Clima.NIEVE), 0.02)
        assertEquals(0.70, parte(julio, Clima.DESPEJADO), 0.03); assertEquals(0.20, parte(julio, Clima.NUBES), 0.03); assertEquals(0.10, parte(julio, Clima.TORMENTA), 0.03)
        // El orden en que se escribe la tabla no cambia el sorteo.
        val alReves = Calendario("cantero", CAL.estaciones, CAL.clima.map { m -> m.entries.reversed().associate { it.key to it.value } }, CAL.luz, CAL.temporadas)
        dias("2027-01-01", "2027-12-31").forEach { assertEquals(ficha(it.toString()).clima, ficha(it.toString(), alReves).clima) }
    }

    // ---------------------------------------------------------------- Luz

    @Test fun seisFranjas() {
        fun f(h: Double) = luzDe(h, RegimenLuz.SEIS_FRANJAS).franja
        assertEquals(listOf(Franja.NOCHE, Franja.NOCHE, Franja.AMANECER, Franja.AMANECER, Franja.MANANA, Franja.MANANA, Franja.MEDIODIA, Franja.MEDIODIA, Franja.TARDE,
            Franja.TARDE, Franja.ATARDECER, Franja.ATARDECER, Franja.NOCHE, Franja.NOCHE),
            listOf(0.0, 4.99, 5.0, 7.99, 8.0, 11.99, 12.0, 15.99, 16.0, 18.99, 19.0, 20.99, 21.0, 23.99).map { f(it) })
        // Las horas de las capturas del plan (7, 13 y 21 h) caen en tres franjas distintas.
        assertEquals(listOf(Franja.AMANECER, Franja.MEDIODIA, Franja.NOCHE), listOf(7.0, 13.0, 21.0).map { f(it) })
        // El avance va de 0 a 1 dentro de la franja, también cuando la noche cruza la medianoche.
        assertEquals(0.0, luzDe(5.0, RegimenLuz.SEIS_FRANJAS).avance, 1e-12); assertEquals(0.5, luzDe(6.5, RegimenLuz.SEIS_FRANJAS).avance, 1e-12)
        assertEquals(0.0, luzDe(21.0, RegimenLuz.SEIS_FRANJAS).avance, 1e-12); assertEquals(0.5, luzDe(1.0, RegimenLuz.SEIS_FRANJAS).avance, 1e-12)
        (0 until 240).forEach { val l = luzDe(it / 10.0, RegimenLuz.SEIS_FRANJAS); assertTrue(l.avance >= 0.0 && l.avance < 1.0) }
        // Fuera de 0 a 24 da la vuelta.
        assertEquals(luzDe(1.5, RegimenLuz.SEIS_FRANJAS), luzDe(25.5, RegimenLuz.SEIS_FRANJAS)); assertEquals(luzDe(23.0, RegimenLuz.SEIS_FRANJAS), luzDe(-1.0, RegimenLuz.SEIS_FRANJAS))
    }

    @Test fun siempreDeNocheYDosEstados() {
        (0 until 96).forEach { q ->
            val h = q / 4.0
            assertEquals(Franja.NOCHE, luzDe(h, RegimenLuz.SIEMPRE_NOCHE).franja)
            assertEquals(if (h >= 5 && h < 21) Franja.ENCENDIDO else Franja.TENUE, luzDe(h, RegimenLuz.DOS_ESTADOS).franja)
            // Lo oscuro coincide en los tres regímenes: de 21 a 5.
            assertEquals(h < 5 || h >= 21, luzDe(h, RegimenLuz.SEIS_FRANJAS).franja.oscura)
            assertEquals(h < 5 || h >= 21, luzDe(h, RegimenLuz.DOS_ESTADOS).franja.oscura)
        }
        assertEquals(0.0, luzDe(5.0, RegimenLuz.SIEMPRE_NOCHE).avance, 1e-12); assertEquals(0.5, luzDe(17.0, RegimenLuz.SIEMPRE_NOCHE).avance, 1e-12)
        assertEquals(0.5, luzDe(13.0, RegimenLuz.DOS_ESTADOS).avance, 1e-12)
    }

    // ---------------------------------------------------------------- Los párrafos y el objeto

    @Test fun ningunaCombinacionSeRepiteEnDosAniosSeguidos() {
        for (anio in 2026..2034) {
            val este = dias("$anio-01-01", "$anio-12-31").map { ficha(it.toString()).piezas(TAMANOS) }
            val siguiente = dias("${anio + 1}-01-01", "${anio + 1}-12-31").map { ficha(it.toString()).piezas(TAMANOS) }
            assertEquals("en $anio", este.size, este.toSet().size)
            assertEquals("de $anio a ${anio + 1}", este.size + siguiente.size, (este + siguiente).toSet().size)
            este.forEach { fila -> assertEquals(8, fila.size); fila.forEachIndexed { i, v -> assertTrue(v in 0 until TAMANOS[i]) } }
        }
    }

    @Test fun ningunParrafoSeRepiteDosDiasSeguidos() {
        for (anio in 2027..2031) {
            val filas = dias("$anio-01-01", "$anio-12-31").map { ficha(it.toString()).piezas(TAMANOS) }
            filas.zipWithNext().forEachIndexed { d, (a, b) -> a.indices.forEach { i -> assertNotEquals("lugar $i, día $d de $anio", a[i], b[i]) } }
            // Y cada variante sale casi las mismas veces: con 6 variantes, unas 61 al año cada una.
            TAMANOS.indices.forEach { i ->
                val cuentas = filas.groupingBy { it[i] }.eachCount()
                assertEquals(TAMANOS[i], cuentas.size)
                cuentas.values.forEach { assertTrue("lugar $i en $anio: $cuentas", abs(it - filas.size / TAMANOS[i].toDouble()) <= 3) }
            }
        }
    }

    @Test fun conLasCombinacionesJustasTampocoSeRepite() {
        // 3 × 6 × 61 = 1,098 combinaciones: justo las de tres años. Un año impar no repite ninguna suya ni de sus vecinos.
        val justas = listOf(3, 6, 61)
        val (a26, a27, a28) = (2026..2028).map { a -> dias("$a-01-01", "$a-12-31").map { ficha(it.toString()).piezas(justas) } }
        assertEquals(365 + 365, (a26 + a27).toSet().size); assertEquals(365 + 366, (a27 + a28).toSet().size)
        // 2 × 3 × 61 = 366: justo un año bisiesto par.
        val unAnio = dias("2028-01-01", "2028-12-31").map { ficha(it.toString()).piezas(listOf(2, 3, 61)) }
        assertEquals(366, unAnio.toSet().size)
        // Con menos combinaciones que días (aquí, 8) no se puede evitar que se repitan, pero no falla: salen las que hay.
        val pocas = dias("2028-01-01", "2028-12-31").map { ficha(it.toString()).piezas(listOf(2, 2, 2, 1)) }
        assertEquals(8, pocas.toSet().size); pocas.forEach { f -> assertEquals(0, f[3]) }
        dias("2027-01-01", "2027-12-31").forEach { d -> ficha(d.toString()).piezas(listOf(2, 2, 2, 1)).forEachIndexed { i, v -> assertTrue(v in 0 until listOf(2, 2, 2, 1)[i]) } }
    }

    @Test fun elObjetoDelDia() {
        for (anio in 2026..2033) {
            val objetos = dias("$anio-01-01", "$anio-12-31").map { ficha(it.toString()).objeto!! }
            assertEquals(CAL.objetos.toSet(), objetos.toSet())
            objetos.zipWithNext().forEach { (a, b) -> assertNotEquals(a, b) }
            objetos.groupingBy { it }.eachCount().values.forEach { assertTrue(it in 72..74) }
        }
        assertNull(ficha("2027-05-10", Calendario("cantero", CAL.estaciones, CAL.clima, CAL.luz, CAL.temporadas)).objeto)
    }

    @Test fun laBarajaReparteParejo() {
        val b = Barajas.baraja(7, 700, Azar(42))
        assertEquals(List(7) { 100 }, (0 until 7).map { v -> b.count { it == v } })
        b.toList().zipWithNext().forEach { (x, y) -> assertNotEquals(x, y) }
        assertEquals(List(10) { 0 }, Barajas.baraja(1, 10, Azar(1)).toList())
        (0 until 50).forEach { s -> Barajas.baraja(2, 40, Azar(s.toLong())).toList().zipWithNext().forEach { (x, y) -> assertNotEquals(x, y) } }
    }

    // ---------------------------------------------------------------- La revisión de un calendario

    @Test fun elCalendarioDePruebaEstaBien() = assertEquals(emptyList<String>(), CAL.problemas())

    @Test fun laRevisionEncuentraCadaError() {
        fun con(dias: List<DiaEspecial>, vida: String = "cantero", arcos: List<Arco> = emptyList(), objetos: List<String> = emptyList()) =
            Calendario(vida, CAL.estaciones, CAL.clima, CAL.luz, (1..5).map { TemporadaVida("a", "paz", "", dias) }, arcos, objetos).problemas()
        fun falla(texto: String, problemas: List<String>) = assertTrue("se esperaba «$texto» en $problemas", problemas.any { texto in it })
        val f = "Fuente" to "https://ejemplo.org/x"

        assertEquals(emptyList<String>(), con(listOf(DiaEspecial("a", "03-01", 1, TipoDia.FIESTA, "A", "", CertezaDia.HISTORICO, f), DiaEspecial("b", "03-02", 1, TipoDia.RITO, "B", "", CertezaDia.APROXIMADO))))
        falla("No existe la vida", con(emptyList(), vida = "nadie"))
        falla("se esperaba MM-DD", con(listOf(dia("a", "02-29", TipoDia.FIESTA))))
        falla("se esperaba MM-DD", con(listOf(dia("a", "3-1", TipoDia.FIESTA))))
        falla("duración 0", con(listOf(dia("a", "03-01", TipoDia.FIESTA, duracion = 0))))
        falla("duración 366", con(listOf(dia("a", "03-01", TipoDia.FIESTA, duracion = 366))))
        falla("está 2 veces", con(listOf(dia("a", "03-01", TipoDia.FIESTA), dia("a", "04-01", TipoDia.FIESTA))))
        falla("el id debe ser una clave", con(listOf(dia("Día A", "03-01", TipoDia.FIESTA))))
        falla("falta el título", con(listOf(DiaEspecial("a", "03-01", 1, TipoDia.FIESTA, " ", "", CertezaDia.INVENTADO))))
        falla("«escena» debe ser una clave", con(listOf(dia("a", "03-01", TipoDia.FIESTA, escena = "Con Velas"))))
        falla("lo inventado no lleva fuente", con(listOf(DiaEspecial("a", "03-01", 1, TipoDia.FIESTA, "A", "", CertezaDia.INVENTADO, f))))
        falla("enlace https", con(listOf(DiaEspecial("a", "03-01", 1, TipoDia.FIESTA, "A", "", CertezaDia.HISTORICO, "Fuente" to "ejemplo.org"))))
        // Kei es de ficción: su calendario es todo inventado.
        falla("personaje de ficción", con(listOf(DiaEspecial("a", "03-01", 1, TipoDia.FIESTA, "A", "", CertezaDia.HISTORICO)), vida = "repartidora"))
        assertEquals(emptyList<String>(), con(listOf(dia("a", "03-01", TipoDia.FIESTA)), vida = "repartidora"))
        falla("mezcla sucesos", con(listOf(dia("a", "03-01", TipoDia.SUCESO, cadena = "c"), dia("b", "03-05", TipoDia.FIESTA, cadena = "c"))))

        falla("no tiene puntos", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, emptyList()))))
        falla("en orden de fecha", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(2, "01-01", 0.1), PuntoArco(1, "06-01", 0.2))))))
        falla("en orden de fecha", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 0.1), PuntoArco(1, "01-01", 0.2))))))
        falla("debe ir de 0 a 1", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 1.2))))))
        falla("debe ir de 0 a 6", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(7, "01-01", 0.2))))))
        falla("no puede ser un salto", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 0.2, salto = true))))))
        falla("está 2 veces", con(emptyList(), arcos = listOf(Arco("x", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 0.2))), Arco("x", TipoArco.OBRA, listOf(PuntoArco(1, "01-01", 0.2))))))
        falla("objetos repetidos", con(emptyList(), objetos = listOf("mazo", "mazo")))
        falla("Objeto «El mazo»", con(emptyList(), objetos = listOf("El mazo")))

        fun entero(estaciones: List<Pair<String, Estacion>> = CAL.estaciones, clima: List<Map<Clima, Int>> = CAL.clima, temporadas: List<TemporadaVida> = CAL.temporadas) =
            Calendario("cantero", estaciones, clima, CAL.luz, temporadas).problemas()
        falla("Faltan las estaciones", entero(estaciones = emptyList()))
        falla("Dos estaciones", entero(estaciones = listOf("03-20" to Estacion.PRIMAVERA, "03-20" to Estacion.VERANO)))
        falla("12 tablas", entero(clima = CAL.clima.take(11)))
        falla("suman 0", entero(clima = CAL.clima.take(11) + mapOf(Clima.LLUVIA to 0)))
        falla("peso negativo", entero(clima = CAL.clima.take(11) + mapOf(Clima.LLUVIA to -1, Clima.NUBES to 5)))
        falla("5 temporadas", entero(temporadas = CAL.temporadas.take(4)))
        falla("falta «cuando»", entero(temporadas = CAL.temporadas.take(4) + TemporadaVida("", "paz", "", emptyList())))
        falla("«mundo» debe ser una clave", entero(temporadas = CAL.temporadas.take(4) + TemporadaVida("a", "En guerra", "", emptyList())))
    }

    companion object {
        /** Ocho lugares con 6 a 8 variantes, como pide el plan para el cuento. */
        val TAMANOS = listOf(6, 6, 7, 6, 8, 6, 7, 6)
        const val SEMILLA_FIJA = 2419565751494628941L
        val PIEZAS_FIJAS = listOf(5, 0, 4, 4, 0, 4, 1, 4)
    }
}
