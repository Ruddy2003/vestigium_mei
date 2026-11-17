package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * El mueble de la galería por omisión (parte 8, encargo C; `docs/diseno/CUADROS_PLAN.md`): el que usan los 39 temas mientras
 * no dibujen el suyo (`DisenoVida.muebleGaleria`). Solo con los colores de [EstadoMueble], para que valga en cualquier tema,
 * en claro y en oscuro. Punto de enganche: hoy no dibuja nada; el cuerpo lo escribe el encargo C.
 */
internal object MuebleGaleriaComun {
    /** Dibuja el mueble a todo el lienzo, con el nicho [nicho] (en fracciones de 0 a 1) en un fondo sólido de su material. */
    fun DrawScope.dibujar(e: EstadoMueble, oscuro: Boolean, nicho: Rect) {
    }
}
