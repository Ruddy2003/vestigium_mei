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
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.TrazosNouveau
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * Lo que está encima de la mesa en el rincón del carnet de bocetos de Art nouveau ([MuebleLibretaNouveau]): el atril con su
 * repisa y su sombra en la pared, el florero con la flor del día, la caja de acuarelas, el vaso del agua y la lámpara colgada.
 * Todo se pinta una sola vez, dentro de la imagen guardada, con la luz del sitio donde está.
 */
internal object PiezasLibretaNouveau {
    private val P = PaletaLibretaNouveau
    /** Los colores de las seis flores del día ([FloresNouveau]) cuando son modelo: campanilla, amapola, lirio, cardo, umbela y rosa. */
    private val FLORES = listOf(Color(0xFF8E6CB8), Color(0xFFD0705A), Color(0xFFF0E6CC), Color(0xFFA97AB0), Color(0xFFEFE4C8), Color(0xFFDB9AA0))
    /** Dónde nace el tallo de cada flor, en su cuadro (para que salga del cuello del florero). */
    private val TALLOS = floatArrayOf(-0.3f, 0.12f, -0.22f, 0.04f, -0.08f, -0.26f)

    // ---------------------------------------------------------------- lo de encima de la mesa

    /** El atril de mesa: las dos patas que se juntan arriba (lo que se ve de ellas, detrás del carnet) y su capullo tallado. */
    fun DrawScope.atril(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val h = pl.h
        val c = pl.cima
        val abre = pl.cw / 2f + h * 0.02f
        val pie = pl.mesaFondo + h * 0.016f
        val g = h * 0.021f
        val lado = if (a.oscuro) -1f else 1f
        for (s in floatArrayOf(-1f, 1f)) {
            val fin = Offset(c.x + s * abre, pie)
            val q = Offset(c.x + s * abre * 0.6f, (c.y + pie) / 2f)
            val col = a.luz(P.NOGAL, pl.k(q), pl.f(q), pl.d(q))
            drawLine(a.sombra(0.3f), c + Offset(-h * 0.02f, h * 0.014f), fin + Offset(-h * 0.02f, h * 0.002f), g)
            drawLine(col, c, fin, g, StrokeCap.Round)
            drawLine(if (a.oscuro) a.filo(0.5f, pl.k(q)) else a.claro(0.4f), c + Offset(lado * g * 0.3f, g * 0.6f), fin + Offset(lado * g * 0.3f, -g * 0.5f), 1f * u)
            drawLine(a.sombra(0.3f), c + Offset(-lado * g * 0.32f, g * 0.6f), fin + Offset(-lado * g * 0.32f, -g * 0.5f), 0.8f * u)
            drawOval(a.sombra(0.35f), Offset(fin.x - h * 0.02f, fin.y - h * 0.002f), Size(h * 0.04f, h * 0.008f))
        }
        // La cima: el taco tallado donde se juntan las patas, el collarín, el capullo y sus dos hojas que bajan.
        val kc = pl.k(c); val fc = pl.f(c)
        val madera = a.luz(P.NOGAL, kc, fc, pl.d(c))
        val claro = a.luz(P.NOGAL_LUZ, kc, fc, pl.d(c) + 0.1f)
        val taco = Rect(c.x - h * 0.03f, c.y - h * 0.008f, c.x + h * 0.03f, c.y + h * 0.02f)
        drawRect(a.sombra(0.3f), taco.topLeft + Offset(-h * 0.012f, h * 0.008f), taco.size)
        drawRect(madera, taco.topLeft, taco.size)
        drawLine(if (a.oscuro) a.filo(0.55f, kc) else a.claro(0.5f), Offset(taco.left, taco.top + 0.7f * u), Offset(taco.right, taco.top + 0.7f * u), 1.2f * u)
        drawLine(a.sombra(0.4f), Offset(taco.left, taco.bottom - 0.6f * u), Offset(taco.right, taco.bottom - 0.6f * u), 1.2f * u)
        drawCircle(a.sombra(0.35f), h * 0.0045f, taco.center + Offset(0.6f * u, 0.8f * u))
        drawCircle(a.luz(P.BRONCE, kc, fc, pl.d(c)), h * 0.0045f, taco.center)
        val top = taco.top
        drawPath(TrazosNouveau.hojaLanza(Offset(c.x, top - h * 0.006f), Offset(c.x - h * 0.048f, top + h * 0.012f), h * 0.012f, -0.28f), madera)
        drawPath(TrazosNouveau.hojaLanza(Offset(c.x, top - h * 0.006f), Offset(c.x + h * 0.048f, top + h * 0.012f), h * 0.012f, 0.28f), madera)
        drawRect(mezcla(madera, a.sombra(1f), 0.2f), Offset(c.x - h * 0.014f, top - h * 0.012f), Size(h * 0.028f, h * 0.01f))
        drawOval(madera, Offset(c.x - h * 0.014f, top - h * 0.06f), Size(h * 0.028f, h * 0.052f))
        drawOval(if (a.oscuro) mezcla(claro, P.LLAMA, 0.3f * kc) else claro, Offset(c.x - h * 0.005f + lado * h * 0.004f, top - h * 0.054f), Size(h * 0.01f, h * 0.032f))
        drawLine(a.sombra(0.35f), Offset(c.x - lado * h * 0.004f, top - h * 0.056f), Offset(c.x - lado * h * 0.004f, top - h * 0.014f), 0.7f * u)
    }

    /** La sombra que el carnet echa en la pared: de día, a la izquierda y abajo; de noche, lejos de la lámpara. */
    fun DrawScope.sombraDelCarnet(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val h = pl.h
        val t = pl.tapa
        val o = Offset(h * 0.032f, -h * 0.025f)
        val sv = if (a.oscuro) {
            val v = t.center - pl.foco; val d = v.getDistance().coerceAtLeast(1f)
            v / d * (h * 0.05f)
        } else Offset(-h * 0.045f, h * 0.028f)
        val forma = poligonoNouveau(t.left, t.top, t.left + o.x, t.top + o.y, t.right + o.x, t.top + o.y, t.right + o.x, t.bottom + o.y, t.right, t.bottom, t.left, t.bottom)
        clipRect(0f, 0f, pl.w, pl.mesaFondo) {
            translate(sv.x, sv.y) {
                drawPath(forma, a.sombra(if (a.oscuro) 0.42f else 0.25f))
                translate(-sv.x * 0.3f, -sv.y * 0.3f) { drawPath(forma, a.sombra(if (a.oscuro) 0.25f else 0.1f)) }
            }
        }
    }

    /**
     * La repisa del atril, delante del carnet: el listón con su labio y su filete, las puntas enroscadas, y en la canal el lápiz
     * de laca violeta (afilado a navaja) y el carboncillo.
     */
    fun DrawScope.repisa(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val h = pl.h
        val r = pl.repisa
        val t = pl.tapa
        // El carboncillo y el lápiz, medio hundidos en la canal.
        val yl = pl.labio - h * 0.002f
        val xc0 = t.left + pl.cw * 0.12f
        val kc = pl.k(Offset(xc0, yl))
        drawLine(a.luz(Color(0xFF2A2626), kc, 0f, 0.6f), Offset(xc0, yl), Offset(xc0 + pl.cw * 0.09f, yl - h * 0.002f), h * 0.009f, StrokeCap.Round)
        val lp0 = Offset(t.left + pl.cw * 0.48f, yl - h * 0.001f); val lp1 = Offset(t.left + pl.cw * 0.8f, yl - h * 0.003f)
        val kl = pl.k(lp0); val fl = pl.f(lp1)
        drawLine(a.luz(P.VIOLETA, kl, fl, 0.7f), lp0, lp1, h * 0.0095f)
        drawLine(a.luz(P.CINTA_LUZ, kl, fl, 0.8f).copy(alpha = 0.6f), lp0 + Offset(0f, -h * 0.0025f), lp1 + Offset(0f, -h * 0.0025f), h * 0.0025f)
        drawPath(poligonoNouveau(lp0.x, lp0.y - h * 0.0047f, lp0.x, lp0.y + h * 0.0047f, lp0.x - h * 0.024f, lp0.y), a.luz(Color(0xFFD9B98A), kl, fl, 0.7f))
        drawPath(poligonoNouveau(lp0.x - h * 0.016f, lp0.y - h * 0.0016f, lp0.x - h * 0.016f, lp0.y + h * 0.0016f, lp0.x - h * 0.024f, lp0.y), a.luz(Color(0xFF3A3638), kl, fl, 0.6f))
        drawLine(a.luz(P.ORO, kl, fl, 0.8f), lp1 + Offset(-h * 0.006f, 0f), lp1, h * 0.0098f)
        // El listón.
        val frente = Rect(r.left, pl.labio, r.right, r.bottom)
        drawRect(a.sombra(0.35f), Offset(frente.left + (if (a.oscuro) 2f else -2f) * u, frente.bottom), Size(frente.width, h * 0.01f))
        campoLibretaNouveau(frente, 20, 2) { q -> a.luz(P.NOGAL, pl.k(q), pl.f(q), pl.d(q) + 0.05f) }
        tramosLibretaNouveau(frente.left, frente.right, frente.top + 0.7f * u, 1.3f * u, 8) { q -> if (a.oscuro) mezcla(a.luz(P.NOGAL_LUZ, pl.k(q) * 1.2f, pl.f(q)), P.LLAMA, 0.3f * pl.k(q)) else a.luz(P.NOGAL_LUZ, 0f, 0f, pl.d(q) + 0.25f) }
        drawLine(a.sombra(0.4f), Offset(frente.left, frente.bottom - 0.6f * u), Offset(frente.right, frente.bottom - 0.6f * u), 1.2f * u)
        // Las puntas enroscadas.
        for ((xe, s) in listOf(frente.left to -1f, frente.right to 1f)) {
            val c = Offset(xe + s * h * 0.004f, frente.center.y)
            val col = a.luz(P.NOGAL, pl.k(c), pl.f(c), pl.d(c))
            drawCircle(col, frente.height * 0.62f, c)
            drawCircle(a.sombra(0.4f), frente.height * 0.62f, c, style = Stroke(0.8f * u))
            drawCircle(a.sombra(0.3f), frente.height * 0.28f, c + Offset(s * 0.6f * u, 0.4f * u), style = Stroke(0.7f * u))
        }
    }

    /**
     * El florero de vidrio irisado, contra la pared, con la flor del día que se está dibujando ([flor]: la de [FloresNouveau]); su
     * sombra en la pared y en el tablero.
     */
    fun DrawScope.florero(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, flor: Int) {
        val u = pl.u; val h = pl.h
        val b = pl.florero
        val alto = h * 0.13f; val rb = h * 0.026f; val cuello = h * 0.0058f
        val labio = b.y - alto
        val k = pl.k(Offset(b.x, labio)); val f = pl.f(b); val d = pl.d(b)
        // La flor (el tallo nace en la boca del florero).
        val r = h * 0.085f
        val o = Offset(b.x - TALLOS[flor] * r, labio - r)
        if (!a.oscuro) translate(-h * 0.04f, h * 0.02f) {
            with(FloresNouveau) { estampa(flor, o, r, a.sombra(0.12f)) }
        }
        with(FloresNouveau) {
            deColor(flor, o, r, a.luz(FLORES[flor], k, f, d + 0.1f), a.luz(P.TALLO, k, f, d), a.luz(P.PASTILLAS[2], k, f, d), a.sombra(0.6f),
                luz = if (a.oscuro) P.LLAMA.copy(alpha = 0.3f * k) else Color.White.copy(alpha = 0.25f))
        }
        // El florero.
        val vaso = Path().apply {
            moveTo(b.x - rb * 0.75f, b.y)
            cubicTo(b.x - rb * 1.3f, b.y - rb * 0.4f, b.x - rb * 1.1f, b.y - rb * 1.7f, b.x - cuello, b.y - rb * 2.4f)
            lineTo(b.x - cuello * 0.8f, labio + h * 0.012f)
            quadraticTo(b.x - cuello * 0.9f, labio + h * 0.004f, b.x - cuello * 1.6f, labio)
            lineTo(b.x + cuello * 1.6f, labio)
            quadraticTo(b.x + cuello * 0.9f, labio + h * 0.004f, b.x + cuello * 0.8f, labio + h * 0.012f)
            lineTo(b.x + cuello, b.y - rb * 2.4f)
            cubicTo(b.x + rb * 1.1f, b.y - rb * 1.7f, b.x + rb * 1.3f, b.y - rb * 0.4f, b.x + rb * 0.75f, b.y)
            close()
        }
        if (!a.oscuro) translate(-h * 0.03f, h * 0.012f) { clipRect(0f, 0f, pl.w, pl.mesaFondo) { drawPath(vaso, a.sombra(0.14f)) } }
        drawOval(a.sombra(0.35f), Offset(b.x - rb * (if (a.oscuro) 0.6f else 1.6f), b.y - h * 0.004f), Size(rb * 2.4f, h * 0.008f))
        drawPath(vaso, Brush.verticalGradient(listOf(a.luz(P.VIOLETA, k, f, d), a.luz(P.VIDRIO_VERDE, k * 0.9f, f, d), a.luz(mezcla(P.VIDRIO_VERDE, P.AMBAR, 0.4f), k * 0.8f, f, d)), labio, b.y))
        // El irisado y el brillo del lado de la luz.
        val lado = if (a.oscuro) -1f else 1f
        drawPath(Path().apply {
            moveTo(b.x + lado * rb * 0.55f, b.y - rb * 0.4f)
            quadraticTo(b.x + lado * rb * 0.85f, b.y - rb * 1.2f, b.x + lado * cuello * 0.4f, b.y - rb * 2.3f)
        }, if (a.oscuro) a.filo(0.7f, k) else a.claro(0.6f), style = Stroke(1.4f * u, cap = StrokeCap.Round))
        drawPath(vaso, a.luz(P.CINTA_LUZ, k, f, d).copy(alpha = 0.3f), style = Stroke(2f * u))
        drawPath(vaso, a.sombra(0.5f), style = Stroke(0.6f * u))
    }

    /**
     * La caja de acuarelas de charol negro, abierta: la tapa de esmalte blanco levantada detrás, con los pocillos y las aguadas
     * de lo que se mezcló; las pastillas de color en la caja; el filete dorado y el cierre; el pincel cruzado encima.
     */
    fun DrawScope.caja(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val h = pl.h
        val r = pl.caja
        val k = (pl.k(r.center) * 1.15f).coerceAtMost(1f); val f = pl.f(r.center); val d = pl.d(r.center)
        val frente = Rect(r.left, r.bottom - h * 0.028f, r.right, r.bottom)
        val fondo = frente.top - h * 0.016f
        // Su sombra en el tablero.
        drawOval(a.sombra(0.38f), Offset(r.left + (if (a.oscuro) h * 0.02f else -h * 0.03f), r.bottom - h * 0.008f), Size(r.width * 1.05f, h * 0.016f))
        // La tapa levantada y echada atrás (baja y ancha), de esmalte por dentro: la paleta de los pocillos y las aguadas.
        val alto = h * 0.056f
        val tapa = poligonoNouveau(r.left + h * 0.006f, fondo, r.right - h * 0.006f, fondo, r.right - h * 0.02f, fondo - alto, r.left + h * 0.02f, fondo - alto)
        drawPath(tapa, a.luz(P.CHAROL, k, f, d))
        val dentro = poligonoNouveau(r.left + h * 0.0095f, fondo - h * 0.0015f, r.right - h * 0.0095f, fondo - h * 0.0015f, r.right - h * 0.022f, fondo - alto + h * 0.003f, r.left + h * 0.022f, fondo - alto + h * 0.003f)
        val esmalte = a.papel(P.ESMALTE, k, d)
        drawPath(dentro, esmalte)
        clipPath(dentro) {
            // Cinco pocillos redondos arriba, hondos (la sombra arriba, la luz abajo), con lo que quedó de cada color.
            for (m in 0 until 5) {
                val c = Offset(r.left + r.width * (0.22f + 0.14f * m), fondo - alto * 0.68f)
                val rw = r.width * 0.052f; val rh = alto * 0.17f
                drawOval(a.sombra(0.3f), Offset(c.x - rw, c.y - rh), Size(rw * 2f, rh * 2f))
                drawOval(mezcla(esmalte, a.luz(P.PASTILLAS[m], k, f, d), 0.55f), Offset(c.x - rw * 0.8f, c.y - rh * 0.5f), Size(rw * 1.6f, rh * 1.4f))
            }
            // Las dos canaletas de mezclar, con su aguada corrida.
            for (m in 0 until 2) {
                val x0 = r.left + r.width * (0.18f + 0.34f * m); val y0 = fondo - alto * 0.34f
                drawRect(a.sombra(0.2f), Offset(x0, y0), Size(r.width * 0.3f, alto * 0.2f))
                drawOval(a.luz(P.PASTILLAS[if (m == 0) 0 else 1], k, f, d).copy(alpha = 0.45f), Offset(x0 + r.width * 0.02f, y0 + alto * 0.04f), Size(r.width * 0.22f, alto * 0.14f))
            }
        }
        // Las bisagras.
        for (xb in floatArrayOf(r.left + r.width * 0.2f, r.right - r.width * 0.2f)) drawRect(a.luz(P.BRONCE, k, f, d), Offset(xb - h * 0.006f, fondo - h * 0.002f), Size(h * 0.012f, h * 0.004f))
        // La caja por dentro, vista desde arriba: las pastillas de color.
        drawPath(poligonoNouveau(r.left + h * 0.006f, fondo, r.right - h * 0.006f, fondo, r.right, frente.top, r.left, frente.top), a.luz(P.CHAROL, k * 0.7f, f, d))
        val n = 6
        val celda = (r.width - h * 0.02f) / n
        for (m in 0 until n) {
            val x = r.left + h * 0.01f + celda * m
            val pa = poligonoNouveau(x + celda * 0.12f, fondo + h * 0.003f, x + celda * 0.88f, fondo + h * 0.003f, x + celda * 0.92f, frente.top - h * 0.002f, x + celda * 0.08f, frente.top - h * 0.002f)
            drawPath(pa, a.luz(P.PASTILLAS[m], k, f, d))
            drawLine(if (a.oscuro) a.filo(0.5f, k) else a.claro(0.45f), Offset(x + celda * 0.2f, fondo + h * 0.005f), Offset(x + celda * 0.55f, fondo + h * 0.005f), 0.8f * u)
        }
        // El frente de charol, con su filete dorado y el cierre.
        drawRect(a.luz(P.CHAROL, k * 0.8f, f, d), frente.topLeft, frente.size)
        val oro = a.luz(P.ORO, k, f, d)
        drawLine(oro.copy(alpha = 0.8f), Offset(frente.left + h * 0.006f, frente.top + h * 0.006f), Offset(frente.right - h * 0.006f, frente.top + h * 0.006f), 0.7f * u)
        drawLine(oro.copy(alpha = 0.6f), Offset(frente.left + h * 0.006f, frente.bottom - h * 0.006f), Offset(frente.right - h * 0.006f, frente.bottom - h * 0.006f), 0.7f * u)
        drawRect(oro, Offset(frente.center.x - h * 0.006f, frente.top + h * 0.003f), Size(h * 0.012f, h * 0.01f))
        drawLine(if (a.oscuro) a.filo(0.6f, k) else a.claro(0.5f), Offset(frente.left, frente.top + 0.5f * u), Offset(frente.right, frente.top + 0.5f * u), 1f * u)
        // El pincel, cruzado sobre la caja.
        val p0 = Offset(r.left - h * 0.012f, frente.top + h * 0.002f); val p1 = Offset(r.right + h * 0.01f, fondo - h * 0.004f)
        val dir = (p1 - p0) / (p1 - p0).getDistance()
        val virola = p0 + dir * (h * 0.03f)
        drawLine(a.sombra(0.35f), p0 + Offset(0f, h * 0.005f), p1 + Offset(0f, h * 0.005f), h * 0.006f)
        drawLine(a.luz(Color(0xFF1F1A1C), k, f, d), virola, p1, h * 0.006f, StrokeCap.Round)
        drawLine(if (a.oscuro) a.filo(0.5f, k) else a.claro(0.35f), virola + Offset(0f, -h * 0.0018f), p1 + Offset(0f, -h * 0.0018f), 0.6f * u)
        drawLine(a.luz(P.BRONCE_LUZ, k, f, d), virola - dir * (h * 0.012f), virola, h * 0.0065f)
        drawPath(poligonoNouveau(virola.x - dir.x * h * 0.012f - dir.y * h * 0.003f, virola.y - dir.y * h * 0.012f + dir.x * h * 0.003f,
            virola.x - dir.x * h * 0.012f + dir.y * h * 0.003f, virola.y - dir.y * h * 0.012f - dir.x * h * 0.003f, p0.x, p0.y), a.luz(P.PASTILLAS[0], k, f, d))
    }

    /** El vaso del agua de lavar los pinceles: el vidrio, el agua teñida de violeta gris, su superficie y el pincel que se quiebra al entrar. */
    fun DrawScope.vaso(pl: PlanoLibretaNouveau, a: AireLibretaNouveau, r: Rect) {
        val u = pl.u; val h = pl.h
        val k = pl.k(r.center); val f = pl.f(r.center); val d = pl.d(r.center)
        drawOval(a.sombra(0.35f), Offset(r.left - (if (a.oscuro) 0f else h * 0.016f), r.bottom - h * 0.006f), Size(r.width * 1.3f, h * 0.012f))
        val agua = r.top + r.height * 0.36f
        val e = h * 0.006f
        val vidrio = poligonoNouveau(r.left, r.top, r.right, r.top, r.right - r.width * 0.06f, r.bottom, r.left + r.width * 0.06f, r.bottom)
        drawPath(vidrio, a.luz(Color(0xFFD9E2DA), k, f, d).copy(alpha = 0.22f))
        // El pincel: debajo del agua, corrido (se quiebra); encima, recto.
        val p0 = Offset(r.left + r.width * 0.35f, r.bottom - h * 0.01f); val p1 = Offset(r.right + h * 0.024f, r.top - h * 0.06f)
        val dir = (p1 - p0) / (p1 - p0).getDistance()
        val t = (agua - p0.y) / dir.y
        val entra = p0 + dir * t
        drawLine(a.luz(Color(0xFF1F1A1C), k, f, d), p0 + Offset(1.6f * u, 0f), entra + Offset(1.6f * u, 0f), h * 0.0055f)
        drawPath(poligonoNouveau(r.left + r.width * 0.06f, agua, r.right - r.width * 0.06f * 0.36f, agua, r.right - r.width * 0.06f, r.bottom, r.left + r.width * 0.06f, r.bottom),
            a.luz(Color(0xFF9A90B6), k, f, d).copy(alpha = 0.55f))
        drawOval(a.luz(Color(0xFFC8C0DA), k, f, d + 0.1f).copy(alpha = 0.7f), Offset(r.left + 0.5f * u, agua - e / 2f), Size(r.width - 1f * u, e))
        drawLine(a.luz(Color(0xFF1F1A1C), k, f, d), entra, p1, h * 0.0055f, StrokeCap.Round)
        drawLine(a.luz(P.BRONCE_LUZ, k, f, d), p1 - dir * (h * 0.012f), p1 - dir * (h * 0.024f), h * 0.0062f)
        // El vidrio: el borde de arriba, el fondo grueso y los brillos.
        drawOval(a.luz(Color(0xFFE8EEE8), k, f, d).copy(alpha = 0.6f), Offset(r.left, r.top - e / 2f), Size(r.width, e), style = Stroke(0.8f * u))
        drawLine(a.luz(Color(0xFFBFC9C2), k, f, d).copy(alpha = 0.7f), Offset(r.left + r.width * 0.08f, r.bottom - 1.2f * u), Offset(r.right - r.width * 0.08f, r.bottom - 1.2f * u), 2f * u)
        val lado = if (a.oscuro) 0.22f else 0.78f
        drawLine(if (a.oscuro) a.filo(0.7f, k) else a.claro(0.65f), Offset(r.left + r.width * lado, r.top + h * 0.006f), Offset(r.left + r.width * lado, r.bottom - h * 0.008f), 1.2f * u)
        drawPath(vidrio, a.sombra(0.3f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- la lámpara

    /**
     * La lámpara colgada del techo: la vara de bronce que cruza el friso, el collarín con su galería de látigos y la pantalla de
     * vidrio en campana de cinco pétalos, del ámbar al violeta, con hojas grabadas al ácido. De noche, encendida: el vidrio
     * arde de dentro, se ve el hueco claro de la pantalla y la bombilla.
     */
    fun DrawScope.lampara(pl: PlanoLibretaNouveau, a: AireLibretaNouveau) {
        val u = pl.u; val h = pl.h
        val x = pl.xLampara
        val s = pl.pantalla
        val y0 = s.top; val y1 = s.bottom
        val bronce = if (a.oscuro) a.luz(P.BRONCE, 0.75f) else a.luz(P.BRONCE, 0f, 0f, 0.6f)
        // La vara y el collarín.
        drawLine(bronce, Offset(x, 0f), Offset(x, y0 - h * 0.01f), h * 0.007f)
        drawLine(if (a.oscuro) a.filo(0.5f, 0.6f) else a.claro(0.5f), Offset(x + (if (a.oscuro) -1f else 1f) * h * 0.0015f, 0f), Offset(x + (if (a.oscuro) -1f else 1f) * h * 0.0015f, y0 - h * 0.012f), 0.7f * u)
        drawOval(bronce, Offset(x - h * 0.016f, y0 - h * 0.018f), Size(h * 0.032f, h * 0.012f))
        val galeria = a.luz(P.BRONCE_LUZ, if (a.oscuro) 0.8f else 0f, 0f, 0.7f)
        listOf(-1f, 1f).forEach { sx ->
            with(TrazosNouveau) { latigo(Offset(x + sx * h * 0.004f, y0 - h * 0.012f), Offset(x + sx * h * 0.03f, y0 + h * 0.008f), h * 0.008f, h * 0.0045f, galeria, 0.3f * sx, sx < 0, false) }
        }
        // La pantalla.
        val rn = h * 0.022f; val rb = s.width / 2f
        val alto = y1 - y0
        val pantalla = Path().apply {
            moveTo(x - rn, y0)
            cubicTo(x - rn * 1.5f, y0 + alto * 0.45f, x - rb * 0.92f, y1 - alto * 0.38f, x - rb, y1 - h * 0.012f)
            for (m in 0 until 5) {
                val xa = x - rb + 2f * rb * m / 5f; val xb = x - rb + 2f * rb * (m + 1) / 5f
                quadraticTo((xa + xb) / 2f, y1 + h * 0.016f, xb, y1 - h * 0.012f)
            }
            cubicTo(x + rb * 0.92f, y1 - alto * 0.38f, x + rn * 1.5f, y0 + alto * 0.45f, x + rn, y0)
            close()
        }
        // El hueco de la pantalla, visto desde abajo: queda detrás de los pétalos del borde.
        val hueco = Rect(x - rb * 0.8f, y1 - h * 0.01f, x + rb * 0.8f, y1 + h * 0.015f)
        drawOval(if (a.oscuro) P.FUEGO else a.luz(mezcla(P.VIOLETA, P.CHAROL, 0.4f), 0f, 0f, 0.5f), hueco.topLeft, hueco.size)
        val colores = if (a.oscuro) listOf(P.FUEGO, P.LAMPARA, P.LLAMA, mezcla(P.VIDRIO_ROSA, P.LLAMA, 0.35f), mezcla(P.VIOLETA, P.VIDRIO_ROSA, 0.35f))
        else listOf(mezcla(P.AMBAR, Color.White, 0.25f), P.AMBAR, mezcla(P.AMBAR, P.VIDRIO_ROSA, 0.6f), P.VIDRIO_ROSA, P.VIOLETA).map { a.luz(it, 0f, 0f, 0.62f) }
        drawPath(pantalla, Brush.verticalGradient(colores, y0, y1 + h * 0.012f))
        // Las hojas grabadas en el vidrio, que suben del borde.
        val grabado = if (a.oscuro) mezcla(P.VIOLETA, P.HONDO, 0.35f).copy(alpha = 0.4f) else mezcla(P.VIOLETA, P.CHAROL, 0.25f).copy(alpha = 0.45f)
        clipPath(pantalla) {
            for (m in 0 until 5) {
                val xb = x - rb * 0.8f + rb * 1.6f * m / 4f
                val inc = (m - 2) * 0.012f * h
                drawPath(TrazosNouveau.hojaLanza(Offset(xb, y1 + h * 0.004f), Offset(xb + inc * 1.6f, y1 - alto * (0.5f + 0.12f * (m % 2))), h * 0.012f, if (m < 2) -0.12f else 0.12f), grabado)
            }
            // El costado en sombra (de día) o los bordes más hondos del vidrio encendido (de noche).
            if (a.oscuro) {
                // Los costados, donde el vidrio se ve de canto, más hondos (no el borde de los pétalos).
                clipRect(x - rb * 1.1f, y0 - h * 0.01f, x + rb * 1.1f, y1 - h * 0.032f) {
                    drawPath(pantalla, mezcla(P.VIOLETA, P.HONDO, 0.4f).copy(alpha = 0.45f), style = Stroke(h * 0.018f))
                }
            } else {
                drawRect(a.sombra(0.22f), Offset(x - rb, y0), Size(rb * 0.55f, alto + h * 0.02f))
                drawPath(Path().apply { moveTo(x + rn * 0.6f, y0 + h * 0.006f); cubicTo(x + rn * 1.2f, y0 + alto * 0.45f, x + rb * 0.7f, y1 - alto * 0.4f, x + rb * 0.78f, y1 - h * 0.01f) },
                    a.claro(0.55f), style = Stroke(h * 0.007f, cap = StrokeCap.Round))
            }
        }
        drawPath(pantalla, (if (a.oscuro) P.LLAMA else P.BRONCE_OSC).copy(alpha = 0.6f), style = Stroke(0.8f * u))
        // La bombilla, que asoma por el hueco.
        val bombilla = Offset(x, y1 + h * 0.006f)
        if (a.oscuro) {
            mancha(bombilla, h * 0.05f, P.FUEGO.copy(alpha = 0.7f))
            drawCircle(Color.White, h * 0.009f, bombilla)
        } else {
            drawCircle(a.luz(Color(0xFFE9E4D6), 0f, 0f, 0.6f), h * 0.009f, bombilla)
            drawCircle(a.claro(0.8f), h * 0.003f, bombilla + Offset(h * 0.003f, -h * 0.003f))
        }
    }
}
