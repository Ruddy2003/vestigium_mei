package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.AnioPiloto
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * Los calendarios del piloto (Demeas, Pierre, Kei y Amaru): que estén bien escritos, que cada día diga qué tan cierto
 * es y de dónde sale, y que la historia de los cinco años sea continua. Las pruebas generales ([todos]) valen también
 * para los calendarios que agreguen las tandas A: basta con registrarlos en [Calendarios].
 */
class AnioPilotoPrueba {
    private val todos = Calendarios.TODOS
    private fun vida(c: Calendario) = OtrasVidas.porId(c.vida)!!
    private fun dias(c: Calendario) = c.temporadas.flatMap { it.dias }

    /** Todas las fichas de un personaje de 2027 a 2031, día por día. */
    private fun fichas(c: Calendario): List<FichaDelDia> =
        generateSequence(LocalDate.of(2027, 1, 1)) { it.plusDays(1) }.takeWhile { it.year <= 2031 }.map { FichaDelDia.de(it.toString(), vida(c), c) }.toList()

    // ---------- Forma ----------

    @Test fun estanLosCuatroYEstanBienEscritos() {
        assertEquals(listOf("remero", "cantero", "repartidora", "fideos"), AnioPiloto.TODOS.map { it.vida })
        todos.forEach { c -> assertEquals("Calendario de ${c.vida}", emptyList<String>(), c.problemas()) }
        assertEquals("Un calendario por personaje", todos.size, todos.map { it.vida }.toSet().size)
    }

    @Test fun cadaTemporadaTieneUnosVeinteDiasYNoTodosSonFiestas() {
        todos.forEach { c ->
            c.temporadas.forEachIndexed { i, t ->
                val donde = "${c.vida}, temporada ${i + 1}"
                assertTrue("$donde: ${t.dias.size} días especiales", t.dias.size in 16..26)
                assertTrue("$donde: faltan sucesos de su vida", t.dias.count { it.tipo == TipoDia.SUCESO } >= 3)
                assertTrue("$donde: demasiadas fiestas", t.dias.count { it.tipo == TipoDia.FIESTA } <= t.dias.size * 2 / 3)
                assertTrue("$donde: falta el resumen", t.resumen.length > 40)
            }
        }
    }

    @Test fun ningunaTemporadaPasaDosMesesSinUnDiaEspecial() {
        todos.forEach { c ->
            var racha = 0; var peor = 0
            fichas(c).forEach { f -> racha = if (f.especial) 0 else racha + 1; peor = maxOf(peor, racha) }
            assertTrue("${c.vida}: $peor días seguidos sin nada especial", peor <= 60)
        }
    }

    @Test fun losTitulosNoLlevanAniosNiPuntoFinal() {
        val anio = Regex("""\b(1[0-9]{3}|20[0-9]{2})\b""")
        todos.forEach { c -> dias(c).forEach { d ->
            assertTrue("${c.vida}, «${d.titulo}»: lleva un año", !anio.containsMatchIn(d.titulo))
            assertTrue("${c.vida}, «${d.titulo}»: termina en punto", !d.titulo.endsWith("."))
            assertTrue("${c.vida}, «${d.titulo}»: muy largo para la cabecera", d.titulo.length <= 90)
        } }
    }

    // ---------- Certeza y fuentes ----------

    @Test fun loHistoricoLlevaFuenteOSaleDeUnCalculo() {
        todos.forEach { c -> dias(c).filter { it.certeza == CertezaDia.HISTORICO }.forEach { d ->
            assertTrue("${c.vida}, «${d.id}»: histórico sin fuente", d.fuente != null || d.id in AnioPiloto.CALCULADOS)
        } }
        // Y al revés: lo calculado existe y es histórico.
        AnioPiloto.CALCULADOS.forEach { id -> assertTrue("«$id» no está en ningún calendario", todos.any { c -> dias(c).any { it.id == id && it.certeza == CertezaDia.HISTORICO } }) }
    }

    @Test fun lasFuentesSonDeWikipediaYLosSucesosDeSuVidaSonInventados() {
        val enlace = Regex("""https://(en|es|fr)\.wikipedia\.org/wiki/\S+""")
        todos.forEach { c -> dias(c).forEach { d ->
            d.fuente?.let { assertTrue("${c.vida}, «${d.id}»: enlace «${it.second}»", enlace.matches(it.second)) }
            if (d.tipo == TipoDia.SUCESO) assertEquals("${c.vida}, «${d.id}»", CertezaDia.INVENTADO, d.certeza)
        } }
    }

    @Test fun enLaFiccionTodoEsInventadoYEnLoRealHayHistoria() {
        todos.forEach { c ->
            val q = vida(c)
            if (q.ficticia) assertTrue("${c.vida}: es ficción", dias(c).all { it.certeza == CertezaDia.INVENTADO && it.fuente == null })
            else c.temporadas.forEachIndexed { i, t ->
                assertTrue("${c.vida}, temporada ${i + 1}: sin nada con fuente", t.dias.count { it.fuente != null } >= 5)
                assertTrue("${c.vida}, temporada ${i + 1}: sin un hecho de su mundo", t.dias.any { it.tipo == TipoDia.GUERRA || it.tipo == TipoDia.MUNDO })
            }
        }
    }

    // ---------- Continuidad ----------

    @Test fun unMismoIdEsElMismoDiaEnTodasLasTemporadas() {
        // Una fiesta que vuelve comparte su texto: no puede cambiar de tipo ni de certeza de un año a otro.
        todos.forEach { c -> dias(c).groupBy { it.id }.forEach { (id, l) ->
            assertEquals("${c.vida}, «$id»: cambia de tipo", 1, l.map { it.tipo }.toSet().size)
            assertEquals("${c.vida}, «$id»: cambia de certeza", 1, l.map { it.certeza }.toSet().size)
        } }
    }

    @Test fun losArcosEmpalmanDeUnAnioAlSiguiente() {
        todos.forEach { c ->
            val f = fichas(c)
            c.arcos.forEach { arco ->
                val saltos = arco.puntos.filter { it.salto }.map { it.lugar }.toSet()
                f.zipWithNext().forEach { (ayer, hoy) ->
                    val d = LocalDate.parse(hoy.dia)
                    val lugar = (hoy.temporada - 1) * 365 + MesDia.indice(d)
                    val cambio = abs(hoy.arcos.getValue(arco.id) - ayer.arcos.getValue(arco.id))
                    val tope = if (d.dayOfYear == 1) 0.03 else 0.3
                    assertTrue("${c.vida}, arco «${arco.id}», ${hoy.dia}: brinca $cambio", cambio <= tope || lugar in saltos)
                }
            }
        }
    }

    @Test fun lasCadenasVanEnOrdenYCabenEnElAnio() {
        todos.forEach { c -> c.temporadas.forEachIndexed { i, t ->
            t.dias.filter { it.cadena != null }.groupBy { it.cadena }.forEach { (cadena, l) ->
                assertTrue("${c.vida}, temporada ${i + 1}: la cadena «$cadena» tiene un solo día", l.size >= 2)
                val lugares = l.map { MesDia.indice(it.fecha) }
                assertEquals("${c.vida}, temporada ${i + 1}: la cadena «$cadena» está desordenada", lugares.sorted(), lugares)
                // Un eslabón no empieza antes de que termine el anterior.
                l.zipWithNext().forEach { (a, b) -> assertTrue("${c.vida}: «${a.id}» se encima con «${b.id}»", MesDia.indice(b.fecha) >= MesDia.indice(a.fecha) + a.duracion) }
            }
        } }
    }

    @Test fun enLaTemporadaCincoLosSucesosAguantanMoverse() {
        // Desde 2032 cada suceso personal se mueve hasta 45 días. No debe quedar el mismo día que otro suceso de un
        // solo día en ninguna de las diez primeras repeticiones, ni salirse del año.
        todos.forEach { c -> (2032..2041).forEach { anio ->
            val inicios = FichaDelDia.inicios(c, anio).filter { it.first.tipo == TipoDia.SUCESO }
            inicios.forEach { (d, inicio) -> assertEquals("${c.vida}, «${d.id}» en $anio", anio, inicio.plusDays(d.duracion - 1L).year) }
            inicios.groupBy { it.second }.forEach { (dia, l) -> assertTrue("${c.vida}, $dia: coinciden ${l.map { it.first.id }}", l.map { it.first.cadena ?: it.first.id }.toSet().size == l.size) }
        } }
    }

    @Test fun keiYAmaruVivenEnLaMismaCiudad() {
        val kei = AnioPiloto.KEI; val amaru = AnioPiloto.AMARU
        (0 until Temporadas.CUANTAS).forEach { i ->
            fun delMundo(c: Calendario) = c.temporadas[i].dias.filter { it.tipo != TipoDia.SUCESO }.map { Triple(it.id, it.fecha, it.duracion) }.toSet()
            assertEquals("Temporada ${i + 1}: la ciudad no es la misma", delMundo(kei), delMundo(amaru))
            assertEquals(kei.temporadas[i].mundo, amaru.temporadas[i].mundo)
            assertEquals(kei.temporadas[i].cuando, amaru.temporadas[i].cuando)
        }
        // Los arcos de la ciudad valen lo mismo el mismo día.
        val comunes = amaru.arcos.map { it.id }
        listOf("2027-07-21", "2029-03-10", "2030-09-02", "2031-12-31").forEach { dia ->
            val a = FichaDelDia.de(dia, vida(amaru), amaru); val k = FichaDelDia.de(dia, vida(kei), kei)
            comunes.forEach { assertEquals("$dia, arco «$it»", a.arcos.getValue(it), k.arcos.getValue(it), 0.0) }
            assertEquals("$dia: clima distinto en la misma ciudad no es un error, pero la estación sí", a.estacion, k.estacion)
        }
        // Se conocen: la noche en que él le dice su nombre es la misma en los dos calendarios.
        assertEquals(kei.temporadas[2].dias.first { it.id == "nombre_amaru" }.fecha, amaru.temporadas[2].dias.first { it.id == "nombre_kei" }.fecha)
        assertEquals(kei.temporadas[0].dias.first { it.id == "primeros_fideos" }.fecha, amaru.temporadas[0].dias.first { it.id == "doble_caldo" }.fecha)
    }

    // ---------- Lo que dice cada historia ----------

    @Test fun demeasVeElEclipseYLaPesteLlegaElSegundoAnio() {
        val c = AnioPiloto.DEMEAS; val q = vida(c)
        val eclipse = FichaDelDia.de("2027-08-03", q, c)
        assertEquals("eclipse", eclipse.principal!!.dia.id)
        assertEquals(CertezaDia.HISTORICO, eclipse.principal!!.dia.certeza)
        assertEquals(Clima.DESPEJADO, eclipse.clima)
        assertEquals(-431, eclipse.anioHistorico); assertEquals("431 a. C.", eclipse.cuando)
        assertEquals(0.0, FichaDelDia.de("2027-12-31", q, c).arcos.getValue("peste"), 0.0)
        assertTrue(FichaDelDia.de("2028-08-01", q, c).arcos.getValue("peste") > 0.9)
        assertEquals("peste", FichaDelDia.de("2028-08-01", q, c).mundo)
        // El día de fiesta manda sobre la invasión, que dura semanas, y la escena sigue mostrando la fiesta.
        val muniquias = FichaDelDia.de("2028-05-11", q, c)
        assertEquals(listOf("muniquias", "segunda_invasion"), muniquias.especiales.map { it.dia.id })
        assertEquals("fiesta", muniquias.escena)
        // Las fiestas áticas siguen la luna: cambian de fecha cada año.
        assertEquals(5, c.temporadas.map { t -> t.dias.first { it.id == "dionisias" }.fecha }.toSet().size)
        // Las Bendidias empiezan en la temporada 3.
        assertEquals(listOf(false, false, true, true, true), c.temporadas.map { t -> t.dias.any { it.id == "bendidias" } })
        // El segundo trirreme: sale un día después de la condena y llega dos días después.
        val t5 = c.temporadas[4].dias.associateBy { it.id }
        assertEquals(MesDia.indice(t5.getValue("condena").fecha) + 1, MesDia.indice(t5.getValue("segundo_trirreme").fecha))
        assertEquals(MesDia.indice(t5.getValue("segundo_trirreme").fecha) + 2, MesDia.indice(t5.getValue("llegan_a_tiempo").fecha))
    }

    @Test fun elCuadroDeDemeasPuedeContarLoMismoQueElCuento() {
        // Lo que el cuento dice y se ve está en la ficha, que es lo que lee la escena: dónde está él y dónde su nave.
        val c = AnioPiloto.DEMEAS; val q = vida(c)
        fun ficha(dia: String) = FichaDelDia.de(dia, q, c)
        // El «Aura» pasa el invierno en su cobertizo y se echa al agua el 12 de abril.
        assertEquals(0.0, ficha("2027-04-11").arcos.getValue("nave"), 0.0); assertEquals(1.0, ficha("2027-04-12").arcos.getValue("nave"), 0.0)
        assertEquals(1.0, ficha("2027-10-31").arcos.getValue("nave"), 0.0); assertEquals(0.0, ficha("2027-11-01").arcos.getValue("nave"), 0.0)
        // Zarpa el 5 de julio y vuelve el 18 de septiembre: entre tanto está en el «mar» y su nave no está en el puerto.
        assertEquals(null, ficha("2027-07-04").estado); assertEquals("mar", ficha("2027-07-05").estado)
        assertEquals("mar", ficha("2027-09-17").estado); assertEquals(null, ficha("2027-09-18").estado)
        assertEquals("partida", ficha("2027-07-05").escena); assertEquals("regreso", ficha("2027-09-18").escena)
        assertTrue(ficha("2027-08-01").arcos.getValue("flota") < 0.5); assertEquals(1.0, ficha("2027-09-18").arcos.getValue("flota"), 0.0)
        // Los otros viajes: Epidauro, las cien naves (diez días) y el segundo trirreme (dos días).
        assertEquals("mar", ficha("2028-06-20").estado); assertEquals(null, ficha("2028-07-10").estado)
        assertEquals(null, ficha("2030-08-21").estado); assertEquals("mar", ficha("2030-08-22").estado); assertEquals("mar", ficha("2030-08-31").estado); assertEquals(null, ficha("2030-09-01").estado)
        assertEquals("mar", ficha("2031-07-15").estado); assertEquals("mar", ficha("2031-07-16").estado); assertEquals(null, ficha("2031-07-17").estado)
        assertEquals(null, ficha("2029-07-20").estado)
        // Cuando la temporada 5 se repite, el estado sigue a sus días aunque sean de guerra y no se muevan.
        assertEquals("mar", ficha("2034-07-15").estado)
        // Los demás personajes del piloto también tienen los suyos: Pierre herido tras la caída y Kei sin bici.
        fun de(c2: Calendario, dia: String) = FichaDelDia.de(dia, vida(c2), c2).estado
        assertEquals(null, de(AnioPiloto.PIERRE, "2028-05-26")); assertEquals("herido", de(AnioPiloto.PIERRE, "2028-05-27"))
        assertEquals("herido", de(AnioPiloto.PIERRE, "2028-08-24")); assertEquals(null, de(AnioPiloto.PIERRE, "2028-08-25"))
        assertEquals("sin_bici", de(AnioPiloto.KEI, "2028-03-30")); assertEquals(null, de(AnioPiloto.KEI, "2028-04-08"))
        assertEquals("sin_implante", de(AnioPiloto.KEI, "2029-05-11")); assertEquals(null, de(AnioPiloto.AMARU, "2029-05-11"))
    }

    @Test fun laCatedralDePierreSubeYNuncaBaja() {
        val c = AnioPiloto.PIERRE
        val coro = fichas(c).map { it.arcos.getValue("coro") }
        assertTrue("El coro baja", coro.zipWithNext().all { (a, b) -> b >= a })
        assertTrue("El coro no sube en cinco años", coro.last() - coro.first() > 0.3)
        // En invierno no sube: del día de san Martín a marzo vale lo mismo.
        val q = vida(c)
        assertEquals(FichaDelDia.de("2028-11-11", q, c).arcos.getValue("coro"), FichaDelDia.de("2029-02-20", q, c).arcos.getValue("coro"), 1e-9)
        // El año en que se acaba el dinero la obra se para en septiembre.
        assertTrue(FichaDelDia.de("2027-09-02", q, c).arcos.getValue("andamios") > 0.9)
        assertTrue(FichaDelDia.de("2027-09-03", q, c).arcos.getValue("andamios") < 0.3)
        assertEquals("obra_parada", FichaDelDia.de("2027-09-03", q, c).escena)
        // La Pascua juliana de 1240 a 1244 y lo que cuelga de ella.
        assertEquals(listOf("04-15", "03-31", "04-20", "04-12", "04-03"), c.temporadas.map { t -> t.dias.first { it.id == "pascua" }.fecha })
        c.temporadas.forEach { t ->
            val pascua = MesDia.indice(t.dias.first { it.id == "pascua" }.fecha)
            assertEquals("Las rogativas van 36 días después de la Pascua", pascua + 36, MesDia.indice(t.dias.first { it.id == "rogativas" }.fecha))
        }
        // La Navidad cruza el año: el 3 de enero sigue siendo Navidad, y ya es la temporada siguiente.
        val enero = FichaDelDia.de("2028-01-03", q, c)
        assertEquals("navidad", enero.principal!!.dia.id); assertEquals(2, enero.temporada); assertEquals(10, enero.principal!!.numero)
    }

    @Test fun enLaCiudadDeNeonSiempreEsDeNoche() {
        listOf(AnioPiloto.KEI, AnioPiloto.AMARU).forEach { c ->
            val f = FichaDelDia.de("2027-06-01", vida(c), c)
            listOf(7.0, 13.0, 21.0).forEach { assertEquals(Franja.NOCHE, f.luz(it).franja) }
            assertEquals(Estacion.LLUVIAS, f.estacion)
            assertEquals(Estacion.SECAS, FichaDelDia.de("2027-01-20", vida(c), c).estacion)
        }
    }

    // ---------- El motor con calendarios de verdad ----------

    @Test fun laFichaQueSeRecuerdaEsLaMisma() {
        todos.forEach { c ->
            val directa = FichaDelDia.de("2029-07-15", vida(c), c)
            val recordada = FichaDelDia.de("2029-07-15", c.vida)
            assertNotNull(recordada)
            assertEquals(directa, recordada)
            assertTrue("La segunda vez devuelve la misma", recordada === FichaDelDia.de("2029-07-15", c.vida))
        }
    }

    @Test fun cadaDiaTieneFichaObjetoYPiezas() {
        todos.forEach { c ->
            val f = fichas(c)
            assertEquals(1826, f.size)
            assertTrue("${c.vida}: faltan objetos", c.objetos.size >= 10)
            assertTrue(f.all { it.objeto != null && it.objeto in c.objetos })
            assertTrue("${c.vida}: el objeto se repite dos días seguidos", f.zipWithNext().none { (a, b) -> a.dia.take(4) == b.dia.take(4) && a.objeto == b.objeto })
            assertTrue("${c.vida}: valores de arco fuera de 0 a 1", f.all { x -> x.arcos.values.all { it in 0.0..1.0 } })
        }
    }
}
