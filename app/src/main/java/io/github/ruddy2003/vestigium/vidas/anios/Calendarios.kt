package io.github.ruddy2003.vestigium.vidas.anios

import io.github.ruddy2003.vestigium.vidas.Calendario

/**
 * El registro de los calendarios: uno por personaje de «Otras vidas» que ya tiene escritas sus cinco temporadas.
 * Cada tanda de calendarios suma aquí su lista, sin tocar el motor: [AnioPiloto] trae a Demeas, Pierre, Kei y Amaru;
 * [AnioG1] (tanda A1), a Naa, Ata, Ipy, Lucio, Alice y Citlali; [AnioG2] (tanda A2), a Kichizō, Mary, José, Rosa, Alguien del mundo y Alguien de México; [AnioG3] (tanda A3), a Cayo, Pay, Ama, K'an y Nanai-vandak; las tandas A4 a A8 agregan `AnioG4.TODOS`…
 *
 * Un personaje que no está aquí todavía no tiene ficha del día, ni cuento, ni escena.
 */
object Calendarios {
    val TODOS: List<Calendario> by lazy { AnioPiloto.TODOS + AnioG1.TODOS + AnioG2.TODOS + AnioG3.TODOS }

    /**
     * Los ids de los días «históricos» cuya fecha sale de un cálculo hecho en la sesión y no de una página (la Pascua
     * juliana, un eclipse), de todos los calendarios. Cada `Anio<Grupo>` declara los suyos.
     */
    val CALCULADOS: Set<String> by lazy { AnioPiloto.CALCULADOS + AnioG1.CALCULADOS + AnioG2.CALCULADOS + AnioG3.CALCULADOS }

    private val porVida: Map<String, Calendario> by lazy { TODOS.associateBy { it.vida } }

    fun de(vidaId: String): Calendario? = porVida[vidaId]
}
