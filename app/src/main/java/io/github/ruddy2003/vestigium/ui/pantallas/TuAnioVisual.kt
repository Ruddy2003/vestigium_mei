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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ciencia.TuAnio
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import kotlin.math.PI
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
 * El diario del año como pilas de libros: una pila por mes, cada libro un tanto de palabras. Cada tema tiene su libro
 * (rollo con umbilicus, papiro, terminal, códice, expediente, archivador…); los colores, los del tema.
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
    val tonos = listOf(cs.primary, cs.secondary, cs.tertiary, lerp(cs.primary, cs.onBackground, 0.35f))
    Canvas(Modifier.fillMaxWidth().height(230.dp).padding(top = 10.dp)) {
        val col = size.width / 12f; val alto = (size.height - 22 * density) / 12.5f
        for (m in 0 until 12) {
            val libros = ceil(porMes[m] / unidad.toDouble()).toInt()
            for (k in 0 until libros) {
                val ancho = col * (0.66f + ((m * 7 + k * 3) % 5) * 0.04f)
                val x = m * col + (col - ancho) / 2 + (if (k % 2 == 0) -1f else 1f) * density
                val y = size.height - 22 * density - (k + 1) * alto
                volumen(tema, Offset(x, y), Size(ancho, alto * 0.88f), tonos[(m + k) % tonos.size], cs.background)
            }
        }
        // La repisa.
        drawRect(cs.onBackground.copy(alpha = 0.25f), Offset(0f, size.height - 22 * density), Size(size.width, 2 * density))
    }
    Row(Modifier.fillMaxWidth()) {
        // La inicial del mes del tema; si en el tema se repiten (MES 1, MES 2…; Luna de…), su numeral o el número.
        val usar = etiquetasMesesAnio()
        for (m in 1..12) Text(usar[m - 1], style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant,
            modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1, softWrap = false)
    }
    Ayuda("Cada libro = ${miles(unidad)} palabras. El mes más escrito: ${Estilo.mes(porMes.indexOfFirst { it == maximo } + 1)} (${miles(maximo.toLong())}).", Modifier.padding(top = 6.dp))
}

/** Etiqueta corta de cada mes en el tema: la inicial de su nombre; si se repiten, su numeral (si es corto) o el número. */
internal fun etiquetasMesesAnio(): List<String> {
    val iniciales = (1..12).map { Estilo.mes(it).trim().take(1).uppercase() }
    if (iniciales.toSet().size >= 9) return iniciales
    val numerales = (1..12).map { Estilo.numeral(it) }
    return if (numerales.all { it.length <= 4 }) numerales else (1..12).map { "$it" }
}

private fun polarCosa(c: Offset, r: Float, ang: Double) = Offset(c.x + r * cos(ang).toFloat(), c.y + r * sin(ang).toFloat())

/** Estrella de [puntas] puntas con la punta de arriba vertical. */
private fun estrellaCosa(c: Offset, r: Float, puntas: Int, interior: Float): Path = Path().apply {
    for (k in 0 until puntas * 2) {
        val p = polarCosa(c, if (k % 2 == 0) r else r * interior, -PI / 2 + k * PI / puntas)
        if (k == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
    }
    close()
}

/** Un volumen de la pila, con la forma del libro de cada tema. */
private fun DrawScope.volumen(tema: TemaId, o: Offset, s: Size, c: Color, fondo: Color) {
    val d = density
    val oscuro = lerp(c, Color.Black, 0.35f); val claro = lerp(c, Color.White, 0.35f)
    val x0 = o.x; val y0 = o.y; val w = s.width; val h = s.height; val cy = y0 + h / 2
    val oro = Color(0xFFD4AF37)
    when (tema) {
        // Rollo romano: el papiro enrollado en su umbilicus, cuyos pomos asoman por los dos lados, y el titulus colgando.
        TemaId.ROMANO -> {
            drawLine(oscuro, Offset(x0, cy), Offset(x0 + w, cy), h * 0.22f)
            drawRoundRect(lerp(Color(0xFFE9D9A9), c, 0.2f), Offset(x0 + w * 0.12f, y0), Size(w * 0.76f, h), CornerRadius(h * 0.45f))
            listOf(x0 + w * 0.05f, x0 + w * 0.95f).forEach { x -> drawCircle(c, h * 0.2f, Offset(x, cy)) }
            drawLine(oscuro.copy(alpha = 0.5f), Offset(x0 + w * 0.3f, y0 + 1 * d), Offset(x0 + w * 0.3f, y0 + h - 1 * d), 0.6f * d)
            drawRect(Color(0xFFA3312F), Offset(x0 + w * 0.62f, y0 + h * 0.5f), Size(w * 0.12f, h * 0.5f))
        }
        // Terminal: una pantallita con su indicador, una línea de texto y el cursor.
        TemaId.CIBER -> {
            drawRoundRect(lerp(c, Color.Black, 0.7f), o, s, CornerRadius(1.5f * d))
            drawRoundRect(c, o, s, CornerRadius(1.5f * d), style = Stroke(0.8f * d))
            drawPath(Path().apply { moveTo(x0 + w * 0.1f, y0 + h * 0.3f); lineTo(x0 + w * 0.2f, cy); lineTo(x0 + w * 0.1f, y0 + h * 0.7f) }, c, style = Stroke(0.8f * d))
            drawLine(c.copy(alpha = 0.6f), Offset(x0 + w * 0.28f, cy), Offset(x0 + w * 0.62f, cy), 0.9f * d)
            drawRect(c, Offset(x0 + w * 0.68f, y0 + h * 0.28f), Size(w * 0.1f, h * 0.44f))
        }
        // Papiro griego: sin varilla, con la espiral del rollo a la vista, el cordón que lo ata y las fibras.
        TemaId.HELENO -> {
            val papiro = lerp(Color(0xFFDCC48A), c, 0.15f)
            drawRect(papiro, Offset(x0 + h * 0.5f, y0), Size(w - h * 0.5f, h))
            drawCircle(lerp(papiro, Color.Black, 0.12f), h * 0.5f, Offset(x0 + h * 0.5f, cy))
            drawArc(oscuro, 0f, 300f, false, Offset(x0 + h * 0.18f, y0 + h * 0.18f), Size(h * 0.64f, h * 0.64f), style = Stroke(0.7f * d))
            drawArc(oscuro, 60f, 300f, false, Offset(x0 + h * 0.34f, y0 + h * 0.34f), Size(h * 0.32f, h * 0.32f), style = Stroke(0.7f * d))
            drawLine(oscuro.copy(alpha = 0.25f), Offset(x0 + h * 0.6f, y0 + h * 0.35f), Offset(x0 + w, y0 + h * 0.35f), 0.5f * d)
            drawLine(c, Offset(x0 + w * 0.65f, y0), Offset(x0 + w * 0.65f, y0 + h), 1.2f * d)
        }
        // Códice iluminado: cuero, cantoneras de oro, un bullón en el centro y el cajo del lomo.
        TemaId.MEDIEVAL -> {
            drawRect(lerp(c, Color(0xFF6B3A1E), 0.4f), o, s)
            val q = minOf(w, h) * 0.32f
            listOf(Offset(x0, y0) to Offset(1f, 1f), Offset(x0 + w, y0) to Offset(-1f, 1f), Offset(x0, y0 + h) to Offset(1f, -1f), Offset(x0 + w, y0 + h) to Offset(-1f, -1f)).forEach { (p, sg) ->
                drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x + sg.x * q, p.y); lineTo(p.x, p.y + sg.y * q); close() }, oro)
            }
            drawCircle(oro, h * 0.16f, Offset(x0 + w / 2, cy))
            drawLine(oscuro, Offset(x0 + w * 0.12f, y0), Offset(x0 + w * 0.12f, y0 + h), 1f * d)
        }
        // Expediente: carpeta de cartón con pestaña, franja roja y la ficha pegada.
        TemaId.DISTOPIA -> {
            drawRect(lerp(Color(0xFFD8C08A), c, 0.25f), o, s)
            drawRect(lerp(Color(0xFFD8C08A), c, 0.25f), Offset(x0 + w * 0.1f, y0 - h * 0.18f), Size(w * 0.3f, h * 0.2f))
            drawRect(Color(0xFFC8102E), Offset(x0, y0 + h * 0.7f), Size(w, h * 0.14f))
            drawRect(Color(0xFFF2EFE6), Offset(x0 + w * 0.55f, y0 + h * 0.22f), Size(w * 0.3f, h * 0.3f))
        }
        // Archivador de anillas del refugio, con la etiqueta amarilla.
        TemaId.REFUGIO -> {
            drawRoundRect(c, o, s, CornerRadius(2f * d))
            drawRect(oscuro, o, Size(w * 0.18f, h))
            listOf(0.3f, 0.7f).forEach { f -> drawCircle(Color(0xFFC9CED3), h * 0.12f, Offset(x0 + w * 0.09f, y0 + h * f), style = Stroke(0.9f * d)) }
            drawRect(Color(0xFFF2C230), Offset(x0 + w * 0.35f, y0 + h * 0.25f), Size(w * 0.45f, h * 0.5f))
            drawLine(oscuro, Offset(x0 + w * 0.42f, cy), Offset(x0 + w * 0.72f, cy), 0.8f * d)
        }
        // Códice técnico blindado: placa de acero, banda carmesí y remaches de latón.
        TemaId.ORDEN -> {
            drawRect(lerp(c, Color(0xFF6F7883), 0.45f), o, s)
            drawRect(Color(0xFF9E1B22), Offset(x0, y0 + h * 0.38f), Size(w, h * 0.24f))
            listOf(x0 + w * 0.08f, x0 + w * 0.92f).forEach { x -> listOf(y0 + h * 0.18f, y0 + h * 0.82f).forEach { y -> drawCircle(Color(0xFFB08D3A), h * 0.08f, Offset(x, y)) } }
        }
        // Cuaderno de laboratorio: tapa cuadriculada, lomo de color y la etiqueta.
        TemaId.LABORATORIO -> {
            drawRect(lerp(c, Color.White, 0.8f), o, s)
            var gx = x0 + w * 0.2f
            while (gx < x0 + w) { drawLine(c.copy(alpha = 0.25f), Offset(gx, y0), Offset(gx, y0 + h), 0.4f * d); gx += w * 0.16f }
            drawLine(c.copy(alpha = 0.25f), Offset(x0, cy), Offset(x0 + w, cy), 0.4f * d)
            drawRect(c, o, Size(w * 0.12f, h))
            drawRect(Color.White, Offset(x0 + w * 0.45f, y0 + h * 0.25f), Size(w * 0.35f, h * 0.5f))
            drawRect(c, Offset(x0 + w * 0.45f, y0 + h * 0.25f), Size(w * 0.35f, h * 0.5f), style = Stroke(0.5f * d))
        }
        // Gaceta doblada: cabecera, columnas y el cordel que la ata.
        TemaId.COLONOS -> {
            drawRect(lerp(Color(0xFFEDE6D3), c, 0.12f), o, s)
            drawLine(oscuro, Offset(x0 + w * 0.08f, y0 + h * 0.22f), Offset(x0 + w * 0.92f, y0 + h * 0.22f), 1.4f * d)
            listOf(0.35f, 0.62f).forEach { f -> drawLine(oscuro.copy(alpha = 0.35f), Offset(x0 + w * f, y0 + h * 0.38f), Offset(x0 + w * f, y0 + h * 0.9f), 0.5f * d) }
            drawLine(Color(0xFF8A6A3A), Offset(x0 + w * 0.82f, y0), Offset(x0 + w * 0.82f, y0 + h), 1f * d)
        }
        // Legajo: pliegos atados con cinta roja y un sello.
        TemaId.NAPOLEON -> {
            drawRect(lerp(c, Color.White, 0.75f), o, s)
            drawLine(oscuro.copy(alpha = 0.4f), Offset(x0, cy), Offset(x0 + w, cy), 0.6f * d)
            drawLine(Color(0xFFA3172E), Offset(x0 + w * 0.5f, y0), Offset(x0 + w * 0.5f, y0 + h), 1.6f * d)
            drawCircle(Color(0xFFA3172E), h * 0.28f, Offset(x0 + w * 0.5f, cy))
        }
        // Herbario: cuaderno de campo verde con una hoja.
        TemaId.BOTANICO -> {
            drawRoundRect(lerp(c, Color(0xFF3F6A3A), 0.35f), o, s, CornerRadius(2 * d))
            val cx = x0 + w * 0.5f; val r = h * 0.32f
            drawPath(Path().apply { moveTo(cx - r * 1.4f, cy); quadraticTo(cx, cy - r * 1.3f, cx + r * 1.4f, cy); quadraticTo(cx, cy + r * 1.3f, cx - r * 1.4f, cy); close() }, claro)
        }
        // Libro envuelto para regalo: cinta dorada en cruz y su lazo.
        TemaId.NAVIDAD -> {
            drawRoundRect(c, o, s, CornerRadius(1.5f * d))
            drawLine(oro, Offset(x0 + w * 0.62f, y0), Offset(x0 + w * 0.62f, y0 + h), 1.4f * d)
            drawLine(oro, Offset(x0, cy), Offset(x0 + w, cy), 1.4f * d)
            listOf(0.54f, 0.7f).forEach { f -> drawCircle(oro, h * 0.16f, Offset(x0 + w * f, cy - h * 0.05f), style = Stroke(0.9f * d)) }
        }
        // Libro cosido: tapa lisa, costura de hilo a la izquierda y la etiqueta del título.
        TemaId.SEDA -> {
            drawRect(c, o, s)
            for (k in 0 until 4) { val y = y0 + h * (0.15f + k * 0.23f); drawLine(claro, Offset(x0 + 3 * d, y), Offset(x0 + 7 * d, y), 0.8f * d) }
            drawRect(lerp(Color.White, c, 0.15f), Offset(x0 + w * 0.5f, y0 + h * 0.15f), Size(w * 0.18f, h * 0.7f))
        }
        // Libro de horas: tapa con una ventana ojival dorada y el broche.
        TemaId.GOTICO -> {
            drawRoundRect(c, o, s, CornerRadius(1.5f * d))
            drawLine(oscuro, Offset(x0 + w * 0.14f, y0), Offset(x0 + w * 0.14f, y0 + h), 0.8f * d)
            val ax = x0 + w * 0.48f; val aw = w * 0.14f
            drawPath(Path().apply {
                moveTo(ax - aw, y0 + h * 0.88f); lineTo(ax - aw, y0 + h * 0.45f); quadraticTo(ax - aw, y0 + h * 0.15f, ax, y0 + h * 0.1f)
                quadraticTo(ax + aw, y0 + h * 0.15f, ax + aw, y0 + h * 0.45f); lineTo(ax + aw, y0 + h * 0.88f)
            }, oro, style = Stroke(0.8f * d))
            drawRect(oro, Offset(x0 + w - 3 * d, y0 + h * 0.3f), Size(3 * d, h * 0.4f))
        }
        // Cartucho de datos de la misión: la ventana con sus dos bobinas y la cinta.
        TemaId.ESPACIO -> {
            drawRoundRect(lerp(c, Color(0xFF2A2F3A), 0.5f), o, s, CornerRadius(2f * d))
            drawRect(lerp(c, Color.White, 0.6f), Offset(x0 + w * 0.2f, y0 + h * 0.2f), Size(w * 0.6f, h * 0.6f))
            listOf(0.36f, 0.64f).forEach { f -> drawCircle(oscuro, h * 0.17f, Offset(x0 + w * f, cy)); drawCircle(claro, h * 0.06f, Offset(x0 + w * f, cy)) }
            drawLine(c, Offset(x0 + w * 0.36f, cy + h * 0.17f), Offset(x0 + w * 0.64f, cy + h * 0.17f), 0.6f * d)
        }
        // Encuadernación andalusí: la solapa que envuelve el canto, el filete y la mandorla dorada.
        TemaId.ANDALUS -> {
            drawRect(lerp(c, Color(0xFF7A3B1E), 0.3f), o, Size(w * 0.86f, h))
            drawPath(Path().apply { moveTo(x0 + w * 0.86f, y0); lineTo(x0 + w, cy); lineTo(x0 + w * 0.86f, y0 + h); close() }, oscuro)
            drawRect(oro, Offset(x0 + w * 0.04f, y0 + h * 0.1f), Size(w * 0.78f, h * 0.8f), style = Stroke(0.6f * d))
            val mx = x0 + w * 0.43f; val mr = h * 0.28f
            drawPath(Path().apply { moveTo(mx - mr * 1.5f, cy); lineTo(mx, cy - mr); lineTo(mx + mr * 1.5f, cy); lineTo(mx, cy + mr); close() }, oro)
        }
        // Hojas sueltas entre dos tablas de madera, atadas con cordones.
        TemaId.MONGOL -> {
            val madera = lerp(Color(0xFF7A4E2A), c, 0.2f)
            drawRect(lerp(Color(0xFFEFE3C2), c, 0.1f), Offset(x0, y0 + h * 0.22f), Size(w, h * 0.56f))
            drawLine(oscuro.copy(alpha = 0.3f), Offset(x0, cy), Offset(x0 + w, cy), 0.4f * d)
            drawRect(madera, o, Size(w, h * 0.24f)); drawRect(madera, Offset(x0, y0 + h * 0.76f), Size(w, h * 0.24f))
            listOf(0.25f, 0.75f).forEach { f -> drawLine(c, Offset(x0 + w * f, y0), Offset(x0 + w * f, y0 + h), 1.2f * d) }
        }
        // Cuaderno de Leonardo: papel sepia, un círculo de construcción y renglones de escritura especular.
        TemaId.RENACIMIENTO -> {
            drawRect(lerp(Color(0xFFE8D8B4), c, 0.15f), o, s)
            val sepia = Color(0xFF7A4A2A); val cx = x0 + w * 0.28f
            drawCircle(sepia, h * 0.28f, Offset(cx, cy), style = Stroke(0.6f * d))
            drawLine(sepia, Offset(cx - h * 0.28f, cy), Offset(cx + h * 0.28f, cy), 0.5f * d)
            listOf(0.3f, 0.5f, 0.7f).forEach { f -> drawLine(sepia.copy(alpha = 0.6f), Offset(x0 + w * (0.58f - f * 0.1f), y0 + h * f), Offset(x0 + w * 0.9f, y0 + h * f), 0.5f * d) }
            drawLine(Color(0xFFA0442A), Offset(x0, y0), Offset(x0, y0 + h), 1.6f * d)
        }
        // Folleto del plan: cuña roja en diagonal y franja negra, a la manera constructivista.
        TemaId.URSS -> {
            drawRect(lerp(Color(0xFFEDE3CF), c, 0.1f), o, s)
            drawPath(Path().apply { moveTo(x0, y0 + h); lineTo(x0 + w * 0.7f, y0); lineTo(x0 + w, y0); lineTo(x0 + w * 0.3f, y0 + h); close() }, Color(0xFFC8102E))
            drawRect(Color(0xFF1A1A1A), Offset(x0, y0 + h * 0.78f), Size(w, h * 0.22f))
        }
        // Cuaderno de quejas: pliegos escritos y la cinta tricolor.
        TemaId.REVOLUCION -> {
            drawRect(lerp(Color(0xFFF1E9D6), c, 0.1f), o, s)
            listOf(0.3f, 0.5f, 0.7f).forEach { f -> drawLine(oscuro.copy(alpha = 0.3f), Offset(x0 + w * 0.08f, y0 + h * f), Offset(x0 + w * 0.62f, y0 + h * f), 0.5f * d) }
            val bw = w * 0.06f
            listOf(Color(0xFF1F3A93), Color.White, Color(0xFFC8102E)).forEachIndexed { i, cinta -> drawRect(cinta, Offset(x0 + w * 0.72f + i * bw, y0), Size(bw, h)) }
        }
        // Tomo de la compañía: grueso, con dos correas y una gema en la tapa.
        TemaId.ANTIGUAS -> {
            drawRoundRect(lerp(c, Color(0xFF4A3420), 0.4f), o, s, CornerRadius(2.5f * d))
            listOf(0.22f, 0.78f).forEach { f -> drawRect(oscuro, Offset(x0 + w * f - w * 0.04f, y0), Size(w * 0.08f, h)) }
            val gx = x0 + w / 2; val gr = h * 0.26f
            drawPath(Path().apply { moveTo(gx, cy - gr); lineTo(gx + gr * 0.8f, cy); lineTo(gx, cy + gr); lineTo(gx - gr * 0.8f, cy); close() }, Color(0xFF3FA7A0))
        }
    }
    drawRect(fondo.copy(alpha = 0.12f), o, Size(w, 1f * d))
}

/** Cifras del año como objetos que se cuentan de un vistazo: lunas, huellas, alfileres, pesas, tazas, estrellas y sobres, a la manera del tema. */
@Composable
internal fun CosasDelAnio(d: TuAnio.Datos, tema: TemaId = Estilo.tema) {
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
                    val paso = size.width / 15f; val r = minOf(paso, 22 * density) * 0.47f
                    for (i in 0 until n) {
                        val c = Offset(paso * (i % 15) + paso / 2, (i / 15) * 22 * density + 11 * density)
                        objeto(tema, forma, c, r, if (i % 2 == 0) cs.primary else cs.secondary, cs.background)
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

/**
 * Un objeto contado: la ficha del tema (moneda, escudo, hexágono, estampilla…) y, dentro, el objeto. Cada tema tiene
 * además sus propios objetos para algunas cifras (el ánfora romana, el vaso facetado soviético, el copo de Navidad…).
 */
private fun DrawScope.objeto(tema: TemaId, forma: String, c: Offset, r: Float, col: Color, fondo: Color) {
    val relleno = lerp(fondo, col, 0.14f)
    fichaCosa(tema, c, r, col, relleno, fondo)
    val ri = r * 0.64f
    if (!objetoDelTema(tema, forma, c, ri, col, relleno)) objetoComun(forma, c, ri, col, relleno)
}

/** La ficha de fondo de cada objeto, una por tema. */
private fun DrawScope.fichaCosa(tema: TemaId, c: Offset, r: Float, col: Color, relleno: Color, fondo: Color) {
    val d = density
    val borde = col.copy(alpha = 0.55f); val fino = Stroke(0.9f * d)
    val x = c.x; val y = c.y
    fun lleno(p: Path) { drawPath(p, relleno); drawPath(p, borde, style = fino) }
    when (tema) {
        // Denario con su canto de perlas.
        TemaId.ROMANO -> {
            drawCircle(relleno, r, c); drawCircle(borde, r, c, style = fino)
            for (k in 0 until 14) drawCircle(borde, r * 0.07f, polarCosa(c, r * 0.84f, k * 2 * PI / 14))
        }
        // Celda de consola entre corchetes.
        TemaId.CIBER -> {
            drawRect(relleno, Offset(x - r, y - r * 0.85f), Size(2 * r, 1.7f * r))
            val k = Stroke(1.3f * d)
            drawPath(Path().apply { moveTo(x - r * 0.7f, y - r * 0.85f); lineTo(x - r, y - r * 0.85f); lineTo(x - r, y + r * 0.85f); lineTo(x - r * 0.7f, y + r * 0.85f) }, col, style = k)
            drawPath(Path().apply { moveTo(x + r * 0.7f, y - r * 0.85f); lineTo(x + r, y - r * 0.85f); lineTo(x + r, y + r * 0.85f); lineTo(x + r * 0.7f, y + r * 0.85f) }, col, style = k)
        }
        // Hoplon: escudo redondo de borde grueso.
        TemaId.HELENO -> {
            drawCircle(relleno, r, c); drawCircle(borde, r * 0.9f, c, style = Stroke(2.2f * d)); drawCircle(borde, r * 0.7f, c, style = fino)
        }
        // Escudo heráldico.
        TemaId.MEDIEVAL -> lleno(Path().apply {
            moveTo(x - r * 0.9f, y - r * 0.9f); lineTo(x + r * 0.9f, y - r * 0.9f); lineTo(x + r * 0.9f, y + r * 0.1f)
            quadraticTo(x + r * 0.85f, y + r * 0.75f, x, y + r * 1.05f); quadraticTo(x - r * 0.85f, y + r * 0.75f, x - r * 0.9f, y + r * 0.1f); close()
        })
        // Sello de goma de oficina: doble marco.
        TemaId.DISTOPIA -> {
            drawRect(relleno, Offset(x - r, y - r * 0.85f), Size(2 * r, 1.7f * r))
            drawRect(borde, Offset(x - r, y - r * 0.85f), Size(2 * r, 1.7f * r), style = fino)
            drawRect(borde, Offset(x - r * 0.86f, y - r * 0.71f), Size(1.72f * r, 1.42f * r), style = fino)
        }
        // Placa con cuatro remaches.
        TemaId.REFUGIO -> {
            drawRoundRect(relleno, Offset(x - r, y - r), Size(2 * r, 2 * r), CornerRadius(r * 0.3f))
            drawRoundRect(borde, Offset(x - r, y - r), Size(2 * r, 2 * r), CornerRadius(r * 0.3f), style = fino)
            for (sx in listOf(-1f, 1f)) for (sy in listOf(-1f, 1f)) drawCircle(borde, r * 0.08f, Offset(x + sx * r * 0.74f, y + sy * r * 0.74f))
        }
        // Engranaje.
        TemaId.ORDEN -> {
            for (k in 0 until 8) rotate(k * 45f, c) { drawRect(borde, Offset(x - r * 0.14f, y - r), Size(r * 0.28f, r * 0.3f)) }
            drawCircle(relleno, r * 0.8f, c); drawCircle(borde, r * 0.8f, c, style = fino)
        }
        // Hexágono.
        TemaId.LABORATORIO -> lleno(Path().apply {
            for (k in 0 until 6) { val q = polarCosa(c, r, k * PI / 3); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }
            close()
        })
        // Estampilla de correo con su dentado.
        TemaId.COLONOS -> {
            drawRect(relleno, Offset(x - r * 0.9f, y - r), Size(1.8f * r, 2 * r))
            val dr = r * 0.09f
            for (i in 0..6) { val px = x + (-0.9f + i * 0.3f) * r; drawCircle(fondo, dr, Offset(px, y - r)); drawCircle(fondo, dr, Offset(px, y + r)) }
            for (i in 0..6) { val py = y + (-1f + i * (2f / 6f)) * r; drawCircle(fondo, dr, Offset(x - r * 0.9f, py)); drawCircle(fondo, dr, Offset(x + r * 0.9f, py)) }
            drawRect(borde, Offset(x - r * 0.72f, y - r * 0.82f), Size(1.44f * r, 1.64f * r), style = fino)
        }
        // Estrella de la Legión de Honor: cinco brazos de dos puntas.
        TemaId.NAPOLEON -> lleno(Path().apply {
            for (k in 0 until 5) {
                val a = -PI / 2 + k * 2 * PI / 5
                val p1 = polarCosa(c, r, a - 0.2); val m = polarCosa(c, r * 0.74f, a); val p2 = polarCosa(c, r, a + 0.2); val v = polarCosa(c, r * 0.32f, a + PI / 5)
                if (k == 0) moveTo(p1.x, p1.y) else lineTo(p1.x, p1.y)
                lineTo(m.x, m.y); lineTo(p2.x, p2.y); lineTo(v.x, v.y)
            }
            close()
        })
        // Etiqueta de herbario con su ojal.
        TemaId.BOTANICO -> {
            lleno(Path().apply { moveTo(x - r * 0.55f, y - r * 0.85f); lineTo(x + r, y - r * 0.85f); lineTo(x + r, y + r * 0.85f); lineTo(x - r * 0.55f, y + r * 0.85f); lineTo(x - r, y); close() })
            drawCircle(borde, r * 0.1f, Offset(x - r * 0.74f, y), style = fino)
        }
        // Bola del árbol con su casquillo.
        TemaId.NAVIDAD -> {
            drawCircle(relleno, r * 0.9f, Offset(x, y + r * 0.1f)); drawCircle(borde, r * 0.9f, Offset(x, y + r * 0.1f), style = fino)
            drawRect(borde, Offset(x - r * 0.2f, y - r * 0.95f), Size(r * 0.4f, r * 0.2f)); drawCircle(borde, r * 0.12f, Offset(x, y - r * 1.05f), style = fino)
        }
        // Moneda con agujero cuadrado: lo contado queda dentro del cuadrado.
        TemaId.SEDA -> {
            drawCircle(relleno, r, c); drawCircle(borde, r, c, style = fino)
            drawRect(borde, Offset(x - r * 0.68f, y - r * 0.68f), Size(1.36f * r, 1.36f * r), style = fino)
        }
        // Ventana ojival.
        TemaId.GOTICO -> lleno(Path().apply {
            moveTo(x - r * 0.75f, y + r); lineTo(x - r * 0.75f, y - r * 0.1f); quadraticTo(x - r * 0.75f, y - r * 0.85f, x, y - r * 1.1f)
            quadraticTo(x + r * 0.75f, y - r * 0.85f, x + r * 0.75f, y - r * 0.1f); lineTo(x + r * 0.75f, y + r); close()
        })
        // Un planeta con su órbita inclinada y una luna.
        TemaId.ESPACIO -> {
            drawCircle(relleno, r * 0.78f, c)
            rotate(-25f, c) {
                drawOval(borde, Offset(x - r * 1.05f, y - r * 0.38f), Size(2.1f * r, 0.76f * r), style = fino)
                drawCircle(col, r * 0.1f, Offset(x + r * 1.05f, y))
            }
        }
        // Estrella de ocho puntas del lazo.
        TemaId.ANDALUS -> lleno(estrellaCosa(c, r, 8, 0.77f))
        // Rombo doble, como en los bordados del fieltro.
        TemaId.MONGOL -> {
            lleno(Path().apply { moveTo(x, y - r); lineTo(x + r, y); lineTo(x, y + r); lineTo(x - r, y); close() })
            drawPath(Path().apply { moveTo(x, y - r * 0.82f); lineTo(x + r * 0.82f, y); lineTo(x, y + r * 0.82f); lineTo(x - r * 0.82f, y); close() }, borde, style = fino)
        }
        // Trazo de construcción: círculo y cuadrado, como el hombre de Vitruvio.
        TemaId.RENACIMIENTO -> {
            drawCircle(relleno, r, c); drawCircle(borde, r, c, style = fino)
            drawRect(borde, Offset(x - r * 0.82f, y - r * 0.72f), Size(1.64f * r, 1.64f * r), style = fino)
        }
        // Cuadro inclinado, a la manera constructivista.
        TemaId.URSS -> rotate(-15f, c) {
            drawRect(relleno, Offset(x - r * 0.82f, y - r * 0.82f), Size(1.64f * r, 1.64f * r))
            drawRect(borde, Offset(x - r * 0.82f, y - r * 0.82f), Size(1.64f * r, 1.64f * r), style = fino)
        }
        // Escarapela plisada.
        TemaId.REVOLUCION -> {
            drawCircle(relleno, r, c); drawCircle(borde, r, c, style = fino); drawCircle(borde, r * 0.82f, c, style = fino)
            for (k in 0 until 16) drawLine(borde, polarCosa(c, r * 0.84f, k * PI / 8), polarCosa(c, r * 0.98f, k * PI / 8), 0.6f * d)
        }
        // Piedra rúnica de canto irregular.
        TemaId.ANTIGUAS -> lleno(Path().apply {
            listOf(1f, 0.9f, 0.97f, 0.86f, 1f, 0.9f, 0.95f, 0.85f).forEachIndexed { k, f ->
                val q = polarCosa(c, r * f, k * PI / 4 + 0.2)
                if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        })
    }
}

/**
 * Los objetos propios de cada tema, para las cifras que en ese tema tienen otra forma. Devuelve false si el tema usa
 * el objeto común para esa cifra.
 */
private fun DrawScope.objetoDelTema(tema: TemaId, forma: String, c: Offset, r: Float, col: Color, fondo: Color): Boolean {
    val d = density
    val x = c.x; val y = c.y
    val trazo = Stroke(1.2f * d, cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (tema) {
        TemaId.ROMANO -> when (forma) {
            // Ánfora.
            "taza" -> {
                drawOval(col, Offset(x - r * 0.5f, y - r * 0.5f), Size(r, r * 1.25f))
                drawRect(col, Offset(x - r * 0.16f, y - r), Size(r * 0.32f, r * 0.6f))
                drawPath(Path().apply {
                    moveTo(x - r * 0.16f, y - r * 0.85f); quadraticTo(x - r * 0.65f, y - r * 0.8f, x - r * 0.42f, y - r * 0.3f)
                    moveTo(x + r * 0.16f, y - r * 0.85f); quadraticTo(x + r * 0.65f, y - r * 0.8f, x + r * 0.42f, y - r * 0.3f)
                }, col, style = trazo)
                drawPath(Path().apply { moveTo(x - r * 0.2f, y + r * 0.65f); lineTo(x + r * 0.2f, y + r * 0.65f); lineTo(x, y + r); close() }, col)
            }
            // Tablilla de cera con su estilo.
            "sobre" -> {
                drawRect(col, Offset(x - r, y - r * 0.7f), Size(2 * r, 1.4f * r))
                drawRect(fondo, Offset(x - r * 0.75f, y - r * 0.45f), Size(1.5f * r, 0.9f * r))
                drawLine(col, Offset(x - r * 0.5f, y + r * 0.3f), Offset(x + r * 0.6f, y - r * 0.3f), 1.2f * d)
            }
            // Miliario: la columna que marcaba las millas.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.35f, y - r * 0.6f), Size(r * 0.7f, r * 1.4f))
                drawCircle(col, r * 0.35f, Offset(x, y - r * 0.6f))
                drawRect(col, Offset(x - r * 0.6f, y + r * 0.75f), Size(r * 1.2f, r * 0.25f))
            }
            else -> return false
        }
        TemaId.CIBER -> when (forma) {
            // Asterisco de consola.
            "estrella" -> { for (k in 0 until 3) drawLine(col, polarCosa(c, r, PI / 2 + k * PI / 3), polarCosa(c, r, PI / 2 + k * PI / 3 + PI), 1.6f * d) }
            // Retícula de coordenadas.
            "alfiler" -> {
                drawCircle(col, r * 0.6f, c, style = trazo); drawCircle(col, r * 0.14f, c)
                for (k in 0 until 4) drawLine(col, polarCosa(c, r * 0.35f, k * PI / 2), polarCosa(c, r, k * PI / 2), 1.2f * d)
            }
            // Taza en píxeles, con su vapor.
            "taza" -> {
                drawRect(col, Offset(x - r * 0.7f, y - r * 0.35f), Size(r * 1.05f, r * 1.1f))
                drawRect(col, Offset(x + r * 0.35f, y - r * 0.2f), Size(r * 0.45f, r * 0.2f))
                drawRect(col, Offset(x + r * 0.6f, y - r * 0.2f), Size(r * 0.2f, r * 0.6f))
                drawRect(col, Offset(x + r * 0.35f, y + r * 0.2f), Size(r * 0.45f, r * 0.2f))
                drawRect(col, Offset(x - r * 0.45f, y - r * 0.85f), Size(r * 0.2f, r * 0.3f)); drawRect(col, Offset(x, y - r), Size(r * 0.2f, r * 0.3f))
            }
            else -> return false
        }
        TemaId.HELENO -> when (forma) {
            // Kílix: la copa ancha de beber, con su pie y sus asas.
            "taza" -> {
                drawArc(col, 0f, 180f, true, Offset(x - r, y - r * 0.6f), Size(2 * r, r * 0.9f))
                drawLine(col, Offset(x, y + r * 0.2f), Offset(x, y + r * 0.75f), 1.4f * d)
                drawLine(col, Offset(x - r * 0.4f, y + r * 0.8f), Offset(x + r * 0.4f, y + r * 0.8f), 1.6f * d)
                listOf(-1f, 1f).forEach { sx -> drawCircle(col, r * 0.2f, Offset(x + sx * r * 1.05f, y - r * 0.2f), style = trazo) }
            }
            // Columna jónica, con las volutas del capitel.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.25f, y - r * 0.5f), Size(r * 0.5f, r * 1.3f))
                drawLine(col, Offset(x - r * 0.6f, y - r * 0.55f), Offset(x + r * 0.6f, y - r * 0.55f), 1.4f * d)
                listOf(-1f, 1f).forEach { sx -> drawCircle(col, r * 0.18f, Offset(x + sx * r * 0.58f, y - r * 0.4f), style = Stroke(0.9f * d)) }
                drawRect(col, Offset(x - r * 0.45f, y + r * 0.8f), Size(r * 0.9f, r * 0.2f))
                drawLine(fondo, Offset(x, y - r * 0.4f), Offset(x, y + r * 0.7f), 0.6f * d)
            }
            // Sol de Vergina.
            "estrella" -> {
                drawCircle(col, r * 0.28f, c)
                for (k in 0 until 8) {
                    val a = k * PI / 4
                    drawPath(Path().apply {
                        val p1 = polarCosa(c, r * 0.36f, a - 0.18); val p2 = polarCosa(c, r, a); val p3 = polarCosa(c, r * 0.36f, a + 0.18)
                        moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
                    }, col)
                }
            }
            else -> return false
        }
        TemaId.MEDIEVAL -> when (forma) {
            // Cáliz.
            "taza" -> {
                drawArc(col, 0f, 180f, true, Offset(x - r * 0.6f, y - r * 0.9f), Size(r * 1.2f, r * 1.2f))
                drawLine(col, Offset(x, y - r * 0.2f), Offset(x, y + r * 0.75f), 1.4f * d)
                drawCircle(col, r * 0.13f, Offset(x, y + r * 0.45f))
                drawOval(col, Offset(x - r * 0.45f, y + r * 0.7f), Size(r * 0.9f, r * 0.28f))
            }
            // Pergamino enrollado con su sello de lacre.
            "sobre" -> {
                drawRoundRect(col, Offset(x - r, y - r * 0.45f), Size(2 * r, r * 0.9f), CornerRadius(r * 0.45f))
                listOf(-1f, 1f).forEach { sx -> drawCircle(fondo, r * 0.18f, Offset(x + sx * r * 0.78f, y), style = Stroke(0.8f * d)) }
                drawCircle(Color(0xFFA3172E), r * 0.3f, c)
            }
            // Torre almenada.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.5f, y - r * 0.4f), Size(r, r * 1.4f))
                for (k in 0 until 3) drawRect(col, Offset(x - r * 0.5f + k * r * 0.4f, y - r * 0.75f), Size(r * 0.2f, r * 0.35f))
                drawRect(fondo, Offset(x - r * 0.15f, y + r * 0.5f), Size(r * 0.3f, r * 0.5f))
            }
            else -> return false
        }
        TemaId.DISTOPIA -> when (forma) {
            // El ojo que vigila.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x - r, y); quadraticTo(x, y - r * 0.9f, x + r, y); quadraticTo(x, y + r * 0.9f, x - r, y); close() }, col)
                drawCircle(fondo, r * 0.4f, c); drawCircle(col, r * 0.2f, c)
            }
            // Expediente con pestaña y franja roja.
            "sobre" -> {
                drawRect(col, Offset(x - r, y - r * 0.5f), Size(2 * r, r * 1.2f))
                drawRect(col, Offset(x - r, y - r * 0.75f), Size(r * 0.8f, r * 0.3f))
                drawRect(Color(0xFFC8102E), Offset(x - r, y + r * 0.35f), Size(2 * r, r * 0.2f))
            }
            // Lata de ración.
            "taza" -> {
                drawRect(col, Offset(x - r * 0.55f, y - r * 0.75f), Size(r * 1.1f, r * 1.5f))
                listOf(-0.5f, 0.5f).forEach { f -> drawLine(fondo, Offset(x - r * 0.55f, y + f * r), Offset(x + r * 0.55f, y + f * r), 0.7f * d) }
                drawRect(fondo, Offset(x - r * 0.38f, y - r * 0.2f), Size(r * 0.76f, r * 0.4f))
            }
            else -> return false
        }
        TemaId.REFUGIO -> when (forma) {
            // Cantimplora.
            "taza" -> {
                drawCircle(col, r * 0.72f, Offset(x, y + r * 0.2f))
                drawCircle(fondo, r * 0.45f, Offset(x, y + r * 0.2f), style = Stroke(0.7f * d))
                drawRect(col, Offset(x - r * 0.15f, y - r * 0.8f), Size(r * 0.3f, r * 0.35f))
                drawRect(col, Offset(x - r * 0.25f, y - r), Size(r * 0.5f, r * 0.2f))
            }
            // La compuerta del refugio, con su volante.
            "alfiler" -> {
                drawCircle(col, r * 0.9f, c); drawCircle(fondo, r * 0.62f, c)
                drawCircle(col, r * 0.32f, c, style = Stroke(1f * d))
                for (k in 0 until 3) drawLine(col, c, polarCosa(c, r * 0.55f, -PI / 2 + k * 2 * PI / 3), 1f * d)
            }
            // Átomo.
            "estrella" -> {
                for (k in 0 until 3) rotate(k * 60f, c) { drawOval(col, Offset(x - r, y - r * 0.36f), Size(2 * r, r * 0.72f), style = Stroke(0.9f * d)) }
                drawCircle(col, r * 0.2f, c)
            }
            else -> return false
        }
        TemaId.ORDEN -> when (forma) {
            // Espada clavada.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.12f, y - r * 0.45f), Size(r * 0.24f, r * 1.15f))
                drawPath(Path().apply { moveTo(x - r * 0.12f, y + r * 0.7f); lineTo(x + r * 0.12f, y + r * 0.7f); lineTo(x, y + r); close() }, col)
                drawRect(col, Offset(x - r * 0.55f, y - r * 0.6f), Size(r * 1.1f, r * 0.18f))
                drawLine(col, Offset(x, y - r * 0.6f), Offset(x, y - r * 0.9f), 1.4f * d)
                drawCircle(col, r * 0.14f, Offset(x, y - r * 0.95f))
            }
            // Galones de rango.
            "estrella" -> listOf(-0.35f, 0.25f).forEach { f ->
                drawPath(Path().apply { moveTo(x - r * 0.8f, y + (f - 0.35f) * r); lineTo(x, y + (f + 0.2f) * r); lineTo(x + r * 0.8f, y + (f - 0.35f) * r) }, col, style = Stroke(r * 0.25f, join = StrokeJoin.Miter))
            }
            // Placa de datos con la esquina cortada.
            "sobre" -> {
                drawPath(Path().apply { moveTo(x - r, y - r * 0.6f); lineTo(x + r * 0.6f, y - r * 0.6f); lineTo(x + r, y - r * 0.2f); lineTo(x + r, y + r * 0.6f); lineTo(x - r, y + r * 0.6f); close() }, col)
                listOf(-0.25f, 0.05f, 0.35f).forEach { f -> drawLine(fondo, Offset(x - r * 0.7f, y + f * r), Offset(x + r * 0.5f, y + f * r), 0.7f * d) }
            }
            else -> return false
        }
        TemaId.LABORATORIO -> when (forma) {
            // Vaso de precipitados con su pico, graduado y a medio llenar.
            "taza" -> {
                drawRect(col, Offset(x - r * 0.6f, y), Size(r * 1.2f, r * 0.8f))
                drawPath(Path().apply { moveTo(x - r * 0.6f, y - r * 0.8f); lineTo(x - r * 0.6f, y + r * 0.8f); lineTo(x + r * 0.6f, y + r * 0.8f); lineTo(x + r * 0.6f, y - r * 0.8f); lineTo(x + r * 0.78f, y - r * 0.95f) }, col, style = trazo)
                listOf(-0.5f, -0.2f).forEach { f -> drawLine(col, Offset(x - r * 0.6f, y + f * r), Offset(x - r * 0.3f, y + f * r), 0.7f * d) }
            }
            // Molécula: tres átomos y sus enlaces.
            "estrella" -> {
                val a = Offset(x, y - r * 0.5f); val b = Offset(x - r * 0.6f, y + r * 0.4f); val e = Offset(x + r * 0.6f, y + r * 0.4f)
                drawLine(col, a, b, 1.2f * d); drawLine(col, a, e, 1.2f * d)
                drawCircle(col, r * 0.36f, a); drawCircle(col, r * 0.28f, b); drawCircle(col, r * 0.28f, e)
            }
            // Portapapeles.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.75f, y - r * 0.85f), Size(r * 1.5f, r * 1.85f))
                drawRect(fondo, Offset(x - r * 0.55f, y - r * 0.55f), Size(r * 1.1f, r * 1.35f))
                drawRect(col, Offset(x - r * 0.35f, y - r), Size(r * 0.7f, r * 0.3f))
                listOf(-0.2f, 0.15f, 0.5f).forEach { f -> drawLine(col, Offset(x - r * 0.4f, y + f * r), Offset(x + r * 0.4f, y + f * r), 0.6f * d) }
            }
            else -> return false
        }
        TemaId.COLONOS -> when (forma) {
            // Una casa del pueblo.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.65f, y - r * 0.1f), Size(r * 1.3f, r * 1.0f))
                drawPath(Path().apply { moveTo(x - r * 0.85f, y - r * 0.1f); lineTo(x, y - r * 0.9f); lineTo(x + r * 0.85f, y - r * 0.1f); close() }, col)
                drawRect(fondo, Offset(x - r * 0.15f, y + r * 0.35f), Size(r * 0.3f, r * 0.55f))
            }
            // Lechera.
            "taza" -> {
                drawRect(col, Offset(x - r * 0.5f, y - r * 0.2f), Size(r, r * 1.1f))
                drawPath(Path().apply { moveTo(x - r * 0.5f, y - r * 0.2f); lineTo(x - r * 0.25f, y - r * 0.65f); lineTo(x + r * 0.25f, y - r * 0.65f); lineTo(x + r * 0.5f, y - r * 0.2f); close() }, col)
                drawRect(col, Offset(x - r * 0.35f, y - r * 0.9f), Size(r * 0.7f, r * 0.2f))
                drawLine(fondo, Offset(x - r * 0.5f, y + r * 0.35f), Offset(x + r * 0.5f, y + r * 0.35f), 0.7f * d)
            }
            // Periódico doblado.
            "sobre" -> {
                drawRect(col, Offset(x - r, y - r * 0.7f), Size(2 * r, r * 1.4f))
                drawLine(fondo, Offset(x - r * 0.8f, y - r * 0.4f), Offset(x + r * 0.8f, y - r * 0.4f), 1.3f * d)
                listOf(-0.3f, 0.3f).forEach { f -> drawLine(fondo, Offset(x + f * r, y - r * 0.15f), Offset(x + f * r, y + r * 0.55f), 0.6f * d) }
            }
            else -> return false
        }
        TemaId.NAPOLEON -> when (forma) {
            // Bicornio con su escarapela.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x - r, y + r * 0.3f); quadraticTo(x, y - r * 1.1f, x + r, y + r * 0.3f); close() }, col)
                drawCircle(fondo, r * 0.18f, Offset(x + r * 0.35f, y - r * 0.12f))
            }
            // Boletín plegado con su lacre.
            "sobre" -> {
                drawRect(col, Offset(x - r, y - r * 0.65f), Size(2 * r, r * 1.3f))
                drawRect(fondo, Offset(x - r * 0.8f, y - r * 0.45f), Size(r * 1.6f, r * 0.9f))
                drawCircle(Color(0xFFA3172E), r * 0.3f, c)
            }
            // Abeja imperial.
            "estrella" -> {
                drawOval(col, Offset(x - r * 0.32f, y - r * 0.35f), Size(r * 0.64f, r * 1.1f))
                drawCircle(col, r * 0.22f, Offset(x, y - r * 0.55f))
                drawOval(col, Offset(x - r * 0.95f, y - r * 0.6f), Size(r * 0.7f, r * 0.5f), style = Stroke(0.9f * d))
                drawOval(col, Offset(x + r * 0.25f, y - r * 0.6f), Size(r * 0.7f, r * 0.5f), style = Stroke(0.9f * d))
                listOf(0.05f, 0.3f).forEach { f -> drawLine(fondo, Offset(x - r * 0.3f, y + f * r), Offset(x + r * 0.3f, y + f * r), 0.7f * d) }
            }
            else -> return false
        }
        TemaId.BOTANICO -> when (forma) {
            // Escarabajo clavado con su alfiler.
            "alfiler" -> {
                for (k in 0 until 3) {
                    val yy = y + (k - 1) * r * 0.35f
                    drawLine(col, Offset(x - r * 0.75f, yy - r * 0.1f), Offset(x + r * 0.75f, yy - r * 0.1f), 0.7f * d)
                }
                drawOval(col, Offset(x - r * 0.45f, y - r * 0.5f), Size(r * 0.9f, r * 1.3f))
                drawCircle(col, r * 0.22f, Offset(x, y - r * 0.65f))
                drawLine(fondo, Offset(x, y - r * 0.4f), Offset(x, y + r * 0.75f), 0.6f * d)
                drawCircle(fondo, r * 0.12f, Offset(x, y - r * 0.2f))
            }
            // Flor de cinco pétalos.
            "estrella" -> {
                for (k in 0 until 5) drawCircle(col, r * 0.36f, polarCosa(c, r * 0.52f, -PI / 2 + k * 2 * PI / 5))
                drawCircle(fondo, r * 0.22f, c)
            }
            // Frasco de muestras con su corcho.
            "taza" -> {
                drawRoundRect(col, Offset(x - r * 0.5f, y - r * 0.5f), Size(r, r * 1.4f), CornerRadius(r * 0.2f))
                drawRect(lerp(col, Color(0xFF8A6A3A), 0.6f), Offset(x - r * 0.3f, y - r * 0.85f), Size(r * 0.6f, r * 0.35f))
                drawRect(fondo, Offset(x - r * 0.32f, y), Size(r * 0.64f, r * 0.35f))
            }
            else -> return false
        }
        TemaId.NAVIDAD -> when (forma) {
            // Copo de nieve.
            "estrella" -> {
                for (k in 0 until 6) {
                    val a = -PI / 2 + k * PI / 3
                    drawLine(col, c, polarCosa(c, r, a), 1.1f * d)
                    val m = polarCosa(c, r * 0.55f, a)
                    drawLine(col, m, polarCosa(m, r * 0.3f, a - 0.7), 0.9f * d); drawLine(col, m, polarCosa(m, r * 0.3f, a + 0.7), 0.9f * d)
                }
            }
            // Regalo con su lazo.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.8f, y - r * 0.5f), Size(r * 1.6f, r * 1.4f))
                drawLine(fondo, Offset(x, y - r * 0.5f), Offset(x, y + r * 0.9f), 1.2f * d)
                drawLine(fondo, Offset(x - r * 0.8f, y + r * 0.1f), Offset(x + r * 0.8f, y + r * 0.1f), 1.2f * d)
                listOf(-1f, 1f).forEach { sx -> drawCircle(col, r * 0.22f, Offset(x + sx * r * 0.24f, y - r * 0.72f), style = Stroke(1f * d)) }
            }
            // Pino.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x, y - r); lineTo(x + r * 0.75f, y + r * 0.55f); lineTo(x - r * 0.75f, y + r * 0.55f); close() }, col)
                drawRect(col, Offset(x - r * 0.15f, y + r * 0.55f), Size(r * 0.3f, r * 0.42f))
            }
            else -> return false
        }
        TemaId.SEDA -> when (forma) {
            // Cuenco de té con su vapor.
            "taza" -> {
                drawArc(col, 0f, 180f, true, Offset(x - r * 0.85f, y - r * 0.55f), Size(r * 1.7f, r * 1.2f))
                drawRect(col, Offset(x - r * 0.3f, y + r * 0.6f), Size(r * 0.6f, r * 0.15f))
                drawPath(Path().apply { moveTo(x - r * 0.2f, y - r * 0.15f); quadraticTo(x - r * 0.45f, y - r * 0.5f, x - r * 0.15f, y - r * 0.9f) }, col, style = Stroke(0.8f * d))
            }
            // Farol de papel.
            "alfiler" -> {
                drawOval(col, Offset(x - r * 0.6f, y - r * 0.65f), Size(r * 1.2f, r * 1.3f))
                drawRect(col, Offset(x - r * 0.35f, y - r * 0.8f), Size(r * 0.7f, r * 0.18f)); drawRect(col, Offset(x - r * 0.35f, y + r * 0.62f), Size(r * 0.7f, r * 0.18f))
                listOf(-0.28f, 0f, 0.28f).forEach { f -> drawLine(fondo, Offset(x + f * r, y - r * 0.55f), Offset(x + f * r, y + r * 0.55f), 0.6f * d) }
                drawLine(col, Offset(x, y + r * 0.8f), Offset(x, y + r), 1f * d)
            }
            // Rollo colgante.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.55f, y - r * 0.75f), Size(r * 1.1f, r * 1.5f))
                drawRect(fondo, Offset(x - r * 0.4f, y - r * 0.55f), Size(r * 0.8f, r * 1.1f))
                listOf(-0.8f, 0.8f).forEach { f -> drawLine(col, Offset(x - r * 0.75f, y + f * r), Offset(x + r * 0.75f, y + f * r), 1.4f * d) }
                drawLine(col, Offset(x, y + r * 0.35f), Offset(x, y - r * 0.35f), 0.8f * d)
            }
            else -> return false
        }
        TemaId.GOTICO -> when (forma) {
            // Aguja de catedral.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x, y - r); lineTo(x + r * 0.38f, y - r * 0.1f); lineTo(x - r * 0.38f, y - r * 0.1f); close() }, col)
                drawRect(col, Offset(x - r * 0.38f, y - r * 0.1f), Size(r * 0.76f, r * 1.1f))
                drawPath(Path().apply { moveTo(x - r * 0.12f, y + r * 0.6f); lineTo(x - r * 0.12f, y + r * 0.25f); quadraticTo(x - r * 0.12f, y + r * 0.08f, x, y + r * 0.02f); quadraticTo(x + r * 0.12f, y + r * 0.08f, x + r * 0.12f, y + r * 0.25f); lineTo(x + r * 0.12f, y + r * 0.6f); close() }, fondo)
            }
            // Libro de horas con su broche y una cruz.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.8f, y - r * 0.65f), Size(r * 1.6f, r * 1.3f))
                drawRect(fondo, Offset(x + r * 0.62f, y - r * 0.15f), Size(r * 0.3f, r * 0.3f))
                drawLine(fondo, Offset(x - r * 0.1f, y - r * 0.4f), Offset(x - r * 0.1f, y + r * 0.4f), 0.8f * d)
                drawLine(fondo, Offset(x - r * 0.4f, y - r * 0.1f), Offset(x + r * 0.2f, y - r * 0.1f), 0.8f * d)
            }
            // Estrella de ocho puntas de la bóveda.
            "estrella" -> drawPath(estrellaCosa(c, r, 8, 0.45f), col)
            else -> return false
        }
        TemaId.ESPACIO -> when (forma) {
            // Cohete.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x, y - r); quadraticTo(x + r * 0.35f, y - r * 0.6f, x + r * 0.3f, y - r * 0.2f); lineTo(x + r * 0.3f, y + r * 0.5f); lineTo(x - r * 0.3f, y + r * 0.5f); lineTo(x - r * 0.3f, y - r * 0.2f); quadraticTo(x - r * 0.35f, y - r * 0.6f, x, y - r); close() }, col)
                listOf(-1f, 1f).forEach { sx -> drawPath(Path().apply { moveTo(x + sx * r * 0.3f, y + r * 0.05f); lineTo(x + sx * r * 0.7f, y + r * 0.75f); lineTo(x + sx * r * 0.3f, y + r * 0.5f); close() }, col) }
                drawCircle(fondo, r * 0.14f, Offset(x, y - r * 0.25f))
            }
            // Destello de cuatro puntas.
            "estrella" -> drawPath(estrellaCosa(c, r, 4, 0.25f), col)
            // Antena de la sala de control, con su señal.
            "sobre" -> {
                rotate(-30f, c) { drawArc(col, 0f, 180f, true, Offset(x - r * 0.75f, y - r * 0.75f), Size(r * 1.5f, r * 1.1f)) }
                drawPath(Path().apply { moveTo(x, y + r * 0.1f); lineTo(x + r * 0.45f, y + r * 0.95f); lineTo(x - r * 0.45f, y + r * 0.95f); close() }, col)
                drawArc(col, -70f, 50f, false, Offset(x - r * 0.2f, y - r * 1.1f), Size(r * 1.1f, r * 1.1f), style = Stroke(0.8f * d))
            }
            else -> return false
        }
        TemaId.ANDALUS -> when (forma) {
            // Arco de herradura.
            "alfiler" -> {
                drawArc(col, 140f, 260f, false, Offset(x - r * 0.6f, y - r * 0.8f), Size(r * 1.2f, r * 1.2f), style = Stroke(r * 0.26f))
                listOf(-1f, 1f).forEach { sx -> drawLine(col, Offset(x + sx * r * 0.46f, y + r * 0.18f), Offset(x + sx * r * 0.46f, y + r * 0.95f), r * 0.26f) }
            }
            // Dallah: la cafetera de pico curvo.
            "taza" -> {
                drawOval(col, Offset(x - r * 0.45f, y - r * 0.1f), Size(r * 0.9f, r * 0.95f))
                drawPath(Path().apply { moveTo(x - r * 0.2f, y - r * 0.05f); lineTo(x - r * 0.15f, y - r * 0.55f); lineTo(x + r * 0.15f, y - r * 0.55f); lineTo(x + r * 0.2f, y - r * 0.05f); close() }, col)
                drawPath(Path().apply { moveTo(x - r * 0.2f, y - r * 0.55f); lineTo(x, y - r * 0.9f); lineTo(x + r * 0.2f, y - r * 0.55f); close() }, col)
                drawPath(Path().apply { moveTo(x - r * 0.38f, y + r * 0.25f); quadraticTo(x - r * 0.85f, y - r * 0.1f, x - r * 0.95f, y - r * 0.6f) }, col, style = Stroke(r * 0.14f, cap = StrokeCap.Round))
                drawArc(col, -90f, 180f, false, Offset(x + r * 0.15f, y - r * 0.45f), Size(r * 0.6f, r * 0.8f), style = Stroke(r * 0.12f))
            }
            // Libro con solapa y mandorla.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.9f, y - r * 0.65f), Size(r * 1.5f, r * 1.3f))
                drawPath(Path().apply { moveTo(x + r * 0.6f, y - r * 0.65f); lineTo(x + r, y); lineTo(x + r * 0.6f, y + r * 0.65f); close() }, col)
                drawPath(Path().apply { moveTo(x - r * 0.6f, y); lineTo(x - r * 0.15f, y - r * 0.35f); lineTo(x + r * 0.3f, y); lineTo(x - r * 0.15f, y + r * 0.35f); close() }, fondo)
            }
            else -> return false
        }
        TemaId.MONGOL -> when (forma) {
            // Ger, la tienda de fieltro.
            "alfiler" -> {
                drawRect(col, Offset(x - r * 0.8f, y - r * 0.05f), Size(r * 1.6f, r * 0.85f))
                drawPath(Path().apply { moveTo(x - r * 0.85f, y); quadraticTo(x, y - r * 1.1f, x + r * 0.85f, y); close() }, col)
                drawRect(fondo, Offset(x - r * 0.15f, y + r * 0.3f), Size(r * 0.3f, r * 0.5f))
                drawCircle(fondo, r * 0.1f, Offset(x, y - r * 0.5f))
            }
            // Odre de cuero con su tapón.
            "taza" -> {
                drawPath(Path().apply { moveTo(x, y - r * 0.75f); quadraticTo(x + r * 0.95f, y + r * 0.3f, x, y + r * 0.9f); quadraticTo(x - r * 0.95f, y + r * 0.3f, x, y - r * 0.75f); close() }, col)
                drawRect(col, Offset(x - r * 0.12f, y - r), Size(r * 0.24f, r * 0.28f))
                drawArc(fondo, 200f, 140f, false, Offset(x - r * 0.45f, y - r * 0.1f), Size(r * 0.9f, r * 0.7f), style = Stroke(0.7f * d))
            }
            // Arco y flecha: el ejercicio de la estepa.
            "pesa" -> {
                drawArc(col, 125f, 110f, false, Offset(x - r * 0.45f, y - r), Size(2 * r, 2 * r), style = Stroke(1.6f * d))
                drawLine(col, Offset(x - r * 0.02f, y - r * 0.82f), Offset(x - r * 0.02f, y + r * 0.82f), 0.6f * d)
                drawLine(col, Offset(x - r * 0.6f, y), Offset(x + r * 0.8f, y), 1f * d)
                drawPath(Path().apply { moveTo(x + r, y); lineTo(x + r * 0.75f, y - r * 0.16f); lineTo(x + r * 0.75f, y + r * 0.16f); close() }, col)
            }
            else -> return false
        }
        TemaId.RENACIMIENTO -> when (forma) {
            // Compás de dibujo.
            "alfiler" -> {
                drawLine(col, Offset(x, y - r * 0.75f), Offset(x - r * 0.55f, y + r * 0.9f), r * 0.18f)
                drawLine(col, Offset(x, y - r * 0.75f), Offset(x + r * 0.55f, y + r * 0.9f), r * 0.18f)
                drawCircle(col, r * 0.18f, Offset(x, y - r * 0.75f)); drawLine(col, Offset(x, y - r * 0.75f), Offset(x, y - r), 1f * d)
                drawArc(col, 30f, 120f, false, Offset(x - r * 0.65f, y - r * 0.1f), Size(r * 1.3f, r * 1.3f), style = Stroke(0.6f * d))
            }
            // Poliedro, como los que Leonardo dibujó para Pacioli.
            "estrella" -> {
                drawPath(Path().apply { for (k in 0 until 6) { val q = polarCosa(c, r, -PI / 2 + k * PI / 3); if (k == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y) }; close() }, col, style = trazo)
                for (k in 0 until 3) drawLine(col, c, polarCosa(c, r, PI / 2 + k * 2 * PI / 3), 1.1f * d)
            }
            // Cuaderno con renglones de escritura especular.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.8f, y - r * 0.65f), Size(r * 1.6f, r * 1.3f))
                drawRect(fondo, Offset(x - r * 0.65f, y - r * 0.5f), Size(r * 1.3f, r))
                listOf(-0.25f, 0.05f, 0.35f).forEachIndexed { i, f -> drawLine(col, Offset(x - r * (0.2f + i * 0.12f), y + f * r), Offset(x + r * 0.5f, y + f * r), 0.6f * d) }
            }
            else -> return false
        }
        TemaId.URSS -> when (forma) {
            // Fábrica con su chimenea.
            "alfiler" -> {
                drawPath(Path().apply {
                    moveTo(x - r, y + r * 0.8f); lineTo(x - r, y); lineTo(x - r * 0.55f, y - r * 0.35f); lineTo(x - r * 0.55f, y); lineTo(x - r * 0.1f, y - r * 0.35f)
                    lineTo(x - r * 0.1f, y); lineTo(x + r * 0.4f, y); lineTo(x + r * 0.4f, y + r * 0.8f); close()
                }, col)
                drawRect(col, Offset(x + r * 0.45f, y - r), Size(r * 0.3f, r * 1.8f))
            }
            // Vaso facetado.
            "taza" -> {
                drawPath(Path().apply { moveTo(x - r * 0.55f, y - r * 0.85f); lineTo(x + r * 0.55f, y - r * 0.85f); lineTo(x + r * 0.42f, y + r * 0.85f); lineTo(x - r * 0.42f, y + r * 0.85f); close() }, col)
                drawLine(fondo, Offset(x - r * 0.52f, y - r * 0.55f), Offset(x + r * 0.52f, y - r * 0.55f), 0.7f * d)
                listOf(-0.25f, 0f, 0.25f).forEach { f -> drawLine(fondo, Offset(x + f * r, y - r * 0.4f), Offset(x + f * r * 0.85f, y + r * 0.75f), 0.6f * d) }
            }
            // Pesa rusa.
            "pesa" -> {
                drawCircle(col, r * 0.68f, Offset(x, y + r * 0.28f))
                drawArc(col, 180f, 180f, false, Offset(x - r * 0.42f, y - r * 0.95f), Size(r * 0.84f, r * 0.84f), style = Stroke(r * 0.2f))
                listOf(-1f, 1f).forEach { sx -> drawLine(col, Offset(x + sx * r * 0.42f, y - r * 0.53f), Offset(x + sx * r * 0.42f, y - r * 0.2f), r * 0.2f) }
            }
            else -> return false
        }
        TemaId.REVOLUCION -> when (forma) {
            // Gorro frigio con escarapela.
            "alfiler" -> {
                drawPath(Path().apply {
                    moveTo(x - r * 0.7f, y + r * 0.7f); lineTo(x + r * 0.7f, y + r * 0.7f); quadraticTo(x + r * 0.75f, y - r * 0.7f, x - r * 0.1f, y - r * 0.75f)
                    quadraticTo(x - r * 0.75f, y - r * 0.75f, x - r * 0.85f, y - r * 0.2f); quadraticTo(x - r * 0.45f, y - r * 0.35f, x - r * 0.5f, y + r * 0.1f); close()
                }, col)
                drawCircle(fondo, r * 0.18f, Offset(x + r * 0.3f, y + r * 0.35f))
            }
            // Cuaderno de quejas: pliegos escritos.
            "sobre" -> {
                drawRect(col, Offset(x - r * 0.65f, y - r * 0.85f), Size(r * 1.5f, r * 1.6f))
                drawRect(fondo, Offset(x - r * 0.85f, y - r * 0.65f), Size(r * 1.5f, r * 1.6f))
                drawRect(col, Offset(x - r * 0.85f, y - r * 0.65f), Size(r * 1.5f, r * 1.6f), style = Stroke(0.8f * d))
                listOf(-0.3f, 0f, 0.3f, 0.6f).forEach { f -> drawLine(col, Offset(x - r * 0.65f, y + f * r), Offset(x + r * 0.45f, y + f * r), 0.6f * d) }
            }
            // Jarra de barro.
            "taza" -> {
                drawPath(Path().apply {
                    moveTo(x - r * 0.35f, y - r * 0.8f); lineTo(x + r * 0.35f, y - r * 0.8f); quadraticTo(x + r * 0.85f, y + r * 0.1f, x + r * 0.4f, y + r * 0.85f)
                    lineTo(x - r * 0.4f, y + r * 0.85f); quadraticTo(x - r * 0.85f, y + r * 0.1f, x - r * 0.35f, y - r * 0.8f); close()
                }, col)
                drawPath(Path().apply { moveTo(x - r * 0.35f, y - r * 0.8f); lineTo(x - r * 0.6f, y - r * 0.95f); lineTo(x - r * 0.3f, y - r * 0.6f); close() }, col)
                drawArc(col, -80f, 160f, false, Offset(x + r * 0.35f, y - r * 0.55f), Size(r * 0.55f, r * 0.8f), style = Stroke(r * 0.14f))
            }
            else -> return false
        }
        TemaId.ANTIGUAS -> when (forma) {
            // Gema tallada.
            "estrella" -> {
                drawPath(Path().apply { moveTo(x - r * 0.45f, y - r * 0.55f); lineTo(x + r * 0.45f, y - r * 0.55f); lineTo(x + r * 0.85f, y - r * 0.1f); lineTo(x, y + r * 0.9f); lineTo(x - r * 0.85f, y - r * 0.1f); close() }, col)
                drawLine(fondo, Offset(x - r * 0.85f, y - r * 0.1f), Offset(x + r * 0.85f, y - r * 0.1f), 0.6f * d)
                drawLine(fondo, Offset(x - r * 0.2f, y - r * 0.1f), Offset(x, y + r * 0.9f), 0.5f * d); drawLine(fondo, Offset(x + r * 0.2f, y - r * 0.1f), Offset(x, y + r * 0.9f), 0.5f * d)
            }
            // Montañas del mapa, con nieve en la cumbre.
            "alfiler" -> {
                drawPath(Path().apply { moveTo(x - r, y + r * 0.8f); lineTo(x - r * 0.2f, y - r * 0.85f); lineTo(x + r * 0.2f, y - r * 0.05f); lineTo(x + r * 0.5f, y - r * 0.4f); lineTo(x + r, y + r * 0.8f); close() }, col)
                drawPath(Path().apply { moveTo(x - r * 0.2f, y - r * 0.85f); lineTo(x - r * 0.04f, y - r * 0.52f); lineTo(x - r * 0.36f, y - r * 0.52f); close() }, fondo)
            }
            // Jarra con tapa.
            "taza" -> {
                drawRect(col, Offset(x - r * 0.55f, y - r * 0.5f), Size(r * 0.95f, r * 1.35f))
                drawRect(col, Offset(x - r * 0.62f, y - r * 0.75f), Size(r * 1.1f, r * 0.2f))
                drawArc(col, -80f, 160f, false, Offset(x + r * 0.15f, y - r * 0.35f), Size(r * 0.6f, r * 0.8f), style = Stroke(r * 0.14f))
                listOf(-0.2f, 0.5f).forEach { f -> drawLine(fondo, Offset(x - r * 0.55f, y + f * r), Offset(x + r * 0.4f, y + f * r), 0.7f * d) }
            }
            else -> return false
        }
    }
    return true
}

/** Los objetos comunes: luna, huella, alfiler, pesa, taza, estrella y sobre. */
private fun DrawScope.objetoComun(forma: String, c: Offset, r: Float, col: Color, fondo: Color) {
    val d = density
    when (forma) {
        "luna" -> { drawCircle(col, r, c); drawCircle(fondo, r * 0.85f, Offset(c.x + r * 0.45f, c.y - r * 0.2f)) }
        "huella" -> { drawOval(col, Offset(c.x - r * 0.45f, c.y - r * 0.9f), Size(r * 0.9f, r * 1.3f)); drawOval(col, Offset(c.x - r * 0.35f, c.y + r * 0.5f), Size(r * 0.7f, r * 0.5f)) }
        "alfiler" -> { drawCircle(col, r * 0.55f, Offset(c.x, c.y - r * 0.35f)); drawLine(col, Offset(c.x, c.y - r * 0.2f), Offset(c.x, c.y + r), 1.4f * d) }
        "pesa" -> { drawRect(col, Offset(c.x - r, c.y - r * 0.6f), Size(r * 0.45f, r * 1.2f)); drawRect(col, Offset(c.x + r * 0.55f, c.y - r * 0.6f), Size(r * 0.45f, r * 1.2f)); drawLine(col, Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y), 1.6f * d) }
        "taza" -> { drawRoundRect(col, Offset(c.x - r * 0.7f, c.y - r * 0.6f), Size(r * 1.2f, r * 1.3f), CornerRadius(r * 0.25f)); drawCircle(col, r * 0.35f, Offset(c.x + r * 0.55f, c.y), style = Stroke(1.2f * d)) }
        "estrella" -> drawPath(estrellaCosa(c, r, 5, 0.45f), col)
        else -> { drawRect(col, Offset(c.x - r, c.y - r * 0.65f), Size(r * 2, r * 1.3f)); drawPath(Path().apply { moveTo(c.x - r, c.y - r * 0.65f); lineTo(c.x, c.y + r * 0.1f); lineTo(c.x + r, c.y - r * 0.65f) }, fondo, style = Stroke(1.2f * d)) }
    }
}
