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
import androidx.compose.ui.graphics.drawscope.Stroke
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
            drawCircle(Color(0xFF2F4A3A).copy(alpha = 0.55f), rr, Offset(o.x + x + 0.9f, o.y + y + 1f))
            drawCircle(Color(0xFFF7F5EA).copy(alpha = 0.85f), rr * 0.82f, Offset(o.x + x, o.y + y))
            drawCircle(Color(0xFFFFFFFF).copy(alpha = 0.6f), rr * 0.3f, Offset(o.x + x - rr * 0.3f, o.y + y - rr * 0.3f))
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

/**
 * El papel pintado: filas de abanicos de oro viejo, cada uno con su arco, su arco de dentro y sus rayos, corridos medio
 * abanico de una fila a la otra; el dorado lleva un filo claro al lado, como impreso en relieve. Calmo: es el fondo.
 */
private val TESELA_PAPEL: ShaderBrush by lazy {
    val r = 22f
    val centros = listOf(Offset(r, r), Offset(0f, 2 * r), Offset(2 * r, 2 * r))
    val oro = Color(0xFF9A7434)
    val claro = Color(0xFFFFF3D6)
    baldosaDeco(44) { o ->
        for (c0 in centros) {
            val c = Offset(o.x + c0.x, o.y + c0.y)
            for ((col, dy, al) in listOf(Triple(claro, 0.9f, 0.4f), Triple(oro, 0f, 0.5f))) {
                drawArc(col.copy(alpha = al), 180f, 180f, false, Offset(c.x - r, c.y - r + dy), Size(2 * r, 2 * r), style = Stroke(1.1f))
                drawArc(col.copy(alpha = al * 0.8f), 180f, 180f, false, Offset(c.x - r * 0.42f, c.y - r * 0.42f + dy), Size(r * 0.84f, r * 0.84f), style = Stroke(0.9f))
                for (j in 1 until 6) {
                    val ang = Math.toRadians(180.0 + 30.0 * j)
                    val ca = kotlin.math.cos(ang).toFloat(); val sa = kotlin.math.sin(ang).toFloat()
                    drawLine(col.copy(alpha = al * 0.7f), Offset(c.x + ca * r * 0.5f, c.y + sa * r * 0.5f + dy), Offset(c.x + ca * r * 0.92f, c.y + sa * r * 0.92f + dy), 0.8f)
                }
            }
            drawCircle(oro.copy(alpha = 0.55f), 1.6f, Offset(c.x, c.y - r * 0.2f))
        }
    }
}

/** El terciopelo: un pelo fino y desparejo, con su lustre a rayas suaves a lo alto. */
private val TESELA_TERCIOPELO: ShaderBrush by lazy {
    val r = java.util.Random(91)
    val hebras = List(160) { Triple(r.nextFloat() * 48f, r.nextFloat() * 48f, r.nextFloat()) }
    baldosaDeco(48) { o ->
        for ((x, y, z) in hebras) {
            val col = if (z < 0.5f) Color(0xFF000000).copy(alpha = 0.22f) else Color(0xFFFFFFFF).copy(alpha = 0.1f)
            drawLine(col, Offset(o.x + x, o.y + y), Offset(o.x + x, o.y + y + 2.2f), 0.8f)
        }
    }
}

/** Qué textura: el galuchat, el terrazo, el cuero, el papel pintado o el terciopelo. */
internal enum class TexturaDeco { GALUCHAT, TERRAZO, CUERO, PAPEL, TERCIOPELO }

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
        TexturaDeco.PAPEL -> TESELA_PAPEL
        TexturaDeco.TERCIOPELO -> TESELA_TERCIOPELO
    }
    // Con la luz entera (de día) no hace falta el filtro, que cuesta en cada píxel.
    val filtro = if (tinte == Color.White) null else ColorFilter.tint(tinte, BlendMode.Modulate)
    drawRect(pincel, r.topLeft, r.size, alpha = alfa.coerceIn(0f, 1f), colorFilter = filtro)
}
