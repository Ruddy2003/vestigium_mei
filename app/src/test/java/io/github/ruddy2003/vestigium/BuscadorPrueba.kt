package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.pantallas.Pantallas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** El buscador encuentra pantallas y acciones por palabras sueltas, en cualquier orden y sin acentos (N-22). */
class BuscadorPrueba {
    private fun buscar(q: String) = Pantallas.buscar(q) { "" }

    @Test fun corregirSueno() {
        val l = buscar("corregir sueño")
        assertTrue(l.first().accion)
        assertEquals("dia_fecha", l.first().ruta)
    }

    @Test fun enCualquierOrdenYSinAcentos() = assertEquals(buscar("corregir sueño").map { it.titulo }, buscar("sueno corregir").map { it.titulo })

    @Test fun lugares() = assertTrue(buscar("lugares").any { it.ruta == "lugares" })

    @Test fun gastosDelMes() = assertTrue(buscar("gastos mes").any { it.ruta == "gastos" && !it.accion })

    @Test fun cadaHerramientaDeAnalisis() = assertTrue(buscar("ritmo sueño").any { it.ruta == "analisis" && it.antes != null })

    @Test fun loPrivadoSeEncuentraPorSuFuncion() = assertTrue(buscar("impulso").any { it.ruta == "recaidas" })

    @Test fun ideario() = assertTrue(buscar("aprendí video").any { it.ruta == "biblioteca_ideas" })

    @Test fun unaLetraNoBusca() = assertTrue(buscar("a").isEmpty())
}
