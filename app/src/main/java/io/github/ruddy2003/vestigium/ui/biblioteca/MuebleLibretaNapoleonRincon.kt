package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo fijo del rincón de la libreta de Napoleónico ([MuebleLibretaNapoleon]), pintado una vez por tamaño y modo: la pared amarilla
 * con sus manos de pintura, el recuadro de filetes verdes y la trama de la ventana (el sol o la luna); la cortina de damasco
 * recogida; la mesa de escribir con su cuero y su faldón de caoba; el atril con su marco, su puntal y su listón; el tintero con
 * la pluma y la palmatoria con su vela (de noche, encendida) y la barra de lacre.
 */
internal object RinconLibretaNapoleon {
    private val P = PaletaNapoleon
    /** La seda verde Imperio de la cortina: su cara, su luz y lo hondo de los pliegues. */
    private val DAMASCO = Color(0xFF2E6046)
    private val DAMASCO_LUZ = Color(0xFF6E9E72)
    private val DAMASCO_OSC = Color(0xFF10281C)
    /** El cuero verde del tablero y del atril. */
    private val CUERO_VERDE = Color(0xFF2E5C40)
    /** La cera de la vela. */
    private val CERA = Color(0xFFF2EAD6)

    private fun SitioLibretaNapoleon.k(a: AireNapoleon, p: Offset) = MuebleLibretaNapoleon.cerca(p, this, a)

    /** Un degradado de izquierda a derecha de [col] (con [f] como en `dia`) según la luz de la vela a lo ancho de [x0]..[x1], a la altura [y]. */
    private fun SitioLibretaNapoleon.aLoAncho(a: AireNapoleon, col: Color, f: Float, x0: Float, x1: Float, y: Float): Brush {
        if (!a.oscuro) return Brush.horizontalGradient(listOf(a.dia(col, f), a.dia(col, f)), x0, x1)
        val n = 6
        return Brush.horizontalGradient(List(n) { i -> a.dia(col, f, k(a, Offset(x0 + (x1 - x0) * i / (n - 1f), y))) }, x0, x1)
    }

    fun DrawScope.rincon(s: SitioLibretaNapoleon, a: AireNapoleon, ac: AireNapoleon) {
        val m = MuebleLibretaNapoleon.mancha(s, a)
        pared(s, a, m)
        sombraDelAtril(s, a)
        cortina(s, a)
        mesa(s, ac)
        atril(s, ac, m)
        tintero(s, ac)
        palmatoria(s, ac)
    }

    // ---------------------------------------------------------------- la pared

    private fun DrawScope.pared(s: SitioLibretaNapoleon, a: AireNapoleon, m: ManchaNapoleon) {
        val U = s.U
        val r = Rect(0f, 0f, s.w, s.mesa + 2f * U)
        amarillo(s, a, r, null)
        // La trama de la ventana: la misma pared, alumbrada (de día, el sol; de noche, la luna), con su penumbra.
        clipRect(r.left, r.top, r.right, r.bottom) {
            val medio = ManchaNapoleon(m.q, m.color, m.fuerza * 0.42f)
            clipPath(m.forma(1f, 0f, 0.014f)) { amarillo(s, a, r, medio) }
            clipPath(m.forma(1f)) { amarillo(s, a, r, m) }
        }
        pintura(s, a, r)
        // Lo hondo donde la pared se junta con el tablero.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0f), a.sombra(if (a.oscuro) 0.3f else 0.18f)), s.mesa - 14f * U, s.mesa), Offset(0f, s.mesa - 14f * U), Size(s.w, 16f * U))
    }

    /** El amarillo de [r]: de día, más vivo hacia la ventana y más hondo bajo el techo; de noche, encendido junto a la vela; con [m], alumbrado. */
    private fun DrawScope.amarillo(s: SitioLibretaNapoleon, a: AireNapoleon, r: Rect, m: ManchaNapoleon?) {
        val U = s.U
        if (a.oscuro) {
            val radios = floatArrayOf(0f, 30f, 70f, 120f, 180f, 260f, 360f, 480f)
            val tope = 480f * U
            drawRect(Brush.radialGradient(*Array(radios.size) { i ->
                val p = Offset(s.llama.x - radios[i] * U, s.llama.y)
                val base = a.luz(P.AMARILLO, s.k(a, p))
                (radios[i] / 480f) to (if (m != null) a.sol(P.AMARILLO, base, m.fuerza) else base)
            }, center = s.llama, radius = tope), r.topLeft, r.size)
            return
        }
        if (m != null) {
            drawRect(Brush.verticalGradient(listOf(a.en(P.AMARILLO, 0f, 1f, 1f, m), a.en(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.25f), 0f, 1f, 1f, m)), m.q[1], m.q[5]), r.topLeft, r.size)
            return
        }
        val claro = a.dia(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.1f), 0f)
        val hondo = a.dia(mezcla(P.AMARILLO, P.AMARILLO_SOMBRA, 0.6f), 0f)
        drawRect(Brush.horizontalGradient(listOf(claro, mezcla(claro, hondo, 0.45f), hondo), 0f, s.w), r.topLeft, r.size)
        drawRect(Brush.verticalGradient(listOf(a.sombra(0.16f), a.sombra(0f)), 0f, 46f * U), Offset.Zero, Size(s.w, 46f * U))
    }

    /** Las manos de pintura y los brochazos, el recuadro de filetes verdes de la casa (detrás del atril) y una grieta. */
    private fun DrawScope.pintura(s: SitioLibretaNapoleon, a: AireNapoleon, r: Rect) {
        val U = s.U; val w = s.w
        val motas = Path()
        for (j in 0 until 14) {
            val mx = w * azar(8350, j); val my = r.bottom * azar(8351, j)
            val mw = (36f + 50f * azar(8352, j)) * U; val mh = mw * (0.3f + 0.3f * azar(8353, j))
            motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
        }
        drawPath(motas, Color(0xFFF2E2C0).copy(alpha = 0.34f), blendMode = BlendMode.Multiply)
        val brochazos = Path()
        for (j in 0 until 30) {
            val bx = w * azar(8360, j); val by = r.bottom * azar(8361, j)
            val bl = (22f + 36f * azar(8362, j)) * U
            brochazos.moveTo(bx, by); brochazos.quadraticTo(bx + bl * 0.5f, by + 2f * U, bx + bl * 0.9f, by + 7f * U)
        }
        drawPath(brochazos, Color(0xFFEEDDBA).copy(alpha = 0.5f), style = Stroke(2f * U, cap = StrokeCap.Round), blendMode = BlendMode.Multiply)
        // El recuadro pintado: un filete verde ancho y otro fino dentro, con las esquinas en cuarto de círculo hacia dentro.
        val verde = Color(0xFF5C7A52)
        val rec = Rect(s.cx - 156f * U, 9f * U, s.cx + 160f * U, s.mesa + 30f * U)
        listOf(0f to 2.6f, 5.5f to 0.9f).forEach { (d, g) ->
            val q = Rect(rec.left + d * U, rec.top + d * U, rec.right - d * U, rec.bottom - d * U)
            val rr = 11f * U
            val p = Path().apply {
                moveTo(q.left + rr, q.top); lineTo(q.right - rr, q.top)
                arcTo(Rect(q.right - rr, q.top - rr, q.right + rr, q.top + rr), 180f, -90f, false)
                lineTo(q.right, q.bottom)
                moveTo(q.left, q.bottom); lineTo(q.left, q.top + rr)
                arcTo(Rect(q.left - rr, q.top - rr, q.left + rr, q.top + rr), 90f, -90f, false)
            }
            drawPath(p, verde.copy(alpha = 0.8f), style = Stroke(g * U), blendMode = BlendMode.Multiply)
        }
        // Una grieta que baja desde el recuadro, a la derecha.
        val grieta = Path()
        var p = Offset(s.cx + 170f * U, 26f * U)
        grieta.moveTo(p.x, p.y)
        for (j in 0 until 8) { p += Offset((2f + 4f * azar(8370, j)) * U * (if (j % 3 == 1) -1f else 1f), (6f + 5f * azar(8371, j)) * U); grieta.lineTo(p.x, p.y) }
        drawPath(grieta, Color(0xFF7A5A2C).copy(alpha = 0.55f), style = Stroke(0.8f * U), blendMode = BlendMode.Multiply)
    }

    /** La silueta del atril (la tabla y el puntal) corrida [d]: para su sombra en la pared. */
    private fun siluetaAtril(s: SitioLibretaNapoleon, d: Offset): Path {
        val U = s.U
        return poligonoNapoleon(
            s.atrilIzq - 3f * U + d.x, s.atrilArriba - 4f * U + d.y, s.atrilDer + 3f * U + d.x, s.atrilArriba - 4f * U + d.y,
            s.atrilDer + 30f * U + d.x, s.pie + d.y, s.atrilIzq + d.x, s.pie + d.y,
        )
    }

    /** La sombra del atril en la pared: de día, a la derecha y abajo (el sol entra por la izquierda); de noche, la vela la echa a la izquierda. */
    private fun DrawScope.sombraDelAtril(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        clipRect(0f, 0f, s.w, s.mesa) {
            if (!a.oscuro) {
                drawPath(siluetaAtril(s, Offset(22f * U, 10f * U)), a.sombra(0.1f))
                drawPath(siluetaAtril(s, Offset(16f * U, 7f * U)), a.sombra(0.12f))
            } else {
                drawPath(siluetaAtril(s, Offset(-30f * U, -14f * U)), a.sombra(0.28f))
            }
        }
    }

    // ---------------------------------------------------------------- la cortina

    /**
     * La cortina de damasco granate, a la izquierda, recogida con su alzapaño de cordón dorado y su borla: de la barra de latón
     * baja ancha, se estrecha en el alzapaño y se abre otra vez hasta perderse detrás de la mesa. Los pliegues, en tiras que
     * convergen en el alzapaño; de día los alumbra la ventana (está a su lado); de noche, la luna le pone un filo frío.
     */
    private fun DrawScope.cortina(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val ancho = s.cortina
        if (ancho < 8f * U) return
        val yA = 128f * U
        val arriba = 9f * U; val abajo = s.mesa + 4f * U
        // El ancho a cada alto: entero arriba, más de la mitad en el alzapaño, casi entero abajo.
        fun borde(y: Float): Float = if (y <= yA) {
            val t = ((y - arriba) / (yA - arriba)).coerceIn(0f, 1f)
            ancho * (1f - 0.42f * t * t)
        } else {
            val t = ((y - yA) / (abajo - yA)).coerceIn(0f, 1f)
            ancho * (0.58f + 0.36f * Math.sqrt(t.toDouble()).toFloat())
        }
        val tiras = 28
        val pliegues = 3.4f
        val filas = floatArrayOf(arriba, arriba + (yA - arriba) * 0.5f, yA, yA + (abajo - yA) * 0.5f, abajo)
        for (i in 0 until tiras) {
            val u0 = i / tiras.toFloat(); val u1 = (i + 1) / tiras.toFloat()
            val p = Path()
            p.moveTo(-2f * U, filas[0])
            filas.forEachIndexed { j, y -> val x = -2f * U + (borde(y) + 2f * U) * u1; if (j == 0) p.moveTo(x, y) else p.lineTo(x, y) }
            for (j in filas.indices.reversed()) { val y = filas[j]; p.lineTo(-2f * U + (borde(y) + 2f * U) * u0 - 0.6f, y) }
            p.close()
            // El valor de cada tira: el coseno de los pliegues, con la luz que viene de la ventana (la izquierda).
            fun v(u: Float) = 0.5f + 0.5f * kotlin.math.cos(2f * Math.PI.toFloat() * pliegues * u + 0.6f)
            val luz = if (a.oscuro) 0.25f else 0.85f - 0.3f * u0
            val c0 = mezcla(DAMASCO_OSC, mezcla(DAMASCO, DAMASCO_LUZ, v(u0) * luz), 0.35f + 0.65f * v(u0))
            val c1 = mezcla(DAMASCO_OSC, mezcla(DAMASCO, DAMASCO_LUZ, v(u1) * luz), 0.35f + 0.65f * v(u1))
            val y0 = filas[2]
            val x0 = -2f * U + (borde(y0) + 2f * U) * u0; val x1 = -2f * U + (borde(y0) + 2f * U) * u1
            val k0 = s.k(a, Offset(x0, y0))
            drawPath(p, Brush.horizontalGradient(listOf(a.luz(c0, k0), a.luz(c1, k0)), x0, maxOf(x1, x0 + 0.5f)))
        }
        // La sombra que la cortina echa en la pared, a su derecha (de día).
        if (!a.oscuro) {
            val sp = Path().apply {
                moveTo(ancho, arriba); quadraticTo(ancho * 0.62f + 4f * U, yA * 0.75f, ancho * 0.58f + 3f * U, yA)
                quadraticTo(ancho * 0.7f + 4f * U, yA + 40f * U, ancho * 0.94f + 4f * U, abajo)
                lineTo(ancho * 0.94f + 12f * U, abajo); quadraticTo(ancho * 0.75f + 12f * U, yA + 30f * U, ancho * 0.6f + 9f * U, yA)
                quadraticTo(ancho * 0.72f + 10f * U, yA * 0.6f, ancho + 10f * U, arriba); close()
            }
            drawPath(sp, a.sombra(0.14f))
        }
        // El galón dorado del canto de delante y el filo de la luna.
        val canto = Path()
        var y = arriba
        canto.moveTo(borde(y) - 1.2f * U, y)
        while (y < abajo) { y += 6f * U; canto.lineTo(borde(y) - 1.2f * U, y) }
        drawPath(canto, a.luz(P.OCRE, s.k(a, Offset(ancho, yA))), style = Stroke(1.6f * U))
        if (a.oscuro) drawPath(canto, a.lunar(0.25f), style = Stroke(1f * U))
        // La barra de latón con sus anillas y el pomo.
        val kb = s.k(a, Offset(ancho, 6f * U))
        drawRect(a.sombra(0.3f), Offset(-2f * U, 8f * U), Size(ancho + 14f * U, 2.4f * U))
        drawRect(a.luz(P.LATON, kb), Offset(-2f * U, 4.5f * U), Size(ancho + 12f * U, 3.6f * U))
        drawRect(a.brillo(0.5f, kb), Offset(-2f * U, 4.9f * U), Size(ancho + 12f * U, 0.9f * U))
        for (j in 0 until 4) {
            val x = ancho * (0.12f + 0.27f * j)
            drawCircle(a.luz(P.LATON_OSC, kb), 3.2f * U, Offset(x, 6.3f * U), style = Stroke(1.2f * U))
        }
        val pomo = Offset(ancho + 12f * U, 6.3f * U)
        drawCircle(a.luz(P.LATON, kb), 4.6f * U, pomo)
        drawCircle(a.luz(P.LATON_OSC, kb), 4.6f * U, pomo, style = Stroke(0.8f * U))
        drawCircle(a.brillo(0.6f, kb), 1.5f * U, pomo - Offset(1.4f * U, 1.4f * U))
        // El alzapaño: el cordón que ciñe la cortina y la borla que cuelga.
        val ka = s.k(a, Offset(ancho * 0.6f, yA))
        val oro = a.luz(P.OCRE, ka)
        val bx = borde(yA)
        drawLine(a.sombra(0.4f), Offset(-2f * U, yA + 2.6f * U), Offset(bx + 1f * U, yA + 1.6f * U), 3.4f * U)
        drawLine(oro, Offset(-2f * U, yA + 0.8f * U), Offset(bx + 1f * U, yA), 3f * U)
        for (j in 0 until 6) {
            val x = bx * (j + 0.5f) / 6f
            drawLine(a.luz(P.LATON_OSC, ka), Offset(x - 1.2f * U, yA - 1f * U), Offset(x + 1.2f * U, yA + 2f * U), 0.7f * U)
        }
        val b = Offset(bx - 3f * U, yA + 4f * U)
        drawLine(oro, Offset(bx - 1f * U, yA + 1f * U), b + Offset(0f, 8f * U), 1.2f * U)
        drawCircle(oro, 3.2f * U, b + Offset(0f, 10f * U))
        val falda = Path().apply {
            moveTo(b.x - 3f * U, b.y + 12f * U); lineTo(b.x + 3f * U, b.y + 12f * U); lineTo(b.x + 5f * U, b.y + 28f * U); lineTo(b.x - 5f * U, b.y + 28f * U); close()
        }
        drawPath(falda, oro)
        clipPath(falda) {
            for (j in 0 until 5) drawLine(a.luz(P.LATON_OSC, ka), Offset(b.x - 4f * U + 2f * U * j, b.y + 13f * U), Offset(b.x - 5f * U + 2.5f * U * j, b.y + 28f * U), 0.6f * U)
            drawRect(a.sombra(0.25f), Offset(b.x + 1f * U, b.y + 12f * U), Size(6f * U, 16f * U))
        }
    }

    // ---------------------------------------------------------------- la mesa

    /**
     * La mesa de escribir: el tablero con su cuero verde y su filete dorado, el canto con su baqueta de latón y el faldón de caoba
     * flameada con el cajón, su bocallave y sus dos tiradores de anilla.
     */
    private fun DrawScope.mesa(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U; val w = s.w
        val ym = (s.mesa + s.canto) / 2
        // El tablero: el borde de caoba de atrás y el cuero verde.
        drawRect(s.aLoAncho(a, P.CAOBA, -0.1f, 0f, w, ym), Offset(0f, s.mesa), Size(w, s.canto - s.mesa))
        drawRect(s.aLoAncho(a, CUERO_VERDE, 0.05f, 0f, w, ym), Offset(0f, s.mesa + 2.2f * U), Size(w, s.canto - s.mesa - 4.4f * U))
        drawRect(a.luz(P.LATON_OSC, 0.5f).copy(alpha = 0.7f), Offset(0f, s.canto - 4.4f * U), Size(w, 0.7f * U))
        if (!a.oscuro) drawRect(Brush.horizontalGradient(listOf(a.claro(0.18f), a.claro(0f)), 0f, s.cx), Offset(0f, s.mesa + 2.2f * U), Size(s.cx, s.canto - s.mesa - 4.4f * U))
        // La baqueta de latón del canto.
        drawRect(s.aLoAncho(a, P.LATON, 0f, 0f, w, s.canto), Offset(0f, s.canto), Size(w, 3.6f * U))
        drawRect(if (a.oscuro) s.aLoAncho(a, P.LATON_LUZ, 0f, 0f, w, s.canto) else Brush.horizontalGradient(listOf(a.brillo(0.6f, 1f), a.brillo(0.6f, 1f))), Offset(0f, s.canto), Size(w, 1f * U))
        // El faldón de caoba flameada.
        val f = Rect(0f, s.canto + 3.6f * U, w, s.h)
        drawRect(s.aLoAncho(a, P.CAOBA, -0.12f, 0f, w, f.center.y), f.topLeft, f.size)
        // La veta de la chapa, a lo largo: hebras largas que ondulan.
        val veta = Path()
        for (j in 0 until 7) {
            val y = f.top + (5f + 5.4f * j) * U
            var x = -10f * U
            veta.moveTo(x, y)
            while (x < w + 10f * U) {
                val paso = (40f + 30f * azar(8380 + j, (x / U).toInt() and 63)) * U
                veta.quadraticTo(x + paso * 0.5f, y + (if (((x / paso).toInt() + j) % 2 == 0) 2.2f else -2.2f) * U, x + paso, y)
                x += paso
            }
        }
        drawPath(veta, Color(0xFFC89080).copy(alpha = 0.5f), style = Stroke(1.1f * U), blendMode = BlendMode.Multiply)
        drawRect(a.sombra(0.35f), f.topLeft, Size(w, 2.5f * U))
        // El cajón: su frente con la moldura, la bocallave y los tiradores.
        val c = Rect(s.cx - 72f * U, f.top + 8f * U, s.cx + 72f * U, f.top + 34f * U)
        val kc = s.k(a, c.center)
        drawRect(a.sombra(0.4f), c.topLeft, c.size, style = Stroke(1.6f * U))
        drawLine(a.dia(P.CAOBA_LUZ, 0f, kc), Offset(c.left + 1f * U, c.bottom + 0.8f * U), Offset(c.right + 1f * U, c.bottom + 0.8f * U), 1f * U)
        drawRect(a.luz(P.LATON, kc).copy(alpha = 0.8f), c.deflate(3f * U).topLeft, c.deflate(3f * U).size, style = Stroke(0.7f * U))
        val bl = Offset(c.center.x, c.center.y)
        drawPath(poligonoNapoleon(bl.x - 4.5f * U, bl.y - 6f * U, bl.x + 4.5f * U, bl.y - 6f * U, bl.x + 5.5f * U, bl.y, bl.x, bl.y + 7f * U, bl.x - 5.5f * U, bl.y), a.luz(P.LATON, kc))
        drawCircle(P.CAOBA_HONDO, 1.3f * U, bl + Offset(0f, -1.4f * U))
        drawLine(P.CAOBA_HONDO, bl, bl + Offset(0f, 3f * U), 1f * U)
        listOf(-46f, 46f).forEach { dx ->
            val t = Offset(c.center.x + dx * U, c.center.y)
            val kt = s.k(a, t)
            drawCircle(a.luz(P.LATON, kt), 2.4f * U, t + Offset(0f, -2f * U))
            drawCircle(a.sombra(0.35f), 5.2f * U, t + Offset(0.8f * U, 4.6f * U), style = Stroke(1.4f * U))
            drawCircle(a.luz(P.LATON, kt), 5.2f * U, t + Offset(0f, 3.8f * U), style = Stroke(1.4f * U))
            drawCircle(a.brillo(0.5f, kt), 0.9f * U, t + Offset(-1f * U, -2.8f * U))
        }
    }

    // ---------------------------------------------------------------- el atril

    /**
     * El atril de sobremesa: detrás, a la derecha, el puntal en la cremallera de latón; la tabla de caoba con su remate en arco
     * rebajado y su junquillo de latón, que asoma por encima del legajo; el grueso de la tabla por la derecha; abajo, el listón con
     * su labio de latón (donde apoya el legajo) y el zócalo con sus patas de bollo. La trama de la ventana cae en su lado
     * izquierdo, corrida.
     */
    private fun DrawScope.atril(s: SitioLibretaNapoleon, a: AireNapoleon, m: ManchaNapoleon) {
        val U = s.U
        val kd = s.k(a, Offset(s.atrilDer + 20f * U, 150f * U))
        // El puntal y la cremallera.
        drawPath(poligonoNapoleon(s.atrilDer - 10f * U, 66f * U, s.atrilDer - 2f * U, 64f * U, s.atrilDer + 27f * U, s.pie - 1f * U, s.atrilDer + 18f * U, s.pie),
            a.dia(P.CAOBA_OSC, 0f, kd))
        drawLine(a.dia(P.CAOBA, 0f, kd), Offset(s.atrilDer - 2f * U, 65f * U), Offset(s.atrilDer + 27f * U, s.pie - 1f * U), 1.2f * U)
        drawRect(a.dia(P.LATON_OSC, 0f, kd), Offset(s.atrilDer - 4f * U, s.pie - 3f * U), Size(38f * U, 3f * U))
        for (j in 0 until 6) drawLine(a.dia(P.LATON, 0f, kd), Offset(s.atrilDer + (j * 6f - 2f) * U, s.pie - 3f * U), Offset(s.atrilDer + (j * 6f + 1f) * U, s.pie - 5.4f * U), 1f * U)
        // El grueso de la tabla por la derecha.
        drawPath(poligonoNapoleon(s.atrilDer, s.atrilArriba + 8f * U, s.atrilDer + s.tabla, s.atrilArriba + 12f * U, s.atrilDer + s.tabla, s.listonAbajo + 2f * U, s.atrilDer, s.listonAbajo),
            a.dia(P.CAOBA_OSC, -0.1f, kd))
        tabla(s, a, null)
        clipPath(m.forma(1f, -10f * U, 0.014f)) { tabla(s, a, ManchaNapoleon(m.q, m.color, m.fuerza * 0.42f)) }
        clipPath(m.forma(1f, -10f * U)) { tabla(s, a, m) }
        if (a.oscuro) {
            drawLine(a.lunar(0.3f), Offset(s.atrilIzq + 0.6f * U, s.atrilArriba + 12f * U), Offset(s.atrilIzq + 0.6f * U, s.liston - 4f * U), 1f * U)
            drawLine(a.filo(0.6f * kd), Offset(s.atrilDer + s.tabla - 0.6f * U, s.atrilArriba + 30f * U), Offset(s.atrilDer + s.tabla - 0.6f * U, s.listonAbajo), 1.2f * U)
        }
        // El listón: el labio de latón arriba y la cara de caoba con su filete.
        val l = Rect(s.atrilIzq - 6f * U, s.liston - 3f * U, s.atrilDer + 6f * U, s.listonAbajo)
        drawRect(a.sombra(0.4f), Offset(l.left, l.bottom), Size(l.width, 2f * U))
        drawRect(s.aLoAncho(a, P.CAOBA, -0.05f, l.left, l.right, l.center.y), Offset(l.left, l.top + 3.4f * U), Size(l.width, l.height - 3.4f * U))
        drawRect(s.aLoAncho(a, P.LATON, 0f, l.left, l.right, l.top), l.topLeft, Size(l.width, 3.4f * U))
        drawRect(if (a.oscuro) s.aLoAncho(a, P.LATON_LUZ, 0f, l.left, l.right, l.top) else Brush.horizontalGradient(listOf(a.brillo(0.7f, 1f), a.brillo(0.2f, 1f)), l.left, l.right),
            l.topLeft, Size(l.width, 1.1f * U))
        drawRect(s.aLoAncho(a, P.LATON_OSC, 0f, l.left, l.right, l.bottom), Offset(l.left + 3f * U, l.bottom - 4f * U), Size(l.width - 6f * U, 0.8f * U))
        drawPath(poligonoNapoleon(l.right, l.top, l.right + 3f * U, l.top + 1.6f * U, l.right + 3f * U, l.bottom + 1f * U, l.right, l.bottom), a.dia(P.CAOBA_OSC, 0f, kd))
        // El zócalo y las patas de bollo.
        val z = Rect(s.atrilIzq + 8f * U, s.listonAbajo, s.atrilDer - 8f * U, s.pie - 4f * U)
        drawRect(s.aLoAncho(a, P.CAOBA_OSC, 0f, z.left, z.right, z.center.y), z.topLeft, z.size)
        listOf(s.atrilIzq + 16f * U, s.atrilDer - 16f * U).forEach { x ->
            val kp = s.k(a, Offset(x, s.pie))
            drawOval(a.luz(P.LATON_OSC, kp), Offset(x - 6f * U, s.pie - 6f * U), Size(12f * U, 6.5f * U))
            drawOval(a.luz(P.LATON, kp), Offset(x - 5f * U, s.pie - 6f * U), Size(8f * U, 4f * U))
        }
    }

    /** La silueta de la tabla del atril: los costados rectos y arriba el arco rebajado, desplazada [d] hacia dentro. */
    private fun tablaSilueta(s: SitioLibretaNapoleon, d: Float): Path {
        val U = s.U
        return Path().apply {
            moveTo(s.atrilIzq + d, s.liston + 2f * U)
            lineTo(s.atrilIzq + d, s.atrilArriba + 12f * U + d)
            quadraticTo(s.cx, s.atrilArriba - 12f * U + d, s.atrilDer - d, s.atrilArriba + 12f * U + d)
            lineTo(s.atrilDer - d, s.liston + 2f * U)
            close()
        }
    }

    /** La tabla del atril (la caoba con su veta de pluma y su junquillo de latón), con la luz del momento; con [m], dentro de la trama de la ventana. */
    private fun DrawScope.tabla(s: SitioLibretaNapoleon, a: AireNapoleon, m: ManchaNapoleon?) {
        val U = s.U
        val y = (s.atrilArriba + s.liston) / 2
        fun pincel(col: Color, f: Float): Brush {
            if (m == null) return s.aLoAncho(a, col, f, s.atrilIzq, s.atrilDer, y)
            val c = a.en(col, f, 1f, 1f, m)
            return Brush.horizontalGradient(listOf(c, c), s.atrilIzq, s.atrilDer)
        }
        val silueta = tablaSilueta(s, 0f)
        drawPath(silueta, pincel(P.CAOBA, 0f))
        // La veta de pluma de la chapa, que sube por el medio y se abre en el arco.
        val pluma = Path()
        for (j in 0 until 6) {
            val d = (14f + 15f * j) * U
            pluma.moveTo(s.cx - d, s.atrilArriba + (26f + 6f * j) * U)
            pluma.quadraticTo(s.cx - d * 0.3f, s.atrilArriba + (2f + 2f * j) * U, s.cx, s.atrilArriba + (12f + 4f * j) * U)
            pluma.quadraticTo(s.cx + d * 0.3f, s.atrilArriba + (2f + 2f * j) * U, s.cx + d, s.atrilArriba + (26f + 6f * j) * U)
        }
        clipPath(silueta) { drawPath(pluma, Color(0xFFC88A78).copy(alpha = 0.55f), style = Stroke(1.4f * U), blendMode = BlendMode.Multiply) }
        // El canto del arco: la cara de arriba clara (mira a la luz) y el junquillo de latón por dentro.
        drawPath(silueta, pincel(P.CAOBA_LUZ, 0.05f), style = Stroke(2f * U))
        drawPath(tablaSilueta(s, 4f * U), pincel(P.LATON, 0f), style = Stroke(1f * U))
        if (m == null && !a.oscuro) drawRect(a.claro(0.2f), Offset(s.atrilIzq, s.atrilArriba + 12f * U), Size(1.4f * U, s.liston - s.atrilArriba - 12f * U))
    }

    // ---------------------------------------------------------------- el tintero y la palmatoria

    /** El tintero de bronce: la bandeja con sus patas, el pocillo de vidrio tallado con la tinta, la salvadera y la pluma de ganso. */
    private fun DrawScope.tintero(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val c = s.tintero
        val k = s.k(a, c)
        val dx = if (a.oscuro) -1f else 1f
        // La sombra en el tablero.
        drawOval(a.sombra(0.35f), Offset(c.x - 26f * U + 6f * U * dx, c.y - 2f * U), Size(60f * U, 8f * U))
        // La pluma (detrás de todo): el cañón sale del pocillo y la barba se abre hacia arriba a la izquierda.
        val base = Offset(c.x - 11f * U, c.y - 17f * U)
        val punta = Offset(c.x - 34f * U, c.y - 92f * U)
        val ctrl = Offset(base.x - 2f * U, base.y - 44f * U)
        fun eje(t: Float) = Offset((1 - t) * (1 - t) * base.x + 2 * (1 - t) * t * ctrl.x + t * t * punta.x, (1 - t) * (1 - t) * base.y + 2 * (1 - t) * t * ctrl.y + t * t * punta.y)
        // La barba: angosta a la izquierda del cañón, ancha a la derecha, y se afila en la punta (con dos mellas).
        val izq = ArrayList<Offset>(); val der = ArrayList<Offset>()
        for (j in 0..10) {
            val t = 0.22f + 0.78f * j / 10f
            val p = eje(t); val q = eje((t + 0.02f).coerceAtMost(1f)); val r = eje((t - 0.02f).coerceAtLeast(0f))
            val dx = q.x - r.x; val dy = q.y - r.y; val n = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(1e-3f)
            val nx = -dy / n; val ny = dx / n
            val f = kotlin.math.sin(Math.PI.toFloat() * ((t - 0.22f) / 0.78f).coerceIn(0f, 1f)).let { kotlin.math.sqrt(it) }
            val mella = if (j == 4 || j == 7) 0.55f else 1f
            der.add(Offset(p.x - nx * 7f * U * f * mella, p.y - ny * 7f * U * f * mella))
            izq.add(Offset(p.x + nx * 2.6f * U * f, p.y + ny * 2.6f * U * f))
        }
        val pluma = Path().apply {
            moveTo(izq[0].x, izq[0].y)
            izq.forEach { lineTo(it.x, it.y) }
            der.reversed().forEach { lineTo(it.x, it.y) }
            close()
        }
        drawPath(pluma, a.luz(P.PAPEL, k))
        clipPath(pluma) { drawPath(Path().apply { der.forEachIndexed { j, p -> if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }, a.sombra(0.22f), style = Stroke(4f * U)) }
        drawPath(Path().apply { moveTo(base.x, base.y); quadraticTo(ctrl.x, ctrl.y, punta.x, punta.y) }, a.luz(P.CARTULINA_OSC, k), style = Stroke(1f * U))
        if (a.oscuro) drawPath(Path().apply { izq.forEachIndexed { j, p -> if (j == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) } }, a.lunar(0.35f), style = Stroke(0.9f * U))
        // La bandeja y sus patas.
        val b = Rect(c.x - 27f * U, c.y - 4f * U, c.x + 27f * U, c.y + 3f * U)
        listOf(b.left + 5f * U, b.right - 5f * U).forEach { x -> drawCircle(a.luz(P.LATON_OSC, k), 2.2f * U, Offset(x, b.bottom + 0.8f * U)) }
        drawRoundRect(a.luz(P.LATON_OSC, k), b.topLeft, b.size, androidx.compose.ui.geometry.CornerRadius(3.5f * U))
        drawRoundRect(a.luz(P.LATON, k), b.topLeft, Size(b.width, 3.4f * U), androidx.compose.ui.geometry.CornerRadius(1.7f * U))
        drawLine(a.brillo(0.6f, k), Offset(b.left + 3f * U, b.top + 0.8f * U), Offset(b.right - 3f * U, b.top + 0.8f * U), 0.8f * U)
        // El pocillo de vidrio tallado con su collar de latón.
        val v = Rect(c.x - 19f * U, c.y - 19f * U, c.x - 3f * U, c.y - 2f * U)
        drawRect(a.luz(Color(0xFF1E2A34), k), v.topLeft, v.size)
        drawRect(a.luz(Color(0xFF5C7486), k), Offset(v.left + 3f * U, v.top + 3f * U), Size(2.4f * U, v.height - 5f * U))
        drawRect(a.luz(Color(0xFF3C5060), k), Offset(v.left + 9f * U, v.top + 3f * U), Size(2f * U, v.height - 5f * U))
        if (!a.oscuro) drawRect(a.claro(0.5f), Offset(v.left + 1f * U, v.top + 2f * U), Size(1.2f * U, v.height - 4f * U))
        drawRect(a.luz(P.LATON, k), Offset(v.left - 1f * U, v.top - 3f * U), Size(v.width + 2f * U, 3.4f * U))
        // La salvadera: el cuerpo de bronce y su copa agujereada.
        val sv = Rect(c.x + 3f * U, c.y - 15f * U, c.x + 19f * U, c.y - 2f * U)
        drawRect(a.luz(P.LATON_OSC, k), sv.topLeft, sv.size)
        drawRect(a.luz(P.LATON, k), sv.topLeft, Size(6f * U, sv.height))
        drawArc(a.luz(P.LATON, k), 180f, 180f, true, Offset(sv.left, sv.top - 6f * U), Size(sv.width, 12f * U))
        for (j in 0 until 4) drawCircle(a.luz(P.LATON_OSC, k), 0.8f * U, Offset(sv.left + (3.5f + 3f * j) * U, sv.top - 2.6f * U - (if (j == 1 || j == 2) 1.4f * U else 0f)))
        drawCircle(a.brillo(0.6f, k), 1f * U, Offset(sv.left + 4f * U, sv.top - 3.4f * U))
    }

    /**
     * La palmatoria de lacrar: el platillo de latón con su anilla, el cañón y la vela de cera, gastada y con su goterón; de noche,
     * encendida (la llama y su halo; el charco va pintado en la pared, la mesa y el legajo). Delante, la barra de lacre.
     */
    private fun DrawScope.palmatoria(s: SitioLibretaNapoleon, a: AireNapoleon) {
        val U = s.U
        val c = s.palmatoria
        val k = s.k(a, c)
        // La sombra en el tablero (de día, a la derecha; de noche, poca, bajo el platillo).
        if (!a.oscuro) drawPath(poligonoNapoleon(c.x - 3f * U, c.y - 2f * U, c.x + 3f * U, c.y - 2f * U, c.x + 40f * U, c.y + 1f * U, c.x + 36f * U, c.y + 3f * U), a.sombra(0.22f))
        drawOval(a.sombra(if (a.oscuro) 0.5f else 0.35f), Offset(c.x - 16f * U + (if (a.oscuro) 0f else 3f * U), c.y - 2f * U), Size(34f * U, 8f * U))
        // La anilla y el platillo.
        drawCircle(a.luz(P.LATON_OSC, k), 4.6f * U, Offset(c.x + 17f * U, c.y - 1.6f * U), style = Stroke(1.6f * U))
        drawOval(a.luz(P.LATON_OSC, k), Offset(c.x - 15f * U, c.y - 4.6f * U), Size(30f * U, 9f * U))
        drawOval(a.luz(P.LATON, k), Offset(c.x - 15f * U, c.y - 5.6f * U), Size(30f * U, 8f * U))
        drawOval(a.luz(P.LATON_OSC, k), Offset(c.x - 10f * U, c.y - 4f * U), Size(20f * U, 5f * U))
        if (a.oscuro) drawArc(a.filo(0.7f), 180f, 180f, false, Offset(c.x - 15f * U, c.y - 5.6f * U), Size(30f * U, 8f * U), style = Stroke(1f * U))
        else drawArc(a.brillo(0.6f, 1f), 200f, 90f, false, Offset(c.x - 15f * U, c.y - 5.6f * U), Size(30f * U, 8f * U), style = Stroke(1f * U))
        // El cañón de latón.
        drawRect(a.luz(P.LATON, k), Offset(c.x - 4.5f * U, c.y - 13f * U), Size(9f * U, 11f * U))
        drawRect(a.luz(P.LATON_OSC, k), Offset(c.x + 1.5f * U, c.y - 13f * U), Size(3f * U, 11f * U))
        drawRect(a.luz(P.LATON_LUZ, k), Offset(c.x - 5.5f * U, c.y - 14.5f * U), Size(11f * U, 2.4f * U))
        // La vela, gastada: la cera alumbrada por su propia llama de noche, con el goterón.
        val arriba = s.llama.y + 7f * U
        val cera = if (a.oscuro) mezcla(CERA, P.LLAMA, 0.25f) else CERA
        drawRect(a.luz(cera, k), Offset(c.x - 3.4f * U, arriba), Size(6.8f * U, c.y - 14f * U - arriba))
        drawRect(if (a.oscuro) a.sombra(0.2f) else a.sombra(0.14f), Offset(c.x + 1.2f * U, arriba), Size(2.2f * U, c.y - 14f * U - arriba))
        drawRoundRect(a.luz(cera, k), Offset(c.x - 4.6f * U, arriba + 4f * U), Size(2.4f * U, 12f * U), androidx.compose.ui.geometry.CornerRadius(1.2f * U))
        drawOval(a.luz(mezcla(cera, Color.White, 0.3f), k), Offset(c.x - 3.4f * U, arriba - 1.2f * U), Size(6.8f * U, 2.6f * U))
        // La mecha y, de noche, la llama con su halo.
        drawLine(Color(0xFF1A120C), Offset(c.x, arriba), Offset(c.x + 0.4f * U, arriba - 3.4f * U), 1f * U)
        if (a.oscuro) {
            val f = s.llama
            drawCircle(Brush.radialGradient(listOf(P.LLAMA.copy(alpha = 0.42f), P.LLAMA.copy(alpha = 0.12f), P.LLAMA.copy(alpha = 0f)), f, 30f * U), 30f * U, f)
            val l = Path().apply {
                moveTo(f.x, f.y - 9f * U); quadraticTo(f.x + 4.4f * U, f.y + 1f * U, f.x, f.y + 4.5f * U)
                quadraticTo(f.x - 4.4f * U, f.y + 1f * U, f.x, f.y - 9f * U); close()
            }
            drawPath(l, P.LLAMA)
            drawOval(P.FUEGO, Offset(f.x - 1.8f * U, f.y - 3f * U), Size(3.6f * U, 6.4f * U))
            drawOval(Color(0xFF6E86C0).copy(alpha = 0.6f), Offset(f.x - 1.4f * U, f.y + 2f * U), Size(2.8f * U, 2.6f * U))
        }
        // La barra de lacre, delante, tendida en el tablero.
        val kl = s.k(a, Offset(c.x - 24f * U, s.canto - 3f * U))
        val y0 = s.canto - 3.5f * U
        drawLine(a.sombra(0.35f), Offset(c.x - 36f * U, y0 + 2f * U), Offset(c.x - 8f * U, y0 + 1f * U), 3.2f * U)
        drawLine(a.luz(P.LACRE, kl), Offset(c.x - 38f * U, y0), Offset(c.x - 10f * U, y0 - 1.4f * U), 3.4f * U)
        drawLine(a.luz(mezcla(P.LACRE, Color.White, 0.3f), kl), Offset(c.x - 37f * U, y0 - 1.2f * U), Offset(c.x - 11f * U, y0 - 2.6f * U), 0.8f * U)
        drawLine(a.luz(Color(0xFF3A1210), kl), Offset(c.x - 12f * U, y0 - 1.3f * U), Offset(c.x - 9f * U, y0 - 1.5f * U), 3.4f * U)
    }
}
