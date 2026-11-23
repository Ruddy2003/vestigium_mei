package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

/**
 * El pie del estante de Art nouveau ([MuebleEstanteNouveau]), pintado una vez en la imagen de abajo: el zócalo de caoba con su
 * filete de limoncillo y la rama de marquetería, el faldón festoneado con su colgante tallado en medio y los pies que se abren
 * como raíz; el suelo de baldosas hidráulicas (crema, salvia, ocre y violeta) en escorzo, con su cenefa junto a la pared;
 * delante, la pila de álbumes con la palmatoria de latón y su tulipa (de noche, la tercera luz), la papelera de mimbre con las
 * pruebas estropeadas hechas una bola y, a la derecha, cortado por el borde, el paragüero de grès flameado con el paraguas y el
 * bastón. Todo lo de aquí se mide desde [Rect.bottom] del pie (el lienzo de la imagen guardada es más bajo que el estante).
 */
internal object PieEstanteNouveau {
    private val P = PaletaNouveau

    /** El zócalo, en anchos desde el borde de arriba del pie: su moldura, su cara y dónde acaba el faldón en las esquinas. */
    private const val MOLDURA = 0.026f
    private const val CARA = 0.074f
    private const val FALDON = 0.098f

    fun DrawScope.pie(a: AireNouveau, r: Rect, b: Rect, cerca: (Offset) -> Float) {
        val w = size.width
        val alto = r.bottom
        val piso = b.top + b.height * MuebleEstanteNouveau.PISA
        suelo(a, Rect(0f, b.top + w * CARA, w, alto), piso)
        zocalo(a, r, b, piso)
        rollosSuelo(a, alto, cerca)
        papelera(a, b, alto, cerca)
        paraguero(a, b, alto, cerca)
        albumes(a, b, alto, cerca)
        if (a.lumbre) mapaPie(a, r, b)
        if (a.lumbre) llama(a, b)
    }

    // ---------------------------------------------------------------- el zócalo

    private fun DrawScope.zocalo(a: AireNouveau, r: Rect, b: Rect, piso: Float) {
        val w = size.width; val u = density
        val lado = w * MuebleEstanteNouveau.LADO
        val top = b.top
        val k = 1f
        val yc = top + w * CARA
        val yf = top + w * FALDON
        // Lo de encima del zócalo (la junta con la última tabla va debajo, del color del canto) y la moldura.
        drawRect(a.luz(P.CAOBA, k), Offset(0f, r.top), Size(w, yc - r.top))
        val mo = w * MOLDURA
        drawRect(a.dia(P.CAOBA_LUZ, 0.22f), Offset(0f, top), Size(w, mo * 0.22f))
        drawRect(a.luz(P.CAOBA, k), Offset(0f, top + mo * 0.22f), Size(w, mo * 0.38f))
        drawRect(a.dia(P.CAOBA_LUZ, 0.06f), Offset(0f, top + mo * 0.52f), Size(w, 0.8f * u))
        drawRect(a.luz(P.CAOBA_OSC, k), Offset(0f, top + mo * 0.62f), Size(w, mo * 0.2f))
        drawRect(a.luz(P.CAOBA_HONDA, k), Offset(0f, top + mo * 0.82f), Size(w, mo * 0.18f))
        // La cara del zócalo con su veta, su filete de limoncillo y la rama de marquetería.
        val veta = Path()
        for (j in 0 until 4) {
            val y = top + mo + (yc - top - mo) * (0.2f + 0.2f * j)
            veta.moveTo(0f, y); veta.cubicTo(w * 0.3f, y + 1.4f * u * (azar(1021, j) - 0.5f), w * 0.6f, y - 1f * u, w, y + 0.7f * u)
        }
        drawPath(veta, a.sombra(0.15f), style = Stroke(0.7f * u))
        drawRect(a.luz(P.LIMONCILLO, k).copy(alpha = 0.75f), Offset(lado, top + mo + 2.4f * u), Size(w - 2 * lado, 0.9f * u))
        val rama = Path(); val hojas = Path(); val flores = Path()
        val cy = (top + mo + yc) / 2 + 1.6f * u
        listOf(-1f, 1f).forEach { s ->
            var x = w * 0.5f; var i = 0
            rama.moveTo(x, cy)
            while (i < 4) {
                val x2 = x + s * w * 0.085f
                val arriba = if (i % 2 == 0) -1f else 1f
                rama.cubicTo(x + s * w * 0.03f, cy + arriba * 4f * u, x2 - s * w * 0.03f, cy + arriba * 4f * u, x2, cy)
                val hx = x + s * w * 0.045f
                hojas.moveTo(hx, cy + arriba * 2.6f * u)
                hojas.quadraticTo(hx + s * w * 0.016f, cy + arriba * 7f * u, hx + s * w * 0.03f, cy + arriba * 5f * u)
                hojas.quadraticTo(hx + s * w * 0.012f, cy + arriba * 3f * u, hx, cy + arriba * 2.6f * u); hojas.close()
                if (i == 1 || i == 3) for (q in 0 until 5) {
                    val ang = (2 * Math.PI * q / 5).toFloat()
                    val px = x2 + 2f * u * cos(ang); val py = cy - arriba * 3f * u + 2f * u * sin(ang)
                    flores.addOval(Rect(px - 1.7f * u, py - 1.5f * u, px + 1.7f * u, py + 1.5f * u))
                }
                x = x2; i++
            }
        }
        drawPath(rama, a.luz(P.TENIDA, k), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        drawPath(hojas, a.luz(P.TENIDA, k))
        drawPath(flores, a.luz(P.LIMONCILLO, k))
        // El faldón festoneado: baja en las esquinas, sube en medio y deja colgar un capullo tallado.
        val falda = Path().apply {
            moveTo(lado, yc - 1f); lineTo(w - lado, yc - 1f); lineTo(w - lado, yf)
            cubicTo(w - lado - w * 0.05f, yf, w * 0.7f, yc + w * 0.002f, w * 0.56f, yc + w * 0.004f)
            quadraticTo(w * 0.52f, yc + w * 0.004f, w * 0.5f, yc + w * 0.02f)
            quadraticTo(w * 0.48f, yc + w * 0.004f, w * 0.44f, yc + w * 0.004f)
            cubicTo(w * 0.3f, yc + w * 0.002f, lado + w * 0.05f, yf, lado, yf)
            close()
        }
        // Lo de debajo: la sombra del mueble en el suelo y en el rodapié.
        drawRect(a.sombra(0.7f), Offset(lado, yc), Size(w - 2 * lado, piso - yc))
        translate(0f, 1.6f * u) { drawPath(falda, a.sombra(0.4f)) }
        drawPath(falda, a.luz(P.CAOBA, k))
        drawPath(falda, a.dia(P.CAOBA_LUZ, 0.12f), style = Stroke(1f * u))
        val yema = Path().apply {
            moveTo(w * 0.5f, yc + w * 0.034f)
            cubicTo(w * 0.513f, yc + w * 0.024f, w * 0.51f, yc + w * 0.012f, w * 0.5f, yc + w * 0.008f)
            cubicTo(w * 0.49f, yc + w * 0.012f, w * 0.487f, yc + w * 0.024f, w * 0.5f, yc + w * 0.034f)
            close()
        }
        translate(0.8f * u, 1.2f * u) { drawPath(yema, a.sombra(0.4f)) }
        drawPath(yema, a.dia(P.CAOBA_LUZ, 0.05f))
        // Los montantes que bajan hasta el suelo y se abren como raíz.
        listOf(0f to lado, w - lado to w).forEachIndexed { i, (x0, x1) ->
            drawRect(a.luz(P.CAOBA, k), Offset(x0, top + mo), Size(x1 - x0, piso - top - mo))
            drawRect(a.dia(P.CAOBA_LUZ, 0.16f), Offset(x0, top + mo), Size(1.6f * u, piso - top - mo))
            drawRect(a.sombra(0.3f), Offset(x1 - 2f * u, top + mo), Size(2f * u, piso - top - mo))
            val s = if (i == 0) 1f else -1f
            val cx = (x0 + x1) / 2
            val raiz = Path().apply {
                moveTo(x0, piso - w * 0.06f)
                cubicTo(x0, piso - w * 0.02f, x0 - w * 0.01f, piso - w * 0.004f, x0 - w * 0.018f, piso + w * 0.004f)
                lineTo(x1 + w * 0.018f, piso + w * 0.004f)
                cubicTo(x1 + w * 0.01f, piso - w * 0.004f, x1, piso - w * 0.02f, x1, piso - w * 0.06f)
                close()
            }
            drawOval(a.sombra(0.5f), Offset(x0 - w * 0.03f, piso - w * 0.006f), Size(x1 - x0 + w * 0.07f, w * 0.024f))
            drawPath(raiz, a.luz(P.CAOBA, k))
            drawPath(raiz, a.sombra(0.3f), style = Stroke(0.6f * u))
            val vena = Path().apply { moveTo(cx, piso - w * 0.05f); quadraticTo(cx + s * w * 0.008f, piso - w * 0.016f, cx - s * w * 0.02f, piso + w * 0.002f) }
            drawPath(vena, a.dia(P.CAOBA_LUZ, 0.08f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            // El roce del pie, donde pega la escoba.
            drawRect(a.claro(0.22f), Offset(x0 + (x1 - x0) * 0.2f, piso - w * 0.012f), Size((x1 - x0) * 0.5f, 1f * u))
        }
    }

    // ---------------------------------------------------------------- el suelo

    /** Un punto de la baldosa: el cuadro ([c0]..[c3]: arriba izquierda, arriba derecha, abajo derecha, abajo izquierda) en ([fx], [fy]). */
    private fun en(c: FloatArray, fx: Float, fy: Float): Offset {
        val tx = c[0] + (c[2] - c[0]) * fx; val ty = c[1] + (c[3] - c[1]) * fx
        val bx = c[6] + (c[4] - c[6]) * fx; val by = c[7] + (c[5] - c[7]) * fx
        return Offset(tx + (bx - tx) * fy, ty + (by - ty) * fy)
    }

    /**
     * Las baldosas hidráulicas en escorzo, dentro de [r] (desde el rodapié): la cenefa de violeta y ocre junto a la pared y luego
     * hileras que crecen hacia delante; cada baldosa crema con un cuarto de círculo salvia en cada esquina (que con las vecinas
     * forma un círculo) y, en medio, una flor de cinco pétalos ocre con el corazón violeta. Con sus juntas, el gastado y una
     * mancha de tinta.
     */
    private fun DrawScope.suelo(a: AireNouveau, r: Rect, piso: Float) {
        val w = size.width; val u = density
        drawRect(a.luz(P.BALDOSA, 1f), r.topLeft, r.size)
        val fuga = Offset(w * 0.5f, r.top - w * 0.6f)
        val filas = floatArrayOf(0f, 0.1f, 0.24f, 0.4f, 0.6f, 0.82f, 1.06f)
        val n = 13
        val esquinas = Path(); val flores = Path(); val corazones = Path(); val juntas = Path()
        fun xEn(i: Float, y: Float): Float {
            val x0 = -w * 0.15f + (w * 1.3f) * i / n
            val t = (y - fuga.y) / (r.bottom - fuga.y)
            return fuga.x + (x0 - fuga.x) * t
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            val yc = r.top + r.height * filas[1]
            drawRect(a.luz(P.BALDOSA_VIOLETA, 1f), Offset(0f, r.top), Size(w, yc - r.top))
            drawRect(a.luz(P.BALDOSA_OCRE, 1f), Offset(0f, r.top + (yc - r.top) * 0.38f), Size(w, (yc - r.top) * 0.26f))
            for (j in 1 until filas.size - 1) {
                val ya = r.top + r.height * filas[j]; val yb = r.top + r.height * filas[j + 1]
                for (i in 0 until n) {
                    val c = floatArrayOf(xEn(i.toFloat(), ya), ya, xEn(i + 1f, ya), ya, xEn(i + 1f, yb), yb, xEn(i.toFloat(), yb), yb)
                    listOf(0f to 0f, 1f to 0f, 1f to 1f, 0f to 1f).forEach { (fx, fy) ->
                        val p0 = en(c, fx, fy)
                        esquinas.moveTo(p0.x, p0.y)
                        val m = 8
                        for (q in 0..m) {
                            val ang = (Math.PI / 2 * q / m).toFloat()
                            val ex = if (fx == 0f) cos(ang) * 0.3f else 1f - cos(ang) * 0.3f
                            val ey = if (fy == 0f) sin(ang) * 0.3f else 1f - sin(ang) * 0.3f
                            val p = en(c, ex, ey); esquinas.lineTo(p.x, p.y)
                        }
                        esquinas.close()
                    }
                    val cc = en(c, 0.5f, 0.5f)
                    val ex = (en(c, 0.62f, 0.5f).x - cc.x); val ey = (en(c, 0.5f, 0.62f).y - cc.y)
                    for (q in 0 until 5) {
                        val ang = (2 * Math.PI * q / 5 - Math.PI / 2).toFloat()
                        val pc = Offset(cc.x + ex * cos(ang), cc.y + ey * sin(ang))
                        flores.addOval(Rect(pc.x - ex * 0.72f, pc.y - ey * 0.72f, pc.x + ex * 0.72f, pc.y + ey * 0.72f))
                    }
                    corazones.addOval(Rect(cc.x - ex * 0.45f, cc.y - ey * 0.45f, cc.x + ex * 0.45f, cc.y + ey * 0.45f))
                    juntas.moveTo(c[0], c[1]); juntas.lineTo(c[6], c[7])
                }
                juntas.moveTo(0f, ya); juntas.lineTo(w, ya)
            }
            drawPath(esquinas, a.luz(mezcla(P.BALDOSA_SALVIA, P.BALDOSA, 0.2f), 1f))
            drawPath(flores, a.luz(P.BALDOSA_OCRE, 1f))
            drawPath(corazones, a.luz(P.BALDOSA_VIOLETA, 1f))
            drawPath(juntas, a.sombra(0.34f), style = Stroke(0.8f * u))
            // El suelo algo más hondo que las baldosas nuevas: el polvo y el uso.
            drawRect(a.sombra(0.12f), Offset(0f, r.top), Size(w, r.height))
            // El gastado: más claro por donde se pisa, y una mancha de tinta.
            drawOval(a.claro(0.16f), Offset(w * 0.25f, r.bottom - r.height * 0.42f), Size(w * 0.5f, r.height * 0.5f))
            drawOval(a.luz(P.TINTA, 1f).copy(alpha = 0.4f), Offset(w * 0.6f, r.top + r.height * 0.6f), Size(w * 0.04f, w * 0.014f))
            drawOval(a.luz(P.TINTA, 1f).copy(alpha = 0.3f), Offset(w * 0.645f, r.top + r.height * 0.66f), Size(w * 0.012f, w * 0.005f))
            // La sombra del mueble en el suelo, junto al zócalo.
            drawRect(Brush.verticalGradient(listOf(a.sombra(0.6f), Color.Transparent), piso - w * 0.03f, piso + w * 0.05f), Offset(0f, r.top), Size(w, piso + w * 0.05f - r.top))
        }
    }

    // ---------------------------------------------------------------- lo del suelo

    /**
     * Los carteles enrollados que esperan en el suelo, a la izquierda, cortados por el borde: tres rollos acostados (dos abajo y
     * uno encima), de papel de estraza y de papel crema, atados con bramante, con la espiral de las hojas en la boca y el canto de
     * las tintas.
     */
    private fun DrawScope.rollosSuelo(a: AireNouveau, alto: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = cerca(Offset(w * 0.1f, alto - w * 0.08f))
        val rollos = listOf(
            floatArrayOf(-0.06f, 0.175f, alto / w - 0.098f, 0.022f, 0f),
            floatArrayOf(-0.05f, 0.205f, alto / w - 0.062f, 0.026f, 1f),
            floatArrayOf(-0.04f, 0.15f, alto / w - 0.128f, 0.021f, 2f),
        )
        drawOval(a.sombra(0.45f), Offset(-w * 0.05f, alto - w * 0.05f), Size(w * 0.29f, w * 0.03f))
        rollos.forEach { (x0, x1, cy, rr, i) ->
            val x0p = x0 * w; val x1p = x1 * w; val c = cy * w; val r = rr * w
            val papel = if (i == 1f) P.ESTRAZA else if (i == 0f) Color(0xFFD8C8A0) else Color(0xFFB9A3C4)
            // El cuerpo: el claro arriba (la luz), el hondo abajo.
            drawRect(a.luz(papel, k), Offset(x0p, c - r), Size(x1p - x0p, r * 2))
            drawRect(a.dia(papel, 0.3f, k), Offset(x0p, c - r * 0.8f), Size(x1p - x0p, r * 0.35f))
            drawRect(a.sombra(0.25f), Offset(x0p, c + r * 0.2f), Size(x1p - x0p, r * 0.5f))
            drawRect(a.sombra(0.4f), Offset(x0p, c + r * 0.7f), Size(x1p - x0p, r * 0.3f))
            if (a.lumbre) drawRect(a.filo(0.4f * k), Offset(x0p, c - r), Size(x1p - x0p, 0.8f * u))
            // Las arrugas del envoltorio.
            for (j in 0 until 4) {
                val x = x0p + (x1p - x0p) * (0.2f + 0.2f * j)
                drawLine(a.sombra(0.2f), Offset(x, c - r * 0.9f), Offset(x + r * 0.6f, c + r * 0.8f), 0.6f * u)
            }
            // El bramante, dos vueltas.
            listOf(0.3f, 0.7f).forEach { f ->
                val x = x0p + (x1p - x0p) * f
                drawLine(a.luz(Color(0xFF8A6E44), k), Offset(x, c - r), Offset(x + r * 0.3f, c + r), 1.2f * u)
            }
            // La boca, a la derecha: el canto de las vueltas con la espiral y el canto de las tintas.
            val boca = Rect(x1p - r * 0.42f, c - r, x1p + r * 0.42f, c + r)
            drawOval(a.luz(P.PAPEL_OSC, k), boca.topLeft, boca.size)
            val espiral = Path()
            for (q in 0..18) {
                val t = q / 18f
                val ang = t * 2.4f * 2f * Math.PI.toFloat()
                val rq = 0.2f + 0.75f * t
                val p = Offset(boca.center.x + boca.width * 0.5f * rq * cos(ang), boca.center.y + boca.height * 0.5f * rq * sin(ang))
                if (q == 0) espiral.moveTo(p.x, p.y) else espiral.lineTo(p.x, p.y)
            }
            drawPath(espiral, a.luz(Color(0xFF8A7A5A), k), style = Stroke(0.55f * u))
            drawArc(a.luz(if (i == 1f) P.IRIS else Color(0xFFC9583E), k).copy(alpha = 0.7f), -80f, 160f, false, boca.deflate(0.6f * u).topLeft, boca.deflate(0.6f * u).size, style = Stroke(0.8f * u))
            drawOval(a.sombra(0.35f), boca.topLeft, boca.size, style = Stroke(0.5f * u))
        }
    }

    /** La pila de álbumes acostados en el suelo y, encima, la palmatoria de latón con su vela y su tulipa. */
    private fun DrawScope.albumes(a: AireNouveau, b: Rect, alto: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val vx = w * MuebleEstanteNouveau.VELA_X
        val vy = b.top + b.height * MuebleEstanteNouveau.VELA_Y
        val k = if (a.lumbre) 1f else cerca(Offset(vx, alto - w * 0.08f))
        val base = alto - w * 0.03f
        val cols = listOf(Color(0xFF5A3A5E), Color(0xFF34503F), Color(0xFF7A3A26), Color(0xFF6E4E34))
        val anchos = listOf(0.38f, 0.34f, 0.31f, 0.26f)
        var y = base
        drawOval(a.sombra(0.5f), Offset(vx - w * 0.17f, base - w * 0.018f), Size(w * 0.44f, w * 0.038f))
        cols.forEachIndexed { i, c ->
            val g = w * (0.031f - 0.002f * i)
            val an = w * anchos[i]
            val x = vx - an / 2 + w * (0.014f * (if (i % 2 == 0) -1 else 1)) - w * 0.02f
            drawRect(a.sombra(0.3f), Offset(x + w * 0.006f, y - g * 0.2f), Size(an, g * 0.25f))
            drawRect(a.luz(P.PAPEL_OSC, k), Offset(x + w * 0.006f, y - g * 0.78f), Size(an - w * 0.008f, g * 0.56f))
            for (j in 0 until 3) drawLine(a.sombra(0.15f), Offset(x + w * 0.01f, y - g * (0.68f - 0.16f * j)), Offset(x + an - w * 0.004f, y - g * (0.68f - 0.16f * j)), 0.4f * u)
            drawRect(a.luz(c, k), Offset(x, y - g), Size(an, g * 0.24f))
            drawRect(a.luz(c, k), Offset(x, y - g * 0.22f), Size(an, g * 0.22f))
            drawRoundRect(a.luz(c, k), Offset(x - w * 0.004f, y - g), Size(w * 0.02f, g), CornerRadius(g * 0.4f))
            drawRect(a.dia(c, 0.28f, k), Offset(x, y - g), Size(an, g * 0.08f))
            if (a.lumbre) drawRect(a.filo(0.55f * k), Offset(x, y - g), Size(an, 0.8f * u))
            drawLine(a.luz(P.ORO, k), Offset(x + w * 0.005f, y - g * 0.72f), Offset(x + w * 0.005f, y - g * 0.3f), 1f * u)
            y -= g
        }
        // La palmatoria: el platillo con su asa, el cañón, la vela y la tulipa de vidrio.
        val pr = w * 0.034f
        drawOval(a.sombra(0.4f), Offset(vx - pr * 0.9f, y - pr * 0.16f), Size(pr * 2.2f, pr * 0.34f))
        drawOval(a.luz(P.LATON_OSC, k), Offset(vx - pr, y - pr * 0.34f), Size(pr * 2, pr * 0.4f))
        drawOval(a.luz(P.LATON, k), Offset(vx - pr, y - pr * 0.44f), Size(pr * 2, pr * 0.36f))
        drawOval(a.brillo(0.5f, k), Offset(vx - pr * 0.6f, y - pr * 0.4f), Size(pr * 0.7f, pr * 0.12f))
        drawArc(a.luz(P.LATON_OSC, k), 200f, 300f, false, Offset(vx + pr * 0.82f, y - pr * 0.62f), Size(pr * 0.6f, pr * 0.55f), style = Stroke(2f * u))
        drawRect(a.luz(P.LATON, k), Offset(vx - pr * 0.24f, y - pr * 0.9f), Size(pr * 0.48f, pr * 0.55f))
        drawRect(a.brillo(0.45f, k), Offset(vx - pr * 0.18f, y - pr * 0.9f), Size(pr * 0.1f, pr * 0.55f))
        val cera = Color(0xFFF0E6CC)
        val vt = vy + w * 0.01f
        if (y - pr * 0.9f > vt) {
            drawRect(a.dia(cera, 0f, k), Offset(vx - pr * 0.17f, vt), Size(pr * 0.34f, y - pr * 0.9f - vt))
            drawRect(a.sombra(0.2f), Offset(vx + pr * 0.04f, vt), Size(pr * 0.13f, y - pr * 0.9f - vt))
            drawLine(a.dia(cera, 0.1f, k), Offset(vx - pr * 0.18f, vt + 2f * u), Offset(vx - pr * 0.18f, vt + (y - vt) * 0.3f), 1.6f * u, StrokeCap.Round)
        }
        // La tulipa de vidrio en su anillo, alrededor de la llama.
        val tul = tulipaVela(vx, vy, pr)
        if (!a.lumbre) {
            drawPath(tul, Color(0xFFE8EEE8).copy(alpha = 0.35f))
            drawLine(a.claro(0.75f), Offset(vx - pr * 0.6f, vy - w * 0.03f), Offset(vx - pr * 0.72f, vy + w * 0.01f), 1.2f * u)
            drawLine(a.luz(P.TINTA, k), Offset(vx, vt), Offset(vx + 0.5f * u, vt - 2.6f * u), 1f * u)
        }
        drawPath(tul, a.luz(P.LATON_OSC, k).copy(alpha = 0.55f), style = Stroke(0.7f * u))
        drawRect(a.luz(P.LATON, k), Offset(vx - pr * 0.45f, vy + w * 0.022f), Size(pr * 0.9f, w * 0.006f))
    }

    /** La tulipa de vidrio de la palmatoria alrededor de la llama en ([vx], [vy]). */
    private fun tulipaVela(vx: Float, vy: Float, pr: Float): Path = Path().apply {
        moveTo(vx - pr * 0.42f, vy + pr * 0.66f)
        cubicTo(vx - pr * 1.0f, vy + pr * 0.36f, vx - pr * 1.0f, vy - pr * 0.75f, vx - pr * 0.58f, vy - pr * 1.25f)
        lineTo(vx + pr * 0.58f, vy - pr * 1.25f)
        cubicTo(vx + pr * 1.0f, vy - pr * 0.75f, vx + pr * 1.0f, vy + pr * 0.36f, vx + pr * 0.42f, vy + pr * 0.66f)
        close()
    }

    /** La papelera de mimbre, entre la pila y el paragüero, con las pruebas estropeadas hechas una bola y una caída al lado. */
    private fun DrawScope.papelera(a: AireNouveau, b: Rect, alto: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val cx = w * 0.665f
        val base = alto - w * 0.05f
        val h = w * 0.15f
        val top = base - h
        val k = cerca(Offset(cx, top + h * 0.5f))
        val sombraX = if (a.oscuro) -1f else 1f
        drawOval(a.sombra(0.45f), Offset(cx - w * 0.08f + sombraX * w * 0.02f, base - w * 0.014f), Size(w * 0.2f, w * 0.03f))
        // Las bolas de papel que asoman por arriba (detrás del borde).
        val bolas = listOf(Triple(-0.045f, -0.02f, 0.03f), Triple(0.02f, -0.03f, 0.034f), Triple(0.06f, -0.012f, 0.026f))
        bolas.forEachIndexed { i, (dx, dy, rr) -> bola(Offset(cx + dx * w, top + dy * w), rr * w, a, k, 1031 + i) }
        // El cesto, más ancho arriba, de mimbre: los mimbres de pie y las vueltas tejidas.
        val cesto = Path().apply {
            moveTo(cx - w * 0.085f, top); lineTo(cx + w * 0.085f, top); lineTo(cx + w * 0.062f, base); lineTo(cx - w * 0.062f, base); close()
        }
        drawPath(cesto, a.luz(Color(0xFFB98E52), k))
        clipPath(cesto) {
            var y = top + 2f * u; var i = 0
            while (y < base) {
                drawRect(a.luz(if (i % 2 == 0) Color(0xFFD2A866) else Color(0xFF9A7240), k), Offset(cx - w * 0.09f, y), Size(w * 0.18f, 2.2f * u))
                y += 3.2f * u; i++
            }
            for (j in -6..6) {
                val xt = cx + j * w * 0.013f
                drawLine(a.sombra(0.28f), Offset(xt, top), Offset(cx + j * w * 0.0095f, base), 0.9f * u)
            }
            drawRect(a.sombra(0.28f), Offset(cx + w * (if (a.oscuro) -0.09f else 0.03f), top), Size(w * 0.06f, h))
            drawRect(a.brillo(0.18f, k), Offset(cx + w * (if (a.oscuro) 0.04f else -0.07f), top), Size(w * 0.025f, h))
        }
        drawRect(a.luz(Color(0xFF8A6438), k), Offset(cx - w * 0.088f, top - 1.6f * u), Size(w * 0.176f, 3.4f * u))
        drawRect(a.brillo(0.35f, k), Offset(cx - w * 0.088f, top - 1.6f * u), Size(w * 0.176f, 0.8f * u))
        // Una bola caída en el suelo, delante.
        bola(Offset(cx + w * 0.11f, alto - w * 0.04f), w * 0.024f, a, k, 1039)
    }

    /** Una prueba estropeada hecha una bola: el papel arrugado con sus pliegues y un resto de tinta. */
    private fun DrawScope.bola(c: Offset, r: Float, a: AireNouveau, k: Float, s: Int) {
        val u = density
        val p = Path()
        val n = 9
        for (i in 0..n) {
            val ang = (2 * Math.PI * i / n).toFloat()
            val rr = r * (0.82f + 0.3f * azar(s, i))
            val q = Offset(c.x + rr * cos(ang), c.y + rr * 0.86f * sin(ang))
            if (i == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
        }
        p.close()
        drawPath(p, a.luz(P.PAPEL, k))
        clipPath(p) {
            drawRect(a.sombra(0.25f), Offset(c.x, c.y - r), Size(r * 1.2f, r * 2.2f))
            val pl = Path()
            for (i in 0 until 4) {
                pl.moveTo(c.x - r * 0.6f + r * 0.4f * i, c.y - r * 0.7f); pl.lineTo(c.x - r * 0.2f + r * 0.3f * i, c.y + r * 0.1f * (i - 1)); pl.lineTo(c.x - r * 0.5f + r * 0.35f * i, c.y + r * 0.8f)
            }
            drawPath(pl, a.sombra(0.3f), style = Stroke(0.6f * u, join = StrokeJoin.Round))
            drawRect(a.luz(if (s % 2 == 0) Color(0xFFC9583E) else P.IRIS, k).copy(alpha = 0.6f), Offset(c.x - r * 0.5f, c.y - r * 0.1f), Size(r * 0.45f, r * 0.3f))
        }
        drawPath(p, a.sombra(0.3f), style = Stroke(0.5f * u))
    }

    /** El paragüero de grès flameado, cortado por el borde derecho, con el paraguas plegado y el bastón de puño de plata. */
    private fun DrawScope.paraguero(a: AireNouveau, b: Rect, alto: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val x0 = w * 0.8f; val x1 = w * 1.04f
        val base = alto - w * 0.022f
        val top = base - w * 0.21f
        val k = cerca(Offset(w * 0.9f, top))
        val arriba = b.top + w * 0.012f
        // El bastón y el paraguas, detrás de la boca.
        drawLine(a.sombra(0.4f), Offset(w * 0.858f, top + w * 0.02f), Offset(w * 0.838f + 2f * u, arriba + w * 0.01f + 2f * u), 3f * u)
        drawLine(a.luz(Color(0xFF4A2E1E), k), Offset(w * 0.858f, top + w * 0.02f), Offset(w * 0.838f, arriba + w * 0.01f), 3f * u, StrokeCap.Round)
        drawCircle(a.luz(Color(0xFFC8CCD0), k), 3.6f * u, Offset(w * 0.838f, arriba + w * 0.006f))
        drawCircle(a.brillo(0.6f, k), 1.2f * u, Offset(w * 0.835f, arriba + w * 0.003f))
        val tela = Path().apply {
            moveTo(w * 0.882f, top + w * 0.02f); lineTo(w * 0.935f, top + w * 0.02f)
            quadraticTo(w * 0.94f, arriba + w * 0.12f, w * 0.917f, arriba + w * 0.045f)
            lineTo(w * 0.9f, arriba + w * 0.045f)
            quadraticTo(w * 0.876f, arriba + w * 0.12f, w * 0.882f, top + w * 0.02f); close()
        }
        drawPath(tela, a.luz(Color(0xFF2A2834), k))
        clipPath(tela) {
            drawLine(a.luz(Color(0xFF3E3A4A), k), Offset(w * 0.9f, arriba), Offset(w * 0.892f, top + w * 0.03f), 1.2f * u)
            drawLine(a.sombra(0.4f), Offset(w * 0.918f, arriba), Offset(w * 0.925f, top + w * 0.03f), 1.4f * u)
        }
        drawLine(a.brillo(0.3f, k), Offset(w * 0.897f, top), Offset(w * 0.905f, arriba + w * 0.06f), 0.8f * u)
        drawLine(a.luz(Color(0xFF4A2E1E), k), Offset(w * 0.908f, arriba + w * 0.045f), Offset(w * 0.908f, arriba + w * 0.016f), 2.4f * u)
        drawArc(a.luz(Color(0xFF6A3E22), k), 180f, 200f, false, Offset(w * 0.908f, arriba - w * 0.002f), Size(w * 0.032f, w * 0.032f), style = Stroke(2.8f * u, cap = StrokeCap.Round))
        // El paragüero.
        val cuerpo = Rect(x0, top, x1, base)
        drawOval(a.sombra(0.5f), Offset(x0 - w * 0.04f, base - w * 0.012f), Size(x1 - x0 + w * 0.05f, w * 0.028f))
        drawRect(a.luz(P.GRES_BARRO, k), cuerpo.topLeft, cuerpo.size)
        clipRect(cuerpo.left, cuerpo.top, cuerpo.right, cuerpo.bottom) {
            val esmalte = Path().apply {
                moveTo(x0, top); lineTo(x1, top); lineTo(x1, base - w * 0.06f)
                var x = x1; var i = 0
                while (x > x0) { quadraticTo(x - w * 0.012f, base - w * (0.02f + 0.04f * azar(1011, i)), x - w * 0.025f, base - w * 0.065f); x -= w * 0.025f; i++ }
                close()
            }
            drawPath(esmalte, a.luz(P.GRES, k))
            for (i in 0 until 7) {
                val x = x0 + w * 0.016f + w * 0.032f * i
                drawLine(a.luz(P.GRES_CHORRO, k).copy(alpha = 0.6f), Offset(x, top), Offset(x + w * 0.004f, top + w * (0.08f + 0.07f * azar(1012, i))), w * 0.01f, StrokeCap.Round)
            }
            drawRect(a.brillo(0.2f, k), Offset(x0 + w * 0.024f, top), Size(w * 0.03f, base - top))
            drawRect(a.brillo(0.35f, k), Offset(x0 + w * 0.032f, top), Size(w * 0.008f, base - top))
            drawRect(a.sombra(0.32f), Offset(x0, top), Size(w * 0.012f, base - top))
            if (a.lumbre) drawRect(a.filo(0.5f * k), Offset(x0, top), Size(1.4f * u, base - top))
        }
        drawOval(a.luz(Color(0xFF1E3A40), k), Offset(x0, top - w * 0.014f), Size(x1 - x0, w * 0.028f))
        drawOval(a.luz(P.GRES_CHORRO, k), Offset(x0, top - w * 0.014f), Size(x1 - x0, w * 0.028f), style = Stroke(1.6f * u))
    }

    // ---------------------------------------------------------------- la noche del pie

    /** Lo que se apaga lejos de la palmatoria y del aplique, en una imagen chica estirada sobre todo el pie. */
    private fun DrawScope.mapaPie(a: AireNouveau, r: Rect, b: Rect) {
        val w = size.width
        val y0 = r.top.toInt(); val y1 = ceil(r.bottom).toInt()
        if (y1 <= y0) return
        val cols = 36
        val filas = ((y1 - y0) / (w / cols)).toInt().coerceIn(2, 100)
        val yV = (b.top + b.height * MuebleEstanteNouveau.VELA_Y) / w
        val img = ImageBitmap(cols, filas)
        CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(img), Size(cols.toFloat(), filas.toFloat())) {
            for (j in 0 until filas) for (i in 0 until cols) {
                val x = (i + 0.5f) / cols
                val y = (y0 + (j + 0.5f) / filas * (y1 - y0)) / w
                val k = MuebleEstanteNouveau.calor(x, y, yV, a.noche, FloatArray(0))
                val c = if (a.oscuro) {
                    val osc = (1f - k) * 0.84f; val cal = 0.18f * k
                    val al = (osc + cal).coerceIn(0f, 1f)
                    mezcla(a.hondo, P.LLAMA, if (al > 0.001f) cal / al else 0f).copy(alpha = al)
                } else {
                    val violeta = (0.14f + 0.18f * x) * (1f - k)
                    val cal = 0.2f * k
                    val al = (violeta + cal).coerceIn(0f, 1f)
                    mezcla(P.SOMBRA_OCASO, P.LLAMA, if (al > 0.001f) cal / al else 0f).copy(alpha = al)
                }
                drawRect(c, Offset(i.toFloat(), j.toFloat()), Size(1f, 1f))
            }
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, y0), IntSize(ceil(w).toInt(), y1 - y0))
    }

    /** La llama de la palmatoria encendida en su tulipa, con su resplandor (de noche, encima de todo). */
    private fun DrawScope.llama(a: AireNouveau, b: Rect) {
        val w = size.width; val u = density
        val vx = w * MuebleEstanteNouveau.VELA_X
        val vy = b.top + b.height * MuebleEstanteNouveau.VELA_Y
        val f = if (a.oscuro) (if (a.noche) 0.65f else 1f) else 0.6f
        mancha(Offset(vx, vy), w * 0.3f, P.LLAMA.copy(alpha = 0.22f * f))
        mancha(Offset(vx, vy), w * 0.08f, P.FUEGO.copy(alpha = 0.45f * f))
        val pr = w * 0.034f
        val tul = tulipaVela(vx, vy, pr)
        drawPath(tul, P.LLAMA.copy(alpha = 0.32f * f))
        val l = Path().apply {
            moveTo(vx, vy - pr * 0.9f); quadraticTo(vx + pr * 0.3f, vy - pr * 0.1f, vx, vy + pr * 0.3f); quadraticTo(vx - pr * 0.3f, vy - pr * 0.1f, vx, vy - pr * 0.9f); close()
        }
        drawPath(l, P.LLAMA)
        drawOval(P.FUEGO, Offset(vx - pr * 0.11f, vy - pr * 0.45f), Size(pr * 0.22f, pr * 0.6f))
        drawPath(tul, P.LATON_OSC.copy(alpha = 0.5f), style = Stroke(0.7f * u))
        drawRect(a.luz(P.LATON, 1f), Offset(vx - pr * 0.45f, vy + w * 0.022f), Size(pr * 0.9f, w * 0.006f))
        drawLine(P.FUEGO.copy(alpha = 0.55f), Offset(vx - pr * 0.6f, vy - pr * 0.9f), Offset(vx - pr * 0.72f, vy + pr * 0.3f), 0.9f * u)
    }
}
