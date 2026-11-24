package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La pared de arriba del estante de Galia ([MuebleEstanteGalia]), en la imagen guardada: el alero por dentro (la paja
 * ennegrecida de humo entre los cabrios y las varas de avellano que la sujetan), la viga de la pared con sus ganchos, y el barro
 * alisado con su lechada de arcilla blanca, la cenefa pintada a la almagra y al hollín bajo la viga, las pasadas de la mano,
 * las grietas y la mancha de hollín sobre la tea. De día, la mancha de sol
 * de la puerta pinta el barro (la misma pared, alumbrada); de noche, el barro toma el color de la tea según la distancia. La tea
 * en su soporte de hierro, apagada de día y encendida de noche.
 */
internal object MuroEstanteGalia {
    private val P = PaletaGalia

    /** Dónde empieza la viga de la pared, su grueso y la cenefa de debajo, en anchos. */
    const val Y_VIGA = 0.112f
    const val VIGA = 0.034f
    private const val FRISO0 = 0.158f
    private const val FRISO1 = 0.193f

    fun DrawScope.pared(a: AireGalia, apoyo: Float, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width
        val muro = Rect(0f, w * (Y_VIGA + VIGA) - 1f, w, apoyo + w * 0.03f)
        baseBarro(muro, a, null)
        pintura(muro, a, cerca, null)
        // La mancha de la puerta: la misma pared, alumbrada, con su penumbra.
        if (m != null) {
            clipRect(muro.left, muro.top, muro.right, muro.bottom) {
                val medio = ManchaGalia(m.q, m.color, m.fuerza * 0.45f)
                clipPath(m.forma(w, 0f, 0.03f)) { baseBarro(muro, a, medio); pintura(muro, a, cerca, medio) }
                clipPath(m.forma(w)) { baseBarro(muro, a, m); pintura(muro, a, cerca, m) }
            }
        }
        alero(a, cerca)
        viga(a, cerca, m)
        soporteTea(a, cerca, m)
    }

    // ---------------------------------------------------------------- el barro blanqueado

    /** El color del barro de fondo: de día, a la sombra del cuarto; de noche, el charco de la tea (un solo degradado). */
    private fun DrawScope.baseBarro(r: Rect, a: AireGalia, m: ManchaGalia?) {
        val w = size.width
        val cara = mezcla(P.BLANCO, P.BLANCO_SOMBRA, 0.22f)
        if (m != null && !a.oscuro) {
            // Dentro de la mancha: el barro al sol, más fuerte arriba (junto a la puerta) y algo menos abajo.
            val c0 = a.sol(cara, a.dia(cara, 0f), m.fuerza); val c1 = a.sol(cara, a.dia(mezcla(cara, P.BLANCO_SOMBRA, 0.3f), 0f), m.fuerza * 0.85f)
            drawRect(Brush.verticalGradient(listOf(c0, c1), r.top, r.bottom), r.topLeft, r.size)
            return
        }
        if (a.oscuro) {
            val c = Offset(w * MuebleEstanteGalia.TEA_X, w * MuebleEstanteGalia.TEA_Y)
            val radios = floatArrayOf(0f, 0.04f, 0.09f, 0.15f, 0.23f, 0.33f, 0.46f, 0.64f, 0.95f)
            val col = radios.map { rr -> a.luz(cara, MuebleEstanteGalia.teaArriba(rr, 0f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.95f to col[i] }, center = c, radius = w * 0.95f), r.topLeft, r.size)
            // La luna, dentro de su parche: la cal virada a un azul hondo, un paso más clara que lo que la rodea (no un velo pálido).
            if (m != null) drawRect(Color(0xFF283A5C).copy(alpha = (0.72f * m.fuerza).coerceAtMost(1f)), r.topLeft, r.size)
            return
        }
        // De día (y al ocaso): el barro a la sombra del cuarto, más vivo junto a la puerta y más hondo a la derecha y bajo la viga.
        val claro = a.dia(cara, 0f)
        val hondo = a.dia(mezcla(P.BLANCO_SOMBRA, P.BARRO, 0.2f), 0f)
        drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.45f), hondo), r.left, r.right), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.2f), a.sombra(0f)), r.top, r.top + w * 0.12f), r.topLeft, Size(r.width, w * 0.12f))
    }

    /**
     * Lo pintado y lo gastado del barro (con mezcla de multiplicar, así sigue la luz que tenga debajo): las pasadas de la mano,
     * la cenefa de la viga (dos filetes de hollín y dos de glasto, una hilera de rombos de almagra apartados y un punto de
     * hollín entre cada dos), el hollín de la tea, una mancha de humedad y las grietas.
     */
    private fun DrawScope.pintura(r: Rect, a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las pasadas de la mano al alisar: arcos anchos, un poco más oscuros, con su labio de luz.
            val arcos = Path(); val labios = Path()
            for (k in 0 until 26) {
                val c = Offset(w * azar(8101, k), r.top + r.height * azar(8102, k))
                val rr = w * (0.025f + 0.04f * azar(8103, k))
                val a0 = 200f + 70f * azar(8104, k)
                arcos.addArc(Rect(c.x - rr, c.y - rr, c.x + rr, c.y + rr), a0, 70f)
                labios.addArc(Rect(c.x - rr, c.y - rr - 1.4f * u, c.x + rr, c.y + rr - 1.4f * u), a0 + 6f, 55f)
            }
            drawPath(arcos, Color(0xFFB09A78).copy(alpha = 0.5f), style = Stroke(2f * u), blendMode = BlendMode.Multiply)
            if (!a.oscuro) drawPath(labios, a.claro(if (m != null) 0.3f else 0.16f), style = Stroke(1f * u))
            // La cenefa de la viga.
            val y0 = w * FRISO0; val y1 = w * FRISO1
            val hollin = Color(0xFF5A4E44)
            val almagra = Color(0xFFC0705A)
            drawRect(hollin, Offset(0f, y0), Size(w, w * 0.0045f), blendMode = BlendMode.Multiply)
            drawRect(hollin, Offset(0f, y1 - w * 0.0045f), Size(w, w * 0.0045f), blendMode = BlendMode.Multiply)
            // Un filete de azul de glasto bajo el de arriba.
            drawRect(Color(0xFF8AA0BC), Offset(0f, y0 + w * 0.0065f), Size(w, w * 0.003f), blendMode = BlendMode.Multiply)
            drawRect(Color(0xFF8AA0BC), Offset(0f, y1 - w * 0.0095f), Size(w, w * 0.003f), blendMode = BlendMode.Multiply)
            // Una hilera de rombos de almagra, apartados, y entre cada dos un punto de hollín; alguno borrado del todo y
            // otros a medias.
            val rombos = Path(); val puntos = Path()
            val paso = w * 0.05f
            val ym = (y0 + y1) / 2; val ra = (y1 - y0) * 0.3f
            var x = w * 0.012f; var j = 0
            while (x < w) {
                val gasto = azar(8110, j)
                if (gasto > 0.1f) {
                    val e = if (gasto < 0.3f) 0.6f else 1f
                    val cx = x + paso * 0.5f
                    rombos.moveTo(cx - paso * 0.3f * e, ym); rombos.lineTo(cx, ym - ra * e); rombos.lineTo(cx + paso * 0.3f * e, ym); rombos.lineTo(cx, ym + ra * e); rombos.close()
                }
                puntos.addOval(Rect(x - w * 0.003f, ym - w * 0.003f, x + w * 0.003f, ym + w * 0.003f))
                x += paso; j++
            }
            drawPath(rombos, almagra, blendMode = BlendMode.Multiply)
            drawPath(puntos, hollin, blendMode = BlendMode.Multiply)
            // Los rayones del gastado en la cenefa, que la cortan a trechos.
            for (q in 0 until 9) {
                val xq = w * azar(8120, q)
                drawLine(if (m != null) a.claro(0.4f) else Color(0xFFE8DCC0).copy(alpha = if (a.oscuro) 0.08f else 0.55f), Offset(xq, y0 + w * 0.003f), Offset(xq + w * 0.02f, y1 - w * 0.004f), 1.1f * u)
            }
            // El hollín de la tea, que sube negro por el barro hasta la viga.
            val tx = w * MuebleEstanteGalia.TEA_X
            val yh = w * (Y_VIGA + VIGA)
            drawOval(Brush.verticalGradient(listOf(Color(0xFF6A5A4C).copy(alpha = 0.7f), Color(0xFF8A7A66).copy(alpha = 0f)), yh, w * 0.215f),
                Offset(tx - w * 0.026f, yh - w * 0.03f), Size(w * 0.06f, w * 0.25f - yh), blendMode = BlendMode.Multiply)
            // Una mancha de humedad abajo a la izquierda y las grietas del barro seco.
            drawOval(Color(0xFFD8C8AA).copy(alpha = 0.4f), Offset(-w * 0.02f, r.bottom - w * 0.1f), Size(w * 0.2f, w * 0.12f), blendMode = BlendMode.Multiply)
            val grietas = Path()
            for (g in 0 until 5) {
                var q = Offset(w * (0.08f + 0.84f * azar(8130, g)), w * (0.21f + 0.2f * azar(8131, g)))
                grietas.moveTo(q.x, q.y)
                for (i in 0 until 8) {
                    q += Offset((azar(8132 + g, i) - 0.5f) * w * 0.016f, w * (0.006f + 0.009f * azar(8140 + g, i)))
                    grietas.lineTo(q.x, q.y)
                }
            }
            drawPath(grietas, Color(0xFF6E5E4C), style = Stroke(0.9f * u, cap = StrokeCap.Round, join = StrokeJoin.Round), blendMode = BlendMode.Multiply)
        }
    }

    // ---------------------------------------------------------------- el alero por dentro

    /**
     * El alero: la paja vista por debajo, ennegrecida de humo, con las varas de avellano que la sujetan y los cabrios que bajan
     * a la viga, abiertos en abanico (se acercan al subir). De noche, la tea la alumbra desde abajo y lo alto se pierde.
     */
    private fun DrawScope.alero(a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val y1 = w * Y_VIGA + 1f
        val r = Rect(0f, 0f, w, y1)
        // La paja: de día, parda y más negra arriba; de noche, el charco de la tea.
        if (a.oscuro) {
            val c = Offset(w * MuebleEstanteGalia.TEA_X, w * MuebleEstanteGalia.TEA_Y)
            val radios = floatArrayOf(0f, 0.08f, 0.16f, 0.26f, 0.4f, 0.6f, 0.9f)
            val col = radios.map { rr -> a.luz(P.PAJA_HUMO, MuebleEstanteGalia.teaArriba(rr * 1.2f, 0f, a.noche)) }
            drawRect(Brush.radialGradient(*Array(radios.size) { i -> radios[i] / 0.9f to col[i] }, center = c, radius = w * 0.9f), r.topLeft, r.size)
        } else {
            drawRect(Brush.verticalGradient(listOf(a.dia(mezcla(P.PAJA_HUMO, P.HOLLIN, 0.55f), 0f), a.dia(P.PAJA_HUMO, 0f), a.dia(mezcla(P.PAJA_HUMO, P.PAJA, 0.4f), 0f)), 0f, y1), r.topLeft, r.size)
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            // Las cabezas de los haces de paja, en hileras entre las varas: trazos cortos que bajan.
            val claras = Path(); val oscuras = Path()
            val filas = 4
            for (f in 0 until filas) {
                val yf = y1 * (0.08f + 0.24f * f)
                for (q in 0 until 70) {
                    val x = w * 1.04f * azar(8200 + f, q) - w * 0.02f
                    val l = w * (0.012f + 0.018f * azar(8210 + f, q))
                    val inc = (x - w * 0.5f) / w * 0.25f
                    (if (q % 3 == 0) claras else oscuras).apply { moveTo(x, yf); lineTo(x + inc * l * 3f, yf + l) }
                }
            }
            drawPath(oscuras, a.luz(P.HOLLIN, cerca(Offset(w * 0.8f, y1 * 0.6f))).copy(alpha = 0.6f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
            drawPath(claras, a.dia(P.PAJA, -0.05f, cerca(Offset(w * 0.8f, y1 * 0.6f))).copy(alpha = if (a.oscuro) 0.55f else 0.5f), style = Stroke(0.9f * u, cap = StrokeCap.Round))
            // Las varas de avellano, de lado a lado, con su luz arriba y su sombra abajo.
            for (f in 0 until filas) {
                val yv = y1 * (0.08f + 0.24f * f) - w * 0.003f
                val k = cerca(Offset(w * 0.8f, yv))
                drawRect(a.sombra(0.45f), Offset(0f, yv + w * 0.004f), Size(w, w * 0.004f))
                drawRect(a.dia(P.ZARZO, -0.1f, k), Offset(0f, yv), Size(w, w * 0.0055f))
                drawRect(a.brillo(0.25f, k), Offset(0f, yv), Size(w, 0.8f * u))
            }
            // Los cabrios: palos redondos con su corteza, que se abren hacia arriba; cada uno echa su sombra en la paja.
            for (i in 0 until 8) {
                val xb = w * (-0.04f + 0.155f * i + 0.02f * azar(8220, i))
                val xt = xb + (xb - w * 0.5f) * 0.42f
                val g0 = w * 0.024f; val g1 = w * 0.034f
                val k = cerca(Offset(xb, y1 * 0.6f))
                val cara = poligonoGalia(xb - g0 / 2, y1, xb + g0 / 2, y1, xt + g1 / 2, -1f, xt - g1 / 2, -1f)
                drawPath(poligonoGalia(xb + g0 / 2, y1, xb + g0 / 2 + w * 0.012f, y1, xt + g1 / 2 + w * 0.02f, -1f, xt + g1 / 2, -1f), a.sombra(0.4f))
                drawPath(cara, a.dia(P.CABRIO, 0f, k))
                clipPath(cara) {
                    val luzIzq = !a.oscuro || xb > w * MuebleEstanteGalia.TEA_X
                    val xl0 = if (luzIzq) xb - g0 / 2 else xb + g0 * 0.1f; val xl1 = if (luzIzq) xt - g1 / 2 else xt + g1 * 0.1f
                    drawPath(poligonoGalia(xl0, y1, xl0 + g0 * 0.4f, y1, xl1 + g1 * 0.4f, -1f, xl1, -1f), a.brillo(0.22f, k))
                    val xs0 = if (luzIzq) xb + g0 * 0.15f else xb - g0 / 2; val xs1 = if (luzIzq) xt + g1 * 0.15f else xt - g1 / 2
                    drawPath(poligonoGalia(xs0, y1, xs0 + g0 * 0.35f, y1, xs1 + g1 * 0.35f, -1f, xs1, -1f), a.sombra(0.3f))
                }
            }
            // El hollín de tantos inviernos, más negro arriba.
            drawRect(Brush.verticalGradient(listOf(a.sombra(if (a.oscuro) 0.6f else 0.55f), a.sombra(0f)), 0f, y1 * 0.7f), Offset.Zero, Size(w, y1 * 0.7f))
        }
    }

    // ---------------------------------------------------------------- la viga de la pared

    /** La viga de roble sobre la que apoya el alero: labrada a azuela, con su luz arriba, sus ganchos y su sombra en el barro. */
    private fun DrawScope.viga(a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val y0 = w * Y_VIGA; val y1 = w * (Y_VIGA + VIGA)
        // La sombra de la viga en el barro, franca.
        drawRect(a.sombra(if (a.oscuro) 0.5f else 0.38f), Offset(0f, y1), Size(w, w * 0.008f))
        drawRect(a.sombra(0.14f), Offset(0f, y1 + w * 0.008f), Size(w, w * 0.012f))
        val n = if (a.oscuro) 24 else 1
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1.2f
            val k = cerca(Offset((x0 + x1) / 2, (y0 + y1) / 2))
            fun c(col: Color, f: Float): Color {
                val base = a.dia(col, f, k)
                return if (m != null && !a.oscuro) { val lit = m.toca((x0 + x1) / 2 / w, (y0 + y1) / 2 / w, ManchaGalia.FRENTE * 0.3f); if (lit > 0f) a.sol(col, base, lit * m.fuerza) else base } else base
            }
            drawRect(c(P.ROBLE_LUZ, -0.05f), Offset(x0, y0), Size(x1 - x0, (y1 - y0) * 0.22f + 1f))
            drawRect(c(P.ROBLE, 0f), Offset(x0, y0 + (y1 - y0) * 0.22f), Size(x1 - x0, (y1 - y0) * 0.56f + 1f))
            drawRect(c(P.ROBLE_OSC, 0f), Offset(x0, y0 + (y1 - y0) * 0.78f), Size(x1 - x0, (y1 - y0) * 0.22f))
        }
        val km = cerca(Offset(w * 0.8f, y0))
        drawRect(a.brillo(0.45f, km), Offset(0f, y0), Size(w, 1f * u))
        // Los golpes de la azuela y una grieta de secado.
        val golpes = Path(); val filos = Path()
        var x = w * 0.01f; var j = 0
        while (x < w) {
            val l = (y1 - y0) * (0.3f + 0.4f * azar(8300, j))
            val ya = y0 + (y1 - y0) * (0.1f + 0.5f * azar(8301, j))
            golpes.moveTo(x, ya); golpes.lineTo(x + w * 0.006f, ya + l)
            filos.moveTo(x + w * 0.004f, ya); filos.lineTo(x + w * 0.01f, ya + l)
            x += w * (0.025f + 0.03f * azar(8302, j)); j++
        }
        drawPath(golpes, a.sombra(0.32f), style = Stroke(1f * u))
        drawPath(filos, a.brillo(0.2f, km), style = Stroke(0.7f * u))
        val grieta = Path().apply {
            moveTo(w * 0.12f, y0 + (y1 - y0) * 0.5f)
            var gx = w * 0.12f; var q = 0
            while (gx < w * 0.48f) { gx += w * 0.02f; lineTo(gx, y0 + (y1 - y0) * (0.45f + 0.12f * azar(8310, q))); q++ }
        }
        drawPath(grieta, a.luz(P.ROBLE_HONDO, km).copy(alpha = 0.75f), style = Stroke(1f * u, cap = StrokeCap.Round))
        // Los ganchos de hierro de lo colgado (las piezas de cerdo y la ristra) y una clavija de roble.
        listOf(0.56f, 0.655f, 0.735f).forEach { fx ->
            val p = Offset(w * fx, y1 - w * 0.004f)
            val k = cerca(p)
            clavoGalia(p, 2f * u, a, k)
            drawPath(Path().apply { moveTo(p.x, p.y); lineTo(p.x, p.y + w * 0.014f); quadraticTo(p.x, p.y + w * 0.024f, p.x + w * 0.008f, p.y + w * 0.02f) }, a.luz(P.HIERRO, k), style = Stroke(1.6f * u, cap = StrokeCap.Round))
        }
    }

    // ---------------------------------------------------------------- la tea

    /** El soporte de hierro de la tea, clavado en el barro, y la tea de pino metida en su anillo (sin la llama). */
    private fun DrawScope.soporteTea(a: AireGalia, cerca: (Offset) -> Float, m: ManchaGalia?) {
        val w = size.width; val u = density
        val tx = w * MuebleEstanteGalia.TEA_X; val ty = w * MuebleEstanteGalia.TEA_Y
        val k = cerca(Offset(tx, ty + w * 0.06f))
        val anillo = Offset(tx + w * 0.006f, ty + w * 0.07f)
        val placa = Rect(tx + w * 0.03f, ty + w * 0.045f, tx + w * 0.042f, ty + w * 0.11f)
        // La sombra del soporte y de la tea en el barro (de día, corrida a la derecha y abajo).
        val dx = if (a.oscuro) w * 0.004f else w * 0.012f
        drawLine(a.sombra(0.3f), Offset(tx + dx, ty + w * 0.02f + dx), Offset(tx + w * 0.012f + dx, ty + w * 0.125f + dx), w * 0.013f, StrokeCap.Round)
        drawRect(a.sombra(0.35f), Offset(placa.left + 1.2f * u, placa.top + 1.4f * u), placa.size)
        // La pletina clavada y el brazo que sale hacia el anillo.
        drawRect(a.luz(P.HIERRO, k), placa.topLeft, placa.size)
        drawRect(a.brillo(0.35f, k), placa.topLeft, Size(placa.width, 0.9f * u))
        clavoGalia(Offset(placa.center.x, placa.top + placa.height * 0.2f), 1.7f * u, a, k)
        clavoGalia(Offset(placa.center.x, placa.top + placa.height * 0.8f), 1.7f * u, a, k)
        drawLine(a.luz(P.HIERRO, k), Offset(placa.left, anillo.y), anillo, 2.2f * u, StrokeCap.Round)
        // La tea: un palo de pino resinoso, ladeado, con la cabeza de astillas atadas y negras.
        val pie = Offset(tx + w * 0.012f, ty + w * 0.128f)
        val cabeza = Offset(tx, ty + w * 0.012f)
        drawLine(a.dia(P.ROBLE, 0.05f, k), pie, cabeza, w * 0.011f, StrokeCap.Round)
        drawLine(a.brillo(0.3f, k), pie - Offset(w * 0.003f, 0f), cabeza - Offset(w * 0.003f, 0f), 0.9f * u, StrokeCap.Round)
        drawLine(a.luz(P.HOLLIN, k), cabeza + Offset(w * 0.001f, w * 0.006f), cabeza - Offset(w * 0.001f, w * 0.014f), w * 0.017f, StrokeCap.Round)
        for (q in 0 until 3) drawLine(a.luz(P.CUERO, k), Offset(cabeza.x - w * 0.009f, cabeza.y + w * (0.012f + 0.008f * q)), Offset(cabeza.x + w * 0.009f, cabeza.y + w * (0.014f + 0.008f * q)), 1.2f * u)
        // El anillo, por delante de la tea.
        ovaloGalia(a.luz(P.HIERRO, k), Rect(anillo.x - w * 0.012f, anillo.y - w * 0.005f, anillo.x + w * 0.012f, anillo.y + w * 0.005f), style = Stroke(2f * u))
        drawArc(a.brillo(0.4f, k), 190f, 120f, false, Offset(anillo.x - w * 0.012f, anillo.y - w * 0.005f), Size(w * 0.024f, w * 0.01f), style = Stroke(0.8f * u))
    }

    /** La llama de la tea (de noche y en los Sueños), con su halo; de día, un hilo de humo de la tea apagada. */
    fun DrawScope.teaEncendida(a: AireGalia, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val c = Offset(w * MuebleEstanteGalia.TEA_X, w * MuebleEstanteGalia.TEA_Y)
        if (!a.lumbre) {
            // El hilo de humo de la tea apagada, que sube torcido hacia la viga.
            val humo = Path().apply { moveTo(c.x, c.y); cubicTo(c.x - w * 0.012f, c.y - w * 0.04f, c.x + w * 0.014f, c.y - w * 0.06f, c.x - w * 0.004f, w * (Y_VIGA + VIGA)) }
            drawPath(humo, P.CENIZA.copy(alpha = 0.35f), style = Stroke(2.2f * u, cap = StrokeCap.Round))
            return
        }
        val f = if (a.oscuro && a.noche) 0.62f else if (a.oscuro) 1f else 0.7f
        // El halo cálido en el aire, corto (el barro ya lo tiñe la base).
        drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.3f * f), P.LLAMA.copy(alpha = 0f)), c, w * 0.17f), w * 0.17f, c)
        drawCircle(Brush.radialGradient(listOf(P.FUEGO.copy(alpha = 0.35f * f), P.FUEGO.copy(alpha = 0f)), c, w * 0.05f), w * 0.05f, c)
        // La llama: tres lenguas, la de fuera naranja, la de dentro amarilla y el corazón casi blanco, inclinadas por el tiro.
        val alto = w * 0.062f * (0.75f + 0.25f * f); val ancho = w * 0.024f
        fun lengua(dx: Float, h: Float, an: Float, col: Color) {
            val base = Offset(c.x + dx, c.y + w * 0.012f)
            drawPath(Path().apply {
                moveTo(base.x - an, base.y)
                cubicTo(base.x - an * 1.1f, base.y - h * 0.45f, base.x - an * 0.2f, base.y - h * 0.7f, base.x + an * 0.35f, base.y - h)
                cubicTo(base.x + an * 0.3f, base.y - h * 0.65f, base.x + an * 1.1f, base.y - h * 0.4f, base.x + an, base.y)
                close()
            }, col)
        }
        lengua(0f, alto, ancho, P.BRASA.copy(alpha = 0.9f))
        lengua(w * 0.002f, alto * 0.78f, ancho * 0.75f, P.LLAMA)
        lengua(w * 0.003f, alto * 0.5f, ancho * 0.45f, P.FUEGO)
        // Unas chispas que suben.
        for (q in 0 until 4) drawCircle(P.FUEGO.copy(alpha = 0.8f * f), (0.8f + 0.6f * azar(8400, q)) * u, Offset(c.x + (azar(8401, q) - 0.5f) * w * 0.03f, c.y - alto * (1.1f + 0.5f * azar(8402, q))))
    }
}
