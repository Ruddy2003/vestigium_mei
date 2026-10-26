package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Los halls del lote 2, cada uno enmarcando las columnas de piezas: el patio andalusí de arcos de herradura con celosías,
 * el caravasar con ventanas lunares y farolillos, la gran yurta con su enrejado y los postes del techo, el almacén de los
 * colonos con tablones y repisas, el invernadero de cristal y hierro, la sala de Navidad con chimenea, árbol y adornos
 * que cuelgan, y el gran salón de piedra de Tierras Antiguas con pilares y estandartes.
 */
object SalonesMas {
    fun DrawScope.escenaMas(tema: TemaId, cs: ColorScheme, oscuro: Boolean, n: Int) {
        when (tema) {
            TemaId.ANDALUS -> patio(cs, oscuro, n)
            TemaId.SEDA -> caravasar(cs, oscuro, n)
            TemaId.MONGOL -> yurta(cs, oscuro, n)
            TemaId.COLONOS -> almacen(cs, oscuro, n)
            TemaId.BOTANICO -> invernadero(cs, oscuro, n)
            TemaId.NAVIDAD -> chimenea(cs, oscuro, n)
            TemaId.ANTIGUAS -> salonPiedra(cs, oscuro, n)
            else -> {}
        }
    }

    fun DrawScope.pedestalMas(tema: TemaId, cs: ColorScheme, metal: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.2f), c, lerp(c, Color.Black, 0.25f)), 0f, h)
        when (tema) {
            TemaId.ANDALUS -> {
                drawRect(piedra(Color(0xFFEDE3CF)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h))
                for (k in 0 until 5) { val cx = w * (0.22f + k * 0.14f); val r = h * 0.32f
                    drawPath(Path().apply { moveTo(cx, h / 2 - r); lineTo(cx + r, h / 2); lineTo(cx, h / 2 + r); lineTo(cx - r, h / 2); close() }, if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A)) }
            }
            TemaId.SEDA -> { drawRoundRect(piedra(Color(0xFF8E1B1B)), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.55f), CornerRadius(h * 0.2f)); drawRect(Color(0xFFD4AF37), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.1f))
                listOf(0.2f, 0.8f).forEach { x -> drawRect(Color(0xFF8E1B1B), Offset(w * x - w * 0.03f, h * 0.5f), Size(w * 0.06f, h * 0.5f)) } }
            TemaId.MONGOL -> for (k in 0 until 3) drawRoundRect(lerp(Color(0xFFEDE6D6), Color.Black, k * 0.08f), Offset(w * (0.18f - k * 0.04f), h * k / 3f), Size(w * (0.64f + k * 0.08f), h / 3f), CornerRadius(h * 0.15f))
            TemaId.COLONOS -> { drawRect(Brush.horizontalGradient(listOf(Color(0xFF5A3A22), Color(0xFF8A5A2B), Color(0xFF5A3A22)), w * 0.2f, w * 0.8f), Offset(w * 0.2f, h * 0.2f), Size(w * 0.6f, h * 0.8f)); drawOval(Color(0xFFC9A57A), Offset(w * 0.2f, 0f), Size(w * 0.6f, h * 0.4f)) }
            TemaId.BOTANICO -> { drawRect(Color(0xFF8A6A44), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.3f)); listOf(0.2f, 0.8f).forEach { x -> drawLine(Color(0xFF2F3A33), Offset(w * x, h * 0.3f), Offset(w * x, h), w * 0.03f) } }
            TemaId.NAVIDAD -> {
                // Una rama de acebo con sus bayas, de la que «cuelga» el adorno.
                listOf(-1f, 1f).forEach { s -> drawPath(Path().apply { moveTo(w / 2, h * 0.5f); quadraticTo(w * (0.5f + s * 0.16f), 0f, w * (0.5f + s * 0.34f), h * 0.4f); quadraticTo(w * (0.5f + s * 0.16f), h * 0.8f, w / 2, h * 0.5f); close() }, Color(0xFF1F6B3A)) }
                listOf(-0.05f, 0f, 0.05f).forEach { dx -> drawCircle(Color(0xFFB3122E), h * 0.16f, Offset(w * (0.5f + dx), h * 0.5f)) }
            }
            TemaId.ANTIGUAS -> { drawRect(piedra(Color(0xFF7D7A72)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h)); with(TrofeosMas) { runas(Offset(w * 0.22f, h * 0.5f), w * 0.56f, h * 0.5f, Color(0xFF2B2A28).copy(alpha = 0.5f), h * 0.06f, 6) } }
            else -> drawRect(metal, Offset(0f, 0f), Size(w, h))
        }
    }

    // ------------------------------------------------------------------ Al-Ándalus

    /** El patio: un arco de herradura sobre cada columna de piezas, celosía en las enjutas, zócalo de azulejo y la acequia. */
    private fun DrawScope.patio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val yeso = (if (oscuro) Color(0xFF3B3A44) else Color(0xFFEDE3CF)).copy(alpha = 0.7f)
        val oro = Color(0xFFC9A04A).copy(alpha = 0.55f)
        val paso = w / n
        // Zócalo de azulejo abajo.
        val zocalo = 24 * d
        var x = 0f; var k = 0
        while (x < w) { val c = Offset(x + 6 * d, h - zocalo / 2); val r = 6 * d
            drawPath(Path().apply { moveTo(c.x, c.y - r); lineTo(c.x + r, c.y); lineTo(c.x, c.y + r); lineTo(c.x - r, c.y); close() }, (if (k % 2 == 0) Color(0xFF1E6B5A) else Color(0xFF1E4F8A)).copy(alpha = if (oscuro) 0.4f else 0.3f)); x += 12 * d; k++ }
        drawLine(oro, Offset(0f, h - zocalo), Offset(w, h - zocalo), 1.2f * d)
        for (i in 0 until n) {
            val x0 = i * paso + 8 * d; val x1 = (i + 1) * paso - 8 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            val yC = 18 * d + r * 0.9f
            // La herradura: un poco más que media circunferencia, sobre jambas.
            val arco = Path().apply {
                moveTo(x0 + r * 0.12f, h - zocalo); lineTo(x0 + r * 0.12f, yC + r * 0.35f)
                arcTo(Rect(Offset(cx, yC), r * 0.92f), 160f, 220f, false)
                lineTo(x1 - r * 0.12f, h - zocalo)
            }
            drawPath(arco, yeso, style = Stroke(5 * d))
            // Dovelas alternadas (rojo y yeso) en la rosca.
            for (j in 0 until 11) drawArc((if (j % 2 == 0) Color(0xFF9E2B25) else Color(0xFFF1E8D4)).copy(alpha = if (oscuro) 0.35f else 0.28f), 160f + j * 20f, 20f, false,
                Offset(cx - r, yC - r), Size(2 * r, 2 * r), style = Stroke(7 * d))
            // El alfiz.
            drawRect(oro, Offset(x0 - 2 * d, 10 * d), Size(x1 - x0 + 4 * d, yC + r * 0.4f - 10 * d), style = Stroke(1 * d))
        }
        // Celosía en las enjutas (entre arcos), con rombos.
        for (i in 0..n) {
            val cx = i * paso; val ancho = 10 * d
            clipPath(Path().apply { addRect(Rect(cx - ancho, 12 * d, cx + ancho, h - zocalo - 4 * d)) }) {
                var y = 12 * d
                while (y < h) { drawLine(yeso.copy(alpha = 0.4f), Offset(cx - ancho, y), Offset(cx + ancho, y + ancho * 2), 1f); drawLine(yeso.copy(alpha = 0.4f), Offset(cx + ancho, y), Offset(cx - ancho, y + ancho * 2), 1f); y += 8 * d }
            }
        }
    }

    // ------------------------------------------------------------------ Ruta de la Seda

    /** El caravasar: una ventana lunar que enmarca cada columna, muros de adobe, celosía arriba y farolillos rojos entre columnas. */
    private fun DrawScope.caravasar(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val adobe = (if (oscuro) Color(0xFF3A3028) else Color(0xFFE6D3B0)).copy(alpha = 0.6f)
        val paso = w / n
        // La celosía de arriba (meandro chino).
        val alto = 16 * d
        var x = 0f
        while (x < w) { drawPath(Path().apply { moveTo(x, alto); lineTo(x, 4 * d); lineTo(x + 10 * d, 4 * d); lineTo(x + 10 * d, 10 * d); lineTo(x + 4 * d, 10 * d) }, Color(0xFF8E1B1B).copy(alpha = 0.45f), style = Stroke(1.4f * d)); x += 14 * d }
        drawLine(Color(0xFF8E1B1B).copy(alpha = 0.5f), Offset(0f, alto + 2 * d), Offset(w, alto + 2 * d), 1.6f * d)
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d; val cx = (x0 + x1) / 2; val r = (x1 - x0) / 2
            // Muro con el vano redondo.
            val muro = Path().apply { fillType = PathFillType.EvenOdd; addRect(Rect(x0, alto + 6 * d, x1, h)); addOval(Rect(Offset(cx, alto + 6 * d + r * 1.05f), r * 0.98f)) }
            drawPath(muro, adobe.copy(alpha = 0.18f))
            drawCircle(adobe, r * 0.98f, Offset(cx, alto + 6 * d + r * 1.05f), style = Stroke(3 * d))
            drawCircle(Color(0xFF3A3A3A).copy(alpha = 0.25f), r * 1.04f, Offset(cx, alto + 6 * d + r * 1.05f), style = Stroke(1.2f * d))
        }
        // Farolillos entre las columnas.
        for (i in 0..n) {
            val cx = i * paso; var y = alto + 30 * d
            while (y < h - 40 * d) {
                drawLine(Color(0xFF3A2414).copy(alpha = 0.5f), Offset(cx, y - 14 * d), Offset(cx, y - 7 * d), 1 * d)
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.18f), Color.Transparent), Offset(cx, y), 30 * d), 30 * d, Offset(cx, y))
                drawOval(Color(0xFFC0392B).copy(alpha = 0.65f), Offset(cx - 6 * d, y - 8 * d), Size(12 * d, 16 * d))
                drawRect(Color(0xFFD4AF37).copy(alpha = 0.7f), Offset(cx - 4 * d, y - 9 * d), Size(8 * d, 2 * d)); drawRect(Color(0xFFD4AF37).copy(alpha = 0.7f), Offset(cx - 4 * d, y + 7 * d), Size(8 * d, 2 * d))
                y += 200 * d
            }
        }
    }

    // ------------------------------------------------------------------ Mongol

    /** La gran yurta: postes del techo que salen de la rueda central, el enrejado de la pared abajo y estandartes en los lados. */
    private fun DrawScope.yurta(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val madera = Color(0xFFD9772A).copy(alpha = if (oscuro) 0.35f else 0.3f)
        val fieltro = (if (oscuro) Color(0xFF3A3630) else Color(0xFFEDE6D6)).copy(alpha = 0.5f)
        val c = Offset(w / 2, -min(w, h) * 0.1f); val R = 26 * d
        // Los postes del techo, que bajan hasta el muro de cada lado sin cruzar las columnas de piezas.
        val paso = w / n
        for (i in 0..n) { val x = i * paso; drawLine(madera, c, Offset(x, 34 * d), 2.2f * d) }
        for (k in 1..10) { val x = w * k / 11f; drawLine(madera.copy(alpha = madera.alpha * 0.5f), c, Offset(x, 30 * d), 1.2f * d) }
        drawRect(fieltro, Offset(0f, 28 * d), Size(w, 8 * d))
        drawRect(Color(0xFF2E6DB4).copy(alpha = 0.45f), Offset(0f, 30 * d), Size(w, 3 * d))
        drawCircle(Color(0xFFD9772A).copy(alpha = 0.6f), R, Offset(c.x, c.y + R * 0.6f), style = Stroke(3 * d))
        // El enrejado (khana) abajo: rombos de madera.
        val y0 = h - 40 * d
        clipPath(Path().apply { addRect(Rect(0f, y0, w, h)) }) {
            var x = -h; while (x < w + h) { drawLine(madera, Offset(x, y0), Offset(x + 40 * d, h), 1.4f * d); drawLine(madera, Offset(x + 40 * d, y0), Offset(x, h), 1.4f * d); x += 14 * d }
        }
        drawLine(madera, Offset(0f, y0), Offset(w, y0), 2 * d)
        // Estandartes de colas en los lados.
        for (i in 0..n) {
            val x = i * paso
            drawLine(Color(0xFF6B4A2A).copy(alpha = 0.5f), Offset(x, 50 * d), Offset(x, y0 - 6 * d), 1.6f * d)
            for (j in 0 until 7) { val a = -0.5 + j * 0.16
                drawLine((if (j % 3 == 1) Color(0xFFEDE6D6) else Color(0xFF2A2420)).copy(alpha = 0.45f), Offset(x, 58 * d), Offset(x + (24 * d * sin(a)).toFloat(), 58 * d + 30 * d), 1.4f * d) }
        }
    }

    // ------------------------------------------------------------------ Colonos

    /** El almacén de los pioneros: pared de tablones, postes entre las columnas, repisas cortas en los lados y la linterna. */
    private fun DrawScope.almacen(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val tablon = Color(0xFF8A5A2B).copy(alpha = if (oscuro) 0.18f else 0.12f)
        var x = 0f; var k = 0
        while (x < w) { drawRect(if (k % 2 == 0) tablon else tablon.copy(alpha = tablon.alpha * 1.4f), Offset(x, 0f), Size(22 * d, h)); drawLine(Color(0xFF3A2414).copy(alpha = 0.15f), Offset(x, 0f), Offset(x, h), 1f); x += 22 * d; k++ }
        val paso = w / n
        val poste = Color(0xFF5A3A22).copy(alpha = if (oscuro) 0.55f else 0.4f)
        for (i in 0..n) { val xx = i * paso; drawRect(poste, Offset(xx - 5 * d, 0f), Size(10 * d, h)) }
        // La viga de arriba con sus clavos.
        drawRect(poste, Offset(0f, 0f), Size(w, 14 * d))
        var c = 10 * d; while (c < w) { drawCircle(Color(0xFF2A2A2A).copy(alpha = 0.4f), 1.4f * d, Offset(c, 7 * d)); c += 40 * d }
        // Herramientas colgadas en los postes: una cuerda enrollada y una linterna.
        for (i in 0..n) {
            val xx = i * paso; var y = 60 * d
            while (y < h - 60 * d) {
                drawCircle(Color(0xFFC9A57A).copy(alpha = 0.55f), 7 * d, Offset(xx, y), style = Stroke(2 * d))
                drawCircle(Color(0xFFC9A57A).copy(alpha = 0.55f), 4 * d, Offset(xx, y), style = Stroke(1.5f * d))
                val yl = y + 100 * d
                if (yl < h - 30 * d) {
                    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFD27A).copy(alpha = 0.2f), Color.Transparent), Offset(xx, yl), 28 * d), 28 * d, Offset(xx, yl))
                    drawRect(Color(0xFF4A4E54).copy(alpha = 0.7f), Offset(xx - 4 * d, yl - 6 * d), Size(8 * d, 12 * d))
                    drawRect(Color(0xFFFFE6A3).copy(alpha = 0.7f), Offset(xx - 2.5f * d, yl - 4 * d), Size(5 * d, 8 * d))
                }
                y += 220 * d
            }
        }
        // El piso de tablones.
        drawRect(Color(0xFF5A3A22).copy(alpha = 0.3f), Offset(0f, h - 10 * d), Size(w, 10 * d))
    }

    // ------------------------------------------------------------------ Botánico

    /** El invernadero: una bóveda de cristal y hierro sobre cada columna, plantas en macetas abajo y luz que entra. */
    private fun DrawScope.invernadero(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val hierro = (if (oscuro) Color(0xFF8FA89A) else Color(0xFF2F3A33)).copy(alpha = 0.4f)
        val vidrio = Color(0xFFCFE6F0).copy(alpha = if (oscuro) 0.06f else 0.18f)
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 4 * d; val x1 = (i + 1) * paso - 4 * d; val r = (x1 - x0) / 2; val cx = (x0 + x1) / 2
            val boveda = Path().apply { moveTo(x0, h); lineTo(x0, 6 * d + r); arcTo(Rect(x0, 6 * d, x1, 6 * d + 2 * r), 180f, 180f, false); lineTo(x1, h) }
            drawPath(boveda, vidrio)
            drawPath(boveda, hierro, style = Stroke(2 * d))
            // Montantes y travesaños del cristal (solo en los lados de la columna).
            listOf(x0 + r * 0.2f, x1 - r * 0.2f).forEach { xx -> drawLine(hierro.copy(alpha = hierro.alpha * 0.6f), Offset(xx, 6 * d + r * 0.6f), Offset(xx, h), 1f * d) }
            var y = 6 * d + r; while (y < h) { drawLine(hierro.copy(alpha = hierro.alpha * 0.5f), Offset(x0, y), Offset(x0 + r * 0.2f, y), 1f * d); drawLine(hierro.copy(alpha = hierro.alpha * 0.5f), Offset(x1 - r * 0.2f, y), Offset(x1, y), 1f * d); y += 40 * d }
            for (k in 1..5) { val a = PI + k * PI / 6; drawLine(hierro.copy(alpha = hierro.alpha * 0.6f), Offset(cx, 6 * d + r), Offset(cx + (r * cos(a)).toFloat(), 6 * d + r + (r * sin(a)).toFloat()), 1f * d) }
        }
        // Plantas en macetas en los lados de cada columna, abajo.
        for (i in 0..n) {
            val xx = i * paso
            drawPath(Path().apply { moveTo(xx - 9 * d, h - 16 * d); lineTo(xx + 9 * d, h - 16 * d); lineTo(xx + 7 * d, h); lineTo(xx - 7 * d, h); close() }, Color(0xFFB0603A).copy(alpha = 0.5f))
            for (j in 0 until 5) { val a = -PI / 2 + (j - 2) * 0.45
                drawPath(Path().apply { moveTo(xx, h - 16 * d); quadraticTo(xx + (20 * d * cos(a + 0.3)).toFloat(), h - 16 * d + (20 * d * sin(a + 0.3)).toFloat(), xx + (34 * d * cos(a)).toFloat(), h - 16 * d + (34 * d * sin(a)).toFloat()) },
                    Color(0xFF3F7A3A).copy(alpha = 0.55f), style = Stroke(3 * d)) }
        }
        // Rayos de luz.
        drawPath(Path().apply { moveTo(w * 0.15f, 0f); lineTo(w * 0.35f, 0f); lineTo(w * 0.6f, h); lineTo(w * 0.3f, h); close() }, Color(0xFFFFF4C2).copy(alpha = if (oscuro) 0.03f else 0.08f))
    }

    // ------------------------------------------------------------------ Navidad

    /** La sala de Navidad: guirnaldas arriba, cordeles de los que cuelgan las piezas como adornos, el árbol tenue y la chimenea. */
    private fun DrawScope.chimenea(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val verde = Color(0xFF1F6B3A).copy(alpha = if (oscuro) 0.5f else 0.4f)
        val rojo = Color(0xFFB3122E).copy(alpha = 0.6f)
        // La chimenea de ladrillo con el fuego, abajo al centro.
        val chW = min(w * 0.5f, 220 * d); val chH = 46 * d; val chX = (w - chW) / 2
        var yy = h - chH; var fila = 0
        while (yy < h) { var xx = chX - (if (fila % 2 == 0) 0f else 8 * d); while (xx < chX + chW) { drawRect(Color(0xFF8E3A2A).copy(alpha = 0.22f), Offset(xx, yy), Size(16 * d, 7 * d), style = Stroke(1f)); xx += 16 * d }; yy += 7 * d; fila++ }
        drawRect(Color(0xFF1A1210).copy(alpha = 0.35f), Offset(w / 2 - chW * 0.25f, h - chH * 0.7f), Size(chW * 0.5f, chH * 0.7f))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFFF8A2E).copy(alpha = 0.35f), Color.Transparent), Offset(w / 2, h - 10 * d), 60 * d), 60 * d, Offset(w / 2, h - 10 * d))
        // El árbol tenue en una esquina.
        val ax = w - 30 * d; val ay = h - chH - 10 * d
        for (k in 0 until 4) { val tw = (60 - k * 12) * d; val ty = ay - k * 30 * d
            drawPath(Path().apply { moveTo(ax - tw / 2, ty); lineTo(ax, ty - 40 * d); lineTo(ax + tw / 2, ty); close() }, verde.copy(alpha = verde.alpha * 0.6f)) }
        // Guirnaldas arriba entre los cordeles, con sus focos.
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso; val x1 = (i + 1) * paso
            drawPath(Path().apply { moveTo(x0, 10 * d); quadraticTo((x0 + x1) / 2, 34 * d, x1, 10 * d) }, verde, style = Stroke(5 * d))
            for (k in 1..5) { val t = k / 6f; val x = x0 + (x1 - x0) * t; val y = 10 * d + 24 * d * 2 * t * (1 - t)
                drawCircle(listOf(Color(0xFFFFD27A), Color(0xFFB3122E), Color(0xFF6FB3E0))[k % 3].copy(alpha = 0.8f), 2.4f * d, Offset(x, y + 3 * d)) }
        }
        // Cordeles: en cada columna, de los que cuelgan las piezas como adornos.
        for (i in 0 until n) {
            val cx = (i + 0.5f) * paso
            drawLine(Color(0xFFD4AF37).copy(alpha = 0.35f), Offset(cx, 12 * d), Offset(cx, h - chH - 4 * d), 1 * d, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * d, 3 * d)))
            drawPath(Path().apply { moveTo(cx, 16 * d); quadraticTo(cx - 8 * d, 6 * d, cx - 10 * d, 16 * d); close() }, rojo)
            drawPath(Path().apply { moveTo(cx, 16 * d); quadraticTo(cx + 8 * d, 6 * d, cx + 10 * d, 16 * d); close() }, rojo)
        }
        // Nieve que cae afuera (en los bordes).
        val az = java.util.Random(12)
        repeat(30) { drawCircle(Color.White.copy(alpha = if (oscuro) 0.25f else 0.5f), (1 + az.nextFloat() * 2) * d, Offset(az.nextFloat() * w, az.nextFloat() * (h - chH))) }
    }

    // ------------------------------------------------------------------ Tierras Antiguas

    /** El gran salón de piedra: pilares entre las columnas con capitel tallado, estandartes que cuelgan y antorchas. */
    private fun DrawScope.salonPiedra(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF3A3A40) else Color(0xFFB8B2A6)).copy(alpha = 0.55f)
        val junta = (if (oscuro) Color.Black else Color(0xFF6E6A62)).copy(alpha = 0.18f)
        // Sillares de fondo.
        var y = 0f; var k = 0
        while (y < h) { val off = if (k % 2 == 0) 0f else 18 * d; var x = -off; while (x < w) { drawRect(junta, Offset(x, y), Size(36 * d, 14 * d), style = Stroke(1f)); x += 36 * d }; y += 14 * d; k++ }
        val paso = w / n
        // Pilares con su capitel y runas inventadas.
        for (i in 0..n) {
            val x = i * paso
            drawRect(piedra, Offset(x - 7 * d, 20 * d), Size(14 * d, h - 20 * d))
            drawRect(piedra, Offset(x - 10 * d, 14 * d), Size(20 * d, 8 * d))
            with(TrofeosMas) { runas(Offset(x - 5 * d, h * 0.5f), 10 * d, 18 * d, Color(0xFF2B2A28).copy(alpha = 0.35f), 1.2f * d, 1, i + 3) }
        }
        // El arco de medio punto sobre cada columna de piezas.
        for (i in 0 until n) {
            val x0 = i * paso + 7 * d; val x1 = (i + 1) * paso - 7 * d; val r = (x1 - x0) / 2
            drawArc(piedra, 180f, 180f, false, Offset(x0, 20 * d), Size(x1 - x0, 2 * r), style = Stroke(6 * d))
            for (j in 0 until 9) drawArc(junta, 180f + j * 20f, 1.5f, false, Offset(x0, 20 * d), Size(x1 - x0, 2 * r), style = Stroke(6 * d))
        }
        // Estandartes con dos colas colgando de los pilares, y antorchas.
        val telas = listOf(Color(0xFF1F3A5A), Color(0xFF7A1A12))
        for (i in 0..n) {
            val x = i * paso; val y0 = 60 * d; val an = 9 * d
            drawPath(Path().apply { moveTo(x - an, y0); lineTo(x + an, y0); lineTo(x + an, y0 + 70 * d); lineTo(x, y0 + 58 * d); lineTo(x - an, y0 + 70 * d); close() }, telas[i % 2].copy(alpha = if (oscuro) 0.55f else 0.4f))
            drawLine(Color(0xFFC9A04A).copy(alpha = 0.6f), Offset(x - an - 2 * d, y0), Offset(x + an + 2 * d, y0), 1.5f * d)
            val yt = y0 + 150 * d
            if (yt < h - 30 * d) {
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.22f), Color.Transparent), Offset(x, yt), 36 * d), 36 * d, Offset(x, yt))
                drawLine(Color(0xFF3A2414).copy(alpha = 0.6f), Offset(x, yt + 14 * d), Offset(x, yt), 2.4f * d)
                drawPath(Path().apply { moveTo(x, yt - 10 * d); quadraticTo(x + 4 * d, yt - 4 * d, x, yt); quadraticTo(x - 4 * d, yt - 4 * d, x, yt - 10 * d); close() }, Color(0xFFFFB347).copy(alpha = 0.7f))
            }
        }
        // El piso de losas.
        drawRect(piedra.copy(alpha = piedra.alpha * 0.5f), Offset(0f, h - 8 * d), Size(w, 8 * d))
    }
}
