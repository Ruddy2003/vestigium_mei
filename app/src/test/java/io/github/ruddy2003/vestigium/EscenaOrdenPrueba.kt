package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Cisterna
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Garza
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Nucleo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Pila
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosOrden
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro de Orden (tanda S18: Peñaseca, la ciudadela de la escriba) cuenta lo mismo que el cuento: dónde está, la Garza,
 * la cisterna y el códice con los cortes del cuento, lo encendido a cada hora, los postigos y la barra, la sombra de la
 * Garza que camina con el sol, la noche a dos luces y lo que cambia cada día señalado salen de la ficha del día, y aquí se
 * comparan con su calendario (`AnioG8.kt`) todos los días de 2026 a 2032.
 */
class EscenaOrdenPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "escriba")!!, hora)
    private fun cadaDia(hora: Double = 10.0, hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString(), hora)); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeLaEscriba() {
        assertEquals(listOf("escriba"), EscenaOrden.vidas)
        assertEquals("escriba", EscenaOrden.vida("2027-05-05"))
        assertEquals("orden", OtrasVidas.porId("escriba")!!.tema)
        assertTrue(EscenaOrden.tallaDelGato in 0.8f..1.8f)
    }

    /** Los tres estados en sus fechas: la expedición (49 días de 2028), el encierro (27 de 2029) y la herida (35 de 2030); ninguno desde 2031. */
    @Test fun estaDondeDiceElCalendario() {
        val dias = HashMap<Pair<Lugar, Int>, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "expedicion" -> Lugar.EXPEDICION; "encierro" -> Lugar.ENCIERRO; "herida" -> Lugar.HERIDA; null -> Lugar.PENASECA; else -> error("estado desconocido: ${m.ficha.estado}") }
            assertEquals(d.toString(), esperado, EscenaOrden.lugar(m))
            if (esperado != Lugar.PENASECA) dias[esperado to d.year] = (dias[esperado to d.year] ?: 0) + 1
        }
        assertEquals(mapOf((Lugar.EXPEDICION to 2028) to 49, (Lugar.ENCIERRO to 2029) to 27, (Lugar.HERIDA to 2030) to 35), dias)
        assertEquals(Lugar.EXPEDICION, EscenaOrden.lugar(momento("2028-04-26"))); assertEquals(Lugar.PENASECA, EscenaOrden.lugar(momento("2028-06-14")))
        assertEquals(Lugar.ENCIERRO, EscenaOrden.lugar(momento("2029-02-15"))); assertEquals(Lugar.PENASECA, EscenaOrden.lugar(momento("2029-03-14")))
        assertEquals(Lugar.HERIDA, EscenaOrden.lugar(momento("2030-04-28"))); assertEquals(Lugar.PENASECA, EscenaOrden.lugar(momento("2030-06-02")))
    }

    /** La Garza en cada fase, con los cortes del cuento (`ENTORNOS`: tendida hasta 0.15; cosiéndose después) y ausente en la expedición. */
    @Test fun laGarzaSigueAlArcoYNoEstaEnLaExpedicion() {
        val vistas = HashSet<Garza>(); var antes = -1f
        cadaDia { d, m ->
            val a = m.ficha.arcos["garza"]!!
            val g = EscenaOrden.garza(m); vistas += g
            val esperada = when {
                m.ficha.estado == "expedicion" -> Garza.AUSENTE
                hoyEs(m, "garza_vuela") -> Garza.VOLANDO
                a <= 0.15 -> Garza.CAIDA
                a < 1.0 -> Garza.COSIENDO
                else -> Garza.AMARRADA
            }
            assertEquals(d.toString(), esperada, g)
            val c = EscenaOrden.costura(m)
            assertTrue(d.toString(), c in 0f..1f)
            if (g == Garza.COSIENDO) { assertTrue(d.toString(), c >= antes); antes = c } else antes = -1f
        }
        assertEquals(Garza.entries.toSet(), vistas)
        assertEquals(Garza.AMARRADA, EscenaOrden.garza(momento("2027-05-05")))
        assertEquals(Garza.AUSENTE, EscenaOrden.garza(momento("2028-05-10")))
        assertEquals(Garza.CAIDA, EscenaOrden.garza(momento("2029-07-30"))); assertEquals(Garza.AMARRADA, EscenaOrden.garza(momento("2029-07-29")))
        assertEquals(Garza.CAIDA, EscenaOrden.garza(momento("2029-10-10"))); assertTrue(EscenaOrden.telaNueva(momento("2029-10-10"))); assertFalse(EscenaOrden.telaNueva(momento("2029-09-10")))
        assertEquals(Garza.COSIENDO, EscenaOrden.garza(momento("2030-01-01")))
        assertEquals(0.3f, EscenaOrden.costura(momento("2030-01-12")), 0.05f)
        assertEquals(0.7f, EscenaOrden.costura(momento("2030-05-30")), 0.05f)
        assertEquals(Garza.VOLANDO, EscenaOrden.garza(momento("2030-09-10"))); assertEquals(Garza.AMARRADA, EscenaOrden.garza(momento("2030-09-11")))
        // Amarrada, gira con el viento del día, siempre hacia la izquierda (no tapa los astros), y el mismo día da lo mismo.
        cadaDia { d, m -> val r = EscenaOrden.rumbo(m); assertTrue(d.toString(), r in -0.51f..0.01f); assertEquals(r, EscenaOrden.rumbo(momento(d.toString(), 16.0)), 0f) }
    }

    /** La cisterna con los tres tramos del cuento (cerrados, manda el primero) y su nivel en la escalera. */
    @Test fun laCisternaSigueAlArcoConLosTramosDelCuento() {
        val vistos = HashSet<Cisterna>()
        cadaDia { d, m ->
            val a = m.ficha.arcos["cisterna"]!!
            val tramo = EscenaOrden.tramoDeLaCisterna(m); vistos += tramo
            assertEquals(d.toString(), if (a <= 0.3) Cisterna.BAJA else if (a <= 0.7) Cisterna.MEDIA else Cisterna.LLENA, tramo)
            assertTrue(d.toString(), EscenaOrden.peldano(m) in 0..EscenaOrden.PELDANOS)
            if (m.ficha.temporada >= 5) assertNotEquals(d.toString(), Cisterna.LLENA, tramo)
        }
        assertEquals(Cisterna.entries.toSet(), vistos)
        // «a medias, y en su escalera el agua llega por el peldaño doce».
        assertEquals(12, EscenaOrden.peldano(Momento(momento("2027-05-05").ficha.let { it.copy(arcos = it.arcos + ("cisterna" to 0.5)) }, 10.0)))
        assertTrue(EscenaOrden.peldano(momento("2027-07-11")) > EscenaOrden.peldano(momento("2027-08-31")))
        assertTrue(EscenaOrden.canalesCorren(Momento(momento("2027-07-20").ficha.copy(clima = Clima.TORMENTA), 15.0)))
    }

    /** El núcleo: no está hasta que vuelve la Garza, apagado hasta que se enciende; en el encierro, con ella en el archivo; la pila crece con el códice. */
    @Test fun elNucleoYLaPilaDeHojasSiguenAlCodice() {
        cadaDia { d, m ->
            val n = EscenaOrden.nucleo(m)
            val esperado = when { m.ficha.temporada < 2 || (m.ficha.temporada == 2 && md(d) < "06-14") -> Nucleo.AUSENTE; m.ficha.temporada == 2 && md(d) < "11-05" -> Nucleo.APAGADO; else -> Nucleo.ENCENDIDO }
            assertEquals(d.toString(), esperado, n)
            assertEquals(d.toString(), n != Nucleo.AUSENTE && m.ficha.estado != "encierro", EscenaOrden.nucleoEnLaCamara(m))
            if (!EscenaOrden.nucleoEnLaCamara(m)) assertEquals(d.toString(), Pila.NINGUNA, EscenaOrden.pila(m))
            assertEquals(d.toString(), (m.ficha.arcos["codice"] ?: 0.0).toFloat(), EscenaOrden.codice(m), 1e-6f)
        }
        assertEquals(Pila.NINGUNA, EscenaOrden.pila(momento("2028-11-06")))                 // recién encendido, todavía sin leer
        assertEquals(Pila.MEDIA, EscenaOrden.pila(momento("2029-03-20")))                   // «con la mitad leída»
        assertEquals(Pila.ALTA, EscenaOrden.pila(momento("2031-09-01")))
        assertEquals(1f, EscenaOrden.ventanaDeLaCamara(momento("2029-05-01", 3.0)).verde, 0f)
        assertEquals(0f, EscenaOrden.ventanaDeLaCamara(momento("2029-02-20", 3.0)).verde, 0f)   // en el encierro, el núcleo está en el archivo
    }

    /** Los postigos del polvo y la barra de la puerta de la escalera, con sus fechas. */
    @Test fun losPostigosYLaBarraTienenSusFechas() {
        cadaDia(13.0) { d, m ->
            assertEquals(d.toString(), md(d) in "05-20".."07-11" || hoyEs(m, "tormenta_polvo"), EscenaOrden.postigos(m))
            val barra = (md(d) >= "11-15" || md(d) < "03-21") && !hoyEs(m, "caravana")
            assertEquals(d.toString(), barra, EscenaOrden.barra(m))
            if (barra && !hoyEs(m, "caravana_norte") && !m.con("caza")) assertEquals(d.toString(), Puerta.ATRANCADA, EscenaOrden.puerta(m))
        }
        for (anio in 2027..2031) {
            assertTrue(EscenaOrden.postigos(momento("$anio-05-20"))); assertFalse(EscenaOrden.postigos(momento("$anio-05-19"))); assertFalse(EscenaOrden.postigos(momento("$anio-07-12")))
            assertTrue(EscenaOrden.barra(momento("$anio-11-15"))); assertFalse(EscenaOrden.barra(momento("$anio-11-14")))
        }
        assertTrue(EscenaOrden.postigos(momento("2027-04-03")))                              // la tormenta de polvo
        assertEquals(Puerta.ABIERTA, EscenaOrden.puerta(momento("2027-05-05", 7.0)))        // la patrulla, fuera
        assertEquals(Puerta.CERRADA, EscenaOrden.puerta(momento("2027-05-05", 21.0)))
        assertEquals(Puerta.ABIERTA, EscenaOrden.puerta(momento("2027-12-08", 13.0)))       // sube la caravana
    }

    /** Los candiles de las almenas y las ventanas, según la hora, el estado y el día. */
    @Test fun loEncendidoSigueLaHora() {
        val n = EscenaOrden.ALMENAS
        assertEquals(0, EscenaOrden.candiles(momento("2027-05-05", 13.0)))
        assertEquals(n, EscenaOrden.candiles(momento("2027-05-05", 23.0))); assertEquals(n, EscenaOrden.candiles(momento("2027-05-05", 3.0)))
        val tarde = (0..8).map { EscenaOrden.candiles(momento("2027-05-05", 19.4 + it / 6.0)) }
        assertEquals("uno a uno, al caer la tarde", tarde, tarde.sorted()); assertTrue(tarde.first() in 1 until n); assertEquals(n, tarde.last())
        assertEquals(n, EscenaOrden.candiles(momento("2027-01-01", 13.0)))                 // año nuevo: encendidos aunque sea de día
        assertEquals(n, EscenaOrden.candiles(momento("2027-04-03", 13.0)))                 // la tormenta de polvo: a todas horas
        assertEquals(0, EscenaOrden.candiles(momento("2027-09-20", 23.0)))                 // los Caídos: apagados
        assertEquals(1f, EscenaOrden.salaDelCodigo(momento("2027-05-05", 21.0)), 0f); assertEquals(0f, EscenaOrden.salaDelCodigo(momento("2027-05-05", 13.0)), 0f)
        assertEquals(1f, EscenaOrden.salaDelCodigo(momento("2027-12-21", 19.0)), 0f)      // la Noche Larga
        assertEquals(1f, EscenaOrden.ventanaDelArchivo(momento("2029-02-20", 23.0)), 0f)  // el encierro: «un ojo abierto» toda la noche
        assertEquals(0f, EscenaOrden.ventanaDelArchivo(momento("2029-04-20", 23.0)), 0f)
        assertTrue(EscenaOrden.enfermeria(momento("2030-05-10", 22.0)) > 0f); assertEquals(0f, EscenaOrden.enfermeria(momento("2030-07-10", 22.0)), 0f)
        assertEquals(1f, EscenaOrden.fragua(momento("2027-05-05", 13.0)), 0f); assertTrue(EscenaOrden.fragua(momento("2027-05-05", 23.0)) < 0.5f)
        assertTrue(EscenaOrden.vigilia(momento("2027-06-21", 21.0))); assertFalse(EscenaOrden.vigilia(momento("2027-06-21", 13.0)))
    }

    /** La sombra de la Garza camina con la hora; no existe de noche, con el cielo cubierto, con polvo ni sin la Garza. */
    @Test fun laSombraDeLaGarzaCaminaConElSol() {
        var vistos = 0
        cadaDia(13.0) { d, m ->
            if (EscenaOrden.garza(m) != Garza.AMARRADA || m.ficha.clima != Clima.DESPEJADO) return@cadaDia
            val dia = d.toString()
            val s7 = LuzOrden.sombraDeLaGarza(momento(dia, 7.33)); val s13 = LuzOrden.sombraDeLaGarza(momento(dia, 13.0)); val s17 = LuzOrden.sombraDeLaGarza(momento(dia, 17.0))
            assertNotNull(dia, s7); assertNotNull(dia, s13); assertNotNull(dia, s17)
            assertTrue("$dia: por la mañana se va a la izquierda", s7!!.x < s13!!.x - 3f)
            assertTrue("$dia: por la tarde, a la derecha", s17!!.x > s13.x + 3f)
            assertNull(dia, LuzOrden.sombraDeLaGarza(momento(dia, 23.0)))
            for (c in listOf(Clima.NUBES, Clima.LLUVIA, Clima.TORMENTA, Clima.POLVO, Clima.NIEVE)) assertNull("$dia $c", LuzOrden.sombraDeLaGarza(Momento(m.ficha.copy(clima = c), 13.0)))
            vistos++
        }
        assertTrue(vistos > 300)
        assertNull(LuzOrden.sombraDeLaGarza(momento("2028-05-10", 13.0)))                  // en la expedición no está
    }

    /** De noche, con el cielo limpio, hay dos luces: la cálida de los candiles y las ventanas, y la fría de la luna. */
    @Test fun deNocheHayDosLuces() {
        var vistas = 0
        cadaDia(21.0) { d, m ->
            if (m.ficha.clima != Clima.DESPEJADO || m.con("luto")) return@cadaDia
            for (h in listOf(21.0, 23.0)) {
                val (calida, fria) = EscenaOrden.dosLuces(momento(d.toString(), h))
                assertTrue("$d $h: la cálida ($calida)", calida > 0.3f); assertTrue("$d $h: la luna ($fria)", fria > 0.1f)
            }
            vistas++
        }
        assertTrue(vistas > 300)
        assertEquals(0f, EscenaOrden.dosLuces(momento("2027-05-05", 13.0)).second, 0f)
    }

    /**
     * Cada día señalado que se ve en Peñaseca cambia algo del cuadro: o lo que se pinta ese día no es lo mismo que sin el día
     * especial, o no es lo mismo que la víspera (lo que empieza ese día y sigue). Los que no se ven desde el adarve (los de
     * solo título, el consejo, la última página) no están en la lista.
     */
    @Test fun cadaDiaDelCuentoCambiaAlgo() {
        val visibles = mapOf(
            "anio_nuevo" to 13.0, "inventario" to 13.0, "fundacion" to 13.0, "juramento" to 21.0, "postigos" to 13.0, "vigilia" to 21.0, "canales" to 13.0,
            "caza" to 13.0, "caidos" to 21.0, "rutas" to 13.0, "noche_larga" to 19.0, "tormenta_polvo" to 13.0, "pozo" to 13.0, "caravana" to 13.0,
            "sale_garza" to 13.0, "vuelve_garza" to 13.0, "granizo" to 13.0, "nucleo" to 3.0, "entra_archivo" to 23.0, "sale_archivo" to 23.0,
            "ulises" to 13.0, "pozo_seco" to 13.0, "garza_cae" to 13.0, "costura" to 13.0, "emboscada" to 13.0, "sale_enfermeria" to 23.0,
            "garza_vuela" to 13.0, "fermin_muere" to 21.0, "caravana_norte" to 13.0, "ciro" to 13.0, "sequia" to 13.0,
        )
        val vistos = HashSet<String>()
        cadaDia { d, m0 ->
            for ((id, h) in visibles) if (hoyEs(m0, id)) {
                if (d.year > 2031) continue
                val m = momento(d.toString(), h)
                val sin = Momento(m.ficha.copy(especiales = emptyList()), h)
                val vispera = momento(d.minusDays(1).toString(), h)
                assertTrue("$d $id: no cambia nada", EscenaOrden.huella(m) != EscenaOrden.huella(sin) || EscenaOrden.huella(m) != EscenaOrden.huella(vispera))
                vistos += id
            }
        }
        assertEquals(visibles.keys, vistos)
        // Los que mandan su tiempo: la nieve se posa y el polvo se ve.
        for (dia in listOf("2028-01-20", "2030-01-15", "2030-01-16")) assertEquals(dia, 1f, EscenaOrden.nieve(momento(dia)), 0f)
        assertEquals(Clima.POLVO, momento("2031-06-28").ficha.clima)
    }

    /** Los objetos son los del calendario; el candil de la almena de delante no está el día que el candil es el objeto. */
    @Test fun losObjetosSonLosDelCalendarioYNoSalenDosVeces() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosOrden.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosOrden.TODOS.containsAll(ObjetosOrden.GRANDES + ObjetosOrden.EN_EL_CUADRO + ObjetosOrden.QUITA_DEL_CUADRO))
        val base = momento("2027-05-05").ficha
        assertFalse(ObjetosOrden.elCuadroPinta(Momento(base.copy(objeto = "candil"), 21.0), "candil"))
        assertTrue(ObjetosOrden.elCuadroPinta(Momento(base.copy(objeto = "pluma"), 21.0), "candil"))
    }

    /** El gato tiene su tramo libre en el adarve (al menos 110), sus sitios, y una luz válida en todos los estados. */
    @Test fun elGatoTieneSuTramoLibreYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaOrden.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val lugares = HashSet<Lugar>()
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 19.67, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaOrden.anclas(m); lugares += EscenaOrden.lugar(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                val estorbos = EscenaOrden.estorbos(m)
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 30f && suelo.hasta - suelo.x >= 110f)
                // El tramo libre: el más largo entre lo plantado.
                val cortes = (listOf(suelo.x) + estorbos.sorted() + listOf(suelo.hasta))
                assertTrue("$d $h: sin tramo libre de 110", cortes.zipWithNext().any { (a, b) -> b - a >= 110f })
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                estorbos.forEach { assertTrue("$d $h: lo plantado fuera del tramo", it in suelo.x..suelo.hasta) }
                val quieto = gatoQuieto(suelo, anclas, talla, estorbos)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaOrden.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFFA3A8AE), Color.White, Color.Black, Color(0xFF9E1B22))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f)
                }
            }
            d = d.plusDays(3)
        }
        assertEquals(Lugar.entries.toSet(), lugares)
    }

    @Test fun elMismoMomentoDaLoMismo() {
        val a = momento("2027-05-05", 13.0); val b = momento("2027-05-05", 13.0)
        assertEquals(EscenaOrden.huella(a), EscenaOrden.huella(b))
        assertEquals(EscenaOrden.anclas(a).map { it.x to it.y }, EscenaOrden.anclas(b).map { it.x to it.y })
        cadaDia { d, m -> assertTrue("$d", EscenaOrden.seMueve(m)) }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** El cuadro se dibuja entero (la parte quieta y la viva) en cada estado, fase de la Garza, tiempo y objeto, sin lanzar nada. */
    @Test fun elCuadroSeDibujaEnTodosSusEstados() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-05-05", "2027-01-01", "2027-03-15", "2027-04-02", "2027-06-21", "2027-07-12", "2027-08-25", "2027-09-20", "2027-12-08",
            "2028-05-10", "2028-07-18", "2029-02-20", "2029-08-10", "2030-01-15", "2030-04-28", "2030-06-10", "2030-09-10", "2031-06-28", "2032-03-01")
        val garzas = HashSet<Garza>()
        for (dia in dias) for (h in listOf(3.0, 7.0, 13.0, 19.67, 21.0)) {
            val base = momento(dia, h); garzas += EscenaOrden.garza(base)
            for (o in ObjetosOrden.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.POLVO, Clima.TORMENTA, Clima.NIEVE)) {
                if (clima != base.ficha.clima && o != null) continue
                if (o != null && h != 13.0 && dia != "2027-05-05") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                    lienzo { with(EscenaOrden) { quieta(m) } }
                    lienzo { with(EscenaOrden) { viva(m, 3.7f) } }
                }
            }
        }
        assertEquals(Garza.entries.toSet(), garzas)
    }
}
