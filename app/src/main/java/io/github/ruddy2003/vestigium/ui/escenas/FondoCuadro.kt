package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.vidas.FichaDelDia
import kotlin.math.ceil
import kotlin.math.roundToInt

// =====================================================================
//  El cuadro como fondo del teléfono (parte 6 del plan de mejoras, 17 nov 2026)
// =====================================================================
//
// El cuadro es apaisado (4:3) y el teléfono, vertical: el fondo es una franja vertical del cuadro, pintado a la altura de
// la pantalla. Se pinta fuera de la pantalla, sin componer nada, así que se puede hacer desde el pulso de 15 minutos con
// la app cerrada. Solo se pinta la franja: el resto del cuadro cae fuera de la imagen y no ocupa memoria.

/** La proporción del cuadro (ancho entre alto): la del lienzo de las escenas, 380 × 285. */
const val PROPORCION_CUADRO = ANCHO / ALTO

/**
 * Dónde cae la franja del fondo dentro del cuadro, en pixeles: el cuadro entero mide [anchoCuadro] × [altoCuadro] y la
 * franja, que es lo que se ve, empieza en ([izquierda], [arriba]) y mide [ancho] × [alto] (justo el tamaño de la pantalla).
 */
data class RecorteFranja(val anchoCuadro: Int, val altoCuadro: Int, val izquierda: Int, val arriba: Int, val ancho: Int, val alto: Int)

/**
 * La franja de una pantalla de [anchoPantalla] × [altoPantalla] pixeles, con su centro en [centro] (fracción del ancho del
 * cuadro: 0 la orilla izquierda, 1 la derecha; ver [EscenaVida.franjaFondo]).
 *
 * El cuadro se escala para **cubrir** la pantalla sin deformarse: en un teléfono vertical, a lo alto (el alto del cuadro es
 * el de la pantalla); en una pantalla más ancha que el cuadro, a lo ancho, y entonces sobra cuadro arriba y abajo y se
 * toma su centro. La franja nunca se sale del cuadro: si el centro pedido queda muy cerca de una orilla, se corre hacia
 * dentro lo justo.
 */
fun recorteFranja(anchoPantalla: Int, altoPantalla: Int, centro: Float, proporcion: Float = PROPORCION_CUADRO): RecorteFranja {
    require(anchoPantalla > 0 && altoPantalla > 0) { "pantalla sin tamaño: $anchoPantalla × $altoPantalla" }
    val alto = maxOf(altoPantalla, ceil(anchoPantalla / proporcion).toInt())
    val ancho = maxOf(anchoPantalla, (alto * proporcion).roundToInt())
    val c = if (centro.isNaN()) 0.5f else centro.coerceIn(0f, 1f)
    val izquierda = (c * ancho - anchoPantalla / 2f).roundToInt().coerceIn(0, ancho - anchoPantalla)
    val arriba = (alto - altoPantalla) / 2
    return RecorteFranja(ancho, alto, izquierda, arriba, anchoPantalla, altoPantalla)
}

/**
 * El [Momento] del cuadro de [escena] en el día lógico [dia] («2027-03-21») a la [hora] (0 a 24): el mismo que pinta Hoy.
 * null si a su personaje le falta el calendario (entonces no hay cuadro y el fondo sigue con el papel).
 */
fun momentoDelFondo(escena: EscenaVida, dia: String, hora: Double): Momento? {
    val ficha = FichaDelDia.de(dia, escena.vida(dia)) ?: return null
    return Momento(ficha, hora)
}

/** Cuánto se oscurece la parte alta del fondo de bloqueo, donde va el reloj del sistema (de 0 a 1, arriba del todo). */
const val SOMBRA_RELOJ = 0.30f

/** Hasta dónde baja esa sombra, en fracción del alto de la pantalla (el reloj grande del Pixel ocupa más o menos el primer cuarto). */
const val ALTO_SOMBRA_RELOJ = 0.42f

/**
 * El velo del fondo de inicio, del color de fondo del tema **en oscuro**: deja ver el cuadro, pero calmado, para que los
 * íconos y sus nombres se lean. Es oscuro también con la app en claro: los nombres de los íconos del Pixel son blancos, y
 * con un velo claro (probado en el arnés) no se leían sobre el cielo de día. De noche el cuadro ya es oscuro y basta menos.
 */
const val VELO_INICIO_DIA = 0.46f
const val VELO_INICIO_NOCHE = 0.28f

/** Los dos fondos del cuadro: [bloqueo] (la franja tal cual, con sombra para el reloj) e [inicio] (la misma, con el velo). */
class FondosDelCuadro(val bloqueo: ImageBitmap, val inicio: ImageBitmap)

/**
 * Pinta los dos fondos del cuadro de [escena] en el [momento], del tamaño de la pantalla que dice [recorte]. Es una
 * función pura: no toca la pantalla ni compone nada, y se puede llamar desde cualquier hilo (el pulso la llama con la app
 * cerrada). La franja se pinta una sola vez: el de inicio es una copia con el velo encima.
 *
 * - La parte quieta y la viva (en el instante [RELOJ_QUIETO], como las capturas) se pintan sobre el cuadro entero, pero
 *   el lienzo se corre a la izquierda de la franja y todo lo de fuera cae fuera de la imagen.
 * - **Bloqueo:** la franja, con una sombra suave en la parte alta para que el reloj del sistema se lea (también cuando el
 *   cielo es claro) y otra, más leve, abajo, donde van los accesos de la pantalla de bloqueo.
 * - **Inicio:** la franja bajo un velo de [velo] (el color de fondo del tema en oscuro), con la fuerza
 *   [VELO_INICIO_DIA] o, si la luz del cuadro es de noche, [VELO_INICIO_NOCHE]. Sin emblema ni texto.
 *
 * Cada imagen ocupa ancho × alto × 4 bytes (unos 10 MB a 1080 × 2424): quien las pide las suelta en cuanto las pone.
 */
fun pintarFondosDelCuadro(escena: EscenaVida, momento: Momento, recorte: RecorteFranja, densidad: Float, velo: Color): FondosDelCuadro {
    val w = recorte.ancho; val h = recorte.alto
    val bloqueo = ImageBitmap(w, h)
    val tela = Canvas(bloqueo)
    // 1. La franja del cuadro: el lienzo entero, corrido para que la franja caiga en la imagen.
    tela.save()
    tela.translate(-recorte.izquierda.toFloat(), -recorte.arriba.toFloat())
    CanvasDrawScope().draw(Density(densidad), LayoutDirection.Ltr, tela, Size(recorte.anchoCuadro.toFloat(), recorte.altoCuadro.toFloat())) {
        lienzo { with(escena) { quieta(momento) } }
        lienzo { with(escena) { viva(momento, RELOJ_QUIETO) } }
    }
    tela.restore()
    // 2. El de inicio: la misma franja, con el velo.
    val inicio = ImageBitmap(w, h)
    CanvasDrawScope().draw(Density(densidad), LayoutDirection.Ltr, Canvas(inicio), Size(w.toFloat(), h.toFloat())) {
        drawImage(bloqueo)
        drawRect(velo.copy(alpha = if (momento.luz.franja.oscura) VELO_INICIO_NOCHE else VELO_INICIO_DIA))
    }
    // 3. El de bloqueo: la sombra del reloj arriba y una más leve abajo.
    CanvasDrawScope().draw(Density(densidad), LayoutDirection.Ltr, tela, Size(w.toFloat(), h.toFloat())) {
        drawRect(Brush.verticalGradient(listOf(Color.Black.copy(alpha = SOMBRA_RELOJ), Color.Black.copy(alpha = SOMBRA_RELOJ * 0.45f), Color.Transparent),
            startY = 0f, endY = h * ALTO_SOMBRA_RELOJ))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f)), startY = h * 0.86f, endY = h.toFloat()),
            topLeft = Offset(0f, h * 0.86f), size = Size(w.toFloat(), h * 0.14f))
    }
    bloqueo.prepareToDraw(); inicio.prepareToDraw()
    return FondosDelCuadro(bloqueo, inicio)
}
