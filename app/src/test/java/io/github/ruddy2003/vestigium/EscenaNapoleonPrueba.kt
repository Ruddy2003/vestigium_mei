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
import io.github.ruddy2003.vestigium.data.OtrasVidas.Hacer
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Compania
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Fuego
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Marmita
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Moscu
import io.github.ruddy2003.vestigium.ui.escenas.EscenaNapoleon.Tramo
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaLuz
import io.github.ruddy2003.vestigium.ui.escenas.RetiradaNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.VistaNapoleon
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * Los cuadros de Napoleónico (tanda S14: la retirada, y el vivac todavía provisional) cuentan lo mismo que el cuento de
 * Jean-Baptiste: dónde está, el tramo de la retirada, la nieve, la compañía y sus fuegos, la fase del día y lo que hay
 * encendido o en uso a cada hora, y los días señalados salen de la ficha del día, y aquí se comparan con su calendario
 * (`AnioG5.kt`, `JEAN_BAPTISTE`) todos los días de 2026 a 2032.
 */
class EscenaNapoleonPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "granadero")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDelGranadero() {
        assertEquals(listOf("granadero"), EscenaNapoleon.vidas)
        assertEquals("granadero", EscenaNapoleon.vida("2027-11-10"))
        assertTrue(EscenaNapoleon.tallaDelGato in 0.8f..1.8f)
        assertEquals("napoleon", OtrasVidas.porId("granadero")!!.tema)
    }

    @Test fun estaDondeDiceElCalendario() {
        val retirada = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) {
                "retirada" -> Lugar.RETIRADA; "cuartel" -> Lugar.CUARTEL; "moscu" -> Lugar.MOSCU; "hospital" -> Lugar.HOSPITAL; "pueblo" -> Lugar.PUEBLO
                else -> Lugar.VIVAC
            }
            assertEquals(d.toString(), esperado, EscenaNapoleon.lugar(m))
            // Solo la retirada tiene cuadro propio; los demás sitios se pintan, por ahora, con el vivac provisional.
            assertEquals(d.toString(), if (esperado == Lugar.RETIRADA) Cuadro.RETIRADA else Cuadro.VIVAC, EscenaNapoleon.cuadro(m))
            assertEquals(d.toString(), esperado != Lugar.RETIRADA && esperado != Lugar.VIVAC, EscenaNapoleon.provisional(m))
            if (esperado == Lugar.RETIRADA) { retirada[d.year] = (retirada[d.year] ?: 0) + 1; assertTrue(d.toString(), md(d) >= "10-19" && md(d) <= "12-13") }
        }
        // La retirada: 56 días, del 19 de octubre al 13 de diciembre, en 2026 (el avance) y en 2027; nunca más.
        assertEquals(mapOf(2026 to 56, 2027 to 56), retirada)
        assertEquals(Lugar.MOSCU, EscenaNapoleon.lugar(momento("2027-10-18"))); assertEquals(Lugar.RETIRADA, EscenaNapoleon.lugar(momento("2027-10-19")))
        assertEquals(Lugar.RETIRADA, EscenaNapoleon.lugar(momento("2027-12-13"))); assertEquals(Lugar.VIVAC, EscenaNapoleon.lugar(momento("2027-12-14")))
        assertEquals(Lugar.CUARTEL, EscenaNapoleon.lugar(momento("2027-02-01"))); assertEquals(Lugar.HOSPITAL, EscenaNapoleon.lugar(momento("2028-02-01")))
        assertEquals(Lugar.PUEBLO, EscenaNapoleon.lugar(momento("2031-06-01"))); assertEquals(Lugar.VIVAC, EscenaNapoleon.lugar(momento("2027-07-10")))
    }

    @Test fun elTramoDeLaRetirada() {
        val berezina = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val t = EscenaNapoleon.tramo(m)
            if (EscenaNapoleon.lugar(m) != Lugar.RETIRADA) { assertNull(d.toString(), t); return@cadaDia }
            when {
                hoyEs(m, "berezina") -> { assertEquals(d.toString(), Tramo.BEREZINA, t); berezina[d.year] = (berezina[d.year] ?: 0) + 1 }
                md(d) < "11-06" -> assertEquals(d.toString(), Tramo.SIN_NIEVE, t)
                else -> assertEquals(d.toString(), Tramo.NEVADA, t)
            }
        }
        // El Berezina, sus tres días (del 26 al 28 de noviembre), en los dos años de la retirada.
        assertEquals(mapOf(2026 to 3, 2027 to 3), berezina)
        assertEquals(Tramo.BEREZINA, EscenaNapoleon.tramo(momento("2027-11-28"))); assertEquals(Tramo.NEVADA, EscenaNapoleon.tramo(momento("2027-11-29")))
    }

    /** La regla de la nieve (**[Aproximación]**): no contradice el cuento. */
    @Test fun laNieveNoContradiceAlCuento() {
        cadaDia { d, m ->
            for (h in listOf(5.0, 9.0, 12.0, 21.0)) {
                val mh = momento(d.toString(), h); val n = EscenaNapoleon.nieve(mh); val clima = mh.ficha.clima
                assertTrue("$d $h", n in 0f..1f)
                if (EscenaNapoleon.lugar(mh) == Lugar.RETIRADA) {
                    // Antes del 6 de noviembre no hay nieve en el suelo; si el sorteo trae nieve, solo una primera nevada fina.
                    if (md(d) < "11-06") assertEquals("$d $h", if (clima == Clima.NIEVE) 0.35f else 0f, n, 0.0001f)
                    // Desde el 7, «y ya no paró»: el suelo blanco (aplastado si llueve).
                    if (md(d) > "11-06") assertTrue("$d $h", n >= 0.84f * 0.85f - 0.001f)
                    if (md(d) > "11-06" && clima == Clima.NIEVE) assertTrue("$d $h", n >= 0.9f)
                    // La escarcha solo sin nieve, y el barro helado de noviembre todo el día.
                    if (EscenaNapoleon.helada(mh)) assertTrue("$d $h", n < 0.5f)
                } else {
                    // Fuera de la retirada: la clave «nieve» (Montmirail) la pone toda; en el pueblo, en verano, nada.
                    if (mh.con("nieve")) assertEquals("$d $h", 1f, n, 0f)
                    if (EscenaNapoleon.lugar(mh) == Lugar.PUEBLO && md(d) in "05-01".."09-30" && clima != Clima.NIEVE) assertEquals("$d $h", 0f, n, 0f)
                }
                if (EscenaNapoleon.nieveMojada(mh)) assertTrue("$d $h", clima == Clima.LLUVIA || clima == Clima.TORMENTA)
            }
        }
        // El 6 de noviembre «empezó a nevar… Al mediodía el camino estaba blanco».
        for (a in listOf(2026, 2027)) {
            val alba = EscenaNapoleon.nieve(momento("$a-11-06", 5.0)); val mediodia = EscenaNapoleon.nieve(momento("$a-11-06", 12.0))
            assertTrue("$a", alba < 0.2f); assertTrue("$a", mediodia >= 0.85f)
            assertEquals("$a", 1f, EscenaNapoleon.nieve(momento("$a-12-01")), 0.16f)
        }
        // El 19 de octubre, al salir de Moscú, «ya hace frío» (escarcha por la mañana) y no hay nieve.
        assertTrue(EscenaNapoleon.helada(momento("2027-10-19", 7.0))); assertEquals(0f, EscenaNapoleon.nieve(momento("2027-10-19", 7.0)), 0f)
        // El sol de noviembre va bajo; el de julio, el de siempre.
        assertTrue(EscenaNapoleon.alturaDelSol(momento("2027-11-21")) < 0.5f); assertEquals(1f, EscenaNapoleon.alturaDelSol(momento("2027-07-10")), 0f)
    }

    @Test fun losFuegosSiguenALaCompania() {
        cadaDia { d, m ->
            val c = m.ficha.arcos["compania"]!!.toFloat()
            assertEquals(d.toString(), c.coerceIn(0f, 1f), EscenaNapoleon.compania(m), 0.0001f)
            val esperado = if (c >= 0.8f) Compania.ENTERA else if (c >= 0.5f) Compania.MERMADA else Compania.POCOS
            assertEquals(d.toString(), esperado, EscenaNapoleon.cuantos(m))
            val f = EscenaNapoleon.fuegos(m)
            when (esperado) {
                Compania.ENTERA -> assertTrue(d.toString(), f in 14..17)            // «tantos fuegos como ranchos»
                Compania.MERMADA -> assertTrue(d.toString(), f in 8..13)
                Compania.POCOS -> assertEquals(d.toString(), 1, f)                  // «caben todos alrededor de un solo fuego»
            }
            val camino = RetiradaNapoleon.fuegosDelCamino(m)
            assertTrue(d.toString(), camino in 0..5); if (esperado == Compania.POCOS) assertEquals(d.toString(), 0, camino)
        }
        // En la retirada, con pocos desde el 7 de noviembre (el día siguiente a la nieve).
        assertEquals(Compania.MERMADA, EscenaNapoleon.cuantos(momento("2027-10-19"))); assertEquals(Compania.POCOS, EscenaNapoleon.cuantos(momento("2027-11-07")))
        assertEquals(Compania.ENTERA, EscenaNapoleon.cuantos(momento("2027-07-01"))); assertEquals(1, EscenaNapoleon.fuegos(momento("2027-12-01")))
        // La compañía se rehace de golpe el 20 de marzo de 1813.
        assertEquals(Compania.POCOS, EscenaNapoleon.cuantos(momento("2028-03-19"))); assertEquals(Compania.ENTERA, EscenaNapoleon.cuantos(momento("2028-03-20")))
    }

    @Test fun loEncendidoYEnUsoSigueElHorario() {
        cadaDia { d, _ ->
            for (h in listOf(1.0, 3.6, 4.5, 7.0, 9.0, 12.5, 13.0, 16.0, 19.0, 21.0, 23.0)) {
                val m = momento(d.toString(), h)
                val fase = EscenaNapoleon.fase(m)
                assertEquals("$d $h", when (m.hacer) { Hacer.CAMINO -> Fase.MARCHA; Hacer.COMER -> Fase.ALTO; Hacer.CUIDAR -> Fase.VIVAC; Hacer.DORMIR -> Fase.SUENO; else -> Fase.MARCHA }, fase)
                val f = EscenaNapoleon.fuego(m)
                assertTrue("$d $h", f in 0f..1f)
                val estado = EscenaNapoleon.estadoDelFuego(m)
                assertEquals("$d $h", if (f >= 0.5f) Fuego.ENCENDIDO else if (f > 0.02f) Fuego.RESCOLDO else Fuego.APAGADO, estado)
                // El vivac encendido de 18 a 22; de noche, el rescoldo; por la tarde, de marcha, la ceniza fría.
                if (fase == Fase.VIVAC) assertEquals("$d $h", Fuego.ENCENDIDO, estado)
                if (fase == Fase.SUENO) assertEquals("$d $h", Fuego.RESCOLDO, estado)
                if (fase == Fase.MARCHA && h >= 10.0) assertEquals("$d $h", Fuego.APAGADO, estado)
                // Los fusiles en pabellón y el águila, mientras está la compañía; de marcha se los llevan.
                assertEquals("$d $h", fase != Fase.MARCHA, EscenaNapoleon.pabellon(m))
                assertEquals("$d $h", when (fase) { Fase.ALTO, Fase.VIVAC -> Marmita.AL_FUEGO; Fase.SUENO -> Marmita.AL_LADO; Fase.MARCHA -> Marmita.NINGUNA }, EscenaNapoleon.marmita(m))
                assertEquals("$d $h", fase == Fase.SUENO, EscenaNapoleon.camas(m))
                if (EscenaNapoleon.carneEnLaBayoneta(m)) assertTrue("$d $h", EscenaNapoleon.lugar(m) == Lugar.RETIRADA && (fase == Fase.ALTO || fase == Fase.VIVAC))
                assertTrue("$d $h", EscenaNapoleon.huellas(m) in 0f..1f)
                assertEquals("$d $h", h >= 17.0 || h < 6.0, EscenaNapoleon.fuegosLejos(m))
            }
        }
        // Las tres horas de las capturas dan tres cuadros distintos: de marcha por la mañana (rescoldo), de marcha por la
        // tarde (ceniza fría) y el vivac encendido.
        val d = "2027-11-21"
        assertEquals(Fuego.RESCOLDO, EscenaNapoleon.estadoDelFuego(momento(d, 7.0))); assertEquals(Fuego.APAGADO, EscenaNapoleon.estadoDelFuego(momento(d, 13.0)))
        assertEquals(Fuego.ENCENDIDO, EscenaNapoleon.estadoDelFuego(momento(d, 21.0)))
        assertEquals(Fase.ALTO, EscenaNapoleon.fase(momento(d, 12.5))); assertTrue(EscenaNapoleon.carneEnLaBayoneta(momento(d, 12.5)))
    }

    @Test fun losDiasSenaladosSeVenSuDiaYNoLaVispera() {
        cadaDia { d, m ->
            assertEquals(d.toString(), m.con("guerra"), EscenaNapoleon.guerra(m)); assertEquals(d.toString(), m.con("luto"), EscenaNapoleon.luto(m))
            assertEquals(d.toString(), m.con("fiesta"), EscenaNapoleon.fiesta(m)); assertEquals(d.toString(), m.con("marcha"), EscenaNapoleon.marcha(m))
            assertEquals(d.toString(), EscenaNapoleon.lugar(m) == Lugar.RETIRADA, EscenaNapoleon.aguilaEnvuelta(m))
            if (EscenaNapoleon.puebloArde(m)) assertEquals(d.toString(), "10-24", md(d))
            if (EscenaNapoleon.humoDelPuente(m)) assertEquals(d.toString(), "11-29", md(d))
            if (EscenaNapoleon.trineo(m)) assertEquals(d.toString(), "12-06", md(d))
            if (EscenaNapoleon.smolensk(m)) assertTrue(d.toString(), md(d) == "08-17" || md(d) == "08-18")
            for (h in listOf(5.0, 12.0, 20.0)) { val mh = momento(d.toString(), h); if (EscenaNapoleon.canonesEnLasColinas(mh)) assertTrue("$d $h", hoyEs(mh, "krasnoi") && h in 6.0..18.0) }
            val a = EscenaNapoleon.abandono(m); assertTrue(d.toString(), a in 0f..1f); if (EscenaNapoleon.lugar(m) != Lugar.RETIRADA) assertEquals(d.toString(), 0f, a, 0f)
            assertTrue(d.toString(), RetiradaNapoleon.abandonadas(m) in 2..9)
            val ms = EscenaNapoleon.moscu(m)
            if (EscenaNapoleon.lugar(m) != Lugar.MOSCU) assertNull(d.toString(), ms) else assertNotNull(d.toString(), ms)
        }
        for (a in listOf(2026, 2027)) {
            assertTrue(EscenaNapoleon.puebloArde(momento("$a-10-24"))); assertFalse(EscenaNapoleon.puebloArde(momento("$a-10-23")))
            assertTrue(EscenaNapoleon.humoDelPuente(momento("$a-11-29"))); assertTrue(EscenaNapoleon.luto(momento("$a-11-29")))
            assertTrue(EscenaNapoleon.trineo(momento("$a-12-06"))); assertFalse(EscenaNapoleon.trineo(momento("$a-12-05")))
            assertTrue(EscenaNapoleon.canonesEnLasColinas(momento("$a-11-16", 12.0))); assertFalse(EscenaNapoleon.canonesEnLasColinas(momento("$a-11-16", 21.0)))
            assertTrue(EscenaNapoleon.cargados(momento("$a-10-19"))); assertTrue(EscenaNapoleon.marcha(momento("$a-10-19")))
            // Moscú: entera el primer día, en llamas cuatro días, y luego quemada.
            assertEquals(Moscu.ENTERA, EscenaNapoleon.moscu(momento("$a-09-14"))); assertEquals(Moscu.ARDE, EscenaNapoleon.moscu(momento("$a-09-15")))
            assertEquals(Moscu.ARDE, EscenaNapoleon.moscu(momento("$a-09-18"))); assertEquals(Moscu.QUEMADA, EscenaNapoleon.moscu(momento("$a-09-19")))
            assertTrue(EscenaNapoleon.incendio(momento("$a-09-16")) > EscenaNapoleon.incendio(momento("$a-09-18")))
            assertTrue(EscenaNapoleon.smolensk(momento("$a-08-17"))); assertFalse(EscenaNapoleon.smolensk(momento("$a-08-16")))
            // Lo abandonado crece a lo largo de la retirada.
            assertTrue(EscenaNapoleon.abandono(momento("$a-10-19")) < EscenaNapoleon.abandono(momento("$a-11-10")))
            assertTrue(RetiradaNapoleon.abandonadas(momento("$a-10-19")) < RetiradaNapoleon.abandonadas(momento("$a-12-01")))
        }
    }

    /** La luz de la retirada: las franjas del bosque solo con sol (o con luna), caminan con la hora, y de noche hay dos luces. */
    @Test fun laLuzCaminaConLaHoraYDeNocheHayDosLuces() {
        val manana = VistaNapoleon(momento("2027-11-21", 7.0)); val tarde = VistaNapoleon(momento("2027-11-21", 16.5)); val noche = VistaNapoleon(momento("2027-11-21", 21.0))
        assertTrue(manana.franjas.isNotEmpty()); assertTrue(manana.r.fuerza > 0.1f)
        // Por la mañana el sol viene de la derecha; por la tarde, de la izquierda: las sombras caen al revés.
        assertTrue(manana.r.ax < 0f); assertTrue(tarde.r.ax > 0f)
        // De noche: la luna tumba sus sombras y el fuego del vivac alumbra.
        assertNotNull(noche.rLuna); assertTrue(noche.luna > 0.3f); assertTrue(noche.lumbre > 0.8f); assertTrue(RetiradaLuz.mandaElFuego(noche) > 0.5f)
        // Nevando no hay sombras arrojadas.
        val nevando = VistaNapoleon(Momento(FichaDelDia.de("2027-11-21", "granadero")!!.copy(clima = Clima.NIEVE), 12.0))
        assertTrue(nevando.franjas.isEmpty()); assertNull(RetiradaLuz.deSombras(nevando))
    }

    /** Lo mismo que vigila `GatoCuadroPrueba` en todos los cuadros: suelo a todas horas y bastante largo, sus sitios y una luz válida. */
    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosDosCuadros() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaNapoleon.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Cuadro>()
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 17.0, 20.5, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaNapoleon.anclas(m); vistos += EscenaNapoleon.cuadro(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val quieto = gatoQuieto(suelo, anclas, talla, EscenaNapoleon.estorbos(m))
                assertEquals(AnimGato.SENTADO, quieto.anim)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaNapoleon.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
            d = d.plusDays(3)
        }
        assertEquals(setOf(Cuadro.VIVAC, Cuadro.RETIRADA), vistos)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosNapoleon.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun elHumoSeMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaNapoleon.seMueve(m)) }
        val a = momento("2027-11-21", 13.0); val b = momento("2027-11-21", 13.0)
        assertEquals(EscenaNapoleon.nieve(a), EscenaNapoleon.nieve(b), 0f)
        assertEquals(EscenaNapoleon.anclas(a).map { it.x to it.y }, EscenaNapoleon.anclas(b).map { it.x to it.y })
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * Los dos cuadros se dibujan enteros (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, a las
     * horas de cada fase, de día y de noche, con nieve, nevando, con lluvia y en los días señalados (la salida de Moscú,
     * Maloyaroslavets, la nieve, Krasnoi, el Berezina, Lucien, el trineo; el vivac, Moscú en llamas, el cuartel, el
     * hospital y el pueblo), sin lanzar nada.
     */
    @Test fun losDosCuadrosSeDibujanConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-10-19", "2027-10-24", "2027-11-06", "2027-11-15", "2027-11-26", "2027-11-29", "2027-12-06", "2027-07-10", "2027-09-16", "2027-02-01", "2028-02-01", "2031-06-01")
        val vistos = HashSet<Cuadro>()
        for (dia in dias) for (h in listOf(4.5, 7.0, 12.5, 19.0, 21.0, 23.0)) {
            val base = momento(dia, h); vistos += EscenaNapoleon.cuadro(base)
            for (o in ObjetosNapoleon.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.NIEVE, Clima.LLUVIA)) {
                if (clima != base.ficha.clima && o != null && o != "fusil") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaNapoleon) { quieta(m) } }
                        lienzo { with(EscenaNapoleon) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(setOf(Cuadro.VIVAC, Cuadro.RETIRADA), vistos)
    }
}
