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
import io.github.ruddy2003.vestigium.ui.escenas.AparejoAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Carga
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Costa
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Escala
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Fase
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Fiesta
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Manguera
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Mar
import io.github.ruddy2003.vestigium.ui.escenas.Gatuno
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Estacion
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import java.time.LocalDate

/**
 * Los cuadros de Abismo (tanda S16: el barco esponjero y el muelle de Tarpon Springs) cuentan lo mismo que el cuento de
 * Nikos: dónde está (también los días que el cuento pone el barco en tierra), la fase del día y lo que hay en uso a cada
 * hora, si Nikos está abajo, las esponjas con los cortes del cuento y el día en que se venden, el mar, las fiestas por el
 * calendario juliano, la cruz, la vuelta, el luto y lo encendido salen de la ficha del día, y aquí se comparan con su
 * calendario (`AnioG6.kt`) todos los días de 2026 a 2032.
 */
class EscenaAbismoPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "buzo_esponjas")!!, hora)
    private fun cadaDia(hora: Double = 10.0, hacer: (LocalDate, Momento) -> Unit) { var d = LocalDate.of(2026, 1, 1); while (d.year <= 2032) { hacer(d, momento(d.toString(), hora)); d = d.plusDays(1) } }
    private fun md(d: LocalDate) = d.toString().substring(5)
    private fun hoyEs(m: Momento, id: String) = m.ficha.especiales.any { it.dia.id == id }

    @Test fun laEscenaEsLaDeNikos() {
        assertEquals(listOf("buzo_esponjas"), EscenaAbismo.vidas)
        assertEquals("buzo_esponjas", EscenaAbismo.vida("2027-05-05"))
        assertEquals("abismo", OtrasVidas.porId("buzo_esponjas")!!.tema)
        assertTrue(EscenaAbismo.tallaDelGato in 0.8f..1.8f)
    }

    @Test fun estaDondeDiceElCalendarioYElCuento() {
        val cuarto = mutableMapOf<Int, Int>(); val muelle = mutableMapOf<Int, Int>()
        cadaDia { d, m ->
            val tierra = m.ficha.estado == null && (EscenaAbismo.DIAS_DE_TIERRA.any { hoyEs(m, it) } || (hoyEs(m, "zarpa") && m.hora < EscenaAbismo.HORA_DE_ZARPAR))
            val esperado = when (m.ficha.estado) { "puerto" -> Lugar.MUELLE; "enfermo" -> Lugar.CUARTO; else -> if (tierra) Lugar.MUELLE else Lugar.BARCO }
            assertEquals(d.toString(), esperado, EscenaAbismo.lugar(m))
            assertEquals(d.toString(), if (esperado == Lugar.BARCO) Cuadro.BARCO else Cuadro.MUELLE, EscenaAbismo.cuadro(m))
            assertEquals(d.toString(), esperado == Lugar.CUARTO, EscenaAbismo.provisional(m))
            if (esperado == Lugar.CUARTO) cuarto[d.year] = (cuarto[d.year] ?: 0) + 1
            if (m.ficha.estado == "puerto") { muelle[d.year] = (muelle[d.year] ?: 0) + 1; assertTrue(d.toString(), md(d) <= "01-25" || md(d) >= "12-18") }
        }
        // El cuarto: los 36 días de 2030 (del 12 de junio al 17 de julio); el muelle de invierno, 39 días cada año.
        assertEquals(mapOf(2030 to 36), cuarto)
        for (anio in 2026..2032) assertEquals("$anio", 39, muelle[anio])
        assertEquals(Lugar.MUELLE, EscenaAbismo.lugar(momento("2027-12-18"))); assertEquals(Lugar.BARCO, EscenaAbismo.lugar(momento("2027-12-17")))
        assertEquals(Lugar.BARCO, EscenaAbismo.lugar(momento("2027-05-05")))
    }

    /** Los días que el cuento pone el barco en el muelle aunque la ficha diga «barco»: la lonja, el 4 de julio, la boda y la mañana de zarpar. */
    @Test fun losDiasDeTierraSonLosDelCuento() {
        assertEquals(listOf("lonja", "cuatro_julio", "boda"), EscenaAbismo.DIAS_DE_TIERRA)
        val vistos = HashSet<String>()
        cadaDia { d, m ->
            for (id in EscenaAbismo.DIAS_DE_TIERRA) if (hoyEs(m, id)) {
                vistos += id
                assertEquals("$d $id", if (m.ficha.estado == "enfermo") Lugar.CUARTO else Lugar.MUELLE, EscenaAbismo.lugar(m))
            }
            if (hoyEs(m, "zarpa")) {
                assertEquals(d.toString(), Lugar.MUELLE, EscenaAbismo.lugar(momento(d.toString(), 7.0)))
                assertEquals(d.toString(), Flota.SALIENDO, EscenaAbismo.flota(momento(d.toString(), 7.0)))
                assertEquals(d.toString(), Lugar.BARCO, EscenaAbismo.lugar(momento(d.toString(), 13.0)))
            }
            if (EscenaAbismo.lugar(m) == Lugar.BARCO) assertNull(d.toString(), EscenaAbismo.flota(m))
        }
        assertEquals(setOf("lonja", "cuatro_julio", "boda"), vistos)
        // En 2030 la lonja y el 4 de julio caen en el cuarto: manda el estado.
        assertEquals(Lugar.CUARTO, EscenaAbismo.lugar(momento("2030-06-26"))); assertEquals(Flota.VACIO, EscenaAbismo.flota(momento("2030-06-26")))
        assertEquals(Lugar.MUELLE, EscenaAbismo.lugar(momento("2027-06-26"))); assertEquals(Flota.POCOS, EscenaAbismo.flota(momento("2027-06-26")))
        assertEquals(Flota.LLENO, EscenaAbismo.flota(momento("2027-01-12")))
    }

    @Test fun elHorarioMandaLoQueSeVe() {
        for (dia in listOf("2027-04-03", "2027-08-12", "2028-05-05", "2031-10-02")) for (paso in 0 until 72) {
            val h = paso / 3.0; val m = momento(dia, h)
            val fase = EscenaAbismo.fase(m)
            val esperada = when (m.hacer) { Hacer.DORMIR -> Fase.SUENO; Hacer.TRABAJO -> if (h < 12.0) Fase.INMERSION else Fase.ESPONJAS; Hacer.COMER -> if (h < 5.0) Fase.ALBA else Fase.COMIDA; Hacer.GENTE -> Fase.CORRO; else -> Fase.COMIDA }
            assertEquals("$dia $h", esperada, fase)
            if (h >= 5.0 && h < 11.0) assertEquals("$dia $h", Fase.INMERSION, fase)
            if (h >= 12.67 && h < 17.0) assertEquals("$dia $h", Fase.ESPONJAS, fase)
            if (h >= 20.0 || h < 4.5) assertEquals("$dia $h", Fase.SUENO, fase)
            val abajo = EscenaAbismo.buzoAbajo(m)
            assertEquals("$dia $h", EscenaAbismo.lugar(m) == Lugar.BARCO && fase == Fase.INMERSION && !EscenaAbismo.diaSinBajar(m), abajo)
            assertEquals("$dia $h", if (abajo) Manguera.AL_AGUA else Manguera.ADUJADA, EscenaAbismo.manguera(m))
            assertEquals("$dia $h", if (abajo) 2 else 3, EscenaAbismo.cascosColgados(m))
            if (abajo) assertEquals("$dia $h", Escala.ECHADA, EscenaAbismo.escala(m))
            if (fase == Fase.SUENO) { assertEquals(Escala.RECOGIDA, EscenaAbismo.escala(m)); assertTrue(EscenaAbismo.mantas(m)); assertEquals(0f, EscenaAbismo.esponjasNegras(m), 0f) }
            assertEquals("$dia $h", fase == Fase.CORRO || fase == Fase.ALBA || (hoyEs(m, "pascua") && (m.hora >= 23.0 || m.hora < 1.0)), EscenaAbismo.farol(m))
            assertTrue("La lamparilla del icono no se apaga nunca", EscenaAbismo.lamparilla(m))
            assertTrue(EscenaAbismo.esponjasNegras(m) in 0f..1f)
        }
        // Los días de fiesta a bordo, el del cometa y el del huracán no se baja; con tormenta, tampoco.
        cadaDia(7.0) { d, m ->
            if (EscenaAbismo.lugar(m) != Lugar.BARCO) return@cadaDia
            val sinBajar = hoyEs(m, "pascua") || hoyEs(m, "dormicion") || hoyEs(m, "cometa") || hoyEs(m, "huracan") || hoyEs(m, "paralisis") || m.ficha.clima == Clima.TORMENTA
            assertEquals(d.toString(), !sinBajar, EscenaAbismo.buzoAbajo(m))
        }
    }

    @Test fun lasEsponjasSiguenAlArcoConLosCortesDelCuento() {
        var antes: Float? = null
        cadaDia { d, m ->
            val arco = m.arco("esponjas")
            if (EscenaAbismo.lugar(m) != Lugar.BARCO) { assertEquals(d.toString(), 0f, EscenaAbismo.esponjas(m), 0f); assertEquals(0, EscenaAbismo.ristras(m)); antes = null; return@cadaDia }
            assertEquals(d.toString(), arco, EscenaAbismo.esponjas(m), 1e-6f)
            val carga = EscenaAbismo.carga(m)
            assertEquals(d.toString(), if (arco <= 0.2f) Carga.POCAS else if (arco <= 0.6f) Carga.RACIMOS else Carga.LLENO, carga)
            val n = EscenaAbismo.ristras(m)
            assertTrue(d.toString(), n in 1..14)
            assertEquals(d.toString(), n, AparejoAbismo.ristras(m).size)
            if (m.ficha.clima != Clima.TORMENTA && !hoyEs(m, "huracan")) antes?.let { if (arco >= it) assertTrue(d.toString(), n >= EscenaAbismo.ristras(momento(d.minusDays(1).toString())) || momento(d.minusDays(1).toString()).ficha.clima == Clima.TORMENTA) }
            antes = arco
        }
        // El día que se venden: la lonja (el arco cae de casi 1 a 0.1) y la vuelta (de 0.9 a 0).
        for (anio in listOf(2027, 2029, 2031)) {
            val vispera = momento("$anio-06-25"); val lonja = momento("$anio-06-26")
            assertTrue(vispera.arco("esponjas") > 0.9f); assertTrue(lonja.arco("esponjas") < 0.2f)
            assertTrue(EscenaAbismo.seVende(lonja)); assertTrue(EscenaAbismo.cargadoEnElMuelle(lonja))
            val vuelta = momento("$anio-12-18")
            assertTrue(EscenaAbismo.seVende(vuelta)); assertTrue(EscenaAbismo.vuelta(vuelta)); assertTrue(EscenaAbismo.cargadoEnElMuelle(vuelta))
            assertEquals(Carga.LLENO, EscenaAbismo.carga(momento("$anio-12-17")))
            assertEquals(Carga.POCAS, EscenaAbismo.carga(momento("$anio-01-27")))
        }
        cadaDia { d, m -> if (EscenaAbismo.seVende(m)) assertTrue(d.toString(), hoyEs(m, "lonja") || hoyEs(m, "vuelta")) }
    }

    @Test fun elMarSaleDelTiempoDelDia() {
        val vistos = HashSet<Mar>()
        cadaDia { d, m ->
            val mar = EscenaAbismo.mar(m); vistos += mar
            val esperado = when {
                hoyEs(m, "huracan") -> Mar.HURACAN
                m.ficha.clima == Clima.TORMENTA -> Mar.TORMENTA
                m.ficha.clima == Clima.VIENTO -> Mar.NORTE
                hoyEs(m, "calma") -> Mar.CALMA
                m.ficha.clima == Clima.DESPEJADO -> mar.also { assertTrue(d.toString(), it == Mar.CALMA || it == Mar.CLARA) }
                else -> Mar.CLARA
            }
            assertEquals(d.toString(), esperado, mar)
            val fondo = EscenaAbismo.fondo(m)
            assertTrue(d.toString(), fondo in 0f..1f)
            if (mar == Mar.TORMENTA || mar == Mar.HURACAN) assertEquals(d.toString(), 0f, fondo, 0f)
            if (mar == Mar.NORTE) assertTrue(d.toString(), fondo < 0.2f)
            if (mar == Mar.CALMA && !hoyEs(m, "hondo") && !hoyEs(m, "hondo_otra_vez")) assertEquals(d.toString(), 1f, fondo, 0f)
            if (hoyEs(m, "huracan")) assertEquals(Costa.ENSENADA, EscenaAbismo.costa(m))
            if (hoyEs(m, "banco_nuevo")) assertEquals(Costa.RAYA, EscenaAbismo.costa(m))
        }
        assertEquals(Mar.entries.toSet(), vistos)
        assertEquals(Mar.CALMA, EscenaAbismo.mar(momento("2028-11-10")))      // «Una calma de cinco días»
        assertEquals(Mar.HURACAN, EscenaAbismo.mar(momento("2027-10-18")))
    }

    @Test fun lasFiestasVanPorElCalendarioJuliano() {
        assertEquals("01-07", EscenaAbismo.juliana("12-25")); assertEquals("01-19", EscenaAbismo.juliana("01-06")); assertEquals("12-19", EscenaAbismo.juliana("12-06"))
        val vistas = HashSet<Fiesta>()
        cadaDia { d, m ->
            val f = EscenaAbismo.fiestaDeHoy(m) ?: return@cadaDia
            vistas += f
            if (f.juliana && d.year < 2032) assertEquals("$d $f", EscenaAbismo.juliana(EscenaAbismo.GREGORIANA.getValue(f)), md(d))
        }
        assertTrue(vistas.containsAll(listOf(Fiesta.NAVIDAD, Fiesta.SAN_BASILIO, Fiesta.EPIFANIA, Fiesta.PASCUA, Fiesta.DORMICION, Fiesta.SAN_NICOLAS, Fiesta.CUATRO_DE_JULIO, Fiesta.BODA)))
        cadaDia { d, m ->
            assertEquals(d.toString(), m.con("fiesta"), EscenaAbismo.fiesta(m))
            assertEquals(d.toString(), hoyEs(m, "epifania"), EscenaAbismo.cruz(m))
            if (EscenaAbismo.cruz(m)) { assertEquals(d.toString(), "01-19", md(d)); assertEquals(Lugar.MUELLE, EscenaAbismo.lugar(m)) }
            assertEquals(d.toString(), hoyEs(m, "vuelta"), EscenaAbismo.vuelta(m))
            if (EscenaAbismo.vuelta(m)) assertEquals(d.toString(), "12-18", md(d))
            assertEquals(d.toString(), hoyEs(m, "titanic") || hoyEs(m, "perdido"), EscenaAbismo.luto(m))
            if (EscenaAbismo.cometa(m) > 0f) assertTrue(d.toString(), m.ficha.temporada == 1)
        }
    }

    @Test fun loEncendidoEnElMuelleSigueLaHora() {
        val m21 = momento("2027-01-12", 21.0); val m13 = momento("2027-01-12", 13.0)
        assertEquals(1f, EscenaAbismo.lucesDelMuelle(m21).faroles, 0f); assertEquals(0f, EscenaAbismo.lucesDelMuelle(m13).faroles, 0f)
        assertEquals(1f, EscenaAbismo.lucesDelMuelle(m21).cafes, 0f); assertEquals(0f, EscenaAbismo.lucesDelMuelle(m13).cafes, 0f)
        assertTrue(EscenaAbismo.lucesDelMuelle(momento("2027-01-12", 18.0)).iglesia > 0f)
        assertEquals(1f, EscenaAbismo.lucesDelMuelle(momento("2027-01-19", 20.0)).iglesia, 0f)       // la Epifanía, toda la tarde
        assertEquals(0f, EscenaAbismo.lucesDelMuelle(momento("2030-06-20", 21.0)).faroles, 0f)      // el cuarto: ningún barco
        assertTrue(EscenaAbismo.lucesDelMuelle(m21).lamparillas)
        assertTrue(EscenaAbismo.farolesLejos(momento("2027-04-03", 21.0))); assertFalse(EscenaAbismo.farolesLejos(momento("2027-04-03", 13.0)))
        assertTrue(EscenaAbismo.cohetes(momento("2027-12-31", 23.0))); assertFalse(EscenaAbismo.cohetes(momento("2027-12-31", 13.0)))
        assertEquals(Estacion.LLUVIAS, momento("2030-06-20").ficha.estacion); assertTrue(EscenaAbismo.veranoEnTierra(momento("2030-06-20")))
    }

    @Test fun losObjetosSonLosDelCalendarioYNoSalenDosVeces() {
        val delCalendario = sortedSetOf<String>()
        cadaDia { _, m -> m.ficha.objeto?.let { delCalendario += it } }
        assertEquals(ObjetosAbismo.TODOS.toSortedSet(), delCalendario)
        assertTrue(ObjetosAbismo.TODOS.containsAll(ObjetosAbismo.GRANDES + ObjetosAbismo.EN_EL_CUADRO + ObjetosAbismo.QUITA_DEL_CUADRO))
        val base = momento("2027-04-03").ficha
        for (pieza in ObjetosAbismo.QUITA_DEL_CUADRO) {
            assertFalse(pieza, ObjetosAbismo.elCuadroPinta(Momento(base.copy(objeto = pieza), 13.0), pieza))
            assertTrue(pieza, ObjetosAbismo.elCuadroPinta(Momento(base.copy(objeto = "pan"), 13.0), pieza))
        }
    }

    @Test fun elGatoTieneSueloLuzYSusSitiosEnLosDosCuadros() {
        fun valido(c: Color) = c.red in 0f..1f && c.green in 0f..1f && c.blue in 0f..1f && c.alpha == 1f
        val talla = EscenaAbismo.tallaDelGato; val medio = Gatuno.MEDIO_LARGO * talla; val vistos = HashSet<Cuadro>()
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) {
            for (h in listOf(3.0, 7.0, 13.0, 18.5, 21.0)) {
                val m = momento(d.toString(), h); val anclas = EscenaAbismo.anclas(m); vistos += EscenaAbismo.cuadro(m)
                val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
                assertTrue("$d $h: el suelo es corto", suelo.hasta - suelo.x >= 2 * medio + 30f && suelo.hasta - suelo.x >= 110f)
                assertTrue("$d $h: el gato no cabe", suelo.y - Gatuno.ALTO * talla > 150f && suelo.y <= 285f)
                for (t in listOf(TipoAncla.MIRADOR, TipoAncla.OBJETO, TipoAncla.ABRIGO)) assertEquals("$d $h $t", 1, anclas.count { it.tipo == t })
                for (a in anclas) { assertTrue("$d $h ${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO); if (a.tipo == TipoAncla.OBJETO || a.tipo == TipoAncla.ABRIGO) assertTrue("$d $h ${a.tipo}", a.x in suelo.x..suelo.hasta) }
                val quieto = gatoQuieto(suelo, anclas, talla, EscenaAbismo.estorbos(m))
                assertEquals(AnimGato.SENTADO, quieto.anim)
                for (x in listOf(suelo.x, quieto.x, suelo.hasta) + anclas.map { it.x }) {
                    val luz = EscenaAbismo.luzDelGato(m, Offset(x, suelo.y))
                    for (c in listOf(Color(0xFF8C9FAE), Color.White, Color.Black, Color(0xFFC9A24A))) assertTrue("$d $h: un color del gato no es válido", valido(luz.tono(c)))
                    assertTrue("$d $h: la luz del gato", luz.rayo.fuerza in 0f..1f && luz.rayo.contraste in 0f..1f && luz.rayo.ax.isFinite() && luz.rayo.ay.isFinite() && luz.noche in 0f..1f)
                }
            }
            d = d.plusDays(5)
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.MUELLE), vistos)
    }

    @Test fun elAguaSiempreSeMueveYElMismoMomentoDaLoMismo() {
        cadaDia { d, m -> assertTrue("$d", EscenaAbismo.seMueve(m)) }
        val a = momento("2027-04-03", 13.0); val b = momento("2027-04-03", 13.0)
        assertEquals(EscenaAbismo.mar(a), EscenaAbismo.mar(b)); assertEquals(EscenaAbismo.costa(a), EscenaAbismo.costa(b))
        assertEquals(EscenaAbismo.anclas(a).map { it.x to it.y }, EscenaAbismo.anclas(b).map { it.x to it.y })
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /**
     * Los dos cuadros se dibujan enteros (la parte quieta y la viva) con cada uno de los doce objetos y sin objeto, a las
     * horas de cada fase, con cada estado del mar (calma, agua clara, norte, tormenta, huracán), en los días señalados (la
     * lonja, el 4 de julio, zarpar, la vuelta, la Epifanía, fin de año, Pascua, el cometa, el buzo de al lado, el luto) y en
     * el cuarto, sin lanzar nada.
     */
    @Test fun losDosCuadrosSeDibujanConCadaObjetoACadaHoraYConCadaMar() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        val dias = listOf("2027-04-03", "2027-06-20", "2027-10-18", "2028-11-10", "2027-05-01", "2027-05-19", "2027-08-10", "2029-04-17", "2027-01-12", "2027-01-19",
            "2027-12-18", "2027-12-31", "2027-06-26", "2027-07-04", "2027-01-26", "2030-06-20", "2031-02-17")
        val vistos = HashSet<Cuadro>(); val mares = HashSet<Mar>()
        for (dia in dias) for (h in listOf(4.75, 7.0, 13.0, 18.5, 21.0, 23.5)) {
            val base = momento(dia, h); vistos += EscenaAbismo.cuadro(base)
            for (o in ObjetosAbismo.TODOS + listOf(null)) for (clima in listOf(base.ficha.clima, Clima.VIENTO, Clima.TORMENTA, Clima.LLUVIA)) {
                if (clima != base.ficha.clima && o != null && o != "casco") continue
                if (o != null && h != 13.0 && dia != "2027-01-12" && dia != "2027-04-03") continue
                val m = Momento(base.ficha.copy(objeto = o, clima = clima), h); mares += EscenaAbismo.mar(m)
                CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                    lienzo { with(EscenaAbismo) { quieta(m) } }
                    lienzo { with(EscenaAbismo) { viva(m, 3.7f) } }
                }
            }
        }
        assertEquals(setOf(Cuadro.BARCO, Cuadro.MUELLE), vistos)
        assertEquals(Mar.entries.toSet(), mares)
    }
}
