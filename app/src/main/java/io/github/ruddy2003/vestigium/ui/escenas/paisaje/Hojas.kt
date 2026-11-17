package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

/**
 * Una hoja de higuera: ancha, de cinco lóbulos, con el centro en [c], de [rad] de radio y vuelta [giro] radianes.
 * Salió de la higuera de Helénico (`EscenaHeleno`) en la tanda S11, sin cambiar nada, para la de Templario.
 */
fun hojaDeHiguera(c: Offset, rad: Float, giro: Float): Path {
    val radios = listOf(1f, 0.6f, 0.95f, 0.55f, 0.8f, 0.3f, 0.8f, 0.55f, 0.95f, 0.6f)
    val pts = radios.mapIndexed { i, k -> val a = giro + i / 10f * 6.283f; c + Offset(sin(a), -cos(a)) * (rad * k) }
    return curva(pts + pts.first()).apply { close() }
}
