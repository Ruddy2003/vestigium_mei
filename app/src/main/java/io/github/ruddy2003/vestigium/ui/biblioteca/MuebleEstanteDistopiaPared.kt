package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaDistopia as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import io.github.ruddy2003.vestigium.ui.muebles.pinceladas
import kotlin.math.ceil

/**
 * La pared del sótano (la del Ideario): pintura de oficina pública a dos alturas, crema sucio arriba y verde gris abajo, con la
 * raya oliva dada a mano; la tubería del techo con sus collarines y la abrazadera oxidada, la mancha de humedad que baja de ella,
 * el tubo de la luz eléctrica con la caja de empalmes y el enchufe, la regleta del tubo fluorescente en la esquina, los
 * desconchones y la tablilla colgada de un clavo. Y el fondo de chapa de cada hueco.
 */
internal object ParedEstanteDistopia {
    /** Lo hondo: el oscuro con color en que se hunden las sombras. */
    fun honda(g: MarcoDistopia): Color = if (g.a.oscuro) g.a.hondo else mezcla(P.ACERO_OSC, P.HIERRO, 0.5f)

    /**
     * Un rectángulo pintado del [color] con la luz de cada sitio: franjas a lo alto de [paso] px, cada una con su degradado a lo
     * ancho (la luz del tubo, el halo de cada bombilla). Para lo que se pinta una sola vez (la corona guardada).
     */
    fun DrawScope.campo(g: MarcoDistopia, r: Rect, color: Color, delante: Float, mancha: Float?, paso: Float = 4f, muestras: Int = 11) {
        if (r.width < 1f || r.height < 1f) return
        val franjas = ceil(r.height / maxOf(1f, paso)).toInt().coerceIn(1, 400)
        val alto = r.height / franjas
        // Debajo, el rectángulo entero de un solo color: donde dos franjas se tocan a medio píxel no asoma el fondo.
        drawRect(g.luz(r.center.x, r.center.y, delante, mancha).c(color), r.topLeft, r.size)
        for (i in 0 until franjas) {
            val y = r.top + alto * (i + 0.5f)
            val cols = List(muestras) { j -> g.luz(r.left + r.width * j / (muestras - 1f), y, delante, mancha).c(color) }
            drawRect(Brush.horizontalGradient(cols, r.left, r.right), Offset(r.left, r.top + alto * i), Size(r.width, minOf(alto + 1.2f, r.bottom - r.top - alto * i)))
        }
    }

    // ---------------------------------------------------------------- la pared de arriba (se pinta una vez, en la corona guardada)

    fun DrawScope.paredArriba(g: MarcoDistopia) {
        val w = g.w; val u = density
        val yR = w * MuebleEstanteDistopia.RAYA
        val ch = g.ch
        conRejaDistopia(g, 0f, Rect(0f, 0f, w, ch)) { s ->
            campo(g, Rect(0f, 0f, w, yR), P.CREMA, 0f, s)
            campo(g, Rect(0f, yR, w, ch), P.VERDE, 0f, s)
        }
        // La pintura dada a brochazos y lo sucio de abajo.
        val pincel = if (g.a.oscuro) g.a.hondo else mezcla(P.CREMA, P.OXIDO, 0.35f)
        pinceladas(Rect(0f, 0f, w, yR), pincel, 6101, 30, u, 0.07f)
        pinceladas(Rect(0f, yR, w, ch), if (g.a.oscuro) g.a.hondo else P.OLIVA, 6102, 24, u, 0.08f)
        // El techo: lo de más arriba, en penumbra (la regleta tapa su luz hacia arriba).
        drawRect(Brush.verticalGradient(listOf(g.a.sombra(if (g.a.oscuro) 0.5f else 0.32f), g.a.sombra(0f)), 0f, w * 0.1f), Offset.Zero, Size(w, w * 0.1f))
        // De día, el tubo aclara la pared cerca de su regleta.
        if (g.a.tubo) mancha(Offset(w * 0.1f, w * 0.13f), w * 0.42f, P.LUZ_TUBO.copy(alpha = 0.5f))
        // De noche, el halo del flexo en la pared, encima de su pantalla (lo que se le escapa por detrás).
        if (g.a.flexo > 0f) {
            mancha(Offset(g.flexo.x - w * 0.06f, g.flexo.y - w * 0.06f), w * 0.5f, P.FLEXO.copy(alpha = 0.13f * g.a.flexo))
            mancha(Offset(g.flexo.x + w * 0.02f, g.flexo.y - w * 0.04f), w * 0.22f, P.FLEXO.copy(alpha = 0.16f * g.a.flexo))
        }
        raya(g, yR)
        humedad(g)
        listOf(Triple(0.33f, 0.24f, 0.018f), Triple(0.55f, 0.08f, 0.012f), Triple(0.03f, 0.44f, 0.015f), Triple(0.94f, 0.27f, 0.014f),
            Triple(0.69f, 0.42f, 0.012f), Triple(0.47f, 0.17f, 0.01f)).forEachIndexed { j, (x, y, r) -> desconchon(g, x, y, r, 6120 + j * 7) }
        tuberia(g)
        conducto(g)
        tuboNeumatico(g)
        regleta(g)
        tablilla(g)
        enchufe(g)
    }

    /** La raya: un filete oliva dado a mano, un poco ondulado; de noche, encendido donde lo toca el halo. */
    private fun DrawScope.raya(g: MarcoDistopia, yR: Float) {
        val w = g.w; val u = density
        val raya = Path().apply {
            moveTo(0f, yR - 0.005f * w)
            for (j in 1..10) lineTo(w * j / 10f, yR - 0.005f * w + (azar(6104, j) - 0.5f) * 1.2f * u)
            for (j in 10 downTo 0) lineTo(w * j / 10f, yR + 0.006f * w + (azar(6105, j) - 0.5f) * 1.2f * u)
            close()
        }
        drawPath(raya, Brush.horizontalGradient(List(9) { j -> g.luz(w * j / 8f, yR, 0f).c(P.OLIVA) }, 0f, w))
        drawLine(g.luz(w * 0.2f, yR, 0f).b(0.3f), Offset(0f, yR + 0.006f * w), Offset(w, yR + 0.006f * w), 0.8f * u)
    }

    /** La humedad que baja de la abrazadera de la tubería: manchas con su cerco, más hondas arriba. */
    private fun DrawScope.humedad(g: MarcoDistopia) {
        val w = g.w; val u = density
        val m = Path()
        for (j in 0 until 6) {
            val c = Offset(w * (0.815f + 0.03f * azar(6110, j) - 0.008f * j), w * (0.07f + 0.038f * j))
            val rx = w * (0.02f + 0.01f * azar(6111, j) + 0.005f * j)
            m.addPath(blandaDistopia(c, rx, rx * 1.25f, 6113 + j, 10))
        }
        val tinte = g.luz(w * 0.83f, w * 0.15f, 0f).c(mezcla(P.OXIDO, P.OLIVA, 0.3f))
        drawPath(m, tinte.copy(alpha = if (g.a.oscuro) 0.16f else 0.11f))
        translate(0f, -0.006f * w) { drawPath(m, tinte.copy(alpha = if (g.a.oscuro) 0.08f else 0.07f)) }
        drawPath(m, tinte.copy(alpha = if (g.a.oscuro) 0.1f else 0.08f), style = Stroke(1.1f * u))
    }

    /** Un desconchón: la capa vieja que asoma (más clara) con la sombra del borde de la pintura arriba a la izquierda. */
    private fun DrawScope.desconchon(g: MarcoDistopia, x: Float, y: Float, r: Float, semilla: Int) {
        val w = g.w; val u = density
        val c = Offset(x * w, y * w)
        val l = g.luz(c.x, c.y, 0f)
        val p = escamaDistopia(c, r * w, r * w * 0.7f, semilla, 11)
        drawPath(p, l.s(0.32f))
        translate(1.1f * u, 1.3f * u) { drawPath(p, l.c(mezcla(P.HUESO, P.CREMA, 0.4f))) }
        for (j in 0 until 3) drawCircle(l.c(mezcla(P.HUESO, P.CREMA, 0.4f)), (0.6f + azar(semilla, 70 + j)) * u,
            Offset((x + r * (1.3f + 0.6f * azar(semilla, 80 + j)) * (if (j == 1) -1f else 1f)) * w, (y + r * (0.8f * azar(semilla, 90 + j) - 0.3f)) * w))
    }

    /**
     * La tubería del techo, pintada como la pared, con sus collarines, la abrazadera oxidada que la cuelga del techo y el chorreón
     * de óxido; es la misma que en el Ideario baja por la izquierda.
     */
    private fun DrawScope.tuberia(g: MarcoDistopia) {
        val w = g.w; val u = density
        val y0 = 0.008f * w; val y1 = 0.054f * w
        val tubo = mezcla(P.CREMA, P.ACERO, 0.45f)
        // La sombra en la pared, debajo (de día, la luz viene de arriba a la izquierda).
        drawRect(g.a.sombra(if (g.a.oscuro) 0.4f else 0.24f), Offset(0f, y1), Size(w, 0.012f * w))
        val cols = List(9) { j -> g.luz(w * j / 8f, (y0 + y1) / 2, 0.02f).c(tubo) }
        drawRect(Brush.horizontalGradient(cols, 0f, w), Offset(0f, y0), Size(w, y1 - y0))
        // El volumen: luz arriba, sombra abajo.
        drawRect(Brush.verticalGradient(listOf(g.luz(w * 0.3f, y0, 0.02f).b(0.6f), Color.Transparent), y0, y0 + (y1 - y0) * 0.45f), Offset(0f, y0), Size(w, (y1 - y0) * 0.45f))
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, g.a.sombra(0.4f)), y0 + (y1 - y0) * 0.5f, y1), Offset(0f, y0 + (y1 - y0) * 0.5f), Size(w, (y1 - y0) * 0.5f))
        // Los collarines.
        listOf(0.18f, 0.53f, 0.91f).forEach { fx ->
            val x = fx * w
            val l = g.luz(x, (y0 + y1) / 2, 0.025f)
            drawRect(l.c(mezcla(tubo, P.ACERO_OSC, 0.25f)), Offset(x, y0 - 0.004f * w), Size(0.016f * w, y1 - y0 + 0.008f * w))
            drawRect(l.b(0.5f), Offset(x, y0 - 0.004f * w), Size(1f * u, y1 - y0 + 0.008f * w))
            drawRect(l.s(0.35f), Offset(x + 0.016f * w - 1.2f * u, y0 - 0.004f * w), Size(1.2f * u, y1 - y0 + 0.008f * w))
        }
        // La abrazadera oxidada con su varilla al techo, y el óxido que chorrea por la pared.
        val xa = 0.8f * w
        val la = g.luz(xa, y1, 0.025f)
        drawRect(la.c(P.HIERRO), Offset(xa + 0.004f * w, 0f), Size(0.006f * w, y0))
        drawRect(la.c(P.OXIDO), Offset(xa - 0.002f * w, y0 - 0.003f * w), Size(0.018f * w, y1 - y0 + 0.006f * w))
        drawRect(la.c(mezcla(P.OXIDO, P.HIERRO, 0.4f)), Offset(xa - 0.002f * w, y1 - 0.004f * w), Size(0.018f * w, 0.004f * w + 0.003f * w))
        drawRect(la.b(0.4f), Offset(xa - 0.002f * w, y0 - 0.003f * w), Size(0.018f * w, 1f * u))
        val ch = Path().apply {
            moveTo(xa + 0.006f * w, y1 + 0.003f * w)
            cubicTo(xa + 0.009f * w, y1 + 0.04f * w, xa + 0.003f * w, y1 + 0.07f * w, xa + 0.008f * w, y1 + 0.12f * w)
        }
        drawPath(ch, la.c(P.OXIDO).copy(alpha = 0.55f), style = Stroke(1.6f * u, cap = StrokeCap.Round))
    }

    /** El tubo de la luz eléctrica, fino, por debajo de la tubería, con la caja de empalmes y los tubos que bajan de ella. */
    private fun DrawScope.conducto(g: MarcoDistopia) {
        val w = g.w; val u = density
        val y = 0.078f * w; val gr = 0.009f * w
        val metal = mezcla(P.CREMA, P.ACERO, 0.6f)
        fun tramo(a: Offset, b: Offset) {
            val l = g.luz((a.x + b.x) / 2, (a.y + b.y) / 2, 0.015f)
            drawLine(l.s(0.3f), a + Offset(1.4f * u, 1.8f * u), b + Offset(1.4f * u, 1.8f * u), gr)
            drawLine(l.c(metal), a, b, gr)
            drawLine(l.b(0.45f), a - Offset(if (a.x == b.x) gr * 0.25f else 0f, if (a.y == b.y) gr * 0.25f else 0f), b - Offset(if (a.x == b.x) gr * 0.25f else 0f, if (a.y == b.y) gr * 0.25f else 0f), 0.8f * u)
        }
        tramo(Offset(0f, y), Offset(w, y))
        // La caja de empalmes, con su tapa de cuatro tornillos, y los tubos que bajan: uno al enchufe del flexo, otro a la izquierda.
        val caja = Rect(0.705f * w, 0.112f * w, 0.775f * w, 0.172f * w)
        tramo(Offset(caja.center.x, y), Offset(caja.center.x, caja.top))
        tramo(Offset(caja.right, caja.center.y), Offset(0.935f * w, caja.center.y))
        tramo(Offset(0.935f * w, caja.center.y), Offset(0.935f * w, 0.335f * w))
        // El de la lámpara de inspección, que baja por la izquierda hasta el costado.
        tramo(Offset(0.016f * w, y), Offset(0.016f * w, 0.17f * w))
        val l = g.luz(caja.center.x, caja.center.y, 0.02f)
        drawRect(l.s(0.35f), caja.topLeft + Offset(2f * u, 2.4f * u), caja.size)
        drawRect(l.c(mezcla(P.ACERO, P.CREMA, 0.25f)), caja.topLeft, caja.size)
        drawRect(l.b(0.55f), caja.topLeft, Size(caja.width, 1.2f * u))
        drawRect(l.b(0.35f), caja.topLeft, Size(1.2f * u, caja.height))
        drawRect(l.s(0.3f), Offset(caja.right - 1.4f * u, caja.top), Size(1.4f * u, caja.height))
        drawRect(l.s(0.35f), Offset(caja.left, caja.bottom - 1.4f * u), Size(caja.width, 1.4f * u))
        val tapa = caja.deflate(0.007f * w)
        drawRect(l.s(0.18f), tapa.topLeft, Size(tapa.width, 1f * u))
        drawRect(l.b(0.25f), Offset(tapa.left, tapa.bottom - 1f * u), Size(tapa.width, 1f * u))
        listOf(tapa.topLeft, Offset(tapa.right, tapa.top), Offset(tapa.left, tapa.bottom), tapa.bottomRight).forEachIndexed { i, p ->
            tornilloDistopia(p + Offset(if (p.x < tapa.center.x) 2.2f * u else -2.2f * u, if (p.y < tapa.center.y) 2.2f * u else -2.2f * u), 1.2f * u, l, 6140 + i)
        }
    }

    /**
     * La boca del correo neumático: el tubo de acero que baja del techo, sujeto a la pared con dos abrazaderas, la campana de
     * llegada con su tapa abatible y, debajo, la cesta de alambre con una cápsula que acaba de llegar (la misma de las entradas).
     */
    private fun DrawScope.tuboNeumatico(g: MarcoDistopia) {
        val w = g.w; val u = density
        val xt = 0.578f * w; val rt = 0.019f * w
        val y1 = 0.168f * w
        val l = g.luz(xt, 0.1f * w, 0.03f)
        val acero = mezcla(P.ACERO_OSC, P.ACERO, 0.35f)
        // La sombra en la pared (de día, a la derecha y abajo).
        val sombra = Path().apply {
            addRect(Rect(xt - rt, -2f, xt + rt, y1))
            addPath(poligonoDistopia(Offset(xt - rt, y1), Offset(xt + rt, y1), Offset(xt + rt * 1.7f, y1 + 0.03f * w), Offset(xt - rt * 1.7f, y1 + 0.03f * w)))
            addRect(Rect(xt - 0.05f * w, 0.215f * w, xt + 0.05f * w, 0.295f * w))
        }
        drawPath(corridoDistopia(sombra, if (g.a.oscuro) 0.004f * w else 0.014f * w, if (g.a.oscuro) 0.006f * w else 0.011f * w), g.a.sombra(if (g.a.oscuro) 0.4f else 0.24f))
        // El tubo, con su volumen de cilindro.
        val tubo = Path().apply { addRect(Rect(xt - rt, -2f, xt + rt, y1)) }
        cilindroDistopia(tubo, l.c(acero), l, -1f, 0.5f)
        // La campana de llegada: se abre hacia abajo y hacia delante; la boca, oscura, vista de sesgo.
        val campana = poligonoDistopia(Offset(xt - rt, y1 - 1f), Offset(xt + rt, y1 - 1f), Offset(xt + rt * 1.75f, y1 + 0.028f * w), Offset(xt - rt * 1.75f, y1 + 0.028f * w))
        val lc = g.luz(xt, y1 + 0.015f * w, 0.035f)
        cilindroDistopia(campana, lc.c(acero), lc, -1f, 0.45f)
        drawOval(lc.c(P.ACERO_LUZ), Offset(xt - rt * 1.85f, y1 + 0.022f * w), Size(rt * 3.7f, 0.014f * w))
        drawOval(Color(0xFF0E1012), Offset(xt - rt * 1.6f, y1 + 0.024f * w), Size(rt * 3.2f, 0.01f * w))
        // La tapa abatible, levantada contra el tubo, con su bisagra.
        val tapa = poligonoDistopia(Offset(xt + rt * 1.75f, y1 + 0.026f * w), Offset(xt + rt * 2.1f, y1 - 0.004f * w), Offset(xt + rt * 2.4f, y1 - 0.0f * w), Offset(xt + rt * 2.05f, y1 + 0.03f * w))
        drawPath(tapa, lc.c(P.ACERO))
        drawPath(bordeDistopia(tapa, 1f * u, 0f), lc.b(0.5f))
        // Las dos abrazaderas a la pared, con sus tornillos.
        listOf(0.05f, 0.125f).forEachIndexed { i, fy ->
            val y = fy * w
            val la = g.luz(xt, y, 0.035f)
            drawRect(la.c(P.HIERRO), Offset(xt - rt - 0.012f * w, y), Size(rt * 2 + 0.024f * w, 0.01f * w))
            drawRect(la.b(0.45f), Offset(xt - rt - 0.012f * w, y), Size(rt * 2 + 0.024f * w, 1f * u))
            tornilloDistopia(Offset(xt - rt - 0.006f * w, y + 0.005f * w), 1.3f * u, la, 6160 + i)
            tornilloDistopia(Offset(xt + rt + 0.006f * w, y + 0.005f * w), 1.3f * u, la, 6162 + i)
        }
        // La cesta de alambre, colgada de su escuadra, con la cápsula dentro (de esmalte rojo, tumbada y asomando).
        val c0 = Rect(xt - 0.052f * w, 0.215f * w, xt + 0.052f * w, 0.292f * w)
        val lk = g.luz(c0.center.x, c0.center.y, 0.04f)
        val cap = Offset(xt + 0.004f * w, 0.232f * w)
        rotate(-14f, cap) {
            val r = Rect(cap.x - 0.045f * w, cap.y - 0.011f * w, cap.x + 0.045f * w, cap.y + 0.011f * w)
            val cuerpo = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(r, CornerRadius(0.011f * w))) }
            drawPath(cuerpo, lk.c(mezcla(P.CARTEL, P.BURDEOS, 0.25f)))
            drawRect(lk.b(0.5f), Offset(r.left + 0.01f * w, r.top + r.height * 0.18f), Size(r.width - 0.02f * w, r.height * 0.16f))
            drawRect(lk.s(0.35f), Offset(r.left, r.bottom - r.height * 0.3f), Size(r.width, r.height * 0.3f))
            for (fx in listOf(0.2f, 0.78f)) drawRoundRect(lk.c(mezcla(P.OLIVA, P.BAQUELITA, 0.55f)), Offset(r.left + r.width * fx, r.top - 0.003f * w), Size(0.008f * w, r.height + 0.006f * w), CornerRadius(1.4f * u))
            drawRoundRect(lk.c(mezcla(P.ACERO, P.HIERRO, 0.4f)), Offset(r.left - 0.002f * w, r.top), Size(0.012f * w, r.height), CornerRadius(0.006f * w))
        }
        val malla = Path()
        val cesta = poligonoDistopia(Offset(c0.left, c0.top), Offset(c0.right, c0.top), Offset(c0.right - 0.01f * w, c0.bottom), Offset(c0.left + 0.01f * w, c0.bottom))
        for (j in 0..7) {
            val f = j / 7f
            malla.moveTo(c0.left + c0.width * f, c0.top); malla.lineTo(c0.left + 0.01f * w + (c0.width - 0.02f * w) * f, c0.bottom)
        }
        for (f in listOf(0.45f, 1f)) { malla.moveTo(c0.left + 0.01f * w * f, c0.top + c0.height * f); malla.lineTo(c0.right - 0.01f * w * f, c0.top + c0.height * f) }
        drawPath(cesta, lk.c(P.FONDO).copy(alpha = 0.25f))
        drawPath(malla, lk.c(P.HIERRO), style = Stroke(1f * u))
        drawLine(lk.c(P.HIERRO), Offset(c0.left - 1f * u, c0.top), Offset(c0.right + 1f * u, c0.top), 2f * u)
        drawLine(lk.b(0.5f), Offset(c0.left, c0.top - 0.8f * u), Offset(c0.right, c0.top - 0.8f * u), 0.8f * u)
        // La escuadra que la sujeta a la pared.
        drawLine(lk.c(P.HIERRO), Offset(c0.right - 0.004f * w, c0.top), Offset(c0.right + 0.012f * w, c0.top - 0.02f * w), 1.6f * u)
        drawLine(lk.c(P.HIERRO), Offset(c0.left + 0.004f * w, c0.top), Offset(c0.left - 0.012f * w, c0.top - 0.02f * w), 1.6f * u)
    }

    /** El enchufe de baquelita al pie del tubo, a la derecha, con la clavija del flexo metida y su cordón. */
    private fun DrawScope.enchufe(g: MarcoDistopia) {
        val w = g.w; val u = density
        val c = Offset(0.935f * w, 0.35f * w)
        val l = g.luz(c.x, c.y, 0.02f)
        val placa = Rect(c.x - 0.022f * w, c.y - 0.018f * w, c.x + 0.022f * w, c.y + 0.026f * w)
        drawRoundRect(l.s(0.4f), placa.topLeft + Offset(1.6f * u, 2f * u), placa.size, CornerRadius(2f * u))
        drawRoundRect(l.c(mezcla(P.BAQUELITA, P.MADERA, 0.25f)), placa.topLeft, placa.size, CornerRadius(2f * u))
        drawRoundRect(l.b(0.5f), placa.topLeft + Offset(1f * u, 1f * u), Size(placa.width * 0.4f, 1f * u), CornerRadius(0.5f * u))
        // La clavija, redonda, y el cordón que baja y se va hacia el flexo, por detrás de lo de arriba.
        val clavija = Offset(c.x, c.y + 0.004f * w)
        drawCircle(l.s(0.4f), 0.012f * w, clavija + Offset(1.2f * u, 1.6f * u))
        drawCircle(l.c(mezcla(P.BAQUELITA, P.ACERO_OSC, 0.3f)), 0.012f * w, clavija)
        drawCircle(l.b(0.5f), 0.004f * w, clavija - Offset(0.004f * w, 0.004f * w))
        val cordon = abiertaDistopia(listOf(clavija + Offset(0f, 0.012f * w), Offset(0.93f * w, 0.42f * w), Offset(0.9f * w, 0.47f * w), Offset(0.875f * w, 0.49f * w)))
        val lc = g.luz(0.91f * w, 0.44f * w, 0.02f)
        drawPath(corridoDistopia(cordon, 1.4f * u, 1.6f * u), lc.s(0.35f), style = Stroke(1.5f * u))
        drawPath(cordon, lc.c(P.BAQUELITA), style = Stroke(1.5f * u, cap = StrokeCap.Round))
    }

    /**
     * La regleta del tubo fluorescente en la esquina de arriba a la izquierda, cortada por el borde: la canaleta de chapa colgada de
     * dos varillas, el tubo debajo (encendido de día, apagado de noche) y la sombra que la canaleta echa en la pared por encima.
     */
    private fun DrawScope.regleta(g: MarcoDistopia) {
        val w = g.w; val u = density
        val x0 = -0.04f * w; val x1 = 0.29f * w
        val y0 = 0.104f * w; val y1 = 0.126f * w
        val l = g.luz(0.12f * w, y0, 0.03f)
        // Las varillas al techo.
        listOf(0.04f, 0.24f).forEach { fx ->
            drawLine(l.s(0.3f), Offset(fx * w + 1.6f * u, 0f), Offset(fx * w + 1.6f * u, y0), 1.4f * u)
            drawLine(l.c(P.HIERRO), Offset(fx * w, 0f), Offset(fx * w, y0), 1.4f * u)
        }
        // La sombra de la canaleta en la pared: arriba no le llega la luz del tubo.
        if (g.a.tubo) drawRect(Brush.verticalGradient(listOf(g.a.sombra(0f), g.a.sombra(0.22f)), y0 - 0.04f * w, y0), Offset(x0, y0 - 0.04f * w), Size(x1 - x0 + 0.02f * w, 0.04f * w))
        else drawRect(g.a.sombra(0.3f), Offset(x0 + 0.01f * w, y1), Size(x1 - x0, 0.012f * w))
        // La canaleta: su cara, su labio de abajo y las tapas de los extremos.
        drawRect(Brush.horizontalGradient(listOf(l.c(P.ACERO_LUZ), g.luz(x1, y0, 0.03f).c(P.ACERO)), x0, x1), Offset(x0, y0), Size(x1 - x0, y1 - y0))
        drawRect(l.b(0.6f), Offset(x0, y0), Size(x1 - x0, 1f * u))
        drawRect(l.s(0.35f), Offset(x0, y1 - 1.6f * u), Size(x1 - x0, 1.6f * u))
        drawRect(g.luz(x1, y0, 0.03f).c(P.ACERO_OSC), Offset(x1 - 0.008f * w, y0 - 0.002f * w), Size(0.01f * w, y1 - y0 + 0.012f * w))
        // El tubo: encendido, casi blanco con su halo; apagado, gris verdoso, con su brillo de vidrio.
        val t0 = y1; val t1 = y1 + 0.011f * w
        if (g.a.tubo) {
            drawRect(Brush.verticalGradient(listOf(P.LUZ_TUBO.copy(alpha = 0.6f), P.LUZ_TUBO.copy(alpha = 0f)), t1, t1 + 0.05f * w), Offset(x0, t1), Size(x1 - x0 - 0.01f * w, 0.05f * w))
            drawRoundRect(Color(0xFFFBFBEE), Offset(x0, t0), Size(x1 - x0 - 0.01f * w, t1 - t0), CornerRadius((t1 - t0) / 2))
            drawRoundRect(Color.White, Offset(x0, t0 + (t1 - t0) * 0.25f), Size(x1 - x0 - 0.02f * w, (t1 - t0) * 0.35f), CornerRadius((t1 - t0) / 4))
        } else {
            val vidrio = g.luz(0.12f * w, t0, 0.03f).c(mezcla(P.ACERO_LUZ, P.HUESO, 0.4f))
            drawRoundRect(vidrio, Offset(x0, t0), Size(x1 - x0 - 0.01f * w, t1 - t0), CornerRadius((t1 - t0) / 2))
            drawRoundRect(g.a.sombra(0.3f), Offset(x0, t0 + (t1 - t0) * 0.6f), Size(x1 - x0 - 0.01f * w, (t1 - t0) * 0.4f), CornerRadius((t1 - t0) / 4))
            drawLine(g.luz(0.12f * w, t0, 0.03f).b(0.4f), Offset(x0, t0 + (t1 - t0) * 0.3f), Offset(x1 - 0.02f * w, t0 + (t1 - t0) * 0.3f), 0.8f * u)
            // La punta ennegrecida del tubo viejo.
            drawRect(g.a.sombra(0.35f), Offset(x1 - 0.06f * w, t0), Size(0.035f * w, t1 - t0))
        }
        // El portalámparas del extremo.
        drawRect(g.luz(x1, t0, 0.03f).c(P.BAQUELITA), Offset(x1 - 0.018f * w, t0 - 0.002f * w), Size(0.012f * w, t1 - t0 + 0.004f * w))
    }

    /**
     * La tablilla con pinza colgada de un clavo a la izquierda: el tablero de aglomerado, la pinza de chapa y el impreso con sus
     * renglones a máquina que no se leen y un sello rojo desvaído; un poco torcida.
     */
    private fun DrawScope.tablilla(g: MarcoDistopia) {
        val w = g.w; val u = density
        val clavo = Offset(0.083f * w, 0.168f * w)
        val tabla = Rect(0.036f * w, 0.178f * w, 0.132f * w, 0.318f * w)
        val l = g.luz(tabla.center.x, tabla.center.y, 0.01f)
        val forma = Path().apply { addRoundRect(androidx.compose.ui.geometry.RoundRect(tabla, CornerRadius(2.4f * u))) }
        rotate(3f, clavo) {
            // La sombra en la pared, corrida (de día, abajo a la derecha; de noche, poca).
            drawPath(corridoDistopia(forma, 0.012f * w, 0.01f * w), l.s(if (g.a.oscuro) 0.4f else 0.26f))
            drawPath(forma, l.c(mezcla(P.KRAFT, P.CARTON, 0.4f)))
            drawRect(l.b(0.35f), tabla.topLeft + Offset(1f * u, 1f * u), Size(1.2f * u, tabla.height - 2f * u))
            drawRect(l.s(0.3f), Offset(tabla.right - 1.6f * u, tabla.top), Size(1.6f * u, tabla.height))
            // El impreso, un poco más chico que el tablero, con su doblez en la esquina de abajo.
            val hoja = Rect(tabla.left + 0.007f * w, tabla.top + 0.02f * w, tabla.right - 0.007f * w, tabla.bottom - 0.006f * w)
            drawRect(l.s(0.25f), hoja.topLeft + Offset(1f * u, 1.2f * u), hoja.size)
            drawRect(l.c(P.HUESO), hoja.topLeft, hoja.size)
            drawPath(renglonesDistopia(Rect(hoja.left + 0.008f * w, hoja.top + 0.02f * w, hoja.right - 0.006f * w, hoja.bottom - 0.012f * w), 0.0028f * w, 0.0085f * w, 6150, 0.01f * w),
                l.c(P.BAQUELITA).copy(alpha = 0.55f))
            drawLine(l.c(P.CARTEL).copy(alpha = 0.6f), Offset(hoja.left + 0.004f * w, hoja.top + 0.014f * w), Offset(hoja.right - 0.004f * w, hoja.top + 0.014f * w), 0.8f * u)
            drawCircle(l.c(P.CARTEL).copy(alpha = 0.35f), 0.012f * w, Offset(hoja.right - 0.024f * w, hoja.bottom - 0.03f * w), style = Stroke(1.3f * u))
            drawPath(poligonoDistopia(Offset(hoja.right - 0.016f * w, hoja.bottom), Offset(hoja.right, hoja.bottom - 0.016f * w), Offset(hoja.right, hoja.bottom)), l.s(0.3f))
            // La pinza de chapa, con su muelle y el agujero de colgar.
            val pinza = Rect(tabla.center.x - 0.026f * w, tabla.top - 0.002f * w, tabla.center.x + 0.026f * w, tabla.top + 0.022f * w)
            drawRoundRect(l.s(0.4f), pinza.topLeft + Offset(1.2f * u, 1.6f * u), pinza.size, CornerRadius(2f * u))
            drawRoundRect(l.c(P.ACERO_LUZ), pinza.topLeft, pinza.size, CornerRadius(2f * u))
            drawRect(l.b(0.7f), pinza.topLeft + Offset(2f * u, 1f * u), Size(pinza.width - 4f * u, 1f * u))
            drawRect(l.s(0.3f), Offset(pinza.left, pinza.bottom - 1.6f * u), Size(pinza.width, 1.6f * u))
            drawCircle(l.c(P.HIERRO), 0.004f * w, Offset(clavo.x, tabla.top + 0.007f * w))
        }
        // El clavo, por delante del agujero.
        drawCircle(l.c(P.HIERRO), 1.8f * u, clavo)
        drawCircle(l.b(0.6f), 0.7f * u, clavo - Offset(0.5f * u, 0.5f * u))
    }

    // ---------------------------------------------------------------- el fondo de cada hueco

    /**
     * El fondo de un hueco: la chapa de detrás, más honda, con la luz de cada sitio; la cara de dentro de los costados (la de la
     * izquierda en sombra, la de la derecha a la luz) y la de abajo de la balda de arriba; la sombra franca de la balda y del
     * costado; la junta de las dos chapas con sus remaches, el óxido que chorrea de ellos y la pintura saltada.
     */
    fun DrawScope.fondoHueco(k: Int, r: Rect, g: MarcoDistopia) {
        val w = g.w; val u = density
        if (r.width < 2f || r.height < 2f) return
        val xs = List(9) { j -> r.left + r.width * j / 8f }
        val ys = listOf(r.top + r.height * 0.2f, r.top + r.height * 0.75f)
        // La chapa: dos franjas (arriba y abajo) con su degradado a lo ancho y el paso entre ellas en un degradado vertical.
        val arriba = xs.map { g.luz(it, ys[0], 0f).c(P.FONDO) }
        val abajo = xs.map { g.luz(it, ys[1], 0f).c(P.FONDO) }
        drawRect(Brush.horizontalGradient(arriba, r.left, r.right), r.topLeft, Size(r.width, r.height * 0.5f + 1f))
        drawRect(Brush.horizontalGradient(abajo, r.left, r.right), Offset(r.left, r.top + r.height * 0.5f), Size(r.width, r.height * 0.5f))
        val s = 6300 + k * 17
        pinceladas(r, if (g.a.oscuro) g.a.hondo else P.ACERO_OSC, s, 8, u, 0.1f)
        // La junta de las dos chapas del fondo, con sus remaches y el óxido que chorrea de alguno.
        val xj = r.left + r.width * (if (k % 2 == 0) 0.36f else 0.64f)
        val lj = g.luz(xj, r.center.y, 0f)
        drawLine(lj.s(0.3f), Offset(xj, r.top), Offset(xj, r.bottom), 1.2f * u)
        drawLine(lj.b(0.2f), Offset(xj + 1.2f * u, r.top), Offset(xj + 1.2f * u, r.bottom), 0.8f * u)
        for (i in 0 until 4) {
            val c = Offset(xj + 3.2f * u, r.top + r.height * (0.28f + 0.2f * i))
            if (azar(s, 40 + i) < 0.4f) drawRect(lj.c(P.OXIDO).copy(alpha = 0.32f), Offset(c.x - 0.8f * u, c.y), Size(1.6f * u, r.height * (0.06f + 0.12f * azar(s, 50 + i))))
            drawCircle(lj.c(P.HIERRO), 1.3f * u, c)
            drawCircle(lj.b(0.45f), 0.5f * u, c - Offset(0.4f * u, 0.4f * u))
        }
        // Un sitio donde saltó la pintura del fondo.
        val cs = Offset(r.left + r.width * (0.15f + 0.7f * azar(s, 60)), r.top + r.height * (0.45f + 0.3f * azar(s, 61)))
        val lc = g.luz(cs.x, cs.y, 0f)
        saltadoDistopia(cs, w * (0.01f + 0.008f * azar(s, 62)), s + 63, lc.c(P.HIERRO), lc.b(0.4f), lc.c(P.OXIDO))
        // La cara de dentro de los costados: la de la izquierda mira a la derecha (en sombra de día), la de la derecha mira a la
        // luz. Un trapecio estrecho: más ancho delante, se cierra hacia el fondo.
        val hondo = 0.022f * w
        val izq = poligonoDistopia(Offset(r.left, r.top), Offset(r.left + hondo, r.top + hondo * 0.8f), Offset(r.left + hondo, r.bottom), Offset(r.left, r.bottom))
        val der = poligonoDistopia(Offset(r.right, r.top), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.bottom), Offset(r.right, r.bottom))
        val li = g.luz(r.left + hondo / 2, r.center.y, 0.015f)
        val ld = g.luz(r.right - hondo / 2, r.center.y, 0.015f)
        drawPath(izq, if (g.a.oscuro) mezcla(li.c(P.ACERO), g.a.hondo, 0.25f - 0.2f * li.k) else mezcla(li.c(P.ACERO_OSC), P.ACERO, 0.25f))
        drawPath(der, if (g.a.oscuro) mezcla(ld.c(P.ACERO), g.a.hondo, 0.25f - 0.2f * ld.k) else ld.c(mezcla(P.ACERO, P.ACERO_LUZ, 0.3f)))
        drawLine(ld.b(0.4f), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.bottom), 1f * u)
        // La cara de abajo de la balda de arriba (se ve un poco desde abajo).
        drawPath(poligonoDistopia(Offset(r.left, r.top), Offset(r.right, r.top), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.left + hondo, r.top + hondo * 0.8f)),
            mezcla(g.luz(r.center.x, r.top, 0.015f).c(P.ACERO_OSC), honda(g), 0.3f))
        // La sombra franca de la balda de arriba y la del costado de la izquierda en el fondo; de noche, la de la balda, más suave.
        val hs = r.height * (if (g.a.oscuro) 0.16f else 0.2f)
        val ws = if (g.a.oscuro) 0f else 0.03f * w
        val sombra = poligonoDistopia(listOf(Offset(r.left + hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.top + hondo * 0.8f), Offset(r.right - hondo, r.top + hs),
            Offset(r.left + hondo + ws + hs * 0.4f, r.top + hs), Offset(r.left + hondo + ws, r.top + hs + hs * 0.6f), Offset(r.left + hondo + ws, r.bottom), Offset(r.left + hondo, r.bottom)))
        drawPath(sombra, g.a.sombra(if (g.a.oscuro) 0.32f else 0.3f))
        drawRect(g.a.sombra(if (g.a.oscuro) 0.28f else 0.16f), Offset(r.left, r.top), Size(r.width, hs * 0.35f))
        // De noche, lo que la bombilla de cerca enciende del fondo: un charco cálido donde le llega.
        if (g.a.oscuro) {
            val kc = xs.map { g.luz(it, r.center.y, 0f).k }
            val mx = kc.indices.maxByOrNull { kc[it] } ?: 0
            if (kc[mx] > 0.15f) mancha(Offset(xs[mx], r.center.y), r.height * 0.9f, g.luz(xs[mx], r.center.y, 0f).tinte.copy(alpha = 0.1f * kc[mx]))
        }
        // El contacto de lo que pisa: una línea honda al pie del fondo.
        drawRect(g.a.sombra(0.35f), Offset(r.left, r.bottom - 2f * u), Size(r.width, 2f * u))
        // Un papel pegado con cinta en el fondo de algunos huecos, a la derecha (lo tapa lo que haya delante).
        if (k % 3 == 1) {
            val pr = Rect(r.right - 0.17f * w, r.top + r.height * 0.32f, r.right - 0.07f * w, r.top + r.height * 0.74f)
            val lp = g.luz(pr.center.x, pr.center.y, 0.005f)
            rotate(-2.5f, pr.center) {
                drawRect(lp.s(0.3f), pr.topLeft + Offset(1.2f * u, 1.6f * u), pr.size)
                drawRect(lp.c(mezcla(P.HUESO, P.CARPETA, 0.3f)), pr.topLeft, pr.size)
                drawPath(renglonesDistopia(Rect(pr.left + 0.008f * w, pr.top + 0.014f * w, pr.right - 0.006f * w, pr.bottom - 0.01f * w), 0.0026f * w, 0.008f * w, s + 70),
                    lp.c(P.BAQUELITA).copy(alpha = 0.5f))
                drawRect(lp.c(mezcla(P.CARPETA, P.HUESO, 0.4f)).copy(alpha = 0.75f), Offset(pr.center.x - 0.02f * w, pr.top - 0.008f * w), Size(0.04f * w, 0.014f * w))
            }
        }
    }
}
