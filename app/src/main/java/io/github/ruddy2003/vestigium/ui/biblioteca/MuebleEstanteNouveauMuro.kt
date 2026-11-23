package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
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
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil

/**
 * La pared del estante de Art nouveau ([MuebleEstanteNouveau]): el papel pintado verde gris, en tiras con su junta, con un
 * tallo inventado que sube en látigo y abre una flor de cinco pétalos, impreso en verde hondo y oro pálido; arriba, la cenefa
 * de ocre con su rama corrida y la moldura de colgar cuadros. El escaparate de la tienda queda fuera del cuadro, a la
 * izquierda: de día deja en la pared y en la cornisa su paño de sol con el dibujo de sus montantes curvos (abajo tres
 * cristales; arriba, dos que se curvan en un capullo); de noche, el mismo dibujo con el verde del café de enfrente; en los
 * Sueños, el ocaso (en claro) o la luna (en oscuro).
 */
internal object MuroEstanteNouveau {
    private val P = PaletaNouveau

    /** La cenefa y la moldura de colgar cuadros, en anchos desde arriba. */
    const val CENEFA = 0.058f
    const val MOLDURA = 0.074f

    // ---------------------------------------------------------------- el paño del escaparate

    /** El paño de luz del escaparate en la pared, en anchos: (arriba izquierda, arriba derecha, abajo derecha, abajo izquierda). */
    fun pano(a: AireNouveau): FloatArray = when {
        a.oscuro && a.noche -> floatArrayOf(-0.03f, 0.12f, 0.33f, 0.17f, 0.39f, 0.58f, -0.03f, 0.55f)
        a.oscuro -> floatArrayOf(-0.05f, 0.07f, 0.24f, 0.1f, 0.29f, 0.46f, -0.05f, 0.44f)
        a.noche -> floatArrayOf(-0.02f, 0.2f, 0.58f, 0.25f, 0.66f, 0.53f, -0.02f, 0.5f)
        else -> floatArrayOf(-0.02f, 0.1f, 0.31f, 0.16f, 0.37f, 0.57f, -0.02f, 0.53f)
    }

    /**
     * El paño que entra en el cuerpo del mueble en los Sueños (el sol bajo del ocaso y la luna llegan más hondo que el sol de
     * mediodía): en el fondo y en el frente de las repisas de arriba a la izquierda, en anchos; o ninguno.
     */
    fun panoCuerpo(a: AireNouveau): FloatArray? = when {
        a.oscuro && a.noche -> floatArrayOf(-0.03f, 0.6f, 0.34f, 0.64f, 0.5f, 1.08f, -0.03f, 1.03f)
        !a.oscuro && a.noche -> floatArrayOf(-0.03f, 0.62f, 0.5f, 0.66f, 0.62f, 0.98f, -0.03f, 0.95f)
        else -> null
    }

    /** El color de la luz del escaparate y su fuerza: el sol, el café, el ocaso o la luna. */
    fun colorPano(a: AireNouveau): Pair<Color, Float> = when {
        a.oscuro && a.noche -> Color(0xFFB8CAEA) to 0.62f
        a.oscuro -> P.CAFE to 0.32f
        a.noche -> Color(0xFFF59A48) to 0.95f
        else -> P.SOL to 0.5f
    }

    /** Un punto del paño: [fx] a lo ancho, [fy] a lo alto, de 0 a 1, en el plano corrido ([dx], [dy]) anchos. */
    private fun enPano(q: FloatArray, w: Float, fx: Float, fy: Float, dx: Float, dy: Float): Offset {
        val tx = q[0] + (q[2] - q[0]) * fx; val ty = q[1] + (q[3] - q[1]) * fx
        val bx = q[6] + (q[4] - q[6]) * fx; val by = q[7] + (q[5] - q[7]) * fx
        return Offset((tx + (bx - tx) * fy + dx) * w, (ty + (by - ty) * fy + dy) * w)
    }

    /** Un punto del barrote en látigo del montante de arriba del escaparate, de la izquierda ([t] = 0) a la derecha ([t] = 1). */
    private fun ola(t: Float): Pair<Float, Float> {
        val m = 1f - t
        val x = m * m * m * 0.03f + 3 * m * m * t * 0.3f + 3 * m * t * t * 0.62f + t * t * t * 0.97f
        val y = m * m * m * 0.25f + 3 * m * m * t * -0.02f + 3 * m * t * t * 0.36f + t * t * t * 0.1f
        return x to y
    }

    /** Los cristales ya armados, en cuatro ranuras (la pared, la cornisa y el cuadro; el cuerpo; las entradas). */
    private val clavesPanos = arrayOf("", "", "", "")
    private val caminosPanos = arrayOf(Path(), Path(), Path(), Path())

    /**
     * Los cristales del escaparate proyectados en el paño [q], en el plano corrido ([dx], [dy]): abajo, dos grandes partidos por
     * un montante corrido de su medio; arriba, el montante de la puerta partido por un barrote que ondula en látigo (los barrotes
     * quedan en sombra). Se arma una vez por paño y plano, en su [ranura].
     */
    fun cristales(q: FloatArray, w: Float, dx: Float = 0f, dy: Float = 0f, ranura: Int = 0): Path {
        val clave = "${q.joinToString(",")}:$w:$dx:$dy"
        val rr = ranura.coerceIn(0, 3)
        if (clave == clavesPanos[rr]) return caminosPanos[rr]
        val g = 0.024f
        val c = Path()
        fun poli(pts: List<Pair<Float, Float>>) {
            pts.forEachIndexed { i, (fx, fy) -> val p = enPano(q, w, fx, fy, dx, dy); if (i == 0) c.moveTo(p.x, p.y) else c.lineTo(p.x, p.y) }
            c.close()
        }
        fun lado(x0: Float, y0: Float, x1: Float, y1: Float, n: Int = 4): List<Pair<Float, Float>> = List(n + 1) { i -> (x0 + (x1 - x0) * i / n) to (y0 + (y1 - y0) * i / n) }
        // Abajo: dos cristales grandes, el montante corrido a la derecha.
        listOf(0.03f to 0.56f - g, 0.56f + g to 0.97f).forEach { (x0, x1) ->
            poli(lado(x0, 0.32f + g, x1, 0.32f + g) + lado(x1, 0.32f + g, x1, 0.97f) + lado(x1, 0.97f, x0, 0.97f) + lado(x0, 0.97f, x0, 0.32f + g))
        }
        // Arriba: el cristal de encima de la ola y el de debajo.
        val n = 16
        val arriba = List(n + 1) { i -> val (x, y) = ola(1f - i / n.toFloat()); x to (y - g) }
        poli(lado(0.03f, 0.03f, 0.97f, 0.03f) + arriba + listOf(0.03f to 0.03f))
        val abajo = List(n + 1) { i -> val (x, y) = ola(i / n.toFloat()); x to minOf(y + g, 0.32f - g) }
        poli(abajo + lado(0.97f, 0.32f - g, 0.03f, 0.32f - g))
        caminosPanos[rr] = c; clavesPanos[rr] = clave
        return c
    }

    /** Si ([x], [y]) (en anchos) cae dentro del paño (sin contar los montantes): 1; si no, 0. */
    fun dentro(q: FloatArray, x: Float, y: Float): Float {
        fun cruz(ax: Float, ay: Float, bx: Float, by: Float) = (bx - ax) * (y - ay) - (by - ay) * (x - ax)
        val c1 = cruz(q[0], q[1], q[2], q[3]); val c2 = cruz(q[2], q[3], q[4], q[5])
        val c3 = cruz(q[4], q[5], q[6], q[7]); val c4 = cruz(q[6], q[7], q[0], q[1])
        return if ((c1 >= 0 && c2 >= 0 && c3 >= 0 && c4 >= 0) || (c1 <= 0 && c2 <= 0 && c3 <= 0 && c4 <= 0)) 1f else 0f
    }

    /** El paño del escaparate sobre lo que hay dentro de [donde], en el plano corrido ([dx], [dy]): borde decidido, sin velar. */
    fun DrawScope.panoEn(a: AireNouveau, donde: Path?, dx: Float, dy: Float, fuerza: Float = 1f) =
        panoSobre(a, pano(a), size.width, donde, dx, dy, fuerza, 0)

    /**
     * El paño [q] (en anchos de un estante de [w] píxeles) sobre lo que hay dentro de [donde], en el plano corrido ([dx], [dy]):
     * mezcla de pantalla (aclara y tiñe sin tapar), más fuerte junto al escaparate.
     */
    fun DrawScope.panoSobre(a: AireNouveau, q: FloatArray, w: Float, donde: Path?, dx: Float, dy: Float, fuerza: Float, ranura: Int) {
        val (col, f) = colorPano(a)
        val pincel = Brush.linearGradient(listOf(col.copy(alpha = (f * fuerza).coerceAtMost(1f)), col.copy(alpha = f * fuerza * 0.45f)),
            Offset((q[0] + dx) * w, (q[1] + dy) * w), Offset((q[4] + dx) * w, (q[5] + dy) * w))
        val c = cristales(q, w, dx, dy, ranura)
        if (donde != null) clipPath(donde) { drawPath(c, pincel, blendMode = BlendMode.Screen) }
        else drawPath(c, pincel, blendMode = BlendMode.Screen)
    }

    /**
     * El paño de los Sueños en el cuerpo, de [ch] a [abajo]: en el fondo de cada casillero (el plano de atrás) y en las tablas y
     * los montantes (el plano del frente, corrido hacia el escaparate). Lo guardado lo recibe en su frente ([VolumenEstanteNouveau]).
     */
    fun DrawScope.panoEnElCuerpo(a: AireNouveau, d: DisposicionEstante, ch: Float, abajo: Float) {
        val q = panoCuerpo(a) ?: return
        val w = size.width
        val dT = w * 0.017f; val dI = w * 0.024f
        val fondo = Path()
        d.repisas.forEach { r -> fondo.addRect(Rect(d.izquierda.right + dI, r.top + dT, d.derecha.left - dI, r.bottom)) }
        val ocaso = !a.oscuro
        panoSobre(a, q, w, fondo, 0f, 0f, if (ocaso) 0.62f else 0.8f, 1)
        val frente = Path()
        d.tablas.forEach { r -> frente.addRect(r) }
        if (!ocaso) {
            frente.addRect(Rect(d.izquierda.left, ch, d.izquierda.right, abajo))
            frente.addRect(Rect(d.derecha.left, ch, d.derecha.right, abajo))
        }
        panoSobre(a, q, w, frente, -0.03f, -0.02f, if (ocaso) 0.6f else 0.9f, 2)
    }

    // ---------------------------------------------------------------- la pared entera (arriba)

    /** El papel pintado, la cenefa y la moldura; de noche, la luz del aplique en la pared; y el paño del escaparate. */
    fun DrawScope.pared(a: AireNouveau, apoyo: Float, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val ab = apoyo + w * 0.02f
        val ys = w * MOLDURA
        // El papel, con la luz entera (la noche lo apaga después con su mapa).
        drawRect(a.luz(P.PARED, 1f), Offset(0f, ys), Size(w, ab - ys))
        papel(a, Rect(0f, ys, w, ab))
        // La cenefa de ocre con su rama corrida, y la moldura de colgar cuadros.
        cenefa(a, Rect(0f, 0f, w, w * CENEFA))
        val m = Rect(0f, w * CENEFA, w, ys)
        drawRect(a.luz(P.CAOBA, 1f), m.topLeft, m.size)
        drawRect(a.dia(P.CAOBA_LUZ, 0.2f), m.topLeft, Size(w, m.height * 0.3f))
        drawRect(a.luz(P.CAOBA_HONDA, 1f), Offset(0f, m.bottom - m.height * 0.22f), Size(w, m.height * 0.22f))
        drawRect(a.sombra(0.32f), Offset(0f, ys), Size(w, 2.2f * u))
        drawRect(a.sombra(0.12f), Offset(0f, ys + 2.2f * u), Size(w, 5f * u))
        // La luz de la noche en la pared: el mapa de lo que se apaga lejos del aplique.
        if (a.lumbre) mapaPared(a, 0f, ab)
        // El paño del escaparate: el sol, el café, el ocaso o la luna, en la pared (y, encima, la cenefa).
        panoEn(a, Path().apply { addRect(Rect(0f, 0f, w, ab)) }, 0f, 0f)
    }

    /**
     * El papel pintado dentro de [r], en tiras de 0.27 anchos con su junta: en cada tira, un tallo que sube en látigo, echa dos
     * hojas y abre una flor de cinco pétalos, en verde hondo con los cantos de oro pálido; las tiras se empalman corridas media
     * flor. Un poco gastado: más claro donde le dio el sol y una mancha de humedad.
     */
    private fun DrawScope.papel(a: AireNouveau, r: Rect) {
        val w = size.width; val u = density
        val paso = w * 0.27f
        val alto = w * 0.2f
        val motivo = a.luz(P.MOTIVO, 1f).copy(alpha = 0.6f)
        val oro = a.luz(P.ORO, 1f).copy(alpha = if (a.oscuro) 0.42f else 0.5f)
        val tallos = Path(); val hojas = Path(); val flores = Path(); val corazones = Path(); val filos = Path()
        clipRect(r.left, r.top, r.right, r.bottom) {
            var col = 0
            var x = -paso * 0.3f
            while (x < r.right + paso) {
                var y = r.top - alto * (if (col % 2 == 0) 0.2f else 0.7f)
                while (y < r.bottom + alto) {
                    val cx = x + paso * 0.5f
                    tallos.moveTo(cx - paso * 0.1f, y + alto)
                    tallos.cubicTo(cx + paso * 0.3f, y + alto * 0.7f, cx - paso * 0.32f, y + alto * 0.35f, cx + paso * 0.04f, y + alto * 0.1f)
                    // Las dos hojas.
                    hojas.moveTo(cx - paso * 0.02f, y + alto * 0.62f)
                    hojas.quadraticTo(cx - paso * 0.2f, y + alto * 0.5f, cx - paso * 0.3f, y + alto * 0.62f)
                    hojas.quadraticTo(cx - paso * 0.16f, y + alto * 0.68f, cx - paso * 0.02f, y + alto * 0.62f); hojas.close()
                    hojas.moveTo(cx + paso * 0.02f, y + alto * 0.42f)
                    hojas.quadraticTo(cx + paso * 0.22f, y + alto * 0.3f, cx + paso * 0.3f, y + alto * 0.4f)
                    hojas.quadraticTo(cx + paso * 0.18f, y + alto * 0.48f, cx + paso * 0.02f, y + alto * 0.42f); hojas.close()
                    // La flor, de cinco pétalos, ladeada.
                    val fc = Offset(cx + paso * 0.05f, y + alto * 0.05f)
                    for (j in 0 until 5) {
                        val ang = (2 * Math.PI * j / 5 + 0.5).toFloat()
                        val px = fc.x + paso * 0.07f * kotlin.math.cos(ang); val py = fc.y + paso * 0.06f * kotlin.math.sin(ang)
                        flores.addOval(Rect(px - paso * 0.055f, py - paso * 0.048f, px + paso * 0.055f, py + paso * 0.048f))
                    }
                    corazones.addOval(Rect(fc.x - paso * 0.03f, fc.y - paso * 0.027f, fc.x + paso * 0.03f, fc.y + paso * 0.027f))
                    filos.moveTo(cx - paso * 0.08f, y + alto * 0.9f)
                    filos.cubicTo(cx + paso * 0.2f, y + alto * 0.7f, cx - paso * 0.24f, y + alto * 0.38f, cx + paso * 0.0f, y + alto * 0.16f)
                    y += alto
                }
                x += paso; col++
            }
            drawPath(tallos, motivo, style = Stroke(2f * u, cap = StrokeCap.Round))
            drawPath(filos, oro, style = Stroke(0.7f * u, cap = StrokeCap.Round))
            drawPath(hojas, motivo)
            drawPath(flores, motivo)
            drawPath(flores, oro, style = Stroke(0.6f * u))
            drawPath(corazones, oro)
            // Las juntas de las tiras: una línea fina, algo despegada a trechos.
            var xj = paso * 0.98f
            while (xj < r.right) {
                drawLine(a.sombra(0.22f), Offset(xj, r.top), Offset(xj, r.bottom), 0.8f * u)
                drawLine(a.claro(0.14f), Offset(xj + 0.9f * u, r.top), Offset(xj + 0.9f * u, r.bottom), 0.6f * u)
                xj += paso
            }
            // El papel algo comido por el sol del escaparate y una mancha de humedad bajo la moldura, a la derecha.
            if (!a.lumbre) drawRect(Brush.horizontalGradient(listOf(luzOpaca(0.12f), Color.Transparent), r.left, r.left + w * 0.45f), r.topLeft, Size(w * 0.45f, r.height))
            drawOval(a.luz(P.PARED_OSC, 1f).copy(alpha = 0.22f), Offset(w * 0.64f, r.top + w * 0.01f), Size(w * 0.16f, w * 0.05f))
            drawOval(a.luz(P.PARED_LUZ, 1f).copy(alpha = 0.12f), Offset(w * 0.67f, r.top + w * 0.02f), Size(w * 0.1f, w * 0.025f))
        }
    }

    private fun luzOpaca(a: Float) = PaletaNouveau.SOL.copy(alpha = a)

    /** La cenefa de ocre dentro de [r]: el fondo, una rama corrida con sus hojas y bayas, y los filetes. */
    private fun DrawScope.cenefa(a: AireNouveau, r: Rect) {
        val w = size.width; val u = density
        drawRect(a.luz(P.CENEFA, 1f), r.topLeft, r.size)
        val rama = Path(); val hojas = Path(); val bayas = Path()
        val cy = r.center.y; val hh = r.height
        val paso = w * 0.12f
        var x = -paso * 0.5f; var i = 0
        rama.moveTo(x, cy)
        while (x < w + paso) {
            val arriba = if (i % 2 == 0) -0.26f else 0.26f
            rama.cubicTo(x + paso * 0.3f, cy + hh * arriba, x + paso * 0.7f, cy + hh * arriba, x + paso, cy)
            val hx = x + paso * 0.5f; val hy = cy + hh * arriba * 0.8f
            val dir = if (i % 2 == 0) -1f else 1f
            hojas.moveTo(hx, hy)
            hojas.quadraticTo(hx + paso * 0.18f, hy + dir * hh * 0.34f, hx + paso * 0.36f, hy + dir * hh * 0.22f)
            hojas.quadraticTo(hx + paso * 0.14f, hy + dir * hh * 0.06f, hx, hy); hojas.close()
            bayas.addOval(Rect(x + paso * 0.9f - hh * 0.08f, cy - dir * hh * 0.2f - hh * 0.08f, x + paso * 0.9f + hh * 0.08f, cy - dir * hh * 0.2f + hh * 0.08f))
            x += paso; i++
        }
        drawPath(rama, a.luz(P.MOTIVO, 1f).copy(alpha = 0.7f), style = Stroke(1.4f * u, cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawPath(hojas, a.luz(P.MOTIVO, 1f).copy(alpha = 0.62f))
        drawPath(bayas, a.luz(P.IRIS_OSC, 1f).copy(alpha = 0.7f))
        drawRect(a.luz(P.ORO_OSC, 1f), Offset(0f, r.top + hh * 0.08f), Size(w, 0.8f * u))
        drawRect(a.luz(P.ORO_OSC, 1f), Offset(0f, r.bottom - hh * 0.12f), Size(w, 0.8f * u))
    }

    private var claveMapa = ""
    private var mapa: ImageBitmap? = null

    /**
     * La luz de la noche en la pared, de [arriba] a [abajo]: una imagen chica (un píxel por cada 1/36 del ancho), pintada una vez
     * por tamaño y modo y estirada encima: lo que se apaga con la distancia al aplique (hacia el verde botella de la calle) y, cerca
     * de él, el oro de la luz eléctrica. En los Sueños claros, el violeta del ocaso que crece hacia la derecha.
     */
    private fun DrawScope.mapaPared(a: AireNouveau, arriba: Float, abajo: Float) {
        val w = size.width
        val y0 = arriba.toInt(); val y1 = ceil(abajo).toInt()
        if (y1 <= y0 || w < 2f) return
        val cols = 36
        val filas = ((y1 - y0) / (w / cols)).toInt().coerceIn(2, 200)
        val clave = "${w.toInt()}:$y0:$y1:${a.oscuro}:${a.noche}"
        val img = mapa.takeIf { clave == claveMapa } ?: ImageBitmap(cols, filas).also { nueva ->
            CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(nueva), Size(cols.toFloat(), filas.toFloat())) {
                for (j in 0 until filas) for (i in 0 until cols) {
                    val x = (i + 0.5f) / cols
                    val y = (y0 + (j + 0.5f) / filas * (y1 - y0)) / w
                    val k = MuebleEstanteNouveau.apliqueArriba(x - MuebleEstanteNouveau.LAMP_X, y - MuebleEstanteNouveau.LAMP_Y, a.noche)
                    val c = if (a.oscuro) {
                        val fr = MuebleEstanteNouveau.fria(x, y, a.noche)
                        val hondo = mezcla(a.hondo, if (a.noche) Color(0xFF223258) else Color(0xFF15402F), (fr * 1.2f).coerceAtMost(0.7f))
                        val osc = (1f - k) * 0.88f; val cal = 0.2f * k
                        val al = (osc + cal).coerceIn(0f, 1f)
                        mezcla(hondo, P.LAMPARA, if (al > 0.001f) cal / al else 0f).copy(alpha = al)
                    } else {
                        val violeta = (0.1f + 0.22f * ((x - 0.15f) * 0.9f).coerceIn(0f, 1f)) * (1f - k)
                        val cal = 0.22f * k
                        val al = (violeta + cal).coerceIn(0f, 1f)
                        mezcla(P.SOMBRA_OCASO, P.LLAMA, if (al > 0.001f) cal / al else 0f).copy(alpha = al)
                    }
                    drawRect(c, Offset(i.toFloat(), j.toFloat()), Size(1f, 1f))
                }
            }
            mapa = nueva; claveMapa = clave
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, y0), IntSize(ceil(w).toInt(), y1 - y0))
    }

    /** Lo último de arriba, de noche: el resplandor de la tulipa encendida en la pared y en lo que tiene cerca. */
    fun DrawScope.luzDeNoche(a: AireNouveau, ch: Float) {
        if (!a.lumbre) return
        val w = size.width
        val c = Offset(w * MuebleEstanteNouveau.LAMP_X, w * MuebleEstanteNouveau.LAMP_Y)
        val f = if (a.oscuro) (if (a.noche) 0.6f else 1f) else 0.55f
        clipRect(0f, 0f, w, ch) {
            mancha(c, w * 0.34f, P.LAMPARA.copy(alpha = 0.2f * f))
            mancha(c, w * 0.12f, P.FUEGO.copy(alpha = 0.35f * f))
        }
    }
}
