package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los mapas de campaña de los temas históricos. Roma marcha por la calzada hacia las Galias, Grecia navega el Egeo,
 * el scriptorium camina a Compostela con su venera, la catedral se levanta etapa por etapa, el cuadro de Florencia
 * se pinta día por día, el Imperio va de Boulogne a Austerlitz cruzando el Rin y el Danubio, y la Revolución recorre
 * sus jornadas por París, a orillas del Sena.
 */
object MapasCampana {
    val TEMAS = TrofeosTema.TEMAS

    /** El fondo del mapa. [avance] va de 0 a 1 (días cumplidos de los siete). */
    fun DrawScope.fondo(tema: TemaId, cs: ColorScheme, avance: Float) {
        val oscuro = cs.background.luminance() < 0.5f
        val w = size.width; val h = size.height; val d = density
        val tinta = cs.onSurfaceVariant.copy(alpha = 0.22f)
        val agua = Color(0xFF4F86B8).copy(alpha = if (oscuro) 0.28f else 0.2f)
        when (tema) {
            TemaId.ROMANO -> {
                // El mar al sur, los Alpes en medio y la retícula de las centurias.
                drawPath(Path().apply { moveTo(0f, h); lineTo(0f, h * 0.7f); cubicTo(w * 0.12f, h * 0.62f, w * 0.2f, h * 0.9f, w * 0.34f, h * 0.82f); lineTo(w * 0.42f, h); close() }, agua)
                for (k in 0 until 5) { val x = w * (0.38f + k * 0.05f); drawPath(Path().apply { moveTo(x - 7 * d, h * 0.2f); lineTo(x, h * 0.08f); lineTo(x + 7 * d, h * 0.2f) }, tinta, style = Stroke(1.2f * d)) }
                var x = 0f; while (x < w) { drawLine(tinta.copy(alpha = 0.08f), Offset(x, 0f), Offset(x, h), 1f); x += 22 * d }
            }
            TemaId.HELENO -> {
                drawRect(Brush.verticalGradient(listOf(agua, agua.copy(alpha = agua.alpha * 0.5f))), Offset.Zero, size)
                val az = java.util.Random(7)
                repeat(9) { val c = Offset(az.nextFloat() * w, az.nextFloat() * h); drawOval(Color(0xFFE6D3A6).copy(alpha = if (oscuro) 0.3f else 0.6f), Offset(c.x - 9 * d, c.y - 5 * d), Size(18 * d * (0.6f + az.nextFloat()), 10 * d)) }
                repeat(14) { val c = Offset(az.nextFloat() * w, az.nextFloat() * h); drawArc(Color.White.copy(alpha = 0.35f), 200f, 140f, false, Offset(c.x - 5 * d, c.y - 3 * d), Size(10 * d, 6 * d), style = Stroke(1f * d)) }
            }
            TemaId.MEDIEVAL -> {
                // Pergamino con montes y la rosa de los vientos.
                for (k in 0 until 4) { val x = w * (0.2f + k * 0.18f); drawArc(tinta, 200f, 140f, false, Offset(x, h * 0.8f), Size(26 * d, 18 * d), style = Stroke(1.2f * d)) }
                val c = Offset(w * 0.92f, h * 0.2f); val r = 12 * d
                for (k in 0 until 8) { val a = k * PI / 4; val rr = if (k % 2 == 0) r else r * 0.5f; drawLine(tinta.copy(alpha = 0.4f), c, Offset(c.x + (rr * cos(a)).toFloat(), c.y + (rr * sin(a)).toFloat()), 1.2f * d) }
            }
            TemaId.GOTICO -> {
                // La catedral se levanta con los días cumplidos: la silueta completa tenue y lo construido con más fuerza.
                val silueta = catedral(w, h)
                drawPath(silueta, tinta.copy(alpha = 0.08f))
                drawPath(silueta, tinta.copy(alpha = 0.25f), style = Stroke(1f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 3 * d))))
                val alto = h * avance.coerceIn(0f, 1f)
                clipRect(0f, h - alto, w, h) { drawPath(silueta, cs.primary.copy(alpha = if (oscuro) 0.3f else 0.18f)) }
            }
            TemaId.RENACIMIENTO -> {
                // El cuadro sobre el caballete: primero la retícula del boceto, luego el color que va entrando.
                val r = Rect(w * 0.3f, h * 0.08f, w * 0.7f, h * 0.92f)
                drawRect(Color(0xFF9A7A50).copy(alpha = 0.35f), r.topLeft, r.size, style = Stroke(3 * d))
                for (k in 1..3) { drawLine(tinta.copy(alpha = 0.18f), Offset(r.left + r.width * k / 4, r.top), Offset(r.left + r.width * k / 4, r.bottom), 1f); drawLine(tinta.copy(alpha = 0.18f), Offset(r.left, r.top + r.height * k / 4), Offset(r.right, r.top + r.height * k / 4), 1f) }
                val colores = listOf(Color(0xFF8C3B1E), Color(0xFF2F4F6F), Color(0xFF9C7A2E), Color(0xFF5E7A4A))
                val manchas = (avance * 8).toInt()
                val az = java.util.Random(3)
                repeat(manchas) { k -> val c = Offset(r.left + az.nextFloat() * r.width, r.top + az.nextFloat() * r.height); drawCircle(colores[k % 4].copy(alpha = 0.22f), r.width * (0.12f + az.nextFloat() * 0.1f), c) }
            }
            TemaId.NAPOLEON -> {
                // El Rin y el Danubio, y las fronteras punteadas del teatro de operaciones.
                drawPath(Path().apply { moveTo(w * 0.24f, 0f); cubicTo(w * 0.2f, h * 0.3f, w * 0.3f, h * 0.6f, w * 0.22f, h) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2.4f * d))
                drawPath(Path().apply { moveTo(w * 0.3f, h * 0.9f); cubicTo(w * 0.5f, h * 0.8f, w * 0.6f, h * 0.95f, w * 0.8f, h * 0.78f); lineTo(w, h * 0.72f) }, agua.copy(alpha = agua.alpha * 2), style = Stroke(2.4f * d))
                drawPath(Path().apply { moveTo(w * 0.1f, h * 0.05f); lineTo(w * 0.4f, h * 0.15f); lineTo(w * 0.62f, h * 0.05f); lineTo(w * 0.95f, h * 0.2f) }, tinta, style = Stroke(1f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(5 * d, 2 * d, 1 * d, 2 * d))))
            }
            TemaId.REVOLUCION -> {
                // París: el Sena en curva y la cuadrícula de las secciones.
                var x = 0f; while (x < w) { drawLine(tinta.copy(alpha = 0.1f), Offset(x, 0f), Offset(x, h), 1f); x += 18 * d }
                var y = 0f; while (y < h) { drawLine(tinta.copy(alpha = 0.1f), Offset(0f, y), Offset(w, y), 1f); y += 18 * d }
                drawPath(Path().apply { moveTo(0f, h * 0.48f); cubicTo(w * 0.25f, h * 0.2f, w * 0.45f, h * 0.85f, w * 0.7f, h * 0.5f); cubicTo(w * 0.82f, h * 0.32f, w * 0.9f, h * 0.5f, w, h * 0.44f) },
                    agua.copy(alpha = agua.alpha * 2.2f), style = Stroke(5 * d, cap = StrokeCap.Round))
            }
            else -> {}
        }
    }

    private fun DrawScope.catedral(w: Float, h: Float): Path = Path().apply {
        val cx = w / 2; val b = h * 0.98f
        // Dos torres con aguja y la nave con su rosetón y el portal.
        moveTo(cx - w * 0.2f, b); lineTo(cx - w * 0.2f, h * 0.3f); lineTo(cx - w * 0.16f, h * 0.06f); lineTo(cx - w * 0.12f, h * 0.3f)
        lineTo(cx - w * 0.12f, h * 0.42f); lineTo(cx, h * 0.24f); lineTo(cx + w * 0.12f, h * 0.42f)
        lineTo(cx + w * 0.12f, h * 0.3f); lineTo(cx + w * 0.16f, h * 0.06f); lineTo(cx + w * 0.2f, h * 0.3f); lineTo(cx + w * 0.2f, b); close()
        addOval(Rect(Offset(cx, h * 0.55f), h * 0.11f))
    }

    /** El camino entre etapas. */
    fun DrawScope.ruta(tema: TemaId, cs: ColorScheme, p: Path) {
        val d = density
        when (tema) {
            // La calzada: borde de piedra y losas.
            TemaId.ROMANO -> { drawPath(p, Color(0xFF8C8478).copy(alpha = 0.7f), style = Stroke(5 * d)); drawPath(p, cs.surface, style = Stroke(3 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3 * d, 1.2f * d)))) }
            // La ruta de la nave, en olas punteadas.
            TemaId.HELENO -> drawPath(p, Color.White.copy(alpha = 0.85f), style = Stroke(1.6f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 4 * d))))
            TemaId.MEDIEVAL -> drawPath(p, Color(0xFF6B2E1A).copy(alpha = 0.7f), style = Stroke(1.4f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 3 * d))))
            TemaId.GOTICO -> drawPath(p, cs.tertiary.copy(alpha = 0.7f), style = Stroke(1.6f * d))
            // Un trazo de sanguina, con el lápiz de la construcción debajo.
            TemaId.RENACIMIENTO -> { drawPath(p, cs.onSurfaceVariant.copy(alpha = 0.25f), style = Stroke(0.8f * d)); drawPath(p, Color(0xFF8C3B1E).copy(alpha = 0.8f), style = Stroke(2 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10 * d, 3 * d)))) }
            // La flecha roja de los mapas de campaña, rayada.
            TemaId.NAPOLEON -> { drawPath(p, Color(0xFFA3172E).copy(alpha = 0.35f), style = Stroke(7 * d)); drawPath(p, Color(0xFFA3172E), style = Stroke(1.6f * d)) }
            // El cortejo: tres líneas, azul, blanca y roja.
            TemaId.REVOLUCION -> {
                drawPath(p, Color(0xFF1F3A6E), style = Stroke(6 * d)); drawPath(p, Color.White, style = Stroke(4 * d)); drawPath(p, Color(0xFFB22234), style = Stroke(2 * d))
            }
            else -> drawPath(p, cs.outline, style = Stroke(2 * d))
        }
    }

    /** La marca de cada etapa (la de la meta un poco más grande). */
    fun DrawScope.etapa(tema: TemaId, cs: ColorScheme, c: Offset, r: Float, col: Color, meta: Boolean) {
        val d = density
        when (tema) {
            // Miliario: una columnita con su base.
            TemaId.ROMANO -> { drawRect(col, Offset(c.x - r * 0.55f, c.y - r), Size(r * 1.1f, r * 1.8f)); drawOval(col, Offset(c.x - r * 0.55f, c.y - r * 1.25f), Size(r * 1.1f, r * 0.5f)); drawRect(col, Offset(c.x - r * 0.8f, c.y + r * 0.7f), Size(r * 1.6f, r * 0.35f)) }
            // Ánfora-isla: un círculo con la greca alrededor.
            TemaId.HELENO -> { drawCircle(col, r, c); drawCircle(Color(0xFFE6D3A6), r * 1.25f, c, style = Stroke(1.2f * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * d, 1.5f * d)))) }
            // La venera del peregrino.
            TemaId.MEDIEVAL -> {
                val p = Path().apply { moveTo(c.x, c.y + r); for (k in 0..6) { val a = PI + k * PI / 6; val x = c.x + (r * 1.15f * cos(a)).toFloat(); val y = c.y - r * 0.1f + (r * 1.1f * sin(a)).toFloat(); lineTo(x, y) }; close() }
                drawPath(p, col)
                for (k in 1..5) { val a = PI + k * PI / 6; drawLine(Color.Black.copy(alpha = 0.25f), Offset(c.x, c.y + r * 0.9f), Offset(c.x + (r * 1.05f * cos(a)).toFloat(), c.y - r * 0.1f + (r * sin(a)).toFloat()), 0.8f * d) }
            }
            // Un arco apuntado.
            TemaId.GOTICO -> drawPath(Path().apply { moveTo(c.x - r * 0.8f, c.y + r); lineTo(c.x - r * 0.8f, c.y); quadraticTo(c.x - r * 0.8f, c.y - r * 0.8f, c.x, c.y - r * 1.3f); quadraticTo(c.x + r * 0.8f, c.y - r * 0.8f, c.x + r * 0.8f, c.y); lineTo(c.x + r * 0.8f, c.y + r); close() }, col)
            // Un punto de compás con su cruz de construcción.
            TemaId.RENACIMIENTO -> { drawCircle(col, r, c); drawLine(Color(0xFF3A2614).copy(alpha = 0.5f), Offset(c.x - r * 1.6f, c.y), Offset(c.x + r * 1.6f, c.y), 0.8f * d); drawLine(Color(0xFF3A2614).copy(alpha = 0.5f), Offset(c.x, c.y - r * 1.6f), Offset(c.x, c.y + r * 1.6f), 0.8f * d) }
            // El banderín de la división; en la meta, el sol de Austerlitz.
            TemaId.NAPOLEON -> if (meta) {
                for (k in 0 until 12) { val a = k * PI / 6; drawLine(Color(0xFFD4AF37), Offset(c.x + (r * 1.1f * cos(a)).toFloat(), c.y + (r * 1.1f * sin(a)).toFloat()), Offset(c.x + (r * 1.7f * cos(a)).toFloat(), c.y + (r * 1.7f * sin(a)).toFloat()), 1.2f * d) }
                drawCircle(col, r, c)
            } else { drawRect(col, Offset(c.x - r, c.y - r * 0.7f), Size(r * 2, r * 1.4f)); drawLine(cs.onSurface.copy(alpha = 0.6f), Offset(c.x - r, c.y - r * 0.7f), Offset(c.x - r, c.y + r * 1.4f), 1f * d) }
            // La escarapela: azul afuera, el color del día en medio y rojo al centro.
            TemaId.REVOLUCION -> { drawCircle(Color(0xFF1F3A6E), r * 1.15f, c); drawCircle(col, r * 0.8f, c); drawCircle(Color(0xFFB22234), r * 0.38f, c) }
            else -> drawCircle(col, r, c)
        }
    }

    /** Lo que se ve en la meta cuando se gana: la corona, el templo, la catedral, la firma, el sol, la federación. */
    fun DrawScope.victoria(tema: TemaId, cs: ColorScheme, m: Offset) {
        val d = density
        val oro = Color(0xFFD4AF37)
        when (tema) {
            TemaId.ROMANO, TemaId.HELENO -> for (lado in listOf(-1f, 1f)) for (k in 0 until 5) {
                val a = (-PI / 2 + lado * (0.5 + k * 0.42)).toFloat(); val o = Offset(m.x + 15 * d * cos(a), m.y - 4 * d + 15 * d * sin(a))
                drawOval(oro, Offset(o.x - 3 * d, o.y - 1.5f * d), Size(6 * d, 3 * d))
            }
            TemaId.MEDIEVAL, TemaId.GOTICO -> { drawLine(cs.onSurface, m, m + Offset(0f, -16 * d), 1f * d); drawLine(cs.onSurface, m + Offset(-5 * d, -11 * d), m + Offset(5 * d, -11 * d), 1f * d) }
            TemaId.RENACIMIENTO -> drawPath(Path().apply { moveTo(m.x - 12 * d, m.y + 14 * d); cubicTo(m.x - 4 * d, m.y + 6 * d, m.x + 2 * d, m.y + 20 * d, m.x + 12 * d, m.y + 10 * d) }, Color(0xFF8C3B1E), style = Stroke(1.2f * d))
            TemaId.NAPOLEON -> drawCircle(oro.copy(alpha = 0.35f), 20 * d, m)
            TemaId.REVOLUCION -> {
                drawLine(cs.onSurface, m, m + Offset(0f, -18 * d), 1f * d)
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, c -> drawRect(c, Offset(m.x + i * 4 * d, m.y - 18 * d), Size(4 * d, 9 * d)) }
            }
            else -> {}
        }
    }
}
