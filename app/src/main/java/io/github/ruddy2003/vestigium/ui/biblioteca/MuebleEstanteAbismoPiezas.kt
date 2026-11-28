package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tres cosas grandes del pañol para los huecos de Abismo ([HuecosEstanteAbismo]): el cubo de lona de la cosecha, con su asa de
 * cabo y las esponjas que asoman; la lata de café de hojalata pintada, con sus aros y su tapa; y el cabo adujado y tumbado en la
 * tabla, con el chicote y su guardacabo de hierro colgando por el canto. Cada una con su luz, su sombra en el fondo y en la tabla.
 */
internal object PiezasHuecoAbismo {
    private val P = PaletaAbismo

    /** De qué lado le llega la luz a algo en [p]: de día, de la izquierda (1 = la luz a la izquierda, sombra a la derecha). */
    private fun lado(a: AireAbismo, p: Offset, w: Float): Float = if (!a.oscuro) 1f else if (p.x / w < MuebleEstanteAbismo.FAROL_X) -1f else 1f

    /** La sombra de algo de pie en el fondo del hueco (corrida a la derecha) y en la tabla. */
    private fun DrawScope.sombras(pie: Offset, ancho: Float, alto: Float, a: AireAbismo, k: Float) {
        val w = size.width
        val dx = if (a.oscuro) 0f else w * 0.014f
        if (!a.oscuro || k > 0.2f) drawRect(a.sombra(if (a.oscuro) 0.25f * k else 0.16f), Offset(pie.x - ancho * 0.4f + dx, pie.y - alto * 0.92f), Size(ancho * 0.85f, alto * 0.92f))
        contactoAbismo(Offset(pie.x + w * 0.006f, pie.y - 1f), ancho * 1.25f, w * 0.014f, a, 0.4f)
    }

    /**
     * El cubo de lona encerada de la cosecha: el cuerpo algo más ancho arriba, con el aro de cabo del borde, las costuras y las
     * arrugas de la lona; el asa de cabo en arco; y las esponjas que asoman por encima del borde.
     */
    fun DrawScope.cuboLona(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.3f))
        val hacia = lado(a, pie, w)
        val alto = h * 0.44f; val arr = w * 0.064f; val abj = w * 0.052f
        val top = pie.y - alto
        sombras(pie, arr * 2, h * 0.62f, a, k)
        // Las esponjas que asoman (detrás del borde de delante).
        listOf(Triple(-0.45f, 0.07f, P.ESPONJA), Triple(0.2f, 0.09f, P.ESPONJA_OSC), Triple(0.55f, 0.06f, mezcla(P.ESPONJA, P.CORCHO, 0.5f))).forEachIndexed { j, (fx, fy, col) ->
            with(MuroEstanteAbismo) { esponjaAbismo(Offset(pie.x + arr * fx, top - h * fy), arr * 0.55f, col, a, k, 8600 + j) }
        }
        // El asa de cabo en arco, de un lado al otro.
        val asa = Path().apply { moveTo(pie.x - arr * 0.95f, top + w * 0.006f); cubicTo(pie.x - arr * 0.9f, top - h * 0.36f, pie.x + arr * 0.9f, top - h * 0.36f, pie.x + arr * 0.95f, top + w * 0.006f) }
        drawPath(asa, a.dia(P.MANILA_OSC, 0f, k), style = Stroke(2.8f * u, cap = StrokeCap.Round))
        drawPath(asa, a.dia(P.MANILA, 0.05f, k), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        // El cuerpo de lona.
        val cuerpo = poligonoAbismo(pie.x - arr, top, pie.x + arr, top, pie.x + abj, pie.y, pie.x - abj, pie.y)
        drawPath(cuerpo, a.dia(P.LONA, -0.05f, k))
        clipPath(cuerpo) {
            cilindroAbismo(Rect(pie.x - arr, top, pie.x + arr, pie.y), a, k, -hacia, 1.3f)
            // Las costuras y las arrugas de la lona.
            listOf(-0.5f, 0.15f).forEach { f -> drawLine(a.dia(P.LONA, -0.35f, k), Offset(pie.x + arr * f, top), Offset(pie.x + abj * f, pie.y), 1f * u) }
            for (j in 0 until 3) {
                val y = top + alto * (0.3f + 0.22f * j)
                drawLine(a.dia(P.LONA, -0.25f, k), Offset(pie.x - arr * (0.6f - 0.2f * j), y), Offset(pie.x - arr * (0.2f - 0.2f * j), y + w * 0.006f), 1f * u)
            }
            drawRect(a.dia(P.LONA, -0.3f, k), Offset(pie.x - arr, pie.y - w * 0.008f), Size(arr * 2, w * 0.008f))
        }
        // El aro de cabo del borde, con sus vueltas de piola.
        drawRoundRect(a.dia(P.MANILA, 0f, k), Offset(pie.x - arr - w * 0.004f, top - w * 0.007f), Size(arr * 2 + w * 0.008f, w * 0.014f), CornerRadius(w * 0.007f))
        drawRect(a.brillo(0.45f, k), Offset(pie.x - arr, top - w * 0.006f), Size(arr * 2, 1f * u))
        var x = pie.x - arr + w * 0.006f
        while (x < pie.x + arr) { drawLine(a.dia(P.MANILA_OSC, 0f, k), Offset(x, top - w * 0.007f), Offset(x + w * 0.004f, top + w * 0.007f), 0.8f * u); x += w * 0.01f }
        listOf(-0.95f, 0.95f).forEach { f -> drawCircle(a.dia(P.LATON, 0f, k), 2f * u, Offset(pie.x + arr * f, top + w * 0.006f), style = Stroke(1.3f * u)) }
    }

    /** La lata de café de hojalata pintada de minio, con sus aros de latón, la tapa con su botón, algún golpe y el desgaste. */
    fun DrawScope.lataCafe(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.3f))
        val hacia = lado(a, pie, w)
        val alto = h * 0.52f; val an = w * 0.076f
        sombras(pie, an, alto, a, k)
        val r = Rect(pie.x - an / 2, pie.y - alto, pie.x + an / 2, pie.y)
        drawRect(a.dia(P.MINIO, -0.05f, k), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            cilindroAbismo(r, a, k, -hacia, 1.3f)
            // El desgaste: la hojalata que asoma en los cantos y un golpe.
            for (j in 0 until 4) {
                val p = Offset(r.left + r.width * azar(8610, j), r.top + r.height * (0.2f + 0.7f * azar(8611, j)))
                drawOval(a.dia(P.HOJALATA, 0f, k).copy(alpha = 0.6f), p, Size(w * (0.006f + 0.01f * azar(8612, j)), w * 0.004f))
            }
            drawOval(a.sombra(0.3f), Offset(r.left + r.width * 0.55f, r.top + r.height * 0.55f), Size(r.width * 0.25f, r.height * 0.12f))
            drawArc(a.brillo(0.4f, k), 200f, 120f, false, Offset(r.left + r.width * 0.55f, r.top + r.height * 0.55f), Size(r.width * 0.25f, r.height * 0.12f), style = Stroke(0.8f * u))
        }
        // Los aros de latón (sin letra ni dibujo).
        listOf(0.18f, 0.82f).forEach { f ->
            val y = r.top + r.height * f
            drawRect(a.dia(P.LATON, 0f, k), Offset(r.left, y - w * 0.005f), Size(r.width, w * 0.01f))
            drawRect(a.brillo(0.5f, k), Offset(r.left, y - w * 0.005f), Size(r.width, 1f * u))
        }
        // La tapa, algo más ancha, con su botón.
        val tp = Rect(r.left - w * 0.004f, r.top - w * 0.014f, r.right + w * 0.004f, r.top + w * 0.004f)
        drawRect(a.dia(P.MINIO, 0.05f, k), tp.topLeft, tp.size)
        drawOval(a.dia(P.MINIO, 0.18f, k), Offset(tp.left, tp.top - w * 0.006f), Size(tp.width, w * 0.012f))
        drawRect(a.sombra(0.35f), Offset(tp.left, tp.bottom - 1f * u), Size(tp.width, 1f * u))
        drawOval(a.dia(P.LATON, 0f, k), Offset(pie.x - w * 0.009f, tp.top - w * 0.012f), Size(w * 0.018f, w * 0.01f))
        destelloAbismo(Offset(pie.x - hacia * an * 0.25f, r.top + r.height * 0.35f), 1.1f * u, a, k)
    }

    /**
     * El cabo adujado y tumbado en la tabla: una pila baja de vueltas vistas en elipse, cada una con su luz arriba y su sombra abajo,
     * el colchado, el hueco del centro y el chicote que sale de la vuelta de arriba hacia el canto (lo que cuelga, en [chicote]).
     */
    fun DrawScope.rolloTumbado(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - h * 0.1f))
        val rx = w * 0.078f; val ry = w * 0.026f
        val g = h * 0.042f
        contactoAbismo(Offset(pie.x + w * 0.008f, pie.y - 1f), rx * 2.3f, w * 0.02f, a, 0.45f)
        if (!a.oscuro) drawOval(a.sombra(0.14f), Offset(pie.x - rx + w * 0.018f, pie.y - g * 5f - ry), Size(rx * 2, g * 4f + ry * 2))
        // Las vueltas, de abajo arriba.
        val n = 4
        for (j in 0 until n) {
            val cy = pie.y - ry - g * 0.6f - j * g * 0.95f
            val ww = rx * (1f - 0.04f * j)
            val ov = Rect(pie.x - ww + j * w * 0.002f, cy - ry, pie.x + ww + j * w * 0.002f, cy + ry)
            drawOval(a.sombra(0.35f), Offset(ov.left, ov.top + g * 0.5f), ov.size, style = Stroke(g))
            drawOval(a.dia(P.MANILA_OSC, -0.05f, k), ov.topLeft, ov.size, style = Stroke(g))
            drawArc(a.dia(if (j % 2 == 0) P.MANILA else mezcla(P.MANILA, P.MANILA_OSC, 0.25f), 0f, k), 0f, 180f, false, ov.topLeft, ov.size, style = Stroke(g * 0.65f))
            drawArc(a.brillo(0.4f, k), 200f, 120f, false, Offset(ov.left, ov.top - g * 0.3f), ov.size, style = Stroke(g * 0.2f))
            // El colchado en la cara de delante de la vuelta.
            val tic = Path()
            for (i in 0 until 16) {
                val t = Math.PI * (i + 0.5) / 16
                val p = Offset(ov.center.x + ww * cos(t).toFloat(), cy + ry * sin(t).toFloat())
                tic.moveTo(p.x - g * 0.22f, p.y - g * 0.28f); tic.lineTo(p.x + g * 0.22f, p.y + g * 0.28f)
            }
            drawPath(tic, a.dia(P.MANILA_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        }
        // El hueco del centro, visto desde arriba, y el chicote que sale hacia el canto.
        val top = pie.y - ry - g * 0.6f - (n - 1) * g * 0.95f
        drawOval(a.sombra(0.5f), Offset(pie.x - rx * 0.55f, top - ry * 0.5f), Size(rx * 1.1f, ry))
        val ch = Path().apply { moveTo(pie.x + rx * 0.5f, top + ry * 0.6f); cubicTo(pie.x + rx * 0.7f, top + ry * 2f, pie.x + rx * 0.45f, pie.y - g * 0.4f, pie.x + rx * 0.3f, pie.y + 1f * u) }
        drawPath(ch, a.dia(P.MANILA_OSC, 0f, k), style = Stroke(g * 0.95f, cap = StrokeCap.Round))
        drawPath(ch, a.dia(P.MANILA, 0.05f, k), style = Stroke(g * 0.6f, cap = StrokeCap.Round))
    }

    /** El chicote del cabo adujado en [pie] que cuelga por el canto de la tabla, con su gaza y el guardacabo de hierro. */
    fun DrawScope.chicote(pie: Offset, h: Float, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y + w * 0.02f))
        val rx = w * 0.078f
        val g = h * 0.042f
        val p0 = Offset(pie.x + rx * 0.3f, pie.y - 1f * u)
        val p1 = Offset(p0.x + w * 0.006f, pie.y + w * 0.035f)
        if (!a.oscuro) drawLine(a.sombra(0.3f), p0 + Offset(2f * u, 2f * u), p1 + Offset(2f * u, 2f * u), g * 0.9f)
        drawLine(a.dia(P.MANILA_OSC, 0f, k), p0, p1, g * 0.95f, StrokeCap.Round)
        drawLine(a.dia(P.MANILA, 0.05f, k), p0, p1, g * 0.6f, StrokeCap.Round)
        val gz = Offset(p1.x, p1.y + w * 0.012f)
        val gu = Path().apply {
            moveTo(gz.x, gz.y - w * 0.012f)
            cubicTo(gz.x + w * 0.015f, gz.y - w * 0.008f, gz.x + w * 0.013f, gz.y + w * 0.013f, gz.x, gz.y + w * 0.015f)
            cubicTo(gz.x - w * 0.013f, gz.y + w * 0.013f, gz.x - w * 0.015f, gz.y - w * 0.008f, gz.x, gz.y - w * 0.012f)
        }
        drawPath(corridoAbismo(gu, 1.5f * u, 2f * u), a.sombra(0.4f), style = Stroke(2.6f * u))
        drawPath(gu, a.dia(P.HIERRO, 0f, k), style = Stroke(2.6f * u))
        drawPath(gu, a.brillo(0.45f, k), style = Stroke(0.8f * u))
    }
}
