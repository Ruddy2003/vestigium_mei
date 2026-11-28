package io.github.ruddy2003.vestigium.ui.biblioteca

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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.arcoApuntado
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * La pared del estante de Orden: el mismo muro de placas de acero del Ideario (las mismas hileras, de 0.38 anchos de alto con la
 * primera costura a 0.262; las mismas juntas alternas; los remaches de cabeza grande en la costura de abajo y el óxido que
 * chorrea de alguno) y la misma arquería ciega de tres arcos apuntados en relieve bajo. De día, la mancha de la luminaria con
 * las rayas de su reja; en la vigilia, la misma luz bajada a ámbar; de noche, el halo de la vela del candelero; en la guardia,
 * el frío de la luminaria. Dentro de los casilleros, el mismo muro en la sombra del mueble ([fondo]).
 */
internal object MuroEstanteOrden {
    private val P = PaletaOrden
    private val M = MuebleEstanteOrden

    /** Dónde va la costura [i] de las placas (en anchos): la primera a 0.262 y luego cada 0.38, como en el Ideario. */
    private fun costura(i: Int): Float = if (i <= 0) 0f else 0.262f + 0.38f * (i - 1)

    /** Las juntas a lo ancho de la hilera [i] (en fracción del ancho): alternas, como en el Ideario. */
    private fun juntas(i: Int): FloatArray = if (i % 2 == 0) floatArrayOf(0f, 0.31f, 0.69f, 1f) else floatArrayOf(0f, 0.15f, 0.5f, 0.86f, 1f)

    /** El color de la pared en un punto, con la luz del momento. */
    private fun DrawScope.pared(x: Float, y: Float, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float, f: Float = 0f): Color {
        val w = size.width
        val p = Offset(x, y)
        // De noche, la pared se queda en su hondo y la vela la tiñe con su halo (como en el Ideario), no la aclara entera.
        val b = a.dia(P.PARED, f, cerca(p) * 0.4f, frio(p))
        if (a.oscuro) return b
        val f = M.luminaria(x / w, y / w)
        return a.luminaria(b, if (a.noche) f * f * 0.75f else f * 0.55f)
    }

    // ---------------------------------------------------------------- la pared de arriba

    /** La pared de arriba, en [r]: las placas, la arquería, la luz del momento y sus sombras. */
    fun DrawScope.pared(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        clipRect(r.left, r.top, r.right, r.bottom) {
            // El fondo en tramos lisos: de día y en la vigilia, más claro arriba a la izquierda; de noche, el hondo con la vela.
            val nx = 10; val ny = 6
            for (j in 0 until ny) for (i in 0 until nx) {
                val x0 = r.left + r.width * i / nx; val y0 = r.top + r.height * j / ny
                drawRect(pared(x0 + r.width / nx / 2, y0 + r.height / ny / 2, a, cerca, frio), Offset(x0, y0), Size(r.width / nx + 1f, r.height / ny + 1f))
            }
            // De día, la mancha de la luminaria, suave, encima de los tramos (es la luz en la pared, antes de cualquier cosa).
            if (!a.oscuro) {
                val luz = if (a.noche) P.VIGILIA else P.LUZ_FRIA
                mancha(Offset(w * 0.06f, -w * 0.03f), w * (if (a.noche) 0.52f else 0.85f), luz.copy(alpha = if (a.noche) 0.3f else 0.34f))
            } else {
                // De noche, el halo de la vela del candelero sube por las placas; en la guardia, el frío de la luminaria.
                val c = Offset(w * M.VELA_X, w * M.VELA_Y)
                val f = if (a.dormida) 0.5f else 1f
                mancha(c, w * 0.6f, P.LLAMA.copy(alpha = 0.19f * f))
                mancha(c, w * 0.26f, P.LLAMA.copy(alpha = 0.28f * f))
                if (a.dormida) mancha(Offset(w * 0.02f, -w * 0.06f), w * 0.85f, P.GUARDIA.copy(alpha = 0.24f))
            }
            placas(r, a, cerca, frio)
            arqueria(r, a, cerca, frio)
            if (!a.oscuro || a.dormida) reja(r, a)
            pinceladas(r, if (a.oscuro) P.NOCHE else P.SOMBRA_FRIA, 811, 40, u, if (a.oscuro) 0.06f else 0.05f)
            pinceladas(r, if (a.oscuro) P.DESTELLO else P.LUZ_FRIA, 812, 22, u, if (a.oscuro) 0.025f else 0.05f)
        }
    }

    /** Una mancha radial suave. */
    private fun DrawScope.mancha(c: Offset, r: Float, color: Color) =
        drawCircle(Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), c, r.coerceAtLeast(1f)), r, c)

    /** Las costuras de las placas de [r], con sus remaches y su óxido. */
    private fun DrawScope.placas(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        var i = 0
        while (costura(i) * w < r.bottom && i < 60) {
            val y0 = costura(i) * w; val y1 = minOf(costura(i + 1) * w, r.bottom)
            val js = juntas(i)
            for (j in 0 until js.size - 1) {
                val pr = Rect(w * js[j], y0, w * js[j + 1], y1)
                if (pr.bottom < r.top) continue
                val k = cerca(pr.center); val fr = frio(pr.center)
                // Cada placa con su tono, apenas distinto.
                val tono = azar(500 + i, j)
                drawRect(if (tono > 0.5f) a.brillo(0.06f * (tono - 0.5f) * 2f, 0.1f + k) else a.sombra(0.08f * (0.5f - tono) * 2f), pr.topLeft, pr.size)
                // La costura: un canto de luz arriba e izquierda y la junta en sombra abajo y a la derecha.
                drawRect(a.sombra(0.5f), Offset(pr.right - 1.6f * u, pr.top), Size(1.6f * u, pr.height))
                if (y1 == costura(i + 1) * w) drawRect(a.sombra(0.55f), Offset(pr.left, pr.bottom - 1.8f * u), Size(pr.width, 1.8f * u))
                drawRect(a.destello(0.2f, k, fr), Offset(pr.left, pr.top), Size(pr.width, 1f * u))
                drawRect(a.destello(0.14f, k, fr), Offset(pr.left, pr.top), Size(1f * u, pr.height))
                if (y1 < costura(i + 1) * w) continue
                // Los remaches de la costura de abajo, con el óxido que chorrea de alguno.
                val m = (pr.width / (w * 0.045f)).toInt().coerceAtLeast(2)
                for (q in 0 until m) {
                    val p = Offset(pr.left + pr.width * (q + 0.5f) / m, pr.bottom - w * 0.011f)
                    val kq = cerca(p)
                    if (azar(530 + i * 9 + j, q) < 0.3f) {
                        val largo = w * (0.012f + 0.035f * azar(540 + i, q + j * 7))
                        val ox = a.luz(P.OXIDO, kq * 0.6f, frio(p))
                        drawLine(ox.copy(alpha = 0.32f), Offset(p.x + 0.4f * u, p.y + 1.6f * u), Offset(p.x + 0.8f * u, p.y + 1.6f * u + largo), 1.6f * u, StrokeCap.Round)
                        drawCircle(ox.copy(alpha = 0.4f), 2.6f * u, p + Offset(0.6f * u, 1f * u))
                    }
                    roblonOrden(p, w * 0.0052f, a, kq, mezcla(P.ACERO_MEDIO, P.ACERO_CLARO, 0.3f), frio(p))
                }
            }
            i++
        }
    }

    /** La arquería ciega: los tres arcos apuntados del Ideario, en relieve bajo, con los remaches de su rosca. */
    private fun DrawScope.arqueria(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val banda = w * 0.017f
        val abajo = r.bottom
        for (q in 0 until 3) {
            val x0 = w * (0.012f + q * 0.325f); val x1 = x0 + w * 0.325f
            val arco = arcoApuntado(Rect(x0, w * 0.034f, x1, abajo), w * 0.155f)
            val c = Offset((x0 + x1) / 2, w * 0.12f)
            val k = cerca(c); val fr = frio(c)
            // El tímpano, un punto hundido.
            clipPath(arco) { drawRect(a.sombra(0.07f), Offset(x0, 0f), Size(x1 - x0, abajo)) }
            translate(banda * 0.18f, banda * 0.22f) { drawPath(arco, a.sombra(0.45f), style = Stroke(banda)) }
            drawPath(arco, a.dia(mezcla(P.ACERO_MEDIO, P.ACERO_CLARO, 0.45f), 0f, k * 0.9f, fr).let { if (!a.oscuro) a.luminaria(it, M.luminaria(c.x / w, c.y / w) * 0.4f) else it }, style = Stroke(banda))
            translate(-banda * 0.28f, -banda * 0.28f) { drawPath(arco, a.destello(0.3f, k, fr), style = Stroke(1f * u)) }
            translate(banda * 0.3f, banda * 0.3f) { drawPath(arco, a.sombra(0.3f), style = Stroke(1f * u)) }
            val cx = (x0 + x1) / 2
            listOf(0.12f, 0.3f, 0.5f, 0.7f, 0.88f).forEach { f ->
                val ang = Math.PI * (1.0 - f)
                val px = cx + (x1 - x0) / 2 * cos(ang).toFloat() * 0.97f
                val py = w * 0.19f - w * 0.152f * sin(ang).toFloat().pow(0.8f)
                val p = Offset(px, py)
                roblonOrden(p, w * 0.0042f, a, cerca(p), P.ACERO_CLARO, frio(p))
            }
        }
    }

    /**
     * Las rayas de la reja de la luminaria en la mancha de luz (de día y en la vigilia): sombras finas en diagonal dentro de un
     * óvalo arriba a la izquierda, en la pared (lo de delante se pinta después y no las recibe).
     */
    private fun DrawScope.reja(r: Rect, a: AireOrden) {
        val w = size.width
        val zona = Path().apply { addOval(Rect(-w * 0.2f, -w * 0.2f, w * 0.6f, w * 0.44f)) }
        val s = a.sombra(if (a.noche) 0.06f else 0.07f)
        clipPath(zona) {
            for (q in 0 until 4) {
                val x = w * (-0.06f + 0.14f * q)
                drawPath(poligonoOrden(x, 0f, x + w * 0.028f, 0f, x + w * 0.2f, w * 0.5f, x + w * 0.172f, w * 0.5f), s)
            }
            drawPath(poligonoOrden(0f, w * 0.15f, w * 0.6f, w * 0.27f, w * 0.6f, w * 0.29f, 0f, w * 0.17f), s)
        }
        // Lejos de la luminaria, la pared se va a su sombra hacia la derecha.
        drawRect(Brush.horizontalGradient(listOf(Color.Transparent, a.sombra(0.2f)), w * 0.4f, w), Offset(w * 0.4f, r.top), Size(w * 0.6f, r.height))
    }

    // ---------------------------------------------------------------- el muro dentro de los casilleros

    /**
     * El muro de placas dentro del casillero [r], en la sombra del mueble: los tramos con la luz que le llega, las costuras que
     * caigan dentro (a la misma altura que en la pared de arriba, así el muro sigue por detrás), sus remaches en un solo camino,
     * el óxido y alguna mancha.
     */
    fun DrawScope.fondo(r: Rect, a: AireOrden, cerca: (Offset) -> Float, frio: (Offset) -> Float) {
        val u = density
        val w = size.width
        val y = r.top + r.height * 0.55f
        fun en(x: Float): Color {
            val p = Offset(x, y)
            val base = a.dia(P.PARED, -0.38f, cerca(p), frio(p))
            return if (!a.oscuro) a.luminaria(base, M.luminaria(p.x / w, r.top / w) * 0.25f) else base
        }
        if (a.lumbre) {
            // Con lumbre, un degradado a lo ancho: el muro pasa de un charco a la sombra sin escalones.
            val paradas = Array(9) { i -> (i / 8f) to en(r.left + r.width * i / 8f) }
            drawRect(Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right), r.topLeft, r.size)
        } else {
            val n = 4
            val paso = r.width / n
            for (i in 0 until n) {
                val x0 = r.left + paso * i
                drawRect(en(x0 + paso / 2), Offset(x0, r.top), Size(paso + (if (i < n - 1) 1f else 0f), r.height))
            }
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las costuras que caen en este casillero.
            var i = 1
            while (costura(i - 1) * w < r.bottom && i < 80) {
                val ys = costura(i) * w
                if (ys > r.top - w * 0.02f && ys < r.bottom + w * 0.02f) {
                    drawRect(a.sombra(0.45f), Offset(r.left, ys - 1.8f * u), Size(r.width, 1.8f * u))
                    drawRect(a.destello(0.14f, cerca(Offset(r.center.x, ys)), frio(Offset(r.center.x, ys))), Offset(r.left, ys), Size(r.width, 1f * u))
                    val rem = Path(); val bri = Path()
                    val m = (w / (w * 0.045f)).toInt()
                    for (q in 0 until m) {
                        val px = w * (q + 0.5f) / m
                        if (px < r.left - 4f * u || px > r.right + 4f * u) continue
                        val c = Offset(px, ys - w * 0.011f)
                        val rr = w * 0.0048f
                        rem.addOval(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr))
                        bri.addOval(Rect(c.x - rr * 0.65f, c.y - rr * 0.7f, c.x - rr * 0.05f, c.y - rr * 0.1f))
                    }
                    val kc = cerca(Offset(r.center.x, ys)); val fc = frio(Offset(r.center.x, ys))
                    translate(1.2f * u, 1.4f * u) { drawPath(rem, a.sombra(0.4f)) }
                    drawPath(rem, a.dia(P.ACERO_MEDIO, -0.3f, kc, fc))
                    drawPath(bri, a.destello(0.45f, kc, fc))
                }
                // Las juntas verticales de la hilera que pasa por aquí.
                val js = juntas(i - 1)
                for (j in 1 until js.size - 1) {
                    val x = w * js[j]
                    if (x < r.left || x > r.right) continue
                    val y0 = maxOf(r.top, costura(i - 1) * w); val y1 = minOf(r.bottom, ys)
                    if (y1 > y0) drawRect(a.sombra(0.4f), Offset(x - 1.6f * u, y0), Size(1.6f * u, y1 - y0))
                }
                i++
            }
            // Una chorrera de óxido y una mancha de humedad en algunos.
            val s = (r.top / maxOf(1f, w) * 1000).toInt()
            for (q in 0 until 3) {
                val x = r.left + r.width * azar(s + 61, q)
                val y0 = r.top + r.height * (0.15f + 0.3f * azar(s + 62, q))
                val ox = a.dia(P.OXIDO, -0.2f, cerca(Offset(x, y0)), frio(Offset(x, y0)))
                drawLine(ox.copy(alpha = 0.3f), Offset(x, y0), Offset(x + 0.6f * u, y0 + r.height * (0.2f + 0.3f * azar(s + 63, q))), (1.4f + azar(s + 64, q)) * u, StrokeCap.Round)
            }
            pinceladas(r, if (a.oscuro) P.NOCHE else P.SOMBRA_FRIA, s, 8, u, 0.09f)
        }
    }
}
