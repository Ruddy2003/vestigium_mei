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
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.cos
import kotlin.math.sin

/**
 * El pie del andamio: el arranque de la pared detrás de las patas, el suelo de tierra pisada con su enlosado de lajas y cantos,
 * la ceniza y las manchas de ocre; los pies de los postes acuñados con piedras; el hogar de lajas puestas de canto (no el cerco
 * de cantos del Ideario) con la leña, la piedra de asar inclinada, sus brasas y su llama; el tocón del tallador; los cantos de río
 * para calentar el agua; el haz de astillas de pino para las teas y el haz de enebro, cortado por el borde.
 */
internal object PieEstanteCueva {

    /** La pared detrás de las patas y el suelo, con su enlosado, la ceniza y el charco del hogar. */
    fun DrawScope.suelo(g: MarcoCueva) {
        val w = g.w; val h = g.h; val u = density
        val sp = g.suelo
        // El arranque de la pared, entre la última repisa y el suelo: la caliza en la sombra del zarzo.
        val xs = listOf(0f, w * 0.33f, w * 0.67f, w)
        drawRect(Brush.horizontalGradient(xs.map { g.luz(it, (g.fondo + sp) / 2).c(P.CALIZA_HONDA) }, 0f, w), Offset(0f, g.fondo - 1f), Size(w, sp - g.fondo + 2f))
        drawRect(g.luz(w * 0.5f, g.fondo).s(0.4f), Offset(0f, g.fondo), Size(w, (sp - g.fondo) * 0.45f))
        // El suelo: tierra pisada, más clara hacia la boca, con el charco del hogar.
        val cols = xs.map { x -> g.luz(x, (sp + h) / 2).c(P.TIERRA) }
        drawRect(Brush.horizontalGradient(cols, 0f, w), Offset(0f, sp), Size(w, h - sp))
        drawRect(Brush.verticalGradient(listOf(g.luz(w * 0.5f, sp).s(0.42f), Color.Transparent), sp, sp + (h - sp) * 0.3f), Offset(0f, sp), Size(w, (h - sp) * 0.3f))
        // La junta de la pared con el suelo: una franja de cascajo en sombra.
        drawRect(g.luz(w * 0.5f, sp).s(0.5f), Offset(0f, sp - 1f), Size(w, 2.2f * u))
        // La ceniza alrededor del hogar y las manchas de ocre pisado.
        val ho = g.hogar
        drawOval(g.luz(ho.x, ho.y).c(mezcla(P.CENIZA, P.TIERRA, 0.35f)).copy(alpha = 0.75f), Offset(ho.x - w * 0.2f, ho.y - w * 0.045f), Size(w * 0.4f, w * 0.1f))
        listOf(0.62f to 0.75f, 0.12f to 0.55f, 0.84f to 0.4f).forEachIndexed { i, (fx, fy) ->
            val c = Offset(w * fx, sp + (h - sp) * fy)
            drawPath(piedraCueva(c, w * 0.05f, w * 0.012f, 6100 + i, 9, 0.35f), g.luz(c).c(P.OCRE_ROJO).copy(alpha = 0.32f))
        }
        // El enlosado: lajas planas y cantos hundidos en la tierra, más chatos cuanto más atrás. Todos en tres caminos.
        val caras = Path(); val sombras = Path(); val filos = Path()
        LOSAS.forEachIndexed { i, (fx, fy, r) ->
            val c = Offset(w * fx, sp + (h - sp) * fy)
            val ry = w * r * (0.22f + 0.2f * fy)
            val f = piedraCueva(c, w * r, ry, 6200 + i, 8, 0.2f)
            sombras.addPath(f, Offset(w * 0.004f, ry * 0.35f))
            caras.addPath(f)
            filos.addPath(f, Offset(-w * 0.002f, -ry * 0.18f))
        }
        val l = g.luz(w * 0.5f, (sp + h) / 2)
        drawPath(sombras, l.s(0.45f))
        drawPath(caras, Brush.horizontalGradient(xs.map { x -> g.luz(x, (sp + h) / 2, 0.02f).c(mezcla(P.CALIZA, P.TIERRA, 0.3f)) }, 0f, w))
        drawPath(filos, Brush.horizontalGradient(xs.map { x -> g.luz(x, (sp + h) / 2, 0.02f).b(0.45f) }, 0f, w))
        // De noche, el charco del hogar en el suelo.
        if (g.a.hogar > 0f) {
            mancha(ho, w * 0.32f, mezcla(P.FUEGO, P.BRASA, 0.3f).copy(alpha = 0.45f * g.a.hogar))
            mancha(ho, w * 0.15f, P.LLAMA.copy(alpha = 0.3f * g.a.hogar))
        }
    }

    /** Las losas del suelo: (x, y en fracción de la franja del suelo, radio en anchos). */
    private val LOSAS = listOf(Triple(0.05f, 0.35f, 0.05f), Triple(0.17f, 0.82f, 0.07f), Triple(0.47f, 0.38f, 0.045f), Triple(0.56f, 0.9f, 0.06f),
        Triple(0.71f, 0.3f, 0.05f), Triple(0.79f, 0.78f, 0.065f), Triple(0.93f, 0.28f, 0.04f), Triple(0.38f, 0.95f, 0.05f), Triple(0.97f, 0.88f, 0.05f))

    /** Lo que hay al pie, de atrás adelante: las cuñas de los postes, las astillas, el hogar, el tocón del tallador, el enebro y los cantos. */
    fun DrawScope.alPie(g: MarcoCueva) {
        val w = g.w; val h = g.h
        cunas(g, g.posteI, 0)
        cunas(g, g.posteD, 1)
        astillas(g, Offset(w * 0.1f, h - w * 0.035f))
        hogar(g)
        tocon(g, Offset(w * 0.62f, h - w * 0.05f))
        enebro(g, Offset(w * 0.86f, h - w * 0.045f))
        cantos(g, Offset(w * 0.82f, h - w * 0.025f))
    }

    /** Las piedras que acuñan el pie de un poste: tres o cuatro cantos apretados contra él. */
    private fun DrawScope.cunas(g: MarcoCueva, cx: Float, lado: Int) {
        val w = g.w
        val y = g.suelo + w * 0.02f
        val r = g.radioPoste
        listOf(-1.3f to 0.5f, 1.25f to 0.55f, -0.5f to 0.85f, 0.6f to 0.95f).forEachIndexed { i, (fx, fr) ->
            val c = Offset(cx + r * fx, y + r * 0.25f * (i % 2))
            val l = g.luz(c, 0.03f)
            val f = piedraCueva(c, r * fr, r * fr * 0.62f, 6300 + lado * 10 + i, 8, 0.2f)
            drawPath(corridoCueva(f, r * 0.15f, r * 0.2f), l.s(0.45f))
            bultoRapidoCueva(f, l.c(mezcla(P.CALIZA, if (i % 2 == 0) P.CALIZA_FRIA else P.TIERRA, 0.35f)), l, haciaPie(g, c), 1f, 0.42f)
        }
    }

    /** De dónde le llega la luz a algo del pie: de día, de la boca (izquierda); de noche, del hogar. */
    private fun haciaPie(g: MarcoCueva, c: Offset): Float = if (g.a.oscuro || g.a.noche) (if (c.x < g.hogar.x) 1f else -1f) else -1f

    /**
     * El hogar de lajas puestas de canto (no el cerco de cantos del Ideario): las de atrás, más altas; dentro, la ceniza, la leña
     * que arde cruzada y las brasas; la piedra de asar apoyada atrás; delante, las lajas más bajas. De día, una llama mediana y el
     * humo; de noche, en llamas; en los Sueños, en brasas.
     */
    private fun DrawScope.hogar(g: MarcoCueva) {
        val w = g.w; val u = density
        val c = g.hogar
        val rx = w * 0.15f; val ry = w * 0.045f
        val ld = g.luz(c, 0.03f)
        val ardiente = g.a.hogar > 0f
        drawOval(ld.c(mezcla(P.CENIZA, P.TIERRA, 0.2f)), Offset(c.x - rx * 0.9f, c.y - ry * 0.85f), Size(rx * 1.8f, ry * 1.7f))
        drawOval(P.HOLLIN.copy(alpha = 0.8f), Offset(c.x - rx * 0.62f, c.y - ry * 0.55f), Size(rx * 1.24f, ry * 1.1f))
        // La piedra de asar: una laja ancha apoyada en el cerco de atrás, inclinada sobre las brasas, tiznada.
        val la = g.luz(c.x + rx * 0.3f, c.y - ry, 0.03f)
        val asar = cerradaCueva(listOf(Offset(c.x - rx * 0.05f, c.y - ry * 0.7f), Offset(c.x + rx * 0.1f, c.y - ry * 2.6f), Offset(c.x + rx * 0.62f, c.y - ry * 2.9f),
            Offset(c.x + rx * 0.78f, c.y - ry * 1.0f), Offset(c.x + rx * 0.35f, c.y - ry * 0.45f)))
        drawPath(corridoCueva(asar, w * 0.008f, w * 0.008f), la.s(0.4f))
        bultoRapidoCueva(asar, la.c(mezcla(P.CALIZA, P.CALIZA_FRIA, 0.4f)), la, -1f, 1f, 0.45f)
        // El hollín de abajo y, de noche, la cara de abajo encendida por las brasas: un degradado en la misma forma.
        val ba = asar.getBounds()
        drawPath(asar, Brush.verticalGradient(listOf(Color.Transparent, P.HOLLIN.copy(alpha = 0.4f), if (ardiente) P.BRASA.copy(alpha = 0.5f * g.a.hogar) else P.HOLLIN.copy(alpha = 0.5f)), ba.top, ba.bottom))
        lajasHogar(g, c, rx, ry, true, ardiente)
        // La leña que arde: tres palos cruzados, negros, con las puntas encendidas.
        val palos = listOf(Offset(-0.62f, 0.25f) to Offset(0.32f, -0.45f), Offset(0.6f, 0.3f) to Offset(-0.28f, -0.4f), Offset(-0.5f, -0.1f) to Offset(0.55f, -0.05f))
        palos.forEachIndexed { i, (a0, b0) ->
            val a = Offset(c.x + rx * a0.x, c.y + ry * a0.y); val b = Offset(c.x + rx * b0.x, c.y + ry * b0.y)
            drawLine(ld.c(Color(0xFF2A1C16)), a, b, w * 0.014f, StrokeCap.Round)
            drawLine(ld.c(Color(0xFF5A3A28)), a + Offset(0f, -w * 0.003f), b + Offset(0f, -w * 0.003f), w * 0.004f, StrokeCap.Round)
            drawCircle(if (ardiente || i == 0) P.BRASA else ld.c(P.CARBON), w * 0.007f, b)
            drawCircle(if (ardiente) P.LLAMA else P.FUEGO.copy(alpha = 0.7f), w * 0.0035f, b)
        }
        for (k in 0 until 12) {
            val q = Offset(c.x + rx * (-0.5f + azar(6400, k)), c.y + ry * (-0.3f + 0.6f * azar(6401, k)))
            val rr = (1.4f + 2f * azar(6402, k)) * u
            drawCircle(if (ardiente) (if (k % 3 == 0) P.LLAMA else P.BRASA) else ld.c(if (k % 3 == 0) P.BRASA else P.CARBON), rr, q)
        }
        // La llama: de día mediana y con humo; de noche alta; en los Sueños, solo brasas.
        when {
            g.a.oscuro && !g.a.noche -> {
                mancha(Offset(c.x, c.y - ry * 2f), w * 0.2f, P.LLAMA.copy(alpha = 0.42f))
                llamaCueva(Offset(c.x - rx * 0.05f, c.y - ry * 0.2f), w * 0.15f, w * 0.04f, 11)
                llamaCueva(Offset(c.x - rx * 0.4f, c.y), w * 0.09f, w * 0.026f, 12)
                llamaCueva(Offset(c.x + rx * 0.3f, c.y), w * 0.07f, w * 0.02f, 15)
                drawCircle(P.LLAMA.copy(alpha = 0.85f), 1.3f * u, Offset(c.x + rx * 0.1f, c.y - w * 0.2f))
                drawCircle(P.FUEGO.copy(alpha = 0.7f), 1f * u, Offset(c.x - rx * 0.25f, c.y - w * 0.24f))
            }
            g.a.noche -> {
                mancha(c, w * 0.12f, P.BRASA.copy(alpha = 0.5f * g.a.hogar))
                llamaCueva(Offset(c.x - rx * 0.1f, c.y), w * 0.035f, w * 0.018f, 13)
            }
            else -> {
                llamaCueva(Offset(c.x - rx * 0.05f, c.y - ry * 0.2f), w * 0.07f, w * 0.022f, 14)
                llamaCueva(Offset(c.x - rx * 0.32f, c.y), w * 0.04f, w * 0.014f, 16)
                val humo = abiertaCueva(listOf(Offset(c.x, c.y - w * 0.07f), Offset(c.x + rx * 0.1f, c.y - w * 0.12f), Offset(c.x - rx * 0.12f, c.y - w * 0.17f), Offset(c.x + rx * 0.02f, c.y - w * 0.22f)))
                drawPath(humo, P.CENIZA.copy(alpha = 0.22f), style = Stroke(w * 0.012f, cap = StrokeCap.Round))
                drawPath(humo, P.CENIZA.copy(alpha = 0.14f), style = Stroke(w * 0.026f, cap = StrokeCap.Round))
            }
        }
        lajasHogar(g, c, rx, ry, false, ardiente)
    }

    /**
     * Las lajas del cerco del hogar, puestas de canto ([atras]: la mitad de atrás, más altas; si no, la de delante, más bajas): cada
     * una con su cara partida, sus esquinas apenas romas, su canto de arriba más claro y el hollín del lado del fuego.
     */
    private fun DrawScope.lajasHogar(g: MarcoCueva, c: Offset, rx: Float, ry: Float, atras: Boolean, ardiente: Boolean) {
        val w = g.w
        val lajas = (0 until 9).map { k ->
            val a = 3.1416f + 6.2832f * k / 9f + 0.2f
            Offset(c.x + rx * cos(a), c.y + ry * sin(a))
        }.filter { (it.y < c.y) == atras }.sortedBy { it.y }
        lajas.forEachIndexed { i, q ->
            val semilla = (q.x * 3f).toInt()
            val alto = w * (if (atras) 0.06f else 0.036f) * (0.85f + 0.35f * azar(6410, semilla))
            val ancho = w * (0.05f + 0.02f * azar(6411, semilla))
            val l = g.luz(q, 0.03f)
            val izq = q.y - alto * (0.7f + 0.3f * azar(6413, semilla)); val der = q.y - alto * (0.62f + 0.38f * azar(6415, semilla))
            val x0 = q.x - ancho * 0.5f; val x1 = q.x + ancho * 0.5f
            // El canto de arriba, partido y en pendiente, con una esquina desportillada.
            val mella = ancho * (0.12f + 0.12f * azar(6416, semilla))
            val f = Path().apply {
                moveTo(x0, q.y + w * 0.004f); lineTo(x0 + ancho * 0.02f, izq + mella * 0.6f)
                lineTo(x0 + mella, izq); lineTo(x1 - ancho * 0.04f, der)
                lineTo(x1, der + mella * 0.5f); lineTo(x1, q.y + w * 0.004f); close()
            }
            drawPath(corridoCueva(f, w * 0.006f, w * 0.006f), l.s(0.42f))
            val hacia = if (ardiente) (if (q.x < c.x) 1f else -1f) else -1f
            bultoRapidoCueva(f, l.c(mezcla(P.CALIZA, P.CALIZA_FRIA, 0.25f + 0.35f * azar(6412, semilla))), l, hacia, 1f, 0.4f)
            // El hollín del lado del fuego y, de noche, la cara de las de atrás encendida desde abajo.
            if (ardiente && atras) drawPath(f, Brush.verticalGradient(listOf(Color.Transparent, P.BRASA.copy(alpha = 0.6f * g.a.hogar)), izq, q.y))
            else drawPath(f, Brush.horizontalGradient(if (q.x < c.x) listOf(Color.Transparent, P.HOLLIN.copy(alpha = 0.35f)) else listOf(P.HOLLIN.copy(alpha = 0.35f), Color.Transparent), x0, x1))
            // El canto de arriba: una franja clara (la cara de arriba de la laja, vista un poco desde arriba).
            val canto = Path().apply {
                moveTo(x0 + mella, izq); lineTo(x1 - ancho * 0.04f, der)
                lineTo(x1 - ancho * 0.08f, der - w * 0.006f); lineTo(x0 + mella + ancho * 0.03f, izq - w * 0.006f); close()
            }
            drawPath(canto, if (ardiente) mezcla(l.c(P.CALIZA), P.FUEGO, 0.35f * g.a.hogar) else l.c(mezcla(P.CALIZA, P.CALIZA_SOL, 0.45f)))
        }
    }

    /**
     * El tocón del tallador: un tronco corto de pie que sirve de yunque, con la cara de arriba gastada y sus anillos, la corteza
     * rojiza a los lados; encima, el núcleo de sílex, unas lascas y el percutor de asta.
     */
    private fun DrawScope.tocon(g: MarcoCueva, pie: Offset) {
        val w = g.w; val u = density
        val rx = w * 0.085f; val ry = w * 0.024f
        val alto = w * 0.085f
        val top = pie.y - alto
        val l = g.luz(pie.x, top, 0.03f)
        val hacia = haciaPie(g, pie)
        drawOval(l.s(0.5f), Offset(pie.x - rx * 1.1f + w * 0.012f, pie.y - ry * 0.6f), Size(rx * 2.4f, ry * 1.6f))
        val lado = Path().apply {
            moveTo(pie.x - rx, top); lineTo(pie.x - rx * 1.04f, pie.y - ry * 0.2f)
            quadraticTo(pie.x, pie.y + ry * 1.2f, pie.x + rx * 1.04f, pie.y - ry * 0.2f)
            lineTo(pie.x + rx, top); close()
        }
        bultoRapidoCueva(lado, l.c(P.CORTEZA), l, hacia, 1.2f, 0.32f)
        val grietas = Path()
        for (k in 0 until 7) {
            val x = pie.x - rx * 0.85f + rx * 1.7f * k / 6f
            grietas.moveTo(x, top + ry * 0.8f); grietas.lineTo(x + rx * 0.04f * sin(k * 2f), pie.y - ry * 0.1f + ry * 0.6f * (1f - ((x - pie.x) / rx) * ((x - pie.x) / rx)))
        }
        drawPath(grietas, l.c(P.CORTEZA_OSC), style = Stroke(1.2f * u, cap = StrokeCap.Round))
        // La cara de arriba: la madera cortada, con sus anillos y la grieta del secado.
        val cara = Rect(pie.x - rx, top - ry, pie.x + rx, top + ry)
        drawOval(l.c(P.PINO_LUZ), cara.topLeft, cara.size)
        for (k in 1..3) drawOval(l.c(P.PINO_OSC).copy(alpha = 0.35f), Offset(pie.x - rx * k / 4f, top - ry * k / 4f), Size(rx * k / 2f, ry * k / 2f), style = Stroke(0.8f * u))
        drawLine(l.c(P.PINO_OSC).copy(alpha = 0.6f), Offset(pie.x, top), Offset(pie.x + rx * 0.7f, top + ry * 0.3f), 1f * u)
        drawOval(l.c(P.CORTEZA_OSC), cara.topLeft, cara.size, style = Stroke(1.4f * u))
        drawArc(l.b(0.55f), 180f, 120f, false, cara.topLeft + Offset(1f * u, 1f * u), Size(cara.width - 2f * u, cara.height - 2f * u), style = Stroke(1.2f * u))
        // Lo de encima: el núcleo, las lascas y el percutor.
        val nc = Offset(pie.x - rx * 0.35f, top - w * 0.012f)
        val nucleo = poligonoCueva(nc, w * 0.02f, w * 0.016f, 6800, 7)
        drawOval(l.s(0.45f), Offset(nc.x - w * 0.016f, top - w * 0.004f), Size(w * 0.04f, w * 0.01f))
        bultoRapidoCueva(nucleo, l.c(P.SILEX), l, hacia, 1.3f, 0.5f)
        drawOval(l.c(P.CAOLIN), Offset(nc.x - w * 0.016f, nc.y - w * 0.02f), Size(w * 0.026f, w * 0.01f))
        listOf(Triple(0.25f, -0.1f, 6801), Triple(0.5f, 0.25f, 6802), Triple(-0.7f, 0.3f, 6803)).forEach { (fx, fy, sm) ->
            val p = Offset(pie.x + rx * fx, top + ry * fy)
            val lasca = poligonoCueva(p, w * 0.011f, w * 0.005f, sm, 5)
            drawPath(lasca, l.c(mezcla(P.SILEX, P.CAOLIN, 0.35f)))
            drawPath(corridoCueva(lasca, 0f, -w * 0.0015f), l.b(0.4f), style = Stroke(0.6f * u))
        }
        val pa = Offset(pie.x + rx * 0.1f, top + ry * 0.2f); val pb = Offset(pie.x + rx * 0.78f, top - ry * 0.25f)
        drawLine(l.s(0.45f), pa + Offset(w * 0.004f, w * 0.004f), pb + Offset(w * 0.004f, w * 0.004f), w * 0.016f, StrokeCap.Round)
        drawLine(l.c(mezcla(P.HUESO_OSC, P.PIEL_OSC, 0.3f)), pa, pb, w * 0.016f, StrokeCap.Round)
        drawLine(l.c(P.HUESO), pa + Offset(0f, -w * 0.003f), pb + Offset(0f, -w * 0.003f), w * 0.006f, StrokeCap.Round)
        drawCircle(l.c(mezcla(P.HUESO_OSC, P.PIEL_OSC, 0.4f)), w * 0.011f, pb)
    }

    /** El montón de cantos de río para calentar el agua: redondos, grises azulados, alguno tiznado o rajado por el fuego. */
    private fun DrawScope.cantos(g: MarcoCueva, c: Offset) {
        val w = g.w; val u = density
        val sitios = listOf(Triple(-0.05f, 0f, 0.025f), Triple(0f, 0.004f, 0.028f), Triple(0.052f, 0f, 0.024f), Triple(-0.026f, -0.035f, 0.023f),
            Triple(0.026f, -0.034f, 0.025f))
        val l0 = g.luz(c, 0.03f)
        drawOval(l0.s(0.45f), Offset(c.x - w * 0.1f, c.y + w * 0.012f), Size(w * 0.22f, w * 0.03f))
        sitios.forEachIndexed { i, (dx, dy, r) ->
            val q = c + Offset(w * dx, w * dy)
            val l = g.luz(q, 0.03f)
            val f = piedraCueva(q, w * r * 1.12f, w * r * 0.82f, 6500 + i, 9, 0.1f)
            drawPath(corridoCueva(f, w * 0.005f, w * 0.006f), l.s(0.38f))
            bultoRapidoCueva(f, l.c(mezcla(P.CALIZA_FRIA, P.SILEX, 0.25f + 0.3f * azar(6501, i))), l, haciaPie(g, q), 1.1f, 0.5f)
            if (i == 4) drawLine(l.s(0.6f), q + Offset(-w * 0.012f, -w * 0.01f), q + Offset(w * 0.006f, w * 0.014f), 1f * u)
            drawCircle(l.b(0.55f), w * r * 0.22f, q + Offset(-w * r * 0.4f, -w * r * 0.35f))
        }
    }

    /** El haz de astillas de pino resinoso, para las teas: largas, rojizas por dentro, atadas y apoyadas contra el poste. */
    private fun DrawScope.astillas(g: MarcoCueva, pie: Offset) {
        val w = g.w; val u = density
        val l = g.luz(pie.x, pie.y - w * 0.06f, 0.03f)
        val tope = Offset(pie.x - w * 0.045f, pie.y - w * 0.2f)
        drawLine(l.s(0.4f), pie + Offset(w * 0.02f, w * 0.004f), tope + Offset(w * 0.03f, w * 0.02f), w * 0.055f, StrokeCap.Round)
        for (k in 0 until 9) {
            val d = (k - 4) * w * 0.006f
            val a = pie + Offset(d, 0f); val b = tope + Offset(d * 1.6f + w * 0.004f * sin(k * 2.3f), -w * 0.01f * azar(6600, k))
            val c = if (k % 3 == 0) mezcla(P.CORTEZA, P.FUEGO, 0.25f) else mezcla(P.PINO, P.CORTEZA, 0.35f + 0.3f * azar(6601, k))
            drawLine(l.c(c), a, b, w * 0.0075f, StrokeCap.Butt)
            drawLine(l.b(0.4f), a + Offset(-w * 0.002f, 0f), b + Offset(-w * 0.002f, 0f), 0.7f * u)
        }
        listOf(0.35f, 0.7f).forEach { t ->
            val p = pie + (tope - pie) * t
            drawLine(l.s(0.45f), p + Offset(-w * 0.03f, w * 0.003f), p + Offset(w * 0.032f, w * 0.006f), 2.6f * u, StrokeCap.Round)
            drawLine(l.c(P.CUERO), p + Offset(-w * 0.031f, 0f), p + Offset(w * 0.031f, w * 0.002f), 2.2f * u, StrokeCap.Round)
        }
    }

    /** El haz de enebro (para la cama y para avivar el fuego): ramas verdes azuladas con sus bayas, atadas, cortado por el borde. */
    private fun DrawScope.enebro(g: MarcoCueva, c: Offset) {
        val w = g.w; val u = density
        val l = g.luz(c, 0.03f)
        clipRect(0f, 0f, w, g.h) {
            drawOval(l.s(0.45f), Offset(c.x - w * 0.13f, c.y - w * 0.002f), Size(w * 0.3f, w * 0.03f))
            val ramas = Path(); val hojas = Path(); val bayas = Path()
            for (k in 0 until 14) {
                val a = Offset(c.x - w * 0.14f + k * w * 0.014f, c.y - w * 0.006f * (k % 3))
                val b = Offset(c.x + w * 0.2f + w * 0.012f * sin(k * 1.3f), c.y - w * (0.07f + 0.08f * azar(6700, k)))
                ramas.moveTo(a.x, a.y); ramas.quadraticTo((a.x + b.x) / 2, a.y - w * 0.03f, b.x, b.y)
                for (j in 1..5) {
                    val t = 0.2f + 0.15f * j
                    val p = Offset(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t - w * 0.03f * 4f * t * (1f - t))
                    hojas.addOval(Rect(p.x - w * 0.016f, p.y - w * 0.009f, p.x + w * 0.008f, p.y + w * 0.005f))
                    if ((j + k) % 4 == 0) bayas.addOval(Rect(p.x - w * 0.0035f, p.y - w * 0.0035f, p.x + w * 0.0035f, p.y + w * 0.0035f))
                }
            }
            drawPath(ramas, l.c(P.CORTEZA_OSC), style = Stroke(1.4f * u, cap = StrokeCap.Round))
            drawPath(corridoCueva(hojas, w * 0.003f, w * 0.004f), l.c(mezcla(P.ENEBRO, P.HOLLIN, 0.5f)))
            drawPath(hojas, l.c(P.ENEBRO))
            drawPath(corridoCueva(hojas, -w * 0.002f, -w * 0.003f), l.c(mezcla(P.ENEBRO, Color(0xFFA9C29A), 0.35f)).copy(alpha = 0.6f))
            drawPath(bayas, l.c(Color(0xFF5B6E9A)))
            drawPath(corridoCueva(bayas, -w * 0.001f, -w * 0.001f), l.b(0.5f))
            val atado = Offset(c.x - w * 0.07f, c.y - w * 0.018f)
            drawLine(l.c(P.CUERO), atado + Offset(0f, -w * 0.02f), atado + Offset(w * 0.006f, w * 0.014f), 2.4f * u, StrokeCap.Round)
        }
    }
}
