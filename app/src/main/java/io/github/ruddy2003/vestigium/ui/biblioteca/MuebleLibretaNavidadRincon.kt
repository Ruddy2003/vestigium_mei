package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo fijo del rincón de la libreta de Navidad ([MuebleLibretaNavidad]), pintado una vez por tamaño y modo: la pared de la cocina
 * (la pintura verdosa, la cenefa de azulejo verde y el azulejo crema) con la mancha del sol o, de noche, el charco de la vela; la
 * ventana con su escarcha y la nieve; la guirnalda de rodajas de naranja seca; la mesa de pino con su canto y su faldón; el cuerpo
 * de la cajonera de adviento (el copete recortado con su cartela, la cornisa, el marco de los cajoncitos, el zócalo y las patas)
 * con su sombra; y lo de la mesa: los papelitos con el lápiz, la palmatoria con su vela y el plato de mandarinas.
 */
internal object RinconLibretaNavidad {
    private val P = PaletaNavidad

    private fun SitioLibretaNavidad.k(a: AireNavidad, p: Offset) = MuebleLibretaNavidad.cerca(p, this, a)

    fun DrawScope.rincon(s: SitioLibretaNavidad, a: AireNavidad, ac: AireNavidad) {
        val m = MuebleLibretaNavidad.mancha(s, a)
        pared(s, a, null)
        if (m != null) clipRect(s.ventana.right, 0f, s.w, s.mesa) {
            clipPath(m.forma(1f, 0f, 0.016f)) { pared(s, a, ManchaNavidad(m.q, m.color, m.fuerza * 0.42f)) }
            clipPath(m.forma(1f)) { pared(s, a, m) }
        }
        if (a.oscuro) charco(s, a)
        ventana(s, a)
        guirnalda(s, a, m)
        mesa(s, ac)
        sombraCajonera(s, a)
        cuerpo(s, ac, null)
        if (m != null) clipPath(m.forma(1f, ManchaNavidad.FRENTE * s.w * 0.6f)) { cuerpo(s, ac, ManchaNavidad(m.q, m.color, m.fuerza * 0.7f)) }
        papeles(s, ac)
        plato(s, ac)
        val l = LuzNavidad(ac, maxOf(s.k(ac, s.palmatoria), if (a.oscuro) 0.85f else 0f), 0f, null, 0f, if (a.oscuro) -1f else -1f)
        val llama = palmatoriaNavidad(s.palmatoria, 15f * s.U, 46f * s.U, l, 9900)
        if (a.oscuro) llamaNavidad(llama, 11f * s.U)
    }

    // ---------------------------------------------------------------- la pared

    /** La pared de arriba a la mesa: la pintura, la cenefa y el azulejo, a la luz del momento (con [m], dentro de la mancha del sol). */
    private fun DrawScope.pared(s: SitioLibretaNavidad, a: AireNavidad, m: ManchaNavidad?) {
        val U = s.U; val w = s.w
        val lit = if (m != null) 1f else 0f
        // La pintura: más honda bajo el techo; de noche, por columnas, con la luz de la vela.
        val n = if (a.oscuro) 28 else 1
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val cx = (x0 + x1) / 2
            val ys = floatArrayOf(0f, s.cenefa0 * 0.5f, s.cenefa0)
            val cols = Array(3) { i -> (i / 2f) to a.en(mezcla(P.PINTURA, P.PINTURA_SOMBRA, 0.3f * (1f - i / 2f)), 0f, s.k(a, Offset(cx, ys[i])), lit, m) }
            drawRect(Brush.verticalGradient(*cols, startY = 0f, endY = s.cenefa0 + 1f), Offset(x0, 0f), Size(x1 - x0, s.cenefa0 + 1f))
        }
        // Las manos de pintura y el cerco de la humedad.
        if (m == null) {
            val motas = Path()
            for (j in 0 until 12) {
                val mx = w * azar(9910, j); val my = s.cenefa0 * azar(9911, j)
                val mw = w * (0.07f + 0.12f * azar(9912, j)); val mh = mw * (0.3f + 0.3f * azar(9913, j))
                motas.addOval(Rect(mx - mw / 2, my - mh / 2, mx + mw / 2, my + mh / 2))
            }
            drawPath(motas, Color(0xFFE6E2C8).copy(alpha = 0.22f), blendMode = BlendMode.Multiply)
            drawOval(Color(0xFFC9C3A0).copy(alpha = 0.3f), Offset(w * 0.78f, -24f * U), Size(w * 0.3f, 50f * U), blendMode = BlendMode.Multiply)
        }
        // La cenefa de azulejo verde, con su raya clara.
        val lc = 26f * U
        var x = -6f * U
        var j = 0
        while (x < w && j < 60) {
            val k = s.k(a, Offset(x + lc / 2, s.cenefa0))
            drawRect(a.en(mezcla(P.FILETE, P.SALVIA, 0.12f * (j % 3)), 0f, k, lit, m), Offset(x, s.cenefa0), Size(lc - 1.2f * U, s.cenefa1 - s.cenefa0))
            drawRect(a.en(P.PAPEL, 0f, k, lit, m).copy(alpha = 0.5f), Offset(x, s.cenefa0 + (s.cenefa1 - s.cenefa0) * 0.46f), Size(lc - 1.2f * U, 1f * U))
            if (!a.oscuro) drawRect(a.claro(0.45f), Offset(x + 1.5f * U, s.cenefa0 + 1.2f * U), Size(lc * 0.4f, 1f * U))
            x += lc; j++
        }
        drawRect(a.sombra(0.3f), Offset(0f, s.cenefa1), Size(w, 1.3f * U))
        // El azulejo crema, cuadrado, con sus juntas, su vidriado y alguno cuarteado.
        drawRect(a.en(P.JUNTA, 0f, s.k(a, Offset(w * 0.6f, (s.cenefa1 + s.mesa) / 2)), lit, m), Offset(0f, s.cenefa1 + 1.3f * U), Size(w, s.mesa - s.cenefa1))
        val lt = 22f * U
        var yy = s.cenefa1 + 1.3f * U
        var fy = 0
        while (yy < s.mesa && fy < 20) {
            var xx = -lt * 0.4f
            var fx = 0
            while (xx < w && fx < 40) {
                val c = Offset(xx + lt / 2, yy + lt / 2)
                val tono = mezcla(P.AZULEJO, if ((fx + fy) % 3 == 0) P.PINTURA else P.PAPEL, 0.22f * azar(9920 + fy, fx))
                val alto = minOf(lt, s.mesa - yy) - 1f * U
                if (alto > 0f) {
                    drawRect(a.en(tono, 0f, s.k(a, c), lit, m), Offset(xx, yy), Size(lt - 1f * U, alto))
                    if (!a.oscuro) drawRect(a.claro(if (lit > 0f) 0.5f else 0.3f), Offset(xx + lt * 0.12f, yy + lt * 0.12f), Size(lt * 0.34f, 1.1f * U))
                    else if (s.k(a, c) > 0.3f) drawRect(a.brillo(0.35f, s.k(a, c)), Offset(xx + lt * 0.12f, yy + lt * 0.12f), Size(lt * 0.34f, 1f * U))
                    if (azar(9921, fx * 31 + fy) < 0.06f) drawLine(a.sombra(0.14f), Offset(xx + lt * 0.2f, yy + lt * 0.55f), Offset(xx + lt * 0.62f, yy + lt * 0.72f), 0.6f * U)
                }
                xx += lt; fx++
            }
            yy += lt; fy++
        }
        // Lo hondo donde la pared se junta con el tablero.
        drawRect(Brush.verticalGradient(listOf(a.sombra(0f), a.sombra(if (a.oscuro) 0.3f else 0.18f)), s.mesa - 12f * U, s.mesa), Offset(0f, s.mesa - 12f * U), Size(w, 13f * U))
    }

    /** De noche, el charco de la vela en la pared (y la luz fría de la nieve, que entra por la izquierda, en la pintura). */
    private fun DrawScope.charco(s: SitioLibretaNavidad, a: AireNavidad) {
        val l = MuebleLibretaNavidad.llama(s)
        clipRect(0f, 0f, s.w, s.mesa) {
            mancha(l, 150f * s.U, P.LLAMA.copy(alpha = 0.18f))
            mancha(l, 60f * s.U, P.LLAMA.copy(alpha = 0.26f))
        }
        clipRect(s.ventana.right, 0f, s.w, s.mesa) {
            mancha(Offset(s.ventana.right, s.ventana.center.y), 90f * s.U, P.NIEVE_FILO.copy(alpha = 0.12f))
        }
    }

    // ---------------------------------------------------------------- la ventana

    private fun DrawScope.ventana(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val o = s.ventana
        val k = s.k(a, o.center)
        val d = 9f * U; val dt = 5f * U
        // El hueco: el derrame de la derecha (de día, al sol), el dintel en sombra.
        drawRect(a.dia(P.PINTURA_SOMBRA, -0.15f, k), o.topLeft, o.size)
        drawPath(poligonoNavidad(o.right, o.top, o.right, o.bottom, o.right - d, o.bottom - dt * 0.5f, o.right - d, o.top + dt), a.dia(P.PINTURA, if (a.oscuro) 0f else 0.12f, k))
        drawPath(poligonoNavidad(o.left, o.top, o.right, o.top, o.right - d, o.top + dt, o.left, o.top + dt), a.dia(P.PINTURA_SOMBRA, -0.3f, k))
        // El marco crema y la hoja, con su travesaño y el parteluz al borde.
        val g = Rect(o.left, o.top + dt, o.right - d, o.bottom - dt * 0.5f)
        drawRect(a.dia(P.CREMA, 0f, k), g.topLeft, g.size)
        val mf = 6f * U
        val vid = Rect(g.left + mf, g.top + mf, g.right - mf, g.bottom - mf * 1.3f)
        val ym = vid.center.y
        val vidrios = listOf(Rect(vid.left, vid.top, vid.right, ym - mf * 0.4f), Rect(vid.left, ym + mf * 0.4f, vid.right, vid.bottom))
        clipPath(Path().apply { vidrios.forEach { addRect(it) } }) { afuera(vid, s, a) }
        vidrios.forEach { v ->
            escarcha(v, a, U)
            drawRect(a.sombra(0.3f), v.topLeft, Size(v.width, 1.6f * U))
            drawRect(a.sombra(0.22f), Offset(v.right - 1.4f * U, v.top), Size(1.4f * U, v.height))
            drawRect(a.brillo(0.5f, k), Offset(v.left - 1.2f * U, v.top), Size(1.2f * U, v.height))
        }
        drawRect(a.brillo(0.45f, k), g.topLeft, Size(g.width, 1.2f * U))
        // De noche, el filo frío de la nieve en el canto del derrame y del marco.
        if (a.oscuro) {
            drawRect(a.filoFrio(0.6f), Offset(g.right - 1.6f * U, g.top), Size(1.6f * U, g.height))
            drawRect(a.filoFrio(0.45f), Offset(o.right - 1.4f * U, o.top + dt), Size(1.4f * U, o.height - dt))
        }
        // El alféizar de dentro, con su canto y su sombra en la pared.
        val al = Rect(o.left - 4f * U, o.bottom - 3f * U, o.right + 6f * U, o.bottom + 5f * U)
        drawRect(a.sombra(0.3f), Offset(al.left, al.bottom), Size(al.width + 3f * U, 5f * U))
        drawRect(a.dia(P.CREMA, 0.08f, k), al.topLeft, Size(al.width, al.height * 0.4f))
        drawRect(a.dia(P.CREMA, -0.08f, k), Offset(al.left, al.top + al.height * 0.4f), Size(al.width, al.height * 0.6f))
        drawRect(a.brillo(0.5f, k), al.topLeft, Size(al.width, 1f * U))
        with(MuroEstanteNavidad) { saltado(Offset(al.right - 8f * U, al.bottom - 1.5f * U), 3f * U, 9930, a, k) }
        // Un ramito de acebo en un vaso, en el alféizar.
        val vaso = Rect(o.right - 30f * U, al.top - 16f * U, o.right - 20f * U, al.top)
        val lv = LuzNavidad(a, k, 0f, null, 0f, -1f)
        aceboNavidad(Offset(vaso.center.x - 1f * U, vaso.top - 6f * U), 14f * U, -10f, lv)
        aceboNavidad(Offset(vaso.center.x + 3f * U, vaso.top - 2f * U), 11f * U, 50f, lv)
        drawRect(a.dia(P.VIDRIO_PLATA, -0.05f, k).copy(alpha = 0.75f), vaso.topLeft, vaso.size)
        drawRect(a.brillo(0.6f, k), Offset(vaso.left + 1.5f * U, vaso.top + 2f * U), Size(1.2f * U, vaso.height - 4f * U))
        drawRect(a.sombra(0.25f), Offset(vaso.right - 2.5f * U, vaso.top), Size(2.5f * U, vaso.height))
    }

    /** Lo de afuera: el cielo de la tarde de invierno (de noche, el azul de la nieve), la loma nevada, la nieve del alféizar y los copos. */
    private fun DrawScope.afuera(v: Rect, s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val alto: Color; val bajo: Color; val nieve: Color
        if (a.oscuro) { alto = P.CIELO_NOCHE_ALTO; bajo = P.CIELO_NOCHE; nieve = P.NIEVE_NOCHE } else { alto = P.CIELO_ALTO; bajo = P.CIELO; nieve = P.NIEVE }
        drawRect(Brush.verticalGradient(listOf(alto, bajo), v.top, v.bottom), v.topLeft, v.size)
        val loma = Path().apply {
            moveTo(v.left, v.top + v.height * 0.66f)
            cubicTo(v.left + v.width * 0.3f, v.top + v.height * 0.56f, v.left + v.width * 0.7f, v.top + v.height * 0.7f, v.right, v.top + v.height * 0.6f)
            lineTo(v.right, v.bottom); lineTo(v.left, v.bottom); close()
        }
        drawPath(loma, mezcla(nieve, alto, 0.3f))
        val y0 = v.bottom - v.height * 0.12f
        val monton = Path().apply {
            moveTo(v.left, v.bottom); lineTo(v.left, y0)
            cubicTo(v.left + v.width * 0.3f, y0 - v.height * 0.06f, v.left + v.width * 0.6f, y0 + v.height * 0.03f, v.right, y0 - v.height * 0.02f)
            lineTo(v.right, v.bottom); close()
        }
        drawPath(monton, nieve)
        val copos = Path()
        for (j in 0 until 16) copos.addOval(Rect(Offset(v.left + v.width * azar(9940, j), v.top + v.height * 0.85f * azar(9941, j)), (0.7f + 0.9f * azar(9942, j)) * U))
        drawPath(copos, mezcla(nieve, Color.White, 0.4f).copy(alpha = if (a.oscuro) 0.7f else 0.9f))
    }

    /** La escarcha de un vidrio: los helechos blancos que crecen desde las esquinas de abajo. */
    private fun DrawScope.escarcha(v: Rect, a: AireNavidad, U: Float) {
        val p = Path()
        listOf(Offset(v.left, v.bottom) to 1f, Offset(v.right, v.bottom) to -1f).forEachIndexed { e, (c, lado) ->
            for (j in 0 until 5) {
                val an = (-0.2f - 1.2f * azar(9945 + e, j)) * lado
                val dir = Offset(kotlin.math.cos(an) * lado, -kotlin.math.abs(kotlin.math.sin(an)))
                val l = (9f + 12f * azar(9947 + e, j)) * U
                p.moveTo(c.x, c.y); p.lineTo(c.x + dir.x * l, c.y + dir.y * l)
                for (b in 1..3) {
                    val q = c + dir * (l * b / 4f); val bl = l * 0.26f
                    p.moveTo(q.x, q.y); p.lineTo(q.x + (dir.x * 0.5f - dir.y * 0.8f) * bl, q.y + (dir.y * 0.5f + dir.x * 0.8f) * bl)
                    p.moveTo(q.x, q.y); p.lineTo(q.x + (dir.x * 0.5f + dir.y * 0.8f) * bl, q.y + (dir.y * 0.5f - dir.x * 0.8f) * bl)
                }
            }
        }
        val blanco = if (a.oscuro) Color(0xFFB8CCE4).copy(alpha = 0.32f) else Color.White.copy(alpha = 0.62f)
        clipRect(v.left, v.top, v.right, v.bottom) { drawPath(p, blanco, style = Stroke(0.8f * U, cap = StrokeCap.Round)) }
    }

    // ---------------------------------------------------------------- la guirnalda

    /** La guirnalda de rodajas de naranja seca y palitos de canela, ensartadas en un cordel que cruza de la ventana al otro lado. */
    private fun DrawScope.guirnalda(s: SitioLibretaNavidad, a: AireNavidad, m: ManchaNavidad?) {
        val U = s.U; val w = s.w
        val p0 = Offset(s.ventana.right - 6f * U, 6f * U); val p1 = Offset(w + 10f * U, 10f * U)
        val caida = 30f * U
        fun punto(t: Float) = Offset(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t + caida * 4f * t * (1f - t))
        val cordel = Path().apply { val q = punto(0f); moveTo(q.x, q.y); for (i in 1..20) { val r = punto(i / 20f); lineTo(r.x, r.y) } }
        drawPath(corridoNavidad(cordel, 3f * U, 4f * U), a.sombra(0.2f), style = Stroke(1f * U))
        drawPath(cordel, a.dia(P.CARTULINA, -0.15f, s.k(a, punto(0.5f))), style = Stroke(1f * U))
        val n = ((p1.x - p0.x) / (26f * U)).toInt().coerceIn(3, 30)
        for (i in 0 until n) {
            val t = (i + 0.5f) / n
            val c = punto(t) + Offset(0f, 7f * U)
            val k = s.k(a, c)
            val lit = if (m != null && m.toca(c.x, c.y) > 0f) 1f else 0f
            if (i % 4 == 3) {
                // Un atado de palitos de canela, con su lazo de cordel.
                rotate((azar(9950, i) - 0.5f) * 30f, c) {
                    drawRect(a.sombra(0.22f), Offset(c.x - 5f * U + 3f * U, c.y - 2f * U + 4f * U), Size(10f * U, 4f * U))
                    listOf(-1.4f, 0f, 1.4f).forEach { dy -> drawLine(a.en(Color(0xFF8A4E2A), 0f, k, lit, m), Offset(c.x - 6f * U, c.y + dy * U), Offset(c.x + 6f * U, c.y + dy * U), 1.6f * U, StrokeCap.Round) }
                    drawLine(a.claro(0.3f), Offset(c.x - 5f * U, c.y - 2f * U), Offset(c.x + 5f * U, c.y - 2f * U), 0.6f * U)
                    drawLine(a.dia(P.CARTULINA, -0.1f, k), Offset(c.x, c.y - 3f * U), Offset(c.x, c.y + 3f * U), 1f * U)
                }
                continue
            }
            val r = (6.5f + 1.5f * azar(9951, i)) * U
            drawOval(a.sombra(0.22f), Offset(c.x - r + 3f * U, c.y - r * 0.9f + 4f * U), Size(r * 2f, r * 1.8f))
            // La rodaja, algo de canto: la corteza oscura, la pulpa traslúcida y los gajos.
            drawOval(a.en(Color(0xFFB8561E), 0f, k, lit, m), Offset(c.x - r, c.y - r * 0.9f), Size(r * 2f, r * 1.8f))
            drawOval(a.en(Color(0xFFE79A3C), 0f, k, lit, m), Offset(c.x - r * 0.84f, c.y - r * 0.76f), Size(r * 1.68f, r * 1.52f))
            val gajos = Path()
            for (q in 0 until 8) { val an = q * 0.785f + azar(9952, i); gajos.moveTo(c.x, c.y); gajos.lineTo(c.x + r * 0.8f * kotlin.math.cos(an), c.y + r * 0.72f * kotlin.math.sin(an)) }
            drawPath(gajos, a.en(Color(0xFFF6CF8A), 0f, k, lit, m), style = Stroke(0.8f * U))
            drawCircle(a.en(Color(0xFFF6CF8A), 0f, k, lit, m), r * 0.14f, c)
            drawArc(a.sombra(0.25f), 0f, 160f, false, Offset(c.x - r, c.y - r * 0.9f), Size(r * 2f, r * 1.8f), style = Stroke(1.4f * U))
        }
    }

    // ---------------------------------------------------------------- la mesa

    private fun DrawScope.mesa(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U; val w = s.w
        val n = if (a.oscuro) 8 else 1
        // El tablero de pino fregado, visto desde arriba: más claro hacia delante; de noche, encendido junto a la vela.
        val cols = List(n + 1) { i -> a.dia(mezcla(P.PINO, P.PINO_LUZ, 0.35f), 0f, s.k(a, Offset(w * i / n, (s.mesa + s.canto) / 2))) }
        drawRect(if (n > 1) Brush.horizontalGradient(cols, 0f, w) else Brush.horizontalGradient(listOf(cols[0], cols[0]), 0f, w), Offset(0f, s.mesa), Size(w, s.canto - s.mesa + 1f))
        val vetas = Path()
        for (j in 0 until 9) {
            val y = s.mesa + (s.canto - s.mesa) * (0.08f + 0.1f * j)
            vetas.moveTo(0f, y)
            var x = 0f; var q = 0
            while (x < w && q < 60) { val x1 = x + 40f * U; vetas.quadraticTo((x + x1) / 2, y + (azar(9960 + j, q) - 0.5f) * 2.4f * U, x1, y); x = x1; q++ }
        }
        drawPath(vetas, a.dia(P.PINO_OSC, 0f, s.k(a, Offset(w * 0.6f, s.mesa))).copy(alpha = 0.28f), style = Stroke(0.7f * U))
        drawRect(a.sombra(0.35f), Offset(0f, s.mesa), Size(w, 1.4f * U))
        // El canto de delante, con su filo de luz, y el faldón en sombra con su cajón.
        val colsC = List(n + 1) { i -> a.dia(P.PINO, -0.05f, s.k(a, Offset(w * i / n, s.canto))) }
        drawRect(if (n > 1) Brush.horizontalGradient(colsC, 0f, w) else Brush.horizontalGradient(listOf(colsC[0], colsC[0]), 0f, w), Offset(0f, s.canto), Size(w, s.faldon - s.canto))
        drawRect(a.brillo(0.55f, s.k(a, Offset(w * 0.8f, s.canto))), Offset(0f, s.canto), Size(w, 1.2f * U))
        drawRect(a.sombra(0.4f), Offset(0f, s.faldon - 1.4f * U), Size(w, 1.4f * U))
        val colsF = List(n + 1) { i -> a.dia(P.PINO_OSC, 0f, s.k(a, Offset(w * i / n, s.faldon + 10f * U))) }
        drawRect(if (n > 1) Brush.horizontalGradient(colsF, 0f, w) else Brush.horizontalGradient(listOf(colsF[0], colsF[0]), 0f, w), Offset(0f, s.faldon), Size(w, s.h - s.faldon + 1f))
        drawRect(a.sombra(0.35f), Offset(0f, s.faldon), Size(w, 6f * U))
        // Las tablas del faldón, a lo alto, con su junta y la veta.
        val juntas = Path()
        var xj = 14f * U
        var q = 0
        while (xj < w && q < 40) { juntas.moveTo(xj, s.faldon + 6f * U); juntas.lineTo(xj, s.h); xj += (38f + 14f * azar(9963, q)) * U; q++ }
        drawPath(juntas, a.sombra(0.4f), style = Stroke(1.2f * U))
        drawPath(corridoNavidad(juntas, 1.4f * U, 0f), a.claro(0.18f), style = Stroke(0.7f * U))
        val cj = Rect(s.cx - 60f * U, s.faldon + 9f * U, s.cx + 60f * U, s.h + 2f * U)
        drawRect(a.sombra(0.4f), cj.topLeft, Size(cj.width, 1.2f * U))
        drawRect(a.claro(0.2f), Offset(cj.left, cj.top + 1.2f * U), Size(cj.width, 1f * U))
        drawRect(a.sombra(0.3f), Offset(cj.left, cj.top), Size(1.2f * U, cj.height))
        drawRect(a.sombra(0.3f), Offset(cj.right, cj.top), Size(1.2f * U, cj.height))
        clavoNavidad(Offset(s.cx, cj.top + 14f * U), 3f * U, a, s.k(a, Offset(s.cx, cj.top)), P.CREMA)
        with(MuroEstanteNavidad) { saltado(Offset(w * 0.18f, s.canto + 3f * U), 3f * U, 9965, a, s.k(a, Offset(w * 0.18f, s.canto))) }
        pano(s, a)
    }

    /** El paño de vichy (el del mantel del Ideario) colgado por el canto de la mesa, a la izquierda, con sus pliegues. */
    private fun DrawScope.pano(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val x0 = maxOf(s.papeles.x - 34f * U, 6f * U); val x1 = x0 + 46f * U
        val y0 = s.canto - 3f * U; val y1 = s.h + 4f * U
        val tela = Path().apply {
            moveTo(x0, y0); lineTo(x1, y0)
            lineTo(x1 + 3f * U, y1 - 14f * U); lineTo(x1 - 4f * U, y1); lineTo(x0 + 6f * U, y1); lineTo(x0 - 2f * U, y1 - 20f * U); close()
        }
        val k = s.k(a, Offset((x0 + x1) / 2, (y0 + y1) / 2))
        drawPath(corridoNavidad(tela, 4f * U, 3f * U), a.sombra(0.4f))
        clipPath(tela) {
            drawRect(a.dia(P.PAPEL, -0.04f, k), Offset(x0 - 4f * U, y0), Size(x1 - x0 + 10f * U, y1 - y0))
            val paso = 4.5f * U
            var x = x0 - 4f * U
            while (x < x1 + 6f * U) { drawRect(a.dia(P.CARMIN, 0f, k).copy(alpha = 0.55f), Offset(x, y0), Size(paso, y1 - y0)); x += paso * 2 }
            var y = y0
            while (y < y1) { drawRect(a.dia(P.CARMIN, 0f, k).copy(alpha = 0.55f), Offset(x0 - 4f * U, y), Size(x1 - x0 + 10f * U, paso)); y += paso * 2 }
            // Los pliegues que caen: el lado de cada uno en sombra y el otro a la luz.
            listOf(0.25f, 0.55f, 0.8f).forEach { f ->
                val px = x0 + (x1 - x0) * f
                drawPath(poligonoNavidad(px, y0 + 6f * U, px + 6f * U, y0 + 6f * U, px + 9f * U, y1, px + 2f * U, y1), a.sombra(0.22f))
                drawPath(poligonoNavidad(px - 4f * U, y0 + 6f * U, px, y0 + 6f * U, px + 2f * U, y1, px - 3f * U, y1), a.claro(0.2f))
            }
            drawRect(a.sombra(0.3f), Offset(x0 - 4f * U, y0), Size(x1 - x0 + 10f * U, 6f * U))
        }
        drawRect(a.dia(P.PAPEL, 0.02f, k), Offset(x0 - 1f * U, y0 - 3f * U), Size(x1 - x0 + 2f * U, 4f * U))
    }

    // ---------------------------------------------------------------- la cajonera (el cuerpo)

    /** La sombra de la cajonera en la pared (de día, a la derecha y abajo; de noche, a la izquierda, lejos de la vela) y en la mesa. */
    private fun DrawScope.sombraCajonera(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val dx = if (a.oscuro) -16f * U else 14f * U
        val forma = Path().apply {
            addPath(copeteForma(s), Offset(dx, 8f * U))
            addRect(Rect(s.izq + dx, s.cornisa0 + 8f * U, s.der + s.fondo + dx, s.mesa))
        }
        clipRect(0f, 0f, s.w, s.mesa) { drawPath(forma, a.sombra(if (a.oscuro) 0.4f else 0.22f)) }
        drawOval(a.sombra(0.5f), Offset(s.izq - 4f * U + dx * 0.3f, s.pies - 6f * U), Size(s.ancho + s.fondo + 12f * U, 10f * U))
    }

    /** El copete recortado: dos hombros y un arco en medio, con sus volutas. */
    fun copeteForma(s: SitioLibretaNavidad): Path = Path().apply {
        val U = s.U
        val l = s.izq + 4f * U; val r = s.der - 4f * U
        moveTo(l, s.cornisa0)
        lineTo(l, s.copete + 30f * U)
        cubicTo(l + 6f * U, s.copete + 22f * U, l + 18f * U, s.copete + 22f * U, l + 26f * U, s.copete + 16f * U)
        cubicTo(s.cx - 50f * U, s.copete + 4f * U, s.cx - 24f * U, s.copete - 4f * U, s.cx, s.copete - 4f * U)
        cubicTo(s.cx + 24f * U, s.copete - 4f * U, s.cx + 50f * U, s.copete + 4f * U, r - 26f * U, s.copete + 16f * U)
        cubicTo(r - 18f * U, s.copete + 22f * U, r - 6f * U, s.copete + 22f * U, r, s.copete + 30f * U)
        lineTo(r, s.cornisa0); close()
    }

    /**
     * El cuerpo de la cajonera, de pino pintado de crema con los filetes verdes: el copete con su cartela, la cornisa, el costado
     * que se ve, el marco de los cajoncitos (hondo entre ellos), el zócalo con su filete y las patas. Con [m], dentro de la mancha.
     */
    private fun DrawScope.cuerpo(s: SitioLibretaNavidad, a: AireNavidad, m: ManchaNavidad?) {
        val U = s.U
        val V = PaletaLibretaNavidad
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, p: Offset) = MuebleLibretaNavidad.conFrio(a.en(col, f, s.k(a, p), lit, m), p, s, a)
        /** [r] pintado a lo ancho con [col] según la luz de cada punto (de noche, del frío de la izquierda a la vela de la derecha). */
        fun aLoAncho(r: Rect, col: Color, f: Float, y: Float, fuerza: Float = 1f): Brush {
            if (!a.oscuro) return Brush.horizontalGradient(listOf(c(col, f, Offset(r.center.x, y)), c(col, f, Offset(r.center.x, y))), r.left, r.right)
            val n = 10
            return Brush.horizontalGradient(List(n + 1) { i ->
                val p = Offset(r.left + r.width * i / n, y)
                MuebleLibretaNavidad.conFrio(a.en(col, f, s.k(a, p), lit, m), p, s, a, fuerza)
            }, r.left, r.right)
        }
        val medio = Offset(s.cx, (s.cajones0 + s.cajones1) / 2)
        val derecha = Offset(s.der, medio.y)
        // El costado que se ve (a la derecha), con su canto.
        val costado = poligonoNavidad(s.der, s.cornisa0, s.der + s.fondo, s.cornisa0 - s.fondo * 0.5f, s.der + s.fondo, s.zocalo1 - s.fondo * 0.5f, s.der, s.zocalo1)
        drawPath(costado, c(V.VERDE_OSC, if (a.oscuro) 0f else 0.04f, derecha))
        if (a.oscuro) drawPath(costado, P.LLAMA.copy(alpha = 0.18f * s.k(a, derecha)))
        // El frente: el marco crema de los cajoncitos, con lo hondo entre ellos (se ve por las rendijas).
        val frente = Rect(s.izq, s.cornisa1, s.der, s.zocalo1)
        drawRect(aLoAncho(frente, V.VERDE, 0f, medio.y), frente.topLeft, frente.size)
        // De noche, el larguero de la izquierda mira a la ventana: un plano frío, azul verdoso claro, con su filo.
        if (a.oscuro) {
            val larguero = Rect(frente.left, frente.top, s.rejilla.left - 3.5f * U, frente.bottom)
            drawRect(MuebleLibretaNavidad.conFrio(a.en(V.VERDE_LUZ, 0f, s.k(a, larguero.center), lit, m), Offset(larguero.left, s.cajones0), s, a, 1.25f), larguero.topLeft, larguero.size)
        }
        drawRect(c(mezcla(P.PINO_OSC, P.TINTA, 0.4f), 0f, medio), s.rejilla.topLeft, s.rejilla.size)
        drawRect(c(mezcla(P.PINO_OSC, P.TINTA, 0.4f), 0f, medio), Offset(s.rejilla.left, s.largo0), Size(s.rejilla.width, s.largo1 - s.largo0))
        // Los filetes verdes del marco: alrededor de la rejilla y del cajón largo.
        drawRect(c(P.CREMA, 0f, medio), s.rejilla.inflate(3.5f * U).topLeft, s.rejilla.inflate(3.5f * U).size, style = Stroke(1.2f * U))
        // La luz del lado de la ventana y la sombra del otro (de noche, al revés: la vela está a la derecha).
        if (!a.oscuro) {
            drawRect(a.claro(0.3f), frente.topLeft, Size(4f * U, frente.height))
            drawRect(a.sombra(0.12f), Offset(frente.right - 6f * U, frente.top), Size(6f * U, frente.height))
        } else {
            drawRect(a.filoFrio(0.5f * MuebleLibretaNavidad.frio(frente.topLeft, s)), frente.topLeft, Size(1.6f * U, frente.height))
            drawRect(a.filo(0.6f * s.k(a, derecha)), Offset(frente.right - 1.5f * U, frente.top), Size(1.5f * U, frente.height))
        }
        // El copete con su cartela.
        val cp = copeteForma(s)
        drawPath(cp, aLoAncho(Rect(s.izq, s.copete, s.der, s.cornisa0), V.VERDE, 0f, s.copete + 20f * U, 1.1f))
        drawPath(Path().apply { op(cp, corridoNavidad(cp, 0f, 2.5f * U), PathOperation.Difference) }, if (a.oscuro) a.filoFrio(0.55f * MuebleLibretaNavidad.frio(Offset(s.izq, s.copete), s)) else a.claro(0.55f))
        clipPath(cp) {
            drawPath(corridoNavidad(cp, 0f, 5f * U), c(P.CREMA, 0f, Offset(s.cx, s.copete)), style = Stroke(1.1f * U))
            drawRect(a.sombra(0.18f), Offset(s.der - 22f * U, s.copete - 6f * U), Size(22f * U, s.cornisa0 - s.copete + 6f * U))
        }
        val ct = s.cartela
        drawRect(a.sombra(0.3f), ct.topLeft + Offset(1.2f * U, 1.6f * U), ct.size)
        drawRect(if (a.oscuro) a.etiqueta(P.PAPEL, s.k(a, ct.center)) else c(P.PAPEL, 0f, ct.center), ct.topLeft, ct.size)
        drawRect(c(P.CARMIN, 0f, ct.center), ct.deflate(2.5f * U).topLeft, ct.deflate(2.5f * U).size, style = Stroke(0.9f * U))
        // Las volutas pintadas a los lados de la cartela: un tallo de acebo verde con sus bayas.
        listOf(-1f, 1f).forEach { l ->
            val b = Offset(s.cx + l * (ct.width / 2 + 10f * U), ct.center.y + 2f * U)
            aceboNavidad(b, 11f * U, if (l < 0) -100f else -80f, LuzNavidad(a, s.k(a, b), lit, m, 0f, -1f))
        }
        // La cornisa: el tablero de arriba, su bocel y la gola, con su filete verde.
        val n0 = s.cornisa0; val n1 = s.cornisa1
        val cor = Rect(s.izq - 5f * U, n0, s.der + 5f * U, n1)
        drawRect(aLoAncho(cor, V.VERDE_LUZ, 0f, n0, 1.15f), Offset(s.izq - 5f * U, n0), Size(s.ancho + 10f * U, (n1 - n0) * 0.45f))
        drawRect(aLoAncho(cor, V.VERDE, -0.08f, n1), Offset(s.izq - 5f * U, n0 + (n1 - n0) * 0.45f), Size(s.ancho + 10f * U, (n1 - n0) * 0.55f))
        drawRect(c(P.CREMA, 0f, Offset(s.cx, n0)), Offset(s.izq - 5f * U, n0 + (n1 - n0) * 0.62f), Size(s.ancho + 10f * U, 1.2f * U))
        drawRect(a.brillo(0.5f, s.k(a, Offset(s.cx, n0))), Offset(s.izq - 5f * U, n0), Size(s.ancho + 10f * U, 1f * U))
        drawRect(a.sombra(0.4f), Offset(s.izq, n1), Size(s.ancho, 1.6f * U))
        drawPath(poligonoNavidad(s.der + 5f * U, n0, s.der + s.fondo + 5f * U, n0 - s.fondo * 0.5f, s.der + s.fondo + 5f * U, n1 - s.fondo * 0.5f, s.der + 5f * U, n1), c(V.VERDE_OSC, -0.1f, derecha))
        // El zócalo con su filete y su bocel, y las patas de bollo.
        val z0 = s.largo1; val z1 = s.zocalo1
        drawRect(aLoAncho(Rect(s.izq - 3f * U, z0, s.der + 3f * U, z1), V.VERDE, -0.04f, z1), Offset(s.izq - 3f * U, z0), Size(s.ancho + 6f * U, z1 - z0))
        drawRect(c(P.CREMA, 0f, Offset(s.cx, z1)), Offset(s.izq - 3f * U, z0 + (z1 - z0) * 0.5f), Size(s.ancho + 6f * U, 1.1f * U))
        drawRect(a.brillo(0.45f, s.k(a, Offset(s.cx, z0))), Offset(s.izq - 3f * U, z0), Size(s.ancho + 6f * U, 1f * U))
        drawRect(a.sombra(0.35f), Offset(s.izq - 3f * U, z1 - 1.4f * U), Size(s.ancho + 6f * U, 1.4f * U))
        listOf(s.izq + 8f * U, s.der - 8f * U).forEach { x ->
            val pk = s.k(a, Offset(x, s.pies))
            drawOval(a.sombra(0.4f), Offset(x - 8f * U, s.pies - 2f * U), Size(18f * U, 5f * U))
            drawOval(a.dia(V.VERDE, -0.05f, pk), Offset(x - 7f * U, z1 - 1f * U), Size(14f * U, s.pies - z1 + 2f * U))
            drawOval(a.claro(0.3f), Offset(x - 5f * U, z1), Size(5f * U, s.pies - z1 - 1f * U))
            drawOval(a.sombra(0.25f), Offset(x - 1f * U, z1), Size(8f * U, s.pies - z1 + 1f * U))
        }
        // La pintura saltada donde se agarra, que deja ver el pino.
        with(MuroEstanteNavidad) {
            saltado(Offset(s.izq + 3f * U, s.cornisa1 + 30f * U), 3.4f * U, 9970, a, s.k(a, Offset(s.izq, s.cornisa1)))
            saltado(Offset(s.der - 14f * U, n0 + 2f * U), 3f * U, 9971, a, s.k(a, Offset(s.der, n0)))
            saltado(Offset(s.izq + 30f * U, z1 - 2f * U), 2.6f * U, 9972, a, s.k(a, Offset(s.izq, z1)))
        }
    }

    // ---------------------------------------------------------------- lo de la mesa

    /** Los papelitos en blanco, cortados a mano, con el lápiz atravesado encima: ahí se apunta el momento antes de guardarlo. */
    private fun DrawScope.papeles(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val p = s.papeles
        val l = LuzNavidad(a, s.k(a, p), 0f, null, 0f, -1f)
        for (i in 0 until 4) {
            val r = Rect(p.x - 22f * U, p.y - 12f * U - i * 1.6f * U, p.x + 20f * U, p.y + 6f * U - i * 1.6f * U)
            rotate(-8f + 5f * i + azar(9975, i) * 4f, r.center) {
                if (i == 0) drawRect(a.sombra(0.35f), r.topLeft + Offset(2f * U, 2.5f * U), r.size)
                drawRect(l.c(mezcla(P.PAPEL, P.CARTULINA, 0.15f * (i % 2)), -0.03f * (3 - i)), r.topLeft, r.size)
                drawRect(a.sombra(0.2f), Offset(r.left, r.bottom - 1f * U), Size(r.width, 1f * U))
                if (i == 3) drawLine(a.sombra(0.2f), Offset(r.left + r.width * 0.5f, r.top), Offset(r.left + r.width * 0.5f, r.bottom), 0.7f * U)
            }
        }
        // El lápiz, verde, hexagonal, con su punta de pino y la mina.
        val a0 = Offset(p.x - 26f * U, p.y + 2f * U); val b0 = Offset(p.x + 22f * U, p.y - 12f * U)
        drawLine(a.sombra(0.35f), a0 + Offset(2f * U, 3f * U), b0 + Offset(2f * U, 3f * U), 4f * U, StrokeCap.Round)
        val tip = a0 + (b0 - a0) * 0.86f
        drawLine(l.c(P.FILETE), a0, tip, 4f * U)
        drawLine(l.c(P.FILETE_LUZ), a0 + Offset(0f, -1f * U), tip + Offset(0f, -1f * U), 1.2f * U)
        drawLine(l.c(P.PINO_LUZ), tip, a0 + (b0 - a0) * 0.96f, 3f * U)
        drawLine(l.c(P.TINTA), a0 + (b0 - a0) * 0.96f, b0, 1.2f * U, StrokeCap.Round)
        drawLine(l.c(P.LATON), a0, a0 + (b0 - a0) * 0.06f, 4.2f * U)
    }

    /** El plato de loza con dos mandarinas (una con su hoja) y unas mondas, a la derecha. */
    private fun DrawScope.plato(s: SitioLibretaNavidad, a: AireNavidad) {
        val U = s.U
        val c = s.plato
        if (c.x - 30f * U > s.w) return
        val l = LuzNavidad(a, s.k(a, c), 0f, null, 0f, if (a.oscuro) -1f else -1f)
        drawOval(a.sombra(0.4f), Offset(c.x - 28f * U, c.y - 3f * U), Size(60f * U, 12f * U))
        drawOval(l.c(P.LOZA, -0.05f), Offset(c.x - 30f * U, c.y - 7f * U), Size(60f * U, 13f * U))
        drawOval(l.c(P.FILETE), Offset(c.x - 26f * U, c.y - 5.5f * U), Size(52f * U, 10f * U), style = Stroke(1f * U))
        listOf(Offset(-9f, -10f) to 10f, Offset(9f, -9f) to 9f).forEachIndexed { i, (o, r) ->
            val q = Offset(c.x + o.x * U, c.y + o.y * U)
            val rr = r * U
            drawCircle(l.sombra(0.3f), rr, q + Offset(2f * U, 2.5f * U))
            drawCircle(l.c(P.NARANJA), rr, q)
            drawCircle(l.sombra(0.25f), rr, q + Offset(rr * 0.35f, rr * 0.3f), style = Stroke(rr * 0.45f))
            drawCircle(l.claro(0.5f), rr * 0.2f, q - Offset(rr * 0.38f, rr * 0.4f))
            drawCircle(l.c(P.PINO_OSC), 1.1f * U, q - Offset(0f, rr * 0.85f))
            if (i == 0) {
                val hoja = Path().apply { moveTo(q.x, q.y - rr * 0.9f); quadraticTo(q.x + 6f * U, q.y - rr * 1.6f, q.x + 12f * U, q.y - rr * 1.3f); quadraticTo(q.x + 6f * U, q.y - rr * 0.9f, q.x, q.y - rr * 0.9f); close() }
                drawPath(hoja, l.c(P.ABETO))
                drawLine(l.c(P.ABETO_LUZ), Offset(q.x, q.y - rr * 0.9f), Offset(q.x + 10f * U, q.y - rr * 1.25f), 0.7f * U)
            }
        }
    }
}
