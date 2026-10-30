package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ciencia.Terapia
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Repo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El reporte para terapia: sus cuentas y la regla de lo privado (con datos inventados). */
class TerapiaPrueba {
    @Test fun losNombresSalenComoIniciales() {
        assertEquals("Comí con A.L. y B.", Terapia.sinNombres("Comí con @ana_luisa y @beto"))
        assertEquals("sin nombres", Terapia.sinNombres("sin nombres"))
    }

    @Test fun periodo() {
        assertEquals("2026-10-14", Terapia.Opciones(dias = 7, fin = "2026-10-20").ini)
        assertEquals("2026-10-07", Terapia.Opciones(dias = 14, fin = "2026-10-20").ini)
    }

    @Test fun caloriasContraElObjetivo() {
        val o = Terapia.contraObjetivo(listOf(
            Triple("a", 2300.0, 2000.0), Triple("b", 1700.0, 2000.0), Triple("c", 2050.0, 2000.0),
            // Sin registro (menos de 300 kcal), sin dato y sin objetivo: no cuentan.
            Triple("d", 100.0, 2000.0), Triple("e", null, 2000.0), Triple("f", 2000.0, null),
        ), Terapia.MINIMO_KCAL)!!
        assertEquals(listOf("a", "b", "c"), o.diferencias.map { it.first })
        assertEquals(50.0 / 3, o.diferenciaMedia, 1e-9)
        assertEquals((0.15 - 0.15 + 0.025) / 3, o.porcentajeMedio, 1e-9)
        assertEquals(1, o.dentro); assertEquals(1, o.encima); assertEquals(1, o.debajo)
        assertEquals(2000.0, o.objetivoMedio, 1e-9)
        assertNull(Terapia.contraObjetivo(listOf(Triple("a", 100.0, 2000.0)), Terapia.MINIMO_KCAL))
    }

    @Test fun emociones() {
        fun m(id: String, vararg e: String) = Momento(id, "2026-10-20T12:00:00-06:00", "2026-10-20", "muestreo", emociones = e.toList())
        val (top, neg) = Terapia.emociones(listOf(m("1", "ansiedad", "calma"), m("2", "ansiedad"), m("3", "nostalgia")))
        assertEquals("Ansiedad" to 2, top.first())
        // Dos de las cuatro nombradas son desagradables (la nostalgia es mixta y cuenta en el total).
        assertEquals(0.5, neg!!, 1e-9)
        assertNull(Terapia.emociones(emptyList()).second)
    }

    @Test fun impulsosSuperados() {
        val h = 3_600_000L
        // El primero tuvo una recaída una hora después; el segundo, no.
        assertEquals(1, Terapia.superados(listOf(0L, 10 * h), listOf(1 * h)))
        assertEquals(0, Terapia.superados(emptyList(), listOf(h)))
    }

    @Test fun desviacion() {
        assertEquals(1.0, Terapia.desviacion(listOf(1.0, 2.0, 3.0))!!, 1e-12)
        assertNull(Terapia.desviacion(listOf(1.0)))
    }

    @Test fun loPrivadoSoloConLaCasilla() {
        val antes = Repo.dias
        val f = "2026-10-20"
        try {
            Repo.diasEnMemoria(mapOf(f to Dia(f, valencia = 5, impulso = 3, recaida = true, medicamentos = listOf("medicina inventada"),
                detonantes = listOf("estres"), mejor_momento = "Comer con @ana_luisa", peor_momento = "Tráfico")))
            val sin = Terapia.calcular(Terapia.Opciones(dias = 7, fin = f))
            assertNull(sin.privados)
            assertTrue(sin.filas.all { it.impulso == null && it.recaida == null })
            assertEquals(f to "Comer con A.L.", sin.mejorMomento)
            assertTrue(sin.filas.all { it.mejor.isEmpty() && it.peor.isEmpty() })

            val con = Terapia.calcular(Terapia.Opciones(dias = 7, fin = f, momentosDia = true, privado = true))
            assertEquals(1, con.privados!!.diasConRecaida)
            assertEquals(listOf("medicina inventada" to 1), con.privados!!.medicamentos)
            assertEquals(listOf("Estrés" to 1), con.privados!!.detonantes)
            assertEquals(3, con.filas.last().impulso)
            assertEquals("Comer con A.L.", con.filas.last().mejor)

            val sinMomentos = Terapia.calcular(Terapia.Opciones(dias = 14, fin = f, momentosPeriodo = false))
            assertNull(sinMomentos.mejorMomento)
            assertEquals(14, sinMomentos.n)
        } finally {
            Repo.diasEnMemoria(antes)
        }
    }
}
