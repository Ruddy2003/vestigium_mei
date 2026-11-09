package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.paredTema

/**
 * La escena de un mueble u objeto de las pestañas Diario y Vida: el mismo marco para todos (9 nov 2026).
 *
 * - Detrás, la pared del tema ([paredTema]) sobre un tono apenas distinto de la página, con las esquinas del tema y
 *   sin línea de contorno. Con [fondo] en `false` no se pone la pared: para lo que ya trae su propio fondo de punta a
 *   punta (el cielo de los sueños).
 * - Alrededor, un aura muy tenue del color del tema que dice «esto se toca». Se enciende mientras el dedo está encima
 *   (al tocar, al deslizar sobre ella o al pasar el puntero) y se apaga despacio al soltar.
 *
 * [fuente] deja que algo de fuera (el título de un recuadro de Vida, por ejemplo) también la encienda.
 */
@Composable
fun Escena(
    modifier: Modifier = Modifier,
    fondo: Boolean = true,
    relleno: PaddingValues = PaddingValues(0.dp),
    fuente: MutableInteractionSource? = null,
    forma: Shape = MaterialTheme.shapes.medium,
    content: @Composable BoxScope.() -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val oscuro = esOscuro()
    val propia = remember { MutableInteractionSource() }
    val src = fuente ?: propia
    val presionado by src.collectIsPressedAsState()
    val encima by src.collectIsHoveredAsState()
    var tocando by remember { mutableStateOf(false) }
    var puntero by remember { mutableStateOf(false) }
    val encendido = tocando || puntero || presionado || encima
    val animar = Repo.config.animaciones
    val luz by animateFloatAsState(if (encendido) 1f else 0f, tween(if (!animar) 0 else if (encendido) 140 else 520), label = "aura")
    val aura = colorAura(cs.primary, oscuro)
    // La pared: un poco más clara arriba y un poco más oscura abajo, como una sala con su piso.
    val base = lerp(lerp(cs.surface, cs.primary, 0.05f), cs.onSurface, if (oscuro) 0.04f else 0.025f)
    val arriba = lerp(base, if (oscuro) Color.White else cs.surface, if (oscuro) 0.03f else 0.35f)
    val abajo = lerp(base, cs.onSurface, if (oscuro) 0.05f else 0.06f)
    Box(
        modifier
            .drawBehind {
                // El aura: anillos cada vez más anchos y tenues alrededor del contorno, que se suman cerca del borde.
                val contorno = forma.createOutline(size, layoutDirection, this)
                val alcance = (5f + 6f * luz).dp.toPx()
                val total = 0.12f + 0.50f * luz
                val pasos = 8
                for (i in pasos downTo 1) {
                    val r = alcance * i / pasos
                    drawOutline(contorno, aura.copy(alpha = total / pasos * 1.6f), style = Stroke(2 * r))
                }
            }
            .pointerInput(Unit) {
                // Solo mira: no consume nada, así los cajones, volúmenes y notas de dentro siguen recibiendo su toque.
                awaitPointerEventScope {
                    while (true) {
                        val e = awaitPointerEvent(PointerEventPass.Initial)
                        when (e.type) {
                            PointerEventType.Enter -> puntero = true
                            PointerEventType.Exit -> puntero = false
                            else -> {}
                        }
                        tocando = e.changes.any { c ->
                            c.pressed && c.position.x in 0f..size.width.toFloat() && c.position.y in 0f..size.height.toFloat()
                        }
                    }
                }
            }
            .clip(forma)
            .then(if (fondo) Modifier.background(Brush.verticalGradient(listOf(arriba, base, abajo))) else Modifier)
            .drawWithContent {
                if (fondo) paredTema(tema, cs.onSurface, cs.primary)
                drawContent()
                // Encendida, la escena se aclara apenas, como si le diera la luz.
                if (luz > 0f) drawRect(aura.copy(alpha = 0.07f * luz), Offset.Zero, size)
            }
            .padding(relleno),
        content = content,
    )
}

/** El color del aura: el acento del tema, un poco más luminoso de noche para que se note sobre el fondo oscuro. */
private fun colorAura(primario: Color, oscuro: Boolean): Color = if (oscuro) lerp(primario, Color.White, 0.18f) else primario
