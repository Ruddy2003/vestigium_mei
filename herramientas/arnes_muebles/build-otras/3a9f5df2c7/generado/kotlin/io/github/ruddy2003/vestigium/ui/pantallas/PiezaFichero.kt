// Generado por el arnés: piezas de Fichero.kt (cajones, rotuloCajon), copiadas tal cual en cada compilación. No se edita.
@file:Suppress("unused")
package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Ideario
import io.github.ruddy2003.vestigium.ui.ideario.CajonIdeas

/** Los cajones del fichero: uno por tema, del más lleno al más vacío, y al final el de las ideas sin tema. */
internal fun cajones(ideas: List<Idea>): List<CajonIdeas> {
    val porTema = Ideario.temas(ideas).map { (t, n) -> CajonIdeas(t, n, Ideario.deTema(ideas, t).firstOrNull()?.fecha) }
    val sin = Ideario.deTema(ideas, null)
    return porTema + (if (sin.isNotEmpty()) listOf(CajonIdeas("", sin.size, sin.firstOrNull()?.fecha)) else emptyList())
}

/** Cómo se lee el nombre de un cajón. */
internal fun rotuloCajon(tema: String) = if (tema.isEmpty()) "sin tema" else "#" + tema.replace('_', ' ')
