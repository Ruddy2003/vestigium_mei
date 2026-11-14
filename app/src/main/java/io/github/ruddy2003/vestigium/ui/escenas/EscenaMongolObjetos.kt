package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Bat, iguales en los dos cuadros (el valle y el campamento de verano).
 * Cada cuadro dice dónde: los chicos van sobre su soporte (el cofre o el fieltro);
 * los grandes van de pie o apoyados en la tienda o en la carreta.
 */
internal object ObjetosMongol {
    /** Si el objeto de hoy es de los grandes (no cabe sobre el soporte y va apoyado). */
    fun esGrande(m: Momento): Boolean = false

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], un punto alto de la tienda o de la carreta.
     * Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
