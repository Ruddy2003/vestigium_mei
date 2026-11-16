package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Brenna, en la puerta de la posada. Los chicos van sobre su soporte, la losa del poyo, a la
 * derecha de la puerta, donde los toca el sol de la tarde y, de noche, la luz que sale de la sala; los grandes van de pie
 * en el suelo de losas y se recuestan en el muro del zaguán, a la izquierda de la puerta, junto al banco.
 *
 * **Contrato vacío (tanda S18, primer agente):** las llamadas ya están puestas en el primer plano (`CercaAntiguas`) con
 * un soporte y un apoyo de buen tamaño, a la vista de día y de noche, en verano y en invierno; los dibujos los pondrá otro
 * agente.
 *
 * **Para que nada salga dos veces:** tres objetos son también piezas del cuadro ([YA_EN_EL_CUADRO]): el bastón de Brenna
 * (apoyado junto a la puerta), la montura de carga (en su caballete, en primer plano a la izquierda de la puerta) y la cuerda (que no se pinta suelta:
 * va en la montura). Mientras su id no esté en [DIBUJADOS], la pieza del cuadro es el objeto del día; cuando este contrato
 * lo dibuje en su sitio, se agrega a [DIBUJADOS] y ese día el cuadro deja de pintar la suya ([elCuadroPinta]). Otros dos
 * tienen su pareja en el cuadro y **no** se quitan: el `cuerno` del objeto es el de guía, con la boca de latón (el de la
 * puerta es el cuerno de carnero clavado, que es de la posada), y la `linterna` del objeto es la de mina de Brenna (las del
 * pino son las de los que vuelven tarde).
 */
internal object ObjetosAntiguas {
    /** Los doce objetos del calendario de Brenna (`AnioG8.kt`). */
    val TODOS = listOf("baston", "linterna", "cuerno", "libro", "mapa", "cuerda", "cecina", "botas", "capa", "jarra", "pedernal", "montura")

    /** Los grandes: no caben sobre el poyo y van de pie, recostados en el muro (el bastón «de su altura y un palmo más», la cuerda de treinta brazas, la capa, la montura, las botas). */
    val GRANDES = setOf("baston", "cuerda", "capa", "montura", "botas")

    /** Los objetos que el cuadro ya tiene como piezas suyas. */
    val YA_EN_EL_CUADRO = setOf("baston", "montura", "cuerda")

    /** Los objetos del cuadro que este contrato ya dibuja él mismo el día que tocan. Vacío mientras no haya dibujos. */
    val DIBUJADOS: Set<String> = emptySet()

    /** Si el cuadro pinta hoy su pieza [pieza] (el bastón, la montura): sí, salvo que sea el objeto del día y este contrato ya lo dibuje. */
    fun elCuadroPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in DIBUJADOS)

    /** Si el objeto de hoy es de los grandes (no cabe sobre el poyo y va de pie, recostado en el muro). */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto o es de los grandes.
     * [sobre] es el punto de la losa del poyo donde se apoya; [escala] es cuántas veces 48 unidades del lienzo mide allí un
     * metro (el poyo está cerca: algo más de 1). [p] trae la luz de ese sitio: el sol de la tarde, la sombra que sube, la luz de la sala de noche.
     */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {}

    /**
     * El objeto grande de hoy: pisa en [pie] (las losas, junto al muro) y se recuesta en [apoyo] (el muro, a la altura de
     * la cadera). Devuelve null si hoy no hay objeto o es de los chicos.
     */
    @Suppress("UNUSED_PARAMETER")
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? = null
}
