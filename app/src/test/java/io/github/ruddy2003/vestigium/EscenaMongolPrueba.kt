package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Colgado
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Faena
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Tienda
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosMongol
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * Los dos cuadros de Mongol (tanda S12) cuentan lo mismo que el cuento de Bat: dónde está el campamento (en el valle de
 * invierno o en el llano del verano), qué tiendas hay, cuántos animales, la hierba, la nieve y el río, las faenas y los
 * días señalados, y lo que hay encendido a cada hora salen de la ficha del día, y aquí se comparan con su calendario
 * (`AnioG4.kt`, `BAT`) todos los días de 2026 a 2032.
 */
class EscenaMongolPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "pastor_mongol")!!, hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)

    @Test fun laEscenaEsLaDeBat() {
        assertEquals(listOf("pastor_mongol"), EscenaMongol.vidas)
        assertEquals("pastor_mongol", EscenaMongol.vida("2027-05-10"))
        assertTrue(EscenaMongol.tallaDelGato in 0.8f..1.8f)
        assertEquals("mongol", OtrasVidas.porId("pastor_mongol")!!.tema)
        assertEquals(setOf(1, 2, 3, 4, 5), (2027..2031).map { momento("$it-06-15").ficha.temporada }.toSet())
    }

    @Test fun estaDondeDiceElCalendario() {
        val dias = mutableMapOf<Pair<Int, Lugar>, Int>()
        cadaDia { d, m ->
            val esperado = if (m.ficha.estado == "verano") Lugar.VERANO else Lugar.VALLE
            assertEquals(d.toString(), esperado, EscenaMongol.lugar(m))
            // El verano del calendario va del 25 de mayo (la mudanza) al 24 de septiembre, todos los años.
            assertEquals(d.toString(), md(d) >= "05-25" && md(d) <= "09-24", EscenaMongol.lugar(m) == Lugar.VERANO)
            dias[d.year to esperado] = (dias[d.year to esperado] ?: 0) + 1
        }
        for (a in 2026..2032) { assertEquals("$a", 123, dias[a to Lugar.VERANO]); assertEquals("$a", if (a % 4 == 0) 243 else 242, dias[a to Lugar.VALLE]) }
        assertEquals(Lugar.VALLE, EscenaMongol.lugar(momento("2027-05-24"))); assertEquals(Lugar.VERANO, EscenaMongol.lugar(momento("2027-05-25")))
        assertEquals(Lugar.VERANO, EscenaMongol.lugar(momento("2027-09-24"))); assertEquals(Lugar.VALLE, EscenaMongol.lugar(momento("2027-09-25")))
    }

    @Test fun lasTiendasAparecenYDesaparecenDesdeSuDia() {
        cadaDia { d, m ->
            val tiendas = EscenaMongol.tiendas(m)
            // Desde 2032 se repite la temporada 5: como en el cuento, Temür vuelve a llegar cada 28 de julio.
            val madre = d < LocalDate.of(2030, 3, 8); val temur = d.year >= 2031 && md(d) >= "07-28"
            assertEquals("$d: la tienda de la madre", madre, Tienda.MADRE in tiendas)
            assertEquals("$d: la tienda de Temür", temur, Tienda.TEMUR in tiendas)
            assertEquals("$d: seis tiendas, cinco sin la madre, seis con Temür", 5 + (if (madre) 1 else 0) + (if (temur) 1 else 0), tiendas.size)
            for (t in listOf(Tienda.BAT, Tienda.BOLAD, Tienda.MADRE_BOLAD, Tienda.CHAMAN, Tienda.VECINO)) assertTrue("$d $t", t in tiendas)
            assertEquals("$d: el sitio quemado, solo en el valle y desde que muere", !madre && EscenaMongol.lugar(m) == Lugar.VALLE, EscenaMongol.sitioDeLaMadre(m))
            assertEquals("$d: la madre enferma no sale de su tienda", d >= LocalDate.of(2029, 1, 30) && madre, EscenaMongol.madreEnferma(m))
            // La cuna de Saran va en la carreta desde que nace hasta que monta sola; el potro blanco, desde que nace.
            assertEquals("$d: la cuna", (d >= LocalDate.of(2028, 3, 12) && d < LocalDate.of(2031, 5, 5)) || (d.year >= 2032 && md(d) < "05-05"), EscenaMongol.cuna(m))
            // (En 2026, el avance, la temporada 1 ya cuenta «desde su día», como el cuento.)
            assertEquals("$d: el potro blanco", d.year >= 2028 || md(d) >= "05-02", EscenaMongol.potroBlanco(m))
        }
        // En 2026 (el avance de la temporada 1) la madre vive en su tienda y aún no ha vuelto Temür.
        assertTrue(Tienda.MADRE in EscenaMongol.tiendas(momento("2026-12-31"))); assertFalse(Tienda.TEMUR in EscenaMongol.tiendas(momento("2026-12-31")))
    }

    @Test fun elRebanoYLaHierbaSiguenSusArcos() {
        cadaDia { d, m ->
            val arco = m.ficha.arcos["rebano"]!!.toFloat(); val pasto = m.ficha.arcos["pasto"]!!.toFloat()
            assertEquals("$d", arco.coerceIn(0f, 1f), EscenaMongol.rebano(m), 0.0001f); assertEquals("$d", pasto.coerceIn(0f, 1f), EscenaMongol.pasto(m), 0.0001f)
            assertTrue("$d", EscenaMongol.ovejas(m) in 20..54 && EscenaMongol.caballos(m) in 4..10)
            assertTrue("$d", EscenaMongol.flores(m) in 0f..1f)
            if (pasto <= 0.45f) assertEquals("$d: sin hierba alta no hay flores", 0f, EscenaMongol.flores(m), 0f)
            if (md(d) >= "11-01" || md(d) <= "04-15") assertEquals("$d: el pasto en cero", 0f, EscenaMongol.pasto(m), 0.0001f)
        }
        // El invierno malo de la temporada 4: entre la costra de hielo y el fondo, el rebaño se ve mermado.
        assertTrue(EscenaMongol.ovejas(momento("2030-02-14")) - EscenaMongol.ovejas(momento("2030-03-21")) >= 15)
        assertTrue(EscenaMongol.caballos(momento("2030-02-14")) > EscenaMongol.caballos(momento("2030-03-21")))
        assertTrue(EscenaMongol.ovejas(momento("2031-12-31")) > EscenaMongol.ovejas(momento("2030-03-21")))
    }

    @Test fun laNieveYElRioNoContradicenAlCuento() {
        var nevados = 0
        cadaDia { d, m ->
            val k = EscenaMongol.nieve(m); val h = EscenaMongol.hielo(m)
            assertTrue("$d", k in 0f..1f && h in 0f..1f)
            if (EscenaMongol.lugar(m) == Lugar.VERANO) { assertEquals("$d: en el llano del verano no hay nieve", 0f, k, 0f); assertEquals("$d", 0f, h, 0f) }
            else {
                if (m.con("nieve")) assertEquals("$d: la costra de hielo", 1f, k, 0f)
                else if (m.ficha.clima == Clima.NIEVE) { assertTrue("$d", k >= 0.85f); nevados++ }
                // El pasto en cero del 1 de noviembre al 15 de abril: «parda y seca, o blanca de nieve».
                if (md(d) >= "05-01" && md(d) < "10-15" && m.ficha.clima != Clima.NIEVE) assertEquals("$d: sin nieve fuera del invierno", 0f, k, 0f)
            }
            assertEquals("$d", h >= 0.5f, EscenaMongol.rioHelado(m))
            if (EscenaMongol.deshielo(m)) assertTrue("$d", d.monthValue == 4 && d.dayOfMonth in 5..24)
        }
        assertTrue(nevados > 30)
        for (a in 2026..2032) {
            assertTrue("$a", EscenaMongol.nieve(momento("$a-01-15")) >= 0.85f); assertTrue("$a", EscenaMongol.rioHelado(momento("$a-01-15")))
            assertTrue("$a: «descalzo en la nieve dura», de noviembre a marzo", EscenaMongol.nieve(momento("$a-12-20")) >= 0.85f && EscenaMongol.nieve(momento("$a-03-10")) >= 0.5f)
            // La parición: «corderos en la nieve», del 25 de marzo al 13 de abril.
            for (dia in listOf("03-25", "04-01", "04-13")) assertTrue("$a-$dia", EscenaMongol.nieve(momento("$a-$dia")) >= 0.1f)
            // La matanza: «ya hiela de día».
            assertTrue("$a", EscenaMongol.nieve(momento("$a-11-28")) >= 0.7f && EscenaMongol.rioHelado(momento("$a-11-28")))
            // El día que bajan al valle: sin nieve, pero con las primeras heladas a primera hora.
            val baja = momento("$a-09-25", 7.0); assertEquals("$a", 0f, EscenaMongol.nieve(baja), 0f); assertTrue("$a", EscenaMongol.helada(baja)); assertFalse("$a", EscenaMongol.helada(momento("$a-09-25", 13.0)))
            assertFalse("$a", EscenaMongol.rioHelado(momento("$a-10-10"))); assertFalse("$a", EscenaMongol.rioHelado(momento("$a-05-10"))); assertTrue("$a", EscenaMongol.deshielo(momento("$a-04-10")))
        }
        assertEquals(1f, EscenaMongol.nieve(momento("2030-02-15")), 0f)
    }

    @Test fun losDiasSenaladosSeVenSuDiaYNoLaVispera() {
        for (a in 2026..2032) {
            // Las mudanzas: el día del sitio adonde llegan, con las carretas cargadas y las tiendas que se arman por la tarde.
            for (dia in listOf("05-25", "09-25")) {
                val m7 = momento("$a-$dia", 7.0); val m19 = momento("$a-$dia", 19.0)
                assertTrue("$a-$dia", EscenaMongol.partida(m7) && EscenaMongol.carretasCargadas(m7))
                assertTrue("$a-$dia", EscenaMongol.armado(m7) < 0.01f); assertEquals("$a-$dia", 1f, EscenaMongol.armado(m19), 0f)
                assertEquals("$a-$dia", 0f, EscenaMongol.fuegoDentro(m7), 0f); assertEquals(Puerta.ABIERTA, EscenaMongol.puerta(m7)); assertFalse(EscenaMongol.odre(m7))
            }
            assertFalse("$a", EscenaMongol.partida(momento("$a-05-24"))); assertEquals(1f, EscenaMongol.armado(momento("$a-05-26", 7.0)), 0f)
            // Las faenas, por su día (todas llevan la misma clave «cosecha»).
            for ((faena, desde, hasta) in listOf(Triple(Faena.PARICION, "03-25", "04-13"), Triple(Faena.ESQUILA, "06-25", "07-04"), Triple(Faena.FIELTRO, "09-05", "09-11"), Triple(Faena.CAZA, "11-10", "11-19"), Triple(Faena.MATANZA, "11-28", "11-30"))) {
                assertEquals("$a-$desde", faena, EscenaMongol.faena(momento("$a-$desde"))); assertEquals("$a-$hasta", faena, EscenaMongol.faena(momento("$a-$hasta")))
                assertTrue("$a $faena", EscenaMongol.faena(momento(LocalDate.parse("$a-$desde").minusDays(1).toString())) != faena)
                assertTrue("$a $faena", EscenaMongol.faena(momento(LocalDate.parse("$a-$hasta").plusDays(1).toString())) != faena)
            }
            assertEquals(Colgado.LANA, EscenaMongol.colgado(momento("$a-06-28"))); assertEquals(Colgado.FIELTRO, EscenaMongol.colgado(momento("$a-09-07")))
            assertTrue(EscenaMongol.carretasCargadas(momento("$a-06-28"))); assertTrue(EscenaMongol.carretasCargadas(momento("$a-11-29")))
            // El ovoo, la primera leche y la noche más larga.
            assertTrue(EscenaMongol.diaDelOvoo(momento("$a-07-12"))); assertFalse(EscenaMongol.diaDelOvoo(momento("$a-07-11"))); assertEquals(Colgado.CINTAS, EscenaMongol.colgado(momento("$a-07-12")))
            assertTrue(EscenaMongol.primeraLeche(momento("$a-06-16"))); assertFalse(EscenaMongol.primeraLeche(momento("$a-06-17")))
            assertEquals(Tienda.MADRE_BOLAD, EscenaMongol.dondeSeJuntan(momento("$a-12-22", 20.0))); assertNull(EscenaMongol.dondeSeJuntan(momento("$a-12-22", 10.0))); assertNull(EscenaMongol.dondeSeJuntan(momento("$a-12-21", 20.0)))
            assertTrue(EscenaMongol.fuegoDentro(momento("$a-12-22", 20.0)) <= 0.2f)
            // El mes blanco: fiesta, en el valle; la puerta entreabierta de día, por las visitas.
            val blanco = momento("$a-${listOf("02-06", "02-06", "01-25", "01-14", "02-02", "01-22", "01-22")[a - 2026]}", 11.0)
            assertTrue("$a", EscenaMongol.fiesta(blanco)); assertEquals(Lugar.VALLE, EscenaMongol.lugar(blanco)); assertEquals(Puerta.ENTREABIERTA, EscenaMongol.puerta(blanco)); assertEquals(Colgado.NADA, EscenaMongol.colgado(blanco))
        }
        // Las cintas del ovoo crecen cada 12 de julio y no se quitan.
        assertTrue(EscenaMongol.cintasDelOvoo(momento("2027-07-12")) > EscenaMongol.cintasDelOvoo(momento("2027-07-11")))
        var antes = 0; cadaDia { d, m -> val c = EscenaMongol.cintasDelOvoo(m); if (d.year in 2027..2031) { assertTrue("$d", c >= antes); antes = c } }
        // Las noticias de la guerra traen un caballo ajeno; los lutos cierran la puerta y no cuelgan nada.
        for (dia in listOf("2027-04-20", "2027-05-15", "2028-07-01", "2029-03-05", "2030-09-15")) { val m = momento(dia); assertTrue(dia, EscenaMongol.guerra(m) && EscenaMongol.caballoAjeno(m)) }
        assertTrue(EscenaMongol.caballoAjeno(momento("2027-07-20"))); assertFalse(EscenaMongol.caballoAjeno(momento("2027-07-21")))
        for (dia in listOf("2030-03-08", "2030-05-05", "2031-03-01")) { val m = momento(dia, 20.0); assertTrue(dia, EscenaMongol.luto(m)); assertEquals(dia, Puerta.CERRADA, EscenaMongol.puerta(m)) }
        assertEquals(Colgado.CINTAS, EscenaMongol.colgado(momento("2031-07-28"))); assertTrue(EscenaMongol.fiesta(momento("2031-07-28")))
        // Los sucesos que dejan algo a la vista.
        assertTrue(EscenaMongol.pielDeLobo(momento("2028-12-05"))); assertTrue(EscenaMongol.liebre(momento("2029-06-05"))); assertTrue(EscenaMongol.carrera(momento("2028-08-08"))); assertTrue(EscenaMongol.carrera(momento("2031-08-25")))
        assertTrue(EscenaMongol.monjeDePaso(momento("2028-04-25"))); assertTrue(EscenaMongol.fuegoDeMonkh(momento("2030-07-25", 23.0))); assertFalse(EscenaMongol.fuegoDeMonkh(momento("2030-07-25", 10.0)))
    }

    @Test fun loEncendidoSigueElHorarioDeBat() {
        cadaDia { d, _ ->
            for (h in listOf(3.0, 5.5, 7.0, 12.5, 13.0, 20.0, 21.0, 23.0)) {
                val m = momento(d.toString(), h)
                assertTrue("$d $h", EscenaMongol.fuegoDentro(m) in 0f..1f && EscenaMongol.fuegoFuera(m) in 0f..1f && EscenaMongol.ovejasCerca(m) in 0f..1f)
                val valle = EscenaMongol.lugar(m) == Lugar.VALLE
                // De noche las ovejas están apretadas cerca de las tiendas; el Negro, atado junto a la puerta.
                if (h == 3.0 || h == 21.0) { assertEquals("$d $h", 1f, EscenaMongol.ovejasCerca(m), 0f); assertTrue("$d $h", EscenaMongol.negroAtado(m)) }
                if (h == 7.0 || h == 13.0) assertFalse("$d $h: de día va montado en el Negro", EscenaMongol.negroAtado(m))
                // La puerta: en el verano, abierta salvo con lluvia; en el valle, cerrada por el frío salvo a la hora de la gente.
                val p = EscenaMongol.puerta(m)
                if (EscenaMongol.armado(m) >= 1f && !EscenaMongol.luto(m)) {
                    if (!valle) assertEquals("$d $h", if (m.ficha.clima == Clima.LLUVIA || m.ficha.clima == Clima.TORMENTA) Puerta.ENTREABIERTA else Puerta.ABIERTA, p)
                    else if (h == 20.0 || h == 21.0) assertEquals("$d $h", Puerta.ENTREABIERTA, p)
                    else if (!EscenaMongol.fiesta(m) && (h == 3.0 || h == 7.0 || h == 13.0 || h == 23.0)) assertEquals("$d $h", Puerta.CERRADA, p)
                }
                // Las yeguas, en la cuerda del alba al anochecer, y solo en el verano. El odre, solo en el verano.
                assertEquals("$d $h", !valle && h >= 5.0 && h < 21.0, EscenaMongol.yeguasEnLaCuerda(m))
                if (valle) { assertFalse(EscenaMongol.odre(m)); assertEquals(Colgado.NADA, EscenaMongol.colgado(m)) }
                // A la hora de la gente, en el valle, arde el fuego de la tienda (salvo la noche que se juntan en otra).
                if (valle && h == 20.0 && EscenaMongol.dondeSeJuntan(m) == null && EscenaMongol.armado(m) >= 1f) assertEquals("$d $h", 1f, EscenaMongol.fuegoDentro(m), 0f)
                if (!valle && h == 21.0) assertEquals("$d $h: el fuego del campamento, al anochecer", 1f, EscenaMongol.fuegoFuera(m), 0f)
            }
        }
        // Un día corriente del valle: brasas de madrugada, fuego vivo al alba y a la hora de la gente, rescoldo de día.
        val dia = "2028-01-10"
        assertTrue(EscenaMongol.fuegoDentro(momento(dia, 3.0)) < EscenaMongol.fuegoDentro(momento(dia, 5.5)))
        assertTrue(EscenaMongol.fuegoDentro(momento(dia, 10.0)) < EscenaMongol.fuegoDentro(momento(dia, 20.0)))
        assertEquals(0.15f, EscenaMongol.ovejasCerca(momento(dia, 10.0)), 0f)
        assertEquals(0f, EscenaMongol.fuegoFuera(momento(dia, 10.0)), 0f); assertTrue(EscenaMongol.fuegoFuera(momento("2028-11-29", 10.0)) >= 0.45f)
    }

    /**
     * Lo mismo que comprueban `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` y `EscenaCuevaPrueba` en sus cuadros: suelo a
     * todas horas y bastante largo, lo plantado dentro del tramo, el gato quieto fuera de lo plantado, del fuego y del
     * objeto, y una luz válida. Aquí, todos los días y en los dos cuadros.
     */
    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosDosCuadros() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaMongol.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Lugar>()
        cadaDia { d, _ ->
            for (h in listOf(3.0, 7.0, 13.0, 17.0, 20.5, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaMongol.anclas(m); vistos += EscenaMongol.lugar(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo != TipoAncla.SUELO && a.tipo != TipoAncla.MIRADOR && a.tipo != TipoAncla.ADORNO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val plantado = EscenaMongol.estorbos(m)
                for (x in plantado) assertTrue("$d $h: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta)
                val quieto = gatoQuieto(suelo, anclas, talla, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in plantado) assertTrue("$d $h: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$d $h: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
                // En el valle se echa junto al hogar de fuera solo si arde; en el verano el fuego del campamento queda lejos de su paso.
                val fuego = anclas.any { it.tipo == TipoAncla.FUEGO }
                if (EscenaMongol.lugar(m) == Lugar.VALLE) assertEquals("$d $h", EscenaMongol.fuegoFuera(m) >= 0.45f, fuego) else assertFalse("$d $h", fuego)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaMongol.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
        }
        assertEquals(setOf(Lugar.VALLE, Lugar.VERANO), vistos)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(sortedSetOf("arco", "lazo", "silla", "ger", "odre", "cuchillo", "eslabon", "morin_khuur", "cuenco", "deel", "argal", "copa"), delCalendario)
    }

    @Test fun elHumoSeMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaMongol.seMueve(m)) }
        val a = momento("2029-12-20", 13.0); val b = momento("2029-12-20", 13.0)
        assertEquals(EscenaMongol.lugar(a), EscenaMongol.lugar(b)); assertEquals(EscenaMongol.tiendas(a), EscenaMongol.tiendas(b)); assertEquals(EscenaMongol.nieve(a), EscenaMongol.nieve(b), 0f)
        assertEquals(EscenaMongol.anclas(a).map { it.x to it.y }, EscenaMongol.anclas(b).map { it.x to it.y })
    }

    /**
     * Los doce objetos tienen su dibujo y su sitio: los del calendario son los de [ObjetosMongol.TODOS]; siete son de los
     * grandes (van de pie o apoyados) y cinco de los chicos (van sobre el soporte), y cada día el cuadro sabe cuál le toca.
     */
    @Test fun cadaObjetoDelDiaEsGrandeOChicoYTieneSuDibujo() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { d, m ->
            val o = m.ficha.objeto ?: return@cadaDia
            delCalendario += o
            assertEquals("$d: $o", o in ObjetosMongol.GRANDES, ObjetosMongol.esGrande(m))
        }
        assertEquals(ObjetosMongol.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosMongol.TODOS.containsAll(ObjetosMongol.GRANDES))
        assertEquals(setOf("arco", "lazo", "silla", "ger", "morin_khuur", "deel", "argal"), ObjetosMongol.GRANDES)
        assertEquals(5, ObjetosMongol.TODOS.count { !ObjetosMongol.esGrande(it) })
        assertFalse(ObjetosMongol.esGrande(null as String?))
    }

    /** Las posturas del caballo que usan los cuadros siguen ahí (las piden por su nombre). */
    @Test fun elCaballoTieneSusPosturas() {
        assertEquals(listOf("DE_PIE", "PASTANDO", "MIRANDO_ATRAS", "DESCANSO", "ECHADO", "MAMANDO"), PiezasMongol.Postura.entries.map { it.name })
    }
}
