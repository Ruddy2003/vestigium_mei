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
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.ui.Letra
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.granoMueble
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los colores del pasaplatos del reparto (la libreta de Ciudad neón), además de los de la casa ([PaletaNeon]: el azulejo jade, la
 * laca, el latón, el petróleo): la lámpara de calor y el tubo de la nevera; el acero de la encimera; el papel térmico de los
 * tickets (blanco y sus copias rosa, amarilla y azul) y su impresión; la goma de los fajos.
 */
internal object PaletaLibretaNeon {
    /** La bombilla roja de la lámpara de calor: su luz, rojo anaranjada, y su alma. */
    val CALOR = Color(0xFFFF6236)
    val CALOR_ALMA = Color(0xFFFFD6A0)
    /** El tubo de la nevera: cian blanquecino. */
    val FRIO = Color(0xFF9FE6FF)
    val ACERO = Color(0xFFAEB6C3)
    val ACERO_LUZ = Color(0xFFE6EAF1)
    val ACERO_OSC = Color(0xFF59606E)
    val PAPEL = Color(0xFFEDE9DF)
    val COPIAS = listOf(Color(0xFFEDE9DF), Color(0xFFF0C6D5), Color(0xFFEDE9DF), Color(0xFFF0E1A4), Color(0xFFEDE9DF), Color(0xFFC4DCF0))
    val IMPRESO = Color(0xFF231B2B)
    val GOMA = Color(0xFFC8302C)
    val HIERRO = Color(0xFF2E2C35)
}

/**
 * La luz del pasaplatos, en píxeles: la lámpara de calor ([lampara], la bombilla), que alumbra hacia abajo con un charco ancho y
 * casi nada hacia arriba, y la nevera de la izquierda (su borde derecho en [xNevera]), que echa su luz fría hacia la derecha y
 * llega poco debajo de la encimera ([suelo]). Lo demás se hunde en el petróleo casi negro del cuarto ([AireNeon.hondo]).
 */
internal class LuzLibretaNeon(val h: Float, val lampara: Offset, val xNevera: Float, val suelo: Float) {
    val aire = AireNeon(false)

    /** Lo que llega de la lámpara de calor a [p]. */
    fun kCalor(p: Offset): Float {
        val dx = p.x - lampara.x; val dy = p.y - lampara.y
        val dyE = if (dy < 0f) dy * 2.6f else dy * 0.8f
        return 1.3f * exp(-sqrt(dx * dx + dyE * dyE) / (0.34f * h))
    }

    /** Lo que llega de la nevera a [p]. */
    fun kFrio(p: Offset): Float = 0.75f * exp(-max(0f, p.x - xNevera) / (0.24f * h)) * (if (p.y > suelo) 0.5f else 1f)

    /** El tono con solo la luz de la nevera (el jade de la pared lo tiñe la lámpara aparte, sin apagarlo). */
    fun tFrio(p: Offset): TonoNeon = TonoNeon(aire, kFrio(p), PaletaLibretaNeon.FRIO, -1f)

    fun t(p: Offset): TonoNeon {
        val kc = kCalor(p)
        val kf = kFrio(p)
        val k = kc + kf
        if (k < 1e-4f) return TonoNeon(aire, 0f, PaletaLibretaNeon.CALOR, 1f)
        val c = PaletaLibretaNeon.CALOR; val f = PaletaLibretaNeon.FRIO
        val l = Color((c.red * kc + f.red * kf) / k, (c.green * kc + f.green * kf) / k, (c.blue * kc + f.blue * kf) / k)
        val lado = kc * (if (lampara.x > p.x) 1f else -1f) - kf
        return TonoNeon(aire, k.coerceAtMost(1.3f), l, if (lado >= 0f) 1f else -1f)
    }

    fun t(x: Float, y: Float): TonoNeon = t(Offset(x, y))
}

/**
 * Dónde va cada cosa en un lienzo de [w] × [h] (el alto es fijo; el ancho cambia con el teléfono y el rincón se estira a lo
 * ancho sin deformar el pincho): la encimera (su arista de atrás, contra la pared; su canto de delante y el pie del canto), la
 * nevera, el pincho, la lámpara, el timbre y la bolsa.
 */
internal class EscenaLibretaNeon(val w: Float, val h: Float, val u: Float) {
    val atras = h * 0.755f
    val canto = h * 0.84f
    val frente = h * 0.872f
    val xNevera = h * 0.27f
    val xPincho = maxOf(w * 0.47f, xNevera + h * 0.3f)
    val yPincho = h * 0.808f
    val lampara = Offset(minOf(xPincho + h * 0.42f, w - h * 0.25f), h * 0.37f)
    val xTimbre = minOf(lampara.x + h * 0.06f, w - h * 0.26f)
    val xBolsa = w - h * 0.11f
    val luz = LuzLibretaNeon(h, lampara, xNevera, frente)
}

/**
 * La libreta de momentos de Ciudad neón (la usa `EstanteNeon`, en `BibliotecaFuturo.kt`): el pasaplatos del reparto, abajo, en el
 * puesto de la misma casa del Ideario y del estante. Contra el azulejo jade, con la tubería de cobre que lo cruza, sobre la
 * encimera de acero, el pincho de tickets de hierro colado con su púa: los tickets térmicos ensartados en una pila que engorda
 * con las hojas (cuando son muchos, los viejos quedan en fajos con su goma) y, arriba, el último ticket ensartado por su agujero
 * y caído hacia delante, con el nombre y la cuenta impresos. Cuelga de la tubería la lámpara de calor del pasaplatos (la luz
 * cálida: su charco rojo anaranjado); a la izquierda, cortada por el borde, la nevera de bebidas de puerta de vidrio (la luz
 * fría). Al lado, el timbre de mostrador, el bote de los rotuladores, la bolsa de un pedido y el trapo. El tema es solo de
 * noche: el dibujo es el mismo con [oscuro] o sin él. Al abrirse, el último ticket sube por la púa, se suelta y se ladea.
 * Lo fijo (todo menos el pincho) se pinta una vez por tamaño en una imagen guardada (`MuebleLibretaNeonRincon.kt`).
 */
internal object MuebleLibretaNeon {
    val ALTO: Dp = 266.dp

    private var clave = ""
    private var imagen: ImageBitmap? = null

    fun DrawScope.libreta(e: EstadoLibreta, medidor: TextMeasurer) {
        val w = size.width; val h = size.height
        if (w < 8f || h < 8f) return
        val u = density
        val g = EscenaLibretaNeon(w, h, u)
        clipRect(0f, 0f, w, h) {
            fija(g)
            pincho(e, medidor, g)
        }
    }

    /** El rincón sin el pincho, pintado una vez por tamaño y guardado (unos 2.8 MB en un teléfono de 1080 px). */
    private fun DrawScope.fija(g: EscenaLibretaNeon) {
        val ancho = g.w.roundToInt(); val alto = g.h.roundToInt()
        val k = "$ancho|$alto|$density|$fontScale"
        var img = imagen
        if (img == null || clave != k) {
            val nueva = ImageBitmap(ancho, alto)
            CanvasDrawScope().draw(Density(density, fontScale), layoutDirection, Canvas(nueva), Size(ancho.toFloat(), alto.toFloat())) {
                with(RinconLibretaNeon) { rincon(g) }
                granoMueble(Rect(0f, 0f, g.w, g.h), true)
            }
            imagen = nueva; clave = k; img = nueva
        }
        drawImage(img)
    }

    // ---------------------------------------------------------------- el pincho

    /**
     * El pincho: la sombra que echa en la encimera y en la pared, los fajos de tickets viejos, la base de hierro colado, la pila
     * de tickets ensartados (cada uno un poco girado, con su color de copia y la luz que le toca), la púa y el último ticket.
     */
    private fun DrawScope.pincho(e: EstadoLibreta, medidor: TextMeasurer, g: EscenaLibretaNeon) {
        val u = g.u; val h = g.h
        val P = PaletaLibretaNeon
        val ab = e.abierto.coerceIn(0f, 1f)
        val cx = g.xPincho
        val pie = g.yPincho
        val rx = h * 0.15f; val ry = h * 0.045f; val canto = h * 0.055f
        val tb = g.luz.t(cx, pie - h * 0.02f)
        // La sombra en la encimera, hacia donde no da la lámpara (a la izquierda y un poco hacia delante).
        drawOval(tb.s(0.4f), Offset(cx - rx * 1.45f, pie - ry * 0.5f), Size(rx * 2.3f, ry * 1.8f))
        // La de la pila y la púa en la pared, hacia arriba a la izquierda (la lámpara queda a la derecha, más baja que la punta).
        val hojas = e.hojas.coerceAtLeast(0)
        val pila = ((0.02f + 0.022f * sqrt(hojas.toFloat())).coerceAtMost(0.39f)) * h
        val base = pie - canto
        drawPath(poligonoNeon(cx - h * 0.02f, g.atras, cx - h * 0.09f - pila * 0.2f, g.atras - pila * 0.6f - h * 0.05f,
            cx - h * 0.15f - pila * 0.25f, g.atras - pila * 0.55f - h * 0.04f, cx - h * 0.09f, g.atras), tb.s(0.3f))
        drawLine(tb.s(0.25f), Offset(cx - h * 0.04f, g.atras), Offset(cx - h * 0.3f, h * 0.08f), 2f * u)
        fajos(hojas, e.semilla, g)
        // La base de hierro colado: el canto (un cilindro bajo, con la luz de la lámpara a la derecha y la fría a la izquierda),
        // la cara de arriba con su moldura y el filo encendido.
        val hierro = mezcla(P.HIERRO, tb.c(Color(0xFF6A6474), 1.4f), 0.75f)
        val lado = mezcla(hierro, PaletaNeon.SOMBRA, 0.3f)
        drawOval(lado, Offset(cx - rx, pie - ry), Size(rx * 2, ry * 2))
        drawRect(lado, Offset(cx - rx, base), Size(rx * 2, canto))
        drawRect(g.luz.t(cx + rx, base).b(0.5f, 1.2f), Offset(cx + rx * 0.5f, base), Size(rx * 0.32f, canto))
        drawRect(g.luz.t(cx - rx, base).b(0.35f), Offset(cx - rx, base), Size(rx * 0.12f, canto))
        drawRect(tb.s(0.3f), Offset(cx - rx * 0.6f, base), Size(rx * 0.7f, canto))
        drawLine(tb.s(0.4f), Offset(cx - rx, base + canto * 0.55f), Offset(cx + rx, base + canto * 0.55f), 1f * u)
        drawOval(hierro, Offset(cx - rx, base - ry), Size(rx * 2, ry * 2))
        drawArc(P.CALOR_ALMA.copy(alpha = 0.7f), -70f, 140f, false, Offset(cx - rx, base - ry), Size(rx * 2, ry * 2), style = Stroke(1.4f * u))
        drawArc(P.FRIO.copy(alpha = 0.45f), 150f, 60f, false, Offset(cx - rx, base - ry), Size(rx * 2, ry * 2), style = Stroke(1f * u))
        drawOval(P.CALOR.copy(alpha = 0.22f), Offset(cx + rx * 0.05f, base - ry * 0.75f), Size(rx * 0.8f, ry * 0.9f))
        drawOval(tb.s(0.45f), Offset(cx - rx * 0.6f, base - ry * 0.6f), Size(rx * 1.2f, ry * 1.2f), style = Stroke(1.4f * u))
        drawOval(tb.b(0.3f), Offset(cx - rx * 0.56f, base - ry * 0.68f), Size(rx * 1.12f, ry * 1.12f), style = Stroke(0.7f * u))
        // La pila de tickets ensartados: tiras de papel térmico (blancas, alguna copia rosa o amarilla), cada una apenas girada.
        val n = if (hojas <= 6) hojas else (6 + (hojas - 6) / 3).coerceAtMost(60)
        val paso = if (n > 0) pila / n else 0f
        val lx = h * 0.165f; val ly = h * 0.07f; val kd = 0.4f
        for (k in 0 until n) {
            val y = base - k * paso
            val suelto = azar(e.semilla, 60 + k) > 0.85f
            val giro = ((azar(e.semilla, 50 + k) - 0.5f) * (if (suelto) 0.7f else 0.22f)) * (1f - ab * 0.4f) + ab * ((k + 0.5f) / n - 0.5f) * 1.4f
            val co = cos(giro); val si = sin(giro)
            fun p(x: Float, z: Float) = Offset(cx + x * co - z * si, y - (x * si + z * co) * kd)
            val q0 = p(-lx, -ly); val q1 = p(lx, -ly); val q2 = p(lx, ly); val q3 = p(-lx, ly)
            val t = g.luz.t(cx + h * 0.05f, y)
            val copia = P.COPIAS[(k * 5 + (e.semilla and 7)) % P.COPIAS.size]
            val col = mezcla(copia, t.c(copia, 1.25f), 0.45f)
            // El canto de la pila (el grueso de cada ticket) y la cara de arriba.
            drawPath(poligonoNeon(q3.x, q3.y, q2.x, q2.y, q2.x, q2.y + paso + 0.6f * u, q3.x, q3.y + paso + 0.6f * u), mezcla(col, PaletaNeon.SOMBRA, 0.38f))
            drawPath(poligonoNeon(q0.x, q0.y, q1.x, q1.y, q2.x, q2.y, q3.x, q3.y), col)
            // El canto que mira a la lámpara, encendido; el de la nevera, frío; algún renglón impreso.
            if (k == n - 1 || k % 3 == 0) {
                drawLine(t.b(0.6f), q1, q2, 0.8f * u)
                drawLine(g.luz.t(cx - lx, y).b(0.3f), q3, q0, 0.7f * u)
                drawLine(t.s(0.3f), q3, q2, 0.6f * u)
                for (j in 0 until 2) {
                    val z = -ly * 0.4f + j * ly * 0.5f
                    drawLine(P.IMPRESO.copy(alpha = 0.3f), p(-lx * 0.7f, z), p(lx * (0.1f + 0.4f * azar(e.semilla, 90 + k + j)), z), 0.8f * u)
                }
            }
        }
        val cima = base - pila
        // La púa entera, de acero, con su filo de luz del lado de la lámpara: el último ticket la tapa desde su agujero hasta su
        // borde de arriba, y la punta asoma por encima.
        val punta = Offset(cx, h * 0.03f)
        val tp = g.luz.t(cx, h * 0.35f)
        drawLine(tp.s(0.45f), Offset(cx + 1.8f * u, cima), Offset(cx + 1.8f * u, punta.y + h * 0.02f), 2f * u)
        drawLine(tp.c(P.ACERO, 1.2f), Offset(cx, base), Offset(cx, punta.y), 2.6f * u, StrokeCap.Round)
        drawLine(tp.b(0.85f, 1.4f), Offset(cx + 0.7f * u, cima - 2f * u), Offset(cx + 0.7f * u, punta.y + 3f * u), 0.8f * u)
        drawLine(g.luz.t(cx - h * 0.1f, h * 0.35f).b(0.4f), Offset(cx - 0.8f * u, cima - 2f * u), Offset(cx - 0.8f * u, punta.y + 3f * u), 0.5f * u)
        drawCircle(tp.b(0.9f, 1.4f), 1f * u, punta + Offset(0.3f * u, 0.8f * u))
        ultimoTicket(e, medidor, g, ab, cx, cima, tp)
    }

    /** Los fajos de tickets viejos, con su goma, echados en la encimera a la derecha del pincho: uno por cada tanto de hojas. */
    private fun DrawScope.fajos(hojas: Int, semilla: Int, g: EscenaLibretaNeon) {
        val n = (hojas / 70).coerceIn(0, 3)
        if (n == 0) return
        val u = g.u; val h = g.h
        val P = PaletaLibretaNeon
        val x0 = g.xPincho + h * 0.16f
        for (j in 0 until n) {
            val c = Offset(x0 + h * 0.012f * (azar(semilla, 400 + j) - 0.5f), g.yPincho + h * 0.008f - j * h * 0.045f)
            val t = g.luz.t(c)
            val lx = h * 0.11f; val ly = h * 0.05f; val alto = h * 0.04f
            val giro = (azar(semilla, 410 + j) - 0.5f) * 0.5f
            val co = cos(giro); val si = sin(giro)
            fun p(x: Float, z: Float, y: Float) = Offset(c.x + x * co - z * si, c.y - y - (x * si + z * co) * 0.36f)
            drawOval(t.s(0.4f), Offset(c.x - lx * 1.2f, c.y - ly * 0.4f), Size(lx * 2.2f, ly * 1f))
            val papel = t.c(P.COPIAS[(j * 3 + 1) % P.COPIAS.size], 1.05f)
            // El canto (los bordes de cientos de tickets, a rayas) y la cara de arriba.
            val a0 = p(-lx, ly, 0f); val a1 = p(lx, ly, 0f); val b1 = p(lx, ly, alto); val b0 = p(-lx, ly, alto)
            drawPath(poligonoNeon(a0.x, a0.y, a1.x, a1.y, b1.x, b1.y, b0.x, b0.y), mezcla(papel, PaletaNeon.SOMBRA, 0.25f))
            for (r in 1 until 6) {
                val f = r / 6f
                drawLine(t.s(0.25f), Offset(a0.x + (b0.x - a0.x) * f, a0.y + (b0.y - a0.y) * f), Offset(a1.x + (b1.x - a1.x) * f, a1.y + (b1.y - a1.y) * f), 0.6f * u)
            }
            val c0 = p(-lx, -ly, alto); val c1 = p(lx, -ly, alto)
            drawPath(poligonoNeon(b0.x, b0.y, b1.x, b1.y, c1.x, c1.y, c0.x, c0.y), papel)
            drawLine(t.b(0.45f), b1, c1, 0.8f * u)
            // La goma, que los ciñe por el medio.
            val m0 = p(0f, ly, 0f); val m1 = p(0f, ly, alto); val m2 = p(0f, -ly, alto)
            val goma = t.c(P.GOMA, 1.1f)
            drawLine(goma, m0 + Offset(-1.2f * u, 0f), m1 + Offset(-1.2f * u, 0f), 2.2f * u)
            drawLine(goma, m1 + Offset(-1.2f * u, 0f), m2 + Offset(-1.2f * u, 0f), 2.2f * u)
            drawLine(t.b(0.4f), m1 + Offset(-0.4f * u, -0.6f * u), m2 + Offset(-0.4f * u, -0.6f * u), 0.6f * u)
        }
    }

    /** [texto] en una o dos líneas: si es largo, partido en el espacio más cerca de la mitad (o en « · », si lo tiene). */
    private fun partir(texto: String): List<String> {
        val punto = texto.indexOf(" · ")
        if (punto > 0) return listOf(texto.substring(0, punto), texto.substring(punto + 3))
        if (texto.length <= 11) return listOf(texto)
        val medio = texto.length / 2
        val c = texto.indices.filter { texto[it] == ' ' }.minByOrNull { kotlin.math.abs(it - medio) } ?: return listOf(texto)
        return listOf(texto.substring(0, c), texto.substring(c + 1))
    }

    /**
     * El último ticket, ENSARTADO: una tira de papel térmico estrecha y alta, atravesada por la púa a dos tercios de su alto (la
     * punta asoma por encima de su borde y el hierro pasa por delante del tercio de abajo), combada (más clara en el lomo, en
     * sombra y fría en un canto, encendida por la lámpara en el otro) y ladeada, con la rasgadura donde entra el hierro y su sombra
     * en la pila. Queda encima de la pila: con pocos tickets baja hacia la base. Lleva impresos el nombre y la cuenta (en dos
     * líneas si no caben), los renglones de lo pedido y el código de barras. Con [ab] sube por la púa y se ladea más.
     */
    private fun DrawScope.ultimoTicket(e: EstadoLibreta, medidor: TextMeasurer, g: EscenaLibretaNeon, ab: Float, cx: Float, cima: Float, tp: TonoNeon) {
        val u = g.u; val h = g.h
        val P = PaletaLibretaNeon
        val tw = h * 0.3f; val alto = h * 0.45f
        val sube = ab * h * 0.14f
        val agujero = Offset(cx, maxOf(cima - h * 0.012f, alto * 2f / 3f + h * 0.075f) - sube)
        val r = Rect(cx - tw / 2, agujero.y - alto * 2f / 3f, cx + tw / 2, agujero.y + alto / 3f)
        val t = g.luz.t(cx + h * 0.04f, r.center.y)
        val ladeo = -6f - 9f * ab + 4f * (azar(e.semilla, 7) - 0.5f)
        val comba = 2.6f * u
        rotate(ladeo, agujero) {
            // La tira: el borde de arriba rasgado en dientes y combado, los cantos rectos, el de abajo enrollado.
            val tira = Path().apply {
                moveTo(r.left, r.bottom); lineTo(r.left, r.top + 2.6f * u)
                var x = r.left; var arriba = true
                while (x < r.right) {
                    x = minOf(x + 4f * u, r.right)
                    val f = (x - r.left) / tw
                    lineTo(x, r.top - comba * sin(f * 3.1416f) + (if (arriba) 0f else 2.6f * u)); arriba = !arriba
                }
                lineTo(r.right, r.bottom); quadraticTo(cx, r.bottom + comba, r.left, r.bottom); close()
            }
            drawPath(Path().apply { addPath(tira, Offset(-5f * u, 5f * u)) }, t.s(0.42f))
            val papel = mezcla(P.PAPEL, t.c(P.PAPEL, 1.3f), 0.35f)
            drawPath(tira, Brush.horizontalGradient(
                0f to mezcla(mezcla(papel, P.FRIO, 0.15f), PaletaNeon.SOMBRA, 0.28f), 0.3f to papel, 0.55f to mezcla(papel, Color.White, 0.12f),
                0.85f to mezcla(papel, P.CALOR, 0.12f), 1f to mezcla(mezcla(papel, P.CALOR, 0.3f), PaletaNeon.SOMBRA, 0.15f), startX = r.left, endX = r.right))
            drawRect(t.b(0.55f), Offset(r.right - 1.4f * u, r.top + 3f * u), Size(1.4f * u, r.height - 3f * u))
            // Lo enrollado abajo.
            drawRect(mezcla(papel, PaletaNeon.SOMBRA, 0.3f), Offset(r.left, r.bottom - 6f * u), Size(r.width, 6f * u))
            drawRect(t.b(0.5f), Offset(r.left, r.bottom - 6f * u), Size(r.width, 0.9f * u))
            // Lo impreso: el nombre y la cuenta (en dos líneas si no caben), el filete, los renglones y el código de barras.
            val m = 5f * u
            val tinta = P.IMPRESO
            var y = r.top + 4f * u
            partir(e.nombre).forEach { linea ->
                textoEn(medidor, linea, Rect(r.left + m, y, r.right - m, y + 14f * u), Letra.letrero, tinta, 12.5f, peso = FontWeight.SemiBold, minimo = 7f); y += 13.5f * u
            }
            val filete = Path()
            var fx = r.left + m
            while (fx < r.right - m) { filete.moveTo(fx, y + 1.5f * u); filete.lineTo(minOf(fx + 2f * u, r.right - m), y + 1.5f * u); fx += 3.6f * u }
            drawPath(filete, tinta.copy(alpha = 0.6f), style = Stroke(0.7f * u))
            y += 3.5f * u
            partir(e.cuenta).forEach { linea ->
                textoEn(medidor, linea, Rect(r.left + 3f * u, y, r.right - 3f * u, y + 10f * u), Letra.terminal, tinta.copy(alpha = 0.88f), 8.5f, minimo = 5f); y += 9.5f * u
            }
            y += 3f * u
            var k = 0
            while (y < agujero.y - 16f * u) {
                drawLine(tinta.copy(alpha = 0.42f), Offset(r.left + m, y), Offset(r.left + m + tw * 0.38f * (0.5f + 0.5f * azar(e.semilla, 130 + k)), y), 1f * u)
                drawLine(tinta.copy(alpha = 0.42f), Offset(r.right - m - tw * 0.16f, y), Offset(r.right - m, y), 1f * u)
                y += 5.5f * u; k++
            }
            var bx = r.left + tw * 0.18f; var j = 0
            val by = agujero.y - 13f * u
            while (bx < r.right - tw * 0.18f) {
                val gr = (if (azar(e.semilla, 300 + j) > 0.5f) 1.5f else 0.8f) * u
                drawRect(tinta.copy(alpha = 0.85f), Offset(bx, by), Size(gr, 8f * u))
                bx += gr + (0.9f + azar(e.semilla, 400 + j)) * u; j++
            }
            // Debajo del agujero: el total repasado con el rotulador fluorescente (una franja corta, sin letra).
            drawRoundRect(Color(0xFFFF4FA0).copy(alpha = 0.3f), Offset(r.right - m - tw * 0.3f, agujero.y + 6f * u), Size(tw * 0.3f, 5f * u), CornerRadius(2f * u))
            drawLine(tinta.copy(alpha = 0.42f), Offset(r.left + m, agujero.y + 8.5f * u), Offset(r.left + m + tw * 0.3f, agujero.y + 8.5f * u), 1f * u)
            // La rasgadura donde entra el hierro: el agujero oscuro y las lengüetas de papel levantadas alrededor.
            for (q in 0 until 5) {
                val an = q * 1.2566f + 0.4f
                val p0 = agujero + Offset(cos(an) * 2.4f * u, sin(an) * 2.4f * u)
                val p1 = agujero + Offset(cos(an + 0.3f) * 4.6f * u, sin(an + 0.3f) * 4.6f * u)
                drawLine(t.s(0.45f), p0, p1, 0.8f * u)
            }
            drawCircle(PaletaNeon.SOMBRA.copy(alpha = 0.8f), 2.4f * u, agujero)
            drawCircle(Color.White.copy(alpha = 0.35f), 3.4f * u, agujero + Offset(0.6f * u, 0.6f * u), style = Stroke(0.6f * u))
        }
        // El hierro pasa por delante del tercio de abajo, desde el agujero, y se mete en la pila.
        val abajo = agujero.y + alto / 3f - 5f * u
        drawLine(tp.s(0.4f), Offset(cx + 1.8f * u, agujero.y + 2f * u), Offset(cx + 1.8f * u, abajo), 2f * u)
        drawLine(tp.c(P.ACERO, 1.2f), agujero, Offset(cx, abajo), 2.6f * u)
        drawLine(tp.b(0.85f, 1.4f), Offset(cx + 0.7f * u, agujero.y), Offset(cx + 0.7f * u, abajo), 0.8f * u)
    }
}
