// Sustitutos de dos funciones de dibujo que viven en archivos de pantallas (ui/pantallas/Galerias.kt y ui/Cartas.kt) y que
// arrastran media app. Los muebles no las usan (solo las fichas fijadas de Helénico y el sobre de Frente exterior): aquí no
// dibujan nada.
package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import io.github.ruddy2003.vestigium.ui.TemaId

fun simboloCalificacion(t: TemaId, c: Offset, r: Float): Path = Path()
