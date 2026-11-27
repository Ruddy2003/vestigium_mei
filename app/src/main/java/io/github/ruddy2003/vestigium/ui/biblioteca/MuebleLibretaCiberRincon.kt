package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ACERO_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ALUMINIO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.BEIS_OSC
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CABLE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CAFE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CARTON
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.CHAPA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.LINO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.LINO_B
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NEGRO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOCHE
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.NOTA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PAPEL
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.PARED
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.RAYA
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.ROJO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.TABLERO
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCiber.VERDE
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas

/**
 * El rincón de la consola de registro sin lo que cambia con cada dibujo (lo impreso, el carro, lo del cesto): la pared, la esquina
 * del tablero perforado, la cinta perforada colgada, los cables, el suelo, el pie de la consola con la caja del papel, la consola,
 * el papel que sube, el flexo encendido, la taza y la nota. Se pinta una vez por tamaño (`MuebleLibretaCiber.fija`).
 */
internal object RinconLibretaCiber {
    private val P = PaletaLibretaCiber

    fun DrawScope.rincon(g: EscenaLibretaCiber) {
        pared(g)
        tablero(g)
        cintaPerforada(g)
        cables(g)
        nota(g, Offset(minOf(g.x1 + g.h * 0.14f, g.w - g.h * 0.06f), g.h * 0.4f), g.h * 0.075f, 7f, 1)
        suelo(g)
        sombras(g)
        pie(g)
        cestoAtras(g)
        cuerpo(g)
        papel(g)
        flexo(g)
        taza(g, Offset(g.x0 + g.mw * 0.065f, g.capota + 1f * g.u))
    }

    // ---------------------------------------------------------------- la pared y el suelo

    /** La pared azul pizarra: el cian que llega por la izquierda, el charco cálido del flexo, lo alto más hundido, las pinceladas. */
    private fun DrawScope.pared(g: EscenaLibretaCiber) {
        val u = g.u; val w = g.w; val h = g.h
        val y1 = g.rincon
        val paradas = Array(9) { i -> val f = i / 8f; f to g.luz.t(w * f, h * 0.42f).c(PARED, 0.75f) }
        drawRect(Brush.horizontalGradient(*paradas, startX = 0f, endX = w), Offset.Zero, Size(w, y1 + 1f))
        drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.4f), NOCHE.copy(alpha = 0f)), 0f, h * 0.3f), Offset.Zero, Size(w, h * 0.3f))
        pinceladas(Rect(0f, 0f, w, y1), NOCHE, 7101, 34, u, 0.16f)
        mancha(g.lampara + Offset(0f, h * 0.16f), h * 0.42f, P.CALOR.copy(alpha = 0.15f))
        // El rodapié negro.
        val rod = h * 0.03f
        val paradasR = Array(6) { i -> val f = i / 5f; f to g.luz.t(w * f, y1).c(NEGRO, 0.9f) }
        drawRect(Brush.horizontalGradient(*paradasR, startX = 0f, endX = w), Offset(0f, y1 - rod), Size(w, rod))
        drawRect(g.luz.t(w * 0.6f, y1).b(0.35f), Offset(0f, y1 - rod), Size(w, 0.8f * u))
        // Las rozaduras de abajo.
        pinceladas(Rect(0f, y1 - h * 0.14f, w, y1 - rod), NOCHE, 7102, 10, u, 0.22f)
    }

    /** El linóleo a cuadros en perspectiva, con la luz a lo ancho, sus juntas y el charco del flexo. */
    private fun DrawScope.suelo(g: EscenaLibretaCiber) {
        val u = g.u; val w = g.w; val h = g.h
        val y0 = g.rincon
        val filas = ArrayList<Float>()
        var y = y0; var paso = (h - y0) * 0.22f
        while (y < h && filas.size < 10) { filas.add(y); y += maxOf(2f, paso); paso *= 1.45f }
        filas.add(h)
        val cols = 10
        fun xEn(i: Int, yy: Float): Float {
            val xa = w * i / (cols - 1f) * 1.1f - w * 0.05f
            return w / 2 + (xa - w / 2) * (1f + 0.7f * (yy - y0) / (h - y0))
        }
        val claras = Path()
        for (f in 0 until filas.size - 1) for (c in -1 until cols) {
            if ((f + c) % 2 != 0) continue
            val ya = filas[f]; val yb = filas[f + 1]
            claras.addPath(poligonoCiber(xEn(c, ya), ya, xEn(c + 1, ya), ya, xEn(c + 1, yb), yb, xEn(c, yb), yb))
        }
        val ym = (y0 + h) / 2
        drawRect(Brush.horizontalGradient(*Array(6) { i -> val f = i / 5f; f to g.luz.t(w * f, ym).c(LINO, 0.9f) }, startX = 0f, endX = w), Offset(0f, y0), Size(w, h - y0))
        drawPath(claras, Brush.horizontalGradient(*Array(6) { i -> val f = i / 5f; f to g.luz.t(w * f, ym).c(LINO_B, 0.9f) }, startX = 0f, endX = w))
        // El charco del flexo en el suelo, a la derecha del pie: dentro, las losetas encendidas (las claras y las oscuras se siguen
        // distinguiendo); su borde, una elipse en perspectiva.
        val cx0 = g.x1 - h * 0.42f; val cx1 = g.x1 + h * 0.34f
        val charco = Path().apply { addOval(Rect(cx0, y0 + h * 0.035f, cx1, h + h * 0.08f)) }
        clipPath(charco) {
            drawRect(Brush.horizontalGradient(*Array(5) { i -> val f = i / 4f; f to g.luz.t(cx0 + (cx1 - cx0) * f, ym).c(LINO, 2.1f) }, startX = cx0, endX = cx1), Offset(cx0, y0), Size(cx1 - cx0, h - y0))
            drawPath(claras, Brush.horizontalGradient(*Array(5) { i -> val f = i / 4f; f to g.luz.t(cx0 + (cx1 - cx0) * f, ym).c(LINO_B, 2.1f) }, startX = cx0, endX = cx1))
        }
        clipRect(0f, y0, w, h) {
            pinceladas(Rect(0f, y0, w, h), NOCHE, 7110, 12, u, 0.2f)
            val juntas = Path()
            filas.forEach { yy -> juntas.moveTo(0f, yy); juntas.lineTo(w, yy) }
            for (c in -1..cols) { juntas.moveTo(xEn(c, y0), y0); juntas.lineTo(xEn(c, h), h) }
            drawPath(juntas, NOCHE.copy(alpha = 0.35f), style = Stroke(1f * u))
            mancha(Offset(g.x1 - h * 0.04f, h * 0.95f), h * 0.36f, P.CALOR.copy(alpha = 0.14f))
            drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.5f), NOCHE.copy(alpha = 0f)), y0, y0 + h * 0.05f), Offset(0f, y0), Size(w, h * 0.05f))
        }
    }

    // ---------------------------------------------------------------- lo de la pared

    /**
     * La esquina del tablero perforado del Ideario, cortada por el borde de la izquierda: el aglomerado pardo con la luz que le
     * llega, sus agujeros en rejilla con su labio de luz, el canto de la derecha con su grueso y su sombra, y dos tornillos.
     */
    private fun DrawScope.tablero(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val r = Rect(-2f * u, h * 0.05f, g.x0 + g.mw * 0.18f, h * 0.6f)
        if (r.width < 4f) return
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(r.right, r.top + h * 0.02f), Size(h * 0.03f, r.height))
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(r.left, r.bottom), Size(r.width + h * 0.03f, h * 0.02f))
        drawRect(Brush.horizontalGradient(*Array(5) { i -> val f = i / 4f; f to g.luz.t(r.left + r.width * f, r.center.y).c(TABLERO, 0.85f) }, startX = r.left, endX = r.right), r.topLeft, r.size)
        pinceladas(r, NOCHE, 7120, 14, u, 0.13f)
        val paso = h * 0.05f
        val huecos = ArrayList<Offset>(); val labios = ArrayList<Offset>()
        var y = r.top + paso * 0.55f
        while (y < r.bottom - paso * 0.3f) {
            var x = r.left + paso * 0.6f
            while (x < r.right - paso * 0.3f) { huecos.add(Offset(x, y)); labios.add(Offset(x + 0.3f * u, y + 1.3f * u)); x += paso }
            y += paso
        }
        drawPoints(labios, PointMode.Points, g.luz.t(r.center).b(0.3f), 2.8f * u, StrokeCap.Round)
        drawPoints(huecos, PointMode.Points, NOCHE.copy(alpha = 0.86f), 2.8f * u, StrokeCap.Round)
        val t = g.luz.t(r.right, r.center.y)
        drawRect(t.c(mezcla(TABLERO, BEIS, 0.3f)), Offset(r.right - 2f * u, r.top), Size(2f * u, r.height))
        drawRect(t.b(0.4f), Offset(r.left, r.top), Size(r.width, 1f * u))
        drawRect(NOCHE.copy(alpha = 0.4f), Offset(r.left, r.bottom - 1.5f * u), Size(r.width, 1.5f * u))
        listOf(Offset(r.right - h * 0.025f, r.top + h * 0.025f), Offset(r.right - h * 0.025f, r.bottom - h * 0.025f)).forEach { remacheCiber(it, 1.8f * u, g.luz.t(it)) }
    }

    /**
     * La cinta de papel perforado colgada de un gancho del tablero: el rollo visto de frente (el papel enrollado con sus vueltas y el
     * hueco del centro, por donde pasa el gancho) y la cola que cae, con su fila de agujeros y la de los de arrastre, y su sombra.
     */
    private fun DrawScope.cintaPerforada(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val c = Offset(maxOf(h * 0.1f, g.x0 * 0.55f), h * 0.2f)
        val rr = h * 0.065f
        val t = g.luz.t(c)
        // La cola que cae del rollo, un poco combada, y su sombra en el tablero.
        val ancho = h * 0.028f
        val cola = Path().apply {
            moveTo(c.x + rr * 0.55f, c.y + rr * 0.6f)
            cubicTo(c.x + rr * 0.8f, c.y + rr * 2f, c.x - rr * 0.1f, c.y + rr * 3.2f, c.x + rr * 0.3f, c.y + rr * 4.6f)
        }
        translate(h * 0.012f, h * 0.01f) { drawPath(cola, NOCHE.copy(alpha = 0.4f), style = Stroke(ancho)) }
        drawPath(cola, t.c(P.CINTA, 0.95f), style = Stroke(ancho, cap = StrokeCap.Butt))
        drawPath(cola, NOCHE.copy(alpha = 0.55f), style = Stroke(1.6f * u, cap = StrokeCap.Round, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(0.1f * u, 3.4f * u))))
        drawPath(cola, t.b(0.4f), style = Stroke(0.6f * u))
        // El rollo y su sombra.
        drawCircle(NOCHE.copy(alpha = 0.45f), rr, c + Offset(h * 0.014f, h * 0.012f))
        drawCircle(t.c(P.CINTA, 0.95f), rr, c)
        for (k in 1..4) drawCircle(t.c(mezcla(P.CINTA, CAFE, 0.25f)), rr * (0.42f + 0.14f * k), c, style = Stroke(0.6f * u))
        drawArc(t.b(0.6f), 180f, 100f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.1f * u))
        drawArc(t.s(0.4f), 0f, 120f, false, Offset(c.x - rr, c.y - rr), Size(rr * 2, rr * 2), style = Stroke(1.6f * u))
        // El hueco del centro: el cubo de cartón y, dentro, el gancho del tablero, que asoma arriba y sujeta el rollo por el borde.
        drawCircle(t.c(mezcla(P.CINTA, CAFE, 0.45f)), rr * 0.36f, c)
        drawCircle(NOCHE.copy(alpha = 0.8f), rr * 0.27f, c)
        drawArc(t.c(ACERO, 1.1f), 200f, 140f, false, Offset(c.x - rr * 0.2f, c.y - rr * 0.33f), Size(rr * 0.4f, rr * 0.3f), style = Stroke(1.8f * u, cap = StrokeCap.Round))
    }

    /** El mazo de cables que cruza la pared por arriba, sujeto con bridas, y baja por detrás de la consola. */
    private fun DrawScope.cables(g: EscenaLibretaCiber) {
        val u = g.u; val w = g.w; val h = g.h
        val y0 = h * 0.085f
        val xb = g.cx - g.pw * 0.62f
        val cols = listOf(NEGRO to 2.8f, CABLE to 2.2f, mezcla(BEIS_OSC, NEGRO, 0.4f) to 2.4f)
        cols.forEachIndexed { j, (col, gr) ->
            val yy = y0 + j * 2.6f * u
            val p = Path().apply {
                moveTo(-4f * u, yy + 2f * u)
                cubicTo(w * 0.1f, yy + 4f * u, xb - h * 0.2f, yy - 1f * u, xb - h * 0.05f + j * 2f * u, yy)
                cubicTo(xb + j * 2.4f * u, yy, xb + j * 2.4f * u, yy + h * 0.05f, xb + j * 2.4f * u - h * 0.01f, g.capota + h * 0.02f)
            }
            val t = g.luz.t(xb, yy)
            translate(h * 0.01f, h * 0.01f) { drawPath(p, NOCHE.copy(alpha = 0.4f), style = Stroke(gr * u)) }
            drawPath(p, t.c(col), style = Stroke(gr * u, cap = StrokeCap.Round))
            drawPath(p, t.b(0.35f), style = Stroke(gr * 0.3f * u))
        }
        listOf(0.25f, 0.6f).forEach { f ->
            val x = maxOf(h * 0.04f, (xb - h * 0.1f) * f)
            val t = g.luz.t(x, y0)
            drawRect(t.s(0.4f), Offset(x + 1.2f * u, y0 - 1.5f * u), Size(2.2f * u, 10f * u))
            drawRect(t.c(PAPEL, 0.9f), Offset(x, y0 - 2.5f * u), Size(2.2f * u, 10.5f * u))
            drawRect(t.c(PAPEL, 0.9f), Offset(x + 2.2f * u, y0 - 3.5f * u), Size(2.6f * u, 2.2f * u))
        }
    }

    /** Una nota adhesiva amarilla en la pared, ladeada [giro] grados, con garabatos, la punta levantada y su sombra. */
    private fun DrawScope.nota(g: EscenaLibretaCiber, c: Offset, lado: Float, giro: Float, semilla: Int) {
        val u = g.u
        val t = g.luz.t(c)
        val r = Rect(c.x - lado / 2, c.y - lado / 2, c.x + lado / 2, c.y + lado / 2)
        rotate(giro, c) {
            val esq = lado * 0.22f
            val hoja = poligonoCiber(r.left, r.top, r.right, r.top, r.right, r.bottom - esq, r.right - esq, r.bottom, r.left, r.bottom)
            translate(-lado * 0.1f, lado * 0.12f) { drawPath(hoja, NOCHE.copy(alpha = 0.4f)) }
            drawPath(hoja, t.c(NOTA))
            drawRect(t.c(mezcla(NOTA, BEIS_OSC, 0.3f)), r.topLeft, Size(lado, lado * 0.14f))
            garabatosCiber(Rect(r.left + lado * 0.12f, r.top + lado * 0.24f, r.right - lado * 0.14f, r.bottom - lado * 0.2f), 3,
                t.c(mezcla(PARED, NOCHE, 0.4f)), 7130 + semilla, 1f * u)
            val doblez = poligonoCiber(r.right, r.bottom - esq, r.right - esq * 0.2f, r.bottom - esq * 0.2f, r.right - esq, r.bottom)
            drawPath(doblez, t.c(mezcla(NOTA, Color.White, 0.25f)))
        }
    }

    // ---------------------------------------------------------------- las sombras

    /** La sombra de la consola y del papel en la pared (hacia la izquierda: el flexo queda a la derecha) y la del pie en el suelo. */
    private fun DrawScope.sombras(g: EscenaLibretaCiber) {
        val h = g.h
        drawPath(poligonoCiber(g.x0 - h * 0.02f, g.capota + h * 0.04f, g.x0 - h * 0.07f, g.capota + h * 0.07f, g.x0 - h * 0.07f, g.rincon, g.x1 - h * 0.12f, g.rincon,
            g.x1 - h * 0.12f, g.capota + h * 0.04f), NOCHE.copy(alpha = 0.42f))
        drawPath(poligonoCiber(g.px0, g.papelArriba + h * 0.02f, g.px0 - h * 0.07f, g.papelArriba + h * 0.05f, g.px0 - h * 0.07f, g.capota, g.px0, g.capota), NOCHE.copy(alpha = 0.3f))
        drawOval(NOCHE.copy(alpha = 0.55f), Offset(g.x0 - h * 0.05f, g.suelo - h * 0.035f), Size(g.mw + h * 0.04f, h * 0.06f))
        // Las patas y el estante bajo echan su sombra en el charco, hacia abajo y a la izquierda (el flexo queda arriba a la derecha).
        val pa = h * 0.05f
        val xd = g.x1 - g.mw * 0.06f - pa
        drawPath(poligonoCiber(xd, g.suelo - h * 0.01f, xd + pa, g.suelo - h * 0.01f, xd + pa - h * 0.11f, g.h, xd - h * 0.13f, g.h), NOCHE.copy(alpha = 0.42f))
        drawPath(poligonoCiber(g.x0 + g.mw * 0.06f, g.suelo - h * 0.005f, xd + pa, g.suelo - h * 0.005f, xd + pa - h * 0.05f, g.suelo + h * 0.03f, g.x0 + g.mw * 0.06f - h * 0.05f, g.suelo + h * 0.03f),
            NOCHE.copy(alpha = 0.3f))
    }

    // ---------------------------------------------------------------- el pie

    /**
     * El pie de chapa de la consola: dos patas con su canto y su pie nivelador, el travesaño de atrás, el estante bajo con la caja
     * del papel en blanco (las solapas abiertas, la pila con sus bandas) y la tira que sube de la caja a la consola.
     */
    private fun DrawScope.pie(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val pa = h * 0.05f
        val patas = listOf(g.x0 + g.mw * 0.06f, g.x1 - g.mw * 0.06f - pa)
        // El travesaño de atrás, en sombra.
        drawRect(g.luz.t(g.cx, g.base + h * 0.06f).c(mezcla(CHAPA, BEIS_OSC, 0.5f), 0.55f), Offset(patas[0] + pa, g.base), Size(patas[1] - patas[0] - pa, h * 0.05f))
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(patas[0] + pa, g.base), Size(patas[1] - patas[0] - pa, h * 0.012f))
        // La caja del papel en el estante.
        val caja = Rect(g.cx - g.pw * 0.56f, g.estante - h * 0.1f, g.cx + g.pw * 0.56f, g.estante)
        val tc = g.luz.t(caja.center)
        drawPath(poligonoCiber(caja.left, caja.top, caja.left + h * 0.02f, caja.top - h * 0.035f, caja.right - h * 0.02f, caja.top - h * 0.035f, caja.right, caja.top),
            tc.c(mezcla(CARTON, NOCHE, 0.4f)))
        // La pila dentro, vista desde arriba, con sus bandas, y la tira que sube a la consola.
        val pila = Rect(caja.left + h * 0.025f, caja.top - h * 0.03f, caja.right - h * 0.025f, caja.top + h * 0.004f)
        drawRect(tc.c(PAPEL, 0.85f), pila.topLeft, pila.size)
        drawRect(tc.c(RAYA, 0.85f), Offset(pila.left, pila.top + pila.height * 0.4f), Size(pila.width, pila.height * 0.25f))
        // La tira sube de la pila, inclinada hacia atrás, y entra por la trasera de la consola: el papel alumbrado, sus bandas, los
        // agujeros de arrastre de los dos márgenes, el canto de la derecha encendido por el flexo y el de la izquierda, frío.
        val ta = pila.top + pila.height * 0.4f
        val tira = poligonoCiber(g.cx - g.pw * 0.44f, ta, g.cx + g.pw * 0.44f, ta, g.cx + g.pw * 0.4f + h * 0.03f, g.base - 1f, g.cx - g.pw * 0.48f + h * 0.03f, g.base - 1f)
        val tt = g.luz.t(g.cx + g.pw * 0.3f, (ta + g.base) / 2)
        drawPath(tira, Brush.horizontalGradient(listOf(tt.c(PAPEL, 0.9f), tt.c(PAPEL, 1.7f)), g.cx - g.pw * 0.44f, g.cx + g.pw * 0.44f))
        clipPath(tira) {
            var yb = g.base + h * 0.004f
            var ib = 0
            while (yb < ta) { if (ib % 2 == 0) drawRect(tt.c(RAYA, 1.5f).copy(alpha = 0.8f), Offset(g.cx - g.pw * 0.5f, yb), Size(g.pw * 1.1f, h * 0.012f)); yb += h * 0.012f; ib++ }
            val ag = ArrayList<Offset>()
            var ya = g.base + h * 0.008f
            while (ya < ta) {
                val f = (ya - g.base) / (ta - g.base)
                ag.add(Offset(g.cx - g.pw * (0.46f - 0.02f * f) + h * 0.03f * (1f - f) + h * 0.012f, ya))
                ag.add(Offset(g.cx + g.pw * (0.42f + 0.02f * f) + h * 0.03f * (1f - f) - h * 0.012f, ya))
                ya += h * 0.016f
            }
            drawPoints(ag, PointMode.Points, NOCHE.copy(alpha = 0.75f), 2.2f * u, StrokeCap.Round)
            drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.45f), NOCHE.copy(alpha = 0f)), g.base, g.base + h * 0.02f), Offset(g.cx - g.pw * 0.5f, g.base), Size(g.pw * 1.1f, h * 0.02f))
        }
        drawLine(P.CALOR.copy(alpha = 0.7f), Offset(g.cx + g.pw * 0.44f, ta), Offset(g.cx + g.pw * 0.4f + h * 0.03f, g.base), 1.2f * u)
        drawLine(PaletaCiber.CIAN_LUZ.copy(alpha = 0.45f), Offset(g.cx - g.pw * 0.44f, ta), Offset(g.cx - g.pw * 0.48f + h * 0.03f, g.base), 1f * u)
        // El frente de la caja: cartón con la cinta de embalar, una etiqueta sin letras y su canto.
        drawRect(Brush.horizontalGradient(*Array(4) { i -> val f = i / 3f; f to g.luz.t(caja.left + caja.width * f, caja.center.y).c(CARTON, 0.85f) }, startX = caja.left, endX = caja.right), caja.topLeft, caja.size)
        clipRect(caja.left, caja.top, caja.right, caja.bottom) {
            pinceladas(caja, NOCHE, 7140, 5, u, 0.14f)
            drawRect(tc.c(mezcla(CARTON, AMBAR_CARTON, 0.3f)).copy(alpha = 0.75f), Offset(caja.left + caja.width * 0.42f, caja.top), Size(caja.width * 0.14f, caja.height * 0.55f))
            drawRect(tc.c(mezcla(PAPEL, CARTON, 0.3f), 0.8f), Offset(caja.left + caja.width * 0.12f, caja.top + caja.height * 0.35f), Size(caja.width * 0.2f, caja.height * 0.32f))
            drawRect(NOCHE.copy(alpha = 0.25f), Offset(caja.right - caja.width * 0.12f, caja.top), Size(caja.width * 0.12f, caja.height))
        }
        drawRect(tc.b(0.5f), caja.topLeft, Size(caja.width, 0.9f * u))
        val frio = PaletaCiber.CIAN_LUZ
        fun aFrio(x: Float) = (0.2f + 0.9f * g.luz.kFrio(Offset(x, 0f))).coerceAtMost(0.75f)
        drawLine(frio.copy(alpha = aFrio(caja.left)), Offset(caja.left + 0.5f * u, caja.top), Offset(caja.left + 0.5f * u, caja.bottom), 1.2f * u)
        drawLine(frio.copy(alpha = aFrio(caja.left) * 0.8f), Offset(caja.left, caja.top + 0.5f * u), Offset(caja.left + caja.width * 0.35f, caja.top + 0.5f * u), 1f * u)
        // El estante bajo: la cara de arriba y el canto, entre las patas.
        val e0 = g.estante; val e1 = g.estante + h * 0.03f
        drawRect(Brush.horizontalGradient(*Array(4) { i -> val f = i / 3f; f to g.luz.t(patas[0] + (patas[1] - patas[0]) * f, e0).c(CHAPA, 0.8f) }, startX = patas[0], endX = patas[1] + pa), Offset(patas[0], e0), Size(patas[1] + pa - patas[0], e1 - e0))
        drawRect(g.luz.t(g.cx, e0).b(0.55f), Offset(patas[0], e0), Size(patas[1] + pa - patas[0], 1f * u))
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(patas[0], e1 - 1.2f * u), Size(patas[1] + pa - patas[0], 1.2f * u))
        drawRect(Brush.horizontalGradient(listOf(frio.copy(alpha = aFrio(patas[0])), frio.copy(alpha = 0f)), patas[0], patas[0] + (patas[1] - patas[0]) * 0.5f),
            Offset(patas[0], e0), Size((patas[1] - patas[0]) * 0.5f, 1.2f * u))
        drawRect(Brush.horizontalGradient(listOf(frio.copy(alpha = aFrio(patas[0]) * 0.7f), frio.copy(alpha = 0f)), patas[0], patas[0] + (patas[1] - patas[0]) * 0.35f),
            Offset(patas[0], e1 - h * 0.012f), Size((patas[1] - patas[0]) * 0.35f, 1f * u))
        // Las patas: el frente de chapa con la luz a lo alto, el canto de dentro, las ranuras, el pie nivelador y su sombra.
        patas.forEachIndexed { i, x ->
            val t = g.luz.t(x + pa / 2, (g.base + g.suelo) / 2)
            columna(g, x, x + pa, g.base - 1f, g.suelo - h * 0.02f, CHAPA, 0.85f)
            drawRect(NOCHE.copy(alpha = 0.35f), Offset(if (i == 0) x + pa * 0.78f else x, g.base), Size(pa * 0.22f, g.suelo - h * 0.02f - g.base))
            drawRect(t.b(0.5f), Offset(if (i == 0) x else x + pa * 0.8f, g.base), Size(1f * u, g.suelo - h * 0.02f - g.base))
            if (i == 0) drawRect(frio.copy(alpha = aFrio(x)), Offset(x, g.base), Size(1.4f * u, g.suelo - h * 0.02f - g.base))
            else drawRect(P.CALOR.copy(alpha = 0.5f), Offset(x + pa - 1.2f * u, g.base), Size(1.2f * u, g.suelo - h * 0.02f - g.base))
            val ranuras = ArrayList<Offset>()
            var y = g.base + h * 0.03f
            while (y < g.suelo - h * 0.05f) { ranuras.add(Offset(x + pa * 0.45f, y)); y += h * 0.032f }
            drawPoints(ranuras, PointMode.Points, NOCHE.copy(alpha = 0.8f), 3f * u, StrokeCap.Butt)
            drawRect(t.c(ACERO_OSC), Offset(x + pa * 0.3f, g.suelo - h * 0.022f), Size(pa * 0.4f, h * 0.014f))
            drawOval(NOCHE.copy(alpha = 0.6f), Offset(x - pa * 0.3f, g.suelo - h * 0.012f), Size(pa * 1.8f, h * 0.02f))
            drawRoundRect(t.c(NEGRO, 1.1f), Offset(x - pa * 0.05f, g.suelo - h * 0.012f), Size(pa * 1.1f, h * 0.013f), CornerRadius(2f * u))
            drawRect(t.b(0.35f), Offset(x - pa * 0.05f, g.suelo - h * 0.012f), Size(pa * 1.1f, 0.7f * u))
        }
    }

    /** Una columna vertical de [col] con la luz que le llega a lo alto. */
    private fun DrawScope.columna(g: EscenaLibretaCiber, x0: Float, x1: Float, y0: Float, y1: Float, col: Color, mas: Float = 1f) {
        if (x1 <= x0 || y1 <= y0) return
        val xm = (x0 + x1) / 2
        drawRect(Brush.verticalGradient(*Array(5) { i -> val f = i / 4f; f to g.luz.t(xm, y0 + (y1 - y0) * f).c(col, mas) }, startY = y0, endY = y1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
    }

    /** La parte de atrás del cesto de alambre: la rejilla del fondo, en penumbra (el frente y lo que tiene dentro van en cada dibujo). */
    private fun DrawScope.cestoAtras(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val r = g.cesto
        val t = g.luz.t(r.center)
        drawOval(NOCHE.copy(alpha = 0.6f), Offset(r.left - h * 0.02f, r.bottom - h * 0.03f), Size(r.width * 1.1f, h * 0.05f))
        val alambre = t.c(mezcla(NEGRO, ACERO, 0.3f), 0.8f)
        val fondo = Path()
        var x = r.left + r.width * 0.08f
        var j = 0
        while (x < r.right && j < 14) { fondo.moveTo(x, r.top); fondo.lineTo(x + r.width * 0.03f, r.bottom - h * 0.04f); x += r.width / 9f; j++ }
        var y = r.top + h * 0.03f
        while (y < r.bottom - h * 0.03f) { fondo.moveTo(r.left + r.width * 0.05f, y); fondo.lineTo(r.right - r.width * 0.02f, y); y += h * 0.05f }
        drawPath(fondo, alambre.copy(alpha = 0.6f), style = Stroke(1f * u))
        val aro = Rect(r.left, r.top, r.right, r.top + h * 0.07f)
        drawArc(alambre, 180f, 180f, false, aro.topLeft, aro.size, style = Stroke(1.8f * u))
        drawArc(t.b(0.5f), 200f, 120f, false, aro.topLeft.copy(y = aro.top - 1f * u), aro.size, style = Stroke(0.7f * u))
    }

    // ---------------------------------------------------------------- la consola

    /**
     * La consola: la caja del carro (de plástico beis, con los cantos de arriba redondeados, la cavidad oscura bajo la tapa, la
     * perilla del rodillo en el costado), el costado derecho que se va hacia atrás, la cubierta inclinada del teclado con sus
     * teclas lisas y el panel de botones, el frente con la placa sin letras, la rejilla y los pilotos fijos.
     */
    private fun DrawScope.cuerpo(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val dv = Offset(h * 0.04f, -h * 0.026f)
        // El costado derecho de toda la consola, que se va hacia atrás (no mira al flexo: en sombra).
        val lado = poligonoCiber(g.x1, g.capota + h * 0.01f, g.x1 + dv.x, g.capota + dv.y + h * 0.01f, g.x1 + dv.x, g.base + dv.y, g.x1, g.base)
        drawPath(lado, g.luz.t(g.x1 + dv.x, (g.capota + g.base) / 2).c(BEIS_OSC, 0.55f))
        clipPath(lado) { for (k in 0 until 6) drawLine(NOCHE.copy(alpha = 0.5f), Offset(g.x1 + dv.x * 0.2f, g.teclado + h * (0.015f * k)), Offset(g.x1 + dv.x * 0.8f, g.teclado + h * (0.015f * k) + dv.y * 0.6f), 1.4f * u) }
        // La perilla del rodillo, en el costado, a la altura del carro.
        val per = Rect(g.x1 + dv.x * 0.3f, g.capota + h * 0.03f, g.x1 + dv.x * 0.3f + h * 0.03f, g.capota + h * 0.09f)
        drawRoundRect(g.luz.t(per.center).c(NEGRO, 1.1f), per.topLeft, per.size, CornerRadius(h * 0.012f))
        for (k in 0 until 4) drawLine(g.luz.t(per.center).b(0.4f), Offset(per.left + per.width * 0.3f, per.top + per.height * (0.2f + 0.2f * k)), Offset(per.right, per.top + per.height * (0.2f + 0.2f * k)), 0.7f * u)
        // La caja del carro: los dos costados beis a los lados de la tapa, con los cantos de arriba redondeados.
        val caja = Path().apply { addRoundRect(RoundRect(Rect(g.x0, g.capota, g.x1, g.carro + h * 0.01f), CornerRadius(h * 0.03f, h * 0.03f))) }
        drawPath(caja, Brush.horizontalGradient(*Array(7) { i -> val f = i / 6f; f to g.luz.t(g.x0 + g.mw * f, g.capota + h * 0.05f).c(BEIS) }, startX = g.x0, endX = g.x1))
        clipPath(caja) {
            pinceladas(Rect(g.x0, g.capota, g.x1, g.carro), BEIS_OSC, 7150, 10, u, 0.16f)
            drawRect(mezcla(BEIS_OSC, PaletaCiber.AMBAR, 0.3f).copy(alpha = 0.1f), Offset(g.x0, g.capota), Size(g.mw, h * 0.04f))
            // La cavidad del carro, bajo la tapa (lo que se ve por el acrílico): oscura, con el fondo de la caja.
            drawRect(g.luz.t(g.cx, g.capota + h * 0.06f).c(mezcla(NEGRO, NOCHE, 0.3f), 0.6f), Offset(g.hx0 - h * 0.012f, g.capota), Size(g.hx1 - g.hx0 + h * 0.024f, g.carro - g.capota + h * 0.01f))
            drawRect(NOCHE.copy(alpha = 0.3f), Offset(g.x1 - g.mw * 0.04f, g.capota), Size(g.mw * 0.04f, g.carro - g.capota + h * 0.01f))
        }
        drawArc(g.luz.t(g.x0, g.capota).b(0.6f), 180f, 90f, false, Offset(g.x0, g.capota), Size(h * 0.06f, h * 0.06f), style = Stroke(1.2f * u))
        drawLine(g.luz.t(g.x0 + g.mw * 0.06f, g.capota).b(0.7f), Offset(g.x0 + h * 0.03f, g.capota), Offset(g.hx0 - h * 0.012f, g.capota), 1.2f * u)
        drawLine(g.luz.t(g.x1 - g.mw * 0.06f, g.capota).b(0.9f), Offset(g.hx1 + h * 0.012f, g.capota), Offset(g.x1 - h * 0.03f, g.capota), 1.2f * u)
        // La cubierta del teclado: inclinada hacia quien mira (le da la luz de arriba), con el marco hundido y las teclas lisas.
        val c0 = g.carro; val c1 = g.teclado
        val cubierta = poligonoCiber(g.x0, c0, g.x1, c0, g.x1, c1, g.x0, c1)
        drawPath(cubierta, Brush.horizontalGradient(*Array(7) { i -> val f = i / 6f; f to g.luz.t(g.x0 + g.mw * f, (c0 + c1) / 2).c(mezcla(BEIS, Color.White, 0.05f), 1.05f) }, startX = g.x0, endX = g.x1))
        drawRect(NOCHE.copy(alpha = 0.45f), Offset(g.x0, c0), Size(g.mw, h * 0.01f))
        val marco = Rect(g.x0 + g.mw * 0.07f, c0 + h * 0.016f, g.x1 - g.mw * 0.22f, c1 - h * 0.012f)
        drawRoundRect(g.luz.t(marco.center).c(mezcla(NEGRO, BEIS_OSC, 0.3f), 0.9f), marco.topLeft, marco.size, CornerRadius(h * 0.008f))
        drawRect(NOCHE.copy(alpha = 0.4f), marco.topLeft, Size(marco.width, 1.6f * u))
        val filas = 4; val n = 14
        val fh = (marco.height - h * 0.01f) / filas
        for (f in 0 until filas) {
            val y = marco.top + h * 0.006f + fh * f
            val paso = (marco.width - h * 0.016f) / n
            var k = 0
            while (k < n) {
                val ancho = if (f == 3 && k == 4) 6 else 1
                val x = marco.left + h * 0.008f + paso * k + (if (f % 2 == 1) paso * 0.25f else 0f)
                if (x + paso * ancho > marco.right - h * 0.004f) break
                val tk = g.luz.t(x, y)
                val col = if (k == 0 || k == n - 1 || (f == 3 && k != 4)) P.TECLA_OSC else P.TECLA
                drawRect(NOCHE.copy(alpha = 0.5f), Offset(x, y + fh * 0.62f), Size(paso * ancho - h * 0.006f, fh * 0.2f))
                drawRect(tk.c(col), Offset(x, y), Size(paso * ancho - h * 0.006f, fh * 0.66f))
                drawRect(tk.b(0.55f), Offset(x, y), Size(paso * ancho - h * 0.006f, 0.7f * u))
                k += ancho
            }
        }
        // El panel de la derecha: tres botones cuadrados y el interruptor de balancín.
        val pn = Rect(g.x1 - g.mw * 0.19f, c0 + h * 0.018f, g.x1 - g.mw * 0.04f, c1 - h * 0.014f)
        drawRect(g.luz.t(pn.center).c(mezcla(NEGRO, PARED, 0.2f)), pn.topLeft, pn.size)
        for (k in 0 until 3) {
            val b = Rect(pn.left + pn.width * (0.08f + 0.22f * k), pn.top + pn.height * 0.25f, pn.left + pn.width * (0.25f + 0.22f * k), pn.bottom - pn.height * 0.25f)
            drawRect(g.luz.t(b.center).c(if (k == 2) mezcla(ROJO, NEGRO, 0.2f) else P.TECLA), b.topLeft, b.size)
            drawRect(g.luz.t(b.center).b(0.6f), b.topLeft, Size(b.width, 0.7f * u))
        }
        drawRect(g.luz.t(pn.center).b(0.4f), pn.topLeft, Size(pn.width, 0.8f * u))
        // El frente: el plástico beis de cara (menos luz), el canto de la cubierta con su sombra, la placa, la rejilla y los pilotos.
        columna(g, g.x0, g.x1, c1, g.base, BEIS, 0.8f)
        clipRect(g.x0, c1, g.x1, g.base) {
            drawRect(Brush.horizontalGradient(*Array(5) { i -> val f = i / 4f; f to g.luz.t(g.x0 + g.mw * f, (c1 + g.base) / 2).c(BEIS_OSC, 0.75f).copy(alpha = 0.55f) }, startX = g.x0, endX = g.x1),
                Offset(g.x0, c1), Size(g.mw, g.base - c1))
            pinceladas(Rect(g.x0, c1, g.x1, g.base), BEIS_OSC, 7160, 10, u, 0.18f)
        }
        drawRect(g.luz.t(g.cx, c1).b(0.75f), Offset(g.x0, c1), Size(g.mw, 1.1f * u))
        drawRect(NOCHE.copy(alpha = 0.35f), Offset(g.x0, c1 + 1.1f * u), Size(g.mw, h * 0.01f))
        drawRect(NOCHE.copy(alpha = 0.5f), Offset(g.x0, g.base - h * 0.01f), Size(g.mw, h * 0.01f))
        val ym = c1 + (g.base - c1) * 0.38f
        val pl = Rect(g.x0 + g.mw * 0.05f, ym - h * 0.016f, g.x0 + g.mw * 0.2f, ym + h * 0.016f)
        val tp = g.luz.t(pl.center)
        drawRect(tp.s(0.35f), pl.translate(1f * u, 1.2f * u).topLeft, pl.size)
        drawRect(tp.c(ALUMINIO), pl.topLeft, pl.size)
        drawRect(tp.c(mezcla(ACERO_OSC, NOCHE, 0.3f)), Offset(pl.left + pl.width * 0.1f, pl.top + pl.height * 0.4f), Size(pl.width * 0.55f, pl.height * 0.2f))
        drawRect(tp.b(0.55f), pl.topLeft, Size(pl.width, 0.7f * u))
        for (k in 0 until 9) drawRect(NOCHE.copy(alpha = 0.5f), Offset(g.x0 + g.mw * (0.32f + 0.035f * k), ym - h * 0.018f), Size(g.mw * 0.014f, h * 0.036f))
        val tq = g.luz.t(g.x1 - g.mw * 0.2f, ym)
        pilotoCiber(Offset(g.x1 - g.mw * 0.2f, ym), 1.8f * u, VERDE, true, tq)
        pilotoCiber(Offset(g.x1 - g.mw * 0.15f, ym), 1.8f * u, ROJO, false, tq)
        // La pintura gastada del frente, donde se apoyan las manos.
        listOf(0.3f, 0.62f).forEachIndexed { k, f ->
            val p = Offset(g.x0 + g.mw * f, c1 + h * 0.006f)
            drawOval(g.luz.t(p).c(mezcla(BEIS, PAPEL, 0.4f)).copy(alpha = 0.4f), Offset(p.x - h * 0.04f, p.y - h * 0.004f), Size(h * (0.08f + 0.02f * k), h * 0.01f))
        }
    }

    // ---------------------------------------------------------------- el papel

    /**
     * El papel continuo que sale del carro y sube: el papel con la luz que le toca (el flexo enciende su mitad derecha), las bandas
     * verdes, los márgenes de arrastre con sus agujeros, la línea de corte entre hojas, la sombra de su comba, la guía de alambre
     * que lo sujeta sobre la tapa y, arriba, el rizo hacia atrás.
     */
    private fun DrawScope.papel(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val top = g.papelArriba; val bot = g.capota + h * 0.085f
        val r = Rect(g.px0, top, g.px1, bot)
        drawRect(Brush.horizontalGradient(*Array(6) { i -> val f = i / 5f; f to g.luz.t(g.px0 + g.pw * f, h * 0.22f).c(PAPEL, 0.95f) }, startX = g.px0, endX = g.px1), r.topLeft, r.size)
        clipRect(r.left, r.top, r.right, r.bottom) {
            val m = g.pw * 0.08f
            val banda = h * 0.034f
            var y = top + banda * 0.5f
            var i = 0
            while (y < bot) {
                if (i % 2 == 0) drawRect(g.luz.t(g.cx, y).c(RAYA, 0.9f).copy(alpha = 0.75f), Offset(r.left + m, y), Size(r.width - 2 * m, banda))
                y += banda; i++
            }
            val agujeros = ArrayList<Offset>()
            var yy = top + h * 0.012f
            while (yy < bot) { agujeros.add(Offset(r.left + m * 0.5f, yy)); agujeros.add(Offset(r.right - m * 0.5f, yy)); yy += h * 0.026f }
            drawPoints(agujeros, PointMode.Points, g.luz.t(r.center).c(mezcla(PARED, NOCHE, 0.4f), 0.6f), 2.6f * u, StrokeCap.Round)
            listOf(r.left + m, r.right - m).forEach { x ->
                var yl = top
                while (yl < bot) { drawLine(NOCHE.copy(alpha = 0.2f), Offset(x, yl), Offset(x, yl + 1.6f * u), 0.6f * u); yl += 3.6f * u }
            }
            // La línea de corte entre dos hojas.
            val corte = top + h * 0.2f
            var xc = r.left
            while (xc < r.right) { drawLine(NOCHE.copy(alpha = 0.35f), Offset(xc, corte), Offset(xc + 2f * u, corte), 0.7f * u); xc += 4f * u }
            // La comba: lo de la izquierda, más lejos del flexo, en sombra; arriba, la sombra del rizo.
            drawRect(Brush.horizontalGradient(listOf(NOCHE.copy(alpha = 0.22f), NOCHE.copy(alpha = 0f)), r.left, r.left + g.pw * 0.25f), r.topLeft, Size(g.pw * 0.25f, r.height))
            drawRect(g.luz.t(r.right, h * 0.25f).b(0.5f), Offset(r.right - 1.2f * u, r.top), Size(1.2f * u, r.height))
            drawRect(Brush.verticalGradient(listOf(NOCHE.copy(alpha = 0.5f), NOCHE.copy(alpha = 0f)), g.rizoBajo, g.rizoBajo + h * 0.035f), Offset(r.left, g.rizoBajo), Size(r.width, h * 0.035f))
        }
        // El rizo de arriba: el papel se enrolla hacia delante. El lomo del rollo, alumbrado, con su brillo; colgando de él, el
        // reverso del papel (más oscuro, sin las bandas, con los agujeros que lo atraviesan) y su borde de abajo, combado.
        val rizo = Rect(g.px0 - 1f * u, top - h * 0.03f, g.px1 + 1f * u, top + h * 0.006f)
        val tr = g.luz.t(rizo.center)
        drawRoundRect(Brush.horizontalGradient(listOf(tr.c(PAPEL, 0.85f), tr.c(PAPEL, 1.25f)), rizo.left, rizo.right), rizo.topLeft, rizo.size, CornerRadius(rizo.height / 2))
        drawLine(tr.b(0.8f), Offset(rizo.left + rizo.height * 0.4f, rizo.top + 1.4f * u), Offset(rizo.right - rizo.height * 0.4f, rizo.top + 1.4f * u), 1.2f * u)
        val borde = Path().apply { moveTo(g.px1 + 0.5f * u, g.rizoBajo - h * 0.004f); quadraticTo(g.cx, g.rizoBajo + h * 0.008f, g.px0 - 0.5f * u, g.rizoBajo - h * 0.002f) }
        val labio = Path().apply {
            moveTo(g.px0 - 1f * u, rizo.center.y)
            lineTo(g.px1 + 1f * u, rizo.center.y)
            lineTo(g.px1 + 0.5f * u, g.rizoBajo - h * 0.004f)
            quadraticTo(g.cx, g.rizoBajo + h * 0.008f, g.px0 - 0.5f * u, g.rizoBajo - h * 0.002f)
            close()
        }
        drawPath(labio, Brush.horizontalGradient(listOf(tr.c(mezcla(PAPEL, BEIS_OSC, 0.5f), 0.6f), tr.c(mezcla(PAPEL, BEIS_OSC, 0.4f), 1.05f)), g.px0, g.px1))
        clipPath(labio) {
            drawRect(tr.c(RAYA, 0.7f).copy(alpha = 0.18f), Offset(g.px0, rizo.center.y + h * 0.008f), Size(g.pw, h * 0.01f))
            val ag = ArrayList<Offset>()
            var yr = rizo.center.y + h * 0.008f
            while (yr < g.rizoBajo) { ag.add(Offset(g.px0 + g.pw * 0.04f, yr)); ag.add(Offset(g.px1 - g.pw * 0.04f, yr)); yr += h * 0.013f }
            drawPoints(ag, PointMode.Points, NOCHE.copy(alpha = 0.6f), 2.2f * u, StrokeCap.Round)
            drawRect(NOCHE.copy(alpha = 0.25f), Offset(g.px0, rizo.center.y), Size(g.pw, h * 0.008f))
        }
        drawPath(borde, tr.b(0.55f), style = Stroke(1f * u))
        // La guía de alambre, con sus dos rodillos.
        val tg = g.luz.t(g.cx, g.guia)
        drawLine(NOCHE.copy(alpha = 0.45f), Offset(g.px0 - h * 0.03f, g.guia + 2f * u), Offset(g.px1 + h * 0.03f, g.guia + 2f * u), 2f * u)
        drawLine(tg.c(ACERO, 1.2f), Offset(g.px0 - h * 0.03f, g.guia), Offset(g.px1 + h * 0.03f, g.guia), 1.6f * u)
        listOf(g.px0 + g.pw * 0.25f, g.px1 - g.pw * 0.25f).forEach { x ->
            drawRoundRect(tg.c(NEGRO, 1.1f), Offset(x - h * 0.014f, g.guia - h * 0.008f), Size(h * 0.028f, h * 0.016f), CornerRadius(h * 0.008f))
            drawRect(tg.b(0.6f), Offset(x - h * 0.012f, g.guia - h * 0.008f), Size(h * 0.024f, 0.7f * u))
        }
    }

    // ---------------------------------------------------------------- el flexo

    /**
     * El flexo del Ideario, aquí encendido: la base pesada sobre la caja del carro, los dos tramos de varillas con sus muelles y la
     * rótula, y la pantalla cónica que mira hacia el papel, con la boca encendida, el alma de la bombilla y el halo. El brazo, del
     * negro del cuarto con el filo cálido del lado de la bombilla.
     */
    private fun DrawScope.flexo(g: EscenaLibretaCiber) {
        val u = g.u; val h = g.h
        val pie = Offset(g.x1 - g.mw * 0.06f, g.capota + 1f * u)
        val codo = Offset(minOf(g.x1 + h * 0.05f, g.w - h * 0.03f), h * 0.07f)
        val cabeza = g.lampara + Offset(h * 0.03f, -h * 0.045f)
        val t = g.luz.t(cabeza)
        val col = mezcla(NEGRO, PARED, 0.35f)
        // La sombra del brazo en la pared, a la izquierda y abajo.
        translate(-h * 0.03f, h * 0.025f) {
            drawLine(NOCHE.copy(alpha = 0.35f), pie - Offset(0f, h * 0.03f), codo, 3f * u)
            drawLine(NOCHE.copy(alpha = 0.35f), codo, cabeza, 3f * u)
        }
        // La base.
        val tb = g.luz.t(pie)
        val base = Rect(pie.x - h * 0.05f, pie.y - h * 0.028f, pie.x + h * 0.05f, pie.y)
        drawOval(NOCHE.copy(alpha = 0.6f), Offset(base.left - h * 0.01f, pie.y - h * 0.008f), Size(base.width * 1.2f, h * 0.018f))
        drawRoundRect(tb.c(col), base.topLeft, base.size, CornerRadius(h * 0.012f))
        drawOval(tb.c(mezcla(col, ACERO_OSC, 0.3f)), Offset(base.left, base.top - h * 0.01f), Size(base.width, h * 0.02f))
        drawArc(tb.b(0.8f), 200f, 120f, false, Offset(base.left, base.top - h * 0.01f), Size(base.width, h * 0.02f), style = Stroke(1f * u))
        // Los dos tramos: dos varillas paralelas cada uno, con su muelle.
        val p0 = Offset(pie.x, base.top - h * 0.006f)
        listOf(p0 to codo, codo to cabeza).forEach { (a, b) ->
            val dir = (b - a) / (b - a).getDistance()
            val nrm = Offset(-dir.y, dir.x) * (2.2f * u)
            val ta = g.luz.t((a + b) / 2f)
            listOf(nrm, -nrm).forEach { o ->
                drawLine(ta.c(col), a + o, b + o, 1.8f * u, StrokeCap.Round)
                drawLine(ta.b(0.7f), a + o + Offset(-0.6f * u, 0f), b + o + Offset(-0.6f * u, 0f), 0.5f * u)
            }
            val m0 = a + (b - a) * 0.25f; val m1 = a + (b - a) * 0.6f
            for (k in 0..9) {
                val q = m0 + (m1 - m0) * (k / 9f)
                drawLine(ta.c(ACERO_OSC, 1.1f), q + nrm * 2.2f, q - nrm * 2.2f + dir * (1.2f * u), 0.8f * u)
            }
        }
        drawCircle(t.c(ACERO_OSC), 3.6f * u, codo)
        drawCircle(g.luz.t(codo).b(0.6f), 3.6f * u, codo, style = Stroke(0.8f * u))
        drawCircle(t.c(NEGRO), 1.4f * u, codo)
        // La pantalla cónica, que apunta abajo a la izquierda, hacia el papel: el halo, el cono, la boca encendida y el alma.
        mancha(g.lampara, h * 0.16f, P.CALOR.copy(alpha = 0.4f))
        val hl = h * 0.11f
        rotate(38f, cabeza) {
            val cono = poligonoCiber(cabeza.x - hl * 0.18f, cabeza.y - hl * 0.15f, cabeza.x + hl * 0.18f, cabeza.y - hl * 0.15f,
                cabeza.x + hl * 0.55f, cabeza.y + hl * 0.62f, cabeza.x - hl * 0.55f, cabeza.y + hl * 0.62f)
            drawPath(cono, t.c(col, 0.8f))
            clipPath(cono) {
                drawRect(t.b(0.7f), Offset(cabeza.x + hl * 0.18f, cabeza.y - hl * 0.2f), Size(hl * 0.42f, hl))
                drawRect(NOCHE.copy(alpha = 0.35f), Offset(cabeza.x - hl * 0.6f, cabeza.y - hl * 0.2f), Size(hl * 0.35f, hl))
            }
            drawOval(P.CALOR, Offset(cabeza.x - hl * 0.55f, cabeza.y + hl * 0.53f), Size(hl * 1.1f, hl * 0.2f))
            drawOval(P.ALMA, Offset(cabeza.x - hl * 0.32f, cabeza.y + hl * 0.57f), Size(hl * 0.64f, hl * 0.12f))
            drawRoundRect(t.c(ACERO_OSC), Offset(cabeza.x - hl * 0.12f, cabeza.y - hl * 0.32f), Size(hl * 0.24f, hl * 0.2f), CornerRadius(2f * u))
        }
        // El cable del flexo, que baja suelto por detrás de la base.
        val cab = Path().apply { moveTo(cabeza.x + h * 0.02f, cabeza.y - h * 0.02f); cubicTo(codo.x + h * 0.03f, codo.y + h * 0.05f, codo.x + h * 0.02f, pie.y - h * 0.15f, pie.x + h * 0.06f, pie.y - h * 0.01f) }
        drawPath(cab, g.luz.t(codo).c(NEGRO), style = Stroke(1.3f * u))
    }

    /** La taza del Ideario, sobre la caja del carro: beis, con el chorreón de café, el asa a la izquierda (de espaldas a la luz). */
    private fun DrawScope.taza(g: EscenaLibretaCiber, pie: Offset) {
        val u = g.u; val h = g.h
        val a = h * 0.075f; val al = h * 0.085f
        val t = g.luz.t(Offset(pie.x, pie.y - al / 2))
        val ry = a * 0.2f
        val r = Rect(pie.x - a / 2, pie.y - al, pie.x + a / 2, pie.y)
        contactoCiber(Offset(pie.x - a * 0.15f, pie.y), a * 1.5f, 3f * u, t, 0.7f)
        drawArc(t.c(BEIS_OSC), 100f, 160f, false, Offset(r.left - a * 0.25f, r.top + al * 0.2f), Size(a * 0.5f, al * 0.5f), style = Stroke(2.6f * u))
        val cuerpo = Path().apply {
            moveTo(r.left, r.top); lineTo(r.left + a * 0.03f, r.bottom - ry)
            arcTo(Rect(r.left + a * 0.03f, r.bottom - 2 * ry, r.right - a * 0.03f, r.bottom), 180f, -180f, false)
            lineTo(r.right, r.top); close()
        }
        drawPath(cuerpo, t.c(BEIS))
        clipPath(cuerpo) {
            planosCiber(r, t, 1.2f)
            drawRect(t.c(CAFE).copy(alpha = 0.55f), Offset(r.left + a * 0.62f, r.top), Size(a * 0.07f, al * 0.45f))
        }
        drawOval(t.c(mezcla(BEIS, Color.White, 0.15f)), Offset(r.left, r.top - ry), Size(a, 2 * ry))
        drawOval(t.c(CAFE), Offset(r.left + a * 0.1f, r.top - ry * 0.6f), Size(a * 0.8f, ry * 1.3f))
        drawArc(t.b(0.6f), 280f, 120f, false, Offset(r.left, r.top - ry), Size(a, 2 * ry), style = Stroke(0.8f * u))
    }

    /** El ámbar de la cinta de embalar del cartón. */
    private val AMBAR_CARTON = PaletaCiber.AMBAR
}
