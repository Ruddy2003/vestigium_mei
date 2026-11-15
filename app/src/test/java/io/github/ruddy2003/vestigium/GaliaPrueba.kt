package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Galia
import org.junit.Assert.assertEquals
import org.junit.Test

/** Lo que calcula el tema Galia: la noche del año, el mes de Coligny y los números. */
class GaliaPrueba {
    @Test fun fecha() {
        assertEquals("Noche 290 · Cantlos ANM", Galia.fecha("2026-10-17"))
        assertEquals("Noche 305 · Samonios MAT", Galia.fecha("2026-11-01"))
        assertEquals("Noche 1 · Rivros MAT", Galia.fecha("2027-01-01"))
    }

    @Test fun meses() {
        assertEquals(12, Galia.MESES.map { it.nombre }.toSet().size)
        assertEquals("Samonios", Galia.mes(11).nombre)
        assertEquals("Cantlos", Galia.mes(10).nombre)
        assertEquals(6, Galia.MESES.count { it.marca == "MAT" })
    }

    @Test fun numeros() {
        assertEquals("cintuxos", Galia.numero(1))
        assertEquals("decametos", Galia.numero(10))
        assertEquals("XVII.", Galia.numero(17))
        assertEquals("MMXXVI.", Galia.numero(2026))
        assertEquals("0", Galia.numero(0))
    }
}
