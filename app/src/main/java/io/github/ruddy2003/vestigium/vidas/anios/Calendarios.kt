package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Calendario

/**
 * El registro de los calendarios: uno por personaje de «Otras vidas» que ya tiene escritas sus cinco temporadas.
 * Cada tanda de calendarios suma aquí su lista, sin tocar el motor: [AnioPiloto] trae a Demeas, Pierre, Kei y Amaru;
 * las tandas A1 a A8 agregan `AnioG1.TODOS`, `AnioG2.TODOS`…
 *
 * Un personaje que no está aquí todavía no tiene ficha del día, ni cuento, ni escena.
 */
object Calendarios {
    val TODOS: List<Calendario> by lazy { AnioPiloto.TODOS }

    private val porVida: Map<String, Calendario> by lazy { TODOS.associateBy { it.vida } }

    fun de(vidaId: String): Calendario? = porVida[vidaId]
}
