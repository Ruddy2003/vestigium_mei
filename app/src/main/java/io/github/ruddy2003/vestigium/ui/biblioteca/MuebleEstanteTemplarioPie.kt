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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El pie del estante de Templario ([MuebleEstanteTemplario]): las losas de caliza con la estera de palma tejida en espiga y su
 * orilla de almagra y negro; a la izquierda, el brasero de bronce de tres patas con sus ascuas y la badila (de noche, la
 * segunda luz), y la espuerta del carbón cortada por el borde; a la derecha, la tinaja del agua en su pie de madera con el
 * jarrillo encima, cortada por el borde.
 */
internal object PieEstanteTemplario {
    private val P = PaletaTemplario

    fun DrawScope.alPie(a: AireTemplario, b: Rect, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width
        piso(a, suelo, h, cerca)
        // La sombra del armario en el suelo, junto al zócalo.
        drawRect(a.sombra(0.45f), Offset(0f, suelo), Size(w, w * 0.008f))
        drawRect(a.sombra(0.2f), Offset(0f, suelo + w * 0.008f), Size(w, w * 0.016f))
        espuerta(a, Offset(w * 0.06f, h - w * 0.03f), cerca)
        // La tinaja, algo más chica, para que el jarrillo de encima no pase del zócalo.
        Offset(w * 0.95f, h - w * 0.02f).let { p -> scale(0.8f, p) { tinaja(a, p, cerca) } }
        brasero(a, Offset(w * MuebleEstanteTemplario.BRASERO_X, h - w * 0.03f), b, cerca)
    }

    /** El color de [c] en el suelo de noche, en un solo degradado: encendido junto al brasero y hundido lejos. */
    private fun DrawScope.charco(a: AireTemplario, c: Color, f: Float, h: Float, cerca: (Offset) -> Float): Brush {
        val w = size.width
        val centro = Offset(w * MuebleEstanteTemplario.BRASERO_X, h - w * 0.14f)
        val radios = floatArrayOf(0f, 0.06f, 0.12f, 0.2f, 0.3f, 0.45f, 0.65f, 0.9f)
        return Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.9f to a.dia(c, f, cerca(Offset(centro.x + w * radios[i], centro.y))) },
            center = centro, radius = w * 0.9f)
    }

    /** Las losas y la estera, en escorzo (se abren hacia quien mira). */
    private fun DrawScope.piso(a: AireTemplario, suelo: Float, h: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = h - suelo
        val fuga = Offset(w * 0.5f, suelo - w * 1.3f)
        fun xEn(xb: Float, y: Float) = fuga.x + (xb - fuga.x) * (y - fuga.y) / (h - fuga.y)
        // Las losas (de noche, el charco del brasero: el color de la losa a cada distancia de la ascua).
        val losa = mezcla(P.CALIZA, P.CALIZA_OSC, 0.55f)
        if (a.oscuro) drawRect(charco(a, losa, -0.08f, h, cerca), Offset(0f, suelo), Size(w, alto))
        else drawRect(a.dia(losa, -0.08f), Offset(0f, suelo), Size(w, alto))
        val juntas = Path()
        listOf(0.2f, 0.5f, 0.86f).forEach { f -> juntas.moveTo(0f, suelo + alto * f); juntas.lineTo(w, suelo + alto * f) }
        for (j in -4..4) { val xb = w * 0.5f + j * w * 0.24f; juntas.moveTo(xEn(xb, suelo), suelo); juntas.lineTo(xEn(xb, h), h) }
        drawPath(juntas, a.sombra(0.35f), style = Stroke(1.2f * u))
        // La estera de palma: un trapecio que se abre hacia abajo, con su orilla y su tejido en espiga.
        val e0 = suelo + alto * 0.12f
        val l0 = xEn(w * 0.1f, e0); val r0 = xEn(w * 0.9f, e0)
        val l1 = xEn(w * 0.1f, h + w * 0.02f); val r1 = xEn(w * 0.9f, h + w * 0.02f)
        val estera = poligonoTemplario(l0, e0, r0, e0, r1, h + w * 0.02f, l1, h + w * 0.02f)
        drawPath(Path().apply { addPath(estera, Offset(w * 0.006f, w * 0.004f)) }, a.sombra(0.35f))
        clipPath(estera) {
            if (a.oscuro) drawRect(charco(a, P.PALMA, 0f, h, cerca), Offset(0f, e0), Size(w, h - e0 + w * 0.02f))
            else drawRect(a.dia(P.PALMA, 0f), Offset(0f, e0), Size(w, h - e0 + w * 0.02f))
            // El tejido en espiga: bandas de trazos inclinados, una a cada lado.
            val izq = Path(); val der = Path()
            val bandas = 9
            for (bnd in 0 until bandas) {
                val ya = e0 + (h - e0) * bnd / bandas; val yb = e0 + (h - e0) * (bnd + 1) / bandas
                var xb = w * 0.08f
                while (xb < w * 0.92f) {
                    val xa = xEn(xb, ya); val xc = xEn(xb, yb)
                    val paso = (xEn(xb + w * 0.018f, ya) - xa)
                    if (bnd % 2 == 0) { izq.moveTo(xa, ya); izq.lineTo(xc + paso * 0.8f, yb) } else { der.moveTo(xa + paso * 0.8f, ya); der.lineTo(xc, yb) }
                    xb += w * 0.018f
                }
            }
            val km = cerca(Offset(w * 0.5f, e0 + alto * 0.4f))
            drawPath(izq, a.dia(P.PALMA_OSC, 0f, km).copy(alpha = 0.55f), style = Stroke(0.9f * u))
            drawPath(der, a.dia(P.PALMA_OSC, 0f, km).copy(alpha = 0.4f), style = Stroke(0.9f * u))
            drawPath(der, a.claro(0.18f), style = Stroke(0.5f * u))
            // La orilla: una lista de almagra y otra negra, a lo largo de arriba y de los lados.
            val oA = alto * 0.07f
            drawRect(a.dia(P.ALMAGRA, 0f, km), Offset(0f, e0 + oA * 0.4f), Size(w, oA))
            drawRect(a.dia(Color(0xFF2A2220), 0f, km), Offset(0f, e0 + oA * 1.6f), Size(w, oA * 0.5f))
            listOf(0.13f to 0.135f, 0.86f to 0.865f).forEach { (fa, _) ->
                val xa = w * fa
                drawPath(poligonoTemplario(xEn(xa, e0), e0, xEn(xa + w * 0.02f, e0), e0, xEn(xa + w * 0.02f, h + w * 0.02f), h + w * 0.02f, xEn(xa, h + w * 0.02f), h + w * 0.02f),
                    a.dia(P.ALMAGRA, -0.05f, km))
            }
            // Lo gastado delante del armario.
            drawOval(a.sombra(0.12f), Offset(w * 0.3f, e0 + alto * 0.1f), Size(w * 0.4f, alto * 0.25f))
        }
        drawPath(estera, a.dia(P.PALMA_OSC, -0.2f, cerca(Offset(w * 0.5f, e0))), style = Stroke(1.2f * u))
    }

    /** El brasero de bronce: tres patas de garfio, la taza ancha con su borde, las ascuas (de noche, encendidas) y la badila. */
    private fun DrawScope.brasero(a: AireTemplario, pie: Offset, b: Rect, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val k = cerca(Offset(x, y - w * 0.1f))
        val taza = y - w * 0.125f
        val tw = w * 0.27f
        // La sombra en la estera (de día corrida a la derecha; de noche, la ascua alumbra todo alrededor).
        if (!a.oscuro) drawOval(a.sombra(0.35f), Offset(x - tw * 0.4f, y - w * 0.012f), Size(tw * 1.05f, w * 0.028f))
        else drawOval(a.sombra(0.25f), Offset(x - tw * 0.45f, y - w * 0.008f), Size(tw * 0.9f, w * 0.02f))
        // Las patas.
        val patas = Path()
        listOf(-1f, 0f, 1f).forEach { s ->
            val sx = x + s * tw * 0.36f
            patas.moveTo(x + s * tw * 0.22f, taza + w * 0.02f)
            patas.cubicTo(x + s * tw * 0.32f, taza + w * 0.05f, sx, y - w * 0.04f, sx + s * w * 0.006f, y - w * 0.006f)
        }
        drawPath(patas, a.luz(P.BRONCE, k).let { if (a.oscuro) it else a.dia(P.BRONCE, -0.2f, k) }, style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        listOf(-1f, 0f, 1f).forEach { s -> drawOval(a.dia(P.BRONCE, -0.1f, k), Offset(x + s * tw * 0.36f + s * w * 0.006f - w * 0.01f, y - w * 0.01f), Size(w * 0.02f, w * 0.008f)) }
        // La taza.
        val cuerpo = Path().apply {
            moveTo(x - tw / 2, taza)
            cubicTo(x - tw * 0.5f, taza + w * 0.045f, x - tw * 0.3f, taza + w * 0.062f, x, taza + w * 0.062f)
            cubicTo(x + tw * 0.3f, taza + w * 0.062f, x + tw * 0.5f, taza + w * 0.045f, x + tw / 2, taza)
            close()
        }
        // Las ascuas, amontonadas por encima del borde.
        val ascuas = Path().apply {
            moveTo(x - tw * 0.44f, taza + w * 0.002f)
            cubicTo(x - tw * 0.3f, taza - w * 0.03f, x + tw * 0.25f, taza - w * 0.034f, x + tw * 0.44f, taza + w * 0.002f); close()
        }
        val ardiendo = a.oscuro
        drawPath(ascuas, if (ardiendo) Color(0xFF3A1208) else a.dia(Color(0xFF3C3430), 0f, k))
        clipPath(ascuas) {
            for (j in 0 until 16) {
                val c = Offset(x + tw * (azar(9050, j) - 0.5f) * 0.85f, taza - w * 0.022f * azar(9051, j))
                val rr = w * (0.008f + 0.008f * azar(9052, j))
                val brasa = when {
                    ardiendo -> mezcla(P.BRASA, P.FUEGO, azar(9053, j) * (if (a.noche) 0.4f else 0.8f))
                    a.noche -> mezcla(Color(0xFF6A5048), P.BRASA, 0.55f)
                    else -> mezcla(Color(0xFF8A8078), Color(0xFF4A3A34), azar(9053, j))
                }
                drawCircle(brasa, rr, c)
                drawCircle(if (ardiendo) Color(0xFF4A160A).copy(alpha = 0.6f) else a.sombra(0.3f), rr * 0.5f, c + Offset(rr * 0.35f, rr * 0.3f))
            }
            if (!ardiendo) drawRect(a.dia(Color(0xFFB8B0A8), 0f, k).copy(alpha = 0.5f), Offset(x - tw / 2, taza - w * 0.012f), Size(tw * 0.5f, w * 0.01f))
        }
        drawPath(cuerpo, a.luz(P.BRONCE, k).let { if (a.oscuro) it else a.dia(P.BRONCE, 0f, k) })
        clipPath(cuerpo) {
            cilindroTemplario(Rect(x - tw / 2, taza, x + tw / 2, taza + w * 0.062f), a, k, -1f, 1.3f)
            drawRect(a.sombra(0.3f), Offset(x - tw / 2, taza + w * 0.04f), Size(tw, w * 0.03f))
            // Una banda de gallones repujados.
            for (j in 0 until 10) {
                val xx = x - tw * 0.45f + tw * 0.1f * j
                drawLine(a.sombra(0.35f), Offset(xx, taza + w * 0.008f), Offset(xx + w * 0.003f, taza + w * 0.05f), 1f * u)
            }
            // De noche, el bronce encendido por arriba por la ascua.
            if (ardiendo) drawRect(P.BRASA.copy(alpha = if (a.noche) 0.25f else 0.4f), Offset(x - tw / 2, taza), Size(tw, w * 0.012f))
        }
        // El borde de la taza, con su filo.
        drawRoundRect(a.dia(P.BRONCE, 0.12f, k).let { if (ardiendo) mezcla(it, P.LLAMA, 0.45f) else it }, Offset(x - tw / 2 - w * 0.004f, taza - w * 0.004f), Size(tw + w * 0.008f, w * 0.008f), CornerRadius(w * 0.004f))
        // Las asas de anilla.
        listOf(-1f, 1f).forEach { s -> drawCircle(a.dia(P.BRONCE, -0.1f, k), w * 0.009f, Offset(x + s * tw * 0.52f, taza + w * 0.008f), style = Stroke(1.6f * u)) }
        // La badila, metida en la ascua, inclinada.
        rotate(28f, Offset(x + tw * 0.2f, taza - w * 0.01f)) {
            drawLine(a.luz(P.HIERRO, k).let { if (a.oscuro) it else a.dia(P.HIERRO, 0f, k) }, Offset(x + tw * 0.2f, taza - w * 0.01f), Offset(x + tw * 0.2f, taza - w * 0.1f), w * 0.004f, StrokeCap.Round)
            drawCircle(a.dia(P.HIERRO, 0f, k), w * 0.005f, Offset(x + tw * 0.2f, taza - w * 0.103f), style = Stroke(1.2f * u))
        }
        // La lumbre: el resplandor de las ascuas (solo hasta donde se ve) y las chispas.
        if (ardiendo) {
            val c = Offset(x, taza - w * 0.012f)
            val f = if (a.noche) 0.6f else 1f
            drawCircle(Brush.radialGradient(listOf(P.BRASA.copy(alpha = 0.32f * f), P.LLAMA.copy(alpha = 0.1f * f), Color.Transparent), c, w * 0.2f), w * 0.2f, c)
            drawOval(P.FUEGO.copy(alpha = 0.5f * f), Offset(x - tw * 0.25f, taza - w * 0.016f), Size(tw * 0.45f, w * 0.008f))
            for (j in 0 until 5) drawCircle(P.FUEGO.copy(alpha = 0.7f * f), 0.9f * u, Offset(x + tw * (azar(9060, j) - 0.5f) * 0.5f, taza - w * (0.03f + 0.04f * azar(9061, j))))
        } else if (a.noche) {
            val c = Offset(x, taza - w * 0.012f)
            drawCircle(Brush.radialGradient(listOf(P.BRASA.copy(alpha = 0.2f), Color.Transparent), c, w * 0.08f), w * 0.08f, c)
        }
    }

    /** La espuerta de palma llena de carbón, cortada por el borde de la izquierda. */
    private fun DrawScope.espuerta(a: AireTemplario, pie: Offset, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(pie.x, pie.y - w * 0.04f))
        val ew = w * 0.17f; val eh = w * 0.08f
        val x = pie.x; val y = pie.y
        drawOval(a.sombra(0.35f), Offset(x - ew * 0.42f, y - w * 0.01f), Size(ew, w * 0.022f))
        // El carbón por encima del borde.
        for (j in 0 until 11) {
            val c = Offset(x + ew * (azar(9070, j) - 0.5f) * 0.85f, y - eh - w * 0.006f * azar(9071, j))
            val rr = w * (0.009f + 0.008f * azar(9072, j))
            drawPath(poligonoTemplario(c.x - rr, c.y, c.x - rr * 0.3f, c.y - rr, c.x + rr, c.y - rr * 0.4f, c.x + rr * 0.6f, c.y + rr * 0.6f), a.dia(Color(0xFF26211E), 0f, k))
            drawLine(a.brillo(0.3f, k), Offset(c.x - rr * 0.6f, c.y - rr * 0.3f), Offset(c.x - rr * 0.1f, c.y - rr * 0.9f), 0.7f * u)
        }
        val cesta = Path().apply {
            moveTo(x - ew / 2, y - eh)
            cubicTo(x - ew * 0.5f, y - eh * 0.4f, x - ew * 0.42f, y, x - ew * 0.3f, y)
            lineTo(x + ew * 0.3f, y)
            cubicTo(x + ew * 0.42f, y, x + ew * 0.5f, y - eh * 0.4f, x + ew / 2, y - eh); close()
        }
        drawPath(cesta, a.dia(P.PALMA, -0.05f, k))
        clipPath(cesta) {
            // El trenzado: hileras de puntadas inclinadas.
            val t = Path()
            for (fila in 0 until 6) {
                val yy = y - eh + eh * (fila + 0.5f) / 6f
                var xx = x - ew / 2
                while (xx < x + ew / 2) { t.moveTo(xx, yy - eh * 0.06f); t.lineTo(xx + w * 0.008f, yy + eh * 0.06f); xx += w * 0.012f }
            }
            drawPath(t, a.dia(P.PALMA_OSC, 0f, k).copy(alpha = 0.7f), style = Stroke(1f * u))
            cilindroTemplario(Rect(x - ew / 2, y - eh, x + ew / 2, y), a, k, -1f, 1.2f)
        }
        drawLine(a.dia(P.PALMA, 0.15f, k), Offset(x - ew / 2, y - eh), Offset(x + ew / 2, y - eh), 2f * u)
        // El asa de soga.
        drawArc(a.dia(P.PALMA_OSC, 0f, k), 200f, 140f, false, Offset(x + ew * 0.1f, y - eh - w * 0.03f), Size(ew * 0.36f, w * 0.05f), style = Stroke(2f * u))
    }

    /** La tinaja del agua en su pie de madera, con la tapa de tabla y el jarrillo encima, cortada por el borde de la derecha. */
    private fun DrawScope.tinaja(a: AireTemplario, pie: Offset, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x = pie.x; val y = pie.y
        val k = cerca(Offset(x, y - w * 0.12f))
        // El pie de madera: tres patas y un aro.
        val patas = Path().apply {
            moveTo(x - w * 0.07f, y); lineTo(x - w * 0.05f, y - w * 0.07f)
            moveTo(x + w * 0.07f, y); lineTo(x + w * 0.05f, y - w * 0.07f)
            moveTo(x, y + w * 0.004f); lineTo(x, y - w * 0.06f)
        }
        drawOval(a.sombra(0.35f), Offset(x - w * 0.08f, y - w * 0.01f), Size(w * 0.2f, w * 0.022f))
        drawPath(patas, a.dia(P.CEDRO_OSC, 0f, k), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
        drawRect(a.dia(P.CEDRO, 0f, k), Offset(x - w * 0.065f, y - w * 0.075f), Size(w * 0.13f, w * 0.012f))
        drawRect(a.brillo(0.35f, k), Offset(x - w * 0.065f, y - w * 0.075f), Size(w * 0.13f, 0.9f * u))
        // La tinaja: panza ancha, cuello corto, labio grueso.
        val top = y - w * 0.27f
        val cuerpo = Path().apply {
            moveTo(x - w * 0.03f, y - w * 0.07f)
            cubicTo(x - w * 0.09f, y - w * 0.1f, x - w * 0.1f, y - w * 0.2f, x - w * 0.04f, y - w * 0.245f)
            lineTo(x - w * 0.042f, top); lineTo(x + w * 0.042f, top); lineTo(x + w * 0.04f, y - w * 0.245f)
            cubicTo(x + w * 0.1f, y - w * 0.2f, x + w * 0.09f, y - w * 0.1f, x + w * 0.03f, y - w * 0.07f); close()
        }
        drawPath(cuerpo, a.dia(P.BARRO, 0.05f, k))
        clipPath(cuerpo) {
            // El sudor del barro: más oscuro abajo, donde rezuma.
            drawRect(a.dia(P.BARRO_OSC, 0f, k).copy(alpha = 0.55f), Offset(x - w * 0.1f, y - w * 0.14f), Size(w * 0.2f, w * 0.08f))
            for (j in 0 until 7) drawOval(a.dia(P.BARRO_OSC, -0.1f, k).copy(alpha = 0.5f), Offset(x - w * 0.07f + w * 0.02f * j, y - w * (0.15f + 0.02f * azar(9080, j))), Size(w * 0.006f, w * 0.014f))
            // La cuerda en relieve del hombro.
            val cuerda = Path()
            var xx = x - w * 0.1f
            while (xx < x + w * 0.1f) { cuerda.moveTo(xx, y - w * 0.21f); cuerda.lineTo(xx + w * 0.008f, y - w * 0.2f); xx += w * 0.009f }
            drawPath(cuerda, a.dia(P.BARRO_OSC, 0f, k), style = Stroke(1.2f * u))
            cilindroTemplario(Rect(x - w * 0.1f, top, x + w * 0.1f, y - w * 0.07f), a, k, -1f, 1.4f)
        }
        // La tapa de tabla y el jarrillo encima.
        drawRect(a.dia(P.CEDRO, 0.05f, k), Offset(x - w * 0.055f, top - w * 0.01f), Size(w * 0.11f, w * 0.012f))
        drawRect(a.brillo(0.4f, k), Offset(x - w * 0.055f, top - w * 0.01f), Size(w * 0.11f, 0.9f * u))
        val jx = x - w * 0.015f; val jy = top - w * 0.01f
        val jarro = Path().apply {
            moveTo(jx - w * 0.012f, jy); cubicTo(jx - w * 0.026f, jy - w * 0.012f, jx - w * 0.024f, jy - w * 0.03f, jx - w * 0.01f, jy - w * 0.038f)
            lineTo(jx - w * 0.012f, jy - w * 0.05f); lineTo(jx + w * 0.012f, jy - w * 0.05f); lineTo(jx + w * 0.01f, jy - w * 0.038f)
            cubicTo(jx + w * 0.024f, jy - w * 0.03f, jx + w * 0.026f, jy - w * 0.012f, jx + w * 0.012f, jy); close()
        }
        drawArc(a.dia(P.VIDRIADO_VERDE, -0.1f, k), -90f, 180f, false, Offset(jx + w * 0.008f, jy - w * 0.046f), Size(w * 0.026f, w * 0.026f), style = Stroke(w * 0.005f))
        drawPath(jarro, a.dia(P.VIDRIADO_VERDE, 0f, k))
        clipPath(jarro) { cilindroTemplario(Rect(jx - w * 0.026f, jy - w * 0.05f, jx + w * 0.026f, jy), a, k, -1f, 1.3f) }
        drawRoundRect(a.claro(0.6f), Offset(jx - w * 0.012f, jy - w * 0.032f), Size(w * 0.003f, w * 0.012f), CornerRadius(w * 0.0015f))
    }
}
