package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dinero
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Subcategoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** Subcategorías, monto habitual y frecuentes de los gastos (N-29), con gastos inventados. */
class DineroPrueba {
    private fun g(id: String, fecha: String, hora: String, monto: Double, cat: String = "Comida", sub: String = "Café", creado: String? = null) =
        Gasto(id, fecha, hora, monto, cat, subcategoria = sub, ts_creado = creado)

    @Test fun montoHabitualEsElQueMasSeRepite() {
        val l = listOf(g("1", "2026-10-01", "08:00", 55.0), g("2", "2026-10-02", "08:00", 60.0), g("3", "2026-10-03", "08:00", 55.0),
            g("4", "2026-10-04", "08:00", 48.0))
        assertEquals(55.0, Dinero.montoHabitual("Comida", "Café", l, Config())!!, 0.0)
    }

    @Test fun enEmpateGanaElMasReciente() {
        val l = listOf(g("1", "2026-10-01", "08:00", 55.0), g("2", "2026-10-05", "08:00", 60.0))
        assertEquals(60.0, Dinero.montoHabitual("Comida", "Café", l, Config())!!, 0.0)
    }

    @Test fun soloCuentanLosUltimosDiez() {
        // Once viejos de 40 y diez nuevos de 55: los viejos ya no cuentan.
        val viejos = (1..11).map { g("v$it", "2026-09-%02d".format(it), "08:00", 40.0) }
        val nuevos = (1..10).map { g("n$it", "2026-10-%02d".format(it), "08:00", 55.0) }
        assertEquals(55.0, Dinero.montoHabitual("Comida", "Café", viejos + nuevos, Config())!!, 0.0)
    }

    @Test fun elMontoFijadoAManoManda() {
        val c = Config(subcategorias = mapOf("Comida" to listOf(Subcategoria("Café", 70.0))))
        assertEquals(70.0, Dinero.montoHabitual("Comida", "Café", listOf(g("1", "2026-10-01", "08:00", 55.0)), c)!!, 0.0)
    }

    @Test fun sinHistoriaNiMontoNoHayHabitual() = assertNull(Dinero.montoHabitual("Comida", "Café", emptyList(), Config()))

    @Test fun frecuentesPorUsoYDespuesLasFijas() {
        val c = Config(subcategorias = mapOf("Transporte" to listOf(Subcategoria("Camión", 12.0)), "Comida" to listOf(Subcategoria("Café"), Subcategoria("Súper"))))
        val l = listOf(g("1", "2026-10-20", "08:00", 55.0), g("2", "2026-10-21", "08:00", 55.0), g("3", "2026-10-22", "19:00", 800.0, sub = "Súper"),
            // De hace más de 90 días: no cuenta como uso.
            g("4", "2026-06-01", "19:00", 900.0, sub = "Súper"), g("5", "2026-06-02", "19:00", 900.0, sub = "Súper"))
        val f = Dinero.frecuentes(6, "2026-10-27", l, c)
        assertEquals(listOf("Café", "Súper", "Camión"), f.map { it.sub })
        assertEquals(55.0, f[0].monto, 0.0)
        assertEquals(12.0, f[2].monto, 0.0)
    }

    @Test fun unToqueDobleNoRepite() {
        val ahora = Fechas.millis("2026-10-27T08:00:01-06:00")
        val l = listOf(g("1", "2026-10-27", "08:00", 55.0, creado = "2026-10-27T08:00:00-06:00"))
        assertNotNull(Dinero.repetido("Comida", "Café", 55.0, ahora, l))
        // Tres segundos después ya es otro gasto.
        assertNull(Dinero.repetido("Comida", "Café", 55.0, ahora + 2000, l))
        // Otro monto u otra subcategoría tampoco es repetición.
        assertNull(Dinero.repetido("Comida", "Café", 60.0, ahora, l))
        assertNull(Dinero.repetido("Comida", "Súper", 55.0, ahora, l))
    }
}
