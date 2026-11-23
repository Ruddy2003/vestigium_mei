package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.ALMAGRE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.BERMELLON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.HOJALATA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.LOZA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.NOGAL_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.PAPEL_VIEJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.TIZNE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaVapor.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Más cosas para los huecos libres de Vapor ([HuecosEstanteVapor]), grandes, para ir en grupo: el haz de planos enrollados de
 * pie, atado con su cinta; el manómetro de repuesto en su caja de madera (la esfera sin cifras, con sus marcas y la aguja); la
 * aceitera de maquinista, de hojalata, alta y de pico largo y fino, con su asa; y la pila de guías de la línea atadas en cruz con
 * cinta. Todas con su lado de luz, su sombra en la pared del fondo y su sombra de contacto.
 */
internal object CosasEstanteVapor {

    /** El haz de planos enrollados, de pie: tres o cuatro rollos de distinto alto y papel, atados con una cinta. */
    fun DrawScope.rollos(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val h = alto * 0.86f
        val r = h * 0.075f
        val papeles = listOf(PAPEL, Color(0xFF41709A), PAPEL_VIEJO, Color(0xFFE2D2AA))
        val xs = listOf(-1.5f, -0.3f, 0.9f, 2.0f)
        val altos = listOf(0.82f, 1f, 0.9f, 0.7f)
        // La sombra del haz en la pared del fondo.
        drawRect(a.sombra(0.3f), Offset(cx - r * 2.2f - hacia * r * 1.4f, pie - h), Size(r * 5.4f, h))
        contactoVapor(Offset(cx, pie), r * 6.5f, r * 0.9f, a, 0.42f, -hacia)
        xs.forEachIndexed { i, dx ->
            val x = cx + dx * r * 1.1f
            val top = pie - h * altos[i]
            val col = a.luz(papeles[i], k, f, br)
            drawRect(col, Offset(x - r, top), Size(r * 2f, pie - top))
            drawRect(a.brillo(0.35f, maxOf(k, 0.3f), br), Offset(x - r * (if (hacia < 0) 0.7f else -0.3f), top), Size(r * 0.35f, pie - top))
            drawRect(a.sombra(0.28f), Offset(x + r * (if (hacia < 0) 0.35f else -1f), top), Size(r * 0.65f, pie - top))
            // La boca del rollo, con su espiral.
            val boca = Rect(x - r, top - r * 0.4f, x + r, top + r * 0.4f)
            drawOval(a.luz(mezcla(papeles[i], Color.White, 0.25f), k, f, br), boca.topLeft, boca.size)
            drawArc(a.sombra(0.45f), 10f, 300f, false, boca.deflate(r * 0.25f).topLeft, boca.deflate(r * 0.25f).size, style = Stroke(0.7f * u))
            drawArc(a.sombra(0.35f), 60f, 260f, false, boca.deflate(r * 0.55f).topLeft, boca.deflate(r * 0.55f).size, style = Stroke(0.6f * u))
            // El canto del papel enrollado, en diagonal.
            drawLine(a.sombra(0.25f), Offset(x - r, top + h * 0.25f), Offset(x + r, top + h * 0.18f), 0.6f * u)
        }
        // La cinta que los ata, con su lazo.
        val yc = pie - h * 0.42f
        drawRect(a.luz(BERMELLON, k, f, br), Offset(cx - r * 2.7f, yc), Size(r * 5.4f, r * 0.45f))
        drawRect(a.sombra(0.3f), Offset(cx - r * 2.7f, yc + r * 0.35f), Size(r * 5.4f, r * 0.12f))
        drawOval(a.luz(BERMELLON, k, f, br), Offset(cx - r * 0.2f, yc - r * 0.55f), Size(r * 0.9f, r * 0.6f), style = Stroke(1f * u))
        drawLine(a.luz(BERMELLON, k, f, br), Offset(cx + r * 0.2f, yc + r * 0.4f), Offset(cx + r * 0.6f, yc + r * 1.6f), 1.2f * u)
    }

    /** El manómetro de repuesto en su caja de madera abierta: la esfera de loza sin cifras, sus marcas, la aguja y el aro de latón. */
    fun DrawScope.manometro(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val s = alto * 0.5f
        val caja = Rect(cx - s * 0.55f, pie - s, cx + s * 0.55f, pie)
        drawRect(a.sombra(0.32f), Offset(caja.left - hacia * s * 0.12f, caja.top + s * 0.04f), caja.size)
        contactoVapor(Offset(cx, pie), s * 1.3f, s * 0.08f, a, 0.4f, -hacia)
        drawRect(a.luz(NOGAL_LUZ, k, f, br), caja.topLeft, caja.size)
        drawRect(a.luz(NOGAL, k, f, br), caja.deflate(s * 0.07f).topLeft, caja.deflate(s * 0.07f).size)
        drawRect(a.brillo(0.45f, maxOf(k, 0.3f), br), caja.topLeft, Size(caja.width, 0.9f * u))
        drawRect(a.sombra(0.35f), Offset(caja.left, caja.bottom - 1.2f * u), Size(caja.width, 1.2f * u))
        // La tapa abierta, apoyada en la pared, detrás.
        val c = Offset(cx, caja.center.y)
        val r = s * 0.36f
        drawCircle(a.sombra(0.35f), r * 1.12f, c + Offset(r * 0.1f, r * 0.12f))
        drawCircle(a.luz(LATON, k, f, br), r * 1.1f, c)
        drawArc(a.brillo(0.7f, maxOf(k, 0.3f), br), 190f, 90f, false, Offset(c.x - r * 1.05f, c.y - r * 1.05f), Size(r * 2.1f, r * 2.1f), style = Stroke(1f * u))
        drawCircle(a.luz(LOZA, k, f, br), r * 0.95f, c)
        // Las marcas del arco de la esfera (sin cifras) y la franja roja del límite.
        val marcas = Path()
        for (i in 0..8) {
            val t = Math.toRadians(135.0 + 270.0 * i / 8)
            val dx = kotlin.math.cos(t).toFloat(); val dy = kotlin.math.sin(t).toFloat()
            marcas.moveTo(c.x + dx * r * 0.62f, c.y + dy * r * 0.62f); marcas.lineTo(c.x + dx * r * 0.8f, c.y + dy * r * 0.8f)
        }
        drawPath(marcas, a.luz(NEGRO, k, f, br), style = Stroke(0.8f * u))
        drawArc(a.luz(BERMELLON, k, f, br), 10f, 35f, false, Offset(c.x - r * 0.72f, c.y - r * 0.72f), Size(r * 1.44f, r * 1.44f), style = Stroke(r * 0.1f))
        drawLine(a.luz(NEGRO, k, f, br), c, c + Offset(r * 0.5f, -r * 0.4f), 1.1f * u, StrokeCap.Round)
        drawCircle(a.luz(LATON, k, f, br), r * 0.1f, c)
        drawArc(Color.White.copy(alpha = if (a.oscuro) 0.4f * k else 0.5f), 200f, 60f, false, Offset(c.x - r * 0.8f, c.y - r * 0.8f), Size(r * 1.6f, r * 1.6f), style = Stroke(1.2f * u))
        // El sifón de cobre que asoma por abajo.
        drawLine(a.luz(PaletaVapor.COBRE, k, f, br), Offset(c.x, c.y + r * 1.1f), Offset(c.x, caja.bottom - s * 0.07f), 1.6f * u)
    }

    /** La aceitera de maquinista: el cuerpo alto de hojalata, la tapa con su tapón, el asa de lado y el pico largo y fino. */
    fun DrawScope.aceitera(cx: Float, pie: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float) {
        val u = density
        val h = alto * 0.46f
        val rw = h * 0.3f
        val cuerpo = Path().apply {
            moveTo(cx - rw, pie); lineTo(cx + rw, pie); lineTo(cx + rw * 0.85f, pie - h); lineTo(cx - rw * 0.85f, pie - h); close()
        }
        drawPath(corridoVapor(cuerpo, -hacia * rw * 0.7f, -h * 0.03f), a.sombra(0.3f))
        contactoVapor(Offset(cx, pie), rw * 3f, rw * 0.4f, a, 0.4f, -hacia)
        // El pico, del pie del cuerpo hacia arriba, largo y fino, con su gota.
        val p0 = Offset(cx + rw * 0.6f, pie - h * 0.25f); val p1 = Offset(cx + rw * 2.6f, pie - alto * 0.74f)
        drawLine(a.luz(mezcla(HOJALATA, LATON, 0.3f), k, f, br), p0, p1, rw * 0.22f, StrokeCap.Round)
        drawLine(a.brillo(0.45f, maxOf(k, 0.3f), br), p0 - Offset(rw * 0.08f, 0f), p1 - Offset(rw * 0.08f, 0f), 0.6f * u)
        drawCircle(a.luz(Color(0xFF6B4A1C), k, f, br), rw * 0.1f, p1 + Offset(0f, rw * 0.2f))
        drawPath(cuerpo, a.luz(HOJALATA, k, f, br))
        clipPath(cuerpo) {
            drawRect(a.brillo(0.45f, maxOf(k, 0.3f), br), Offset(cx - rw * (if (hacia < 0) 0.6f else -0.3f), pie - h), Size(rw * 0.3f, h))
            drawRect(a.sombra(0.28f), Offset(cx + rw * (if (hacia < 0) 0.35f else -1f), pie - h), Size(rw * 0.65f, h))
            drawRect(a.grano(TIZNE, 0.25f), Offset(cx - rw, pie - h * 0.18f), Size(rw * 2f, h * 0.18f))
            listOf(0.35f, 0.7f).forEach { fy -> drawRect(a.sombra(0.3f), Offset(cx - rw, pie - h * fy), Size(rw * 2f, 0.8f * u)) }
        }
        // La tapa con su tapón y el asa de lado.
        drawOval(a.luz(mezcla(HOJALATA, PAPEL, 0.2f), k, f, br), Offset(cx - rw * 0.85f, pie - h - rw * 0.2f), Size(rw * 1.7f, rw * 0.4f))
        drawRect(a.luz(LATON, k, f, br), Offset(cx - rw * 0.25f, pie - h - rw * 0.55f), Size(rw * 0.5f, rw * 0.4f))
        drawArc(a.luz(HIERRO, k, f, br), 90f, 180f, false, Offset(cx - rw * 1.55f, pie - h * 0.85f), Size(rw * 1.4f, h * 0.55f), style = Stroke(1.4f * u))
    }

    /** La pila de guías de la línea, cuadernillos de tapas de colores, atados en cruz con una cinta, y el lazo. */
    fun DrawScope.guias(cx: Float, pie: Float, ancho: Float, alto: Float, a: AireVapor, k: Float, f: Float, br: Float, hacia: Float): Float {
        val u = density
        val l = minOf(ancho * 0.85f, alto * 0.75f)
        val tapas = listOf(Color(0xFF7A5A2E), ALMAGRE, Color(0xFF2E4566), VERDE, Color(0xFFB08A34), ALMAGRE)
        var y = pie
        contactoVapor(Offset(cx, pie), l * 1.15f, l * 0.08f, a, 0.4f, -hacia)
        drawRect(a.sombra(0.3f), Offset(cx - l / 2 - hacia * l * 0.08f, pie - alto * 0.34f), Size(l, alto * 0.34f))
        tapas.forEachIndexed { i, col ->
            val grueso = alto * (0.05f + 0.008f * (i % 3))
            val dx = l * 0.03f * (if (i % 2 == 0) 1f else -1f)
            val b = Rect(cx - l / 2 + dx, y - grueso, cx + l / 2 + dx, y)
            drawRect(a.luz(col, k, f, br), b.topLeft, b.size)
            val hojas = Rect(b.left + l * 0.015f, b.top + grueso * 0.3f, b.right - l * 0.01f, b.bottom - grueso * 0.25f)
            drawRect(a.luz(PAPEL_VIEJO, k, f, br), hojas.topLeft, hojas.size)
            drawRect(a.brillo(0.4f, maxOf(k, 0.3f), br), b.topLeft, Size(b.width, 0.8f * u))
            drawRect(a.sombra(0.3f), Offset(b.left, b.bottom - 0.8f * u), Size(b.width, 0.8f * u))
            y -= grueso
        }
        // La cinta en cruz: la vertical por delante, con el lazo arriba.
        val cinta = a.luz(BERMELLON, k, f, br)
        drawRect(cinta, Offset(cx - l * 0.03f, y), Size(l * 0.06f, pie - y))
        drawRect(a.sombra(0.25f), Offset(cx + l * 0.02f, y), Size(l * 0.012f, pie - y))
        drawOval(cinta, Offset(cx - l * 0.12f, y - l * 0.05f), Size(l * 0.11f, l * 0.06f), style = Stroke(1.1f * u))
        drawOval(cinta, Offset(cx + l * 0.01f, y - l * 0.05f), Size(l * 0.11f, l * 0.06f), style = Stroke(1.1f * u))
        drawLine(cinta, Offset(cx, y), Offset(cx + l * 0.08f, y + l * 0.12f), 1.1f * u)
        return y
    }
}
