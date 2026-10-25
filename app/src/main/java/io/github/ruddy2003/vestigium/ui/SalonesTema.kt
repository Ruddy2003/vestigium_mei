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

    fun DrawScope.escena(tema: TemaId, cs: ColorScheme) {
        val oscuro = cs.background.luminance() < 0.5f
        when (tema) {
            TemaId.ROMANO -> atrio(cs, oscuro)
            TemaId.HELENO -> stoa(cs, oscuro)
            TemaId.MEDIEVAL -> salaGestas(cs, oscuro)
            TemaId.GOTICO -> nave(cs, oscuro)
            TemaId.RENACIMIENTO -> galeria(cs, oscuro)
            TemaId.NAPOLEON -> imperial(cs, oscuro)
            TemaId.REVOLUCION -> panteon(cs, oscuro)
            else -> {}
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
            else -> drawRect(metal, Offset(0f, 0f), Size(w, h))
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

    /** El atrio: zócalo de pórfido, pilastras de mármol con capitel corintio y el friso con la greca. */
    private fun DrawScope.atrio(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val marmol = (if (oscuro) Color(0xFF2A2F34) else Color(0xFFDCDAD2)).copy(alpha = 0.55f)
        val porfido = Color(0xFF7C2433).copy(alpha = if (oscuro) 0.35f else 0.22f)
        drawRect(porfido, Offset(0f, h * 0.78f), Size(w, h * 0.22f))
        drawRect(cs.tertiary.copy(alpha = 0.35f), Offset(0f, h * 0.78f), Size(w, 2f))
        val n = 4; val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, 30f, h * 0.78f, 16f, marmol, 2)
        drawRect(marmol, Offset(0f, 0f), Size(w, 22f))
        greca(4f, 12f, cs.tertiary.copy(alpha = 0.45f))
    }

    /** La stoa: columnas jónicas, el cielo del Egeo entre ellas arriba y el estilóbato abajo. */
    private fun DrawScope.stoa(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        drawRect(Brush.verticalGradient(listOf(cs.secondary.copy(alpha = if (oscuro) 0.18f else 0.14f), Color.Transparent), 0f, h * 0.4f), Offset.Zero, Size(w, h * 0.4f))
        val marmol = (if (oscuro) Color(0xFF3A3550) else Color(0xFFEDE7DA)).copy(alpha = 0.7f)
        val n = 5; val paso = w / n
        for (i in 0..n) columnaClasica(i * paso + paso / 2 - paso / 2, 34f, h - 18f, 14f, marmol, 1)
        drawRect(marmol, Offset(0f, 0f), Size(w, 26f))
        greca(6f, 12f, cs.primary.copy(alpha = 0.45f))
        for (k in 0 until 3) drawRect(marmol, Offset(0f, h - 18f + k * 6f), Size(w, 5f))
    }

    /** La sala de gestas: sillares de piedra, tapices colgados con su borde de oro y antorchas. */
    private fun DrawScope.salaGestas(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val junta = (if (oscuro) Color.Black else Color(0xFF8A7A5C)).copy(alpha = 0.18f)
        var y = 0f; var k = 0
        while (y < h) { val off = if (k % 2 == 0) 0f else 34f; var x = -off; while (x < w) { drawRect(junta, Offset(x, y), Size(68f, 26f), style = Stroke(1f)); x += 68f }; y += 26f; k++ }
        val tapices = listOf(Color(0xFFB0281E), Color(0xFF1F3F8F))
        listOf(0.16f, 0.84f).forEachIndexed { i, fx ->
            val x0 = w * fx - 30f; val x1 = w * fx + 30f; val yb = min(h * 0.55f, 230f)
            val tela = Path().apply { moveTo(x0, 14f); lineTo(x1, 14f); lineTo(x1, yb); lineTo(w * fx, yb - 20f); lineTo(x0, yb); close() }
            drawPath(tela, tapices[i].copy(alpha = 0.38f))
            drawPath(tela, Color(0xFFD4A93C).copy(alpha = 0.55f), style = Stroke(2f))
            drawLine(Color(0xFF5A3A22).copy(alpha = 0.6f), Offset(x0 - 8f, 14f), Offset(x1 + 8f, 14f), 4f)
            estrella(Offset(w * fx, yb * 0.45f), 12f, 5f, Color(0xFFD4A93C).copy(alpha = 0.6f), 6)
        }
        // Antorchas.
        listOf(0.5f).forEach { fx ->
            val x = w * fx; drawLine(Color(0xFF5A3A22).copy(alpha = 0.5f), Offset(x, 60f), Offset(x, 26f), 4f)
            drawPath(Path().apply { moveTo(x, 4f); quadraticTo(x + 9f, 16f, x, 26f); quadraticTo(x - 9f, 16f, x, 4f); close() }, Color(0xFFFFB347).copy(alpha = 0.6f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB347).copy(alpha = 0.18f), Color.Transparent), Offset(x, 20f), 90f), 90f, Offset(x, 20f))
        }
    }

    /** La nave gótica: arcos apuntados, manchas de luz de colores en el suelo y el rosetón arriba al centro. */
    private fun DrawScope.nave(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val piedra = (if (oscuro) Color(0xFF1A2356) else Color(0xFFC9BC9F)).copy(alpha = 0.55f)
        val n = 4; val paso = w / n
        for (i in 0 until n) {
            val x0 = i * paso + 8f; val x1 = (i + 1) * paso - 8f; val yArr = 70f
            drawPath(Path().apply { moveTo(x0, h); lineTo(x0, yArr); quadraticTo(x0, yArr - 40f, (x0 + x1) / 2, 14f); quadraticTo(x1, yArr - 40f, x1, yArr); lineTo(x1, h) }, piedra, style = Stroke(3f))
        }
        for (i in 0..n) drawRect(piedra, Offset(i * paso - 5f, 40f), Size(10f, h - 40f))
        // Luz de los vitrales.
        val luces = listOf(Color(0xFFB3122E), Color(0xFF1F3A9E), Color(0xFFE2B34A), Color(0xFF2E7D4A))
        val az = java.util.Random(4)
        repeat(9) { k ->
            val c = Offset(az.nextFloat() * w, h * (0.35f + az.nextFloat() * 0.6f))
            drawCircle(Brush.radialGradient(listOf(luces[k % 4].copy(alpha = if (oscuro) 0.22f else 0.14f), Color.Transparent), c, 60f), 60f, c)
        }
        // Rosetón.
        val c = Offset(w / 2, 46f); val R = 34f
        drawCircle(Color(0xFF1F3A9E).copy(alpha = 0.35f), R, c)
        for (k in 0 until 12) { val a = k * PI / 6; drawCircle(luces[k % 4].copy(alpha = 0.45f), R * 0.22f, Offset(c.x + (R * 0.68f * cos(a)).toFloat(), c.y + (R * 0.68f * sin(a)).toFloat())) }
        drawCircle(Color(0xFFE2B34A).copy(alpha = 0.6f), R * 0.25f, c)
        drawCircle(piedra, R, c, style = Stroke(3f))
    }

    /** La galería: artesonado en perspectiva arriba, hornacinas en el muro y el pavimento en damero que huye al centro. */
    private fun DrawScope.galeria(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val tinta = (if (oscuro) Color(0xFFEFE0BE) else Color(0xFF3A2614)).copy(alpha = 0.16f)
        val fuga = Offset(w / 2, h * 0.42f)
        // Artesonado.
        val alto = 60f
        for (k in 0..8) { val x = w * k / 8; drawLine(tinta, Offset(x, 0f), Offset(fuga.x + (x - fuga.x) * 0.55f, alto), 1.2f) }
        listOf(0f, alto * 0.5f, alto).forEach { yy -> val f = yy / alto; drawLine(tinta, Offset(fuga.x - w / 2 * (1 - 0.45f * f), yy), Offset(fuga.x + w / 2 * (1 - 0.45f * f), yy), 1.2f) }
        // Hornacinas.
        listOf(0.18f, 0.82f).forEach { fx ->
            val x0 = w * fx - 26f; val x1 = w * fx + 26f
            drawPath(Path().apply { moveTo(x0, h * 0.6f); lineTo(x0, 110f); arcTo(Rect(x0, 84f, x1, 136f), 180f, 180f, false); lineTo(x1, h * 0.6f) }, tinta, style = Stroke(1.6f))
            drawArc(cs.primary.copy(alpha = 0.12f), 180f, 180f, true, Offset(x0 + 6f, 92f), Size(40f, 36f))
        }
        // Pavimento en damero.
        val y0 = h * 0.72f
        for (k in -10..10) drawLine(tinta, Offset(fuga.x + k * w / 10, h), Offset(fuga.x + k * w / 10 * 0.5f, y0), 1f)
        var yy = y0; var paso = 10f
        while (yy < h) { drawLine(tinta, Offset(0f, yy), Offset(w, yy), 1f); yy += paso; paso *= 1.35f }
    }

    /** La galería imperial: pilastras de mármol verde, guirnaldas de laurel en oro y cortinajes carmesí arriba. */
    private fun DrawScope.imperial(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val verde = Color(0xFF2F5D4A).copy(alpha = if (oscuro) 0.45f else 0.3f)
        val oro = Color(0xFFD4AF37).copy(alpha = 0.6f)
        val n = 4; val paso = w / n
        for (i in 0..n) { val x = i * paso; drawRect(verde, Offset(x - 9f, 50f), Size(18f, h - 50f)); drawRect(oro, Offset(x - 12f, 46f), Size(24f, 5f)); drawRect(oro, Offset(x - 12f, h - 8f), Size(24f, 5f)) }
        // Guirnaldas de laurel entre pilastras.
        for (i in 0 until n) {
            val x0 = i * paso + 12f; val x1 = (i + 1) * paso - 12f
            val p = Path().apply { moveTo(x0, 56f); quadraticTo((x0 + x1) / 2, 96f, x1, 56f) }
            drawPath(p, oro, style = Stroke(2f))
            for (k in 1..7) { val t = k / 8f; val x = x0 + (x1 - x0) * t; val y = 56f + 80f * t * (1 - t)
                drawOval(oro, Offset(x - 5f, y - 2.5f), Size(10f, 5f)) }
            estrella(Offset((x0 + x1) / 2, 30f), 9f, 3.6f, oro)
        }
        // Cortinaje.
        val carmesi = Color(0xFFA3172E).copy(alpha = if (oscuro) 0.5f else 0.35f)
        val tela = Path().apply { moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, 22f); var x = w; while (x > 0f) { val x2 = (x - w / 6).coerceAtLeast(0f); quadraticTo((x + x2) / 2, 46f, x2, 22f); x = x2 }; close() }
        drawPath(tela, carmesi)
        drawLine(oro, Offset(0f, 22f), Offset(w, 22f), 2f)
    }

    /** El panteón: la cúpula artesonada en arcos, columnas lisas y estandartes tricolores. */
    private fun DrawScope.panteon(cs: ColorScheme, oscuro: Boolean) {
        val w = size.width; val h = size.height
        val piedra = (if (oscuro) Color(0xFF3A3F4A) else Color(0xFFD8D1C2)).copy(alpha = 0.6f)
        // La cúpula: arcos concéntricos con casetones.
        val c = Offset(w / 2, 120f)
        for (k in 0 until 4) {
            val r = w * (0.48f - k * 0.1f)
            drawArc(piedra, 180f, 180f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(2f))
            if (k < 3) for (j in 1 until 10) { val a = PI + j * PI / 10; val r2 = w * (0.48f - (k + 1) * 0.1f)
                drawLine(piedra, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), Offset(c.x + (r2 * cos(a)).toFloat(), c.y + (r2 * sin(a)).toFloat()), 1.2f) }
        }
        drawRect(piedra, Offset(0f, c.y), Size(w, 8f))
        // Columnas lisas.
        val n = 4; val paso = w / n
        for (i in 0..n) columnaClasica(i * paso, c.y + 26f, h - 10f, 14f, piedra, 0)
        // Estandartes tricolores entre columnas.
        val tri = listOf(Color(0xFF1F3A6E), Color.White, Color(0xFFB22234))
        listOf(0, n - 1).forEach { i ->
            val x0 = i * paso + paso * 0.3f; val an = paso * 0.4f / 3; val y0 = c.y + 34f; val y1 = min(h * 0.6f, y0 + 150f)
            tri.forEachIndexed { k, col -> drawRect(col.copy(alpha = if (k == 1) 0.5f else 0.38f), Offset(x0 + k * an, y0), Size(an, y1 - y0)) }
            drawLine(Color(0xFFC9A04A).copy(alpha = 0.6f), Offset(x0 - 6f, y0), Offset(x0 + 3 * an + 6f, y0), 3f)
        }
        // La escarapela en lo alto de la cúpula.
        drawCircle(tri[0].copy(alpha = 0.6f), 13f, Offset(c.x, 30f)); drawCircle(tri[1].copy(alpha = 0.8f), 9f, Offset(c.x, 30f)); drawCircle(tri[2].copy(alpha = 0.7f), 5f, Offset(c.x, 30f))
    }
}
