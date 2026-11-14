package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.CampamentoSeda
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaSeda.Paisaje
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosSeda
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * Los cuadros de la Ruta de la Seda (tanda S7) cuentan lo mismo que el cuento de Nanai-vandak: dónde está (en el
 * camino, en Chang'an o en Samarcanda), qué paisaje cruza, si la caravana marcha o descansa y lo que le va pasando
 * salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaSedaPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "mercader_sogdiano")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeNanai() {
        assertEquals(listOf("mercader_sogdiano"), EscenaSeda.vidas)
        assertEquals("mercader_sogdiano", EscenaSeda.vida("2027-05-10"))
        assertTrue(EscenaSeda.tallaDelGato in 0.8f..1.8f)
    }

    @Test fun estaDondeDiceElCalendario() {
        val dias = mutableMapOf<Pair<Int, Lugar>, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "changan" -> Lugar.CHANGAN; "samarcanda" -> Lugar.SAMARCANDA; else -> Lugar.CAMINO }
            assertEquals(d.toString(), esperado, EscenaSeda.lugar(m))
            dias[d.year to esperado] = (dias[d.year to esperado] ?: 0) + 1
        }
        // El verano en Chang'an, las temporadas 1, 3 y 5; en Samarcanda, la 2 y la 4. El resto del año, en el camino.
        for (anio in listOf(2026, 2027, 2029, 2031, 2032)) { assertTrue("$anio", (dias[anio to Lugar.CHANGAN] ?: 0) in 140..170); assertEquals("$anio", null, dias[anio to Lugar.SAMARCANDA]) }
        for (anio in listOf(2028, 2030)) { assertTrue("$anio", (dias[anio to Lugar.SAMARCANDA] ?: 0) in 140..170); assertEquals("$anio", null, dias[anio to Lugar.CHANGAN]) }
        for (anio in 2026..2032) assertTrue("$anio", (dias[anio to Lugar.CAMINO] ?: 0) > 190)
        assertEquals(Lugar.CAMINO, EscenaSeda.lugar(momento("2027-04-14"))); assertEquals(Lugar.CHANGAN, EscenaSeda.lugar(momento("2027-04-15")))
        assertEquals(Lugar.CHANGAN, EscenaSeda.lugar(momento("2027-06-10"))); assertEquals(Lugar.CAMINO, EscenaSeda.lugar(momento("2027-12-20")))
        assertEquals(Lugar.CAMINO, EscenaSeda.lugar(momento("2028-04-24"))); assertEquals(Lugar.SAMARCANDA, EscenaSeda.lugar(momento("2028-04-25"))); assertEquals(Lugar.SAMARCANDA, EscenaSeda.lugar(momento("2030-06-10")))
    }

    @Test fun elPaisajeEsElDeLaRuta() {
        // Los cortes son los del cuento (`PiezasNanai.ENTORNOS`): valles y montañas hasta 0.35, desierto y oasis hasta 0.72, y de ahí el corredor y el loess.
        assertEquals(Paisaje.MONTANA, EscenaSeda.paisajeDe(0f)); assertEquals(Paisaje.MONTANA, EscenaSeda.paisajeDe(0.34f)); assertEquals(Paisaje.DESIERTO, EscenaSeda.paisajeDe(0.35f))
        assertEquals(Paisaje.DESIERTO, EscenaSeda.paisajeDe(0.71f)); assertEquals(Paisaje.LOESS, EscenaSeda.paisajeDe(0.72f)); assertEquals(Paisaje.LOESS, EscenaSeda.paisajeDe(1f))
        val vistos = mutableMapOf<Int, MutableSet<Paisaje>>()
        cadaDia { d, m ->
            val ruta = m.ficha.arcos["ruta"]!!.toFloat()
            assertEquals(d.toString(), ruta, EscenaSeda.ruta(m), 0.0001f); assertEquals(d.toString(), EscenaSeda.paisajeDe(ruta), EscenaSeda.paisaje(m))
            if (EscenaSeda.lugar(m) == Lugar.CAMINO) vistos.getOrPut(d.year) { mutableSetOf() } += EscenaSeda.paisaje(m)
            // En las ciudades está al final de la ruta: Chang'an en 1 y Samarcanda en 0. (En 2032, cuando la temporada
            // 5 se repite, el calendario lo da por llegado doce días antes de que el arco toque 1: por eso el margen.)
            if (EscenaSeda.lugar(m) == Lugar.CHANGAN) assertTrue("$d $ruta", ruta >= 0.9f)
            if (EscenaSeda.lugar(m) == Lugar.SAMARCANDA) assertTrue("$d $ruta", ruta <= 0.1f)
        }
        // Los años que va a Chang'an (temporadas 1, 3 y 5) cruza el desierto y el loess; los que vuelve a Samarcanda (2 y
        // 4), el desierto y las montañas. Entre todos, los tres paisajes.
        for (anio in listOf(2026, 2027, 2029, 2031, 2032)) assertEquals("$anio", setOf(Paisaje.DESIERTO, Paisaje.LOESS), vistos[anio])
        for (anio in listOf(2028, 2030)) assertEquals("$anio", setOf(Paisaje.MONTANA, Paisaje.DESIERTO), vistos[anio])
        assertEquals(Paisaje.DESIERTO, EscenaSeda.paisaje(momento("2027-12-20"))); assertEquals(Paisaje.MONTANA, EscenaSeda.paisaje(momento("2028-03-25"))); assertEquals(Paisaje.LOESS, EscenaSeda.paisaje(momento("2029-04-10")))
        // La mezcla de paisajes suma siempre 1, manda el que dice el cuento y no da saltos de un punto de la ruta al siguiente.
        var r = 0f
        while (r <= 1f) {
            val p = EscenaSeda.pesos(r); val q = EscenaSeda.pesos((r + 0.01f).coerceAtMost(1f))
            assertEquals("$r", 1f, p.montana + p.desierto + p.loess, 0.001f)
            for (x in listOf(p.montana, p.desierto, p.loess, p.alto, p.capital)) assertTrue("$r", x in 0f..1f)
            val manda = when (EscenaSeda.paisajeDe(r)) { Paisaje.MONTANA -> p.montana; Paisaje.DESIERTO -> p.desierto; Paisaje.LOESS -> p.loess }
            assertTrue("$r: $manda", manda >= 0.49f)
            assertTrue("$r", abs(p.montana - q.montana) < 0.12f && abs(p.desierto - q.desierto) < 0.12f && abs(p.loess - q.loess) < 0.12f)
            r += 0.01f
        }
    }

    @Test fun laCaravanaMarchaALasHorasDeCaminoYLaLumbreArdeEnLosAltos() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 4.4, 7.0, 10.0, 13.4, 15.0, 19.0, 21.0, 23.8)) {
                val m = momento(d.toString(), h); val lumbre = EscenaSeda.lumbre(m)
                assertTrue("$d $h", lumbre in 0f..1f)
                if (EscenaSeda.enMarcha(m)) { assertEquals("$d $h", Lugar.CAMINO, EscenaSeda.lugar(m)); assertEquals("$d $h", 0f, lumbre, 0f); assertFalse("$d $h", m.con("nieve")); assertFalse("$d $h", EscenaSeda.tormentaNegra(m)) }
                if (EscenaSeda.lugar(m) == Lugar.CHANGAN) { assertEquals("$d $h", 0f, lumbre, 0f); assertFalse("$d $h", EscenaSeda.enMarcha(m)) }
                if (EscenaSeda.lugar(m) == Lugar.SAMARCANDA) assertFalse("$d $h", EscenaSeda.enMarcha(m))
                if (EscenaSeda.lugar(m) != Lugar.CHANGAN && !EscenaSeda.enMarcha(m) && !EscenaSeda.tormentaNegra(m)) assertTrue("$d $h", lumbre > 0f)
            }
        }
        // Un día corriente del camino: anda por la mañana y por la tarde, come a mediodía y de noche queda el rescoldo.
        assertTrue(EscenaSeda.enMarcha(momento("2027-11-05", 10.0))); assertFalse(EscenaSeda.enMarcha(momento("2027-11-05", 13.4))); assertTrue(EscenaSeda.enMarcha(momento("2027-11-05", 15.0))); assertFalse(EscenaSeda.enMarcha(momento("2027-11-05", 19.0)))
        assertEquals(1f, EscenaSeda.lumbre(momento("2027-11-05", 19.0)), 0f); assertTrue(EscenaSeda.lumbre(momento("2027-11-05", 3.0)) in 0.2f..0.5f)
    }

    @Test fun loQueElCuentoCambiaSeVeDesdeSuDia() {
        // Farn sale con él de Samarcanda el 28 de septiembre de la temporada 2 y se queda en Chang'an el 28 de septiembre de la 3.
        assertFalse(EscenaSeda.conFarn(momento("2028-09-27"))); assertTrue(EscenaSeda.conFarn(momento("2028-09-28"))); assertTrue(EscenaSeda.conFarn(momento("2029-03-01"))); assertTrue(EscenaSeda.conFarn(momento("2029-09-27"))); assertFalse(EscenaSeda.conFarn(momento("2029-09-28")))
        assertFalse(EscenaSeda.conFarn(momento("2027-06-01"))); assertFalse(EscenaSeda.conFarn(momento("2031-06-01")))
        // El camello blanco, desde que lo compra en Kashgar (5 de marzo de la temporada 4).
        assertFalse(EscenaSeda.camelloBlanco(momento("2030-03-04"))); assertTrue(EscenaSeda.camelloBlanco(momento("2030-03-05"))); assertTrue(EscenaSeda.camelloBlanco(momento("2032-01-05")))
        assertTrue(EscenaSeda.camelloMuerto(momento("2027-01-20"))); assertFalse(EscenaSeda.camelloMuerto(momento("2027-01-21")))
        assertTrue(EscenaSeda.mogao(momento("2027-11-20"))); assertTrue(EscenaSeda.mogao(momento("2030-12-20"))); assertFalse(EscenaSeda.mogao(momento("2028-11-20")))
        // La nieve en el paso y la tormenta de arena negra paran la caravana.
        for (dia in listOf("2027-12-15", "2028-11-01", "2029-10-25")) { assertTrue(dia, EscenaSeda.nevado(momento(dia))); assertFalse(dia, EscenaSeda.enMarcha(momento(dia, 10.0))) }
        for (anio in 2027..2031) { val m = momento("$anio-03-20"); assertTrue("$anio", EscenaSeda.tormentaNegra(m)); assertEquals("$anio", Lugar.CAMINO, EscenaSeda.lugar(m)); assertFalse("$anio", EscenaSeda.enMarcha(m)); assertEquals("$anio", 0f, EscenaSeda.lumbre(m), 0f) }
        // Las fiestas del camino (el año nuevo y las linternas) y las de las ciudades (las dos bodas).
        assertTrue(EscenaSeda.fiesta(momento("2027-02-11"))); assertEquals(Lugar.CAMINO, EscenaSeda.lugar(momento("2027-02-11"))); assertTrue(EscenaSeda.linternas(momento("2027-02-25")))
        assertTrue(EscenaSeda.fiesta(momento("2028-07-10"))); assertEquals(Lugar.SAMARCANDA, EscenaSeda.lugar(momento("2028-07-10")))
        assertTrue(EscenaSeda.fiesta(momento("2031-07-10"))); assertEquals(Lugar.CHANGAN, EscenaSeda.lugar(momento("2031-07-10")))
        // El día que los bandidos atacan la caravana hay armas a la puerta; la noticia de una batalla lejana no se ve.
        assertTrue(EscenaSeda.guerra(momento("2030-01-10"))); assertFalse(EscenaSeda.guerra(momento("2030-01-11"))); assertFalse(EscenaSeda.guerra(momento("2028-08-15")))
    }

    @Test fun elMercadoDelOesteAbreAMediodiaYCierraAntesDeAnochecer() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 10.0, 13.0, 17.0, 20.0)) {
                val m = momento(d.toString(), h)
                if (EscenaSeda.mercadoAbierto(m)) { assertEquals("$d $h", Lugar.CHANGAN, EscenaSeda.lugar(m)); assertTrue("$d $h", h >= 12.0 && h < 18.6); assertFalse("$d $h", EscenaSeda.crecida(m)) }
            }
        }
        assertFalse(EscenaSeda.mercadoAbierto(momento("2027-06-10", 10.0))); assertTrue(EscenaSeda.mercadoAbierto(momento("2027-06-10", 13.0))); assertTrue(EscenaSeda.mercadoAbierto(momento("2027-06-10", 17.0))); assertTrue(EscenaSeda.mercadoAbierto(momento("2027-06-10", 18.4))); assertFalse(EscenaSeda.mercadoAbierto(momento("2027-06-10", 18.7))); assertFalse(EscenaSeda.mercadoAbierto(momento("2027-06-10", 20.0)))
        assertFalse(EscenaSeda.mercadoAbierto(momento("2027-12-20", 13.0)))
        // El día que el Wei entra en el mercado no se abre.
        assertTrue(EscenaSeda.crecida(momento("2029-07-25"))); assertFalse(EscenaSeda.mercadoAbierto(momento("2029-07-25", 13.0))); assertFalse(EscenaSeda.crecida(momento("2029-07-26")))
        // Las fiestas de Chang'an caen estando allí.
        for ((dia, es) in listOf<Pair<String, (Momento) -> Boolean>>("2027-06-13" to EscenaSeda::duanwu, "2027-08-13" to EscenaSeda::qixi, "2027-09-19" to EscenaSeda::lunaDeOtono, "2029-06-20" to EscenaSeda::duanwu, "2031-09-06" to EscenaSeda::lunaDeOtono)) {
            assertTrue(dia, es(momento(dia))); assertEquals(dia, Lugar.CHANGAN, EscenaSeda.lugar(momento(dia)))
        }
        assertTrue(EscenaSeda.farolesDeLaTienda(momento("2027-06-10", 21.0))); assertFalse(EscenaSeda.farolesDeLaTienda(momento("2027-06-10", 13.0))); assertFalse(EscenaSeda.farolesDeLaTienda(momento("2027-06-10", 3.0)))
    }

    /**
     * Lo mismo que comprueba `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` para las escenas registradas en `escenaDe`
     * (esta todavía no lo está): suelo a todas horas y bastante largo, lo plantado dentro del tramo, el gato quieto
     * fuera de lo plantado, del fuego y del objeto, y una luz válida. Aquí, todos los días y en los tres lugares.
     */
    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosTresLugares() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaSeda.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 14.7, 17.0, 20.5, 21.0, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaSeda.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo != TipoAncla.SUELO && a.tipo != TipoAncla.MIRADOR && a.tipo != TipoAncla.ADORNO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val plantado = EscenaSeda.estorbos(m)
                for (x in plantado) assertTrue("$d $h: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in plantado) assertTrue("$d $h: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$d $h: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
                // Solo se echa junto a una lumbre encendida.
                val fuego = if (EscenaSeda.lugar(m) == Lugar.CHANGAN) io.github.ruddy2003.vestigium.ui.escenas.ChanganSeda.hornillo(m) else EscenaSeda.lumbre(m) > 0f
                assertEquals("$d $h", fuego, anclas.any { it.tipo == TipoAncla.FUEGO })
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = EscenaSeda.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
    }

    @Test fun laCargaVaSegunElSentidoDelViajeYElAltoCambiaConElDia() {
        // Los años de Chang'an va hacia el este la primera mitad y vuelve la segunda; los de Samarcanda, al revés.
        assertTrue(EscenaSeda.haciaElEste(momento("2027-02-01"))); assertTrue(!EscenaSeda.haciaElEste(momento("2027-11-05")))
        assertTrue(!EscenaSeda.haciaElEste(momento("2028-02-10"))); assertTrue(EscenaSeda.haciaElEste(momento("2028-11-05")))
        // Los fardos del alto son tres o cuatro (tres, si hay ropa tendida encima), y los dos casos se dan.
        val vistos = HashSet<Int>(); var tendida = 0
        cadaDia { d, m -> val n = CampamentoSeda.fardos(m); vistos.add(n); assertTrue("$d", n == 3 || n == 4); if (CampamentoSeda.ropaTendida(m)) { tendida++; assertEquals("$d", 3, n) } }
        assertEquals(setOf(3, 4), vistos); assertTrue("nunca hay ropa tendida", tendida > 100)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosSeda.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun algoSeMueveCuandoHayLumbreOFaroles() {
        assertTrue(EscenaSeda.seMueve(momento("2027-11-05", 19.0))); assertTrue(EscenaSeda.seMueve(momento("2027-11-05", 23.0))); assertTrue(EscenaSeda.seMueve(momento("2027-06-10", 21.0)))
        // El mismo momento da siempre lo mismo.
        val a = momento("2027-12-20", 13.0); val b = momento("2027-12-20", 13.0)
        assertEquals(EscenaSeda.lugar(a), EscenaSeda.lugar(b)); assertEquals(EscenaSeda.ruta(a), EscenaSeda.ruta(b), 0f); assertEquals(EscenaSeda.anclas(a).map { it.x to it.y }, EscenaSeda.anclas(b).map { it.x to it.y })
    }
}
