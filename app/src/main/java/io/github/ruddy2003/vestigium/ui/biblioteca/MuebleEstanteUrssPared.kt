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
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil

/**
 * La pared del club (la del Ideario): la cal amarillenta arriba, con sus brochazos, lo amarillo de la cal vieja y unas grietas;
 * la raya de remate a pulso; el óleo azul apagado abajo, con los brochazos de arriba abajo, los chorreones bajo la raya y los
 * desconchones. Y el fondo de cada hueco de la estantería, que es esa misma pared de óleo, vista por detrás de las tablas.
 */
internal object ParedEstanteUrss {
    /** Lo hondo: el oscuro con color en que se hunden las sombras. */
    fun honda(g: MarcoUrss): Color = if (g.a.oscuro) g.a.hondo else mezcla(P.OLEO_OSC, P.NEGRO, 0.35f)

    /**
     * Un rectángulo pintado del [color] con la luz de cada sitio: franjas a lo alto de [paso] px, cada una con su degradado a lo
     * ancho. Para lo que se pinta una sola vez (la corona guardada).
     */
    fun DrawScope.campo(g: MarcoUrss, r: Rect, color: Color, delante: Float, mancha: Float?, paso: Float = 4f, muestras: Int = 25) {
        if (r.width < 1f || r.height < 1f) return
        val franjas = ceil(r.height / maxOf(1f, paso)).toInt().coerceIn(1, 500)
        val alto = r.height / franjas
        drawRect(g.luz(r.center.x, r.center.y, delante, mancha).c(color), r.topLeft, r.size)
        for (i in 0 until franjas) {
            val y = r.top + alto * (i + 0.5f)
            val cols = List(muestras) { j -> g.luz(r.left + r.width * j / (muestras - 1f), y, delante, mancha).c(color) }
            drawRect(Brush.horizontalGradient(cols, r.left, r.right), Offset(r.left, r.top + alto * i), Size(r.width, minOf(alto + 1.2f, r.bottom - r.top - alto * i)))
        }
    }

    // ---------------------------------------------------------------- la pared de arriba (se pinta una vez, en la corona guardada)

    fun DrawScope.paredArriba(g: MarcoUrss) {
        val w = g.w; val u = density
        val yR = g.raya
        val ch = g.ch
        val oscuro = g.a.oscuro
        conPanosUrss(g, 0f, Rect(0f, 0f, w, ch)) { s ->
            campo(g, Rect(0f, 0f, w, yR), P.CAL, 0f, s, muestras = if (oscuro) 41 else 17)
            campo(g, Rect(0f, yR, w, ch), P.OLEO, 0f, s, muestras = if (oscuro) 41 else 17)
        }
        // De día, la mancha de luz arriba a la izquierda y el paso de la luz a la sombra a lo ancho (como en el Ideario).
        if (g.a.dia) {
            drawRect(Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.16f), Color.Transparent, P.OLEO_OSC.copy(alpha = 0.08f)), 0f, w), Offset.Zero, Size(w, ch))
            mancha(Offset(w * 0.02f, 0f), w * 0.6f, Color(0xFFFFF6E4).copy(alpha = 0.45f))
        }
        // Los brochazos de la cal y lo amarillo de la cal vieja, hacia arriba y hacia la derecha.
        pinceladas(Rect(0f, 0f, w, yR), if (oscuro) g.a.hondo else P.CAL_OSC, 7101, 44, u, if (oscuro) 0.12f else 0.15f)
        pinceladas(Rect(0f, 0f, w, yR), if (oscuro) mezcla(P.CAL, g.a.hondo, 0.6f) else Color(0xFFFFF4DE), 7102, 24, u, if (oscuro) 0.06f else 0.16f)
        for (k in 0 until 7) {
            val c = Offset(w * (0.25f + 0.72f * azar(7103, k)), yR * (0.05f + 0.55f * azar(7104, k)))
            drawOval(g.luz(c, 0f).c(mezcla(P.CAL_OSC, P.LATON, 0.4f)).copy(alpha = 0.1f), c - Offset(w * 0.06f, w * 0.025f), Size(w * (0.08f + 0.08f * azar(7105, k)), w * 0.05f))
        }
        // Los brochazos del óleo, de arriba abajo, en una baldosa.
        drawRect(TexturasUrss.oleo(u), Offset(0f, yR), Size(w, ch - yR), alpha = if (oscuro) 0.6f else 1f)
        if (g.a.dia) drawRect(Color.White.copy(alpha = 0.08f), Offset(w * 0.02f, yR), Size(w * 0.05f, ch - yR))
        raya(g, yR)
        // Los chorreones del óleo que se pasó de la raya.
        listOf(0.31f to 0.03f, 0.325f to 0.016f, 0.57f to 0.024f, 0.92f to 0.02f, 0.12f to 0.014f).forEach { (fx, largo) ->
            val l = g.luz(w * fx, yR + w * largo * 0.5f, 0f)
            drawLine(l.c(P.OLEO_OSC).copy(alpha = 0.34f), Offset(w * fx, yR + 2f * u), Offset(w * fx, yR + w * largo), 1.1f * u, StrokeCap.Round)
            drawCircle(l.c(P.OLEO_OSC).copy(alpha = 0.45f), 1.3f * u, Offset(w * fx, yR + w * largo))
            drawCircle(l.b(0.4f), 0.5f * u, Offset(w * fx - 0.4f * u, yR + w * largo - 0.4f * u))
        }
        // Las grietas finas de la cal y los desconchones.
        val zona = Rect(0f, 0f, w, ch)
        conPanosTodoUrss(g, 0f, zona) {
            listOf(Offset(0.47f, 0f), Offset(0.985f, 0.16f), Offset(0.09f, 0.24f)).forEachIndexed { i, f -> grieta(g, Offset(w * f.x, w * f.y), 7130 + i) }
            desconchon(g, Offset(w * 0.255f, w * 0.29f), w * 0.015f, 7160, 1.5f, P.CAL_OSC)
            desconchon(g, Offset(w * 0.9f, w * 0.41f), w * 0.016f, 7161, 0.8f, mezcla(P.CAL_OSC, P.OLEO, 0.25f))
            desconchon(g, Offset(w * 0.6f, w * 0.06f), w * 0.011f, 7162, 1.3f, P.CAL_OSC)
        }
        // De noche, el halo de la bombilla bajo la pantalla y el resplandor verde por encima; lo más claro del cuadro, junto a ella.
        if (g.a.lampara > 0f) {
            val f = g.a.lampara
            mancha(g.lampara + Offset(0f, w * 0.06f), w * 0.22f, P.LLAMA.copy(alpha = 0.16f * f))
            mancha(g.lampara - Offset(0f, w * 0.06f), w * 0.16f, P.VERDE_LUZ.copy(alpha = 0.12f * f))
        }
    }

    /** La raya de remate, pintada a pulso: sube y baja un poco, con su filo de luz debajo. */
    private fun DrawScope.raya(g: MarcoUrss, yR: Float) {
        val w = g.w; val u = density
        val raya = Path().apply {
            moveTo(0f, yR - 0.004f * w)
            for (j in 1..12) lineTo(w * j / 12f, yR - 0.004f * w + (azar(7120, j) - 0.5f) * 2f * u)
            for (j in 12 downTo 0) lineTo(w * j / 12f, yR + 0.004f * w + (azar(7121, j) - 0.5f) * 2f * u)
            close()
        }
        drawPath(raya, Brush.horizontalGradient(List(17) { j -> g.luz(w * j / 16f, yR, 0f).c(P.OLEO_OSC) }, 0f, w))
        drawLine(g.luz(w * 0.2f, yR, 0f).b(if (g.a.dia) 0.25f else 0.12f), Offset(0f, yR + 0.006f * w), Offset(w, yR + 0.006f * w), 0.8f * u)
    }

    /** Una grieta fina que baja en zigzag, con su sombra y su filo claro. */
    private fun DrawScope.grieta(g: MarcoUrss, p0: Offset, semilla: Int) {
        val w = g.w; val u = density
        val p = Path().apply {
            moveTo(p0.x, p0.y)
            var q = p0
            for (j in 0 until 6) { q += Offset(w * (0.002f + 0.007f * azar(semilla, j)) * (if (j % 2 == 0) 1f else -0.7f), w * (0.01f + 0.016f * azar(semilla + 10, j))); lineTo(q.x, q.y) }
        }
        val l = g.luz(p0, 0f)
        drawPath(p, l.s(0.3f), style = Stroke(0.7f * u))
        translate(0.7f * u, 0.4f * u) { drawPath(p, l.b(0.35f), style = Stroke(0.4f * u)) }
    }

    /** Un desconchón: la pintura saltada deja ver el yeso, con el escalón de la capa en sombra arriba y con luz abajo. */
    fun DrawScope.desconchon(g: MarcoUrss, c: Offset, r: Float, semilla: Int, estira: Float, yeso: Color) {
        val u = density
        val l = g.luz(c, 0f)
        val p = escamaUrss(c, r * estira, r, semilla, 12)
        drawPath(p, l.c(yeso))
        translate(1.4f * u, 1.6f * u) { drawPath(p, l.s(0.34f), style = Stroke(2f * u)) }
        drawCircle(l.s(0.12f), r * 0.3f, c + Offset(r * 0.2f, r * 0.1f))
        translate(-0.8f * u, -0.9f * u) { drawPath(p, l.b(0.45f), style = Stroke(1f * u)) }
        for (j in 0 until 3) drawCircle(l.c(yeso), (0.6f + azar(semilla, 70 + j)) * u,
            Offset(c.x + r * (1.4f + 0.6f * azar(semilla, 80 + j)) * estira * (if (j == 1) -1f else 1f), c.y + r * (0.8f * azar(semilla, 90 + j) - 0.3f)))
    }

    // ---------------------------------------------------------------- el fondo de cada hueco (en cada dibujo)

    /**
     * El fondo de un hueco: la misma pared de óleo, vista por detrás de las tablas (la estantería no tiene fondo), con sus
     * brochazos; la cara de dentro de los costados, la sombra franca de la tabla de arriba y la del costado que tapa la luz, el roce
     * de los libros abajo, algún desconchón o chorreón, y de noche el charco de la bombilla que le toca.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, g: MarcoUrss) {
        val w = g.w; val u = density
        if (r.width < 2f || r.height < 2f) return
        val xs = List(9) { j -> r.left + r.width * j / 8f }
        // El óleo de la pared en la sombra de la estantería: el mismo, más hondo, para que lo guardado resalte.
        val oleo = mezcla(P.OLEO, P.OLEO_OSC, 0.5f)
        val arriba = xs.map { g.luz(it, r.top + r.height * 0.22f, 0f).c(oleo) }
        val abajo = xs.map { g.luz(it, r.top + r.height * 0.78f, 0f).c(oleo) }
        drawRect(Brush.horizontalGradient(arriba, r.left, r.right), r.topLeft, Size(r.width, r.height * 0.5f + 1f))
        drawRect(Brush.horizontalGradient(abajo, r.left, r.right), Offset(r.left, r.top + r.height * 0.5f), Size(r.width, r.height * 0.5f))
        drawRect(TexturasUrss.oleo(u), r.topLeft, r.size, alpha = if (g.a.oscuro) 0.4f else 0.55f)
        val s = 7300 + k * 17
        // El roce de abajo: el óleo mate y sucio a la altura de los lomos.
        drawRect(g.a.sombra(if (g.a.oscuro) 0.12f else 0.08f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
        // Un desconchón o un chorreón en algunos huecos (lo tapa lo que haya delante, o no).
        if (azar(s, 1) < 0.65f) {
            val c = Offset(r.left + r.width * (0.12f + 0.76f * azar(s, 2)), r.top + r.height * (0.28f + 0.3f * azar(s, 3)))
            desconchon(g, c, w * (0.009f + 0.006f * azar(s, 4)), s + 5, 1.2f + 0.5f * azar(s, 6), mezcla(P.CAL_OSC, P.OLEO, 0.3f))
        }
        // La cara de dentro de los costados: la de la izquierda mira a la derecha (en sombra de día), la de la derecha mira a la luz.
        val hondo = 0.024f * w
        val izq = poligonoUrss(Offset(r.left, r.top), Offset(r.left + hondo, r.top + hondo * 0.5f), Offset(r.left + hondo, r.bottom - hondo * 0.3f), Offset(r.left, r.bottom))
        val der = poligonoUrss(Offset(r.right, r.top), Offset(r.right - hondo, r.top + hondo * 0.5f), Offset(r.right - hondo, r.bottom - hondo * 0.3f), Offset(r.right, r.bottom))
        val li = g.luz(r.left + hondo / 2, r.center.y, 0.015f)
        val ld = g.luz(r.right - hondo / 2, r.center.y, 0.015f)
        val caraI = if (g.a.oscuro) mezcla(li.c(P.ROBLE), g.a.hondo, (0.3f - 0.25f * li.k).coerceAtLeast(0f)) else mezcla(li.c(P.ROBLE_OSC), P.ROBLE, 0.3f)
        val caraD = if (g.a.oscuro) mezcla(ld.c(P.ROBLE), g.a.hondo, (0.3f - 0.25f * ld.k).coerceAtLeast(0f)) else ld.c(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.35f))
        drawPath(izq, caraI)
        drawPath(der, caraD)
        drawPath(izq, TexturasUrss.vetaV(u), alpha = 0.6f)
        drawPath(der, TexturasUrss.vetaV(u), alpha = 0.6f)
        drawLine(ld.b(0.4f), Offset(r.right - hondo, r.top + hondo * 0.5f), Offset(r.right - hondo, r.bottom - hondo * 0.3f), 1f * u)
        // La sombra franca de la tabla de arriba y la del costado que tapa la luz.
        val hs = r.height * (if (g.a.oscuro) 0.14f else 0.2f)
        val ladoLuz = g.hacia(r.center.x, r.center.y)
        val ws = if (g.a.oscuro) 0.012f * w else 0.034f * w
        val sombra = if (ladoLuz < 0f) poligonoUrss(listOf(Offset(r.left + hondo, r.top + hondo * 0.5f), Offset(r.right - hondo, r.top + hondo * 0.5f), Offset(r.right - hondo, r.top + hs),
                Offset(r.left + hondo + ws + hs * 0.4f, r.top + hs), Offset(r.left + hondo + ws, r.top + hs + hs * 0.6f), Offset(r.left + hondo + ws, r.bottom), Offset(r.left + hondo, r.bottom)))
            else poligonoUrss(listOf(Offset(r.right - hondo, r.top + hondo * 0.5f), Offset(r.left + hondo, r.top + hondo * 0.5f), Offset(r.left + hondo, r.top + hs),
                Offset(r.right - hondo - ws - hs * 0.4f, r.top + hs), Offset(r.right - hondo - ws, r.top + hs + hs * 0.6f), Offset(r.right - hondo - ws, r.bottom), Offset(r.right - hondo, r.bottom)))
        drawPath(sombra, g.a.sombra(if (g.a.oscuro) 0.34f else 0.28f))
        drawRect(g.a.sombra(if (g.a.oscuro) 0.3f else 0.18f), Offset(r.left, r.top), Size(r.width, hs * 0.35f))
        // La cara de arriba de la tabla de abajo, vista desde arriba: el roble con la luz que le llega y el canto de atrás.
        val ct = 0.014f * w
        val tc = xs.map { g.luz(it, r.bottom - ct / 2, 0.03f).c(mezcla(P.ROBLE, P.ROBLE_LUZ, 0.3f)) }
        drawRect(Brush.horizontalGradient(tc, r.left, r.right), Offset(r.left, r.bottom - ct), Size(r.width, ct + 0.6f))
        drawRect(TexturasUrss.vetaH(u), Offset(r.left, r.bottom - ct), Size(r.width, ct), alpha = 0.5f)
        drawRect(g.a.sombra(0.35f), Offset(r.left + hondo, r.bottom - ct), Size(r.width - 2 * hondo, 1.2f * u))
        // De noche, lo que la bombilla de cerca enciende del fondo: un charco cálido donde le llega.
        if (g.a.oscuro) {
            val kc = xs.map { g.luz(it, r.center.y, 0f).k }
            val mx = kc.indices.maxByOrNull { kc[it] } ?: 0
            if (kc[mx] > 0.15f) mancha(Offset(xs[mx], r.center.y), r.height * 0.85f, g.luz(xs[mx], r.center.y, 0f).tinte.copy(alpha = 0.1f * kc[mx]))
        }
        // Un trozo de cartel viejo pegado al fondo en algunos huecos (lo tapa lo que haya delante): papel crudo con una banda roja.
        if (k % 3 == 1) {
            val pr = Rect(r.right - 0.19f * w, r.top + r.height * 0.3f, r.right - 0.08f * w, r.top + r.height * 0.72f)
            val lp = g.luz(pr.center.x, pr.center.y, 0.004f)
            clipRect(r.left, r.top, r.right, r.bottom) {
                drawRect(lp.s(0.3f), pr.topLeft + Offset(1.2f * u, 1.6f * u), pr.size)
                drawRect(lp.c(mezcla(P.CARTULINA_OSC, P.CARTON, 0.2f)), pr.topLeft, pr.size)
                drawRect(lp.c(P.ROJO), Offset(pr.left, pr.top + pr.height * 0.18f), Size(pr.width, pr.height * 0.16f))
                drawPath(bloquesUrss(Offset(pr.left + 0.01f * w, pr.top + pr.height * 0.48f), pr.width * 0.75f, 0.006f * w, s + 70, 3), lp.c(P.NEGRO).copy(alpha = 0.7f))
                drawRect(lp.s(0.12f), Offset(pr.left, pr.top + pr.height * 0.5f), Size(pr.width, 0.8f * u))
            }
        }
    }
}
