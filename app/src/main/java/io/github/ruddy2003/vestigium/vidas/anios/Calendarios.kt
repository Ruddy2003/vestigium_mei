package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Calendario

/**
 * El registro de los calendarios: qué personajes de «Otras vidas» tienen ya escrito su año. Cada tanda de calendarios
 * (P2, y de A1 a A8) agrega aquí la lista de su archivo `Anio<Grupo>.kt`; el motor (`vidas/Ficha.kt`) no se toca.
 * Un personaje que no está aquí todavía no tiene ficha del día, ni cuento, ni escena.
 */
object Calendarios {
    val TODOS: List<Calendario> = listOf()

    private val porVida: Map<String, Calendario> by lazy { TODOS.associateBy { it.vida } }

    /** El calendario del personaje [vidaId], o null si todavía no se escribe. */
    fun de(vidaId: String): Calendario? = porVida[vidaId]
}
