package io.github.ruddy2003.vestigium.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.paredTema
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * La escena de un mueble u objeto de las pestañas Diario y Vida: el mismo fondo para todos (9 nov 2026). Detrás va la
 * pared del tema ([paredTema]) sobre un tono apenas distinto de la página, con las esquinas del tema y sin línea de
 * contorno. Con [fondo] en `false` no se pone la pared: para lo que ya trae su propio fondo de punta a punta (el cielo
 * de los sueños). La escena no lleva aura: el aura ([halo]) va solo en los objetos que se tocan uno por uno.
 *
 * La escena entera se guarda como imagen (capa `Offscreen`, 10 nov 2026): la pared, el mueble y las auras en reposo se
 * trazan una vez y, mientras nada cambie dentro, cada cuadro solo se pega esa imagen. Antes, con el fondo vivo moviéndose
 * 60 veces por segundo, la pantalla volvía a trazar en cada cuadro los cientos de figuras de cada pared y cada mueble, y
 * las copias de cada aura (17 por objeto): por eso Diario y Vida se sentían más lentas desde que llegaron las escenas.
 */
@Composable
fun Escena(
    modifier: Modifier = Modifier,
    fondo: Boolean = true,
    relleno: PaddingValues = PaddingValues(0.dp),
    forma: Shape = MaterialTheme.shapes.medium,
    content: @Composable BoxScope.() -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val oscuro = esOscuro()
    // La pared: un poco más clara arriba y un poco más oscura abajo, como una sala con su piso.
    val base = lerp(lerp(cs.surface, cs.primary, 0.05f), cs.onSurface, if (oscuro) 0.04f else 0.025f)
    val arriba = lerp(base, if (oscuro) Color.White else cs.surface, if (oscuro) 0.03f else 0.35f)
    val abajo = lerp(base, cs.onSurface, if (oscuro) 0.05f else 0.06f)
    Box(
        modifier
            .graphicsLayer { shape = forma; clip = true; compositingStrategy = CompositingStrategy.Offscreen }
            .then(if (fondo) Modifier.background(Brush.verticalGradient(listOf(arriba, base, abajo))) else Modifier)
            .drawWithContent {
                if (fondo) paredTema(tema, cs.onSurface, cs.primary)
                drawContent()
            }
            .padding(relleno),
        content = content,
    )
}

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
fun Modifier.halo(): Modifier = composed {
    val cs = MaterialTheme.colorScheme
    val oscuro = esOscuro()
    val color = if (oscuro) lerp(cs.primary, Color.White, 0.18f) else cs.primary
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
            val alcance = (2.5f + 4.5f * luz).dp.toPx()
            val total = 0.16f + 0.50f * luz
            // En reposo el aura es angosta y bastan pocas copias; encendida es más ancha y lleva más para que no se vean escalones.
            val anillos = if (luz > 0.98f) 4 else 2
            val direcciones = if (luz > 0.98f) 16 else 8
            val lienzo = drawContext.canvas
            val borde = Rect(-alcance - 2f, -alcance - 2f, size.width + alcance + 2f, size.height + alcance + 2f)
            for (k in anillos downTo 1) {
                val r = alcance * k / anillos
                val pincel = Paint().apply { colorFilter = ColorFilter.tint(color, BlendMode.SrcIn); alpha = (total / anillos * 1.4f * (if (anillos == 2) 1.2f else 1f)).coerceAtMost(1f) }
                lienzo.saveLayer(borde, pincel)
                for (j in 0 until direcciones) {
                    val a = 2 * PI * j / direcciones
                    translate((r * cos(a)).toFloat(), (r * sin(a)).toFloat()) { this@drawWithContent.drawContent() }
                }
                lienzo.restore()
            }
            drawContent()
        }
}
