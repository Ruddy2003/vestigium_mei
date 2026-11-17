package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.escenas.AbrigoCueva
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaCueva
import io.github.ruddy2003.vestigium.ui.escenas.EscenaCueva.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaCueva.Manada
import io.github.ruddy2003.vestigium.ui.escenas.FrisoCueva
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.MesetaCueva
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosCueva
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * Los dos cuadros de Prehistoria (tanda S10) cuentan lo mismo que el cuento de Ama: dónde vive el grupo (bajo el abrigo
 * o en la meseta), cuánto del friso está pintado, cuánta nieve hay, cómo arde el fuego y qué pasa los días señalados
 * salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaCuevaPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "pintora_cueva")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    /** El año (de 2027 a 2031) en que cae la temporada [t] del cuento. */
    private fun anio(t: Int): Int = (2027..2031).first { momento("$it-06-15").ficha.temporada == t }

    @Test fun laEscenaEsLaDeAma() {
        assertEquals(listOf("pintora_cueva"), EscenaCueva.vidas)
        assertEquals("pintora_cueva", EscenaCueva.vida("2027-05-10"))
        assertTrue(EscenaCueva.tallaDelGato in 0.8f..1.8f)
        assertEquals("cueva", OtrasVidas.porId("pintora_cueva")!!.tema)
        // Las cinco temporadas caen en cinco años seguidos.
        assertEquals(setOf(1, 2, 3, 4, 5), (2027..2031).map { momento("$it-06-15").ficha.temporada }.toSet())
    }

    @Test fun estaDondeDiceElCalendario() {
        val dias = mutableMapOf<Pair<Int, Lugar>, Int>()
        cadaDia { d, m ->
            val esperado = if (m.ficha.estado == "verano") Lugar.MESETA else Lugar.ABRIGO
            assertEquals(d.toString(), esperado, EscenaCueva.lugar(m))
            // El verano del calendario va del 1 de junio al 19 de septiembre, todos los años.
            val md = d.toString().substring(5)
            assertEquals(d.toString(), md >= "06-01" && md < "09-20", EscenaCueva.lugar(m) == Lugar.MESETA)
            dias[d.year to esperado] = (dias[d.year to esperado] ?: 0) + 1
        }
        for (a in 2026..2032) { assertEquals("$a", 111, dias[a to Lugar.MESETA]); assertTrue("$a", (dias[a to Lugar.ABRIGO] ?: 0) >= 254) }
        assertEquals(Lugar.ABRIGO, EscenaCueva.lugar(momento("2027-05-31"))); assertEquals(Lugar.MESETA, EscenaCueva.lugar(momento("2027-06-01")))
        assertEquals(Lugar.MESETA, EscenaCueva.lugar(momento("2027-09-19"))); assertEquals(Lugar.ABRIGO, EscenaCueva.lugar(momento("2027-09-20")))
    }

    @Test fun elFrisoCreceAnioAAnioYSeVeDesdeSuDia() {
        val t1 = anio(1)
        // Antes del primer caballo no hay nada; al día siguiente ya hay trazo.
        assertEquals(0f, EscenaCueva.friso(momento("$t1-01-14")), 0f); assertEquals(0f, FrisoCueva.avance(0, EscenaCueva.friso(momento("$t1-01-15"))), 0.0001f)
        assertTrue(FrisoCueva.avance(0, EscenaCueva.friso(momento("$t1-01-25"))) > 0f)
        // No retrocede nunca dentro de las cinco temporadas, y cada año deja más figuras terminadas.
        var antes = 0f; var d = LocalDate.of(t1, 1, 1)
        while (d.year <= t1 + 4) { val f = EscenaCueva.friso(momento(d.toString())); assertTrue("$d", f in 0f..1f && f >= antes - 0.0001f); assertEquals("$d", momento(d.toString()).ficha.arcos["friso"]!!.toFloat().coerceIn(0f, 1f), f, 0.0001f); antes = f; d = d.plusDays(1) }
        val alFinal = (0..4).map { FrisoCueva.terminadas(EscenaCueva.friso(momento("${t1 + it}-12-31"))) }
        assertEquals(listOf(1, 2, 3, 4, 6), alFinal)
        // «El friso va por la mitad» (10 de noviembre de la temporada 3): tres figuras, la fila de puntos entera y la cuarta empezada.
        val mitad = EscenaCueva.friso(momento("${t1 + 2}-11-10"))
        assertEquals(3, FrisoCueva.terminadas(mitad)); assertEquals(12, FrisoCueva.puntos(mitad)); assertTrue(FrisoCueva.avance(3, mitad) > 0f && FrisoCueva.avance(3, mitad) < 1f)
        // El día que lo termina (28 de octubre de la temporada 5) están las seis.
        assertTrue(FrisoCueva.terminadas(EscenaCueva.friso(momento("${t1 + 4}-10-27"))) < 6); assertEquals(6, FrisoCueva.terminadas(EscenaCueva.friso(momento("${t1 + 4}-10-28"))))
        // Las figuras caben en el paño de la pared y los tramos del arco se suceden sin huecos que dejen un año sin pintar.
        for (f in FrisoCueva.FIGURAS) { assertTrue(f.centro.x - f.largo * 0.6f >= FrisoCueva.PANEL.left - 4f && f.centro.x + f.largo * 0.6f <= FrisoCueva.PANEL.right + 6f); assertTrue(f.desde < f.hasta) }
        for (i in 1 until FrisoCueva.FIGURAS.size) assertTrue(FrisoCueva.FIGURAS[i].desde >= FrisoCueva.FIGURAS[i - 1].hasta - 0.0001f)
        // Las manos: la de Ama, desde que su madre le enseña; la de Lin, en la temporada 5; la lámpara nueva, desde la 2.
        assertFalse(EscenaCueva.manoDeAma(momento("$t1-02-19"))); assertTrue(EscenaCueva.manoDeAma(momento("$t1-02-20"))); assertTrue(EscenaCueva.manoDeAma(momento("${t1 + 3}-07-01")))
        assertFalse(EscenaCueva.manoDeLin(momento("${t1 + 4}-03-09"))); assertTrue(EscenaCueva.manoDeLin(momento("${t1 + 4}-03-10"))); assertFalse(EscenaCueva.manoDeLin(momento("${t1 + 3}-12-01")))
        assertFalse(EscenaCueva.lamparaNueva(momento("${t1 + 1}-08-09"))); assertTrue(EscenaCueva.lamparaNueva(momento("${t1 + 1}-08-10")))
        assertEquals(1, AbrigoCueva.lamparas(momento("$t1-11-05", 17.0)).size); assertEquals(2, AbrigoCueva.lamparas(momento("${t1 + 2}-11-05", 17.0)).size); assertEquals(5, AbrigoCueva.lamparas(momento("${t1 + 4}-10-28", 17.0)).size)
    }

    @Test fun laNieveYElRioSiguenSuArco() {
        var nevados = 0
        cadaDia { d, m ->
            val k = EscenaCueva.nieve(m); val h = EscenaCueva.hielo(m); val arco = m.ficha.arcos["nieve"]!!.toFloat().coerceIn(0f, 1f)
            assertTrue("$d", k in 0f..1f && h in 0f..1f)
            if (EscenaCueva.lugar(m) == Lugar.MESETA) { assertEquals("$d", 0f, k, 0f); assertEquals("$d", 0f, h, 0f) }
            else { assertTrue("$d", k >= arco - 0.0001f); if (m.con("nieve") || m.ficha.clima == Clima.NIEVE) { assertTrue("$d", k >= 0.85f); nevados++ } else assertEquals("$d", arco, k, 0.0001f) }
            if (h > 0f) assertTrue("$d", arco > 0.3f)
            if (EscenaCueva.deshielo(m)) assertTrue("$d", d.monthValue == 4 && d.dayOfMonth in 10..30)
        }
        assertTrue(nevados > 50)
        for (a in 2027..2031) {
            assertEquals("$a", 1f, EscenaCueva.nieve(momento("$a-01-15")), 0.001f); assertEquals("$a", 1f, EscenaCueva.hielo(momento("$a-01-15")), 0.001f)
            assertTrue("$a", EscenaCueva.nieve(momento("$a-02-01")) >= 0.85f)                                       // el frío hondo
            assertTrue("$a", EscenaCueva.deshielo(momento("$a-04-10"))); assertFalse("$a", EscenaCueva.deshielo(momento("$a-04-09")))
            assertEquals("$a", 0f, EscenaCueva.nieve(momento("$a-07-15")), 0f); assertEquals("$a", 0f, EscenaCueva.hielo(momento("$a-10-05")), 0f)
            assertTrue("$a", EscenaCueva.nieve(momento("$a-11-20")) >= 0.85f)                                       // la primera nieve
        }
    }

    @Test fun losDiasSenaladosSeVenDesdeSuDia() {
        val t1 = anio(1)
        for (a in 2027..2031) {
            // Los renos cruzan el vado veinte días desde el 1 de octubre: la caza.
            assertFalse("$a", EscenaCueva.caza(momento("$a-09-30"))); for (dia in listOf("10-01", "10-12", "10-20")) { val m = momento("$a-$dia"); assertTrue("$a-$dia", EscenaCueva.caza(m)); assertEquals(Lugar.ABRIGO, EscenaCueva.lugar(m)); assertEquals(Manada.RENOS, EscenaCueva.manada(m)) }
            assertFalse("$a", EscenaCueva.caza(momento("$a-10-21")))
            // La noche más larga: fuego grande desde la tarde.
            val larga = momento("$a-12-21", 20.0); assertTrue("$a", EscenaCueva.fuegoGrande(larga)); assertEquals("$a", 1.3f, EscenaCueva.lumbre(larga), 0f); assertTrue("$a", EscenaCueva.lumbre(momento("$a-12-21", 10.0)) < 1f)
            // Los salmones suben quince días desde el 20 de julio, en la meseta.
            assertTrue("$a", EscenaCueva.salmones(momento("$a-07-20"))); assertTrue("$a", EscenaCueva.salmones(momento("$a-08-03"))); assertFalse("$a", EscenaCueva.salmones(momento("$a-08-04"))); assertEquals(Lugar.MESETA, EscenaCueva.lugar(momento("$a-07-25")))
            assertEquals("$a", Manada.SAIGAS, EscenaCueva.manada(momento("$a-05-15")))
        }
        assertEquals(Manada.MAMUTS, EscenaCueva.manada(momento("$t1-05-05")))
        assertTrue(EscenaCueva.leon(momento("${t1 + 1}-01-25"))); assertTrue(EscenaCueva.lumbre(momento("${t1 + 1}-01-25", 3.0)) >= 0.8f)
        // El luto por Yemi (10 de febrero de la temporada 3) y su túmulo, que queda.
        assertTrue(EscenaCueva.luto(momento("${t1 + 2}-02-10"))); assertFalse(EscenaCueva.luto(momento("${t1 + 2}-02-11"))); assertFalse(EscenaCueva.paso(momento("${t1 + 2}-02-09"), 3, "02-10")); assertTrue(EscenaCueva.paso(momento("${t1 + 3}-06-01"), 3, "02-10"))
        assertTrue(EscenaCueva.lampara(momento("${t1 + 2}-02-10", 10.0)))
        // La reunión de los grupos (cinco días desde el 1 de julio de la temporada 3), en la meseta.
        for (dia in listOf("07-01", "07-03", "07-05")) { val m = momento("${t1 + 2}-$dia"); assertTrue(dia, EscenaCueva.reunion(m)); assertEquals(Lugar.MESETA, EscenaCueva.lugar(m)) }
        assertFalse(EscenaCueva.reunion(momento("${t1 + 2}-07-06"))); assertFalse(EscenaCueva.reunion(momento("${t1 + 1}-07-01")))
        // La aurora (20 de enero de la temporada 5), desde el abrigo.
        assertTrue(EscenaCueva.aurora(momento("${t1 + 4}-01-20"))); assertEquals(Lugar.ABRIGO, EscenaCueva.lugar(momento("${t1 + 4}-01-20"))); assertFalse(EscenaCueva.aurora(momento("${t1 + 4}-01-21"))); assertFalse(EscenaCueva.aurora(momento("${t1 + 3}-01-20")))
        // El día que el grupo baja a ver el friso: fuego grande y la hilera de lámparas.
        val fin = momento("${t1 + 4}-10-28", 19.0); assertTrue(EscenaCueva.fiestaDelFriso(fin)); assertTrue(EscenaCueva.fuegoGrande(fin)); assertTrue(EscenaCueva.lampara(momento("${t1 + 4}-10-28", 10.0)))
    }

    @Test fun elFuegoSigueElDiaYMandaDeNoche() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 6.4, 10.0, 13.0, 17.0, 19.0, 21.0, 23.4)) {
                val m = momento(d.toString(), h); val f = EscenaCueva.lumbre(m)
                assertTrue("$d $h", f in 0.2f..1.3f)
                if (h == 19.0) assertTrue("$d $h", f >= 1f)
                if (EscenaCueva.lugar(m) == Lugar.MESETA) { assertFalse("$d $h", EscenaCueva.lampara(m)); assertTrue("$d $h: la hoguera del corro arde todo el día", f >= 0.6f) }
                else {
                    val z = EscenaCueva.luzDeDentro(m); assertTrue("$d $h", z.mandaElFuego in 0f..1f)
                    if (h == 17.0) assertTrue("$d", EscenaCueva.lampara(m))
                    if (h == 21.0) assertTrue("$d: a las nueve manda el fuego (${z.mandaElFuego})", z.mandaElFuego > 0.6f)
                    if (h == 13.0 && m.ficha.clima == Clima.DESPEJADO) assertTrue("$d: a mediodía manda el sol (${z.mandaElFuego})", z.mandaElFuego < 0.35f)
                }
            }
        }
        // Un día corriente: rescoldo por la mañana, fuego vivo al caer la tarde, brasas de madrugada; la lámpara, mientras pinta.
        val dia = "${anio(2)}-11-05"
        assertTrue(EscenaCueva.lumbre(momento(dia, 10.0)) < EscenaCueva.lumbre(momento(dia, 19.0))); assertTrue(EscenaCueva.lumbre(momento(dia, 4.0)) < EscenaCueva.lumbre(momento(dia, 21.0)))
        assertFalse(EscenaCueva.lampara(momento(dia, 10.0))); assertTrue(EscenaCueva.lampara(momento(dia, 16.4))); assertTrue(EscenaCueva.lampara(momento(dia, 22.0)))
        // Cuando el grupo se duerme queda una sola lámpara, de vela, hasta el alba.
        assertEquals(2, AbrigoCueva.lamparas(momento(dia, 22.0)).size); assertTrue(EscenaCueva.lampara(momento(dia, 23.4))); assertTrue(EscenaCueva.velando(momento(dia, 23.4))); assertEquals(1, AbrigoCueva.lamparas(momento(dia, 23.4)).size); assertEquals(1, AbrigoCueva.lamparas(momento(dia, 3.0)).size); assertFalse(EscenaCueva.lampara(momento(dia, 7.0)))
        // La mancha de sol cae dentro del suelo del abrigo a cualquier hora.
        for (h in listOf(6.0, 7.0, 9.0, 13.0, 17.0, 18.4)) { val c = AbrigoCueva.centroDelSol(EscenaCueva.luzDeDentro(momento(dia, h)).sol); assertTrue("$h $c", c.y >= AbrigoCueva.LABIO && c.y <= ALTO && c.x in 60f..ANCHO) }
    }

    /**
     * Lo mismo que comprueba `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` para las escenas registradas en `escenaDe`
     * (esta todavía no lo está): suelo a todas horas y bastante largo, lo plantado dentro del tramo, el gato quieto
     * fuera de lo plantado, del fuego y del objeto, y una luz válida. Aquí, todos los días y en los dos cuadros.
     */
    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosDosCuadros() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaCueva.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Lugar>()
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 14.7, 17.0, 20.5, 21.0, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaCueva.anclas(m); vistos += EscenaCueva.lugar(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                assertEquals((suelo.x + suelo.hasta) / 2f, gatoQuieto(suelo).x, 0.01f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo != TipoAncla.SUELO && a.tipo != TipoAncla.MIRADOR && a.tipo != TipoAncla.ADORNO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val plantado = EscenaCueva.estorbos(m)
                for (x in plantado) assertTrue("$d $h: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in plantado) assertTrue("$d $h: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$d $h: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
                // Solo se echa junto a un fuego con llama.
                assertEquals("$d $h", EscenaCueva.lumbre(m) >= 0.45f, anclas.any { it.tipo == TipoAncla.FUEGO })
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaCueva.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
                if (EscenaCueva.lugar(m) == Lugar.MESETA) assertTrue("$d $h", MesetaCueva.calor(m, Offset(suelo.x, suelo.y)) in 0f..1f)
            }
        }
        assertEquals(setOf(Lugar.ABRIGO, Lugar.MESETA), vistos)
    }

    @Test fun lasManadasCambianConElDia() {
        val enElValle = HashSet<Manada>(); val enLaMeseta = HashSet<Manada>(); var ninguna = 0; var dias = 0
        cadaDia { _, m -> val x = EscenaCueva.manada(m); dias++; if (x == Manada.NINGUNA) ninguna++; if (EscenaCueva.lugar(m) == Lugar.ABRIGO) enElValle += x else enLaMeseta += x }
        assertEquals(Manada.entries.toSet(), enElValle); assertTrue(enLaMeseta.containsAll(listOf(Manada.NINGUNA, Manada.CABALLOS, Manada.BISONTES, Manada.SAIGAS)))
        assertTrue("hay manada entre la mitad y las tres cuartas partes de los días ($ninguna de $dias sin ella)", ninguna > dias / 4 && ninguna < dias / 2)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosCueva.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun elFuegoSeMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaCueva.seMueve(m)) }
        val a = momento("2028-12-20", 13.0); val b = momento("2028-12-20", 13.0)
        assertEquals(EscenaCueva.lugar(a), EscenaCueva.lugar(b)); assertEquals(EscenaCueva.friso(a), EscenaCueva.friso(b), 0f); assertEquals(EscenaCueva.manada(a), EscenaCueva.manada(b)); assertEquals(EscenaCueva.anclas(a).map { it.x to it.y }, EscenaCueva.anclas(b).map { it.x to it.y })
    }
}
