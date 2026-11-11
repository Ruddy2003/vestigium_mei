package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Una cosa del primer plano de una escena: su silueta ([forma], para la sombra que arroja), dónde pisa ([pie]) y cómo
 * se pinta ([pintar]). Con una lista de cosas, ordenada por dónde pisan, el primer plano sale en este orden
 * (`docs/diseno/ESCENAS.md`, regla 5; ver `patio` en `EscenaHeleno`):
 * 1. el muro y el suelo;
 * 2. una [capaSombra] con la [sombraArrojada] de cada cosa y la del follaje o el alero que enmarque;
 * 3. las cosas, de atrás adelante;
 * 4. otra [capaSombra], más tenue, con la sombra del follaje recortada sobre cada cosa que esté debajo de él
 *    ([bajoElFollaje]; el árbol que da la sombra no la recibe en su propio tronco).
 */
class Cosa(val forma: Path, val pie: Offset, val bajoElFollaje: Boolean = true, val pintar: DrawScope.() -> Unit)
