package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect

/** Pone [contenido] en el nicho [area] del mueble (fracciones de 0 a 1 del espacio disponible). */
@Composable
fun EnNicho(area: Rect, modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        Box(Modifier.offset(maxWidth * area.left, maxHeight * area.top).size(maxWidth * area.width, maxHeight * area.height), content = contenido)
    }
}
