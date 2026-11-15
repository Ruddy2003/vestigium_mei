package io.github.ruddy2003.vestigium

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.BarcoBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Velas
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.Sol3
import io.github.ruddy2003.vestigium.ui.escenas.VelasBarco
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
import kotlin.math.abs

/**
 * El bergantín de Botánico (tanda S13, cuadro A): la luz que camina por la cubierta con la hora (la sombra de la amurada
 * por la mañana, la de las velas a mediodía, la de la amurada de babor por la tarde), la noche a dos luces, las velas
 * que dan sombra solo si van largas, la penumbra de lo que queda a la sombra, el sitio de las cosas (la mesa, los
 * cajones, el cañón en su porta) frente al paso del gato, y que nada de las velas tape la ventana de los astros.
 */
class EscenaBotanicoBarcoPrueba {
    private fun momento(dia: String, hora: Double = 10.0, clima: Clima? = null): Momento {
        val f = FichaDelDia.de(dia, "naturalista")!!
        return Momento(if (clima == null) f else f.copy(clima = clima), hora)
    }

    /** Un día de trópico navegando, despejado: el 21 de marzo de 2027 (Bahía a Río). */
    private val DIA = "2027-03-21"

    @Test fun laLuzCaminaConLaHora() {
        val m7 = momento(DIA, 7.0, Clima.DESPEJADO); val m13 = momento(DIA, 13.0, Clima.DESPEJADO); val m17 = momento(DIA, 17.5, Clima.DESPEJADO)
        assertEquals(Velas.LARGAS, EscenaBotanico.velas(m13))
        val (s7, s13, s17) = listOf(m7, m13, m17).map { Sol3(BarcoBotanico.rayoDelBarco(EscenaBotanico.luces(it))) }
        // Por la mañana el sol viene de estribor y la sombra cae a babor; por la tarde, al revés; a mediodía, más corta.
        assertTrue(s7.sx < 0f); assertTrue(s17.sx > 0f)
        assertTrue(abs(s7.sx) + abs(s7.sz) > abs(s13.sx) + abs(s13.sz))
        for (s in listOf(s7, s13, s17)) assertTrue(abs(s.sx) <= BarcoBotanico.COT_MAX + 0.01f && s.sz >= 0f)
        // La amurada de estribor deja su sombra por la mañana; la de babor, por la tarde.
        assertNotNull(BarcoBotanico.sombraDeLaAmurada(s7, true)); assertNull(BarcoBotanico.sombraDeLaAmurada(s7, false))
        assertNotNull(BarcoBotanico.sombraDeLaAmurada(s17, false)); assertNull(BarcoBotanico.sombraDeLaAmurada(s17, true))
        // Lo de la banda de estribor (los cajones) queda en penumbra por la mañana y al sol por la tarde; la mesa, al sol por la mañana.
        val v7 = BarcoBotanico.Vista(m7); val v17 = BarcoBotanico.Vista(m17)
        assertEquals(1f, v7.sombraEn(1.2f, 3.6f, 0.3f), 0f); assertEquals(0f, v17.sombraEn(1.2f, 3.6f, 0.3f), 0f)
        assertEquals(0f, v7.sombraEn(-0.55f, 3.62f, BarcoBotanico.MESA_ALTO), 0f)
        // A mediodía la mayor echa su sombra en medio de la cubierta, entre el palo mayor y el trinquete.
        val v13 = BarcoBotanico.Vista(m13)
        assertEquals(1f, v13.sombraEn(-2.3f, 15f), 0f)
        // Y las tres horas dan tres manchas distintas.
        assertTrue(v7.manchas.size != v13.manchas.size || v7.manchas != v13.manchas)
        assertTrue(v13.manchas != v17.manchas)
    }

    @Test fun lasVelasDanSombraSoloSiVanLargas() {
        var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val m = momento(d.toString())
            if (EscenaBotanico.cuadro(m) == Cuadro.BARCO) {
                val velas = VelasBarco.velasQueDanSombra(m)
                if (EscenaBotanico.velas(m) == Velas.AFERRADAS) assertTrue("$d", velas.isEmpty()) else assertEquals("$d", 2, velas.size)
            }
            d = d.plusDays(9)
        }
    }

    @Test fun deNocheHayDosLuces() {
        val m = momento(DIA, 21.0, Clima.DESPEJADO); val v = BarcoBotanico.Vista(m)
        // La cálida: la cabina por la claraboya (el diario), la bitácora y el farol de popa.
        assertTrue(v.lu.claraboya > 0.5f && v.lu.bitacora > 0.5f && v.lu.farol > 0.5f)
        // La fría: la luna, que da su sombra.
        assertTrue(v.luna > 0.1f); assertTrue(v.r.fuerza > 0.05f && v.r.dia == 0f)
        // Lo que está junto a la claraboya se pinta con su calor; lo de la banda de estribor lejos de ella, no.
        assertNotNull(v.calido(BarcoBotanico.LUZ_CLARABOYA))
        // De día, ninguna lumbre: el pincel es el del sol.
        assertNull(BarcoBotanico.Vista(momento(DIA, 13.0, Clima.DESPEJADO)).calido(BarcoBotanico.LUZ_CLARABOYA))
        // Con el cielo cubierto no hay luna, pero siguen las lumbres.
        val cubierto = BarcoBotanico.Vista(momento(DIA, 21.0, Clima.LLUVIA))
        assertEquals(0f, cubierto.luna, 0f); assertTrue(cubierto.lu.claraboya > 0.5f)
    }

    @Test fun lasCosasEstanDondeDebenYElGatoPasa() {
        val m = momento(DIA, 9.0)
        val suelo = BarcoBotanico.anclas(m).single { it.tipo == io.github.ruddy2003.vestigium.ui.escenas.TipoAncla.SUELO }
        // La mesa pisa detrás del paso del gato (sus patas de delante, más arriba que su línea de suelo).
        val pataDelante = BarcoBotanico.SITIO_MESA.p(0f, 0f, -BarcoBotanico.MESA_FONDO * 0.45f)
        assertTrue(pataDelante.y < suelo.y)
        // Los cajones, contra la amurada y fuera del paso del gato o detrás de él.
        for (h in BarcoBotanico.HUECOS) { val q = BarcoBotanico.pc(h[0], 0f, h[1]); assertTrue(q.x > suelo.hasta || q.y < suelo.y) }
        // El cañón, arrimado a su porta: la boca llega a la amurada.
        val boca = BarcoBotanico.SITIO_CANON.mx(1.15f, 0f)
        assertEquals(BarcoBotanico.estribor(BarcoBotanico.Z_CANON), boca, 0.15f)
        // El objeto del día: en el tablero, dentro del lienzo y bien grande (más de un metro a 60 unidades).
        val sop = BarcoBotanico.soporte(m)
        assertTrue(sop.x in 0f..ANCHO && sop.y in 0f..ALTO)
        assertTrue(BarcoBotanico.escalaEn(3.6f) > 1.2f)
    }

    @Test fun nadaTapaLaVentanaDeLosAstros() {
        var d = LocalDate.of(2027, 1, 1)
        while (d.year <= 2031) {
            val m = momento(d.toString())
            if (EscenaBotanico.cuadro(m) == Cuadro.BARCO) for (q in VelasBarco.loQueCruzaElCielo(m)) assertFalse("$d $q", q.x in 272f..328f && q.y in 47f..108f)
            d = d.plusDays(13)
        }
    }

    private val sePuedeDibujar: Boolean by lazy { runCatching { ImageBitmap(1, 1) }.isSuccess }

    /** Se dibuja entero en cada tramo del viaje (cada costa), de día, al atardecer y de noche, sin lanzar nada. */
    @Test fun seDibujaEnCadaTramo() {
        Assume.assumeTrue("esta JVM no tiene con qué dibujar (se dibuja en el arnés)", sePuedeDibujar)
        val ancho = 190; val alto = (ancho * ALTO / ANCHO).toInt()
        val lienzoImg = Canvas(ImageBitmap(ancho, alto))
        for (tr in EscenaBotanico.ITINERARIO) {
            val anio = 2026 + tr.temporada
            for (h in listOf(7.0, 13.0, 19.5, 21.0)) {
                val m = momento("$anio-${tr.desde}", h)
                try {
                    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, lienzoImg, Size(ancho.toFloat(), alto.toFloat())) {
                        lienzo { with(EscenaBotanico) { quieta(m) } }
                        lienzo { with(EscenaBotanico) { viva(m, 2.3f) } }
                    }
                } catch (x: Exception) {
                    throw AssertionError("$anio-${tr.desde} $h: $x", x)
                }
            }
        }
    }
}
