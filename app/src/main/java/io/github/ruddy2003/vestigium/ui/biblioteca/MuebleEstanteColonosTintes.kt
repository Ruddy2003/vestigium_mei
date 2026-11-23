package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.juegoTonos
import kotlin.math.roundToInt

/*
 * El ánimo de las entradas de la alacena de Colonos: su color de día, los tintes del pueblo con que se pinta, y los estampados
 * de la tela de costal de los fardos.
 */

/** Las escalas del ánimo de Colonos, la de día y la de noche (bajo, medio, alto). */
private val TONOS_COLONOS: Pair<List<Color>, List<Color>> by lazy { juegoTonos(TemaId.COLONOS, false) to juegoTonos(TemaId.COLONOS, true) }

private fun cuadradoColonos(x: Float) = x * x


/** Dónde cae un color en una escala (de 0, bajo, a 1, alto): el tramo y la fracción que más se le acercan; si no está, null. */
private fun enEscalaColonos(t: Color, e: List<Color>): Float? {
    if (e.size < 3) return null
    var mejor: Float? = null
    var error = 0.03f
    for (seg in 0 until 2) {
        val a0 = e[seg]; val a1 = e[seg + 1]
        val dr = a1.red - a0.red; val dg = a1.green - a0.green; val db = a1.blue - a0.blue
        val largo = dr * dr + dg * dg + db * db
        if (largo < 1e-5f) continue
        val f = (((t.red - a0.red) * dr + (t.green - a0.green) * dg + (t.blue - a0.blue) * db) / largo).coerceIn(0f, 1f)
        val er = cuadradoColonos(t.red - a0.red - dr * f) + cuadradoColonos(t.green - a0.green - dg * f) + cuadradoColonos(t.blue - a0.blue - db * f)
        if (er < error) { error = er; mejor = (seg + f) / 2f }
    }
    return mejor
}

private fun puntoEscalaColonos(e: List<Color>, x: Float): Color = if (x < 0.5f) mezcla(e[0], e[1], x * 2) else mezcla(e[1], e[2], (x - 0.5f) * 2)

/**
 * El color del ánimo tal como es de día. En el modo oscuro la escala del tema cambia de colores (el de en medio pasa del crema
 * a un casi negro): aquí la entrada es un objeto del cuarto, así que se ubica el color en la escala de noche y se toma el mismo
 * punto en la de día. Si no está en la escala, queda igual.
 */
internal fun tonoDeDiaColonos(t: Color?, oscuro: Boolean): Color? {
    if (t == null || !oscuro) return t
    val (dia, noche) = TONOS_COLONOS
    val x = enEscalaColonos(t, noche) ?: return t
    return puntoEscalaColonos(dia, x)
}

/**
 * Las tintas, pinturas y tintes del pueblo con que se dice el ánimo, en el orden de la escala del tema (de lo peor a lo mejor):
 * ladrillo, calabaza, el crema de en medio, añil y marino. Así los puntos de en medio son un color con cuerpo (la calabaza, el
 * añil) y no un rosa o un gris lavados; los extremos y el crema son los del tema.
 */
private val TINTES_COLONOS = listOf(Color(0xFF9E2B25), Color(0xFFC9662C), Color(0xFFE7DDC8), Color(0xFF3F6EA6), Color(0xFF1F3A5F))

/** El tinte que corresponde a un color del ánimo (ya de día): su punto en la escala del tema, llevado a [TINTES_COLONOS]. */
internal fun tenidoColonos(t: Color?): Color? {
    if (t == null) return null
    val x = enEscalaColonos(t, TONOS_COLONOS.first) ?: return t
    val q = x.coerceIn(0f, 1f) * (TINTES_COLONOS.size - 1)
    val i = q.toInt().coerceAtMost(TINTES_COLONOS.size - 2)
    return mezcla(TINTES_COLONOS[i], TINTES_COLONOS[i + 1], q - i)
}

/**
 * Los estampados de la tela de costal, en blanco (se tiñen al pintarlos), una baldosa por densidad: florecitas de cinco puntos
 * en tresbolillo, lunares, el cotí de rayas a lo alto y ondas a lo ancho. Cada fardo lleva uno según su semilla.
 */
internal object TelaColonos {
    private var densidad = 0f
    private val pinceles = arrayOfNulls<ShaderBrush>(4)
    fun pincel(cual: Int, u: Float): ShaderBrush {
        if (u != densidad) { for (i in 0 until 4) pinceles[i] = null; densidad = u }
        val i = Math.floorMod(cual, 4)
        return pinceles[i] ?: baldosa(i, u).also { pinceles[i] = it }
    }
    private fun baldosa(cual: Int, u: Float): ShaderBrush {
        val lado = ((if (cual == 2) 5f else 9f) * u).roundToInt().coerceAtLeast(5)
        val l = lado.toFloat()
        val img = ImageBitmap(lado, lado)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(l, l)) {
            val b = Color.White
            when (cual) {
                0 -> {
                    val rp = 0.55f * u
                    for (c in listOf(Offset(l * 0.25f, l * 0.25f), Offset(l * 0.75f, l * 0.75f))) for (j in 0 until 5) {
                        val ang = j * 1.2566f - 1.5708f
                        drawCircle(b, rp, Offset(c.x + rp * 1.5f * kotlin.math.cos(ang), c.y + rp * 1.5f * kotlin.math.sin(ang)))
                    }
                }
                1 -> {
                    drawCircle(b, 1.1f * u, Offset(l * 0.25f, l * 0.25f))
                    drawCircle(b, 1.1f * u, Offset(l * 0.75f, l * 0.75f))
                }
                2 -> {
                    drawRect(b, Offset(l * 0.1f, 0f), Size(l * 0.22f, l))
                    drawRect(b.copy(alpha = 0.5f), Offset(l * 0.55f, 0f), Size(l * 0.08f, l))
                }
                else -> {
                    // Ondas a lo ancho, una gruesa y una fina.
                    for ((yy, gr) in listOf(l * 0.3f to 0.9f, l * 0.78f to 0.5f)) {
                        val onda = Path().apply { moveTo(0f, yy); cubicTo(l * 0.25f, yy - l * 0.12f, l * 0.25f, yy - l * 0.12f, l * 0.5f, yy); cubicTo(l * 0.75f, yy + l * 0.12f, l * 0.75f, yy + l * 0.12f, l, yy) }
                        drawPath(onda, b, style = Stroke(gr * u))
                    }
                }
            }
            // El hilo de la trama, fino.
            drawLine(b.copy(alpha = 0.12f), Offset(0f, l * 0.5f), Offset(l, l * 0.5f), 0.5f * u)
            drawLine(b.copy(alpha = 0.08f), Offset(l * 0.5f, 0f), Offset(l * 0.5f, l), 0.5f * u)
        }
        return ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated))
    }
}
