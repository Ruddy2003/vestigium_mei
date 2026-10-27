package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Decodificar
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Fusion
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Importar
import io.github.ruddy2003.vestigium.data.Respaldos
import io.github.ruddy2003.vestigium.data.SCHEMA_V
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Lo que no se puede leer no se pierde, y restaurar o respaldar no pisa lo bueno con algo más pobre (P-07 a P-09). */
class IntegridadPrueba {
    @Test fun cuentaLoIlegible() {
        val buenos = """{"id":"g1","fecha":"2026-10-01","hora":"10:00","monto":50.0,"categoria":"Café"}"""
        val (l, fallidos) = Decodificar.todos(listOf(buenos, "{roto", """{"id":"g2"}"""), Gasto.serializer())
        assertEquals(1, l.size)
        assertEquals(2, fallidos)
    }

    @Test fun rechazaEsquemaFuturo() {
        assertNotNull(Importar.esquemaFuturo(99))
        assertNull(Importar.esquemaFuturo(SCHEMA_V))
        assertNull(Importar.esquemaFuturo(null))
        // El texto completo tampoco se restaura.
        val r = Importar.respaldoTexto("""{"exportado":"2030-01-01T00:00:00Z","schema_v":99,"momentos":[]}""")
        assertTrue(r.mensaje.contains("más nueva"))
    }

    @Test fun gastosSinDuplicarNiPisar() {
        val aqui = listOf(Gasto("a", "2026-10-01", "10:00", 50.0, "Café"))
        val respaldo = listOf(Gasto("a", "2026-10-01", "10:00", 99.0, "Otro"), Gasto("b", "2026-10-02", "11:00", 20.0, "Café"), Gasto("b", "2026-10-02", "11:00", 20.0, "Café"))
        assertEquals(listOf("b"), Fusion.nuevosPorId(aqui, respaldo) { it.id }.map { it.id })
    }

    private fun exp(fin: String? = null, activo: Boolean = true, hecho: Map<String, Boolean> = emptyMap()) =
        Experimento("e", "Prueba", "acción", "valencia", "2026-09-07", 6, listOf(true, false, true, false, true, false), activo = activo, fin = fin, hecho = hecho)

    @Test fun experimentoTerminadoNoSeReactiva() {
        // Aquí terminado; el respaldo viejo lo trae activo y sin fin: no se toca nada.
        assertNull(Fusion.experimento(exp(fin = "2026-09-25", activo = false), exp()))
        // Aquí activo; el respaldo trae la fecha de fin: se termina.
        val n = Fusion.experimento(exp(), exp(fin = "2026-09-25", activo = false))!!
        assertEquals("2026-09-25", n.fin); assertFalse(n.activo)
        // Las respuestas se suman; si chocan, manda lo de aquí.
        val h = Fusion.experimento(exp(hecho = mapOf("2026-09-08" to true)), exp(hecho = mapOf("2026-09-08" to false, "2026-09-09" to true)))!!
        assertEquals(mapOf("2026-09-08" to true, "2026-09-09" to true), h.hecho)
    }

    @Test fun driveNoSeEmpobrece() {
        val previo = mapOf("dias" to 300, "momentos" to 1000)
        assertFalse(Respaldos.empobrecido(previo, mapOf("dias" to 301, "momentos" to 1005)))
        assertTrue(Respaldos.empobrecido(previo, mapOf("dias" to 260, "momentos" to 1005)))
        assertTrue(Respaldos.empobrecido(previo, mapOf("dias" to 300, "momentos" to 10)))
        // Al principio (o sin respaldo anterior) no se compara.
        assertFalse(Respaldos.empobrecido(emptyMap(), mapOf("dias" to 0, "momentos" to 0)))
        assertFalse(Respaldos.empobrecido(mapOf("dias" to 10, "momentos" to 5), mapOf("dias" to 1, "momentos" to 0)))
    }
}
