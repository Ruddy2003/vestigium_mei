package io.github.ruddy2003.vestigium.data

import kotlinx.serialization.KSerializer

/** Lectura de documentos guardados: lo que no se puede leer se cuenta, en vez de perderse en silencio. */
object Decodificar {
    /** Los documentos que se pudieron leer y cuántos fallaron; [ilegible] recibe el texto de cada uno que falló. */
    fun <T> todos(textos: List<String>, s: KSerializer<T>, ilegible: (String) -> Unit = {}): Pair<List<T>, Int> {
        var fallidos = 0
        val l = textos.mapNotNull { t -> runCatching { JSON.decodeFromString(s, t) }.getOrElse { fallidos++; ilegible(t); null } }
        return l to fallidos
    }
}
