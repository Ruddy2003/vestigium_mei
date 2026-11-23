package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.AMBAR
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HIERRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.HUESO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA_LUZ
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LACA_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.LATON_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.MADERA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.NEGRO_LACA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.PETROLEO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaNeon.PETROLEO_LUZ
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.floor
import kotlin.math.pow

/** En qué repisa va la lámpara de pinza (arriba de ella), de [n]. */
internal fun filaLamparaNeon(n: Int): Int = floor(n * MuebleEstanteNeon.LAMP_F).toInt().coerceIn(0, maxOf(0, n - 1))

/**
 * El fondo de un casillero ([r], el hueco de la repisa [k]): las tablillas lacadas de verde petróleo con la luz que les llega a lo
 * ancho, la sombra franca de la tabla de arriba y la del costado que no da a la luz, los cantos de dentro de los costados, la
 * laca gastada y las vetas; el charco de la luz que tiene cerca; y la cara de arriba de la tabla de abajo, donde pisa lo guardado.
 */
internal fun DrawScope.casilleroNeon(k: Int, r: Rect, g: MarcoNeon) {
    val u = density; val w = g.w
    if (r.width <= 1f || r.height <= 1f) return
    // El fondo, con la luz a lo ancho (a media altura). En el diario, lejos de la lámpara, un velo de penumbra: lo que llega de
    // las luces vecinas (ver [caidasCasilleroNeon]) entra encima con su caída, y la repisa sigue siendo de noche.
    bandaNeon(r.left, r.right, r.top, r.bottom, PETROLEO, g, velo = if (!g.a.noche && k != filaLamparaNeon(g.n)) 0.26f else 0f)
    clipRect(r.left, r.top, r.right, r.bottom) {
        // Las juntas de las tablillas del fondo y sus vetas.
        val tablillas = 6
        for (j in 1 until tablillas) {
            val x = r.left + r.width * j / tablillas + w * 0.01f * (azar(5701 + k, j) - 0.5f)
            val t = g.t(x, r.center.y)
            drawLine(t.s(0.55f), Offset(x, r.top), Offset(x, r.bottom), 1.3f * u)
            drawLine(t.b(0.18f), Offset(x + 1.3f * u, r.top), Offset(x + 1.3f * u, r.bottom), 0.7f * u)
        }
        for (j in 0 until 10) {
            val x = r.left + r.width * azar(5711 + k, j)
            val t = g.t(x, r.center.y)
            drawLine(t.c(PETROLEO_LUZ, 0.8f).copy(alpha = 0.35f), Offset(x, r.top + r.height * azar(5712 + k, j) * 0.4f), Offset(x + 1f * u, r.bottom - r.height * azar(5713 + k, j) * 0.3f), 0.7f * u)
        }
        // La laca gastada abajo, donde se rozan las cosas: manchas más claras.
        for (j in 0 until 4) {
            val c = Offset(r.left + r.width * azar(5721 + k, j), r.bottom - r.height * (0.08f + 0.2f * azar(5722 + k, j)))
            val t = g.t(c)
            drawOval(t.c(PETROLEO_LUZ).copy(alpha = 0.4f), Offset(c.x - w * 0.03f, c.y - w * 0.008f), Size(w * (0.04f + 0.04f * azar(5723 + k, j)), w * 0.014f))
        }
        // La laca saltada del fondo, donde asoma la madera, y un cerco de agua.
        for (j in 0 until 2) {
            val c = Offset(r.left + r.width * (0.15f + 0.7f * azar(5731 + k, j)), r.top + r.height * (0.3f + 0.45f * azar(5732 + k, j)))
            val t = g.t(c)
            val s0 = w * (0.012f + 0.014f * azar(5733 + k, j))
            val p = poligonoNeon(c.x - s0, c.y - s0 * 0.3f, c.x - s0 * 0.2f, c.y - s0 * 0.8f, c.x + s0 * 0.9f, c.y - s0 * 0.4f, c.x + s0 * 0.6f, c.y + s0 * 0.5f, c.x - s0 * 0.4f, c.y + s0 * 0.6f)
            drawPath(p, t.c(MADERA, 0.75f))
            drawPath(p, t.b(0.35f), style = Stroke(0.7f * u))
        }
        val cerco = Offset(r.left + r.width * (0.2f + 0.6f * azar(5741, k)), r.top + r.height * 0.55f)
        drawOval(g.t(cerco).s(0.25f), Offset(cerco.x - w * 0.03f, cerco.y - w * 0.02f), Size(w * 0.06f, w * 0.045f), style = Stroke(1.4f * u))
        // El charco de cada luz cercana en el fondo, y lo que llega de las de fuera del casillero, con su caída.
        charcosCasilleroNeon(r, g)
        caidasCasilleroNeon(k, r, g)
        // La sombra franca de la tabla de arriba y el degradado que la sigue.
        val sTop = r.height * 0.11f
        drawRect(g.a.sombra(0.55f), r.topLeft, Size(r.width, sTop))
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(0.4f), g.a.sombra(0f)), r.top + sTop, r.top + r.height * 0.42f), Offset(r.left, r.top + sTop), Size(r.width, r.height * 0.31f))
        // Los cantos de dentro de los costados: el de la izquierda mira a la derecha y el de la derecha, a la izquierda.
        val canto = w * 0.016f
        val tl = g.t(r.left, r.center.y); val tr = g.t(r.right, r.center.y)
        drawPath(poligonoNeon(r.left, r.top, r.left + canto, r.top + canto * 1.2f, r.left + canto, r.bottom, r.left, r.bottom), tl.c(LACA_OSC, if (tl.izq) 0.55f else 1.05f))
        drawPath(poligonoNeon(r.right, r.top, r.right - canto, r.top + canto * 1.2f, r.right - canto, r.bottom, r.right, r.bottom), tr.c(LACA_OSC, if (tr.izq) 1.05f else 0.55f))
        // La sombra del costado que no da a la luz, sobre el fondo.
        if (tl.izq) drawRect(g.a.sombra(0.3f), Offset(r.right - canto - w * 0.03f, r.top), Size(w * 0.03f, r.height))
        else drawRect(g.a.sombra(0.3f), Offset(r.left + canto, r.top), Size(w * 0.03f, r.height))
        // La cara de arriba de la tabla de abajo (vista desde arriba), con el filo de su canto.
        val cara = w * 0.014f
        bandaNeon(r.left, r.right, r.bottom - cara, r.bottom, LACA_OSC, g, 0.9f)
        drawRect(g.a.sombra(0.4f), Offset(r.left, r.bottom - cara), Size(r.width, 1.2f * u))
    }
}

/** Los charcos de las luces que tiene cerca un casillero: el de cada luz, donde le llega más. */
private fun DrawScope.charcosCasilleroNeon(r: Rect, g: MarcoNeon) {
    val w = g.w
    val m = MuebleEstanteNeon
    val k = FloatArray(4)
    val donde = arrayOf(
        Offset(r.left + r.width * 0.12f, r.top + r.height * 0.4f),
        Offset((w * (m.ACU_X0 + m.ACU_X1) / 2).coerceIn(r.left, r.right), r.top + r.height * 0.3f),
        Offset(r.right - r.width * 0.16f, r.top + r.height * 0.45f),
        Offset(r.left + r.width * 0.18f, r.bottom - r.height * 0.2f),
    )
    for (i in 0 until 4) {
        if (g.a.fuerzas[i] <= 0f) continue
        // En los Sueños la ventana no deja charco: deja el paño, que pinta lo que toca (ver [bandaNeon]).
        if (i == 0 && g.a.noche) continue
        val p = donde[i]
        lucesNeon(p.x / w, p.y / w, g.a, g.lampY, g.estufaY, k)
        val ki = k[i]
        if (ki < 0.3f) continue
        mancha(p, r.height * (0.6f + 0.8f * ki), g.a.colores[i].copy(alpha = ((if (i == 0) 0.2f else 0.34f) * ki).coerceAtMost(0.42f)))
    }
}

/**
 * Una luz que llega al cuerpo desde fuera de un casillero: dónde está ([p], en píxeles), su color, su fuerza y hasta dónde llega
 * ([alcance], en píxeles). [lampara]: la de pinza, que en su propio casillero ya deja su charco.
 */
private class FuenteNeon(val p: Offset, val color: Color, val fuerza: Float, val alcance: Float, val lampara: Boolean = false,
                         val estufa: Boolean = false)

/**
 * Las luces de fuera de los casilleros: la lámpara de pinza (su ámbar sube a la repisa de arriba y baja a la de abajo), la estufa
 * (su naranja sube por las repisas de abajo hasta la cuarta) y, en los Sueños, el acuario azul, que cae en la primera repisa.
 */
private fun fuentesNeon(g: MarcoNeon): List<FuenteNeon> {
    val w = g.w; val m = MuebleEstanteNeon; val a = g.a
    val f = ArrayList<FuenteNeon>(3)
    if (a.lampara) f += FuenteNeon(Offset(w * m.LAMP_X, w * g.lampY), AMBAR, a.fuerzas[2] / 1.1f, g.fila * 1.85f, lampara = true)
    if (a.fuerzas[3] > 0f) f += FuenteNeon(Offset(w * m.ESTUFA_X, w * g.estufaY), a.colores[3], a.fuerzas[3], g.fila * 2.9f, estufa = true)
    if (a.noche) f += FuenteNeon(Offset(w * (m.ACU_X0 + m.ACU_X1) / 2, w * m.ACU_Y1), a.colores[1], a.fuerzas[1], g.fila * 1.7f)
    return f
}

/**
 * Lo que llega a un casillero ([r], la repisa [k]) de las luces de fuera, con su caída visible: el resplandor de cada una, que
 * entra por el lado que la mira y se apaga hacia el otro; y, en el diario, el rebote tenue del magenta de la ventana y del cian
 * del acuario que baja desde arriba, cada vez menos repisa a repisa. Tiñe sin subir el valor de lo que queda lejos.
 */
private fun DrawScope.caidasCasilleroNeon(k: Int, r: Rect, g: MarcoNeon) {
    val m = MuebleEstanteNeon
    fuentesNeon(g).forEach { f ->
        // La lámpara en su casillero ya deja su charco: aquí, lo que llega a los demás.
        if (f.lampara && k == filaLamparaNeon(g.n)) return@forEach
        if (f.estufa) {
            // La estufa: su calor sube por delante del mueble y entra en cada casillero por abajo, del lado de la estufa, cada
            // vez más tenue repisa arriba (en la cuarta todavía se ve).
            if (f.p.y < r.bottom) return@forEach
            val a = 0.62f * f.fuerza * kotlin.math.exp(-(f.p.y - r.bottom) / (1.3f * g.fila))
            if (a < 0.06f) return@forEach
            val c = Offset(f.p.x, r.bottom)
            val rr = g.fila * 0.95f
            scale(1.35f, 1f, c) {
                resplandorNeon(c, rr, f.color, a, 0.45f, r, 1.35f, 1f)
            }
            return@forEach
        }
        val dx = maxOf(0f, r.left - f.p.x, f.p.x - r.right); val dy = maxOf(0f, r.top - f.p.y, f.p.y - r.bottom)
        if (dx * dx + dy * dy > f.alcance * f.alcance) return@forEach
        resplandorNeon(f.p, f.alcance, f.color, 0.26f * f.fuerza, 0.4f, r)
    }
    // El rebote de arriba, solo en la segunda repisa: en la primera ya manda la luz de la ventana y del acuario, y más abajo es
    // tan tenue que no se ve (y cada uno es un degradado del ancho del casillero, que pesa).
    if (!g.a.noche && k == 1) {
        val a0 = 0.085f
        val alto = g.w * 0.5f
        listOf(PaletaNeon.ROSA to 0.2f, PaletaNeon.CIAN to (m.ACU_X0 + m.ACU_X1) / 2).forEach { (col, fx) ->
            resplandorNeon(Offset(g.w * fx, r.top - g.w * 0.12f), alto, col, a0, 0.5f, r)
        }
    }
}

/**
 * Un resplandor redondo de [col] con centro [c] y radio [radio] ([a] en el centro, el 40 % en [medio] y nada en el borde), pintado
 * solo donde se ve: en el trozo de [r] que cae dentro del 95 % de su radio (más allá queda menos del 1 %, y no se nota el corte).
 * [sx] y [sy]: la escala con que se dibuja, para llevar [r] a sus coordenadas.
 */
private fun DrawScope.resplandorNeon(c: Offset, radio: Float, col: Color, a: Float, medio: Float, r: Rect, sx: Float = 1f, sy: Float = 1f) {
    val util = radio * 0.95f
    // [r] en las coordenadas del dibujo escalado alrededor de [c].
    val l = c.x + (r.left - c.x) / sx; val d = c.x + (r.right - c.x) / sx
    val t = c.y + (r.top - c.y) / sy; val b = c.y + (r.bottom - c.y) / sy
    val x0 = maxOf(l, c.x - util); val x1 = minOf(d, c.x + util)
    val y0 = maxOf(t, c.y - util); val y1 = minOf(b, c.y + util)
    if (x1 <= x0 || y1 <= y0) return
    drawRect(Brush.radialGradient(0f to col.copy(alpha = a), medio to col.copy(alpha = a * 0.4f), 1f to col.copy(alpha = 0f), center = c, radius = radio),
        Offset(x0, y0), Size(x1 - x0, y1 - y0))
}

/**
 * Los filos de color de las luces de fuera en los cantos que las miran: el de arriba de cada tabla, si la luz está encima; el de
 * abajo (el junquillo), si está debajo; y el canto de dentro de cada costado, el izquierdo si la luz queda a su derecha y el
 * derecho si queda a su izquierda. Cada filo se apaga con la distancia a su luz.
 */
internal fun DrawScope.filosLucesNeon(g: MarcoNeon, tablas: List<Rect>) {
    val u = density
    val grueso = 1.6f * u
    fuentesNeon(g).forEach { f ->
        val col = mezcla(f.color, Color.White, 0.3f)
        fun alfa(x: Float, y: Float): Float {
            val ex = (x - f.p.x) / (if (f.estufa) 0.8f else 1f); val ey = (y - f.p.y) / (if (f.estufa) 1.45f else 1f)
            val d = kotlin.math.sqrt(ex * ex + ey * ey)
            return 0.6f * f.fuerza * (1f - d / f.alcance).coerceIn(0f, 1f).pow(1.4f)
        }
        tablas.forEach { r ->
            val arriba = f.p.y < r.top
            val y = if (arriba) r.top else r.bottom - grueso
            if (!arriba && f.p.y < r.bottom) return@forEach
            val paradas = Array(7) { i -> val fx = i / 6f; fx to col.copy(alpha = alfa(r.left + r.width * fx, y)) }
            if (paradas.all { it.second.alpha < 0.01f }) return@forEach
            drawRect(Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right), Offset(r.left, y), Size(r.width, grueso))
        }
        listOf(g.x0 - grueso to (f.p.x > g.x0), g.x1 to (f.p.x < g.x1)).forEach { (x, mira) ->
            if (!mira || g.fondo - g.ch < 2f) return@forEach
            val paradas = Array(9) { i -> val fy = i / 8f; fy to col.copy(alpha = 0.8f * alfa(x, g.ch + (g.fondo - g.ch) * fy)) }
            if (paradas.all { it.second.alpha < 0.01f }) return@forEach
            drawRect(Brush.verticalGradient(*paradas, startY = g.ch, endY = g.fondo), Offset(x, g.ch), Size(grueso, g.fondo - g.ch))
        }
    }
}

/**
 * Los costados de la alacena, del pie de la corona a la última tabla: la cara de laca con la luz que le llega a lo alto, la media
 * caña, el junquillo negro con su filete que enmarca los casilleros, las cantoneras de latón, las pletinas de las bisagras de las
 * puertas que ya no están, la laca saltada en el canto, la regleta de enchufes del costado izquierdo y el cable con sus bridas
 * del derecho, que baja de la lámpara.
 */
internal fun DrawScope.costadosNeon(g: MarcoNeon, tablas: List<Rect>) {
    val u = density; val w = g.w
    val y0 = g.ch; val y1 = g.fondo
    if (y1 - y0 < 2f) return
    listOf(true, false).forEach { izq ->
        val xa = if (izq) g.ml else g.x1
        val xb = if (izq) g.x0 else g.mr
        val ancho = xb - xa
        val xm = (xa + xb) / 2
        // La cara, con la luz a lo alto (ocho paradas, un solo trazo); en los Sueños, el paño de la farola con su borde.
        fun cara(gg: MarcoNeon) {
            val paradas = Array(8) { i -> val f = i / 7f; f to gg.t(xm, y0 + (y1 - y0) * f).c(LACA) }
            drawRect(Brush.verticalGradient(*paradas, startY = y0, endY = y1), Offset(xa, y0), Size(ancho, y1 - y0))
        }
        if (g.tocaPano(xa, xb, y0, y1)) { cara(g.sinPano); clipPath(g.panoRecorte) { cara(g.conPano) } } else cara(g)
        // El canto de fuera redondeado y la media caña.
        val n = 8
        for (j in 0 until n) {
            val ya = y0 + (y1 - y0) * j / n; val yb = y0 + (y1 - y0) * (j + 1) / n
            val t = g.t(xm, (ya + yb) / 2)
            val fuera = if (izq) xa else xb - 2.4f * u
            drawRect(if (izq == t.izq) t.b(0.55f) else t.s(0.4f), Offset(fuera, ya), Size(2.4f * u, yb - ya + 0.5f))
            val mc = xa + ancho * (if (izq) 0.32f else 0.62f)
            drawRect(t.s(0.38f), Offset(mc, ya), Size(1.6f * u, yb - ya + 0.5f))
            drawRect(t.b(0.42f), Offset(mc + 1.6f * u, ya), Size(1.2f * u, yb - ya + 0.5f))
        }
        // El junquillo negro con su filete, del lado de los casilleros.
        val jx = if (izq) xb - w * 0.008f else xa
        val parN = Array(6) { i -> val f = i / 5f; f to g.t(jx, y0 + (y1 - y0) * f).c(NEGRO_LACA) }
        drawRect(Brush.verticalGradient(*parN, startY = y0, endY = y1), Offset(jx, y0), Size(w * 0.008f, y1 - y0))
        val parL = Array(6) { i -> val f = i / 5f; f to g.t(jx, y0 + (y1 - y0) * f).c(LATON, 0.9f) }
        drawRect(Brush.verticalGradient(*parL, startY = y0, endY = y1), Offset(if (izq) jx + w * 0.002f else jx + w * 0.005f, y0), Size(maxOf(1f, 0.8f * u), y1 - y0))
        // La laca saltada en el canto de fuera, a la altura de la mano.
        for (j in 0 until 3) {
            val yy = y0 + (y1 - y0) * (0.3f + 0.25f * j + 0.08f * azar(5801 + (if (izq) 0 else 9), j))
            val t = g.t(xm, yy)
            val x = if (izq) xa + 1f * u else xb - 1f * u
            val s = if (izq) 1f else -1f
            drawPath(poligonoNeon(x, yy, x + s * w * 0.012f, yy + w * 0.004f, x + s * w * 0.008f, yy + w * 0.016f, x, yy + w * 0.02f), t.c(MADERA, 0.85f))
        }
        // Las cantoneras de latón de arriba y de abajo.
        listOf(y0, y1 - w * 0.04f).forEachIndexed { i, ya ->
            val r = Rect(xa, ya, xb, ya + w * 0.04f)
            val t = g.t(r.center)
            val forma = if (i == 0) poligonoNeon(r.left, r.top, r.right, r.top, r.right, r.top + r.height * 0.5f, r.center.x, r.bottom, r.left, r.top + r.height * 0.5f)
                else poligonoNeon(r.left, r.top + r.height * 0.5f, r.center.x, r.top, r.right, r.top + r.height * 0.5f, r.right, r.bottom, r.left, r.bottom)
            drawPath(Path().apply { addPath(forma, Offset(2f * u, 2f * u)) }, t.s(0.4f))
            drawPath(forma, t.c(LATON_OSC))
            clipPath(forma) {
                drawRect(t.c(LATON), r.topLeft, Size(r.width * 0.6f, r.height))
                drawRect(t.b(0.6f), r.topLeft, Size(r.width, 1.2f * u))
            }
            clavoNeon(Offset(r.center.x, r.top + r.height * (if (i == 0) 0.32f else 0.7f)), 1.3f * u, t)
        }
        // Las pletinas de las bisagras de las puertas de vidrio que ya no están (en el borde de los casilleros).
        if (y1 - y0 > w * 0.5f) listOf(0.18f, 0.78f).forEach { fy ->
            val yy = y0 + (y1 - y0) * fy
            val r = Rect(if (izq) xb - w * 0.026f else xa + w * 0.006f, yy, if (izq) xb - w * 0.006f else xa + w * 0.026f, yy + w * 0.05f)
            val t = g.t(r.center)
            drawRect(t.s(0.45f), r.topLeft + Offset(1.6f * u, 1.6f * u), r.size)
            drawRect(t.c(LATON_OSC), r.topLeft, r.size)
            drawRect(t.c(LATON), r.topLeft, Size(r.width * 0.45f, r.height))
            drawRect(t.b(0.5f), r.topLeft, Size(r.width, 1f * u))
            listOf(0.2f, 0.5f, 0.8f).forEach { f -> clavoNeon(Offset(r.center.x, r.top + r.height * f), 0.9f * u, t, HIERRO) }
        }
    }
    regletaNeon(g)
    cableCostadoNeon(g)
    pegatinasNeon(g)
}

/**
 * La regleta de enchufes atornillada al costado izquierdo: el plástico, tres enchufes llenos (dos clavijas, una blanca y una negra
 * de cargador, y abajo la que baja al suelo; ninguno vacío, que dos agujeros en un círculo se leen como una cara), el
 * interruptor con su piloto y los cables, que bajan juntos.
 */
private fun DrawScope.regletaNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    if (g.fondo - g.ch < w * 0.6f) return
    val xc = (g.ml + g.x0) / 2 - w * 0.004f
    val y = g.ch + (g.fondo - g.ch) * 0.58f
    val r = Rect(xc - w * 0.014f, y, xc + w * 0.014f, y + w * 0.15f)
    val t = g.t(r.center)
    drawRoundRect(t.s(0.5f), r.topLeft + Offset(2f * u, 2.4f * u), r.size, CornerRadius(2f * u))
    drawRoundRect(t.c(Color(0xFFD9D4CC)), r.topLeft, r.size, CornerRadius(2f * u))
    clipRect(r.left, r.top, r.right, r.bottom) { planosNeon(r, t, 0.8f) }
    // Las dos clavijas de arriba: el hueco del enchufe, la clavija con sus estrías y su cable, que baja pegado al plástico.
    listOf(Color(0xFFE4DED2), Color(0xFF1E1C24)).forEachIndexed { j, col ->
        val c = Offset(r.center.x, r.top + r.height * (0.32f + 0.24f * j))
        drawCircle(t.c(Color(0xFF8A847C)), r.width * 0.34f, c)
        val cl = Rect(c.x - r.width * 0.3f, c.y - r.width * (0.3f + 0.08f * j), c.x + r.width * 0.3f, c.y + r.width * 0.3f)
        drawRoundRect(t.s(0.5f), cl.topLeft + Offset(1.2f * u, 1.4f * u), cl.size, CornerRadius(1.6f * u))
        drawRoundRect(t.c(col), cl.topLeft, cl.size, CornerRadius(1.6f * u))
        clipRect(cl.left, cl.top, cl.right, cl.bottom) { planosNeon(cl, t, 0.9f) }
        for (e in 0 until 3) drawLine(t.s(0.35f), Offset(cl.left + cl.width * 0.2f, cl.top + cl.height * (0.25f + 0.2f * e)), Offset(cl.right - cl.width * 0.2f, cl.top + cl.height * (0.25f + 0.2f * e)), 0.6f * u)
        drawRect(t.b(0.45f), cl.topLeft, Size(cl.width, 0.8f * u))
        drawLine(t.c(col, 0.9f), Offset(cl.right - 1f * u, cl.bottom), Offset(r.right + 0.6f * u, r.bottom), 1.3f * u)
    }
    // El interruptor rojo, con su piloto encendido.
    val sw = Rect(r.left + r.width * 0.2f, r.top + r.height * 0.06f, r.right - r.width * 0.2f, r.top + r.height * 0.17f)
    drawRect(Color(0xFFD8362E), sw.topLeft, sw.size)
    drawRect(Color(0xFFFF8A6A).copy(alpha = 0.8f), sw.topLeft, Size(sw.width, sw.height * 0.4f))
    mancha(sw.center, w * 0.025f, Color(0xFFFF4A3A).copy(alpha = 0.35f))
    // El enchufe de abajo con su cable, que baja al suelo.
    val c = Offset(r.center.x, r.top + r.height * 0.8f)
    drawRoundRect(t.c(HIERRO), Offset(c.x - r.width * 0.36f, c.y - r.width * 0.3f), Size(r.width * 0.72f, r.width * 0.75f), CornerRadius(1.5f * u))
    val cable = Path().apply { moveTo(c.x, c.y + r.width * 0.4f); cubicTo(c.x + w * 0.01f, c.y + w * 0.06f, c.x - w * 0.012f, g.fondo - w * 0.02f, c.x, g.fondo + w * 0.02f) }
    drawPath(cable, t.c(Color(0xFF1A1820)), style = Stroke(1.8f * u))
}

/** El cable de la lámpara de pinza, que baja por el costado derecho sujeto con bridas blancas. */
private fun DrawScope.cableCostadoNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    val ly = g.ch + g.fila * (filaLamparaNeon(g.n) + 0.1f)
    val x = g.mr - w * 0.012f
    if (g.fondo <= ly) return
    val t0 = g.t(x, ly)
    drawLine(t0.s(0.35f), Offset(x + 1.6f * u, ly), Offset(x + 1.6f * u, g.fondo), 2f * u)
    val paradas = Array(5) { i -> val f = i / 4f; f to g.t(x, ly + (g.fondo - ly) * f).c(Color(0xFF1C1A24)) }
    drawRect(Brush.verticalGradient(*paradas, startY = ly, endY = g.fondo), Offset(x - 0.9f * u, ly), Size(1.8f * u, g.fondo - ly))
    var y = ly + w * 0.06f
    while (y < g.fondo - w * 0.02f) {
        val t = g.t(x, y)
        drawRect(t.c(HUESO), Offset(x - 2.2f * u, y), Size(4.4f * u, 1.6f * u))
        drawLine(t.c(HUESO), Offset(x + 2.2f * u, y + 0.8f * u), Offset(x + 4.4f * u, y + 2.6f * u), 1f * u)
        y += w * 0.14f
    }
}

/** Dos pegatinas gastadas en el costado derecho: una de franjas y una con una hoja ladeada, medio despegadas (sin letras ni signos). */
private fun DrawScope.pegatinasNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    if (g.fondo - g.ch < w * 0.45f) return
    val xc = (g.x1 + g.mr) / 2 - w * 0.004f
    listOf(0.12f to -6f, 0.86f to 8f).forEachIndexed { i, (fy, giro) ->
        val c = Offset(xc, g.ch + (g.fondo - g.ch) * fy)
        val t = g.t(c)
        val s = w * 0.017f
        rotate(giro, c) {
            val r = Rect(c.x - s, c.y - s * 1.3f, c.x + s, c.y + s * 1.3f)
            if (i == 0) {
                drawRect(t.c(Color(0xFF3FB8C8)), r.topLeft, r.size)
                drawRect(t.c(Color(0xFFF0C83A)), Offset(r.left, r.top + r.height * 0.33f), Size(r.width, r.height * 0.34f))
                drawRect(t.c(Color(0xFFE8466A)), Offset(r.left, r.top + r.height * 0.67f), Size(r.width, r.height * 0.33f))
            } else {
                // Una hoja verde sobre el óvalo crema.
                drawOval(t.c(HUESO), r.topLeft, r.size)
                val hoja = Path().apply { moveTo(c.x, r.top + s * 0.45f); quadraticTo(c.x + s * 0.75f, c.y, c.x, r.bottom - s * 0.45f); quadraticTo(c.x - s * 0.75f, c.y, c.x, r.top + s * 0.45f); close() }
                rotate(42f, c) {
                    drawPath(hoja, t.c(Color(0xFF3F8A4A)))
                    drawLine(t.c(Color(0xFF1E4A28)), Offset(c.x, r.top + s * 0.6f), Offset(c.x, r.bottom - s * 0.2f), 0.8f * u)
                }
            }
            drawPath(poligonoNeon(r.right - s * 0.6f, r.top, r.right, r.top, r.right, r.top + s * 0.6f), t.c(HUESO, 1.2f))
            drawPath(poligonoNeon(r.right - s * 0.6f, r.top, r.right, r.top + s * 0.6f, r.right - s * 0.7f, r.top + s * 0.7f), t.s(0.3f))
            for (j in 0 until 2) drawLine(t.c(HUESO).copy(alpha = 0.3f), Offset(r.left + r.width * azar(5901 + i, j), r.top), Offset(r.left + r.width * azar(5902 + i, j), r.bottom), 0.6f * u)
        }
    }
}

/**
 * La tabla de una repisa ([r], su canto de frente): la laca roja con la luz a lo ancho, el filo redondeado de arriba, el
 * junquillo negro con su filete dorado gastado, la laca saltada donde se saca lo guardado y la sombra que echa debajo.
 */
internal fun DrawScope.tablaNeon(k: Int, r: Rect, ultima: Boolean, g: MarcoNeon) {
    val u = density; val w = g.w
    if (r.width <= 1f || r.height <= 1f) return
    bandaNeon(r.left, r.right, r.top, r.bottom, LACA, g)
    bandaNeon(r.left, r.right, r.top, r.top + r.height * 0.16f, LACA_LUZ, g, 1.15f)
    val jq = r.top + r.height * 0.68f
    bandaNeon(r.left, r.right, jq, r.bottom, NEGRO_LACA, g)
    bandaNeon(r.left, r.right, jq + r.height * 0.08f, jq + r.height * 0.08f + maxOf(1f, 0.9f * u), LATON, g, 0.9f)
    drawRect(g.a.sombra(0.35f), Offset(r.left, jq - 1f * u), Size(r.width, 1f * u))
    // La laca saltada en el canto, donde se saca lo guardado.
    for (j in 0 until 3) {
        val x = r.left + r.width * (0.1f + 0.3f * j + 0.15f * azar(6001 + k, j))
        val t = g.t(x, r.top)
        drawPath(poligonoNeon(x, r.top + 1f * u, x + w * (0.012f + 0.012f * azar(6002 + k, j)), r.top + 1f * u, x + w * 0.009f, r.top + r.height * 0.3f, x + w * 0.002f, r.top + r.height * 0.22f), t.c(MADERA, 0.85f))
    }
    // La sombra que echa la tabla en el casillero de debajo (la de la última cae en los cajones).
    drawRect(g.a.sombra(0.5f), Offset(r.left, r.bottom), Size(r.width, (if (ultima) 3f else 2f) * u))
}

/**
 * El rótulo del mes de una tabla: una tira de cinta de rotular de plástico negro, pegada un poco torcida en el canto, con las
 * letras en relieve claras (su sombra y su luz), teñida por la luz que le llega.
 */
internal fun DrawScope.rotuloNeon(p: PlacaEstante, medidor: TextMeasurer, g: MarcoNeon) {
    val u = density
    val r = p.r
    if (r.width < 4f || r.height < 4f) return
    val t = g.t(r.center)
    val tam = 10.5f
    val estilo = TextStyle(fontFamily = PaletaNeon.LETRA, fontSize = tam.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.4f.sp)
    val texto = p.texto.uppercase()
    val medida = medidor.measure(texto, estilo, maxLines = 1, softWrap = false)
    val ancho = (medida.size.width + 16f * u).coerceAtMost(r.width)
    val alto = r.height * 0.86f
    val cinta = Rect(r.center.x - ancho / 2, r.center.y - alto / 2, r.center.x + ancho / 2, r.center.y + alto / 2)
    rotate(-0.8f, cinta.center) {
        drawRect(t.s(0.5f), cinta.topLeft + Offset(1.4f * u, 1.6f * u), cinta.size)
        drawRect(t.c(Color(0xFF17151C), 1.2f), cinta.topLeft, cinta.size)
        drawRect(t.b(0.45f), cinta.topLeft, Size(cinta.width, 1f * u))
        drawRect(t.b(0.18f), Offset(cinta.left, cinta.top + cinta.height * 0.15f), Size(cinta.width, cinta.height * 0.2f))
        // Las letras en relieve: la sombra corrida y la cara clara.
        val claro = mezcla(t.c(HUESO, 1.4f), HUESO, 0.35f)
        val dentro = Rect(cinta.left + 4f * u, cinta.top, cinta.right - 4f * u, cinta.bottom)
        translate(0.7f * u, 0.8f * u) { textoEn(medidor, texto, dentro, PaletaNeon.LETRA, Color(0xFF05040A), tam, peso = FontWeight.SemiBold, espaciado = 1.4f, minimo = 6f) }
        textoEn(medidor, texto, dentro, PaletaNeon.LETRA, claro, tam, peso = FontWeight.SemiBold, espaciado = 1.4f, minimo = 6f)
        // Las muescas de los cortes de la cinta.
        listOf(cinta.left, cinta.right).forEach { x -> drawLine(g.a.sombra(0.6f), Offset(x, cinta.top + cinta.height * 0.3f), Offset(x, cinta.bottom - cinta.height * 0.3f), 1f * u) }
    }
}

/**
 * La lámpara de pinza en el costado derecho, arriba de la repisa que le toca: la pinza de muelle negra mordiendo el canto, la
 * rótula, la pantalla cónica de aluminio vuelta hacia la izquierda y abajo, la bombilla encendida (ámbar) con su halo y su
 * reflejo en el borde. En los Sueños, apagada.
 */
internal fun DrawScope.lamparaPinzaNeon(g: MarcoNeon) {
    val u = density; val w = g.w
    if (g.n < 1) return
    val y = g.ch + g.fila * (filaLamparaNeon(g.n) + 0.04f)
    val xPinza = g.x1 + (g.mr - g.x1) * 0.35f
    val t = g.t(xPinza, y)
    val enc = g.a.lampara
    // La pinza: dos mandíbulas negras que muerden el costado, con su muelle.
    val pinza = Rect(xPinza - w * 0.016f, y - w * 0.008f, xPinza + w * 0.02f, y + w * 0.03f)
    drawRoundRect(t.s(0.45f), pinza.topLeft + Offset(2f * u, 2f * u), pinza.size, CornerRadius(2f * u))
    drawRoundRect(t.c(HIERRO), pinza.topLeft, pinza.size, CornerRadius(2f * u))
    drawRect(t.b(0.55f), pinza.topLeft, Size(pinza.width, 1f * u))
    drawArc(t.c(ALUMINIO), 180f, 180f, false, Offset(pinza.left + pinza.width * 0.2f, pinza.top - w * 0.012f), Size(pinza.width * 0.6f, w * 0.024f), style = Stroke(1.4f * u))
    // La rótula y el brazo corto hacia la pantalla.
    val rot = Offset(pinza.left + pinza.width * 0.2f, pinza.top + w * 0.004f)
    val boca = Offset(rot.x - w * 0.06f, rot.y + w * 0.05f)
    drawLine(t.c(ALUMINIO), rot, Offset(rot.x - w * 0.018f, rot.y + w * 0.01f), 2.4f * u)
    drawCircle(t.c(HIERRO), 2.6f * u, rot)
    // La pantalla: un cono truncado de aluminio, con la boca hacia abajo y a la izquierda.
    val eje = Offset(boca.x - (rot.x - w * 0.018f), boca.y - (rot.y + w * 0.01f))
    val largo = kotlin.math.sqrt(eje.x * eje.x + eje.y * eje.y)
    val angulo = Math.toDegrees(kotlin.math.atan2(eje.y.toDouble(), eje.x.toDouble())).toFloat()
    val cuello = Offset(rot.x - w * 0.018f, rot.y + w * 0.01f)
    rotate(angulo, cuello) {
        val l = largo
        val cono = poligonoNeon(cuello.x, cuello.y - w * 0.008f, cuello.x + l, cuello.y - w * 0.03f, cuello.x + l, cuello.y + w * 0.03f, cuello.x, cuello.y + w * 0.008f)
        drawPath(Path().apply { addPath(cono, Offset(2f * u, 3f * u)) }, t.s(0.4f))
        drawPath(cono, t.c(ALUMINIO, 0.75f))
        clipPath(cono) {
            drawRect(t.b(0.55f, 1.3f), Offset(cuello.x, cuello.y - w * 0.03f), Size(l, w * 0.012f))
            drawRect(t.s(0.35f), Offset(cuello.x, cuello.y + w * 0.012f), Size(l, w * 0.02f))
        }
        // La boca: el borde y, dentro, la bombilla.
        val bocaR = Rect(cuello.x + l - w * 0.008f, cuello.y - w * 0.03f, cuello.x + l + w * 0.008f, cuello.y + w * 0.03f)
        drawOval(if (enc) mezcla(AMBAR, Color.White, 0.55f) else Color(0xFF2A2A30), bocaR.topLeft, bocaR.size)
        drawOval(t.c(ALUMINIO), bocaR.topLeft, bocaR.size, style = Stroke(1.2f * u))
    }
    if (enc) {
        val bomb = Offset(cuello.x + eje.x, cuello.y + eje.y)
        mancha(bomb, w * 0.16f, AMBAR.copy(alpha = 0.35f))
        mancha(bomb, w * 0.05f, Color(0xFFFFE6B0).copy(alpha = 0.7f))
        drawCircle(Color(0xFFFFF2D8), 2.6f * u, bomb)
    }
    // El cable que sale de la pinza y baja.
    drawLine(t.c(Color(0xFF1C1A24)), Offset(pinza.right - 2f * u, pinza.bottom), Offset(g.mr - w * 0.012f, pinza.bottom + w * 0.02f), 1.8f * u)
}
