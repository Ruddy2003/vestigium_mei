package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Jean-Baptiste, iguales en los dos cuadros (la retirada y el vivac).
 * Cada cuadro dice dónde: los chicos van sobre su soporte (la mochila de pie del alto, junto al pabellón);
 * los grandes van de pie o apoyados (en los fusiles en pabellón).
 *
 * **Contrato vacío (tanda S14, primer agente):** las llamadas ya están puestas en la retirada con un soporte y un apoyo de
 * buen tamaño; los dibujos los pondrá otro agente.
 */
internal object ObjetosNapoleon {
    /** Los doce objetos del calendario de Jean-Baptiste (`AnioG5.kt`). */
    val TODOS = listOf("fusil", "bayoneta", "mochila", "capote", "chaco", "cantimplora", "galleta", "pipa", "carta", "cuchara", "botas", "manta")

    /** Si el objeto de hoy es de los grandes (no cabe sobre el soporte y va apoyado). */
    fun esGrande(m: Momento): Boolean = false

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], un punto alto de los fusiles en pabellón.
     * Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
