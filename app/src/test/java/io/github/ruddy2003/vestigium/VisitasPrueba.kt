package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Lugar
import io.github.ruddy2003.vestigium.data.Lugares
import io.github.ruddy2003.vestigium.data.Visita
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Esquema 19: cómo se cierra una visita y cuándo cuenta (decisiones del autor del 5 nov 2026). */
class VisitasPrueba {
    @Test fun visitaViejaSinCierre() {
        val v = JSON.decodeFromString(Visita.serializer(), """{"id":"v1","lugar":"casa","entrada":"2026-03-02T19:00:00-06:00","salida":"2026-03-03T08:00:00-06:00"}""")
        assertNull(v.cierre)
        val n = JSON.decodeFromString(Visita.serializer(), JSON.encodeToString(Visita.serializer(), v.copy(cierre = Lugares.CIERRE_OTRO_LUGAR)))
        assertEquals("otro_lugar", n.cierre)
    }

    @Test fun lugaresLejosYAnidados() {
        // Dos lugares a ~1.1 km: llegar a uno saca del otro.
        val casa = Lugar("casa", "Casa", 10.0000, 20.0000, 100, "casa")
        val trabajo = Lugar("trabajo", "Trabajo", 10.0100, 20.0000, 100, "trabajo")
        assertTrue(Lugares.lejos(casa, trabajo))
        // Un gimnasio dentro del campus (a ~60 m del centro): estar en uno no te saca del otro.
        val campus = Lugar("campus", "Campus", 11.0000, 21.0000, 400, "escuela")
        val gimnasio = Lugar("gim", "Gimnasio", 11.0005, 21.0000, 60, "ejercicio")
        assertFalse(Lugares.lejos(campus, gimnasio))
    }

    @Test fun duracionYMinimo() {
        val corta = Visita("v2", "gim", "2026-03-04T12:00:00-06:00", "2026-03-04T12:00:25-06:00")
        assertEquals(25_000L, Lugares.duracion(corta))
        assertTrue(Lugares.duracion(corta)!! < Lugares.VISITA_MINIMA_MS)
        assertEquals(8 * 60_000L, Lugares.VISITA_MINIMA_MS)
        assertNull(Lugares.duracion(Visita("v3", "casa", "2026-03-04T12:00:00-06:00")))
    }
}
