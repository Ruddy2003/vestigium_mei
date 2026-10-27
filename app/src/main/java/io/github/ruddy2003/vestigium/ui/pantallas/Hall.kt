@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Logros
import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonPrincipal
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.LibroDiario
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.SalonesTema
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Vacio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal val METALES_HALL = listOf(Color(0xFFB27A45), Color(0xFFBFC6CF), Color(0xFFE3B748), Color(0xFF9AD8E6))

/** Familias de temas para el salón: cada una con su arquitectura. */
enum class Salon { TEMPLO, CASTILLO, LABORATORIO, TALLER }

fun salonDe(t: TemaId): Salon = when (t) {
    TemaId.ROMANO, TemaId.HELENO, TemaId.ANDALUS, TemaId.GOTICO, TemaId.RENACIMIENTO, TemaId.REVOLUCION -> Salon.TEMPLO
    TemaId.MEDIEVAL, TemaId.ORDEN, TemaId.MONGOL, TemaId.NAPOLEON, TemaId.ANTIGUAS -> Salon.CASTILLO
    TemaId.CIBER, TemaId.DISTOPIA, TemaId.REFUGIO, TemaId.LABORATORIO, TemaId.ESPACIO -> Salon.LABORATORIO
    TemaId.COLONOS, TemaId.BOTANICO, TemaId.SEDA, TemaId.NAVIDAD, TemaId.URSS -> Salon.TALLER
}

/**
 * Cada logro, dibujado según lo que celebra: un horizonte de ciudad, un sello de pasaporte con bandera, un globo,
 * un templo del patrimonio, una brújula, huellas, una pesa, una montaña, un libro abierto, una luna, un sobre lacrado…
 * Los techos de la ciudad, la forma del sello y el templo cambian con el tema.
 */
object PiezasLogro {
    private fun pre(id: String) = id.substringBeforeLast('_')

    fun DrawScope.pieza(l: Logros.Logro, tema: TemaId, cs: ColorScheme) {
        val c = Offset(size.width / 2, size.height / 2)
        val r = size.minDimension / 2
        val u = r / 50f
        val metal = METALES_HALL[(l.nivel - 1).coerceIn(0, 3)]
        val hecho = l.hecho
        val tinta = if (hecho) Color(0xFF2A1E0E) else cs.onSurfaceVariant.copy(alpha = 0.6f)
        val luz = if (hecho) metal else cs.surfaceVariant
        // El marco: un disco con el metal del nivel y un borde según el salón.
        drawCircle(Brush.radialGradient(listOf(luz, luz.copy(alpha = 0.75f), luz.copy(alpha = 0.5f)), c, r), r)
        when (salonDe(tema)) {
            Salon.TEMPLO -> for (i in 0 until 24) { val a = i * PI / 12; drawCircle(metal.copy(alpha = if (hecho) 0.9f else 0.3f), 1.4f * u, Offset(c.x + (r * 0.92f * cos(a)).toFloat(), c.y + (r * 0.92f * sin(a)).toFloat())) }
            Salon.CASTILLO -> drawCircle(tinta.copy(alpha = 0.35f), r * 0.9f, c, style = Stroke(2.2f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * u, 3 * u))))
            Salon.LABORATORIO -> { drawCircle(cs.primary.copy(alpha = if (hecho) 0.8f else 0.25f), r * 0.93f, c, style = Stroke(1.6f * u)); drawCircle(cs.primary.copy(alpha = 0.25f), r * 0.86f, c, style = Stroke(0.8f * u)) }
            Salon.TALLER -> drawCircle(tinta.copy(alpha = 0.3f), r * 0.9f, c, style = Stroke(1.2f * u))
        }
        drawCircle(metal, r, c, style = Stroke(3 * u))
        if (l.nivel >= 4 && hecho) for (i in 0 until 16) { val a = i * PI / 8; drawLine(metal, Offset(c.x + (r * 1.02f * cos(a)).toFloat(), c.y + (r * 1.02f * sin(a)).toFloat()), Offset(c.x + (r * 1.12f * cos(a)).toFloat(), c.y + (r * 1.12f * sin(a)).toFloat()), 2 * u) }
        if (l.secreto && !hecho) return
        val p = pre(l.id)
        val tono = if (hecho) cs.primary else tinta
        when {
            l.id.startsWith("ciudades") -> ciudad(c, u, tema, tinta, tono, l.nivel)
            l.id.startsWith("paises") -> sello(c, u, tema, tinta, cs)
            l.id.startsWith("continentes") -> globo(c, u, tinta, tono)
            l.id.startsWith("unesco") -> templo(c, u, tema, tinta)
            l.id.startsWith("lejos") -> brujula(c, u, tinta, tono)
            p == "pasos_dia" || p == "pasos_total" || l.id.startsWith("c_pasos") || l.id == "maraton" -> huellas(c, u, tinta)
            l.id.startsWith("entrenos") || l.id.startsWith("volumen") || l.id.startsWith("c_gym") -> pesa(c, u, tinta)
            l.id.startsWith("escalada") -> montana(c, u, tinta, tono)
            l.id.startsWith("diario") || l.id.startsWith("palabras") || l.id.startsWith("racha_diario") || l.id.startsWith("c_diario") -> libro(c, u, tinta, tono)
            l.id.startsWith("suenos") || l.id == "lucido" || l.id.startsWith("c_sueno") -> luna(c, u, tinta)
            l.id.startsWith("carta") -> sobre(c, u, tinta, tono)
            l.id.startsWith("momentos") || l.id.startsWith("avisos") || l.id.startsWith("enfoque") -> reloj(c, u, tinta)
            l.id.startsWith("reaccion") || l.id.startsWith("c_reac") -> rayo(c, u, tinta, tono)
            l.id == "brujula" -> brujula(c, u, tinta, tono)
            l.id.startsWith("obras") || l.id.startsWith("libros") || l.id.startsWith("subrayados") -> libros(c, u, tinta, tono)
            l.id.startsWith("frases") -> comillas(c, u, tinta)
            l.id.startsWith("personas") -> gente(c, u, tinta, tono)
            l.id.startsWith("c_hab") -> calendario(c, u, tinta, tono)
            l.id.startsWith("campanas") -> estandarte(c, u, tinta, tono)
            l.id == "madrugador" || l.id == "dia_perfecto" -> sol(c, u, tinta, tono)
            l.id.startsWith("gastos") -> moneda(c, u, tinta)
            else -> signo(l.icono, c, r, tinta)
        }
    }

    private fun DrawScope.signo(s: String, c: Offset, r: Float, tinta: Color) {
        val p = android.graphics.Paint().apply { color = android.graphics.Color.argb((tinta.alpha * 255).toInt(), (tinta.red * 255).toInt(), (tinta.green * 255).toInt(), (tinta.blue * 255).toInt()); textSize = r * 0.9f; textAlign = android.graphics.Paint.Align.CENTER; isAntiAlias = true }
        drawContext.canvas.nativeCanvas.drawText(s, c.x, c.y + r * 0.32f, p)
    }

    private fun DrawScope.ciudad(c: Offset, u: Float, tema: TemaId, tinta: Color, tono: Color, nivel: Int) {
        val base = c.y + 22 * u
        val n = 3 + nivel
        val ancho = 60 * u / n
        for (i in 0 until n) {
            val x = c.x - 30 * u + i * ancho
            val h = (14 + ((i * 37 + nivel * 11) % 22)) * u
            drawRect(tinta, Offset(x + 1 * u, base - h), Size(ancho - 2 * u, h))
            // Techos según el tema.
            when (tema) {
                TemaId.ROMANO, TemaId.HELENO, TemaId.RENACIMIENTO -> drawPath(Path().apply { moveTo(x, base - h); lineTo(x + ancho / 2, base - h - 5 * u); lineTo(x + ancho, base - h); close() }, tinta)
                TemaId.ANDALUS, TemaId.SEDA, TemaId.MONGOL -> drawArc(tinta, 180f, 180f, true, Offset(x + 1 * u, base - h - (ancho - 2 * u) / 2), Size(ancho - 2 * u, ancho - 2 * u))
                TemaId.GOTICO, TemaId.MEDIEVAL, TemaId.ORDEN, TemaId.NAPOLEON -> drawPath(Path().apply { moveTo(x + 1 * u, base - h); lineTo(x + ancho / 2, base - h - 10 * u); lineTo(x + ancho - 1 * u, base - h); close() }, tinta)
                TemaId.CIBER, TemaId.ESPACIO, TemaId.LABORATORIO, TemaId.DISTOPIA, TemaId.REFUGIO -> drawLine(tinta, Offset(x + ancho / 2, base - h), Offset(x + ancho / 2, base - h - 6 * u), 1 * u)
                else -> drawPath(Path().apply { moveTo(x, base - h + 1 * u); lineTo(x + ancho / 2, base - h - 4 * u); lineTo(x + ancho, base - h + 1 * u); close() }, tinta)
            }
            // Ventanas encendidas.
            for (k in 0 until (h / (6 * u)).toInt()) drawRect(tono.copy(alpha = 0.85f), Offset(x + ancho / 2 - 1.2f * u, base - h + 3 * u + k * 6 * u), Size(2.4f * u, 2.4f * u))
        }
        drawLine(tinta, Offset(c.x - 32 * u, base), Offset(c.x + 32 * u, base), 1.6f * u)
    }

    private fun DrawScope.sello(c: Offset, u: Float, tema: TemaId, tinta: Color, cs: ColorScheme) {
        // Sello de pasaporte: la forma cambia con el salón; adentro, una bandera de tres franjas.
        val tinto = tinta.copy(alpha = 0.8f)
        when (salonDe(tema)) {
            Salon.TEMPLO -> drawCircle(tinto, 27 * u, c, style = Stroke(1.6f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * u, 2 * u))))
            Salon.CASTILLO -> drawPath(Path().apply { moveTo(c.x - 26 * u, c.y - 20 * u); lineTo(c.x + 26 * u, c.y - 20 * u); lineTo(c.x + 26 * u, c.y + 8 * u); lineTo(c.x, c.y + 26 * u); lineTo(c.x - 26 * u, c.y + 8 * u); close() }, tinto, style = Stroke(1.6f * u))
            Salon.LABORATORIO -> drawPath(Path().apply { for (i in 0 until 6) { val a = PI / 6 + i * PI / 3; val x = c.x + (27 * u * cos(a)).toFloat(); val y = c.y + (27 * u * sin(a)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }; close() }, tinto, style = Stroke(1.6f * u))
            Salon.TALLER -> drawRoundRect(tinto, Offset(c.x - 28 * u, c.y - 22 * u), Size(56 * u, 44 * u), CornerRadius(5 * u, 5 * u), style = Stroke(1.6f * u))
        }
        val col = listOf(cs.primary, cs.secondary, cs.tertiary)
        drawLine(tinta, Offset(c.x - 14 * u, c.y + 16 * u), Offset(c.x - 14 * u, c.y - 16 * u), 1.6f * u)
        for (i in 0 until 3) drawRect(col[i], Offset(c.x - 13 * u, c.y - 16 * u + i * 5 * u), Size(26 * u, 5 * u))
        drawRect(tinta, Offset(c.x - 13 * u, c.y - 16 * u), Size(26 * u, 15 * u), style = Stroke(0.8f * u))
    }

    private fun DrawScope.globo(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawCircle(tono.copy(alpha = 0.35f), 24 * u, c)
        drawCircle(tinta, 24 * u, c, style = Stroke(1.6f * u))
        listOf(0.4f, 0.8f).forEach { k -> drawOval(tinta, Offset(c.x - 24 * u * k, c.y - 24 * u), Size(48 * u * k, 48 * u), style = Stroke(1f * u)) }
        listOf(-12f, 0f, 12f).forEach { dy -> val w = kotlin.math.sqrt(24f * 24f - dy * dy) * u; drawLine(tinta, Offset(c.x - w, c.y + dy * u), Offset(c.x + w, c.y + dy * u), 1f * u) }
    }

    private fun DrawScope.templo(c: Offset, u: Float, tema: TemaId, tinta: Color) {
        val base = c.y + 20 * u
        when (tema) {
            TemaId.ANDALUS -> { drawRect(tinta, Offset(c.x - 22 * u, base - 30 * u), Size(44 * u, 30 * u)); drawArc(Color.White.copy(alpha = 0.6f), 180f, 180f, true, Offset(c.x - 9 * u, base - 26 * u), Size(18 * u, 18 * u)); drawRect(Color.White.copy(alpha = 0.6f), Offset(c.x - 9 * u, base - 17 * u), Size(18 * u, 17 * u)) }
            TemaId.SEDA, TemaId.MONGOL -> for (k in 0 until 3) { val w = (34 - k * 9) * u; val y = base - k * 11 * u; drawRect(tinta, Offset(c.x - w / 2 + 3 * u, y - 9 * u), Size(w - 6 * u, 9 * u)); drawPath(Path().apply { moveTo(c.x - w / 2 - 3 * u, y - 9 * u); lineTo(c.x + w / 2 + 3 * u, y - 9 * u); lineTo(c.x + w / 2 - 2 * u, y - 13 * u); lineTo(c.x - w / 2 + 2 * u, y - 13 * u); close() }, tinta) }
            TemaId.GOTICO, TemaId.MEDIEVAL -> { drawRect(tinta, Offset(c.x - 16 * u, base - 24 * u), Size(32 * u, 24 * u)); listOf(-12f, 12f).forEach { dx -> drawPath(Path().apply { moveTo(c.x + (dx - 5) * u, base - 24 * u); lineTo(c.x + dx * u, base - 40 * u); lineTo(c.x + (dx + 5) * u, base - 24 * u); close() }, tinta) }; drawCircle(Color.White.copy(alpha = 0.6f), 4 * u, Offset(c.x, base - 15 * u)) }
            else -> {
                drawPath(Path().apply { moveTo(c.x - 26 * u, base - 26 * u); lineTo(c.x, base - 38 * u); lineTo(c.x + 26 * u, base - 26 * u); close() }, tinta)
                for (i in 0 until 5) drawRect(tinta, Offset(c.x - 22 * u + i * 10 * u, base - 24 * u), Size(4 * u, 22 * u))
                drawRect(tinta, Offset(c.x - 28 * u, base - 2 * u), Size(56 * u, 4 * u))
            }
        }
    }

    private fun DrawScope.brujula(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawCircle(tinta, 24 * u, c, style = Stroke(1.4f * u))
        for (i in 0 until 4) { val a = i * PI / 2; drawLine(tinta, Offset(c.x + (20 * u * cos(a)).toFloat(), c.y + (20 * u * sin(a)).toFloat()), Offset(c.x + (24 * u * cos(a)).toFloat(), c.y + (24 * u * sin(a)).toFloat()), 1.4f * u) }
        rotate(30f, c) {
            drawPath(Path().apply { moveTo(c.x, c.y - 20 * u); lineTo(c.x + 5 * u, c.y); lineTo(c.x - 5 * u, c.y); close() }, tono)
            drawPath(Path().apply { moveTo(c.x, c.y + 20 * u); lineTo(c.x + 5 * u, c.y); lineTo(c.x - 5 * u, c.y); close() }, tinta)
        }
        drawCircle(tinta, 2 * u, c)
    }

    private fun DrawScope.huellas(c: Offset, u: Float, tinta: Color) {
        listOf(Offset(-10f, 14f) to -12f, Offset(6f, 2f) to 8f, Offset(-6f, -12f) to -10f, Offset(10f, -24f) to 10f).forEach { (o, giro) ->
            val p = c + Offset(o.x * u, o.y * u)
            rotate(giro, p) {
                drawOval(tinta, Offset(p.x - 3.5f * u, p.y - 5 * u), Size(7 * u, 10 * u))
                for (k in 0 until 3) drawCircle(tinta, 1.3f * u, Offset(p.x - 2.4f * u + k * 2.4f * u, p.y - 7.5f * u))
            }
        }
    }

    private fun DrawScope.pesa(c: Offset, u: Float, tinta: Color) {
        drawLine(tinta, Offset(c.x - 22 * u, c.y), Offset(c.x + 22 * u, c.y), 3 * u, StrokeCap.Round)
        listOf(-1f, 1f).forEach { s ->
            drawRoundRect(tinta, Offset(c.x + s * 14 * u - 3 * u, c.y - 12 * u), Size(6 * u, 24 * u), CornerRadius(1.5f * u, 1.5f * u))
            drawRoundRect(tinta, Offset(c.x + s * 20 * u - 2.5f * u, c.y - 8 * u), Size(5 * u, 16 * u), CornerRadius(1.5f * u, 1.5f * u))
        }
    }

    private fun DrawScope.montana(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawPath(Path().apply { moveTo(c.x - 28 * u, c.y + 20 * u); lineTo(c.x - 4 * u, c.y - 18 * u); lineTo(c.x + 6 * u, c.y - 4 * u); lineTo(c.x + 14 * u, c.y - 12 * u); lineTo(c.x + 28 * u, c.y + 20 * u); close() }, tinta)
        drawPath(Path().apply { moveTo(c.x - 9 * u, c.y - 10 * u); lineTo(c.x - 4 * u, c.y - 18 * u); lineTo(c.x + 1 * u, c.y - 10 * u); close() }, Color.White.copy(alpha = 0.75f))
        drawLine(tinta, Offset(c.x - 4 * u, c.y - 18 * u), Offset(c.x - 4 * u, c.y - 30 * u), 1.2f * u)
        drawPath(Path().apply { moveTo(c.x - 4 * u, c.y - 30 * u); lineTo(c.x + 6 * u, c.y - 27 * u); lineTo(c.x - 4 * u, c.y - 24 * u); close() }, tono)
    }

    private fun DrawScope.libro(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawPath(Path().apply { moveTo(c.x, c.y - 14 * u); quadraticTo(c.x - 14 * u, c.y - 20 * u, c.x - 26 * u, c.y - 14 * u); lineTo(c.x - 26 * u, c.y + 16 * u); quadraticTo(c.x - 14 * u, c.y + 10 * u, c.x, c.y + 16 * u); close() }, Color.White.copy(alpha = 0.85f))
        drawPath(Path().apply { moveTo(c.x, c.y - 14 * u); quadraticTo(c.x + 14 * u, c.y - 20 * u, c.x + 26 * u, c.y - 14 * u); lineTo(c.x + 26 * u, c.y + 16 * u); quadraticTo(c.x + 14 * u, c.y + 10 * u, c.x, c.y + 16 * u); close() }, Color.White.copy(alpha = 0.85f))
        drawLine(tinta, Offset(c.x, c.y - 14 * u), Offset(c.x, c.y + 16 * u), 1.4f * u)
        for (k in 0 until 4) { val y = c.y - 8 * u + k * 6 * u; drawLine(tinta.copy(alpha = 0.6f), Offset(c.x - 21 * u, y), Offset(c.x - 5 * u, y), 0.9f * u); drawLine(tinta.copy(alpha = 0.6f), Offset(c.x + 5 * u, y), Offset(c.x + 21 * u, y), 0.9f * u) }
        drawPath(Path().apply { moveTo(c.x + 14 * u, c.y - 17 * u); lineTo(c.x + 14 * u, c.y - 4 * u); lineTo(c.x + 17 * u, c.y - 7 * u); lineTo(c.x + 20 * u, c.y - 4 * u); lineTo(c.x + 20 * u, c.y - 18 * u); close() }, tono)
    }

    private fun DrawScope.luna(c: Offset, u: Float, tinta: Color) {
        val p = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - 20 * u, c.y - 20 * u, c.x + 20 * u, c.y + 20 * u)) }
        val hueco = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - 10 * u, c.y - 26 * u, c.x + 26 * u, c.y + 14 * u)) }
        drawPath(Path.combine(androidx.compose.ui.graphics.PathOperation.Difference, p, hueco), tinta)
        listOf(Offset(14f, -14f) to 3f, Offset(20f, 6f) to 2f, Offset(6f, 18f) to 1.6f).forEach { (o, s) -> estrella(c + Offset(o.x * u, o.y * u), s * u, tinta) }
    }

    private fun DrawScope.estrella(p: Offset, s: Float, col: Color) {
        drawPath(Path().apply { for (i in 0 until 8) { val rr = if (i % 2 == 0) s * 1.6f else s * 0.5f; val a = -PI / 2 + i * PI / 4; val x = p.x + (rr * cos(a)).toFloat(); val y = p.y + (rr * sin(a)).toFloat(); if (i == 0) moveTo(x, y) else lineTo(x, y) }; close() }, col)
    }

    private fun DrawScope.sobre(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawRect(Color.White.copy(alpha = 0.85f), Offset(c.x - 24 * u, c.y - 15 * u), Size(48 * u, 30 * u))
        drawRect(tinta, Offset(c.x - 24 * u, c.y - 15 * u), Size(48 * u, 30 * u), style = Stroke(1.2f * u))
        drawPath(Path().apply { moveTo(c.x - 24 * u, c.y - 15 * u); lineTo(c.x, c.y + 3 * u); lineTo(c.x + 24 * u, c.y - 15 * u) }, tinta, style = Stroke(1.2f * u))
        drawCircle(tono, 6 * u, Offset(c.x, c.y + 3 * u))
    }

    private fun DrawScope.reloj(c: Offset, u: Float, tinta: Color) {
        drawPath(Path().apply { moveTo(c.x - 14 * u, c.y - 22 * u); lineTo(c.x + 14 * u, c.y - 22 * u); lineTo(c.x, c.y); lineTo(c.x + 14 * u, c.y + 22 * u); lineTo(c.x - 14 * u, c.y + 22 * u); lineTo(c.x, c.y); close() }, tinta, style = Stroke(1.6f * u))
        drawPath(Path().apply { moveTo(c.x - 9 * u, c.y + 18 * u); lineTo(c.x + 9 * u, c.y + 18 * u); lineTo(c.x, c.y + 6 * u); close() }, tinta)
        drawLine(tinta, Offset(c.x - 18 * u, c.y - 22 * u), Offset(c.x + 18 * u, c.y - 22 * u), 2 * u); drawLine(tinta, Offset(c.x - 18 * u, c.y + 22 * u), Offset(c.x + 18 * u, c.y + 22 * u), 2 * u)
    }

    private fun DrawScope.rayo(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawPath(Path().apply { moveTo(c.x + 4 * u, c.y - 26 * u); lineTo(c.x - 12 * u, c.y + 4 * u); lineTo(c.x - 1 * u, c.y + 4 * u); lineTo(c.x - 6 * u, c.y + 26 * u); lineTo(c.x + 14 * u, c.y - 6 * u); lineTo(c.x + 2 * u, c.y - 6 * u); close() }, tono)
        drawPath(Path().apply { moveTo(c.x + 4 * u, c.y - 26 * u); lineTo(c.x - 12 * u, c.y + 4 * u); lineTo(c.x - 1 * u, c.y + 4 * u); lineTo(c.x - 6 * u, c.y + 26 * u); lineTo(c.x + 14 * u, c.y - 6 * u); lineTo(c.x + 2 * u, c.y - 6 * u); close() }, tinta, style = Stroke(1.2f * u))
    }

    private fun DrawScope.libros(c: Offset, u: Float, tinta: Color, tono: Color) {
        val cols = listOf(tono, tinta, tono.copy(alpha = 0.6f), tinta.copy(alpha = 0.7f))
        cols.forEachIndexed { i, col -> drawRoundRect(col, Offset(c.x - 22 * u + i * 2 * u, c.y + 14 * u - (i + 1) * 8 * u), Size(40 * u, 7 * u), CornerRadius(1.5f * u, 1.5f * u)) }
        drawRect(tinta, Offset(c.x - 22 * u, c.y + 14 * u), Size(46 * u, 2 * u))
    }

    private fun DrawScope.comillas(c: Offset, u: Float, tinta: Color) {
        listOf(-10f, 10f).forEach { dx -> drawCircle(tinta, 7 * u, Offset(c.x + dx * u, c.y - 4 * u)); drawPath(Path().apply { moveTo(c.x + (dx + 6) * u, c.y - 2 * u); quadraticTo(c.x + (dx + 6) * u, c.y + 12 * u, c.x + (dx - 4) * u, c.y + 16 * u); lineTo(c.x + (dx - 2) * u, c.y + 10 * u); close() }, tinta) }
    }

    private fun DrawScope.gente(c: Offset, u: Float, tinta: Color, tono: Color) {
        listOf(Offset(-14f, 4f) to tinta, Offset(14f, 4f) to tinta, Offset(0f, -2f) to tono).forEach { (o, col) ->
            val p = c + Offset(o.x * u, o.y * u)
            drawCircle(col, 6 * u, p - Offset(0f, 8 * u))
            drawArc(col, 180f, 180f, true, Offset(p.x - 10 * u, p.y), Size(20 * u, 20 * u))
        }
    }

    private fun DrawScope.calendario(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawRoundRect(Color.White.copy(alpha = 0.85f), Offset(c.x - 22 * u, c.y - 18 * u), Size(44 * u, 38 * u), CornerRadius(3 * u, 3 * u))
        drawRect(tinta, Offset(c.x - 22 * u, c.y - 18 * u), Size(44 * u, 8 * u))
        for (f in 0 until 4) for (k in 0 until 5) {
            val o = Offset(c.x - 18 * u + k * 8 * u, c.y - 6 * u + f * 6.5f * u)
            if ((f * 5 + k) % 4 != 3) drawLine(tono, o, o + Offset(2.5f * u, 2.5f * u), 1.3f * u) else drawCircle(tinta.copy(alpha = 0.3f), 1 * u, o)
        }
    }

    private fun DrawScope.estandarte(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawLine(tinta, Offset(c.x - 14 * u, c.y + 26 * u), Offset(c.x - 14 * u, c.y - 26 * u), 2 * u)
        drawPath(Path().apply { moveTo(c.x - 14 * u, c.y - 24 * u); lineTo(c.x + 18 * u, c.y - 24 * u); lineTo(c.x + 18 * u, c.y + 6 * u); lineTo(c.x + 2 * u, c.y - 2 * u); lineTo(c.x - 14 * u, c.y + 6 * u); close() }, tono)
        estrella(Offset(c.x + 2 * u, c.y - 12 * u), 4 * u, Color.White.copy(alpha = 0.85f))
    }

    private fun DrawScope.sol(c: Offset, u: Float, tinta: Color, tono: Color) {
        drawCircle(tono, 11 * u, c)
        for (i in 0 until 12) { val a = i * PI / 6; drawLine(tinta, Offset(c.x + (15 * u * cos(a)).toFloat(), c.y + (15 * u * sin(a)).toFloat()), Offset(c.x + (22 * u * cos(a)).toFloat(), c.y + (22 * u * sin(a)).toFloat()), 1.6f * u, StrokeCap.Round) }
    }

    private fun DrawScope.moneda(c: Offset, u: Float, tinta: Color) {
        drawCircle(tinta, 18 * u, c, style = Stroke(2 * u)); drawCircle(tinta, 13 * u, c, style = Stroke(0.8f * u))
        drawLine(tinta, Offset(c.x, c.y - 10 * u), Offset(c.x, c.y + 10 * u), 1.4f * u)
        drawArc(tinta, 200f, 230f, false, Offset(c.x - 6 * u, c.y - 7 * u), Size(12 * u, 7 * u), style = Stroke(1.4f * u))
        drawArc(tinta, 20f, 230f, false, Offset(c.x - 6 * u, c.y), Size(12 * u, 7 * u), style = Stroke(1.4f * u))
    }
}

/** Un logro dibujado (su pieza), del tamaño que se pida. */
@Composable
fun PiezaLogro(l: Logros.Logro, tam: Dp, modifier: Modifier = Modifier, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    Canvas(modifier.size(tam)) { with(PiezasLogro) { pieza(l, tema, cs) } }
}

/** El fondo del salón: arcos y columnas, muros con estandartes, vitrinas de laboratorio o repisas de madera. */
@Composable
private fun BoxScope.FondoSalon(tema: TemaId) {
    val cs = MaterialTheme.colorScheme
    Canvas(Modifier.matchParentSize()) {
        if (tema in SalonesTema.TEMAS) { with(SalonesTema) { escena(tema, cs) }; return@Canvas }
        val w = size.width; val h = size.height
        val linea = cs.outline.copy(alpha = 0.35f)
        when (salonDe(tema)) {
            Salon.TEMPLO -> {
                val n = 4; val paso = w / n
                for (i in 0..n) drawRect(cs.surfaceVariant.copy(alpha = 0.5f), Offset(i * paso - 6f, 0f), Size(12f, h))
                for (i in 0 until n) drawArc(linea, 180f, 180f, false, Offset(i * paso + 6f, 8f), Size(paso - 12f, paso * 0.7f), style = Stroke(2f))
            }
            Salon.CASTILLO -> {
                val fila = 22f
                var y = 0f; var k = 0
                while (y < h) { val off = if (k % 2 == 0) 0f else 30f; var x = -off; while (x < w) { drawRect(linea, Offset(x, y), Size(60f, fila), style = Stroke(1f)); x += 60f }; y += fila; k++ }
                listOf(0.12f, 0.88f).forEach { fx -> drawPath(Path().apply { moveTo(w * fx - 18f, 10f); lineTo(w * fx + 18f, 10f); lineTo(w * fx + 18f, 110f); lineTo(w * fx, 92f); lineTo(w * fx - 18f, 110f); close() }, cs.primary.copy(alpha = 0.35f)) }
            }
            Salon.LABORATORIO -> {
                var x = 0f; while (x < w) { drawLine(cs.primary.copy(alpha = 0.10f), Offset(x, 0f), Offset(x, h), 1f); x += 24f }
                var y = 0f; while (y < h) { drawLine(cs.primary.copy(alpha = 0.10f), Offset(0f, y), Offset(w, y), 1f); y += 24f }
            }
            Salon.TALLER -> {
                var y = 0f; while (y < h) { drawLine(Color(0xFF8A6A44).copy(alpha = 0.18f), Offset(0f, y), Offset(w, y), 1.5f); y += 14f }
            }
        }
    }
}

/** El hall: las piezas en sus pedestales con el salón del tema detrás, y el editor para elegir cuáles (hasta 12). Va arriba de Logros. */
@Composable
fun CuerpoHall(l: List<Logros.Logro>) {
    var editar by remember { mutableStateOf(false) }
    var abierto by remember { mutableStateOf<Logros.Logro?>(null) }
    var conTextos by remember { mutableStateOf(true) }
    val tema = Estilo.tema
    val cs = MaterialTheme.colorScheme
    val ctx = LocalContext.current
    abierto?.let { a -> DetalleTrofeo(a, l) { abierto = null } }
    Column {
        val hechos = l.filter { it.hecho }
        val ids = Repo.config.hall_logros.filter { id -> hechos.any { it.id == id } }
        val enHall = ids.mapNotNull { id -> hechos.firstOrNull { it.id == id } }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${enHall.size} de 12 lugares", style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant, modifier = Modifier.weight(1f))
            TextButton(onClick = { editar = !editar }) { Text(if (editar) "listo" else "elegir piezas", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
        }
        if (enHall.isEmpty() && !editar) {
            Vacio(if (hechos.isEmpty()) "Cuando consigas logros, aquí podrás exhibir los que más te gusten." else "Tu hall está vacío. Elige qué exhibir o llénalo con los mejores.")
            if (hechos.isNotEmpty()) BotonPrincipal("Llenar con mis mejores logros", Modifier.fillMaxWidth().padding(top = 8.dp)) {
                val mejores = hechos.sortedWith(compareByDescending<Logros.Logro> { it.nivel }.thenBy { it.conseguido }).distinctBy { it.id.substringBeforeLast('_') }.take(8).map { it.id }
                Repo.cambiarConfig { c -> c.copy(hall_logros = mejores) }
            }
        }
        if (enHall.isNotEmpty() && !editar) Box(Modifier.fillMaxWidth().padding(top = 8.dp).background(cs.surface.copy(alpha = 0.6f), MaterialTheme.shapes.medium)) {
            FondoSalon(tema)
            Column(Modifier.padding(vertical = 12.dp)) {
                enHall.chunked(2).forEach { fila ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        fila.forEach { g -> Pedestal(g, tema, Modifier.weight(1f).clickable { abierto = g }) }
                        if (fila.size == 1) Box(Modifier.weight(1f))
                    }
                }
            }
        }
        if (enHall.isNotEmpty() && !editar) {
            Ayuda("Toca una pieza para ver cómo la conseguiste.", Modifier.padding(top = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = conTextos, onCheckedChange = { conTextos = it })
                Text("con nombres y fechas", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f).clickable { conTextos = !conTextos })
                TextButton(onClick = {
                    runCatching { HallImagen.crear(ctx, enHall, tema, cs, conTextos) }.onSuccess { LibroDiario.compartir(ctx, it, "image/png") }
                }) { Text("guardar imagen", style = MaterialTheme.typography.labelLarge, color = cs.primary) }
            }
        }
        if (editar) {
            Ayuda("Toca para poner o quitar. Hasta 12. Las flechas cambian el orden.", Modifier.padding(top = 6.dp))
            if (enHall.isNotEmpty()) Tarjeta(Modifier.padding(top = 8.dp)) {
                Text("En el hall", style = MaterialTheme.typography.titleMedium)
                enHall.forEachIndexed { i, g ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        TrofeoLogro(g, 44.dp, tema = tema)
                        Text(g.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f).padding(start = 10.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        TextButton(onClick = { mover(ids, i, -1) }, enabled = i > 0) { Text("↑") }
                        TextButton(onClick = { mover(ids, i, 1) }, enabled = i < ids.lastIndex) { Text("↓") }
                    }
                }
            }
            var cat by remember { mutableStateOf<String?>(null) }
            Row(Modifier.padding(top = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("todos", cat == null) { cat = null }
                    Logros.CATEGORIAS.filter { c -> hechos.any { it.categoria == c } }.forEach { c -> Ficha(c, cat == c) { cat = c } }
                }
            }
            Tarjeta(Modifier.padding(top = 8.dp)) {
                hechos.filter { cat == null || it.categoria == cat }.sortedByDescending { it.conseguido }.forEachIndexed { i, g ->
                    if (i > 0) HorizontalDivider()
                    val dentro = g.id in ids
                    Row(Modifier.fillMaxWidth().clickable {
                        Repo.cambiarConfig { c -> c.copy(hall_logros = if (dentro) c.hall_logros - g.id else if (c.hall_logros.size >= 12) c.hall_logros else c.hall_logros + g.id) }
                    }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = dentro, onCheckedChange = null)
                        TrofeoLogro(g, 40.dp, tema = tema)
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(g.nombre, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(g.categoria + " · " + Fechas.corto(g.conseguido!!), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun mover(ids: List<String>, i: Int, d: Int) {
    val j = i + d
    if (j !in ids.indices) return
    val n = ids.toMutableList(); val x = n[i]; n[i] = n[j]; n[j] = x
    Repo.cambiarConfig { c -> c.copy(hall_logros = n + c.hall_logros.filter { it !in n }) }
}

/** Una pieza en su pedestal, con la placa: nombre, fecha y nivel. */
@Composable
private fun Pedestal(g: Logros.Logro, tema: TemaId, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    val metal = METALES_HALL[(g.nivel - 1).coerceIn(0, 3)]
    Column(modifier.padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // El trofeo del tema (el mismo de la sala de trofeos), sobre la repisa del salón.
        TrofeoLogro(g, 118.dp, tema = tema)
        // El pedestal: columna en el templo, plinto de piedra en el castillo, base iluminada en el laboratorio, repisa en el taller.
        Canvas(Modifier.fillMaxWidth().height(22.dp).padding(horizontal = 18.dp)) {
            if (tema in SalonesTema.TEMAS) { with(SalonesTema) { pedestal(tema, cs, metal) }; return@Canvas }
            when (salonDe(tema)) {
                Salon.TEMPLO -> { drawRect(cs.surfaceVariant, Offset(size.width * 0.15f, 0f), Size(size.width * 0.7f, size.height)); for (k in 1..4) drawLine(cs.outline.copy(alpha = 0.5f), Offset(size.width * (0.15f + 0.14f * k), 0f), Offset(size.width * (0.15f + 0.14f * k), size.height), 1f); drawRect(cs.outline, Offset(0f, size.height - 3f), Size(size.width, 3f)) }
                Salon.CASTILLO -> { drawRect(cs.outline.copy(alpha = 0.6f), Offset(size.width * 0.08f, 0f), Size(size.width * 0.84f, size.height)); drawRect(cs.surface, Offset(size.width * 0.08f, size.height * 0.45f), Size(size.width * 0.84f, 1.5f)) }
                Salon.LABORATORIO -> { drawRoundRect(cs.primary.copy(alpha = 0.18f), Offset(0f, 0f), Size(size.width, size.height), CornerRadius(6f, 6f)); drawLine(cs.primary, Offset(8f, 2f), Offset(size.width - 8f, 2f), 2f) }
                Salon.TALLER -> { drawRect(Color(0xFF8A6A44).copy(alpha = 0.7f), Offset(0f, size.height * 0.3f), Size(size.width, size.height * 0.4f)); drawLine(Color(0xFF5A4630), Offset(0f, size.height * 0.7f), Offset(size.width, size.height * 0.7f), 1.5f) }
            }
        }
        Box(Modifier.padding(top = 4.dp).background(metal.copy(alpha = 0.22f), MaterialTheme.shapes.small).padding(horizontal = 8.dp, vertical = 4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(g.nombre, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(Fechas.corto(g.conseguido ?: "") + " · " + g.conseguido?.take(4), style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, fontSize = 10.sp)
            }
        }
    }
}

