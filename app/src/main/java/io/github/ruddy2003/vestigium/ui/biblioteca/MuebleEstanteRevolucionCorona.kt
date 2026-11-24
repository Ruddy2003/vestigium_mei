package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * El bodegón del cajista sobre la cornisa de Revolución ([MuebleEstanteRevolucion]), de atrás adelante: manda la caja de tipos
 * apoyada en la pared, ladeada (el tablero de pino con sus cajetines desiguales y el plomo que brilla dentro); delante, la pila
 * de pliegos doblados atada con bramante con el componedor encima, el bote de tinta de gres con su espátula y el bote de cola
 * con la brocha de pegar carteles; a la derecha del centro, la vela de sebo en su candelero de latón (la luz de la noche); la
 * jarra de loza blanca con su cenefa azul y el borde desportillado, delante de tres resmas envueltas cortadas por el borde; y,
 * colgado de un clavo, el mazo de velas de sebo atadas por el pabilo. Cada cosa deja su sombra en la pared y en la cornisa, y
 * la mancha de la ventana pinta lo que alcanza (se pinta otra vez, alumbrado, dentro de los cuarterones).
 */
internal object CoronaEstanteRevolucion {
    private val P = PaletaRevolucion

    /** Lo colgado en la pared, antes de la cornisa: el mazo de velas de sebo. */
    fun DrawScope.atras(a: AireRevolucion, apoyo: Float, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width
        conLuz(m, -0.006f) { mm -> mazoDeVelas(Offset(w * 0.905f, w * 0.205f), a, cerca, mm) }
    }

    /** Lo que está de pie sobre la cornisa, de atrás adelante. */
    fun DrawScope.delante(a: AireRevolucion, apoyo: Float, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width
        val pieA = apoyo - w * 0.003f; val pieF = apoyo + w * 0.004f
        sombras(a, apoyo, pieA, pieF, cerca)
        conLuz(m, -0.012f) { mm -> cajaDeTipos(a, pieA, cerca, mm) }
        conLuz(m, -0.02f) { mm -> resmas(Offset(w * 0.935f, pieA + w * 0.002f), a, cerca, mm) }
        conLuz(m, -0.025f) { mm -> pliegos(Offset(w * 0.14f, pieF), a, cerca, mm) }
        conLuz(m, -0.025f) { mm -> boteDeTinta(Offset(w * 0.335f, pieF), a, cerca, mm) }
        conLuz(m, -0.025f) { mm -> boteDeCola(Offset(w * 0.535f, pieF), a, cerca, mm) }
        candelero(Offset(w * MuebleEstanteRevolucion.VELA_X, pieA + w * 0.002f), a, cerca)
        conLuz(m, -0.025f) { mm -> jarra(Offset(w * 0.79f, pieF), a, cerca, mm) }
    }

    /**
     * Dibuja [dibujo] tal cual y, si hay mancha de ventana, otra vez alumbrado dentro de sus cuarterones (corridos [corrido]
     * anchos, porque está más adelante que la pared) y a media luz en el de papel: así la luz pinta lo que toca, con su borde.
     */
    private inline fun DrawScope.conLuz(m: ManchaRevolucion?, corrido: Float, dibujo: DrawScope.(ManchaRevolucion?) -> Unit) {
        dibujo(null)
        if (m != null) {
            val w = size.width
            clipPath(m.forma(w, corrido)) { dibujo(m) }
            clipPath(m.forma(w, corrido, papel = true)) { dibujo(ManchaRevolucion(m.q, m.color, m.fuerza * 0.5f)) }
        }
    }

    /** El color de [col] para lo que pinta la mancha [m] (si la hay), en un objeto con la luz [k]. */
    private fun c(a: AireRevolucion, col: Color, f: Float, k: Float, m: ManchaRevolucion?) = a.en(col, f, k, if (m != null) 1f else 0f, m)

    /** De qué lado le llega la luz a algo en [x]: de día, de la ventana (izquierda, -1); de noche, de la vela. */
    private fun hacia(a: AireRevolucion, x: Float, w: Float) = if (a.oscuro) (if (x < w * MuebleEstanteRevolucion.VELA_X) 1f else -1f) else -1f

    // ---------------------------------------------------------------- las sombras en la pared y en la cornisa

    /**
     * Las sombras de todo el bodegón, antes de las cosas: en la pared (de día, corridas a la derecha y abajo; de noche, hacia
     * fuera de la vela, más largas cerca de ella) y las de contacto en la cornisa.
     */
    private fun DrawScope.sombras(a: AireRevolucion, apoyo: Float, pieA: Float, pieF: Float, cerca: (Offset) -> Float) {
        val w = size.width
        val siluetas = listOf(
            Rect(w * 0.2f, pieA - w * 0.29f, w * 0.605f, pieA) to 0.26f,
            Rect(w * 0.05f, pieF - w * 0.11f, w * 0.235f, pieF) to 0.2f,
            Rect(w * 0.3f, pieF - w * 0.17f, w * 0.38f, pieF) to 0.2f,
            Rect(w * 0.49f, pieF - w * 0.16f, w * 0.58f, pieF) to 0.2f,
            Rect(w * 0.645f, w * MuebleEstanteRevolucion.VELA_Y, w * 0.685f, pieA) to 0.2f,
            Rect(w * 0.735f, pieF - w * 0.15f, w * 0.86f, pieF) to 0.22f,
            Rect(w * 0.85f, pieA - w * 0.15f, w * 1.02f, pieA) to 0.22f,
        )
        clipRect(0f, 0f, w, apoyo) {
            siluetas.forEach { (r, f) ->
                val p = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, androidx.compose.ui.geometry.CornerRadius(w * 0.01f))) }
                if (!a.oscuro) translate(w * (if (a.noche) 0.05f else 0.03f), w * (if (a.noche) 0.006f else 0.014f)) { drawPath(p, a.sombra(f)) }
                else {
                    val k = cerca(r.center)
                    if (k > 0.1f) {
                        val dx = (r.center.x / w - MuebleEstanteRevolucion.VELA_X).coerceIn(-0.3f, 0.3f) / 0.3f
                        translate(w * 0.035f * dx, w * 0.008f) { drawPath(p, a.sombra((0.4f * k).coerceAtMost(0.34f))) }
                    }
                }
            }
        }
        siluetas.forEach { (r, _) -> contactoRevolucion(Offset(r.center.x, minOf(r.bottom, pieF) + w * 0.002f), r.width * 1.02f, w * 0.01f, a, if (a.oscuro) 0.32f else 0.42f, hacia(a, r.center.x, w)) }
    }

    // ---------------------------------------------------------------- la caja de tipos

    /** Los cajetines de abajo, por renglón: cuántas novenas partes del ancho ocupa cada uno. */
    private val BAJOS = listOf(intArrayOf(1, 1, 2, 1, 2, 1, 1), intArrayOf(2, 1, 3, 1, 2), intArrayOf(1, 2, 1, 3, 2), intArrayOf(1, 1, 1, 2, 1, 2, 1))

    private fun DrawScope.cajaDeTipos(a: AireRevolucion, pie: Float, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val caja = Rect(w * 0.2f, pie - w * 0.29f, w * 0.605f, pie)
        val k = cerca(caja.center)
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        rotate(-2.4f, Offset(caja.center.x, caja.bottom)) {
            // El grueso del tablero: el canto de arriba (se apoya hacia atrás) y el de la derecha, en sombra.
            drawRect(cc(P.PINO_LUZ, -0.05f), Offset(caja.left + w * 0.004f, caja.top - w * 0.009f), Size(caja.width, w * 0.01f))
            drawRect(cc(P.PINO_OSC), Offset(caja.right, caja.top - w * 0.005f), Size(w * 0.008f, caja.height + w * 0.002f))
            drawRect(cc(P.PINO), caja.topLeft, caja.size)
            val t = w * 0.014f
            val dentro = caja.deflate(t)
            drawRect(a.claro(0.4f), caja.topLeft, Size(caja.width, 1.2f * u))
            drawRect(a.claro(0.3f), caja.topLeft, Size(1.2f * u, caja.height))
            drawRect(a.sombra(0.35f), Offset(caja.right - 1.6f * u, caja.top), Size(1.6f * u, caja.height))
            // Las vetas del marco y sus golpes.
            val vetas = Path()
            for (j in 0 until 4) { val y = caja.top + t * (0.2f + 0.2f * j); vetas.moveTo(caja.left + t, y); vetas.lineTo(caja.right - t, y + (azar(5000, j) - 0.5f) * 2f * u) }
            for (j in 0 until 4) { val y = caja.bottom - t * (0.2f + 0.2f * j); vetas.moveTo(caja.left + t, y); vetas.lineTo(caja.right - t, y + (azar(5001, j) - 0.5f) * 2f * u) }
            for (j in 0 until 3) { val x = caja.left + t * (0.25f + 0.25f * j); vetas.moveTo(x, caja.top + t); vetas.lineTo(x + (azar(5002, j) - 0.5f) * 2f * u, caja.bottom - t) }
            drawPath(vetas, cc(P.PINO_OSC).copy(alpha = 0.45f), style = Stroke(0.7f * u))
            // El fondo de la caja, hondo, y los cajetines: arriba, nueve por cuatro iguales; abajo, desparejos.
            drawRect(cc(P.PINO_OSC, -0.35f), dentro.topLeft, dentro.size)
            val celdas = ArrayList<Rect>()
            val alto0 = dentro.height * 0.4f
            val sep = w * 0.009f
            val cw = dentro.width / 9f
            for (f in 0 until 4) for (q in 0 until 9) celdas.add(Rect(dentro.left + cw * q, dentro.top + alto0 / 4 * f, dentro.left + cw * (q + 1), dentro.top + alto0 / 4 * (f + 1)))
            val y1 = dentro.top + alto0 + sep
            val fh = (dentro.bottom - y1) / BAJOS.size
            BAJOS.forEachIndexed { f, fila ->
                var q = 0
                fila.forEach { n -> celdas.add(Rect(dentro.left + cw * q, y1 + fh * f, dentro.left + cw * (q + n), y1 + fh * (f + 1))); q += n }
            }
            val plomo = Path(); val brillos = Path(); val huecos = Path(); val sombrasC = Path(); val tabiques = Path(); val cantos = Path()
            val tw = w * 0.0032f
            celdas.forEachIndexed { i, ce ->
                val cel = ce.deflate(tw / 2)
                tabiques.addRect(Rect(ce.left - tw / 2, ce.top - tw / 2, ce.right + tw / 2, ce.top + tw / 2))
                tabiques.addRect(Rect(ce.left - tw / 2, ce.top - tw / 2, ce.left + tw / 2, ce.bottom + tw / 2))
                cantos.addRect(Rect(ce.left - tw / 2, ce.top - tw / 2, ce.right + tw / 2, ce.top))
                // El plomo se junta abajo (la caja está ladeada hacia atrás): cada cajetín, más o menos lleno; alguno vacío.
                val lleno = 0.35f + 0.6f * azar(5100, i)
                if (azar(5101, i) > 0.1f) {
                    val pl = Rect(cel.left, cel.bottom - cel.height * lleno, cel.right, cel.bottom)
                    plomo.addRect(pl)
                    // Los tipos, de cara: puntos de luz y rendijas oscuras sobre el plomo, en hileras torcidas.
                    val n = (pl.width * pl.height / (w * w * 0.00007f)).toInt().coerceIn(3, 14)
                    for (q in 0 until n) {
                        val gx = pl.left + pl.width * (0.05f + 0.85f * azar(5200 + i, q)); val gy = pl.top + pl.height * (0.08f + 0.85f * azar(5300 + i, q))
                        if (q % 3 == 0) huecos.addRect(Rect(gx, gy, gx + w * 0.0034f, gy + w * 0.0018f))
                        else brillos.addRect(Rect(gx, gy, gx + w * 0.0026f, gy + w * 0.0014f))
                    }
                }
                sombrasC.addRect(Rect(cel.left, cel.top, cel.right, cel.top + minOf(cel.height * 0.26f, w * 0.01f)))
                sombrasC.addRect(Rect(cel.left, cel.top, cel.left + minOf(cel.width * 0.12f, w * 0.005f), cel.bottom))
            }
            drawPath(plomo, cc(P.PLOMO))
            drawPath(huecos, cc(P.TINTA).copy(alpha = 0.75f))
            drawPath(brillos, cc(P.PLOMO_LUZ, 0.15f))
            drawPath(sombrasC, a.sombra(0.45f))
            drawPath(tabiques, cc(P.PINO, 0.05f))
            drawPath(cantos, cc(P.PINO_LUZ, 0.1f))
            // El travesaño grueso entre la caja alta y la baja.
            drawRect(cc(P.PINO, 0.05f), Offset(dentro.left, dentro.top + alto0), Size(dentro.width, sep))
            drawRect(a.claro(0.3f), Offset(dentro.left, dentro.top + alto0), Size(dentro.width, 1f * u))
            drawRect(a.sombra(0.4f), Offset(dentro.left, dentro.top + alto0 + sep), Size(dentro.width, 1.4f * u))
            // Las manchas de tinta en el marco, donde lo agarran, y unos tipos sueltos en el canto de abajo.
            drawOval(Color(0xFF1E1820).copy(alpha = 0.42f), Offset(caja.left + w * 0.002f, caja.center.y), Size(w * 0.01f, w * 0.018f))
            drawOval(Color(0xFF1E1820).copy(alpha = 0.32f), Offset(caja.right - w * 0.012f, caja.top + w * 0.07f), Size(w * 0.009f, w * 0.016f))
            for (j in 0 until 4) drawRect(cc(P.PLOMO_LUZ), Offset(caja.left + caja.width * (0.3f + 0.12f * j + 0.04f * azar(5003, j)), caja.bottom - t * 0.7f), Size(w * 0.004f, w * 0.005f))
            if (a.oscuro && m == null) {
                // De noche el plomo de los cajetines cercanos a la vela echa chispas y el canto de la derecha arde.
                val kk = cerca(Offset(caja.right, caja.center.y))
                if (kk > 0.2f) clipRect(caja.center.x, caja.top, caja.right, caja.bottom) { drawPath(brillos, P.FUEGO.copy(alpha = (0.7f * kk).coerceAtMost(0.8f))) }
                drawRect(a.filo((0.8f * kk).coerceAtMost(0.7f)), Offset(caja.right - 1.4f * u, caja.top), Size(1.4f * u, caja.height))
                drawRect(a.filo((0.6f * kk).coerceAtMost(0.5f)), Offset(caja.right, caja.top), Size(w * 0.008f, caja.height))
            }
        }
    }

    // ---------------------------------------------------------------- la pila de pliegos con el componedor

    private fun DrawScope.pliegos(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val k = cerca(p - Offset(0f, w * 0.05f))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val an = w * 0.185f
        // Los cuadernillos doblados, uno sobre otro, desparejos; uno con su cubierta de papel azul y otro tostado.
        val capas = 8
        val alto = w * 0.098f
        var y = p.y
        for (i in 0 until capas) {
            val g = alto / capas * (0.75f + 0.5f * azar(5400, i))
            val dx = (azar(5401, i) - 0.5f) * w * 0.016f
            val r = Rect(p.x - an / 2 + dx, y - g, p.x + an / 2 + dx - w * 0.008f * azar(5402, i), y)
            val base = when (i) { 2 -> P.AZUL; 5 -> P.TOSTADO; else -> mezcla(P.PAPEL, P.TOSTADO, 0.35f * azar(5403, i)) }
            drawRect(cc(base), r.topLeft, r.size)
            // El lomo del doblez, redondo: claro arriba, hondo abajo; a la derecha, el corte con sus barbas.
            drawRect(a.claro(if (i == 2) 0.18f else 0.35f), Offset(r.left, r.top), Size(r.width, r.height * 0.3f))
            drawRect(a.sombra(0.28f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            drawLine(a.sombra(0.4f), Offset(r.left, r.bottom), Offset(r.right, r.bottom), 0.8f * u)
            drawRect(a.sombra(0.2f), Offset(r.right - w * 0.012f, r.top), Size(w * 0.012f, r.height))
            y -= g
        }
        // Un pliego que asoma, ladeado, por el medio de la pila.
        drawPath(poligonoRevolucion(p.x + an * 0.3f, p.y - alto * 0.45f, p.x + an * 0.62f, p.y - alto * 0.52f, p.x + an * 0.64f, p.y - alto * 0.42f, p.x + an * 0.32f, p.y - alto * 0.38f), cc(P.PAPEL_LUZ))
        // El bramante cruzado, con el nudo arriba.
        val bx = p.x + an * 0.1f
        drawLine(cc(P.BRAMANTE), Offset(bx, p.y), Offset(bx, y), 1.7f * u)
        drawLine(a.sombra(0.3f), Offset(bx + 1.2f * u, p.y), Offset(bx + 1.2f * u, y), 0.8f * u)
        // El componedor de latón encima: el listón largo con su escuadra y su tope, con dos renglones de tipos compuestos.
        val cy = y - w * 0.003f
        val comp = Rect(p.x - an * 0.48f, cy - w * 0.016f, p.x + an * 0.44f, cy)
        drawRect(a.sombra(0.4f), Offset(comp.left + 1.4f * u, comp.top + 1.8f * u), comp.size)
        drawRect(cc(P.LATON, -0.05f), comp.topLeft, comp.size)
        drawRect(cc(P.LATON_LUZ), comp.topLeft, Size(comp.width, w * 0.003f))
        drawRect(cc(P.LATON_OSC), Offset(comp.left, comp.bottom - w * 0.004f), Size(comp.width, w * 0.004f))
        drawRect(cc(P.PLOMO), Offset(comp.left + w * 0.012f, comp.top - w * 0.012f), Size(comp.width * 0.58f, w * 0.012f))
        drawRect(cc(P.PLOMO_LUZ), Offset(comp.left + w * 0.012f, comp.top - w * 0.012f), Size(comp.width * 0.58f, w * 0.002f))
        drawRect(cc(P.TINTA).copy(alpha = 0.6f), Offset(comp.left + w * 0.012f, comp.top - w * 0.006f), Size(comp.width * 0.58f, w * 0.0012f))
        drawRect(cc(P.LATON_OSC), Offset(comp.left, comp.top - w * 0.016f), Size(w * 0.007f, w * 0.016f))
        drawRect(cc(P.LATON), Offset(comp.left + comp.width * 0.66f, comp.top - w * 0.018f), Size(w * 0.009f, w * 0.018f))
        destelloRevolucion(Offset(comp.left + comp.width * 0.2f, comp.top + w * 0.003f), 1.5f * u, a, k)
        // El nudo del bramante y sus cabos.
        drawCircle(cc(P.BRAMANTE_OSC), 2.4f * u, Offset(bx, y + w * 0.004f))
        drawLine(cc(P.BRAMANTE), Offset(bx, y + w * 0.004f), Offset(bx + w * 0.022f, y + w * 0.022f), 1.2f * u, StrokeCap.Round)
        drawLine(cc(P.BRAMANTE), Offset(bx, y + w * 0.004f), Offset(bx - w * 0.014f, y + w * 0.026f), 1.2f * u, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- el bote de tinta de gres

    private fun DrawScope.boteDeTinta(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val an = w * 0.078f; val al = w * 0.09f
        val k = cerca(p - Offset(0f, al / 2))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val hc = hacia(a, p.x, w)
        val cuerpo = Path().apply {
            moveTo(p.x - an * 0.42f, p.y)
            cubicTo(p.x - an * 0.58f, p.y - al * 0.35f, p.x - an * 0.54f, p.y - al * 0.8f, p.x - an * 0.4f, p.y - al * 0.92f)
            lineTo(p.x + an * 0.4f, p.y - al * 0.92f)
            cubicTo(p.x + an * 0.54f, p.y - al * 0.8f, p.x + an * 0.58f, p.y - al * 0.35f, p.x + an * 0.42f, p.y)
            close()
        }
        // La espátula de madera, metida en el bote.
        val esp = poligonoRevolucion(p.x + an * 0.04f, p.y - al * 0.85f, p.x + an * 0.18f, p.y - al * 0.85f, p.x + an * 0.46f, p.y - al * 1.8f, p.x + an * 0.32f, p.y - al * 1.84f)
        drawPath(esp, cc(P.PINO))
        drawPath(poligonoRevolucion(p.x + an * 0.05f, p.y - al * 0.85f, p.x + an * 0.17f, p.y - al * 0.85f, p.x + an * 0.24f, p.y - al * 1.12f, p.x + an * 0.12f, p.y - al * 1.13f), cc(P.TINTA))
        drawPath(esp, cc(P.PINO_OSC).copy(alpha = 0.7f), style = Stroke(0.8f * u))
        drawPath(cuerpo, cc(P.GRES))
        clipPath(cuerpo) {
            cilindroRevolucion(Rect(p.x - an * 0.58f, p.y - al, p.x + an * 0.58f, p.y), a, k, hc, 1.3f)
            for (j in 0 until 12) drawCircle(cc(P.GRES_OSC).copy(alpha = 0.45f), w * 0.0024f, Offset(p.x + an * (azar(5500, j) - 0.5f), p.y - al * azar(5501, j)))
            drawRect(cc(P.GRES_OSC), Offset(p.x - an, p.y - al * 0.92f), Size(an * 2, al * 0.12f))
            for (j in 0 until 4) {
                val dx = p.x + an * (-0.35f + 0.25f * j + 0.06f * azar(5502, j))
                val largo = al * (0.18f + 0.45f * azar(5503, j))
                drawLine(cc(P.TINTA), Offset(dx, p.y - al * 0.9f), Offset(dx, p.y - al * 0.9f + largo), w * 0.005f, StrokeCap.Round)
                drawCircle(cc(P.TINTA), w * 0.004f, Offset(dx, p.y - al * 0.9f + largo))
            }
        }
        drawOval(cc(P.GRES_OSC, -0.1f), Offset(p.x - an * 0.42f, p.y - al * 0.97f), Size(an * 0.84f, al * 0.12f))
        drawOval(cc(P.TINTA), Offset(p.x - an * 0.34f, p.y - al * 0.955f), Size(an * 0.68f, al * 0.08f))
        destelloRevolucion(Offset(p.x - an * 0.14f, p.y - al * 0.93f), 1.3f * u, a, k)
        destelloRevolucion(Offset(p.x - an * 0.28f, p.y - al * 0.6f), 1.5f * u, a, k)
    }

    // ---------------------------------------------------------------- el bote de cola con su brocha

    private fun DrawScope.boteDeCola(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val an = w * 0.088f; val al = w * 0.074f
        val k = cerca(p - Offset(0f, al / 2))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val hc = hacia(a, p.x, w)
        val barro = Color(0xFFA8662E)
        val cuerpo = Path().apply {
            moveTo(p.x - an * 0.4f, p.y)
            lineTo(p.x - an * 0.5f, p.y - al * 0.88f); lineTo(p.x + an * 0.5f, p.y - al * 0.88f); lineTo(p.x + an * 0.4f, p.y); close()
        }
        // La brocha de pegar: el mango de madera inclinado y la cerda atada con bramante, que asoma por la boca.
        val mango = poligonoRevolucion(p.x - an * 0.16f, p.y - al * 0.7f, p.x - an * 0.02f, p.y - al * 0.72f, p.x + an * 0.3f, p.y - al * 2.05f, p.x + an * 0.18f, p.y - al * 2.08f)
        drawPath(mango, cc(P.PINO, 0.05f))
        drawPath(mango, cc(P.PINO_OSC), style = Stroke(0.8f * u))
        val atado = poligonoRevolucion(p.x - an * 0.24f, p.y - al * 0.8f, p.x + an * 0.04f, p.y - al * 0.84f, p.x + an * 0.04f, p.y - al * 1.12f, p.x - an * 0.2f, p.y - al * 1.1f)
        drawPath(atado, cc(P.BRAMANTE))
        for (j in 0 until 3) drawLine(cc(P.BRAMANTE_OSC), Offset(p.x - an * 0.23f, p.y - al * (0.9f + 0.07f * j)), Offset(p.x + an * 0.04f, p.y - al * (0.93f + 0.07f * j)), 0.8f * u)
        drawPath(cuerpo, cc(barro))
        clipPath(cuerpo) {
            cilindroRevolucion(Rect(p.x - an * 0.5f, p.y - al, p.x + an * 0.5f, p.y), a, k, hc, 1.2f)
            // El vidriado verde que solo cubre la mitad de arriba (el barro de abajo, crudo) y las chorreras de cola.
            drawRect(cc(Color(0xFF5E6A2E)), Offset(p.x - an, p.y - al), Size(an * 2, al * 0.55f))
            cilindroRevolucion(Rect(p.x - an * 0.5f, p.y - al, p.x + an * 0.5f, p.y - al * 0.45f), a, k, hc, 1.4f)
            drawLine(a.claro(0.25f), Offset(p.x - an, p.y - al * 0.45f), Offset(p.x + an, p.y - al * 0.45f), 0.8f * u)
            for (j in 0 until 3) {
                val dx = p.x + an * (-0.3f + 0.28f * j)
                val largo = al * (0.2f + 0.4f * azar(5600, j))
                drawLine(cc(P.PAPEL_LUZ, -0.05f), Offset(dx, p.y - al * 0.88f), Offset(dx + w * 0.001f, p.y - al * 0.88f + largo), w * 0.0065f, StrokeCap.Round)
            }
        }
        drawOval(cc(Color(0xFF3A3E18)), Offset(p.x - an * 0.5f, p.y - al * 0.94f), Size(an, al * 0.13f))
        drawOval(cc(P.PAPEL, -0.2f), Offset(p.x - an * 0.42f, p.y - al * 0.925f), Size(an * 0.84f, al * 0.09f))
        drawRect(a.claro(0.3f), Offset(p.x - an * 0.5f, p.y - al * 0.88f), Size(an, 1f * u))
        destelloRevolucion(Offset(p.x - an * 0.3f, p.y - al * 0.7f), 1.5f * u, a, k)
    }

    // ---------------------------------------------------------------- el candelero con la vela de sebo

    private fun DrawScope.candelero(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val k = if (a.oscuro) 1f else cerca(p)
        val vy = w * MuebleEstanteRevolucion.VELA_Y
        val plato = w * 0.078f
        val fuste = Path().apply {
            // El pie de platillo, el balaustre y el cubo con su arandela.
            moveTo(p.x - plato * 0.5f, p.y)
            lineTo(p.x - plato * 0.46f, p.y - w * 0.009f)
            lineTo(p.x - w * 0.013f, p.y - w * 0.015f)
            cubicTo(p.x - w * 0.004f, p.y - w * 0.022f, p.x - w * 0.014f, p.y - w * 0.037f, p.x - w * 0.006f, p.y - w * 0.05f)
            lineTo(p.x - w * 0.01f, p.y - w * 0.064f)
            lineTo(p.x - w * 0.022f, p.y - w * 0.068f)
            lineTo(p.x - w * 0.022f, p.y - w * 0.074f)
            lineTo(p.x + w * 0.022f, p.y - w * 0.074f)
            lineTo(p.x + w * 0.022f, p.y - w * 0.068f)
            lineTo(p.x + w * 0.01f, p.y - w * 0.064f)
            lineTo(p.x + w * 0.006f, p.y - w * 0.05f)
            cubicTo(p.x + w * 0.014f, p.y - w * 0.037f, p.x + w * 0.004f, p.y - w * 0.022f, p.x + w * 0.013f, p.y - w * 0.015f)
            lineTo(p.x + plato * 0.46f, p.y - w * 0.009f)
            lineTo(p.x + plato * 0.5f, p.y)
            close()
        }
        val boca = p.y - w * 0.074f
        // La vela de sebo, algo torcida, con sus chorreras.
        val vela = Path().apply {
            moveTo(p.x - w * 0.009f, boca)
            lineTo(p.x - w * 0.0075f, vy + w * 0.006f)
            quadraticTo(p.x, vy + w * 0.002f, p.x + w * 0.008f, vy + w * 0.006f)
            lineTo(p.x + w * 0.009f, boca); close()
        }
        drawPath(vela, a.dia(P.SEBO, 0f, k))
        clipPath(vela) {
            drawRect(a.sombra(0.22f), Offset(p.x + w * 0.003f, vy), Size(w * 0.01f, boca - vy))
            drawRect(a.claro(0.3f), Offset(p.x - w * 0.006f, vy), Size(w * 0.003f, boca - vy))
            if (a.oscuro) drawRect(P.FUEGO.copy(alpha = 0.4f), Offset(p.x - w * 0.01f, vy), Size(w * 0.02f, w * 0.035f))
        }
        listOf(-0.006f to 0.055f, 0.005f to 0.035f).forEach { (dx, largo) ->
            drawLine(a.dia(P.SEBO, 0.05f, k), Offset(p.x + w * dx, vy + w * 0.008f), Offset(p.x + w * dx * 1.3f, vy + w * largo), w * 0.0038f, StrokeCap.Round)
        }
        drawPath(fuste, a.dia(P.LATON, 0f, k))
        clipPath(fuste) {
            cilindroRevolucion(Rect(p.x - plato * 0.5f, boca, p.x + plato * 0.5f, p.y), a, k, if (a.oscuro) 1f else -1f, 1.4f)
            drawRect(a.dia(P.LATON_OSC, 0f, k), Offset(p.x - plato, p.y - w * 0.01f), Size(plato * 2, w * 0.002f))
        }
        drawOval(a.dia(P.LATON_LUZ, -0.1f, k), Offset(p.x - w * 0.022f, boca - w * 0.003f), Size(w * 0.044f, w * 0.006f))
        destelloRevolucion(Offset(p.x - w * 0.004f, p.y - w * 0.042f), 1.6f * u, a, k)
        destelloRevolucion(Offset(p.x - plato * 0.22f, p.y - w * 0.006f), 1.4f * u, a, k)
        // La despabiladera de hierro, apoyada en el platillo.
        drawLine(a.dia(P.HIERRO, 0f, k), Offset(p.x + plato * 0.08f, p.y - w * 0.006f), Offset(p.x + plato * 0.7f, p.y - w * 0.001f), w * 0.003f, StrokeCap.Round)
        drawOval(a.dia(P.HIERRO, 0f, k), Offset(p.x + plato * 0.58f, p.y - w * 0.006f), Size(w * 0.014f, w * 0.007f), style = Stroke(1.1f * u))
        // El pabilo y, si arde, la llama (de día, apagada: el pabilo negro).
        if (a.lumbre) llamaRevolucion(Offset(p.x + w * 0.0005f, vy + w * 0.001f), w * 0.0145f, if (a.oscuro && a.noche) 0.75f else if (!a.oscuro) 0.55f else 1f)
        else drawLine(P.TINTA, Offset(p.x, vy + w * 0.003f), Offset(p.x + w * 0.002f, vy - w * 0.006f), w * 0.0022f, StrokeCap.Round)
    }

    // ---------------------------------------------------------------- la jarra de loza

    private fun DrawScope.jarra(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val an = w * 0.105f; val al = w * 0.15f
        val k = cerca(p - Offset(0f, al / 2))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val hc = hacia(a, p.x, w)
        val cuerpo = Path().apply {
            moveTo(p.x - an * 0.3f, p.y)
            cubicTo(p.x - an * 0.62f, p.y - al * 0.2f, p.x - an * 0.6f, p.y - al * 0.55f, p.x - an * 0.28f, p.y - al * 0.74f)
            lineTo(p.x - an * 0.26f, p.y - al * 0.9f)
            lineTo(p.x - an * 0.44f, p.y - al)
            lineTo(p.x + an * 0.3f, p.y - al * 0.98f)
            lineTo(p.x + an * 0.26f, p.y - al * 0.88f)
            lineTo(p.x + an * 0.28f, p.y - al * 0.74f)
            cubicTo(p.x + an * 0.6f, p.y - al * 0.55f, p.x + an * 0.62f, p.y - al * 0.2f, p.x + an * 0.3f, p.y)
            close()
        }
        val asa = Path().apply {
            moveTo(p.x + an * 0.28f, p.y - al * 0.82f)
            cubicTo(p.x + an * 0.8f, p.y - al * 0.86f, p.x + an * 0.76f, p.y - al * 0.4f, p.x + an * 0.44f, p.y - al * 0.3f)
        }
        drawPath(asa, cc(P.LOZA_OSC), style = Stroke(w * 0.013f, cap = StrokeCap.Round))
        drawPath(asa, cc(P.LOZA), style = Stroke(w * 0.008f, cap = StrokeCap.Round))
        drawPath(cuerpo, cc(P.LOZA))
        clipPath(cuerpo) {
            cilindroRevolucion(Rect(p.x - an * 0.62f, p.y - al, p.x + an * 0.62f, p.y), a, k, hc, 1.5f)
            // La cenefa azul pintada a mano: dos filetes y, entre ellos, una onda seguida (sin trazos sueltos que se lean).
            val azul = cc(P.AZUL_LOZA)
            drawRect(azul, Offset(p.x - an, p.y - al * 0.69f), Size(an * 2, w * 0.0038f))
            drawRect(azul, Offset(p.x - an, p.y - al * 0.56f), Size(an * 2, w * 0.0028f))
            ondaRevolucion(p.x - an * 0.62f, p.x + an * 0.62f, p.y - al * 0.625f, al * 0.03f, an * 0.16f, azul, w * 0.0028f)
            drawRect(azul.copy(alpha = 0.85f), Offset(p.x - an, p.y - al * 0.12f), Size(an * 2, w * 0.0045f))
            // El borde desportillado: el barro rojizo que asoma bajo el esmalte.
            drawPath(poligonoRevolucion(p.x + an * 0.05f, p.y - al * 0.99f, p.x + an * 0.2f, p.y - al * 0.985f, p.x + an * 0.12f, p.y - al * 0.94f), cc(Color(0xFFC07A4E)))
            drawPath(poligonoRevolucion(p.x - an * 0.5f, p.y, p.x - an * 0.36f, p.y - al * 0.05f, p.x - an * 0.24f, p.y), cc(Color(0xFFC07A4E)))
        }
        drawPath(cuerpo, cc(P.LOZA_OSC, -0.2f), style = Stroke(0.8f * u))
        destelloRevolucion(Offset(p.x - an * 0.3f, p.y - al * 0.42f), 2.4f * u, a, k)
        destelloRevolucion(Offset(p.x - an * 0.18f, p.y - al * 0.85f), 1.5f * u, a, k)
        if (a.oscuro && m == null) drawPath(cuerpo, a.filo((0.7f * k).coerceAtMost(0.6f)), style = Stroke(1.3f * u))
    }

    // ---------------------------------------------------------------- las resmas envueltas, cortadas por el borde

    private fun DrawScope.resmas(p: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val k = cerca(p - Offset(0f, w * 0.06f))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val an = w * 0.18f
        val r1 = Rect(p.x - an / 2, p.y - w * 0.052f, p.x + an / 2, p.y)
        val r2 = Rect(p.x - an / 2 + w * 0.016f, r1.top - w * 0.048f, p.x + an / 2 + w * 0.012f, r1.top)
        val r3 = Rect(p.x - an / 2 - w * 0.004f, r2.top - w * 0.044f, p.x + an / 2 + w * 0.02f, r2.top)
        listOf(r1 to Color(0xFF7E8A96), r2 to P.ESTRAZA, r3 to mezcla(P.TOSTADO, P.ESTRAZA, 0.3f)).forEachIndexed { i, (r, col) ->
            drawRect(cc(col), r.topLeft, r.size)
            // Las puntas del envoltorio dobladas en la esquina de la cara, la luz arriba y la sombra abajo.
            drawPath(poligonoRevolucion(r.left, r.top, r.left + r.height * 0.6f, r.center.y, r.left, r.bottom), a.sombra(0.2f))
            drawRect(a.claro(0.3f), r.topLeft, Size(r.width, 1.2f * u))
            drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - r.height * 0.2f), Size(r.width, r.height * 0.2f))
            drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1.2f * u))
            // El bramante en cruz y la etiqueta del molino (un rectángulo con su recuadro y un renglón, sin letras).
            val bx = r.left + r.width * (0.3f + 0.08f * i)
            drawLine(cc(P.BRAMANTE), Offset(bx, r.top), Offset(bx, r.bottom), 1.6f * u)
            drawLine(cc(P.BRAMANTE), Offset(r.left, r.center.y + w * 0.004f), Offset(r.right, r.center.y - w * 0.002f), 1.4f * u)
            if (i != 1) {
                val et = Rect(r.left + r.width * 0.45f, r.top + r.height * 0.2f, r.left + r.width * 0.72f, r.top + r.height * 0.72f)
                drawRect(cc(P.PAPEL), et.topLeft, et.size)
                drawRect(cc(P.TINTA).copy(alpha = 0.6f), et.deflate(1.2f * u).topLeft, et.deflate(1.2f * u).size, style = Stroke(0.6f * u))
                drawRect(cc(P.TINTA).copy(alpha = 0.6f), Offset(et.left + et.width * 0.2f, et.top + et.height * 0.42f), Size(et.width * 0.6f, w * 0.002f))
            }
        }
        // Un pliego suelto, caído sobre la resma de arriba, que cuelga por delante.
        val pl = poligonoRevolucion(r3.left + w * 0.006f, r3.top, r3.left + w * 0.07f, r3.top - w * 0.002f, r3.left + w * 0.066f, r3.top + w * 0.058f, r3.left + w * 0.01f, r3.top + w * 0.05f)
        drawPath(pl, cc(P.PAPEL, 0.05f))
        drawPath(pl, a.sombra(0.2f), style = Stroke(0.7f * u))
        drawRect(cc(P.TINTA).copy(alpha = 0.7f), Offset(r3.left + w * 0.016f, r3.top + w * 0.008f), Size(w * 0.04f, w * 0.006f))
        for (j in 0 until 4) drawLine(cc(P.TINTA).copy(alpha = 0.45f), Offset(r3.left + w * 0.016f, r3.top + w * (0.022f + 0.008f * j)), Offset(r3.left + w * 0.058f, r3.top + w * (0.021f + 0.008f * j)), w * 0.002f)
    }

    // ---------------------------------------------------------------- el mazo de velas colgado

    private fun DrawScope.mazoDeVelas(clavo: Offset, a: AireRevolucion, cerca: (Offset) -> Float, m: ManchaRevolucion?) {
        val w = size.width; val u = density
        val k = cerca(clavo + Offset(0f, w * 0.05f))
        fun cc(col: Color, f: Float = 0f) = c(a, col, f, k, m)
        val velas = Path()
        val n = 6
        for (i in 0 until n) {
            val ang = (i - (n - 1) / 2f) * 0.08f
            val largo = w * (0.11f + 0.014f * azar(5700, i))
            val x0 = clavo.x + (i - 2.5f) * w * 0.0035f; val y0 = clavo.y + w * 0.022f
            val x1 = x0 + largo * ang; val y1 = y0 + largo
            val g = w * 0.007f
            velas.addPath(poligonoRevolucion(x0 - g * 0.8f, y0, x0 + g * 0.8f, y0, x1 + g, y1, x1 - g, y1))
        }
        if (m == null) translate(w * (if (a.oscuro) 0.022f else 0.018f), w * 0.01f) { drawPath(velas, a.sombra(if (a.oscuro) 0.3f * k else 0.22f)) }
        drawPath(velas, cc(P.SEBO))
        drawPath(velas, cc(P.SEBO_OSC), style = Stroke(0.8f * u))
        for (i in 0 until n) drawLine(cc(P.TINTA).copy(alpha = 0.8f), clavo, Offset(clavo.x + (i - 2.5f) * w * 0.0035f, clavo.y + w * 0.022f), 0.8f * u)
        drawRect(cc(P.BRAMANTE), Offset(clavo.x - w * 0.014f, clavo.y + w * 0.018f), Size(w * 0.028f, w * 0.006f))
        clavoRevolucion(clavo, 2f * u, a, k)
    }
}
