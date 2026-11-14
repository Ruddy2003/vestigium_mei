package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.GuiaUso
import org.junit.Assert.assertTrue
import org.junit.Test

/** El «?» de cada pantalla abre una sección que existe, y cada pantalla del mapa existe (antes había claves que ninguna usaba). */
class GuiaUsoPrueba {
    @Test fun cadaClaveEsUnaPantallaYCadaSeccionExiste() {
        val ui = Pendientes.main("java/io/github/ruddy2003/vestigium/ui")
        val codigo = ui.walkTopDown().filter { it.isFile && it.name.endsWith(".kt") }.joinToString("\n") { it.readText() }
        // Los títulos de las herramientas de Análisis salen de su lista, no de un `Hoja("…"` escrito.
        val herramientas = io.github.ruddy2003.vestigium.ui.pantallas.HERRAMIENTAS.map { it.second }.toSet()
        GuiaUso.PANTALLAS.forEach { (titulo, seccion) ->
            assertTrue("la sección «$seccion» de «$titulo» no existe", GuiaUso.de(seccion) != null)
            // Una búsqueda en el texto: el título entre comillas en alguna pantalla (`Hoja("…"`, o el título que arma un `when`).
            val hay = titulo in herramientas || "\"$titulo\"" in codigo
            assertTrue("ninguna pantalla se llama «$titulo»", hay)
        }
    }
}
