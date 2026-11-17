package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs

/**
 * La sombra corta de algo lejano o pequeño que está de pie en [pie] (una casa del pueblo, un árbol del llano): un óvalo
 * tendido hacia el lado contrario al sol, más largo cuanto más [alto] y más bajo el sol. Es la idea de la
 * `sombraCorta` privada de Helénico, que no se toca; se agregó en la tanda S11 para Templario y Japón Edo.
 */
fun DrawScope.sombraCorta(r: Rayo, pie: Offset, ancho: Float, alto: Float) {
    if (r.fuerza < 0.05f) return
    val d = (r.ax * alto).coerceIn(-14f, 14f)
    drawOval(r.color, Offset(minOf(pie.x, pie.x + d) - ancho / 2f, pie.y - 0.5f), Size(abs(d) + ancho, maxOf(0.8f, ancho * 0.3f)), alpha = r.fuerza * 0.8f, blendMode = BlendMode.Multiply)
}
