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
import androidx.compose.ui.text.TextMeasurer
import io.github.ruddy2003.vestigium.ui.ideario.azar

/**
 * La alacena de las cartas de Navidad ([MuebleEstanteNavidad]): pino pintado de rojo de almagre, con los filetes verdes de la misma
 * mano que el aparador del Ideario. La cornisa (el tablero de arriba, la gola, el friso con su filete verde y la guirnalda de acebo
 * pintada y gastada, el bocel), los casilleros (el machihembrado salvia del fondo con la sombra franca de la tabla de arriba, los
 * cantos de dentro y la tabla vista desde arriba), las tablas de canto redondeado con su filete, los costados con su filete y el
 * paño de cocina colgado, los tarjeteros de latón de los meses y el zócalo recortado con sus patas de cartela. Gastada donde se
 * toca: asoma el pino. De día, la luz viene de la ventana (izquierda); de noche, cada tramo se apaga con la distancia a su luz.
 */
internal object ObraEstanteNavidad {
    private val P = PaletaNavidad

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireNavidad, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.045f)).toInt().coerceIn(2, 32) else 1

    /**
     * [r] pintado con el color de [color] (t, de 0 a 1 a lo largo) muestreado en [n] + 1 puntos y unido por un degradado: de noche,
     * la luz cambia de un punto a otro sin escalones; de día ([n] = 1), un solo color.
     */
    private fun DrawScope.degradado(r: Rect, vertical: Boolean, n: Int, color: (Float) -> Color) {
        if (r.width <= 0f || r.height <= 0f) return
        if (n <= 1) { drawRect(color(0.5f), r.topLeft, r.size); return }
        val paradas = Array(n + 1) { i -> (i / n.toFloat()) to color(i / n.toFloat()) }
        val pincel = if (vertical) Brush.verticalGradient(*paradas, startY = r.top, endY = r.bottom) else Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right)
        drawRect(pincel, r.topLeft, r.size)
    }

    /** La pintura saltada en un canto: asoma el pino, con el borde de la pintura en sombra. */
    private fun DrawScope.gastado(p: Offset, r: Float, semilla: Int, a: AireNavidad, k: Float) {
        with(MuroEstanteNavidad) { saltado(p, r, semilla, a, k) }
    }

    // ---------------------------------------------------------------- la cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más). */
    fun DrawScope.cornisa(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width
        cornisaCon(r, a, cerca, null)
        // El sol de la ventana (o la luna, más tenue) alcanza el tablero y el friso: la misma cornisa, pintada con esa luz, corrida
        // porque está más adelante que la pared; así la mancha se quiebra en su canto.
        if (m != null) clipPath(m.forma(w, ManchaNavidad.FRENTE)) { cornisaCon(r, a, cerca, ManchaNavidad(m.q, m.color, m.fuerza * (if (a.oscuro) 0.5f else 0.6f))) }
        // La puntilla de papel calado del tablero, que cuelga por el canto (la del Ideario, aquí sobre la cornisa).
        val y0 = r.top + w * 0.005f
        puntilla(0f, w, y0, w * 0.022f, AireNavidad(a.oscuro, a.noche, cuerpo = true), cerca, 9250)
        // De noche, el filo frío de la nieve de la ventana en el canto del tablero, a la izquierda.
        if (a.oscuro) filoFrioHorizontal(0f, w, r.top - w * 0.008f, a)
    }

    /**
     * La puntilla de papel calado (como la de las baldas del Ideario, aquí colgando del canto de una tabla): una tira crema con el
     * borde de abajo en festones, cada uno con su agujero, su sombra en la madera y, de noche, encendida junto a cada llama y hundida
     * entre ellas (un degradado a lo ancho, una sola pintada).
     */
    fun DrawScope.puntilla(x0: Float, x1: Float, y: Float, alto: Float, a: AireNavidad, cerca: (Offset) -> Float, semilla: Int) {
        val w = size.width; val u = density
        if (x1 - x0 < 4f || alto < 1f) return
        val n = ((x1 - x0) / (w * 0.034f)).toInt().coerceIn(1, 80)
        val pp = (x1 - x0) / n
        val p = Path().apply {
            moveTo(x0, y - 1f); lineTo(x1, y - 1f); lineTo(x1, y + alto * 0.55f)
            for (j in n downTo 1) {
                val xa = x0 + pp * j; val xb = x0 + pp * (j - 1)
                val hondo = alto * (1.05f + 0.08f * azar(semilla, j))
                cubicTo(xa - pp * 0.04f, y + hondo, xb + pp * 0.04f, y + hondo, xb, y + alto * 0.55f)
            }
            close()
        }
        val agujeros = Path()
        for (j in 0 until n) {
            agujeros.addOval(Rect(Offset(x0 + pp * (j + 0.5f), y + alto * 0.66f), alto * 0.12f))
            agujeros.addOval(Rect(Offset(x0 + pp * j + pp * 0.25f, y + alto * 0.3f), alto * 0.06f))
            agujeros.addOval(Rect(Offset(x0 + pp * j + pp * 0.75f, y + alto * 0.3f), alto * 0.06f))
        }
        drawPath(corridoNavidad(p, 1f * u, 1.8f * u), a.sombra(0.38f))
        if (a.lumbre) {
            val tramos = ((x1 - x0) / (w * 0.06f)).toInt().coerceIn(2, 24)
            val paradas = Array(tramos + 1) { i -> (i / tramos.toFloat()) to a.dia(P.PAPEL, 0f, cerca(Offset(x0 + (x1 - x0) * i / tramos, y + alto * 0.5f))) }
            drawPath(p, Brush.horizontalGradient(*paradas, startX = x0, endX = x1))
        } else drawPath(p, a.dia(P.PAPEL, 0f, 1f))
        drawPath(agujeros, a.sombra(0.55f))
        drawPath(p, a.sombra(0.18f), style = Stroke(0.7f * u))
        drawRect(a.sombra(0.12f), Offset(x0, y - 1f), Size(x1 - x0, 1.2f * u))
    }

    /** El filo frío de la ventana, de noche, en un canto horizontal de [x0] a [x1] a la altura [y]: fuerte a la izquierda, se apaga. */
    private fun DrawScope.filoFrioHorizontal(x0: Float, x1: Float, y: Float, a: AireNavidad) {
        val w = size.width; val u = density
        val n = ((x1 - x0) / (w * 0.06f)).toInt().coerceIn(1, 24)
        for (t in 0 until n) {
            val xa = x0 + (x1 - x0) * t / n; val xb = x0 + (x1 - x0) * (t + 1) / n
            val f = frioVentanaNavidad((xa + xb) / 2 / w, y / w)
            if (f > 0.02f) drawRect(a.filoFrio(0.5f * f), Offset(xa, y), Size(xb - xa, 1.3f * u))
        }
    }

    private fun DrawScope.cornisaCon(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val lit = if (m != null) 1f else 0f
        fun c(col: Color, f: Float, k: Float) = a.en(col, f, k, lit, m)
        val y0 = r.top
        val sup0 = y0 - w * 0.01f; val sup1 = y0 + w * 0.005f
        val canto1 = y0 + w * 0.018f
        val gola1 = y0 + w * 0.034f
        val friso0 = y0 + w * 0.038f
        val friso1 = y0 + w * 0.074f
        val bocel1 = y0 + w * 0.087f
        val n = tramos(a, w, w)
        for (t in 0 until n) {
            val x0 = w * t / n; val x1 = w * (t + 1) / n + 1f
            val k = cerca(Offset((x0 + x1) / 2, y0 + w * 0.04f))
            // El tablero de arriba, visto desde encima (más claro y gastado), y su canto.
            drawRect(c(P.ALMAGRE_LUZ, -0.18f, k), Offset(x0, sup0), Size(x1 - x0, sup1 - sup0))
            drawRect(c(P.ALMAGRE, 0.04f, k), Offset(x0, sup1), Size(x1 - x0, canto1 - sup1))
            // La gola: clara arriba (mira al techo y a la luz) y honda abajo.
            drawRect(c(P.ALMAGRE_LUZ, -0.04f, k), Offset(x0, canto1), Size(x1 - x0, (gola1 - canto1) * 0.45f))
            drawRect(c(P.ALMAGRE_OSC, 0f, k), Offset(x0, canto1 + (gola1 - canto1) * 0.45f), Size(x1 - x0, (gola1 - canto1) * 0.55f))
            drawRect(c(P.ALMAGRE_HONDO, 0f, k), Offset(x0, gola1), Size(x1 - x0, friso0 - gola1))
            // El friso.
            drawRect(c(P.ALMAGRE, 0f, k), Offset(x0, friso0), Size(x1 - x0, friso1 - friso0))
            // El bocel: un medio cilindro (claro arriba, hondo abajo).
            drawRect(c(P.ALMAGRE_LUZ, 0f, k), Offset(x0, friso1), Size(x1 - x0, (bocel1 - friso1) * 0.45f))
            drawRect(c(P.ALMAGRE, -0.15f, k), Offset(x0, friso1 + (bocel1 - friso1) * 0.45f), Size(x1 - x0, (bocel1 - friso1) * 0.55f))
            // El larguero de arriba de la alacena.
            drawRect(c(P.ALMAGRE_OSC, -0.05f, k), Offset(x0, bocel1), Size(x1 - x0, r.bottom - bocel1))
        }
        val kc = cerca(Offset(w * 0.6f, friso0))
        // El filete verde del friso y la guirnalda de acebo pintada a pulso, gastada.
        val fl = Rect(w * 0.04f, friso0 + w * 0.007f, w * 0.96f, friso1 - w * 0.007f)
        drawRect(c(P.FILETE, 0f, kc), fl.topLeft, fl.size, style = Stroke(1.3f * u))
        guirnalda(fl, a, cerca, lit, m)
        // Los filetes de luz y las líneas de sombra de cada moldura.
        drawRect(a.claro(0.45f), Offset(0f, sup1), Size(w, 1.1f * u))
        drawRect(a.sombra(0.35f), Offset(0f, canto1 - 1f * u), Size(w, 1f * u))
        drawRect(a.sombra(0.5f), Offset(0f, friso0 - 1.2f * u), Size(w, 1.2f * u))
        drawRect(a.claro(0.35f), Offset(0f, friso1), Size(w, 1f * u))
        drawRect(a.sombra(0.55f), Offset(0f, bocel1), Size(w, 1.6f * u))
        drawRect(a.sombra(0.4f), Offset(0f, r.bottom - 3f * u), Size(w, 3f * u))
        // El gastado: golpes en el canto del tablero, donde asoma el pino, y rozaduras.
        for (j in 0 until 6) {
            val x = w * (0.03f + 0.94f * azar(9210, j))
            if (j < 4) gastado(Offset(x, sup1 + w * 0.004f), w * (0.003f + 0.003f * azar(9211, j)), 9212 + j, a, cerca(Offset(x, sup1)))
            val y = friso0 + (friso1 - friso0) * azar(9213, j)
            drawLine(a.claro(0.12f), Offset(x, y), Offset(x + w * (0.02f + 0.03f * azar(9214, j)), y + 0.5f * u), 0.8f * u)
        }
        gastado(Offset(w * 0.012f, bocel1 - w * 0.004f), w * 0.01f, 9220, a, cerca(Offset(0f, bocel1)))
        gastado(Offset(w * 0.985f, friso0 + w * 0.01f), w * 0.009f, 9221, a, cerca(Offset(w, friso0)))
    }

    /** La guirnalda pintada en el friso: un tallo que ondea con hojas de acebo y bayas, gastada (la pintura se fue a trozos). */
    private fun DrawScope.guirnalda(r: Rect, a: AireNavidad, cerca: (Offset) -> Float, lit: Float, m: ManchaNavidad?) {
        val w = size.width; val u = density
        val tallo = Path(); val hojas = Path(); val bayas = Path()
        val paso = w * 0.06f
        val n = (r.width / paso).toInt().coerceIn(2, 40)
        val yc = r.center.y
        tallo.moveTo(r.left + w * 0.01f, yc)
        for (i in 0 until n) {
            val x0 = r.left + w * 0.01f + i * paso
            val s = if (i % 2 == 0) 1f else -1f
            tallo.quadraticTo(x0 + paso * 0.5f, yc + s * r.height * 0.26f, x0 + paso, yc)
            // Una hoja de cada lado del tallo, de acebo (con dos picos), y tres bayas en cada cresta.
            val h = Offset(x0 + paso * 0.5f, yc + s * r.height * 0.13f)
            if (azar(9230, i) < 0.82f) {
                val l = paso * 0.36f
                hojas.moveTo(h.x, h.y)
                hojas.lineTo(h.x + l * 0.35f, h.y - s * l * 0.32f); hojas.lineTo(h.x + l * 0.5f, h.y - s * l * 0.12f)
                hojas.lineTo(h.x + l * 0.75f, h.y - s * l * 0.36f); hojas.lineTo(h.x + l, h.y - s * l * 0.08f)
                hojas.lineTo(h.x + l * 0.6f, h.y + s * l * 0.1f); hojas.close()
            }
            if (i % 2 == 1) for (b in 0 until 3) bayas.addOval(Rect(Offset(h.x - paso * 0.08f + b * paso * 0.07f, h.y + s * r.height * (0.05f + 0.06f * (b % 2))), r.height * 0.09f))
        }
        val k = cerca(r.center)
        drawPath(tallo, a.en(P.FILETE, 0f, k, lit, m).copy(alpha = 0.75f), style = Stroke(1.2f * u))
        drawPath(hojas, a.en(P.FILETE_LUZ, 0f, k, lit, m).copy(alpha = 0.8f))
        drawPath(bayas, a.en(P.CARMIN, 0.1f, k, lit, m).copy(alpha = 0.85f))
        // Donde la pintura saltó: el almagre de debajo vuelve a asomar en manchones.
        for (j in 0 until 7) {
            val x = r.left + r.width * azar(9240, j); val y = r.top + r.height * azar(9241, j)
            drawOval(a.en(P.ALMAGRE, 0f, k, lit, m), Offset(x, y), Size(w * (0.012f + 0.02f * azar(9242, j)), r.height * 0.3f))
        }
    }

    // ---------------------------------------------------------------- los casilleros

    /** El casillero [r] de la repisa [k]: el machihembrado salvia, sus sombras, los cantos de dentro y la tabla de abajo. */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireNavidad, am: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val af = AireNavidad(a.oscuro, a.noche, cuerpo = true, fondo = true)
        val luna = lunaCuerpoNavidad(a, r.left / w + 0.2f, r.center.y / w)
        fun c(col: Color, f: Float, kk: Float) = af.dia(col, f, kk).let { if (luna > 0f) af.sol(col, it, luna * 0.7f) else it }
        // Una fila de más arriba y abajo: así las juntas con las tablas no dejan asomar el fondo.
        val fr = Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f)
        val n = tramos(a, r.width, w)
        val techo = w * 0.034f
        val lado = w * 0.022f
        val piso = w * 0.016f
        // El salvia: más vivo abajo, donde llega la luz por la boca, y más hondo arriba, bajo la tabla.
        fun kx(t: Float) = cerca(Offset(fr.left + fr.width * t, r.center.y))
        degradado(fr, false, n) { t -> c(P.SALVIA, 0f, kx(t)) }
        degradado(Rect(fr.left, fr.top, fr.right, fr.top + r.height * 0.42f), false, n) { t -> c(P.SALVIA_OSC, 0f, kx(t)).copy(alpha = 0.5f) }
        degradado(Rect(fr.left, r.top + r.height * 0.6f, fr.right, r.top + r.height * 0.9f), false, n) { t -> c(P.SALVIA_LUZ, 0f, kx(t)).copy(alpha = if (a.oscuro) 0.25f else 0.4f) }
        // El machihembrado: la junta de cada tabla (honda) con su canto de luz al lado, algo torcidas.
        val juntas = Path(); val cantos = Path()
        var x = r.left + w * 0.03f
        var j = 0
        while (x < r.right - w * 0.01f && j < 60) {
            val xx = x + w * 0.006f * (azar(9300 + k, j) - 0.5f)
            juntas.moveTo(xx, r.top); juntas.lineTo(xx, r.bottom - piso)
            cantos.moveTo(xx + 1.3f * u, r.top); cantos.lineTo(xx + 1.3f * u, r.bottom - piso)
            x += w * 0.058f; j++
        }
        val kc = cerca(r.center)
        drawPath(juntas, c(P.SALVIA_HONDO, 0f, kc).copy(alpha = 0.6f), style = Stroke(1.1f * u))
        drawPath(cantos, c(P.SALVIA_LUZ, 0.1f, kc).copy(alpha = if (a.oscuro) 0.25f else 0.55f), style = Stroke(0.7f * u))
        // Lo gastado: una rozadura clara donde roza lo que se saca y se mete, y alguna mancha de humo.
        val rx = r.left + r.width * (0.12f + 0.6f * azar(9310, k))
        drawOval(c(P.SALVIA_LUZ, 0.12f, cerca(Offset(rx, r.bottom))).copy(alpha = 0.32f), Offset(rx, r.top + r.height * 0.56f), Size(w * 0.16f, r.height * 0.28f))
        val mx = r.left + r.width * azar(9311, k)
        drawOval(c(P.SALVIA_HONDO, 0f, kc).copy(alpha = 0.22f), Offset(mx, r.top + r.height * (0.3f + 0.25f * azar(9312, k))), Size(w * 0.05f, w * 0.03f))
        // La tabla de arriba: su cara de abajo (un trapecio de almagre en sombra) y la sombra franca que echa en el fondo.
        val kt = cerca(Offset(r.center.x, r.top))
        drawRect(af.sombra(0.55f), Offset(r.left, r.top + techo * 0.8f), Size(r.width, techo * 0.5f))
        drawRect(af.sombra(0.26f), Offset(r.left, r.top + techo * 1.3f), Size(r.width, techo * 1.1f))
        drawPath(poligonoNavidad(fr.left, fr.top, fr.right, fr.top, r.right - lado, r.top + techo, r.left + lado, r.top + techo), am.dia(P.ALMAGRE_OSC, -0.3f, kt))
        drawRect(a.sombra(0.35f), Offset(r.left + lado, r.top + techo - 1.2f * u), Size(r.width - 2 * lado, 1.2f * u))
        // Los costados de dentro, pintados de salvia: el de la izquierda en sombra (de espaldas a la ventana); el de la
        // derecha, alumbrado. De noche, cada uno según su luz.
        val kl = cerca(Offset(r.left, r.center.y)); val kr = cerca(Offset(r.right, r.center.y))
        drawPath(poligonoNavidad(fr.left, fr.top, r.left + lado, r.top + techo, r.left + lado, r.bottom - piso, fr.left, fr.bottom), c(P.SALVIA_OSC, -0.2f, kl))
        drawPath(poligonoNavidad(fr.right, fr.top, r.right - lado, r.top + techo, r.right - lado, r.bottom - piso, fr.right, fr.bottom), c(P.SALVIA_LUZ, -0.02f, kr))
        drawRect(a.sombra(0.28f), Offset(r.left + lado, r.top + techo), Size(w * 0.012f, r.height - techo - piso))
        drawRect(a.brillo(0.22f, kr), Offset(r.right - lado - 1f * u, r.top + techo), Size(1f * u, r.height - techo - piso))
        // De noche, el costado de dentro de la derecha mira a la ventana: su filo frío.
        if (a.oscuro) drawRect(a.filoFrio(0.32f * frioVentanaNavidad(r.right / w, r.center.y / w)), Offset(r.right - lado, r.top + techo), Size(1.2f * u, r.height - techo - piso))
        // La tabla, vista desde arriba, con la luz que entra por la boca, y su roce de pino.
        val kp = cerca(Offset(r.center.x, r.bottom))
        drawPath(poligonoNavidad(r.left + lado, r.bottom - piso, r.right - lado, r.bottom - piso, fr.right, fr.bottom, fr.left, fr.bottom), c(P.SALVIA_LUZ, -0.06f, kp))
        drawRect(a.sombra(0.4f), Offset(r.left + lado, r.bottom - piso), Size(r.width - 2 * lado, 1f * u))
        for (b in 0 until 3) {
            val bx = r.left + r.width * (0.1f + 0.8f * azar(9320 + k, b))
            drawOval(c(P.PINO, 0f, kp).copy(alpha = 0.45f), Offset(bx, r.bottom - piso * 0.7f), Size(w * (0.03f + 0.04f * azar(9321 + k, b)), piso * 0.5f))
        }
    }

    // ---------------------------------------------------------------- las tablas

    /** La tabla [r] bajo la repisa [k]: el canto redondeado con su filete verde, gastado donde se toca. */
    fun DrawScope.tabla(k: Int, r: Rect, d: DisposicionEstante, a: AireNavidad, ap: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val n = tramos(a, r.width, w)
        val nariz = r.top + r.height * 0.26f
        val cara1 = r.top + r.height * 0.84f
        // Una fila de más arriba y abajo, del color del canto (la junta con el casillero cae a medio píxel).
        fun c(col: Color, f: Float, t: Float): Color {
            val x = r.left + r.width * t
            val luna = lunaCuerpoNavidad(a, x / w, r.center.y / w) * 0.6f
            return a.dia(col, f, cerca(Offset(x, r.center.y))).let { if (luna > 0f) a.sol(col, it, luna) else it }
        }
        // Debajo, la cara a todo el alto (así, donde una banda acaba a medio píxel, asoma la cara y no el fondo).
        degradado(Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f), false, n) { t -> c(P.ALMAGRE, 0f, t) }
        degradado(Rect(r.left - 1f, r.top - 1f, r.right + 1f, nariz), false, n) { t -> c(P.ALMAGRE_LUZ, -0.06f, t) }
        degradado(Rect(r.left - 1f, cara1, r.right + 1f, r.bottom + 1f), false, n) { t -> c(P.ALMAGRE_OSC, -0.1f, t) }
        val kc = cerca(r.center)
        // El filo de luz de la nariz, el filete verde pintado y la sombra de abajo.
        drawRect(a.brillo(0.55f, kc), Offset(r.left, r.top), Size(r.width, 1.3f * u))
        drawRect(a.claro(0.2f), Offset(r.left, r.top + 1.3f * u), Size(r.width, (nariz - r.top) * 0.5f))
        drawRect(a.sombra(0.25f), Offset(r.left, nariz - 1f * u), Size(r.width, 1f * u))
        val fy = nariz + (cara1 - nariz) * 0.5f
        drawRect(a.dia(P.FILETE, 0f, kc), Offset(r.left + w * 0.012f, fy - 0.8f * u), Size(r.width - w * 0.024f, 1.6f * u))
        drawRect(a.sombra(0.5f), Offset(r.left, r.bottom - 1.4f * u), Size(r.width, 1.4f * u))
        // De noche, el filo de la luz en la nariz (más donde más alumbra).
        if (a.lumbre && a.oscuro) degradado(Rect(r.left, r.top, r.right, r.top + 1.5f * u), false, n) { t -> a.filo(0.55f * cerca(Offset(r.left + r.width * t, r.top))) }
        // El gastado de la nariz, donde se apoyan las manos, y algún golpe.
        for (j in 0 until 2) {
            val x = r.left + r.width * azar(9400 + k, j)
            gastado(Offset(x, r.top + r.height * 0.14f), w * (0.004f + 0.003f * azar(9401 + k, j)), 9402 + k * 5 + j, a, cerca(Offset(x, r.top)))
        }
        // La puntilla de papel calado que cuelga por la nariz, y el filo frío de la ventana en el canto de arriba.
        puntilla(r.left, r.right, r.top + 0.6f * u, r.height * 0.36f, ap, cerca, 9260 + k)
        if (a.oscuro) filoFrioHorizontal(r.left, r.right, r.top, a)
    }

    // ---------------------------------------------------------------- los costados

    /** El costado [r] ([izq]: el de la izquierda): el larguero con su filete verde, su canto, el gastado y lo colgado. */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val tramosV = (r.height / (w * 0.04f)).toInt().coerceIn(1, 120).let { if (a.lumbre) it else 1 }
        fun c(col: Color, f: Float, t: Float, x: Float = r.center.x): Color {
            val y = r.top + r.height * t
            val luna = lunaCuerpoNavidad(a, x / w, y / w) * 0.6f
            return a.dia(col, f, cerca(Offset(x, y))).let { if (luna > 0f) a.sol(col, it, luna) else it }
        }
        degradado(Rect(r.left - 1f, r.top - 1f, r.right + 1f, r.bottom + 1f), true, tramosV) { t -> c(P.ALMAGRE, if (izq) 0.03f else -0.06f, t) }
        // El canto que mira a la ventana, más claro (el bocel de la arista); el otro, en sombra.
        degradado(Rect(r.left, r.top - 1f, r.left + r.width * 0.13f, r.bottom + 1f), true, tramosV) { t -> c(P.ALMAGRE_LUZ, -0.04f, t, r.left) }
        degradado(Rect(r.right - r.width * 0.15f, r.top - 1f, r.right, r.bottom + 1f), true, tramosV) { t -> c(P.ALMAGRE_OSC, 0f, t, r.right) }
        val kc = cerca(r.center)
        // Las vetas del pino que se marcan bajo la pintura.
        val veta = Path()
        for (j in 0 until 5) {
            val x = r.left + r.width * (0.2f + 0.14f * j + 0.04f * azar(9500 + (if (izq) 0 else 1), j))
            veta.moveTo(x, r.top)
            var y = r.top
            var s = 0
            while (y < r.bottom && s < 200) { val y1 = y + w * 0.1f; veta.quadraticTo(x + (azar(9510 + j, s) - 0.5f) * 2f * u, (y + y1) / 2, x, y1); y = y1; s++ }
        }
        clipRect(r.left, r.top, r.right, r.bottom) { drawPath(veta, a.luz(P.ALMAGRE_OSC, kc).copy(alpha = 0.35f), style = Stroke(0.7f * u)) }
        // El filete verde: una media caña pintada a lo alto, con su luz y su sombra.
        val fx = r.left + r.width * 0.46f
        val fr0 = r.top + w * 0.03f; val fr1 = r.bottom - w * 0.03f
        degradado(Rect(fx - 1.2f * u, fr0, fx + 1.2f * u, fr1), true, tramosV) { t -> a.dia(P.FILETE, 0f, cerca(Offset(fx, fr0 + (fr1 - fr0) * t))) }
        drawLine(a.sombra(0.3f), Offset(fx + 1.8f * u, r.top + w * 0.03f), Offset(fx + 1.8f * u, r.bottom - w * 0.03f), 0.8f * u)
        drawLine(a.claro(0.3f), Offset(fx - 2f * u, r.top + w * 0.03f), Offset(fx - 2f * u, r.bottom - w * 0.03f), 0.7f * u)
        // El capitel de arriba (bajo la cornisa) y la basa de abajo (sobre el zócalo): dos tacos con su moldura.
        listOf(true, false).forEach { arriba ->
            val y0 = if (arriba) r.top else r.bottom - w * 0.05f
            val y1 = if (arriba) r.top + w * 0.045f else r.bottom
            val kk = cerca(Offset(r.center.x, (y0 + y1) / 2))
            drawRect(a.dia(P.ALMAGRE, if (izq) 0.05f else -0.04f, kk), Offset(r.left - 1f, y0), Size(r.width + 2f, y1 - y0))
            val mold = if (arriba) y1 - w * 0.012f else y0
            drawRect(a.dia(P.ALMAGRE_LUZ, -0.02f, kk), Offset(r.left - 1f, mold), Size(r.width + 2f, w * 0.006f))
            drawRect(a.dia(P.ALMAGRE_OSC, 0f, kk), Offset(r.left - 1f, mold + w * 0.006f), Size(r.width + 2f, w * 0.006f))
            drawRect(a.dia(P.FILETE, 0f, kk), Offset(r.left + r.width * 0.2f, (if (arriba) y0 + w * 0.012f else y0 + w * 0.022f)), Size(r.width * 0.6f, w * 0.014f), style = Stroke(1.1f * u))
            drawRect(a.sombra(if (arriba) 0.45f else 0.3f), Offset(r.left, if (arriba) y1 else y0 - 1.4f * u), Size(r.width, 1.4f * u))
            drawRect(a.claro(0.3f), Offset(r.left, if (arriba) y0 + 1f * u else y1 - w * 0.012f), Size(r.width, 1f * u))
        }
        // De noche: el filo de la llama en el canto que la mira y, en el canto que mira a la ventana (el de la izquierda), el filo
        // frío de la nieve, fuerte arriba y apagándose hacia abajo.
        if (a.lumbre && a.oscuro) {
            val luzDer = r.center.x / w < 0.5f
            val xf = if (luzDer) r.right - 1.5f * u else r.left
            degradado(Rect(xf, r.top, xf + 1.5f * u, r.bottom), true, tramosV) { t -> a.filo(0.6f * cerca(Offset(r.center.x, r.top + r.height * t))) }
            degradado(Rect(r.left, r.top, r.left + 1.6f * u, r.bottom), true, tramosV) { t -> a.filoFrio((if (izq) 0.6f else 0.3f) * frioVentanaNavidad(r.left / w, (r.top + r.height * t) / w)) }
        }
        // El gastado donde se agarra la alacena (a la altura de las manos, en el canto de dentro) y los golpes.
        val canto = if (izq) r.right - r.width * 0.08f else r.left + r.width * 0.08f
        d.tablas.forEachIndexed { j, tb ->
            if (azar(9520 + (if (izq) 0 else 1), j) < 0.3f) gastado(Offset(canto, tb.top + tb.height * 0.4f), w * 0.005f, 9530 + j * 3 + (if (izq) 0 else 1), a, cerca(Offset(canto, tb.top)))
        }
        for (j in 0 until (r.height / (w * 0.2f)).toInt().coerceIn(1, 40)) {
            val y = r.top + r.height * azar(9540 + (if (izq) 0 else 1), j)
            val x = r.left + r.width * (0.15f + 0.6f * azar(9541, j))
            drawLine(a.claro(0.14f), Offset(x, y), Offset(x + w * 0.008f, y + w * 0.012f), 0.8f * u, StrokeCap.Round)
        }
        // En el de la izquierda, el gancho de latón con el paño de cocina colgado.
        if (izq && d.tablas.isNotEmpty()) pano(r, d.tablas[0].top + w * 0.06f, a, cerca)
    }

    /** El paño de cocina de lino crema con sus rayas rojas, doblado sobre su gancho de latón. */
    private fun DrawScope.pano(r: Rect, y: Float, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val g = Offset(r.center.x, y)
        val k = cerca(g)
        val ancho = r.width * 0.86f
        val largo = w * 0.16f
        val tela = Path().apply {
            moveTo(g.x - ancho * 0.42f, g.y + w * 0.004f)
            quadraticTo(g.x, g.y - w * 0.008f, g.x + ancho * 0.42f, g.y + w * 0.004f)
            lineTo(g.x + ancho * 0.5f, g.y + largo * 0.95f)
            quadraticTo(g.x + ancho * 0.1f, g.y + largo * 1.02f, g.x - ancho * 0.48f, g.y + largo)
            close()
        }
        drawPath(corridoNavidad(tela, w * 0.007f, w * 0.008f), a.sombra(0.35f))
        drawPath(tela, a.dia(P.LANA_CREMA, 0f, k))
        clipPath(tela) {
            // Las rayas rojas del paño, cerca de la punta, y el pliegue de la doblez.
            listOf(0.72f, 0.78f, 0.86f).forEachIndexed { i, f ->
                drawRect(a.dia(P.CARMIN, 0f, k), Offset(g.x - ancho, g.y + largo * f), Size(ancho * 2f, w * (if (i == 1) 0.004f else 0.008f)))
            }
            drawRect(a.sombra(0.22f), Offset(g.x + ancho * 0.12f, g.y), Size(ancho * 0.4f, largo * 1.1f))
            drawRect(a.claro(0.3f), Offset(g.x - ancho * 0.46f, g.y), Size(ancho * 0.16f, largo * 1.1f))
            drawLine(a.sombra(0.25f), Offset(g.x - ancho * 0.1f, g.y + w * 0.01f), Offset(g.x - ancho * 0.05f, g.y + largo), 0.8f * u)
        }
        // El gancho.
        drawArc(a.luz(P.LATON, k), 180f, 180f, false, Offset(g.x - w * 0.006f, g.y - w * 0.01f), Size(w * 0.012f, w * 0.012f), style = Stroke(1.4f * u))
        clavoNavidad(Offset(g.x, g.y - w * 0.012f), 1.6f * u, a, k)
    }

    // ---------------------------------------------------------------- los tarjeteros de los meses

    /** El tarjetero de latón de un mes (el mismo del cajón del Ideario), con su tarjeta y sus dos clavos. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireNavidad, k: Float) {
        val u = density
        val r0 = p.r
        val largo = (p.texto.length - 16).coerceIn(0, 20) / 20f
        val mas = r0.width * 0.22f * largo
        val r = Rect(r0.left - mas + r0.width * 0.05f, r0.top - 0.6f * u, r0.right + mas - r0.width * 0.05f, r0.bottom + 0.6f * u)
        drawRect(a.sombra(0.45f), r.topLeft + Offset(1.4f * u, 1.8f * u), r.size)
        drawRect(a.luz(P.LATON_OSC, k), r.topLeft, r.size)
        drawRect(a.luz(P.LATON, k), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, r.height - 1.6f * u))
        drawRect(a.brillo(0.55f, k), r.topLeft + Offset(0.8f * u, 0.8f * u), Size(r.width - 1.6f * u, 0.9f * u))
        val m = r.height * 0.16f
        val t = Rect(r.left + m * 1.6f, r.top + m, r.right - m * 1.6f, r.bottom - m)
        drawRect(a.luz(P.LATON_OSC, k), t.topLeft - Offset(0.6f * u, 0.6f * u), Size(t.width + 1.2f * u, t.height + 1.2f * u))
        drawRect(a.etiqueta(P.CARTULINA, k), t.topLeft, t.size)
        drawRect(a.sombra(0.2f), t.topLeft, Size(t.width, t.height * 0.2f))
        listOf(r.left + m * 0.8f, r.right - m * 0.8f).forEach { x -> clavoNavidad(Offset(x, r.center.y), 1.2f * u, a, k, P.LATON_OSC) }
        val tinta = if (a.oscuro) Color(0xFF140C06) else P.TINTA
        textoEn(medidor, p.texto, Rect(t.left + 2f * u, t.top + 0.4f * u, t.right - 2f * u, t.bottom), P.LETRA, tinta, 11f, espaciado = 0.3f, minimo = 7f)
    }

    // ---------------------------------------------------------------- el zócalo

    /** El zócalo de [r] (de debajo de la última tabla hasta el suelo): el rodapié con su bocel, recortado abajo, y sus patas de cartela. */
    fun DrawScope.zocalo(r: Rect, a: AireNavidad, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val alto = r.height
        val bocel = r.top + alto * 0.16f
        val falda = r.bottom - alto * 0.3f
        val n = tramos(a, w, w)
        // Debajo de la alacena, entre las patas, la sombra honda en el suelo.
        drawRect(a.sombra(if (a.oscuro) 0.75f else 0.55f), Offset(0f, falda), Size(w, r.bottom - falda + w * 0.01f))
        fun kx(t: Float) = cerca(Offset(w * t, r.center.y))
        degradado(Rect(0f, r.top, w, r.top + (bocel - r.top) * 0.5f), false, n) { t -> a.dia(P.ALMAGRE_LUZ, -0.05f, kx(t)) }
        degradado(Rect(0f, r.top + (bocel - r.top) * 0.5f, w, bocel), false, n) { t -> a.dia(P.ALMAGRE_OSC, 0f, kx(t)) }
        degradado(Rect(0f, bocel, w, falda), false, n) { t -> a.dia(P.ALMAGRE, -0.04f, kx(t)) }
        // El recorte de la falda: arcos rebajados entre las patas, con su canto en sombra.
        val faldaP = Path()
        val arcos = 5
        val x0 = w * 0.09f; val x1 = w * 0.91f
        faldaP.moveTo(0f, falda - 1f)
        faldaP.lineTo(x0, falda - 1f)
        for (i in 0 until arcos) {
            val a0 = x0 + (x1 - x0) * i / arcos; val a1 = x0 + (x1 - x0) * (i + 1) / arcos
            faldaP.cubicTo(a0 + (a1 - a0) * 0.15f, falda + alto * 0.2f, a1 - (a1 - a0) * 0.15f, falda + alto * 0.2f, a1, falda - 1f)
        }
        faldaP.lineTo(w, falda - 1f); faldaP.lineTo(w, falda - alto * 0.02f); faldaP.lineTo(0f, falda - alto * 0.02f); faldaP.close()
        val kc = cerca(Offset(w * 0.5f, falda))
        drawPath(faldaP, a.dia(P.ALMAGRE, -0.06f, kc))
        drawPath(faldaP, a.sombra(0.35f), style = Stroke(1.2f * u))
        drawRect(a.brillo(0.5f, kc), Offset(0f, r.top), Size(w, 1.2f * u))
        drawRect(a.sombra(0.45f), Offset(0f, bocel), Size(w, 1.4f * u))
        drawRect(a.dia(P.FILETE, 0f, kc), Offset(w * 0.06f, bocel + (falda - bocel) * 0.4f), Size(w * 0.88f, 1.5f * u))
        // La sombra de la última tabla en el zócalo.
        drawRect(a.sombra(0.4f), Offset(0f, r.top), Size(w, 2.6f * u))
        // Las patas de cartela: la de cada punta, con su voluta y su sombra en el suelo.
        listOf(0f to 1f, w to -1f).forEach { (ex, s) ->
            val k = cerca(Offset(ex, r.bottom))
            val pw = w * 0.09f
            val pata = Path().apply {
                moveTo(ex, falda - alto * 0.02f)
                lineTo(ex + s * pw, falda - alto * 0.02f)
                cubicTo(ex + s * pw * 0.9f, falda + alto * 0.12f, ex + s * pw * 0.55f, falda + alto * 0.12f, ex + s * pw * 0.45f, r.bottom)
                lineTo(ex, r.bottom); close()
            }
            drawOval(a.sombra(0.5f), Offset(ex - pw * 0.2f + (if (s > 0) 0f else -pw * 0.6f), r.bottom - alto * 0.06f), Size(pw * 1.1f, alto * 0.14f))
            drawPath(pata, a.dia(P.ALMAGRE, if (s > 0) 0.02f else -0.1f, k))
            drawPath(pata, a.sombra(0.3f), style = Stroke(1f * u))
            gastado(Offset(ex + s * pw * 0.4f, r.bottom - alto * 0.05f), w * 0.009f, 9600 + (if (s > 0) 0 else 1), a, k)
        }
        gastado(Offset(w * 0.3f, r.top + alto * 0.06f), w * 0.006f, 9610, a, kc)
    }
}
