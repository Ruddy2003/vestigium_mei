package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de la doctora, en su laboratorio. Los chicos van sobre su soporte, el claro de la mesa más
 * cercano (`MesaLab.CLARO`: en la esquina de cerca, donde cae la lámpara de su mesa y, a mediodía, el charco de la
 * lámpara de cultivo cercana); los grandes van de pie en el suelo, recostados en el taburete de Lauro
 * (`CuadroLab.PIE_GRANDE`), delante de la campana, donde se ven de día y de noche.
 *
 * **Contrato vacío (tanda S17, primer agente):** las llamadas ya están puestas en el cuadro con su soporte y su apoyo; los
 * dibujos los pondrá otro agente.
 *
 * Varios objetos son también cosas fijas del laboratorio ([DE_LA_SALA]): el microscopio, la taza, la tableta, el cuaderno
 * abierto, las semillas del plato negro, la pipeta y los auriculares están en la mesa; la bata, en la percha; los guantes
 * amarillos, colgados de sus pinzas en la percha, junto a la puerta; las macetas, apiladas en el estante de las semillas.
 * Mientras su id no esté en [DIBUJADOS], el del laboratorio es el objeto del día (ese día «bien a la vista»); cuando este
 * contrato lo dibuje él mismo, se agrega a [DIBUJADOS] y el cuadro deja de pintar el suyo ese día (`SalaLab.laSalaPinta`),
 * para que no salga dos veces. La tarjeta y la piedra (que está en el cajón) no están a la vista en el laboratorio: solo
 * salen el día que son el objeto.
 */
internal object ObjetosLaboratorio {
    /** Los doce objetos del calendario de la doctora (`AnioG8.kt`). */
    val TODOS = listOf("bata", "tableta", "pipeta", "microscopio", "semilla", "maceta", "guantes", "tarjeta", "auriculares", "cuaderno", "piedra", "taza")

    /** Los que no caben en el claro de la mesa: la bata, echada en el taburete, y la maceta con su grama, de pie en el suelo. */
    val GRANDES = setOf("bata", "maceta")

    /** Los objetos que son también cosas fijas del laboratorio (en la mesa, en la percha, en el estante). */
    val DE_LA_SALA = setOf("microscopio", "taza", "tableta", "cuaderno", "semilla", "pipeta", "bata", "guantes", "maceta", "auriculares")

    /** Los objetos del laboratorio que este contrato ya dibuja él mismo el día que tocan. Vacío mientras no haya dibujos. */
    val DIBUJADOS: Set<String> = emptySet()

    /** Si el objeto de hoy es de los grandes (va de pie en el suelo, recostado en el taburete). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro del claro de la mesa; [escala] 1 es el tamaño de primer plano.
     */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], el asiento del taburete. Devuelve null si hoy no hay
     * objeto o es de los chicos.
     */
    @Suppress("UNUSED_PARAMETER")
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
