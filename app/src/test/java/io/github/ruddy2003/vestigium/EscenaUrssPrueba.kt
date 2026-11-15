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
import io.github.ruddy2003.vestigium.ui.escenas.CocinaUrss
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.Banderas
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.Casa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaUrss.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.LuzCocinaUrss
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosUrss
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
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
 * El cuadro de Soviético (tanda S15: la cocina de Liudmila, con su ventana al patio) cuenta lo mismo que el cuento: dónde
 * está, la casa de cada día y las cajas de la mudanza, la nevera, la nieve, el frío, las estaciones, los días señalados,
 * la fase del día y lo que está encendido a cada hora salen de la ficha del día, y aquí se comparan con su calendario
 * (`AnioG6.kt`, `LIUDMILA`) todos los días de 2026 a 2032. La mancha de sol camina con la hora y de noche hay dos luces.
 */
class EscenaUrssPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "obrera_moscu")!!, hora)
    private fun conClima(dia: String, hora: Double, clima: Clima) = Momento(FichaDelDia.de(dia, "obrera_moscu")!!.copy(clima = clima), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }
    private fun numero(m: Momento, id: String) = m.ficha.especiales.firstOrNull { it.dia.id == id }?.numero ?: 0

    @Test fun laEscenaEsLaDeLiudmila() {
        assertEquals(listOf("obrera_moscu"), EscenaUrss.vidas)
        assertEquals("obrera_moscu", EscenaUrss.vida("2027-11-10"))
        assertTrue(EscenaUrss.tallaDelGato in 0.8f..1.8f)
        assertEquals("urss", OtrasVidas.porId("obrera_moscu")!!.tema)
    }

    @Test fun estaDondeDiceElCalendario() {
        val sanatorio = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val esperado = if (m.ficha.estado == "sanatorio") Lugar.SANATORIO else Lugar.MOSCU
            assertEquals(d.toString(), esperado, EscenaUrss.lugar(m))
            assertEquals(d.toString(), Cuadro.COCINA, EscenaUrss.cuadro(m))
            assertEquals(d.toString(), esperado == Lugar.SANATORIO, EscenaUrss.provisional(m))
            assertEquals(d.toString(), esperado == Lugar.SANATORIO, EscenaUrss.visilloCorrido(m))
            if (esperado == Lugar.SANATORIO) sanatorio[d.year] = (sanatorio[d.year] ?: 0) + 1
        }
        // Tres semanas en el mar Negro en las temporadas 2 y 4: del 5 al 25 de julio de 2028 y del 10 al 30 de julio de 2030.
        assertEquals(mapOf(2028 to 21, 2030 to 21), sanatorio)
        assertEquals(Lugar.MOSCU, EscenaUrss.lugar(momento("2028-07-04"))); assertEquals(Lugar.SANATORIO, EscenaUrss.lugar(momento("2028-07-05")))
        assertEquals(Lugar.SANATORIO, EscenaUrss.lugar(momento("2028-07-25"))); assertEquals(Lugar.MOSCU, EscenaUrss.lugar(momento("2028-07-26")))
        assertEquals(Lugar.SANATORIO, EscenaUrss.lugar(momento("2030-07-10"))); assertEquals(Lugar.MOSCU, EscenaUrss.lugar(momento("2030-07-31")))
        // En el sanatorio no está ella: ni el gas encendido, ni su bolsa, ni sus botas, ni su abrigo.
        for (h in listOf(6.5, 13.0, 19.0, 21.0)) { val m = momento("2028-07-15", h); assertFalse(EscenaUrss.fogon(m)); assertFalse(EscenaUrss.bolsaColgada(m)); assertFalse(EscenaUrss.botas(m)); assertFalse(EscenaUrss.abrigoColgado(m)) }
    }

    /** La casa vieja hasta la víspera de la mudanza (25 de junio de 2029) y la nueva desde ese día, con sus cajas. */
    @Test fun laCasaDeCadaDiaYLasCajasDeLaMudanza() {
        cadaDia { d, m ->
            val nueva = d >= LocalDate.of(2029, 6, 25)
            assertEquals(d.toString(), if (nueva) Casa.NUEVA else Casa.VIEJA, EscenaUrss.casa(m))
            assertEquals(d.toString(), nueva, EscenaUrss.casaNueva(m))
            val c = EscenaUrss.cajas(m)
            assertTrue(d.toString(), c in 0..7)
            val conCajas = d >= LocalDate.of(2029, 6, 15) && d <= LocalDate.of(2029, 7, 10)
            assertEquals(d.toString(), conCajas, c > 0)
            assertEquals(d.toString(), d.year == 2029 && md(d) in "06-25".."06-27", EscenaUrss.bombillaDesnuda(m))
            // El día de la mudanza del calendario es el primero de la casa nueva, con todas las cajas.
            if (hoyEs(m, "mudanza")) { assertEquals(d.toString(), Casa.NUEVA, EscenaUrss.casa(m)); assertEquals(7, c) }
            if (hoyEs(m, "piso")) { assertEquals(d.toString(), Casa.VIEJA, EscenaUrss.casa(m)); assertEquals(2, c) }
        }
        // Se empaqueta en la casa vieja (las cajas crecen) y se vacían en la nueva (decrecen).
        assertTrue(EscenaUrss.cajas(momento("2029-06-16")) < EscenaUrss.cajas(momento("2029-06-24")))
        assertTrue(EscenaUrss.cajas(momento("2029-06-26")) > EscenaUrss.cajas(momento("2029-07-08")))
        // Desde 2032 se repite la temporada 5: la casa nueva, sin cajas.
        assertEquals(Casa.NUEVA, EscenaUrss.casa(momento("2032-06-20"))); assertEquals(0, EscenaUrss.cajas(momento("2032-06-25")))
        // Lo de fuera cambia con la casa; la cocina (el suelo del gato, la mesa y el objeto) no.
        val antes = EscenaUrss.anclas(momento("2029-06-24", 13.0)).map { Triple(it.tipo, it.x, it.y) }
        val despues = EscenaUrss.anclas(momento("2029-06-25", 13.0)).map { Triple(it.tipo, it.x, it.y) }
        assertEquals(antes.filter { it.first != TipoAncla.ADORNO }, despues.filter { it.first != TipoAncla.ADORNO })
    }

    /** La nevera llega el 20 de marzo de la temporada 1 (en 2026, el avance, y en 2027); antes, la alacena y la leche entre los cristales. */
    @Test fun laNeveraLlegaSuDia() {
        cadaDia { d, m ->
            val esperado = d.year >= 2028 || md(d) >= "03-20"
            assertEquals(d.toString(), esperado, EscenaUrss.nevera(m))
            if (hoyEs(m, "nevera")) assertTrue(d.toString(), EscenaUrss.nevera(m))
            if (EscenaUrss.lecheEntreLosCristales(m)) assertTrue(d.toString(), !esperado && (d.monthValue >= 11 || d.monthValue <= 3))
        }
        assertFalse(EscenaUrss.nevera(momento("2027-03-19"))); assertTrue(EscenaUrss.nevera(momento("2027-03-20")))
        assertTrue(EscenaUrss.lecheEntreLosCristales(momento("2027-01-20"))); assertFalse(EscenaUrss.lecheEntreLosCristales(momento("2027-12-20")))
    }

    /** La regla de la nieve (**[Aproximación]**): no contradice el cuento. */
    @Test fun laNieveNoContradiceAlCuento() {
        cadaDia { d, _ ->
            for (h in listOf(5.0, 9.0, 13.0, 21.0)) {
                val m = momento(d.toString(), h); val n = EscenaUrss.nieve(m); val clima = m.ficha.clima; val f = md(d)
                assertTrue("$d $h", n in 0f..1f)
                // «La primera nieve que no se derrite», el 8 de noviembre; antes, solo la nevada fina que no cuaja, si nieva.
                if (f in "04-15".."11-07" && !m.con("nieve")) assertEquals("$d $h", if (clima == Clima.NIEVE) 0.35f else 0f, n, 0.0001f)
                if (f == "11-08") assertTrue("$d $h", n >= 0.7f)
                // Todo el invierno, el patio blanco (aplastado si llueve).
                if (f >= "11-26" || f <= "03-01") assertTrue("$d $h: $n", n >= 0.79f)
                if (m.con("nieve")) assertEquals("$d $h", 1f, n, 0f)
                if (EscenaUrss.nieveMojada(m)) assertTrue("$d $h", clima == Clima.LLUVIA || clima == Clima.TORMENTA)
                if (EscenaUrss.pista(m)) assertTrue("$d $h", n >= 0.8f && (f >= "12-10" || f < "03-06"))
                val e = EscenaUrss.escarcha(m); assertTrue("$d $h", e in 0f..1f); if (e > 0f) assertTrue("$d $h", EscenaUrss.frio(m) >= 0.72f)
                assertTrue("$d $h", EscenaUrss.termometro(m) in 0f..1f)
            }
        }
        // El frío que nadie recuerda: el 3 de enero de 2031, todo blanco, el termómetro abajo del todo y la escarcha a media ventana.
        val frio = momento("2031-01-03", 13.0)
        assertTrue(frio.con("nieve")); assertEquals(1f, EscenaUrss.nieve(frio), 0f); assertEquals(1f, EscenaUrss.escarcha(frio), 0f)
        assertTrue(EscenaUrss.termometro(frio) < EscenaUrss.termometro(momento("2031-01-10", 13.0)))
        assertTrue(EscenaUrss.termometro(momento("2027-07-15")) > EscenaUrss.termometro(momento("2027-01-15")))
        // El deshielo de la primavera y las tiras de papel que se quitan.
        assertTrue(EscenaUrss.deshielo(momento("2027-03-28"))); assertFalse(EscenaUrss.deshielo(momento("2027-05-10")))
        assertTrue(EscenaUrss.tiras(momento("2027-01-10"))); assertFalse(EscenaUrss.tiras(momento("2027-06-10")))
    }

    @Test fun lasEstacionesDelPatio() {
        cadaDia { d, m ->
            val h = EscenaUrss.hojas(m); val a = EscenaUrss.amarillas(m); val p = EscenaUrss.pelusa(m); val b = EscenaUrss.barro(m)
            for (v in listOf(h, a, p, b)) assertTrue(d.toString(), v in 0f..1f)
            if (d.monthValue in listOf(12, 1, 2, 3)) assertEquals(d.toString(), 0f, h, 0f)
            if (d.monthValue in listOf(6, 7, 8)) assertEquals(d.toString(), 1f, h, 0f)
            if (p > 0f) assertEquals(d.toString(), 6, d.monthValue)
            // En el barrio nuevo siempre hay algo de barro, salvo con el patio nevado.
            if (EscenaUrss.casaNueva(m) && EscenaUrss.nieve(m) < 0.5f) assertTrue(d.toString(), b > 0.3f)
        }
        assertTrue(EscenaUrss.amarillas(momento("2027-10-10")) > 0.9f); assertEquals(0f, EscenaUrss.amarillas(momento("2027-07-10")), 0f)
        assertTrue(EscenaUrss.pelusa(momento("2027-06-12")) > EscenaUrss.pelusa(momento("2030-06-12")))
    }

    @Test fun losDiasSenaladosSeVenSuDia() {
        cadaDia { d, m ->
            assertEquals(d.toString(), m.con("fiesta"), EscenaUrss.fiesta(m)); assertEquals(d.toString(), m.con("desfile"), EscenaUrss.desfile(m))
            assertEquals(d.toString(), m.con("luto"), EscenaUrss.luto(m))
            val esperadas = when {
                hoyEs(m, "sesenta") -> Banderas.TODAS
                numero(m, "primero_mayo") == 1 || numero(m, "revolucion") == 1 -> Banderas.TODAS
                numero(m, "primero_mayo") == 2 -> Banderas.BALCONES
                numero(m, "revolucion") == 2 -> Banderas.FAROLAS
                hoyEs(m, "constitucion") -> Banderas.TODAS
                else -> Banderas.NINGUNA
            }
            assertEquals(d.toString(), esperadas, EscenaUrss.banderas(m))
            assertEquals(d.toString(), hoyEs(m, "anio_nuevo"), EscenaUrss.anioNuevo(m))
            assertEquals(d.toString(), hoyEs(m, "ocho_marzo"), EscenaUrss.mimosas(m)); assertEquals(d.toString(), hoyEs(m, "victoria"), EscenaUrss.claveles(m))
            assertEquals(d.toString(), hoyEs(m, "pascua_suegra"), EscenaUrss.pascua(m)); assertEquals(d.toString(), hoyEs(m, "vuelve_sanatorio"), EscenaUrss.melocotones(m))
            if (EscenaUrss.mesaDeFiesta(m)) assertTrue(d.toString(), hoyEs(m, "anio_nuevo") || numero(m, "primero_mayo") == 2 || numero(m, "revolucion") == 2)
            // El arco del plan: la raya roja del cartel de la fábrica.
            assertEquals(d.toString(), m.ficha.arcos["plan"]!!.toFloat().coerceIn(0f, 1f), EscenaUrss.plan(m), 0.0001f)
        }
        // El luto: el día que muere la suegra (20 de octubre de 2030), el espejo tapado y la vela.
        assertTrue(EscenaUrss.luto(momento("2030-10-20"))); assertFalse(EscenaUrss.luto(momento("2030-10-19")))
        // Las banderas mojadas del 8 de noviembre, el día de la primera nieve que no se derrite.
        assertEquals(Banderas.FAROLAS, EscenaUrss.banderas(momento("2027-11-08"))); assertTrue(EscenaUrss.nieve(momento("2027-11-08")) >= 0.7f)
        assertEquals(Banderas.TODAS, EscenaUrss.banderas(momento("2029-11-05")))
        // La raya del plan sube todo el año, casi arriba el 28 de diciembre, y cae a cero el 29.
        assertTrue(EscenaUrss.plan(momento("2027-12-28")) > 0.95f); assertEquals(0f, EscenaUrss.plan(momento("2027-12-29")), 0.001f)
        assertTrue(EscenaUrss.plan(momento("2027-06-01")) < EscenaUrss.plan(momento("2027-11-01")))
    }

    @Test fun loEncendidoSigueElHorario() {
        cadaDia { d, _ ->
            for (h in listOf(1.0, 6.5, 7.0, 10.0, 13.0, 17.5, 19.0, 21.0, 23.5)) {
                val m = momento(d.toString(), h); val f = EscenaUrss.fase(m)
                val esperada = when (m.hacer) { Hacer.DORMIR -> Fase.SUENO; Hacer.CUIDAR -> if (h < 12.0) Fase.MANANA else Fase.VUELTA; Hacer.LIBRE -> Fase.NOCHE; else -> Fase.SOLA }
                assertEquals("$d $h", esperada, f)
                assertEquals("$d $h", (f == Fase.MANANA || f == Fase.VUELTA) && !EscenaUrss.enElSanatorio(m), EscenaUrss.fogon(m))
                assertEquals("$d $h", f == Fase.NOCHE, EscenaUrss.televisor(m))
                if (f == Fase.SUENO) assertFalse("$d $h", EscenaUrss.lampara(m))
                if (f == Fase.NOCHE) assertTrue("$d $h", EscenaUrss.lampara(m))
                if (f == Fase.MANANA && !EscenaUrss.enElSanatorio(m)) assertTrue("$d $h", EscenaUrss.lampara(m))
                // De día la cocina está sola: a las 7:00 y a las 13:00, nada encendido.
                if (h == 7.0 || h == 13.0) { assertEquals("$d $h", Fase.SOLA, f); assertFalse("$d $h", EscenaUrss.lampara(m)); assertFalse("$d $h", EscenaUrss.fogon(m)) }
            }
        }
        // Las ventanas de enfrente se encienden una a una al caer la tarde: muchas más a las 21:00 que a las 15:00 o a las 3:00.
        fun encendidas(h: Double) = (0 until 400).count { EscenaUrss.ventanaEncendida(momento("2027-01-20", h), it) }
        assertTrue(encendidas(21.0) > 2 * encendidas(15.0)); assertTrue(encendidas(21.0) > 2 * encendidas(3.0))
        assertTrue(encendidas(18.0) in (encendidas(15.0) + 1) until encendidas(21.0))
        // Las farolas: de noche sí, a mediodía de verano no; en la tarde de invierno se encienden antes.
        assertTrue(EscenaUrss.farolas(momento("2027-01-20", 21.0))); assertFalse(EscenaUrss.farolas(conClima("2027-07-10", 13.0, Clima.DESPEJADO)))
        assertTrue(EscenaUrss.crepusculo(momento("2027-01-20", 17.5)) > 0.5f); assertEquals(0f, EscenaUrss.crepusculo(momento("2027-07-10", 17.5)), 0f)
    }

    /** La mancha de sol camina con la hora: a la izquierda y larga por la mañana, corta a mediodía, a la derecha por la tarde. */
    @Test fun laManchaDeSolCaminaConLaHora() {
        val dia = "2027-04-25"
        val s7 = EscenaUrss.solQueEntra(conClima(dia, 7.0, Clima.DESPEJADO)); val s13 = EscenaUrss.solQueEntra(conClima(dia, 13.0, Clima.DESPEJADO)); val s17 = EscenaUrss.solQueEntra(conClima(dia, 17.5, Clima.DESPEJADO))
        assertNotNull(s7); assertNotNull(s13); assertNotNull(s17); s7!!; s13!!; s17!!
        assertFalse(s7.deLuna || s13.deLuna || s17.deLuna)
        // Por la mañana el sol está a la derecha y bajo: el rayo va a la izquierda y baja poco; por la tarde, al revés.
        assertTrue(s7.corre < 0f); assertTrue(s17.corre > 0f); assertTrue(s7.baja < s13.baja)
        val c7 = LuzCocinaUrss.centroDeLaMancha(s7); val c13 = LuzCocinaUrss.centroDeLaMancha(s13); val c17 = LuzCocinaUrss.centroDeLaMancha(s17)
        assertTrue("$c7 $c13 $c17", c7.x < c13.x && c13.x < c17.x)
        // Por la mañana cae más lejos de la ventana (más cerca de quien mira): más abajo en el cuadro.
        assertTrue("$c7 $c13", c7.y > c13.y)
        // Las sombras: más largas por la mañana que a mediodía, y la de la tarde se va a la derecha.
        val pie = CocinaUrss.SOBRE_LA_MESA
        assertTrue(LuzCocinaUrss.rayoDelSol(s7, 1f, pie).ay > LuzCocinaUrss.rayoDelSol(s13, 1f, pie).ay)
        assertTrue(LuzCocinaUrss.rayoDelSol(s17, 1f, pie).ax > LuzCocinaUrss.rayoDelSol(s7, 1f, pie).ax)
        // Con el cielo cubierto no entra el sol.
        assertNull(EscenaUrss.solQueEntra(conClima(dia, 13.0, Clima.LLUVIA))); assertNull(EscenaUrss.solQueEntra(conClima(dia, 13.0, Clima.NIEVE)))
        // A mediodía la mancha cae en la mesa (con su cruz) y por la mañana también.
        assertEquals(LuzCocinaUrss.Plano.MESA, LuzCocinaUrss.cae(260f, 120f, s13).plano)
        assertEquals(LuzCocinaUrss.Plano.MESA, LuzCocinaUrss.cae(280f, 140f, s7).plano)
    }

    /** De noche, dos luces y media: la lámpara naranja, la luna fría por la ventana (si está despejado) y la tele. */
    @Test fun deNocheHayDosLuces() {
        val m = conClima("2027-01-20", 21.0, Clima.DESPEJADO)
        assertTrue(EscenaUrss.lampara(m)); assertTrue(EscenaUrss.televisor(m))
        val luna = EscenaUrss.solQueEntra(m); assertNotNull(luna); assertTrue(luna!!.deLuna)
        val focos = CocinaUrss.focos(m)
        val lampara = focos.single { it.p == CocinaUrss.BOMBILLA }; val ventana = focos.first()
        assertTrue(lampara.color.red > lampara.color.blue); assertTrue(ventana.color.blue > ventana.color.red)
        assertTrue(lampara.fuerza > ventana.fuerza)
        assertTrue(focos.any { it.p == CocinaUrss.LUZ_TELE && it.color.blue > it.color.red })
        // Y de día, a las 13:00, ni la lámpara ni la tele: la luz es la de la ventana.
        val dia = conClima("2027-01-20", 13.0, Clima.DESPEJADO)
        assertTrue(CocinaUrss.focos(dia).none { it.p == CocinaUrss.BOMBILLA || it.p == CocinaUrss.LUZ_TELE })
        // Nublado de noche: sin luna, pero la lámpara sigue.
        assertNull(EscenaUrss.solQueEntra(conClima("2027-01-20", 21.0, Clima.NUBES)))
    }

    @Test fun elGatoTieneSueloLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaUrss.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 17.5, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaUrss.anclas(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                assertTrue(anclas.single { it.tipo == TipoAncla.MIRADOR }.y < suelo.y - 60f)
                val quieto = gatoQuieto(suelo, anclas, talla, EscenaUrss.estorbos(m))
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaUrss.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f)
                }
            }
            d = d.plusDays(5)
        }
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosUrss.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosUrss.TODOS.containsAll(EscenaUrss.DE_LA_COCINA))
        // Mientras el contrato esté vacío, la cocina pinta sus piezas (son el objeto del día de esas fechas).
        cadaDia { d, m -> for (p in EscenaUrss.DE_LA_COCINA) assertTrue("$d $p", EscenaUrss.laCocinaPinta(m, p) || p in ObjetosUrss.DIBUJADOS) }
    }

    /**
     * El cuadro se dibuja sin fallar con cada objeto y sin objeto, a varias horas, con nieve, lluvia y niebla, en la casa
     * vieja y en la nueva, el día de la mudanza, en el sanatorio y en los días señalados.
     */
    @Test fun elCuadroSeDibujaEnTodosSusCasos() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", runCatching { ImageBitmap(1, 1) }.isSuccess)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt(); val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        fun dibuja(m: Momento) = CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
            lienzo { with(EscenaUrss) { quieta(m) } }
            lienzo { with(EscenaUrss) { viva(m, 2.5f) } }
        }
        val dias = listOf("2027-01-20", "2027-03-08", "2027-05-01", "2027-07-12", "2027-11-08", "2028-07-15", "2029-06-20", "2029-06-25", "2030-01-15", "2030-10-20", "2031-01-03", "2032-12-31")
        for (dia in dias) {
            val base = FichaDelDia.de(dia, "obrera_moscu")!!
            for (h in listOf(3.0, 7.0, 13.0, 17.5, 21.0)) for (clima in listOf(base.clima, Clima.DESPEJADO, Clima.NIEVE, Clima.LLUVIA, Clima.NIEBLA)) dibuja(Momento(base.copy(clima = clima), h))
        }
        val base = FichaDelDia.de("2027-01-20", "obrera_moscu")!!
        for (o in ObjetosUrss.TODOS + listOf<String?>(null)) for (h in listOf(7.0, 13.0, 21.0)) dibuja(Momento(base.copy(objeto = o), h))
    }
}
