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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.pantallas.paredTema
import kotlin.math.hypot

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
 *
 * Con [luz], en los modos oscuros la escena tiene una lámpara (10 nov 2026): un charco de luz cálida que cae desde arriba
 * sobre la pared, con las esquinas más hondas, y esa misma luz sumada sobre la parte alta del mueble. Solo la llevan algunas
 * escenas (la de hoy, el Ideario y las cartas en Diario; Social, Logros y Otras vidas en Vida): las demás quedan en penumbra,
 * y de ese contraste sale que lo iluminado se note. De día no hace nada, y Básico no la lleva (va sin adornos).
 * Con [llena] (el mueble ocupa toda la escena y tapa la pared, como en los recuadros de Vida), la luz va toda sobre el
 * mueble: más viva arriba, con las esquinas de abajo en sombra.
 */
@Composable
fun Escena(
    modifier: Modifier = Modifier,
    fondo: Boolean = true,
    relleno: PaddingValues = PaddingValues(0.dp),
    forma: Shape = MaterialTheme.shapes.medium,
    luz: Boolean = false,
    llena: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tema = Estilo.tema
    val oscuro = esOscuro()
    val lampara = luz && oscuro && !tema.sinDiseno
    val calida = luzCalida(cs.primary)
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
                if (lampara && fondo) luzEnPared(calida)
                drawContent()
                if (lampara) luzEnMueble(calida, llena)
            }
            .padding(relleno),
        content = content,
    )
}

/**
 * La respuesta al toque de un objeto que se toca por sí solo (un volumen del estante, un cajón del Ideario, el diario de
 * hoy, un sobre). **Desde el 19 nov 2026 ya no hay aura ni brillo, en ningún tema ni en ningún mueble** (el autor: «quitar
 * el brillo en todos los temas y todos los muebles»): mientras el dedo está encima, el objeto se hunde un poco (se encoge
 * a 0.965 de su tamaño, desde el centro) y al soltar vuelve, en 120 ms. Es una propiedad de su capa (`graphicsLayer`): el
 * objeto no se vuelve a trazar ni se copia, solo se pega un poco más chico.
 *
 * Antes era el aura: el objeto se volvía a dibujar 8 o 16 veces corrido en varias direcciones y teñido del acento, en 2 o
 * 4 anillos (hasta 65 trazados por cuadro mientras se encendía), con una capa fuera de pantalla por anillo. Con los muebles
 * del lote 10, de decenas de degradados cada uno, eso hacía que tocar un volumen o un cajón se sintiera pesado.
 *
 * Como antes, solo mira los toques (no los consume) y, si el dedo se mueve más de lo que cuenta como toque (estás
 * desplazando la pantalla), no responde: así los objetos que pasan bajo el dedo al desplazar no se mueven.
 *
 * [realce] y [reposo] ya no hacen nada (el aura cálida de los modos oscuros y el aura en reposo): se quedan para no tocar a
 * quien los pasa (`DisenoBiblioteca.auraEnReposo` y `DisenoIdeas.auraEnReposo` siguen en los contratos, sin efecto).
 */
fun Modifier.halo(realce: Boolean = false, reposo: Boolean = false): Modifier = composed {
    var tocando by remember { mutableStateOf(false) }
    var puntero by remember { mutableStateOf(false) }
    val encendido = tocando || puntero
    val animar = Repo.config.animaciones
    // Se lee solo dentro de `graphicsLayer`: mientras anima no se recompone ni se vuelve a trazar nada.
    val hundido = animateFloatAsState(if (encendido) 1f else 0f, tween(if (!animar) 0 else 120), label = "toque")
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
        .graphicsLayer {
            val k = 1f - 0.035f * hundido.value
            scaleX = k; scaleY = k
        }
}

/** El color de la luz de las lámparas: el acento del tema entibiado hacia el ámbar claro de una vela, para que cada tema conserve su tono. */
private fun luzCalida(acento: Color): Color = lerp(acento, Color(0xFFFFDDA8), 0.6f)

/**
 * La lámpara sobre la pared, detrás del mueble: un charco de luz que cae desde arriba al centro y se apaga hacia abajo, y
 * las esquinas un poco más oscuras para que la luz tenga contra qué notarse.
 */
private fun DrawScope.luzEnPared(calida: Color) {
    val w = size.width; val h = size.height
    drawRect(Brush.radialGradient(0f to calida.copy(alpha = 0.26f), 0.42f to calida.copy(alpha = 0.09f), 1f to Color.Transparent,
        center = Offset(w * 0.5f, -h * 0.12f), radius = maxOf(w * 0.62f, h * 1.2f)))
    drawRect(Brush.radialGradient(0f to Color.Transparent, 0.55f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.28f),
        center = Offset(w * 0.5f, h * 0.3f), radius = hypot(w * 0.5f, h * 0.8f)))
}

/**
 * La misma lámpara sobre el mueble: suma luz en su parte alta (no lo cubre con un velo), poca, para que los bordes de arriba
 * la tomen sin que el texto de los nichos pierda contraste.
 */
private fun DrawScope.luzEnMueble(calida: Color, llena: Boolean) {
    val w = size.width; val h = size.height
    val fuerza = if (llena) 0.24f else 0.12f
    drawRect(Brush.radialGradient(0f to calida.copy(alpha = fuerza), 0.5f to calida.copy(alpha = fuerza / 3), 1f to Color.Transparent,
        center = Offset(w * 0.5f, -h * 0.05f), radius = maxOf(w * 0.5f, h)), blendMode = BlendMode.Plus)
    // El borde de arriba toma la luz: una línea fina, viva al centro, que se pierde hacia los lados.
    drawRect(Brush.horizontalGradient(0f to Color.Transparent, 0.5f to calida.copy(alpha = if (llena) 0.6f else 0.42f), 1f to Color.Transparent,
        startX = w * 0.1f, endX = w * 0.9f), topLeft = Offset(w * 0.1f, 0f), size = androidx.compose.ui.geometry.Size(w * 0.8f, 1.5.dp.toPx()), blendMode = BlendMode.Plus)
    // Sin pared a la vista, la sombra de las esquinas de abajo va sobre el propio mueble.
    if (llena) drawRect(Brush.radialGradient(0f to Color.Transparent, 0.6f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.22f),
        center = Offset(w * 0.5f, h * 0.2f), radius = hypot(w * 0.5f, h * 0.9f)))
}
