package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance

/**
 * Los muebles sin diseño: rectángulos, líneas y círculos con los colores del esquema. Los usa el tema Básico y es lo que
 * queda si un tema todavía no tiene los suyos. Claros y baratos.
 */
object VidaPlana : DisenoVida {
    private fun DrawScope.u() = size.minDimension / 100f

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        // Una mesa redonda vista desde arriba: el tablero tenue y un anillo de sillas alrededor.
        val c = Offset(w / 2, h / 2)
        val rx = w * 0.40f; val ry = h * 0.36f
        drawOval(e.acento.copy(alpha = 0.06f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        drawOval(e.acento.copy(alpha = 0.30f), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2), style = Stroke(1.2f * u))
        repeat(10) { i ->
            val a = i / 10.0 * 2 * Math.PI
            val p = Offset(c.x + (rx + 5f * u) * kotlin.math.cos(a).toFloat(), c.y + (ry + 5f * u) * kotlin.math.sin(a).toFloat())
            drawRoundRect(e.tinta.copy(alpha = 0.18f), Offset(p.x - 3f * u, p.y - 3f * u), Size(6f * u, 6f * u), CornerRadius(1.5f * u))
        }
    }

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        // Una mesa: el tablero con su borde y dos patas que asoman abajo.
        val borde = 3f * u
        drawRoundRect(e.tinta.copy(alpha = 0.10f), Offset(borde, borde), Size(w - 2 * borde, h - 2 * borde), CornerRadius(2f * u))
        drawRoundRect(e.tinta.copy(alpha = 0.30f), Offset(borde, borde), Size(w - 2 * borde, h - 2 * borde), CornerRadius(2f * u), style = Stroke(1f * u))
        listOf(0.12f, 0.88f).forEach { x -> drawLine(e.tinta.copy(alpha = 0.25f), Offset(w * x, h - borde), Offset(w * x, h), 2f * u, StrokeCap.Round) }
    }

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        // Una repisa con cinco huecos.
        val y = h * 0.82f
        drawLine(e.tinta.copy(alpha = 0.35f), Offset(w * 0.04f, y), Offset(w * 0.96f, y), 1.6f * u, StrokeCap.Round)
        val paso = w * 0.92f / 5f
        repeat(5) { i ->
            val x = w * 0.04f + paso * i + paso * 0.12f
            drawRoundRect(e.acento.copy(alpha = 0.06f), Offset(x, h * 0.12f), Size(paso * 0.76f, y - h * 0.14f), CornerRadius(3f * u))
        }
    }

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        val w = size.width; val h = size.height; val c = Offset(w / 2, h / 2); val r = minOf(w, h) * 0.36f
        val trazo = Stroke(1.8f * density, cap = StrokeCap.Round)
        when (id) {
            "unesco" -> {
                // Un frontón sobre tres columnas.
                val p = Path().apply { moveTo(c.x - r, c.y - r * 0.35f); lineTo(c.x, c.y - r); lineTo(c.x + r, c.y - r * 0.35f); close() }
                drawPath(p, e.c1, style = trazo)
                listOf(-0.7f, 0f, 0.7f).forEach { k -> drawLine(e.c1, Offset(c.x + k * r, c.y - r * 0.25f), Offset(c.x + k * r, c.y + r * 0.7f), 1.8f * density) }
                drawLine(e.c2, Offset(c.x - r * 1.1f, c.y + r * 0.8f), Offset(c.x + r * 1.1f, c.y + r * 0.8f), 2.2f * density)
            }
            "rascar" -> {
                // Un rectángulo con una esquina descubierta.
                drawRoundRect(e.c2.copy(alpha = 0.35f), Offset(c.x - r * 1.3f, c.y - r * 0.8f), Size(r * 2.6f, r * 1.6f), CornerRadius(4f * density))
                val p = Path().apply { moveTo(c.x + r * 0.2f, c.y - r * 0.8f); lineTo(c.x + r * 1.3f, c.y - r * 0.8f); lineTo(c.x + r * 1.3f, c.y + r * 0.3f); close() }
                drawPath(p, e.c1)
                drawRoundRect(e.c1, Offset(c.x - r * 1.3f, c.y - r * 0.8f), Size(r * 2.6f, r * 1.6f), CornerRadius(4f * density), style = trazo)
            }
            else -> {
                // Tres marcadores redondos.
                listOf(Offset(-0.8f, 0.3f), Offset(0f, -0.4f), Offset(0.8f, 0.2f)).forEach { o ->
                    val p = Offset(c.x + o.x * r, c.y + o.y * r)
                    drawCircle(e.c1, r * 0.28f, p)
                    drawCircle(e.fondo, r * 0.11f, p)
                }
            }
        }
    }

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        val w = size.width; val h = size.height; val u = u()
        drawRoundRect(e.tinta.copy(alpha = 0.06f), Offset.Zero, size, CornerRadius(3f * u))
        drawRoundRect(e.tinta.copy(alpha = 0.22f), Offset.Zero, size, CornerRadius(3f * u), style = Stroke(1f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        val papel = if (oscuro) Color(0xFF2A2D33) else Color(0xFFFFFFFF)
        drawRoundRect(papel, r.topLeft, r.size, CornerRadius(4f * density))
        drawRoundRect(if (e.fijada) e.acento else e.acento.copy(alpha = 0.35f), r.topLeft, r.size, CornerRadius(4f * density), style = Stroke(if (e.fijada) 1.6f * density else 1f * density))
        val marca = when (e.tipo) { "idea" -> e.acento; "proposito" -> Color(0xFF2E7D32); "recordatorio" -> Color(0xFFE0A100); else -> e.acento.copy(alpha = 0.5f) }
        drawRect(marca, r.topLeft, Size(r.width, 3f * density))
    }

    override fun tintaNota(oscuro: Boolean): Color = if (oscuro) Color(0xFFE6E8EB) else Color(0xFF1F2226)

    /** Si un color es oscuro (para elegir la tinta sobre él). */
    fun oscuroDe(c: Color) = c.luminance() < 0.5f
}
