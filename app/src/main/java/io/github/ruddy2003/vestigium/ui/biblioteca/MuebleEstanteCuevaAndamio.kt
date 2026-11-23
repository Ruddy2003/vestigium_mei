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
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import io.github.ruddy2003.vestigium.ui.biblioteca.PaletaCueva as P
import io.github.ruddy2003.vestigium.ui.ideario.azar
import io.github.ruddy2003.vestigium.ui.ideario.mancha
import io.github.ruddy2003.vestigium.ui.ideario.mezcla
import kotlin.math.floor
import kotlin.math.sin

/**
 * El andamio de varas: los dos postes de pino descortezado con su horqueta (con restos de corteza rojiza, nudos y muñones de
 * rama), la viga de arriba en las horquetas, y cada repisa: el travesaño redondo atado delante de los postes con correas de cuero
 * crudo y, encima, las varas puestas de delante atrás, cuyos cabos cortados son el canto de la repisa. El rótulo del mes,
 * una tira de cuero crudo atada al travesaño. La tea de pino resinoso atada al poste derecho.
 */
internal object AndamioEstanteCueva {
    /** Cuánto están por delante de la pared los postes y los cantos, en anchos (para la mancha de la boca). */
    private const val DELANTE = 0.08f

    /** Debajo de la corona guardada: la fila de la junta, de píxel entero, del color de la sombra del primer hueco. */
    fun DrawScope.arranque(g: MarcoCueva) {
        val y0 = floor(g.ch) - 1f
        drawRect(g.luz(g.w * 0.5f, g.ch).c(mezcla(P.CALIZA_HONDA, P.HOLLIN, 0.5f)), Offset(0f, y0), Size(g.w, g.ch + 3f - y0))
    }

    // ---------------------------------------------------------------- lo de arriba (en la corona guardada)

    /** Las horquetas y lo alto de los postes, la viga en las horquetas, y la plataforma de arriba con su faldón de flecos. */
    fun DrawScope.arriba(g: MarcoCueva) {
        val w = g.w
        conManchaCueva(g, DELANTE) { s ->
            listOf(g.posteI to 0, g.posteD to 1).forEach { (cx, lado) -> horqueta(g, cx, lado, s) }
            poste(g, g.posteI, g.viga, g.ch, 0, s)
            poste(g, g.posteD, g.viga, g.ch, 1, s)
            viga(g, s)
        }
        // La plataforma de arriba: su cara (donde pisa lo de arriba), el canto de cabos, el travesaño y el faldón.
        val ap = g.apoyo
        conManchaCueva(g, 0.05f) { s -> caraZarzo(g, Rect(g.x0, ap - w * 0.014f, g.x1, ap), s) }
        conManchaCueva(g, DELANTE) { s ->
            cabos(g, Rect(g.x0 - w * 0.01f, ap, g.x1 + w * 0.01f, ap + w * 0.022f), 7, s)
            tronco(g, w * 0.002f, w * 0.998f, ap + w * 0.017f, ap + w * 0.046f, 77, s)
            faldon(g, ap + w * 0.04f, g.ch, s)
            atadura(g, Offset(g.posteI, ap + w * 0.03f), w * 0.05f, w * 0.04f, 61, s)
            atadura(g, Offset(g.posteD, ap + w * 0.03f), w * 0.05f, w * 0.04f, 62, s)
        }
    }

    /** La horqueta de un poste: el tronco que se abre en dos brazos por encima de la viga. */
    private fun DrawScope.horqueta(g: MarcoCueva, cx: Float, lado: Int, s: Float?) {
        val w = g.w; val r = g.radioPoste
        val cruz = g.viga + w * 0.01f
        val l = g.luz(cx, cruz, DELANTE, s)
        val hacia = haciaPoste(g, lado)
        listOf(-1f, 1f).forEach { dir ->
            val brazo = Path().apply {
                moveTo(cx - r * 0.9f, cruz + r * 0.6f)
                cubicTo(cx - r * 0.4f + dir * r * 0.4f, cruz - r * 0.2f, cx + dir * r * 1.2f, cruz - w * 0.04f, cx + dir * r * 1.6f - r * 0.32f, cruz - w * 0.085f)
                lineTo(cx + dir * r * 1.6f + r * 0.32f, cruz - w * 0.08f)
                cubicTo(cx + dir * r * 1.3f + r * 0.3f, cruz - w * 0.035f, cx + r * 0.6f + dir * r * 0.3f, cruz - r * 0.1f, cx + r * 0.9f, cruz + r * 0.6f)
                close()
            }
            drawPath(corridoCueva(brazo, w * 0.008f, w * 0.006f), l.s(0.28f))
            bultoCueva(brazo, l.c(P.PINO), l, hacia, 1f, 0.4f)
            // La punta cortada a hacha: la cara clara, inclinada.
            val punta = Offset(cx + dir * r * 1.6f, cruz - w * 0.083f)
            drawOval(l.c(P.PINO_LUZ), Offset(punta.x - r * 0.34f, punta.y - r * 0.12f), Size(r * 0.68f, r * 0.24f))
            drawOval(l.c(P.PINO_OSC).copy(alpha = 0.7f), Offset(punta.x - r * 0.12f, punta.y - r * 0.05f), Size(r * 0.24f, r * 0.1f))
        }
    }

    /** La viga de arriba: un tronco que descansa en las horquetas, con su corteza a parches y su atadura en cada horqueta. */
    private fun DrawScope.viga(g: MarcoCueva, s: Float?) {
        val w = g.w
        tronco(g, -w * 0.01f, w * 1.01f, g.viga - w * 0.019f, g.viga + w * 0.019f, 51, s)
        atadura(g, Offset(g.posteI, g.viga), w * 0.055f, w * 0.05f, 52, s)
        atadura(g, Offset(g.posteD, g.viga), w * 0.055f, w * 0.05f, 53, s)
    }

    // ---------------------------------------------------------------- los postes (en cada dibujo, de la corona al suelo)

    /** De qué lado le llega la luz a un poste: de día y en los Sueños, de la boca (izquierda); de noche, del fuego. */
    private fun haciaPoste(g: MarcoCueva, lado: Int): Float = if (g.a.oscuro && !g.a.noche) (if (lado == 0) 1f else -1f) else -1f

    fun DrawScope.postes(g: MarcoCueva, d: DisposicionEstante) {
        val y0 = floor(g.ch) - 1f
        val y1 = g.suelo + g.w * 0.03f
        // La roca de detrás de cada poste, en todo su costado (el poste no lo llena): de píxel entero a píxel entero, para que no
        // asome el fondo de la pantalla a los lados.
        val muro = List(5) { i -> g.luz(g.w * 0.5f, y0 + (y1 - y0) * i / 4f).c(mezcla(P.CALIZA_HONDA, P.HOLLIN, 0.3f)) }
        val xi = kotlin.math.ceil(g.x0) + 1f; val xd = floor(g.x1) - 1f
        drawRect(Brush.verticalGradient(muro, y0, y1), Offset(0f, y0), Size(xi, y1 - y0))
        drawRect(Brush.verticalGradient(muro, y0, y1), Offset(xd, y0), Size(g.w - xd, y1 - y0))
        conManchaCueva(g, DELANTE, Rect(0f, y0, g.w, y1)) { s ->
            // La pasada de la mancha, solo en el tramo que ella alcanza (el poste es largo y pintarlo entero dos veces cuesta).
            val b = if (s != null && s > 0f) g.mancha(DELANTE)?.getBounds() else null
            val ya = if (b != null) maxOf(y0, floor(b.top) - 1f) else y0
            val yb = if (b != null) minOf(y1, kotlin.math.ceil(b.bottom) + 1f) else y1
            poste(g, g.posteI, ya, yb, 0, s)
            poste(g, g.posteD, ya, yb, 1, s)
        }
    }

    /**
     * Un tramo de poste, de [y0] a [y1]: el tronco redondo de borde ondulado (siempre el mismo a la misma altura, para que el tramo
     * guardado y el de cada dibujo empalmen), su luz y su sombra en franjas que cambian a lo alto con la luz que le llega, la veta,
     * los parches de corteza, los nudos y algún muñón de rama.
     */
    private fun DrawScope.poste(g: MarcoCueva, cx: Float, y0: Float, y1: Float, lado: Int, s: Float?) {
        if (y1 <= y0 + 1f) return
        val w = g.w; val r = g.radioPoste; val u = density
        val hacia = haciaPoste(g, lado)
        fun borde(y: Float, dir: Float) = cx + dir * r * (1f + 0.05f * sin(y / w * 23f + lado * 2f + dir) + 0.03f * sin(y / w * 57f + dir * 3f))
        val paso = maxOf(4f, w * 0.02f)
        val forma = Path().apply {
            var y = y0; moveTo(borde(y, -1f), y)
            while (y < y1) { y = minOf(y1, y + paso); lineTo(borde(y, -1f), y) }
            while (y > y0) { lineTo(borde(y, 1f), y); y = maxOf(y0, y - paso) }
            lineTo(borde(y0, 1f), y0); close()
        }
        // La luz a lo alto: el color en cinco alturas, en un degradado.
        val ys = List(6) { i -> y0 + (y1 - y0) * i / 5f }
        val luces = ys.map { y -> g.luz(cx, y, DELANTE, s) }
        // La sombra que el poste echa en la pared (a la derecha de día; de noche, lejos del fuego), fuera de él.
        drawRect(Brush.verticalGradient(luces.map { it.s(0.3f) }, y0, y1), Offset(cx - hacia * r * 0.2f, y0), Size(r * 1.5f, y1 - y0))
        run {
            drawPath(forma, Brush.verticalGradient(luces.map { it.c(P.PINO) }, y0, y1))
            // El plano de luz y el de sombra, redondos: franjas del lado de la luz y del contrario, dentro del canto (que nunca baja
            // de 0.92 del radio), así no hace falta recortar.
            val xl = if (hacia < 0f) cx - r * 0.86f else cx + r * 0.3f
            val xs = if (hacia < 0f) cx + r * 0.28f else cx - r * 0.88f
            drawRect(Brush.verticalGradient(luces.map { it.b(0.36f) }, y0, y1), Offset(xl, y0), Size(r * 0.56f, y1 - y0))
            drawRect(Brush.verticalGradient(luces.map { it.s(0.4f) }, y0, y1), Offset(xs, y0), Size(r * 0.6f, y1 - y0))
            // La veta: líneas onduladas a lo largo.
            // La veta: rayas finas a tramos, a lo alto.
            val veta = luces[2].c(P.PINO_OSC).copy(alpha = 0.32f)
            for (k in 0 until 5) {
                val x = cx + r * (-0.7f + 0.35f * k + 0.08f * azar(5000 + lado, k))
                // Tramos de largo fijo contados desde arriba del lienzo: el tramo guardado y el de cada dibujo empalman.
                val largo = w * 0.06f
                var j = floor(y0 / largo).toInt()
                while (j * largo < y1) {
                    val ya = maxOf(j * largo, y0); val yb = minOf((j + 1) * largo, y1)
                    if (azar(5010 + lado * 7 + k, j) > 0.25f && yb > ya) drawRect(veta, Offset(x + r * 0.06f * sin(ya / w * (31f + k * 7f) + k), ya), Size(0.7f * u, yb - ya))
                    j++
                }
            }
            // Los parches de corteza que quedaron, los nudos y las grietas de secado: a alturas fijas del poste (en anchos).
            for (i in 0 until 40) {
                val yc = w * (0.08f + 0.19f * i + 0.07f * azar(5100 + lado, i))
                if (yc < y0 - w * 0.06f) continue
                if (yc > y1 + w * 0.06f) break
                val l = g.luz(cx, yc, DELANTE, s)
                when (Math.floorMod(i + lado, 3)) {
                    0 -> {
                        val xc = cx + r * (if (azar(5101, i) < 0.5f) -0.42f else 0.4f)
                        // Un jirón de corteza que quedó: alargado a lo alto, con sus grietas, su canto levantado y su sombra.
                        val rx = r * (0.26f + 0.12f * azar(5102, i)); val ryy = w * (0.045f + 0.05f * azar(5103, i))
                        val f = piedraCueva(Offset(xc, yc), rx, ryy, 5104 + i, 9, 0.3f)
                        drawPath(corridoCueva(f, -hacia * r * 0.1f, w * 0.004f), l.s(0.35f))
                        drawPath(f, l.c(P.CORTEZA))
                        val grietas = Path()
                        for (j in 0 until 3) {
                            val gx = xc + rx * (-0.5f + 0.5f * j)
                            grietas.moveTo(gx, yc - ryy * 0.7f); grietas.lineTo(gx + rx * 0.1f, yc); grietas.lineTo(gx - rx * 0.05f, yc + ryy * 0.65f)
                        }
                        drawPath(grietas, l.c(P.CORTEZA_OSC), style = Stroke(1f * u, cap = StrokeCap.Round))
                        drawPath(f, l.c(P.CORTEZA_OSC).copy(alpha = 0.7f), style = Stroke(1f * u))
                    }
                    1 -> {
                        val q = Offset(cx + r * (azar(5105, i) - 0.5f) * 0.8f, yc)
                        drawOval(l.c(P.PINO_OSC), Offset(q.x - r * 0.2f, q.y - r * 0.28f), Size(r * 0.4f, r * 0.56f))
                        drawOval(l.c(P.CORTEZA_OSC).copy(alpha = 0.8f), Offset(q.x - r * 0.09f, q.y - r * 0.13f), Size(r * 0.18f, r * 0.26f))
                        drawOval(l.c(P.PINO_OSC).copy(alpha = 0.4f), Offset(q.x - r * 0.34f, q.y - r * 0.5f), Size(r * 0.68f, r * 1f), style = Stroke(0.7f * u))
                    }
                    else -> {
                        val x = cx + r * (azar(5106, i) - 0.5f)
                        val gr = Path().apply { moveTo(x, yc - w * 0.04f); lineTo(x + r * 0.06f, yc); lineTo(x - r * 0.03f, yc + w * 0.045f) }
                        drawPath(gr, l.s(0.5f), style = Stroke(1.1f * u, cap = StrokeCap.Round))
                        drawPath(corridoCueva(gr, -hacia * 0.9f * u, 0f), l.b(0.4f), style = Stroke(0.6f * u))
                    }
                }
            }
        }
        // El filo del fuego en el canto que lo mira.
        if (g.a.lampara > 0f || g.a.tea > 0f || g.a.hogar > 0f) {
            val filo = Path().apply {
                var y = y0; moveTo(borde(y, -hacia) + hacia * 0.6f * u, y)
                while (y < y1) { y = minOf(y1, y + paso); lineTo(borde(y, -hacia) + hacia * 0.6f * u, y) }
            }
            drawPath(filo, Brush.verticalGradient(luces.map { it.filo(0.8f) }, y0, y1), style = Stroke(1.6f * u))
        }
    }

    // ---------------------------------------------------------------- las repisas

    /** La cara de cada zarzo, vista apenas desde arriba, al pie de cada hueco (detrás de las entradas). */
    fun DrawScope.zarzos(g: MarcoCueva, d: DisposicionEstante) {
        val w = g.w
        d.repisas.forEach { r -> caraZarzo(g, Rect(r.left, r.bottom - w * 0.014f, r.right, r.bottom), null) }
        // De noche, el charco de la tea y el del hogar en la roca de los huecos (debajo de las entradas y del andamio).
        if (g.a.tea > 0f) {
            val t = g.tea
            mancha(t, w * 0.27f, g.luz(t.x - w * 0.05f, t.y).c(P.CALIZA_HONDA).copy(alpha = 0.65f * g.a.tea))
            mancha(t, w * 0.13f, P.LLAMA.copy(alpha = 0.32f * g.a.tea))
        }
        if (g.a.hogar > 0f) {
            val ho = g.hogar
            mancha(Offset(ho.x, g.fondo), w * 0.34f, mezcla(P.FUEGO, P.BRASA, 0.3f).copy(alpha = 0.34f * g.a.hogar))
        }
    }

    /** La cara de arriba de un zarzo: las varas que se van hacia la pared, cada una con su luz y su junta. */
    private fun DrawScope.caraZarzo(g: MarcoCueva, r: Rect, s: Float?) {
        val w = g.w; val u = density
        val xs = listOf(r.left, r.center.x, r.right)
        val cols = xs.map { x -> g.luz(x, r.center.y, 0.05f, s).c(mezcla(P.PINO, P.PINO_LUZ, 0.25f)) }
        drawRect(Brush.horizontalGradient(cols, r.left, r.right), r.topLeft, r.size)
        val l = g.luz(r.center.x, r.center.y, 0.05f, s)
        drawRect(l.s(0.4f), r.topLeft, Size(r.width, r.height * 0.3f))
        // Las juntas entre vara y vara: rayas finas sueltas (más baratas que un camino largo).
        val junta = l.s(0.34f)
        val paso = maxOf(3f, w * 0.0155f)
        var x = r.left + paso * 0.5f
        while (x < r.right) { drawRect(junta, Offset(x, r.top), Size(0.8f * u, r.height)); x += paso }
    }

    /**
     * El canto de una repisa: la fila de cabos de las varas, cortados a hacha, cada uno con su anillo de corteza, su médula y su
     * sombra en la franja honda de detrás.
     */
    private fun DrawScope.cabos(g: MarcoCueva, r: Rect, semilla: Int, s: Float?) {
        val w = g.w; val u = density
        val rad = r.height * 0.5f
        val paso = rad * 2.08f
        // Cada cabo con primitivas sueltas (un círculo es mucho más barato de pintar que un camino con muchos óvalos): la cara de
        // la madera con la luz de su sitio, el anillo de corteza y la médula; debajo, la franja de la sombra.
        val l = g.luz(r.center.x, r.center.y, DELANTE, s)
        // Detrás de los cabos, lo hondo entre vara y vara: una franja llena, de píxel entero arriba (así, entre los cabos, no asoma
        // el fondo de la pantalla en la junta con el hueco de arriba), y la sombra que echan abajo.
        val y0 = kotlin.math.floor(r.top) - 1f
        drawRect(l.c(mezcla(P.PINO_OSC, P.HOLLIN, 0.55f)), Offset(r.left, y0), Size(r.width, r.bottom - y0))
        drawRect(l.s(0.45f), Offset(r.left, r.center.y + rad * 0.2f), Size(r.width, rad * 1.1f))
        val madera = mezcla(P.PINO_LUZ, P.PINO, 0.35f)
        val anillo = Stroke(rad * 0.22f)
        var x = r.left + rad; var i = 0
        while (x < r.right + rad) {
            val rr = rad * (0.86f + 0.16f * azar(semilla, i))
            val cy = r.center.y + rad * 0.1f * (azar(semilla, i + 300) - 0.5f)
            val lx = if (i % 3 == 0 || s != null) g.luz(x, cy, DELANTE, s) else l
            val c = Offset(x, cy)
            drawCircle(lx.c(madera), rr, c)
            drawCircle(lx.c(P.CORTEZA), rr * 0.9f, c, style = anillo)
            drawCircle(lx.c(P.CORTEZA_OSC).copy(alpha = 0.7f), rr * 0.17f, c)
            x += paso * (0.94f + 0.12f * azar(semilla, i + 600)); i++
        }
    }

    /**
     * Un tronco tendido de [xa] a [xb] entre [ya] y [yb]: el cuerpo redondo (luz arriba, sombra abajo) con la luz que le llega a lo
     * largo, la veta, los parches de corteza y algún nudo.
     */
    private fun DrawScope.tronco(g: MarcoCueva, xa: Float, xb: Float, ya: Float, yb: Float, semilla: Int, s: Float?) {
        val w = g.w; val u = density
        val alto = yb - ya
        val paso = maxOf(4f, w * 0.025f)
        val forma = Path().apply {
            var x = xa; moveTo(x, ya + alto * 0.05f * sin(x / w * 19f + semilla))
            while (x < xb) { x = minOf(xb, x + paso); lineTo(x, ya + alto * 0.05f * sin(x / w * 19f + semilla)) }
            while (x > xa) { lineTo(x, yb + alto * 0.05f * sin(x / w * 23f + semilla)); x = maxOf(xa, x - paso) }
            lineTo(xa, yb + alto * 0.05f * sin(xa / w * 23f + semilla)); close()
        }
        val xs = List(5) { i -> xa + (xb - xa) * i / 4f }
        val yc = (ya + yb) / 2
        val luces = xs.map { g.luz(it, yc, DELANTE, s) }
        drawRect(Brush.horizontalGradient(luces.map { it.s(0.38f) }, xa, xb), Offset(xa, yb - alto * 0.1f), Size(xb - xa, alto * 0.45f))
        run {
            drawPath(forma, Brush.horizontalGradient(luces.map { it.c(P.PINO) }, xa, xb))
            // La luz arriba y la sombra abajo, dentro del canto ondulado (que no se aparta más de 0.05 del alto).
            drawRect(Brush.horizontalGradient(luces.map { it.b(0.5f) }, xa, xb), Offset(xa, ya + alto * 0.07f), Size(xb - xa, alto * 0.28f))
            drawRect(Brush.horizontalGradient(luces.map { it.s(0.4f) }, xa, xb), Offset(xa, ya + alto * 0.62f), Size(xb - xa, alto * 0.31f))
            // La veta: rayas finas a tramos, a lo largo (primitivas sueltas: un camino largo cuesta mucho más de pintar).
            val veta = luces[2].c(P.PINO_OSC).copy(alpha = 0.3f)
            for (k in 0 until 3) {
                var x = xa; var j = 0
                val y = ya + alto * (0.3f + 0.2f * k)
                while (x < xb) {
                    val largo = w * (0.04f + 0.08f * azar(semilla + k, j))
                    if (j % 4 != 3) drawRect(veta, Offset(x, y + alto * 0.05f * sin(x / w * (27f + 9f * k) + k)), Size(largo, 0.7f * u))
                    x += largo; j++
                }
            }
            // Los parches de corteza, cada uno con el color de su sitio.
            var x = xa + (xb - xa) * 0.04f; var i = 0
            while (x < xb) {
                val largo = w * (0.03f + 0.05f * azar(semilla, i))
                val arriba = azar(semilla, i + 40) < 0.5f
                val c = Offset(x + largo / 2, if (arriba) ya + alto * 0.32f else yb - alto * 0.3f)
                val ll = g.luz(c, DELANTE, s)
                drawOval(ll.c(P.CORTEZA), Offset(c.x - largo / 2, c.y - alto * 0.18f), Size(largo, alto * 0.36f))
                drawOval(ll.c(P.CORTEZA_OSC).copy(alpha = 0.6f), Offset(c.x - largo / 2, c.y - alto * 0.18f), Size(largo, alto * 0.36f), style = Stroke(0.8f * u))
                x += largo + w * (0.08f + 0.16f * azar(semilla, i + 80)); i++
            }
        }
        if (g.a.lampara > 0f || g.a.tea > 0f || g.a.hogar > 0f) {
            val filo = Path().apply {
                var x = xa; moveTo(x, ya + alto * 0.05f * sin(x / w * 19f + semilla) + 0.8f * u)
                while (x < xb) { x = minOf(xb, x + paso); lineTo(x, ya + alto * 0.05f * sin(x / w * 19f + semilla) + 0.8f * u) }
            }
            drawPath(filo, Brush.horizontalGradient(luces.map { it.filo(0.75f) }, xa, xb), style = Stroke(1.4f * u))
        }
    }

    /**
     * El canto de una repisa (en cada dibujo): la fila de cabos de las varas y, debajo, el travesaño redondo, atado a cada poste
     * con su atadura de cuero crudo.
     */
    fun DrawScope.travesano(k: Int, r: Rect, g: MarcoCueva) {
        val w = g.w
        val th = r.height
        conManchaCueva(g, DELANTE, Rect(0f, r.top - th * 0.5f, w, r.bottom + th * 0.5f)) { s ->
            cabos(g, Rect(g.x0 - w * 0.012f, r.top - th * 0.02f, g.x1 + w * 0.012f, r.top + th * 0.44f), 100 + k, s)
            tronco(g, w * 0.002f, w * 0.998f, r.top + th * 0.36f, r.bottom + th * 0.1f, 120 + k, s)
            atadura(g, Offset(g.posteI, r.top + th * 0.55f), w * 0.05f, th * 1.15f, 140 + k, s)
            atadura(g, Offset(g.posteD, r.top + th * 0.55f), w * 0.05f, th * 1.15f, 160 + k, s)
        }
    }

    /**
     * Una atadura de cuero crudo sobre el cruce de un poste y un tronco ([c], de [ancho] por [alto]): cuatro vueltas en diagonal y
     * dos de amarre alrededor, cada una con su sombra y su filo de luz; a veces un nudo con dos cabos que cuelgan.
     */
    private fun DrawScope.atadura(g: MarcoCueva, c: Offset, ancho: Float, alto: Float, semilla: Int, s: Float?) {
        val u = density
        val l = g.luz(c, DELANTE + 0.01f, s)
        val a = ancho * 0.5f; val b = alto * 0.5f
        val grosor = maxOf(1.6f * u, alto * 0.13f)
        val aspa = Path(); val vueltas = Path()
        // Las vueltas en diagonal, todas en el mismo sentido (en aspa, de lejos, se leían como una cruz), y dos de amarre que
        // rodean el poste por encima y por debajo del tronco.
        for (i in 0 until 4) {
            val d = (i - 1.5f) * grosor * 1.15f
            aspa.moveTo(c.x - a * 0.7f + d, c.y - b); aspa.lineTo(c.x + a * 0.7f + d, c.y + b)
        }
        for (i in 0 until 2) {
            val y = c.y + (if (i == 0) -0.84f else 0.84f) * b
            vueltas.moveTo(c.x - a * 0.95f, y); vueltas.quadraticTo(c.x, y + grosor * 0.5f, c.x + a * 0.95f, y)
        }
        aspa.addPath(vueltas)
        drawPath(corridoCueva(aspa, grosor * 0.35f, grosor * 0.45f), l.s(0.45f), style = Stroke(grosor, cap = StrokeCap.Round))
        drawPath(aspa, l.c(P.CUERO), style = Stroke(grosor, cap = StrokeCap.Round))
        drawPath(corridoCueva(aspa, -grosor * 0.15f, -grosor * 0.25f), l.b(0.55f), style = Stroke(grosor * 0.3f, cap = StrokeCap.Round))
        if (azar(semilla, 1) < 0.55f) {
            val nudo = Offset(c.x + a * (if (azar(semilla, 2) < 0.5f) 0.7f else -0.7f), c.y + b * 0.75f)
            val cabo = Path().apply {
                moveTo(nudo.x, nudo.y); quadraticTo(nudo.x + grosor * 1.2f, nudo.y + alto * 0.5f, nudo.x + grosor * 0.4f, nudo.y + alto * (0.8f + 0.3f * azar(semilla, 3)))
                moveTo(nudo.x, nudo.y); quadraticTo(nudo.x - grosor * 0.6f, nudo.y + alto * 0.4f, nudo.x - grosor * 0.9f, nudo.y + alto * (0.55f + 0.2f * azar(semilla, 4)))
            }
            drawPath(corridoCueva(cabo, grosor * 0.4f, grosor * 0.3f), l.s(0.4f), style = Stroke(grosor * 0.7f, cap = StrokeCap.Round))
            drawPath(cabo, l.c(P.CUERO), style = Stroke(grosor * 0.7f, cap = StrokeCap.Round))
            drawCircle(l.s(0.45f), grosor * 0.95f, nudo + Offset(grosor * 0.3f, grosor * 0.3f))
            drawCircle(l.c(mezcla(P.CUERO, P.CUERO_OSC, 0.3f)), grosor * 0.9f, nudo)
            drawCircle(l.b(0.6f), grosor * 0.35f, nudo - Offset(grosor * 0.3f, grosor * 0.3f))
        }
    }

    /** El faldón de la plataforma de arriba: una tira de piel con su hilera de puntos de ocre y sus flecos, de [ya] a [yb]. */
    private fun DrawScope.faldon(g: MarcoCueva, ya: Float, yb: Float, s: Float?) {
        val w = g.w; val u = density
        val alto = yb - ya
        val fleco = alto * 0.38f
        val cuerpo = Path().apply {
            moveTo(g.x0 - w * 0.01f, ya)
            lineTo(g.x1 + w * 0.01f, ya)
            var x = g.x1 + w * 0.01f; var i = 0
            while (x > g.x0 - w * 0.01f) {
                lineTo(x, yb - fleco + alto * 0.06f * sin(i * 1.7f)); x -= w * 0.04f; i++
            }
            lineTo(g.x0 - w * 0.01f, yb - fleco); close()
        }
        val xs = List(5) { i -> g.x0 + (g.x1 - g.x0) * i / 4f }
        val luces = xs.map { g.luz(it, (ya + yb) / 2, DELANTE, s) }
        drawPath(corridoCueva(cuerpo, 0f, alto * 0.15f), luces[2].s(0.3f))
        drawPath(cuerpo, Brush.horizontalGradient(luces.map { it.c(P.PIEL) }, g.x0, g.x1))
        drawRect(Brush.horizontalGradient(luces.map { it.b(0.35f) }, g.x0, g.x1), Offset(g.x0, ya), Size(g.x1 - g.x0, alto * 0.12f))
        drawRect(Brush.horizontalGradient(luces.map { it.s(0.28f) }, g.x0, g.x1), Offset(g.x0, yb - fleco - alto * 0.14f), Size(g.x1 - g.x0, alto * 0.14f))
        // La hilera de puntos de ocre rojo, a pares.
        val puntos = Path()
        var x = g.x0 + w * 0.02f; var i = 0
        while (x < g.x1 - w * 0.01f) {
            val r = alto * 0.09f
            puntos.addOval(Rect(x - r, ya + alto * 0.32f - r, x + r, ya + alto * 0.32f + r))
            x += if (i % 2 == 0) r * 3f else w * 0.045f; i++
        }
        drawPath(puntos, Brush.horizontalGradient(luces.map { it.c(P.OCRE_ROJO).copy(alpha = 0.85f) }, g.x0, g.x1))
        // Los flecos: tiras finas que cuelgan, cada una con su largo.
        val tiras = Path()
        var xf = g.x0 - w * 0.005f; var j = 0
        while (xf < g.x1 + w * 0.01f) {
            tiras.moveTo(xf, yb - fleco - alto * 0.04f)
            tiras.lineTo(xf + w * 0.002f * sin(j * 2.1f), yb - fleco * (0.08f * azar(5300, j)))
            xf += maxOf(2.5f, w * 0.0075f); j++
        }
        drawPath(corridoCueva(tiras, 1f * u, 1f * u), luces[2].s(0.35f), style = Stroke(1.3f * u, cap = StrokeCap.Round))
        drawPath(tiras, Brush.horizontalGradient(luces.map { it.c(mezcla(P.PIEL, P.PIEL_OSC, 0.25f)) }, g.x0, g.x1), style = Stroke(1.3f * u, cap = StrokeCap.Round))
    }

    // ---------------------------------------------------------------- el rótulo del mes y la tea

    /** El rótulo del mes: una tira de cuero crudo atada al travesaño con dos tientos, con el nombre pintado con ocre hondo. */
    fun DrawScope.placa(p: PlacaEstante, medidor: TextMeasurer, g: MarcoCueva) {
        val w = g.w
        val mitad = maxOf(p.r.width * 0.5f, (g.x1 - g.x0) * 0.31f)
        val r = Rect(p.r.center.x - mitad, p.r.top - p.r.height * 0.05f, p.r.center.x + mitad, p.r.bottom + p.r.height * 0.12f)
        val l = g.luz(r.center, DELANTE + 0.01f)
        val forma = cerradaCueva(listOf(Offset(r.left, r.top + r.height * 0.1f), Offset(r.left + r.width * 0.3f, r.top), Offset(r.left + r.width * 0.7f, r.top + r.height * 0.04f),
            Offset(r.right, r.top + r.height * 0.06f), Offset(r.right - r.width * 0.008f, r.bottom - r.height * 0.1f), Offset(r.left + r.width * 0.6f, r.bottom),
            Offset(r.left + r.width * 0.25f, r.bottom - r.height * 0.04f), Offset(r.left + r.width * 0.006f, r.bottom - r.height * 0.12f)))
        drawPath(corridoCueva(forma, w * 0.004f, w * 0.006f), l.s(0.42f))
        bultoRapidoCueva(forma, l.etiqueta(), l, -1f, 0.7f, 0.3f)
        // Los dos tientos que la atan al travesaño.
        listOf(r.left + r.width * 0.035f, r.right - r.width * 0.035f).forEach { x ->
            drawLine(l.s(0.4f), Offset(x + 1.2f * density, r.top - r.height * 0.1f), Offset(x + 1.2f * density, r.bottom + r.height * 0.12f), r.height * 0.14f)
            drawLine(l.c(P.CUERO_OSC), Offset(x, r.top - r.height * 0.12f), Offset(x, r.bottom + r.height * 0.1f), r.height * 0.13f, StrokeCap.Round)
        }
        val tinta = if (g.a.oscuro) mezcla(Color(0xFF2A0D08), l.tinte, 0.1f * l.k) else Color(0xFF5A1A0E)
        textoEn(medidor, p.texto, Rect(r.left + r.width * 0.06f, r.top, r.right - r.width * 0.06f, r.bottom), MuebleEstanteCueva.LETRA, tinta, 12f,
            peso = FontWeight.Bold, minimo = 6f)
    }

    /**
     * La tea de pino resinoso atada al poste derecho, a media altura: el palo, dos vueltas de tiento y la cabeza de astillas
     * resinosas. De noche arde (la llama en capas, su charco en el poste y alguna chispa); de día, la cabeza apagada y negra.
     */
    fun DrawScope.tea(g: MarcoCueva) {
        val w = g.w; val u = density
        val t = g.tea
        val pie = t.y + w * 0.16f
        val l = g.luz(t.x, t.y + w * 0.06f, DELANTE + 0.01f)
        val palo = Path().apply {
            moveTo(t.x - w * 0.006f, t.y + w * 0.02f); lineTo(t.x + w * 0.007f, t.y + w * 0.02f)
            lineTo(t.x + w * 0.005f, pie); lineTo(t.x - w * 0.005f, pie); close()
        }
        drawPath(corridoCueva(palo, w * 0.006f, w * 0.004f), l.s(0.4f))
        bultoRapidoCueva(palo, l.c(mezcla(P.CORTEZA, P.PINO_OSC, 0.5f)), l, -1f, 1f, 0.3f)
        listOf(0.55f, 0.85f).forEach { f ->
            val y = t.y + (pie - t.y) * f
            drawLine(l.s(0.4f), Offset(t.x - w * 0.016f, y + 1.5f * u), Offset(t.x + w * 0.018f, y + 2.5f * u), 2.4f * u, StrokeCap.Round)
            drawLine(l.c(P.CUERO), Offset(t.x - w * 0.017f, y), Offset(t.x + w * 0.017f, y + 1f * u), 2.2f * u, StrokeCap.Round)
        }
        // La cabeza: astillas resinosas atadas, ennegrecidas.
        val cabeza = cerradaCueva(listOf(Offset(t.x - w * 0.012f, t.y + w * 0.03f), Offset(t.x - w * 0.014f, t.y), Offset(t.x - w * 0.006f, t.y - w * 0.012f),
            Offset(t.x + w * 0.008f, t.y - w * 0.01f), Offset(t.x + w * 0.014f, t.y + w * 0.004f), Offset(t.x + w * 0.011f, t.y + w * 0.03f)))
        bultoRapidoCueva(cabeza, l.c(Color(0xFF2E2019)), l, -1f, 1f, 0.2f)
        for (k in 0 until 4) drawLine(l.c(P.CARBON).copy(alpha = 0.7f), Offset(t.x - w * 0.01f + k * w * 0.006f, t.y - w * 0.006f), Offset(t.x - w * 0.009f + k * w * 0.006f, t.y + w * 0.028f), 0.8f * u)
        if (g.a.tea <= 0f) return
        // La llama: alta, inquieta, en capas opacas; el charco en el poste y unas chispas.
        mancha(t, w * 0.1f, P.LLAMA.copy(alpha = 0.45f))
        llamaCueva(Offset(t.x, t.y + w * 0.004f), w * 0.085f, w * 0.022f, 7)
        drawCircle(P.LLAMA.copy(alpha = 0.85f), 1.1f * u, Offset(t.x + w * 0.012f, t.y - w * 0.1f))
        drawCircle(P.FUEGO.copy(alpha = 0.7f), 0.9f * u, Offset(t.x - w * 0.01f, t.y - w * 0.125f))
    }
}

/**
 * Una llama de fuego en capas opacas, de base [base], [alto] y medio ancho [ancho]: el rojo de fuera, el naranja, el amarillo y
 * el centro casi blanco, con la punta que se tuerce según [semilla].
 */
internal fun DrawScope.llamaCueva(base: Offset, alto: Float, ancho: Float, semilla: Int) {
    val tuerce = (azar(semilla, 1) - 0.5f) * ancho * 0.9f
    listOf(Triple(1f, 1f, P.BRASA), Triple(0.78f, 0.82f, P.FUEGO), Triple(0.55f, 0.62f, P.LLAMA), Triple(0.3f, 0.36f, Color(0xFFFFF2CC))).forEach { (fx, fy, c) ->
        val h = alto * fy; val an = ancho * fx
        val p = Path().apply {
            moveTo(base.x - an, base.y)
            cubicTo(base.x - an * 1.15f, base.y - h * 0.42f, base.x - an * 0.3f + tuerce * 0.3f, base.y - h * 0.66f, base.x + tuerce * fx, base.y - h)
            cubicTo(base.x + an * 0.3f + tuerce * 0.3f, base.y - h * 0.62f, base.x + an * 1.15f, base.y - h * 0.36f, base.x + an, base.y)
            cubicTo(base.x + an * 0.5f, base.y + an * 0.35f, base.x - an * 0.5f, base.y + an * 0.35f, base.x - an, base.y)
            close()
        }
        drawPath(p, c)
    }
}
