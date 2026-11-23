package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.AZUL_ACUARIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.CIAN
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MADERA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.NEGRO_LACA
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/** El verde de las hojas del potos y de las plantas del acuario. */
private val HOJA_NEON = Color(0xFF4E9A3E)
private val HOJA_OSC_NEON = Color(0xFF2B5E2A)

/**
 * La corona entera, pintada una vez: la pared de azulejo con la ventana y lo colgado, la cornisa de la alacena y el bodegón de
 * encima (el acuario, que manda; la radio con su antena; la planta de potos con sus guías que bajan por la cornisa; las revistas
 * y la taza; la bomba de aire).
 */
internal fun DrawScope.coronaNeon(g: MarcoNeon) {
    paredArribaNeon(g)
    ventanaNeon(g)
    colgadoNeon(g)
    cornisaNeon(g)
    sombrasPanoNeon(g, g.apoyoAtras, g.ch)
    acuarioNeon(g)
    revistasNeon(g, Offset(g.w * 0.21f, g.apoyo))
    radioNeon(g, Rect(g.w * 0.255f, g.apoyo - g.w * 0.118f, g.w * 0.45f, g.apoyo))
    potosNeon(g, Offset(g.w * 0.105f, g.apoyo))
    bombaNeon(g, Offset(g.w * 0.875f, g.apoyo))
}

/**
 * Una banda horizontal de [col] de [x0] a [x1] entre [y0] e [y1], con la luz que le llega a lo largo (un degradado de seis
 * paradas, de un solo trazo).
 */
internal fun DrawScope.bandaNeon(x0: Float, x1: Float, y0: Float, y1: Float, col: Color, g: MarcoNeon, mas: Float = 1f, velo: Float = 0f) {
    if (x1 <= x0 || y1 <= y0) return
    // En los Sueños, lo que toca el paño de la farola se pinta dos veces: sin él y, dentro de su recorte, con él entero. Así el
    // paño pinta la superficie (la laca se va al ocre, el petróleo al oliva) con su borde, y no es una franja encima.
    if (g.tocaPano(x0, x1, y0, y1)) {
        bandaSolaNeon(x0, x1, y0, y1, col, g.sinPano, mas, velo)
        clipPath(g.panoRecorte) { bandaSolaNeon(x0, x1, y0, y1, col, g.conPano, mas, velo) }
    } else bandaSolaNeon(x0, x1, y0, y1, col, g, mas, velo)
}

/** La banda de un solo trazo; [velo]: cuánto se hunde hacia la sombra del cuarto (la penumbra, sin otra capa encima). */
private fun DrawScope.bandaSolaNeon(x0: Float, x1: Float, y0: Float, y1: Float, col: Color, g: MarcoNeon, mas: Float, velo: Float) {
    val ym = (y0 + y1) / 2
    val paradas = Array(6) { i -> val f = i / 5f; f to mezcla(g.t(x0 + (x1 - x0) * f, ym).c(col, mas), PaletaNeon.SOMBRA, velo) }
    drawRect(Brush.horizontalGradient(*paradas, startX = x0, endX = x1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
}

/**
 * La cornisa de la alacena, de [MarcoNeon.apoyoAtras] al pie de la corona: la cara de arriba, donde pisa el bodegón; la
 * moldura volada de laca negra con su filete dorado; la banda de clavos de latón; el friso de laca roja con la celosía calada de
 * rombos entre dos tableros lisos; el junquillo de abajo y las cantoneras de latón en las esquinas; la laca saltada en el canto.
 */
private fun DrawScope.cornisaNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val x0 = g.ml; val x1 = g.mr
    val vuelo = w * 0.012f
    val ap = g.apoyo; val ch = g.ch
    // La sombra de la cornisa en la pared, a la derecha y abajo.
    drawRect(g.a.sombra(0.4f), Offset(x0 + 4f * u, ap), Size(x1 - x0 + vuelo, 6f * u))
    // La cara de arriba (vista un poco desde arriba): laca rozada, con el canto de atrás en sombra.
    bandaNeon(x0 - vuelo * 0.6f, x1 + vuelo * 0.6f, g.apoyoAtras, ap, LACA_LUZ, g, 0.9f)
    drawRect(g.a.sombra(0.35f), Offset(x0 - vuelo * 0.6f, g.apoyoAtras), Size(x1 - x0 + vuelo * 1.2f, 1.5f * u))
    for (j in 0 until 9) {
        val x = x0 + (x1 - x0) * azar(5401, j)
        val t = g.t(x, ap)
        drawLine(t.c(MADERA, 0.8f), Offset(x, g.apoyoAtras + (ap - g.apoyoAtras) * 0.5f), Offset(x + w * (0.01f + 0.03f * azar(5402, j)), g.apoyoAtras + (ap - g.apoyoAtras) * 0.6f), 1.2f * u, StrokeCap.Round)
    }
    // La moldura volada: laca negra, el filo de luz arriba, el filete dorado y su sombra.
    val m0 = ap; val m1 = ap + w * 0.024f
    bandaNeon(x0 - vuelo, x1 + vuelo, m0, m1, NEGRO_LACA, g)
    bandaNeon(x0 - vuelo, x1 + vuelo, m0, m0 + w * 0.006f, mezcla(NEGRO_LACA, LACA_LUZ, 0.45f), g, 1.1f)
    bandaNeon(x0 - vuelo, x1 + vuelo, m1 - w * 0.007f, m1 - w * 0.0045f, LATON, g)
    drawRect(g.a.sombra(0.5f), Offset(x0 - vuelo, m1), Size(x1 - x0 + vuelo * 2, 2.6f * u))
    // La banda de los clavos de latón.
    val b0 = m1; val b1 = m1 + w * 0.02f
    bandaNeon(x0, x1, b0, b1, LACA, g)
    var x = x0 + w * 0.025f
    while (x < x1 - w * 0.015f) { clavoNeon(Offset(x, (b0 + b1) / 2 + 0.6f * u), w * 0.0055f, g.t(x, b0)); x += w * 0.04f }
    // El friso: dos tableros lisos y, en medio, la celosía calada de rombos.
    val f0 = b1 + w * 0.003f; val f1 = ch - w * 0.013f
    bandaNeon(x0, x1, b1, ch, LACA_OSC, g)
    val cel = Rect(x0 + (x1 - x0) * 0.3f, f0 + w * 0.004f, x1 - (x1 - x0) * 0.3f, f1 - w * 0.004f)
    listOf(Rect(x0 + w * 0.012f, f0, cel.left - w * 0.012f, f1), Rect(cel.right + w * 0.012f, f0, x1 - w * 0.012f, f1)).forEach { r ->
        bandaNeon(r.left, r.right, r.top, r.bottom, LACA, g)
        val t = g.t(r.center)
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
        drawRect(t.b(0.3f), r.topLeft, Size(1f * u, r.height))
        drawRect(t.s(0.45f), Offset(r.left, r.bottom - 1.2f * u), Size(r.width, 1.2f * u))
        drawRect(t.s(0.35f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
    }
    celosiaNeon(cel, g)
    // El junquillo de abajo.
    bandaNeon(x0, x1, f1, ch, NEGRO_LACA, g)
    bandaNeon(x0, x1, f1, f1 + w * 0.003f, mezcla(NEGRO_LACA, LACA_LUZ, 0.4f), g, 1.1f)
    // Las cantoneras de latón en las esquinas del friso.
    listOf(x0, x1 - w * 0.03f).forEachIndexed { i, cx ->
        val r = Rect(cx, b0, cx + w * 0.03f, ch)
        val t = g.t(r.center)
        val forma = if (i == 0) poligonoNeon(r.left, r.top, r.right, r.top, r.left + r.width * 0.3f, r.center.y, r.right, r.bottom, r.left, r.bottom)
            else poligonoNeon(r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom, r.left + r.width * 0.7f, r.center.y)
        drawPath(forma, t.c(LATON_OSC))
        clipPath(forma) { drawRect(t.c(LATON), r.topLeft, Size(r.width, r.height * 0.5f)); drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1.2f * u)) }
        clavoNeon(Offset(r.left + r.width * (if (i == 0) 0.3f else 0.7f), r.top + r.height * 0.2f), 1.3f * u, t)
        clavoNeon(Offset(r.left + r.width * (if (i == 0) 0.3f else 0.7f), r.bottom - r.height * 0.2f), 1.3f * u, t)
    }
    // La laca saltada en el canto de la moldura, donde asoma la madera.
    listOf(0.18f, 0.55f, 0.83f).forEachIndexed { j, fx ->
        val cx = x0 + (x1 - x0) * fx
        val t = g.t(cx, m0)
        drawPath(poligonoNeon(cx, m0 + 1f * u, cx + w * (0.014f + 0.01f * j), m0 + 1f * u, cx + w * 0.01f, m0 + w * 0.006f, cx + w * 0.003f, m0 + w * 0.004f), t.c(MADERA, 0.85f))
    }
}

/** La celosía calada de rombos del friso: el hueco en sombra detrás y los listones cruzados, con su filo de luz. */
private fun DrawScope.celosiaNeon(r: Rect, g: MarcoNeon) {
    val u = density
    val t = g.t(r.center)
    drawRect(t.c(NEGRO_LACA, 0.35f), r.topLeft, r.size)
    clipRect(r.left, r.top, r.right, r.bottom) {
        val paso = r.height * 0.9f
        val g1 = maxOf(1.6f, r.height * 0.16f)
        var x = r.left - r.height
        while (x < r.right + r.height) {
            val tl = g.t(x, r.center.y)
            drawLine(tl.s(0.5f), Offset(x + 1f * u, r.top + 1f * u), Offset(x + r.height + 1f * u, r.bottom + 1f * u), g1)
            drawLine(tl.s(0.5f), Offset(x + r.height + 1f * u, r.top + 1f * u), Offset(x + 1f * u, r.bottom + 1f * u), g1)
            drawLine(tl.c(LACA), Offset(x, r.top), Offset(x + r.height, r.bottom), g1)
            drawLine(tl.c(LACA), Offset(x + r.height, r.top), Offset(x, r.bottom), g1)
            drawLine(tl.b(0.4f), Offset(x - g1 * 0.3f, r.top), Offset(x + r.height - g1 * 0.3f, r.bottom), 0.7f * u)
            x += paso
        }
    }
    drawRect(t.c(NEGRO_LACA), r.topLeft, r.size, style = Stroke(2f * u))
    drawRect(t.b(0.4f), r.topLeft, Size(r.width, 1f * u))
}

// ---------------------------------------------------------------- el acuario

/**
 * El acuario, que manda en la corona: la peana negra, el vidrio con el agua encendida (cian en el diario; azul y baja en los
 * Sueños), la grava, la piedra, las plantas, la columna de burbujas, dos peces naranjas, los rayos que bajan de la tapa, la
 * tapa con su tubo de luz, las juntas de silicona de las esquinas y los reflejos. Su luz tiñe la pared de detrás y la cornisa.
 */
private fun DrawScope.acuarioNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val m = MuebleEstanteNeon
    val noche = g.a.noche
    val v = Rect(w * m.ACU_X0, w * m.ACU_Y0, w * m.ACU_X1, w * m.ACU_Y1)
    val agua = if (noche) AZUL_ACUARIO else CIAN
    // El resplandor en la pared y en la cornisa.
    mancha(v.center, v.width * 0.9f, agua.copy(alpha = if (noche) 0.12f else 0.2f))
    drawRect(g.a.sombra(0.5f), Offset(v.left + w * 0.028f, v.top - w * 0.01f), Size(v.width, g.apoyo - v.top + w * 0.01f))
    // El agua: de la luz de la tapa a lo hondo de abajo.
    val arriba = if (noche) Color(0xFF3253A8) else Color(0xFF7DF0F0)
    val abajo = if (noche) Color(0xFF0C1840) else Color(0xFF137A86)
    val aguaR = Rect(v.left, v.top + v.height * 0.06f, v.right, v.bottom)
    drawRect(Color(0xFF0A1A24), v.topLeft, v.size)
    drawRect(Brush.verticalGradient(listOf(arriba, abajo), aguaR.top, aguaR.bottom), aguaR.topLeft, aguaR.size)
    clipRect(v.left, v.top, v.right, v.bottom) {
        // Los rayos de la tapa: cuñas claras que bajan.
        for (j in 0 until 4) {
            val x = v.left + v.width * (0.12f + 0.24f * j)
            drawPath(poligonoNeon(x, aguaR.top, x + v.width * 0.06f, aguaR.top, x + v.width * 0.02f, v.bottom, x - v.width * 0.08f, v.bottom), Color.White.copy(alpha = if (noche) 0.03f else 0.07f))
        }
        // El fondo: la grava en lomo, más alta atrás a la derecha, con sus piedrecitas.
        val grava = Path().apply {
            moveTo(v.left, v.bottom - v.height * 0.14f)
            cubicTo(v.left + v.width * 0.3f, v.bottom - v.height * 0.2f, v.left + v.width * 0.6f, v.bottom - v.height * 0.12f, v.right, v.bottom - v.height * 0.22f)
            lineTo(v.right, v.bottom); lineTo(v.left, v.bottom); close()
        }
        val tierra = if (noche) Color(0xFF3A3A5A) else Color(0xFFC9A57A)
        drawPath(grava, tierra)
        for (j in 0 until 40) {
            val px = v.left + v.width * azar(5501, j)
            val py = v.bottom - v.height * (0.02f + 0.13f * azar(5502, j))
            drawCircle(mezcla(tierra, if (j % 3 == 0) Color(0xFFE8D8C0) else Color(0xFF6A4A3A), 0.4f), (0.8f + 1.1f * azar(5503, j)) * u, Offset(px, py))
        }
        // La piedra grande con su arco, a la derecha.
        val piedra = Path().apply {
            moveTo(v.left + v.width * 0.62f, v.bottom - v.height * 0.12f)
            cubicTo(v.left + v.width * 0.64f, v.bottom - v.height * 0.42f, v.left + v.width * 0.8f, v.bottom - v.height * 0.46f, v.left + v.width * 0.86f, v.bottom - v.height * 0.16f)
            close()
        }
        drawPath(piedra, if (noche) Color(0xFF2A2E4A) else Color(0xFF5C6A70))
        drawPath(piedra, Color.White.copy(alpha = 0.18f), style = Stroke(1.2f * u))
        // Las plantas: hojas largas que ondean y una de hoja ancha.
        val verde = if (noche) Color(0xFF1E4A5A) else HOJA_NEON
        val verdeOsc = if (noche) Color(0xFF12304A) else HOJA_OSC_NEON
        for (j in 0 until 9) {
            val bx = v.left + v.width * (0.05f + 0.1f * j + 0.03f * azar(5511, j))
            val alto = v.height * (0.35f + 0.45f * azar(5512, j))
            val curva = v.width * (0.03f + 0.05f * azar(5513, j)) * (if (j % 2 == 0) 1f else -1f)
            val p = Path().apply {
                moveTo(bx - 2f * u, v.bottom - v.height * 0.12f)
                cubicTo(bx + curva, v.bottom - alto * 0.5f, bx - curva, v.bottom - alto * 0.8f, bx + curva * 0.5f, v.bottom - alto)
                cubicTo(bx - curva * 0.8f + 2f * u, v.bottom - alto * 0.8f, bx + curva + 2f * u, v.bottom - alto * 0.5f, bx + 2f * u, v.bottom - v.height * 0.12f)
                close()
            }
            drawPath(p, if (j % 3 == 1) verdeOsc else verde)
            drawPath(p, Color.White.copy(alpha = 0.12f), style = Stroke(0.6f * u))
        }
        // Las burbujas, de la piedra difusora de la izquierda hacia arriba.
        val bx = v.left + v.width * 0.2f
        for (j in 0 until 12) {
            val fy = j / 12f
            val p = Offset(bx + v.width * 0.02f * kotlin.math.sin(j * 1.7f), v.bottom - v.height * 0.14f - (v.height * 0.8f) * fy)
            drawCircle(Color.White.copy(alpha = 0.6f), (0.9f + 1.4f * fy) * u, p, style = Stroke(0.7f * u))
        }
        // Los dos peces naranjas (en los Sueños, quietos cerca del fondo).
        pezNeon(Offset(v.left + v.width * (if (noche) 0.46f else 0.38f), v.top + v.height * (if (noche) 0.66f else 0.42f)), v.width * 0.07f, 1f, noche)
        pezNeon(Offset(v.left + v.width * (if (noche) 0.3f else 0.62f), v.top + v.height * (if (noche) 0.72f else 0.3f)), v.width * 0.055f, -1f, noche)
        // El reflejo en el vidrio: una franja clara en diagonal.
        drawPath(poligonoNeon(v.left + v.width * 0.06f, v.top, v.left + v.width * 0.14f, v.top, v.left + v.width * 0.04f, v.bottom, v.left - v.width * 0.04f, v.bottom), Color.White.copy(alpha = 0.08f))
    }
    // La línea del agua y el vidrio de arriba.
    drawLine(Color.White.copy(alpha = 0.55f), Offset(v.left, aguaR.top), Offset(v.right, aguaR.top), 1.2f * u)
    // Las juntas de silicona de las esquinas y el canto del vidrio.
    listOf(v.left, v.right).forEach { x -> drawLine(Color(0xFF0A1418).copy(alpha = 0.7f), Offset(x, v.top), Offset(x, v.bottom), 2.6f * u) }
    drawLine(agua.copy(alpha = 0.8f), Offset(v.left + 1.6f * u, v.top), Offset(v.left + 1.6f * u, v.bottom), 0.9f * u)
    drawLine(mezcla(agua, Color.White, 0.5f), Offset(v.right - 1.8f * u, v.top + v.height * 0.1f), Offset(v.right - 1.8f * u, v.bottom - v.height * 0.1f), 0.8f * u)
    // La tapa con su tubo de luz.
    val tapa = Rect(v.left - w * 0.006f, v.top - w * 0.022f, v.right + w * 0.006f, v.top + w * 0.004f)
    drawRect(g.a.sombra(0.4f), Offset(tapa.left + 3f * u, tapa.top + 3f * u), tapa.size)
    drawRoundRect(g.a.luz(HIERRO, 0.5f, agua), tapa.topLeft, tapa.size, CornerRadius(2f * u))
    drawRect(g.t(tapa.center).b(0.7f), Offset(tapa.left + 2f * u, tapa.top), Size(tapa.width - 4f * u, 1.2f * u))
    drawRect(mezcla(agua, Color.White, 0.6f).copy(alpha = if (noche) 0.5f else 0.95f), Offset(tapa.left + tapa.width * 0.06f, tapa.bottom - 2f * u), Size(tapa.width * 0.88f, 1.6f * u))
    // La peana negra.
    val peana = Rect(v.left - w * 0.008f, v.bottom, v.right + w * 0.008f, g.apoyo)
    drawRect(g.a.luz(HIERRO, 0.55f, agua), peana.topLeft, peana.size)
    drawRect(mezcla(agua, Color.White, 0.3f).copy(alpha = 0.5f), peana.topLeft, Size(peana.width, 1.2f * u))
    drawRect(g.a.sombra(0.5f), Offset(peana.left, peana.bottom - 1.6f * u), Size(peana.width, 1.6f * u))
    // La luz del agua en la cara de arriba de la cornisa, delante.
    drawOval(agua.copy(alpha = if (noche) 0.12f else 0.22f), Offset(v.left - w * 0.02f, g.apoyo - w * 0.006f), Size(v.width + w * 0.04f, w * 0.02f))
}

/** Un pez naranja visto de lado: el cuerpo, la cola, la aleta y el brillo. [lado] 1 mira a la derecha, -1 a la izquierda. */
private fun DrawScope.pezNeon(c: Offset, s: Float, lado: Float, noche: Boolean) {
    val naranja = if (noche) Color(0xFF8A4A5A) else Color(0xFFFF8A3A)
    val cuerpo = Path().apply {
        moveTo(c.x + s * 0.6f * lado, c.y)
        cubicTo(c.x + s * 0.4f * lado, c.y - s * 0.4f, c.x - s * 0.3f * lado, c.y - s * 0.38f, c.x - s * 0.45f * lado, c.y)
        cubicTo(c.x - s * 0.3f * lado, c.y + s * 0.36f, c.x + s * 0.4f * lado, c.y + s * 0.38f, c.x + s * 0.6f * lado, c.y)
        close()
    }
    val cola = poligonoNeon(c.x - s * 0.4f * lado, c.y, c.x - s * 0.95f * lado, c.y - s * 0.42f, c.x - s * 0.8f * lado, c.y, c.x - s * 0.95f * lado, c.y + s * 0.42f)
    drawPath(cola, mezcla(naranja, Color.White, 0.35f).copy(alpha = 0.85f))
    drawPath(cuerpo, naranja)
    drawPath(poligonoNeon(c.x, c.y - s * 0.28f, c.x - s * 0.25f * lado, c.y - s * 0.5f, c.x - s * 0.2f * lado, c.y - s * 0.22f), mezcla(naranja, Color.White, 0.3f))
    drawLine(Color.White.copy(alpha = 0.6f), Offset(c.x - s * 0.2f * lado, c.y - s * 0.18f), Offset(c.x + s * 0.3f * lado, c.y - s * 0.2f), 0.9f * density)
}

// ---------------------------------------------------------------- lo de delante

/** La pila de revistas sin letras (cubiertas de colores con un dibujo de franjas) y la taza encima. */
private fun DrawScope.revistasNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    val t = g.t(pie.x, pie.y - w * 0.02f)
    val cols = listOf(Color(0xFFE8C43A), Color(0xFF3A6AC8), Color(0xFFE0E0E8), Color(0xFFD8466A))
    var y = pie.y
    contactoNeon(Offset(pie.x, pie.y), w * 0.12f, w * 0.012f, t)
    for (j in 0 until 4) {
        val alto = w * (0.008f + 0.003f * (j % 2))
        val dx = w * 0.008f * (azar(5601, j) - 0.5f)
        val r = Rect(pie.x - w * 0.05f + dx, y - alto, pie.x + w * 0.05f + dx, y)
        drawRect(t.c(cols[j]), r.topLeft, r.size)
        drawRect(t.c(HUESO, 0.8f), Offset(r.left, r.top + alto * 0.35f), Size(r.width, alto * 0.3f))
        drawRect(t.b(0.5f), r.topLeft, Size(r.width, 0.8f * u))
        drawRect(t.s(0.35f), Offset(r.left, r.bottom - 0.8f * u), Size(r.width, 0.8f * u))
        y -= alto
    }
    // La taza de loza con su asa, encima.
    val tz = Rect(pie.x - w * 0.02f, y - w * 0.04f, pie.x + w * 0.018f, y)
    val tt = g.t(tz.center)
    drawOval(tt.s(0.4f), Offset(tz.left, y - 2f * u), Size(tz.width * 1.2f, 4f * u))
    drawArc(tt.c(HUESO), -80f, 160f, false, Offset(tz.right - tz.width * 0.25f, tz.top + tz.height * 0.25f), Size(tz.width * 0.45f, tz.height * 0.45f), style = Stroke(2.2f * u))
    drawRoundRect(tt.c(HUESO), tz.topLeft, tz.size, CornerRadius(1f * u, 4f * u))
    clipRect(tz.left, tz.top, tz.right, tz.bottom) { planosNeon(tz, tt) }
    drawRect(tt.c(Color(0xFF2F5FA8)), Offset(tz.left, tz.top + tz.height * 0.18f), Size(tz.width, tz.height * 0.12f))
    drawOval(tt.c(Color(0xFF3A2418)), Offset(tz.left + 1f * u, tz.top - 1.4f * u), Size(tz.width - 2f * u, 3f * u))
}

/**
 * La radio de transistores: la caja de baquelita de dos colores, la rejilla de ranuras del altavoz, la ventanilla del dial
 * encendida (ámbar, con sus marcas sin cifras), la perilla, el asa cromada y la antena telescópica que cruza hacia la ventana.
 */
private fun DrawScope.radioNeon(g: MarcoNeon, r: Rect) {
    val u = density; val w = g.w
    val t = g.t(r.center)
    // La antena, detrás: sube en diagonal hacia la ventana, por tramos más finos.
    val base = Offset(r.left + r.width * 0.18f, r.top + r.height * 0.1f)
    val punta = Offset(r.left - w * 0.13f, r.top - w * 0.17f)
    for (j in 0 until 3) {
        val a = base + (punta - base) * (j / 3f); val b = base + (punta - base) * ((j + 1) / 3f)
        drawLine(t.s(0.35f), a + Offset(3f * u, 3f * u), b + Offset(3f * u, 3f * u), (2.4f - 0.6f * j) * u)
        drawLine(g.t(a).c(ALUMINIO, 1.1f), a, b, (2.4f - 0.6f * j) * u)
        drawLine(g.t(a).b(0.8f, 1.3f), a + Offset(-0.6f * u, -0.3f * u), b + Offset(-0.6f * u, -0.3f * u), 0.7f * u)
    }
    drawCircle(g.t(punta).c(ALUMINIO, 1.2f), 1.8f * u, punta)
    // La sombra en la pared y la de contacto.
    drawRoundRect(t.s(0.5f), Offset(r.left + w * 0.022f, r.top + w * 0.01f), r.size, CornerRadius(5f * u))
    contactoNeon(Offset(r.center.x, r.bottom), r.width * 1.05f, w * 0.014f, t, 0.6f)
    // El asa cromada.
    drawRoundRect(t.c(ALUMINIO), Offset(r.left + r.width * 0.2f, r.top - r.height * 0.2f), Size(r.width * 0.6f, r.height * 0.3f), CornerRadius(4f * u), style = Stroke(2.4f * u))
    drawArc(t.b(0.8f), 180f, 90f, false, Offset(r.left + r.width * 0.2f, r.top - r.height * 0.2f), Size(r.width * 0.3f, r.height * 0.3f), style = Stroke(0.8f * u))
    // La caja: el color de arriba y el de abajo, sus planos y el filo.
    val caja = redondoNeon(r, 5f * u)
    drawPath(caja, t.c(Color(0xFF2F7E78)))
    clipPath(caja) {
        drawRect(t.c(HUESO), Offset(r.left, r.top + r.height * 0.55f), Size(r.width, r.height * 0.45f))
        planosNeon(r, t, 0.9f)
        drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1.4f * u))
    }
    // La rejilla del altavoz: ranuras horizontales.
    val rej = Rect(r.left + r.width * 0.08f, r.top + r.height * 0.18f, r.left + r.width * 0.55f, r.top + r.height * 0.86f)
    drawRoundRect(t.c(Color(0xFF1A2A2C)), rej.topLeft, rej.size, CornerRadius(2f * u))
    for (j in 0 until 7) {
        val y = rej.top + rej.height * (0.12f + 0.12f * j)
        drawRect(t.c(ALUMINIO, 0.9f), Offset(rej.left + 2f * u, y), Size(rej.width - 4f * u, rej.height * 0.05f))
    }
    // La ventanilla del dial, encendida.
    val dial = Rect(r.left + r.width * 0.62f, r.top + r.height * 0.2f, r.right - r.width * 0.08f, r.top + r.height * 0.42f)
    val luzDial = if (g.a.noche) Color(0xFF7A5A2A) else AMBAR
    mancha(dial.center, dial.width * 0.9f, luzDial.copy(alpha = 0.3f))
    drawRect(mezcla(luzDial, Color(0xFF2A1808), 0.25f), dial.topLeft, dial.size)
    for (j in 0 until 7) drawLine(Color(0xFF2A1808), Offset(dial.left + dial.width * (0.1f + 0.13f * j), dial.top + dial.height * 0.2f), Offset(dial.left + dial.width * (0.1f + 0.13f * j), dial.top + dial.height * (if (j % 2 == 0) 0.6f else 0.45f)), 0.7f * u)
    drawLine(Color(0xFFE8362E), Offset(dial.left + dial.width * 0.62f, dial.top), Offset(dial.left + dial.width * 0.62f, dial.bottom), 1f * u)
    drawRect(t.c(ALUMINIO), dial.topLeft, dial.size, style = Stroke(1f * u))
    // La perilla.
    val pc = Offset(r.left + r.width * 0.77f, r.top + r.height * 0.68f); val pr = r.height * 0.15f
    drawCircle(t.s(0.45f), pr, pc + Offset(1.6f * u, 1.6f * u))
    drawCircle(t.c(Color(0xFF6A3A2A)), pr, pc)
    for (j in 0 until 10) {
        val an = j * 0.628f
        drawLine(t.s(0.4f), Offset(pc.x + pr * 0.7f * kotlin.math.cos(an), pc.y + pr * 0.7f * kotlin.math.sin(an)), Offset(pc.x + pr * kotlin.math.cos(an), pc.y + pr * kotlin.math.sin(an)), 0.7f * u)
    }
    drawCircle(t.b(0.7f), pr * 0.35f, pc - Offset(pr * 0.35f, pr * 0.35f))
}

/** Un rectángulo de esquinas redondas como camino. */
internal fun redondoNeon(r: Rect, radio: Float): Path = Path().apply {
    addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(radio)))
}

/**
 * La planta de potos en una lata de conservas: la lata con su borde y la etiqueta arrancada (sin letras), las hojas acorazonadas
 * que suben delante de la ventana y las guías que caen por la cornisa.
 */
private fun DrawScope.potosNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    val lata = Rect(pie.x - w * 0.042f, pie.y - w * 0.075f, pie.x + w * 0.042f, pie.y)
    val t = g.t(lata.center)
    contactoNeon(Offset(lata.center.x, lata.bottom), lata.width * 1.3f, w * 0.014f, t)
    drawRect(t.s(0.45f), Offset(lata.left + w * 0.018f, lata.top + w * 0.008f), lata.size)
    drawRect(t.c(ALUMINIO), lata.topLeft, lata.size)
    drawRect(t.c(Color(0xFFC8402E)), Offset(lata.left, lata.top + lata.height * 0.25f), Size(lata.width * 0.7f, lata.height * 0.55f))
    drawPath(poligonoNeon(lata.left + lata.width * 0.7f, lata.top + lata.height * 0.25f, lata.left + lata.width * 0.78f, lata.top + lata.height * 0.4f, lata.left + lata.width * 0.7f, lata.top + lata.height * 0.8f), t.c(Color(0xFFC8402E)))
    clipRect(lata.left, lata.top, lata.right, lata.bottom) {
        planosNeon(lata, t)
        for (j in 0 until 5) drawLine(t.s(0.25f), Offset(lata.left, lata.top + lata.height * (0.12f + 0.18f * j)), Offset(lata.right, lata.top + lata.height * (0.12f + 0.18f * j)), 0.8f * u)
    }
    drawOval(t.c(ALUMINIO, 1.2f), Offset(lata.left - 1f * u, lata.top - 2f * u), Size(lata.width + 2f * u, 4f * u))
    drawOval(t.c(Color(0xFF2A1A12)), Offset(lata.left + 1.5f * u, lata.top - 1f * u), Size(lata.width - 3f * u, 2.6f * u))
    // Las hojas: las que suben, delante de la ventana, y las guías que caen por la cornisa.
    val hojas = listOf(
        Triple(Offset(-0.04f, -0.13f), 0.032f, -30f), Triple(Offset(0.01f, -0.17f), 0.036f, 10f), Triple(Offset(0.045f, -0.12f), 0.03f, 40f),
        Triple(Offset(-0.06f, -0.09f), 0.028f, -60f), Triple(Offset(0.035f, -0.2f), 0.028f, 25f), Triple(Offset(-0.015f, -0.22f), 0.026f, -10f),
    )
    // Los tallos.
    hojas.forEach { (o, _, _) ->
        val p = Offset(pie.x + w * o.x, pie.y + w * o.y)
        drawLine(g.t(p).c(HOJA_OSC_NEON), Offset(pie.x, lata.top), p, 1.2f * u)
    }
    val guia = Path().apply {
        moveTo(lata.right - w * 0.01f, lata.top)
        cubicTo(lata.right + w * 0.04f, lata.top - w * 0.01f, lata.right + w * 0.05f, g.apoyo, lata.right + w * 0.035f, g.ch - w * 0.005f)
    }
    val guia2 = Path().apply {
        moveTo(lata.left + w * 0.01f, lata.top)
        cubicTo(lata.left - w * 0.03f, lata.top + w * 0.01f, lata.left - w * 0.035f, g.apoyo + w * 0.02f, lata.left - w * 0.02f, g.ch - w * 0.03f)
    }
    listOf(guia, guia2).forEach { p -> drawPath(p, g.t(pie).c(HOJA_OSC_NEON), style = Stroke(1.3f * u)) }
    val colgadas = listOf(Triple(Offset(0.075f, 0.01f), 0.024f, 150f), Triple(Offset(0.085f, 0.045f), 0.026f, 190f), Triple(Offset(0.078f, 0.085f), 0.022f, 170f),
        Triple(Offset(-0.07f, 0.02f), 0.022f, 200f), Triple(Offset(-0.072f, 0.06f), 0.02f, 160f))
    (hojas + colgadas).forEach { (o, s, giro) -> hojaPotosNeon(g, Offset(pie.x + w * o.x, pie.y + w * o.y), w * s, giro) }
}

/** Una hoja de potos: la lámina acorazonada (alargada, con punta), el nervio, su lado de luz y su sombra. */
private fun DrawScope.hojaPotosNeon(g: MarcoNeon, c: Offset, s: Float, giro: Float) {
    val u = density
    val t = g.t(c)
    rotate(giro, c) {
        val lamina = Path().apply {
            moveTo(c.x, c.y - s * 0.7f)
            cubicTo(c.x + s * 0.55f, c.y - s * 0.55f, c.x + s * 0.5f, c.y + s * 0.25f, c.x, c.y + s * 0.62f)
            cubicTo(c.x - s * 0.5f, c.y + s * 0.25f, c.x - s * 0.55f, c.y - s * 0.55f, c.x, c.y - s * 0.7f)
            close()
        }
        drawPath(Path().apply { addPath(lamina, Offset(2.5f * u, 2.5f * u)) }, t.s(0.3f))
        drawPath(lamina, t.c(HOJA_NEON))
        clipPath(lamina) {
            drawRect(t.c(HOJA_OSC_NEON), Offset(c.x, c.y - s), Size(s, s * 2))
            drawRect(t.c(Color(0xFFB9D86A), 0.8f), Offset(c.x - s * 0.3f, c.y - s * 0.4f), Size(s * 0.12f, s * 0.6f))
        }
        drawLine(t.c(HOJA_OSC_NEON), Offset(c.x, c.y - s * 0.6f), Offset(c.x, c.y + s * 0.5f), 0.8f * u)
        drawPath(lamina, t.b(0.45f), style = Stroke(0.7f * u))
    }
}

/** La bomba de aire del acuario: la cajita, su goma y la manguera que sube al agua. */
private fun DrawScope.bombaNeon(g: MarcoNeon, pie: Offset) {
    val u = density; val w = g.w
    val r = Rect(pie.x - w * 0.035f, pie.y - w * 0.035f, pie.x + w * 0.035f, pie.y)
    val t = g.t(r.center)
    val tubo = Path().apply { moveTo(r.left + r.width * 0.3f, r.top); cubicTo(r.left, r.top - w * 0.05f, w * 0.86f, w * 0.2f, w * 0.8f, w * 0.29f) }
    drawPath(tubo, t.s(0.3f), style = Stroke(1.8f * u))
    drawPath(tubo, Color(0xFFB8E0D8).copy(alpha = 0.55f), style = Stroke(1.2f * u))
    contactoNeon(Offset(r.center.x, r.bottom), r.width * 1.2f, w * 0.012f, t)
    drawRoundRect(t.c(Color(0xFF3E4A6A)), r.topLeft, r.size, CornerRadius(4f * u))
    clipPath(redondoNeon(r, 4f * u)) { planosNeon(r, t) }
    drawRect(t.c(HUESO, 0.7f), Offset(r.left + r.width * 0.2f, r.top + r.height * 0.4f), Size(r.width * 0.5f, r.height * 0.18f))
    drawCircle(if (g.a.noche) Color(0xFF2A6A3A) else Color(0xFF7CFF6B), 1.2f * u, Offset(r.right - r.width * 0.18f, r.top + r.height * 0.3f))
}
