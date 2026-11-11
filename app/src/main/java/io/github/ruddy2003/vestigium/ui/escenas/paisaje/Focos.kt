package io.github.ruddy2003.vestigium.ui.escenas.paisaje

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.vidas.Azar
import kotlin.math.abs
import kotlin.math.pow

/*
 * Luces que no son el sol: letreros, faroles, lámparas, velas, pantallas. Son para las escenas donde siempre es de
 * noche (Ciudad neón) y para los interiores (`docs/diseno/ESCENAS.md`, sección 5). Ahí la regla de «luz con
 * dirección» se cumple así: cada [Foco] tiñe lo que tiene cerca con su color, y deja en sombra el otro lado.
 *
 * Cómo se usan:
 * 1. La escena arma su lista de focos del momento (cuáles están encendidos) y con ella un [Alumbrado].
 * 2. Cada cosa pide su color a [Alumbrado.en] en vez de a `Luces.tono`: es el color de noche más lo que le llega de
 *    cada foco. Las superficies grandes se pintan con [planoH] o [planoV], y los cuerpos con [cuerpo].
 * 3. Las sombras arrojadas salen del foco que manda, con [rayoDeFoco], dentro de una `capaSombra`.
 * 4. Lo que da luz (un tubo de neón, una ventana) se pinta tal cual, con [tuboNeon] o a mano.
 */

/**
 * Una luz en un sitio del cuadro. [p]: dónde está. [color] y [fuerza] (de 0 a algo más de 1): lo que da. [alcance]: a
 * qué distancia, en unidades del lienzo, su luz cae a la mitad. [suelo]: la altura del cuadro (y) en la que pisa lo que
 * tiene debajo; sirve para saber hacia dónde tumba las sombras.
 */
class Foco(val p: Offset, val color: Color, val fuerza: Float, val alcance: Float, val suelo: Float = p.y)

/** La luz de un momento en una escena de focos: la de fondo ([l], la de la noche o la del interior) y los [focos]. */
class Alumbrado(val l: Luces, val focos: List<Foco>) {
    /**
     * El color [propio] de algo que está en [p]. [mira]: hacia dónde da su cara, de −1 (hacia la izquierda) a 1 (hacia
     * la derecha); con 0 recibe la luz de todos los lados. Una cara que da la espalda a un foco apenas recibe su luz.
     * [lejos] lo desvanece hacia la bruma, como en `Luces.tono`.
     */
    fun en(p: Offset, propio: Color, mira: Float = 0f, lejos: Float = 0f): Color {
        val base = l.tono(propio, lejos)
        var r = base.red; var g = base.green; var b = base.blue
        val cerca = 1f - 0.8f * lejos.coerceIn(0f, 1f)
        for (f in focos) {
            if (f.fuerza <= 0.01f) continue
            val dx = f.p.x - p.x; val dy = f.p.y - p.y
            var k = f.fuerza / (1f + (dx * dx + dy * dy) / (f.alcance * f.alcance)) * cerca
            if (mira != 0f) k *= if (dx * mira > 0f) 1f else 1f - 0.85f * abs(mira).coerceAtMost(1f)
            r += (propio.red * 0.9f + 0.06f) * f.color.red * k; g += (propio.green * 0.9f + 0.06f) * f.color.green * k; b += (propio.blue * 0.9f + 0.06f) * f.color.blue * k
        }
        return Color(r.coerceIn(0f, 1f), g.coerceIn(0f, 1f), b.coerceIn(0f, 1f), propio.alpha)
    }

    /** Lo mismo con un entero `0xAARRGGBB`. */
    fun en(p: Offset, propio: Long, mira: Float = 0f, lejos: Float = 0f): Color = en(p, Color(propio), mira, lejos)
}

/** Una superficie que se extiende de lado, de [x0] a [x1] a la altura [y]: su color cambia según a qué foco se acerca. */
fun DrawScope.planoH(a: Alumbrado, forma: Path, x0: Float, x1: Float, y: Float, propio: Color, mira: Float = 0f, lejos: Float = 0f, pasos: Int = 6) {
    val paradas = Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to a.en(Offset(x0 + (x1 - x0) * t, y), propio, mira, lejos) }
    drawPath(forma, Brush.horizontalGradient(*paradas, startX = x0, endX = x1))
}

/** Una superficie que se extiende de arriba abajo, de [y0] a [y1] en la vertical [x]. [lejos0] y [lejos1]: la bruma en cada extremo. */
fun DrawScope.planoV(a: Alumbrado, forma: Path, x: Float, y0: Float, y1: Float, propio: Color, mira: Float = 0f, lejos0: Float = 0f, lejos1: Float = 0f, pasos: Int = 6) {
    val paradas = Array(pasos + 1) { i -> val t = i / pasos.toFloat(); t to a.en(Offset(x, y0 + (y1 - y0) * t), propio, mira, lejos0 + (lejos1 - lejos0) * t) }
    drawPath(forma, Brush.verticalGradient(*paradas, startY = y0, endY = y1))
}

/**
 * Un cuerpo (una bombona, una caja, un poste) entre [x0] y [x1], a la altura [y]: su lado izquierdo recibe los focos
 * de la izquierda y el derecho los de la derecha, así que con dos luces de distinto color queda con un filo de cada una.
 */
fun DrawScope.cuerpo(a: Alumbrado, forma: Path, x0: Float, x1: Float, y: Float, propio: Color, lejos: Float = 0f) {
    val m = (x0 + x1) / 2f
    val izq = a.en(Offset(x0, y), lerp(propio, Color.White, 0.22f), -1f, lejos); val der = a.en(Offset(x1, y), lerp(propio, Color.White, 0.22f), 1f, lejos)
    val medio = a.en(Offset(m, y), lerp(propio, Color(0xFF0E0C1E), 0.22f), 0f, lejos)
    drawPath(forma, Brush.horizontalGradient(0f to izq, 0.26f to lerp(izq, medio, 0.7f), 0.6f to medio, 0.84f to lerp(der, medio, 0.55f), 1f to der, startX = x0, endX = x1))
}

/**
 * El charco de luz que un foco deja en una pared o en el suelo: suma su color alrededor de [centro], hasta [rx] de
 * lado y [ry] de alto. [propio] es el color de la superficie (una pared oscura devuelve poca luz).
 */
fun DrawScope.charcoDeLuz(f: Foco, centro: Offset, rx: Float, ry: Float, propio: Color, cuanto: Float = 1f) {
    val k = (f.fuerza * cuanto).coerceIn(0f, 1.2f)
    if (k <= 0.02f || rx <= 0f || ry <= 0f) return
    val c = Color((propio.red * 0.8f + 0.2f) * f.color.red, (propio.green * 0.8f + 0.2f) * f.color.green, (propio.blue * 0.8f + 0.2f) * f.color.blue)
    scale(rx / ry, 1f, centro) {
        drawCircle(Brush.radialGradient(listOf(c.copy(alpha = (0.62f * k).coerceIn(0f, 1f)), c.copy(alpha = (0.26f * k).coerceIn(0f, 1f)), Color.Transparent), centro, ry), ry, centro, blendMode = BlendMode.Plus)
    }
}

/**
 * El [Rayo] con que el foco [f] tumba la sombra de algo que pisa en [pie]: lejos del foco y más larga cuanto más bajo
 * está. Se usa con `sombraArrojada` dentro de una `capaSombra`. [fuerza] y [color]: los de la sombra.
 */
fun rayoDeFoco(f: Foco, pie: Offset, fuerza: Float, color: Color): Rayo {
    val alto = (f.suelo - f.p.y).coerceAtLeast(12f)
    val ax = ((pie.x - f.p.x) / alto).coerceIn(-2.4f, 2.4f); val ay = ((pie.y - f.suelo) / alto).coerceIn(-1.2f, 1.2f)
    val lat = if (pie.x >= f.p.x) -1f else 1f
    return Rayo(dia = 1f, lat = lat, cot = abs(ax), tan = 1f / abs(ax).coerceAtLeast(0.05f), ax = ax, ay = ay, fuerza = fuerza.coerceIn(0f, 1f), color = color, contraste = 0.8f)
}

/**
 * Un tubo de neón que sigue el [trazo]. [encendido], de 0 (el vidrio apagado, gris) a 1: el halo ancho de su [color],
 * el tubo y, dentro, el filo casi blanco.
 */
fun DrawScope.tuboNeon(trazo: Path, color: Color, encendido: Float, grueso: Float = 1.6f) {
    val k = encendido.coerceIn(0f, 1f)
    val redondo = Stroke(grueso, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(trazo, lerp(Color(0xFF4A4660), color, 0.2f), style = redondo)
    drawPath(trazo, Color(0xFF8A86A0).copy(alpha = 0.35f), style = Stroke(grueso * 0.3f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    if (k <= 0.02f) return
    drawPath(trazo, color.copy(alpha = 0.16f * k), style = Stroke(grueso * 5.2f, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = BlendMode.Plus)
    drawPath(trazo, color.copy(alpha = 0.34f * k), style = Stroke(grueso * 2.6f, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = BlendMode.Plus)
    drawPath(trazo, lerp(Color(0xFF4A4660), color, k), style = redondo)
    drawPath(trazo, lerp(color, Color.White, 0.72f).copy(alpha = k), style = Stroke(grueso * 0.42f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/**
 * El reflejo de una luz en un suelo mojado: una columna de trazos tendidos que baja desde [pie], [alto] hacia quien
 * mira, de [ancho] de lado; se rompe y se abre al alejarse de la luz.
 */
fun DrawScope.reflejoMojado(pie: Offset, alto: Float, ancho: Float, color: Color, fuerza: Float, azar: Azar, paso: Float = 1.5f) {
    if (fuerza <= 0.02f || alto <= 1f) return
    var y = pie.y + 0.6f
    while (y < pie.y + alto) {
        val k = (y - pie.y) / alto
        val w = ancho * (0.5f + 0.5f * azar.real()) * (1f + 0.5f * k); val corre = (azar.real() - 0.5f) * ancho * (0.25f + 0.9f * k)
        val alfa = fuerza * (1f - k).pow(1.4f) * (0.55f + 0.45f * azar.real())
        if (azar.real() > 0.28f * k) drawLine(color.copy(alpha = alfa.coerceIn(0f, 1f)), Offset(pie.x - w / 2f + corre, y), Offset(pie.x + w / 2f + corre, y), paso * (0.5f + 0.25f * k), StrokeCap.Round, blendMode = BlendMode.Screen)
        y += paso * (0.9f + 1.5f * k)
    }
}

/** Un óvalo de luz difusa en el aire (la bruma alrededor de un letrero). Más grande y más fuerte con niebla. */
fun DrawScope.haloDeBruma(centro: Offset, rx: Float, ry: Float, color: Color, fuerza: Float) {
    if (fuerza <= 0.01f) return
    scale(rx / ry, 1f, centro) {
        drawCircle(Brush.radialGradient(listOf(color.copy(alpha = (0.36f * fuerza).coerceIn(0f, 1f)), color.copy(alpha = (0.12f * fuerza).coerceIn(0f, 1f)), Color.Transparent), centro, ry), ry, centro, blendMode = BlendMode.Plus)
    }
}
