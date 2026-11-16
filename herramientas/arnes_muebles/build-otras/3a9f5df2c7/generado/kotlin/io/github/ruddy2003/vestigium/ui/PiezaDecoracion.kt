// Generado por el arnés: piezas de Decoracion.kt (piezasMano), copiadas tal cual en cada compilación. No se edita.
@file:Suppress("unused")
package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.sin

/**
 * La mano de la Prehistoria, de alto 2·[r] con centro en [c] (los dedos arriba, el pulgar a la derecha): palma, muñeca,
 * cuatro dedos y el pulgar, cada pieza por separado (se pintan una por una, del mismo color, sin huecos entre ellas).
 * Sirve para la mano en negativo (pintada del color de la pared sobre el ocre) y para la mano en positivo (en ocre).
 */
internal fun piezasMano(c: Offset, r: Float): List<Path> {
    fun caja(l: Float, t: Float, d: Float, b: Float, radio: Float) = Path().apply {
        addRoundRect(androidx.compose.ui.geometry.RoundRect(c.x + l * r, c.y + t * r, c.x + d * r, c.y + b * r, CornerRadius(radio * r)))
    }
    fun dedo(x: Float, arriba: Float, abajo: Float, ancho: Float) = caja(x - ancho / 2, arriba, x + ancho / 2, abajo, ancho / 2)
    val pulgar = Path().apply {
        moveTo(c.x + 0.3f * r, c.y + 0.5f * r); lineTo(c.x + 0.36f * r, c.y + 0.08f * r)
        quadraticTo(c.x + 0.62f * r, c.y - 0.2f * r, c.x + 0.76f * r, c.y - 0.08f * r)
        quadraticTo(c.x + 0.84f * r, c.y + 0.02f * r, c.x + 0.66f * r, c.y + 0.22f * r)
        lineTo(c.x + 0.4f * r, c.y + 0.6f * r); close()
    }
    return listOf(
        caja(-0.44f, -0.12f, 0.4f, 0.62f, 0.2f),     // palma
        caja(-0.3f, 0.45f, 0.26f, 1f, 0.06f),        // muñeca
        dedo(-0.34f, -0.62f, 0.05f, 0.17f),          // meñique
        dedo(-0.13f, -0.86f, 0f, 0.19f),             // anular
        dedo(0.1f, -0.98f, 0f, 0.19f),               // medio
        dedo(0.3f, -0.8f, 0.05f, 0.18f),             // índice
        pulgar,
    )
}
