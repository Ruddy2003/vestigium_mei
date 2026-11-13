package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** El filtro de las capturas a pedido ([CapturasAPedido]): los temas, la corrida «solo» y la regla de los nombres. */
class CapturasAPedidoPrueba {
    @Test fun elFiltroDeTemas() {
        assertEquals(TemaId.entries, CapturasAPedido.temas(emptyList()))
        assertEquals(listOf(TemaId.HELENO), CapturasAPedido.temas(listOf("heleno")))
        // Salen en el orden de TemaId, no en el que se escribieron.
        assertEquals(listOf(TemaId.NEON, TemaId.HELENO, TemaId.GOTICO), CapturasAPedido.temas(listOf("gotico", "heleno", "neon")))
        // Un id mal escrito no puede dar una corrida vacía que parezca buena.
        assertThrows(IllegalArgumentException::class.java) { CapturasAPedido.temas(listOf("helenico")) }
    }

    @Test fun laCorridaSolo() {
        listOf("true", "TRUE", " 1 ", "si").forEach { assertTrue(it, CapturasAPedido.solo(it)) }
        listOf(null, "", "false", "0", "no").forEach { assertFalse("$it", CapturasAPedido.solo(it)) }
    }

    /**
     * En una corrida «solo», las capturas a pedido se reconocen por su nombre. Aquí se lee `Capturas.kt` y se
     * comprueba que el nombre de cada prueba dice la verdad: es a pedido si y solo si pide su superficie (con
     * `porTema` o con `CapturasAPedido.pedida`). Si no, una captura de siempre se colaría en las corridas cortas, o
     * una pedida no correría nunca.
     */
    @Test fun elNombreDeCadaCapturaDiceSiEsAPedido() {
        val ruta = "src/test/java/io/github/ruddy2003/vestigium"
        val carpeta = listOf(File(ruta), File("app/$ruta")).first { it.exists() }
        val prueba = Regex("""@Test\s+fun\s+(\w+)\s*\(""")
        val corte = Regex("""^\s*(@Test|private fun|fun |/\*\*|//)""")
        var vistas = 0
        listOf("Capturas.kt", "CapturasEscenario.kt").forEach { archivo ->
            val lineas = carpeta.resolve(archivo).readLines()
            lineas.forEachIndexed { i, linea ->
                val nombre = prueba.find(linea)?.groupValues?.get(1) ?: return@forEachIndexed
                val fin = (i + 1 until lineas.size).firstOrNull { corte.containsMatchIn(lineas[it]) } ?: lineas.size
                val pide = lineas.subList(i, fin).any { "porTema(" in it || "CapturasAPedido.pedida(" in it }
                assertEquals("$archivo, «$nombre»: su nombre y lo que hace no coinciden (ver CapturasAPedido.aPedido)", pide, CapturasAPedido.aPedido(nombre))
                vistas++
            }
        }
        assertTrue("Se esperaban cientos de capturas y se leyeron $vistas", vistas > 300)
        assertTrue(CapturasAPedido.aPedido("temaCuadro"))
        assertFalse("«temasAjustes» es una hoja de siempre", CapturasAPedido.aPedido("temasAjustes"))
    }
}
