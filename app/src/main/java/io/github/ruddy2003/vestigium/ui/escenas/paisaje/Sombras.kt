package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.abs
import kotlin.math.tan

/** [c] multiplicado por [s] (el color de una sombra), con fuerza [k] de 0 a 1. */
fun mult(c: Color, s: Color, k: Float = 1f): Color = lerp(c, Color(c.red * s.red, c.green * s.green, c.blue * s.blue, c.alpha), k.coerceIn(0f, 1f))

/**
 * De dónde viene la luz y cómo son las sombras que arroja. La del sol se calcula con las [Luces] del momento
 * (`Rayo(luces)`); la de una luz fija, con [rayoFijo].
 *
 * **[Aproximación]**: el sol se dibuja dentro del cuadro, pero la luz entra de lado (del lado en que está el sol) y un
 * poco desde delante, como en un cuadro pintado: con el sol de frente todo quedaría a contraluz. Por eso las sombras
 * caen hacia el otro lado y hacia el fondo, largas con el sol bajo y cortas a mediodía.
 *
 * - [lat]: de −1 (el sol a la izquierda) a 1 (a la derecha). Nunca es 0: a mediodía la luz queda un poco de lado. De
 *   noche sigue a la luna, y solo sirve para saber qué lado de las cosas es el claro.
 * - [ax] y [ay]: cuánto se corre una sombra en el suelo, de lado y hacia el fondo, por cada unidad de altura de lo que
 *   la arroja.
 * - [fuerza]: de 0 (de noche, o con el cielo cubierto) a 1. [dia]: de 0 a 1, cuánto es de día.
 * - [color]: el de las sombras, azul violeta; multiplica lo que tiene debajo.
 * - [contraste]: cuánto se distinguen el lado de la luz y el de la sombra de un cuerpo.
 */
class Rayo(
    val dia: Float, val lat: Float, val cot: Float, val tan: Float, val ax: Float, val ay: Float,
    val fuerza: Float, val color: Color, val contraste: Float,
)

/** El [Rayo] del sol (o de la luna) con las [Luces] del momento. Es el de todas las escenas al aire libre con sol. */
fun Rayo(l: Luces): Rayo {
    val dia = (1f - l.noche * 1.6f).coerceIn(0f, 1f)
    val cruda = (((if (dia > 0.05f) l.solX else l.lunaX) - 0.5f) * 2.6f).coerceIn(-1f, 1f)
    val lat = if (abs(cruda) < 0.35f) (if (cruda < 0f) -0.35f else 0.35f) else cruda
    val elevacion = Math.toRadians(8.0 + 50.0 * (l.solAltura / 1.35f).coerceIn(0f, 1f))
    val cot = (1.0 / tan(elevacion)).toFloat()
    return Rayo(
        dia = dia, lat = lat, cot = cot, tan = tan(elevacion).toFloat(), ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * abs(lat)) * cot,
        fuerza = 0.72f * l.solFuerza * dia * (1f - 0.8f * l.eclipse) * (l.solAltura / 0.12f).coerceIn(0f, 1f),
        color = lerp(Color(0xFF9484BC), Color(0xFF8688C6), ((l.solAltura - 0.3f) / 0.9f).coerceIn(0f, 1f)),
        contraste = 0.3f + 0.7f * dia * (0.4f + 0.6f * l.solFuerza),
    )
}

/**
 * El [Rayo] de una luz que no es el sol y no se mueve con la hora: un farol, un letrero, una lámpara de techo, una
 * hoguera. Para las escenas donde siempre es de noche y para los interiores (`docs/diseno/ESCENAS.md`, sección 5).
 *
 * - [lat]: de −1 (la luz a la izquierda de lo que alumbra) a 1 (a la derecha).
 * - [elevacion]: en grados sobre el suelo; baja da sombras largas.
 * - [fuerza] (de 0 a 1) y [color]: los de las sombras que arroja. El color multiplica lo que tiene debajo: para una
 *   luz cálida, una sombra fría, y al revés.
 * - [contraste]: cuánto se distinguen el lado de la luz y el de la sombra de un cuerpo.
 */
fun rayoFijo(lat: Float, elevacion: Float, fuerza: Float, color: Color, contraste: Float = 0.8f): Rayo {
    val e = Math.toRadians(elevacion.coerceIn(5f, 85f).toDouble())
    val cot = (1.0 / tan(e)).toFloat()
    return Rayo(dia = 1f, lat = lat, cot = cot, tan = tan(e).toFloat(), ax = -lat * cot, ay = -0.33f * (1.3f - 0.6f * abs(lat)) * cot,
        fuerza = fuerza.coerceIn(0f, 1f), color = color, contraste = contraste.coerceIn(0f, 1f))
}

/**
 * Una capa de sombras arrojadas: todo lo que [dibujo] pinte dentro (con el color que recibe) oscurece lo que ya hay
 * debajo, y una sola vez aunque las manchas se encimen. [cuanta] la atenúa. Si no hay sol, no dibuja nada.
 */
inline fun DrawScope.capaSombra(r: Rayo, cuanta: Float = 1f, crossinline dibujo: DrawScope.(Color) -> Unit) {
    if (r.fuerza * cuanta <= 0.02f) return
    drawIntoCanvas { tela ->
        tela.saveLayer(Rect(0f, 0f, ANCHO, ALTO), Paint().apply { blendMode = BlendMode.Multiply; alpha = (r.fuerza * cuanta).coerceIn(0f, 1f) })
        dibujo(r.color)
        tela.restore()
    }
}

/** Una sombra dentro de [capaSombra]: la [forma] con un poco de penumbra en el borde. */
fun DrawScope.sombra(forma: Path, c: Color) {
    drawPath(forma, c.copy(alpha = 0.4f), style = Stroke(1.2f))
    drawPath(forma, c)
}

/**
 * La sombra que arroja algo que está de pie en [pie], con la silueta [forma]: tumbada en el suelo y, si alcanza la
 * pared que hay detrás, subida a ella. La pared va de [paredArriba] a [suelo] (donde toca el suelo) y de [x0] a [x1].
 * Va dentro de [capaSombra].
 */
fun DrawScope.sombraArrojada(r: Rayo, forma: Path, pie: Offset, c: Color, suelo: Float, paredArriba: Float, x0: Float = 0f, x1: Float = ANCHO) {
    val separa = (pie.y - suelo).coerceAtLeast(1f)
    clipRect(0f, suelo, ANCHO, ALTO) {
        withTransform({ transform(Matrix().apply { values[4] = -r.ax; values[12] = r.ax * pie.y; values[5] = -r.ay; values[13] = pie.y * (1f + r.ay) }) }) { sombra(forma, c) }
    }
    clipRect(x0, paredArriba, x1, suelo) { translate(r.ax * separa / -r.ay, separa / -r.ay) { sombra(forma, c) } }
}

/**
 * La sombra de un follaje sobre lo que tiene debajo, dentro de [zona]: una masa de borde irregular con claros por
 * donde se cuela el sol. Con el sol bajo, las manchas se estiran. Va dentro de [capaSombra].
 */
fun DrawScope.sombraDeFollaje(az: Azar, zona: Rect, r: Rayo, c: Color, masas: Int = 46, claros: Int = 120) {
    val estira = 1f + abs(r.lat) * (r.cot / 2.4f).coerceAtMost(1.2f)
    fun punto(): Offset { val u = (az.real() + az.real()) - 1f; val v = (az.real() + az.real()) / 2f; return Offset(zona.center.x + u * zona.width / 2f, zona.top + v * zona.height) }
    repeat(masas) { val p = punto(); val rad = az.entre(9f, 20f); drawOval(c, Offset(p.x - rad * estira, p.y - rad * 0.6f), Size(rad * 2f * estira, rad * 1.2f)) }
    // El borde: hojas sueltas.
    repeat(masas * 2) { val p = punto() + Offset(az.entre(-26f, 26f) * estira, az.entre(-10f, 10f)); val rad = az.entre(1.4f, 4f); drawOval(c, Offset(p.x - rad * estira, p.y - rad * 0.5f), Size(rad * 2f * estira, rad)) }
    // Los claros.
    repeat(claros) { val p = punto(); val rad = az.entre(0.9f, 3.1f); drawOval(Color.Black, Offset(p.x - rad * estira, p.y - rad * 0.5f), Size(rad * 2f * estira, rad), blendMode = BlendMode.Clear) }
}

/**
 * Un cuerpo redondo (una vasija, un tronco, un canasto) entre [x0] y [x1]: el lado de la luz, el color propio, la
 * sombra propia y, en el borde, la luz que rebota del suelo.
 */
fun DrawScope.volumen(l: Luces, r: Rayo, forma: Path, x0: Float, x1: Float, propio: Color) {
    val luz = l.tono(lerp(propio, Color(0xFFFFF2D0), 0.42f * r.contraste)); val medio = l.tono(propio)
    val sombra = l.tono(mult(propio, r.color, 0.95f * r.contraste))
    val rebote = lerp(sombra, l.tono(lerp(propio, Color(0xFFF0B888), 0.3f)), 0.4f)
    drawPath(forma, Brush.horizontalGradient(0f to medio, 0.2f to luz, 0.48f to medio, 0.8f to sombra, 1f to rebote, startX = if (r.lat >= 0f) x1 else x0, endX = if (r.lat >= 0f) x0 else x1))
}

// Para escribir trazos con puntos.
fun Path.moveTo(p: Offset) = moveTo(p.x, p.y)
fun Path.lineTo(p: Offset) = lineTo(p.x, p.y)
fun Path.cubicTo(a: Offset, b: Offset, c: Offset) = cubicTo(a.x, a.y, b.x, b.y, c.x, c.y)
