// Generado por el arnés: piezas de Escena.kt (halo, luzCalida), copiadas tal cual en cada compilación. No se edita.
@file:Suppress("unused")
package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * El aura de un objeto que se toca por sí solo (un volumen del estante, el diario de hoy, un cajón del Ideario, un sobre):
 * sigue su silueta, no un rectángulo. Es muy tenue, del acento del tema, y se enciende mientras el dedo está encima (al
 * tocar o al pasar el puntero); al soltar se apaga. Solo mira los toques: no los consume. Si el dedo se mueve más de lo que
 * cuenta como toque (estás desplazando la pantalla), se apaga: antes cada objeto que pasaba bajo el dedo se encendía y se
 * volvía a trazar 65 veces por cuadro justo mientras la pantalla se movía.
 *
 * Cómo se dibuja: el objeto se vuelve a dibujar varias veces, corrido en varias direcciones (8 en reposo, 16 encendido) y
 * teñido del color del aura, en anillos cada vez más anchos y tenues (2 o 4); encima va el objeto tal cual. Donde el objeto es
 * transparente no hay aura. Las 16 direcciones y los 4 anillos son solo para el aura ya encendida del todo; mientras se
 * enciende o se apaga van las mismas 8 y 2 que en reposo (a esa velocidad no se notan los escalones).
 */
fun Modifier.halo(realce: Boolean = false): Modifier = composed {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    // Con [realce], en los modos oscuros el objeto toma la luz de la lámpara: en reposo el aura es cálida, algo más viva, y
    // asoma por el borde de arriba y los costados (de donde viene la luz) en vez de rodearlo parejo. Al tocarlo vuelve a
    // centrarse y crece como las demás.
    val toma = realce && oscuro && !Estilo.tema.sinDiseno
    val color = if (toma) luzCalida(cs.primary) else if (oscuro) lerp(cs.primary, Color.White, 0.18f) else cs.primary
    var tocando by remember { mutableStateOf(false) }
    var puntero by remember { mutableStateOf(false) }
    val encendido = tocando || puntero
    val animar = Repo.config.animaciones
    val luz by animateFloatAsState(if (encendido) 1f else 0f, tween(if (!animar) 0 else if (encendido) 140 else 260), label = "halo")
    this
        .pointerInput(Unit) {
            awaitPointerEventScope {
                // Dónde bajó el dedo y si ya se movió más que un toque (un arrastre: la pantalla se está desplazando).
                var origen: Offset? = null
                var arrastre = false
                while (true) {
                    val e = awaitPointerEvent(PointerEventPass.Initial)
                    when (e.type) {
                        PointerEventType.Enter -> puntero = true
                        PointerEventType.Exit -> puntero = false
                        else -> {}
                    }
                    val dedo = e.changes.firstOrNull { it.pressed }
                    if (dedo == null) { origen = null; arrastre = false }
                    else {
                        val o = origen
                        if (o == null) origen = dedo.position
                        else if ((dedo.position - o).getDistance() > viewConfiguration.touchSlop) arrastre = true
                    }
                    tocando = !arrastre && e.changes.any { c ->
                        c.pressed && c.position.x in 0f..size.width.toFloat() && c.position.y in 0f..size.height.toFloat()
                    }
                }
            }
        }
        .drawWithContent {
            val alcance = ((if (toma) 3.2f else 2.5f) + 4.5f * luz).dp.toPx()
            val total = (if (toma) 0.30f else 0.16f) + 0.50f * luz
            val sube = if (toma) alcance * 0.55f * (1f - luz) else 0f
            // En reposo el aura es angosta y bastan pocas copias; encendida es más ancha y lleva más para que no se vean escalones.
            val anillos = if (luz > 0.98f) 4 else 2
            val direcciones = if (luz > 0.98f) 16 else 8
            val lienzo = drawContext.canvas
            val borde = Rect(-alcance - 2f, -alcance - 2f - sube, size.width + alcance + 2f, size.height + alcance + 2f)
            for (k in anillos downTo 1) {
                val r = alcance * k / anillos
                val pincel = Paint().apply { colorFilter = ColorFilter.tint(color, BlendMode.SrcIn); alpha = (total / anillos * 1.4f * (if (anillos == 2) 1.2f else 1f)).coerceAtMost(1f) }
                lienzo.saveLayer(borde, pincel)
                for (j in 0 until direcciones) {
                    val a = 2 * PI * j / direcciones
                    translate((r * cos(a)).toFloat(), (r * sin(a)).toFloat() - sube) { this@drawWithContent.drawContent() }
                }
                lienzo.restore()
            }
            drawContent()
        }
}

/** El color de la luz de las lámparas: el acento del tema entibiado hacia el ámbar claro de una vela, para que cada tema conserve su tono. */
internal fun luzCalida(acento: Color): Color = lerp(acento, Color(0xFFFFDDA8), 0.6f)
