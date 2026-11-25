package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.JSON
import io.github.ruddy2003.vestigium.data.Lugar
import io.github.ruddy2003.vestigium.data.Lugares
import io.github.ruddy2003.vestigium.data.Visita
import io.github.ruddy2003.vestigium.data.VisitaQuitada
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
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

    // ---------------------------------------------------------------- Esquema 29: lo quitado no vuelve

    private val h = 3_600_000L

    @Test fun restarTramos() {
        // Sin tramos, o sin choques, las rachas quedan igual.
        val r = listOf(10 * h to 12 * h)
        assertSame(r, Lugares.restar(r, emptyList()))
        assertEquals(r, Lugares.restar(r, listOf(13 * h to 14 * h)))
        // Un tramo en medio parte la racha en dos; uno que la cubre entera la quita.
        assertEquals(listOf(10 * h to 11 * h, (11 * h + h / 2) to 12 * h), Lugares.restar(r, listOf(11 * h to (11 * h + h / 2))))
        assertEquals(emptyList<Pair<Long, Long>>(), Lugares.restar(r, listOf(9 * h to 13 * h)))
        // Dos tramos que se enciman y otro que toca el final.
        assertEquals(listOf(10 * h to (10 * h + h / 4)), Lugares.restar(r, listOf((10 * h + h / 4) to 11 * h, (10 * h + h / 2) to (11 * h + h / 2), (11 * h + h / 2) to 12 * h)))
    }

    @Test fun tramoDeUnaQuitada() {
        val q = VisitaQuitada("gim", "2026-12-02T09:00:00-06:00", "2026-12-02T10:30:00-06:00", "2026-12-03T08:00:00-06:00")
        assertEquals(Fechas.millis(q.entrada) to Fechas.millis(q.salida!!), Lugares.tramoQuitado(q))
        // Abierta: hasta que la quitaste; sin eso, 12 h.
        val abierta = q.copy(salida = null)
        assertEquals(Fechas.millis(abierta.quitada), Lugares.tramoQuitado(abierta)!!.second)
        assertEquals(Fechas.millis(q.entrada) + 12 * h, Lugares.tramoQuitado(abierta.copy(quitada = ""))!!.second)
        assertNull(Lugares.tramoQuitado(q.copy(entrada = "no es hora")))
    }

    @Test fun anotarYOlvidar() {
        val v = Visita("v9", "gim", "2026-12-02T09:00:00-06:00", "2026-12-02T10:30:00-06:00")
        val vieja = VisitaQuitada("casa", "2026-08-01T09:00:00-06:00", "2026-08-01T10:00:00-06:00", "2026-08-02T09:00:00-06:00")
        var c = Config(visitas_quitadas = listOf(vieja))
        c = Lugares.anotarQuitada(c, v, "2026-12-03T08:00:00-06:00")
        // La de agosto pasa de 90 días y se olvida; la nueva queda una sola vez aunque se anote dos.
        assertEquals(listOf(VisitaQuitada("gim", v.entrada, v.salida, "2026-12-03T08:00:00-06:00")), c.visitas_quitadas)
        assertEquals(1, Lugares.anotarQuitada(c, v, "2026-12-03T09:00:00-06:00").visitas_quitadas.size)
        // Deshacer: la visita vuelve a su lugar y deja de estar quitada; otra visita no cambia nada.
        assertEquals(emptyList<VisitaQuitada>(), Lugares.olvidarQuitada(c, v).visitas_quitadas)
        assertSame(c, Lugares.olvidarQuitada(c, v.copy(lugar = "casa")))
    }

    @Test fun configDeAntesSinQuitadas() {
        val c = JSON.decodeFromString(Config.serializer(), """{"tema":"heleno"}""")
        assertEquals(emptyList<VisitaQuitada>(), c.visitas_quitadas)
        val q = VisitaQuitada("gim", "2026-12-02T09:00:00-06:00")
        val ida = JSON.decodeFromString(Config.serializer(), JSON.encodeToString(Config.serializer(), c.copy(visitas_quitadas = listOf(q))))
        assertEquals(listOf(q), ida.visitas_quitadas)
    }
}
