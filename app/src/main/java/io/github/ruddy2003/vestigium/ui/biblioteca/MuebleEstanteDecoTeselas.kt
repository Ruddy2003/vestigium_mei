package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

// =====================================================================
//  Art déco: las texturas en baldosa (el grano del galuchat, las chinas del terrazo, el grano del cuero)
// =====================================================================

/**
 * Una baldosa de [lado] píxeles, pintada una sola vez, hecha pincel que se repite. Así una textura de cientos de granos es
 * una sola operación por dibujo (en el teléfono, cada punto suelto se vuelve a pintar en cada cuadro). Lo que pinta [pinta]
 * se repite corrido un lado en cada dirección, para que la baldosa empalme sin costura.
 */
private fun baldosaDeco(lado: Int, pinta: DrawScope.(Offset) -> Unit): ShaderBrush {
    val img = ImageBitmap(lado, lado)
    val l = lado.toFloat()
    CanvasDrawScope().draw(Density(1f), LayoutDirection.Ltr, Canvas(img), Size(l, l)) {
        for (dx in -1..1) for (dy in -1..1) pinta(Offset(dx * l, dy * l))
    }
    return ShaderBrush(ImageShader(img, TileMode.Repeated, TileMode.Repeated))
}

/** El galuchat: perlas en tresbolillo, cada una con su punto de luz arriba a la izquierda y su sombra abajo a la derecha. */
private val TESELA_GALUCHAT: ShaderBrush by lazy {
    val r = java.util.Random(31)
    val perlas = buildList {
        for (i in 0 until 8) for (j in 0 until 8) {
            val x = j * 6f + (if (i % 2 == 0) 0f else 3f) + (r.nextFloat() - 0.5f) * 1.4f
            val y = i * 6f + (r.nextFloat() - 0.5f) * 1.4f
            add(Triple(x, y, 1.3f + r.nextFloat() * 0.9f))
        }
    }
    baldosaDeco(48) { o ->
        for ((x, y, rr) in perlas) {
            drawCircle(Color(0xFF3F5A4A).copy(alpha = 0.35f), rr, Offset(o.x + x + 0.8f, o.y + y + 0.9f))
            drawCircle(Color(0xFFF4F1E4).copy(alpha = 0.6f), rr * 0.85f, Offset(o.x + x, o.y + y))
        }
    }
}

/** El terrazo: chinas de mármol de tres colores y tamaños distintos. */
private val TESELA_TERRAZO: ShaderBrush by lazy {
    val r = java.util.Random(53)
    val colores = listOf(Color(0xFF5E5A52), Color(0xFF9E6B4A), Color(0xFFEDE7DA), Color(0xFF7C8A7E))
    val chinas = List(80) { Triple(Offset(r.nextFloat() * 96f, r.nextFloat() * 96f), 1.2f + r.nextFloat() * 2.4f, colores[r.nextInt(colores.size)]) }
    baldosaDeco(96) { o ->
        for ((c, rr, col) in chinas) drawOval(col.copy(alpha = 0.8f), Offset(o.x + c.x - rr, o.y + c.y - rr * 0.7f), Size(rr * 2f, rr * 1.4f))
    }
}

/** El cuero: un grano fino, de poros oscuros y algún brillo. */
private val TESELA_CUERO: ShaderBrush by lazy {
    val r = java.util.Random(71)
    val poros = List(70) { Offset(r.nextFloat() * 40f, r.nextFloat() * 40f) }
    val brillos = List(18) { Offset(r.nextFloat() * 40f, r.nextFloat() * 40f) }
    baldosaDeco(40) { o ->
        for (p in poros) drawCircle(Color(0xFF0E0908).copy(alpha = 0.4f), 0.8f, Offset(o.x + p.x, o.y + p.y))
        for (p in brillos) drawCircle(Color(0xFFFFF4E6).copy(alpha = 0.22f), 0.7f, Offset(o.x + p.x, o.y + p.y))
    }
}

/** Qué textura: el galuchat, el terrazo o el cuero. */
internal enum class TexturaDeco { GALUCHAT, TERRAZO, CUERO }

/**
 * Pinta la textura [t] sobre [r], con la luz que le llega: el [tinte] (blanco de día; de noche, apagado) multiplica la baldosa,
 * así los granos se hunden con lo demás y no brillan sueltos en lo oscuro.
 */
internal fun DrawScope.texturaDeco(t: TexturaDeco, r: Rect, tinte: Color, alfa: Float) {
    if (r.width <= 0f || r.height <= 0f) return
    val pincel = when (t) {
        TexturaDeco.GALUCHAT -> TESELA_GALUCHAT
        TexturaDeco.TERRAZO -> TESELA_TERRAZO
        TexturaDeco.CUERO -> TESELA_CUERO
    }
    drawRect(pincel, r.topLeft, r.size, alpha = alfa.coerceIn(0f, 1f), colorFilter = ColorFilter.tint(tinte, BlendMode.Modulate))
}
