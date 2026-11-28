package io.github.ruddy2003.vestigium.ui.biblioteca

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaUrss as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.ceil
import kotlin.math.floor

/**
 * El pie de la estantería: el zócalo pintado de negro, con los golpes de las escobas que dejan ver el roble; el parquet en espiga del
 * Ideario, con el paso gastado y el brillo de la cera; la pila de periódicos atada con bramante, cortada por la izquierda; y el
 * receptor de radio de válvulas encima de un taburete, cortado por la derecha, con su dial (de noche, encendido: la luz del pie) y
 * su cable por el suelo.
 */
internal object PieEstanteUrss {
    /** El zócalo, en anchos desde el pie de la última tabla. */
    private const val ZOCALO = 0.062f

    fun DrawScope.pie(g: MarcoUrss) {
        val w = g.w; val h = g.h
        val y0 = g.fondo
        if (h - y0 < 4f) return
        val suelo = y0 + w * ZOCALO
        // Debajo, la fila de la junta del zócalo con la última tabla, de píxel entero.
        drawRect(g.luz(w * 0.5f, y0, 0.03f).c(P.NEGRO), Offset(0f, floor(y0)), Size(w, 2f))
        parquet(g, suelo)
        zocalo(g, y0, suelo)
        cable(g, suelo)
        periodicos(g, suelo)
        scale(1.12f, 1.12f, Offset(w * 0.86f, h - (h - suelo) * 0.1f)) { receptor(g, suelo) }
    }

    // ---------------------------------------------------------------- el parquet

    private fun DrawScope.parquet(g: MarcoUrss, suelo: Float) {
        val w = g.w; val h = g.h; val u = density
        val alto = h - suelo
        if (alto < 1f) return
        // El roble con la luz de cada sitio: franjas a lo alto, cada una con su degradado a lo ancho.
        val franjas = ceil(alto / maxOf(1f, w * 0.03f)).toInt().coerceIn(1, 40)
        val paso = alto / franjas
        for (i in 0 until franjas) {
            val y = suelo + paso * (i + 0.5f)
            val cols = List(9) { j -> g.luz(w * j / 8f, y, 0.04f).c(P.PARQUET) }
            drawRect(Brush.horizontalGradient(cols, 0f, w), Offset(0f, suelo + paso * i), Size(w, minOf(paso + 1f, h - suelo - paso * i)))
        }
        // Las tablillas en espiga, en su baldosa, y el paso gastado con el brillo de la cera.
        drawRect(TexturasUrss.parquet(u, w / 12.4f), Offset(0f, suelo), Size(w, alto), alpha = if (g.a.oscuro) 0.75f else 1f)
        val lc = g.luz(w * 0.42f, h, 0.04f)
        drawOval(lc.b(0.14f), Offset(w * 0.26f, h - alto * 0.5f), Size(w * 0.36f, alto * 0.34f))
        drawRect(lc.b(0.1f), Offset(w * 0.3f, h - alto * 0.22f), Size(w * 0.2f, 1.4f * u))
        drawOval(g.a.sombra(0.08f), Offset(w * 0.3f, h - alto * 0.38f), Size(w * 0.34f, alto * 0.24f))
        // La sombra de la estantería en el suelo, junto al zócalo.
        drawRect(g.a.sombra(if (g.a.oscuro) 0.5f else 0.4f), Offset(0f, suelo), Size(w, 2.4f * u))
        drawRect(g.a.sombra(if (g.a.oscuro) 0.28f else 0.18f), Offset(0f, suelo + 2.4f * u), Size(w, w * 0.016f))
        // De noche, el charco ámbar del dial en el suelo, delante del receptor.
        if (g.a.dial > 0f) mancha(Offset(g.dial.x - w * 0.06f, h - alto * 0.15f), w * 0.3f, P.DIAL.copy(alpha = 0.16f * g.a.dial))
    }

    // ---------------------------------------------------------------- el zócalo

    private fun DrawScope.zocalo(g: MarcoUrss, y0: Float, suelo: Float) {
        val w = g.w; val u = density
        val xs = List(9) { w * it / 8f }
        val ym = (y0 + suelo) / 2
        drawRect(Brush.horizontalGradient(xs.map { g.luz(it, ym, 0.035f).c(mezcla(P.NEGRO, P.ROBLE_OSC, 0.2f)) }, 0f, w), Offset(0f, y0), Size(w, suelo - y0))
        drawRect(TexturasUrss.vetaH(u), Offset(0f, y0), Size(w, suelo - y0), alpha = 0.3f)
        // El canto de arriba con su luz y el retranqueo de abajo, en sombra.
        drawRect(Brush.horizontalGradient(xs.map { g.luz(it, y0, 0.035f).b(0.5f) }, 0f, w), Offset(0f, y0 + 1f * u), Size(w, 1.2f * u))
        drawRect(g.a.sombra(0.4f), Offset(0f, suelo - (suelo - y0) * 0.22f), Size(w, (suelo - y0) * 0.22f))
        // Los golpes de las escobas y los zapatos: la pintura negra saltada deja ver el roble.
        for (k in 0 until 7) {
            val x = w * (0.05f + 0.9f * azar(8501, k))
            val y = y0 + (suelo - y0) * (0.45f + 0.35f * azar(8502, k))
            val l = g.luz(x, y, 0.035f)
            drawPath(escamaUrss(Offset(x, y), w * (0.012f + 0.022f * azar(8503, k)), w * (0.0018f + 0.002f * azar(8504, k)), 8505 + k, 9),
                l.c(mezcla(P.ROBLE, P.ACERO_OSC, 0.35f)).copy(alpha = 0.7f))
        }
        // De noche, el filo de las bombillas cercanas en el canto.
        if (g.a.oscuro) {
            val filos = xs.map { g.luz(it, y0, 0.035f).filo(0.8f) }
            if (filos.any { it.alpha > 0.04f }) drawRect(Brush.horizontalGradient(filos, 0f, w), Offset(0f, y0), Size(w, 1.8f * u))
        }
    }

    /** El cable del receptor, por el suelo, desde el taburete hacia la pared de la izquierda, por delante del zócalo. */
    private fun DrawScope.cable(g: MarcoUrss, suelo: Float) {
        val w = g.w; val h = g.h; val u = density
        val alto = h - suelo
        val p = abiertaUrss(listOf(Offset(w * 0.76f, h - alto * 0.42f), Offset(w * 0.64f, h - alto * 0.3f), Offset(w * 0.5f, h - alto * 0.62f), Offset(w * 0.36f, suelo + alto * 0.14f), Offset(w * 0.24f, suelo + alto * 0.08f)))
        val l = g.luz(w * 0.5f, h - alto * 0.4f, 0.04f)
        drawPath(corridoUrss(p, 1.2f * u, 1.6f * u), l.s(0.35f), style = Stroke(2.2f * u))
        drawPath(p, l.c(mezcla(P.PARQUET_OSC, P.NEGRO, 0.45f)), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawPath(p, l.b(0.3f), style = Stroke(0.6f * u))
    }

    // ---------------------------------------------------------------- la pila de periódicos

    /**
     * La pila de periódicos de la sala de lectura, atada con bramante para el papel viejo, cortada por el borde izquierdo: la cara
     * de arriba (el periódico de encima, con su cabecera de bloques, una foto en gris y sus columnas, que no se leen), el canto
     * de delante con los pliegues de cada periódico, y dos vueltas de bramante paralelas con su nudo.
     */
    private fun DrawScope.periodicos(g: MarcoUrss, suelo: Float) {
        val w = g.w; val h = g.h; val u = density
        val alto = h - suelo
        val pie = h - alto * 0.12f
        val x0 = -w * 0.04f; val x1 = w * 0.25f
        val ya = pie - w * 0.15f
        val fx = w * 0.03f; val fy = w * 0.026f
        val l = g.luz(w * 0.1f, pie - w * 0.06f, 0.06f)
        // Su sombra en el suelo y en el zócalo.
        drawPath(poligonoUrss(listOf(Offset(x1, pie), Offset(x1 + fx, pie - fy), Offset(x1 + fx + w * 0.05f, pie - fy + w * 0.014f), Offset(x1 + w * 0.04f, pie + w * 0.008f), Offset(x0, pie + w * 0.008f))), l.s(0.4f))
        // La cara de arriba (el periódico de encima) y el costado.
        val arriba = poligonoUrss(Offset(x0, ya), Offset(x0 + fx, ya - fy), Offset(x1 + fx, ya - fy), Offset(x1, ya))
        drawPath(arriba, l.c(mezcla(P.PERIODICO, Color.White, 0.15f)))
        clipPath(arriba) {
            val tinta = l.c(P.NEGRO).copy(alpha = 0.75f)
            drawPath(bloquesUrss(Offset(x0 + fx * 0.8f + w * 0.03f, ya - fy * 0.78f), w * 0.13f, w * 0.006f, 8601), tinta)
            drawRect(l.c(mezcla(P.PERIODICO, P.NEGRO, 0.45f)), Offset(x0 + fx * 0.5f + w * 0.03f, ya - fy * 0.5f), Size(w * 0.05f, fy * 0.3f))
            drawPath(bloquesUrss(Offset(x0 + fx * 0.5f + w * 0.1f, ya - fy * 0.48f), w * 0.09f, w * 0.0025f, 8602, 3, 2f), tinta.copy(alpha = 0.5f))
            drawRect(l.c(P.ROJO).copy(alpha = 0.8f), Offset(x0 + fx * 0.85f + w * 0.03f, ya - fy * 0.95f), Size(w * 0.03f, w * 0.004f))
        }
        val costado = poligonoUrss(Offset(x1, ya), Offset(x1 + fx, ya - fy), Offset(x1 + fx, pie - fy), Offset(x1, pie))
        drawPath(costado, l.c(mezcla(P.PERIODICO, P.PARQUET_OSC, 0.45f)))
        // El canto de delante: los pliegues de los periódicos, uno sobre otro, algunos más salidos.
        val frente = Rect(x0, ya, x1, pie)
        drawRect(l.c(P.PERIODICO), frente.topLeft, frente.size)
        clipRect(frente.left, frente.top, frente.right, frente.bottom) {
            var y = ya; var k = 0
            while (y < pie && k < 40) {
                val grueso = w * (0.006f + 0.006f * azar(8610, k))
                val sale = w * 0.008f * azar(8611, k)
                drawRect(l.c(mezcla(P.PERIODICO, if (k % 4 == 0) P.CARTULINA_OSC else P.CARTON, 0.2f + 0.2f * azar(8612, k))), Offset(x0, y), Size(x1 - x0 + sale, grueso))
                drawLine(l.s(0.35f), Offset(x0, y + grueso), Offset(x1 + sale, y + grueso), 0.7f * u)
                drawLine(l.b(0.35f), Offset(x0, y + 0.6f * u), Offset(x1, y + 0.6f * u), 0.5f * u)
                y += grueso; k++
            }
            drawRect(Brush.horizontalGradient(listOf(Color.Transparent, l.s(0.25f)), x0, x1), frente.topLeft, frente.size)
        }
        // El bramante: dos vueltas paralelas por delante que siguen por la cara de arriba hacia atrás, y el nudo.
        val cuerda = l.c(P.BRAMANTE)
        listOf(0.42f, 0.78f).forEach { f ->
            val x = x0 + (x1 - x0) * f
            drawLine(l.s(0.35f), Offset(x + 1.2f * u, ya), Offset(x + 1.2f * u, pie), 1.4f * u)
            drawLine(cuerda, Offset(x, ya), Offset(x, pie), 1.6f * u)
            drawLine(cuerda, Offset(x, ya), Offset(x + fx, ya - fy), 1.4f * u)
        }
        val nudo = Offset(x0 + (x1 - x0) * 0.78f, ya + (pie - ya) * 0.3f)
        drawCircle(cuerda, 2.4f * u, nudo)
        drawLine(cuerda, nudo, nudo + Offset(w * 0.016f, w * 0.022f), 1.2f * u, StrokeCap.Round)
        drawLine(cuerda, nudo, nudo + Offset(-w * 0.008f, w * 0.026f), 1.2f * u, StrokeCap.Round)
        if (l.k > 0.05f) drawLine(l.filo(0.8f), Offset(x1, ya), Offset(x1, pie), 1.2f * u)
    }

    // ---------------------------------------------------------------- el receptor en su taburete

    /**
     * El taburete de madera, con su asiento de canto grueso, las cuatro patas (las de atrás más altas en el cuadro) y sus
     * travesaños, cortado por la derecha; encima, el receptor de válvulas: la caja de madera barnizada con las esquinas de arriba
     * redondeadas, la tela del altavoz tras su calado de tres listones, la escala del dial con sus rayas y su aguja roja (de noche,
     * encendida: ámbar), y los dos botones de baquelita (uno delante, otro en el costado).
     */
    private fun DrawScope.receptor(g: MarcoUrss, suelo: Float) {
        val w = g.w; val h = g.h; val u = density
        val alto = h - suelo
        val pieD = h - alto * 0.1f
        val asiento = pieD - w * 0.12f
        val x0 = w * 0.68f; val x1 = w * 1.02f
        val l = g.luz(w * 0.82f, asiento, 0.06f)
        val madera = mezcla(P.ROBLE, P.TE, 0.2f)
        // La sombra del taburete en el suelo.
        drawOval(l.s(0.4f), Offset(x0 - w * 0.01f, pieD - w * 0.012f), Size(x1 - x0 + w * 0.06f, w * 0.03f))
        // Las patas de atrás, en sombra, y los travesaños.
        listOf(x0 + w * 0.05f, x1 - w * 0.02f).forEach { x ->
            drawRect(l.c(mezcla(madera, P.NEGRO, 0.4f)), Offset(x, asiento), Size(w * 0.018f, pieD - asiento - w * 0.03f))
        }
        drawRect(l.c(mezcla(madera, P.NEGRO, 0.3f)), Offset(x0 + w * 0.02f, asiento + (pieD - asiento) * 0.55f), Size(x1 - x0, w * 0.012f))
        listOf(x0 + w * 0.012f, x1 - w * 0.05f).forEach { x ->
            val r = Rect(x, asiento, x + w * 0.022f, pieD)
            drawRect(l.c(madera), r.topLeft, r.size)
            drawRect(l.b(0.3f), r.topLeft, Size(1.2f * u, r.height))
            drawRect(l.s(0.35f), Offset(r.right - r.width * 0.35f, r.top), Size(r.width * 0.35f, r.height))
            drawOval(l.s(0.45f), Offset(r.left - w * 0.004f, pieD - w * 0.005f), Size(r.width + w * 0.016f, w * 0.01f))
        }
        // El asiento: la cara de arriba (vista desde arriba) y el canto.
        val canto = Rect(x0, asiento - w * 0.016f, x1, asiento + w * 0.004f)
        drawPath(poligonoUrss(Offset(x0, canto.top), Offset(x0 + w * 0.02f, canto.top - w * 0.03f), Offset(x1, canto.top - w * 0.03f), Offset(x1, canto.top)), l.c(mezcla(madera, P.ROBLE_LUZ, 0.3f)))
        drawRect(l.c(madera), canto.topLeft, canto.size)
        drawRect(TexturasUrss.vetaH(u), canto.topLeft, canto.size, alpha = 0.7f)
        drawRect(l.b(0.5f), canto.topLeft, Size(canto.width, 1.2f * u))
        drawRect(l.s(0.35f), Offset(canto.left, canto.bottom - 1.4f * u), Size(canto.width, 1.4f * u))
        // El receptor.
        val r = Rect(w * 0.705f, canto.top - w * 0.015f - w * 0.132f, w * 0.965f, canto.top - w * 0.015f)
        val lr = g.luz(r.center, 0.06f)
        val hacia = g.hacia(r.center.x, r.center.y)
        val caja = Path().apply { addRoundRect(RoundRect(r, CornerRadius(w * 0.028f, w * 0.028f))) }
        drawPath(corridoUrss(caja, -hacia * w * 0.012f, w * 0.006f), lr.s(0.3f))
        planosUrss(caja, lr.c(mezcla(P.ROBLE, P.ROBLE_OSC, 0.25f)), lr, hacia, 0.18f, 0.24f)
        clipPath(caja) {
            drawRect(TexturasUrss.vetaH(u), r.topLeft, r.size, alpha = 0.6f)
            drawRect(lr.b(0.35f), Offset(r.left + w * 0.02f, r.top + 1f * u), Size(r.width - w * 0.04f, 1.4f * u))
        }
        // La tela del altavoz, tras el calado de tres listones.
        val tela = Rect(r.left + r.width * 0.07f, r.top + r.height * 0.16f, r.left + r.width * 0.56f, r.bottom - r.height * 0.14f)
        drawRoundRect(lr.c(mezcla(P.BRAMANTE, P.CARTON, 0.4f)), tela.topLeft, tela.size, CornerRadius(w * 0.01f))
        clipRect(tela.left, tela.top, tela.right, tela.bottom) {
            var yy = tela.top; var j = 0
            while (yy < tela.bottom && j < 60) { drawLine(lr.s(0.1f), Offset(tela.left, yy), Offset(tela.right, yy), 0.5f * u); yy += 1.6f * u; j++ }
            drawRect(lr.s(0.25f), tela.topLeft, Size(tela.width, tela.height * 0.14f))
        }
        for (j in 1..3) {
            val x = tela.left + tela.width * j / 4f
            drawLine(lr.s(0.3f), Offset(x + 1.2f * u, tela.top), Offset(x + 1.2f * u, tela.bottom), w * 0.008f)
            drawLine(lr.c(mezcla(P.ROBLE, P.ROBLE_OSC, 0.4f)), Offset(x, tela.top), Offset(x, tela.bottom), w * 0.008f)
            drawLine(lr.b(0.3f), Offset(x - w * 0.003f, tela.top), Offset(x - w * 0.003f, tela.bottom), 0.7f * u)
        }
        drawRoundRect(lr.s(0.4f), tela.topLeft, tela.size, CornerRadius(w * 0.01f), style = Stroke(1.2f * u))
        // La escala del dial, a la derecha, con sus rayas, su aguja y el marco de latón; de noche, encendida.
        val dial = Rect(r.left + r.width * 0.62f, r.top + r.height * 0.16f, r.right - r.width * 0.07f, r.top + r.height * 0.56f)
        val enc = g.a.dial > 0f
        if (enc) {
            mancha(dial.center, w * 0.08f, P.DIAL.copy(alpha = 0.5f * g.a.dial))
            drawRect(mezcla(P.DIAL, Color(0xFFFFF1D0), 0.35f), dial.topLeft, dial.size)
        } else drawRect(lr.c(mezcla(P.CARTULINA, P.LATON, 0.25f)), dial.topLeft, dial.size)
        val rayas = Path()
        for (j in 0 until 9) {
            val x = dial.left + dial.width * (0.1f + 0.1f * j)
            rayas.moveTo(x, dial.top + dial.height * (if (j % 2 == 0) 0.25f else 0.4f)); rayas.lineTo(x, dial.bottom - dial.height * 0.12f)
        }
        drawPath(rayas, (if (enc) mezcla(P.NEGRO, P.DIAL, 0.25f) else lr.c(P.NEGRO)).copy(alpha = 0.7f), style = Stroke(0.7f * u))
        drawLine(if (enc) P.ROJO else lr.c(P.ROJO), Offset(dial.left + dial.width * 0.64f, dial.top + 1f * u), Offset(dial.left + dial.width * 0.64f, dial.bottom - 1f * u), 1.2f * u)
        drawRect(lr.c(P.LATON), dial.topLeft, dial.size, style = Stroke(1.4f * u))
        drawRect(lr.b(0.5f), Offset(dial.left, dial.top), Size(dial.width * 0.4f, 1f * u))
        // Los dos botones de baquelita: uno bajo el dial y otro en el canto de la derecha.
        val b1 = Offset(dial.center.x, r.top + r.height * 0.75f)
        drawCircle(lr.s(0.4f), w * 0.013f, b1 + Offset(w * 0.003f, w * 0.003f))
        drawCircle(lr.c(P.NEGRO), w * 0.013f, b1)
        drawCircle(lr.b(0.5f), w * 0.005f, b1 - Offset(w * 0.004f, w * 0.004f))
        drawRoundRect(lr.c(P.NEGRO), Offset(r.right - w * 0.002f, r.top + r.height * 0.6f), Size(w * 0.01f, w * 0.024f), CornerRadius(w * 0.003f))
        // De noche, la luz del dial en lo que tiene cerca: el canto de la caja y el asiento.
        if (enc) {
            drawLine(P.DIAL.copy(alpha = 0.5f * g.a.dial), Offset(r.left + w * 0.02f, r.bottom), Offset(r.right - w * 0.02f, r.bottom), 1.4f * u)
            mancha(Offset(dial.center.x, canto.top), w * 0.1f, P.DIAL.copy(alpha = 0.16f * g.a.dial))
        }
    }
}
