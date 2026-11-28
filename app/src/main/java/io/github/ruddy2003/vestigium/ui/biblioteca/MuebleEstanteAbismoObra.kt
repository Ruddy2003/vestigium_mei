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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.ideario.vetas

/**
 * El armario de las bitácoras de Abismo ([MuebleEstanteAbismo]): teca, como la estantería del Ideario, hecho por el carpintero de
 * ribera y empotrado en el costado del pañol. La cornisa (el tablero de arriba, la gola, el friso con el cordón tallado, el bocel y
 * las cantoneras de latón), los casilleros (el forro pintado del costado con la sombra franca de la tabla de arriba, los cantos de
 * dentro y la tabla vista desde arriba), las tablas con la barandilla de mar de latón, los costados gruesos con su moldura, sus
 * pernos al casco y sus cantoneras, las placas de latón estampadas de los meses y el zócalo con su chapa. Gastado donde se toca.
 * De día, la luz viene de la escotilla (arriba a la izquierda); de noche, cada tramo se apaga con la distancia a su luz.
 */
internal object ObraEstanteAbismo {
    private val P = PaletaAbismo

    /** Cuántos tramos lleva lo que se pinta a lo ancho: de noche la luz cambia de un tramo a otro sin escalón. */
    private fun tramos(a: AireAbismo, ancho: Float, w: Float) = if (a.lumbre) (ancho / (w * 0.045f)).toInt().coerceIn(2, 32) else 1

    /** [r] pintado con [color] (t de 0 a 1 a lo largo) muestreado en [n] + 1 puntos y unido por un degradado (de día, un color). */
    private fun DrawScope.degradado(r: Rect, vertical: Boolean, n: Int, color: (Float) -> Color) {
        if (r.width <= 0f || r.height <= 0f) return
        if (n <= 1) { drawRect(color(0.5f), r.topLeft, r.size); return }
        val paradas = Array(n + 1) { i -> (i / n.toFloat()) to color(i / n.toFloat()) }
        val pincel = if (vertical) Brush.verticalGradient(*paradas, startY = r.top, endY = r.bottom) else Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right)
        drawRect(pincel, r.topLeft, r.size)
    }

    /** Una franja a lo ancho de [r], de [y0] a [y1], del color [col] con la luz de cada tramo ([f] como en [AireAbismo.dia]). */
    private fun DrawScope.franja(r: Rect, y0: Float, y1: Float, col: Color, f: Float, a: AireAbismo, cerca: (Offset) -> Float, lit: Float = 0f, m: ManchaAbismo? = null) {
        val w = size.width
        degradado(Rect(r.left, y0, r.right, y1), false, tramos(a, r.width, w)) { t -> a.en(col, f, cerca(Offset(r.left + r.width * t, (y0 + y1) / 2)), lit, m) }
    }

    // ---------------------------------------------------------------- la cornisa (en la imagen guardada de arriba)

    /** La cornisa de [r] (de donde pisan las cosas hasta el arranque de las repisas, y un poco más) y, encima, la luz de la escotilla. */
    fun DrawScope.cornisa(r: Rect, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?) {
        val w = size.width
        cornisaCon(r, a, cerca, null, 0f)
        if (m != null) clipPath(m.forma(w, ManchaAbismo.FRENTE)) { cornisaCon(r, a, cerca, m, if (a.oscuro) 0.5f else 0.65f) }
    }

    private fun DrawScope.cornisaCon(r: Rect, a: AireAbismo, cerca: (Offset) -> Float, m: ManchaAbismo?, lit: Float) {
        val w = size.width; val u = density
        val y0 = r.top
        val sup0 = y0 - w * 0.012f; val sup1 = y0 + w * 0.004f
        val canto1 = y0 + w * 0.02f
        val gola1 = y0 + w * 0.031f
        val friso0 = y0 + w * 0.034f
        val friso1 = y0 + w * 0.063f
        val bocel1 = y0 + w * 0.075f
        // El tablero de arriba visto desde encima (más claro, con su veta y un cerco de sal) y su canto.
        franja(r, sup0, sup1, mezcla(P.TECA_LUZ, P.TECA, 0.3f), 0.05f, a, cerca, lit, m)
        vetas(Rect(r.left, sup0, r.right, sup1), a.dia(P.TECA_OSC, 0f, 0.6f).copy(alpha = 0.35f), 8201, densidad = 0.8f, grosor = 0.5f, ondulacion = 0.4f)
        drawOval(a.dia(Color(0xFFF2EEE2), 0f, cerca(Offset(w * 0.3f, y0))).copy(alpha = 0.35f), Offset(w * 0.26f, sup0 + (sup1 - sup0) * 0.25f), Size(w * 0.05f, (sup1 - sup0) * 0.5f), style = Stroke(1f * u))
        franja(r, sup1, canto1, P.TECA, 0.02f, a, cerca, lit, m)
        franja(r, sup1, sup1 + 1.4f * u, P.TECA_LUZ, 0.25f, a, cerca, lit, m)
        vetas(Rect(r.left, sup1, r.right, canto1), a.dia(P.TECA_OSC, 0f, 0.6f).copy(alpha = 0.3f), 8202, densidad = 0.8f, grosor = 0.5f)
        // La gola: clara arriba y honda abajo.
        franja(r, canto1, canto1 + (gola1 - canto1) * 0.45f, P.TECA_LUZ, -0.05f, a, cerca, lit, m)
        franja(r, canto1 + (gola1 - canto1) * 0.45f, gola1, P.TECA_OSC, 0f, a, cerca, lit, m)
        franja(r, gola1, friso0, P.TECA_HONDA, 0f, a, cerca, lit, m)
        // El friso con el cordón tallado: los cabos torcidos, cada vuelta con su luz arriba a la izquierda y su sombra abajo.
        franja(r, friso0, friso1, P.TECA, -0.08f, a, cerca, lit, m)
        cordon(Rect(r.left + w * 0.02f, friso0 + (friso1 - friso0) * 0.18f, r.right - w * 0.02f, friso1 - (friso1 - friso0) * 0.18f), a, cerca, lit, m)
        // El bocel: medio cilindro.
        franja(r, friso1, friso1 + (bocel1 - friso1) * 0.45f, P.TECA_LUZ, 0f, a, cerca, lit, m)
        franja(r, friso1 + (bocel1 - friso1) * 0.45f, bocel1, P.TECA, -0.12f, a, cerca, lit, m)
        // El larguero de arriba del armario, hasta el arranque de las repisas.
        franja(r, bocel1, r.bottom, P.TECA_OSC, -0.04f, a, cerca, lit, m)
        if (lit > 0f) return
        drawRect(a.sombra(0.4f), Offset(r.left, gola1), Size(r.width, 1.2f * u))
        drawRect(a.sombra(0.35f), Offset(r.left, bocel1 - 1.2f * u), Size(r.width, 1.2f * u))
        // Los golpes del canto (asoma la teca clara) y las cantoneras de latón en las esquinas.
        for (j in 0 until 6) {
            val x = r.left + r.width * (0.06f + 0.88f * azar(8203, j))
            drawLine(a.brillo(0.35f, cerca(Offset(x, canto1))), Offset(x, sup1 + 2f * u), Offset(x + w * 0.015f, sup1 + (canto1 - sup1) * (0.5f + 0.4f * azar(8204, j))), 0.7f * u)
        }
        cantonera(Offset(r.left, sup1), bocel1 - sup1, false, a, cerca(Offset(r.left, sup1)))
        cantonera(Offset(r.right, sup1), bocel1 - sup1, true, a, cerca(Offset(r.right, sup1)))
    }

    /** El cordón tallado del friso, en [r]: vueltas inclinadas, cada una un lóbulo con su luz arriba a la izquierda y su sombra. */
    private fun DrawScope.cordon(r: Rect, a: AireAbismo, cerca: (Offset) -> Float, lit: Float, m: ManchaAbismo?) {
        val u = density
        val paso = r.height * 0.9f
        if (paso < 1f) return
        val luzP = Path(); val sombraP = Path(); val surco = Path()
        var x = r.left
        while (x < r.right) {
            // Una vuelta: un paralelogramo inclinado con las puntas redondas.
            val x0 = x; val x1 = x + paso
            surco.moveTo(x0, r.bottom); surco.lineTo(x0 + paso * 0.9f, r.top)
            luzP.moveTo(x0 + paso * 0.18f, r.bottom - r.height * 0.15f)
            luzP.quadraticTo(x0 + paso * 0.4f, r.center.y - r.height * 0.25f, x0 + paso * 0.95f, r.top + r.height * 0.12f)
            sombraP.moveTo(x0 + paso * 0.05f, r.bottom - r.height * 0.05f)
            sombraP.quadraticTo(x0 + paso * 0.55f, r.center.y + r.height * 0.3f, x1 - paso * 0.05f, r.top + r.height * 0.2f)
            x += paso
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            val n = tramos(a, r.width, size.width)
            if (n <= 1) {
                drawPath(sombraP, a.sombra(0.35f), style = Stroke(r.height * 0.18f))
                drawPath(luzP, a.en(P.TECA_LUZ, 0.15f, 1f, lit, m), style = Stroke(r.height * 0.16f))
                drawPath(surco, a.sombra(0.6f), style = Stroke(1.2f * u))
            } else {
                val paradas = Array(n + 1) { i -> (i / n.toFloat()) to a.en(P.TECA_LUZ, 0.1f, cerca(Offset(r.left + r.width * i / n, r.center.y)), lit, m) }
                drawPath(sombraP, a.sombra(0.4f), style = Stroke(r.height * 0.18f))
                drawPath(luzP, Brush.horizontalGradient(*paradas, startX = r.left, endX = r.right), style = Stroke(r.height * 0.16f))
                drawPath(surco, a.sombra(0.7f), style = Stroke(1.2f * u))
            }
        }
    }

    /** El cordón tallado a lo alto, en [r]: vueltas inclinadas con su luz a la izquierda y su sombra; de noche, a tramos de luz. */
    private fun DrawScope.cordonVertical(r: Rect, a: AireAbismo, cerca: (Offset) -> Float, n: Int) {
        val u = density
        val paso = r.width * 0.9f
        if (paso < 1f || r.height < paso) return
        val luzP = Path(); val sombraP = Path(); val surco = Path()
        var y = r.top
        var j = 0
        while (y < r.bottom && j < 400) {
            surco.moveTo(r.left, y + paso * 0.9f); surco.lineTo(r.right, y)
            luzP.moveTo(r.left + r.width * 0.12f, y + paso * 0.75f)
            luzP.quadraticTo(r.left + r.width * 0.3f, y + paso * 0.3f, r.right - r.width * 0.15f, y + paso * 0.05f)
            sombraP.moveTo(r.left + r.width * 0.3f, y + paso * 0.95f)
            sombraP.quadraticTo(r.left + r.width * 0.75f, y + paso * 0.7f, r.right - r.width * 0.05f, y + paso * 0.2f)
            y += paso; j++
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawPath(sombraP, a.sombra(0.4f), style = Stroke(r.width * 0.2f))
            if (n <= 1) drawPath(luzP, a.dia(P.TECA_LUZ, 0.15f, 1f), style = Stroke(r.width * 0.18f))
            else {
                val paradas = Array(n + 1) { i -> (i / n.toFloat()) to a.dia(P.TECA_LUZ, 0.1f, cerca(Offset(r.center.x, r.top + r.height * i / n))) }
                drawPath(luzP, Brush.verticalGradient(*paradas, startY = r.top, endY = r.bottom), style = Stroke(r.width * 0.18f))
            }
            drawPath(surco, a.sombra(0.65f), style = Stroke(1.1f * u))
        }
    }

    /** Una cantonera de latón en la esquina de un canto: la chapa doblada con su tornillo y su verdín; [der] si es la del lado derecho. */
    fun DrawScope.cantonera(p: Offset, alto: Float, der: Boolean, a: AireAbismo, k: Float) {
        val u = density
        val an = size.width * 0.032f
        val r = if (der) Rect(p.x - an, p.y, p.x, p.y + alto) else Rect(p.x, p.y, p.x + an, p.y + alto)
        drawRect(a.sombra(0.3f), Offset(r.left + 1f * u, r.top + 1.2f * u), r.size)
        drawRect(a.dia(P.LATON, 0f, k), r.topLeft, r.size)
        drawRect(a.brillo(0.55f, k), r.topLeft, Size(r.width, 0.9f * u))
        drawRect(if (der) a.sombra(0.25f) else a.brillo(0.3f, k), Offset(if (der) r.right - 1.2f * u else r.left, r.top), Size(1.2f * u, r.height))
        drawRect(a.sombra(0.25f), Offset(r.left, r.bottom - 1f * u), Size(r.width, 1f * u))
        drawCircle(a.dia(mezcla(P.LATON, P.TECA_OSC, 0.45f), 0f, k), 0.9f * u, Offset(r.center.x, r.top + r.height * 0.5f))
        drawOval(a.dia(P.VERDIN, 0f, k).copy(alpha = 0.5f), Offset(r.left + r.width * 0.1f, r.bottom - r.height * 0.3f), Size(r.width * 0.5f, r.height * 0.22f))
    }

    // ---------------------------------------------------------------- el cuerpo (en cada dibujo)

    /**
     * El casillero [k] en [r]: el forro pintado del costado del barco (el mismo de la pared) con sus juntas, en la sombra franca de la
     * tabla de arriba y del costado de la izquierda, con su salitre y algún desconchón; los cantos de dentro de los costados y la
     * tabla vista desde arriba.
     */
    fun DrawScope.casillero(k: Int, r: Rect, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (r.width < 2f || r.height < 2f) return
        val n = tramos(a, r.width, w)
        // El fondo: la misma pintura blanca rota, en la sombra del hueco.
        degradado(r, false, n) { t -> a.dia(mezcla(P.MAMPARO, P.MAMPARO_OSC, 0.42f), 0f, cerca(Offset(r.left + r.width * t, r.center.y))) }
        // Las juntas de las tablas del forro.
        val tabla = w * 0.052f
        var y = r.top + tabla * (0.3f + 0.4f * azar(8210, k))
        var i = 0
        while (y < r.bottom) {
            val kk = cerca(Offset(r.center.x, y))
            drawRect(a.sombra(0.3f), Offset(r.left, y - 1.2f * u), Size(r.width, 1.2f * u))
            drawRect(a.sombra(0.1f), Offset(r.left, y), Size(r.width, 2.4f * u))
            drawRect(a.brillo(0.3f, kk), Offset(r.left, y - 2.4f * u), Size(r.width, 1f * u))
            val xt = r.left + r.width * (0.15f + 0.7f * azar(8211 + k, i))
            drawRect(a.sombra(0.22f), Offset(xt, y), Size(1f * u, minOf(tabla, r.bottom - y)))
            y += tabla; i++
        }
        // El salitre, un desconchón y un clavo con su chorrera.
        for (j in 0 until 3) {
            val p = Offset(r.left + r.width * azar(8212 + k, j), r.top + r.height * (0.35f + 0.55f * azar(8213 + k, j)))
            drawOval(a.dia(Color(0xFFF2EFE4), 0f, cerca(p)).copy(alpha = if (a.oscuro) 0.18f else 0.4f), p, Size(w * (0.04f + 0.05f * azar(8214, j + k)), w * 0.01f))
        }
        val pd = Offset(r.left + r.width * (0.2f + 0.6f * azar(8215, k)), r.top + r.height * (0.4f + 0.3f * azar(8216, k)))
        val s = w * 0.012f
        drawPath(poligonoAbismo(pd.x, pd.y, pd.x + s, pd.y - s * 0.3f, pd.x + s * 1.5f, pd.y + s * 0.3f, pd.x + s * 0.7f, pd.y + s * 0.7f), a.dia(mezcla(P.TECA, P.MAMPARO_OSC, 0.45f), 0f, cerca(pd)))
        val pc = Offset(r.left + r.width * (0.1f + 0.8f * azar(8217, k)), r.top + r.height * 0.3f)
        drawRect(a.dia(P.OXIDO, 0f, cerca(pc)).copy(alpha = 0.26f), Offset(pc.x - 0.8f * u, pc.y), Size(1.6f * u, w * 0.04f))
        drawCircle(a.dia(mezcla(P.OXIDO, P.TINTA, 0.35f), 0f, cerca(pc)), 1.3f * u, pc)
        // La sombra franca de la tabla de arriba (corrida hacia abajo) y la del costado de la izquierda (la luz baja de la izquierda).
        val hs = r.height * 0.2f
        val fs = if (!a.oscuro && a.noche) 0.62f else 1f
        drawRect(a.sombra((if (a.oscuro) 0.5f else 0.42f) * fs), r.topLeft, Size(r.width, hs * 0.45f))
        drawRect(a.sombra(0.22f * fs), Offset(r.left, r.top + hs * 0.45f), Size(r.width, hs * 0.55f))
        drawRect(a.sombra(0.1f * fs), Offset(r.left, r.top + hs), Size(r.width, hs * 0.6f))
        val ls = w * 0.022f
        drawPath(poligonoAbismo(r.left, r.top, r.left + ls * 1.6f, r.top, r.left + ls, r.bottom, r.left, r.bottom), a.sombra((if (a.oscuro) 0.3f else 0.26f) * fs))
        // Los cantos de dentro de los costados: el de la izquierda en sombra; el de la derecha recibe la luz.
        val cw = w * 0.012f
        drawPath(poligonoAbismo(r.left, r.top, r.left + cw, r.top + cw, r.left + cw, r.bottom - cw * 0.3f, r.left, r.bottom), a.dia(P.TECA_OSC, -0.1f, cerca(Offset(r.left, r.center.y))))
        degradado(Rect(r.right - cw, r.top, r.right, r.bottom), true, if (a.lumbre) 6 else 1) { t -> a.dia(P.TECA, 0.06f, cerca(Offset(r.right, r.top + r.height * t))) }
        drawPath(poligonoAbismo(r.right - cw, r.top, r.right, r.top, r.right, r.top + cw, r.right - cw, r.top + cw * 1.6f), a.sombra(0.4f))
        // La tabla vista desde arriba, al pie del hueco (lo guardado se para en ella).
        val ft = w * 0.011f
        degradado(Rect(r.left, r.bottom - ft, r.right, r.bottom), false, n) { t -> a.dia(mezcla(P.TECA_LUZ, P.TECA, 0.4f), -0.05f, cerca(Offset(r.left + r.width * t, r.bottom))) }
        drawRect(a.sombra(0.3f), Offset(r.left, r.bottom - ft), Size(r.width, 1f * u))
        // De noche, el filo frío del mar del ojo de buey no llega aquí; la sombra de la tabla, más honda.
    }

    /**
     * La tabla [k] en [r]: el canto de teca con su luz arriba, la moldura de abajo, la barandilla de mar de latón (la barra con sus
     * soportes) que no deja caer lo guardado, y las cantoneras en los extremos.
     */
    fun DrawScope.tabla(k: Int, r: Rect, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        val canto = r.top + r.height * 0.62f
        franja(r, r.top, canto, P.TECA, 0f, a, cerca)
        vetas(Rect(r.left, r.top, r.right, canto), a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.35f), 8220 + k, densidad = 0.8f, grosor = 0.5f)
        franja(r, r.top, r.top + 1.5f * u, P.TECA_LUZ, 0.3f, a, cerca)
        franja(r, canto, r.bottom, P.TECA_OSC, -0.05f, a, cerca)
        drawRect(a.sombra(0.35f), Offset(r.left, canto), Size(r.width, 1.4f * u))
        franja(r, canto + 1.4f * u, canto + 2.6f * u, P.TECA, 0.1f, a, cerca)
        drawRect(a.sombra(0.4f), Offset(r.left, r.bottom - 1.6f * u), Size(r.width, 1.6f * u))
        // Los golpes del canto.
        for (j in 0 until 4) {
            val x = r.left + r.width * (0.05f + 0.9f * azar(8230 + k, j))
            drawLine(a.brillo(0.35f, cerca(Offset(x, r.top))), Offset(x, r.top + r.height * 0.25f), Offset(x + w * 0.012f, r.top + r.height * 0.45f), 0.7f * u)
        }
        // La barandilla de mar: la barra de latón a ras del canto, sobre sus soportes.
        val yb = r.top + r.height * 0.2f
        val g = 2.2f * u
        val nb = tramos(a, r.width, w)
        degradado(Rect(r.left, yb - g / 2, r.right, yb + g / 2), false, nb) { t -> a.dia(P.LATON, 0f, cerca(Offset(r.left + r.width * t, yb))) }
        degradado(Rect(r.left, yb - g / 2, r.right, yb - g / 2 + 0.8f * u), false, nb) { t -> a.dia(P.LATON_LUZ, 0.2f, cerca(Offset(r.left + r.width * t, yb))) }
        drawRect(a.sombra(0.35f), Offset(r.left, yb + g / 2), Size(r.width, 1.2f * u))
        var x = r.left + w * 0.07f
        while (x < r.right - w * 0.04f) {
            val kk = cerca(Offset(x, yb))
            drawRect(a.sombra(0.3f), Offset(x - 1f * u, yb), Size(3.4f * u, r.height * 0.3f))
            drawRect(a.dia(P.LATON_OSC, 0f, kk), Offset(x - 1.4f * u, yb - g * 0.6f), Size(2.8f * u, r.height * 0.28f))
            drawRect(a.brillo(0.45f, kk), Offset(x - 1.4f * u, yb - g * 0.6f), Size(0.8f * u, r.height * 0.28f))
            x += w * 0.19f
        }
        cantonera(Offset(r.left, r.top), r.height, false, a, cerca(Offset(r.left, r.top)))
        cantonera(Offset(r.right, r.top), r.height, true, a, cerca(Offset(r.right, r.top)))
    }

    /**
     * Un costado ([izq], el de la izquierda) en [r]: la cara de teca con su veta, la moldura de media caña junto al hueco, el
     * bisel de fuera, los pernos al casco a media altura de cada repisa y su desgaste.
     */
    fun DrawScope.costado(r: Rect, izq: Boolean, d: DisposicionEstante, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (r.width < 2f || r.height < 2f) return
        val nv = if (a.lumbre) (r.height / (w * 0.05f)).toInt().coerceIn(2, 48) else 1
        degradado(r, true, nv) { t -> a.dia(P.TECA, if (izq) 0.04f else -0.04f, cerca(Offset(r.center.x, r.top + r.height * t))) }
        vetas(r, a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.4f), if (izq) 8240 else 8241, densidad = 0.9f, vertical = true, grosor = 0.6f, ondulacion = 0.6f)
        // La moldura de media caña junto al hueco y el bisel de fuera.
        val mx = if (izq) r.right - r.width * 0.24f else r.left + r.width * 0.14f
        val mw = r.width * 0.1f
        degradado(Rect(mx, r.top, mx + mw * 0.5f, r.bottom), true, nv) { t -> a.dia(P.TECA_LUZ, 0.1f, cerca(Offset(mx, r.top + r.height * t))) }
        drawRect(a.sombra(0.35f), Offset(mx + mw * 0.5f, r.top), Size(mw * 0.5f, r.height))
        drawRect(a.sombra(0.45f), Offset(mx - 1f * u, r.top), Size(1f * u, r.height))
        if (izq) {
            drawRect(a.brillo(0.45f, 1f), Offset(r.left, r.top), Size(1.6f * u, r.height))
            drawRect(a.sombra(0.25f), Offset(r.right - 1.4f * u, r.top), Size(1.4f * u, r.height))
        } else {
            drawRect(a.brillo(0.3f, 1f), Offset(r.left, r.top), Size(1.2f * u, r.height))
            drawRect(a.sombra(0.4f), Offset(r.right - r.width * 0.12f, r.top), Size(r.width * 0.12f, r.height))
        }
        // El cordón tallado que baja por la cara del costado (el mismo del friso), en su caja rehundida.
        val cx0 = r.left + r.width * (if (izq) 0.3f else 0.38f)
        val caja = Rect(cx0, r.top + w * 0.02f, cx0 + r.width * 0.3f, r.bottom - w * 0.02f)
        degradado(caja, true, nv) { t -> a.dia(P.TECA_OSC, -0.05f, cerca(Offset(caja.center.x, caja.top + caja.height * t))) }
        drawRect(a.sombra(0.4f), caja.topLeft, Size(1.2f * u, caja.height))
        drawRect(a.brillo(0.3f, 1f), Offset(caja.right - 1f * u, caja.top), Size(1f * u, caja.height))
        cordonVertical(Rect(caja.left + caja.width * 0.12f, caja.top + 2f * u, caja.right - caja.width * 0.12f, caja.bottom - 2f * u), a, cerca, nv)
        // El filo de las llamas en el canto que las mira (de noche).
        if (a.lumbre) {
            val xf = if (izq) r.right - 1.5f * u else r.left
            degradado(Rect(xf, r.top, xf + 1.5f * u, r.bottom), true, nv) { t -> a.filo(0.6f * cerca(Offset(xf, r.top + r.height * t))) }
        }
        // Los pernos al casco, a media altura de cada repisa, y el desgaste.
        val px = if (izq) r.left + r.width * 0.42f else r.left + r.width * 0.55f
        d.repisas.forEach { rp -> clavoAbismo(Offset(px, rp.center.y), 2f * u, a, cerca(Offset(px, rp.center.y)), mezcla(P.LATON, P.TECA_OSC, 0.3f)) }
        for (j in 0 until (r.height / (w * 0.25f)).toInt().coerceIn(1, 20)) {
            val y = r.top + r.height * azar(if (izq) 8242 else 8243, j)
            drawLine(a.brillo(0.3f, cerca(Offset(r.center.x, y))), Offset(r.left + r.width * 0.2f, y), Offset(r.left + r.width * 0.45f, y + w * 0.01f), 0.6f * u)
        }
    }

    /**
     * La placa de latón estampada del mes (como las de los temas del Ideario), atornillada al canto de la tabla: su sombra, el
     * borde rebajado, los dos tornillos y el texto en hueco; de noche, ámbar junto a la llama y hundida lejos, siempre legible.
     */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, a: AireAbismo, k: Float) {
        val u = density
        val r = Rect(p.r.left, p.r.top + p.r.height * 0.06f, p.r.right, p.r.bottom - p.r.height * 0.02f)
        drawRoundRect(a.sombra(0.4f), Offset(r.left + 1.5f * u, r.top + 2f * u), r.size, CornerRadius(2f * u))
        val fondo = if (a.oscuro) a.etiqueta(P.LATON_PLACA, k) else a.dia(P.LATON_PLACA, 0f, k)
        drawRoundRect(fondo, r.topLeft, r.size, CornerRadius(2f * u))
        drawRoundRect(a.dia(mezcla(P.LATON, P.TECA_OSC, 0.3f), 0f, maxOf(k, 0.5f)).copy(alpha = 0.6f), Offset(r.left + 2.5f * u, r.top + 2.5f * u), Size(r.width - 5f * u, r.height - 5f * u), CornerRadius(1.5f * u), style = Stroke(0.7f * u))
        drawRect(a.brillo(0.5f, k), r.topLeft, Size(r.width, 1f * u))
        drawOval(a.dia(P.VERDIN, 0f, k).copy(alpha = 0.35f), Offset(r.right - r.width * 0.12f, r.bottom - r.height * 0.4f), Size(r.width * 0.08f, r.height * 0.3f))
        listOf(Offset(r.left + 4f * u, r.center.y), Offset(r.right - 4f * u, r.center.y)).forEach { clavoAbismo(it, 1.3f * u, a, k) }
        textoEn(medidor, p.texto, Rect(r.left + 9f * u, r.top, r.right - 9f * u, r.bottom), P.LETRA, Color(0xFF1A1C1E), 10.5f, peso = FontWeight.SemiBold, espaciado = 0.6f, minimo = 6f)
    }

    /**
     * El zócalo de [r]: el rodapié de teca con su moldura de arriba, la chapa de latón de los puntapiés con sus tornillos, abollada
     * y con verdín, y su sombra en la cubierta.
     */
    fun DrawScope.zocalo(r: Rect, a: AireAbismo, cerca: (Offset) -> Float) {
        val w = size.width; val u = density
        if (r.height < 2f) return
        val m0 = r.top + r.height * 0.18f
        franja(r, r.top, m0, P.TECA_LUZ, 0f, a, cerca)
        drawRect(a.sombra(0.35f), Offset(r.left, m0 - 1.2f * u), Size(r.width, 1.2f * u))
        franja(r, m0, r.bottom, P.TECA, -0.06f, a, cerca)
        vetas(Rect(r.left, m0, r.right, r.bottom), a.dia(P.TECA_OSC, 0f, 0.5f).copy(alpha = 0.35f), 8250, densidad = 0.7f, grosor = 0.5f)
        // La chapa de latón.
        val c0 = r.bottom - r.height * 0.42f
        franja(r, c0, r.bottom - 2f * u, P.LATON, -0.08f, a, cerca)
        franja(r, c0, c0 + 1.2f * u, P.LATON_LUZ, 0.2f, a, cerca)
        var x = r.left + w * 0.04f
        while (x < r.right) {
            clavoAbismo(Offset(x, c0 + r.height * 0.1f), 1.1f * u, a, cerca(Offset(x, c0)), P.LATON_OSC)
            x += w * 0.09f
        }
        for (j in 0 until 6) {
            val p = Offset(r.left + r.width * azar(8251, j), c0 + (r.bottom - c0) * (0.4f + 0.4f * azar(8252, j)))
            drawOval(a.sombra(0.2f), Offset(p.x, p.y), Size(w * 0.02f, w * 0.006f))
            drawArc(a.brillo(0.3f, cerca(p)), 200f, 120f, false, Offset(p.x, p.y - w * 0.002f), Size(w * 0.02f, w * 0.006f), style = Stroke(0.6f * u))
        }
        drawOval(a.dia(P.VERDIN, 0f, cerca(Offset(w * 0.8f, r.bottom))).copy(alpha = 0.4f), Offset(w * 0.78f, r.bottom - r.height * 0.18f), Size(w * 0.05f, r.height * 0.12f))
        drawRect(a.sombra(0.45f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        cantonera(Offset(r.left, r.top), r.height * 0.5f, false, a, cerca(Offset(r.left, r.top)))
        cantonera(Offset(r.right, r.top), r.height * 0.5f, true, a, cerca(Offset(r.right, r.top)))
    }
}
