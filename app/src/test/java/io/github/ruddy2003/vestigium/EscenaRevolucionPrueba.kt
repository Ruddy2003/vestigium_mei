package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.ui.escenas.BanderitaRevolucion
import io.github.ruddy2003.vestigium.ui.escenas.CiudadRevolucion
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRevolucion
import io.github.ruddy2003.vestigium.ui.escenas.EscenaRevolucion.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosRevolucion
import io.github.ruddy2003.vestigium.ui.escenas.PELAJE_REVOLUCION
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Revolución francesa (tanda S8) cuenta lo mismo que el cuento de Marie-Jeanne: cómo está el barco (en el
 * río, preso en el hielo o vacío los días de Versalles), cuánta ropa hay tendida, lo que cuesta el pan y lo que los
 * años van cambiando salen de la ficha del día, y aquí se comparan con su calendario.
 */
class EscenaRevolucionPrueba {
    private val E = EscenaRevolucion
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "lavandera")!!, hora)
    private fun despejado(dia: String, hora: Double) = Momento(FichaDelDia.de(dia, "lavandera")!!.copy(clima = Clima.DESPEJADO), hora)
    private fun cadaDia(hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString())); d = d.plusDays(1) } }

    @Test fun laEscenaEsLaDeMarieJeanne() {
        assertEquals(listOf("lavandera"), E.vidas)
        assertEquals("lavandera", E.vida("2027-05-10"))
        assertNotNull(BanderitaRevolucion); assertEquals(2, PELAJE_REVOLUCION.especiales.size)
    }

    @Test fun elBarcoEstaPresoEnElHieloYVacioLosDiasDeVersallesCuandoDiceElCalendario() {
        var helada = 0; var marcha = 0
        cadaDia { d, m ->
            val esperado = when (m.ficha.estado) { "helada" -> Lugar.HELADA; "marcha" -> Lugar.MARCHA; else -> Lugar.BARCO }
            assertEquals(d.toString(), esperado, E.lugar(m))
            if (d.year == 2027) { if (E.lugar(m) == Lugar.HELADA) helada++; if (E.lugar(m) == Lugar.MARCHA) marcha++ }
            // Con el barco preso o vacío no se lava, no hay lumbre ni ropa en la cuerda.
            if (E.lugar(m) != Lugar.BARCO) for (h in listOf(7.0, 13.0, 21.0)) { val mh = momento(d.toString(), h); assertTrue(d.toString(), E.barcoCerrado(mh)); assertFalse(E.lavando(mh)); assertFalse(E.lumbre(mh)); assertEquals(0f, E.ropaTendida(mh), 0f) }
        }
        // El Sena está helado del 1 al 19 de enero de la temporada 1 (el 20 se rompe el hielo); la marcha son el 5 y el 6 de octubre.
        assertEquals(Lugar.HELADA, E.lugar(momento("2027-01-01"))); assertEquals(Lugar.HELADA, E.lugar(momento("2027-01-19"))); assertEquals(Lugar.BARCO, E.lugar(momento("2027-01-21")))
        assertEquals(Lugar.MARCHA, E.lugar(momento("2027-10-05"))); assertEquals(Lugar.MARCHA, E.lugar(momento("2027-10-06"))); assertEquals(Lugar.BARCO, E.lugar(momento("2027-10-08")))
        assertTrue("la helada dura unas tres semanas: $helada días", helada in 19..20); assertTrue("la marcha son dos o tres días: $marcha", marcha in 2..3)
        // Solo en la temporada 1.
        assertEquals(Lugar.BARCO, E.lugar(momento("2028-01-10"))); assertEquals(Lugar.BARCO, E.lugar(momento("2030-10-05")))
        // Sin aceite: en la helada no hay faroles ni farol de popa, ni de noche.
        val noche = momento("2027-01-10", 21.0)
        assertFalse(E.hayAceite(noche)); assertFalse(E.faroles(noche)); assertFalse(E.farolDePopa(noche))
        assertTrue(E.faroles(despejado("2027-05-05", 21.0))); assertTrue(E.farolDePopa(despejado("2027-05-05", 21.0)))
    }

    @Test fun laRopaTendidaVaConLaHoraElTiempoYElTrabajo() {
        // Un día de trabajo despejado: poca por la mañana, toda por la tarde, casi ninguna de noche. Son tres cuadros distintos.
        val manana = despejado("2027-05-05", 7.0); val tarde = despejado("2027-05-05", 13.0); val noche = despejado("2027-05-05", 21.0)
        assertTrue(E.lavando(manana)); assertTrue(E.lumbre(manana)); assertTrue(E.ropaTendida(manana) in 0.2f..0.5f)
        assertTrue(E.lavando(tarde)); assertEquals(1f, E.ropaTendida(tarde), 0f)
        assertFalse(E.lavando(noche)); assertFalse(E.lumbre(noche)); assertTrue(E.ropaTendida(noche) <= 0.1f); assertTrue(E.farolDePopa(noche)); assertTrue(E.ventanaDeGaspard(noche))
        // La cuerda se va llenando a lo largo del día y nunca pasa de llena.
        var antes = 0f
        for (h in listOf(6.0, 7.0, 9.0, 11.0, 12.4, 13.0, 16.0, 18.0)) { val r = E.ropaTendida(despejado("2027-05-05", h)); assertTrue("a las $h", r >= antes && r <= 1f); antes = r }
        // Con lluvia o nieve «habrá que tender dentro»; y los días de fiesta el barco está cerrado.
        for (c in listOf(Clima.LLUVIA, Clima.TORMENTA, Clima.NIEVE)) assertEquals(0f, E.ropaTendida(Momento(FichaDelDia.de("2027-05-05", "lavandera")!!.copy(clima = c), 13.0)), 0f)
        for (dia in listOf("2027-12-25", "2027-04-12", "2028-07-14")) { val m = momento(dia, 13.0); assertTrue(dia, E.barcoCerrado(m)); assertEquals(dia, 0f, E.ropaTendida(m), 0f); assertFalse(dia, E.lumbre(m)) }
        // La gran colada (cinco días desde el 20 de marzo): la cuerda, llena desde temprano.
        for (d in 20..24) { val m = despejado("2027-03-$d", 8.0); assertTrue(E.colada(m)); assertTrue(E.ropaTendida(m) >= 0.85f) }
        assertFalse(E.colada(momento("2027-03-26")))
        cadaDia { d, m -> for (h in listOf(3.0, 7.0, 13.0, 19.0, 23.0)) { val r = E.ropaTendida(momento(d.toString(), h)); assertTrue("$d $h", r in 0f..1f) } }
    }

    @Test fun laColaYLaHogazaSiguenLoQueCuestaElPan() {
        cadaDia { d, m ->
            val pan = m.arco("pan")
            assertTrue(d.toString(), pan in 0f..1f); assertEquals(pan, E.pan(m), 0f)
            assertEquals(d.toString(), ((pan - 0.35f) / 0.65f).coerceIn(0f, 1f), E.cola(m), 0.0001f)
            assertEquals(d.toString(), if (pan < 0.5f) 3 else if (pan < 0.8f) 2 else 1, E.hogaza(m))
        }
        // El 14 de julio de 1789 el pan está en lo más caro: la cola, entera, y en el cesto, un cuarto de hogaza.
        val caro = momento("2027-07-14"); assertEquals(1f, E.cola(caro), 0.01f); assertEquals(1, E.hogaza(caro))
        // A finales de 1790 se puede pagar: casi no hay cola y la hogaza está entera.
        val barato = momento("2028-12-28"); assertTrue(E.cola(barato) < 0.12f); assertEquals(3, E.hogaza(barato))
        // En 1793 vuelve a subir.
        assertTrue(E.cola(momento("2031-09-29")) > 0.95f)
        // Los cestos guardan sitio de madrugada; a mediodía ya no hay.
        assertTrue(E.cestosEnLaCola(momento("2027-07-14", 5.0))); assertFalse(E.cestosEnLaCola(momento("2027-07-14", 13.0)))
    }

    @Test fun loQueLosAniosVanCambiandoLlegaElDiaQueDiceElCalendarioYYaNoSeVa() {
        // La escarapela de Jacques (20 de julio de la temporada 1).
        assertFalse(E.escarapela(momento("2027-07-19"))); assertTrue(E.escarapela(momento("2027-07-20"))); assertTrue(E.escarapela(momento("2030-02-02")))
        // Las banderas: ninguna antes de la Bastilla; una desde ese día; tres desde la Federación; cinco desde la República.
        assertEquals(0, E.banderas(momento("2027-07-13"))); assertEquals(1, E.banderas(momento("2027-07-14"))); assertEquals(1, E.banderas(momento("2028-07-13")))
        assertEquals(3, E.banderas(momento("2028-07-14"))); assertEquals(3, E.banderas(momento("2030-09-22"))); assertEquals(5, E.banderas(momento("2030-09-23"))); assertEquals(5, E.banderas(momento("2031-12-31")))
        // Los carteles, la cesta de Paul, la muleta y la cruz de la iglesia pequeña.
        assertEquals(0, E.carteles(momento("2027-08-27"))); assertEquals(1, E.carteles(momento("2027-08-28"))); assertEquals(2, E.carteles(momento("2029-09-10"))); assertEquals(3, E.carteles(momento("2031-03-15")))
        assertFalse(E.paulAyuda(momento("2027-11-15"))); assertTrue(E.paulAyuda(momento("2027-11-16")))
        assertFalse(E.muleta(momento("2031-06-14"))); assertTrue(E.muleta(momento("2031-06-15")))
        assertFalse(E.sinCruz(momento("2031-11-09"))); assertTrue(E.sinCruz(momento("2031-11-10")))
        // De 2027 a 2031 nada de eso va a menos.
        var banderas = 0; var carteles = 0; var esc = false; var paul = false; var muleta = false; var cruz = false
        var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val m = momento(d.toString())
            assertTrue(d.toString(), E.banderas(m) >= banderas && E.carteles(m) >= carteles); banderas = E.banderas(m); carteles = E.carteles(m)
            if (esc) assertTrue(E.escarapela(m)); if (paul) assertTrue(E.paulAyuda(m)); if (muleta) assertTrue(E.muleta(m)); if (cruz) assertTrue(E.sinCruz(m))
            esc = E.escarapela(m); paul = E.paulAyuda(m); muleta = E.muleta(m); cruz = E.sinCruz(m)
            d = d.plusDays(1)
        }
        // El año de avance (2026) es la temporada 1, y cuando la 5 se repite (2032) el cuadro vuelve a contarla, como el cuento.
        assertEquals(0, E.banderas(momento("2026-03-01"))); assertEquals(5, E.banderas(momento("2032-03-01"))); assertFalse(E.sinCruz(momento("2032-03-01"))); assertTrue(E.sinCruz(momento("2032-12-01")))
    }

    @Test fun losDiasSenaladosSeCuentanConSobriedad() {
        // El luto y la pólvora: los postigos cerrados y el río vacío; nada más.
        for (dia in listOf("2029-04-04", "2029-07-17", "2030-09-03", "2031-01-21", "2031-07-15", "2031-10-16", "2027-07-14", "2030-08-10")) { val m = momento(dia, 13.0); assertTrue(dia, E.postigosCerrados(m)); assertTrue(dia, E.rioVacio(m)); assertFalse(dia, E.barcaza(m)) }
        assertFalse(E.postigosCerrados(momento("2027-05-05")))
        // El humo, lejos: la Bastilla, a la izquierda; las Tullerías, río abajo; Réveillon, tres días.
        assertEquals(-1, E.humoDeGuerra(momento("2027-07-14"))); assertEquals(1, E.humoDeGuerra(momento("2030-08-10"))); assertEquals(0, E.humoDeGuerra(momento("2027-05-05")))
        for (d in 27..29) assertTrue(E.incendio(momento("2027-04-$d"))); assertFalse(E.incendio(momento("2027-04-30")))
        // La hoguera de San Juan: de noche, los años en que la hay (en 1791 se queda la procesión, y en 1793 ya no hay).
        assertTrue(E.hogueraDeSanJuan(momento("2027-06-23", 22.0))); assertFalse(E.hogueraDeSanJuan(momento("2027-06-23", 12.0)))
        assertFalse(E.hogueraDeSanJuan(momento("2029-06-23", 22.0))); assertFalse(E.hogueraDeSanJuan(momento("2031-06-23", 22.0)))
        // La crecida de 1793.
        assertTrue(E.crecida(momento("2031-02-25"))); assertFalse(E.crecida(momento("2031-02-27")))
        // Las ventanas con luz: siempre entre 0 y 1; de día, ninguna si no hay niebla.
        cadaDia { d, m -> for (h in listOf(3.0, 13.0, 21.0)) { val mh = momento(d.toString(), h); assertTrue("$d $h", E.ventanas(mh) in 0f..1f) } }
        assertEquals(0f, E.ventanas(despejado("2027-05-05", 13.0)), 0f); assertTrue(E.ventanas(despejado("2027-05-05", 21.0)) > 0.3f)
    }

    @Test fun losObjetosSonLosDelCalendarioYElPuenteTieneSusOjos() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosRevolucion.TODOS.toSortedSet(), delCalendario)
        // Los ojos del puente van del estribo de enfrente hacia acá, sin pisarse, cada uno más ancho que el anterior.
        val ojos = CiudadRevolucion.OJOS
        assertEquals(6, ojos.size)
        for (i in ojos.indices) { assertTrue(ojos[i].first < ojos[i].second); if (i > 0) { assertTrue(ojos[i - 1].second < ojos[i].first); assertTrue(CiudadRevolucion.px(ojos[i].second) - CiudadRevolucion.px(ojos[i].first) > CiudadRevolucion.px(ojos[i - 1].second) - CiudadRevolucion.px(ojos[i - 1].first)) } }
        // Las casas de la orilla no se pisan y ninguna tapa las torres de Notre-Dame (que suben hasta 50).
        val casas = CiudadRevolucion.CASAS
        for (i in 1 until casas.size) assertTrue(casas[i - 1].x1 <= casas[i].x0)
        for (c in casas) assertTrue(c.top >= 86f)
    }

    /** Lo mismo que `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` comprueba en las escenas registradas, aquí para esta (en el arnés todavía no lo está). */
    @Test fun elGatoTieneSueloLuzYSitioDondeSentarse() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        assertTrue(E.tallaDelGato in 0.8f..1.8f)
        val dias = listOf("2027-01-10", "2027-01-20", "2027-05-05", "2027-06-23", "2027-10-05", "2027-12-25", "2028-08-21", "2029-03-18", "2030-11-10", "2031-02-25", "2031-11-10")
        for (dia in dias) for (hora in listOf(3.0, 7.0, 12.2, 13.0, 14.7, 17.0, 20.5, 23.0)) {
            val m = momento(dia, hora)
            val anclas = E.anclas(m)
            val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
            assertTrue("$dia $hora: no hay suelo para el gato", suelo != null); suelo!!
            assertTrue("el suelo es muy corto para caminar", suelo.hasta - suelo.x >= 2 * Gatuno.MEDIO_LARGO * E.tallaDelGato + 60f)
            assertTrue("el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * E.tallaDelGato > 150f && suelo.y <= 285f)
            val plantado = E.estorbos(m)
            plantado.forEach { x -> assertTrue("$dia $hora: lo plantado en $x no está en el suelo del gato", x in suelo.x..suelo.hasta) }
            val quieto = gatoQuieto(suelo, anclas, E.tallaDelGato, plantado)
            assertEquals(AnimGato.SENTADO, quieto.anim)
            plantado.forEach { x -> assertTrue("$dia $hora: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= Gatuno.MEDIO_LARGO * E.tallaDelGato) }
            assertTrue("quieto se sale de su tramo", quieto.x - Gatuno.MEDIO_LARGO * E.tallaDelGato >= suelo.x - 0.01f && quieto.x + Gatuno.MEDIO_LARGO * E.tallaDelGato <= suelo.hasta + 0.01f)
            anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$dia $hora: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= Gatuno.MEDIO_LARGO * E.tallaDelGato) }
            for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                val luz = E.luzDelGato(m, Offset(x, suelo.y))
                for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$dia $hora: un color del gato no es válido", valido(luz.tono(c)))
                assertTrue("$dia $hora: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
            }
            // Las anclas: siempre hay mirador y abrigo; el fuego, solo con la lumbre encendida; el adorno, solo en fiesta; todas, dentro del cuadro.
            assertTrue(anclas.any { it.tipo == TipoAncla.MIRADOR }); assertTrue(anclas.any { it.tipo == TipoAncla.ABRIGO })
            assertEquals("$dia $hora", E.lumbre(m), anclas.any { it.tipo == TipoAncla.FUEGO })
            assertEquals("$dia $hora", E.fiesta(m), anclas.any { it.tipo == TipoAncla.ADORNO })
            for (a in anclas) assertTrue(a.x in 0f..380f && a.y in 0f..285f && a.hasta in 0f..380f)
        }
        // En la helada no hay lumbre ni objeto sobre el banco (está todo recogido y nevado).
        val h = E.anclas(momento("2027-01-10", 10.0)); assertFalse(h.any { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO })
    }

    @Test fun siempreSeMueveAlgoYLasLucesSonValidas() {
        cadaDia { d, m -> if (d.dayOfMonth % 3 == 0) for (h in listOf(2.0, 7.0, 13.0, 21.0)) {
            val mh = momento(d.toString(), h)
            assertTrue(E.seMueve(mh))
            for (f in E.focos(mh)) { assertTrue("$d $h", f.fuerza in 0f..1.2f && f.alcance > 0f); assertTrue(f.p.x.isFinite() && f.p.y.isFinite()) }
            val l = E.luces(mh); assertTrue(l.noche in 0f..1f)
            assertTrue(E.perdido(mh, 0.5f) in 0f..1f)
        } }
    }

    @Test fun laNocheTieneSusLucesYLaHeladaSuHoguera() {
        // En la helada, sin aceite, de noche arde la hoguera del muelle; de día no, ni un día cualquiera.
        assertTrue(E.hogueraDelMuelle(momento("2027-01-10", 21.0))); assertTrue(E.hogueraDelMuelle(momento("2027-01-10", 2.0)))
        assertFalse(E.hogueraDelMuelle(momento("2027-01-10", 13.0))); assertFalse(E.hogueraDelMuelle(momento("2027-05-05", 21.0))); assertFalse(E.hogueraDelMuelle(momento("2028-01-10", 21.0)))
        // Una noche cualquiera, las dos luces de la popa llegan al centro de la cubierta (la cuba, el hornillo, el banco no quedan en penumbra).
        val noche = despejado("2027-05-05", 21.0)
        val focos = E.focos(noche)
        assertTrue("el farol de popa y la ventana de Gaspard", focos.size >= 2)
        for (x in listOf(126f, 186f, 268f)) { val suma = focos.sumOf { f -> (f.fuerza / (1f + ((f.p.x - x) * (f.p.x - x) + (f.p.y - 250f) * (f.p.y - 250f)) / (f.alcance * f.alcance))).toDouble() }; assertTrue("en x=$x llega poca luz: $suma", suma > 0.4) }
        // El río devuelve las ventanas encendidas: hay hileras con luz de noche, dentro de las casas, y ninguna a mediodía.
        val hileras = CiudadRevolucion.lucesDeEnfrente(noche)
        assertTrue("hileras de ventanas con luz: ${hileras.size}", hileras.size >= 5)
        for ((x, n) in hileras) { assertTrue(n >= 1); assertTrue("x=$x", CiudadRevolucion.CASAS.any { x > it.x0 && x < it.x1 }) }
        assertTrue(CiudadRevolucion.lucesDeEnfrente(despejado("2027-05-05", 13.0)).isEmpty())
        // Con los postigos echados (luto) no se ve ninguna.
        assertTrue(CiudadRevolucion.lucesDeEnfrente(momento("2029-04-04", 21.0)).isEmpty())
        // Las torres de Notre-Dame, agrandadas, caben en el cuadro, quedan por debajo de la cuerda y a la derecha de la casa del barco.
        assertEquals(2, CiudadRevolucion.TORRES.size)
        assertTrue(CiudadRevolucion.ANCHO_TORRE >= 15f); assertTrue(CiudadRevolucion.REMATE_TORRES in 18f..40f)
        for (x in CiudadRevolucion.TORRES) assertTrue(x > 60f && x + CiudadRevolucion.ANCHO_TORRE < 200f)
    }
}
