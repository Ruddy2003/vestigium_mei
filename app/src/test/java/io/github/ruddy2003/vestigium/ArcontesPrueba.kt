package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.Arcontes
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/** Lo que calcula el tema Arcontes: la fecha hebrea, el cielo del día y los números con letras hebreas. */
class ArcontesPrueba {
    private fun h(iso: String) = Arcontes.hebrea(LocalDate.parse(iso))

    @Test fun fechasHebreasConocidas() {
        // Año nuevo (Rosh Hashaná) del 5786 y del 5787
        assertEquals(Arcontes.Hebrea(5786, 7, 1), h("2025-09-23"))
        assertEquals(Arcontes.Hebrea(5787, 7, 1), h("2026-09-12"))
        // Pésaj del 2025: 15 de nisán del 5785
        assertEquals(Arcontes.Hebrea(5785, 1, 15), h("2025-04-13"))
        // Purim del 2024 (año embolismal): 14 de adar II del 5784
        assertEquals(Arcontes.Hebrea(5784, 13, 14), h("2024-03-24"))
        assertEquals("adar II", h("2024-03-24").nombreMes)
        // Yom Kipur del 2026: 10 de tishréi del 5787
        assertEquals(Arcontes.Hebrea(5787, 7, 10), h("2026-09-21"))
        assertEquals("6 de jeshván", h("2026-10-17").let { "${it.dia} de ${it.nombreMes}" })
    }

    @Test fun diasSeguidosNoSaltan() {
        var d = LocalDate.parse("2026-01-01")
        var antes = Arcontes.hebrea(d.minusDays(1))
        repeat(2000) {
            val hoy = Arcontes.hebrea(d)
            val sigue = (hoy.anio == antes.anio && hoy.mes == antes.mes && hoy.dia == antes.dia + 1) ||
                (hoy.dia == 1 && antes.dia >= 29)
            assertEquals("tras $antes viene $hoy ($d)", true, sigue)
            antes = hoy; d = d.plusDays(1)
        }
    }

    @Test fun numerosHebreos() {
        assertEquals("ה׳", Arcontes.numero(5))
        assertEquals("כ״ג", Arcontes.numero(23))
        assertEquals("ט״ו", Arcontes.numero(15))
        assertEquals("ט״ז", Arcontes.numero(16))
        assertEquals("רי״ב", Arcontes.numero(212))
        assertEquals("שס״ה", Arcontes.numero(365))
        assertEquals("ה׳תשפ״ז", Arcontes.numero(5787))
        assertEquals("0", Arcontes.numero(0))
    }

    @Test fun cieloYPlaneta() {
        assertEquals(290, Arcontes.cielo(LocalDate.parse("2026-10-17")))
        assertEquals(365, Arcontes.cielo(LocalDate.parse("2028-12-31")))
        assertEquals("Saturno", Arcontes.planeta(LocalDate.parse("2026-10-17")))
        assertEquals("Cielo 290 · Saturno · 6 de jeshván", Arcontes.fecha("2026-10-17"))
    }
}
