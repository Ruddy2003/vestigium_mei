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
import io.github.ruddy2003.vestigium.ui.ideario.vetas
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil
import kotlin.math.floor

/**
 * La pared y el suelo del estante de Tierras Antiguas ([MuebleEstanteAntiguas]): el enlucido de cal pintado de azul de
 * lapislázuli, con su sembrado de rombos de oro viejo gastado y sus grietas;
 * arriba, la viga de roble del techo y, debajo, la cenefa ocre con la ronda de las fases de la luna; a la izquierda, el muro
 * del costado en escorzo, donde está la ventana. Abajo, el suelo de tablas anchas de roble y la alfombra de lana de rombos
 * escalonados. La noche de lo de arriba, en un mapa chico de luz ([MapaLuzAntiguas]).
 */
internal object MuroEstanteAntiguas {
    private val P = PaletaAntiguas
    private val M = MuebleEstanteAntiguas

    /** La viga del techo y la cenefa, en anchos desde arriba. */
    const val VIGA = 0.056f
    const val CENEFA0 = 0.066f
    const val CENEFA1 = 0.112f

    /** El color de la pared con la luz de noche del momento (de día, tal cual): lo que se pinta debajo del mapa de luz. */
    private fun pc(a: AireAntiguas, c: Color): Color = colorParedAntiguas(a, c)

    // ---------------------------------------------------------------- la pared de arriba

    fun DrawScope.pared(g: MarcoAntiguas) {
        val w = g.w; val u = density
        val a = g.a
        val e = g.esquina
        val ap = g.apoyo
        // El enlucido de la pared del fondo, con su mano de azul y las pinceladas.
        val fondo = Rect(e, 0f, w, ap + 2f)
        drawRect(pc(a, P.LAPIS), fondo.topLeft, fondo.size)
        if (!a.lumbre) {
            // La luz de día: más clara junto a la ventana, más honda hacia la derecha.
            drawRect(Brush.horizontalGradient(listOf(P.LAPIS_LUZ.copy(alpha = 0.32f), Color.Transparent), e, e + w * 0.5f), fondo.topLeft, fondo.size)
        } else if (!a.oscuro) {
            drawRect(Brush.horizontalGradient(listOf(P.OCASO.copy(alpha = 0.12f), Color.Transparent), e, e + w * 0.55f), fondo.topLeft, fondo.size)
        }
        pinceladas(fondo, pc(a, P.LAPIS_LUZ), 7101, 34, u, 0.14f)
        pinceladas(fondo, pc(a, P.LAPIS_HONDO), 7102, 30, u, 0.16f)
        estarcido(fondo, a, 7110)
        grietas(fondo, a, 7130)
        // El muro del costado, en escorzo: más hondo (no le da el sol) y con su propia pincelada.
        val costado = poligonoAntiguas(0f, 0f, e, 0f, e, ap + 2f, 0f, ap + 2f)
        drawPath(costado, pc(a, mezcla(P.LAPIS, P.LAPIS_OSC, 0.55f)))
        clipPath(costado) {
            pinceladas(Rect(0f, 0f, e, ap), pc(a, P.LAPIS_LUZ), 7140, 8, u, 0.12f)
            // La luz que rebota del derrame en el muro del costado.
            if (!a.oscuro) mancha(Offset(w * 0.1f, w * 0.33f), w * 0.14f, (if (a.noche) P.OCASO else P.SOL).copy(alpha = 0.18f))
        }
        // La esquina: un poco más honda, y su arista.
        drawRect(Brush.horizontalGradient(listOf(a.sombra(0.3f), Color.Transparent), e, e + w * 0.05f), Offset(e, 0f), Size(w * 0.05f, ap))
        drawLine(a.sombra(0.4f), Offset(e, 0f), Offset(e, ap), 1.3f * u)
        cenefa(g)
        viga(g)
    }

    /**
     * El estarcido de oro viejo: un sembrado de rombos chicos al tresbolillo, cada uno con un punto a cada lado, gastado (falta
     * alguno y otros quedan a medias). Es tenue: lo que manda en la pared es el azul.
     */
    private fun DrawScope.estarcido(r: Rect, a: AireAntiguas, s: Int) {
        val w = size.width; val u = density
        val paso = w * 0.078f
        val rombos = Path()
        val puntos = Path()
        var fila = 0
        var yy = r.top + paso * 0.4f
        while (yy < r.bottom + paso) {
            var xx = r.left + (if (fila % 2 == 0) paso * 0.3f else paso * 0.8f)
            var col = 0
            while (xx < r.right + paso) {
                val z = azar(s + fila * 31, col)
                if (z > 0.16f) {
                    val q = 3.6f * u * (if (z > 0.85f) 0.7f else 1f)
                    rombos.addPath(poligonoAntiguas(xx, yy - q, xx + q * 0.72f, yy, xx, yy + q, xx - q * 0.72f, yy))
                    if (z > 0.3f) {
                        puntos.addOval(Rect(xx - q * 1.9f - 1f * u, yy - 1f * u, xx - q * 1.9f + 1f * u, yy + 1f * u))
                        puntos.addOval(Rect(xx + q * 1.9f - 1f * u, yy - 1f * u, xx + q * 1.9f + 1f * u, yy + 1f * u))
                    }
                }
                xx += paso; col++
            }
            yy += paso * 0.72f; fila++
        }
        val oro = pc(a, P.ORO)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(rombos, oro.copy(alpha = 0.4f))
            drawPath(puntos, oro.copy(alpha = 0.34f))
        }
    }

    /** Las grietas del enlucido: unas pocas, finas y quebradas. */
    private fun DrawScope.grietas(r: Rect, a: AireAntiguas, s: Int) {
        val u = density
        val p = Path()
        listOf(0.32f to 0.18f, 0.74f to 0.12f, 0.56f to 0.36f).forEachIndexed { i, (fx, fy) ->
            var q = Offset(r.left + r.width * fx, r.top + r.height * fy)
            p.moveTo(q.x, q.y)
            for (j in 0 until 6) {
                q = Offset(q.x + (azar(s + i, j) - 0.4f) * 9f * u, q.y + (5f + 6f * azar(s + i, j + 10)) * u)
                p.lineTo(q.x, q.y)
            }
        }
        drawPath(p, pc(a, P.LAPIS_HONDO).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        drawPath(p, a.claro(0.18f), style = Stroke(0.5f * u))
    }

    /**
     * La cenefa ocre bajo la viga: la ronda de las fases de la luna (de la nueva a la llena y otra vez a la nueva), el calendario
     * de la compañía, en oro viejo gastado entre dos filetes rojos.
     */
    private fun DrawScope.cenefa(g: MarcoAntiguas) {
        val w = g.w; val u = density
        val a = g.a
        val e = g.esquina
        val y0 = w * CENEFA0; val y1 = w * CENEFA1
        drawRect(pc(a, P.OCRE), Offset(e, y0), Size(w - e, y1 - y0))
        pinceladas(Rect(e, y0, w, y1), pc(a, P.OCRE_OSC), 7201, 18, u, 0.18f)
        drawRect(pc(a, P.RUBIA), Offset(e, y0), Size(w - e, 2.2f * u))
        drawRect(pc(a, P.RUBIA), Offset(e, y1 - 2.2f * u), Size(w - e, 2.2f * u))
        drawRect(a.sombra(0.3f), Offset(e, y1), Size(w - e, 2.4f * u))
        // Trece lunas: cada una con su parte de luz (oro) y su parte de sombra (ocre hondo).
        val n = 13
        val r = (y1 - y0) * 0.3f
        val cy = (y0 + y1) / 2
        val oro = pc(a, P.ORO_LUZ)
        val sombra = pc(a, P.OCRE_OSC)
        for (i in 0 until n) {
            val cx = e + (w - e) * (i + 0.5f) / n
            val fase = i / (n - 1f)                 // 0, nueva; 0.5, llena; 1, nueva otra vez
            val c = Offset(cx, cy)
            drawCircle(sombra, r, c)
            val luz = 1f - kotlin.math.abs(fase - 0.5f) * 2f   // 0 nueva, 1 llena
            if (luz > 0.04f) {
                val disco = Path().apply { addOval(Rect(cx - r, cy - r, cx + r, cy + r)) }
                // La sombra: un disco corrido hacia el lado oscuro (creciente a la derecha, menguante a la izquierda).
                val lado = if (fase < 0.5f) -1f else 1f
                val corre = 2f * r * luz
                val tapa = Path().apply { addOval(Rect(cx - r + lado * corre, cy - r, cx + r + lado * corre, cy + r)) }
                val parte = Path().apply { op(disco, tapa, androidx.compose.ui.graphics.PathOperation.Difference) }
                drawPath(if (luz > 0.96f) disco else parte, oro)
            }
            drawCircle(sombra, r, c, style = Stroke(0.8f * u))
            // Gastada: un poco de pintura que se fue.
            if (azar(7210, i) > 0.6f) drawCircle(pc(a, P.OCRE).copy(alpha = 0.7f), r * 0.35f, Offset(cx + r * 0.3f, cy + r * 0.25f))
        }
    }

    /** La viga de roble del techo, con las marcas de la azuela, y su sombra en la pared; en el costado, en escorzo. */
    private fun DrawScope.viga(g: MarcoAntiguas) {
        val w = g.w; val u = density
        val am = g.am
        val e = g.esquina
        val alto = w * VIGA
        val frente = Rect(e, 0f, w, alto)
        drawRect(am.dia(P.ROBLE_OSC, 0f, 1f), frente.topLeft, frente.size)
        vetas(frente, am.dia(P.ROBLE_HONDO, 0f, 1f).copy(alpha = 0.5f), 7301, densidad = 0.8f, grosor = 0.7f, ondulacion = 0.6f)
        // Las marcas de la azuela: golpes cortos en fila.
        for (i in 0 until 26) {
            val x = e + (w - e) * (i + 0.3f * azar(7302, i)) / 26f
            drawLine(am.dia(P.ROBLE, 0.08f, 1f).copy(alpha = 0.55f), Offset(x, alto * 0.3f), Offset(x + w * 0.02f, alto * 0.42f), 1.2f * u, StrokeCap.Round)
        }
        drawRect(am.dia(P.ROBLE_LUZ, 0.05f, 1f), Offset(e, alto - 2.6f * u), Size(w - e, 1.4f * u))
        drawRect(am.sombra(0.5f), Offset(e, alto - 1.2f * u), Size(w - e, 1.2f * u))
        drawRect(g.a.sombra(0.32f), Offset(e, alto), Size(w - e, w * 0.008f))
        // En el muro del costado la viga se acerca: su canto de abajo sube hacia el borde.
        val y0 = VentanaAntiguas.y(0f, VIGA)
        drawPath(poligonoAntiguas(0f, 0f, e, 0f, e, alto, 0f, y0), am.dia(P.ROBLE_HONDO, 0.04f, 1f))
        drawLine(am.dia(P.ROBLE_LUZ, 0.05f, 1f), Offset(0f, y0 - 1.6f * u), Offset(e, alto - 1.6f * u), 1.2f * u)
    }

    /**
     * La noche (y la sombra del día) de lo de arriba: un mapa chico de luz estirado sobre la pared y la cornisa. La pared se hunde
     * en el añil hondo y la cornisa en el pardo rojizo del roble; junto a la vela, encendido y con su calor.
     */
    fun DrawScope.nocheArriba(g: MarcoAntiguas) {
        val w = g.w
        val ap = g.apoyo / w
        val clave = "a:${w.toInt()}:${g.ch}:${g.a.oscuro}:${g.a.noche}"
        with(MapaLuzAntiguas) {
            pintar(0, clave, 0f, g.ch, 40) { x, y ->
                val k = g.cerca(Offset(x * w, y * w))
                mascaraAntiguas(if (y >= ap) g.am else g.a, x, y, k)
            }
        }
    }

    // ---------------------------------------------------------------- el suelo

    /** El suelo de tablas anchas de roble, que se abren hacia quien mira, y la alfombra de lana de rombos escalonados. */
    fun DrawScope.suelo(g: MarcoAntiguas) {
        val w = g.w; val h = g.h; val u = density
        val am = g.am
        val y0 = g.suelo
        if (h - y0 < 2f) return
        drawRect(am.dia(P.SUELO, 0f, 1f), Offset(0f, y0), Size(w, h - y0))
        // Las juntas de las tablas, que se abren hacia delante desde el punto de fuga.
        val fx = w * M.FUGA_X
        val juntas = Path()
        for (i in -6..16) {
            val xa = w * (i / 10f)
            val xb = fx + (xa - fx) * (1f + 1.3f * (h - y0) / w)
            juntas.moveTo(xa, y0); juntas.lineTo(xb, h)
        }
        drawPath(juntas, am.sombra(0.4f), style = Stroke(1.3f * u))
        drawPath(juntas, am.claro(0.12f), style = Stroke(0.6f * u))
        vetas(Rect(0f, y0, w, h), am.dia(P.ROBLE_OSC, 0f, 1f).copy(alpha = 0.22f), 7401, densidad = 0.5f, grosor = 0.6f, ondulacion = 0.6f)
        // Los clavos de las tablas.
        for (i in 0 until 9) {
            val x = w * (0.05f + 0.11f * i)
            drawCircle(am.dia(P.HIERRO, 0f, 1f), 1.4f * u, Offset(x, y0 + (h - y0) * 0.72f))
        }
        // La alfombra de lana: un trapecio que se abre hacia delante, con su orla de rombos escalonados y sus flecos.
        alfombra(g)
        // La sombra del mueble en el suelo, a la derecha y debajo del zócalo.
        drawRect(Brush.verticalGradient(listOf(am.sombra(0.5f), Color.Transparent), y0, y0 + w * 0.04f), Offset(0f, y0), Size(w, w * 0.04f))
    }

    /** La alfombra de lana: campo de rubia, orla de añil con rombos escalonados de ocre y crudo, y los flecos de delante. */
    private fun DrawScope.alfombra(g: MarcoAntiguas) {
        val w = g.w; val h = g.h; val u = density
        val am = g.am
        val y0 = g.suelo + w * 0.022f
        val y1 = h + 2f
        if (y1 - y0 < 4f) return
        val fx = w * M.FUGA_X
        fun abre(x: Float, y: Float) = fx + (x - fx) * (1f + 1.3f * (y - g.suelo) / w)
        val xl = w * 0.2f; val xr = w * 0.74f
        val forma = poligonoAntiguas(abre(xl, y0), y0, abre(xr, y0), y0, abre(xr, y1), y1, abre(xl, y1), y1)
        translate(2f * u, 2.4f * u) { drawPath(forma, am.sombra(0.35f)) }
        drawPath(forma, am.dia(P.RUBIA, 0f, 1f))
        clipPath(forma) {
            // La orla: dos bandas de añil con los rombos escalonados, arriba y a los lados.
            val banda = w * 0.034f
            val orla = Path().apply {
                addPath(poligonoAntiguas(abre(xl, y0), y0, abre(xr, y0), y0, abre(xr, y0 + banda), y0 + banda, abre(xl, y0 + banda), y0 + banda))
                addPath(poligonoAntiguas(abre(xl, y0), y0, abre(xl + banda, y0), y0, abre(xl + banda, y1), y1, abre(xl, y1), y1))
                addPath(poligonoAntiguas(abre(xr - banda, y0), y0, abre(xr, y0), y0, abre(xr, y1), y1, abre(xr - banda, y1), y1))
            }
            drawPath(orla, am.dia(P.ANIL, 0f, 1f))
            // Los rombos escalonados de la banda de arriba.
            val n = 9
            for (i in 0 until n) {
                val cx = abre(xl + banda + (xr - xl - 2 * banda) * (i + 0.5f) / n, y0 + banda / 2)
                drawPath(romboEscalonadoAntiguas(Offset(cx, y0 + banda / 2), banda * 0.36f), am.dia(if (i % 2 == 0) P.OCRE else P.CRUDO, 0f, 1f))
            }
            // En el campo, rombos grandes de añil con su corazón crudo, en fila hacia delante.
            for (j in 0 until 3) {
                val y = y0 + banda + w * (0.05f + 0.075f * j)
                listOf(0.33f, 0.47f, 0.61f).forEachIndexed { i, fxr ->
                    val c = Offset(abre(w * fxr, y), y)
                    val r = w * 0.028f * (1f + 0.25f * j)
                    drawPath(romboEscalonadoAntiguas(c, r), am.dia(P.ANIL, 0f, 1f))
                    drawPath(romboEscalonadoAntiguas(c, r * 0.5f), am.dia(if ((i + j) % 2 == 0) P.CRUDO else P.OCRE, 0f, 1f))
                }
            }
            // El pelo de la lana, gastado en el centro.
            pinceladas(Rect(0f, y0, w, y1), am.dia(P.CRUDO, 0f, 1f), 7501, 22, u, 0.06f)
            drawOval(am.dia(P.CRUDO, 0f, 1f).copy(alpha = 0.08f), Offset(w * 0.32f, y0 + w * 0.06f), Size(w * 0.36f, w * 0.12f))
        }
        // Los flecos de la orilla de arriba.
        val flecos = Path()
        var x = xl
        while (x < xr) {
            val xa = abre(x, y0)
            flecos.moveTo(xa, y0); flecos.lineTo(xa - 0.6f * u, y0 - 3.2f * u)
            x += w * 0.012f
        }
        drawPath(flecos, am.dia(P.CRUDO, 0f, 1f), style = Stroke(1f * u, cap = StrokeCap.Round))
    }
}

/** Un rombo escalonado de radio [r] alrededor de [c]: el contorno hecho en [n] escalones iguales por lado (no se lee como cruz). */
internal fun romboEscalonadoAntiguas(c: Offset, r: Float, n: Int = 3): Path {
    val pasos = n.coerceAtLeast(1)
    val d = r / (pasos + 0.5f)
    return Path().apply {
        val cuarto = ArrayList<Offset>()
        cuarto.add(Offset(d / 2, -r))
        for (i in 1..pasos) {
            cuarto.add(Offset(d / 2 + (i - 1) * d, -r + i * d))
            cuarto.add(Offset(d / 2 + i * d, -r + i * d))
        }
        val todo = ArrayList<Offset>()
        todo.addAll(cuarto)
        cuarto.asReversed().forEach { o -> todo.add(Offset(o.x, -o.y)) }
        cuarto.forEach { o -> todo.add(Offset(-o.x, -o.y)) }
        cuarto.asReversed().forEach { o -> todo.add(Offset(-o.x, o.y)) }
        todo.forEachIndexed { i, o -> if (i == 0) moveTo(c.x + o.x, c.y + o.y) else lineTo(c.x + o.x, c.y + o.y) }
        close()
    }
}

/** El corazón cálido de cada charco de luz, de noche, en el mapa de luz (en el roble). */
private val CALIDO_NOCHE = Color(0xFFC0702A)

/**
 * El color de la pared (el enlucido, su estarcido, el fondo de los casilleros) bajo el mapa de luz: de día, tal cual; al ocaso,
 * más hondo y tibio; de noche, apagado hacia el lapislázuli de la vela (menos azul real) y después lo hunde el mapa de luz.
 */
internal fun colorParedAntiguas(a: AireAntiguas, c: Color): Color = when {
    a.oscuro -> mezcla(c, Color(0xFF2A2840), 0.48f)
    a.noche -> Color(c.red * 0.8f, c.green * 0.8f, c.blue * 0.86f, c.alpha)
    else -> c
}

/**
 * Lo que se pone encima de lo que se pintó con la luz entera, en ([x], [y]) (anchos), con [k] de luz cálida: de noche, el oscuro
 * con color del aire [a] tanto como falte la luz, con el calor de la llama cerca; al ocaso, la sombra violeta que crece hacia
 * abajo y a la derecha; de día, la sombra fría lejos de la ventana.
 */
internal fun mascaraAntiguas(a: AireAntiguas, x: Float, y: Float, k: Float): Color {
    val P = PaletaAntiguas
    if (!a.lumbre) {
        val f = ((y - 0.4f) * 0.06f + (x - 0.25f).coerceAtLeast(0f) * 0.12f).coerceIn(0f, 0.17f)
        return P.FRIO.copy(alpha = f)
    }
    if (!a.oscuro) {
        // El ocaso: la tarde baja el azul de la pared hacia un azul hondo (el violeta queda en las sombras, no en un velo) y el roble
        // hacia un pardo; más hondo hacia la derecha y abajo, lejos de la ventana; la vela calienta lo que tiene cerca.
        val tarde = (0.3f + 0.3f * ((x - 0.25f) * 0.8f + (y - 0.5f) * 0.3f).coerceIn(0f, 1f)) * (1f - k)
        val calido = 0.14f * k
        val al = (tarde + calido).coerceIn(0f, 1f)
        val fondo = if (a.mueble) Color(0xFF2A160E) else P.AZUL_TARDE
        return mezcla(fondo, P.LLAMA, if (al > 0.001f) calido / al else 0f).copy(alpha = al)
    }
    if (!a.mueble) {
        // De noche, la pared: la luz se apaga más deprisa que en el roble (kk); lejos, añil casi negro; cerca de cada llama, el
        // azul cálido violáceo con su halo. Así se leen los charcos y no un fondo azul parejo.
        val kk = Math.pow(k.toDouble(), 1.5).toFloat()
        val hondo = Math.pow((1f - kk).toDouble(), 0.65).toFloat() * 0.97f
        val nucleo = ((kk - 0.18f) / 0.7f).coerceIn(0f, 1f)
        val f = nucleo * nucleo * (3f - 2f * nucleo)
        return mezcla(a.hondo, P.CALIDO_PARED, f).copy(alpha = maxOf(hondo, 0.45f * f))
    }
    // De noche, el roble: lejos de la luz, el pardo rojizo tapa casi todo; cerca, la luz deja ver su color y, junto a la llama, un
    // corazón cálido.
    val hondo = Math.pow((1f - k).toDouble(), 0.75).toFloat() * 0.93f
    val nucleo = ((k - 0.58f) / 0.4f).coerceIn(0f, 1f)
    val f = nucleo * nucleo * (3f - 2f * nucleo)
    val al = maxOf(hondo, 0.55f * f)
    return mezcla(a.hondo, CALIDO_NOCHE, f).copy(alpha = al)
}

/**
 * Los mapas chicos de luz del estante (lo de arriba, el cuerpo y el pie): una imagen de [cols] columnas pintada una vez por
 * tamaño y modo, estirada sobre su franja en un solo dibujo (en lugar de un degradado por pieza). Se guarda el último de cada uno.
 */
internal object MapaLuzAntiguas {
    private val claves = arrayOfNulls<String>(3)
    private val mapas = arrayOfNulls<ImageBitmap>(3)

    /** Pinta (si cambió) y estira sobre las filas de [y0] a [y1] (enteras hacia fuera) el mapa de la ranura [ranura]. */
    fun DrawScope.pintar(ranura: Int, clave: String, y0: Float, y1: Float, cols: Int, color: (Float, Float) -> Color) {
        val w = size.width
        val a = floor(y0).toInt(); val b = ceil(y1).toInt()
        if (b <= a || w < 2f || ranura !in 0..2) return
        val c = cols.coerceIn(4, 200)
        val filas = ((b - a) / (w / c)).toInt().coerceIn(2, 600)
        val img = mapas[ranura].takeIf { claves[ranura] == clave } ?: ImageBitmap(c, filas).also { nueva ->
            CanvasDrawScope().draw(Density(1f), layoutDirection, Canvas(nueva), Size(c.toFloat(), filas.toFloat())) {
                for (j in 0 until filas) for (i in 0 until c) {
                    val x = (i + 0.5f) / c
                    val y = (a + (j + 0.5f) / filas * (b - a)) / w
                    drawRect(color(x, y), Offset(i.toFloat(), j.toFloat()), Size(1f, 1f), blendMode = BlendMode.Src)
                }
            }
            mapas[ranura] = nueva; claves[ranura] = clave
        }
        drawImage(img, IntOffset.Zero, IntSize(img.width, img.height), IntOffset(0, a), IntSize(ceil(w).toInt(), b - a))
    }
}
