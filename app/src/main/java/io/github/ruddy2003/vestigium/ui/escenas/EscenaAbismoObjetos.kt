package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel

/**
 * Los doce objetos del día de Nikos, iguales en los dos cuadros (el barco y el muelle). **Por ahora, solo el contrato**
 * (tanda S16, primer agente): los dibuja el agente de los objetos. Cada cuadro dice dónde:
 * - los **chicos** van sobre su soporte: en el barco, la tapa de la escotilla; en el muelle, el cajón del primer plano;
 * - los **grandes** ([GRANDES]) van de pie o apoyados: en el barco, recostados en la bomba; en el muelle, en el cajón.
 *
 * **Lo que ya está en el cuadro.** Varios objetos son también piezas fijas del cuadro:
 * - [EN_EL_CUADRO] (la bomba, el icono, el barco): ese día no se pinta otro; el objeto es el del cuadro, que ya está bien
 *   a la vista (la bomba en medio de la cubierta, la hornacina en la popa, la goleta entera o, en el muelle, su popa).
 * - [QUITA_DEL_CUADRO] (el casco, el traje, las botas): ese día el objeto se pinta en su soporte y el cuadro **quita el
 *   suyo** ([elCuadroPinta]), como `laCocinaPinta` en Soviético: un casco menos en los ganchos, el traje no cuelga del
 *   palo, las botas no están junto a la bomba. Así no sale dos veces.
 */
internal object ObjetosAbismo {
    /** Los doce objetos del calendario de Nikos (`AnioG6.kt`). */
    val TODOS = listOf("casco", "escafandra", "botas", "bomba", "cuerda", "cuchillo", "red", "esponja", "comboloi", "icono", "barco", "pan")

    /** Los que no caben sobre el soporte: van de pie o apoyados (el traje, la cuerda adujada, la red con su aro, las botas). */
    val GRANDES = setOf("escafandra", "cuerda", "red", "botas")

    /** Los que son piezas fijas del cuadro: ese día no se pinta otro. */
    val EN_EL_CUADRO = setOf("bomba", "icono", "barco")

    /** Los que el cuadro tiene y quita el día que son el objeto, para que no salgan dos veces. */
    val QUITA_DEL_CUADRO = setOf("casco", "escafandra", "botas")

    /** Si el objeto de hoy es de los grandes. */
    fun esGrande(m: Momento): Boolean = m.ficha.objeto in GRANDES

    /** Si [id] es de los grandes. */
    fun esGrande(id: String?): Boolean = id in GRANDES

    /** Si el cuadro pinta hoy su [pieza] («casco», «escafandra», «botas»): no, el día que esa pieza es el objeto. */
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
