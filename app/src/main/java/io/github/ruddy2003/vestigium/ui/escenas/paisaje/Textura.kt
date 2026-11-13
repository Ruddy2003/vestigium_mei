package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.ruddy2003.vestigium.vidas.Azar

/**
 * El grano del papel, encima de toda la parte quieta de una escena: puntos claros y oscuros muy finos y unas manchas
 * grandes y tenues, porque un lavado nunca queda parejo. Quita el aspecto liso de los degradados. [fuerza], de 0 a 1.
 * Sale siempre igual con la misma semilla, para que el grano no baile de una hora a otra.
 */
fun DrawScope.grano(azar: Azar, fuerza: Float = 1f) {
    if (fuerza <= 0.01f) return
    repeat(46) {
        mancha(Offset(azar.entre(0f, ANCHO), azar.entre(0f, ALTO)), azar.entre(26f, 70f), azar.entre(14f, 40f), if (it % 2 == 0) Color(0xFFFFF4DC) else Color(0xFF2A2440), 0.035f * fuerza)
    }
    val cuantos = 17000
    val oscuros = ArrayList<Offset>(cuantos); val claros = ArrayList<Offset>(cuantos)
    repeat(cuantos) { oscuros += Offset(azar.entre(0f, ANCHO), azar.entre(0f, ALTO)); claros += Offset(azar.entre(0f, ANCHO), azar.entre(0f, ALTO)) }
    drawPoints(oscuros, PointMode.Points, Color(0xFF2A2440).copy(alpha = (0.11f * fuerza).coerceIn(0f, 1f)), 0.55f, StrokeCap.Round)
    drawPoints(claros, PointMode.Points, Color(0xFFFFF6E0).copy(alpha = (0.13f * fuerza).coerceIn(0f, 1f)), 0.55f, StrokeCap.Round)
}
