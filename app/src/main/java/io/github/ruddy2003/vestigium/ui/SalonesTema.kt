package io.github.ruddy2003.vestigium.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * El hall de los temas históricos, cada uno con su escena: el atrio romano, la stoa griega, la sala de gestas con tapices,
 * la nave gótica con su rosetón, la galería renacentista con su artesonado, la galería imperial y el panteón de la Revolución.
 * Todo se dibuja tenue para que las piezas se lean encima.
 */
object SalonesTema {
    val TEMAS = TrofeosTema.TEMAS

    /**
     * La escena detrás de las piezas. [columnas] = cuántas piezas hay por fila: la arquitectura (columnas, arcos,
     * hornacinas) se acomoda para enmarcar cada columna de piezas y nunca pasar por detrás de una.
     */
    fun DrawScope.escena(tema: TemaId, cs: ColorScheme, columnas: Int = 2) {
        val oscuro = cs.background.luminance() < 0.5f
        val n = columnas.coerceAtLeast(1)
        when (tema) {
            TemaId.ROMANO -> atrio(cs, oscuro, n)
            TemaId.HELENO -> stoa(cs, oscuro, n)
            TemaId.MEDIEVAL -> salaGestas(cs, oscuro, n)
            TemaId.GOTICO -> nave(cs, oscuro, n)
            TemaId.RENACIMIENTO -> galeria(cs, oscuro, n)
            TemaId.NAPOLEON -> imperial(cs, oscuro, n)
            TemaId.REVOLUCION -> panteon(cs, oscuro, n)
            else -> with(SalonesMas) { escenaMas(tema, cs, oscuro, n) }
        }
    }

    /** El pedestal de cada pieza en el hall de estos temas. */
    fun DrawScope.pedestal(tema: TemaId, cs: ColorScheme, metal: Color) {
        val w = size.width; val h = size.height
        fun piedra(c: Color) = Brush.verticalGradient(listOf(lerp(c, Color.White, 0.2f), c, lerp(c, Color.Black, 0.25f)), 0f, h)
        when (tema) {
            TemaId.ROMANO -> { drawRect(piedra(Color(0xFF7C2433).copy(alpha = 0.75f)), Offset(w * 0.2f, h * 0.15f), Size(w * 0.6f, h * 0.7f)); drawRect(piedra(Color(0xFFE7E3DA)), Offset(w * 0.14f, 0f), Size(w * 0.72f, h * 0.18f)); drawRect(piedra(Color(0xFFE7E3DA)), Offset(w * 0.1f, h * 0.82f), Size(w * 0.8f, h * 0.18f)) }
            TemaId.HELENO -> for (k in 0 until 3) drawRect(piedra(Color(0xFFEDE7DA)), Offset(w * (0.24f - k * 0.07f), h * k / 3f), Size(w * (0.52f + k * 0.14f), h / 3f))
            TemaId.MEDIEVAL -> { drawRect(piedra(Color(0xFF5A3A22)), Offset(w * 0.12f, h * 0.1f), Size(w * 0.76f, h * 0.35f)); drawRect(Color(0xFF5A3A22), Offset(w * 0.45f, h * 0.45f), Size(w * 0.1f, h * 0.55f)) }
            TemaId.GOTICO -> { drawRect(piedra(Color(0xFF6B6F7A)), Offset(w * 0.18f, 0f), Size(w * 0.64f, h)); for (k in 1..3) drawLine(Color(0xFF3A3E48).copy(alpha = 0.5f), Offset(w * (0.18f + 0.16f * k), 0f), Offset(w * (0.18f + 0.16f * k), h), 1.2f) }
            TemaId.RENACIMIENTO -> { drawRect(piedra(Color(0xFF8C9188)), Offset(w * 0.16f, h * 0.2f), Size(w * 0.68f, h * 0.8f)); drawRect(piedra(Color(0xFFB5B9AE)), Offset(w * 0.1f, 0f), Size(w * 0.8f, h * 0.22f)) }
            TemaId.NAPOLEON -> { drawRect(piedra(Color(0xFF2F5D4A)), Offset(w * 0.16f, 0f), Size(w * 0.68f, h)); drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, 0f), Size(w * 0.68f, h * 0.12f)); drawRect(Color(0xFFD4AF37), Offset(w * 0.16f, h * 0.88f), Size(w * 0.68f, h * 0.12f)) }
            TemaId.REVOLUCION -> {
                drawRect(piedra(Color(0xFFD8D1C2)), Offset(w * 0.18f, 0f), Size(w * 0.64f, h))
                listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234)).forEachIndexed { i, c -> drawRect(c, Offset(w * (0.18f + i * 0.2133f), 0f), Size(w * 0.2133f, h * 0.22f)) }
            }
            else -> with(SalonesMas) { pedestalMas(tema, cs, metal) }
        }
    }

    // ------------------------------------------------------------------ utilidades

    private fun DrawScope.columnaClasica(x: Float, y0: Float, y1: Float, ancho: Float, c: Color, capitel: Int) {
        drawRect(c, Offset(x - ancho / 2, y0), Size(ancho, y1 - y0))
        for (k in 1..3) drawLine(lerp(c, Color.Black, 0.25f), Offset(x - ancho / 2 + ancho * k / 4, y0), Offset(x - ancho / 2 + ancho * k / 4, y1), 1f)
        when (capitel) {
            // Jónico: ábaco y volutas.
            1 -> { drawRect(c, Offset(x - ancho * 0.85f, y0 - ancho * 0.25f), Size(ancho * 1.7f, ancho * 0.25f)); listOf(-1f, 1f).forEach { s -> drawCircle(c, ancho * 0.28f, Offset(x + s * ancho * 0.7f, y0 + ancho * 0.05f), style = Stroke(2f)) } }
            // Corintio: hojas.
            2 -> for (k in 0 until 3) drawPath(Path().apply { val cx = x - ancho * 0.5f + k * ancho * 0.5f; moveTo(cx - ancho * 0.3f, y0); quadraticTo(cx, y0 - ancho * 0.7f, cx + ancho * 0.3f, y0); close() }, c)
            // Dórico / toscano.
            else -> drawRect(c, Offset(x - ancho * 0.75f, y0 - ancho * 0.22f), Size(ancho * 1.5f, ancho * 0.22f))
        }
        drawRect(c, Offset(x - ancho * 0.7f, y1), Size(ancho * 1.4f, ancho * 0.2f))
    }

    private fun DrawScope.greca(y: Float, alto: Float, c: Color) {
        val u = alto / 3f; val paso = u * 4; var x = 0f; val p = Path()
        while (x + paso <= size.width + paso) {
            p.moveTo(x, y + alto); p.lineTo(x, y); p.lineTo(x + u * 3, y); p.lineTo(x + u * 3, y + u * 2); p.lineTo(x + u, y + u * 2); p.lineTo(x + u, y + u)
            p.moveTo(x, y + alto); p.lineTo(x + paso, y + alto); x += paso
        }
        drawPath(p, c, style = Stroke(1.6f))
    }

    private fun DrawScope.estrella(c: Offset, R: Float, r: Float, color: Color, puntas: Int = 5) {
        val p = Path()
        for (k in 0 until puntas * 2) { val a = -PI / 2 + k * PI / puntas; val rr = if (k % 2 == 0) R else r; val x = c.x + (rr * cos(a)).toFloat(); val y = c.y + (rr * sin(a)).toFloat(); if (k == 0) p.moveTo(x, y) else p.lineTo(x, y) }
        p.close(); drawPath(p, color)
    }

    // ------------------------------------------------------------------ las escenas

    /** El atrio: pilastras de mármol con capitel corintio entre las piezas, el friso con la greca y un zócalo de pórfido abajo. */
    private fun DrawScope.atrio(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val marmol = (if (oscuro) Color(0xFF2A2F34) else Color(0xFFDCDAD2)).copy(alpha = 0.6f)
        val porfido = Color(0xFF7C2433).copy(alpha = if (oscuro) 0.3f else 0.18f)
        drawRect(porfido, Offset(0f, h - 26 * d), Size(w, 26 * d))
        drawRect(cs.tertiary.copy(alpha = 0.35f), Offset(0f, h - 26 * d), Size(w, 1.5f * d))
        val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, 22 * d, h - 26 * d, 7 * d, marmol, 2)
        drawRect(marmol, Offset(0f, 0f), Size(w, 14 * d))
        greca(2.5f * d, 8 * d, cs.tertiary.copy(alpha = 0.45f))
    }

    /** La stoa: columnas jónicas entre las piezas, el cielo del Egeo arriba y el estilóbato abajo. */
    private fun DrawScope.stoa(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        drawRect(Brush.verticalGradient(listOf(cs.secondary.copy(alpha = if (oscuro) 0.18f else 0.14f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
        val marmol = (if (oscuro) Color(0xFF3A3550) else Color(0xFFEDE7DA)).copy(alpha = 0.75f)
        val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, 20 * d, h - 10 * d, 7 * d, marmol, 1)
        drawRect(marmol, Offset(0f, 0f), Size(w, 15 * d))
        greca(3 * d, 8 * d, cs.primary.copy(alpha = 0.45f))
        for (k in 0 until 3) drawRect(marmol, Offset(0f, h - 10 * d + k * 3.4f * d), Size(w, 3 * d))
    }

    /** La sala de gestas: sillares, un tapiz colgado entre cada par de piezas y antorchas en los muros de los lados. */
    private fun DrawScope.salaGestas(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val junta = (if (oscuro) Color.Black else Color(0xFF8A7A5C)).copy(alpha = 0.18f)
        val sw = 26 * d; val sh = 10 * d
        var y = 0f; var k = 0
        while (y < h) { val off = if (k % 2 == 0) 0f else sw / 2; var x = -off; while (x < w) { drawRect(junta, Offset(x, y), Size(sw, sh), style = Stroke(1f)); x += sw }; y += sh; k++ }
        val tapices = listOf(Color(0xFFB0281E), Color(0xFF1F3F8F))
        val paso = w / n
        for (i in 1 until n) {
            val cx = i * paso; val an = 11 * d; val yb = h * 0.9f
            val tela = Path().apply { moveTo(cx - an, 6 * d); lineTo(cx + an, 6 * d); lineTo(cx + an, yb); lineTo(cx, yb - 8 * d); lineTo(cx - an, yb); close() }
            drawPath(tela, tapices[i % 2].copy(alpha = 0.32f))
            drawPath(tela, Color(0xFFD4A93C).copy(alpha = 0.55f), style = Stroke(1.2f * d))
            drawLine(Color(0xFF5A3A22).copy(alpha = 0.6f), Offset(cx - an - 3 * d, 6 * d), Offset(cx + an + 3 * d, 6 * d), 2 * d)
            var yy = 40 * d; while (yy < yb - 30 * d) { estrella(Offset(cx, yy), 5 * d, 2 * d, Color(0xFFD4A93C).copy(alpha = 0.55f), 6); yy += 120 * d }
        }
        // Antorchas en los muros de los lados.
        listOf(6 * d, w - 6 * d).forEach { x ->
            var yy = 30 * d
            while (yy < h) {
                drawLine(Color(0xFF5A3A22).copy(alpha = 0.5f), Offset(x, yy + 14 * d), Offset(x, yy), 2 * d)
                drawPath(Path().apply { moveTo(x, yy - 10 * d); quadraticTo(x + 4 * d, yy - 4 * d, x, yy); quadraticTo(x - 4 * d, yy - 4 * d, x, yy - 10 * d); close() }, Color(0xFFFFB347).copy(alpha = 0.6f))
                drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.16f), Color.Transparent), Offset(x, yy - 5 * d), 40 * d), 40 * d, Offset(x, yy - 5 * d))
                yy += 230 * d
            }
        }
    }

    /** La nave gótica: un arco apuntado sobre cada columna de piezas, manchas de luz de colores y el rosetón entre los arcos. */
    private fun DrawScope.nave(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF2E3A7A) else Color(0xFFC9BC9F)).copy(alpha = 0.6f)
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d; val yArr = 34 * d
            drawPath(Path().apply { moveTo(x0, h); lineTo(x0, yArr); quadraticTo(x0, yArr - 20 * d, (x0 + x1) / 2, 4 * d); quadraticTo(x1, yArr - 20 * d, x1, yArr); lineTo(x1, h) }, piedra, style = Stroke(2 * d))
        }
        for (i in 0..n) drawRect(piedra, Offset(i * paso - 3 * d, 20 * d), Size(6 * d, h - 20 * d))
        // Luz de los vitrales.
        val luces = listOf(Color(0xFFB3122E), Color(0xFF1F3A9E), Color(0xFFE2B34A), Color(0xFF2E7D4A))
        val az = java.util.Random(4)
        repeat(10) { k ->
            val c = Offset(az.nextFloat() * w, h * (0.2f + az.nextFloat() * 0.75f))
            drawCircle(Brush.radialGradient(listOf(luces[k % 4].copy(alpha = if (oscuro) 0.2f else 0.12f), Color.Transparent), c, 34 * d), 34 * d, c)
        }
        // Rosetones entre los arcos.
        for (i in 1 until n) {
            val c = Offset(i * paso, 24 * d); val R = 14 * d
            drawCircle(Color(0xFF1F3A9E).copy(alpha = 0.4f), R, c)
            for (k in 0 until 12) { val a = k * PI / 6; drawCircle(luces[k % 4].copy(alpha = 0.5f), R * 0.22f, Offset(c.x + (R * 0.68f * cos(a)).toFloat(), c.y + (R * 0.68f * sin(a)).toFloat())) }
            drawCircle(Color(0xFFE2B34A).copy(alpha = 0.65f), R * 0.25f, c)
            drawCircle(piedra, R, c, style = Stroke(1.5f * d))
        }
    }

    /** La galería: artesonado arriba, una hornacina que enmarca cada columna de piezas y el pavimento en damero que huye. */
    private fun DrawScope.galeria(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val tinta = (if (oscuro) Color(0xFFEFE0BE) else Color(0xFF3A2614)).copy(alpha = 0.16f)
        val fuga = Offset(w / 2, 26 * d)
        val alto = 24 * d
        for (k in 0..8) { val x = w * k / 8; drawLine(tinta, Offset(x, 0f), Offset(fuga.x + (x - fuga.x) * 0.7f, alto), 1f) }
        listOf(0f, alto * 0.5f, alto).forEach { yy -> drawLine(tinta, Offset(0f, yy), Offset(w, yy), 1f) }
        val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 10 * d; val x1 = (i + 1) * paso - 10 * d; val r = (x1 - x0) / 2
            drawPath(Path().apply { moveTo(x0, h - 30 * d); lineTo(x0, alto + 8 * d + r); arcTo(Rect(x0, alto + 8 * d, x1, alto + 8 * d + 2 * r), 180f, 180f, false); lineTo(x1, h - 30 * d) }, tinta, style = Stroke(1.4f * d))
            drawArc(cs.primary.copy(alpha = 0.07f), 180f, 180f, true, Offset(x0 + 4 * d, alto + 12 * d), Size(x1 - x0 - 8 * d, 2 * r - 8 * d))
        }
        // Pavimento en damero.
        val y0 = h - 30 * d
        for (k in -10..10) drawLine(tinta, Offset(w / 2 + k * w / 10, h), Offset(w / 2 + k * w / 10 * 0.6f, y0), 1f)
        var yy = y0; var pasoY = 4 * d
        while (yy < h) { drawLine(tinta, Offset(0f, yy), Offset(w, yy), 1f); yy += pasoY; pasoY *= 1.35f }
    }

    /** La galería imperial: pilastras de mármol verde entre las piezas, guirnaldas de laurel en oro y el cortinaje carmesí arriba. */
    private fun DrawScope.imperial(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val verde = Color(0xFF2F5D4A).copy(alpha = if (oscuro) 0.45f else 0.3f)
        val oro = Color(0xFFD4AF37).copy(alpha = 0.6f)
        val paso = w / n; val arriba = 26 * d
        for (i in 0..n) { val x = i * paso; drawRect(verde, Offset(x - 4 * d, arriba), Size(8 * d, h - arriba)); drawRect(oro, Offset(x - 6 * d, arriba - 2 * d), Size(12 * d, 2.5f * d)); drawRect(oro, Offset(x - 6 * d, h - 4 * d), Size(12 * d, 2.5f * d)) }
        for (i in 0 until n) {
            val x0 = i * paso + 6 * d; val x1 = (i + 1) * paso - 6 * d
            val y0 = arriba + 4 * d; val caida = 14 * d
            drawPath(Path().apply { moveTo(x0, y0); quadraticTo((x0 + x1) / 2, y0 + 2 * caida, x1, y0) }, oro, style = Stroke(1.2f * d))
            for (k in 1..9) { val t = k / 10f; val x = x0 + (x1 - x0) * t; val y = y0 + 2 * caida * 2 * t * (1 - t)
                drawOval(oro, Offset(x - 2.5f * d, y - 1.2f * d), Size(5 * d, 2.4f * d)) }
            estrella(Offset((x0 + x1) / 2, y0 + caida * 2.2f), 4 * d, 1.6f * d, oro)
        }
        // Cortinaje.
        val carmesi = Color(0xFFA3172E).copy(alpha = if (oscuro) 0.5f else 0.35f)
        val tela = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, 10 * d); var x = w; while (x > 0f) { val x2 = (x - w / (3 * n)).coerceAtLeast(0f); quadraticTo((x + x2) / 2, 20 * d, x2, 10 * d); x = x2 }; close() }
        drawPath(tela, carmesi)
        drawLine(oro, Offset(0f, 10 * d), Offset(w, 10 * d), 1 * d)
    }

    /** El panteón: la cúpula artesonada arriba, columnas lisas entre las piezas y estandartes tricolores en los muros. */
    private fun DrawScope.panteon(cs: ColorScheme, oscuro: Boolean, n: Int) {
        val w = size.width; val h = size.height; val d = density
        val piedra = (if (oscuro) Color(0xFF3A3F4A) else Color(0xFFD8D1C2)).copy(alpha = 0.65f)
        // La cúpula: arcos concéntricos con casetones, apoyada en la cornisa.
        val base = min(w * 0.42f, h * 0.4f); val c = Offset(w / 2, base)
        for (k in 0 until 4) {
            val r = base * (0.96f - k * 0.2f)
            drawArc(piedra, 180f, 180f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(1.5f * d))
            if (k < 3) for (j in 1 until 10) { val a = PI + j * PI / 10; val r2 = base * (0.96f - (k + 1) * 0.2f)
                drawLine(piedra, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), Offset(c.x + (r2 * cos(a)).toFloat(), c.y + (r2 * sin(a)).toFloat()), 1f) }
        }
        drawRect(piedra, Offset(0f, c.y), Size(w, 4 * d))
        val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, c.y + 10 * d, h - 6 * d, 6 * d, piedra, 0)
        // Estandartes tricolores en los muros de los lados.
        val tri = listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234))
        val an = 3.2f * d
        listOf(8 * d, w - 8 * d - 3 * an).forEach { x0 ->
            var y0 = c.y + 18 * d
            while (y0 < h - 60 * d) {
                tri.forEachIndexed { k, col -> drawRect(col.copy(alpha = if (k == 1) 0.6f else 0.45f), Offset(x0 + k * an, y0), Size(an, 46 * d)) }
                drawLine(Color(0xFFC9A04A).copy(alpha = 0.6f), Offset(x0 - 2 * d, y0), Offset(x0 + 3 * an + 2 * d, y0), 1.5f * d)
                y0 += 220 * d
            }
        }
        // La escarapela en lo alto de la cúpula.
        val e = Offset(c.x, c.y - base * 0.96f + 8 * d)
        drawCircle(tri[2].copy(alpha = 0.6f), 6 * d, e); drawCircle(tri[1].copy(alpha = 0.85f), 4 * d, e); drawCircle(tri[0].copy(alpha = 0.7f), 2.2f * d, e)
    }
}
