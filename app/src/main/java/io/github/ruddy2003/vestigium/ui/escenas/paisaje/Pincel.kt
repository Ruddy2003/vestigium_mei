package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * El color de una cosa de caras planas (un muro, una caja, un banco) con la luz de un momento: su cara de luz, su cara
 * de sombra, la que mira a quien mira y la que mira al cielo. De día manda el sol ([r]); de noche, además, [calida]
 * (de 0 a 1) dice cuánto la alumbra una lumbre, una vela o un farol cercano, con el color [lumbre].
 *
 * Es la misma idea que ya llevaban dentro, cada una a su manera, Distopía (`Pincel`), Revolución francesa (`Luz`),
 * Vapor victoriano (`PincelVapor`) y Egipto (`SolEgipto`). Se subió a la biblioteca en la tanda S11 para las escenas
 * nuevas; las que ya estaban aprobadas siguen con la suya y no cambian.
 */
class Pincel(val l: Luces, val r: Rayo, val calida: Float = 0f, val lumbre: Color = Color(0xFFFFB45E)) {
    /** El mismo pincel con otra [calida]. */
    fun con(calida: Float) = Pincel(l, r, calida.coerceIn(0f, 1f), lumbre)

    private fun deLumbre(c: Color) = Color(c.red * lumbre.red, c.green * lumbre.green, c.blue * lumbre.blue, c.alpha)
    private fun mas(base: Color, c: Color, k: Float) = if (calida <= 0f) base else lerp(base, deLumbre(lerp(c, Color.White, 0.15f)), (calida * k).coerceIn(0f, 1f))

    /** La cara que mira al sol. */
    fun luz(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.36f * r.contraste), lejos), c, 0.85f)
    /** El color propio, de lado. */
    fun medio(c: Color, lejos: Float = 0f) = mas(l.tono(c, lejos), c, 0.5f)
    /** La cara que le da la espalda al sol: azul violeta, nunca gris. */
    fun sombra(c: Color, lejos: Float = 0f) = mas(l.tono(mult(c, r.color, 0.9f * r.contraste), lejos), c, 0.16f)
    /** La cara que mira a quien mira. */
    fun frente(c: Color, lejos: Float = 0f) = mas(if (r.lat < 0f) l.tono(lerp(c, Color(0xFFFFF2D0), 0.14f * r.contraste), lejos) else l.tono(mult(c, r.color, 0.24f * r.contraste), lejos), c, 0.5f)
    /** La cara que mira a la derecha; con [alReves], la que mira a la izquierda. */
    fun lado(c: Color, lejos: Float = 0f, alReves: Boolean = false) = if ((r.lat < 0f) != alReves) sombra(c, lejos) else luz(c, lejos)
    /** Lo que mira al cielo: un tejado plano, el suelo, la tapa de una caja. */
    fun arriba(c: Color, lejos: Float = 0f) = mas(l.tono(lerp(c, Color(0xFFFFF2D0), 0.22f * r.contraste * r.dia), lejos), c, 0.6f)
    /** El sol está a la derecha del cuadro. */
    val solALaDerecha: Boolean get() = r.lat > 0f
}
