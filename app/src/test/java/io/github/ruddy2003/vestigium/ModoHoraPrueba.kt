package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.ui.ModoHora
import io.github.ruddy2003.vestigium.ui.oscuroSegun
import io.github.ruddy2003.vestigium.vidas.Franja
import io.github.ruddy2003.vestigium.vidas.RegimenLuz
import io.github.ruddy2003.vestigium.vidas.luzDe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * El modo «hora» (esquema 26): claro de 5:00 a 21:00 y oscuro de 21:00 a 5:00, las mismas horas que la franja «noche» del
 * cuadro de «Otras vidas». Lo que solo se ve en el teléfono (que el tema cambie solo al cruzar la hora con la app abierta)
 * queda para probarlo ahí.
 */
class ModoHoraPrueba {
    @Test fun losBordesExactos() {
        assertFalse(ModoHora.esDeNoche(LocalTime.of(20, 59, 59)))
        assertTrue(ModoHora.esDeNoche(LocalTime.of(21, 0)))
        assertTrue(ModoHora.esDeNoche(LocalTime.of(23, 59, 59)))
        assertTrue(ModoHora.esDeNoche(LocalTime.MIDNIGHT))
        assertTrue(ModoHora.esDeNoche(LocalTime.of(4, 59, 59)))
        assertFalse(ModoHora.esDeNoche(LocalTime.of(5, 0)))
        assertFalse(ModoHora.esDeNoche(LocalTime.NOON))
    }

    @Test fun coincideConLaNocheDelCuadro() {
        // Cada cuarto de hora del día: oscuro justo cuando el cuadro de seis franjas está en la franja «noche».
        for (m in 0 until 24 * 60 step 15) {
            val t = LocalTime.of(m / 60, m % 60)
            val franja = luzDe(m / 60.0, RegimenLuz.SEIS_FRANJAS).franja
            assertEquals("a las $t", franja == Franja.NOCHE, ModoHora.esDeNoche(t))
        }
    }

    @Test fun cuantoFaltaParaElCambio() {
        val z = ZoneId.of("America/Mexico_City")
        fun ms(h: Int, min: Int) = ModoHora.msHastaElCambio(LocalDateTime.of(2026, 11, 16, h, min).atZone(z))
        val hora = 3_600_000L
        assertEquals(1 * hora, ms(20, 0))           // a las 21:00
        assertEquals(8 * hora, ms(21, 0))           // ya es de noche: hasta las 5:00 del día siguiente
        assertEquals(hora / 2, ms(4, 30))           // a las 5:00 de hoy
        assertEquals(16 * hora, ms(5, 0))           // ya es de día: hasta las 21:00
        assertEquals(5 * hora, ms(0, 0))
    }

    @Test fun conCambioDeHorarioCuentaLaHoraReal() {
        // La noche del 7 al 8 de marzo de 2026 en Nueva York el reloj se adelanta a las 2:00: de 21:00 a 5:00 pasan 7 horas.
        val z = ZoneId.of("America/New_York")
        assertEquals(7 * 3_600_000L, ModoHora.msHastaElCambio(LocalDateTime.of(2026, 3, 7, 21, 0).atZone(z)))
    }

    @Test fun losCuatroModos() {
        listOf(true, false).forEach { telefono ->
            listOf(true, false).forEach { noche ->
                assertFalse(oscuroSegun("claro", false, telefono, noche))
                assertTrue(oscuroSegun("oscuro", false, telefono, noche))
                assertEquals(telefono, oscuroSegun("sistema", false, telefono, noche))
                // «hora» no se confunde con «sistema»: sigue a la hora, no al teléfono.
                assertEquals(noche, oscuroSegun("hora", false, telefono, noche))
                // Un valor desconocido sigue al teléfono, como antes.
                assertEquals(telefono, oscuroSegun("otro", false, telefono, noche))
                // El tema que solo existe en oscuro, siempre oscuro.
                listOf("claro", "oscuro", "sistema", "hora").forEach { assertTrue(oscuroSegun(it, true, telefono, noche)) }
            }
        }
    }

    @Test fun laConfiguracionGuardaElModoHora() {
        val c = JSON.decodeFromString(Config.serializer(), JSON.encodeToString(Config.serializer(), Config(modo = "hora")))
        assertEquals("hora", c.modo)
        // Una configuración de antes, sin modo, sigue como el teléfono.
        assertEquals("sistema", JSON.decodeFromString(Config.serializer(), "{}").modo)
    }
}
