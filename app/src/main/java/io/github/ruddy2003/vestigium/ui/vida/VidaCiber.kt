package io.github.ruddy2003.vestigium.ui.vida

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Terminal: pura consola, siempre en oscuro. Fósforo cian en tres intensidades (el acento, el secundario y la tinta del
 * esquema) sobre negro, con la rejilla de celdas de caracteres, barras de título y de estado, y dibujos de trazo fino y
 * esquinas rectas, como su Ideario (la ventana con su árbol de directorios) y su sala de cómputo. Sin texto: las «letras»
 * son rayitas que se leen como texto de lejos.
 * - Social: una ventana de consola con su barra de título y su barra de estado; en los bordes, un diagrama de nodos hecho
 *   con cajas de caracteres (`+--+`, `|`) que manda flechas `-->` hacia el centro, donde va el grafo sobre la rejilla.
 * - Mapas: la pantalla del radar: los anillos y los ejes que asoman alrededor del mapa, el barrido, las reglas graduadas
 *   de los cuatro bordes y los ecos en los márgenes.
 * - Otras vidas: un chasis de servidores con cinco cuchillas: el riel con sus agujeros, la rejilla de ventilación, y en
 *   cada cuchilla sus pilotos, la bahía donde va la medalla y la manija.
 * - Íconos, en trazo de línea: la torre de radio con sus ondas, la pantalla cuyas celdas se borran y dejan ver la costa
 *   (con el cursor que borra) y retículas de mira unidas por una ruta punteada.
 * - Notas: el escritorio de la terminal con su panel de escritorios virtuales y su línea de estado; cada nota es un buffer
 *   en su ventana, con la ventana de atrás asomando, la barra de título con el glifo del tipo, los números de línea y el
 *   cursor.
 */
internal object VidaCiber : DisenoVida {
    /** El fondo de una ventana de la consola. */
    private val PANEL = Color(0xFF071216)
    /** El texto de la consola (la tinta del esquema). */
    private val TEXTO = Color(0xFFBDEFF5)
    /** El único color que no es cian: el de los avisos (el error del esquema). */
    private val ALERTA = Color(0xFFFF8F8F)

    /** Un número estable de 0 a 1 (las irregularidades de cada cosa, siempre las mismas). */
    private fun azar(semilla: Int, k: Int): Float {
        var x = semilla * 374761393 + k * 668265263
        x = (x xor (x ushr 13)) * 1274126177
        x = x xor (x ushr 16)
        return (x and 0x7fffffff) / 2147483647f
    }

    private fun entre(a: Offset, b: Offset, f: Float): Offset = Offset(a.x + (b.x - a.x) * f, a.y + (b.y - a.y) * f)

    /** La rejilla de celdas de caracteres: un punto por celda, todos en una sola llamada. */
    private fun DrawScope.rejilla(r: Rect, color: Color, u: Float) {
        val cw = 4.2f * u; val ch = 7f * u
        if (cw < 1f || ch < 1f) return
        val puntos = ArrayList<Offset>()
        var y = r.top + ch / 2
        while (y < r.bottom) {
            var x = r.left + cw / 2
            while (x < r.right) { puntos.add(Offset(x, y)); x += cw }
            y += ch
        }
        drawPoints(puntos, PointMode.Points, color, strokeWidth = 0.7f * u, cap = StrokeCap.Round)
    }

    /** Rayitas que se leen como texto de lejos: [n] palabras de largo al azar desde [x0]. */
    private fun DrawScope.rayas(x0: Float, y: Float, n: Int, color: Color, semilla: Int, grosor: Float, u: Float) {
        var x = x0
        for (k in 0 until n) {
            val l = (3f + 8f * azar(semilla, k)) * u
            drawLine(color, Offset(x, y), Offset(x + l, y), grosor)
            x += l + 2.4f * u
        }
    }

    /** Un «+» de caracteres. */
    private fun DrawScope.mas(p: Offset, l: Float, c: Color, g: Float) {
        drawLine(c, Offset(p.x - l, p.y), Offset(p.x + l, p.y), g)
        drawLine(c, Offset(p.x, p.y - l), Offset(p.x, p.y + l), g)
    }

    // ---------------------------------------------------------------- Social: el diagrama de nodos

    override fun DrawScope.muebleSocial(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        drawRect(Color.Black.copy(alpha = 0.3f), Offset.Zero, size)
        val barra = h * 0.1f; val estado = h * 0.9f
        rejilla(Rect(0f, barra, w, estado), e.acento.copy(alpha = 0.09f), u)
        // La barra de título: el nombre en rayitas y los tres botones cuadrados.
        drawRect(e.acento.copy(alpha = 0.1f), Offset.Zero, Size(w, barra))
        drawLine(e.secundario.copy(alpha = 0.5f), Offset(0f, barra), Offset(w, barra), 0.5f * u)
        val s = barra * 0.36f
        for (i in 0 until 3) drawRect(e.secundario.copy(alpha = 0.6f), Offset(w * 0.88f - i * s * 1.8f, (barra - s) / 2), Size(s, s), style = Stroke(0.5f * u))
        rayas(w * 0.4f, barra / 2, 3, e.tinta.copy(alpha = 0.3f), e.semilla, 0.7f * u, u)
        // La barra de estado: el bloque del modo y la posición.
        drawRect(e.acento.copy(alpha = 0.07f), Offset(0f, estado), Size(w, h - estado))
        drawLine(e.secundario.copy(alpha = 0.4f), Offset(0f, estado), Offset(w, estado), 0.5f * u)
        drawRect(e.acento.copy(alpha = 0.55f), Offset(w * 0.1f, estado + (h - estado) * 0.25f), Size(w * 0.1f, (h - estado) * 0.5f))
        rayas(w * 0.66f, (estado + h) / 2, 3, e.secundario.copy(alpha = 0.4f), e.semilla + 3, 0.7f * u, u)
        // Los números de línea en el margen izquierdo.
        var y = barra + 3.5f * u
        while (y < estado - 2f * u) {
            drawLine(e.secundario.copy(alpha = 0.28f), Offset(w * 0.012f, y), Offset(w * 0.024f, y), 0.6f * u)
            y += 7f * u
        }
        // El diagrama: una columna de tres nodos en cada orilla, unidos por «|», y una flecha «-->» hacia el centro.
        val alto = h * 0.11f; val ancho = w * 0.055f
        val raya = PathEffect.dashPathEffect(floatArrayOf(1.6f * u, 1.2f * u))
        listOf(w * 0.07f to 1f, w * 0.93f to -1f).forEach { (x, lado) ->
            val ys = listOf(h * 0.27f, h * 0.5f, h * 0.73f)
            drawLine(e.secundario.copy(alpha = 0.45f), Offset(x, ys[0] + alto / 2), Offset(x, ys[2] - alto / 2), 0.6f * u, pathEffect = raya)
            ys.forEachIndexed { i, yy -> nodo(Rect(x - ancho / 2, yy - alto / 2, x + ancho / 2, yy + alto / 2), e, i == 1) }
            val yc = ys[1]
            val x0 = x + lado * (ancho / 2 + u); val x1 = x + lado * (ancho / 2 + w * 0.045f)
            val flecha = e.acento.copy(alpha = 0.6f)
            drawLine(flecha, Offset(x0, yc), Offset(x1, yc), 0.6f * u, pathEffect = raya)
            drawLine(flecha, Offset(x1, yc), Offset(x1 - lado * 1.6f * u, yc - 1.4f * u), 0.6f * u)
            drawLine(flecha, Offset(x1, yc), Offset(x1 - lado * 1.6f * u, yc + 1.4f * u), 0.6f * u)
            // Y las ramas sueltas de arriba y de abajo, que se pierden hacia el centro.
            listOf(ys[0], ys[2]).forEach { yy ->
                drawLine(e.secundario.copy(alpha = 0.3f), Offset(x0, yy), Offset(x + lado * (ancho / 2 + w * 0.025f), yy), 0.5f * u, pathEffect = raya)
            }
        }
        // El borde de la ventana.
        drawRect(e.secundario.copy(alpha = 0.45f), Offset(0.3f * u, 0.3f * u), Size(w - 0.6f * u, h - 0.6f * u), style = Stroke(0.6f * u))
    }

    /** Un nodo de caracteres: la caja `+--+` con sus lados `|` y una «o» dentro; el activo, más encendido. */
    private fun DrawScope.nodo(r: Rect, e: EstadoMueble, activo: Boolean) {
        val u = size.minDimension / 100f
        val c = if (activo) e.acento else e.secundario.copy(alpha = 0.7f)
        val raya = PathEffect.dashPathEffect(floatArrayOf(1.4f * u, 1f * u))
        if (activo) drawCircle(e.acento.copy(alpha = 0.1f), minOf(r.width, r.height) * 0.8f, r.center)
        drawRect(Color.Black.copy(alpha = 0.4f), r.topLeft, r.size)
        drawLine(c, Offset(r.left + 1.5f * u, r.top), Offset(r.right - 1.5f * u, r.top), 0.6f * u, pathEffect = raya)
        drawLine(c, Offset(r.left + 1.5f * u, r.bottom), Offset(r.right - 1.5f * u, r.bottom), 0.6f * u, pathEffect = raya)
        drawLine(c, Offset(r.left, r.top + 1.5f * u), Offset(r.left, r.bottom - 1.5f * u), 0.6f * u)
        drawLine(c, Offset(r.right, r.top + 1.5f * u), Offset(r.right, r.bottom - 1.5f * u), 0.6f * u)
        listOf(r.topLeft, Offset(r.right, r.top), Offset(r.left, r.bottom), Offset(r.right, r.bottom)).forEach { p -> mas(p, 1f * u, c, 0.6f * u) }
        drawCircle(c, minOf(r.width, r.height) * 0.18f, r.center, style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- Mapas: la pantalla del radar

    override fun DrawScope.muebleMapas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val mapa = Rect(w * 0.06f, h * 0.1f, w * 0.94f, h * 0.9f)
        drawRect(Color.Black.copy(alpha = 0.3f), Offset.Zero, size)
        rejilla(Rect(0f, 0f, w, h), e.acento.copy(alpha = 0.07f), u)
        val c = Offset(w / 2, h / 2)
        val diag = sqrt(w * w + h * h) / 2
        // Los anillos del radar, que asoman por los bordes.
        var rr = h * 0.22f; var k = 0
        while (rr < diag && k < 14) {
            drawCircle(e.acento.copy(alpha = if (k % 2 == 1) 0.16f else 0.08f), rr, c, style = Stroke(0.5f * u))
            rr += h * 0.22f; k++
        }
        // Los ejes, a trazos.
        val raya = PathEffect.dashPathEffect(floatArrayOf(2f * u, 1.6f * u))
        drawLine(e.secundario.copy(alpha = 0.18f), Offset(0f, c.y), Offset(w, c.y), 0.5f * u, pathEffect = raya)
        drawLine(e.secundario.copy(alpha = 0.18f), Offset(c.x, 0f), Offset(c.x, h), 0.5f * u, pathEffect = raya)
        // El barrido: una cuña tenue que deja atrás la línea que gira.
        drawArc(e.acento.copy(alpha = 0.06f), -50f, 26f, true, Offset(c.x - diag, c.y - diag), Size(diag * 2, diag * 2))
        drawArc(e.acento.copy(alpha = 0.035f), -76f, 26f, true, Offset(c.x - diag, c.y - diag), Size(diag * 2, diag * 2))
        val ang = Math.toRadians(-24.0)
        drawLine(e.acento.copy(alpha = 0.45f), c, Offset(c.x + diag * cos(ang).toFloat(), c.y + diag * sin(ang).toFloat()), 0.7f * u)
        // Las reglas graduadas en los cuatro bordes.
        val marca = e.secundario.copy(alpha = 0.5f)
        val nx = 40
        for (i in 0..nx) {
            val x = w * i / nx; val largo = if (i % 5 == 0) h * 0.05f else h * 0.025f
            drawLine(marca, Offset(x, h * 0.015f), Offset(x, h * 0.015f + largo), 0.5f * u)
            drawLine(marca, Offset(x, h * 0.985f), Offset(x, h * 0.985f - largo), 0.5f * u)
        }
        val ny = 16
        for (j in 0..ny) {
            val y = h * j / ny; val largo = if (j % 4 == 0) w * 0.03f else w * 0.015f
            drawLine(marca, Offset(w * 0.008f, y), Offset(w * 0.008f + largo, y), 0.5f * u)
            drawLine(marca, Offset(w * 0.992f, y), Offset(w * 0.992f - largo, y), 0.5f * u)
        }
        // Los ecos en los márgenes, con su halo.
        listOf(
            Offset(w * 0.03f, h * (0.25f + 0.5f * azar(e.semilla, 1))),
            Offset(w * 0.97f, h * (0.2f + 0.6f * azar(e.semilla, 2))),
            Offset(w * (0.15f + 0.3f * azar(e.semilla, 3)), h * 0.085f),
            Offset(w * (0.55f + 0.3f * azar(e.semilla, 4)), h * 0.915f),
            Offset(w * 0.035f, h * (0.12f + 0.1f * azar(e.semilla, 5))),
        ).forEach { p ->
            drawCircle(e.acento.copy(alpha = 0.15f), 3f * u, p)
            drawRect(e.acento, Offset(p.x - 0.9f * u, p.y - 0.9f * u), Size(1.8f * u, 1.8f * u))
        }
        // El cuadro de la pantalla donde va el mapa.
        drawRect(e.secundario.copy(alpha = 0.35f), mapa.topLeft, mapa.size, style = Stroke(0.5f * u))
    }

    // ---------------------------------------------------------------- Otras vidas: el chasis de cuchillas

    override fun DrawScope.muebleOtrasVidas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        val chasis = Rect(w * 0.012f, h * 0.03f, w * 0.988f, h * 0.97f)
        drawRect(Color.Black.copy(alpha = 0.35f), chasis.topLeft, chasis.size)
        drawRect(e.secundario.copy(alpha = 0.45f), chasis.topLeft, chasis.size, style = Stroke(0.6f * u))
        // El riel de arriba, con sus agujeros cuadrados.
        val riel = Rect(chasis.left, chasis.top, chasis.right, h * 0.1f)
        drawLine(e.secundario.copy(alpha = 0.35f), Offset(riel.left, riel.bottom), Offset(riel.right, riel.bottom), 0.5f * u)
        var x = riel.left + 2.5f * u
        while (x < riel.right - 2f * u) {
            drawRect(e.secundario.copy(alpha = 0.3f), Offset(x, riel.center.y - 0.9f * u), Size(1.8f * u, 1.8f * u), style = Stroke(0.4f * u))
            x += w * 0.03f
        }
        // La rejilla de ventilación de abajo.
        val rej = Rect(chasis.left, h * 0.89f, chasis.right, chasis.bottom)
        drawLine(e.secundario.copy(alpha = 0.35f), Offset(rej.left, rej.top), Offset(rej.right, rej.top), 0.5f * u)
        x = rej.left + 1.5f * u
        while (x < rej.right - 1f * u) {
            drawLine(e.secundario.copy(alpha = 0.22f), Offset(x, rej.top + 1.2f * u), Offset(x, rej.bottom - 1.2f * u), 0.5f * u)
            x += w * 0.012f
        }
        // Las cinco cuchillas, una por medalla.
        val paso = w * 0.184f
        val bw = minOf(paso * 0.88f, h * 0.55f)
        for (i in 0 until 5) {
            val cx = w / 2 + (i - 2) * paso
            cuchilla(Rect(cx - bw / 2, h * 0.13f, cx + bw / 2, h * 0.86f), e, i)
        }
    }

    /** Una cuchilla del chasis: sus ranuras, los pilotos arriba, la bahía oscura donde va la medalla y la manija abajo. */
    private fun DrawScope.cuchilla(r: Rect, e: EstadoMueble, i: Int) {
        val u = size.minDimension / 100f
        drawRect(e.acento.copy(alpha = 0.04f), r.topLeft, r.size)
        drawRect(e.secundario.copy(alpha = 0.5f), r.topLeft, r.size, style = Stroke(0.6f * u))
        listOf(r.left + 1.6f * u, r.right - 1.6f * u).forEach { x -> drawLine(e.secundario.copy(alpha = 0.18f), Offset(x, r.top + 2f * u), Offset(x, r.bottom - 2f * u), 0.4f * u) }
        // Los pilotos: el primero siempre encendido; el segundo, en cuchillas alternas.
        val y = r.top + 2.4f * u
        for (k in 0 until 3) {
            val p = Offset(r.left + 3.2f * u + k * 2.6f * u, y)
            val encendido = k == 0 || (k == 1 && (i + e.semilla) % 2 == 0)
            if (encendido) drawCircle(e.acento.copy(alpha = 0.2f), 2f * u, p)
            drawRect(if (encendido) e.acento else e.secundario.copy(alpha = 0.25f), Offset(p.x - 0.7f * u, p.y - 0.7f * u), Size(1.4f * u, 1.4f * u))
        }
        // La bahía donde va la medalla, con sus esquinas de mira.
        val bahia = Rect(r.left + 2.6f * u, r.top + 5f * u, r.right - 2.6f * u, r.bottom - 6f * u)
        drawRect(Color.Black.copy(alpha = 0.4f), bahia.topLeft, bahia.size)
        drawRect(e.acento.copy(alpha = 0.16f), bahia.topLeft, bahia.size, style = Stroke(0.4f * u))
        val l = 2.4f * u
        listOf(bahia.topLeft to Offset(1f, 1f), Offset(bahia.right, bahia.top) to Offset(-1f, 1f), Offset(bahia.left, bahia.bottom) to Offset(1f, -1f), Offset(bahia.right, bahia.bottom) to Offset(-1f, -1f)).forEach { (p, d) ->
            drawLine(e.acento.copy(alpha = 0.55f), p, Offset(p.x + d.x * l, p.y), 0.6f * u)
            drawLine(e.acento.copy(alpha = 0.55f), p, Offset(p.x, p.y + d.y * l), 0.6f * u)
        }
        // La manija de expulsión.
        val my = r.bottom - 3f * u
        val mx0 = r.center.x - r.width * 0.25f; val mx1 = r.center.x + r.width * 0.25f
        drawLine(e.secundario.copy(alpha = 0.6f), Offset(mx0, my), Offset(mx1, my), 1f * u, StrokeCap.Round)
        drawLine(e.secundario.copy(alpha = 0.4f), Offset(mx0, my), Offset(mx0, my - 1.4f * u), 0.6f * u)
        drawLine(e.secundario.copy(alpha = 0.4f), Offset(mx1, my), Offset(mx1, my - 1.4f * u), 0.6f * u)
    }

    // ---------------------------------------------------------------- Íconos de los mapas

    override fun DrawScope.iconoMapa(id: String, e: EstadoIcono, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val c = Offset(size.width / 2, size.height / 2)
        val a = size.height * 0.4f
        val g = 1.6f * density
        when (id) {
            "unesco" -> torre(c, a, g, e)
            "rascar" -> pantallaRascar(c, a, g, e)
            else -> reticulas(c, a, g, e)
        }
    }

    /** La torre de radio: las dos patas con su enrejado, el mástil con el piloto en la punta y las ondas a los lados. */
    private fun DrawScope.torre(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        val base = c.y + a * 0.92f; val cima = c.y - a * 0.58f
        val izqB = Offset(c.x - a * 0.62f, base); val derB = Offset(c.x + a * 0.62f, base)
        val izqA = Offset(c.x - a * 0.12f, cima); val derA = Offset(c.x + a * 0.12f, cima)
        drawLine(e.c1, izqB, izqA, g); drawLine(e.c1, derB, derA, g)
        val tramos = 4
        for (k in 0..tramos) {
            val f = k / tramos.toFloat()
            val l = entre(izqB, izqA, f); val r = entre(derB, derA, f)
            drawLine(e.c1, l, r, g * 0.7f)
            if (k < tramos) {
                val f2 = (k + 1) / tramos.toFloat()
                drawLine(e.c2, l, entre(derB, derA, f2), g * 0.55f)
                drawLine(e.c2, r, entre(izqB, izqA, f2), g * 0.55f)
            }
        }
        val punta = Offset(c.x, c.y - a * 0.95f)
        drawLine(e.c1, Offset(c.x, cima), punta, g)
        drawRect(e.c1, Offset(punta.x - g * 1.2f, punta.y - g * 1.2f), Size(g * 2.4f, g * 2.4f))
        listOf(0.38f, 0.66f).forEach { r ->
            val tl = Offset(punta.x - a * r, punta.y - a * r); val sz = Size(a * r * 2, a * r * 2)
            drawArc(e.c2, 150f, 60f, false, tl, sz, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
            drawArc(e.c2, -30f, 60f, false, tl, sz, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        }
        drawLine(e.c2, Offset(c.x - a * 1.3f, base), Offset(c.x + a * 1.3f, base), g * 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g * 2.4f, g * 1.4f)))
    }

    /** El mapa para rascar: una ventana llena de celdas que se borran en diagonal y dejan ver la costa, con el cursor que borra. */
    private fun DrawScope.pantallaRascar(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        val v = Rect(c.x - a * 1.35f, c.y - a * 0.9f, c.x + a * 1.35f, c.y + a * 0.9f)
        val barra = a * 0.28f
        drawRect(e.c1, v.topLeft, v.size, style = Stroke(g))
        drawLine(e.c1, Offset(v.left, v.top + barra), Offset(v.right, v.top + barra), g * 0.7f)
        drawRect(e.c1, Offset(v.left + g * 2f, v.top + barra * 0.28f), Size(barra * 0.45f, barra * 0.45f))
        val cuerpo = Rect(v.left + g * 2.2f, v.top + barra + g * 2f, v.right - g * 2.2f, v.bottom - g * 2f)
        val cols = 10; val filas = 4
        val cw = cuerpo.width / cols; val ch = cuerpo.height / filas
        for (f in 0 until filas) for (k in 0 until cols) {
            val borrada = (k - f * 1.3f) in 2.6f..5.8f
            if (!borrada) drawRect(e.c2.copy(alpha = 0.5f), Offset(cuerpo.left + k * cw + cw * 0.1f, cuerpo.top + f * ch + ch * 0.1f), Size(cw * 0.8f, ch * 0.8f))
        }
        // La costa que quedó al descubierto en la franja borrada.
        val costa = Path().apply {
            moveTo(cuerpo.left + cw * 3f, cuerpo.top + ch * 0.2f)
            cubicTo(cuerpo.left + cw * 5.5f, cuerpo.top + ch * 0.9f, cuerpo.left + cw * 4.5f, cuerpo.top + ch * 2.4f, cuerpo.left + cw * 7f, cuerpo.top + ch * 2.6f)
            quadraticTo(cuerpo.left + cw * 8.6f, cuerpo.top + ch * 2.8f, cuerpo.left + cw * 8.4f, cuerpo.bottom - ch * 0.1f)
        }
        drawPath(costa, e.c1, style = Stroke(g * 0.8f, cap = StrokeCap.Round))
        // El cursor de bloque que borra, al frente de la franja.
        drawRect(e.c1, Offset(cuerpo.left + 6 * cw + cw * 0.1f, cuerpo.top + ch * 0.1f), Size(cw * 0.8f, ch * 0.8f))
    }

    /** Tus lugares: tres retículas de mira unidas por una ruta punteada. */
    private fun DrawScope.reticulas(c: Offset, a: Float, g: Float, e: EstadoIcono) {
        val p1 = Offset(c.x - a * 1.15f, c.y + a * 0.4f)
        val p2 = Offset(c.x - a * 0.05f, c.y - a * 0.42f)
        val p3 = Offset(c.x + a * 1.15f, c.y + a * 0.3f)
        val ruta = Path().apply { moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y) }
        drawPath(ruta, e.c2.copy(alpha = 0.7f), style = Stroke(g * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(g, g * 2f))))
        listOf(p1 to 0.85f, p2 to 1.1f, p3 to 0.8f).forEach { (p, k) -> reticula(p, a * 0.3f * k, g, e.c1, e.fondo) }
    }

    /** Una retícula: el círculo, cuatro marcas que salen de él y el bloque del centro. */
    private fun DrawScope.reticula(p: Offset, r: Float, g: Float, c: Color, fondo: Color) {
        drawCircle(fondo, r, p)
        drawCircle(c, r, p, style = Stroke(g * 0.8f))
        listOf(Offset(1f, 0f), Offset(-1f, 0f), Offset(0f, 1f), Offset(0f, -1f)).forEach { d ->
            drawLine(c, Offset(p.x + d.x * r * 0.55f, p.y + d.y * r * 0.55f), Offset(p.x + d.x * r * 1.45f, p.y + d.y * r * 1.45f), g * 0.8f)
        }
        drawRect(c, Offset(p.x - g, p.y - g), Size(g * 2f, g * 2f))
    }

    // ---------------------------------------------------------------- Notas: el escritorio de ventanas

    override fun DrawScope.tableroNotas(e: EstadoMueble, oscuro: Boolean) {
        if (size.minDimension < 1f) return
        val w = size.width; val h = size.height; val u = size.minDimension / 100f
        drawRoundRect(Color.Black.copy(alpha = 0.35f), Offset.Zero, size, CornerRadius(1.5f * u))
        val panel = h * 0.055f; val est = h * 0.955f
        rejilla(Rect(0f, panel, w, est), e.acento.copy(alpha = 0.07f), u)
        // Dos ventanas apagadas al fondo del escritorio.
        listOf(Rect(w * 0.02f, h * 0.09f, w * 0.58f, h * 0.62f), Rect(w * 0.4f, h * 0.4f, w * 0.985f, h * 0.93f)).forEach { v ->
            drawRect(e.secundario.copy(alpha = 0.14f), v.topLeft, v.size, style = Stroke(0.5f * u))
            drawRect(e.secundario.copy(alpha = 0.06f), v.topLeft, Size(v.width, h * 0.045f))
        }
        // El panel de arriba: los escritorios virtuales (el primero, activo) y la hora en rayitas.
        drawRect(e.acento.copy(alpha = 0.08f), Offset.Zero, Size(w, panel))
        drawLine(e.secundario.copy(alpha = 0.4f), Offset(0f, panel), Offset(w, panel), 0.5f * u)
        val s = panel * 0.5f
        for (i in 0 until 5) {
            val p = Offset(w * 0.02f + i * s * 1.6f, (panel - s) / 2)
            if (i == 0) drawRect(e.acento.copy(alpha = 0.8f), p, Size(s, s))
            else drawRect(e.secundario.copy(alpha = 0.4f), p, Size(s, s), style = Stroke(0.4f * u))
        }
        rayas(w * 0.82f, panel / 2, 2, e.tinta.copy(alpha = 0.45f), 4, 0.6f * u, u)
        // La línea de estado de abajo, con el bloque del modo.
        drawRect(e.acento.copy(alpha = 0.06f), Offset(0f, est), Size(w, h - est))
        drawRect(e.acento.copy(alpha = 0.6f), Offset(0f, est), Size(w * 0.08f, h - est))
        rayas(w * 0.11f, (est + h) / 2, 3, e.secundario.copy(alpha = 0.45f), 8, 0.6f * u, u)
        drawRoundRect(e.secundario.copy(alpha = 0.5f), Offset(0.3f * u, 0.3f * u), Size(w - 0.6f * u, h - 0.6f * u), CornerRadius(1.5f * u), style = Stroke(0.6f * u))
    }

    override fun DrawScope.notaTablero(e: EstadoNota, oscuro: Boolean) {
        val r = e.rect
        if (r.width < 1f || r.height < 1f) return
        val k = minOf(r.width, r.height) / 100f
        val d = 4f * k
        // La ventana de atrás, que asoma.
        val atras = Rect(r.left + d, r.top + d, r.right, r.bottom)
        drawRect(PANEL.copy(alpha = 0.7f), atras.topLeft, atras.size)
        drawRect(e.acento.copy(alpha = 0.22f), atras.topLeft, atras.size, style = Stroke(0.8f * k))
        // La ventana de la nota y su barra de título, más encendida según el tipo y si está fijada.
        val v = Rect(r.left, r.top, r.right - d, r.bottom - d)
        drawRect(PANEL, v.topLeft, v.size)
        val barra = r.height * 0.13f
        val tono = when (e.tipo) {
            "idea" -> 0.3f
            "proposito" -> 0.22f
            "recordatorio" -> 0.18f
            "pensamiento" -> 0.14f
            else -> 0.1f
        } + (if (e.fijada) 0.15f else 0f)
        drawRect(e.acento.copy(alpha = tono), v.topLeft, Size(v.width, barra))
        drawLine(e.acento.copy(alpha = 0.5f), Offset(v.left, v.top + barra), Offset(v.right, v.top + barra), 0.6f * k)
        val s = barra * 0.45f
        for (i in 0 until 2) drawRect(e.acento.copy(alpha = 0.6f), Offset(v.right - (i + 1) * s * 1.7f - k, v.top + (barra - s) / 2), Size(s, s), style = Stroke(0.6f * k))
        // El glifo del tipo, a la izquierda de la barra: «*» idea, «~» pensamiento, «[x]» propósito, «!» recordatorio, «>» lo demás.
        val gc = Offset(v.left + barra * 0.7f, v.top + barra / 2); val gr = barra * 0.32f
        val gg = 1f * k
        when (e.tipo) {
            "idea" -> for (j in 0 until 3) {
                val an = Math.toRadians(j * 60.0 + 90.0)
                val dx = (cos(an) * gr).toFloat(); val dy = (sin(an) * gr).toFloat()
                drawLine(e.acento, Offset(gc.x - dx, gc.y - dy), Offset(gc.x + dx, gc.y + dy), gg)
            }
            "pensamiento" -> drawPath(Path().apply {
                moveTo(gc.x - gr, gc.y)
                cubicTo(gc.x - gr * 0.4f, gc.y - gr * 0.9f, gc.x + gr * 0.4f, gc.y + gr * 0.9f, gc.x + gr, gc.y)
            }, e.acento, style = Stroke(gg, cap = StrokeCap.Round))
            "proposito" -> {
                drawRect(e.acento, Offset(gc.x - gr, gc.y - gr), Size(gr * 2, gr * 2), style = Stroke(gg))
                drawPath(Path().apply { moveTo(gc.x - gr * 0.55f, gc.y); lineTo(gc.x - gr * 0.1f, gc.y + gr * 0.5f); lineTo(gc.x + gr * 0.6f, gc.y - gr * 0.5f) }, e.acento, style = Stroke(gg, cap = StrokeCap.Round))
            }
            "recordatorio" -> {
                drawLine(ALERTA, Offset(gc.x, gc.y - gr), Offset(gc.x, gc.y + gr * 0.3f), gg * 1.2f)
                drawCircle(ALERTA, gg * 0.75f, Offset(gc.x, gc.y + gr * 0.85f))
            }
            else -> drawPath(Path().apply { moveTo(gc.x - gr * 0.5f, gc.y - gr); lineTo(gc.x + gr * 0.5f, gc.y); lineTo(gc.x - gr * 0.5f, gc.y + gr) }, e.acento, style = Stroke(gg))
        }
        // Los números de línea, en el margen izquierdo.
        var y = v.top + barra + 6f * k
        while (y < v.bottom - 6f * k) {
            drawLine(e.acento.copy(alpha = 0.22f), Offset(v.left + 2.4f * k, y), Offset(v.left + 5.5f * k, y), 0.8f * k)
            y += 9f * k
        }
        // El cursor de bloque, abajo a la derecha, y el borde de la ventana.
        drawRect(e.acento.copy(alpha = 0.85f), Offset(v.right - 14f * k, v.bottom - 11f * k), Size(4f * k, 7f * k))
        drawRect(if (e.fijada) e.acento else e.acento.copy(alpha = 0.45f), v.topLeft, v.size, style = Stroke(if (e.fijada) 1.6f * density else 0.8f * k))
    }

    override fun tintaNota(oscuro: Boolean): Color = TEXTO
}
