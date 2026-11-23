package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La biblioteca de caoba del estante de Art nouveau ([MuebleEstanteNouveau]): la cornisa con su gola y el friso de marquetería
 * de flores; los montantes con su canal y el tallo tallado que baja por él, y su ménsula de hoja bajo la cornisa; cada casillero
 * forrado de papel-cuero dorado estampado, con la tabla de encima vista por debajo, sus caras de dentro, sus sombras y el
 * faldón festoneado que cuelga de la tabla de arriba; las tablas con su bocel y su filete de limoncillo; la cartela de
 * marquetería del mes; y la luz de la noche sobre el cuerpo.
 */
internal object ObraEstanteNouveau {
    private val P = PaletaNouveau

    /** Hacia dónde mira la luz en el cuerpo: de día, del escaparate (izquierda); de noche, del aplique (derecha). */
    private fun luzIzq(a: AireNouveau) = !a.oscuro

    // ---------------------------------------------------------------- la cornisa (en la imagen de arriba)

    /**
     * La cornisa, de arriba abajo: la cara de encima (donde pisa el bodegón), el listel, la gola, el junquillo, el friso de
     * marquetería entre los dados de los montantes y el cuarto bocel de abajo. De noche, por tramos, con la luz del aplique.
     */
    fun DrawScope.cornisa(r: Rect, a: AireNouveau, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = w * MuebleEstanteNouveau.CORNISA
        fun y(f: Float) = r.top + alto * f
        val tramos = if (a.lumbre) 14 else 1
        for (t in 0 until tramos) {
            val x0 = w * t / tramos; val x1 = w * (t + 1) / tramos + 1f
            val k = cerca(Offset((x0 + x1) / 2, r.top + alto * 0.5f))
            fun banda(f0: Float, f1: Float, c: Color) = drawRect(c, Offset(x0, y(f0)), Size(x1 - x0, y(f1) - y(f0) + 0.6f))
            banda(0f, 0.09f, a.dia(P.CAOBA_LUZ, 0.12f, k))
            banda(0.09f, 0.17f, a.dia(P.CAOBA_LUZ, 0.3f, k))
            if (a.lumbre) banda(0.09f, 0.13f, a.filo(0.7f * k))
            banda(0.17f, 0.24f, a.luz(P.CAOBA, k))
            banda(0.24f, 0.31f, a.luz(P.CAOBA_OSC, k))
            banda(0.31f, 0.38f, a.dia(P.CAOBA, 0.04f, k))
            banda(0.38f, 0.43f, a.dia(P.CAOBA_LUZ, 0.1f, k))
            banda(0.43f, 0.46f, a.luz(P.CAOBA_HONDA, k))
            banda(0.46f, 0.86f, a.luz(P.CAOBA, k))
            banda(0.86f, 0.9f, a.luz(P.CAOBA_HONDA, k))
            banda(0.9f, 0.95f, a.dia(P.CAOBA_LUZ, 0.06f, k))
            banda(0.95f, 1.02f, a.luz(P.CAOBA_OSC, k))
        }
        // La veta de la gola y del friso, una sola pasada.
        val veta = Path()
        for (j in 0 until 7) {
            val yy = y(0.48f + 0.055f * j)
            veta.moveTo(0f, yy); veta.cubicTo(w * 0.3f, yy + 1.4f * u * (azar(901, j) - 0.5f), w * 0.62f, yy - 1.1f * u, w, yy + 0.7f * u)
        }
        drawPath(veta, a.sombra(0.13f), style = Stroke(0.7f * u))
        // El friso: la rama de marquetería, de cada lado del centro, entre los dados.
        val lado = w * MuebleEstanteNouveau.LADO
        val f0 = y(0.48f); val f1 = y(0.84f)
        val dados = listOf(0f to lado * 1.06f, w - lado * 1.06f to w)
        marqueteria(Rect(lado * 1.06f + w * 0.01f, f0, w - lado * 1.06f - w * 0.01f, f1), a, cerca)
        dados.forEachIndexed { i, (d0, d1) ->
            val k = cerca(Offset((d0 + d1) / 2, (f0 + f1) / 2))
            val d = Rect(d0, y(0.44f), d1, y(0.9f))
            drawRect(a.luz(P.CAOBA, k), d.topLeft, d.size)
            drawRect(a.dia(P.CAOBA_LUZ, 0.12f, k), d.topLeft, Size(d.width, 1.4f * u))
            drawRect(if (luzIzq(a)) a.claro(0.22f) else a.filo(0.35f * k), Offset(if (luzIzq(a)) d.left else d.right - 1.5f * u, d.top), Size(1.5f * u, d.height))
            drawRect(a.sombra(0.3f), Offset(if (luzIzq(a)) d.right - 1.6f * u else d.left, d.top), Size(1.6f * u, d.height))
            // En el dado, una hoja tallada que baja.
            val c = d.center
            val hoja = Path().apply {
                moveTo(c.x, d.top + d.height * 0.14f)
                cubicTo(c.x + d.width * 0.34f, d.top + d.height * 0.3f, c.x + d.width * 0.2f, d.bottom - d.height * 0.22f, c.x, d.bottom - d.height * 0.1f)
                cubicTo(c.x - d.width * 0.2f, d.bottom - d.height * 0.22f, c.x - d.width * 0.34f, d.top + d.height * 0.3f, c.x, d.top + d.height * 0.14f)
                close()
            }
            translate(1f * u, 1.2f * u) { drawPath(hoja, a.sombra(0.4f)) }
            drawPath(hoja, a.dia(P.CAOBA_LUZ, 0f, k))
            drawPath(hoja, a.sombra(0.35f), style = Stroke(0.6f * u))
            drawLine(a.luz(P.CAOBA_OSC, k), Offset(c.x, d.top + d.height * 0.22f), Offset(c.x, d.bottom - d.height * 0.16f), 0.8f * u)
        }
        // El canto de luz de la cara de encima y la línea de sombra del cuarto bocel.
        drawLine(a.claro(0.5f), Offset(0f, y(0.095f)), Offset(w, y(0.095f)), 1f * u)
        drawLine(a.sombra(0.4f), Offset(0f, y(0.46f)), Offset(w, y(0.46f)), 1.1f * u)
        // El roce de los años en el canto: la caoba gastada a lo claro.
        val roce = Path()
        for (j in 0 until 9) {
            val x = w * (0.08f + 0.84f * azar(905, j))
            roce.moveTo(x, y(0.12f)); roce.lineTo(x + w * (0.01f + 0.03f * azar(906, j)), y(0.12f) + 0.4f * u)
        }
        drawPath(roce, a.claro(0.3f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
    }

    /**
     * La rama de marquetería del friso dentro de [r]: un tallo de madera teñida de verde que corre en látigo desde el centro
     * hacia los lados, con sus hojas, y flores de limoncillo y sicomoro; un filete de boj arriba y abajo.
     */
    private fun DrawScope.marqueteria(r: Rect, a: AireNouveau, cerca: (Offset) -> Float) {
        val u = density
        val k = cerca(r.center)
        val tallo = Path(); val hojas = Path(); val flores = Path(); val corazones = Path()
        val cy = r.center.y; val hh = r.height
        listOf(-1f, 1f).forEach { s ->
            val x0 = r.center.x + s * r.width * 0.02f
            val largo = r.width * 0.47f
            tallo.moveTo(x0, cy + hh * 0.1f)
            val n = 4
            for (i in 0 until n) {
                val xa = x0 + s * largo * i / n; val xb = x0 + s * largo * (i + 1) / n
                val arriba = if (i % 2 == 0) -0.34f else 0.3f
                tallo.cubicTo(xa + s * (xb - xa) * 0.3f * s, cy + hh * arriba, xa + s * (xb - xa) * 0.7f * s, cy + hh * arriba, xb, cy + hh * 0.04f * (if (i % 2 == 0) 1f else -1f))
                val hx = (xa + xb) / 2
                val hy = cy + hh * arriba * 0.7f
                val dir = if (i % 2 == 0) -1f else 1f
                hojas.moveTo(hx, hy)
                hojas.quadraticTo(hx + s * hh * 0.25f, hy + dir * hh * 0.42f, hx + s * hh * 0.52f, hy + dir * hh * 0.28f)
                hojas.quadraticTo(hx + s * hh * 0.18f, hy + dir * hh * 0.08f, hx, hy)
                hojas.close()
                if (i == 1 || i == 3) {
                    val fc = Offset(xb - s * (xb - xa) * 0.1f, cy - dir * hh * 0.18f)
                    for (j in 0 until 5) {
                        val ang = (2 * Math.PI * j / 5 + i).toFloat()
                        val px = fc.x + hh * 0.15f * kotlin.math.cos(ang); val py = fc.y + hh * 0.15f * kotlin.math.sin(ang)
                        flores.addOval(Rect(px - hh * 0.12f, py - hh * 0.11f, px + hh * 0.12f, py + hh * 0.11f))
                    }
                    corazones.addOval(Rect(fc.x - hh * 0.07f, fc.y - hh * 0.07f, fc.x + hh * 0.07f, fc.y + hh * 0.07f))
                }
            }
        }
        // El filete de boj, arriba y abajo.
        drawRect(a.luz(P.SICOMORO, k).copy(alpha = 0.75f), Offset(r.left, r.top + hh * 0.06f), Size(r.width, 0.8f * u))
        drawRect(a.luz(P.SICOMORO, k).copy(alpha = 0.75f), Offset(r.left, r.bottom - hh * 0.08f), Size(r.width, 0.8f * u))
        drawPath(tallo, a.luz(P.TENIDA, k), style = Stroke(maxOf(1.2f * u, hh * 0.07f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(hojas, a.luz(P.TENIDA, k))
        drawPath(hojas, a.luz(P.CAOBA_HONDA, k).copy(alpha = 0.6f), style = Stroke(0.5f * u))
        drawPath(flores, a.luz(P.LIMONCILLO, k))
        drawPath(flores, a.luz(P.CAOBA_HONDA, k).copy(alpha = 0.55f), style = Stroke(0.5f * u))
        drawPath(corazones, a.luz(P.CAOBA_OSC, k))
    }

    // ---------------------------------------------------------------- el cuerpo (en cada dibujo)

    /** La baldosa del papel-cuero dorado, pintada una vez por modo y tamaño. */
    private var claveCuero = ""
    private var baldosa: ImageBitmap? = null

    /**
     * La baldosa del papel-cuero: el dorado con su estampado en relieve (un tallo que serpentea en diagonal con su hoja y una
     * flor de cinco pétalos), el hueco más oscuro y el canto de luz arriba a la izquierda.
     */
    private fun DrawScope.baldosaCuero(a: AireNouveau): ImageBitmap {
        val u = density
        val lado = ceil(34f * u).toInt().coerceAtLeast(8)
        val clave = "$lado:${a.oscuro}:${a.noche}"
        baldosa?.let { if (clave == claveCuero) return it }
        val img = ImageBitmap(lado, lado)
        CanvasDrawScope().draw(Density(density), layoutDirection, Canvas(img), Size(lado.toFloat(), lado.toFloat())) {
            val s = lado.toFloat()
            drawRect(a.dia(P.CUERO, 0f, 1f), Offset.Zero, Size(s, s))
            val motivo = Path().apply {
                moveTo(-s * 0.1f, s * 0.9f); cubicTo(s * 0.25f, s * 0.7f, s * 0.1f, s * 0.35f, s * 0.5f, s * 0.42f)
                cubicTo(s * 0.85f, s * 0.48f, s * 0.75f, s * 0.12f, s * 1.1f, -s * 0.1f)
            }
            val hoja = Path().apply {
                moveTo(s * 0.32f, s * 0.55f); quadraticTo(s * 0.2f, s * 0.78f, s * 0.04f, s * 0.8f); quadraticTo(s * 0.16f, s * 0.6f, s * 0.32f, s * 0.55f); close()
            }
            val flor = Path()
            for (j in 0 until 5) {
                val ang = (2 * Math.PI * j / 5 + 0.4).toFloat()
                val px = s * 0.74f + s * 0.09f * kotlin.math.cos(ang); val py = s * 0.74f + s * 0.09f * kotlin.math.sin(ang)
                flor.addOval(Rect(px - s * 0.07f, py - s * 0.065f, px + s * 0.07f, py + s * 0.065f))
            }
            // El hueco del estampado y el canto que mira a la luz.
            translate(0.8f * u, 0.8f * u) {
                drawPath(motivo, a.dia(P.CUERO_LUZ, 0f, 1f).copy(alpha = 0.55f), style = Stroke(1.6f * u))
                drawPath(hoja, a.dia(P.CUERO_LUZ, 0f, 1f).copy(alpha = 0.5f)); drawPath(flor, a.dia(P.CUERO_LUZ, 0f, 1f).copy(alpha = 0.5f))
            }
            drawPath(motivo, a.dia(P.CUERO_OSC, 0f, 1f), style = Stroke(1.6f * u))
            drawPath(hoja, a.dia(P.CUERO_OSC, 0f, 1f)); drawPath(flor, a.dia(P.CUERO_OSC, 0f, 1f))
            drawCircle(a.dia(P.CUERO_LUZ, 0f, 1f), s * 0.035f, Offset(s * 0.74f, s * 0.74f))
            // El granulado del cuero.
            for (j in 0 until 26) drawCircle((if (j % 2 == 0) P.CUERO_OSC else P.CUERO_LUZ).copy(alpha = 0.22f), 0.6f * u, Offset(s * azar(911, j), s * azar(912, j)))
        }
        baldosa = img; claveCuero = clave
        return img
    }

    /**
     * Un casillero con su hondura: el fondo de papel-cuero dorado, la tabla de encima vista por debajo, las dos caras de dentro (la
     * que mira a la luz, encendida), la sombra de la tabla de arriba y del costado en el fondo, y el roce del dorado. Se pinta un
     * píxel de más alrededor (bajo los montantes y las tablas, que van encima) para que ninguna junta deje ver el fondo.
     */
    fun DrawScope.casillero(k: Int, r: Rect, d: DisposicionEstante, a: AireNouveau) {
        val w = size.width; val u = density
        val x0 = d.izquierda.right; val x1 = d.derecha.left
        val dT = w * 0.017f; val dI = w * 0.024f
        val izq = luzIzq(a)
        drawRect(P.CAOBA_OSC, Offset(x0 - 1f, r.top - 1f), Size(x1 - x0 + 2f, r.height + 2f))
        // El papel-cuero, en baldosas.
        val img = baldosaCuero(a)
        val fondo = Rect(x0 + dI, r.top + dT, x1 - dI, r.bottom)
        clipRect(fondo.left, fondo.top, fondo.right, fondo.bottom) {
            var yy = fondo.top - (k * 11f * u) % img.height
            while (yy < fondo.bottom) {
                var xx = fondo.left - (k * 17f * u) % img.width
                while (xx < fondo.right) { drawImage(img, Offset(xx, yy)); xx += img.width }
                yy += img.height
            }
            // El roce del dorado y una mancha de humedad, distintos en cada casillero.
            val c = Offset(fondo.left + fondo.width * (0.2f + 0.6f * azar(913, k)), fondo.top + fondo.height * (0.35f + 0.4f * azar(914, k)))
            drawOval(a.dia(P.CUERO_OSC, 0f).copy(alpha = 0.22f), Offset(c.x - w * 0.06f, c.y - w * 0.03f), Size(w * 0.12f, w * 0.06f))
            drawOval(a.dia(P.CUERO_LUZ, 0.1f).copy(alpha = 0.18f), Offset(fondo.left + fondo.width * azar(915, k), fondo.bottom - w * 0.07f), Size(w * 0.09f, w * 0.05f))
        }
        // La sombra de la tabla de arriba, franca, y la del costado que tapa la luz.
        drawRect(a.sombra(0.5f), Offset(x0, r.top + dT), Size(x1 - x0, r.height * 0.075f))
        drawRect(a.sombra(0.26f), Offset(x0, r.top + dT + r.height * 0.075f), Size(x1 - x0, r.height * 0.12f))
        drawRect(a.sombra(0.11f), Offset(x0, r.top + dT + r.height * 0.195f), Size(x1 - x0, r.height * 0.14f))
        if (izq) drawRect(a.sombra(0.24f), Offset(x0 + dI, r.top + dT), Size(w * 0.026f, r.height - dT))
        else drawRect(a.sombra(0.24f), Offset(x1 - dI - w * 0.026f, r.top + dT), Size(w * 0.026f, r.height - dT))
        // La tabla de encima vista por debajo y las dos caras de dentro.
        drawPath(poligonoNouveau(x0, r.top, x1, r.top, x1 - dI, r.top + dT, x0 + dI, r.top + dT), a.luz(P.CAOBA_HONDA, 1f))
        val caraIzq = poligonoNouveau(x0, r.top, x0 + dI, r.top + dT, x0 + dI, r.bottom, x0, r.bottom)
        val caraDer = poligonoNouveau(x1, r.top, x1, r.bottom, x1 - dI, r.bottom, x1 - dI, r.top + dT)
        drawPath(caraIzq, if (izq) a.dia(P.CAOBA_OSC, -0.1f) else a.dia(P.CAOBA, 0.12f))
        drawPath(caraDer, if (izq) a.dia(P.CAOBA, 0.14f) else a.dia(P.CAOBA_OSC, -0.1f))
        // El canto de la esquina de dentro.
        drawLine(a.sombra(0.45f), Offset(x0 + dI, r.top + dT), Offset(x0 + dI, r.bottom), 1f * u)
        drawLine(a.sombra(0.3f), Offset(x1 - dI, r.top + dT), Offset(x1 - dI, r.bottom), 1f * u)
        drawLine(a.sombra(0.55f), Offset(x0 + dI, r.top + dT), Offset(x1 - dI, r.top + dT), 1f * u)
    }

    /** El faldón festoneado bajo la tabla de arriba de un casillero, armado una vez por tamaño (baja solo en las esquinas). */
    private var claveFaldon = ""
    private var faldon = Path()
    private var luzFaldon = Path()

    private fun caminoFaldon(ancho: Float, u: Float) {
        val clave = "$ancho:$u"
        if (clave == claveFaldon) return
        val baja = 6.6f * u
        val p = Path()
        p.moveTo(0f, 0f); p.lineTo(ancho, 0f)
        p.lineTo(ancho, baja)
        p.cubicTo(ancho - ancho * 0.05f, baja, ancho - ancho * 0.07f, baja * 0.35f, ancho - ancho * 0.14f, baja * 0.3f)
        p.cubicTo(ancho - ancho * 0.3f, baja * 0.24f, ancho * 0.62f, baja * 0.08f, ancho * 0.5f, baja * 0.12f)
        p.cubicTo(ancho * 0.38f, baja * 0.08f, ancho * 0.3f, baja * 0.24f, ancho * 0.14f, baja * 0.3f)
        p.cubicTo(ancho * 0.07f, baja * 0.35f, ancho * 0.05f, baja, 0f, baja)
        p.close()
        val l = Path()
        l.moveTo(ancho, baja - 0.6f * u)
        l.cubicTo(ancho - ancho * 0.05f, baja - 0.6f * u, ancho - ancho * 0.07f, baja * 0.35f, ancho - ancho * 0.14f, baja * 0.3f)
        l.cubicTo(ancho - ancho * 0.3f, baja * 0.24f, ancho * 0.62f, baja * 0.08f, ancho * 0.5f, baja * 0.12f)
        l.cubicTo(ancho * 0.38f, baja * 0.08f, ancho * 0.3f, baja * 0.24f, ancho * 0.14f, baja * 0.3f)
        l.cubicTo(ancho * 0.07f, baja * 0.35f, ancho * 0.05f, baja - 0.6f * u, 0f, baja - 0.6f * u)
        faldon = p; luzFaldon = l; claveFaldon = clave
    }

    /** El faldón festoneado de un casillero (el plano del frente), con su canto de luz, su sombra y una hoja tallada en cada esquina. */
    fun DrawScope.enjutas(r: Rect, d: DisposicionEstante, a: AireNouveau) {
        val u = density
        val x0 = d.izquierda.right; val x1 = d.derecha.left
        caminoFaldon(x1 - x0, u)
        translate(x0, r.top) {
            translate(0f, 1.6f * u) { drawPath(faldon, a.sombra(0.35f)) }
            drawPath(faldon, a.luz(P.CAOBA, 1f))
            drawPath(luzFaldon, a.dia(P.CAOBA_LUZ, 0.15f), style = Stroke(1.1f * u))
        }
        // Las hojas talladas de las esquinas.
        listOf(x0 + 4.5f * u to 1f, x1 - 4.5f * u to -1f).forEach { (x, s) ->
            val y = r.top + 3.2f * u
            val hoja = Path().apply {
                moveTo(x - s * 2.6f * u, y - 1.2f * u)
                quadraticTo(x + s * 2f * u, y - 2.4f * u, x + s * 6f * u, y + 1.4f * u)
                quadraticTo(x + s * 1f * u, y + 1.8f * u, x - s * 2.6f * u, y - 1.2f * u)
                close()
            }
            translate(0.6f * u, 0.8f * u) { drawPath(hoja, a.sombra(0.4f)) }
            drawPath(hoja, a.dia(P.CAOBA_LUZ, 0f))
        }
    }

    /**
     * Una tabla con su canto: el bocel de arriba (luz arriba, sombra abajo), la cara de caoba con su veta y su filete de
     * limoncillo, el canto de abajo y el roce de los años donde se sacan los libros.
     */
    fun DrawScope.tabla(k: Int, r: Rect, a: AireNouveau) {
        val u = density
        val h = r.height
        // Debajo, la fila de la junta con el casillero de abajo, de píxel entero y del color del canto.
        drawRect(a.luz(P.CAOBA_HONDA, 1f), Offset(r.left, floor(r.bottom) - 1f), Size(r.width, 2f))
        drawRect(a.luz(P.CAOBA, 1f), r.topLeft, r.size)
        drawRect(a.dia(P.CAOBA_LUZ, 0.24f), r.topLeft, Size(r.width, h * 0.08f))
        drawRect(a.dia(P.CAOBA_LUZ, 0.04f), Offset(r.left, r.top + h * 0.08f), Size(r.width, h * 0.13f))
        drawRect(a.luz(P.CAOBA_OSC, 1f), Offset(r.left, r.top + h * 0.21f), Size(r.width, h * 0.1f))
        // El filete de limoncillo de la cara.
        drawRect(a.luz(P.LIMONCILLO, 1f).copy(alpha = 0.7f), Offset(r.left, r.top + h * 0.42f), Size(r.width, 0.9f * u))
        drawRect(a.luz(P.CAOBA_HONDA, 1f).copy(alpha = 0.5f), Offset(r.left, r.top + h * 0.42f + 0.9f * u), Size(r.width, 0.5f * u))
        // La veta de la cara, de la tabla entera.
        val veta = Path()
        for (j in 0 until 4) {
            val y = r.top + h * (0.5f + 0.1f * j)
            veta.moveTo(r.left, y); veta.cubicTo(r.left + r.width * 0.35f, y + 1.5f * u * (azar(921 + k, j) - 0.5f), r.left + r.width * 0.65f, y - 1.1f * u, r.right, y + 0.8f * u)
        }
        drawPath(veta, a.sombra(0.18f), style = Stroke(0.7f * u))
        drawRect(a.luz(P.CAOBA_HONDA, 1f), Offset(r.left, r.bottom - h * 0.09f), Size(r.width, h * 0.09f + 0.5f))
        drawRect(a.dia(P.CAOBA_LUZ, 0f).copy(alpha = 0.55f), Offset(r.left, r.bottom - h * 0.11f), Size(r.width, 0.8f * u))
        // El roce del canto: claros cortos donde se agarran los libros, y un golpe.
        val roce = Path()
        for (j in 0 until 6) {
            val x = r.left + r.width * azar(931 + k, j)
            roce.moveTo(x, r.top + h * 0.05f); roce.lineTo(x + r.width * (0.02f + 0.04f * azar(941 + k, j)), r.top + h * 0.05f)
        }
        drawPath(roce, a.claro(0.35f), style = Stroke(1f * u, cap = StrokeCap.Round))
        val g = r.left + r.width * (0.15f + 0.7f * azar(951, k))
        drawOval(a.luz(P.CAOBA_HONDA, 1f).copy(alpha = 0.6f), Offset(g, r.top + h * 0.6f), Size(3.4f * u, 1.6f * u))
    }

    /** Los caminos fijos de un montante (el tallo tallado y sus hojas), armados una vez por tamaño. */
    private var claveMontante = ""
    private var talloM = Path()
    private var hojasM = Path()

    private fun caminosMontante(ancho: Float, alto: Float, u: Float) {
        val clave = "$ancho:$alto:$u"
        if (clave == claveMontante) return
        val t = Path(); val hj = Path()
        val cx = ancho * 0.5f
        val paso = maxOf(8f * u, ancho * 1.6f)
        var y = alto
        t.moveTo(cx, alto)
        var lado = 1f
        while (y > paso * 0.8f) {
            val y1 = y - paso
            t.cubicTo(cx + lado * ancho * 0.1f, y - paso * 0.3f, cx + lado * ancho * 0.1f, y - paso * 0.7f, cx, y1)
            val hy = y - paso * 0.5f
            val hx = cx + lado * ancho * 0.08f
            hj.moveTo(hx, hy)
            hj.quadraticTo(hx + lado * ancho * 0.12f, hy - paso * 0.36f, hx + lado * ancho * 0.24f, hy - paso * 0.42f)
            hj.quadraticTo(hx + lado * ancho * 0.04f, hy - paso * 0.12f, hx, hy)
            hj.close()
            y = y1; lado = -lado
        }
        talloM = t; hojasM = hj; claveMontante = clave
    }

    /**
     * Un montante: la cara de caoba con su canal hundido y el tallo tallado que baja por él echando hojas, el chaflán del canto de
     * dentro, la ménsula de hoja bajo la cornisa y el roce de la caoba a la altura de la mano.
     */
    fun DrawScope.montante(r: Rect, izquierdo: Boolean, a: AireNouveau) {
        val u = density; val w = size.width
        if (r.height < 4f || r.width < 2f) return
        val izq = luzIzq(a)
        drawRect(a.luz(P.CAOBA, 1f), r.topLeft, r.size)
        // La veta a lo alto.
        val veta = Path()
        for (j in 0 until 3) {
            val x = r.left + r.width * (0.12f + 0.3f * j)
            veta.moveTo(x, r.top); veta.cubicTo(x + 1.2f * u, r.top + r.height * 0.3f, x - 1.2f * u, r.top + r.height * 0.7f, x + 0.6f * u, r.bottom)
        }
        drawPath(veta, a.sombra(0.15f), style = Stroke(0.7f * u))
        // El canal hundido, con su sombra del lado de la luz y su canto encendido del otro.
        val c = Rect(r.left + r.width * 0.26f, r.top + w * 0.07f, r.right - r.width * 0.26f, r.bottom - w * 0.02f)
        if (c.height > 6f * u) {
            drawRect(a.luz(P.CAOBA_OSC, 1f), c.topLeft, c.size)
            drawRect(a.sombra(0.42f), if (izq) c.topLeft else Offset(c.right - 1.8f * u, c.top), Size(1.8f * u, c.height))
            drawRect(a.dia(P.CAOBA_LUZ, 0.06f).copy(alpha = 0.8f), if (izq) Offset(c.right - 1.2f * u, c.top) else c.topLeft, Size(1.2f * u, c.height))
            caminosMontante(c.width, c.height, u)
            translate(c.left, c.top) {
                clipRect(0f, 0f, c.width, c.height) {
                    translate(0.7f * u, 0.9f * u) { drawPath(talloM, a.sombra(0.45f), style = Stroke(1.8f * u, cap = StrokeCap.Round)); drawPath(hojasM, a.sombra(0.45f)) }
                    drawPath(talloM, a.dia(P.CAOBA_LUZ, -0.05f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
                    drawPath(hojasM, a.dia(P.CAOBA_LUZ, -0.05f))
                    drawPath(hojasM, a.sombra(0.3f), style = Stroke(0.5f * u))
                }
            }
        }
        // El chaflán del canto de dentro y el canto de fuera.
        val chx = if (izquierdo) r.right - r.width * 0.14f else r.left
        drawRect(if ((izquierdo && !izq) || (!izquierdo && izq)) a.dia(P.CAOBA_LUZ, 0.12f) else a.luz(P.CAOBA_OSC, 1f), Offset(chx, r.top), Size(r.width * 0.14f, r.height))
        drawRect(a.dia(P.CAOBA_LUZ, 0.18f).copy(alpha = 0.8f), Offset(if (izq) r.left else r.right - 1.6f * u, r.top), Size(1.6f * u, r.height))
        // La ménsula de hoja bajo la cornisa.
        val m = Rect(r.left, r.top, r.right, r.top + w * 0.06f)
        val mensula = Path().apply {
            moveTo(m.left, m.top); lineTo(m.right, m.top)
            cubicTo(m.right, m.top + m.height * 0.5f, m.center.x + m.width * 0.25f, m.bottom - m.height * 0.1f, m.center.x, m.bottom)
            cubicTo(m.center.x - m.width * 0.25f, m.bottom - m.height * 0.1f, m.left, m.top + m.height * 0.5f, m.left, m.top)
            close()
        }
        translate(0.8f * u, 1.4f * u) { drawPath(mensula, a.sombra(0.4f)) }
        drawPath(mensula, a.luz(P.CAOBA, 1f))
        drawPath(mensula, a.dia(P.CAOBA_LUZ, 0.1f), style = Stroke(1f * u))
        drawLine(a.luz(P.CAOBA_HONDA, 1f), Offset(m.center.x, m.top + m.height * 0.15f), Offset(m.center.x, m.bottom - m.height * 0.12f), 1f * u)
        // El roce a la altura de la mano: la caoba gastada a lo claro en el canto.
        val yr = r.top + r.height * (0.35f + 0.2f * (if (izquierdo) 0.2f else 0.7f))
        val xr = if (izquierdo) r.right - r.width * 0.16f else r.left + r.width * 0.02f
        drawLine(a.claro(0.3f), Offset(xr, yr), Offset(xr, yr + w * 0.09f), 1.1f * u, StrokeCap.Round)
        drawLine(a.claro(0.18f), Offset(xr + r.width * 0.06f, yr + w * 0.02f), Offset(xr + r.width * 0.06f, yr + w * 0.06f), 0.8f * u, StrokeCap.Round)
    }

    /**
     * El rótulo del mes: la cartela de marquetería de limoncillo embutida en el canto de la tabla, con sus puntas enroscadas en
     * látigo de madera teñida, el filete de ébano y el mes en tinta.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireNouveau, k: Float, maximo: Float) {
        val u = density
        // La cartela crece con el texto (el mes con el común es largo), hasta [maximo].
        val tw = medidor.measure(p.texto, TextStyle(fontFamily = P.LETRA, fontSize = 10.sp), maxLines = 1, softWrap = false).size.width.toFloat()
        val ancho = (tw + p.r.height + 10f * u).coerceIn(p.r.width, maxOf(p.r.width, maximo))
        val r = Rect(p.r.center.x - ancho / 2, p.r.top, p.r.center.x + ancho / 2, p.r.bottom)
        val kk = maxOf(k, 0.4f)
        val cart = Path().apply {
            val e = r.height * 0.5f
            moveTo(r.left + e, r.top)
            lineTo(r.right - e, r.top)
            quadraticTo(r.right - e * 0.2f, r.top + e * 0.1f, r.right - e * 0.25f, r.center.y)
            quadraticTo(r.right - e * 0.2f, r.bottom - e * 0.1f, r.right - e, r.bottom)
            lineTo(r.left + e, r.bottom)
            quadraticTo(r.left + e * 0.2f, r.bottom - e * 0.1f, r.left + e * 0.25f, r.center.y)
            quadraticTo(r.left + e * 0.2f, r.top + e * 0.1f, r.left + e, r.top)
            close()
        }
        drawPath(cart, a.luz(P.CAOBA_HONDA, kk), style = Stroke(1.6f * u))
        drawPath(cart, a.etiqueta(P.LIMONCILLO, kk))
        drawPath(cart, a.luz(P.CAOBA_HONDA, kk).copy(alpha = 0.7f), style = Stroke(0.6f * u))
        drawRect(a.claro(0.35f), Offset(r.left + r.height * 0.5f, r.top + 1f * u), Size(r.width - r.height, 0.7f * u))
        // Las puntas en látigo, de madera teñida, a los lados.
        listOf(-1f, 1f).forEach { s ->
            val x = if (s < 0) r.left - 1f * u else r.right + 1f * u
            val rizo = Path().apply {
                moveTo(x, r.center.y)
                cubicTo(x + s * 5f * u, r.center.y - 4f * u, x + s * 10f * u, r.center.y + 3f * u, x + s * 6f * u, r.center.y + 3.4f * u)
                quadraticTo(x + s * 3.6f * u, r.center.y + 3f * u, x + s * 5f * u, r.center.y + 0.6f * u)
            }
            drawPath(rizo, a.luz(P.TENIDA, kk), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        }
        textoEn(medidor, p.texto, Rect(r.left + r.height * 0.5f, r.top, r.right - r.height * 0.5f, r.bottom), P.LETRA,
            a.luz(P.CAOBA_HONDA, maxOf(k, 0.8f)), 10f, peso = FontWeight.Normal, minimo = 6f)
    }

    // ---------------------------------------------------------------- la luz de la noche sobre el cuerpo

    private var claveMapa = ""
    private var mapa: ImageBitmap? = null

    /**
     * La luz sobre el cuerpo, en una imagen chica (un píxel por cada 1/36 del ancho) pintada una vez por tamaño y modo y estirada
     * en un solo dibujo encima de todo el cuerpo: de noche, lo que se apaga con la distancia al aplique, a los quinqués y a la
     * palmatoria (hacia el vino oscuro; arriba a la izquierda, hacia el verde hondo del café), con el oro de la lámpara cerca; de
     * día, lo que oscurece la sombra hacia abajo y hacia la derecha; al ocaso, el violeta que crece lejos del escaparate.
     */
    fun DrawScope.luzCuerpo(arriba: Float, abajo: Float, a: AireNouveau, yVela: Float, quinques: FloatArray) {
        val w = size.width
        val y0 = ceil(arriba).toInt(); val y1 = floor(abajo).toInt()
        if (y1 <= y0 || w < 2f) return
        val cols = 36
        val filas = ((y1 - y0) / (w / cols)).toInt().coerceIn(2, 400)
        val clave = "${w.toInt()}:$y0:$y1:${a.oscuro}:${a.noche}:$yVela:${quinques.joinToString(",")}"
        val img = mapa.takeIf { clave == claveMapa } ?: ImageBitmap(cols, filas).also { nueva ->
            CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(nueva), Size(cols.toFloat(), filas.toFloat())) {
                for (j in 0 until filas) for (i in 0 until cols) {
                    val x = (i + 0.5f) / cols
                    val y = (y0 + (j + 0.5f) / filas * (y1 - y0)) / w
                    drawRect(mascara(a, x, y, yVela, quinques), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f))
                }
            }
            mapa = nueva; claveMapa = clave
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, y0), IntSize(ceil(w).toInt(), y1 - y0))
    }

    /** El color (con su alfa) que se pone encima del cuerpo en ([x], [y]), en anchos (también el pie, con la misma luz). */
    fun mascara(a: AireNouveau, x: Float, y: Float, yVela: Float, quinques: FloatArray): Color {
        if (!a.lumbre) {
            // De día: algo más hondo abajo y a la derecha, lejos del escaparate.
            val f = ((y - 0.7f) * 0.07f + (x - 0.2f).coerceAtLeast(0f) * 0.1f).coerceIn(0f, 0.16f)
            return P.FRIO.copy(alpha = f)
        }
        val k = MuebleEstanteNouveau.calor(x, y, yVela, a.noche, quinques)
        if (!a.oscuro) {
            // El ocaso de los Sueños: la luz rasante viene del escaparate (arriba a la izquierda); lejos de él, la sombra violeta
            // crece hacia abajo y hacia la derecha; las lámparas calientan lo que tienen cerca.
            val violeta = (0.03f + 0.11f * ((x - 0.1f) * 0.9f + (y - 0.7f) * 0.35f).coerceIn(0f, 1f)) * (1f - k)
            val calido = 0.2f * k
            val al = (violeta + calido).coerceIn(0f, 1f)
            return mezcla(P.SOMBRA_OCASO, P.LLAMA, if (al > 0.001f) calido / al else 0f).copy(alpha = al)
        }
        val fr = MuebleEstanteNouveau.fria(x, y, a.noche)
        val hondo = mezcla(a.hondo, if (a.noche) Color(0xFF1A2A4A) else Color(0xFF123A30), (fr * 1.3f).coerceAtMost(0.8f))
        val oscuridad = (1f - k) * 0.9f
        val calido = 0.16f * k
        val al = (oscuridad + calido).coerceIn(0f, 1f)
        val f = if (al > 0.001f) calido / al else 0f
        return mezcla(hondo, P.LAMPARA, f).copy(alpha = al)
    }
}
