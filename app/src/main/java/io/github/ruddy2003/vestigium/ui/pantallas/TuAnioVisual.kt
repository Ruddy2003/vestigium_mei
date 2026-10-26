package io.github.ruddy2003.vestigium.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.TuAnio
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Bases
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/** Una unidad «redonda» (1, 2, 5, 10, 20, 50…) para que [total] quepa en unos [max] objetos. */
internal fun unidadRedonda(total: Double, max: Int): Long {
    if (total <= max) return 1
    val crudo = total / max
    var u = 1L
    while (true) { for (m in listOf(1L, 2L, 5L)) { if (u * m >= crudo) return u * m }; u *= 10 }
}

private fun miles(n: Long) = "%,d".format(n)

/**
 * El diario del año como pilas de libros: una pila por mes, cada libro un tanto de palabras. La forma del libro es la del
 * tema base (códice, legajo, expediente, rollo, libro cosido, herbario); los colores, los del tema que tengas.
 */
@Composable
internal fun LibrosDelAnio(anio: Int, tema: TemaId = Estilo.tema) {
    val cs = MaterialTheme.colorScheme
    val porMes = remember(Repo.dias, anio) {
        val m = IntArray(12)
        Repo.dias.values.filter { it.fecha.startsWith("$anio-") }.forEach { d -> m[d.fecha.substring(5, 7).toInt() - 1] += Texto.palabras(d.nota) }
        m
    }
    val maximo = porMes.maxOrNull() ?: 0
    if (maximo == 0) { Ayuda("Cuando escribas en el diario, aquí se apilarán tus libros."); return }
    val unidad = unidadRedonda(maximo.toDouble(), 12)
    val base = Bases.de(tema)
    val tonos = listOf(cs.primary, cs.secondary, cs.tertiary, lerp(cs.primary, cs.onBackground, 0.35f))
    Canvas(Modifier.fillMaxWidth().height(230.dp).padding(top = 10.dp)) {
        val col = size.width / 12f; val alto = (size.height - 22 * density) / 12.5f
        for (m in 0 until 12) {
            val libros = ceil(porMes[m] / unidad.toDouble()).toInt()
            for (k in 0 until libros) {
                val ancho = col * (0.66f + ((m * 7 + k * 3) % 5) * 0.04f)
                val x = m * col + (col - ancho) / 2 + (if (k % 2 == 0) -1f else 1f) * density
                val y = size.height - 22 * density - (k + 1) * alto
                volumen(base, Offset(x, y), Size(ancho, alto * 0.88f), tonos[(m + k) % tonos.size], cs.background)
            }
        }
        // La repisa.
        drawRect(cs.onBackground.copy(alpha = 0.25f), Offset(0f, size.height - 22 * density), Size(size.width, 2 * density))
    }
    Row(Modifier.fillMaxWidth()) {
        // La inicial del mes del tema; si en el tema se repiten (MES 1, MES 2…; Luna de…), el número.
        val iniciales = (1..12).map { Estilo.mes(it).trim().take(1).uppercase() }
        val usar = if (iniciales.toSet().size >= 9) iniciales else (1..12).map { "$it" }
        for (m in 1..12) Text(usar[m - 1], style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant,
            modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
    Ayuda("Cada libro = ${miles(unidad)} palabras. El mes más escrito: ${Estilo.mes(porMes.indexOfFirst { it == maximo } + 1)} (${miles(maximo.toLong())}).", Modifier.padding(top = 6.dp))
}

/** Un volumen de la pila, con la forma del tema base. */
private fun DrawScope.volumen(base: TemaId, o: Offset, s: Size, c: Color, fondo: Color) {
    val d = density
    val oscuro = lerp(c, Color.Black, 0.35f); val claro = lerp(c, Color.White, 0.35f)
    when (base) {
        // Códice: tapa de cuero, nervios en el lomo y un broche.
        TemaId.GOTICO -> {
            drawRoundRect(c, o, s, CornerRadius(1.5f * d))
            for (k in 1..3) drawLine(oscuro, Offset(o.x + s.width * k / 4f, o.y), Offset(o.x + s.width * k / 4f, o.y + s.height), 0.8f * d)
            drawRect(Color(0xFFD4AF37), Offset(o.x + s.width - 3 * d, o.y + s.height * 0.3f), Size(3 * d, s.height * 0.4f))
        }
        // Legajo: pliegos atados con cinta roja y un sello.
        TemaId.NAPOLEON -> {
            drawRect(lerp(c, Color.White, 0.75f), o, s)
            drawLine(oscuro.copy(alpha = 0.4f), Offset(o.x, o.y + s.height * 0.5f), Offset(o.x + s.width, o.y + s.height * 0.5f), 0.6f * d)
            drawLine(Color(0xFFA3172E), Offset(o.x + s.width * 0.5f, o.y), Offset(o.x + s.width * 0.5f, o.y + s.height), 1.6f * d)
            drawCircle(Color(0xFFA3172E), s.height * 0.28f, Offset(o.x + s.width * 0.5f, o.y + s.height * 0.5f))
        }
        // Expediente: carpeta de cartón con pestaña y franja roja.
        TemaId.DISTOPIA -> {
            drawRect(lerp(Color(0xFFD8C08A), c, 0.25f), o, s)
            drawRect(lerp(Color(0xFFD8C08A), c, 0.25f), Offset(o.x + s.width * 0.1f, o.y - s.height * 0.18f), Size(s.width * 0.3f, s.height * 0.2f))
            drawRect(Color(0xFFC8102E), Offset(o.x, o.y + s.height * 0.7f), Size(s.width, s.height * 0.14f))
            drawRect(Color(0xFFF2EFE6), Offset(o.x + s.width * 0.55f, o.y + s.height * 0.22f), Size(s.width * 0.3f, s.height * 0.3f))
        }
        // Rollo: un cilindro de papiro con sus varillas.
        TemaId.ROMANO -> {
            drawRoundRect(lerp(Color(0xFFE9D9A9), c, 0.2f), Offset(o.x + s.height * 0.25f, o.y), Size(s.width - s.height * 0.5f, s.height), CornerRadius(s.height / 2))
            listOf(o.x + s.height * 0.25f, o.x + s.width - s.height * 0.25f).forEach { x -> drawCircle(oscuro, s.height * 0.5f, Offset(x, o.y + s.height / 2)) }
            drawLine(c, Offset(o.x + s.width * 0.3f, o.y + s.height / 2), Offset(o.x + s.width * 0.7f, o.y + s.height / 2), 1.2f * d)
        }
        // Libro cosido: tapa lisa, costura de hilo a la izquierda y la etiqueta del título.
        TemaId.SEDA -> {
            drawRect(c, o, s)
            for (k in 0 until 4) { val x = o.x + 3 * d + k * 0.0f; val y = o.y + s.height * (0.15f + k * 0.23f); drawLine(claro, Offset(x, y), Offset(x + 4 * d, y), 0.8f * d) }
            drawRect(lerp(Color.White, c, 0.15f), Offset(o.x + s.width * 0.5f, o.y + s.height * 0.15f), Size(s.width * 0.18f, s.height * 0.7f))
        }
        // Herbario: cuaderno de campo verde con una hoja.
        TemaId.BOTANICO -> {
            drawRoundRect(lerp(c, Color(0xFF3F6A3A), 0.35f), o, s, CornerRadius(2 * d))
            val cx = o.x + s.width * 0.5f; val cy = o.y + s.height * 0.5f; val r = s.height * 0.32f
            drawPath(Path().apply { moveTo(cx - r * 1.4f, cy); quadraticTo(cx, cy - r * 1.3f, cx + r * 1.4f, cy); quadraticTo(cx, cy + r * 1.3f, cx - r * 1.4f, cy); close() }, claro)
        }
        else -> drawRect(c, o, s)
    }
    drawRect(fondo.copy(alpha = 0.12f), o, Size(s.width, 1f * d))
}

/** Cifras del año como objetos que se cuentan de un vistazo: lunas, huellas, alfileres, pesas, tazas y estrellas. */
@Composable
internal fun CosasDelAnio(d: TuAnio.Datos) {
    val p = d.p
    val filas = listOfNotNull(
        (d.suenoDias.takeIf { it >= 1 })?.let { Triple("luna", it, "días enteros dormido") },
        (p.pasosTotal.toDouble().takeIf { it > 0 })?.let { Triple("huella", it, "pasos") },
        (p.ciudades.size.toDouble().takeIf { it > 0 })?.let { Triple("alfiler", it, "ciudades") },
        (p.entrenos.toDouble().takeIf { it > 0 })?.let { Triple("pesa", it, "entrenamientos") },
        (p.tazasCafe.takeIf { it >= 1 })?.let { Triple("taza", it, "tazas de café") },
        (p.suenosRecordados.toDouble().takeIf { it > 0 })?.let { Triple("estrella", it, "sueños recordados") },
        (p.cartas.toDouble().takeIf { it > 0 })?.let { Triple("sobre", it, "cartas escritas") },
    )
    if (filas.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        filas.forEach { (forma, total, etiqueta) ->
            val u = unidadRedonda(total, 30)
            val n = ceil(total / u).toInt().coerceAtMost(30)
            Column {
                Canvas(Modifier.fillMaxWidth().height(if (n > 15) 44.dp else 22.dp)) {
                    val paso = size.width / 15f; val r = minOf(paso, 22 * density) * 0.42f
                    for (i in 0 until n) {
                        val c = Offset(paso * (i % 15) + paso / 2, (i / 15) * 22 * density + 11 * density)
                        objeto(forma, c, r, if (i % 2 == 0) cs.primary else cs.secondary, cs.background)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${miles(total.toLong())} $etiqueta", style = MaterialTheme.typography.labelLarge, color = cs.onBackground, modifier = Modifier.weight(1f))
                    if (u > 1) Text("cada uno = ${miles(u)}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant, modifier = Modifier.width(140.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End)
                }
            }
        }
    }
}

private fun DrawScope.objeto(forma: String, c: Offset, r: Float, col: Color, fondo: Color) {
    val d = density
    when (forma) {
        "luna" -> { drawCircle(col, r, c); drawCircle(fondo, r * 0.85f, Offset(c.x + r * 0.45f, c.y - r * 0.2f)) }
        "huella" -> { drawOval(col, Offset(c.x - r * 0.45f, c.y - r * 0.9f), Size(r * 0.9f, r * 1.3f)); drawOval(col, Offset(c.x - r * 0.35f, c.y + r * 0.5f), Size(r * 0.7f, r * 0.5f)) }
        "alfiler" -> { drawCircle(col, r * 0.55f, Offset(c.x, c.y - r * 0.35f)); drawLine(col, Offset(c.x, c.y - r * 0.2f), Offset(c.x, c.y + r), 1.4f * d) }
        "pesa" -> { drawRect(col, Offset(c.x - r, c.y - r * 0.6f), Size(r * 0.45f, r * 1.2f)); drawRect(col, Offset(c.x + r * 0.55f, c.y - r * 0.6f), Size(r * 0.45f, r * 1.2f)); drawLine(col, Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y), 1.6f * d) }
        "taza" -> { drawRoundRect(col, Offset(c.x - r * 0.7f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.3f), CornerRadius(r * 0.25f)); drawCircle(col, r * 0.35f, Offset(c.x + r * 0.55f, c.y), style = Stroke(1.2f * d)) }
        "estrella" -> {
            val path = Path(); for (k in 0 until 10) { val a = -Math.PI / 2 + k * Math.PI / 5; val rr = if (k % 2 == 0) r else r * 0.45f
                val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat(); if (k == 0) path.moveTo(x, y) else path.lineTo(x, y) }
            path.close(); drawPath(path, col)
        }
        else -> { drawRect(col, Offset(c.x - r, c.y - r * 0.65f), Size(r * 2, r * 1.3f)); drawPath(Path().apply { moveTo(c.x - r, c.y - r * 0.65f); lineTo(c.x, c.y + r * 0.1f); lineTo(c.x + r, c.y - r * 0.65f) }, fondo, style = Stroke(1.2f * d)) }
    }
}
