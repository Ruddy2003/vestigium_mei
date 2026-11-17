package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Flota
import io.github.ruddy2003.vestigium.ui.escenas.EscenaAbismo.Lugar
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.MuelleAbismo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import kotlin.math.abs

/**
 * El cuadro del muelle de Abismo (tanda S16): su composición (el canto del muelle sale del borde de abajo y fuga al mismo
 * punto que la fila de barcos; nada alto tapa la zona de los astros), la reja de sombras que camina con la hora, la flota
 * de cada día (llena en invierno, unos pocos los días de tierra del verano, saliendo la mañana de zarpar, ninguno en el
 * cuarto), el barco de Nikos y lo que cuelga de su botavara.
 */
class EscenaAbismoMuellePrueba {
    private fun momento(dia: String, hora: Double = 13.0, clima: Clima? = null): Momento {
        val f = FichaDelDia.de(dia, "buzo_esponjas")!!
        return Momento(if (clima != null) f.copy(clima = clima) else f, hora)
    }

    /** Todos los días que se pinta el muelle (el invierno, los días de tierra y el cuarto), de 2026 a 2032, a la hora [hora]. */
    private fun diasDelMuelle(hora: Double, hacer: (LocalDate, Momento) -> Unit) {
        var d = LocalDate.of(2026, 1, 1)
        while (d.year <= 2032) { val m = momento(d.toString(), hora); if (EscenaAbismo.cuadro(m) == EscenaAbismo.Cuadro.MUELLE) hacer(d, m); d = d.plusDays(1) }
    }

    @Test fun elCantoDelMuelleSaleDelBordeDeAbajoYFugaConLaFila() {
        // No hay repisa: el canto del muelle (x = 0) cruza el borde de abajo hacia el centro y se aleja en diagonal.
        var z = 0.5f; while (MuelleAbismo.pm(0f, 0f, z).y > ALTO) z += 0.01f
        val abajo = MuelleAbismo.pm(0f, 0f, z)
        assertTrue("el canto sale del borde de abajo en x = ${abajo.x}", abajo.x in 100f..230f)
        val lejos = MuelleAbismo.pm(0f, 0f, 5000f)
        assertTrue("el canto se aleja hacia la derecha y arriba", lejos.x > abajo.x + 40f && lejos.y < abajo.y - 100f)
        // Las tablas, el canto y la fila de barcos fugan al mismo punto, en el horizonte.
        for (x in listOf(0f, 1.5f, MuelleAbismo.FILA_DENTRO, MuelleAbismo.FILA_FUERA, -MuelleAbismo.RIO)) {
            val q = MuelleAbismo.pm(x, MuelleAbismo.AGUA, 1e6f)
            assertEquals("fuga de x = $x", MuelleAbismo.FUGA.x, q.x, 0.5f); assertEquals(MuelleAbismo.H, MuelleAbismo.FUGA.y, 0f)
        }
        // El punto de fuga queda a la derecha, para que el río ocupe el centro y la calle de la luna caiga en el agua.
        assertTrue(MuelleAbismo.FUGA.x in 260f..320f)
    }

    @Test fun nadaAltoTapaLaZonaDeLosAstros() {
        val zona = MuelleAbismo.ZONA_DE_LOS_ASTROS
        for (dia in listOf("2027-01-12", "2027-12-18", "2027-06-26", "2027-07-04", "2027-01-26", "2031-02-17", "2030-06-20"))
            for (h in listOf(7.0, 13.0, 21.0)) {
                val m = momento(dia, h)
                for (q in MuelleAbismo.altos(m)) assertFalse("$dia $h: algo alto en la zona de los astros en $q", zona.contains(q))
            }
    }

    @Test fun laRejaDeSombrasCaminaConLaHora() {
        val l7 = EscenaAbismo.luces(momento("2027-01-12", 7.0)); val l13 = EscenaAbismo.luces(momento("2027-01-12", 13.0)); val l17 = EscenaAbismo.luces(momento("2027-01-12", 17.0))
        val s7 = MuelleAbismo.sombraEnLasTablas(l7); val s13 = MuelleAbismo.sombraEnLasTablas(l13); val s17 = MuelleAbismo.sombraEnLasTablas(l17)
        assertNotNull(s7); assertNotNull(s13); assertNotNull(s17)
        // Por la mañana el sol está a la derecha del cuadro y las sombras van hacia el agua; por la tarde, hacia la calle.
        assertTrue("7:00 hacia el agua: $s7", s7!!.x < -0.5f)
        assertTrue("17:00 hacia la calle: $s17", s17!!.x > 0.2f)
        // A mediodía, cortas.
        assertTrue("13:00 más cortas que a las 7:00", s13!!.getDistance() < s7.getDistance())
        // Con lluvia no hay sombras arrojadas; de noche, solo las de la luna (y no sin ella).
        assertNull(MuelleAbismo.sombraEnLasTablas(EscenaAbismo.luces(momento("2027-01-12", 13.0, Clima.LLUVIA))))
        val noche = momento("2027-01-12", 21.0); val ln = EscenaAbismo.luces(noche)
        if (EscenaAbismo.luna(ln) > 0.1f) assertNotNull("21:00 con luna", MuelleAbismo.sombraEnLasTablas(ln))
        assertNull(MuelleAbismo.sombraEnLasTablas(EscenaAbismo.luces(momento("2027-01-12", 21.0, Clima.LLUVIA))))
    }

    @Test fun laFlotaDelMuelleEsLaDelDia() {
        diasDelMuelle(9.0) { d, m ->
            val n = MuelleAbismo.barcos(m).size
            when (EscenaAbismo.flota(m)!!) {
                Flota.LLENO -> assertTrue("$d: el muelle de invierno, lleno ($n)", n >= 14)
                Flota.POCOS -> assertTrue("$d: los días de tierra, unos pocos ($n)", n in 2..4)
                Flota.SALIENDO -> assertTrue("$d: zarpando, faltan los de delante ($n)", n in 3..10)
                Flota.VACIO -> assertEquals("$d: en el cuarto, ninguno", 0, n)
            }
            assertEquals(d.toString(), EscenaAbismo.lugar(m) != Lugar.CUARTO, MuelleAbismo.nikosAmarrado(m))
            // De dos en dos: cada barco de fuera tiene uno de dentro a su lado.
            if (EscenaAbismo.flota(m) == Flota.LLENO) for ((x, z, _) in MuelleAbismo.barcos(m).filter { it.first == MuelleAbismo.FILA_FUERA })
                assertTrue("$d: el de fuera en $z va abarloado", MuelleAbismo.barcos(m).any { it.first == MuelleAbismo.FILA_DENTRO && abs(it.second - z) < 3f })
        }
    }

    @Test fun laBotavaraDeNikosLlevaLoQueDiceElDia() {
        diasDelMuelle(13.0) { d, m ->
            val n = MuelleAbismo.ristrasDeLaBotavara(m)
            when {
                !MuelleAbismo.nikosAmarrado(m) -> assertEquals(d.toString(), 0, n)
                EscenaAbismo.cargadoEnElMuelle(m) -> assertTrue("$d: llega cargado ($n)", n >= 6)
                else -> assertTrue("$d: en invierno, pocas ($n)", n in 1..5)
            }
        }
        // La vuelta (18 dic) llega cargado; el 12 de enero, pocas; en el cuarto, ninguna.
        assertTrue(MuelleAbismo.ristrasDeLaBotavara(momento("2027-12-18")) > MuelleAbismo.ristrasDeLaBotavara(momento("2027-01-12")))
        assertEquals(0, MuelleAbismo.ristrasDeLaBotavara(momento("2030-06-20")))
    }

    @Test fun elGatoTieneSuTramoLibreEnElMuelle() {
        diasDelMuelle(13.0) { d, m ->
            val anclas = MuelleAbismo.anclas(m)
            val suelo = anclas.single { it.tipo == io.github.ruddy2003.vestigium.ui.escenas.TipoAncla.SUELO }
            assertTrue("$d: el tramo del gato", suelo.hasta - suelo.x >= 110f)
            for (x in MuelleAbismo.estorbos(m)) assertTrue("$d: lo plantado está en su tramo", x in suelo.x..suelo.hasta)
        }
    }
}
