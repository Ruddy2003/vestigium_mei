package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.cos
import kotlin.math.sin

/**
 * Lo que rodea al armario del estante de Scriptorium ([MuebleEstanteMedieval]): el muro de sillarejo de caras planas con su
 * mortero y los paños de enlucido viejo, la ventana abocinada con su saetera, el rayo que entra por ella con su polvo y el
 * entarimado de pino.
 */
internal object MuroEstanteMedieval {
    private val P = PaletaMedieval
    private val M = MuebleEstanteMedieval

    // ---------------------------------------------------------------- el muro

    /**
     * El muro: sillarejo de caras planas en ocres y grises, de tamaños desiguales (alguna piedra grande que ocupa dos hiladas,
     * ripios en las juntas), con su mortero de cal; paños de enlucido viejo que tapan la piedra y se desconchan, la humedad y
     * el hollín del candil. De noche, el mortero y las piedras toman la luz del candil por zonas.
     */
    fun DrawScope.muro(a: AireMedieval, alto: Float, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        val zx = if (a.lumbre) 18 else 1; val zy = if (a.lumbre) 7 else 1
        for (i in 0 until zx) for (j in 0 until zy) {
            val r = Rect(w * i / zx, alto * j / zy, w * (i + 1) / zx + 0.6f, alto * (j + 1) / zy + 0.6f)
            drawRect(a.luz(P.MORTERO, 0.7f * cerca(r.center)), r.topLeft, r.size)
        }
        pinceladas(Rect(0f, 0f, w, alto), a.luz(P.PIEDRA_OSC, 0.2f), 1300, 30, u, 0.08f)
        // Las hiladas: altos desiguales; una piedra alta ocupa también la hilada de abajo, que la rodea.
        val hiladas = mutableListOf<Float>()
        var yy = -w * 0.03f
        while (yy < alto + w * 0.02f) { val hh = w * (0.045f + 0.045f * azar(1301, hiladas.size)); hiladas.add(hh); yy += hh }
        var tomadas: List<Pair<Float, Float>> = emptyList()
        var y = -w * 0.03f
        for (f in hiladas.indices) {
            val hil = hiladas[f]
            val siguientes = mutableListOf<Pair<Float, Float>>()
            var x = -w * 0.12f * azar(1302, f)
            var j = 0
            while (x < w) {
                val dentro = tomadas.firstOrNull { x >= it.first - 1f && x < it.second }
                if (dentro != null) { x = dentro.second; continue }
                val s = f * 37 + j
                var largo = w * (0.055f + 0.1f * azar(1303 + f, j))
                if (azar(s, 44) > 0.86f) largo *= 1.6f
                val proxima = tomadas.filter { it.first > x }.minOfOrNull { it.first } ?: Float.MAX_VALUE
                val fin = maxOf(x + 1f, minOf(x + largo, proxima))
                val alta = f + 1 < hiladas.size && azar(s, 45) > 0.8f && fin - x > w * 0.07f
                val baja = 1.6f * u + hil * 0.08f * azar(s, 46)
                val r = Rect(x + 1.8f * u, y + baja, fin - 1.8f * u, minOf(y + hil + (if (alta) hiladas[f + 1] else 0f), alto + w * 0.03f) - 1.6f * u)
                if (r.width > 2f && r.height > 2f) piedra(r, s, a, 0.8f * cerca(r.center))
                // El ripio: una piedra chica calzada arriba de la que baja.
                if (baja > 3.2f * u && r.width > w * 0.07f) {
                    val q = Rect(r.left + r.width * 0.25f, y + 0.8f * u, r.left + r.width * (0.4f + 0.2f * azar(s, 41)), r.top - 1f * u)
                    if (q.height > 1.5f * u) piedra(q, s + 9001, a, 0.8f * cerca(q.center))
                }
                if (alta) siguientes.add(x to fin)
                x = fin; j++
            }
            tomadas = siguientes; y += hil
        }
        // La humedad que baja de arriba por las juntas.
        humedad(Offset(w * 0.6f, -w * 0.01f), w * 0.05f, w * 0.26f, 1310, a)
        // Los paños de enlucido viejo.
        enlucido(Offset(w * 0.8f, w * 0.13f), w * 0.21f, w * 0.13f, 1320, a, 0.8f * cerca(Offset(w * 0.8f, w * 0.15f)))
        enlucido(Offset(w * 0.42f, w * 0.03f), w * 0.13f, w * 0.07f, 1340, a, 0.8f * cerca(Offset(w * 0.42f, w * 0.05f)))
        enlucido(Offset(w * 0.04f, w * 0.42f), w * 0.12f, w * 0.08f, 1360, a, 0.8f * cerca(Offset(w * 0.06f, w * 0.42f)))
        // La luz de día, más clara arriba a la izquierda (la ventana y la galería que no se ve).
        // La luz de día baja de arriba a la izquierda (la ventana y la galería que no se ve): el muro se enfría hacia abajo, junto
        // al tablero que vuela, y alrededor de la ventana, que se come la luz.
        if (!a.oscuro) {
            drawRect(a.frio(0.1f), Offset(0f, alto - w * 0.07f), Size(w, w * 0.07f))
            drawRect(a.frio(0.1f), Offset(0f, alto - w * 0.035f), Size(w, w * 0.035f))
            drawRect(a.frio(0.12f), Offset(0f, alto - w * 0.012f), Size(w, w * 0.012f))
            drawOval(a.frio(0.08f), Offset(w * (M.VENT_X - 0.17f), w * 0.0f), Size(w * 0.34f, w * 0.42f))
        }
        // El hollín que el candil ha dejado encima de donde cuelga.
        val hollin = Offset(w * M.LAMP_X, w * (M.LAMP_Y - 0.1f))
        val tizne = mezcla(P.NOGAL_OSC, P.NEGRO, 0.5f)
        for (i in 0 until 5) {
            val t = i / 4f
            val c = Offset(hollin.x + w * 0.01f * (t - 0.5f), hollin.y + w * 0.06f - w * 0.1f * t)
            val rx = w * (0.014f + 0.03f * t); val ry = w * (0.03f + 0.03f * t)
            drawOval(tizne.copy(alpha = 0.1f - 0.05f * t), Offset(c.x - rx, c.y - ry), Size(rx * 2, ry * 2))
        }
    }

    /** Una piedra de sillarejo de cara plana: el canto en sombra fría abajo y a la derecha, la cara con una faceta y su picado. */
    private fun DrawScope.piedra(r: Rect, s: Int, a: AireMedieval, k: Float) {
        val u = density
        val m = minOf(r.width, r.height)
        fun c(i: Int) = m * (0.05f + 0.25f * azar(s, i))
        fun j(i: Int) = 1.2f * u * azar(s, i)
        val pts = floatArrayOf(
            r.left + c(1), r.top + j(2), r.right - c(3), r.top + j(4),
            r.right, r.top + c(3) * 0.8f, r.right - j(5), r.bottom - c(6),
            r.right - c(6), r.bottom, r.left + c(7), r.bottom - j(8),
            r.left, r.bottom - c(7) * 0.8f, r.left + j(9), r.top + c(1) * 0.9f,
        )
        val p = poligonoMedieval(*pts)
        val sorteo = azar(s, 15)
        val otro = when { sorteo > 0.75f -> P.PIEDRA_CLARA; sorteo > 0.5f -> P.OCRE; sorteo > 0.25f -> P.GRIS; else -> P.PIEDRA_OSC }
        val tono = mezcla(mezcla(P.PIEDRA, otro, 0.3f + 0.6f * azar(s, 16)), P.PIEDRA_OSC, if (azar(s, 19) > 0.7f) 0.25f else 0f)
        // La sombra que deja en el mortero, el canto (lo que sobresale) y la cara.
        drawPath(corridoMedieval(p, 1.4f * u, 1.7f * u), a.frio(if (a.oscuro) 0.3f else 0.26f))
        drawPath(p, a.luz(mezcla(tono, P.FRIO, 0.4f), k))
        drawPath(corridoMedieval(p, -1f * u, -1f * u), a.luz(tono, k))
        // Una faceta: la cara no es lisa, la parten dos planos.
        val face = poligonoMedieval(pts[2] - u, pts[3] - u, pts[8] - u, pts[9] - u, pts[12] - u, pts[13] - u)
        drawPath(face, if (azar(s, 17) > 0.5f) a.claro(0.08f) else a.frio(0.07f))
        // El filo de arriba a la izquierda, que mira a la luz.
        drawLine(a.claro(0.38f), Offset(pts[14] - u, pts[15] - u), Offset(pts[0] - u, pts[1] - u), 0.9f * u)
        drawLine(a.claro(0.38f), Offset(pts[0] - u, pts[1] - u), Offset(pts[2] - u, pts[3] - u), 0.9f * u)
        if (azar(s, 29) > 0.5f) drawCircle(a.frio(0.18f), (0.5f + 0.5f * azar(s, 30)) * u, Offset(r.left + r.width * azar(s, 33), r.top + r.height * azar(s, 36)))
    }

    /**
     * Un paño de enlucido de cal viejo: el borde se deshace (una película más fina que deja ver la piedra, migas de cal sueltas
     * y el canto con su sombra fina), con grietas, churretes de humedad y desconchones donde la cal se adelgaza y asoma la piedra.
     */
    private fun DrawScope.enlucido(c: Offset, rx: Float, ry: Float, s: Int, a: AireMedieval, k: Float) {
        val u = density
        val n = 40
        fun contorno(escala: Float, semilla: Int) = Path().apply {
            for (i in 0 until n) {
                val ang = 2.0 * Math.PI * i / n
                val f = escala * (0.84f + 0.2f * azar(semilla, i)) * (if (azar(semilla, 100 + i) > 0.82f) 0.8f else 1f)
                val q = Offset(c.x + (rx * f * cos(ang)).toFloat(), c.y + (ry * f * sin(ang)).toFloat())
                if (i == 0) moveTo(q.x, q.y) else lineTo(q.x, q.y)
            }
            close()
        }
        val cal = a.luz(mezcla(mezcla(P.CAL, P.MORTERO, 0.5f), P.PIEDRA, 0.24f), k)
        val p = contorno(1f, s)
        val caja = Rect(c.x - rx * 1.25f, c.y - ry * 1.25f, c.x + rx * 1.25f, c.y + ry * 1.25f)
        // La película fina de cal alrededor, que ya deja ver la piedra: la transición.
        drawPath(contorno(1.12f, s + 7), cal.copy(alpha = 0.32f))
        // El paño, con el canto en sombra fina abajo a la derecha.
        drawPath(corridoMedieval(p, 0.7f * u, 0.9f * u), a.frio(0.22f))
        drawPath(p, cal)
        clipPath(p) {
            pinceladas(caja, a.sombra(1f), s, 16, u, 0.07f)
            pinceladas(caja, a.claro(1f), s + 1, 8, u, 0.1f)
            drawRect(a.sombra(0.07f), Offset(caja.left, c.y), Size(caja.width, caja.height))
            for (i in 0 until 3) {
                val x = caja.left + caja.width * (0.2f + 0.3f * i + 0.1f * azar(s, 200 + i))
                val largo = caja.height * (0.4f + 0.5f * azar(s, 210 + i))
                drawPath(poligonoMedieval(x, caja.top, x + 4f * u, caja.top, x + 2.5f * u, caja.top + largo, x + 0.5f * u, caja.top + largo * 0.9f), a.frio(0.07f))
            }
            for (i in 0 until 3) {
                val g0 = Offset(caja.left + caja.width * azar(s, 220 + i), caja.top + caja.height * azar(s, 230 + i))
                val g = Path().apply {
                    moveTo(g0.x, g0.y)
                    lineTo(g0.x + 5f * u, g0.y + 3f * u); lineTo(g0.x + 7f * u, g0.y + 9f * u); lineTo(g0.x + 12f * u, g0.y + 11f * u)
                }
                drawPath(g, a.sombra(0.26f), style = Stroke(0.6f * u))
            }
            // Los desconchones: la cal se adelgaza y asoma la piedra, con el labio de cal que toma la luz arriba a la izquierda.
            for (i in 0 until 4) {
                val d0 = Offset(caja.left + caja.width * (0.18f + 0.64f * azar(s, 240 + i)), caja.top + caja.height * (0.25f + 0.5f * azar(s, 250 + i)))
                val t = (3f + 5f * azar(s, 260 + i)) * u
                val hueco = poligonoMedieval(d0.x - t, d0.y, d0.x - t * 0.3f, d0.y - t * 0.55f, d0.x + t * 0.8f, d0.y - t * 0.3f, d0.x + t, d0.y + t * 0.35f, d0.x, d0.y + t * 0.55f)
                drawPath(hueco, a.luz(P.PIEDRA, k).copy(alpha = 0.55f))
                drawLine(a.claro(0.4f), Offset(d0.x - t, d0.y - 0.6f * u), Offset(d0.x - t * 0.3f, d0.y - t * 0.55f - 0.6f * u), 0.7f * u)
                drawLine(a.frio(0.14f), Offset(d0.x + t, d0.y + t * 0.35f), Offset(d0.x, d0.y + t * 0.55f), 0.7f * u)
            }
        }
        // Las migas de cal sueltas en el borde, y algún punto de piedra que asoma dentro.
        for (i in 0 until 14) {
            val ang = 2.0 * Math.PI * azar(s, 300 + i)
            val f = 1f + 0.12f * azar(s, 330 + i)
            val q = Offset(c.x + (rx * f * cos(ang)).toFloat(), c.y + (ry * f * sin(ang)).toFloat())
            val t = (0.8f + 1.6f * azar(s, 360 + i)) * u
            drawPath(poligonoMedieval(q.x - t, q.y, q.x, q.y - t * 0.7f, q.x + t, q.y, q.x, q.y + t * 0.7f), cal.copy(alpha = 0.85f))
        }
    }

    /** Una mancha de humedad que baja por el muro, fría y deshecha. */
    private fun DrawScope.humedad(arriba: Offset, ancho: Float, largo: Float, s: Int, a: AireMedieval) {
        val p = Path().apply {
            moveTo(arriba.x - ancho / 2, arriba.y)
            lineTo(arriba.x + ancho / 2, arriba.y)
            cubicTo(arriba.x + ancho * 0.6f, arriba.y + largo * 0.4f, arriba.x + ancho * 0.1f, arriba.y + largo * 0.7f, arriba.x + ancho * 0.2f, arriba.y + largo)
            cubicTo(arriba.x - ancho * 0.1f, arriba.y + largo * 0.8f, arriba.x - ancho * 0.7f, arriba.y + largo * (0.4f + 0.2f * azar(s, 1)), arriba.x - ancho / 2, arriba.y)
            close()
        }
        drawPath(p, a.frio(0.1f))
        drawPath(p, a.frio(0.08f), style = Stroke(2f * density))
    }

    // ---------------------------------------------------------------- la ventana

    /**
     * La ventana abocinada: el anillo de dovelas y las jambas de sillar en el muro; dentro, el derrame hondo que se cierra hacia
     * la saetera (las juntas de sus piedras convergen hacia ella) y el alféizar en rampa. De día el cielo blanco y la cara
     * derecha y el alféizar al sol, la izquierda y el intradós en sombra fría; al anochecer, el cielo de fuego; de noche, azul
     * con dos estrellas; en los Sueños, la luna asoma por la saetera.
     */
    fun DrawScope.ventana(a: AireMedieval) {
        val u = density; val w = size.width
        val cx = w * M.VENT_X
        val ex0 = cx - w * 0.075f; val ex1 = cx + w * 0.075f; val eTop = w * 0.065f; val eSill = w * 0.34f
        val sx0 = cx - w * 0.013f; val sx1 = cx + w * 0.013f; val sTop = w * 0.13f; val sBot = w * 0.26f
        val eSpring = eTop + (ex1 - ex0) / 2; val sSpring = sTop + (sx1 - sx0) / 2
        val k = if (a.lumbre) M.alcanceMuro(M.VENT_X - M.LAMP_X, 0.2f - M.LAMP_Y) else 1f
        anillo(cx, eSpring, ex1 - cx, w * 0.034f, eSill, a, k)
        // El derrame.
        val tonos: List<Color> = when {
            a.oscuro && a.noche -> listOf(mezcla(P.CAL, P.NOCHE_SUENOS, 0.76f), mezcla(P.CAL, P.NOCHE_SUENOS, 0.84f),
                mezcla(mezcla(P.CAL, P.CIELO_NOCHE, 0.5f), P.LUNA, 0.22f), mezcla(P.CAL, P.CIELO_NOCHE, 0.58f))
            a.oscuro -> listOf(mezcla(P.CAL, P.NOCHE, 0.82f), mezcla(P.CAL, P.NOCHE, 0.88f), mezcla(P.CAL, P.CIELO_NOCHE, 0.76f), mezcla(P.CAL, P.CIELO_NOCHE, 0.8f))
            a.noche -> listOf(mezcla(P.CAL, P.ANOCHECER, 0.45f), mezcla(P.CAL, P.ANOCHECER, 0.58f), mezcla(P.CAL, P.CREPUSCULO, 0.42f), mezcla(P.CAL, P.CREPUSCULO, 0.3f))
            else -> listOf(mezcla(P.CAL, P.FRIO, 0.26f), mezcla(P.CAL, P.FRIO, 0.36f), mezcla(P.CAL, P.SOL, 0.3f), mezcla(P.CAL, P.SOL, 0.6f))
        }
        drawPath(medioPuntoMedieval(ex0, ex1, eTop, eSill), tonos[0])
        val izq = poligonoMedieval(ex0, eSpring, ex0, eSill, sx0, sBot, sx0, sSpring)
        val der = poligonoMedieval(ex1, eSpring, ex1, eSill, sx1, sBot, sx1, sSpring)
        val piso = poligonoMedieval(ex0, eSill, ex1, eSill, sx1, sBot, sx0, sBot)
        drawPath(izq, tonos[1]); drawPath(der, tonos[2]); drawPath(piso, tonos[3])
        // Las juntas que convergen hacia la saetera: en el intradós (radiales), en las caras (hiladas) y en el alféizar (losas).
        val junta = a.frio(if (a.oscuro) 0.3f else 0.22f)
        for (i in 1 until 6) {
            val ang = Math.PI + Math.PI * i / 6
            val r0 = ex1 - cx; val r1 = sx1 - cx
            drawLine(junta, Offset(cx + (r0 * cos(ang)).toFloat(), eSpring + (r0 * sin(ang)).toFloat()), Offset(cx + (r1 * cos(ang)).toFloat(), sSpring + (r1 * sin(ang)).toFloat()), 0.8f * u)
        }
        for (i in 1 until 5) {
            val t = i / 5f
            val ye = eSpring + (eSill - eSpring) * t; val ys = sSpring + (sBot - sSpring) * t
            drawLine(junta, Offset(ex0, ye), Offset(sx0, ys), 0.8f * u)
            drawLine(junta, Offset(ex1, ye), Offset(sx1, ys), 0.8f * u)
        }
        for (t in listOf(0.33f, 0.66f)) drawLine(junta, Offset(ex0 + (ex1 - ex0) * t, eSill), Offset(sx0 + (sx1 - sx0) * t, sBot), 0.8f * u)
        drawLine(junta, Offset((ex0 + sx0) / 2, (eSill + sBot) / 2), Offset((ex1 + sx1) / 2, (eSill + sBot) / 2), 0.8f * u)
        // Las aristas del derrame: la de la izquierda en sombra, la de la derecha con luz.
        drawLine(a.frio(0.35f), Offset(ex0, eSpring), Offset(sx0, sSpring), 1f * u)
        drawLine(a.claro(0.35f), Offset(ex1, eSpring), Offset(sx1, sSpring), 1f * u)
        drawLine(a.frio(0.3f), Offset(ex0, eSill), Offset(sx0, sBot), 1f * u)
        drawLine(a.claro(0.4f), Offset(ex1, eSill), Offset(sx1, sBot), 1f * u)
        clipPath(izq) { drawRect(a.frio(0.14f), Offset(ex0, eSpring), Size(sx0 - ex0, (eSill - eSpring) * 0.35f)) }
        pinceladas(Rect(ex0, eTop, ex1, eSill), a.sombra(1f), 1321, 10, u, 0.05f)
        // La saetera: su canto hondo y el cielo.
        val luz = medioPuntoMedieval(sx0, sx1, sTop, sBot)
        drawPath(medioPuntoMedieval(sx0 - 1.6f * u, sx1 + 1.6f * u, sTop - 1.6f * u, sBot + 1f * u), a.frio(0.5f))
        clipPath(luz) { cielo(a, sx0, sx1, sTop, sBot, cx) }
        if (!a.oscuro) mancha(Offset(cx, (sTop + sBot) / 2), w * 0.08f, Color.White.copy(alpha = if (a.noche) 0.12f else 0.35f))
        else if (a.noche) mancha(Offset(cx, sTop + w * 0.05f), w * 0.12f, P.LUNA.copy(alpha = 0.16f))
    }

    /** El anillo de dovelas alrededor del vano, con su clave, y las jambas de sillar hasta el alféizar. */
    private fun DrawScope.anillo(cx: Float, cy: Float, r0: Float, grueso: Float, abajo: Float, a: AireMedieval, k: Float) {
        val u = density
        val r1 = r0 + grueso
        val piezas = 9
        for (i in 0 until piezas) {
            val a0 = Math.PI + Math.PI * i / piezas; val a1 = Math.PI + Math.PI * (i + 1) / piezas
            fun pt(r: Float, ang: Double) = Offset(cx + (r * cos(ang)).toFloat(), cy + (r * sin(ang)).toFloat())
            val ext = if (i == piezas / 2) 1.18f else 1f
            val p0 = pt(r0, a0); val p1 = pt(r0, a1); val q1 = pt(r0 + grueso * ext, a1); val q0 = pt(r0 + grueso * ext, a0)
            val dov = poligonoMedieval(p0.x, p0.y, q0.x, q0.y, q1.x, q1.y, p1.x, p1.y)
            drawPath(corridoMedieval(dov, 1.4f * u, 1.6f * u), a.frio(0.28f))
            val tono = mezcla(P.PIEDRA_CLARA, if (i % 2 == 0) P.OCRE else P.GRIS, 0.3f + 0.2f * azar(1350, i))
            drawPath(dov, a.luz(tono, k))
            drawPath(dov, a.luz(P.MORTERO, k), style = Stroke(1.2f * u))
            drawLine(a.claro(0.35f), q0, q1, 0.9f * u)
        }
        // Las jambas: sillares alternos, uno largo y uno corto, a cada lado.
        for (lado in listOf(-1f, 1f)) {
            var y = cy; var i = 0
            while (y < abajo) {
                val alto = minOf(grueso * (if (i % 2 == 0) 1.15f else 0.8f), abajo - y)
                val ancho = grueso * (if (i % 2 == 0) 1f else 1.4f)
                val x0 = if (lado < 0) cx - r0 - ancho else cx + r0
                val sil = Rect(x0, y + 0.6f * u, x0 + ancho, y + alto - 0.6f * u)
                drawRect(a.frio(0.28f), sil.topLeft + Offset(1.4f * u, 1.6f * u), sil.size)
                drawRect(a.luz(mezcla(P.PIEDRA_CLARA, if (i % 2 == 0) P.GRIS else P.OCRE, 0.3f), k), sil.topLeft, sil.size)
                drawRect(a.claro(0.3f), sil.topLeft, Size(sil.width, 0.9f * u))
                drawRect(a.frio(0.25f), Offset(sil.left, sil.bottom - 1.2f * u), Size(sil.width, 1.2f * u))
                y += alto; i++
            }
        }
    }

    /** El cielo que se ve por la saetera, según la hora. */
    private fun DrawScope.cielo(a: AireMedieval, sx0: Float, sx1: Float, sTop: Float, sBot: Float, cx: Float) {
        val u = density; val w = size.width
        val r = Rect(sx0, sTop, sx1, sBot)
        when {
            a.oscuro -> {
                drawRect(if (a.noche) mezcla(P.CIELO_NOCHE, P.LUNA, 0.18f) else P.CIELO_NOCHE, r.topLeft, r.size)
                if (a.noche) {
                    val cl = Offset(cx + w * 0.008f, sTop + w * 0.05f)
                    drawCircle(P.LUNA.copy(alpha = 0.25f), w * 0.034f, cl)
                    drawCircle(P.LUNA, w * 0.022f, cl)
                    drawCircle(mezcla(P.LUNA, P.CIELO_NOCHE, 0.18f), w * 0.007f, cl + Offset(-w * 0.007f, w * 0.005f))
                } else {
                    drawCircle(P.LUNA.copy(alpha = 0.85f), 1f * u, Offset(cx - w * 0.005f, sTop + w * 0.05f))
                    drawCircle(P.LUNA.copy(alpha = 0.6f), 0.8f * u, Offset(cx + w * 0.006f, sTop + w * 0.1f))
                }
            }
            a.noche -> {
                drawRect(P.CREPUSCULO_ALTO, r.topLeft, r.size)
                drawRect(mezcla(P.CREPUSCULO_ALTO, P.CREPUSCULO, 0.5f), Offset(sx0, sTop + r.height * 0.45f), Size(r.width, r.height))
                drawRect(P.CREPUSCULO, Offset(sx0, sTop + r.height * 0.72f), Size(r.width, r.height))
            }
            else -> {
                drawRect(P.CIELO, r.topLeft, r.size)
                drawRect(Color.White.copy(alpha = 0.7f), r.topLeft, Size(r.width, r.height * 0.55f))
            }
        }
    }

    // ---------------------------------------------------------------- el rayo

    /** Dónde cae el rayo sobre el tablero: de [CAE_DESDE] a [CAE_HASTA] anchos (encima está el registro de Cuaresma). */
    const val CAE_DESDE = 0.27f
    const val CAE_HASTA = 0.5f

    /**
     * El rayo que entra por la saetera y cae sesgado sobre el tablero y lo que hay en él, con su mancha de luz con la forma de
     * la saetera: de día, de sol; al anochecer, un resto naranja; en los Sueños de noche, el de la luna. De noche sin Sueños
     * no hay. El polvo que flota en él va aparte, encima de todo ([polvo]).
     */
    fun DrawScope.rayo(a: AireMedieval, apoyo: Float) {
        if (a.oscuro && !a.noche) return
        val u = density; val w = size.width
        val cx = w * M.VENT_X
        val color = when { a.oscuro -> P.LUNA; a.noche -> P.CREPUSCULO; else -> P.SOL }
        val f = when { a.oscuro -> 0.75f; a.noche -> 0.6f; else -> 1.6f }
        val d0 = w * CAE_DESDE; val d1 = w * CAE_HASTA
        val haz = poligonoMedieval(cx + w * 0.013f, w * 0.13f, cx + w * 0.015f, w * 0.15f, d1, apoyo, d0, apoyo, cx - w * 0.013f, w * 0.26f)
        drawPath(haz, color.copy(alpha = 0.13f * f))
        drawPath(poligonoMedieval(cx + w * 0.006f, w * 0.15f, d1 - w * 0.04f, apoyo, d0 + w * 0.04f, apoyo, cx - w * 0.007f, w * 0.24f), color.copy(alpha = 0.12f * f))
        drawPath(poligonoMedieval(cx + w * 0.002f, w * 0.17f, d1 - w * 0.08f, apoyo, d0 + w * 0.08f, apoyo, cx - w * 0.003f, w * 0.22f), color.copy(alpha = 0.1f * f))
        // Unas hebras más claras a lo largo del rayo.
        for (i in 0 until 3) {
            val t = 0.25f + 0.25f * i
            drawLine(color.copy(alpha = 0.09f * f), Offset(cx + w * 0.02f * (t - 0.5f), w * (0.15f + 0.1f * t)), Offset(d0 + (d1 - d0) * t, apoyo), (2f + 3f * azar(1335, i)) * u)
        }
        // La mancha en la cara del tablero: la saetera estirada, con su punta redonda al fondo.
        val mancha = Path().apply {
            moveTo(d0, apoyo + w * 0.008f)
            lineTo(d0 + w * 0.03f, apoyo - w * 0.007f)
            lineTo(d1 - w * 0.03f, apoyo - w * 0.007f)
            cubicTo(d1 + w * 0.005f, apoyo - w * 0.007f, d1 + w * 0.01f, apoyo + w * 0.008f, d1 - w * 0.012f, apoyo + w * 0.008f)
            close()
        }
        drawPath(mancha, color.copy(alpha = (0.42f * f).coerceAtMost(0.66f)))
    }

    /** El polvo que flota en el rayo, encima de lo que hay en el tablero. */
    fun DrawScope.polvo(a: AireMedieval, apoyo: Float) {
        if (a.oscuro && !a.noche) return
        val u = density; val w = size.width
        val cx = w * M.VENT_X
        val color = if (a.oscuro) P.LUNA else if (a.noche) P.CREPUSCULO else Color.White
        val cuantas = if (!a.oscuro && !a.noche) 26 else 10
        for (i in 0 until cuantas) {
            val t = azar(1331, i)
            val x = cx + (w * (CAE_DESDE + CAE_HASTA) / 2 - cx) * t + w * 0.07f * t * (azar(1332, i) - 0.5f)
            val y = w * 0.2f + (apoyo - w * 0.21f) * t
            drawCircle(color.copy(alpha = 0.35f + 0.45f * azar(1333, i)), (0.6f + 1f * azar(1334, i)) * u, Offset(x, y))
        }
    }

    // ---------------------------------------------------------------- el suelo

    /**
     * El entarimado de pino: tablas anchas que vienen hacia quien mira, con sus juntas abiertas, la veta, los clavos de dos
     * en dos sobre las vigas, algún nudo y el paso gastado; la sombra fría del armario.
     */
    fun DrawScope.entarimado(a: AireMedieval, suelo: Float, w: Float, h: Float, cerca: (Offset) -> Float) {
        val u = density
        val alto = h - suelo
        if (alto < 1f) return
        val k = 0.6f * cerca(Offset(w * 0.8f, suelo))
        drawRect(a.luz(mezcla(P.PINO, P.NOGAL_OSC, 0.4f), k), Offset(0f, suelo), Size(w, alto))
        val fuga = Offset(w / 2, suelo - alto * 2.4f)
        fun xEn(x0: Float, y: Float) = fuga.x + (x0 - fuga.x) * (y - fuga.y) / (h - fuga.y)
        val tablas = 7
        for (i in 0..tablas) {
            val x0 = w * (-0.15f + 1.3f * i / tablas)
            val x1 = w * (-0.15f + 1.3f * (i + 1) / tablas)
            val p = poligonoMedieval(xEn(x0, suelo), suelo, xEn(x1, suelo), suelo, x1, h, x0, h)
            drawPath(p, a.luz(mezcla(P.PINO, P.NOGAL, 0.1f + 0.35f * azar(1471, i)), k))
            // La veta, que sigue la tabla.
            for (v in 0 until 4) {
                val t = 0.15f + 0.22f * v + 0.06f * azar(1472, i * 5 + v)
                drawLine(a.sombra(0.12f), Offset(xEn(x0 + (x1 - x0) * t, suelo), suelo), Offset(x0 + (x1 - x0) * (t + 0.03f), h), 0.7f * u)
            }
            if (azar(1473, i) > 0.5f) {
                val t = 0.3f + 0.4f * azar(1474, i); val yv = suelo + alto * (0.3f + 0.4f * azar(1475, i))
                val xv = xEn(x0 + (x1 - x0) * t, yv)
                drawOval(a.sombra(0.3f), Offset(xv - 2f * u, yv - 1.2f * u), Size(4f * u, 2.4f * u))
            }
            // La junta abierta y su canto.
            drawLine(a.frio(0.55f), Offset(xEn(x0, suelo), suelo), Offset(x0, h), 1.5f * u)
            drawLine(a.claro(0.2f), Offset(xEn(x0, suelo) + 1.4f * u, suelo), Offset(x0 + 1.8f * u, h), 0.7f * u)
            // Los clavos, de dos en dos, sobre dos vigas.
            for (yf in listOf(0.38f, 0.82f)) {
                val yc = suelo + alto * yf
                val xa = xEn(x0, yc); val xb = xEn(x1, yc)
                for (t in listOf(0.22f, 0.78f)) clavoMedieval(Offset(xa + (xb - xa) * t, yc), (0.8f + 0.4f * yf) * u, a.luz(P.HIERRO, k), a)
            }
        }
        // El paso gastado delante del armario.
        drawOval(a.claro(if (a.oscuro) 0.04f else 0.1f), Offset(w * 0.25f, suelo + alto * 0.35f), Size(w * 0.5f, alto * 0.5f))
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(P.PINO, P.PIEDRA_CLARA, 0.3f), k), 1476, 18, u, if (a.oscuro) 0.04f else 0.1f)
        val c = if (a.oscuro) -0.04f else 0.06f
        drawPath(poligonoMedieval(0f, suelo, w, suelo, w + w * c, suelo + alto * 0.3f, w * c, suelo + alto * 0.3f), a.frio(if (a.oscuro) 0.45f else 0.28f))
        drawRect(a.frio(0.4f), Offset(0f, suelo), Size(w, 2.2f * u))
    }
}
