package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.TemaId
import org.junit.Assert.fail
import java.io.File

/**
 * Las pruebas del plan de diseño (docs/diseno/PLAN_TEMAS.md, principio 1) miden lo que le falta a cada tema y lo comparan con
 * una lista de pendientes escrita en la prueba. La prueba falla en los dos sentidos:
 * - si aparece un hueco que no está en la lista (algo se rompió o un tema nuevo llegó incompleto);
 * - si se cerró un hueco que sigue en la lista (hay que borrarlo, para que la lista solo se acorte).
 * Cada lote borra lo que completa, hasta que la lista quede vacía. El estado de hoy, listo para pegar en la prueba, se escribe
 * además en `app/build/temas/<nombre>.txt` (y Actions lo sube a la rama `capturas`).
 */
object Pendientes {
    /** [huecos] y [pendientes]: por superficie, los ids de los temas a los que les falta algo. */
    fun revisar(nombre: String, huecos: Map<String, Set<String>>, pendientes: Map<String, Set<String>>) {
        val errores = mutableListOf<String>()
        (huecos.keys + pendientes.keys).sorted().forEach { s ->
            val h = huecos[s].orEmpty(); val p = pendientes[s].orEmpty()
            (h - p).takeIf { it.isNotEmpty() }?.let { errores += "$s: hueco nuevo en ${it.sorted()}" }
            (p - h).takeIf { it.isNotEmpty() }?.let { errores += "$s: ya no falta en ${it.sorted()}; bórralo de los pendientes" }
        }
        val texto = huecos.filterValues { it.isNotEmpty() }.toSortedMap().entries.joinToString("\n", "mapOf(\n", "\n)") { (s, ids) ->
            "    \"$s\" to setOf(" + ids.sortedBy { id -> TemaId.entries.indexOfFirst { it.id == id } }.joinToString(", ") { "\"$it\"" } + "),"
        }
        guardar(nombre, texto)
        if (errores.isNotEmpty()) fail("$nombre: ${errores.size} diferencias con los pendientes\n" + errores.joinToString("\n") + "\n\nEstado de hoy:\n$texto")
    }

    /** Lo mismo con cuentas (por ejemplo, despachos agrupados por archivo): ninguna puede subir y la que baja se anota. */
    fun revisarCuentas(nombre: String, cuentas: Map<String, Int>, pendientes: Map<String, Int>) {
        val errores = mutableListOf<String>()
        (cuentas.keys + pendientes.keys).sorted().forEach { k ->
            val c = cuentas[k] ?: 0; val p = pendientes[k] ?: 0
            if (c > p) errores += "$k: $c (antes $p): no se agregan despachos así; usa un `when (tema)` exhaustivo"
            if (c < p) errores += "$k: bajó de $p a $c; anótalo en los pendientes"
        }
        val texto = cuentas.filterValues { it > 0 }.toSortedMap().entries.joinToString("\n", "mapOf(\n", "\n)") { (k, n) -> "    \"$k\" to $n," }
        guardar(nombre, texto)
        if (errores.isNotEmpty()) fail("$nombre: ${errores.size} diferencias con los pendientes\n" + errores.joinToString("\n") + "\n\nEstado de hoy:\n$texto")
    }

    private fun guardar(nombre: String, texto: String) = runCatching {
        File("build/temas").apply { mkdirs() }.resolve("$nombre.txt").writeText(texto + "\n")
    }

    /** Un archivo o carpeta de `app/src/main` (las pruebas corren desde `app/`, pero también desde la raíz). */
    fun main(ruta: String): File = listOf("src/main/$ruta", "app/src/main/$ruta").map { File(it) }.firstOrNull { it.exists() } ?: File("src/main/$ruta")

    /** Corre [bloque] con [tema] elegido en la configuración (lo que lee `Estilo`) y deja la configuración como estaba. */
    fun <T> conTema(tema: TemaId, bloque: () -> T): T {
        val antes = Repo.config
        Repo.configEnMemoria(Config(tema = tema.id, modo = "claro"))
        return try { bloque() } finally { Repo.configEnMemoria(antes) }
    }
}
