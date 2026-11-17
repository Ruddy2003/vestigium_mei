package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.escenas.BovedaSelva
import io.github.ruddy2003.vestigium.ui.escenas.CercaSelva
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.EscenaBotanico.Cuadro
import io.github.ruddy2003.vestigium.ui.escenas.LuzSelva
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
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
import kotlin.math.hypot

/**
 * La selva de Botánico (tanda S13, cuadro B, composición v2: un invernadero sin vidrio). Vigila que la luz camine con la
 * hora (la mancha de sol bajo la bóveda), que no haya mancha con el cielo cerrado, que de noche estén las dos luces (la
 * lámpara y la luna), que lo que el cuento mueve en la casa de Botafogo salga su día, que la bóveda no tape el sol ni la
 * luna, y que el gato tenga su tramo libre.
 */
class EscenaBotanicoSelvaPrueba {
    private fun momento(dia: String, hora: Double = 10.0) = Momento(FichaDelDia.de(dia, "naturalista")!!, hora)
    private fun conClima(dia: String, hora: Double, clima: Clima): Momento { val f = FichaDelDia.de(dia, "naturalista")!!; return Momento(f.copy(clima = clima), hora) }

    /** Los días de la selva de un año (del 25 de abril al 4 de julio de la temporada 1). */
    private fun diasDeLaSelva(anio: Int): List<String> { val r = ArrayList<String>(); var d = LocalDate.of(anio, 4, 1); while (d.monthValue <= 7) { val m = momento(d.toString()); if (EscenaBotanico.cuadro(m) == Cuadro.SELVA) r += d.toString(); d = d.plusDays(1) }; return r }

    @Test fun laSelvaEsLaCasaDeBotafogo() {
        val dias = diasDeLaSelva(2027)
        assertEquals("2027-04-25", dias.first()); assertEquals("2027-07-04", dias.last()); assertEquals(71, dias.size)
        for (d in dias) assertTrue(d, SelvaBotanico.cajonesEnLaGaleria(momento(d)) in 1..5)
    }

    /** Con sol, la luz que entra camina: por la mañana tumba largo hacia la izquierda; a mediodía cae casi a plomo; por la tarde, al revés. */
    @Test fun laManchaCaminaConLaHora() {
        val dia = "2027-06-12"
        assertEquals(Clima.DESPEJADO, momento(dia).ficha.clima)
        val manana = LuzSelva.tumba(momento(dia, 7.0)); val medio = LuzSelva.tumba(momento(dia, 13.0)); val tarde = LuzSelva.tumba(momento(dia, 17.0))
        assertNotNull(manana); assertNotNull(medio); assertNotNull(tarde)
        assertTrue("por la mañana, hacia la izquierda", manana!!.x < -1.2f)
        assertTrue("a mediodía, corta", hypot(medio!!.x, medio.y) < 0.8f * hypot(manana.x, manana.y))
        assertTrue("por la tarde, hacia la derecha", tarde!!.x > 0f)
        // La mesa, bajo el alero: le da el sol bajo de la mañana, no el de mediodía (la tapa el tejado) ni el de la tarde.
        assertTrue(LuzSelva.solEnLaMesa(momento(dia, 7.0)))
        assertFalse(LuzSelva.solEnLaMesa(momento(dia, 13.0)))
        assertFalse(LuzSelva.solEnLaMesa(momento(dia, 17.0)))
    }

    /** A mediodía la bóveda reparte la luz: en el tablado hay sitios al sol y sitios a la sombra de las hojas. */
    @Test fun aMediodiaLaLuzLlegaRota() {
        val luz = LuzSelva.Luz(momento("2027-06-12", 13.0))
        var sol = 0; var sombra = 0
        var x = -0.6f; while (x < 1.4f) { var z = 3.1f; while (z < 5.2f) { if (LuzSelva.alumbrado(luz, x, 0f, z) > 0.5f) sol++ else sombra++; z += 0.1f }; x += 0.1f }
        assertTrue("al sol: $sol, a la sombra: $sombra", sol > 15 && sombra > 15)
    }

    @Test fun conElCieloCerradoNoHayMancha() {
        for (c in listOf(Clima.LLUVIA, Clima.NIEBLA, Clima.TORMENTA, Clima.NIEVE)) for (h in listOf(7.0, 13.0)) {
            val m = conClima("2027-06-12", h, c)
            assertFalse("$c $h", LuzSelva.hayMancha(m)); assertNull("$c $h", LuzSelva.tumba(m))
        }
        assertTrue(LuzSelva.cubierto(Clima.LLUVIA)); assertFalse(LuzSelva.cubierto(Clima.DESPEJADO))
    }

    /** De noche, a dos luces: la lámpara de la mesa encendida (de 19 a 22) y, con el cielo limpio, la luna que deja su mancha. */
    @Test fun deNocheDosLuces() {
        val m = momento("2027-06-12", 21.0)
        val luz = LuzSelva.Luz(m)
        assertTrue("la lámpara", luz.lampara > 0.6f)
        assertTrue("la luna", luz.deLuna && luz.directa > 0.15f)
        // De día la lámpara está apagada; a medianoche, también (duerme).
        assertTrue(LuzSelva.Luz(momento("2027-06-12", 10.0)).lampara < 0.05f)
        assertTrue(LuzSelva.Luz(momento("2027-06-12", 23.5)).lampara < 0.05f)
        // Con lluvia, la luna no deja mancha, pero la lámpara sigue.
        val lluvia = LuzSelva.Luz(conClima("2027-06-12", 21.0, Clima.LLUVIA))
        assertTrue(lluvia.lampara > 0.6f && lluvia.directa < 0.15f)
    }

    /** Lo que el cuento mueve en la casa: el bergantín en la bahía «cuando está», los libros al sol, el paraguas y los escarabajos. */
    @Test fun loQueElCuentoMueveSaleSuDia() {
        assertTrue(EscenaBotanico.bergantinEnLaBahia(momento("2027-04-25")))
        assertFalse(EscenaBotanico.bergantinEnLaBahia(momento("2027-05-10")))
        assertTrue(EscenaBotanico.bergantinEnLaBahia(momento("2027-06-12")))
        assertTrue(EscenaBotanico.librosAlSol(momento("2027-04-25", 13.0)))
        assertFalse(EscenaBotanico.librosAlSol(momento("2027-04-26", 13.0)))
        assertTrue(SelvaBotanico.cajonesAbiertos(momento("2027-04-25", 13.0)))
        assertTrue(SelvaBotanico.paraguasAlReves(momento("2027-05-20", 13.0)))
        assertFalse(SelvaBotanico.paraguasAlReves(momento("2027-05-20", 8.0)))
        assertTrue(SelvaBotanico.escarabajosEnLaMesa(momento("2027-05-20", 15.0)))
        assertFalse(SelvaBotanico.escarabajosEnLaMesa(momento("2027-05-20", 11.0)))
        for (d in diasDeLaSelva(2027).filter { it != "2027-05-20" }) assertFalse(d, SelvaBotanico.paraguasAlReves(momento(d, 15.0)))
        // Con lluvia el arroyo baja crecido y las hojas gotean.
        assertTrue(SelvaBotanico.arroyoCrecido(conClima("2027-06-12", 10.0, Clima.LLUVIA))); assertFalse(SelvaBotanico.arroyoCrecido(momento("2027-06-12")))
        assertTrue(SelvaBotanico.mojado(conClima("2027-06-12", 10.0, Clima.NIEBLA)))
    }

    /** Ninguna hoja de la bóveda tapa el sol de la mañana ni la luna de la noche (la zona de los astros, x 270–330, y 45–110). */
    @Test fun laBovedaDejaVerLosAstros() {
        for (h in BovedaSelva.HOJAS) for (q in h.contorno(12) + listOf(h.base, h.punta)) {
            val p = SelvaBotanico.pc(q)
            assertFalse("una hoja tapa los astros en $p", p.x in 272f..328f && p.y in 47f..108f)
        }
    }

    /** Con sol hay tres o cuatro haces entre las copas, que cambian de sitio con la hora; de noche y con el cielo cerrado, ninguno. */
    @Test fun losHacesCambianDeSitioConLaHora() {
        val manana = LuzSelva.hacesDeCopas(LuzSelva.Luz(momento("2027-06-12", 8.0)))
        val tarde = LuzSelva.hacesDeCopas(LuzSelva.Luz(momento("2027-06-12", 16.0)))
        assertTrue(manana.size in 3..4 && tarde.size in 3..4)
        // Donde llega cada haz se mueve con el sol (más de diez unidades del lienzo entre la mañana y la tarde).
        for (i in manana.indices) assertTrue("haz $i", (manana[i].b - tarde[i].b).getDistance() > 10f)
        // Y algo encienden en el jardín (lo que tocan se pinta con la luz del sol).
        val b = manana.first().b - (manana.first().b - manana.first().a) * 0.3f
        assertTrue(LuzSelva.enHaz(LuzSelva.Luz(momento("2027-06-12", 8.0)), b) > 0.5f)
        assertTrue(LuzSelva.hacesDeCopas(LuzSelva.Luz(momento("2027-06-12", 21.0))).isEmpty())
        assertTrue(LuzSelva.hacesDeCopas(LuzSelva.Luz(conClima("2027-06-12", 10.0, Clima.LLUVIA))).isEmpty())
    }

    /** La lámpara, baja junto al poste, tumba hacia arriba la sombra del poste y la de su bromelia en las hojas de detrás. */
    @Test fun laLamparaTumbaSombrasEnLasHojas() {
        val enLasHojas = LuzSelva.sombrasDeLaLampara(5.5f).getBounds()
        assertFalse(enLasHojas.isEmpty)
        // Más grandes que el poste mismo (la lámpara está cerca y las agranda) y más arriba que la llama.
        assertTrue(enLasHojas.width > 12f && enLasHojas.top < CercaSelva.LAMPARA.y - 30f)
    }

    @Test fun elGatoTieneSuTramoLibre() {
        for (d in listOf("2027-04-25", "2027-05-20", "2027-06-12")) for (h in listOf(7.0, 13.0, 21.0)) {
            val m = momento(d, h); val anclas = EscenaBotanico.anclas(m)
            val suelo = anclas.single { it.tipo == TipoAncla.SUELO }
            assertTrue(suelo.hasta - suelo.x >= 110f)
            // El tramo está sobre las tablas y delante de todo lo del tablado.
            val izq = LuzSelva.enElSuelo(androidx.compose.ui.geometry.Offset(suelo.x, suelo.y)); val der = LuzSelva.enElSuelo(androidx.compose.ui.geometry.Offset(suelo.hasta, suelo.y))
            assertTrue(izq.x > SelvaBotanico.TABLADO_X0 && der.x < SelvaBotanico.TABLADO_X1 && izq.z < 3.15f)
            for (a in anclas) assertTrue("${a.tipo}", a.x in 0f..ANCHO && a.y in 0f..ALTO)
            val mira = CercaSelva.mirador(m); assertTrue(mira.y < suelo.y - 40f)
        }
        assertTrue(abs(CercaSelva.LAMPARA.x - CercaSelva.SOPORTE.x) > 10f)
    }
}
