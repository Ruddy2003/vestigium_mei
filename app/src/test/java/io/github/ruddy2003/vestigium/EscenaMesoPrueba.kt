package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import io.github.ruddy2003.vestigium.ui.escenas.AnimGato
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMeso
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMeso.Estela
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMeso.Fogon
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMeso.Milpa
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosMeso
import io.github.ruddy2003.vestigium.ui.escenas.PELAJE_MESO
import io.github.ruddy2003.vestigium.ui.escenas.PatioMeso
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro de Mesoamérica (tanda S9) cuenta lo mismo que el cuento de K'an: cómo va la estela del fin del k'atun, en
 * qué etapa está la milpa, si él está de embajada y lo que pasa cada día señalado salen de la ficha del día, y aquí se
 * comparan con su calendario (`AnioG3.kt`).
 */
class EscenaMesoPrueba {
    private fun momento(dia: String, hora: Double = 13.0) = Momento(FichaDelDia.de(dia, "escriba_maya")!!, hora)
    private fun cadaDia(desde: Int = 2026, hacer: (LocalDate, FichaDelDia) -> Unit) { var d = LocalDate.of(desde, 1, 1); while (d.year <= 2032) { hacer(d, FichaDelDia.de(d.toString(), "escriba_maya")!!); d = d.plusDays(1) } }
    private fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
    private val horas = listOf(0.0, 3.0, 5.0, 7.0, 9.0, 13.0, 14.7, 17.0, 19.0, 20.5, 21.0, 23.0)

    @Test fun laEscenaEsLaDelEscriba() {
        assertEquals(listOf("escriba_maya"), EscenaMeso.vidas)
        assertEquals("escriba_maya", EscenaMeso.vida("2027-05-10"))
        assertEquals(2, PELAJE_MESO.especiales.size)
    }

    @Test fun laEstelaSeEncargaSeTallaYSeLevantaParaSiempre() {
        // Temporada 1 (2027): el señor la encarga el 20 de febrero. Temporada 2 (2028): se levanta el 3 de mayo, fin del k'atun.
        assertTrue(momento("2027-02-20").ficha.especiales.any { it.dia.id == "encargo_estela" })
        assertTrue(momento("2028-05-03").ficha.especiales.any { it.dia.id == "fin_katun" })
        for (dia in listOf("2027-01-01", "2027-02-19")) assertEquals(dia, Estela.SIN, EscenaMeso.estela(momento(dia)))
        for (dia in listOf("2027-02-20", "2027-04-01")) assertEquals(dia, Estela.PIEDRA, EscenaMeso.estela(momento(dia)))
        for (dia in listOf("2027-06-01", "2027-12-31", "2028-01-01", "2028-05-02")) assertEquals(dia, Estela.EN_OBRA, EscenaMeso.estela(momento(dia)))
        for (dia in listOf("2028-05-03", "2028-05-04", "2029-01-01", "2031-07-01", "2032-12-31")) assertEquals(dia, Estela.LEVANTADA, EscenaMeso.estela(momento(dia)))
        assertEquals(0f, EscenaMeso.tallado(momento("2027-03-01")), 0f); assertEquals(1f, EscenaMeso.tallado(momento("2028-05-03")), 0.001f)
        assertTrue(EscenaMeso.tallado(momento("2027-10-01")) in 0.3f..0.7f)
        // De 2027 a 2032 la estela nunca va a menos: ni de estado ni de tallado. Y el cuadro usa el arco del cuento.
        var antes = Estela.SIN; var tallado = 0f; var levantada = 0
        cadaDia(2027) { d, f ->
            val m = Momento(f, 13.0); val e = EscenaMeso.estela(m); val t = EscenaMeso.tallado(m)
            assertTrue("$d: la estela no retrocede ($antes -> $e)", e.ordinal >= antes.ordinal); assertTrue("$d: lo tallado no se borra", t >= tallado - 0.0001f)
            assertEquals("$d", f.arcos.getValue("estela") >= 0.999, e == Estela.LEVANTADA)
            // No depende de la hora ni del tiempo que haga.
            for (h in listOf(2.0, 21.0)) assertEquals("$d $h", e, EscenaMeso.estela(Momento(f.copy(clima = Clima.TORMENTA), h)))
            if (e == Estela.LEVANTADA) { levantada++; assertTrue("$d", d >= LocalDate.of(2028, 5, 3)) }
            antes = e; tallado = t
        }
        assertEquals(Estela.LEVANTADA, antes)
        assertEquals(LocalDate.of(2028, 5, 3).until(LocalDate.of(2033, 1, 1), java.time.temporal.ChronoUnit.DAYS).toInt(), levantada)
        // 2026 es el avance de la temporada 1: se ve lo mismo que en 2027.
        assertEquals(Estela.SIN, EscenaMeso.estela(momento("2026-01-10"))); assertEquals(Estela.EN_OBRA, EscenaMeso.estela(momento("2026-09-01")))
    }

    @Test fun laMilpaSigueSuAnio() {
        for (anio in 2027..2032) {
            assertEquals("$anio", Milpa.RASTROJO, EscenaMeso.milpa(momento("$anio-03-01")))
            assertEquals("$anio", Milpa.ARDE, EscenaMeso.milpa(momento("$anio-04-06")))
            assertEquals("$anio", Milpa.CENIZA, EscenaMeso.milpa(momento("$anio-05-01")))
            assertEquals("$anio", Milpa.BAJA, EscenaMeso.milpa(momento("$anio-06-15")))
            assertEquals("$anio", Milpa.ALTA, EscenaMeso.milpa(momento("$anio-08-20")))
            assertEquals("$anio", Milpa.DOBLADA, EscenaMeso.milpa(momento("$anio-10-20")))
            assertEquals("$anio", Milpa.RASTROJO, EscenaMeso.milpa(momento("$anio-11-10")))
            assertEquals(0f, EscenaMeso.cosechado(momento("$anio-10-14")), 0f)
            assertTrue(EscenaMeso.cosechado(momento("$anio-10-25")) in 0.3f..0.7f); assertTrue(EscenaMeso.cosechado(momento("$anio-11-04")) > 0.9f)
        }
        var arde = 0; var tumbada = 0; var mustia = 0
        cadaDia { d, f ->
            val m = Momento(f, 13.0); val estado = EscenaMeso.milpa(m); val md = d.toString().substring(5)
            // Arde justo los días de la quema del cuento, y su humo vela el cielo.
            assertEquals("$d", m.con("quema"), estado == Milpa.ARDE)
            if (estado == Milpa.ARDE) { arde++; assertTrue("$d: la quema es en abril", d.monthValue == 4); assertTrue("$d", EscenaMeso.luces(m).solFuerza <= m.luces().solFuerza) }
            // Hay maíz en pie solo entre la siembra y el fin de la cosecha.
            val conMaiz = estado == Milpa.BAJA || estado == Milpa.ALTA || estado == Milpa.DOBLADA
            if (estado != Milpa.ARDE) assertEquals("$d", md >= "05-20" && md < "11-05", conMaiz)
            if (EscenaMeso.cosechado(m) > 0f) assertEquals("$d", Milpa.DOBLADA, estado)
            if (EscenaMeso.tumbada(m)) { tumbada++; assertEquals("$d: el huracán es de la temporada 2", 2028, d.year); assertTrue("$d", conMaiz) }
            if (EscenaMeso.mustia(m)) { mustia++; assertTrue("$d: la sequía es de la temporada 1", d.year <= 2027); assertTrue("$d", conMaiz) }
            // No depende de la hora.
            assertEquals("$d", estado, EscenaMeso.milpa(Momento(f, 3.0)))
        }
        assertEquals(7 * 7, arde)                                               // siete días de quema, siete años
        assertTrue(tumbada in 30..45); assertEquals(2 * 22, mustia)
        // El día del huracán de la temporada 2 hay tormenta y, desde entonces, la milpa queda tumbada.
        val h = momento("2028-09-05"); assertTrue(EscenaMeso.huracan(h)); assertEquals(Clima.TORMENTA, h.ficha.clima); assertTrue(EscenaMeso.tumbada(h)); assertFalse(EscenaMeso.tumbada(momento("2028-09-04")))
    }

    @Test fun deEmbajadaElPatioQuedaSinEl() {
        // Temporada 3 (2029): sale el 12 de abril y vuelve el 15 de mayo. Son 33 días: una variante, no un cuadro nuevo.
        var dias = 0
        cadaDia { d, f ->
            for (h in horas) {
                val m = Momento(f, h)
                assertEquals("$d", f.estado == "embajada", EscenaMeso.deEmbajada(m))
                if (EscenaMeso.deEmbajada(m)) {
                    assertFalse("$d $h: de viaje, el códice está guardado", EscenaMeso.codiceAbierto(m)); assertFalse("$d $h: la fiesta es de allá", EscenaMeso.fiesta(m))
                    assertFalse("$d $h: él no está en el templo", EscenaMeso.copalEnElTemplo(m) && !EscenaMeso.copal(m))
                    assertTrue("$d $h: el patio sigue teniendo suelo para el gato", EscenaMeso.anclas(m).any { it.tipo == TipoAncla.SUELO })
                }
            }
            if (f.estado == "embajada") { dias++; assertEquals("$d", 2029, d.year); assertTrue("$d", d >= LocalDate.of(2029, 4, 12) && d < LocalDate.of(2029, 5, 15)) }
        }
        assertEquals(33, dias)
        // La víspera y el día de la vuelta, el códice está otra vez sobre el banco.
        assertTrue(EscenaMeso.codiceAbierto(momento("2029-04-11", 10.0).let { Momento(it.ficha.copy(clima = Clima.DESPEJADO), 10.0) }))
        assertTrue(EscenaMeso.codiceAbierto(momento("2029-05-15", 10.0).let { Momento(it.ficha.copy(clima = Clima.DESPEJADO), 10.0) }))
    }

    @Test fun losDiasSenaladosSeVen() {
        var copal = 0; var luto = 0; var guerra = 0; var wayeb = 0; var fiestas = 0; var lucero = 0; var murcielagos = 0
        cadaDia(2027) { d, f ->
            val m = Momento(f, 20.0)
            if (EscenaMeso.copal(m)) { copal++; assertTrue("$d: con copal sube humo del templo", EscenaMeso.copalEnElTemplo(m)) }
            if (EscenaMeso.luto(m)) { luto++; assertFalse("$d: de luto, el códice está tapado", EscenaMeso.codiceAbierto(Momento(f, 10.0))) }
            if (EscenaMeso.guerra(m)) guerra++
            if (EscenaMeso.fiesta(m)) { fiestas++; assertTrue("$d: en fiesta hay adorno para el gato", EscenaMeso.anclas(m).any { it.tipo == TipoAncla.ADORNO }) }
            else assertFalse("$d", EscenaMeso.anclas(m).any { it.tipo == TipoAncla.ADORNO })
            if (EscenaMeso.wayeb(m)) {
                wayeb++
                // Los cinco días sin nombre: ni fuego, ni humo, ni fiesta, a ninguna hora.
                for (h in horas) { val w = Momento(f, h); assertEquals("$d $h", Fogon.APAGADO, EscenaMeso.fogon(w)); assertFalse("$d $h", EscenaMeso.ocote(w)); assertFalse("$d $h", EscenaMeso.fuegoDelTemplo(w)); assertFalse("$d $h", EscenaMeso.copalEnElTemplo(w)); assertTrue("$d $h", EscenaMeso.focos(w).isEmpty()); assertFalse("$d $h", EscenaMeso.codiceAbierto(w)) }
            }
            if (EscenaMeso.lucero(Momento(f, 5.0))) { lucero++; assertFalse("$d: el lucero es de antes del alba", EscenaMeso.lucero(Momento(f, 13.0))) }
            if (EscenaMeso.murcielagos(Momento(f, 19.0))) { murcielagos++; assertFalse("$d", EscenaMeso.murcielagos(Momento(f, 9.0))) }
        }
        assertTrue("copal: $copal", copal >= 4); assertTrue("luto: $luto", luto >= 1); assertTrue("guerra: $guerra", guerra >= 2)
        assertEquals(5 * 6, wayeb); assertTrue("fiestas: $fiestas", fiestas >= 6 * 3); assertEquals(4, lucero); assertTrue(murcielagos in 1..2)      // Venus: temporadas 2, 3 y 5 (y 2032 repite la 5)
        // El fin del k'atun: la estela, levantada, con copal y fiesta.
        val k = momento("2028-05-03", 10.0); assertTrue(EscenaMeso.finDelKatun(k)); assertTrue(EscenaMeso.copal(k)); assertTrue(EscenaMeso.fiesta(k)); assertEquals(Estela.LEVANTADA, EscenaMeso.estela(k))
    }

    @Test fun elFogonYLasLucesSiguenElDia() {
        val base = FichaDelDia.de("2027-06-10", "escriba_maya")!!.copy(clima = Clima.DESPEJADO)
        assertEquals(Fogon.COMAL, EscenaMeso.fogon(Momento(base, 6.0)))
        assertEquals(Fogon.ENCENDIDO, EscenaMeso.fogon(Momento(base, 19.4)))
        assertEquals(Fogon.RESCOLDO, EscenaMeso.fogon(Momento(base, 23.0))); assertEquals(Fogon.RESCOLDO, EscenaMeso.fogon(Momento(base, 2.0)))
        assertEquals(Fogon.APAGADO, EscenaMeso.fogon(Momento(base.copy(clima = Clima.TORMENTA), 19.4)))
        // La lumbre da luz solo si hay fuego, y de noche siempre alumbra algo (menos en los días sin nombre y con tormenta).
        cadaDia(2027) { d, f ->
            for (h in horas) {
                val m = Momento(f, h); val focos = EscenaMeso.focos(m)
                assertEquals("$d $h", EscenaMeso.fogon(m) != Fogon.APAGADO, EscenaMeso.lumbre(m) > 0f)
                focos.forEach { assertTrue("$d $h: un foco sin fuerza válida", it.fuerza in 0f..1.2f && it.alcance > 0f) }
                val l = EscenaMeso.luces(m)
                if (l.noche > 0.6f && !EscenaMeso.wayeb(m) && f.clima != Clima.TORMENTA) assertTrue("$d $h: de noche no alumbra nada", focos.isNotEmpty() || EscenaMeso.fuegoDelTemplo(m))
                if (EscenaMeso.ocote(m) || EscenaMeso.fuegoDelTemplo(m)) assertTrue("$d $h: fuego encendido de día", l.noche > 0.2f)
                if (EscenaMeso.codiceAbierto(m)) assertFalse("$d $h: el códice no se moja", EscenaMeso.mojado(m))
            }
        }
        // 7:00, 13:00 y 21:00 son tres cuadros distintos: bruma de la mañana, sol alto y noche con fuegos.
        val m7 = Momento(base, 7.0); val m13 = Momento(base, 13.0); val m21 = Momento(base, 21.0)
        assertTrue(EscenaMeso.brumaDeLaManana(m7) > 0.9f); assertEquals(0f, EscenaMeso.brumaDeLaManana(m13), 0f); assertEquals(0f, EscenaMeso.brumaDeLaManana(m21), 0f)
        assertTrue(EscenaMeso.luces(m21).noche > 0.6f); assertTrue(EscenaMeso.fuegoDelTemplo(m21)); assertTrue(EscenaMeso.ocote(m21)); assertFalse(EscenaMeso.fuegoDelTemplo(m13))
        assertTrue(EscenaMeso.codiceAbierto(m13))
        // El telar y el pavo, de día y con buen tiempo; la ceiba, pelada al final de las secas.
        assertTrue(PatioMeso.telarTendido(m13)); assertFalse(PatioMeso.telarTendido(m21)); assertFalse(PatioMeso.telarTendido(Momento(base.copy(clima = Clima.LLUVIA), 13.0)))
        assertTrue(PatioMeso.pavo(m13)); assertFalse(PatioMeso.pavo(m21))
        assertTrue(PatioMeso.ceibaPelada(momento("2027-03-15"))); assertFalse(PatioMeso.ceibaPelada(momento("2027-08-15")))
    }

    @Test fun lasEstacionesYLosObjetosSonLosDeSuCalendario() {
        val estaciones = sortedSetOf<Estacion>(); val objetos = sortedSetOf<String>()
        cadaDia { d, f ->
            estaciones += f.estacion; f.objeto?.let { objetos += it }
            assertEquals("$d", f.estacion == Estacion.LLUVIAS, EscenaMeso.lluvias(Momento(f, 13.0)))
        }
        assertEquals(setOf(Estacion.SECAS, Estacion.LLUVIAS), estaciones.toSet())
        assertEquals("el cuadro sabe dibujar justo los objetos de su calendario", ObjetosMeso.TODOS.toSortedSet(), objetos)
    }

    /** Lo mismo que `GatoCuadroPrueba.cadaEscenaLeDaSueloYLuz` y las pruebas de `EscenasPrueba` que recorren `escenaDe`, para esta escena y todos sus días. */
    @Test fun elGatoTieneSueloLuzYSitioTodosLosDias() {
        val escena = EscenaMeso
        assertTrue("la talla del gato", escena.tallaDelGato in 0.8f..1.8f)
        val medio = Gatuno.MEDIO_LARGO * escena.tallaDelGato
        cadaDia { d, f ->
            for (h in if (f.especiales.isNotEmpty() || f.estado != null || d.dayOfMonth % 5 == 0) horas else listOf(3.0, 7.0, 13.0, 20.5)) {
                val m = Momento(f, h); val anclas = escena.anclas(m)
                val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: no hay suelo para el gato", suelo != null)
                assertEquals("$d $h: un solo suelo", 1, anclas.count { it.tipo == TipoAncla.SUELO })
                assertTrue("el suelo es muy corto para caminar", suelo!!.hasta - suelo.x >= 2 * medio + 60f)
                assertTrue("el gato no cabe de alto en el cuadro", suelo.y - Gatuno.ALTO * escena.tallaDelGato > 150f && suelo.y <= 285f)
                assertTrue("el suelo se sale del lienzo", suelo.x >= 0f && suelo.hasta <= ANCHO)
                assertEquals((suelo.x + suelo.hasta) / 2f, gatoQuieto(suelo).x, 0.01f)
                val plantado = escena.estorbos(m)
                plantado.forEach { x -> assertTrue("$d $h: lo plantado ($x) no está en el suelo del gato", x in suelo.x..suelo.hasta) }
                val quieto = gatoQuieto(suelo, anclas, escena.tallaDelGato, plantado)
                assertEquals(AnimGato.SENTADO, quieto.anim)
                plantado.forEach { x -> assertTrue("$d $h: quieto queda encima de lo plantado en $x", abs(quieto.x - x) >= medio) }
                assertTrue("$d $h: quieto se sale de su tramo", quieto.x - medio >= suelo.x - 0.01f && quieto.x + medio <= suelo.hasta + 0.01f)
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.OBJETO }.forEach { a -> assertTrue("$d $h: quieto tapa ${a.tipo}", abs(quieto.x - a.hasta) >= medio) }
                // Cada ancla cae dentro del lienzo; el fuego solo es ancla si hay lumbre; el mirador y el abrigo, siempre.
                anclas.forEach { a -> assertTrue("$d $h: ${a.tipo} fuera del lienzo", a.x in 0f..ANCHO && a.y in 0f..ALTO && a.hasta in 0f..ANCHO) }
                assertEquals("$d $h", EscenaMeso.lumbre(m) > 0f, anclas.any { it.tipo == TipoAncla.FUEGO })
                assertTrue("$d $h", anclas.any { it.tipo == TipoAncla.MIRADOR } && anclas.any { it.tipo == TipoAncla.ABRIGO } && anclas.any { it.tipo == TipoAncla.OBJETO })
                anclas.filter { it.tipo == TipoAncla.FUEGO || it.tipo == TipoAncla.ABRIGO }.forEach { a -> assertTrue("$d $h: ${a.tipo} fuera del suelo", a.x - medio >= suelo.x - 0.01f && a.x + medio <= suelo.hasta + 0.01f) }
                for (x in listOf(suelo.x, quieto.x, suelo.hasta)) {
                    val luz = escena.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF6E6A70), Color.White, Color.Black, Color(0xFFE08A3C))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f && valido(luz.brillo))
                }
                assertTrue("$d $h", escena.seMueve(m))
            }
        }
    }
}
