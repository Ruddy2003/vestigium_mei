package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.SOMBRA_MUEBLE
import io.github.ruddy2003.vestigium.ui.muebles.luzMueble
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * Lo que rodea al armario del estante de Scriptorium ([MuebleEstanteMedieval]): el muro de sillarejo de arenisca rosada, la
 * ventana abocinada con su saetera, el rayo que entra por ella y el entarimado de pino.
 */
internal object MuroEstanteMedieval {
    private val P = PaletaMedieval
    private val M = MuebleEstanteMedieval

    /**
     * El muro de sillarejo de arenisca rosada: hiladas de alto desigual, piedras desbastadas de esquinas mordidas, con su
     * tono, la veta de óxido en alguna y el mortero de cal, que de noche toma la luz del candil por zonas.
     */
    fun DrawScope.muro(a: AireMedieval, alto: Float, cerca: (Offset) -> Float) {
        val u = density; val w = size.width
        // El mortero, por zonas para que la luz del candil se reparta.
        val zx = if (a.lumbre) 10 else 1; val zy = if (a.lumbre) 4 else 1
        for (i in 0 until zx) for (j in 0 until zy) {
            val r = Rect(w * i / zx, alto * j / zy, w * (i + 1) / zx + 0.6f, alto * (j + 1) / zy + 0.6f)
            drawRect(a.luz(P.MORTERO, 0.7f * cerca(r.center)), r.topLeft, r.size)
        }
        var y = -w * 0.03f; var fila = 0
        while (y < alto) {
            val hil = w * (0.065f + 0.05f * azar(1301, fila))
            var x = -w * 0.14f * azar(1302, fila)
            var j = 0
            while (x < w) {
                val largo = w * (0.075f + 0.13f * azar(1303 + fila, j))
                val s = fila * 37 + j
                val baja = hil * 0.18f * azar(s, 40)
                val r = Rect(x + 1.9f * u, y + 1.7f * u + baja, x + largo - 1.9f * u, minOf(y + hil, alto + w * 0.03f) - 1.7f * u)
                if (r.height > 2f && r.width > 2f) piedra(r, s, a, 0.8f * cerca(r.center))
                // El ripio: una piedra chica calzada en la junta, arriba de las que bajan.
                if (baja > hil * 0.1f && r.width > w * 0.08f) {
                    val q = Rect(r.left + r.width * 0.2f, y + 1.2f * u, r.left + r.width * (0.35f + 0.2f * azar(s, 41)), r.top - 1.2f * u)
                    if (q.height > 2f * u) piedra(q, s + 9001, a, 0.8f * cerca(q.center))
                }
                x += largo; j++
            }
            y += hil; fila++
        }
        pinceladas(Rect(0f, 0f, w, alto), a.luz(P.ARENISCA_OSC, 0.2f), 1311, 34, u, if (a.oscuro) 0.05f else 0.08f)
        // La luz de día baja de arriba a la izquierda (la ventana y la galería que no se ve).
        if (!a.oscuro) mancha(Offset(w * 0.12f, w * 0.06f), w * 0.8f, luzMueble(if (a.noche) 0.08f else 0.2f))
        // El hollín que el candil ha dejado encima de donde cuelga.
        mancha(Offset(w * M.LAMP_X, w * (M.LAMP_Y - 0.12f)), w * 0.09f, a.sombra(if (a.oscuro) 0.3f else 0.22f))
    }

    /** Una piedra de sillarejo: desbastada, de esquinas mordidas, con su tono, la luz arriba a la izquierda y la sombra abajo. */
    private fun DrawScope.piedra(r: Rect, s: Int, a: AireMedieval, k: Float) {
        val u = density
        val c = minOf(r.width, r.height) * 0.2f
        fun j(i: Int) = c * (0.3f + 0.9f * azar(s, i))
        fun m(i: Int) = minOf(r.width, r.height) * 0.12f * (azar(s, i) - 0.3f)
        // Doce puntos: las esquinas mordidas y los lados que se abomban o se hunden un poco.
        val pts = floatArrayOf(
            r.left + j(1), r.top + 0.8f * u * azar(s, 2), r.center.x + r.width * 0.1f * (azar(s, 50) - 0.5f), r.top - m(51),
            r.right - j(3), r.top + 0.8f * u * azar(s, 4), r.right - 0.6f * u * azar(s, 5), r.top + j(6),
            r.right + m(52), r.center.y, r.right - 0.8f * u * azar(s, 7), r.bottom - j(8),
            r.right - j(9), r.bottom, r.center.x + r.width * 0.1f * (azar(s, 53) - 0.5f), r.bottom + m(54),
            r.left + j(10), r.bottom - 0.6f * u * azar(s, 11), r.left + 0.7f * u * azar(s, 12), r.bottom - j(13),
            r.left - m(55), r.center.y, r.left, r.top + j(14),
        )
        val p = poligonoMedieval(*pts)
        val sorteo = azar(s, 15)
        val otro = when { sorteo > 0.7f -> P.ARENISCA_CLARA; sorteo > 0.4f -> P.ARENISCA_OSC; sorteo > 0.2f -> P.MORTERO; else -> P.RUBRICA }
        val tono = mezcla(P.ARENISCA, otro, if (otro == P.RUBRICA) 0.12f + 0.1f * azar(s, 16) else 0.15f + 0.5f * azar(s, 16))
        drawPath(p, a.luz(tono, k))
        clipPath(p) {
            // La cara de la piedra: más honda hacia abajo y a la derecha; una mancha de óxido o de liquen en alguna.
            drawPath(poligonoMedieval(r.left + r.width * 0.35f, r.bottom, r.right, r.top + r.height * 0.4f, r.right, r.bottom), a.sombra(0.12f))
            drawPath(poligonoMedieval(r.left, r.top, r.left + r.width * 0.55f, r.top, r.left, r.top + r.height * 0.6f), a.claro(0.08f))
            if (azar(s, 24) > 0.7f) {
                val c = Offset(r.left + r.width * azar(s, 25), r.top + r.height * azar(s, 26))
                drawOval(a.luz(mezcla(P.ARENISCA_OSC, P.RUBRICA, 0.35f), k).copy(alpha = 0.35f), Offset(c.x - r.width * 0.2f, c.y - r.height * 0.18f), Size(r.width * 0.4f, r.height * 0.36f))
            }
        }
        // El picado del desbaste.
        for (i in 0 until 5) drawCircle(a.sombra(0.18f), (0.5f + 0.5f * azar(s, 40 + i)) * u, Offset(r.left + r.width * azar(s, 30 + i), r.top + r.height * azar(s, 34 + i)))
        // Los cantos: luz arriba e izquierda, sombra abajo y derecha.
        drawLine(a.claro(0.42f), Offset(pts[0], pts[1]), Offset(pts[2], pts[3]), 1.2f * u)
        drawLine(a.claro(0.42f), Offset(pts[2], pts[3]), Offset(pts[4], pts[5]), 1.2f * u)
        drawLine(a.claro(0.26f), Offset(pts[20], pts[21]), Offset(pts[22], pts[23]), 1f * u)
        drawLine(a.sombra(0.38f), Offset(pts[12], pts[13]), Offset(pts[14], pts[15]), 1.6f * u)
        drawLine(a.sombra(0.38f), Offset(pts[14], pts[15]), Offset(pts[16], pts[17]), 1.6f * u)
        drawLine(a.sombra(0.24f), Offset(pts[8], pts[9]), Offset(pts[10], pts[11]), 1.2f * u)
    }

    /**
     * La ventana abocinada: la saetera de medio punto al fondo del derrame encalado, enmarcado por sillares mejor labrados. De
     * día el cielo blanco y el derrame de la derecha al sol; al anochecer, el cielo de fuego; de noche, azul con dos estrellas;
     * en los Sueños, la luna asoma por la saetera.
     */
    fun DrawScope.ventana(a: AireMedieval) {
        val u = density; val w = size.width
        val cx = w * M.VENT_X
        val ex0 = cx - w * 0.098f; val ex1 = cx + w * 0.098f; val eTop = w * 0.04f; val eSill = w * 0.37f
        val sx0 = cx - w * 0.019f; val sx1 = cx + w * 0.019f; val sTop = w * 0.11f; val sBot = w * 0.305f
        val eSpring = eTop + (ex1 - ex0) / 2; val sSpring = sTop + (sx1 - sx0) / 2
        val k = if (a.lumbre) M.alcanceMuro(M.VENT_X - M.LAMP_X, 0.2f - M.LAMP_Y) else 1f
        // Los sillares que enmarcan el vano: un anillo de dovelas y las jambas.
        val marco = medioPuntoMedieval(ex0 - w * 0.03f, ex1 + w * 0.03f, eTop - w * 0.03f, eSill + w * 0.012f)
        drawPath(marco, a.sombra(if (a.oscuro) 0.3f else 0.22f), style = Stroke(2.2f * u))
        drawPath(marco, a.luz(mezcla(P.ARENISCA_CLARA, P.MORTERO, 0.25f), k))
        clipPath(marco) {
            // Las juntas del marco: radiales en el arco, horizontales en las jambas.
            for (i in 1 until 7) {
                val ang = Math.PI * i / 7
                val r0 = (ex1 - ex0) / 2; val r1 = r0 + w * 0.03f
                val c0 = Offset(cx, eSpring)
                drawLine(a.sombra(0.4f), c0 + Offset((-r0 * kotlin.math.cos(ang)).toFloat(), (-r0 * kotlin.math.sin(ang)).toFloat()),
                    c0 + Offset((-r1 * kotlin.math.cos(ang)).toFloat(), (-r1 * kotlin.math.sin(ang)).toFloat()), 1f * u)
            }
            var yj = eSpring + w * 0.045f
            while (yj < eSill) {
                drawLine(a.sombra(0.4f), Offset(ex0 - w * 0.03f, yj), Offset(ex0, yj), 1f * u)
                drawLine(a.sombra(0.4f), Offset(ex1, yj + w * 0.02f), Offset(ex1 + w * 0.03f, yj + w * 0.02f), 1f * u)
                yj += w * 0.052f
            }
            drawRect(a.claro(0.3f), Offset(ex0 - w * 0.03f, eTop - w * 0.03f), Size(w * 0.012f, eSill))
        }
        // El derrame: la cal del intradós, las dos caras y el alféizar en rampa.
        val derrame = medioPuntoMedieval(ex0, ex1, eTop, eSill)
        // Los tonos de la cal: el intradós, la cara izquierda (en sombra), la derecha (al sol o a la luna) y el alféizar.
        val tonos: List<Color> = when {
            a.oscuro && a.noche -> listOf(mezcla(P.CAL, P.NOCHE_SUENOS, 0.74f), mezcla(P.CAL, P.NOCHE_SUENOS, 0.84f),
                mezcla(mezcla(P.CAL, P.CIELO_NOCHE, 0.5f), P.LUNA, 0.2f), mezcla(P.CAL, P.CIELO_NOCHE, 0.6f))
            a.oscuro -> listOf(mezcla(P.CAL, P.NOCHE, 0.8f), mezcla(P.CAL, P.NOCHE, 0.87f), mezcla(P.CAL, P.CIELO_NOCHE, 0.74f), mezcla(P.CAL, P.CIELO_NOCHE, 0.8f))
            a.noche -> listOf(mezcla(P.CAL, P.ANOCHECER, 0.4f), mezcla(P.CAL, P.ANOCHECER, 0.55f), mezcla(P.CAL, P.CREPUSCULO, 0.4f), mezcla(P.CAL, P.CREPUSCULO, 0.3f))
            else -> listOf(mezcla(P.CAL, P.ARENISCA_OSC, 0.22f), mezcla(P.CAL, SOMBRA_MUEBLE, 0.3f), mezcla(P.CAL, P.FUEGO, 0.3f), mezcla(P.CAL, P.FUEGO, 0.55f))
        }
        drawPath(derrame, tonos[0])
        val izq = poligonoMedieval(ex0, eSpring, ex0, eSill, sx0, sBot, sx0, sSpring)
        val der = poligonoMedieval(ex1, eSpring, ex1, eSill, sx1, sBot, sx1, sSpring)
        val piso = poligonoMedieval(ex0, eSill, ex1, eSill, sx1, sBot, sx0, sBot)
        drawPath(izq, tonos[1])
        drawPath(der, tonos[2])
        drawPath(piso, tonos[3])
        // Las aristas del derrame y la sombra del intradós sobre la cara izquierda.
        drawLine(a.sombra(0.3f), Offset(ex0, eSpring), Offset(sx0, sSpring), 0.8f * u)
        drawLine(a.claro(0.3f), Offset(ex1, eSpring), Offset(sx1, sSpring), 0.8f * u)
        drawLine(a.sombra(0.25f), Offset(ex0, eSill), Offset(sx0, sBot), 0.8f * u)
        drawLine(a.claro(0.35f), Offset(ex1, eSill), Offset(sx1, sBot), 0.8f * u)
        clipPath(izq) {
            drawRect(a.sombra(0.16f), Offset(ex0, eSpring), Size(sx0 - ex0, (eSill - eSpring) * 0.3f))
            // La cara se va oscureciendo hacia la saetera, y la piedra se adivina bajo la cal.
            drawPath(poligonoMedieval(ex0 + (sx0 - ex0) * 0.55f, eSpring, sx0, sSpring, sx0, sBot, ex0 + (sx0 - ex0) * 0.55f, eSill), a.sombra(0.12f))
            for (i in 0 until 3) drawLine(a.sombra(0.08f), Offset(ex0, eSpring + (eSill - eSpring) * (0.25f + 0.25f * i)), Offset(sx0, sSpring + (sBot - sSpring) * (0.25f + 0.25f * i)), 0.8f * u)
        }
        clipPath(der) {
            drawPath(poligonoMedieval(ex1 - (ex1 - sx1) * 0.5f, eSpring, sx1, sSpring, sx1, sBot, ex1 - (ex1 - sx1) * 0.5f, eSill), a.sombra(0.07f))
            for (i in 0 until 3) drawLine(a.sombra(0.06f), Offset(ex1, eSpring + (eSill - eSpring) * (0.2f + 0.27f * i)), Offset(sx1, sSpring + (sBot - sSpring) * (0.2f + 0.27f * i)), 0.8f * u)
        }
        pinceladas(Rect(ex0, eTop, ex1, eSill), a.sombra(1f), 1321, 12, u, 0.05f)
        // La saetera y su cielo.
        val luz = medioPuntoMedieval(sx0, sx1, sTop, sBot)
        clipPath(luz) {
            when {
                a.oscuro -> {
                    drawRect(if (a.noche) mezcla(P.CIELO_NOCHE, P.LUNA, 0.18f) else P.CIELO_NOCHE, Offset(sx0, sTop), Size(sx1 - sx0, sBot - sTop))
                    if (a.noche) {
                        val cl = Offset(cx + w * 0.012f, sTop + w * 0.05f)
                        drawCircle(P.LUNA.copy(alpha = 0.25f), w * 0.04f, cl)
                        drawCircle(P.LUNA, w * 0.026f, cl)
                        drawCircle(mezcla(P.LUNA, P.CIELO_NOCHE, 0.18f), w * 0.008f, cl + Offset(-w * 0.008f, w * 0.006f))
                    } else {
                        drawCircle(P.LUNA.copy(alpha = 0.85f), 1.1f * u, Offset(cx - w * 0.006f, sTop + w * 0.06f))
                        drawCircle(P.LUNA.copy(alpha = 0.6f), 0.8f * u, Offset(cx + w * 0.009f, sTop + w * 0.13f))
                    }
                }
                a.noche -> {
                    drawRect(P.CREPUSCULO_ALTO, Offset(sx0, sTop), Size(sx1 - sx0, sBot - sTop))
                    drawRect(mezcla(P.CREPUSCULO_ALTO, P.CREPUSCULO, 0.5f), Offset(sx0, sTop + (sBot - sTop) * 0.45f), Size(sx1 - sx0, sBot - sTop))
                    drawRect(P.CREPUSCULO, Offset(sx0, sTop + (sBot - sTop) * 0.72f), Size(sx1 - sx0, sBot - sTop))
                }
                else -> {
                    drawRect(P.CIELO, Offset(sx0, sTop), Size(sx1 - sx0, sBot - sTop))
                    drawRect(Color.White.copy(alpha = 0.6f), Offset(sx0, sTop), Size(sx1 - sx0, (sBot - sTop) * 0.5f))
                }
            }
        }
        // El canto de la saetera: un filo de piedra en sombra a la izquierda.
        drawLine(a.sombra(0.35f), Offset(sx0, sSpring), Offset(sx0, sBot), 1.2f * u)
        if (!a.oscuro) mancha(Offset(cx, (sTop + sBot) / 2), w * 0.09f, Color.White.copy(alpha = if (a.noche) 0.12f else 0.3f))
        else if (a.noche) mancha(Offset(cx, sTop + w * 0.05f), w * 0.12f, P.LUNA.copy(alpha = 0.16f))
    }

    /**
     * El rayo que entra por la saetera y cae sobre el tablero de arriba, donde está el libro: de día, de sol, con su polvo
     * en el aire; al anochecer, un resto naranja; en los Sueños de noche, el de la luna. De noche sin Sueños no hay.
     */
    fun DrawScope.rayo(a: AireMedieval, apoyo: Float) {
        if (a.oscuro && !a.noche) return
        val u = density; val w = size.width
        val cx = w * M.VENT_X
        val color = when {
            a.oscuro -> P.LUNA
            a.noche -> P.CREPUSCULO
            else -> Color(0xFFFFF3D2)
        }
        val f = when { a.oscuro -> 0.7f; a.noche -> 0.6f; else -> 1.5f }
        val haz = poligonoMedieval(cx + w * 0.014f, w * 0.12f, cx + w * 0.019f, w * 0.15f, w * 0.6f, apoyo, w * 0.34f, apoyo, cx - w * 0.019f, w * 0.3f)
        drawPath(haz, color.copy(alpha = 0.16f * f))
        drawPath(poligonoMedieval(cx + w * 0.006f, w * 0.16f, w * 0.54f, apoyo, w * 0.4f, apoyo, cx - w * 0.008f, w * 0.27f), color.copy(alpha = 0.12f * f))
        drawPath(poligonoMedieval(cx + w * 0.002f, w * 0.19f, w * 0.5f, apoyo, w * 0.44f, apoyo, cx - w * 0.004f, w * 0.24f), color.copy(alpha = 0.08f * f))
        // Unas hebras más claras a lo largo del rayo.
        for (i in 0 until 3) {
            val t = 0.25f + 0.25f * i
            val x0 = cx + w * 0.02f * (t - 0.5f); val y0 = w * (0.14f + 0.14f * t)
            val x1 = w * (0.36f + 0.22f * t)
            drawLine(color.copy(alpha = 0.1f * f), Offset(x0, y0), Offset(x1, apoyo), (2f + 3f * azar(1335, i)) * u)
        }
        // Donde cae: la mancha en la cara del tablero.
        drawPath(poligonoMedieval(w * 0.335f, apoyo - w * 0.006f, w * 0.6f, apoyo - w * 0.006f, w * 0.61f, apoyo + w * 0.007f, w * 0.325f, apoyo + w * 0.007f), color.copy(alpha = (0.4f * f).coerceAtMost(0.6f)))
        // El polvo que flota en el rayo.
        if (!a.oscuro && !a.noche) for (i in 0 until 14) {
            val t = azar(1331, i)
            val x = cx + (w * 0.44f - cx) * t + w * 0.06f * (azar(1332, i) - 0.5f)
            val y = w * 0.2f + (apoyo - w * 0.2f) * t
            drawCircle(Color.White.copy(alpha = 0.5f * azar(1333, i)), (0.6f + 0.7f * azar(1334, i)) * u, Offset(x, y))
        }
    }


    /** El entarimado de pino: tablas que vienen hacia quien mira, con sus juntas, clavos y nudos, y la sombra del armario. */
    fun DrawScope.entarimado(a: AireMedieval, suelo: Float, w: Float, h: Float, cerca: (Offset) -> Float) {
        val u = density
        val alto = h - suelo
        if (alto < 1f) return
        val k = 0.6f * cerca(Offset(w * 0.8f, suelo))
        drawRect(a.luz(mezcla(P.PINO, P.NOGAL_OSC, 0.25f), k), Offset(0f, suelo), Size(w, alto))
        // Las tablas huyen hacia un punto alto en medio.
        val fuga = Offset(w / 2, suelo - alto * 2.4f)
        fun xEn(x0: Float, y: Float) = fuga.x + (x0 - fuga.x) * (y - fuga.y) / (h - fuga.y)
        val tablas = 8
        for (i in 0..tablas) {
            val x0 = w * (-0.15f + 1.3f * i / tablas)
            val x1 = w * (-0.15f + 1.3f * (i + 1) / tablas)
            val p = poligonoMedieval(xEn(x0, suelo), suelo, xEn(x1, suelo), suelo, x1, h, x0, h)
            drawPath(p, a.luz(mezcla(P.PINO, P.NOGAL, 0.15f + 0.3f * azar(1471, i)), k))
            drawLine(a.sombra(0.45f), Offset(xEn(x0, suelo), suelo), Offset(x0, h), 1.1f * u)
            drawLine(a.claro(0.18f), Offset(xEn(x0, suelo) + 1.2f * u, suelo), Offset(x0 + 1.6f * u, h), 0.7f * u)
            // Los clavos de la tabla, sobre la viga que no se ve.
            val yc = suelo + alto * 0.62f
            clavoMedieval(Offset(xEn(x0, yc) + (xEn(x1, yc) - xEn(x0, yc)) * 0.2f, yc), 0.9f * u, a.luz(P.HIERRO, k), a)
        }
        pinceladas(Rect(0f, suelo, w, h), a.luz(mezcla(P.PINO, P.ARENISCA_CLARA, 0.3f), k), 1472, 18, u, if (a.oscuro) 0.04f else 0.1f)
        // La sombra del armario en el suelo, corrida a la derecha de día.
        val c = if (a.oscuro) -0.04f else 0.06f
        drawPath(poligonoMedieval(0f, suelo, w, suelo, w + w * c, suelo + alto * 0.34f, w * c, suelo + alto * 0.34f), a.sombra(if (a.oscuro) 0.45f else 0.26f))
        drawRect(a.sombra(0.35f), Offset(0f, suelo), Size(w, 2.2f * u))
    }
}
