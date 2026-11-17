package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Consumo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Lectura
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.Intensidad
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.caraSegun
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La intensidad de Psicodelia v2 (`ui/CaraTema.kt`, decisiones del autor del 16 nov 2026): cualquier sustancia con nivel,
 * nivel 3 → 0.5 y nivel 8 o más → 1 (en dos rectas), el mayor de los consumos en curso, abierto sin nivel = nivel 3, sin
 * corte por ocultas; y la cara: la psicodelia de noche o con un consumo en curso.
 */
class IntensidadPrueba {
    private val ahora = Fechas.millis("2026-11-16T22:00:00-06:00")

    private fun consumo(sustancia: String, nivel: Int?, inicio: String = "2026-11-16T20:00:00-06:00", fin: String? = null, id: String = sustancia) =
        Consumo(id = id, fecha = "2026-11-16", sustancia = sustancia, inicio = inicio, fin = fin,
            niveles = if (nivel != null) listOf(Lectura("2026-11-16T20:30:00-06:00", nivel)) else emptyList(), nivel_max = nivel)

    private fun de(vararg c: Consumo) = Intensidad.de(c.toList(), ahora)

    @Test fun laEscalaPorTramos() {
        assertEquals(0f, Intensidad.deNivel(0.0), 1e-6f)
        assertEquals(0.25f, Intensidad.deNivel(1.5), 1e-6f)
        assertEquals(0.5f, Intensidad.deNivel(3.0), 1e-6f)
        assertEquals(0.75f, Intensidad.deNivel(5.5), 1e-6f)
        assertEquals(0.9f, Intensidad.deNivel(7.0), 1e-6f)
        assertEquals(1f, Intensidad.deNivel(8.0), 1e-6f)
        assertEquals(1f, Intensidad.deNivel(10.0), 1e-6f)
        assertEquals(0f, Intensidad.deNivel(-2.0), 1e-6f)
        assertEquals(0f, Intensidad.deNivel(Double.NaN), 1e-6f)
    }

    @Test fun sinConsumosEsCero() = assertEquals(0f, de(), 0f)

    @Test fun abiertoConNivel() {
        assertEquals(0.5f, de(consumo("cannabis", 3)), 1e-6f)
        assertEquals(1f, de(consumo("cannabis", 8)), 1e-6f)
        assertEquals(1f, de(consumo("cannabis", 10)), 1e-6f)
    }

    @Test fun abiertoSinNivelCuentaComoTres() = assertEquals(0.5f, de(consumo("cannabis", null)), 1e-6f)

    @Test fun cerradoEsCero() = assertEquals(0f, de(consumo("cannabis", 6, fin = "2026-11-16T21:00:00-06:00")), 0f)

    @Test fun olvidadoAbiertoDeOtroDiaEsCero() {
        // Abierto desde hace 20 horas: Sustancias.finEfectivo lo corta a las 4 horas de su inicio.
        assertEquals(0f, de(consumo("cannabis", 6, inicio = "2026-11-16T02:00:00-06:00")), 0f)
    }

    @Test fun conVariosMandaElMayor() {
        // Cannabis en 3 (0.5) y «otra» en 6 (0.5 + 3/5 · 0.5 = 0.8).
        assertEquals(0.8f, de(consumo("cannabis", 3), consumo("otra", 6)), 1e-6f)
        assertEquals(0.8f, de(consumo("otra", 6), consumo("cannabis", 3)), 1e-6f)
    }

    @Test fun cualquierSustanciaConNivel() {
        assertEquals(1f, de(consumo("alcohol", 8)), 1e-6f)
        assertEquals(0.5f, de(consumo("otra", 3)), 1e-6f)
        // Tabaco y cafeína no tienen nivel: no cuentan aunque se anote uno.
        assertEquals(0f, de(consumo("tabaco", 8)), 0f)
        assertEquals(0f, de(consumo("cafeina", 8)), 0f)
    }

    @Test fun reaccionaAunqueEsteOculta() {
        // Las pantallas lo ocultan, pero la intensidad no lee la lista de ocultas (decisión 5 del autor).
        val conOculto = Config(sustancias_ocultas = listOf("cannabis"))
        assertFalse(Sustancias.visible("cannabis", conOculto))
        assertEquals(1f, de(consumo("cannabis", 9)), 1e-6f)
    }

    @Test fun soloMiraCadaRatoConAlgoAbierto() {
        assertTrue(Intensidad.hayAbierto(listOf(consumo("cannabis", 3)), ahora))
        assertFalse(Intensidad.hayAbierto(listOf(consumo("cannabis", 3, fin = "2026-11-16T21:00:00-06:00")), ahora))
        assertFalse(Intensidad.hayAbierto(listOf(consumo("tabaco", null)), ahora))
    }

    @Test fun laCara() {
        assertEquals(Cara.BASE, caraSegun(TemaId.PSICODELIA, noche = false, intensidad = 0f))
        assertEquals(Cara.INTENSA, caraSegun(TemaId.PSICODELIA, noche = true, intensidad = 0f))
        // Con un consumo en curso de día, la psicodelia (decisión 3 del autor).
        assertEquals(Cara.INTENSA, caraSegun(TemaId.PSICODELIA, noche = false, intensidad = 0.1f))
        // Los temas sin caras, siempre en la base.
        for (t in TemaId.entries) if (!t.conCaras) {
            assertEquals(t.id, Cara.BASE, caraSegun(t, noche = true, intensidad = 1f))
        }
        assertTrue(TemaId.PSICODELIA.conCaras && TemaId.PSICODELIA.conIntensidad)
        assertEquals(listOf(TemaId.PSICODELIA), TemaId.entries.filter { it.conCaras || it.conIntensidad })
    }

    @Test fun forzarYEscalones() {
        try {
            EstadoTema.forzar(Cara.INTENSA, 0.62f)
            assertEquals(Cara.INTENSA, EstadoTema.caraDe(TemaId.PSICODELIA))
            assertEquals(Cara.BASE, EstadoTema.caraDe(TemaId.HELENO))
            assertEquals(0.62f, EstadoTema.intensidad, 1e-6f)
            EstadoTema.forzar(Cara.BASE, 2f)
            assertEquals(1f, EstadoTema.intensidad, 0f)
        } finally {
            EstadoTema.forzar(null, null)
        }
        assertEquals(0.625f, EstadoTema.escalon(0.62f), 1e-6f)
        assertEquals(0f, EstadoTema.escalon(Float.NaN), 0f)
        assertEquals(1f, EstadoTema.escalon(3f), 0f)
    }
}
