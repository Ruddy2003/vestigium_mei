package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Liudmila, en su cocina. Los chicos van sobre su soporte, la mesa, en la esquina de delante
 * del hule, donde da la mancha de sol por la mañana y el cono de la lámpara de noche; los grandes van de pie en el suelo
 * de linóleo, recostados contra el canto de la mesa.
 *
 * **Contrato vacío (tanda S15, primer agente):** las llamadas ya están puestas en la cocina con un soporte y un apoyo de
 * buen tamaño; los dibujos los pondrá otro agente.
 *
 * Seis objetos son también piezas de la cocina (el samovar, el televisor, la radio de cable, el abrigo, las botas de
 * fieltro y la bolsa de red: `EscenaUrss.DE_LA_COCINA`). Mientras su id no esté en [DIBUJADOS], el de la cocina es el
 * objeto del día; cuando este contrato lo dibuje en su sitio, se agrega a [DIBUJADOS] y la cocina deja de pintar el suyo
 * ese día (`EscenaUrss.laCocinaPinta`), para que no salga dos veces.
 */
internal object ObjetosUrss {
    /** Los doce objetos del calendario de Liudmila (`AnioG6.kt`). */
    val TODOS = listOf("bolsa", "moneda", "libro", "abrigo", "botas", "radio", "televisor", "samovar", "termo", "chal", "pase", "postal")

    /** Los objetos de la cocina que este contrato ya dibuja él mismo el día que tocan. Vacío mientras no haya dibujos. */
    val DIBUJADOS: Set<String> = emptySet()

    /** Si el objeto de hoy es de los grandes (no cabe sobre la mesa y va de pie, recostado contra ella). */
    fun esGrande(m: Momento): Boolean = false

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo], el canto de la mesa. Devuelve null si hoy no hay
     * objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
