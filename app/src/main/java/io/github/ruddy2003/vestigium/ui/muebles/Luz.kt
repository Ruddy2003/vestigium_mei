package io.github.ruddy2003.vestigium.ui.muebles

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/*
 * La luz de los muebles de la pestaña Diario, para acercarlos a los cuadros de «Otras vidas» (piloto en Helénico, 13 nov
 * 2026). Son las reglas de `docs/diseno/ESCENAS.md` llevadas a un interior, sin cambiar la forma de ningún mueble:
 * - la sombra tiene color (violeta hondo, como en los cuadros), nunca negro ni gris;
 * - el lado que mira a la luz se aclara hacia crema cálida, no hacia blanco;
 * - la noche (el modo oscuro) es azul violeta, no un velo café: lo encendido alumbra y lo oscuro lleva un filo cálido;
 * - nada es liso: pinceladas en las superficies grandes y un grano fino de papel al final.
 * Es autónomo a propósito (no usa el motor de los cuadros, `ui/escenas/paisaje/`): los muebles dibujan en píxeles, no en el
 * lienzo de 380 × 285 de las escenas, y así se pueden ver en el arnés de escritorio. Las cifras de los colores salen de las
 * sombras y la noche del cuadro de Helénico.
 */

/** La sombra de los muebles: violeta hondo. Con alfa, sobre cualquier color, oscurece hacia frío en lugar de ensuciar. */
val SOMBRA_MUEBLE = Color(0xFF231B3A)
/** La luz que da en lo que mira a la ventana: crema cálida. */
val LUZ_MUEBLE = Color(0xFFFFF1D2)
/** El aire de un interior de noche: azul violeta. */
val NOCHE_MUEBLE = Color(0xFF221D3A)
/** El filo que deja la lámpara en lo que mira hacia ella, de noche. */
val FILO_MUEBLE = Color(0xFFFFC56E)

/** La sombra con color: en lugar de `Color.Black.copy(alpha = a)`. */
fun sombraMueble(a: Float): Color = SOMBRA_MUEBLE.copy(alpha = a.coerceIn(0f, 1f))

/** La luz cálida: en lugar de `Color.White.copy(alpha = a)`. */
fun luzMueble(a: Float): Color = LUZ_MUEBLE.copy(alpha = a.coerceIn(0f, 1f))

/** El filo cálido de noche y la luz crema de día, para los cantos que miran arriba. */
fun filoMueble(oscuro: Boolean, a: Float): Color = (if (oscuro) FILO_MUEBLE else LUZ_MUEBLE).copy(alpha = a.coerceIn(0f, 1f))

/** Lo que multiplica cada color en una noche normal: el aire azul violeta del cuarto. */
private val AIRE_NOCHE = Color(0xFF75719F)

/**
 * Un color en el modo oscuro: **multiplicado** por el aire azul violeta de la noche, como en los cuadros, y un poco hacia
 * [NOCHE_MUEBLE] para unir la paleta. Así lo claro baja y se enfría, pero lo oscuro sigue oscuro: mezclar hacia un color
 * de noche aclaraba los negros y dejaba todo como con neblina. [k] es cuánta noche (0.42, la normal). De día, tal cual.
 */
fun deNoche(c: Color, oscuro: Boolean, k: Float = 0.42f): Color {
    if (!oscuro) return c
    val f = (k / 0.42f).coerceIn(0f, 1.6f)
    val m = lerp(Color.White, AIRE_NOCHE, (0.85f * f).coerceIn(0f, 1f))
    return lerp(Color(c.red * m.red, c.green * m.green, c.blue * m.blue, c.alpha), NOCHE_MUEBLE.copy(alpha = c.alpha), (0.12f * f).coerceIn(0f, 1f))
}

/** Un número de 0 a 1, siempre el mismo para la misma [semilla] y [k]. */
private fun azarMueble(semilla: Int, k: Int): Float {
    var x = semilla * 374761393 + k * 668265263
    x = (x xor (x ushr 13)) * 1274126177
    return ((x xor (x ushr 16)) and 0xffffff) / 16777215f
}

/**
 * Pinceladas de gouache sobre una superficie grande y lisa: óvalos largos y tenues del [color], que rompen el plano sin
 * degradados (regla 7). [u] es un dp en píxeles.
 */
fun DrawScope.pinceladas(r: Rect, color: Color, semilla: Int, cuantas: Int, u: Float, alfa: Float = 0.08f) {
    clipRect(r.left, r.top, r.right, r.bottom) {
        for (k in 0 until cuantas) {
            val largo = (10f + 26f * azarMueble(semilla, 3 * k)) * u
            val alto = largo * (0.18f + 0.16f * azarMueble(semilla, 3 * k + 1))
            val x = r.left + r.width * azarMueble(semilla, 3 * k + 2) - largo / 2
            val y = r.top + r.height * azarMueble(semilla + 7, k) - alto / 2
            drawOval(color.copy(alpha = alfa * (0.6f + 0.8f * azarMueble(semilla + 3, k))), Offset(x, y), Size(largo, alto))
        }
    }
}

/** La sombra que un objeto deja en lo que lo sostiene, corrida a la derecha (la luz viene de arriba a la izquierda). */
fun DrawScope.sombraContacto(centro: Offset, ancho: Float, alto: Float, a: Float = 0.32f) {
    drawOval(sombraMueble(a), Offset(centro.x - ancho * 0.42f, centro.y - alto / 2), Size(ancho, alto))
}

/** La baldosa del grano: puntos oscuros (violeta) y claros (crema), pintados una sola vez. */
private val BALDOSA_GRANO: ImageBitmap by lazy {
    val lado = 160
    val img = ImageBitmap(lado, lado)
    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(lado.toFloat(), lado.toFloat())) {
        val r = java.util.Random(7)
        val oscuros = List(900) { Offset(r.nextFloat() * lado, r.nextFloat() * lado) }
        val claros = List(900) { Offset(r.nextFloat() * lado, r.nextFloat() * lado) }
        drawPoints(oscuros, PointMode.Points, SOMBRA_MUEBLE.copy(alpha = 0.5f), 1.1f, StrokeCap.Round)
        drawPoints(claros, PointMode.Points, LUZ_MUEBLE.copy(alpha = 0.55f), 1.1f, StrokeCap.Round)
    }
    img
}

/**
 * El grano fino de papel al final del dibujo (regla 7), en mosaico sobre [r]. Es barato: una baldosa ya pintada que se pega,
 * no miles de puntos por dibujo. Va solo en los muebles grandes (estante, libreta, mueble del Ideario, tablero de notas):
 * en los objetos chicos con aura cuadraría el aura.
 */
fun DrawScope.granoMueble(r: Rect, oscuro: Boolean, fuerza: Float = 1f) {
    val b = BALDOSA_GRANO
    val a = ((if (oscuro) 0.22f else 0.28f) * fuerza).coerceIn(0f, 1f)
    clipRect(r.left, r.top, r.right, r.bottom) {
        var y = r.top
        while (y < r.bottom) {
            var x = r.left
            while (x < r.right) { drawImage(b, Offset(x, y), alpha = a); x += b.width }
            y += b.height
        }
    }
}
