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
import io.github.ruddy2003.vestigium.ui.escenas.ColorDeco
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Gente
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaDeco.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.LuzDeco
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosDeco
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.VistaDeco
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
 * El cuadro de Art déco (tanda S15: el sótano del club de Sully) cuenta lo mismo que el cuento de Lou: dónde está (el club,
 * la gira, sin club; el club nuevo desde el 5 de mayo de la temporada 5), la fase del día según su horario, el público del
 * arco `publico`, lo que está encendido a cada hora, la puerta de la escalera, el neón de la calle y los días señalados
 * salen de la ficha del día, y aquí se comparan con su calendario (`AnioG6.kt`) todos los días de 2026 a 2032.
 */
class EscenaDecoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "pianista")!!, hora)
    private fun conClima(dia: String, hora: Double, clima: Clima) = Momento(FichaDelDia.de(dia, "pianista")!!.copy(clima = clima), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeLaPianista() {
        assertEquals(listOf("pianista"), EscenaDeco.vidas)
        assertEquals("pianista", EscenaDeco.vida("2027-11-10"))
        assertTrue(EscenaDeco.tallaDelGato in 0.8f..1.8f)
        assertEquals("deco", OtrasVidas.porId("pianista")!!.tema)
    }

    @Test fun estaDondeDiceElCalendario() {
        val gira = mutableMapOf<Int, Int>(); val sinClub = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "gira" -> Lugar.GIRA; "sin_club" -> Lugar.SIN_CLUB; else -> Lugar.CLUB }
            assertEquals(d.toString(), esperado, EscenaDeco.lugar(m))
            assertEquals(d.toString(), esperado != Lugar.CLUB, EscenaDeco.provisional(m))
            // El club nuevo, desde el 5 de mayo de la temporada 5 (con la fecha escrita, la misma cuenta del cuento).
            val nuevo = esperado == Lugar.CLUB && (m.ficha.temporada > 5 || (m.ficha.temporada == 5 && md(d) >= "05-05"))
            assertEquals(d.toString(), nuevo, EscenaDeco.clubNuevo(m))
            assertEquals(d.toString(), if (nuevo) 4 else 6, EscenaDeco.mesas(m))
            if (esperado == Lugar.GIRA) gira[d.year] = (gira[d.year] ?: 0) + 1
            if (esperado == Lugar.SIN_CLUB) sinClub[d.year] = (sinClub[d.year] ?: 0) + 1
        }
        // La gira: 73 días de 2029 (del 18 de junio al 29 de agosto). Sin club: 77 días de 2031, y otros tantos cada año desde 2032.
        assertEquals(mapOf(2029 to 73), gira)
        assertEquals(77, sinClub[2031]); assertTrue("${sinClub[2032]}", sinClub[2032]!! in 77..78)        // 2032 es bisiesto
        assertEquals(Lugar.CLUB, EscenaDeco.lugar(momento("2029-06-17"))); assertEquals(Lugar.GIRA, EscenaDeco.lugar(momento("2029-06-18")))
        assertEquals(Lugar.GIRA, EscenaDeco.lugar(momento("2029-08-29"))); assertEquals(Lugar.CLUB, EscenaDeco.lugar(momento("2029-08-30")))
        assertEquals(Lugar.SIN_CLUB, EscenaDeco.lugar(momento("2031-02-17"))); assertEquals(Lugar.SIN_CLUB, EscenaDeco.lugar(momento("2031-05-04")))
        assertTrue(EscenaDeco.clubNuevo(momento("2031-05-05"))); assertFalse(EscenaDeco.clubNuevo(momento("2031-02-16")))
    }

    @Test fun elPublicoSigueAlArco() {
        cadaDia { d, m ->
            val v = m.ficha.arcos["publico"]!!.toFloat()
            assertEquals(d.toString(), v.coerceIn(0f, 1f), EscenaDeco.publico(m), 0.0001f)
            // Los tramos del cuento (`ENTORNOS`, rangos cerrados, manda el primero); el día del club vacío manda el título.
            val esperado = if (hoyEs(m, "vacio")) Gente.CASI_VACIO else if (v <= 0.3f) Gente.CASI_VACIO else if (v <= 0.5f) Gente.MEDIO else if (v <= 0.75f) Gente.LLENO else Gente.A_REVENTAR
            assertEquals(d.toString(), esperado, EscenaDeco.gente(m))
            for (h in listOf(3.0, 7.0, 13.0, 15.5, 18.0, 21.0, 23.5, 4.5)) {
                val mh = momento(d.toString(), h); val n = EscenaDeco.mesas(mh); val p = EscenaDeco.puestas(mh); val a = EscenaDeco.sillasArriba(mh)
                assertTrue("$d $h", p in 0..n && a in 0..n && p + a <= n)
                val humo = EscenaDeco.humo(mh); assertTrue("$d $h", humo in 0f..1f)
                if (EscenaDeco.provisional(mh)) { assertEquals("$d $h", 0, p); assertEquals("$d $h", 0f, humo, 0f); continue }
                when (EscenaDeco.fase(mh)) {
                    Fase.ABIERTO -> {
                        // Más gente, más mesas puestas y más humo; las que no tienen gente, con las sillas arriba.
                        assertEquals("$d $h", when (EscenaDeco.gente(mh)) { Gente.CASI_VACIO -> 2; Gente.MEDIO -> Math.round(n * 0.58f); Gente.LLENO -> n - 1; Gente.A_REVENTAR -> n }, p)
                        assertEquals("$d $h", n - p, a); assertTrue("$d $h", humo >= 0.15f); assertTrue("$d $h", EscenaDeco.sombreros(mh) >= 2)
                    }
                    Fase.CERRADO, Fase.ENSAYO -> { assertEquals("$d $h", 0, p); assertEquals("$d $h", n - 1, a) }      // «con las sillas encima de las mesas»
                    Fase.PREPARA -> { assertEquals("$d $h", n, p); assertEquals("$d $h", 0, a) }
                    Fase.DESPUES -> assertTrue("$d $h", humo > 0f)
                }
            }
        }
        // Lleno de 2028 a noviembre de 2030; casi vacío a principios de 2031; el club vacío del 16 de noviembre de 2030.
        assertEquals(Gente.A_REVENTAR, EscenaDeco.gente(momento("2028-10-12"))); assertEquals(Gente.CASI_VACIO, EscenaDeco.gente(momento("2031-02-01")))
        assertEquals(Gente.CASI_VACIO, EscenaDeco.gente(momento("2030-11-16"))); assertEquals(Gente.MEDIO, EscenaDeco.gente(momento("2030-11-17")))
        assertTrue(EscenaDeco.puestas(momento("2028-10-12", 22.0)) > EscenaDeco.puestas(momento("2031-02-01", 22.0)))
        // El humo se espesa a lo largo de la noche.
        assertTrue(EscenaDeco.humo(momento("2028-10-12", 2.0)) > EscenaDeco.humo(momento("2028-10-12", 21.0)))
    }

    @Test fun laFaseYLoEncendidoSiguenElHorario() {
        cadaDia { d, _ ->
            for (h in listOf(0.5, 3.0, 4.5, 5.5, 7.0, 12.0, 13.0, 14.5, 16.5, 18.0, 19.5, 20.5, 21.0, 23.0)) {
                val m = momento(d.toString(), h); val fase = EscenaDeco.fase(m)
                val esperada = when (m.hacer) { Hacer.DORMIR -> Fase.CERRADO; Hacer.COMER -> if (h < 16.0) Fase.CERRADO else Fase.PREPARA; Hacer.LIBRE -> Fase.ENSAYO
                    Hacer.CUIDAR, Hacer.CAMINO -> Fase.PREPARA; Hacer.TRABAJO -> Fase.ABIERTO; Hacer.GENTE -> Fase.DESPUES; else -> Fase.CERRADO }
                assertEquals("$d $h", esperada, fase)
                val e = EscenaDeco.encendido(m); val prov = EscenaDeco.provisional(m)
                for (x in listOf(e.deco, e.mesas, e.barra, e.piano, e.guardia)) assertTrue("$d $h", x in 0f..1f)
                if (prov) { assertEquals("$d $h", 0f, e.deco + e.mesas + e.barra + e.piano, 0f); assertEquals("$d $h", Puerta.CERRADA, EscenaDeco.puerta(m)); continue }
                when (fase) {
                    Fase.ABIERTO -> { assertTrue("$d $h", e.deco > 0.5f && e.mesas > 0.5f && e.barra > 0.5f && e.piano > 0.5f); assertTrue("$d $h", EscenaDeco.abierto(m)) }
                    Fase.ENSAYO -> { assertEquals("$d $h", 1f, e.piano, 0f); assertEquals("$d $h", 0f, e.deco + e.mesas + e.barra, 0f) }
                    Fase.CERRADO -> { assertFalse("$d $h", e.alguna); assertEquals("$d $h", Puerta.CERRADA, EscenaDeco.puerta(m)) }
                    Fase.PREPARA, Fase.DESPUES -> assertTrue("$d $h", e.barra > 0f && e.mesas == 0f)
                }
                if (EscenaDeco.redada(m)) assertEquals("$d $h", Puerta.CERRADA, EscenaDeco.puerta(m))
                // El neón de la calle: apagado de día, encendido de noche.
                val n = EscenaDeco.neon(m); assertTrue("$d $h", n in 0f..1f)
                if (h in 7.0..17.0) assertEquals("$d $h", 0f, n, 0f)
                if (h >= 20.5 || h < 4.9) assertEquals("$d $h", 1f, n, 0f)
                assertTrue("$d $h", EscenaDeco.letreros(m) in 1..3)
            }
        }
        // Las cinco horas de las hojas dan cinco fases distintas en un día normal.
        val d = "2028-10-12"
        assertEquals(listOf(Fase.CERRADO, Fase.CERRADO, Fase.ENSAYO, Fase.ABIERTO, Fase.ABIERTO), listOf(7.0, 13.0, 15.5, 21.0, 1.0).map { EscenaDeco.fase(momento(d, it)) })
        assertEquals(Fase.PREPARA, EscenaDeco.fase(momento(d, 18.0))); assertEquals(Fase.DESPUES, EscenaDeco.fase(momento(d, 4.5)))
        // Con el club a reventar la puerta está abierta («hay cola en la escalera»); con medio público, entornada.
        assertEquals(Puerta.ABIERTA, EscenaDeco.puerta(momento(d, 22.0))); assertEquals(Puerta.ENTORNADA, EscenaDeco.puerta(momento("2030-12-01", 22.0)))
        // Desde el martes negro, un letrero menos; sin club, uno.
        assertEquals(3, EscenaDeco.letreros(momento("2030-10-28"))); assertEquals(2, EscenaDeco.letreros(momento("2030-10-29"))); assertEquals(1, EscenaDeco.letreros(momento("2031-03-01")))
    }

    @Test fun losDiasSenaladosSeVenSuDiaYNoLaVispera() {
        cadaDia { d, m ->
            assertEquals(d.toString(), m.con("fiesta"), EscenaDeco.fiesta(m)); assertEquals(d.toString(), m.con("luto"), EscenaDeco.luto(m))
            assertEquals(d.toString(), m.con("calor"), EscenaDeco.calor(m))
            assertEquals(d.toString(), m.ficha.clima == Clima.NIEVE || m.con("nieve"), EscenaDeco.nevada(m))
            if (EscenaDeco.navidad(m)) assertEquals(d.toString(), "12-25", md(d))
            if (EscenaDeco.finDeAnio(m)) assertTrue(d.toString(), md(d) == "12-31" || md(d) == "01-01")
            if (EscenaDeco.calor(m)) assertTrue(d.toString(), EscenaDeco.ventilador(m) && EscenaDeco.ventanasAbiertas(m))
            if (EscenaDeco.luto(m) && EscenaDeco.abierto(momento(d.toString(), 22.0))) assertTrue(d.toString(), EscenaDeco.encendido(momento(d.toString(), 22.0)).deco < 1f)
        }
        for (a in listOf(2026, 2027)) { assertTrue(EscenaDeco.redada(momento("$a-10-15"))); assertFalse(EscenaDeco.redada(momento("$a-10-14"))); assertTrue(EscenaDeco.madre(momento("$a-11-06"))) }
        assertTrue(EscenaDeco.luto(momento("2027-08-24"))); assertTrue(EscenaDeco.luto(momento("2028-11-06"))); assertFalse(EscenaDeco.luto(momento("2027-08-23")))
        assertTrue(EscenaDeco.calor(momento("2027-07-20"))); assertTrue(EscenaDeco.nevada(momento("2031-01-20"))); assertTrue(EscenaDeco.inundado(momento("2029-05-22")))
        assertTrue(EscenaDeco.boda(momento("2031-09-14"))); assertTrue(EscenaDeco.fiesta(momento("2031-09-14")))
        assertTrue(EscenaDeco.navidad(momento("2028-12-25"))); assertTrue(EscenaDeco.finDeAnio(momento("2028-12-31")))
        // El 4 de julio de 2029 cae en la gira: la fiesta se ve igual (el cuadro provisional lleva sus adornos).
        assertTrue(EscenaDeco.fiesta(momento("2029-07-04"))); assertTrue(EscenaDeco.provisional(momento("2029-07-04")))
    }

    /** La luz: el sol entra por las ventanas altas a media mañana y su mancha camina; nunca con lluvia; de noche, varias luces de color opuesto. */
    @Test fun laLuzCaminaConLaHoraYDeNocheHayVariasLuces() {
        val d = "2028-10-12"
        assertNull("a las 7:00 la luz fría y gris, sin sol directo", EscenaDeco.sol(conClima(d, 7.0, Clima.DESPEJADO)))
        val centros = listOf(11.0, 13.0, 15.0, 17.0).map { h -> val v = VistaDeco(conClima(d, h, Clima.DESPEJADO)); assertNotNull("$h", v.sol); LuzDeco.manchaCentro(v)!! }
        for (i in 1 until centros.size) assertTrue("la mancha camina", (centros[i] - centros[i - 1]).getDistance() > 4f)
        // Por la mañana la mancha se va hacia el fondo (sube por la barra); por la tarde, hacia quien mira.
        assertTrue(EscenaDeco.sol(conClima(d, 10.5, Clima.DESPEJADO))!!.a < 0f); assertTrue(EscenaDeco.sol(conClima(d, 17.0, Clima.DESPEJADO))!!.a > 0f)
        assertTrue(EscenaDeco.sol(conClima(d, 13.0, Clima.DESPEJADO))!!.e > EscenaDeco.sol(conClima(d, 17.5, Clima.DESPEJADO))!!.e)
        for (c in listOf(Clima.LLUVIA, Clima.TORMENTA)) assertNull(EscenaDeco.sol(conClima(d, 13.0, c)))
        for (h in listOf(21.0, 23.0, 2.0)) assertNull(EscenaDeco.sol(conClima(d, h, Clima.DESPEJADO)))
        // De noche, con el club abierto: las lámparas cálidas, el neón de la calle y el azul de la noche por la escalera.
        val noche = VistaDeco(conClima(d, 23.0, Clima.DESPEJADO))
        assertTrue(noche.focos.count { it.color == ColorDeco.LAMPARA } >= 4)
        assertTrue(noche.focos.any { it.color == ColorDeco.NEON }); assertTrue(noche.focos.any { it.color == ColorDeco.NEON_JADE }); assertTrue(noche.focos.any { it.color == ColorDeco.NOCHE })
        // Un objeto en el haz de sol se alumbra; fuera del haz, no.
        val v13 = VistaDeco(conClima(d, 13.0, Clima.DESPEJADO))
        assertTrue(v13.alSol(io.github.ruddy2003.vestigium.ui.escenas.V3(-3f, 2.2f, 2.6f)) < 0.01f)
    }

    /** Lo mismo que vigila `GatoCuadroPrueba` en todos los cuadros: suelo a todas horas y bastante largo, sus sitios y una luz válida. */
    @Test fun elGatoTieneSueloLuzYSusSitios() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaDeco.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Lugar>()
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 15.5, 20.5, 23.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaDeco.anclas(m); vistos += EscenaDeco.lugar(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 60f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val quieto = gatoQuieto(suelo, anclas, talla, EscenaDeco.estorbos(m))
                assertEquals(AnimGato.SENTADO, quieto.anim)
                assertTrue("$d $h: quieto se sale", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaDeco.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
            }
            d = d.plusDays(3)
        }
        assertEquals(setOf(Lugar.CLUB, Lugar.GIRA, Lugar.SIN_CLUB), vistos)
    }

    @Test fun losObjetosSonLosDelCalendario() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosDeco.TODOS.toSortedSet(), delCalendario)
    }

    @Test fun seMueveSiempreYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaDeco.seMueve(m)) }
        val a = momento("2028-10-12", 13.0); val b = momento("2028-10-12", 13.0)
        assertEquals(EscenaDeco.humo(a), EscenaDeco.humo(b), 0f)
        assertEquals(EscenaDeco.anclas(a).map { it.x to it.y }, EscenaDeco.anclas(b).map { it.x to it.y })
        assertEquals(VistaDeco(a).focos.map { it.p }, VistaDeco(b).focos.map { it.p })
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * El cuadro se dibuja entero (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, a las horas de
     * cada fase, con nieve, con lluvia y en los días señalados (la redada, la madre, el calor, Valentino, la Navidad, la
     * Nochevieja, la inundación, la gira, sin club, el club nuevo, el club vacío, la nevada, la boda), sin lanzar nada.
     */
    @Test fun elCuadroSeDibujaConCadaObjeto() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2028-10-12", "2027-10-15", "2027-11-06", "2027-07-20", "2027-08-24", "2028-12-25", "2028-12-31", "2029-05-22", "2029-07-04", "2031-03-01", "2031-06-01", "2030-11-16", "2031-01-20", "2031-09-14", "2032-03-10")
        val vistos = HashSet<Lugar>()
        for (dia in dias) for (h in listOf(4.5, 7.0, 15.5, 21.0, 1.0)) {
            val base = momento(dia, h); vistos += EscenaDeco.lugar(base)
            for (o in ObjetosDeco.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.NIEVE, Clima.LLUVIA, Clima.DESPEJADO)) {
                if (clima != base.ficha.clima && o != null && o != "piano") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaDeco) { quieta(m) } }
                        lienzo { with(EscenaDeco) { viva(m, 3.7f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$dia $h con «$o» y $clima: $x", x)
                }
            }
        }
        assertEquals(setOf(Lugar.CLUB, Lugar.GIRA, Lugar.SIN_CLUB), vistos)
    }
}
