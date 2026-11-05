package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Exportar
import io.github.ruddy2003.vestigium.data.Macrofactor
import org.junit.Assert.assertEquals
import org.junit.Test

/** Lo anotado antes de las 05:00 es lo último del día, no lo primero (5 nov 2026, D.156). */
class HorariosPrueba {
    @Test fun cenaDespuesDeMedianoche() {
        assertEquals("08:10" to "00:40", Macrofactor.horario(listOf("14:00", "00:40", "08:10", "21:30")))
        assertEquals(24.67, Exportar.horaNoche("00:40")!!, 1e-9)
        assertEquals(21.5, Exportar.horaNoche("21:30")!!, 1e-9)
    }
}
