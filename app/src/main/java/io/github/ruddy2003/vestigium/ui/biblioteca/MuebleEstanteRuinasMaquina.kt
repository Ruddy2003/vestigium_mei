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
import androidx.compose.ui.graphics.drawscope.clipPath
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaRuinas as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla

/**
 * La pieza que manda en lo alto de la librería: la máquina de escribir manual que alguien bajó de una oficina del edificio, puesta
 * sobre dos libros grandes tumbados, con un pliego en el rodillo (en blanco). Hierro esmaltado de negro con sus filetes niquelados,
 * cuatro filas de teclas redondas de hueso con su aro (sin letras: se borraron con los años), la barra espaciadora, la cesta de
 * los tipos abierta en abanico, el rodillo con sus perillas, la palanca del carro y los dos carretes de la cinta. El óxido en las
 * esquinas y musgo entre las teclas.
 */
internal object MaquinaRuinas {
    /** El pie de la máquina (la cara de arriba de los libros), en anchos. */
    private const val PIE = 0.428f
    private const val IZQ = 0.29f
    private const val DER = 0.615f

    /** Los dos libros grandes tumbados donde descansa la máquina, con su lomo de tela, sus nervios y las hojas onduladas. */
    fun DrawScope.libros(g: MarcoRuinas) {
        val w = g.w; val u = density
        val hacia = -1f
        val libros = listOf(
            floatArrayOf(0.275f, 0.632f, 0.449f, 0.47f, 0f),
            floatArrayOf(0.288f, 0.612f, PIE, 0.449f, 1f),
        )
        libros.forEachIndexed { i, e ->
            val r = Rect(w * e[0], w * e[2], w * e[1], w * e[3])
            val tela = if (e[4] > 0.5f) Color(0xFF7A3524) else Color(0xFF2F5640)
            conManchaRuinas(g, 0.07f, r) { s ->
                val l = g.luz(r.center.x, r.center.y, 0.07f, s)
                // La sombra que echa en lo de abajo.
                drawRect(l.s(0.4f), Offset(r.left + w * 0.012f, r.bottom - 1f * u), Size(r.width, 2.4f * u))
                // Las hojas, a la derecha del lomo (onduladas por la humedad).
                val hojas = Rect(r.left + r.width * 0.86f, r.top + r.height * 0.12f, r.right, r.bottom - r.height * 0.1f)
                drawRect(l.c(mezcla(P.PAPEL, P.KRAFT, 0.3f)), hojas.topLeft, hojas.size)
                for (k in 1..3) drawLine(l.s(0.22f), Offset(hojas.left, hojas.top + hojas.height * k / 4f), Offset(hojas.right, hojas.top + hojas.height * k / 4f + 0.6f * u), 0.6f * u)
                // El lomo de tela, redondo: claro arriba, sombra abajo, con sus nervios.
                val lomo = Rect(r.left, r.top, r.left + r.width * 0.88f, r.bottom)
                drawRoundRect(Brush.verticalGradient(listOf(mezcla(l.c(tela), Color.White, 0.18f), l.c(tela), mezcla(l.c(tela), g.a.hondo, 0.4f)), lomo.top, lomo.bottom),
                    lomo.topLeft, lomo.size, CornerRadius(lomo.height * 0.45f))
                for (k in 0 until 4) {
                    val x = lomo.left + lomo.width * (0.12f + 0.25f * k)
                    drawLine(l.s(0.35f), Offset(x, lomo.top + 1f * u), Offset(x, lomo.bottom - 1f * u), 1.4f * u)
                    drawLine(l.b(0.35f), Offset(x + 1.4f * u, lomo.top + 1f * u), Offset(x + 1.4f * u, lomo.bottom - 1f * u), 0.8f * u)
                }
                // El sol comió el color de la cabeza del lomo.
                drawRect(l.c(P.GASTADO).copy(alpha = 0.25f), Offset(lomo.left + lomo.width * 0.3f, lomo.top + 1f * u), Size(lomo.width * 0.35f, lomo.height * 0.3f))
                modeladoBorde(r, l, hacia)
            }
        }
    }

    private fun DrawScope.modeladoBorde(r: Rect, l: LuzRuinas, hacia: Float) {
        if (l.k > 0.08f) drawLine(l.filo(0.7f), Offset(r.left, r.top + 1f), Offset(r.right, r.top + 1f), 1.2f * density)
    }

    /** La máquina de escribir, de pie sobre los libros. */
    fun DrawScope.maquina(g: MarcoRuinas) {
        val w = g.w
        val zona = Rect(w * (IZQ - 0.04f), w * 0.22f, w * (DER + 0.04f), w * PIE)
        // La sombra de la máquina en la pared: de día, corrida a la derecha y abajo; de noche, lejos de la vela (a la derecha y
        // arriba). Primero la del cuerpo y el carro; la del pliego la pone él.
        val silueta = Path().apply {
            addRect(Rect(w * (IZQ + 0.012f), w * 0.348f, w * (DER - 0.012f), w * PIE))
            addRect(Rect(w * (IZQ - 0.047f), w * 0.317f, w * (DER + 0.047f), w * 0.349f))
        }
        val corre = if (g.a.oscuro && !g.a.noche) Offset(w * 0.04f, -w * 0.014f) else Offset(w * 0.03f, w * 0.014f)
        drawPath(corridoRuinas(silueta, corre.x, corre.y), g.a.sombra(if (g.a.oscuro) 0.32f else 0.24f))
        conManchaRuinas(g, 0.08f, zona) { s -> cuerpo(g, s) }
        pliego(g)
    }

    private fun DrawScope.cuerpo(g: MarcoRuinas, s: Float?) {
        val w = g.w; val u = density
        val l = g.luz(w * 0.45f, w * 0.38f, 0.08f, s)
        val hacia = if (g.a.oscuro && !g.a.noche) -1f else -1f
        val pie = w * PIE
        val x0 = w * IZQ; val x1 = w * DER
        // La sombra que echa en los libros y en la pared.
        drawRect(l.s(0.45f), Offset(x0 + w * 0.02f, pie - 2.4f * u), Size(x1 - x0, 3.6f * u))
        // El cuerpo: un trapecio con la cara de delante inclinada (donde van las teclas) y el lomo de arriba.
        val arriba = w * 0.348f
        val cuerpo = poligonoRuinas(Offset(x0, pie), Offset(x1, pie), Offset(x1 - w * 0.012f, arriba), Offset(x0 + w * 0.012f, arriba))
        val negro = l.c(P.HIERRO)
        drawPath(cuerpo, Brush.horizontalGradient(listOf(mezcla(negro, P.HIERRO_LUZ, 0.5f), negro, mezcla(negro, g.a.hondo, 0.5f)), x0, x1))
        // El filete niquelado del canto de abajo y del de arriba.
        drawRect(l.c(P.NIQUEL), Offset(x0, pie - 3.2f * u), Size(x1 - x0, 1.6f * u))
        drawLine(l.b(0.6f), Offset(x0 + w * 0.012f, arriba + 0.8f * u), Offset(x1 - w * 0.012f, arriba + 0.8f * u), 1.2f * u)
        // La cesta de los tipos: el hueco semicircular arriba, con los tipos en abanico.
        val cx = (x0 + x1) / 2
        val cesta = Rect(cx - w * 0.07f, arriba - w * 0.02f, cx + w * 0.07f, arriba + w * 0.045f)
        clipPath(cuerpo) {
            // Solo se ve la boca oscura de la cesta y la guía de la cinta, sin el abanico de los tipos.
            drawArc(mezcla(negro, g.a.hondo, 0.6f), 0f, 180f, true, cesta.topLeft, cesta.size)
            drawRect(l.c(P.NIQUEL).copy(alpha = 0.6f), Offset(cx - w * 0.006f, arriba - 1f * u), Size(w * 0.012f, w * 0.008f))
        }
        // Las teclas: cuatro filas escalonadas de botones de hueso con su aro niquelado, vistos un poco desde arriba (óvalos), sin
        // letras; entre ellos se ve el hierro negro. La barra espaciadora delante.
        val filas = 4
        val ry = w * 0.0042f
        for (f in 0 until filas) {
            val y = arriba + w * (0.028f + 0.0118f * f)
            val n = 11 - (if (f >= 2) f - 1 else 0)
            val margen = w * (0.03f + 0.006f * f)
            val paso = (x1 - x0 - 2 * margen) / n
            val rx = paso * 0.3f
            for (k in 0 until n) {
                val x = x0 + margen + paso * (k + 0.5f) + (if (f % 2 == 1) paso * 0.22f else 0f)
                if (x > x1 - margen * 0.7f) continue
                drawOval(l.s(0.5f), Offset(x - rx, y - ry + ry * 0.9f), Size(rx * 2.1f, ry * 2.3f))
                drawOval(l.c(P.NIQUEL), Offset(x - rx, y - ry), Size(rx * 2, ry * 2))
                val tecla = l.c(mezcla(P.TECLA, P.GASTADO, 0.25f * azar(1401, f * 12 + k)))
                drawOval(tecla, Offset(x - rx * 0.74f, y - ry * 0.78f), Size(rx * 1.48f, ry * 1.4f))
                drawOval(l.b(0.45f), Offset(x - rx * 0.6f, y - ry * 0.7f), Size(rx * 0.7f, ry * 0.5f))
            }
        }
        val barra = Rect(x0 + w * 0.08f, pie - w * 0.013f, x1 - w * 0.08f, pie - w * 0.0075f)
        drawRoundRect(l.s(0.4f), barra.topLeft + Offset(1.4f * u, 1.4f * u), barra.size, CornerRadius(barra.height / 2))
        drawRoundRect(l.c(P.NIQUEL), barra.topLeft, barra.size, CornerRadius(barra.height / 2))
        drawLine(l.b(0.7f), Offset(barra.left + 2f * u, barra.top + 0.8f * u), Offset(barra.right - 2f * u, barra.top + 0.8f * u), 0.8f * u)
        // El óxido de las esquinas y el musgo que se metió entre las teclas de abajo.
        mancha(Offset(x0 + w * 0.012f, pie - w * 0.01f), w * 0.018f, l.c(P.BARRO).copy(alpha = 0.6f))
        mancha(Offset(x1 - w * 0.02f, arriba + w * 0.01f), w * 0.014f, l.c(P.BARRO).copy(alpha = 0.5f))
        for (k in 0 until 6) drawCircle(l.c(if (k % 2 == 0) P.MUSGO else P.MUSGO_LUZ), w * 0.0035f, Offset(x0 + w * (0.04f + 0.012f * k), pie - w * 0.006f - w * 0.002f * (k % 3)))
        // Los dos carretes de la cinta, a los lados de arriba.
        for (cxr in listOf(x0 + w * 0.045f, x1 - w * 0.045f)) {
            val c = Offset(cxr, arriba - w * 0.004f)
            drawOval(l.s(0.4f), Offset(c.x - w * 0.026f, c.y - w * 0.004f), Size(w * 0.054f, w * 0.014f))
            drawOval(l.c(P.NIQUEL), Offset(c.x - w * 0.026f, c.y - w * 0.01f), Size(w * 0.052f, w * 0.016f))
            drawOval(l.c(Color(0xFF5A1E1A)), Offset(c.x - w * 0.018f, c.y - w * 0.008f), Size(w * 0.036f, w * 0.011f))
            drawCircle(l.c(P.HIERRO_LUZ), w * 0.003f, Offset(c.x, c.y - w * 0.003f))
        }
        // El carro: el rodillo de goma negra a lo largo, las perillas en los extremos, la barra del papel y la palanca.
        val ryo = w * 0.333f
        val rod = Rect(x0 - w * 0.025f, ryo - w * 0.012f, x1 + w * 0.025f, ryo + w * 0.012f)
        drawRect(l.s(0.4f), Offset(rod.left + w * 0.012f, rod.bottom), Size(rod.width, w * 0.008f))
        drawRect(Brush.horizontalGradient(listOf(mezcla(negro, P.HIERRO_LUZ, 0.3f), negro), rod.left, rod.right), Offset(rod.left, rod.top - w * 0.006f), Size(rod.width, w * 0.008f))
        drawRoundRect(Brush.verticalGradient(listOf(mezcla(l.c(P.HIERRO), P.HIERRO_LUZ, 0.7f), l.c(P.HIERRO), mezcla(l.c(P.HIERRO), g.a.hondo, 0.4f)), rod.top, rod.bottom),
            rod.topLeft, rod.size, CornerRadius(rod.height / 2))
        drawLine(l.b(0.55f), Offset(rod.left + w * 0.02f, rod.top + rod.height * 0.25f), Offset(rod.right - w * 0.02f, rod.top + rod.height * 0.25f), 1.2f * u)
        for ((px, lado) in listOf(rod.left to -1f, rod.right to 1f)) {
            val per = Rect(px + (if (lado < 0f) -w * 0.022f else 0f), ryo - w * 0.019f, px + (if (lado < 0f) 0f else w * 0.022f), ryo + w * 0.019f)
            drawRoundRect(l.s(0.4f), per.topLeft + Offset(2f * u, 2f * u), per.size, CornerRadius(per.width * 0.3f))
            drawRoundRect(Brush.horizontalGradient(listOf(l.c(P.HIERRO_LUZ), l.c(P.HIERRO)), per.left, per.right), per.topLeft, per.size, CornerRadius(per.width * 0.3f))
            for (k in 1..4) drawLine(l.b(0.25f), Offset(per.left + 1f * u, per.top + per.height * k / 5f), Offset(per.right - 1f * u, per.top + per.height * k / 5f), 0.6f * u)
        }
        val bp = ryo + w * 0.008f
        drawLine(l.c(P.NIQUEL), Offset(x0 + w * 0.02f, bp), Offset(x1 - w * 0.02f, bp), 1.3f * u, StrokeCap.Round)
        for (fx in listOf(0.38f, 0.62f)) drawCircle(l.c(P.HIERRO), w * 0.005f, Offset(x0 + (x1 - x0) * fx, bp))
        // La palanca del carro: un brazo niquelado que sale a la izquierda y sube.
        val palanca = abiertaRuinas(listOf(Offset(rod.left + w * 0.01f, ryo - w * 0.01f), Offset(rod.left - w * 0.012f, ryo - w * 0.03f), Offset(rod.left - w * 0.03f, ryo - w * 0.034f)))
        drawPath(corridoRuinas(palanca, w * 0.006f, w * 0.006f), l.s(0.35f), style = Stroke(2.4f * u, cap = StrokeCap.Round))
        drawPath(palanca, l.c(P.NIQUEL), style = Stroke(2.2f * u, cap = StrokeCap.Round))
        drawCircle(l.c(P.HIERRO), w * 0.007f, Offset(rod.left - w * 0.03f, ryo - w * 0.034f))
        if (l.k > 0.06f) drawLine(l.filo(0.8f), Offset(rod.left, rod.top + 1f), Offset(rod.right, rod.top + 1f), 1.2f * u)
        // El modelado del lado contrario a la luz y el brillo en el canto que la mira.
        drawPath(bordeRuinas(cuerpo, hacia * w * 0.012f, 0f), l.s(0.3f))
    }

    /** El pliego en el rodillo: papel en blanco, un poco amarillo, combado hacia atrás, con su sombra en la pared. */
    private fun DrawScope.pliego(g: MarcoRuinas) {
        val w = g.w; val u = density
        val ry = w * 0.333f
        val x0 = w * 0.405f; val x1 = w * 0.5f
        val arriba = w * 0.284f
        val hoja = Path().apply {
            moveTo(x0, ry - w * 0.01f); lineTo(x0 - w * 0.004f, arriba + w * 0.01f)
            quadraticTo(x0 + (x1 - x0) * 0.5f, arriba - w * 0.006f, x1 + w * 0.006f, arriba + w * 0.004f)
            lineTo(x1, ry - w * 0.01f); close()
        }
        conManchaRuinas(g, 0.1f, hoja.getBounds()) { s ->
            val l = g.luz((x0 + x1) / 2, (arriba + ry) / 2, 0.1f, s)
            val hacia = if (g.a.oscuro && !g.a.noche) -1f else -1f
            // La sombra en la pared, corrida lejos de la luz.
            drawPath(corridoRuinas(hoja, -hacia * w * 0.022f, w * 0.012f), l.s(0.3f))
            drawPath(hoja, l.c(mezcla(P.PAPEL, P.KRAFT, 0.12f)))
            // El papel combado: la sombra a lo largo de un lado, la luz en el otro y la esquina de arriba vuelta.
            drawPath(bordeRuinas(hoja, hacia * w * 0.03f, 0f), l.s(0.12f))
            drawPath(bordeRuinas(hoja, -hacia * w * 0.01f, 0f), l.b(0.4f))
            val esquina = poligonoRuinas(Offset(x1 + w * 0.006f, arriba + w * 0.004f), Offset(x1 - w * 0.018f, arriba + w * 0.002f), Offset(x1 - w * 0.004f, arriba + w * 0.022f))
            drawPath(esquina, l.c(mezcla(P.PAPEL, P.KRAFT, 0.4f)))
            if (l.k > 0.06f) drawPath(bordeRuinas(hoja, -hacia * 1.2f * u, 0f), l.filo(0.5f))
        }
    }
}
