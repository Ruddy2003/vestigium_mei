package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Las texturas de tela de Espacio (el libro del día y la libreta): baldosas pintadas una sola vez y repetidas con un pincel de
 * imagen, así una superficie grande lleva su trama en un solo dibujo (los miles de hilos sueltos pesaban demasiado al pintar).
 * Son blancas y negras con transparencia: sirven sobre cualquier color y con cualquier luz.
 */
internal object TelasEspacio {
    /** La trama de la lona: hilos de urdimbre y de trama que se cruzan, alguno más grueso (los nudos del hilo) y motas. */
    private val lona: ImageBitmap by lazy {
        val lado = 48
        val img = ImageBitmap(lado, lado)
        CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(lado.toFloat(), lado.toFloat())) {
            val r = java.util.Random(11)
            for (x in 0 until lado step 3) {
                val a = 0.16f + 0.14f * r.nextFloat()
                drawLine(Color.White.copy(alpha = a), Offset(x + 0.5f, 0f), Offset(x + 0.5f, lado.toFloat()), 1f)
                drawLine(Color.Black.copy(alpha = a * 0.8f), Offset(x + 1.5f, 0f), Offset(x + 1.5f, lado.toFloat()), 1f)
            }
            for (y in 0 until lado step 4) {
                val a = 0.12f + 0.12f * r.nextFloat()
                drawLine(Color.Black.copy(alpha = a), Offset(0f, y + 0.5f), Offset(lado.toFloat(), y + 0.5f), 1f)
                drawLine(Color.White.copy(alpha = a * 0.7f), Offset(0f, y + 2f), Offset(lado.toFloat(), y + 2f), 1f)
            }
            // Los nudos del hilo: tramos cortos más gruesos y claros, y unas motas oscuras.
            repeat(7) {
                val x = r.nextFloat() * lado; val y = r.nextFloat() * lado
                drawLine(Color.White.copy(alpha = 0.3f), Offset(x, y), Offset(x + 5f + 6f * r.nextFloat(), y), 1.6f)
            }
            repeat(10) { drawCircle(Color.Black.copy(alpha = 0.25f), 0.7f, Offset(r.nextFloat() * lado, r.nextFloat() * lado)) }
        }
        img
    }
    private val pincelLona by lazy { ShaderBrush(ImageShader(lona, TileMode.Repeated, TileMode.Repeated)) }

    /** La trama de la lona sobre [r], con [alfa]. */
    fun DrawScope.trama(r: Rect, alfa: Float) = drawRect(pincelLona, r.topLeft, r.size, alpha = alfa.coerceIn(0f, 1f))

    /** La trama de la lona dentro de [p], con [alfa]. */
    fun DrawScope.trama(p: Path, alfa: Float) = drawPath(p, pincelLona, alpha = alfa.coerceIn(0f, 1f))
}

/**
 * Las puntadas a mano a lo largo de los cuatro lados de [r], en [p] (que se vacía antes): largos y huecos desparejos y la
 * línea que se tuerce un poco, siempre igual para la misma [semilla]. Un solo camino, para un solo trazo.
 */
internal fun puntadasEspacio(p: Path, r: Rect, semilla: Int, u: Float): Path {
    p.reset()
    var k = 0
    fun lado(x0: Float, y0: Float, x1: Float, y1: Float) {
        val largo = kotlin.math.hypot(x1 - x0, y1 - y0)
        if (largo < 2f) return
        val dx = (x1 - x0) / largo; val dy = (y1 - y0) / largo
        var t = 0f
        while (t < largo - 1f) {
            val l = (2.1f + 1.2f * io.github.ruddy2003.vestigium.ui.ideario.azar(semilla, 300 + k)) * u
            val j = (io.github.ruddy2003.vestigium.ui.ideario.azar(semilla, 600 + k) - 0.5f) * 0.7f * u
            val t1 = minOf(largo, t + l)
            p.moveTo(x0 + dx * t - dy * j, y0 + dy * t + dx * j)
            p.lineTo(x0 + dx * t1 - dy * j, y0 + dy * t1 + dx * j)
            t = t1 + maxOf(0.5f, (1.4f + 1f * io.github.ruddy2003.vestigium.ui.ideario.azar(semilla, 900 + k)) * u)
            k++
        }
    }
    lado(r.left, r.top, r.right, r.top); lado(r.right, r.top, r.right, r.bottom)
    lado(r.right, r.bottom, r.left, r.bottom); lado(r.left, r.bottom, r.left, r.top)
    return p
}
