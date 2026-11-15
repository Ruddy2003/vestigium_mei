package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Lou. Cada uno tiene su sitio en el club: los chicos van sobre la mesa cercana (cortada por
 * el borde de abajo), en su borde de delante; los grandes van de pie al lado de esa mesa y se recuestan en su canto. El
 * piano es el piano de siempre: ese día, algo lo señala.
 *
 * **Contrato vacío (tanda S15, primer agente):** las llamadas ya están puestas en el club (`EscenaDecoClubCosas.kt`,
 * `CosasDeco.mesaCercana`) con un soporte y un apoyo de buen tamaño; los dibujos los pondrá otro agente.
 */
internal object ObjetosDeco {
    /** Los doce objetos del calendario de Lou (`AnioG6.kt`). */
    val TODOS = listOf("piano", "partitura", "sombrero", "ficha", "taza", "disco", "radio", "collar", "zapatos", "panuelo", "cigarrillo", "metronomo")

    /** Si el objeto de hoy es de los grandes (no cabe sobre la mesa y va apoyado). */
    fun esGrande(m: Momento): Boolean = false

    /**
     * El objeto chico de hoy, sobre la mesa cercana. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el punto de la tapa de la mesa donde se apoya; [escala] 1 es el tamaño del primer plano (120 unidades del
     * lienzo por metro, las de la mesa cercana). [p] trae la luz de ese sitio: la lámpara de la mesa, el neón o el sol.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] (el suelo, junto a la mesa cercana) y se recuesta en [apoyo] (el canto de la
     * mesa). Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
