package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.AparejoAbismo
import io.github.ruddy2003.vestigium.ui.escenas.BarcoAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.MarAbismo
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuadro del barco de Abismo (tanda S16): lo que decide la luz y lo que cambia con la hora sale de funciones que se
 * pueden leer, y aquí se comprueba que hace lo que el cuadro dice: la sombra del barco en el fondo camina con la hora; de
 * noche hay tres luces (el farol, la lamparilla y la luna); el farol, los trajes, las esponjas limpias y los cascos siguen
 * el horario de Nikos; el agua deja ver el fondo cerca y lo tapa lejos; el buzo y la hornacina quedan dentro del cuadro.
 */
class EscenaAbismoBarcoPrueba {
    private fun momento(dia: String, hora: Double, objeto: String? = "pan", clima: Clima? = null): Momento {
        val f = FichaDelDia.de(dia, "buzo_esponjas")!!
        return Momento(f.copy(objeto = objeto, clima = clima ?: f.clima), hora)
    }

    /** Un día de barco, despejado y de bajar: el 3 de abril de 2027. */
    private val DIA = "2027-04-03"

    @Test fun elDiaDePruebaEsUnDiaDeBarcoDespejado() {
        val m = momento(DIA, 13.0)
        assertEquals(Lugar.BARCO, EscenaAbismo.lugar(m))
        assertEquals(Clima.DESPEJADO, m.ficha.clima)
        assertTrue(EscenaAbismo.buzoAbajo(momento(DIA, 7.0)))
    }

    @Test fun laSombraDelBarcoEnElFondoCaminaConLaHora() {
        fun corre(h: Double) = BarcoAbismo.corrimientoDeLaSombra(MarAbismo.agua(BarcoAbismo.Vista(momento(DIA, h))))
        val manana = corre(7.0); val mediodia = corre(13.0); val tarde = corre(18.0)
        // Por la mañana cae bajo el barco (a babor); por la tarde se tiende hacia fuera, a estribor, donde se ve.
        assertTrue("mañana $manana, mediodía $mediodia", manana.x < mediodia.x)
        assertTrue("mediodía $mediodia, tarde $tarde", mediodia.x < tarde.x)
        assertTrue("la tarde la tiende hacia fuera: $tarde", tarde.x > 1.5f)
        for (c in listOf(manana, mediodia, tarde)) assertTrue("$c", c.x in -3f..4.5f && c.y in 0.3f..3.5f)
        // Y el sol del barco cambia de lado de la mañana a la tarde: la sombra calada de la jarcia cruza las tablas de un lado y del otro.
        val r7 = BarcoAbismo.Vista(momento(DIA, 7.0)).r; val r18 = BarcoAbismo.Vista(momento(DIA, 18.0)).r
        assertTrue("${r7.lat} ${r18.lat}", r7.lat > 0f && r18.lat < 0f)
        assertTrue("la sombra larga por la mañana, corta a mediodía", r7.cot > BarcoAbismo.Vista(momento(DIA, 13.0)).r.cot)
    }

    @Test fun deNocheHayTresLuces() {
        val v = BarcoAbismo.Vista(momento(DIA, 21.0))
        assertTrue("el farol, bajo: ${v.lu.farol}", v.lu.farol > 0.3f && v.lu.farol < 1f)
        assertTrue("la lamparilla: ${v.lu.lamparilla}", v.lu.lamparilla > 0.6f)
        assertTrue("la luna: ${v.luna}", v.luna > 0.05f)
        assertTrue("la luna tumba sus sombras: ${v.r.fuerza}", v.r.fuerza > 0.05f)
        // Lo cercano al farol se tiñe de su luz (los cascos, la bomba, la tina, el palo); lo cercano a la lamparilla, también.
        for (en in listOf(BarcoAbismo.SITIO_BOMBA.pie, BarcoAbismo.SITIO_TINA.pie, BarcoAbismo.pb(AparejoAbismo.GANCHOS[1].x, AparejoAbismo.GANCHOS[1].y - 0.4f, AparejoAbismo.GANCHOS[1].z)))
            assertTrue("$en: ${v.pincel(en).calida}", v.pincel(en).calida > 0.35f)
        assertTrue(v.pincel(BarcoAbismo.LAMPARILLA + androidx.compose.ui.geometry.Offset(-10f, 20f)).calida > 0.3f)
        // Y cada una tiene un lado de cada color: la cara que mira al farol, naranja; la otra, azul violeta.
        val pb = v.pincel(BarcoAbismo.SITIO_BOMBA.pie)
        val luz = pb.luz(io.github.ruddy2003.vestigium.ui.escenas.ColorAbismo.COBRE); val frio = io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ladoFrio(pb)
        assertTrue("$luz", luz.red > luz.blue + 0.2f)
        assertTrue("$frio", frio.alpha > 0.25f && frio.blue > frio.red)
        // De día no hay lado frío que añadir: la sombra ya es la del sol.
        assertEquals(0f, io.github.ruddy2003.vestigium.ui.escenas.PiezasAbismo.ladoFrio(BarcoAbismo.Vista(momento(DIA, 13.0)).pincel(BarcoAbismo.SITIO_BOMBA.pie)).alpha, 0f)
        // Al caer la tarde (el corro), el farol ya calienta lo que tiene cerca.
        assertTrue(BarcoAbismo.Vista(momento(DIA, 19.5)).pincel(BarcoAbismo.SITIO_BOMBA.pie).calida > 0.15f)
        // Más tarde se apaga el farol: quedan la lamparilla y la luna.
        val tarde = BarcoAbismo.Vista(momento(DIA, 23.0))
        assertEquals(0f, tarde.lu.farol, 0f)
        assertTrue(tarde.lu.lamparilla > 0.6f && tarde.luna > 0.05f)
    }

    @Test fun elFarolSigueElHorario() {
        assertEquals(1f, BarcoAbismo.farolDeCubierta(momento(DIA, 19.0)), 0f)            // el corro
        assertEquals(1f, BarcoAbismo.farolDeCubierta(momento(DIA, 4.75)), 0f)            // el bocado de antes de bajar
        assertEquals(BarcoAbismo.FAROL_BAJO, BarcoAbismo.farolDeCubierta(momento(DIA, 21.0)), 0f)
        for (h in listOf(7.0, 13.0, 23.0, 2.0)) assertEquals("$h", 0f, BarcoAbismo.farolDeCubierta(momento(DIA, h)), 0f)
        assertEquals(0f, BarcoAbismo.farolDeCubierta(momento("2027-01-12", 19.0)), 0f)  // en el muelle, no
    }

    @Test fun lasTresHorasSonTresCuadros() {
        val m7 = momento(DIA, 7.0); val m13 = momento(DIA, 13.0); val m21 = momento(DIA, 21.0)
        // 7:00: se bucea. Un traje tendido, dos cascos en el palo y el de Nikos no está (lo lleva puesto), nada limpio todavía.
        assertEquals(1, BarcoAbismo.trajesASecar(m7)); assertEquals(2, AparejoAbismo.cascosHoy(m7)); assertEquals(0f, BarcoAbismo.esponjasLimpias(m7), 0f)
        assertTrue(!BarcoAbismo.cascoEnCubierta(m7)); assertEquals(2, AparejoAbismo.cascosEnElPalo(m7))
        // 13:00: se limpian esponjas. El traje, dos cascos en el palo y el de Nikos en la cubierta (no tres en columna), las limpias que crecen.
        assertEquals(1, BarcoAbismo.trajesASecar(m13)); assertEquals(3, AparejoAbismo.cascosHoy(m13))
        assertTrue(BarcoAbismo.cascoEnCubierta(m13)); assertEquals(2, AparejoAbismo.cascosEnElPalo(m13))
        // Lo que se planta en el paso del gato (el casco, las limpias) está en su tramo.
        val suelo = EscenaAbismo.anclas(m13).single { it.tipo == io.github.ruddy2003.vestigium.ui.escenas.TipoAncla.SUELO }
        assertEquals(2, BarcoAbismo.estorbos(m13).size)
        for (x in BarcoAbismo.estorbos(m13)) assertTrue("$x", x in suelo.x..suelo.hasta)
        assertTrue(BarcoAbismo.esponjasLimpias(m13) > 0f && BarcoAbismo.esponjasLimpias(m13) < BarcoAbismo.esponjasLimpias(momento(DIA, 16.5)))
        // 21:00: se duerme. Las mantas, ningún traje, nada por limpiar.
        assertTrue(EscenaAbismo.mantas(m21)); assertEquals(0, BarcoAbismo.trajesASecar(m21)); assertEquals(0f, BarcoAbismo.esponjasLimpias(m21), 0f)
        assertEquals(0f, EscenaAbismo.esponjasNegras(m21), 0f)
        // El día que el objeto es el traje o el casco, el cuadro no los repite.
        assertEquals(0, BarcoAbismo.trajesASecar(momento(DIA, 13.0, objeto = "escafandra")))
        assertEquals(2, AparejoAbismo.cascosHoy(momento(DIA, 13.0, objeto = "casco"))); assertTrue(!BarcoAbismo.cascoEnCubierta(momento(DIA, 13.0, objeto = "casco")))
        assertEquals(1, AparejoAbismo.cascosHoy(momento(DIA, 7.0, objeto = "casco")))
        // Un día que no se baja (Pascua) no hay esponjas que limpiar; se asa el cordero en cubierta «desde que amanece».
        assertEquals(0f, BarcoAbismo.esponjasLimpias(momento("2027-05-01", 14.0)), 0f)
        assertTrue(BarcoAbismo.cordero(momento("2027-05-01", 9.0))); assertTrue(!BarcoAbismo.cordero(momento("2027-05-01", 21.0))); assertTrue(!BarcoAbismo.cordero(momento(DIA, 9.0)))
    }

    @Test fun losObjetosGrandesTienenSuSitioYNadaSaleDosVeces() {
        for (o in listOf("escafandra", "botas", "cuerda", "red")) {
            val m = momento(DIA, 13.0, objeto = o)
            // El casco de Nikos se aparta a la escotilla, la manta de delante no se tiende y lo plantado sigue en el paso del gato.
            assertTrue(o, BarcoAbismo.cascoEnLaEscotilla(m))
            assertEquals(o, 2, BarcoAbismo.mantasHoy(momento(DIA, 21.0, objeto = o)))
            val suelo = EscenaAbismo.anclas(m).single { it.tipo == io.github.ruddy2003.vestigium.ui.escenas.TipoAncla.SUELO }
            for (x in BarcoAbismo.estorbos(m)) assertTrue("$o $x", x in suelo.x..suelo.hasta)
            // Lo que el grande tiende a la derecha de su pie no pisa el casco ni las limpias.
            val pie = BarcoAbismo.PIE_GRANDE; val limpias = BarcoAbismo.limpiasEn(m).let { BarcoAbismo.pb(it.x, 0f, it.y) }
            assertTrue("$o: las limpias, a la derecha del grande", limpias.x - pie.x > 0.9f * io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.METRO * BarcoAbismo.ESCALA_GRANDE)
        }
        assertEquals(3, BarcoAbismo.mantasHoy(momento(DIA, 21.0, objeto = "pan"))); assertTrue(!BarcoAbismo.cascoEnLaEscotilla(momento(DIA, 13.0, objeto = "pan")))
        // Nada dos veces: el traje tendido, las botas, el casco de la cubierta y la aduja de cabo se quitan el día que son el objeto.
        assertEquals(0, BarcoAbismo.trajesASecar(momento(DIA, 13.0, objeto = "escafandra")))
        assertTrue(!BarcoAbismo.cascoEnCubierta(momento(DIA, 13.0, objeto = "casco")))
        assertTrue(!BarcoAbismo.adujaDeCabo(momento(DIA, 13.0, objeto = "cuerda"))); assertTrue(BarcoAbismo.adujaDeCabo(momento(DIA, 13.0, objeto = "pan")))
        // Y nada cuelga delante del objeto chico, sobre la tapa de la bomba: el farol queda por encima.
        assertTrue(BarcoAbismo.FAROL.y + 0.25f * BarcoAbismo.K_FAROL < BarcoAbismo.soporte().y - 0.45f * io.github.ruddy2003.vestigium.ui.escenas.ObjetosAbismo.METRO * BarcoAbismo.escalaEn(BarcoAbismo.SITIO_BOMBA.z0))
    }

    @Test fun elAguaDejaVerElFondoCercaYLoTapaLejos() {
        val a = MarAbismo.agua(BarcoAbismo.Vista(momento(DIA, 13.0)))
        assertTrue(a.transparencia(ALTO - 2f) > 0.6f)
        assertTrue(a.transparencia(BarcoAbismo.H + 8f) < 0.05f)
        assertTrue(a.absorbe(ALTO - 2f) < a.absorbe(BarcoAbismo.H + 30f))
        // Con tormenta no se ve ni la mano; de noche, tampoco.
        assertEquals(0f, MarAbismo.agua(BarcoAbismo.Vista(momento(DIA, 13.0, clima = Clima.TORMENTA))).transparencia(ALTO - 2f), 0f)
        assertEquals(0f, MarAbismo.agua(BarcoAbismo.Vista(momento(DIA, 23.0))).transparencia(ALTO - 2f), 0f)
        // La luz que entra en el agua, más empinada que en el aire: la sombra de lo que nada se corre menos que la de la cubierta.
        assertTrue(a.corre.getDistance() <= 1.05f)
    }

    @Test fun elBuzoYLaHornacinaQuedanEnElCuadro() {
        val b = MarAbismo.burbujas()
        assertTrue("las burbujas: $b", b.x in 0f..ANCHO && b.y in BarcoAbismo.H..ALTO && b.x > MarAbismo.bordeDelAgua(b.y) + 20f)
        val caja = MarAbismo.iconoCaja()
        assertTrue("la hornacina: $caja", caja.left > 0f && caja.right < ANCHO && caja.top > BarcoAbismo.H && caja.top < ALTO)
        // El farol cuelga dentro del cuadro, por encima de la bomba.
        assertTrue(BarcoAbismo.FAROL.y < BarcoAbismo.SITIO_BOMBA.pie.y - 40f && BarcoAbismo.FAROL.x in 0f..ANCHO)
    }

    @Test fun todosLosDiasDeBarcoTienenSuLuz() {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2031) {
            for (h in listOf(7.0, 13.0, 21.0)) {
                val m = momento(d.toString(), h)
                if (EscenaAbismo.lugar(m) != Lugar.BARCO) continue
                val v = BarcoAbismo.Vista(m)
                assertTrue("$d $h", v.lu.farol in 0f..1f && v.lu.lamparilla in 0f..1f && v.luna in 0f..1f)
                val c = BarcoAbismo.corrimientoDeLaSombra(MarAbismo.agua(v))
                assertTrue("$d $h $c", c.x.isFinite() && c.y.isFinite())
                assertTrue("$d $h", BarcoAbismo.trajesASecar(m) in 0..1 && AparejoAbismo.cascosHoy(m) in 1..3 && AparejoAbismo.cascosEnElPalo(m) in 1..2)
            }
            d = d.plusDays(3)
        }
    }
}
