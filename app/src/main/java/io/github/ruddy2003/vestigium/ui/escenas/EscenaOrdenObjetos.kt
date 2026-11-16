package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de la escriba. **Por ahora, solo el contrato** (tanda S18, primer agente): los dibuja el agente
 * de los objetos. El cuadro dice dónde:
 * - los **chicos** van sobre su soporte: el fondo de la primera tronera del parapeto, contra el páramo
 *   ([AdarveOrden.SOPORTE]);
 * - los **grandes** ([GRANDES]) van de pie en el adarve, apoyados en la segunda almena ([AdarveOrden.PIE_GRANDE],
 *   [AdarveOrden.APOYO_GRANDE]).
 *
 * **Lo que ya está en el cuadro.** El `candil` es objeto del día y también la lámpara de cada almena (la orden lo tiene por
 * signo). El día que es el objeto, **la hornacina de la almena de delante queda vacía** ([elCuadroPinta]): el candil está
 * en la tronera, como objeto del día, y no sale dos veces. De noche, ese día, alumbra desde la tronera **[Diseño propio]**.
 */
internal object ObjetosOrden {
    /** Los doce objetos del calendario de la escriba (`AnioG8.kt`). */
    val TODOS = listOf("pluma", "cuaderno", "lupa", "destornillador", "radio", "cantimplora", "racion", "manual", "candil", "brujula", "soldador", "codigo")

    /** Los que no caben en la tronera: van de pie en el adarve, apoyados en la almena (la radio con su antena, el manual abierto, la ración, la cantimplora, el soldador grande). */
    val GRANDES = setOf("radio", "manual", "racion", "cantimplora", "soldador")

    /** Los que son piezas fijas del cuadro: ese día no se pinta otro. Ninguno en Orden. */
    val EN_EL_CUADRO = emptySet<String>()

    /** Los que el cuadro tiene y quita el día que son el objeto, para que no salgan dos veces: el candil de la almena de delante. */
    val QUITA_DEL_CUADRO = setOf("candil")

    /** Si el objeto de hoy es de los grandes. */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Si el cuadro pinta hoy su [pieza] («candil»): no, el día que esa pieza es el objeto. */
    fun elCuadroPinta(m: Momento, pieza: String): Boolean = !(m.ficha.objeto == pieza && pieza in QUITA_DEL_CUADRO)

    /**
     * El objeto chico de hoy, sobre el soporte. No pinta nada si hoy no hay objeto, si es de los grandes o si ya está en el
     * cuadro. [sobre] es el centro de la cara de arriba del soporte; [escala] 1 es el tamaño de primer plano (60 unidades
     * por metro). **Pendiente** (agente de los objetos).
     */
    @Suppress("UNUSED_PARAMETER")
    fun DrawScope.sobreElSoporte(m: Momento, p: Pincel, sobre: Offset, escala: Float = 1f) {
        val id = m.ficha.objeto
        if (id == null || id in GRANDES || id in EN_EL_CUADRO || id !in TODOS) return
    }

    /**
     * El objeto grande de hoy: pisa en [pie] y se recuesta en [apoyo]. Devuelve null si hoy no hay objeto o es de los
     * chicos. **Pendiente** (agente de los objetos): hasta entonces, null.
     */
    @Suppress("UNUSED_PARAMETER")
    fun grande(m: Momento, p: Pincel, pie: Offset, apoyo: Offset, escala: Float = 1f): Cosa? {
        val id = m.ficha.objeto
        if (id == null || id !in GRANDES) return null
        return null
    }
}
