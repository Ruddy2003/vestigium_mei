package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Fusion
import io.github.ruddy2003.vestigium.data.Lugares
import io.github.ruddy2003.vestigium.data.SaludDia
import io.github.ruddy2003.vestigium.data.Visita
import io.github.ruddy2003.vestigium.data.claveFuenteSueno
import org.junit.Assert.assertEquals
import org.junit.Test

/** Corregir lo que registró el teléfono (N-04, N-05), con datos inventados. */
class CorregirPrueba {
    @Test fun laClaveDelSuenoNoCambiaConElEstado() {
        // La misma sesión se lee como «noche» o «siesta» según lo demás; la clave es la misma.
        val a = claveFuenteSueno("Samsung Health 15:10–16:05 · 0 etapas · siesta")
        val b = claveFuenteSueno("Samsung Health 15:10–16:05 · 0 etapas · no cuenta")
        assertEquals("Samsung Health 15:10–16:05", a)
        assertEquals(a, b)
    }

    @Test fun elRespaldoNoDevuelveLoQueNoCuenta() {
        val aqui = SaludDia(fecha = "2026-10-20", sueno_descartadas = listOf("Samsung Health 15:10–16:05"))
        val respaldo = SaludDia(fecha = "2026-10-20", sueno_min = 420)
        val r = Fusion.salud(aqui, respaldo)
        assertEquals(listOf("Samsung Health 15:10–16:05"), r.sueno_descartadas)
        assertEquals(420, r.sueno_min)
    }

    private val visitas = listOf(
        Visita("v1", "casa", "2026-10-20T08:00:00-06:00", "2026-10-20T09:00:00-06:00"),
        Visita("v2", "cafe", "2026-10-20T10:00:00-06:00", "2026-10-20T11:00:00-06:00"),
        Visita("v3", "cafe", "2026-10-21T10:00:00-06:00", null),
    )

    @Test fun borrarUnLugarPasandoSusVisitas() {
        val r = Lugares.reasignar(visitas, "cafe", "oficina")
        assertEquals(listOf("casa", "oficina", "oficina"), r.map { it.lugar })
        assertEquals(visitas.map { it.id }, r.map { it.id })
    }

    @Test fun borrarUnLugarConSusVisitas() = assertEquals(listOf("v1"), Lugares.reasignar(visitas, "cafe", null).map { it.id })
}
