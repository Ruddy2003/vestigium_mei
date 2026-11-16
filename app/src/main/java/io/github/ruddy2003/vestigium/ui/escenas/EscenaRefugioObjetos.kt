package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de la residente, en la sala de bombas. Los chicos van sobre su soporte, la bancada, en la
 * esquina de delante a la derecha, donde la lámpara los alumbra a mediodía y la luz del portón de noche; los grandes van
 * de pie en el suelo junto a la punta de la bancada y se recuestan en su canto.
 *
 * **Contrato vacío (tanda S17, primer agente):** las llamadas ya están puestas en la sala (`PiezasSala.bancada` y
 * `PiezasSala.cosas`) con un soporte y un apoyo de buen tamaño, a la vista de día y de noche; los dibujos los pondrá otro
 * agente.
 *
 * **Para que nada salga dos veces:** cinco objetos son también piezas de la sala ([YA_EN_LA_SALA], que es
 * `EscenaRefugio.DE_LA_SALA`): el tocadiscos pequeño y el cuaderno del turno (en la bancada), el mono (en su gancho), la
 * llave de válvulas (apoyada en la bomba grande) y las linternas de dinamo (en su percha). Mientras su id no esté en
 * [DIBUJADOS], la pieza de la sala es el objeto del día; cuando este contrato lo dibuje en su sitio, se agrega a
 * [DIBUJADOS] y ese día la sala deja de pintar la suya (`EscenaRefugio.laSalaPinta`). Otros dos tienen su pareja en el
 * cuadro y **no** se quitan: la `flor` del objeto es de papel (la de verdad, la de Noa, va en su frasco desde `vuelven`), y
 * el `disco` del objeto es el que no tiene nadie (el de la sala va en su funda, junto al tocadiscos).
 */
internal object ObjetosRefugio {
    /** Los doce objetos del calendario de la residente (`AnioG7.kt`). */
    val TODOS = listOf("llave", "disco", "linterna", "mono", "contador", "racion", "cuaderno", "pastilla", "manta", "foto", "tocadiscos", "flor")

    /** Los grandes: no caben sobre la bancada y van de pie junto a ella (la llave larga como un brazo, el mono, la manta, el tocadiscos del comedor). */
    val GRANDES = setOf("llave", "mono", "manta", "tocadiscos")

    /** Los objetos que ya están en la sala como piezas suyas. */
    val YA_EN_LA_SALA: Set<String> get() = EscenaRefugio.DE_LA_SALA

    /** Los objetos de la sala que este contrato ya dibuja él mismo el día que tocan. Vacío mientras no haya dibujos. */
    val DIBUJADOS: Set<String> = emptySet()

    /** Si el objeto de hoy es de los grandes (no cabe sobre la bancada y va de pie, recostado contra ella). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el punto de la tapa de la bancada donde se apoya; [escala] 1 es el tamaño del primer plano (120 unidades
     * del lienzo por metro). [p] trae la luz de ese sitio: la lámpara, la luz del portón, la penumbra.
     */
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] (el suelo, junto a la punta de la bancada) y se recuesta en [apoyo] (el canto
     * de la bancada). Devuelve null si hoy no hay objeto o es de los chicos.
     */
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
